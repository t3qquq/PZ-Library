-- ************************************************************************
-- **        ██████  ██████   █████  ██    ██ ███████ ███    ██          **
-- **        ██   ██ ██   ██ ██   ██ ██    ██ ██      ████   ██          **
-- **        ██████  ██████  ███████ ██    ██ █████   ██ ██  ██          **
-- **        ██   ██ ██   ██ ██   ██  ██  ██  ██      ██  ██ ██          **
-- **        ██████  ██   ██ ██   ██   ████   ███████ ██   ████          **
-- ************************************************************************
-- ** All rights reserved. This content is protected by © Copyright law. **
-- ************************************************************************

CSServer = {}

CSServer.PryDoorOpen = function(worldObjects, priableObject, playerObj)

    if instanceof(priableObject, "IsoDoor") then

        priableObject:setLockedByKey(false)

        local doubleDoorObjects = buildUtil.getDoubleDoorObjects(priableObject)

        for i=1,#doubleDoorObjects do
            local object = doubleDoorObjects[i]
            object:setLockedByKey(false)
        end

        local garageDoorObjects = buildUtil.getGarageDoorObjects(priableObject)

        for i=1,#garageDoorObjects do
            local object = garageDoorObjects[i]
            object:setLockedByKey(false)
            playerObj:getXp():AddXP(Perks.Strength, 5)
        end

        ISTimedActionQueue.add(ISOpenCloseDoor:new(playerObj, priableObject, 0))
        BB_CS_Utils.TryPlaySoundClip(playerObj, "BreakBarricadePlank")
        playerObj:getXp():AddXP(Perks.Strength, 7)

    elseif instanceof(priableObject, "IsoWindow") then

        if ZombRand(100) > SandboxVars.CommonSense.WindowShatterChance then
            priableObject:setIsLocked(false) -- Code snippet thanks to "Buffy"!
            priableObject:setPermaLocked(false)
            ISWorldObjectContextMenu.onOpenCloseWindow(worldObjects, priableObject, playerObj:getPlayerNum())
            playerObj:getXp():AddXP(Perks.Strength, 4)
        else
            priableObject:setSmashed(true)
            BB_CS_Utils.TryPlaySoundClip(playerObj, "SmashWindow")
            playerObj:getXp():AddXP(Perks.Strength, 3)
        end
    end
end