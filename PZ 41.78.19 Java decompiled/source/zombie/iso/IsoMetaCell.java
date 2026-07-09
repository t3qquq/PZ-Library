// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.util.ArrayList;
import java.util.Set;
import zombie.Lua.LuaEventManager;
import zombie.core.math.PZMath;
import zombie.iso.objects.IsoMannequin;

public final class IsoMetaCell {
    public final ArrayList<IsoMetaGrid.VehicleZone> vehicleZones = new ArrayList<>();
    public final IsoMetaChunk[] ChunkMap = new IsoMetaChunk[900];
    public LotHeader info = null;
    public final ArrayList<IsoMetaGrid.Trigger> triggers = new ArrayList<>();
    private int wx = 0;
    private int wy = 0;
    public final ArrayList<IsoMannequin.MannequinZone> mannequinZones = new ArrayList<>();
    public final ArrayList<IsoMetaGrid.RoomTone> roomTones = new ArrayList<>();

    public IsoMetaCell(int _wx, int _wy) {
        this.wx = _wx;
        this.wy = _wy;

        for (int int0 = 0; int0 < 900; int0++) {
            this.ChunkMap[int0] = new IsoMetaChunk();
        }
    }

    public void addTrigger(BuildingDef def, int triggerRange, int zombieExclusionRange, String type) {
        this.triggers.add(new IsoMetaGrid.Trigger(def, triggerRange, zombieExclusionRange, type));
    }

    public void checkTriggers() {
        if (IsoCamera.CamCharacter != null) {
            int int0 = (int)IsoCamera.CamCharacter.getX();
            int int1 = (int)IsoCamera.CamCharacter.getY();

            for (int int2 = 0; int2 < this.triggers.size(); int2++) {
                IsoMetaGrid.Trigger trigger = this.triggers.get(int2);
                if (int0 >= trigger.def.x - trigger.triggerRange
                    && int0 <= trigger.def.x2 + trigger.triggerRange
                    && int1 >= trigger.def.y - trigger.triggerRange
                    && int1 <= trigger.def.y2 + trigger.triggerRange) {
                    if (!trigger.triggered) {
                        LuaEventManager.triggerEvent("OnTriggerNPCEvent", trigger.type, trigger.data, trigger.def);
                    }

                    LuaEventManager.triggerEvent("OnMultiTriggerNPCEvent", trigger.type, trigger.data, trigger.def);
                    trigger.triggered = true;
                }
            }
        }
    }

    public IsoMetaChunk getChunk(int x, int y) {
        return y < 30 && x < 30 && x >= 0 && y >= 0 ? this.ChunkMap[y * 30 + x] : null;
    }

    public void addZone(IsoMetaGrid.Zone zone, int cellX, int cellY) {
        int int0 = zone.x / 10;
        int int1 = zone.y / 10;
        int int2 = (zone.x + zone.w) / 10;
        if ((zone.x + zone.w) % 10 == 0) {
            int2--;
        }

        int int3 = (zone.y + zone.h) / 10;
        if ((zone.y + zone.h) % 10 == 0) {
            int3--;
        }

        int0 = PZMath.clamp(int0, cellX / 10, (cellX + 300) / 10);
        int1 = PZMath.clamp(int1, cellY / 10, (cellY + 300) / 10);
        int2 = PZMath.clamp(int2, cellX / 10, (cellX + 300) / 10 - 1);
        int3 = PZMath.clamp(int3, cellY / 10, (cellY + 300) / 10 - 1);

        for (int int4 = int1; int4 <= int3; int4++) {
            for (int int5 = int0; int5 <= int2; int5++) {
                if (zone.intersects(int5 * 10, int4 * 10, zone.z, 10, 10)) {
                    int int6 = int5 - cellX / 10 + (int4 - cellY / 10) * 30;
                    if (this.ChunkMap[int6] != null) {
                        this.ChunkMap[int6].addZone(zone);
                    }
                }
            }
        }
    }

    public void removeZone(IsoMetaGrid.Zone zone) {
        int int0 = (zone.x + zone.w) / 10;
        if ((zone.x + zone.w) % 10 == 0) {
            int0--;
        }

        int int1 = (zone.y + zone.h) / 10;
        if ((zone.y + zone.h) % 10 == 0) {
            int1--;
        }

        int int2 = this.wx * 300;
        int int3 = this.wy * 300;

        for (int int4 = zone.y / 10; int4 <= int1; int4++) {
            for (int int5 = zone.x / 10; int5 <= int0; int5++) {
                if (int5 >= int2 / 10 && int5 < (int2 + 300) / 10 && int4 >= int3 / 10 && int4 < (int3 + 300) / 10) {
                    int int6 = int5 - int2 / 10 + (int4 - int3 / 10) * 30;
                    if (this.ChunkMap[int6] != null) {
                        this.ChunkMap[int6].removeZone(zone);
                    }
                }
            }
        }
    }

    public void addRoom(RoomDef room, int cellX, int cellY) {
        int int0 = room.x2 / 10;
        if (room.x2 % 10 == 0) {
            int0--;
        }

        int int1 = room.y2 / 10;
        if (room.y2 % 10 == 0) {
            int1--;
        }

        for (int int2 = room.y / 10; int2 <= int1; int2++) {
            for (int int3 = room.x / 10; int3 <= int0; int3++) {
                if (int3 >= cellX / 10 && int3 < (cellX + 300) / 10 && int2 >= cellY / 10 && int2 < (cellY + 300) / 10) {
                    int int4 = int3 - cellX / 10 + (int2 - cellY / 10) * 30;
                    if (this.ChunkMap[int4] != null) {
                        this.ChunkMap[int4].addRoom(room);
                    }
                }
            }
        }
    }

    public void getZonesUnique(Set<IsoMetaGrid.Zone> result) {
        for (int int0 = 0; int0 < this.ChunkMap.length; int0++) {
            IsoMetaChunk metaChunk = this.ChunkMap[int0];
            if (metaChunk != null) {
                metaChunk.getZonesUnique(result);
            }
        }
    }

    public void getZonesIntersecting(int x, int y, int z, int w, int h, ArrayList<IsoMetaGrid.Zone> result) {
        int int0 = (x + w) / 10;
        if ((x + w) % 10 == 0) {
            int0--;
        }

        int int1 = (y + h) / 10;
        if ((y + h) % 10 == 0) {
            int1--;
        }

        int int2 = this.wx * 300;
        int int3 = this.wy * 300;

        for (int int4 = y / 10; int4 <= int1; int4++) {
            for (int int5 = x / 10; int5 <= int0; int5++) {
                if (int5 >= int2 / 10 && int5 < (int2 + 300) / 10 && int4 >= int3 / 10 && int4 < (int3 + 300) / 10) {
                    int int6 = int5 - int2 / 10 + (int4 - int3 / 10) * 30;
                    if (this.ChunkMap[int6] != null) {
                        this.ChunkMap[int6].getZonesIntersecting(x, y, z, w, h, result);
                    }
                }
            }
        }
    }

    public void getRoomsIntersecting(int x, int y, int w, int h, ArrayList<RoomDef> result) {
        int int0 = (x + w) / 10;
        if ((x + w) % 10 == 0) {
            int0--;
        }

        int int1 = (y + h) / 10;
        if ((y + h) % 10 == 0) {
            int1--;
        }

        int int2 = this.wx * 300;
        int int3 = this.wy * 300;

        for (int int4 = y / 10; int4 <= int1; int4++) {
            for (int int5 = x / 10; int5 <= int0; int5++) {
                if (int5 >= int2 / 10 && int5 < (int2 + 300) / 10 && int4 >= int3 / 10 && int4 < (int3 + 300) / 10) {
                    int int6 = int5 - int2 / 10 + (int4 - int3 / 10) * 30;
                    if (this.ChunkMap[int6] != null) {
                        this.ChunkMap[int6].getRoomsIntersecting(x, y, w, h, result);
                    }
                }
            }
        }
    }

    public void Dispose() {
        for (int int0 = 0; int0 < this.ChunkMap.length; int0++) {
            IsoMetaChunk metaChunk = this.ChunkMap[int0];
            if (metaChunk != null) {
                metaChunk.Dispose();
                this.ChunkMap[int0] = null;
            }
        }

        this.info = null;
        this.mannequinZones.clear();
        this.roomTones.clear();
    }
}
