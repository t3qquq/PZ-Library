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

-- GENERAL EVENT CREATION
local DanceMusicOriginalVolume = tonumber(getSoundManager():getMusicVolume())
local oldVanillaMusicResume = 0
-- UPDATES EVERY TEN MINUTES:

local function getDancingDataPoints(playerData)	
	local points = 0
	local t = {
		ActiveDiscoBallNearby = 1,
		ActiveDiscoFloorNearby = 1,
		IsListeningToDJ = 2,
		IsListeningToJukebox = 1,
	}
	
	for k, v in pairs(t) do
		if playerData[k] then
			points = points+v
		end
	end
	return points
end

function LSEveryTenMinutes()

		-- GET THE PLAYER
	local player = getPlayer()
	local SkipPartyCalc = false
	local PartyCalc = false
	local hasEarProtection = false
	
	if DanceMusicOriginalVolume == 0 then
		DanceMusicOriginalVolume = tonumber(getSoundManager():getMusicVolume())
	end
		-- TO MAKE SURE NOTHING APPLIES WHILE PLAYER IS SLEEPING
	if player ~= nil then
		if player:isDead() then return; end
		local playerData = player:getModData()
		if not playerData then return; end
		if not playerData.LSMoodles then return; end
		if not player:isAsleep() then
		
		if not player:HasTrait("CouchPotato") and playerData.LSMoodles["AtHouse"].Value > 0 or playerData.LSMoodles["HomeSick"].Value > 0 then
			playerData.LSMoodles["AtHouse"].Value = 0
			playerData.LSMoodles["HomeSick"].Value = 0
			playerData.HomeSickCountUp = 1
		end
		
		
        -- CALL TO METHODS THAT USE EVERYTENMINUTES EVENT
		
			-- COUCH POTATOES WILL GET SLIGHTLY STRESSED WHEN OUTDOORS
			if player:HasTrait("CouchPotato") and player:isOutside() then
			
				if playerData.HomeSickCountUp == nil then
					playerData.HomeSickCountUp = 1
				end
			
				local HomeSickTotalCU = 6
			
				if playerData.HomeSickCountUp > HomeSickTotalCU then
		
					if playerData.LSMoodles["AtHouse"].Value == 0.2 then
						LSMoodleManager.setValue("AtHouse", 0.0)
						playerData.HomeSickCountUp = 1
					elseif playerData.LSMoodles["AtHouse"].Value == 0.0 and playerData.LSMoodles["HomeSick"].Value == 0.0 then
						playerData.HomeSickCountUp = 1
						LSMoodleManager.setValue("HomeSick", 0.2)
					elseif playerData.LSMoodles["HomeSick"].Value == 0.2 then
						playerData.HomeSickCountUp = 1
						LSMoodleManager.setValue("HomeSick", 0.4)
					elseif playerData.LSMoodles["HomeSick"].Value == 0.4 then
						playerData.HomeSickCountUp = 1
						LSMoodleManager.setValue("HomeSick", 0.6)
					elseif playerData.LSMoodles["HomeSick"].Value == 0.6 then
						playerData.HomeSickCountUp = 1
						LSMoodleManager.setValue("HomeSick", 0.8)
					end
				elseif playerData.LSMoodles["HomeSick"].Value == 0.8 then
					if playerData.HomeSickCountdown ~= nil and playerData.HomeSickCountdown > 1 then
						playerData.HomeSickCountdown = playerData.HomeSickCountdown - 1
					end
				elseif playerData.HomeSickCountUp > 0 then
					playerData.HomeSickCountUp = playerData.HomeSickCountUp + 1
					if playerData.HomeSickCountdown ~= nil and playerData.HomeSickCountdown > 1 then
						playerData.HomeSickCountdown = playerData.HomeSickCountdown - 1
					end
				end
			
			elseif player:HasTrait("CouchPotato") then

				local HomeSickTotalCD = 3
			
				if playerData.HomeSickCountdown == nil then
					playerData.HomeSickCountdown = 1
				end
			
				if playerData.HomeSickCountdown > HomeSickTotalCD then

					if playerData.LSMoodles["HomeSick"].Value == 0.8 then
						LSMoodleManager.setValue("HomeSick", 0.6)
						playerData.HomeSickCountdown = 1
					elseif playerData.LSMoodles["HomeSick"].Value == 0.6 then
						LSMoodleManager.setValue("HomeSick", 0.4)
						playerData.HomeSickCountdown = 1
					elseif playerData.LSMoodles["HomeSick"].Value == 0.4 then
						playerData.HomeSickCountdown = 1
						LSMoodleManager.setValue("HomeSick", 0.2)
					elseif playerData.LSMoodles["HomeSick"].Value == 0.2 then
						playerData.HomeSickCountdown = 1
						LSMoodleManager.setValue("HomeSick", 0.0)
					elseif playerData.LSMoodles["HomeSick"].Value == 0.0 and playerData.LSMoodles["AtHouse"].Value == 0.0 then
						playerData.HomeSickCountdown = 1
						LSMoodleManager.setValue("AtHouse", 0.2)
					end
				elseif playerData.LSMoodles["AtHouse"].Value == 0.2 then
					if playerData.HomeSickCountUp ~= nil and playerData.HomeSickCountUp > 1 then
						playerData.HomeSickCountUp = playerData.HomeSickCountUp - 1
					end
				elseif playerData.HomeSickCountdown > 0 then
					playerData.HomeSickCountdown = playerData.HomeSickCountdown + 1
					if playerData.HomeSickCountUp ~= nil and playerData.HomeSickCountUp > 1 then
						playerData.HomeSickCountUp = playerData.HomeSickCountUp - 1
					end
				end
			end		

			if playerData.LSMoodles and playerData.LSMoodles["HomeSick"].Value ~= 0 then
				local currentStress = player:getStats():getStress();
				local stressFactor = 0.04
				
				if playerData.LSMoodles["HomeSick"].Value == 0.8 then
					stressFactor = 0.12
				elseif playerData.LSMoodles["HomeSick"].Value == 0.6 then
					stressFactor = 0.08
				elseif playerData.LSMoodles["HomeSick"].Value == 0.4 then
					stressFactor = 0.06
				else
					stressFactor = 0.04
				end
				
				player:getStats():setStress(currentStress + stressFactor);
				--print("Current Stress:" .. player:getStats():getStress());
				if player:getStats():getStress() > 1 then
                player:getStats():setStress(1);
				end
			end
			
			-- COUCH POTATOES TAKE LONGER TO GET BORED WHEN INDOORS
			if playerData.LSMoodles and playerData.LSMoodles["AtHouse"].Value ~= 0 then
				local currentBoredom = player:getBodyDamage():getBoredomLevel();
				player:getBodyDamage():setBoredomLevel(currentBoredom - 0.6);
				--print("Current Boredom:" .. player:getBodyDamage():getBoredomLevel());
				if player:getBodyDamage():getBoredomLevel() < 0 then
				player:getBodyDamage():setBoredomLevel(0);
				end
			end
			
			-- COUCH POTATOES GET SLEEPY FASTER
			if player:HasTrait("CouchPotato") then
				local currentFatigue = player:getStats():getFatigue();
				player:getStats():setFatigue(currentFatigue + 0.002);
				--print("Current Tiredness:" .. player:getStats():getFatigue());
				if player:getStats():getFatigue() > 1 then
					player:getStats():setFatigue(1);
				end
			end
		
		--DJAUDIENCE AND PARTYSTUFF
		if (playerData.IsListeningToJukebox ~= nil) and
		((playerData.IsListeningToJukebox == true) or
		--(player:HasTrait("Killjoy") and (player:getPerkLevel(Perks.Dancing) < 4) and playerData.IsDancingFull == true) or
		(playerData.IsListeningToDJ == true) or
		(playerData.PlayingDJBooth == true)) then
		
			if player:HasTrait("Deaf") then
				SkipPartyCalc = true
			end

			local inventory = player:getInventory()	
			local it = inventory:getItems();

			for j = 0, it:size()-1 do
				local item = it:get(j);
				if item:getType() == "Hat_EarMuff_Protectors" or item:getType() == "Hat_EarMuffs" then
					if player:isEquippedClothing(item) then
						hasEarProtection = true
					end
				end
			end
		
		if SkipPartyCalc == false then
		--PeopleCheck - do only if first stage is true -- this will give us the number of players and the number of players dancing nearby
		local OtherPlayersAround = 0
		local OtherPlayersAroundDancing = playerData.OtherPlayersAroundDancing
		
		for playerIndex = 0, getNumActivePlayers()-1 do
			local playersList = {};--get players
			local playerObj = getSpecificPlayer(playerIndex)
				for x = playerObj:getX()-8,playerObj:getX()+8 do
					for y = playerObj:getY()-8,playerObj:getY()+8 do
						local square = getCell():getGridSquare(x,y,playerObj:getZ());
						if square then
							for i = 0,square:getMovingObjects():size()-1 do
								local moving = square:getMovingObjects():get(i);
								if instanceof(moving, "IsoPlayer") then
									table.insert(playersList, moving);
								end
							end
						end
					end
				end

            if #playersList > 0 then
                for i,v in ipairs(playersList) do
					if v:getUsername() ~= playerObj:getUsername() and
					v:isOutside() == playerObj:isOutside() then
						OtherPlayersAround = OtherPlayersAround + 1
						local DanceTargetID = v:getOnlineID()
						local DanceProposer = tostring(playerObj:getUsername())
						sendClientCommand(playerObj, "LS", "AskIfIsDancing", {DanceTargetID, DanceProposer})

					end
                end			
            end
		--end	
		end--loop
			
		if OtherPlayersAround < playerData.OtherPlayersAroundDancing then
			OtherPlayersAroundDancing = OtherPlayersAround
		end
		--now we have all the variables we need (disco, dj, jukebox, #people, #dancers, if player has killjoy or party animal, if dancing and dancing skill level, wether or not they're enjoying the music)
		local TotalPartyPoints = getDancingDataPoints(playerData) -- the more the better (with a few exceptions for killjoy) - from 1-13 (10 with 2 extra points for flexibility and 1 for party animals), 0 disables it, certain thresholds need to be achieved also
		-- For players with no relevant traits:
		-- 3 is first stage
		-- 7 is second stage
		-- 10 is last stage
		local POtherPlayers = 0 -- maxes at 2
		local POtherPlayersDancing = 0 -- maxes at 2
		local PDancing = 0 -- maxes at 3, only if is dancing and considers skill level, killjoy is inverted
		local PMusicMoodle = 0 -- maxes at 2 (starts at 3 for killjoy and is inverted)
		local PExtra = 0 -- maxes at 1, used to lower the threshold for party animals for second and third stage
		--maximum possible value = 12
		--examples of first stage combinations:
		-- 4 people and at least 2 of them dancing
		-- 4 people, an active discoball and song comes from jukebox
		-- 4 people and player is dancing
		-- A DJ is playing and the music is good
		-- 8 people and song comes from jukebox
		-- The character favorite genre is playing in the jukebox and a discoball is active
		--examples of second stage combinations: (requires a minimum of 3 other people)
		-- 4 people, at least 2 dancing, an active disco ball, a dj and the music is good
		-- 8 people, at least 2 dancing, Jukebox playing, player is dancing with a swkill of at least 4, music is good
		-- 4 people, DJ playing with a very high music skill, an active disco ball, player is dancing
		--examples of third stage combinations: (requires a minimum of 5 other people and at least 2 dancing)
		-- 8 people, at least 4 dancing, jukebox, is favorite genre, player is dancing with a skill of at least 8 
		-- 5 people, at least 2 dancing, DJ with very high skill, active discoball, player is dancing with a skill of 4 and is a party animal
		
		--Other Players Around



		if OtherPlayersAround >= 8 then
			POtherPlayers = 2
		elseif OtherPlayersAround >= 4 then
			POtherPlayers = 1
		end
		--Other Players Dancing
		if OtherPlayersAroundDancing >= 4 then
			POtherPlayersDancing = 2
		elseif OtherPlayersAroundDancing >= 2 then
			POtherPlayersDancing = 1
		end
		--Player is dancing
		if playerData.IsDancingFull == true then
			if player:getPerkLevel(Perks.Dancing) >= 8 and player:HasTrait("Killjoy") then
				PDancing = 0
			elseif player:getPerkLevel(Perks.Dancing) >= 8 then
				PDancing = 3
			elseif player:getPerkLevel(Perks.Dancing) >= 4 and player:HasTrait("Killjoy") then
				PDancing = 1
			elseif player:getPerkLevel(Perks.Dancing) >= 4 then
				PDancing = 2
			elseif player:HasTrait("Killjoy") then
				PDancing = 2
			else
				PDancing = 1
			end
		end
		--If is enjoying the music
		if playerData.LSMoodles["MusicGood"].Value == 0.4 and player:HasTrait("Killjoy") then
			PMusicMoodle = 0
		elseif playerData.LSMoodles["MusicGood"].Value == 0.4 then
			PMusicMoodle = 2
		elseif playerData.LSMoodles["MusicGood"].Value == 0.2 then
			PMusicMoodle = 1
		elseif playerData.LSMoodles["MusicBad"].Value == 0.2 and player:HasTrait("Killjoy") then
			PMusicMoodle = 2
		elseif playerData.LSMoodles["MusicBad"].Value == 0.4 and player:HasTrait("Killjoy") then
			PMusicMoodle = 3
		end
		--Extra
		if playerData.LSMoodles["PartyGood"].Value == 0.6 and player:HasTrait("PartyAnimal") then
			PExtra = 1
		elseif playerData.LSMoodles["PartyGood"].Value == 0.4 and player:HasTrait("PartyAnimal") then
			PExtra = 1
		elseif playerData.LSMoodles["PartyGood"].Value == 0.2 and player:HasTrait("PartyAnimal") then
			PExtra = 1
		end

		TotalPartyPoints = TotalPartyPoints+(POtherPlayers + POtherPlayersDancing + PDancing + PMusicMoodle + PExtra)
		
		if TotalPartyPoints > 0 and player:HasTrait("Killjoy") then
		
			if (playerData.LSMoodles["PartyBad"].Value == 0.6) and (TotalPartyPoints < 10) and (OtherPlayersAround < 5) then
				LSMoodleManager.setValue("PartyBad", 0.4)
			elseif (playerData.LSMoodles["PartyBad"].Value == 0.4) and (TotalPartyPoints >= 10) and (OtherPlayersAround >= 5) then
				LSMoodleManager.setValue("PartyBad", 0.6)
			elseif (playerData.LSMoodles["PartyBad"].Value == 0.4) and (TotalPartyPoints < 7) and (OtherPlayersAround < 3) then
				LSMoodleManager.setValue("PartyBad", 0.2)
			elseif (playerData.LSMoodles["PartyBad"].Value == 0.2) and (TotalPartyPoints >= 7) and (OtherPlayersAround >= 3) then
				LSMoodleManager.setValue("PartyBad", 0.4)
			elseif playerData.LSMoodles["PartyBad"].Value == 0.2 and TotalPartyPoints < 3 then
				LSMoodleManager.setValue("PartyBad", 0.0)
			elseif playerData.LSMoodles["PartyBad"].Value == 0.0 and TotalPartyPoints >= 3 then
				LSMoodleManager.setValue("PartyBad", 0.2)
			end
		PartyCalc = true
		TotalPartyPoints = 0
		elseif TotalPartyPoints > 0 then
		
			if (playerData.LSMoodles["PartyGood"].Value == 0.6) and (TotalPartyPoints < 10) and (OtherPlayersAround < 5) then
				LSMoodleManager.setValue("PartyGood", 0.4)
			elseif (playerData.LSMoodles["PartyGood"].Value == 0.4) and (TotalPartyPoints >= 10) and (OtherPlayersAround >= 5) then
				LSMoodleManager.setValue("PartyGood", 0.6)
			elseif (playerData.LSMoodles["PartyGood"].Value == 0.4) and (TotalPartyPoints < 7) and (OtherPlayersAround < 3) then
				LSMoodleManager.setValue("PartyGood", 0.2)
			elseif (playerData.LSMoodles["PartyGood"].Value == 0.2) and (TotalPartyPoints >= 7) and (OtherPlayersAround >= 3) then
				LSMoodleManager.setValue("PartyGood", 0.4)
			elseif playerData.LSMoodles["PartyGood"].Value == 0.2 and TotalPartyPoints < 3 then
				LSMoodleManager.setValue("PartyGood", 0.0)
			elseif playerData.LSMoodles["PartyGood"].Value == 0.0 and TotalPartyPoints >= 3 then
				LSMoodleManager.setValue("PartyGood", 0.2)
			end
			
			--DJAudienceStuff
			if not player:HasTrait("ToneDeaf") then
				if (playerData.LSMoodles["DJAudience"].Value == 0.6) and (OtherPlayersAround < 8) then
					LSMoodleManager.setValue("DJAudience", 0.4)
				elseif (playerData.LSMoodles["DJAudience"].Value == 0.4) and (OtherPlayersAround >= 8) and playerData.PlayingDJBooth == true then
					LSMoodleManager.setValue("DJAudience", 0.6)
				elseif (playerData.LSMoodles["DJAudience"].Value == 0.4) and (OtherPlayersAround < 5) then
					LSMoodleManager.setValue("DJAudience", 0.2)
				elseif (playerData.LSMoodles["DJAudience"].Value == 0.2) and (OtherPlayersAround >= 5) and playerData.PlayingDJBooth == true then
					LSMoodleManager.setValue("DJAudience", 0.4)
				elseif playerData.LSMoodles["DJAudience"].Value == 0.2 and OtherPlayersAround < 3 then
					LSMoodleManager.setValue("DJAudience", 0.0)
				elseif playerData.LSMoodles["DJAudience"].Value == 0.0 and OtherPlayersAround >= 3 and playerData.PlayingDJBooth == true then
					LSMoodleManager.setValue("DJAudience", 0.2)
				end
			end
		PartyCalc = true
		TotalPartyPoints = 0
		end--TotalPartyPoints
		
		end--SkipPartyCalc
	end--partystuff
		
		end --NotAsleep
		
		---IF PLAYER IS ASLEEP PARTYSTUFF
		--Static Variables
		if playerData.ActiveDiscoBallNearby == true then
		playerData.ActiveDiscoBallNearby = false
		end
		if playerData.OtherPlayersAroundDancing ~= 0 then
		playerData.OtherPlayersAroundDancing = 0
		end
		if ((SkipPartyCalc == true) or (player:isAsleep()) or (PartyCalc == false)) and
		((playerData.LSMoodles["PartyBad"].Value ~= 0.0) or (playerData.LSMoodles["PartyGood"].Value ~= 0.0) or (playerData.LSMoodles["DJAudience"].Value ~= 0.0)) then
		
		if player:HasTrait("Killjoy") then
		
			if playerData.LSMoodles["PartyBad"].Value == 0.6 then
				LSMoodleManager.setValue("PartyBad", 0.4)
			elseif playerData.LSMoodles["PartyBad"].Value == 0.4 then
				LSMoodleManager.setValue("PartyBad", 0.2)
			elseif playerData.LSMoodles["PartyBad"].Value == 0.2 then
				LSMoodleManager.setValue("PartyBad", 0.0)
			end
		
		else

			if playerData.LSMoodles["PartyGood"].Value == 0.6 then
				LSMoodleManager.setValue("PartyGood", 0.4)
			elseif playerData.LSMoodles["PartyGood"].Value == 0.4 then
				LSMoodleManager.setValue("PartyGood", 0.2)
			elseif playerData.LSMoodles["PartyGood"].Value == 0.2 then
				LSMoodleManager.setValue("PartyGood", 0.0)
			end
		
			if playerData.LSMoodles["DJAudience"].Value == 0.6 then
				LSMoodleManager.setValue("DJAudience", 0.4)
			elseif playerData.LSMoodles["DJAudience"].Value == 0.4 then
				LSMoodleManager.setValue("DJAudience", 0.2)
			elseif playerData.LSMoodles["DJAudience"].Value == 0.2 then
				LSMoodleManager.setValue("DJAudience", 0.0)
			end
		
		end
			SkipPartyCalc = false
			PartyCalc = false
		end
		
		--MUSICMOODLESTUFF
		if playerData.ListenedToMusic ~= nil and playerData.ListenedToMusic >= 0 then
		
			local MusicQuality = 0
		
			if player:HasTrait("Deaf") or player:isAsleep() then
				playerData.ListenedToMusic = -1
			elseif playerData.ListenedToMusic >= 6 and player:HasTrait("Virtuoso") then--VERYGOOD
				MusicQuality = 4
			elseif playerData.ListenedToMusic >= 2 and player:HasTrait("Virtuoso") then--GOOD
				MusicQuality = 3
			elseif player:HasTrait("Virtuoso") then--BAD
				MusicQuality = 2
			elseif playerData.ListenedToMusic >= 6 and player:HasTrait("ToneDeaf") then--BAD
				MusicQuality = 2
			elseif player:HasTrait("ToneDeaf") then--VERYBAD
				MusicQuality = 1
			elseif playerData.ListenedToMusic >= 8 then--VERYGOOD
				MusicQuality = 4
			elseif playerData.ListenedToMusic >= 2 then--GOOD
				MusicQuality = 3
			else--BAD
				MusicQuality = 2
			end
			
			if MusicQuality ~= 0 then
					
					if playerData.LSMoodles["MusicBad"].Value == 0.0 and playerData.LSMoodles["MusicGood"].Value == 0.4 and MusicQuality < 4 then
						LSMoodleManager.setValue("MusicGood", 0.2)
					elseif playerData.LSMoodles["MusicBad"].Value == 0.0 and playerData.LSMoodles["MusicGood"].Value == 0.2 and MusicQuality == 4 then
						LSMoodleManager.setValue("MusicGood", 0.4)
					elseif playerData.LSMoodles["MusicBad"].Value == 0.0 and playerData.LSMoodles["MusicGood"].Value == 0.2 and MusicQuality < 3 then
						LSMoodleManager.setValue("MusicGood", 0.0)
					elseif playerData.LSMoodles["MusicBad"].Value == 0.0 and playerData.LSMoodles["MusicGood"].Value == 0.0 and MusicQuality >= 3 then
						LSMoodleManager.setValue("MusicGood", 0.2)
					
					elseif playerData.LSMoodles["MusicGood"].Value == 0.0 and playerData.LSMoodles["MusicBad"].Value == 0.4 and MusicQuality > 1 then
						LSMoodleManager.setValue("MusicBad", 0.2)
					elseif playerData.LSMoodles["MusicGood"].Value == 0.0 and playerData.LSMoodles["MusicBad"].Value == 0.2 and MusicQuality == 1 then
						LSMoodleManager.setValue("MusicBad", 0.4)
					elseif playerData.LSMoodles["MusicGood"].Value == 0.0 and playerData.LSMoodles["MusicBad"].Value == 0.2 and MusicQuality > 2 then
						LSMoodleManager.setValue("MusicBad", 0.0)
					elseif playerData.LSMoodles["MusicGood"].Value == 0.0 and playerData.LSMoodles["MusicBad"].Value == 0.0 and MusicQuality <= 2 then
						LSMoodleManager.setValue("MusicBad", 0.2)
					end
			
			MusicQuality = 0
			end

			playerData.ListenedToMusic = -1
			
		elseif playerData.ListenedToMusic ~= nil and playerData.ListenedToMusic == -1 and ((playerData.LSMoodles["MusicGood"].Value ~= 0.0) or (playerData.LSMoodles["MusicBad"].Value ~= 0.0)) then
		
					if playerData.LSMoodles["MusicGood"].Value == 0.4 then
						LSMoodleManager.setValue("MusicGood", 0.2)
					elseif playerData.LSMoodles["MusicGood"].Value == 0.2 then
						LSMoodleManager.setValue("MusicGood", 0.0)
					elseif playerData.LSMoodles["MusicBad"].Value == 0.4 then
						LSMoodleManager.setValue("MusicBad", 0.2)
					elseif playerData.LSMoodles["MusicBad"].Value == 0.2 then
						LSMoodleManager.setValue("MusicBad", 0.0)
					end
		end--ListenedToMusic
		
	if playerData.VanillaMusicResume ~= 0 and playerData.VanillaMusicResume == oldVanillaMusicResume and playerData.PlayingInstrument == false and playerData.PlayingDJBooth == false and playerData.IsListeningToJukebox == false and playerData.PlayingDJBooth == false then
		getSoundManager():setMusicVolume(DanceMusicOriginalVolume)
		DanceMusicOriginalVolume = 0
		playerData.VanillaMusicResume = 0
	elseif playerData.VanillaMusicResume ~= oldVanillaMusicResume then
		oldVanillaMusicResume = playerData.VanillaMusicResume
	end
		
    end --Player
end

Events.EveryTenMinutes.Add(LSEveryTenMinutes);
