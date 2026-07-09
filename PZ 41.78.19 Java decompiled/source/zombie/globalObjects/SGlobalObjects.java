// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.globalObjects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.iso.IsoObject;
import zombie.iso.SliceY;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.TableNetworkUtils;

public final class SGlobalObjects {
    protected static final ArrayList<SGlobalObjectSystem> systems = new ArrayList<>();

    public static void noise(String message) {
        if (Core.bDebug) {
            DebugLog.log("SGlobalObjects: " + message);
        }
    }

    public static SGlobalObjectSystem registerSystem(String name) {
        SGlobalObjectSystem sGlobalObjectSystem = getSystemByName(name);
        if (sGlobalObjectSystem == null) {
            sGlobalObjectSystem = newSystem(name);
            sGlobalObjectSystem.load();
        }

        return sGlobalObjectSystem;
    }

    public static SGlobalObjectSystem newSystem(String name) throws IllegalStateException {
        if (getSystemByName(name) != null) {
            throw new IllegalStateException("system with that name already exists");
        } else {
            noise("newSystem " + name);
            SGlobalObjectSystem sGlobalObjectSystem = new SGlobalObjectSystem(name);
            systems.add(sGlobalObjectSystem);
            return sGlobalObjectSystem;
        }
    }

    public static int getSystemCount() {
        return systems.size();
    }

    public static SGlobalObjectSystem getSystemByIndex(int index) {
        return index >= 0 && index < systems.size() ? systems.get(index) : null;
    }

    public static SGlobalObjectSystem getSystemByName(String name) {
        for (int int0 = 0; int0 < systems.size(); int0++) {
            SGlobalObjectSystem sGlobalObjectSystem = systems.get(int0);
            if (sGlobalObjectSystem.name.equals(name)) {
                return sGlobalObjectSystem;
            }
        }

        return null;
    }

    public static void update() {
        for (int int0 = 0; int0 < systems.size(); int0++) {
            SGlobalObjectSystem sGlobalObjectSystem = systems.get(int0);
            sGlobalObjectSystem.update();
        }
    }

    public static void chunkLoaded(int wx, int wy) {
        for (int int0 = 0; int0 < systems.size(); int0++) {
            SGlobalObjectSystem sGlobalObjectSystem = systems.get(int0);
            sGlobalObjectSystem.chunkLoaded(wx, wy);
        }
    }

    public static void initSystems() {
        if (!GameClient.bClient) {
            LuaEventManager.triggerEvent("OnSGlobalObjectSystemInit");
            if (!GameServer.bServer) {
                try {
                    synchronized (SliceY.SliceBufferLock) {
                        SliceY.SliceBuffer.clear();
                        saveInitialStateForClient(SliceY.SliceBuffer);
                        SliceY.SliceBuffer.flip();
                        CGlobalObjects.loadInitialState(SliceY.SliceBuffer);
                    }
                } catch (Throwable throwable) {
                    ExceptionLogger.logException(throwable);
                }
            }
        }
    }

    public static void saveInitialStateForClient(ByteBuffer bb) throws IOException {
        bb.put((byte)systems.size());

        for (int int0 = 0; int0 < systems.size(); int0++) {
            SGlobalObjectSystem sGlobalObjectSystem = systems.get(int0);
            GameWindow.WriteStringUTF(bb, sGlobalObjectSystem.name);
            KahluaTable table0 = sGlobalObjectSystem.getInitialStateForClient();
            if (table0 == null) {
                table0 = LuaManager.platform.newTable();
            }

            KahluaTable table1 = LuaManager.platform.newTable();
            table0.rawset("_objects", table1);

            for (int int1 = 0; int1 < sGlobalObjectSystem.getObjectCount(); int1++) {
                GlobalObject globalObject = sGlobalObjectSystem.getObjectByIndex(int1);
                KahluaTable table2 = LuaManager.platform.newTable();
                table2.rawset("x", BoxedStaticValues.toDouble(globalObject.getX()));
                table2.rawset("y", BoxedStaticValues.toDouble(globalObject.getY()));
                table2.rawset("z", BoxedStaticValues.toDouble(globalObject.getZ()));

                for (String string : sGlobalObjectSystem.objectSyncKeys) {
                    table2.rawset(string, globalObject.getModData().rawget(string));
                }

                table1.rawset(int1 + 1, table2);
            }

            if (table0 != null && !table0.isEmpty()) {
                bb.put((byte)1);
                TableNetworkUtils.save(table0, bb);
            } else {
                bb.put((byte)0);
            }
        }
    }

    public static boolean receiveClientCommand(String systemName, String command, IsoPlayer playerObj, KahluaTable args) {
        noise("receiveClientCommand " + systemName + " " + command + " OnlineID=" + playerObj.getOnlineID());
        SGlobalObjectSystem sGlobalObjectSystem = getSystemByName(systemName);
        if (sGlobalObjectSystem == null) {
            throw new IllegalStateException("system '" + systemName + "' not found");
        } else {
            sGlobalObjectSystem.receiveClientCommand(command, playerObj, args);
            return true;
        }
    }

    public static void load() {
    }

    public static void save() {
        for (int int0 = 0; int0 < systems.size(); int0++) {
            SGlobalObjectSystem sGlobalObjectSystem = systems.get(int0);
            sGlobalObjectSystem.save();
        }
    }

    public static void OnIsoObjectChangedItself(String systemName, IsoObject isoObject) {
        if (!GameClient.bClient) {
            SGlobalObjectSystem sGlobalObjectSystem = getSystemByName(systemName);
            if (sGlobalObjectSystem != null) {
                sGlobalObjectSystem.OnIsoObjectChangedItself(isoObject);
            }
        }
    }

    public static void Reset() {
        for (int int0 = 0; int0 < systems.size(); int0++) {
            SGlobalObjectSystem sGlobalObjectSystem = systems.get(int0);
            sGlobalObjectSystem.Reset();
        }

        systems.clear();
        GlobalObjectLookup.Reset();
    }
}
