// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.sixlegs.png;

import java.awt.image.WritableRaster;

final class SubsamplingDestination extends RasterDestination {
    private final int xsub;
    private final int ysub;
    private final int xoff;
    private final int yoff;
    private final int[] singlePixel;

    public SubsamplingDestination(WritableRaster writableRaster, int int0, int int1, int int2, int int3, int int4) {
        super(writableRaster, int0);
        this.xsub = int1;
        this.ysub = int2;
        this.xoff = int3;
        this.yoff = int4;
        this.singlePixel = new int[writableRaster.getNumBands()];
    }

    @Override
    public void setPixels(int int2, int int0, int int8, int[] ints) {
        if ((int0 - this.yoff) % this.ysub == 0) {
            int int1 = (int2 - this.xoff) / this.xsub;
            int int3 = (int0 - this.yoff) / this.ysub;
            int int4 = int1 * this.xsub + this.xoff;
            if (int4 < int2) {
                int1++;
                int4 += this.xsub;
            }

            int int5 = this.raster.getNumBands();
            int int6 = int4 - int2;

            for (int int7 = int2 + int8; int6 < int7; int6 += this.xsub) {
                System.arraycopy(ints, int6 * int5, this.singlePixel, 0, int5);
                super.setPixel(int1++, int3, this.singlePixel);
            }
        }
    }

    @Override
    public void setPixel(int int0, int int1, int[] ints) {
        int0 -= this.xoff;
        int1 -= this.yoff;
        if (int0 % this.xsub == 0 && int1 % this.ysub == 0) {
            super.setPixel(int0 / this.xsub, int1 / this.ysub, ints);
        }
    }

    @Override
    public void getPixel(int var1, int var2, int[] var3) {
        throw new UnsupportedOperationException();
    }
}
