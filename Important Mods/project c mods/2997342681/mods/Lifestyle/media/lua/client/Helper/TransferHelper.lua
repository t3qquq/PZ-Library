--------------------------------------------------------------------------------------------------
--		----	  |			  |			|		 |				|    --    |      ----			--
--		----	  |			  |			|		 |				|    --	   |      ----			--
--		----	  |		-------	   -----|	 ---------		-----          -      ----	   -------
--		----	  |			---			|		 -----		------        --      ----			--
--		----	  |			---			|		 -----		-------	 	 ---      ----			--
--		----	  |		-------	   ----------	 -----		-------		 ---      ----	   -------
--			|	  |		-------			|		 -----		-------		 ---		  |			--
--			|	  |		-------			|	 	 -----		-------		 ---		  |			--
--------------------------------------------------------------------------------------------------

require "ISUI/ISInventoryPane"
require "ISUI/ISInventoryPaneContextMenu"
require "Helper/ActionHelper"

TransferHelper = {};

TransferHelper.litCandleExtinguish = function(item, player)
    local playerObj = player

    local candle = playerObj:getInventory():AddItem("Base.Candle");
    candle:setUsedDelta(item:getUsedDelta());
    candle:setCondition(item:getCondition());
    candle:setFavorite(item:isFavorite());
    if item == playerObj:getPrimaryHandItem() then
        playerObj:setPrimaryHandItem(candle);
    else
        playerObj:setSecondaryHandItem(candle);
    end
    playerObj:getInventory():Remove(item);
    return candle;
end

TransferHelper.unequipItem = function(item, player)
    local playerObj = player
    if not playerObj:isEquipped(item) then return end
    if item ~= nil and item:getType() == "CandleLit" then item = TransferHelper.litCandleExtinguish(item, player) end
    ISTimedActionQueue.add(ISUnequipAction:new(playerObj, item, 50));
end

TransferHelper.dropItem = function(item, player)

	local playerObj = player

    if item:getType() == "CandleLit" and item:isEquipped() then
        item = TransferHelper.litCandleExtinguish(item, player)
    end

	if not playerObj:isHandItem(item) then
	--	local hotbar = getPlayerHotbar(isoPlayer)
	--	if hotbar and hotbar:isItemAttached(item) then
	--		hotbar:removeItem(item, true)
	--	else
		TransferHelper.unequipItem(item, player)
	--	end
	end
	
	--ISTimedActionQueue.add(ISInventoryTransferAction:new(playerObj, item, item:getContainer(), ISInventoryPage.floorContainer[isoPlayer + 1]))

	local dest
	local containerList = ArrayList.new();
	local playerNum = playerObj and playerObj:getPlayerNum() or -1
    for i,v in ipairs(getPlayerLoot(playerNum).inventoryPane.inventoryPage.backpacks) do
		containerList:add(v.inventory);
    end

--	if #containerList > 0 then
--		for i,v in ipairs(containerList:getItems()) do
	for i=0,containerList:size()-1 do
		local container = containerList:get(i);
		if container:getType() == "floor" then
			dest = container
			break
		end
	end

	if dest then
		ISTimedActionQueue.add(ISInventoryTransferAction:new(playerObj, item, item:getContainer(), dest))
	end
--	ISInventoryPaneContextMenu.transferIfNeeded(playerObj, item)
 --  ISTimedActionQueue.add(ISInventoryTransferAction:new(playerObj, item, playerObj:getInventory(), ISInventoryPage.floorContainer[player + 1]));
end

TransferHelper.onDropItems = function(items, player, single)
	local playerObj = player

    local noVehicle = true
    local vehicleNoWindow = true
    local vehicleWindowOpen = true

    local vehicle = playerObj:getVehicle()

    if vehicle ~= nil then
        noVehicle = false
        local seat = vehicle:getSeat(playerObj)
        local door = vehicle:getPassengerDoor(seat)
        local windowPart = VehicleUtils.getChildWindow(door)
        if windowPart and (not windowPart:getItemType() or windowPart:getInventoryItem()) then
            vehicleNoWindow = false
            local window = windowPart:getWindow()
            if window:isOpenable() and not window:isOpen() then
                vehicleWindowOpen = false
            end
        end
    end

    if not (noVehicle or vehicleNoWindow or vehicleWindowOpen) then return end

	if single then
		if not items:isFavorite() then
			TransferHelper.dropItem(items, player)
		end
	else
		items = ISInventoryPane.getActualItems(items)
	--	ISInventoryPaneContextMenu.transferItems(items, playerObj:getInventory(), player, true)
		for _,item in ipairs(items) do
			if not item:isFavorite() then
				TransferHelper.dropItem(item, player)
			end
		end
	end
end

TransferHelper.onMoveItemsTo = function(items, dest, player, single)
    if dest:getType() == "floor" then
		if single then
			if not items:isFavorite() then
				TransferHelper.dropItem(items, player)
			end
			return
		else
			return TransferHelper.onDropItems(items, player, single)
		end
    end
    local playerObj = player
	if not ActionHelper.walkToContainer(playerObj, dest) then
		return
	end
	if single then
		if playerObj:isEquipped(items) then
			ISTimedActionQueue.add(ISUnequipAction:new(playerObj, items, 50));
		end
		ISTimedActionQueue.add(ISInventoryTransferAction:new(playerObj, items, items:getContainer(), dest))
	else
		for i,item in ipairs(items) do
			if playerObj:isEquipped(item) then
				ISTimedActionQueue.add(ISUnequipAction:new(playerObj, item, 50));
			end
			ISTimedActionQueue.add(ISInventoryTransferAction:new(playerObj, item, item:getContainer(), dest))
		end
	end
end