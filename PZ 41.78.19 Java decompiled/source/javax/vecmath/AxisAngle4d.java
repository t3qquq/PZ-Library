// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class AxisAngle4d implements Serializable, Cloneable {
    static final long serialVersionUID = 3644296204459140589L;
    public double x;
    public double y;
    public double z;
    public double angle;
    static final double EPS = 1.0E-12;

    public AxisAngle4d(double double0, double double1, double double2, double double3) {
        this.x = double0;
        this.y = double1;
        this.z = double2;
        this.angle = double3;
    }

    public AxisAngle4d(double[] doubles) {
        this.x = doubles[0];
        this.y = doubles[1];
        this.z = doubles[2];
        this.angle = doubles[3];
    }

    public AxisAngle4d(AxisAngle4d axisAngle4d1) {
        this.x = axisAngle4d1.x;
        this.y = axisAngle4d1.y;
        this.z = axisAngle4d1.z;
        this.angle = axisAngle4d1.angle;
    }

    public AxisAngle4d(AxisAngle4f axisAngle4f) {
        this.x = axisAngle4f.x;
        this.y = axisAngle4f.y;
        this.z = axisAngle4f.z;
        this.angle = axisAngle4f.angle;
    }

    public AxisAngle4d(Vector3d vector3d, double double0) {
        this.x = vector3d.x;
        this.y = vector3d.y;
        this.z = vector3d.z;
        this.angle = double0;
    }

    public AxisAngle4d() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 1.0;
        this.angle = 0.0;
    }

    public final void set(double double0, double double1, double double2, double double3) {
        this.x = double0;
        this.y = double1;
        this.z = double2;
        this.angle = double3;
    }

    public final void set(double[] doubles) {
        this.x = doubles[0];
        this.y = doubles[1];
        this.z = doubles[2];
        this.angle = doubles[3];
    }

    public final void set(AxisAngle4d axisAngle4d0) {
        this.x = axisAngle4d0.x;
        this.y = axisAngle4d0.y;
        this.z = axisAngle4d0.z;
        this.angle = axisAngle4d0.angle;
    }

    public final void set(AxisAngle4f axisAngle4f) {
        this.x = axisAngle4f.x;
        this.y = axisAngle4f.y;
        this.z = axisAngle4f.z;
        this.angle = axisAngle4f.angle;
    }

    public final void set(Vector3d vector3d, double double0) {
        this.x = vector3d.x;
        this.y = vector3d.y;
        this.z = vector3d.z;
        this.angle = double0;
    }

    public final void get(double[] doubles) {
        doubles[0] = this.x;
        doubles[1] = this.y;
        doubles[2] = this.z;
        doubles[3] = this.angle;
    }

    public final void set(Matrix4f matrix4f) {
        Matrix3d matrix3d = new Matrix3d();
        matrix4f.get(matrix3d);
        this.x = (float)(matrix3d.m21 - matrix3d.m12);
        this.y = (float)(matrix3d.m02 - matrix3d.m20);
        this.z = (float)(matrix3d.m10 - matrix3d.m01);
        double double0 = this.x * this.x + this.y * this.y + this.z * this.z;
        if (double0 > 1.0E-12) {
            double0 = Math.sqrt(double0);
            double double1 = 0.5 * double0;
            double double2 = 0.5 * (matrix3d.m00 + matrix3d.m11 + matrix3d.m22 - 1.0);
            this.angle = (float)Math.atan2(double1, double2);
            double double3 = 1.0 / double0;
            this.x *= double3;
            this.y *= double3;
            this.z *= double3;
        } else {
            this.x = 0.0;
            this.y = 1.0;
            this.z = 0.0;
            this.angle = 0.0;
        }
    }

    public final void set(Matrix4d matrix4d) {
        Matrix3d matrix3d = new Matrix3d();
        matrix4d.get(matrix3d);
        this.x = (float)(matrix3d.m21 - matrix3d.m12);
        this.y = (float)(matrix3d.m02 - matrix3d.m20);
        this.z = (float)(matrix3d.m10 - matrix3d.m01);
        double double0 = this.x * this.x + this.y * this.y + this.z * this.z;
        if (double0 > 1.0E-12) {
            double0 = Math.sqrt(double0);
            double double1 = 0.5 * double0;
            double double2 = 0.5 * (matrix3d.m00 + matrix3d.m11 + matrix3d.m22 - 1.0);
            this.angle = (float)Math.atan2(double1, double2);
            double double3 = 1.0 / double0;
            this.x *= double3;
            this.y *= double3;
            this.z *= double3;
        } else {
            this.x = 0.0;
            this.y = 1.0;
            this.z = 0.0;
            this.angle = 0.0;
        }
    }

    public final void set(Matrix3f matrix3f) {
        this.x = matrix3f.m21 - matrix3f.m12;
        this.y = matrix3f.m02 - matrix3f.m20;
        this.z = matrix3f.m10 - matrix3f.m01;
        double double0 = this.x * this.x + this.y * this.y + this.z * this.z;
        if (double0 > 1.0E-12) {
            double0 = Math.sqrt(double0);
            double double1 = 0.5 * double0;
            double double2 = 0.5 * (matrix3f.m00 + matrix3f.m11 + matrix3f.m22 - 1.0);
            this.angle = (float)Math.atan2(double1, double2);
            double double3 = 1.0 / double0;
            this.x *= double3;
            this.y *= double3;
            this.z *= double3;
        } else {
            this.x = 0.0;
            this.y = 1.0;
            this.z = 0.0;
            this.angle = 0.0;
        }
    }

    public final void set(Matrix3d matrix3d) {
        this.x = (float)(matrix3d.m21 - matrix3d.m12);
        this.y = (float)(matrix3d.m02 - matrix3d.m20);
        this.z = (float)(matrix3d.m10 - matrix3d.m01);
        double double0 = this.x * this.x + this.y * this.y + this.z * this.z;
        if (double0 > 1.0E-12) {
            double0 = Math.sqrt(double0);
            double double1 = 0.5 * double0;
            double double2 = 0.5 * (matrix3d.m00 + matrix3d.m11 + matrix3d.m22 - 1.0);
            this.angle = (float)Math.atan2(double1, double2);
            double double3 = 1.0 / double0;
            this.x *= double3;
            this.y *= double3;
            this.z *= double3;
        } else {
            this.x = 0.0;
            this.y = 1.0;
            this.z = 0.0;
            this.angle = 0.0;
        }
    }

    public final void set(Quat4f quat4f) {
        double double0 = quat4f.x * quat4f.x + quat4f.y * quat4f.y + quat4f.z * quat4f.z;
        if (double0 > 1.0E-12) {
            double0 = Math.sqrt(double0);
            double double1 = 1.0 / double0;
            this.x = quat4f.x * double1;
            this.y = quat4f.y * double1;
            this.z = quat4f.z * double1;
            this.angle = 2.0 * Math.atan2(double0, quat4f.w);
        } else {
            this.x = 0.0;
            this.y = 1.0;
            this.z = 0.0;
            this.angle = 0.0;
        }
    }

    public final void set(Quat4d quat4d) {
        double double0 = quat4d.x * quat4d.x + quat4d.y * quat4d.y + quat4d.z * quat4d.z;
        if (double0 > 1.0E-12) {
            double0 = Math.sqrt(double0);
            double double1 = 1.0 / double0;
            this.x = quat4d.x * double1;
            this.y = quat4d.y * double1;
            this.z = quat4d.z * double1;
            this.angle = 2.0 * Math.atan2(double0, quat4d.w);
        } else {
            this.x = 0.0;
            this.y = 1.0;
            this.z = 0.0;
            this.angle = 0.0;
        }
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.angle + ")";
    }

    public boolean equals(AxisAngle4d axisAngle4d0) {
        try {
            return this.x == axisAngle4d0.x && this.y == axisAngle4d0.y && this.z == axisAngle4d0.z && this.angle == axisAngle4d0.angle;
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    @Override
    public boolean equals(Object object) {
        try {
            AxisAngle4d axisAngle4d0 = (AxisAngle4d)object;
            return this.x == axisAngle4d0.x && this.y == axisAngle4d0.y && this.z == axisAngle4d0.z && this.angle == axisAngle4d0.angle;
        } catch (NullPointerException nullPointerException) {
            return false;
        } catch (ClassCastException classCastException) {
            return false;
        }
    }

    public boolean epsilonEquals(AxisAngle4d axisAngle4d0, double double1) {
        double double0 = this.x - axisAngle4d0.x;
        if ((double0 < 0.0 ? -double0 : double0) > double1) {
            return false;
        } else {
            double0 = this.y - axisAngle4d0.y;
            if ((double0 < 0.0 ? -double0 : double0) > double1) {
                return false;
            } else {
                double0 = this.z - axisAngle4d0.z;
                if ((double0 < 0.0 ? -double0 : double0) > double1) {
                    return false;
                } else {
                    double0 = this.angle - axisAngle4d0.angle;
                    return !((double0 < 0.0 ? -double0 : double0) > double1);
                }
            }
        }
    }

    @Override
    public int hashCode() {
        long long0 = 1L;
        long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.x);
        long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.y);
        long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.z);
        long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.angle);
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

    public final double getAngle() {
        return this.angle;
    }

    public final void setAngle(double double0) {
        this.angle = double0;
    }

    public double getX() {
        return this.x;
    }

    public final void setX(double double0) {
        this.x = double0;
    }

    public final double getY() {
        return this.y;
    }

    public final void setY(double double0) {
        this.y = double0;
    }

    public double getZ() {
        return this.z;
    }

    public final void setZ(double double0) {
        this.z = double0;
    }
}
