// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

import zombie.core.Color;
import zombie.core.ImmutableColor;

public final class ColorInfo {
    public float a = 1.0F;
    public float b = 1.0F;
    public float g = 1.0F;
    public float r = 1.0F;

    public ColorInfo() {
        this.r = 1.0F;
        this.g = 1.0F;
        this.b = 1.0F;
        this.a = 1.0F;
    }

    public ColorInfo(float R, float G, float B, float A) {
        this.r = R;
        this.g = G;
        this.b = B;
        this.a = A;
    }

    public ColorInfo set(ColorInfo other) {
        this.r = other.r;
        this.g = other.g;
        this.b = other.b;
        this.a = other.a;
        return this;
    }

    public ColorInfo set(float R, float G, float B, float A) {
        this.r = R;
        this.g = G;
        this.b = B;
        this.a = A;
        return this;
    }

    public float getR() {
        return this.r;
    }

    public float getG() {
        return this.g;
    }

    public float getB() {
        return this.b;
    }

    public Color toColor() {
        return new Color(this.r, this.g, this.b, this.a);
    }

    public ImmutableColor toImmutableColor() {
        return new ImmutableColor(this.r, this.g, this.b, this.a);
    }

    public float getA() {
        return this.a;
    }

    public void desaturate(float s) {
        float float0 = this.r * 0.3086F + this.g * 0.6094F + this.b * 0.082F;
        this.r = float0 * s + this.r * (1.0F - s);
        this.g = float0 * s + this.g * (1.0F - s);
        this.b = float0 * s + this.b * (1.0F - s);
    }

    public void interp(ColorInfo to, float delta, ColorInfo dest) {
        float float0 = to.r - this.r;
        float float1 = to.g - this.g;
        float float2 = to.b - this.b;
        float float3 = to.a - this.a;
        float0 *= delta;
        float1 *= delta;
        float2 *= delta;
        float3 *= delta;
        dest.r = this.r + float0;
        dest.g = this.g + float1;
        dest.b = this.b + float2;
        dest.a = this.a + float3;
    }

    @Override
    public String toString() {
        return "Color (" + this.r + "," + this.g + "," + this.b + "," + this.a + ")";
    }
}
