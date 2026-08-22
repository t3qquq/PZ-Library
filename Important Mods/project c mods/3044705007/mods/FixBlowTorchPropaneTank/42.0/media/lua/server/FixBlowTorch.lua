local function FixBlowTorch()
	local item = ScriptManager.instance:getItem("Base.BlowTorch")
	if item:getUseDelta() > 0.01 then
		item:DoParam("UseDelta = 0.01")
	end

	local body = 
	[[{
		OnCreate = Recipe.OnCreate.RefillBlowTorch,
		OnTest = Recipe.OnTest.RefillBlowTorch,
		}
	}]]

	local Recipe = getScriptManager():getCraftRecipe("RefillBlowTorch")
	Recipe:Load("RefillBlowTorch", body)
end

Events.OnInitGlobalModData.Add(FixBlowTorch)