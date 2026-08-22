------------------------------------------------------
--			COMPATIBILITY ADDON						--
-- To work with True Crouching sneak animations		--
------------------------------------------------------

CrawlComp = CrawlComp or {}

CrawlComp.OnGameStart = function()
	local playerObj = getPlayer()
	
	if getActivatedMods():contains("TrueCrouching") then
	
		playerObj:setVariable("isTrueCrouchingEnabled", "true")
		isTrueCrouchingEnabled = playerObj:getVariableBoolean("isTrueCrouchingEnabled")
		Events.OnPlayerUpdate.Add(CrawlComp.OnPlayerUpdate)
		print("TRUE CRAWL : isTrueCrouchingEnabled? ", "[",isTrueCrouchingEnabled,"]")
		
		
		elseif not getActivatedMods():contains("TrueCrouching") then
		playerObj:setVariable("isTrueCrouchingEnabled", "false")
		isTrueCrouchingEnabled = playerObj:getVariableBoolean("isTrueCrouchingEnabled")
		print("TRUE CRAWL : isTrueCrouchingEnabled? ", "[",isTrueCrouchingEnabled,"]")

	end
end

-- To fix the new character not getting the variable(isTrueCrouchingEnabled) problem.
CrawlComp.OnPlayerUpdate = function (isoPlayer)

	isTrueCrouchingEnabled = isoPlayer:getVariableBoolean("isTrueCrouchingEnabled")
	
	if isTrueCrouchingEnabled ~= true then
	isoPlayer:setVariable("isTrueCrouchingEnabled", "true")
	end
	
	--print(isTrueCrouchingEnabled)
	
end

Events.OnGameStart.Add(CrawlComp.OnGameStart)
