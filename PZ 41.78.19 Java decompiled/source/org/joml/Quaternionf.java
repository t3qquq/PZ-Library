// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.NumberFormat;

public class Quaternionf implements Externalizable, Quaternionfc {
    private static final long serialVersionUID = 1L;
    public float x;
    public float y;
    public float z;
    public float w;

    public Quaternionf() {
        this.w = 1.0F;
    }

    public Quaternionf(double arg0, double arg1, double arg2, double arg3) {
        this.x = (float)arg0;
        this.y = (float)arg1;
        this.z = (float)arg2;
        this.w = (float)arg3;
    }

    public Quaternionf(float arg0, float arg1, float arg2, float arg3) {
        this.x = arg0;
        this.y = arg1;
        this.z = arg2;
        this.w = arg3;
    }

    public Quaternionf(Quaternionfc arg0) {
        this.set(arg0);
    }

    public Quaternionf(Quaterniondc arg0) {
        this.set(arg0);
    }

    public Quaternionf(AxisAngle4f arg0) {
        float float0 = Math.sin(arg0.angle * 0.5F);
        float float1 = Math.cosFromSin(float0, arg0.angle * 0.5F);
        this.x = arg0.x * float0;
        this.y = arg0.y * float0;
        this.z = arg0.z * float0;
        this.w = float1;
    }

    public Quaternionf(AxisAngle4d arg0) {
        double double0 = Math.sin(arg0.angle * 0.5);
        double double1 = Math.cosFromSin(double0, arg0.angle * 0.5);
        this.x = (float)(arg0.x * double0);
        this.y = (float)(arg0.y * double0);
        this.z = (float)(arg0.z * double0);
        this.w = (float)double1;
    }

    @Override
    public float x() {
        return this.x;
    }

    @Override
    public float y() {
        return this.y;
    }

    @Override
    public float z() {
        return this.z;
    }

    @Override
    public float w() {
        return this.w;
    }

    public Quaternionf normalize() {
        return this.normalize(this);
    }

    @Override
    public Quaternionf normalize(Quaternionf arg0) {
        float float0 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w))));
        arg0.x = this.x * float0;
        arg0.y = this.y * float0;
        arg0.z = this.z * float0;
        arg0.w = this.w * float0;
        return arg0;
    }

    public Quaternionf add(float arg0, float arg1, float arg2, float arg3) {
        return this.add(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Quaternionf add(float arg0, float arg1, float arg2, float arg3, Quaternionf arg4) {
        arg4.x = this.x + arg0;
        arg4.y = this.y + arg1;
        arg4.z = this.z + arg2;
        arg4.w = this.w + arg3;
        return arg4;
    }

    public Quaternionf add(Quaternionfc arg0) {
        return this.add(arg0, this);
    }

    @Override
    public Quaternionf add(Quaternionfc arg0, Quaternionf arg1) {
        arg1.x = this.x + arg0.x();
        arg1.y = this.y + arg0.y();
        arg1.z = this.z + arg0.z();
        arg1.w = this.w + arg0.w();
        return arg1;
    }

    public float dot(Quaternionf arg0) {
        return this.x * arg0.x + this.y * arg0.y + this.z * arg0.z + this.w * arg0.w;
    }

    @Override
    public float angle() {
        return (float)(2.0 * Math.safeAcos(this.w));
    }

    @Override
    public Matrix3f get(Matrix3f arg0) {
        return arg0.set(this);
    }

    @Override
    public Matrix3d get(Matrix3d arg0) {
        return arg0.set(this);
    }

    @Override
    public Matrix4f get(Matrix4f arg0) {
        return arg0.set(this);
    }

    @Override
    public Matrix4d get(Matrix4d arg0) {
        return arg0.set(this);
    }

    @Override
    public Matrix4x3f get(Matrix4x3f arg0) {
        return arg0.set(this);
    }

    @Override
    public Matrix4x3d get(Matrix4x3d arg0) {
        return arg0.set(this);
    }

    @Override
    public AxisAngle4f get(AxisAngle4f arg0) {
        float float0 = this.x;
        float float1 = this.y;
        float float2 = this.z;
        float float3 = this.w;
        if (float3 > 1.0F) {
            float float4 = Math.invsqrt(Math.fma(float0, float0, Math.fma(float1, float1, Math.fma(float2, float2, float3 * float3))));
            float0 *= float4;
            float1 *= float4;
            float2 *= float4;
            float3 *= float4;
        }

        arg0.angle = 2.0F * Math.acos(float3);
        float float5 = Math.sqrt(1.0F - float3 * float3);
        if (float5 < 0.001F) {
            arg0.x = float0;
            arg0.y = float1;
            arg0.z = float2;
        } else {
            float5 = 1.0F / float5;
            arg0.x = float0 * float5;
            arg0.y = float1 * float5;
            arg0.z = float2 * float5;
        }

        return arg0;
    }

    @Override
    public AxisAngle4d get(AxisAngle4d arg0) {
        float float0 = this.x;
        float float1 = this.y;
        float float2 = this.z;
        float float3 = this.w;
        if (float3 > 1.0F) {
            float float4 = Math.invsqrt(Math.fma(float0, float0, Math.fma(float1, float1, Math.fma(float2, float2, float3 * float3))));
            float0 *= float4;
            float1 *= float4;
            float2 *= float4;
            float3 *= float4;
        }

        arg0.angle = 2.0F * Math.acos(float3);
        float float5 = Math.sqrt(1.0F - float3 * float3);
        if (float5 < 0.001F) {
            arg0.x = float0;
            arg0.y = float1;
            arg0.z = float2;
        } else {
            float5 = 1.0F / float5;
            arg0.x = float0 * float5;
            arg0.y = float1 * float5;
            arg0.z = float2 * float5;
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

    @Override
    public ByteBuffer getAsMatrix3f(ByteBuffer arg0) {
        MemUtil.INSTANCE.putMatrix3f(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public FloatBuffer getAsMatrix3f(FloatBuffer arg0) {
        MemUtil.INSTANCE.putMatrix3f(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public ByteBuffer getAsMatrix4f(ByteBuffer arg0) {
        MemUtil.INSTANCE.putMatrix4f(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public FloatBuffer getAsMatrix4f(FloatBuffer arg0) {
        MemUtil.INSTANCE.putMatrix4f(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public ByteBuffer getAsMatrix4x3f(ByteBuffer arg0) {
        MemUtil.INSTANCE.putMatrix4x3f(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public FloatBuffer getAsMatrix4x3f(FloatBuffer arg0) {
        MemUtil.INSTANCE.putMatrix4x3f(this, arg0.position(), arg0);
        return arg0;
    }

    public Quaternionf set(float arg0, float arg1, float arg2, float arg3) {
        this.x = arg0;
        this.y = arg1;
        this.z = arg2;
        this.w = arg3;
        return this;
    }

    public Quaternionf set(Quaternionfc arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg0.w();
        return this;
    }

    public Quaternionf set(Quaterniondc arg0) {
        this.x = (float)arg0.x();
        this.y = (float)arg0.y();
        this.z = (float)arg0.z();
        this.w = (float)arg0.w();
        return this;
    }

    public Quaternionf set(AxisAngle4f arg0) {
        return this.setAngleAxis(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    public Quaternionf set(AxisAngle4d arg0) {
        return this.setAngleAxis(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    public Quaternionf setAngleAxis(float arg0, float arg1, float arg2, float arg3) {
        float float0 = Math.sin(arg0 * 0.5F);
        this.x = arg1 * float0;
        this.y = arg2 * float0;
        this.z = arg3 * float0;
        this.w = Math.cosFromSin(float0, arg0 * 0.5F);
        return this;
    }

    public Quaternionf setAngleAxis(double arg0, double arg1, double arg2, double arg3) {
        double double0 = Math.sin(arg0 * 0.5);
        this.x = (float)(arg1 * double0);
        this.y = (float)(arg2 * double0);
        this.z = (float)(arg3 * double0);
        this.w = (float)Math.cosFromSin(double0, arg0 * 0.5);
        return this;
    }

    public Quaternionf rotationAxis(AxisAngle4f arg0) {
        return this.rotationAxis(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    public Quaternionf rotationAxis(float arg0, float arg1, float arg2, float arg3) {
        float float0 = arg0 / 2.0F;
        float float1 = Math.sin(float0);
        float float2 = Math.invsqrt(arg1 * arg1 + arg2 * arg2 + arg3 * arg3);
        return this.set(arg1 * float2 * float1, arg2 * float2 * float1, arg3 * float2 * float1, Math.cosFromSin(float1, float0));
    }

    public Quaternionf rotationAxis(float arg0, Vector3fc arg1) {
        return this.rotationAxis(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    public Quaternionf rotationX(float arg0) {
        float float0 = Math.sin(arg0 * 0.5F);
        float float1 = Math.cosFromSin(float0, arg0 * 0.5F);
        return this.set(float0, 0.0F, 0.0F, float1);
    }

    public Quaternionf rotationY(float arg0) {
        float float0 = Math.sin(arg0 * 0.5F);
        float float1 = Math.cosFromSin(float0, arg0 * 0.5F);
        return this.set(0.0F, float0, 0.0F, float1);
    }

    public Quaternionf rotationZ(float arg0) {
        float float0 = Math.sin(arg0 * 0.5F);
        float float1 = Math.cosFromSin(float0, arg0 * 0.5F);
        return this.set(0.0F, 0.0F, float0, float1);
    }

    private void setFromUnnormalized(
        float float3, float float2, float float1, float float7, float float6, float float5, float float11, float float10, float float9
    ) {
        float float0 = Math.invsqrt(float3 * float3 + float2 * float2 + float1 * float1);
        float float4 = Math.invsqrt(float7 * float7 + float6 * float6 + float5 * float5);
        float float8 = Math.invsqrt(float11 * float11 + float10 * float10 + float9 * float9);
        float float12 = float3 * float0;
        float float13 = float2 * float0;
        float float14 = float1 * float0;
        float float15 = float7 * float4;
        float float16 = float6 * float4;
        float float17 = float5 * float4;
        float float18 = float11 * float8;
        float float19 = float10 * float8;
        float float20 = float9 * float8;
        this.setFromNormalized(float12, float13, float14, float15, float16, float17, float18, float19, float20);
    }

    private void setFromNormalized(
        float float2, float float9, float float8, float float10, float float3, float float5, float float7, float float6, float float1
    ) {
        float float0 = float2 + float3 + float1;
        if (float0 >= 0.0F) {
            float float4 = Math.sqrt(float0 + 1.0F);
            this.w = float4 * 0.5F;
            float4 = 0.5F / float4;
            this.x = (float5 - float6) * float4;
            this.y = (float7 - float8) * float4;
            this.z = (float9 - float10) * float4;
        } else if (float2 >= float3 && float2 >= float1) {
            float float11 = Math.sqrt(float2 - (float3 + float1) + 1.0F);
            this.x = float11 * 0.5F;
            float11 = 0.5F / float11;
            this.y = (float10 + float9) * float11;
            this.z = (float8 + float7) * float11;
            this.w = (float5 - float6) * float11;
        } else if (float3 > float1) {
            float float12 = Math.sqrt(float3 - (float1 + float2) + 1.0F);
            this.y = float12 * 0.5F;
            float12 = 0.5F / float12;
            this.z = (float6 + float5) * float12;
            this.x = (float10 + float9) * float12;
            this.w = (float7 - float8) * float12;
        } else {
            float float13 = Math.sqrt(float1 - (float2 + float3) + 1.0F);
            this.z = float13 * 0.5F;
            float13 = 0.5F / float13;
            this.x = (float8 + float7) * float13;
            this.y = (float6 + float5) * float13;
            this.w = (float9 - float10) * float13;
        }
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
            this.w = (float)(double4 * 0.5);
            double4 = 0.5 / double4;
            this.x = (float)((double5 - double6) * double4);
            this.y = (float)((double7 - double8) * double4);
            this.z = (float)((double9 - double10) * double4);
        } else if (double2 >= double3 && double2 >= double1) {
            double double11 = Math.sqrt(double2 - (double3 + double1) + 1.0);
            this.x = (float)(double11 * 0.5);
            double11 = 0.5 / double11;
            this.y = (float)((double10 + double9) * double11);
            this.z = (float)((double8 + double7) * double11);
            this.w = (float)((double5 - double6) * double11);
        } else if (double3 > double1) {
            double double12 = Math.sqrt(double3 - (double1 + double2) + 1.0);
            this.y = (float)(double12 * 0.5);
            double12 = 0.5 / double12;
            this.z = (float)((double6 + double5) * double12);
            this.x = (float)((double10 + double9) * double12);
            this.w = (float)((double7 - double8) * double12);
        } else {
            double double13 = Math.sqrt(double1 - (double2 + double3) + 1.0);
            this.z = (float)(double13 * 0.5);
            double13 = 0.5 / double13;
            this.x = (float)((double8 + double7) * double13);
            this.y = (float)((double6 + double5) * double13);
            this.w = (float)((double9 - double10) * double13);
        }
    }

    public Quaternionf setFromUnnormalized(Matrix4fc arg0) {
        this.setFromUnnormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaternionf setFromUnnormalized(Matrix4x3fc arg0) {
        this.setFromUnnormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaternionf setFromUnnormalized(Matrix4x3dc arg0) {
        this.setFromUnnormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaternionf setFromNormalized(Matrix4fc arg0) {
        this.setFromNormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaternionf setFromNormalized(Matrix4x3fc arg0) {
        this.setFromNormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaternionf setFromNormalized(Matrix4x3dc arg0) {
        this.setFromNormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaternionf setFromUnnormalized(Matrix4dc arg0) {
        this.setFromUnnormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaternionf setFromNormalized(Matrix4dc arg0) {
        this.setFromNormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaternionf setFromUnnormalized(Matrix3fc arg0) {
        this.setFromUnnormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaternionf setFromNormalized(Matrix3fc arg0) {
        this.setFromNormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaternionf setFromUnnormalized(Matrix3dc arg0) {
        this.setFromUnnormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaternionf setFromNormalized(Matrix3dc arg0) {
        this.setFromNormalized(arg0.m00(), arg0.m01(), arg0.m02(), arg0.m10(), arg0.m11(), arg0.m12(), arg0.m20(), arg0.m21(), arg0.m22());
        return this;
    }

    public Quaternionf fromAxisAngleRad(Vector3fc arg0, float arg1) {
        return this.fromAxisAngleRad(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    public Quaternionf fromAxisAngleRad(float arg0, float arg1, float arg2, float arg3) {
        float float0 = arg3 / 2.0F;
        float float1 = Math.sin(float0);
        float float2 = Math.sqrt(arg0 * arg0 + arg1 * arg1 + arg2 * arg2);
        this.x = arg0 / float2 * float1;
        this.y = arg1 / float2 * float1;
        this.z = arg2 / float2 * float1;
        this.w = Math.cosFromSin(float1, float0);
        return this;
    }

    public Quaternionf fromAxisAngleDeg(Vector3fc arg0, float arg1) {
        return this.fromAxisAngleRad(arg0.x(), arg0.y(), arg0.z(), Math.toRadians(arg1));
    }

    public Quaternionf fromAxisAngleDeg(float arg0, float arg1, float arg2, float arg3) {
        return this.fromAxisAngleRad(arg0, arg1, arg2, Math.toRadians(arg3));
    }

    public Quaternionf mul(Quaternionfc arg0) {
        return this.mul(arg0, this);
    }

    @Override
    public Quaternionf mul(Quaternionfc arg0, Quaternionf arg1) {
        return arg1.set(
            Math.fma(this.w, arg0.x(), Math.fma(this.x, arg0.w(), Math.fma(this.y, arg0.z(), -this.z * arg0.y()))),
            Math.fma(this.w, arg0.y(), Math.fma(-this.x, arg0.z(), Math.fma(this.y, arg0.w(), this.z * arg0.x()))),
            Math.fma(this.w, arg0.z(), Math.fma(this.x, arg0.y(), Math.fma(-this.y, arg0.x(), this.z * arg0.w()))),
            Math.fma(this.w, arg0.w(), Math.fma(-this.x, arg0.x(), Math.fma(-this.y, arg0.y(), -this.z * arg0.z())))
        );
    }

    public Quaternionf mul(float arg0, float arg1, float arg2, float arg3) {
        return this.mul(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Quaternionf mul(float arg0, float arg1, float arg2, float arg3, Quaternionf arg4) {
        return arg4.set(
            Math.fma(this.w, arg0, Math.fma(this.x, arg3, Math.fma(this.y, arg2, -this.z * arg1))),
            Math.fma(this.w, arg1, Math.fma(-this.x, arg2, Math.fma(this.y, arg3, this.z * arg0))),
            Math.fma(this.w, arg2, Math.fma(this.x, arg1, Math.fma(-this.y, arg0, this.z * arg3))),
            Math.fma(this.w, arg3, Math.fma(-this.x, arg0, Math.fma(-this.y, arg1, -this.z * arg2)))
        );
    }

    public Quaternionf premul(Quaternionfc arg0) {
        return this.premul(arg0, this);
    }

    @Override
    public Quaternionf premul(Quaternionfc arg0, Quaternionf arg1) {
        return arg1.set(
            Math.fma(arg0.w(), this.x, Math.fma(arg0.x(), this.w, Math.fma(arg0.y(), this.z, -arg0.z() * this.y))),
            Math.fma(arg0.w(), this.y, Math.fma(-arg0.x(), this.z, Math.fma(arg0.y(), this.w, arg0.z() * this.x))),
            Math.fma(arg0.w(), this.z, Math.fma(arg0.x(), this.y, Math.fma(-arg0.y(), this.x, arg0.z() * this.w))),
            Math.fma(arg0.w(), this.w, Math.fma(-arg0.x(), this.x, Math.fma(-arg0.y(), this.y, -arg0.z() * this.z)))
        );
    }

    public Quaternionf premul(float arg0, float arg1, float arg2, float arg3) {
        return this.premul(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Quaternionf premul(float arg0, float arg1, float arg2, float arg3, Quaternionf arg4) {
        return arg4.set(
            Math.fma(arg3, this.x, Math.fma(arg0, this.w, Math.fma(arg1, this.z, -arg2 * this.y))),
            Math.fma(arg3, this.y, Math.fma(-arg0, this.z, Math.fma(arg1, this.w, arg2 * this.x))),
            Math.fma(arg3, this.z, Math.fma(arg0, this.y, Math.fma(-arg1, this.x, arg2 * this.w))),
            Math.fma(arg3, this.w, Math.fma(-arg0, this.x, Math.fma(-arg1, this.y, -arg2 * this.z)))
        );
    }

    @Override
    public Vector3f transform(Vector3f arg0) {
        return this.transform(arg0.x, arg0.y, arg0.z, arg0);
    }

    @Override
    public Vector3f transformInverse(Vector3f arg0) {
        return this.transformInverse(arg0.x, arg0.y, arg0.z, arg0);
    }

    @Override
    public Vector3f transformPositiveX(Vector3f arg0) {
        float float0 = this.w * this.w;
        float float1 = this.x * this.x;
        float float2 = this.y * this.y;
        float float3 = this.z * this.z;
        float float4 = this.z * this.w;
        float float5 = this.x * this.y;
        float float6 = this.x * this.z;
        float float7 = this.y * this.w;
        arg0.x = float0 + float1 - float3 - float2;
        arg0.y = float5 + float4 + float4 + float5;
        arg0.z = float6 - float7 + float6 - float7;
        return arg0;
    }

    @Override
    public Vector4f transformPositiveX(Vector4f arg0) {
        float float0 = this.w * this.w;
        float float1 = this.x * this.x;
        float float2 = this.y * this.y;
        float float3 = this.z * this.z;
        float float4 = this.z * this.w;
        float float5 = this.x * this.y;
        float float6 = this.x * this.z;
        float float7 = this.y * this.w;
        arg0.x = float0 + float1 - float3 - float2;
        arg0.y = float5 + float4 + float4 + float5;
        arg0.z = float6 - float7 + float6 - float7;
        return arg0;
    }

    @Override
    public Vector3f transformUnitPositiveX(Vector3f arg0) {
        float float0 = this.x * this.y;
        float float1 = this.x * this.z;
        float float2 = this.y * this.y;
        float float3 = this.y * this.w;
        float float4 = this.z * this.z;
        float float5 = this.z * this.w;
        arg0.x = 1.0F - float2 - float4 - float2 - float4;
        arg0.y = float0 + float5 + float0 + float5;
        arg0.z = float1 - float3 + float1 - float3;
        return arg0;
    }

    @Override
    public Vector4f transformUnitPositiveX(Vector4f arg0) {
        float float0 = this.y * this.y;
        float float1 = this.z * this.z;
        float float2 = this.x * this.y;
        float float3 = this.x * this.z;
        float float4 = this.y * this.w;
        float float5 = this.z * this.w;
        arg0.x = 1.0F - float0 - float0 - float1 - float1;
        arg0.y = float2 + float5 + float2 + float5;
        arg0.z = float3 - float4 + float3 - float4;
        return arg0;
    }

    @Override
    public Vector3f transformPositiveY(Vector3f arg0) {
        float float0 = this.w * this.w;
        float float1 = this.x * this.x;
        float float2 = this.y * this.y;
        float float3 = this.z * this.z;
        float float4 = this.z * this.w;
        float float5 = this.x * this.y;
        float float6 = this.y * this.z;
        float float7 = this.x * this.w;
        arg0.x = -float4 + float5 - float4 + float5;
        arg0.y = float2 - float3 + float0 - float1;
        arg0.z = float6 + float6 + float7 + float7;
        return arg0;
    }

    @Override
    public Vector4f transformPositiveY(Vector4f arg0) {
        float float0 = this.w * this.w;
        float float1 = this.x * this.x;
        float float2 = this.y * this.y;
        float float3 = this.z * this.z;
        float float4 = this.z * this.w;
        float float5 = this.x * this.y;
        float float6 = this.y * this.z;
        float float7 = this.x * this.w;
        arg0.x = -float4 + float5 - float4 + float5;
        arg0.y = float2 - float3 + float0 - float1;
        arg0.z = float6 + float6 + float7 + float7;
        return arg0;
    }

    @Override
    public Vector4f transformUnitPositiveY(Vector4f arg0) {
        float float0 = this.x * this.x;
        float float1 = this.z * this.z;
        float float2 = this.x * this.y;
        float float3 = this.y * this.z;
        float float4 = this.x * this.w;
        float float5 = this.z * this.w;
        arg0.x = float2 - float5 + float2 - float5;
        arg0.y = 1.0F - float0 - float0 - float1 - float1;
        arg0.z = float3 + float3 + float4 + float4;
        return arg0;
    }

    @Override
    public Vector3f transformUnitPositiveY(Vector3f arg0) {
        float float0 = this.x * this.x;
        float float1 = this.z * this.z;
        float float2 = this.x * this.y;
        float float3 = this.y * this.z;
        float float4 = this.x * this.w;
        float float5 = this.z * this.w;
        arg0.x = float2 - float5 + float2 - float5;
        arg0.y = 1.0F - float0 - float0 - float1 - float1;
        arg0.z = float3 + float3 + float4 + float4;
        return arg0;
    }

    @Override
    public Vector3f transformPositiveZ(Vector3f arg0) {
        float float0 = this.w * this.w;
        float float1 = this.x * this.x;
        float float2 = this.y * this.y;
        float float3 = this.z * this.z;
        float float4 = this.x * this.z;
        float float5 = this.y * this.w;
        float float6 = this.y * this.z;
        float float7 = this.x * this.w;
        arg0.x = float5 + float4 + float4 + float5;
        arg0.y = float6 + float6 - float7 - float7;
        arg0.z = float3 - float2 - float1 + float0;
        return arg0;
    }

    @Override
    public Vector4f transformPositiveZ(Vector4f arg0) {
        float float0 = this.w * this.w;
        float float1 = this.x * this.x;
        float float2 = this.y * this.y;
        float float3 = this.z * this.z;
        float float4 = this.x * this.z;
        float float5 = this.y * this.w;
        float float6 = this.y * this.z;
        float float7 = this.x * this.w;
        arg0.x = float5 + float4 + float4 + float5;
        arg0.y = float6 + float6 - float7 - float7;
        arg0.z = float3 - float2 - float1 + float0;
        return arg0;
    }

    @Override
    public Vector4f transformUnitPositiveZ(Vector4f arg0) {
        float float0 = this.x * this.x;
        float float1 = this.y * this.y;
        float float2 = this.x * this.z;
        float float3 = this.y * this.z;
        float float4 = this.x * this.w;
        float float5 = this.y * this.w;
        arg0.x = float2 + float5 + float2 + float5;
        arg0.y = float3 + float3 - float4 - float4;
        arg0.z = 1.0F - float0 - float0 - float1 - float1;
        return arg0;
    }

    @Override
    public Vector3f transformUnitPositiveZ(Vector3f arg0) {
        float float0 = this.x * this.x;
        float float1 = this.y * this.y;
        float float2 = this.x * this.z;
        float float3 = this.y * this.z;
        float float4 = this.x * this.w;
        float float5 = this.y * this.w;
        arg0.x = float2 + float5 + float2 + float5;
        arg0.y = float3 + float3 - float4 - float4;
        arg0.z = 1.0F - float0 - float0 - float1 - float1;
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
        return this.transform(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Vector3f transformInverse(Vector3fc arg0, Vector3f arg1) {
        return this.transformInverse(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Vector3f transform(float arg0, float arg1, float arg2, Vector3f arg3) {
        float float0 = this.x * this.x;
        float float1 = this.y * this.y;
        float float2 = this.z * this.z;
        float float3 = this.w * this.w;
        float float4 = this.x * this.y;
        float float5 = this.x * this.z;
        float float6 = this.y * this.z;
        float float7 = this.x * this.w;
        float float8 = this.z * this.w;
        float float9 = this.y * this.w;
        float float10 = 1.0F / (float0 + float1 + float2 + float3);
        return arg3.set(
            Math.fma(
                (float0 - float1 - float2 + float3) * float10,
                arg0,
                Math.fma(2.0F * (float4 - float8) * float10, arg1, 2.0F * (float5 + float9) * float10 * arg2)
            ),
            Math.fma(
                2.0F * (float4 + float8) * float10,
                arg0,
                Math.fma((float1 - float0 - float2 + float3) * float10, arg1, 2.0F * (float6 - float7) * float10 * arg2)
            ),
            Math.fma(
                2.0F * (float5 - float9) * float10,
                arg0,
                Math.fma(2.0F * (float6 + float7) * float10, arg1, (float2 - float0 - float1 + float3) * float10 * arg2)
            )
        );
    }

    @Override
    public Vector3f transformInverse(float arg0, float arg1, float arg2, Vector3f arg3) {
        float float0 = 1.0F / Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
        float float1 = this.x * float0;
        float float2 = this.y * float0;
        float float3 = this.z * float0;
        float float4 = this.w * float0;
        float float5 = float1 * float1;
        float float6 = float2 * float2;
        float float7 = float3 * float3;
        float float8 = float4 * float4;
        float float9 = float1 * float2;
        float float10 = float1 * float3;
        float float11 = float2 * float3;
        float float12 = float1 * float4;
        float float13 = float3 * float4;
        float float14 = float2 * float4;
        float float15 = 1.0F / (float5 + float6 + float7 + float8);
        return arg3.set(
            Math.fma(
                (float5 - float6 - float7 + float8) * float15,
                arg0,
                Math.fma(2.0F * (float9 + float13) * float15, arg1, 2.0F * (float10 - float14) * float15 * arg2)
            ),
            Math.fma(
                2.0F * (float9 - float13) * float15,
                arg0,
                Math.fma((float6 - float5 - float7 + float8) * float15, arg1, 2.0F * (float11 + float12) * float15 * arg2)
            ),
            Math.fma(
                2.0F * (float10 + float14) * float15,
                arg0,
                Math.fma(2.0F * (float11 - float12) * float15, arg1, (float7 - float5 - float6 + float8) * float15 * arg2)
            )
        );
    }

    @Override
    public Vector3f transformUnit(Vector3f arg0) {
        return this.transformUnit(arg0.x, arg0.y, arg0.z, arg0);
    }

    @Override
    public Vector3f transformInverseUnit(Vector3f arg0) {
        return this.transformInverseUnit(arg0.x, arg0.y, arg0.z, arg0);
    }

    @Override
    public Vector3f transformUnit(Vector3fc arg0, Vector3f arg1) {
        return this.transformUnit(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Vector3f transformInverseUnit(Vector3fc arg0, Vector3f arg1) {
        return this.transformInverseUnit(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Vector3f transformUnit(float arg0, float arg1, float arg2, Vector3f arg3) {
        float float0 = this.x * this.x;
        float float1 = this.x * this.y;
        float float2 = this.x * this.z;
        float float3 = this.x * this.w;
        float float4 = this.y * this.y;
        float float5 = this.y * this.z;
        float float6 = this.y * this.w;
        float float7 = this.z * this.z;
        float float8 = this.z * this.w;
        return arg3.set(
            Math.fma(Math.fma(-2.0F, float4 + float7, 1.0F), arg0, Math.fma(2.0F * (float1 - float8), arg1, 2.0F * (float2 + float6) * arg2)),
            Math.fma(2.0F * (float1 + float8), arg0, Math.fma(Math.fma(-2.0F, float0 + float7, 1.0F), arg1, 2.0F * (float5 - float3) * arg2)),
            Math.fma(2.0F * (float2 - float6), arg0, Math.fma(2.0F * (float5 + float3), arg1, Math.fma(-2.0F, float0 + float4, 1.0F) * arg2))
        );
    }

    @Override
    public Vector3f transformInverseUnit(float arg0, float arg1, float arg2, Vector3f arg3) {
        float float0 = this.x * this.x;
        float float1 = this.x * this.y;
        float float2 = this.x * this.z;
        float float3 = this.x * this.w;
        float float4 = this.y * this.y;
        float float5 = this.y * this.z;
        float float6 = this.y * this.w;
        float float7 = this.z * this.z;
        float float8 = this.z * this.w;
        return arg3.set(
            Math.fma(Math.fma(-2.0F, float4 + float7, 1.0F), arg0, Math.fma(2.0F * (float1 + float8), arg1, 2.0F * (float2 - float6) * arg2)),
            Math.fma(2.0F * (float1 - float8), arg0, Math.fma(Math.fma(-2.0F, float0 + float7, 1.0F), arg1, 2.0F * (float5 + float3) * arg2)),
            Math.fma(2.0F * (float2 + float6), arg0, Math.fma(2.0F * (float5 - float3), arg1, Math.fma(-2.0F, float0 + float4, 1.0F) * arg2))
        );
    }

    @Override
    public Vector4f transform(Vector4fc arg0, Vector4f arg1) {
        return this.transform(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Vector4f transformInverse(Vector4fc arg0, Vector4f arg1) {
        return this.transformInverse(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Vector4f transform(float arg0, float arg1, float arg2, Vector4f arg3) {
        float float0 = this.x * this.x;
        float float1 = this.y * this.y;
        float float2 = this.z * this.z;
        float float3 = this.w * this.w;
        float float4 = this.x * this.y;
        float float5 = this.x * this.z;
        float float6 = this.y * this.z;
        float float7 = this.x * this.w;
        float float8 = this.z * this.w;
        float float9 = this.y * this.w;
        float float10 = 1.0F / (float0 + float1 + float2 + float3);
        return arg3.set(
            Math.fma(
                (float0 - float1 - float2 + float3) * float10,
                arg0,
                Math.fma(2.0F * (float4 - float8) * float10, arg1, 2.0F * (float5 + float9) * float10 * arg2)
            ),
            Math.fma(
                2.0F * (float4 + float8) * float10,
                arg0,
                Math.fma((float1 - float0 - float2 + float3) * float10, arg1, 2.0F * (float6 - float7) * float10 * arg2)
            ),
            Math.fma(
                2.0F * (float5 - float9) * float10,
                arg0,
                Math.fma(2.0F * (float6 + float7) * float10, arg1, (float2 - float0 - float1 + float3) * float10 * arg2)
            )
        );
    }

    @Override
    public Vector4f transformInverse(float arg0, float arg1, float arg2, Vector4f arg3) {
        float float0 = 1.0F / Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
        float float1 = this.x * float0;
        float float2 = this.y * float0;
        float float3 = this.z * float0;
        float float4 = this.w * float0;
        float float5 = float1 * float1;
        float float6 = float2 * float2;
        float float7 = float3 * float3;
        float float8 = float4 * float4;
        float float9 = float1 * float2;
        float float10 = float1 * float3;
        float float11 = float2 * float3;
        float float12 = float1 * float4;
        float float13 = float3 * float4;
        float float14 = float2 * float4;
        float float15 = 1.0F / (float5 + float6 + float7 + float8);
        return arg3.set(
            Math.fma(
                (float5 - float6 - float7 + float8) * float15,
                arg0,
                Math.fma(2.0F * (float9 + float13) * float15, arg1, 2.0F * (float10 - float14) * float15 * arg2)
            ),
            Math.fma(
                2.0F * (float9 - float13) * float15,
                arg0,
                Math.fma((float6 - float5 - float7 + float8) * float15, arg1, 2.0F * (float11 + float12) * float15 * arg2)
            ),
            Math.fma(
                2.0F * (float10 + float14) * float15,
                arg0,
                Math.fma(2.0F * (float11 - float12) * float15, arg1, (float7 - float5 - float6 + float8) * float15 * arg2)
            )
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
    public Vector4f transformUnit(Vector4f arg0) {
        return this.transformUnit(arg0.x, arg0.y, arg0.z, arg0);
    }

    @Override
    public Vector4f transformInverseUnit(Vector4f arg0) {
        return this.transformInverseUnit(arg0.x, arg0.y, arg0.z, arg0);
    }

    @Override
    public Vector4f transformUnit(Vector4fc arg0, Vector4f arg1) {
        return this.transformUnit(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Vector4f transformInverseUnit(Vector4fc arg0, Vector4f arg1) {
        return this.transformInverseUnit(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Vector4f transformUnit(float arg0, float arg1, float arg2, Vector4f arg3) {
        float float0 = this.x * this.x;
        float float1 = this.x * this.y;
        float float2 = this.x * this.z;
        float float3 = this.x * this.w;
        float float4 = this.y * this.y;
        float float5 = this.y * this.z;
        float float6 = this.y * this.w;
        float float7 = this.z * this.z;
        float float8 = this.z * this.w;
        return arg3.set(
            Math.fma(Math.fma(-2.0F, float4 + float7, 1.0F), arg0, Math.fma(2.0F * (float1 - float8), arg1, 2.0F * (float2 + float6) * arg2)),
            Math.fma(2.0F * (float1 + float8), arg0, Math.fma(Math.fma(-2.0F, float0 + float7, 1.0F), arg1, 2.0F * (float5 - float3) * arg2)),
            Math.fma(2.0F * (float2 - float6), arg0, Math.fma(2.0F * (float5 + float3), arg1, Math.fma(-2.0F, float0 + float4, 1.0F) * arg2))
        );
    }

    @Override
    public Vector4f transformInverseUnit(float arg0, float arg1, float arg2, Vector4f arg3) {
        float float0 = this.x * this.x;
        float float1 = this.x * this.y;
        float float2 = this.x * this.z;
        float float3 = this.x * this.w;
        float float4 = this.y * this.y;
        float float5 = this.y * this.z;
        float float6 = this.y * this.w;
        float float7 = this.z * this.z;
        float float8 = this.z * this.w;
        return arg3.set(
            Math.fma(Math.fma(-2.0F, float4 + float7, 1.0F), arg0, Math.fma(2.0F * (float1 + float8), arg1, 2.0F * (float2 - float6) * arg2)),
            Math.fma(2.0F * (float1 - float8), arg0, Math.fma(Math.fma(-2.0F, float0 + float7, 1.0F), arg1, 2.0F * (float5 + float3) * arg2)),
            Math.fma(2.0F * (float2 + float6), arg0, Math.fma(2.0F * (float5 - float3), arg1, Math.fma(-2.0F, float0 + float4, 1.0F) * arg2))
        );
    }

    @Override
    public Vector3d transformPositiveX(Vector3d arg0) {
        float float0 = this.w * this.w;
        float float1 = this.x * this.x;
        float float2 = this.y * this.y;
        float float3 = this.z * this.z;
        float float4 = this.z * this.w;
        float float5 = this.x * this.y;
        float float6 = this.x * this.z;
        float float7 = this.y * this.w;
        arg0.x = float0 + float1 - float3 - float2;
        arg0.y = float5 + float4 + float4 + float5;
        arg0.z = float6 - float7 + float6 - float7;
        return arg0;
    }

    @Override
    public Vector4d transformPositiveX(Vector4d arg0) {
        float float0 = this.w * this.w;
        float float1 = this.x * this.x;
        float float2 = this.y * this.y;
        float float3 = this.z * this.z;
        float float4 = this.z * this.w;
        float float5 = this.x * this.y;
        float float6 = this.x * this.z;
        float float7 = this.y * this.w;
        arg0.x = float0 + float1 - float3 - float2;
        arg0.y = float5 + float4 + float4 + float5;
        arg0.z = float6 - float7 + float6 - float7;
        return arg0;
    }

    @Override
    public Vector3d transformUnitPositiveX(Vector3d arg0) {
        float float0 = this.y * this.y;
        float float1 = this.z * this.z;
        float float2 = this.x * this.y;
        float float3 = this.x * this.z;
        float float4 = this.y * this.w;
        float float5 = this.z * this.w;
        arg0.x = 1.0F - float0 - float0 - float1 - float1;
        arg0.y = float2 + float5 + float2 + float5;
        arg0.z = float3 - float4 + float3 - float4;
        return arg0;
    }

    @Override
    public Vector4d transformUnitPositiveX(Vector4d arg0) {
        float float0 = this.y * this.y;
        float float1 = this.z * this.z;
        float float2 = this.x * this.y;
        float float3 = this.x * this.z;
        float float4 = this.y * this.w;
        float float5 = this.z * this.w;
        arg0.x = 1.0F - float0 - float0 - float1 - float1;
        arg0.y = float2 + float5 + float2 + float5;
        arg0.z = float3 - float4 + float3 - float4;
        return arg0;
    }

    @Override
    public Vector3d transformPositiveY(Vector3d arg0) {
        float float0 = this.w * this.w;
        float float1 = this.x * this.x;
        float float2 = this.y * this.y;
        float float3 = this.z * this.z;
        float float4 = this.z * this.w;
        float float5 = this.x * this.y;
        float float6 = this.y * this.z;
        float float7 = this.x * this.w;
        arg0.x = -float4 + float5 - float4 + float5;
        arg0.y = float2 - float3 + float0 - float1;
        arg0.z = float6 + float6 + float7 + float7;
        return arg0;
    }

    @Override
    public Vector4d transformPositiveY(Vector4d arg0) {
        float float0 = this.w * this.w;
        float float1 = this.x * this.x;
        float float2 = this.y * this.y;
        float float3 = this.z * this.z;
        float float4 = this.z * this.w;
        float float5 = this.x * this.y;
        float float6 = this.y * this.z;
        float float7 = this.x * this.w;
        arg0.x = -float4 + float5 - float4 + float5;
        arg0.y = float2 - float3 + float0 - float1;
        arg0.z = float6 + float6 + float7 + float7;
        return arg0;
    }

    @Override
    public Vector4d transformUnitPositiveY(Vector4d arg0) {
        float float0 = this.x * this.x;
        float float1 = this.z * this.z;
        float float2 = this.x * this.y;
        float float3 = this.y * this.z;
        float float4 = this.x * this.w;
        float float5 = this.z * this.w;
        arg0.x = float2 - float5 + float2 - float5;
        arg0.y = 1.0F - float0 - float0 - float1 - float1;
        arg0.z = float3 + float3 + float4 + float4;
        return arg0;
    }

    @Override
    public Vector3d transformUnitPositiveY(Vector3d arg0) {
        float float0 = this.x * this.x;
        float float1 = this.z * this.z;
        float float2 = this.x * this.y;
        float float3 = this.y * this.z;
        float float4 = this.x * this.w;
        float float5 = this.z * this.w;
        arg0.x = float2 - float5 + float2 - float5;
        arg0.y = 1.0F - float0 - float0 - float1 - float1;
        arg0.z = float3 + float3 + float4 + float4;
        return arg0;
    }

    @Override
    public Vector3d transformPositiveZ(Vector3d arg0) {
        float float0 = this.w * this.w;
        float float1 = this.x * this.x;
        float float2 = this.y * this.y;
        float float3 = this.z * this.z;
        float float4 = this.x * this.z;
        float float5 = this.y * this.w;
        float float6 = this.y * this.z;
        float float7 = this.x * this.w;
        arg0.x = float5 + float4 + float4 + float5;
        arg0.y = float6 + float6 - float7 - float7;
        arg0.z = float3 - float2 - float1 + float0;
        return arg0;
    }

    @Override
    public Vector4d transformPositiveZ(Vector4d arg0) {
        float float0 = this.w * this.w;
        float float1 = this.x * this.x;
        float float2 = this.y * this.y;
        float float3 = this.z * this.z;
        float float4 = this.x * this.z;
        float float5 = this.y * this.w;
        float float6 = this.y * this.z;
        float float7 = this.x * this.w;
        arg0.x = float5 + float4 + float4 + float5;
        arg0.y = float6 + float6 - float7 - float7;
        arg0.z = float3 - float2 - float1 + float0;
        return arg0;
    }

    @Override
    public Vector4d transformUnitPositiveZ(Vector4d arg0) {
        float float0 = this.x * this.x;
        float float1 = this.y * this.y;
        float float2 = this.x * this.z;
        float float3 = this.y * this.z;
        float float4 = this.x * this.w;
        float float5 = this.y * this.w;
        arg0.x = float2 + float5 + float2 + float5;
        arg0.y = float3 + float3 - float4 - float4;
        arg0.z = 1.0F - float0 - float0 - float1 - float1;
        return arg0;
    }

    @Override
    public Vector3d transformUnitPositiveZ(Vector3d arg0) {
        float float0 = this.x * this.x;
        float float1 = this.y * this.y;
        float float2 = this.x * this.z;
        float float3 = this.y * this.z;
        float float4 = this.x * this.w;
        float float5 = this.y * this.w;
        arg0.x = float2 + float5 + float2 + float5;
        arg0.y = float3 + float3 - float4 - float4;
        arg0.z = 1.0F - float0 - float0 - float1 - float1;
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
    public Vector3d transform(float arg0, float arg1, float arg2, Vector3d arg3) {
        return this.transform((double)arg0, (double)arg1, (double)arg2, arg3);
    }

    @Override
    public Vector3d transformInverse(float arg0, float arg1, float arg2, Vector3d arg3) {
        return this.transformInverse((double)arg0, (double)arg1, (double)arg2, arg3);
    }

    @Override
    public Vector3d transform(double arg0, double arg1, double arg2, Vector3d arg3) {
        float float0 = this.x * this.x;
        float float1 = this.y * this.y;
        float float2 = this.z * this.z;
        float float3 = this.w * this.w;
        float float4 = this.x * this.y;
        float float5 = this.x * this.z;
        float float6 = this.y * this.z;
        float float7 = this.x * this.w;
        float float8 = this.z * this.w;
        float float9 = this.y * this.w;
        float float10 = 1.0F / (float0 + float1 + float2 + float3);
        return arg3.set(
            Math.fma(
                (double)((float0 - float1 - float2 + float3) * float10),
                arg0,
                Math.fma((double)(2.0F * (float4 - float8) * float10), arg1, 2.0F * (float5 + float9) * float10 * arg2)
            ),
            Math.fma(
                (double)(2.0F * (float4 + float8) * float10),
                arg0,
                Math.fma((double)((float1 - float0 - float2 + float3) * float10), arg1, 2.0F * (float6 - float7) * float10 * arg2)
            ),
            Math.fma(
                (double)(2.0F * (float5 - float9) * float10),
                arg0,
                Math.fma((double)(2.0F * (float6 + float7) * float10), arg1, (float2 - float0 - float1 + float3) * float10 * arg2)
            )
        );
    }

    @Override
    public Vector3d transformInverse(double arg0, double arg1, double arg2, Vector3d arg3) {
        float float0 = 1.0F / Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
        float float1 = this.x * float0;
        float float2 = this.y * float0;
        float float3 = this.z * float0;
        float float4 = this.w * float0;
        float float5 = float1 * float1;
        float float6 = float2 * float2;
        float float7 = float3 * float3;
        float float8 = float4 * float4;
        float float9 = float1 * float2;
        float float10 = float1 * float3;
        float float11 = float2 * float3;
        float float12 = float1 * float4;
        float float13 = float3 * float4;
        float float14 = float2 * float4;
        float float15 = 1.0F / (float5 + float6 + float7 + float8);
        return arg3.set(
            Math.fma(
                (double)((float5 - float6 - float7 + float8) * float15),
                arg0,
                Math.fma((double)(2.0F * (float9 + float13) * float15), arg1, 2.0F * (float10 - float14) * float15 * arg2)
            ),
            Math.fma(
                (double)(2.0F * (float9 - float13) * float15),
                arg0,
                Math.fma((double)((float6 - float5 - float7 + float8) * float15), arg1, 2.0F * (float11 + float12) * float15 * arg2)
            ),
            Math.fma(
                (double)(2.0F * (float10 + float14) * float15),
                arg0,
                Math.fma((double)(2.0F * (float11 - float12) * float15), arg1, (float7 - float5 - float6 + float8) * float15 * arg2)
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
        float float0 = this.x * this.x;
        float float1 = this.y * this.y;
        float float2 = this.z * this.z;
        float float3 = this.w * this.w;
        float float4 = this.x * this.y;
        float float5 = this.x * this.z;
        float float6 = this.y * this.z;
        float float7 = this.x * this.w;
        float float8 = this.z * this.w;
        float float9 = this.y * this.w;
        float float10 = 1.0F / (float0 + float1 + float2 + float3);
        return arg3.set(
            Math.fma(
                (double)((float0 - float1 - float2 + float3) * float10),
                arg0,
                Math.fma((double)(2.0F * (float4 - float8) * float10), arg1, 2.0F * (float5 + float9) * float10 * arg2)
            ),
            Math.fma(
                (double)(2.0F * (float4 + float8) * float10),
                arg0,
                Math.fma((double)((float1 - float0 - float2 + float3) * float10), arg1, 2.0F * (float6 - float7) * float10 * arg2)
            ),
            Math.fma(
                (double)(2.0F * (float5 - float9) * float10),
                arg0,
                Math.fma((double)(2.0F * (float6 + float7) * float10), arg1, (float2 - float0 - float1 + float3) * float10 * arg2)
            )
        );
    }

    @Override
    public Vector4d transformInverse(double arg0, double arg1, double arg2, Vector4d arg3) {
        float float0 = 1.0F / Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
        float float1 = this.x * float0;
        float float2 = this.y * float0;
        float float3 = this.z * float0;
        float float4 = this.w * float0;
        float float5 = float1 * float1;
        float float6 = float2 * float2;
        float float7 = float3 * float3;
        float float8 = float4 * float4;
        float float9 = float1 * float2;
        float float10 = float1 * float3;
        float float11 = float2 * float3;
        float float12 = float1 * float4;
        float float13 = float3 * float4;
        float float14 = float2 * float4;
        float float15 = 1.0F / (float5 + float6 + float7 + float8);
        return arg3.set(
            Math.fma(
                (double)((float5 - float6 - float7 + float8) * float15),
                arg0,
                Math.fma((double)(2.0F * (float9 + float13) * float15), arg1, 2.0F * (float10 - float14) * float15 * arg2)
            ),
            Math.fma(
                (double)(2.0F * (float9 - float13) * float15),
                arg0,
                Math.fma((double)((float6 - float5 - float7 + float8) * float15), arg1, 2.0F * (float11 + float12) * float15 * arg2)
            ),
            Math.fma(
                (double)(2.0F * (float10 + float14) * float15),
                arg0,
                Math.fma((double)(2.0F * (float11 - float12) * float15), arg1, (float7 - float5 - float6 + float8) * float15 * arg2)
            )
        );
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
    public Vector3d transformUnit(float arg0, float arg1, float arg2, Vector3d arg3) {
        return this.transformUnit((double)arg0, (double)arg1, (double)arg2, arg3);
    }

    @Override
    public Vector3d transformInverseUnit(float arg0, float arg1, float arg2, Vector3d arg3) {
        return this.transformInverseUnit((double)arg0, (double)arg1, (double)arg2, arg3);
    }

    @Override
    public Vector3d transformUnit(double arg0, double arg1, double arg2, Vector3d arg3) {
        float float0 = this.x * this.x;
        float float1 = this.x * this.y;
        float float2 = this.x * this.z;
        float float3 = this.x * this.w;
        float float4 = this.y * this.y;
        float float5 = this.y * this.z;
        float float6 = this.y * this.w;
        float float7 = this.z * this.z;
        float float8 = this.z * this.w;
        return arg3.set(
            Math.fma((double)Math.fma(-2.0F, float4 + float7, 1.0F), arg0, Math.fma((double)(2.0F * (float1 - float8)), arg1, 2.0F * (float2 + float6) * arg2)),
            Math.fma((double)(2.0F * (float1 + float8)), arg0, Math.fma((double)Math.fma(-2.0F, float0 + float7, 1.0F), arg1, 2.0F * (float5 - float3) * arg2)),
            Math.fma(
                (double)(2.0F * (float2 - float6)), arg0, Math.fma((double)(2.0F * (float5 + float3)), arg1, Math.fma(-2.0F, float0 + float4, 1.0F) * arg2)
            )
        );
    }

    @Override
    public Vector3d transformInverseUnit(double arg0, double arg1, double arg2, Vector3d arg3) {
        float float0 = this.x * this.x;
        float float1 = this.x * this.y;
        float float2 = this.x * this.z;
        float float3 = this.x * this.w;
        float float4 = this.y * this.y;
        float float5 = this.y * this.z;
        float float6 = this.y * this.w;
        float float7 = this.z * this.z;
        float float8 = this.z * this.w;
        return arg3.set(
            Math.fma((double)Math.fma(-2.0F, float4 + float7, 1.0F), arg0, Math.fma((double)(2.0F * (float1 + float8)), arg1, 2.0F * (float2 - float6) * arg2)),
            Math.fma((double)(2.0F * (float1 - float8)), arg0, Math.fma((double)Math.fma(-2.0F, float0 + float7, 1.0F), arg1, 2.0F * (float5 + float3) * arg2)),
            Math.fma(
                (double)(2.0F * (float2 + float6)), arg0, Math.fma((double)(2.0F * (float5 - float3)), arg1, Math.fma(-2.0F, float0 + float4, 1.0F) * arg2)
            )
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
        float float0 = this.x * this.x;
        float float1 = this.x * this.y;
        float float2 = this.x * this.z;
        float float3 = this.x * this.w;
        float float4 = this.y * this.y;
        float float5 = this.y * this.z;
        float float6 = this.y * this.w;
        float float7 = this.z * this.z;
        float float8 = this.z * this.w;
        return arg3.set(
            Math.fma((double)Math.fma(-2.0F, float4 + float7, 1.0F), arg0, Math.fma((double)(2.0F * (float1 - float8)), arg1, 2.0F * (float2 + float6) * arg2)),
            Math.fma((double)(2.0F * (float1 + float8)), arg0, Math.fma((double)Math.fma(-2.0F, float0 + float7, 1.0F), arg1, 2.0F * (float5 - float3) * arg2)),
            Math.fma(
                (double)(2.0F * (float2 - float6)), arg0, Math.fma((double)(2.0F * (float5 + float3)), arg1, Math.fma(-2.0F, float0 + float4, 1.0F) * arg2)
            )
        );
    }

    @Override
    public Vector4d transformInverseUnit(double arg0, double arg1, double arg2, Vector4d arg3) {
        float float0 = this.x * this.x;
        float float1 = this.x * this.y;
        float float2 = this.x * this.z;
        float float3 = this.x * this.w;
        float float4 = this.y * this.y;
        float float5 = this.y * this.z;
        float float6 = this.y * this.w;
        float float7 = this.z * this.z;
        float float8 = this.z * this.w;
        return arg3.set(
            Math.fma((double)Math.fma(-2.0F, float4 + float7, 1.0F), arg0, Math.fma((double)(2.0F * (float1 + float8)), arg1, 2.0F * (float2 - float6) * arg2)),
            Math.fma((double)(2.0F * (float1 - float8)), arg0, Math.fma((double)Math.fma(-2.0F, float0 + float7, 1.0F), arg1, 2.0F * (float5 + float3) * arg2)),
            Math.fma(
                (double)(2.0F * (float2 + float6)), arg0, Math.fma((double)(2.0F * (float5 - float3)), arg1, Math.fma(-2.0F, float0 + float4, 1.0F) * arg2)
            )
        );
    }

    @Override
    public Quaternionf invert(Quaternionf arg0) {
        float float0 = 1.0F / Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
        arg0.x = -this.x * float0;
        arg0.y = -this.y * float0;
        arg0.z = -this.z * float0;
        arg0.w = this.w * float0;
        return arg0;
    }

    public Quaternionf invert() {
        return this.invert(this);
    }

    @Override
    public Quaternionf div(Quaternionfc arg0, Quaternionf arg1) {
        float float0 = 1.0F / Math.fma(arg0.x(), arg0.x(), Math.fma(arg0.y(), arg0.y(), Math.fma(arg0.z(), arg0.z(), arg0.w() * arg0.w())));
        float float1 = -arg0.x() * float0;
        float float2 = -arg0.y() * float0;
        float float3 = -arg0.z() * float0;
        float float4 = arg0.w() * float0;
        return arg1.set(
            Math.fma(this.w, float1, Math.fma(this.x, float4, Math.fma(this.y, float3, -this.z * float2))),
            Math.fma(this.w, float2, Math.fma(-this.x, float3, Math.fma(this.y, float4, this.z * float1))),
            Math.fma(this.w, float3, Math.fma(this.x, float2, Math.fma(-this.y, float1, this.z * float4))),
            Math.fma(this.w, float4, Math.fma(-this.x, float1, Math.fma(-this.y, float2, -this.z * float3)))
        );
    }

    public Quaternionf div(Quaternionfc arg0) {
        return this.div(arg0, this);
    }

    public Quaternionf conjugate() {
        return this.conjugate(this);
    }

    @Override
    public Quaternionf conjugate(Quaternionf arg0) {
        arg0.x = -this.x;
        arg0.y = -this.y;
        arg0.z = -this.z;
        arg0.w = this.w;
        return arg0;
    }

    public Quaternionf identity() {
        this.x = 0.0F;
        this.y = 0.0F;
        this.z = 0.0F;
        this.w = 1.0F;
        return this;
    }

    public Quaternionf rotateXYZ(float arg0, float arg1, float arg2) {
        return this.rotateXYZ(arg0, arg1, arg2, this);
    }

    @Override
    public Quaternionf rotateXYZ(float arg0, float arg1, float arg2, Quaternionf arg3) {
        float float0 = Math.sin(arg0 * 0.5F);
        float float1 = Math.cosFromSin(float0, arg0 * 0.5F);
        float float2 = Math.sin(arg1 * 0.5F);
        float float3 = Math.cosFromSin(float2, arg1 * 0.5F);
        float float4 = Math.sin(arg2 * 0.5F);
        float float5 = Math.cosFromSin(float4, arg2 * 0.5F);
        float float6 = float3 * float5;
        float float7 = float2 * float4;
        float float8 = float2 * float5;
        float float9 = float3 * float4;
        float float10 = float1 * float6 - float0 * float7;
        float float11 = float0 * float6 + float1 * float7;
        float float12 = float1 * float8 - float0 * float9;
        float float13 = float1 * float9 + float0 * float8;
        return arg3.set(
            Math.fma(this.w, float11, Math.fma(this.x, float10, Math.fma(this.y, float13, -this.z * float12))),
            Math.fma(this.w, float12, Math.fma(-this.x, float13, Math.fma(this.y, float10, this.z * float11))),
            Math.fma(this.w, float13, Math.fma(this.x, float12, Math.fma(-this.y, float11, this.z * float10))),
            Math.fma(this.w, float10, Math.fma(-this.x, float11, Math.fma(-this.y, float12, -this.z * float13)))
        );
    }

    public Quaternionf rotateZYX(float arg0, float arg1, float arg2) {
        return this.rotateZYX(arg0, arg1, arg2, this);
    }

    @Override
    public Quaternionf rotateZYX(float arg0, float arg1, float arg2, Quaternionf arg3) {
        float float0 = Math.sin(arg2 * 0.5F);
        float float1 = Math.cosFromSin(float0, arg2 * 0.5F);
        float float2 = Math.sin(arg1 * 0.5F);
        float float3 = Math.cosFromSin(float2, arg1 * 0.5F);
        float float4 = Math.sin(arg0 * 0.5F);
        float float5 = Math.cosFromSin(float4, arg0 * 0.5F);
        float float6 = float3 * float5;
        float float7 = float2 * float4;
        float float8 = float2 * float5;
        float float9 = float3 * float4;
        float float10 = float1 * float6 + float0 * float7;
        float float11 = float0 * float6 - float1 * float7;
        float float12 = float1 * float8 + float0 * float9;
        float float13 = float1 * float9 - float0 * float8;
        return arg3.set(
            Math.fma(this.w, float11, Math.fma(this.x, float10, Math.fma(this.y, float13, -this.z * float12))),
            Math.fma(this.w, float12, Math.fma(-this.x, float13, Math.fma(this.y, float10, this.z * float11))),
            Math.fma(this.w, float13, Math.fma(this.x, float12, Math.fma(-this.y, float11, this.z * float10))),
            Math.fma(this.w, float10, Math.fma(-this.x, float11, Math.fma(-this.y, float12, -this.z * float13)))
        );
    }

    public Quaternionf rotateYXZ(float arg0, float arg1, float arg2) {
        return this.rotateYXZ(arg0, arg1, arg2, this);
    }

    @Override
    public Quaternionf rotateYXZ(float arg0, float arg1, float arg2, Quaternionf arg3) {
        float float0 = Math.sin(arg1 * 0.5F);
        float float1 = Math.cosFromSin(float0, arg1 * 0.5F);
        float float2 = Math.sin(arg0 * 0.5F);
        float float3 = Math.cosFromSin(float2, arg0 * 0.5F);
        float float4 = Math.sin(arg2 * 0.5F);
        float float5 = Math.cosFromSin(float4, arg2 * 0.5F);
        float float6 = float3 * float0;
        float float7 = float2 * float1;
        float float8 = float2 * float0;
        float float9 = float3 * float1;
        float float10 = float6 * float5 + float7 * float4;
        float float11 = float7 * float5 - float6 * float4;
        float float12 = float9 * float4 - float8 * float5;
        float float13 = float9 * float5 + float8 * float4;
        return arg3.set(
            Math.fma(this.w, float10, Math.fma(this.x, float13, Math.fma(this.y, float12, -this.z * float11))),
            Math.fma(this.w, float11, Math.fma(-this.x, float12, Math.fma(this.y, float13, this.z * float10))),
            Math.fma(this.w, float12, Math.fma(this.x, float11, Math.fma(-this.y, float10, this.z * float13))),
            Math.fma(this.w, float13, Math.fma(-this.x, float10, Math.fma(-this.y, float11, -this.z * float12)))
        );
    }

    @Override
    public Vector3f getEulerAnglesXYZ(Vector3f arg0) {
        arg0.x = Math.atan2(2.0F * (this.x * this.w - this.y * this.z), 1.0F - 2.0F * (this.x * this.x + this.y * this.y));
        arg0.y = Math.safeAsin(2.0F * (this.x * this.z + this.y * this.w));
        arg0.z = Math.atan2(2.0F * (this.z * this.w - this.x * this.y), 1.0F - 2.0F * (this.y * this.y + this.z * this.z));
        return arg0;
    }

    @Override
    public float lengthSquared() {
        return Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
    }

    public Quaternionf rotationXYZ(float arg0, float arg1, float arg2) {
        float float0 = Math.sin(arg0 * 0.5F);
        float float1 = Math.cosFromSin(float0, arg0 * 0.5F);
        float float2 = Math.sin(arg1 * 0.5F);
        float float3 = Math.cosFromSin(float2, arg1 * 0.5F);
        float float4 = Math.sin(arg2 * 0.5F);
        float float5 = Math.cosFromSin(float4, arg2 * 0.5F);
        float float6 = float3 * float5;
        float float7 = float2 * float4;
        float float8 = float2 * float5;
        float float9 = float3 * float4;
        this.w = float1 * float6 - float0 * float7;
        this.x = float0 * float6 + float1 * float7;
        this.y = float1 * float8 - float0 * float9;
        this.z = float1 * float9 + float0 * float8;
        return this;
    }

    public Quaternionf rotationZYX(float arg0, float arg1, float arg2) {
        float float0 = Math.sin(arg2 * 0.5F);
        float float1 = Math.cosFromSin(float0, arg2 * 0.5F);
        float float2 = Math.sin(arg1 * 0.5F);
        float float3 = Math.cosFromSin(float2, arg1 * 0.5F);
        float float4 = Math.sin(arg0 * 0.5F);
        float float5 = Math.cosFromSin(float4, arg0 * 0.5F);
        float float6 = float3 * float5;
        float float7 = float2 * float4;
        float float8 = float2 * float5;
        float float9 = float3 * float4;
        this.w = float1 * float6 + float0 * float7;
        this.x = float0 * float6 - float1 * float7;
        this.y = float1 * float8 + float0 * float9;
        this.z = float1 * float9 - float0 * float8;
        return this;
    }

    public Quaternionf rotationYXZ(float arg0, float arg1, float arg2) {
        float float0 = Math.sin(arg1 * 0.5F);
        float float1 = Math.cosFromSin(float0, arg1 * 0.5F);
        float float2 = Math.sin(arg0 * 0.5F);
        float float3 = Math.cosFromSin(float2, arg0 * 0.5F);
        float float4 = Math.sin(arg2 * 0.5F);
        float float5 = Math.cosFromSin(float4, arg2 * 0.5F);
        float float6 = float3 * float0;
        float float7 = float2 * float1;
        float float8 = float2 * float0;
        float float9 = float3 * float1;
        this.x = float6 * float5 + float7 * float4;
        this.y = float7 * float5 - float6 * float4;
        this.z = float9 * float4 - float8 * float5;
        this.w = float9 * float5 + float8 * float4;
        return this;
    }

    public Quaternionf slerp(Quaternionfc arg0, float arg1) {
        return this.slerp(arg0, arg1, this);
    }

    @Override
    public Quaternionf slerp(Quaternionfc arg0, float arg1, Quaternionf arg2) {
        float float0 = Math.fma(this.x, arg0.x(), Math.fma(this.y, arg0.y(), Math.fma(this.z, arg0.z(), this.w * arg0.w())));
        float float1 = Math.abs(float0);
        float float2;
        float float3;
        if (1.0F - float1 > 1.0E-6F) {
            float float4 = 1.0F - float1 * float1;
            float float5 = Math.invsqrt(float4);
            float float6 = Math.atan2(float4 * float5, float1);
            float2 = (float)(Math.sin((1.0 - arg1) * float6) * float5);
            float3 = Math.sin(arg1 * float6) * float5;
        } else {
            float2 = 1.0F - arg1;
            float3 = arg1;
        }

        float3 = float0 >= 0.0F ? float3 : -float3;
        arg2.x = Math.fma(float2, this.x, float3 * arg0.x());
        arg2.y = Math.fma(float2, this.y, float3 * arg0.y());
        arg2.z = Math.fma(float2, this.z, float3 * arg0.z());
        arg2.w = Math.fma(float2, this.w, float3 * arg0.w());
        return arg2;
    }

    public static Quaternionfc slerp(Quaternionf[] quaternionfs, float[] floats, Quaternionf quaternionf) {
        quaternionf.set(quaternionfs[0]);
        float float0 = floats[0];

        for (int int0 = 1; int0 < quaternionfs.length; int0++) {
            float float1 = floats[int0];
            float float2 = float1 / (float0 + float1);
            float0 += float1;
            quaternionf.slerp(quaternionfs[int0], float2);
        }

        return quaternionf;
    }

    public Quaternionf scale(float arg0) {
        return this.scale(arg0, this);
    }

    @Override
    public Quaternionf scale(float arg0, Quaternionf arg1) {
        float float0 = Math.sqrt(arg0);
        arg1.x = float0 * this.x;
        arg1.y = float0 * this.y;
        arg1.z = float0 * this.z;
        arg1.w = float0 * this.w;
        return arg1;
    }

    public Quaternionf scaling(float arg0) {
        float float0 = Math.sqrt(arg0);
        this.x = 0.0F;
        this.y = 0.0F;
        this.z = 0.0F;
        this.w = float0;
        return this;
    }

    public Quaternionf integrate(float arg0, float arg1, float arg2, float arg3) {
        return this.integrate(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Quaternionf integrate(float arg0, float arg1, float arg2, float arg3, Quaternionf arg4) {
        float float0 = arg0 * arg1 * 0.5F;
        float float1 = arg0 * arg2 * 0.5F;
        float float2 = arg0 * arg3 * 0.5F;
        float float3 = float0 * float0 + float1 * float1 + float2 * float2;
        float float4;
        float float5;
        if (float3 * float3 / 24.0F < 1.0E-8F) {
            float5 = 1.0F - float3 * 0.5F;
            float4 = 1.0F - float3 / 6.0F;
        } else {
            float float6 = Math.sqrt(float3);
            float float7 = Math.sin(float6);
            float4 = float7 / float6;
            float5 = Math.cosFromSin(float7, float6);
        }

        float float8 = float0 * float4;
        float float9 = float1 * float4;
        float float10 = float2 * float4;
        return arg4.set(
            Math.fma(float5, this.x, Math.fma(float8, this.w, Math.fma(float9, this.z, -float10 * this.y))),
            Math.fma(float5, this.y, Math.fma(-float8, this.z, Math.fma(float9, this.w, float10 * this.x))),
            Math.fma(float5, this.z, Math.fma(float8, this.y, Math.fma(-float9, this.x, float10 * this.w))),
            Math.fma(float5, this.w, Math.fma(-float8, this.x, Math.fma(-float9, this.y, -float10 * this.z)))
        );
    }

    public Quaternionf nlerp(Quaternionfc arg0, float arg1) {
        return this.nlerp(arg0, arg1, this);
    }

    @Override
    public Quaternionf nlerp(Quaternionfc arg0, float arg1, Quaternionf arg2) {
        float float0 = Math.fma(this.x, arg0.x(), Math.fma(this.y, arg0.y(), Math.fma(this.z, arg0.z(), this.w * arg0.w())));
        float float1 = 1.0F - arg1;
        float float2 = float0 >= 0.0F ? arg1 : -arg1;
        arg2.x = Math.fma(float1, this.x, float2 * arg0.x());
        arg2.y = Math.fma(float1, this.y, float2 * arg0.y());
        arg2.z = Math.fma(float1, this.z, float2 * arg0.z());
        arg2.w = Math.fma(float1, this.w, float2 * arg0.w());
        float float3 = Math.invsqrt(Math.fma(arg2.x, arg2.x, Math.fma(arg2.y, arg2.y, Math.fma(arg2.z, arg2.z, arg2.w * arg2.w))));
        arg2.x *= float3;
        arg2.y *= float3;
        arg2.z *= float3;
        arg2.w *= float3;
        return arg2;
    }

    public static Quaternionfc nlerp(Quaternionfc[] quaternionfcs, float[] floats, Quaternionf quaternionf) {
        quaternionf.set(quaternionfcs[0]);
        float float0 = floats[0];

        for (int int0 = 1; int0 < quaternionfcs.length; int0++) {
            float float1 = floats[int0];
            float float2 = float1 / (float0 + float1);
            float0 += float1;
            quaternionf.nlerp(quaternionfcs[int0], float2);
        }

        return quaternionf;
    }

    @Override
    public Quaternionf nlerpIterative(Quaternionfc arg0, float arg1, float arg2, Quaternionf arg3) {
        float float0 = this.x;
        float float1 = this.y;
        float float2 = this.z;
        float float3 = this.w;
        float float4 = arg0.x();
        float float5 = arg0.y();
        float float6 = arg0.z();
        float float7 = arg0.w();
        float float8 = Math.fma(float0, float4, Math.fma(float1, float5, Math.fma(float2, float6, float3 * float7)));
        float float9 = Math.abs(float8);
        if (0.999999F < float9) {
            return arg3.set(this);
        } else {
            float float10;
            for (float10 = arg1; float9 < arg2; float9 = Math.abs(float8)) {
                float float11 = 0.5F;
                float float12 = float8 >= 0.0F ? 0.5F : -0.5F;
                if (float10 < 0.5F) {
                    float4 = Math.fma(float11, float4, float12 * float0);
                    float5 = Math.fma(float11, float5, float12 * float1);
                    float6 = Math.fma(float11, float6, float12 * float2);
                    float7 = Math.fma(float11, float7, float12 * float3);
                    float float13 = Math.invsqrt(Math.fma(float4, float4, Math.fma(float5, float5, Math.fma(float6, float6, float7 * float7))));
                    float4 *= float13;
                    float5 *= float13;
                    float6 *= float13;
                    float7 *= float13;
                    float10 += float10;
                } else {
                    float0 = Math.fma(float11, float0, float12 * float4);
                    float1 = Math.fma(float11, float1, float12 * float5);
                    float2 = Math.fma(float11, float2, float12 * float6);
                    float3 = Math.fma(float11, float3, float12 * float7);
                    float float14 = Math.invsqrt(Math.fma(float0, float0, Math.fma(float1, float1, Math.fma(float2, float2, float3 * float3))));
                    float0 *= float14;
                    float1 *= float14;
                    float2 *= float14;
                    float3 *= float14;
                    float10 = float10 + float10 - 1.0F;
                }

                float8 = Math.fma(float0, float4, Math.fma(float1, float5, Math.fma(float2, float6, float3 * float7)));
            }

            float float15 = 1.0F - float10;
            float float16 = float8 >= 0.0F ? float10 : -float10;
            float float17 = Math.fma(float15, float0, float16 * float4);
            float float18 = Math.fma(float15, float1, float16 * float5);
            float float19 = Math.fma(float15, float2, float16 * float6);
            float float20 = Math.fma(float15, float3, float16 * float7);
            float float21 = Math.invsqrt(Math.fma(float17, float17, Math.fma(float18, float18, Math.fma(float19, float19, float20 * float20))));
            arg3.x = float17 * float21;
            arg3.y = float18 * float21;
            arg3.z = float19 * float21;
            arg3.w = float20 * float21;
            return arg3;
        }
    }

    public Quaternionf nlerpIterative(Quaternionfc arg0, float arg1, float arg2) {
        return this.nlerpIterative(arg0, arg1, arg2, this);
    }

    public static Quaternionfc nlerpIterative(Quaternionf[] quaternionfs, float[] floats, float float3, Quaternionf quaternionf) {
        quaternionf.set(quaternionfs[0]);
        float float0 = floats[0];

        for (int int0 = 1; int0 < quaternionfs.length; int0++) {
            float float1 = floats[int0];
            float float2 = float1 / (float0 + float1);
            float0 += float1;
            quaternionf.nlerpIterative(quaternionfs[int0], float2, float3);
        }

        return quaternionf;
    }

    public Quaternionf lookAlong(Vector3fc arg0, Vector3fc arg1) {
        return this.lookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), this);
    }

    @Override
    public Quaternionf lookAlong(Vector3fc arg0, Vector3fc arg1, Quaternionf arg2) {
        return this.lookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    public Quaternionf lookAlong(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.lookAlong(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    @Override
    public Quaternionf lookAlong(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Quaternionf arg6) {
        float float0 = Math.invsqrt(arg0 * arg0 + arg1 * arg1 + arg2 * arg2);
        float float1 = -arg0 * float0;
        float float2 = -arg1 * float0;
        float float3 = -arg2 * float0;
        float float4 = arg4 * float3 - arg5 * float2;
        float float5 = arg5 * float1 - arg3 * float3;
        float float6 = arg3 * float2 - arg4 * float1;
        float float7 = Math.invsqrt(float4 * float4 + float5 * float5 + float6 * float6);
        float4 *= float7;
        float5 *= float7;
        float6 *= float7;
        float float8 = float2 * float6 - float3 * float5;
        float float9 = float3 * float4 - float1 * float6;
        float float10 = float1 * float5 - float2 * float4;
        double double0 = float4 + float9 + float3;
        float float11;
        float float12;
        float float13;
        float float14;
        if (double0 >= 0.0) {
            double double1 = Math.sqrt(double0 + 1.0);
            float14 = (float)(double1 * 0.5);
            double1 = 0.5 / double1;
            float11 = (float)((float2 - float10) * double1);
            float12 = (float)((float6 - float1) * double1);
            float13 = (float)((float8 - float5) * double1);
        } else if (float4 > float9 && float4 > float3) {
            double double2 = Math.sqrt(1.0 + float4 - float9 - float3);
            float11 = (float)(double2 * 0.5);
            double2 = 0.5 / double2;
            float12 = (float)((float5 + float8) * double2);
            float13 = (float)((float1 + float6) * double2);
            float14 = (float)((float2 - float10) * double2);
        } else if (float9 > float3) {
            double double3 = Math.sqrt(1.0 + float9 - float4 - float3);
            float12 = (float)(double3 * 0.5);
            double3 = 0.5 / double3;
            float11 = (float)((float5 + float8) * double3);
            float13 = (float)((float10 + float2) * double3);
            float14 = (float)((float6 - float1) * double3);
        } else {
            double double4 = Math.sqrt(1.0 + float3 - float4 - float9);
            float13 = (float)(double4 * 0.5);
            double4 = 0.5 / double4;
            float11 = (float)((float1 + float6) * double4);
            float12 = (float)((float10 + float2) * double4);
            float14 = (float)((float8 - float5) * double4);
        }

        return arg6.set(
            Math.fma(this.w, float11, Math.fma(this.x, float14, Math.fma(this.y, float13, -this.z * float12))),
            Math.fma(this.w, float12, Math.fma(-this.x, float13, Math.fma(this.y, float14, this.z * float11))),
            Math.fma(this.w, float13, Math.fma(this.x, float12, Math.fma(-this.y, float11, this.z * float14))),
            Math.fma(this.w, float14, Math.fma(-this.x, float11, Math.fma(-this.y, float12, -this.z * float13)))
        );
    }

    public Quaternionf rotationTo(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        float float0 = Math.invsqrt(Math.fma(arg0, arg0, Math.fma(arg1, arg1, arg2 * arg2)));
        float float1 = Math.invsqrt(Math.fma(arg3, arg3, Math.fma(arg4, arg4, arg5 * arg5)));
        float float2 = arg0 * float0;
        float float3 = arg1 * float0;
        float float4 = arg2 * float0;
        float float5 = arg3 * float1;
        float float6 = arg4 * float1;
        float float7 = arg5 * float1;
        float float8 = float2 * float5 + float3 * float6 + float4 * float7;
        if (float8 < -0.999999F) {
            float float9 = float3;
            float float10 = -float2;
            float float11 = 0.0F;
            float float12 = 0.0F;
            if (float3 * float3 + float10 * float10 == 0.0F) {
                float9 = 0.0F;
                float10 = float4;
                float11 = -float3;
                float12 = 0.0F;
            }

            this.x = float9;
            this.y = float10;
            this.z = float11;
            this.w = 0.0F;
        } else {
            float float13 = Math.sqrt((1.0F + float8) * 2.0F);
            float float14 = 1.0F / float13;
            float float15 = float3 * float7 - float4 * float6;
            float float16 = float4 * float5 - float2 * float7;
            float float17 = float2 * float6 - float3 * float5;
            float float18 = float15 * float14;
            float float19 = float16 * float14;
            float float20 = float17 * float14;
            float float21 = float13 * 0.5F;
            float float22 = Math.invsqrt(Math.fma(float18, float18, Math.fma(float19, float19, Math.fma(float20, float20, float21 * float21))));
            this.x = float18 * float22;
            this.y = float19 * float22;
            this.z = float20 * float22;
            this.w = float21 * float22;
        }

        return this;
    }

    public Quaternionf rotationTo(Vector3fc arg0, Vector3fc arg1) {
        return this.rotationTo(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    @Override
    public Quaternionf rotateTo(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Quaternionf arg6) {
        float float0 = Math.invsqrt(Math.fma(arg0, arg0, Math.fma(arg1, arg1, arg2 * arg2)));
        float float1 = Math.invsqrt(Math.fma(arg3, arg3, Math.fma(arg4, arg4, arg5 * arg5)));
        float float2 = arg0 * float0;
        float float3 = arg1 * float0;
        float float4 = arg2 * float0;
        float float5 = arg3 * float1;
        float float6 = arg4 * float1;
        float float7 = arg5 * float1;
        float float8 = float2 * float5 + float3 * float6 + float4 * float7;
        float float9;
        float float10;
        float float11;
        float float12;
        if (float8 < -0.999999F) {
            float9 = float3;
            float10 = -float2;
            float11 = 0.0F;
            float12 = 0.0F;
            if (float3 * float3 + float10 * float10 == 0.0F) {
                float9 = 0.0F;
                float10 = float4;
                float11 = -float3;
                float12 = 0.0F;
            }
        } else {
            float float13 = Math.sqrt((1.0F + float8) * 2.0F);
            float float14 = 1.0F / float13;
            float float15 = float3 * float7 - float4 * float6;
            float float16 = float4 * float5 - float2 * float7;
            float float17 = float2 * float6 - float3 * float5;
            float9 = float15 * float14;
            float10 = float16 * float14;
            float11 = float17 * float14;
            float12 = float13 * 0.5F;
            float float18 = Math.invsqrt(Math.fma(float9, float9, Math.fma(float10, float10, Math.fma(float11, float11, float12 * float12))));
            float9 *= float18;
            float10 *= float18;
            float11 *= float18;
            float12 *= float18;
        }

        return arg6.set(
            Math.fma(this.w, float9, Math.fma(this.x, float12, Math.fma(this.y, float11, -this.z * float10))),
            Math.fma(this.w, float10, Math.fma(-this.x, float11, Math.fma(this.y, float12, this.z * float9))),
            Math.fma(this.w, float11, Math.fma(this.x, float10, Math.fma(-this.y, float9, this.z * float12))),
            Math.fma(this.w, float12, Math.fma(-this.x, float9, Math.fma(-this.y, float10, -this.z * float11)))
        );
    }

    public Quaternionf rotateTo(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.rotateTo(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    @Override
    public Quaternionf rotateTo(Vector3fc arg0, Vector3fc arg1, Quaternionf arg2) {
        return this.rotateTo(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    public Quaternionf rotateTo(Vector3fc arg0, Vector3fc arg1) {
        return this.rotateTo(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), this);
    }

    public Quaternionf rotateX(float arg0) {
        return this.rotateX(arg0, this);
    }

    @Override
    public Quaternionf rotateX(float arg0, Quaternionf arg1) {
        float float0 = Math.sin(arg0 * 0.5F);
        float float1 = Math.cosFromSin(float0, arg0 * 0.5F);
        return arg1.set(
            this.w * float0 + this.x * float1, this.y * float1 + this.z * float0, this.z * float1 - this.y * float0, this.w * float1 - this.x * float0
        );
    }

    public Quaternionf rotateY(float arg0) {
        return this.rotateY(arg0, this);
    }

    @Override
    public Quaternionf rotateY(float arg0, Quaternionf arg1) {
        float float0 = Math.sin(arg0 * 0.5F);
        float float1 = Math.cosFromSin(float0, arg0 * 0.5F);
        return arg1.set(
            this.x * float1 - this.z * float0, this.w * float0 + this.y * float1, this.x * float0 + this.z * float1, this.w * float1 - this.y * float0
        );
    }

    public Quaternionf rotateZ(float arg0) {
        return this.rotateZ(arg0, this);
    }

    @Override
    public Quaternionf rotateZ(float arg0, Quaternionf arg1) {
        float float0 = Math.sin(arg0 * 0.5F);
        float float1 = Math.cosFromSin(float0, arg0 * 0.5F);
        return arg1.set(
            this.x * float1 + this.y * float0, this.y * float1 - this.x * float0, this.w * float0 + this.z * float1, this.w * float1 - this.z * float0
        );
    }

    public Quaternionf rotateLocalX(float arg0) {
        return this.rotateLocalX(arg0, this);
    }

    @Override
    public Quaternionf rotateLocalX(float arg0, Quaternionf arg1) {
        float float0 = arg0 * 0.5F;
        float float1 = Math.sin(float0);
        float float2 = Math.cosFromSin(float1, float0);
        arg1.set(float2 * this.x + float1 * this.w, float2 * this.y - float1 * this.z, float2 * this.z + float1 * this.y, float2 * this.w - float1 * this.x);
        return arg1;
    }

    public Quaternionf rotateLocalY(float arg0) {
        return this.rotateLocalY(arg0, this);
    }

    @Override
    public Quaternionf rotateLocalY(float arg0, Quaternionf arg1) {
        float float0 = arg0 * 0.5F;
        float float1 = Math.sin(float0);
        float float2 = Math.cosFromSin(float1, float0);
        arg1.set(float2 * this.x + float1 * this.z, float2 * this.y + float1 * this.w, float2 * this.z - float1 * this.x, float2 * this.w - float1 * this.y);
        return arg1;
    }

    public Quaternionf rotateLocalZ(float arg0) {
        return this.rotateLocalZ(arg0, this);
    }

    @Override
    public Quaternionf rotateLocalZ(float arg0, Quaternionf arg1) {
        float float0 = arg0 * 0.5F;
        float float1 = Math.sin(float0);
        float float2 = Math.cosFromSin(float1, float0);
        arg1.set(float2 * this.x - float1 * this.y, float2 * this.y + float1 * this.x, float2 * this.z + float1 * this.w, float2 * this.w - float1 * this.z);
        return arg1;
    }

    @Override
    public Quaternionf rotateAxis(float arg0, float arg1, float arg2, float arg3, Quaternionf arg4) {
        float float0 = arg0 / 2.0F;
        float float1 = Math.sin(float0);
        float float2 = Math.invsqrt(Math.fma(arg1, arg1, Math.fma(arg2, arg2, arg3 * arg3)));
        float float3 = arg1 * float2 * float1;
        float float4 = arg2 * float2 * float1;
        float float5 = arg3 * float2 * float1;
        float float6 = Math.cosFromSin(float1, float0);
        return arg4.set(
            Math.fma(this.w, float3, Math.fma(this.x, float6, Math.fma(this.y, float5, -this.z * float4))),
            Math.fma(this.w, float4, Math.fma(-this.x, float5, Math.fma(this.y, float6, this.z * float3))),
            Math.fma(this.w, float5, Math.fma(this.x, float4, Math.fma(-this.y, float3, this.z * float6))),
            Math.fma(this.w, float6, Math.fma(-this.x, float3, Math.fma(-this.y, float4, -this.z * float5)))
        );
    }

    @Override
    public Quaternionf rotateAxis(float arg0, Vector3fc arg1, Quaternionf arg2) {
        return this.rotateAxis(arg0, arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    public Quaternionf rotateAxis(float arg0, Vector3fc arg1) {
        return this.rotateAxis(arg0, arg1.x(), arg1.y(), arg1.z(), this);
    }

    public Quaternionf rotateAxis(float arg0, float arg1, float arg2, float arg3) {
        return this.rotateAxis(arg0, arg1, arg2, arg3, this);
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
        arg0.writeFloat(this.x);
        arg0.writeFloat(this.y);
        arg0.writeFloat(this.z);
        arg0.writeFloat(this.w);
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        this.x = arg0.readFloat();
        this.y = arg0.readFloat();
        this.z = arg0.readFloat();
        this.w = arg0.readFloat();
    }

    @Override
    public int hashCode() {
        int int0 = 1;
        int0 = 31 * int0 + Float.floatToIntBits(this.w);
        int0 = 31 * int0 + Float.floatToIntBits(this.x);
        int0 = 31 * int0 + Float.floatToIntBits(this.y);
        return 31 * int0 + Float.floatToIntBits(this.z);
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
            Quaternionf quaternionf = (Quaternionf)arg0;
            if (Float.floatToIntBits(this.w) != Float.floatToIntBits(quaternionf.w)) {
                return false;
            } else if (Float.floatToIntBits(this.x) != Float.floatToIntBits(quaternionf.x)) {
                return false;
            } else {
                return Float.floatToIntBits(this.y) != Float.floatToIntBits(quaternionf.y)
                    ? false
                    : Float.floatToIntBits(this.z) == Float.floatToIntBits(quaternionf.z);
            }
        }
    }

    public Quaternionf difference(Quaternionf arg0) {
        return this.difference(arg0, this);
    }

    @Override
    public Quaternionf difference(Quaternionfc arg0, Quaternionf arg1) {
        float float0 = 1.0F / this.lengthSquared();
        float float1 = -this.x * float0;
        float float2 = -this.y * float0;
        float float3 = -this.z * float0;
        float float4 = this.w * float0;
        arg1.set(
            Math.fma(float4, arg0.x(), Math.fma(float1, arg0.w(), Math.fma(float2, arg0.z(), -float3 * arg0.y()))),
            Math.fma(float4, arg0.y(), Math.fma(-float1, arg0.z(), Math.fma(float2, arg0.w(), float3 * arg0.x()))),
            Math.fma(float4, arg0.z(), Math.fma(float1, arg0.y(), Math.fma(-float2, arg0.x(), float3 * arg0.w()))),
            Math.fma(float4, arg0.w(), Math.fma(-float1, arg0.x(), Math.fma(-float2, arg0.y(), -float3 * arg0.z())))
        );
        return arg1;
    }

    @Override
    public Vector3f positiveX(Vector3f arg0) {
        float float0 = 1.0F / this.lengthSquared();
        float float1 = -this.x * float0;
        float float2 = -this.y * float0;
        float float3 = -this.z * float0;
        float float4 = this.w * float0;
        float float5 = float2 + float2;
        float float6 = float3 + float3;
        arg0.x = -float2 * float5 - float3 * float6 + 1.0F;
        arg0.y = float1 * float5 + float4 * float6;
        arg0.z = float1 * float6 - float4 * float5;
        return arg0;
    }

    @Override
    public Vector3f normalizedPositiveX(Vector3f arg0) {
        float float0 = this.y + this.y;
        float float1 = this.z + this.z;
        arg0.x = -this.y * float0 - this.z * float1 + 1.0F;
        arg0.y = this.x * float0 - this.w * float1;
        arg0.z = this.x * float1 + this.w * float0;
        return arg0;
    }

    @Override
    public Vector3f positiveY(Vector3f arg0) {
        float float0 = 1.0F / this.lengthSquared();
        float float1 = -this.x * float0;
        float float2 = -this.y * float0;
        float float3 = -this.z * float0;
        float float4 = this.w * float0;
        float float5 = float1 + float1;
        float float6 = float2 + float2;
        float float7 = float3 + float3;
        arg0.x = float1 * float6 - float4 * float7;
        arg0.y = -float1 * float5 - float3 * float7 + 1.0F;
        arg0.z = float2 * float7 + float4 * float5;
        return arg0;
    }

    @Override
    public Vector3f normalizedPositiveY(Vector3f arg0) {
        float float0 = this.x + this.x;
        float float1 = this.y + this.y;
        float float2 = this.z + this.z;
        arg0.x = this.x * float1 + this.w * float2;
        arg0.y = -this.x * float0 - this.z * float2 + 1.0F;
        arg0.z = this.y * float2 - this.w * float0;
        return arg0;
    }

    @Override
    public Vector3f positiveZ(Vector3f arg0) {
        float float0 = 1.0F / this.lengthSquared();
        float float1 = -this.x * float0;
        float float2 = -this.y * float0;
        float float3 = -this.z * float0;
        float float4 = this.w * float0;
        float float5 = float1 + float1;
        float float6 = float2 + float2;
        float float7 = float3 + float3;
        arg0.x = float1 * float7 + float4 * float6;
        arg0.y = float2 * float7 - float4 * float5;
        arg0.z = -float1 * float5 - float2 * float6 + 1.0F;
        return arg0;
    }

    @Override
    public Vector3f normalizedPositiveZ(Vector3f arg0) {
        float float0 = this.x + this.x;
        float float1 = this.y + this.y;
        float float2 = this.z + this.z;
        arg0.x = this.x * float2 - this.w * float1;
        arg0.y = this.y * float2 + this.w * float0;
        arg0.z = -this.x * float0 - this.y * float1 + 1.0F;
        return arg0;
    }

    public Quaternionf conjugateBy(Quaternionfc arg0) {
        return this.conjugateBy(arg0, this);
    }

    @Override
    public Quaternionf conjugateBy(Quaternionfc arg0, Quaternionf arg1) {
        float float0 = 1.0F / arg0.lengthSquared();
        float float1 = -arg0.x() * float0;
        float float2 = -arg0.y() * float0;
        float float3 = -arg0.z() * float0;
        float float4 = arg0.w() * float0;
        float float5 = Math.fma(arg0.w(), this.x, Math.fma(arg0.x(), this.w, Math.fma(arg0.y(), this.z, -arg0.z() * this.y)));
        float float6 = Math.fma(arg0.w(), this.y, Math.fma(-arg0.x(), this.z, Math.fma(arg0.y(), this.w, arg0.z() * this.x)));
        float float7 = Math.fma(arg0.w(), this.z, Math.fma(arg0.x(), this.y, Math.fma(-arg0.y(), this.x, arg0.z() * this.w)));
        float float8 = Math.fma(arg0.w(), this.w, Math.fma(-arg0.x(), this.x, Math.fma(-arg0.y(), this.y, -arg0.z() * this.z)));
        return arg1.set(
            Math.fma(float8, float1, Math.fma(float5, float4, Math.fma(float6, float3, -float7 * float2))),
            Math.fma(float8, float2, Math.fma(-float5, float3, Math.fma(float6, float4, float7 * float1))),
            Math.fma(float8, float3, Math.fma(float5, float2, Math.fma(-float6, float1, float7 * float4))),
            Math.fma(float8, float4, Math.fma(-float5, float1, Math.fma(-float6, float2, -float7 * float3)))
        );
    }

    @Override
    public boolean isFinite() {
        return Math.isFinite(this.x) && Math.isFinite(this.y) && Math.isFinite(this.z) && Math.isFinite(this.w);
    }

    @Override
    public boolean equals(Quaternionfc arg0, float arg1) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (!(arg0 instanceof Quaternionfc)) {
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
    public boolean equals(float arg0, float arg1, float arg2, float arg3) {
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(arg0)) {
            return false;
        } else if (Float.floatToIntBits(this.y) != Float.floatToIntBits(arg1)) {
            return false;
        } else {
            return Float.floatToIntBits(this.z) != Float.floatToIntBits(arg2) ? false : Float.floatToIntBits(this.w) == Float.floatToIntBits(arg3);
        }
    }
}
