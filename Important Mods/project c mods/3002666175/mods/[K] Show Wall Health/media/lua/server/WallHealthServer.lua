function HandleServerRequestsK(module, command, player, args)
    if not isServer() or module ~= "KAMER_WallHealth_Client" then return end;

    if command == "updateHealth" then
        local GetSquare = getCell():getGridSquare(args.x, args.y, args.z)
        local GetObjects = GetSquare:getObjects()
        local ob = GetObjects:get(args.index)

        if ob and (instanceof(ob, "IsoThumpable") or instanceof(ob, "IsoDoor")) then
            local serverHealth = ob:getHealth()
            if args.health < serverHealth then
                local clientDamage = serverHealth - args.health
                local newHealth = serverHealth - clientDamage
                if newHealth <= 0 then
                    ob:setHealth(1)
                else
                    ob:setHealth(newHealth)
                end
            end
            args.newHealth = ob:getHealth()

            local objMaxHealth = ob:getMaxHealth()
            if objMaxHealth and args.newHealth > objMaxHealth then
                if ob.setMaxHealth then
                    ob:setMaxHealth(args.newHealth)
                end
            end

            sendServerCommand("KAMER_WallHealth_Server", "updateHealth", args)
        end
    end
end

Events.OnClientCommand.Add(HandleServerRequestsK)