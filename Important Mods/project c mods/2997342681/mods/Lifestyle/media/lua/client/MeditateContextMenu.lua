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
require 'ISUI/ISWorldObjectContextMenu'

LSMeditateContextMenu = {}

LSMeditateContextMenu.onAction = function(worldobjects, player, soundFile, length, level, xp, boredomReduction, stressReduction, neckPain, actionType)
    ISTimedActionQueue.clear(player)
	forceDropHeavyItems(player)
	player:setPrimaryHandItem(nil)
	player:setSecondaryHandItem(nil)
	
	-- take off worn bags if character level is low
	if player:getPerkLevel(Perks.Meditation) < 3 then
		for i=0,player:getWornItems():size()-1 do
			local item = player:getWornItems():get(i):getItem();
			if item and instanceof(item, "InventoryContainer") then
				ISTimedActionQueue.add(ISUnequipAction:new(player, item, 50));
				return
			end
		end
	end
	
	
	local isSitOnGround = player:isSitOnGround()
	if not isSitOnGround then
	return
	end
    ISTimedActionQueue.add(MeditateAction:new(player, soundFile, length, level, xp, boredomReduction, stressReduction, neckPain, actionType))
end

LSMeditateContextMenu.doBuildMenu = function(player, context, worldobjects, DebugBuildOption)
    --local USE_CUSTOM_ANIMATION = true
    local player = getSpecificPlayer(player)
    local isSitOnGround = player:isSitOnGround()

    if player:getVehicle() then return end

    if not isSitOnGround then return end
	
	if player:getModData().IsMeditating or player:getModData().IsSittingOnSeat then return; end
	
	local soundFile = nil;
	local actionType = nil;
	local length = 100;
	local sound = "defaultsound"
	local level = -1
	
	level = player:getPerkLevel(Perks.Meditation)
	
	--XP calc
	local xp = 0
	
	if level >= 10 then
		xp = 0
	elseif level >=8 then
		xp = 99
	elseif level >=6 then
		xp = 54
	elseif level >=4 then
		xp = 36
	else
		xp = 9
	end
	
	--Stress and Boredom calc
	
	local boredomReduction = 3
	local stressReduction = 3
	local neckPain = 1
	local randomNumber = ZombRand(10, 20)
	local randomNumberInter = ZombRand(5, 10)
	local randomNumberAdvan = ZombRand(1, 5)

	local StressMultiplier = 1

		if SandboxVars.Meditation.StressReduction ~= nil then
			if SandboxVars.Meditation.StressReduction == 1 then
				StressMultiplier = 0.2
			elseif SandboxVars.Meditation.StressReduction == 2 then
				StressMultiplier = 1
			elseif SandboxVars.Meditation.StressReduction == 3 then
				StressMultiplier = 3
			end
		end

		if level == 10 then
			length = 30000
			boredomReduction = 15
			stressReduction = 2 * StressMultiplier
			neckPain = 0
		elseif level >= 8 then
			length = 20000
			boredomReduction = 12
			stressReduction = 1.5 * StressMultiplier
			neckPain = 0
		elseif level >= 6 then
			length = 12000
			boredomReduction = 8
			stressReduction = 1 * StressMultiplier
			neckPain = 0
		elseif level >= 4 then
			length = 8000
			boredomReduction = 6
			stressReduction = 0.5 * StressMultiplier
			neckPain = 1 + randomNumberAdvan
		elseif level >= 2 then
			length = 2800
			boredomReduction = 3
			stressReduction = 0.3 * StressMultiplier
			neckPain = 1 + randomNumberInter
		else
			length = 2000
			boredomReduction = 0
			stressReduction = 0.1 * StressMultiplier
			neckPain = 1 + randomNumber
		
		
	end
	
	--Anim and (sound) defined by level 
	actionType = "Bob_meditating"

	local randomNumberAnim = ZombRand(1, 100)
	
	--if USE_CUSTOM_ANIMATION == true then
		if (level >= 10) and (not SandboxVars.LSMeditation.RemoveLevitation) then
			actionType = "Bob_meditatingMaster"
			sound = "master"
		elseif (level >= 8) and (not SandboxVars.LSMeditation.RemoveLevitation) then
			if randomNumberAnim >= 98 then
				actionType = "Bob_meditatingMaster" -- small chance to play master, might remove or tweak later
				sound = "master"
			elseif randomNumberAnim >= 1 then
				actionType = "Bob_meditatingAdvanced"
				sound = "Advanced"
			end
		elseif level >= 6 then
			if (randomNumberAnim >= 98) and (not SandboxVars.LSMeditation.RemoveLevitation) then
				actionType = "Bob_meditatingAdvanced" -- small chance to play advanced, might remove or tweak later
				sound = "Advanced"
			elseif randomNumberAnim >= 64 then
				actionType = "Bob_meditating"
			elseif randomNumberAnim >= 34 then
				actionType = "Bob_meditatingB"
			elseif randomNumberAnim >= 1 then
				actionType = "Bob_meditatingC"
			end
		elseif level >= 4 then
			if randomNumberAnim >= 64 then
				actionType = "Bob_meditating"
			elseif randomNumberAnim >= 34 then
				actionType = "Bob_meditatingB"
			elseif randomNumberAnim >= 1 then
				actionType = "Bob_meditatingC"
			end
		elseif level >= 2 then
			if randomNumberAnim >= 74 then
				actionType = "Bob_meditatingInterC"
			elseif randomNumberAnim >= 44 then
				actionType = "Bob_meditatingInterA"
			elseif randomNumberAnim >= 1 then
				actionType = "Bob_meditatingInterB"
			end
		else
			-- actionType = "Bob_meditatingAdvanced" -- comment this out! debug testing animation purpose
			if randomNumberAnim >= 84 then --if block is commented out, comment back in
				actionType = "Bob_meditatingBeginner" --if block is commented out, comment back in
			elseif randomNumberAnim >= 44 then
				actionType = "Bob_meditatingBeginnerB" --if block is commented out, comment back in
			elseif randomNumberAnim >= 1 then
				actionType = "Bob_meditatingBeginnerC" --if block is commented out, comment back in
			end
	
		end
	
	--end
	
	local contextMenu = "ContextMenu_Meditate"
	local contextMenuBored = "ContextMenu_Meditate_Bored"
	local meditateOption = context:addOption(getText(contextMenu), worldobjects, LSMeditateContextMenu.onAction, player, sound, length, level, xp, boredomReduction, stressReduction, neckPain, actionType);
	if meditateOption.notAvailable == true then
	meditateOption.iconTexture = getTexture('media/ui/meditationNo_icon.png')
	else
	meditateOption.iconTexture = getTexture('media/ui/meditation_icon.png')
	end
	
	local tooltip = ISToolTip:new();
		tooltip:initialise();
		tooltip:setVisible(false);

	if (player:getBodyDamage():getBoredomLevel() > 30) then
		meditateOption.notAvailable = true;
		description = " <RED>" .. getText(contextMenuBored);
		tooltip.description = description
		meditateOption.toolTip = tooltip

	end
end
]]--