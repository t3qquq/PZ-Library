-- Namespace per la gestione della rete (Pipe)
WQS_NetPipe = WQS_NetPipe or {}
-- ID fisso usato ESCLUSIVAMENTE per il sistema di Handshake (Ping/Pong)
WQS_NetPipe.ID = "WQS_NetPipe"
WQS_NetPipe.isServerReady = false
WQS_NetPipe.commandQueue = {} -- La nostra coda di attesa




-------------------------------------------------------------------------
-- NUOVA FUNZIONE PUBBLICA: WQS_NetPipe.isServerReady()
-- Restituisce true se l'Handshake con il server è stato completato.
-------------------------------------------------------------------------
function WQS_NetPipe.checkIfServerIsReady()
    if not WQS_NetPipe.isServerReady then
        WQS_NetPipe.initConnection(true) --keepState=true
    end
    return WQS_NetPipe.isServerReady
end

-------------------------------------------------------------------------
-- IL WRAPPER PUBBLICO: WQS_NetPipe.WSendClientCommand(player, module, command, args)
-------------------------------------------------------------------------
function WQS_NetPipe.WSendClientCommand(player, module, command, args)
    -- Se il player non è specificato, usa il giocatore locale
    player = player or getSpecificPlayer(0)
    if not player or not module or not command then
        print(WQS_NetPipe.ID .. ": WSendClientCommand fallito: Argomenti mancanti.")
        return
    end

    args = args or {} -- Assicura che args sia una tabella

    if WQS_NetPipe.isServerReady then
        -- SCENARIO A: Il server è pronto. Invio immediato usando il 'module' dinamico fornito.
        -- print(WQS_NetPipe.ID ..": Server pronto. Invio comando '" .. command .. "' per il modulo '" .. module .. "'.")
        sendClientCommand(player, module, command, args)
    else
        -- SCENARIO B: Server non pronto. Mettiamo in coda il comando e tutti i suoi argomenti.
        print(WQS_NetPipe.ID ..
            ": Server non pronto. Comando '" .. command .. "' per il modulo '" .. module .. "' aggiunto alla coda.")
        table.insert(WQS_NetPipe.commandQueue, { player = player, module = module, cmd = command, args = args })
    end
end

-------------------------------------------------
-- LOGICA INTERNA: Svuota la coda quando pronto
-------------------------------------------------
function WQS_NetPipe.processQueue()
    local player = getPlayer()
    if not player then return end

    if #WQS_NetPipe.commandQueue > 0 then
        --print(WQS_NetPipe.ID .. ": Server pronto. Invio " .. #WQS_NetPipe.commandQueue .. " comandi in coda...")

        for _, entry in ipairs(WQS_NetPipe.commandQueue) do
            -- Re-invia i comandi accumulati usando il 'module' e 'player' specifici salvati.
            sendClientCommand(entry.player, entry.module, entry.cmd, entry.args)
        end

        -- Svuota la coda dopo l'invio
        WQS_NetPipe.commandQueue = {}
    end
end

-------------------------------------------------
-- LOGICA INTERNA: Gestione Handshake (Ricezione conferma)
-------------------------------------------------
function WQS_NetPipe.onServerCommand(module, command, args)
    -- Controlla se la risposta proviene dal modulo di Handshake fisso
    if module ~= WQS_NetPipe.ID then return end

    if command == "ServerIsReady" then
        WQS_NetPipe.isServerReady = true
        print(WQS_NetPipe.ID .. ": Connessione stabilita. Pronto a inviare comandi.")

        -- Appena riceviamo l'OK, processiamo la coda
        WQS_NetPipe.processQueue()
    end
end

-------------------------------------------------
-- LOGICA INTERNA: Invio Ping iniziale all'avvio
-------------------------------------------------
function WQS_NetPipe.initConnection(keepState)
    print("WQS_NetPipe.initConnection called keepState=" .. tostring(keepState))
    local player = getPlayer()
    if not player then return end
    player:transmitModData()
    -- Resetta lo stato in caso di respawn
    if not keepState then
        WQS_NetPipe.isServerReady = false
        WQS_NetPipe.commandQueue = {}
    end

    -- Invia il ping iniziale usando l'ID fisso del sistema di pipe
    --WQS_NetPipe.WSendClientCommand(player, WQS_NetPipe.ID, "CheckServerStatus", {})
    print(WQS_NetPipe.ID .. ": Requesting server status using command CheckServerStatus on sendClientCommand")
    sendClientCommand(player, WQS_NetPipe.ID, "CheckServerStatus", {})
end

-- Registrazione degli eventi
Events.OnServerCommand.Add(WQS_NetPipe.onServerCommand)
--Events.OnGameStart.Add(WQS_NetPipe.initConnection)
Events.OnCreatePlayer.Add(WQS_NetPipe.initConnection)
