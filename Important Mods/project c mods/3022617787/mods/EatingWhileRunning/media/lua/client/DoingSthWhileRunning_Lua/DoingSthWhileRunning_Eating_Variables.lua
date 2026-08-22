--Made by SportXAI
local function SetVariable(player)
	local actions = player:getCharacterActions()
	for index = 0, actions:size()-1 do
		local action = actions:get(index)
		local actionName = action:getMetaType()
		if not player:hasFootInjury() then
			--Equip
			if actionName == "ISEatFoodAction" then
				player:setVariable("DoingSthWhileRunning_Eating_Permit",true)
			elseif actionName == "ISDrinkFromBottle" then
				player:setVariable("DoingSthWhileRunning_Eating_Permit",true)
			end
		end
	end

	--Settings for player
	local key = getCore():getKey("Run")
	if not getCore():isToggleToRun() then
		if player:getVariableBoolean("DoingSthWhileRunning_Eating_Permit") then
			player:setAllowRun(false)
			if isKeyDown(key) then
				player:setVariable("DoingSthWhileRunning_Eating_Enable",true)
			else
				player:setVariable("DoingSthWhileRunning_Eating_Enable",false)
			end
		else
			player:setAllowRun(true)
		end
	end

	if not player:hasTimedActions() then
		player:setVariable("iRunAndEquip_ToggleToRun",false)  --For not toggle to run player
		if player:getVariableBoolean("DoingSthWhileRunning_Eating_Permit") then
			if not getCore():isToggleToRun() then
				player:setVariable("DoingSthWhileRunning_Eating_Enable",false)
				player:setVariable("DoingSthWhileRunning_Eating_Permit",false)
			else  --Toggle to run
				if player:getVariableBoolean("DoingSthWhileRunning_Eating_Enable") then
					player:setRunning(true)
					player:setForceRun(true)
					player:setVariable("DoingSthWhileRunning_Eating_Enable",false)
					player:setVariable("DoingSthWhileRunning_Eating_Permit",false)
				end
			end
		end
	end
end

Events.OnPlayerUpdate.Add(SetVariable)