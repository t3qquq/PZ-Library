// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.evildevil.engines.bubble.texture;

final class DDSurfaceDesc2 implements DDSurface {
    private final String DDS_IDENTIFIER = "DDS ";
    protected long identifier = 0L;
    private String identifierString = "";
    protected long size = 0L;
    protected long flags = 0L;
    protected long height = 0L;
    protected long width = 0L;
    protected long pitchOrLinearSize = 0L;
    protected long depth = 0L;
    protected long mipMapCount = 0L;
    protected long reserved = 0L;
    private DDPixelFormat pixelFormat = null;
    private DDSCaps2 caps2 = null;
    protected int reserved2 = 0;

    public DDSurfaceDesc2() {
        this.pixelFormat = new DDPixelFormat();
        this.caps2 = new DDSCaps2();
    }

    public void setIdentifier(long long0) throws TextureFormatException {
        this.identifier = long0;
        this.createIdentifierString();
    }

    private void createIdentifierString() throws TextureFormatException {
        byte[] bytes = new byte[]{(byte)this.identifier, (byte)(this.identifier >> 8), (byte)(this.identifier >> 16), (byte)(this.identifier >> 24)};
        this.identifierString = new String(bytes);
        if (!this.identifierString.equalsIgnoreCase("DDS ")) {
            throw new TextureFormatException("The DDS Identifier is wrong. Have to be \"DDS \"!");
        }
    }

    public void setSize(long long0) throws TextureFormatException {
        if (long0 != 124L) {
            throw new TextureFormatException("Wrong DDSurfaceDesc2 size. DDSurfaceDesc2 size must be 124!");
        } else {
            this.size = long0;
        }
    }

    public void setFlags(long long0) throws TextureFormatException {
        this.flags = long0;
        if ((long0 & 1L) != 1L || (long0 & 4096L) != 4096L || (long0 & 4L) != 4L || (long0 & 2L) != 2L) {
            throw new TextureFormatException(
                "One or more required flag bits are set wrong\nflags have to include \"DDSD_CAPS, DDSD_PIXELFORMAT, DDSD_WIDTH, DDSD_HEIGHT\""
            );
        }
    }

    public void setHeight(long long0) {
        this.height = Math.abs(long0);
    }

    public void setWidth(long long0) {
        this.width = long0;
    }

    public void setPitchOrLinearSize(long long0) {
        this.pitchOrLinearSize = long0;
        this.pitchOrLinearSize = (this.width + 3L) / 4L * ((this.height + 3L) / 4L) * 16L;
        if (this.pitchOrLinearSize > 1000000L) {
            this.pitchOrLinearSize = (this.width + 3L) / 4L * ((this.height + 3L) / 4L) * 16L;
        }
    }

    public void setDepth(long long0) {
        this.depth = long0;
    }

    public void setMipMapCount(long long0) {
        this.mipMapCount = long0;
    }

    public void setDDPixelFormat(DDPixelFormat dDPixelFormat) throws NullPointerException {
        if (dDPixelFormat == null) {
            throw new NullPointerException("DDPixelFormat can't be null. DDSurfaceDesc2 needs a valid DDPixelFormat.");
        } else {
            this.pixelFormat = dDPixelFormat;
        }
    }

    public DDPixelFormat getDDPixelformat() {
        return this.pixelFormat;
    }

    public void setDDSCaps2(DDSCaps2 dDSCaps2) throws NullPointerException {
        if (dDSCaps2 == null) {
            throw new NullPointerException("DDSCaps can't be null. DDSurfaceDesc2 needs a valid DDSCaps2.");
        } else {
            this.caps2 = dDSCaps2;
        }
    }

    public DDSCaps2 getDDSCaps2() {
        return this.caps2;
    }
}
