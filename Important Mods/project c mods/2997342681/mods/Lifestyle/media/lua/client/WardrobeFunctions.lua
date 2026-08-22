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

LSWardrobeContextMenu = {};

local function getAvailableStorageOptions()
	return {
		{CN="Wardrobe",GN="none"},
		{CN="Drawers",GN="none"},
		{CN="Rack",GN="none"},
		{CN="Clothes Stand",GN="none"},
		{CN="Locker",GN="Yellow Wall"},
		{CN="Locker",GN="Blue Wall"},
	}
end

local function isValidStorageType(properties)
	local isValidProp
	for k, prop in ipairs(getAvailableStorageOptions()) do
		if properties and ((properties:Val("CustomName") == prop.CN) or (prop.CN == "none")) and ((properties:Val("GroupName") == prop.GN) or (prop.GN == "none")) then
			isValidProp = true
			break
		end
	end
	return isValidProp
end

LSWardrobeContextMenu.doBuildMenu = function(player, context, worldobjects)
	local playerObj = getSpecificPlayer(player)
	local optiontype
	
	local Wardrobe
	local spriteName

	local objects = {}
	for _,object in ipairs(worldobjects) do
		local square = object:getSquare()
		if square then
			for i=1,square:getObjects():size() do
				local thisObject = square:getObjects():get(i-1)

				local thisSprite = thisObject:getSprite()
				
				if thisSprite ~= nil then
				
					local properties = thisObject:getSprite():getProperties()

					if properties then
						local groupName = nil
						local customName = nil
						local thisSpriteName = nil
					
						--local thisSprite = thisObject:getSprite()
						if thisSprite:getName() then
							thisSpriteName = thisSprite:getName()
							--print("Sprite Name is " .. spriteName)
						end

						if isValidStorageType(properties) then
							Wardrobe = thisObject;
							spriteName = thisSpriteName;
						end
					end
				end
			end
		end
	end

	if not Wardrobe then return end

					--local subMenuA = context:getNew(context);
					--context:addSubMenu(context:addOption(getText("ContextMenu_ChangeClothes")), subMenuA);
					local subMenuAOption = context:addOption(getText("ContextMenu_ChangeClothes"));
					local subMenuA = ISContextMenu:getNew(context);
					context:addSubMenu(subMenuAOption, subMenuA)
					subMenuAOption.iconTexture = getTexture('media/ui/clothes_icon.png')
					
					optiontype = "naked"
					local optionNaked = subMenuA:addOption(getText("ContextMenu_BirthdaySuit"), worldobjects, LSWardrobeContextMenu.onStartAction, playerObj, Wardrobe, optiontype);
					optionNaked.iconTexture = getTexture('media/ui/naked_icon.png')
					--optiontype = "test"
					--local optionWear = subMenuA:addOption(getText("wearTest"), worldobjects, LSWardrobeContextMenu.onStartAction, playerObj, Wardrobe, optiontype);
					optiontype = "casual"
					local optionWearCasual = subMenuA:addOption(getText("ContextMenu_ChangeToCasualClothes"), worldobjects, LSWardrobeContextMenu.onStartAction, playerObj, Wardrobe, optiontype);
					optiontype = "formal"
					local optionWearFormal = subMenuA:addOption(getText("ContextMenu_ChangeToFormalClothes"), worldobjects, LSWardrobeContextMenu.onStartAction, playerObj, Wardrobe, optiontype);
					optiontype = "gym"
					local optionWearGym = subMenuA:addOption(getText("ContextMenu_ChangeToGymClothes"), worldobjects, LSWardrobeContextMenu.onStartAction, playerObj, Wardrobe, optiontype);
					optiontype = "sleep"
					local optionWearSleep = subMenuA:addOption(getText("ContextMenu_ChangeToSleepClothes"), worldobjects, LSWardrobeContextMenu.onStartAction, playerObj, Wardrobe, optiontype);
					optiontype = "party"
					local optionWearParty = subMenuA:addOption(getText("ContextMenu_ChangeToPartyClothes"), worldobjects, LSWardrobeContextMenu.onStartAction, playerObj, Wardrobe, optiontype);
					optiontype = "summer"
					local optionWearSummer = subMenuA:addOption(getText("ContextMenu_ChangeToSummerClothes"), worldobjects, LSWardrobeContextMenu.onStartAction, playerObj, Wardrobe, optiontype);
					optiontype = "winter"
					local optionWearWinter = subMenuA:addOption(getText("ContextMenu_ChangeToWinterClothes"), worldobjects, LSWardrobeContextMenu.onStartAction, playerObj, Wardrobe, optiontype);
					optiontype = "work"
					local optionWearWork = subMenuA:addOption(getText("ContextMenu_ChangeToWorkClothes"), worldobjects, LSWardrobeContextMenu.onStartAction, playerObj, Wardrobe, optiontype);
					optiontype = "combat"
					local optionWearCombat = subMenuA:addOption(getText("ContextMenu_ChangeToCombatClothes"), worldobjects, LSWardrobeContextMenu.onStartAction, playerObj, Wardrobe, optiontype);

					local subMenuBOption = context:addOption(getText("ContextMenu_SetClothes"));
					local subMenuB = ISContextMenu:getNew(context);
					context:addSubMenu(subMenuBOption, subMenuB)
		
					optiontype = "casual"
					local optionSetCasual
					if #playerObj:getModData().CasualClothes > 0 then
						optionSetCasual = subMenuB:addOption(getText("ContextMenu_SetCasualClothes"), playerObj, LSWardrobeContextMenu.onSetAction, optiontype);
						optionSetCasual.iconTexture = getTexture('media/ui/okay_icon.png')
					else
						optionSetCasual = subMenuB:addOption(getText("ContextMenu_SetCasualClothes"), playerObj, AboutToSetCasualClothes)
						optionSetCasual.iconTexture = getTexture('media/ui/okayNo_icon.png')
					end
					optiontype = "formal"
					local optionSetFormal
					if #playerObj:getModData().FormalClothes > 0 then
						optionSetFormal = subMenuB:addOption(getText("ContextMenu_SetFormalClothes"), playerObj, LSWardrobeContextMenu.onSetAction, optiontype);
						optionSetFormal.iconTexture = getTexture('media/ui/okay_icon.png')
					else
						optionSetFormal = subMenuB:addOption(getText("ContextMenu_SetFormalClothes"), playerObj, AboutToSetFormalClothes);
						optionSetFormal.iconTexture = getTexture('media/ui/okayNo_icon.png')
					end
					optiontype = "gym"
					local optionSetGym
					if #playerObj:getModData().GymClothes > 0 then
						optionSetGym = subMenuB:addOption(getText("ContextMenu_SetGymClothes"), playerObj, LSWardrobeContextMenu.onSetAction, optiontype);
						optionSetGym.iconTexture = getTexture('media/ui/okay_icon.png')
					else
						optionSetGym = subMenuB:addOption(getText("ContextMenu_SetGymClothes"), playerObj, AboutToSetGymClothes);
						optionSetGym.iconTexture = getTexture('media/ui/okayNo_icon.png')
					end
					optiontype = "sleep"
					local optionSetSleep
					if #playerObj:getModData().SleepClothes > 0 then
						optionSetSleep = subMenuB:addOption(getText("ContextMenu_SetSleepClothes"), playerObj, LSWardrobeContextMenu.onSetAction, optiontype);
						optionSetSleep.iconTexture = getTexture('media/ui/okay_icon.png')
					else
						optionSetSleep = subMenuB:addOption(getText("ContextMenu_SetSleepClothes"), playerObj, AboutToSetSleepClothes);
						optionSetSleep.iconTexture = getTexture('media/ui/okayNo_icon.png')
					end
					optiontype = "party"
					local optionSetParty
					if #playerObj:getModData().PartyClothes > 0 then
						optionSetParty = subMenuB:addOption(getText("ContextMenu_SetPartyClothes"), playerObj, LSWardrobeContextMenu.onSetAction, optiontype);
						optionSetParty.iconTexture = getTexture('media/ui/okay_icon.png')
					else
						optionSetParty = subMenuB:addOption(getText("ContextMenu_SetPartyClothes"), playerObj, AboutToSetPartyClothes);
						optionSetParty.iconTexture = getTexture('media/ui/okayNo_icon.png')
					end
					optiontype = "summer"
					local optionSetSummer
					if #playerObj:getModData().SummerClothes > 0 then
						optionSetSummer = subMenuB:addOption(getText("ContextMenu_SetSummerClothes"), playerObj, LSWardrobeContextMenu.onSetAction, optiontype);
						optionSetSummer.iconTexture = getTexture('media/ui/okay_icon.png')
					else
						optionSetSummer = subMenuB:addOption(getText("ContextMenu_SetSummerClothes"), playerObj, AboutToSetSummerClothes);
						optionSetSummer.iconTexture = getTexture('media/ui/okayNo_icon.png')
					end
					optiontype = "winter"
					local optionSetWinter
					if #playerObj:getModData().WinterClothes > 0 then
						optionSetWinter = subMenuB:addOption(getText("ContextMenu_SetWinterClothes"), playerObj, LSWardrobeContextMenu.onSetAction, optiontype);
						optionSetWinter.iconTexture = getTexture('media/ui/okay_icon.png')
					else
						optionSetWinter = subMenuB:addOption(getText("ContextMenu_SetWinterClothes"), playerObj, AboutToSetWinterClothes);
						optionSetWinter.iconTexture = getTexture('media/ui/okayNo_icon.png')
					end
					optiontype = "work"
					local optionSetWork
					if #playerObj:getModData().WorkClothes > 0 then
						optionSetWork = subMenuB:addOption(getText("ContextMenu_SetWorkClothes"), playerObj, LSWardrobeContextMenu.onSetAction, optiontype);
						optionSetWork.iconTexture = getTexture('media/ui/okay_icon.png')
					else
						optionSetWork = subMenuB:addOption(getText("ContextMenu_SetWorkClothes"), playerObj, AboutToSetWorkClothes);
						optionSetWork.iconTexture = getTexture('media/ui/okayNo_icon.png')
					end
					optiontype = "combat"
					local optionSetCombat
					if #playerObj:getModData().CombatClothes > 0 then
						optionSetCombat = subMenuB:addOption(getText("ContextMenu_SetCombatClothes"), playerObj, LSWardrobeContextMenu.onSetAction, optiontype);
						optionSetCombat.iconTexture = getTexture('media/ui/okay_icon.png')
					else
						optionSetCombat = subMenuB:addOption(getText("ContextMenu_SetCombatClothes"), playerObj, AboutToSetCombatClothes);
						optionSetCombat.iconTexture = getTexture('media/ui/okayNo_icon.png')
					end

		local tooltipSet = ISToolTip:new();
		tooltipSet:initialise();
		tooltipSet:setVisible(false);
		descriptionSet = getText("Tooltip_SetOutfit");
		tooltipSet.description = descriptionSet
		subMenuBOption.toolTip = tooltipSet
		subMenuBOption.iconTexture = getTexture('media/ui/setclothes_icon.png')


		local tooltipCasual = ISToolTip:new();
			tooltipCasual:initialise();
			tooltipCasual:setVisible(false);

		if #playerObj:getModData().CasualClothes < 1 then--disable the option
			optionWearCasual.notAvailable = true;
			descriptionC = " <RED>" .. getText("ContextMenu_ChangeToCasualClothes_Fail");
			tooltipCasual.description = descriptionC
			optionWearCasual.toolTip = tooltipCasual
			optionWearCasual.iconTexture = getTexture('media/ui/clothes_casualNO_icon.png')
		else
			optionWearCasual.iconTexture = getTexture('media/ui/clothes_casual_icon.png')
		end

		local tooltipFormal = ISToolTip:new();
			tooltipFormal:initialise();
			tooltipFormal:setVisible(false);

		if #playerObj:getModData().FormalClothes < 1 then--disable the option
			optionWearFormal.notAvailable = true;
			descriptionF = " <RED>" .. getText("ContextMenu_ChangeToFormalClothes_Fail");
			tooltipFormal.description = descriptionF
			optionWearFormal.toolTip = tooltipFormal
			optionWearFormal.iconTexture = getTexture('media/ui/clothes_formalNO_icon.png')
		else
			optionWearFormal.iconTexture = getTexture('media/ui/clothes_formal_icon.png')
		end

		local tooltipGym = ISToolTip:new();
			tooltipGym:initialise();
			tooltipGym:setVisible(false);

		if #playerObj:getModData().GymClothes < 1 then--disable the option
			optionWearGym.notAvailable = true;
			descriptionG = " <RED>" .. getText("ContextMenu_ChangeToGymClothes_Fail");
			tooltipGym.description = descriptionG
			optionWearGym.toolTip = tooltipGym
			optionWearGym.iconTexture = getTexture('media/ui/clothes_gymNO_icon.png')
		else
			optionWearGym.iconTexture = getTexture('media/ui/clothes_gym_icon.png')
		end

		local tooltipSleep = ISToolTip:new();
			tooltipSleep:initialise();
			tooltipSleep:setVisible(false);

		if #playerObj:getModData().SleepClothes < 1 then--disable the option
			optionWearSleep.notAvailable = true;
			descriptionS = " <RED>" .. getText("ContextMenu_ChangeToSleepClothes_Fail");
			tooltipSleep.description = descriptionS
			optionWearSleep.toolTip = tooltipSleep
			optionWearSleep.iconTexture = getTexture('media/ui/clothes_sleepNO_icon.png')
		else
			optionWearSleep.iconTexture = getTexture('media/ui/clothes_sleep_icon.png')
		end

		local tooltipParty = ISToolTip:new();
			tooltipParty:initialise();
			tooltipParty:setVisible(false);

		if #playerObj:getModData().PartyClothes < 1 then--disable the option
			optionWearParty.notAvailable = true;
			descriptionP = " <RED>" .. getText("ContextMenu_ChangeToPartyClothes_Fail");
			tooltipParty.description = descriptionP
			optionWearParty.toolTip = tooltipParty
			optionWearParty.iconTexture = getTexture('media/ui/clothes_partyNO_icon.png')
		else
			optionWearParty.iconTexture = getTexture('media/ui/clothes_party_icon.png')
		end

		local tooltipSummer = ISToolTip:new();
			tooltipSummer:initialise();
			tooltipSummer:setVisible(false);

		if #playerObj:getModData().SummerClothes < 1 then--disable the option
			optionWearSummer.notAvailable = true;
			descriptionS = " <RED>" .. getText("ContextMenu_ChangeToSummerClothes_Fail");
			tooltipSummer.description = descriptionS
			optionWearSummer.toolTip = tooltipSummer
			optionWearSummer.iconTexture = getTexture('media/ui/clothes_summerNO_icon.png')
		else
			optionWearSummer.iconTexture = getTexture('media/ui/clothes_summer_icon.png')
		end

		local tooltipWinter = ISToolTip:new();
			tooltipWinter:initialise();
			tooltipWinter:setVisible(false);

		if #playerObj:getModData().WinterClothes < 1 then--disable the option
			optionWearWinter.notAvailable = true;
			descriptionW = " <RED>" .. getText("ContextMenu_ChangeToWinterClothes_Fail");
			tooltipWinter.description = descriptionW
			optionWearWinter.toolTip = tooltipWinter
			optionWearWinter.iconTexture = getTexture('media/ui/clothes_winterNO_icon.png')
		else
			optionWearWinter.iconTexture = getTexture('media/ui/clothes_winter_icon.png')
		end

		local tooltipWork = ISToolTip:new();
			tooltipWork:initialise();
			tooltipWork:setVisible(false);

		if #playerObj:getModData().WorkClothes < 1 then--disable the option
			optionWearWork.notAvailable = true;
			descriptionWK = " <RED>" .. getText("ContextMenu_ChangeToWorkClothes_Fail");
			tooltipWork.description = descriptionWK
			optionWearWork.toolTip = tooltipWork
			optionWearWork.iconTexture = getTexture('media/ui/clothes_workNO_icon.png')
		else
			optionWearWork.iconTexture = getTexture('media/ui/clothes_work_icon.png')
		end

		local tooltipCombat = ISToolTip:new();
			tooltipCombat:initialise();
			tooltipCombat:setVisible(false);

		if #playerObj:getModData().CombatClothes < 1 then--disable the option
			optionWearCombat.notAvailable = true;
			descriptionCBT = " <RED>" .. getText("ContextMenu_ChangeToCombatClothes_Fail");
			tooltipCombat.description = descriptionCBT
			optionWearCombat.toolTip = tooltipCombat
			optionWearCombat.iconTexture = getTexture('media/ui/clothes_combatNO_icon.png')
		else
			optionWearCombat.iconTexture = getTexture('media/ui/clothes_combat_icon.png')
		end

end


LSWardrobeContextMenu.walkToFront = function(thisPlayer, thisObject)
	local frontSquare = nil
	local controllerSquare = nil
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
			frontSquare = thisObject:getSquare():getS()
		elseif facing == "E" then
			frontSquare = thisObject:getSquare():getE()
		elseif facing == "W" then
			frontSquare = thisObject:getSquare():getW()
		elseif facing == "N" then
			frontSquare = thisObject:getSquare():getN()
		end
	end
	
	if frontSquare then
		if AdjacentFreeTileFinder.privTrySquare(thisSquare, frontSquare) then
			ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, frontSquare))
			return true
		end
	end

	local freeSquare = (thisSquare:getS() or thisSquare:getE() or thisSquare:getW() or thisSquare:getN())

	if not freeSquare then
		if luautils.walkAdj(thisPlayer, thisSquare, true) then
		return true
		else
		return false
		end
	end

	--sometimes showers are blocked from one or more sides, so we find and move the player to the closes available free tile
	local N
	local S
	local E
	local W

	if thisSquare:getS() and AdjacentFreeTileFinder.privTrySquare(thisSquare, thisSquare:getS()) then
		if thisPlayer:getY() >= thisSquare:getS():getY() then
			ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getS()))
			return true
		else
			S = 1
		end
	end
	
	if thisSquare:getN() and AdjacentFreeTileFinder.privTrySquare(thisSquare, thisSquare:getN()) then
		if thisPlayer:getY() <= thisSquare:getN():getY() then
			ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getN()))
			return true
		else
			N = 1
		end
	end

	if thisSquare:getE() and AdjacentFreeTileFinder.privTrySquare(thisSquare, thisSquare:getE()) then
		if thisPlayer:getX() >= thisSquare:getE():getX() then
			ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getE()))
			return true
		else
			E = 1
		end
	end

	if thisSquare:getW() and AdjacentFreeTileFinder.privTrySquare(thisSquare, thisSquare:getW()) then
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

LSWardrobeContextMenu.onStartAction = function(worldobjects, player, Wardrobe, optiontype)
	if LSWardrobeContextMenu.walkToFront(player, Wardrobe) then
		local PlayerChangeClothes = require "TimedActions/PlayerChangeClothes"
		ISTimedActionQueue.add(PlayerChangeClothes:new(player, Wardrobe, optiontype));
	end
end

LSWardrobeContextMenu.onSetAction = function(player, optiontype)

	getSoundManager():playUISound("UI_Button_SELECT")
	local player = getPlayer()
	local thisplayer = getPlayer():getPlayerNum()
	local optiontype = optiontype
	--player:Say(tostring(optiontype))
	player:getModData().optionTypeWDB = tostring(optiontype)
	--local WardrobeConfirm = require "ISUI/WardrobeConfirm"
	local WardrobeConfirm = WardrobeConfirm:new(thisplayer);
    WardrobeConfirm:initialise();
    WardrobeConfirm:addToUIManager()

end

Events.OnFillWorldObjectContextMenu.Add(LSWardrobeContextMenu.doBuildMenu);
