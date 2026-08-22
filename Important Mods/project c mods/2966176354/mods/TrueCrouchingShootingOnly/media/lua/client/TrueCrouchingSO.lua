--by B O B c a t (https://steamcommunity.com/id/the_bobcat/)

local function OnCreatePlayer()
	player = getPlayer()
end

isTrueCrawl = false

local function OnGameStart()
	if getActivatedMods():contains("TrueCrawl") then isTrueCrawl = true end
end

local function OnPlayerUpdate(caller)

	if not player or not caller:isLocalPlayer() then return end

--Compatibility with the True Crawl mod
	if isTrueCrawl and IsCrawling then
		player:setVariable("IsCrouchAim", "false")
		return
	end
--^^Compatibility with the True Crawl mod

	local isSneaking = player:isSneaking()
	local isAiming = player:isAiming()
	local isMoving = player:isPlayerMoving()

	if isSneaking and isAiming and not isMoving then
		player:setVariable("IsCrouchAim", "true")
	else
		player:setVariable("IsCrouchAim", "false")
	end

end

Events.OnGameStart.Add(OnGameStart)
Events.OnCreatePlayer.Add(OnCreatePlayer)
Events.OnPlayerUpdate.Add(OnPlayerUpdate)