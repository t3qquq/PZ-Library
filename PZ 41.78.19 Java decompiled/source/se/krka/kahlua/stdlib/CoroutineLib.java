// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.stdlib;

import se.krka.kahlua.vm.Coroutine;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;

public class CoroutineLib implements JavaFunction {
    private static final int CREATE = 0;
    private static final int RESUME = 1;
    private static final int YIELD = 2;
    private static final int STATUS = 3;
    private static final int RUNNING = 4;
    private static final int NUM_FUNCTIONS = 5;
    private static final String[] names = new String[5];
    private static final Class COROUTINE_CLASS = new Coroutine().getClass();
    private final int index;
    private static final CoroutineLib[] functions = new CoroutineLib[5];

    @Override
    public String toString() {
        return "coroutine." + names[this.index];
    }

    public CoroutineLib(int int0) {
        this.index = int0;
    }

    public static void register(Platform platform, KahluaTable table2) {
        KahluaTable table0 = platform.newTable();

        for (int int0 = 0; int0 < 5; int0++) {
            table0.rawset(names[int0], functions[int0]);
        }

        table0.rawset("__index", table0);
        KahluaTable table1 = KahluaUtil.getClassMetatables(platform, table2);
        table1.rawset(COROUTINE_CLASS, table0);
        table2.rawset("coroutine", table0);
    }

    @Override
    public int call(LuaCallFrame luaCallFrame, int int0) {
        switch (this.index) {
            case 0:
                return this.create(luaCallFrame, int0);
            case 1:
                return this.resume(luaCallFrame, int0);
            case 2:
                return yield_int(luaCallFrame, int0);
            case 3:
                return this.status(luaCallFrame, int0);
            case 4:
                return this.running(luaCallFrame, int0);
            default:
                return 0;
        }
    }

    private int running(LuaCallFrame luaCallFrame, int var2) {
        Coroutine coroutine = luaCallFrame.coroutine;
        if (coroutine.getStatus() != "normal") {
            coroutine = null;
        }

        return luaCallFrame.push(coroutine);
    }

    private int status(LuaCallFrame luaCallFrame, int var2) {
        Coroutine coroutine = this.getCoroutine(luaCallFrame, "status");
        return luaCallFrame.coroutine == coroutine ? luaCallFrame.push("running") : luaCallFrame.push(coroutine.getStatus());
    }

    private int resume(LuaCallFrame luaCallFrame0, int int1) {
        Coroutine coroutine0 = this.getCoroutine(luaCallFrame0, "resume");
        String string = coroutine0.getStatus();
        if (string != "suspended") {
            KahluaUtil.fail("Can not resume coroutine that is in status: " + string);
        }

        Coroutine coroutine1 = luaCallFrame0.coroutine;
        coroutine0.resume(coroutine1);
        LuaCallFrame luaCallFrame1 = coroutine0.currentCallFrame();
        if (luaCallFrame1.nArguments == -1) {
            luaCallFrame1.setTop(0);
        }

        for (int int0 = 1; int0 < int1; int0++) {
            luaCallFrame1.push(luaCallFrame0.get(int0));
        }

        if (luaCallFrame1.nArguments == -1) {
            luaCallFrame1.nArguments = int1 - 1;
            luaCallFrame1.init();
        }

        luaCallFrame0.getThread().currentCoroutine = coroutine0;
        return 0;
    }

    private static int yield_int(LuaCallFrame luaCallFrame0, int int0) {
        Coroutine coroutine0 = luaCallFrame0.coroutine;
        Coroutine coroutine1 = coroutine0.getParent();
        KahluaUtil.luaAssert(coroutine1 != null, "Can not yield outside of a coroutine");
        LuaCallFrame luaCallFrame1 = coroutine0.getCallFrame(-2);
        Coroutine.yieldHelper(luaCallFrame1, luaCallFrame0, int0);
        return 0;
    }

    private int create(LuaCallFrame luaCallFrame, int var2) {
        LuaClosure luaClosure = this.getFunction(luaCallFrame, "create");
        Coroutine coroutine = new Coroutine(luaCallFrame.getPlatform(), luaCallFrame.getEnvironment());
        coroutine.pushNewCallFrame(luaClosure, null, 0, 0, -1, true, true);
        luaCallFrame.push(coroutine);
        return 1;
    }

    private LuaClosure getFunction(LuaCallFrame luaCallFrame, String string) {
        Object object = KahluaUtil.getArg(luaCallFrame, 1, string);
        KahluaUtil.luaAssert(object instanceof LuaClosure, "argument must be a lua function");
        return (LuaClosure)object;
    }

    private Coroutine getCoroutine(LuaCallFrame luaCallFrame, String string) {
        Object object = KahluaUtil.getArg(luaCallFrame, 1, string);
        KahluaUtil.luaAssert(object instanceof Coroutine, "argument must be a coroutine");
        return (Coroutine)object;
    }

    static {
        names[0] = "create";
        names[1] = "resume";
        names[2] = "yield";
        names[3] = "status";
        names[4] = "running";

        for (int int0 = 0; int0 < 5; int0++) {
            functions[int0] = new CoroutineLib(int0);
        }
    }
}
