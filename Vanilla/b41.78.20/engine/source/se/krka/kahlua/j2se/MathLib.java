/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.j2se;

import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;

public class MathLib
implements JavaFunction {
    private static final int ABS = 0;
    private static final int ACOS = 1;
    private static final int ASIN = 2;
    private static final int ATAN = 3;
    private static final int ATAN2 = 4;
    private static final int CEIL = 5;
    private static final int COS = 6;
    private static final int COSH = 7;
    private static final int DEG = 8;
    private static final int EXP = 9;
    private static final int FLOOR = 10;
    private static final int FMOD = 11;
    private static final int FREXP = 12;
    private static final int LDEXP = 13;
    private static final int LOG = 14;
    private static final int LOG10 = 15;
    private static final int MODF = 16;
    private static final int POW = 17;
    private static final int RAD = 18;
    private static final int SIN = 19;
    private static final int SINH = 20;
    private static final int SQRT = 21;
    private static final int TAN = 22;
    private static final int TANH = 23;
    private static final int NUM_FUNCTIONS = 24;
    private static final String[] names = new String[24];
    private static final MathLib[] functions;
    private final int index;
    private static final double LN2_INV;

    public MathLib(int n) {
        this.index = n;
    }

    public static void register(Platform platform, KahluaTable kahluaTable) {
        KahluaTable kahluaTable2 = platform.newTable();
        kahluaTable.rawset("math", (Object)kahluaTable2);
        kahluaTable2.rawset("pi", (Object)KahluaUtil.toDouble(Math.PI));
        kahluaTable2.rawset("huge", (Object)KahluaUtil.toDouble(Double.POSITIVE_INFINITY));
        for (int i = 0; i < 24; ++i) {
            kahluaTable2.rawset(names[i], (Object)functions[i]);
        }
    }

    public String toString() {
        return "math." + names[this.index];
    }

    @Override
    public int call(LuaCallFrame luaCallFrame, int n) {
        switch (this.index) {
            case 0: {
                return MathLib.abs(luaCallFrame, n);
            }
            case 1: {
                return MathLib.acos(luaCallFrame, n);
            }
            case 2: {
                return MathLib.asin(luaCallFrame, n);
            }
            case 3: {
                return MathLib.atan(luaCallFrame, n);
            }
            case 4: {
                return MathLib.atan2(luaCallFrame, n);
            }
            case 5: {
                return MathLib.ceil(luaCallFrame, n);
            }
            case 6: {
                return MathLib.cos(luaCallFrame, n);
            }
            case 7: {
                return MathLib.cosh(luaCallFrame, n);
            }
            case 8: {
                return MathLib.deg(luaCallFrame, n);
            }
            case 9: {
                return MathLib.exp(luaCallFrame, n);
            }
            case 10: {
                return MathLib.floor(luaCallFrame, n);
            }
            case 11: {
                return MathLib.fmod(luaCallFrame, n);
            }
            case 12: {
                return MathLib.frexp(luaCallFrame, n);
            }
            case 13: {
                return MathLib.ldexp(luaCallFrame, n);
            }
            case 14: {
                return MathLib.log(luaCallFrame, n);
            }
            case 15: {
                return MathLib.log10(luaCallFrame, n);
            }
            case 16: {
                return MathLib.modf(luaCallFrame, n);
            }
            case 17: {
                return MathLib.pow(luaCallFrame, n);
            }
            case 18: {
                return MathLib.rad(luaCallFrame, n);
            }
            case 19: {
                return MathLib.sin(luaCallFrame, n);
            }
            case 20: {
                return MathLib.sinh(luaCallFrame, n);
            }
            case 21: {
                return MathLib.sqrt(luaCallFrame, n);
            }
            case 22: {
                return MathLib.tan(luaCallFrame, n);
            }
            case 23: {
                return MathLib.tanh(luaCallFrame, n);
            }
        }
        return 0;
    }

    private static int abs(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[0]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.abs(d)));
        return 1;
    }

    private static int ceil(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[5]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.ceil(d)));
        return 1;
    }

    private static int floor(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[10]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.floor(d)));
        return 1;
    }

    public static boolean isNegative(double d) {
        return Double.doubleToLongBits(d) < 0L;
    }

    public static double round(double d) {
        if (d < 0.0) {
            return -MathLib.round(-d);
        }
        double d2 = Math.floor(d += 0.5);
        if (d2 == d) {
            return d2 - (double)((long)d2 & 1L);
        }
        return d2;
    }

    private static int modf(LuaCallFrame luaCallFrame, int n) {
        double d;
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        double d2 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[16]);
        boolean bl = false;
        if (MathLib.isNegative(d2)) {
            bl = true;
            d2 = -d2;
        }
        double d3 = Double.isInfinite(d = Math.floor(d2)) ? 0.0 : d2 - d;
        if (bl) {
            d = -d;
            d3 = -d3;
        }
        luaCallFrame.push(KahluaUtil.toDouble(d), KahluaUtil.toDouble(d3));
        return 2;
    }

    private static int fmod(LuaCallFrame luaCallFrame, int n) {
        double d;
        KahluaUtil.luaAssert(n >= 2, "Not enough arguments");
        double d2 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[11]);
        double d3 = KahluaUtil.getDoubleArg(luaCallFrame, 2, names[11]);
        if (Double.isInfinite(d2) || Double.isNaN(d2)) {
            d = Double.NaN;
        } else if (Double.isInfinite(d3)) {
            d = d2;
        } else {
            d3 = Math.abs(d3);
            boolean bl = false;
            if (MathLib.isNegative(d2)) {
                bl = true;
                d2 = -d2;
            }
            d = d2 - Math.floor(d2 / d3) * d3;
            if (bl) {
                d = -d;
            }
        }
        luaCallFrame.push(KahluaUtil.toDouble(d));
        return 1;
    }

    private static int cosh(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[7]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.cosh(d)));
        return 1;
    }

    private static int sinh(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[20]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.sinh(d)));
        return 1;
    }

    private static int tanh(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[23]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.tanh(d)));
        return 1;
    }

    private static int deg(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[8]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.toDegrees(d)));
        return 1;
    }

    private static int rad(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[18]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.toRadians(d)));
        return 1;
    }

    private static int acos(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[1]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.acos(d)));
        return 1;
    }

    private static int asin(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[2]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.asin(d)));
        return 1;
    }

    private static int atan(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[3]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.atan(d)));
        return 1;
    }

    private static int atan2(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 2, "Not enough arguments");
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[4]);
        double d2 = KahluaUtil.getDoubleArg(luaCallFrame, 2, names[4]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.atan2(d, d2)));
        return 1;
    }

    private static int cos(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[6]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.cos(d)));
        return 1;
    }

    private static int sin(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[19]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.sin(d)));
        return 1;
    }

    private static int tan(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[22]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.tan(d)));
        return 1;
    }

    private static int sqrt(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[21]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.sqrt(d)));
        return 1;
    }

    private static int exp(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[9]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.exp(d)));
        return 1;
    }

    private static int pow(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 2, "Not enough arguments");
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[17]);
        double d2 = KahluaUtil.getDoubleArg(luaCallFrame, 2, names[17]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.pow(d, d2)));
        return 1;
    }

    private static int log(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[14]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.log(d)));
        return 1;
    }

    private static int log10(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[15]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.log10(d)));
        return 1;
    }

    private static int frexp(LuaCallFrame luaCallFrame, int n) {
        double d;
        double d2;
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        double d3 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[12]);
        if (Double.isInfinite(d3) || Double.isNaN(d3)) {
            d2 = 0.0;
            d = d3;
        } else {
            d2 = Math.ceil(Math.log(d3) * LN2_INV);
            int n2 = 1 << (int)d2;
            d = d3 / (double)n2;
        }
        luaCallFrame.push(KahluaUtil.toDouble(d), KahluaUtil.toDouble(d2));
        return 2;
    }

    private static int ldexp(LuaCallFrame luaCallFrame, int n) {
        double d;
        KahluaUtil.luaAssert(n >= 2, "Not enough arguments");
        double d2 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[13]);
        double d3 = KahluaUtil.getDoubleArg(luaCallFrame, 2, names[13]);
        double d4 = d2 + d3;
        if (Double.isInfinite(d4) || Double.isNaN(d4)) {
            d = d2;
        } else {
            int n2 = (int)d3;
            d = d2 * (double)(1 << n2);
        }
        luaCallFrame.push(KahluaUtil.toDouble(d));
        return 1;
    }

    static {
        MathLib.names[0] = "abs";
        MathLib.names[1] = "acos";
        MathLib.names[2] = "asin";
        MathLib.names[3] = "atan";
        MathLib.names[4] = "atan2";
        MathLib.names[5] = "ceil";
        MathLib.names[6] = "cos";
        MathLib.names[7] = "cosh";
        MathLib.names[8] = "deg";
        MathLib.names[9] = "exp";
        MathLib.names[10] = "floor";
        MathLib.names[11] = "fmod";
        MathLib.names[12] = "frexp";
        MathLib.names[13] = "ldexp";
        MathLib.names[14] = "log";
        MathLib.names[15] = "log10";
        MathLib.names[16] = "modf";
        MathLib.names[17] = "pow";
        MathLib.names[18] = "rad";
        MathLib.names[19] = "sin";
        MathLib.names[20] = "sinh";
        MathLib.names[21] = "sqrt";
        MathLib.names[22] = "tan";
        MathLib.names[23] = "tanh";
        functions = new MathLib[24];
        for (int i = 0; i < 24; ++i) {
            MathLib.functions[i] = new MathLib(i);
        }
        LN2_INV = 1.0 / Math.log(2.0);
    }
}

