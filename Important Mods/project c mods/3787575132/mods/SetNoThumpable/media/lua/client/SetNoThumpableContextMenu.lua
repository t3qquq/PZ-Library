SetNoThumpableContextMenu = {}

local syncMenuAccessOverlays

local function removeDuplicates(list)
	local result = {}
	local seen = {}
	for _, item in ipairs(list) do
		if not seen[item] then
			seen[item] = true
			table.insert(result, item)
		end
	end
	return result
end

local function addDisabledTooltip(option, text)
	option.notAvailable = true
	local tooltip = ISWorldObjectContextMenu.addToolTip()
	tooltip.description = text
	option.toolTip = tooltip
end

function SetNoThumpableContextMenu.doMenu(player, context, worldobjects, test)
	syncMenuAccessOverlays()
	if not SetNoThumpable.canUseMenu() then
		return
	end
	if test and ISWorldObjectContextMenu.Test then
		return true
	end

	local square = nil
	for _, object in ipairs(worldobjects) do
		square = object:getSquare()
		break
	end

	for i = 1, square:getObjects():size() do
		table.insert(worldobjects, square:getObjects():get(i - 1))
	end
	worldobjects = removeDuplicates(worldobjects)

	local barrier = SetNoThumpable.getZombieBarrier(square)
	if test then
		return ISWorldObjectContextMenu.setTest()
	end
	if barrier then
		context:addOption(getText("ContextMenu_AllowZombieEntry"), worldobjects, SetNoThumpableContextMenu.onSetZombieBarrier, square, player, false)
	else
		context:addOption(getText("ContextMenu_BlockZombieEntry"), worldobjects, SetNoThumpableContextMenu.onSetZombieBarrier, square, player, true)
	end

	local seenDoors = {}
	for _, object in ipairs(worldobjects) do
		if SetNoThumpable.isDoorObject(object) and not seenDoors[object] then
			local linkedDoors = SetNoThumpable.getLinkedDoors(object)
			SetNoThumpable.markLinkedDoors(linkedDoors, seenDoors)
			local groupEnabled = SetNoThumpable.isGroupThumpableEnabled(linkedDoors)
			local label = getText("ContextMenu_DisableThumpable")
			if not groupEnabled then
				label = getText("ContextMenu_EnableThumpable")
			end
			local option = context:addOption(label, worldobjects, SetNoThumpableContextMenu.onSetDoorThumpable, object, player, not groupEnabled)
			if groupEnabled then
				local reason = SetNoThumpable.getConversionBlockReason(linkedDoors)
				if reason == "curtains" then
					addDisabledTooltip(option, getText("ContextMenu_CannotConvertCurtains"))
				elseif reason == "barricade" then
					addDisabledTooltip(option, getText("ContextMenu_CannotConvertBarricade"))
				end
			end
		end
	end
end

function SetNoThumpableContextMenu.onSetDoorThumpable(worldobjects, door, player, thumpable)
	local square = door:getSquare()
	local args = {}
	args.x = square:getX()
	args.y = square:getY()
	args.z = square:getZ()
	args.index = door:getObjectIndex()
	args.thumpable = thumpable
	SetNoThumpable.log("request SetDoorThumpable thumpable=" .. tostring(thumpable) .. " x=" .. args.x .. " y=" .. args.y .. " z=" .. args.z .. " index=" .. args.index)
	if isClient() then
		sendClientCommand(getSpecificPlayer(player), SetNoThumpable.MODULE, SetNoThumpable.COMMAND_SET_DOOR, args)
		return
	end
	SetNoThumpableCommands.SetDoorThumpable(getSpecificPlayer(player), args)
end

function SetNoThumpableContextMenu.onSetZombieBarrier(worldobjects, square, player, blocked)
	local args = {}
	args.x = square:getX()
	args.y = square:getY()
	args.z = square:getZ()
	args.blocked = blocked
	SetNoThumpable.log("request SetZombieBarrier blocked=" .. tostring(blocked) .. " x=" .. args.x .. " y=" .. args.y .. " z=" .. args.z)
	if isClient() then
		sendClientCommand(getSpecificPlayer(player), SetNoThumpable.MODULE, SetNoThumpable.COMMAND_SET_BARRIER, args)
		return
	end
	SetNoThumpableCommands.SetZombieBarrier(getSpecificPlayer(player), args)
	SetNoThumpableZombieGuard.syncSquare(square)
	SetNoThumpableContextMenu.refreshBarrierOverlay(square)
end

SetNoThumpableContextMenu.barrierMarkers = {}

local function posKey(x, y, z)
	return SetNoThumpable.posKey(x, y, z)
end

local function squareKey(square)
	return posKey(square:getX(), square:getY(), square:getZ())
end

local function clearBarrierOverlayAt(x, y, z)
	local key = posKey(x, y, z)
	local marker = SetNoThumpableContextMenu.barrierMarkers[key]
	if not marker then
		return
	end
	if not marker:isRemoved() then
		marker:remove()
	end
	SetNoThumpableContextMenu.barrierMarkers[key] = nil
end

local function clearBarrierOverlay(square)
	clearBarrierOverlayAt(square:getX(), square:getY(), square:getZ())
end

local function forgetAllOverlays()
	SetNoThumpableContextMenu.barrierMarkers = {}
end

local lastMenuAccess = false

local function hideAllBarrierOverlays()
	for _, marker in pairs(SetNoThumpableContextMenu.barrierMarkers) do
		if marker and not marker:isRemoved() then
			marker:remove()
		end
	end
	forgetAllOverlays()
end

-- ExtraInfo updates access with no Lua event. Edge-detect on vanilla
-- interaction/time/chat hooks so grant/revoke mid-session hides or restores.
syncMenuAccessOverlays = function()
	local allowed = SetNoThumpable.canUseMenu()
	if allowed == lastMenuAccess then
		return
	end
	lastMenuAccess = allowed
	if allowed then
		SetNoThumpableContextMenu.restoreBarrierOverlays()
		return
	end
	hideAllBarrierOverlays()
	SetNoThumpable.log("cleared barrier overlays, menu access lost")
end

-- WorldMarkers.reset() on IngameState.exit clears the Java list without
-- isRemoved. Lua can keep a dead marker; setActive would then no-op.
local function isLiveGridSquareMarker(marker)
	if not marker or marker:isRemoved() then
		return false
	end
	return getWorldMarkers():getGridSquareMarker(marker:getID()) == marker
end

-- Vanilla Tutorial 11-arg overload: tex, overlay, square, r, g, b, doAlpha,
-- size, fadeSpeed, fadeMin, fadeMax. Keep min/max close so the marker stays visible.
local function showBarrierOverlay(square)
	if not SetNoThumpable.canUseMenu() then
		return
	end
	local key = squareKey(square)
	local marker = SetNoThumpableContextMenu.barrierMarkers[key]
	if isLiveGridSquareMarker(marker) then
		marker:setActive(true)
		return
	end
	if marker and not marker:isRemoved() then
		marker:remove()
	end
	marker = getWorldMarkers():addGridSquareMarker("circle_center", "circle_only_highlight", square, 1.0, 0.25, 0.05, true, 0.9, 0.02, 0.55, 0.9)
	marker:setActive(true)
	marker:setScaleCircleTexture(true)
	SetNoThumpableContextMenu.barrierMarkers[key] = marker
end

function SetNoThumpableContextMenu.applyBarrierBlocked(x, y, z, blocked)
	if not blocked then
		clearBarrierOverlayAt(x, y, z)
		return
	end
	if not SetNoThumpable.canUseMenu() then
		return
	end
	local square = getCell():getGridSquare(x, y, z)
	if not square then
		SetNoThumpable.log("barrier overlay skipped, no square at " .. posKey(x, y, z))
		return
	end
	showBarrierOverlay(square)
end

function SetNoThumpableContextMenu.refreshBarrierOverlay(square)
	if not SetNoThumpable.getZombieBarrier(square) then
		clearBarrierOverlay(square)
		return
	end
	if not SetNoThumpable.canUseMenu() then
		return
	end
	showBarrierOverlay(square)
end

function SetNoThumpableContextMenu.restoreBarrierOverlays()
	if not SetNoThumpable.canUseMenu() then
		return false
	end
	local count = 0
	SetNoThumpableZombieGuard.forEachBarrier(function(barrier)
		local square = barrier:getSquare()
		if square then
			SetNoThumpableContextMenu.refreshBarrierOverlay(square)
			count = count + 1
		end
	end)
	SetNoThumpable.log("restored barrier overlays count=" .. tostring(count))
	return true
end

local function onObjectAdded(object)
	if not SetNoThumpable.isZombieBarrier(object) then
		return
	end
	SetNoThumpable.normalizeZombieBarrier(object)
	SetNoThumpableZombieGuard.register(object)
	local square = object:getSquare()
	if square then
		SetNoThumpableContextMenu.refreshBarrierOverlay(square)
	end
end

local function onObjectAboutToBeRemoved(object)
	if not SetNoThumpable.isZombieBarrier(object) then
		return
	end
	SetNoThumpableZombieGuard.unregister(object)
	local square = object:getSquare()
	if square then
		clearBarrierOverlay(square)
		return
	end
	clearBarrierOverlayAt(object:getX(), object:getY(), object:getZ())
end

local function onLoadGridsquare(square)
	syncMenuAccessOverlays()
	local barrier = SetNoThumpable.getZombieBarrier(square)
	if barrier then
		SetNoThumpable.normalizeZombieBarrier(barrier)
	end
	SetNoThumpableZombieGuard.syncSquare(square)
	SetNoThumpableContextMenu.refreshBarrierOverlay(square)
end

local function onReuseGridsquare(square)
	SetNoThumpableZombieGuard.unregisterAt(square:getX(), square:getY(), square:getZ(), nil)
	clearBarrierOverlay(square)
end

local function onMainMenuEnter()
	lastMenuAccess = false
	forgetAllOverlays()
	SetNoThumpableZombieGuard.reset()
end

local function onCreatePlayer(playerIndex)
	if playerIndex ~= 0 then
		return
	end
	syncMenuAccessOverlays()
end

Events.OnFillWorldObjectContextMenu.Add(SetNoThumpableContextMenu.doMenu)
Events.OnObjectAdded.Add(onObjectAdded)
Events.OnObjectAboutToBeRemoved.Add(onObjectAboutToBeRemoved)
Events.LoadGridsquare.Add(onLoadGridsquare)
Events.ReuseGridsquare.Add(onReuseGridsquare)
Events.OnMainMenuEnter.Add(onMainMenuEnter)
Events.OnCreatePlayer.Add(onCreatePlayer)
Events.OnGameStart.Add(syncMenuAccessOverlays)
Events.OnMiniScoreboardUpdate.Add(syncMenuAccessOverlays)
Events.OnScoreboardUpdate.Add(syncMenuAccessOverlays)
Events.OnTabAdded.Add(syncMenuAccessOverlays)
Events.OnTabRemoved.Add(syncMenuAccessOverlays)
Events.OnKeyPressed.Add(syncMenuAccessOverlays)
Events.EveryOneMinute.Add(syncMenuAccessOverlays)
