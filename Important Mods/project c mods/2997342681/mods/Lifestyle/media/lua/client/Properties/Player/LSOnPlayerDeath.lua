--------------------------------------------------------------------------------------------------
--		----	  |			  |			|		 |				|    --    |      ----			--
--		----	  |			  |			|		 |				|    --	   |      ----			--
--		----	  |		-------	   -----|	 ---------		-----          -      ----	   -------
--		----	  |			---			|		 -----		------        --      ----			--
--		----	  |			---			|		 -----		-------	 	 ---      ----			--
--		----	  |		-------	   ----------	 -----		-------		 ---      ----	   -------
--			|	  |		-------			|		 -----		-------		 ---		  |			--
--			|	  |		-------			|	 	 -----		-------		 ---		  |			--
--------------------------------------------------------------------------------------------------

--[[
local function removeZenBonus(thisPlayer, playerData)
	if FitnessExercises and FitnessExercises.exercisesType then
		for k, v in pairs(FitnessExercises.exercisesType) do
			if v and v.xpMod then
				local val = playerData.LSZenActive[k]
				print("removeZenBonus - key value "..tostring(k).." was: "..tostring(v.xpMod).." and is now:"..tostring(val))
				if val then v.xpMod = val; end
			end
		end
		playerData.LSZenActive = nil
	end
end

local function LSOnPlayerDeath(player)
	if not player then return; end
	if player:hasModData() then
		local playerData = player:getModData()
		if playerData.LSZenActive then removeZenBonus(player, playerData); end
	end
end

Events.OnPlayerDeath.Add(LSOnPlayerDeath)
]]--