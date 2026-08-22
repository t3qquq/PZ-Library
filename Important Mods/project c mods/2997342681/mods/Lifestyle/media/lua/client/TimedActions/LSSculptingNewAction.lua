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

LSSculptingNewAction = ISBaseTimedAction:derive("LSSculptingNewAction");

function LSSculptingNewAction:isValid()
	if self.res and self.res[1] then
		local invItem = self.character:getInventory():getItemCount(self.res[1], true)
		if invItem < self.res[2] then return false; end
	end
	return true
end

function LSSculptingNewAction:waitToStart()
	self.action:setUseProgressBar(false)
	self.character:faceThisObject(self.station);
	return self.character:shouldBeTurning();
end

function LSSculptingNewAction:update()
	if self.character:isSitOnGround() then self:forceStop(); end

end

function LSSculptingNewAction:start()
	self:setOverrideHandModels(nil, nil)
	self:setActionAnim("Loot")
	self.character:SetVariable("LootPosition", "High")
	self.character:getEmitter():playSound("PutItemInBag")
end

function LSSculptingNewAction:stop()
	self.character:SetVariable("LootPosition", "Mid")
    ISBaseTimedAction.stop(self);		
end

local function isIceSculpture(station)
	return station:getModData().style and (station:getModData().style == "Ice")
end

local function consumeItems(thisPlayer, material, itemList)
	local consumed = 0
	for x=0,itemList:size() - 1 do
		local item = itemList:get(x)
		local itemCont = item:getContainer()
		if not item:IsClothing() or not thisPlayer:isEquippedClothing(item) then
			itemCont:DoRemoveItem(item)
			itemCont:setDrawDirty(true)
			consumed = consumed+1
			if consumed >= material[2] then break; end
		end
	end
	if material[3] then
		for n=1, material[2] do
			local item = InventoryItemFactory.CreateItem(material[3])
			thisPlayer:getInventory():AddItem(item)
			thisPlayer:getInventory():setDrawDirty(true)
		end
	end
end

function LSSculptingNewAction:perform()
	if self.saveSculpture then
		self.newItem = self.character:getInventory():AddItem('Moveables.Moveable')
		self.newItem:ReadFromWorldSprite(self.station:getModData().sculpture["result"])
		self.newItem:getModData().movableData = self.newItem:getModData().movableData or {}
		self.newItem:getModData().movableData['artAuthor'] = self.station:getModData().author
		self.newItem:getModData().movableData['artBeauty'] = self.station:getModData().sculpture["beauty"]
		self.newItem:getModData().movableData['artStyle'] = self.station:getModData().sculpture["style"]
		self.newItem:getModData().movableData['artSize'] = self.station:getModData().sculpture["size"]
		self.newItem:getModData().movableData['artQuality'] = self.station:getModData().sculpture["quality"]
		if isIceSculpture(self.station) then self.newItem:getModData().movableData['meltTime'] = 32000; end
		--self.newItem:setTooltip(getText("IGUI_PaintingAuthor")..": "..self.newItem:getModData().movableData['artAuthor'])
		--self.character:getInventory():AddItem('Moveables.Moveable'):ReadFromWorldSprite(self.station:getModData().sculpture["result"])
		self.character:getInventory():setDrawDirty(true);
		AuthorPainting.createPaintingName(self.character:getPlayerNum(), self.newItem, self.station:getModData().sculpture["result"])
	elseif self.res and self.res[1] then
		consumeItems(self.character, self.res, self.character:getInventory():getItemsFromType(self.res[1], true))
	end
	self.station:getModData().stage = 0
	self.station:getModData().sculpture = false
	self.station:getModData().author = false
	self.station:getModData().style = self.style
	self.station:setOverlaySprite(self.overlay, isClient())
	self.station:setSprite(self.newStation)
	if isClient() then self.station:transmitUpdatedSpriteToServer(); self.station:transmitModData(); end
	self.character:SetVariable("LootPosition", "Mid")
	ISBaseTimedAction.perform(self);
end

function LSSculptingNewAction:new(Player, Station, NewStation, Texture, Style, Resources, GetSculpture)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = Player
	o.station = Station
	o.newStation = NewStation
	o.saveSculpture = GetSculpture
	o.overlay = Texture
	o.style = Style
	o.res = Resources
	o.newItem = false
	o.ignoreHandsWounds = true;
    o.stopOnWalk        = true;
    o.stopOnRun         = true;
	o.maxTime = 60
	if o.character:isTimedActionInstant() then o.maxTime = 1; end
	return o;
end
