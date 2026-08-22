--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "Vehicles/TimedActions/ISRepairEngine"
-----------------------------------------------------------------------------------------------------------------------------------------------------------
local upperLayer = {}
upperLayer.ISRepairEngine = {}
-----------------------------------------------------------------------------------------------------------------------------------------------------------
upperLayer.ISRepairEngine.new = ISRepairEngine.new
function ISRepairEngine:new(character, part, item, time)
    local o = upperLayer.ISRepairEngine.new(self,character, part, item, time)
    local mecaSkill = character:getPerkLevel(Perks.Mechanics)
    local mlty = SandboxVars.addMecanicSound.timeMultiplier*10
    local skillBonus = mecaSkill*mlty
    local timeMalus = (12*mlty) -skillBonus
    if not ISVehicleMechanics.cheat then o.maxTime = o.maxTime + timeMalus end
    return o
end
