// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jorbis;

class Residue1 extends Residue0 {
    @Override
    int inverse(Block block, Object object, float[][] floats, int[] ints, int int2) {
        int int0 = 0;

        for (int int1 = 0; int1 < int2; int1++) {
            if (ints[int1] != 0) {
                floats[int0++] = floats[int1];
            }
        }

        return int0 != 0 ? _01inverse(block, object, floats, int0, 1) : 0;
    }
}
