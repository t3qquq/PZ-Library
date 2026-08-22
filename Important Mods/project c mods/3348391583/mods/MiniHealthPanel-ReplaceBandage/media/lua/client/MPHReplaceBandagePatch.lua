require "MiniHealth/MiniHealthTreatments"
require "ReplaceBandage/BaseHandler"
require "ReplaceBandage/BaseHealthActions"
require "ReplaceBandage/CustomHealthActions"

-- Override the original function
local original_doBodyPartContextMenu = MiniHealthTreatment.doBodyPartContextMenu

function MiniHealthTreatment:doBodyPartContextMenu(bodyPart, context)
    -- Call the original function first
    original_doBodyPartContextMenu(self, bodyPart, context)

    -- Add ReplaceBandage handlers
    local replaceBandageHandlers =
        {HReplaceBandage:new(self, bodyPart), HReplaceAndDisinfectBandage:new(self, bodyPart)}

    self:checkItems(replaceBandageHandlers)

    for _, handler in ipairs(replaceBandageHandlers) do
        handler:addToMenu(context)
    end
end

-- Add this function to support the ReplaceBandage handlers
function MiniHealthTreatment:getPatient()
    return mhpHandle.player
end

-- Patch the checkItems function to work with ReplaceBandage handlers
local original_checkItems = MiniHealthTreatment.checkItems

function MiniHealthTreatment:checkItems(handlers)
    original_checkItems(self, handlers)

    -- Additional check for ReplaceBandage items
    local containers = ISInventoryPaneContextMenu.getContainers(mhpHandle.player)
    for i = 1, containers:size() do
        local container = containers:get(i - 1)
        local items = container:getItems()
        for j = 1, items:size() do
            local item = items:get(j - 1)
            for _, handler in ipairs(handlers) do
                if handler.checkItem then
                    handler:checkItem(item)
                end
            end
        end
    end
end

-- Patch HReplaceBandage to work with MiniHealthPanel
local original_HReplaceBandage_new = HReplaceBandage.new
function HReplaceBandage:new(panel, bodyPart)
    local o = original_HReplaceBandage_new(self, panel, bodyPart)
    o.panel = {
        character = mhpHandle.player,
        otherPlayer = mhpHandle.player
    }
    return o
end

-- Patch HReplaceAndDisinfectBandage to work with MiniHealthPanel
local original_HReplaceAndDisinfectBandage_new = HReplaceAndDisinfectBandage.new
function HReplaceAndDisinfectBandage:new(panel, bodyPart)
    local o = original_HReplaceAndDisinfectBandage_new(self, panel, bodyPart)
    o.panel = {
        character = mhpHandle.player,
        otherPlayer = mhpHandle.player
    }
    return o
end
