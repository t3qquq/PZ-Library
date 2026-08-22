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

LSAddFuelAction = ISBaseTimedAction:derive("LSAddFuelAction");

function LSAddFuelAction:isValid()
	return self.character:isPrimaryHandItem(self.petrol) or self.character:isSecondaryHandItem(self.petrol)
end

function LSAddFuelAction:waitToStart()
	return false
end

function LSAddFuelAction:update()

    self.character:setMetabolicTarget(Metabolics.HeavyDomestic);
end

function LSAddFuelAction:start()
	self:setActionAnim("refuelgascan")
	self:setOverrideHandModels(self.petrol:getStaticModel(), nil)
	self.sound = self.character:playSound("GeneratorAddFuel")
end

function LSAddFuelAction:stop()
	self.character:stopOrTriggerSound(self.sound)
    ISBaseTimedAction.stop(self);
end

function LSAddFuelAction:perform()
	self.character:stopOrTriggerSound(self.sound)

    local endFuel = 0;
    while self.petrol and self.petrol:getUsedDelta() > 0 and self.item:getUsedDelta() < 1 do
		self.item:setUsedDelta(math.min(self.item:getUsedDelta() + self.item:getUseDelta() * 5, 1));
        self.petrol:Use();
    end

	ISBaseTimedAction.perform(self);
end

function LSAddFuelAction:new(character, item, petrolCan, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
    o.item = item
	o.petrol = petrolCan
	o.stopOnWalk = true
	o.stopOnRun = true
	o.maxTime = time
	if o.character:isTimedActionInstant() then o.maxTime = 1; end
	return o
end

return LSAddFuelAction