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
require 'Helper/MovementUtil'

--ambt.goal1 - string or float / your first goal
--ambt.goal1progress - boolean or float / progress on your first goal. If goal is a string then goal progress must return a boolean (true if satisfied)
--ambt.reset - if true then all progress will reset if the player disables this ambition mid-progress
--LSAmbtMng.doComplete(player, ambt) - call when your ambition conditions are satisfied

local LSAMBTWEvent = {
	false,
	2,
	false,
}

local function LSAmbtActiveComplete(player, ambt)
	local LSMovUtil = require('Helper/MovementUtil')
	if LSMovUtil.isRunning then return; end
	LSMovUtil.setRunning(true)
end

local function LSAmbtComplete(player, ambt)
	if not player:HasTrait("Outdoorsman") then
		player:getTraits():add("Outdoorsman")
		HaloTextHelper.addTextWithArrow(player, getText("UI_trait_outdoorsman"), true, HaloTextHelper.getColorGreen())
	end
	local playerWeight = player:getMaxWeightBase()
	if not ambt.newWeight or (not LSAMBTWEvent[3] and playerWeight ~= ambt.newWeight) or playerWeight < ambt.newWeight then 
		ambt.newWeight = playerWeight+LSAMBTWEvent[2]
		player:setMaxWeightBase(ambt.newWeight)
		LSAMBTWEvent[3] = true
		return
	end
end

local function playerMovedAway(pX, pY, x, y)
	if pX > x+1 or pX < x-1 or pY > y+1 or pY < y-1 then return true; end
	return false
end

local function LSWOnMove(player)
	if not LSAMBTWEvent[1] then LSAMBTWEvent[1] = true; end
	if player:getVehicle() then return; end
	local ambt = player:getModData().Ambitions['LSWanderer']
	if not ambt or not ambt['goal1progress'] then return; end
	if not ambt.ogX or not ambt.ogY or playerMovedAway(player:getX(), player:getY(), ambt.ogX, ambt.ogY) then
		ambt['goal1progress'] = math.ceil(ambt['goal1progress']+1); ambt.ogX = player:getX(); ambt.ogY = player:getY()
	end
	--[[
	if not ambt.ogSqr or player:getSquare() ~= ambt.ogSqr then
		ambt['goal1progress'] = math.ceil(ambt['goal1progress']+1); ambt.ogSqr = player:getSquare()
	end
	]]--
	--[[
	local addMeters, isRunning = 0, "walking"
	if player:isRunning() then isRunning = "running"; end
	print("Player moving at "..tostring(player:getMoveSpeed()).." and is "..isRunning)
	]]--
end

local function LSAmbtActiveIncomplete(player, ambt)
	if not player:isAsleep() then 
		local completed = true
		if not ambt['goal1progress'] then ambt['goal1progress'] = 0; end
		if ambt['goal1progress'] < ambt['goal1'] then completed = false; end
		if completed then LSAmbtMng.doComplete(player, ambt); return; end
		if not LSAMBTWEvent[1] then Events.OnPlayerMove.Add(LSWOnMove); LSAMBTWEvent[1] = true; end
	end
end

local function disableEventsCheck(player, ambt)
	if LSAMBTWEvent[1] and (ambt.completed or not ambt.isActive) then Events.OnPlayerMove.Remove(LSWOnMove); LSAMBTWEvent[1] = false; end
end

LSAmbtMng.LSWanderer = function(player, ambt)
	disableEventsCheck(player, ambt)
	if ambt.completed then -- ambition was completed
		if ambt.isActive then LSAmbtActiveComplete(player, ambt); end --has active bonuses
		LSAmbtComplete(player, ambt)
	elseif ambt.isActive or ambt.isPassive then LSAmbtActiveIncomplete(player, ambt); end -- ambition is in progress
end
