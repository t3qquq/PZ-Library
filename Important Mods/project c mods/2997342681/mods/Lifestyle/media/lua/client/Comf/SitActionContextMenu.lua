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

SitActionMenu = {};

local function isArtwork(Art)
	if (not Art) or (not Art:hasModData()) or (not Art:getModData().movableData) or (not Art:getModData().movableData['artBeauty']) then return false; end
	return true
end

local function LSgetSeatingParams(TargetObject, ChairOrCouch, groupName, customName)
	local SeatingParamsTable, B, C, U, M, F = require "Properties/ComfortProperties", 0, 0.35, false, "soft", false
	for k, v in ipairs(SeatingParamsTable) do
		if string.find(ChairOrCouch, v.name) then B = v.beauty; C = v.comfort; U = v.isugly; M = v.mat; break; end
	end
	if customName == "Couch" then B = B*0.5; end
	F = TargetObject:getSprite():getProperties():Val("Facing")
	if ChairOrCouch == "Piano Stool" and F == "S" then F = "W"; end
	if ChairOrCouch == "Piano Stool" and F == "E" then F = "N"; end
	if isArtwork(TargetObject) then B = TargetObject:getModData().movableData['artBeauty']; end
	return B, C, U, M, F
end

local function LScombineStrings(str1, str2)
    return str1.." "..str2
end

local function LSSitActionMenucheckConditions(player, TargetObject)
	local thisPlayer = getSpecificPlayer(player)
	if (thisPlayer:getVehicle()) or (thisPlayer:isSitOnGround()) or (thisPlayer:isSneaking()) or (not TargetObject) then return false; end
	return thisPlayer
end

local function LScheckIsOccupied(thisPlayer, TargetObject)
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
				break
			end
		end
	end
	return occupied
end

local function LScheckForConnectedObj(spriteName, ChairOrCouch, TargetObject)
	local ConnectedObject, invertObj = false, false
	if ChairOrCouch ~= "Piano Stool" then return "none", invertObj; end
	--for _,object in ipairs(worldobjects) do
		for x = TargetObject:getX()-1,TargetObject:getX()+1 do
			for y = TargetObject:getY()-1,TargetObject:getY()+1 do
				local square = getCell():getGridSquare(x,y,TargetObject:getZ());
				if square then
					for i=1,square:getObjects():size() do
						local thisObject = square:getObjects():get(i-1)
						local thisSprite = thisObject:getSprite()
						if thisSprite then
							local properties = thisObject:getSprite():getProperties()
							if properties then
								local groupName, customName, thisSpriteName
								if thisSprite:getName() then thisSpriteName = thisSprite:getName(); end
								if properties:Is("GroupName") then groupName = properties:Val("GroupName"); end
								if properties:Is("CustomName") then customName = properties:Val("CustomName"); end
					
								if groupName and customName and thisSpriteName and (groupName == "Piano") and (customName == "Stool") and (spriteName ~= thisSpriteName) then
									if (thisSpriteName == "recreational_01_11") or (thisSpriteName == "recreational_01_14") then
										ConnectedObject = thisObject
									else
										invertObj = {}
										invertObj.obj = thisObject; invertObj.sprite = thisSpriteName
										ConnectedObject = TargetObject
									end
									break
								end
							end
						end
					end
				end
				if ConnectedObject then break; end
			end
			if ConnectedObject then break; end
		end
		--if ConnectedObject then break; end
	--end
	return ConnectedObject, invertObj
end

local function LSSitActionMenuGetXYPos(TargetObject, Isfacing, ChairOrCouch)
	local X, Y = TargetObject:getSquare():getX(), TargetObject:getSquare():getY()
	if (Isfacing == "W") and (ChairOrCouch == "Piano Stool") then Y = Y-10;
	elseif (Isfacing == "N") and (ChairOrCouch == "Piano Stool") then X = X-10;
	else
	--if (string.find(ChairOrCouch, "Couch")) then
		if Isfacing == "W" then X = X-10;
		elseif Isfacing == "N" then Y = Y-10;
		elseif Isfacing == "E" then X = X+10;
		elseif Isfacing == "S" then Y = Y+10; end
	end
	return X, Y
end

local function LSSitActionMenuGetExtendedObjs(ChairOrCouch, Isfacing)
	local NewObject, SeatBack, ConnectedObjectNew, seatAdded = "none", "none", "none", false
	--if (Isfacing == "S") or (Isfacing == "E") then return NewObject, SeatBack, ConnectedObjectNew; end
	--print("LSSitActionMenuGetExtendedObjs ChairOrCouch: "..ChairOrCouch)
	local SeatingSpritesTable = require "Properties/LSSpriteNameList"
	for k, v in ipairs(SeatingSpritesTable) do
		--print("LSSitActionMenuGetExtendedObjs v.name: "..v.name)
		if ChairOrCouch == v.name then seatAdded = true; end
		if (ChairOrCouch == v.name) and (Isfacing == v.facing) then NewObject = v.oldSprite; SeatBack = v.seatBackSprite; ConnectedObjectNew = v.connectedSprite; seatAdded = true; break; end
	end
	return NewObject, SeatBack, ConnectedObjectNew, seatAdded
end

local function LSSitActionMenuGetBSide(ChairOrCouch, spriteName)
	local addB
	if not (string.find(ChairOrCouch, "Couch")) then return false; end
	local CouchSpritesTable = require "Properties/LSCouchSpriteNameList"
	for n=1, #CouchSpritesTable do
		if (spriteName == CouchSpritesTable[n]) then addB = LScombineStrings(ChairOrCouch,"B"); break; end
	end
	return addB
end

local function LSSitActionMenuGetContextText(ChairOrCouch)
	local context = "ContextMenu_Sit_Action"
	if (string.find(ChairOrCouch, "Chair")) then return context; end
	if (string.find(ChairOrCouch, "Couch")) then context = "ContextMenu_Sit_Action_Couch";
	elseif (string.find(ChairOrCouch, "Stool")) then context = "ContextMenu_Sit_Action_Stool"; end
	return context
end

SitActionMenu.doBuildMenu = function(player, context, worldobjects, TargetObject, spriteName, customName, groupName, DebugBuildOption)
	--print("SitActionMenu doBuildMenu")
	--print("SitActionMenu LSSitActionMenucheckConditions")
	local thisPlayer = LSSitActionMenucheckConditions(player, TargetObject)
	if not thisPlayer then return; end
	if isArtwork(TargetObject) then ArtCardContextMenu.doBuildMenu(player, context, worldobjects, TargetObject, spriteName, customName, groupName, DebugBuildOption); end
	--print("SitActionMenu LScombineStrings")
	local ChairOrCouch = LScombineStrings(groupName, customName)
	--print("SitActionMenu LSgetSeatingParams")
	local seatingParams = {}
	seatingParams.comfortVal, seatingParams.beautyVal, seatingParams.isUgly, seatingParams.matType, seatingParams.Isfacing = LSgetSeatingParams(TargetObject, ChairOrCouch, groupName, customName)
	--print("SitActionMenu LScheckIsOccupied")
	local occupied = LScheckIsOccupied(thisPlayer, TargetObject)
	if occupied then return; end
	--print("SitActionMenu LScheckForConnectedObj")
	local ConnectedObject, invertObj = LScheckForConnectedObj(spriteName, ChairOrCouch, TargetObject)
	if not ConnectedObject then return; end
	if invertObj then TargetObject = invertObj.obj; spriteName = invertObj.sprite; end
	--print("SitActionMenu LSSitActionMenuGetXYPos")
	local X, Y = LSSitActionMenuGetXYPos(TargetObject, seatingParams.Isfacing, ChairOrCouch)
	--print("SitActionMenu LSSitActionMenuGetBSide")
	local addB = LSSitActionMenuGetBSide(ChairOrCouch, spriteName)
	if addB then ChairOrCouch = addB; end
	--print("SitActionMenu LSSitActionMenuGetExtendedObjs")
	local NewObject, SeatBack, ConnectedObjectNew, seatAdded = LSSitActionMenuGetExtendedObjs(ChairOrCouch, seatingParams.Isfacing)
	if not seatAdded then return; end
	local contextMenu = LSSitActionMenuGetContextText(ChairOrCouch)

---------------SIT
	--print("SitActionMenu SitOption")
	local SitOption = context:addOptionOnTop(getText(contextMenu),
		worldobjects,
		SitActionMenu.onAction,
		thisPlayer,
		TargetObject,
		ConnectedObject,
		NewObject,
		ChairOrCouch,
		X,
		Y,
		SeatBack,
		ConnectedObjectNew,
		seatingParams);

		SitOption.iconTexture = getTexture('media/ui/moodles/Comfort.png')

-----------INFO
	--[[
	local InfoOption = context:addOptionOnTop(getText("ContextMenu_Sit_Info"));

	local tooltip = ISToolTip:new();
		tooltip:initialise();
		tooltip:setVisible(false);
		
		description = getText("Tooltip_Sit_Comfort") .. " <RGB:1,1,0>" .. seatingParams.comfortVal .. " <WHITE>" .. getText(" | ") .. getText("Tooltip_Sit_Beauty") .. " <RGB:1,1,0>" .. seatingParams.beautyVal;
		tooltip.description = description
		InfoOption.toolTip = tooltip
	]]--
end

local function getSquareClosestToPlayer(thisPlayer, thisObject, facing, controllerSquare)
	local sqr1, sqr2, sqr1Aprox, sqr2Aprox
	if (facing == "S") or (facing == "N") then
		if AdjacentFreeTileFinder.privTrySquare(controllerSquare, thisObject:getSquare():getE()) then sqr1 = thisObject:getSquare():getAdjacentSquare(IsoDirections.E); sqr1Aprox = thisObject:getSquare():getE(); end
		if AdjacentFreeTileFinder.privTrySquare(controllerSquare, thisObject:getSquare():getW()) then sqr2 = thisObject:getSquare():getAdjacentSquare(IsoDirections.W); sqr2Aprox = thisObject:getSquare():getW(); end
	elseif (facing == "W") or (facing == "E") then
		if AdjacentFreeTileFinder.privTrySquare(controllerSquare, thisObject:getSquare():getN()) then sqr1 = thisObject:getSquare():getAdjacentSquare(IsoDirections.N); sqr1Aprox = thisObject:getSquare():getN(); end
		if AdjacentFreeTileFinder.privTrySquare(controllerSquare, thisObject:getSquare():getS()) then sqr2 = thisObject:getSquare():getAdjacentSquare(IsoDirections.S); sqr2Aprox = thisObject:getSquare():getS(); end
	end
	if sqr1 and sqr2 then
		if (thisPlayer:getSquare():DistTo(sqr1)) <= (thisPlayer:getSquare():DistTo(sqr2)) then return sqr1Aprox; end
		return sqr2Aprox
	end
	if sqr1 then return sqr1Aprox; end
	if sqr2 then return sqr2Aprox; end
	
	return false
end

SitActionMenu.walkToFront = function(thisPlayer, thisObject, Isfacing, ChairOrCouch)
	local frontSquare
	local controllerSquare = nil
	local spriteName = thisObject:getSprite():getName()
	local ChairFromFront
	local adjSquare
	if not spriteName then
		return false, false
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

	if not frontSquare then
		if facing == "S" then
			frontSquare = thisObject:getSquare():getS()
			adjSquare = thisObject:getSquare():getAdjacentSquare(IsoDirections.S)
		elseif facing == "E" then
			frontSquare = thisObject:getSquare():getE()
			adjSquare = thisObject:getSquare():getAdjacentSquare(IsoDirections.E)
		elseif facing == "W" then
			frontSquare = thisObject:getSquare():getW()
			adjSquare = thisObject:getSquare():getAdjacentSquare(IsoDirections.W)
		elseif facing == "N" then
			frontSquare = thisObject:getSquare():getN()
			adjSquare = thisObject:getSquare():getAdjacentSquare(IsoDirections.N)
		end
	end

	if not controllerSquare then
		controllerSquare = thisObject:getSquare()
	end

	if adjSquare and ((adjSquare:isWallTo(controllerSquare)) or (adjSquare:isWindowTo(controllerSquare)) or (adjSquare:isDoorTo(controllerSquare)) or (adjSquare:HasStairs()) or
	(controllerSquare:isWallTo(adjSquare)) or (controllerSquare:isWindowTo(adjSquare)) or (controllerSquare:isDoorTo(adjSquare))) then return false, false; end

	--if (controllerSquare:isWallTo(adjSquare)) then print("SitActionMenu.walkToFront - controllerSquare isWallTo adjSquare"); end
	--if (controllerSquare:isBlockedTo(adjSquare)) then print("SitActionMenu.walkToFront - controllerSquare isBlockedTo adjSquare"); end
	--if not (adjSquare:isCanSee(thisPlayer:getPlayerIndex())) then print("SitActionMenu.walkToFront - controllerSquare isWallTo adjSquare"); end

	if (string.find(ChairOrCouch, "Chair")) and ((not frontSquare) or (not AdjacentFreeTileFinder.privTrySquare(controllerSquare, frontSquare))) then
		local sideSqr = getSquareClosestToPlayer(thisPlayer, thisObject, facing, controllerSquare)
		if sideSqr then frontSquare = sideSqr; end
		--[[
		if (facing == "S") or (facing == "N") then
			local sideSqr = getSquareClosestToPlayer(thisPlayer, thisObject, facing, controllerSquare)
			if AdjacentFreeTileFinder.privTrySquare(controllerSquare, thisObject:getSquare():getE()) then
				frontSquare = thisObject:getSquare():getE()
			elseif AdjacentFreeTileFinder.privTrySquare(controllerSquare, thisObject:getSquare():getW()) then
				frontSquare = thisObject:getSquare():getW()
			end
		elseif (facing == "E") or (facing == "W") then
			if AdjacentFreeTileFinder.privTrySquare(controllerSquare, thisObject:getSquare():getN()) then
				frontSquare = thisObject:getSquare():getN()
			elseif AdjacentFreeTileFinder.privTrySquare(controllerSquare, thisObject:getSquare():getS()) then
				frontSquare = thisObject:getSquare():getS()
			end
		end
		]]--
	else
		ChairFromFront = true
	end

	if not frontSquare then
		return false, false
	end

	if AdjacentFreeTileFinder.privTrySquare(controllerSquare, frontSquare) then
		ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, frontSquare))
		return true, ChairFromFront
	end
	return false, false
end

SitActionMenu.onAction = function(worldobjects, player, TargetObject, ConnectedObject, NewObject, ChairOrCouch, X, Y, SeatBack, ConnectedObjectNew, SP)
	local DoSitAction = require "TimedActions/DoSitAction"
	local CanWalk, ChairFromFront = SitActionMenu.walkToFront(player, TargetObject, SP.Isfacing, ChairOrCouch)
	if CanWalk then
		--if SP.ChairFront and (string.find(ChairOrCouch, "Chair")) then X, Y = LSSitActionMenuGetXYPos(TargetObject, SP.Isfacing, "Couch"); end
		ISTimedActionQueue.add(DoSitAction:new(player, TargetObject, ConnectedObject, NewObject, ChairOrCouch, X, Y, SeatBack, ConnectedObjectNew, {SP.Isfacing,ChairFromFront}));
	end
end


