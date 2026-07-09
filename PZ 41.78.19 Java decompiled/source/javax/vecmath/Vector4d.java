// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class Vector4d extends Tuple4d implements Serializable {
    static final long serialVersionUID = 3938123424117448700L;

    public Vector4d(double double0, double double1, double double2, double double3) {
        super(double0, double1, double2, double3);
    }

    public Vector4d(double[] doubles) {
        super(doubles);
    }

    public Vector4d(Vector4d vector4d1) {
        super(vector4d1);
    }

    public Vector4d(Vector4f vector4f) {
        super(vector4f);
    }

    public Vector4d(Tuple4f tuple4f) {
        super(tuple4f);
    }

    public Vector4d(Tuple4d tuple4d) {
        super(tuple4d);
    }

    public Vector4d(Tuple3d tuple3d) {
        super(tuple3d.x, tuple3d.y, tuple3d.z, 0.0);
    }

    public Vector4d() {
    }

    public final void set(Tuple3d tuple3d) {
        this.x = tuple3d.x;
        this.y = tuple3d.y;
        this.z = tuple3d.z;
        this.w = 0.0;
    }

    public final double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
    }

    public final double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
    }

    public final double dot(Vector4d vector4d0) {
        return this.x * vector4d0.x + this.y * vector4d0.y + this.z * vector4d0.z + this.w * vector4d0.w;
    }

    public final void normalize(Vector4d vector4d0) {
        double double0 = 1.0 / Math.sqrt(vector4d0.x * vector4d0.x + vector4d0.y * vector4d0.y + vector4d0.z * vector4d0.z + vector4d0.w * vector4d0.w);
        this.x = vector4d0.x * double0;
        this.y = vector4d0.y * double0;
        this.z = vector4d0.z * double0;
        this.w = vector4d0.w * double0;
    }

    public final void normalize() {
        double double0 = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
        this.x *= double0;
        this.y *= double0;
        this.z *= double0;
        this.w *= double0;
    }

    public final double angle(Vector4d vector4d0) {
        double double0 = this.dot(vector4d0) / (this.length() * vector4d0.length());
        if (double0 < -1.0) {
            double0 = -1.0;
        }

        if (double0 > 1.0) {
            double0 = 1.0;
        }

        return Math.acos(double0);
    }
}
