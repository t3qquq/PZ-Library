// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.globalObjects;

import java.util.ArrayDeque;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.util.StringUtils;

public abstract class GlobalObjectSystem {
    private static final ArrayDeque<ArrayList<GlobalObject>> objectListPool = new ArrayDeque<>();
    protected final String name;
    protected final KahluaTable modData;
    protected final ArrayList<GlobalObject> objects = new ArrayList<>();
    protected final GlobalObjectLookup lookup = new GlobalObjectLookup(this);

    GlobalObjectSystem(String string) {
        if (StringUtils.containsDoubleDot(string)) {
            throw new IllegalStateException("Name of GlobalObjectSystem invalid: %s".formatted(string));
        } else {
            this.name = string;
            this.modData = LuaManager.platform.newTable();
        }
    }

    public String getName() {
        return this.name;
    }

    public final KahluaTable getModData() {
        return this.modData;
    }

    protected abstract GlobalObject makeObject(int var1, int var2, int var3);

    public final GlobalObject newObject(int x, int y, int z) {
        if (this.getObjectAt(x, y, z) != null) {
            throw new IllegalStateException("already an object at " + x + "," + y + "," + z);
        } else {
            GlobalObject globalObject = this.makeObject(x, y, z);
            this.objects.add(globalObject);
            this.lookup.addObject(globalObject);
            return globalObject;
        }
    }

    public final void removeObject(GlobalObject object) throws IllegalArgumentException, IllegalStateException {
        if (object == null) {
            throw new NullPointerException("object is null");
        } else if (object.system != this) {
            throw new IllegalStateException("object not in this system");
        } else {
            this.objects.remove(object);
            this.lookup.removeObject(object);
            object.Reset();
        }
    }

    public final GlobalObject getObjectAt(int x, int y, int z) {
        return this.lookup.getObjectAt(x, y, z);
    }

    public final boolean hasObjectsInChunk(int wx, int wy) {
        return this.lookup.hasObjectsInChunk(wx, wy);
    }

    public final ArrayList<GlobalObject> getObjectsInChunk(int wx, int wy) {
        return this.lookup.getObjectsInChunk(wx, wy, this.allocList());
    }

    public final ArrayList<GlobalObject> getObjectsAdjacentTo(int x, int y, int z) {
        return this.lookup.getObjectsAdjacentTo(x, y, z, this.allocList());
    }

    public final int getObjectCount() {
        return this.objects.size();
    }

    public final GlobalObject getObjectByIndex(int index) {
        return index >= 0 && index < this.objects.size() ? this.objects.get(index) : null;
    }

    public final ArrayList<GlobalObject> allocList() {
        return objectListPool.isEmpty() ? new ArrayList<>() : objectListPool.pop();
    }

    public final void finishedWithList(ArrayList<GlobalObject> list) {
        if (list != null && !objectListPool.contains(list)) {
            list.clear();
            objectListPool.add(list);
        }
    }

    public void Reset() {
        for (int int0 = 0; int0 < this.objects.size(); int0++) {
            GlobalObject globalObject = this.objects.get(int0);
            globalObject.Reset();
        }

        this.objects.clear();
        this.modData.wipe();
    }
}
