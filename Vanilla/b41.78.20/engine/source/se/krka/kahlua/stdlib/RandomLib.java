/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.stdlib;

import java.util.Random;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;

public class RandomLib
implements JavaFunction {
    private static final Class RANDOM_CLASS = new Random().getClass();
    private static final int RANDOM = 0;
    private static final int RANDOMSEED = 1;
    private static final int NEWRANDOM = 2;
    private static final int NUM_FUNCTIONS = 3;
    private static final String[] names = new String[3];
    private static final RandomLib[] functions;
    private static final RandomLib NEWRANDOM_FUN;
    private final int index;

    public RandomLib(int n) {
        this.index = n;
    }

    public static void register(Platform platform, KahluaTable kahluaTable) {
        KahluaTable kahluaTable2 = platform.newTable();
        for (int i = 0; i < 2; ++i) {
            kahluaTable2.rawset(names[i], (Object)functions[i]);
        }
        kahluaTable2.rawset("__index", (Object)kahluaTable2);
        KahluaTable kahluaTable3 = KahluaUtil.getClassMetatables(platform, kahluaTable);
        kahluaTable3.rawset(RANDOM_CLASS, (Object)kahluaTable2);
        kahluaTable.rawset("newrandom", (Object)NEWRANDOM_FUN);
    }

    @Override
    public int call(LuaCallFrame luaCallFrame, int n) {
        switch (this.index) {
            case 0: {
                return this.random(luaCallFrame, n);
            }
            case 1: {
                return this.randomSeed(luaCallFrame, n);
            }
            case 2: {
                return this.newRandom(luaCallFrame);
            }
        }
        return 0;
    }

    private int randomSeed(LuaCallFrame luaCallFrame, int n) {
        Random random = this.getRandom(luaCallFrame, "seed");
        Object object = luaCallFrame.get(1);
        int n2 = object == null ? 0 : object.hashCode();
        random.setSeed(n2);
        return 0;
    }

    private int random(LuaCallFrame luaCallFrame, int n) {
        int n2;
        Random random = this.getRandom(luaCallFrame, "random");
        Double d = KahluaUtil.getOptionalNumberArg(luaCallFrame, 2);
        Double d2 = KahluaUtil.getOptionalNumberArg(luaCallFrame, 3);
        if (d == null) {
            return luaCallFrame.push(KahluaUtil.toDouble(random.nextDouble()));
        }
        int n3 = d.intValue();
        if (d2 == null) {
            n2 = n3;
            n3 = 1;
        } else {
            n2 = d2.intValue();
        }
        return luaCallFrame.push(KahluaUtil.toDouble(n3 + random.nextInt(n2 - n3 + 1)));
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
        RandomLib.names[0] = "random";
        RandomLib.names[1] = "seed";
        RandomLib.names[2] = "newrandom";
        functions = new RandomLib[3];
        for (int i = 0; i < 3; ++i) {
            RandomLib.functions[i] = new RandomLib(i);
        }
        NEWRANDOM_FUN = new RandomLib(2);
    }
}

