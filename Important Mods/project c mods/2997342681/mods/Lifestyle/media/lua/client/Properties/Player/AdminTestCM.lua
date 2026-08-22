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

require 'ISUI/ISWorldObjectContextMenu'

local function getOtherPlayer(worldobjects, thisPlayer)
	local otherPlayer
	for _,object in ipairs(worldobjects) do
		local square = object:getSquare()
		if square then
			for i=1,square:getMovingObjects():size() do
				local moving = square:getMovingObjects():get(i-1)
				if instanceof(moving, "IsoPlayer") and moving:getUsername() ~= thisPlayer:getUsername() and
				moving:isOutside() == thisPlayer:isOutside() and thisPlayer:CanSee(moving) and
				--moving:isOutside() == thisPlayer:isOutside() and thisPlayer:CanSee(moving) and not otherPlayer then
				thisPlayer:checkCanSeeClient(moving) and not otherPlayer then
					otherPlayer = moving
				end
				if otherPlayer then break; end
			end
		end
		if otherPlayer then break; end
	end
	return otherPlayer
end

local function onAdminTest(worldobjects, character)
    ISTimedActionQueue.add(ToneDeafSuffering:new(character))
end

local function onAdminTestB(worldobjects, character)
    ISTimedActionQueue.add(PraiseMusician:new(character))
end

local function onAdminTestC(worldobjects, character)

	--if character:isFacingLocation((character:getX()+1),(character:getY()-1),character:getZ()) then
	--print("PLAYER IS FACING ... 1")
	--character:Say("PLAYER IS FACING ... 1")
	--elseif character:isFacingLocation((character:getX()-1),(character:getY()+1),character:getZ()) then
	--print("PLAYER IS FACING ... 2")
	--character:Say("PLAYER IS FACING ... 2")
	--elseif character:isFacingLocation((character:getX()-1),character:getY(),character:getZ()) then
	--print("PLAYER IS FACING ... 3")
	--character:Say("PLAYER IS FACING ... 3")
	--elseif character:isFacingLocation((character:getX()+1),character:getY(),character:getZ()) then
	--print("PLAYER IS FACING ... 4")
	--character:Say("PLAYER IS FACING ... 4")
	--else
	--print("BUGGED FACING")
	--character:Say("BUGGED FACING")
	--end
    ISTimedActionQueue.add(BooingMusician:new(character))
end

local function onAdminTestI(worldobjects, character)
    --ISTimedActionQueue.add(LSAnimTest:new(character))
	if isShiftKeyDown() then
		ISTimedActionQueue.add(LSApplyPerfumeAction:new(character))
	else
		ISTimedActionQueue.add(LSReactionStinking:new(character))
	end
end

local function onAdminTestD(worldobjects, character, litterCategory)

	local MainLitterList = require("Properties/LitterTypes")

	local LitterList = {}

	for k,v in pairs(MainLitterList) do
		if v.category == litterCategory then
			table.insert(LitterList, v)
		end
	end

	local randomNumber = ZombRand(#LitterList) + 1
	local dirtSprite = LitterList[randomNumber].name
		
	if isClient() then
		sendClientCommand("LS", "DebugAddLitter", {character:getX(), character:getY(), character:getZ(), 2, dirtSprite})
	else
		LSAddLitter(character:getX(), character:getY(), character:getZ(), 2, dirtSprite)
	end

end

local function onAdminTestE(worldobjects, character)
  
 	local playerdata
	
	if character:hasModData() then
		playerdata = character:getModData()
	else
	return; end
 
 	if not playerdata.bathroomNeed then
		playerdata.bathroomNeed = 0
	end
  
	if isShiftKeyDown() then
		playerdata.bathroomNeed = playerdata.bathroomNeed + 50
		character:Say("Bathroom Need increased by 50")
	else
		playerdata.bathroomNeed = playerdata.bathroomNeed + 10
		character:Say("Bathroom Need increased by 10")
	end
 
  	if playerdata.bathroomNeed > 100 then
		playerdata.bathroomNeed = 100
	end
 
	character:Say("Bathroom Need is now " .. tonumber(playerdata.bathroomNeed))
 
end

local function onAdminTestF(worldobjects, character)


    for playerIndex = 0, getNumActivePlayers()-1 do
    local playersList = {};--get players
	local playerObj = getSpecificPlayer(playerIndex)
	local playerIso

		if (playerObj ~= nil) then


				for x = playerObj:getX()-8,playerObj:getX()+8 do
					for y = playerObj:getY()-8,playerObj:getY()+8 do
						local square = getCell():getGridSquare(x,y,playerObj:getZ());
						if square then
							for i = 0,square:getMovingObjects():size()-1 do
								local moving = square:getMovingObjects():get(i);
								if instanceof(moving, "IsoPlayer") then
									table.insert(playersList, moving);
								end
							end
						end
					end
				end

            if #playersList > 0 then
                for i,v in ipairs(playersList) do
					if v:getUsername() == playerObj:getUsername() then
						playerIso = v
					end
				end
				for i,v in ipairs(playersList) do
					if playerIso and
					v:getUsername() ~= playerObj:getUsername() and
					v:isOutside() == playerObj:isOutside() then
						--if playerIso:checkCanSeeClient(v) then
						if playerObj:CanSee(v) and playerIso:checkCanSeeClient(v) then
							local DanceTargetName = tostring(v:getDescriptor():getForename())
							local DanceTargetSurname = tostring(v:getDescriptor():getSurname())
							playerObj:Say("I see a player and his name is " .. DanceTargetName .. DanceTargetSurname)
						else
						playerObj:Say("There is someone around but I don't see them")
						end
					else
						playerObj:Say("No players around me")
					end
                end	
			else
				playerObj:Say("No players around me")
            end
	

		end
	end
end

local function onAdminTestG(worldobjects, character)
  
 	local playerdata
	
	if character:hasModData() then
		playerdata = character:getModData()
	else
	return; end
 
 	if not playerdata.hygieneNeed then
		playerdata.hygieneNeed = 40
	end
 
	if isShiftKeyDown() then
		playerdata.hygieneNeed = playerdata.hygieneNeed + 50
		character:Say("Hygiene Need increased by 50")
	else
		playerdata.hygieneNeed = playerdata.hygieneNeed + 10
		character:Say("Hygiene Need increased by 10")
	end
 
  	if playerdata.hygieneNeed > 100 then
		playerdata.hygieneNeed = 100
	end
 
	character:Say("Hygiene Need is now " .. tonumber(playerdata.hygieneNeed))
 
end
--[[ --!
local function getPlayerCooldowns()

	return {
		"TeachCooldown",
		"LessonCooldown",
		"InteractionSpam",
	}

end
]]--
local function onAdminTestH(worldobjects, character) --!
	if not character:hasModData() then return; end
 	local playerData = character:getModData()
	if not playerData.LSCooldowns then return; end
	character:Say("HIDDEN: Reseting Cooldowns")
	
	local cooldownList = LSUtil.getPlayerCooldowns(false)
	
	for k, v in pairs(cooldownList) do
		for n=1, #v[1] do
			local moodle = v[1][n]
			if playerData.LSMoodles[moodle] and playerData.LSMoodles[moodle].Value ~= 0 then
				playerData.LSMoodles[moodle].Value = 0
				character:Say("HIDDEN:" .. moodle .. " moodle value set to 0")
			end
		end
		for n=1, #v[2] do
			local data = v[2][n]
			if playerData.LSCooldowns[data] and playerData.LSCooldowns[data] ~= 0 then
				playerData.LSCooldowns[data] = 0
				character:Say("HIDDEN:" .. data .. " value set to 0")
			end
		end
	end
end
--[[
local function resetAmbt(playerObj, ambtName)
	local AmbitionsList = require("Properties/Player/LSAmbitions")
	for k, v in ipairs(AmbitionsList) do
		if v.name and (v.name == ambtName) and (not v.disable) then playerObj:getModData().Ambitions[v.name] = v; break; end
	end
end
]]--
local function ambitionsCharOnToggle(worldobjects, character, ambt, isCompleted, doUnlock)
	if doUnlock then LSAmbtMng.doUnlock(character, ambt); return; end
	if not isCompleted then
		if ambt.isHidden then ambt.isHidden = false; end
		LSAmbtMng.doComplete(character, ambt)
		return
	end
	LSAmbtMng.resetAmbt(character, ambt.name, true)
end

local function getCustomAmbtData(ambtName)
	local lsData = ModData.getOrCreate("LSDATA")
	if lsData and lsData["AMBT"] and lsData["AMBT"][ambtName] then
		return lsData["AMBT"][ambtName]
	end
	return false
end

local function doToggleOption(worldobjects, context, menu, character, ambt)
	local texture, text = "ambt_completed_icon.png", "ContextMenu_LSDebug_Ambitions_Char_Complete"
	if ambt.completed then texture, text = "reset_icon.png", "ContextMenu_LSDebug_Ambitions_Char_Reset"; end
	local ToggleOption = menu:addOption(getText(text), worldobjects, ambitionsCharOnToggle, character, ambt, ambt.completed, false)
	ToggleOption.iconTexture = getTexture('media/ui/'..texture)
end

local function doUnlockOption(worldobjects, context, menu, character, ambt)
	local option = menu:addOption(getText("ContextMenu_LSDebug_Ambitions_Char_Unlock"), worldobjects, ambitionsCharOnToggle, character, ambt, ambt.completed, true)
	option.iconTexture = getTexture('media/ui/ambt_unlock_icon.png')
end

local function characterAmbts(worldobjects, context, menu, character, ogAmbt, ambt)
	doToggleOption(worldobjects, context, menu, character, ambt) -- complete/reset
	if ambt.isHidden and (not ambt.completed) then doUnlockOption(worldobjects, context, menu, character, ambt); end -- unlock
end

local function ambitionsTargetOnToggle(worldobjects, character, target, ogAmbt, doComplete)
	if doComplete then
		sendClientCommand("LS", "CompleteTargetAmbt", {target:getOnlineID(),ogAmbt.name})
		character:Say("debug: Ambition "..ogAmbt.name.." completed for: "..target:getUsername())
		return
	end
	sendClientCommand("LS", "ResetTargetAmbt", {target:getOnlineID(),ogAmbt.name})
	character:Say("debug: Ambition "..ogAmbt.name.." reset for: "..target:getUsername())
end

local function doTargetResetOption(worldobjects, context, menu, character, target, ogAmbt)
	local option = menu:addOption(getText("ContextMenu_LSDebug_Ambitions_Target_Reset").." ("..target:getUsername()..")", worldobjects, ambitionsTargetOnToggle, character, target, ogAmbt, false)
	option.iconTexture = getTexture('media/ui/reset_icon.png')
end

local function doTargetCompleteOption(worldobjects, context, menu, character, target, ogAmbt)
	local option = menu:addOption(getText("ContextMenu_LSDebug_Ambitions_Target_Complete").." ("..target:getUsername()..")", worldobjects, ambitionsTargetOnToggle, character, target, ogAmbt, true)
	option.iconTexture = getTexture('media/ui/ambt_completed_icon.png')
end

local function targetAmbts(worldobjects, context, menu, character, target, ogAmbt)
	doTargetResetOption(worldobjects, context, menu, character, target, ogAmbt) -- reset
	doTargetCompleteOption(worldobjects, context, menu, character, target, ogAmbt) -- complete
end

local function ambitionsAllOnSendOption(worldobjects, character, ogAmbt, key, value, canEdit)
	--local t = deepCopy(ogAmbt)
	--sendClientCommand("LS", "UpdateAmbt", {t,ogAmbt.name,key,value})
	local playerNum = character:getPlayerNum()
	local width, height = 320, 260
	local LSAmbtConfirm = LSAmbtConfirm:new((getPlayerScreenWidth(playerNum)-width)/2,(getPlayerScreenHeight(playerNum)-height)/2,width,height,character,ogAmbt,key,value,canEdit);
    LSAmbtConfirm:initialise();
    LSAmbtConfirm:addToUIManager()

end

local function doAllResetOption(worldobjects, context, menu, character, ogAmbt)
	local option = menu:addOption(getText("ContextMenu_LSDebug_Ambitions_All_Reset"), worldobjects, ambitionsAllOnSendOption, character, ogAmbt, "resetAdm", true, false)
	option.iconTexture = getTexture('media/ui/resetAll_icon.png')
end

local function doAllToggleOption(worldobjects, context, menu, character, ogAmbt, ambtData)
	local texture, text, value = "okayNo_icon.png", "ContextMenu_LSDebug_Ambitions_All_Disable", true
	if ambtData and ambtData.custom and ambtData.disable then
		texture, text, value = "okay_icon.png", "ContextMenu_LSDebug_Ambitions_All_Enable", false
	end
	local option = menu:addOption(getText(text), worldobjects, ambitionsAllOnSendOption, character, ogAmbt, "disable", value, false)
	option.iconTexture = getTexture('media/ui/'..texture)
end

local function doAllGoalOption(worldobjects, context, menu, character, ogAmbt, ambtData)
	local t = ogAmbt
	if ambtData and ambtData.custom then t = ambtData; end
	for n=1, 6 do
		local value = t['goal'..n]
		if value and (type(value) == "number") and (value > 0) then
			local option = menu:addOption(getText("ContextMenu_LSDebug_Ambitions_All_Goal")..": "..n.." ("..value..")", worldobjects, ambitionsAllOnSendOption, character, ogAmbt, "goal"..n, value, true)
			option.iconTexture = getTexture('media/ui/bookWrite_icon.png')
		end
	end
end


local function allAmbts(worldobjects, context, menu, character, ogAmbt)
	local ambtData = getCustomAmbtData(ogAmbt.name)
	doAllResetOption(worldobjects, context, menu, character, ogAmbt) -- reset
	doAllToggleOption(worldobjects, context, menu, character, ogAmbt, ambtData) -- toggle on/off
	doAllGoalOption(worldobjects, context, menu, character, ogAmbt, ambtData) -- change goal values
end

local function ambitionOptions(worldobjects, context, menu, character, ambition)
	if character:getModData().Ambitions[ambition.name] then
		characterAmbts(worldobjects, context, menu, character, ambition, character:getModData().Ambitions[ambition.name])
	end
	if isClient() then 
		local target = getOtherPlayer(worldobjects, character)
		if target then targetAmbts(worldobjects, context, menu, character, target, ambition); end
	end
	allAmbts(worldobjects, context, menu, character, ambition)
end

local function ambitionsAll(worldobjects, context, menu, character)
	if character and character:hasModData() and (not character:isDead()) and character:getModData().Ambitions then
		local AmbitionsList = require("Properties/Player/LSAmbitions")
		for k, v in ipairs(AmbitionsList) do
			if (not v.disable) and LSAmbtMng and LSAmbtMng[v.name] then
				local newOption = menu:addOption(getText(v.name))
				local texture = getTexture('media/ui/Ambitions/'..v.texture..'.png')
				if texture then newOption.iconTexture = texture; end
				local newSubMenu = menu:getNew(menu)
				context:addSubMenu(newOption, newSubMenu)
				ambitionOptions(worldobjects, context, newSubMenu, character, v)
			end
		end
	end
end

local function onAdminTestHung(worldobjects, character)
	local hunger = character:getStats():getHunger()
	if hunger then character:Say("Hunger Val Is: "..tostring(hunger)); end
end

local function skillsOnEdit(worldobjects, character, skill, skillName)
	--local t = deepCopy(ogAmbt)
	--sendClientCommand("LS", "UpdateAmbt", {t,ogAmbt.name,key,value})
	local playerNum = character:getPlayerNum()
	local width, height = 320, 260
	local LSDebugConfirm = LSDebugConfirm:new((getPlayerScreenWidth(playerNum)-width)/2,(getPlayerScreenHeight(playerNum)-height)/2,width,height,character,skill, skillName);
    LSDebugConfirm:initialise();
    LSDebugConfirm:addToUIManager()

end

local function onAdminTestSkill(worldobjects, context, menu, character)
	if not character or not character:hasModData() or character:isDead() then return; end
	local t = {"Yoga"}
	for n=1, #t do
		local skill = HiddenSkills.getSkill(character, t[n])
		if skill then menu:addOption(getText('UI_LSHS_'..t[n]).." ("..tostring(skill[1])..")", worldobjects, skillsOnEdit, character, skill, t[n]); end
	end
end

local function onAdminPrint(worldobjects, character, option)
	if not option then return; end
	if option == "fitness" then
		if FitnessExercises and FitnessExercises.exercisesType then
			for k, v in pairs(FitnessExercises.exercisesType) do
				if v and v.xpMod then
					print("--------------WARNING: onAdminPrint - fitness - key value "..tostring(k).." is: "..tostring(v.xpMod))
				end
			end
		end
	end
end

local function admBTYonEdit(worldobjects, character, spriteName)
	--local t = deepCopy(ogAmbt)
	--sendClientCommand("LS", "UpdateAmbt", {t,ogAmbt.name,key,value})
	local value = getBeautyProperty(spriteName, false) or 0
	local negative = value < 0
	if negative then value = -1*value; end
	local playerNum = character:getPlayerNum()
	local width, height = 260, 180
	local newUI = LSDebugBTYConfirm:new((getPlayerScreenWidth(playerNum)-width)/2,(getPlayerScreenHeight(playerNum)-height)/2,width,height,spriteName,value,negative);
    newUI:initialise();
    newUI:addToUIManager()
end

local function removeDuplicates(list)
    local result = {}
    local seen = {}
    for _,item in ipairs(list) do
        if not seen[item] then
            seen[item] = true
            table.insert(result, item)
        end
    end
    return result
end

local function admBTYonImport()
	sendClientCommand("LS", "ImportServerBeauty", {})
end

local function onAdminBTY(worldobjects, context, menu, character)
	if not SandboxVars.Text.DividerArt then return; end
	menu:addOption("Import custom beauty values", worldobjects, admBTYonImport)
    local square
    for i,v in ipairs(worldobjects) do
        square = v:getSquare();
        break;
    end

    for i=1,square:getObjects():size() do
        table.insert(worldobjects, square:getObjects():get(i-1))
    end
    worldobjects = removeDuplicates(worldobjects)
	for _,obj in ipairs(worldobjects) do
		if instanceof(obj, "IsoObject") then
			local objName = obj:getTextureName()
			if objName and not luautils.stringStarts(objName, "LS_Painting") and not luautils.stringStarts(objName, "LS_Sculptures") and not luautils.stringStarts(objName, "LS_Graffiti") then
				local spriteName = obj:getSprite():getName()
				local option = menu:addOption(spriteName, worldobjects, admBTYonEdit, character, spriteName)
				local tex = getSprite(spriteName):getTextureForCurrentFrame(IsoDirections.E)
				option.iconTexture = tex
			end
		end
	end
end

local function AdminCMOptionsTable(subMenuOther, subMenuExpressions, subMenuNeeds)
return {
	{name='Expressions',text="ContextMenu_LSDebug_TDSuffer",localF=onAdminTest,arg1=false},
	{name='Expressions',text="ContextMenu_LSDebug_Applause",localF=onAdminTestB,arg1=false},
	{name='Expressions',text="ContextMenu_LSDebug_Boo",localF=onAdminTestC,arg1=false},
	{name='Expressions',text="ContextMenu_LSDebug_TestAnim",localF=onAdminTestI,arg1=false},
	{name='Other',text="Edit Beauty",localF=onAdminBTY,arg1="submenu"},
	{name='Other',text="ContextMenu_LSDebug_Litter",localF=onAdminTestD,arg1="grime"},
	{name='Other',text="ContextMenu_LSDebug_LitterB",localF=onAdminTestD,arg1="blood"},
	{name='Other',text="ContextMenu_LSDebug_VisionCheck",localF=onAdminTestF,arg1=false},
	{name='Other',text="Reset Cooldowns",localF=onAdminTestH,arg1=false}, --!
	{name='Other',text="Print FitnessDefs",localF=onAdminPrint,arg1="fitness"},
	{name='Needs',text="ContextMenu_LSDebug_IncreaseBathroomNeed",localF=onAdminTestE,arg1=false},
	{name='Needs',text="ContextMenu_LSDebug_IncreaseHygieneNeed",localF=onAdminTestG,arg1=false},
	{name='Ambitions',text="ContextMenu_LSDebug_Ambitions_All",localF=ambitionsAll,arg1="submenu"},
	--{name='Other',text="ContextMenu_LSDebug_Hung",localF=onAdminTestHung,arg1=false},
	{name='HSkills',text="HiddenSkills",localF=onAdminTestSkill,arg1="submenu"}, --!
}
end

LSDebugAdmin = {};
LSDebugAdmin.doBuildMenu = function(player, context, worldobjects, DebugBuildOption)

    if isAdmin() or isDebugEnabled() then

	--local sandboxExpressions = SandboxVars.Debug.Expressions or false
	--if sandboxExpressions then
		local menuNames = {'Expressions','Needs','HSkills','Ambitions','Other'}
		local adminCMOptions, character = AdminCMOptionsTable(), getSpecificPlayer(player)

		for n=1, #menuNames do
			local menu = DebugBuildOption:addOptionOnTop(getText("ContextMenu_LSDebug_"..menuNames[n]))
			local subMenu = DebugBuildOption:getNew(DebugBuildOption);
			context:addSubMenu(menu, subMenu)
			for k, v in pairs(adminCMOptions) do
				if menuNames[n] == v.name then
					local newOption
					if v.arg1 and (v.arg1 == "submenu") then
						local newOption = subMenu:addOption(getText(v.text))
						local newSubMenu = subMenu:getNew(subMenu)
						context:addSubMenu(newOption, newSubMenu)
						v.localF(worldobjects, context, newSubMenu, character)
					else
						subMenu:addOption(getText(v.text), worldobjects, v.localF, character, v.arg1)
					end
				end
			end
		end
	--end
	end
end
