// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.stdlib;

import java.util.Random;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;

public class RandomLib implements JavaFunction {
    private static final Class RANDOM_CLASS = new Random().getClass();
    private static final int RANDOM = 0;
    private static final int RANDOMSEED = 1;
    private static final int NEWRANDOM = 2;
    private static final int NUM_FUNCTIONS = 3;
    private static final String[] names = new String[3];
    private static final RandomLib[] functions = new RandomLib[3];
    private static final RandomLib NEWRANDOM_FUN;
    private final int index;

    public RandomLib(int int0) {
        this.index = int0;
    }

    public static void register(Platform platform, KahluaTable table2) {
        KahluaTable table0 = platform.newTable();

        for (int int0 = 0; int0 < 2; int0++) {
            table0.rawset(names[int0], functions[int0]);
        }

        table0.rawset("__index", table0);
        KahluaTable table1 = KahluaUtil.getClassMetatables(platform, table2);
        table1.rawset(RANDOM_CLASS, table0);
        table2.rawset("newrandom", NEWRANDOM_FUN);
    }

    @Override
    public int call(LuaCallFrame luaCallFrame, int int0) {
        switch (this.index) {
            case 0:
                return this.random(luaCallFrame, int0);
            case 1:
                return this.randomSeed(luaCallFrame, int0);
            case 2:
                return this.newRandom(luaCallFrame);
            default:
                return 0;
        }
    }

    private int randomSeed(LuaCallFrame luaCallFrame, int var2) {
        Random random = this.getRandom(luaCallFrame, "seed");
        Object object = luaCallFrame.get(1);
        int int0 = object == null ? 0 : object.hashCode();
        random.setSeed(int0);
        return 0;
    }

    private int random(LuaCallFrame luaCallFrame, int var2) {
        Random random = this.getRandom(luaCallFrame, "random");
        Double double0 = KahluaUtil.getOptionalNumberArg(luaCallFrame, 2);
        Double double1 = KahluaUtil.getOptionalNumberArg(luaCallFrame, 3);
        if (double0 == null) {
            return luaCallFrame.push(KahluaUtil.toDouble(random.nextDouble()));
        } else {
            int int0 = double0.intValue();
            int int1;
            if (double1 == null) {
                int1 = int0;
                int0 = 1;
            } else {
                int1 = double1.intValue();
            }

            return luaCallFrame.push(KahluaUtil.toDouble((long)(int0 + random.nextInt(int1 - int0 + 1))));
        }
    }

    private Random getRandom(LuaCallFrame luaCallFrame, String string) {
        Object object = KahluaUtil.getArg(luaCallFrame, 1, string);
        if (!(object instanceof Random)) {
            KahluaUtil.fail("First argument to " + string + " must be an object of type random.");
        }

        return (Random)object;
    }

    private int newRandom(LuaCallFrame luaCallFrame) {
        return luaCallFrame.push(new Random());
    }

    static {
        names[0] = "random";
        names[1] = "seed";
        names[2] = "newrandom";

        for (int int0 = 0; int0 < 3; int0++) {
            functions[int0] = new RandomLib(int0);
        }

        NEWRANDOM_FUN = new RandomLib(2);
    }
}
