if (getActivatedMods():contains("XnerTree")) then
	local function setPlantingValues(planting, nbOfGrow, growFunction, nextGrowing, updateNbOfGrow)
		if (planting.nbOfGrow > nbOfGrow) then
			planting.nbOfGrow = nbOfGrow
			nextGrowing = planting.nextGrowing + (SandboxVars.CropsNeverRot.DiseaseCheckTime or 48.0)
		end
		planting = growFunction(planting, nextGrowing, updateNbOfGrow)

		return planting
	end

	local function overrideXnGrowTrees()
		local growTrees = XNTrees.growTrees
		XNTrees.growTrees =  function(planting, nextGrowing, updateNbOfGrow)
			return setPlantingValues(planting, 6, growTrees, nextGrowing, updateNbOfGrow)
		end

		local growPineapple = XNTrees.growPineapple
		XNTrees.growPineapple =  function(planting, nextGrowing, updateNbOfGrow)
			return setPlantingValues(planting, 6, growPineapple, nextGrowing, updateNbOfGrow)
		end

		local growOrange = XNTrees.growOrange
		XNTrees.growOrange =  function(planting, nextGrowing, updateNbOfGrow)
			return setPlantingValues(planting, 6, growOrange, nextGrowing, updateNbOfGrow)
		end
	end

	Events.OnGameStart.Add(overrideXnGrowTrees)
end
