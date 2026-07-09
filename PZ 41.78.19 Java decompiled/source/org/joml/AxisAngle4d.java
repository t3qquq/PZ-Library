// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;

public class AxisAngle4d implements Externalizable {
    private static final long serialVersionUID = 1L;
    public double angle;
    public double x;
    public double y;
    public double z;

    public AxisAngle4d() {
        this.z = 1.0;
    }

    public AxisAngle4d(AxisAngle4d arg0) {
        this.x = arg0.x;
        this.y = arg0.y;
        this.z = arg0.z;
        this.angle = (arg0.angle < 0.0 ? (java.lang.Math.PI * 2) + arg0.angle % (java.lang.Math.PI * 2) : arg0.angle) % (java.lang.Math.PI * 2);
    }

    public AxisAngle4d(AxisAngle4f arg0) {
        this.x = arg0.x;
        this.y = arg0.y;
        this.z = arg0.z;
        this.angle = (arg0.angle < 0.0 ? (java.lang.Math.PI * 2) + arg0.angle % (java.lang.Math.PI * 2) : arg0.angle) % (java.lang.Math.PI * 2);
    }

    public AxisAngle4d(Quaternionfc arg0) {
        float float0 = Math.safeAcos(arg0.w());
        float float1 = Math.invsqrt(1.0F - arg0.w() * arg0.w());
        if (Float.isInfinite(float1)) {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
        } else {
            this.x = arg0.x() * float1;
            this.y = arg0.y() * float1;
            this.z = arg0.z() * float1;
        }

        this.angle = float0 + float0;
    }

    public AxisAngle4d(Quaterniondc arg0) {
        double double0 = Math.safeAcos(arg0.w());
        double double1 = Math.invsqrt(1.0 - arg0.w() * arg0.w());
        if (Double.isInfinite(double1)) {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
        } else {
            this.x = arg0.x() * double1;
            this.y = arg0.y() * double1;
            this.z = arg0.z() * double1;
        }

        this.angle = double0 + double0;
    }

    public AxisAngle4d(double arg0, double arg1, double arg2, double arg3) {
        this.x = arg1;
        this.y = arg2;
        this.z = arg3;
        this.angle = (arg0 < 0.0 ? (java.lang.Math.PI * 2) + arg0 % (java.lang.Math.PI * 2) : arg0) % (java.lang.Math.PI * 2);
    }

    public AxisAngle4d(double arg0, Vector3dc arg1) {
        this(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    public AxisAngle4d(double arg0, Vector3f arg1) {
        this(arg0, arg1.x, arg1.y, arg1.z);
    }

    public AxisAngle4d set(AxisAngle4d arg0) {
        this.x = arg0.x;
        this.y = arg0.y;
        this.z = arg0.z;
        this.angle = (arg0.angle < 0.0 ? (java.lang.Math.PI * 2) + arg0.angle % (java.lang.Math.PI * 2) : arg0.angle) % (java.lang.Math.PI * 2);
        return this;
    }

    public AxisAngle4d set(AxisAngle4f arg0) {
        this.x = arg0.x;
        this.y = arg0.y;
        this.z = arg0.z;
        this.angle = (arg0.angle < 0.0 ? (java.lang.Math.PI * 2) + arg0.angle % (java.lang.Math.PI * 2) : arg0.angle) % (java.lang.Math.PI * 2);
        return this;
    }

    public AxisAngle4d set(double arg0, double arg1, double arg2, double arg3) {
        this.x = arg1;
        this.y = arg2;
        this.z = arg3;
        this.angle = (arg0 < 0.0 ? (java.lang.Math.PI * 2) + arg0 % (java.lang.Math.PI * 2) : arg0) % (java.lang.Math.PI * 2);
        return this;
    }

    public AxisAngle4d set(double arg0, Vector3dc arg1) {
        return this.set(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    public AxisAngle4d set(double arg0, Vector3f arg1) {
        return this.set(arg0, arg1.x, arg1.y, arg1.z);
    }

    public AxisAngle4d set(Quaternionfc arg0) {
        float float0 = Math.safeAcos(arg0.w());
        float float1 = Math.invsqrt(1.0F - arg0.w() * arg0.w());
        if (Float.isInfinite(float1)) {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
        } else {
            this.x = arg0.x() * float1;
            this.y = arg0.y() * float1;
            this.z = arg0.z() * float1;
        }

        this.angle = float0 + float0;
        return this;
    }

    public AxisAngle4d set(Quaterniondc arg0) {
        double double0 = Math.safeAcos(arg0.w());
        double double1 = Math.invsqrt(1.0 - arg0.w() * arg0.w());
        if (Double.isInfinite(double1)) {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
        } else {
            this.x = arg0.x() * double1;
            this.y = arg0.y() * double1;
            this.z = arg0.z() * double1;
        }

        this.angle = double0 + double0;
        return this;
    }

    public AxisAngle4d set(Matrix3fc arg0) {
        double double0 = arg0.m00();
        double double1 = arg0.m01();
        double double2 = arg0.m02();
        double double3 = arg0.m10();
        double double4 = arg0.m11();
        double double5 = arg0.m12();
        double double6 = arg0.m20();
        double double7 = arg0.m21();
        double double8 = arg0.m22();
        double double9 = Math.invsqrt(arg0.m00() * arg0.m00() + arg0.m01() * arg0.m01() + arg0.m02() * arg0.m02());
        double double10 = Math.invsqrt(arg0.m10() * arg0.m10() + arg0.m11() * arg0.m11() + arg0.m12() * arg0.m12());
        double double11 = Math.invsqrt(arg0.m20() * arg0.m20() + arg0.m21() * arg0.m21() + arg0.m22() * arg0.m22());
        double0 *= double9;
        double1 *= double9;
        double2 *= double9;
        double3 *= double10;
        double4 *= double10;
        double5 *= double10;
        double6 *= double11;
        double7 *= double11;
        double8 *= double11;
        double double12 = 1.0E-4;
        double double13 = 0.001;
        if (!(Math.abs(double3 - double1) < double12) || !(Math.abs(double6 - double2) < double12) || !(Math.abs(double7 - double5) < double12)) {
            double double14 = Math.sqrt(
                (double5 - double7) * (double5 - double7) + (double6 - double2) * (double6 - double2) + (double1 - double3) * (double1 - double3)
            );
            this.angle = Math.safeAcos((double0 + double4 + double8 - 1.0) / 2.0);
            this.x = (double5 - double7) / double14;
            this.y = (double6 - double2) / double14;
            this.z = (double1 - double3) / double14;
            return this;
        } else if (Math.abs(double3 + double1) < double13
            && Math.abs(double6 + double2) < double13
            && Math.abs(double7 + double5) < double13
            && Math.abs(double0 + double4 + double8 - 3.0) < double13) {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
            this.angle = 0.0;
            return this;
        } else {
            this.angle = java.lang.Math.PI;
            double double15 = (double0 + 1.0) / 2.0;
            double double16 = (double4 + 1.0) / 2.0;
            double double17 = (double8 + 1.0) / 2.0;
            double double18 = (double3 + double1) / 4.0;
            double double19 = (double6 + double2) / 4.0;
            double double20 = (double7 + double5) / 4.0;
            if (double15 > double16 && double15 > double17) {
                this.x = Math.sqrt(double15);
                this.y = double18 / this.x;
                this.z = double19 / this.x;
            } else if (double16 > double17) {
                this.y = Math.sqrt(double16);
                this.x = double18 / this.y;
                this.z = double20 / this.y;
            } else {
                this.z = Math.sqrt(double17);
                this.x = double19 / this.z;
                this.y = double20 / this.z;
            }

            return this;
        }
    }

    public AxisAngle4d set(Matrix3dc arg0) {
        double double0 = arg0.m00();
        double double1 = arg0.m01();
        double double2 = arg0.m02();
        double double3 = arg0.m10();
        double double4 = arg0.m11();
        double double5 = arg0.m12();
        double double6 = arg0.m20();
        double double7 = arg0.m21();
        double double8 = arg0.m22();
        double double9 = Math.invsqrt(arg0.m00() * arg0.m00() + arg0.m01() * arg0.m01() + arg0.m02() * arg0.m02());
        double double10 = Math.invsqrt(arg0.m10() * arg0.m10() + arg0.m11() * arg0.m11() + arg0.m12() * arg0.m12());
        double double11 = Math.invsqrt(arg0.m20() * arg0.m20() + arg0.m21() * arg0.m21() + arg0.m22() * arg0.m22());
        double0 *= double9;
        double1 *= double9;
        double2 *= double9;
        double3 *= double10;
        double4 *= double10;
        double5 *= double10;
        double6 *= double11;
        double7 *= double11;
        double8 *= double11;
        double double12 = 1.0E-4;
        double double13 = 0.001;
        if (!(Math.abs(double3 - double1) < double12) || !(Math.abs(double6 - double2) < double12) || !(Math.abs(double7 - double5) < double12)) {
            double double14 = Math.sqrt(
                (double5 - double7) * (double5 - double7) + (double6 - double2) * (double6 - double2) + (double1 - double3) * (double1 - double3)
            );
            this.angle = Math.safeAcos((double0 + double4 + double8 - 1.0) / 2.0);
            this.x = (double5 - double7) / double14;
            this.y = (double6 - double2) / double14;
            this.z = (double1 - double3) / double14;
            return this;
        } else if (Math.abs(double3 + double1) < double13
            && Math.abs(double6 + double2) < double13
            && Math.abs(double7 + double5) < double13
            && Math.abs(double0 + double4 + double8 - 3.0) < double13) {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
            this.angle = 0.0;
            return this;
        } else {
            this.angle = java.lang.Math.PI;
            double double15 = (double0 + 1.0) / 2.0;
            double double16 = (double4 + 1.0) / 2.0;
            double double17 = (double8 + 1.0) / 2.0;
            double double18 = (double3 + double1) / 4.0;
            double double19 = (double6 + double2) / 4.0;
            double double20 = (double7 + double5) / 4.0;
            if (double15 > double16 && double15 > double17) {
                this.x = Math.sqrt(double15);
                this.y = double18 / this.x;
                this.z = double19 / this.x;
            } else if (double16 > double17) {
                this.y = Math.sqrt(double16);
                this.x = double18 / this.y;
                this.z = double20 / this.y;
            } else {
                this.z = Math.sqrt(double17);
                this.x = double19 / this.z;
                this.y = double20 / this.z;
            }

            return this;
        }
    }

    public AxisAngle4d set(Matrix4fc arg0) {
        double double0 = arg0.m00();
        double double1 = arg0.m01();
        double double2 = arg0.m02();
        double double3 = arg0.m10();
        double double4 = arg0.m11();
        double double5 = arg0.m12();
        double double6 = arg0.m20();
        double double7 = arg0.m21();
        double double8 = arg0.m22();
        double double9 = Math.invsqrt(arg0.m00() * arg0.m00() + arg0.m01() * arg0.m01() + arg0.m02() * arg0.m02());
        double double10 = Math.invsqrt(arg0.m10() * arg0.m10() + arg0.m11() * arg0.m11() + arg0.m12() * arg0.m12());
        double double11 = Math.invsqrt(arg0.m20() * arg0.m20() + arg0.m21() * arg0.m21() + arg0.m22() * arg0.m22());
        double0 *= double9;
        double1 *= double9;
        double2 *= double9;
        double3 *= double10;
        double4 *= double10;
        double5 *= double10;
        double6 *= double11;
        double7 *= double11;
        double8 *= double11;
        double double12 = 1.0E-4;
        double double13 = 0.001;
        if (!(Math.abs(double3 - double1) < double12) || !(Math.abs(double6 - double2) < double12) || !(Math.abs(double7 - double5) < double12)) {
            double double14 = Math.sqrt(
                (double5 - double7) * (double5 - double7) + (double6 - double2) * (double6 - double2) + (double1 - double3) * (double1 - double3)
            );
            this.angle = Math.safeAcos((double0 + double4 + double8 - 1.0) / 2.0);
            this.x = (double5 - double7) / double14;
            this.y = (double6 - double2) / double14;
            this.z = (double1 - double3) / double14;
            return this;
        } else if (Math.abs(double3 + double1) < double13
            && Math.abs(double6 + double2) < double13
            && Math.abs(double7 + double5) < double13
            && Math.abs(double0 + double4 + double8 - 3.0) < double13) {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
            this.angle = 0.0;
            return this;
        } else {
            this.angle = java.lang.Math.PI;
            double double15 = (double0 + 1.0) / 2.0;
            double double16 = (double4 + 1.0) / 2.0;
            double double17 = (double8 + 1.0) / 2.0;
            double double18 = (double3 + double1) / 4.0;
            double double19 = (double6 + double2) / 4.0;
            double double20 = (double7 + double5) / 4.0;
            if (double15 > double16 && double15 > double17) {
                this.x = Math.sqrt(double15);
                this.y = double18 / this.x;
                this.z = double19 / this.x;
            } else if (double16 > double17) {
                this.y = Math.sqrt(double16);
                this.x = double18 / this.y;
                this.z = double20 / this.y;
            } else {
                this.z = Math.sqrt(double17);
                this.x = double19 / this.z;
                this.y = double20 / this.z;
            }

            return this;
        }
    }

    public AxisAngle4d set(Matrix4x3fc arg0) {
        double double0 = arg0.m00();
        double double1 = arg0.m01();
        double double2 = arg0.m02();
        double double3 = arg0.m10();
        double double4 = arg0.m11();
        double double5 = arg0.m12();
        double double6 = arg0.m20();
        double double7 = arg0.m21();
        double double8 = arg0.m22();
        double double9 = Math.invsqrt(arg0.m00() * arg0.m00() + arg0.m01() * arg0.m01() + arg0.m02() * arg0.m02());
        double double10 = Math.invsqrt(arg0.m10() * arg0.m10() + arg0.m11() * arg0.m11() + arg0.m12() * arg0.m12());
        double double11 = Math.invsqrt(arg0.m20() * arg0.m20() + arg0.m21() * arg0.m21() + arg0.m22() * arg0.m22());
        double0 *= double9;
        double1 *= double9;
        double2 *= double9;
        double3 *= double10;
        double4 *= double10;
        double5 *= double10;
        double6 *= double11;
        double7 *= double11;
        double8 *= double11;
        double double12 = 1.0E-4;
        double double13 = 0.001;
        if (!(Math.abs(double3 - double1) < double12) || !(Math.abs(double6 - double2) < double12) || !(Math.abs(double7 - double5) < double12)) {
            double double14 = Math.sqrt(
                (double5 - double7) * (double5 - double7) + (double6 - double2) * (double6 - double2) + (double1 - double3) * (double1 - double3)
            );
            this.angle = Math.safeAcos((double0 + double4 + double8 - 1.0) / 2.0);
            this.x = (double5 - double7) / double14;
            this.y = (double6 - double2) / double14;
            this.z = (double1 - double3) / double14;
            return this;
        } else if (Math.abs(double3 + double1) < double13
            && Math.abs(double6 + double2) < double13
            && Math.abs(double7 + double5) < double13
            && Math.abs(double0 + double4 + double8 - 3.0) < double13) {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
            this.angle = 0.0;
            return this;
        } else {
            this.angle = java.lang.Math.PI;
            double double15 = (double0 + 1.0) / 2.0;
            double double16 = (double4 + 1.0) / 2.0;
            double double17 = (double8 + 1.0) / 2.0;
            double double18 = (double3 + double1) / 4.0;
            double double19 = (double6 + double2) / 4.0;
            double double20 = (double7 + double5) / 4.0;
            if (double15 > double16 && double15 > double17) {
                this.x = Math.sqrt(double15);
                this.y = double18 / this.x;
                this.z = double19 / this.x;
            } else if (double16 > double17) {
                this.y = Math.sqrt(double16);
                this.x = double18 / this.y;
                this.z = double20 / this.y;
            } else {
                this.z = Math.sqrt(double17);
                this.x = double19 / this.z;
                this.y = double20 / this.z;
            }

            return this;
        }
    }

    public AxisAngle4d set(Matrix4dc arg0) {
        double double0 = arg0.m00();
        double double1 = arg0.m01();
        double double2 = arg0.m02();
        double double3 = arg0.m10();
        double double4 = arg0.m11();
        double double5 = arg0.m12();
        double double6 = arg0.m20();
        double double7 = arg0.m21();
        double double8 = arg0.m22();
        double double9 = Math.invsqrt(arg0.m00() * arg0.m00() + arg0.m01() * arg0.m01() + arg0.m02() * arg0.m02());
        double double10 = Math.invsqrt(arg0.m10() * arg0.m10() + arg0.m11() * arg0.m11() + arg0.m12() * arg0.m12());
        double double11 = Math.invsqrt(arg0.m20() * arg0.m20() + arg0.m21() * arg0.m21() + arg0.m22() * arg0.m22());
        double0 *= double9;
        double1 *= double9;
        double2 *= double9;
        double3 *= double10;
        double4 *= double10;
        double5 *= double10;
        double6 *= double11;
        double7 *= double11;
        double8 *= double11;
        double double12 = 1.0E-4;
        double double13 = 0.001;
        if (!(Math.abs(double3 - double1) < double12) || !(Math.abs(double6 - double2) < double12) || !(Math.abs(double7 - double5) < double12)) {
            double double14 = Math.sqrt(
                (double5 - double7) * (double5 - double7) + (double6 - double2) * (double6 - double2) + (double1 - double3) * (double1 - double3)
            );
            this.angle = Math.safeAcos((double0 + double4 + double8 - 1.0) / 2.0);
            this.x = (double5 - double7) / double14;
            this.y = (double6 - double2) / double14;
            this.z = (double1 - double3) / double14;
            return this;
        } else if (Math.abs(double3 + double1) < double13
            && Math.abs(double6 + double2) < double13
            && Math.abs(double7 + double5) < double13
            && Math.abs(double0 + double4 + double8 - 3.0) < double13) {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
            this.angle = 0.0;
            return this;
        } else {
            this.angle = java.lang.Math.PI;
            double double15 = (double0 + 1.0) / 2.0;
            double double16 = (double4 + 1.0) / 2.0;
            double double17 = (double8 + 1.0) / 2.0;
            double double18 = (double3 + double1) / 4.0;
            double double19 = (double6 + double2) / 4.0;
            double double20 = (double7 + double5) / 4.0;
            if (double15 > double16 && double15 > double17) {
                this.x = Math.sqrt(double15);
                this.y = double18 / this.x;
                this.z = double19 / this.x;
            } else if (double16 > double17) {
                this.y = Math.sqrt(double16);
                this.x = double18 / this.y;
                this.z = double20 / this.y;
            } else {
                this.z = Math.sqrt(double17);
                this.x = double19 / this.z;
                this.y = double20 / this.z;
            }

            return this;
        }
    }

    public Quaternionf get(Quaternionf arg0) {
        return arg0.set(this);
    }

    public Quaterniond get(Quaterniond arg0) {
        return arg0.set(this);
    }

    public Matrix4f get(Matrix4f arg0) {
        return arg0.set(this);
    }

    public Matrix3f get(Matrix3f arg0) {
        return arg0.set(this);
    }

    public Matrix4d get(Matrix4d arg0) {
        return arg0.set(this);
    }

    public Matrix3d get(Matrix3d arg0) {
        return arg0.set(this);
    }

    public AxisAngle4d get(AxisAngle4d arg0) {
        return arg0.set(this);
    }

    public AxisAngle4f get(AxisAngle4f arg0) {
        return arg0.set(this);
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        arg0.writeDouble(this.angle);
        arg0.writeDouble(this.x);
        arg0.writeDouble(this.y);
        arg0.writeDouble(this.z);
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        this.angle = arg0.readDouble();
        this.x = arg0.readDouble();
        this.y = arg0.readDouble();
        this.z = arg0.readDouble();
    }

    public AxisAngle4d normalize() {
        double double0 = Math.invsqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        this.x *= double0;
        this.y *= double0;
        this.z *= double0;
        return this;
    }

    public AxisAngle4d rotate(double arg0) {
        this.angle += arg0;
        this.angle = (this.angle < 0.0 ? (java.lang.Math.PI * 2) + this.angle % (java.lang.Math.PI * 2) : this.angle) % (java.lang.Math.PI * 2);
        return this;
    }

    public Vector3d transform(Vector3d arg0) {
        return this.transform(arg0, arg0);
    }

    public Vector3d transform(Vector3dc arg0, Vector3d arg1) {
        double double0 = Math.sin(this.angle);
        double double1 = Math.cosFromSin(double0, this.angle);
        double double2 = this.x * arg0.x() + this.y * arg0.y() + this.z * arg0.z();
        arg1.set(
            arg0.x() * double1 + double0 * (this.y * arg0.z() - this.z * arg0.y()) + (1.0 - double1) * double2 * this.x,
            arg0.y() * double1 + double0 * (this.z * arg0.x() - this.x * arg0.z()) + (1.0 - double1) * double2 * this.y,
            arg0.z() * double1 + double0 * (this.x * arg0.y() - this.y * arg0.x()) + (1.0 - double1) * double2 * this.z
        );
        return arg1;
    }

    public Vector3f transform(Vector3f arg0) {
        return this.transform(arg0, arg0);
    }

    public Vector3f transform(Vector3fc arg0, Vector3f arg1) {
        double double0 = Math.sin(this.angle);
        double double1 = Math.cosFromSin(double0, this.angle);
        double double2 = this.x * arg0.x() + this.y * arg0.y() + this.z * arg0.z();
        arg1.set(
            (float)(arg0.x() * double1 + double0 * (this.y * arg0.z() - this.z * arg0.y()) + (1.0 - double1) * double2 * this.x),
            (float)(arg0.y() * double1 + double0 * (this.z * arg0.x() - this.x * arg0.z()) + (1.0 - double1) * double2 * this.y),
            (float)(arg0.z() * double1 + double0 * (this.x * arg0.y() - this.y * arg0.x()) + (1.0 - double1) * double2 * this.z)
        );
        return arg1;
    }

    public Vector4d transform(Vector4d arg0) {
        return this.transform(arg0, arg0);
    }

    public Vector4d transform(Vector4dc arg0, Vector4d arg1) {
        double double0 = Math.sin(this.angle);
        double double1 = Math.cosFromSin(double0, this.angle);
        double double2 = this.x * arg0.x() + this.y * arg0.y() + this.z * arg0.z();
        arg1.set(
            arg0.x() * double1 + double0 * (this.y * arg0.z() - this.z * arg0.y()) + (1.0 - double1) * double2 * this.x,
            arg0.y() * double1 + double0 * (this.z * arg0.x() - this.x * arg0.z()) + (1.0 - double1) * double2 * this.y,
            arg0.z() * double1 + double0 * (this.x * arg0.y() - this.y * arg0.x()) + (1.0 - double1) * double2 * this.z,
            arg1.w
        );
        return arg1;
    }

    @Override
    public String toString() {
        return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
    }

    public String toString(NumberFormat numberFormat) {
        return "("
            + Runtime.format(this.x, numberFormat)
            + " "
            + Runtime.format(this.y, numberFormat)
            + " "
            + Runtime.format(this.z, numberFormat)
            + " <| "
            + Runtime.format(this.angle, numberFormat)
            + ")";
    }

    @Override
    public int hashCode() {
        int int0 = 1;
        long long0 = Double.doubleToLongBits(
            (this.angle < 0.0 ? (java.lang.Math.PI * 2) + this.angle % (java.lang.Math.PI * 2) : this.angle) % (java.lang.Math.PI * 2)
        );
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.x);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.y);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.z);
        return 31 * int0 + (int)(long0 ^ long0 >>> 32);
    }

    @Override
    public boolean equals(Object arg0) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (this.getClass() != arg0.getClass()) {
            return false;
        } else {
            AxisAngle4d axisAngle4d = (AxisAngle4d)arg0;
            if (Double.doubleToLongBits(
                    (this.angle < 0.0 ? (java.lang.Math.PI * 2) + this.angle % (java.lang.Math.PI * 2) : this.angle) % (java.lang.Math.PI * 2)
                )
                != Double.doubleToLongBits(
                    (axisAngle4d.angle < 0.0 ? (java.lang.Math.PI * 2) + axisAngle4d.angle % (java.lang.Math.PI * 2) : axisAngle4d.angle)
                        % (java.lang.Math.PI * 2)
                )) {
                return false;
            } else if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(axisAngle4d.x)) {
                return false;
            } else {
                return Double.doubleToLongBits(this.y) != Double.doubleToLongBits(axisAngle4d.y)
                    ? false
                    : Double.doubleToLongBits(this.z) == Double.doubleToLongBits(axisAngle4d.z);
            }
        }
    }
}
