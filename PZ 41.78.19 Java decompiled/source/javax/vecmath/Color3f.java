// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.awt.Color;
import java.io.Serializable;

public class Color3f extends Tuple3f implements Serializable {
    static final long serialVersionUID = -1861792981817493659L;

    public Color3f(float float0, float float1, float float2) {
        super(float0, float1, float2);
    }

    public Color3f(float[] floats) {
        super(floats);
    }

    public Color3f(Color3f color3f1) {
        super(color3f1);
    }

    public Color3f(Tuple3f tuple3f) {
        super(tuple3f);
    }

    public Color3f(Tuple3d tuple3d) {
        super(tuple3d);
    }

    public Color3f(Color color) {
        super(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F);
    }

    public Color3f() {
    }

    public final void set(Color color) {
        this.x = color.getRed() / 255.0F;
        this.y = color.getGreen() / 255.0F;
        this.z = color.getBlue() / 255.0F;
    }

    public final Color get() {
        int int0 = Math.round(this.x * 255.0F);
        int int1 = Math.round(this.y * 255.0F);
        int int2 = Math.round(this.z * 255.0F);
        return new Color(int0, int1, int2);
    }
}
