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

--ambt.goal1 - string or float / your first goal
--ambt.goal1progress - boolean or float / progress on your first goal. If goal is a string then goal progress must return a boolean (true if satisfied)
--ambt.reset - if true then all progress will reset if the player toggles this ambition off mid-progress
--ambt.offbhv - if true then ambtMng will call your ambition when updating even if not passive and not isActive, useful to add behavior when ambition was deactivated by the player
--LSAmbtMng.doComplete(player, ambt) - call when your ambition conditions are satisfied

local LSAMBTEDEvent = {
	false,
	false,
	0,
	0,
	0,
	false,
	2,
}

local function getItemsTable(n)
	if n == 1 then return {"Moveables.location_military_knox_01_1","Moveables.location_military_knox_01_2"};
	elseif n == 2 then return {"Base.Bracelet_BangleRightGold","Base.Bracelet_BangleLeftGold","Base.Bracelet_ChainRightGold","Base.Bracelet_ChainLeftGold","Base.Necklace_Gold",
	"Base.Necklace_GoldDiamond","Base.Necklace_GoldRuby","Base.NoseRing_Gold","Base.NoseStud_Gold","Base.Earring_Stud_Gold","Base.WristWatch_Right_ClassicGold",
	"Base.WristWatch_Left_ClassicGold","Base.Earring_LoopLrg_Gold","Base.NecklaceLong_Gold","Base.NecklaceLong_GoldDiamond","Base.Earring_LoopMed_Gold","Base.Earring_LoopSmall_Gold_Both",
	"Base.Earring_LoopSmall_Gold_Top","Base.WeddingRing_Man","Base.WeddingRing_Woman","Base.BellyButton_DangleGold","Base.BellyButton_RingGoldRuby","Base.BellyButton_DangleGoldRuby","Base.BellyButton_RingGold",
	"Base.BellyButton_RingGoldDiamond","Base.BellyButton_StudGold","Base.BellyButton_StudGoldDiamond","Base.Ring_Right_MiddleFinger_Gold","Base.Ring_Left_MiddleFinger_Gold","Base.Ring_Right_RingFinger_Gold",
	"Base.Ring_Left_RingFinger_Gold","Base.Ring_Right_MiddleFinger_GoldRuby","Base.Ring_Left_MiddleFinger_GoldRuby","Base.Ring_Right_RingFinger_GoldRuby","Base.Ring_Left_RingFinger_GoldRuby","Base.Ring_Right_MiddleFinger_GoldDiamond",
	"Base.Ring_Left_MiddleFinger_GoldDiamond","Base.Ring_Right_RingFinger_GoldDiamond","Base.Ring_Left_RingFinger_GoldDiamond"};
	elseif n == 3 then return {"Base.Money"};
	end
end

local function consumeItems(thisPlayer, itemList, amount, key)
	local add = 1
	if key == 3 then add = 0.5; end
	for x=0,itemList:size() - 1 do
		local item = itemList:get(x)
		if not item:IsClothing() or not thisPlayer:isEquippedClothing(item) then
			thisPlayer:getInventory():DoRemoveItem(item)
			thisPlayer:getInventory():setDrawDirty(true);
			amount = amount+add
			if amount >= LSAMBTEDEvent[7] then break; end
		end
	end
	return amount
end

local function getItemsTotal(thisPlayer, key, completed)
	local t = getItemsTable(key)
	local amount = 0
	for n=1, #t do
		if completed and amount >= LSAMBTEDEvent[7] then return amount; end
		local items = thisPlayer:getInventory():getItemCount(t[n], true)
		--if thisPlayer:getInventory():getItemFromType(t[n]):IsClothing() and thisPlayer:isEquippedClothing(t[n]) then items = math.max(0, items-1); end
		if completed and items > 0 then amount = consumeItems(thisPlayer, thisPlayer:getInventory():getItemsFromType(t[n], false), amount, key);
		else amount = amount+items; end
	end
	return amount
end

local function eventIsValid(player, target, damage)
	if not player or not instanceof(player, "IsoPlayer") or player:isDoShove() or not target or not damage then return false; end
	if player and player:hasModData() and (not player:isDead()) and player:getModData().Ambitions then return true; end
	return false
end

local function getOverDMG(damage)
	return damage*LSAMBTEDEvent[5]
end

local function playerHasWT(player, weapon)
	if (not weapon) or (not weapon:IsWeapon()) then return false; end
	local weaponType = WeaponType.getWeaponType(player)
	if weaponType and ((weaponType == WeaponType.barehand)) then return false; end
	local wCat = weapon.getCategories and weapon:getCategories() --!
	if wCat then return true; end
	return false
end

local LSEDlastZomb

local function LSEDonHit(attacker, target, weapon, damage)
	if eventIsValid(attacker, target, damage) then
		local ambt = attacker:getModData().Ambitions['LSElDorado']
		if ambt and ambt.completed then
			LSEDlastZomb = tostring(target)
			if ambt.isActive and not target:isDead() then
				local overDmg = getOverDMG(damage)
				if playerHasWT(attacker, weapon) and (overDmg > 0) and (target:getHealth() > 0) then
					local newHealth = target:getHealth()-overDmg
					if newHealth < 0 then target:Kill(attacker); return; end
					target:setHealth(newHealth)
				end
			end
		else
			LSAMBTEDEvent[1] = false
			Events.OnZombieDead.Remove(LSEDOnZDead)
			Events.OnWeaponHitCharacter.Remove(LSEDonHit)
		end
	end
end

local function getCompleteAmount(player)
	local amount = 0
	for n=2, 3 do
		local multi, check = 1, 2
		if n == 1 then multi = 100; elseif n == 2 then check, multi = 3, 0.5; end
		amount = amount+(getItemsTotal(player, check, true))
	end
	return amount
end

local function getTargetVal(ambt)
	if ambt.isActive then
		return LSAMBTEDEvent[4]+3
	end
	local val = LSAMBTEDEvent[4]/2
	if val < 10 then val = 0; end
	return val
end

local function floatToDigitString(num)
    local str = string.format("%.1f", num)
    return string.gsub(str, "0%.", "")
end

local function getMoodleBonus(moodleVal)
	local t = {
		ElDoradoGood8 = 1.5,
		ElDoradoGood6 = 1,
		ElDoradoGood4 = 0.5,
		ElDoradoGood2 = 0.2,
	}
	return t[moodleVal] or 0
end

local function resetGoldMoodles(player)
	if player:getModData().LSMoodles["ElDoradoGood"].Value ~= 0 then player:getModData().LSMoodles["ElDoradoGood"].Value = 0; end
	if player:getModData().LSMoodles["ElDoradoBad"].Value ~= 0 then player:getModData().LSMoodles["ElDoradoBad"].Value = 0; end
end

local function setGoldMoodle(player, moodle)
	if moodle[1] ~= "ElDoradoGood" then player:getModData().LSMoodles["ElDoradoGood"].Value = 0; else player:getModData().LSMoodles["ElDoradoBad"].Value = 0; end
	if moodle[2] then player:getModData().LSMoodles[moodle[1]].Value = math.min(0.8, player:getModData().LSMoodles[moodle[1]].Value+0.2);
	else player:getModData().LSMoodles[moodle[1]].Value = math.max(0, player:getModData().LSMoodles[moodle[1]].Value-0.2); end
	player:getModData().LSMoodles[moodle[1]].Value = math.floor(player:getModData().LSMoodles[moodle[1]].Value*10)/10
end

local function getGoldMod(player)
	local num, moodle = 0, false
	if LSAMBTEDEvent[3] >= LSAMBTEDEvent[7] then
		if player:getModData().LSMoodles["ElDoradoBad"].Value > 0 then moodle = {"ElDoradoBad", false};
		else moodle = {"ElDoradoGood", true}; end
	else
		if player:getModData().LSMoodles["ElDoradoGood"].Value > 0 then moodle = {"ElDoradoGood", false};
		else moodle = {"ElDoradoBad", true}; end
	end
	setGoldMoodle(player, moodle)
	if moodle[1] == "ElDoradoGood" then
		local moodleValStr = floatToDigitString(player:getModData().LSMoodles[moodle[1]].Value)
		num = getMoodleBonus(moodle[1]..moodleValStr)
	end
	return num
end

local function fixStats(player)
	if (player:getStats():getStress() > 1) then
		player:getStats():setStress(1)
	end
end

local function applyStress(player)
	--stress stuff here
	if player:HasTrait("Smoker") then
		player:getStats():setStressFromCigarettes(0)
	end
	local addStress, currentStress = 0.1, player:getStats():getStress()
	if LSAMBTEDEvent[5] == 1 then addStress = 0.2; end
	player:getStats():setStress(currentStress+addStress)
	fixStats(player)
end

local function getItem()
	local prop
	local items = getAllItems()
    for i=0, items:size()-1 do
        local item = items:get(i)
        if item and item:getFullName() and ((item:getFullName() == "Money") or (item:getFullName() == "Base.Money")) then
			prop = item:InstanceItem(item:getFullName())
			break
        end
    end
	return prop
end

local function getChance(player) -- 20 is 5% / 30 is 3.3% / 15 is about 7.5%
	local chance = 20
	if player:HasTrait('Lucky') then chance = chance-5;
	elseif player:HasTrait('Unlucky') then chance = chance+10; end
	return chance
end

local function updateZombKills(player, ambt)
	if not ambt.ogKills then ambt.ogKills = player:getZombieKills(); end
	if player:getZombieKills() > ambt.ogKills then 
		ambt.ogKills = player:getZombieKills()
		local chance = getChance(player)
		if ZombRand(chance) == 0 then
			local item = getItem()
			player:getInventory():AddItem(item)
			--player:getInventory():setDrawDirty(true)
			ISInventoryPage.dirtyUI()
		end
	end
end

local function LSEDOnZDead(zombie)
	if zombie and LSEDlastZomb and (LSEDlastZomb == tostring(zombie)) then
		local chance = LSAMBTEDEvent[6]
		if chance and (ZombRand(chance) == 0) then
			local money = InventoryItemFactory.CreateItem("Base.Money");
			zombie:getInventory():AddItem(money);
		end
		LSEDlastZomb = nil
	end
end

local function LSAmbtComplete(player, ambt)
	-- optional
	-- add active completed effects
	if player:isAsleep() then return; end
	--updateZombKills(player, ambt)
	if not LSAMBTEDEvent[6] then LSAMBTEDEvent[6] = getChance(player); end
	if LSAMBTEDEvent[4] ~= 0 then LSAMBTEDEvent[4] = getTargetVal(ambt); end
	if not LSAMBTEDEvent[1] then LSAMBTEDEvent[1] = true; Events.OnWeaponHitCharacter.Add(LSEDonHit); Events.OnZombieDead.Add(LSEDOnZDead); end
	if ambt.isActive and (not LSAMBTEDEvent[2]) then -- checks each 10 min
		if LSAMBTEDEvent[4] == 0 then LSAMBTEDEvent[4] = 10; end
		LSAMBTEDEvent[3] = getCompleteAmount(player)
		LSAMBTEDEvent[5] = getGoldMod(player)
	end
	if not LSAMBTEDEvent[2] then LSAMBTEDEvent[2] = true; else LSAMBTEDEvent[2] = false; end
	if ambt.isActive and (LSAMBTEDEvent[5] == 0) then applyStress(player); end
	if not ambt.isActive then resetGoldMoodles(player); end
end

local function LSAmbtActiveIncomplete(player, ambt)
	-- add conditions to add/remove progress
	-- apply buffs/debuffs if ambition has any while active and incomplete
	if player:isAsleep() then return; end
	local completed = true
	for n=1, 3 do
		if not ambt['goal'..n..'progress'] then ambt['goal'..n..'progress'] = 0; end
		if ambt['goal'..n..'progress'] < ambt['goal'..n] then -- additional check to ensure it won't recalculate if player had previously satisfied the condition
			ambt['goal'..n..'progress'] = getItemsTotal(player, n)
			if ambt['goal'..n..'progress'] < ambt['goal'..n] then completed = false; end
		end
	end
	if completed then ambt.offBhv = true; LSAmbtMng.doComplete(player, ambt); end
end

local function LSAmbtIsHidden(player, ambt)
	-- if your ambition starts hidden, add conditions to unlock
	
	--LSAmbtMng.doUnlock(player, ambt)
end

local function disableEventsCheck(player, ambt)
	if LSAMBTEDEvent[1] and (not ambt.completed) then Events.OnWeaponHitCharacter.Remove(LSEDonHit); Events.OnZombieDead.Remove(LSEDOnZDead); LSAMBTEDEvent[1] = false; resetGoldMoodles(player); end
end

LSAmbtMng.LSElDorado = function(player, ambt)
	disableEventsCheck(player, ambt)
	if ambt.isHidden then LSAmbtIsHidden(player, ambt); return; end
	if ambt.completed then LSAmbtComplete(player, ambt); -- ambition was completed
	elseif ambt.isActive or ambt.isPassive then LSAmbtActiveIncomplete(player, ambt); end -- ambition is in progress
end
