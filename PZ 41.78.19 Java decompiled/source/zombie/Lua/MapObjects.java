// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.Lua;

import gnu.trove.list.array.TShortArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Prototype;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.network.GameServer;
import zombie.network.ServerMap;

public final class MapObjects {
    private static final HashMap<String, MapObjects.Callback> onNew = new HashMap<>();
    private static final HashMap<String, MapObjects.Callback> onLoad = new HashMap<>();
    private static final ArrayList<IsoObject> tempObjects = new ArrayList<>();
    private static final Object[] params = new Object[1];

    private static MapObjects.Callback getOnNew(String string) {
        MapObjects.Callback callback = onNew.get(string);
        if (callback == null) {
            callback = new MapObjects.Callback(string);
            onNew.put(string, callback);
        }

        return callback;
    }

    public static void OnNewWithSprite(String spriteName, LuaClosure __function__, int priority) {
        if (spriteName != null && !spriteName.isEmpty()) {
            if (__function__ == null) {
                throw new NullPointerException("function is null");
            } else {
                MapObjects.Callback callback = getOnNew(spriteName);

                for (int int0 = 0; int0 < callback.functions.size(); int0++) {
                    if (callback.priority.get(int0) < priority) {
                        callback.functions.add(int0, __function__);
                        callback.priority.insert(int0, (short)priority);
                        return;
                    }

                    if (callback.priority.get(int0) == priority) {
                        callback.functions.set(int0, __function__);
                        callback.priority.set(int0, (short)priority);
                        return;
                    }
                }

                callback.functions.add(__function__);
                callback.priority.add((short)priority);
            }
        } else {
            throw new IllegalArgumentException("invalid sprite name");
        }
    }

    public static void OnNewWithSprite(KahluaTable spriteNames, LuaClosure __function__, int priority) {
        if (spriteNames != null && !spriteNames.isEmpty()) {
            if (__function__ == null) {
                throw new NullPointerException("function is null");
            } else {
                KahluaTableIterator kahluaTableIterator = spriteNames.iterator();

                while (kahluaTableIterator.advance()) {
                    Object object = kahluaTableIterator.getValue();
                    if (!(object instanceof String)) {
                        throw new IllegalArgumentException("expected string but got \"" + object + "\"");
                    }

                    OnNewWithSprite((String)object, __function__, priority);
                }
            }
        } else {
            throw new IllegalArgumentException("invalid sprite-name table");
        }
    }

    public static void newGridSquare(IsoGridSquare square) {
        if (square != null && !square.getObjects().isEmpty()) {
            tempObjects.clear();

            for (int int0 = 0; int0 < square.getObjects().size(); int0++) {
                tempObjects.add(square.getObjects().get(int0));
            }

            for (int int1 = 0; int1 < tempObjects.size(); int1++) {
                IsoObject object = tempObjects.get(int1);
                if (square.getObjects().contains(object) && !(object instanceof IsoWorldInventoryObject) && object != null && object.sprite != null) {
                    String string = object.sprite.name == null ? object.spriteName : object.sprite.name;
                    if (string != null && !string.isEmpty()) {
                        MapObjects.Callback callback = onNew.get(string);
                        if (callback != null) {
                            params[0] = object;

                            for (int int2 = 0; int2 < callback.functions.size(); int2++) {
                                try {
                                    LuaManager.caller.protectedCallVoid(LuaManager.thread, callback.functions.get(int2), params);
                                } catch (Throwable throwable) {
                                    ExceptionLogger.logException(throwable);
                                }

                                string = object.sprite != null && object.sprite.name != null ? object.sprite.name : object.spriteName;
                                if (!square.getObjects().contains(object) || object.sprite == null || !callback.spriteName.equals(string)) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static MapObjects.Callback getOnLoad(String string) {
        MapObjects.Callback callback = onLoad.get(string);
        if (callback == null) {
            callback = new MapObjects.Callback(string);
            onLoad.put(string, callback);
        }

        return callback;
    }

    public static void OnLoadWithSprite(String spriteName, LuaClosure __function__, int priority) {
        if (spriteName != null && !spriteName.isEmpty()) {
            if (__function__ == null) {
                throw new NullPointerException("function is null");
            } else {
                MapObjects.Callback callback = getOnLoad(spriteName);

                for (int int0 = 0; int0 < callback.functions.size(); int0++) {
                    if (callback.priority.get(int0) < priority) {
                        callback.functions.add(int0, __function__);
                        callback.priority.insert(int0, (short)priority);
                        return;
                    }

                    if (callback.priority.get(int0) == priority) {
                        callback.functions.set(int0, __function__);
                        callback.priority.set(int0, (short)priority);
                        return;
                    }
                }

                callback.functions.add(__function__);
                callback.priority.add((short)priority);
            }
        } else {
            throw new IllegalArgumentException("invalid sprite name");
        }
    }

    public static void OnLoadWithSprite(KahluaTable spriteNames, LuaClosure __function__, int priority) {
        if (spriteNames != null && !spriteNames.isEmpty()) {
            if (__function__ == null) {
                throw new NullPointerException("function is null");
            } else {
                KahluaTableIterator kahluaTableIterator = spriteNames.iterator();

                while (kahluaTableIterator.advance()) {
                    Object object = kahluaTableIterator.getValue();
                    if (!(object instanceof String)) {
                        throw new IllegalArgumentException("expected string but got \"" + object + "\"");
                    }

                    OnLoadWithSprite((String)object, __function__, priority);
                }
            }
        } else {
            throw new IllegalArgumentException("invalid sprite-name table");
        }
    }

    public static void loadGridSquare(IsoGridSquare square) {
        if (square != null && !square.getObjects().isEmpty()) {
            tempObjects.clear();

            for (int int0 = 0; int0 < square.getObjects().size(); int0++) {
                tempObjects.add(square.getObjects().get(int0));
            }

            for (int int1 = 0; int1 < tempObjects.size(); int1++) {
                IsoObject object = tempObjects.get(int1);
                if (square.getObjects().contains(object) && !(object instanceof IsoWorldInventoryObject) && object != null && object.sprite != null) {
                    String string = object.sprite.name == null ? object.spriteName : object.sprite.name;
                    if (string != null && !string.isEmpty()) {
                        MapObjects.Callback callback = onLoad.get(string);
                        if (callback != null) {
                            params[0] = object;

                            for (int int2 = 0; int2 < callback.functions.size(); int2++) {
                                try {
                                    LuaManager.caller.protectedCallVoid(LuaManager.thread, callback.functions.get(int2), params);
                                } catch (Throwable throwable) {
                                    ExceptionLogger.logException(throwable);
                                }

                                string = object.sprite != null && object.sprite.name != null ? object.sprite.name : object.spriteName;
                                if (!square.getObjects().contains(object) || object.sprite == null || !callback.spriteName.equals(string)) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void debugNewSquare(int x, int y, int z) {
        if (Core.bDebug) {
            IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(x, y, z);
            if (square != null) {
                newGridSquare(square);
            }
        }
    }

    public static void debugLoadSquare(int x, int y, int z) {
        if (Core.bDebug) {
            IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(x, y, z);
            if (square != null) {
                loadGridSquare(square);
            }
        }
    }

    public static void debugLoadChunk(int wx, int wy) {
        if (Core.bDebug) {
            IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(wx, wy) : IsoWorld.instance.CurrentCell.getChunk(wx, wy);
            if (chunk != null) {
                for (int int0 = 0; int0 <= chunk.maxLevel; int0++) {
                    for (int int1 = 0; int1 < 10; int1++) {
                        for (int int2 = 0; int2 < 10; int2++) {
                            IsoGridSquare square = chunk.getGridSquare(int1, int2, int0);
                            if (square != null && !square.getObjects().isEmpty()) {
                                loadGridSquare(square);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void reroute(Prototype prototype, LuaClosure luaClosure) {
        for (MapObjects.Callback callback : onNew.values()) {
            for (int int0 = 0; int0 < callback.functions.size(); int0++) {
                LuaClosure _luaClosure = callback.functions.get(int0);
                if (_luaClosure.prototype.filename.equals(prototype.filename) && _luaClosure.prototype.name.equals(prototype.name)) {
                    callback.functions.set(int0, luaClosure);
                }
            }
        }
    }

    public static void Reset() {
        onNew.clear();
        onLoad.clear();
    }

    private static final class Callback {
        final String spriteName;
        final ArrayList<LuaClosure> functions = new ArrayList<>();
        final TShortArrayList priority = new TShortArrayList();

        Callback(String string) {
            this.spriteName = string;
        }
    }
}
