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
--[[
require 'ISUI/ISWorldObjectContextMenu'

LSYogaContextMenu = {}

--perhaps move this to TA
local function getSRMulti(sandboxOption)
	local option = sandboxOption or 2
	local t = {
		m1 = 0.2,
		m2 = 1,
		m3 = 3,
	}
	return t["m"..tostring(option)] or 1
end

local function getActionValues(level)
	--pose type / added chance to fail (adds to pose nat fail chance roll) / yoga effects / pose limit
	--xp is gained by completing poses (xp amount is decided by specific pose), failing a pose grants no xp
	local t = {
		l0 = {"Beginner",50,1,2},
		l1 = {"Beginner",50,1,2},
		l2 = {"Beginner",40,1,3},
		l3 = {"Intermediate",40,2,3},
		l4 = {"Intermediate",30,2,4},
		l5 = {"Intermediate",30,2,4},
		l6 = {"Advanced",30,3,6},
		l7 = {"Advanced",20,3,6},
		l8 = {"Advanced",20,3,8},
		l9 = {"Master",10,4,10},
		l10 = {"Master",0,4,10},
	}
	return t["l"..tostring(level)]
end

local function isOptionValid(thisPlayer)
	if thisPlayer:getVehicle() or thisPlayer:hasTimedActions() or thisPlayer:getModData().IsSittingOnSeat or
	thisPlayer:isSitOnGround() then return false; end
	return true
end

local function doRemoveBags(thisPlayer)
	for i=0,thisPlayer:getWornItems():size()-1 do
		local item = thisPlayer:getWornItems():get(i):getItem();
		if item and instanceof(item, "InventoryContainer") then
			ISTimedActionQueue.add(ISUnequipAction:new(thisPlayer, item, 50));
			--return
		end
	end
end

local function doNote(character, texture)
	local text = " <CENTRE> "..getText("IGUI_T_Yoga_Note")
	local infoText = " <LINE><H1> "..getText("IGUI_T_Yoga_Title").." <LINE> ".." <CENTRE> <IMAGE:media/ui/tutorial/Yoga_01.png,300,200> <LINE><LINE><TEXT> "..getText("IGUI_T_Yoga_Body").." <LINE><LINE> "..getText("IGUI_T_Yoga_Body2").." <LINE><LINE> "..getText("IGUI_T_Yoga_Body3").." <LINE><LINE> "..getText("IGUI_T_Yoga_Body4")
	.." <LINE><LINE> "..getText("IGUI_T_Yoga_Body5").." <LINE><LINE> "..getText("IGUI_T_Yoga_Body6").." <LINE><LINE> "..getText("IGUI_T_Yoga_Body7").." <LINE><LINE> "..getText("IGUI_T_Yoga_Body8").." <LINE><LINE> "..getText("IGUI_T_Yoga_Body9")
	LSNoteMng.addToQueue(getCore():getScreenWidth()-400,(getCore():getScreenHeight()/5)-50,300,50, {character, text, "tutorialYoga", texture, 4, "noteYoga", infoText, true, {5,9,32}}) -- player, mainText, queueType, tex, time, closePerm, infoPanel, noSpam, TextureCustomProps(w,h,size)
end

LSYogaContextMenu.onAction = function(worldobjects, thisPlayer)
	if not isOptionValid(thisPlayer) then return; end
    ISTimedActionQueue.clear(thisPlayer)--do NOT trigger this during a timed action's perform - results in unexpected behavior
	forceDropHeavyItems(thisPlayer)
	if not SandboxVars.Yoga.KeepBags then doRemoveBags(thisPlayer); end
	local skill = HiddenSkills.getSkill(thisPlayer, "Yoga")
	if not skill or not skill[1] or not skill[2] or not skill[3] then HiddenSkills.resetSkill(thisPlayer, "Yoga"); print("---------- WARNING: LSYogaContextMenu.onAction - FAILED TO GET YOGA LEVEL, RUNNING SKILL RESET, RETURNING..."); return; end
	
	local actionProperties = getActionValues(math.floor(skill[1]))
	
	doNote(thisPlayer, 'media/ui/yoga_icon.png')
	
    ISTimedActionQueue.add(LSYogaAction:new(thisPlayer, skill[1], actionProperties))
end

local function getIsEmbarrassed(embarrassment, key)
	local t = {
		[1] = false,
		[2] = 1,
		[3] = 2,
		[4] = 3,
	}
	if not t[key] then return false; end
	return embarrassment >= t[key]
end

local function getIsExhausted(endurance, key)
	local t = {
		[1] = false,
		[2] = 0.8,
		[3] = 0.6,
		[4] = 0.3,
	}
	if not t[key] then return false; end
	return endurance <= t[key]
end

local function getMat(key)
	local t ={
		floors_rugs_01_52 = {"floors_rugs_01_53","getE"},
		floors_rugs_01_53 = {"floors_rugs_01_52","getW"},
		floors_rugs_01_54 = {"floors_rugs_01_55","getN"},
		floors_rugs_01_55 = {"floors_rugs_01_54","getS"},
	}
	return t[key]
end
]]--

--local function getYogaMat(character)
--	local square = character:getSquare()
--	if not square then return false; end
--	local matVars, adjObject
--	for i=1,square:getObjects():size() do
--		local obj = square:getObjects():get(i-1)
--		if obj then
--			local objName = obj:getSpriteName() or obj:getTextureName()
--			if objName then
--				matVars = getMat(objName)
--				if matVars then break; end
--			end
--		end
--	end
--	if matVars then
--		objAdjSqr = square[matVars[2]](square)
--		if not objAdjSqr then return false; end
--		for i=1,objAdjSqr:getObjects():size() do
--			local obj = objAdjSqr:getObjects():get(i-1)
--			if obj then
--				local objName = obj:getSpriteName() or obj:getTextureName()
--				if objName and objName == matVars[1] then adjObject = obj; break; end
--			end
--		end
--	end
--	return adjObject
--end

--[[
local function getCanPerform(thisPlayer, tooltipText, tex)
	local notAvailable, embarrassment, endurance = true, thisPlayer:getModData().LSMoodles["Embarrassed"].Level, thisPlayer:getStats():getEndurance()
	local isEmbarrassed, isExhausted, hasMat
	if endurance <= 0.8 then
		isExhausted = getIsExhausted(endurance, SandboxVars.Yoga.Exhaustion or 3)
	end
	if embarrassment >= 1 then
		isEmbarrassed = getIsEmbarrassed(embarrassment, SandboxVars.Yoga.Embarrassment or 2)
	end
	if SandboxVars.Yoga.RequiresMat then
		hasMat = getYogaMat(thisPlayer)
	end
	
	if SandboxVars.Yoga.RequiresMat and not hasMat then tooltipText = getText("Tooltip_Yoga_MissingMat");
	elseif isExhausted then tooltipText = getText("Tooltip_Tired");
	elseif isEmbarrassed then tooltipText = getText("ContextMenu_Embarrassed");
	else notAvailable, tex, tooltipText = false, getTexture('media/ui/yoga_icon.png'), getText("Tooltip_Yoga_Option"); end
	return notAvailable, tooltipText, tex
end

local function getNewTooltip(description)
	local tooltip = ISToolTip:new();
	tooltip:initialise();
	tooltip:setVisible(false);
	tooltip.description = description
	return tooltip
end

LSYogaContextMenu.doBuildMenu = function(thisPlayer, context, worldobjects, DebugBuildOption)
	if not isOptionValid(thisPlayer) then return; end

	--local strengthM = getSRMulti(SandboxVars.Yoga.StrengthMultiplier)
	
	local yogaOption = context:addOptionOnTop(getText("ContextMenu_LSBody_Yoga"), worldobjects, LSYogaContextMenu.onAction, thisPlayer);
	local tex, tooltipText = getTexture('media/ui/yoga_icon_no.png'), getText("Tooltip_CantPerform")
	yogaOption.notAvailable, tooltipText, tex = getCanPerform(thisPlayer, tooltipText, tex)
	yogaOption.iconTexture = tex
	yogaOption.toolTip = getNewTooltip(tooltipText)
	
end
]]--