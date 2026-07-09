// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.sixlegs.png;

import java.awt.image.BufferedImage;

final class ProgressUpdater extends PixelProcessor {
    private static final int STEP_PERCENT = 5;
    private final PngImage png;
    private final BufferedImage image;
    private final PixelProcessor pp;
    private final int total;
    private final int step;
    private int count;
    private int mod;

    public ProgressUpdater(PngImage pngImage, BufferedImage bufferedImage, PixelProcessor pixelProcessor) {
        this.png = pngImage;
        this.image = bufferedImage;
        this.pp = pixelProcessor;
        this.total = pngImage.getWidth() * pngImage.getHeight();
        this.step = Math.max(1, this.total * 5 / 100);
    }

    @Override
    public boolean process(int[] ints, int int0, int int1, int int2, int int3, int int4) {
        boolean boolean0 = this.pp.process(ints, int0, int1, int2, int3, int4);
        this.mod += int4;
        this.count += int4;
        if (this.mod > this.step) {
            this.mod = this.mod % this.step;
            boolean0 = boolean0 && this.png.handleProgress(this.image, 100.0F * this.count / this.total);
        }

        return boolean0;
    }
}
