-----------------------------------------------------------------------
-----------------------------------------------------------------------
-------------------------- KATTAJ1 - TTAJ1bl4 -------------------------
-----------------------------------------------------------------------
-----------------------------------------------------------------------




require "ISUI/ISInventoryPaneContextMenu"

local restrictEquippedOptions = {}

function restrictEquippedOptions.apply(player, context, items)
    -- print("OnFillInventoryObjectContextMenu event triggered")

    local specificItems = {
        -- Military Backpack Pocket Small
        "Base.Military_Backpack_Pocket_Small-Black",
        "Base.Military_Backpack_Pocket_Small-Black_Tight",
        "Base.Military_Backpack_Pocket_Small-Desert",
        "Base.Military_Backpack_Pocket_Small-Desert_Tight",
        "Base.Military_Backpack_Pocket_Small-Green",
        "Base.Military_Backpack_Pocket_Small-Green_Tight",
        "Base.Military_Backpack_Pocket_Small-White",
        "Base.Military_Backpack_Pocket_Small-White_Tight",
    
        -- Military Backpack Strategist Medium
        "Base.Military_Backpack_Strategist_Medium-Black",
        "Base.Military_Backpack_Strategist_Medium-Black_Tight",
        "Base.Military_Backpack_Strategist_Medium-Desert",
        "Base.Military_Backpack_Strategist_Medium-Desert_Tight",
        "Base.Military_Backpack_Strategist_Medium-Green",
        "Base.Military_Backpack_Strategist_Medium-Green_Tight",
        "Base.Military_Backpack_Strategist_Medium-White",
        "Base.Military_Backpack_Strategist_Medium-White_Tight",
    
        -- Military Backpack Ranger Large
        "Base.Military_Backpack_Ranger_Large-Black",
        "Base.Military_Backpack_Ranger_Large-Black_Tight",
        "Base.Military_Backpack_Ranger_Large-Desert",
        "Base.Military_Backpack_Ranger_Large-Desert_Tight",
        "Base.Military_Backpack_Ranger_Large-Green",
        "Base.Military_Backpack_Ranger_Large-Green_Tight",
        "Base.Military_Backpack_Ranger_Large-White",
        "Base.Military_Backpack_Ranger_Large-White_Tight",
    
        -- Military Backpack Colossus VeryLarge
        "Base.Military_Backpack_Colossus_VeryLarge-Black",
        "Base.Military_Backpack_Colossus_VeryLarge-Black_Tight",
        "Base.Military_Backpack_Colossus_VeryLarge-Desert",
        "Base.Military_Backpack_Colossus_VeryLarge-Desert_Tight",
        "Base.Military_Backpack_Colossus_VeryLarge-Green",
        "Base.Military_Backpack_Colossus_VeryLarge-Green_Tight",
        "Base.Military_Backpack_Colossus_VeryLarge-White",
        "Base.Military_Backpack_Colossus_VeryLarge-White_Tight",
    
        -- Military Backpack Echo Radio
        "Base.Military_Backpack_Echo_Radio-Black",
        "Base.Military_Backpack_Echo_Radio-Black_Tight",
        "Base.Military_Backpack_Echo_Radio-Desert",
        "Base.Military_Backpack_Echo_Radio-Desert_Tight",
        "Base.Military_Backpack_Echo_Radio-Green",
        "Base.Military_Backpack_Echo_Radio-Green_Tight",
        "Base.Military_Backpack_Echo_Radio-White",
        "Base.Military_Backpack_Echo_Radio-White_Tight",

        -- Military_BagLeg_MagSecure_Small
        "Base.Military_BagLeg_MagSecure_Small-Black_Left",
        "Base.Military_BagLeg_MagSecure_Small-Black_Right",
        "Base.Military_BagLeg_MagSecure_Small-Black_ArmorLeft",
        "Base.Military_BagLeg_MagSecure_Small-Black_ArmorRight",
        "Base.Military_BagLeg_MagSecure_Small-Desert_Left",
        "Base.Military_BagLeg_MagSecure_Small-Desert_Right",
        "Base.Military_BagLeg_MagSecure_Small-Desert_ArmorLeft",
        "Base.Military_BagLeg_MagSecure_Small-Desert_ArmorRight",
        "Base.Military_BagLeg_MagSecure_Small-Green_Left",
        "Base.Military_BagLeg_MagSecure_Small-Green_Right",
        "Base.Military_BagLeg_MagSecure_Small-Green_ArmorLeft",
        "Base.Military_BagLeg_MagSecure_Small-Green_ArmorRight",
        "Base.Military_BagLeg_MagSecure_Small-White_Left",
        "Base.Military_BagLeg_MagSecure_Small-White_Right",
        "Base.Military_BagLeg_MagSecure_Small-White_ArmorLeft",
        "Base.Military_BagLeg_MagSecure_Small-White_ArmorRight",

        -- Military_BagLeg_Utility_Medium
        "Base.Military_BagLeg_Utility_Medium-Black_Left",
        "Base.Military_BagLeg_Utility_Medium-Black_Right",
        "Base.Military_BagLeg_Utility_Medium-Black_ArmorLeft",
        "Base.Military_BagLeg_Utility_Medium-Black_ArmorRight",
        "Base.Military_BagLeg_Utility_Medium-Desert_Left",
        "Base.Military_BagLeg_Utility_Medium-Desert_Right",
        "Base.Military_BagLeg_Utility_Medium-Desert_ArmorLeft",
        "Base.Military_BagLeg_Utility_Medium-Desert_ArmorRight",
        "Base.Military_BagLeg_Utility_Medium-Green_Left",
        "Base.Military_BagLeg_Utility_Medium-Green_Right",
        "Base.Military_BagLeg_Utility_Medium-Green_ArmorLeft",
        "Base.Military_BagLeg_Utility_Medium-Green_ArmorRight",
        "Base.Military_BagLeg_Utility_Medium-White_Left",
        "Base.Military_BagLeg_Utility_Medium-White_Right",
        "Base.Military_BagLeg_Utility_Medium-White_ArmorLeft",
        "Base.Military_BagLeg_Utility_Medium-White_ArmorRight",
        "Base.Military_BagLeg_Utility_Medium-Medic_Left",
        "Base.Military_BagLeg_Utility_Medium-Medic_Right",
        "Base.Military_BagLeg_Utility_Medium-Medic_ArmorLeft",
        "Base.Military_BagLeg_Utility_Medium-Medic_ArmorRight"
    }

    local commonContextOptions = {
        "ContextMenu_Wear",
        "ContextMenu_Equip_on_your_Back",
        "ContextMenu_AttachPouchToHipLeft",
        "ContextMenu_AttachPouchToHipRight",
        "ContextMenu_AttachPouchToHipLeftOnArmor",
        "ContextMenu_AttachPouchToHipRightOnArmor"
    }


    for i, v in ipairs(items) do
        local testItem = v
        if not instanceof(v, "InventoryItem") then
            testItem = v.items[1]
        end

        if instanceof(testItem, "InventoryItem") and testItem:isEquipped() then
            local itemType = testItem:getFullType()
            if table.contains(specificItems, itemType) then
                for _, contextOption in ipairs(commonContextOptions) do
                    context:removeOptionByName(getText(contextOption))
                end

                local restrictedMessage = context:addOption("Unequip this item to access wear options.", nil)
                restrictedMessage.notAvailable = true
            end
        end
    end
end

Events.OnFillInventoryObjectContextMenu.Add(restrictEquippedOptions.apply)

function table.contains(table, element)
    for _, value in pairs(table) do
        if value == element then
            return true
        end
    end
    return false
end






