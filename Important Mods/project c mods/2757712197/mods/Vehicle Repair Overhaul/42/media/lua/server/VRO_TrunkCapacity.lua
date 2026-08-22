--[[---------------------------------------------------------------------------
  SFC Fix Pack -- correcao 01: capacidade do porta-malas apos conserto (VRO)

  PROBLEMA
    O mod Vehicle Repair Overhaul aplica o conserto no servidor
    (VRO_VehicleCommands.lua, comando "VRO_vehicle"/"doFix") assim:

        part:setCondition(nova)
        inv:setCondition(part:getCondition())
        vehicle:updatePartStats()
        vehicle:transmitPartCondition(part) / transmitPartItem(part)

    Falta a chamada a part:doInventoryItemStats(). No Java, esse e o unico
    metodo que recalcula ItemContainer.capacity a partir da condicao para
    pecas declaradas com "conditionAffectsCapacity = true" (TruckBed e
    TruckBedOpen):

        setContainerCapacity( (int) getNumberByCondition(
                                        item:getMaxCapacity(),
                                        item:getCondition(), 5.0f) )

    setCondition() nao faz isso (so tem um caso especial p/ TrailerTrunk) e
    updatePartStats() tambem nao (cuida de motor, suspensao e ruido).

    Resultado: ao receber o item da peca, o CLIENTE recalcula sozinho
    (VehicleUpdatePacket.parse chama doInventoryItemStats) e mostra a
    capacidade nova; o SERVIDOR fica com a antiga, e como toda movimentacao
    de item passa por TransactionManager.isConsistent() ->
    ItemContainer.hasRoomFor(), o porta-malas trava no limite de quando
    estava danificado. E o valor ainda e persistido (ItemContainer.save),
    entao reiniciar o servidor nao resolve.

  REFERENCIA
    O caminho vanilla equivalente esta em
    media/lua/server/Vehicles/VehicleCommands.lua -> Commands.fixPart(),
    que faz exatamente a mesma sequencia do VRO MAIS o doInventoryItemStats.
    Esta correcao so recoloca esse passo.

  O QUE FAZ
    1. Depois de cada "doFix" do VRO, reaplica doInventoryItemStats na peca
       consertada (mesma sequencia do Commands.fixPart vanilla).
    2. Varredura periodica que conserta veiculos que ja ficaram com a
       capacidade travada antes desta correcao. So AUMENTA a capacidade,
       nunca reduz, para nao brigar com mods que ampliam porta-malas.

  Somente servidor: nada aqui entra no checksum de Lua que o cliente
  compara ao conectar (NetChecksum cobre media/lua/shared e media/lua/client).
-----------------------------------------------------------------------------]]

if isClient() then return end

-- Varredura de recuperacao dos veiculos ja afetados. Deixe false se algum
-- outro mod passar a gerenciar a capacidade dos conteineres de veiculo.
local ENABLE_LEGACY_SWEEP = true
local DEBUG = false

local function log(msg) print("[SFCFixPack/TrunkCapacity] " .. tostring(msg)) end
local function dbg(msg) if DEBUG then log(msg) end end

--------------------------------------------------------------------------
-- Espelho exato de VehiclePart.getNumberByCondition(value, condition, 5.0)
-- seguido do cast (int) que doInventoryItemStats aplica.
--   escala = condicao + 20 * (100 - condicao) / 100   -> 100% em 100 de cond,
--                                                        60% em 50 de cond
--------------------------------------------------------------------------
local function expectedCapacity(maxCapacity, condition)
    local scale = (condition + 20.0 * (100.0 - condition) / 100.0) / 100.0
    local value = math.max(5.0, maxCapacity * scale)
    value = math.floor(value * 100.0 + 0.5) / 100.0   -- Math.round(x*100)/100
    return math.floor(value)                          -- cast (int) do Java
end

--------------------------------------------------------------------------
-- Reaplica as estatisticas da peca a partir do item instalado.
-- Mesma sequencia de Commands.fixPart() do vanilla.
--------------------------------------------------------------------------
local function refreshPart(vehicle, part)
    local item = part:getInventoryItem()
    if not item then return false end

    part:doInventoryItemStats(item, part:getMechanicSkillInstaller())

    -- Mudar a condicao pode mudar a capacidade: reencaixa o conteudo dentro
    -- do novo limite (guarda identica a do vanilla).
    if part:isContainer() and not part:getItemContainer() then
        part:setContainerContentAmount(part:getContainerContentAmount())
    end

    vehicle:updatePartStats()
    vehicle:updateBulletStats()
    vehicle:transmitPartCondition(part)
    vehicle:transmitPartItem(part)
    return true
end

--------------------------------------------------------------------------
-- Fila: a ordem dos handlers de OnClientCommand entre mods nao e garantida,
-- entao nao da para agir dentro do proprio evento (o VRO pode ainda nao ter
-- aplicado a condicao nova). Adiamos para o proximo tick.
--------------------------------------------------------------------------
local pending, pendingCount = {}, 0

local function onClientCommand(module, command, player, args)
    if module ~= "VRO_vehicle" then return end
    if command ~= "doFix" then return end
    if not (args and args.vehicleId and args.partId) then return end

    pendingCount = pendingCount + 1
    pending[pendingCount] = { vehicleId = args.vehicleId, partId = args.partId }
end

local function drainQueue()
    if pendingCount == 0 then return end

    local queue = pending
    pending, pendingCount = {}, 0

    for i = 1, #queue do
        local job = queue[i]
        local vehicle = getVehicleById(job.vehicleId)
        if vehicle then
            local part = vehicle:getPartById(job.partId)
            if part then
                local ok, err = pcall(refreshPart, vehicle, part)
                if ok then
                    dbg(string.format("peca %s do veiculo %s reajustada apos conserto",
                        tostring(job.partId), tostring(job.vehicleId)))
                else
                    log("falha ao reajustar " .. tostring(job.partId) .. ": " .. tostring(err))
                end
            end
        end
    end
end

--------------------------------------------------------------------------
-- Varredura de recuperacao: veiculos consertados antes desta correcao
-- ficaram com a capacidade congelada no save. Aqui so subimos para o valor
-- que o proprio jogo calcularia; nunca reduzimos.
--------------------------------------------------------------------------
local sweepBroken = false

local function sweepVehicle(vehicle)
    local count = vehicle:getPartCount()
    for i = 0, count - 1 do
        local part = vehicle:getPartByIndex(i)
        if part then
            local container = part:getItemContainer()
            local item = part:getInventoryItem()
            if container and item then
                local maxCapacity = item:getMaxCapacity()
                if maxCapacity and maxCapacity > 0 then
                    local want = expectedCapacity(maxCapacity, item:getCondition())
                    if container:getCapacity() < want then
                        log(string.format("veiculo %s, peca %s: capacidade %s -> %s",
                            tostring(vehicle:getId()), tostring(part:getId()),
                            tostring(container:getCapacity()), tostring(want)))
                        part:setContainerCapacity(want)
                        vehicle:transmitPartItem(part)
                    end
                end
            end
        end
    end
end

local function sweepLoadedVehicles()
    local cell = getCell()
    if not cell then return end
    local vehicles = cell:getVehicles()
    if not vehicles then return end

    local iterator = vehicles:iterator()
    while iterator:hasNext() do
        local vehicle = iterator:next()
        if vehicle then sweepVehicle(vehicle) end
    end
end

local function onEveryOneMinute()
    drainQueue()

    if not ENABLE_LEGACY_SWEEP or sweepBroken then return end
    local ok, err = pcall(sweepLoadedVehicles)
    if not ok then
        sweepBroken = true
        log("varredura desativada (a correcao pos-conserto continua ativa): " .. tostring(err))
    end
end

Events.OnClientCommand.Add(onClientCommand)
Events.OnTick.Add(drainQueue)
Events.EveryOneMinute.Add(onEveryOneMinute)

log("ativo")
