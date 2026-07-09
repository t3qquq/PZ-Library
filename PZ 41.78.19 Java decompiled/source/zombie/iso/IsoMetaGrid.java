// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import gnu.trove.list.array.TIntArrayList;
import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import org.joml.Vector2f;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.MapGroups;
import zombie.SandboxOptions;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.characters.Faction;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferWriter;
import zombie.core.stash.StashSystem;
import zombie.debug.DebugLog;
import zombie.gameStates.ChooseGameInfo;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.areas.SafeHouse;
import zombie.iso.objects.IsoMannequin;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.randomizedWorld.randomizedBuilding.RBBasic;
import zombie.randomizedWorld.randomizedZoneStory.RandomizedZoneStoryBase;
import zombie.util.BufferedRandomAccessFile;
import zombie.util.SharedStrings;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.Clipper;
import zombie.vehicles.ClipperOffset;
import zombie.vehicles.PolygonalMap2;

public final class IsoMetaGrid {
    private static final int NUM_LOADER_THREADS = 8;
    private static ArrayList<String> s_PreferredZoneTypes = new ArrayList<>();
    private static Clipper s_clipper = null;
    private static ClipperOffset s_clipperOffset = null;
    private static ByteBuffer s_clipperBuffer = null;
    private static final ThreadLocal<IsoGameCharacter.Location> TL_Location = ThreadLocal.withInitial(IsoGameCharacter.Location::new);
    private static final ThreadLocal<ArrayList<IsoMetaGrid.Zone>> TL_ZoneList = ThreadLocal.withInitial(ArrayList::new);
    static Rectangle a = new Rectangle();
    static Rectangle b = new Rectangle();
    static ArrayList<RoomDef> roomChoices = new ArrayList<>(50);
    private final ArrayList<RoomDef> tempRooms = new ArrayList<>();
    private final ArrayList<IsoMetaGrid.Zone> tempZones1 = new ArrayList<>();
    private final ArrayList<IsoMetaGrid.Zone> tempZones2 = new ArrayList<>();
    private final IsoMetaGrid.MetaGridLoaderThread[] threads = new IsoMetaGrid.MetaGridLoaderThread[8];
    public int minX = 10000000;
    public int minY = 10000000;
    public int maxX = -10000000;
    public int maxY = -10000000;
    public final ArrayList<IsoMetaGrid.Zone> Zones = new ArrayList<>();
    public final ArrayList<BuildingDef> Buildings = new ArrayList<>();
    public final ArrayList<IsoMetaGrid.VehicleZone> VehiclesZones = new ArrayList<>();
    public IsoMetaCell[][] Grid;
    public final ArrayList<IsoGameCharacter> MetaCharacters = new ArrayList<>();
    final ArrayList<Vector2> HighZombieList = new ArrayList<>();
    private int width;
    private int height;
    private final SharedStrings sharedStrings = new SharedStrings();
    private long createStartTime;

    public void AddToMeta(IsoGameCharacter isoPlayer) {
        IsoWorld.instance.CurrentCell.Remove(isoPlayer);
        if (!this.MetaCharacters.contains(isoPlayer)) {
            this.MetaCharacters.add(isoPlayer);
        }
    }

    public void RemoveFromMeta(IsoPlayer isoPlayer) {
        this.MetaCharacters.remove(isoPlayer);
        if (!IsoWorld.instance.CurrentCell.getObjectList().contains(isoPlayer)) {
            IsoWorld.instance.CurrentCell.getObjectList().add(isoPlayer);
        }
    }

    public int getMinX() {
        return this.minX;
    }

    public int getMinY() {
        return this.minY;
    }

    public int getMaxX() {
        return this.maxX;
    }

    public int getMaxY() {
        return this.maxY;
    }

    public IsoMetaGrid.Zone getZoneAt(int x, int y, int z) {
        IsoMetaChunk metaChunk = this.getChunkDataFromTile(x, y);
        return metaChunk != null ? metaChunk.getZoneAt(x, y, z) : null;
    }

    public ArrayList<IsoMetaGrid.Zone> getZonesAt(int x, int y, int z) {
        return this.getZonesAt(x, y, z, new ArrayList<>());
    }

    public ArrayList<IsoMetaGrid.Zone> getZonesAt(int x, int y, int z, ArrayList<IsoMetaGrid.Zone> result) {
        IsoMetaChunk metaChunk = this.getChunkDataFromTile(x, y);
        return metaChunk != null ? metaChunk.getZonesAt(x, y, z, result) : result;
    }

    public ArrayList<IsoMetaGrid.Zone> getZonesIntersecting(int x, int y, int z, int w, int h) {
        ArrayList arrayList = new ArrayList();
        return this.getZonesIntersecting(x, y, z, w, h, arrayList);
    }

    public ArrayList<IsoMetaGrid.Zone> getZonesIntersecting(int x, int y, int z, int w, int h, ArrayList<IsoMetaGrid.Zone> result) {
        for (int int0 = y / 300; int0 <= (y + h) / 300; int0++) {
            for (int int1 = x / 300; int1 <= (x + w) / 300; int1++) {
                if (int1 >= this.minX && int1 <= this.maxX && int0 >= this.minY && int0 <= this.maxY && this.Grid[int1 - this.minX][int0 - this.minY] != null) {
                    this.Grid[int1 - this.minX][int0 - this.minY].getZonesIntersecting(x, y, z, w, h, result);
                }
            }
        }

        return result;
    }

    public IsoMetaGrid.Zone getZoneWithBoundsAndType(int x, int y, int z, int w, int h, String type) {
        ArrayList arrayList = TL_ZoneList.get();
        arrayList.clear();
        this.getZonesIntersecting(x, y, z, w, h, arrayList);

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)arrayList.get(int0);
            if (zone.x == x && zone.y == y && zone.z == z && zone.w == w && zone.h == h && StringUtils.equalsIgnoreCase(zone.type, type)) {
                return zone;
            }
        }

        return null;
    }

    public IsoMetaGrid.VehicleZone getVehicleZoneAt(int x, int y, int z) {
        IsoMetaCell metaCell = this.getMetaGridFromTile(x, y);
        if (metaCell != null && !metaCell.vehicleZones.isEmpty()) {
            for (int int0 = 0; int0 < metaCell.vehicleZones.size(); int0++) {
                IsoMetaGrid.VehicleZone vehicleZone = metaCell.vehicleZones.get(int0);
                if (vehicleZone.contains(x, y, z)) {
                    return vehicleZone;
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public BuildingDef getBuildingAt(int x, int y) {
        for (int int0 = 0; int0 < this.Buildings.size(); int0++) {
            BuildingDef buildingDef = this.Buildings.get(int0);
            if (buildingDef.x <= x && buildingDef.y <= y && buildingDef.getW() > x - buildingDef.x && buildingDef.getH() > y - buildingDef.y) {
                return buildingDef;
            }
        }

        return null;
    }

    public BuildingDef getBuildingAtRelax(int x, int y) {
        for (int int0 = 0; int0 < this.Buildings.size(); int0++) {
            BuildingDef buildingDef = this.Buildings.get(int0);
            if (buildingDef.x <= x + 1 && buildingDef.y <= y + 1 && buildingDef.getW() > x - buildingDef.x - 1 && buildingDef.getH() > y - buildingDef.y - 1) {
                return buildingDef;
            }
        }

        return null;
    }

    public RoomDef getRoomAt(int x, int y, int z) {
        IsoMetaChunk metaChunk = this.getChunkDataFromTile(x, y);
        return metaChunk != null ? metaChunk.getRoomAt(x, y, z) : null;
    }

    public RoomDef getEmptyOutsideAt(int x, int y, int z) {
        IsoMetaChunk metaChunk = this.getChunkDataFromTile(x, y);
        return metaChunk != null ? metaChunk.getEmptyOutsideAt(x, y, z) : null;
    }

    public void getRoomsIntersecting(int x, int y, int w, int h, ArrayList<RoomDef> roomDefs) {
        for (int int0 = y / 300; int0 <= (y + this.height) / 300; int0++) {
            for (int int1 = x / 300; int1 <= (x + this.width) / 300; int1++) {
                if (int1 >= this.minX && int1 <= this.maxX && int0 >= this.minY && int0 <= this.maxY) {
                    IsoMetaCell metaCell = this.Grid[int1 - this.minX][int0 - this.minY];
                    if (metaCell != null) {
                        metaCell.getRoomsIntersecting(x, y, w, h, roomDefs);
                    }
                }
            }
        }
    }

    public int countRoomsIntersecting(int x, int y, int w, int h) {
        this.tempRooms.clear();

        for (int int0 = y / 300; int0 <= (y + this.height) / 300; int0++) {
            for (int int1 = x / 300; int1 <= (x + this.width) / 300; int1++) {
                if (int1 >= this.minX && int1 <= this.maxX && int0 >= this.minY && int0 <= this.maxY) {
                    IsoMetaCell metaCell = this.Grid[int1 - this.minX][int0 - this.minY];
                    if (metaCell != null) {
                        metaCell.getRoomsIntersecting(x, y, w, h, this.tempRooms);
                    }
                }
            }
        }

        return this.tempRooms.size();
    }

    public int countNearbyBuildingsRooms(IsoPlayer isoPlayer) {
        int int0 = (int)isoPlayer.getX() - 20;
        int int1 = (int)isoPlayer.getY() - 20;
        byte byte0 = 40;
        byte byte1 = 40;
        return this.countRoomsIntersecting(int0, int1, byte0, byte1);
    }

    private boolean isInside(IsoMetaGrid.Zone zone, BuildingDef buildingDef) {
        a.x = zone.x;
        a.y = zone.y;
        a.width = zone.w;
        a.height = zone.h;
        b.x = buildingDef.x;
        b.y = buildingDef.y;
        b.width = buildingDef.getW();
        b.height = buildingDef.getH();
        return a.contains(b);
    }

    private boolean isAdjacent(IsoMetaGrid.Zone zone0, IsoMetaGrid.Zone zone1) {
        if (zone0 == zone1) {
            return false;
        } else {
            a.x = zone0.x;
            a.y = zone0.y;
            a.width = zone0.w;
            a.height = zone0.h;
            b.x = zone1.x;
            b.y = zone1.y;
            b.width = zone1.w;
            b.height = zone1.h;
            a.x--;
            a.y--;
            a.width += 2;
            a.height += 2;
            b.x--;
            b.y--;
            b.width += 2;
            b.height += 2;
            return a.intersects(b);
        }
    }

    public IsoMetaGrid.Zone registerZone(String name, String type, int x, int y, int z, int _width, int _height) {
        return this.registerZone(name, type, x, y, z, _width, _height, IsoMetaGrid.ZoneGeometryType.INVALID, null, 0);
    }

    public IsoMetaGrid.Zone registerZone(
        String name,
        String type,
        int x,
        int y,
        int z,
        int _width,
        int _height,
        IsoMetaGrid.ZoneGeometryType geometryType,
        TIntArrayList points,
        int polylineWidth
    ) {
        name = this.sharedStrings.get(name);
        type = this.sharedStrings.get(type);
        IsoMetaGrid.Zone zone = new IsoMetaGrid.Zone(name, type, x, y, z, _width, _height);
        zone.geometryType = geometryType;
        if (points != null) {
            zone.points.addAll(points);
            zone.polylineWidth = polylineWidth;
        }

        zone.isPreferredZoneForSquare = isPreferredZoneForSquare(type);
        if (x >= this.minX * 300 - 100
            && y >= this.minY * 300 - 100
            && x + _width <= (this.maxX + 1) * 300 + 100
            && y + _height <= (this.maxY + 1) * 300 + 100
            && z >= 0
            && z < 8
            && _width <= 600
            && _height <= 600) {
            this.addZone(zone);
            return zone;
        } else {
            return zone;
        }
    }

    public IsoMetaGrid.Zone registerGeometryZone(String name, String type, int z, String geometry, KahluaTable pointsTable, KahluaTable properties) {
        int int0 = Integer.MAX_VALUE;
        int int1 = Integer.MAX_VALUE;
        int int2 = Integer.MIN_VALUE;
        int int3 = Integer.MIN_VALUE;
        TIntArrayList tIntArrayList = new TIntArrayList(pointsTable.len());

        for (byte byte0 = 0; byte0 < pointsTable.len(); byte0 += 2) {
            Object object0 = pointsTable.rawget(byte0 + 1);
            Object object1 = pointsTable.rawget(byte0 + 2);
            int int4 = ((Double)object0).intValue();
            int int5 = ((Double)object1).intValue();
            tIntArrayList.add(int4);
            tIntArrayList.add(int5);
            int0 = Math.min(int0, int4);
            int1 = Math.min(int1, int5);
            int2 = Math.max(int2, int4);
            int3 = Math.max(int3, int5);
        }
        IsoMetaGrid.ZoneGeometryType zoneGeometryType = switch (geometry) {
            case "point" -> IsoMetaGrid.ZoneGeometryType.Point;
            case "polygon" -> IsoMetaGrid.ZoneGeometryType.Polygon;
            case "polyline" -> IsoMetaGrid.ZoneGeometryType.Polyline;
            default -> throw new IllegalArgumentException("unknown zone geometry type");
        };
        Double double0 = zoneGeometryType == IsoMetaGrid.ZoneGeometryType.Polyline && properties != null
            ? Type.tryCastTo(properties.rawget("LineWidth"), Double.class)
            : null;
        if (double0 != null) {
            int[] ints = new int[4];
            this.calculatePolylineOutlineBounds(tIntArrayList, double0.intValue(), ints);
            int0 = ints[0];
            int1 = ints[1];
            int2 = ints[2];
            int3 = ints[3];
        }

        if (!type.equals("Vehicle") && !type.equals("ParkingStall")) {
            IsoMetaGrid.Zone zone0 = this.registerZone(
                name, type, int0, int1, z, int2 - int0 + 1, int3 - int1 + 1, zoneGeometryType, tIntArrayList, double0 == null ? 0 : double0.intValue()
            );
            tIntArrayList.clear();
            return zone0;
        } else {
            IsoMetaGrid.Zone zone1 = this.registerVehiclesZone(name, type, int0, int1, z, int2 - int0 + 1, int3 - int1 + 1, properties);
            if (zone1 != null) {
                zone1.geometryType = zoneGeometryType;
                zone1.points.addAll(tIntArrayList);
                zone1.polylineWidth = double0 == null ? 0 : double0.intValue();
            }

            return zone1;
        }
    }

    private void calculatePolylineOutlineBounds(TIntArrayList tIntArrayList, int int0, int[] ints) {
        if (s_clipperOffset == null) {
            s_clipperOffset = new ClipperOffset();
            s_clipperBuffer = ByteBuffer.allocateDirect(3072);
        }

        s_clipperOffset.clear();
        s_clipperBuffer.clear();
        float float0 = int0 % 2 == 0 ? 0.0F : 0.5F;

        for (byte byte0 = 0; byte0 < tIntArrayList.size(); byte0 += 2) {
            int int1 = tIntArrayList.get(byte0);
            int int2 = tIntArrayList.get(byte0 + 1);
            s_clipperBuffer.putFloat(int1 + float0);
            s_clipperBuffer.putFloat(int2 + float0);
        }

        s_clipperBuffer.flip();
        s_clipperOffset.addPath(tIntArrayList.size() / 2, s_clipperBuffer, ClipperOffset.JoinType.jtMiter.ordinal(), ClipperOffset.EndType.etOpenButt.ordinal());
        s_clipperOffset.execute(int0 / 2.0F);
        int int3 = s_clipperOffset.getPolygonCount();
        if (int3 < 1) {
            DebugLog.General.warn("Failed to generate polyline outline");
        } else {
            s_clipperBuffer.clear();
            s_clipperOffset.getPolygon(0, s_clipperBuffer);
            short short0 = s_clipperBuffer.getShort();
            float float1 = Float.MAX_VALUE;
            float float2 = Float.MAX_VALUE;
            float float3 = -Float.MAX_VALUE;
            float float4 = -Float.MAX_VALUE;

            for (int int4 = 0; int4 < short0; int4++) {
                float float5 = s_clipperBuffer.getFloat();
                float float6 = s_clipperBuffer.getFloat();
                float1 = PZMath.min(float1, float5);
                float2 = PZMath.min(float2, float6);
                float3 = PZMath.max(float3, float5);
                float4 = PZMath.max(float4, float6);
            }

            ints[0] = (int)PZMath.floor(float1);
            ints[1] = (int)PZMath.floor(float2);
            ints[2] = (int)PZMath.ceil(float3);
            ints[3] = (int)PZMath.ceil(float4);
        }
    }

    @Deprecated
    public IsoMetaGrid.Zone registerZoneNoOverlap(String name, String type, int x, int y, int z, int _width, int _height) {
        return x >= this.minX * 300 - 100
                && y >= this.minY * 300 - 100
                && x + _width <= (this.maxX + 1) * 300 + 100
                && y + _height <= (this.maxY + 1) * 300 + 100
                && z >= 0
                && z < 8
                && _width <= 600
                && _height <= 600
            ? this.registerZone(name, type, x, y, z, _width, _height)
            : null;
    }

    private void addZone(IsoMetaGrid.Zone zone) {
        this.Zones.add(zone);

        for (int int0 = zone.y / 300; int0 <= (zone.y + zone.h) / 300; int0++) {
            for (int int1 = zone.x / 300; int1 <= (zone.x + zone.w) / 300; int1++) {
                if (int1 >= this.minX && int1 <= this.maxX && int0 >= this.minY && int0 <= this.maxY && this.Grid[int1 - this.minX][int0 - this.minY] != null) {
                    this.Grid[int1 - this.minX][int0 - this.minY].addZone(zone, int1 * 300, int0 * 300);
                }
            }
        }
    }

    public void removeZone(IsoMetaGrid.Zone zone) {
        this.Zones.remove(zone);

        for (int int0 = zone.y / 300; int0 <= (zone.y + zone.h) / 300; int0++) {
            for (int int1 = zone.x / 300; int1 <= (zone.x + zone.w) / 300; int1++) {
                if (int1 >= this.minX && int1 <= this.maxX && int0 >= this.minY && int0 <= this.maxY && this.Grid[int1 - this.minX][int0 - this.minY] != null) {
                    this.Grid[int1 - this.minX][int0 - this.minY].removeZone(zone);
                }
            }
        }
    }

    public void removeZonesForCell(int cellX, int cellY) {
        IsoMetaCell metaCell = this.getCellData(cellX, cellY);
        if (metaCell != null) {
            ArrayList arrayList0 = this.tempZones1;
            arrayList0.clear();

            for (int int0 = 0; int0 < 900; int0++) {
                metaCell.ChunkMap[int0].getZonesIntersecting(cellX * 300, cellY * 300, 0, 300, 300, arrayList0);
            }

            for (int int1 = 0; int1 < arrayList0.size(); int1++) {
                IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)arrayList0.get(int1);
                ArrayList arrayList1 = this.tempZones2;
                if (zone.difference(cellX * 300, cellY * 300, 0, 300, 300, arrayList1)) {
                    this.removeZone(zone);

                    for (int int2 = 0; int2 < arrayList1.size(); int2++) {
                        this.addZone((IsoMetaGrid.Zone)arrayList1.get(int2));
                    }
                }
            }

            if (!metaCell.vehicleZones.isEmpty()) {
                metaCell.vehicleZones.clear();
            }

            if (!metaCell.mannequinZones.isEmpty()) {
                metaCell.mannequinZones.clear();
            }
        }
    }

    public void removeZonesForLotDirectory(String lotDir) {
        if (!this.Zones.isEmpty()) {
            File file = new File(ZomboidFileSystem.instance.getString("media/maps/" + lotDir + "/"));
            if (file.isDirectory()) {
                ChooseGameInfo.Map map = ChooseGameInfo.getMapDetails(lotDir);
                if (map != null) {
                    String[] strings0 = file.list();
                    if (strings0 != null) {
                        for (int int0 = 0; int0 < strings0.length; int0++) {
                            String string = strings0[int0];
                            if (string.endsWith(".lotheader")) {
                                String[] strings1 = string.split("_");
                                strings1[1] = strings1[1].replace(".lotheader", "");
                                int int1 = Integer.parseInt(strings1[0].trim());
                                int int2 = Integer.parseInt(strings1[1].trim());
                                this.removeZonesForCell(int1, int2);
                            }
                        }
                    }
                }
            }
        }
    }

    public void processZones() {
        int int0 = 0;

        for (int int1 = this.minX; int1 <= this.maxX; int1++) {
            for (int int2 = this.minY; int2 <= this.maxY; int2++) {
                if (this.Grid[int1 - this.minX][int2 - this.minY] != null) {
                    for (int int3 = 0; int3 < 30; int3++) {
                        for (int int4 = 0; int4 < 30; int4++) {
                            int0 = Math.max(int0, this.Grid[int1 - this.minX][int2 - this.minY].getChunk(int4, int3).numZones());
                        }
                    }
                }
            }
        }

        DebugLog.log("Max #ZONES on one chunk is " + int0);
    }

    public IsoMetaGrid.Zone registerVehiclesZone(String name, String type, int x, int y, int z, int _width, int _height, KahluaTable properties) {
        if (!type.equals("Vehicle") && !type.equals("ParkingStall")) {
            return null;
        } else {
            name = this.sharedStrings.get(name);
            type = this.sharedStrings.get(type);
            IsoMetaGrid.VehicleZone vehicleZone = new IsoMetaGrid.VehicleZone(name, type, x, y, z, _width, _height, properties);
            this.VehiclesZones.add(vehicleZone);
            int int0 = (int)Math.ceil((vehicleZone.x + vehicleZone.w) / 300.0F);
            int int1 = (int)Math.ceil((vehicleZone.y + vehicleZone.h) / 300.0F);

            for (int int2 = vehicleZone.y / 300; int2 < int1; int2++) {
                for (int int3 = vehicleZone.x / 300; int3 < int0; int3++) {
                    if (int3 >= this.minX
                        && int3 <= this.maxX
                        && int2 >= this.minY
                        && int2 <= this.maxY
                        && this.Grid[int3 - this.minX][int2 - this.minY] != null) {
                        this.Grid[int3 - this.minX][int2 - this.minY].vehicleZones.add(vehicleZone);
                    }
                }
            }

            return vehicleZone;
        }
    }

    public void checkVehiclesZones() {
        int int0 = 0;

        while (int0 < this.VehiclesZones.size()) {
            boolean boolean0 = true;

            for (int int1 = 0; int1 < int0; int1++) {
                IsoMetaGrid.Zone zone0 = this.VehiclesZones.get(int0);
                IsoMetaGrid.Zone zone1 = this.VehiclesZones.get(int1);
                if (zone0.getX() == zone1.getX() && zone0.getY() == zone1.getY() && zone0.h == zone1.h && zone0.w == zone1.w) {
                    boolean0 = false;
                    DebugLog.log(
                        "checkVehiclesZones: ERROR! Zone '"
                            + zone0.name
                            + "':'"
                            + zone0.type
                            + "' ("
                            + zone0.x
                            + ", "
                            + zone0.y
                            + ") duplicate with Zone '"
                            + zone1.name
                            + "':'"
                            + zone1.type
                            + "' ("
                            + zone1.x
                            + ", "
                            + zone1.y
                            + ")"
                    );
                    break;
                }
            }

            if (boolean0) {
                int0++;
            } else {
                this.VehiclesZones.remove(int0);
            }
        }
    }

    public IsoMetaGrid.Zone registerMannequinZone(String name, String type, int x, int y, int z, int _width, int _height, KahluaTable properties) {
        if (!"Mannequin".equals(type)) {
            return null;
        } else {
            name = this.sharedStrings.get(name);
            type = this.sharedStrings.get(type);
            IsoMannequin.MannequinZone mannequinZone = new IsoMannequin.MannequinZone(name, type, x, y, z, _width, _height, properties);
            int int0 = (int)Math.ceil((mannequinZone.x + mannequinZone.w) / 300.0F);
            int int1 = (int)Math.ceil((mannequinZone.y + mannequinZone.h) / 300.0F);

            for (int int2 = mannequinZone.y / 300; int2 < int1; int2++) {
                for (int int3 = mannequinZone.x / 300; int3 < int0; int3++) {
                    if (int3 >= this.minX
                        && int3 <= this.maxX
                        && int2 >= this.minY
                        && int2 <= this.maxY
                        && this.Grid[int3 - this.minX][int2 - this.minY] != null) {
                        this.Grid[int3 - this.minX][int2 - this.minY].mannequinZones.add(mannequinZone);
                    }
                }
            }

            return mannequinZone;
        }
    }

    public void registerRoomTone(String name, String type, int x, int y, int z, int _width, int _height, KahluaTable properties) {
        if ("RoomTone".equals(type)) {
            IsoMetaCell metaCell = this.getCellData(x / 300, y / 300);
            if (metaCell != null) {
                IsoMetaGrid.RoomTone roomTone = new IsoMetaGrid.RoomTone();
                roomTone.x = x;
                roomTone.y = y;
                roomTone.z = z;
                roomTone.enumValue = properties.getString("RoomTone");
                roomTone.entireBuilding = Boolean.TRUE.equals(properties.rawget("EntireBuilding"));
                metaCell.roomTones.add(roomTone);
            }
        }
    }

    public boolean isZoneAbove(IsoMetaGrid.Zone zone1, IsoMetaGrid.Zone zone2, int x, int y, int z) {
        if (zone1 != null && zone1 != zone2) {
            ArrayList arrayList = TL_ZoneList.get();
            arrayList.clear();
            this.getZonesAt(x, y, z, arrayList);
            return arrayList.indexOf(zone1) > arrayList.indexOf(zone2);
        } else {
            return false;
        }
    }

    public void save(ByteBuffer output) {
        this.savePart(output, 0, false);
        this.savePart(output, 1, false);
    }

    public void savePart(ByteBuffer output, int part, boolean fromServer) {
        if (part == 0) {
            output.put((byte)77);
            output.put((byte)69);
            output.put((byte)84);
            output.put((byte)65);
            output.putInt(195);
            output.putInt(this.minX);
            output.putInt(this.minY);
            output.putInt(this.maxX);
            output.putInt(this.maxY);

            for (int int0 = 0; int0 < this.Grid.length; int0++) {
                for (int int1 = 0; int1 < this.Grid[0].length; int1++) {
                    IsoMetaCell metaCell = this.Grid[int0][int1];
                    int int2 = 0;
                    if (metaCell.info != null) {
                        int2 = metaCell.info.Rooms.values().size();
                    }

                    output.putInt(int2);
                    if (metaCell.info != null) {
                        for (Entry entry : metaCell.info.Rooms.entrySet()) {
                            RoomDef roomDef = (RoomDef)entry.getValue();
                            output.putLong(roomDef.metaID);
                            short short0 = 0;
                            if (roomDef.bExplored) {
                                short0 = (short)(short0 | 1);
                            }

                            if (roomDef.bLightsActive) {
                                short0 = (short)(short0 | 2);
                            }

                            if (roomDef.bDoneSpawn) {
                                short0 = (short)(short0 | 4);
                            }

                            if (roomDef.isRoofFixed()) {
                                short0 = (short)(short0 | 8);
                            }

                            output.putShort(short0);
                        }
                    }

                    if (metaCell.info != null) {
                        output.putInt(metaCell.info.Buildings.size());
                    } else {
                        output.putInt(0);
                    }

                    if (metaCell.info != null) {
                        for (BuildingDef buildingDef : metaCell.info.Buildings) {
                            output.putLong(buildingDef.metaID);
                            output.put((byte)(buildingDef.bAlarmed ? 1 : 0));
                            output.putInt(buildingDef.getKeyId());
                            output.put((byte)(buildingDef.seen ? 1 : 0));
                            output.put((byte)(buildingDef.isHasBeenVisited() ? 1 : 0));
                            output.putInt(buildingDef.lootRespawnHour);
                        }
                    }
                }
            }
        } else {
            output.putInt(SafeHouse.getSafehouseList().size());

            for (int int3 = 0; int3 < SafeHouse.getSafehouseList().size(); int3++) {
                SafeHouse.getSafehouseList().get(int3).save(output);
            }

            output.putInt(NonPvpZone.getAllZones().size());

            for (int int4 = 0; int4 < NonPvpZone.getAllZones().size(); int4++) {
                NonPvpZone.getAllZones().get(int4).save(output);
            }

            output.putInt(Faction.getFactions().size());

            for (int int5 = 0; int5 < Faction.getFactions().size(); int5++) {
                Faction.getFactions().get(int5).save(output);
            }

            if (GameServer.bServer) {
                int int6 = output.position();
                output.putInt(0);
                StashSystem.save(output);
                output.putInt(int6, output.position());
            } else if (!GameClient.bClient) {
                StashSystem.save(output);
            }

            output.putInt(RBBasic.getUniqueRDSSpawned().size());

            for (int int7 = 0; int7 < RBBasic.getUniqueRDSSpawned().size(); int7++) {
                GameWindow.WriteString(output, RBBasic.getUniqueRDSSpawned().get(int7));
            }
        }
    }

    public void load() {
        File file = ZomboidFileSystem.instance.getFileInCurrentSave("map_meta.bin");

        try (
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        ) {
            synchronized (SliceY.SliceBufferLock) {
                SliceY.SliceBuffer.clear();
                int int0 = bufferedInputStream.read(SliceY.SliceBuffer.array());
                SliceY.SliceBuffer.limit(int0);
                this.load(SliceY.SliceBuffer);
            }
        } catch (FileNotFoundException fileNotFoundException) {
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
        }
    }

    public void load(ByteBuffer input) {
        input.mark();
        byte byte0 = input.get();
        byte byte1 = input.get();
        byte byte2 = input.get();
        byte byte3 = input.get();
        int int0;
        if (byte0 == 77 && byte1 == 69 && byte2 == 84 && byte3 == 65) {
            int0 = input.getInt();
        } else {
            int0 = 33;
            input.reset();
        }

        int int1 = this.minX;
        int int2 = this.minY;
        int int3 = this.maxX;
        int int4 = this.maxY;
        int int5;
        int int6;
        if (int0 >= 194) {
            int1 = input.getInt();
            int2 = input.getInt();
            int3 = input.getInt();
            int4 = input.getInt();
            int5 = int3 - int1 + 1;
            int6 = int4 - int2 + 1;
        } else {
            int5 = input.getInt();
            int6 = input.getInt();
            if (int5 == 40 && int6 == 42 && this.width == 66 && this.height == 53 && this.getLotDirectories().contains("Muldraugh, KY")) {
                int1 = 10;
                int2 = 3;
            }

            int3 = int1 + int5 - 1;
            int4 = int2 + int6 - 1;
        }

        if (int5 != this.Grid.length || int6 != this.Grid[0].length) {
            DebugLog.log(
                "map_meta.bin world size ("
                    + int5
                    + "x"
                    + int6
                    + ") does not match the current map size ("
                    + this.Grid.length
                    + "x"
                    + this.Grid[0].length
                    + ")"
            );
        }

        int int7 = 0;
        int int8 = 0;

        for (int int9 = int1; int9 <= int3; int9++) {
            for (int int10 = int2; int10 <= int4; int10++) {
                IsoMetaCell metaCell0 = this.getCellData(int9, int10);
                int int11 = input.getInt();

                for (int int12 = 0; int12 < int11; int12++) {
                    int int13 = int0 < 194 ? input.getInt() : 0;
                    long long0 = int0 >= 194 ? input.getLong() : 0L;
                    boolean boolean0 = false;
                    boolean boolean1 = false;
                    boolean boolean2 = false;
                    boolean boolean3 = false;
                    if (int0 >= 160) {
                        short short0 = input.getShort();
                        boolean0 = (short0 & 1) != 0;
                        boolean1 = (short0 & 2) != 0;
                        boolean2 = (short0 & 4) != 0;
                        boolean3 = (short0 & 8) != 0;
                    } else {
                        boolean0 = input.get() == 1;
                        if (int0 >= 34) {
                            boolean1 = input.get() == 1;
                        } else {
                            boolean1 = Rand.Next(2) == 0;
                        }
                    }

                    if (metaCell0 != null && metaCell0.info != null) {
                        RoomDef roomDef = int0 < 194 ? metaCell0.info.Rooms.get(int13) : metaCell0.info.RoomByMetaID.get(long0);
                        if (roomDef != null) {
                            roomDef.setExplored(boolean0);
                            roomDef.bLightsActive = boolean1;
                            roomDef.bDoneSpawn = boolean2;
                            roomDef.setRoofFixed(boolean3);
                        } else if (int0 < 194) {
                            DebugLog.General.error("invalid room ID #" + int13 + " in cell " + int9 + "," + int10 + " while reading map_meta.bin");
                        } else {
                            DebugLog.General.error("invalid room metaID #" + long0 + " in cell " + int9 + "," + int10 + " while reading map_meta.bin");
                        }
                    }
                }

                int int14 = input.getInt();
                int7 += int14;

                for (int int15 = 0; int15 < int14; int15++) {
                    long long1 = int0 >= 194 ? input.getLong() : 0L;
                    boolean boolean4 = input.get() == 1;
                    int int16 = int0 >= 57 ? input.getInt() : -1;
                    boolean boolean5 = int0 >= 74 ? input.get() == 1 : false;
                    boolean boolean6 = int0 >= 107 ? input.get() == 1 : false;
                    if (int0 >= 111 && int0 < 121) {
                        input.getInt();
                    } else {
                        boolean boolean7 = false;
                    }

                    int int17 = int0 >= 125 ? input.getInt() : 0;
                    if (metaCell0 != null && metaCell0.info != null) {
                        BuildingDef buildingDef = null;
                        if (int0 >= 194) {
                            buildingDef = metaCell0.info.BuildingByMetaID.get(long1);
                        } else if (int15 < metaCell0.info.Buildings.size()) {
                            buildingDef = metaCell0.info.Buildings.get(int15);
                        }

                        if (buildingDef != null) {
                            if (boolean4) {
                                int8++;
                            }

                            buildingDef.bAlarmed = boolean4;
                            buildingDef.setKeyId(int16);
                            if (int0 >= 74) {
                                buildingDef.seen = boolean5;
                            }

                            buildingDef.hasBeenVisited = boolean6;
                            buildingDef.lootRespawnHour = int17;
                        } else if (int0 >= 194) {
                            DebugLog.General.error("invalid building metaID #" + long1 + " in cell " + int9 + "," + int10 + " while reading map_meta.bin");
                        }
                    }
                }
            }
        }

        if (int0 <= 112) {
            this.Zones.clear();

            for (int int18 = 0; int18 < this.height; int18++) {
                for (int int19 = 0; int19 < this.width; int19++) {
                    IsoMetaCell metaCell1 = this.Grid[int19][int18];
                    if (metaCell1 != null) {
                        for (int int20 = 0; int20 < 30; int20++) {
                            for (int int21 = 0; int21 < 30; int21++) {
                                metaCell1.ChunkMap[int21 + int20 * 30].clearZones();
                            }
                        }
                    }
                }
            }

            this.loadZone(input, int0);
        }

        SafeHouse.clearSafehouseList();
        int int22 = input.getInt();

        for (int int23 = 0; int23 < int22; int23++) {
            SafeHouse.load(input, int0);
        }

        NonPvpZone.nonPvpZoneList.clear();
        int int24 = input.getInt();

        for (int int25 = 0; int25 < int24; int25++) {
            NonPvpZone nonPvpZone = new NonPvpZone();
            nonPvpZone.load(input, int0);
            NonPvpZone.getAllZones().add(nonPvpZone);
        }

        Faction.factions = new ArrayList<>();
        int int26 = input.getInt();

        for (int int27 = 0; int27 < int26; int27++) {
            Faction faction = new Faction();
            faction.load(input, int0);
            Faction.getFactions().add(faction);
        }

        if (GameServer.bServer) {
            int int28 = input.getInt();
            StashSystem.load(input, int0);
        } else if (GameClient.bClient) {
            int int29 = input.getInt();
            input.position(int29);
        } else {
            StashSystem.load(input, int0);
        }

        ArrayList arrayList = RBBasic.getUniqueRDSSpawned();
        arrayList.clear();
        int int30 = input.getInt();

        for (int int31 = 0; int31 < int30; int31++) {
            arrayList.add(GameWindow.ReadString(input));
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public IsoMetaCell getCellData(int x, int y) {
        return x - this.minX >= 0 && y - this.minY >= 0 && x - this.minX < this.width && y - this.minY < this.height
            ? this.Grid[x - this.minX][y - this.minY]
            : null;
    }

    public IsoMetaCell getCellDataAbs(int x, int y) {
        return this.Grid[x][y];
    }

    public IsoMetaCell getCurrentCellData() {
        int int0 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldX;
        int int1 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldY;
        float float0 = int0;
        float float1 = int1;
        float0 /= 30.0F;
        float1 /= 30.0F;
        if (float0 < 0.0F) {
            float0 = (int)float0 - 1;
        }

        if (float1 < 0.0F) {
            float1 = (int)float1 - 1;
        }

        int0 = (int)float0;
        int1 = (int)float1;
        return this.getCellData(int0, int1);
    }

    public IsoMetaCell getMetaGridFromTile(int wx, int wy) {
        int int0 = wx / 300;
        int int1 = wy / 300;
        return this.getCellData(int0, int1);
    }

    public IsoMetaChunk getCurrentChunkData() {
        int int0 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldX;
        int int1 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldY;
        float float0 = int0;
        float float1 = int1;
        float0 /= 30.0F;
        float1 /= 30.0F;
        if (float0 < 0.0F) {
            float0 = (int)float0 - 1;
        }

        if (float1 < 0.0F) {
            float1 = (int)float1 - 1;
        }

        int0 = (int)float0;
        int1 = (int)float1;
        return this.getCellData(int0, int1)
            .getChunk(
                IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldX - int0 * 30,
                IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldY - int1 * 30
            );
    }

    public IsoMetaChunk getChunkData(int cx, int cy) {
        float float0 = cx;
        float float1 = cy;
        float0 /= 30.0F;
        float1 /= 30.0F;
        if (float0 < 0.0F) {
            float0 = (int)float0 - 1;
        }

        if (float1 < 0.0F) {
            float1 = (int)float1 - 1;
        }

        int int0 = (int)float0;
        int int1 = (int)float1;
        IsoMetaCell metaCell = this.getCellData(int0, int1);
        return metaCell == null ? null : metaCell.getChunk(cx - int0 * 30, cy - int1 * 30);
    }

    public IsoMetaChunk getChunkDataFromTile(int x, int y) {
        int int0 = x / 10;
        int int1 = y / 10;
        int0 -= this.minX * 30;
        int1 -= this.minY * 30;
        int int2 = int0 / 30;
        int int3 = int1 / 30;
        int0 += this.minX * 30;
        int1 += this.minY * 30;
        int2 += this.minX;
        int3 += this.minY;
        IsoMetaCell metaCell = this.getCellData(int2, int3);
        return metaCell == null ? null : metaCell.getChunk(int0 - int2 * 30, int1 - int3 * 30);
    }

    public boolean isValidSquare(int x, int y) {
        if (x < this.minX * 300) {
            return false;
        } else if (x >= (this.maxX + 1) * 300) {
            return false;
        } else {
            return y < this.minY * 300 ? false : y < (this.maxY + 1) * 300;
        }
    }

    public boolean isValidChunk(int wx, int wy) {
        wx *= 10;
        wy *= 10;
        if (wx < this.minX * 300) {
            return false;
        } else if (wx >= (this.maxX + 1) * 300) {
            return false;
        } else if (wy < this.minY * 300) {
            return false;
        } else {
            return wy >= (this.maxY + 1) * 300 ? false : this.Grid[wx / 300 - this.minX][wy / 300 - this.minY].info != null;
        }
    }

    public void Create() {
        this.CreateStep1();
        this.CreateStep2();
    }

    public void CreateStep1() {
        this.minX = 10000000;
        this.minY = 10000000;
        this.maxX = -10000000;
        this.maxY = -10000000;
        IsoLot.InfoHeaders.clear();
        IsoLot.InfoHeaderNames.clear();
        IsoLot.InfoFileNames.clear();
        long long0 = System.currentTimeMillis();
        DebugLog.log("IsoMetaGrid.Create: begin scanning directories");
        ArrayList arrayList = this.getLotDirectories();
        DebugLog.log("Looking in these map folders:");

        for (String string0 : arrayList) {
            string0 = ZomboidFileSystem.instance.getString("media/maps/" + string0 + "/");
            DebugLog.log("    " + new File(string0).getAbsolutePath());
        }

        DebugLog.log("<End of map-folders list>");

        for (String string1 : arrayList) {
            File file = new File(ZomboidFileSystem.instance.getString("media/maps/" + string1 + "/"));
            if (file.isDirectory()) {
                ChooseGameInfo.Map map = ChooseGameInfo.getMapDetails(string1);
                String[] strings0 = file.list();

                for (int int0 = 0; int0 < strings0.length; int0++) {
                    if (!IsoLot.InfoFileNames.containsKey(strings0[int0])) {
                        if (strings0[int0].endsWith(".lotheader")) {
                            String[] strings1 = strings0[int0].split("_");
                            strings1[1] = strings1[1].replace(".lotheader", "");
                            int int1 = Integer.parseInt(strings1[0].trim());
                            int int2 = Integer.parseInt(strings1[1].trim());
                            if (int1 < this.minX) {
                                this.minX = int1;
                            }

                            if (int2 < this.minY) {
                                this.minY = int2;
                            }

                            if (int1 > this.maxX) {
                                this.maxX = int1;
                            }

                            if (int2 > this.maxY) {
                                this.maxY = int2;
                            }

                            IsoLot.InfoFileNames.put(strings0[int0], file.getAbsolutePath() + File.separator + strings0[int0]);
                            LotHeader lotHeader = new LotHeader();
                            lotHeader.cellX = int1;
                            lotHeader.cellY = int2;
                            lotHeader.bFixed2x = map.isFixed2x();
                            IsoLot.InfoHeaders.put(strings0[int0], lotHeader);
                            IsoLot.InfoHeaderNames.add(strings0[int0]);
                        } else if (strings0[int0].endsWith(".lotpack")) {
                            IsoLot.InfoFileNames.put(strings0[int0], file.getAbsolutePath() + File.separator + strings0[int0]);
                        } else if (strings0[int0].startsWith("chunkdata_")) {
                            IsoLot.InfoFileNames.put(strings0[int0], file.getAbsolutePath() + File.separator + strings0[int0]);
                        }
                    }
                }
            }
        }

        if (this.maxX >= this.minX && this.maxY >= this.minY) {
            this.Grid = new IsoMetaCell[this.maxX - this.minX + 1][this.maxY - this.minY + 1];
            this.width = this.maxX - this.minX + 1;
            this.height = this.maxY - this.minY + 1;
            long long1 = System.currentTimeMillis() - long0;
            DebugLog.log("IsoMetaGrid.Create: finished scanning directories in " + (float)long1 / 1000.0F + " seconds");
            DebugLog.log("IsoMetaGrid.Create: begin loading");
            this.createStartTime = System.currentTimeMillis();

            for (int int3 = 0; int3 < 8; int3++) {
                IsoMetaGrid.MetaGridLoaderThread metaGridLoaderThread = new IsoMetaGrid.MetaGridLoaderThread(this.minY + int3);
                metaGridLoaderThread.setDaemon(true);
                metaGridLoaderThread.setName("MetaGridLoaderThread" + int3);
                metaGridLoaderThread.start();
                this.threads[int3] = metaGridLoaderThread;
            }
        } else {
            throw new IllegalStateException("Failed to find any .lotheader files");
        }
    }

    public void CreateStep2() {
        boolean boolean0 = true;

        while (boolean0) {
            boolean0 = false;

            for (int int0 = 0; int0 < 8; int0++) {
                if (this.threads[int0].isAlive()) {
                    boolean0 = true;

                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException interruptedException) {
                    }
                    break;
                }
            }
        }

        for (int int1 = 0; int1 < 8; int1++) {
            this.threads[int1].postLoad();
            this.threads[int1] = null;
        }

        for (int int2 = 0; int2 < this.Buildings.size(); int2++) {
            BuildingDef buildingDef = this.Buildings.get(int2);
            if (!Core.GameMode.equals("LastStand") && buildingDef.rooms.size() > 2) {
                byte byte0 = 11;
                if (SandboxOptions.instance.getElecShutModifier() > -1 && GameTime.instance.NightsSurvived < SandboxOptions.instance.getElecShutModifier()) {
                    byte0 = 9;
                }

                if (SandboxOptions.instance.Alarm.getValue() == 1) {
                    byte0 = -1;
                } else if (SandboxOptions.instance.Alarm.getValue() == 2) {
                    byte0 += 5;
                } else if (SandboxOptions.instance.Alarm.getValue() == 3) {
                    byte0 += 3;
                } else if (SandboxOptions.instance.Alarm.getValue() == 5) {
                    byte0 -= 3;
                } else if (SandboxOptions.instance.Alarm.getValue() == 6) {
                    byte0 -= 5;
                }

                if (byte0 > -1) {
                    buildingDef.bAlarmed = Rand.Next((int)byte0) == 0;
                }
            }
        }

        long long0 = System.currentTimeMillis() - this.createStartTime;
        DebugLog.log("IsoMetaGrid.Create: finished loading in " + (float)long0 / 1000.0F + " seconds");
    }

    public void Dispose() {
        if (this.Grid != null) {
            for (int int0 = 0; int0 < this.Grid.length; int0++) {
                IsoMetaCell[] metaCells = this.Grid[int0];

                for (int int1 = 0; int1 < metaCells.length; int1++) {
                    IsoMetaCell metaCell = metaCells[int1];
                    if (metaCell != null) {
                        metaCell.Dispose();
                    }
                }

                Arrays.fill(metaCells, null);
            }

            Arrays.fill(this.Grid, null);
            this.Grid = null;

            for (BuildingDef buildingDef : this.Buildings) {
                buildingDef.Dispose();
            }

            this.Buildings.clear();
            this.VehiclesZones.clear();

            for (IsoMetaGrid.Zone zone : this.Zones) {
                zone.Dispose();
            }

            this.Zones.clear();
            this.sharedStrings.clear();
        }
    }

    public Vector2 getRandomIndoorCoord() {
        return null;
    }

    public RoomDef getRandomRoomBetweenRange(float x, float y, float min, float max) {
        Object object0 = null;
        float float0 = 0.0F;
        roomChoices.clear();
        Object object1 = null;

        for (int int0 = 0; int0 < IsoLot.InfoHeaderNames.size(); int0++) {
            object1 = IsoLot.InfoHeaders.get(IsoLot.InfoHeaderNames.get(int0));
            if (!((LotHeader)object1).RoomList.isEmpty()) {
                for (int int1 = 0; int1 < ((LotHeader)object1).RoomList.size(); int1++) {
                    object0 = ((LotHeader)object1).RoomList.get(int1);
                    float0 = IsoUtils.DistanceManhatten(x, y, ((RoomDef)object0).x, ((RoomDef)object0).y);
                    if (float0 > min && float0 < max) {
                        roomChoices.add((RoomDef)object0);
                    }
                }
            }
        }

        return !roomChoices.isEmpty() ? roomChoices.get(Rand.Next(roomChoices.size())) : null;
    }

    public RoomDef getRandomRoomNotInRange(float x, float y, int range) {
        RoomDef roomDef = null;

        do {
            Object object = null;

            do {
                object = IsoLot.InfoHeaders.get(IsoLot.InfoHeaderNames.get(Rand.Next(IsoLot.InfoHeaderNames.size())));
            } while (((LotHeader)object).RoomList.isEmpty());

            roomDef = ((LotHeader)object).RoomList.get(Rand.Next(((LotHeader)object).RoomList.size()));
        } while (roomDef == null || IsoUtils.DistanceManhatten(x, y, roomDef.x, roomDef.y) < range);

        return roomDef;
    }

    public void save() {
        try {
            File file0 = ZomboidFileSystem.instance.getFileInCurrentSave("map_meta.bin");

            try (
                FileOutputStream fileOutputStream0 = new FileOutputStream(file0);
                BufferedOutputStream bufferedOutputStream0 = new BufferedOutputStream(fileOutputStream0);
            ) {
                synchronized (SliceY.SliceBufferLock) {
                    SliceY.SliceBuffer.clear();
                    this.save(SliceY.SliceBuffer);
                    bufferedOutputStream0.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
                }
            }

            File file1 = ZomboidFileSystem.instance.getFileInCurrentSave("map_zone.bin");

            try (
                FileOutputStream fileOutputStream1 = new FileOutputStream(file1);
                BufferedOutputStream bufferedOutputStream1 = new BufferedOutputStream(fileOutputStream1);
            ) {
                synchronized (SliceY.SliceBufferLock) {
                    SliceY.SliceBuffer.clear();
                    this.saveZone(SliceY.SliceBuffer);
                    bufferedOutputStream1.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
                }
            }
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
        }
    }

    public void loadZones() {
        File file = ZomboidFileSystem.instance.getFileInCurrentSave("map_zone.bin");

        try (
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        ) {
            synchronized (SliceY.SliceBufferLock) {
                SliceY.SliceBuffer.clear();
                int int0 = bufferedInputStream.read(SliceY.SliceBuffer.array());
                SliceY.SliceBuffer.limit(int0);
                this.loadZone(SliceY.SliceBuffer, -1);
            }
        } catch (FileNotFoundException fileNotFoundException) {
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
        }
    }

    public void loadZone(ByteBuffer input, int WorldVersion) {
        if (WorldVersion == -1) {
            byte byte0 = input.get();
            byte byte1 = input.get();
            byte byte2 = input.get();
            byte byte3 = input.get();
            if (byte0 != 90 || byte1 != 79 || byte2 != 78 || byte3 != 69) {
                DebugLog.log("ERROR: expected 'ZONE' at start of map_zone.bin");
                return;
            }

            WorldVersion = input.getInt();
        }

        int int0 = this.Zones.size();
        if (!GameServer.bServer && WorldVersion >= 34 || GameServer.bServer && WorldVersion >= 36) {
            for (IsoMetaGrid.Zone zone0 : this.Zones) {
                zone0.Dispose();
            }

            this.Zones.clear();

            for (int int1 = 0; int1 < this.height; int1++) {
                for (int int2 = 0; int2 < this.width; int2++) {
                    IsoMetaCell metaCell = this.Grid[int2][int1];
                    if (metaCell != null) {
                        for (int int3 = 0; int3 < 30; int3++) {
                            for (int int4 = 0; int4 < 30; int4++) {
                                metaCell.ChunkMap[int4 + int3 * 30].clearZones();
                            }
                        }
                    }
                }
            }

            IsoMetaGrid.ZoneGeometryType[] zoneGeometryTypes = IsoMetaGrid.ZoneGeometryType.values();
            TIntArrayList tIntArrayList = new TIntArrayList();
            if (WorldVersion >= 141) {
                int int5 = input.getInt();
                HashMap hashMap = new HashMap();

                for (int int6 = 0; int6 < int5; int6++) {
                    String string0 = GameWindow.ReadStringUTF(input);
                    hashMap.put(int6, string0);
                }

                int int7 = input.getInt();
                DebugLog.log("loading " + int7 + " zones from map_zone.bin");

                for (int int8 = 0; int8 < int7; int8++) {
                    String string1 = (String)hashMap.get(Integer.valueOf(input.getShort()));
                    String string2 = (String)hashMap.get(Integer.valueOf(input.getShort()));
                    int int9 = input.getInt();
                    int int10 = input.getInt();
                    byte byte4 = input.get();
                    int int11 = input.getInt();
                    int int12 = input.getInt();
                    IsoMetaGrid.ZoneGeometryType zoneGeometryType = IsoMetaGrid.ZoneGeometryType.INVALID;
                    tIntArrayList.clear();
                    int int13 = 0;
                    if (WorldVersion >= 185) {
                        byte byte5 = input.get();
                        if (byte5 < 0 || byte5 >= zoneGeometryTypes.length) {
                            byte5 = 0;
                        }

                        zoneGeometryType = zoneGeometryTypes[byte5];
                        if (zoneGeometryType != IsoMetaGrid.ZoneGeometryType.INVALID) {
                            if (WorldVersion >= 186 && zoneGeometryType == IsoMetaGrid.ZoneGeometryType.Polyline) {
                                int13 = PZMath.clamp(input.get(), 0, 255);
                            }

                            short short0 = input.getShort();

                            for (int int14 = 0; int14 < short0; int14++) {
                                tIntArrayList.add(input.getInt());
                            }
                        }
                    }

                    int int15 = input.getInt();
                    IsoMetaGrid.Zone zone1 = this.registerZone(
                        string1,
                        string2,
                        int9,
                        int10,
                        byte4,
                        int11,
                        int12,
                        zoneGeometryType,
                        zoneGeometryType == IsoMetaGrid.ZoneGeometryType.INVALID ? null : tIntArrayList,
                        int13
                    );
                    zone1.hourLastSeen = int15;
                    zone1.haveConstruction = input.get() == 1;
                    zone1.lastActionTimestamp = input.getInt();
                    zone1.setOriginalName((String)hashMap.get(Integer.valueOf(input.getShort())));
                    zone1.id = input.getDouble();
                }

                int int16 = input.getInt();

                for (int int17 = 0; int17 < int16; int17++) {
                    String string3 = GameWindow.ReadString(input);
                    ArrayList arrayList = new ArrayList();
                    int int18 = input.getInt();

                    for (int int19 = 0; int19 < int18; int19++) {
                        arrayList.add(input.getDouble());
                    }

                    IsoWorld.instance.getSpawnedZombieZone().put(string3, arrayList);
                }

                return;
            }

            int int20 = input.getInt();
            DebugLog.log("loading " + int20 + " zones from map_zone.bin");
            if (WorldVersion <= 112 && int20 > int0 * 2) {
                DebugLog.log("ERROR: seems like too many zones in map_zone.bin");
                return;
            }

            for (int int21 = 0; int21 < int20; int21++) {
                String string4 = GameWindow.ReadString(input);
                String string5 = GameWindow.ReadString(input);
                int int22 = input.getInt();
                int int23 = input.getInt();
                int int24 = input.getInt();
                int int25 = input.getInt();
                int int26 = input.getInt();
                if (WorldVersion < 121) {
                    input.getInt();
                } else {
                    boolean boolean0 = false;
                }

                int int27 = WorldVersion < 68 ? input.getShort() : input.getInt();
                IsoMetaGrid.Zone zone2 = this.registerZone(string4, string5, int22, int23, int24, int25, int26);
                zone2.hourLastSeen = int27;
                if (WorldVersion >= 35) {
                    boolean boolean1 = input.get() == 1;
                    zone2.haveConstruction = boolean1;
                }

                if (WorldVersion >= 41) {
                    zone2.lastActionTimestamp = input.getInt();
                }

                if (WorldVersion >= 98) {
                    zone2.setOriginalName(GameWindow.ReadString(input));
                }

                if (WorldVersion >= 110 && WorldVersion < 121) {
                    int int28 = input.getInt();
                }

                zone2.id = input.getDouble();
            }
        }
    }

    public void saveZone(ByteBuffer output) {
        output.put((byte)90);
        output.put((byte)79);
        output.put((byte)78);
        output.put((byte)69);
        output.putInt(195);
        HashSet hashSet = new HashSet();

        for (int int0 = 0; int0 < this.Zones.size(); int0++) {
            IsoMetaGrid.Zone zone0 = this.Zones.get(int0);
            hashSet.add(zone0.getName());
            hashSet.add(zone0.getOriginalName());
            hashSet.add(zone0.getType());
        }

        ArrayList arrayList0 = new ArrayList(hashSet);
        HashMap hashMap = new HashMap();

        for (int int1 = 0; int1 < arrayList0.size(); int1++) {
            hashMap.put((String)arrayList0.get(int1), int1);
        }

        if (arrayList0.size() > 32767) {
            throw new IllegalStateException("IsoMetaGrid.saveZone() string table is too large");
        } else {
            output.putInt(arrayList0.size());

            for (int int2 = 0; int2 < arrayList0.size(); int2++) {
                GameWindow.WriteString(output, (String)arrayList0.get(int2));
            }

            output.putInt(this.Zones.size());

            for (int int3 = 0; int3 < this.Zones.size(); int3++) {
                IsoMetaGrid.Zone zone1 = this.Zones.get(int3);
                output.putShort(((Integer)hashMap.get(zone1.getName())).shortValue());
                output.putShort(((Integer)hashMap.get(zone1.getType())).shortValue());
                output.putInt(zone1.x);
                output.putInt(zone1.y);
                output.put((byte)zone1.z);
                output.putInt(zone1.w);
                output.putInt(zone1.h);
                output.put((byte)zone1.geometryType.ordinal());
                if (!zone1.isRectangle()) {
                    if (zone1.isPolyline()) {
                        output.put((byte)zone1.polylineWidth);
                    }

                    output.putShort((short)zone1.points.size());

                    for (int int4 = 0; int4 < zone1.points.size(); int4++) {
                        output.putInt(zone1.points.get(int4));
                    }
                }

                output.putInt(zone1.hourLastSeen);
                output.put((byte)(zone1.haveConstruction ? 1 : 0));
                output.putInt(zone1.lastActionTimestamp);
                output.putShort(((Integer)hashMap.get(zone1.getOriginalName())).shortValue());
                output.putDouble(zone1.id);
            }

            hashSet.clear();
            arrayList0.clear();
            hashMap.clear();
            output.putInt(IsoWorld.instance.getSpawnedZombieZone().size());

            for (String string : IsoWorld.instance.getSpawnedZombieZone().keySet()) {
                ArrayList arrayList1 = IsoWorld.instance.getSpawnedZombieZone().get(string);
                GameWindow.WriteString(output, string);
                output.putInt(arrayList1.size());

                for (int int5 = 0; int5 < arrayList1.size(); int5++) {
                    output.putDouble((Double)arrayList1.get(int5));
                }
            }
        }
    }

    private void getLotDirectories(String string0, ArrayList<String> arrayList) {
        if (!arrayList.contains(string0)) {
            ChooseGameInfo.Map map = ChooseGameInfo.getMapDetails(string0);
            if (map != null) {
                arrayList.add(string0);

                for (String string1 : map.getLotDirectories()) {
                    this.getLotDirectories(string1, arrayList);
                }
            }
        }
    }

    public ArrayList<String> getLotDirectories() {
        if (GameClient.bClient) {
            Core.GameMap = GameClient.GameMap;
        }

        if (GameServer.bServer) {
            Core.GameMap = GameServer.GameMap;
        }

        if (Core.GameMap.equals("DEFAULT")) {
            MapGroups mapGroups = new MapGroups();
            mapGroups.createGroups();
            if (mapGroups.getNumberOfGroups() != 1) {
                throw new RuntimeException("GameMap is DEFAULT but there are multiple worlds to choose from");
            }

            mapGroups.setWorld(0);
        }

        ArrayList arrayList = new ArrayList();
        if (Core.GameMap.contains(";")) {
            String[] strings = Core.GameMap.split(";");

            for (int int0 = 0; int0 < strings.length; int0++) {
                String string = strings[int0].trim();
                if (!string.isEmpty() && !arrayList.contains(string)) {
                    arrayList.add(string);
                }
            }
        } else {
            this.getLotDirectories(Core.GameMap, arrayList);
        }

        return arrayList;
    }

    public static boolean isPreferredZoneForSquare(String type) {
        return s_PreferredZoneTypes.contains(type);
    }

    static {
        s_PreferredZoneTypes.add("DeepForest");
        s_PreferredZoneTypes.add("Farm");
        s_PreferredZoneTypes.add("FarmLand");
        s_PreferredZoneTypes.add("Forest");
        s_PreferredZoneTypes.add("Vegitation");
        s_PreferredZoneTypes.add("Nav");
        s_PreferredZoneTypes.add("TownZone");
        s_PreferredZoneTypes.add("TrailerPark");
    }

    private final class MetaGridLoaderThread extends Thread {
        final SharedStrings sharedStrings = new SharedStrings();
        final ArrayList<BuildingDef> Buildings = new ArrayList<>();
        final ArrayList<RoomDef> tempRooms = new ArrayList<>();
        int wY;

        MetaGridLoaderThread(int int0) {
            this.wY = int0;
        }

        @Override
        public void run() {
            try {
                this.runInner();
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
            }
        }

        void runInner() {
            for (int int0 = this.wY; int0 <= IsoMetaGrid.this.maxY; int0 += 8) {
                for (int int1 = IsoMetaGrid.this.minX; int1 <= IsoMetaGrid.this.maxX; int1++) {
                    this.loadCell(int1, int0);
                }
            }
        }

        void loadCell(int int0, int int1) {
            IsoMetaCell metaCell = new IsoMetaCell(int0, int1);
            IsoMetaGrid.this.Grid[int0 - IsoMetaGrid.this.minX][int1 - IsoMetaGrid.this.minY] = metaCell;
            String string0 = int0 + "_" + int1 + ".lotheader";
            if (IsoLot.InfoFileNames.containsKey(string0)) {
                LotHeader lotHeader = IsoLot.InfoHeaders.get(string0);
                if (lotHeader != null) {
                    File file = new File(IsoLot.InfoFileNames.get(string0));
                    if (file.exists()) {
                        metaCell.info = lotHeader;

                        try (BufferedRandomAccessFile bufferedRandomAccessFile = new BufferedRandomAccessFile(file.getAbsolutePath(), "r", 4096)) {
                            lotHeader.version = IsoLot.readInt(bufferedRandomAccessFile);
                            int int2 = IsoLot.readInt(bufferedRandomAccessFile);

                            for (int int3 = 0; int3 < int2; int3++) {
                                String string1 = IsoLot.readString(bufferedRandomAccessFile);
                                lotHeader.tilesUsed.add(this.sharedStrings.get(string1.trim()));
                            }

                            bufferedRandomAccessFile.read();
                            lotHeader.width = IsoLot.readInt(bufferedRandomAccessFile);
                            lotHeader.height = IsoLot.readInt(bufferedRandomAccessFile);
                            lotHeader.levels = IsoLot.readInt(bufferedRandomAccessFile);
                            int int4 = IsoLot.readInt(bufferedRandomAccessFile);

                            for (int int5 = 0; int5 < int4; int5++) {
                                String string2 = IsoLot.readString(bufferedRandomAccessFile);
                                RoomDef roomDef0 = new RoomDef(int5, this.sharedStrings.get(string2));
                                roomDef0.level = IsoLot.readInt(bufferedRandomAccessFile);
                                int int6 = IsoLot.readInt(bufferedRandomAccessFile);

                                for (int int7 = 0; int7 < int6; int7++) {
                                    RoomDef.RoomRect roomRect = new RoomDef.RoomRect(
                                        IsoLot.readInt(bufferedRandomAccessFile) + int0 * 300,
                                        IsoLot.readInt(bufferedRandomAccessFile) + int1 * 300,
                                        IsoLot.readInt(bufferedRandomAccessFile),
                                        IsoLot.readInt(bufferedRandomAccessFile)
                                    );
                                    roomDef0.rects.add(roomRect);
                                }

                                roomDef0.CalculateBounds();
                                roomDef0.metaID = roomDef0.calculateMetaID(int0, int1);
                                lotHeader.Rooms.put(roomDef0.ID, roomDef0);
                                if (lotHeader.RoomByMetaID.contains(roomDef0.metaID)) {
                                    DebugLog.General.error("duplicate RoomDef.metaID for room at %d,%d,%d", roomDef0.x, roomDef0.y, roomDef0.level);
                                }

                                lotHeader.RoomByMetaID.put(roomDef0.metaID, roomDef0);
                                lotHeader.RoomList.add(roomDef0);
                                metaCell.addRoom(roomDef0, int0 * 300, int1 * 300);
                                int int8 = IsoLot.readInt(bufferedRandomAccessFile);

                                for (int int9 = 0; int9 < int8; int9++) {
                                    int int10 = IsoLot.readInt(bufferedRandomAccessFile);
                                    int int11 = IsoLot.readInt(bufferedRandomAccessFile);
                                    int int12 = IsoLot.readInt(bufferedRandomAccessFile);
                                    roomDef0.objects.add(new MetaObject(int10, int11 + int0 * 300 - roomDef0.x, int12 + int1 * 300 - roomDef0.y, roomDef0));
                                }

                                roomDef0.bLightsActive = Rand.Next(2) == 0;
                            }

                            int int13 = IsoLot.readInt(bufferedRandomAccessFile);

                            for (int int14 = 0; int14 < int13; int14++) {
                                BuildingDef buildingDef = new BuildingDef();
                                int int15 = IsoLot.readInt(bufferedRandomAccessFile);
                                buildingDef.ID = int14;

                                for (int int16 = 0; int16 < int15; int16++) {
                                    RoomDef roomDef1 = lotHeader.Rooms.get(IsoLot.readInt(bufferedRandomAccessFile));
                                    roomDef1.building = buildingDef;
                                    if (roomDef1.isEmptyOutside()) {
                                        buildingDef.emptyoutside.add(roomDef1);
                                    } else {
                                        buildingDef.rooms.add(roomDef1);
                                    }
                                }

                                buildingDef.CalculateBounds(this.tempRooms);
                                buildingDef.metaID = buildingDef.calculateMetaID(int0, int1);
                                lotHeader.Buildings.add(buildingDef);
                                lotHeader.BuildingByMetaID.put(buildingDef.metaID, buildingDef);
                                this.Buildings.add(buildingDef);
                            }

                            for (int int17 = 0; int17 < 30; int17++) {
                                for (int int18 = 0; int18 < 30; int18++) {
                                    int int19 = bufferedRandomAccessFile.read();
                                    IsoMetaChunk metaChunk = metaCell.getChunk(int17, int18);
                                    metaChunk.setZombieIntensity(int19);
                                }
                            }
                        } catch (Exception exception) {
                            DebugLog.log("ERROR loading " + file.getAbsolutePath());
                            ExceptionLogger.logException(exception);
                        }
                    }
                }
            }
        }

        void postLoad() {
            IsoMetaGrid.this.Buildings.addAll(this.Buildings);
            this.Buildings.clear();
            this.sharedStrings.clear();
            this.tempRooms.clear();
        }
    }

    public static final class RoomTone {
        public int x;
        public int y;
        public int z;
        public String enumValue;
        public boolean entireBuilding;
    }

    public static final class Trigger {
        public BuildingDef def;
        public int triggerRange;
        public int zombieExclusionRange;
        public String type;
        public boolean triggered = false;
        public KahluaTable data;

        public Trigger(BuildingDef buildingDef, int int0, int int1, String string) {
            this.def = buildingDef;
            this.triggerRange = int0;
            this.zombieExclusionRange = int1;
            this.type = string;
            this.data = LuaManager.platform.newTable();
        }

        public KahluaTable getModData() {
            return this.data;
        }
    }

    public static final class VehicleZone extends IsoMetaGrid.Zone {
        public static final short VZF_FaceDirection = 1;
        public IsoDirections dir = IsoDirections.Max;
        public short flags = 0;

        public VehicleZone(String string0, String string1, int int0, int int1, int int2, int int3, int int4, KahluaTable table) {
            super(string0, string1, int0, int1, int2, int3, int4);
            if (table != null) {
                Object object = table.rawget("Direction");
                if (object instanceof String) {
                    this.dir = IsoDirections.valueOf((String)object);
                }

                object = table.rawget("FaceDirection");
                if (object == Boolean.TRUE) {
                    this.flags = (short)(this.flags | 1);
                }
            }
        }

        public boolean isFaceDirection() {
            return (this.flags & 1) != 0;
        }
    }

    public static class Zone {
        public Double id = 0.0;
        public int hourLastSeen = 0;
        public int lastActionTimestamp = 0;
        public boolean haveConstruction = false;
        public final HashMap<String, Integer> spawnedZombies = new HashMap<>();
        public String zombiesTypeToSpawn = null;
        public Boolean spawnSpecialZombies = null;
        public String name;
        public String type;
        public int x;
        public int y;
        public int z;
        public int w;
        public int h;
        public IsoMetaGrid.ZoneGeometryType geometryType = IsoMetaGrid.ZoneGeometryType.INVALID;
        public final TIntArrayList points = new TIntArrayList();
        private boolean bTriangulateFailed = false;
        public int polylineWidth = 0;
        public float[] polylineOutlinePoints;
        public float[] triangles;
        public float[] triangleAreas;
        public float totalArea = 0.0F;
        public int pickedXForZoneStory;
        public int pickedYForZoneStory;
        public RandomizedZoneStoryBase pickedRZStory;
        private String originalName;
        public boolean isPreferredZoneForSquare = false;
        static final PolygonalMap2.LiangBarsky LIANG_BARSKY = new PolygonalMap2.LiangBarsky();
        static final Vector2 L_lineSegmentIntersects = new Vector2();

        public Zone(String string0, String string1, int int0, int int1, int int2, int int3, int int4) {
            this.id = Rand.Next(9999999) + 100000.0;
            this.originalName = string0;
            this.name = string0;
            this.type = string1;
            this.x = int0;
            this.y = int1;
            this.z = int2;
            this.w = int3;
            this.h = int4;
        }

        public void setX(int int0) {
            this.x = int0;
        }

        public void setY(int int0) {
            this.y = int0;
        }

        public void setW(int int0) {
            this.w = int0;
        }

        public void setH(int int0) {
            this.h = int0;
        }

        public boolean isPoint() {
            return this.geometryType == IsoMetaGrid.ZoneGeometryType.Point;
        }

        public boolean isPolygon() {
            return this.geometryType == IsoMetaGrid.ZoneGeometryType.Polygon;
        }

        public boolean isPolyline() {
            return this.geometryType == IsoMetaGrid.ZoneGeometryType.Polyline;
        }

        public boolean isRectangle() {
            return this.geometryType == IsoMetaGrid.ZoneGeometryType.INVALID;
        }

        public void setPickedXForZoneStory(int int0) {
            this.pickedXForZoneStory = int0;
        }

        public void setPickedYForZoneStory(int int0) {
            this.pickedYForZoneStory = int0;
        }

        public float getHoursSinceLastSeen() {
            return (float)GameTime.instance.getWorldAgeHours() - this.hourLastSeen;
        }

        public void setHourSeenToCurrent() {
            this.hourLastSeen = (int)GameTime.instance.getWorldAgeHours();
        }

        public void setHaveConstruction(boolean boolean0) {
            this.haveConstruction = boolean0;
            if (GameClient.bClient) {
                ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
                PacketTypes.PacketType.ConstructedZone.doPacket(byteBufferWriter);
                byteBufferWriter.putInt(this.x);
                byteBufferWriter.putInt(this.y);
                byteBufferWriter.putInt(this.z);
                PacketTypes.PacketType.ConstructedZone.send(GameClient.connection);
            }
        }

        public boolean haveCons() {
            return this.haveConstruction;
        }

        public int getZombieDensity() {
            IsoMetaChunk metaChunk = IsoWorld.instance.MetaGrid.getChunkDataFromTile(this.x, this.y);
            return metaChunk != null ? metaChunk.getUnadjustedZombieIntensity() : 0;
        }

        public boolean contains(int int1, int int2, int int0) {
            if (int0 != this.z) {
                return false;
            } else if (int1 < this.x || int1 >= this.x + this.w) {
                return false;
            } else if (int2 < this.y || int2 >= this.y + this.h) {
                return false;
            } else if (this.isPoint()) {
                return false;
            } else if (this.isPolyline()) {
                if (this.polylineWidth > 0) {
                    this.checkPolylineOutline();
                    return this.isPointInPolyline_WindingNumber(int1 + 0.5F, int2 + 0.5F, 0) == IsoMetaGrid.Zone.PolygonHit.Inside;
                } else {
                    return false;
                }
            } else {
                return this.isPolygon() ? this.isPointInPolygon_WindingNumber(int1 + 0.5F, int2 + 0.5F, 0) == IsoMetaGrid.Zone.PolygonHit.Inside : true;
            }
        }

        public boolean intersects(int int1, int int3, int int0, int int2, int int4) {
            if (this.z != int0) {
                return false;
            } else if (int1 + int2 > this.x && int1 < this.x + this.w) {
                if (int3 + int4 <= this.y || int3 >= this.y + this.h) {
                    return false;
                } else if (this.isPolygon()) {
                    return this.polygonRectIntersect(int1, int3, int2, int4);
                } else if (this.isPolyline()) {
                    if (this.polylineWidth > 0) {
                        this.checkPolylineOutline();
                        return this.polylineOutlineRectIntersect(int1, int3, int2, int4);
                    } else {
                        for (byte byte0 = 0; byte0 < this.points.size() - 2; byte0 += 2) {
                            int int5 = this.points.getQuick(byte0);
                            int int6 = this.points.getQuick(byte0 + 1);
                            int int7 = this.points.getQuick(byte0 + 2);
                            int int8 = this.points.getQuick(byte0 + 3);
                            if (LIANG_BARSKY.lineRectIntersect(int5, int6, int7 - int5, int8 - int6, int1, int3, int1 + int2, int3 + int4)) {
                                return true;
                            }
                        }

                        return false;
                    }
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }

        public boolean difference(int int0, int int1, int int2, int int3, int int4, ArrayList<IsoMetaGrid.Zone> arrayList) {
            arrayList.clear();
            if (!this.intersects(int0, int1, int2, int3, int4)) {
                return false;
            } else if (this.isRectangle()) {
                if (this.x < int0) {
                    int int5 = Math.max(int1, this.y);
                    int int6 = Math.min(int1 + int4, this.y + this.h);
                    arrayList.add(new IsoMetaGrid.Zone(this.name, this.type, this.x, int5, int2, int0 - this.x, int6 - int5));
                }

                if (int0 + int3 < this.x + this.w) {
                    int int7 = Math.max(int1, this.y);
                    int int8 = Math.min(int1 + int4, this.y + this.h);
                    arrayList.add(new IsoMetaGrid.Zone(this.name, this.type, int0 + int3, int7, int2, this.x + this.w - (int0 + int3), int8 - int7));
                }

                if (this.y < int1) {
                    arrayList.add(new IsoMetaGrid.Zone(this.name, this.type, this.x, this.y, int2, this.w, int1 - this.y));
                }

                if (int1 + int4 < this.y + this.h) {
                    arrayList.add(new IsoMetaGrid.Zone(this.name, this.type, this.x, int1 + int4, int2, this.w, this.y + this.h - (int1 + int4)));
                }

                return true;
            } else {
                if (this.isPolygon()) {
                    if (IsoMetaGrid.s_clipper == null) {
                        IsoMetaGrid.s_clipper = new Clipper();
                        IsoMetaGrid.s_clipperBuffer = ByteBuffer.allocateDirect(3072);
                    }

                    Clipper clipper = IsoMetaGrid.s_clipper;
                    ByteBuffer byteBuffer = IsoMetaGrid.s_clipperBuffer;
                    byteBuffer.clear();

                    for (byte byte0 = 0; byte0 < this.points.size(); byte0 += 2) {
                        byteBuffer.putFloat(this.points.getQuick(byte0));
                        byteBuffer.putFloat(this.points.getQuick(byte0 + 1));
                    }

                    clipper.clear();
                    clipper.addPath(this.points.size() / 2, byteBuffer, false);
                    clipper.clipAABB(int0, int1, int0 + int3, int1 + int4);
                    int int9 = clipper.generatePolygons();

                    for (int int10 = 0; int10 < int9; int10++) {
                        byteBuffer.clear();
                        clipper.getPolygon(int10, byteBuffer);
                        short short0 = byteBuffer.getShort();
                        if (short0 < 3) {
                            byteBuffer.position(byteBuffer.position() + short0 * 4 * 2);
                        } else {
                            IsoMetaGrid.Zone zone1 = new IsoMetaGrid.Zone(this.name, this.type, this.x, this.y, this.z, this.w, this.h);
                            zone1.geometryType = IsoMetaGrid.ZoneGeometryType.Polygon;

                            for (int int11 = 0; int11 < short0; int11++) {
                                zone1.points.add((int)byteBuffer.getFloat());
                                zone1.points.add((int)byteBuffer.getFloat());
                            }

                            arrayList.add(zone1);
                        }
                    }
                }

                if (this.isPolyline()) {
                }

                return true;
            }
        }

        private int pickRandomTriangle() {
            float[] floats = this.isPolygon() ? this.getPolygonTriangles() : (this.isPolyline() ? this.getPolylineOutlineTriangles() : null);
            if (floats == null) {
                return -1;
            } else {
                int int0 = floats.length / 6;
                float float0 = Rand.Next(0.0F, this.totalArea);
                float float1 = 0.0F;

                for (int int1 = 0; int1 < this.triangleAreas.length; int1++) {
                    float1 += this.triangleAreas[int1];
                    if (float1 >= float0) {
                        return int1;
                    }
                }

                return Rand.Next(int0);
            }
        }

        private Vector2 pickRandomPointInTriangle(int int0, Vector2 vector) {
            float float0 = this.triangles[int0 * 3 * 2];
            float float1 = this.triangles[int0 * 3 * 2 + 1];
            float float2 = this.triangles[int0 * 3 * 2 + 2];
            float float3 = this.triangles[int0 * 3 * 2 + 3];
            float float4 = this.triangles[int0 * 3 * 2 + 4];
            float float5 = this.triangles[int0 * 3 * 2 + 5];
            float float6 = Rand.Next(0.0F, 1.0F);
            float float7 = Rand.Next(0.0F, 1.0F);
            boolean boolean0 = float6 + float7 <= 1.0F;
            float float8;
            float float9;
            if (boolean0) {
                float8 = float6 * (float2 - float0) + float7 * (float4 - float0);
                float9 = float6 * (float3 - float1) + float7 * (float5 - float1);
            } else {
                float8 = (1.0F - float6) * (float2 - float0) + (1.0F - float7) * (float4 - float0);
                float9 = (1.0F - float6) * (float3 - float1) + (1.0F - float7) * (float5 - float1);
            }

            float8 += float0;
            float9 += float1;
            return vector.set(float8, float9);
        }

        public IsoGameCharacter.Location pickRandomLocation(IsoGameCharacter.Location location) {
            if (this.isPolygon() || this.isPolyline() && this.polylineWidth > 0) {
                int int0 = this.pickRandomTriangle();
                if (int0 == -1) {
                    return null;
                } else {
                    for (int int1 = 0; int1 < 20; int1++) {
                        Vector2 vector = this.pickRandomPointInTriangle(int0, BaseVehicle.allocVector2());
                        if (this.contains((int)vector.x, (int)vector.y, this.z)) {
                            location.set((int)vector.x, (int)vector.y, this.z);
                            BaseVehicle.releaseVector2(vector);
                            return location;
                        }
                    }

                    return null;
                }
            } else {
                return !this.isPoint() && !this.isPolyline()
                    ? location.set(Rand.Next(this.x, this.x + this.w), Rand.Next(this.y, this.y + this.h), this.z)
                    : null;
            }
        }

        public IsoGridSquare getRandomSquareInZone() {
            IsoGameCharacter.Location location = this.pickRandomLocation(IsoMetaGrid.TL_Location.get());
            return location == null ? null : IsoWorld.instance.CurrentCell.getGridSquare(location.x, location.y, location.z);
        }

        public IsoGridSquare getRandomUnseenSquareInZone() {
            return null;
        }

        public void addSquare(IsoGridSquare var1) {
        }

        public ArrayList<IsoGridSquare> getSquares() {
            return null;
        }

        public void removeSquare(IsoGridSquare var1) {
        }

        public String getName() {
            return this.name;
        }

        public void setName(String string) {
            this.name = string;
        }

        public String getType() {
            return this.type;
        }

        public void setType(String string) {
            this.type = string;
        }

        public int getLastActionTimestamp() {
            return this.lastActionTimestamp;
        }

        public void setLastActionTimestamp(int int0) {
            this.lastActionTimestamp = int0;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getZ() {
            return this.z;
        }

        public int getHeight() {
            return this.h;
        }

        public int getWidth() {
            return this.w;
        }

        public float getTotalArea() {
            if (!this.isRectangle() && !this.isPoint() && (!this.isPolyline() || this.polylineWidth > 0)) {
                this.getPolygonTriangles();
                this.getPolylineOutlineTriangles();
                return this.totalArea;
            } else {
                return this.getWidth() * this.getHeight();
            }
        }

        public void sendToServer() {
            if (GameClient.bClient) {
                GameClient.registerZone(this, true);
            }
        }

        public String getOriginalName() {
            return this.originalName;
        }

        public void setOriginalName(String string) {
            this.originalName = string;
        }

        public int getClippedSegmentOfPolyline(int int7, int int6, int int5, int int4, double[] doubles) {
            if (!this.isPolyline()) {
                return -1;
            } else {
                float float0 = this.polylineWidth % 2 == 0 ? 0.0F : 0.5F;

                for (byte byte0 = 0; byte0 < this.points.size() - 2; byte0 += 2) {
                    int int0 = this.points.getQuick(byte0);
                    int int1 = this.points.getQuick(byte0 + 1);
                    int int2 = this.points.getQuick(byte0 + 2);
                    int int3 = this.points.getQuick(byte0 + 3);
                    if (LIANG_BARSKY.lineRectIntersect(int0 + float0, int1 + float0, int2 - int0, int3 - int1, int7, int6, int5, int4, doubles)) {
                        return byte0 / 2;
                    }
                }

                return -1;
            }
        }

        private void checkPolylineOutline() {
            if (this.polylineOutlinePoints == null) {
                if (this.isPolyline()) {
                    if (this.polylineWidth > 0) {
                        if (IsoMetaGrid.s_clipperOffset == null) {
                            IsoMetaGrid.s_clipperOffset = new ClipperOffset();
                            IsoMetaGrid.s_clipperBuffer = ByteBuffer.allocateDirect(3072);
                        }

                        ClipperOffset clipperOffset = IsoMetaGrid.s_clipperOffset;
                        ByteBuffer byteBuffer = IsoMetaGrid.s_clipperBuffer;
                        clipperOffset.clear();
                        byteBuffer.clear();
                        float float0 = this.polylineWidth % 2 == 0 ? 0.0F : 0.5F;

                        for (byte byte0 = 0; byte0 < this.points.size(); byte0 += 2) {
                            int int0 = this.points.get(byte0);
                            int int1 = this.points.get(byte0 + 1);
                            byteBuffer.putFloat(int0 + float0);
                            byteBuffer.putFloat(int1 + float0);
                        }

                        byteBuffer.flip();
                        clipperOffset.addPath(
                            this.points.size() / 2, byteBuffer, ClipperOffset.JoinType.jtMiter.ordinal(), ClipperOffset.EndType.etOpenButt.ordinal()
                        );
                        clipperOffset.execute(this.polylineWidth / 2.0F);
                        int int2 = clipperOffset.getPolygonCount();
                        if (int2 < 1) {
                            DebugLog.General.warn("Failed to generate polyline outline");
                        } else {
                            byteBuffer.clear();
                            clipperOffset.getPolygon(0, byteBuffer);
                            short short0 = byteBuffer.getShort();
                            this.polylineOutlinePoints = new float[short0 * 2];

                            for (int int3 = 0; int3 < short0; int3++) {
                                this.polylineOutlinePoints[int3 * 2] = byteBuffer.getFloat();
                                this.polylineOutlinePoints[int3 * 2 + 1] = byteBuffer.getFloat();
                            }
                        }
                    }
                }
            }
        }

        float isLeft(float float3, float float1, float float5, float float0, float float2, float float4) {
            return (float5 - float3) * (float4 - float1) - (float2 - float3) * (float0 - float1);
        }

        IsoMetaGrid.Zone.PolygonHit isPointInPolygon_WindingNumber(float float1, float float0, int var3) {
            int int0 = 0;

            for (byte byte0 = 0; byte0 < this.points.size(); byte0 += 2) {
                int int1 = this.points.getQuick(byte0);
                int int2 = this.points.getQuick(byte0 + 1);
                int int3 = this.points.getQuick((byte0 + 2) % this.points.size());
                int int4 = this.points.getQuick((byte0 + 3) % this.points.size());
                if (int2 <= float0) {
                    if (int4 > float0 && this.isLeft(int1, int2, int3, int4, float1, float0) > 0.0F) {
                        int0++;
                    }
                } else if (int4 <= float0 && this.isLeft(int1, int2, int3, int4, float1, float0) < 0.0F) {
                    int0--;
                }
            }

            return int0 == 0 ? IsoMetaGrid.Zone.PolygonHit.Outside : IsoMetaGrid.Zone.PolygonHit.Inside;
        }

        IsoMetaGrid.Zone.PolygonHit isPointInPolyline_WindingNumber(float float5, float float4, int var3) {
            int int0 = 0;
            float[] floats = this.polylineOutlinePoints;
            if (floats == null) {
                return IsoMetaGrid.Zone.PolygonHit.Outside;
            } else {
                for (byte byte0 = 0; byte0 < floats.length; byte0 += 2) {
                    float float0 = floats[byte0];
                    float float1 = floats[byte0 + 1];
                    float float2 = floats[(byte0 + 2) % floats.length];
                    float float3 = floats[(byte0 + 3) % floats.length];
                    if (float1 <= float4) {
                        if (float3 > float4 && this.isLeft(float0, float1, float2, float3, float5, float4) > 0.0F) {
                            int0++;
                        }
                    } else if (float3 <= float4 && this.isLeft(float0, float1, float2, float3, float5, float4) < 0.0F) {
                        int0--;
                    }
                }

                return int0 == 0 ? IsoMetaGrid.Zone.PolygonHit.Outside : IsoMetaGrid.Zone.PolygonHit.Inside;
            }
        }

        boolean polygonRectIntersect(int int1, int int0, int int3, int int2) {
            return this.x >= int1 && this.x + this.w <= int1 + int3 && this.y >= int0 && this.y + this.h <= int0 + int2
                ? true
                : this.lineSegmentIntersects(int1, int0, int1 + int3, int0)
                    || this.lineSegmentIntersects(int1 + int3, int0, int1 + int3, int0 + int2)
                    || this.lineSegmentIntersects(int1 + int3, int0 + int2, int1, int0 + int2)
                    || this.lineSegmentIntersects(int1, int0 + int2, int1, int0);
        }

        boolean lineSegmentIntersects(float float3, float float1, float float2, float float0) {
            L_lineSegmentIntersects.set(float2 - float3, float0 - float1);
            float float4 = L_lineSegmentIntersects.getLength();
            L_lineSegmentIntersects.normalize();
            float float5 = L_lineSegmentIntersects.x;
            float float6 = L_lineSegmentIntersects.y;

            for (byte byte0 = 0; byte0 < this.points.size(); byte0 += 2) {
                float float7 = this.points.getQuick(byte0);
                float float8 = this.points.getQuick(byte0 + 1);
                float float9 = this.points.getQuick((byte0 + 2) % this.points.size());
                float float10 = this.points.getQuick((byte0 + 3) % this.points.size());
                float float11 = float3 - float7;
                float float12 = float1 - float8;
                float float13 = float9 - float7;
                float float14 = float10 - float8;
                float float15 = 1.0F / (float14 * float5 - float13 * float6);
                float float16 = (float13 * float12 - float14 * float11) * float15;
                if (float16 >= 0.0F && float16 <= float4) {
                    float float17 = (float12 * float5 - float11 * float6) * float15;
                    if (float17 >= 0.0F && float17 <= 1.0F) {
                        return true;
                    }
                }
            }

            return this.isPointInPolygon_WindingNumber((float3 + float2) / 2.0F, (float1 + float0) / 2.0F, 0) != IsoMetaGrid.Zone.PolygonHit.Outside;
        }

        boolean polylineOutlineRectIntersect(int int1, int int0, int int3, int int2) {
            if (this.polylineOutlinePoints == null) {
                return false;
            } else {
                return this.x >= int1 && this.x + this.w <= int1 + int3 && this.y >= int0 && this.y + this.h <= int0 + int2
                    ? true
                    : this.polylineOutlineSegmentIntersects(int1, int0, int1 + int3, int0)
                        || this.polylineOutlineSegmentIntersects(int1 + int3, int0, int1 + int3, int0 + int2)
                        || this.polylineOutlineSegmentIntersects(int1 + int3, int0 + int2, int1, int0 + int2)
                        || this.polylineOutlineSegmentIntersects(int1, int0 + int2, int1, int0);
            }
        }

        boolean polylineOutlineSegmentIntersects(float float3, float float1, float float2, float float0) {
            L_lineSegmentIntersects.set(float2 - float3, float0 - float1);
            float float4 = L_lineSegmentIntersects.getLength();
            L_lineSegmentIntersects.normalize();
            float float5 = L_lineSegmentIntersects.x;
            float float6 = L_lineSegmentIntersects.y;
            float[] floats = this.polylineOutlinePoints;

            for (byte byte0 = 0; byte0 < floats.length; byte0 += 2) {
                float float7 = floats[byte0];
                float float8 = floats[byte0 + 1];
                float float9 = floats[(byte0 + 2) % floats.length];
                float float10 = floats[(byte0 + 3) % floats.length];
                float float11 = float3 - float7;
                float float12 = float1 - float8;
                float float13 = float9 - float7;
                float float14 = float10 - float8;
                float float15 = 1.0F / (float14 * float5 - float13 * float6);
                float float16 = (float13 * float12 - float14 * float11) * float15;
                if (float16 >= 0.0F && float16 <= float4) {
                    float float17 = (float12 * float5 - float11 * float6) * float15;
                    if (float17 >= 0.0F && float17 <= 1.0F) {
                        return true;
                    }
                }
            }

            return this.isPointInPolyline_WindingNumber((float3 + float2) / 2.0F, (float1 + float0) / 2.0F, 0) != IsoMetaGrid.Zone.PolygonHit.Outside;
        }

        private boolean isClockwise() {
            if (!this.isPolygon()) {
                return false;
            } else {
                float float0 = 0.0F;

                for (byte byte0 = 0; byte0 < this.points.size(); byte0 += 2) {
                    int int0 = this.points.getQuick(byte0);
                    int int1 = this.points.getQuick(byte0 + 1);
                    int int2 = this.points.getQuick((byte0 + 2) % this.points.size());
                    int int3 = this.points.getQuick((byte0 + 3) % this.points.size());
                    float0 += (int2 - int0) * (int3 + int1);
                }

                return float0 > 0.0;
            }
        }

        public float[] getPolygonTriangles() {
            if (this.triangles != null) {
                return this.triangles;
            } else if (this.bTriangulateFailed) {
                return null;
            } else if (!this.isPolygon()) {
                return null;
            } else {
                if (IsoMetaGrid.s_clipper == null) {
                    IsoMetaGrid.s_clipper = new Clipper();
                    IsoMetaGrid.s_clipperBuffer = ByteBuffer.allocateDirect(3072);
                }

                Clipper clipper = IsoMetaGrid.s_clipper;
                ByteBuffer byteBuffer = IsoMetaGrid.s_clipperBuffer;
                byteBuffer.clear();
                if (this.isClockwise()) {
                    for (int int0 = this.points.size() - 1; int0 > 0; int0 -= 2) {
                        byteBuffer.putFloat(this.points.getQuick(int0 - 1));
                        byteBuffer.putFloat(this.points.getQuick(int0));
                    }
                } else {
                    for (byte byte0 = 0; byte0 < this.points.size(); byte0 += 2) {
                        byteBuffer.putFloat(this.points.getQuick(byte0));
                        byteBuffer.putFloat(this.points.getQuick(byte0 + 1));
                    }
                }

                clipper.clear();
                clipper.addPath(this.points.size() / 2, byteBuffer, false);
                int int1 = clipper.generatePolygons();
                if (int1 < 1) {
                    this.bTriangulateFailed = true;
                    return null;
                } else {
                    byteBuffer.clear();
                    int int2 = clipper.triangulate(0, byteBuffer);
                    this.triangles = new float[int2 * 2];

                    for (int int3 = 0; int3 < int2; int3++) {
                        this.triangles[int3 * 2] = byteBuffer.getFloat();
                        this.triangles[int3 * 2 + 1] = byteBuffer.getFloat();
                    }

                    this.initTriangleAreas();
                    return this.triangles;
                }
            }
        }

        private float triangleArea(float float4, float float2, float float3, float float1, float float7, float float6) {
            float float0 = Vector2f.length(float3 - float4, float1 - float2);
            float float5 = Vector2f.length(float7 - float3, float6 - float1);
            float float8 = Vector2f.length(float4 - float7, float2 - float6);
            float float9 = (float0 + float5 + float8) / 2.0F;
            return (float)Math.sqrt(float9 * (float9 - float0) * (float9 - float5) * (float9 - float8));
        }

        private void initTriangleAreas() {
            int int0 = this.triangles.length / 6;
            this.triangleAreas = new float[int0];
            this.totalArea = 0.0F;

            for (byte byte0 = 0; byte0 < this.triangles.length; byte0 += 6) {
                float float0 = this.triangles[byte0];
                float float1 = this.triangles[byte0 + 1];
                float float2 = this.triangles[byte0 + 2];
                float float3 = this.triangles[byte0 + 3];
                float float4 = this.triangles[byte0 + 4];
                float float5 = this.triangles[byte0 + 5];
                float float6 = this.triangleArea(float0, float1, float2, float3, float4, float5);
                this.triangleAreas[byte0 / 6] = float6;
                this.totalArea += float6;
            }
        }

        public float[] getPolylineOutlineTriangles() {
            if (this.triangles != null) {
                return this.triangles;
            } else if (!this.isPolyline() || this.polylineWidth <= 0) {
                return null;
            } else if (this.bTriangulateFailed) {
                return null;
            } else {
                this.checkPolylineOutline();
                float[] floats = this.polylineOutlinePoints;
                if (floats == null) {
                    this.bTriangulateFailed = true;
                    return null;
                } else {
                    if (IsoMetaGrid.s_clipper == null) {
                        IsoMetaGrid.s_clipper = new Clipper();
                        IsoMetaGrid.s_clipperBuffer = ByteBuffer.allocateDirect(3072);
                    }

                    Clipper clipper = IsoMetaGrid.s_clipper;
                    ByteBuffer byteBuffer = IsoMetaGrid.s_clipperBuffer;
                    byteBuffer.clear();
                    if (this.isClockwise()) {
                        for (int int0 = floats.length - 1; int0 > 0; int0 -= 2) {
                            byteBuffer.putFloat(floats[int0 - 1]);
                            byteBuffer.putFloat(floats[int0]);
                        }
                    } else {
                        for (byte byte0 = 0; byte0 < floats.length; byte0 += 2) {
                            byteBuffer.putFloat(floats[byte0]);
                            byteBuffer.putFloat(floats[byte0 + 1]);
                        }
                    }

                    clipper.clear();
                    clipper.addPath(floats.length / 2, byteBuffer, false);
                    int int1 = clipper.generatePolygons();
                    if (int1 < 1) {
                        this.bTriangulateFailed = true;
                        return null;
                    } else {
                        byteBuffer.clear();
                        int int2 = clipper.triangulate(0, byteBuffer);
                        this.triangles = new float[int2 * 2];

                        for (int int3 = 0; int3 < int2; int3++) {
                            this.triangles[int3 * 2] = byteBuffer.getFloat();
                            this.triangles[int3 * 2 + 1] = byteBuffer.getFloat();
                        }

                        this.initTriangleAreas();
                        return this.triangles;
                    }
                }
            }
        }

        public float getPolylineLength() {
            if (this.isPolyline() && !this.points.isEmpty()) {
                float float0 = 0.0F;

                for (byte byte0 = 0; byte0 < this.points.size() - 2; byte0 += 2) {
                    int int0 = this.points.get(byte0);
                    int int1 = this.points.get(byte0 + 1);
                    int int2 = this.points.get(byte0 + 2);
                    int int3 = this.points.get(byte0 + 3);
                    float0 += Vector2f.length(int2 - int0, int3 - int1);
                }

                return float0;
            } else {
                return 0.0F;
            }
        }

        public void Dispose() {
            this.pickedRZStory = null;
            this.points.clear();
            this.polylineOutlinePoints = null;
            this.spawnedZombies.clear();
            this.triangles = null;
        }

        private static enum PolygonHit {
            OnEdge,
            Inside,
            Outside;
        }
    }

    public static enum ZoneGeometryType {
        INVALID,
        Point,
        Polyline,
        Polygon;
    }
}
