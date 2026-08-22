--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

-- Locations must be declared in render-order.
-- Location IDs must match BodyLocation= and CanBeEquipped= values in items.txt.
local group = BodyLocations.getGroup("Human")

-- Can use belt location from vanilla or write function to check rendering order...

group:getOrCreateLocation("HipLeft")
group:getOrCreateLocation("HipRight")

group:getOrCreateLocation("ChestRigs")
group:getOrCreateLocation("TacticalFannyPack")
group:setExclusive("TacticalFannyPack", "FannyPackBack")
group:getOrCreateLocation("TacticalFannyPackFront")
group:setExclusive("TacticalFannyPackFront", "FannyPackFront")

group:getOrCreateLocation("SkinnyPants")

group:getOrCreateLocation("Balaclava")
group:setExclusive("Balaclava", "Mask")


group:getOrCreateLocation("Headsets")


group:getOrCreateLocation("Mandible")
group:setExclusive("Mandible", "MaskFull")
group:setExclusive("Mandible", "FullHat")
group:setExclusive("Mandible", "FullSuitHead")



group:getOrCreateLocation("ShoulderPads")
group:getOrCreateLocation("HipProtection")
group:getOrCreateLocation("ElbowPads")
group:getOrCreateLocation("KneePads")


group:getOrCreateLocation("TorsoExtraPelvic")
group:getOrCreateLocation("TorsoExtraShoulder")
group:getOrCreateLocation("TorsoExtraShoulderP")
group:getOrCreateLocation("TorsoExtraFull")




group:setExclusive("TorsoExtraPelvic", "TorsoExtraShoulder")
group:setExclusive("TorsoExtraPelvic", "TorsoExtraShoulderP")
group:setExclusive("TorsoExtraPelvic", "TorsoExtraFull")
group:setExclusive("TorsoExtraPelvic", "FullSuitHead")
group:setExclusive("TorsoExtraPelvic", "FullSuit")
group:setExclusive("TorsoExtraPelvic", "FullTop")
group:setExclusive("TorsoExtraPelvic", "Boilersuit")
group:setExclusive("TorsoExtraPelvic", "BathRobe")
group:setExclusive("TorsoExtraPelvic", "BodyCostume")
group:setExclusive("TorsoExtraPelvic", "TorsoExtraVest")
group:setExclusive("TorsoExtraPelvic", "TorsoExtra")

group:setExclusive("TorsoExtraShoulder", "ShoulderPads")
group:setExclusive("TorsoExtraShoulder", "TorsoExtraPelvic")
group:setExclusive("TorsoExtraShoulder", "TorsoExtraShoulderP")
group:setExclusive("TorsoExtraShoulder", "TorsoExtraFull")
group:setExclusive("TorsoExtraShoulder", "FullSuitHead")
group:setExclusive("TorsoExtraShoulder", "FullSuit")
group:setExclusive("TorsoExtraShoulder", "FullTop")
group:setExclusive("TorsoExtraShoulder", "Boilersuit")
group:setExclusive("TorsoExtraShoulder", "BathRobe")
group:setExclusive("TorsoExtraShoulder", "BodyCostume")
group:setExclusive("TorsoExtraShoulder", "TorsoExtraVest")
group:setExclusive("TorsoExtraShoulder", "TorsoExtra")

group:setExclusive("TorsoExtraShoulderP", "ShoulderPads")
group:setExclusive("TorsoExtraShoulderP", "TorsoExtraPelvic")
group:setExclusive("TorsoExtraShoulderP", "TorsoExtraShoulderP")
group:setExclusive("TorsoExtraShoulderP", "TorsoExtraFull")
group:setExclusive("TorsoExtraShoulderP", "FullSuitHead")
group:setExclusive("TorsoExtraShoulderP", "FullSuit")
group:setExclusive("TorsoExtraShoulderP", "FullTop")
group:setExclusive("TorsoExtraShoulderP", "Boilersuit")
group:setExclusive("TorsoExtraShoulderP", "BathRobe")
group:setExclusive("TorsoExtraShoulderP", "BodyCostume")
group:setExclusive("TorsoExtraShoulderP", "TorsoExtraVest")
group:setExclusive("TorsoExtraShoulderP", "TorsoExtra")

group:setExclusive("TorsoExtraFull", "ShoulderPads")
group:setExclusive("TorsoExtraFull", "HipProtection")
group:setExclusive("TorsoExtraFull", "TorsoExtraPelvic")
group:setExclusive("TorsoExtraFull", "TorsoExtraShoulder")
group:setExclusive("TorsoExtraFull", "TorsoExtraShoulderP")
group:setExclusive("TorsoExtraFull", "FullSuitHead")
group:setExclusive("TorsoExtraFull", "FullSuit")
group:setExclusive("TorsoExtraFull", "FullTop")
group:setExclusive("TorsoExtraFull", "Boilersuit")
group:setExclusive("TorsoExtraFull", "BathRobe")
group:setExclusive("TorsoExtraFull", "BodyCostume")
group:setExclusive("TorsoExtraFull", "TorsoExtraVest")
group:setExclusive("TorsoExtraFull", "TorsoExtra")


group:setHideModel("TorsoExtraPelvic", "TacticalFannyPackFront")
group:setHideModel("TorsoExtraShoulderP", "TacticalFannyPackFront")
group:setHideModel("TorsoExtraFull", "TacticalFannyPackFront")