
require "TimedActions/ISBaseTimedAction"

LSCleanObject = ISBaseTimedAction:derive("LSCleanObject");

local function getDirtSprites(spriteName, dirtSprites)

	if not dirtSprites then print("LSCleanObject: ERROR - dirtSprites not found"); return; end

	local dirtSprite, dirtSprite2, dirtSprite3, dirtSprite4

	for k, v in pairs(dirtSprites) do
		if v.spriteMain == spriteName then
			dirtSprite = v.sprite1
			dirtSprite2 = v.sprite2
			dirtSprite3 = v.sprite3
			dirtSprite4 = v.sprite4
			break
		end
	end

	return dirtSprite, dirtSprite2, dirtSprite3, dirtSprite4

end

local function getCleanSound(item, spriteName)

	if spriteName and ((spriteName == "fixtures_bathroom_01_25") or (spriteName == "fixtures_bathroom_01_26")) then
		return "Tub"
	end

	if item then
		if item:getType() and (item:getType() == "Sponge") then
			return "Sponge"
		end
	end

	return false

end

local function GetConsumption(character, CleanItem)

	local consumption = 0.004
	local skillLevel = character:getPerkLevel(Perks.Cleaning)
	local skillDiv = 1

	if skillLevel == 10 then skillDiv = 3.5;
	elseif skillLevel >= 8 then skillDiv = 3;
	elseif skillLevel >= 6 then skillDiv = 2.5;
	elseif skillLevel >= 4 then skillDiv = 2;
	elseif skillLevel >= 2 then skillDiv = 1.5;
	end

	if character:HasTrait("Tidy") then
		consumption = consumption*0.5
	elseif character:HasTrait("CleanFreak") or character:HasTrait("Sloppy") then
		consumption = consumption*1.5
	elseif character:HasTrait("AllThumbs") then
		consumption = consumption*1.1
	elseif character:HasTrait("Dextrous") then
		consumption = consumption*0.9
	end

	consumption = consumption/skillDiv

	return consumption

end

local function AdjustStatsAndAddCleaningXP(character)

	local xpChange = 10
	
	local skillLevel = character:getPerkLevel(Perks.Cleaning)
	if character:HasTrait("Smoker") then
		character:getStats():setStressFromCigarettes(0)
	end
	local currentBoredom = character:getBodyDamage():getBoredomLevel()
	local currentStress = character:getStats():getStress();

	if skillLevel == 0 then skillLevel = 1; end

	if character:HasTrait("Sloppy") then
		xpChange = xpChange*0.5
		character:getStats():setStress(currentStress + 0.01)
		HaloTextHelper.addTextWithArrow(character, getText("IGUI_HaloNote_Stress"), true, 255, 180, 180)
	elseif character:HasTrait("CouchPotato") then
		character:getBodyDamage():setBoredomLevel(currentBoredom + 1)
		HaloTextHelper.addTextWithArrow(character, getText("IGUI_HaloNote_Boredom"), true, 255, 180, 180)
	elseif character:HasTrait("Tidy") then
		character:getBodyDamage():setBoredomLevel(currentBoredom - 1)
		character:getStats():setStress(currentStress - 0.005)
		HaloTextHelper.addTextWithArrow(character, getText("IGUI_HaloNote_Stress"), false, 170, 255, 150)
	end

	if (character:getBodyDamage():getBoredomLevel() < 0) then
		character:getBodyDamage():setBoredomLevel(0)
	end
	if (character:getBodyDamage():getBoredomLevel() > 100) then
		character:getBodyDamage():setBoredomLevel(100)
	end
	if (character:getStats():getStress() > 1) then
		character:getStats():setStress(1)
	end
	if (character:getStats():getStress() < 0) then
		character:getStats():setStress(0)
	end

	if skillLevel == 10 then return; end

	xpChange = xpChange*skillLevel

	character:getXp():AddXP(Perks.Cleaning, xpChange)

	HaloTextHelper.addText(character, getText("IGUI_HaloNote_XP"), 200, 255, 200)

end

function LSCleanObject:isValid()
	return true;
end

function LSCleanObject:waitToStart()
	self.action:setUseProgressBar(false)
	if isClient() then
		local cX = self.character:getX()
		local cY = self.character:getY()
		self.character:setLy(cY)
		self.character:setLx(cX)
	end
	self.character:faceThisObject(self.thisObject);
	return self.character:shouldBeTurning();
end

function LSCleanObject:update()

	self.character:faceThisObject(self.thisObject)

	if self.thisObject:getModData().Condition > 0 then

		if self.cleanRateCount >= self.cleanRate then
			self.cleanRateCount = 0
			
			AdjustStatsAndAddCleaningXP(self.character)
			
			self.thisObject:getModData().Condition = self.thisObject:getModData().Condition - 1
			if self.connectedObject then
				self.connectedObject:getModData().Condition = self.thisObject:getModData().Condition
			end
			
			local thisDirtSprite, thisDirtSpriteConnected

			if self.thisObject:getModData().ConditionLevel == 1 and self.thisObject:getModData().Condition < 30 then
				self.thisObject:getModData().ConditionLevel = 0
				thisDirtSprite = self.overlayDirtSprite
				thisDirtSpriteConnected = self.overlayDirtSprite3
			elseif self.thisObject:getModData().ConditionLevel == 2 and self.thisObject:getModData().Condition < 60 then
				self.thisObject:getModData().ConditionLevel = 1
				thisDirtSprite = self.overlayDirtSprite
				thisDirtSpriteConnected = self.overlayDirtSprite3
			elseif self.thisObject:getModData().ConditionLevel == 3 and self.thisObject:getModData().Condition < 90 then
				self.thisObject:getModData().ConditionLevel = 2
				thisDirtSprite = self.overlayDirtSprite2
				thisDirtSpriteConnected = self.overlayDirtSprite4
			end

			if self.connectedObject and (self.connectedObject:getModData().ConditionLevel ~= self.thisObject:getModData().ConditionLevel) then
				self.connectedObject:getModData().ConditionLevel = self.thisObject:getModData().ConditionLevel
			end


			if thisDirtSprite then
				if isClient() and self.thisObject:getModData().ConditionLevel > 0 then
					self.thisObject:setOverlaySprite(thisDirtSprite, true)
					self.thisObject:transmitUpdatedSpriteToServer()
					self.thisObject:transmitModData()
					if self.connectedObject then
						self.connectedObject:setOverlaySprite(thisDirtSpriteConnected, true)
						self.connectedObject:transmitUpdatedSpriteToServer()
						self.connectedObject:transmitModData()
					end
				elseif isClient() then
					self.thisObject:setOverlaySprite(nil, true)
					self.thisObject:transmitUpdatedSpriteToServer()
					self.thisObject:transmitModData()
					if self.connectedObject then
						self.connectedObject:setOverlaySprite(nil, true)
						self.connectedObject:transmitUpdatedSpriteToServer()
						self.connectedObject:transmitModData()
					end
				elseif self.thisObject:getModData().ConditionLevel > 0 then
					self.thisObject:setOverlaySprite(thisDirtSprite, false)
					if self.connectedObject then self.connectedObject:setOverlaySprite(thisDirtSpriteConnected, false); end
				else
					self.thisObject:setOverlaySprite(nil, false)
					if self.connectedObject then self.connectedObject:setOverlaySprite(nil, false); end
				end
			end
			
			if self.itemLHand then
				
				local inventory = self.character:getInventory();
				local it = inventory:getItems();
				local CleanItem

				for j = 0, it:size()-1 do
					local item = it:get(j);
					if item:getType() == self.itemLHand:getType() then
						CleanItem = item
						break
					end
				end
				
				if CleanItem then
					local consumption
					consumption = GetConsumption(self.character, CleanItem)
					CleanItem:setUseDelta(consumption)
					CleanItem:Use()
				else
					self:forceStop()
				end
			end
			
			if self.cleanSound then
				local soundrandomiser = ZombRand(1, 100)
				local actionSound = "Clean_Sponge1"
				if self.cleanSound == "Sponge" then
					if soundrandomiser >=82 then
						actionSound = "Clean_Sponge1"
					elseif soundrandomiser >=68 then
						actionSound = "Clean_Sponge2"
					elseif soundrandomiser >=50 then
						actionSound = "Clean_Sponge3"
					elseif soundrandomiser >=35 then
						actionSound = "Clean_Sponge4"
					elseif soundrandomiser >=18 then
						actionSound = "Clean_Sponge5"
					else
						actionSound = "Clean_Sponge6"
					end
				elseif self.cleanSound == "Tub" then
					if soundrandomiser >=66 then
						actionSound = "Clean_Tub1"
					elseif soundrandomiser >=33 then
						actionSound = "Clean_Tub2"
					else
						actionSound = "Clean_Tub3"
					end
				end
				self.character:getEmitter():playSound(actionSound);
			end
			
		else
			self.cleanRateCount = self.cleanRateCount + (getGameTime():getGameWorldSecondsSinceLastUpdate()*GTLSCheck)
		end

	else
		self.thisObject:getModData().Condition = 0
		if self.connectedObject then
			self.connectedObject:getModData().Condition = 0
		end
		getSoundManager():playUISound("UI_CleanObject_Perform")
		self:forceStop()
	end
	
	self.character:setMetabolicTarget(Metabolics.LightDomestic);
end

function LSCleanObject:start()

	if self.actionAnim then
		self:setActionAnim(self.actionAnim)
	else
		self:setActionAnim("Loot")
		self.character:SetVariable("LootPosition", "Mid")
	end

	if self.itemLHand and self.itemRHand then
		self:setOverrideHandModels(self.itemRHand:getWorldStaticItem(), self.itemLHand:getWorldStaticItem())
	elseif self.itemRHand then
		self:setOverrideHandModels(self.itemRHand:getWorldStaticItem(), nil)
	else
		self:setOverrideHandModels(nil, nil)
	end

	if not self.cleanSound then
		self.cleanSound = getCleanSound(self.itemRHand, self.objectSpriteName)
	end

	if not self.overlayDirtSprite then
		local dirtSprites = require "Properties/DirtSprites"
		self.overlayDirtSprite, self.overlayDirtSprite2, self.overlayDirtSprite3, self.overlayDirtSprite4 = getDirtSprites(self.objectSpriteName, dirtSprites)
	end

	if not self.overlayDirtSprite then
		self:forceStop()
	end

end

function LSCleanObject:stop()

	if isClient() then
		self.thisObject:transmitModData()
	end

    ISBaseTimedAction.stop(self);
		
end

function LSCleanObject:perform()

	getSoundManager():playUISound("UI_CleanObject_Perform")

	if self.itemLHand then
				
		local inventory = self.character:getInventory();
		local it = inventory:getItems();
		local CleanItem

		for j = 0, it:size()-1 do
			local item = it:get(j);
			if item:getType() == self.itemLHand:getType() then
				CleanItem = item
				break
			end
		end
				
		if CleanItem then
			local consumption
			consumption = GetConsumption(self.character, CleanItem)
			CleanItem:setUseDelta(consumption)
			CleanItem:Use()
		end
	end

	self.thisObject:getModData().ConditionLevel = 0
	self.thisObject:getModData().Condition = 0

	if isClient() then
		self.thisObject:setOverlaySprite(nil, true)
		self.thisObject:transmitUpdatedSpriteToServer()
		self.thisObject:transmitModData()
	else
		self.thisObject:setOverlaySprite(nil, false)
	end

	if self.connectedObject then
		self.connectedObject:getModData().ConditionLevel = 0
		self.connectedObject:getModData().Condition = 0

		if isClient() then
			self.connectedObject:setOverlaySprite(nil, true)
			self.connectedObject:transmitUpdatedSpriteToServer()
			self.connectedObject:transmitModData()
		else
			self.connectedObject:setOverlaySprite(nil, false)
		end

	end

	ISBaseTimedAction.perform(self);

end

function LSCleanObject:new(character, Object, ConnectedObject, Duration, Difficulty, Anim, Item1, Item2, SpriteName)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.thisObject = Object
	o.connectedObject = ConnectedObject
	o.cleanRate = Difficulty
	o.cleanRateCount = 0
	o.actionAnim = Anim
	o.itemRHand = Item1
	o.itemLHand = Item2
	o.ignoreHandsWounds = true
    o.stopOnWalk = true
    o.stopOnRun = true
	o.maxTime = Duration
	o.overlayDirtSprite = false
	o.overlayDirtSprite2 = false
	o.overlayDirtSprite3 = false
	o.overlayDirtSprite4 = false
	o.cleanSound = false
	o.objectSpriteName = SpriteName
	return o;
end

return LSCleanObject