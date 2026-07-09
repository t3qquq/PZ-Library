// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.opengl;

public final class PixelFormat implements PixelFormatLWJGL {
    private int bpp;
    private int alpha;
    private int depth;
    private int stencil;
    private int samples;
    private int colorSamples;
    private int num_aux_buffers;
    private int accum_bpp;
    private int accum_alpha;
    private boolean stereo;
    private boolean floating_point;
    private boolean floating_point_packed;
    private boolean sRGB;

    public PixelFormat() {
        this(0, 8, 0);
    }

    public PixelFormat(int int0, int int1, int int2) {
        this(int0, int1, int2, 0);
    }

    public PixelFormat(int int0, int int1, int int2, int int3) {
        this(0, int0, int1, int2, int3);
    }

    public PixelFormat(int int0, int int1, int int2, int int3, int int4) {
        this(int0, int1, int2, int3, int4, 0, 0, 0, false);
    }

    public PixelFormat(int int0, int int1, int int2, int int3, int int4, int int5, int int6, int int7, boolean boolean0) {
        this(int0, int1, int2, int3, int4, int5, int6, int7, boolean0, false);
    }

    public PixelFormat(int int0, int int1, int int2, int int3, int int4, int int5, int int6, int int7, boolean boolean0, boolean boolean1) {
        this.bpp = int0;
        this.alpha = int1;
        this.depth = int2;
        this.stencil = int3;
        this.samples = int4;
        this.num_aux_buffers = int5;
        this.accum_bpp = int6;
        this.accum_alpha = int7;
        this.stereo = boolean0;
        this.floating_point = boolean1;
        this.floating_point_packed = false;
        this.sRGB = false;
    }

    private PixelFormat(PixelFormat pixelFormat1) {
        this.bpp = pixelFormat1.bpp;
        this.alpha = pixelFormat1.alpha;
        this.depth = pixelFormat1.depth;
        this.stencil = pixelFormat1.stencil;
        this.samples = pixelFormat1.samples;
        this.colorSamples = pixelFormat1.colorSamples;
        this.num_aux_buffers = pixelFormat1.num_aux_buffers;
        this.accum_bpp = pixelFormat1.accum_bpp;
        this.accum_alpha = pixelFormat1.accum_alpha;
        this.stereo = pixelFormat1.stereo;
        this.floating_point = pixelFormat1.floating_point;
        this.floating_point_packed = pixelFormat1.floating_point_packed;
        this.sRGB = pixelFormat1.sRGB;
    }

    public int getBitsPerPixel() {
        return this.bpp;
    }

    public PixelFormat withBitsPerPixel(int int0) {
        if (int0 < 0) {
            throw new IllegalArgumentException("Invalid number of bits per pixel specified: " + int0);
        } else {
            PixelFormat pixelFormat0 = new PixelFormat(this);
            pixelFormat0.bpp = int0;
            return pixelFormat0;
        }
    }

    public int getAlphaBits() {
        return this.alpha;
    }

    public PixelFormat withAlphaBits(int int0) {
        if (int0 < 0) {
            throw new IllegalArgumentException("Invalid number of alpha bits specified: " + int0);
        } else {
            PixelFormat pixelFormat0 = new PixelFormat(this);
            pixelFormat0.alpha = int0;
            return pixelFormat0;
        }
    }

    public int getDepthBits() {
        return this.depth;
    }

    public PixelFormat withDepthBits(int int0) {
        if (int0 < 0) {
            throw new IllegalArgumentException("Invalid number of depth bits specified: " + int0);
        } else {
            PixelFormat pixelFormat0 = new PixelFormat(this);
            pixelFormat0.depth = int0;
            return pixelFormat0;
        }
    }

    public int getStencilBits() {
        return this.stencil;
    }

    public PixelFormat withStencilBits(int int0) {
        if (int0 < 0) {
            throw new IllegalArgumentException("Invalid number of stencil bits specified: " + int0);
        } else {
            PixelFormat pixelFormat0 = new PixelFormat(this);
            pixelFormat0.stencil = int0;
            return pixelFormat0;
        }
    }

    public int getSamples() {
        return this.samples;
    }

    public PixelFormat withSamples(int int0) {
        if (int0 < 0) {
            throw new IllegalArgumentException("Invalid number of samples specified: " + int0);
        } else {
            PixelFormat pixelFormat0 = new PixelFormat(this);
            pixelFormat0.samples = int0;
            return pixelFormat0;
        }
    }

    public PixelFormat withCoverageSamples(int int0) {
        return this.withCoverageSamples(int0, this.samples);
    }

    public PixelFormat withCoverageSamples(int int1, int int0) {
        if (int0 >= 0 && int1 >= 0 && (int0 != 0 || 0 >= int1) && int0 >= int1) {
            PixelFormat pixelFormat0 = new PixelFormat(this);
            pixelFormat0.samples = int0;
            pixelFormat0.colorSamples = int1;
            return pixelFormat0;
        } else {
            throw new IllegalArgumentException("Invalid number of coverage samples specified: " + int0 + " - " + int1);
        }
    }

    public int getAuxBuffers() {
        return this.num_aux_buffers;
    }

    public PixelFormat withAuxBuffers(int int0) {
        if (int0 < 0) {
            throw new IllegalArgumentException("Invalid number of auxiliary buffers specified: " + int0);
        } else {
            PixelFormat pixelFormat0 = new PixelFormat(this);
            pixelFormat0.num_aux_buffers = int0;
            return pixelFormat0;
        }
    }

    public int getAccumulationBitsPerPixel() {
        return this.accum_bpp;
    }

    public PixelFormat withAccumulationBitsPerPixel(int int0) {
        if (int0 < 0) {
            throw new IllegalArgumentException("Invalid number of bits per pixel in the accumulation buffer specified: " + int0);
        } else {
            PixelFormat pixelFormat0 = new PixelFormat(this);
            pixelFormat0.accum_bpp = int0;
            return pixelFormat0;
        }
    }

    public int getAccumulationAlpha() {
        return this.accum_alpha;
    }

    public PixelFormat withAccumulationAlpha(int int0) {
        if (int0 < 0) {
            throw new IllegalArgumentException("Invalid number of alpha bits in the accumulation buffer specified: " + int0);
        } else {
            PixelFormat pixelFormat0 = new PixelFormat(this);
            pixelFormat0.accum_alpha = int0;
            return pixelFormat0;
        }
    }

    public boolean isStereo() {
        return this.stereo;
    }

    public PixelFormat withStereo(boolean boolean0) {
        PixelFormat pixelFormat0 = new PixelFormat(this);
        pixelFormat0.stereo = boolean0;
        return pixelFormat0;
    }

    public boolean isFloatingPoint() {
        return this.floating_point;
    }

    public PixelFormat withFloatingPoint(boolean boolean0) {
        PixelFormat pixelFormat0 = new PixelFormat(this);
        pixelFormat0.floating_point = boolean0;
        if (boolean0) {
            pixelFormat0.floating_point_packed = false;
        }

        return pixelFormat0;
    }

    public PixelFormat withFloatingPointPacked(boolean boolean0) {
        PixelFormat pixelFormat0 = new PixelFormat(this);
        pixelFormat0.floating_point_packed = boolean0;
        if (boolean0) {
            pixelFormat0.floating_point = false;
        }

        return pixelFormat0;
    }

    public boolean isSRGB() {
        return this.sRGB;
    }

    public PixelFormat withSRGB(boolean boolean0) {
        PixelFormat pixelFormat0 = new PixelFormat(this);
        pixelFormat0.sRGB = boolean0;
        return pixelFormat0;
    }
}
