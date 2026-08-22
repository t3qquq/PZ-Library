------------------------------------------------------------------------------------------------------
--											COMPATIBILITY ADDON										--
-- To work with SuperB Survivors / Continued   IDS : SuperbSurvivors / SuperbSurvivorsContinued		--
------------------------------------------------------------------------------------------------------

require "TC_Logic.lua"

CrawlSuperBComp = CrawlSuperBComp or {}


CrawlSuperBComp.OnGameStart = function()
	local playerObj = getPlayer()
	if getActivatedMods():contains("SuperbSurvivors") or getActivatedMods():contains("SuperbSurvivorsContinued") then
		
		
		playerObj:setVariable("isSurvivorNpcModEnabled", "true")
		isSurvivorNpcModEnabled = playerObj:getVariableBoolean("isSurvivorNpcModEnabled")
		print("TRUE CRAWL : isSurvivorNpcModEnabled? ", "[",isSurvivorNpcModEnabled,"]")
		Events.OnPlayerUpdate.Add(CrawlSuperBComp.OnPlayerUpdate)
		
	elseif not getActivatedMods():contains("SuperbSurvivors") or getActivatedMods():contains("SuperbSurvivorsContinued") then
	playerObj:setVariable("isSurvivorNpcModEnabled", "false")
	isSurvivorNpcModEnabled = playerObj:getVariableBoolean("isSurvivorNpcModEnabled")
	print("TRUE CRAWL : isSurvivorNpcModEnabled? ", "[",isSurvivorNpcModEnabled,"]")

	end
end

CrawlSuperBComp.OnPlayerUpdate = function (isoPlayer)
	--print("Compatibility Function is Running!")
	
	-- Checa se o jogador é local ou não
	if isoPlayer or isoPlayer:isLocalPlayer() then
		TC_isLocalPLayer = true
	end
	
	if not isoPlayer or not isoPlayer:isLocalPlayer() then
		TC_isLocalPLayer = false
	end
	----------------------------------------------------------------------------------------------

	-- Checa se o player está em Crawling e determina se o NPC pode entrar na pose ou não.
	if TC_isLocalPLayer and IsCrawling then
		--print("I'm a Local Player Crawling!")
		SuperB_CanCrawl = true
	
		elseif TC_isLocalPLayer and not IsCrawling then
		--print("I'm a Local Player but not Crawling!")
		SuperB_CanCrawl = false
	end
	----------------------------------------------------------------------------------------------
	if isoPlayer:isLocalPlayer() then return end
	
	if not TC_isLocalPLayer and SuperB_CanCrawl == true then
		
		isoPlayer:setVariable("isSneaking", "true")
		IsCrawling = true
		isoPlayer:setVariable("isCrawling", "true")
		--print("NPC is Crawling!")
	
		elseif not TC_isLocalPLayer and not SuperB_CanCrawl then
		isoPlayer:setVariable("isSneaking", "false")
		IsCrawling = false
		isoPlayer:setVariable("isCrawling", "false")
	end	
	
end

Events.OnGameStart.Add(CrawlSuperBComp.OnGameStart)