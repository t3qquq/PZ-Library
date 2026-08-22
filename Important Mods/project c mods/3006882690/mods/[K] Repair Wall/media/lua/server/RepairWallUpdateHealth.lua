require "Utils/RepairWallsUtils"

function HandleClientRequestsK(module, command, player, args)
    if not isServer() or module ~= "KAMER_RepairWall" then return end;

    if command == "updateHealth" then
        local GetSquare = getCell():getGridSquare(args.kamerX, args.kamerY, args.kamerZ)
        local GetObjects = GetSquare:getObjects()

        local ob = GetObjects:get(args.kamerSin)

        local objectData = KAMER_RepairWallsUtils:Data(ob)
        local objHealth = objectData.Health
        local objMaxHealth = objectData.MaxHealth
    
        local addHealth = math.ceil((objMaxHealth * args.kamerLv)/100)
        local total = objHealth + addHealth
            if total >= objMaxHealth then
                ob:setHealth(objMaxHealth)
            else
                ob:setHealth(total)
            end
        ob:transmitModData()
    end
end

Events.OnClientCommand.Add(HandleClientRequestsK)