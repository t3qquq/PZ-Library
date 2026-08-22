--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************


local KATTAJ1Locations = {
  "KATTAJ1BeltLeft",
  "KATTAJ1BeltRight",
  "KATTAJ1BeltBackLeft", -- Extra locations used for back knife sheath
  "KATTAJ1BeltBackRight", -- Extra locations used for back knife sheath
  "KATTAJ1UpperLegs",
  "KATTAJ1LowerLegs",
  "KATTAJ1Knees",
  "KATTAJ1UpperArms",
  "KATTAJ1LowerArms",
  "KATTAJ1Elbows",
  "KATTAJ1BackFanny",
  "KATTAJ1Balaclava"
}

local group = BodyLocations.getGroup("Human")
for _, location in ipairs(KATTAJ1Locations) do
    local bodyLocation = BodyLocation.new(group, location)
    group:getAllLocations():add(bodyLocation)
end

local KATTAJ1ExtraLocations = {
  "KATTAJ1_ChestRig",
  "TacticalFannyPack",
  "TacticalFannyPackFront",
  "SkinnyPants",
  "Balaclava", 
  "Headsets",
  "Mandible",
  "ShoulderPads",
  "HipProtection",
  "TorsoExtraPelvic",
  "TorsoExtraShoulder",
  "TorsoExtraShoulderP",
  "TorsoExtraFull"
}

local group = BodyLocations.getGroup("Human")
for _, location in ipairs(KATTAJ1ExtraLocations) do
    local bodyLocation = BodyLocation.new(group, location)
    group:getAllLocations():add(bodyLocation)
end





group:setExclusive("TacticalFannyPack", "FannyPackBack")
group:setExclusive("TacticalFannyPackFront", "FannyPackFront")


group:setExclusive("Balaclava", "Mask")



group:setExclusive("Mandible", "MaskFull")
group:setExclusive("Mandible", "FullHat")
group:setExclusive("Mandible", "FullSuitHead")



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
group:setExclusive("TorsoExtraPelvic", "TorsoExtraVestBullet")

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
group:setExclusive("TorsoExtraShoulder", "TorsoExtraVestBullet")


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
group:setExclusive("TorsoExtraShoulderP", "TorsoExtraVestBullet")

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
group:setExclusive("TorsoExtraFull", "TorsoExtraVestBullet")


group:setHideModel("TorsoExtraPelvic", "TacticalFannyPackFront")
group:setHideModel("TorsoExtraShoulderP", "TacticalFannyPackFront")
group:setHideModel("TorsoExtraFull", "TacticalFannyPackFront")

------------------------------------------------------------


------------------------------------------------------------


------------------------------------------------------------
-- B42 Slots Exclusions
group:setExclusive("KATTAJ1UpperLegs", "Thigh_Left")
group:setExclusive("KATTAJ1UpperLegs", "Thigh_Right")
group:setExclusive("KATTAJ1LowerLegs", "Knee_Left")
group:setExclusive("KATTAJ1LowerLegs", "Knee_Right")
group:setExclusive("KATTAJ1LowerLegs", "Calf_Left")
group:setExclusive("KATTAJ1LowerLegs", "Calf_Right")

group:setExclusive("KATTAJ1Knees", "Knee_Left")
group:setExclusive("KATTAJ1Knees", "Knee_Right")
group:setExclusive("KATTAJ1Knees", "KATTAJ1LowerLegs")


------------------------------------------------------------
-- B42 Slots Exclusions
group:setExclusive("KATTAJ1UpperArms", "ShoulderpadLeft")
group:setExclusive("KATTAJ1UpperArms", "ShoulderpadRight")
group:setExclusive("KATTAJ1LowerArms", "Elbow_Left")
group:setExclusive("KATTAJ1LowerArms", "Elbow_Right")
group:setExclusive("KATTAJ1LowerArms", "ForeArm_Left")
group:setExclusive("KATTAJ1LowerArms", "ForeArm_Right")

group:setExclusive("KATTAJ1Elbows", "Elbow_Left")
group:setExclusive("KATTAJ1Elbows", "Elbow_Right")
group:setExclusive("KATTAJ1Elbows", "KATTAJ1LowerArms")

