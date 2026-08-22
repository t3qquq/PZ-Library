KAMER_RepairWallsUtils = KAMER_RepairWallsUtils or {}
    --- Take all items from containers / player useless.
function KAMER_RepairWallsUtils.GetAllItems(self, items, op, player)

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

    return op;
end

function KAMER_RepairWallsUtils.Data(self, object)
    local data = {}

    local fieldNum = getNumClassFields(object)
    for i=0, fieldNum-1 do
        local objectname = getClassField(object, i)
        local c = string.match(tostring(objectname), '[^.]+$')
        local objectvalue = getClassFieldVal(object, objectname)

        data[c] = objectvalue
    end
    return data;
end

function KAMER_RepairWallsUtils.ModData(self, object)
    local data = {}
    data.material = "Wood"
    data.needs = {}
    data.needs.Nails = 2
    data.needs.Plank = 1
    data.needs.Hammer = 1
    data.allItems = 3

    local info = object:getModData()

    --- Mod without moddata #More Builds --------------
    if GetTableLng(info) == 0 then
        local spr = object:getSprite()
        if not spr then return data end;
        local sprProp = spr:getProperties()
        if not sprProp then return data end;
        local sprVal = sprProp:Val("Material")
        if not sprVal then return data end;
        if tostring(sprVal) == "MetalWelding" or tostring(sprVal) == "MetalBars" then
            data.material = "Metal"
            data.needs = {}
            data.needs.MetalPipe = 1
            data.needs.BlowTorch = 1
            data.needs.WeldingMask = 1
            data.allItems = 3

            return data
        end
        return data
    end
    ---------------------------------------------------------

    for aName, aValue in pairs(info) do
        local c = string.match(tostring(aName), '[^:]+$')

        if c == "MetalWelding" or c == "MetalBars" then
            data.material = "Metal"
            data.needs = {}
            data.needs.MetalPipe = 1
            data.needs.BlowTorch = 1
            data.needs.WeldingMask = 1
            data.allItems = 3
        end
    end

    return data;
end

function KAMER_RepairWallsUtils.getCurrentSkills(self, player)

    local data = {}

    local playerSkills = player:getPerkList()
    for iskills=0, playerSkills:size() - 1 do
        local skill = playerSkills:get(iskills)
        local skillData = KAMER_RepairWallsUtils:Data(skill)
        local skillLvl = player:getPerkLevel(skillData.perk)
        data[tostring(skillData.perk)] = skillLvl
    end

    return data;
end 

function GetTableLng(tbl)
    local getN = 0
    for n in pairs(tbl) do 
        getN = getN + 1 
    end
    return getN
end
