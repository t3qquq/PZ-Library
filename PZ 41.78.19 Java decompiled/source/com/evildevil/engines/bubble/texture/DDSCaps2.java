// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.evildevil.engines.bubble.texture;

final class DDSCaps2 implements DDSurface {
    protected long caps1 = 0L;
    protected long caps2 = 0L;
    protected long reserved = 0L;
    protected boolean isVolumeTexture = false;

    public DDSCaps2() {
    }

    public void setCaps1(long long0) throws TextureFormatException {
        this.caps1 = long0;
        if ((long0 & 4096L) != 4096L) {
            throw new TextureFormatException("DDS file does not contain DDSCAPS_TEXTURE, but it must!");
        }
    }

    public void setCaps2(long long0) {
        this.caps2 = long0;
        if ((long0 & 2097152L) == 2097152L) {
            this.isVolumeTexture = true;
        }
    }
}
