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

require "Helper/CheckPlayerHelper"

local function getSittingPos(chairorcouch, facing)
	local posX, posY
	local SitPosition = require("Properties/SitPosition")	
	for k,v in pairs(SitPosition) do
		if facing == "N" and v.name == chairorcouch then
				posX = v.xN
				posY = v.yN
		elseif facing == "S" and v.name == chairorcouch then
				posX = v.xS
				posY = v.yS
		elseif facing == "E" and v.name == chairorcouch then
				posX = v.xE
				posY = v.yE
		elseif facing == "W" and v.name == chairorcouch then
				posX = v.xW
				posY = v.yW
		end
	end
	return posX, posY
end

local function doSittingPos(object, chairorcouch, thisPlayer, facing, isChairSide)
	if chairorcouch == "Piano Stool" then return; end
	local posX, posY = getSittingPos(chairorcouch, facing)
	if (not posX) or (not posY) then thisPlayer:Say("position not found"); return; end
	local objX, objY, NSvar = object:getSquare():getX(), object:getSquare():getY(), "N"
	if isChairSide and ((facing == "S") or (facing == "E")) then objX, objY, posY, posX, NSvar = object:getSquare():getX(), object:getSquare():getY(), 0, 0, "S"; end
	thisPlayer:setY(objY + posY)
	thisPlayer:setX(objX + posX)
	thisPlayer:setLy(objY + posY)
	thisPlayer:setLx(objX + posX)
	local teleportX, teleportY = (objX + posX), (objY + posY)
	ScanForPlayers("TeleportSittingLocation", {teleportX, teleportY, NSvar})
end

function LSIsSitActionOnMainMenu()
	local thisPlayer = getPlayer()
	local playerData = thisPlayer:getModData()
	--print("PLAYER ENTERED MENU ENDING SEAT ACTION")
	if thisPlayer and playerData then
		if not playerData.IsSittingOnSeat then playerData.IsSittingOnSeat = false; end
		if playerData.IsSittingOnSeat then
			playerData.IsSittingOnSeat = false
		end
	end
end

function LSIsSitActionOnMove()
	local thisPlayer = getPlayer()
	local playerData = thisPlayer:getModData()
	--print("PLAYER MOVED ENDING SEAT ACTION")
	if thisPlayer and playerData then
		if not playerData.IsSittingOnSeat then playerData.IsSittingOnSeat = false; end
		if playerData.IsSittingOnSeat then
			playerData.IsSittingOnSeat = false
		end
	end
end

local function sendSitToggleCommand(animVar1, animVar2, animVar3)
	if not isClient() then return; end
	ScanForPlayers("ChangeAnimVar", {"SittingToggleStart", animVar1, "SittingToggleLoop", animVar2, "IsSittingInChair", animVar3})
end

function LSIsSitAction(newspriteC, objectC, object, sprite, seatback, sqr, backobj, spriteC, chairorcouch, facing, newsprite)
	--print("LSIsSitAction FUNCTION CALLED")
	local thisPlayer = getPlayer()

	if thisPlayer ~= nil then
		local playerData = thisPlayer:getModData()
		--local bodyDamage = thisPlayer:getBodyDamage()
		--local stats = thisPlayer:getStats()
		--local currentBoredom = bodyDamage:getBoredomLevel()
		--local currentUnhappiness = bodyDamage:getUnhappynessLevel()
		--local currentStress = stats:getStress();
		
		--local objectC = objectC
		--local spriteC = spriteC
		--local object = object
		--local sprite = sprite
		--local seatback = seatback
		--local sqr = sqr
		--local backobj = backobj
		
		--local ogX = thisPlayer:getX()
		--local ogY = thisPlayer:getY()
		
		--local newspriteC = newspriteC
		local quality
		--local chairorcouch = chairorcouch
		--local facing = facing
		local facingX
		local facingY
		--local newsprite = newsprite
		local ISTimedActionQueue = require "TimedActions/ISTimedActionQueue"
		--local spriteName = object:getSprite():getName() or false
		local LSIsSitActionHell
		local posX
		local posY
		local objX = object:getSquare():getX()
		local objY = object:getSquare():getY()
		--playerData.IsSittingOnPianoStool = false
		local isRested

		if newspriteC ~= "none" then
			objectC:transmitUpdatedSprite()
		end

		if newsprite ~= "none" then
			object:transmitUpdatedSprite()
		end

		if seatback ~= "none" then
			sqr:transmitModdata()
		end

		if facing == "N" then
			facingX = objX
			facingY = objY - 10
		elseif facing == "S" then
			facingX = objX
			facingY = object:getSquare():getY() + 10
		elseif facing == "W" then
			facingX = objX - 10
			facingY = object:getSquare():getY()
		elseif facing == "E" then
			facingX = objX + 10
			facingY = objY
		end
		
		local animVar1, animVar2 = "N", "IsSitting"
		if (string.find(chairorcouch, "Chair")) and ((facing == "S") or (facing == "E")) then
			animVar1, animVar2 = "S", "IsSittingS"
			playerData.IsSittingOnSeatSouth = true
		end
		
		thisPlayer:setVariable("SittingToggleLoop", animVar1)
		thisPlayer:setVariable("IsSittingInChair", animVar2)
		sendSitToggleCommand(thisPlayer:getVariableString("SittingToggleStart"), animVar1, animVar2)
		--if isClient() then
			--sendClientCommand("LS", "ChangeAnimVarMulti", {thisPlayer:getDisplayName(), "SittingToggleLoop", "N", "IsSittingInChair", "IsSitting"})
		--end
		
		--thisPlayer:PlayAnim(actiontype)
		--[[
		if (string.find(chairorcouch, "Chair")) and ((facing == "S") or (facing == "E")) then
			doSittingPos(object, chairorcouch, thisPlayer, facing, true)
		else
			doSittingPos(object, chairorcouch, thisPlayer, facing, false)
		end
		]]--
		--[[
		local SitPosition = require("Properties/SitPosition")
		
		for k,v in pairs(SitPosition) do
			if facing == "N" and v.name == chairorcouch then
					posX = v.xN
					posY = v.yN
			elseif facing == "S" and v.name == chairorcouch then
					posX = v.xS
					posY = v.yS
			elseif facing == "E" and v.name == chairorcouch then
					posX = v.xE
					posY = v.yE
			elseif facing == "W" and v.name == chairorcouch then
					posX = v.xW
					posY = v.yW
			end
		end
		
		if not posX or not posY then thisPlayer:Say("position not found"); return; end
		
		thisPlayer:setY(objY + posY)
		thisPlayer:setX(objX + posX)
		thisPlayer:setLy(objY + posY)
		thisPlayer:setLx(objX + posX)
		local teleportX = (objX + posX)
		local teleportY = (objY + posY)
		ScanForPlayers("TeleportSittingLocation", teleportX, teleportY)
		--sendClientCommand("LS", "TeleportSittingLocation", {thisPlayer:getDisplayName(), teleportX, teleportY})
		]]--
		LSIsSitActionHell = function()
				
			if playerData.IsSittingOnSeat ~= nil and playerData.IsSittingOnSeat == true then

				thisPlayer:setBlockMovement(true)
				thisPlayer:nullifyAiming()
				if (not thisPlayer:hasTimedActions()) and (not isRested) then
					if thisPlayer:getStats():getEndurance() < 1 then
						thisPlayer:updateEnduranceWhileSitting()
					else
						HaloTextHelper.addTextWithArrow(thisPlayer, getText("IGUI_HaloNote_WellRested"), true, 80, 200, 0)
						isRested = true
					end
				end
				if thisPlayer:pressedMovement(true) then
					playerData.IsSittingOnSeat = false
					playerData.IsSittingOnSeatSouth = false
				end
				
			--	if thisPlayer:getVariableString("SittingToggleLoop") ~= facing then
				
				--	if facing == "N" then
				--		thisPlayer:setVariable("SittingToggleLoop", "N")
				--	elseif facing == "S" then
				--		thisPlayer:setVariable("SittingToggleLoop", "N")
				--	elseif facing == "W" then
				--		thisPlayer:setVariable("SittingToggleLoop", "N")
				--	elseif facing == "E" then
				--		thisPlayer:setVariable("SittingToggleLoop", "N")
				--	end
				
				--end
				
				--if spriteName and playerData.IsSittingOnPianoStool == false and (spriteName == "recreational_01_10" or spriteName == "recreational_01_15") then
					--playerData.IsSittingOnPianoStool = true
				--end
				
			--	if not thisPlayer:isFacingLocation(facingX, facingY) then
			--		thisPlayer:faceLocation(facingX, facingY)
			--	end
				
				if thisPlayer:shouldBeTurning() or thisPlayer:shouldBeTurning90() or thisPlayer:shouldBeTurningAround() then
					--ISTimedActionQueue.getTimedActionQueue(thisPlayer):resetQueue()
					thisPlayer:StopAllActionQueue()
					thisPlayer:faceLocation(facingX, facingY)
				end
				
			else
				playerData.IsSittingOnSeat = false
				playerData.IsSittingOnSeatSouth = false
				
				thisPlayer:setBlockMovement(false)
				--thisPlayer:setVariable("SittingToggleLoop", "GetUp")
				thisPlayer:clearVariable("SittingToggleStart")
				sendSitToggleCommand(false, false, false)
				--if isClient() then
					--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "SittingToggleStart", false})
					--sendClientCommand("LS", "ChangeAnimVarMulti", {thisPlayer:getDisplayName(), "SittingToggleLoop", false, "IsSittingInChair", false})
				--end
				--thisPlayer:clearVariable("SittingToggleLoop")
				if (chairorcouch == "Piano Stool") or (string.find(chairorcouch, "Chair")) then
				--if playerData.IsSittingOnPianoStool == true then
					local getTX, getTY = object:getX(), object:getY()
					if facing == "N" then
						getTX, getTY = getTX+0.5, getTY
					elseif facing == "E" then
						getTX, getTY = getTX+0.6, getTY+0.5
					elseif facing == "S" then
						getTX, getTY = getTX+0.5, getTY+0.6
					elseif facing == "W" then
						getTX, getTY = getTX, getTY+0.5
					end
					thisPlayer:setY(getTY)
					thisPlayer:setX(getTX)
					thisPlayer:setLy(getTY)
					thisPlayer:setLx(getTX)
				--elseif playerData.IsSittingOnChair == true then--for chairs without collision like most dining chairs, we don't need to set the player out of it and facing doesn't matter
					--thisPlayer:setY(objY)
					--thisPlayer:setX(objX)
					--thisPlayer:setLy(objY)
					--thisPlayer:setLx(objX)
				else
					--if facing == "N" then
					--	thisPlayer:setY(objY-0.5)
					--	thisPlayer:setX(objX)
					--	-- this guarantees player position is sinc in multiplayer
					--	thisPlayer:setLy(objY-0.5)
					--	thisPlayer:setLx(objX)
					--elseif facing == "S" then
					--	thisPlayer:setY(objY+1.1)
					--	thisPlayer:setX(objX+0.5)
					--	thisPlayer:setLy(objY+1.1)
					--	thisPlayer:setLx(objX+0.5)
					--elseif facing == "E" then
					--	thisPlayer:setY(objY+0.5)
					--	thisPlayer:setX(objX+1.1)
					--	thisPlayer:setLy(objY+0.5)
					--	thisPlayer:setLx(objX+1.1)
					--elseif facing == "W" then
					--	thisPlayer:setY(objY)
					--	thisPlayer:setX(objX-0.5)
					--	thisPlayer:setLy(objY)
					--	thisPlayer:setLx(objX-0.5)
					--end
				
				end
				--playerData.IsSittingOnChair = false
				playerData.IsSittingOnPianoStool = false

				if newsprite ~= "none" then
				object:setSprite(sprite)
				object:transmitUpdatedSprite()
				end
				if seatback ~= "none" then
					sqr:RemoveTileObject(backobj)
					sqr:transmitModdata()
					--thisPlayer:Say("Object removed")--test!! remove
				end
				if newspriteC ~= "none" then
				objectC:setSprite(spriteC)
				objectC:transmitUpdatedSprite()
				end
				
				if chairorcouch == "Piano Stool" then
					thisPlayer:getEmitter():playSound("Chair_Move");
				end
				
				Events.OnMainMenuEnter.Remove(LSIsSitActionOnMainMenu)
				Events.OnPlayerMove.Remove(LSIsSitActionOnMove)
				Events.OnTick.Remove(LSIsSitActionHell)
			end
		end
		Events.OnMainMenuEnter.Add(LSIsSitActionOnMainMenu)
		Events.OnPlayerMove.Add(LSIsSitActionOnMove)
		Events.OnTick.Add(LSIsSitActionHell)
		
	end
end