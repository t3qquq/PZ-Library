/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zombie.debug.DebugLog
 */
package se.krka.kahlua.stdlib;

import java.util.function.Consumer;
import se.krka.kahlua.vm.Coroutine;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaException;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import zombie.debug.DebugLog;

public final class BaseLib
implements JavaFunction {
    private static final Runtime RUNTIME = Runtime.getRuntime();
    private static final int PCALL = 0;
    private static final int PRINT = 1;
    private static final int SELECT = 2;
    private static final int TYPE = 3;
    private static final int TOSTRING = 4;
    private static final int TONUMBER = 5;
    private static final int GETMETATABLE = 6;
    private static final int SETMETATABLE = 7;
    private static final int ERROR = 8;
    private static final int UNPACK = 9;
    private static final int SETFENV = 10;
    private static final int GETFENV = 11;
    private static final int RAWEQUAL = 12;
    private static final int RAWSET = 13;
    private static final int RAWGET = 14;
    private static final int COLLECTGARBAGE = 15;
    private static final int DEBUGSTACKTRACE = 16;
    private static final int BYTECODELOADER = 17;
    private static final int NUM_FUNCTIONS = 18;
    private static final String[] names;
    private static final Object DOUBLE_ONE;
    private static final BaseLib[] functions;
    private final int index;
    private static Consumer<String> PRINT_CALLBACK;

    public BaseLib(int n) {
        this.index = n;
    }

    public static void register(KahluaTable kahluaTable) {
        for (int i = 0; i < 18; ++i) {
            kahluaTable.rawset(names[i], (Object)functions[i]);
        }
    }

    public String toString() {
        return names[this.index];
    }

    @Override
    public int call(LuaCallFrame luaCallFrame, int n) {
        switch (this.index) {
            case 0: {
                return BaseLib.pcall(luaCallFrame, n);
            }
            case 1: {
                return BaseLib.print(luaCallFrame, n);
            }
            case 2: {
                return BaseLib.select(luaCallFrame, n);
            }
            case 3: {
                return BaseLib.type(luaCallFrame, n);
            }
            case 4: {
                return BaseLib.tostring(luaCallFrame, n);
            }
            case 5: {
                return BaseLib.tonumber(luaCallFrame, n);
            }
            case 6: {
                return BaseLib.getmetatable(luaCallFrame, n);
            }
            case 7: {
                return BaseLib.setmetatable(luaCallFrame, n);
            }
            case 8: {
                return this.error(luaCallFrame, n);
            }
            case 9: {
                return this.unpack(luaCallFrame, n);
            }
            case 10: {
                return this.setfenv(luaCallFrame, n);
            }
            case 11: {
                return this.getfenv(luaCallFrame, n);
            }
            case 12: {
                return this.rawequal(luaCallFrame, n);
            }
            case 13: {
                return this.rawset(luaCallFrame, n);
            }
            case 14: {
                return this.rawget(luaCallFrame, n);
            }
            case 15: {
                return BaseLib.collectgarbage(luaCallFrame, n);
            }
            case 16: {
                return this.debugstacktrace(luaCallFrame, n);
            }
            case 17: {
                return BaseLib.bytecodeloader(luaCallFrame, n);
            }
        }
        return 0;
    }

    private int debugstacktrace(LuaCallFrame luaCallFrame, int n) {
        Coroutine coroutine = (Coroutine)KahluaUtil.getOptionalArg(luaCallFrame, 1);
        if (coroutine == null) {
            coroutine = luaCallFrame.coroutine;
        }
        Double d = KahluaUtil.getOptionalNumberArg(luaCallFrame, 2);
        int n2 = 0;
        if (d != null) {
            n2 = d.intValue();
        }
        Double d2 = KahluaUtil.getOptionalNumberArg(luaCallFrame, 3);
        int n3 = Integer.MAX_VALUE;
        if (d2 != null) {
            n3 = d2.intValue();
        }
        Double d3 = KahluaUtil.getOptionalNumberArg(luaCallFrame, 4);
        int n4 = 0;
        if (d3 != null) {
            n4 = d3.intValue();
        }
        return luaCallFrame.push(coroutine.getCurrentStackTrace(n2, n3, n4));
    }

    private int rawget(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 2, "Not enough arguments");
        KahluaTable kahluaTable = (KahluaTable)luaCallFrame.get(0);
        Object object = luaCallFrame.get(1);
        luaCallFrame.push(kahluaTable.rawget(object));
        return 1;
    }

    private int rawset(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 3, "Not enough arguments");
        KahluaTable kahluaTable = (KahluaTable)luaCallFrame.get(0);
        Object object = luaCallFrame.get(1);
        Object object2 = luaCallFrame.get(2);
        kahluaTable.rawset(object, object2);
        luaCallFrame.setTop(1);
        return 1;
    }

    private int rawequal(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 2, "Not enough arguments");
        Object object = luaCallFrame.get(0);
        Object object2 = luaCallFrame.get(1);
        luaCallFrame.push(KahluaUtil.toBoolean(BaseLib.luaEquals(object, object2)));
        return 1;
    }

    private int setfenv(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 2, "Not enough arguments");
        KahluaTable kahluaTable = (KahluaTable)luaCallFrame.get(1);
        KahluaUtil.luaAssert(kahluaTable != null, "expected a table");
        LuaClosure luaClosure = null;
        Object object = luaCallFrame.get(0);
        if (object instanceof LuaClosure) {
            luaClosure = (LuaClosure)object;
        } else {
            KahluaUtil.luaAssert((object = KahluaUtil.rawTonumber(object)) != null, "expected a lua function or a number");
            int n2 = ((Double)object).intValue();
            if (n2 == 0) {
                luaCallFrame.coroutine.environment = kahluaTable;
                return 0;
            }
            LuaCallFrame luaCallFrame2 = luaCallFrame.coroutine.getParent(n2);
            if (!luaCallFrame2.isLua()) {
                KahluaUtil.fail("No closure found at this level: " + n2);
            }
            luaClosure = luaCallFrame2.closure;
        }
        luaClosure.env = kahluaTable;
        luaCallFrame.setTop(1);
        return 1;
    }

    private int getfenv(LuaCallFrame luaCallFrame, int n) {
        Object object = DOUBLE_ONE;
        if (n >= 1) {
            object = luaCallFrame.get(0);
        }
        KahluaTable kahluaTable = null;
        if (object == null || object instanceof JavaFunction) {
            kahluaTable = luaCallFrame.coroutine.environment;
        } else if (object instanceof LuaClosure) {
            LuaClosure luaClosure = (LuaClosure)object;
            kahluaTable = luaClosure.env;
        } else {
            Double d = KahluaUtil.rawTonumber(object);
            KahluaUtil.luaAssert(d != null, "Expected number");
            int n2 = d.intValue();
            KahluaUtil.luaAssert(n2 >= 0, "level must be non-negative");
            LuaCallFrame luaCallFrame2 = luaCallFrame.coroutine.getParent(n2);
            kahluaTable = luaCallFrame2.getEnvironment();
        }
        luaCallFrame.push(kahluaTable);
        return 1;
    }

    private int unpack(LuaCallFrame luaCallFrame, int n) {
        int n2;
        int n3;
        int n4;
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        KahluaTable kahluaTable = (KahluaTable)luaCallFrame.get(0);
        Object object = null;
        Object object2 = null;
        if (n >= 2) {
            object = luaCallFrame.get(1);
        }
        if (n >= 3) {
            object2 = luaCallFrame.get(2);
        }
        if ((n4 = 1 + (n3 = object2 != null ? (int)KahluaUtil.fromDouble(object2) : kahluaTable.len()) - (n2 = object != null ? (int)KahluaUtil.fromDouble(object) : 1)) <= 0) {
            luaCallFrame.setTop(0);
            return 0;
        }
        luaCallFrame.setTop(n4);
        for (int i = 0; i < n4; ++i) {
            luaCallFrame.set(i, kahluaTable.rawget(KahluaUtil.toDouble(n2 + i)));
        }
        return n4;
    }

    private int error(LuaCallFrame luaCallFrame, int n) {
        if (n >= 1) {
            String string = KahluaUtil.getOptionalStringArg(luaCallFrame, 2);
            if (string == null) {
                string = "";
            }
            luaCallFrame.coroutine.stackTrace = string;
            throw new KahluaException(luaCallFrame.get(0));
        }
        return 0;
    }

    public static int pcall(LuaCallFrame luaCallFrame, int n) {
        return luaCallFrame.getThread().pcall(n - 1);
    }

    private static int print(LuaCallFrame luaCallFrame, int n) {
        KahluaThread kahluaThread = luaCallFrame.getThread();
        KahluaTable kahluaTable = kahluaThread.getEnvironment();
        Object object = kahluaThread.tableget(kahluaTable, "tostring");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < n; ++i) {
            if (i > 0) {
                stringBuilder.append("\t");
            }
            Object object2 = kahluaThread.call(object, luaCallFrame.get(i), null, null);
            stringBuilder.append(object2);
        }
        String string = stringBuilder.toString();
        DebugLog.log((String)string);
        if (PRINT_CALLBACK != null) {
            PRINT_CALLBACK.accept(string);
        }
        return 0;
    }

    public static void setPrintCallback(Consumer<String> consumer) {
        PRINT_CALLBACK = consumer;
    }

    private static int select(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        Object object = luaCallFrame.get(0);
        if (object instanceof String && ((String)object).startsWith("#")) {
            luaCallFrame.push(KahluaUtil.toDouble(n - 1));
            return 1;
        }
        Double d = KahluaUtil.rawTonumber(object);
        double d2 = KahluaUtil.fromDouble(d);
        int n2 = (int)d2;
        if (n2 >= 1 && n2 <= n - 1) {
            int n3 = n - n2;
            return n3;
        }
        return 0;
    }

    private static int getmetatable(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        Object object = luaCallFrame.get(0);
        Object object2 = luaCallFrame.getThread().getmetatable(object, false);
        luaCallFrame.push(object2);
        return 1;
    }

    private static int setmetatable(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 2, "Not enough arguments");
        Object object = luaCallFrame.get(0);
        KahluaTable kahluaTable = (KahluaTable)luaCallFrame.get(1);
        BaseLib.setmetatable(luaCallFrame.getThread(), object, kahluaTable, false);
        luaCallFrame.setTop(1);
        return 1;
    }

    public static void setmetatable(KahluaThread kahluaThread, Object object, KahluaTable kahluaTable, boolean bl) {
        KahluaUtil.luaAssert(object != null, "Expected table, got nil");
        Object object2 = kahluaThread.getmetatable(object, true);
        if (!bl && object2 != null && kahluaThread.tableget(object2, "__metatable") != null) {
            throw new RuntimeException("cannot change a protected metatable");
        }
        kahluaThread.setmetatable(object, kahluaTable);
    }

    private static int type(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        Object object = luaCallFrame.get(0);
        luaCallFrame.push(KahluaUtil.type(object));
        return 1;
    }

    private static int tostring(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        Object object = luaCallFrame.get(0);
        String string = KahluaUtil.tostring(object, luaCallFrame.getThread());
        luaCallFrame.push(string);
        return 1;
    }

    private static int tonumber(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        Object object = luaCallFrame.get(0);
        if (n == 1) {
            luaCallFrame.push(KahluaUtil.rawTonumber(object));
            return 1;
        }
        String string = (String)object;
        Object object2 = luaCallFrame.get(1);
        Double d = KahluaUtil.rawTonumber(object2);
        if (d == null) {
            luaCallFrame.push(null);
            return 1;
        }
        KahluaUtil.luaAssert(d != null, "Argument 2 must be a number");
        double d2 = KahluaUtil.fromDouble(d);
        int n2 = (int)d2;
        if ((double)n2 != d2) {
            luaCallFrame.push(null);
            return 1;
        }
        if ((double)n2 != d2) {
            throw new RuntimeException("base is not an integer");
        }
        Double d3 = KahluaUtil.tonumber(string, n2);
        luaCallFrame.push(d3);
        return 1;
    }

    public static int collectgarbage(LuaCallFrame luaCallFrame, int n) {
        Object object = null;
        if (n > 0) {
            object = luaCallFrame.get(0);
        }
        if (object == null || object.equals("step") || object.equals("collect")) {
            System.gc();
            return 0;
        }
        if (object.equals("count")) {
            long l = RUNTIME.freeMemory();
            long l2 = RUNTIME.totalMemory();
            luaCallFrame.setTop(3);
            luaCallFrame.set(0, BaseLib.toKiloBytes(l2 - l));
            luaCallFrame.set(1, BaseLib.toKiloBytes(l));
            luaCallFrame.set(2, BaseLib.toKiloBytes(l2));
            return 3;
        }
        throw new RuntimeException("invalid option: " + object);
    }

    private static Double toKiloBytes(long l) {
        return KahluaUtil.toDouble((double)l / 1024.0);
    }

    private static int bytecodeloader(LuaCallFrame luaCallFrame, int n) {
        String string = KahluaUtil.getStringArg(luaCallFrame, 1, "loader");
        KahluaTable kahluaTable = (KahluaTable)luaCallFrame.getEnvironment().rawget("package");
        String string2 = (String)kahluaTable.rawget("classpath");
        int n2 = 0;
        while (n2 < string2.length()) {
            Object object;
            int n3 = string2.indexOf(";", n2);
            if (n3 == -1) {
                n3 = string2.length();
            }
            if (((String)(object = string2.substring(n2, n3))).length() > 0) {
                LuaClosure luaClosure;
                if (!((String)object).endsWith("/")) {
                    object = (String)object + "/";
                }
                if ((luaClosure = KahluaUtil.loadByteCodeFromResource((String)object + string, luaCallFrame.getEnvironment())) != null) {
                    return luaCallFrame.push(luaClosure);
                }
            }
            n2 = n3;
        }
        return luaCallFrame.push("Could not find the bytecode for '" + string + "' in classpath");
    }

    public static boolean luaEquals(Object object, Object object2) {
        if (object == null || object2 == null) {
            return object == object2;
        }
        if (object instanceof Double && object2 instanceof Double) {
            Double d = (Double)object;
            Double d2 = (Double)object2;
            return d.doubleValue() == d2.doubleValue();
        }
        return object == object2;
    }

    static {
        DOUBLE_ONE = new Double(1.0);
        names = new String[18];
        BaseLib.names[0] = "pcall";
        BaseLib.names[1] = "print";
        BaseLib.names[2] = "select";
        BaseLib.names[3] = "type";
        BaseLib.names[4] = "tostring";
        BaseLib.names[5] = "tonumber";
        BaseLib.names[6] = "getmetatable";
        BaseLib.names[7] = "setmetatable";
        BaseLib.names[8] = "error";
        BaseLib.names[9] = "unpack";
        BaseLib.names[10] = "setfenv";
        BaseLib.names[11] = "getfenv";
        BaseLib.names[12] = "rawequal";
        BaseLib.names[13] = "rawset";
        BaseLib.names[14] = "rawget";
        BaseLib.names[15] = "collectgarbage";
        BaseLib.names[16] = "debugstacktrace";
        BaseLib.names[17] = "bytecodeloader";
        functions = new BaseLib[18];
        for (int i = 0; i < 18; ++i) {
            BaseLib.functions[i] = new BaseLib(i);
        }
        PRINT_CALLBACK = null;
    }
}

