PercMult = 1            --tutte le percentuali di drop vengono moltiplicate per questo valore

GenericFragmentPerc = 7 --percentuale base di drop dei frammenti dagli zombie generici

OutfitList = {}
OutfitList['ArmyCamoDesert'] = 22 --percentuale da 1 a 100
OutfitList['ArmyCamoGreen'] = 22
OutfitList['PrivateMilitia'] = 18
OutfitList['Police'] = 10
OutfitList['PoliceState'] = 10
OutfitList['ArmyInstructor'] = 10

OutfitList['Survivalist'] = 8
OutfitList['Survivalist02'] = 8
OutfitList['Survivalist03'] = 8

OutfitList['Ranger'] = 6
OutfitList['PrisonGuard'] = 6
OutfitList['Fireman'] = 5


-- Helper function to add item to container and sync in MP server
local function myAddItemToContainer(container, itemType)
    if not container or not itemType then return nil end
    local addedItem = container:AddItem(itemType)
    if addedItem and isServer() and sendAddItemToContainer then
        sendAddItemToContainer(container, addedItem)
    end
    return addedItem
end

-- function executed for each zombie death
local function WQSOnZombieDead(zombie)
    if isClient() then return end

    local isRepMode = WQS_Shared.IsActiveRepeatersMode()
    if not isRepMode then
        return
    end

    --tutte le percentuali di drop vengono moltiplicate per questo valore
    PercMult = SandboxVars.WQS_ItemInZombieLootMultiplier_opt or 1
    --PercMult = 80                        --debug
    GenericFragmentPerc = 1.2 * PercMult --percentuale base di drop dei frammenti dagli zombie generici
    --in realtà la percentuale reale alla fine è un terzo di quella calcolata qui, quindi di base è 1%
    --dati: kill 600zombie -> 3 frammenti

    local outfit = tostring(zombie:getOutfitName())
    local zombieInventory = zombie:getInventory()
    local itemType = WQS_Shared.getExtractionItemId(true)
    local RepeaterFragmentItemType = WQS_Shared.getRepeaterFragmentLocationItemId(true)
    local myRand = 0

    local check1 = outfit and zombieInventory and itemType

    if (not (check1)) then
        --print("check1 fail")
        return
    end
    --print("outfit " .. outfit)
    --print("PercMult=" .. PercMult .. " outfit=" .. outfit .. " frag perc=" .. GenericFragmentPerc);

    if OutfitList[outfit] ~= nil then -- è un outfit giusto!
        local myPerc = OutfitList[outfit] * PercMult
        myRand = ZombRand(101)
        print("PercMult=" .. PercMult .. " outfit=" .. outfit .. " roll=" .. myRand .. "<" .. myPerc);
        --print("percentuale per " .. outfit .. "=" .. (myPerc) .. " rand=" .. myRand)

        --add walkie talkie?
        if (myRand <= myPerc) then
            myAddItemToContainer(zombieInventory, itemType)
            print("-added " .. itemType .. " outfit=" .. outfit .. "roll" .. myRand .. "<" .. myPerc)
        end

        --add fragment?
        myRand = ZombRand(101)
        --print(" second rand=" .. myRand)
        if (myRand <= math.ceil(myPerc * 1) and isRepMode) then
            myAddItemToContainer(zombieInventory, RepeaterFragmentItemType)
            print("Added " ..
                RepeaterFragmentItemType .. " outfit=" .. outfit .. " zombieInventory " .. tostring(zombieInventory))
        end
        -- elseif isRepMode then
    else
        myRand = ZombRand(101 * 10)
        GenericFragmentPerc = GenericFragmentPerc * 10
        --print(myRand .. " -> " .. GenericFragmentPerc)
        --if ((outfit == "Generic01") or (outfit == "Generic02")) and (myRand <= GenericFragmentPerc) then -- 2.5%
        if (string.find(string.lower(outfit), "generic") ~= nil) then
            --print("Outfit valid " .. outfit .. " myRand-> " .. myRand .. " GenericFragmentPerc-> " .. GenericFragmentPerc)
            if (myRand <= GenericFragmentPerc) then
                myAddItemToContainer(zombieInventory, RepeaterFragmentItemType)
                print("AAAdded " ..
                    RepeaterFragmentItemType ..
                    " outfit=" .. outfit .. " GenericFragmentPerc=" .. GenericFragmentPerc .. " roll=" .. myRand)
            end
        end
    end
end

Events.OnZombieDead.Add(WQSOnZombieDead)


--pl = getPlayer();
--addZombiesInOutfit(pl:getX() + 2 ,pl:getY()+2, 0, 1, 'ArmyCamoDesert', 0)
