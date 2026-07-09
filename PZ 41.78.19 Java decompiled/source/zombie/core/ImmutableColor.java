// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core;

import zombie.core.math.PZMath;

public final class ImmutableColor {
    public static final ImmutableColor transparent = new ImmutableColor(0.0F, 0.0F, 0.0F, 0.0F);
    public static final ImmutableColor white = new ImmutableColor(1.0F, 1.0F, 1.0F, 1.0F);
    public static final ImmutableColor yellow = new ImmutableColor(1.0F, 1.0F, 0.0F, 1.0F);
    public static final ImmutableColor red = new ImmutableColor(1.0F, 0.0F, 0.0F, 1.0F);
    public static final ImmutableColor purple = new ImmutableColor(196.0F, 0.0F, 171.0F);
    public static final ImmutableColor blue = new ImmutableColor(0.0F, 0.0F, 1.0F, 1.0F);
    public static final ImmutableColor green = new ImmutableColor(0.0F, 1.0F, 0.0F, 1.0F);
    public static final ImmutableColor black = new ImmutableColor(0.0F, 0.0F, 0.0F, 1.0F);
    public static final ImmutableColor gray = new ImmutableColor(0.5F, 0.5F, 0.5F, 1.0F);
    public static final ImmutableColor cyan = new ImmutableColor(0.0F, 1.0F, 1.0F, 1.0F);
    public static final ImmutableColor darkGray = new ImmutableColor(0.3F, 0.3F, 0.3F, 1.0F);
    public static final ImmutableColor lightGray = new ImmutableColor(0.7F, 0.7F, 0.7F, 1.0F);
    public static final ImmutableColor pink = new ImmutableColor(255, 175, 175, 255);
    public static final ImmutableColor orange = new ImmutableColor(255, 200, 0, 255);
    public static final ImmutableColor magenta = new ImmutableColor(255, 0, 255, 255);
    public static final ImmutableColor darkGreen = new ImmutableColor(22, 113, 20, 255);
    public static final ImmutableColor lightGreen = new ImmutableColor(55, 148, 53, 255);
    public final float a;
    public final float b;
    public final float g;
    public final float r;

    public ImmutableColor(ImmutableColor color) {
        if (color == null) {
            this.r = 0.0F;
            this.g = 0.0F;
            this.b = 0.0F;
            this.a = 1.0F;
        } else {
            this.r = color.r;
            this.g = color.g;
            this.b = color.b;
            this.a = color.a;
        }
    }

    public ImmutableColor(Color color) {
        if (color == null) {
            this.r = 0.0F;
            this.g = 0.0F;
            this.b = 0.0F;
            this.a = 1.0F;
        } else {
            this.r = color.r;
            this.g = color.g;
            this.b = color.b;
            this.a = color.a;
        }
    }

    public Color toMutableColor() {
        return new Color(this.r, this.g, this.b, this.a);
    }

    public ImmutableColor(float _r, float _g, float _b) {
        this.r = PZMath.clamp(_r, 0.0F, 1.0F);
        this.g = PZMath.clamp(_g, 0.0F, 1.0F);
        this.b = PZMath.clamp(_b, 0.0F, 1.0F);
        this.a = 1.0F;
    }

    public ImmutableColor(float _r, float _g, float _b, float _a) {
        this.r = Math.min(_r, 1.0F);
        this.g = Math.min(_g, 1.0F);
        this.b = Math.min(_b, 1.0F);
        this.a = Math.min(_a, 1.0F);
    }

    public ImmutableColor(Color A, Color B, float delta) {
        float float0 = (B.r - A.r) * delta;
        float float1 = (B.g - A.g) * delta;
        float float2 = (B.b - A.b) * delta;
        float float3 = (B.a - A.a) * delta;
        this.r = A.r + float0;
        this.g = A.g + float1;
        this.b = A.b + float2;
        this.a = A.a + float3;
    }

    public ImmutableColor(int _r, int _g, int _b) {
        this.r = _r / 255.0F;
        this.g = _g / 255.0F;
        this.b = _b / 255.0F;
        this.a = 1.0F;
    }

    public ImmutableColor(int _r, int _g, int _b, int _a) {
        this.r = _r / 255.0F;
        this.g = _g / 255.0F;
        this.b = _b / 255.0F;
        this.a = _a / 255.0F;
    }

    public ImmutableColor(int value) {
        int int0 = (value & 0xFF0000) >> 16;
        int int1 = (value & 0xFF00) >> 8;
        int int2 = value & 0xFF;
        int int3 = (value & 0xFF000000) >> 24;
        if (int3 < 0) {
            int3 += 256;
        }

        if (int3 == 0) {
            int3 = 255;
        }

        this.r = int2 / 255.0F;
        this.g = int1 / 255.0F;
        this.b = int0 / 255.0F;
        this.a = int3 / 255.0F;
    }

    public static ImmutableColor random() {
        float float0 = Rand.Next(0.0F, 1.0F);
        float float1 = Rand.Next(0.0F, 0.6F);
        float float2 = Rand.Next(0.0F, 0.9F);
        Color color = Color.HSBtoRGB(float0, float1, float2);
        return new ImmutableColor(color);
    }

    public static ImmutableColor decode(String nm) {
        return new ImmutableColor(Integer.decode(nm));
    }

    public ImmutableColor add(ImmutableColor c) {
        return new ImmutableColor(this.r + c.r, this.g + c.g, this.b + c.b, this.a + c.a);
    }

    public ImmutableColor brighter() {
        return this.brighter(0.2F);
    }

    public ImmutableColor brighter(float scale) {
        return new ImmutableColor(this.r + scale, this.g + scale, this.b + scale);
    }

    public ImmutableColor darker() {
        return this.darker(0.5F);
    }

    public ImmutableColor darker(float scale) {
        return new ImmutableColor(this.r - scale, this.g - scale, this.b - scale);
    }

    @Override
    public boolean equals(Object other) {
        return !(other instanceof ImmutableColor immutableColor)
            ? false
            : immutableColor.r == this.r && immutableColor.g == this.g && immutableColor.b == this.b && immutableColor.a == this.a;
    }

    public int getAlphaInt() {
        return (int)(this.a * 255.0F);
    }

    public float getAlphaFloat() {
        return this.a;
    }

    public float getRedFloat() {
        return this.r;
    }

    public float getGreenFloat() {
        return this.g;
    }

    public float getBlueFloat() {
        return this.b;
    }

    public byte getAlphaByte() {
        return (byte)((int)(this.a * 255.0F) & 0xFF);
    }

    public int getBlueInt() {
        return (int)(this.b * 255.0F);
    }

    public byte getBlueByte() {
        return (byte)((int)(this.b * 255.0F) & 0xFF);
    }

    public int getGreenInt() {
        return (int)(this.g * 255.0F);
    }

    public byte getGreenByte() {
        return (byte)((int)(this.g * 255.0F) & 0xFF);
    }

    public int getRedInt() {
        return (int)(this.r * 255.0F);
    }

    public byte getRedByte() {
        return (byte)((int)(this.r * 255.0F) & 0xFF);
    }

    @Override
    public int hashCode() {
        return (int)(this.r + this.g + this.b + this.a) * 255;
    }

    public ImmutableColor multiply(Color c) {
        return new ImmutableColor(this.r * c.r, this.g * c.g, this.b * c.b, this.a * c.a);
    }

    public ImmutableColor scale(float value) {
        return new ImmutableColor(this.r * value, this.g * value, this.b * value, this.a * value);
    }

    @Override
    public String toString() {
        return "ImmutableColor (" + this.r + "," + this.g + "," + this.b + "," + this.a + ")";
    }

    public ImmutableColor interp(ImmutableColor to, float delta) {
        float float0 = to.r - this.r;
        float float1 = to.g - this.g;
        float float2 = to.b - this.b;
        float float3 = to.a - this.a;
        float0 *= delta;
        float1 *= delta;
        float2 *= delta;
        float3 *= delta;
        return new ImmutableColor(this.r + float0, this.g + float1, this.b + float2, this.a + float3);
    }

    public static Integer[] HSBtoRGB(float hue, float saturation, float brightness) {
        int int0 = 0;
        int int1 = 0;
        int int2 = 0;
        if (saturation == 0.0F) {
            int0 = int1 = int2 = (int)(brightness * 255.0F + 0.5F);
        } else {
            float float0 = (hue - (float)Math.floor(hue)) * 6.0F;
            float float1 = float0 - (float)Math.floor(float0);
            float float2 = brightness * (1.0F - saturation);
            float float3 = brightness * (1.0F - saturation * float1);
            float float4 = brightness * (1.0F - saturation * (1.0F - float1));
            switch ((int)float0) {
                case 0:
                    int0 = (int)(brightness * 255.0F + 0.5F);
                    int1 = (int)(float4 * 255.0F + 0.5F);
                    int2 = (int)(float2 * 255.0F + 0.5F);
                    break;
                case 1:
                    int0 = (int)(float3 * 255.0F + 0.5F);
                    int1 = (int)(brightness * 255.0F + 0.5F);
                    int2 = (int)(float2 * 255.0F + 0.5F);
                    break;
                case 2:
                    int0 = (int)(float2 * 255.0F + 0.5F);
                    int1 = (int)(brightness * 255.0F + 0.5F);
                    int2 = (int)(float4 * 255.0F + 0.5F);
                    break;
                case 3:
                    int0 = (int)(float2 * 255.0F + 0.5F);
                    int1 = (int)(float3 * 255.0F + 0.5F);
                    int2 = (int)(brightness * 255.0F + 0.5F);
                    break;
                case 4:
                    int0 = (int)(float4 * 255.0F + 0.5F);
                    int1 = (int)(float2 * 255.0F + 0.5F);
                    int2 = (int)(brightness * 255.0F + 0.5F);
                    break;
                case 5:
                    int0 = (int)(brightness * 255.0F + 0.5F);
                    int1 = (int)(float2 * 255.0F + 0.5F);
                    int2 = (int)(float3 * 255.0F + 0.5F);
            }
        }

        return new Integer[]{int0, int1, int2};
    }
}
