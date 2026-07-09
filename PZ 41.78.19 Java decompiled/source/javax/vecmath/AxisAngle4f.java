// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class AxisAngle4f implements Serializable, Cloneable {
    static final long serialVersionUID = -163246355858070601L;
    public float x;
    public float y;
    public float z;
    public float angle;
    static final double EPS = 1.0E-6;

    public AxisAngle4f(float float0, float float1, float float2, float float3) {
        this.x = float0;
        this.y = float1;
        this.z = float2;
        this.angle = float3;
    }

    public AxisAngle4f(float[] floats) {
        this.x = floats[0];
        this.y = floats[1];
        this.z = floats[2];
        this.angle = floats[3];
    }

    public AxisAngle4f(AxisAngle4f axisAngle4f1) {
        this.x = axisAngle4f1.x;
        this.y = axisAngle4f1.y;
        this.z = axisAngle4f1.z;
        this.angle = axisAngle4f1.angle;
    }

    public AxisAngle4f(AxisAngle4d axisAngle4d) {
        this.x = (float)axisAngle4d.x;
        this.y = (float)axisAngle4d.y;
        this.z = (float)axisAngle4d.z;
        this.angle = (float)axisAngle4d.angle;
    }

    public AxisAngle4f(Vector3f vector3f, float float0) {
        this.x = vector3f.x;
        this.y = vector3f.y;
        this.z = vector3f.z;
        this.angle = float0;
    }

    public AxisAngle4f() {
        this.x = 0.0F;
        this.y = 0.0F;
        this.z = 1.0F;
        this.angle = 0.0F;
    }

    public final void set(float float0, float float1, float float2, float float3) {
        this.x = float0;
        this.y = float1;
        this.z = float2;
        this.angle = float3;
    }

    public final void set(float[] floats) {
        this.x = floats[0];
        this.y = floats[1];
        this.z = floats[2];
        this.angle = floats[3];
    }

    public final void set(AxisAngle4f axisAngle4f0) {
        this.x = axisAngle4f0.x;
        this.y = axisAngle4f0.y;
        this.z = axisAngle4f0.z;
        this.angle = axisAngle4f0.angle;
    }

    public final void set(AxisAngle4d axisAngle4d) {
        this.x = (float)axisAngle4d.x;
        this.y = (float)axisAngle4d.y;
        this.z = (float)axisAngle4d.z;
        this.angle = (float)axisAngle4d.angle;
    }

    public final void set(Vector3f vector3f, float float0) {
        this.x = vector3f.x;
        this.y = vector3f.y;
        this.z = vector3f.z;
        this.angle = float0;
    }

    public final void get(float[] floats) {
        floats[0] = this.x;
        floats[1] = this.y;
        floats[2] = this.z;
        floats[3] = this.angle;
    }

    public final void set(Quat4f quat4f) {
        double double0 = quat4f.x * quat4f.x + quat4f.y * quat4f.y + quat4f.z * quat4f.z;
        if (double0 > 1.0E-6) {
            double0 = Math.sqrt(double0);
            double double1 = 1.0 / double0;
            this.x = (float)(quat4f.x * double1);
            this.y = (float)(quat4f.y * double1);
            this.z = (float)(quat4f.z * double1);
            this.angle = (float)(2.0 * Math.atan2(double0, quat4f.w));
        } else {
            this.x = 0.0F;
            this.y = 1.0F;
            this.z = 0.0F;
            this.angle = 0.0F;
        }
    }

    public final void set(Quat4d quat4d) {
        double double0 = quat4d.x * quat4d.x + quat4d.y * quat4d.y + quat4d.z * quat4d.z;
        if (double0 > 1.0E-6) {
            double0 = Math.sqrt(double0);
            double double1 = 1.0 / double0;
            this.x = (float)(quat4d.x * double1);
            this.y = (float)(quat4d.y * double1);
            this.z = (float)(quat4d.z * double1);
            this.angle = (float)(2.0 * Math.atan2(double0, quat4d.w));
        } else {
            this.x = 0.0F;
            this.y = 1.0F;
            this.z = 0.0F;
            this.angle = 0.0F;
        }
    }

    public final void set(Matrix4f matrix4f) {
        Matrix3f matrix3f = new Matrix3f();
        matrix4f.get(matrix3f);
        this.x = matrix3f.m21 - matrix3f.m12;
        this.y = matrix3f.m02 - matrix3f.m20;
        this.z = matrix3f.m10 - matrix3f.m01;
        double double0 = this.x * this.x + this.y * this.y + this.z * this.z;
        if (double0 > 1.0E-6) {
            double0 = Math.sqrt(double0);
            double double1 = 0.5 * double0;
            double double2 = 0.5 * (matrix3f.m00 + matrix3f.m11 + matrix3f.m22 - 1.0);
            this.angle = (float)Math.atan2(double1, double2);
            double double3 = 1.0 / double0;
            this.x = (float)(this.x * double3);
            this.y = (float)(this.y * double3);
            this.z = (float)(this.z * double3);
        } else {
            this.x = 0.0F;
            this.y = 1.0F;
            this.z = 0.0F;
            this.angle = 0.0F;
        }
    }

    public final void set(Matrix4d matrix4d) {
        Matrix3d matrix3d = new Matrix3d();
        matrix4d.get(matrix3d);
        this.x = (float)(matrix3d.m21 - matrix3d.m12);
        this.y = (float)(matrix3d.m02 - matrix3d.m20);
        this.z = (float)(matrix3d.m10 - matrix3d.m01);
        double double0 = this.x * this.x + this.y * this.y + this.z * this.z;
        if (double0 > 1.0E-6) {
            double0 = Math.sqrt(double0);
            double double1 = 0.5 * double0;
            double double2 = 0.5 * (matrix3d.m00 + matrix3d.m11 + matrix3d.m22 - 1.0);
            this.angle = (float)Math.atan2(double1, double2);
            double double3 = 1.0 / double0;
            this.x = (float)(this.x * double3);
            this.y = (float)(this.y * double3);
            this.z = (float)(this.z * double3);
        } else {
            this.x = 0.0F;
            this.y = 1.0F;
            this.z = 0.0F;
            this.angle = 0.0F;
        }
    }

    public final void set(Matrix3f matrix3f) {
        this.x = matrix3f.m21 - matrix3f.m12;
        this.y = matrix3f.m02 - matrix3f.m20;
        this.z = matrix3f.m10 - matrix3f.m01;
        double double0 = this.x * this.x + this.y * this.y + this.z * this.z;
        if (double0 > 1.0E-6) {
            double0 = Math.sqrt(double0);
            double double1 = 0.5 * double0;
            double double2 = 0.5 * (matrix3f.m00 + matrix3f.m11 + matrix3f.m22 - 1.0);
            this.angle = (float)Math.atan2(double1, double2);
            double double3 = 1.0 / double0;
            this.x = (float)(this.x * double3);
            this.y = (float)(this.y * double3);
            this.z = (float)(this.z * double3);
        } else {
            this.x = 0.0F;
            this.y = 1.0F;
            this.z = 0.0F;
            this.angle = 0.0F;
        }
    }

    public final void set(Matrix3d matrix3d) {
        this.x = (float)(matrix3d.m21 - matrix3d.m12);
        this.y = (float)(matrix3d.m02 - matrix3d.m20);
        this.z = (float)(matrix3d.m10 - matrix3d.m01);
        double double0 = this.x * this.x + this.y * this.y + this.z * this.z;
        if (double0 > 1.0E-6) {
            double0 = Math.sqrt(double0);
            double double1 = 0.5 * double0;
            double double2 = 0.5 * (matrix3d.m00 + matrix3d.m11 + matrix3d.m22 - 1.0);
            this.angle = (float)Math.atan2(double1, double2);
            double double3 = 1.0 / double0;
            this.x = (float)(this.x * double3);
            this.y = (float)(this.y * double3);
            this.z = (float)(this.z * double3);
        } else {
            this.x = 0.0F;
            this.y = 1.0F;
            this.z = 0.0F;
            this.angle = 0.0F;
        }
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.angle + ")";
    }

    public boolean equals(AxisAngle4f axisAngle4f0) {
        try {
            return this.x == axisAngle4f0.x && this.y == axisAngle4f0.y && this.z == axisAngle4f0.z && this.angle == axisAngle4f0.angle;
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    @Override
    public boolean equals(Object object) {
        try {
            AxisAngle4f axisAngle4f0 = (AxisAngle4f)object;
            return this.x == axisAngle4f0.x && this.y == axisAngle4f0.y && this.z == axisAngle4f0.z && this.angle == axisAngle4f0.angle;
        } catch (NullPointerException nullPointerException) {
            return false;
        } catch (ClassCastException classCastException) {
            return false;
        }
    }

    public boolean epsilonEquals(AxisAngle4f axisAngle4f0, float float1) {
        float float0 = this.x - axisAngle4f0.x;
        if ((float0 < 0.0F ? -float0 : float0) > float1) {
            return false;
        } else {
            float0 = this.y - axisAngle4f0.y;
            if ((float0 < 0.0F ? -float0 : float0) > float1) {
                return false;
            } else {
                float0 = this.z - axisAngle4f0.z;
                if ((float0 < 0.0F ? -float0 : float0) > float1) {
                    return false;
                } else {
                    float0 = this.angle - axisAngle4f0.angle;
                    return !((float0 < 0.0F ? -float0 : float0) > float1);
                }
            }
        }
    }

    @Override
    public int hashCode() {
        long long0 = 1L;
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.x);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.y);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.z);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.angle);
        return (int)(long0 ^ long0 >> 32);
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new InternalError();
        }
    }

    public final float getAngle() {
        return this.angle;
    }

    public final void setAngle(float float0) {
        this.angle = float0;
    }

    public final float getX() {
        return this.x;
    }

    public final void setX(float float0) {
        this.x = float0;
    }

    public final float getY() {
        return this.y;
    }

    public final void setY(float float0) {
        this.y = float0;
    }

    public final float getZ() {
        return this.z;
    }

    public final void setZ(float float0) {
        this.z = float0;
    }
}
