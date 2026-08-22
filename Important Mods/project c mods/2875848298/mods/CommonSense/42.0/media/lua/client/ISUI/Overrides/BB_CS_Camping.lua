-- ************************************************************************
-- **        ██████  ██████   █████  ██    ██ ███████ ███    ██          **
-- **        ██   ██ ██   ██ ██   ██ ██    ██ ██      ████   ██          **
-- **        ██████  ██████  ███████ ██    ██ █████   ██ ██  ██          **
-- **        ██   ██ ██   ██ ██   ██  ██  ██  ██      ██  ██ ██          **
-- **        ██████  ██   ██ ██   ██   ████   ███████ ██   ████          **
-- ************************************************************************
-- ** All rights reserved. This content is protected by © Copyright law. **
-- ************************************************************************

local function addFuel(playerObj, campfire, fuelItems)
	if fuelItems:isEmpty() then return end
	if not ISCampingMenu.walkToCampfire(playerObj, campfire:getSquare()) then return end

	for i=1,fuelItems:size() do
		local fuelItem = fuelItems:get(i-1)
		ISCampingMenu.toPlayerInventory(playerObj, fuelItem)

		if playerObj:isEquipped(fuelItem) then
			ISTimedActionQueue.add(ISUnequipAction:new(playerObj, fuelItem, 50))
		end

		local fuelAmt = ISCampingMenu.getFuelDurationForItem(fuelItem)
		local uses = ISCampingMenu.getFuelItemUses(fuelItem)

		for j=1,uses do
			ISTimedActionQueue.add(ISAddFuelAction:new(playerObj, campfire, fuelItem, fuelAmt, 100))
		end
	end
end

ISCampingMenu.onAddAllFuel = function(playerObj, campfire)
	if not ISCampingMenu.isValidCampfire(campfire) then return end

	local fuelItems = ArrayList.new()
	local containers = ISInventoryPaneContextMenu.getContainers(playerObj)

	for i=1,containers:size() do
		local container = containers:get(i-1)
		container:getAllEval(ISCampingMenu.isValidFuel, fuelItems)
	end

	addFuel(playerObj, campfire, fuelItems)
end

ISCampingMenu.onAddMultipleFuel = function(playerObj, campfire, fuelType)
	if not ISCampingMenu.isValidCampfire(campfire) then return end

	local fuelItems = ArrayList.new()
	local containers = ISInventoryPaneContextMenu.getContainers(playerObj)

	for i=1,containers:size() do
		local container = containers:get(i-1)
		container:getAllTypeEval(fuelType, ISCampingMenu.isValidFuel, fuelItems)
	end

	addFuel(playerObj, campfire, fuelItems)
end