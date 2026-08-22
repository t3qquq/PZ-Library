require "Utils/RepairWallsUtils"
require "Actions/RepairWallsAction"

co = {}

function HandleClientRequestsK(module, command, player, args)

    --- WYJEBAC ISSERVER
    if isClient() or module ~= "KAMER_RepairWall" then return end

    if command == "updateHealth" then
        local GetSquare = getCell():getGridSquare(args.kamerX, args.kamerY, args.kamerZ)
        local GetObjects = GetSquare:getObjects()

        local ob = GetObjects:get(args.kamerSin)

        local objectData = KAMER_RepairWallsUtils:Data(ob)
        local objHealth = objectData.health
        local objMaxHealth = objectData.maxHealth
    
        local addHealth = math.ceil((objMaxHealth * args.kamerLv)/100)
        local total = objHealth + addHealth
            if total >= objMaxHealth then
                ob:setHealth(objMaxHealth)
            else
                ob:setHealth(total)
            end
        ob:transmitModData()
    end

    if command == "DeleteItems" then
        for l, m in pairs(co.neededItems) do
            if l ~= "Hammer" and l ~= "BlowTorch" and l ~= "WeldingMask" then
                for i=0, m - 1 do
                    local iyy = co.items[l].items[i + 1]
                    local icont = iyy:getContainer()
                    icont:Remove(iyy)
                    sendRemoveItemFromContainer(icont, iyy)
                end
        	end
        end
    end

    if command == "CheckInventory" then
        local square = getCell():getGridSquare(args.obiekt.x, args.obiekt.y, args.obiekt.z)
        local obiekt = square:getObjects():get(args.obiekt.index)
        local objName = obiekt:getName() or obiekt:getProperties():get("CustomName")
        if objName then
            local prepareName = "ContextMenu_" .. string.gsub(objName, " ", "_")
            objName = getText(prepareName)
            if objName == prepareName then
                objName = obiekt:getProperties():get("CustomName") or obiekt:getName() or "Unknown Object (Unnamed)"
            end
        else
            objName = "Unknown Object (Unnamed)"
        end

        local menu = args.menu
        GetAllItems(items, args.objmat, player, obiekt, args.obiekt, objName, args.playerid)
    end
end

function GetAllItems(items, Material, player, obiekt, daneobiektu, objName, playerid)
    op = {}
        local inventory = player:getInventory()
        local items = inventory:getItems()

    for i=0, items:size() - 1 do
        local item = items:get(i)
        local itemString = tostring(item)
        local IsBackpack = string.find(itemString, "InventoryContainer")
        table.insert(op, item)

        if IsBackpack ~= nil then
            local itemEq = item:isEquipped()
            local itemFav = item:isFavorite()
            local itemNotKeyring = item:getType()

            if itemEq and not itemFav and itemNotKeyring ~= "KeyRing" then
                local newInv = item:getInventory()
                local newItems = newInv:getItems()
                for l=0, newItems:size() - 1 do
                     local newItem = newItems:get(l)
                     table.insert(op, newItem)
                end
            end
        end
    end
    VerifyItems(items, Material, player, op, playerid, objName)
end

function VerifyItems(items, Material, player, op, playerid, objName)
    co.items = {}
    co.check = 0
    co.currentMaterial = Material.objmat
    co.neededItems = Material.objneeds
    co.neededItemsAll =  Material.objall
    co.HasItems = false

    for l,k in ipairs(op) do
        local item = k
        local itemType = item:getType()
        local isTakeable = true
        local isBroken = false

        if string.find(itemType, "Hammer") then
            itemType = "Hammer"
            isTakeable = false
            if item:getCondition() == 0 then
                isBroken = true
            end
        elseif string.find(itemType, "WeldingMask") then
            itemType = "WeldingMask"
            isTakeable = false
        elseif string.find(itemType, "BlowTorch") then
            itemType = "BlowTorch"
            isTakeable = false
            if item:getUseDelta() == 0 then
                isBroken = true
            end
        end
        if isBroken == false then
            if co.items[itemType] == nil then
                co.items[itemType] = {}
                co.items[itemType].count = 0
                co.items[itemType].items = {}
                co.items[itemType].containers = {}
                co.items[itemType].isTakeable = true
            end
    
            co.items[itemType].count = co.items[itemType].count + 1
            table.insert(co.items[itemType].items, item)
            table.insert(co.items[itemType].containers, playerInv)
            co.items[itemType].isTakeable = isTakeable
        end
    end

    for nNeededItems, vNeededItems in pairs(co.neededItems) do
        if co.items[nNeededItems] == nil then
            co.items[nNeededItems] = {}
            co.items[nNeededItems].count = 0
        end

        if vNeededItems <= co.items[nNeededItems].count then
            co.check = co.check + 1
        end

        if co.check >= co.neededItemsAll then
            co.HasItems = true
        end
    end
    if not isClient() and not isServer() then
        HandleServerRequestsK("KAMER_RepairWall", "CreateMenu", { material=Material.objmat, itemy=co, playerid=playerid, nazwaobj= objName })
    else
        sendServerCommand(player, "KAMER_RepairWall", "CreateMenu", { material=Material.objmat, itemy=co, playerid=playerid, nazwaobj = objName });
    end
end


Events.OnClientCommand.Add(HandleClientRequestsK)