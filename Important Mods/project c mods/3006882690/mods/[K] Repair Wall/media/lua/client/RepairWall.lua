--- ████████████████████████████████████████
--- ████████████████████████████████████████
--- ██████▀░░░░░░░░▀████████▀▀░░░░░░░▀██████
--- ████▀░░░░░░░░░░░░▀████▀░░░░░░░░░░░░▀████
--- ██▀░░░░░░░░░░░░░░░░▀▀░░░░░░░░░░░░░░░░▀██
--- ██░░░░░░░░░░░░░░░░░░░▄▄░░░░░░░░░░░░░░░██
--- ██░░░░░░░░░░░░░░░░░░█░█░░░░░░░░░░░░░░░██
--- ██░░░░░░░░░░░░░░░░░▄▀░█░░░░░░░░░░░░░░░██
--- ██░░░░░░░░░░████▄▄▄▀░░▀▀▀▀▄░░░░░░░░░░░██
--- ██▄░░░░░░░░░████░░░░░░░░░░█░░░░░░░░░░▄██
--- ████▄░░░░░░░████░░░░░░░░░░█░░░░░░░░▄████
--- ██████▄░░░░░████▄▄▄░░░░░░░█░░░░░░▄██████
--- ████████▄░░░▀▀▀▀░░░▀▀▀▀▀▀▀░░░░░▄████████
--- ██████████▄░░░░░░░░░░░░░░░░░░▄██████████
--- ████████████▄░░░░░░░░░░░░░░▄████████████
--- ██████████████▄░░░░░░░░░░▄██████████████
--- ████████████████▄░░░░░░▄████████████████
--- ██████████████████▄▄▄▄██████████████████
--- ████████████████████████████████████████
--- ████████████████████████████████████████


require "Utils/RepairWallsUtils"
require "Actions/RepairWallsAction"


KAMER_RepairWall = KAMER_RepairWall or {}

function KAMER_RepairWall.Repair(playerIN, context, worldObjects, test)
    local saveK = {};
    local SubMenuOption = nil;
    local repairMenu = ISContextMenu:getNew(context);

    local d = 1

    for _, k in ipairs(worldObjects) do
        local walldata = k:getModData()
        if (instanceof(k, "IsoThumpable") and walldata.wallType ~= "doorframe") and saveK[k] == nil then
            saveK[k] = true

            local objectMaterial = KAMER_RepairWallsUtils:ModData(k)

            local objHealth = k:getHealth()
            local objMaxHealth = k:getMaxHealth()
            if objHealth == 0 or objHealth == objMaxHealth or objHealth > objMaxHealth then return end

            if d == 1 then
                if getActivatedMods():contains("KAMER_WallHealth") then
                    SubMenuOption = context:insertOptionAfter(getText("ContextMenu_Check_Status"), getText("ContextMenu_Kamer_RepairWall_Repair"), worldObjects, nil)
                else
                    SubMenuOption = context:addOptionOnTop(getText("ContextMenu_Kamer_RepairWall_Repair"), worldObjects, nil)
                end
                SubMenuOption.iconTexture = getTexture("media/ui/repairIcon.png");
                context:addSubMenu(SubMenuOption, repairMenu);
                d = d + 1
            end

            local player = getSpecificPlayer(playerIN)
            if player then
                local playerItems = {}
                local playerInventory = player:getInventory()
                local objName = k:getName() or k:getProperties():Val("CustomName")
                if objName then
                    local prepareName = "ContextMenu_" .. string.gsub(objName, " ", "_")
                    objName = getText(prepareName)
                    if objName == prepareName then
                        objName = k:getProperties():Val("CustomName") or k:getName() or "Unknown Object (Unnamed)"
                    end
                else
                    objName = "Unknown Object (Unnamed)"
                end


                playerItems = KAMER_RepairWall:VerifyItems(playerInventory, objectMaterial, player)
                local playerSkills = KAMER_RepairWallsUtils:getCurrentSkills(player)
                local Option = nil;
                Option = repairMenu:addOption(getText("ContextMenu_Kamer_RepairWall_RepairOption", objName), k, KAMER_RepairWall.Prepare, objectMaterial, player, k, playerItems)
                
                local tooltip = ISToolTip:new();
                tooltip:initialise();
                tooltip:setName(objName);
                tooltip:setTexture(k:getSprite():getName())
                local checkTip = 0
                local addLV = 6
                local color = "<RGB:1,0,0>"

                if objectMaterial.material == "Wood" and playerSkills["Woodwork"] then
                    addLV = (playerSkills["Woodwork"] + 1) * 6
                elseif objectMaterial.material == "Metal" and playerSkills["MetalWelding"] then
                    addLV = (playerSkills["MetalWelding"] + 1) * 6
                end

                if addLV > 25 and addLV < 45 then
                    color = "<RGB:0.604,0.804,0.196>"
                elseif addLV > 45 then
                    color = "<RGB:0,1,0>"
                end

                tooltip.description = getText("ContextMenu_Kamer_RepairWall_ToolTip1", addLV, color)
                for nNeededItems, vNeededItems in pairs(playerItems.neededItems) do
                    local txt = "Base." .. nNeededItems
                    local getItem = getItemNameFromFullType(txt)
                    if vNeededItems <= playerItems.items[nNeededItems].count then
                        tooltip.description = getText("%3 <LINE> <RGB:0.0,255.0,0.0> %1 %4/%2", getItem, vNeededItems, tooltip.description, playerItems.items[nNeededItems].count)
                        checkTip = checkTip + 1
                    elseif vNeededItems > playerItems.items[nNeededItems].count then
                        tooltip.description = getText("%3 <LINE> <RGB:255.0,0.0,0.0> %1 %4/%2", getItem, vNeededItems, tooltip.description, (playerItems.items[nNeededItems].count or 0))
                    end
                end

                if checkTip >= playerItems.neededItemsAll then
                    Option.notAvailable = false;
                else
                    Option.notAvailable = true;
                end

                Option.toolTip = tooltip;
            end
        end
    end
end

function KAMER_RepairWall.VerifyItems(self, playerInv, Material, player)
    local items = playerInv:getItems()
    local op = {}
    op = KAMER_RepairWallsUtils:GetAllItems(items, op, player)
    local co = {}
    co.items = {}
    co.check = 0
    co.currentMaterial = Material.material
    co.neededItems = Material.needs
    co.neededItemsAll =  Material.allItems
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

    return co;
end

--- dick

function KAMER_RepairWall.Prepare(self, objectMaterial, player, k, playerItems)
    local ObjSq = k:getSquare()
    if luautils.walkAdjWindowOrDoor(player, ObjSq, k) then
        local itemHeld = player:getPrimaryHandItem()
    
        if objectMaterial.material == "Wood" then
            if not itemHeld or (itemHeld and not string.find(itemHeld:getType(), "Hammer")) then
                itemHeld = playerItems.items["Hammer"].items[1]
                ISTimedActionQueue.add(ISInventoryTransferAction:new(player, playerItems.items["Hammer"].items[1], playerItems.items["Hammer"].items[1]:getContainer(), player:getInventory(), 100))
                ISTimedActionQueue.add(ISEquipWeaponAction:new(player, playerItems.items["Hammer"].items[1], 10, true))
            end
        else
            if not itemHeld or (itemHeld and not string.find(itemHeld:getType(), "BlowTorch")) then
                -- player:setPrimaryHandItem(playerItems.items["BlowTorch"].items[1])
                itemHeld = playerItems.items["BlowTorch"].items[1]
                ISTimedActionQueue.add(ISInventoryTransferAction:new(player, playerItems.items["BlowTorch"].items[1], playerItems.items["BlowTorch"].items[1]:getContainer(), player:getInventory(), 100))
                ISTimedActionQueue.add(ISEquipWeaponAction:new(player, playerItems.items["BlowTorch"].items[1], 10, true))
            end
            ISTimedActionQueue.add(ISInventoryTransferAction:new(player, playerItems.items["WeldingMask"].items[1], playerItems.items["WeldingMask"].items[1]:getContainer(), player:getInventory(), 100))
            ISTimedActionQueue.add(ISWearClothing:new(player, playerItems.items["WeldingMask"].items[1], 10))
        end
        -- ISInventoryPage.renderDirty = true;
        ISTimedActionQueue.add(KAMER_RepairWallsAction:new(objectMaterial, player, k, ObjSq, itemHeld))
    end
end

Events.OnFillWorldObjectContextMenu.Add(KAMER_RepairWall.Repair)