local player
local radius


local function setOutline(object, state, blink, color)
    if object == nil then return end
    object:setOutlineHighlight(state)
    object:setOutlineHlBlink(blink)
    object:setOutlineThickness(0.03)
    if color then
        object:setOutlineHighlightCol(color[1], color[2], color[3], color[4])
    end
end

local function getSwitchesInRadius(targetX, targetY, targetZ, radius, squares)
    local minX = targetX - radius
    local maxX = targetX + radius
    local minY = targetY - radius
    local maxY = targetY + radius
    for y = minY,maxY do
        for x = minX,maxX do
            local square = getCell():getGridSquare(x, y, targetZ)
            if square then
                for i = 0, square:getObjects():size()-1 do
                    local obj = square:getObjects():get(i)
                    if instanceof(obj, "IsoLightSwitch") then
                        table.insert(squares, obj)
                    end
                end
            end
        end
    end
end

local function isElecEnabled()
    return SandboxVars.ElecShutModifier > -1 and getGameTime():getNightsSurvived() < SandboxVars.ElecShutModifier
end

local function onTick()
    local objects = {}

    getSwitchesInRadius(player:getX(), player:getY(), player:getZ(), radius, objects)

    for i = #objects, 1, -1 do
        local obj = objects[i]
        local objSq = obj:getSquare()
        local playerSq = player:getCurrentSquare()
        if not playerSq:isWallTo(objSq) and objSq:DistToProper(player) < 2 then
            if isElecEnabled() or objSq:haveElectricity() then
                if obj:isActivated() then
                    setOutline(obj, true, false, {0.5, 1, 0.5, 1})
                else
                    setOutline(obj, true, true, {1, 0.5, 0.5, 1})
                end
            -- else
            --     setOutline(obj, true, true, {1, 1, 0.5, 1, 1})
            end
        else
            setOutline(obj, false, false)
            table.remove(objects, i)
        end 
    end
end

local function onStart()
    player = getSpecificPlayer(0)
    radius = 2.0
    Events.OnTick.Add(onTick)
end

Events.OnGameStart.Add(onStart)
