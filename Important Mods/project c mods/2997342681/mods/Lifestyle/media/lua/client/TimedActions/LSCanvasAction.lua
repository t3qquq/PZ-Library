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

LSCanvasAction = ISBaseTimedAction:derive("LSCanvasAction");

function LSCanvasAction:isValid()
	return true;
end

function LSCanvasAction:waitToStart()
	self.action:setUseProgressBar(false)
	self.character:faceThisObject(self.easel);
	return self.character:shouldBeTurning();
end

function LSCanvasAction:update()
	if self.character:isSitOnGround() then self:forceStop(); end

end

function LSCanvasAction:start()
	self:setOverrideHandModels(nil, nil)
	self:setActionAnim("Loot")
	self.character:SetVariable("LootPosition", "High")
	self.character:getEmitter():playSound("PutItemInBag")
end

function LSCanvasAction:stop()
	self.character:SetVariable("LootPosition", "Mid")
    ISBaseTimedAction.stop(self);		
end

function LSCanvasAction:perform()
	if self.savePainting then
		self.newItem = self.character:getInventory():AddItem('Moveables.Moveable')
		self.newItem:ReadFromWorldSprite(self.easel:getModData().painting["result"])
		self.newItem:getModData().movableData = self.newItem:getModData().movableData or {}
		self.newItem:getModData().movableData['artAuthor'] = self.easel:getModData().author
		self.newItem:getModData().movableData['artBeauty'] = self.easel:getModData().painting["beauty"]
		self.newItem:getModData().movableData['artStyle'] = self.easel:getModData().painting["style"]
		self.newItem:getModData().movableData['artSize'] = self.easel:getModData().painting["size"]
		self.newItem:getModData().movableData['artQuality'] = self.easel:getModData().painting["quality"]
		--self.newItem:setTooltip(getText("IGUI_PaintingAuthor")..": "..self.newItem:getModData().movableData['artAuthor'])
		--self.character:getInventory():AddItem('Moveables.Moveable'):ReadFromWorldSprite(self.easel:getModData().painting["result"])
		self.character:getInventory():setDrawDirty(true);
		AuthorPainting.createPaintingName(self.character:getPlayerNum(), self.newItem, self.easel:getModData().painting["result"])
	end
	self.easel:getModData().stage = 0
	self.easel:getModData().painting = false
	self.easel:getModData().author = false
	self.easel:setOverlaySprite(nil, isClient())
	self.easel:setSprite(self.newEasel)
	if isClient() then self.easel:transmitUpdatedSpriteToServer(); self.easel:transmitModData(); end
	self.character:SetVariable("LootPosition", "Mid")
	ISBaseTimedAction.perform(self);
end

function LSCanvasAction:new(Player, Easel, NewEasel, GetPainting)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = Player
	o.easel = Easel
	o.newEasel = NewEasel
	o.savePainting = GetPainting
	o.newItem = false
	o.ignoreHandsWounds = true;
    o.stopOnWalk        = true;
    o.stopOnRun         = true;
	o.maxTime = 60
	if o.character:isTimedActionInstant() then o.maxTime = 1; end
	return o;
end
