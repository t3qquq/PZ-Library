-- ************************************************************************
-- **        ██████  ██████   █████  ██    ██ ███████ ███    ██          **
-- **        ██   ██ ██   ██ ██   ██ ██    ██ ██      ████   ██          **
-- **        ██████  ██████  ███████ ██    ██ █████   ██ ██  ██          **
-- **        ██   ██ ██   ██ ██   ██  ██  ██  ██      ██  ██ ██          **
-- **        ██████  ██   ██ ██   ██   ████   ███████ ██   ████          **
-- ************************************************************************
-- ** All rights reserved. This content is protected by © Copyright law. **
-- ************************************************************************

local function tryEquipWeapon(worldObjects, playerObj, obj)
    if obj.square and luautils.walkAdj(playerObj, obj.square) then
        ISWorldObjectContextMenu.transferIfNeeded(playerObj, obj.item)
        ISTimedActionQueue.add(ISEquipWeaponAction:new(playerObj, obj.item, 50, true, obj.item:isTwoHandWeapon()))
	end
end

local function tryEquipClothing(worldObjects, playerObj, obj)
    if obj.square and luautils.walkAdj(playerObj, obj.square) then
        ISWorldObjectContextMenu.transferIfNeeded(playerObj, obj.item)
        ISTimedActionQueue.add(ISWearClothing:new(playerObj, obj.item, 50))
	end
end

local function tryEquipContainer(worldObjects, playerObj, obj)
    if obj.square and luautils.walkAdj(playerObj, obj.square) then
        ISWorldObjectContextMenu.transferIfNeeded(playerObj, obj.item)
        ISTimedActionQueue.add(ISWearClothing:new(playerObj, obj.item, 50))
	end
end

local function onHandleObjInteraction(x, y, context, worldObjects, playerObj)
    -- Code snippet taken from game's source code (Then made more readable by yours truly)
	local playerNum = playerObj:getPlayerNum()
	local squares = {}
	local doneSquare = {}

	for _,v in ipairs(worldObjects) do
		if v:getSquare() and not doneSquare[v:getSquare()] then
			doneSquare[v:getSquare()] = true
			table.insert(squares, v:getSquare())
		end
	end

	local groundObjects = {}
	if JoypadState.players[playerNum+1] then
		for _,square in ipairs(squares) do
			for i=1,square:getWorldObjects():size() do
				local worldObject = square:getWorldObjects():get(i-1)
				table.insert(groundObjects, worldObject)
			end
		end
	else
		local squares2 = {}
		for k,v in pairs(squares) do
			squares2[k] = v
		end

		local radius = 1
		for _,square in ipairs(squares2) do
			local worldX = screenToIsoX(playerNum, x, y, square:getZ())
			local worldY = screenToIsoY(playerNum, x, y, square:getZ())
			ISWorldObjectContextMenu.getSquaresInRadius(worldX, worldY, square:getZ(), radius, doneSquare, squares)
		end

		ISWorldObjectContextMenu.getWorldObjectsInRadius(playerNum, x, y, squares, radius, groundObjects)
	end

	if #groundObjects == 0 then return false end
    -- End of snippet

    local itemList = {}
	for _,worldObject in ipairs(groundObjects) do
        local item = worldObject:getItem()
        if item then
            if item:IsWeapon() or item:IsClothing() or item:IsInventoryContainer() then
                local itemTable = {}
                itemTable.name = item:getName() or "???"
                itemTable.item = item
                itemTable.square = worldObject:getSquare()
                table.insert(itemList, itemTable)
            end
        end
	end

	local equipOption = context:addOptionOnTop(getText("ContextMenu_CS_Equip"), groundObjects, nil)
	local submenu = ISContextMenu:getNew(context)
    local optionsAmount = 0
	context:addSubMenu(equipOption, submenu)

	for _,obj in pairs(itemList) do
        if obj.item:IsWeapon() then
            submenu:addOption(obj.name, worldObjects, tryEquipWeapon, playerObj, obj)
        elseif obj.item:IsClothing() then
            submenu:addOption(obj.name, worldObjects, tryEquipClothing, playerObj, obj)
        elseif obj.item:IsInventoryContainer() then
            submenu:addOption(obj.name, worldObjects, tryEquipContainer, playerObj, obj)
        end

        optionsAmount = optionsAmount + 1
	end

    if optionsAmount == 0 then
        context:removeOptionByName(getText("ContextMenu_CS_Equip"))
    end
end

local onWorldObjContextInteraction = ISWorldObjectContextMenu.handleInteraction

function ISWorldObjectContextMenu.handleInteraction(x, y, test, context, worldobjects, playerObj, playerInv)
    onWorldObjContextInteraction(x, y, test, context, worldobjects, playerObj, playerInv)
    onHandleObjInteraction(x, y, context, worldobjects, playerObj)
end