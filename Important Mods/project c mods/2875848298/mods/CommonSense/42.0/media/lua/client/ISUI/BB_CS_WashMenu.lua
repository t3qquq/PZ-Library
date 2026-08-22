-- ************************************************************************
-- **        ██████  ██████   █████  ██    ██ ███████ ███    ██          **
-- **        ██   ██ ██   ██ ██   ██ ██    ██ ██      ████   ██          **
-- **        ██████  ██████  ███████ ██    ██ █████   ██ ██  ██          **
-- **        ██   ██ ██   ██ ██   ██  ██  ██  ██      ██  ██ ██          **
-- **        ██████  ██   ██ ██   ██   ████   ███████ ██   ████          **
-- ************************************************************************
-- ** All rights reserved. This content is protected by © Copyright law. **
-- ************************************************************************

local function getMoveableDisplayName(obj)
	if not obj then return nil end
	if not obj:getSprite() then return nil end
	local props = obj:getSprite():getProperties()
	if props:Is("CustomName") then
		local name = props:Val("CustomName")
		if props:Is("GroupName") then
			name = props:Val("GroupName") .. " " .. name
		end
		return Translator.getMoveableDisplayName(name)
	end
	return nil
end

local function defineWashList(playerObj, playerInv)

    local data = {}
    data.fullWashList = {}
    data.equippedWashList = {}
    data.unequippedWashList = {}
    data.weaponWashList = {}
    data.washEquipment = false

	local clothingInventory = playerInv:getItemsFromCategory("Clothing")
	for i=0, clothingInventory:size() - 1 do
		local item = clothingInventory:get(i)
		if not item:isHidden() and (item:hasBlood() or item:hasDirt()) then
			if data.washEquipment == false then
				data.washEquipment = true
			end

            if playerObj:isEquipped(item) then
                table.insert(data.equippedWashList, item)
            else
                table.insert(data.unequippedWashList, item)
            end

			table.insert(data.fullWashList, item)
		end
	end


    local weaponInventory = playerInv:getItemsFromCategory("Weapon")
    for i=0, weaponInventory:size() - 1 do
        local item = weaponInventory:get(i)
        if item:hasBlood() then
            if data.washEquipment == false then
                data.washEquipment = true
            end

            if playerObj:isEquipped(item) then
                table.insert(data.equippedWashList, item)
            else
                table.insert(data.unequippedWashList, item)
            end

            table.insert(data.weaponWashList, item)
            table.insert(data.fullWashList, item)
        end
	end

	local clothingInventory = playerInv:getItemsFromCategory("Container")
	for i=0, clothingInventory:size() - 1 do
		local item = clothingInventory:get(i)
		if not item:isHidden() and (item:hasBlood() or item:hasDirt()) then
			data.washEquipment = true

            if playerObj:isEquipped(item) then
                table.insert(data.equippedWashList, item)
            else
                table.insert(data.unequippedWashList, item)
            end

			table.insert(data.fullWashList, item)
		end
	end

    return data
end

local doWashClothingMenu = ISWorldObjectContextMenu.doWashClothingMenu

local function onWashClothingProper(playerObj, sink, soapList, washList, singleClothing, noSoap, context)
	ISWorldObjectContextMenu.onWashClothing(playerObj, sink, soapList, washList, singleClothing, noSoap)
	context:closeAll()
end

ISWorldObjectContextMenu.doWashClothingMenu = function(sink, player, context)

    doWashClothingMenu(sink, player, context)

    local playerObj = getSpecificPlayer(player)
	if sink:getSquare():getBuilding() ~= playerObj:getBuilding() then return; end
    local playerInv = playerObj:getInventory()
    local customData = defineWashList(playerObj, playerInv)
    local washEquipment = customData.washEquipment
	local washList = customData.fullWashList
	local equippedWashList = customData.equippedWashList
	local unequippedWashList = customData.unequippedWashList
	local weaponWashList = customData.weaponWashList
	local soapList = {}
	local noSoap = true

	local barList = playerInv:getItemsFromType("Soap2", true)
	for i=0, barList:size() - 1 do
        local item = barList:get(i)
		table.insert(soapList, item)
	end

    local bottleList = playerInv:getItemsFromType("CleaningLiquid2", true)
    for i=0, bottleList:size() - 1 do
        local item = bottleList:get(i)
        table.insert(soapList, item)
    end

	table.sort(washList, ISWorldObjectContextMenu.compareClothingBlood)
	table.sort(equippedWashList, ISWorldObjectContextMenu.compareClothingBlood)
	table.sort(unequippedWashList, ISWorldObjectContextMenu.compareClothingBlood)
	table.sort(weaponWashList, ISWorldObjectContextMenu.compareClothingBlood)

	if washEquipment then

		local soapRemaining = ISWashClothing.GetSoapRemaining(soapList)
		local waterRemaining = sink:getFluidAmount()

        if #washList > 1 then

            local soapRequired = 0
            local waterRequired = 0
            for _,item in ipairs(washList) do
                soapRequired = soapRequired + ISWashClothing.GetRequiredSoap(item)
                waterRequired = waterRequired + ISWashClothing.GetRequiredWater(item)
            end

            local tooltip = ISWorldObjectContextMenu.addToolTip();
            local source = getMoveableDisplayName(sink)
            if source == nil and instanceof(sink, "IsoWorldInventoryObject") and sink:getItem() then
                source = sink:getItem():getDisplayName()
            end

            if source == nil then
                source = getText("ContextMenu_NaturalWaterSource")
            end
            tooltip.description = getText("ContextMenu_WaterSource")  .. ": " .. source .. " <LINE> "

            if (soapRemaining < soapRequired) then
                tooltip.description = tooltip.description .. getText("IGUI_Washing_WithoutSoap") .. " <LINE> "
                noSoap = true;
            else
                tooltip.description = tooltip.description .. getText("IGUI_Washing_Soap") .. ": " .. tostring(math.min(soapRemaining, soapRequired)) .. " / " .. tostring(soapRequired) .. " <LINE> "
                noSoap = false;
            end

            tooltip.description = tooltip.description .. getText("ContextMenu_WaterName") .. ": " .. tostring(math.min(waterRemaining, waterRequired)) .. " / " .. tostring(waterRequired)

            -- MY MAGICAL CODE HERE!
            local mainSubMenu = nil
            local option = nil
            local submenuIndex = 1

            while option == nil do
                mainSubMenu = context:getSubMenu(submenuIndex)
                if mainSubMenu == nil then break end
                option = mainSubMenu:getOptionFromName(getText("ContextMenu_WashAllClothing"))
                submenuIndex = submenuIndex + 1
            end

            if mainSubMenu ~= nil and option ~= nil then
                option.toolTip = tooltip
                local bravenSubMenu = ISContextMenu:getNew(context)
                context:addSubMenu(option, bravenSubMenu)
                bravenSubMenu:addOption(getText("ContextMenu_All"), playerObj, onWashClothingProper, sink, soapList, washList, nil,  noSoap, context)

                if #equippedWashList > 0 then
                    bravenSubMenu:addOption(getText("ContextMenu_WashEquippedOnly"), playerObj, onWashClothingProper, sink, soapList, equippedWashList, nil,  noSoap, context)
                end

                if #unequippedWashList > 0 then
                    bravenSubMenu:addOption(getText("ContextMenu_WashUnequippedOnly"), playerObj, onWashClothingProper, sink, soapList, unequippedWashList, nil,  noSoap, context)
                end

                if #weaponWashList > 0 then
                    bravenSubMenu:addOption(getText("ContextMenu_WashWeaponsOnly"), playerObj, onWashClothingProper, sink, soapList, weaponWashList, nil,  noSoap, context)
                end
            end
            -- END OF GOLDEN UNICORNS <3

            if (waterRemaining < waterRequired) then
                option.notAvailable = true;
            end
        end
	end
end