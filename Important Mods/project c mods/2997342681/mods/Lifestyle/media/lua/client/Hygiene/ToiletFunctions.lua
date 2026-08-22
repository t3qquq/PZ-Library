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

ToiletFunctions = {};

local function TFMoveToCenterSquare(player)
	local sqr = player:getSquare()
	if sqr then ISTimedActionQueue.add(ISWalkToTimedAction:new(player, sqr)); return true; end
	return false
end

local function TFFlushRandomForget(player)
	local FlushOdds = ZombRand(100) + 1
	if player:HasTrait("Sloppy") then
		return FlushOdds+75
	elseif player:HasTrait("Tidy") or player:HasTrait("CleanFreak") then
		return 0
	end

	return FlushOdds
end

local function TFCanFlush(player, object, objectWaterUsage)
	if (not player) or (not object) or
	(not TFMoveToCenterSquare(player)) or
	(not object:hasWater()) or (object:hasWater() and (object:getWaterAmount() < objectWaterUsage)) or
	(not object:hasModData()) or (not object:getModData().NeedsFlush) or
	(TFFlushRandomForget(player) >= 90)
	then return false; end

	return true
end

local function TFGetToiletSounds(objectType)
	local ToiletSounds = require("Hygiene/Tracks/ToiletSounds")
	local AvailableToiletSounds = {}
	for k,v in pairs(ToiletSounds) do
		if v.category == objectType then
			table.insert(AvailableToiletSounds, v)
		end
	end
	return AvailableToiletSounds
end

ToiletFunctions.DoActionFlush = function(player, object, objectType, objectWaterUsage, newToilet)
	if TFCanFlush(player, object, objectWaterUsage) then 
		local TS = TFGetToiletSounds(objectType)
		if TS and (#TS > 0) then
			local rN = ZombRand(#TS) + 1
			ToiletFunctions.onAction(player, object, objectType, objectWaterUsage, TS[rN].isflush, TS[rN].seatUp, TS[rN].seatDown, "IsFlush", false, newToilet);
		end
-------
	end--TFCanFlush
------
end

ToiletFunctions.walkToFront = function(thisPlayer, thisObject, newToiletObj)
	local frontSquare = nil
	local thisSquare = thisObject:getSquare()
	if not thisSquare then
		return false
	end
	local Nfacing = nil

	print("walking to front")

	if newToiletObj then
		Nfacing = newToiletObj
		print(tostring(Nfacing))
	else
		local properties = thisObject:getSprite():getProperties()
		if properties:Is("Facing") then
			Nfacing = properties:Val("Facing")
		else
			print("walk to FAILED No NFacing")
			--return false
		end
	end

	if Nfacing then
		if Nfacing == "S" then
			frontSquare = thisObject:getSquare():getS()
		elseif Nfacing == "E" then
			frontSquare = thisObject:getSquare():getE()
		elseif Nfacing == "W" then
			frontSquare = thisObject:getSquare():getW()
		elseif Nfacing == "N" then
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
	
	print("walk to FAILED")
	return false
	
end

local function TFGetObjFrontSqr(player, object, Nobject)

	if Nobject and (Nobject ~= "none") then return player:getSquare(); end
	if (not object) or (not object:getSquare()) then return false; end
	local frontSquare, Nfacing
	if object:getSprite() and object:getSprite():getProperties() and object:getSprite():getProperties():Is("Facing") then
		Nfacing = object:getSprite():getProperties():Val("Facing")
	end

	if Nfacing then
		if Nfacing == "S" then
			frontSquare = object:getSquare():getS()
		elseif Nfacing == "E" then
			frontSquare = object:getSquare():getE()
		elseif Nfacing == "W" then
			frontSquare = object:getSquare():getW()
		elseif Nfacing == "N" then
			frontSquare = object:getSquare():getN()
		end
	end
	return frontSquare
end

local function TFGetUpNoFlush(player, object, Nobject)
	--local sqr = TFGetObjFrontSqr(player, object, Nobject)
	local sqr = player:getSquare()
	--if not sqr then return; end
	--local adjacent = AdjacentFreeTileFinder.Find(sqr, player)
	if sqr then ISTimedActionQueue.add(ISWalkToTimedAction:new(player, sqr)); end
end

ToiletFunctions.onAction = function(player, Toilet, Type, WaterUsage, Flush, SeatUp, SeatDown, ActionType, PlungerItem, newToiletObj)
	local LSUseToilet = require "TimedActions/LSUseToilet"
	local LSFlushToilet = require "TimedActions/LSFlushToilet"
	local LSUnclogToilet = require "TimedActions/LSUnclogToilet"
	--if ToiletFunctions.walkToFront(player, Toilet, newToiletObj) then
	--if TFMoveToCenterSquare(player) then
		if ActionType == "IsUse" then
		
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
			end
		
			ISTimedActionQueue.add(LSUseToilet:new(player, Toilet, Type, WaterUsage, Flush, SeatUp, SeatDown, dirtSprite, dirtSprite2, dirtSprite3))
		elseif ActionType == "IsFlush" then
			if player:HasTrait("Sloppy") then
				local sloppyFlush = ZombRand(100) + 1
				if sloppyFlush >= 75 then--70
					ISTimedActionQueue.add(LSFlushToilet:new(player, Toilet, Type, WaterUsage, Flush, SeatDown))
				else
					--TFGetUpNoFlush(player, Toilet, newToiletObj)
					--player:getModData().IsSittingOnSeat = false
					--player:getModData().IsSittingOnSeatSouth = false
					--player:setSitOnGround(false)
					--player:MoveForward(0.1, player:getX(), player:getY(), 0)
				end
			else
				ISTimedActionQueue.add(LSFlushToilet:new(player, Toilet, Type, WaterUsage, Flush, SeatDown))
			end
		end
	--end
end

ToiletFunctions.DoActionDisturbed = function(player, TargetX, TargetY, object, newToilet)
 
	--print("do action disturbed")
 
    local thisPlayer = player
	local otherPlayerX = TargetX
	local otherPlayerY = TargetY
	if not thisPlayer then return; end
	if not otherPlayerX then return; end
	if not otherPlayerY then return; end
	
	local newToiletObj = newToilet
	local Toilet = object

	if not Toilet then 
		ISTimedActionQueue.add(ShooOther:new(thisPlayer, otherPlayerX, otherPlayerY))
	else

		local toiletData = Toilet:getModData()
	
		--if toiletData:hasModData() then
			--toiletData = toiletData:getModData()
		--end
		--print("about to walk to front")
------
		if ToiletFunctions.walkToFront(thisPlayer, Toilet, newToiletObj) then
			--print("do TIMED action disturbed")
			ISTimedActionQueue.add(ShooOther:new(thisPlayer, otherPlayerX, otherPlayerY))
		end
	end
end