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

LSClockWind = ISBaseTimedAction:derive("LSClockWind");

function LSClockWind:isValid()
	return true;
end

function LSClockWind:waitToStart()
	self.action:setUseProgressBar(true)
	self.character:faceThisObject(self.clock);
	return self.character:shouldBeTurning();
end

function LSClockWind:update()
	if self.character:isSitOnGround() then self:forceStop(); end
	if self.jobProgress < (self.maxTime*0.35) then
		self.jobProgress = self:getJobDelta()*self.maxTime
	elseif not self.secondPhase then
		self.secondPhase = true
		self.sound = self.character:getEmitter():playSound("GFClock_Wind")
	end

end

function LSClockWind:start()
	self:setOverrideHandModels(nil, nil)
	self:setActionAnim("Loot")
	self.character:SetVariable("LootPosition", "Mid")
	self.character:getEmitter():playSound("PutItemInBag")
end

function LSClockWind:stop()
	if self.sound and self.sound ~= 0 and self.character:getEmitter():isPlaying(self.sound) then
		self.character:getEmitter():stopSound(self.sound);
	end
    ISBaseTimedAction.stop(self);		
end

function LSClockWind:perform()
	self.clock:getModData().movableData['active'] = true
	self.clock:getModData().movableData['lastWind'] = tonumber(getGameTime():getWorldAgeHours())
	if isClient() then self.clock:transmitModData(); end
	getSoundManager():playUISound("UI_Painting_Complete")
	ISBaseTimedAction.perform(self);
end

function LSClockWind:new(Player, Clock)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = Player
	o.clock = Clock
	o.ignoreHandsWounds = true;
    o.stopOnWalk        = true;
    o.stopOnRun         = true;
	o.maxTime = 80
	o.jobProgress = 0
	o.secondPhase = false
	o.sound = 0
	if o.character:isTimedActionInstant() then o.maxTime = 1; end
	return o;
end
