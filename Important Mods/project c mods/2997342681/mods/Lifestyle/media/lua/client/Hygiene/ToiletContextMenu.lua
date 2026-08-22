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

ToiletContextMenu = {};

ToiletContextMenu.LookForTP = function(thisPlayer)

	local TPTypes = require "Properties/TPTypes"
	local containerList = ArrayList.new();
	local playerNum = thisPlayer and thisPlayer:getPlayerNum() or -1
	local ToiletPaper
	local TPQuality = "bad"
    for i,v in ipairs(getPlayerInventory(playerNum).inventoryPane.inventoryPage.backpacks) do
		containerList:add(v.inventory);
    end
    for i,v in ipairs(getPlayerLoot(playerNum).inventoryPane.inventoryPage.backpacks) do
		containerList:add(v.inventory);
    end

--	if #containerList > 0 then
--		for i,v in ipairs(containerList:getItems()) do
		for i=0,containerList:size()-1 do
			local container = containerList:get(i);
			for x=0,container:getItems():size() - 1 do
				local containerItem = container:getItems():get(x);
				
				for k,v in pairs(TPTypes) do

					if (containerItem:getFullType() == v.name) and not ToiletPaper then
						ToiletPaper = containerItem
						TPQuality = v.category
						break
					elseif (containerItem:getFullType() == v.name) and (TPQuality == "bad") and (v.category ~= "bad") then
						ToiletPaper = containerItem
						TPQuality = v.category
						break
					elseif (containerItem:getFullType() == v.name) and (TPQuality == "normal") and (v.category == "good") then
						ToiletPaper = containerItem
						TPQuality = v.category
						break
					end
				end
				if ToiletPaper and (TPQuality == "good") then
					break
				end
			end
			if ToiletPaper and (TPQuality == "good") then
				break
			end
		end

	if not ToiletPaper then
		return false
	else
		return ToiletPaper, TPQuality
	end
end


ToiletContextMenu.CheckTPTexture = function(toiletPaperQuality)

	local Icon = getTexture('media/ui/toiletNOPAPER_icon.png')
	local Icon2 = getTexture('media/ui/toiletRelaxNOPAPER_icon.png')

	if toiletPaperQuality == "bad" then
		Icon = getTexture('media/ui/toiletBAD_icon.png')
		Icon2 = getTexture('media/ui/toiletRelaxBAD_icon.png')
	elseif toiletPaperQuality == "normal" then
		Icon = getTexture('media/ui/toiletRAGS_icon.png')
		Icon2 = getTexture('media/ui/toiletRelaxRAGS_icon.png')
	elseif toiletPaperQuality == "good" then
		Icon = getTexture('media/ui/toilet_icon.png')
		Icon2 = getTexture('media/ui/toiletRelax_icon.png')
	end
	return Icon, Icon2

end


ToiletContextMenu.doBuildMenu = function(player, context, worldobjects, Toilet, spriteName, customName, groupName, DebugBuildOption)
 
    local thisPlayer = getSpecificPlayer(player)

	if not thisPlayer then return; end
    if thisPlayer:getVehicle() then return; end
	--if not thisPlayer:isSitOnGround() then return; end
	--if thisPlayer:isSneaking() then return; end
	
	local playerdata
	
	if thisPlayer:hasModData() then
		playerdata = thisPlayer:getModData()
	else
	return; end

	if not Toilet then return; end

	local waterUsage = 10
	local comfortVal = 0.35
	local beautyVal = 0.5

	if groupName == "Fancy" then
		waterUsage = 5
		comfortVal = 0.45
		beautyVal = 1
	elseif groupName == "Chemical" or groupName == "Wooden" then
		comfortVal = 0.15
		beautyVal = -5
		waterUsage = 20
	end

	local toiletData = Toilet:getModData()

	if not playerdata.bathroomNeed then
		playerdata.bathroomNeed = 0
	end

	if (Toilet:hasWater() and Toilet:getWaterAmount() < waterUsage) or not Toilet:hasWater() then 
	
	--thisPlayer:Say("not enough water in toilet")
	
	local RefuseOption = context:addOptionOnTop(getText("ContextMenu_Toilet_NoWater"));

	local tooltip = ISToolTip:new();
		tooltip:initialise();
		tooltip:setVisible(false);
		

		RefuseOption.notAvailable = true;
		description = " <RED>" .. getText("Tooltip_Toilet_NoWater");
		tooltip.description = description
		RefuseOption.toolTip = tooltip
		RefuseOption.iconTexture = getTexture('media/ui/toiletNO_icon.png')

	elseif (Toilet:hasWater() and Toilet:getWaterAmount() >= waterUsage) and not
	playerdata.IsDoingToilet and not thisPlayer:isSitOnGround() then

	--thisPlayer:Say("toilet with water")

		local flushBeforeUse = "IsUse"
		local flushBeforeUseRelax = "IsUseRelax"
		--if Toilet:hasModData() then
			--toiletData = Toilet:getModData()
		--end


	local thisDirtSprite


	if not toiletData.Condition then
		toiletData.Condition = 0
	end

	if toiletData.ConditionLevel then

		local dirtSprite
		local dirtSprite2
		local dirtSprite3
		if groupName == "Fancy" then
			if Toilet:getSprite():getProperties():Val("Facing") == "S" then
					dirtSprite = "LS_Misc_0"
					dirtSprite2 = "LS_Misc_8"
					dirtSprite3 = "LS_Misc_16"
				elseif Toilet:getSprite():getProperties():Val("Facing") == "E" then
					dirtSprite = "LS_Misc_1"
					dirtSprite2 = "LS_Misc_9"
					dirtSprite3 = "LS_Misc_17"
				elseif Toilet:getSprite():getProperties():Val("Facing") == "W" then
					dirtSprite = "LS_Misc_2"
					dirtSprite2 = "LS_Misc_10"
					dirtSprite3 = "LS_Misc_18"
				elseif Toilet:getSprite():getProperties():Val("Facing") == "N" then
					dirtSprite = "LS_Misc_3"
					dirtSprite2 = "LS_Misc_11"
					dirtSprite3 = "LS_Misc_19"
			end
		elseif groupName == "Low" then
			if Toilet:getSprite():getProperties():Val("Facing") == "S" then
					dirtSprite = "LS_Misc_4"
					dirtSprite2 = "LS_Misc_12"
					dirtSprite3 = "LS_Misc_20"
				elseif Toilet:getSprite():getProperties():Val("Facing") == "E" then
					dirtSprite = "LS_Misc_5"
					dirtSprite2 = "LS_Misc_13"
					dirtSprite3 = "LS_Misc_21"
				elseif Toilet:getSprite():getProperties():Val("Facing") == "W" then
					dirtSprite = "LS_Misc_6"
					dirtSprite2 = "LS_Misc_14"
					dirtSprite3 = "LS_Misc_22"
				elseif Toilet:getSprite():getProperties():Val("Facing") == "N" then
					dirtSprite = "LS_Misc_7"
					dirtSprite2 = "LS_Misc_15"
					dirtSprite3 = "LS_Misc_23"
			end
		end

		if toiletData.ConditionLevel == 1 and toiletData.Condition >= 30 then
			thisDirtSprite = dirtSprite
		elseif toiletData.ConditionLevel == 2 and toiletData.Condition >= 60 then
			thisDirtSprite = dirtSprite2
		elseif toiletData.ConditionLevel == 3 and toiletData.Condition >= 90 then
			thisDirtSprite = dirtSprite3
		end
	else
		toiletData.ConditionLevel = 0
	end

	if isClient() and thisDirtSprite then
		Toilet:setOverlaySprite(thisDirtSprite, true)
		Toilet:transmitUpdatedSpriteToServer()
		Toilet:transmitModData()
	--elseif isClient() then
		--self.toiletObject:transmitModData()
	elseif thisDirtSprite then
		Toilet:setOverlaySprite(thisDirtSprite, false)
	end

		local ToiletSounds = require("Hygiene/Tracks/ToiletSounds")

		local AvailableToiletSounds = {}
		
		for k,v in pairs(ToiletSounds) do
			if v.category == groupName then
				table.insert(AvailableToiletSounds, v)
			end
		end

		local randomNumber = ZombRand(#AvailableToiletSounds) + 1
		local randomTrack = AvailableToiletSounds[randomNumber]

		if toiletData.NeedsFlush then
	
			if not thisPlayer:HasTrait("Sloppy") then
				flushBeforeUse = "IsflushBeforeUse"
				flushBeforeUseRelax = "IsflushBeforeUseRelax"
			end
	
			local flushOption = context:addOptionOnTop(getText("ContextMenu_Toilet_Flush"),
			worldobjects,
			ToiletContextMenu.onAction,
			thisPlayer,
			Toilet,
			groupName,
			waterUsage,
			randomTrack.isflush,
			randomTrack.seatUp,
			randomTrack.seatDown,
			"IsFlush",
			false);
	
			if toiletData.IsClogged and toiletData.IsClogged >= 0 then
				flushOption.iconTexture = getTexture('media/ui/gearsBAD_icon.png')
			else
				flushOption.iconTexture = getTexture('media/ui/gears_icon.png')
			end
	
		end

		if toiletData.ConditionLevel > 0 then

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

			local cleanDuration
			cleanDuration = GetCleaningTimeTile(thisPlayer, Toilet, toiletData.Condition)
			--local cleanDuration = toiletData.Condition*200
			local cleanOption = context:addOptionOnTop(getText("ContextMenu_Toilet_Clean"),
			worldobjects,
			ToiletContextMenu.onAction,
			thisPlayer,
			Toilet,
			groupName,
			cleanDuration,
			CleanItem1,
			CleanItem2,
			randomTrack.seatUp,
			"IsClean",
			false);
	
			local tooltipClean = ISToolTip:new();
				tooltipClean:initialise();
				tooltipClean:setVisible(false);

			if CleanItem1 and CleanItem2 then--disable the option
				cleanOption.iconTexture = getTexture('media/ui/clean_icon.png')
			else
				cleanOption.notAvailable = true;
				descriptionC = " <RED>" .. getText("Tooltip_Toilet_CleanNoItem");
				tooltipClean.description = descriptionC
				cleanOption.toolTip = tooltipClean
				
				cleanOption.iconTexture = getTexture('media/ui/cleanNO_icon.png')
			end
	
		end

		if toiletData.IsClogged and toiletData.IsClogged >= 0 then

			local inventory = thisPlayer:getInventory();
			local it = inventory:getItems();
			local PlungerItem

			for j = 0, it:size()-1 do
				local item = it:get(j);
				if item:getType() == "Plunger" and not item:isBroken() and not PlungerItem then
					PlungerItem = item
				end
				if PlungerItem then
					break
				end
			end
			local unclogVal
			local rgbColor = " <RGB:1,1,1>"
			
			if toiletData.IsClogged > 0 then
				unclogVal = 100 - (math.floor(tonumber(toiletData.IsClogged)/30))
			else
				unclogVal = 0
			end
			if unclogVal < 30 then
				rgbColor = " <RGB:1,0,0>"
			elseif unclogVal < 60 then
				rgbColor = " <RGB:1,1,0>"
			else
				rgbColor = " <RGB:0,1,0>"
			end
			
			local unclogOption = context:addOptionOnTop(getText("ContextMenu_Toilet_Unclog"),
			worldobjects,
			ToiletContextMenu.onAction,
			thisPlayer,
			Toilet,
			groupName,
			waterUsage,
			randomTrack.isflush,
			randomTrack.seatUp,
			randomTrack.seatDown,
			"IsUnclog",
			PlungerItem);
	
			local tooltipUnclog = ISToolTip:new();
				tooltipUnclog:initialise();
				tooltipUnclog:setVisible(false);

			if not PlungerItem then--disable the option
				unclogOption.notAvailable = true;
				descriptionU = rgbColor .. getText("Tooltip_Toilet_UnclogNoItem");
				tooltipUnclog.description = descriptionU
				unclogOption.toolTip = tooltipUnclog
				unclogOption.iconTexture = getTexture('media/ui/unclogNO_icon.png')
			else
				descriptionU = getText("Tooltip_Toilet_UnclogProgress") .. rgbColor .. unclogVal .. " <RGB:1,1,1>" .. getText(" / 100 %");
				tooltipUnclog.description = descriptionU
				unclogOption.toolTip = tooltipUnclog
				unclogOption.iconTexture = getTexture('media/ui/unclog_icon.png')
			end
	
		end

		-------------------TOILET PAPER---------
		local toiletPaper, toiletPaperQuality = ToiletContextMenu.LookForTP(thisPlayer)
		local TPIcon = getTexture('media/ui/toiletNOPAPER_icon.png')
		local TPIcon2 = getTexture('media/ui/toiletRelaxNOPAPER_icon.png')
		if toiletPaper and toiletPaperQuality then
			TPIcon, TPIcon2 = ToiletContextMenu.CheckTPTexture(toiletPaperQuality)
		end
		----------

		--if (groupName == "Fancy" or groupName == "Low") and playerdata.bathroomNeed >= 30 then
		if (groupName == "Fancy" or groupName == "Low") and playerdata.LSMoodles["BladderNeed"].Value >= 0.2 then
			local doToiletOption = context:addOptionOnTop(getText("ContextMenu_Toilet_Use"),
			worldobjects,
			ToiletContextMenu.onAction,
			thisPlayer,
			Toilet,
			groupName,
			waterUsage,
			randomTrack.isflush,
			toiletPaperQuality,
			randomTrack.seatDown,
			flushBeforeUse,
			toiletPaper);

			local tooltipClog = ISToolTip:new();
				tooltipClog:initialise();
				tooltipClog:setVisible(false);

			if toiletData.IsClogged and toiletData.IsClogged >= 0 then--disable the option
				doToiletOption.notAvailable = true;
				descriptionC = " <RED>" .. getText("Tooltip_Toilet_Clogged");
				tooltipClog.description = descriptionC
				doToiletOption.toolTip = tooltipClog
				doToiletOption.iconTexture = getTexture('media/ui/toiletNO_icon.png')
			else
				doToiletOption.iconTexture = TPIcon
			end

			local inventory = thisPlayer:getInventory();
			local it = inventory:getItems();
			local readingMaterial

			for j = 0, it:size()-1 do
				local item = it:get(j);
				if item:getType() == "ComicBook" then
					readingMaterial = item
					break
				elseif item:getType() == "HottieZ" then
					readingMaterial = item
					break
				elseif item:getType() == "Book" then
					readingMaterial = item
					break
				elseif item:getType() == "Magazine" then
					readingMaterial = item
					break
				elseif item:getType() == "Newspaper" then
					readingMaterial = item
					break
				end
			end

			if readingMaterial and ((not toiletData.IsClogged)) then

				local doToiletRelaxOption = context:addOptionOnTop(getText("ContextMenu_Toilet_UseRelax"),
				worldobjects,
				ToiletContextMenu.onAction,
				thisPlayer,
				Toilet,
				groupName,
				waterUsage,
				randomTrack.isflush,
				toiletPaperQuality,
				readingMaterial,
				flushBeforeUseRelax,
				toiletPaper);

				doToiletRelaxOption.iconTexture = TPIcon2
				
			end

		elseif ((groupName == "Hanging" and not thisPlayer:isFemale()) or groupName == "Chemical" or groupName == "Wooden") and playerdata.LSMoodles["BladderNeed"].Value >= 0.2 then

			local doToiletAltOption = context:addOptionOnTop(getText("ContextMenu_Toilet_Use"),
			worldobjects,
			ToiletContextMenu.onAction,
			thisPlayer,
			Toilet,
			groupName,
			waterUsage,
			"none",
			"none",
			"none",
			"IsUse",
			toiletPaper);

		end
-------
	end--water
------

-----------INFO
	--[[
	local InfoOption = context:addOptionOnTop(getText("ContextMenu_Info"));

	local dirtVal = tonumber(toiletData.Condition) or 0

	local tooltip = ISToolTip:new();
		tooltip:initialise();
		tooltip:setVisible(false);
		if groupName ~= "Hanging" then
		description = getText("Tooltip_Sit_Comfort") .. " <RGB:1,1,0>" .. comfortVal .. " <RGB:1,1,1>" .. getText(" | ") .. getText("Tooltip_Sit_Beauty") .. " <RGB:1,1,0>" .. beautyVal .. " <RGB:1,1,1>" .. getText(" | ") .. getText("Tooltip_Object_Dirtiness") .. " <RGB:1,1,0>" .. dirtVal.. getText("/100");
		else
		description = getText("Tooltip_Sit_Beauty") .. " <RGB:1,1,0>" .. beautyVal .. " <RGB:1,1,1>" .. getText(" | ") .. getText("Tooltip_Object_Dirtiness") .. " <RGB:1,1,0>" .. dirtVal.. getText("/100");
		end
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

			local doToiletDebugResetOption = subMenu:addOptionOnTop(getText("ContextMenu_LSDebug_ResetTileData"),
			worldobjects,
			ToiletContextMenu.onDebug,
			thisPlayer,
			Toilet,
			groupName,
			"IsDebugReset");

		end
	end

end

ToiletContextMenu.walkToFront = function(thisPlayer, thisObject)

	local frontSquare = nil
	local controllerSquare = nil
	local spriteName = thisObject:getSprite():getName()
	if not spriteName then
		return false
	end

	local properties = thisObject:getSprite():getProperties()
	
	local facing = nil
	if properties:Is("Facing") then
		facing = properties:Val("Facing")
	else
		return
	end
	
	if facing == "S" then
		frontSquare = thisObject:getSquare():getS()
	elseif facing == "E" then
		frontSquare = thisObject:getSquare():getE()
	elseif facing == "W" then
		frontSquare = thisObject:getSquare():getW()
	elseif facing == "N" then
		frontSquare = thisObject:getSquare():getN()
	end
	
	if not frontSquare then
		return false
	end
	
	if not controllerSquare then
		controllerSquare = thisObject:getSquare()
	end

	if AdjacentFreeTileFinder.privTrySquare(controllerSquare, frontSquare) then
		ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, frontSquare))
		return true
	end
	return false
end

local newToiletObject

ToiletContextMenu.sitOnToilet = function(thisPlayer, thisObject, thisObjectType)

	local DoSitAction = require "TimedActions/DoSitAction"

	local NewObject = "none"
	local SeatBack = "none"
	local X
	local Y

	local OriginalSprite = thisObject:getSprite()
	local SeatBack = "none"

	local spriteName = thisObject:getSprite():getName()
	if not spriteName then
		return false
	end

	local properties = thisObject:getSprite():getProperties()
	
	local facing = nil
	if properties:Is("Facing") then
		facing = properties:Val("Facing")
	else
		return false
	end
	
	if facing == "S" then
		X = thisObject:getSquare():getX()
		Y = thisObject:getSquare():getY() + 10
	elseif facing == "E" then
		X = thisObject:getSquare():getX() + 10
		Y = thisObject:getSquare():getY()
	elseif facing == "W" then
		X = thisObject:getSquare():getX() - 10
		Y = thisObject:getSquare():getY()
	elseif facing == "N" then
		X = thisObject:getSquare():getX()
		Y = thisObject:getSquare():getY() - 10
	end

	local toiletData = thisObject:getModData()
	local DirtLevel = 0

	if toiletData and toiletData.ConditionLevel and toiletData.Condition then
		if toiletData.ConditionLevel == 1 and toiletData.Condition >= 30 then
			DirtLevel = 1
		elseif toiletData.ConditionLevel == 2 and toiletData.Condition >= 60 then
			DirtLevel = 2
		elseif toiletData.ConditionLevel == 3 and toiletData.Condition >= 90 then
			DirtLevel = 3
		end
	end

	if thisObjectType == "Fancy" then
		thisObjectType = "Fancy Toilet"
		if facing == "N" then
			if DirtLevel == 0 then
				NewObject = "LS_Misc_25"
				SeatBack = "LS_Misc_27"
			elseif DirtLevel == 1 then
				NewObject = "LS_Misc_25"
				SeatBack = "LS_Misc_27"
			elseif DirtLevel == 2 then
				NewObject = "LS_Misc_33"
				SeatBack = "LS_Misc_35"
			elseif DirtLevel == 3 then
				NewObject = "LS_Misc_41"
				SeatBack = "LS_Misc_43"
			end
		elseif facing == "W" then
			if DirtLevel == 0 then
				NewObject = "LS_Misc_24"
				SeatBack = "LS_Misc_26"
			elseif DirtLevel == 1 then
				NewObject = "LS_Misc_24"
				SeatBack = "LS_Misc_26"
			elseif DirtLevel == 2 then
				NewObject = "LS_Misc_32"
				SeatBack = "LS_Misc_34"
			elseif DirtLevel == 3 then
				NewObject = "LS_Misc_40"
				SeatBack = "LS_Misc_42"
			end
		end
	elseif thisObjectType == "Low" then
		thisObjectType = "Low Toilet"
		if facing == "N" then
			if DirtLevel == 0 then
				NewObject = "LS_Misc_29"
				SeatBack = "LS_Misc_31"
			elseif DirtLevel == 1 then
				NewObject = "LS_Misc_29"
				SeatBack = "LS_Misc_31"
			elseif DirtLevel == 2 then
				NewObject = "LS_Misc_37"
				SeatBack = "LS_Misc_39"
			elseif DirtLevel == 3 then
				NewObject = "LS_Misc_45"
				SeatBack = "LS_Misc_47"
			end
		elseif facing == "W" then
			if DirtLevel == 0 then
				NewObject = "LS_Misc_28"
				SeatBack = "LS_Misc_30"
			elseif DirtLevel == 1 then
				NewObject = "LS_Misc_28"
				SeatBack = "LS_Misc_30"
			elseif DirtLevel == 2 then
				NewObject = "LS_Misc_36"
				SeatBack = "LS_Misc_38"
			elseif DirtLevel == 3 then
				NewObject = "LS_Misc_44"
				SeatBack = "LS_Misc_46"
			end
		end
	elseif thisObjectType == "Chemical" then
		thisObjectType = "Chemical Toilet"
		if facing == "N" then
			NewObject = "LS_Chairs_37"
			SeatBack = "LS_Chairs_39"
		elseif facing == "W" then
			NewObject = "LS_Chairs_36"
			SeatBack = "LS_Chairs_38"
		end
	elseif thisObjectType == "Wooden" then
		thisObjectType = "Wooden Toilet"
		if facing == "N" then
			NewObject = "LS_Chairs_37"
			SeatBack = "LS_Chairs_39"
		elseif facing == "W" then
			NewObject = "LS_Chairs_36"
			SeatBack = "LS_Chairs_38"
		end
	end

	if thisPlayer and thisObject and facing and X and Y then
		if facing and ((facing == "W") or (facing == "N")) then
			if isClient() then
				thisObject:transmitModData()
				thisObject:setOverlaySprite(nil, true)
				thisObject:transmitUpdatedSpriteToServer()
			else
			thisObject:setOverlaySprite(nil, false)
			end
		end

		ISTimedActionQueue.add(DoSitAction:new(thisPlayer, thisObject, "none", NewObject, thisObjectType, X, Y, SeatBack, "none", {facing,true}))
		if NewObject ~= "none" then
			newToiletObject = facing
		end
			return true
	end
	return false
end


ToiletContextMenu.onAction = function(worldobjects, player, Toilet, Type, WaterUsage, Flush, SeatUp, SeatDown, ActionType, PlungerOrTPItem)
	local LSUseToilet = require "TimedActions/LSUseToilet"
	local LSFlushToilet = require "TimedActions/LSFlushToilet"
	local LSUnclogToilet = require "TimedActions/LSUnclogToilet"
	local toiletData = Toilet:getModData()
	local spriteName = Toilet:getSprite():getName()

	if player:isSneaking() then player:setSneaking(false); end

	if ToiletContextMenu.walkToFront(player, Toilet) then
	
		local dirtSprite
		local dirtSprite2
		local dirtSprite3
		if Type == "Fancy" then
			if Toilet:getSprite():getProperties():Val("Facing") == "S" then
					dirtSprite = "LS_Misc_0"
					dirtSprite2 = "LS_Misc_8"
					dirtSprite3 = "LS_Misc_16"
				elseif Toilet:getSprite():getProperties():Val("Facing") == "E" then
					dirtSprite = "LS_Misc_1"
					dirtSprite2 = "LS_Misc_9"
					dirtSprite3 = "LS_Misc_17"
				elseif Toilet:getSprite():getProperties():Val("Facing") == "W" then
					dirtSprite = "LS_Misc_2"
					dirtSprite2 = "LS_Misc_10"
					dirtSprite3 = "LS_Misc_18"
				elseif Toilet:getSprite():getProperties():Val("Facing") == "N" then
					dirtSprite = "LS_Misc_3"
					dirtSprite2 = "LS_Misc_11"
					dirtSprite3 = "LS_Misc_19"
			end
		elseif Type == "Low" then
			if Toilet:getSprite():getProperties():Val("Facing") == "S" then
					dirtSprite = "LS_Misc_4"
					dirtSprite2 = "LS_Misc_12"
					dirtSprite3 = "LS_Misc_20"
				elseif Toilet:getSprite():getProperties():Val("Facing") == "E" then
					dirtSprite = "LS_Misc_5"
					dirtSprite2 = "LS_Misc_13"
					dirtSprite3 = "LS_Misc_21"
				elseif Toilet:getSprite():getProperties():Val("Facing") == "W" then
					dirtSprite = "LS_Misc_6"
					dirtSprite2 = "LS_Misc_14"
					dirtSprite3 = "LS_Misc_22"
				elseif Toilet:getSprite():getProperties():Val("Facing") == "N" then
					dirtSprite = "LS_Misc_7"
					dirtSprite2 = "LS_Misc_15"
					dirtSprite3 = "LS_Misc_23"
			end
		end
	
		if ActionType == "IsflushBeforeUse" then
			ISTimedActionQueue.add(LSFlushToilet:new(player, Toilet, Type, WaterUsage, Flush, SeatDown, dirtSprite, dirtSprite2, dirtSprite3))
			if Type ~= "Hanging" and ToiletContextMenu.sitOnToilet(player, Toilet, Type) then
				ISTimedActionQueue.add(LSUseToilet:new(player, Toilet, Type, WaterUsage, Flush, SeatUp, false, dirtSprite, dirtSprite2, dirtSprite3, newToiletObject, PlungerOrTPItem))
			elseif Type == "Hanging" then
				ISTimedActionQueue.add(LSUseToilet:new(player, Toilet, Type, WaterUsage, Flush, SeatUp, false, dirtSprite, dirtSprite2, dirtSprite3, newToiletObject, PlungerOrTPItem))
			end
	
		elseif ActionType == "IsUse" then		
			if Type ~= "Hanging" and ToiletContextMenu.sitOnToilet(player, Toilet, Type) then
				ISTimedActionQueue.add(LSUseToilet:new(player, Toilet, Type, WaterUsage, Flush, SeatUp, false, dirtSprite, dirtSprite2, dirtSprite3, newToiletObject, PlungerOrTPItem))--seatdown is relax; seatup is tpquality
			elseif Type == "Hanging" then
				ISTimedActionQueue.add(LSUseToilet:new(player, Toilet, Type, WaterUsage, Flush, SeatUp, false, dirtSprite, dirtSprite2, dirtSprite3, newToiletObject, PlungerOrTPItem))
			end
		elseif ActionType == "IsflushBeforeUseRelax" then
			ISTimedActionQueue.add(LSFlushToilet:new(player, Toilet, Type, WaterUsage, Flush, SeatDown, dirtSprite, dirtSprite2, dirtSprite3))
			if Type ~= "Hanging" and ToiletContextMenu.sitOnToilet(player, Toilet, Type) then
				ISTimedActionQueue.add(LSUseToilet:new(player, Toilet, Type, WaterUsage, Flush, SeatUp, SeatDown, dirtSprite, dirtSprite2, dirtSprite3, newToiletObject, PlungerOrTPItem))
			end
		elseif ActionType == "IsUseRelax" then
			if Type ~= "Hanging" and ToiletContextMenu.sitOnToilet(player, Toilet, Type) then
				ISTimedActionQueue.add(LSUseToilet:new(player, Toilet, Type, WaterUsage, Flush, SeatUp, SeatDown, dirtSprite, dirtSprite2, dirtSprite3, newToiletObject, PlungerOrTPItem))--seatdown is relax
			end
		elseif ActionType == "IsFlush" then
			ISTimedActionQueue.add(LSFlushToilet:new(player, Toilet, Type, WaterUsage, Flush, SeatDown, dirtSprite, dirtSprite2, dirtSprite3))
		elseif ActionType == "IsUnclog" then
			if not PlungerOrTPItem:isEquipped() then
				ISTimedActionQueue.add(ISEquipWeaponAction:new(player, PlungerOrTPItem, 50, true, false))
			end
			local UnclogProgress
			if toiletData.IsClogged and toiletData.IsClogged > 0 then
				UnclogProgress = (tonumber(toiletData.IsClogged))
			else
				UnclogProgress = GetUnclogBaseDuration(player, Toilet)
			end
			ISTimedActionQueue.add(LSUnclogToilet:new(player, Toilet, Type, WaterUsage, Flush, SeatUp, SeatDown, PlungerOrTPItem, UnclogProgress));
		elseif ActionType == "IsClean" then
			--ISTimedActionQueue.add(LSCleanObject:new(player, Toilet, WaterUsage, 12, "Bob_Cleaning_Low", Flush, SeatUp, dirtSprite, dirtSprite2, "Sponge"));--player, object, duration, difficulty, animation, item1, item2 (if false use default: sponge and cleaning liquid)
			ISTimedActionQueue.add(LSCleanObject:new(player, Toilet, false, WaterUsage, 12, "Bob_Cleaning_Low", Flush, SeatUp, spriteName));--player, object 1, object 2, duration, difficulty, animation, item1, item2, obj1 spriteName
		end
	end
end


ToiletContextMenu.onDebug = function(worldobjects, player, Toilet, Type, Action)

	if Action == "IsDebugReset" then
		Toilet:getModData().ConditionLevel = 0
		Toilet:getModData().Condition = 0
		if isClient() then
		Toilet:transmitModData()
		Toilet:setOverlaySprite(nil, true)
		Toilet:transmitUpdatedSpriteToServer()
		else
		Toilet:setOverlaySprite(nil, false)
		end
		player:Say("Toilet Reset")
	end
end

--Events.OnFillWorldObjectContextMenu.Add(ToiletContextMenu.doBuildMenu);
