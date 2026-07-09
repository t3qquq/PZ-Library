// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import zombie.SandboxOptions;
import zombie.core.Rand;

public final class IsoMetaChunk {
    public static final float zombiesMinPerChunk = 0.06F;
    public static final float zombiesFullPerChunk = 12.0F;
    private int ZombieIntensity = 0;
    private IsoMetaGrid.Zone[] zones;
    private int zonesSize;
    private RoomDef[] rooms;
    private int roomsSize;

    public float getZombieIntensity(boolean bRandom) {
        float float0 = this.ZombieIntensity;
        float float1 = float0 / 255.0F;
        if (SandboxOptions.instance.Distribution.getValue() == 2) {
            float0 = 128.0F;
            float1 = 0.5F;
        }

        float0 *= 0.5F;
        if (SandboxOptions.instance.Zombies.getValue() == 1) {
            float0 *= 4.0F;
        } else if (SandboxOptions.instance.Zombies.getValue() == 2) {
            float0 *= 3.0F;
        } else if (SandboxOptions.instance.Zombies.getValue() == 3) {
            float0 *= 2.0F;
        } else if (SandboxOptions.instance.Zombies.getValue() == 5) {
            float0 *= 0.35F;
        } else if (SandboxOptions.instance.Zombies.getValue() == 6) {
            float0 = 0.0F;
        }

        float1 = float0 / 255.0F;
        float float2 = 11.94F;
        float2 *= float1;
        float0 = 0.06F + float2;
        if (!bRandom) {
            return float0;
        } else {
            float float3 = float1 * 10.0F;
            if (Rand.Next(3) == 0) {
                return 0.0F;
            } else {
                float3 *= 0.5F;
                int int0 = 1000;
                if (SandboxOptions.instance.Zombies.getValue() == 1) {
                    int0 = (int)(int0 / 2.0F);
                } else if (SandboxOptions.instance.Zombies.getValue() == 2) {
                    int0 = (int)(int0 / 1.7F);
                } else if (SandboxOptions.instance.Zombies.getValue() == 3) {
                    int0 = (int)(int0 / 1.5F);
                } else if (SandboxOptions.instance.Zombies.getValue() == 5) {
                    int0 = (int)(int0 * 1.5F);
                }

                if (Rand.Next(int0) < float3 && IsoWorld.getZombiesEnabled()) {
                    float0 = 120.0F;
                    if (float0 > 12.0F) {
                        float0 = 12.0F;
                    }
                }

                return float0;
            }
        }
    }

    public float getZombieIntensity() {
        return this.getZombieIntensity(true);
    }

    public void setZombieIntensity(int zombieIntensity) {
        if (zombieIntensity >= 0) {
            this.ZombieIntensity = zombieIntensity;
        }
    }

    public float getLootZombieIntensity() {
        float float0 = this.ZombieIntensity;
        float float1 = float0 / 255.0F;
        float1 = float0 / 255.0F;
        float float2 = 11.94F;
        float2 *= float1;
        float0 = 0.06F + float2;
        float float3 = float1 * 10.0F;
        float1 = float1 * float1 * float1;
        if (Rand.Next(300) <= float3) {
            float0 = 120.0F;
        }

        return IsoWorld.getZombiesDisabled() ? 400.0F : float0;
    }

    public int getUnadjustedZombieIntensity() {
        return this.ZombieIntensity;
    }

    public void addZone(IsoMetaGrid.Zone zone) {
        if (this.zones == null) {
            this.zones = new IsoMetaGrid.Zone[8];
        }

        if (this.zonesSize == this.zones.length) {
            IsoMetaGrid.Zone[] zonesx = new IsoMetaGrid.Zone[this.zones.length + 8];
            System.arraycopy(this.zones, 0, zonesx, 0, this.zonesSize);
            this.zones = zonesx;
        }

        this.zones[this.zonesSize++] = zone;
    }

    public void removeZone(IsoMetaGrid.Zone zone) {
        if (this.zones != null) {
            for (int int0 = 0; int0 < this.zonesSize; int0++) {
                if (this.zones[int0] == zone) {
                    while (int0 < this.zonesSize - 1) {
                        this.zones[int0] = this.zones[int0 + 1];
                        int0++;
                    }

                    this.zones[this.zonesSize - 1] = null;
                    this.zonesSize--;
                    break;
                }
            }
        }
    }

    public IsoMetaGrid.Zone getZone(int index) {
        return index >= 0 && index < this.zonesSize ? this.zones[index] : null;
    }

    public IsoMetaGrid.Zone getZoneAt(int x, int y, int z) {
        if (this.zones != null && this.zonesSize > 0) {
            IsoMetaGrid.Zone zone0 = null;

            for (int int0 = this.zonesSize - 1; int0 >= 0; int0--) {
                IsoMetaGrid.Zone zone1 = this.zones[int0];
                if (zone1.contains(x, y, z)) {
                    if (zone1.isPreferredZoneForSquare) {
                        return zone1;
                    }

                    if (zone0 == null) {
                        zone0 = zone1;
                    }
                }
            }

            return zone0;
        } else {
            return null;
        }
    }

    public ArrayList<IsoMetaGrid.Zone> getZonesAt(int x, int y, int z, ArrayList<IsoMetaGrid.Zone> result) {
        for (int int0 = 0; int0 < this.zonesSize; int0++) {
            IsoMetaGrid.Zone zone = this.zones[int0];
            if (zone.contains(x, y, z)) {
                result.add(zone);
            }
        }

        return result;
    }

    public void getZonesUnique(Set<IsoMetaGrid.Zone> result) {
        for (int int0 = 0; int0 < this.zonesSize; int0++) {
            IsoMetaGrid.Zone zone = this.zones[int0];
            result.add(zone);
        }
    }

    public void getZonesIntersecting(int x, int y, int z, int w, int h, ArrayList<IsoMetaGrid.Zone> result) {
        for (int int0 = 0; int0 < this.zonesSize; int0++) {
            IsoMetaGrid.Zone zone = this.zones[int0];
            if (!result.contains(zone) && zone.intersects(x, y, z, w, h)) {
                result.add(zone);
            }
        }
    }

    public void clearZones() {
        if (this.zones != null) {
            for (int int0 = 0; int0 < this.zones.length; int0++) {
                this.zones[int0] = null;
            }
        }

        this.zones = null;
        this.zonesSize = 0;
    }

    public void clearRooms() {
        if (this.rooms != null) {
            for (int int0 = 0; int0 < this.rooms.length; int0++) {
                this.rooms[int0] = null;
            }
        }

        this.rooms = null;
        this.roomsSize = 0;
    }

    public int numZones() {
        return this.zonesSize;
    }

    public void addRoom(RoomDef room) {
        if (this.rooms == null) {
            this.rooms = new RoomDef[8];
        }

        if (this.roomsSize == this.rooms.length) {
            RoomDef[] roomDefs = new RoomDef[this.rooms.length + 8];
            System.arraycopy(this.rooms, 0, roomDefs, 0, this.roomsSize);
            this.rooms = roomDefs;
        }

        this.rooms[this.roomsSize++] = room;
    }

    public RoomDef getRoomAt(int x, int y, int z) {
        for (int int0 = 0; int0 < this.roomsSize; int0++) {
            RoomDef roomDef = this.rooms[int0];
            if (!roomDef.isEmptyOutside() && roomDef.level == z) {
                for (int int1 = 0; int1 < roomDef.rects.size(); int1++) {
                    RoomDef.RoomRect roomRect = roomDef.rects.get(int1);
                    if (roomRect.x <= x && roomRect.y <= y && x < roomRect.getX2() && y < roomRect.getY2()) {
                        return roomDef;
                    }
                }
            }
        }

        return null;
    }

    public RoomDef getEmptyOutsideAt(int x, int y, int z) {
        for (int int0 = 0; int0 < this.roomsSize; int0++) {
            RoomDef roomDef = this.rooms[int0];
            if (roomDef.isEmptyOutside() && roomDef.level == z) {
                for (int int1 = 0; int1 < roomDef.rects.size(); int1++) {
                    RoomDef.RoomRect roomRect = roomDef.rects.get(int1);
                    if (roomRect.x <= x && roomRect.y <= y && x < roomRect.getX2() && y < roomRect.getY2()) {
                        return roomDef;
                    }
                }
            }
        }

        return null;
    }

    public int getNumRooms() {
        return this.roomsSize;
    }

    public void getRoomsIntersecting(int x, int y, int w, int h, ArrayList<RoomDef> result) {
        for (int int0 = 0; int0 < this.roomsSize; int0++) {
            RoomDef roomDef = this.rooms[int0];
            if (!roomDef.isEmptyOutside() && !result.contains(roomDef) && roomDef.intersects(x, y, w, h)) {
                result.add(roomDef);
            }
        }
    }

    public void Dispose() {
        if (this.rooms != null) {
            Arrays.fill(this.rooms, null);
        }

        if (this.zones != null) {
            Arrays.fill(this.zones, null);
        }
    }
}
