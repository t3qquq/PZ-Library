// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.awt.Color;
import java.io.Serializable;

public class Color4f extends Tuple4f implements Serializable {
    static final long serialVersionUID = 8577680141580006740L;

    public Color4f(float float0, float float1, float float2, float float3) {
        super(float0, float1, float2, float3);
    }

    public Color4f(float[] floats) {
        super(floats);
    }

    public Color4f(Color4f color4f1) {
        super(color4f1);
    }

    public Color4f(Tuple4f tuple4f) {
        super(tuple4f);
    }

    public Color4f(Tuple4d tuple4d) {
        super(tuple4d);
    }

    public Color4f(Color color) {
        super(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
    }

    public Color4f() {
    }

    public final void set(Color color) {
        this.x = color.getRed() / 255.0F;
        this.y = color.getGreen() / 255.0F;
        this.z = color.getBlue() / 255.0F;
        this.w = color.getAlpha() / 255.0F;
    }

    public final Color get() {
        int int0 = Math.round(this.x * 255.0F);
        int int1 = Math.round(this.y * 255.0F);
        int int2 = Math.round(this.z * 255.0F);
        int int3 = Math.round(this.w * 255.0F);
        return new Color(int0, int1, int2, int3);
    }
}
