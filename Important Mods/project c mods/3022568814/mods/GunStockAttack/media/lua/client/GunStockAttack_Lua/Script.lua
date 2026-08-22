--Made by SportXAI
--Create time : 2022/12/20  20:10
--Rewrite time : 2023/8/12 19:00
--local defaultRifleSpeed = 1.34
--local defaultPistolSpeed = 1.24
local _defaultShoveSpeed = 0.80

local function GunStockAttack_ReturnWeaponType(item)
    if item == nil then return end

        if item:IsWeapon() and item:isRanged() then
            if not item:isTwoHandWeapon() then
                return "P" --pistol
            else 
                return "R" --rifle
            end
        end

    return "N"
end

local function GunStockAttack_HitZombie(zombie, player)
    if not instanceof (player, "IsoPlayer") then return end

    local playerStats = player:getStats()
    local enduranceLost = getSandboxOptions():getOptionByName("GunStockAttackOption.EnduranceLost"):getValue()
    local conditionLost = getSandboxOptions():getOptionByName("GunStockAttackOption.WeaponConditionLost"):getValue()
    local stockDamage = getSandboxOptions():getOptionByName("GunStockAttackOption.Damage"):getValue()
    local weapon = player:getPrimaryHandItem()

    --is weapon on hand
    if weapon == nil then return end
    --not stamp on zombie
    if player:getVariableBoolean("StompAnim") then return end

    if player:isDoShove() and player:isAiming() and player:getVariableBoolean("bShoveAiming") then
        if GunStockAttack_ReturnWeaponType(weapon) == "N" then return end

        zombie:setHealth(zombie:getHealth() - (stockDamage / 100) * playerStats:getEndurance())

        --weapon condition lose
        local fLossChance = (weapon:getConditionLowerChance()) + math.floor(math.floor(player:getPerkLevel(Perks.Maintenance) + (player:getPerkLevel(Perks.Aiming) / 2 )) / 2)
        if ZombRand(fLossChance) == 0 then
            weapon:setCondition(weapon:getCondition() - conditionLost)
        end

        --player endurance lose
        playerStats:setEndurance(playerStats:getEndurance() - enduranceLost)

        --Hit type
        if GunStockAttack_ReturnWeaponType(weapon) == "R" then  
            zombie:setVariable("GunStockAttack_HitByRifle", true)
        else
            zombie:setVariable("GunStockAttack_HitByPistol", true)
        end

        --set variable to play hit sound
        if not player:getVariableBoolean("GSA_ShouldPlayHitSound") then
            player:setVariable("GSA_ShouldPlayHitSound", true)
        end

        --add blood
        if zombie:getHealth() <= 0.6 then
            zombie:addBlood(BloodBodyPartType.Head, false, false ,false)

            if zombie:getHealth() <= 0.4 then
                zombie:addBlood(6)
            end

        end
    end
end

local function GunStockAttack_BreakAction(player)
    if player == nil then return end
	local primaryHandItem = player:getPrimaryHandItem()

	if GunStockAttack_ReturnWeaponType(primaryHandItem) ~= "N" then

		if GameKeyboard.isKeyPressed(getCore():getKey("Melee")) then
			for i = 0,player:getCharacterActions():size() - 1 do
                local action = player:getCharacterActions():get(i)
                action:forceStop()
                return
            end
		end

	end
end

local function GunStockAttack_AttackSpeedSet(player)
    if player == nil then return end
    local handItem = player:getPrimaryHandItem()

    if handItem == nil then 
        player:setVariable("GSA_AttackSpeed", _defaultShoveSpeed) 
        return 
    end

    if player:isAiming() then
        if GunStockAttack_ReturnWeaponType(handItem) == "R" then
            player:setVariable("GSA_AttackSpeed", getSandboxOptions():getOptionByName("GunStockAttackOption.RifleSpeed"):getValue())
        elseif GunStockAttack_ReturnWeaponType(handItem) == "P" then
            player:setVariable("GSA_AttackSpeed", getSandboxOptions():getOptionByName("GunStockAttackOption.PistolSpeed"):getValue())
        end
    else
        player:setVariable("GSA_AttackSpeed", _defaultShoveSpeed)
    end
end

Events.OnPlayerUpdate.Add(GunStockAttack_AttackSpeedSet)
Events.OnPlayerUpdate.Add(GunStockAttack_BreakAction)
Events.OnHitZombie.Add(GunStockAttack_HitZombie)