// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;

public final class LotHeader {
    public int cellX;
    public int cellY;
    public int width = 0;
    public int height = 0;
    public int levels = 0;
    public int version = 0;
    public final HashMap<Integer, RoomDef> Rooms = new HashMap<>();
    public final TLongObjectHashMap<RoomDef> RoomByMetaID = new TLongObjectHashMap<>();
    public final ArrayList<RoomDef> RoomList = new ArrayList<>();
    public final ArrayList<BuildingDef> Buildings = new ArrayList<>();
    public final TLongObjectHashMap<BuildingDef> BuildingByMetaID = new TLongObjectHashMap<>();
    public final HashMap<Integer, IsoRoom> isoRooms = new HashMap<>();
    public final HashMap<Integer, IsoBuilding> isoBuildings = new HashMap<>();
    public boolean bFixed2x;
    protected final ArrayList<String> tilesUsed = new ArrayList<>();

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getLevels() {
        return this.levels;
    }

    public IsoRoom getRoom(int roomID) {
        RoomDef roomDef = this.Rooms.get(roomID);
        if (!this.isoRooms.containsKey(roomID)) {
            IsoRoom room = new IsoRoom();
            room.rects.addAll(roomDef.rects);
            room.RoomDef = roomDef.name;
            room.def = roomDef;
            room.layer = roomDef.level;
            IsoWorld.instance.CurrentCell.getRoomList().add(room);
            if (roomDef.building == null) {
                roomDef.building = new BuildingDef();
                roomDef.building.ID = this.Buildings.size();
                roomDef.building.rooms.add(roomDef);
                roomDef.building.CalculateBounds(new ArrayList<>());
                roomDef.building.metaID = roomDef.building.calculateMetaID(this.cellX, this.cellY);
                this.Buildings.add(roomDef.building);
            }

            int int0 = roomDef.building.ID;
            this.isoRooms.put(roomID, room);
            if (!this.isoBuildings.containsKey(int0)) {
                room.building = new IsoBuilding();
                room.building.def = roomDef.building;
                this.isoBuildings.put(int0, room.building);
                room.building.CreateFrom(roomDef.building, this);
            } else {
                room.building = this.isoBuildings.get(int0);
            }

            return room;
        } else {
            return this.isoRooms.get(roomID);
        }
    }

    @Deprecated
    public int getRoomAt(int x, int y, int z) {
        for (Entry entry : this.Rooms.entrySet()) {
            RoomDef roomDef = (RoomDef)entry.getValue();

            for (int int0 = 0; int0 < roomDef.rects.size(); int0++) {
                RoomDef.RoomRect roomRect = roomDef.rects.get(int0);
                if (roomRect.x <= x && roomRect.y <= y && roomDef.level == z && roomRect.getX2() > x && roomRect.getY2() > y) {
                    return (Integer)entry.getKey();
                }
            }
        }

        return -1;
    }

    public void Dispose() {
        this.Rooms.clear();
        this.RoomByMetaID.clear();
        this.RoomList.clear();
        this.Buildings.clear();
        this.BuildingByMetaID.clear();
        this.isoRooms.clear();
        this.isoBuildings.clear();
        this.tilesUsed.clear();
    }
}
