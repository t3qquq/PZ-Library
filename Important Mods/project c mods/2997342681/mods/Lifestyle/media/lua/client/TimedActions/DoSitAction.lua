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



require "TimedActions/ISBaseTimedAction"

local DoSitAction = ISBaseTimedAction:derive('DoSitAction');
local ChairDoTextHelperUnhappyness = 0
local ChairDoTextHelperBoredom = 0

local function getDefaultSittingPos(chairorcouch, facing)
	local posX, posY = 0, 0
	local DefaultSitPosition = require("Properties/DefaultSitPosition")	
	for k,v in pairs(DefaultSitPosition) do
		if facing == "N" and (string.find(chairorcouch, v.name)) then
			posX = v.xN
			posY = v.yN
			break
		elseif facing == "S" and (string.find(chairorcouch, v.name)) then
			posX = v.xS
			posY = v.yS
			break
		elseif facing == "E" and (string.find(chairorcouch, v.name)) then
			posX = v.xE
			posY = v.yE
			break
		elseif facing == "W" and (string.find(chairorcouch, v.name)) then
			posX = v.xW
			posY = v.yW
			break
		end
	end
	return posX, posY
end

local function getSittingPos(chairorcouch, facing)
	local posX, posY = getDefaultSittingPos(chairorcouch, facing)
	if (chairorcouch == "Piano Stool") then return posX, posY; end
	local SitPosition = require("Properties/SitPosition")	
	for k,v in pairs(SitPosition) do
		if facing == "N" and v.name == chairorcouch then
			posX = posX+v.xN
			posY = posY+v.yN
			break
		elseif facing == "S" and v.name == chairorcouch then
			posX = posX+v.xS
			posY = posY+v.yS
			break
		elseif facing == "E" and v.name == chairorcouch then
			posX = posX+v.xE
			posY = posY+v.yE
			break
		elseif facing == "W" and v.name == chairorcouch then
			posX = posX+v.xW
			posY = posY+v.yW
			break
		end
	end
	return posX, posY
end

local function getSittingPosChairSide(chairorcouch, facing)
	local posX, posY = 0, 0
	local SitPosition = require("Properties/SitPosition")	
	for k,v in pairs(SitPosition) do
		if facing == "S" and v.name == chairorcouch then
			if v.xSideS and v.ySideS then
				posX = v.xSideS
				posY = v.ySideS
			end
		elseif facing == "E" and v.name == chairorcouch then
			if v.xSideE and v.ySideE then
				posX = v.xSideE
				posY = v.ySideE
			end
		end
	end
	return posX, posY
end

local function getXYisChairSide(object, facing)
	local getTX, getTY = object:getX(), object:getY()
	if facing == "E" then
		getTX, getTY = getTX+0.7, getTY+0.5
	elseif facing == "S" then
		getTX, getTY = getTX+0.5, getTY+0.7
	end
	return getTX, getTY
end

local function doSittingPos(object, chairorcouch, thisPlayer, facing, isChairSide)
	--if chairorcouch == "Piano Stool" then return; end
	local posX, posY = 0, 0
	local objX, objY, NSvar = object:getSquare():getX(), object:getSquare():getY(), "N"
	if isChairSide and (chairorcouch ~= "Piano Stool") and ((facing == "S") or (facing == "E")) then 
		objX, objY = getXYisChairSide(object, facing)
		posX, posY = getSittingPosChairSide(chairorcouch, facing)
		NSvar = "S"
	else
		posX, posY = getSittingPos(chairorcouch, facing)
	end
	if (not posX) or (not posY) then thisPlayer:Say("position not found"); return; end
	thisPlayer:setY(objY + posY)
	thisPlayer:setX(objX + posX)
	thisPlayer:setLy(objY + posY)
	thisPlayer:setLx(objX + posX)
	local teleportX, teleportY = (objX + posX), (objY + posY)
	ScanForPlayers("TeleportSittingLocation", {teleportX, teleportY, NSvar})
end

function DoSitAction:isValid()
   return true;
end

function DoSitAction:waitToStart()
	self.character:faceLocation(self.x, self.y)
	self.character:faceLocationF(self.x, self.y)
	--self.character:faceThisObject(self.originalObject)
	--self.character:faceThisObject(self.originalObject)
	return self.character:shouldBeTurning();
end

function DoSitAction:update()

	local characterData = self.character:getModData()

	if self.character:isSneaking() then
		self:forceStop()
	end

	if self.animToplay ~= 0 then
		self.animDelayStart = self.animDelayStart + getGameTime():getGameWorldSecondsSinceLastUpdate()
	end

	if self.animDelayStart >= self.animDelayEnd then
		self.animDelayStart = 0
		
		if self.chairOrCouch == "Piano Stool" then

			if self.facing == "N" then
				self.facing = "W"
			elseif self.facing == "W" then
				self.facing = "N"
			end

		end
		
		
		--self:setActionAnim(self.actionType)
		--self.character:setVariable(self.actionType, "Idle")
		--self.character:getOrCreateVariable(self.actionType)
		self:forceComplete()
		--self.animToplay = 0
	--if self.facing == "S" then
	--	self.character:setY(self.objectY + 0.5)
	--	self.character:setX(self.objectX + 0.35)
	--	self.character:setLy(self.objectY + 0.5)
	--	self.character:setLx(self.objectX + 0.35)
	--elseif self.facing == "E" then
	--	self.character:setY(self.objectY + 0.35)
	--	self.character:setX(self.objectX + 0.5)
	--	self.character:setLy(self.objectY + 0.35)
	--	self.character:setLx(self.objectX + 0.5)
	--elseif self.facing == "N" then
	--	self.character:setY(self.objectY - 0.01)
	--	self.character:setX(self.objectX + 0.5)
	--	self.character:setLy(self.objectY - 0.01)
	--	self.character:setLx(self.objectX + 0.5)
	--elseif self.facing == "W" then
	--	self.character:setY(self.objectY + 0.5)
	--	self.character:setX(self.objectX - 0.01)
	--	self.character:setLy(self.objectY + 0.5)
	--	self.character:setLx(self.objectX - 0.01)
	--end
	--self.character:faceLocation(self.x, self.y)
	--self.character:faceLocationF(self.x, self.y)
	end

end

function DoSitAction:start()

	self.character:setVariable("ExerciseStarted", false)
	self.character:setVariable("ExerciseEnded", true)

	self:setOverrideHandModels(nil, nil)

	self.objectX = self.originalObject:getX()
	self.objectY = self.originalObject:getY()
	self.objectZ = self.originalObject:getZ()

	local properties = self.originalObject:getSprite():getProperties()
	--self.facing = properties:Val("Facing")

	self.characterX = self.character:getX()
	self.characterY = self.character:getY()

	self.originalSprite = self.originalObject:getSprite()

	if self.connectedObject ~= "none" then
		self.connectedOriginalSprite = self.connectedObject:getSprite()
	end

	--if self.facing == "S" then
		--self.animToplay = "Bob_IsSittingStartN"
	--elseif self.facing == "E" then
	--	self.animToplay = "Bob_IsSittingStartN"
	--elseif self.facing == "W" then
	--	self.animToplay = "Bob_IsSittingStartN"
	--elseif self.facing == "N" then
	--	self.animToplay = "Bob_IsSittingStartN"
	--end


	--self.character:Say(tostring(self.animToplay))

	if self.connectedObject ~= "none" then
	
		self.objectSquare = self.connectedObject:getCell():getGridSquare((self.connectedObject:getX()-1), (self.connectedObject:getY()-1), self.connectedObject:getZ())
	else
		self.objectSquare = self.originalObject:getCell():getGridSquare((self.objectX-1), (self.objectY-1), self.objectZ)
	end

	if self.newSprite ~= "none" then
		self.originalObject:setSprite(self.newSprite)---newSprite
	end

	if self.seatBack ~= "none" then
		self.backObject = IsoObject.new(self.objectSquare, self.seatBack)
		self.backObject:setName("SeatBack")
		self.objectSquare:AddTileObject(self.backObject)
	end

	if self.connectedObjectNewSprite ~= "none" then
		self.connectedObject:setSprite(self.connectedObjectNewSprite)---newSprite
	end

	if (self.chairOrCouch == "Piano Stool") or ((string.find(self.chairOrCouch, "Chair")) and (not self.chairFront)) then
		--print("DoSitAction: start - IS CHAIR OR STOOL")
		self.animDelayEnd = 5/GTLSCheck--30
		self.animToplay = "Bob_PushToSideLow"
		self.character:getEmitter():playSound("Chair_Move")
	end

	self:setActionAnim(self.animToplay)

	--self.character:SetVariable("LootPosition", "Mid")

	--self:setOverrideHandModels(nil, nil)
	self.action:setUseProgressBar(false)
	if (self.chairOrCouch ~= "Piano Stool") and (((string.find(self.chairOrCouch, "Chair")) and (self.chairFront)) or (not (string.find(self.chairOrCouch, "Chair")))) then
		doSittingPos(self.originalObject, self.chairOrCouch, self.character, self.facing, false)
		self.sitPos = true
	end
end

function DoSitAction:stop()

	self.character:setY(self.characterY)
	self.character:setX(self.characterX)
	-- this guarantees player position is sinc in multiplayer
	self.character:setLy(self.characterY)
	self.character:setLx(self.characterX)

	if self.newSprite ~= "none" then
		self.originalObject:setSprite(self.originalSprite)
	end

	if self.seatBack ~= "none" then
		self.objectSquare:RemoveTileObject(self.backObject)
	end

	if self.connectedObjectNewSprite ~= "none" then
		self.connectedObject:setSprite(self.connectedOriginalSprite)
	end

	if (self.chairOrCouch == "Piano Stool") or (string.find(self.chairOrCouch, "Chair")) then
		self.character:getEmitter():playSound("Chair_Move")
	end

	local bodyDamage = self.character:getBodyDamage()
	-- adjust stats to 0 if levels went below 0
	if (bodyDamage:getBoredomLevel() < 0) then
		bodyDamage:setBoredomLevel(0)
	end
	if (bodyDamage:getUnhappynessLevel() < 0) then
		bodyDamage:setUnhappynessLevel(0)
	end
	if (self.character:getStats():getStress() < 0) then
		self.character:getStats():setStress(0)
	end

	-- adjust stats if levels went above
	if (bodyDamage:getBoredomLevel() > 100) then
		bodyDamage:setBoredomLevel(100)
	end
	if (bodyDamage:getUnhappynessLevel() > 100) then
		bodyDamage:setUnhappynessLevel(100)
	end
	if (self.character:getStats():getStress() > 1) then
		self.character:getStats():setStress(1)
	end
	
	ISBaseTimedAction.stop(self);
end

function DoSitAction:perform()

	if not self.sitPos then doSittingPos(self.originalObject, self.chairOrCouch, self.character, self.facing, true); self.sitPos = true; end
	if (string.find(self.chairOrCouch, "Chair")) and ((self.facing == "S") or (self.facing == "E")) then
		self.character:setVariable("SittingToggleStart", "S")
	else
		self.character:setVariable("SittingToggleStart", "N")
	end
	self.character:reportEvent("EventSitOnGround")
	--if self.facing == "N" then
	--self.character:setVariable("SittingToggleStart", "N")

	--elseif self.facing == "S" then
	--self.character:setVariable("SittingToggleStart", "N")
	--elseif self.facing == "W" then
	--self.character:setVariable("SittingToggleStart", "N")
	--elseif self.facing == "E" then
	--self.character:setVariable("SittingToggleStart", "N")
	--end
	
	local characterData = self.character:getModData()

	local ComfortProperties = require("Properties/ComfortProperties")
	local ComfortVal
	--local isChair = string.find(self.chairOrCouch, "Chair")
	
	for k,v in pairs(ComfortProperties) do
		if string.find(self.chairOrCouch, v.name) then
			ComfortVal = v.comfort
			break
		end
	end
	if not ComfortVal then ComfortVal = 0.65; end
	--if isChair then ComfortVal = ComfortVal*0.7; end
	characterData.ComfortVal = ComfortVal

		local newsoundrandomiser = ZombRand(1, 100)
		local sitSound = "Sit_Comfy1"
		if newsoundrandomiser >=66 then
			sitSound = "Sit_Comfy2"
		elseif newsoundrandomiser >=33 then
			sitSound = "Sit_Comfy3"
		else
			sitSound = "Sit_Comfy4"
		end
		self.character:getEmitter():playSound(sitSound);
	--end

	characterData.IsSittingOnSeat = true
	if self.chairOrCouch == "Piano Stool" then
		characterData.IsSittingOnPianoStool = true
		LSUtil.pianoPos = false
	end
	LSIsSitAction(self.connectedObjectNewSprite, self.connectedObject, self.originalObject, self.originalSprite, self.seatBack, self.objectSquare, self.backObject, self.connectedOriginalSprite, self.chairOrCouch, self.facing, self.newSprite)
    ISBaseTimedAction.perform(self);
end

function DoSitAction:new(character, TargetObject, ConnectedObject, NewObject, ChairOrCouch, X, Y, SeatBack, ConnectedObjectNew, args)
    local o = {}
    setmetatable(o, self)
    self.__index = self
    o.character = character;
	o.originalObject = TargetObject;
    o.stopOnWalk = true;
    o.stopOnRun = true;
    o.stopOnAim = true;
	o.ignoreHandsWounds = true;
	o.maxTime = 3000
	o.connectedObjectNewSprite = ConnectedObjectNew
	o.newSprite = NewObject
	o.chairOrCouch = ChairOrCouch
	o.x = X
	o.y = Y
	o.seatBack = SeatBack
	o.connectedObject = ConnectedObject
	o.connectedOriginalSprite = 0
	o.originalSprite = 0
	o.actionType = 0
	o.facing = args[1]
	o.chairFront = args[2]
	o.characterX = 0
	o.characterY = 0
	o.objectX = 0
	o.objectY = 0
	o.objectZ = 0
	o.objectSquare = 0
	o.backObject = 0
	o.pastAnim = 0
	o.animToplay = "Bob_IsSitStart"
	o.animDelayStart = 0
	o.animDelayEnd = 15/GTLSCheck--85
	o.sitPos = false
    return o;
end

return DoSitAction;