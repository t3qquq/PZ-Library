// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.opengl;

public final class DisplayMode {
    private final int width;
    private final int height;
    private final int bpp;
    private final int freq;
    private final boolean fullscreen;

    public DisplayMode(int int0, int int1) {
        this(int0, int1, 0, 0, false);
    }

    DisplayMode(int int0, int int1, int int2, int int3) {
        this(int0, int1, int2, int3, true);
    }

    private DisplayMode(int int0, int int1, int int2, int int3, boolean boolean0) {
        this.width = int0;
        this.height = int1;
        this.bpp = int2;
        this.freq = int3;
        this.fullscreen = boolean0;
    }

    public boolean isFullscreenCapable() {
        return this.fullscreen;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getBitsPerPixel() {
        return this.bpp;
    }

    public int getFrequency() {
        return this.freq;
    }

    @Override
    public boolean equals(Object object) {
        return object != null && object instanceof DisplayMode displayMode1
            ? displayMode1.width == this.width && displayMode1.height == this.height && displayMode1.bpp == this.bpp && displayMode1.freq == this.freq
            : false;
    }

    @Override
    public int hashCode() {
        return this.width ^ this.height ^ this.freq ^ this.bpp;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(32);
        stringBuilder.append(this.width);
        stringBuilder.append(" x ");
        stringBuilder.append(this.height);
        stringBuilder.append(" x ");
        stringBuilder.append(this.bpp);
        stringBuilder.append(" @");
        stringBuilder.append(this.freq);
        stringBuilder.append("Hz");
        return stringBuilder.toString();
    }
}
