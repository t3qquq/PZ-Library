-- ************************************************************************
-- **        ██████  ██████   █████  ██    ██ ███████ ███    ██          **
-- **        ██   ██ ██   ██ ██   ██ ██    ██ ██      ████   ██          **
-- **        ██████  ██████  ███████ ██    ██ █████   ██ ██  ██          **
-- **        ██   ██ ██   ██ ██   ██  ██  ██  ██      ██  ██ ██          **
-- **        ██████  ██   ██ ██   ██   ████   ███████ ██   ████          **
-- ************************************************************************
-- ** All rights reserved. This content is protected by © Copyright law. **
-- ************************************************************************

CSUtils = {}
CSUtils.predicateNotBroken = function(item)
	return not item:isBroken()
end

CSUtils.playerHasPryingTool = function(playerObj, itemID, override)

    if override then return true end
    if not playerObj then return false end
    if not itemID then return false end

    local playerInv = playerObj:getInventory()
    local pryingTool = playerInv:getFirstTypeEvalRecurse(itemID, CSUtils.predicateNotBroken)


    if not pryingTool then return false end
end

-- This function is a shameful, steaming pile of pasta. I blame TIS for the noodles and LUA for the sauce.
CSUtils.Loop = function(worldobjects, playerObj, priableObject, actionType, toolID, toolContainer)
    if not playerObj:hasEquipped(toolID) then
        BB_CS_Utils.DelayFunction(function()
            if not playerObj:hasEquipped(toolID) then
                CSUtils.Loop(worldobjects, playerObj, priableObject, actionType, toolID, toolContainer)
            else
                if(actionType == "PryDoor") then
                    luautils.walkAdjWindowOrDoor(playerObj, priableObject:getSquare(), priableObject)
                    ISTimedActionQueue.add(CSISTimedAction:PryDoor(worldobjects, priableObject, playerObj, toolContainer, 190))
                elseif(actionType == "PryVehicleDoor") then
                    ISTimedActionQueue.add(CSISTimedAction:PryVehicleDoor(worldobjects, priableObject, playerObj, toolContainer, 190))
                end
            end
        end, 10, true)
    end
end

CSUtils.PryEntityOpen = function(worldobjects, priableObject, playerObj, pryingTool)

    local toolID = pryingTool:getFullType()
    local toolContainer = nil

    ISInventoryPaneContextMenu.transferIfNeeded(playerObj, pryingTool)

    if not playerObj:hasEquipped(toolID) then
        ISInventoryPaneContextMenu.equipWeapon(pryingTool, true, true, playerObj:getPlayerNum())
        toolContainer = pryingTool:getContainer()
    end

    if not playerObj:hasEquipped(toolID) then
        CSUtils.Loop(worldobjects, playerObj, priableObject, "PryDoor", toolID, toolContainer)
    else
        luautils.walkAdjWindowOrDoor(playerObj, priableObject:getSquare(), priableObject)
        ISTimedActionQueue.add(CSISTimedAction:PryDoor(worldobjects, priableObject, playerObj, toolContainer, 190))
    end
end

CSUtils.PryVehicleOpen = function(vehicle, vehiclePart, playerObj, pryingTool)

    local toolID = pryingTool:getFullType()
    local toolContainer = nil
    ISInventoryPaneContextMenu.transferIfNeeded(playerObj, pryingTool)

    if not playerObj:hasEquipped(toolID) then
        ISInventoryPaneContextMenu.equipWeapon(pryingTool, true, true, playerObj:getPlayerNum())
        toolContainer = pryingTool:getContainer()
    end

    if not playerObj:hasEquipped(toolID) then
        CSUtils.Loop(vehicle, playerObj, vehiclePart, "PryVehicleDoor", toolID, toolContainer)
    else
        ISTimedActionQueue.add(CSISTimedAction:PryVehicleDoor(vehicle, vehiclePart, playerObj, toolContainer, 190))
    end
end

CSUtils.PrySuccessfully = function(playerObj, failBoost)

    if playerObj:HasTrait("Burglar") then

        if ZombRand(10) > 1 then
            return true
        else
            return false
        end
    end

    local strengthLevel = playerObj:getPerkLevel(Perks.Strength)
    local succeedChance = ZombRand(100)
    local failChance = (180 / strengthLevel) + failBoost

    failChance = failChance * SandboxVars.CommonSense.PryingChanceMultiplier

    if succeedChance > failChance then
        return true
    else
        return false
    end
end

--- Pry open Vehicle doors and trunks using a Weapon.
--- <br> Call this function when Player is opening the Radial Menu outside a Vehicle.
---@param playerObj IsoPlayer
---@param crowbarID string
CSUtils.showRadialMenuOutsideCrowbar = function(playerObj)

    if not SandboxVars.CommonSense.PryingMechanic then return end
    if not SandboxVars.CommonSense.PryVehicleDoors then return end

	local vehicle = playerObj:getNearVehicle() if not vehicle then return end
	local menu = getPlayerRadialMenu(playerObj:getPlayerNum())
    local playerInv = playerObj:getInventory()
    local pryingTool = nil

	for _, v in pairs(CommonSense.PryingTools) do

        pryingTool = playerInv:getFirstTypeEvalRecurse(v, CSUtils.predicateNotBroken)

        if pryingTool then
            break
        end
  	end

    if not pryingTool then return end

    local doorPart = vehicle:getUseablePart(playerObj)

    if doorPart and doorPart:getDoor() and doorPart:getInventoryItem() then

        if not doorPart:getDoor():isLocked() then return end

        local isHood = doorPart:getId() == "EngineDoor"

        if not (isHood) then
            menu:addSlice(getText("ContextMenu_Pry_open"), getTexture("media/ui/vehicles/PryOpen.png"), CSUtils.PryVehicleOpen, vehicle, doorPart, playerObj, pryingTool)
        end

    end
end

CSUtils.OnFillWorldObjectContextMenuCrowbar = function(player, context, worldobjects, test)
    if not SandboxVars.CommonSense.PryingMechanic then return end

    local playerObj = getSpecificPlayer(player)
    if playerObj:getVehicle() then return end

    local playerInv = playerObj:getInventory()
    local pryingTool = nil

	for _, v in pairs(CommonSense.PryingTools) do
        pryingTool = playerInv:getFirstTypeEvalRecurse(v, CSUtils.predicateNotBroken)
        if pryingTool then
            break
        end
  	end

    if not pryingTool then return end

    local priableObject = nil

    for _,v in ipairs(worldobjects) do
        if ISWorldObjectContextMenu.isThumpDoor(v) == true then
            priableObject = v
            break
        end
    end

    -- Look for objects around player in case they are trying to pry open something not visible at the square click.
    -- THANKS B42 =3
    local fetch = ISWorldObjectContextMenu.fetchVars
    if fetch and fetch.clickedSquare then
        local clickedSqX = fetch.clickedSquare:getX()
        local clickedSqY = fetch.clickedSquare:getY()
        local clickedSqZ = fetch.clickedSquare:getZ()

        if not priableObject then
            for dx = -1, 1 do
                if priableObject then break end
                for dy = -1, 1 do
                    local sq = getCell():getGridSquare(clickedSqX + dx, clickedSqY + dy, clickedSqZ)
                    if sq then
                        local objs = sq:getObjects()
                        for i = 0, objs:size() - 1 do
                            local obj = objs:get(i)
                            if ISWorldObjectContextMenu.isThumpDoor(obj) == true then
                                priableObject = obj
                                break
                            end
                        end
                    end
                end
            end
        end
    end

	if priableObject ~= nil then

        local isReinforcedDoor = false
        local canPryOpenReinforcedDoors = false
        if SandboxVars.CommonSense.PrySafeDoors then canPryOpenReinforcedDoors = true end

        if canPryOpenReinforcedDoors then
            local strengthLevel = playerObj:getPerkLevel(Perks.Strength)
            if strengthLevel < SandboxVars.CommonSense.ReinforcedDoorLevel then
                canPryOpenReinforcedDoors = false
            end
        end

        local sprite = priableObject:getSprite()
        if sprite then
            local spriteName = sprite:getName() or ""
            if spriteName == "fixtures_doors_01_32" or
            spriteName == "fixtures_doors_01_33" or
            spriteName == "location_community_police_01_4" or
            spriteName == "location_community_police_01_5" then
                isReinforcedDoor = true
            end
        end

		if instanceof(priableObject, "IsoDoor")
        and priableObject:isLocked() == true
        and priableObject:isBarricaded() == false then

            local isGarage = false
            local garageDoorObjects = buildUtil.getGarageDoorObjects(priableObject)
            if garageDoorObjects then
                for _ = 1, #garageDoorObjects do
                    if not SandboxVars.CommonSense.PryGarageDoors then return end
                    isGarage = true
                    break
                end
            end

            if isReinforcedDoor and not SandboxVars.CommonSense.PrySafeDoors then return end
            if not (SandboxVars.CommonSense.PryBuildingDoors or isGarage or (isReinforcedDoor and SandboxVars.CommonSense.PrySafeDoors)) then return end

            local option = context:addOptionOnTop(getText("ContextMenu_Pry_open"), worldobjects, CSUtils.PryEntityOpen, priableObject, playerObj, pryingTool)
            local description = getText("Tooltip_CS_PryOpenDoor")

            if isReinforcedDoor and not canPryOpenReinforcedDoors then
                description = string.format(getText("Tooltip_CS_CantPryOpenRDoor"), SandboxVars.CommonSense.ReinforcedDoorLevel)
                option.notAvailable = true
            end

            BB_CS_Utils.addTooltip(description, option)
            return
		end

		if instanceof(priableObject, "IsoWindow")
        and not priableObject:IsOpen()
        and SandboxVars.CommonSense.PryWindows then
            local option = context:addOptionOnTop(getText("ContextMenu_Pry_open"), worldobjects, CSUtils.PryEntityOpen, priableObject, playerObj, pryingTool)
            local description = getText("Tooltip_CS_PryOpenWindow")

            if priableObject:IsOpen() or priableObject:isPermaLocked() or priableObject:isBarricaded() or priableObject:isSmashed() then
                description = getText("Tooltip_CS_CantPryOpenWindow")
                option.notAvailable = true
            end

            BB_CS_Utils.addTooltip(description, option)
            return
		end
	end
end

