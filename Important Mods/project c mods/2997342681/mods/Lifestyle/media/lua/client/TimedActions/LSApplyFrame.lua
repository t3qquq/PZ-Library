--------------------------------------------------------------------------------------------------
--		----	  |			  |			|		 |				|    --    |      ----			--
--		----	  |			  |			|		 |				|    --	   |      ----			--
--		----	  |		-------	   -----|	 ---------		-----          -      ----	   -------
--		----	  |			---			|		 -----		------        --      ----			--
--		----	  |			---			|		 -----		-------	 	 ---      ----			--
--		----	  |		-------	   ----------	 -----		-------		 ---      ----	   -------
--			|	  |		-------			|		 -----		-------		 ---		  |			--
--			|	  |		-------			|	 	 -----		-------		 ---		  |			--
--------------------------------------------------------------------------------------------------

require "TimedActions/ISBaseTimedAction"
require "Helper/TransferHelper"

LSApplyFrame = ISBaseTimedAction:derive("LSApplyFrame");

local function transferItem(character, hammer, cont)
	if cont:isItemAllowed(hammer) then
		if cont:getType() == "floor" then
			TransferHelper.dropItem(hammer, character)
		else
			TransferHelper.onMoveItemsTo(hammer, cont, character, true)
		end
	end
end

local function isBSide(thisObject)
	local bSide
	local properties = thisObject:getSprite():getProperties()
	if properties:Is("Facing") and properties:Is("SpriteGridPos") then
		local facing, gridPos = properties:Val("Facing"), properties:Val("SpriteGridPos")
		if ((facing == "E") and (gridPos == "0,0")) or ((facing == "S") and (gridPos == "1,0")) then
			bSide = true
		end
	end
	return bSide
end

local function checkGround(character, fullName, amount)
	local groundItems = buildUtil.getMaterialOnGround(character:getSquare())
	local items = groundItems[fullName]
	if items then
		local count = math.min(amount, #items)
		for i=1,count do
			local item = items[i]
			local worldObj = item:getWorldItem()
			worldObj:getSquare():transmitRemoveItemFromSquare(worldObj)
		end
		amount = amount - count
		ISInventoryPage.dirtyUI()
	end
end

local function findAndRemove(character, fullName, amount)
    if character and fullName then
        --local items = character:getInventory():getItems();
        --local items_size = items:size();
        --for i = items_size-1, 0, -1 do
		local items = character:getInventory():getSomeTypeEvalRecurse(fullName, buildUtil.predicateMaterial, amount)
		for i=1,items:size() do
			local item = items:get(i-1)
			character:removeFromHands(item)
			if amount < 1 then break; end
            --local item = items:get(i);
            if item and (item:getFullType() == fullName) then
				amount = amount-1
                if item:getContainer() then
                    item:getContainer():Remove(item)
				else
					character:getInventory():Remove(item)
				end
            end
        end
		if amount > 0 then checkGround(character, fullName, amount); end
    end
end

local function removeMaterials(thisPlayer, frame)
	if thisPlayer:isBuildCheat() then return; end
	for n=1, #frame.mats do
		if (type(frame.mats[n]) == "string") and (frame.mats[n+1] > 0) then
			findAndRemove(thisPlayer, frame.mats[n], frame.mats[n+1])
		end
	end
end

local function getMaterials(thisPlayer, frame)
	if thisPlayer:isBuildCheat() then return true; end
	local hasMats = true
	for n=1, #frame.mats do
		if type(frame.mats[n]) == "string" then
			local mat = thisPlayer:getInventory():getItemCount(frame.mats[n], true);
			if (mat < frame.mats[n+1]) and (frame.mats[n+1] > 0) then
				hasMats = false; break
			end
		end
	end
	return hasMats
end

function LSApplyFrame:isValid()
	local hasMaterials = getMaterials(self.character, self.frame)
	return hasMaterials;
end

function LSApplyFrame:waitToStart()
	--self.action:setUseProgressBar(false)
	self.character:faceThisObject(self.artwork);
	return self.character:shouldBeTurning();
end

function LSApplyFrame:update()
	if self.character:isSitOnGround() then self:forceStop(); end

	if self.jobProgress < (self.maxTime*0.75) then
		self.jobProgress = self:getJobDelta()*self.maxTime
	elseif not self.endPhase then
		self.endPhase = true
		self:setOverrideHandModels(nil, nil)
		self.character:SetVariable("LootPosition", "High")
		self:setActionAnim("Loot")
		if self.actionSound and self.actionSound ~= 0 and self.character:getEmitter():isPlaying(self.actionSound) then
			self.character:getEmitter():stopSound(self.actionSound);
		end
		self.actionSound = self.character:getEmitter():playSound("PutItemInBag")
		self.soundDelay = 100
	end

    if self.soundTime + self.soundDelay < getTimestamp() then
        self.soundTime = getTimestamp()
		if (not self.actionSound ~= 0) or ((self.actionSound ~= 0) and (not self.character:getEmitter():isPlaying(self.actionSound))) then self.actionSound = self.character:getEmitter():playSound(self.soundName); end
	end
	
	
    self.character:setMetabolicTarget(Metabolics.HeavyWork)
	
end

function LSApplyFrame:start()
	--self:setOverrideHandModels(nil, nil)
	self:setActionAnim("BuildLow")
	--self.character:SetVariable("LootPosition", "High")
	--self.character:getEmitter():playSound("PutItemInBag")
end

function LSApplyFrame:stop()
	self.character:SetVariable("LootPosition", "Mid")
    if self.actionSound and self.actionSound ~= 0 and self.character:getEmitter():isPlaying(self.actionSound) then
        self.character:getEmitter():stopSound(self.actionSound);
    end
    ISBaseTimedAction.stop(self);		
end

function LSApplyFrame:perform()
	removeMaterials(self.character, self.frame)
	self.artwork:getModData().movableData['artFrame'] = self.frame
	if self.conObj then
		self.conObj:getModData().movableData['artFrame'] = self.frame
		local bSide = isBSide(self.artwork)
		if bSide then self.artwork:setOverlaySprite(self.conSpriteName, isClient()); self.conObj:setOverlaySprite(self.spriteName, isClient());
		else self.artwork:setOverlaySprite(self.spriteName, isClient()); self.conObj:setOverlaySprite(self.conSpriteName, isClient()); end
		if isClient() then self.conObj:transmitUpdatedSpriteToServer(); self.conObj:transmitModData(); end
	else
		self.artwork:setOverlaySprite(self.spriteName, isClient())
	end
	if isClient() then self.artwork:transmitUpdatedSpriteToServer(); self.artwork:transmitModData(); end
	self.character:SetVariable("LootPosition", "Mid")
    if self.actionSound and self.actionSound ~= 0 and self.character:getEmitter():isPlaying(self.actionSound) then
        self.character:getEmitter():stopSound(self.actionSound);
    end
	
	if self.item and self.itemCont then transferItem(self.character, self.item, self.itemCont); end
	
	ISBaseTimedAction.perform(self);
end

function LSApplyFrame:new(Player, Art, SpriteName, AttachedSpriteName, Frame, AttachedObj, Item, Container)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = Player
	o.artwork = Art
	o.spriteName = SpriteName
	o.conSpriteName = AttachedSpriteName
	o.frame = Frame
	o.conObj = AttachedObj
	o.item = Item
	o.itemCont = Container
	o.ignoreHandsWounds = true;
    o.stopOnWalk        = true;
    o.stopOnRun         = true;
	o.maxTime = 600
	o.soundDelay = 3
	o.soundName = "Hammering"
	o.soundTime = 0
	o.actionSound = 0
	o.jobProgress = 0
	o.endPhase = false
	if o.character:isTimedActionInstant() then o.maxTime = 1; end
	return o;
end
