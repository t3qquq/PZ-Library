SetNoThumpable = {}
SetNoThumpable.MODULE = "SetNoThumpable"
SetNoThumpable.COMMAND_SET_DOOR = "SetDoorThumpable"
SetNoThumpable.COMMAND_SET_BARRIER = "SetZombieBarrier"
SetNoThumpable.COMMAND_SYNC_DOOR = "SyncDoorThumpable"
SetNoThumpable.COMMAND_SYNC_BARRIER = "SyncZombieBarrier"
SetNoThumpable.BARRIER_NAME = "SetNoThumpableZombieBarrier"
SetNoThumpable.BARRIER_SPRITE = "invisible_01_0"
SetNoThumpable.BARRIER_SCHEMA_VERSION = 4
SetNoThumpable.BARRIER_HEALTH = 100000

function SetNoThumpable.log(message)
	DebugLog.log("[SetNoThumpable] " .. message)
end

function SetNoThumpable.squarePos(square)
	return square:getX() .. "," .. square:getY() .. "," .. square:getZ()
end

function SetNoThumpable.isZombieBarrier(object)
	if not instanceof(object, "IsoThumpable") then
		return false
	end
	if object:getName() == SetNoThumpable.BARRIER_NAME then
		return true
	end
	return object:getModData().SetNoThumpableZombieBlock == true
end

function SetNoThumpable.getZombieBarrier(square)
	local specials = square:getSpecialObjects()
	for i = 0, specials:size() - 1 do
		local object = specials:get(i)
		if SetNoThumpable.isZombieBarrier(object) then
			return object
		end
	end
	local objects = square:getObjects()
	for i = 0, objects:size() - 1 do
		local object = objects:get(i)
		if SetNoThumpable.isZombieBarrier(object) then
			return object
		end
	end
	return nil
end

function SetNoThumpable.getObjectSpriteName(object)
	local sprite = object:getSprite()
	if not sprite then
		return nil
	end
	return sprite:getName()
end

-- This is a non-physical controller. b41 getSpecialSolid() skips it because
-- blockAllTheSquare is false, and getSpecialWall() skips IsoThumpable stairs.
-- Server and owning-client Lua stop zombies; players and vehicles pass.
function SetNoThumpable.normalizeZombieBarrier(object)
	local changed = false
	local modData = object:getModData()
	if object:IsOpen() then
		object:ToggleDoorSilent()
		changed = true
	end
	if object:isDoor() then
		object:setIsDoor(false)
		changed = true
	end
	if not object:isThumpable() then
		object:setIsThumpable(true)
		changed = true
	end
	if object:isBlockAllTheSquare() then
		object:setBlockAllTheSquare(false)
		changed = true
	end
	if not object:isCanPassThrough() then
		object:setCanPassThrough(true)
		changed = true
	end
	if not object:isStairs() then
		object:setIsStairs(true)
		changed = true
	end
	if object:isHoppable() then
		object:setIsHoppable(false)
		changed = true
	end
	if object:getCanBarricade() then
		object:setCanBarricade(false)
		changed = true
	end
	if object:isDismantable() then
		object:setIsDismantable(false)
		changed = true
	end
	if object:getThumpDmg() ~= 1 then
		object:setThumpDmg(1)
		changed = true
	end
	if object:getMaxHealth() < SetNoThumpable.BARRIER_HEALTH then
		object:setMaxHealth(SetNoThumpable.BARRIER_HEALTH)
		changed = true
	end
	if object:getHealth() < SetNoThumpable.BARRIER_HEALTH then
		object:setHealth(SetNoThumpable.BARRIER_HEALTH)
		changed = true
	end
	if SetNoThumpable.getObjectSpriteName(object) ~= SetNoThumpable.BARRIER_SPRITE then
		object:setSprite(SetNoThumpable.BARRIER_SPRITE)
		changed = true
	end
	if modData.SetNoThumpableZombieBlock ~= true then
		modData.SetNoThumpableZombieBlock = true
		changed = true
	end
	if modData.SetNoThumpableBarrierSchema ~= SetNoThumpable.BARRIER_SCHEMA_VERSION then
		modData.SetNoThumpableBarrierSchema = SetNoThumpable.BARRIER_SCHEMA_VERSION
		changed = true
	end
	return changed
end

-- Same door check as vanilla AdminContextMenu / DebugContextMenu.
function SetNoThumpable.isDoorObject(object)
	if SetNoThumpable.isZombieBarrier(object) then
		return false
	end
	return instanceof(object, "IsoDoor") or (instanceof(object, "IsoThumpable") and object:isDoor())
end

-- Vanilla AdminContextMenu is MP admin/moderator. DebugContextMenu is debug-only.
-- Split the same way so MP -debug players do not see a menu the server will reject.
function SetNoThumpable.canUseMenu()
	if isClient() then
		return isAdmin() or getAccessLevel() == "moderator"
	end
	return isDebugEnabled()
end

function SetNoThumpable.canPlayerCommand(player)
	return player:isAccessLevel("admin") or player:isAccessLevel("moderator")
end

function SetNoThumpable.isThumpableEnabled(door)
	if instanceof(door, "IsoThumpable") then
		if door:getModData().SetNoThumpable then
			return false
		end
		return door:isThumpable()
	end
	return true
end

function SetNoThumpable.getLinkedDoors(door)
	local doubleDoors = buildUtil.getDoubleDoorObjects(door)
	if #doubleDoors > 0 then
		return doubleDoors
	end
	local garageDoors = buildUtil.getGarageDoorObjects(door)
	if #garageDoors > 0 then
		return garageDoors
	end
	local objects = {}
	table.insert(objects, door)
	return objects
end

function SetNoThumpable.markLinkedDoors(doors, seen)
	for i = 1, #doors do
		seen[doors[i]] = true
	end
end

function SetNoThumpable.isGroupThumpableEnabled(doors)
	for i = 1, #doors do
		if SetNoThumpable.isThumpableEnabled(doors[i]) then
			return true
		end
	end
	return false
end

-- Returns a reason string if any IsoDoor in the group cannot be replaced.
-- Curtains and barricades are rejected so replacement cannot drop those states.
function SetNoThumpable.getConversionBlockReason(doors)
	for i = 1, #doors do
		local door = doors[i]
		if instanceof(door, "IsoDoor") then
			local square = door:getSquare()
			if not square then
				return "square"
			end
			if door:getObjectIndex() < 0 then
				return "index"
			end
			if door:HasCurtains() then
				return "curtains"
			end
			if door:isBarricaded() then
				return "barricade"
			end
			local closedSprite = door:getSprite()
			if not closedSprite or not closedSprite:getName() then
				return "closedSprite"
			end
			local openSprite = door:getOpenSprite()
			if not openSprite or not openSprite:getName() then
				return "openSprite"
			end
		end
	end
	return nil
end

function SetNoThumpable.posKey(x, y, z)
	return math.floor(x) .. "," .. math.floor(y) .. "," .. math.floor(z)
end
