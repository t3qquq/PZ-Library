// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Matrix3f implements Externalizable, Matrix3fc {
    private static final long serialVersionUID = 1L;
    public float m00;
    public float m01;
    public float m02;
    public float m10;
    public float m11;
    public float m12;
    public float m20;
    public float m21;
    public float m22;

    public Matrix3f() {
        this.m00 = 1.0F;
        this.m11 = 1.0F;
        this.m22 = 1.0F;
    }

    public Matrix3f(Matrix2fc arg0) {
        this.set(arg0);
    }

    public Matrix3f(Matrix3fc arg0) {
        this.set(arg0);
    }

    public Matrix3f(Matrix4fc arg0) {
        this.set(arg0);
    }

    public Matrix3f(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8) {
        this.m00 = arg0;
        this.m01 = arg1;
        this.m02 = arg2;
        this.m10 = arg3;
        this.m11 = arg4;
        this.m12 = arg5;
        this.m20 = arg6;
        this.m21 = arg7;
        this.m22 = arg8;
    }

    public Matrix3f(FloatBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
    }

    public Matrix3f(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2) {
        this.set(arg0, arg1, arg2);
    }

    @Override
    public float m00() {
        return this.m00;
    }

    @Override
    public float m01() {
        return this.m01;
    }

    @Override
    public float m02() {
        return this.m02;
    }

    @Override
    public float m10() {
        return this.m10;
    }

    @Override
    public float m11() {
        return this.m11;
    }

    @Override
    public float m12() {
        return this.m12;
    }

    @Override
    public float m20() {
        return this.m20;
    }

    @Override
    public float m21() {
        return this.m21;
    }

    @Override
    public float m22() {
        return this.m22;
    }

    public Matrix3f m00(float arg0) {
        this.m00 = arg0;
        return this;
    }

    public Matrix3f m01(float arg0) {
        this.m01 = arg0;
        return this;
    }

    public Matrix3f m02(float arg0) {
        this.m02 = arg0;
        return this;
    }

    public Matrix3f m10(float arg0) {
        this.m10 = arg0;
        return this;
    }

    public Matrix3f m11(float arg0) {
        this.m11 = arg0;
        return this;
    }

    public Matrix3f m12(float arg0) {
        this.m12 = arg0;
        return this;
    }

    public Matrix3f m20(float arg0) {
        this.m20 = arg0;
        return this;
    }

    public Matrix3f m21(float arg0) {
        this.m21 = arg0;
        return this;
    }

    public Matrix3f m22(float arg0) {
        this.m22 = arg0;
        return this;
    }

    Matrix3f _m00(float float0) {
        this.m00 = float0;
        return this;
    }

    Matrix3f _m01(float float0) {
        this.m01 = float0;
        return this;
    }

    Matrix3f _m02(float float0) {
        this.m02 = float0;
        return this;
    }

    Matrix3f _m10(float float0) {
        this.m10 = float0;
        return this;
    }

    Matrix3f _m11(float float0) {
        this.m11 = float0;
        return this;
    }

    Matrix3f _m12(float float0) {
        this.m12 = float0;
        return this;
    }

    Matrix3f _m20(float float0) {
        this.m20 = float0;
        return this;
    }

    Matrix3f _m21(float float0) {
        this.m21 = float0;
        return this;
    }

    Matrix3f _m22(float float0) {
        this.m22 = float0;
        return this;
    }

    public Matrix3f set(Matrix3fc arg0) {
        return this._m00(arg0.m00())
            ._m01(arg0.m01())
            ._m02(arg0.m02())
            ._m10(arg0.m10())
            ._m11(arg0.m11())
            ._m12(arg0.m12())
            ._m20(arg0.m20())
            ._m21(arg0.m21())
            ._m22(arg0.m22());
    }

    public Matrix3f setTransposed(Matrix3fc arg0) {
        float float0 = arg0.m01();
        float float1 = arg0.m21();
        float float2 = arg0.m02();
        float float3 = arg0.m12();
        return this._m00(arg0.m00())._m01(arg0.m10())._m02(arg0.m20())._m10(float0)._m11(arg0.m11())._m12(float1)._m20(float2)._m21(float3)._m22(arg0.m22());
    }

    public Matrix3f set(Matrix4x3fc arg0) {
        this.m00 = arg0.m00();
        this.m01 = arg0.m01();
        this.m02 = arg0.m02();
        this.m10 = arg0.m10();
        this.m11 = arg0.m11();
        this.m12 = arg0.m12();
        this.m20 = arg0.m20();
        this.m21 = arg0.m21();
        this.m22 = arg0.m22();
        return this;
    }

    public Matrix3f set(Matrix4fc arg0) {
        this.m00 = arg0.m00();
        this.m01 = arg0.m01();
        this.m02 = arg0.m02();
        this.m10 = arg0.m10();
        this.m11 = arg0.m11();
        this.m12 = arg0.m12();
        this.m20 = arg0.m20();
        this.m21 = arg0.m21();
        this.m22 = arg0.m22();
        return this;
    }

    public Matrix3f set(Matrix2fc arg0) {
        this.m00 = arg0.m00();
        this.m01 = arg0.m01();
        this.m02 = 0.0F;
        this.m10 = arg0.m10();
        this.m11 = arg0.m11();
        this.m12 = 0.0F;
        this.m20 = 0.0F;
        this.m21 = 0.0F;
        this.m22 = 1.0F;
        return this;
    }

    public Matrix3f set(AxisAngle4f arg0) {
        float float0 = arg0.x;
        float float1 = arg0.y;
        float float2 = arg0.z;
        float float3 = arg0.angle;
        float float4 = Math.invsqrt(float0 * float0 + float1 * float1 + float2 * float2);
        float0 *= float4;
        float1 *= float4;
        float2 *= float4;
        float float5 = Math.sin(float3);
        float float6 = Math.cosFromSin(float5, float3);
        float float7 = 1.0F - float6;
        this.m00 = float6 + float0 * float0 * float7;
        this.m11 = float6 + float1 * float1 * float7;
        this.m22 = float6 + float2 * float2 * float7;
        float float8 = float0 * float1 * float7;
        float float9 = float2 * float5;
        this.m10 = float8 - float9;
        this.m01 = float8 + float9;
        float8 = float0 * float2 * float7;
        float9 = float1 * float5;
        this.m20 = float8 + float9;
        this.m02 = float8 - float9;
        float8 = float1 * float2 * float7;
        float9 = float0 * float5;
        this.m21 = float8 - float9;
        this.m12 = float8 + float9;
        return this;
    }

    public Matrix3f set(AxisAngle4d arg0) {
        double double0 = arg0.x;
        double double1 = arg0.y;
        double double2 = arg0.z;
        double double3 = arg0.angle;
        double double4 = Math.invsqrt(double0 * double0 + double1 * double1 + double2 * double2);
        double0 *= double4;
        double1 *= double4;
        double2 *= double4;
        double double5 = Math.sin(double3);
        double double6 = Math.cosFromSin(double5, double3);
        double double7 = 1.0 - double6;
        this.m00 = (float)(double6 + double0 * double0 * double7);
        this.m11 = (float)(double6 + double1 * double1 * double7);
        this.m22 = (float)(double6 + double2 * double2 * double7);
        double double8 = double0 * double1 * double7;
        double double9 = double2 * double5;
        this.m10 = (float)(double8 - double9);
        this.m01 = (float)(double8 + double9);
        double8 = double0 * double2 * double7;
        double9 = double1 * double5;
        this.m20 = (float)(double8 + double9);
        this.m02 = (float)(double8 - double9);
        double8 = double1 * double2 * double7;
        double9 = double0 * double5;
        this.m21 = (float)(double8 - double9);
        this.m12 = (float)(double8 + double9);
        return this;
    }

    public Matrix3f set(Quaternionfc arg0) {
        return this.rotation(arg0);
    }

    public Matrix3f set(Quaterniondc arg0) {
        double double0 = arg0.w() * arg0.w();
        double double1 = arg0.x() * arg0.x();
        double double2 = arg0.y() * arg0.y();
        double double3 = arg0.z() * arg0.z();
        double double4 = arg0.z() * arg0.w();
        double double5 = arg0.x() * arg0.y();
        double double6 = arg0.x() * arg0.z();
        double double7 = arg0.y() * arg0.w();
        double double8 = arg0.y() * arg0.z();
        double double9 = arg0.x() * arg0.w();
        this.m00 = (float)(double0 + double1 - double3 - double2);
        this.m01 = (float)(double5 + double4 + double4 + double5);
        this.m02 = (float)(double6 - double7 + double6 - double7);
        this.m10 = (float)(-double4 + double5 - double4 + double5);
        this.m11 = (float)(double2 - double3 + double0 - double1);
        this.m12 = (float)(double8 + double8 + double9 + double9);
        this.m20 = (float)(double7 + double6 + double6 + double7);
        this.m21 = (float)(double8 + double8 - double9 - double9);
        this.m22 = (float)(double3 - double2 - double1 + double0);
        return this;
    }

    public Matrix3f mul(Matrix3fc arg0) {
        return this.mul(arg0, this);
    }

    @Override
    public Matrix3f mul(Matrix3fc arg0, Matrix3f arg1) {
        float float0 = Math.fma(this.m00, arg0.m00(), Math.fma(this.m10, arg0.m01(), this.m20 * arg0.m02()));
        float float1 = Math.fma(this.m01, arg0.m00(), Math.fma(this.m11, arg0.m01(), this.m21 * arg0.m02()));
        float float2 = Math.fma(this.m02, arg0.m00(), Math.fma(this.m12, arg0.m01(), this.m22 * arg0.m02()));
        float float3 = Math.fma(this.m00, arg0.m10(), Math.fma(this.m10, arg0.m11(), this.m20 * arg0.m12()));
        float float4 = Math.fma(this.m01, arg0.m10(), Math.fma(this.m11, arg0.m11(), this.m21 * arg0.m12()));
        float float5 = Math.fma(this.m02, arg0.m10(), Math.fma(this.m12, arg0.m11(), this.m22 * arg0.m12()));
        float float6 = Math.fma(this.m00, arg0.m20(), Math.fma(this.m10, arg0.m21(), this.m20 * arg0.m22()));
        float float7 = Math.fma(this.m01, arg0.m20(), Math.fma(this.m11, arg0.m21(), this.m21 * arg0.m22()));
        float float8 = Math.fma(this.m02, arg0.m20(), Math.fma(this.m12, arg0.m21(), this.m22 * arg0.m22()));
        arg1.m00 = float0;
        arg1.m01 = float1;
        arg1.m02 = float2;
        arg1.m10 = float3;
        arg1.m11 = float4;
        arg1.m12 = float5;
        arg1.m20 = float6;
        arg1.m21 = float7;
        arg1.m22 = float8;
        return arg1;
    }

    public Matrix3f mulLocal(Matrix3fc arg0) {
        return this.mulLocal(arg0, this);
    }

    @Override
    public Matrix3f mulLocal(Matrix3fc arg0, Matrix3f arg1) {
        float float0 = arg0.m00() * this.m00 + arg0.m10() * this.m01 + arg0.m20() * this.m02;
        float float1 = arg0.m01() * this.m00 + arg0.m11() * this.m01 + arg0.m21() * this.m02;
        float float2 = arg0.m02() * this.m00 + arg0.m12() * this.m01 + arg0.m22() * this.m02;
        float float3 = arg0.m00() * this.m10 + arg0.m10() * this.m11 + arg0.m20() * this.m12;
        float float4 = arg0.m01() * this.m10 + arg0.m11() * this.m11 + arg0.m21() * this.m12;
        float float5 = arg0.m02() * this.m10 + arg0.m12() * this.m11 + arg0.m22() * this.m12;
        float float6 = arg0.m00() * this.m20 + arg0.m10() * this.m21 + arg0.m20() * this.m22;
        float float7 = arg0.m01() * this.m20 + arg0.m11() * this.m21 + arg0.m21() * this.m22;
        float float8 = arg0.m02() * this.m20 + arg0.m12() * this.m21 + arg0.m22() * this.m22;
        arg1.m00 = float0;
        arg1.m01 = float1;
        arg1.m02 = float2;
        arg1.m10 = float3;
        arg1.m11 = float4;
        arg1.m12 = float5;
        arg1.m20 = float6;
        arg1.m21 = float7;
        arg1.m22 = float8;
        return arg1;
    }

    public Matrix3f set(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8) {
        this.m00 = arg0;
        this.m01 = arg1;
        this.m02 = arg2;
        this.m10 = arg3;
        this.m11 = arg4;
        this.m12 = arg5;
        this.m20 = arg6;
        this.m21 = arg7;
        this.m22 = arg8;
        return this;
    }

    public Matrix3f set(float[] floats) {
        MemUtil.INSTANCE.copy(floats, 0, this);
        return this;
    }

    public Matrix3f set(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2) {
        this.m00 = arg0.x();
        this.m01 = arg0.y();
        this.m02 = arg0.z();
        this.m10 = arg1.x();
        this.m11 = arg1.y();
        this.m12 = arg1.z();
        this.m20 = arg2.x();
        this.m21 = arg2.y();
        this.m22 = arg2.z();
        return this;
    }

    @Override
    public float determinant() {
        return (this.m00 * this.m11 - this.m01 * this.m10) * this.m22
            + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21
            + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
    }

    public Matrix3f invert() {
        return this.invert(this);
    }

    @Override
    public Matrix3f invert(Matrix3f arg0) {
        float float0 = Math.fma(this.m00, this.m11, -this.m01 * this.m10);
        float float1 = Math.fma(this.m02, this.m10, -this.m00 * this.m12);
        float float2 = Math.fma(this.m01, this.m12, -this.m02 * this.m11);
        float float3 = Math.fma(float0, this.m22, Math.fma(float1, this.m21, float2 * this.m20));
        float float4 = 1.0F / float3;
        float float5 = Math.fma(this.m11, this.m22, -this.m21 * this.m12) * float4;
        float float6 = Math.fma(this.m21, this.m02, -this.m01 * this.m22) * float4;
        float float7 = float2 * float4;
        float float8 = Math.fma(this.m20, this.m12, -this.m10 * this.m22) * float4;
        float float9 = Math.fma(this.m00, this.m22, -this.m20 * this.m02) * float4;
        float float10 = float1 * float4;
        float float11 = Math.fma(this.m10, this.m21, -this.m20 * this.m11) * float4;
        float float12 = Math.fma(this.m20, this.m01, -this.m00 * this.m21) * float4;
        float float13 = float0 * float4;
        arg0.m00 = float5;
        arg0.m01 = float6;
        arg0.m02 = float7;
        arg0.m10 = float8;
        arg0.m11 = float9;
        arg0.m12 = float10;
        arg0.m20 = float11;
        arg0.m21 = float12;
        arg0.m22 = float13;
        return arg0;
    }

    public Matrix3f transpose() {
        return this.transpose(this);
    }

    @Override
    public Matrix3f transpose(Matrix3f arg0) {
        return arg0.set(this.m00, this.m10, this.m20, this.m01, this.m11, this.m21, this.m02, this.m12, this.m22);
    }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat(" 0.000E0;-");
        String string = this.toString(decimalFormat);
        StringBuffer stringBuffer = new StringBuffer();
        int int0 = Integer.MIN_VALUE;

        for (int int1 = 0; int1 < string.length(); int1++) {
            char char0 = string.charAt(int1);
            if (char0 == 'E') {
                int0 = int1;
            } else {
                if (char0 == ' ' && int0 == int1 - 1) {
                    stringBuffer.append('+');
                    continue;
                }

                if (Character.isDigit(char0) && int0 == int1 - 1) {
                    stringBuffer.append('+');
                }
            }

            stringBuffer.append(char0);
        }

        return stringBuffer.toString();
    }

    public String toString(NumberFormat numberFormat) {
        return Runtime.format(this.m00, numberFormat)
            + " "
            + Runtime.format(this.m10, numberFormat)
            + " "
            + Runtime.format(this.m20, numberFormat)
            + "\n"
            + Runtime.format(this.m01, numberFormat)
            + " "
            + Runtime.format(this.m11, numberFormat)
            + " "
            + Runtime.format(this.m21, numberFormat)
            + "\n"
            + Runtime.format(this.m02, numberFormat)
            + " "
            + Runtime.format(this.m12, numberFormat)
            + " "
            + Runtime.format(this.m22, numberFormat)
            + "\n";
    }

    @Override
    public Matrix3f get(Matrix3f arg0) {
        return arg0.set(this);
    }

    @Override
    public Matrix4f get(Matrix4f arg0) {
        return arg0.set(this);
    }

    @Override
    public AxisAngle4f getRotation(AxisAngle4f arg0) {
        return arg0.set(this);
    }

    @Override
    public Quaternionf getUnnormalizedRotation(Quaternionf arg0) {
        return arg0.setFromUnnormalized(this);
    }

    @Override
    public Quaternionf getNormalizedRotation(Quaternionf arg0) {
        return arg0.setFromNormalized(this);
    }

    @Override
    public Quaterniond getUnnormalizedRotation(Quaterniond arg0) {
        return arg0.setFromUnnormalized(this);
    }

    @Override
    public Quaterniond getNormalizedRotation(Quaterniond arg0) {
        return arg0.setFromNormalized(this);
    }

    @Override
    public FloatBuffer get(FloatBuffer arg0) {
        return this.get(arg0.position(), arg0);
    }

    @Override
    public FloatBuffer get(int arg0, FloatBuffer arg1) {
        MemUtil.INSTANCE.put(this, arg0, arg1);
        return arg1;
    }

    @Override
    public ByteBuffer get(ByteBuffer arg0) {
        return this.get(arg0.position(), arg0);
    }

    @Override
    public ByteBuffer get(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.put(this, arg0, arg1);
        return arg1;
    }

    @Override
    public FloatBuffer get3x4(FloatBuffer arg0) {
        return this.get3x4(arg0.position(), arg0);
    }

    @Override
    public FloatBuffer get3x4(int arg0, FloatBuffer arg1) {
        MemUtil.INSTANCE.put3x4(this, arg0, arg1);
        return arg1;
    }

    @Override
    public ByteBuffer get3x4(ByteBuffer arg0) {
        return this.get3x4(arg0.position(), arg0);
    }

    @Override
    public ByteBuffer get3x4(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.put3x4(this, arg0, arg1);
        return arg1;
    }

    @Override
    public FloatBuffer getTransposed(FloatBuffer arg0) {
        return this.getTransposed(arg0.position(), arg0);
    }

    @Override
    public FloatBuffer getTransposed(int arg0, FloatBuffer arg1) {
        MemUtil.INSTANCE.putTransposed(this, arg0, arg1);
        return arg1;
    }

    @Override
    public ByteBuffer getTransposed(ByteBuffer arg0) {
        return this.getTransposed(arg0.position(), arg0);
    }

    @Override
    public ByteBuffer getTransposed(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.putTransposed(this, arg0, arg1);
        return arg1;
    }

    @Override
    public Matrix3fc getToAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.put(this, arg0);
            return this;
        }
    }

    @Override
    public float[] get(float[] floats, int int0) {
        MemUtil.INSTANCE.copy(this, floats, int0);
        return floats;
    }

    @Override
    public float[] get(float[] floats) {
        return this.get(floats, 0);
    }

    public Matrix3f set(FloatBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Matrix3f set(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Matrix3f setFromAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.get(this, arg0);
            return this;
        }
    }

    public Matrix3f zero() {
        MemUtil.INSTANCE.zero(this);
        return this;
    }

    public Matrix3f identity() {
        MemUtil.INSTANCE.identity(this);
        return this;
    }

    @Override
    public Matrix3f scale(Vector3fc arg0, Matrix3f arg1) {
        return this.scale(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    public Matrix3f scale(Vector3fc arg0) {
        return this.scale(arg0.x(), arg0.y(), arg0.z(), this);
    }

    @Override
    public Matrix3f scale(float arg0, float arg1, float arg2, Matrix3f arg3) {
        arg3.m00 = this.m00 * arg0;
        arg3.m01 = this.m01 * arg0;
        arg3.m02 = this.m02 * arg0;
        arg3.m10 = this.m10 * arg1;
        arg3.m11 = this.m11 * arg1;
        arg3.m12 = this.m12 * arg1;
        arg3.m20 = this.m20 * arg2;
        arg3.m21 = this.m21 * arg2;
        arg3.m22 = this.m22 * arg2;
        return arg3;
    }

    public Matrix3f scale(float arg0, float arg1, float arg2) {
        return this.scale(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix3f scale(float arg0, Matrix3f arg1) {
        return this.scale(arg0, arg0, arg0, arg1);
    }

    public Matrix3f scale(float arg0) {
        return this.scale(arg0, arg0, arg0);
    }

    @Override
    public Matrix3f scaleLocal(float arg0, float arg1, float arg2, Matrix3f arg3) {
        float float0 = arg0 * this.m00;
        float float1 = arg1 * this.m01;
        float float2 = arg2 * this.m02;
        float float3 = arg0 * this.m10;
        float float4 = arg1 * this.m11;
        float float5 = arg2 * this.m12;
        float float6 = arg0 * this.m20;
        float float7 = arg1 * this.m21;
        float float8 = arg2 * this.m22;
        arg3.m00 = float0;
        arg3.m01 = float1;
        arg3.m02 = float2;
        arg3.m10 = float3;
        arg3.m11 = float4;
        arg3.m12 = float5;
        arg3.m20 = float6;
        arg3.m21 = float7;
        arg3.m22 = float8;
        return arg3;
    }

    public Matrix3f scaleLocal(float arg0, float arg1, float arg2) {
        return this.scaleLocal(arg0, arg1, arg2, this);
    }

    public Matrix3f scaling(float arg0) {
        MemUtil.INSTANCE.zero(this);
        this.m00 = arg0;
        this.m11 = arg0;
        this.m22 = arg0;
        return this;
    }

    public Matrix3f scaling(float arg0, float arg1, float arg2) {
        MemUtil.INSTANCE.zero(this);
        this.m00 = arg0;
        this.m11 = arg1;
        this.m22 = arg2;
        return this;
    }

    public Matrix3f scaling(Vector3fc arg0) {
        return this.scaling(arg0.x(), arg0.y(), arg0.z());
    }

    public Matrix3f rotation(float arg0, Vector3fc arg1) {
        return this.rotation(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix3f rotation(AxisAngle4f arg0) {
        return this.rotation(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    public Matrix3f rotation(float arg0, float arg1, float arg2, float arg3) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = 1.0F - float1;
        float float3 = arg1 * arg2;
        float float4 = arg1 * arg3;
        float float5 = arg2 * arg3;
        this.m00 = float1 + arg1 * arg1 * float2;
        this.m10 = float3 * float2 - arg3 * float0;
        this.m20 = float4 * float2 + arg2 * float0;
        this.m01 = float3 * float2 + arg3 * float0;
        this.m11 = float1 + arg2 * arg2 * float2;
        this.m21 = float5 * float2 - arg1 * float0;
        this.m02 = float4 * float2 - arg2 * float0;
        this.m12 = float5 * float2 + arg1 * float0;
        this.m22 = float1 + arg3 * arg3 * float2;
        return this;
    }

    public Matrix3f rotationX(float arg0) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        this.m00 = 1.0F;
        this.m01 = 0.0F;
        this.m02 = 0.0F;
        this.m10 = 0.0F;
        this.m11 = float1;
        this.m12 = float0;
        this.m20 = 0.0F;
        this.m21 = -float0;
        this.m22 = float1;
        return this;
    }

    public Matrix3f rotationY(float arg0) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        this.m00 = float1;
        this.m01 = 0.0F;
        this.m02 = -float0;
        this.m10 = 0.0F;
        this.m11 = 1.0F;
        this.m12 = 0.0F;
        this.m20 = float0;
        this.m21 = 0.0F;
        this.m22 = float1;
        return this;
    }

    public Matrix3f rotationZ(float arg0) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        this.m00 = float1;
        this.m01 = float0;
        this.m02 = 0.0F;
        this.m10 = -float0;
        this.m11 = float1;
        this.m12 = 0.0F;
        this.m20 = 0.0F;
        this.m21 = 0.0F;
        this.m22 = 1.0F;
        return this;
    }

    public Matrix3f rotationXYZ(float arg0, float arg1, float arg2) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = Math.sin(arg1);
        float float3 = Math.cosFromSin(float2, arg1);
        float float4 = Math.sin(arg2);
        float float5 = Math.cosFromSin(float4, arg2);
        float float6 = -float0;
        float float7 = -float2;
        float float8 = -float4;
        float float9 = float6 * float7;
        float float10 = float1 * float7;
        this.m20 = float2;
        this.m21 = float6 * float3;
        this.m22 = float1 * float3;
        this.m00 = float3 * float5;
        this.m01 = float9 * float5 + float1 * float4;
        this.m02 = float10 * float5 + float0 * float4;
        this.m10 = float3 * float8;
        this.m11 = float9 * float8 + float1 * float5;
        this.m12 = float10 * float8 + float0 * float5;
        return this;
    }

    public Matrix3f rotationZYX(float arg0, float arg1, float arg2) {
        float float0 = Math.sin(arg2);
        float float1 = Math.cosFromSin(float0, arg2);
        float float2 = Math.sin(arg1);
        float float3 = Math.cosFromSin(float2, arg1);
        float float4 = Math.sin(arg0);
        float float5 = Math.cosFromSin(float4, arg0);
        float float6 = -float4;
        float float7 = -float2;
        float float8 = -float0;
        float float9 = float5 * float2;
        float float10 = float4 * float2;
        this.m00 = float5 * float3;
        this.m01 = float4 * float3;
        this.m02 = float7;
        this.m10 = float6 * float1 + float9 * float0;
        this.m11 = float5 * float1 + float10 * float0;
        this.m12 = float3 * float0;
        this.m20 = float6 * float8 + float9 * float1;
        this.m21 = float5 * float8 + float10 * float1;
        this.m22 = float3 * float1;
        return this;
    }

    public Matrix3f rotationYXZ(float arg0, float arg1, float arg2) {
        float float0 = Math.sin(arg1);
        float float1 = Math.cosFromSin(float0, arg1);
        float float2 = Math.sin(arg0);
        float float3 = Math.cosFromSin(float2, arg0);
        float float4 = Math.sin(arg2);
        float float5 = Math.cosFromSin(float4, arg2);
        float float6 = -float2;
        float float7 = -float0;
        float float8 = -float4;
        float float9 = float2 * float0;
        float float10 = float3 * float0;
        this.m20 = float2 * float1;
        this.m21 = float7;
        this.m22 = float3 * float1;
        this.m00 = float3 * float5 + float9 * float4;
        this.m01 = float1 * float4;
        this.m02 = float6 * float5 + float10 * float4;
        this.m10 = float3 * float8 + float9 * float5;
        this.m11 = float1 * float5;
        this.m12 = float6 * float8 + float10 * float5;
        return this;
    }

    public Matrix3f rotation(Quaternionfc arg0) {
        float float0 = arg0.w() * arg0.w();
        float float1 = arg0.x() * arg0.x();
        float float2 = arg0.y() * arg0.y();
        float float3 = arg0.z() * arg0.z();
        float float4 = arg0.z() * arg0.w();
        float float5 = float4 + float4;
        float float6 = arg0.x() * arg0.y();
        float float7 = float6 + float6;
        float float8 = arg0.x() * arg0.z();
        float float9 = float8 + float8;
        float float10 = arg0.y() * arg0.w();
        float float11 = float10 + float10;
        float float12 = arg0.y() * arg0.z();
        float float13 = float12 + float12;
        float float14 = arg0.x() * arg0.w();
        float float15 = float14 + float14;
        this.m00 = float0 + float1 - float3 - float2;
        this.m01 = float7 + float5;
        this.m02 = float9 - float11;
        this.m10 = -float5 + float7;
        this.m11 = float2 - float3 + float0 - float1;
        this.m12 = float13 + float15;
        this.m20 = float11 + float9;
        this.m21 = float13 - float15;
        this.m22 = float3 - float2 - float1 + float0;
        return this;
    }

    @Override
    public Vector3f transform(Vector3f arg0) {
        return arg0.mul(this);
    }

    @Override
    public Vector3f transform(Vector3fc arg0, Vector3f arg1) {
        return arg0.mul(this, arg1);
    }

    @Override
    public Vector3f transform(float arg0, float arg1, float arg2, Vector3f arg3) {
        return arg3.set(
            Math.fma(this.m00, arg0, Math.fma(this.m10, arg1, this.m20 * arg2)),
            Math.fma(this.m01, arg0, Math.fma(this.m11, arg1, this.m21 * arg2)),
            Math.fma(this.m02, arg0, Math.fma(this.m12, arg1, this.m22 * arg2))
        );
    }

    @Override
    public Vector3f transformTranspose(Vector3f arg0) {
        return arg0.mulTranspose(this);
    }

    @Override
    public Vector3f transformTranspose(Vector3fc arg0, Vector3f arg1) {
        return arg0.mulTranspose(this, arg1);
    }

    @Override
    public Vector3f transformTranspose(float arg0, float arg1, float arg2, Vector3f arg3) {
        return arg3.set(
            Math.fma(this.m00, arg0, Math.fma(this.m01, arg1, this.m02 * arg2)),
            Math.fma(this.m10, arg0, Math.fma(this.m11, arg1, this.m12 * arg2)),
            Math.fma(this.m20, arg0, Math.fma(this.m21, arg1, this.m22 * arg2))
        );
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        arg0.writeFloat(this.m00);
        arg0.writeFloat(this.m01);
        arg0.writeFloat(this.m02);
        arg0.writeFloat(this.m10);
        arg0.writeFloat(this.m11);
        arg0.writeFloat(this.m12);
        arg0.writeFloat(this.m20);
        arg0.writeFloat(this.m21);
        arg0.writeFloat(this.m22);
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException {
        this.m00 = arg0.readFloat();
        this.m01 = arg0.readFloat();
        this.m02 = arg0.readFloat();
        this.m10 = arg0.readFloat();
        this.m11 = arg0.readFloat();
        this.m12 = arg0.readFloat();
        this.m20 = arg0.readFloat();
        this.m21 = arg0.readFloat();
        this.m22 = arg0.readFloat();
    }

    @Override
    public Matrix3f rotateX(float arg0, Matrix3f arg1) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = -float0;
        float float3 = this.m10 * float1 + this.m20 * float0;
        float float4 = this.m11 * float1 + this.m21 * float0;
        float float5 = this.m12 * float1 + this.m22 * float0;
        arg1.m20 = this.m10 * float2 + this.m20 * float1;
        arg1.m21 = this.m11 * float2 + this.m21 * float1;
        arg1.m22 = this.m12 * float2 + this.m22 * float1;
        arg1.m10 = float3;
        arg1.m11 = float4;
        arg1.m12 = float5;
        arg1.m00 = this.m00;
        arg1.m01 = this.m01;
        arg1.m02 = this.m02;
        return arg1;
    }

    public Matrix3f rotateX(float arg0) {
        return this.rotateX(arg0, this);
    }

    @Override
    public Matrix3f rotateY(float arg0, Matrix3f arg1) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = -float0;
        float float3 = this.m00 * float1 + this.m20 * float2;
        float float4 = this.m01 * float1 + this.m21 * float2;
        float float5 = this.m02 * float1 + this.m22 * float2;
        arg1.m20 = this.m00 * float0 + this.m20 * float1;
        arg1.m21 = this.m01 * float0 + this.m21 * float1;
        arg1.m22 = this.m02 * float0 + this.m22 * float1;
        arg1.m00 = float3;
        arg1.m01 = float4;
        arg1.m02 = float5;
        arg1.m10 = this.m10;
        arg1.m11 = this.m11;
        arg1.m12 = this.m12;
        return arg1;
    }

    public Matrix3f rotateY(float arg0) {
        return this.rotateY(arg0, this);
    }

    @Override
    public Matrix3f rotateZ(float arg0, Matrix3f arg1) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = -float0;
        float float3 = this.m00 * float1 + this.m10 * float0;
        float float4 = this.m01 * float1 + this.m11 * float0;
        float float5 = this.m02 * float1 + this.m12 * float0;
        arg1.m10 = this.m00 * float2 + this.m10 * float1;
        arg1.m11 = this.m01 * float2 + this.m11 * float1;
        arg1.m12 = this.m02 * float2 + this.m12 * float1;
        arg1.m00 = float3;
        arg1.m01 = float4;
        arg1.m02 = float5;
        arg1.m20 = this.m20;
        arg1.m21 = this.m21;
        arg1.m22 = this.m22;
        return arg1;
    }

    public Matrix3f rotateZ(float arg0) {
        return this.rotateZ(arg0, this);
    }

    public Matrix3f rotateXYZ(Vector3f arg0) {
        return this.rotateXYZ(arg0.x, arg0.y, arg0.z);
    }

    public Matrix3f rotateXYZ(float arg0, float arg1, float arg2) {
        return this.rotateXYZ(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix3f rotateXYZ(float arg0, float arg1, float arg2, Matrix3f arg3) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = Math.sin(arg1);
        float float3 = Math.cosFromSin(float2, arg1);
        float float4 = Math.sin(arg2);
        float float5 = Math.cosFromSin(float4, arg2);
        float float6 = -float0;
        float float7 = -float2;
        float float8 = -float4;
        float float9 = this.m10 * float1 + this.m20 * float0;
        float float10 = this.m11 * float1 + this.m21 * float0;
        float float11 = this.m12 * float1 + this.m22 * float0;
        float float12 = this.m10 * float6 + this.m20 * float1;
        float float13 = this.m11 * float6 + this.m21 * float1;
        float float14 = this.m12 * float6 + this.m22 * float1;
        float float15 = this.m00 * float3 + float12 * float7;
        float float16 = this.m01 * float3 + float13 * float7;
        float float17 = this.m02 * float3 + float14 * float7;
        arg3.m20 = this.m00 * float2 + float12 * float3;
        arg3.m21 = this.m01 * float2 + float13 * float3;
        arg3.m22 = this.m02 * float2 + float14 * float3;
        arg3.m00 = float15 * float5 + float9 * float4;
        arg3.m01 = float16 * float5 + float10 * float4;
        arg3.m02 = float17 * float5 + float11 * float4;
        arg3.m10 = float15 * float8 + float9 * float5;
        arg3.m11 = float16 * float8 + float10 * float5;
        arg3.m12 = float17 * float8 + float11 * float5;
        return arg3;
    }

    public Matrix3f rotateZYX(Vector3f arg0) {
        return this.rotateZYX(arg0.z, arg0.y, arg0.x);
    }

    public Matrix3f rotateZYX(float arg0, float arg1, float arg2) {
        return this.rotateZYX(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix3f rotateZYX(float arg0, float arg1, float arg2, Matrix3f arg3) {
        float float0 = Math.sin(arg2);
        float float1 = Math.cosFromSin(float0, arg2);
        float float2 = Math.sin(arg1);
        float float3 = Math.cosFromSin(float2, arg1);
        float float4 = Math.sin(arg0);
        float float5 = Math.cosFromSin(float4, arg0);
        float float6 = -float4;
        float float7 = -float2;
        float float8 = -float0;
        float float9 = this.m00 * float5 + this.m10 * float4;
        float float10 = this.m01 * float5 + this.m11 * float4;
        float float11 = this.m02 * float5 + this.m12 * float4;
        float float12 = this.m00 * float6 + this.m10 * float5;
        float float13 = this.m01 * float6 + this.m11 * float5;
        float float14 = this.m02 * float6 + this.m12 * float5;
        float float15 = float9 * float2 + this.m20 * float3;
        float float16 = float10 * float2 + this.m21 * float3;
        float float17 = float11 * float2 + this.m22 * float3;
        arg3.m00 = float9 * float3 + this.m20 * float7;
        arg3.m01 = float10 * float3 + this.m21 * float7;
        arg3.m02 = float11 * float3 + this.m22 * float7;
        arg3.m10 = float12 * float1 + float15 * float0;
        arg3.m11 = float13 * float1 + float16 * float0;
        arg3.m12 = float14 * float1 + float17 * float0;
        arg3.m20 = float12 * float8 + float15 * float1;
        arg3.m21 = float13 * float8 + float16 * float1;
        arg3.m22 = float14 * float8 + float17 * float1;
        return arg3;
    }

    public Matrix3f rotateYXZ(Vector3f arg0) {
        return this.rotateYXZ(arg0.y, arg0.x, arg0.z);
    }

    public Matrix3f rotateYXZ(float arg0, float arg1, float arg2) {
        return this.rotateYXZ(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix3f rotateYXZ(float arg0, float arg1, float arg2, Matrix3f arg3) {
        float float0 = Math.sin(arg1);
        float float1 = Math.cosFromSin(float0, arg1);
        float float2 = Math.sin(arg0);
        float float3 = Math.cosFromSin(float2, arg0);
        float float4 = Math.sin(arg2);
        float float5 = Math.cosFromSin(float4, arg2);
        float float6 = -float2;
        float float7 = -float0;
        float float8 = -float4;
        float float9 = this.m00 * float2 + this.m20 * float3;
        float float10 = this.m01 * float2 + this.m21 * float3;
        float float11 = this.m02 * float2 + this.m22 * float3;
        float float12 = this.m00 * float3 + this.m20 * float6;
        float float13 = this.m01 * float3 + this.m21 * float6;
        float float14 = this.m02 * float3 + this.m22 * float6;
        float float15 = this.m10 * float1 + float9 * float0;
        float float16 = this.m11 * float1 + float10 * float0;
        float float17 = this.m12 * float1 + float11 * float0;
        arg3.m20 = this.m10 * float7 + float9 * float1;
        arg3.m21 = this.m11 * float7 + float10 * float1;
        arg3.m22 = this.m12 * float7 + float11 * float1;
        arg3.m00 = float12 * float5 + float15 * float4;
        arg3.m01 = float13 * float5 + float16 * float4;
        arg3.m02 = float14 * float5 + float17 * float4;
        arg3.m10 = float12 * float8 + float15 * float5;
        arg3.m11 = float13 * float8 + float16 * float5;
        arg3.m12 = float14 * float8 + float17 * float5;
        return arg3;
    }

    public Matrix3f rotate(float arg0, float arg1, float arg2, float arg3) {
        return this.rotate(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix3f rotate(float arg0, float arg1, float arg2, float arg3, Matrix3f arg4) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = 1.0F - float1;
        float float3 = arg1 * arg1;
        float float4 = arg1 * arg2;
        float float5 = arg1 * arg3;
        float float6 = arg2 * arg2;
        float float7 = arg2 * arg3;
        float float8 = arg3 * arg3;
        float float9 = float3 * float2 + float1;
        float float10 = float4 * float2 + arg3 * float0;
        float float11 = float5 * float2 - arg2 * float0;
        float float12 = float4 * float2 - arg3 * float0;
        float float13 = float6 * float2 + float1;
        float float14 = float7 * float2 + arg1 * float0;
        float float15 = float5 * float2 + arg2 * float0;
        float float16 = float7 * float2 - arg1 * float0;
        float float17 = float8 * float2 + float1;
        float float18 = this.m00 * float9 + this.m10 * float10 + this.m20 * float11;
        float float19 = this.m01 * float9 + this.m11 * float10 + this.m21 * float11;
        float float20 = this.m02 * float9 + this.m12 * float10 + this.m22 * float11;
        float float21 = this.m00 * float12 + this.m10 * float13 + this.m20 * float14;
        float float22 = this.m01 * float12 + this.m11 * float13 + this.m21 * float14;
        float float23 = this.m02 * float12 + this.m12 * float13 + this.m22 * float14;
        arg4.m20 = this.m00 * float15 + this.m10 * float16 + this.m20 * float17;
        arg4.m21 = this.m01 * float15 + this.m11 * float16 + this.m21 * float17;
        arg4.m22 = this.m02 * float15 + this.m12 * float16 + this.m22 * float17;
        arg4.m00 = float18;
        arg4.m01 = float19;
        arg4.m02 = float20;
        arg4.m10 = float21;
        arg4.m11 = float22;
        arg4.m12 = float23;
        return arg4;
    }

    @Override
    public Matrix3f rotateLocal(float arg0, float arg1, float arg2, float arg3, Matrix3f arg4) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = 1.0F - float1;
        float float3 = arg1 * arg1;
        float float4 = arg1 * arg2;
        float float5 = arg1 * arg3;
        float float6 = arg2 * arg2;
        float float7 = arg2 * arg3;
        float float8 = arg3 * arg3;
        float float9 = float3 * float2 + float1;
        float float10 = float4 * float2 + arg3 * float0;
        float float11 = float5 * float2 - arg2 * float0;
        float float12 = float4 * float2 - arg3 * float0;
        float float13 = float6 * float2 + float1;
        float float14 = float7 * float2 + arg1 * float0;
        float float15 = float5 * float2 + arg2 * float0;
        float float16 = float7 * float2 - arg1 * float0;
        float float17 = float8 * float2 + float1;
        float float18 = float9 * this.m00 + float12 * this.m01 + float15 * this.m02;
        float float19 = float10 * this.m00 + float13 * this.m01 + float16 * this.m02;
        float float20 = float11 * this.m00 + float14 * this.m01 + float17 * this.m02;
        float float21 = float9 * this.m10 + float12 * this.m11 + float15 * this.m12;
        float float22 = float10 * this.m10 + float13 * this.m11 + float16 * this.m12;
        float float23 = float11 * this.m10 + float14 * this.m11 + float17 * this.m12;
        float float24 = float9 * this.m20 + float12 * this.m21 + float15 * this.m22;
        float float25 = float10 * this.m20 + float13 * this.m21 + float16 * this.m22;
        float float26 = float11 * this.m20 + float14 * this.m21 + float17 * this.m22;
        arg4.m00 = float18;
        arg4.m01 = float19;
        arg4.m02 = float20;
        arg4.m10 = float21;
        arg4.m11 = float22;
        arg4.m12 = float23;
        arg4.m20 = float24;
        arg4.m21 = float25;
        arg4.m22 = float26;
        return arg4;
    }

    public Matrix3f rotateLocal(float arg0, float arg1, float arg2, float arg3) {
        return this.rotateLocal(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix3f rotateLocalX(float arg0, Matrix3f arg1) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = float1 * this.m01 - float0 * this.m02;
        float float3 = float0 * this.m01 + float1 * this.m02;
        float float4 = float1 * this.m11 - float0 * this.m12;
        float float5 = float0 * this.m11 + float1 * this.m12;
        float float6 = float1 * this.m21 - float0 * this.m22;
        float float7 = float0 * this.m21 + float1 * this.m22;
        arg1.m00 = this.m00;
        arg1.m01 = float2;
        arg1.m02 = float3;
        arg1.m10 = this.m10;
        arg1.m11 = float4;
        arg1.m12 = float5;
        arg1.m20 = this.m20;
        arg1.m21 = float6;
        arg1.m22 = float7;
        return arg1;
    }

    public Matrix3f rotateLocalX(float arg0) {
        return this.rotateLocalX(arg0, this);
    }

    @Override
    public Matrix3f rotateLocalY(float arg0, Matrix3f arg1) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = float1 * this.m00 + float0 * this.m02;
        float float3 = -float0 * this.m00 + float1 * this.m02;
        float float4 = float1 * this.m10 + float0 * this.m12;
        float float5 = -float0 * this.m10 + float1 * this.m12;
        float float6 = float1 * this.m20 + float0 * this.m22;
        float float7 = -float0 * this.m20 + float1 * this.m22;
        arg1.m00 = float2;
        arg1.m01 = this.m01;
        arg1.m02 = float3;
        arg1.m10 = float4;
        arg1.m11 = this.m11;
        arg1.m12 = float5;
        arg1.m20 = float6;
        arg1.m21 = this.m21;
        arg1.m22 = float7;
        return arg1;
    }

    public Matrix3f rotateLocalY(float arg0) {
        return this.rotateLocalY(arg0, this);
    }

    @Override
    public Matrix3f rotateLocalZ(float arg0, Matrix3f arg1) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = float1 * this.m00 - float0 * this.m01;
        float float3 = float0 * this.m00 + float1 * this.m01;
        float float4 = float1 * this.m10 - float0 * this.m11;
        float float5 = float0 * this.m10 + float1 * this.m11;
        float float6 = float1 * this.m20 - float0 * this.m21;
        float float7 = float0 * this.m20 + float1 * this.m21;
        arg1.m00 = float2;
        arg1.m01 = float3;
        arg1.m02 = this.m02;
        arg1.m10 = float4;
        arg1.m11 = float5;
        arg1.m12 = this.m12;
        arg1.m20 = float6;
        arg1.m21 = float7;
        arg1.m22 = this.m22;
        return arg1;
    }

    public Matrix3f rotateLocalZ(float arg0) {
        return this.rotateLocalZ(arg0, this);
    }

    public Matrix3f rotate(Quaternionfc arg0) {
        return this.rotate(arg0, this);
    }

    @Override
    public Matrix3f rotate(Quaternionfc arg0, Matrix3f arg1) {
        float float0 = arg0.w() * arg0.w();
        float float1 = arg0.x() * arg0.x();
        float float2 = arg0.y() * arg0.y();
        float float3 = arg0.z() * arg0.z();
        float float4 = arg0.z() * arg0.w();
        float float5 = float4 + float4;
        float float6 = arg0.x() * arg0.y();
        float float7 = float6 + float6;
        float float8 = arg0.x() * arg0.z();
        float float9 = float8 + float8;
        float float10 = arg0.y() * arg0.w();
        float float11 = float10 + float10;
        float float12 = arg0.y() * arg0.z();
        float float13 = float12 + float12;
        float float14 = arg0.x() * arg0.w();
        float float15 = float14 + float14;
        float float16 = float0 + float1 - float3 - float2;
        float float17 = float7 + float5;
        float float18 = float9 - float11;
        float float19 = float7 - float5;
        float float20 = float2 - float3 + float0 - float1;
        float float21 = float13 + float15;
        float float22 = float11 + float9;
        float float23 = float13 - float15;
        float float24 = float3 - float2 - float1 + float0;
        float float25 = this.m00 * float16 + this.m10 * float17 + this.m20 * float18;
        float float26 = this.m01 * float16 + this.m11 * float17 + this.m21 * float18;
        float float27 = this.m02 * float16 + this.m12 * float17 + this.m22 * float18;
        float float28 = this.m00 * float19 + this.m10 * float20 + this.m20 * float21;
        float float29 = this.m01 * float19 + this.m11 * float20 + this.m21 * float21;
        float float30 = this.m02 * float19 + this.m12 * float20 + this.m22 * float21;
        arg1.m20 = this.m00 * float22 + this.m10 * float23 + this.m20 * float24;
        arg1.m21 = this.m01 * float22 + this.m11 * float23 + this.m21 * float24;
        arg1.m22 = this.m02 * float22 + this.m12 * float23 + this.m22 * float24;
        arg1.m00 = float25;
        arg1.m01 = float26;
        arg1.m02 = float27;
        arg1.m10 = float28;
        arg1.m11 = float29;
        arg1.m12 = float30;
        return arg1;
    }

    @Override
    public Matrix3f rotateLocal(Quaternionfc arg0, Matrix3f arg1) {
        float float0 = arg0.w() * arg0.w();
        float float1 = arg0.x() * arg0.x();
        float float2 = arg0.y() * arg0.y();
        float float3 = arg0.z() * arg0.z();
        float float4 = arg0.z() * arg0.w();
        float float5 = float4 + float4;
        float float6 = arg0.x() * arg0.y();
        float float7 = float6 + float6;
        float float8 = arg0.x() * arg0.z();
        float float9 = float8 + float8;
        float float10 = arg0.y() * arg0.w();
        float float11 = float10 + float10;
        float float12 = arg0.y() * arg0.z();
        float float13 = float12 + float12;
        float float14 = arg0.x() * arg0.w();
        float float15 = float14 + float14;
        float float16 = float0 + float1 - float3 - float2;
        float float17 = float7 + float5;
        float float18 = float9 - float11;
        float float19 = float7 - float5;
        float float20 = float2 - float3 + float0 - float1;
        float float21 = float13 + float15;
        float float22 = float11 + float9;
        float float23 = float13 - float15;
        float float24 = float3 - float2 - float1 + float0;
        float float25 = float16 * this.m00 + float19 * this.m01 + float22 * this.m02;
        float float26 = float17 * this.m00 + float20 * this.m01 + float23 * this.m02;
        float float27 = float18 * this.m00 + float21 * this.m01 + float24 * this.m02;
        float float28 = float16 * this.m10 + float19 * this.m11 + float22 * this.m12;
        float float29 = float17 * this.m10 + float20 * this.m11 + float23 * this.m12;
        float float30 = float18 * this.m10 + float21 * this.m11 + float24 * this.m12;
        float float31 = float16 * this.m20 + float19 * this.m21 + float22 * this.m22;
        float float32 = float17 * this.m20 + float20 * this.m21 + float23 * this.m22;
        float float33 = float18 * this.m20 + float21 * this.m21 + float24 * this.m22;
        arg1.m00 = float25;
        arg1.m01 = float26;
        arg1.m02 = float27;
        arg1.m10 = float28;
        arg1.m11 = float29;
        arg1.m12 = float30;
        arg1.m20 = float31;
        arg1.m21 = float32;
        arg1.m22 = float33;
        return arg1;
    }

    public Matrix3f rotateLocal(Quaternionfc arg0) {
        return this.rotateLocal(arg0, this);
    }

    public Matrix3f rotate(AxisAngle4f arg0) {
        return this.rotate(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    @Override
    public Matrix3f rotate(AxisAngle4f arg0, Matrix3f arg1) {
        return this.rotate(arg0.angle, arg0.x, arg0.y, arg0.z, arg1);
    }

    public Matrix3f rotate(float arg0, Vector3fc arg1) {
        return this.rotate(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    @Override
    public Matrix3f rotate(float arg0, Vector3fc arg1, Matrix3f arg2) {
        return this.rotate(arg0, arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    public Matrix3f lookAlong(Vector3fc arg0, Vector3fc arg1) {
        return this.lookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), this);
    }

    @Override
    public Matrix3f lookAlong(Vector3fc arg0, Vector3fc arg1, Matrix3f arg2) {
        return this.lookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    @Override
    public Matrix3f lookAlong(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix3f arg6) {
        float float0 = Math.invsqrt(arg0 * arg0 + arg1 * arg1 + arg2 * arg2);
        arg0 *= -float0;
        arg1 *= -float0;
        arg2 *= -float0;
        float float1 = arg4 * arg2 - arg5 * arg1;
        float float2 = arg5 * arg0 - arg3 * arg2;
        float float3 = arg3 * arg1 - arg4 * arg0;
        float float4 = Math.invsqrt(float1 * float1 + float2 * float2 + float3 * float3);
        float1 *= float4;
        float2 *= float4;
        float3 *= float4;
        float float5 = arg1 * float3 - arg2 * float2;
        float float6 = arg2 * float1 - arg0 * float3;
        float float7 = arg0 * float2 - arg1 * float1;
        float float8 = this.m00 * float1 + this.m10 * float5 + this.m20 * arg0;
        float float9 = this.m01 * float1 + this.m11 * float5 + this.m21 * arg0;
        float float10 = this.m02 * float1 + this.m12 * float5 + this.m22 * arg0;
        float float11 = this.m00 * float2 + this.m10 * float6 + this.m20 * arg1;
        float float12 = this.m01 * float2 + this.m11 * float6 + this.m21 * arg1;
        float float13 = this.m02 * float2 + this.m12 * float6 + this.m22 * arg1;
        arg6.m20 = this.m00 * float3 + this.m10 * float7 + this.m20 * arg2;
        arg6.m21 = this.m01 * float3 + this.m11 * float7 + this.m21 * arg2;
        arg6.m22 = this.m02 * float3 + this.m12 * float7 + this.m22 * arg2;
        arg6.m00 = float8;
        arg6.m01 = float9;
        arg6.m02 = float10;
        arg6.m10 = float11;
        arg6.m11 = float12;
        arg6.m12 = float13;
        return arg6;
    }

    public Matrix3f lookAlong(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.lookAlong(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    public Matrix3f setLookAlong(Vector3fc arg0, Vector3fc arg1) {
        return this.setLookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix3f setLookAlong(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        float float0 = Math.invsqrt(arg0 * arg0 + arg1 * arg1 + arg2 * arg2);
        arg0 *= -float0;
        arg1 *= -float0;
        arg2 *= -float0;
        float float1 = arg4 * arg2 - arg5 * arg1;
        float float2 = arg5 * arg0 - arg3 * arg2;
        float float3 = arg3 * arg1 - arg4 * arg0;
        float float4 = Math.invsqrt(float1 * float1 + float2 * float2 + float3 * float3);
        float1 *= float4;
        float2 *= float4;
        float3 *= float4;
        float float5 = arg1 * float3 - arg2 * float2;
        float float6 = arg2 * float1 - arg0 * float3;
        float float7 = arg0 * float2 - arg1 * float1;
        this.m00 = float1;
        this.m01 = float5;
        this.m02 = arg0;
        this.m10 = float2;
        this.m11 = float6;
        this.m12 = arg1;
        this.m20 = float3;
        this.m21 = float7;
        this.m22 = arg2;
        return this;
    }

    @Override
    public Vector3f getRow(int arg0, Vector3f arg1) throws IndexOutOfBoundsException {
        switch (arg0) {
            case 0:
                return arg1.set(this.m00, this.m10, this.m20);
            case 1:
                return arg1.set(this.m01, this.m11, this.m21);
            case 2:
                return arg1.set(this.m02, this.m12, this.m22);
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    public Matrix3f setRow(int arg0, Vector3fc arg1) throws IndexOutOfBoundsException {
        return this.setRow(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix3f setRow(int arg0, float arg1, float arg2, float arg3) throws IndexOutOfBoundsException {
        switch (arg0) {
            case 0:
                this.m00 = arg1;
                this.m10 = arg2;
                this.m20 = arg3;
                break;
            case 1:
                this.m01 = arg1;
                this.m11 = arg2;
                this.m21 = arg3;
                break;
            case 2:
                this.m02 = arg1;
                this.m12 = arg2;
                this.m22 = arg3;
                break;
            default:
                throw new IndexOutOfBoundsException();
        }

        return this;
    }

    @Override
    public Vector3f getColumn(int arg0, Vector3f arg1) throws IndexOutOfBoundsException {
        switch (arg0) {
            case 0:
                return arg1.set(this.m00, this.m01, this.m02);
            case 1:
                return arg1.set(this.m10, this.m11, this.m12);
            case 2:
                return arg1.set(this.m20, this.m21, this.m22);
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    public Matrix3f setColumn(int arg0, Vector3fc arg1) throws IndexOutOfBoundsException {
        return this.setColumn(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix3f setColumn(int arg0, float arg1, float arg2, float arg3) throws IndexOutOfBoundsException {
        switch (arg0) {
            case 0:
                this.m00 = arg1;
                this.m01 = arg2;
                this.m02 = arg3;
                break;
            case 1:
                this.m10 = arg1;
                this.m11 = arg2;
                this.m12 = arg3;
                break;
            case 2:
                this.m20 = arg1;
                this.m21 = arg2;
                this.m22 = arg3;
                break;
            default:
                throw new IndexOutOfBoundsException();
        }

        return this;
    }

    @Override
    public float get(int arg0, int arg1) {
        return MemUtil.INSTANCE.get(this, arg0, arg1);
    }

    public Matrix3f set(int arg0, int arg1, float arg2) {
        return MemUtil.INSTANCE.set(this, arg0, arg1, arg2);
    }

    @Override
    public float getRowColumn(int arg0, int arg1) {
        return MemUtil.INSTANCE.get(this, arg1, arg0);
    }

    public Matrix3f setRowColumn(int arg0, int arg1, float arg2) {
        return MemUtil.INSTANCE.set(this, arg1, arg0, arg2);
    }

    public Matrix3f normal() {
        return this.normal(this);
    }

    @Override
    public Matrix3f normal(Matrix3f arg0) {
        float float0 = this.m00 * this.m11;
        float float1 = this.m01 * this.m10;
        float float2 = this.m02 * this.m10;
        float float3 = this.m00 * this.m12;
        float float4 = this.m01 * this.m12;
        float float5 = this.m02 * this.m11;
        float float6 = (float0 - float1) * this.m22 + (float2 - float3) * this.m21 + (float4 - float5) * this.m20;
        float float7 = 1.0F / float6;
        float float8 = (this.m11 * this.m22 - this.m21 * this.m12) * float7;
        float float9 = (this.m20 * this.m12 - this.m10 * this.m22) * float7;
        float float10 = (this.m10 * this.m21 - this.m20 * this.m11) * float7;
        float float11 = (this.m21 * this.m02 - this.m01 * this.m22) * float7;
        float float12 = (this.m00 * this.m22 - this.m20 * this.m02) * float7;
        float float13 = (this.m20 * this.m01 - this.m00 * this.m21) * float7;
        float float14 = (float4 - float5) * float7;
        float float15 = (float2 - float3) * float7;
        float float16 = (float0 - float1) * float7;
        arg0.m00 = float8;
        arg0.m01 = float9;
        arg0.m02 = float10;
        arg0.m10 = float11;
        arg0.m11 = float12;
        arg0.m12 = float13;
        arg0.m20 = float14;
        arg0.m21 = float15;
        arg0.m22 = float16;
        return arg0;
    }

    public Matrix3f cofactor() {
        return this.cofactor(this);
    }

    @Override
    public Matrix3f cofactor(Matrix3f arg0) {
        float float0 = this.m11 * this.m22 - this.m21 * this.m12;
        float float1 = this.m20 * this.m12 - this.m10 * this.m22;
        float float2 = this.m10 * this.m21 - this.m20 * this.m11;
        float float3 = this.m21 * this.m02 - this.m01 * this.m22;
        float float4 = this.m00 * this.m22 - this.m20 * this.m02;
        float float5 = this.m20 * this.m01 - this.m00 * this.m21;
        float float6 = this.m01 * this.m12 - this.m11 * this.m02;
        float float7 = this.m02 * this.m10 - this.m12 * this.m00;
        float float8 = this.m00 * this.m11 - this.m10 * this.m01;
        arg0.m00 = float0;
        arg0.m01 = float1;
        arg0.m02 = float2;
        arg0.m10 = float3;
        arg0.m11 = float4;
        arg0.m12 = float5;
        arg0.m20 = float6;
        arg0.m21 = float7;
        arg0.m22 = float8;
        return arg0;
    }

    @Override
    public Vector3f getScale(Vector3f arg0) {
        return arg0.set(
            Math.sqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02),
            Math.sqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12),
            Math.sqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22)
        );
    }

    @Override
    public Vector3f positiveZ(Vector3f arg0) {
        arg0.x = this.m10 * this.m21 - this.m11 * this.m20;
        arg0.y = this.m20 * this.m01 - this.m21 * this.m00;
        arg0.z = this.m00 * this.m11 - this.m01 * this.m10;
        return arg0.normalize(arg0);
    }

    @Override
    public Vector3f normalizedPositiveZ(Vector3f arg0) {
        arg0.x = this.m02;
        arg0.y = this.m12;
        arg0.z = this.m22;
        return arg0;
    }

    @Override
    public Vector3f positiveX(Vector3f arg0) {
        arg0.x = this.m11 * this.m22 - this.m12 * this.m21;
        arg0.y = this.m02 * this.m21 - this.m01 * this.m22;
        arg0.z = this.m01 * this.m12 - this.m02 * this.m11;
        return arg0.normalize(arg0);
    }

    @Override
    public Vector3f normalizedPositiveX(Vector3f arg0) {
        arg0.x = this.m00;
        arg0.y = this.m10;
        arg0.z = this.m20;
        return arg0;
    }

    @Override
    public Vector3f positiveY(Vector3f arg0) {
        arg0.x = this.m12 * this.m20 - this.m10 * this.m22;
        arg0.y = this.m00 * this.m22 - this.m02 * this.m20;
        arg0.z = this.m02 * this.m10 - this.m00 * this.m12;
        return arg0.normalize(arg0);
    }

    @Override
    public Vector3f normalizedPositiveY(Vector3f arg0) {
        arg0.x = this.m01;
        arg0.y = this.m11;
        arg0.z = this.m21;
        return arg0;
    }

    @Override
    public int hashCode() {
        int int0 = 1;
        int0 = 31 * int0 + Float.floatToIntBits(this.m00);
        int0 = 31 * int0 + Float.floatToIntBits(this.m01);
        int0 = 31 * int0 + Float.floatToIntBits(this.m02);
        int0 = 31 * int0 + Float.floatToIntBits(this.m10);
        int0 = 31 * int0 + Float.floatToIntBits(this.m11);
        int0 = 31 * int0 + Float.floatToIntBits(this.m12);
        int0 = 31 * int0 + Float.floatToIntBits(this.m20);
        int0 = 31 * int0 + Float.floatToIntBits(this.m21);
        return 31 * int0 + Float.floatToIntBits(this.m22);
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
            Matrix3f matrix3f = (Matrix3f)arg0;
            if (Float.floatToIntBits(this.m00) != Float.floatToIntBits(matrix3f.m00)) {
                return false;
            } else if (Float.floatToIntBits(this.m01) != Float.floatToIntBits(matrix3f.m01)) {
                return false;
            } else if (Float.floatToIntBits(this.m02) != Float.floatToIntBits(matrix3f.m02)) {
                return false;
            } else if (Float.floatToIntBits(this.m10) != Float.floatToIntBits(matrix3f.m10)) {
                return false;
            } else if (Float.floatToIntBits(this.m11) != Float.floatToIntBits(matrix3f.m11)) {
                return false;
            } else if (Float.floatToIntBits(this.m12) != Float.floatToIntBits(matrix3f.m12)) {
                return false;
            } else if (Float.floatToIntBits(this.m20) != Float.floatToIntBits(matrix3f.m20)) {
                return false;
            } else {
                return Float.floatToIntBits(this.m21) != Float.floatToIntBits(matrix3f.m21)
                    ? false
                    : Float.floatToIntBits(this.m22) == Float.floatToIntBits(matrix3f.m22);
            }
        }
    }

    @Override
    public boolean equals(Matrix3fc arg0, float arg1) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (!(arg0 instanceof Matrix3f)) {
            return false;
        } else if (!Runtime.equals(this.m00, arg0.m00(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m01, arg0.m01(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m02, arg0.m02(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m10, arg0.m10(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m11, arg0.m11(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m12, arg0.m12(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m20, arg0.m20(), arg1)) {
            return false;
        } else {
            return !Runtime.equals(this.m21, arg0.m21(), arg1) ? false : Runtime.equals(this.m22, arg0.m22(), arg1);
        }
    }

    public Matrix3f swap(Matrix3f arg0) {
        MemUtil.INSTANCE.swap(this, arg0);
        return this;
    }

    public Matrix3f add(Matrix3fc arg0) {
        return this.add(arg0, this);
    }

    @Override
    public Matrix3f add(Matrix3fc arg0, Matrix3f arg1) {
        arg1.m00 = this.m00 + arg0.m00();
        arg1.m01 = this.m01 + arg0.m01();
        arg1.m02 = this.m02 + arg0.m02();
        arg1.m10 = this.m10 + arg0.m10();
        arg1.m11 = this.m11 + arg0.m11();
        arg1.m12 = this.m12 + arg0.m12();
        arg1.m20 = this.m20 + arg0.m20();
        arg1.m21 = this.m21 + arg0.m21();
        arg1.m22 = this.m22 + arg0.m22();
        return arg1;
    }

    public Matrix3f sub(Matrix3fc arg0) {
        return this.sub(arg0, this);
    }

    @Override
    public Matrix3f sub(Matrix3fc arg0, Matrix3f arg1) {
        arg1.m00 = this.m00 - arg0.m00();
        arg1.m01 = this.m01 - arg0.m01();
        arg1.m02 = this.m02 - arg0.m02();
        arg1.m10 = this.m10 - arg0.m10();
        arg1.m11 = this.m11 - arg0.m11();
        arg1.m12 = this.m12 - arg0.m12();
        arg1.m20 = this.m20 - arg0.m20();
        arg1.m21 = this.m21 - arg0.m21();
        arg1.m22 = this.m22 - arg0.m22();
        return arg1;
    }

    public Matrix3f mulComponentWise(Matrix3fc arg0) {
        return this.mulComponentWise(arg0, this);
    }

    @Override
    public Matrix3f mulComponentWise(Matrix3fc arg0, Matrix3f arg1) {
        arg1.m00 = this.m00 * arg0.m00();
        arg1.m01 = this.m01 * arg0.m01();
        arg1.m02 = this.m02 * arg0.m02();
        arg1.m10 = this.m10 * arg0.m10();
        arg1.m11 = this.m11 * arg0.m11();
        arg1.m12 = this.m12 * arg0.m12();
        arg1.m20 = this.m20 * arg0.m20();
        arg1.m21 = this.m21 * arg0.m21();
        arg1.m22 = this.m22 * arg0.m22();
        return arg1;
    }

    public Matrix3f setSkewSymmetric(float arg0, float arg1, float arg2) {
        this.m00 = this.m11 = this.m22 = 0.0F;
        this.m01 = -arg0;
        this.m02 = arg1;
        this.m10 = arg0;
        this.m12 = -arg2;
        this.m20 = -arg1;
        this.m21 = arg2;
        return this;
    }

    public Matrix3f lerp(Matrix3fc arg0, float arg1) {
        return this.lerp(arg0, arg1, this);
    }

    @Override
    public Matrix3f lerp(Matrix3fc arg0, float arg1, Matrix3f arg2) {
        arg2.m00 = Math.fma(arg0.m00() - this.m00, arg1, this.m00);
        arg2.m01 = Math.fma(arg0.m01() - this.m01, arg1, this.m01);
        arg2.m02 = Math.fma(arg0.m02() - this.m02, arg1, this.m02);
        arg2.m10 = Math.fma(arg0.m10() - this.m10, arg1, this.m10);
        arg2.m11 = Math.fma(arg0.m11() - this.m11, arg1, this.m11);
        arg2.m12 = Math.fma(arg0.m12() - this.m12, arg1, this.m12);
        arg2.m20 = Math.fma(arg0.m20() - this.m20, arg1, this.m20);
        arg2.m21 = Math.fma(arg0.m21() - this.m21, arg1, this.m21);
        arg2.m22 = Math.fma(arg0.m22() - this.m22, arg1, this.m22);
        return arg2;
    }

    @Override
    public Matrix3f rotateTowards(Vector3fc arg0, Vector3fc arg1, Matrix3f arg2) {
        return this.rotateTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    public Matrix3f rotateTowards(Vector3fc arg0, Vector3fc arg1) {
        return this.rotateTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), this);
    }

    public Matrix3f rotateTowards(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.rotateTowards(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    @Override
    public Matrix3f rotateTowards(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix3f arg6) {
        float float0 = Math.invsqrt(arg0 * arg0 + arg1 * arg1 + arg2 * arg2);
        float float1 = arg0 * float0;
        float float2 = arg1 * float0;
        float float3 = arg2 * float0;
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
        float float11 = this.m00 * float4 + this.m10 * float5 + this.m20 * float6;
        float float12 = this.m01 * float4 + this.m11 * float5 + this.m21 * float6;
        float float13 = this.m02 * float4 + this.m12 * float5 + this.m22 * float6;
        float float14 = this.m00 * float8 + this.m10 * float9 + this.m20 * float10;
        float float15 = this.m01 * float8 + this.m11 * float9 + this.m21 * float10;
        float float16 = this.m02 * float8 + this.m12 * float9 + this.m22 * float10;
        arg6.m20 = this.m00 * float1 + this.m10 * float2 + this.m20 * float3;
        arg6.m21 = this.m01 * float1 + this.m11 * float2 + this.m21 * float3;
        arg6.m22 = this.m02 * float1 + this.m12 * float2 + this.m22 * float3;
        arg6.m00 = float11;
        arg6.m01 = float12;
        arg6.m02 = float13;
        arg6.m10 = float14;
        arg6.m11 = float15;
        arg6.m12 = float16;
        return arg6;
    }

    public Matrix3f rotationTowards(Vector3fc arg0, Vector3fc arg1) {
        return this.rotationTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix3f rotationTowards(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        float float0 = Math.invsqrt(arg0 * arg0 + arg1 * arg1 + arg2 * arg2);
        float float1 = arg0 * float0;
        float float2 = arg1 * float0;
        float float3 = arg2 * float0;
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
        this.m00 = float4;
        this.m01 = float5;
        this.m02 = float6;
        this.m10 = float8;
        this.m11 = float9;
        this.m12 = float10;
        this.m20 = float1;
        this.m21 = float2;
        this.m22 = float3;
        return this;
    }

    @Override
    public Vector3f getEulerAnglesZYX(Vector3f arg0) {
        arg0.x = Math.atan2(this.m12, this.m22);
        arg0.y = Math.atan2(-this.m02, Math.sqrt(this.m12 * this.m12 + this.m22 * this.m22));
        arg0.z = Math.atan2(this.m01, this.m00);
        return arg0;
    }

    public Matrix3f obliqueZ(float arg0, float arg1) {
        this.m20 = this.m00 * arg0 + this.m10 * arg1 + this.m20;
        this.m21 = this.m01 * arg0 + this.m11 * arg1 + this.m21;
        this.m22 = this.m02 * arg0 + this.m12 * arg1 + this.m22;
        return this;
    }

    @Override
    public Matrix3f obliqueZ(float arg0, float arg1, Matrix3f arg2) {
        arg2.m00 = this.m00;
        arg2.m01 = this.m01;
        arg2.m02 = this.m02;
        arg2.m10 = this.m10;
        arg2.m11 = this.m11;
        arg2.m12 = this.m12;
        arg2.m20 = this.m00 * arg0 + this.m10 * arg1 + this.m20;
        arg2.m21 = this.m01 * arg0 + this.m11 * arg1 + this.m21;
        arg2.m22 = this.m02 * arg0 + this.m12 * arg1 + this.m22;
        return arg2;
    }

    @Override
    public Matrix3f reflect(float arg0, float arg1, float arg2, Matrix3f arg3) {
        float float0 = arg0 + arg0;
        float float1 = arg1 + arg1;
        float float2 = arg2 + arg2;
        float float3 = 1.0F - float0 * arg0;
        float float4 = -float0 * arg1;
        float float5 = -float0 * arg2;
        float float6 = -float1 * arg0;
        float float7 = 1.0F - float1 * arg1;
        float float8 = -float1 * arg2;
        float float9 = -float2 * arg0;
        float float10 = -float2 * arg1;
        float float11 = 1.0F - float2 * arg2;
        float float12 = this.m00 * float3 + this.m10 * float4 + this.m20 * float5;
        float float13 = this.m01 * float3 + this.m11 * float4 + this.m21 * float5;
        float float14 = this.m02 * float3 + this.m12 * float4 + this.m22 * float5;
        float float15 = this.m00 * float6 + this.m10 * float7 + this.m20 * float8;
        float float16 = this.m01 * float6 + this.m11 * float7 + this.m21 * float8;
        float float17 = this.m02 * float6 + this.m12 * float7 + this.m22 * float8;
        return arg3._m20(this.m00 * float9 + this.m10 * float10 + this.m20 * float11)
            ._m21(this.m01 * float9 + this.m11 * float10 + this.m21 * float11)
            ._m22(this.m02 * float9 + this.m12 * float10 + this.m22 * float11)
            ._m00(float12)
            ._m01(float13)
            ._m02(float14)
            ._m10(float15)
            ._m11(float16)
            ._m12(float17);
    }

    public Matrix3f reflect(float arg0, float arg1, float arg2) {
        return this.reflect(arg0, arg1, arg2, this);
    }

    public Matrix3f reflect(Vector3fc arg0) {
        return this.reflect(arg0.x(), arg0.y(), arg0.z());
    }

    public Matrix3f reflect(Quaternionfc arg0) {
        return this.reflect(arg0, this);
    }

    @Override
    public Matrix3f reflect(Quaternionfc arg0, Matrix3f arg1) {
        float float0 = arg0.x() + arg0.x();
        float float1 = arg0.y() + arg0.y();
        float float2 = arg0.z() + arg0.z();
        float float3 = arg0.x() * float2 + arg0.w() * float1;
        float float4 = arg0.y() * float2 - arg0.w() * float0;
        float float5 = 1.0F - (arg0.x() * float0 + arg0.y() * float1);
        return this.reflect(float3, float4, float5, arg1);
    }

    @Override
    public Matrix3f reflect(Vector3fc arg0, Matrix3f arg1) {
        return this.reflect(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    public Matrix3f reflection(float arg0, float arg1, float arg2) {
        float float0 = arg0 + arg0;
        float float1 = arg1 + arg1;
        float float2 = arg2 + arg2;
        this._m00(1.0F - float0 * arg0);
        this._m01(-float0 * arg1);
        this._m02(-float0 * arg2);
        this._m10(-float1 * arg0);
        this._m11(1.0F - float1 * arg1);
        this._m12(-float1 * arg2);
        this._m20(-float2 * arg0);
        this._m21(-float2 * arg1);
        this._m22(1.0F - float2 * arg2);
        return this;
    }

    public Matrix3f reflection(Vector3fc arg0) {
        return this.reflection(arg0.x(), arg0.y(), arg0.z());
    }

    public Matrix3f reflection(Quaternionfc arg0) {
        float float0 = arg0.x() + arg0.x();
        float float1 = arg0.y() + arg0.y();
        float float2 = arg0.z() + arg0.z();
        float float3 = arg0.x() * float2 + arg0.w() * float1;
        float float4 = arg0.y() * float2 - arg0.w() * float0;
        float float5 = 1.0F - (arg0.x() * float0 + arg0.y() * float1);
        return this.reflection(float3, float4, float5);
    }

    @Override
    public boolean isFinite() {
        return Math.isFinite(this.m00)
            && Math.isFinite(this.m01)
            && Math.isFinite(this.m02)
            && Math.isFinite(this.m10)
            && Math.isFinite(this.m11)
            && Math.isFinite(this.m12)
            && Math.isFinite(this.m20)
            && Math.isFinite(this.m21)
            && Math.isFinite(this.m22);
    }

    @Override
    public float quadraticFormProduct(float arg0, float arg1, float arg2) {
        float float0 = this.m00 * arg0 + this.m10 * arg1 + this.m20 * arg2;
        float float1 = this.m01 * arg0 + this.m11 * arg1 + this.m21 * arg2;
        float float2 = this.m02 * arg0 + this.m12 * arg1 + this.m22 * arg2;
        return arg0 * float0 + arg1 * float1 + arg2 * float2;
    }

    @Override
    public float quadraticFormProduct(Vector3fc arg0) {
        return this.quadraticFormProduct(arg0.x(), arg0.y(), arg0.z());
    }
}
