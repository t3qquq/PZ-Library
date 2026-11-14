// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.j2se;

import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;

public class MathLib implements JavaFunction {
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
    private static final MathLib[] functions = new MathLib[24];
    private final int index;
    private static final double LN2_INV;

    public MathLib(int index) {
        this.index = index;
    }

    public static void register(Platform platform, KahluaTable env) {
        KahluaTable math = platform.newTable();
        env.rawset("math", math);
        math.rawset("pi", KahluaUtil.toDouble(Math.PI));
        math.rawset("huge", KahluaUtil.toDouble(Double.POSITIVE_INFINITY));

        for (int i = 0; i < 24; i++) {
            math.rawset(names[i], functions[i]);
        }
    }

    @Override
    public String toString() {
        return "math." + names[this.index];
    }

    @Override
    public int call(LuaCallFrame callFrame, int nArguments) {
        switch (this.index) {
            case 0:
                return abs(callFrame, nArguments);
            case 1:
                return acos(callFrame, nArguments);
            case 2:
                return asin(callFrame, nArguments);
            case 3:
                return atan(callFrame, nArguments);
            case 4:
                return atan2(callFrame, nArguments);
            case 5:
                return ceil(callFrame, nArguments);
            case 6:
                return cos(callFrame, nArguments);
            case 7:
                return cosh(callFrame, nArguments);
            case 8:
                return deg(callFrame, nArguments);
            case 9:
                return exp(callFrame, nArguments);
            case 10:
                return floor(callFrame, nArguments);
            case 11:
                return fmod(callFrame, nArguments);
            case 12:
                return frexp(callFrame, nArguments);
            case 13:
                return ldexp(callFrame, nArguments);
            case 14:
                return log(callFrame, nArguments);
            case 15:
                return log10(callFrame, nArguments);
            case 16:
                return modf(callFrame, nArguments);
            case 17:
                return pow(callFrame, nArguments);
            case 18:
                return rad(callFrame, nArguments);
            case 19:
                return sin(callFrame, nArguments);
            case 20:
                return sinh(callFrame, nArguments);
            case 21:
                return sqrt(callFrame, nArguments);
            case 22:
                return tan(callFrame, nArguments);
            case 23:
                return tanh(callFrame, nArguments);
            default:
                return 0;
        }
    }

    private static int abs(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        double x = KahluaUtil.getDoubleArg(callFrame, 1, names[0]);
        callFrame.push(KahluaUtil.toDouble(Math.abs(x)));
        return 1;
    }

    private static int ceil(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        double x = KahluaUtil.getDoubleArg(callFrame, 1, names[5]);
        callFrame.push(KahluaUtil.toDouble(Math.ceil(x)));
        return 1;
    }

    private static int floor(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        double x = KahluaUtil.getDoubleArg(callFrame, 1, names[10]);
        callFrame.push(KahluaUtil.toDouble(Math.floor(x)));
        return 1;
    }

    public static boolean isNegative(double vDouble) {
        return Double.doubleToLongBits(vDouble) < 0L;
    }

    public static double round(double x) {
        if (x < 0.0) {
            return -round(-x);
        }

        x += 0.5;
        double x2 = Math.floor(x);
        return x2 == x ? x2 - ((long)x2 & 1L) : x2;
    }

    private static int modf(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        double x = KahluaUtil.getDoubleArg(callFrame, 1, names[16]);
        boolean negate = false;
        if (isNegative(x)) {
            negate = true;
            x = -x;
        }

        double intPart = Math.floor(x);
        double fracPart;
        if (Double.isInfinite(intPart)) {
            fracPart = 0.0;
        } else {
            fracPart = x - intPart;
        }

        if (negate) {
            intPart = -intPart;
            fracPart = -fracPart;
        }

        callFrame.push(KahluaUtil.toDouble(intPart), KahluaUtil.toDouble(fracPart));
        return 2;
    }

    private static int fmod(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 2, "Not enough arguments");
        double v1 = KahluaUtil.getDoubleArg(callFrame, 1, names[11]);
        double v2 = KahluaUtil.getDoubleArg(callFrame, 2, names[11]);
        double res;
        if (Double.isInfinite(v1) || Double.isNaN(v1)) {
            res = Double.NaN;
        } else if (Double.isInfinite(v2)) {
            res = v1;
        } else {
            v2 = Math.abs(v2);
            boolean negate = false;
            if (isNegative(v1)) {
                negate = true;
                v1 = -v1;
            }

            res = v1 - Math.floor(v1 / v2) * v2;
            if (negate) {
                res = -res;
            }
        }

        callFrame.push(KahluaUtil.toDouble(res));
        return 1;
    }

    private static int cosh(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        double x = KahluaUtil.getDoubleArg(callFrame, 1, names[7]);
        callFrame.push(KahluaUtil.toDouble(Math.cosh(x)));
        return 1;
    }

    private static int sinh(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        double x = KahluaUtil.getDoubleArg(callFrame, 1, names[20]);
        callFrame.push(KahluaUtil.toDouble(Math.sinh(x)));
        return 1;
    }

    private static int tanh(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        double x = KahluaUtil.getDoubleArg(callFrame, 1, names[23]);
        callFrame.push(KahluaUtil.toDouble(Math.tanh(x)));
        return 1;
    }

    private static int deg(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        double x = KahluaUtil.getDoubleArg(callFrame, 1, names[8]);
        callFrame.push(KahluaUtil.toDouble(Math.toDegrees(x)));
        return 1;
    }

    private static int rad(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        double x = KahluaUtil.getDoubleArg(callFrame, 1, names[18]);
        callFrame.push(KahluaUtil.toDouble(Math.toRadians(x)));
        return 1;
    }

    private static int acos(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        double x = KahluaUtil.getDoubleArg(callFrame, 1, names[1]);
        callFrame.push(KahluaUtil.toDouble(Math.acos(x)));
        return 1;
    }

    private static int asin(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        double x = KahluaUtil.getDoubleArg(callFrame, 1, names[2]);
        callFrame.push(KahluaUtil.toDouble(Math.asin(x)));
        return 1;
    }

    private static int atan(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        double x = KahluaUtil.getDoubleArg(callFrame, 1, names[3]);
        callFrame.push(KahluaUtil.toDouble(Math.atan(x)));
        return 1;
    }

    private static int atan2(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 2, "Not enough arguments");
        double y = KahluaUtil.getDoubleArg(callFrame, 1, names[4]);
        double x = KahluaUtil.getDoubleArg(callFrame, 2, names[4]);
        callFrame.push(KahluaUtil.toDouble(Math.atan2(y, x)));
        return 1;
    }

    private static int cos(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        double x = KahluaUtil.getDoubleArg(callFrame, 1, names[6]);
        callFrame.push(KahluaUtil.toDouble(Math.cos(x)));
        return 1;
    }

    private static int sin(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        double x = KahluaUtil.getDoubleArg(callFrame, 1, names[19]);
        callFrame.push(KahluaUtil.toDouble(Math.sin(x)));
        return 1;
    }

    private static int tan(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        double x = KahluaUtil.getDoubleArg(callFrame, 1, names[22]);
        callFrame.push(KahluaUtil.toDouble(Math.tan(x)));
        return 1;
    }

    private static int sqrt(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        double x = KahluaUtil.getDoubleArg(callFrame, 1, names[21]);
        callFrame.push(KahluaUtil.toDouble(Math.sqrt(x)));
        return 1;
    }

    private static int exp(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        double x = KahluaUtil.getDoubleArg(callFrame, 1, names[9]);
        callFrame.push(KahluaUtil.toDouble(Math.exp(x)));
        return 1;
    }

    private static int pow(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 2, "Not enough arguments");
        double x = KahluaUtil.getDoubleArg(callFrame, 1, names[17]);
        double y = KahluaUtil.getDoubleArg(callFrame, 2, names[17]);
        callFrame.push(KahluaUtil.toDouble(Math.pow(x, y)));
        return 1;
    }

    private static int log(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        double x = KahluaUtil.getDoubleArg(callFrame, 1, names[14]);
        callFrame.push(KahluaUtil.toDouble(Math.log(x)));
        return 1;
    }

    private static int log10(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        double x = KahluaUtil.getDoubleArg(callFrame, 1, names[15]);
        callFrame.push(KahluaUtil.toDouble(Math.log10(x)));
        return 1;
    }

    private static int frexp(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        double x = KahluaUtil.getDoubleArg(callFrame, 1, names[12]);
        double e;
        double m;
        if (!Double.isInfinite(x) && !Double.isNaN(x)) {
            e = Math.ceil(Math.log(x) * LN2_INV);
            int div = 1 << (int)e;
            m = x / div;
        } else {
            e = 0.0;
            m = x;
        }

        callFrame.push(KahluaUtil.toDouble(m), KahluaUtil.toDouble(e));
        return 2;
    }

    private static int ldexp(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 2, "Not enough arguments");
        double m = KahluaUtil.getDoubleArg(callFrame, 1, names[13]);
        double dE = KahluaUtil.getDoubleArg(callFrame, 2, names[13]);
        double tmp = m + dE;
        double ret;
        if (!Double.isInfinite(tmp) && !Double.isNaN(tmp)) {
            int e = (int)dE;
            ret = m * (1 << e);
        } else {
            ret = m;
        }

        callFrame.push(KahluaUtil.toDouble(ret));
        return 1;
    }

    static {
        names[0] = "abs";
        names[1] = "acos";
        names[2] = "asin";
        names[3] = "atan";
        names[4] = "atan2";
        names[5] = "ceil";
        names[6] = "cos";
        names[7] = "cosh";
        names[8] = "deg";
        names[9] = "exp";
        names[10] = "floor";
        names[11] = "fmod";
        names[12] = "frexp";
        names[13] = "ldexp";
        names[14] = "log";
        names[15] = "log10";
        names[16] = "modf";
        names[17] = "pow";
        names[18] = "rad";
        names[19] = "sin";
        names[20] = "sinh";
        names[21] = "sqrt";
        names[22] = "tan";
        names[23] = "tanh";

        for (int i = 0; i < 24; i++) {
            functions[i] = new MathLib(i);
        }

        LN2_INV = 1.0 / Math.log(2.0);
    }
}
