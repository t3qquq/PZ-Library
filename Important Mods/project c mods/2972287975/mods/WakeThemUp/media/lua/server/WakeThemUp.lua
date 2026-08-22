
WakeThemUp = WakeThemUp or {}
WakeThemUp.Verbose = false

-----do one room per cycle and suspend once the cell is fully woken up
-----continue when the size increases.
-----the thing is: it never decreases !
-----as a consequence: each room is only requested once.
-----as a consequence: the peak cost is only one room. (instead of many an it occures a lot that many pop in one cycle).
-----as a consequence: long time server runs bigroom list is not a problem as the room list is never parsed.

WakeThemUp.LastCellChecked = nil
WakeThemUp.LastCellCheckedRoomCheckIter = nil
WakeThemUp.LastCellCheckedRoomNb = 0
WakeThemUp.LastCellWakenUpIter = {}
WakeThemUp.EmptyRoomPrefix = 'empty'


local lcl = {}
lcl.player_base = __classmetatables[IsoPlayer.class].__index
lcl.player_getCurrentBuilding         = lcl.player_base.getCurrentBuilding

lcl.room_base = __classmetatables[IsoRoom.class].__index
lcl.room_getSquares  = lcl.room_base.getSquares
lcl.room_getBuilding = lcl.room_base.getBuilding
lcl.room_getRoomDef  = lcl.room_base.getRoomDef

lcl.roomDef_base        = __classmetatables[RoomDef.class].__index
lcl.roomDef_getID       = lcl.roomDef_base.getID
lcl.roomDef_isExplored  = lcl.roomDef_base.isExplored

lcl.building_base    = __classmetatables[IsoBuilding.class].__index
lcl.building_getDef  = lcl.building_base.getDef

lcl.buildingDef_base              = __classmetatables[BuildingDef.class].__index
lcl.buildingDef_getID             = lcl.buildingDef_base.getID
lcl.buildingDef_isHasBeenVisited  = lcl.buildingDef_base.isHasBeenVisited


lcl.ArrayList_base   = __classmetatables[ArrayList.class].__index
lcl.ArrayList_size   = lcl.ArrayList_base.size
lcl.ArrayList_get    = lcl.ArrayList_base.get

lcl.getCell = getCell
lcl.cell_base        = __classmetatables[IsoCell.class].__index
lcl.cell_getRoomList = lcl.cell_base.getRoomList
lcl.cell_roomSpotted = lcl.cell_base.roomSpotted

function WakeThemUp.blacklistRoom(room)
    return string.sub(room:getName(),1,string.len(WakeThemUp.EmptyRoomPrefix))==WakeThemUp.EmptyRoomPrefix
end

function WakeThemUp.isPlayerInBuilding(room)
    local roomBuilding = lcl.room_getBuilding(room)
    if roomBuilding then
        local onlinePlayers = getOnlinePlayers()
        if onlinePlayers then
            for i=0,lcl.ArrayList_size(onlinePlayers)-1 do
                local player = lcl.ArrayList_get(onlinePlayers,i)
                if player then
                    local playerBuilding = lcl.player_getCurrentBuilding(player)
                    if playerBuilding == roomBuilding then return true end
                end
            end
        end
    end
    return false
end

function WakeThemUp.isExplored(room)
    local def = lcl.room_getRoomDef(room)
    if def then
        return lcl.roomDef_isExplored(def) --or lcl.getClassPublicFieldValue(def,'bDoneSpawn')
    end
    return false
end

function WakeThemUp.isInVisitedBuilding(room)
    local roomBuilding = lcl.room_getBuilding(room)
    if roomBuilding then
        local def = lcl.building_getDef(roomBuilding)
        if def then
            return lcl.buildingDef_isHasBeenVisited(def)
        end
    end
    return false
end

function WakeThemUp.OnTick()
    local cell = lcl.getCell()
    local timestamp = nil
    if WakeThemUp.Verbose then timestamp = getTimestampMs() end
    if cell and WakeThemUp.LastCellChecked ~= cell then--occures only once at start
        WakeThemUp.LastCellChecked = cell
        WakeThemUp.LastCellCheckedRoomCheckIter = 0
        if WakeThemUp.Verbose then print('WakeThemUp.OnTick new cell.'..timestamp) end
    end

    local rooms = lcl.cell_getRoomList(cell) -- Returns an ArrayList
    local roomNumber = lcl.ArrayList_size(rooms)
    if roomNumber ~= WakeThemUp.LastCellCheckedRoomNb then--roomNumber only ever increases after game load
        if roomNumber < WakeThemUp.LastCellCheckedRoomNb then--never occures in B41.78.16
            print('WakeThemUp.OnTick ERROR number of rooms decreased from '..roomNumber..' to '..(timestamp or getTimestampMs()))--if this log occures, consider stop using that mod
            WakeThemUp.LastCellCheckedRoomCheckIter = 0;--do as if it was a whole other world
        end
        WakeThemUp.LastCellCheckedRoomNb = roomNumber
        if WakeThemUp.Verbose then print('WakeThemUp.OnTick number of rooms changed to '..roomNumber..' '..timestamp) end
    end
    
    if WakeThemUp.LastCellCheckedRoomCheckIter and WakeThemUp.LastCellCheckedRoomCheckIter < roomNumber then
        local room = lcl.ArrayList_get(rooms,WakeThemUp.LastCellCheckedRoomCheckIter)
        if (room ~= nil) then
            if WakeThemUp.isInVisitedBuilding(room) then
                if WakeThemUp.Verbose then print('WakeThemUp.OnTick bypass room in visited building '..WakeThemUp.LastCellCheckedRoomCheckIter..' '..timestamp..' '..(room:getName() or 'nil')) end
            elseif WakeThemUp.isExplored(room) then
                if WakeThemUp.Verbose then print('WakeThemUp.OnTick bypass explored room '..WakeThemUp.LastCellCheckedRoomCheckIter..' '..timestamp..' '..(room:getName() or 'nil')) end
            elseif WakeThemUp.blacklistRoom(room) then
                if WakeThemUp.Verbose then print('WakeThemUp.OnTick bypass blacklist room '..WakeThemUp.LastCellCheckedRoomCheckIter..' '..timestamp..' '..(room:getName() or 'nil')) end
            elseif WakeThemUp.isPlayerInBuilding(room) then
                if WakeThemUp.Verbose then print('WakeThemUp.OnTick bypass room with player '..WakeThemUp.LastCellCheckedRoomCheckIter..' '..timestamp..' '..(room:getName() or 'nil')) end
            else
                lcl.cell_roomSpotted(cell,room)
            end
            if WakeThemUp.Verbose then WakeThemUp.LastCellWakenUpIter[tostring(room)] = {it=WakeThemUp.LastCellCheckedRoomCheckIter,ts=timestamp} end
        end
        WakeThemUp.LastCellCheckedRoomCheckIter = WakeThemUp.LastCellCheckedRoomCheckIter + 1
    end
end

if not isClient() then
    Events.OnTick.Add(WakeThemUp.OnTick)
end

--just for RE analysis
function WakeThemUp.OnSeeNewRoom(isoRoom)
    if WakeThemUp.Verbose then
        local struct = WakeThemUp.LastCellWakenUpIter[tostring(isoRoom)]
        local addedStr = ''
        if struct then
            addedStr = ' '..struct.it..' '..struct.ts
        end
        print ("WakeThemUp.OnSeeNewRoom "..tostring(isoRoom or 'nil')..addedStr)
    end
end
--
if WakeThemUp.Verbose then Events.OnSeeNewRoom.Add(WakeThemUp.OnSeeNewRoom) end
