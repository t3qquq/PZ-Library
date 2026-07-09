// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.sixlegs.png;

final class GammaPixelProcessor extends BasicPixelProcessor {
    private final short[] gammaTable;
    private final int shift;
    private final int samplesNoAlpha;
    private final boolean hasAlpha;
    private final boolean shiftAlpha;

    public GammaPixelProcessor(Destination destination, short[] shorts, int int0) {
        super(destination, destination.getRaster().getNumBands());
        this.gammaTable = shorts;
        this.shift = int0;
        this.hasAlpha = this.samples % 2 == 0;
        this.samplesNoAlpha = this.hasAlpha ? this.samples - 1 : this.samples;
        this.shiftAlpha = this.hasAlpha && int0 > 0;
    }

    @Override
    public boolean process(int[] ints, int int5, int int6, int int7, int int8, int int1) {
        int int0 = this.samples * int1;

        for (int int2 = 0; int2 < this.samplesNoAlpha; int2++) {
            for (int int3 = int2; int3 < int0; int3 += this.samples) {
                ints[int3] = '\uffff' & this.gammaTable[ints[int3] >> this.shift];
            }
        }

        if (this.shiftAlpha) {
            for (int int4 = this.samplesNoAlpha; int4 < int0; int4 += this.samples) {
                ints[int4] >>= this.shift;
            }
        }

        return super.process(ints, int5, int6, int7, int8, int1);
    }
}
