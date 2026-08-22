if not getActivatedMods():contains("RandomSoundEvents") then return; end

--- Load Utils and RandomSoundEventsAPI
local Utils = require 'RandomSoundEvents/Utils';
local RandomSoundEventsAPI = require 'RandomSoundEvents/Classes/RandomSoundEventsAPI';

local modName = "RandomSoundEvents_DyingGunFire";

-- CAN PLAY
-- Server Function to validate if the sound event can play
local function canPlay(player, x, y)
    local worldAge = Utils.GetWorldTotalDays();
    return not SandboxVars.RandomSoundEvents_DyingGunFire.disabled and 
            worldAge >= SandboxVars.RandomSoundEvents_DyingGunFire.daysSinceApocalypse and 
            worldAge < SandboxVars.RandomSoundEvents_DyingGunFire.daysSinceApocalypseEnd;
end

-- ON PLAY
-- Client Function triggered when the sound event start.
local function onPlay(soundName, soundRange, x, y)
    Utils.PlayerWorldSoundAt(x, y, 0, soundRange, nil);

    if SandboxVars.RandomSoundEvents_DyingGunFire.disableFear then return; end
    Utils.ForEachLocalPlayer(function(player)

        if not player:isDead() and not player:isGodMod() then
            if Utils.IsInRange(soundRange, x, y, player:getX(), player:getY()) then
                local stats = player:getStats();
                local panic = stats:getPanic();
                local stress = stats:getStress();

                if player:HasTrait("Brave") then
                    stats:setPanic(panic + 5);
                    stats:setStress(stress + 0.5);
                else
                    if not SandboxVars.RandomSoundEvents_DyingGunFire.disableWakingUp and player:isAsleep() then
                        player:setAsleep(false);
                        player:setAsleepTime(0.0);
                        UIManager.FadeIn(player:getPlayerNum(), 1);
                    end
                    stats:setPanic(panic + 10);
                    stats:setStress(stress + 0.05);
                end
            end
        end

    end);
end

-- ON UPDATE
-- Client Function triggered every tick during the sound event.
local function onUpdate(ticks, soundName, soundRange, x, y)
    if SandboxVars.RandomSoundEvents_DyingGunFire.disableFear then return; end

    Utils.ForEachLocalPlayer(function(player)

        if not player:isDead() and not player:isGodMod() then

            if Utils.IsInRange(soundRange, x, y, player:getX(), player:getY()) then
                local gameTimeMultiplier = getGameTime():getMultiplier();
    
                -- Stress the un-brave player
                if not player:HasTrait("Brave") then
                    local stats = player:getStats();
                    local panic = stats:getPanic();
                    local stress = stats:getStress();

                    stats:setPanic(panic + 0.01 * gameTimeMultiplier);
                    stats:setStress(stress + 0.0002 * gameTimeMultiplier);
                end
            end

        end

    end);
end

-- ON COMPLETED
-- Client Function triggered when the sound event is completed.
local function onCompleted(soundName, soundRange, x, y)

end

--- Gun Sounds
--- { SoundName, Range, canPlayFunction, onPlayFunction, onUpdateFunction, onCompletedFunction },
RandomSoundEventsAPI.Add(modName, "Female", {
    { "Female1", 115, canPlay, onPlay, onUpdate, onCompleted },
    { "Female2", 115, canPlay, onPlay, onUpdate, onCompleted },
    { "Female3", 120, canPlay, onPlay, onUpdate, onCompleted },
    { "Female4", 130, canPlay, onPlay, onUpdate, onCompleted },
});

RandomSoundEventsAPI.Add(modName, "Male", {
    { "Male1", 150, canPlay, onPlay, onUpdate, onCompleted },
    { "Male2", 150, canPlay, onPlay, onUpdate, onCompleted },
    { "Male3", 200, canPlay, onPlay, onUpdate, onCompleted },
    { "Male4", 190, canPlay, onPlay, onUpdate, onCompleted },
    { "Male5", 150, canPlay, onPlay, onUpdate, onCompleted },
    { "Male6", 140, canPlay, onPlay, onUpdate, onCompleted },
    { "Male7", 250, canPlay, onPlay, onUpdate, onCompleted },
    { "Male8", 180, canPlay, onPlay, onUpdate, onCompleted },
    { "Male9", 180, canPlay, onPlay, onUpdate, onCompleted },
    { "Male10", 200, canPlay, onPlay, onUpdate, onCompleted },
});