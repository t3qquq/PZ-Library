// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.test;

import java.util.Vector;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;

public class UserdataArray implements JavaFunction {
    private static final int LENGTH = 0;
    private static final int INDEX = 1;
    private static final int NEWINDEX = 2;
    private static final int NEW = 3;
    private static final int PUSH = 4;
    private static final Class VECTOR_CLASS = new Vector().getClass();
    private static KahluaTable metatable;
    private int index;

    public static synchronized void register(Platform platform, KahluaTable table1) {
        if (metatable == null) {
            metatable = platform.newTable();
            metatable.rawset("__metatable", "restricted");
            metatable.rawset("__len", new UserdataArray(0));
            metatable.rawset("__index", new UserdataArray(1));
            metatable.rawset("__newindex", new UserdataArray(2));
            metatable.rawset("new", new UserdataArray(3));
            metatable.rawset("push", new UserdataArray(4));
        }

        KahluaTable table0 = KahluaUtil.getClassMetatables(platform, table1);
        table0.rawset(VECTOR_CLASS, metatable);
        table1.rawset("array", metatable);
    }

    private UserdataArray(int int0) {
        this.index = int0;
    }

    @Override
    public int call(LuaCallFrame luaCallFrame, int int0) {
        switch (this.index) {
            case 0:
                return this.length(luaCallFrame, int0);
            case 1:
                return this.index(luaCallFrame, int0);
            case 2:
                return this.newindex(luaCallFrame, int0);
            case 3:
                return this.newVector(luaCallFrame, int0);
            case 4:
                return this.push(luaCallFrame, int0);
            default:
                return 0;
        }
    }

    private int push(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 2, "not enough parameters");
        Vector vector = (Vector)luaCallFrame.get(0);
        Object object = luaCallFrame.get(1);
        vector.addElement(object);
        luaCallFrame.push(vector);
        return 1;
    }

    private int newVector(LuaCallFrame luaCallFrame, int var2) {
        luaCallFrame.push(new Vector());
        return 1;
    }

    private int newindex(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 3, "not enough parameters");
        Vector vector = (Vector)luaCallFrame.get(0);
        Object object0 = luaCallFrame.get(1);
        Object object1 = luaCallFrame.get(2);
        vector.setElementAt(object1, (int)KahluaUtil.fromDouble(object0));
        return 0;
    }

    private int index(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 2, "not enough parameters");
        Object object0 = luaCallFrame.get(0);
        if (object0 != null && object0 instanceof Vector vector) {
            Object object1 = luaCallFrame.get(1);
            Object object2;
            if (object1 instanceof Double) {
                object2 = vector.elementAt((int)KahluaUtil.fromDouble(object1));
            } else {
                object2 = metatable.rawget(object1);
            }

            luaCallFrame.push(object2);
            return 1;
        } else {
            return 0;
        }
    }

    private int length(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "not enough parameters");
        Vector vector = (Vector)luaCallFrame.get(0);
        double double0 = vector.size();
        luaCallFrame.push(KahluaUtil.toDouble(double0));
        return 1;
    }
}
