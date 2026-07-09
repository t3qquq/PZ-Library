// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.util.Type;

public final class SpawnPoints {
    public static final SpawnPoints instance = new SpawnPoints();
    private KahluaTable SpawnRegions;
    private final ArrayList<IsoGameCharacter.Location> SpawnPoints = new ArrayList<>();
    private final ArrayList<BuildingDef> SpawnBuildings = new ArrayList<>();
    private final IsoGameCharacter.Location m_tempLocation = new IsoGameCharacter.Location(-1, -1, -1);

    public void init() {
        this.SpawnRegions = LuaManager.platform.newTable();
        this.SpawnPoints.clear();
        this.SpawnBuildings.clear();
    }

    public void initServer1() {
        this.init();
        this.initSpawnRegions();
    }

    public void initServer2() {
        if (!this.parseServerSpawnPoint()) {
            this.parseSpawnRegions();
            this.initSpawnBuildings();
        }
    }

    public void initSinglePlayer() {
        this.init();
        this.initSpawnRegions();
        this.parseSpawnRegions();
        this.initSpawnBuildings();
    }

    private void initSpawnRegions() {
        KahluaTable table = (KahluaTable)LuaManager.env.rawget("SpawnRegionMgr");
        if (table == null) {
            DebugLog.General.error("SpawnRegionMgr is undefined");
        } else {
            Object[] objects = LuaManager.caller.pcall(LuaManager.thread, table.rawget("getSpawnRegions"));
            if (objects.length > 1 && objects[1] instanceof KahluaTable) {
                this.SpawnRegions = (KahluaTable)objects[1];
            }
        }
    }

    private boolean parseServerSpawnPoint() {
        if (!GameServer.bServer) {
            return false;
        } else if (ServerOptions.instance.SpawnPoint.getValue().isEmpty()) {
            return false;
        } else {
            String[] strings = ServerOptions.instance.SpawnPoint.getValue().split(",");
            if (strings.length == 3) {
                try {
                    int int0 = Integer.parseInt(strings[0].trim());
                    int int1 = Integer.parseInt(strings[1].trim());
                    int int2 = Integer.parseInt(strings[2].trim());
                    if (int0 != 0 || int1 != 0) {
                        this.SpawnPoints.add(new IsoGameCharacter.Location(int0, int1, int2));
                        return true;
                    }
                } catch (NumberFormatException numberFormatException) {
                    DebugLog.General.error("SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
                }
            } else {
                DebugLog.General.error("SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
            }

            return false;
        }
    }

    private void parseSpawnRegions() {
        KahluaTableIterator kahluaTableIterator = this.SpawnRegions.iterator();

        while (kahluaTableIterator.advance()) {
            KahluaTable table = Type.tryCastTo(kahluaTableIterator.getValue(), KahluaTable.class);
            if (table != null) {
                this.parseRegion(table);
            }
        }
    }

    private void parseRegion(KahluaTable table1) {
        KahluaTable table0 = Type.tryCastTo(table1.rawget("points"), KahluaTable.class);
        if (table0 != null) {
            KahluaTableIterator kahluaTableIterator = table0.iterator();

            while (kahluaTableIterator.advance()) {
                KahluaTable table2 = Type.tryCastTo(kahluaTableIterator.getValue(), KahluaTable.class);
                if (table2 != null) {
                    this.parseProfession(table2);
                }
            }
        }
    }

    private void parseProfession(KahluaTable table0) {
        KahluaTableIterator kahluaTableIterator = table0.iterator();

        while (kahluaTableIterator.advance()) {
            KahluaTable table1 = Type.tryCastTo(kahluaTableIterator.getValue(), KahluaTable.class);
            if (table1 != null) {
                this.parsePoint(table1);
            }
        }
    }

    private void parsePoint(KahluaTable table) {
        Double double0 = Type.tryCastTo(table.rawget("worldX"), Double.class);
        Double double1 = Type.tryCastTo(table.rawget("worldY"), Double.class);
        Double double2 = Type.tryCastTo(table.rawget("posX"), Double.class);
        Double double3 = Type.tryCastTo(table.rawget("posY"), Double.class);
        Double double4 = Type.tryCastTo(table.rawget("posZ"), Double.class);
        if (double0 != null && double1 != null && double2 != null && double3 != null) {
            this.m_tempLocation.x = double0.intValue() * 300 + double2.intValue();
            this.m_tempLocation.y = double1.intValue() * 300 + double3.intValue();
            this.m_tempLocation.z = double4 == null ? 0 : double4.intValue();
            if (!this.SpawnPoints.contains(this.m_tempLocation)) {
                IsoGameCharacter.Location location = new IsoGameCharacter.Location(this.m_tempLocation.x, this.m_tempLocation.y, this.m_tempLocation.z);
                this.SpawnPoints.add(location);
            }
        }
    }

    private void initSpawnBuildings() {
        for (int int0 = 0; int0 < this.SpawnPoints.size(); int0++) {
            IsoGameCharacter.Location location = this.SpawnPoints.get(int0);
            RoomDef roomDef = IsoWorld.instance.MetaGrid.getRoomAt(location.x, location.y, location.z);
            if (roomDef != null && roomDef.getBuilding() != null) {
                this.SpawnBuildings.add(roomDef.getBuilding());
            } else {
                DebugLog.General.warn("initSpawnBuildings: no room or building at %d,%d,%d", location.x, location.y, location.z);
            }
        }
    }

    public boolean isSpawnBuilding(BuildingDef buildingDef) {
        return this.SpawnBuildings.contains(buildingDef);
    }

    public KahluaTable getSpawnRegions() {
        return this.SpawnRegions;
    }
}
