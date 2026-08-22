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

local cologneItem = ScriptManager.instance:getItem("Base.Cologne")
if cologneItem then
    cologneItem:getTags():add("Disinfectant")
    cologneItem:DoParam("Type = Drainable")
    cologneItem:DoParam("AlcoholPower = 3")
    cologneItem:DoParam("UseDelta = 0.5")
end

local bathTowelItem = ScriptManager.instance:getItem("Base.BathTowel")
if bathTowelItem then
    bathTowelItem:DoParam("UseDelta = 0.03")
end

local dishClothItem = ScriptManager.instance:getItem("Base.DishCloth")
if dishClothItem then
    dishClothItem:DoParam("UseDelta = 0.05")
end

local exceptionIDs = { "shine_together", "noirrsling", "nattachments" }
local handTorchItem = ScriptManager.instance:getItem("Base.HandTorch")
if handTorchItem then
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
        handTorchItem:DoParam("AttachmentType = Screwdriver")
    end
end

local pie1Item = ScriptManager.instance:getItem("Base.Pie")
if pie1Item then
    pie1Item:DoParam("UnhappyChange = -5")
end

local pie2Item = ScriptManager.instance:getItem("Base.PiePumpkin")
if pie2Item then
    pie2Item:DoParam("UnhappyChange = -5")
end

local pie3Item = ScriptManager.instance:getItem("Base.PieApple")
if pie3Item then
    pie3Item:DoParam("UnhappyChange = -5")
end

local pie4Item = ScriptManager.instance:getItem("Base.PieBlueberry")
if pie4Item then
    pie4Item:DoParam("UnhappyChange = -5")
end

local pie5Item = ScriptManager.instance:getItem("Base.PieKeyLime")
if pie5Item then
    pie5Item:DoParam("UnhappyChange = -5")
end

local pie6Item = ScriptManager.instance:getItem("Base.PieLemonMeringue")
if pie6Item then
    pie6Item:DoParam("UnhappyChange = -5")
end

local recipes = getScriptManager():getAllRecipes()
for i = 0, recipes:size() - 1 do
    local recipe = recipes:get(i)
    local recipeName = recipe:getOriginalname()
    if string.match(recipeName, "Open Jar of ") then
        recipe:setLuaCreate("Recipe.OnCreate.OpenJarredFood")
    end

    if string.match(recipeName, "Open Canned")
    or recipeName == "Open Dog Food"
    or recipeName == "Open Condensed Milk" then
        recipe:setLuaTest("Recipe.OnTest.HasCanOpener")
    end
end