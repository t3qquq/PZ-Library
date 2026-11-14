// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
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
    private static final Class<?> COROUTINE_CLASS = new Coroutine().getClass();
    private final int index;
    private static final CoroutineLib[] functions = new CoroutineLib[5];

    @Override
    public String toString() {
        return "coroutine." + names[this.index];
    }

    public CoroutineLib(int index) {
        this.index = index;
    }

    public static void register(Platform platform, KahluaTable env) {
        KahluaTable coroutine = platform.newTable();

        for (int i = 0; i < 5; i++) {
            coroutine.rawset(names[i], functions[i]);
        }

        coroutine.rawset("__index", coroutine);
        KahluaTable metatables = KahluaUtil.getClassMetatables(platform, env);
        metatables.rawset(COROUTINE_CLASS, coroutine);
        env.rawset("coroutine", coroutine);
    }

    @Override
    public int call(LuaCallFrame callFrame, int nArguments) {
        switch (this.index) {
            case 0:
                return this.create(callFrame, nArguments);
            case 1:
                return this.resume(callFrame, nArguments);
            case 2:
                return yield_int(callFrame, nArguments);
            case 3:
                return this.status(callFrame, nArguments);
            case 4:
                return this.running(callFrame, nArguments);
            default:
                return 0;
        }
    }

    private int running(LuaCallFrame callFrame, int nArguments) {
        Coroutine t = callFrame.coroutine;
        if (t.getStatus() != "normal") {
            t = null;
        }

        return callFrame.push(t);
    }

    private int status(LuaCallFrame callFrame, int nArguments) {
        Coroutine t = this.getCoroutine(callFrame, "status");
        return callFrame.coroutine == t ? callFrame.push("running") : callFrame.push(t.getStatus());
    }

    private int resume(LuaCallFrame callFrame, int nArguments) {
        Coroutine t = this.getCoroutine(callFrame, "resume");
        String status = t.getStatus();
        if (status != "suspended") {
            KahluaUtil.fail("Can not resume coroutine that is in status: " + status);
        }

        Coroutine parent = callFrame.coroutine;
        t.resume(parent);
        LuaCallFrame nextCallFrame = t.currentCallFrame();
        if (nextCallFrame.nArguments == -1) {
            nextCallFrame.setTop(0);
        }

        for (int i = 1; i < nArguments; i++) {
            nextCallFrame.push(callFrame.get(i));
        }

        if (nextCallFrame.nArguments == -1) {
            nextCallFrame.nArguments = nArguments - 1;
            nextCallFrame.init();
        }

        callFrame.getThread().currentCoroutine = t;
        return 0;
    }

    private static int yield_int(LuaCallFrame callFrame, int nArguments) {
        Coroutine t = callFrame.coroutine;
        Coroutine parent = t.getParent();
        KahluaUtil.luaAssert(parent != null, "Can not yield outside of a coroutine");
        LuaCallFrame realCallFrame = t.getCallFrame(-2);
        Coroutine.yieldHelper(realCallFrame, callFrame, nArguments);
        return 0;
    }

    private int create(LuaCallFrame callFrame, int nArguments) {
        LuaClosure c = this.getFunction(callFrame, "create");
        Coroutine coroutine = new Coroutine(callFrame.getPlatform(), callFrame.getEnvironment());
        coroutine.pushNewCallFrame(c, null, 0, 0, -1, true, true);
        callFrame.push(coroutine);
        return 1;
    }

    private LuaClosure getFunction(LuaCallFrame callFrame, String name) {
        Object o = KahluaUtil.getArg(callFrame, 1, name);
        KahluaUtil.luaAssert(o instanceof LuaClosure, "argument must be a lua function");
        return (LuaClosure)o;
    }

    private Coroutine getCoroutine(LuaCallFrame callFrame, String name) {
        Object o = KahluaUtil.getArg(callFrame, 1, name);
        KahluaUtil.luaAssert(o instanceof Coroutine, "argument must be a coroutine");
        return (Coroutine)o;
    }

    static {
        names[0] = "create";
        names[1] = "resume";
        names[2] = "yield";
        names[3] = "status";
        names[4] = "running";

        for (int i = 0; i < 5; i++) {
            functions[i] = new CoroutineLib(i);
        }
    }
}
