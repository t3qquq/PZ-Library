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



require "TimedActions/ISBaseTimedAction"

local PlayInstrumentVocal = ISBaseTimedAction:derive('PlayInstrumentVocal');
local Failstate = false
local randomchance = ZombRand(1, 100)
local MusicDoTextHelperUnhappyness = 0
local MusicDoTextHelperBoredom = 0

local function adjustStats(character, tracklevel)

	local characterData = character:getModData()
	local PlayerMusicLevel = character:getPerkLevel(Perks.Music)
	local bodyDamage = character:getBodyDamage()
	local stats = character:getStats()
	local currentBoredom = bodyDamage:getBoredomLevel()
	local currentUnhappyness = bodyDamage:getUnhappynessLevel()
	local currentStress = stats:getStress();
	local currentExhaustion = stats:getEndurance()
	local currentFatigue = stats:getFatigue()
	if character:HasTrait("Smoker") then
		stats:setStressFromCigarettes(0)
	end

	--SANDBOX
	local StrengthMultiplier = 1
	local sandboxMusicStrengthMultiplier = SandboxVars.Music.StrengthMultiplier or 2
	if sandboxMusicStrengthMultiplier ~= nil then
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

	--VARIABLES
	--local Party = 0
	local Trait = 0
	local Level = 0
	local varMult = 1
	local reverseBuffs = 0
	--TRAIT
	if character:HasTrait("Virtuoso") or character:HasTrait("KeenHearing") or (characterData.LSMoodles["PartyGood"].Value >= 0.2) then
		Trait = 1
	end
	if character:HasTrait("HardOfHearing") then
		if varMult > 0.9 then
		varMult = 0.9
		end
	end
	if character:HasTrait("ToneDeaf") then
		reverseBuffs = 1
	end
	--LEVEL
	Level = ((tonumber(PlayerMusicLevel))/3) + ((tonumber(tracklevel))/3)
	
	--STRESS
	if currentStress >= 0.8 then
		if varMult > 0.1 then
		varMult = 0.1
		end
	elseif currentStress >= 0.6 then
		if varMult > 0.8 then
		varMult = 0.8
		end
	elseif currentStress >= 0.4 then
		if varMult > 0.9 then
		varMult = 0.9
		end
	end
	--PARTYBAD
	if (characterData.LSMoodles["PartyBad"].Value == 0.6) then
		if varMult > 0.1 then
		varMult = 0.1
		end
	elseif characterData.LSMoodles["PartyBad"].Value == 0.4 then
		if varMult > 0.5 then
		varMult = 0.5
		end
	elseif characterData.LSMoodles["PartyBad"].Value == 0.2 then
		if varMult > 0.9 then
		varMult = 0.9
		end
	end

	--EMBARRASSED
	if (characterData.LSMoodles["Embarrassed"].Value == 0.8) then
		if varMult > 0.01 then
		varMult = 0.01
		end
	elseif (characterData.LSMoodles["Embarrassed"].Value == 0.6) then
		if varMult > 0.15 then
		varMult = 0.15
		end
	elseif characterData.LSMoodles["Embarrassed"].Value == 0.4 then
		if varMult > 0.3 then
		varMult = 0.3
		end
	elseif characterData.LSMoodles["Embarrassed"].Value == 0.2 then
		if varMult > 0.5 then
		varMult = 0.5
		end
	end

	--FATIGUE
	if currentFatigue >= 0.8 then
		if varMult > 0.02 then
		varMult = 0.02
		end
	elseif currentFatigue >= 0.7 then
		if varMult > 0.2 then
		varMult = 0.2
		end
	elseif currentFatigue >= 0.5 then
		if varMult > 0.4 then
		varMult = 0.4
		end
	elseif currentFatigue >= 0.4 then
		if varMult > 0.6 then
		varMult = 0.6
		end
	end

	--EXHAUSTION
	if currentExhaustion <= 0.2 then
		if varMult > 0.02 then
		varMult = 0.02
		end
	elseif currentExhaustion <= 0.3 then
		if varMult > 0.2 then
		varMult = 0.2
		end
	elseif currentExhaustion <= 0.4 then
		if varMult > 0.4 then
		varMult = 0.4
		end
	elseif currentExhaustion <= 0.7 then
		if varMult > 0.6 then
		varMult = 0.6
		end
	end

	--RESULT
	local varAdd = Trait + Level + 1
	local varAddRev = Trait + Level + StrengthMultiplier + 1
	local varResult = varAdd * varMult * StrengthMultiplier
	
	if reverseBuffs == 1 then
		if varAddRev >= 6  then
			varAddRev = 5.9
		end
		varResult = (6 - varAddRev)/varMult
	end
	
	--DEFINES
	--ENDURANCE
	stats:setEndurance(currentExhaustion - 0.003)
	stats:setFatigue(currentFatigue + 0.001)
	--BOREDOM 0 - 100
	local boredomChange = 1 * varResult
	--STRESS 0 - 1
	local stressChange = 0.005 * varResult
	--UNHAPPYNESS 0 - 100
	local unhappynessChange = 0.5 * varResult

	--SET
	if reverseBuffs == 1 then
		bodyDamage:setBoredomLevel(currentBoredom + boredomChange)
		stats:setStress(currentStress + stressChange)
		bodyDamage:setUnhappynessLevel(currentUnhappyness + unhappynessChange)
		varResult = varAdd * varMult * StrengthMultiplier * 0.5-- FOR XP
	else	
		bodyDamage:setBoredomLevel(currentBoredom - boredomChange)
		stats:setStress(currentStress - stressChange)
		bodyDamage:setUnhappynessLevel(currentUnhappyness - unhappynessChange)
	end

	--XP
	if Level < 1 then Level = 1; end
	local xpChange = math.floor((Level*varResult)/3)
	--local xpChange = math.floor(varResult/3)
	if PlayerMusicLevel == 10 then
		xpChange = 0
	end
	character:getXp():AddXP(Perks.Music, xpChange)

	--HALOTEXT

	if boredomChange >= 10 then
		MusicDoTextHelperBoredom = 3
	elseif boredomChange >= 5 then
		MusicDoTextHelperBoredom = 2
	elseif boredomChange >= 1 then
		MusicDoTextHelperBoredom = 1
	end

	if unhappynessChange >= 5 then
		MusicDoTextHelperUnhappyness = 2
	elseif unhappynessChange >= 1 then
		MusicDoTextHelperUnhappyness = 1
	end

	--(my) Sanity Check
	if character:isInvisible() then character:Say("PLAYER IS INVISIBLE: CAN'T PLAY SOUND"); end
end
	
function PlayInstrumentVocal:isValid()
	if (self.character:getVehicle()) or (self.character:isSitOnGround()) or (self.character:isSneaking()) then return false; end
   return true;
end


function PlayInstrumentVocal:waitToStart()
	self.character:faceThisObject(self.mic)

	return self.character:shouldBeTurning()
	end

function PlayInstrumentVocal:update()
	
	if not self.mic then
		self:forceStop()
	end
	if isKeyDown(Keyboard.KEY_E) then
		self:forceStop()
	end
	if self.character:isSneaking() then
		self:forceStop()
	end
	if self.character:isSitOnGround() then
		self:forceStop()
	end

	if self.character:getModData().WaitingDuet == true then
	local waitforTime = "Bob_PlayTrumpetWaiting"
	self:setActionAnim(waitforTime)
	self:resetJobDelta()
	else
		if not self.data.PlayingDJBooth then self.data.PlayingDJBooth = true; end --!
	-- panic check
	if not self.character:HasTrait("Desensitized") then
		if self.character:HasTrait("Brave") or self.character:HasTrait("Disciplined") then
			if self.character:getMoodles():getMoodleLevel(MoodleType.Panic) > 3 then
				Failstate = true
			end
		elseif self.character:getMoodles():getMoodleLevel(MoodleType.Panic) > 2 then
				Failstate = true
		end
	end
	
	if Failstate == true then
	self:forceStop()
	end

	local playerlevel = self.character:getPerkLevel(Perks.Music)
	local tracklevel = self.level
	
	if isKeyDown(Keyboard.KEY_O) then
		self:setActionAnim("Bob_SingingSlow")
	else
		self:setActionAnim(self.AnimToplay)
	end

	local isPlaying = self.gameSound
		and self.gameSound ~= 0
		and self.character:getEmitter():isPlaying(self.gameSound)

	if not isPlaying then
		-- Some examples of radius and volume found in PZ code:
		-- Fishing (20,1)
		-- Remove Grass (10,5)
		-- Remove Glass (20,1)
		-- Destroy Stuff (20,10)
		-- Remove Bush (20,10)
		-- Move Sprite (10,5)
		local soundRadius = 10
		local volume = 5
		
		if self.character:isOutside() then
		soundRadius = 30
		volume = 10
		end

		self.gameSound = self.character:getEmitter():playSound(self.soundFile);
		
		addSound(self.character,
				 self.character:getX(),
				 self.character:getY(),
				 self.character:getZ(),
				 soundRadius,
				 volume)
		
	end
	
	self.actionCount = self.actionCount + (getGameTime():getGameWorldSecondsSinceLastUpdate()*GTLSCheck)
	if self.actionCount > self.actionTotal then
		self.actionCount = 0
		adjustStats(self.character, self.level)
		
		local soundRadius = 10
		local volume = 5

		if self.character:isOutside() then
		soundRadius = 30
		volume = 10
		end

		-- update for zombies as the character moves

		addSound(self.character,
				 self.character:getX(),
				 self.character:getY(),
				 self.character:getZ(),
				 soundRadius,
				 volume)
		
		if (playerlevel >= tracklevel and
		playerlevel <= 5) and not self.isDuet then
		
	-- stressCheck
	if not self.character:HasTrait("Disciplined") then
		if self.character:HasTrait("Dextrous") then
			if self.character:getStats():getStress() > 0.8 then
					randomchance = ZombRand(22, 100)
			elseif self.character:getStats():getStress() > 0.5 then
					randomchance = ZombRand(14, 100)
			elseif self.character:getStats():getStress() > 0.2 then
					randomchance = ZombRand(6, 100)
			else
					randomchance = ZombRand(1, 100)
			end
		elseif self.character:HasTrait("Clumsy") then
				if self.character:getStats():getStress() > 0.8 then
				randomchance = ZombRand(48, 100)
				elseif self.character:getStats():getStress() > 0.5 then
					randomchance = ZombRand(36, 100)
				elseif self.character:getStats():getStress() > 0.2 then
					randomchance = ZombRand(24, 100)
				else
					randomchance = ZombRand(16, 100)
				end
		elseif self.character:getStats():getStress() > 0.8 then
				randomchance = ZombRand(31, 100)
		elseif self.character:getStats():getStress() > 0.5 then
				randomchance = ZombRand(21, 100)
		elseif self.character:getStats():getStress() > 0.2 then
				randomchance = ZombRand(11, 100)
		else
				randomchance = ZombRand(1, 100)
		end
	elseif self.character:HasTrait("Clumsy") then
	randomchance = ZombRand(16, 100)
	else
	randomchance = ZombRand(1, 100)
	end

			if playerlevel > tracklevel + 2
			and randomchance >= 99 then
			Failstate = true
			end
			if playerlevel == tracklevel + 2
			and randomchance >= 97 then
			Failstate = true
			end
			if playerlevel == tracklevel + 1
			and randomchance >= 95 then
			Failstate = true
			end
			if playerlevel == tracklevel
			and randomchance >= 92 then
			Failstate = true
			end

	end
		local HappyOrBored = ZombRand(2)+1
		if Failstate == false and self.character:HasTrait("ToneDeaf") then
			if HappyOrBored == 1 then 
				if MusicDoTextHelperUnhappyness == 2 then
					HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Happyness"), false, 255, 75, 75)
				elseif MusicDoTextHelperUnhappyness == 1 then
					HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Happyness"), false, 255, 120, 120)
				end
			elseif HappyOrBored == 2 then
				if MusicDoTextHelperBoredom == 3 then
					HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Boredom"), true, 255, 30, 30)
				elseif MusicDoTextHelperBoredom == 2 then
					HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Boredom"), true, 255, 75, 75)
				elseif MusicDoTextHelperBoredom == 1 then
					HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Boredom"), true, 255, 120, 120)
				end
			end
			MusicDoTextHelperUnhappyness = 0
			MusicDoTextHelperBoredom = 0
		elseif Failstate == false then
			if HappyOrBored == 1 then 
				if MusicDoTextHelperUnhappyness == 2 then
					HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Happyness"), true, 70, 255, 50)
				elseif MusicDoTextHelperUnhappyness == 1 then
					HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Happyness"), true, 170, 255, 150)
				end
			elseif HappyOrBored == 2 then
				if MusicDoTextHelperBoredom == 3 then
					HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Boredom"), false, 70, 255, 50)
				elseif MusicDoTextHelperBoredom == 2 then
					HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Boredom"), false, 170, 255, 150)
				elseif MusicDoTextHelperBoredom == 1 then
					HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Boredom"), false, 200, 255, 200)
				end
			end
			MusicDoTextHelperUnhappyness = 0
			MusicDoTextHelperBoredom = 0
		end
		
	end
	end--WAITING
end

local function doNote(character, texture)
	local text = " <CENTRE> "..getText("IGUI_Instruments_Band")
	local infoText = " <LINE><H1> "..getText("IGUI_Instruments_Band_Title").." <LINE> ".." <CENTRE> <IMAGE:media/ui/tutorial/Instruments_Band_01.png,300,200> <LINE><LINE><TEXT> "..getText("IGUI_Instruments_Band_Body").." <LINE><LINE> "..getText("IGUI_Instruments_Band_Body2").." <LINE><LINE> "..getText("IGUI_Instruments_Band_Body3").." <LINE><LINE> "..getText("IGUI_Instruments_Band_Body4")
	LSNoteMng.addToQueue(getCore():getScreenWidth()-400,(getCore():getScreenHeight()/5)-50,300,50, {character, text, "noteBand", texture, 4, "noteBand", infoText, true}) -- player, mainText, queueType, tex, time, closePerm, infoPanel, noSpam
end

function PlayInstrumentVocal:start()

	self:setOverrideHandModels(nil, nil)
	self.character:getModData().PlayingInstrument = true

	getSoundManager():setMusicVolume(0)

	self.action:setUseProgressBar(false)

	local playerlevel = self.character:getPerkLevel(Perks.Music)
	self.AvailableAnims = {}

	for k,v in pairs(self.instrumentAnimations) do
		if playerlevel >= 4 then
			if v.instrument == "vocal" and v.level <= playerlevel and v.level >= (playerlevel - 3) then
				table.insert(self.AvailableAnims, v)
			end
		elseif v.instrument == "vocal" and v.level <= playerlevel then
			table.insert(self.AvailableAnims, v)
		end
	end

	self.idxAnim = ZombRand(#self.AvailableAnims) + 1
	self.AnimToplay = self.AvailableAnims[self.idxAnim].name
	self.AnimTime = self.AvailableAnims[self.idxAnim].keyframes

	if self.isDuet then
		self.character:getModData().WaitingDuet = true
		doNote(self.character, "appliances_com_01_68")
	end

end

function PlayInstrumentVocal:stop()

	local characterData = self.character:getModData()

	if self.isDuet and
	characterData.WaitingDuet then
	characterData.WaitingDuet = false
	end

	if self.gameSound and
		self.gameSound ~= 0 and
		self.character:getEmitter():isPlaying(self.gameSound) then
		self.character:getEmitter():stopSound(self.gameSound);
	end

	if Failstate == true then
	
	local failsound = "TrumpetFailstate01"
	local soundrandomiser = ZombRand(1, 100)
	
		if self.micType == "Standing" then
			if soundrandomiser >=75 then
				failsound = "PianoFailstate01"
			elseif soundrandomiser >=50 then
				failsound = "PianoFailstate02"
			elseif soundrandomiser >=25 then
				failsound = "PianoFailstate03"
			else
				failsound = "PianoFailstate04"
			end
	
		end

	local soundRadius = 10
	local volume = 5

		if self.character:isOutside() then
		soundRadius = 30
		volume = 10
		end

		self.character:getEmitter():playSound(failsound);
		
		addSound(self.character,
				 self.character:getX(),
				 self.character:getY(),
				 self.character:getZ(),
				 soundRadius,
				 volume)

		if characterData.LSMoodles["Embarrassed"].Value ~= nil then
			characterData.LSMoodles["Embarrassed"].Value = characterData.LSMoodles["Embarrassed"].Value + 0.1
		end
		HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Embarrassed"), true, 255, 120, 120)

	Failstate = false
	end

	local bodyDamage = self.character:getBodyDamage()
	-- adjust stats to 0 if levels went below 0
	if (bodyDamage:getBoredomLevel() < 0) then
		bodyDamage:setBoredomLevel(0)
	end
	if (bodyDamage:getUnhappynessLevel() < 0) then
		bodyDamage:setUnhappynessLevel(0)
	end
	if (self.character:getStats():getStress() < 0) then
		self.character:getStats():setStress(0)
	end

	-- adjust stats if levels went above
	if (bodyDamage:getBoredomLevel() > 100) then
		bodyDamage:setBoredomLevel(100)
	end
	if (bodyDamage:getUnhappynessLevel() > 100) then
		bodyDamage:setUnhappynessLevel(100)
	end
	if (self.character:getStats():getStress() > 1) then
		self.character:getStats():setStress(1)
	end

	self.data.PlayingInstrument = false --!
	self.data.PlayingDJBooth = false --!
	self.data.PlayingDJBoothStopped = true --!

	getSoundManager():setMusicVolume(self.musicOriginalVolume)

	ISBaseTimedAction.stop(self);
end

function PlayInstrumentVocal:perform()

	if self.gameSound and
		self.gameSound ~= 0 and
		self.character:getEmitter():isPlaying(self.gameSound) then
		self.character:getEmitter():stopSound(self.gameSound);
	end

	adjustStats(self.character, self.level)

	local bodyDamage = self.character:getBodyDamage()
	-- adjust stats to 0 if levels went below 0
	if (bodyDamage:getBoredomLevel() < 0) then
		bodyDamage:setBoredomLevel(0)
	end
	if (bodyDamage:getUnhappynessLevel() < 0) then
		bodyDamage:setUnhappynessLevel(0)
	end
	if (self.character:getStats():getStress() < 0) then
		self.character:getStats():setStress(0)
	end

	-- adjust stats if levels went above
	if (bodyDamage:getBoredomLevel() > 100) then
		bodyDamage:setBoredomLevel(100)
	end
	if (bodyDamage:getUnhappynessLevel() > 100) then
		bodyDamage:setUnhappynessLevel(100)
	end
	if (self.character:getStats():getStress() > 1) then
		self.character:getStats():setStress(1)
	end

	local characterData = self.character:getModData()
	
	if self.isDuet and
	characterData.WaitingDuet then
	characterData.WaitingDuet = false
	end
	
	self.data.PlayingInstrument = false --!
	self.data.PlayingDJBooth = false --!
	self.data.PlayingDJBoothStopped = true --!

	getSoundManager():setMusicVolume(self.musicOriginalVolume)

    ISBaseTimedAction.perform(self);
end

function PlayInstrumentVocal:new(character, Mic, Type, sound, length, level, IsTraining, IsDuet)
    local o = {}
    setmetatable(o, self)
    self.__index = self
    o.character = character;
	o.mic = Mic;
	o.soundFile = sound
    o.stopOnWalk = true;
    o.stopOnRun = true;
    o.stopOnAim = true;
	o.ignoreHandsWounds = true;
	o.maxTime = length
	o.micType = Type
	o.isTraining = IsTraining
	o.isDuet = IsDuet
	o.AvailableAnims = 0
	o.instrumentAnimations = require("Instruments/InstrumentAnimations")
	o.idxAnim = 0
	o.AnimToplay = 0
	o.AnimTime = 0
	o.gameSound = 0
	o.level = level
	o.musicOriginalVolume = tonumber(getSoundManager():getMusicVolume())
	o.actionCount = 0
	o.actionTotal = 120--600
	o.data = character:getModData() --!
    return o;
end

return PlayInstrumentVocal;