require 'NPCs/ZombiesZoneDefinition'

-- local KATTAJ1_BlackGearedZombies_SpawnChance_Army - local variable. goes only in this script
-- SandboxVars.KATTAJ1.BlackGearedZombiesArmy -- "KATTAJ1.BlackGearedZombiesArmy" is name of the option in sandbox-options.txt 

local function KATTAJ1_InitializeZombieSpawnSandboxOptions()


    --////////////////////////////////////////////////////// Black Geared Zombies Spawns //////////////////////////////////////////////////////
    local KATTAJ1_BlackGearedZombies_Patriot_SpawnChance_Army = SandboxVars.KATTAJ1.BlackGearedZombiesPatriotArmy
    local KATTAJ1_BlackGearedZombies_Defender_SpawnChance_Army = SandboxVars.KATTAJ1.BlackGearedZombiesDefenderArmy
    local KATTAJ1_BlackGearedZombies_Vanguard_SpawnChance_Army = SandboxVars.KATTAJ1.BlackGearedZombiesVanguardArmy
    table.insert(ZombiesZoneDefinition.Army, {name = "KATTAJ1_Army_Black_Patriot", chance = KATTAJ1_BlackGearedZombies_Patriot_SpawnChance_Army})
    table.insert(ZombiesZoneDefinition.Army, {name = "KATTAJ1_Army_Black_Defender", chance = KATTAJ1_BlackGearedZombies_Defender_SpawnChance_Army})
    table.insert(ZombiesZoneDefinition.Army, {name = "KATTAJ1_Army_Black_Vanguard", chance = KATTAJ1_BlackGearedZombies_Vanguard_SpawnChance_Army})

    local KATTAJ1_BlackGearedZombies_Patriot_SpawnChance_Default = SandboxVars.KATTAJ1.BlackGearedZombiesPatriotDefault
    local KATTAJ1_BlackGearedZombies_Defender_SpawnChance_Default = SandboxVars.KATTAJ1.BlackGearedZombiesDefenderDefault
    local KATTAJ1_BlackGearedZombies_Vanguard_SpawnChance_Default = SandboxVars.KATTAJ1.BlackGearedZombiesVanguardDefault
    table.insert(ZombiesZoneDefinition.Default,{name = "KATTAJ1_Army_Black_Patriot", chance= KATTAJ1_BlackGearedZombies_Patriot_SpawnChance_Default});
    table.insert(ZombiesZoneDefinition.Default,{name = "KATTAJ1_Army_Black_Defender", chance= KATTAJ1_BlackGearedZombies_Defender_SpawnChance_Default});
    table.insert(ZombiesZoneDefinition.Default,{name = "KATTAJ1_Army_Black_Vanguard", chance= KATTAJ1_BlackGearedZombies_Vanguard_SpawnChance_Default});

    local KATTAJ1_BlackGearedZombies_Patriot_SpawnChance_SecretBase = SandboxVars.KATTAJ1.BlackGearedZombiesPatriotSecretBase
    local KATTAJ1_BlackGearedZombies_Defender_SpawnChance_SecretBase = SandboxVars.KATTAJ1.BlackGearedZombiesDefenderSecretBase
    local KATTAJ1_BlackGearedZombies_Vanguard_SpawnChance_SecretBase = SandboxVars.KATTAJ1.BlackGearedZombiesVanguardSecretBase
    table.insert(ZombiesZoneDefinition.SecretBase,{name = "KATTAJ1_Army_Black_Patriot", chance= KATTAJ1_BlackGearedZombies_Patriot_SpawnChance_SecretBase});
    table.insert(ZombiesZoneDefinition.SecretBase,{name = "KATTAJ1_Army_Black_Defender", chance= KATTAJ1_BlackGearedZombies_Defender_SpawnChance_SecretBase});
    table.insert(ZombiesZoneDefinition.SecretBase,{name = "KATTAJ1_Army_Black_Vanguard", chance= KATTAJ1_BlackGearedZombies_Vanguard_SpawnChance_SecretBase});


    --////////////////////////////////////////////////////// Desert Geared Zombies Spawns //////////////////////////////////////////////////////
    local KATTAJ1_DesertGearedZombies_Patriot_SpawnChance_Army = SandboxVars.KATTAJ1.DesertGearedZombiesPatriotArmy
    local KATTAJ1_DesertGearedZombies_Defender_SpawnChance_Army = SandboxVars.KATTAJ1.DesertGearedZombiesDefenderArmy
    local KATTAJ1_DesertGearedZombies_Vanguard_SpawnChance_Army = SandboxVars.KATTAJ1.DesertGearedZombiesVanguardArmy
    table.insert(ZombiesZoneDefinition.Army, {name = "KATTAJ1_Army_Desert_Patriot", chance = KATTAJ1_DesertGearedZombies_Patriot_SpawnChance_Army})
    table.insert(ZombiesZoneDefinition.Army, {name = "KATTAJ1_Army_Desert_Defender", chance = KATTAJ1_DesertGearedZombies_Defender_SpawnChance_Army})
    table.insert(ZombiesZoneDefinition.Army, {name = "KATTAJ1_Army_Desert_Vanguard", chance = KATTAJ1_DesertGearedZombies_Vanguard_SpawnChance_Army})

    local KATTAJ1_DesertGearedZombies_Patriot_SpawnChance_Default = SandboxVars.KATTAJ1.DesertGearedZombiesPatriotDefault
    local KATTAJ1_DesertGearedZombies_Defender_SpawnChance_Default = SandboxVars.KATTAJ1.DesertGearedZombiesDefenderDefault
    local KATTAJ1_DesertGearedZombies_Vanguard_SpawnChance_Default = SandboxVars.KATTAJ1.DesertGearedZombiesVanguardDefault
    table.insert(ZombiesZoneDefinition.Default,{name = "KATTAJ1_Army_Desert_Patriot", chance= KATTAJ1_DesertGearedZombies_Patriot_SpawnChance_Default});
    table.insert(ZombiesZoneDefinition.Default,{name = "KATTAJ1_Army_Desert_Defender", chance= KATTAJ1_DesertGearedZombies_Defender_SpawnChance_Default});
    table.insert(ZombiesZoneDefinition.Default,{name = "KATTAJ1_Army_Desert_Vanguard", chance= KATTAJ1_DesertGearedZombies_Vanguard_SpawnChance_Default});

    local KATTAJ1_DesertGearedZombies_Patriot_SpawnChance_SecretBase = SandboxVars.KATTAJ1.DesertGearedZombiesPatriotSecretBase
    local KATTAJ1_DesertGearedZombies_Defender_SpawnChance_SecretBase = SandboxVars.KATTAJ1.DesertGearedZombiesDefenderSecretBase
    local KATTAJ1_DesertGearedZombies_Vanguard_SpawnChance_SecretBase = SandboxVars.KATTAJ1.DesertGearedZombiesVanguardSecretBase
    table.insert(ZombiesZoneDefinition.SecretBase,{name = "KATTAJ1_Army_Desert_Patriot", chance= KATTAJ1_DesertGearedZombies_Patriot_SpawnChance_SecretBase});
    table.insert(ZombiesZoneDefinition.SecretBase,{name = "KATTAJ1_Army_Desert_Defender", chance= KATTAJ1_DesertGearedZombies_Defender_SpawnChance_SecretBase});
    table.insert(ZombiesZoneDefinition.SecretBase,{name = "KATTAJ1_Army_Desert_Vanguard", chance= KATTAJ1_DesertGearedZombies_Vanguard_SpawnChance_SecretBase});


    --////////////////////////////////////////////////////// Green Geared Zombies Spawns //////////////////////////////////////////////////////
    local KATTAJ1_GreenGearedZombies_Patriot_SpawnChance_Army = SandboxVars.KATTAJ1.GreenGearedZombiesPatriotArmy
    local KATTAJ1_GreenGearedZombies_Defender_SpawnChance_Army = SandboxVars.KATTAJ1.GreenGearedZombiesDefenderArmy
    local KATTAJ1_GreenGearedZombies_Vanguard_SpawnChance_Army = SandboxVars.KATTAJ1.GreenGearedZombiesVanguardArmy
    table.insert(ZombiesZoneDefinition.Army, {name = "KATTAJ1_Army_Green_Patriot", chance = KATTAJ1_GreenGearedZombies_Patriot_SpawnChance_Army})
    table.insert(ZombiesZoneDefinition.Army, {name = "KATTAJ1_Army_Green_Defender", chance = KATTAJ1_GreenGearedZombies_Defender_SpawnChance_Army})
    table.insert(ZombiesZoneDefinition.Army, {name = "KATTAJ1_Army_Green_Vanguard", chance = KATTAJ1_GreenGearedZombies_Vanguard_SpawnChance_Army})

    local KATTAJ1_GreenGearedZombies_Patriot_SpawnChance_Default = SandboxVars.KATTAJ1.GreenGearedZombiesPatriotDefault
    local KATTAJ1_GreenGearedZombies_Defender_SpawnChance_Default = SandboxVars.KATTAJ1.GreenGearedZombiesDefenderDefault
    local KATTAJ1_GreenGearedZombies_Vanguard_SpawnChance_Default = SandboxVars.KATTAJ1.GreenGearedZombiesVanguardDefault
    table.insert(ZombiesZoneDefinition.Default,{name = "KATTAJ1_Army_Green_Patriot", chance= KATTAJ1_GreenGearedZombies_Patriot_SpawnChance_Default});
    table.insert(ZombiesZoneDefinition.Default,{name = "KATTAJ1_Army_Green_Defender", chance= KATTAJ1_GreenGearedZombies_Defender_SpawnChance_Default});
    table.insert(ZombiesZoneDefinition.Default,{name = "KATTAJ1_Army_Green_Vanguard", chance= KATTAJ1_GreenGearedZombies_Vanguard_SpawnChance_Default});

    local KATTAJ1_GreenGearedZombies_Patriot_SpawnChance_SecretBase = SandboxVars.KATTAJ1.GreenGearedZombiesPatriotSecretBase
    local KATTAJ1_GreenGearedZombies_Defender_SpawnChance_SecretBase = SandboxVars.KATTAJ1.GreenGearedZombiesDefenderSecretBase
    local KATTAJ1_GreenGearedZombies_Vanguard_SpawnChance_SecretBase = SandboxVars.KATTAJ1.GreenGearedZombiesVanguardSecretBase
    table.insert(ZombiesZoneDefinition.SecretBase,{name = "KATTAJ1_Army_Green_Patriot", chance= KATTAJ1_GreenGearedZombies_Patriot_SpawnChance_SecretBase});
    table.insert(ZombiesZoneDefinition.SecretBase,{name = "KATTAJ1_Army_Green_Defender", chance= KATTAJ1_GreenGearedZombies_Defender_SpawnChance_SecretBase});
    table.insert(ZombiesZoneDefinition.SecretBase,{name = "KATTAJ1_Army_Green_Vanguard", chance= KATTAJ1_GreenGearedZombies_Vanguard_SpawnChance_SecretBase});


    --////////////////////////////////////////////////////// White Geared Zombies Spawns //////////////////////////////////////////////////////
    local KATTAJ1_WhiteGearedZombies_Patriot_SpawnChance_Army = SandboxVars.KATTAJ1.WhiteGearedZombiesPatriotArmy
    local KATTAJ1_WhiteGearedZombies_Defender_SpawnChance_Army = SandboxVars.KATTAJ1.WhiteGearedZombiesDefenderArmy
    local KATTAJ1_WhiteGearedZombies_Vanguard_SpawnChance_Army = SandboxVars.KATTAJ1.WhiteGearedZombiesVanguardArmy
    table.insert(ZombiesZoneDefinition.Army, {name = "KATTAJ1_Army_White_Patriot", chance = KATTAJ1_WhiteGearedZombies_Patriot_SpawnChance_Army})
    table.insert(ZombiesZoneDefinition.Army, {name = "KATTAJ1_Army_White_Defender", chance = KATTAJ1_WhiteGearedZombies_Defender_SpawnChance_Army})
    table.insert(ZombiesZoneDefinition.Army, {name = "KATTAJ1_Army_White_Vanguard", chance = KATTAJ1_WhiteGearedZombies_Vanguard_SpawnChance_Army})

    local KATTAJ1_WhiteGearedZombies_Patriot_SpawnChance_Default = SandboxVars.KATTAJ1.WhiteGearedZombiesPatriotDefault
    local KATTAJ1_WhiteGearedZombies_Defender_SpawnChance_Default = SandboxVars.KATTAJ1.WhiteGearedZombiesDefenderDefault
    local KATTAJ1_WhiteGearedZombies_Vanguard_SpawnChance_Default = SandboxVars.KATTAJ1.WhiteGearedZombiesVanguardDefault
    table.insert(ZombiesZoneDefinition.Default,{name = "KATTAJ1_Army_White_Patriot", chance= KATTAJ1_WhiteGearedZombies_Patriot_SpawnChance_Default});
    table.insert(ZombiesZoneDefinition.Default,{name = "KATTAJ1_Army_White_Defender", chance= KATTAJ1_WhiteGearedZombies_Defender_SpawnChance_Default});
    table.insert(ZombiesZoneDefinition.Default,{name = "KATTAJ1_Army_White_Vanguard", chance= KATTAJ1_WhiteGearedZombies_Vanguard_SpawnChance_Default});

    local KATTAJ1_WhiteGearedZombies_Patriot_SpawnChance_SecretBase = SandboxVars.KATTAJ1.WhiteGearedZombiesPatriotSecretBase
    local KATTAJ1_WhiteGearedZombies_Defender_SpawnChance_SecretBase = SandboxVars.KATTAJ1.WhiteGearedZombiesDefenderSecretBase
    local KATTAJ1_WhiteGearedZombies_Vanguard_SpawnChance_SecretBase = SandboxVars.KATTAJ1.WhiteGearedZombiesVanguardSecretBase
    table.insert(ZombiesZoneDefinition.SecretBase,{name = "KATTAJ1_Army_White_Patriot", chance= KATTAJ1_WhiteGearedZombies_Patriot_SpawnChance_SecretBase});
    table.insert(ZombiesZoneDefinition.SecretBase,{name = "KATTAJ1_Army_White_Defender", chance= KATTAJ1_WhiteGearedZombies_Defender_SpawnChance_SecretBase});
    table.insert(ZombiesZoneDefinition.SecretBase,{name = "KATTAJ1_Army_White_Vanguard", chance= KATTAJ1_WhiteGearedZombies_Vanguard_SpawnChance_SecretBase});


    ItemPickerJava.Parse()
end


Events.OnInitGlobalModData.Add(KATTAJ1_InitializeZombieSpawnSandboxOptions)
