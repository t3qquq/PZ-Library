-- ************************************************************************
-- **        ██████  ██████   █████  ██    ██ ███████ ███    ██          **
-- **        ██   ██ ██   ██ ██   ██ ██    ██ ██      ████   ██          **
-- **        ██████  ██████  ███████ ██    ██ █████   ██ ██  ██          **
-- **        ██   ██ ██   ██ ██   ██  ██  ██  ██      ██  ██ ██          **
-- **        ██████  ██   ██ ██   ██   ████   ███████ ██   ████          **
-- ************************************************************************
-- ** All rights reserved. This content is protected by © Copyright law. **
-- ************************************************************************
-- OVERRIDE FOR THIS SPECIFIC MOD

BB_CS_Utils = {}

BB_CS_Utils.TryPlaySoundClip = function(obj, soundName)

	if obj:getEmitter():isPlaying(soundName) then return end
    obj:getEmitter():playSoundImpl(soundName, IsoObject.new())

end

BB_CS_Utils.TryStopSoundClip = function(obj, soundName)

	if not obj:getEmitter():isPlaying(soundName) then return end
	obj:getEmitter():stopSoundByName(soundName)
end

BB_CS_Utils.GetProperSound = function (priableObject, getSoundType)

    if not buildUtil.getGarageDoorObjects(priableObject)[1] then
        if not getSoundType then
            return "BeginRemoveBarricadePlank"
        else
            return "Wooden"
        end
    else
        if not getSoundType then
            return "PrisonMetalDoorBlocked"
        else
            return "Metal"
        end
    end
end

BB_CS_Utils.TirePlayer = function(playerObj, amount)

	local stats = playerObj:getStats()
	if not stats then return end

	if stats:getEndurance() < 0.21 then return end
	stats:setEndurance(stats:getEndurance() - (amount / (playerObj:getPerkLevel(Perks.Fitness) / 2)))
end

BB_CS_Utils.GetGameSpeed = function()
    local speedControl = UIManager.getSpeedControls():getCurrentGameSpeed()
    local gameSpeed = 1

    if speedControl == 2 then
        gameSpeed = 5
    elseif speedControl == 3 then
        gameSpeed = 20
    elseif speedControl == 4 then
        gameSpeed = 40
    end

    return gameSpeed
end

BB_CS_Utils.DelayFunction = function(func, delay, adaptToSpeed)

    delay = delay or 1
    local multiplier = 1
    local ticks = 0
    local canceled = false

    local function onTick()
        if adaptToSpeed then multiplier = BB_CS_Utils.GetGameSpeed() end
        if not canceled and ticks < delay then
            ticks = ticks + multiplier
            return
        end

        Events.OnTick.Remove(onTick)
        if not canceled then func() end
    end

    Events.OnTick.Add(onTick)
    return function()
        canceled = true
    end
end

BB_CS_Utils.DistanceBetween = function(firstObj, secondObj)
    local x1, y1, z1 = firstObj:getX(), firstObj:getY(), firstObj:getZ()
    local x2, y2, z2 = secondObj:getX(), secondObj:getY(), secondObj:getZ()

    local dx = x1 - x2
    local dy = y1 - y2
    local dz = z1 - z2

    if dz >= 2 then
        return 999
    end

    local distance = math.sqrt(dx * dx + dy * dy)
    return distance
end

BB_CS_Utils.addTooltip = function(description, menuOption)
    local tooltip = ISWorldObjectContextMenu.addToolTip()
    tooltip.description = description
    menuOption.toolTip = tooltip
end