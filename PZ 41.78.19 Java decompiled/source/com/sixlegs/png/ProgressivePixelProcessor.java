// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.sixlegs.png;

final class ProgressivePixelProcessor extends PixelProcessor {
    private final PixelProcessor pp;
    private final int imgWidth;
    private final int imgHeight;
    private final Destination dst;
    private final int samples;
    private final int[] pixels;

    public ProgressivePixelProcessor(Destination destination, PixelProcessor pixelProcessor, int int0, int int1) {
        this.pp = pixelProcessor;
        this.imgWidth = int0;
        this.imgHeight = int1;
        this.dst = destination;
        this.samples = destination.getRaster().getNumBands();
        this.pixels = new int[this.samples * 8];
    }

    @Override
    public boolean process(int[] ints, int int0, int int1, int int2, int int3, int int4) {
        this.pp.process(ints, int0, int1, int2, int3, int4);
        int int5 = int1 - int0;
        if (int5 > 1 || int1 > 1) {
            int int6 = Math.min(int3 + int1, this.imgHeight);
            int int7 = 0;

            for (int int8 = int0; int7 < int4; int7++) {
                this.dst.getPixel(int8, int3, this.pixels);
                int int9 = Math.min(int8 + int5, this.imgWidth);
                int int10 = int9 - int8;
                int int11 = this.samples;

                for (int int12 = int10 * this.samples; int11 < int12; int11++) {
                    this.pixels[int11] = this.pixels[int11 - this.samples];
                }

                for (int int13 = int3; int13 < int6; int13++) {
                    this.dst.setPixels(int8, int13, int10, this.pixels);
                }

                int8 += int1;
            }
        }

        return true;
    }
}
