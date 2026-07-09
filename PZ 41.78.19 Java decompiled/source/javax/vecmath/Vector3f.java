// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class Vector3f extends Tuple3f implements Serializable {
    static final long serialVersionUID = -7031930069184524614L;

    public Vector3f(float float0, float float1, float float2) {
        super(float0, float1, float2);
    }

    public Vector3f(float[] floats) {
        super(floats);
    }

    public Vector3f(Vector3f vector3f1) {
        super(vector3f1);
    }

    public Vector3f(Vector3d vector3d) {
        super(vector3d);
    }

    public Vector3f(Tuple3f tuple3f) {
        super(tuple3f);
    }

    public Vector3f(Tuple3d tuple3d) {
        super(tuple3d);
    }

    public Vector3f() {
    }

    public final float lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public final float length() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public final void cross(Vector3f vector3f1, Vector3f vector3f0) {
        float float0 = vector3f1.y * vector3f0.z - vector3f1.z * vector3f0.y;
        float float1 = vector3f0.x * vector3f1.z - vector3f0.z * vector3f1.x;
        this.z = vector3f1.x * vector3f0.y - vector3f1.y * vector3f0.x;
        this.x = float0;
        this.y = float1;
    }

    public final float dot(Vector3f vector3f0) {
        return this.x * vector3f0.x + this.y * vector3f0.y + this.z * vector3f0.z;
    }

    public final void normalize(Vector3f vector3f0) {
        float float0 = (float)(1.0 / Math.sqrt(vector3f0.x * vector3f0.x + vector3f0.y * vector3f0.y + vector3f0.z * vector3f0.z));
        this.x = vector3f0.x * float0;
        this.y = vector3f0.y * float0;
        this.z = vector3f0.z * float0;
    }

    public final void normalize() {
        float float0 = (float)(1.0 / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z));
        this.x *= float0;
        this.y *= float0;
        this.z *= float0;
    }

    public final float angle(Vector3f vector3f0) {
        double double0 = this.dot(vector3f0) / (this.length() * vector3f0.length());
        if (double0 < -1.0) {
            double0 = -1.0;
        }

        if (double0 > 1.0) {
            double0 = 1.0;
        }

        return (float)Math.acos(double0);
    }
}
