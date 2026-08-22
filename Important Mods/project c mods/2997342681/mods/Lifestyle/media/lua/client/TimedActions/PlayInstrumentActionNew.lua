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

local PlayInstrumentActionNew = ISBaseTimedAction:derive('PlayInstrumentActionNew');

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
	
function PlayInstrumentActionNew:isValid()
	if self.character:getVehicle() or self.character:isSneaking() then return false; end
	return true
end

function PlayInstrumentActionNew:waitToStart()
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

local function getWaitAnim(instrument)
	local t = {
		Trumpet = "Bob_PlayTrumpetWaiting",
		GuitarAcoustic = "Bob_PlayGuitarWaiting",
		Banjo = "Bob_PlayGuitarWaiting",
		Keytar = "Bob_PlayKeytarWaiting",
		Saxophone = "Bob_PlaySaxophoneWaiting",
		GuitarElectricBass = "Bob_PlayGuitarBassWaiting",
		GuitarElectric = "Bob_PlayGuitarBassWaiting",
		Flute = "Bob_PlayFluteWaiting",
		Harmonica = "Bob_PlayFluteWaiting",
		Violin = "Bob_PlayViolinWaiting",
		Piano = "Bob_PlayPianoWaiting",
	}
	return t[instrument] or "Bob_PlayGuitarWaiting"
end

local function getStandingAnim(playerLevel, instrument, movingAnim)
	local t = {
		GuitarAcoustic = {3, "Bob_PlayGuitar"},
		GuitarElectricBass = {3, "Bob_PlayGuitar"},
		GuitarElectric = {3, "Bob_PlayGuitar"},
		Violin = {0, "Bob_PlayViolin"}
	}
	if not t[instrument] or playerLevel < t[instrument][1] then return movingAnim; end
	if playerLevel < 6 then return t[instrument][2].."Default_Mov"; end
	local animTypes = {"Default", "Good"}
	local animIdx = ZombRand(#animTypes)+1
	return t[instrument][2]..animTypes[animIdx].."_Mov"
end

function PlayInstrumentActionNew:soundPing()
	local soundRadius, volume = 10, 5
	if self.character:isOutside() then soundRadius, volume = 30, 10; end
	addSound(self.character,self.character:getX(),self.character:getY(),self.character:getZ(),soundRadius,volume)
end

local function getAdjPiece(key)
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

local function getAdjObj(mainObj, spriteName, ogSquare)
	local square = mainObj:getSquare()
	if not square or (square and square ~= ogSquare) then return false; end
	local objVars, adjObject = getAdjPiece(spriteName), false
	if objVars then
		local objAdjSqr = square:getAdjacentSquare(IsoDirections[objVars[2]])
		if not objAdjSqr then return false; end
		for i=1,objAdjSqr:getObjects():size() do
			local obj = objAdjSqr:getObjects():get(i-1)
			if obj then
				local objName = obj:getSpriteName() or obj:getTextureName()
				if objName and objName == objVars[1] then adjObject = obj; break; end
			end
		end
	end
	return adjObject
end

function PlayInstrumentActionNew:checkObj()
	if self.instrumentType ~= "Piano" and self.instrumentType ~= "Drums" then return; end
	if not self.obj or not instanceof(self.obj, "IsoObject") or not self.objSquare then self.instrument = false; return; end
	local adjObj = getAdjObj(self.obj, self.instrument, self.objSquare)
	if not adjObj then self.instrument = false; return; end
end

function PlayInstrumentActionNew:update()
	
	if self.panicLevel and self.character:getMoodles():getMoodleLevel(MoodleType.Panic) > self.panicLevel then self.isFailState = true; end
	
	if self.isFailState or not self.instrument or isKeyDown(Keyboard.KEY_E) or self.character:isSneaking() or (not self.character:getPrimaryHandItem() and self.instrumentType ~= "Piano") then
		self:forceStop()
	end

	if self.character:getModData().WaitingDuet then
		if self.currentAction ~= 2 then
		local waitAnim = getWaitAnim(self.instrumentType)
		self:setActionAnim(waitAnim)
		self.currentAction = 2
		end
		self:resetJobDelta()
	else

		if not self.character:isPlayerMoving() and not self.character:isSitOnGround() and not self.character:getModData().IsSittingOnSeat then
			if self.currentAction ~= 3 then
				local anim = getStandingAnim(self.playerLevel, self.instrumentType, self.AnimToplay)
				self:setActionAnim(anim)
				self.currentAction = 3
			end
		elseif self.currentAction ~= 1 then
			self:setActionAnim(self.AnimToplay)
			self.currentAction = 1
		end

		local isPlaying = self.gameSound and self.gameSound ~= 0 and self.character:getEmitter():isPlaying(self.gameSound)
		if not isPlaying then
			self.gameSound = self.character:getEmitter():playSound(self.soundFile)
			self["soundPing"](self)
		end
	
		self.actionCount = self.actionCount + (getGameTime():getGameWorldSecondsSinceLastUpdate()*GTLSCheck)
		if self.actionCount > self.actionTotal then
			self.actionCount = 0
			self.moodArgs = adjustStats(self.character, self.trackLevel, self.moodArgs)
			
			self["soundPing"](self)
		
			if not self.isDuet and (self.playerLevel >= self.trackLevel and self.playerLevel <= 5) then
				-- stress and level check
				local num1, num2 = getRandomChance(self.character, self.character:getStats():getStress(), self.stressLvls, self.baseStress)
				local randomchance = ZombRand(num1, num2)
				self.isFailState = getFailState(self.playerLevel, self.trackLevel, randomchance)
			end
		
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
			self["checkObj"](self)
		end
		--Metabolics
		if self.doMetabolics then self.character:setMetabolicTarget(Metabolics.UsingTools); end
	end--WAITING
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

local function doNote(character, texture)
	local text = " <CENTRE> "..getText("IGUI_Instruments_Band")
	local infoText = " <LINE><H1> "..getText("IGUI_Instruments_Band_Title").." <LINE> ".." <CENTRE> <IMAGE:media/ui/tutorial/Instruments_Band_01.png,300,200> <LINE><LINE><TEXT> "..getText("IGUI_Instruments_Band_Body").." <LINE><LINE> "..getText("IGUI_Instruments_Band_Body2").." <LINE><LINE> "..getText("IGUI_Instruments_Band_Body3").." <LINE><LINE> "..getText("IGUI_Instruments_Band_Body4")
	LSNoteMng.addToQueue(getCore():getScreenWidth()-400,(getCore():getScreenHeight()/5)-50,300,50, {character, text, "noteBand", texture, 4, "noteBand", infoText, true}) -- player, mainText, queueType, tex, time, closePerm, infoPanel, noSpam
end

local function getPropItem(itemName)
	local prop
	local items = getAllItems()
    for i=0, items:size()-1 do
        local item = items:get(i)
        if item and item:getFullName() and item:getFullName() == itemName then
			local itemInstance = item:InstanceItem(item:getFullName())
			prop = itemInstance:getWorldStaticItem()
			break
        end
    end
	return prop
end

function PlayInstrumentActionNew:adjustPlayerPosition()
	-- keeping this as a temporary workaround for grand pianos as it works for both b41 and b42 (removing this for b42 soon)
	if LSUtil.pianoPos then return; end
	--if self.ogPos then self.character:setX(self.ogPos[1]); self.character:setY(self.ogPos[2]); return; end
	local vars = getAdjPiece(self.instrument)
	if not vars or not vars[3] or not vars[4] then return; end
	local sqr = self.character:getSquare()
	local x, y = sqr:getX(),sqr:getY()
	--self.ogPos = {x,y}
	self.character:setX(x+vars[3]); self.character:setY(y+vars[4])
	LSUtil.pianoPos = true
end

function PlayInstrumentActionNew:pianoParams()
	self.stopOnWalk = true
	self.stopOnAim = true
	self:setOverrideHandModels(nil, nil)
	self["adjustPlayerPosition"](self)
end

function PlayInstrumentActionNew:instrumentParams()
	if self.instrumentType == "Piano" then
		self["pianoParams"](self)
	elseif self.instrumentType == "Violin" then
		local prop = getPropItem("Lifestyle.violinBow")
		self:setOverrideHandModels(prop, self.instrument)
	elseif self.instrumentType == "Harmonica" and self.instrument:getFullType() ~= "Lifestyle.Harmonica" then
		self:setOverrideHandModels("Lifestyle.Harmonica", nil)
	else
		self:setOverrideHandModels(self.instrument, nil)
	end
end

local function isValidAnim(pLvl, sitting, aLvl, aSit)
	if pLvl < aLvl then return false; end
	if aSit and ((sitting and aSit ~= 1) or (not sitting and aSit ~= 0)) then return false; end
	if pLvl >= 4 and aLvl < pLvl-3 then return false; end
	return true
end

function PlayInstrumentActionNew:animParams()
	local t = {}
	local isSitting = self.character:isSitOnGround()
	for k,v in pairs(self.instrumentAnimations) do
		if v.instrument == self.instrumentType and isValidAnim(self.playerLevel, isSitting, v.level, v.isSit) then
			table.insert(t, v)
		end
	end
	local idxAnim = ZombRand(#t)+1
	self.AnimToplay = t[idxAnim].name
end

local function playGuitarSFX(character, instrument, soundName, num)
	if instrument ~= "GuitarElectric" and instrument ~= "GuitarElectricBass" then return; end
	local sfx = soundName..tostring(ZombRand(num)+1)
	character:getEmitter():playSound(sfx)
end

function PlayInstrumentActionNew:start()
	self["instrumentParams"](self)
	
	self.character:getModData().PlayingInstrument = true
	
	getSoundManager():setMusicVolume(0)

	self.action:setUseProgressBar(false)

	self["animParams"](self)

	if self.isDuet then
		self.character:getModData().WaitingDuet = true
		doNote(self.character, "appliances_com_01_68")
	end

	-- panic check
	if not self.character:HasTrait("Desensitized") then
		if self.character:HasTrait("Brave") or self.character:HasTrait("Disciplined") then
			self.panicLevel = 3
		else
			self.panicLevel = 2
		end
	end
	self.baseStress, self.stressLvls = getFailChanceArgs(self.character)
	playGuitarSFX(self.character, self.instrumentType, "Guitar_pickup", 4)
end

function PlayInstrumentActionNew:stop()

	local characterData = self.character:getModData()
	characterData.PlayingInstrument = false
	if self.isDuet and characterData.WaitingDuet then characterData.WaitingDuet = false; end

	if self.gameSound and self.gameSound ~= 0 and self.character:getEmitter():isPlaying(self.gameSound) then self.character:getEmitter():stopSound(self.gameSound); end

	if self.isFailState then
		local failsound = self.instrumentType.."Failstate0"..tostring(ZombRand(3)+1)
		self.character:getEmitter():playSound(failsound)
		self["soundPing"](self)
		if characterData.LSMoodles["Embarrassed"].Value then
			characterData.LSMoodles["Embarrassed"].Value = characterData.LSMoodles["Embarrassed"].Value + 0.1
		end
		HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Embarrassed"), true, 255, 120, 120)
	end

	doFixMood(self.character)

	if characterData.IsSittingOnSeat then
		if characterData.IsSittingOnSeatSouth then
			self.character:setVariable("SittingToggleLoop", "S")
			self.character:setVariable("IsSittingInChair", "IsSittingS")
		else
			self.character:setVariable("SittingToggleLoop", "N")
			self.character:setVariable("IsSittingInChair", "IsSitting")
		end
	end

	playGuitarSFX(self.character, self.instrumentType, "Guitar_stop", 3)

	getSoundManager():setMusicVolume(self.musicOriginalVolume)

	ISBaseTimedAction.stop(self);
end

function PlayInstrumentActionNew:perform()

	if self.gameSound and self.gameSound ~= 0 and self.character:getEmitter():isPlaying(self.gameSound) then self.character:getEmitter():stopSound(self.gameSound); end

	adjustStats(self.character, self.trackLevel, false)

	local characterData = self.character:getModData()
	characterData.PlayingInstrument = false
	if self.isDuet and characterData.WaitingDuet then characterData.WaitingDuet = false; end
	
	if characterData.IsSittingOnSeat then
		if characterData.IsSittingOnSeatSouth then
			self.character:setVariable("SittingToggleLoop", "S")
			self.character:setVariable("IsSittingInChair", "IsSittingS")
		else
			self.character:setVariable("SittingToggleLoop", "N")
			self.character:setVariable("IsSittingInChair", "IsSitting")
		end
	end
	
	playGuitarSFX(self.character, self.instrumentType, "Guitar_stop", 3)

	getSoundManager():setMusicVolume(self.musicOriginalVolume)

    ISBaseTimedAction.perform(self);
end

local function isMetabolicsEnabled(option)
	return option == 1
end

function PlayInstrumentActionNew:new(character, Item, Type, sound, length, level, IsTraining, IsDuet, Object) -- Item must be SPRITENAME for piano and drums, obj is used for piano and drums
    local o = {}
    setmetatable(o, self)
    self.__index = self
    o.character = character;
	o.instrument = Item;
	o.soundFile = sound
	o.obj = Object
	o.objSquare = Object and Object:getSquare()
    o.stopOnWalk = false;
    o.stopOnRun = true;
    o.stopOnAim = false;
	o.ignoreHandsWounds = true;
	o.maxTime = length
	o.instrumentType = Type
	o.isTraining = IsTraining
	o.isDuet = IsDuet
	o.instrumentAnimations = require("Instruments/InstrumentAnimations")
	o.AnimToplay = 0
	o.gameSound = 0
	o.trackLevel = level
	o.musicOriginalVolume = tonumber(getSoundManager():getMusicVolume())
	o.actionCount = 0
	o.actionTotal = 120--600
	o.handItem = false
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

return PlayInstrumentActionNew;