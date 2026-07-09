// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.sixlegs.png;

import java.awt.Rectangle;
import java.awt.image.WritableRaster;

final class SourceRegionDestination extends Destination {
    private final Destination dst;
    private final int xoff;
    private final int yoff;
    private final int xlen;
    private final int ylen;
    private final int samples;

    public SourceRegionDestination(Destination destination, Rectangle rectangle) {
        this.dst = destination;
        this.xoff = rectangle.x;
        this.yoff = rectangle.y;
        this.xlen = rectangle.width;
        this.ylen = rectangle.height;
        this.samples = destination.getRaster().getNumBands();
    }

    @Override
    public void setPixels(int int2, int int0, int int4, int[] ints) {
        if (int0 >= this.yoff && int0 < this.yoff + this.ylen) {
            int int1 = Math.max(int2, this.xoff);
            int int3 = Math.min(int2 + int4, this.xoff + this.xlen) - int1;
            if (int3 > 0) {
                if (int1 > int2) {
                    System.arraycopy(ints, int1 * this.samples, ints, 0, int3 * this.samples);
                }

                this.dst.setPixels(int1 - this.xoff, int0 - this.yoff, int3, ints);
            }
        }
    }

    @Override
    public void setPixel(int int0, int int1, int[] ints) {
        int0 -= this.xoff;
        int1 -= this.yoff;
        if (int0 >= 0 && int1 >= 0 && int0 < this.xlen && int1 < this.ylen) {
            this.dst.setPixel(int0, int1, ints);
        }
    }

    @Override
    public void getPixel(int var1, int var2, int[] var3) {
        throw new UnsupportedOperationException();
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
