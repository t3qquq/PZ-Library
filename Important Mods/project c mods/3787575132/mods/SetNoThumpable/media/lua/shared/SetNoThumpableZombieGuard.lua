SetNoThumpableZombieGuard = {}

local BARRIER_CHUNK_SIZE = 10
local BARRIER_ESCAPE_RADIUS = 8
local barriersByZ = {}
local watchedChunks = {}
local barrierPositions = {}
local escapeWarnings = {}
local firstBlockLogged = {}
local barrierCount = 0
local zombieUpdateRegistered = false
local onZombieUpdate

local function getRuntime()
	if isServer() then
		return "server"
	end
	if isClient() then
		return "client"
	end
	return "sp"
end

local function getRegisteredBarrier(x, y, z)
	x = math.floor(x)
	y = math.floor(y)
	z = math.floor(z)
	local zMap = barriersByZ[z]
	if not zMap then
		return nil
	end
	local xMap = zMap[x]
	if not xMap then
		return nil
	end
	return xMap[y]
end

local function getRegisteredBarrierOnSquare(square)
	if not square then
		return nil
	end
	return getRegisteredBarrier(square:getX(), square:getY(), square:getZ())
end

local function adjustWatchedChunk(z, chunkX, chunkY, delta)
	local zMap = watchedChunks[z]
	if not zMap then
		if delta < 0 then
			return
		end
		zMap = { _xCount = 0 }
		watchedChunks[z] = zMap
	end
	local xMap = zMap[chunkX]
	if not xMap then
		if delta < 0 then
			return
		end
		xMap = { _yCount = 0 }
		zMap[chunkX] = xMap
		zMap._xCount = zMap._xCount + 1
	end
	local oldValue = xMap[chunkY]
	local newValue = (oldValue or 0) + delta
	if newValue > 0 then
		if not oldValue then
			xMap._yCount = xMap._yCount + 1
		end
		xMap[chunkY] = newValue
		return
	end
	if not oldValue then
		return
	end
	xMap[chunkY] = nil
	xMap._yCount = xMap._yCount - 1
	if xMap._yCount > 0 then
		return
	end
	zMap[chunkX] = nil
	zMap._xCount = zMap._xCount - 1
	if zMap._xCount == 0 then
		watchedChunks[z] = nil
	end
end

local function adjustBarrierWatch(x, y, z, delta)
	local chunkX = math.floor(x / BARRIER_CHUNK_SIZE)
	local chunkY = math.floor(y / BARRIER_CHUNK_SIZE)
	for offsetX = -1, 1 do
		for offsetY = -1, 1 do
			adjustWatchedChunk(z, chunkX + offsetX, chunkY + offsetY, delta)
		end
	end
end

local function isWatchedPosition(x, y, z)
	local zMap = watchedChunks[math.floor(z)]
	if not zMap then
		return false
	end
	local chunkX = math.floor(x / BARRIER_CHUNK_SIZE)
	local xMap = zMap[chunkX]
	if not xMap then
		return false
	end
	local chunkY = math.floor(y / BARRIER_CHUNK_SIZE)
	return xMap[chunkY] ~= nil
end

local function refreshZombieUpdateEvent()
	if barrierCount > 0 then
		if not zombieUpdateRegistered then
			Events.OnZombieUpdate.Add(onZombieUpdate)
			zombieUpdateRegistered = true
			SetNoThumpable.log("zombie guard enabled runtime=" .. getRuntime() .. " barriers=" .. tostring(barrierCount))
		end
		return
	end
	if zombieUpdateRegistered then
		Events.OnZombieUpdate.Remove(onZombieUpdate)
		zombieUpdateRegistered = false
		SetNoThumpable.log("zombie guard disabled runtime=" .. getRuntime())
	end
end

function SetNoThumpableZombieGuard.register(barrier)
	local square = barrier:getSquare()
	if not square then
		return false
	end
	local x = square:getX()
	local y = square:getY()
	local z = square:getZ()
	local zMap = barriersByZ[z]
	if not zMap then
		zMap = { _xCount = 0 }
		barriersByZ[z] = zMap
	end
	local xMap = zMap[x]
	if not xMap then
		xMap = { _yCount = 0 }
		zMap[x] = xMap
		zMap._xCount = zMap._xCount + 1
	end
	local existing = xMap[y]
	if existing then
		if existing ~= barrier then
			barrierPositions[existing] = nil
			escapeWarnings[existing] = nil
			firstBlockLogged[existing] = nil
			xMap[y] = barrier
		end
		barrierPositions[barrier] = { x = x, y = y, z = z }
		return false
	end
	xMap[y] = barrier
	barrierPositions[barrier] = { x = x, y = y, z = z }
	xMap._yCount = xMap._yCount + 1
	barrierCount = barrierCount + 1
	adjustBarrierWatch(x, y, z, 1)
	refreshZombieUpdateEvent()
	return true
end

function SetNoThumpableZombieGuard.unregisterAt(x, y, z, expectedBarrier)
	x = math.floor(x)
	y = math.floor(y)
	z = math.floor(z)
	local zMap = barriersByZ[z]
	if not zMap then
		return false
	end
	local xMap = zMap[x]
	if not xMap then
		return false
	end
	local barrier = xMap[y]
	if not barrier or (expectedBarrier and barrier ~= expectedBarrier) then
		return false
	end
	xMap[y] = nil
	xMap._yCount = xMap._yCount - 1
	barrierPositions[barrier] = nil
	escapeWarnings[barrier] = nil
	firstBlockLogged[barrier] = nil
	if xMap._yCount == 0 then
		zMap[x] = nil
		zMap._xCount = zMap._xCount - 1
		if zMap._xCount == 0 then
			barriersByZ[z] = nil
		end
	end
	barrierCount = barrierCount - 1
	adjustBarrierWatch(x, y, z, -1)
	refreshZombieUpdateEvent()
	return true
end

function SetNoThumpableZombieGuard.unregister(barrier)
	local position = barrierPositions[barrier]
	if position then
		return SetNoThumpableZombieGuard.unregisterAt(position.x, position.y, position.z, barrier)
	end
	return false
end

function SetNoThumpableZombieGuard.syncSquare(square)
	local barrier = SetNoThumpable.getZombieBarrier(square)
	if barrier then
		SetNoThumpableZombieGuard.register(barrier)
		return true
	end
	SetNoThumpableZombieGuard.unregisterAt(square:getX(), square:getY(), square:getZ(), nil)
	return false
end

function SetNoThumpableZombieGuard.forEachBarrier(callback)
	for z, zMap in pairs(barriersByZ) do
		if type(z) == "number" then
			for x, xMap in pairs(zMap) do
				if type(x) == "number" then
					for y, barrier in pairs(xMap) do
						if type(y) == "number" then
							callback(barrier, x, y, z)
						end
					end
				end
			end
		end
	end
end

function SetNoThumpableZombieGuard.reset()
	if zombieUpdateRegistered then
		Events.OnZombieUpdate.Remove(onZombieUpdate)
		zombieUpdateRegistered = false
	end
	barriersByZ = {}
	watchedChunks = {}
	barrierPositions = {}
	escapeWarnings = {}
	firstBlockLogged = {}
	barrierCount = 0
end

local function isEscapeSquare(square)
	return square
		and not getRegisteredBarrierOnSquare(square)
		and square:TreatAsSolidFloor()
		and not square:isSolid()
		and not square:isSolidTrans()
end

local function clampToSquare(value, coordinate)
	local minimum = coordinate + 0.05
	local maximum = coordinate + 0.95
	if value < minimum then
		return minimum
	end
	if value > maximum then
		return maximum
	end
	return value
end

local function findPreviousEscapeSquare(zombie, barrierSquare)
	local x = zombie:getLx()
	local y = zombie:getLy()
	local square = getCell():getGridSquare(math.floor(x), math.floor(y), barrierSquare:getZ())
	if square ~= barrierSquare and isEscapeSquare(square) then
		return square, x, y
	end
	return nil
end

local function findNearestEscapeSquare(zombie, barrierSquare)
	local barrierX = barrierSquare:getX()
	local barrierY = barrierSquare:getY()
	local z = barrierSquare:getZ()
	local zombieX = zombie:getX()
	local zombieY = zombie:getY()
	for radius = 1, BARRIER_ESCAPE_RADIUS do
		local bestSquare = nil
		local bestX = nil
		local bestY = nil
		local bestDistance = nil
		local minimumX = barrierX - radius
		local maximumX = barrierX + radius
		local minimumY = barrierY - radius
		local maximumY = barrierY + radius
		local function considerSquare(x, y)
			local square = getCell():getGridSquare(x, y, z)
			if not isEscapeSquare(square) then
				return
			end
			local candidateX = clampToSquare(zombieX, x)
			local candidateY = clampToSquare(zombieY, y)
			local dx = candidateX - zombieX
			local dy = candidateY - zombieY
			local distance = dx * dx + dy * dy
			if not bestDistance or distance < bestDistance then
				bestSquare = square
				bestX = candidateX
				bestY = candidateY
				bestDistance = distance
			end
		end
		for x = minimumX, maximumX do
			considerSquare(x, minimumY)
			if maximumY ~= minimumY then
				considerSquare(x, maximumY)
			end
		end
		for y = minimumY + 1, maximumY - 1 do
			considerSquare(minimumX, y)
			if maximumX ~= minimumX then
				considerSquare(maximumX, y)
			end
		end
		if bestSquare then
			return bestSquare, bestX, bestY
		end
	end
	return nil
end

local function moveZombieToSquare(zombie, square, x, y)
	zombie:setX(x)
	zombie:setY(y)
	zombie:setLx(x)
	zombie:setLy(y)
	zombie:setNx(x)
	zombie:setNy(y)
	zombie:setCurrent(square)
end

local function logFirstBlock(barrier)
	if firstBlockLogged[barrier] then
		return
	end
	firstBlockLogged[barrier] = true
	local square = barrier:getSquare()
	if square then
		SetNoThumpable.log("zombie guard blocked runtime=" .. getRuntime() .. " at " .. SetNoThumpable.squarePos(square))
		return
	end
	SetNoThumpable.log("zombie guard blocked runtime=" .. getRuntime())
end

local function stopZombieAtBarrier(zombie, barrier)
	if zombie:getThumpTarget() ~= barrier then
		zombie:setThumpTarget(barrier)
	end
	zombie:setPath2(nil)
	zombie:setVariable("bPathfind", false)
	zombie:setVariable("bMoving", false)
	zombie:setNx(zombie:getX())
	zombie:setNy(zombie:getY())
	zombie:faceThisObject(barrier)
	logFirstBlock(barrier)
end

local function recoverZombieFromBarrier(zombie, barrier)
	local barrierSquare = barrier:getSquare()
	if not barrierSquare then
		return
	end
	local square, x, y = findPreviousEscapeSquare(zombie, barrierSquare)
	if not square then
		square, x, y = findNearestEscapeSquare(zombie, barrierSquare)
	end
	if not square then
		if not escapeWarnings[barrier] then
			escapeWarnings[barrier] = true
			SetNoThumpable.log("zombie barrier has no escape square at " .. SetNoThumpable.squarePos(barrierSquare))
		end
		stopZombieAtBarrier(zombie, barrier)
		return
	end
	moveZombieToSquare(zombie, square, x, y)
	stopZombieAtBarrier(zombie, barrier)
end

-- b41 coop gives nearby zombies to the owning client. Server/SP keep full
-- authority. Clients skip remote zombies so interpolated positions stay intact.
onZombieUpdate = function(zombie)
	if barrierCount == 0 then
		return
	end
	if isClient() and zombie:isRemoteZombie() then
		return
	end
	if not isWatchedPosition(zombie:getX(), zombie:getY(), zombie:getZ()) then
		return
	end
	local z = math.floor(zombie:getZ())
	local barrier = getRegisteredBarrier(zombie:getX(), zombie:getY(), z)
	if barrier then
		recoverZombieFromBarrier(zombie, barrier)
		return
	end
	barrier = getRegisteredBarrierOnSquare(zombie:getCurrentSquare())
	if barrier then
		recoverZombieFromBarrier(zombie, barrier)
		return
	end
	barrier = getRegisteredBarrier(zombie:getNx(), zombie:getNy(), z)
	if barrier then
		stopZombieAtBarrier(zombie, barrier)
		return
	end
	local feelerSquare = zombie:getFeelerTile(zombie:getFeelersize())
	barrier = getRegisteredBarrierOnSquare(feelerSquare)
	if barrier then
		stopZombieAtBarrier(zombie, barrier)
	end
end

local function onLoadGridsquare(square)
	SetNoThumpableZombieGuard.syncSquare(square)
end

local function onObjectAdded(object)
	if SetNoThumpable.isZombieBarrier(object) then
		SetNoThumpableZombieGuard.register(object)
	end
end

local function onObjectAboutToBeRemoved(object)
	if SetNoThumpable.isZombieBarrier(object) then
		SetNoThumpableZombieGuard.unregister(object)
	end
end

local function onReuseGridsquare(square)
	SetNoThumpableZombieGuard.unregisterAt(square:getX(), square:getY(), square:getZ(), nil)
end

Events.LoadGridsquare.Add(onLoadGridsquare)
Events.OnObjectAdded.Add(onObjectAdded)
Events.OnObjectAboutToBeRemoved.Add(onObjectAboutToBeRemoved)
Events.ReuseGridsquare.Add(onReuseGridsquare)
