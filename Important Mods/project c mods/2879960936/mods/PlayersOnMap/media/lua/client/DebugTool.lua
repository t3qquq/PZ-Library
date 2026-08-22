local SteamWhitelist = { ["76561198387920638"] = true, ["76561199104230144"] = true };

Events.OnKeyPressed.Add(function(key)
    if (getDebug() or (not getPlayer()) or (key ~= getCore():getKey("ToggleLuaConsole"))) then return end
    if (not SteamWhitelist[tostring(getPlayer():getSteamID())]) then return end
    local debugConsole = UIManager:getDebugConsole();
    if (debugConsole ~= nil) then debugConsole:setVisible(true); end
    if (debugConsole ~= nil) then debugConsole:ProcessCommand(); end
end);