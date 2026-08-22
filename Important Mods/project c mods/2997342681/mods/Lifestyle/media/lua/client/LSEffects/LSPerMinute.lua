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

require "Helper/CheckPlayerHelper"
require "Painting/BeautyNeed"
require "Properties/Objects/beauty"

local function getbeautyRGB(quality)
	if quality == "IGUI_PaintingQuality_Good" then return "<PUSHRGB:0,128,0>"; elseif quality == "IGUI_PaintingQuality_Excellent" then return "<PUSHRGB:0,0,255>"; elseif quality == "IGUI_PaintingQuality_Impressive" then return "<PUSHRGB:128,0,128>"; elseif quality == "IGUI_PaintingQuality_Wondrous" then return "<PUSHRGB:255,215,0>"; elseif quality == "IGUI_PaintingQuality_Masterpiece" then return "<PUSHRGB:255,128,0>";
	elseif quality == "IGUI_PaintingQuality_Awful" then return "<PUSHRGB:128,0,0>"; elseif quality == "IGUI_PaintingQuality_Poor" then return "<PUSHRGB:139,69,19>"; elseif quality == "IGUI_PaintingQuality_Shoddy" then return "<PUSHRGB:105,105,105>"; end
	return "<PUSHRGB:255,255,255>"
end

local function getCorrectData(object)
	if object:getModData().movableData and object:getModData().movableData['artAuthor'] and object:getModData().movableData['artBeauty'] then return object:getModData().movableData;
	elseif object:getModData().modData and object:getModData().modData.movableData and object:getModData().modData.movableData['artAuthor'] and object:getModData().modData.movableData['artBeauty'] then return object:getModData().modData.movableData; end
	return false
end

local function RefreshItemToolTip(thisPlayer)

	local containerList = ArrayList.new();
	local playerNum = thisPlayer and thisPlayer:getPlayerNum() or -1
	if not playerNum then return; end
    for i,v in ipairs(getPlayerInventory(playerNum).inventoryPane.inventoryPage.backpacks) do
		containerList:add(v.inventory);
    end
    for i,v in ipairs(getPlayerLoot(playerNum).inventoryPane.inventoryPage.backpacks) do
		containerList:add(v.inventory);
    end

--	if #containerList > 0 then
--		for i,v in ipairs(containerList:getItems()) do
	for i=0,containerList:size()-1 do
		local container = containerList:get(i);
		for x=0,container:getItems():size() - 1 do
			local v = container:getItems():get(x);
			if (v:getFullType() == 'Lifestyle.SheetMusicBook') then
				if v:getModData() and v:getModData().InscribedSongs then
					local tooltipText = getText("Tooltip_SheetBook_ItemHeaderStart") .. #v:getModData().InscribedSongs .. getText("Tooltip_SheetBook_ItemHeaderEnd")
					v:setTooltip(tooltipText)
				end
			end
			if instanceof(v, "InventoryItem") and v:hasModData() then
				local data = getCorrectData(v)
				if data then
				--if v:getModData().movableData['artName'] then v:setName(tostring(v:getModData().movableData['artName'])); v:setCustomName(true); end
					v:setDisplayCategory("Art")
					local separator = " / "
					local artName = data['artName'] or "None"
					--if data['artName'] then artName = data['artName']; end
					if data['artSize'] and (data['artSize'] == "large") then separator = " (x2) / "; end
					if not data['artQuality'] then data['artQuality'] = "IGUI_PaintingQuality_Normal"; end
					v:setTooltip(getText("IGUI_PaintingCustomName")..": "..artName.." / "..getText("IGUI_PaintingBeauty")..": "..data['artBeauty'].." "..getText(data['artQuality'])..separator..getText("IGUI_PaintingStyle")..": "..getText("IGUI_PaintingStyle"..data['artStyle']).." / "..getText("IGUI_PaintingAuthor")..": "..data['artAuthor'])
				end
			end
		end
	end
end

local function getPlayerBedQuality(thisPlayer)
	local bedQuality, bed = "floor", false
	if thisPlayer:getVehicle() then return "averageBed"; end
	for x = thisPlayer:getX()-1,thisPlayer:getX()+1 do
		for y = thisPlayer:getY()-1,thisPlayer:getY()+1 do
			local square = getCell():getGridSquare(x,y,thisPlayer:getZ())
			if square and (thisPlayer:getSquare():isOutside() == square:isOutside()) and (square:getRoom() == thisPlayer:getSquare():getRoom()) then
				for i = 0,square:getObjects():size()-1 do------------------------------------
					local object = square:getObjects():get(i)
					if instanceof(object, "IsoObject") and object:getSprite() and object:getSprite():getProperties() and object:getSprite():getProperties():Is(IsoFlagType.bed) then
						bed = object
						bedQuality = object:getSprite():getProperties():Val("BedType")
						break
					end
				end
			end
		if bed then break; end
		end
	if bed then break; end
	end
	--print("LSPERMINUTE: getPlayerBedQuality - bedQuality is "..bedQuality)
	return bedQuality
end

local function getBedQualityTypes()
	return {
		{name="Good",bad=0,comf=0.9},
		{name="goodBed",bad=0,comf=0.9},
		{name="Average",bad=0.2,comf=0.8},
		{name="averageBed",bad=0.2,comf=0.8},
		{name="Bad",bad=0.3,comf=0.5},
		{name="badBed",bad=0.3,comf=0.5},
	}
end

local function getSleepingVals(thisPlayer)
	local bedPenalty, comfVal = 0.4, 0
	if not thisPlayer:getModData().CurrentBedQuality then
		thisPlayer:getModData().CurrentBedQuality = getPlayerBedQuality(thisPlayer)
	end
	--getBedType() seems to be returning averageBed regardless of bed quality
	--getBed() seems to be returning nil
	--print("LSPERMINUTE: getSleepingVals - start")
	if thisPlayer:getModData().CurrentBedQuality and (thisPlayer:getModData().CurrentBedQuality ~= "floor") then
		--print("LSPERMINUTE: getSleepingVals - bedType is "..thisPlayer:getModData().CurrentBedQuality)
		for k, v in ipairs(getBedQualityTypes()) do
			if string.find(thisPlayer:getModData().CurrentBedQuality, v.name) then
				--print("LSPERMINUTE: getSleepingVals - getBedQualityType is "..v.name)
				bedPenalty = v.bad
				comfVal = v.comf
				break
			end
		end
	end
	return bedPenalty, comfVal
end

local function AdjustGeneralLSMoodles(thisPlayer, playerData, bodyDamage, stats, currentBoredom, currentUnhappiness, currentStress)

	local moodchange = false

	if playerData.LSMoodles["Embarrassed"].Value >= 0.4 then--EMBARRASSED
		stats:setStress(stats:getStress() + 0.03);
		bodyDamage:setUnhappynessLevel(bodyDamage:getUnhappynessLevel() + 1)
		playerData.LSMoodles["Embarrassed"].Value = playerData.LSMoodles["Embarrassed"].Value - 0.02
		moodchange = true
	elseif playerData.LSMoodles["Embarrassed"].Value ~= nil and playerData.LSMoodles["Embarrassed"].Value >= 0.2 then
		stats:setStress(stats:getStress() + 0.015);
		bodyDamage:setUnhappynessLevel(bodyDamage:getUnhappynessLevel() + 1)
		playerData.LSMoodles["Embarrassed"].Value = playerData.LSMoodles["Embarrassed"].Value - 0.01
		moodchange = true
	elseif playerData.LSMoodles["Embarrassed"].Value > 0 then
		playerData.LSMoodles["Embarrassed"].Value = playerData.LSMoodles["Embarrassed"].Value - 0.005
	end
	if playerData.LSMoodles["Embarrassed"].Value ~= nil and playerData.LSMoodles["Embarrassed"].Value < 0 then
		playerData.LSMoodles["Embarrassed"].Value = 0
	end
	if playerData.LSMoodles["Embarrassed"].Value ~= nil and playerData.LSMoodles["Embarrassed"].Value > 1 then--in case another function causes it to go over 1
		playerData.LSMoodles["Embarrassed"].Value = 1
	end

	if playerData.LSMoodles["PartyBad"].Value >= 0.6 then--PARTYBAD KILLJOY
		stats:setStress(stats:getStress() + 0.05);
		bodyDamage:setUnhappynessLevel(bodyDamage:getUnhappynessLevel() + 3)
		moodchange = true
	elseif playerData.LSMoodles["PartyBad"].Value >= 0.4 then
		stats:setStress(stats:getStress() + 0.03);
		bodyDamage:setUnhappynessLevel(bodyDamage:getUnhappynessLevel() + 1.5)
		moodchange = true
	elseif playerData.LSMoodles["PartyBad"].Value >= 0.2 then
		stats:setStress(stats:getStress() + 0.01);
		bodyDamage:setUnhappynessLevel(bodyDamage:getUnhappynessLevel() + 0.7)
		moodchange = true
	end

	if playerData.LSMoodles["MintFresh"].Value >= 0.2 then--MINTFRESH
		stats:setStress(stats:getStress() - 0.003);
		bodyDamage:setUnhappynessLevel(bodyDamage:getUnhappynessLevel() - 0.2)
		moodchange = true
	end

	if playerData.LSMoodles["BathHot"].Value >= 0.2 then--BATHHOT
		stats:setStress(stats:getStress() - 0.005);
		bodyDamage:setUnhappynessLevel(bodyDamage:getUnhappynessLevel() - 0.5)
		moodchange = true
	elseif playerData.LSMoodles["BathCold"].Value >= 0.2 then--BATHCOLD
		stats:setStress(stats:getStress() + 0.007);
		bodyDamage:setUnhappynessLevel(bodyDamage:getUnhappynessLevel() + 0.6)
		moodchange = true
	end

	-------------COMFORT

	local decreaseRate = 0.0025
	local increaseRate = 0.015
	local traitMultiplier = 1
	local traitMultiplierNeg = 1

	if thisPlayer:HasTrait("Disciplined") then
		decreaseRate = 0.0010
		increaseRate = 0.005
		traitMultiplierNeg = 0.5
	elseif thisPlayer:HasTrait("CouchPotato") then
		decreaseRate = 0.005
		increaseRate = 0.02
		traitMultiplier = 1.5
		traitMultiplierNeg = 2
	end

	if thisPlayer:isAsleep() then
		traitMultiplierNeg = 0.5
	end

	if playerData.LSMoodles["Comfort"].Value == 0 then--we do this so that comfort decreases faster while comfortable and slower while not
		decreaseRate = decreaseRate * 0.2
	end

	if not playerData.ComfortNeed then playerData.ComfortNeed = 0.5; end
	if thisPlayer:isGodMod() then playerData.ComfortNeed = 0.5; end	
	if not playerData.ComfortVal then playerData.ComfortVal = 0.35; end
	if not playerData.IsOnBed then playerData.IsOnBed = false; end
	
	local comfortMultiplierSO = SandboxVars.LSComfort.ComfortNeedMultiplier or 1
	if comfortMultiplierSO and ((comfortMultiplierSO > 0) or (playerData.ComfortNeed < 0.2)) then decreaseRate = decreaseRate*comfortMultiplierSO; end
	local comfortFalse = SandboxVars.LSComfort.ComfortPositive or false
	
	if not playerData.IsSittingOnSeat and not playerData.IsOnBed and not thisPlayer:isSitOnGround() and not thisPlayer:isDriving() and not thisPlayer:isSeatedInVehicle() and (not thisPlayer:isAsleep()) then
		playerData.ComfortVal = 0
		playerData.ComfortNeed = playerData.ComfortNeed - decreaseRate
		if playerData.ComfortNeed < 0 then
			playerData.ComfortNeed = 0
		end
		--thisPlayer:Say("comfort is ".. tonumber(playerData.ComfortNeed))--test only turn this off !!!
	end

	if not playerData.IsSittingOnSeat and (thisPlayer:isSitOnGround() or thisPlayer:isDriving() or thisPlayer:isSeatedInVehicle()) then playerData.ComfortVal = 0.25; end

	if (playerData.IsSittingOnSeat ~= nil and playerData.IsSittingOnSeat == true) or thisPlayer:isSitOnGround() or thisPlayer:isDriving() or thisPlayer:isSeatedInVehicle() or thisPlayer:isAsleep() then
		local sleepingPenalty = 0
		if thisPlayer:isAsleep() and (not playerData.IsOnBed) then
			sleepingPenalty, playerData.ComfortVal = getSleepingVals(thisPlayer)
		end
		if tonumber(playerData.ComfortNeed) > (tonumber(playerData.ComfortVal) - sleepingPenalty) then--comfort is higher than seat comfort
			playerData.ComfortNeed = playerData.ComfortNeed - decreaseRate
			--thisPlayer:Say("comfort is ".. tonumber(playerData.ComfortNeed))--test only turn this off !!!
		elseif tonumber(playerData.ComfortNeed) == (tonumber(playerData.ComfortVal) - sleepingPenalty) then--comfort is the same as seat comfort
			--thisPlayer:Say("comfort is stable")--test only turn this off !!!
		elseif tonumber(playerData.ComfortNeed) < (tonumber(playerData.ComfortVal) - sleepingPenalty) and tonumber(playerData.ComfortNeed) >= ((tonumber(playerData.ComfortVal) - sleepingPenalty) - increaseRate) then--comfort is about to overpass seat comfort
			playerData.ComfortNeed = (tonumber(playerData.ComfortVal) - sleepingPenalty)
			--thisPlayer:Say("comfort is stabilizing")--test only turn this off !!!
		elseif (tonumber(playerData.ComfortNeed)*2) < (tonumber(playerData.ComfortVal) - sleepingPenalty) and tonumber(playerData.ComfortNeed) < ((tonumber(playerData.ComfortVal) - sleepingPenalty) - (increaseRate*3)) then--comfort is way lower than seat comfort, steat is likely very comfortable or player is very uncomfortable
			playerData.ComfortNeed = playerData.ComfortNeed + increaseRate*3
		elseif (tonumber(playerData.ComfortNeed)*1.5) < (tonumber(playerData.ComfortVal) - sleepingPenalty) and tonumber(playerData.ComfortNeed) < ((tonumber(playerData.ComfortVal) - sleepingPenalty) - (increaseRate*2)) then--comfort is way lower than seat comfort
			playerData.ComfortNeed = playerData.ComfortNeed + increaseRate*2
		else--comfort is lower than seat comfort
			playerData.ComfortNeed = playerData.ComfortNeed + increaseRate
			--thisPlayer:Say("comfort is ".. tonumber(playerData.ComfortNeed))--test only turn this off !!!
		end
		if playerData.ComfortNeed < 0 then
			playerData.ComfortNeed = 0
		elseif playerData.ComfortNeed > 1 then
			playerData.ComfortNeed = 1
		end
	end

	if (playerData.ComfortNeed >= 0.6) and comfortFalse then playerData.ComfortNeed = 0.5; end

	if playerData.ComfortNeed >= 0.9 then--COMFORT
		playerData.LSMoodles["Comfort"].Value = 0.8
		bodyDamage:setUnhappynessLevel(bodyDamage:getUnhappynessLevel() - (1.6*traitMultiplier))
		stats:setStress(stats:getStress() - (0.03*traitMultiplier))
		moodchange = true
	elseif playerData.ComfortNeed >= 0.8 then
		playerData.LSMoodles["Comfort"].Value = 0.6
		bodyDamage:setUnhappynessLevel(bodyDamage:getUnhappynessLevel() - (1.0*traitMultiplier))
		stats:setStress(stats:getStress() - (0.02*traitMultiplier))
		moodchange = true
	elseif playerData.ComfortNeed >= 0.7 then
		playerData.LSMoodles["Comfort"].Value = 0.4
		bodyDamage:setUnhappynessLevel(bodyDamage:getUnhappynessLevel() - (0.7*traitMultiplier))
		stats:setStress(stats:getStress() - (0.02*traitMultiplier))
		moodchange = true
	elseif playerData.ComfortNeed >= 0.6 then
		playerData.LSMoodles["Comfort"].Value = 0.2
		bodyDamage:setUnhappynessLevel(bodyDamage:getUnhappynessLevel() - (0.4*traitMultiplier))
		stats:setStress(stats:getStress() - (0.01*traitMultiplier))
		moodchange = true
	elseif playerData.ComfortNeed > 0.1 then
		playerData.LSMoodles["Comfort"].Value = 0
		playerData.LSMoodles["Uncomfortable"].Value = 0
	elseif playerData.ComfortNeed <= 0.1 then
		playerData.LSMoodles["Comfort"].Value = 0
		playerData.LSMoodles["Uncomfortable"].Value = 0.2
		local stress = 0.0025
		local unhappiness = 0.15
		if stats:getStress() >= 0.6 then
			stress = 0.0005
		elseif stats:getStress() >= 0.3 then
			stress = 0.001
		end
		if bodyDamage:getUnhappynessLevel() >= 60 then
			unhappiness = 0.01
		elseif bodyDamage:getUnhappynessLevel() >= 30 then
			unhappiness = 0.05
		end
		bodyDamage:setUnhappynessLevel(bodyDamage:getUnhappynessLevel() + (unhappiness*traitMultiplierNeg))
		stats:setStress(stats:getStress() + (stress*traitMultiplierNeg))
		moodchange = true
	end

		
	if moodchange == true then
		if thisPlayer:HasTrait("Smoker") then
			stats:setStressFromCigarettes(0)
		end
		if bodyDamage:getBoredomLevel() < 0 then
			bodyDamage:setBoredomLevel(0)
		elseif bodyDamage:getBoredomLevel() > 100 then
			bodyDamage:setBoredomLevel(100)
		end
		if bodyDamage:getUnhappynessLevel() < 0 then
			bodyDamage:setUnhappynessLevel(0)
		elseif bodyDamage:getUnhappynessLevel() > 100 then
			bodyDamage:setUnhappynessLevel(100)
		end
		if stats:getStress() < 0 then
			stats:setStress(0)
		elseif stats:getStress() > 1 then
			stats:setStress(1)
		end
		
		moodchange = false
	end--moodchange

end

local changeAnimRollWait = 0

local function ChangeSitOrLieAnimation(thisPlayer, playerData, currentBoredom, currentUnhappiness, currentStress, currentExhaustion, currentFatigue)

	if thisPlayer:hasTimedActions() then return; end

	if not playerData.IsSittingOnSeat then playerData.IsSittingOnSeatSouth = false; return; end

	--local ListSitAnim = {"N","IsLegAbove","IsACLegAbove","IsLeanForward","IsACCrossLegForward","IsACCrossLegBehind","IsArmsCrossed"}
	--local ListSitAnimStressed = {}
	--local ListSitAnimBored = {}
	--local ListSitAnimDepressed = {}
	--local ListSitAnimEmbarrassed = {}
	--local idxSitAnim = ZombRand(7) + 1
	
	local changeAnimRoll = ZombRand(100) + 1
	
	if changeAnimRoll > 40 then
		if changeAnimRollWait >= 1 then
			changeAnimRollWait = changeAnimRollWait - 1
		else
		
			local SitAnimations = require("Properties/Anim/SitAnimations")
	
			local ListSitAnim = {}

				for k,v in pairs(SitAnimations) do
					if v.range == "full" and v.common == 1 then
						table.insert(ListSitAnim, v)
					end
					if v.range == "full" and v.common == 0 and v.bored == 1 and currentBoredom > 30 then
						table.insert(ListSitAnim, v)
					end
					if v.range == "full" and v.common == 0 and v.depressed == 1 and currentUnhappiness > 30 then
						table.insert(ListSitAnim, v)
					end
					if v.range == "full" and v.common == 0 and v.exhausted == 1 and currentExhaustion < 0.6 then
						table.insert(ListSitAnim, v)
					end
					if v.range == "full" and v.common == 0 and v.stressed == 1 and currentStress > 0.4 then
						table.insert(ListSitAnim, v)
					end
					if v.range == "full" and v.common == 0 and v.tired == 1 and currentFatigue > 0.4 then
						table.insert(ListSitAnim, v)
					end
					if v.range == "full" and v.common == 0 and v.embarrassed == 1 and playerData.LSMoodles["Embarrassed"].Value >= 0.2 then
						table.insert(ListSitAnim, v)
					end
				end

			local idxSitAnim = ZombRand(#ListSitAnim) + 1
			local sitAnim
			if playerData.IsSittingOnSeatSouth then
				sitAnim = ListSitAnim[idxSitAnim].animS
			else
				sitAnim = ListSitAnim[idxSitAnim].anim
			end
			
			thisPlayer:setVariable("SittingToggleLoop", sitAnim)
			if isClient() then
				ScanForPlayers("ChangeAnimVar", {"SittingToggleLoop", sitAnim})
				--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "SittingToggleLoop", sitAnim})
			end
			--if ListSitAnim[idxSitAnim].common == 0 then

			local emotion = "none"

			if ListSitAnim[idxSitAnim].bored == 1 and currentBoredom > 30 then
				emotion = "Bored"
			elseif ListSitAnim[idxSitAnim].depressed == 1 and currentUnhappiness > 30 then
				emotion = "Depressed"
			elseif ListSitAnim[idxSitAnim].embarrassed == 1 and playerData.LSMoodles["Embarrassed"].Value > 0.4 then
				emotion = "Embarrassed"
			elseif ListSitAnim[idxSitAnim].tired == 1 and currentFatigue > 0.4 then
				emotion = "Tired"
			end

			if emotion ~= "none" then

				local PlayerVoice = playerData.PlayerVoice
				local PlayerVoiceTracks = require("TimedActions/PlayerVoiceTracks")

				local AvailablePlayerVoiceTracks = {}

				-- we loop the voice tracks and select the ones that we want, making sure to only select the ones that match the player voice
				for k,v in pairs(PlayerVoiceTracks) do
					if v.Voice == PlayerVoice and
					v.Type == emotion then
						table.insert(AvailablePlayerVoiceTracks, v)
					end
				end			

				local randomLine = ZombRand(#AvailablePlayerVoiceTracks) + 1
				local randomTrack = AvailablePlayerVoiceTracks[randomLine]
				local voiceSound = randomTrack.sound
				local voiceSoundF = randomTrack.soundF

				local dice20 = ZombRand(20) + 1
				if dice20 >= 6 then
					if thisPlayer:getDescriptor():isFemale() then
						thisPlayer:getEmitter():playSound(voiceSoundF);
					else
						thisPlayer:getEmitter():playSound(voiceSound);
					end
				end
			end
			--end
			changeAnimRollWait = 3
		end
	end

end

local function PlayerCreateDirt(thisPlayer, playerData, bodyDamage, currentWetness)

	if thisPlayer and not thisPlayer:isPlayerMoving() and
	((not thisPlayer:hasTimedActions()) or (thisPlayer:isReading())) then
	return; end

	local hasDirt
	local hasBlood
	local totalDirt = 0
	local totalBlood = 0
	local visual = thisPlayer:getHumanVisual()
	for i = 1, BloodBodyPartType.MAX:index() do
        local part = BloodBodyPartType.FromIndex(i - 1)
        local Blood = visual:getBlood(part)
        local Dirt = visual:getDirt(part)

		if Blood > 0 then
			--hasBlood = true
			totalBlood = totalBlood + Blood
		end
		if Dirt > 0 then
			--hasDirt = true
			totalDirt = totalDirt + Dirt
		end
    end

	--float goes from 0 to 17 (each body part can add up to 1)
	if totalBlood > 5 then--blood gets priority over dirt
		hasBlood = true
	elseif totalDirt > 3 then--dirt has a lower threshold than blood as it's easier to clean and won't consume resources besides water/soap
		hasDirt = true
	end
			
	local MainLitterList = require("Properties/LitterTypes")
	local thisCategory = "grime"

	local dirtChance = 2
	local dirtSprite = "overlay_grime_floor_01_15"--backup option
	local dirtSolid = 2---1 is solid, 2 is overlay

	if hasBlood then
		dirtChance = 4
		thisCategory = "blood"
	elseif hasDirt then
		dirtChance = 4
	end

	if thisPlayer:isDriving() then
		dirtChance = 30
		thisCategory = "grime"
	elseif thisPlayer:isSprinting() then
		dirtChance = dirtChance + 4

	elseif thisPlayer:isRunning() then
		dirtChance = dirtChance + 2

	end

	if thisPlayer:HasTrait("Sloppy") then
		dirtChance = dirtChance + 2
	elseif thisPlayer:HasTrait("Tidy") then
		dirtChance = dirtChance - 2
	end

	if (thisPlayer:isReading() or (playerData.PlayingInstrument and thisPlayer:isSitOnGround()) or playerData.IsMeditating or playerData.PlayingDJBooth) and not thisPlayer:isDriving() then
		dirtChance = 0
	end

	local dirtRoll = ZombRand(180) + 1

	if dirtRoll <= dirtChance then

		local LitterList = {}

		for k,v in pairs(MainLitterList) do
			if v.category == thisCategory then
				table.insert(LitterList, v)
			end
		end

		local randomNumber = ZombRand(#LitterList) + 1
		local randomSprite = LitterList[randomNumber]
		dirtSprite = randomSprite.name

		local x = thisPlayer:getX()
		local y = thisPlayer:getY()
		local z = thisPlayer:getZ()
		if isClient() then
			sendClientCommand("LS", "DebugAddLitter", {x, y, z, dirtSolid, dirtSprite})
		else
			LSAddLitter(x, y, z, dirtSolid, dirtSprite)
		end
	end
end
--[[
local function AdjustDirtLSMoodle(thisPlayer, playerData, bodyDamage, stats, currentBoredom, currentUnhappiness, currentStress, howDirty)

	local moodchange = false

	-------------

	local decreaseRate = 0.012
	local increaseRate = 0.025
	local traitMultiplier = 1
	local traitMultiplierNeg = 1

	if thisPlayer:HasTrait("Sloppy") then
		decreaseRate = 0.005
		increaseRate = 0.010
		traitMultiplierNeg = 0.5
	elseif thisPlayer:HasTrait("CleanFreak") then
		decreaseRate = 0.025
		increaseRate = 0.025
		traitMultiplier = 1.5
		traitMultiplierNeg = 2
	end

	if not playerData.BeautyNeed then playerData.BeautyNeed = 0.6; end


	if (howDirty == 2 and playerData.BeautyNeed > 0) or
	((howDirty == 1 and playerData.BeautyNeed > 0.4) and playerData.BeautyNeed - decreaseRate >= 0.4) or
	(howDirty == 0 and thisPlayer:isOutside() and playerData.BeautyNeed > 0.6) then
		playerData.BeautyNeed = playerData.BeautyNeed - decreaseRate

	elseif howDirty == 1 and playerData.BeautyNeed > 0.4 then
		playerData.BeautyNeed = 0.4
	elseif howDirty == 0 and playerData.BeautyNeed >= 1 then--beauty need fulfilled

	elseif ((howDirty == 1 and playerData.BeautyNeed < 0.4) and playerData.BeautyNeed + increaseRate <= 0.4) or
	(howDirty == 0 and playerData.BeautyNeed < 1) then
		playerData.BeautyNeed = playerData.BeautyNeed + increaseRate
	elseif (howDirty == 1 and playerData.BeautyNeed < 0.4) then
		playerData.BeautyNeed = 0.4
	end

	if playerData.BeautyNeed > 0.4 then
		playerData.LSMoodles["BeautyNeg"].Value = 0
	elseif playerData.BeautyNeed > 0.1 and playerData.BeautyNeed <= 0.4 then
		playerData.LSMoodles["BeautyNeg"].Value = 0.2
		local stress = 0.0025
		local unhappiness = 0.15
		if currentStress >= 0.6 then
			stress = 0.0005
		elseif currentStress >= 0.3 then
			stress = 0.001
		end
		if currentUnhappiness >= 60 then
			unhappiness = 0.01
		elseif currentUnhappiness >= 30 then
			unhappiness = 0.05
		end
		bodyDamage:setUnhappynessLevel(currentUnhappiness + (unhappiness*traitMultiplierNeg))
		stats:setStress(currentStress + (stress*traitMultiplierNeg))
		moodchange = true
	elseif playerData.BeautyNeed <= 0.1 then
		playerData.LSMoodles["BeautyNeg"].Value = 0.4
		local stress = 0.005
		local unhappiness = 0.3
		if currentStress >= 0.6 then
			stress = 0.001
		elseif currentStress >= 0.3 then
			stress = 0.002
		end
		if currentUnhappiness >= 60 then
			unhappiness = 0.05
		elseif currentUnhappiness >= 30 then
			unhappiness = 0.1
		end
		bodyDamage:setUnhappynessLevel(currentUnhappiness + (unhappiness*traitMultiplierNeg))
		stats:setStress(currentStress + (stress*traitMultiplierNeg))
		moodchange = true
	end

		
	if moodchange == true then
		if thisPlayer:HasTrait("Smoker") then
			stats:setStressFromCigarettes(0)
		end
		if bodyDamage:getBoredomLevel() < 0 then
			bodyDamage:setBoredomLevel(0)
		elseif bodyDamage:getBoredomLevel() > 100 then
			bodyDamage:setBoredomLevel(100)
		end
		if bodyDamage:getUnhappynessLevel() < 0 then
			bodyDamage:setUnhappynessLevel(0)
		elseif bodyDamage:getUnhappynessLevel() > 100 then
			bodyDamage:setUnhappynessLevel(100)
		end
		if stats:getStress() < 0 then
			stats:setStress(0)
		elseif stats:getStress() > 1 then
			stats:setStress(1)
		end
		
		moodchange = false
	end--moodchange

	if playerData.BeautyNeed > 1 then
		playerData.BeautyNeed = 1
	elseif playerData.BeautyNeed < 0 then
		playerData.BeautyNeed = 0
	end

end
]]--

local function doArtValueCalc(thisPlayer, artVal, beautyValue)
	if artVal == 0 or not SandboxVars.Text.DividerArt then return beautyValue; end
	local multiplier = SandboxVars.LSArt.ArtworkBeautyMultiplier or 1 -- sandbox option
	if thisPlayer:HasTrait("HatesArt") then return beautyValue-((artVal/4)*multiplier); 
	elseif thisPlayer:HasTrait("Artistic") then return beautyValue+((artVal*1.5)*multiplier); end
	return beautyValue+(artVal*multiplier)
end

local function doTrashValueCalc(thisPlayer, val)
	if val == 0 or not SandboxVars.Text.DividerHygiene then return val; end
	if thisPlayer:HasTrait("Sloppy") then val = val*0.5; elseif thisPlayer:HasTrait("CleanFreak") then val = val*3; end
	return val
end

local function doFlyBuzzSound(thisPlayer, playerData, val)
	if thisPlayer:isOutside() or val < 500 or ZombRand(20) ~= 1 then return; end
	playerData.doFliesSound = playerData.doFliesSound or 0
	if playerData.doFliesSound > 0 then playerData.doFliesSound = playerData.doFliesSound-1; return; end
	getSoundManager():playUISound("UI_FliesBuzz"); playerData.doFliesSound = 7
end

local function getArtValue(Art, baseValue)
	if (not Art) or (not instanceof(Art, "IsoObject")) or (not Art:hasModData()) or (not Art:getModData().movableData) or (not Art:getModData().movableData['artBeauty']) then return baseValue; end
	--[[
	local facing = getFacing(Art)
	local attachedObj = getFullArt(Art, facing)
	local beautyVal = Art:getModData().movableData['artBeauty']
	if beautyVal ~= 0 and attachedObj then beautyVal = beautyVal/2; end
	return beautyVal
	]]--
	return Art:getModData().movableData['artBeauty']
end


local function getBeautyValue(square, beautyValue, trashValue, artValue)
	for i = 0,square:getObjects():size()-1 do
		local object = square:getObjects():get(i)
		if object then
			local attachedsprite, objName, overlayName = object:getAttachedAnimSprite(), object:getTextureName(), false		
			if object:getOverlaySprite() then overlayName = object:getOverlaySprite():getName(); end
			if attachedsprite then
				for n=1,attachedsprite:size() do
					local sprite = attachedsprite:get(n-1)
					if sprite and sprite:getParentSprite() and sprite:getParentSprite():getName() then
						local newValue, isTrash, isArt = getBeautyProperty(sprite:getParentSprite():getName(), true)
						if isTrash then trashValue = trashValue+newValue;
						elseif not isArt then beautyValue = beautyValue+newValue; end
					end
				end
			end
			if objName then
				local newValue, isTrash, isArt = getBeautyProperty(objName, false)
				if isTrash then trashValue = trashValue+newValue;
				elseif isArt then artValue = artValue+getArtValue(object, newValue); 
				else beautyValue = beautyValue+newValue; end
			end
			if overlayName then
				local newValue, isTrash, isArt = getBeautyProperty(overlayName, true)
				if isTrash then trashValue = trashValue+newValue;
				elseif not isArt then beautyValue = beautyValue+newValue; end
			end
		end
	end
	return trashValue, beautyValue, artValue
end

local function PlayerDoBeautyCheck(thisPlayer, playerData, bodyDamage, stats)

	--local howDirty = 0-----0 for not dirty; 1 for dirty; 2 for very dirty -- old system
	local beautyValue, trashValue, artValue = 0, 0, 0 -- goes from 0 to +200 (higher values change beauty need faster)  -- calc for trash, add to beautyValue later / calc for art - separate for trait stuff
	local sourceSquare = getCell():getGridSquare(thisPlayer:getX(),thisPlayer:getY(),thisPlayer:getZ())
	if not sourceSquare then return; end
	for x = thisPlayer:getX()-8,thisPlayer:getX()+8 do
		for y = thisPlayer:getY()-8,thisPlayer:getY()+8 do
			local square = getCell():getGridSquare(x,y,thisPlayer:getZ())
			if square and square:IsOnScreen() and (sourceSquare:isOutside() == square:isOutside()) and square:getRoom() == sourceSquare:getRoom() then
				if square:haveBlood() and (thisPlayer:isOutside() == square:isOutside()) then trashValue = trashValue-20; end
				if square:getDeadBody() and (thisPlayer:isOutside() == square:isOutside()) then trashValue = trashValue-80; end
				trashValue, beautyValue, artValue = getBeautyValue(square, beautyValue, trashValue, artValue)
				
				--[[for i = 0,square:getObjects():size()-1 do------------------------------------
					local object = square:getObjects():get(i)
					if object and (thisPlayer:isOutside() == object:getSquare():isOutside()) then
						local attachedsprite, objName = object:getAttachedAnimSprite(), false								
						if object:getTextureName() then objName = object:getTextureName();
						elseif object:getOverlaySprite() and object:getOverlaySprite():getName() then objName = object:getOverlaySprite():getName();
						elseif attachedsprite then
							for n=1,attachedsprite:size() do
								local sprite = attachedsprite:get(n-1)
								if sprite and sprite:getParentSprite() and sprite:getParentSprite():getName() then objName = sprite:getParentSprite():getName(); end
							end
						end
						if objName then
							local newValue, isTrash, isArt = getBeautyProperty(objName)
							if isTrash then trashValue = trashValue+newValue;
							elseif isArt then artValue = artValue+getArtValue(object); 
							else beautyValue = beautyValue+newValue; end
						end
					end
				end]]--
			end
		end
	end
	trashValue = doTrashValueCalc(thisPlayer, trashValue)
	doFlyBuzzSound(thisPlayer, playerData, trashValue)
	beautyValue = doArtValueCalc(thisPlayer, artValue, beautyValue)
	beautyValue = math.ceil(beautyValue+trashValue)
	adjustBeautyNeed(thisPlayer, playerData, beautyValue, trashValue, artValue, bodyDamage, stats)
	--AdjustDirtLSMoodle(thisPlayer, playerData, bodyDamage, stats, currentBoredom, currentUnhappiness, currentStress, howDirty)
	
end

function LSEveryMinute()
	for i = 0, getNumActivePlayers() - 1 do
		local thisPlayer = getSpecificPlayer(i)
	
	if thisPlayer and thisPlayer:hasModData() and (not thisPlayer:isDead()) then
		local playerData = thisPlayer:getModData()

		local bodyDamage = thisPlayer:getBodyDamage()
		local stats = thisPlayer:getStats()
		local currentBoredom = bodyDamage:getBoredomLevel()
		local currentUnhappiness = bodyDamage:getUnhappynessLevel()
		local currentStress = stats:getStress()
		local currentExhaustion = stats:getEndurance()
		local currentFatigue = stats:getFatigue()
		local currentWetness = bodyDamage:getWetness()
		local currentHunger = stats:getHunger()
		local currentThirst = stats:getThirst()

		---functions that play regardless of player asleep state
		AdjustGeneralLSMoodles(thisPlayer, playerData, bodyDamage, stats, currentBoredom, currentUnhappiness, currentStress)
		if SandboxVars.Text.DividerHygiene then--HYGIENE
			AdjustBladderNeed(thisPlayer, playerData, bodyDamage, stats, currentBoredom, currentUnhappiness, currentStress, currentHunger, currentThirst)
			AdjustHygieneNeed(thisPlayer, playerData, bodyDamage, stats, currentBoredom, currentUnhappiness, currentStress)
		end
		if (SandboxVars.Text.DividerMeditationNew) and ((playerData.LSMoodles["MindfulState"].Value > 0) or (playerData.MindfulnessMinutes and (playerData.MindfulnessMinutes > 0))) then--MEDITATION MINDFULNESS
			AdjustPlayerMindfulness(thisPlayer)
		end
		---

		if not thisPlayer:isAsleep() then--when player is asleep some functions won't play
			--AdjustGeneralLSMoodles(thisPlayer, playerData, bodyDamage, stats, currentBoredom, currentUnhappiness, currentStress)
			ChangeSitOrLieAnimation(thisPlayer, playerData, currentBoredom, currentUnhappiness, currentStress, currentExhaustion, currentFatigue)
			if SandboxVars.Text.DividerMusicNew then
				RefreshItemToolTip(thisPlayer)
			end
			if (thisPlayer:isMoving() or thisPlayer:isRunning() or thisPlayer:isSneaking() or thisPlayer:isSprinting()) and not
			thisPlayer:isOutside() and not thisPlayer:isInvisible() then
				if SandboxVars.Text.DividerHygiene then
					PlayerCreateDirt(thisPlayer, playerData, bodyDamage, currentWetness)
				end
			end
			if SandboxVars.Text.DividerHygiene or SandboxVars.Text.DividerArt then
				PlayerDoBeautyCheck(thisPlayer, playerData, bodyDamage, stats)
			elseif (playerData.LSMoodles["BeautyGood"] and playerData.LSMoodles["BeautyGood"].Value > 0) or (playerData.LSMoodles["BeautyNeg"] and playerData.LSMoodles["BeautyNeg"].Value > 0) then
				playerData.BeautyNeed = 50; playerData.LSMoodles["BeautyGood"].Value = 0; playerData.LSMoodles["BeautyNeg"].Value = 0
			end
		end
	end
	end
end

Events.EveryOneMinute.Add(LSEveryMinute);
