local originalWeights = {}

local fixedWeights = {
	[1] = 0,
	[2] = 0.0001,
	[3] = 0.0005,
	[4] = 0.001,
	[5] = 0.005,
	[6] = 0.01,
}

local weightMultipliers = {
	[7] = 0.75,
	[8] = 0.50,
	[9] = 0.25,
	[10] = 0.01,
}

local ammoTags = {
	["ammo"] = true,
	["ammocase"] = true,
}

local magazineTags = {
	["magazine"] = true,
	["pistolmagazine"] = true,
	["riflemagazine"] = true,
}

local function itemHasAnyTag(scriptItem, wantedTags)
	local tags = scriptItem:getTags()
	for index = 0, tags:size() - 1 do
		local tagName = string.lower(tostring(tags:get(index)))
		tagName = string.match(tagName, "([^:]+)$")
		if wantedTags[tagName] then
			return true
		end
	end

	return false
end

local function classifyItem(scriptItem)
	local itemType = scriptItem:getType()
	local displayCategory = scriptItem:getDisplayCategory()

	if itemType == Type.Weapon or itemType == Type.Container then
		return nil
	end

	if itemHasAnyTag(scriptItem, magazineTags) then
		return "magazine"
	end

	-- Ammo-category WeaponParts with ammunition metadata are detachable magazines; other WeaponParts are attachments.
	local hasMagazineMetadata = scriptItem:getAmmoType() ~= nil
	if hasMagazineMetadata
			and (itemType == Type.Normal
				or (itemType == Type.WeaponPart and displayCategory == "Ammo")) then
		return "magazine"
	end

	if itemType == Type.WeaponPart then
		return nil
	end

	if displayCategory == "Ammo" or itemHasAnyTag(scriptItem, ammoTags) then
		return "ammo"
	end

	return nil
end

local function getSelectedWeight(originalWeight, option)
	if option <= 6 then
		return fixedWeights[option]
	end

	return originalWeight * weightMultipliers[option]
end

local function getItemWeight(scriptItem, option)
	local fullName = scriptItem:getFullName()
	local originalWeight = originalWeights[fullName]

	if originalWeight == nil then
		originalWeight = scriptItem:getActualWeight()
		originalWeights[fullName] = originalWeight
	end

	return getSelectedWeight(originalWeight, option)
end

local function applyWeights(ammoOption, magazineOption)
	local allItems = getScriptManager():getAllItems()

	for index = 0, allItems:size() - 1 do
		local scriptItem = allItems:get(index)
		local classification = classifyItem(scriptItem)

		if classification == "magazine" then
			scriptItem:DoParam("Weight = " .. tostring(getItemWeight(scriptItem, magazineOption)))
		elseif classification == "ammo" then
			scriptItem:DoParam("Weight = " .. tostring(getItemWeight(scriptItem, ammoOption)))
		end
	end
end

local function applyInventoryContainer(container, ammoOption, magazineOption)
	local items = container:getItems()

	for index = 0, items:size() - 1 do
		local item = items:get(index)
		local scriptItem = item:getScriptItem()
		local classification = classifyItem(scriptItem)
		local option = nil

		if classification == "magazine" then
			option = magazineOption
		elseif classification == "ammo" then
			option = ammoOption
		end

		if option then
			local selectedWeight = getItemWeight(scriptItem, option)
			item:setActualWeight(selectedWeight)
			item:setWeight(selectedWeight)
		end

		if item:IsInventoryContainer() then
			applyInventoryContainer(item:getItemContainer(), ammoOption, magazineOption)
		end
	end
end

local function applySandboxWeights()
	local ammoOption = SandboxVars.CustomizableAmmo.Weight
	local magazineOption = SandboxVars.CustomizableMagazines.Weight

	applyWeights(ammoOption, magazineOption)

	local player = getPlayer()
	if player then
		applyInventoryContainer(player:getInventory(), ammoOption, magazineOption)
	end
end

Events.OnInitWorld.Add(applySandboxWeights)
Events.OnLoad.Add(applySandboxWeights)
