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
-- DEPRECATED
--[[
require "TimedActions/ISBaseTimedAction"

MeditateAction = ISBaseTimedAction:derive("MeditateAction");

local MeditationDoTextHelperXP = 0
local MeditationDoTextHelperPain = 0
local MeditationDoTextHelperStress = 0
local MeditationDoTextHelperBoredom = 0

local function adjustStats(character, meditationFocus)

	local characterData = character:getModData()
	local PlayerMeditationLevel = character:getPerkLevel(Perks.Meditation)
	local bodyDamage = character:getBodyDamage()
	local stats = character:getStats()
	local currentBoredom = bodyDamage:getBoredomLevel()
	local currentUnhappyness = bodyDamage:getUnhappynessLevel()
	local currentStress = stats:getStress();
	local currentExhaustion = stats:getEndurance()
	local currentFatigue = stats:getFatigue()
	local currentPain = character:getStats():getPain()
	if character:HasTrait("Smoker") then
		stats:setStressFromCigarettes(0)
	end

	--SANDBOX
	local StrengthMultiplier = 1
	local sandboxMeditationStrengthMultiplier = SandboxVars.Meditation.StrengthMultiplier or 2
	if sandboxMeditationStrengthMultiplier ~= nil then
		if sandboxMeditationStrengthMultiplier == 1 then
			StrengthMultiplier = 0.5
		elseif sandboxMeditationStrengthMultiplier == 2 then
			StrengthMultiplier = 1
		elseif sandboxMeditationStrengthMultiplier == 3 then
			StrengthMultiplier = 2
		elseif sandboxMeditationStrengthMultiplier == 4 then
			StrengthMultiplier = 4
		end
	end

	--VARIABLES
	local Trait = 0
	local Level = 0
	local Mind = 0
	local varMult = 1
	local Buffer = 0
	local Aversion = 0
	local WasTaught = 1
	--TRAIT
	if character:HasTrait("Disciplined") then
		Trait = 2
	end
	if character:HasTrait("CouchPotato") then
		Aversion = 8
		Trait = -0.5
	end
	--LEVEL
	Level = ((tonumber(PlayerMeditationLevel))/4) + 1
	if PlayerMeditationLevel >= 8 then
		Aversion = 0
	elseif PlayerMeditationLevel >= 6 then
		if Aversion < 1 then
		Aversion = 1
		end
	elseif PlayerMeditationLevel >= 4 then
		if Aversion < 2 then
		Aversion = 2
		end
	elseif PlayerMeditationLevel >= 2 then
		if Aversion < 3 then
		Aversion = 3
		end
	elseif PlayerMeditationLevel < 2 then
		if Aversion < 4 then
		Aversion = 4
		end
	end
	--CURRENT BOREDOM
	if currentBoredom <= 5 then
		Aversion = Aversion - 5
		Buffer = 3
	elseif currentBoredom <= 10 then
		Aversion = Aversion - 3
		Buffer = 2
	elseif currentBoredom <= 20 then
		Aversion = Aversion - 1
		Buffer = 1
	elseif currentBoredom <= 30 then
		Aversion = Aversion + 1
	elseif currentBoredom > 30 then
		Aversion = Aversion + 3
	end
	if Aversion < 0 then
		Aversion = 0
	end

	--MINDFULNESS
	if (characterData.LSMoodles["MindfulState"].Value == 0.6) then
		Mind = 4
	elseif (characterData.LSMoodles["MindfulState"].Value == 0.4) then
		Mind = 3
	elseif (characterData.LSMoodles["MindfulState"].Value == 0.2) then
		Mind = 2
	end

	--WASTAUGHT
	if (characterData.LSMoodles["WasTaughtMeditation"].Value >= 0.2) then
		WasTaught = 3
	end

	--MEDITATION FOCUS
	if meditationFocus >= 25 then
		if varMult < 7 then
		varMult = 7
		--character:Say("FOCUS 40")
		end
	elseif meditationFocus >= 15 then
		if varMult < 5 then
		varMult = 5
		--character:Say("FOCUS 40")
		end
	elseif meditationFocus >= 10 then
		if varMult < 3 then
		varMult = 3
		--character:Say("FOCUS 20")
		end
	elseif meditationFocus >= 6 then
		if varMult < 2 then
		varMult = 2
		--character:Say("FOCUS 10")
		end
	elseif meditationFocus >= 3 then
		if varMult < 1.5 then
		varMult = 1.5
		--character:Say("FOCUS 5")
		end
	end


	--RESULT
	--local varMult = varMult - varMultN
	Aversion = Aversion/StrengthMultiplier
	local varAdd = Mind + Trait + Level + 1
	local varResult = varAdd * varMult * StrengthMultiplier
	local varResultBuffer = (varResult + Buffer)
	local aversionMulti = (Aversion * 0.1)
	local aversionDiv = Aversion
	if aversionDiv < 1 then
		aversionDiv = 1
	end
	
	--DEFINES
	--BOREDOM 0 - 100
	local boredomChange = (5 + Aversion - Buffer)/varResult
	bodyDamage:setBoredomLevel(currentBoredom + boredomChange)

	--STRESS 0 - 1
	local stressChange = 0.005 * varResultBuffer
	stats:setStress(currentStress - stressChange)

	--UNHAPPYNESS 0 - 100
	local unhappynessChange = 0.1 * varResult
	bodyDamage:setUnhappynessLevel((currentUnhappyness + aversionMulti)- unhappynessChange)

	--PAIN
	local neckPainChange = (2 + Aversion - Buffer)/varResult
	if currentPain > 50 then
		neckPainChange = 0
	end
	bodyDamage:getBodyPart(BodyPartType.Neck):setAdditionalPain(neckPainChange)

	--XP
	local xpChange = (varResultBuffer*WasTaught)/aversionDiv
	if PlayerMeditationLevel == 10 then
		xpChange = 0
	end
	character:getXp():AddXP(Perks.Meditation, xpChange)

	--HALOTEXT

	if boredomChange >= 5 then
		MeditationDoTextHelperBoredom = 3
	elseif boredomChange >= 3 then
		MeditationDoTextHelperBoredom = 2
	elseif boredomChange >= 1 then
		MeditationDoTextHelperBoredom = 1
	end

	if neckPainChange >= 5 then
		MeditationDoTextHelperPain = 2
	elseif neckPainChange >= 2 then
		MeditationDoTextHelperPain = 1
	end

	if xpChange >= 10 then
		MeditationDoTextHelperXP = 3
	elseif xpChange >= 5 then
		MeditationDoTextHelperXP = 2
	elseif xpChange >= 2 then
		MeditationDoTextHelperXP = 1
	end

	if stressChange >= 0.1 then
		MeditationDoTextHelperStress = 3
	elseif stressChange >= 0.06 then
		MeditationDoTextHelperStress = 2
	elseif stressChange >= 0.02 then
		MeditationDoTextHelperStress = 1
	end

end

function MeditateAction:isValid()
	return true
end

function MeditateAction:waitToStart()
    return false
end

function MeditateAction:update()

	local characterData = self.character:getModData()
	--local HaloCounter = characterData.HaloCooldownCounter

	if characterData.IsMeditationDisturbed == true then
		self:forceStop()
	end

	if characterData.LSMoodles ~= nil and characterData.LSMoodles["MindfulState"].Value ~= 0.6 and (self.character:getPerkLevel(Perks.Meditation) == 10) then

		self.MindfulCount = self.MindfulCount + getGameTime():getGameWorldSecondsSinceLastUpdate()
		--*4 is 10k
		--*3 is 7.5k
		--it takes 10k ticks to reach strong mindfulness from 0. It takes 20k ticks to reach perfect mindfulness from 0
		if self.MindfulCount > (self.MindfulTotal * 4) and characterData.LSMoodles["MindfulState"].Value == 0.4 then
			LSMoodleManager.setValue("MindfulState", 0.6)
			characterData.IsMeditationMindfulness = "Perfect"
			characterData.MeditationMindfulness = 0
			self.MindfulCount = 0
		elseif self.MindfulCount > self.MindfulTotal and characterData.LSMoodles["MindfulState"].Value ~= 0.2 and characterData.LSMoodles["MindfulState"].Value ~= 0.4 then
			LSMoodleManager.setValue("MindfulState", 0.2)
			characterData.IsMeditationMindfulness = "Novice"
			characterData.MeditationMindfulness = 0
			self.MindfulCount = 0
		elseif self.MindfulCount > (self.MindfulTotal * 3) and characterData.LSMoodles["MindfulState"].Value == 0.2 then
			LSMoodleManager.setValue("MindfulState", 0.4)
			characterData.IsMeditationMindfulness = "Strong"
			characterData.MeditationMindfulness = 0
			self.MindfulCount = 0
		end

	elseif characterData.LSMoodles ~= nil and characterData.LSMoodles["MindfulState"].Value ~= 0.4 and (self.character:getPerkLevel(Perks.Meditation) > 5) and (self.character:getPerkLevel(Perks.Meditation) < 10) then
	
		self.MindfulCount = self.MindfulCount + getGameTime():getGameWorldSecondsSinceLastUpdate()
		
		if self.MindfulCount > self.MindfulTotal and characterData.LSMoodles["MindfulState"].Value ~= 0.2 then
			LSMoodleManager.setValue("MindfulState", 0.2)
			characterData.IsMeditationMindfulness = "Novice"
			characterData.MeditationMindfulness = 0
			self.MindfulCount = 0
		elseif self.MindfulCount > (self.MindfulTotal * 3) and characterData.LSMoodles["MindfulState"].Value == 0.2 then
			LSMoodleManager.setValue("MindfulState", 0.4)
			characterData.IsMeditationMindfulness = "Strong"
			characterData.MeditationMindfulness = 0
			self.MindfulCount = 0
		end
	elseif characterData.LSMoodles ~= nil and characterData.LSMoodles["MindfulState"].Value ~= 0.2 and (self.character:getPerkLevel(Perks.Meditation) > 2) and (self.character:getPerkLevel(Perks.Meditation) < 6) then
	
		self.MindfulCount = self.MindfulCount + getGameTime():getGameWorldSecondsSinceLastUpdate()
		
		if self.MindfulCount > self.MindfulTotal and characterData.LSMoodles["MindfulState"].Value ~= 0.2 then
			LSMoodleManager.setValue("MindfulState", 0.2)
			characterData.IsMeditationMindfulness = "Novice"
			characterData.MeditationMindfulness = 0
			self.MindfulCount = 0
		end
	end

	if self.character:isAiming() then
		self:forceStop();
	end
	
	if not self.character:getEmitter():isPlaying(self.soundFile) then
		
	local soundRadius = 5
	local volume = 3
	
	self.GameSound = self.character:getEmitter():playSound(self.soundFile);
		
		addSound(self.character,
				 self.character:getX(),
				 self.character:getY(),
				 self.character:getZ(),
				 soundRadius,
				 volume)
	end

	self.AdjustStatsCountdown = self.AdjustStatsCountdown + getGameTime():getGameWorldSecondsSinceLastUpdate()

	if self.AdjustStatsCountdown > self.AdjustStatsCountdownTotal then

	self.AdjustStatsCountdown = 0
	self.meditationFocus = self.meditationFocus + getGameTime():getGameWorldSecondsSinceLastUpdate()

	if MeditationDoTextHelperXP ~= 0 and (self.character:getPerkLevel(Perks.Meditation) ~= 10) then
		if MeditationDoTextHelperXP == 1 then
			HaloTextHelper.addText(self.character, getText("IGUI_HaloNote_XP"), 200, 255, 200)
			MeditationDoTextHelperXP = 0
		elseif MeditationDoTextHelperXP == 2 then
			HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_XP"), true, 170, 255, 150)
			MeditationDoTextHelperXP = 0
		elseif MeditationDoTextHelperXP == 3 then
			HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_XPBonus"), true, 70, 255, 50)
			MeditationDoTextHelperXP = 0
		end
	end
	
	if MeditationDoTextHelperStress ~= 0 then
		if MeditationDoTextHelperStress == 1 then
			HaloTextHelper.addText(self.character, getText("IGUI_HaloNote_Stress"), 200, 255, 200)
			MeditationDoTextHelperStress = 0
		elseif MeditationDoTextHelperStress == 2 then
			HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Stress"), false, 170, 255, 150)
			MeditationDoTextHelperStress = 0
		elseif MeditationDoTextHelperStress == 3 then
			HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Stress"), false, 70, 255, 50)
			MeditationDoTextHelperStress = 0
		end
	end

	if MeditationDoTextHelperBoredom ~= 0 then
		if MeditationDoTextHelperBoredom == 1 then
			HaloTextHelper.addText(self.character, getText("IGUI_HaloNote_Boredom"), 255, 180, 180)
			MeditationDoTextHelperBoredom = 0
		elseif MeditationDoTextHelperBoredom == 2 then
			HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Boredom"), true, 255, 120, 120)
			MeditationDoTextHelperBoredom = 0
		elseif MeditationDoTextHelperBoredom == 3 then
			HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Boredom"), true, 255, 75, 75)
			MeditationDoTextHelperBoredom = 0
		end
	end

	if MeditationDoTextHelperPain ~= 0 then
		if MeditationDoTextHelperPain == 1 then
			HaloTextHelper.addText(self.character, getText("IGUI_HaloNote_Pain_BadPosture"), 255, 180, 180)
			MeditationDoTextHelperPain = 0
		elseif MeditationDoTextHelperPain == 2 then
			HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Pain_BadPosture"), true, 255, 120, 120)
			MeditationDoTextHelperPain = 0
		elseif MeditationDoTextHelperPain == 3 then
			HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Pain"), true, 255, 75, 75)
			MeditationDoTextHelperPain = 0
		end
	end

	adjustStats(self.character, self.meditationFocus)

	end
end

function MeditateAction:start()

	self.action:setUseProgressBar(false)
	local actionType = self.actionType
	local length = self.length
	self:setActionAnim(actionType)

	local soundRadius = 5
	local volume = 3
	
	local characterData = self.character:getModData()
	if characterData.IsMeditating ~= nil
	then
	characterData.IsMeditating = true
	else
	characterData.IsMeditating = true
	end

	if self.character:HasTrait("Disciplined") then
	self.MindfulTotal = 400--2000
	end

	self.GameSound = self.character:getEmitter():playSound(self.soundFile);
		
		addSound(self.character,
				 self.character:getX(),
				 self.character:getY(),
				 self.character:getZ(),
				 soundRadius,
				 volume)

	if characterData.LSMoodles ~= nil and characterData.LSMoodles["MindfulState"].Value == 0 then
		characterData.IsMeditationMindfulness = "disabled"
	end

end

function MeditateAction:stop()

	--local currentDelta = self:getJobDelta()
	--local deltaIncrease = currentDelta - self.deltaTabulated

	--adjustStats(self.character, self.meditationFocus)

	--self.deltaTabulated = currentDelta

	local bodyDamage = self.character:getBodyDamage()
	-- Adjust stats to 0 if levels went below 0
	if (bodyDamage:getBoredomLevel() < 0) then
		bodyDamage:setBoredomLevel(0)
	end
	if (bodyDamage:getUnhappynessLevel() < 0) then
		bodyDamage:setUnhappynessLevel(0)
	end
	if (self.character:getStats():getStress() < 0) then
		self.character:getStats():setStress(0)
	end

	--print("totalpain: " .. bodyDamage:getBodyPart(BodyPartType.Neck):getPain())
	--print("totalbored: " .. bodyDamage:getBoredomLevel())
	--print("totalunhap: " .. bodyDamage:getUnhappynessLevel())
	--print("totalstress: " .. self.character:getStats():getStress())

	self.character:getModData().lastMeditate = getGameTime():getDay()

	local characterData = self.character:getModData()
	characterData.IsMeditating = false
	if characterData.IsMeditationMindfulness ~= nil and characterData.IsMeditationMindfulness ~= "disabled" then
		characterData.MeditationMindfulness = 1
	end
	self.MindfulCount = 0
	
	if self.GameSound and
		self.GameSound ~= 0 then
		self.character:getEmitter():stopSound(self.GameSound);
	end
	
	if characterData.IsMeditationDisturbed == true then
	-- we get the moddata and voice tracks
	local PlayerVoice = characterData.PlayerVoice

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
	if self.character:getDescriptor():isFemale() then
		sound = randomTrack.soundF
	end

	self.character:getEmitter():playSound(sound)
	end

	characterData.IsMeditationDisturbed = false

	ISBaseTimedAction.stop(self)
end

function MeditateAction:perform()

	--local currentDelta = self:getJobDelta()
	--local deltaIncrease = currentDelta - self.deltaTabulated

	adjustStats(self.character, self.meditationFocus)
	--self.deltaTabulated = currentDelta

	local bodyDamage = self.character:getBodyDamage()

	-- Adjust stats to 0 if levels went below 0
	if (bodyDamage:getBoredomLevel() < 0) then
		bodyDamage:setBoredomLevel(0)
	end
	if (bodyDamage:getUnhappynessLevel() < 0) then
		bodyDamage:setUnhappynessLevel(0)
	end
	if (self.character:getStats():getStress() < 0) then
		self.character:getStats():setStress(0)
	end

	--print("totalpain: " .. bodyDamage:getBodyPart(BodyPartType.Neck):getPain())
	--print("totalbored: " .. bodyDamage:getBoredomLevel())
	--print("totalunhap: " .. bodyDamage:getUnhappynessLevel())
	--print("totalstress: " .. self.character:getStats():getStress())

	self.character:getModData().lastMeditate = getGameTime():getDay()

	local characterData = self.character:getModData()
	characterData.IsMeditating = false
	characterData.IsMeditationDisturbed = false
	if characterData.IsMeditationMindfulness ~= nil and characterData.IsMeditationMindfulness ~= "disabled" then
		characterData.MeditationMindfulness = 1
	end
	self.MindfulCount = 0

	if self.GameSound and
		self.GameSound ~= 0 then
		self.character:getEmitter():stopSound(self.GameSound);
	end
	
	ISBaseTimedAction.perform(self);
end

function MeditateAction:new(character, sound, length, level, xp, boredomReduction, stressReduction, neckPain, actionType) -- What to call in your code
    local o = {};
    setmetatable(o, self);
    self.__index = self;
    o.character = character;
	o.soundFile = sound
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = length; -- Time taken by the action defined in meditatecontextmenu
	o.GameSound = 0
	o.level = level
	o.xp = xp
	o.neckPain = neckPain
	o.boredomReduction = boredomReduction
	o.stressReduction = stressReduction
	--o.deltaTabulated = 0
	o.actionType = actionType; -- Anim taken by the action defined in meditatecontextmenu
	o.MindfulCount = 0
	o.MindfulTotal = 500--2500
	o.AdjustStatsCountdown = 0
	o.AdjustStatsCountdownTotal = 100--600
	o.meditationFocus = 0
    if o.character:isTimedActionInstant() then o.maxTime = 1; end
    return o;
end
]]--