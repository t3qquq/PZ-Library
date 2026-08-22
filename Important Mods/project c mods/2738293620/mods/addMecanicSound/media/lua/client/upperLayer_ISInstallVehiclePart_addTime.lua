--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "Vehicles/TimedActions/ISInstallVehiclePart"
-----------------------------------------------------------------------------------------------------------------------------------------------------------
local upperLayer = {}
upperLayer.ISInstallVehiclePart = {}
-----------------------------------------------------------------------------------------------------------------------------------------------------------
upperLayer.ISInstallVehiclePart.new = ISInstallVehiclePart.new
function ISInstallVehiclePart:new(character, part, item, time)
    local o = upperLayer.ISInstallVehiclePart.new(self,character, part, item, time)
    local mecaSkill = character:getPerkLevel(Perks.Mechanics)
    local mlty = SandboxVars.addMecanicSound.timeMultiplier*10
    local skillBonus = mecaSkill*mlty
    local timeMalus = (12*mlty) -skillBonus
    if not ISVehicleMechanics.cheat then o.maxTime = o.maxTime + timeMalus end
    return o
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
