// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jorbis;

class Util {
    static int icount(int int1) {
        int int0 = 0;

        while (int1 != 0) {
            int0 += int1 & 1;
            int1 >>>= 1;
        }

        return int0;
    }

    static int ilog(int int1) {
        int int0 = 0;

        while (int1 != 0) {
            int0++;
            int1 >>>= 1;
        }

        return int0;
    }

    static int ilog2(int int1) {
        int int0 = 0;

        while (int1 > 1) {
            int0++;
            int1 >>>= 1;
        }

        return int0;
    }
}
