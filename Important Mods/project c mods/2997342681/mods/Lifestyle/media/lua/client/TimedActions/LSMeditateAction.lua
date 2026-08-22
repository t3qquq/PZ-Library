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

LSMeditateAction = ISBaseTimedAction:derive("LSMeditateAction");

local function equipHandItem(character, item, side)
	if item and side then 
		if side == "L" then character:setPrimaryHandItem(item);
		elseif side == "R" then character:setSecondaryHandItem(item); end
	end
end

local function getHandItems(character)
	return character:getPrimaryHandItem(), character:getSecondaryHandItem()
end

local function getNewMindfulnessState(skillLevel, mindfulState, count, total)
	local newLevel
	if count > total then
		if mindfulState < 0.2 then newLevel = 0.2; end
		if (skillLevel > 5) and (mindfulState < 0.4) and (not newLevel) and (count > (total*3)) then newLevel = 0.4; end
		if (skillLevel == 10) and (mindfulState < 0.6) and (not newLevel) and (count > (total*4)) then newLevel = 0.6; end
	end
	return newLevel
end

local function doHaloMeditationNeg(character, name, value)
	local r, g, b, doArrow = 255, 180, 180, false
	if value == 2 then
		r, g, b, doArrow = 255, 120, 120, true
	elseif value == 3 then
		r, g, b, doArrow = 255, 75, 75, true
	end
	if doArrow then HaloTextHelper.addTextWithArrow(character, getText("IGUI_HaloNote_"..name), true, r, g, b);
	else HaloTextHelper.addText(character, getText("IGUI_HaloNote_"..name), r, g, b); end
end

local function doHaloMeditation(character, name, value)
	local r, g, b, doArrow, arrow = 200, 255, 200, false, true
	if value == 2 then
		r, g, b, doArrow = 170, 255, 150, true
	elseif value == 3 then
		r, g, b, doArrow = 70, 255, 50, true
	end
	if doArrow then
		if name == "Stress" then arrow = false; end
		HaloTextHelper.addTextWithArrow(character, getText("IGUI_HaloNote_"..name), arrow, r, g, b)
	else
		HaloTextHelper.addText(character, getText("IGUI_HaloNote_"..name), r, g, b)
	end
end

local function fixStats(character)
	local bodyDamage = character:getBodyDamage()
	if (bodyDamage:getBoredomLevel() > 100) then
		bodyDamage:setBoredomLevel(100)
	end
	if (bodyDamage:getUnhappynessLevel() < 0) then
		bodyDamage:setUnhappynessLevel(0)
	end
	if (bodyDamage:getUnhappynessLevel() > 100) then
		bodyDamage:setUnhappynessLevel(100)
	end
	if (character:getStats():getStress() < 0) then
		character:getStats():setStress(0)
	end
end

local function doDisturbedNoise(character)
	local PlayerVoice = character:getModData().PlayerVoice
	local PlayerVoiceTracks = require("TimedActions/PlayerVoiceTracks")
	local AvailablePlayerVoiceTracks = {}
	-- we loop the voice tracks and select the ones that we want, making sure to only select the ones that match the player voice
	for k,v in pairs(PlayerVoiceTracks) do
		if v.Voice == PlayerVoice and
		v.Type == "Frustrated" then--MAKE SURE TO CHANGE THIS LINE
			table.insert(AvailablePlayerVoiceTracks, v)
		end
	end
	-- then we add a random factor to it
	local randomLine = ZombRand(#AvailablePlayerVoiceTracks) + 1
	local randomTrack = AvailablePlayerVoiceTracks[randomLine]
	local sound = randomTrack.sound
	if character:getDescriptor():isFemale() then
		sound = randomTrack.soundF
	end
	character:getEmitter():playSound(sound)
end

local function adjustStatsGetSO()
	local sM = 1
	local multiplierSO = SandboxVars.Meditation.StrengthMultiplier or 2
	if multiplierSO then
		if multiplierSO == 1 then
			sM = 0.5
		elseif multiplierSO == 3 then
			sM = 2
		elseif multiplierSO == 4 then
			sM = 4
		end
	end
	return sM
end

local function adjustStatsGetVariables(character, PlayerMeditationLevel)
	local Trait, Level, Mind, WasTaught, data = 0, 0, 0, 1, character:getModData()
	--TRAIT
	if character:HasTrait("Disciplined") then
		Trait = 2
	elseif character:HasTrait("CouchPotato") then
		Trait = -0.5
	end
	--LEVEL
	Level = ((tonumber(PlayerMeditationLevel))/4)+1
	--MINDFULNESS
	if data.LSMoodles["MindfulState"].Value == 0.6 then
		Mind = 4
	elseif data.LSMoodles["MindfulState"].Value == 0.4 then
		Mind = 3
	elseif data.LSMoodles["MindfulState"].Value == 0.2 then
		Mind = 2
	end
	--WASTAUGHT
	if (data.LSMoodles["WasTaughtSkill"].Value >= 0.2 and data.WasTaughtLast and data.WasTaughtLast == "Meditation") or data.LSMoodles["WasTaughtMeditation"].Value >= 0.2 then
		WasTaught = 3
	end
	return Trait, Level, Mind, WasTaught
end

local function adjustStatsGetAversion(character, PlayerMeditationLevel, currentBoredom)
	local Aversion, Buffer = 0, 0
	--TRAIT
	if character:HasTrait("Disciplined") then
		Aversion = -2
	elseif character:HasTrait("CouchPotato") then
		Aversion = 4
	end
	--LEVEL
	if PlayerMeditationLevel >= 8 then
		Aversion = 0
	elseif PlayerMeditationLevel >= 6 then
		if Aversion < 1 then
		Aversion = Aversion+1
		end
	elseif PlayerMeditationLevel >= 4 then
		if Aversion < 2 then
		Aversion = Aversion+2
		end
	elseif PlayerMeditationLevel >= 2 then
		if Aversion < 3 then
		Aversion = Aversion+3
		end
	elseif PlayerMeditationLevel < 2 then
		if Aversion < 4 then
		Aversion = Aversion+4
		end
	end
	--CURRENT BOREDOM
	if currentBoredom <= 5 then
		Aversion = Aversion-4
		Buffer = 3
	elseif currentBoredom <= 10 then
		Aversion = Aversion-2
		Buffer = 2
	elseif currentBoredom <= 20 then
		Aversion = Aversion+2
		Buffer = 1
	elseif currentBoredom <= 30 then
		Aversion = Aversion+4
	elseif currentBoredom > 30 then
		Aversion = Aversion+8
	end
	if Aversion < 0 then
		Aversion = 0
	end
	return Aversion, Buffer
end

local function adjustStatsGetMFMultiplier(meditationFocus)
	local varMult = 1
	--MEDITATION FOCUS
	if meditationFocus >= 25 then
		varMult = 7
	elseif meditationFocus >= 15 then
		varMult = 5
	elseif meditationFocus >= 10 then
		varMult = 3
	elseif meditationFocus >= 6 then
		varMult = 2
	elseif meditationFocus >= 3 then
		varMult = 1.5
	end
	return varMult
end

local function adjustStatsGetChanges(Aversion, Buffer, varResult, currentPain, WasTaught, PlayerMeditationLevel, character)
	local boredomChange, stressChange, unhappynessChange, neckPainChange, xpChange = 0, 0, 0, 0, 0
	--BOREDOM 0 - 100
	boredomChange = (5+Aversion-Buffer)/varResult
	--STRESS 0 - 1
	stressChange = 0.005*(varResult + Buffer)
	--UNHAPPYNESS 0 - 100
	unhappynessChange = 0.1*varResult
	--PAIN
	if currentPain < 50 then
		neckPainChange = (2+Aversion-Buffer)/varResult
	end
	--XP
	if PlayerMeditationLevel < 10 then
		local zen = 1
		if character:getModData().LSMoodles["Zen"] and character:getModData().LSMoodles["Zen"].Level and character:getModData().LSMoodles["Zen"].Level > 0 then
			zen = 1.1*character:getModData().LSMoodles["Zen"].Level
		end
		xpChange = (varResult+Buffer)*WasTaught*zen
		if Aversion > 1 then xpChange = xpChange/Aversion; end
	end
	return boredomChange, stressChange, unhappynessChange, neckPainChange, xpChange
end

local function adjustStatsGetHalos(boredomChange, stressChange, neckPainChange, xpChange)
	local haloXP, haloSTRESS, haloBOREDOM, haloPAIN
	if boredomChange >= 5 then
		haloBOREDOM = 3
	elseif boredomChange >= 3 then
		haloBOREDOM = 2
	elseif boredomChange >= 1 then
		haloBOREDOM = 1
	end
	if neckPainChange >= 5 then
		haloPAIN = 2
	elseif neckPainChange >= 2 then
		haloPAIN = 1
	end
	if xpChange >= 10 then
		haloXP = 3
	elseif xpChange >= 5 then
		haloXP = 2
	elseif xpChange >= 2 then
		haloXP = 1
	end
	if stressChange >= 0.1 then
		haloSTRESS = 3
	elseif stressChange >= 0.06 then
		haloSTRESS = 2
	elseif stressChange >= 0.02 then
		haloSTRESS = 1
	end
	return haloXP, haloSTRESS, haloBOREDOM, haloPAIN
end

local function adjustStats(character, meditationFocus)

	local PlayerMeditationLevel = character:getPerkLevel(Perks.Meditation)
	local bodyDamage = character:getBodyDamage()
	local stats = character:getStats()
	local currentBoredom = bodyDamage:getBoredomLevel()
	local currentUnhappyness = bodyDamage:getUnhappynessLevel()
	local currentStress = stats:getStress();
	local currentPain = character:getStats():getPain()
	if character:HasTrait("Smoker") then
		stats:setStressFromCigarettes(0)
	end

	--SANDBOX
	local StrengthMultiplier = adjustStatsGetSO()
	--VARIABLES
	local Trait, Level, Mind, WasTaught = adjustStatsGetVariables(character, PlayerMeditationLevel)
	local Aversion, Buffer = adjustStatsGetAversion(character, PlayerMeditationLevel, currentBoredom)
	local varMult = adjustStatsGetMFMultiplier(meditationFocus)
	--RESULT
	--local varMult = varMult - varMultN
	if Aversion ~= 0 then Aversion = Aversion/StrengthMultiplier; end
	local varResult = (Mind+Trait+Level+1)*varMult*StrengthMultiplier
	
	--DEFINES
	local boredomChange, stressChange, unhappynessChange, neckPainChange, xpChange = adjustStatsGetChanges(Aversion, Buffer, varResult, currentPain, WasTaught, PlayerMeditationLevel, character)
	bodyDamage:setBoredomLevel(currentBoredom + boredomChange)
	stats:setStress(currentStress - stressChange)
	bodyDamage:setUnhappynessLevel((currentUnhappyness + (Aversion*0.1))- unhappynessChange)
	bodyDamage:getBodyPart(BodyPartType.Neck):setAdditionalPain(neckPainChange)
	character:getXp():AddXP(Perks.Meditation, xpChange)


	--HALOTEXT
	local haloXP, haloSTRESS, haloBOREDOM, haloPAIN = adjustStatsGetHalos(boredomChange, stressChange, neckPainChange, xpChange)
	return haloXP, haloSTRESS, haloBOREDOM, haloPAIN
end

function LSMeditateAction:isValid()
	if (self.character:getVehicle()) or (not self.character:isSitOnGround()) or (self.character:isSneaking()) then return false; end
	return true
end

function LSMeditateAction:waitToStart()
    return false
end

function LSMeditateAction:update()

	if (self.character:isAiming()) or (self.character:getModData().IsMeditationDisturbed) or (self.character:getVehicle()) then
		self:forceStop()
	end

	if (self.skillLevel > 2) and (self.mindfulState < 0.6) then
		local newMindfulnessLevel = getNewMindfulnessState(self.skillLevel, self.mindfulState, self.MindfulCount, self.MindfulTotal)
		if newMindfulnessLevel then self.character:getModData().LSMoodles["MindfulState"].Value = newMindfulnessLevel; self.character:getModData().MindfulnessMinutes = false; self.holdTime = true;
		self.character:getModData().MindfulnessLast = newMindfulnessLevel; self.mindfulState = newMindfulnessLevel; self.MindfulCount = 0; end
		self.MindfulCount = self.MindfulCount + (getGameTime():getGameWorldSecondsSinceLastUpdate()*GTLSCheck)
	end

	if not self.character:getEmitter():isPlaying(self.soundFile) then
		self.GameSound = self.character:getEmitter():playSound(self.soundFile);
	end



	if self.AdjustStatsCountdown > self.AdjustStatsCountdownTotal then
		self.AdjustStatsCountdown = 0
		self.meditationFocus = self.meditationFocus + 0.5
		
		self.haloTxts[1].value, self.haloTxts[2].value, self.haloTxts[3].value, self.haloTxts[4].value = adjustStats(self.character, self.meditationFocus)

		for _, halo in ipairs(self.haloTxts) do
			if halo.value then
				if halo.neg then doHaloMeditationNeg(self.character, halo.name, halo.value);
				else doHaloMeditation(self.character, halo.name, halo.value); end
			end
		end

		if self.holdTime then self.character:getModData().MindfulnessMinutes = false; end
	end

	self.AdjustStatsCountdown = self.AdjustStatsCountdown + (getGameTime():getGameWorldSecondsSinceLastUpdate()*GTLSCheck)
end

function LSMeditateAction:start()
	self.handItemLeft, self.handItemRight = getHandItems(self.character)
	self.character:setPrimaryHandItem(nil); self.character:setSecondaryHandItem(nil)
	self.action:setUseProgressBar(false)
	self:setActionAnim(self.actionType)
	self.GameSound = self.character:getEmitter():playSound(self.soundFile)

	if self.character:HasTrait("Disciplined") then
		self.MindfulTotal = 400--2000
	end
	
	self.character:getModData().IsMeditating = true
end

function LSMeditateAction:stop()

	if self.handItemLeft then equipHandItem(self.character, self.handItemLeft, "L"); end
	if self.handItemRight then equipHandItem(self.character, self.handItemRight, "R"); end

	fixStats(self.character)

	if self.GameSound and
		self.GameSound ~= 0 then
		self.character:getEmitter():stopSound(self.GameSound);
	end
	
	if self.character:getModData().IsMeditationDisturbed then doDisturbedNoise(self.character); self.character:getModData().IsMeditationDisturbed = false; end

	self.character:getModData().IsMeditating = false
	ISBaseTimedAction.stop(self)
end

function LSMeditateAction:perform()

	if self.handItemLeft then equipHandItem(self.character, self.handItemLeft, "L"); end
	if self.handItemRight then equipHandItem(self.character, self.handItemRight, "R"); end

	self.haloTxts[1].value, self.haloTxts[2].value, self.haloTxts[3].value, self.haloTxts[4].value = adjustStats(self.character, self.meditationFocus)

	for _, halo in ipairs(self.haloTxts) do
		if halo.value then
			if halo.neg then doHaloMeditationNeg(self.character, halo.name, halo.value);
			else doHaloMeditation(self.character, halo.name, halo.value); end
		end
	end

	fixStats(self.character)

	if self.GameSound and
		self.GameSound ~= 0 then
		self.character:getEmitter():stopSound(self.GameSound);
	end
	
	self.character:getModData().IsMeditating = false
	ISBaseTimedAction.perform(self);
end

function LSMeditateAction:new(character, sound, length, level, xp, boredomReduction, stressReduction, neckPain, actionType)
    local o = {};
    setmetatable(o, self);
    self.__index = self;
    o.character = character;
	o.soundFile = sound
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = length;
	o.GameSound = 0
	o.level = level
	o.xp = xp
	o.neckPain = neckPain
	o.boredomReduction = boredomReduction
	o.stressReduction = stressReduction
	o.actionType = actionType;
	o.MindfulCount = 0
	o.MindfulTotal = 500--2500
	o.AdjustStatsCountdown = 0
	o.AdjustStatsCountdownTotal = 100--600
	o.meditationFocus = 0
	o.handItemLeft = false
	o.handItemRight = false
	o.skillLevel = character:getPerkLevel(Perks.Meditation)
	o.mindfulState = character:getModData().LSMoodles["MindfulState"].Value
	o.holdTime = false
	o.haloTxts = {{name="XP",value=false,neg=false},{name="Stress",value=false,neg=false},{name="Boredom",value=false,neg=true},{name="Pain_BadPosture",value=false,neg=true}}
    if o.character:isTimedActionInstant() then o.maxTime = 1; end
    return o;
end