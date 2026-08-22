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

local function IsListeningCheckConditions(listener)
	if not listener then return false; end
	if listener:HasTrait("Deaf") or listener:isDead() then return false; end
	if not listener:hasModData() then return false; end
	if listener:getModData().PlayingInstrument or listener:getModData().PlayingDJBooth then return false; end
	if listener:getVehicle() then return false; end
	return true
end

local function IsListeningGetHeadgearList()
	return {"Hat_EarMuff_Protectors","Hat_EarMuffs","Hat_CrashHelmetFULL","Hat_EarMuff_Protectors_AZ","Authentic_Headphones","Authentic_Headphones2","Authentic_Headphones3","Authentic_Headphones4"}
end

local function IsListeningCheckEarProtection(listener)
	local HasEarProtection
	local it = listener:getInventory():getItems()
	local HeadGearList = IsListeningGetHeadgearList()
	for j = 0, it:size()-1 do
		local item = it:get(j)
		for n=0, (#HeadGearList)-1 do
			if item:getType() and (item:getType() == HeadGearList[n]) and listener:isEquippedClothing(item) then
				HasEarProtection = 0.1
				break
			end
		end
		if HasEarProtection then break; end
	end
	if not HasEarProtection then HasEarProtection = 1; end
	return HasEarProtection
end

local function IsListeningIsJukebox(listener)
	return listener:getModData().IsListeningToJukebox
end

local function IsListeningGetTM(listener)
	local tm = 1
	if listener:HasTrait("Virtuoso") then tm = 2;
	elseif listener:HasTrait("KeenHearing") then tm = 1.5;
	elseif listener:HasTrait("HardOfHearing") then tm = 0.5;
	end
	return tm
end

local function IsListeningGetSandboxVars()
	local StrengthMultiplier = 1
	local sandboxMusicStrengthMultiplier = SandboxVars.Music.ListeningStrengthMultiplier or 2
	if sandboxMusicStrengthMultiplier then
		if sandboxMusicStrengthMultiplier == 1 then
			StrengthMultiplier = 0.5
		elseif sandboxMusicStrengthMultiplier == 2 then
			StrengthMultiplier = 1
		elseif sandboxMusicStrengthMultiplier == 3 then
			StrengthMultiplier = 2
		elseif sandboxMusicStrengthMultiplier == 4 then
			StrengthMultiplier = 4
		end
	end
	return StrengthMultiplier
end

local function IsListeningFixMood(listener)
	-- adjust stats if levels went below
	if (listener:getBodyDamage():getBoredomLevel() < 0) then
		listener:getBodyDamage():setBoredomLevel(0)
	end
	if (listener:getBodyDamage():getUnhappynessLevel() < 0) then
		listener:getBodyDamage():setUnhappynessLevel(0)
	end
	if (listener:getStats():getStress() < 0) then
		listener:getStats():setStress(0)
	end

	-- adjust stats if levels went above
	if (listener:getBodyDamage():getBoredomLevel() > 100) then
		listener:getBodyDamage():setBoredomLevel(100)
	end
	if (listener:getBodyDamage():getUnhappynessLevel() > 100) then
		listener:getBodyDamage():setUnhappynessLevel(100)
	end
	if (listener:getStats():getStress() > 1) then
		listener:getStats():setStress(1)
	end
	if listener:HasTrait("Smoker") then listener:getStats():setStressFromCigarettes(0); end
end

local function IsListeningSetMood(listener, HasEarProtection, SongQuality)
	local initialBoredom, initialUnhappiness, initialStress = listener:getBodyDamage():getBoredomLevel(), listener:getBodyDamage():getUnhappynessLevel(), listener:getStats():getStress()
	local sandboxMusic = IsListeningGetSandboxVars()
	if not listener:HasTrait("ToneDeaf") then
		local traitMultiplier = IsListeningGetTM(listener)
		listener:getStats():setStress(initialStress - (0.01 * HasEarProtection * SongQuality * traitMultiplier * sandboxMusic))
		listener:getBodyDamage():setBoredomLevel(initialBoredom - (1 * HasEarProtection * SongQuality * traitMultiplier * sandboxMusic))
		listener:getBodyDamage():setUnhappynessLevel(initialUnhappiness - (0.5 * HasEarProtection * SongQuality * traitMultiplier * sandboxMusic))
	else
		listener:getStats():setStress(initialStress + (0.01 * HasEarProtection)/(SongQuality*sandboxMusic))
		listener:getBodyDamage():setBoredomLevel(initialBoredom + (1 * HasEarProtection)/(SongQuality*sandboxMusic))
		listener:getBodyDamage():setUnhappynessLevel(initialUnhappiness + (0.5 * HasEarProtection)/(SongQuality*sandboxMusic))
	end
	IsListeningFixMood(listener)
end

local function IsListeningCheckAsleepOrMeditating(character)
	local initialStress = character:getStats():getStress()
	if character:isAsleep() then
		character:setAsleep(false); character:setAsleepTime(0.0); UIManager.FadeIn(character:getPlayerNum(), 1)
		HaloTextHelper.addText(character, getText("Disturbed Sleep"), HaloTextHelper.getColorRed())
		ISTimedActionQueue.add(ToneDeafSuffering:new(character))
		character:getModData().TDcomplained = true
		character:getStats():setStress(initialStress + 0.4)
		IsListeningFixMood(character)
		return true
	elseif character:getModData().IsMeditating and (not character:HasTrait("Disciplined")) then
		character:getModData().TDcomplained = true
		character:getStats():setStress(initialStress + 0.2)
		HaloTextHelper.addText(character, getText("Disturbed Focus"), HaloTextHelper.getColorRed())
		ISTimedActionQueue.add(ToneDeafSuffering:new(character))
		character:getModData().IsMeditationDisturbed = true
		IsListeningFixMood(character)
		return true
	end
	return false
end

local function IsListeningToBadMusic(listener, SourceMusiclvl)
	if listener:HasTrait("Virtuoso") then return false; end
	if (tonumber(SourceMusiclvl) < 2) then return true; end
	if listener:HasTrait("ToneDeaf") and (tonumber(SourceMusiclvl) < 8) then return true; end
	return false
end

local function IsListeningToGoodMusic(listener, SourceMusiclvl)
	if listener:HasTrait("ToneDeaf") then return false; end
	if (tonumber(SourceMusiclvl) > 5) then return true; end
	if listener:HasTrait("Virtuoso") and (tonumber(SourceMusiclvl) > 3) then return true; end
	return false
end

local function IsListeningCanDoAction(listener)
	if (not listener:getPrimaryHandItem()) and (not listener:getSecondaryHandItem()) and
	(listener:getCurrentState():equals(IdleState.instance()) or listener:isSitOnGround()) and
	(not listener:isSneaking()) and (not listener:isRunning()) and (not listener:isAiming()) and
	(not listener:hasTimedActions()) and (not listener:isSprinting()) then
		return true
	end
	return false
end

local function IsListeningComplain(listener, Boochance, complained)
	if not complained then
		ISTimedActionQueue.add(ToneDeafSuffering:new(listener))
		listener:getModData().TDcomplained = true
	elseif (Boochance >= 75) then
		ISTimedActionQueue.add(BooingMusician:new(listener))
	end
end

local function IsListeningCheer(listener, Cheerchance, cheered)
	if not cheered then
		ISTimedActionQueue.add(PraiseMusician:new(listener))								
		listener:getModData().GaveApplause = true
	elseif (Cheerchance >= 75) then
		ISTimedActionQueue.add(PraiseMusician:new(listener))
	end
end

function PlayerIsListeningToDJ(DJlistener, SourceMusiclvl, SourceDJ, SourceIsDJ)
	if DJlistener and DJlistener:hasModData() and (not SourceIsDJ) then
		DJlistener:getModData().IsListeningToDJ = false
		DJlistener:getModData().SourceDJName = "nodj"
		return
	end

	if not IsListeningCheckConditions(DJlistener) then return; end

	DJlistener:getModData().SourceDJName = SourceDJ
	DJlistener:getModData().IsListeningToDJ = true

	if IsListeningCanDoAction(DJlistener) then DJlistener:getModData().IsDancingInit = true; end

	PlayerIsListeningToMusic(DJlistener, SourceMusiclvl)
end

function PlayerIsListeningToMusic(listener, SourceMusiclvl)
	if not IsListeningCheckConditions(listener) then return; end
	if not SourceMusiclvl then SourceMusiclvl = 3; end

	local characterData = listener:getModData()
	local HaloCounter = characterData.HaloCooldownCounter

	getSoundManager():setMusicVolume(0)
	characterData.VanillaMusicResume = listener:getModData().VanillaMusicResume + 1

	local HasEarProtection = IsListeningCheckEarProtection(listener)
	if (HasEarProtection == 0.1) and listener:isAsleep() then return; end
	local SongQuality = ((SourceMusiclvl + 1) * 0.5)
	--local NoBooing = IsListeningIsJukebox(listener)

	if not listener:isAsleep() then IsListeningSetMood(listener, HasEarProtection, SongQuality); end
	if HasEarProtection == 0.1 then return; end
	
	if HasEarProtection == 1 then characterData.ListenedToMusic = tonumber(SourceMusiclvl); end
	if IsListeningCheckAsleepOrMeditating(listener) then return; end

	local animChance = ZombRand(100)+1
	if (HaloCounter >= 5) and (animChance >= 50) and IsListeningCanDoAction(listener) and (not IsListeningIsJukebox(listener)) then
		if IsListeningToBadMusic(listener, SourceMusiclvl) then
			IsListeningComplain(listener, animChance, characterData.TDcomplained)
			HaloTextHelper.addTextWithArrow(listener, getText("IGUI_HaloNote_Boredom"), true, HaloTextHelper.getColorRed())
		elseif (not characterData.GaveApplause) and IsListeningToGoodMusic(listener, SourceMusiclvl) then
			IsListeningCheer(listener, animChance, characterData.TDcomplained)
			HaloTextHelper.addTextWithArrow(listener, getText("IGUI_HaloNote_Boredom"), false, HaloTextHelper.getColorGreen())
		end
	end
end

function OtherPlayerIsStartingDuet(currentPerformer, SourceWaitingDuet)
	    if currentPerformer and currentPerformer:hasModData() and
		currentPerformer:getModData().WaitingDuet then
			if not SourceWaitingDuet then currentPerformer:getModData().WaitingDuet = false; end
		end
end

