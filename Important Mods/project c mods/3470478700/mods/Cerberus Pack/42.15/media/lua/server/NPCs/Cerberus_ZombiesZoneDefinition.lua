
require 'NPCs/ZombiesZoneDefinition'

-- local Cerberus_BlackGearedZombies_SpawnChance_Army - local variable. goes only in this script
-- SandboxVars.KATTAJ1.BlackGearedZombiesArmy -- "KATTAJ1.BlackGearedZombiesArmy" is name of the option in sandbox-options.txt 

local function Cerberus_InitializeZombieSpawnSandboxOptions()

    --////////////////////////////////////////////////////// Unifrormed Zombies Spawns //////////////////////////////////////////////////////
    local Cerberus_UnifrormZombies_SpawnChance_Default = SandboxVars.Cerberus.UnifrormZombiesDefault
    local Cerberus_UnifrormZombies_SpawnChance_Police = SandboxVars.Cerberus.UnifrormZombiesPolice
    local Cerberus_UnifrormZombies_SpawnChance_Army = SandboxVars.Cerberus.UnifrormZombiesArmy
    local Cerberus_UnifrormZombies_SpawnChance_SecretBase = SandboxVars.Cerberus.UnifrormZombiesSecretBase

    table.insert(ZombiesZoneDefinition.Default, {name = "Cerberus_NonArmoredUniformed", chance = Cerberus_UnifrormZombies_SpawnChance_Default})
    table.insert(ZombiesZoneDefinition.Police, {name = "Cerberus_NonArmoredUniformed", chance = Cerberus_UnifrormZombies_SpawnChance_Police})
    table.insert(ZombiesZoneDefinition.PoliceState, {name = "Cerberus_NonArmoredUniformed", chance = Cerberus_UnifrormZombies_SpawnChance_Police})
    table.insert(ZombiesZoneDefinition.Army, {name = "Cerberus_NonArmoredUniformed", chance = Cerberus_UnifrormZombies_SpawnChance_Army})
    table.insert(ZombiesZoneDefinition.SecretBase, {name = "Cerberus_NonArmoredUniformed", chance = Cerberus_UnifrormZombies_SpawnChance_SecretBase})




    --////////////////////////////////////////////////////// Light Armored Zombies Spawns //////////////////////////////////////////////////////
    local Cerberus_LightArmoredZombies_SpawnChance_Default = SandboxVars.Cerberus.LightArmoredZombiesDefault
    local Cerberus_LightArmoredZombies_SpawnChance_Police = SandboxVars.Cerberus.LightArmoredZombiesPolice
    local Cerberus_LightArmoredZombies_SpawnChance_Army = SandboxVars.Cerberus.LightArmoredZombiesArmy
    local Cerberus_LightArmoredZombies_SpawnChance_SecretBase = SandboxVars.Cerberus.LightArmoredZombiesSecretBase

    table.insert(ZombiesZoneDefinition.Default, {name = "Cerberus_LightArmored", chance = Cerberus_LightArmoredZombies_SpawnChance_Default})
    table.insert(ZombiesZoneDefinition.Police, {name = "Cerberus_LightArmored", chance = Cerberus_LightArmoredZombies_SpawnChance_Police})
    table.insert(ZombiesZoneDefinition.PoliceState, {name = "Cerberus_LightArmored", chance = Cerberus_LightArmoredZombies_SpawnChance_Police})
    table.insert(ZombiesZoneDefinition.Army, {name = "Cerberus_LightArmored", chance = Cerberus_LightArmoredZombies_SpawnChance_Army})
    table.insert(ZombiesZoneDefinition.SecretBase, {name = "Cerberus_LightArmored", chance = Cerberus_LightArmoredZombies_SpawnChance_SecretBase})




    --////////////////////////////////////////////////////// Medium Armored Zombies Spawns //////////////////////////////////////////////////////
    local Cerberus_MediumArmored_SpawnChance_Default = SandboxVars.Cerberus.MediumArmoredZombiesDefault
    local Cerberus_MediumArmored_SpawnChance_Police = SandboxVars.Cerberus.MediumArmoredZombiesPolice
    local Cerberus_MediumArmored_SpawnChance_Army = SandboxVars.Cerberus.MediumArmoredZombiesArmy
    local Cerberus_MediumArmored_SpawnChance_SecretBase = SandboxVars.Cerberus.MediumArmoredZombiesSecretBase

    table.insert(ZombiesZoneDefinition.Default, {name = "Cerberus_MediumArmored", chance =Cerberus_MediumArmored_SpawnChance_Default})
    table.insert(ZombiesZoneDefinition.Police, {name = "Cerberus_MediumArmored", chance = Cerberus_MediumArmored_SpawnChance_Police})
    table.insert(ZombiesZoneDefinition.PoliceState, {name = "Cerberus_MediumArmored", chance = Cerberus_MediumArmored_SpawnChance_Police})
    table.insert(ZombiesZoneDefinition.Army, {name = "Cerberus_MediumArmored", chance = Cerberus_MediumArmored_SpawnChance_Army})
    table.insert(ZombiesZoneDefinition.SecretBase, {name = "Cerberus_MediumArmored", chance = Cerberus_MediumArmored_SpawnChance_SecretBase})




    --////////////////////////////////////////////////////// Heavy Armored Zombies Spawns //////////////////////////////////////////////////////
    local Cerberus_HeavyArmored_SpawnChance_Default = SandboxVars.Cerberus.HeavyArmoredZombiesDefault
    local Cerberus_HeavyArmored_SpawnChance_Police = SandboxVars.Cerberus.HeavyArmoredZombiesPolice
    local Cerberus_HeavyArmored_SpawnChance_Army = SandboxVars.Cerberus.HeavyArmoredZombiesArmy
    local Cerberus_HeavyArmored_SpawnChance_SecretBase = SandboxVars.Cerberus.HeavyArmoredZombiesSecretBase

    table.insert(ZombiesZoneDefinition.Default, {name = "Cerberus_HeavyArmored", chance = Cerberus_HeavyArmored_SpawnChance_Default})
    table.insert(ZombiesZoneDefinition.Police, {name = "Cerberus_HeavyArmored", chance = Cerberus_HeavyArmored_SpawnChance_Police})
    table.insert(ZombiesZoneDefinition.PoliceState, {name = "Cerberus_HeavyArmored", chance = Cerberus_HeavyArmored_SpawnChance_Police})
    table.insert(ZombiesZoneDefinition.Army, {name = "Cerberus_HeavyArmored", chance = Cerberus_HeavyArmored_SpawnChance_Army})
    table.insert(ZombiesZoneDefinition.SecretBase, {name = "Cerberus_HeavyArmored", chance = Cerberus_HeavyArmored_SpawnChance_SecretBase})


    ItemPickerJava.Parse()
end


Events.OnInitGlobalModData.Add(Cerberus_InitializeZombieSpawnSandboxOptions)


