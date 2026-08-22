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

require "Properties/Player/CleaningSkill"
require "Helper/ContextHelper"

BathContextMenu = {};

local function getFullBath(worldobjects, Bath, spriteName)

	local BathMaster, BathBottom, secondSpriteName, ConnectedSqr, ConnectedObject, ConnectedSprite
	ConnectedSprite = spriteName
	--if spriteName then print("Bathtub spriteName is... " .. spriteName); end
	if (spriteName == "fixtures_bathroom_01_25") then
		BathMaster = Bath
		ConnectedSqr = Bath:getSquare():getAdjacentSquare(IsoDirections.S)
		secondSpriteName = "fixtures_bathroom_01_24"
	elseif (spriteName == "fixtures_bathroom_01_26") then
		BathMaster = Bath
		ConnectedSqr = Bath:getSquare():getAdjacentSquare(IsoDirections.E)
		secondSpriteName = "fixtures_bathroom_01_27"
	elseif (spriteName == "fixtures_bathroom_01_27") then
		BathBottom = Bath
		ConnectedSqr = Bath:getSquare():getAdjacentSquare(IsoDirections.W)
		spriteName = "fixtures_bathroom_01_26"
		secondSpriteName = "fixtures_bathroom_01_27"
	elseif (spriteName == "fixtures_bathroom_01_24") then
		BathBottom = Bath
		ConnectedSqr = Bath:getSquare():getAdjacentSquare(IsoDirections.N)
		spriteName = "fixtures_bathroom_01_25"
		secondSpriteName = "fixtures_bathroom_01_24"
	elseif (spriteName == "fixtures_bathroom_01_52") then
		BathMaster = Bath
		ConnectedSqr = Bath:getSquare():getAdjacentSquare(IsoDirections.N)
		secondSpriteName = "fixtures_bathroom_01_53"
	elseif (spriteName == "fixtures_bathroom_01_55") then
		BathMaster = Bath
		ConnectedSqr = Bath:getSquare():getAdjacentSquare(IsoDirections.W)
		secondSpriteName = "fixtures_bathroom_01_54"
	elseif (spriteName == "fixtures_bathroom_01_53") then
		BathBottom = Bath
		ConnectedSqr = Bath:getSquare():getAdjacentSquare(IsoDirections.S)
		spriteName = "fixtures_bathroom_01_52"
		secondSpriteName = "fixtures_bathroom_01_53"
	elseif (spriteName == "fixtures_bathroom_01_54") then
		BathBottom = Bath
		ConnectedSqr = Bath:getSquare():getAdjacentSquare(IsoDirections.E)
		spriteName = "fixtures_bathroom_01_55"
		secondSpriteName = "fixtures_bathroom_01_54"
	end

	if not ConnectedSqr then print("no ConnectedSqr"); return false, false, false; end
	if ConnectedSprite ~= spriteName then ConnectedSprite = spriteName; else ConnectedSprite = secondSpriteName; end
	
	for _,object in ipairs(worldobjects) do
		if ConnectedSqr then
			for i=1,ConnectedSqr:getObjects():size() do
				local thisObject = ConnectedSqr:getObjects():get(i-1)
				local thisSprite = thisObject:getSprite()	
				if thisSprite ~= nil then
					local properties = thisObject:getSprite():getProperties()
					if properties ~= nil then
						local groupName = nil
						local customName = nil
						local thisSpriteName = nil
						if thisSprite:getName() then
							thisSpriteName = thisSprite:getName()
						end
						if properties:Is("GroupName") then
							groupName = properties:Val("GroupName")
						end
						if properties:Is("CustomName") then
							customName = properties:Val("CustomName")
						end
						if groupName == "Large Deluxe" and customName == "Bath" and (thisSpriteName == ConnectedSprite) then
							ConnectedObject = thisObject;
						end
					end
				end
			end
		end
	end

	if not ConnectedObject then print("no ConnectedObject"); return false, false, false; end

	if BathMaster then
		BathBottom = ConnectedObject
	elseif BathBottom then
		BathMaster = ConnectedObject
	end

	return BathMaster, BathBottom, spriteName, secondSpriteName

end

local function getDirtOrBlood(thisPlayer)

	local visual = thisPlayer:getHumanVisual()
	local hasDirtOrBlood = false

	for i = 1, BloodBodyPartType.MAX:index() do
		local part = BloodBodyPartType.FromIndex(i - 1)
		local dirt = visual:getDirt(part)
		local blood = visual:getBlood(part)
		if (dirt > 0) or (blood > 0) then
			hasDirtOrBlood = true
			break
		end
	end

	return hasDirtOrBlood

end

local function getDirtSprites(ThisSpriteName)

	local dirtSprite, dirtSprite2, dirtSprite3

	if (ThisSpriteName == "fixtures_bathroom_01_26") then
		dirtSprite = "LS_Misc_2_0"
		dirtSprite2 = "LS_Misc_2_8"
		dirtSprite3 = "LS_Misc_2_16"
	elseif (ThisSpriteName == "fixtures_bathroom_01_27") then
		dirtSprite = "LS_Misc_2_1"
		dirtSprite2 = "LS_Misc_2_9"
		dirtSprite3 = "LS_Misc_2_17"
	elseif (ThisSpriteName == "fixtures_bathroom_01_25") then
		dirtSprite = "LS_Misc_2_2"
		dirtSprite2 = "LS_Misc_2_10"
		dirtSprite3 = "LS_Misc_2_18"
	elseif (ThisSpriteName == "fixtures_bathroom_01_24") then
		dirtSprite = "LS_Misc_2_3"
		dirtSprite2 = "LS_Misc_2_11"
		dirtSprite3 = "LS_Misc_2_19"
	elseif (ThisSpriteName == "fixtures_bathroom_01_55") then
		dirtSprite = "LS_Misc_2_4"
		dirtSprite2 = "LS_Misc_2_12"
		dirtSprite3 = "LS_Misc_2_20"
	elseif (ThisSpriteName == "fixtures_bathroom_01_54") then
		dirtSprite = "LS_Misc_2_5"
		dirtSprite2 = "LS_Misc_2_13"
		dirtSprite3 = "LS_Misc_2_21"
	elseif (ThisSpriteName == "fixtures_bathroom_01_53") then
		dirtSprite = "LS_Misc_2_6"
		dirtSprite2 = "LS_Misc_2_14"
		dirtSprite3 = "LS_Misc_2_22"
	elseif (ThisSpriteName == "fixtures_bathroom_01_52") then
		dirtSprite = "LS_Misc_2_7"
		dirtSprite2 = "LS_Misc_2_15"
		dirtSprite3 = "LS_Misc_2_23"
	end

	return dirtSprite, dirtSprite2, dirtSprite3

end

local function updateBathSprites(ThisBath, ThisBathData, ThisSpriteName)

	local dirtSprite, dirtSprite2, dirtSprite3 = getDirtSprites(ThisSpriteName)
	local thisDirtSprite

	if ThisBathData.ConditionLevel == 1 and ThisBathData.Condition >= 30 then
		thisDirtSprite = dirtSprite
	elseif ThisBathData.ConditionLevel == 2 and ThisBathData.Condition >= 60 then
		thisDirtSprite = dirtSprite2
	elseif ThisBathData.ConditionLevel == 3 and ThisBathData.Condition >= 90 then
		thisDirtSprite = dirtSprite3
	end

	if isClient() and thisDirtSprite then
		ThisBath:setOverlaySprite(thisDirtSprite, true)
		ThisBath:transmitUpdatedSpriteToServer()
		ThisBath:transmitModData()
	--elseif isClient() then
		--self.toiletObject:transmitModData()
	elseif thisDirtSprite then
		ThisBath:setOverlaySprite(thisDirtSprite, false)
	elseif isClient() then
		ThisBath:setOverlaySprite(nil, true)
		ThisBath:transmitModData()
		ThisBath:transmitUpdatedSpriteToServer()
	else
		ThisBath:setOverlaySprite(nil, false)
	end

end

local function checkBathDirtness(BathData, BathBottomData, BathMaster, BathBottom, spriteName, secondSpriteName)

	if not BathData.Condition then
		BathData.Condition = 0
		BathData.ConditionLevel = 0
	end
	if not BathBottomData.Condition then
		BathBottomData.Condition = 0
		BathBottomData.ConditionLevel = 0
	end

	if BathData.Condition > BathBottomData.Condition then
		BathBottomData.Condition = BathData.Condition
		BathBottomData.ConditionLevel = BathData.ConditionLevel
	elseif BathBottomData.Condition > BathData.Condition then
		BathData.Condition = BathBottomData.Condition
		BathData.ConditionLevel = BathBottomData.ConditionLevel
	end

	updateBathSprites(BathMaster, BathData, spriteName)
	updateBathSprites(BathBottom, BathBottomData, secondSpriteName)

end

local function getCleaningItems(thisPlayer)

	local inventory = thisPlayer:getInventory();
	local it = inventory:getItems();
	local CleanItem1
	local CleanItem2

	for j = 0, it:size()-1 do
		local item = it:get(j);
		if item:getType() == "Sponge" and not CleanItem1 then
			CleanItem1 = item
		end
		if (item:getType() == "CleaningLiquid" or item:getType() == "CleaningLiquid2") and not CleanItem2 then
			CleanItem2 = item
		end
		if CleanItem1 and CleanItem2 then
		break
		end
	end

	return CleanItem1, CleanItem2

end

local function getFacingDir(ThisSpriteName)

	local facingDir

	if (ThisSpriteName == "fixtures_bathroom_01_25") or (ThisSpriteName == "fixtures_bathroom_01_24") then
		facingDir = "S"
	elseif (ThisSpriteName == "fixtures_bathroom_01_26") or (ThisSpriteName == "fixtures_bathroom_01_27") then
		facingDir = "E"
	elseif (ThisSpriteName == "fixtures_bathroom_01_52") or (ThisSpriteName == "fixtures_bathroom_01_53") then
		facingDir = "N"
	elseif (ThisSpriteName == "fixtures_bathroom_01_55") or (ThisSpriteName == "fixtures_bathroom_01_54") then
		facingDir = "W"
	end

	return facingDir

end

local function isPlayerDressed(player)

	local inventory = player:getInventory()	
	local it = inventory:getItems()
	local wasDressed

	for j = 0, it:size()-1 do
		local item = it:get(j);
		if item:getClothingItem() and player:isEquippedClothing(item) and (not item:isHidden()) and
		item:getType() ~= "Belt" and
		item:getType() ~= "Belt2" and
		item:getType() ~= "HolsterDouble" and
		item:getType() ~= "HolsterSimple" then
			wasDressed = true
			break
		end
	end

	return wasDressed

end

BathContextMenu.doBuildMenu = function(player, context, worldobjects, Bath, spriteName, customName, groupName, DebugBuildOption)
 
    local thisPlayer = getSpecificPlayer(player)

	if not thisPlayer then return; end
    if thisPlayer:getVehicle() then return; end
	--if not thisPlayer:isSitOnGround() then return; end
	if thisPlayer:isSneaking() then return; end
	
	local playerdata
	
	if thisPlayer:hasModData() then
		playerdata = thisPlayer:getModData()
	else
	return; end
	
	if not Bath then return; end
	if not Bath:getSquare() then return; end
	--if not Bath:getSquare():getS() then print("Bath S sqr is nil"); return; end

	local BathMaster, BathBottom, spriteNameNew, secondSpriteName = getFullBath(worldobjects, Bath, spriteName)

	if not BathMaster then return; end 

	local waterUsage = 4
	local beautyVal = 1

	local BathData = BathMaster:getModData()
	local BathBottomData = BathBottom:getModData()

	if not playerdata.hygieneNeed then
		playerdata.hygieneNeed = 40
	end

	if ((BathMaster:hasWater()) and (BathMaster:getWaterAmount() < (waterUsage*2))) or not BathMaster:hasWater() then 
	
	--thisPlayer:Say("not enough water in Bath")
	
	local RefuseOption = context:addOption(getText("ContextMenu_Bath_NoWater"));

	local tooltip = ISToolTip:new();
		tooltip:initialise();
		tooltip:setVisible(false);
		

		RefuseOption.notAvailable = true;
		description = " <RED>" .. getText("Tooltip_Shower_NoWater");
		tooltip.description = description
		RefuseOption.toolTip = tooltip
		RefuseOption.iconTexture = getTexture('media/ui/takebathNO_icon.png')

	elseif (BathMaster:hasWater() and (BathMaster:getWaterAmount() >= (waterUsage*2))) and not
	thisPlayer:hasTimedActions() and not thisPlayer:isSitOnGround() then

	--------------DIRT/BLOOD
	local hasDirtOrBlood = getDirtOrBlood(thisPlayer)
	-------------------------
	checkBathDirtness(BathData, BathBottomData, BathMaster, BathBottom, spriteNameNew, secondSpriteName)

	if BathData.ConditionLevel > 0 then

		local CleanItem1, CleanItem2 = getCleaningItems(thisPlayer)

		--local cleanDuration = BathData.Condition*200
		local cleanDuration = GetCleaningTimeTile(thisPlayer, BathMaster, BathData.Condition)
		local cleanOption = context:addOptionOnTop(getText("ContextMenu_Bath_Clean"),
		worldobjects,
		BathContextMenu.onAction,
		thisPlayer,
		BathMaster,
		BathBottom,
		cleanDuration,
		spriteNameNew,
		CleanItem1,
		CleanItem2,
		"IsClean",
		false);
	
		local tooltipClean = ISToolTip:new();
			tooltipClean:initialise();
			tooltipClean:setVisible(false);

		if CleanItem1 and CleanItem2 then
			cleanOption.iconTexture = getTexture('media/ui/clean_icon.png')
		else
			cleanOption.notAvailable = true;
			descriptionC = " <RED>" .. getText("Tooltip_Shower_CleanNoItem");
			tooltipClean.description = descriptionC
			cleanOption.toolTip = tooltipClean
				
			cleanOption.iconTexture = getTexture('media/ui/cleanNO_icon.png')
		end
	
	end

	if ((playerdata.hygieneNeed > 35) or (hasDirtOrBlood)) then
		local useContextText = "ContextMenu_Bath_Use"

		local doBubbleBathOption = context:addOptionOnTop(getText("ContextMenu_Bath_UseBubble"),
		worldobjects,
		BathContextMenu.onAction,
		thisPlayer,
		BathMaster,
		BathBottom,
		waterUsage,
		spriteNameNew,
		secondSpriteName,
		false,---------------soap
		"IsUse",
		0.5);

		if not ((SandboxVars.ElecShutModifier > -1 and
		GameTime:getInstance():getNightsSurvived() < SandboxVars.ElecShutModifier) or
		Bath:getSquare():haveElectricity()) then
			--print("no electricity for shower")
			doBubbleBathOption.notAvailable = true;
			useContextText = "ContextMenu_Bath_Use_NoHot"
		end
		doBubbleBathOption.iconTexture = getTexture('media/ui/moodles/HygieneGood.png')

		local doBathOption = context:addOptionOnTop(getText(useContextText),
		worldobjects,
		BathContextMenu.onAction,
		thisPlayer,
		BathMaster,
		BathBottom,
		waterUsage,
		spriteNameNew,
		secondSpriteName,
		false,---------------soap
		"IsUse",
		0);
	
		doBathOption.iconTexture = getTexture('media/ui/takebath_icon.png')
	
		end
-------
	end--water
------

-----------INFO
	--[[
	local InfoOption = context:addOptionOnTop(getText("ContextMenu_Info"));

	local dirtVal = tonumber(BathData.Condition) or 0

	local tooltip = ISToolTip:new();
		tooltip:initialise();
		tooltip:setVisible(false);
		description = getText("Tooltip_Sit_Beauty") .. " <RGB:1,1,0>" .. beautyVal .. " <RGB:1,1,1>" .. getText(" | ") .. getText("Tooltip_Object_Dirtiness") .. " <RGB:1,1,0>" .. dirtVal.. getText("/100");
		tooltip.description = description
		InfoOption.toolTip = tooltip
	]]--
-----------DEBUG

	local sandboxExpressions = SandboxVars.Debug.Expressions or false
	if sandboxExpressions then

		if isAdmin() or isDebugEnabled() then

		local debugMenu = DebugBuildOption:addOptionOnTop(getText("ContextMenu_LSDebug_Hygiene"));
		
		local subMenu = DebugBuildOption:getNew(DebugBuildOption);
		context:addSubMenu(debugMenu, subMenu)

		local doBathDebugResetOption = subMenu:addOption(getText("ContextMenu_LSDebug_ResetTileData"),
		worldobjects,
		BathContextMenu.onDebug,
		thisPlayer,
		BathMaster,
		BathBottom,
		spriteNameNew,
		secondSpriteName,
		"IsDebugReset");

		local doBathDebugAddDirtOption = subMenu:addOption(getText("ContextMenu_LSDebug_AddDirt"),
		worldobjects,
		BathContextMenu.onDebug,
		thisPlayer,
		BathMaster,
		BathBottom,
		spriteNameNew,
		secondSpriteName,
		"IsDebugAddDirt");

		end
	end

end

local function getSquareClosestToPlayer(thisPlayer, thisObject, facing, frontSquare, frontSquareAlt)

	if (thisPlayer:getX() > thisObject:getX()) and (thisPlayer:getY() > thisObject:getY()) then
		return frontSquare
	elseif (thisPlayer:getX() < thisObject:getX()) and (thisPlayer:getY() < thisObject:getY()) then
		return frontSquareAlt
	elseif (thisPlayer:getX() < thisObject:getX()) and (thisPlayer:getY() > thisObject:getY()) then
		if facing == "S" then
			return frontSquareAlt
		elseif facing == "E" then
			return frontSquare
		end
	elseif (thisPlayer:getX() > thisObject:getX()) and (thisPlayer:getY() < thisObject:getY()) then
		if facing == "S" then
			return frontSquare
		elseif facing == "E" then
			return frontSquareAlt
		end
	end

	return frontSquare

end

BathContextMenu.walkToFront = function(thisPlayer, thisObject)
	local frontSquare, frontSquareAlt, controllerSquare
	local spriteName = thisObject:getSprite():getName()
	if not spriteName then
		return false
	end
	local thisSquare = thisObject:getSquare()
	if not thisSquare then
		return false
	end
	local properties = thisObject:getSprite():getProperties()
	
	local facing = nil
	if properties:Is("Facing") then
		facing = properties:Val("Facing")
	end
	
	if facing then
		if facing == "S" then
			frontSquare = thisObject:getSquare():getE()
			frontSquareAlt = thisObject:getSquare():getW()
		elseif facing == "E" then
			frontSquare = thisObject:getSquare():getS()
			frontSquareAlt = thisObject:getSquare():getN()
		end
	end
	
	if frontSquare and frontSquareAlt then
		if AdjacentFreeTileFinder.privTrySquare(thisSquare, frontSquare) and AdjacentFreeTileFinder.privTrySquare(thisSquare, frontSquareAlt) then
			local closerSqr = getSquareClosestToPlayer(thisPlayer, thisObject, facing, frontSquare, frontSquareAlt)
			ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, closerSqr))
			return true
		end
	end
	if frontSquare then
		if AdjacentFreeTileFinder.privTrySquare(thisSquare, frontSquare) then
			ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, frontSquare))
			return true
		end
	end
	if frontSquareAlt then
		if AdjacentFreeTileFinder.privTrySquare(thisSquare, frontSquareAlt) then
			ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, frontSquareAlt))
			return true
		end
	end

	local freeSquare

	if (spriteName == "fixtures_bathroom_01_25") or (spriteName == "fixtures_bathroom_01_52") then
		freeSquare = (thisSquare:getE() or thisSquare:getW())
	elseif (spriteName == "fixtures_bathroom_01_26") or (spriteName == "fixtures_bathroom_01_55") then
		freeSquare = (thisSquare:getS() or thisSquare:getN())
	end

	if not freeSquare then
		return false
	end

	--move the player to the closest available free tile
	local N, S, E, W

	if ((spriteName == "fixtures_bathroom_01_26") or (spriteName == "fixtures_bathroom_01_55")) and thisSquare:getS() and AdjacentFreeTileFinder.privTrySquare(thisSquare, thisSquare:getS()) then
		if thisPlayer:getY() >= thisSquare:getS():getY() then
			ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getS()))
			return true
		else
			S = 1
		end
	end
	
	if ((spriteName == "fixtures_bathroom_01_26") or (spriteName == "fixtures_bathroom_01_55")) and thisSquare:getN() and AdjacentFreeTileFinder.privTrySquare(thisSquare, thisSquare:getN()) then
		if thisPlayer:getY() <= thisSquare:getN():getY() then
			ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getN()))
			return true
		else
			N = 1
		end
	end

	if ((spriteName == "fixtures_bathroom_01_25") or (spriteName == "fixtures_bathroom_01_52")) and thisSquare:getE() and AdjacentFreeTileFinder.privTrySquare(thisSquare, thisSquare:getE()) then
		if thisPlayer:getX() >= thisSquare:getE():getX() then
			ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getE()))
			return true
		else
			E = 1
		end
	end

	if ((spriteName == "fixtures_bathroom_01_25") or (spriteName == "fixtures_bathroom_01_52")) and thisSquare:getW() and AdjacentFreeTileFinder.privTrySquare(thisSquare, thisSquare:getW()) then
		if thisPlayer:getX() >= thisSquare:getW():getX() then
			ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getW()))
			return true
		else
			W = 1
		end
	end

	if S then
		ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getS()))
		return true
	elseif N then
		ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getN()))
		return true
	elseif W then
		ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getW()))
		return true
	elseif E then
		ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getE()))
		return true
	end
	
	return false
end

local function BathRemoveMakeup(character, item)
	if item then
		character:removeWornItem(item);
		character:getInventory():Remove(item);
	end
end

local function BathRemoveAllMakeup(character)
	local item = character:getWornItem("MakeUp_FullFace");
	BathRemoveMakeup(character, item);
	item = character:getWornItem("MakeUp_Eyes");
	BathRemoveMakeup(character, item);
	item = character:getWornItem("MakeUp_EyesShadow");
	BathRemoveMakeup(character, item);
	item = character:getWornItem("MakeUp_Lips");
	BathRemoveMakeup(character, item);
end

BathContextMenu.onAction = function(worldobjects, player, BathMaster, BathBottom, WaterUsage, spriteName, secondSpriteName, soap, ActionType, BubbleBath)

	local LSPrepareBath = require "TimedActions/LSPrepareBath"
	local LSUseTub = require "TimedActions/LSUseTub"
	local LSChangeClothes = require "TimedActions/PlayerChangeClothes"
	local BathData = BathMaster:getModData()

	if BathContextMenu.walkToFront(player, BathMaster) then

		if ActionType == "IsUse" then
			BathRemoveAllMakeup(player)
			local facingDir = getFacingDir(spriteName)
			local wasDressed = isPlayerDressed(player)

			if wasDressed then
				ISTimedActionQueue.add(LSChangeClothes:new(player, BathMaster, "isBathNoLaundryStart"))
			end		
			ISTimedActionQueue.add(LSPrepareBath:new(player, BathMaster, BathBottom, WaterUsage, facingDir, BubbleBath))
			--ISTimedActionQueue.add(ISWalkToTimedAction:new(player, Bath:getSquare()))
			ISTimedActionQueue.add(LSUseTub:new(player, BathMaster, BathBottom, spriteName, secondSpriteName, facingDir, wasDressed, BubbleBath))
		elseif ActionType == "IsClean" then
			--ISTimedActionQueue.add(ISWalkToTimedAction:new(player, Bath:getSquare()))
			ISTimedActionQueue.add(LSCleanObject:new(player, BathMaster, BathBottom, WaterUsage, 20, "Bob_Cleaning_Low", secondSpriteName, soap, spriteName));--player, object 1, object 2, duration, difficulty, animation, item1, item2, obj1 spriteName
		end
	end
end

local function setTileCondition(Obj1, Obj2, Level, Number)

	Obj1:getModData().ConditionLevel = Level
	Obj1:getModData().Condition = Number
	Obj2:getModData().ConditionLevel = Level
	Obj2:getModData().Condition = Number

end

BathContextMenu.onDebug = function(worldobjects, player, BathMaster, BathBottom, spriteName, secondSpriteName, Action)

	if Action == "IsDebugReset" then
		setTileCondition(BathMaster, BathBottom, 0, 0)
		updateBathSprites(BathMaster, BathMaster:getModData(), spriteName)
		updateBathSprites(BathBottom, BathBottom:getModData(), secondSpriteName)
		player:Say("Bathtub Reset")
	elseif Action == "IsDebugAddDirt" then
		if BathMaster:getModData().ConditionLevel < 1 then
			setTileCondition(BathMaster, BathBottom, 1, 30)
		elseif BathMaster:getModData().ConditionLevel == 1 then
			setTileCondition(BathMaster, BathBottom, 2, 60)
		elseif BathMaster:getModData().ConditionLevel == 2 then
			setTileCondition(BathMaster, BathBottom, 3, 100)
		end
		updateBathSprites(BathMaster, BathMaster:getModData(), spriteName)
		updateBathSprites(BathBottom, BathBottom:getModData(), secondSpriteName)
		player:Say("Dirt Added")
	end
end

