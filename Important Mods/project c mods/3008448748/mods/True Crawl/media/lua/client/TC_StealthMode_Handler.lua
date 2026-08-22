
-- TrueCrawl Handler by iLusioN

local StealthApplied = false

 --------------------------------------------------------------------------------
 -- Multiplayer Stealth Handler // Working: 03/06/2025
 --------------------------------------------------------------------------------
local function OnPlayerUpdate(isoPlayer)

	if SandboxVars.TrueCrawl.StealhModeEnable == true then
		if isPlayerStealth and not StealthApplied then
			if not (isoPlayer:getAccessLevel() == "Admin") and SandboxVars.TrueCrawl.StealhModeServer == true then
				isoPlayer:setGhostMode(true)
				StealthApplied = true
				print("SERVER MODE: Stealth ON")
			end
		end
	
		if not isPlayerStealth and StealthApplied then
			if not (isoPlayer:getAccessLevel() == "Admin") and SandboxVars.TrueCrawl.StealhModeServer == true then
				isoPlayer:setGhostMode(false)
				StealthApplied = false
				print("SERVER MODE: Stealth OFF")
			
			end
		end
	end
 end
 
 
 
 --------------------------------------------------------------------------------
 -- Singleplayer Stealth Handler // Working: 03/06/2025
 --------------------------------------------------------------------------------
 local function OnZombieUpdate(zombie)
	if not SandboxVars.TrueCrawl.StealhModeEnable == true then return end;
	if not SandboxVars.TrueCrawl.StealhModeServer == false then return end;


	if SandboxVars.TrueCrawl.StealhModeEnable == true then
		if SandboxVars.TrueCrawl.StealhModeServer == false then
		
			if isPlayerStealth then
				zombie:setUseless(true)
				--print("Zombie is Useless")
			
				elseif not isPlayerStealth then
					zombie:setUseless(false)
					--print("Zombie is not Useless")
			end
		end
	end
end

Events.OnPlayerUpdate.Add(OnPlayerUpdate)
Events.OnZombieUpdate.Add(OnZombieUpdate)
