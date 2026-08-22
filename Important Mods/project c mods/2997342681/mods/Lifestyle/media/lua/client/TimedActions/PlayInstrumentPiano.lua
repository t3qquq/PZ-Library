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

local PlayInstrumentPiano = ISBaseTimedAction:derive('PlayInstrumentPiano');

local function doFixMood(character)
	local bodyDamage, stats = character:getBodyDamage(), character:getStats()
	if character:HasTrait("Smoker") then stats:setStressFromCigarettes(0); end
	local unhappyVal, boredVal, stressVal = math.max(0, math.min(100, bodyDamage:getUnhappynessLevel())),math.max(0, math.min(100, bodyDamage:getBoredomLevel())),math.max(0, math.min(1, stats:getStress()))
	local fatigueVal, exhaustionVal = math.max(0, math.min(1, stats:getFatigue())),math.max(0, math.min(1, stats:getEndurance()))
	bodyDamage:setUnhappynessLevel(unhappyVal); bodyDamage:setBoredomLevel(boredVal); stats:setStress(stressVal); stats:setFatigue(fatigueVal); stats:setEndurance(exhaustionVal);
end

local function getSandboxOption()
	local t = {
		[1] = 0.5,
		[2] = 1,
		[3] = 2,
		[4] = 4,
	}
	return t[SandboxVars.Music.StrengthMultiplier or 2] or 1
end

local function minMult(varMult, value, num)
	local thresholds = {
		[1] = {{0.8, 0.1}, {0.6, 0.8}, {0.4, 0.9}},
		[2] = {{0.6, 0.1}, {0.4, 0.5}, {0.2, 0.9}},
		[3] = {{0.8, 0.01}, {0.6, 0.15}, {0.4, 0.3}, {0.2, 0.5}},
		[4] = {{0.8, 0.02}, {0.7, 0.2}, {0.5, 0.4}, {0.4, 0.6}},
		[5] = {{0.2, 0.02}, {0.3, 0.2}, {0.4, 0.4}, {0.7, 0.6}},
	}
	for _, t in ipairs(thresholds[num]) do
		if num == 5 and value <= t[1] then varMult = math.min(varMult, t[2]) break;
		elseif num ~= 5 and value >= t[1] then varMult = math.min(varMult, t[2]) break end
	end
	return varMult
end

local function adjustStats(character, tracklevel, moodArgs)

	local moodleData = character:getModData().LSMoodles
	local musicLevel = character:getPerkLevel(Perks.Music)
	local bodyDamage = character:getBodyDamage()
	local stats = character:getStats()
	local currentBoredom = bodyDamage:getBoredomLevel()
	local currentUnhappyness = bodyDamage:getUnhappynessLevel()
	local currentStress = stats:getStress();
	local currentExhaustion = stats:getEndurance()
	local currentFatigue = stats:getFatigue()

	--SANDBOX
	local sandboxMult = getSandboxOption()

	--VARIABLES
	local traitBonus = LSAmbtMng.hasActiveCompleted(character, "LSRockstar") and 3 or
	(character:HasTrait("Virtuoso") or character:HasTrait("KeenHearing") or (moodleData["PartyGood"] and moodleData["PartyGood"].Value >= 0.2)) and 1 or 0
	local reverseBuffs = character:HasTrait("ToneDeaf") and 1 or 0
	local level = (musicLevel + tracklevel) / 3
	local varMult = character:HasTrait("HardOfHearing") and 0.9 or 1
	local args = {currentStress, moodleData["PartyBad"].Value, moodleData["Embarrassed"].Value, currentFatigue, currentExhaustion}

	for n=1, #args do
		if args[n] and args[n] > 0 then
			varMult = minMult(varMult, args[n], n)
		end
	end

	--RESULT
	local varAdd = traitBonus + level + 1
	local varResult
	
	if reverseBuffs == 1 then
		local varAddRev = varAdd + sandboxMult
		varAddRev = math.min(varAddRev, 5.9)
		varResult = (6 - varAddRev)/varMult
	else
		varResult = varAdd * varMult * sandboxMult
	end
	
	--ENDURANCE
	stats:setEndurance(currentExhaustion - 0.003)
	stats:setFatigue(currentFatigue + 0.001)
	
	local boredomChange = 1 * varResult
	local stressChange = 0.005 * varResult
	local unhappynessChange = 0.5 * varResult

	--SET
	local sign = (reverseBuffs == 1) and 1 or -1
	bodyDamage:setBoredomLevel(currentBoredom + sign * boredomChange)
	bodyDamage:setUnhappynessLevel(currentUnhappyness + sign * unhappynessChange)
	stats:setStress(currentStress + sign * stressChange)
	
	doFixMood(character)

	--XP
	if musicLevel < 10 then
		level = math.max(level, 1)
		local finalResult = (reverseBuffs == 1) and (varAdd * varMult * sandboxMult * 0.5) or varResult
		local xpChange = math.max(math.floor((level*finalResult)/3),0.25)
		character:getXp():AddXP(Perks.Music, xpChange)
	end

	--INVISIBLE
	if character:isInvisible() then character:Say("PLAYER IS INVISIBLE: CAN'T PLAY SOUND"); end
	--HALOTEXT

	if moodArgs then
		local n1 = (boredomChange >= 10) and 3 or 
		(boredomChange >= 5) and 2 or 
		(boredomChange >= 1) and 1 or 0
		local n2 = (unhappynessChange >= 5) and 2 or 
		(unhappynessChange >= 1) and 1 or 0
		return {n1, n2}
	end
end
	
function PlayInstrumentPiano:isValid()
	if self.character:getVehicle() or self.character:isSneaking() then return false; end
	return true
end

function PlayInstrumentPiano:waitToStart()
	return false
end

local function getHaloArgs(level, isToneDeaf)
	local t = {
		[3] = {70, 255, 50, 255, 30, 30},
		[2] = {170, 255, 150, 255, 75, 75},
		[1] = {200, 255, 200, 255, 120, 120}
	}
	if not t[level] then return {200, 255, 200}; end
	if isToneDeaf then return {t[level][4] or 255, t[level][5] or 120, t[level][6] or 120}; end
	return {t[level][1] or 200, t[level][2] or 255, t[level][3] or 200}
end

local function getFailState(playerLevel, trackLevel, randomchance)
	local levelDiff = playerLevel - trackLevel
	local thresholds = {
		[2] = 97,
		[1] = 95,
		[0] = 92
	}
	local failThreshold = thresholds[levelDiff] or 99
	if randomchance >= failThreshold then return true; end
	return false
end

local function getRandomChance(character, stress, lvls, baseStress)
	local num1, num2 = baseStress, 100
	if not lvls or stress < 0.2 then return num1, num2; end

	if stress > 0.8 then num1, num2 = lvls[5], lvls[6];
	elseif stress > 0.5 then num1, num2 = lvls[3], lvls[4];
	else num1, num2 = lvls[1], lvls[2];
	end
	return num1, num2
end

local function getWaitAnim()
	return "Bob_PlayPianoWaiting"
end

local function getPianoAdjPiece(key)
	local t ={
		-- Western Piano
		recreational_01_8 = {"recreational_01_9","E"},
		recreational_01_9 = {"recreational_01_8","W"},
		recreational_01_12 = {"recreational_01_13","N"},
		recreational_01_13 = {"recreational_01_12","S"},
		-- Grand Piano
		recreational_01_40 = {"recreational_01_41","E",0,0.3},
		recreational_01_41 = {"recreational_01_40","W",0,0.3},
		recreational_01_48 = {"recreational_01_49","N",0.3,0},
		recreational_01_49 = {"recreational_01_48","S",0.3,0},
	}
	return t[key]
end
]]--

--local function getAdjObj(mainObj, spriteName, ogSquare)
--	local square = mainObj:getSquare()
--	if not square or (square and square ~= ogSquare) then return false; end
--	local objVars, adjObject = getPianoAdjPiece(spriteName), false
--	if objVars then
--		local objAdjSqr = square:getAdjacentSquare(IsoDirections[objVars[2]])
--		if not objAdjSqr then return false; end
--		for i=1,objAdjSqr:getObjects():size() do
--			local obj = objAdjSqr:getObjects():get(i-1)
--			if obj then
--				local objName = obj:getSpriteName() or obj:getTextureName()
--				if objName and objName == objVars[1] then adjObject = obj; break; end
--			end
--		end
--	end
--	return adjObject
--end
--[[
local function getValidObj(obj, spriteName, ogSquare)
	if not obj or not instanceof(obj, "IsoObject") or not ogSquare then return false; end
	local adjObj = getAdjObj(obj, spriteName, ogSquare)
	if adjObj then return obj; end
	return false
end

function PlayInstrumentPiano:update()
		--Panic
	if self.panicLevel and self.character:getMoodles():getMoodleLevel(MoodleType.Panic) > self.panicLevel then self.isFailState = true; end
		--Stop Conditions
	if self.isFailState or not self.character:isSitOnGround() or isKeyDown(Keyboard.KEY_E) or self.character:isSneaking() or not self.piano then
		self:forceStop()
	end
		--Duet
	if self.characterData.WaitingDuet then
		if self.currentAction ~= 2 then
		local waitAnim = getWaitAnim()
		self:setActionAnim(waitAnim)
		self.currentAction = 2
		end
		self:resetJobDelta()
	else
		--Animation
		if self.currentAction ~= 1 then
			self:setActionAnim(self.AnimToplay)
			self.currentAction = 1
		end
		--Emitter and Sound
		local isPlaying = self.gameSound and self.gameSound ~= 0 and self.character:getEmitter():isPlaying(self.gameSound)
		if not isPlaying then
			local soundRadius, volume = 10, 5
			if self.character:isOutside() then soundRadius, volume = 30, 10; end
			self.gameSound = self.character:getEmitter():playSound(self.soundFile)
			addSound(self.character,self.character:getX(),self.character:getY(),self.character:getZ(),soundRadius,volume)
		end
		--Action Interval
		self.actionCount = self.actionCount + (getGameTime():getGameWorldSecondsSinceLastUpdate()*GTLSCheck)
		if self.actionCount > self.actionTotal then
			self.actionCount = 0
			self.moodArgs = adjustStats(self.character, self.trackLevel, self.moodArgs)
			--Sound
			local soundRadius, volume = 10, 5
			if self.character:isOutside() then soundRadius, volume = 30, 10; end
			addSound(self.character,self.character:getX(),self.character:getY(),self.character:getZ(),soundRadius,volume)
			--Fail Chance
			if not self.isDuet and (self.playerLevel >= self.trackLevel and self.playerLevel <= 5) then
				-- stress and level check
				local num1, num2 = getRandomChance(self.character, self.character:getStats():getStress(), self.stressLvls, self.baseStress)
				local randomchance = ZombRand(num1, num2)
				self.isFailState = getFailState(self.playerLevel, self.trackLevel, randomchance)
			end
			--Apply Mood and XP
			if not self.isFailState and self.moodArgs and (self.moodArgs[1] ~= 0 or self.moodArgs[2] ~= 0) then
				local HappyOrBored = ZombRand(2)+1
				if self.moodArgs[HappyOrBored] and self.moodArgs[HappyOrBored] ~= 0 then
					local text, arrow = "IGUI_HaloNote_Boredom", self.character:HasTrait("ToneDeaf")
					if HappyOrBored == 2 then text, arrow = "IGUI_HaloNote_Happyness", not self.character:HasTrait("ToneDeaf"); end
					local haloRGB = getHaloArgs(self.moodArgs[HappyOrBored], self.character:HasTrait("ToneDeaf"))
					if haloRGB then HaloTextHelper.addTextWithArrow(self.character, getText(text), arrow, haloRGB[1], haloRGB[2], haloRGB[3]); end
				end
				self.moodArgs = {0, 0}
			end
			--Check Obj and Adj Obj
			self.piano = getValidObj(self.piano, self.spriteName, self.ogSquare)
		end
		--Metabolics
		if self.doMetabolics then self.character:setMetabolicTarget(Metabolics.UsingTools); end
	end--Waiting
end

local function doNote(character, texture)
	local text = " <CENTRE> "..getText("IGUI_Instruments_Band")
	local infoText = " <LINE><H1> "..getText("IGUI_Instruments_Band_Title").." <LINE> ".." <CENTRE> <IMAGE:media/ui/tutorial/Instruments_Band_01.png,300,200> <LINE><LINE><TEXT> "..getText("IGUI_Instruments_Band_Body").." <LINE><LINE> "..getText("IGUI_Instruments_Band_Body2").." <LINE><LINE> "..getText("IGUI_Instruments_Band_Body3").." <LINE><LINE> "..getText("IGUI_Instruments_Band_Body4")
	LSNoteMng.addToQueue(getCore():getScreenWidth()-400,(getCore():getScreenHeight()/5)-50,300,50, {character, text, "noteBand", texture, 4, "noteBand", infoText, true}) -- player, mainText, queueType, tex, time, closePerm, infoPanel, noSpam
end

local function getFailChanceArgs(character)
	local baseStress, lvls = 1, {11, 100, 21, 100, 31, 100}
	if character:HasTrait("Clumsy") then baseStress = 16; end
	if character:HasTrait("Disciplined") then return baseStress, false; end

	if character:HasTrait("Dextrous") then
		lvls = {6, 100, 14, 100, 22, 100}
	elseif character:HasTrait("Clumsy") then
		lvls = {24, 100, 36, 100, 48, 100}
	end

	return baseStress, lvls
end

function PlayInstrumentPiano:start()

	self:setOverrideHandModels(nil, nil)
	self.characterData.PlayingInstrument = true
	self["adjustPlayerPosition"](self)
	
	getSoundManager():setMusicVolume(0)

	self.action:setUseProgressBar(false)

	--Animations
	self.AvailableAnims = {}
	for k,v in pairs(self.instrumentAnimations) do
		if self.playerLevel >= 4 then
			if v.instrument == "piano" and v.level <= self.playerLevel and v.level >= (self.playerLevel - 3) then
				table.insert(self.AvailableAnims, v)
			end
		elseif v.instrument == "piano" and v.level <= self.playerLevel then
			table.insert(self.AvailableAnims, v)
		end
	end
	self.idxAnim = ZombRand(#self.AvailableAnims) + 1
	self.AnimToplay = self.AvailableAnims[self.idxAnim].name
	self.AnimTime = self.AvailableAnims[self.idxAnim].keyframes
	--Duet
	if self.isDuet then
		self.characterData.WaitingDuet = true
		doNote(self.character, "appliances_com_01_68")
	end

	--Panic
	if not self.character:HasTrait("Desensitized") then
		if self.character:HasTrait("Brave") or self.character:HasTrait("Disciplined") then
			self.panicLevel = 3
		else
			self.panicLevel = 2
		end
	end
	--Stress
	self.baseStress, self.stressLvls = getFailChanceArgs(self.character)
end

function PlayInstrumentPiano:stop()

	self.characterData.PlayingInstrument = false
	if self.isDuet and self.characterData.WaitingDuet then self.characterData.WaitingDuet = false; end

	if self.gameSound and self.gameSound ~= 0 and self.character:getEmitter():isPlaying(self.gameSound) then self.character:getEmitter():stopSound(self.gameSound); end

	if self.isFailState then
		local failsound = "PianoFailstate0"..tostring(ZombRand(4)+1)
		local soundRadius, volume = 10, 5
		if self.character:isOutside() then soundRadius, volume = 30, 10; end

		self.character:getEmitter():playSound(failsound);
		addSound(self.character,self.character:getX(),self.character:getY(),self.character:getZ(),soundRadius,volume)

		if self.characterData.LSMoodles["Embarrassed"].Value then
			self.characterData.LSMoodles["Embarrassed"].Value = self.characterData.LSMoodles["Embarrassed"].Value + 0.1
		end
		HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Embarrassed"), true, 255, 120, 120)
	end

	doFixMood(self.character)
	
	getSoundManager():setMusicVolume(self.musicOriginalVolume)

	ISBaseTimedAction.stop(self);
end

function PlayInstrumentPiano:perform()

	if self.gameSound and self.gameSound ~= 0 and self.character:getEmitter():isPlaying(self.gameSound) then self.character:getEmitter():stopSound(self.gameSound); end

	adjustStats(self.character, self.trackLevel, false)

	self.characterData.PlayingInstrument = false
	if self.isDuet and self.characterData.WaitingDuet then self.characterData.WaitingDuet = false; end

	getSoundManager():setMusicVolume(self.musicOriginalVolume)

    ISBaseTimedAction.perform(self);
end

function PlayInstrumentPiano:adjustPlayerPosition()
	-- keeping this as a temporary workaround for grand pianos as it works for both b41 and b42 (removing this for b42 soon)
	if LSUtil.pianoPos then return; end
	--if self.ogPos then self.character:setX(self.ogPos[1]); self.character:setY(self.ogPos[2]); return; end
	local vars = getPianoAdjPiece(self.spriteName)
	if not vars or not vars[3] or not vars[4] then return; end
	local sqr = self.character:getSquare()
	--local x, y = sqr:getX(),sqr:getY()
	--self.ogPos = {x,y}
	self.character:setX(sqr:getX()+vars[3]); self.character:setY(sqr:getY()+vars[4])
	LSUtil.pianoPos = true
end

local function isMetabolicsEnabled(option)
	return option == 1
end

function PlayInstrumentPiano:new(character, Piano, Type, SpriteName, Args)--Sound, Length, Level, IsTraining, IsDuet
    local o = {}
    setmetatable(o, self)
    self.__index = self
    o.character = character;
	o.piano = Piano;
	o.soundFile = Args[1]
    o.stopOnWalk = true;
    o.stopOnRun = true;
    o.stopOnAim = true;
	o.ignoreHandsWounds = true;
	o.maxTime = Args[2]
	o.pianoType = Type
	o.isDuet = Args[5]
	o.AvailableAnims = 0
	o.instrumentAnimations = require("Instruments/InstrumentAnimations")
	o.idxAnim = 0
	o.AnimToplay = 0
	o.AnimTime = 0
	o.gameSound = 0
	o.trackLevel = Args[3]
	o.musicOriginalVolume = tonumber(getSoundManager():getMusicVolume())
	o.actionCount = 0
	o.actionTotal = 120--600
	o.characterData = character:getModData()
	o.spriteName = SpriteName
	o.ogSquare = Piano:getSquare()
	o.currentAction = 0
	o.panicLevel = false
	o.isFailState = false
	o.playerLevel = character:getPerkLevel(Perks.Music)
	o.stressLvls = false
	o.baseStress = 1
	o.moodArgs = {0, 0}
	o.doMetabolics = isMetabolicsEnabled(SandboxVars.Music.Metabolics or 1)
    return o;
end

return PlayInstrumentPiano;
]]--