require 'TC_Logic'
require 'TC_AnimationManager'
require 'UI/KeyBindMod'

CrawlUnder = CrawlUnder or {}
CrawlUnder.Verbose = false
CrawlUnder.key = 'CrawlUnder_Key'

KBM.addKeyBinding('[Player Control]', CrawlUnder.key, 42) -- Shift

function CrawlUnder.OnPlayerUpdate(isoPlayer)
    if not isoPlayer then return end
    local square = isoPlayer:getSquare()
    if not square then return end
    local sZ = square:getZ()
    if sZ ~= 0 then return end
	
	if SandboxVars.TrueCrawl.CrawlUnderVehiclesEnable == true then
	
		if IsCrawling == true then
		
			if isKeyPressed(getCore():getKey(CrawlUnder.key)) and not isoPlayer:hasTimedActions() and not square:HasStairs() then
				--print("CrawlUnder key is Working!")
				local vehicle = isoPlayer:getNearVehicle() -- Ve se está perto de veiculos
			
				if vehicle and vehicle:isAtRest() then
				
					local charOrientationAngle = isoPlayer:getAnimAngleRadians()
					local forwardDist = 0.3
					local forwardX = isoPlayer:getX()+ math.cos(charOrientationAngle) * forwardDist
					local forwardY = isoPlayer:getY()+ math.sin(charOrientationAngle) * forwardDist
					
						if vehicle:isInBounds(forwardX,forwardY) then
							ISTimedActionQueue.clear(isoPlayer)
							ISTimedActionQueue.add(ISTC_CrawlUnder:new(isoPlayer,vehicle))
							--print("Crawling under vehicle")
						end
				end
    
			end
		end
	else
	Events.OnPlayerUpdate.Remove(CrawlUnder.OnPlayerUpdate)
	end
end
Events.OnPlayerUpdate.Add(CrawlUnder.OnPlayerUpdate)