if not isClient() then return end

-------------------------------------------------------------------------
-- local functions

--[[ TPSystemHandleOnServerCommand(module, command, args)
    Receives sendServerCommand from Server
]]
local function TPSystemHandleOnServerCommand(module, command, args)
    if (module ~= "TraitsPurchaseSystem") then
        return
    end
    if (not args) then
        return
    end

    local argStr = ''
	for k,v in pairs(args) do argStr = argStr..' '..k..'='..v end
    noise('received OnServerCommand - ' .. module .. ' ' .. command .. ' ' .. argStr);
    
    local localPlayer = getPlayer();
    local playerByID = getPlayerByOnlineID(args.TPSplayerID);
    local playerByUsername = getPlayerFromUsername(args.TPSusername);
    if (localPlayer ~= playerByID or localPlayer~= playerByUsername) then
        return;
    end

    if (command == "TPSTraitAdded") then
        playTraitAdded();
    end
    if (command == "TPSTraitRemoved") then
        playTraitRemoved();
    end
end
Events.OnServerCommand.Add(TPSystemHandleOnServerCommand);

-- /reloadlua client/TraitsPurchaseSystem/Client.lua