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

LSReactionFullBladder = ISBaseTimedAction:derive("LSReactionFullBladder")

function LSReactionFullBladder:isValid()
	return true
end

function LSReactionFullBladder:update()

end

function LSReactionFullBladder:start()
	self:setOverrideHandModels(nil, nil)
	self:setActionAnim("Bob_FullBladder")
end

function LSReactionFullBladder:stop()
    ISBaseTimedAction.stop(self);
end

function LSReactionFullBladder:perform()
	ISBaseTimedAction.perform(self);
end

function LSReactionFullBladder:new(character)
    local o = {}
    setmetatable(o, self)
    self.__index = self
    o.character = character;
	o.stopOnAim = true
	o.stopOnWalk = true
	o.stopOnRun = true
	o.gameSound = 0
	o.maxTime = 200
	o.ignoreHandsWounds = true
	o.useProgressBar = false
	return o;
end
