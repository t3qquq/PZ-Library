if not isClient() then return end

local function applyDoorThumpable(args)
	local square = getCell():getGridSquare(args.x, args.y, args.z)
	if not square then
		return
	end
	if (not args.index) or args.index < 0 or args.index >= square:getObjects():size() then
		SetNoThumpable.log("SyncDoorThumpable invalid index at " .. args.x .. "," .. args.y .. "," .. args.z .. " index=" .. tostring(args.index))
		return
	end
	local object = square:getObjects():get(args.index)
	if not SetNoThumpable.isDoorObject(object) then
		SetNoThumpable.log("SyncDoorThumpable not a door at " .. args.x .. "," .. args.y .. "," .. args.z .. " index=" .. tostring(args.index) .. " object=" .. tostring(object))
		return
	end
	if not instanceof(object, "IsoThumpable") then
		SetNoThumpable.log("SyncDoorThumpable expected IsoThumpable at " .. args.x .. "," .. args.y .. "," .. args.z)
		return
	end
	object:setIsThumpable(args.thumpable)
	if args.thumpable then
		object:getModData().SetNoThumpable = nil
	else
		object:getModData().SetNoThumpable = true
	end
end

local function applyBarrierNormalize(args)
	if args.blocked == false then
		SetNoThumpableZombieGuard.unregisterAt(args.x, args.y, args.z, nil)
		SetNoThumpableContextMenu.applyBarrierBlocked(args.x, args.y, args.z, false)
		return
	end
	local square = getCell():getGridSquare(args.x, args.y, args.z)
	if not square then
		SetNoThumpable.log("SyncZombieBarrier no square at " .. args.x .. "," .. args.y .. "," .. args.z)
		return
	end
	local barrier = SetNoThumpable.getZombieBarrier(square)
	if barrier then
		SetNoThumpable.normalizeZombieBarrier(barrier)
		SetNoThumpableZombieGuard.register(barrier)
	elseif args.blocked ~= true then
		SetNoThumpable.log("SyncZombieBarrier no barrier at " .. args.x .. "," .. args.y .. "," .. args.z)
		return
	end
	if args.blocked == true then
		SetNoThumpableContextMenu.applyBarrierBlocked(args.x, args.y, args.z, true)
		return
	end
	SetNoThumpableContextMenu.refreshBarrierOverlay(square)
end

local function onServerCommand(module, command, args)
	if module ~= SetNoThumpable.MODULE then
		return
	end
	if command == SetNoThumpable.COMMAND_SYNC_DOOR then
		applyDoorThumpable(args)
		return
	end
	if command == SetNoThumpable.COMMAND_SYNC_BARRIER then
		applyBarrierNormalize(args)
		return
	end
end

Events.OnServerCommand.Add(onServerCommand)
