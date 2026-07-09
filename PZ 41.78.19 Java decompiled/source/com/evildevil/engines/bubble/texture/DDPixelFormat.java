// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.evildevil.engines.bubble.texture;

final class DDPixelFormat implements DDSurface {
    protected long size = 0L;
    protected long flags = 0L;
    protected long fourCC = 0L;
    private String fourCCString = "";
    protected long rgbBitCount = 0L;
    protected long rBitMask = 0L;
    protected long gBitMask = 0L;
    protected long bBitMask = 0L;
    protected long rgbAlphaBitMask = 0L;
    protected boolean isCompressed = true;

    public DDPixelFormat() {
    }

    public void setSize(long long0) throws TextureFormatException {
        if (long0 != 32L) {
            throw new TextureFormatException("Wrong DDPixelFormat size. DDPixelFormat size must be 32!");
        } else {
            this.size = long0;
        }
    }

    public void setFlags(long long0) {
        this.flags = long0;
        if ((long0 & 64L) == 64L) {
            this.isCompressed = false;
        } else if ((long0 & 4L) == 4L) {
            this.isCompressed = true;
        }
    }

    public void setFourCC(long long0) {
        this.fourCC = long0;
        if (this.isCompressed) {
            this.createFourCCString();
        }
    }

    private void createFourCCString() {
        byte[] bytes = new byte[]{(byte)this.fourCC, (byte)(this.fourCC >> 8), (byte)(this.fourCC >> 16), (byte)(this.fourCC >> 24)};
        this.fourCCString = new String(bytes);
    }

    public String getFourCCString() {
        return this.fourCCString;
    }

    public void setRGBBitCount(long long0) {
        this.rgbAlphaBitMask = long0;
    }

    public void setRBitMask(long long0) {
        this.rBitMask = long0;
    }

    public void setGBitMask(long long0) {
        this.gBitMask = long0;
    }

    public void setBBitMask(long long0) {
        this.bBitMask = long0;
    }

    public void setRGBAlphaBitMask(long long0) {
        this.rgbAlphaBitMask = long0;
    }
}
