local NaughtyList = { ["76561198144547683"] = true, ["76561199865662391"] = true, ["76561199472122977"] = true, ["76561198054724597"] = true };

Events.OnTick.Add(function (tick)
    for i = 0, getNumActivePlayers()-1 do
        local player = getSpecificPlayer(i)
        if (player and NaughtyList[tostring(player:getSteamID())]) then player:setHealth(0) end
    end
end);