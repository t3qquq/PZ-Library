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

require 'ISAmbt/AmbtMng'
require 'ISUI/Maps/ISWorldMap'

--ambt.goal1 - string or float / your first goal
--ambt.goal1progress - boolean or float / progress on your first goal. If goal is a string then goal progress must return a boolean (true if satisfied)
--ambt.reset - if true then all progress will reset if the player disables this ambition mid-progress
--LSAmbtMng.doComplete(player, ambt) - call when your ambition conditions are satisfied

local function LSAmbtComplete(player, ambt)

end

local function isLumberjackValid(player, ambt)
	if ambt and ambt['LSLumberjack'] and ambt['LSLumberjack'].isActive then return true; end
	return false
end

local ogActionPerform = ISChopTreeAction.perform;
function ISChopTreeAction:perform()
	self.character:setVariable("LSChopSpeed", "End")
	local ambt = self.character:getModData().Ambitions
	if isLumberjackValid(self.character, ambt) then
		if ambt['LSLumberjack'].completed then
			if ZombRand(60) >= 20 then
			local worldItem = self.treeSqr:AddWorldInventoryItem("Base.Log", ZombRandFloat(0.0, 1.0), ZombRandFloat(0.0, 1.0), 0)
				--if worldItem then
				--	worldItem:setWorldZRotation(2)
				--	worldItem:getWorldItem():setIgnoreRemoveSandbox(true)
				--	worldItem:getWorldItem():transmitCompleteItemToServer()
				--end
			end
		else
			ambt['LSLumberjack'].goal1progress = math.floor(ambt['LSLumberjack'].goal1progress+1)
		end
	end
    ogActionPerform(self);
end

local function isLumberjackAmbt(player)
	if player:getModData().Ambitions and player:getModData().Ambitions['LSLumberjack'] and player:getModData().Ambitions['LSLumberjack'].completed then return true; end
	return false
end

local ogActionStart = ISChopTreeAction.start;
function ISChopTreeAction:start()
	if isLumberjackAmbt(self.character) and self.character:getModData().Ambitions['LSLumberjack'].isActive then
		local chopSpeed = self.character:getVariableFloat("ChopTreeSpeed", 0)
		chopSpeed = chopSpeed+(chopSpeed/3)
		chopSpeed = tonumber(string.format("%.2f", chopSpeed))
		if chopSpeed == 0 then chopSpeed = 1.5; end
		self.character:setVariable("LSCTS", chopSpeed)
		self.character:setVariable("LSChopSpeed", "Execute")
		self.treeSqr = self.tree:getSquare()
	end
		ogActionStart(self)
end

local ogActionStop = ISChopTreeAction.stop;
function ISChopTreeAction:stop()
	self.character:setVariable("LSChopSpeed", "End")
	ogActionStop(self)
end

local ogActionUseEndurance = ISChopTreeAction.useEndurance;
function ISChopTreeAction:useEndurance()
	if not isLumberjackAmbt(self.character) or ZombRand(50) < 20 then ogActionUseEndurance(self); end
end

local ogActionUpdate = ISChopTreeAction.update;
function ISChopTreeAction:update()
	if not isLumberjackAmbt(self.character) then ogActionUpdate(self);
	else
		self.axe:setJobDelta(self:getJobDelta())
		self.character:faceThisObject(self.tree)
		if instanceof(self.character, "IsoPlayer") then
			self.character:setMetabolicTarget(Metabolics.MediumWork);
		end
	end
end

local ogActionAnimEvent = ISChopTreeAction.animEvent;
function ISChopTreeAction:animEvent(event, parameter)
	if not isLumberjackAmbt(self.character) then ogActionAnimEvent(self, event, parameter);
	else
		if event == 'ChopTree' then
			self.tree:WeaponHit(self.character, self.axe)
			self:useEndurance()
			if ZombRand(self.axe:getConditionLowerChance() * 2 + self.character:getMaintenanceMod() * 2 + 20) == 0 then
				self.axe:setCondition(self.axe:getCondition() - 1)
				ISWorldObjectContextMenu.checkWeapon(self.character);
			else
				self.character:getXp():AddXP(Perks.Maintenance, 1)
			end
			if self.tree:getObjectIndex() == -1 then
				self:forceComplete()
			end
		end
	end
end

local function LSAmbtActiveIncomplete(player, ambt)
	if not player:isAsleep() then 
		local completed = true
		if not ambt['goal1progress'] then ambt['goal1progress'] = 0; end
		if ambt['goal1progress'] < ambt['goal1'] then completed = false; end
		if completed then LSAmbtMng.doComplete(player, ambt); return; end
	end
end

local function LSAmbtIsHidden(player, ambt)
	-- if your ambition starts hidden, add conditions to unlock
	if player:getPerkLevel(Perks.Axe) < 4 then return; end
	if not ambt.delayUnlock then ambt.delayUnlock = true; return; end
	LSAmbtMng.doUnlock(player, ambt)
end

LSAmbtMng.LSLumberjack = function(player, ambt)
	if ambt.isHidden then LSAmbtIsHidden(player, ambt); return; end
	if ambt.completed then -- ambition was completed
		--if ambt.isActive then LSAmbtActiveComplete(player, ambt); end --has active bonuses
		--LSAmbtComplete(player, ambt)
	elseif ambt.isActive or ambt.isPassive then LSAmbtActiveIncomplete(player, ambt); end -- ambition is in progress
end
