if getActivatedMods():contains("ItemTweakerAPI") then
require("ItemTweaker_Core");
else return end

-- Icons
TweakItem("Base.Axe", "Icon", "HBVW_Axe")
TweakItem("Base.HandAxe", "Icon", "HBVW_HandAxe")
TweakItem("Base.Crowbar", "Icon", "HBVW_Crowbar")
TweakItem("Base.Sledgehammer", "Icon", "HBVW_Sledgehammer")
TweakItem("Base.HuntingKnife", "Icon", "HBVW_HuntingKnife")
TweakItem("Base.SpearHuntingKnife", "Icon", "HBVW_SpearHuntingKnife")
TweakItem("Base.Machete", "Icon", "HBVW_Machete")
TweakItem("Base.SpearMachete", "Icon", "HBVW_SpearMachete")

-- Models
TweakItem("Base.MetalBar", "WeaponSprite", "Base.MetalBar")
TweakItem("Base.LeadPipe", "WeaponSprite", "Base.PipeLead")