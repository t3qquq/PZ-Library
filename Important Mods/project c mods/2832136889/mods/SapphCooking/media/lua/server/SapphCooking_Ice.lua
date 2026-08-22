--[[sapph: hello! so... i spent like, 2 days in a row trying to add this code, unfortunally i`m dumb,so...
HUGE thanks to poltergeist, cause he basically did everything here! --]]

--this checks if the power is on.
local function CheckPoweredSquare(square)
	return (SandboxVars.AllowExteriorGenerator and square:haveElectricity() or (SandboxVars.ElecShutModifier > -1 and GameTime:getInstance():getNightsSurvived() < SandboxVars.ElecShutModifier))
end

--checks on the recipe if the player is near a fridge, and if the power is on.
function FridgeCraft(recipe, playerObj, item)
    local test = function(square) return square ~= nil and CheckPoweredSquare(square) and CheckFridge(square) end
    local square = playerObj:getSquare()
    return square ~= nil and ( test(square)
        or test(square:getAdjacentSquare(playerObj:getDir()))
        or test(square:getAdjacentSquare(playerObj:getDir():RotRight()))
        or test(square:getAdjacentSquare(playerObj:getDir():RotLeft()))
    )
end

--checks if the player is near a fridge.
function CheckFridge(square)
    return square:getContainerItem("fridge") ~= nil
end

--gives back an empty tray after completing the recipe.
function Recipe_IceTray(items, result, player)
    local inv = player:getInventory();
    inv:AddItem("SapphCooking.IceTray", 1);
end



-- will be putting this here, cause its easier to find.
--fixes the NaN issue - should reset the player stat IF they're NaN
--sapph(08/22/24): so - for some, unknown reason, the temperature doesn't change.
--will be fixing the other more importanty stuff (like calories), and i'll be looking for a fix in the future!

--sapph (11/14/24): okay- kinda of a fix, had lua errors because the game was attempting to get NaN tables.

--sapph (11/17/24):needs more testing, will be commenting this for now.

--function SapphOnEat_CheckNaN(food, character, percent)
--    local CalorieValue = getPlayer():getNutrition():getCalories();
--    if CalorieValue == nil then
--        getPlayer():getNutrition():setCalories(400);
--        getPlayer():getNutrition():setProteins(50);
--        getPlayer():getNutrition():setCarbohydrates(300);      
--        getPlayer():getNutrition():setLipids(200);
--    end
--end


