// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package de.jarnbjo.vorbis;

public final class Util {
    public static final int ilog(int int1) {
        int int0;
        for (int0 = 0; int1 > 0; int0++) {
            int1 >>= 1;
        }

        return int0;
    }

    public static final float float32unpack(int int0) {
        float float0 = int0 & 2097151;
        float float1 = (int0 & 2145386496) >> 21;
        if ((int0 & -2147483648) != 0) {
            float0 = -float0;
        }

        return float0 * (float)Math.pow(2.0, float1 - 788.0);
    }

    public static final int lookup1Values(int int2, int int1) {
        int int0 = (int)Math.pow(Math.E, Math.log(int2) / int1);
        return intPow(int0 + 1, int1) <= int2 ? int0 + 1 : int0;
    }

    public static final int intPow(int int2, int int1) {
        int int0 = 1;

        while (int1 > 0) {
            int1--;
            int0 *= int2;
        }

        return int0;
    }

    public static final boolean isBitSet(int int0, int int1) {
        return (int0 & 1 << int1) != 0;
    }

    public static final int icount(int int1) {
        int int0 = 0;

        while (int1 > 0) {
            int0 += int1 & 1;
            int1 >>= 1;
        }

        return int0;
    }

    public static final int lowNeighbour(int[] ints, int int3) {
        int int0 = -1;
        int int1 = 0;

        for (int int2 = 0; int2 < ints.length && int2 < int3; int2++) {
            if (ints[int2] > int0 && ints[int2] < ints[int3]) {
                int0 = ints[int2];
                int1 = int2;
            }
        }

        return int1;
    }

    public static final int highNeighbour(int[] ints, int int3) {
        int int0 = Integer.MAX_VALUE;
        int int1 = 0;

        for (int int2 = 0; int2 < ints.length && int2 < int3; int2++) {
            if (ints[int2] < int0 && ints[int2] > ints[int3]) {
                int0 = ints[int2];
                int1 = int2;
            }
        }

        return int1;
    }

    public static final int renderPoint(int int6, int int5, int int2, int int1, int int7) {
        int int0 = int1 - int2;
        int int3 = int0 < 0 ? -int0 : int0;
        int int4 = int3 * (int7 - int6) / (int5 - int6);
        return int0 < 0 ? int2 - int4 : int2 + int4;
    }

    public static final void renderLine(int int5, int int2, int int4, int int1, float[] floats) {
        int int0 = int1 - int2;
        int int3 = int4 - int5;
        int int6 = int0 / int3;
        int int7 = int0 < 0 ? int6 - 1 : int6 + 1;
        int int8 = int2;
        int int9 = 0;
        int int10 = (int0 < 0 ? -int0 : int0) - (int6 > 0 ? int6 * int3 : -int6 * int3);
        floats[int5] *= Floor.DB_STATIC_TABLE[int2];

        for (int int11 = int5 + 1; int11 < int4; int11++) {
            int9 += int10;
            if (int9 >= int3) {
                int9 -= int3;
                floats[int11] *= Floor.DB_STATIC_TABLE[int8 += int7];
            } else {
                floats[int11] *= Floor.DB_STATIC_TABLE[int8 += int6];
            }
        }
    }
}
