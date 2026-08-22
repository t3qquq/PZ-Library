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
repairMenu = ''
kamer_object = {}

function KAMER_RepairWall.Repair(playerIN, context, worldObjects, test)
    local saveK = {};
    local SubMenuOption = nil;
    repairMenu = ISContextMenu:getNew(context);

    local d = 1

    for _, k in ipairs(worldObjects) do
        local walldata = k:getModData()
        if (instanceof(k, "IsoThumpable") and walldata.wallType ~= "doorframe") and saveK[k] == nil then
            saveK[k] = true

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
                kamer_object = k
                d = d + 1
                if playerIN then

                    local square = k:getSquare()
                    local objects = square:getObjects()
                    local index = objects:indexOf(k)
                    local gracz = getSpecificPlayer(playerIN)
                    local objectMaterial = KAMER_RepairWallsUtils:ModData(k)
                    local objmat = {
                        objmat = objectMaterial.material,
                        objneeds = objectMaterial.needs,
                        objall = objectMaterial.allItems
                    }

                    local objsend = {
                        x = square:getX(),
                        y = square:getY(),
                        z = square:getZ(),
                        index = index
                    }

                    sendClientCommand(gracz, "KAMER_RepairWall", "CheckInventory", { obiekt=objsend, objmat=objmat, playerid=playerIN });
                end
            end
        end
    end
end

--- dick

function HandleServerRequestsK(module, command, args)

    --- WYJEBAC NAJWYZEJ ISCLIENT
    if isServer() or module ~= "KAMER_RepairWall" then return end
    if command == "CreateMenu" then

            --- DO TESTU
            -- local square = getCell():getGridSquare(args.obiekt.x, args.obiekt.y, args.obiekt.z)
            -- local k = square:getObjects():get(args.obiekt.index)

            local k = kamer_object
            ---------

            local player = getSpecificPlayer(args.playerid)

            local playerSkills = KAMER_RepairWallsUtils:getCurrentSkills(player)
            local Option = nil;
            Option = repairMenu:addOption(getText("ContextMenu_Kamer_RepairWall_RepairOption", args.nazwaobj), k, KAMER_RepairWall.Prepare, args.material, player, k, args.itemy)
                
            local tooltip = ISToolTip:new();
            tooltip:initialise();
            tooltip:setName(args.nazwaobj);
            tooltip:setTexture(k:getSprite():getName())
            local checkTip = 0
            local addLV = 6
            local color = "<RGB:1,0,0>"

            if args.material == "Wood" and playerSkills["Woodwork"] then
                 addLV = (playerSkills["Woodwork"] + 1) * 6
             elseif args.material == "Metal" and playerSkills["MetalWelding"] then
                 addLV = (playerSkills["MetalWelding"] + 1) * 6
             end

            if addLV > 25 and addLV < 45 then
                color = "<RGB:0.604,0.804,0.196>"
            elseif addLV > 45 then
                color = "<RGB:0,1,0>"
            end

            tooltip.description = getText("ContextMenu_Kamer_RepairWall_ToolTip1", addLV, color)

             for nNeededItems, vNeededItems in pairs(args.itemy.neededItems) do
                local txt = "Base." .. nNeededItems
                local getItem = getItemNameFromFullType(txt)
                if vNeededItems <= args.itemy.items[nNeededItems].count then
                    tooltip.description = string.format("%s <LINE> <RGB:0.0,255.0,0.0> %s %d/%d", tooltip.description, getItem,  args.itemy.items[nNeededItems].count, vNeededItems)
                    checkTip = checkTip + 1
                elseif vNeededItems > args.itemy.items[nNeededItems].count then
                    tooltip.description = string.format("%s <LINE> <RGB:255.0,0.0,0.0> %s %d/%d", tooltip.description, getItem, (args.itemy.items[nNeededItems].count or 0), vNeededItems)
                end
             end

            if checkTip >= args.itemy.neededItemsAll then
                Option.notAvailable = false;
            else
                Option.notAvailable = true;
            end

            Option.toolTip = tooltip;
    end
end

function KAMER_RepairWall.Prepare(self, objectMaterial, player, k, playerItems)
    local ObjSq = k:getSquare()
    if luautils.walkAdjWindowOrDoor(player, ObjSq, k) then
        local itemHeld = player:getPrimaryHandItem()
    
        if objectMaterial == "Wood" then
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
        ISTimedActionQueue.add(KAMER_RepairWallsAction:new(objectMaterial, player, k, ObjSq, itemHeld, playerItems))
    end
end

Events.OnFillWorldObjectContextMenu.Add(KAMER_RepairWall.Repair)
Events.OnServerCommand.Add(HandleServerRequestsK)