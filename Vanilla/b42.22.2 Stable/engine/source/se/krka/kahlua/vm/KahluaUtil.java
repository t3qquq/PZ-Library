// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.vm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.jspecify.annotations.Nullable;
import se.krka.kahlua.integration.expose.LuaJavaInvoker;
import se.krka.kahlua.integration.expose.MethodDebugInformation;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import zombie.UsedFromLua;
import zombie.Lua.LuaManager;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.debug.DebugType;
import zombie.debug.LogSeverity;
import zombie.ui.UIManager;

@UsedFromLua
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

    public static double fromDouble(Object o) {
        return (Double)o;
    }

    public static Double toDouble(double d) {
        return BoxedStaticValues.toDouble(d);
    }

    public static Double toDouble(long d) {
        return BoxedStaticValues.toDouble(d);
    }

    public static Boolean toBoolean(boolean b) {
        return b ? Boolean.TRUE : Boolean.FALSE;
    }

    public static boolean boolEval(Object o) {
        return o != null && o != Boolean.FALSE;
    }

    public static @Nullable LuaClosure loadByteCodeFromFile(File file, KahluaTable environment) {
        try (InputStream stream = new FileInputStream(file)) {
            return getLuaClosure(environment, stream);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static @Nullable LuaClosure loadByteCodeFromResource(String name, KahluaTable environment) {
        try (InputStream stream = environment.getClass().getResourceAsStream(name + ".lbc")) {
            return getLuaClosure(environment, stream);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static @Nullable LuaClosure getLuaClosure(KahluaTable environment, InputStream stream) throws IOException {
        return stream == null ? null : Prototype.loadByteCode(stream, environment);
    }

    public static void luaAssert(boolean b, String msg) {
        if (!b) {
            fail(msg);
        }
    }

    public static void fail(String msg) {
        if (Core.debug && UIManager.defaultthread == LuaManager.thread) {
            DebugType.Lua.printStackTrace(LogSeverity.Error, -1, "Lua fail. Message: %s", new Object[]{msg});
            UIManager.debugBreakpoint(LuaManager.thread.currentfile, LuaManager.thread.currentLine - 1);
        }

        throw new RuntimeException(msg);
    }

    public static double round(double x) {
        if (x < 0.0) {
            return -round(-x);
        }

        x += 0.5;
        double x2 = Math.floor(x);
        return x2 == x ? x2 - ((long)x2 & 1L) : x2;
    }

    public static long ipow(long base, int exponent) {
        if (exponent <= 0) {
            return 1L;
        }

        long b = 1L;
        b = (exponent & 1) != 0 ? base : 1L;

        for (int var5 = exponent >> 1; var5 != 0; var5 >>= 1) {
            base *= base;
            if ((var5 & 1) != 0) {
                b *= base;
            }
        }

        return b;
    }

    public static boolean isNegative(double vDouble) {
        return Double.doubleToLongBits(vDouble) < 0L;
    }

    public static KahluaTable getClassMetatables(Platform platform, KahluaTable env) {
        return getOrCreateTable(platform, env, "__classmetatables");
    }

    public static KahluaThread getWorkerThread(Platform platform, KahluaTable env) {
        Object workerThread = env.rawget(WORKER_THREAD_KEY);
        if (workerThread == null) {
            workerThread = new KahluaThread(platform, env);
            env.rawset(WORKER_THREAD_KEY, workerThread);
        }

        return (KahluaThread)workerThread;
    }

    public static void setWorkerThread(KahluaTable env, KahluaThread thread) {
        env.rawset(WORKER_THREAD_KEY, thread);
    }

    public static KahluaTable getOrCreateTable(Platform platform, KahluaTable env, String name) {
        Object t = env.rawget(name);
        if (t == null || !(t instanceof KahluaTable)) {
            t = platform.newTable();
            env.rawset(name, t);
        }

        return (KahluaTable)t;
    }

    public static void setupLibrary(KahluaTable env, KahluaThread workerThread, File library) {
        LuaClosure closure = loadByteCodeFromFile(library, env);
        if (closure == null) {
            fail("Could not load " + library);
        }

        workerThread.call(closure, null, null, null);
    }

    public static void setupLibraryText(KahluaTable env, KahluaThread workerThread, File library) {
        try (FileInputStream fis = new FileInputStream(library)) {
            LuaClosure closure = LuaCompiler.loadis(fis, "stdlib.lua", env);
            workerThread.call(closure, null, null, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String numberToString(Double num) {
        if (num.isNaN()) {
            return "nan";
        }

        if (num.isInfinite()) {
            return isNegative(num) ? "-inf" : "inf";
        }

        double n = num;
        return Math.floor(n) == n && Math.abs(n) < 1.0E14 ? String.valueOf(num.longValue()) : num.toString();
    }

    public static String type(Object o) {
        if (o == null) {
            return "nil";
        } else if (o instanceof String) {
            return "string";
        } else if (o instanceof Double) {
            return "number";
        } else if (o instanceof Boolean) {
            return "boolean";
        } else if (o instanceof JavaFunction || o instanceof LuaClosure) {
            return "function";
        } else if (o instanceof KahluaTable) {
            return "table";
        } else {
            return o instanceof Coroutine ? "coroutine" : "userdata";
        }
    }

    public static boolean isUserdata(Object o) {
        return type(o) == "userdata";
    }

    public static boolean isTable(Object o) {
        return type(o) == "table";
    }

    public static boolean isFunction(Object o) {
        return type(o) == "function";
    }

    public static String tostring(Object o, KahluaThread thread) {
        if (o == null) {
            return "nil";
        } else if (o instanceof String s) {
            return s;
        } else {
            if (o instanceof Double) {
                return rawTostring(o);
            }

            if (o instanceof Boolean) {
                return o == Boolean.TRUE ? "true" : "false";
            }

            if (o instanceof LuaClosure) {
                return "closure 0x" + System.identityHashCode(o);
            }

            if (o instanceof JavaFunction) {
                return "function 0x" + System.identityHashCode(o);
            }

            if (thread != null) {
                if (o instanceof KahluaTable) {
                    Object toStringMember = thread.tableget(o, "tostring");
                    if (type(toStringMember) == "function") {
                        return String.valueOf(thread.call(toStringMember, o, null, null));
                    }
                }

                Object tostringFun = thread.getMetaOp(o, "__tostring");
                if (tostringFun != null) {
                    return String.valueOf(thread.call(tostringFun, o, null, null));
                }

                if (o instanceof KahluaTable table) {
                    StringBuilder toStringBuilder = new StringBuilder();
                    toStringBuilder.append(tableToString(table, thread));
                    toStringBuilder.append("{ ");
                    boolean anyFound = false;
                    KahluaTableIterator it = table.iterator();

                    while (it.advance()) {
                        Object key = it.getKey();
                        Object value = it.getValue();
                        if (!anyFound) {
                            anyFound = true;
                        } else {
                            toStringBuilder.append(", ");
                        }

                        toStringBuilder.append(isTable(key) ? tableToString(key, thread) : tostring(key, thread));
                        toStringBuilder.append("=");
                        toStringBuilder.append(isTable(value) ? tableToString(value, thread) : tostring(value, thread));
                    }

                    toStringBuilder.append(" }");
                    return toStringBuilder.toString();
                }
            }

            return o.toString();
        }
    }

    private static String tableToString(Object o, KahluaThread thread) {
        return thread.tableget(o, "Type") instanceof String type ? type + " 0x" + System.identityHashCode(o) : type(o) + " 0x" + System.identityHashCode(o);
    }

    public static String identityHashCode(Object o) {
        return Integer.toHexString(System.identityHashCode(o));
    }

    public static Double tonumber(String s) {
        return tonumber(s, 10);
    }

    public static Double tonumber(String s, int radix) {
        if (radix >= 2 && radix <= 36) {
            try {
                return radix == 10 ? BoxedStaticValues.toDouble(Double.parseDouble(s)) : toDouble(Integer.parseInt(s, radix));
            } catch (NumberFormatException e) {
                s = s.toLowerCase();
                if (s.endsWith("nan")) {
                    return toDouble(Double.NaN);
                } else if (s.endsWith("inf")) {
                    return s.charAt(0) == '-' ? toDouble(Double.NEGATIVE_INFINITY) : toDouble(Double.POSITIVE_INFINITY);
                } else {
                    return null;
                }
            }
        } else {
            throw new RuntimeException("base out of range");
        }
    }

    public static String rawTostring(Object o) {
        if (o instanceof String s) {
            return s;
        } else {
            return o instanceof Double d ? numberToString(d) : null;
        }
    }

    public static String rawTostring2(Object o) {
        if (o instanceof String) {
            return "\"" + o + "\"";
        } else if (o instanceof Texture texture) {
            return "Texture: \"" + texture.getName() + "\"";
        } else if (o instanceof Double d) {
            return numberToString(d);
        } else if (o instanceof LuaClosure closure) {
            return closure.toString2(0);
        } else if (o instanceof LuaCallFrame callFrame) {
            return callFrame.toString2();
        } else if (o instanceof LuaJavaInvoker invoker) {
            if (o.toString().equals("breakpoint")) {
                return null;
            }

            MethodDebugInformation ooo = invoker.getMethodDebugData();
            String params = "";

            for (int n = 0; n < ooo.getParameters().size(); n++) {
                if (ooo.getParameters().get(n) != null) {
                    params = params + ooo.getParameters().get(n);
                }
            }

            return "Java: " + ooo.getReturnType() + " " + o.toString() + "(" + params + ")";
        } else {
            return o != null ? o.toString() : null;
        }
    }

    public static StackTraceElement rawToStackTraceElement(Object o) {
        if (o instanceof LuaClosure closure) {
            return closure.toStackTraceElement(0);
        } else if (o instanceof LuaCallFrame callFrame) {
            return callFrame.toStackTraceElement();
        } else if (o instanceof LuaJavaInvoker invoker) {
            if (o.toString().equals("breakpoint")) {
                return null;
            }

            MethodDebugInformation ooo = invoker.getMethodDebugData();
            StringBuilder params = new StringBuilder();

            for (int n = 0; n < ooo.getParameters().size(); n++) {
                if (ooo.getParameters().get(n) != null) {
                    params.append(ooo.getParameters().get(n));
                }
            }

            return new StackTraceElement("LuaJavaInvoker", ooo.getReturnType() + " " + o.toString() + "(" + params + ")", "(LuaJavaInvoker)", -1);
        } else {
            return null;
        }
    }

    public static Double rawTonumber(Object o) {
        if (o instanceof Double d) {
            return d;
        } else {
            return o instanceof String s ? tonumber(s) : null;
        }
    }

    public static String getStringArg(LuaCallFrame callFrame, int n, String function) {
        Object o = getArg(callFrame, n, function);
        String res = rawTostring(o);
        if (res == null) {
            fail(n, function, "string", type(res));
        }

        return res;
    }

    public static String getOptionalStringArg(LuaCallFrame callFrame, int n) {
        Object o = getOptionalArg(callFrame, n);
        return rawTostring(o);
    }

    public static Double getNumberArg(LuaCallFrame callFrame, int n, String function) {
        Object o = getArg(callFrame, n, function);
        Double res = rawTonumber(o);
        if (res == null) {
            fail(n, function, "double", type(res));
        }

        return res;
    }

    public static Double getOptionalNumberArg(LuaCallFrame callFrame, int n) {
        Object o = getOptionalArg(callFrame, n);
        return rawTonumber(o);
    }

    private static void fail(int n, String function, String wantedType, String gotten) {
        throw new RuntimeException("bad argument #" + n + " to '" + function + "' (" + wantedType + " expected, got " + gotten + ")");
    }

    public static void assertArgNotNull(Object o, int n, String type, String function) {
        if (o == null) {
            fail(n, function, type, "null");
        }
    }

    public static Object getOptionalArg(LuaCallFrame callFrame, int n) {
        int top = callFrame.getTop();
        int index = n - 1;
        return index >= top ? null : callFrame.get(n - 1);
    }

    public static Object getArg(LuaCallFrame callFrame, int n, String function) {
        Object res = getOptionalArg(callFrame, n);
        if (res == null) {
            throw new RuntimeException("missing argument #" + n + " to '" + function + "'");
        } else {
            return res;
        }
    }

    public static int len(KahluaTable kahluaTable, int low, int high) {
        while (low < high) {
            int middle = high + low + 1 >> 1;
            Object value = kahluaTable.rawget(middle);
            if (value == null) {
                high = middle - 1;
            } else {
                low = middle;
            }
        }

        while (kahluaTable.rawget(low + 1) != null) {
            low++;
        }

        return low;
    }

    public static double getDoubleArg(LuaCallFrame callFrame, int i, String name) {
        return getNumberArg(callFrame, i, name);
    }
}
