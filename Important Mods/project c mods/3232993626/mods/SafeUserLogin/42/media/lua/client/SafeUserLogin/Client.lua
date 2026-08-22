if getCore():isDedicated() then return end

-------------------------------------------------------------------------
-- local variables
local Utils = SafeUserLogin_mod.Utils;

local playersList = {};
local onTickAdded = false;
local onZombieAdded = false;

-------------------------------------------------------------------------
-- local functions

local function clearZombieList(player)
    if (not player or not playersList[player]) then
        return
    end
    
    if (playersList[player].zombiesList) then
        for i=0, #playersList[player].zombiesList do
            if (playersList[player].zombiesList[i] and playersList[player].zombiesList[i]:isUseless()) then
                playersList[player].zombiesList[i]:setUseless(false);
            end
        end
    end
end

local function removeProtection(player)
    if (not player or not playersList[player]) then
        return
    end
    
    HaloTextHelper.addText(player, "Login Protection Disabled", "", HaloTextHelper.getColorRed());
    Utils.noise('Login Protection Disabled');
    Utils.setTextBarMessage(player, '')
    Utils.removeTextBar(player);
    playersList[player].playerReady = true;
    --player:setZombiesDontAttack(false);
    clearZombieList(player);
    playersList[player] = nil;
end

--[[ OnTick(numberTicks)
    Receives OnTick event
        - Timer for protection
]]
local function OnTick(numberTicks)
    local removeOnTick = true;
    
    for _, data in pairs(playersList) do
        local player = data.player;
        
        if (Utils.getTextBar(player)) then
            removeOnTick = false;
            if (data.startTimer and Utils.getTextBarMessage(player)) then
                if (not (data.readyStartTime)) then
                    data.readyStartTime = getTimestampMs();
                end
                local currentTime = getTimestampMs();
                local timeElapsed = currentTime - data.readyStartTime;
                local timeLeft = math.ceil((data.readyTimeout - timeElapsed) / 1000.0F);
                Utils.setTextBarMessage(player, timeLeft .. 's');
                if (timeElapsed > data.readyTimeout) then
                    removeProtection(player);
                end
            end
        end
    end
    
    if (removeOnTick) then
        onTickAdded = false;
        Events.OnTick.Remove(OnTick);
    end
end

--[[ OnPlayerUpdate(player)
    Receives OnZombieUpdate event
        - Waits to for player input to disable protection
]]
local function OnPlayerUpdate(player)
    if (not playersList[player]) then
        return;
    end
    
    if (not playersList[player].startTimer) then
        if (player:isAttackStarted() or player:isPlayerMoving()) then
            playersList[player].startTimer = true;
            if (not onTickAdded) then
                onTickAdded = true;
                Events.OnTick.Add(OnTick);
            end
            Events.OnPlayerUpdate.Remove(OnPlayerUpdate);
        end
    end
end

--[[ OnZombieUpdate(zombie)
    Receives OnZombieUpdate event
        - setUseless for Agro'd zombies
]]
local function OnZombieUpdate(zombie)
    if (not zombie) then
        return
    end
    
    local removeOnZombieUpdate = true;
    local zombieAttack = zombie:getVariableBoolean("bAttack");
    
    for _, data in pairs(playersList) do
        local player = data.player;
    
        if (playersList[player].zombiesList) then
            removeOnZombieUpdate = false;
            local isZombieInList = false;
            local zombieIndex = 0;
            for i=0, #playersList[player].zombiesList do
                if (playersList[player].zombiesList[i] and playersList[player].zombiesList[i] == zombie) then
                    isZombieInList = true;
                    zombieIndex = i;
                    break;
                end
            end
            zombie:setUseless(false);
            if (zombie:CanSee(player) and not zombie:isUseless()) then
                if (zombie:getTarget() == player and zombieAttack) then
                    zombie:setUseless(true);
                    if (not isZombieInList) then
                        table.insert(playersList[player].zombiesList, zombie);
                    end
                end
            else
                if (isZombieInList) then
                    playersList[player].zombiesList[zombieIndex] = nil;
                end
            end
        end
    end
    
    if removeOnZombieUpdate then
        onZombieAdded = false;
        Events.OnZombieUpdate.Remove(OnZombieUpdate);
    end
end

--[[ OnPlayerDeath(player)
    Receives OnPlayerDeath event
        - Remove from UI on death
]]
local function OnPlayerDeath(player)
    removeProtection(player);
end
Events.OnPlayerDeath.Add(OnPlayerDeath);

--[[ OnCreatePlayer(playerIndex, player)
    Receives OnCreatePlayer event
        - Main intializer
]]
local function OnCreatePlayer(playerIndex, player)
    if (not player) then
        return
    end
    
    --player:setZombiesDontAttack(true);
    if (not Utils.createTextBar(player, playerIndex)) then
        --player:setZombiesDontAttack(false);
        Utils.noise('Login Protection failed.');
        return
    end
    Utils.noise('Login Protection Enabled');
    HaloTextHelper.addText(player, "Login Protection Enabled", "", HaloTextHelper.getColorGreen());
    
    playersList[player] = {
        player = player,          -- Store the player object separately.
        zombiesList = {},         -- Initialize the zombies list.
        playerReady = false,      -- Default ready status.
        startTimer = false,       -- Default timer status.
        readyStartTime = nil,     -- Default start time.
        readyTimeout = 5000.0     -- Timeout in milliseconds.
    }
    
    Events.OnPlayerUpdate.Add(OnPlayerUpdate);
    if (not onZombieAdded) then
        onZombieAdded = true;
        Events.OnZombieUpdate.Add(OnZombieUpdate);
    end
end
Events.OnCreatePlayer.Add(OnCreatePlayer);

-- /reloadlua client/SafeUserLogin/Client.lua