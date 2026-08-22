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

require('NPCs/MainCreationMethods');

--
local musicStyles = {"disco","beach","classical","country","holiday","jazz","metal","muzak","pop","rap","rbsoul","reggae","rock","salsa","world"};
local musicStylesDislike = {"discono","beachno","classicalno","countryno","holidayno","jazzno","metalno","muzakno","popno","rapno","rbsoulno","reggaeno","rockno","salsano","worldno"};
-- new game
local function initPlayerTracker(_player)
	local player = _player
	--PlayTracker
	player:getModData().PlayTracker = {}
	--HaloCooldownCounter
	if not (player:getModData().HaloCooldownCounter) or (not (tonumber(player:getModData().HaloCooldownCounter))) then player:getModData().HaloCooldownCounter = 0; end
	--PlayerVoice
	if not (not player:getModData().PlayerVoice) or (not (tonumber(player:getModData().PlayerVoice))) then player:getModData().PlayerVoice = ZombRand(5); end
end

-- we use this to enable any data that might not exist
local function LScheckPlayerTracker()
    if isClient() then
        ModData.request("LSDATA")
	end
	
	for i = 0, getNumActivePlayers() - 1 do
		local player = getSpecificPlayer(i)
	
	if player and player:hasModData() and (not player:isDead()) then
	
	if SandboxVars.Text.DividerMusicNew then InstrumentsUtilOnCreatePlayer(player); end
	
	player:getModData().LSZenActive = nil
	player:getModData().IsMeditating = false
	player:getModData().cleaningETime = false
	player:getModData().hygieneNeedETime = false
	--MusicMoodle and DiscoBall
	player:getModData().ListenedToMusic = -1
	player:getModData().ActiveDiscoBallNearby = false
	player:getModData().IsListeningToJukebox = false
	player:getModData().VanillaMusicResume = 0
	player:getModData().PlayingDJBooth = false
	player:getModData().IsDancingInit = false
	player:getModData().IsDancingFull = false
	player:getModData().IsDancingFullPartner = false
	player:getModData().WantsToDance = false
	player:getModData().OtherPlayersAroundDancing = 0
	--Sitting
	player:getModData().IsSittingOnSeat = false
	player:getModData().IsSittingOnPianoStool = false
	player:getModData().IsSittingOnSeatSouth = false
	--Toilet
	player:getModData().IsDoingToilet = false
	--PlayTracker
	if not player:getModData().PlayTracker then player:getModData().PlayTracker = {}; end
	--HaloCooldownCounter
	if (not player:getModData().HaloCooldownCounter) or (not (tonumber(player:getModData().HaloCooldownCounter))) then player:getModData().HaloCooldownCounter = 0; end
	--TDcomplained
	if not player:getModData().TDcomplained then player:getModData().TDcomplained = false; end
	--GaveApplause
	if not player:getModData().GaveApplause then player:getModData().GaveApplause = false; end
	--PlayerVoice
	if (not player:getModData().PlayerVoice) or (not (tonumber(player:getModData().PlayerVoice))) then player:getModData().PlayerVoice = ZombRand(5); end

	--[[
	--PlayerMusicTaste
	if (not player:HasTrait("Deaf")) and (not player:HasTrait("ToneDeaf")) then
		local musicTraitPos, musicTraitNeg
		for i=1, #musicStyles do
			local musicTrait = musicStyles[i]
			if player:HasTrait(musicTrait) then
				musicTraitPos = musicTrait
				break
			end
		end
		for i=1, #musicStylesDislike do
			local musicTrait = musicStylesDislike[i]
			if player:HasTrait(musicTrait) then
				musicTraitNeg = musicTrait
				break
			end
		end
		if not musicTraitPos then
			local idxmusicStyles = ZombRand(#musicStyles) + 1
			local randomMusicStyle = musicStyles[idxmusicStyles]
			if musicTraitNeg and string.find(musicTraitNeg, randomMusicStyle) then
				for i=1, 20 do
					local newIdx = ZombRand(#musicStyles)+1
					randomMusicStyle = musicStyles[newIdx]
					if not string.find(musicTraitNeg, randomMusicStyle) then break; end
				end
			end
			player:getTraits():add(randomMusicStyle)
			player:getModData().PlayerMusicLike = randomMusicStyle
			HaloTextHelper.addTextWithArrow(player, getText("UI_trait_" .. randomMusicStyle), true, HaloTextHelper.getColorGreen())
		else
			player:getModData().PlayerMusicLike = musicTraitPos
		end
		if not musicTraitNeg then
			local idxmusicStyles = ZombRand(#musicStylesDislike) + 1
			local randomMusicStyle = musicStylesDislike[idxmusicStyles]
			if musicTraitPos and string.find(randomMusicStyle, musicTraitPos) then
				for i=1, 20 do
					local newIdx = ZombRand(#musicStylesDislike)+1
					randomMusicStyle = musicStylesDislike[newIdx]
					if not string.find(randomMusicStyle, musicTraitPos) then break; end
				end
			end
			player:getTraits():add(randomMusicStyle)
			player:getModData().PlayerMusicDislike = randomMusicStyle
			HaloTextHelper.addTextWithArrow(player, getText("UI_trait_" .. randomMusicStyle), true, HaloTextHelper.getColorRed())
		else
			player:getModData().PlayerMusicDislike = musicTraitNeg
		end	
	end
	]]--
	--PlayingInstrument
	player:getModData().PlayingInstrument = false
	--IsMeditationDisturbed
	player:getModData().IsMeditationDisturbed = false	
	--HYGIENE
	player:getModData().hygieneNeedLimit = 100
	player:getModData().IsDoingShower = false
	
	--DJBooth
	player:getModData().DJNotFailstate = true
	player:getModData().DJBoothOverlayPanel = false
	player:getModData().DJBoothCustomLoopPlaying = false
	player:getModData().DJBoothCustomLoop = false
	player:getModData().DJBoothCustomLoopKeyPressed = 0
	player:getModData().DJBoothCustomLoopActive = 0
	player:getModData().IsListeningToDJ = false

	-- REGULAR BUTTONS
	player:getModData().DJKEYLEFTRIGHT = false
	player:getModData().DJKEYUP = false
	player:getModData().DJKEYDOWN = false
	player:getModData().DJKEY = 0
	
	--SWITCHES
	player:getModData().DJBoothSwitchAPressed = false
	player:getModData().DJBoothSwitchBPressed = false
	player:getModData().DJBoothSwitchCPressed = false
	player:getModData().DJBoothSwitchDPressed = false
	player:getModData().DJBoothSwitchBAPressed = false
	player:getModData().DJBoothSwitchBAPressed = false
	--VYNIL
	player:getModData().VynilAScratched = false
	player:getModData().VynilAScratchedTimes = 0
	player:getModData().VynilBScratched = false
	player:getModData().VynilBScratchedTimes = 0
	--RGB
	player:getModData().DJBoothBigButtonRGB = 0
	--Small Buttons
	player:getModData().DJBoothSmallButtonAPressed = false
	player:getModData().DJBoothSmallButtonBPressed = false
	player:getModData().DJBoothSmallButtonCPressed = false
	player:getModData().DJBoothSmallButtonDPressed = false
	player:getModData().DJBoothSmallButtonEPressed = false
	player:getModData().DJBoothSmallButtonFPressed = false
	player:getModData().DJBoothSmallButtonGPressed = false
	player:getModData().DJBoothSmallButtonHPressed = false
	player:getModData().DJBoothSmallButtonIPressed = false
	player:getModData().DJBoothSmallButtonJPressed = false
	player:getModData().DJBoothSmallButtonKPressed = false
	player:getModData().DJBoothSmallButtonLPressed = false
	player:getModData().DJBoothSmallButtonMPressed = false
	player:getModData().DJBoothSmallButtonNPressed = false
	player:getModData().DJBoothSmallButtonOPressed = false
	player:getModData().DJBoothSmallButtonPPressed = false
	
	--Big Buttons
	player:getModData().DJBoothBigButtonAPressed = false
	player:getModData().DJBoothBigButtonAPressedCount = 0
	player:getModData().DJBoothBigButtonBPressed = false
	player:getModData().DJBoothBigButtonBPressedCount = 0
	player:getModData().DJBoothBigButtonCPressed = false
	player:getModData().DJBoothBigButtonCPressedCount = 0
	player:getModData().DJBoothBigButtonDPressed = false
	player:getModData().DJBoothBigButtonDPressedCount = 0
	player:getModData().DJBoothBigButtonEPressed = false
	player:getModData().DJBoothBigButtonEPressedCount = 0
	player:getModData().DJBoothBigButtonFPressed = false
	player:getModData().DJBoothBigButtonFPressedCount = 0
	player:getModData().DJBoothBigButtonGPressed = false
	player:getModData().DJBoothBigButtonGPressedCount = 0
	player:getModData().DJBoothBigButtonHPressed = false
	player:getModData().DJBoothBigButtonHPressedCount = 0
	player:getModData().DJBoothBigButton1Pressed = false
	player:getModData().DJBoothBigButton1PressedCount = 0
	player:getModData().DJBoothBigButton2Pressed = false
	player:getModData().DJBoothBigButton2PressedCount = 0
	player:getModData().DJBoothBigButton3Pressed = false
	player:getModData().DJBoothBigButton3PressedCount = 0
	player:getModData().DJBoothBigButton4Pressed = false
	player:getModData().DJBoothBigButton4PressedCount = 0
	player:getModData().DJBoothBigButton5Pressed = false
	player:getModData().DJBoothBigButton5PressedCount = 0
	player:getModData().DJBoothBigButton6Pressed = false
	player:getModData().DJBoothBigButton6PressedCount = 0
	player:getModData().DJBoothBigButton7Pressed = false
	player:getModData().DJBoothBigButton7PressedCount = 0
	player:getModData().DJBoothBigButton8Pressed = false
	player:getModData().DJBoothBigButton8PressedCount = 0
		end
	end
end

--By hour
local function updatePlayerTrackerByHour()
	local player = getPlayer()
	local playerData = player:getModData()

	--PlayTracker
	-- we add this loop and update at each hour to reset the contents stored in PlayTracker if it's more than 12 hours old, adding 1 hour if not
	if player:hasModData()
		and player:getModData().PlayTracker ~= nil then
	local n = #playerData.PlayTracker

	for i = n,1,-1 do
		if playerData.PlayTracker[i].hoursSince > 12 then
			table.remove(playerData.PlayTracker,i)
		else
			playerData.PlayTracker[i].hoursSince = playerData.PlayTracker[i].hoursSince + 1
		end
	end
	else
			player:getModData().PlayTracker = {}
	end
end


--By minute
local function updatePlayerTrackerByMinute()
	local player = getPlayer()
	local playerData = player:getModData()

	--HaloCooldownCounter
	-- we add this loop and update at each minute to reset the contents stored in HaloCooldownCounter if it's more than 5 minutes, adding 1 if not
	if player:hasModData()
		and player:getModData().HaloCooldownCounter ~= nil
		and tonumber(player:getModData().HaloCooldownCounter) ~= nil
	then
		if playerData.HaloCooldownCounter > 5 then
			playerData.HaloCooldownCounter = 0
		else
			playerData.HaloCooldownCounter = playerData.HaloCooldownCounter + 1
		end
	else
		player:getModData().HaloCooldownCounter = 0
	end
	--check if player has voice, needed in case something breaks midgame
	if (not player:getModData().PlayerVoice) or (not (tonumber(player:getModData().PlayerVoice))) then player:getModData().PlayerVoice = ZombRand(5); end
	--check if player has music tastes, needed in case something breaks midgame
	--PlayerMusicTaste
	if (not player:HasTrait("Deaf")) and (not player:HasTrait("ToneDeaf")) then
		local musicTraitPos, musicTraitNeg
		for i=1, #musicStyles do
			local musicTrait = musicStyles[i]
			if player:HasTrait(musicTrait) then
				musicTraitPos = musicTrait
				break
			end
		end
		for i=1, #musicStylesDislike do
			local musicTrait = musicStylesDislike[i]
			if player:HasTrait(musicTrait) then
				musicTraitNeg = musicTrait
				break
			end
		end
		if not musicTraitPos then
			local idxmusicStyles = ZombRand(#musicStyles) + 1
			local randomMusicStyle = musicStyles[idxmusicStyles]
			if musicTraitNeg and string.find(musicTraitNeg, randomMusicStyle) then
				for i=1, 20 do
					local newIdx = ZombRand(#musicStyles)+1
					randomMusicStyle = musicStyles[newIdx]
					if not string.find(musicTraitNeg, randomMusicStyle) then break; end
				end
			end
			player:getTraits():add(randomMusicStyle)
			player:getModData().PlayerMusicLike = randomMusicStyle
			HaloTextHelper.addTextWithArrow(player, getText("UI_trait_" .. randomMusicStyle), true, HaloTextHelper.getColorGreen())
			musicTraitPos = randomMusicStyle
		else
			player:getModData().PlayerMusicLike = musicTraitPos
		end
		if not musicTraitNeg then
			local idxmusicStyles = ZombRand(#musicStylesDislike) + 1
			local randomMusicStyle = musicStylesDislike[idxmusicStyles]
			if musicTraitPos and string.find(randomMusicStyle, musicTraitPos) then
				for i=1, 20 do
					local newIdx = ZombRand(#musicStylesDislike)+1
					randomMusicStyle = musicStylesDislike[newIdx]
					if not string.find(randomMusicStyle, musicTraitPos) then break; end
				end
			end
			player:getTraits():add(randomMusicStyle)
			player:getModData().PlayerMusicDislike = randomMusicStyle
			HaloTextHelper.addTextWithArrow(player, getText("UI_trait_" .. randomMusicStyle), true, HaloTextHelper.getColorRed())
		else
			player:getModData().PlayerMusicDislike = musicTraitNeg
		end	
	end
		
end

Events.OnNewGame.Add(initPlayerTracker)
Events.OnCreatePlayer.Add(LScheckPlayerTracker)
Events.EveryHours.Add(updatePlayerTrackerByHour)
Events.EveryOneMinute.Add(updatePlayerTrackerByMinute)