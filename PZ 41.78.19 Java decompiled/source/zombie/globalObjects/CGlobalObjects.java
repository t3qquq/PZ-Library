// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.globalObjects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.network.TableNetworkUtils;
import zombie.util.Type;

public final class CGlobalObjects {
    protected static final ArrayList<CGlobalObjectSystem> systems = new ArrayList<>();
    protected static final HashMap<String, KahluaTable> initialState = new HashMap<>();

    public static void noise(String message) {
        if (Core.bDebug) {
            DebugLog.log("CGlobalObjects: " + message);
        }
    }

    public static CGlobalObjectSystem registerSystem(String name) {
        CGlobalObjectSystem cGlobalObjectSystem = getSystemByName(name);
        if (cGlobalObjectSystem == null) {
            cGlobalObjectSystem = newSystem(name);
            KahluaTable table0 = initialState.get(name);
            if (table0 != null) {
                KahluaTableIterator kahluaTableIterator0 = table0.iterator();

                while (kahluaTableIterator0.advance()) {
                    Object object0 = kahluaTableIterator0.getKey();
                    Object object1 = kahluaTableIterator0.getValue();
                    if ("_objects".equals(object0)) {
                        KahluaTable table1 = Type.tryCastTo(object1, KahluaTable.class);
                        int int0 = 1;

                        for (int int1 = table1.len(); int0 <= int1; int0++) {
                            KahluaTable table2 = Type.tryCastTo(table1.rawget(int0), KahluaTable.class);
                            int int2 = ((Double)table2.rawget("x")).intValue();
                            int int3 = ((Double)table2.rawget("y")).intValue();
                            int int4 = ((Double)table2.rawget("z")).intValue();
                            table2.rawset("x", null);
                            table2.rawset("y", null);
                            table2.rawset("z", null);
                            CGlobalObject cGlobalObject = Type.tryCastTo(cGlobalObjectSystem.newObject(int2, int3, int4), CGlobalObject.class);
                            KahluaTableIterator kahluaTableIterator1 = table2.iterator();

                            while (kahluaTableIterator1.advance()) {
                                cGlobalObject.getModData().rawset(kahluaTableIterator1.getKey(), kahluaTableIterator1.getValue());
                            }
                        }

                        table1.wipe();
                    } else {
                        cGlobalObjectSystem.modData.rawset(object0, object1);
                    }
                }
            }
        }

        return cGlobalObjectSystem;
    }

    public static CGlobalObjectSystem newSystem(String name) throws IllegalStateException {
        if (getSystemByName(name) != null) {
            throw new IllegalStateException("system with that name already exists");
        } else {
            noise("newSystem " + name);
            CGlobalObjectSystem cGlobalObjectSystem = new CGlobalObjectSystem(name);
            systems.add(cGlobalObjectSystem);
            return cGlobalObjectSystem;
        }
    }

    public static int getSystemCount() {
        return systems.size();
    }

    public static CGlobalObjectSystem getSystemByIndex(int index) {
        return index >= 0 && index < systems.size() ? systems.get(index) : null;
    }

    public static CGlobalObjectSystem getSystemByName(String name) {
        for (int int0 = 0; int0 < systems.size(); int0++) {
            CGlobalObjectSystem cGlobalObjectSystem = systems.get(int0);
            if (cGlobalObjectSystem.name.equals(name)) {
                return cGlobalObjectSystem;
            }
        }

        return null;
    }

    public static void initSystems() {
        LuaEventManager.triggerEvent("OnCGlobalObjectSystemInit");
    }

    public static void loadInitialState(ByteBuffer bb) throws IOException {
        byte byte0 = bb.get();

        for (int int0 = 0; int0 < byte0; int0++) {
            String string = GameWindow.ReadStringUTF(bb);
            if (bb.get() != 0) {
                KahluaTable table = LuaManager.platform.newTable();
                initialState.put(string, table);
                TableNetworkUtils.load(table, bb);
            }
        }
    }

    public static boolean receiveServerCommand(String systemName, String command, KahluaTable args) {
        CGlobalObjectSystem cGlobalObjectSystem = getSystemByName(systemName);
        if (cGlobalObjectSystem == null) {
            throw new IllegalStateException("system '" + systemName + "' not found");
        } else {
            cGlobalObjectSystem.receiveServerCommand(command, args);
            return true;
        }
    }

    public static void Reset() {
        for (int int0 = 0; int0 < systems.size(); int0++) {
            CGlobalObjectSystem cGlobalObjectSystem = systems.get(int0);
            cGlobalObjectSystem.Reset();
        }

        systems.clear();
        initialState.clear();
        CGlobalObjectNetwork.Reset();
    }
}
