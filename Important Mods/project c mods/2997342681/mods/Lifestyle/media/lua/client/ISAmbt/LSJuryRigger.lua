
require 'ISAmbt/AmbtMng'

--ambt.goal1 - string or float / your first goal
--ambt.goal1progress - boolean or float / progress on your first goal. If goal is a string then goal progress must return a boolean (true if satisfied)
--ambt.reset - if true then all progress will reset if the player disables this ambition mid-progress
--ambt.forceReset = true - to reset this ambition for all players, even if they completed it, useful when updating ambition table params - if you don't use forceReset then changes will only apply to new characters
--LSAmbtMng.doComplete(player, ambt) - call when your ambition conditions are satisfied

local ogActionPerform = ISFixAction.perform;
function ISFixAction:perform()
	--print("ISFixAction:perform() called")
	if self.character and self.character:hasModData() and self.character:getModData().Ambitions and
	self.character:getModData().Ambitions['LSJuryRigger'] then
		if self.character:getModData().Ambitions['LSJuryRigger'].completed then
			local JRrepair = self.item:getModData().JRrepair
			if self.vehiclePart then JRrepair = self.character:getModData().JRrepair; end
			local repairNum = self.item:getHaveBeenRepaired()
			--if repairNum then print("ISFixAction:perform() repairNum is "..repairNum); end
			if repairNum and (repairNum < 2) and (not JRrepair) then
				self.item:getModData().JRrepair = 0
				if self.vehiclePart then self.character:getModData().JRrepair = 0; end
				self.item:setHaveBeenRepaired(0)
				--print("ISFixAction:perform() first repair")
			elseif self.character:getModData().Ambitions['LSJuryRigger'].isActive then
				if not self.item:getModData().JRrepair then self.item:getModData().JRrepair = 0; end
				if not self.character:getModData().JRrepair then self.character:getModData().JRrepair = 0; end
				self.item:getModData().JRrepair = math.floor(self.item:getModData().JRrepair+1)
				if self.vehiclePart then self.character:getModData().JRrepair = math.floor(self.character:getModData().JRrepair+1); end
				if math.floor((JRrepair+1))%2 == 0 then -- true only if it's an even number, else is false
					if not repairNum then repairNum = 1; end
					local repairTotal = math.floor(repairNum-1)
					--print("ISFixAction:perform() repair total is "..repairTotal)
					--print("ISFixAction:perform() JRrepair total is "..self.item:getModData().JRrepair)
					self.item:setHaveBeenRepaired(repairTotal)
				end
			end
		elseif self.character:getModData().Ambitions['LSJuryRigger'].isActive then
			if not self.character:getModData().Ambitions['LSJuryRigger'].goal1progress then self.character:getModData().Ambitions['LSJuryRigger'].goal1progress = 0; end
			self.character:getModData().Ambitions['LSJuryRigger'].goal1progress = math.floor(self.character:getModData().Ambitions['LSJuryRigger'].goal1progress+1)		
		end
	end
    ogActionPerform(self);
end

local function LSAmbtActiveIncomplete(player, ambt)
	ambt.reset = true
	if not player:isAsleep() then
		if not ambt.goal1progress then ambt.goal1progress = 0; end
		if ambt.goal1progress >= ambt.goal1 then LSAmbtMng.doComplete(player, ambt); end
	end
end

local function LSAmbtIsHidden(player, ambt)
	if player:getPerkLevel(Perks.Maintenance) < 6 then return; end
	if not ambt.delayUnlock then ambt.delayUnlock = true; return; end
	LSAmbtMng.doUnlock(player, ambt)
end

LSAmbtMng.LSJuryRigger = function(player, ambt)
	if ambt.isHidden then LSAmbtIsHidden(player, ambt); return; end
	if ambt.completed then -- ambition was completed
		return
		--if ambt.isActive then LSAmbtActiveComplete(player, ambt); end --active: no penalties for every second repair
		--LSAmbtPassiveComplete() --passive: no penalty for first repair
	elseif ambt.isActive or ambt.isPassive then LSAmbtActiveIncomplete(player, ambt); end -- ambition is in progress
end
