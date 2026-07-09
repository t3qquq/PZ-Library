// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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

public final class BaseLib implements JavaFunction {
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
    private static final String[] names = new String[18];
    private static final Object DOUBLE_ONE = new Double(1.0);
    private static final BaseLib[] functions = new BaseLib[18];
    private final int index;
    private static Consumer<String> PRINT_CALLBACK;

    public BaseLib(int int0) {
        this.index = int0;
    }

    public static void register(KahluaTable table) {
        for (int int0 = 0; int0 < 18; int0++) {
            table.rawset(names[int0], functions[int0]);
        }
    }

    @Override
    public String toString() {
        return names[this.index];
    }

    @Override
    public int call(LuaCallFrame luaCallFrame, int int0) {
        switch (this.index) {
            case 0:
                return pcall(luaCallFrame, int0);
            case 1:
                return print(luaCallFrame, int0);
            case 2:
                return select(luaCallFrame, int0);
            case 3:
                return type(luaCallFrame, int0);
            case 4:
                return tostring(luaCallFrame, int0);
            case 5:
                return tonumber(luaCallFrame, int0);
            case 6:
                return getmetatable(luaCallFrame, int0);
            case 7:
                return setmetatable(luaCallFrame, int0);
            case 8:
                return this.error(luaCallFrame, int0);
            case 9:
                return this.unpack(luaCallFrame, int0);
            case 10:
                return this.setfenv(luaCallFrame, int0);
            case 11:
                return this.getfenv(luaCallFrame, int0);
            case 12:
                return this.rawequal(luaCallFrame, int0);
            case 13:
                return this.rawset(luaCallFrame, int0);
            case 14:
                return this.rawget(luaCallFrame, int0);
            case 15:
                return collectgarbage(luaCallFrame, int0);
            case 16:
                return this.debugstacktrace(luaCallFrame, int0);
            case 17:
                return bytecodeloader(luaCallFrame, int0);
            default:
                return 0;
        }
    }

    private int debugstacktrace(LuaCallFrame luaCallFrame, int var2) {
        Coroutine coroutine = (Coroutine)KahluaUtil.getOptionalArg(luaCallFrame, 1);
        if (coroutine == null) {
            coroutine = luaCallFrame.coroutine;
        }

        Double double0 = KahluaUtil.getOptionalNumberArg(luaCallFrame, 2);
        int int0 = 0;
        if (double0 != null) {
            int0 = double0.intValue();
        }

        Double double1 = KahluaUtil.getOptionalNumberArg(luaCallFrame, 3);
        int int1 = Integer.MAX_VALUE;
        if (double1 != null) {
            int1 = double1.intValue();
        }

        Double double2 = KahluaUtil.getOptionalNumberArg(luaCallFrame, 4);
        int int2 = 0;
        if (double2 != null) {
            int2 = double2.intValue();
        }

        return luaCallFrame.push(coroutine.getCurrentStackTrace(int0, int1, int2));
    }

    private int rawget(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 2, "Not enough arguments");
        KahluaTable table = (KahluaTable)luaCallFrame.get(0);
        Object object = luaCallFrame.get(1);
        luaCallFrame.push(table.rawget(object));
        return 1;
    }

    private int rawset(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 3, "Not enough arguments");
        KahluaTable table = (KahluaTable)luaCallFrame.get(0);
        Object object0 = luaCallFrame.get(1);
        Object object1 = luaCallFrame.get(2);
        table.rawset(object0, object1);
        luaCallFrame.setTop(1);
        return 1;
    }

    private int rawequal(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 2, "Not enough arguments");
        Object object0 = luaCallFrame.get(0);
        Object object1 = luaCallFrame.get(1);
        luaCallFrame.push(KahluaUtil.toBoolean(luaEquals(object0, object1)));
        return 1;
    }

    private int setfenv(LuaCallFrame luaCallFrame0, int int0) {
        KahluaUtil.luaAssert(int0 >= 2, "Not enough arguments");
        KahluaTable table = (KahluaTable)luaCallFrame0.get(1);
        KahluaUtil.luaAssert(table != null, "expected a table");
        Object object0 = null;
        Object object1 = luaCallFrame0.get(0);
        if (!(object1 instanceof LuaClosure luaClosure)) {
            object1 = KahluaUtil.rawTonumber(object1);
            KahluaUtil.luaAssert(object1 != null, "expected a lua function or a number");
            int int1 = ((Double)object1).intValue();
            if (int1 == 0) {
                luaCallFrame0.coroutine.environment = table;
                return 0;
            }

            LuaCallFrame luaCallFrame1 = luaCallFrame0.coroutine.getParent(int1);
            if (!luaCallFrame1.isLua()) {
                KahluaUtil.fail("No closure found at this level: " + int1);
            }

            luaClosure = luaCallFrame1.closure;
        }

        luaClosure.env = table;
        luaCallFrame0.setTop(1);
        return 1;
    }

    private int getfenv(LuaCallFrame luaCallFrame0, int int0) {
        Object object0 = DOUBLE_ONE;
        if (int0 >= 1) {
            object0 = luaCallFrame0.get(0);
        }

        Object object1 = null;
        if (object0 == null || object0 instanceof JavaFunction) {
            object1 = luaCallFrame0.coroutine.environment;
        } else if (object0 instanceof LuaClosure luaClosure) {
            object1 = luaClosure.env;
        } else {
            Double double0 = KahluaUtil.rawTonumber(object0);
            KahluaUtil.luaAssert(double0 != null, "Expected number");
            int int1 = double0.intValue();
            KahluaUtil.luaAssert(int1 >= 0, "level must be non-negative");
            LuaCallFrame luaCallFrame1 = luaCallFrame0.coroutine.getParent(int1);
            object1 = luaCallFrame1.getEnvironment();
        }

        luaCallFrame0.push(object1);
        return 1;
    }

    private int unpack(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        KahluaTable table = (KahluaTable)luaCallFrame.get(0);
        Object object0 = null;
        Object object1 = null;
        if (int0 >= 2) {
            object0 = luaCallFrame.get(1);
        }

        if (int0 >= 3) {
            object1 = luaCallFrame.get(2);
        }

        int int1;
        if (object0 != null) {
            int1 = (int)KahluaUtil.fromDouble(object0);
        } else {
            int1 = 1;
        }

        int int2;
        if (object1 != null) {
            int2 = (int)KahluaUtil.fromDouble(object1);
        } else {
            int2 = table.len();
        }

        int int3 = 1 + int2 - int1;
        if (int3 <= 0) {
            luaCallFrame.setTop(0);
            return 0;
        } else {
            luaCallFrame.setTop(int3);

            for (int int4 = 0; int4 < int3; int4++) {
                luaCallFrame.set(int4, table.rawget(KahluaUtil.toDouble((long)(int1 + int4))));
            }

            return int3;
        }
    }

    private int error(LuaCallFrame luaCallFrame, int int0) {
        if (int0 >= 1) {
            String string = KahluaUtil.getOptionalStringArg(luaCallFrame, 2);
            if (string == null) {
                string = "";
            }

            luaCallFrame.coroutine.stackTrace = string;
            throw new KahluaException(luaCallFrame.get(0));
        } else {
            return 0;
        }
    }

    public static int pcall(LuaCallFrame luaCallFrame, int int0) {
        return luaCallFrame.getThread().pcall(int0 - 1);
    }

    private static int print(LuaCallFrame luaCallFrame, int int1) {
        KahluaThread kahluaThread = luaCallFrame.getThread();
        KahluaTable table = kahluaThread.getEnvironment();
        Object object0 = kahluaThread.tableget(table, "tostring");
        StringBuilder stringBuilder = new StringBuilder();

        for (int int0 = 0; int0 < int1; int0++) {
            if (int0 > 0) {
                stringBuilder.append("\t");
            }

            Object object1 = kahluaThread.call(object0, luaCallFrame.get(int0), null, null);
            stringBuilder.append(object1);
        }

        String string = stringBuilder.toString();
        DebugLog.log(string);
        if (PRINT_CALLBACK != null) {
            PRINT_CALLBACK.accept(string);
        }

        return 0;
    }

    public static void setPrintCallback(Consumer<String> consumer) {
        PRINT_CALLBACK = consumer;
    }

    private static int select(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        Object object = luaCallFrame.get(0);
        if (object instanceof String && ((String)object).startsWith("#")) {
            luaCallFrame.push(KahluaUtil.toDouble((long)(int0 - 1)));
            return 1;
        } else {
            Double double0 = KahluaUtil.rawTonumber(object);
            double double1 = KahluaUtil.fromDouble(double0);
            int int1 = (int)double1;
            return int1 >= 1 && int1 <= int0 - 1 ? int0 - int1 : 0;
        }
    }

    private static int getmetatable(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        Object object0 = luaCallFrame.get(0);
        Object object1 = luaCallFrame.getThread().getmetatable(object0, false);
        luaCallFrame.push(object1);
        return 1;
    }

    private static int setmetatable(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 2, "Not enough arguments");
        Object object = luaCallFrame.get(0);
        KahluaTable table = (KahluaTable)luaCallFrame.get(1);
        setmetatable(luaCallFrame.getThread(), object, table, false);
        luaCallFrame.setTop(1);
        return 1;
    }

    public static void setmetatable(KahluaThread kahluaThread, Object object0, KahluaTable table, boolean boolean0) {
        KahluaUtil.luaAssert(object0 != null, "Expected table, got nil");
        Object object1 = kahluaThread.getmetatable(object0, true);
        if (!boolean0 && object1 != null && kahluaThread.tableget(object1, "__metatable") != null) {
            throw new RuntimeException("cannot change a protected metatable");
        } else {
            kahluaThread.setmetatable(object0, table);
        }
    }

    private static int type(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        Object object = luaCallFrame.get(0);
        luaCallFrame.push(KahluaUtil.type(object));
        return 1;
    }

    private static int tostring(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        Object object = luaCallFrame.get(0);
        String string = KahluaUtil.tostring(object, luaCallFrame.getThread());
        luaCallFrame.push(string);
        return 1;
    }

    private static int tonumber(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        Object object0 = luaCallFrame.get(0);
        if (int0 == 1) {
            luaCallFrame.push(KahluaUtil.rawTonumber(object0));
            return 1;
        } else {
            String string = (String)object0;
            Object object1 = luaCallFrame.get(1);
            Double double0 = KahluaUtil.rawTonumber(object1);
            if (double0 == null) {
                luaCallFrame.push(null);
                return 1;
            } else {
                KahluaUtil.luaAssert(double0 != null, "Argument 2 must be a number");
                double double1 = KahluaUtil.fromDouble(double0);
                int int1 = (int)double1;
                if (int1 != double1) {
                    luaCallFrame.push(null);
                    return 1;
                } else if (int1 != double1) {
                    throw new RuntimeException("base is not an integer");
                } else {
                    Double double2 = KahluaUtil.tonumber(string, int1);
                    luaCallFrame.push(double2);
                    return 1;
                }
            }
        }
    }

    public static int collectgarbage(LuaCallFrame luaCallFrame, int int0) {
        Object object = null;
        if (int0 > 0) {
            object = luaCallFrame.get(0);
        }

        if (object == null || object.equals("step") || object.equals("collect")) {
            System.gc();
            return 0;
        } else if (object.equals("count")) {
            long long0 = RUNTIME.freeMemory();
            long long1 = RUNTIME.totalMemory();
            luaCallFrame.setTop(3);
            luaCallFrame.set(0, toKiloBytes(long1 - long0));
            luaCallFrame.set(1, toKiloBytes(long0));
            luaCallFrame.set(2, toKiloBytes(long1));
            return 3;
        } else {
            throw new RuntimeException("invalid option: " + object);
        }
    }

    private static Double toKiloBytes(long long0) {
        return KahluaUtil.toDouble(long0 / 1024.0);
    }

    private static int bytecodeloader(LuaCallFrame luaCallFrame, int var1) {
        String string0 = KahluaUtil.getStringArg(luaCallFrame, 1, "loader");
        KahluaTable table = (KahluaTable)luaCallFrame.getEnvironment().rawget("package");
        String string1 = (String)table.rawget("classpath");
        int int0 = 0;

        while (int0 < string1.length()) {
            int int1 = string1.indexOf(";", int0);
            if (int1 == -1) {
                int1 = string1.length();
            }

            String string2 = string1.substring(int0, int1);
            if (string2.length() > 0) {
                if (!string2.endsWith("/")) {
                    string2 = string2 + "/";
                }

                LuaClosure luaClosure = KahluaUtil.loadByteCodeFromResource(string2 + string0, luaCallFrame.getEnvironment());
                if (luaClosure != null) {
                    return luaCallFrame.push(luaClosure);
                }
            }

            int0 = int1;
        }

        return luaCallFrame.push("Could not find the bytecode for '" + string0 + "' in classpath");
    }

    public static boolean luaEquals(Object object1, Object object0) {
        if (object1 == null || object0 == null) {
            return object1 == object0;
        } else if (object1 instanceof Double && object0 instanceof Double) {
            Double double0 = (Double)object1;
            Double double1 = (Double)object0;
            return double0 == double1;
        } else {
            return object1 == object0;
        }
    }

    static {
        names[0] = "pcall";
        names[1] = "print";
        names[2] = "select";
        names[3] = "type";
        names[4] = "tostring";
        names[5] = "tonumber";
        names[6] = "getmetatable";
        names[7] = "setmetatable";
        names[8] = "error";
        names[9] = "unpack";
        names[10] = "setfenv";
        names[11] = "getfenv";
        names[12] = "rawequal";
        names[13] = "rawset";
        names[14] = "rawget";
        names[15] = "collectgarbage";
        names[16] = "debugstacktrace";
        names[17] = "bytecodeloader";

        for (int int0 = 0; int0 < 18; int0++) {
            functions[int0] = new BaseLib(int0);
        }

        PRINT_CALLBACK = null;
    }
}
