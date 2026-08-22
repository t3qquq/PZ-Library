-- ************************************************************************
-- **        ██████  ██████   █████  ██    ██ ███████ ███    ██          **
-- **        ██   ██ ██   ██ ██   ██ ██    ██ ██      ████   ██          **
-- **        ██████  ██████  ███████ ██    ██ █████   ██ ██  ██          **
-- **        ██   ██ ██   ██ ██   ██  ██  ██  ██      ██  ██ ██          **
-- **        ██████  ██   ██ ██   ██   ████   ███████ ██   ████          **
-- ************************************************************************
-- ** All rights reserved. This content is protected by © Copyright law. **
-- ************************************************************************

local function addFuel(playerObj, fireplace, fuelItems)
	if fuelItems:isEmpty() then return end
	if not luautils.walkAdj(playerObj, fireplace:getSquare(), true) then return end

	for i=1,fuelItems:size() do
		local fuelItem = fuelItems:get(i-1)
		ISFireplaceMenu.toPlayerInventory(playerObj, fuelItem)

		if playerObj:isEquipped(fuelItem) then
			ISTimedActionQueue.add(ISUnequipAction:new(playerObj, fuelItem, 50))
		end

		local fuelAmt = ISCampingMenu.getFuelDurationForItem(fuelItem)
		local uses = ISCampingMenu.getFuelItemUses(fuelItem)

		for j=1,uses do
			ISTimedActionQueue.add(ISFireplaceAddFuel:new(playerObj, fireplace, fuelItem, fuelAmt, 100))
		end
	end
end

ISFireplaceMenu.onAddMultipleFuel = function(playerObj, fireplace, fuelType)
	local fuelItems = ArrayList.new()
	local containers = ISInventoryPaneContextMenu.getContainers(playerObj)

	for i=1,containers:size() do
		local container = containers:get(i-1)
		container:getAllTypeEval(fuelType, ISCampingMenu.isValidFuel, fuelItems)
	end

	addFuel(playerObj, fireplace, fuelItems)
end

ISFireplaceMenu.onAddAllFuel = function(playerObj, fireplace)
	local fuelItems = ArrayList.new()
	local containers = ISInventoryPaneContextMenu.getContainers(playerObj)

	for i=1,containers:size() do
		local container = containers:get(i-1)
		container:getAllEval(ISCampingMenu.isValidFuel, fuelItems)
	end

	addFuel(playerObj, fireplace, fuelItems)
end