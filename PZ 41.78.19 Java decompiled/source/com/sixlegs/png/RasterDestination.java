// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.sixlegs.png;

import java.awt.image.WritableRaster;

class RasterDestination extends Destination {
    protected final WritableRaster raster;
    protected final int sourceWidth;

    public RasterDestination(WritableRaster writableRaster, int int0) {
        this.raster = writableRaster;
        this.sourceWidth = int0;
    }

    @Override
    public void setPixels(int int0, int int1, int int2, int[] ints) {
        this.raster.setPixels(int0, int1, int2, 1, ints);
    }

    @Override
    public void setPixel(int int0, int int1, int[] ints) {
        this.raster.setPixel(int0, int1, ints);
    }

    @Override
    public void getPixel(int int0, int int1, int[] ints) {
        this.raster.getPixel(int0, int1, ints);
    }

    @Override
    public WritableRaster getRaster() {
        return this.raster;
    }

    @Override
    public int getSourceWidth() {
        return this.sourceWidth;
    }

    @Override
    public void done() {
    }
}
