// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.vm;

import java.io.IOException;
import java.io.InputStream;
import se.krka.kahlua.integration.expose.LuaJavaInvoker;
import se.krka.kahlua.integration.expose.MethodDebugInformation;
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

    public static double fromDouble(Object arg0) {
        return (Double)arg0;
    }

    public static Double toDouble(double arg0) {
        return BoxedStaticValues.toDouble(arg0);
    }

    public static Double toDouble(long arg0) {
        return BoxedStaticValues.toDouble(arg0);
    }

    public static Boolean toBoolean(boolean arg0) {
        return arg0 ? Boolean.TRUE : Boolean.FALSE;
    }

    public static boolean boolEval(Object arg0) {
        return arg0 != null && arg0 != Boolean.FALSE;
    }

    public static LuaClosure loadByteCodeFromResource(String arg0, KahluaTable arg1) {
        try {
            LuaClosure luaClosure;
            try (InputStream inputStream = arg1.getClass().getResourceAsStream(arg0 + ".lbc")) {
                if (inputStream == null) {
                    return null;
                }

                luaClosure = Prototype.loadByteCode(inputStream, arg1);
            }

            return luaClosure;
        } catch (IOException iOException) {
            throw new RuntimeException(iOException.getMessage());
        }
    }

    public static void luaAssert(boolean arg0, String arg1) {
        if (!arg0) {
            fail(arg1);
        }
    }

    public static void fail(String arg0) {
        if (Core.bDebug && UIManager.defaultthread == LuaManager.thread) {
            DebugLog.log(arg0);
            UIManager.debugBreakpoint(LuaManager.thread.currentfile, LuaManager.thread.currentLine - 1);
        }

        throw new RuntimeException(arg0);
    }

    public static double round(double arg0) {
        if (arg0 < 0.0) {
            return -round(-arg0);
        } else {
            arg0 += 0.5;
            double double0 = Math.floor(arg0);
            return double0 == arg0 ? double0 - ((long)double0 & 1L) : double0;
        }
    }

    public static long ipow(long arg0, int arg1) {
        if (arg1 <= 0) {
            return 1L;
        } else {
            long long0 = 1L;
            long0 = (arg1 & 1) != 0 ? arg0 : 1L;

            for (int int0 = arg1 >> 1; int0 != 0; int0 >>= 1) {
                arg0 *= arg0;
                if ((int0 & 1) != 0) {
                    long0 *= arg0;
                }
            }

            return long0;
        }
    }

    public static boolean isNegative(double arg0) {
        return Double.doubleToLongBits(arg0) < 0L;
    }

    public static KahluaTable getClassMetatables(Platform arg0, KahluaTable arg1) {
        return getOrCreateTable(arg0, arg1, "__classmetatables");
    }

    public static KahluaThread getWorkerThread(Platform arg0, KahluaTable arg1) {
        Object object = arg1.rawget(WORKER_THREAD_KEY);
        if (object == null) {
            object = new KahluaThread(arg0, arg1);
            arg1.rawset(WORKER_THREAD_KEY, object);
        }

        return (KahluaThread)object;
    }

    public static void setWorkerThread(KahluaTable arg0, KahluaThread arg1) {
        arg0.rawset(WORKER_THREAD_KEY, arg1);
    }

    public static KahluaTable getOrCreateTable(Platform arg0, KahluaTable arg1, String arg2) {
        Object object = arg1.rawget(arg2);
        if (object == null || !(object instanceof KahluaTable)) {
            object = arg0.newTable();
            arg1.rawset(arg2, object);
        }

        return (KahluaTable)object;
    }

    public static void setupLibrary(KahluaTable table, KahluaThread kahluaThread, String string) {
        LuaClosure luaClosure = loadByteCodeFromResource(string, table);
        if (luaClosure == null) {
            fail("Could not load " + string + ".lbc");
        }

        kahluaThread.call(luaClosure, null, null, null);
    }

    public static String numberToString(Double arg0) {
        if (arg0.isNaN()) {
            return "nan";
        } else if (arg0.isInfinite()) {
            return isNegative(arg0) ? "-inf" : "inf";
        } else {
            double double0 = arg0;
            return Math.floor(double0) == double0 && Math.abs(double0) < 1.0E14 ? String.valueOf(arg0.longValue()) : arg0.toString();
        }
    }

    public static String type(Object arg0) {
        if (arg0 == null) {
            return "nil";
        } else if (arg0 instanceof String) {
            return "string";
        } else if (arg0 instanceof Double) {
            return "number";
        } else if (arg0 instanceof Boolean) {
            return "boolean";
        } else if (arg0 instanceof JavaFunction || arg0 instanceof LuaClosure) {
            return "function";
        } else if (arg0 instanceof KahluaTable) {
            return "table";
        } else {
            return arg0 instanceof Coroutine ? "coroutine" : "userdata";
        }
    }

    public static String tostring(Object arg0, KahluaThread arg1) {
        if (arg0 == null) {
            return "nil";
        } else if (arg0 instanceof String) {
            return (String)arg0;
        } else if (arg0 instanceof Double) {
            return rawTostring(arg0);
        } else if (arg0 instanceof Boolean) {
            return arg0 == Boolean.TRUE ? "true" : "false";
        } else if (arg0 instanceof LuaClosure) {
            return "closure 0x" + System.identityHashCode(arg0);
        } else if (arg0 instanceof JavaFunction) {
            return "function 0x" + System.identityHashCode(arg0);
        } else {
            if (arg1 != null) {
                Object object = arg1.getMetaOp(arg0, "__tostring");
                if (object != null) {
                    return (String)arg1.call(object, arg0, null, null);
                }
            }

            return arg0.toString();
        }
    }

    public static Double tonumber(String arg0) {
        return tonumber(arg0, 10);
    }

    public static Double tonumber(String arg0, int arg1) {
        if (arg1 >= 2 && arg1 <= 36) {
            try {
                return arg1 == 10 ? Double.valueOf(arg0) : toDouble((long)Integer.parseInt(arg0, arg1));
            } catch (NumberFormatException numberFormatException) {
                arg0 = arg0.toLowerCase();
                if (arg0.endsWith("nan")) {
                    return toDouble(Double.NaN);
                } else if (arg0.endsWith("inf")) {
                    return arg0.charAt(0) == '-' ? toDouble(Double.NEGATIVE_INFINITY) : toDouble(Double.POSITIVE_INFINITY);
                } else {
                    return null;
                }
            }
        } else {
            throw new RuntimeException("base out of range");
        }
    }

    public static String rawTostring(Object arg0) {
        if (arg0 instanceof String) {
            return (String)arg0;
        } else {
            return arg0 instanceof Double ? numberToString((Double)arg0) : null;
        }
    }

    public static String rawTostring2(Object arg0) {
        if (arg0 instanceof String) {
            return "\"" + (String)arg0 + "\"";
        } else if (arg0 instanceof Texture) {
            return "Texture: \"" + ((Texture)arg0).getName() + "\"";
        } else if (arg0 instanceof Double) {
            return numberToString((Double)arg0);
        } else if (arg0 instanceof LuaClosure luaClosure) {
            return luaClosure.toString2(0);
        } else if (arg0 instanceof LuaCallFrame luaCallFrame) {
            return luaCallFrame.toString2();
        } else if (arg0 instanceof LuaJavaInvoker) {
            if (arg0.toString().equals("breakpoint")) {
                return null;
            } else {
                LuaJavaInvoker luaJavaInvoker = (LuaJavaInvoker)arg0;
                MethodDebugInformation methodDebugInformation = luaJavaInvoker.getMethodDebugData();
                Object object = "";

                for (int int0 = 0; int0 < methodDebugInformation.getParameters().size(); int0++) {
                    if (methodDebugInformation.getParameters().get(int0) != null) {
                        object = object + methodDebugInformation.getParameters().get(int0);
                    }
                }

                return "Java: " + methodDebugInformation.getReturnType() + " " + arg0.toString() + "(" + object + ")";
            }
        } else {
            return arg0 != null ? arg0.toString() : null;
        }
    }

    public static Double rawTonumber(Object arg0) {
        if (arg0 instanceof Double) {
            return (Double)arg0;
        } else {
            return arg0 instanceof String ? tonumber((String)arg0) : null;
        }
    }

    public static String getStringArg(LuaCallFrame arg0, int arg1, String arg2) {
        Object object = getArg(arg0, arg1, arg2);
        String string = rawTostring(object);
        if (string == null) {
            fail(arg1, arg2, "string", type(string));
        }

        return string;
    }

    public static String getOptionalStringArg(LuaCallFrame arg0, int arg1) {
        Object object = getOptionalArg(arg0, arg1);
        return rawTostring(object);
    }

    public static Double getNumberArg(LuaCallFrame arg0, int arg1, String arg2) {
        Object object = getArg(arg0, arg1, arg2);
        Double double0 = rawTonumber(object);
        if (double0 == null) {
            fail(arg1, arg2, "double", type(double0));
        }

        return double0;
    }

    public static Double getOptionalNumberArg(LuaCallFrame arg0, int arg1) {
        Object object = getOptionalArg(arg0, arg1);
        return rawTonumber(object);
    }

    private static void fail(int int0, String string2, String string1, String string0) {
        throw new RuntimeException("bad argument #" + int0 + " to '" + string2 + "' (" + string1 + " expected, got " + string0 + ")");
    }

    public static void assertArgNotNull(Object arg0, int arg1, String arg2, String arg3) {
        if (arg0 == null) {
            fail(arg1, arg3, arg2, "null");
        }
    }

    public static Object getOptionalArg(LuaCallFrame arg0, int arg1) {
        int int0 = arg0.getTop();
        int int1 = arg1 - 1;
        return int1 >= int0 ? null : arg0.get(arg1 - 1);
    }

    public static Object getArg(LuaCallFrame arg0, int arg1, String arg2) {
        Object object = getOptionalArg(arg0, arg1);
        if (object == null) {
            throw new RuntimeException("missing argument #" + arg1 + " to '" + arg2 + "'");
        } else {
            return object;
        }
    }

    public static int len(KahluaTable arg0, int arg1, int arg2) {
        while (arg1 < arg2) {
            int int0 = arg2 + arg1 + 1 >> 1;
            Object object = arg0.rawget(int0);
            if (object == null) {
                arg2 = int0 - 1;
            } else {
                arg1 = int0;
            }
        }

        while (arg0.rawget(arg1 + 1) != null) {
            arg1++;
        }

        return arg1;
    }

    public static double getDoubleArg(LuaCallFrame arg0, int arg1, String arg2) {
        return getNumberArg(arg0, arg1, arg2);
    }
}
