if isClient() then return end

WQS_NetPipeServer = WQS_NetPipeServer or {}

-- ID fisso per la logica di Handshake del sistema NetPipe
WQS_PipeID = "WQS_NetPipe"
function WQS_NetPipeServer.onClientCommand(module, command, player, args)
    --local function onClientCommand(module, command, player, args)
    -- === 1. GESTIONE HANDSHAKE (Riconoscimento WQS_NetPipe) ===
    --print(WQS_PipeID ..
    --    ": Server received ClientCommand module=" ..
    --    tostring(module) .. ": command=" .. tostring(command) .. " player=" .. tostring(player))

    -- Controlliamo se il comando è il PING inviato dal client.
    if module == WQS_PipeID and command == "CheckServerStatus" then
        print(WQS_PipeID .. ": Il server ha ricevuto il ping di Handshake dal client. Risponde con ServerIsReady.")

        -- Rispondiamo al client con il segnale di "Ready", usando l'ID fisso.
        -- Questo sbloccherà la coda dei comandi lato client.
        -- sendServerCommand(WQS_PipeID, "ServerIsReady", { status = "OK" })

        -- [v42.20 Update] Targeted sendServerCommand to player for handshake ping response
        sendServerCommand(player, WQS_PipeID, "ServerIsReady", { status = "OK" })
        return
    end
end

Events.OnClientCommand.Add(WQS_NetPipeServer.onClientCommand)
