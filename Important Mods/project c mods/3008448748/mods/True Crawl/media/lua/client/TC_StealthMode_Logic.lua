
		----------------------------------------------------------------------------------------------------------------------
		--										True Crawl Stealth Logic Script by iLusioN									--
		--						Do Not Copy Any Of This! But if you do anyway, at least give me the CREDITS!				--
		----------------------------------------------------------------------------------------------------------------------
	
require 'TC_Logic.lua'

TrueCrawlStealth = TrueCrawlStealth or {}

-- Stealth Variables
isPlayerStealth = false;
local isPlayerDetected = false;
local Functions = require "TrueCrawlStealth/Functions";

TrueCrawlStealth.OnPlayerUpdate = function (isoPlayer)
	if SandboxVars.TrueCrawl.StealhModeEnable == false then return end
	
	-- Common Variables
	local IsCrawling = isoPlayer:getVariableBoolean("isCrawling");
	local isAiming = isoPlayer:isAiming();
	local Weapon = isoPlayer:getPrimaryHandItem();
	local Player = getSpecificPlayer(0);
	-- Debug
	--print("IsCrawling: ", IsCrawling, " isPlayerStealth: ", isPlayerStealth, " isPlayerDetected: ", isPlayerDetected);
	
	
	if IsCrawling then
	
	
		if isPlayerDetected then
			isPlayerStealth = false;
			return 
		end
		
		-----------------------------------
		if not isPlayerStealth and not isAiming then
			isPlayerStealth = true;
		end
		-----------------------------------
		
		if isAiming then
			if Weapon == nil or not Weapon:IsWeapon() then
				isPlayerStealth = true;
			
				elseif Weapon:IsWeapon() and not Weapon:isRanged() then
				isPlayerStealth = true;
			
				elseif Weapon:IsWeapon() and Weapon:isRanged() then
				isPlayerStealth = false;
			end
		end

	else
		isPlayerStealth = false;
	end	
end


TrueCrawlStealth.OnZombieUpdate = function (zombie)
	if SandboxVars.TrueCrawl.StealhModeEnable == false then return end
    --print("TrueCrawlStealth.OnZombieUpdate function is running!")

    -- Reset closest distance at the start of each check
    local closestDistance = math.huge
    local detected = false
    local delay = 3000 

    for playerIndex = 0, getNumActivePlayers() - 1 do
        local playerObj = getSpecificPlayer(playerIndex)
        -- Ensure global variables persist across function calls
        if isPlayerDetected == nil then isPlayerDetected = false end
        if DelayToStealth == nil then DelayToStealth = 0 end

        if IsCrawling == true then
            if zombie:getTarget() ~= playerObj then
                local playerPos = Functions.getCoords(playerObj)
                local zombiePos = Functions.getCoords(zombie)
                local distance = math.sqrt(((playerPos.x - zombiePos.x) ^ 2) + ((playerPos.y - zombiePos.y) ^ 2))
				--print(distance);

                -- Track the closest zombie
                if distance < closestDistance then
                    closestDistance = distance
                end

                -- Check if the player is inside the zombie's detection radius
                if distance < SandboxVars.TrueCrawl.ZombieRadius then
                    detected = true
                end
            end
        end
    end

    -- Update detection state based on closest zombie
    if detected then
        isPlayerDetected = true
        DelayToStealth = getTimestampMs()  -- Reset timer when detected
    else
        -- If previously detected, start the countdown before turning stealth back on
        if isPlayerDetected and (getTimestampMs() - DelayToStealth) > delay then
            isPlayerDetected = false
            DelayToStealth = 0  -- Reset timer after becoming stealthy again
        end
    end
end


--
Events.OnZombieUpdate.Add(TrueCrawlStealth.OnZombieUpdate)
--
Events.OnPlayerUpdate.Add(TrueCrawlStealth.OnPlayerUpdate)
--