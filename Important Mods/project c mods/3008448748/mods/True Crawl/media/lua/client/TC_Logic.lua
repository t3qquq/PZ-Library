
		----------------------------------------------------------------------------------------------------------------------
		--										True Crawl Logic Script by iLusioN											--
		--					Do Not Copy Any Of This! But if you do anyway, at least give me the CREDITS!					--
		----------------------------------------------------------------------------------------------------------------------
	
require 'UI/KeyBindMod'

TrueCrawl = TrueCrawl or {}
TrueCrawl.Verbose = false
TrueCrawl.Variable = 'isCrawling'
----------------------------------------
--TrueCrawl.CrawlingKeyHelper = false
IsCrawlingGlobal = false
----------------------------------------

-------------------------------------------------------
-- Em caso do jogador alterar a tecla de Crouch
local old_MainOptionsApply = MainOptions.apply

function MainOptions:apply (closeAfter)
	MainOptions.saveKeys();
	old_MainOptionsApply(self, closeAfter)
	crouchKey = getCore():getKey("Crouch")
	
end
-------------------------------------------------------

TrueCrawl.OnGameStart = function ()
	crouchKey = getCore():getKey("Crouch")
end
Events.OnGameStart.Add(TrueCrawl.OnGameStart)

TrueCrawl.OnDisconnect = function ()
	crouchKey = getCore():getKey("Crouch")
end
Events.OnDisconnect.Add(TrueCrawl.OnDisconnect)

-------------------------------------------------------

-- Aiming Bonus Control Variable
local IsAimBonusApplied = false
local AimHitChanceModifier = 0
local AimCritModifier = 0

TrueCrawl.OnPlayerUpdate = function(isoPlayer)

	IsCrawling = isoPlayer:getVariableBoolean("isCrawling")
	IsSneaking = isoPlayer:getVariableBoolean("sneaking")
	IsCrawlingGlobal = IsCrawling
	
	
	--print("isCrawling:", IsCrawling)
	--print("isSneaking:", IsSneaking)
	--print("IsCrawlingGlobal:", IsCrawlingGlobal)
	
	--local playerObj = getSpecificPlayer(0)
	local isAiming = isoPlayer:isAiming()
	local Weapon = isoPlayer:getPrimaryHandItem()
	local IsTurning = isoPlayer:getVariableBoolean("isTurning")
	local IsTurningAround = isoPlayer:getVariableBoolean("isTurningAround")
	local IsTurning90 = isoPlayer:getVariableBoolean("isTurning90")
	local IsClimbing = isoPlayer:getVariableBoolean("ClimbFenceStarted")
	local isSprint = isoPlayer:isSprinting()
	local isRun = isoPlayer:isRunning()
	local isMoving = isoPlayer:isPlayerMoving()
	local isRacking = isoPlayer:getVariableBoolean("isracking")
		------------------------------------------
		-- Resets everything if not sneaking (not sneak = not crawl)
		if not IsSneaking and IsCrawling then
			--isoPlayer:setVariable("isCrawling", false)
			TrueCrawl.setCrawling(playerObj,false)
			isoPlayer:setBannedAttacking(false)
			isoPlayer:setIgnoreAimingInput(false)
			isoPlayer:setBlockMovement(false)
			getCore():addKeyBinding("Crouch", crouchKey);
			return
		end
		
		if not IsCrawling then
			isoPlayer:setBannedAttacking(false)
			isoPlayer:setIgnoreAimingInput(false)
			isoPlayer:setBlockMovement(false)
			getCore():addKeyBinding("Crouch", crouchKey);
			return
		end
		
	
		-- nothing happens if is under vehicle
		if IsUnderVehicle == true then return end 
		if isoPlayer:isSitOnGround() then return end
		if isRacking then return end
		------------------------------------------
				-- Crawling Director --
		------------------------------------------
	if IsCrawling then
	
		if isoPlayer:hasTimedActions() then
			--isoPlayer:setVariable("isCrawling", false)
			TrueCrawl.setCrawling(playerObj,false)
			return
		end
	

	
		------------------------------------------
		-- Prevent Shove when Crawling
		if isoPlayer:getVariableBoolean("isCrawling") and not isAiming then
		isoPlayer:setBannedAttacking(true)
		elseif not isoPlayer:getVariableBoolean("isCrawling") then
		isoPlayer:setBannedAttacking(false)
		end
		
	
		------------------------------------------
				-- Prevent animation issues
		------------------------------------------
		if IsTurning then
		getCore():addKeyBinding("Crouch", Keyboard.KEY_NONE);
		--print("Is turning!")
		return
		elseif not IsTurning then
		getCore():addKeyBinding("Crouch", crouchKey);
		end
		
		if isMoving or IsTurningAround then 
		isoPlayer:setIgnoreAimingInput(true)
		--print("isMoving!")
		
		elseif not isMoving and not IsTurningAround then
		isoPlayer:setIgnoreAimingInput(false)
		end
		
		------------------------------------------
				-- Aiming Handler
		------------------------------------------
		-- Aim Bonus variables
		local HitChanceBonus = SandboxVars.TrueCrawl.AimingHitChanceBonusWhileProne
		local CritChanceBonus = SandboxVars.TrueCrawl.AimingCritChanceBonusWhileProne
		
		--Debug Prints
		--print ("IsAimBonusApplied:", IsAimBonusApplied)
		--print("AimHitChanceModifier:", AimHitChanceModifier)
		--print("AimCritModifier:", AimCritModifier)
		
		if isAiming then
		
			if Weapon == nil or not Weapon:IsWeapon() then
			--print("Its not even a Weapon")
			getCore():addKeyBinding("Crouch", Keyboard.KEY_NONE);

			elseif Weapon:IsWeapon() and not Weapon:isRanged() then
			--print("Its a Weapon but not Ranged Weapon")
			getCore():addKeyBinding("Crouch", Keyboard.KEY_NONE);
			
			elseif Weapon:IsWeapon() and Weapon:isRanged() then
			--print("Its endeed a Ranged Weapon")
			getCore():addKeyBinding("Crouch", Keyboard.KEY_NONE);
			isoPlayer:setBannedAttacking(false)
			
				-- Apply the aiming bonus
				if not IsAimBonusApplied then--and SandboxVars.TrueCrawl.AimBonusEnable then
				Weapon:setAimingPerkHitChanceModifier(Weapon:getAimingPerkHitChanceModifier() + HitChanceBonus)
				Weapon:setAimingPerkCritModifier(Weapon:getAimingPerkCritModifier() + CritChanceBonus)
				AimHitChanceModifier = Weapon:getAimingPerkHitChanceModifier()
				AimCritModifier = Weapon:getAimingPerkCritModifier()
				IsAimBonusApplied = true
				--print("Aiming Bonus was applied")
				end
				

			
			end
		
			-- Prevents stucking keys and bonuses
			elseif not isAiming then
			
			getCore():addKeyBinding("Crouch", crouchKey);
			
				-- Removes the aiming bonus
				if Weapon == nil or not Weapon:IsWeapon() then return end
				if Weapon:IsWeapon() and not Weapon:isRanged() then return end
				if Weapon:IsWeapon() and Weapon:isRanged() then
					if IsAimBonusApplied and SandboxVars.TrueCrawl.AimBonusEnable then
						Weapon:setAimingPerkHitChanceModifier(Weapon:getAimingPerkHitChanceModifier() - HitChanceBonus)
						Weapon:setAimingPerkCritModifier(Weapon:getAimingPerkCritModifier() - CritChanceBonus)
						AimHitChanceModifier = Weapon:getAimingPerkHitChanceModifier()
						AimCritModifier = Weapon:getAimingPerkCritModifier()
						IsAimBonusApplied = false
					end
				end
		end
	end
 end



local KEYSTATE = {}
-- Checa a tecla 
TrueCrawl.checkKey = function(key)

		if key ~= getCore():getKey("Crouch") then
			return false
		end
	
		local playerObj = getSpecificPlayer(0)
	
		if not playerObj or playerObj:isDead() then
			return false
		end
	
	return true
end

-- Start the hold
TrueCrawl.onKeyStartPressed = function(key)
	if not TrueCrawl.checkKey(key) then return end
	
		local playerObj = getSpecificPlayer(0)
		local PlayerHasTimedActions = playerObj:hasTimedActions()	
		
	if IsCrawling and IsSneaking and not PlayerHasTimedActions then
	
		-- Stops crawl
		--playerObj:setVariable("isCrawling", false)
		playerObj:setVariable("sneaking", false)
		TrueCrawl.setCrawling(playerObj,false)
		--print("Player was Crawling and Sneaking, this function worked!")
		
	else
		
        -- Store the time when the key is pressed
        KEYSTATE.keyPressedMS = getTimestampMs()
		
        -- Mark the key as being held down
        KEYSTATE.keyHeldDown = true
		
	end
end

-- Runs the hold
TrueCrawl.onKeyKeepPressed = function(key)

    if not TrueCrawl.checkKey(key) then return end
    if not KEYSTATE.keyPressedMS then return end
    if not KEYSTATE.keyHeldDown then return end
	
	local playerObj = getSpecificPlayer(0)
	local delay = 300

	
	local isAiming = playerObj:isAiming()
	local IsTurning = playerObj:getVariableBoolean("isTurning")
	local IsClimbing = playerObj:getVariableBoolean("ClimbFenceStarted")
	local isSprint = playerObj:isSprinting()
	local PlayerHasTimedActions = playerObj:hasTimedActions()
	
	if playerObj:isSitOnGround() then return end
	
	---------------------------------------------------------------------------------------------------------------------------------------------------------
	--if not IsSneaking and not IsCrawling and (getTimestampMs() - KEYSTATE.keyPressedMS > 140) and key == getCore():getKey("Crouch") and playerObj then
	--playerObj:setVariable("sneaking", true)
	--playerObj:setVariable("isCrawling", true)
	--end
	
	-- Prevents the function to run infinite times
	--if TrueCrawl.CrawlingKeyHelper then return end
	---------------------------------------------------------------------------------------------------------------------------------------------------------


	if (getTimestampMs() - KEYSTATE.keyPressedMS > delay) and key == getCore():getKey("Crouch") and playerObj then
	
		if not IsCrawling and not playerObj:hasTimedActions() and not isAiming and not IsTurning and not IsClimbing and not isSprint and not PlayerHasTimedActions then
		--print("HOLDING TO CRAWL KEY IS WORKING!")
		
		-- Starts the Crawl
		--playerObj:setVariable("isCrawling", true)
		playerObj:setVariable("sneaking", true)
		TrueCrawl.setCrawling(playerObj,true)
		end
	end		
end



-- Finishes the hold
TrueCrawl.onKeyReleased = function(key)
	if not TrueCrawl.checkKey(key) then return end
	if key == getCore():getKey("Crouch") then
		--TrueCrawl.CrawlingKeyHelper = false
		KEYSTATE.keyHeldDown = false
	end
end

function TrueCrawl.setCrawling(isoPlayer, isIt)
    if TrueCrawl.Verbose then print ('TrueCrawl.setCrawling '..p2str(isoPlayer)..' '..tab2str(isIt)); end
    PlaVar.set(isoPlayer, TrueCrawl.Variable, isIt)
end


Events.OnKeyStartPressed.Add(TrueCrawl.onKeyStartPressed)
Events.OnKeyKeepPressed.Add(TrueCrawl.onKeyKeepPressed)
Events.OnKeyPressed.Add(TrueCrawl.onKeyReleased)
Events.OnPlayerUpdate.Add(TrueCrawl.OnPlayerUpdate)