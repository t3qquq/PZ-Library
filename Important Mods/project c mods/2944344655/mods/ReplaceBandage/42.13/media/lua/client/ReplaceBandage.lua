---------------------------------
---     Coded by Sioyth       ---
--- https://github.com/Sioyth ---
---------------------------------

ReplaceBandage = {}

local CustomHealthActions = require("CustomHealthActions")

-- Move to util
function ReplaceBandage:GetBandagedParts(bodyParts)
    local result = {}
    for i=0,BodyPartType.ToIndex(BodyPartType.MAX) - 1 do
        local bodyPart = bodyParts:get(i);
        if bodyPart:bandaged() then
            table.insert(result, bodyPart);
        end
    end
    return result
end

function ReplaceBandage:GetBestDisinfectant(items)
    local result = nil
    local highestAlcoholPower = -1

    for k,v in ipairs(items) do
        local itemAlcoholPower = v:getAlcoholPower()
        if itemAlcoholPower > highestAlcoholPower then
            highestAlcoholPower = v:getAlcoholPower()
            result = v
        end
    end

    DebugLog.log("Highest alcohol power: " .. result:getDisplayName())
    return result
end

function ReplaceBandage:createAndAddAction(handler, actionClass, bodyPart, context, player)
    local action = actionClass:new(handler, bodyPart, player or nil)
    action:checkItems()
    action:addToMenu(context)
    return action
end

ReplaceBandage.OnReplaceBandage = function(bandages, bodyPart, player)
    bandages = ISInventoryPane.getActualItems(bandages)
    for i,k in ipairs(bandages) do
        local playerObj = getSpecificPlayer(player)
        ISTimedActionQueue.add(ISApplyBandage:new(playerObj, playerObj, nil, bodyPart, false));

        -- if bandage isn't in main inventory, put it there first.
        ISInventoryPaneContextMenu.transferIfNeeded(playerObj, k)
        -- apply bandage
        ISTimedActionQueue.add(ISApplyBandage:new(playerObj, playerObj, k, bodyPart, true));
        break
    end
end

--- Inventory Panel ---
function ISInventoryPaneContextMenu.doBandageMenu(context, items, player)

    --- Default PZ Method ---
    -- we get all the damaged body part
    local bodyPartDamaged = ISInventoryPaneContextMenu.haveDamagePart(player);
    -- if any part is damaged, we gonna create a sub menu with them
    if #bodyPartDamaged > 0 then
        local bandageOption = context:addOption(getText("ContextMenu_Apply_Bandage"), bodyPartDamaged, nil);
        -- create a new contextual menu
        local subMenuBandage = context:getNew(context);
        -- we add our new menu to the option we want (here bandage)
        context:addSubMenu(bandageOption, subMenuBandage);
        for i,v in ipairs(bodyPartDamaged) do
            subMenuBandage:addOption(BodyPartType.getDisplayName(v:getType()), items, ISInventoryPaneContextMenu.onApplyBandage, v, player);
        end
    end

    --- Custom Replace Bandage ---
    local bandagedParts = ReplaceBandage:GetBandagedParts(getSpecificPlayer(player):getBodyDamage():getBodyParts())
    if #bandagedParts > 0 then
        local option = context:addOption(getText("IGUI_Replace_Bandage"), nil)
        local subMenuBodyPart = context:getNew(context)
        context:addSubMenu(option, subMenuBodyPart)

        for i,v in ipairs(bandagedParts) do
            subMenuBodyPart:addOption(BodyPartType.getDisplayName(v:getType()), items, ReplaceBandage.OnReplaceBandage, v, player);
        end
    end
end

local original_doMenu = ISHealthPanel.doBodyPartContextMenu
local original_get = ISContextMenu.get

--- Health Panel ---
function ISHealthPanel:doBodyPartContextMenu(bodyPart, x, y)
    local capturedContext = nil

    -- TEMP hook ISContextMenu.get, this way we can get the BodyPartContextMenu
    ISContextMenu.get = function(...)
        local ctx = original_get(...)
        capturedContext = ctx
        return ctx
    end

    original_doMenu (self,bodyPart, x, y)
    ISContextMenu.get = original_get

    if not capturedContext then
        return
    end

    local replaceBandage = CustomHealthActions.HReplaceBandage:new(self, bodyPart)
    replaceBandage:checkItems()
    replaceBandage:addToMenu(capturedContext)

    local replaceAndDisinfect = CustomHealthActions.HReplaceAndDisinfectBandage:new(self, bodyPart)
    replaceAndDisinfect:checkItems()
    replaceAndDisinfect:addToMenu(capturedContext)
end