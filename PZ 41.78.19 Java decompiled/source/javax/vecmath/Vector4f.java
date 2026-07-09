// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class Vector4f extends Tuple4f implements Serializable {
    static final long serialVersionUID = 8749319902347760659L;

    public Vector4f(float float0, float float1, float float2, float float3) {
        super(float0, float1, float2, float3);
    }

    public Vector4f(float[] floats) {
        super(floats);
    }

    public Vector4f(Vector4f vector4f1) {
        super(vector4f1);
    }

    public Vector4f(Vector4d vector4d) {
        super(vector4d);
    }

    public Vector4f(Tuple4f tuple4f) {
        super(tuple4f);
    }

    public Vector4f(Tuple4d tuple4d) {
        super(tuple4d);
    }

    public Vector4f(Tuple3f tuple3f) {
        super(tuple3f.x, tuple3f.y, tuple3f.z, 0.0F);
    }

    public Vector4f() {
    }

    public final void set(Tuple3f tuple3f) {
        this.x = tuple3f.x;
        this.y = tuple3f.y;
        this.z = tuple3f.z;
        this.w = 0.0F;
    }

    public final float length() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
    }

    public final float lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
    }

    public final float dot(Vector4f vector4f0) {
        return this.x * vector4f0.x + this.y * vector4f0.y + this.z * vector4f0.z + this.w * vector4f0.w;
    }

    public final void normalize(Vector4f vector4f0) {
        float float0 = (float)(1.0 / Math.sqrt(vector4f0.x * vector4f0.x + vector4f0.y * vector4f0.y + vector4f0.z * vector4f0.z + vector4f0.w * vector4f0.w));
        this.x = vector4f0.x * float0;
        this.y = vector4f0.y * float0;
        this.z = vector4f0.z * float0;
        this.w = vector4f0.w * float0;
    }

    public final void normalize() {
        float float0 = (float)(1.0 / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w));
        this.x *= float0;
        this.y *= float0;
        this.z *= float0;
        this.w *= float0;
    }

    public final float angle(Vector4f vector4f0) {
        double double0 = this.dot(vector4f0) / (this.length() * vector4f0.length());
        if (double0 < -1.0) {
            double0 = -1.0;
        }

        if (double0 > 1.0) {
            double0 = 1.0;
        }

        return (float)Math.acos(double0);
    }
}
