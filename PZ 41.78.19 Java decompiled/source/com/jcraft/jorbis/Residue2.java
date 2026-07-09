// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jorbis;

class Residue2 extends Residue0 {
    @Override
    int inverse(Block block, Object object, float[][] floats, int[] ints, int int1) {
        int int0 = 0;
        int0 = 0;

        while (int0 < int1 && ints[int0] == 0) {
            int0++;
        }

        return int0 == int1 ? 0 : _2inverse(block, object, floats, int1);
    }
}
