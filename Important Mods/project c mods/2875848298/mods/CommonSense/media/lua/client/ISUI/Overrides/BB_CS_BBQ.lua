-- ************************************************************************
-- **        ██████  ██████   █████  ██    ██ ███████ ███    ██          **
-- **        ██   ██ ██   ██ ██   ██ ██    ██ ██      ████   ██          **
-- **        ██████  ██████  ███████ ██    ██ █████   ██ ██  ██          **
-- **        ██   ██ ██   ██ ██   ██  ██  ██  ██      ██  ██ ██          **
-- **        ██████  ██   ██ ██   ██   ████   ███████ ██   ████          **
-- ************************************************************************
-- ** All rights reserved. This content is protected by © Copyright law. **
-- ************************************************************************

local function addFuel(playerObj, bbq, fuelItems)
	if fuelItems:isEmpty() then return end
	if not luautils.walkAdj(playerObj, bbq:getSquare(), true) then return end

	for i=1,fuelItems:size() do
		local fuelItem = fuelItems:get(i-1)
		ISInventoryPaneContextMenu.transferIfNeeded(playerObj, fuelItem)

		if playerObj:isEquipped(fuelItem) then
			ISTimedActionQueue.add(ISUnequipAction:new(playerObj, fuelItem, 50))
		end

		local fuelAmt = ISCampingMenu.getFuelDurationForItem(fuelItem)
		local uses = ISCampingMenu.getFuelItemUses(fuelItem)

		for _=1,uses do
			ISTimedActionQueue.add(ISBBQAddFuel:new(playerObj, bbq, fuelItem, fuelAmt, 100))
		end
	end
end

ISBBQMenu.onAddAllFuel = function(playerObj, bbq)
	local fuelItems = ArrayList.new()
	local containers = ISInventoryPaneContextMenu.getContainers(playerObj)

	for i=1,containers:size() do
		local container = containers:get(i-1)
		container:getAllEval(ISCampingMenu.isValidFuel, fuelItems)
	end

	addFuel(playerObj, bbq, fuelItems)
end

ISBBQMenu.onAddMultipleFuel = function(playerObj, bbq, fuelType)
	local fuelItems = ArrayList.new()
	local containers = ISInventoryPaneContextMenu.getContainers(playerObj)

	for i=1,containers:size() do
		local container = containers:get(i-1)
		container:getAllTypeEval(fuelType, ISCampingMenu.isValidFuel, fuelItems)
	end

	addFuel(playerObj, bbq, fuelItems)
end