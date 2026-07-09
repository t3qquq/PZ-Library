// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.globalObjects;

import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;

public abstract class GlobalObject {
    protected GlobalObjectSystem system;
    protected int x;
    protected int y;
    protected int z;
    protected final KahluaTable modData;

    GlobalObject(GlobalObjectSystem globalObjectSystem, int int0, int int1, int int2) {
        this.system = globalObjectSystem;
        this.x = int0;
        this.y = int1;
        this.z = int2;
        this.modData = LuaManager.platform.newTable();
    }

    public GlobalObjectSystem getSystem() {
        return this.system;
    }

    public void setLocation(int _x, int _y, int _z) {
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

    public KahluaTable getModData() {
        return this.modData;
    }

    public void Reset() {
        this.system = null;
        this.modData.wipe();
    }
}
