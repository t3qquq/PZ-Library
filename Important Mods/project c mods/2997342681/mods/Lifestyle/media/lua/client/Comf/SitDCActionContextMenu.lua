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

SitDCActionMenu = {};

SitDCActionMenu.doBuildMenu = function(player, context, worldobjects)
 
	local DISABLE = true
 
	if DISABLE then return; end
 
    local thisPlayer = getSpecificPlayer(player)

    if thisPlayer:getVehicle() then return; end
	
	if thisPlayer:isSitOnGround() then
		return
	end
	
	if thisPlayer:isSneaking() then
		return
	end
	
	local TargetObject = nil
	local ChairOrCouch = nil
	local spriteName = nil
	local comfortVal
	local beautyVal
	local TableObject
	local surfaceHeight

	local objects = {}
	for _,object in ipairs(worldobjects) do
		local square = object:getSquare()
		if square then
			for i=1,square:getObjects():size() do
				local thisObject = square:getObjects():get(i-1)

				local thisSprite = thisObject:getSprite()
				
				if thisSprite ~= nil then
				
					local properties = thisObject:getSprite():getProperties()

					if properties ~= nil then
						local groupName = nil
						local customName = nil
						local thisSpriteName = nil
					
						--local thisSprite = thisObject:getSprite()
						if thisSprite:getName() then
							thisSpriteName = thisSprite:getName()
							--print("Sprite Name is " .. spriteName)
						end
					
						if properties:Is("GroupName") then
							groupName = properties:Val("GroupName")
							--print("GroupName: " .. groupName);
						end
					
						if properties:Is("CustomName") then
							customName = properties:Val("CustomName")
							--print("CustomName: " .. customName);
						end

						if properties:Is("Surface") then
							surfaceHeight = properties:Val("Surface")
							print("surfaceHeight: " .. surfaceHeight);
						end

						if groupName == "Modern Red" and customName == "Chair" then
							TargetObject = thisObject;
							ChairOrCouch = "Modern Red Chair"
							spriteName = thisSpriteName;
							comfortVal = 0.70
							beautyVal = 4
						elseif groupName == "Black Office" and customName == "Chair" then
							TargetObject = thisObject;
							ChairOrCouch = "Black Office Chair"
							spriteName = thisSpriteName;
							comfortVal = 0.45
							beautyVal = 1
						elseif groupName == "Grey" and customName == "Chair" then
							TargetObject = thisObject;
							ChairOrCouch = "Grey Chair"
							spriteName = thisSpriteName;
							comfortVal = 0.35
							beautyVal = -0.5
						elseif customName == "Table" and surfaceHeight and surfaceHeight >= 25 then
							TableObject = thisObject
						end
					end
				end
			end
		end
	end

	if not TargetObject then return end
	if not TableObject then return end

	local ConnectedObject = "none"

	local occupied = false
    local playersList = {}
	for x = TargetObject:getX()-0.5,TargetObject:getX()+0.5 do
		for y = TargetObject:getY()-0.5,TargetObject:getY()+0.5 do
			local square = getCell():getGridSquare(x,y,TargetObject:getZ());
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
			if v:getUsername() ~= thisPlayer:getUsername() then
				occupied = true
			end
		end
	end

	local properties = TargetObject:getSprite():getProperties()
	local facing = nil
	local Isfacing
	local X
	local Y
	--local Quality = "normal"
	local actionType = "Bob_IsSittingLoop"
	local NewObject = "none"
	local OriginalSprite = TargetObject:getSprite()
	local SeatBack = "none"
	local ConnectedObjectNew = "none"
	local contextMenu = nil;
	contextMenu = "ContextMenu_Sit_Action"
	
	if properties:Is("Facing") then
		facing = properties:Val("Facing")
	else
		return
	end

	actionType = "Bob_IsSittingLoopN"

	if facing == "S" and ChairOrCouch == "Piano Stool" then
		facing = "W"
		Isfacing = "W"
		X = TargetObject:getSquare():getX()
		Y = TargetObject:getSquare():getY() - 10
	elseif facing == "E" and ChairOrCouch == "Piano Stool" then
		facing = "N"
		Isfacing = "N"
		X = TargetObject:getSquare():getX() - 10
		Y = TargetObject:getSquare():getY()
	elseif facing == "S" then
		Isfacing = "S"
		X = TargetObject:getSquare():getX()
		Y = TargetObject:getSquare():getY() + 10
	elseif facing == "E" then
		Isfacing = "E"
		X = TargetObject:getSquare():getX() + 10
		Y = TargetObject:getSquare():getY()
	elseif facing == "W" then
		Isfacing = "W"
		X = TargetObject:getSquare():getX() - 10
		Y = TargetObject:getSquare():getY()
	elseif facing == "N" then
		Isfacing = "N"
		X = TargetObject:getSquare():getX()
		Y = TargetObject:getSquare():getY() - 10
	end

	if ChairOrCouch == "Red Comfy Chair" then
		if facing == "S" then
			--NewObject = "LS_Chairs_8"
			--actionType = "Bob_IsSittingLoopN"
		elseif facing == "N" then
			NewObject = "LS_Chairs_13"--11
			SeatBack = "LS_Chairs_15"
			--actionType = "Bob_IsSittingLoopN"
		elseif facing == "W" then
			NewObject = "LS_Chairs_12"--10
			SeatBack = "LS_Chairs_14"
			--actionType = "Bob_IsSittingLoopN"
		elseif facing == "E" then
			--NewObject = "LS_Chairs_9"
			--actionType = "Bob_IsSittingLoopN"
		end
	elseif ChairOrCouch == "Green Comfy Chair" then
		if facing == "S" then

		elseif facing == "N" then
			NewObject = "LS_Chairs_37"--11
			SeatBack = "LS_Chairs_39"

		elseif facing == "W" then
			NewObject = "LS_Chairs_36"--10
			SeatBack = "LS_Chairs_38"

		elseif facing == "E" then

		end
	elseif ChairOrCouch == "Blue Rattan Chair" then
		if facing == "S" then
			--actionType = "Bob_IsSittingLoopN"
		elseif facing == "N" then
			NewObject = "LS_Chairs_9"
			SeatBack = "LS_Chairs_11"
			--actionType = "Bob_IsSittingLoopN"
		elseif facing == "W" then
			NewObject = "LS_Chairs_8"
			SeatBack = "LS_Chairs_10"
			--actionType = "Bob_IsSittingLoopN"
		elseif facing == "E" then
			--actionType = "Bob_IsSittingLoopN"
		end
	elseif ChairOrCouch == "Purple Rattan Chair" then
		if facing == "S" then

		elseif facing == "N" then
			NewObject = "LS_Chairs_33"--11
			SeatBack = "LS_Chairs_35"

		elseif facing == "W" then
			NewObject = "LS_Chairs_32"--10
			SeatBack = "LS_Chairs_34"

		elseif facing == "E" then

		end
	elseif ChairOrCouch == "Fancy White Chair" then
		if facing == "S" then

		elseif facing == "N" then
			NewObject = "LS_Chairs_49"--11
			SeatBack = "LS_Chairs_51"

		elseif facing == "W" then
			NewObject = "LS_Chairs_48"--10
			SeatBack = "LS_Chairs_50"

		elseif facing == "E" then

		end
	elseif ChairOrCouch == "Modern Red Chair" then
		if facing == "S" then

		elseif facing == "N" then
			NewObject = "LS_Chairs_53"--11
			SeatBack = "LS_Chairs_55"

		elseif facing == "W" then
			NewObject = "LS_Chairs_52"--10
			SeatBack = "LS_Chairs_54"

		elseif facing == "E" then

		end
	elseif ChairOrCouch == "Black Office Chair" then
		if facing == "S" then

		elseif facing == "N" then
			NewObject = "LS_Chairs_57"--11
			SeatBack = "LS_Chairs_59"

		elseif facing == "W" then
			NewObject = "LS_Chairs_56"--10
			SeatBack = "LS_Chairs_58"

		elseif facing == "E" then

		end
	elseif ChairOrCouch == "Grey Chair" then
		if facing == "S" then

		elseif facing == "N" then
			NewObject = "LS_Chairs_61"--11
			SeatBack = "LS_Chairs_63"

		elseif facing == "W" then
			NewObject = "LS_Chairs_60"--10
			SeatBack = "LS_Chairs_62"

		elseif facing == "E" then

		end
	elseif ChairOrCouch == "Red Comfy Couch" then
		contextMenu = "ContextMenu_Sit_Action_Couch"
		if facing == "S" and spriteName == "furniture_seating_indoor_01_0" then
			--NewObject = "LS_Chairs_0"
			--actionType = "Bob_IsSittingLoopN"
			ChairOrCouch = "Red Comfy Couch B"
		elseif facing == "S" then
			--actionType = "Bob_IsSittingLoopN"
			--NewObject = "LS_Chairs_1"
		elseif  facing == "N" and spriteName == "furniture_seating_indoor_01_6" then
			NewObject = "LS_Chairs_16"
			--actionType = "Bob_IsSittingLoopN"
			SeatBack = "LS_Chairs_20"
			ChairOrCouch = "Red Comfy Couch B"
		elseif facing == "N" then
			NewObject = "LS_Chairs_19"
			--actionType = "Bob_IsSittingLoopN"
			SeatBack = "LS_Chairs_23"
		elseif  facing == "W" and spriteName == "furniture_seating_indoor_01_4" then
			NewObject = "LS_Chairs_18"
			--actionType = "Bob_IsSittingLoopN"
			SeatBack = "LS_Chairs_22"
			ChairOrCouch = "Red Comfy Couch B"
		elseif facing == "W" then
			NewObject = "LS_Chairs_17"
			--actionType = "Bob_IsSittingLoopN"
			SeatBack = "LS_Chairs_21"
		elseif  facing == "E" and spriteName == "furniture_seating_indoor_01_2" then
			--NewObject = "LS_Chairs_2"
			--actionType = "Bob_IsSittingLoopN"
			ChairOrCouch = "Red Comfy Couch B"
		elseif facing == "E" then
			--NewObject = "LS_Chairs_3"
			--actionType = "Bob_IsSittingLoopN"
		end
	elseif ChairOrCouch == "Green Comfy Couch" then
		contextMenu = "ContextMenu_Sit_Action_Couch"
		if facing == "S" and spriteName == "furniture_seating_indoor_01_17" then
			ChairOrCouch = "Green Comfy Couch B"
		elseif facing == "S" then
		
		elseif  facing == "N" and spriteName == "furniture_seating_indoor_01_20" then
			NewObject = "LS_Chairs_40"
			SeatBack = "LS_Chairs_44"
			ChairOrCouch = "Green Comfy Couch B"
		elseif facing == "N" then
			NewObject = "LS_Chairs_43"
			SeatBack = "LS_Chairs_47"
		elseif  facing == "W" and spriteName == "furniture_seating_indoor_01_22" then
			NewObject = "LS_Chairs_42"
			SeatBack = "LS_Chairs_46"
			ChairOrCouch = "Green Comfy Couch B"
		elseif facing == "W" then
			NewObject = "LS_Chairs_41"
			SeatBack = "LS_Chairs_45"
		elseif  facing == "E" and spriteName == "furniture_seating_indoor_01_18" then
			ChairOrCouch = "Green Comfy Couch B"
		elseif facing == "E" then

		end
	elseif ChairOrCouch == "Blue Rattan Couch" then
		contextMenu = "ContextMenu_Sit_Action_Couch"
		if facing == "S" and spriteName == "furniture_seating_indoor_02_34" then
			ChairOrCouch = "Blue Rattan Couch B"
		elseif facing == "S" then
		
		elseif  facing == "N" and spriteName == "furniture_seating_indoor_02_38" then
			NewObject = "LS_Chairs_2"
			SeatBack = "LS_Chairs_6"
			ChairOrCouch = "Blue Rattan Couch B"
		elseif facing == "N" then
			NewObject = "LS_Chairs_3"
			SeatBack = "LS_Chairs_7"
		elseif  facing == "W" and spriteName == "furniture_seating_indoor_02_36" then
			NewObject = "LS_Chairs_0"
			SeatBack = "LS_Chairs_4"
			ChairOrCouch = "Blue Rattan Couch B"
		elseif facing == "W" then
			NewObject = "LS_Chairs_1"
			SeatBack = "LS_Chairs_5"
		elseif  facing == "E" and spriteName == "furniture_seating_indoor_02_32" then
			ChairOrCouch = "Blue Rattan Couch B"
		elseif facing == "E" then

		end
	elseif ChairOrCouch == "Purple Rattan Couch" then
		contextMenu = "ContextMenu_Sit_Action_Couch"
		if facing == "S" and spriteName == "furniture_seating_indoor_01_26" then
			ChairOrCouch = "Purple Rattan Couch B"
		elseif facing == "S" then
		
		elseif  facing == "N" and spriteName == "furniture_seating_indoor_01_30" then
			NewObject = "LS_Chairs_26"
			SeatBack = "LS_Chairs_30"
			ChairOrCouch = "Purple Rattan Couch B"
		elseif facing == "N" then
			NewObject = "LS_Chairs_27"
			SeatBack = "LS_Chairs_31"
		elseif  facing == "W" and spriteName == "furniture_seating_indoor_01_28" then
			NewObject = "LS_Chairs_24"
			SeatBack = "LS_Chairs_28"
			ChairOrCouch = "Purple Rattan Couch B"
		elseif facing == "W" then
			NewObject = "LS_Chairs_25"
			SeatBack = "LS_Chairs_29"
		elseif  facing == "E" and spriteName == "furniture_seating_indoor_01_24" then
			ChairOrCouch = "Purple Rattan Couch B"
		elseif facing == "E" then

		end
	elseif ChairOrCouch == "Piano Stool" then
		contextMenu = "ContextMenu_Sit_Action_Stool"
		if facing == "W" then
			NewObject = "LS_Stools_0"--11
			ConnectedObjectNew = "LS_Stools_1"
			SeatBack = "LS_Stools_4"
			--actionType = "Bob_IsSittingLoopN"
		elseif facing == "N" then
			NewObject = "LS_Stools_3"--10
			ConnectedObjectNew = "LS_Stools_2"
			SeatBack = "LS_Stools_5"
			--actionType = "Bob_IsSittingLoopN"
		end
	end


		
---------------SIT

	local SitOption = context:addOption(getText(contextMenu),
		worldobjects,
		SitDCActionMenu.onAction,
		thisPlayer,
		TargetObject,
		ConnectedObject,
		NewObject,
		ChairOrCouch,
		X,
		Y,
		SeatBack,
		ConnectedObjectNew,
		Isfacing);	
		
		local tooltipSit = ISToolTip:new();
			tooltipSit:initialise();
			tooltipSit:setVisible(false);

		if occupied == true then--disable the option
			SitOption.notAvailable = true;
			descriptionS = " <RED>" .. getText("Tooltip_Sit_Occupied");
			tooltipSit.description = descriptionS
			SitOption.toolTip = tooltipSit
			--SitOption.iconTexture = getTexture('media/ui/fire2No_icon.png')
		else
			--SitOption.iconTexture = getTexture('media/ui/fire2_icon.png')
		end

-----------INFO
	--[[
	local InfoOption = context:addOption(getText("ContextMenu_Sit_Info"));

	local tooltip = ISToolTip:new();
		tooltip:initialise();
		tooltip:setVisible(false);
		
		description = getText("Tooltip_Sit_Comfort") .. " <RGB:1,1,0>" .. comfortVal .. " <WHITE>" .. getText(" | ") .. getText("Tooltip_Sit_Beauty") .. " <RGB:1,1,0>" .. beautyVal;
		tooltip.description = description
		InfoOption.toolTip = tooltip
	]]--
end

SitDCActionMenu.walkToFront = function(thisPlayer, thisObject, Isfacing)
	local frontSquare = nil
	local controllerSquare = nil
	local spriteName = thisObject:getSprite():getName()
	if not spriteName then
		return false
	end

	local properties = thisObject:getSprite():getProperties()
	
	local facing = Isfacing
	--if properties:Is("Facing") then
	--	facing = properties:Val("Facing")
	--else
	--	return
	--end

	--if spriteName == "recreational_01_10" or spriteName == "recreational_01_11" then
	--	facing = "N"
	--elseif spriteName == "recreational_01_14" or spriteName == "recreational_01_15" then
	--	facing = "W"
	--end

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

SitDCActionMenu.onAction = function(worldobjects, player, TargetObject, ConnectedObject, NewObject, ChairOrCouch, X, Y, SeatBack, ConnectedObjectNew, Isfacing)
	local DoSitAction = require "TimedActions/DoSitAction"
	if SitDCActionMenu.walkToFront(player, TargetObject, Isfacing) then
		ISTimedActionQueue.add(DoSitAction:new(player, TargetObject, ConnectedObject, NewObject, ChairOrCouch, X, Y, SeatBack, ConnectedObjectNew, Isfacing));
	end
end

Events.OnFillWorldObjectContextMenu.Add(SitDCActionMenu.doBuildMenu);
