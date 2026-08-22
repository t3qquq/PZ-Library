--if isServer() then return end

local traits = Perks.Traits;
local traits2 = Perks.Traits2;
local traits3 = Perks.Traits3;

local players = {};

--[[ playerDeath(player)
    Receives OnPlayerDeath event
        - Resets zombieKillCount
]]
local function playerDeath(player)
    players[player].zombieKillCount = 0;
    local perks = { traits, traits2, traits3 };
    for i = 1, 3, 1 do
        if (perks[i] ~= nil) then
            local perkLevel = player:getPerkLevel(perks[i]);
            if (perkLevel > 0) then
                for j = 1, perkLevel, 1 do
                    player:LoseLevel(perks[i]);
                end
                perkLevel = player:getPerkLevel(perks[i]);
                local xp = player:getXp();
                xp:setXPToLevel(perks[i], player:getPerkLevel(perks[i]));
            end
        end
    end
end
Events.OnPlayerDeath.Add(playerDeath);

--[[ OnPlayerUpdate(player)
    Receives OnPlayerUpdate event
        - Add Traits XP if Zombie kills increased
]]
local function OnPlayerUpdate(player)
    local currentKillCount = player:getZombieKills();
    if (currentKillCount > players[player].zombieKillCount) then
        players[player].zombieKillCount = players[player].zombieKillCount + 1;
        local xp = player:getXp();
        local points = { player:getPerkLevel(Perks.Traits), player:getPerkLevel(Perks.Traits2), player:getPerkLevel(Perks.Traits3) }
        if (points[1] == 10 and points[2] == 10) then
            xp:AddXP(traits3, (SandboxVars.TraitsPurchaseSystem.XPperZombieKill * SandboxVars.TraitsPurchaseSystem.XPMultiplier), false, false, true);
        elseif (points[1] == 10) then
            xp:AddXP(traits2, (SandboxVars.TraitsPurchaseSystem.XPperZombieKill * SandboxVars.TraitsPurchaseSystem.XPMultiplier), false, false, true);
        else
            xp:AddXP(traits, (SandboxVars.TraitsPurchaseSystem.XPperZombieKill * SandboxVars.TraitsPurchaseSystem.XPMultiplier), false, false, true);
        end
    end
end
Events.OnPlayerUpdate.Add(OnPlayerUpdate);

--[[ playerLevelPerk(player, perk, level, levelUp)
    Receives LevelPerk event
        - Add Traits XP if any Perk but Traits level up
]]
local function playerLevelPerk(player, perk, level, levelUp)
    if (player:getHoursSurvived() > 0) then
        if (traits ~= perk and traits2 ~= perk and traits3 ~= perk and levelUp) then
            local xp = player:getXp();
            local points = { player:getPerkLevel(Perks.Traits), player:getPerkLevel(Perks.Traits2), player:getPerkLevel(Perks.Traits3) }
            if (points[1] == 10 and points[2] == 10) then
                xp:AddXP(traits3, (SandboxVars.TraitsPurchaseSystem.XPperPerkLevelup * SandboxVars.TraitsPurchaseSystem.XPMultiplier), false, false, true);
            elseif (points[1] == 10) then
                xp:AddXP(traits2, (SandboxVars.TraitsPurchaseSystem.XPperPerkLevelup * SandboxVars.TraitsPurchaseSystem.XPMultiplier), false, false, true);
            else
                xp:AddXP(traits, (SandboxVars.TraitsPurchaseSystem.XPperPerkLevelup * SandboxVars.TraitsPurchaseSystem.XPMultiplier), false, false, true);
            end
        end
    end
end
Events.LevelPerk.Add(playerLevelPerk)

--[[ OnCreatePlayer(playerIndex, player)
    Receives OnCreatePlayer event
        - Applies defaults to players array
]]
local function OnCreatePlayer(playerIndex, player)
    players[player] = {
        player = player,          -- Store the player object separately.
        playerNum = playerIndex,  -- Store the playerNum object separately.
        zombieKillCount = 0;      -- Initialize the zombieKillCount.
    }
    players[player].zombieKillCount = player:getZombieKills();
end
Events.OnCreatePlayer.Add(OnCreatePlayer);

-- /reloadlua client/TraitsPurchaseSystem/Client.lua