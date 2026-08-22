
require 'ISAmbt/AmbtMng'

--ambt.goal1 - string or float / your first goal
--ambt.goal1progress - boolean or float / progress on your first goal. If goal is a string then goal progress must return a boolean (true if satisfied)
--ambt.reset - if true then all progress will reset if the player disables this ambition mid-progress
--LSAmbtMng.doComplete(player, ambt) - call when your ambition conditions are satisfied

local LSAMBTGEEvent = {
	false,
	false
}

local function eventIsValid(player, target, damage)
	if not player or not instanceof(player, "IsoPlayer") then return false; end
	if player:isDoShove() then return false; end
	if (not target) or (target:isDead()) then return false; end
	if not damage then return false; end
	if player and player:hasModData() and (not player:isDead()) and player:getModData().Ambitions then return true; end
	return false
end

local function LSGEonHit(attacker, target, weapon, damage)
	if eventIsValid(attacker, target, damage) then
		local ambt = attacker:getModData().Ambitions['LSGoodEating']
		if ambt and ambt.completed and ambt.isActive then
			LSAMBTGEEvent[2] = tostring(target)
		else
			LSAMBTGEEvent[1] = false
			LSAMBTGEEvent[2] = false
			Events.OnZombieDead.Remove(LSGEOnZDead)
			Events.OnWeaponHitCharacter.Remove(LSGEonHit)
		end
	end
end

local function LSGEOnZDead(zombie)
	if zombie and LSAMBTGEEvent[2] and (LSAMBTGEEvent[2] == tostring(zombie)) then
		if (ZombRand(20) == 0) then
			local total = ZombRand(3)+1
			for n=1, total do
				local sausage = InventoryItemFactory.CreateItem("Lifestyle.BloodSausage");
				zombie:getInventory():AddItem(sausage);
			end
		end
		LSAMBTGEEvent[2] = false
	end
end

local function LSAmbtActiveComplete(player, ambt)
	if not LSAMBTGEEvent[1] then LSAMBTGEEvent[1] = true; Events.OnWeaponHitCharacter.Add(LSGEonHit); Events.OnZombieDead.Add(LSGEOnZDead); end
end

local function actionIsValid(player)
	if player and player:hasModData() and player:getModData().Ambitions and
	player:getModData().Ambitions['LSGoodEating'] and player:getModData().Ambitions['LSGoodEating'].isActive and
	(not player:getModData().Ambitions['LSGoodEating'].completed) then return true; end
	return false
end

local function foodIsValid(item)
	if item:isbDangerousUncooked() and (not item:isCooked()) and (not item:isBurnt()) then return true; end
	return false
end

local ogActionPerform = ISEatFoodAction.perform;
function ISEatFoodAction:perform()
	if actionIsValid(self.character) and foodIsValid(self.item) then
		if not self.character:getModData().Ambitions['LSGoodEating'].goal1progress then self.character:getModData().Ambitions['LSGoodEating'].goal1progress = 0; end
		self.character:getModData().Ambitions['LSGoodEating'].goal1progress = math.floor(self.character:getModData().Ambitions['LSGoodEating'].goal1progress+1)
	end
    ogActionPerform(self);
end

local function LSAmbtActiveIncomplete(player, ambt)
	if not player:isAsleep() then
		if not ambt.goal1progress then ambt.goal1progress = 0; end
		if ambt.goal1progress >= ambt.goal1 then LSAmbtMng.doComplete(player, ambt); end
	end
end

local function LSAmbtIsHidden(player, ambt)
	-- if your ambition starts hidden, add conditions to unlock
	--local hunger = player:getStats():getHunger()
	local hungerLvl = getPlayer():getMoodleLevel(MoodleType.Hungry)
	if not ambt.countdown then ambt.countdown = 288; end
	if (hungerLvl < 3) and (ambt.countdown > 0) then ambt.countdown = 288; return; end
	ambt.countdown = ambt.countdown-1
	if ambt.countdown > 0 then return; end
	LSAmbtMng.doUnlock(player, ambt)
end

local function disableEventsCheck(ambt)
	if LSAMBTGEEvent[1] and ((not ambt.completed) or not (ambt.isActive)) then Events.OnWeaponHitCharacter.Remove(LSGEonHit); Events.OnZombieDead.Remove(LSGEOnZDead); LSAMBTGEEvent[1] = false; end
end

LSAmbtMng.LSGoodEating = function(player, ambt)
	if ambt.isHidden then LSAmbtIsHidden(player, ambt);
	elseif ambt.completed then -- ambition was completed
		if ambt.isActive then LSAmbtActiveComplete(player, ambt); end --has active bonuses
	elseif ambt.isActive or ambt.isPassive then LSAmbtActiveIncomplete(player, ambt); end -- ambition is in progress
end
