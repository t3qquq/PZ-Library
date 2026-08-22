----------------
--somewhatfrog--
----------------

local customOffroad = {
    --vanilla
    ["Base.OffRoad"] = true,
    --KI5
    ["Base.63beetleBuggy"] = true,
    ["Base.63Type2VanApocalypse"] = true,
    --Filibuster
    ["Base.92wrangler"] = true,
    ["Base.92wranglerjurassic"] = true,
    ["Base.92wrangleroffroad"] = true,
    ["Base.92wranglerranger"] = true,
    --ATA
    ["Base.ATAJeepArcher"] = true,
    ["Base.ATAJeepRubicon"] = true,
    ["Base.ATAJeepClassic"] = true,
    ["Base.ATAJeep"] = true,
}

local heavyMultiplier = 1
local standardMultiplier = 1
local sportMultiplier = 1

local heavyThreshold = 1
local standardThreshold = 1
local sportThreshold = 1

local currentTime = 0
local grabVehicleStatsTime = 0
local grabVehicleStatsTimer = 5000
local applyVehicleStatsTime = 0
local applyVehicleStatsTimer = 500
local vehicleId = 0
local scriptMass = 0
local scriptEngineForce = 1000
local offroadVehicle = false
local excludedVehicle = false
local vehicleType = "none"
local heavyOffroadMass = 0
local standardOffroadMass = 0
local sportOffroadMass = 0

local towingToggle = true
local trailer
local towedId = 0
local towedScriptMass = 0
local towedByHighMass = 0
local towedByLowMass = 0

local function grabVehicleStats()
    local player = getPlayer()
    local vehicle = player:getVehicle()
    if vehicle then
        local vehicleScript = vehicle:getScript()
        vehicleId = vehicle:getId()
        scriptMass = vehicleScript:getMass()

        --figure the mass of all installed parts to get the total mass of the empty vehicle
        local extraMass = 0
        local carriedMass = 0
        local partCount = vehicle:getPartCount()
        for i = 1, partCount do
            local part = vehicle:getPartByIndex(i - 1)
            local inventoryItem = part:getInventoryItem()
            local itemContainer = part:getItemContainer()
            if inventoryItem then
                extraMass = extraMass + inventoryItem:getWeight()
            end
            if itemContainer then
                carriedMass = carriedMass + itemContainer:getCapacityWeight()
            end
        end

        local totalMass = scriptMass + extraMass + carriedMass
        local sportMagicNumber = scriptEngineForce * 0.0016
        --(not so) magic calculations
        heavyOffroadMass = math.floor((totalMass * heavyMultiplier) - extraMass - carriedMass)
        standardOffroadMass = math.floor((totalMass * standardMultiplier) - extraMass - carriedMass)
        sportOffroadMass = math.floor((totalMass * sportMultiplier * sportMagicNumber) - extraMass - carriedMass)

        --adjust the weight of the towed vehicle
        if towingToggle then
            local towedVehicle = vehicle:getVehicleTowing()
            if towedVehicle then
                local towedScript = towedVehicle:getScript()
                local towedName = towedScript:getName()
                trailer = string.find(towedName, "Trailer")
                --exclude trailers because the main factor in towing is the applied braking force
                if not trailer then
                    towedId = towedVehicle:getId()
                    towedScriptMass = towedScript:getMass()

                    --figure the mass of all installed parts to get the total mass of the empty vehicle
                    local towedExtraMass = 0
                    local towedCarriedMass = 0
                    local towedPartCount = vehicle:getPartCount()
                    for i = 1, towedPartCount do
                        local part = vehicle:getPartByIndex(i - 1)
                        local inventoryItem = part:getInventoryItem()
                        local itemContainer = part:getItemContainer()
                        if inventoryItem then
                            towedExtraMass = towedExtraMass + inventoryItem:getWeight()
                        end
                        if itemContainer then
                            towedCarriedMass = towedCarriedMass + itemContainer:getCapacityWeight()
                        end
                    end

                    local totalTowedMass = towedScriptMass + towedExtraMass + towedCarriedMass
                    towedByHighMass = math.floor((totalTowedMass * heavyMultiplier) - towedExtraMass - towedCarriedMass)
                    towedByLowMass = math.floor((totalTowedMass * sportMultiplier) - towedExtraMass - towedCarriedMass)
                end
            end
        end

        --beep boop
        if SandboxVars.OffroadGoBrrr.Debug then
            print("Name: " .. vehicle:getScriptName() .. " | Id: " .. vehicle:getId() .. " | Type: " .. vehicleType)
            print("Mass Base: " .. math.floor(totalMass) .. " | Initial: " .. math.floor(vehicle:getInitialMass()) .. " / " .. vehicleScript:getMass() .. " | Current: " .. math.floor(vehicle:getMass()))
            print("RPM Type: " .. vehicleScript:getEngineRPMType() .. " | Speed: " .. math.floor(vehicle:getCurrentSpeedKmHour()) .. " / " .. vehicle:getMaxSpeed())
            if towedId > 0 then
                print("TowedName: " .. getVehicleById(towedId):getScriptName() .. " | TowedId: " .. getVehicleById(towedId):getId())
                print("TowedInitial: " .. math.floor(getVehicleById(towedId):getInitialMass()) .. " / " .. getVehicleById(towedId):getScript():getMass() .. " | CurrentTowed: " .. math.floor(getVehicleById(towedId):getMass()))
            end
        end
    end
end

local function applyVehicleStats()
    local player = getPlayer()
    local vehicle = player:getVehicle()
    if vehicle then
        if not vehicle:isDriver(player) or not vehicle:isEngineRunning() then return end

        --compare mass to avoid setting the vehicle mass over and over, performance letsgooo
        local initialMass = vehicle:getInitialMass()
        local doingOffroad = vehicle:isDoingOffroad()

        if doingOffroad then
            if initialMass == scriptMass then
                if vehicleType == "high" then
                    vehicle:setInitialMass(heavyOffroadMass)
                end
                if vehicleType == "normal" then
                    vehicle:setInitialMass(standardOffroadMass)
                end
                if vehicleType == "low" then
                    vehicle:setInitialMass(sportOffroadMass)
                end

                vehicle:updateTotalMass()
                --player:Say("Offroad")
            end
        elseif initialMass ~= scriptMass then
            vehicle:setInitialMass(scriptMass)
            vehicle:updateTotalMass()
            --player:Say("Onroad")
        end

        if towingToggle then
            local towedVehicle = vehicle:getVehicleTowing()
            if towedVehicle then
                local towedInitialMass = towedVehicle:getInitialMass()
                if towedInitialMass == towedScriptMass then
                    if vehicleType == "high" then
                        towedVehicle:setInitialMass(towedByHighMass)
                    end
                    if vehicleType == "low" then
                        towedVehicle:setInitialMass(towedByLowMass)
                    end

                    towedVehicle:updateTotalMass()
                end
            elseif towedId > 0 then
                local exTowedVehicle = getVehicleById(towedId)
                exTowedVehicle:setInitialMass(towedScriptMass)
                exTowedVehicle:updateTotalMass()
                towedId = 0
            end
        end
    end
end

local function offroadGoBrrr()
    currentTime = getTimestampMs()

    --every 5 seconds
    if currentTime > grabVehicleStatsTime + grabVehicleStatsTimer then
        grabVehicleStats()
        grabVehicleStatsTime = currentTime
        --print("StatsGrabbed")
    end

    --every 0.5sec in case of heavy desync and/or rapid change in surface
    if currentTime > applyVehicleStatsTime + applyVehicleStatsTimer then
        applyVehicleStats()
        applyVehicleStatsTime = currentTime
        --print("StatsApplied")
    end
end

local function OnEnterVehicle(player)
    Events.OnTick.Remove(offroadGoBrrr)

    if SandboxVars.OffroadGoBrrr.Toggle then
        heavyMultiplier = SandboxVars.OffroadGoBrrr.HeavyMultiplier
        standardMultiplier = SandboxVars.OffroadGoBrrr.StandardMultiplier
        sportMultiplier = SandboxVars.OffroadGoBrrr.SportMultiplier

        heavyThreshold = SandboxVars.OffroadGoBrrr.HeavyThreshold
        standardThreshold = SandboxVars.OffroadGoBrrr.StandardThreshold
        sportThreshold = SandboxVars.OffroadGoBrrr.SportThreshold

        towingToggle = SandboxVars.OffroadGoBrrr.TowingToggle

        local vehicle = player:getVehicle()
        if vehicle then
            offroadVehicle = false
            excludedVehicle = false

            --check if the vehicle is in the exception or exclusion list, this nice way to do that was suggested by Burryaga
            local vehicleName = vehicle:getScriptName()

            if customOffroad[vehicleName] then
                offroadVehicle = true
            end

            local exceptions = SandboxVars.OffroadGoBrrr.Offroad:split(" ")
            for index = 1, #exceptions do
                local exception = exceptions[index]
                if vehicleName == exception then
                    offroadVehicle = true
                    break
                end
            end

            local exclusions = SandboxVars.OffroadGoBrrr.None:split(" ")
            for index = 1, #exclusions do
                local exclusion = exclusions[index]
                if vehicleName == exclusion then
                    excludedVehicle = true
                    break
                end
            end

            local vehicleScript = vehicle:getScript()
            local mechanicType = vehicleScript:getMechanicType()
            scriptEngineForce = vehicleScript:getEngineForce() * 0.1
            --determine the vehicle type
            if offroadVehicle then
                vehicleType = "high"
            else
                if mechanicType == 2 then
                    if heavyThreshold > 0 then
                        if scriptEngineForce < heavyThreshold and not (string.find(string.lower(vehicleName), "van") and not string.find(string.lower(vehicleName), "pickupvan")) then
                            vehicleType = "high"
                        else
                            vehicleType = "normal"
                        end
                    else
                        vehicleType = "none"
                    end
                end
                if mechanicType == 1 then
                    if standardThreshold > 0 then
                        if scriptEngineForce < standardThreshold then
                            vehicleType = "normal"
                        else
                            vehicleType = "low"
                        end
                    else
                        vehicleType = "none"
                    end
                end
                if mechanicType == 3 then
                    if sportThreshold > 0 then
                        if scriptEngineForce < sportThreshold then
                            vehicleType = "none"
                        else
                            vehicleType = "low"
                        end
                    else
                        vehicleType = "none"
                    end
                end
            end

            --activate stuff
            if not excludedVehicle then
                Events.OnTick.Add(offroadGoBrrr)
            end
        end
    end
end

Events.OnEnterVehicle.Add(OnEnterVehicle)

local function OnExitVehicle(player)
    Events.OnTick.Remove(offroadGoBrrr)

    if SandboxVars.OffroadGoBrrr.Toggle then
        --restore the default values
        if vehicleId > 0 then
            local exitedVehicle = getVehicleById(vehicleId)
            exitedVehicle:setInitialMass(exitedVehicle:getScript():getMass())
            exitedVehicle:updateTotalMass()
            vehicleId = 0
        end
        if towedId > 0 then
            local exTowedVehicle = getVehicleById(towedId)
            exTowedVehicle:setInitialMass(exTowedVehicle:getScript():getMass())
            exTowedVehicle:updateTotalMass()
            towedId = 0
        end
    end
end

Events.OnExitVehicle.Add(OnExitVehicle)
