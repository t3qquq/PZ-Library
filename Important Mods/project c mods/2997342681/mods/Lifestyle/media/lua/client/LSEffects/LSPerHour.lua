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
--[[ --!
local function getPlayerCooldowns(moodle)

	if moodle then
	return {
		"TaughtSkill",
		"WasTaughtMeditation",
		"WasTaughtSkill",
		"AdviceWasted",
		"Attractive",
		"Nauseous",
		"SmellGood",
		"FTGood",
		"FTBad",
	}
	else
	return {
		"TeachCooldown",
		"LessonCooldown",
		"InteractionSpam",
		"mirrorPT",
		"mirrorCD",
		"brushmaster",
		"grimefighter",
		"unstoppable",
		"FortuneTeller",
	}
	end

end
]]--
local function doAmbitionsNote(thisPlayer)
	local text = " <CENTRE> "..getText("IGUI_T_Ambt_Note")
	local infoText = " <LINE><H1> "..getText("IGUI_T_Ambt_Title").." <LINE> ".." <CENTRE> <IMAGE:media/ui/tutorial/Ambt_01.png,300,200> <LINE><LINE><TEXT> "..getText("IGUI_T_Ambt_Body").." <LINE><LINE> "..getText("IGUI_T_Ambt_Body2")
	.." <LINE><LINE> "..getText("IGUI_T_Ambt_Body3").." <LINE><LINE> "..getText("IGUI_T_Ambt_Body4").." <LINE><LINE> "..getText("IGUI_T_Ambt_Body5").." <LINE><LINE> "..getText("IGUI_T_Ambt_Body6")
	local args = {
		text,
		"tutorialAmbt",
		'media/ui/Ambitions/Ambitions_RO.png',
		5,
		"noteAmbt",
		infoText,
		true,
		{5,9,32}
	}
	LSUtil.doNote(thisPlayer, args)
end

local function doAmbitionsCooldown(thisPlayer, playerData)
	local items = playerData.Ambitions
	for k, v in pairs(items) do
		if playerData.Ambitions[v.name] and (not v.disable) and v.cd and (v.cd > 0) then
			playerData.Ambitions[v.name].cd = v.cd - 1
		end
	end
	if SandboxVars.LSAmbt.Toggle and not thisPlayer:isAsleep() and ZombRand(4) == 1 and thisPlayer:getHoursSurvived()/24 > 7 then
		doAmbitionsNote(thisPlayer)
	end
end

local function doPlayerCooldowns(playerData)

	--print("doPlayerCooldowns called")

	local t = LSUtil.getPlayerCooldowns(60) --!

	for n=1,#t[2] do --!
		local value = t[2][n] --!
		--print("doPlayerCooldowns checking cooldown for " .. value)
		if not playerData.LSCooldowns then playerData.LSCooldowns = {}; end
		if not playerData.LSCooldowns[value] then
			playerData.LSCooldowns[value] = 0
		end
		if playerData.LSCooldowns[value] and (playerData.LSCooldowns[value] > 0) then
			playerData.LSCooldowns[value] = playerData.LSCooldowns[value] - 1
			--print("doPlayerCooldowns reducing cooldown by 1 for " .. value)
		end
		if playerData.LSCooldowns[value] and (playerData.LSCooldowns[value] < 0) then
			playerData.LSCooldowns[value] = 0
		end
	end

	--t = LSUtil.getPlayerCooldowns(60) --!
	for n=1,#t[1] do --!
		local value = t[1][n] --!
		if playerData.LSMoodles[value] and (playerData.LSMoodles[value].Value > 0) then
			playerData.LSMoodles[value].Value = playerData.LSMoodles[value].Value - 0.2
		end
		if playerData.LSMoodles[value] and (playerData.LSMoodles[value].Value < 0.2) then
			playerData.LSMoodles[value].Value = 0
		end
	end


end

local function doPlayerCooldownsSimple(playerData)
	playerData.TDcomplained = false
	playerData.CurrentBedQuality = false
end

local function severityTable(sev)
	t = {
		k1 = {5, 11},
		k2 = {10, 21},
		k3 = {15, 41},
		k4 = {20, 61},
	}
	return t[sev]
end

local function HNgetColdSeverity()
	local sevSO = SandboxVars.LSHygiene.ColdSeverity or 2
	severity = severityTable("k"..tostring(sevSO))
	return ZombRand(severity[1], severity[2])
end

local function HNdoCatchCold(player)
	local severity = HNgetColdSeverity()
	player:getBodyDamage():setCatchACold(0.0);
	player:getBodyDamage():setHasACold(true);
	player:getBodyDamage():setColdStrength(severity);
	player:getBodyDamage():setTimeToSneezeOrCough(0);
end

local function HNdoColdChance(player)
	if not player or not player:getBodyDamage() or player:getBodyDamage():isHasACold() then return; end --! attention: check method for b42
	local chance, chanceMulti = 100, SandboxVars.LSHygiene.ColdChanceMultiplier or 0
	if chanceMulti <= 0 then return; end
	chance = chance/chanceMulti
	if chance == 0 then return false; end
	if player:HasTrait("Resilient") then
		chance = chance*1.2
	elseif player:HasTrait("ProneToIllness") then
		chance = chance*0.8
	end
	if ZombRand(math.floor(chance)) == 0 then HNdoCatchCold(player); end
end

local function getTraitsTable()
	return {
		{trait="Artistic",traitsN={"Artistic"},skill="Art",level=6,add=true,mod="DividerArt"},
		{trait="Tidy",traitsN={"Tidy","Sloppy"},skill="Cleaning",level=6,add=true,mod="DividerHygiene"},
		{trait="Sloppy",traitsN=false,skill="Cleaning",level=4,add=false,mod="DividerHygiene"},
		{trait="Disciplined",traitsN={"Disciplined","CouchPotato","Overweight","Obese","Out of Shape","Unfit"},skill="Meditation",level=8,add=true,mod="DividerMeditationNew"},
		{trait="CouchPotato",traitsN={"Overweight","Obese"},skill="Meditation",level=4,add=false,mod="DividerMeditationNew"},
		{trait="Virtuoso",traitsN={"Virtuoso","ToneDeaf","Deaf","HardOfHearing"},skill="Music",level=8,add=true,mod="DividerMusicNew"},
		{trait="ToneDeaf",traitsN=false,skill="Music",level=4,add=false,mod="DividerMusicNew"},
		{trait="PartyAnimal",traitsN={"PartyAnimal","Killjoy","Deaf"},skill="Dancing",level=8,add=true,mod="DividerDancingNew"},
		{trait="Killjoy",traitsN=false,skill="Dancing",level=4,add=false,mod="DividerDancingNew"},
	}

end

local function getCanLosePos(thisPlayer, trait, added)
	if not added or not thisPlayer:HasTrait(trait) then return false;
	elseif SandboxVars.LS.DynamicTraitsReverse == 2 then return true;
	elseif SandboxVars.LS.DynamicTraitsReverse == 3 then return thisPlayer:getModData().LSDLT[trait]; end
	return false
end

local function doSatisfyTraitCheck(thisPlayer)
	if not SandboxVars.LS.DynamicTraits then return; end
	if not thisPlayer:getModData().LSDLT then thisPlayer:getModData().LSDLT = {}; end
	local traits, badOutcome, t = false, false, getTraitsTable()
	for k, v in pairs(t) do
		local hasLevel, canLosePos = thisPlayer:getPerkLevel(Perks[v.skill]) >= v.level, getCanLosePos(thisPlayer, v.trait, v.add)
		if (not v.mod or SandboxVars.Text[v.mod]) and (v.add or thisPlayer:HasTrait(v.trait)) and (hasLevel or canLosePos) then
			local incompatible, isAdd = false, v.add
			if canLosePos and not hasLevel then isAdd = false; badOutcome = true;
			elseif v.traitsN then
				for n=1, #v.traitsN do
					if thisPlayer:HasTrait(v.traitsN[n]) then incompatible = true; break; end
				end
			end
			if not incompatible then if not traits then traits = {}; end; table.insert(traits, {trait=v.trait,add=isAdd}); end
		end
	end
	if traits then
		local textAdded, textRemoved = "", ""
		for k, v in pairs(traits) do
			local traitLower = string.lower(v.trait)
			if v.add then thisPlayer:getTraits():add(v.trait); textAdded = textAdded..", "..getText("UI_trait_"..traitLower); thisPlayer:getModData().LSDLT[v.trait] = true;
			else thisPlayer:getTraits():remove(v.trait); textRemoved = textRemoved..", "..getText("UI_trait_"..traitLower); thisPlayer:getModData().LSDLT[v.trait] = false; end
		end
		if textAdded ~= "" then textAdded = getText("IGUI_HaloNote_LSDynamicTraitAdd")..textAdded; HaloTextHelper.addText(thisPlayer, textAdded, 30, 190, 240); end
		if textRemoved ~= "" then textRemoved = getText("IGUI_HaloNote_LSDynamicTraitRemove")..textRemoved; HaloTextHelper.addText(thisPlayer, textRemoved, 30, 190, 240); end
		if not badOutcome then getSoundManager():playUISound("PZLevelSound"); end
	end
end

function LSEveryHour()

	local thisPlayer = getPlayer()

	if thisPlayer and not thisPlayer:isDead() then
		local playerData = thisPlayer:getModData()
		local bodyDamage = thisPlayer:getBodyDamage()
		local stats = thisPlayer:getStats()
		--local currentBoredom = bodyDamage:getBoredomLevel()
		--local currentUnhappiness = bodyDamage:getUnhappynessLevel()
		--local currentStress = stats:getStress();

		--print("trying to call doPlayerCooldowns")
		doPlayerCooldowns(playerData)
		doPlayerCooldownsSimple(playerData)
		doAmbitionsCooldown(thisPlayer, playerData)
		doSatisfyTraitCheck(thisPlayer)
		
		if SandboxVars.Text.DividerHygiene and playerData.hygieneNeed > 80 then HNdoColdChance(thisPlayer); end

	if playerData.LSMoodles["MintFresh"] and playerData.LSMoodles["MintFresh"].Value and playerData.LSMoodles["MintFresh"].Value > 0 then
		if playerData.lastBrushTeeth and (playerData.lastBrushTeeth + 3) <= thisPlayer:getHoursSurvived() then
			playerData.LSMoodles["MintFresh"].Value = 0
		end
	end
	if playerData.LSMoodles["BathHot"] and playerData.LSMoodles["BathHot"].Value and playerData.LSMoodles["BathHot"].Value > 0 then
		if playerData.lastBath and (playerData.lastBath + 1) <= thisPlayer:getHoursSurvived() then
			playerData.LSMoodles["BathHot"].Value = 0
		end
	end
	if playerData.LSMoodles["BathCold"] and playerData.LSMoodles["BathCold"].Value and playerData.LSMoodles["BathCold"].Value > 0 then
		if playerData.lastBath and (playerData.lastBath + 1) <= thisPlayer:getHoursSurvived() then
			playerData.LSMoodles["BathCold"].Value = 0
		end
	end

		if playerData.FitnessActivityMuscles ~= nil and playerData.DidFitnessActivity ~= nil then
		
		else
			playerData.FitnessActivityMuscles = 0
			playerData.DidFitnessActivity = 0
		end
		
		if playerData.DidFitnessActivity ~= nil and playerData.DidFitnessActivity ~= 0 and thisPlayer:getPerkLevel(Perks.Fitness) <= 8 then
			playerData.DidFitnessActivity = playerData.DidFitnessActivity + 1
		end

		if playerData.DidFitnessActivity ~= nil and playerData.DidFitnessActivity >= 36 then
			playerData.DidFitnessActivity = 0
			playerData.FitnessActivityMuscles = 0
		end
		--print("didfitnessactivity is ".. playerData.DidFitnessActivity)
		--print("fitnessactivitymuscles is ".. playerData.FitnessActivityMuscles)
		if playerData.DidFitnessActivity ~= nil and playerData.DidFitnessActivity == 12 then
			local LowerLegL = bodyDamage:getBodyPart(BodyPartType.LowerLeg_L):getStiffness()
			local UpperLegL = bodyDamage:getBodyPart(BodyPartType.UpperLeg_L):getStiffness()
			local LowerLegR = bodyDamage:getBodyPart(BodyPartType.LowerLeg_R):getStiffness()
			local UpperLegR = bodyDamage:getBodyPart(BodyPartType.UpperLeg_R):getStiffness()
			local TorsoLower = bodyDamage:getBodyPart(BodyPartType.Torso_Lower):getStiffness()
			--print("stiffness was ".. bodyDamage:getBodyPart(BodyPartType.LowerLeg_L):getStiffness())
			if playerData.FitnessActivityMuscles >= 200 then--severe
				bodyDamage:getBodyPart(BodyPartType.LowerLeg_L):setStiffness(LowerLegL + 90)
				bodyDamage:getBodyPart(BodyPartType.UpperLeg_L):setStiffness(UpperLegL + 90)
				bodyDamage:getBodyPart(BodyPartType.LowerLeg_R):setStiffness(LowerLegR + 90)
				bodyDamage:getBodyPart(BodyPartType.UpperLeg_R):setStiffness(UpperLegR + 90)
				bodyDamage:getBodyPart(BodyPartType.Torso_Lower):setStiffness(TorsoLower + 90)
			elseif playerData.FitnessActivityMuscles >= 150 then--mild
				bodyDamage:getBodyPart(BodyPartType.LowerLeg_L):setStiffness(LowerLegL + 60)
				bodyDamage:getBodyPart(BodyPartType.UpperLeg_L):setStiffness(UpperLegL + 60)
				bodyDamage:getBodyPart(BodyPartType.LowerLeg_R):setStiffness(LowerLegR + 60)
				bodyDamage:getBodyPart(BodyPartType.UpperLeg_R):setStiffness(UpperLegR + 60)
				bodyDamage:getBodyPart(BodyPartType.Torso_Lower):setStiffness(TorsoLower + 60)
			elseif playerData.FitnessActivityMuscles >= 50 then--low
				bodyDamage:getBodyPart(BodyPartType.LowerLeg_L):setStiffness(LowerLegL + 30)
				bodyDamage:getBodyPart(BodyPartType.UpperLeg_L):setStiffness(UpperLegL + 30)
				bodyDamage:getBodyPart(BodyPartType.LowerLeg_R):setStiffness(LowerLegR + 30)
				bodyDamage:getBodyPart(BodyPartType.UpperLeg_R):setStiffness(UpperLegR + 30)
				bodyDamage:getBodyPart(BodyPartType.Torso_Lower):setStiffness(TorsoLower + 30)
			else
				playerData.DidFitnessActivity = 0
				playerData.FitnessActivityMuscles = 0
			end
			--print("stiffness is ".. bodyDamage:getBodyPart(BodyPartType.LowerLeg_L):getStiffness())
			if bodyDamage:getBodyPart(BodyPartType.LowerLeg_L):getStiffness() > 100 then
				bodyDamage:getBodyPart(BodyPartType.LowerLeg_L):setStiffness(100)
			end
			if bodyDamage:getBodyPart(BodyPartType.UpperLeg_L):getStiffness() > 100 then
				bodyDamage:getBodyPart(BodyPartType.UpperLeg_L):setStiffness(100)
			end
			if bodyDamage:getBodyPart(BodyPartType.LowerLeg_R):getStiffness() > 100 then
				bodyDamage:getBodyPart(BodyPartType.LowerLeg_R):setStiffness(100)
			end
			if bodyDamage:getBodyPart(BodyPartType.UpperLeg_R):getStiffness() > 100 then
				bodyDamage:getBodyPart(BodyPartType.UpperLeg_R):setStiffness(100)
			end
			if bodyDamage:getBodyPart(BodyPartType.Torso_Lower):getStiffness() > 100 then
				bodyDamage:getBodyPart(BodyPartType.Torso_Lower):setStiffness(100)
			end
		end
		

	end
end

Events.EveryHours.Add(LSEveryHour);
