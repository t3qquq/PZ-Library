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

BB_Collect_TA = ISBaseTimedAction:derive("BB_Collect_TA")

local function splitString(inputstr, sep)
    local t = {}
    for str in string.gmatch(inputstr, "([^" .. sep .. "]+)") do
        table.insert(t, str)
    end
    return t
end

local function playSound(playerObj, spriteName)
    if string.find(spriteName, "trash") then
        BB_CS_Utils.TryPlaySoundClip(playerObj, "FoleyTrash")
    else
        BB_CS_Utils.TryPlaySoundClip(playerObj, "FoleyForage")
    end
end

local function stopSound(playerObj, spriteName)
    if string.find(spriteName, "trash") then
        BB_CS_Utils.TryStopSoundClip(playerObj, "FoleyTrash")
    else
        BB_CS_Utils.TryStopSoundClip(playerObj, "FoleyForage")
    end
end

BB_Collect_TA.isValid = function(self)
    return true
end

BB_Collect_TA.start = function(self)
    if self.typeTimeAction == "Collect" then
        self:setActionAnim("Loot")
        self:setAnimVariable("LootPosition", "Low")
        playSound(self.playerObj, self.spriteName)
    end
end

BB_Collect_TA.stop = function(self)
    ISBaseTimedAction.stop(self)
    stopSound(self.playerObj, self.spriteName)
end

BB_Collect_TA.perform = function(self)

    if self.typeTimeAction == "Collect" then

        if not SandboxVars.CommonSense.DisableLoot then

            local playerInv = self.playerObj:getInventory()
            local items = splitString(self.itemsList, ",")
            for _, item in ipairs(items) do

                local itemParts = splitString(item, ":")

                if #itemParts == 2 then
                    local itemName = itemParts[1]
                    local itemValue = math.floor(tonumber(itemParts[2]) * SandboxVars.CommonSense.LootMultiplier)
                    for i = 1, itemValue do
                        playerInv:AddItem(itemName)
                    end
                else
                    print("Invalid item format:", item)
                end
            end
        end

        local args = {
            square = { x = self.clickedSquare:getX(), y = self.clickedSquare:getY(), z = self.clickedSquare:getZ() },
            spriteName = self.spriteName
        }

        sendClientCommand(self.character, 'CommonSense', 'RemoveResources', args)
        stopSound(self.playerObj, self.spriteName)
    end

    ISBaseTimedAction.perform(self)
end

BB_Collect_TA.CollectItem = function(self, playerObj, clickedSquare, spriteName, itemsList)

    local action = ISBaseTimedAction.new(self, playerObj)
    action.typeTimeAction = "Collect"
    action.playerObj = playerObj
    action.clickedSquare = clickedSquare
    action.spriteName = spriteName
    action.itemsList = itemsList
    action.stopOnWalk = true
    action.stopOnRun = true
    action.maxTime = 100
    action.fromHotbar = false

    if action.character:isTimedActionInstant() then action.maxTime = 1; end
    return action
end