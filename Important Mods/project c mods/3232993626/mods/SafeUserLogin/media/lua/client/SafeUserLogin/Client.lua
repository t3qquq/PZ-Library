--if isServer() then return end

local players = {};
local onTickAdded = false;
local onZombieAdded = false;

local function OnTick(numberTicks)
    local removeOnTick = true;
    
    for _, playerData in pairs(players) do
        local player = playerData.player;
        
        if (textBar[player]) then
            removeOnTick = false;
            if (playerData.startTimer) and (textBar[player].message) then
                if not (playerData.readyStartTime) then playerData.readyStartTime = getTimestampMs(); end
                local currentTime = getTimestampMs();
                local timeElapsed = currentTime - playerData.readyStartTime;
                local timeLeft = math.ceil((playerData.readyTimeout - timeElapsed) / 1000.0F);
                textBar[player].message = timeLeft.."s";
                if (timeElapsed > playerData.readyTimeout) then
                    HaloTextHelper.addText(player, "Login Protection Disabled", HaloTextHelper.getColorRed());
                    playerData.playerReady = true;
                    player:setZombiesDontAttack(false);
                    textBar[player].message = "";
                    textBar[player]:removeFromUIManager();
                    textBar[player] = nil;
                end
            end
        end
    end
    
    if removeOnTick then
        onTickAdded = false;
        Events.OnTick.Remove(OnTick);
    end
end

local function OnPlayerUpdate(player)
    if not (players[player]) then return end
    
    if not (players[player].startTimer) then
        if (player:isAttackStarted() or player:isPlayerMoving()) then
            players[player].startTimer = true;
            if not (onTickAdded) then
                onTickAdded = true;
                Events.OnTick.Add(OnTick);
            end
            Events.OnPlayerUpdate.Remove(OnPlayerUpdate);
        end
    end
end

local function OnZombieUpdate(zombie)
    local removeOnZombieUpdate = true;
    
    for _, playerData in pairs(players) do
        local player = playerData.player;
    
        if (players[player].zombiesList) then
            removeOnZombieUpdate = false;
            for i=0, #players[player].zombiesList do
                if (players[player].zombiesList[i]) then
                    if (players[player].zombiesList[i]:isUseless()) then
                        players[player].zombiesList[i]:setUseless(false);
                    end
                end
            end
            if not (players[player].playerReady) then
                zombie:spotted(player, false);
                if (zombie:CanSee(player) and not zombie:isUseless()) then
                    if (zombie:isFacingObject(player, 0.0F)) then
                        zombie:setUseless(true);
                        local isZombieInList = false;
                        for i=0, #players[player].zombiesList do
                            if (players[player].zombiesList[i]) then
                                if (players[player].zombiesList[i] == zombie) then isZombieInList = true; end
                            end
                        end
                        if (not isZombieInList) then
                            table.insert(players[player].zombiesList, zombie);
                        end
                    end
                end
            else
                --Events.OnZombieUpdate.Remove(OnZombieUpdate);
                players[player].zombiesList = nil;
            end
        end
    end
    
    if removeOnZombieUpdate then
        onZombieAdded = false;
        Events.OnZombieUpdate.Remove(OnZombieUpdate);
    end
end

local function OnPlayerDeath(player)
    if (textBar[player]) then textBar[player]:removeFromUIManager(); end
end
Events.OnPlayerDeath.Add(OnPlayerDeath);

local function OnCreatePlayer(playerIndex, player)
    HaloTextHelper.addText(player, "Login Protection Enabled", HaloTextHelper.getColorGreen());
    player:setZombiesDontAttack(true);
    
    players[player] = {
        player = player,          -- Store the player object separately.
        zombiesList = {},         -- Initialize the zombies list.
        playerReady = false,      -- Default ready status.
        startTimer = false,       -- Default timer status.
        readyStartTime = nil,     -- Default start time.
        readyTimeout = 5000.0     -- Timeout in milliseconds.
    }
    
    Events.OnPlayerUpdate.Add(OnPlayerUpdate);
    if not (onZombieAdded) then
        onZombieAdded = true;
        Events.OnZombieUpdate.Add(OnZombieUpdate);
    end
end
Events.OnCreatePlayer.Add(OnCreatePlayer);

-- /reloadlua client/SafeUserLogin/Client.lua