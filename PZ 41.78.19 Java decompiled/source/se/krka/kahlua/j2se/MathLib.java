// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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

    public MathLib(int int0) {
        this.index = int0;
    }

    public static void register(Platform platform, KahluaTable table1) {
        KahluaTable table0 = platform.newTable();
        table1.rawset("math", table0);
        table0.rawset("pi", KahluaUtil.toDouble(Math.PI));
        table0.rawset("huge", KahluaUtil.toDouble(Double.POSITIVE_INFINITY));

        for (int int0 = 0; int0 < 24; int0++) {
            table0.rawset(names[int0], functions[int0]);
        }
    }

    @Override
    public String toString() {
        return "math." + names[this.index];
    }

    @Override
    public int call(LuaCallFrame luaCallFrame, int int0) {
        switch (this.index) {
            case 0:
                return abs(luaCallFrame, int0);
            case 1:
                return acos(luaCallFrame, int0);
            case 2:
                return asin(luaCallFrame, int0);
            case 3:
                return atan(luaCallFrame, int0);
            case 4:
                return atan2(luaCallFrame, int0);
            case 5:
                return ceil(luaCallFrame, int0);
            case 6:
                return cos(luaCallFrame, int0);
            case 7:
                return cosh(luaCallFrame, int0);
            case 8:
                return deg(luaCallFrame, int0);
            case 9:
                return exp(luaCallFrame, int0);
            case 10:
                return floor(luaCallFrame, int0);
            case 11:
                return fmod(luaCallFrame, int0);
            case 12:
                return frexp(luaCallFrame, int0);
            case 13:
                return ldexp(luaCallFrame, int0);
            case 14:
                return log(luaCallFrame, int0);
            case 15:
                return log10(luaCallFrame, int0);
            case 16:
                return modf(luaCallFrame, int0);
            case 17:
                return pow(luaCallFrame, int0);
            case 18:
                return rad(luaCallFrame, int0);
            case 19:
                return sin(luaCallFrame, int0);
            case 20:
                return sinh(luaCallFrame, int0);
            case 21:
                return sqrt(luaCallFrame, int0);
            case 22:
                return tan(luaCallFrame, int0);
            case 23:
                return tanh(luaCallFrame, int0);
            default:
                return 0;
        }
    }

    private static int abs(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[0]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.abs(double0)));
        return 1;
    }

    private static int ceil(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[5]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.ceil(double0)));
        return 1;
    }

    private static int floor(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[10]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.floor(double0)));
        return 1;
    }

    public static boolean isNegative(double double0) {
        return Double.doubleToLongBits(double0) < 0L;
    }

    public static double round(double double0) {
        if (double0 < 0.0) {
            return -round(-double0);
        } else {
            double0 += 0.5;
            double double1 = Math.floor(double0);
            return double1 == double0 ? double1 - ((long)double1 & 1L) : double1;
        }
    }

    private static int modf(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[16]);
        boolean boolean0 = false;
        if (isNegative(double0)) {
            boolean0 = true;
            double0 = -double0;
        }

        double double1 = Math.floor(double0);
        double double2;
        if (Double.isInfinite(double1)) {
            double2 = 0.0;
        } else {
            double2 = double0 - double1;
        }

        if (boolean0) {
            double1 = -double1;
            double2 = -double2;
        }

        luaCallFrame.push(KahluaUtil.toDouble(double1), KahluaUtil.toDouble(double2));
        return 2;
    }

    private static int fmod(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 2, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[11]);
        double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 2, names[11]);
        double double2;
        if (Double.isInfinite(double0) || Double.isNaN(double0)) {
            double2 = Double.NaN;
        } else if (Double.isInfinite(double1)) {
            double2 = double0;
        } else {
            double1 = Math.abs(double1);
            boolean boolean0 = false;
            if (isNegative(double0)) {
                boolean0 = true;
                double0 = -double0;
            }

            double2 = double0 - Math.floor(double0 / double1) * double1;
            if (boolean0) {
                double2 = -double2;
            }
        }

        luaCallFrame.push(KahluaUtil.toDouble(double2));
        return 1;
    }

    private static int cosh(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[7]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.cosh(double0)));
        return 1;
    }

    private static int sinh(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[20]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.sinh(double0)));
        return 1;
    }

    private static int tanh(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[23]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.tanh(double0)));
        return 1;
    }

    private static int deg(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[8]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.toDegrees(double0)));
        return 1;
    }

    private static int rad(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[18]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.toRadians(double0)));
        return 1;
    }

    private static int acos(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[1]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.acos(double0)));
        return 1;
    }

    private static int asin(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[2]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.asin(double0)));
        return 1;
    }

    private static int atan(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[3]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.atan(double0)));
        return 1;
    }

    private static int atan2(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 2, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[4]);
        double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 2, names[4]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.atan2(double0, double1)));
        return 1;
    }

    private static int cos(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[6]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.cos(double0)));
        return 1;
    }

    private static int sin(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[19]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.sin(double0)));
        return 1;
    }

    private static int tan(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[22]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.tan(double0)));
        return 1;
    }

    private static int sqrt(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[21]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.sqrt(double0)));
        return 1;
    }

    private static int exp(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[9]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.exp(double0)));
        return 1;
    }

    private static int pow(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 2, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[17]);
        double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 2, names[17]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.pow(double0, double1)));
        return 1;
    }

    private static int log(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[14]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.log(double0)));
        return 1;
    }

    private static int log10(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[15]);
        luaCallFrame.push(KahluaUtil.toDouble(Math.log10(double0)));
        return 1;
    }

    private static int frexp(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[12]);
        double double1;
        double double2;
        if (!Double.isInfinite(double0) && !Double.isNaN(double0)) {
            double1 = Math.ceil(Math.log(double0) * LN2_INV);
            int int1 = 1 << (int)double1;
            double2 = double0 / int1;
        } else {
            double1 = 0.0;
            double2 = double0;
        }

        luaCallFrame.push(KahluaUtil.toDouble(double2), KahluaUtil.toDouble(double1));
        return 2;
    }

    private static int ldexp(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 2, "Not enough arguments");
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, names[13]);
        double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 2, names[13]);
        double double2 = double0 + double1;
        double double3;
        if (!Double.isInfinite(double2) && !Double.isNaN(double2)) {
            int int1 = (int)double1;
            double3 = double0 * (1 << int1);
        } else {
            double3 = double0;
        }

        luaCallFrame.push(KahluaUtil.toDouble(double3));
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

        for (int int0 = 0; int0 < 24; int0++) {
            functions[int0] = new MathLib(int0);
        }

        LN2_INV = 1.0 / Math.log(2.0);
    }
}
