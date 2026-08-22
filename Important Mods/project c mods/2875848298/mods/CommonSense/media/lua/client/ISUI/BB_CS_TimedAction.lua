-- ************************************************************************
-- **        ██████  ██████   █████  ██    ██ ███████ ███    ██          **
-- **        ██   ██ ██   ██ ██   ██ ██    ██ ██      ████   ██          **
-- **        ██████  ██████  ███████ ██    ██ █████   ██ ██  ██          **
-- **        ██   ██ ██   ██ ██   ██  ██  ██  ██      ██  ██ ██          **
-- **        ██████  ██   ██ ██   ██   ████   ███████ ██   ████          **
-- ************************************************************************
-- ** All rights reserved. This content is protected by © Copyright law. **
-- ************************************************************************

require "TimedActions/ISBaseTimedAction"
CSISTimedAction = ISBaseTimedAction:derive("CSISTimedAction")

CSISTimedAction.TryReturnTool = function (self)
    if self.container == nil then return end
    local tool = self.character:getPrimaryHandItem(); if not tool then return end
    local currentToolContainer = tool:getContainer(); if not currentToolContainer then return end
    if self.container ~= currentToolContainer then
        ISTimedActionQueue.add(ISInventoryTransferAction:new(self.character, tool, currentToolContainer, self.container))
    elseif tool:isEquipped() then
        ISTimedActionQueue.add(ISUnequipAction:new(self.character, tool, 50))
    end
end

CSISTimedAction.isValid = function(self)
    return true
end

CSISTimedAction.start = function(self)

    if self.typeTimeAction == "pryDoor" then

        self:setActionAnim("RemoveBarricade")
        self:setAnimVariable("RemoveBarricade", "CrowbarMid")
        if self.character:isTimedActionInstant() then return end

        BB_CS_Utils.DelayFunction(function()
            BB_CS_Utils.TryPlaySoundClip(self.character, BB_CS_Utils.GetProperSound(self.priableObject, false))
        end, 35, true)
    end

    if self.typeTimeAction == "pryVehicleDoor" then

        self:setActionAnim("RemoveBarricade")
        self:setAnimVariable("RemoveBarricade", "CrowbarMid")
        if self.character:isTimedActionInstant() then return end

        BB_CS_Utils.DelayFunction(function()
            BB_CS_Utils.TryPlaySoundClip(self.character, "MetalBarHit")
        end, 35, true)
    end
end

CSISTimedAction.stop = function(self)
    ISBaseTimedAction.stop(self)

    if self.typeTimeAction == "pryDoor" then
        BB_CS_Utils.TryStopSoundClip(self.character, BB_CS_Utils.GetProperSound(self.priableObject, false))
    end

    if self.typeTimeAction == "pryVehicleDoor" then
        BB_CS_Utils.TryStopSoundClip(self.character, "MetalBarHit")
    end
end

CSISTimedAction.perform = function(self)

    if self.typeTimeAction == "pryDoor" then

        BB_CS_Utils.TirePlayer(self.character, 0.07)
        BB_CS_Utils.TryStopSoundClip(self.character, BB_CS_Utils.GetProperSound(self.priableObject, false))

        if CSUtils.PrySuccessfully(self.character, 0) == true then
            CSServer.PryDoorOpen(self.worldObjects, self.priableObject, self.character)
            CSISTimedAction.TryReturnTool(self)
        else
            self.character:Say(getText("IGUI_Pry_fail"))
            self.character:getXp():AddXP(Perks.Strength, 4)

            if BB_CS_Utils.GetProperSound(self.priableObject, true) == "Wooden" then
                BB_CS_Utils.TryPlaySoundClip(self.character, "BreakLockOnWindow")
            else
                BB_CS_Utils.TryPlaySoundClip(self.character, "MetalBarBreak")
            end
        end
    end

    if self.typeTimeAction == "pryVehicleDoor" then

        BB_CS_Utils.TirePlayer(self.character, 0.1)
        BB_CS_Utils.TryStopSoundClip(self.character, "MetalBarHit")

        if CSUtils.PrySuccessfully(self.character, 20) == true then

            local args = { vehicle = self.vehicleID, part = self.priableObjectID, locked = false, open = true }
            sendClientCommand(self.character, 'vehicle', 'setDoorLocked', args)
	        sendClientCommand(self.character, 'vehicle', 'setDoorOpen', args)

            local isTrunk = self.priableObjectID == "TrunkDoor" or self.priableObjectID == "DoorRear"
            if not (isTrunk) then
                BB_CS_Utils.TryPlaySoundClip(self.character, "VehicleDoorOpen")
            else
                BB_CS_Utils.TryPlaySoundClip(self.character, "VehicleTrunkOpen")
            end

            self.character:getXp():AddXP(Perks.Strength, 10)
            CSISTimedAction.TryReturnTool(self)
        else
            self.character:Say(getText("IGUI_Pry_fail"))
            BB_CS_Utils.TryPlaySoundClip(self.character, "MetalBarBreak")
            self.character:getXp():AddXP(Perks.Strength, 3)

            if SandboxVars.CommonSense.ShatterVehicleWindows and ZombRand(100) <= SandboxVars.CommonSense.WindowShatterChance then
                local part = self.vehicle:getClosestWindow(self.character)
                if part then
                    local window = part:getWindow()
                    if window and not window:isOpen() then
                        if not window:isDestroyed() then
                            window:damage(window:getHealth())
                        end
                    end
                end
            end
        end
    end

    ISBaseTimedAction.perform(self)
end

CSISTimedAction.PryDoor = function(self, worldObjects, priableObject, character, container, time)

    local action = ISBaseTimedAction.new(self, character)
    action.typeTimeAction = "pryDoor"
    action.worldObjects = worldObjects
    action.priableObject = priableObject
    action.character = character
    action.container = container
    action.stopOnWalk = true
    action.stopOnRun = true
    action.maxTime = time
    action.fromHotbar = false

    if action.character:isTimedActionInstant() then action.maxTime = 1 end
    return action
end

CSISTimedAction.PryVehicleDoor = function(self, vehicle, priableObject, character, container, time)

    local action = ISBaseTimedAction.new(self, character)
    action.typeTimeAction = "pryVehicleDoor"
    action.vehicle = vehicle
    action.vehicleID = vehicle:getId()
    action.priableObjectID = priableObject:getId()
    action.character = character
    action.container = container
    action.stopOnWalk = true
    action.stopOnRun = true
    action.maxTime = time
    action.fromHotbar = false

    if action.character:isTimedActionInstant() then action.maxTime = 1 end
    return action
end

return TimeAction