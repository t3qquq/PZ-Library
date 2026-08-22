local consolidateAction = require("esim.action"):new()

local function itemDestroy(item, player)
    if item:getWorldItem() ~= nil then
        item:getWorldItem():getSquare():transmitRemoveItemFromSquare(item:getWorldItem())
        item:getWorldItem():removeFromWorld()
        item:getWorldItem():removeFromSquare()
        item:getWorldItem():setSquare(nil)
        getPlayerLoot(player:getPlayerNum()):refreshBackpacks()
        return
    end

    item:getContainer():Remove(item)
end

local function itemAdd(item, container, player)
    if container:getType() == "floor" then
        player:getCurrentSquare():AddWorldInventoryItem(item, 0, 0, 0)
    end
    container:AddItem(item)
end

local function itemGetDelta(itemStack)
    local totalDelta = 0
    for _, item in pairs(itemStack) do
        totalDelta = totalDelta + item:getUsedDelta()
    end
    return totalDelta
end

local function tableSizeOf(array)
    if not array then return 0 end
    local nCount = 0
    for v, k in pairs(array) do
        nCount = nCount + 1
    end
    return nCount
end

local function itemFindItemsByType(type, player)
    local itemsTable = {}
    local items = getPlayerInventory(player).inventory:getAllTypeRecurse(type)
    for i = 0, items:size() - 1 do
        table.insert(itemsTable, items:get(i))
    end
    local lootItems = getPlayerLoot(player).inventory:getAllTypeRecurse(type)
    for i = 0, lootItems:size() - 1 do
        table.insert(itemsTable, lootItems:get(i))
    end
    return itemsTable
end

local function itemGetFromSelection(selection)
    local newItemStack = {}
    for v, k in pairs(selection) do
        if instanceof(k, "InventoryItem") then
            table.insert(newItemStack, k)
        else
            for x = 2, #k.items do
                if instanceof(k.items[x], "InventoryItem") then
                    table.insert(newItemStack, k.items[x])
                end
            end
        end
    end
    return newItemStack
end

local function itemMove(stack, container, char)
    if (stack.size) then
        for i = 0, stack:size() - 1 do
            ISTimedActionQueue.add(ISInventoryTransferAction:new(char, stack:get(i), stack:get(i):getContainer(), container))
        end
    elseif type(stack) == "table" then
        for _, item in pairs(stack) do
            ISTimedActionQueue.add(ISInventoryTransferAction:new(char, item, item:getContainer(), container))
        end
    else
        ISTimedActionQueue.add(ISInventoryTransferAction:new(char, stack, stack:getContainer(), container))
    end
end

local function filterDrainable(itemStack)
    local selectedItems = {}
    local selectedItem
    for _, item in pairs(itemStack) do
        if item:IsDrainable() and item:getUseDelta() > 0 then
            if selectedItem == nil then
                selectedItem = item
                table.insert(selectedItems, item)
            elseif selectedItem:getFullType() == item:getFullType() and not item:isFavorite() then
                table.insert(selectedItems, item)
            end
        end
    end
    return selectedItems, selectedItem
end

local function filterSpices(itemStack)
    local spices = {}
    local selectedItem
    for _, testItem in pairs(itemStack) do
        if instanceof(testItem, "Food") and testItem:isSpice() and not testItem:isFavorite() and testItem:getScriptItem():getDaysFresh() > 3000 then
            if (selectedItem == nil) then
                selectedItem = testItem
                table.insert(spices, testItem)
            elseif (selectedItem:getName() == testItem:getName()) then
                table.insert(spices, testItem)
            end
        end
    end
    return spices, selectedItem
end

function consolidateAction:doPerform()
    if instanceof(self.item, "Food") and self.item:isSpice() then
        local ogItem = InventoryItemFactory.CreateItem(self.item:getFullType())
        local ogHunger = math.abs(ogItem:getHungerChange())
        local stackHunger = 0
        local destroyItemsList = {}
        for _, item in pairs(self.extra[1]) do
            stackHunger = stackHunger + math.abs(item:getHungerChange());
            table.insert(destroyItemsList, item);
        end

        local fullHunger = math.floor(stackHunger / ogHunger)
        local partHunger = round(stackHunger % ogHunger, 2)
        if fullHunger > 0 then
            for i = 1, fullHunger do
                local item = InventoryItemFactory.CreateItem(self.item:getFullType())
                itemAdd(item, self.extra[2], self.character)
            end
        end

        if partHunger > 0 then
            local itemPart = InventoryItemFactory.CreateItem(self.item:getFullType())
            local partial = partHunger / ogHunger
            itemPart:setBaseHunger(itemPart:getBaseHunger() * partial)
            itemPart:setHungChange(itemPart:getHungChange() * partial)
            itemPart:setThirstChange(itemPart:getThirstChangeUnmodified() * partial)
            itemPart:setBoredomChange(itemPart:getBoredomChangeUnmodified() * partial)
            itemPart:setUnhappyChange(itemPart:getUnhappyChangeUnmodified() * partial)
            itemPart:setCarbohydrates(itemPart:getCarbohydrates() * partial)
            itemPart:setLipids(itemPart:getLipids() * partial)
            itemPart:setProteins(itemPart:getProteins() * partial)
            itemPart:setCalories(itemPart:getCalories() * partial)

            itemAdd(itemPart, self.extra[2], self.character)
        end

        for _, item in pairs(destroyItemsList) do
            itemDestroy(item, self.character)
        end

    else
        local totalStackUses = itemGetDelta(self.extra[1])

        for _, item in pairs(self.extra[1]) do
            if (totalStackUses >= 1) then
                item:setUsedDelta(1)
                totalStackUses = totalStackUses - 1
            elseif (totalStackUses > 0) then
                item:setUsedDelta(totalStackUses)
                totalStackUses = 0
            else
                item:setUsedDelta(0)
                if not self.extra[3] then item:Use() end
            end
        end
    end

    if (isClient()) then
        local merged = self.character:getInventory():FindAll(self.item:getFullType())
        itemMove(merged, self.extra[2], self.character)
    end
end

local function doMenuMerge(selectedItem, itemStack, player, keepEmpty)
    local animate = consolidateAction:new(player, selectedItem, 80)
    animate:setExtra({ itemStack, selectedItem:getContainer(), keepEmpty })

    if (isClient()) then
        itemMove(itemStack, player:getInventory(), player)
    else
        itemMove(itemStack, selectedItem:getContainer(), player)
    end

    ISTimedActionQueue.add(animate)
end

local function doMenuFill(selectedItem, itemStack, player, keepEmpty)
    local animate = consolidateAction:new(player, selectedItem, 80)
    local usesPerItem = round(1 / selectedItem:getUseDelta())
    local fillUses = usesPerItem - selectedItem:getDrainableUsesInt()
    local newStack = {}

    table.insert(newStack, selectedItem)
    for _, item in pairs(itemStack) do
        if (selectedItem ~= item) then
            fillUses = fillUses - item:getDrainableUsesInt()
            table.insert(newStack, item)
            if (fillUses <= 0) then break end
        end
    end

    animate:setExtra({ newStack, selectedItem:getContainer(), keepEmpty })
    if (isClient()) then
        itemMove(newStack, player:getInventory(), player)
    else
        itemMove(newStack, selectedItem:getContainer(), player)
    end
    ISTimedActionQueue.add(animate)
end

local function toggleView(player, set)
    if set then
        player:getModData()["esim.itemMergeView"] = not player:getModData()["esim.itemMergeView"]
        return
    end
    return player:getModData()["esim.itemMergeView"] or false
end

local function doMenu(player, context, items)
    local thisStack, selectedItem = filterDrainable(itemGetFromSelection(items))
    if selectedItem then
        local mergeSubMenu = context:getNew(context)
        local playerObj = getSpecificPlayer(player)
        local allItems = itemFindItemsByType(selectedItem:getFullType(), player)
        local filtered = filterDrainable(allItems)

        if (tableSizeOf(filtered) > 1 and tableSizeOf(thisStack) == 1) then
            local recipeName = getText("UI_ESIM_FILLTHIS") .. " " .. selectedItem:getDisplayName()
            mergeSubMenu:addOption(recipeName, selectedItem, doMenuFill, filtered, playerObj)
        end
        if (tableSizeOf(thisStack) > 1) then
            local recipeName = getText("UI_ESIM_MERGE") .. " " .. selectedItem:getDisplayName() .. " (" .. #thisStack .. ")"
            mergeSubMenu:addOption(recipeName, selectedItem, doMenuMerge, thisStack, playerObj)
        end
        if (tableSizeOf(filtered) > 1 and tableSizeOf(filtered) ~= tableSizeOf(thisStack)) then
            local recipeName = getText("UI_ESIM_MERGEALL") .. " " .. selectedItem:getDisplayName() .. " (" .. #filtered .. ")"
            mergeSubMenu:addOption(recipeName, selectedItem, doMenuMerge, filtered, playerObj)
        end

        -- with option to leave empty container if there is no replace item.
        if not selectedItem:getReplaceOnDeplete() then
            if (tableSizeOf(filtered) > 1 and tableSizeOf(thisStack) == 1) then
                local recipeName = getText("UI_ESIM_FILLTHIS") .. " " .. selectedItem:getDisplayName() .. " & " .. getText("UI_ESIM_LEAVEEMPTY")
                mergeSubMenu:addOption(recipeName, selectedItem, doMenuFill, filtered, playerObj, true)
            end
            if (tableSizeOf(thisStack) > 1) then
                local recipeName = getText("UI_ESIM_MERGE") .. " " .. selectedItem:getDisplayName() .. " (" .. #thisStack .. ")" .. " & " .. getText("UI_ESIM_LEAVEEMPTY")
                mergeSubMenu:addOption(recipeName, selectedItem, doMenuMerge, thisStack, playerObj, true)
            end
            if (tableSizeOf(filtered) > 1 and tableSizeOf(filtered) ~= tableSizeOf(thisStack)) then
                local recipeName = getText("UI_ESIM_MERGEALL") .. " " .. selectedItem:getDisplayName() .. " (" .. #filtered .. ")" .. " & " .. getText("UI_ESIM_LEAVEEMPTY")
                mergeSubMenu:addOption(recipeName, selectedItem, doMenuMerge, filtered, playerObj, true)
            end
        end

        local vset = mergeSubMenu:addOption(getText("IGUI_invpanel_Remaining") .. " " .. getText("UI_On") .. "/" .. getText("UI_Off"), playerObj, toggleView, true)
        vset.checkMark = toggleView(playerObj)
        context:addSubMenu(context:addOption(getText("UI_ESIM_MERGE") .. ":"), mergeSubMenu)
    end

    return context
end

local function doMenuSpice(player, context, items)
    local thisStack, selectedItem = filterSpices(itemGetFromSelection(items))
    if selectedItem then
        local mergeSubMenu = context:getNew(context)
        local playerObj = getSpecificPlayer(player)
        local allItems = itemFindItemsByType(selectedItem:getFullType(), player)
        local filtered = filterSpices(allItems)

        if (tableSizeOf(thisStack) > 1) then
            local recipeName = getText("UI_ESIM_MERGE") .. " " .. selectedItem:getDisplayName() .. " (" .. #thisStack .. ")"
            mergeSubMenu:addOption(recipeName, selectedItem, doMenuMerge, thisStack, playerObj)
        end
        if (tableSizeOf(filtered) > 1 and tableSizeOf(filtered) ~= tableSizeOf(thisStack)) then
            local recipeName = getText("UI_ESIM_MERGEALL") .. " " .. selectedItem:getDisplayName() .. " (" .. #filtered .. ")"
            mergeSubMenu:addOption(recipeName, selectedItem, doMenuMerge, filtered, playerObj)
        end

        if (#mergeSubMenu.options > 0) then
            context:addSubMenu(context:addOption(getText("UI_ESIM_MERGE") .. ":"), mergeSubMenu)
        end
    end

    return context
end

Events.OnFillInventoryObjectContextMenu.Add(doMenu)
Events.OnFillInventoryObjectContextMenu.Add(doMenuSpice)

local baseISInventoryPane = ISInventoryPane.drawItemDetails;
function ISInventoryPane:drawItemDetails(item, y, xoff, yoff, red)

    if (item ~= nil and toggleView(getPlayer()) and instanceof(item, "Drainable") and item:getUseDelta() > 0) then
        local maxUses = round(1 / item:getUseDelta());
        local usesLeft = item:getDrainableUsesInt();

        local hdrHgt = self.headerHgt
        local top = hdrHgt + y * self.itemHgt + yoff
        local fgBar = { r = 0.0, g = 0.6, b = 0.0, a = 0.7 }
        local fgText = { r = 0.6, g = 0.8, b = 0.5, a = 0.6 }
        local text = getText("IGUI_invpanel_Remaining") .. ": " .. usesLeft .. "/" .. maxUses;
        self:drawTextAndProgressBar(text, item:getUsedDelta(), xoff, top, fgText, fgBar)
    else
        baseISInventoryPane(self, item, y, xoff, yoff, red);
    end

end
