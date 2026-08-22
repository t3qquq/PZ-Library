-- ************************************************************************
-- **        ██████  ██████   █████  ██    ██ ███████ ███    ██          **
-- **        ██   ██ ██   ██ ██   ██ ██    ██ ██      ████   ██          **
-- **        ██████  ██████  ███████ ██    ██ █████   ██ ██  ██          **
-- **        ██   ██ ██   ██ ██   ██  ██  ██  ██      ██  ██ ██          **
-- **        ██████  ██   ██ ██   ██   ████   ███████ ██   ████          **
-- ************************************************************************
-- ** All rights reserved. This content is protected by © Copyright law. **
-- ************************************************************************

require "recipecode"

local bathTowelItem = ScriptManager.instance:getItem("Base.BathTowel")
if bathTowelItem then
    bathTowelItem:DoParam("UseDelta = 0.03")
end

local dishClothItem = ScriptManager.instance:getItem("Base.DishCloth")
if dishClothItem then
    dishClothItem:DoParam("UseDelta = 0.05")
end

local exceptionIDs = { "shine_together", "noirrsling", "nattachments" }
local flashlightList = {
    ScriptManager.instance:getItem("Base.HandTorch"),
    ScriptManager.instance:getItem("Base.Flashlight_Crafted")
}

if #flashlightList > 0 then

    local activatedMods = getActivatedMods()
    local compatify = false
    if activatedMods then
        for _, modID in ipairs(exceptionIDs) do
            if activatedMods:contains(modID) then
                compatify = true
                break
            end
        end
    end

    if not compatify then
        for _, flashlight in ipairs(flashlightList) do
            flashlight:DoParam("AttachmentType = Screwdriver")
        end
    end
end