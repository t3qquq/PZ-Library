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

LSFixAction = ISBaseTimedAction:derive("LSFixAction");

function LSFixAction:isValid()
	return self.obj:getObjectIndex() ~= -1 and self.data and (self.data['isBroken'] or (self.data['inventionData'] and self.data['inventionData']['isBroken'])) and LSUtil.hasItemsOnChar(self.character, self.itemList)
end

function LSFixAction:waitToStart()
	self.character:faceThisObject(self.obj)
	return self.character:shouldBeTurning()
end

function LSFixAction:update()
	self.character:faceThisObject(self.obj)

    self.character:setMetabolicTarget(Metabolics.UsingTools);
end

function LSFixAction:start()
	self:setActionAnim("Loot")
	self.character:SetVariable("LootPosition", "Low")
	self.character:reportEvent("EventLootItem")
	self.sound = self.character:playSound("GeneratorRepair")
end

function LSFixAction:stop()
	self.character:stopOrTriggerSound(self.sound)
    ISBaseTimedAction.stop(self);
end

function LSFixAction:perform()
	self.character:stopOrTriggerSound(self.sound)

	if not LSUtil.consumeItemsOnChar(self.character, self.itemList) then return; end;

	if self.data['isBroken'] then self.data['isBroken'] = false; end
	if self.data['inventionData'] and self.data['inventionData']['isBroken'] then
		self.data['inventionData']['isBroken'] = false;
		LSUtil.doInvCooldown(self.obj, self.data['inventionData'])
	end

	self.character:getXp():AddXP(Perks.Maintenance, 5);

    -- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);

end

function LSFixAction:new(character, object, list, baseTime)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.obj = object;
	o.itemList = list
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.data = object:hasModData() and object:getModData().movableData
	o.maxTime = baseTime/(math.max(1,o.character:getPerkLevel(Perks.Maintenance))/10);
	if o.character:isTimedActionInstant() then o.maxTime = 1; end
    o.caloriesModifier = 4;
	return o;
end

return LSFixAction
