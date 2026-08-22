--***********************************************************
--** PseudonymousEd, the Dev
--** Simple Playable Pianos 41
--** Play the pianos for boredom reduction
--***********************************************************

require "TimedActions/ISBaseTimedAction"
require 'XpSystem/ISUI/extraskills'

SPPPlayPiano = ISBaseTimedAction:derive("SPPPlayPiano");

local function adjustStats(character, currentDelta, deltaIncrease, boredomReduction, stressReduction, totalXP)
	--print("PseudoEdSPP: ADJUSTING stats")
	--print("PseudoEdSPP: currentdelta = " .. currentDelta)
	--print("Initial Boredom: " .. initialBoredom)
	--print("Initial Unhappiness: " .. initialUnhappiness)
	--print("Initial Stress: " .. initialStress)
		
	local boredomChange = currentDelta * boredomReduction

	local bodyDamage = character:getBodyDamage()
	
	bodyDamage:setBoredomLevel(initialBoredom - boredomChange)
	local stressChange = currentDelta * stressReduction

	bodyDamage:setUnhappynessLevel(initialUnhappiness - stressChange)

	character:getStats():setStress(initialStress - stressChange)

	local xpChange = deltaIncrease * totalXP
	
	if getCore():getVersionNumber() == "41.50 - IWBUMS" then
		extraskills.addXP(character, "Piano", xpChange)
		--character:Say("Old skill system adding " .. xpChange)
	else
		-- Multiply by 4 because the vanilla system assumes 25% xp
		-- Unless you have traits to improve it.
		xpChange = xpChange * 4
		--character:Say("New skill system adding " .. xpChange)
		--print("PseudoEdSPP: Skill system adding  " .. xpChange)
		character:getXp():AddXP(Perks.PseudonymousEdPiano, xpChange)
	end
	--print("PseudoEdSPP: Stats after adjustStats " .. deltaIncrease);
	--print("Boredom: " .. bodyDamage:getBoredomLevel())
	--print("Unhappiness: " .. bodyDamage:getUnhappynessLevel())
	--print("Stress: " .. character:getStats():getStress())

	-- DO NOT Uncomment this line for debugging without adding version check
	--local xp = character:getModData().skills["Piano"].xp;
	--print("Piano XP: " .. xp)
end

local function insertSession(character, isPractice, lengthSeconds, xp, delta)

	local characterData = character:getModData()
	local sessionsTable = characterData.SPPsessions
	
	-- Print Session Table
--	local n = #sessionsTable

--	print("PseudoEdSPP: sessions table size is ".. n)

--	print("PseudoEdSPP: Sessions Table info")
--	for i = 1,n do
--		print("-----")
--		--if sessionsTable[i].isPractice then
--			if sessionsTable[i].isPractice == true then
--				print("Practice session")
--			else
--				print("Performance")
--			end
--		--end
--		
--		if sessionsTable[i].length then
--			print("Length = " .. sessionsTable[i].length)
--		end
--		if sessionsTable[i].xp then
--			print("xp = " .. sessionsTable[i].xp)
--		end
--		
--		if sessionsTable[i].hoursSince then
--			print("Hours since = " .. sessionsTable[i].hoursSince)
--		end
--		
--	end
		
	local session = {}
	session.hoursSince = 0
	session.isPractice = isPractice
	session.length = lengthSeconds / 48 * delta
	session.xp = xp
	table.insert(sessionsTable,session)
end


function SPPPlayPiano:isValid()
	return true;
end

function SPPPlayPiano:waitToStart()
	self.character:faceThisObject(self.piano);
	return self.character:shouldBeTurning();
end

function SPPPlayPiano:update()
	
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
		local soundRadius = 30
		local volume = 12

		-- Use the emitter because it emits sound in the world (zombies can hear)
		self.gameSound = self.character:getEmitter():playSound(self.soundFile);
		
		addSound(self.character,
				 self.character:getX(),
				 self.character:getY(),
				 self.character:getZ(),
				 soundRadius,
				 volume)
		
	end
	
	local currentDelta = self:getJobDelta()
	local deltaIncrease = currentDelta - self.deltaTabulated
	-- Update at every 0.05 delta milestone
	if deltaIncrease > 0.05 then

		adjustStats(self.character, currentDelta, deltaIncrease, self.boredomReduction, self.stressReduction, self.xp)
		
		self.deltaTabulated = currentDelta

	end
	

	self.character:faceThisObject(self.piano);
end

function SPPPlayPiano:start()
	
	local actionType = self.actionType
	--print("PseudoEdSPP: Programmed action " .. actionType)

	self:setActionAnim(actionType)
	-- Loot is used as a backup action, so keep this
	self.character:SetVariable("LootPosition", "Mid")

	self:setOverrideHandModels(nil, nil)

	-- Some notes to begin
	local seconds = self.maxTime / 48
--	print("************************************************************")
--	print("********** PseudoEdSPP Playing a piece")
	
--	print("length seconds = " .. seconds)
--	print("xp = " .. self.xp)
--	print("Boredom Reduction = " .. self.boredomReduction)
--	print("Stress reduction = " .. self.stressReduction)
	
	local bodyDamage = self.character:getBodyDamage()
	initialBoredom = bodyDamage:getBoredomLevel()
	initialUnhappiness = bodyDamage:getUnhappynessLevel()
	initialStress = self.character:getStats():getStress()

	recentPracticeXPModifier = 1
	recentPerformanceXPModifier = 1
	recentPerformanceMoodModifier = 1

--	print("Initial Boredom: " .. initialBoredom)
--	print("Initial Unhappines: " .. initialUnhappiness)
--	print("Initial Stress: " .. initialStress)

--	print("PseudoEdSPP: At start of piece, here is session info:")
	local characterData = self.character:getModData()
	local sessionsTable = characterData.SPPsessions
	
	-- Session Table info
	local n = #sessionsTable

	local totalPracticeLength = 0
	local totalPerformanceLength = 0
	local recentXPGains = 0
--	print("PseudoEdSPP: sessions table size is ".. n)

--	print("PseudoEdSPP: Sessions Table info")
	-- Check table for overplaying during recent sessions
	for i = 1,n do
		if sessionsTable[i].length then
			if sessionsTable[i].isPractice then
				totalPracticeLength = totalPracticeLength + sessionsTable[i].length
			else
				totalPerformanceLength = totalPerformanceLength + sessionsTable[i].length
			end
			if sessionsTable[i].xp then
				recentXPGains = recentXPGains + sessionsTable[i].xp
			end
		end
	end

--	print("PseudoEdSPP: Total practice seconds = " .. totalPracticeLength)
--	print("PseudoEdSPP: Total performance seconds = " .. totalPerformanceLength)
--	print("PseudoEdSPP: Recent xp gains = " .. recentXPGains)

	if recentXPGains > 3000 then
--		print("PseudoEdSPP: Too many xp gained recently.")
		self.xp = 0;
	end
	
	if self.isPractice then
		if totalPracticeLength > 360 then
			--print("PseudoEdSPP:  Far too much practice recently")
			recentPracticeXPModifier = 0
			self.xp = 0
			self.boredomReduction = self.boredomReduction * 2
			self.stressReduction = self.stressReduction * 2
		elseif totalPracticeLength > 180 then
			--print("PseudoEdSPP:  a bit too much practice recently")
			recentPracticeXPModifier = 0.5
			self.xp = self.xp * 0.5
			self.boredomReduction = self.boredomReduction * 1.5
			self.stressReduction = self.stressReduction * 1.5
		else
			recentPracticeXPModifier = 1
		end
	else
		if totalPerformanceLength > 1200 then
			--print("PseudoEdSPP:  Far too much performance recently")
			self.xp = 0
			self.boredomReduction = self.boredomReduction * 0.1
			self.stressReduction = self.stressReduction * 0.1
			
			recentPerformanceXPModifier = 0.5
			recentPerformanceMoodModifier = 0.5
		elseif totalPerformanceLength > 800 then
			--print("PseudoEdSPP:  A bit too much performance recently")
			self.xp = self.xp * 0.5
			self.boredomReduction = self.boredomReduction * 0.5
			self.stressReduction = self.stressReduction * 0.5
			recentPerformanceXPModifier = 1
			recentPerformanceMoodModifier = 1
		end
	end

end

function SPPPlayPiano:stop()
	-- Make sure game sound has stopped
	if self.gameSound and
		self.gameSound ~= 0 and
		self.character:getEmitter():isPlaying(self.gameSound) then
		self.character:getEmitter():stopSound(self.gameSound);
	end
	
	-- Based on the Delta and piece level
	-- calculate XP and Boredom/Unhappiness/Stress changes
	
	local currentDelta = self:getJobDelta()
	local deltaIncrease = currentDelta - self.deltaTabulated

	adjustStats(self.character, currentDelta, deltaIncrease, self.boredomReduction, self.stressReduction, self.xp)

	self.deltaTabulated = currentDelta

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

	insertSession(self.character, self.isPractice, self.maxTime, self.xp, currentDelta)
	
	-- needed to remove from queue / start next.
	ISBaseTimedAction.stop(self);
	
end

function SPPPlayPiano:perform()
	-- Make sure game sound has stopped
	if self.gameSound and
		self.gameSound ~= 0 and
		self.character:getEmitter():isPlaying(self.gameSound) then
		self.character:getEmitter():stopSound(self.gameSound);
	end

	local currentDelta = self:getJobDelta()
	local deltaIncrease = currentDelta - self.deltaTabulated

	adjustStats(self.character, currentDelta, deltaIncrease, self.boredomReduction, self.stressReduction, self.xp)
	self.deltaTabulated = currentDelta
	
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

	insertSession(self.character, self.isPractice, self.maxTime, self.xp, 1)
	
	ISBaseTimedAction.perform(self);
end


function SPPPlayPiano:new(character, piano, sound, length, level, isPractice, xp, boredomReduction, stressReduction, actionType)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.piano = piano
	o.soundFile = sound
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = length
	o.gameSound = 0
	o.level = level
	o.isPractice = isPractice;
	o.xp = xp
	o.boredomReduction = boredomReduction
	o.stressReduction = stressReduction
	o.deltaTabulated = 0
	o.actionType = actionType
	return o;
end
