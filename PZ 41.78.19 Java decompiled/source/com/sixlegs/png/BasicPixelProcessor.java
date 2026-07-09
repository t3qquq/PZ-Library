// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.sixlegs.png;

class BasicPixelProcessor extends PixelProcessor {
    protected final Destination dst;
    protected final int samples;

    public BasicPixelProcessor(Destination destination, int int0) {
        this.dst = destination;
        this.samples = int0;
    }

    @Override
    public boolean process(int[] ints, int int1, int int0, int var4, int int2, int int3) {
        if (int0 == 1) {
            this.dst.setPixels(int1, int2, int3, ints);
        } else {
            int int4 = int1;
            int int5 = 0;

            for (int int6 = this.samples * int3; int5 < int6; int5 += this.samples) {
                for (int int7 = 0; int7 < this.samples; int7++) {
                    ints[int7] = ints[int5 + int7];
                }

                this.dst.setPixel(int4, int2, ints);
                int4 += int0;
            }
        }

        return true;
    }
}
