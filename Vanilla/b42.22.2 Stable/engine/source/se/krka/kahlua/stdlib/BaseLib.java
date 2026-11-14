// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
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
    private static final Object DOUBLE_ONE = 1.0;
    private static final BaseLib[] functions = new BaseLib[18];
    private final int index;
    private static Consumer<String> printCallback;

    public BaseLib(int index) {
        this.index = index;
    }

    public static void register(KahluaTable environment) {
        for (int i = 0; i < 18; i++) {
            environment.rawset(names[i], functions[i]);
        }
    }

    @Override
    public String toString() {
        return names[this.index];
    }

    @Override
    public int call(LuaCallFrame callFrame, int nArguments) {
        switch (this.index) {
            case 0:
                return pcall(callFrame, nArguments);
            case 1:
                return print(callFrame, nArguments);
            case 2:
                return select(callFrame, nArguments);
            case 3:
                return type(callFrame, nArguments);
            case 4:
                return tostring(callFrame, nArguments);
            case 5:
                return tonumber(callFrame, nArguments);
            case 6:
                return getmetatable(callFrame, nArguments);
            case 7:
                return setmetatable(callFrame, nArguments);
            case 8:
                return this.error(callFrame, nArguments);
            case 9:
                return this.unpack(callFrame, nArguments);
            case 10:
                return this.setfenv(callFrame, nArguments);
            case 11:
                return this.getfenv(callFrame, nArguments);
            case 12:
                return this.rawequal(callFrame, nArguments);
            case 13:
                return this.rawset(callFrame, nArguments);
            case 14:
                return this.rawget(callFrame, nArguments);
            case 15:
                return collectgarbage(callFrame, nArguments);
            case 16:
                return this.debugstacktrace(callFrame, nArguments);
            case 17:
                return bytecodeloader(callFrame, nArguments);
            default:
                return 0;
        }
    }

    private int debugstacktrace(LuaCallFrame callFrame, int nArguments) {
        Coroutine coroutine = (Coroutine)KahluaUtil.getOptionalArg(callFrame, 1);
        if (coroutine == null) {
            coroutine = callFrame.coroutine;
        }

        Double levelDouble = KahluaUtil.getOptionalNumberArg(callFrame, 2);
        int level = 0;
        if (levelDouble != null) {
            level = levelDouble.intValue();
        }

        Double countDouble = KahluaUtil.getOptionalNumberArg(callFrame, 3);
        int count = Integer.MAX_VALUE;
        if (countDouble != null) {
            count = countDouble.intValue();
        }

        Double haltAtDouble = KahluaUtil.getOptionalNumberArg(callFrame, 4);
        int haltAt = 0;
        if (haltAtDouble != null) {
            haltAt = haltAtDouble.intValue();
        }

        return callFrame.push(coroutine.getCurrentStackTrace(level, count, haltAt));
    }

    private int rawget(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 2, "Not enough arguments");
        KahluaTable t = (KahluaTable)callFrame.get(0);
        Object key = callFrame.get(1);
        callFrame.push(t.rawget(key));
        return 1;
    }

    private int rawset(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 3, "Not enough arguments");
        KahluaTable t = (KahluaTable)callFrame.get(0);
        Object key = callFrame.get(1);
        Object value = callFrame.get(2);
        t.rawset(key, value);
        callFrame.setTop(1);
        return 1;
    }

    private int rawequal(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 2, "Not enough arguments");
        Object o1 = callFrame.get(0);
        Object o2 = callFrame.get(1);
        callFrame.push(KahluaUtil.toBoolean(luaEquals(o1, o2)));
        return 1;
    }

    private int setfenv(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 2, "Not enough arguments");
        KahluaTable newEnv = (KahluaTable)callFrame.get(1);
        KahluaUtil.luaAssert(newEnv != null, "expected a table");
        LuaClosure closure = null;
        Object o = callFrame.get(0);
        if (o instanceof LuaClosure luaClosure) {
            closure = luaClosure;
        } else {
            o = KahluaUtil.rawTonumber(o);
            KahluaUtil.luaAssert(o != null, "expected a lua function or a number");
            int level = ((Double)o).intValue();
            if (level == 0) {
                callFrame.coroutine.environment = newEnv;
                return 0;
            }

            LuaCallFrame parentCallFrame = callFrame.coroutine.getParent(level);
            if (!parentCallFrame.isLua()) {
                KahluaUtil.fail("No closure found at this level: " + level);
            }

            closure = parentCallFrame.closure;
        }

        closure.env = newEnv;
        callFrame.setTop(1);
        return 1;
    }

    private int getfenv(LuaCallFrame callFrame, int nArguments) {
        Object o = DOUBLE_ONE;
        if (nArguments >= 1) {
            o = callFrame.get(0);
        }

        Object res = null;
        if (o == null || o instanceof JavaFunction) {
            res = callFrame.coroutine.environment;
        } else if (o instanceof LuaClosure closure) {
            res = closure.env;
        } else {
            Double d = KahluaUtil.rawTonumber(o);
            KahluaUtil.luaAssert(d != null, "Expected number");
            int level = d.intValue();
            KahluaUtil.luaAssert(level >= 0, "level must be non-negative");
            LuaCallFrame callFrame2 = callFrame.coroutine.getParent(level);
            res = callFrame2.getEnvironment();
        }

        callFrame.push(res);
        return 1;
    }

    private int unpack(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        KahluaTable t = (KahluaTable)callFrame.get(0);
        Object di = null;
        Object dj = null;
        if (nArguments >= 2) {
            di = callFrame.get(1);
        }

        if (nArguments >= 3) {
            dj = callFrame.get(2);
        }

        int i;
        if (di != null) {
            i = (int)KahluaUtil.fromDouble(di);
        } else {
            i = 1;
        }

        int j;
        if (dj != null) {
            j = (int)KahluaUtil.fromDouble(dj);
        } else {
            j = t.len();
        }

        int nReturnValues = 1 + j - i;
        if (nReturnValues <= 0) {
            callFrame.setTop(0);
            return 0;
        }

        callFrame.setTop(nReturnValues);

        for (int b = 0; b < nReturnValues; b++) {
            callFrame.set(b, t.rawget(KahluaUtil.toDouble(i + b)));
        }

        return nReturnValues;
    }

    private int error(LuaCallFrame callFrame, int nArguments) {
        if (nArguments >= 1) {
            String stacktrace = KahluaUtil.getOptionalStringArg(callFrame, 2);
            if (stacktrace == null) {
                stacktrace = "";
            }

            callFrame.coroutine.stackTrace = stacktrace;
            throw new KahluaException(callFrame.get(0));
        } else {
            return 0;
        }
    }

    public static int pcall(LuaCallFrame callFrame, int nArguments) {
        return callFrame.getThread().pcall(nArguments - 1);
    }

    private static int print(LuaCallFrame callFrame, int nArguments) {
        KahluaThread thread = callFrame.getThread();
        KahluaTable env = thread.getEnvironment();
        Object toStringFun = thread.tableget(env, "tostring");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < nArguments; i++) {
            if (i > 0) {
                sb.append("\t");
            }

            Object res = thread.call(toStringFun, callFrame.get(i), null, null);
            sb.append(res);
        }

        String str = sb.toString();
        thread.getOut().println(str);
        if (printCallback != null) {
            printCallback.accept(str);
        }

        return 0;
    }

    public static void setPrintCallback(Consumer<String> callback) {
        printCallback = callback;
    }

    private static int select(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        Object arg1 = callFrame.get(0);
        if (arg1 instanceof String s && s.startsWith("#")) {
            callFrame.push(KahluaUtil.toDouble(nArguments - 1));
            return 1;
        } else {
            Double d_indexDouble = KahluaUtil.rawTonumber(arg1);
            double d_index = KahluaUtil.fromDouble(d_indexDouble);
            int index = (int)d_index;
            return index >= 1 && index <= nArguments - 1 ? nArguments - index : 0;
        }
    }

    private static int getmetatable(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        Object o = callFrame.get(0);
        Object metatable = callFrame.getThread().getmetatable(o, false);
        callFrame.push(metatable);
        return 1;
    }

    private static int setmetatable(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 2, "Not enough arguments");
        Object o = callFrame.get(0);
        KahluaTable newMeta = (KahluaTable)callFrame.get(1);
        setmetatable(callFrame.getThread(), o, newMeta, false);
        callFrame.setTop(1);
        return 1;
    }

    public static void setmetatable(KahluaThread thread, Object o, KahluaTable newMeta, boolean raw) {
        KahluaUtil.luaAssert(o != null, "Expected table, got nil");
        Object oldMeta = thread.getmetatable(o, true);
        if (!raw && oldMeta != null && thread.tableget(oldMeta, "__metatable") != null) {
            throw new RuntimeException("cannot change a protected metatable");
        }

        thread.setmetatable(o, newMeta);
    }

    private static int type(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        Object o = callFrame.get(0);
        callFrame.push(KahluaUtil.type(o));
        return 1;
    }

    private static int tostring(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        Object o = callFrame.get(0);
        Object res = KahluaUtil.tostring(o, callFrame.getThread());
        callFrame.push(res);
        return 1;
    }

    private static int tonumber(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        Object o = callFrame.get(0);
        if (nArguments == 1) {
            callFrame.push(KahluaUtil.rawTonumber(o));
            return 1;
        }

        String s = (String)o;
        Object radixObj = callFrame.get(1);
        Double radixDouble = KahluaUtil.rawTonumber(radixObj);
        if (radixDouble == null) {
            callFrame.push(null);
            return 1;
        }

        KahluaUtil.luaAssert(radixDouble != null, "Argument 2 must be a number");
        double dradix = KahluaUtil.fromDouble(radixDouble);
        int radix = (int)dradix;
        if (radix != dradix) {
            callFrame.push(null);
            return 1;
        }

        if (radix != dradix) {
            throw new RuntimeException("base is not an integer");
        }

        Object res = KahluaUtil.tonumber(s, radix);
        callFrame.push(res);
        return 1;
    }

    public static int collectgarbage(LuaCallFrame callFrame, int nArguments) {
        Object option = null;
        if (nArguments > 0) {
            option = callFrame.get(0);
        }

        if (option == null || option.equals("step") || option.equals("collect")) {
            System.gc();
            return 0;
        } else if (option.equals("count")) {
            long freeMemory = RUNTIME.freeMemory();
            long totalMemory = RUNTIME.totalMemory();
            callFrame.setTop(3);
            callFrame.set(0, toKiloBytes(totalMemory - freeMemory));
            callFrame.set(1, toKiloBytes(freeMemory));
            callFrame.set(2, toKiloBytes(totalMemory));
            return 3;
        } else {
            throw new RuntimeException("invalid option: " + option);
        }
    }

    private static Double toKiloBytes(long freeMemory) {
        return KahluaUtil.toDouble(freeMemory / 1024.0);
    }

    private static int bytecodeloader(LuaCallFrame callFrame, int nArguments) {
        String modname = KahluaUtil.getStringArg(callFrame, 1, "loader");
        KahluaTable packageTable = (KahluaTable)callFrame.getEnvironment().rawget("package");
        String classpath = (String)packageTable.rawget("classpath");
        int index = 0;

        while (index < classpath.length()) {
            int nextIndex = classpath.indexOf(";", index);
            if (nextIndex == -1) {
                nextIndex = classpath.length();
            }

            String path = classpath.substring(index, nextIndex);
            if (!path.isEmpty()) {
                if (!path.endsWith("/")) {
                    path = path + "/";
                }

                LuaClosure closure = KahluaUtil.loadByteCodeFromResource(path + modname, callFrame.getEnvironment());
                if (closure != null) {
                    return callFrame.push(closure);
                }
            }

            index = nextIndex;
        }

        return callFrame.push("Could not find the bytecode for '" + modname + "' in classpath");
    }

    public static boolean luaEquals(Object a, Object b) {
        if (a != null && b != null) {
            return a instanceof Double ad && b instanceof Double bd ? ad == bd : a == b;
        } else {
            return a == b;
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

        for (int i = 0; i < 18; i++) {
            functions[i] = new BaseLib(i);
        }
    }
}
