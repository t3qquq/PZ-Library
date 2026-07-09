// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;

public class Quaterniond implements Externalizable, Quaterniondc {
    private static final long serialVersionUID = 1L;
    public double x;
    public double y;
    public double z;
    public double w;

    public Quaterniond() {
        this.w = 1.0;
    }

    public Quaterniond(double arg0, double arg1, double arg2, double arg3) {
        this.x = arg0;
        this.y = arg1;
        this.z = arg2;
        this.w = arg3;
    }

    public Quaterniond(Quaterniondc arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg0.w();
    }

    public Quaterniond(Quaternionfc arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg0.w();
    }

    public Quaterniond(AxisAngle4f arg0) {
        double double0 = Math.sin(arg0.angle * 0.5);
        this.x = arg0.x * double0;
        this.y = arg0.y * double0;
        this.z = arg0.z * double0;
        this.w = Math.cosFromSin(double0, arg0.angle * 0.5);
    }

    public Quaterniond(AxisAngle4d arg0) {
        double double0 = Math.sin(arg0.angle * 0.5);
        this.x = arg0.x * double0;
        this.y = arg0.y * double0;
        this.z = arg0.z * double0;
        this.w = Math.cosFromSin(double0, arg0.angle * 0.5);
    }

    @Override
    public double x() {
        return this.x;
    }

    @Override
    public double y() {
        return this.y;
    }

    @Override
    public double z() {
        return this.z;
    }

    @Override
    public double w() {
        return this.w;
    }

    public Quaterniond normalize() {
        double double0 = Math.invsqrt(this.lengthSquared());
        this.x *= double0;
        this.y *= double0;
        this.z *= double0;
        this.w *= double0;
        return this;
    }

    @Override
    public Quaterniond normalize(Quaterniond arg0) {
        double double0 = Math.invsqrt(this.lengthSquared());
        arg0.x = this.x * double0;
        arg0.y = this.y * double0;
        arg0.z = this.z * double0;
        arg0.w = this.w * double0;
        return arg0;
    }

    public Quaterniond add(double arg0, double arg1, double arg2, double arg3) {
        return this.add(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Quaterniond add(double arg0, double arg1, double arg2, double arg3, Quaterniond arg4) {
        arg4.x = this.x + arg0;
        arg4.y = this.y + arg1;
        arg4.z = this.z + arg2;
        arg4.w = this.w + arg3;
        return arg4;
    }

    public Quaterniond add(Quaterniondc arg0) {
        this.x = this.x + arg0.x();
        this.y = this.y + arg0.y();
        this.z = this.z + arg0.z();
        this.w = this.w + arg0.w();
        return this;
    }

    @Override
    public Quaterniond add(Quaterniondc arg0, Quaterniond arg1) {
        arg1.x = this.x + arg0.x();
        arg1.y = this.y + arg0.y();
        arg1.z = this.z + arg0.z();
        arg1.w = this.w + arg0.w();
        return arg1;
    }

    @Override
    public double dot(Quaterniondc arg0) {
        return this.x * arg0.x() + this.y * arg0.y() + this.z * arg0.z() + this.w * arg0.w();
    }

    @Override
    public double angle() {
        return 2.0 * Math.safeAcos(this.w);
    }

    @Override
    public Matrix3d get(Matrix3d arg0) {
        return arg0.set(this);
    }

    @Override
    public Matrix3f get(Matrix3f arg0) {
        return arg0.set(this);
    }

    @Override
    public Matrix4d get(Matrix4d arg0) {
        return arg0.set(this);
    }

    @Override
    public Matrix4f get(Matrix4f arg0) {
        return arg0.set(this);
    }

    @Override
    public AxisAngle4f get(AxisAngle4f arg0) {
        double double0 = this.x;
        double double1 = this.y;
        double double2 = this.z;
        double double3 = this.w;
        if (double3 > 1.0) {
            double double4 = Math.invsqrt(this.lengthSquared());
            double0 *= double4;
            double1 *= double4;
            double2 *= double4;
            double3 *= double4;
        }

        arg0.angle = (float)(2.0 * Math.acos(double3));
        double double5 = Math.sqrt(1.0 - double3 * double3);
        if (double5 < 0.001) {
            arg0.x = (float)double0;
            arg0.y = (float)double1;
            arg0.z = (float)double2;
        } else {
            double5 = 1.0 / double5;
            arg0.x = (float)(double0 * double5);
            arg0.y = (float)(double1 * double5);
            arg0.z = (float)(double2 * double5);
        }

        return arg0;
    }

    @Override
    public AxisAngle4d get(AxisAngle4d arg0) {
        double double0 = this.x;
        double double1 = this.y;
        double double2 = this.z;
        double double3 = this.w;
        if (double3 > 1.0) {
            double double4 = Math.invsqrt(this.lengthSquared());
            double0 *= double4;
            double1 *= double4;
            double2 *= double4;
            double3 *= double4;
        }

        arg0.angle = 2.0 * Math.acos(double3);
        double double5 = Math.sqrt(1.0 - double3 * double3);
        if (double5 < 0.001) {
            arg0.x = double0;
            arg0.y = double1;
            arg0.z = double2;
        } else {
            double5 = 1.0 / double5;
            arg0.x = double0 * double5;
            arg0.y = double1 * double5;
            arg0.z = double2 * double5;
        }

        return arg0;
    }

    @Override
    public Quaterniond get(Quaterniond arg0) {
        return arg0.set(this);
    }

    @Override
    public Quaternionf get(Quaternionf arg0) {
        return arg0.set(this);
    }

    public Quaterniond set(double arg0, double arg1, double arg2, double arg3) {
        this.x = arg0;
        this.y = arg1;
        this.z = arg2;
        this.w = arg3;
        return this;
    }

    public Quaterniond set(Quaterniondc arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg0.w();
        return this;
    }

    public Quaterniond set(Quaternionfc arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg0.w();
        return this;
    }

    public Quaterniond set(AxisAngle4f arg0) {
        return this.setAngleAxis(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    public Quaterniond set(AxisAngle4d arg0) {
        return this.setAngleAxis(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    public Quaterniond setAngleAxis(double arg0, double arg1, double arg2, double arg3) {
        double double0 = Math.sin(arg0 * 0.5);
        this.x = arg1 * double0;
        this.y = arg2 * double0;
        this.z = arg3 * double0;
        this.w = Math.cosFromSin(double0, arg0 * 0.5);
        return this;
    }

    public Quaterniond setAngleAxis(double arg0, Vector3dc arg1) {
        return this.setAngleAxis(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    private void setFromUnnormalized(
        double double3, double double2, double double1, double double7, double double6, double double5, double double11, double double10, double double9
    ) {
        double double0 = Math.invsqrt(double3 * double3 + double2 * double2 + double1 * double1);
        double double4 = Math.invsqrt(double7 * double7 + double6 * double6 + double5 * double5);
        double double8 = Math.invsqrt(double11 * double11 + double10 * double10 + double9 * double9);
        double double12 = double3 * double0;
        double double13 = double2 * double0;
        double double14 = double1 * double0;
        double double15 = double7 * double4;
        double double16 = double6 * double4;
        double double17 = double5 * double4;
        double double18 = double11 * double8;
        double double19 = double10 * double8;
        double double20 = double9 * double8;
        this.setFromNormalized(double12, double13, double14, double15, double16, double17, double18, double19, double20);
    }

    private void setFromNormalized(
        double double2, double double9, double double8, double double10, double double3, double double5, double double7, double double6, double double1
    ) {
        double double0 = double2 + double3 + double1;
        if (double0 >= 0.0) {
            double double4 = Math.sqrt(double0 + 1.0);
            this.w = double4 * 0.5;
            double4 = 0.5 / double4;
            this.x = (double5 - double6) * double4;
            this.y = (double7 - double8) * double4;
            this.z = (double9 - double10) * double4;
        } else if (double2 >= double3 && double2 >= double1) {
            double double11 = Math.sqrt(double2 - (double3 + double1) + 1.0);
            this.x = double11 * 0.5;
            double11 = 0.5 / double11;
            this.y = (double10 + double9) * double11;
            this.z = (double8 + double7) * double11;
            this.w = (double5 - double6) * double11;
        } else if (double3 > double1) {
            double double12 = Math.sqrt(double3 - (double1 + double2) + 1.0);
            this.y = double12 * 0.5;
            double12 = 0.5 / double12;
            this.z = (double6 + double5) * double12;
            this.x = (double10 + double9) * double12;
            this.w = (double7 - double8) * double12;
        } else {
            double double13 = Math.sqrt(double1 - (double2 + double3) + 1.0);
            this.z = double13 * 0.5;
            double13 = 0.5 / double13;
            this.x = (double8 + double7) * double13;
            this.y = (double6 + double5) * double13;
            this.w = (double9 - double10) * double13;
        }
    }

    public Quaterniond setFromUnnormalized(Matrix4fc arg0) {
        this.setFromUnnormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaterniond setFromUnnormalized(Matrix4x3fc arg0) {
        this.setFromUnnormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaterniond setFromUnnormalized(Matrix4x3dc arg0) {
        this.setFromUnnormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaterniond setFromNormalized(Matrix4fc arg0) {
        this.setFromNormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaterniond setFromNormalized(Matrix4x3fc arg0) {
        this.setFromNormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaterniond setFromNormalized(Matrix4x3dc arg0) {
        this.setFromNormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaterniond setFromUnnormalized(Matrix4dc arg0) {
        this.setFromUnnormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaterniond setFromNormalized(Matrix4dc arg0) {
        this.setFromNormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaterniond setFromUnnormalized(Matrix3fc arg0) {
        this.setFromUnnormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaterniond setFromNormalized(Matrix3fc arg0) {
        this.setFromNormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaterniond setFromUnnormalized(Matrix3dc arg0) {
        this.setFromUnnormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaterniond setFromNormalized(Matrix3dc arg0) {
        this.setFromNormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaterniond fromAxisAngleRad(Vector3dc arg0, double arg1) {
        return this.fromAxisAngleRad(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    public Quaterniond fromAxisAngleRad(double arg0, double arg1, double arg2, double arg3) {
        double double0 = arg3 / 2.0;
        double double1 = Math.sin(double0);
        double double2 = Math.sqrt(arg0 * arg0 + arg1 * arg1 + arg2 * arg2);
        this.x = arg0 / double2 * double1;
        this.y = arg1 / double2 * double1;
        this.z = arg2 / double2 * double1;
        this.w = Math.cosFromSin(double1, double0);
        return this;
    }

    public Quaterniond fromAxisAngleDeg(Vector3dc arg0, double arg1) {
        return this.fromAxisAngleRad(arg0.x(), arg0.y(), arg0.z(), Math.toRadians(arg1));
    }

    public Quaterniond fromAxisAngleDeg(double arg0, double arg1, double arg2, double arg3) {
        return this.fromAxisAngleRad(arg0, arg1, arg2, Math.toRadians(arg3));
    }

    public Quaterniond mul(Quaterniondc arg0) {
        return this.mul(arg0, this);
    }

    @Override
    public Quaterniond mul(Quaterniondc arg0, Quaterniond arg1) {
        return this.mul(arg0.x(), arg0.y(), arg0.z(), arg0.w(), arg1);
    }

    public Quaterniond mul(double arg0, double arg1, double arg2, double arg3) {
        return this.mul(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Quaterniond mul(double arg0, double arg1, double arg2, double arg3, Quaterniond arg4) {
        return arg4.set(
            Math.fma(this.w, arg0, Math.fma(this.x, arg3, Math.fma(this.y, arg2, -this.z * arg1))),
            Math.fma(this.w, arg1, Math.fma(-this.x, arg2, Math.fma(this.y, arg3, this.z * arg0))),
            Math.fma(this.w, arg2, Math.fma(this.x, arg1, Math.fma(-this.y, arg0, this.z * arg3))),
            Math.fma(this.w, arg3, Math.fma(-this.x, arg0, Math.fma(-this.y, arg1, -this.z * arg2)))
        );
    }

    public Quaterniond premul(Quaterniondc arg0) {
        return this.premul(arg0, this);
    }

    @Override
    public Quaterniond premul(Quaterniondc arg0, Quaterniond arg1) {
        return this.premul(arg0.x(), arg0.y(), arg0.z(), arg0.w(), arg1);
    }

    public Quaterniond premul(double arg0, double arg1, double arg2, double arg3) {
        return this.premul(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Quaterniond premul(double arg0, double arg1, double arg2, double arg3, Quaterniond arg4) {
        return arg4.set(
            Math.fma(arg3, this.x, Math.fma(arg0, this.w, Math.fma(arg1, this.z, -arg2 * this.y))),
            Math.fma(arg3, this.y, Math.fma(-arg0, this.z, Math.fma(arg1, this.w, arg2 * this.x))),
            Math.fma(arg3, this.z, Math.fma(arg0, this.y, Math.fma(-arg1, this.x, arg2 * this.w))),
            Math.fma(arg3, this.w, Math.fma(-arg0, this.x, Math.fma(-arg1, this.y, -arg2 * this.z)))
        );
    }

    @Override
    public Vector3d transform(Vector3d arg0) {
        return this.transform(arg0.x, arg0.y, arg0.z, arg0);
    }

    @Override
    public Vector3d transformInverse(Vector3d arg0) {
        return this.transformInverse(arg0.x, arg0.y, arg0.z, arg0);
    }

    @Override
    public Vector3d transformUnit(Vector3d arg0) {
        return this.transformUnit(arg0.x, arg0.y, arg0.z, arg0);
    }

    @Override
    public Vector3d transformInverseUnit(Vector3d arg0) {
        return this.transformInverseUnit(arg0.x, arg0.y, arg0.z, arg0);
    }

    @Override
    public Vector3d transformPositiveX(Vector3d arg0) {
        double double0 = this.w * this.w;
        double double1 = this.x * this.x;
        double double2 = this.y * this.y;
        double double3 = this.z * this.z;
        double double4 = this.z * this.w;
        double double5 = this.x * this.y;
        double double6 = this.x * this.z;
        double double7 = this.y * this.w;
        arg0.x = double0 + double1 - double3 - double2;
        arg0.y = double5 + double4 + double4 + double5;
        arg0.z = double6 - double7 + double6 - double7;
        return arg0;
    }

    @Override
    public Vector4d transformPositiveX(Vector4d arg0) {
        double double0 = this.w * this.w;
        double double1 = this.x * this.x;
        double double2 = this.y * this.y;
        double double3 = this.z * this.z;
        double double4 = this.z * this.w;
        double double5 = this.x * this.y;
        double double6 = this.x * this.z;
        double double7 = this.y * this.w;
        arg0.x = double0 + double1 - double3 - double2;
        arg0.y = double5 + double4 + double4 + double5;
        arg0.z = double6 - double7 + double6 - double7;
        return arg0;
    }

    @Override
    public Vector3d transformUnitPositiveX(Vector3d arg0) {
        double double0 = this.y * this.y;
        double double1 = this.z * this.z;
        double double2 = this.x * this.y;
        double double3 = this.x * this.z;
        double double4 = this.y * this.w;
        double double5 = this.z * this.w;
        arg0.x = 1.0 - double0 - double0 - double1 - double1;
        arg0.y = double2 + double5 + double2 + double5;
        arg0.z = double3 - double4 + double3 - double4;
        return arg0;
    }

    @Override
    public Vector4d transformUnitPositiveX(Vector4d arg0) {
        double double0 = this.y * this.y;
        double double1 = this.z * this.z;
        double double2 = this.x * this.y;
        double double3 = this.x * this.z;
        double double4 = this.y * this.w;
        double double5 = this.z * this.w;
        arg0.x = 1.0 - double0 - double0 - double1 - double1;
        arg0.y = double2 + double5 + double2 + double5;
        arg0.z = double3 - double4 + double3 - double4;
        return arg0;
    }

    @Override
    public Vector3d transformPositiveY(Vector3d arg0) {
        double double0 = this.w * this.w;
        double double1 = this.x * this.x;
        double double2 = this.y * this.y;
        double double3 = this.z * this.z;
        double double4 = this.z * this.w;
        double double5 = this.x * this.y;
        double double6 = this.y * this.z;
        double double7 = this.x * this.w;
        arg0.x = -double4 + double5 - double4 + double5;
        arg0.y = double2 - double3 + double0 - double1;
        arg0.z = double6 + double6 + double7 + double7;
        return arg0;
    }

    @Override
    public Vector4d transformPositiveY(Vector4d arg0) {
        double double0 = this.w * this.w;
        double double1 = this.x * this.x;
        double double2 = this.y * this.y;
        double double3 = this.z * this.z;
        double double4 = this.z * this.w;
        double double5 = this.x * this.y;
        double double6 = this.y * this.z;
        double double7 = this.x * this.w;
        arg0.x = -double4 + double5 - double4 + double5;
        arg0.y = double2 - double3 + double0 - double1;
        arg0.z = double6 + double6 + double7 + double7;
        return arg0;
    }

    @Override
    public Vector4d transformUnitPositiveY(Vector4d arg0) {
        double double0 = this.x * this.x;
        double double1 = this.z * this.z;
        double double2 = this.x * this.y;
        double double3 = this.y * this.z;
        double double4 = this.x * this.w;
        double double5 = this.z * this.w;
        arg0.x = double2 - double5 + double2 - double5;
        arg0.y = 1.0 - double0 - double0 - double1 - double1;
        arg0.z = double3 + double3 + double4 + double4;
        return arg0;
    }

    @Override
    public Vector3d transformUnitPositiveY(Vector3d arg0) {
        double double0 = this.x * this.x;
        double double1 = this.z * this.z;
        double double2 = this.x * this.y;
        double double3 = this.y * this.z;
        double double4 = this.x * this.w;
        double double5 = this.z * this.w;
        arg0.x = double2 - double5 + double2 - double5;
        arg0.y = 1.0 - double0 - double0 - double1 - double1;
        arg0.z = double3 + double3 + double4 + double4;
        return arg0;
    }

    @Override
    public Vector3d transformPositiveZ(Vector3d arg0) {
        double double0 = this.w * this.w;
        double double1 = this.x * this.x;
        double double2 = this.y * this.y;
        double double3 = this.z * this.z;
        double double4 = this.x * this.z;
        double double5 = this.y * this.w;
        double double6 = this.y * this.z;
        double double7 = this.x * this.w;
        arg0.x = double5 + double4 + double4 + double5;
        arg0.y = double6 + double6 - double7 - double7;
        arg0.z = double3 - double2 - double1 + double0;
        return arg0;
    }

    @Override
    public Vector4d transformPositiveZ(Vector4d arg0) {
        double double0 = this.w * this.w;
        double double1 = this.x * this.x;
        double double2 = this.y * this.y;
        double double3 = this.z * this.z;
        double double4 = this.x * this.z;
        double double5 = this.y * this.w;
        double double6 = this.y * this.z;
        double double7 = this.x * this.w;
        arg0.x = double5 + double4 + double4 + double5;
        arg0.y = double6 + double6 - double7 - double7;
        arg0.z = double3 - double2 - double1 + double0;
        return arg0;
    }

    @Override
    public Vector4d transformUnitPositiveZ(Vector4d arg0) {
        double double0 = this.x * this.x;
        double double1 = this.y * this.y;
        double double2 = this.x * this.z;
        double double3 = this.y * this.z;
        double double4 = this.x * this.w;
        double double5 = this.y * this.w;
        arg0.x = double2 + double5 + double2 + double5;
        arg0.y = double3 + double3 - double4 - double4;
        arg0.z = 1.0 - double0 - double0 - double1 - double1;
        return arg0;
    }

    @Override
    public Vector3d transformUnitPositiveZ(Vector3d arg0) {
        double double0 = this.x * this.x;
        double double1 = this.y * this.y;
        double double2 = this.x * this.z;
        double double3 = this.y * this.z;
        double double4 = this.x * this.w;
        double double5 = this.y * this.w;
        arg0.x = double2 + double5 + double2 + double5;
        arg0.y = double3 + double3 - double4 - double4;
        arg0.z = 1.0 - double0 - double0 - double1 - double1;
        return arg0;
    }

    @Override
    public Vector4d transform(Vector4d arg0) {
        return this.transform(arg0, arg0);
    }

    @Override
    public Vector4d transformInverse(Vector4d arg0) {
        return this.transformInverse(arg0, arg0);
    }

    @Override
    public Vector3d transform(Vector3dc arg0, Vector3d arg1) {
        return this.transform(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Vector3d transformInverse(Vector3dc arg0, Vector3d arg1) {
        return this.transformInverse(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Vector3d transform(double arg0, double arg1, double arg2, Vector3d arg3) {
        double double0 = this.x * this.x;
        double double1 = this.y * this.y;
        double double2 = this.z * this.z;
        double double3 = this.w * this.w;
        double double4 = this.x * this.y;
        double double5 = this.x * this.z;
        double double6 = this.y * this.z;
        double double7 = this.x * this.w;
        double double8 = this.z * this.w;
        double double9 = this.y * this.w;
        double double10 = 1.0 / (double0 + double1 + double2 + double3);
        return arg3.set(
            Math.fma(
                (double0 - double1 - double2 + double3) * double10,
                arg0,
                Math.fma(2.0 * (double4 - double8) * double10, arg1, 2.0 * (double5 + double9) * double10 * arg2)
            ),
            Math.fma(
                2.0 * (double4 + double8) * double10,
                arg0,
                Math.fma((double1 - double0 - double2 + double3) * double10, arg1, 2.0 * (double6 - double7) * double10 * arg2)
            ),
            Math.fma(
                2.0 * (double5 - double9) * double10,
                arg0,
                Math.fma(2.0 * (double6 + double7) * double10, arg1, (double2 - double0 - double1 + double3) * double10 * arg2)
            )
        );
    }

    @Override
    public Vector3d transformInverse(double arg0, double arg1, double arg2, Vector3d arg3) {
        double double0 = 1.0 / Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
        double double1 = this.x * double0;
        double double2 = this.y * double0;
        double double3 = this.z * double0;
        double double4 = this.w * double0;
        double double5 = double1 * double1;
        double double6 = double2 * double2;
        double double7 = double3 * double3;
        double double8 = double4 * double4;
        double double9 = double1 * double2;
        double double10 = double1 * double3;
        double double11 = double2 * double3;
        double double12 = double1 * double4;
        double double13 = double3 * double4;
        double double14 = double2 * double4;
        double double15 = 1.0 / (double5 + double6 + double7 + double8);
        return arg3.set(
            Math.fma(
                (double5 - double6 - double7 + double8) * double15,
                arg0,
                Math.fma(2.0 * (double9 + double13) * double15, arg1, 2.0 * (double10 - double14) * double15 * arg2)
            ),
            Math.fma(
                2.0 * (double9 - double13) * double15,
                arg0,
                Math.fma((double6 - double5 - double7 + double8) * double15, arg1, 2.0 * (double11 + double12) * double15 * arg2)
            ),
            Math.fma(
                2.0 * (double10 + double14) * double15,
                arg0,
                Math.fma(2.0 * (double11 - double12) * double15, arg1, (double7 - double5 - double6 + double8) * double15 * arg2)
            )
        );
    }

    @Override
    public Vector4d transform(Vector4dc arg0, Vector4d arg1) {
        return this.transform(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Vector4d transformInverse(Vector4dc arg0, Vector4d arg1) {
        return this.transformInverse(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Vector4d transform(double arg0, double arg1, double arg2, Vector4d arg3) {
        double double0 = this.x * this.x;
        double double1 = this.y * this.y;
        double double2 = this.z * this.z;
        double double3 = this.w * this.w;
        double double4 = this.x * this.y;
        double double5 = this.x * this.z;
        double double6 = this.y * this.z;
        double double7 = this.x * this.w;
        double double8 = this.z * this.w;
        double double9 = this.y * this.w;
        double double10 = 1.0 / (double0 + double1 + double2 + double3);
        return arg3.set(
            Math.fma(
                (double0 - double1 - double2 + double3) * double10,
                arg0,
                Math.fma(2.0 * (double4 - double8) * double10, arg1, 2.0 * (double5 + double9) * double10 * arg2)
            ),
            Math.fma(
                2.0 * (double4 + double8) * double10,
                arg0,
                Math.fma((double1 - double0 - double2 + double3) * double10, arg1, 2.0 * (double6 - double7) * double10 * arg2)
            ),
            Math.fma(
                2.0 * (double5 - double9) * double10,
                arg0,
                Math.fma(2.0 * (double6 + double7) * double10, arg1, (double2 - double0 - double1 + double3) * double10 * arg2)
            ),
            arg3.w
        );
    }

    @Override
    public Vector4d transformInverse(double arg0, double arg1, double arg2, Vector4d arg3) {
        double double0 = 1.0 / Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
        double double1 = this.x * double0;
        double double2 = this.y * double0;
        double double3 = this.z * double0;
        double double4 = this.w * double0;
        double double5 = double1 * double1;
        double double6 = double2 * double2;
        double double7 = double3 * double3;
        double double8 = double4 * double4;
        double double9 = double1 * double2;
        double double10 = double1 * double3;
        double double11 = double2 * double3;
        double double12 = double1 * double4;
        double double13 = double3 * double4;
        double double14 = double2 * double4;
        double double15 = 1.0 / (double5 + double6 + double7 + double8);
        return arg3.set(
            Math.fma(
                (double5 - double6 - double7 + double8) * double15,
                arg0,
                Math.fma(2.0 * (double9 + double13) * double15, arg1, 2.0 * (double10 - double14) * double15 * arg2)
            ),
            Math.fma(
                2.0 * (double9 - double13) * double15,
                arg0,
                Math.fma((double6 - double5 - double7 + double8) * double15, arg1, 2.0 * (double11 + double12) * double15 * arg2)
            ),
            Math.fma(
                2.0 * (double10 + double14) * double15,
                arg0,
                Math.fma(2.0 * (double11 - double12) * double15, arg1, (double7 - double5 - double6 + double8) * double15 * arg2)
            )
        );
    }

    @Override
    public Vector3f transform(Vector3f arg0) {
        return this.transform((double)arg0.x, (double)arg0.y, (double)arg0.z, arg0);
    }

    @Override
    public Vector3f transformInverse(Vector3f arg0) {
        return this.transformInverse((double)arg0.x, (double)arg0.y, (double)arg0.z, arg0);
    }

    @Override
    public Vector4d transformUnit(Vector4d arg0) {
        return this.transformUnit(arg0, arg0);
    }

    @Override
    public Vector4d transformInverseUnit(Vector4d arg0) {
        return this.transformInverseUnit(arg0, arg0);
    }

    @Override
    public Vector3d transformUnit(Vector3dc arg0, Vector3d arg1) {
        return this.transformUnit(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Vector3d transformInverseUnit(Vector3dc arg0, Vector3d arg1) {
        return this.transformInverseUnit(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Vector3d transformUnit(double arg0, double arg1, double arg2, Vector3d arg3) {
        double double0 = this.x * this.x;
        double double1 = this.x * this.y;
        double double2 = this.x * this.z;
        double double3 = this.x * this.w;
        double double4 = this.y * this.y;
        double double5 = this.y * this.z;
        double double6 = this.y * this.w;
        double double7 = this.z * this.z;
        double double8 = this.z * this.w;
        return arg3.set(
            Math.fma(Math.fma(-2.0, double4 + double7, 1.0), arg0, Math.fma(2.0 * (double1 - double8), arg1, 2.0 * (double2 + double6) * arg2)),
            Math.fma(2.0 * (double1 + double8), arg0, Math.fma(Math.fma(-2.0, double0 + double7, 1.0), arg1, 2.0 * (double5 - double3) * arg2)),
            Math.fma(2.0 * (double2 - double6), arg0, Math.fma(2.0 * (double5 + double3), arg1, Math.fma(-2.0, double0 + double4, 1.0) * arg2))
        );
    }

    @Override
    public Vector3d transformInverseUnit(double arg0, double arg1, double arg2, Vector3d arg3) {
        double double0 = this.x * this.x;
        double double1 = this.x * this.y;
        double double2 = this.x * this.z;
        double double3 = this.x * this.w;
        double double4 = this.y * this.y;
        double double5 = this.y * this.z;
        double double6 = this.y * this.w;
        double double7 = this.z * this.z;
        double double8 = this.z * this.w;
        return arg3.set(
            Math.fma(Math.fma(-2.0, double4 + double7, 1.0), arg0, Math.fma(2.0 * (double1 + double8), arg1, 2.0 * (double2 - double6) * arg2)),
            Math.fma(2.0 * (double1 - double8), arg0, Math.fma(Math.fma(-2.0, double0 + double7, 1.0), arg1, 2.0 * (double5 + double3) * arg2)),
            Math.fma(2.0 * (double2 + double6), arg0, Math.fma(2.0 * (double5 - double3), arg1, Math.fma(-2.0, double0 + double4, 1.0) * arg2))
        );
    }

    @Override
    public Vector4d transformUnit(Vector4dc arg0, Vector4d arg1) {
        return this.transformUnit(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Vector4d transformInverseUnit(Vector4dc arg0, Vector4d arg1) {
        return this.transformInverseUnit(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Vector4d transformUnit(double arg0, double arg1, double arg2, Vector4d arg3) {
        double double0 = this.x * this.x;
        double double1 = this.x * this.y;
        double double2 = this.x * this.z;
        double double3 = this.x * this.w;
        double double4 = this.y * this.y;
        double double5 = this.y * this.z;
        double double6 = this.y * this.w;
        double double7 = this.z * this.z;
        double double8 = this.z * this.w;
        return arg3.set(
            Math.fma(Math.fma(-2.0, double4 + double7, 1.0), arg0, Math.fma(2.0 * (double1 - double8), arg1, 2.0 * (double2 + double6) * arg2)),
            Math.fma(2.0 * (double1 + double8), arg0, Math.fma(Math.fma(-2.0, double0 + double7, 1.0), arg1, 2.0 * (double5 - double3) * arg2)),
            Math.fma(2.0 * (double2 - double6), arg0, Math.fma(2.0 * (double5 + double3), arg1, Math.fma(-2.0, double0 + double4, 1.0) * arg2)),
            arg3.w
        );
    }

    @Override
    public Vector4d transformInverseUnit(double arg0, double arg1, double arg2, Vector4d arg3) {
        double double0 = this.x * this.x;
        double double1 = this.x * this.y;
        double double2 = this.x * this.z;
        double double3 = this.x * this.w;
        double double4 = this.y * this.y;
        double double5 = this.y * this.z;
        double double6 = this.y * this.w;
        double double7 = this.z * this.z;
        double double8 = this.z * this.w;
        return arg3.set(
            Math.fma(Math.fma(-2.0, double4 + double7, 1.0), arg0, Math.fma(2.0 * (double1 + double8), arg1, 2.0 * (double2 - double6) * arg2)),
            Math.fma(2.0 * (double1 - double8), arg0, Math.fma(Math.fma(-2.0, double0 + double7, 1.0), arg1, 2.0 * (double5 + double3) * arg2)),
            Math.fma(2.0 * (double2 + double6), arg0, Math.fma(2.0 * (double5 - double3), arg1, Math.fma(-2.0, double0 + double4, 1.0) * arg2)),
            arg3.w
        );
    }

    @Override
    public Vector3f transformUnit(Vector3f arg0) {
        return this.transformUnit((double)arg0.x, (double)arg0.y, (double)arg0.z, arg0);
    }

    @Override
    public Vector3f transformInverseUnit(Vector3f arg0) {
        return this.transformInverseUnit((double)arg0.x, (double)arg0.y, (double)arg0.z, arg0);
    }

    @Override
    public Vector3f transformPositiveX(Vector3f arg0) {
        double double0 = this.w * this.w;
        double double1 = this.x * this.x;
        double double2 = this.y * this.y;
        double double3 = this.z * this.z;
        double double4 = this.z * this.w;
        double double5 = this.x * this.y;
        double double6 = this.x * this.z;
        double double7 = this.y * this.w;
        arg0.x = (float)(double0 + double1 - double3 - double2);
        arg0.y = (float)(double5 + double4 + double4 + double5);
        arg0.z = (float)(double6 - double7 + double6 - double7);
        return arg0;
    }

    @Override
    public Vector4f transformPositiveX(Vector4f arg0) {
        double double0 = this.w * this.w;
        double double1 = this.x * this.x;
        double double2 = this.y * this.y;
        double double3 = this.z * this.z;
        double double4 = this.z * this.w;
        double double5 = this.x * this.y;
        double double6 = this.x * this.z;
        double double7 = this.y * this.w;
        arg0.x = (float)(double0 + double1 - double3 - double2);
        arg0.y = (float)(double5 + double4 + double4 + double5);
        arg0.z = (float)(double6 - double7 + double6 - double7);
        return arg0;
    }

    @Override
    public Vector3f transformUnitPositiveX(Vector3f arg0) {
        double double0 = this.y * this.y;
        double double1 = this.z * this.z;
        double double2 = this.x * this.y;
        double double3 = this.x * this.z;
        double double4 = this.y * this.w;
        double double5 = this.z * this.w;
        arg0.x = (float)(1.0 - double0 - double0 - double1 - double1);
        arg0.y = (float)(double2 + double5 + double2 + double5);
        arg0.z = (float)(double3 - double4 + double3 - double4);
        return arg0;
    }

    @Override
    public Vector4f transformUnitPositiveX(Vector4f arg0) {
        double double0 = this.y * this.y;
        double double1 = this.z * this.z;
        double double2 = this.x * this.y;
        double double3 = this.x * this.z;
        double double4 = this.y * this.w;
        double double5 = this.z * this.w;
        arg0.x = (float)(1.0 - double0 - double0 - double1 - double1);
        arg0.y = (float)(double2 + double5 + double2 + double5);
        arg0.z = (float)(double3 - double4 + double3 - double4);
        return arg0;
    }

    @Override
    public Vector3f transformPositiveY(Vector3f arg0) {
        double double0 = this.w * this.w;
        double double1 = this.x * this.x;
        double double2 = this.y * this.y;
        double double3 = this.z * this.z;
        double double4 = this.z * this.w;
        double double5 = this.x * this.y;
        double double6 = this.y * this.z;
        double double7 = this.x * this.w;
        arg0.x = (float)(-double4 + double5 - double4 + double5);
        arg0.y = (float)(double2 - double3 + double0 - double1);
        arg0.z = (float)(double6 + double6 + double7 + double7);
        return arg0;
    }

    @Override
    public Vector4f transformPositiveY(Vector4f arg0) {
        double double0 = this.w * this.w;
        double double1 = this.x * this.x;
        double double2 = this.y * this.y;
        double double3 = this.z * this.z;
        double double4 = this.z * this.w;
        double double5 = this.x * this.y;
        double double6 = this.y * this.z;
        double double7 = this.x * this.w;
        arg0.x = (float)(-double4 + double5 - double4 + double5);
        arg0.y = (float)(double2 - double3 + double0 - double1);
        arg0.z = (float)(double6 + double6 + double7 + double7);
        return arg0;
    }

    @Override
    public Vector4f transformUnitPositiveY(Vector4f arg0) {
        double double0 = this.x * this.x;
        double double1 = this.z * this.z;
        double double2 = this.x * this.y;
        double double3 = this.y * this.z;
        double double4 = this.x * this.w;
        double double5 = this.z * this.w;
        arg0.x = (float)(double2 - double5 + double2 - double5);
        arg0.y = (float)(1.0 - double0 - double0 - double1 - double1);
        arg0.z = (float)(double3 + double3 + double4 + double4);
        return arg0;
    }

    @Override
    public Vector3f transformUnitPositiveY(Vector3f arg0) {
        double double0 = this.x * this.x;
        double double1 = this.z * this.z;
        double double2 = this.x * this.y;
        double double3 = this.y * this.z;
        double double4 = this.x * this.w;
        double double5 = this.z * this.w;
        arg0.x = (float)(double2 - double5 + double2 - double5);
        arg0.y = (float)(1.0 - double0 - double0 - double1 - double1);
        arg0.z = (float)(double3 + double3 + double4 + double4);
        return arg0;
    }

    @Override
    public Vector3f transformPositiveZ(Vector3f arg0) {
        double double0 = this.w * this.w;
        double double1 = this.x * this.x;
        double double2 = this.y * this.y;
        double double3 = this.z * this.z;
        double double4 = this.x * this.z;
        double double5 = this.y * this.w;
        double double6 = this.y * this.z;
        double double7 = this.x * this.w;
        arg0.x = (float)(double5 + double4 + double4 + double5);
        arg0.y = (float)(double6 + double6 - double7 - double7);
        arg0.z = (float)(double3 - double2 - double1 + double0);
        return arg0;
    }

    @Override
    public Vector4f transformPositiveZ(Vector4f arg0) {
        double double0 = this.w * this.w;
        double double1 = this.x * this.x;
        double double2 = this.y * this.y;
        double double3 = this.z * this.z;
        double double4 = this.x * this.z;
        double double5 = this.y * this.w;
        double double6 = this.y * this.z;
        double double7 = this.x * this.w;
        arg0.x = (float)(double5 + double4 + double4 + double5);
        arg0.y = (float)(double6 + double6 - double7 - double7);
        arg0.z = (float)(double3 - double2 - double1 + double0);
        return arg0;
    }

    @Override
    public Vector4f transformUnitPositiveZ(Vector4f arg0) {
        double double0 = this.x * this.x;
        double double1 = this.y * this.y;
        double double2 = this.x * this.z;
        double double3 = this.y * this.z;
        double double4 = this.x * this.w;
        double double5 = this.y * this.w;
        arg0.x = (float)(double2 + double5 + double2 + double5);
        arg0.y = (float)(double3 + double3 - double4 - double4);
        arg0.z = (float)(1.0 - double0 - double0 - double1 - double1);
        return arg0;
    }

    @Override
    public Vector3f transformUnitPositiveZ(Vector3f arg0) {
        double double0 = this.x * this.x;
        double double1 = this.y * this.y;
        double double2 = this.x * this.z;
        double double3 = this.y * this.z;
        double double4 = this.x * this.w;
        double double5 = this.y * this.w;
        arg0.x = (float)(double2 + double5 + double2 + double5);
        arg0.y = (float)(double3 + double3 - double4 - double4);
        arg0.z = (float)(1.0 - double0 - double0 - double1 - double1);
        return arg0;
    }

    @Override
    public Vector4f transform(Vector4f arg0) {
        return this.transform(arg0, arg0);
    }

    @Override
    public Vector4f transformInverse(Vector4f arg0) {
        return this.transformInverse(arg0, arg0);
    }

    @Override
    public Vector3f transform(Vector3fc arg0, Vector3f arg1) {
        return this.transform((double)arg0.x(), (double)arg0.y(), (double)arg0.z(), arg1);
    }

    @Override
    public Vector3f transformInverse(Vector3fc arg0, Vector3f arg1) {
        return this.transformInverse((double)arg0.x(), (double)arg0.y(), (double)arg0.z(), arg1);
    }

    @Override
    public Vector3f transform(double arg0, double arg1, double arg2, Vector3f arg3) {
        double double0 = this.x * this.x;
        double double1 = this.y * this.y;
        double double2 = this.z * this.z;
        double double3 = this.w * this.w;
        double double4 = this.x * this.y;
        double double5 = this.x * this.z;
        double double6 = this.y * this.z;
        double double7 = this.x * this.w;
        double double8 = this.z * this.w;
        double double9 = this.y * this.w;
        double double10 = 1.0 / (double0 + double1 + double2 + double3);
        return arg3.set(
            Math.fma(
                (double0 - double1 - double2 + double3) * double10,
                arg0,
                Math.fma(2.0 * (double4 - double8) * double10, arg1, 2.0 * (double5 + double9) * double10 * arg2)
            ),
            Math.fma(
                2.0 * (double4 + double8) * double10,
                arg0,
                Math.fma((double1 - double0 - double2 + double3) * double10, arg1, 2.0 * (double6 - double7) * double10 * arg2)
            ),
            Math.fma(
                2.0 * (double5 - double9) * double10,
                arg0,
                Math.fma(2.0 * (double6 + double7) * double10, arg1, (double2 - double0 - double1 + double3) * double10 * arg2)
            )
        );
    }

    @Override
    public Vector3f transformInverse(double arg0, double arg1, double arg2, Vector3f arg3) {
        double double0 = 1.0 / Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
        double double1 = this.x * double0;
        double double2 = this.y * double0;
        double double3 = this.z * double0;
        double double4 = this.w * double0;
        double double5 = double1 * double1;
        double double6 = double2 * double2;
        double double7 = double3 * double3;
        double double8 = double4 * double4;
        double double9 = double1 * double2;
        double double10 = double1 * double3;
        double double11 = double2 * double3;
        double double12 = double1 * double4;
        double double13 = double3 * double4;
        double double14 = double2 * double4;
        double double15 = 1.0 / (double5 + double6 + double7 + double8);
        return arg3.set(
            Math.fma(
                (double5 - double6 - double7 + double8) * double15,
                arg0,
                Math.fma(2.0 * (double9 + double13) * double15, arg1, 2.0 * (double10 - double14) * double15 * arg2)
            ),
            Math.fma(
                2.0 * (double9 - double13) * double15,
                arg0,
                Math.fma((double6 - double5 - double7 + double8) * double15, arg1, 2.0 * (double11 + double12) * double15 * arg2)
            ),
            Math.fma(
                2.0 * (double10 + double14) * double15,
                arg0,
                Math.fma(2.0 * (double11 - double12) * double15, arg1, (double7 - double5 - double6 + double8) * double15 * arg2)
            )
        );
    }

    @Override
    public Vector4f transform(Vector4fc arg0, Vector4f arg1) {
        return this.transform((double)arg0.x(), (double)arg0.y(), (double)arg0.z(), arg1);
    }

    @Override
    public Vector4f transformInverse(Vector4fc arg0, Vector4f arg1) {
        return this.transformInverse((double)arg0.x(), (double)arg0.y(), (double)arg0.z(), arg1);
    }

    @Override
    public Vector4f transform(double arg0, double arg1, double arg2, Vector4f arg3) {
        double double0 = this.x * this.x;
        double double1 = this.y * this.y;
        double double2 = this.z * this.z;
        double double3 = this.w * this.w;
        double double4 = this.x * this.y;
        double double5 = this.x * this.z;
        double double6 = this.y * this.z;
        double double7 = this.x * this.w;
        double double8 = this.z * this.w;
        double double9 = this.y * this.w;
        double double10 = 1.0 / (double0 + double1 + double2 + double3);
        return arg3.set(
            (float)Math.fma(
                (double0 - double1 - double2 + double3) * double10,
                arg0,
                Math.fma(2.0 * (double4 - double8) * double10, arg1, 2.0 * (double5 + double9) * double10 * arg2)
            ),
            (float)Math.fma(
                2.0 * (double4 + double8) * double10,
                arg0,
                Math.fma((double1 - double0 - double2 + double3) * double10, arg1, 2.0 * (double6 - double7) * double10 * arg2)
            ),
            (float)Math.fma(
                2.0 * (double5 - double9) * double10,
                arg0,
                Math.fma(2.0 * (double6 + double7) * double10, arg1, (double2 - double0 - double1 + double3) * double10 * arg2)
            ),
            arg3.w
        );
    }

    @Override
    public Vector4f transformInverse(double arg0, double arg1, double arg2, Vector4f arg3) {
        double double0 = 1.0 / Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
        double double1 = this.x * double0;
        double double2 = this.y * double0;
        double double3 = this.z * double0;
        double double4 = this.w * double0;
        double double5 = double1 * double1;
        double double6 = double2 * double2;
        double double7 = double3 * double3;
        double double8 = double4 * double4;
        double double9 = double1 * double2;
        double double10 = double1 * double3;
        double double11 = double2 * double3;
        double double12 = double1 * double4;
        double double13 = double3 * double4;
        double double14 = double2 * double4;
        double double15 = 1.0 / (double5 + double6 + double7 + double8);
        return arg3.set(
            Math.fma(
                (double5 - double6 - double7 + double8) * double15,
                arg0,
                Math.fma(2.0 * (double9 + double13) * double15, arg1, 2.0 * (double10 - double14) * double15 * arg2)
            ),
            Math.fma(
                2.0 * (double9 - double13) * double15,
                arg0,
                Math.fma((double6 - double5 - double7 + double8) * double15, arg1, 2.0 * (double11 + double12) * double15 * arg2)
            ),
            Math.fma(
                2.0 * (double10 + double14) * double15,
                arg0,
                Math.fma(2.0 * (double11 - double12) * double15, arg1, (double7 - double5 - double6 + double8) * double15 * arg2)
            ),
            (double)arg3.w
        );
    }

    @Override
    public Vector4f transformUnit(Vector4f arg0) {
        return this.transformUnit(arg0, arg0);
    }

    @Override
    public Vector4f transformInverseUnit(Vector4f arg0) {
        return this.transformInverseUnit(arg0, arg0);
    }

    @Override
    public Vector3f transformUnit(Vector3fc arg0, Vector3f arg1) {
        return this.transformUnit((double)arg0.x(), (double)arg0.y(), (double)arg0.z(), arg1);
    }

    @Override
    public Vector3f transformInverseUnit(Vector3fc arg0, Vector3f arg1) {
        return this.transformInverseUnit((double)arg0.x(), (double)arg0.y(), (double)arg0.z(), arg1);
    }

    @Override
    public Vector3f transformUnit(double arg0, double arg1, double arg2, Vector3f arg3) {
        double double0 = this.x * this.x;
        double double1 = this.x * this.y;
        double double2 = this.x * this.z;
        double double3 = this.x * this.w;
        double double4 = this.y * this.y;
        double double5 = this.y * this.z;
        double double6 = this.y * this.w;
        double double7 = this.z * this.z;
        double double8 = this.z * this.w;
        return arg3.set(
            (float)Math.fma(Math.fma(-2.0, double4 + double7, 1.0), arg0, Math.fma(2.0 * (double1 - double8), arg1, 2.0 * (double2 + double6) * arg2)),
            (float)Math.fma(2.0 * (double1 + double8), arg0, Math.fma(Math.fma(-2.0, double0 + double7, 1.0), arg1, 2.0 * (double5 - double3) * arg2)),
            (float)Math.fma(2.0 * (double2 - double6), arg0, Math.fma(2.0 * (double5 + double3), arg1, Math.fma(-2.0, double0 + double4, 1.0) * arg2))
        );
    }

    @Override
    public Vector3f transformInverseUnit(double arg0, double arg1, double arg2, Vector3f arg3) {
        double double0 = this.x * this.x;
        double double1 = this.x * this.y;
        double double2 = this.x * this.z;
        double double3 = this.x * this.w;
        double double4 = this.y * this.y;
        double double5 = this.y * this.z;
        double double6 = this.y * this.w;
        double double7 = this.z * this.z;
        double double8 = this.z * this.w;
        return arg3.set(
            (float)Math.fma(Math.fma(-2.0, double4 + double7, 1.0), arg0, Math.fma(2.0 * (double1 + double8), arg1, 2.0 * (double2 - double6) * arg2)),
            (float)Math.fma(2.0 * (double1 - double8), arg0, Math.fma(Math.fma(-2.0, double0 + double7, 1.0), arg1, 2.0 * (double5 + double3) * arg2)),
            (float)Math.fma(2.0 * (double2 + double6), arg0, Math.fma(2.0 * (double5 - double3), arg1, Math.fma(-2.0, double0 + double4, 1.0) * arg2))
        );
    }

    @Override
    public Vector4f transformUnit(Vector4fc arg0, Vector4f arg1) {
        return this.transformUnit((double)arg0.x(), (double)arg0.y(), (double)arg0.z(), arg1);
    }

    @Override
    public Vector4f transformInverseUnit(Vector4fc arg0, Vector4f arg1) {
        return this.transformInverseUnit((double)arg0.x(), (double)arg0.y(), (double)arg0.z(), arg1);
    }

    @Override
    public Vector4f transformUnit(double arg0, double arg1, double arg2, Vector4f arg3) {
        double double0 = this.x * this.x;
        double double1 = this.x * this.y;
        double double2 = this.x * this.z;
        double double3 = this.x * this.w;
        double double4 = this.y * this.y;
        double double5 = this.y * this.z;
        double double6 = this.y * this.w;
        double double7 = this.z * this.z;
        double double8 = this.z * this.w;
        return arg3.set(
            (float)Math.fma(Math.fma(-2.0, double4 + double7, 1.0), arg0, Math.fma(2.0 * (double1 - double8), arg1, 2.0 * (double2 + double6) * arg2)),
            (float)Math.fma(2.0 * (double1 + double8), arg0, Math.fma(Math.fma(-2.0, double0 + double7, 1.0), arg1, 2.0 * (double5 - double3) * arg2)),
            (float)Math.fma(2.0 * (double2 - double6), arg0, Math.fma(2.0 * (double5 + double3), arg1, Math.fma(-2.0, double0 + double4, 1.0) * arg2))
        );
    }

    @Override
    public Vector4f transformInverseUnit(double arg0, double arg1, double arg2, Vector4f arg3) {
        double double0 = this.x * this.x;
        double double1 = this.x * this.y;
        double double2 = this.x * this.z;
        double double3 = this.x * this.w;
        double double4 = this.y * this.y;
        double double5 = this.y * this.z;
        double double6 = this.y * this.w;
        double double7 = this.z * this.z;
        double double8 = this.z * this.w;
        return arg3.set(
            (float)Math.fma(Math.fma(-2.0, double4 + double7, 1.0), arg0, Math.fma(2.0 * (double1 + double8), arg1, 2.0 * (double2 - double6) * arg2)),
            (float)Math.fma(2.0 * (double1 - double8), arg0, Math.fma(Math.fma(-2.0, double0 + double7, 1.0), arg1, 2.0 * (double5 + double3) * arg2)),
            (float)Math.fma(2.0 * (double2 + double6), arg0, Math.fma(2.0 * (double5 - double3), arg1, Math.fma(-2.0, double0 + double4, 1.0) * arg2))
        );
    }

    @Override
    public Quaterniond invert(Quaterniond arg0) {
        double double0 = 1.0 / this.lengthSquared();
        arg0.x = -this.x * double0;
        arg0.y = -this.y * double0;
        arg0.z = -this.z * double0;
        arg0.w = this.w * double0;
        return arg0;
    }

    public Quaterniond invert() {
        return this.invert(this);
    }

    @Override
    public Quaterniond div(Quaterniondc arg0, Quaterniond arg1) {
        double double0 = 1.0 / Math.fma(arg0.x(), arg0.x(), Math.fma(arg0.y(), arg0.y(), Math.fma(arg0.z(), arg0.z(), arg0.w() * arg0.w())));
        double double1 = -arg0.x() * double0;
        double double2 = -arg0.y() * double0;
        double double3 = -arg0.z() * double0;
        double double4 = arg0.w() * double0;
        return arg1.set(
            Math.fma(this.w, double1, Math.fma(this.x, double4, Math.fma(this.y, double3, -this.z * double2))),
            Math.fma(this.w, double2, Math.fma(-this.x, double3, Math.fma(this.y, double4, this.z * double1))),
            Math.fma(this.w, double3, Math.fma(this.x, double2, Math.fma(-this.y, double1, this.z * double4))),
            Math.fma(this.w, double4, Math.fma(-this.x, double1, Math.fma(-this.y, double2, -this.z * double3)))
        );
    }

    public Quaterniond div(Quaterniondc arg0) {
        return this.div(arg0, this);
    }

    public Quaterniond conjugate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    @Override
    public Quaterniond conjugate(Quaterniond arg0) {
        arg0.x = -this.x;
        arg0.y = -this.y;
        arg0.z = -this.z;
        arg0.w = this.w;
        return arg0;
    }

    public Quaterniond identity() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        this.w = 1.0;
        return this;
    }

    @Override
    public double lengthSquared() {
        return Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
    }

    public Quaterniond rotationXYZ(double arg0, double arg1, double arg2) {
        double double0 = Math.sin(arg0 * 0.5);
        double double1 = Math.cosFromSin(double0, arg0 * 0.5);
        double double2 = Math.sin(arg1 * 0.5);
        double double3 = Math.cosFromSin(double2, arg1 * 0.5);
        double double4 = Math.sin(arg2 * 0.5);
        double double5 = Math.cosFromSin(double4, arg2 * 0.5);
        double double6 = double3 * double5;
        double double7 = double2 * double4;
        double double8 = double2 * double5;
        double double9 = double3 * double4;
        this.w = double1 * double6 - double0 * double7;
        this.x = double0 * double6 + double1 * double7;
        this.y = double1 * double8 - double0 * double9;
        this.z = double1 * double9 + double0 * double8;
        return this;
    }

    public Quaterniond rotationZYX(double arg0, double arg1, double arg2) {
        double double0 = Math.sin(arg2 * 0.5);
        double double1 = Math.cosFromSin(double0, arg2 * 0.5);
        double double2 = Math.sin(arg1 * 0.5);
        double double3 = Math.cosFromSin(double2, arg1 * 0.5);
        double double4 = Math.sin(arg0 * 0.5);
        double double5 = Math.cosFromSin(double4, arg0 * 0.5);
        double double6 = double3 * double5;
        double double7 = double2 * double4;
        double double8 = double2 * double5;
        double double9 = double3 * double4;
        this.w = double1 * double6 + double0 * double7;
        this.x = double0 * double6 - double1 * double7;
        this.y = double1 * double8 + double0 * double9;
        this.z = double1 * double9 - double0 * double8;
        return this;
    }

    public Quaterniond rotationYXZ(double arg0, double arg1, double arg2) {
        double double0 = Math.sin(arg1 * 0.5);
        double double1 = Math.cosFromSin(double0, arg1 * 0.5);
        double double2 = Math.sin(arg0 * 0.5);
        double double3 = Math.cosFromSin(double2, arg0 * 0.5);
        double double4 = Math.sin(arg2 * 0.5);
        double double5 = Math.cosFromSin(double4, arg2 * 0.5);
        double double6 = double3 * double0;
        double double7 = double2 * double1;
        double double8 = double2 * double0;
        double double9 = double3 * double1;
        this.x = double6 * double5 + double7 * double4;
        this.y = double7 * double5 - double6 * double4;
        this.z = double9 * double4 - double8 * double5;
        this.w = double9 * double5 + double8 * double4;
        return this;
    }

    public Quaterniond slerp(Quaterniondc arg0, double arg1) {
        return this.slerp(arg0, arg1, this);
    }

    @Override
    public Quaterniond slerp(Quaterniondc arg0, double arg1, Quaterniond arg2) {
        double double0 = Math.fma(this.x, arg0.x(), Math.fma(this.y, arg0.y(), Math.fma(this.z, arg0.z(), this.w * arg0.w())));
        double double1 = Math.abs(double0);
        double double2;
        double double3;
        if (1.0 - double1 > 1.0E-6) {
            double double4 = 1.0 - double1 * double1;
            double double5 = Math.invsqrt(double4);
            double double6 = Math.atan2(double4 * double5, double1);
            double2 = Math.sin((1.0 - arg1) * double6) * double5;
            double3 = Math.sin(arg1 * double6) * double5;
        } else {
            double2 = 1.0 - arg1;
            double3 = arg1;
        }

        double3 = double0 >= 0.0 ? double3 : -double3;
        arg2.x = Math.fma(double2, this.x, double3 * arg0.x());
        arg2.y = Math.fma(double2, this.y, double3 * arg0.y());
        arg2.z = Math.fma(double2, this.z, double3 * arg0.z());
        arg2.w = Math.fma(double2, this.w, double3 * arg0.w());
        return arg2;
    }

    public static Quaterniondc slerp(Quaterniond[] quaternionds, double[] doubles, Quaterniond quaterniond) {
        quaterniond.set(quaternionds[0]);
        double double0 = doubles[0];

        for (int int0 = 1; int0 < quaternionds.length; int0++) {
            double double1 = doubles[int0];
            double double2 = double1 / (double0 + double1);
            double0 += double1;
            quaterniond.slerp(quaternionds[int0], double2);
        }

        return quaterniond;
    }

    public Quaterniond scale(double arg0) {
        return this.scale(arg0, this);
    }

    @Override
    public Quaterniond scale(double arg0, Quaterniond arg1) {
        double double0 = Math.sqrt(arg0);
        arg1.x = double0 * this.x;
        arg1.y = double0 * this.y;
        arg1.z = double0 * this.z;
        arg1.w = double0 * this.w;
        return arg1;
    }

    public Quaterniond scaling(double arg0) {
        double double0 = Math.sqrt(arg0);
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        this.w = double0;
        return this;
    }

    public Quaterniond integrate(double arg0, double arg1, double arg2, double arg3) {
        return this.integrate(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Quaterniond integrate(double arg0, double arg1, double arg2, double arg3, Quaterniond arg4) {
        double double0 = arg0 * arg1 * 0.5;
        double double1 = arg0 * arg2 * 0.5;
        double double2 = arg0 * arg3 * 0.5;
        double double3 = double0 * double0 + double1 * double1 + double2 * double2;
        double double4;
        double double5;
        if (double3 * double3 / 24.0 < 1.0E-8) {
            double5 = 1.0 - double3 * 0.5;
            double4 = 1.0 - double3 / 6.0;
        } else {
            double double6 = Math.sqrt(double3);
            double double7 = Math.sin(double6);
            double4 = double7 / double6;
            double5 = Math.cosFromSin(double7, double6);
        }

        double double8 = double0 * double4;
        double double9 = double1 * double4;
        double double10 = double2 * double4;
        return arg4.set(
            Math.fma(double5, this.x, Math.fma(double8, this.w, Math.fma(double9, this.z, -double10 * this.y))),
            Math.fma(double5, this.y, Math.fma(-double8, this.z, Math.fma(double9, this.w, double10 * this.x))),
            Math.fma(double5, this.z, Math.fma(double8, this.y, Math.fma(-double9, this.x, double10 * this.w))),
            Math.fma(double5, this.w, Math.fma(-double8, this.x, Math.fma(-double9, this.y, -double10 * this.z)))
        );
    }

    public Quaterniond nlerp(Quaterniondc arg0, double arg1) {
        return this.nlerp(arg0, arg1, this);
    }

    @Override
    public Quaterniond nlerp(Quaterniondc arg0, double arg1, Quaterniond arg2) {
        double double0 = Math.fma(this.x, arg0.x(), Math.fma(this.y, arg0.y(), Math.fma(this.z, arg0.z(), this.w * arg0.w())));
        double double1 = 1.0 - arg1;
        double double2 = double0 >= 0.0 ? arg1 : -arg1;
        arg2.x = Math.fma(double1, this.x, double2 * arg0.x());
        arg2.y = Math.fma(double1, this.y, double2 * arg0.y());
        arg2.z = Math.fma(double1, this.z, double2 * arg0.z());
        arg2.w = Math.fma(double1, this.w, double2 * arg0.w());
        double double3 = Math.invsqrt(Math.fma(arg2.x, arg2.x, Math.fma(arg2.y, arg2.y, Math.fma(arg2.z, arg2.z, arg2.w * arg2.w))));
        arg2.x *= double3;
        arg2.y *= double3;
        arg2.z *= double3;
        arg2.w *= double3;
        return arg2;
    }

    public static Quaterniondc nlerp(Quaterniond[] quaternionds, double[] doubles, Quaterniond quaterniond) {
        quaterniond.set(quaternionds[0]);
        double double0 = doubles[0];

        for (int int0 = 1; int0 < quaternionds.length; int0++) {
            double double1 = doubles[int0];
            double double2 = double1 / (double0 + double1);
            double0 += double1;
            quaterniond.nlerp(quaternionds[int0], double2);
        }

        return quaterniond;
    }

    @Override
    public Quaterniond nlerpIterative(Quaterniondc arg0, double arg1, double arg2, Quaterniond arg3) {
        double double0 = this.x;
        double double1 = this.y;
        double double2 = this.z;
        double double3 = this.w;
        double double4 = arg0.x();
        double double5 = arg0.y();
        double double6 = arg0.z();
        double double7 = arg0.w();
        double double8 = Math.fma(double0, double4, Math.fma(double1, double5, Math.fma(double2, double6, double3 * double7)));
        double double9 = Math.abs(double8);
        if (0.999999 < double9) {
            return arg3.set(this);
        } else {
            double double10;
            for (double10 = arg1; double9 < arg2; double9 = Math.abs(double8)) {
                double double11 = 0.5;
                double double12 = double8 >= 0.0 ? 0.5 : -0.5;
                if (double10 < 0.5) {
                    double4 = Math.fma(double11, double4, double12 * double0);
                    double5 = Math.fma(double11, double5, double12 * double1);
                    double6 = Math.fma(double11, double6, double12 * double2);
                    double7 = Math.fma(double11, double7, double12 * double3);
                    float float0 = (float)Math.invsqrt(Math.fma(double4, double4, Math.fma(double5, double5, Math.fma(double6, double6, double7 * double7))));
                    double4 *= float0;
                    double5 *= float0;
                    double6 *= float0;
                    double7 *= float0;
                    double10 += double10;
                } else {
                    double0 = Math.fma(double11, double0, double12 * double4);
                    double1 = Math.fma(double11, double1, double12 * double5);
                    double2 = Math.fma(double11, double2, double12 * double6);
                    double3 = Math.fma(double11, double3, double12 * double7);
                    float float1 = (float)Math.invsqrt(Math.fma(double0, double0, Math.fma(double1, double1, Math.fma(double2, double2, double3 * double3))));
                    double0 *= float1;
                    double1 *= float1;
                    double2 *= float1;
                    double3 *= float1;
                    double10 = double10 + double10 - 1.0;
                }

                double8 = Math.fma(double0, double4, Math.fma(double1, double5, Math.fma(double2, double6, double3 * double7)));
            }

            double double13 = 1.0 - double10;
            double double14 = double8 >= 0.0 ? double10 : -double10;
            double double15 = Math.fma(double13, double0, double14 * double4);
            double double16 = Math.fma(double13, double1, double14 * double5);
            double double17 = Math.fma(double13, double2, double14 * double6);
            double double18 = Math.fma(double13, double3, double14 * double7);
            double double19 = Math.invsqrt(Math.fma(double15, double15, Math.fma(double16, double16, Math.fma(double17, double17, double18 * double18))));
            arg3.x = double15 * double19;
            arg3.y = double16 * double19;
            arg3.z = double17 * double19;
            arg3.w = double18 * double19;
            return arg3;
        }
    }

    public Quaterniond nlerpIterative(Quaterniondc arg0, double arg1, double arg2) {
        return this.nlerpIterative(arg0, arg1, arg2, this);
    }

    public static Quaterniond nlerpIterative(Quaterniondc[] quaterniondcs, double[] doubles, double double3, Quaterniond quaterniond) {
        quaterniond.set(quaterniondcs[0]);
        double double0 = doubles[0];

        for (int int0 = 1; int0 < quaterniondcs.length; int0++) {
            double double1 = doubles[int0];
            double double2 = double1 / (double0 + double1);
            double0 += double1;
            quaterniond.nlerpIterative(quaterniondcs[int0], double2, double3);
        }

        return quaterniond;
    }

    public Quaterniond lookAlong(Vector3dc arg0, Vector3dc arg1) {
        return this.lookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), this);
    }

    @Override
    public Quaterniond lookAlong(Vector3dc arg0, Vector3dc arg1, Quaterniond arg2) {
        return this.lookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    public Quaterniond lookAlong(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.lookAlong(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    @Override
    public Quaterniond lookAlong(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Quaterniond arg6) {
        double double0 = Math.invsqrt(arg0 * arg0 + arg1 * arg1 + arg2 * arg2);
        double double1 = -arg0 * double0;
        double double2 = -arg1 * double0;
        double double3 = -arg2 * double0;
        double double4 = arg4 * double3 - arg5 * double2;
        double double5 = arg5 * double1 - arg3 * double3;
        double double6 = arg3 * double2 - arg4 * double1;
        double double7 = Math.invsqrt(double4 * double4 + double5 * double5 + double6 * double6);
        double4 *= double7;
        double5 *= double7;
        double6 *= double7;
        double double8 = double2 * double6 - double3 * double5;
        double double9 = double3 * double4 - double1 * double6;
        double double10 = double1 * double5 - double2 * double4;
        double double11 = double4 + double9 + double3;
        double double12;
        double double13;
        double double14;
        double double15;
        if (double11 >= 0.0) {
            double double16 = Math.sqrt(double11 + 1.0);
            double15 = double16 * 0.5;
            double16 = 0.5 / double16;
            double12 = (double2 - double10) * double16;
            double13 = (double6 - double1) * double16;
            double14 = (double8 - double5) * double16;
        } else if (double4 > double9 && double4 > double3) {
            double double17 = Math.sqrt(1.0 + double4 - double9 - double3);
            double12 = double17 * 0.5;
            double17 = 0.5 / double17;
            double13 = (double5 + double8) * double17;
            double14 = (double1 + double6) * double17;
            double15 = (double2 - double10) * double17;
        } else if (double9 > double3) {
            double double18 = Math.sqrt(1.0 + double9 - double4 - double3);
            double13 = double18 * 0.5;
            double18 = 0.5 / double18;
            double12 = (double5 + double8) * double18;
            double14 = (double10 + double2) * double18;
            double15 = (double6 - double1) * double18;
        } else {
            double double19 = Math.sqrt(1.0 + double3 - double4 - double9);
            double14 = double19 * 0.5;
            double19 = 0.5 / double19;
            double12 = (double1 + double6) * double19;
            double13 = (double10 + double2) * double19;
            double15 = (double8 - double5) * double19;
        }

        return arg6.set(
            Math.fma(this.w, double12, Math.fma(this.x, double15, Math.fma(this.y, double14, -this.z * double13))),
            Math.fma(this.w, double13, Math.fma(-this.x, double14, Math.fma(this.y, double15, this.z * double12))),
            Math.fma(this.w, double14, Math.fma(this.x, double13, Math.fma(-this.y, double12, this.z * double15))),
            Math.fma(this.w, double15, Math.fma(-this.x, double12, Math.fma(-this.y, double13, -this.z * double14)))
        );
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
            + " "
            + Runtime.format(this.w, numberFormat)
            + ")";
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        arg0.writeDouble(this.x);
        arg0.writeDouble(this.y);
        arg0.writeDouble(this.z);
        arg0.writeDouble(this.w);
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        this.x = arg0.readDouble();
        this.y = arg0.readDouble();
        this.z = arg0.readDouble();
        this.w = arg0.readDouble();
    }

    @Override
    public int hashCode() {
        int int0 = 1;
        long long0 = Double.doubleToLongBits(this.w);
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
            Quaterniond quaterniond = (Quaterniond)arg0;
            if (Double.doubleToLongBits(this.w) != Double.doubleToLongBits(quaterniond.w)) {
                return false;
            } else if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(quaterniond.x)) {
                return false;
            } else {
                return Double.doubleToLongBits(this.y) != Double.doubleToLongBits(quaterniond.y)
                    ? false
                    : Double.doubleToLongBits(this.z) == Double.doubleToLongBits(quaterniond.z);
            }
        }
    }

    public Quaterniond difference(Quaterniondc arg0) {
        return this.difference(arg0, this);
    }

    @Override
    public Quaterniond difference(Quaterniondc arg0, Quaterniond arg1) {
        double double0 = 1.0 / this.lengthSquared();
        double double1 = -this.x * double0;
        double double2 = -this.y * double0;
        double double3 = -this.z * double0;
        double double4 = this.w * double0;
        arg1.set(
            Math.fma(double4, arg0.x(), Math.fma(double1, arg0.w(), Math.fma(double2, arg0.z(), -double3 * arg0.y()))),
            Math.fma(double4, arg0.y(), Math.fma(-double1, arg0.z(), Math.fma(double2, arg0.w(), double3 * arg0.x()))),
            Math.fma(double4, arg0.z(), Math.fma(double1, arg0.y(), Math.fma(-double2, arg0.x(), double3 * arg0.w()))),
            Math.fma(double4, arg0.w(), Math.fma(-double1, arg0.x(), Math.fma(-double2, arg0.y(), -double3 * arg0.z())))
        );
        return arg1;
    }

    public Quaterniond rotationTo(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        double double0 = Math.invsqrt(Math.fma(arg0, arg0, Math.fma(arg1, arg1, arg2 * arg2)));
        double double1 = Math.invsqrt(Math.fma(arg3, arg3, Math.fma(arg4, arg4, arg5 * arg5)));
        double double2 = arg0 * double0;
        double double3 = arg1 * double0;
        double double4 = arg2 * double0;
        double double5 = arg3 * double1;
        double double6 = arg4 * double1;
        double double7 = arg5 * double1;
        double double8 = double2 * double5 + double3 * double6 + double4 * double7;
        if (double8 < -0.999999) {
            double double9 = double3;
            double double10 = -double2;
            double double11 = 0.0;
            double double12 = 0.0;
            if (double3 * double3 + double10 * double10 == 0.0) {
                double9 = 0.0;
                double10 = double4;
                double11 = -double3;
                double12 = 0.0;
            }

            this.x = double9;
            this.y = double10;
            this.z = double11;
            this.w = 0.0;
        } else {
            double double13 = Math.sqrt((1.0 + double8) * 2.0);
            double double14 = 1.0 / double13;
            double double15 = double3 * double7 - double4 * double6;
            double double16 = double4 * double5 - double2 * double7;
            double double17 = double2 * double6 - double3 * double5;
            double double18 = double15 * double14;
            double double19 = double16 * double14;
            double double20 = double17 * double14;
            double double21 = double13 * 0.5;
            double double22 = Math.invsqrt(Math.fma(double18, double18, Math.fma(double19, double19, Math.fma(double20, double20, double21 * double21))));
            this.x = double18 * double22;
            this.y = double19 * double22;
            this.z = double20 * double22;
            this.w = double21 * double22;
        }

        return this;
    }

    public Quaterniond rotationTo(Vector3dc arg0, Vector3dc arg1) {
        return this.rotationTo(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    @Override
    public Quaterniond rotateTo(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Quaterniond arg6) {
        double double0 = Math.invsqrt(Math.fma(arg0, arg0, Math.fma(arg1, arg1, arg2 * arg2)));
        double double1 = Math.invsqrt(Math.fma(arg3, arg3, Math.fma(arg4, arg4, arg5 * arg5)));
        double double2 = arg0 * double0;
        double double3 = arg1 * double0;
        double double4 = arg2 * double0;
        double double5 = arg3 * double1;
        double double6 = arg4 * double1;
        double double7 = arg5 * double1;
        double double8 = double2 * double5 + double3 * double6 + double4 * double7;
        double double9;
        double double10;
        double double11;
        double double12;
        if (double8 < -0.999999) {
            double9 = double3;
            double10 = -double2;
            double11 = 0.0;
            double12 = 0.0;
            if (double3 * double3 + double10 * double10 == 0.0) {
                double9 = 0.0;
                double10 = double4;
                double11 = -double3;
                double12 = 0.0;
            }
        } else {
            double double13 = Math.sqrt((1.0 + double8) * 2.0);
            double double14 = 1.0 / double13;
            double double15 = double3 * double7 - double4 * double6;
            double double16 = double4 * double5 - double2 * double7;
            double double17 = double2 * double6 - double3 * double5;
            double9 = double15 * double14;
            double10 = double16 * double14;
            double11 = double17 * double14;
            double12 = double13 * 0.5;
            double double18 = Math.invsqrt(Math.fma(double9, double9, Math.fma(double10, double10, Math.fma(double11, double11, double12 * double12))));
            double9 *= double18;
            double10 *= double18;
            double11 *= double18;
            double12 *= double18;
        }

        return arg6.set(
            Math.fma(this.w, double9, Math.fma(this.x, double12, Math.fma(this.y, double11, -this.z * double10))),
            Math.fma(this.w, double10, Math.fma(-this.x, double11, Math.fma(this.y, double12, this.z * double9))),
            Math.fma(this.w, double11, Math.fma(this.x, double10, Math.fma(-this.y, double9, this.z * double12))),
            Math.fma(this.w, double12, Math.fma(-this.x, double9, Math.fma(-this.y, double10, -this.z * double11)))
        );
    }

    public Quaterniond rotationAxis(AxisAngle4f arg0) {
        return this.rotationAxis(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    public Quaterniond rotationAxis(double arg0, double arg1, double arg2, double arg3) {
        double double0 = arg0 / 2.0;
        double double1 = Math.sin(double0);
        double double2 = Math.invsqrt(arg1 * arg1 + arg2 * arg2 + arg3 * arg3);
        return this.set(arg1 * double2 * double1, arg2 * double2 * double1, arg3 * double2 * double1, Math.cosFromSin(double1, double0));
    }

    public Quaterniond rotationX(double arg0) {
        double double0 = Math.sin(arg0 * 0.5);
        double double1 = Math.cosFromSin(double0, arg0 * 0.5);
        return this.set(double0, 0.0, double1, 0.0);
    }

    public Quaterniond rotationY(double arg0) {
        double double0 = Math.sin(arg0 * 0.5);
        double double1 = Math.cosFromSin(double0, arg0 * 0.5);
        return this.set(0.0, double0, 0.0, double1);
    }

    public Quaterniond rotationZ(double arg0) {
        double double0 = Math.sin(arg0 * 0.5);
        double double1 = Math.cosFromSin(double0, arg0 * 0.5);
        return this.set(0.0, 0.0, double0, double1);
    }

    public Quaterniond rotateTo(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.rotateTo(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    @Override
    public Quaterniond rotateTo(Vector3dc arg0, Vector3dc arg1, Quaterniond arg2) {
        return this.rotateTo(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    public Quaterniond rotateTo(Vector3dc arg0, Vector3dc arg1) {
        return this.rotateTo(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), this);
    }

    public Quaterniond rotateX(double arg0) {
        return this.rotateX(arg0, this);
    }

    @Override
    public Quaterniond rotateX(double arg0, Quaterniond arg1) {
        double double0 = Math.sin(arg0 * 0.5);
        double double1 = Math.cosFromSin(double0, arg0 * 0.5);
        return arg1.set(
            this.w * double0 + this.x * double1, this.y * double1 + this.z * double0, this.z * double1 - this.y * double0, this.w * double1 - this.x * double0
        );
    }

    public Quaterniond rotateY(double arg0) {
        return this.rotateY(arg0, this);
    }

    @Override
    public Quaterniond rotateY(double arg0, Quaterniond arg1) {
        double double0 = Math.sin(arg0 * 0.5);
        double double1 = Math.cosFromSin(double0, arg0 * 0.5);
        return arg1.set(
            this.x * double1 - this.z * double0, this.w * double0 + this.y * double1, this.x * double0 + this.z * double1, this.w * double1 - this.y * double0
        );
    }

    public Quaterniond rotateZ(double arg0) {
        return this.rotateZ(arg0, this);
    }

    @Override
    public Quaterniond rotateZ(double arg0, Quaterniond arg1) {
        double double0 = Math.sin(arg0 * 0.5);
        double double1 = Math.cosFromSin(double0, arg0 * 0.5);
        return arg1.set(
            this.x * double1 + this.y * double0, this.y * double1 - this.x * double0, this.w * double0 + this.z * double1, this.w * double1 - this.z * double0
        );
    }

    public Quaterniond rotateLocalX(double arg0) {
        return this.rotateLocalX(arg0, this);
    }

    @Override
    public Quaterniond rotateLocalX(double arg0, Quaterniond arg1) {
        double double0 = arg0 * 0.5;
        double double1 = Math.sin(double0);
        double double2 = Math.cosFromSin(double1, double0);
        arg1.set(
            double2 * this.x + double1 * this.w, double2 * this.y - double1 * this.z, double2 * this.z + double1 * this.y, double2 * this.w - double1 * this.x
        );
        return arg1;
    }

    public Quaterniond rotateLocalY(double arg0) {
        return this.rotateLocalY(arg0, this);
    }

    @Override
    public Quaterniond rotateLocalY(double arg0, Quaterniond arg1) {
        double double0 = arg0 * 0.5;
        double double1 = Math.sin(double0);
        double double2 = Math.cosFromSin(double1, double0);
        arg1.set(
            double2 * this.x + double1 * this.z, double2 * this.y + double1 * this.w, double2 * this.z - double1 * this.x, double2 * this.w - double1 * this.y
        );
        return arg1;
    }

    public Quaterniond rotateLocalZ(double arg0) {
        return this.rotateLocalZ(arg0, this);
    }

    @Override
    public Quaterniond rotateLocalZ(double arg0, Quaterniond arg1) {
        double double0 = arg0 * 0.5;
        double double1 = Math.sin(double0);
        double double2 = Math.cosFromSin(double1, double0);
        arg1.set(
            double2 * this.x - double1 * this.y, double2 * this.y + double1 * this.x, double2 * this.z + double1 * this.w, double2 * this.w - double1 * this.z
        );
        return arg1;
    }

    public Quaterniond rotateXYZ(double arg0, double arg1, double arg2) {
        return this.rotateXYZ(arg0, arg1, arg2, this);
    }

    @Override
    public Quaterniond rotateXYZ(double arg0, double arg1, double arg2, Quaterniond arg3) {
        double double0 = Math.sin(arg0 * 0.5);
        double double1 = Math.cosFromSin(double0, arg0 * 0.5);
        double double2 = Math.sin(arg1 * 0.5);
        double double3 = Math.cosFromSin(double2, arg1 * 0.5);
        double double4 = Math.sin(arg2 * 0.5);
        double double5 = Math.cosFromSin(double4, arg2 * 0.5);
        double double6 = double3 * double5;
        double double7 = double2 * double4;
        double double8 = double2 * double5;
        double double9 = double3 * double4;
        double double10 = double1 * double6 - double0 * double7;
        double double11 = double0 * double6 + double1 * double7;
        double double12 = double1 * double8 - double0 * double9;
        double double13 = double1 * double9 + double0 * double8;
        return arg3.set(
            Math.fma(this.w, double11, Math.fma(this.x, double10, Math.fma(this.y, double13, -this.z * double12))),
            Math.fma(this.w, double12, Math.fma(-this.x, double13, Math.fma(this.y, double10, this.z * double11))),
            Math.fma(this.w, double13, Math.fma(this.x, double12, Math.fma(-this.y, double11, this.z * double10))),
            Math.fma(this.w, double10, Math.fma(-this.x, double11, Math.fma(-this.y, double12, -this.z * double13)))
        );
    }

    public Quaterniond rotateZYX(double arg0, double arg1, double arg2) {
        return this.rotateZYX(arg0, arg1, arg2, this);
    }

    @Override
    public Quaterniond rotateZYX(double arg0, double arg1, double arg2, Quaterniond arg3) {
        double double0 = Math.sin(arg2 * 0.5);
        double double1 = Math.cosFromSin(double0, arg2 * 0.5);
        double double2 = Math.sin(arg1 * 0.5);
        double double3 = Math.cosFromSin(double2, arg1 * 0.5);
        double double4 = Math.sin(arg0 * 0.5);
        double double5 = Math.cosFromSin(double4, arg0 * 0.5);
        double double6 = double3 * double5;
        double double7 = double2 * double4;
        double double8 = double2 * double5;
        double double9 = double3 * double4;
        double double10 = double1 * double6 + double0 * double7;
        double double11 = double0 * double6 - double1 * double7;
        double double12 = double1 * double8 + double0 * double9;
        double double13 = double1 * double9 - double0 * double8;
        return arg3.set(
            Math.fma(this.w, double11, Math.fma(this.x, double10, Math.fma(this.y, double13, -this.z * double12))),
            Math.fma(this.w, double12, Math.fma(-this.x, double13, Math.fma(this.y, double10, this.z * double11))),
            Math.fma(this.w, double13, Math.fma(this.x, double12, Math.fma(-this.y, double11, this.z * double10))),
            Math.fma(this.w, double10, Math.fma(-this.x, double11, Math.fma(-this.y, double12, -this.z * double13)))
        );
    }

    public Quaterniond rotateYXZ(double arg0, double arg1, double arg2) {
        return this.rotateYXZ(arg0, arg1, arg2, this);
    }

    @Override
    public Quaterniond rotateYXZ(double arg0, double arg1, double arg2, Quaterniond arg3) {
        double double0 = Math.sin(arg1 * 0.5);
        double double1 = Math.cosFromSin(double0, arg1 * 0.5);
        double double2 = Math.sin(arg0 * 0.5);
        double double3 = Math.cosFromSin(double2, arg0 * 0.5);
        double double4 = Math.sin(arg2 * 0.5);
        double double5 = Math.cosFromSin(double4, arg2 * 0.5);
        double double6 = double3 * double0;
        double double7 = double2 * double1;
        double double8 = double2 * double0;
        double double9 = double3 * double1;
        double double10 = double6 * double5 + double7 * double4;
        double double11 = double7 * double5 - double6 * double4;
        double double12 = double9 * double4 - double8 * double5;
        double double13 = double9 * double5 + double8 * double4;
        return arg3.set(
            Math.fma(this.w, double10, Math.fma(this.x, double13, Math.fma(this.y, double12, -this.z * double11))),
            Math.fma(this.w, double11, Math.fma(-this.x, double12, Math.fma(this.y, double13, this.z * double10))),
            Math.fma(this.w, double12, Math.fma(this.x, double11, Math.fma(-this.y, double10, this.z * double13))),
            Math.fma(this.w, double13, Math.fma(-this.x, double10, Math.fma(-this.y, double11, -this.z * double12)))
        );
    }

    @Override
    public Vector3d getEulerAnglesXYZ(Vector3d arg0) {
        arg0.x = Math.atan2(2.0 * (this.x * this.w - this.y * this.z), 1.0 - 2.0 * (this.x * this.x + this.y * this.y));
        arg0.y = Math.safeAsin(2.0 * (this.x * this.z + this.y * this.w));
        arg0.z = Math.atan2(2.0 * (this.z * this.w - this.x * this.y), 1.0 - 2.0 * (this.y * this.y + this.z * this.z));
        return arg0;
    }

    @Override
    public Quaterniond rotateAxis(double arg0, double arg1, double arg2, double arg3, Quaterniond arg4) {
        double double0 = arg0 / 2.0;
        double double1 = Math.sin(double0);
        double double2 = Math.invsqrt(Math.fma(arg1, arg1, Math.fma(arg2, arg2, arg3 * arg3)));
        double double3 = arg1 * double2 * double1;
        double double4 = arg2 * double2 * double1;
        double double5 = arg3 * double2 * double1;
        double double6 = Math.cosFromSin(double1, double0);
        return arg4.set(
            Math.fma(this.w, double3, Math.fma(this.x, double6, Math.fma(this.y, double5, -this.z * double4))),
            Math.fma(this.w, double4, Math.fma(-this.x, double5, Math.fma(this.y, double6, this.z * double3))),
            Math.fma(this.w, double5, Math.fma(this.x, double4, Math.fma(-this.y, double3, this.z * double6))),
            Math.fma(this.w, double6, Math.fma(-this.x, double3, Math.fma(-this.y, double4, -this.z * double5)))
        );
    }

    @Override
    public Quaterniond rotateAxis(double arg0, Vector3dc arg1, Quaterniond arg2) {
        return this.rotateAxis(arg0, arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    public Quaterniond rotateAxis(double arg0, Vector3dc arg1) {
        return this.rotateAxis(arg0, arg1.x(), arg1.y(), arg1.z(), this);
    }

    public Quaterniond rotateAxis(double arg0, double arg1, double arg2, double arg3) {
        return this.rotateAxis(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Vector3d positiveX(Vector3d arg0) {
        double double0 = 1.0 / this.lengthSquared();
        double double1 = -this.x * double0;
        double double2 = -this.y * double0;
        double double3 = -this.z * double0;
        double double4 = this.w * double0;
        double double5 = double2 + double2;
        double double6 = double3 + double3;
        arg0.x = -double2 * double5 - double3 * double6 + 1.0;
        arg0.y = double1 * double5 + double4 * double6;
        arg0.z = double1 * double6 - double4 * double5;
        return arg0;
    }

    @Override
    public Vector3d normalizedPositiveX(Vector3d arg0) {
        double double0 = this.y + this.y;
        double double1 = this.z + this.z;
        arg0.x = -this.y * double0 - this.z * double1 + 1.0;
        arg0.y = this.x * double0 - this.w * double1;
        arg0.z = this.x * double1 + this.w * double0;
        return arg0;
    }

    @Override
    public Vector3d positiveY(Vector3d arg0) {
        double double0 = 1.0 / this.lengthSquared();
        double double1 = -this.x * double0;
        double double2 = -this.y * double0;
        double double3 = -this.z * double0;
        double double4 = this.w * double0;
        double double5 = double1 + double1;
        double double6 = double2 + double2;
        double double7 = double3 + double3;
        arg0.x = double1 * double6 - double4 * double7;
        arg0.y = -double1 * double5 - double3 * double7 + 1.0;
        arg0.z = double2 * double7 + double4 * double5;
        return arg0;
    }

    @Override
    public Vector3d normalizedPositiveY(Vector3d arg0) {
        double double0 = this.x + this.x;
        double double1 = this.y + this.y;
        double double2 = this.z + this.z;
        arg0.x = this.x * double1 + this.w * double2;
        arg0.y = -this.x * double0 - this.z * double2 + 1.0;
        arg0.z = this.y * double2 - this.w * double0;
        return arg0;
    }

    @Override
    public Vector3d positiveZ(Vector3d arg0) {
        double double0 = 1.0 / this.lengthSquared();
        double double1 = -this.x * double0;
        double double2 = -this.y * double0;
        double double3 = -this.z * double0;
        double double4 = this.w * double0;
        double double5 = double1 + double1;
        double double6 = double2 + double2;
        double double7 = double3 + double3;
        arg0.x = double1 * double7 + double4 * double6;
        arg0.y = double2 * double7 - double4 * double5;
        arg0.z = -double1 * double5 - double2 * double6 + 1.0;
        return arg0;
    }

    @Override
    public Vector3d normalizedPositiveZ(Vector3d arg0) {
        double double0 = this.x + this.x;
        double double1 = this.y + this.y;
        double double2 = this.z + this.z;
        arg0.x = this.x * double2 - this.w * double1;
        arg0.y = this.y * double2 + this.w * double0;
        arg0.z = -this.x * double0 - this.y * double1 + 1.0;
        return arg0;
    }

    public Quaterniond conjugateBy(Quaterniondc arg0) {
        return this.conjugateBy(arg0, this);
    }

    @Override
    public Quaterniond conjugateBy(Quaterniondc arg0, Quaterniond arg1) {
        double double0 = 1.0 / arg0.lengthSquared();
        double double1 = -arg0.x() * double0;
        double double2 = -arg0.y() * double0;
        double double3 = -arg0.z() * double0;
        double double4 = arg0.w() * double0;
        double double5 = Math.fma(arg0.w(), this.x, Math.fma(arg0.x(), this.w, Math.fma(arg0.y(), this.z, -arg0.z() * this.y)));
        double double6 = Math.fma(arg0.w(), this.y, Math.fma(-arg0.x(), this.z, Math.fma(arg0.y(), this.w, arg0.z() * this.x)));
        double double7 = Math.fma(arg0.w(), this.z, Math.fma(arg0.x(), this.y, Math.fma(-arg0.y(), this.x, arg0.z() * this.w)));
        double double8 = Math.fma(arg0.w(), this.w, Math.fma(-arg0.x(), this.x, Math.fma(-arg0.y(), this.y, -arg0.z() * this.z)));
        return arg1.set(
            Math.fma(double8, double1, Math.fma(double5, double4, Math.fma(double6, double3, -double7 * double2))),
            Math.fma(double8, double2, Math.fma(-double5, double3, Math.fma(double6, double4, double7 * double1))),
            Math.fma(double8, double3, Math.fma(double5, double2, Math.fma(-double6, double1, double7 * double4))),
            Math.fma(double8, double4, Math.fma(-double5, double1, Math.fma(-double6, double2, -double7 * double3)))
        );
    }

    @Override
    public boolean isFinite() {
        return Math.isFinite(this.x) && Math.isFinite(this.y) && Math.isFinite(this.z) && Math.isFinite(this.w);
    }

    @Override
    public boolean equals(Quaterniondc arg0, double arg1) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (!(arg0 instanceof Quaterniondc)) {
            return false;
        } else if (!Runtime.equals(this.x, arg0.x(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.y, arg0.y(), arg1)) {
            return false;
        } else {
            return !Runtime.equals(this.z, arg0.z(), arg1) ? false : Runtime.equals(this.w, arg0.w(), arg1);
        }
    }

    @Override
    public boolean equals(double arg0, double arg1, double arg2, double arg3) {
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(arg0)) {
            return false;
        } else if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(arg1)) {
            return false;
        } else {
            return Double.doubleToLongBits(this.z) != Double.doubleToLongBits(arg2) ? false : Double.doubleToLongBits(this.w) == Double.doubleToLongBits(arg3);
        }
    }
}
