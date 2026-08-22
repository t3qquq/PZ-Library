if isClient() then
    function checkSleep()
        local player = getPlayer()
        local getPlayerID = player:getOnlineID()
        if player:isAsleep() then
            sendClientCommand("SleepWithFriends", "Sleep", {playerId = getPlayerID, bed = player:getBedType()})
        end
    end
    Events.EveryOneMinute.Add(checkSleep)

    function updates(module, command, args)
        if type(module) ~= "string" or not module:match("^SleepWithFriends") then return end
        local player = getPlayer()
        if command == "Update" then
            player:getStats():set(CharacterStat.FATIGUE, args.fatigue )
            player:getStats():set(CharacterStat.ENDURANCE, args.endurance )
        elseif command == "WakeUp" then
            player:forceAwake()
        end
    end
    Events.OnServerCommand.Add(updates)
end

if isServer() then
    sleepDB = sleepDB or {}

    function sleep(module, command, playerObj, args)
        if type(module) ~= "string" or not module:match("^SleepWithFriends") then return end
        if command ~= "Sleep" then return end
        if args then
            local rtMod
            if SandboxVars.DayLength == 1 then rtMod =  .25   -- 15 minutes
                elseif SandboxVars.DayLength == 2 then rtMod = .5   -- 30 minutes
                elseif SandboxVars.DayLength == 3 then rtMod = 1   -- 1 hour (b41 default)
                elseif SandboxVars.DayLength == 4 then rtMod = 1.5   -- 90 minutes (b42 default)
                elseif SandboxVars.DayLength > 4 then rtMod = (SandboxVars.DayLength - 3)   -- 2+ hours
            end

            local bedMod
            if args.bed == "averageBed" then bedMod = SandboxVars.SleepWithFriends.AverageBedMulti
            elseif args.bed == "averageBedPillow" then bedMod = SandboxVars.SleepWithFriends.AverageBedPillowMulti
            elseif args.bed == "goodBed" then bedMod = SandboxVars.SleepWithFriends.GoodBedMulti
            elseif args.bed == "goodBedPillow" then bedMod = SandboxVars.SleepWithFriends.GoodBedPillowMulti
            elseif args.bed == "badBed" then bedMod = SandboxVars.SleepWithFriends.BadBedMulti
            elseif args.bed == "badBedPillow" then bedMod = SandboxVars.SleepWithFriends.BadBedPillowMulti
            elseif args.bed == "floor" then bedMod = SandboxVars.SleepWithFriends.FloorMulti
            elseif args.bed == "floorPillow" then bedMod = SandboxVars.SleepWithFriends.FloorPillowMulti
            end

            local fatigueReduction
            local sleepLength = tonumber(SandboxVars.SleepWithFriends.SleepLength)
            if sleepLength == nil or sleepLength <= 0 then fatigueReduction = 1
            elseif SandboxVars.SleepWithFriends.RTorIG == 1 then fatigueReduction = ((1 / (24 / rtMod )) / sleepLength) * bedMod   -- Real-Time
            elseif SandboxVars.SleepWithFriends.RTorIG == 2 then fatigueReduction = (1 / (SandboxVars.SleepWithFriends.SleepLength * 60)) * bedMod -- In-Game
            else print("Warning: Invalid or missing RTorIG value: " .. tostring(SandboxVars.SleepWithFriends.RTorIG)); return
            end


            local player = getPlayerByOnlineID(args.playerId)
            local playerID = tostring(args.playerId)
            if player:isAsleep() then
                if not sleepDB[playerID] then
                    sleepDB[playerID] = {fatigue = player:getStats():get(CharacterStat.FATIGUE), endurance = player:getStats():get(CharacterStat.ENDURANCE)}
                end
                local newFatigue = math.max(0, sleepDB[playerID].fatigue - fatigueReduction)
                local newEndurance = math.min(1, sleepDB[playerID].endurance + (fatigueReduction * SandboxVars.SleepWithFriends.EndurMulti))
                player:getStats():set(CharacterStat.FATIGUE, newFatigue )
                player:getStats():set(CharacterStat.ENDURANCE, newEndurance )
                sendServerCommand(player, "SleepWithFriends", "Update", {fatigue = newFatigue, endurance = newEndurance})
                sleepDB[playerID] = {fatigue = newFatigue, endurance = newEndurance}
                local autoWakeEnabled = SandboxVars.SleepWithFriends.AutoWake == true or SandboxVars.SleepWithFriends.AutoWake == 1 -- new boolean and old enum to not break existing servers
                if newFatigue <= 0 and autoWakeEnabled then
                    sendServerCommand(player, "SleepWithFriends", "WakeUp", {})
                end
            end
        end
    end
    Events.OnClientCommand.Add(sleep)

    function cleanSleepDB()
        local playerList = getOnlinePlayers()
        local onlinePlayers = {}
        for i = 0, playerList:size() - 1 do
            local player = playerList:get(i)
            local id = tostring(player:getOnlineID())
            onlinePlayers[id] = true
            if not player:isAsleep() then sleepDB[id] = nil end
        end

        for playerID in pairs(sleepDB) do
            if not onlinePlayers[playerID] then sleepDB[playerID] = nil end
        end
    end
    Events.EveryTenMinutes.Add(cleanSleepDB)

end
