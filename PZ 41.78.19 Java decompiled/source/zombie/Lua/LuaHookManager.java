// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.Lua;

import java.util.ArrayList;
import java.util.HashMap;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;
import zombie.debug.DebugLog;

public final class LuaHookManager implements JavaFunction {
    public static final ArrayList<LuaClosure> OnTickCallbacks = new ArrayList<>();
    static Object[] a = new Object[1];
    static Object[] b = new Object[2];
    static Object[] c = new Object[3];
    static Object[] d = new Object[4];
    static Object[] f = new Object[5];
    static Object[] g = new Object[6];
    private static final ArrayList<Event> EventList = new ArrayList<>();
    private static final HashMap<String, Event> EventMap = new HashMap<>();

    public static boolean TriggerHook(String string) {
        if (EventMap.containsKey(string)) {
            Event event = EventMap.get(string);
            a[0] = null;
            return event.trigger(LuaManager.env, LuaManager.caller, a);
        } else {
            return false;
        }
    }

    public static boolean TriggerHook(String string, Object object) {
        if (EventMap.containsKey(string)) {
            Event event = EventMap.get(string);
            a[0] = object;
            return event.trigger(LuaManager.env, LuaManager.caller, a);
        } else {
            return false;
        }
    }

    public static boolean TriggerHook(String string, Object object0, Object object1) {
        if (EventMap.containsKey(string)) {
            Event event = EventMap.get(string);
            b[0] = object0;
            b[1] = object1;
            return event.trigger(LuaManager.env, LuaManager.caller, b);
        } else {
            return false;
        }
    }

    public static boolean TriggerHook(String string, Object object0, Object object1, Object object2) {
        if (EventMap.containsKey(string)) {
            Event event = EventMap.get(string);
            c[0] = object0;
            c[1] = object1;
            c[2] = object2;
            return event.trigger(LuaManager.env, LuaManager.caller, c);
        } else {
            return false;
        }
    }

    public static boolean TriggerHook(String string, Object object0, Object object1, Object object2, Object object3) {
        if (EventMap.containsKey(string)) {
            Event event = EventMap.get(string);
            d[0] = object0;
            d[1] = object1;
            d[2] = object2;
            d[3] = object3;
            return event.trigger(LuaManager.env, LuaManager.caller, d);
        } else {
            return false;
        }
    }

    public static boolean TriggerHook(String string, Object object0, Object object1, Object object2, Object object3, Object object4) {
        if (EventMap.containsKey(string)) {
            Event event = EventMap.get(string);
            f[0] = object0;
            f[1] = object1;
            f[2] = object2;
            f[3] = object3;
            f[4] = object4;
            return event.trigger(LuaManager.env, LuaManager.caller, f);
        } else {
            return false;
        }
    }

    public static boolean TriggerHook(String string, Object object0, Object object1, Object object2, Object object3, Object object4, Object object5) {
        if (EventMap.containsKey(string)) {
            Event event = EventMap.get(string);
            g[0] = object0;
            g[1] = object1;
            g[2] = object2;
            g[3] = object3;
            g[4] = object4;
            g[5] = object5;
            return event.trigger(LuaManager.env, LuaManager.caller, g);
        } else {
            return false;
        }
    }

    public static void AddEvent(String string) {
        if (!EventMap.containsKey(string)) {
            Event event = new Event(string, EventList.size());
            EventList.add(event);
            EventMap.put(string, event);
            if (LuaManager.env.rawget("Hook") instanceof KahluaTable table) {
                event.register(LuaManager.platform, table);
            } else {
                DebugLog.log("ERROR: 'Hook' table not found or not a table");
            }
        }
    }

    private static void AddEvents() {
        AddEvent("AutoDrink");
        AddEvent("UseItem");
        AddEvent("Attack");
        AddEvent("CalculateStats");
        AddEvent("WeaponHitCharacter");
        AddEvent("WeaponSwing");
        AddEvent("WeaponSwingHitPoint");
    }

    public static void clear() {
        a[0] = null;
        b[0] = null;
        b[1] = null;
        c[0] = null;
        c[1] = null;
        c[2] = null;
        d[0] = null;
        d[1] = null;
        d[2] = null;
        d[3] = null;
        f[0] = null;
        f[1] = null;
        f[2] = null;
        f[3] = null;
        f[4] = null;
        g[0] = null;
        g[1] = null;
        g[2] = null;
        g[3] = null;
        g[4] = null;
        g[5] = null;
    }

    public static void register(Platform platform, KahluaTable table1) {
        KahluaTable table0 = platform.newTable();
        table1.rawset("Hook", table0);
        AddEvents();
    }

    public static void Reset() {
        for (Event event : EventList) {
            event.callbacks.clear();
        }

        EventList.clear();
        EventMap.clear();
    }

    @Override
    public int call(LuaCallFrame var1, int var2) {
        return 0;
    }

    private int OnTick(LuaCallFrame var1, int var2) {
        return 0;
    }
}
