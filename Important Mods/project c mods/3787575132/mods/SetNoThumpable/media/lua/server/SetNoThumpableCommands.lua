if isClient() then return end

SetNoThumpableCommands = {}

local function copyModData(src)
	local dest = {}
	for k, v in pairs(src) do
		dest[k] = v
	end
	return dest
end

local function snapshotIsoDoor(door)
	local square = door:getSquare()
	return {
		door = door,
		square = square,
		cell = square:getCell(),
		index = door:getObjectIndex(),
		north = door:getNorth(),
		wasOpen = door:IsOpen(),
		closedSprite = door:getSprite():getName(),
		openSprite = door:getOpenSprite():getName(),
		keyId = door:checkKeyId(),
		locked = door:isLocked(),
		lockedByKey = door:isLockedByKey(),
		health = door:getHealth(),
		maxHealth = door:getMaxHealth(),
		name = door:getName(),
		thumpSound = door:getThumpSound(),
		canBarricade = door:isBarricadeAllowed(),
		hoppable = door:isHoppable(),
		modData = copyModData(door:getModData()),
	}
end

-- B41.78 IsoDoor has no setIsThumpable. Convert to IsoThumpable so zombies
-- honor isThumpable. IsoDoor.toggleDoubleDoor already supports IsoThumpable.
-- Follow vanilla MOTrap replacement: remove, AddSpecialObject(obj, index),
-- then transmitCompleteItemToClients only for the NEW object.
local function convertIsoDoorFromState(state)
	local door = state.door
	if state.wasOpen then
		door:ToggleDoorSilent()
	end
	state.square:transmitRemoveItemFromSquare(door)

	state.modData.SetNoThumpable = true
	local thumpableDoor = IsoThumpable.new(state.cell, state.square, state.closedSprite, state.openSprite, state.north, state.modData)
	thumpableDoor:setIsDoor(true)
	thumpableDoor:setIsThumpable(false)
	thumpableDoor:setCanBarricade(state.canBarricade)
	thumpableDoor:setIsHoppable(state.hoppable)
	thumpableDoor:setName(state.name)
	thumpableDoor:setBreakSound("BreakDoor")
	thumpableDoor:setThumpSound(state.thumpSound)
	thumpableDoor:setMaxHealth(state.maxHealth)
	thumpableDoor:setHealth(state.health)
	thumpableDoor:setKeyId(state.keyId)
	thumpableDoor:setIsLocked(state.locked)
	thumpableDoor:setLockedByKey(state.lockedByKey)
	state.square:AddSpecialObject(thumpableDoor, state.index)
	if state.wasOpen then
		thumpableDoor:ToggleDoorSilent()
	end
	if isServer() then
		thumpableDoor:transmitCompleteItemToClients()
	end
	SetNoThumpable.log("converted IsoDoor to IsoThumpable thumpable=false at " .. SetNoThumpable.squarePos(state.square) .. " index=" .. tostring(state.index))
end

-- Do not call transmitCompleteItemToClients on an existing object: AddItemToMap
-- inserts instead of replacing. syncIsoThumpable does not carry isThumpable.
local function syncExistingThumpable(door, thumpable)
	if not isServer() then
		return
	end
	local args = {}
	args.x = door:getX()
	args.y = door:getY()
	args.z = door:getZ()
	args.index = door:getObjectIndex()
	args.thumpable = thumpable
	sendServerCommand(SetNoThumpable.MODULE, SetNoThumpable.COMMAND_SYNC_DOOR, args)
end

local function applyExistingThumpable(door, thumpable)
	door:setIsThumpable(thumpable)
	if thumpable then
		door:getModData().SetNoThumpable = nil
	else
		door:getModData().SetNoThumpable = true
	end
	door:transmitModData()
	syncExistingThumpable(door, thumpable)
	SetNoThumpable.log("setIsThumpable=" .. tostring(thumpable) .. " at " .. door:getX() .. "," .. door:getY() .. "," .. door:getZ())
end

local function broadcastBarrierState(square, blocked)
	if not isServer() then
		return
	end
	local args = {}
	args.x = square:getX()
	args.y = square:getY()
	args.z = square:getZ()
	args.blocked = blocked
	sendServerCommand(SetNoThumpable.MODULE, SetNoThumpable.COMMAND_SYNC_BARRIER, args)
end

-- Existing objects cannot use transmitCompleteItemToClients: AddItemToMap inserts
-- a duplicate. Sprite uses vanilla trap/plant sendObjectChange; Java fields use
-- SyncZombieBarrier. Schema rides transmitModData.
local function syncExistingBarrier(barrier)
	if not isServer() then
		return
	end
	barrier:sendObjectChange("sprite")
	barrier:transmitModData()
	broadcastBarrierState(barrier:getSquare(), true)
end

local function migrateExistingBarrier(square, barrier)
	local changed = SetNoThumpable.normalizeZombieBarrier(barrier)
	SetNoThumpableZombieGuard.register(barrier)
	if not changed then
		return false
	end
	square:RecalcAllWithNeighbours(true)
	syncExistingBarrier(barrier)
	SetNoThumpable.log("migrated zombie barrier at " .. SetNoThumpable.squarePos(square))
	return true
end

function SetNoThumpableCommands.SetZombieBarrier(player, args)
	if isServer() then
		if not SetNoThumpable.canPlayerCommand(player) then
			SetNoThumpable.log("rejected SetZombieBarrier from " .. player:getUsername() .. " access=" .. player:getAccessLevel())
			return false
		end
	end

	local square = getCell():getGridSquare(args.x, args.y, args.z)
	if not square then
		SetNoThumpable.log("no square for SetZombieBarrier at " .. args.x .. "," .. args.y .. "," .. args.z)
		return false
	end

	local barrier = SetNoThumpable.getZombieBarrier(square)
	if args.blocked then
		if barrier then
			if not migrateExistingBarrier(square, barrier) then
				SetNoThumpable.log("zombie barrier already present at " .. args.x .. "," .. args.y .. "," .. args.z)
				broadcastBarrierState(square, true)
			end
			return true
		end
		local info = {}
		barrier = IsoThumpable.new(square:getCell(), square, SetNoThumpable.BARRIER_SPRITE, false, info)
		barrier:setName(SetNoThumpable.BARRIER_NAME)
		barrier:setBreakSound("BreakObject")
		barrier:setMaxHealth(SetNoThumpable.BARRIER_HEALTH)
		barrier:setHealth(SetNoThumpable.BARRIER_HEALTH)
		SetNoThumpable.normalizeZombieBarrier(barrier)
		square:AddSpecialObject(barrier)
		SetNoThumpableZombieGuard.register(barrier)
		if isServer() then
			barrier:transmitCompleteItemToClients()
		end
		square:RecalcAllWithNeighbours(true)
		broadcastBarrierState(square, true)
		SetNoThumpable.log("added zombie barrier at " .. args.x .. "," .. args.y .. "," .. args.z)
		return true
	end

	if not barrier then
		SetNoThumpableZombieGuard.unregisterAt(args.x, args.y, args.z, nil)
		SetNoThumpable.log("no zombie barrier to remove at " .. args.x .. "," .. args.y .. "," .. args.z)
		broadcastBarrierState(square, false)
		return false
	end
	SetNoThumpableZombieGuard.unregister(barrier)
	square:transmitRemoveItemFromSquare(barrier)
	square:RecalcAllWithNeighbours(true)
	broadcastBarrierState(square, false)
	SetNoThumpable.log("removed zombie barrier at " .. args.x .. "," .. args.y .. "," .. args.z)
	return false
end

function SetNoThumpableCommands.SetDoorThumpable(player, args)
	if isServer() then
		if not SetNoThumpable.canPlayerCommand(player) then
			SetNoThumpable.log("rejected SetDoorThumpable from " .. player:getUsername() .. " access=" .. player:getAccessLevel())
			return
		end
	end

	local square = getCell():getGridSquare(args.x, args.y, args.z)
	if not square then
		SetNoThumpable.log("no square for SetDoorThumpable at " .. args.x .. "," .. args.y .. "," .. args.z)
		return
	end
	if (not args.index) or args.index < 0 or args.index >= square:getObjects():size() then
		SetNoThumpable.log("invalid index for SetDoorThumpable at " .. args.x .. "," .. args.y .. "," .. args.z .. " index=" .. tostring(args.index))
		return
	end
	local object = square:getObjects():get(args.index)
	if not SetNoThumpable.isDoorObject(object) then
		SetNoThumpable.log("object at " .. args.x .. "," .. args.y .. "," .. args.z .. " index=" .. tostring(args.index) .. " is not a door: " .. tostring(object))
		return
	end

	local linkedDoors = SetNoThumpable.getLinkedDoors(object)
	if not args.thumpable then
		local reason = SetNoThumpable.getConversionBlockReason(linkedDoors)
		if reason then
			SetNoThumpable.log("rejected SetDoorThumpable convert reason=" .. reason .. " at " .. args.x .. "," .. args.y .. "," .. args.z)
			return
		end
	end

	SetNoThumpable.log("applying SetDoorThumpable thumpable=" .. tostring(args.thumpable) .. " doors=" .. tostring(#linkedDoors) .. " at " .. args.x .. "," .. args.y .. "," .. args.z)
	local affectedSquares = {}
	local function noteSquare(doorSquare)
		if doorSquare then
			affectedSquares[doorSquare] = doorSquare
		end
	end

	if not args.thumpable then
		local snapshots = {}
		for i = 1, #linkedDoors do
			local door = linkedDoors[i]
			if instanceof(door, "IsoDoor") then
				table.insert(snapshots, snapshotIsoDoor(door))
			end
		end
		for i = 1, #snapshots do
			noteSquare(snapshots[i].square)
			convertIsoDoorFromState(snapshots[i])
		end
	end

	for i = 1, #linkedDoors do
		local door = linkedDoors[i]
		if instanceof(door, "IsoThumpable") then
			noteSquare(door:getSquare())
			applyExistingThumpable(door, args.thumpable)
		elseif instanceof(door, "IsoDoor") then
			if args.thumpable then
				SetNoThumpable.log("skip enable on IsoDoor at " .. SetNoThumpable.squarePos(door:getSquare()))
			end
		else
			SetNoThumpable.log("unsupported door class " .. tostring(door))
		end
	end

	for _, affected in pairs(affectedSquares) do
		affected:RecalcAllWithNeighbours(true)
	end
	SetNoThumpable.log("finished SetDoorThumpable at " .. args.x .. "," .. args.y .. "," .. args.z)
end

local function onClientCommand(module, command, player, args)
	if module ~= SetNoThumpable.MODULE then
		return
	end
	if command == SetNoThumpable.COMMAND_SET_DOOR then
		SetNoThumpable.log("SetDoorThumpable from " .. player:getUsername() .. " thumpable=" .. tostring(args.thumpable) .. " x=" .. args.x .. " y=" .. args.y .. " z=" .. args.z .. " index=" .. args.index)
		SetNoThumpableCommands.SetDoorThumpable(player, args)
		return
	end
	if command == SetNoThumpable.COMMAND_SET_BARRIER then
		SetNoThumpable.log("SetZombieBarrier from " .. player:getUsername() .. " blocked=" .. tostring(args.blocked) .. " x=" .. args.x .. " y=" .. args.y .. " z=" .. args.z)
		SetNoThumpableCommands.SetZombieBarrier(player, args)
		return
	end
	SetNoThumpable.log("unknown command " .. tostring(command) .. " from " .. player:getUsername())
end

local function onLoadGridsquare(square)
	local barrier = SetNoThumpable.getZombieBarrier(square)
	if not barrier then
		return
	end
	migrateExistingBarrier(square, barrier)
end

Events.OnClientCommand.Add(onClientCommand)
Events.LoadGridsquare.Add(onLoadGridsquare)
