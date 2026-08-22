--Made by SportXAI
--[[
local function SetVariable(player)
	local actions = player:getCharacterActions()
	for index = 0, actions:size()-1 do
		local action = actions:get(index)
		local actionName = action:getMetaType()

		if not player:hasFootInjury() then
			--Load bullets in magazine
			if actionName == "ISLoadBulletsInMagazine" then
				player:setVariable("DoingSthWhileRunning_ReloadOrRack_Permit",true)
		
			--Unload bullets from firearm
			elseif actionName == "ISUnloadBulletsFromFirearm" then
				player:setVariable("DoingSthWhileRunning_ReloadOrRack_Permit",true)

			--Unload bullets from magazine
			elseif actionName == "ISUnloadBulletsFromMagazine" then
				player:setVariable("DoingSthWhileRunning_ReloadOrRack_Permit",true)

			--Is rack firearm
			elseif actionName == "ISRackFirearm" then
				player:setVariable("DoingSthWhileRunning_ReloadOrRack_Permit",true)
				local weapon = player:getPrimaryHandItem()
				if weapon ~= nil then
					if weapon:isRanged() then
						if not weapon:haveChamber() then
							weapon:setHaveChamber(true)
						end
					end
				end

			--Insert magazine
			elseif actionName == "ISInsertMagazine" then
				player:setVariable("DoingSthWhileRunning_ReloadOrRack_Permit",true)

			--Reload weapon action
			elseif actionName == "ISReloadWeaponAction" then
				player:setVariable("DoingSthWhileRunning_ReloadOrRack_Permit",true)

			--Eject magazine
			elseif actionName == "ISEjectMagazine" then
				player:setVariable("DoingSthWhileRunning_ReloadOrRack_Permit",true)
			end
		end

	end

	--Settings for player
	local key = getCore():getKey("Run")
	if not getCore():isToggleToRun() then
		if player:getVariableBoolean("DoingSthWhileRunning_ReloadOrRack_Permit") then
			player:setAllowRun(false)
			if isKeyDown(key) then
				player:setVariable("DoingSthWhileRunning_ReloadOrRack_Enable",true)
			else
				player:setVariable("DoingSthWhileRunning_ReloadOrRack_Enable",false)
			end
		else
			player:setAllowRun(true)
		end
	end

	if not player:hasTimedActions() then
		player:setVariable("ExtraReloadMovementSet",false)  --For not toggle to run
		if player:getVariableBoolean("DoingSthWhileRunning_ReloadOrRack_Permit") then
			if not getCore():isToggleToRun() then
				player:setVariable("DoingSthWhileRunning_ReloadOrRack_Enable",false)
				player:setVariable("DoingSthWhileRunning_ReloadOrRack_Permit",false)
			else  --Toggle to run
				if player:getVariableBoolean("DoingSthWhileRunning_ReloadOrRack_Enable") then
					player:setRunning(true)
					player:setForceRun(true)
					player:setVariable("DoingSthWhileRunning_ReloadOrRack_Enable",false)
					player:setVariable("DoingSthWhileRunning_ReloadOrRack_Permit",false)
				end
			end
		end
	end
end

Events.OnPlayerUpdate.Add(SetVariable)--]]

local function SetAllowReloading(player)
    if not player:getVariableBoolean("RunAndReload_IsAllowed") then
        player:setVariable("RunAndReload_IsAllowed",true)

        if getCore():isToggleToRun() and (player:isRunning() or player:isSprinting()) then
            player:setRunning(false)
            player:setForceRun(false)
            player:setVariable("RunAndReload_Enable", true)
        end        
    end
end

local function SetVariable()
    if not isIngameState() then return end
    
    local player = getSpecificPlayer(0)
    if player == nil then return end
    
	local actions = player:getCharacterActions()
	
	for index = 0, actions:size()-1 do
		local action = actions:get(index)
		local actionName = action:getMetaType()
        
        if math.abs(player:getVariableFloat("WalkInjury", 0)) > 0.1 then
            player:setVariable("RunAndReload_Enable", false) 
            player:setVariable("RunAndReload_IsAllowed", false) 
            return
        end

        --Load bullets into magazine
        if actionName == "ISLoadBulletsInMagazine" then
            SetAllowReloading(player)
    
        --Unload bullets from firearm
        elseif actionName == "ISUnloadBulletsFromFirearm" then
            SetAllowReloading(player)

        --Unload bullets from magazine
        elseif actionName == "ISUnloadBulletsFromMagazine" then
            SetAllowReloading(player)

        --Is racking firearm
        elseif actionName == "ISRackFirearm" then
            SetAllowReloading(player)
            --[[local weapon = player:getPrimaryHandItem()
            if weapon ~= nil then
                if weapon:isRanged() then
                    if not weapon:haveChamber() then
                        weapon:setHaveChamber(true)
                    end
                end
            end--]]

        --Insert magazine
        elseif actionName == "ISInsertMagazine" then
            SetAllowReloading(player)

        --Reload weapon action
        elseif actionName == "ISReloadWeaponAction" then
            SetAllowReloading(player)

        --Eject magazine
        elseif actionName == "ISEjectMagazine" then
            SetAllowReloading(player)
        end
	end

	--Settings for player
	local key = getCore():getKey("Run")
	if not getCore():isToggleToRun() then
		if player:getVariableBoolean("RunAndReload_IsAllowed") then
			player:setAllowRun(false)

			if GameKeyboard.isKeyDown(key) then
				player:setVariable("RunAndReload_Enable",true)
			else
				player:setVariable("RunAndReload_Enable",false)
			end

			if isJoypadConnected(0) then
				if isJoypadRTPressed(0) then
					player:setVariable("RunAndReload_Enable",true)
				else
					player:setVariable("RunAndReload_Enable",false)				
				end
			end
		else
			player:setAllowRun(true)
		end
	end

	if not player:hasTimedActions() then
		if player:getVariableBoolean("RunAndReload_IsAllowed") then
			if not getCore():isToggleToRun() then
				player:setVariable("RunAndReload_Enable",false)
				player:setVariable("RunAndReload_IsAllowed",false)
			else  --Toggle to run
				if player:getVariableBoolean("RunAndReload_Enable") then
					player:setRunning(true)
					player:setForceRun(true)
					player:setVariable("RunAndReload_Enable",false)
					player:setVariable("RunAndReload_IsAllowed",false)
				end
			end
		end
	end
end

Events.OnTick.Add(SetVariable)