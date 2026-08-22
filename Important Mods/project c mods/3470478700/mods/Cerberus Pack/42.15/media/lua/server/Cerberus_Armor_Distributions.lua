----------------------------------------------------------------------------
---------------------------------- TTAJ1bl4 --------------------------------
----------------------------------------------------------------------------

require "Items/ProceduralDistributions"


function Cerberus_insertItemSetsIntoDistributions(itemSets, suffixes, spawnChances, distributionNames)
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


function Cerberus_initializeItemDistributionsSandboxOptions()

    -- Sandbox affected
    local commonSuffixes = {""} 


    -- Sandbox Spawn Chances
    local spawnChancesBulletproofVestCerberus = {SandboxVars.Cerberus.BulletproofVest_Cerberus}
    local spawnChancesBulletproofVestCerberusHighMobilityKit = {SandboxVars.Cerberus.BulletproofVest_Cerberus_HighMobilityKit}
    local spawnChanceBulletproofVestCerberusAssaultLightKit = {SandboxVars.Cerberus.BulletproofVest_Cerberus_AssaultLightKit}
    local spawnChancesBulletproofVestCerberusAssaultKit = {SandboxVars.Cerberus.BulletproofVest_Cerberus_AssaultKit}
    local spawnChanceBulletproofVestCerberusFullProtectionKit = {SandboxVars.Cerberus.BulletproofVest_Cerberus_FullProtectionKit}

    local spawnChancesHelmet = {SandboxVars.Cerberus.Helmet}
    local spawnChancesHelmetMandible = {SandboxVars.Cerberus.Helmet_Mandible}
    local spawnChancesMaskFullBallisticMask = {SandboxVars.Cerberus.MaskFull_BallisticMask}
    local spawnChancesElbowPads = {SandboxVars.Cerberus.ElbowPads}
    local spawnChancesKneePads = {SandboxVars.Cerberus.KneePads}

    local spawnChancesHipBag = {SandboxVars.Cerberus.HipBag}
    local spawnChancesFannyPack = {SandboxVars.Cerberus.FannyPack}
    local spawnChancesChestRigs = {SandboxVars.Cerberus.ChestRigs}
    local spawnChancesBackpack = {SandboxVars.Cerberus.Backpack}

    local spawnChancesHeadApparel = {SandboxVars.Cerberus.HeadApparel}
    local spawnChancesJacket = {SandboxVars.Cerberus.Jacket}
    local spawnChancesGloves = {SandboxVars.Cerberus.Gloves}
    local spawnChancesMilitaryTShirts = {SandboxVars.Cerberus.MilitaryTShirts}
    local spawnChancesPantsShorts = {SandboxVars.Cerberus.PantsShorts}
    local spawnChancesBootsShoes = {SandboxVars.Cerberus.BootsShoes}
    local spawnChancesNonMilitary = {SandboxVars.Cerberus.NonMilitary}


    -- Spawn Places Pulls 
    local distributionNames = {"ArmySurplusOutfit", "LockerArmyBedroom", "ArmyStorageOutfit", "GunStoreCounter"} 
    local policePlusArmyDistributions = {"ArmySurplusOutfit", "LockerArmyBedroom", "ArmyStorageOutfit","PoliceStorageOutfit", "PoliceLockers", "PoliceStorageGuns", "GunStoreCounter"} 
    local policeDistributions = {"PoliceStorageOutfit", "PoliceLockers", "PoliceStorageGuns"} 
    local storesDistributions = {"ClothingStorageLegwear", "ClothingStoresJeans", "ClothingStoresDress","ClothingStoresJackets","ClothingStoresJacketsFormal","ClothingStoresJumpers","ClothingStoresOvershirts","ClothingStoresPants","ClothingStoresPantsFormal","ClothingStoresShirts","ClothingStoresShirtsFormal","ClothingStoresSport","ClothingStoresSummer","ClothingStorageAllJackets","ClothingStorageAllShirts"}




    --////////////////////////////////////////////////////// SETS ////////////////////////////////////////////////////// --
    -- VESTS
    local itemSets = { 
        { baseItem = "BulletproofVest_Cerberus" }
    } 
    Cerberus_insertItemSetsIntoDistributions(itemSets, commonSuffixes, spawnChancesBulletproofVestCerberus, distributionNames)

    local itemSets2 = { 
        { baseItem = "BulletproofVest_Cerberus_HighMobilityKit" }
    } 
    Cerberus_insertItemSetsIntoDistributions(itemSets2, commonSuffixes, spawnChancesBulletproofVestCerberusHighMobilityKit, distributionNames)

    local itemSets3 = { 
        { baseItem = "BulletproofVest_Cerberus_AssaultLightKit" }
    } 
    Cerberus_insertItemSetsIntoDistributions(itemSets3, commonSuffixes, spawnChanceBulletproofVestCerberusAssaultLightKit, distributionNames)

    local itemSets4 = { 
        { baseItem = "BulletproofVest_Cerberus_AssaultKit" }
    } 
    Cerberus_insertItemSetsIntoDistributions(itemSets4, commonSuffixes, spawnChancesBulletproofVestCerberusAssaultKit, distributionNames)

    local itemSets5 = { 
        { baseItem = "BulletproofVest_Cerberus_FullProtectionKit" }
    } 
    Cerberus_insertItemSetsIntoDistributions(itemSets5, commonSuffixes, spawnChanceBulletproofVestCerberusFullProtectionKit, distributionNames)




    -- OTHEER ARMOR PIECES
    local itemSets6 = { 
        { baseItem = "Helmet_Cerberus" }
    } 
    Cerberus_insertItemSetsIntoDistributions(itemSets6, commonSuffixes, spawnChancesHelmet, distributionNames)

    local itemSets7 = { 
        { baseItem = "Helmet_Mandible_Cerberus" }
    } 
    Cerberus_insertItemSetsIntoDistributions(itemSets7, commonSuffixes, spawnChancesHelmetMandible, distributionNames)

    local itemSets8 = { 
        { baseItem = "MaskFull_BallisticMask_Cerberus" },
        { baseItem = "MaskFull_BallisticMask_Cerberus-Blood" }
    } 
    Cerberus_insertItemSetsIntoDistributions(itemSets8, commonSuffixes, spawnChancesMaskFullBallisticMask, distributionNames)

    local itemSets9 = { 
        { baseItem = "ElbowPads_Cerberus" }
    } 
    Cerberus_insertItemSetsIntoDistributions(itemSets9, commonSuffixes, spawnChancesElbowPads, distributionNames)

    local itemSets10 = { 
        { baseItem = "KneePads_Cerberus" }
    } 
    Cerberus_insertItemSetsIntoDistributions(itemSets10, commonSuffixes, spawnChancesKneePads, distributionNames)




    -- BAGS
    local itemSets11 = { 
        { baseItem = "HipBag_Cerberus_Left" }
    } 
    Cerberus_insertItemSetsIntoDistributions(itemSets11, commonSuffixes, spawnChancesHipBag, distributionNames)

    local itemSets12 = { 
        { baseItem = "FannyPack_Cerberus" }
    } 
    Cerberus_insertItemSetsIntoDistributions(itemSets12, commonSuffixes, spawnChancesFannyPack, distributionNames)

    local itemSets13 = { 
        { baseItem = "ChestRigs_Cerberus" }
    } 
    Cerberus_insertItemSetsIntoDistributions(itemSets13, commonSuffixes, spawnChancesChestRigs, distributionNames)

    local itemSets14 = { 
        { baseItem = "Backpack_Cerberus" }
    } 
    Cerberus_insertItemSetsIntoDistributions(itemSets14, commonSuffixes, spawnChancesBackpack, distributionNames)




    -- OTHER CLOTHES
    local itemSets15 = { 
        { baseItem = "Headsets_Cerberus" },
        { baseItem = "Hat_BaseballCap_Cerberus" },
        { baseItem = "Glasses_Goggles_Cerberus" },
        { baseItem = "Glasses_Tactical_Cerberus" },
        { baseItem = "Mask_Balaclava_Cerberus" },
        { baseItem = "Mask_BandanaMask_Cerberus" }
    } 
    Cerberus_insertItemSetsIntoDistributions(itemSets15, commonSuffixes, spawnChancesHeadApparel, distributionNames)

    local itemSets16 = { 
        { baseItem = "Jacket_Lightweight_Cerberus" }
    } 
    Cerberus_insertItemSetsIntoDistributions(itemSets16, commonSuffixes, spawnChancesJacket, distributionNames)

    local itemSets17 = { 
        { baseItem = "Gloves_Cerberus" },
        { baseItem = "GlovesFingerless_Cerberus" }
    } 
    Cerberus_insertItemSetsIntoDistributions(itemSets17, commonSuffixes, spawnChancesGloves, distributionNames)

    local itemSets18 = { 
        { baseItem = "Shirt_Cerberus" },
        { baseItem = "Shirt_ShortSleeve_Cerberus" },
        { baseItem = "Shirt_TankTop_Cerberus" },
        { baseItem = "Shirt_TankTopShort_Cerberus" },
        { baseItem = "Shirt_Tshirt_Cerberus" },
        { baseItem = "Shirt_TshirtShort_Cerberus" }
    } 
    Cerberus_insertItemSetsIntoDistributions(itemSets18, commonSuffixes, spawnChancesMilitaryTShirts, distributionNames)

    local itemSets19 = { 
        { baseItem = "Pants_Classic_Cerberus" },
        { baseItem = "Pants_Skinny_Cerberus" }
    } 
    Cerberus_insertItemSetsIntoDistributions(itemSets19, commonSuffixes, spawnChancesPantsShorts, distributionNames)

    local itemSets20 = { 
        { baseItem = "Shoes_TacticalBoots_Cerberus" }
    } 
    Cerberus_insertItemSetsIntoDistributions(itemSets20, commonSuffixes, spawnChancesBootsShoes, distributionNames)

    local itemSets21 = { 
        { baseItem = "Shirt_Neckholder_Cerberus" },
        { baseItem = "Shirt_NeckholderShort_Cerberus" },
        { baseItem = "Pants_Shorts_Cerberus" },
        { baseItem = "Stockings_Cerberus" },
        { baseItem = "Tights_Cerberus" }
    } 
    Cerberus_insertItemSetsIntoDistributions(itemSets21, commonSuffixes, spawnChancesNonMilitary, storesDistributions)

    ItemPickerJava.Parse()
end


Events.OnInitGlobalModData.Add(Cerberus_initializeItemDistributionsSandboxOptions)











