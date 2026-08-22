-- ************************************************************************
-- **        ██████  ██████   █████  ██    ██ ███████ ███    ██          **
-- **        ██   ██ ██   ██ ██   ██ ██    ██ ██      ████   ██          **
-- **        ██████  ██████  ███████ ██    ██ █████   ██ ██  ██          **
-- **        ██   ██ ██   ██ ██   ██  ██  ██  ██      ██  ██ ██          **
-- **        ██████  ██   ██ ██   ██   ████   ███████ ██   ████          **
-- ************************************************************************
-- ** All rights reserved. This content is protected by © Copyright law. **
-- ************************************************************************

local function onFillInventoryObjectContextMenu(player, context, items)

    local itemToPour = nil
    local tempItem = nil

    for _,v in ipairs(items) do

        if not instanceof(v, "InventoryItem") then
            if #v.items == 2 then
                tempItem = v.items[1]
            end
            tempItem = v.items[1]
        else
            tempItem = v
        end

        if tempItem then
            if tempItem:getCategory() == "Food" and tempItem:getReplaceOnUse() == "TinCanEmpty" then
                itemToPour = tempItem
            end
        end
    end

    tempItem = nil

	if itemToPour then
		context:addOption(getText("ContextMenu_Pour_on_Ground"), items, ISInventoryPaneContextMenu.onDumpContents, itemToPour, 30, player)
	end

    return context
end

Events.OnFillInventoryObjectContextMenu.Add(onFillInventoryObjectContextMenu)