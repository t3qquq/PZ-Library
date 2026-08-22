--***********************************************************
--** PseudonymousEd, the Dev
--** Simple Playable Pianos 41
--** Create and Clean the Piano Session Tracker Table
--***********************************************************
require('NPCs/MainCreationMethods');

local function initSPPSessionTracker(_player)
	local player = _player

	player:getModData().SPPsessions = {}
end

-- Designed to enable Session Tracker on existing games
local function checkSPPSessionTracker()
	local player = getPlayer();
	if player:hasModData()
		and player:getModData().SPPsessions ~= nil
	then return
	else
		player:getModData().SPPsessions = {}
	end
end

-- Designed to remove old sessions from tracker table
local function updateSPPSessionTracker()
	local player = getPlayer()
	local playerData = player:getModData()

	local n = #playerData.SPPsessions

	-- Delete records > 7 hours old, add 1 to hoursSince for all others
	for i = n,1,-1 do
		if playerData.SPPsessions[i].hoursSince > 7 then
			table.remove(playerData.SPPsessions,i)
		else
			playerData.SPPsessions[i].hoursSince = playerData.SPPsessions[i].hoursSince + 1
		end
	end
	
end

Events.OnNewGame.Add(initSPPSessionTracker)
Events.EveryHours.Add(updateSPPSessionTracker)
Events.OnGameStart.Add(checkSPPSessionTracker)
