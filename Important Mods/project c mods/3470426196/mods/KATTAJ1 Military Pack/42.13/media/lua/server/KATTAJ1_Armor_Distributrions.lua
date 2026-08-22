----------------------------------------------------------------------------
----------------------------- KATTAJ1 - TTAJ1bl4 ---------------------------
----------------------------------------------------------------------------

require "Items/ProceduralDistributions"


function KATTAJ1_insertItemSetsIntoDistributions(itemSets, suffixes, spawnChances, distributionNames)
    local currentIndex = 1 

    for _, distributionName in ipairs(distributionNames) do
        local distribution = ProceduralDistributions.list[distributionName]

        for i, itemSet in ipairs(itemSets) do
            local baseItem = itemSet.baseItem
            local continuation = itemSet.continuation or ""

            for _, suffix in ipairs(suffixes) do
                local fullItem = "Base." .. baseItem .. suffix .. continuation

                local currentSpawnChance = spawnChances[currentIndex]
                currentIndex = (currentIndex % #spawnChances) + 1

                table.insert(distribution.items, fullItem)
                table.insert(distribution.items, currentSpawnChance)
            end
        end
    end
end




function KATTAJ1_initializeItemDistributionsSandboxOptions()

    -- Sandbox affected
    local commonSuffixes = {} 
    local militarySuffixesPlusMedic = {"-Medic"} 
    local militarySuffixesPlusPress = {}
    local pressSuffixes = {} 

    -- Non affected
    local emptySuffixes = {""} 

    -- Sandbox Check Boxes for color spawns
    if SandboxVars.KATTAJ1.EnableBlackGearLoot == true then
        table.insert(commonSuffixes, "-Black")
        table.insert(militarySuffixesPlusMedic, "-Black")
        table.insert(militarySuffixesPlusPress,"-Black")
    end

    if SandboxVars.KATTAJ1.EnableDesertGearLoot == true then
        table.insert(commonSuffixes, "-Desert") 
        table.insert(militarySuffixesPlusMedic, "-Desert")
        table.insert(militarySuffixesPlusPress,"-Desert")
    end

    if SandboxVars.KATTAJ1.EnableGreenGearLoot == true then
        table.insert(commonSuffixes, "-Green") 
        table.insert(militarySuffixesPlusMedic, "-Green")
        table.insert(militarySuffixesPlusPress,"-Green")
    end

    if SandboxVars.KATTAJ1.EnableWhiteGearLoot == true then
        table.insert(commonSuffixes, "-White") 
        table.insert(militarySuffixesPlusMedic, "-White")
        table.insert(militarySuffixesPlusPress,"-White")
    end

    if SandboxVars.KATTAJ1.EnablePressGearLoot == true then
        table.insert(militarySuffixesPlusPress, "-Press")
        table.insert(pressSuffixes,"-Press")
    end

    -- Sandbox Spawn Chances
    local spawnChancesPatriotGear = {SandboxVars.KATTAJ1.PatriotGear}
    local spawnChancesDefenderGear = {SandboxVars.KATTAJ1.DefenderGear}
    local spawnChanceVanguardGear = {SandboxVars.KATTAJ1.VanguardGear}

    local spawnChancesPocketBackpack = {SandboxVars.KATTAJ1.PocketBackpack}
    local spawnChancesStrategistBackpack = {SandboxVars.KATTAJ1.StrategistBackpack}
    local spawnChancesRangerBackpack = {SandboxVars.KATTAJ1.RangerBackpack}
    local spawnChancesColossusBackpack = {SandboxVars.KATTAJ1.ColossusBackpack}
    local spawnChancesEchoBackpack = {SandboxVars.KATTAJ1.EchoBackpack}

    local spawnChancesStormPackSmall = {SandboxVars.KATTAJ1.StormPackSmall}
    local spawnChancesStormPackMedium = {SandboxVars.KATTAJ1.StormPackMedium}
    local spawnChancesStormPackLarge = {SandboxVars.KATTAJ1.StormPackLarge}

    local spawnChancesPouchesSmall = {SandboxVars.KATTAJ1.PouchesSmall}
    local spawnChancesPouchesMedium = {SandboxVars.KATTAJ1.PouchesMedium}
    local spawnChancesPouchesLarge = {SandboxVars.KATTAJ1.PouchesLarge}

    local spawnChancesHipBagSmall = {SandboxVars.KATTAJ1.HipBagSmall}
    local spawnChancesHipBagMedium = {SandboxVars.KATTAJ1.HipBagMedium}

    local spawnChancesHolsterSheath = {SandboxVars.KATTAJ1.HolsterSheath}
    local spawnChancesHeadApparel = {SandboxVars.KATTAJ1.HeadApparel}
    local spawnChancesBalaclava = {SandboxVars.KATTAJ1.Balaclava}
    local spawnChancesJacket = {SandboxVars.KATTAJ1.Jacket}
    local spawnChancesGloves = {SandboxVars.KATTAJ1.Gloves}
    local spawnChancesMilitaryTShirts = {SandboxVars.KATTAJ1.MilitaryTShirts}
    local spawnChancesPantsShorts = {SandboxVars.KATTAJ1.PantsShorts}
    local spawnChancesBootsShoes = {SandboxVars.KATTAJ1.BootsShoes}
    local spawnChancesThermalUnderwear = {SandboxVars.KATTAJ1.ThermalUnderwear}

    local spawnChancesNonMilitary = {SandboxVars.KATTAJ1.NonMilitary}

    
    -- Пулы мест, где могу звспавнится предметы 
    local distributionNames = {"ArmySurplusOutfit", "LockerArmyBedroom", "ArmyStorageOutfit", "GunStoreCounter"} 
    local policePlusArmyDistributions = {"ArmySurplusOutfit", "LockerArmyBedroom", "ArmyStorageOutfit","PoliceStorageOutfit", "PoliceLockers", "PoliceStorageGuns", "GunStoreCounter"} 
    local policeDistributions = {"PoliceStorageOutfit", "PoliceLockers", "PoliceStorageGuns"} 
    local storesDistributions = {"ClothingStorageLegwear", "ClothingStoresJeans", "ClothingStoresDress","ClothingStoresJackets","ClothingStoresJacketsFormal","ClothingStoresJumpers","ClothingStoresOvershirts","ClothingStoresPants","ClothingStoresPantsFormal","ClothingStoresShirts","ClothingStoresShirtsFormal","ClothingStoresSport","ClothingStoresSummer","ClothingStorageAllJackets","ClothingStorageAllShirts"}

    -- PoliceStorageOutfit
    -- PoliceLockers
    -- PoliceStorageGuns

    --////////////////////////////////////////////////////// SETS ////////////////////////////////////////////////////// --
    local itemSets = { 
    -- Helmets 
        { baseItem = "Military_Helmet_Patriot" }, 
    -- Arms protection lower     
        { baseItem = "Military_ArmsProtectionLower_Patriot_Light" }, 
    -- Arms protection Upper   
        { baseItem = "Military_ArmsProtectionUpper_Patriot_Light" }, 
    -- Vests 
        { baseItem = "Military_BulletproofVest_Patriot_Light" }, 
    -- Legs Protection Lower 
        { baseItem = "Military_LegsProtectionLower_Patriot_Light" }, 
    -- Legs Protection Lower 
        { baseItem = "Military_LegsProtectionUpper_Patriot_Light" }
    } 
    KATTAJ1_insertItemSetsIntoDistributions(itemSets, commonSuffixes, spawnChancesPatriotGear, distributionNames)



    local itemSets21 = { 
        -- Helmets 
            { baseItem = "Military_Helmet_Defender" }, 
        -- Arms protection lower      
            { baseItem = "Military_ArmsProtectionLower_Defender_Medium" }, 
        -- Arms protection Upper   
            { baseItem = "Military_ArmsProtectionUpper_Defender_Medium" },  
        -- Vests 
            { baseItem = "Military_BulletproofVest_Defender_Medium" },  
        -- Legs Protection Lower 
            { baseItem = "Military_LegsProtectionLower_Defender_Medium" }, 
        -- Legs Protection Lower 
            { baseItem = "Military_LegsProtectionUpper_Defender_Medium" }
        } 
    KATTAJ1_insertItemSetsIntoDistributions(itemSets21, commonSuffixes, spawnChancesDefenderGear, distributionNames)

    local itemSets22 = { 
        -- Helmets 
            { baseItem = "Military_FullHelmet_Vanguard" }, 
        -- Arms protection lower     
            { baseItem = "Military_ArmsProtectionLower_Vanguard_Heavy" }, 
        -- Arms protection Upper    
            { baseItem = "Military_ArmsProtectionUpper_Vanguard_Heavy" }, 
        -- Vests 
            { baseItem = "Military_BulletproofVest_Vanguard_Heavy" }, 
        -- Legs Protection Lower 
            { baseItem = "Military_LegsProtectionLower_Vanguard_Heavy" }, 
        -- Legs Protection Lower 
            { baseItem = "Military_LegsProtectionUpper_Vanguard_Heavy" } 
        } 
    KATTAJ1_insertItemSetsIntoDistributions(itemSets22, commonSuffixes, spawnChanceVanguardGear, distributionNames)


    --////////////////////////////////////////////////////// Backpacks //////////////////////////////////////////////////////
    local itemSets23 = {
        { baseItem = "Military_Backpack_Pocket_Small" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets23, commonSuffixes, spawnChancesPocketBackpack, distributionNames)

    local itemSets24 = {
        { baseItem = "Military_Backpack_Strategist_Medium" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets24, commonSuffixes, spawnChancesStrategistBackpack, distributionNames)

    local itemSets25 = {
        { baseItem = "Military_Backpack_Ranger_Large" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets25, commonSuffixes, spawnChancesRangerBackpack, distributionNames)

    local itemSets26 = {
        { baseItem = "Military_Backpack_Colossus_VeryLarge" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets26, commonSuffixes, spawnChancesColossusBackpack, distributionNames)

    local itemSets27 = {
        { baseItem = "Military_Backpack_Echo_Radio" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets27, commonSuffixes, spawnChancesEchoBackpack, distributionNames)


    --////////////////////////////////////////////////////// StormPacks //////////////////////////////////////////////////////
    local itemSets28 = {
        { baseItem = "Military_BagBack_StormPack_Small" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets28, militarySuffixesPlusMedic, spawnChancesStormPackSmall, distributionNames)

    local itemSets29 = {
        { baseItem = "Military_BagBack_StormPack_Medium" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets29, militarySuffixesPlusMedic, spawnChancesStormPackMedium, distributionNames)

    local itemSets30 = {
        { baseItem = "Military_BagBack_StormPack_Large" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets30, militarySuffixesPlusMedic, spawnChancesStormPackLarge, distributionNames)


    --////////////////////////////////////////////////////// Chest Pouches //////////////////////////////////////////////////////
    local itemSets31 = {
        { baseItem = "Military_BagFront_ChestPouches_Small" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets31, commonSuffixes, spawnChancesPouchesSmall, distributionNames)

    local itemSets32 = {
        { baseItem = "Military_BagFront_ChestPouches_Medium" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets32, commonSuffixes, spawnChancesPouchesMedium, distributionNames)

    local itemSets33 = {
        { baseItem = "Military_BagFront_ChestPouches_Large" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets33, commonSuffixes, spawnChancesPouchesLarge, distributionNames)


    --////////////////////////////////////////////////////// Hip Bags ////////////////////////////////////////////////////// --
    local itemSets34 = {
        { baseItem = "Military_BagLeg_MagSecure_Small", continuation = "_Left" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets34, commonSuffixes, spawnChancesHipBagSmall, policePlusArmyDistributions)

    local itemSets35 = {
        { baseItem = "Military_BagLeg_Utility_Medium", continuation = "_Left" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets35, militarySuffixesPlusMedic, spawnChancesHipBagMedium, policePlusArmyDistributions)


    --////////////////////////////////////////////////////// Holsters / Sheaths //////////////////////////////////////////////////////

    local itemSets36 = {
        { baseItem = "Military_Holster_Defender", continuation = "_HipLeft" },
        { baseItem = "Military_Sheath_Defender", continuation = "_HipLeft" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets36, commonSuffixes, spawnChancesHolsterSheath, distributionNames)


    --////////////////////////////////////////////////////// Hats / Headsets / Gas Masks / Glasses ////////////////////////////////////////////////////// --
    local itemSets37 = {
        { baseItem = "Military_Glasses_ShatterShield"},
        { baseItem = "Military_MaskHelmet_GasMask-M80"}
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets37, emptySuffixes, spawnChancesHeadApparel, policePlusArmyDistributions)

    local itemSets48 = {
        { baseItem = "Military_Hat_WarComms" },
        { baseItem = "Military_Hat_Beret" },
        { baseItem = "Military_Hat_BaseballCapM" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets48, commonSuffixes, spawnChancesHeadApparel, policePlusArmyDistributions)


    --////////////////////////////////////////////////////// Balaclavas //////////////////////////////////////////////////////
    local itemSets38 = {
        { baseItem = "Military_Mask_Balaclava1" },
        { baseItem = "Military_Mask_Balaclava2" },
        { baseItem = "Military_Mask_Balaclava3" },
        { baseItem = "Military_Mask_BandanaMask" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets38, commonSuffixes, spawnChancesBalaclava, distributionNames)

    --////////////////////////////////////////////////////// Jackets //////////////////////////////////////////////////////
    local itemSets39 = {
        { baseItem = "Military_Jacket_Classic" },
        { baseItem = "Military_Jacket_Lightweight" },
        { baseItem = "Military_Jacket_WinterHood", continuation = "_DOWN" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets39, commonSuffixes, spawnChancesJacket, distributionNames)


    --////////////////////////////////////////////////////// Gloves //////////////////////////////////////////////////////
    local itemSets40 = {
        { baseItem = "Military_Gloves_Grips" },
        { baseItem = "Military_Gloves_GripFingerless" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets40, commonSuffixes, spawnChancesGloves, distributionNames)


    --////////////////////////////////////////////////////// Military T-shirts //////////////////////////////////////////////////////
    local itemSets41 = {
        { baseItem = "Military_Shirt_Classic" },
        { baseItem = "Military_Shirt_ShortSleeveShirt" },
        { baseItem = "Military_TShirt_TankTop" },
        { baseItem = "Military_TShirt_TankTopShort" },
        { baseItem = "Military_TShirt_TShirt", continuation = "_Man" },
        { baseItem = "Military_TShirt_TShirtShort" },
        { baseItem = "Military_TShirt_LongSleeve", continuation = "_Man" }, 
        { baseItem = "Military_TShirt_LongSleeveShort" },  
        { baseItem = "Military_TShirt_Sleeveless", continuation = "_Man" }, 
        { baseItem = "Military_TShirt_SleevelessShort" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets41, commonSuffixes, spawnChancesMilitaryTShirts, distributionNames)


    --////////////////////////////////////////////////////// Pants / Shorts //////////////////////////////////////////////////////
    local itemSets42 = {
        { baseItem = "Military_Pants_Classic" },
        { baseItem = "Military_Pants_Cargo" },
        { baseItem = "Military_Pants_Capri" },  
        { baseItem = "Military_Pants_ShortsCargo" },
        { baseItem = "Military_Pants_KneeLengthShorts" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets42, commonSuffixes, spawnChancesPantsShorts, distributionNames)


    --////////////////////////////////////////////////////// Boots / Shoes //////////////////////////////////////////////////////
    local itemSets43 = {
        { baseItem = "Military_Shoes_Boots" },
        { baseItem = "Military_Shoes_TacticalBoots" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets43, commonSuffixes, spawnChancesBootsShoes, distributionNames)


    --////////////////////////////////////////////////////// Thermal underwear //////////////////////////////////////////////////////
    local itemSets44 = {
        { baseItem = "Military_ThermalUnderwear" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets44, commonSuffixes, spawnChancesThermalUnderwear, distributionNames)


    --////////////////////////////////////////////////////// Non - Military Apparel //////////////////////////////////////////////////////
    local itemSets45 = {
        { baseItem = "Military_Stockings" },
        { baseItem = "Military_Skirt_KnifePleatedMini" },
        { baseItem = "Military_Skirt_KnifePleatedShort" },
        { baseItem = "Military_Skirt_KnifePleated" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets45, pressSuffixes, spawnChancesNonMilitary, storesDistributions)

    local itemSets46 = {
        { baseItem = "Military_TShirt_OneShoulder" }, 
        { baseItem = "Military_TShirt_OneShoulderShort" },
        { baseItem = "Military_TShirt_Neckholder" },
        { baseItem = "Military_TShirt_NeckholderShort" },
        { baseItem = "Military_Stockings" },
        { baseItem = "Military_Skirt_KnifePleated" },
        { baseItem = "Military_Skirt_KnifePleatedShort" },
        { baseItem = "Military_Skirt_KnifePleatedMini" },
        { baseItem = "Military_Pants_CapriSkinny" },
        { baseItem = "Military_Pants_ShortsMini" },
        { baseItem = "Military_Pants_Shorts" },
        { baseItem = "Military_Pants_KneeLengthShortsSkinny"},
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets46, commonSuffixes, spawnChancesNonMilitary, storesDistributions)


    --////////////////////////////////////////////////////// PRESS ////////////////////////////////////////////////////// --
    local itemSets47 = {
        { baseItem = "Military_LegsProtectionUpper_Patriot_Light" },
        { baseItem = "Military_BulletproofVest_Patriot_Light" },
        { baseItem = "Military_ArmsProtectionUpper_Defender_Medium" },
        { baseItem = "Military_Helmet_Patriot" }
    }
    KATTAJ1_insertItemSetsIntoDistributions(itemSets47, pressSuffixes, spawnChancesPatriotGear, policeDistributions)


    ItemPickerJava.Parse()
end


Events.OnInitGlobalModData.Add(KATTAJ1_initializeItemDistributionsSandboxOptions)