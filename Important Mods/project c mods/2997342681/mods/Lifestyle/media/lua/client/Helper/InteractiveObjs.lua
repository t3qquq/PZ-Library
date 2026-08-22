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

LSIntObjs = {}

--[[
local function getClockOverlay(facing, name)
	local t = {
		stop  = {S="",E=""},
		left  = {S="",E=""},
		right = {S="",E=""},
	}
	return t[name][facing]
end

local function getFacing(object)
	if not object then return nil; end
    local properties = object:getSprite() and object:getSprite():getProperties()
    if properties and properties:Is("Facing") then
        return properties:Val("Facing")
    end
    return nil
end

local function clockIsRested(object, facing)
	if not object:getOverlaySprite() then return false; end
	if (object:getOverlaySprite() == getClockOverlay(facing, "stop")) then return true; end
	return false
end

local function getClockLeftRight(object, facing)
	if not object:getOverlaySprite() then return "left"; end
	if getClockOverlay(facing, "left") == object:getOverlaySprite() then return "right"; end
	return "left"
end

local function checkClockSound(object, state)
	if state == "right" then
		local movementEmitter = getWorld():getFreeEmitter(object:getX(),object:getY(),object:getZ())
		movementEmitter:playSoundImpl("GFClock_TickTock", false, object)
	end
	if (getGameTime():getTimeOfDay() ~= 12) and (getGameTime():getTimeOfDay() ~= 24) and (getGameTime():getTimeOfDay() ~= 0) then
		if object:getModData().movableData['chime'] then object:getModData().movableData['chime'] = false; if isClient() then object:transmitModData(); end; end
		return
	end
	if not object:getModData().movableData['chime'] then
		object:getModData().movableData['chime'] = true
		if isClient() then object:transmitModData(); end
		local emitter = getWorld():getFreeEmitter(object:getX(),object:getY(),object:getZ())
		emitter:playSound("GFClock_Chime", object)
		addSound(object, object:getX(), object:getY(), object:getZ(), 30, 10)
	end
end

LSIntObjs.GFClock = function(player, object)
	object:getModData().movableData = object:getModData().movableData or {}
	if not object:getModData().movableData['lastWind'] then return; end
	local facing = getFacing(object)
	if not facing then return; end
	local nextState
	if object:getModData().movableData['active'] then
		if (object:getModData().movableData['lastWind']+36 < tonumber(getGameTime():getWorldAgeHours())) then
			object:getModData().movableData['active'] = false
			nextState = "stop"
		else
			nextState = getClockLeftRight(object, facing)
			checkClockSound(object, nextState)
		end
	elseif (not object:getModData().movableData['active']) and (not clockIsRested(object, facing)) then
		nextState = "stop"
	end
	if nextState then
		local overlay = getClockOverlay(facing, nextState)
		object:setOverlaySprite(overlay, isClient())
		if isClient() then object:transmitUpdatedSpriteToServer(); object:transmitModData(); end
	end
end
]]--

local function checkClockSound(object)
	local movementEmitter = getWorld():getFreeEmitter(object:getX(),object:getY(),object:getZ())
	movementEmitter:playSoundImpl("GFClock_TickTock", false, object)
	if (getGameTime():getHour() ~= 12) and (getGameTime():getHour() ~= 24) and (getGameTime():getHour() ~= 0) then
		if object:getModData().movableData['chime'] then object:getModData().movableData['chime'] = false; if isClient() then object:transmitModData(); end; end
		return
	end
	if not object:getModData().movableData['chime'] then
		object:getModData().movableData['chime'] = true
		if isClient() then object:transmitModData(); end
		local emitter = getWorld():getFreeEmitter(object:getX(),object:getY(),object:getZ())
		emitter:playSound("GFClock_Chime", object)
		addSound(object, object:getX(), object:getY(), object:getZ(), 30, 10)
	end
end

LSIntObjs.GFClock = function(player, object)
	if not LSUtil.isObjOnSqr(object) then return; end
	object:getModData().movableData = object:getModData().movableData or {}
	if (not object:getModData().movableData['lastWind']) or (not object:getModData().movableData['active']) then return; end
	if (object:getModData().movableData['lastWind']+36 < tonumber(getGameTime():getWorldAgeHours())) then
		object:getModData().movableData['active'] = false
		if isClient() then object:transmitModData(); end
	else
		checkClockSound(object)
	end
end

local function objectIsValid(obj)
	if obj and instanceof(obj, "IsoObject") and obj:getSquare() and obj:getX() and obj:getY() then return true; end
	return false
end

local function sqrHasEnergy(obj)
	if not ((SandboxVars.ElecShutModifier > -1 and
	GameTime:getInstance():getNightsSurvived() < SandboxVars.ElecShutModifier) or
	obj:getSquare():haveElectricity()) then
		return false
	end
	return true
end

local function playerIsClose(player, obj)
	if player and (player:getX() >= obj:getX() - 30 and player:getX() <= obj:getX() + 30 and
	player:getY() >= obj:getY() - 30 and player:getY() <= obj:getY() + 30) then return true; end
	return false
end

local function getCustomName(object)
	if not object then return nil; end
    local properties = object:getSprite() and object:getSprite():getProperties()
    if properties and properties:Is("CustomName") then
        return properties:Val("CustomName")
    end
    return nil
end

local function getGroupName(object)
	if not object then return nil; end
    local properties = object:getSprite() and object:getSprite():getProperties()
    if properties and properties:Is("GroupName") then
        return properties:Val("GroupName")
    end
    return nil
end

local function getInteractiveObjsCN()
	return {"Sculpture Ice","Sculpture Lamp","Hygienator"}
end

local function getInteractiveObjsGN()
	return {"GF Clock","StationWork"}
end

function LSrefreshIO(player)
	local objList = require("Properties/Objects/List")
	if (not objList) or (#objList == 0) then return; end
	for i,v in ipairs(objList) do
		if objectIsValid(v) and playerIsClose(player, v) and LSUtil.isObjOnSqr(v) then
			local groupName = getGroupName(v)
			if groupName then
				local t = getInteractiveObjsGN()
				for n=1, #t do
					if groupName == t[n] then
						local nameWS = groupName:gsub(" ", "")
						if LSIntObjs[nameWS] then LSIntObjs[nameWS](player, v); end
						break
					end
				end
			end
			local customName = getCustomName(v)
			if customName then
				local t = getInteractiveObjsCN()
				for n=1, #t do
					if customName == t[n] then
						local nameWS = customName:gsub(" ", "")
						if LSIntObjs[nameWS] then LSIntObjs[nameWS](player, v); end
						break
					end
				end
			end
		end
	end
end
