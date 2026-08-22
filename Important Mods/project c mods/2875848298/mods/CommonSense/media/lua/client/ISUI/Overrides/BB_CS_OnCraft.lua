-- ************************************************************************
-- **        ██████  ██████   █████  ██    ██ ███████ ███    ██          **
-- **        ██   ██ ██   ██ ██   ██ ██    ██ ██      ████   ██          **
-- **        ██████  ██████  ███████ ██    ██ █████   ██ ██  ██          **
-- **        ██   ██ ██   ██ ██   ██  ██  ██  ██      ██  ██ ██          **
-- **        ██████  ██   ██ ██   ██   ████   ███████ ██   ████          **
-- ************************************************************************
-- ** All rights reserved. This content is protected by © Copyright law. **
-- ************************************************************************

local onCraft = ISInventoryPaneContextMenu.OnCraft
local painDialogue = {
    "Ouch!",
    "Ow!",
    "Ahh!",
    "Oof!",
    "Agh!",
    "Ugh!",
    "Argh!",
}

local function searchForTinOpenerInBag(bagInv)
    local bagItems = bagInv:getItems()
    for x = 1, bagItems:size() do
        local bagItem = bagItems:get(x - 1)
        if not instanceof(bagItem, "InventoryContainer") then
            if bagItem:getType() == "TinOpener" then
                return true
            end
        end
    end

    return false
end

local function searchForTinOpener(playerObj)
    local inventory = playerObj:getInventory()
    if inventory then
        local invItems = inventory:getItems()
        for i = 1, invItems:size() do
            local invItem = invItems:get(i - 1)
            if not instanceof(invItem, "InventoryContainer") then
                if invItem:getType() == "TinOpener" then
                    return true
                end
            else
                local bagInv = invItem:getInventory()
                if bagInv then
                    if searchForTinOpenerInBag(bagInv) then
                        return true
                    end
                end
            end
        end
    end

    return false
end

ISInventoryPaneContextMenu.OnCraft = function(selectedItem, recipe, player, all)

    local recipeName = recipe:getOriginalname()
    if string.match(recipeName, "Open Canned")
    or recipeName == "Open Dog Food"
    or recipeName == "Open Condensed Milk" then

        local playerObj = getPlayer()
        if searchForTinOpener(playerObj) then
            onCraft(selectedItem, recipe, player, all)
            return
        end

        local failChance = SandboxVars.CommonSense.CanWoundChance
        if ZombRand(100) <= failChance then
            local randomIndex = ZombRand(#painDialogue) + 1
            playerObj:Say(painDialogue[randomIndex])

            local bodyDamage = playerObj:getBodyDamage()
            bodyDamage:ReduceGeneralHealth(8 * SandboxVars.CommonSense.CanWoundIntensity)
            playerObj:getEmitter():playSound("KitchenKnifeHit")

            local bodyParts = bodyDamage:getBodyParts()
            for i=0,BodyPartType.ToIndex(BodyPartType.MAX) - 1 do
                local bodyPart = bodyParts:get(i)
                if bodyPart:getType() == BodyPartType.Hand_R then
                    bodyPart:setBleedingTime(1)
                end
            end
        else
            onCraft(selectedItem, recipe, player, all)
        end
    else
        onCraft(selectedItem, recipe, player, all)
    end
end