/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.stdlib;

import se.krka.kahlua.vm.Coroutine;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;

public class CoroutineLib
implements JavaFunction {
    private static final int CREATE = 0;
    private static final int RESUME = 1;
    private static final int YIELD = 2;
    private static final int STATUS = 3;
    private static final int RUNNING = 4;
    private static final int NUM_FUNCTIONS = 5;
    private static final String[] names;
    private static final Class COROUTINE_CLASS;
    private final int index;
    private static final CoroutineLib[] functions;

    public String toString() {
        return "coroutine." + names[this.index];
    }

    public CoroutineLib(int n) {
        this.index = n;
    }

    public static void register(Platform platform, KahluaTable kahluaTable) {
        KahluaTable kahluaTable2 = platform.newTable();
        for (int i = 0; i < 5; ++i) {
            kahluaTable2.rawset(names[i], (Object)functions[i]);
        }
        kahluaTable2.rawset("__index", (Object)kahluaTable2);
        KahluaTable kahluaTable3 = KahluaUtil.getClassMetatables(platform, kahluaTable);
        kahluaTable3.rawset(COROUTINE_CLASS, (Object)kahluaTable2);
        kahluaTable.rawset("coroutine", (Object)kahluaTable2);
    }

    @Override
    public int call(LuaCallFrame luaCallFrame, int n) {
        switch (this.index) {
            case 0: {
                return this.create(luaCallFrame, n);
            }
            case 2: {
                return CoroutineLib.yield_int(luaCallFrame, n);
            }
            case 1: {
                return this.resume(luaCallFrame, n);
            }
            case 3: {
                return this.status(luaCallFrame, n);
            }
            case 4: {
                return this.running(luaCallFrame, n);
            }
        }
        return 0;
    }

    private int running(LuaCallFrame luaCallFrame, int n) {
        Coroutine coroutine = luaCallFrame.coroutine;
        if (coroutine.getStatus() != "normal") {
            coroutine = null;
        }
        return luaCallFrame.push(coroutine);
    }

    private int status(LuaCallFrame luaCallFrame, int n) {
        Coroutine coroutine = this.getCoroutine(luaCallFrame, "status");
        if (luaCallFrame.coroutine == coroutine) {
            return luaCallFrame.push("running");
        }
        return luaCallFrame.push(coroutine.getStatus());
    }

    private int resume(LuaCallFrame luaCallFrame, int n) {
        Coroutine coroutine = this.getCoroutine(luaCallFrame, "resume");
        String string = coroutine.getStatus();
        if (string != "suspended") {
            KahluaUtil.fail("Can not resume coroutine that is in status: " + string);
        }
        Coroutine coroutine2 = luaCallFrame.coroutine;
        coroutine.resume(coroutine2);
        LuaCallFrame luaCallFrame2 = coroutine.currentCallFrame();
        if (luaCallFrame2.nArguments == -1) {
            luaCallFrame2.setTop(0);
        }
        for (int i = 1; i < n; ++i) {
            luaCallFrame2.push(luaCallFrame.get(i));
        }
        if (luaCallFrame2.nArguments == -1) {
            luaCallFrame2.nArguments = n - 1;
            luaCallFrame2.init();
        }
        luaCallFrame.getThread().currentCoroutine = coroutine;
        return 0;
    }

    private static int yield_int(LuaCallFrame luaCallFrame, int n) {
        Coroutine coroutine = luaCallFrame.coroutine;
        Coroutine coroutine2 = coroutine.getParent();
        KahluaUtil.luaAssert(coroutine2 != null, "Can not yield outside of a coroutine");
        LuaCallFrame luaCallFrame2 = coroutine.getCallFrame(-2);
        Coroutine.yieldHelper(luaCallFrame2, luaCallFrame, n);
        return 0;
    }

    private int create(LuaCallFrame luaCallFrame, int n) {
        LuaClosure luaClosure = this.getFunction(luaCallFrame, "create");
        Coroutine coroutine = new Coroutine(luaCallFrame.getPlatform(), luaCallFrame.getEnvironment());
        coroutine.pushNewCallFrame(luaClosure, null, 0, 0, -1, true, true);
        luaCallFrame.push(coroutine);
        return 1;
    }

    private LuaClosure getFunction(LuaCallFrame luaCallFrame, String string) {
        Object object = KahluaUtil.getArg(luaCallFrame, 1, string);
        KahluaUtil.luaAssert(object instanceof LuaClosure, "argument must be a lua function");
        LuaClosure luaClosure = (LuaClosure)object;
        return luaClosure;
    }

    private Coroutine getCoroutine(LuaCallFrame luaCallFrame, String string) {
        Object object = KahluaUtil.getArg(luaCallFrame, 1, string);
        KahluaUtil.luaAssert(object instanceof Coroutine, "argument must be a coroutine");
        Coroutine coroutine = (Coroutine)object;
        return coroutine;
    }

    static {
        COROUTINE_CLASS = new Coroutine().getClass();
        names = new String[5];
        CoroutineLib.names[0] = "create";
        CoroutineLib.names[1] = "resume";
        CoroutineLib.names[2] = "yield";
        CoroutineLib.names[3] = "status";
        CoroutineLib.names[4] = "running";
        functions = new CoroutineLib[5];
        for (int i = 0; i < 5; ++i) {
            CoroutineLib.functions[i] = new CoroutineLib(i);
        }
    }
}

