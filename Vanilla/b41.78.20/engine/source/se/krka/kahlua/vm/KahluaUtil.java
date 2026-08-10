/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zombie.Lua.LuaManager
 *  zombie.core.BoxedStaticValues
 *  zombie.core.Core
 *  zombie.core.textures.Texture
 *  zombie.debug.DebugLog
 *  zombie.ui.UIManager
 */
package se.krka.kahlua.vm;

import java.io.IOException;
import java.io.InputStream;
import se.krka.kahlua.integration.expose.LuaJavaInvoker;
import se.krka.kahlua.integration.expose.MethodDebugInformation;
import se.krka.kahlua.vm.Coroutine;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;
import se.krka.kahlua.vm.Prototype;
import zombie.Lua.LuaManager;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.ui.UIManager;

public class KahluaUtil {
    private static final Object WORKER_THREAD_KEY = new Object();
    private static final String TYPE_NIL = "nil";
    private static final String TYPE_STRING = "string";
    private static final String TYPE_NUMBER = "number";
    private static final String TYPE_BOOLEAN = "boolean";
    private static final String TYPE_FUNCTION = "function";
    private static final String TYPE_TABLE = "table";
    private static final String TYPE_COROUTINE = "coroutine";
    private static final String TYPE_USERDATA = "userdata";

    public static double fromDouble(Object object) {
        return (Double)object;
    }

    public static Double toDouble(double d) {
        return BoxedStaticValues.toDouble((double)d);
    }

    public static Double toDouble(long l) {
        return BoxedStaticValues.toDouble((double)l);
    }

    public static Boolean toBoolean(boolean bl) {
        return bl ? Boolean.TRUE : Boolean.FALSE;
    }

    public static boolean boolEval(Object object) {
        return object != null && object != Boolean.FALSE;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static LuaClosure loadByteCodeFromResource(String string, KahluaTable kahluaTable) {
        try (InputStream inputStream = kahluaTable.getClass().getResourceAsStream(string + ".lbc");){
            if (inputStream == null) {
                LuaClosure luaClosure2 = null;
                return luaClosure2;
            }
            LuaClosure luaClosure = Prototype.loadByteCode(inputStream, kahluaTable);
            return luaClosure;
        }
        catch (IOException iOException) {
            throw new RuntimeException(iOException.getMessage());
        }
    }

    public static void luaAssert(boolean bl, String string) {
        if (!bl) {
            KahluaUtil.fail(string);
        }
    }

    public static void fail(String string) {
        if (Core.bDebug && UIManager.defaultthread == LuaManager.thread) {
            DebugLog.log((String)string);
            UIManager.debugBreakpoint((String)LuaManager.thread.currentfile, (long)(LuaManager.thread.currentLine - 1));
        }
        throw new RuntimeException(string);
    }

    public static double round(double d) {
        if (d < 0.0) {
            return -KahluaUtil.round(-d);
        }
        double d2 = Math.floor(d += 0.5);
        if (d2 == d) {
            return d2 - (double)((long)d2 & 1L);
        }
        return d2;
    }

    public static long ipow(long l, int n) {
        if (n <= 0) {
            return 1L;
        }
        long l2 = 1L;
        l2 = (n & 1) != 0 ? l : 1L;
        n >>= 1;
        while (n != 0) {
            l *= l;
            if ((n & 1) != 0) {
                l2 *= l;
            }
            n >>= 1;
        }
        return l2;
    }

    public static boolean isNegative(double d) {
        return Double.doubleToLongBits(d) < 0L;
    }

    public static KahluaTable getClassMetatables(Platform platform, KahluaTable kahluaTable) {
        return KahluaUtil.getOrCreateTable(platform, kahluaTable, "__classmetatables");
    }

    public static KahluaThread getWorkerThread(Platform platform, KahluaTable kahluaTable) {
        Object object = kahluaTable.rawget(WORKER_THREAD_KEY);
        if (object == null) {
            object = new KahluaThread(platform, kahluaTable);
            kahluaTable.rawset(WORKER_THREAD_KEY, object);
        }
        return (KahluaThread)object;
    }

    public static void setWorkerThread(KahluaTable kahluaTable, KahluaThread kahluaThread) {
        kahluaTable.rawset(WORKER_THREAD_KEY, (Object)kahluaThread);
    }

    public static KahluaTable getOrCreateTable(Platform platform, KahluaTable kahluaTable, String string) {
        Object object = kahluaTable.rawget(string);
        if (object == null || !(object instanceof KahluaTable)) {
            object = platform.newTable();
            kahluaTable.rawset(string, object);
        }
        return (KahluaTable)object;
    }

    public static void setupLibrary(KahluaTable kahluaTable, KahluaThread kahluaThread, String string) {
        LuaClosure luaClosure = KahluaUtil.loadByteCodeFromResource(string, kahluaTable);
        if (luaClosure == null) {
            KahluaUtil.fail("Could not load " + string + ".lbc");
        }
        kahluaThread.call(luaClosure, null, null, null);
    }

    public static String numberToString(Double d) {
        if (d.isNaN()) {
            return "nan";
        }
        if (d.isInfinite()) {
            if (KahluaUtil.isNegative(d)) {
                return "-inf";
            }
            return "inf";
        }
        double d2 = d;
        if (Math.floor(d2) == d2 && Math.abs(d2) < 1.0E14) {
            return String.valueOf(d.longValue());
        }
        return d.toString();
    }

    public static String type(Object object) {
        if (object == null) {
            return TYPE_NIL;
        }
        if (object instanceof String) {
            return TYPE_STRING;
        }
        if (object instanceof Double) {
            return TYPE_NUMBER;
        }
        if (object instanceof Boolean) {
            return TYPE_BOOLEAN;
        }
        if (object instanceof JavaFunction || object instanceof LuaClosure) {
            return TYPE_FUNCTION;
        }
        if (object instanceof KahluaTable) {
            return TYPE_TABLE;
        }
        if (object instanceof Coroutine) {
            return TYPE_COROUTINE;
        }
        return TYPE_USERDATA;
    }

    public static String tostring(Object object, KahluaThread kahluaThread) {
        Object object2;
        if (object == null) {
            return TYPE_NIL;
        }
        if (object instanceof String) {
            return (String)object;
        }
        if (object instanceof Double) {
            return KahluaUtil.rawTostring(object);
        }
        if (object instanceof Boolean) {
            return object == Boolean.TRUE ? "true" : "false";
        }
        if (object instanceof LuaClosure) {
            return "closure 0x" + System.identityHashCode(object);
        }
        if (object instanceof JavaFunction) {
            return "function 0x" + System.identityHashCode(object);
        }
        if (kahluaThread != null && (object2 = kahluaThread.getMetaOp(object, "__tostring")) != null) {
            String string = (String)kahluaThread.call(object2, object, null, null);
            return string;
        }
        return object.toString();
    }

    public static Double tonumber(String string) {
        return KahluaUtil.tonumber(string, 10);
    }

    public static Double tonumber(String string, int n) {
        if (n < 2 || n > 36) {
            throw new RuntimeException("base out of range");
        }
        try {
            if (n == 10) {
                return Double.valueOf(string);
            }
            return KahluaUtil.toDouble(Integer.parseInt(string, n));
        }
        catch (NumberFormatException numberFormatException) {
            string = string.toLowerCase();
            if (string.endsWith("nan")) {
                return KahluaUtil.toDouble(Double.NaN);
            }
            if (string.endsWith("inf")) {
                if (string.charAt(0) == '-') {
                    return KahluaUtil.toDouble(Double.NEGATIVE_INFINITY);
                }
                return KahluaUtil.toDouble(Double.POSITIVE_INFINITY);
            }
            return null;
        }
    }

    public static String rawTostring(Object object) {
        if (object instanceof String) {
            return (String)object;
        }
        if (object instanceof Double) {
            return KahluaUtil.numberToString((Double)object);
        }
        return null;
    }

    public static String rawTostring2(Object object) {
        if (object instanceof String) {
            return "\"" + (String)object + "\"";
        }
        if (object instanceof Texture) {
            return "Texture: \"" + ((Texture)object).getName() + "\"";
        }
        if (object instanceof Double) {
            return KahluaUtil.numberToString((Double)object);
        }
        if (object instanceof LuaClosure) {
            LuaClosure luaClosure = (LuaClosure)object;
            return luaClosure.toString2(0);
        }
        if (object instanceof LuaCallFrame) {
            LuaCallFrame luaCallFrame = (LuaCallFrame)object;
            return luaCallFrame.toString2();
        }
        if (object instanceof LuaJavaInvoker) {
            if (object.toString().equals("breakpoint")) {
                return null;
            }
            LuaJavaInvoker luaJavaInvoker = (LuaJavaInvoker)object;
            MethodDebugInformation methodDebugInformation = luaJavaInvoker.getMethodDebugData();
            Object object2 = "";
            for (int i = 0; i < methodDebugInformation.getParameters().size(); ++i) {
                if (methodDebugInformation.getParameters().get(i) == null) continue;
                object2 = (String)object2 + methodDebugInformation.getParameters().get(i);
            }
            return "Java: " + methodDebugInformation.getReturnType() + " " + object.toString() + "(" + (String)object2 + ")";
        }
        if (object != null) {
            return object.toString();
        }
        return null;
    }

    public static Double rawTonumber(Object object) {
        if (object instanceof Double) {
            return (Double)object;
        }
        if (object instanceof String) {
            return KahluaUtil.tonumber((String)object);
        }
        return null;
    }

    public static String getStringArg(LuaCallFrame luaCallFrame, int n, String string) {
        Object object = KahluaUtil.getArg(luaCallFrame, n, string);
        String string2 = KahluaUtil.rawTostring(object);
        if (string2 == null) {
            KahluaUtil.fail(n, string, TYPE_STRING, KahluaUtil.type(string2));
        }
        return string2;
    }

    public static String getOptionalStringArg(LuaCallFrame luaCallFrame, int n) {
        Object object = KahluaUtil.getOptionalArg(luaCallFrame, n);
        return KahluaUtil.rawTostring(object);
    }

    public static Double getNumberArg(LuaCallFrame luaCallFrame, int n, String string) {
        Object object = KahluaUtil.getArg(luaCallFrame, n, string);
        Double d = KahluaUtil.rawTonumber(object);
        if (d == null) {
            KahluaUtil.fail(n, string, "double", KahluaUtil.type(d));
        }
        return d;
    }

    public static Double getOptionalNumberArg(LuaCallFrame luaCallFrame, int n) {
        Object object = KahluaUtil.getOptionalArg(luaCallFrame, n);
        return KahluaUtil.rawTonumber(object);
    }

    private static void fail(int n, String string, String string2, String string3) {
        throw new RuntimeException("bad argument #" + n + " to '" + string + "' (" + string2 + " expected, got " + string3 + ")");
    }

    public static void assertArgNotNull(Object object, int n, String string, String string2) {
        if (object == null) {
            KahluaUtil.fail(n, string2, string, "null");
        }
    }

    public static Object getOptionalArg(LuaCallFrame luaCallFrame, int n) {
        int n2 = n - 1;
        int n3 = luaCallFrame.getTop();
        if (n2 >= n3) {
            return null;
        }
        return luaCallFrame.get(n - 1);
    }

    public static Object getArg(LuaCallFrame luaCallFrame, int n, String string) {
        Object object = KahluaUtil.getOptionalArg(luaCallFrame, n);
        if (object == null) {
            throw new RuntimeException("missing argument #" + n + " to '" + string + "'");
        }
        return object;
    }

    public static int len(KahluaTable kahluaTable, int n, int n2) {
        while (n < n2) {
            int n3 = n2 + n + 1 >> 1;
            Object object = kahluaTable.rawget(n3);
            if (object == null) {
                n2 = n3 - 1;
                continue;
            }
            n = n3;
        }
        while (kahluaTable.rawget(n + 1) != null) {
            ++n;
        }
        return n;
    }

    public static double getDoubleArg(LuaCallFrame luaCallFrame, int n, String string) {
        return KahluaUtil.getNumberArg(luaCallFrame, n, string);
    }
}

