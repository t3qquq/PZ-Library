KAMER_WallHealthSettings = KAMER_WallHealthSettings or {}
KAMER_WallHealthSettings.immersive = false
KAMER_WallHealthSettings.Text = {}

function KAMER_WallHealthSettings.init()
    KAMER_WallHealthSettings.immersive = SandboxVars.Kamer.WallHealthSettings_immersive or false
    KAMER_WallHealthSettings.Text = {
        [20] = {
            [1] = "ContextMenu_Kamer_ShowWallHealth_ImmersiveHealth1",
            [2] = "ContextMenu_Kamer_ShowWallHealth_ImmersiveHealthLine1",
            [3] = "<RGB:0.922,0.306,0.345>" -- red
        },
        [40]  = {
            [1] = "ContextMenu_Kamer_ShowWallHealth_ImmersiveHealth2",
            [2] = "ContextMenu_Kamer_ShowWallHealth_ImmersiveHealthLine2",
    
        },
        [60]  = {
            [1] = "ContextMenu_Kamer_ShowWallHealth_ImmersiveHealth3",
            [2] = "ContextMenu_Kamer_ShowWallHealth_ImmersiveHealthLine3",
    
        },
        [80]  = {
            [1] = "ContextMenu_Kamer_ShowWallHealth_ImmersiveHealth4",
            [2] = "ContextMenu_Kamer_ShowWallHealth_ImmersiveHealthLine4",
    
        },
        [1000]  = {
            [1] = "ContextMenu_Kamer_ShowWallHealth_ImmersiveHealth5",
            [2] = "ContextMenu_Kamer_ShowWallHealth_ImmersiveHealthLine5",
            [3] = "<RGB:0.306,0.922,0.388>" -- green
        },
    }
end

-- Events.OnInitGlobalModData.Add(KAMER_WallHealthSettings.init)