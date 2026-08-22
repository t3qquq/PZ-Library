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

local PlayInstrumentPianoTraining = ISBaseTimedAction:derive('PlayInstrumentPianoTraining');

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

local function checkAmbitions(character, level)
	if level < 10 then return; end
	if LSAmbtMng.hasActive(character, "LSRockstar") and (not LSAmbtMng.hasCompleted(character, "LSRockstar")) then
		character:getModData().Ambitions["LSRockstar"].goal2progress = math.floor(character:getModData().Ambitions["LSRockstar"].goal2progress+1)
	end
end

local function adjustStats(character, tracklevel)

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
	local level = (musicLevel + tracklevel) / 1.5
	local varMult = character:HasTrait("HardOfHearing") and 0.9 or 1
	local args = {currentStress, moodleData["PartyBad"].Value, moodleData["Embarrassed"].Value, currentFatigue, currentExhaustion}

	for n=1, #args do
		if args[n] and args[n] > 0 then
			varMult = minMult(varMult, args[n], n)
		end
	end

	--RESULT
	local varAdd = traitBonus + level + 1
	local varResult = varAdd * varMult * sandboxMult * 0.5

	--ENDURANCE
	stats:setEndurance(currentExhaustion - 0.003)
	stats:setFatigue(currentFatigue + 0.001)
	--BOREDOM 0 - 100
	local boredomChange = (1/varMult)/(sandboxMult+varAdd)
	--SET
	if character:HasTrait("ToneDeaf") then
		local stressChange = boredomChange/100
		stats:setStress(currentStress + stressChange)
		local unhappynessChange = boredomChange/10
		bodyDamage:setUnhappynessLevel(currentUnhappyness + unhappynessChange)
		boredomChange = boredomChange*2
		varResult = varResult*0.5		
	end
	if not character:HasTrait("Virtuoso") then bodyDamage:setBoredomLevel(currentBoredom + boredomChange); end
	doFixMood(character)

	--XP
	if musicLevel < 10 then
		level = math.max(level, 1)
		--print("PlayInstrumentTraining - adjustStats function - level is: "..tostring(level).." and varResult is: "..tostring(varResult));
		local xpChange = math.max(math.floor(varResult*level),0.25)
		character:getXp():AddXP(Perks.Music, xpChange)
	end

	--INVISIBLE
	if character:isInvisible() then character:Say("PLAYER IS INVISIBLE: CAN'T PLAY SOUND"); end
end

local function getSandboxOptionMusicChance()
	local sandboxValue = SandboxVars.Music.LearningChance or 3
	local chance = 0
	if sandboxValue == 1 then chance = -2;
	elseif sandboxValue == 2 then chance = -1;
	elseif sandboxValue == 3 then chance = 0;
	elseif sandboxValue == 4 then chance = 2;
	elseif sandboxValue == 5 then chance = 5;
	end
	return chance
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

function PlayInstrumentPianoTraining:isValid()
	if self.character:getVehicle() or self.character:isSneaking() then return false; end
	return true
end

function PlayInstrumentPianoTraining:waitToStart()
	return false
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


function PlayInstrumentPianoTraining:update()
	if self.panicLevel and self.character:getMoodles():getMoodleLevel(MoodleType.Panic) > self.panicLevel then self.isFailState = true; end
	
	if self.isFailState or not self.piano or isKeyDown(Keyboard.KEY_E) or self.character:isSneaking() or not self.character:isSitOnGround() or #self.AvailableInstrumentTracks == 0 then
		self:forceStop()
	end

	if self.currentAction ~= 1 then
		self:setActionAnim(self.AnimToplay)
		self.currentAction = 1
	end

	local isPlaying = self.gameSound and self.gameSound ~= 0 and self.character:getEmitter():isPlaying(self.gameSound)
	if not isPlaying then
		local soundRadius, volume = 10, 5
		if self.character:isOutside() then soundRadius, volume = 30, 10; end
		local randomSound = ZombRand(#self.AvailableInstrumentTracks) + 1
		local randomTrack = self.AvailableInstrumentTracks[randomSound]
		local sound = randomTrack.sound
		if self.lastSound and self.lastSound == sound then
			randomSound = ZombRand(#self.AvailableInstrumentTracks) + 1
			randomTrack = self.AvailableInstrumentTracks[randomSound]
			sound = randomTrack.sound
			self.lastSound = sound
		end
		self.gameSound = self.character:getEmitter():playSound(sound)
		addSound(self.character,self.character:getX(),self.character:getY(),self.character:getZ(),soundRadius,volume)
	end
	
	self.actionCount = self.actionCount + (getGameTime():getGameWorldSecondsSinceLastUpdate()*GTLSCheck)
	if self.actionCount > self.actionTotal then
		self.actionCount = 0
		adjustStats(self.character, self.level)
		-- update for zombies
		local soundRadius, volume = 10, 5
		if self.character:isOutside() then soundRadius, volume = 30, 10; end
		addSound(self.character,self.character:getX(),self.character:getY(),self.character:getZ(),soundRadius,volume)
		
		if self.playerLevel >= self.level and self.playerLevel <= 5 then
			-- stress and level check
			local num1, num2 = getRandomChance(self.character, self.character:getStats():getStress(), self.stressLvls, self.baseStress)
			local randomchance = ZombRand(num1, num2)
			self.isFailState = getFailState(self.playerLevel, self.level, randomchance)
		end

		if not self.isFailState then
			if self.learnedSong then
				if not self.character:HasTrait("Virtuoso") then HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Boredom"), true, 255, 200, 200); end
				self.relearnCount = self.relearnCount + 1
				if self.relearnCount >= self.relearnTotal then
					self.relearnCount = 0
					self.learnedSong = false
				end
			else
				local randomDice = ZombRand(200)+1+self.sandboxAddChance
				local randomDiceTarget
				local trait = 0
				if self.character:HasTrait("Virtuoso") then
					trait = 1
				end
				if #self.PriorityToLearnLow > 0 then
					randomDiceTarget = (198 - (self.playerLevel+trait))
				elseif (#self.PriorityToLearnMid > 0) and (self.playerLevel > 2) then
					randomDiceTarget = (201 - (self.playerLevel+trait))
				elseif (#self.PriorityToLearnHigh > 0) and (self.playerLevel > 5) then
					randomDiceTarget = (205 - (self.playerLevel+trait))
				elseif (#self.PriorityToLearnMaster > 0) and (self.playerLevel > 8) then
					randomDiceTarget = (208 - (self.playerLevel+trait))
				end
				
				if randomDice and randomDiceTarget and (randomDice >= randomDiceTarget) and (self.playerLevel > 1) then
				
					local AvailableSong
					local randomSong
					local randomSongName
					
					
					if (#self.PriorityToLearnLow > 0) or (#self.PriorityToLearnMid > 0) or (#self.PriorityToLearnHigh > 0) or (#self.PriorityToLearnMaster > 0) then
						if #self.PriorityToLearnLow > 0 then
							AvailableSong = ZombRand(#self.PriorityToLearnLow) + 1
							randomSong = self.PriorityToLearnLow[AvailableSong]
						elseif #self.PriorityToLearnMid > 0 then
							AvailableSong = ZombRand(#self.PriorityToLearnMid) + 1
							randomSong = self.PriorityToLearnMid[AvailableSong]
						elseif #self.PriorityToLearnHigh > 0 then
							AvailableSong = ZombRand(#self.PriorityToLearnHigh) + 1
							randomSong = self.PriorityToLearnHigh[AvailableSong]
						elseif #self.PriorityToLearnMaster > 0 then
							AvailableSong = ZombRand(#self.PriorityToLearnMaster) + 1
							randomSong = self.PriorityToLearnMaster[AvailableSong]
						end
						
						randomSongName = randomSong.name
						if randomSongName then
							table.insert(self.learnedTracksData,randomSong)
							HaloTextHelper.addText(self.character, getText("IGUI_HaloNote_LearnSong"), 210, 210, 210)
							HaloTextHelper.addText(self.character, getText(randomSongName), 150, 255, 150)
							getSoundManager():playUISound("PZLevelSound")
							checkAmbitions(self.character, randomSong.level)
						end
						self.learnedSong = true
					end
					
					
					--if self.learnedTracksData then
					self.AvailableToLearn = {}
					self.PriorityToLearnLow = {}
					self.PriorityToLearnMid = {}
					self.PriorityToLearnHigh = {}
					self.PriorityToLearnMaster = {}
					local newSong
					for k,v in pairs(self.instrumentSounds) do
						if v.level <= self.playerLevel and v.isaddon ~= 2 then
							newSong = true
							if #self.learnedTracksData > 0 then
								for i,j in pairs(self.learnedTracksData) do
									if j.isaddon ~= 2 and j.name == v.name then
										newSong = false
										break
									end
								end
							end
							if newSong then
								table.insert(self.AvailableToLearn, v)
							end
						end
					end
						
					if #self.AvailableToLearn > 0 then
						for k,v in pairs(self.AvailableToLearn) do
							if v.level <= 2 then
								table.insert(self.PriorityToLearnLow, v)
							elseif v.level <= 5 then
								table.insert(self.PriorityToLearnMid, v)
							elseif v.level <= 8 then
								table.insert(self.PriorityToLearnHigh, v)
							elseif v.level <= 10 then
								table.insert(self.PriorityToLearnMaster, v)
							end
						end
					end
						
				else
					if not self.character:HasTrait("Virtuoso") then HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Boredom"), true, 255, 200, 200); end
				end
			end
		end

	end

	--Metabolics
	if self.doMetabolics then self.character:setMetabolicTarget(Metabolics.UsingTools); end --!
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

local function getInstrumentParams()
	return "PlayPianoTracks", "PianoLearnedTracks"
end

function PlayInstrumentPianoTraining:start()
	self["adjustPlayerPosition"](self)
	self.sandboxAddChance = getSandboxOptionMusicChance()

	self:setOverrideHandModels(nil, nil)

	self.characterData.PlayingInstrument = true
	
	getSoundManager():setMusicVolume(0)

	self.action:setUseProgressBar(true)
	--Animations
	self.AvailableAnims = {}
	for k,v in pairs(self.instrumentAnimations) do
		if self.playerLevel > 4 then
			if v.instrument == "piano" and v.level <= self.playerLevel and v.level >= (self.playerLevel - 4) then
				table.insert(self.AvailableAnims, v)
			end
		elseif v.instrument == "piano" and v.level <= self.playerLevel then
			table.insert(self.AvailableAnims, v)
		end
	end
	self.idxAnim = ZombRand(#self.AvailableAnims) + 1
	self.AnimToplay = self.AvailableAnims[self.idxAnim].name
	self.AnimTime = self.AvailableAnims[self.idxAnim].keyframes
	--Songs
	local fileName, instrumentData = getInstrumentParams()
	self.instrumentSounds = require("Instruments/Tracks/"..fileName)
	self.learnedTracksData = self.characterData[instrumentData]

	for k,v in pairs(self.instrumentSounds) do
		if v.level <= self.level and (self.level < 3 or (v.level+2 >= self.level)) and v.isaddon ~= 0 and v.isaddon ~= 1 then--MAKE SURE TO CHANGE THIS LINE
			table.insert(self.AvailableInstrumentTracks, v)
		end
	end

	if self.level > 1 and self.learnedTracksData then
	
		if #self.learnedTracksData > 0 then
			for k,v in pairs(self.learnedTracksData) do
				if v.level <= self.level and (self.level < 3 or (v.level+2 >= self.level)) then
					table.insert(self.AvailableInstrumentTracks, v)
				end
			end
		end
	end

	if #self.AvailableInstrumentTracks <= 3 then
		for k,v in pairs(self.instrumentSounds) do
			if v.level <= self.level and v.isaddon ~= 0 and v.isaddon ~= 1 then--MAKE SURE TO CHANGE THIS LINE
				table.insert(self.AvailableInstrumentTracks, v)
			end
		end
		if self.level > 1 and self.learnedTracksData then
			if #self.learnedTracksData > 0 then
			for k,v in pairs(self.learnedTracksData) do
				if v.level <= self.level and (self.level >= 3 and (v.level+2 < self.level)) then
					table.insert(self.AvailableInstrumentTracks, v)
				end
			end
		end
		end
	end
	
	if self.playerLevel >= 2 then
					
		if self.learnedTracksData then
			self.AvailableToLearn = {}
			local newSong
			for k,v in pairs(self.instrumentSounds) do
				if v.level <= self.playerLevel and v.isaddon ~= 2 then
					newSong = true
					if #self.learnedTracksData > 0 then
						for i,j in pairs(self.learnedTracksData) do
							if j.isaddon ~= 2 and j.name == v.name then
								newSong = false
								break
							end
						end
					end
					if newSong then
						table.insert(self.AvailableToLearn, v)
					end
				end
			end
						
			if #self.AvailableToLearn > 0 then
				for k,v in pairs(self.AvailableToLearn) do
					if v.level <= 2 then
						table.insert(self.PriorityToLearnLow, v)
					elseif v.level <= 5 then
						table.insert(self.PriorityToLearnMid, v)
					elseif v.level <= 8 then
						table.insert(self.PriorityToLearnHigh, v)
					elseif v.level <= 10 then
						table.insert(self.PriorityToLearnMaster, v)
					end
				end
			end

		else
			self.learnedTracksData = {}
			for k,v in pairs(self.instrumentSounds) do
				if v.level <= 2 and v.level <= self.playerLevel and v.isaddon ~= 2 then
					table.insert(self.PriorityToLearnLow, v)
				end
			end
		end
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

end

function PlayInstrumentPianoTraining:adjustPlayerPosition()
	-- keeping this as a temporary workaround for grand pianos as it works for both b41 and b42 (removing this for b42 soon)
	if LSUtil.pianoPos then return; end
	--if self.ogPos then self.character:setX(self.ogPos[1]); self.character:setY(self.ogPos[2]); return; end
	local vars = getPianoAdjPiece(self.spriteName)
	if not vars or not vars[3] or not vars[4] then return; end
	local sqr = self.character:getSquare()
	local x, y = sqr:getX(),sqr:getY()
	--self.ogPos = {x,y}
	self.character:setX(x+vars[3]); self.character:setY(y+vars[4])
	LSUtil.pianoPos = true
end

function PlayInstrumentPianoTraining:stop()

	self.characterData.PlayingInstrument = false

	if self.gameSound and self.gameSound ~= 0 and self.character:getEmitter():isPlaying(self.gameSound) then self.character:getEmitter():stopSound(self.gameSound); end

	if self.isFailState == true then
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

function PlayInstrumentPianoTraining:perform()

	self.characterData.PlayingInstrument = false

	if self.gameSound and self.gameSound ~= 0 and self.character:getEmitter():isPlaying(self.gameSound) then self.character:getEmitter():stopSound(self.gameSound); end

	adjustStats(self.character, self.level)

	getSoundManager():setMusicVolume(self.musicOriginalVolume)

    ISBaseTimedAction.perform(self);
end

local function isMetabolicsEnabled(option) --!
	local t = {
		[1] = true,
		[2] = true,
		[3] = false,
	}
	return t[option]
end

function PlayInstrumentPianoTraining:new(character, Piano, Type, SpriteName, level)
    local o = {}
    setmetatable(o, self)
    self.__index = self
    o.character = character;
	o.piano = Piano;
    o.stopOnWalk = true;
    o.stopOnRun = true;
    o.stopOnAim = true;
	o.ignoreHandsWounds = true;
	o.maxTime = 9000
	o.pianoType = Type
	o.AvailableAnims = 0
	o.instrumentAnimations = require("Instruments/InstrumentAnimations")
	o.instrumentSounds = false
	o.idxAnim = 0
	o.AnimToplay = 0
	o.AnimTime = 0
	o.gameSound = false
	o.level = level
	o.musicOriginalVolume = tonumber(getSoundManager():getMusicVolume())
	o.actionCount = 0
	o.actionTotal = 60--300
	o.AvailableInstrumentTracks = {}
	o.AvailableToLearn = {}
	o.lastSound = false
	o.learnedSong = true
	o.learnedTracksData = false
	o.PriorityToLearnLow = {}
	o.PriorityToLearnMid = {}
	o.PriorityToLearnHigh = {}
	o.PriorityToLearnMaster = {}
	o.songDifficulty = false
	o.noKnownSongs = false
	o.relearnCount = 0
	o.relearnTotal = 5
	o.sandboxAddChance = 0
	o.spriteName = SpriteName
	o.characterData = character:getModData()
	o.currentAction = 0
	o.panicLevel = false
	o.isFailState = false
	o.playerLevel = character:getPerkLevel(Perks.Music)
	o.stressLvls = false
	o.baseStress = 1
	o.doMetabolics = isMetabolicsEnabled(SandboxVars.Music.Metabolics or 1) --!
    return o;
end

return PlayInstrumentPianoTraining;
]]--