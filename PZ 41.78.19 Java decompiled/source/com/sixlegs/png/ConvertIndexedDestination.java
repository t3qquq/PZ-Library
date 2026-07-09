// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.sixlegs.png;

import java.awt.image.ComponentColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

class ConvertIndexedDestination extends Destination {
    private final Destination dst;
    private final IndexColorModel srcColorModel;
    private final int srcSamples;
    private final int dstSamples;
    private final int sampleDiff;
    private final int[] row;

    public ConvertIndexedDestination(Destination destination, int int0, IndexColorModel indexColorModel, ComponentColorModel componentColorModel) {
        this.dst = destination;
        this.srcColorModel = indexColorModel;
        this.srcSamples = indexColorModel.getNumComponents();
        this.dstSamples = componentColorModel.getNumComponents();
        this.sampleDiff = this.srcSamples - this.dstSamples;
        this.row = new int[int0 * this.dstSamples + this.sampleDiff];
    }

    @Override
    public void setPixels(int int3, int int4, int int1, int[] ints) {
        int int0 = int1 - 1;

        for (int int2 = this.dstSamples * int0; int0 >= 0; int2 -= this.dstSamples) {
            this.srcColorModel.getComponents(ints[int0], this.row, int2);
            int0--;
        }

        if (this.sampleDiff != 0) {
            System.arraycopy(this.row, this.sampleDiff, this.row, 0, this.dstSamples * int1);
        }

        this.dst.setPixels(int3, int4, int1, this.row);
    }

    @Override
    public void setPixel(int int0, int int1, int[] ints) {
        this.setPixels(int0, int1, 1, ints);
    }

    @Override
    public void getPixel(int var1, int var2, int[] var3) {
        throw new UnsupportedOperationException("implement me");
    }

    @Override
    public WritableRaster getRaster() {
        return this.dst.getRaster();
    }

    @Override
    public int getSourceWidth() {
        return this.dst.getSourceWidth();
    }

    @Override
    public void done() {
        this.dst.done();
    }
}
