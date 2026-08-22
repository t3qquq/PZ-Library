-- ************************************************************************
-- **        ██████  ██████   █████  ██    ██ ███████ ███    ██          **
-- **        ██   ██ ██   ██ ██   ██ ██    ██ ██      ████   ██          **
-- **        ██████  ██████  ███████ ██    ██ █████   ██ ██  ██          **
-- **        ██   ██ ██   ██ ██   ██  ██  ██  ██      ██  ██ ██          **
-- **        ██████  ██   ██ ██   ██   ████   ███████ ██   ████          **
-- ************************************************************************
-- ** All rights reserved. This content is protected by © Copyright law. **
-- ************************************************************************

require "BB_WhereAmI_DB"

local areaDB = BB_WhereAmI.areaDB
local areaDBLength = #areaDB

BB_WhereAmI.CheckPlayerArea = function()
    local playerObj = getPlayer()
    local playerSq = playerObj:getSquare()
    local playerX, playerY = playerSq:getX(), playerSq:getY()

    for i = 1, areaDBLength do
        local area = areaDB[i]
        if area.activated then
            if (playerX >= area.startX and playerX <= area.endX and playerY >= area.startY and playerY <= area.endY) then
                BB_WhereAmI.currentArea = area.name
                return
            end
        end
    end

    BB_WhereAmI.currentArea = ""
end

local function onGameStart()
    for i = 1, areaDBLength do
        local area = areaDB[i]
        if not area.activated and area.mod and getActivatedMods():contains(area.mod) then
            area.activated = true
        end
    end
end

Events.OnGameStart.Add(onGameStart)