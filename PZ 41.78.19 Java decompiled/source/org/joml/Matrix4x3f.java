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

public class Matrix4x3f implements Externalizable, Matrix4x3fc {
    private static final long serialVersionUID = 1L;
    float m00;
    float m01;
    float m02;
    float m10;
    float m11;
    float m12;
    float m20;
    float m21;
    float m22;
    float m30;
    float m31;
    float m32;
    int properties;

    public Matrix4x3f() {
        this.m00 = 1.0F;
        this.m11 = 1.0F;
        this.m22 = 1.0F;
        this.properties = 28;
    }

    public Matrix4x3f(Matrix3fc arg0) {
        this.set(arg0);
    }

    public Matrix4x3f(Matrix4x3fc arg0) {
        this.set(arg0);
    }

    public Matrix4x3f(
        float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8, float arg9, float arg10, float arg11
    ) {
        this.m00 = arg0;
        this.m01 = arg1;
        this.m02 = arg2;
        this.m10 = arg3;
        this.m11 = arg4;
        this.m12 = arg5;
        this.m20 = arg6;
        this.m21 = arg7;
        this.m22 = arg8;
        this.m30 = arg9;
        this.m31 = arg10;
        this.m32 = arg11;
        this.determineProperties();
    }

    public Matrix4x3f(FloatBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        this.determineProperties();
    }

    public Matrix4x3f(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2, Vector3fc arg3) {
        this.set(arg0, arg1, arg2, arg3).determineProperties();
    }

    public Matrix4x3f assume(int arg0) {
        this.properties = arg0;
        return this;
    }

    public Matrix4x3f determineProperties() {
        byte byte0 = 0;
        if (this.m00 == 1.0F
            && this.m01 == 0.0F
            && this.m02 == 0.0F
            && this.m10 == 0.0F
            && this.m11 == 1.0F
            && this.m12 == 0.0F
            && this.m20 == 0.0F
            && this.m21 == 0.0F
            && this.m22 == 1.0F) {
            byte0 |= 24;
            if (this.m30 == 0.0F && this.m31 == 0.0F && this.m32 == 0.0F) {
                byte0 |= 4;
            }
        }

        this.properties = byte0;
        return this;
    }

    @Override
    public int properties() {
        return this.properties;
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

    @Override
    public float m30() {
        return this.m30;
    }

    @Override
    public float m31() {
        return this.m31;
    }

    @Override
    public float m32() {
        return this.m32;
    }

    public Matrix4x3f m00(float arg0) {
        this.m00 = arg0;
        this.properties &= -17;
        if (arg0 != 1.0F) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4x3f m01(float arg0) {
        this.m01 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0F) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4x3f m02(float arg0) {
        this.m02 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0F) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4x3f m10(float arg0) {
        this.m10 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0F) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4x3f m11(float arg0) {
        this.m11 = arg0;
        this.properties &= -17;
        if (arg0 != 1.0F) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4x3f m12(float arg0) {
        this.m12 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0F) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4x3f m20(float arg0) {
        this.m20 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0F) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4x3f m21(float arg0) {
        this.m21 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0F) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4x3f m22(float arg0) {
        this.m22 = arg0;
        this.properties &= -17;
        if (arg0 != 1.0F) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4x3f m30(float arg0) {
        this.m30 = arg0;
        if (arg0 != 0.0F) {
            this.properties &= -5;
        }

        return this;
    }

    public Matrix4x3f m31(float arg0) {
        this.m31 = arg0;
        if (arg0 != 0.0F) {
            this.properties &= -5;
        }

        return this;
    }

    public Matrix4x3f m32(float arg0) {
        this.m32 = arg0;
        if (arg0 != 0.0F) {
            this.properties &= -5;
        }

        return this;
    }

    Matrix4x3f _properties(int int0) {
        this.properties = int0;
        return this;
    }

    Matrix4x3f _m00(float float0) {
        this.m00 = float0;
        return this;
    }

    Matrix4x3f _m01(float float0) {
        this.m01 = float0;
        return this;
    }

    Matrix4x3f _m02(float float0) {
        this.m02 = float0;
        return this;
    }

    Matrix4x3f _m10(float float0) {
        this.m10 = float0;
        return this;
    }

    Matrix4x3f _m11(float float0) {
        this.m11 = float0;
        return this;
    }

    Matrix4x3f _m12(float float0) {
        this.m12 = float0;
        return this;
    }

    Matrix4x3f _m20(float float0) {
        this.m20 = float0;
        return this;
    }

    Matrix4x3f _m21(float float0) {
        this.m21 = float0;
        return this;
    }

    Matrix4x3f _m22(float float0) {
        this.m22 = float0;
        return this;
    }

    Matrix4x3f _m30(float float0) {
        this.m30 = float0;
        return this;
    }

    Matrix4x3f _m31(float float0) {
        this.m31 = float0;
        return this;
    }

    Matrix4x3f _m32(float float0) {
        this.m32 = float0;
        return this;
    }

    public Matrix4x3f identity() {
        if ((this.properties & 4) != 0) {
            return this;
        } else {
            MemUtil.INSTANCE.identity(this);
            this.properties = 28;
            return this;
        }
    }

    public Matrix4x3f set(Matrix4x3fc arg0) {
        this.m00 = arg0.m00();
        this.m01 = arg0.m01();
        this.m02 = arg0.m02();
        this.m10 = arg0.m10();
        this.m11 = arg0.m11();
        this.m12 = arg0.m12();
        this.m20 = arg0.m20();
        this.m21 = arg0.m21();
        this.m22 = arg0.m22();
        this.m30 = arg0.m30();
        this.m31 = arg0.m31();
        this.m32 = arg0.m32();
        this.properties = arg0.properties();
        return this;
    }

    public Matrix4x3f set(Matrix4fc arg0) {
        this.m00 = arg0.m00();
        this.m01 = arg0.m01();
        this.m02 = arg0.m02();
        this.m10 = arg0.m10();
        this.m11 = arg0.m11();
        this.m12 = arg0.m12();
        this.m20 = arg0.m20();
        this.m21 = arg0.m21();
        this.m22 = arg0.m22();
        this.m30 = arg0.m30();
        this.m31 = arg0.m31();
        this.m32 = arg0.m32();
        this.properties = arg0.properties() & 28;
        return this;
    }

    @Override
    public Matrix4f get(Matrix4f arg0) {
        return arg0.set4x3(this);
    }

    @Override
    public Matrix4d get(Matrix4d arg0) {
        return arg0.set4x3(this);
    }

    public Matrix4x3f set(Matrix3fc arg0) {
        this.m00 = arg0.m00();
        this.m01 = arg0.m01();
        this.m02 = arg0.m02();
        this.m10 = arg0.m10();
        this.m11 = arg0.m11();
        this.m12 = arg0.m12();
        this.m20 = arg0.m20();
        this.m21 = arg0.m21();
        this.m22 = arg0.m22();
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        return this.determineProperties();
    }

    public Matrix4x3f set(AxisAngle4f arg0) {
        float float0 = arg0.x;
        float float1 = arg0.y;
        float float2 = arg0.z;
        float float3 = arg0.angle;
        float float4 = Math.sqrt(float0 * float0 + float1 * float1 + float2 * float2);
        float4 = 1.0F / float4;
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
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.properties = 16;
        return this;
    }

    public Matrix4x3f set(AxisAngle4d arg0) {
        double double0 = arg0.x;
        double double1 = arg0.y;
        double double2 = arg0.z;
        double double3 = arg0.angle;
        double double4 = Math.sqrt(double0 * double0 + double1 * double1 + double2 * double2);
        double4 = 1.0 / double4;
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
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.properties = 16;
        return this;
    }

    public Matrix4x3f set(Quaternionfc arg0) {
        return this.rotation(arg0);
    }

    public Matrix4x3f set(Quaterniondc arg0) {
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
        this.properties = 16;
        return this;
    }

    public Matrix4x3f set(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2, Vector3fc arg3) {
        this.m00 = arg0.x();
        this.m01 = arg0.y();
        this.m02 = arg0.z();
        this.m10 = arg1.x();
        this.m11 = arg1.y();
        this.m12 = arg1.z();
        this.m20 = arg2.x();
        this.m21 = arg2.y();
        this.m22 = arg2.z();
        this.m30 = arg3.x();
        this.m31 = arg3.y();
        this.m32 = arg3.z();
        return this.determineProperties();
    }

    public Matrix4x3f set3x3(Matrix4x3fc arg0) {
        this.m00 = arg0.m00();
        this.m01 = arg0.m01();
        this.m02 = arg0.m02();
        this.m10 = arg0.m10();
        this.m11 = arg0.m11();
        this.m12 = arg0.m12();
        this.m20 = arg0.m20();
        this.m21 = arg0.m21();
        this.m22 = arg0.m22();
        this.properties = this.properties & arg0.properties();
        return this;
    }

    public Matrix4x3f mul(Matrix4x3fc arg0) {
        return this.mul(arg0, this);
    }

    @Override
    public Matrix4x3f mul(Matrix4x3fc arg0, Matrix4x3f arg1) {
        if ((this.properties & 4) != 0) {
            return arg1.set(arg0);
        } else if ((arg0.properties() & 4) != 0) {
            return arg1.set(this);
        } else {
            return (this.properties & 8) != 0 ? this.mulTranslation(arg0, arg1) : this.mulGeneric(arg0, arg1);
        }
    }

    private Matrix4x3f mulGeneric(Matrix4x3fc matrix4x3fc, Matrix4x3f matrix4x3f1) {
        float float0 = this.m00;
        float float1 = this.m01;
        float float2 = this.m02;
        float float3 = this.m10;
        float float4 = this.m11;
        float float5 = this.m12;
        float float6 = this.m20;
        float float7 = this.m21;
        float float8 = this.m22;
        float float9 = matrix4x3fc.m00();
        float float10 = matrix4x3fc.m01();
        float float11 = matrix4x3fc.m02();
        float float12 = matrix4x3fc.m10();
        float float13 = matrix4x3fc.m11();
        float float14 = matrix4x3fc.m12();
        float float15 = matrix4x3fc.m20();
        float float16 = matrix4x3fc.m21();
        float float17 = matrix4x3fc.m22();
        float float18 = matrix4x3fc.m30();
        float float19 = matrix4x3fc.m31();
        float float20 = matrix4x3fc.m32();
        return matrix4x3f1._m00(Math.fma(float0, float9, Math.fma(float3, float10, float6 * float11)))
            ._m01(Math.fma(float1, float9, Math.fma(float4, float10, float7 * float11)))
            ._m02(Math.fma(float2, float9, Math.fma(float5, float10, float8 * float11)))
            ._m10(Math.fma(float0, float12, Math.fma(float3, float13, float6 * float14)))
            ._m11(Math.fma(float1, float12, Math.fma(float4, float13, float7 * float14)))
            ._m12(Math.fma(float2, float12, Math.fma(float5, float13, float8 * float14)))
            ._m20(Math.fma(float0, float15, Math.fma(float3, float16, float6 * float17)))
            ._m21(Math.fma(float1, float15, Math.fma(float4, float16, float7 * float17)))
            ._m22(Math.fma(float2, float15, Math.fma(float5, float16, float8 * float17)))
            ._m30(Math.fma(float0, float18, Math.fma(float3, float19, Math.fma(float6, float20, this.m30))))
            ._m31(Math.fma(float1, float18, Math.fma(float4, float19, Math.fma(float7, float20, this.m31))))
            ._m32(Math.fma(float2, float18, Math.fma(float5, float19, Math.fma(float8, float20, this.m32))))
            ._properties(this.properties & matrix4x3fc.properties() & 16);
    }

    @Override
    public Matrix4x3f mulTranslation(Matrix4x3fc arg0, Matrix4x3f arg1) {
        return arg1._m00(arg0.m00())
            ._m01(arg0.m01())
            ._m02(arg0.m02())
            ._m10(arg0.m10())
            ._m11(arg0.m11())
            ._m12(arg0.m12())
            ._m20(arg0.m20())
            ._m21(arg0.m21())
            ._m22(arg0.m22())
            ._m30(arg0.m30() + this.m30)
            ._m31(arg0.m31() + this.m31)
            ._m32(arg0.m32() + this.m32)
            ._properties(arg0.properties() & 16);
    }

    public Matrix4x3f mulOrtho(Matrix4x3fc arg0) {
        return this.mulOrtho(arg0, this);
    }

    @Override
    public Matrix4x3f mulOrtho(Matrix4x3fc arg0, Matrix4x3f arg1) {
        float float0 = this.m00 * arg0.m00();
        float float1 = this.m11 * arg0.m01();
        float float2 = this.m22 * arg0.m02();
        float float3 = this.m00 * arg0.m10();
        float float4 = this.m11 * arg0.m11();
        float float5 = this.m22 * arg0.m12();
        float float6 = this.m00 * arg0.m20();
        float float7 = this.m11 * arg0.m21();
        float float8 = this.m22 * arg0.m22();
        float float9 = this.m00 * arg0.m30() + this.m30;
        float float10 = this.m11 * arg0.m31() + this.m31;
        float float11 = this.m22 * arg0.m32() + this.m32;
        arg1.m00 = float0;
        arg1.m01 = float1;
        arg1.m02 = float2;
        arg1.m10 = float3;
        arg1.m11 = float4;
        arg1.m12 = float5;
        arg1.m20 = float6;
        arg1.m21 = float7;
        arg1.m22 = float8;
        arg1.m30 = float9;
        arg1.m31 = float10;
        arg1.m32 = float11;
        arg1.properties = this.properties & arg0.properties() & 16;
        return arg1;
    }

    public Matrix4x3f fma(Matrix4x3fc arg0, float arg1) {
        return this.fma(arg0, arg1, this);
    }

    @Override
    public Matrix4x3f fma(Matrix4x3fc arg0, float arg1, Matrix4x3f arg2) {
        arg2._m00(Math.fma(arg0.m00(), arg1, this.m00))
            ._m01(Math.fma(arg0.m01(), arg1, this.m01))
            ._m02(Math.fma(arg0.m02(), arg1, this.m02))
            ._m10(Math.fma(arg0.m10(), arg1, this.m10))
            ._m11(Math.fma(arg0.m11(), arg1, this.m11))
            ._m12(Math.fma(arg0.m12(), arg1, this.m12))
            ._m20(Math.fma(arg0.m20(), arg1, this.m20))
            ._m21(Math.fma(arg0.m21(), arg1, this.m21))
            ._m22(Math.fma(arg0.m22(), arg1, this.m22))
            ._m30(Math.fma(arg0.m30(), arg1, this.m30))
            ._m31(Math.fma(arg0.m31(), arg1, this.m31))
            ._m32(Math.fma(arg0.m32(), arg1, this.m32))
            ._properties(0);
        return arg2;
    }

    public Matrix4x3f add(Matrix4x3fc arg0) {
        return this.add(arg0, this);
    }

    @Override
    public Matrix4x3f add(Matrix4x3fc arg0, Matrix4x3f arg1) {
        arg1.m00 = this.m00 + arg0.m00();
        arg1.m01 = this.m01 + arg0.m01();
        arg1.m02 = this.m02 + arg0.m02();
        arg1.m10 = this.m10 + arg0.m10();
        arg1.m11 = this.m11 + arg0.m11();
        arg1.m12 = this.m12 + arg0.m12();
        arg1.m20 = this.m20 + arg0.m20();
        arg1.m21 = this.m21 + arg0.m21();
        arg1.m22 = this.m22 + arg0.m22();
        arg1.m30 = this.m30 + arg0.m30();
        arg1.m31 = this.m31 + arg0.m31();
        arg1.m32 = this.m32 + arg0.m32();
        arg1.properties = 0;
        return arg1;
    }

    public Matrix4x3f sub(Matrix4x3fc arg0) {
        return this.sub(arg0, this);
    }

    @Override
    public Matrix4x3f sub(Matrix4x3fc arg0, Matrix4x3f arg1) {
        arg1.m00 = this.m00 - arg0.m00();
        arg1.m01 = this.m01 - arg0.m01();
        arg1.m02 = this.m02 - arg0.m02();
        arg1.m10 = this.m10 - arg0.m10();
        arg1.m11 = this.m11 - arg0.m11();
        arg1.m12 = this.m12 - arg0.m12();
        arg1.m20 = this.m20 - arg0.m20();
        arg1.m21 = this.m21 - arg0.m21();
        arg1.m22 = this.m22 - arg0.m22();
        arg1.m30 = this.m30 - arg0.m30();
        arg1.m31 = this.m31 - arg0.m31();
        arg1.m32 = this.m32 - arg0.m32();
        arg1.properties = 0;
        return arg1;
    }

    public Matrix4x3f mulComponentWise(Matrix4x3fc arg0) {
        return this.mulComponentWise(arg0, this);
    }

    @Override
    public Matrix4x3f mulComponentWise(Matrix4x3fc arg0, Matrix4x3f arg1) {
        arg1.m00 = this.m00 * arg0.m00();
        arg1.m01 = this.m01 * arg0.m01();
        arg1.m02 = this.m02 * arg0.m02();
        arg1.m10 = this.m10 * arg0.m10();
        arg1.m11 = this.m11 * arg0.m11();
        arg1.m12 = this.m12 * arg0.m12();
        arg1.m20 = this.m20 * arg0.m20();
        arg1.m21 = this.m21 * arg0.m21();
        arg1.m22 = this.m22 * arg0.m22();
        arg1.m30 = this.m30 * arg0.m30();
        arg1.m31 = this.m31 * arg0.m31();
        arg1.m32 = this.m32 * arg0.m32();
        arg1.properties = 0;
        return arg1;
    }

    public Matrix4x3f set(
        float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8, float arg9, float arg10, float arg11
    ) {
        this.m00 = arg0;
        this.m01 = arg1;
        this.m02 = arg2;
        this.m10 = arg3;
        this.m11 = arg4;
        this.m12 = arg5;
        this.m20 = arg6;
        this.m21 = arg7;
        this.m22 = arg8;
        this.m30 = arg9;
        this.m31 = arg10;
        this.m32 = arg11;
        return this.determineProperties();
    }

    public Matrix4x3f set(float[] floats, int int0) {
        MemUtil.INSTANCE.copy(floats, int0, this);
        return this.determineProperties();
    }

    public Matrix4x3f set(float[] floats) {
        return this.set(floats, 0);
    }

    public Matrix4x3f set(FloatBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this.determineProperties();
    }

    public Matrix4x3f set(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this.determineProperties();
    }

    public Matrix4x3f setFromAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.get(this, arg0);
            return this.determineProperties();
        }
    }

    @Override
    public float determinant() {
        return (this.m00 * this.m11 - this.m01 * this.m10) * this.m22
            + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21
            + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
    }

    @Override
    public Matrix4x3f invert(Matrix4x3f arg0) {
        if ((this.properties & 4) != 0) {
            return arg0.identity();
        } else {
            return (this.properties & 16) != 0 ? this.invertOrthonormal(arg0) : this.invertGeneric(arg0);
        }
    }

    private Matrix4x3f invertGeneric(Matrix4x3f matrix4x3f1) {
        float float0 = this.m00 * this.m11;
        float float1 = this.m01 * this.m10;
        float float2 = this.m02 * this.m10;
        float float3 = this.m00 * this.m12;
        float float4 = this.m01 * this.m12;
        float float5 = this.m02 * this.m11;
        float float6 = 1.0F / ((float0 - float1) * this.m22 + (float2 - float3) * this.m21 + (float4 - float5) * this.m20);
        float float7 = this.m10 * this.m22;
        float float8 = this.m10 * this.m21;
        float float9 = this.m11 * this.m22;
        float float10 = this.m11 * this.m20;
        float float11 = this.m12 * this.m21;
        float float12 = this.m12 * this.m20;
        float float13 = this.m20 * this.m02;
        float float14 = this.m20 * this.m01;
        float float15 = this.m21 * this.m02;
        float float16 = this.m21 * this.m00;
        float float17 = this.m22 * this.m01;
        float float18 = this.m22 * this.m00;
        float float19 = (float9 - float11) * float6;
        float float20 = (float15 - float17) * float6;
        float float21 = (float4 - float5) * float6;
        float float22 = (float12 - float7) * float6;
        float float23 = (float18 - float13) * float6;
        float float24 = (float2 - float3) * float6;
        float float25 = (float8 - float10) * float6;
        float float26 = (float14 - float16) * float6;
        float float27 = (float0 - float1) * float6;
        float float28 = (float7 * this.m31 - float8 * this.m32 + float10 * this.m32 - float9 * this.m30 + float11 * this.m30 - float12 * this.m31) * float6;
        float float29 = (float13 * this.m31 - float14 * this.m32 + float16 * this.m32 - float15 * this.m30 + float17 * this.m30 - float18 * this.m31) * float6;
        float float30 = (float5 * this.m30 - float4 * this.m30 + float3 * this.m31 - float2 * this.m31 + float1 * this.m32 - float0 * this.m32) * float6;
        matrix4x3f1.m00 = float19;
        matrix4x3f1.m01 = float20;
        matrix4x3f1.m02 = float21;
        matrix4x3f1.m10 = float22;
        matrix4x3f1.m11 = float23;
        matrix4x3f1.m12 = float24;
        matrix4x3f1.m20 = float25;
        matrix4x3f1.m21 = float26;
        matrix4x3f1.m22 = float27;
        matrix4x3f1.m30 = float28;
        matrix4x3f1.m31 = float29;
        matrix4x3f1.m32 = float30;
        matrix4x3f1.properties = 0;
        return matrix4x3f1;
    }

    private Matrix4x3f invertOrthonormal(Matrix4x3f matrix4x3f1) {
        float float0 = -(this.m00 * this.m30 + this.m01 * this.m31 + this.m02 * this.m32);
        float float1 = -(this.m10 * this.m30 + this.m11 * this.m31 + this.m12 * this.m32);
        float float2 = -(this.m20 * this.m30 + this.m21 * this.m31 + this.m22 * this.m32);
        float float3 = this.m01;
        float float4 = this.m02;
        float float5 = this.m12;
        matrix4x3f1.m00 = this.m00;
        matrix4x3f1.m01 = this.m10;
        matrix4x3f1.m02 = this.m20;
        matrix4x3f1.m10 = float3;
        matrix4x3f1.m11 = this.m11;
        matrix4x3f1.m12 = this.m21;
        matrix4x3f1.m20 = float4;
        matrix4x3f1.m21 = float5;
        matrix4x3f1.m22 = this.m22;
        matrix4x3f1.m30 = float0;
        matrix4x3f1.m31 = float1;
        matrix4x3f1.m32 = float2;
        matrix4x3f1.properties = 16;
        return matrix4x3f1;
    }

    @Override
    public Matrix4f invert(Matrix4f arg0) {
        if ((this.properties & 4) != 0) {
            return arg0.identity();
        } else {
            return (this.properties & 16) != 0 ? this.invertOrthonormal(arg0) : this.invertGeneric(arg0);
        }
    }

    private Matrix4f invertGeneric(Matrix4f matrix4f) {
        float float0 = this.m00 * this.m11;
        float float1 = this.m01 * this.m10;
        float float2 = this.m02 * this.m10;
        float float3 = this.m00 * this.m12;
        float float4 = this.m01 * this.m12;
        float float5 = this.m02 * this.m11;
        float float6 = 1.0F / ((float0 - float1) * this.m22 + (float2 - float3) * this.m21 + (float4 - float5) * this.m20);
        float float7 = this.m10 * this.m22;
        float float8 = this.m10 * this.m21;
        float float9 = this.m11 * this.m22;
        float float10 = this.m11 * this.m20;
        float float11 = this.m12 * this.m21;
        float float12 = this.m12 * this.m20;
        float float13 = this.m20 * this.m02;
        float float14 = this.m20 * this.m01;
        float float15 = this.m21 * this.m02;
        float float16 = this.m21 * this.m00;
        float float17 = this.m22 * this.m01;
        float float18 = this.m22 * this.m00;
        float float19 = (float9 - float11) * float6;
        float float20 = (float15 - float17) * float6;
        float float21 = (float4 - float5) * float6;
        float float22 = (float12 - float7) * float6;
        float float23 = (float18 - float13) * float6;
        float float24 = (float2 - float3) * float6;
        float float25 = (float8 - float10) * float6;
        float float26 = (float14 - float16) * float6;
        float float27 = (float0 - float1) * float6;
        float float28 = (float7 * this.m31 - float8 * this.m32 + float10 * this.m32 - float9 * this.m30 + float11 * this.m30 - float12 * this.m31) * float6;
        float float29 = (float13 * this.m31 - float14 * this.m32 + float16 * this.m32 - float15 * this.m30 + float17 * this.m30 - float18 * this.m31) * float6;
        float float30 = (float5 * this.m30 - float4 * this.m30 + float3 * this.m31 - float2 * this.m31 + float1 * this.m32 - float0 * this.m32) * float6;
        matrix4f.m00 = float19;
        matrix4f.m01 = float20;
        matrix4f.m02 = float21;
        matrix4f.m03 = 0.0F;
        matrix4f.m10 = float22;
        matrix4f.m11 = float23;
        matrix4f.m12 = float24;
        matrix4f.m13 = 0.0F;
        matrix4f.m20 = float25;
        matrix4f.m21 = float26;
        matrix4f.m22 = float27;
        matrix4f.m23 = 0.0F;
        matrix4f.m30 = float28;
        matrix4f.m31 = float29;
        matrix4f.m32 = float30;
        matrix4f.m33 = 0.0F;
        matrix4f.properties = 0;
        return matrix4f;
    }

    private Matrix4f invertOrthonormal(Matrix4f matrix4f) {
        float float0 = -(this.m00 * this.m30 + this.m01 * this.m31 + this.m02 * this.m32);
        float float1 = -(this.m10 * this.m30 + this.m11 * this.m31 + this.m12 * this.m32);
        float float2 = -(this.m20 * this.m30 + this.m21 * this.m31 + this.m22 * this.m32);
        float float3 = this.m01;
        float float4 = this.m02;
        float float5 = this.m12;
        matrix4f.m00 = this.m00;
        matrix4f.m01 = this.m10;
        matrix4f.m02 = this.m20;
        matrix4f.m03 = 0.0F;
        matrix4f.m10 = float3;
        matrix4f.m11 = this.m11;
        matrix4f.m12 = this.m21;
        matrix4f.m13 = 0.0F;
        matrix4f.m20 = float4;
        matrix4f.m21 = float5;
        matrix4f.m22 = this.m22;
        matrix4f.m23 = 0.0F;
        matrix4f.m30 = float0;
        matrix4f.m31 = float1;
        matrix4f.m32 = float2;
        matrix4f.m33 = 0.0F;
        matrix4f.properties = 16;
        return matrix4f;
    }

    public Matrix4x3f invert() {
        return this.invert(this);
    }

    @Override
    public Matrix4x3f invertOrtho(Matrix4x3f arg0) {
        float float0 = 1.0F / this.m00;
        float float1 = 1.0F / this.m11;
        float float2 = 1.0F / this.m22;
        arg0.set(float0, 0.0F, 0.0F, 0.0F, float1, 0.0F, 0.0F, 0.0F, float2, -this.m30 * float0, -this.m31 * float1, -this.m32 * float2);
        arg0.properties = 0;
        return arg0;
    }

    public Matrix4x3f invertOrtho() {
        return this.invertOrtho(this);
    }

    public Matrix4x3f transpose3x3() {
        return this.transpose3x3(this);
    }

    @Override
    public Matrix4x3f transpose3x3(Matrix4x3f arg0) {
        float float0 = this.m00;
        float float1 = this.m10;
        float float2 = this.m20;
        float float3 = this.m01;
        float float4 = this.m11;
        float float5 = this.m21;
        float float6 = this.m02;
        float float7 = this.m12;
        float float8 = this.m22;
        arg0.m00 = float0;
        arg0.m01 = float1;
        arg0.m02 = float2;
        arg0.m10 = float3;
        arg0.m11 = float4;
        arg0.m12 = float5;
        arg0.m20 = float6;
        arg0.m21 = float7;
        arg0.m22 = float8;
        arg0.properties = this.properties;
        return arg0;
    }

    @Override
    public Matrix3f transpose3x3(Matrix3f arg0) {
        arg0.m00(this.m00);
        arg0.m01(this.m10);
        arg0.m02(this.m20);
        arg0.m10(this.m01);
        arg0.m11(this.m11);
        arg0.m12(this.m21);
        arg0.m20(this.m02);
        arg0.m21(this.m12);
        arg0.m22(this.m22);
        return arg0;
    }

    public Matrix4x3f translation(float arg0, float arg1, float arg2) {
        if ((this.properties & 4) == 0) {
            MemUtil.INSTANCE.identity(this);
        }

        this.m30 = arg0;
        this.m31 = arg1;
        this.m32 = arg2;
        this.properties = 24;
        return this;
    }

    public Matrix4x3f translation(Vector3fc arg0) {
        return this.translation(arg0.x(), arg0.y(), arg0.z());
    }

    public Matrix4x3f setTranslation(float arg0, float arg1, float arg2) {
        this.m30 = arg0;
        this.m31 = arg1;
        this.m32 = arg2;
        this.properties &= -5;
        return this;
    }

    public Matrix4x3f setTranslation(Vector3fc arg0) {
        return this.setTranslation(arg0.x(), arg0.y(), arg0.z());
    }

    @Override
    public Vector3f getTranslation(Vector3f arg0) {
        arg0.x = this.m30;
        arg0.y = this.m31;
        arg0.z = this.m32;
        return arg0;
    }

    @Override
    public Vector3f getScale(Vector3f arg0) {
        arg0.x = Math.sqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
        arg0.y = Math.sqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
        arg0.z = Math.sqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
        return arg0;
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
            + " "
            + Runtime.format(this.m30, numberFormat)
            + "\n"
            + Runtime.format(this.m01, numberFormat)
            + " "
            + Runtime.format(this.m11, numberFormat)
            + " "
            + Runtime.format(this.m21, numberFormat)
            + " "
            + Runtime.format(this.m31, numberFormat)
            + "\n"
            + Runtime.format(this.m02, numberFormat)
            + " "
            + Runtime.format(this.m12, numberFormat)
            + " "
            + Runtime.format(this.m22, numberFormat)
            + " "
            + Runtime.format(this.m32, numberFormat)
            + "\n";
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
    public AxisAngle4f getRotation(AxisAngle4f arg0) {
        return arg0.set(this);
    }

    @Override
    public AxisAngle4d getRotation(AxisAngle4d arg0) {
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
    public Matrix4x3fc getToAddress(long arg0) {
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

    @Override
    public float[] get4x4(float[] floats, int int0) {
        MemUtil.INSTANCE.copy4x4(this, floats, int0);
        return floats;
    }

    @Override
    public float[] get4x4(float[] floats) {
        return this.get4x4(floats, 0);
    }

    @Override
    public FloatBuffer get4x4(FloatBuffer arg0) {
        return this.get4x4(arg0.position(), arg0);
    }

    @Override
    public FloatBuffer get4x4(int arg0, FloatBuffer arg1) {
        MemUtil.INSTANCE.put4x4(this, arg0, arg1);
        return arg1;
    }

    @Override
    public ByteBuffer get4x4(ByteBuffer arg0) {
        return this.get4x4(arg0.position(), arg0);
    }

    @Override
    public ByteBuffer get4x4(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.put4x4(this, arg0, arg1);
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
    public float[] getTransposed(float[] floats, int int0) {
        floats[int0 + 0] = this.m00;
        floats[int0 + 1] = this.m10;
        floats[int0 + 2] = this.m20;
        floats[int0 + 3] = this.m30;
        floats[int0 + 4] = this.m01;
        floats[int0 + 5] = this.m11;
        floats[int0 + 6] = this.m21;
        floats[int0 + 7] = this.m31;
        floats[int0 + 8] = this.m02;
        floats[int0 + 9] = this.m12;
        floats[int0 + 10] = this.m22;
        floats[int0 + 11] = this.m32;
        return floats;
    }

    @Override
    public float[] getTransposed(float[] floats) {
        return this.getTransposed(floats, 0);
    }

    public Matrix4x3f zero() {
        MemUtil.INSTANCE.zero(this);
        this.properties = 0;
        return this;
    }

    public Matrix4x3f scaling(float arg0) {
        return this.scaling(arg0, arg0, arg0);
    }

    public Matrix4x3f scaling(float arg0, float arg1, float arg2) {
        if ((this.properties & 4) == 0) {
            MemUtil.INSTANCE.identity(this);
        }

        this.m00 = arg0;
        this.m11 = arg1;
        this.m22 = arg2;
        boolean boolean0 = Math.absEqualsOne(arg0) && Math.absEqualsOne(arg1) && Math.absEqualsOne(arg2);
        this.properties = boolean0 ? 16 : 0;
        return this;
    }

    public Matrix4x3f scaling(Vector3fc arg0) {
        return this.scaling(arg0.x(), arg0.y(), arg0.z());
    }

    public Matrix4x3f rotation(float arg0, Vector3fc arg1) {
        return this.rotation(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix4x3f rotation(AxisAngle4f arg0) {
        return this.rotation(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    public Matrix4x3f rotation(float arg0, float arg1, float arg2, float arg3) {
        if (arg2 == 0.0F && arg3 == 0.0F && Math.absEqualsOne(arg1)) {
            return this.rotationX(arg1 * arg0);
        } else if (arg1 == 0.0F && arg3 == 0.0F && Math.absEqualsOne(arg2)) {
            return this.rotationY(arg2 * arg0);
        } else {
            return arg1 == 0.0F && arg2 == 0.0F && Math.absEqualsOne(arg3) ? this.rotationZ(arg3 * arg0) : this.rotationInternal(arg0, arg1, arg2, arg3);
        }
    }

    private Matrix4x3f rotationInternal(float float1, float float5, float float6, float float8) {
        float float0 = Math.sin(float1);
        float float2 = Math.cosFromSin(float0, float1);
        float float3 = 1.0F - float2;
        float float4 = float5 * float6;
        float float7 = float5 * float8;
        float float9 = float6 * float8;
        this.m00 = float2 + float5 * float5 * float3;
        this.m01 = float4 * float3 + float8 * float0;
        this.m02 = float7 * float3 - float6 * float0;
        this.m10 = float4 * float3 - float8 * float0;
        this.m11 = float2 + float6 * float6 * float3;
        this.m12 = float9 * float3 + float5 * float0;
        this.m20 = float7 * float3 + float6 * float0;
        this.m21 = float9 * float3 - float5 * float0;
        this.m22 = float2 + float8 * float8 * float3;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.properties = 16;
        return this;
    }

    public Matrix4x3f rotationX(float arg0) {
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
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.properties = 16;
        return this;
    }

    public Matrix4x3f rotationY(float arg0) {
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
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.properties = 16;
        return this;
    }

    public Matrix4x3f rotationZ(float arg0) {
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
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.properties = 16;
        return this;
    }

    public Matrix4x3f rotationXYZ(float arg0, float arg1, float arg2) {
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
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.properties = 16;
        return this;
    }

    public Matrix4x3f rotationZYX(float arg0, float arg1, float arg2) {
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
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.properties = 16;
        return this;
    }

    public Matrix4x3f rotationYXZ(float arg0, float arg1, float arg2) {
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
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.properties = 16;
        return this;
    }

    public Matrix4x3f setRotationXYZ(float arg0, float arg1, float arg2) {
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
        this.properties &= -13;
        return this;
    }

    public Matrix4x3f setRotationZYX(float arg0, float arg1, float arg2) {
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
        this.properties &= -13;
        return this;
    }

    public Matrix4x3f setRotationYXZ(float arg0, float arg1, float arg2) {
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
        this.properties &= -13;
        return this;
    }

    public Matrix4x3f rotation(Quaternionfc arg0) {
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
        this._m00(float0 + float1 - float3 - float2);
        this._m01(float7 + float5);
        this._m02(float9 - float11);
        this._m10(float7 - float5);
        this._m11(float2 - float3 + float0 - float1);
        this._m12(float13 + float15);
        this._m20(float11 + float9);
        this._m21(float13 - float15);
        this._m22(float3 - float2 - float1 + float0);
        this._m30(0.0F);
        this._m31(0.0F);
        this._m32(0.0F);
        this.properties = 16;
        return this;
    }

    public Matrix4x3f translationRotateScale(
        float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8, float arg9
    ) {
        float float0 = arg3 + arg3;
        float float1 = arg4 + arg4;
        float float2 = arg5 + arg5;
        float float3 = float0 * arg3;
        float float4 = float1 * arg4;
        float float5 = float2 * arg5;
        float float6 = float0 * arg4;
        float float7 = float0 * arg5;
        float float8 = float0 * arg6;
        float float9 = float1 * arg5;
        float float10 = float1 * arg6;
        float float11 = float2 * arg6;
        this.m00 = arg7 - (float4 + float5) * arg7;
        this.m01 = (float6 + float11) * arg7;
        this.m02 = (float7 - float10) * arg7;
        this.m10 = (float6 - float11) * arg8;
        this.m11 = arg8 - (float5 + float3) * arg8;
        this.m12 = (float9 + float8) * arg8;
        this.m20 = (float7 + float10) * arg9;
        this.m21 = (float9 - float8) * arg9;
        this.m22 = arg9 - (float4 + float3) * arg9;
        this.m30 = arg0;
        this.m31 = arg1;
        this.m32 = arg2;
        this.properties = 0;
        return this;
    }

    public Matrix4x3f translationRotateScale(Vector3fc arg0, Quaternionfc arg1, Vector3fc arg2) {
        return this.translationRotateScale(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg1.w(), arg2.x(), arg2.y(), arg2.z());
    }

    public Matrix4x3f translationRotateScaleMul(
        float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8, float arg9, Matrix4x3f arg10
    ) {
        float float0 = arg3 + arg3;
        float float1 = arg4 + arg4;
        float float2 = arg5 + arg5;
        float float3 = float0 * arg3;
        float float4 = float1 * arg4;
        float float5 = float2 * arg5;
        float float6 = float0 * arg4;
        float float7 = float0 * arg5;
        float float8 = float0 * arg6;
        float float9 = float1 * arg5;
        float float10 = float1 * arg6;
        float float11 = float2 * arg6;
        float float12 = arg7 - (float4 + float5) * arg7;
        float float13 = (float6 + float11) * arg7;
        float float14 = (float7 - float10) * arg7;
        float float15 = (float6 - float11) * arg8;
        float float16 = arg8 - (float5 + float3) * arg8;
        float float17 = (float9 + float8) * arg8;
        float float18 = (float7 + float10) * arg9;
        float float19 = (float9 - float8) * arg9;
        float float20 = arg9 - (float4 + float3) * arg9;
        float float21 = float12 * arg10.m00 + float15 * arg10.m01 + float18 * arg10.m02;
        float float22 = float13 * arg10.m00 + float16 * arg10.m01 + float19 * arg10.m02;
        this.m02 = float14 * arg10.m00 + float17 * arg10.m01 + float20 * arg10.m02;
        this.m00 = float21;
        this.m01 = float22;
        float float23 = float12 * arg10.m10 + float15 * arg10.m11 + float18 * arg10.m12;
        float float24 = float13 * arg10.m10 + float16 * arg10.m11 + float19 * arg10.m12;
        this.m12 = float14 * arg10.m10 + float17 * arg10.m11 + float20 * arg10.m12;
        this.m10 = float23;
        this.m11 = float24;
        float float25 = float12 * arg10.m20 + float15 * arg10.m21 + float18 * arg10.m22;
        float float26 = float13 * arg10.m20 + float16 * arg10.m21 + float19 * arg10.m22;
        this.m22 = float14 * arg10.m20 + float17 * arg10.m21 + float20 * arg10.m22;
        this.m20 = float25;
        this.m21 = float26;
        float float27 = float12 * arg10.m30 + float15 * arg10.m31 + float18 * arg10.m32 + arg0;
        float float28 = float13 * arg10.m30 + float16 * arg10.m31 + float19 * arg10.m32 + arg1;
        this.m32 = float14 * arg10.m30 + float17 * arg10.m31 + float20 * arg10.m32 + arg2;
        this.m30 = float27;
        this.m31 = float28;
        this.properties = 0;
        return this;
    }

    public Matrix4x3f translationRotateScaleMul(Vector3fc arg0, Quaternionfc arg1, Vector3fc arg2, Matrix4x3f arg3) {
        return this.translationRotateScaleMul(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg1.w(), arg2.x(), arg2.y(), arg2.z(), arg3);
    }

    public Matrix4x3f translationRotate(float arg0, float arg1, float arg2, Quaternionfc arg3) {
        float float0 = arg3.x() + arg3.x();
        float float1 = arg3.y() + arg3.y();
        float float2 = arg3.z() + arg3.z();
        float float3 = float0 * arg3.x();
        float float4 = float1 * arg3.y();
        float float5 = float2 * arg3.z();
        float float6 = float0 * arg3.y();
        float float7 = float0 * arg3.z();
        float float8 = float0 * arg3.w();
        float float9 = float1 * arg3.z();
        float float10 = float1 * arg3.w();
        float float11 = float2 * arg3.w();
        this.m00 = 1.0F - (float4 + float5);
        this.m01 = float6 + float11;
        this.m02 = float7 - float10;
        this.m10 = float6 - float11;
        this.m11 = 1.0F - (float5 + float3);
        this.m12 = float9 + float8;
        this.m20 = float7 + float10;
        this.m21 = float9 - float8;
        this.m22 = 1.0F - (float4 + float3);
        this.m30 = arg0;
        this.m31 = arg1;
        this.m32 = arg2;
        this.properties = 16;
        return this;
    }

    public Matrix4x3f translationRotateMul(float arg0, float arg1, float arg2, Quaternionfc arg3, Matrix4x3fc arg4) {
        return this.translationRotateMul(arg0, arg1, arg2, arg3.x(), arg3.y(), arg3.z(), arg3.w(), arg4);
    }

    public Matrix4x3f translationRotateMul(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, Matrix4x3fc arg7) {
        float float0 = arg6 * arg6;
        float float1 = arg3 * arg3;
        float float2 = arg4 * arg4;
        float float3 = arg5 * arg5;
        float float4 = arg5 * arg6;
        float float5 = arg3 * arg4;
        float float6 = arg3 * arg5;
        float float7 = arg4 * arg6;
        float float8 = arg4 * arg5;
        float float9 = arg3 * arg6;
        float float10 = float0 + float1 - float3 - float2;
        float float11 = float5 + float4 + float4 + float5;
        float float12 = float6 - float7 + float6 - float7;
        float float13 = -float4 + float5 - float4 + float5;
        float float14 = float2 - float3 + float0 - float1;
        float float15 = float8 + float8 + float9 + float9;
        float float16 = float7 + float6 + float6 + float7;
        float float17 = float8 + float8 - float9 - float9;
        float float18 = float3 - float2 - float1 + float0;
        this.m00 = float10 * arg7.m00() + float13 * arg7.m01() + float16 * arg7.m02();
        this.m01 = float11 * arg7.m00() + float14 * arg7.m01() + float17 * arg7.m02();
        this.m02 = float12 * arg7.m00() + float15 * arg7.m01() + float18 * arg7.m02();
        this.m10 = float10 * arg7.m10() + float13 * arg7.m11() + float16 * arg7.m12();
        this.m11 = float11 * arg7.m10() + float14 * arg7.m11() + float17 * arg7.m12();
        this.m12 = float12 * arg7.m10() + float15 * arg7.m11() + float18 * arg7.m12();
        this.m20 = float10 * arg7.m20() + float13 * arg7.m21() + float16 * arg7.m22();
        this.m21 = float11 * arg7.m20() + float14 * arg7.m21() + float17 * arg7.m22();
        this.m22 = float12 * arg7.m20() + float15 * arg7.m21() + float18 * arg7.m22();
        this.m30 = float10 * arg7.m30() + float13 * arg7.m31() + float16 * arg7.m32() + arg0;
        this.m31 = float11 * arg7.m30() + float14 * arg7.m31() + float17 * arg7.m32() + arg1;
        this.m32 = float12 * arg7.m30() + float15 * arg7.m31() + float18 * arg7.m32() + arg2;
        this.properties = 0;
        return this;
    }

    public Matrix4x3f set3x3(Matrix3fc arg0) {
        if (arg0 instanceof Matrix3f) {
            MemUtil.INSTANCE.copy3x3((Matrix3f)arg0, this);
        } else {
            this.set3x3Matrix3fc(arg0);
        }

        this.properties = 0;
        return this;
    }

    private void set3x3Matrix3fc(Matrix3fc matrix3fc) {
        this.m00 = matrix3fc.m00();
        this.m01 = matrix3fc.m01();
        this.m02 = matrix3fc.m02();
        this.m10 = matrix3fc.m10();
        this.m11 = matrix3fc.m11();
        this.m12 = matrix3fc.m12();
        this.m20 = matrix3fc.m20();
        this.m21 = matrix3fc.m21();
        this.m22 = matrix3fc.m22();
    }

    @Override
    public Vector4f transform(Vector4f arg0) {
        return arg0.mul(this);
    }

    @Override
    public Vector4f transform(Vector4fc arg0, Vector4f arg1) {
        return arg0.mul(this, arg1);
    }

    @Override
    public Vector3f transformPosition(Vector3f arg0) {
        arg0.set(
            this.m00 * arg0.x + this.m10 * arg0.y + this.m20 * arg0.z + this.m30,
            this.m01 * arg0.x + this.m11 * arg0.y + this.m21 * arg0.z + this.m31,
            this.m02 * arg0.x + this.m12 * arg0.y + this.m22 * arg0.z + this.m32
        );
        return arg0;
    }

    @Override
    public Vector3f transformPosition(Vector3fc arg0, Vector3f arg1) {
        arg1.set(
            this.m00 * arg0.x() + this.m10 * arg0.y() + this.m20 * arg0.z() + this.m30,
            this.m01 * arg0.x() + this.m11 * arg0.y() + this.m21 * arg0.z() + this.m31,
            this.m02 * arg0.x() + this.m12 * arg0.y() + this.m22 * arg0.z() + this.m32
        );
        return arg1;
    }

    @Override
    public Vector3f transformDirection(Vector3f arg0) {
        arg0.set(
            this.m00 * arg0.x + this.m10 * arg0.y + this.m20 * arg0.z,
            this.m01 * arg0.x + this.m11 * arg0.y + this.m21 * arg0.z,
            this.m02 * arg0.x + this.m12 * arg0.y + this.m22 * arg0.z
        );
        return arg0;
    }

    @Override
    public Vector3f transformDirection(Vector3fc arg0, Vector3f arg1) {
        arg1.set(
            this.m00 * arg0.x() + this.m10 * arg0.y() + this.m20 * arg0.z(),
            this.m01 * arg0.x() + this.m11 * arg0.y() + this.m21 * arg0.z(),
            this.m02 * arg0.x() + this.m12 * arg0.y() + this.m22 * arg0.z()
        );
        return arg1;
    }

    @Override
    public Matrix4x3f scale(Vector3fc arg0, Matrix4x3f arg1) {
        return this.scale(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    public Matrix4x3f scale(Vector3fc arg0) {
        return this.scale(arg0.x(), arg0.y(), arg0.z(), this);
    }

    @Override
    public Matrix4x3f scale(float arg0, Matrix4x3f arg1) {
        return this.scale(arg0, arg0, arg0, arg1);
    }

    public Matrix4x3f scale(float arg0) {
        return this.scale(arg0, arg0, arg0);
    }

    @Override
    public Matrix4x3f scaleXY(float arg0, float arg1, Matrix4x3f arg2) {
        return this.scale(arg0, arg1, 1.0F, arg2);
    }

    public Matrix4x3f scaleXY(float arg0, float arg1) {
        return this.scale(arg0, arg1, 1.0F);
    }

    @Override
    public Matrix4x3f scale(float arg0, float arg1, float arg2, Matrix4x3f arg3) {
        return (this.properties & 4) != 0 ? arg3.scaling(arg0, arg1, arg2) : this.scaleGeneric(arg0, arg1, arg2, arg3);
    }

    private Matrix4x3f scaleGeneric(float float0, float float1, float float2, Matrix4x3f matrix4x3f1) {
        matrix4x3f1.m00 = this.m00 * float0;
        matrix4x3f1.m01 = this.m01 * float0;
        matrix4x3f1.m02 = this.m02 * float0;
        matrix4x3f1.m10 = this.m10 * float1;
        matrix4x3f1.m11 = this.m11 * float1;
        matrix4x3f1.m12 = this.m12 * float1;
        matrix4x3f1.m20 = this.m20 * float2;
        matrix4x3f1.m21 = this.m21 * float2;
        matrix4x3f1.m22 = this.m22 * float2;
        matrix4x3f1.m30 = this.m30;
        matrix4x3f1.m31 = this.m31;
        matrix4x3f1.m32 = this.m32;
        matrix4x3f1.properties = this.properties & -29;
        return matrix4x3f1;
    }

    public Matrix4x3f scale(float arg0, float arg1, float arg2) {
        return this.scale(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4x3f scaleLocal(float arg0, float arg1, float arg2, Matrix4x3f arg3) {
        if ((this.properties & 4) != 0) {
            return arg3.scaling(arg0, arg1, arg2);
        } else {
            float float0 = arg0 * this.m00;
            float float1 = arg1 * this.m01;
            float float2 = arg2 * this.m02;
            float float3 = arg0 * this.m10;
            float float4 = arg1 * this.m11;
            float float5 = arg2 * this.m12;
            float float6 = arg0 * this.m20;
            float float7 = arg1 * this.m21;
            float float8 = arg2 * this.m22;
            float float9 = arg0 * this.m30;
            float float10 = arg1 * this.m31;
            float float11 = arg2 * this.m32;
            arg3.m00 = float0;
            arg3.m01 = float1;
            arg3.m02 = float2;
            arg3.m10 = float3;
            arg3.m11 = float4;
            arg3.m12 = float5;
            arg3.m20 = float6;
            arg3.m21 = float7;
            arg3.m22 = float8;
            arg3.m30 = float9;
            arg3.m31 = float10;
            arg3.m32 = float11;
            arg3.properties = this.properties & -29;
            return arg3;
        }
    }

    public Matrix4x3f scaleLocal(float arg0, float arg1, float arg2) {
        return this.scaleLocal(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4x3f rotateX(float arg0, Matrix4x3f arg1) {
        if ((this.properties & 4) != 0) {
            return arg1.rotationX(arg0);
        } else if ((this.properties & 8) != 0) {
            float float0 = this.m30;
            float float1 = this.m31;
            float float2 = this.m32;
            return arg1.rotationX(arg0).setTranslation(float0, float1, float2);
        } else {
            return this.rotateXInternal(arg0, arg1);
        }
    }

    private Matrix4x3f rotateXInternal(float float1, Matrix4x3f matrix4x3f1) {
        float float0 = Math.sin(float1);
        float float2 = Math.cosFromSin(float0, float1);
        float float3 = -float0;
        float float4 = this.m10 * float2 + this.m20 * float0;
        float float5 = this.m11 * float2 + this.m21 * float0;
        float float6 = this.m12 * float2 + this.m22 * float0;
        matrix4x3f1.m20 = this.m10 * float3 + this.m20 * float2;
        matrix4x3f1.m21 = this.m11 * float3 + this.m21 * float2;
        matrix4x3f1.m22 = this.m12 * float3 + this.m22 * float2;
        matrix4x3f1.m10 = float4;
        matrix4x3f1.m11 = float5;
        matrix4x3f1.m12 = float6;
        matrix4x3f1.m00 = this.m00;
        matrix4x3f1.m01 = this.m01;
        matrix4x3f1.m02 = this.m02;
        matrix4x3f1.m30 = this.m30;
        matrix4x3f1.m31 = this.m31;
        matrix4x3f1.m32 = this.m32;
        matrix4x3f1.properties = this.properties & -13;
        return matrix4x3f1;
    }

    public Matrix4x3f rotateX(float arg0) {
        return this.rotateX(arg0, this);
    }

    @Override
    public Matrix4x3f rotateY(float arg0, Matrix4x3f arg1) {
        if ((this.properties & 4) != 0) {
            return arg1.rotationY(arg0);
        } else if ((this.properties & 8) != 0) {
            float float0 = this.m30;
            float float1 = this.m31;
            float float2 = this.m32;
            return arg1.rotationY(arg0).setTranslation(float0, float1, float2);
        } else {
            return this.rotateYInternal(arg0, arg1);
        }
    }

    private Matrix4x3f rotateYInternal(float float1, Matrix4x3f matrix4x3f1) {
        float float0 = Math.sin(float1);
        float float2 = Math.cosFromSin(float0, float1);
        float float3 = -float0;
        float float4 = this.m00 * float2 + this.m20 * float3;
        float float5 = this.m01 * float2 + this.m21 * float3;
        float float6 = this.m02 * float2 + this.m22 * float3;
        matrix4x3f1.m20 = this.m00 * float0 + this.m20 * float2;
        matrix4x3f1.m21 = this.m01 * float0 + this.m21 * float2;
        matrix4x3f1.m22 = this.m02 * float0 + this.m22 * float2;
        matrix4x3f1.m00 = float4;
        matrix4x3f1.m01 = float5;
        matrix4x3f1.m02 = float6;
        matrix4x3f1.m10 = this.m10;
        matrix4x3f1.m11 = this.m11;
        matrix4x3f1.m12 = this.m12;
        matrix4x3f1.m30 = this.m30;
        matrix4x3f1.m31 = this.m31;
        matrix4x3f1.m32 = this.m32;
        matrix4x3f1.properties = this.properties & -13;
        return matrix4x3f1;
    }

    public Matrix4x3f rotateY(float arg0) {
        return this.rotateY(arg0, this);
    }

    @Override
    public Matrix4x3f rotateZ(float arg0, Matrix4x3f arg1) {
        if ((this.properties & 4) != 0) {
            return arg1.rotationZ(arg0);
        } else if ((this.properties & 8) != 0) {
            float float0 = this.m30;
            float float1 = this.m31;
            float float2 = this.m32;
            return arg1.rotationZ(arg0).setTranslation(float0, float1, float2);
        } else {
            return this.rotateZInternal(arg0, arg1);
        }
    }

    private Matrix4x3f rotateZInternal(float float1, Matrix4x3f matrix4x3f1) {
        float float0 = Math.sin(float1);
        float float2 = Math.cosFromSin(float0, float1);
        float float3 = -float0;
        float float4 = this.m00 * float2 + this.m10 * float0;
        float float5 = this.m01 * float2 + this.m11 * float0;
        float float6 = this.m02 * float2 + this.m12 * float0;
        matrix4x3f1.m10 = this.m00 * float3 + this.m10 * float2;
        matrix4x3f1.m11 = this.m01 * float3 + this.m11 * float2;
        matrix4x3f1.m12 = this.m02 * float3 + this.m12 * float2;
        matrix4x3f1.m00 = float4;
        matrix4x3f1.m01 = float5;
        matrix4x3f1.m02 = float6;
        matrix4x3f1.m20 = this.m20;
        matrix4x3f1.m21 = this.m21;
        matrix4x3f1.m22 = this.m22;
        matrix4x3f1.m30 = this.m30;
        matrix4x3f1.m31 = this.m31;
        matrix4x3f1.m32 = this.m32;
        matrix4x3f1.properties = this.properties & -13;
        return matrix4x3f1;
    }

    public Matrix4x3f rotateZ(float arg0) {
        return this.rotateZ(arg0, this);
    }

    public Matrix4x3f rotateXYZ(Vector3f arg0) {
        return this.rotateXYZ(arg0.x, arg0.y, arg0.z);
    }

    public Matrix4x3f rotateXYZ(float arg0, float arg1, float arg2) {
        return this.rotateXYZ(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4x3f rotateXYZ(float arg0, float arg1, float arg2, Matrix4x3f arg3) {
        if ((this.properties & 4) != 0) {
            return arg3.rotationXYZ(arg0, arg1, arg2);
        } else if ((this.properties & 8) != 0) {
            float float0 = this.m30;
            float float1 = this.m31;
            float float2 = this.m32;
            return arg3.rotationXYZ(arg0, arg1, arg2).setTranslation(float0, float1, float2);
        } else {
            return this.rotateXYZInternal(arg0, arg1, arg2, arg3);
        }
    }

    private Matrix4x3f rotateXYZInternal(float float1, float float4, float float7, Matrix4x3f matrix4x3f1) {
        float float0 = Math.sin(float1);
        float float2 = Math.cosFromSin(float0, float1);
        float float3 = Math.sin(float4);
        float float5 = Math.cosFromSin(float3, float4);
        float float6 = Math.sin(float7);
        float float8 = Math.cosFromSin(float6, float7);
        float float9 = -float0;
        float float10 = -float3;
        float float11 = -float6;
        float float12 = this.m10 * float2 + this.m20 * float0;
        float float13 = this.m11 * float2 + this.m21 * float0;
        float float14 = this.m12 * float2 + this.m22 * float0;
        float float15 = this.m10 * float9 + this.m20 * float2;
        float float16 = this.m11 * float9 + this.m21 * float2;
        float float17 = this.m12 * float9 + this.m22 * float2;
        float float18 = this.m00 * float5 + float15 * float10;
        float float19 = this.m01 * float5 + float16 * float10;
        float float20 = this.m02 * float5 + float17 * float10;
        matrix4x3f1.m20 = this.m00 * float3 + float15 * float5;
        matrix4x3f1.m21 = this.m01 * float3 + float16 * float5;
        matrix4x3f1.m22 = this.m02 * float3 + float17 * float5;
        matrix4x3f1.m00 = float18 * float8 + float12 * float6;
        matrix4x3f1.m01 = float19 * float8 + float13 * float6;
        matrix4x3f1.m02 = float20 * float8 + float14 * float6;
        matrix4x3f1.m10 = float18 * float11 + float12 * float8;
        matrix4x3f1.m11 = float19 * float11 + float13 * float8;
        matrix4x3f1.m12 = float20 * float11 + float14 * float8;
        matrix4x3f1.m30 = this.m30;
        matrix4x3f1.m31 = this.m31;
        matrix4x3f1.m32 = this.m32;
        matrix4x3f1.properties = this.properties & -13;
        return matrix4x3f1;
    }

    public Matrix4x3f rotateZYX(Vector3f arg0) {
        return this.rotateZYX(arg0.z, arg0.y, arg0.x);
    }

    public Matrix4x3f rotateZYX(float arg0, float arg1, float arg2) {
        return this.rotateZYX(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4x3f rotateZYX(float arg0, float arg1, float arg2, Matrix4x3f arg3) {
        if ((this.properties & 4) != 0) {
            return arg3.rotationZYX(arg0, arg1, arg2);
        } else if ((this.properties & 8) != 0) {
            float float0 = this.m30;
            float float1 = this.m31;
            float float2 = this.m32;
            return arg3.rotationZYX(arg0, arg1, arg2).setTranslation(float0, float1, float2);
        } else {
            return this.rotateZYXInternal(arg0, arg1, arg2, arg3);
        }
    }

    private Matrix4x3f rotateZYXInternal(float float7, float float4, float float1, Matrix4x3f matrix4x3f1) {
        float float0 = Math.sin(float1);
        float float2 = Math.cosFromSin(float0, float1);
        float float3 = Math.sin(float4);
        float float5 = Math.cosFromSin(float3, float4);
        float float6 = Math.sin(float7);
        float float8 = Math.cosFromSin(float6, float7);
        float float9 = -float6;
        float float10 = -float3;
        float float11 = -float0;
        float float12 = this.m00 * float8 + this.m10 * float6;
        float float13 = this.m01 * float8 + this.m11 * float6;
        float float14 = this.m02 * float8 + this.m12 * float6;
        float float15 = this.m00 * float9 + this.m10 * float8;
        float float16 = this.m01 * float9 + this.m11 * float8;
        float float17 = this.m02 * float9 + this.m12 * float8;
        float float18 = float12 * float3 + this.m20 * float5;
        float float19 = float13 * float3 + this.m21 * float5;
        float float20 = float14 * float3 + this.m22 * float5;
        matrix4x3f1.m00 = float12 * float5 + this.m20 * float10;
        matrix4x3f1.m01 = float13 * float5 + this.m21 * float10;
        matrix4x3f1.m02 = float14 * float5 + this.m22 * float10;
        matrix4x3f1.m10 = float15 * float2 + float18 * float0;
        matrix4x3f1.m11 = float16 * float2 + float19 * float0;
        matrix4x3f1.m12 = float17 * float2 + float20 * float0;
        matrix4x3f1.m20 = float15 * float11 + float18 * float2;
        matrix4x3f1.m21 = float16 * float11 + float19 * float2;
        matrix4x3f1.m22 = float17 * float11 + float20 * float2;
        matrix4x3f1.m30 = this.m30;
        matrix4x3f1.m31 = this.m31;
        matrix4x3f1.m32 = this.m32;
        matrix4x3f1.properties = this.properties & -13;
        return matrix4x3f1;
    }

    public Matrix4x3f rotateYXZ(Vector3f arg0) {
        return this.rotateYXZ(arg0.y, arg0.x, arg0.z);
    }

    public Matrix4x3f rotateYXZ(float arg0, float arg1, float arg2) {
        return this.rotateYXZ(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4x3f rotateYXZ(float arg0, float arg1, float arg2, Matrix4x3f arg3) {
        if ((this.properties & 4) != 0) {
            return arg3.rotationYXZ(arg0, arg1, arg2);
        } else if ((this.properties & 8) != 0) {
            float float0 = this.m30;
            float float1 = this.m31;
            float float2 = this.m32;
            return arg3.rotationYXZ(arg0, arg1, arg2).setTranslation(float0, float1, float2);
        } else {
            return this.rotateYXZInternal(arg0, arg1, arg2, arg3);
        }
    }

    private Matrix4x3f rotateYXZInternal(float float4, float float1, float float7, Matrix4x3f matrix4x3f1) {
        float float0 = Math.sin(float1);
        float float2 = Math.cosFromSin(float0, float1);
        float float3 = Math.sin(float4);
        float float5 = Math.cosFromSin(float3, float4);
        float float6 = Math.sin(float7);
        float float8 = Math.cosFromSin(float6, float7);
        float float9 = -float3;
        float float10 = -float0;
        float float11 = -float6;
        float float12 = this.m00 * float3 + this.m20 * float5;
        float float13 = this.m01 * float3 + this.m21 * float5;
        float float14 = this.m02 * float3 + this.m22 * float5;
        float float15 = this.m00 * float5 + this.m20 * float9;
        float float16 = this.m01 * float5 + this.m21 * float9;
        float float17 = this.m02 * float5 + this.m22 * float9;
        float float18 = this.m10 * float2 + float12 * float0;
        float float19 = this.m11 * float2 + float13 * float0;
        float float20 = this.m12 * float2 + float14 * float0;
        matrix4x3f1.m20 = this.m10 * float10 + float12 * float2;
        matrix4x3f1.m21 = this.m11 * float10 + float13 * float2;
        matrix4x3f1.m22 = this.m12 * float10 + float14 * float2;
        matrix4x3f1.m00 = float15 * float8 + float18 * float6;
        matrix4x3f1.m01 = float16 * float8 + float19 * float6;
        matrix4x3f1.m02 = float17 * float8 + float20 * float6;
        matrix4x3f1.m10 = float15 * float11 + float18 * float8;
        matrix4x3f1.m11 = float16 * float11 + float19 * float8;
        matrix4x3f1.m12 = float17 * float11 + float20 * float8;
        matrix4x3f1.m30 = this.m30;
        matrix4x3f1.m31 = this.m31;
        matrix4x3f1.m32 = this.m32;
        matrix4x3f1.properties = this.properties & -13;
        return matrix4x3f1;
    }

    @Override
    public Matrix4x3f rotate(float arg0, float arg1, float arg2, float arg3, Matrix4x3f arg4) {
        if ((this.properties & 4) != 0) {
            return arg4.rotation(arg0, arg1, arg2, arg3);
        } else {
            return (this.properties & 8) != 0 ? this.rotateTranslation(arg0, arg1, arg2, arg3, arg4) : this.rotateGeneric(arg0, arg1, arg2, arg3, arg4);
        }
    }

    private Matrix4x3f rotateGeneric(float float3, float float0, float float2, float float1, Matrix4x3f matrix4x3f1) {
        if (float2 == 0.0F && float1 == 0.0F && Math.absEqualsOne(float0)) {
            return this.rotateX(float0 * float3, matrix4x3f1);
        } else if (float0 == 0.0F && float1 == 0.0F && Math.absEqualsOne(float2)) {
            return this.rotateY(float2 * float3, matrix4x3f1);
        } else {
            return float0 == 0.0F && float2 == 0.0F && Math.absEqualsOne(float1)
                ? this.rotateZ(float1 * float3, matrix4x3f1)
                : this.rotateGenericInternal(float3, float0, float2, float1, matrix4x3f1);
        }
    }

    private Matrix4x3f rotateGenericInternal(float float1, float float5, float float7, float float9, Matrix4x3f matrix4x3f1) {
        float float0 = Math.sin(float1);
        float float2 = Math.cosFromSin(float0, float1);
        float float3 = 1.0F - float2;
        float float4 = float5 * float5;
        float float6 = float5 * float7;
        float float8 = float5 * float9;
        float float10 = float7 * float7;
        float float11 = float7 * float9;
        float float12 = float9 * float9;
        float float13 = float4 * float3 + float2;
        float float14 = float6 * float3 + float9 * float0;
        float float15 = float8 * float3 - float7 * float0;
        float float16 = float6 * float3 - float9 * float0;
        float float17 = float10 * float3 + float2;
        float float18 = float11 * float3 + float5 * float0;
        float float19 = float8 * float3 + float7 * float0;
        float float20 = float11 * float3 - float5 * float0;
        float float21 = float12 * float3 + float2;
        float float22 = this.m00 * float13 + this.m10 * float14 + this.m20 * float15;
        float float23 = this.m01 * float13 + this.m11 * float14 + this.m21 * float15;
        float float24 = this.m02 * float13 + this.m12 * float14 + this.m22 * float15;
        float float25 = this.m00 * float16 + this.m10 * float17 + this.m20 * float18;
        float float26 = this.m01 * float16 + this.m11 * float17 + this.m21 * float18;
        float float27 = this.m02 * float16 + this.m12 * float17 + this.m22 * float18;
        matrix4x3f1.m20 = this.m00 * float19 + this.m10 * float20 + this.m20 * float21;
        matrix4x3f1.m21 = this.m01 * float19 + this.m11 * float20 + this.m21 * float21;
        matrix4x3f1.m22 = this.m02 * float19 + this.m12 * float20 + this.m22 * float21;
        matrix4x3f1.m00 = float22;
        matrix4x3f1.m01 = float23;
        matrix4x3f1.m02 = float24;
        matrix4x3f1.m10 = float25;
        matrix4x3f1.m11 = float26;
        matrix4x3f1.m12 = float27;
        matrix4x3f1.m30 = this.m30;
        matrix4x3f1.m31 = this.m31;
        matrix4x3f1.m32 = this.m32;
        matrix4x3f1.properties = this.properties & -13;
        return matrix4x3f1;
    }

    public Matrix4x3f rotate(float arg0, float arg1, float arg2, float arg3) {
        return this.rotate(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix4x3f rotateTranslation(float arg0, float arg1, float arg2, float arg3, Matrix4x3f arg4) {
        float float0 = this.m30;
        float float1 = this.m31;
        float float2 = this.m32;
        if (arg2 == 0.0F && arg3 == 0.0F && Math.absEqualsOne(arg1)) {
            return arg4.rotationX(arg1 * arg0).setTranslation(float0, float1, float2);
        } else if (arg1 == 0.0F && arg3 == 0.0F && Math.absEqualsOne(arg2)) {
            return arg4.rotationY(arg2 * arg0).setTranslation(float0, float1, float2);
        } else {
            return arg1 == 0.0F && arg2 == 0.0F && Math.absEqualsOne(arg3)
                ? arg4.rotationZ(arg3 * arg0).setTranslation(float0, float1, float2)
                : this.rotateTranslationInternal(arg0, arg1, arg2, arg3, arg4);
        }
    }

    private Matrix4x3f rotateTranslationInternal(float float1, float float5, float float7, float float9, Matrix4x3f matrix4x3f0) {
        float float0 = Math.sin(float1);
        float float2 = Math.cosFromSin(float0, float1);
        float float3 = 1.0F - float2;
        float float4 = float5 * float5;
        float float6 = float5 * float7;
        float float8 = float5 * float9;
        float float10 = float7 * float7;
        float float11 = float7 * float9;
        float float12 = float9 * float9;
        float float13 = float4 * float3 + float2;
        float float14 = float6 * float3 + float9 * float0;
        float float15 = float8 * float3 - float7 * float0;
        float float16 = float6 * float3 - float9 * float0;
        float float17 = float10 * float3 + float2;
        float float18 = float11 * float3 + float5 * float0;
        float float19 = float8 * float3 + float7 * float0;
        float float20 = float11 * float3 - float5 * float0;
        float float21 = float12 * float3 + float2;
        matrix4x3f0.m20 = float19;
        matrix4x3f0.m21 = float20;
        matrix4x3f0.m22 = float21;
        matrix4x3f0.m00 = float13;
        matrix4x3f0.m01 = float14;
        matrix4x3f0.m02 = float15;
        matrix4x3f0.m10 = float16;
        matrix4x3f0.m11 = float17;
        matrix4x3f0.m12 = float18;
        matrix4x3f0.m30 = this.m30;
        matrix4x3f0.m31 = this.m31;
        matrix4x3f0.m32 = this.m32;
        matrix4x3f0.properties = this.properties & -13;
        return matrix4x3f0;
    }

    public Matrix4x3f rotateAround(Quaternionfc arg0, float arg1, float arg2, float arg3) {
        return this.rotateAround(arg0, arg1, arg2, arg3, this);
    }

    private Matrix4x3f rotateAroundAffine(Quaternionfc quaternionfc, float float28, float float27, float float26, Matrix4x3f matrix4x3f1) {
        float float0 = quaternionfc.w() * quaternionfc.w();
        float float1 = quaternionfc.x() * quaternionfc.x();
        float float2 = quaternionfc.y() * quaternionfc.y();
        float float3 = quaternionfc.z() * quaternionfc.z();
        float float4 = quaternionfc.z() * quaternionfc.w();
        float float5 = float4 + float4;
        float float6 = quaternionfc.x() * quaternionfc.y();
        float float7 = float6 + float6;
        float float8 = quaternionfc.x() * quaternionfc.z();
        float float9 = float8 + float8;
        float float10 = quaternionfc.y() * quaternionfc.w();
        float float11 = float10 + float10;
        float float12 = quaternionfc.y() * quaternionfc.z();
        float float13 = float12 + float12;
        float float14 = quaternionfc.x() * quaternionfc.w();
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
        float float25 = this.m00 * float28 + this.m10 * float27 + this.m20 * float26 + this.m30;
        float float29 = this.m01 * float28 + this.m11 * float27 + this.m21 * float26 + this.m31;
        float float30 = this.m02 * float28 + this.m12 * float27 + this.m22 * float26 + this.m32;
        float float31 = this.m00 * float16 + this.m10 * float17 + this.m20 * float18;
        float float32 = this.m01 * float16 + this.m11 * float17 + this.m21 * float18;
        float float33 = this.m02 * float16 + this.m12 * float17 + this.m22 * float18;
        float float34 = this.m00 * float19 + this.m10 * float20 + this.m20 * float21;
        float float35 = this.m01 * float19 + this.m11 * float20 + this.m21 * float21;
        float float36 = this.m02 * float19 + this.m12 * float20 + this.m22 * float21;
        matrix4x3f1._m20(this.m00 * float22 + this.m10 * float23 + this.m20 * float24)
            ._m21(this.m01 * float22 + this.m11 * float23 + this.m21 * float24)
            ._m22(this.m02 * float22 + this.m12 * float23 + this.m22 * float24)
            ._m00(float31)
            ._m01(float32)
            ._m02(float33)
            ._m10(float34)
            ._m11(float35)
            ._m12(float36)
            ._m30(-float31 * float28 - float34 * float27 - this.m20 * float26 + float25)
            ._m31(-float32 * float28 - float35 * float27 - this.m21 * float26 + float29)
            ._m32(-float33 * float28 - float36 * float27 - this.m22 * float26 + float30)
            ._properties(this.properties & -13);
        return matrix4x3f1;
    }

    @Override
    public Matrix4x3f rotateAround(Quaternionfc arg0, float arg1, float arg2, float arg3, Matrix4x3f arg4) {
        return (this.properties & 4) != 0 ? this.rotationAround(arg0, arg1, arg2, arg3) : this.rotateAroundAffine(arg0, arg1, arg2, arg3, arg4);
    }

    public Matrix4x3f rotationAround(Quaternionfc arg0, float arg1, float arg2, float arg3) {
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
        this._m20(float11 + float9);
        this._m21(float13 - float15);
        this._m22(float3 - float2 - float1 + float0);
        this._m00(float0 + float1 - float3 - float2);
        this._m01(float7 + float5);
        this._m02(float9 - float11);
        this._m10(float7 - float5);
        this._m11(float2 - float3 + float0 - float1);
        this._m12(float13 + float15);
        this._m30(-this.m00 * arg1 - this.m10 * arg2 - this.m20 * arg3 + arg1);
        this._m31(-this.m01 * arg1 - this.m11 * arg2 - this.m21 * arg3 + arg2);
        this._m32(-this.m02 * arg1 - this.m12 * arg2 - this.m22 * arg3 + arg3);
        this.properties = 16;
        return this;
    }

    @Override
    public Matrix4x3f rotateLocal(float arg0, float arg1, float arg2, float arg3, Matrix4x3f arg4) {
        if (arg2 == 0.0F && arg3 == 0.0F && Math.absEqualsOne(arg1)) {
            return this.rotateLocalX(arg1 * arg0, arg4);
        } else if (arg1 == 0.0F && arg3 == 0.0F && Math.absEqualsOne(arg2)) {
            return this.rotateLocalY(arg2 * arg0, arg4);
        } else {
            return arg1 == 0.0F && arg2 == 0.0F && Math.absEqualsOne(arg3)
                ? this.rotateLocalZ(arg3 * arg0, arg4)
                : this.rotateLocalInternal(arg0, arg1, arg2, arg3, arg4);
        }
    }

    private Matrix4x3f rotateLocalInternal(float float1, float float5, float float7, float float9, Matrix4x3f matrix4x3f1) {
        float float0 = Math.sin(float1);
        float float2 = Math.cosFromSin(float0, float1);
        float float3 = 1.0F - float2;
        float float4 = float5 * float5;
        float float6 = float5 * float7;
        float float8 = float5 * float9;
        float float10 = float7 * float7;
        float float11 = float7 * float9;
        float float12 = float9 * float9;
        float float13 = float4 * float3 + float2;
        float float14 = float6 * float3 + float9 * float0;
        float float15 = float8 * float3 - float7 * float0;
        float float16 = float6 * float3 - float9 * float0;
        float float17 = float10 * float3 + float2;
        float float18 = float11 * float3 + float5 * float0;
        float float19 = float8 * float3 + float7 * float0;
        float float20 = float11 * float3 - float5 * float0;
        float float21 = float12 * float3 + float2;
        float float22 = float13 * this.m00 + float16 * this.m01 + float19 * this.m02;
        float float23 = float14 * this.m00 + float17 * this.m01 + float20 * this.m02;
        float float24 = float15 * this.m00 + float18 * this.m01 + float21 * this.m02;
        float float25 = float13 * this.m10 + float16 * this.m11 + float19 * this.m12;
        float float26 = float14 * this.m10 + float17 * this.m11 + float20 * this.m12;
        float float27 = float15 * this.m10 + float18 * this.m11 + float21 * this.m12;
        float float28 = float13 * this.m20 + float16 * this.m21 + float19 * this.m22;
        float float29 = float14 * this.m20 + float17 * this.m21 + float20 * this.m22;
        float float30 = float15 * this.m20 + float18 * this.m21 + float21 * this.m22;
        float float31 = float13 * this.m30 + float16 * this.m31 + float19 * this.m32;
        float float32 = float14 * this.m30 + float17 * this.m31 + float20 * this.m32;
        float float33 = float15 * this.m30 + float18 * this.m31 + float21 * this.m32;
        matrix4x3f1.m00 = float22;
        matrix4x3f1.m01 = float23;
        matrix4x3f1.m02 = float24;
        matrix4x3f1.m10 = float25;
        matrix4x3f1.m11 = float26;
        matrix4x3f1.m12 = float27;
        matrix4x3f1.m20 = float28;
        matrix4x3f1.m21 = float29;
        matrix4x3f1.m22 = float30;
        matrix4x3f1.m30 = float31;
        matrix4x3f1.m31 = float32;
        matrix4x3f1.m32 = float33;
        matrix4x3f1.properties = this.properties & -13;
        return matrix4x3f1;
    }

    public Matrix4x3f rotateLocal(float arg0, float arg1, float arg2, float arg3) {
        return this.rotateLocal(arg0, arg1, arg2, arg3, this);
    }

    public Matrix4x3f rotateLocalX(float arg0, Matrix4x3f arg1) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = float1 * this.m01 - float0 * this.m02;
        float float3 = float0 * this.m01 + float1 * this.m02;
        float float4 = float1 * this.m11 - float0 * this.m12;
        float float5 = float0 * this.m11 + float1 * this.m12;
        float float6 = float1 * this.m21 - float0 * this.m22;
        float float7 = float0 * this.m21 + float1 * this.m22;
        float float8 = float1 * this.m31 - float0 * this.m32;
        float float9 = float0 * this.m31 + float1 * this.m32;
        arg1._m00(this.m00)
            ._m01(float2)
            ._m02(float3)
            ._m10(this.m10)
            ._m11(float4)
            ._m12(float5)
            ._m20(this.m20)
            ._m21(float6)
            ._m22(float7)
            ._m30(this.m30)
            ._m31(float8)
            ._m32(float9)
            ._properties(this.properties & -13);
        return arg1;
    }

    public Matrix4x3f rotateLocalX(float arg0) {
        return this.rotateLocalX(arg0, this);
    }

    public Matrix4x3f rotateLocalY(float arg0, Matrix4x3f arg1) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = float1 * this.m00 + float0 * this.m02;
        float float3 = -float0 * this.m00 + float1 * this.m02;
        float float4 = float1 * this.m10 + float0 * this.m12;
        float float5 = -float0 * this.m10 + float1 * this.m12;
        float float6 = float1 * this.m20 + float0 * this.m22;
        float float7 = -float0 * this.m20 + float1 * this.m22;
        float float8 = float1 * this.m30 + float0 * this.m32;
        float float9 = -float0 * this.m30 + float1 * this.m32;
        arg1._m00(float2)
            ._m01(this.m01)
            ._m02(float3)
            ._m10(float4)
            ._m11(this.m11)
            ._m12(float5)
            ._m20(float6)
            ._m21(this.m21)
            ._m22(float7)
            ._m30(float8)
            ._m31(this.m31)
            ._m32(float9)
            ._properties(this.properties & -13);
        return arg1;
    }

    public Matrix4x3f rotateLocalY(float arg0) {
        return this.rotateLocalY(arg0, this);
    }

    public Matrix4x3f rotateLocalZ(float arg0, Matrix4x3f arg1) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = float1 * this.m00 - float0 * this.m01;
        float float3 = float0 * this.m00 + float1 * this.m01;
        float float4 = float1 * this.m10 - float0 * this.m11;
        float float5 = float0 * this.m10 + float1 * this.m11;
        float float6 = float1 * this.m20 - float0 * this.m21;
        float float7 = float0 * this.m20 + float1 * this.m21;
        float float8 = float1 * this.m30 - float0 * this.m31;
        float float9 = float0 * this.m30 + float1 * this.m31;
        arg1._m00(float2)
            ._m01(float3)
            ._m02(this.m02)
            ._m10(float4)
            ._m11(float5)
            ._m12(this.m12)
            ._m20(float6)
            ._m21(float7)
            ._m22(this.m22)
            ._m30(float8)
            ._m31(float9)
            ._m32(this.m32)
            ._properties(this.properties & -13);
        return arg1;
    }

    public Matrix4x3f rotateLocalZ(float arg0) {
        return this.rotateLocalZ(arg0, this);
    }

    public Matrix4x3f translate(Vector3fc arg0) {
        return this.translate(arg0.x(), arg0.y(), arg0.z());
    }

    @Override
    public Matrix4x3f translate(Vector3fc arg0, Matrix4x3f arg1) {
        return this.translate(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Matrix4x3f translate(float arg0, float arg1, float arg2, Matrix4x3f arg3) {
        return (this.properties & 4) != 0 ? arg3.translation(arg0, arg1, arg2) : this.translateGeneric(arg0, arg1, arg2, arg3);
    }

    private Matrix4x3f translateGeneric(float float2, float float1, float float0, Matrix4x3f matrix4x3f1) {
        MemUtil.INSTANCE.copy(this, matrix4x3f1);
        matrix4x3f1.m30 = this.m00 * float2 + this.m10 * float1 + this.m20 * float0 + this.m30;
        matrix4x3f1.m31 = this.m01 * float2 + this.m11 * float1 + this.m21 * float0 + this.m31;
        matrix4x3f1.m32 = this.m02 * float2 + this.m12 * float1 + this.m22 * float0 + this.m32;
        matrix4x3f1.properties = this.properties & -5;
        return matrix4x3f1;
    }

    public Matrix4x3f translate(float arg0, float arg1, float arg2) {
        if ((this.properties & 4) != 0) {
            return this.translation(arg0, arg1, arg2);
        } else {
            this.m30 = this.m00 * arg0 + this.m10 * arg1 + this.m20 * arg2 + this.m30;
            this.m31 = this.m01 * arg0 + this.m11 * arg1 + this.m21 * arg2 + this.m31;
            this.m32 = this.m02 * arg0 + this.m12 * arg1 + this.m22 * arg2 + this.m32;
            this.properties &= -5;
            return this;
        }
    }

    public Matrix4x3f translateLocal(Vector3fc arg0) {
        return this.translateLocal(arg0.x(), arg0.y(), arg0.z());
    }

    @Override
    public Matrix4x3f translateLocal(Vector3fc arg0, Matrix4x3f arg1) {
        return this.translateLocal(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Matrix4x3f translateLocal(float arg0, float arg1, float arg2, Matrix4x3f arg3) {
        arg3.m00 = this.m00;
        arg3.m01 = this.m01;
        arg3.m02 = this.m02;
        arg3.m10 = this.m10;
        arg3.m11 = this.m11;
        arg3.m12 = this.m12;
        arg3.m20 = this.m20;
        arg3.m21 = this.m21;
        arg3.m22 = this.m22;
        arg3.m30 = this.m30 + arg0;
        arg3.m31 = this.m31 + arg1;
        arg3.m32 = this.m32 + arg2;
        arg3.properties = this.properties & -5;
        return arg3;
    }

    public Matrix4x3f translateLocal(float arg0, float arg1, float arg2) {
        return this.translateLocal(arg0, arg1, arg2, this);
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
        arg0.writeFloat(this.m30);
        arg0.writeFloat(this.m31);
        arg0.writeFloat(this.m32);
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
        this.m30 = arg0.readFloat();
        this.m31 = arg0.readFloat();
        this.m32 = arg0.readFloat();
        this.determineProperties();
    }

    @Override
    public Matrix4x3f ortho(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6, Matrix4x3f arg7) {
        float float0 = 2.0F / (arg1 - arg0);
        float float1 = 2.0F / (arg3 - arg2);
        float float2 = (arg6 ? 1.0F : 2.0F) / (arg4 - arg5);
        float float3 = (arg0 + arg1) / (arg0 - arg1);
        float float4 = (arg3 + arg2) / (arg2 - arg3);
        float float5 = (arg6 ? arg4 : arg5 + arg4) / (arg4 - arg5);
        arg7.m30 = this.m00 * float3 + this.m10 * float4 + this.m20 * float5 + this.m30;
        arg7.m31 = this.m01 * float3 + this.m11 * float4 + this.m21 * float5 + this.m31;
        arg7.m32 = this.m02 * float3 + this.m12 * float4 + this.m22 * float5 + this.m32;
        arg7.m00 = this.m00 * float0;
        arg7.m01 = this.m01 * float0;
        arg7.m02 = this.m02 * float0;
        arg7.m10 = this.m10 * float1;
        arg7.m11 = this.m11 * float1;
        arg7.m12 = this.m12 * float1;
        arg7.m20 = this.m20 * float2;
        arg7.m21 = this.m21 * float2;
        arg7.m22 = this.m22 * float2;
        arg7.properties = this.properties & -29;
        return arg7;
    }

    @Override
    public Matrix4x3f ortho(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4x3f arg6) {
        return this.ortho(arg0, arg1, arg2, arg3, arg4, arg5, false, arg6);
    }

    public Matrix4x3f ortho(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6) {
        return this.ortho(arg0, arg1, arg2, arg3, arg4, arg5, arg6, this);
    }

    public Matrix4x3f ortho(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.ortho(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    @Override
    public Matrix4x3f orthoLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6, Matrix4x3f arg7) {
        float float0 = 2.0F / (arg1 - arg0);
        float float1 = 2.0F / (arg3 - arg2);
        float float2 = (arg6 ? 1.0F : 2.0F) / (arg5 - arg4);
        float float3 = (arg0 + arg1) / (arg0 - arg1);
        float float4 = (arg3 + arg2) / (arg2 - arg3);
        float float5 = (arg6 ? arg4 : arg5 + arg4) / (arg4 - arg5);
        arg7.m30 = this.m00 * float3 + this.m10 * float4 + this.m20 * float5 + this.m30;
        arg7.m31 = this.m01 * float3 + this.m11 * float4 + this.m21 * float5 + this.m31;
        arg7.m32 = this.m02 * float3 + this.m12 * float4 + this.m22 * float5 + this.m32;
        arg7.m00 = this.m00 * float0;
        arg7.m01 = this.m01 * float0;
        arg7.m02 = this.m02 * float0;
        arg7.m10 = this.m10 * float1;
        arg7.m11 = this.m11 * float1;
        arg7.m12 = this.m12 * float1;
        arg7.m20 = this.m20 * float2;
        arg7.m21 = this.m21 * float2;
        arg7.m22 = this.m22 * float2;
        arg7.properties = this.properties & -29;
        return arg7;
    }

    @Override
    public Matrix4x3f orthoLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4x3f arg6) {
        return this.orthoLH(arg0, arg1, arg2, arg3, arg4, arg5, false, arg6);
    }

    public Matrix4x3f orthoLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6) {
        return this.orthoLH(arg0, arg1, arg2, arg3, arg4, arg5, arg6, this);
    }

    public Matrix4x3f orthoLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.orthoLH(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    public Matrix4x3f setOrtho(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6) {
        MemUtil.INSTANCE.identity(this);
        this.m00 = 2.0F / (arg1 - arg0);
        this.m11 = 2.0F / (arg3 - arg2);
        this.m22 = (arg6 ? 1.0F : 2.0F) / (arg4 - arg5);
        this.m30 = (arg1 + arg0) / (arg0 - arg1);
        this.m31 = (arg3 + arg2) / (arg2 - arg3);
        this.m32 = (arg6 ? arg4 : arg5 + arg4) / (arg4 - arg5);
        this.properties = 0;
        return this;
    }

    public Matrix4x3f setOrtho(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.setOrtho(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    public Matrix4x3f setOrthoLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6) {
        MemUtil.INSTANCE.identity(this);
        this.m00 = 2.0F / (arg1 - arg0);
        this.m11 = 2.0F / (arg3 - arg2);
        this.m22 = (arg6 ? 1.0F : 2.0F) / (arg5 - arg4);
        this.m30 = (arg1 + arg0) / (arg0 - arg1);
        this.m31 = (arg3 + arg2) / (arg2 - arg3);
        this.m32 = (arg6 ? arg4 : arg5 + arg4) / (arg4 - arg5);
        this.properties = 0;
        return this;
    }

    public Matrix4x3f setOrthoLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.setOrthoLH(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    @Override
    public Matrix4x3f orthoSymmetric(float arg0, float arg1, float arg2, float arg3, boolean arg4, Matrix4x3f arg5) {
        float float0 = 2.0F / arg0;
        float float1 = 2.0F / arg1;
        float float2 = (arg4 ? 1.0F : 2.0F) / (arg2 - arg3);
        float float3 = (arg4 ? arg2 : arg3 + arg2) / (arg2 - arg3);
        arg5.m30 = this.m20 * float3 + this.m30;
        arg5.m31 = this.m21 * float3 + this.m31;
        arg5.m32 = this.m22 * float3 + this.m32;
        arg5.m00 = this.m00 * float0;
        arg5.m01 = this.m01 * float0;
        arg5.m02 = this.m02 * float0;
        arg5.m10 = this.m10 * float1;
        arg5.m11 = this.m11 * float1;
        arg5.m12 = this.m12 * float1;
        arg5.m20 = this.m20 * float2;
        arg5.m21 = this.m21 * float2;
        arg5.m22 = this.m22 * float2;
        arg5.properties = this.properties & -29;
        return arg5;
    }

    @Override
    public Matrix4x3f orthoSymmetric(float arg0, float arg1, float arg2, float arg3, Matrix4x3f arg4) {
        return this.orthoSymmetric(arg0, arg1, arg2, arg3, false, arg4);
    }

    public Matrix4x3f orthoSymmetric(float arg0, float arg1, float arg2, float arg3, boolean arg4) {
        return this.orthoSymmetric(arg0, arg1, arg2, arg3, arg4, this);
    }

    public Matrix4x3f orthoSymmetric(float arg0, float arg1, float arg2, float arg3) {
        return this.orthoSymmetric(arg0, arg1, arg2, arg3, false, this);
    }

    @Override
    public Matrix4x3f orthoSymmetricLH(float arg0, float arg1, float arg2, float arg3, boolean arg4, Matrix4x3f arg5) {
        float float0 = 2.0F / arg0;
        float float1 = 2.0F / arg1;
        float float2 = (arg4 ? 1.0F : 2.0F) / (arg3 - arg2);
        float float3 = (arg4 ? arg2 : arg3 + arg2) / (arg2 - arg3);
        arg5.m30 = this.m20 * float3 + this.m30;
        arg5.m31 = this.m21 * float3 + this.m31;
        arg5.m32 = this.m22 * float3 + this.m32;
        arg5.m00 = this.m00 * float0;
        arg5.m01 = this.m01 * float0;
        arg5.m02 = this.m02 * float0;
        arg5.m10 = this.m10 * float1;
        arg5.m11 = this.m11 * float1;
        arg5.m12 = this.m12 * float1;
        arg5.m20 = this.m20 * float2;
        arg5.m21 = this.m21 * float2;
        arg5.m22 = this.m22 * float2;
        arg5.properties = this.properties & -29;
        return arg5;
    }

    @Override
    public Matrix4x3f orthoSymmetricLH(float arg0, float arg1, float arg2, float arg3, Matrix4x3f arg4) {
        return this.orthoSymmetricLH(arg0, arg1, arg2, arg3, false, arg4);
    }

    public Matrix4x3f orthoSymmetricLH(float arg0, float arg1, float arg2, float arg3, boolean arg4) {
        return this.orthoSymmetricLH(arg0, arg1, arg2, arg3, arg4, this);
    }

    public Matrix4x3f orthoSymmetricLH(float arg0, float arg1, float arg2, float arg3) {
        return this.orthoSymmetricLH(arg0, arg1, arg2, arg3, false, this);
    }

    public Matrix4x3f setOrthoSymmetric(float arg0, float arg1, float arg2, float arg3, boolean arg4) {
        MemUtil.INSTANCE.identity(this);
        this.m00 = 2.0F / arg0;
        this.m11 = 2.0F / arg1;
        this.m22 = (arg4 ? 1.0F : 2.0F) / (arg2 - arg3);
        this.m32 = (arg4 ? arg2 : arg3 + arg2) / (arg2 - arg3);
        this.properties = 0;
        return this;
    }

    public Matrix4x3f setOrthoSymmetric(float arg0, float arg1, float arg2, float arg3) {
        return this.setOrthoSymmetric(arg0, arg1, arg2, arg3, false);
    }

    public Matrix4x3f setOrthoSymmetricLH(float arg0, float arg1, float arg2, float arg3, boolean arg4) {
        MemUtil.INSTANCE.identity(this);
        this.m00 = 2.0F / arg0;
        this.m11 = 2.0F / arg1;
        this.m22 = (arg4 ? 1.0F : 2.0F) / (arg3 - arg2);
        this.m32 = (arg4 ? arg2 : arg3 + arg2) / (arg2 - arg3);
        this.properties = 0;
        return this;
    }

    public Matrix4x3f setOrthoSymmetricLH(float arg0, float arg1, float arg2, float arg3) {
        return this.setOrthoSymmetricLH(arg0, arg1, arg2, arg3, false);
    }

    @Override
    public Matrix4x3f ortho2D(float arg0, float arg1, float arg2, float arg3, Matrix4x3f arg4) {
        float float0 = 2.0F / (arg1 - arg0);
        float float1 = 2.0F / (arg3 - arg2);
        float float2 = -(arg1 + arg0) / (arg1 - arg0);
        float float3 = -(arg3 + arg2) / (arg3 - arg2);
        arg4.m30 = this.m00 * float2 + this.m10 * float3 + this.m30;
        arg4.m31 = this.m01 * float2 + this.m11 * float3 + this.m31;
        arg4.m32 = this.m02 * float2 + this.m12 * float3 + this.m32;
        arg4.m00 = this.m00 * float0;
        arg4.m01 = this.m01 * float0;
        arg4.m02 = this.m02 * float0;
        arg4.m10 = this.m10 * float1;
        arg4.m11 = this.m11 * float1;
        arg4.m12 = this.m12 * float1;
        arg4.m20 = -this.m20;
        arg4.m21 = -this.m21;
        arg4.m22 = -this.m22;
        arg4.properties = this.properties & -29;
        return arg4;
    }

    public Matrix4x3f ortho2D(float arg0, float arg1, float arg2, float arg3) {
        return this.ortho2D(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix4x3f ortho2DLH(float arg0, float arg1, float arg2, float arg3, Matrix4x3f arg4) {
        float float0 = 2.0F / (arg1 - arg0);
        float float1 = 2.0F / (arg3 - arg2);
        float float2 = -(arg1 + arg0) / (arg1 - arg0);
        float float3 = -(arg3 + arg2) / (arg3 - arg2);
        arg4.m30 = this.m00 * float2 + this.m10 * float3 + this.m30;
        arg4.m31 = this.m01 * float2 + this.m11 * float3 + this.m31;
        arg4.m32 = this.m02 * float2 + this.m12 * float3 + this.m32;
        arg4.m00 = this.m00 * float0;
        arg4.m01 = this.m01 * float0;
        arg4.m02 = this.m02 * float0;
        arg4.m10 = this.m10 * float1;
        arg4.m11 = this.m11 * float1;
        arg4.m12 = this.m12 * float1;
        arg4.m20 = this.m20;
        arg4.m21 = this.m21;
        arg4.m22 = this.m22;
        arg4.properties = this.properties & -29;
        return arg4;
    }

    public Matrix4x3f ortho2DLH(float arg0, float arg1, float arg2, float arg3) {
        return this.ortho2DLH(arg0, arg1, arg2, arg3, this);
    }

    public Matrix4x3f setOrtho2D(float arg0, float arg1, float arg2, float arg3) {
        MemUtil.INSTANCE.identity(this);
        this.m00 = 2.0F / (arg1 - arg0);
        this.m11 = 2.0F / (arg3 - arg2);
        this.m22 = -1.0F;
        this.m30 = -(arg1 + arg0) / (arg1 - arg0);
        this.m31 = -(arg3 + arg2) / (arg3 - arg2);
        this.properties = 0;
        return this;
    }

    public Matrix4x3f setOrtho2DLH(float arg0, float arg1, float arg2, float arg3) {
        MemUtil.INSTANCE.identity(this);
        this.m00 = 2.0F / (arg1 - arg0);
        this.m11 = 2.0F / (arg3 - arg2);
        this.m22 = 1.0F;
        this.m30 = -(arg1 + arg0) / (arg1 - arg0);
        this.m31 = -(arg3 + arg2) / (arg3 - arg2);
        this.properties = 0;
        return this;
    }

    public Matrix4x3f lookAlong(Vector3fc arg0, Vector3fc arg1) {
        return this.lookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), this);
    }

    @Override
    public Matrix4x3f lookAlong(Vector3fc arg0, Vector3fc arg1, Matrix4x3f arg2) {
        return this.lookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    @Override
    public Matrix4x3f lookAlong(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4x3f arg6) {
        if ((this.properties & 4) != 0) {
            return this.setLookAlong(arg0, arg1, arg2, arg3, arg4, arg5);
        } else {
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
            arg6.m30 = this.m30;
            arg6.m31 = this.m31;
            arg6.m32 = this.m32;
            arg6.properties = this.properties & -13;
            return arg6;
        }
    }

    public Matrix4x3f lookAlong(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.lookAlong(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    public Matrix4x3f setLookAlong(Vector3fc arg0, Vector3fc arg1) {
        return this.setLookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix4x3f setLookAlong(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
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
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.properties = 16;
        return this;
    }

    public Matrix4x3f setLookAt(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2) {
        return this.setLookAt(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z());
    }

    public Matrix4x3f setLookAt(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8) {
        float float0 = arg0 - arg3;
        float float1 = arg1 - arg4;
        float float2 = arg2 - arg5;
        float float3 = Math.invsqrt(float0 * float0 + float1 * float1 + float2 * float2);
        float0 *= float3;
        float1 *= float3;
        float2 *= float3;
        float float4 = arg7 * float2 - arg8 * float1;
        float float5 = arg8 * float0 - arg6 * float2;
        float float6 = arg6 * float1 - arg7 * float0;
        float float7 = Math.invsqrt(float4 * float4 + float5 * float5 + float6 * float6);
        float4 *= float7;
        float5 *= float7;
        float6 *= float7;
        float float8 = float1 * float6 - float2 * float5;
        float float9 = float2 * float4 - float0 * float6;
        float float10 = float0 * float5 - float1 * float4;
        this.m00 = float4;
        this.m01 = float8;
        this.m02 = float0;
        this.m10 = float5;
        this.m11 = float9;
        this.m12 = float1;
        this.m20 = float6;
        this.m21 = float10;
        this.m22 = float2;
        this.m30 = -(float4 * arg0 + float5 * arg1 + float6 * arg2);
        this.m31 = -(float8 * arg0 + float9 * arg1 + float10 * arg2);
        this.m32 = -(float0 * arg0 + float1 * arg1 + float2 * arg2);
        this.properties = 16;
        return this;
    }

    @Override
    public Matrix4x3f lookAt(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2, Matrix4x3f arg3) {
        return this.lookAt(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z(), arg3);
    }

    public Matrix4x3f lookAt(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2) {
        return this.lookAt(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z(), this);
    }

    @Override
    public Matrix4x3f lookAt(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8, Matrix4x3f arg9) {
        return (this.properties & 4) != 0
            ? arg9.setLookAt(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
            : this.lookAtGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
    }

    private Matrix4x3f lookAtGeneric(
        float float1, float float4, float float7, float float2, float float5, float float8, float float14, float float12, float float11, Matrix4x3f matrix4x3f1
    ) {
        float float0 = float1 - float2;
        float float3 = float4 - float5;
        float float6 = float7 - float8;
        float float9 = Math.invsqrt(float0 * float0 + float3 * float3 + float6 * float6);
        float0 *= float9;
        float3 *= float9;
        float6 *= float9;
        float float10 = float12 * float6 - float11 * float3;
        float float13 = float11 * float0 - float14 * float6;
        float float15 = float14 * float3 - float12 * float0;
        float float16 = Math.invsqrt(float10 * float10 + float13 * float13 + float15 * float15);
        float10 *= float16;
        float13 *= float16;
        float15 *= float16;
        float float17 = float3 * float15 - float6 * float13;
        float float18 = float6 * float10 - float0 * float15;
        float float19 = float0 * float13 - float3 * float10;
        float float20 = -(float10 * float1 + float13 * float4 + float15 * float7);
        float float21 = -(float17 * float1 + float18 * float4 + float19 * float7);
        float float22 = -(float0 * float1 + float3 * float4 + float6 * float7);
        matrix4x3f1.m30 = this.m00 * float20 + this.m10 * float21 + this.m20 * float22 + this.m30;
        matrix4x3f1.m31 = this.m01 * float20 + this.m11 * float21 + this.m21 * float22 + this.m31;
        matrix4x3f1.m32 = this.m02 * float20 + this.m12 * float21 + this.m22 * float22 + this.m32;
        float float23 = this.m00 * float10 + this.m10 * float17 + this.m20 * float0;
        float float24 = this.m01 * float10 + this.m11 * float17 + this.m21 * float0;
        float float25 = this.m02 * float10 + this.m12 * float17 + this.m22 * float0;
        float float26 = this.m00 * float13 + this.m10 * float18 + this.m20 * float3;
        float float27 = this.m01 * float13 + this.m11 * float18 + this.m21 * float3;
        float float28 = this.m02 * float13 + this.m12 * float18 + this.m22 * float3;
        matrix4x3f1.m20 = this.m00 * float15 + this.m10 * float19 + this.m20 * float6;
        matrix4x3f1.m21 = this.m01 * float15 + this.m11 * float19 + this.m21 * float6;
        matrix4x3f1.m22 = this.m02 * float15 + this.m12 * float19 + this.m22 * float6;
        matrix4x3f1.m00 = float23;
        matrix4x3f1.m01 = float24;
        matrix4x3f1.m02 = float25;
        matrix4x3f1.m10 = float26;
        matrix4x3f1.m11 = float27;
        matrix4x3f1.m12 = float28;
        matrix4x3f1.properties = this.properties & -13;
        return matrix4x3f1;
    }

    public Matrix4x3f lookAt(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8) {
        return this.lookAt(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, this);
    }

    public Matrix4x3f setLookAtLH(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2) {
        return this.setLookAtLH(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z());
    }

    public Matrix4x3f setLookAtLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8) {
        float float0 = arg3 - arg0;
        float float1 = arg4 - arg1;
        float float2 = arg5 - arg2;
        float float3 = Math.invsqrt(float0 * float0 + float1 * float1 + float2 * float2);
        float0 *= float3;
        float1 *= float3;
        float2 *= float3;
        float float4 = arg7 * float2 - arg8 * float1;
        float float5 = arg8 * float0 - arg6 * float2;
        float float6 = arg6 * float1 - arg7 * float0;
        float float7 = Math.invsqrt(float4 * float4 + float5 * float5 + float6 * float6);
        float4 *= float7;
        float5 *= float7;
        float6 *= float7;
        float float8 = float1 * float6 - float2 * float5;
        float float9 = float2 * float4 - float0 * float6;
        float float10 = float0 * float5 - float1 * float4;
        this.m00 = float4;
        this.m01 = float8;
        this.m02 = float0;
        this.m10 = float5;
        this.m11 = float9;
        this.m12 = float1;
        this.m20 = float6;
        this.m21 = float10;
        this.m22 = float2;
        this.m30 = -(float4 * arg0 + float5 * arg1 + float6 * arg2);
        this.m31 = -(float8 * arg0 + float9 * arg1 + float10 * arg2);
        this.m32 = -(float0 * arg0 + float1 * arg1 + float2 * arg2);
        this.properties = 16;
        return this;
    }

    @Override
    public Matrix4x3f lookAtLH(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2, Matrix4x3f arg3) {
        return this.lookAtLH(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z(), arg3);
    }

    public Matrix4x3f lookAtLH(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2) {
        return this.lookAtLH(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z(), this);
    }

    @Override
    public Matrix4x3f lookAtLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8, Matrix4x3f arg9) {
        return (this.properties & 4) != 0
            ? arg9.setLookAtLH(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
            : this.lookAtLHGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
    }

    private Matrix4x3f lookAtLHGeneric(
        float float2, float float5, float float8, float float1, float float4, float float7, float float14, float float12, float float11, Matrix4x3f matrix4x3f1
    ) {
        float float0 = float1 - float2;
        float float3 = float4 - float5;
        float float6 = float7 - float8;
        float float9 = Math.invsqrt(float0 * float0 + float3 * float3 + float6 * float6);
        float0 *= float9;
        float3 *= float9;
        float6 *= float9;
        float float10 = float12 * float6 - float11 * float3;
        float float13 = float11 * float0 - float14 * float6;
        float float15 = float14 * float3 - float12 * float0;
        float float16 = Math.invsqrt(float10 * float10 + float13 * float13 + float15 * float15);
        float10 *= float16;
        float13 *= float16;
        float15 *= float16;
        float float17 = float3 * float15 - float6 * float13;
        float float18 = float6 * float10 - float0 * float15;
        float float19 = float0 * float13 - float3 * float10;
        float float20 = -(float10 * float2 + float13 * float5 + float15 * float8);
        float float21 = -(float17 * float2 + float18 * float5 + float19 * float8);
        float float22 = -(float0 * float2 + float3 * float5 + float6 * float8);
        matrix4x3f1.m30 = this.m00 * float20 + this.m10 * float21 + this.m20 * float22 + this.m30;
        matrix4x3f1.m31 = this.m01 * float20 + this.m11 * float21 + this.m21 * float22 + this.m31;
        matrix4x3f1.m32 = this.m02 * float20 + this.m12 * float21 + this.m22 * float22 + this.m32;
        float float23 = this.m00 * float10 + this.m10 * float17 + this.m20 * float0;
        float float24 = this.m01 * float10 + this.m11 * float17 + this.m21 * float0;
        float float25 = this.m02 * float10 + this.m12 * float17 + this.m22 * float0;
        float float26 = this.m00 * float13 + this.m10 * float18 + this.m20 * float3;
        float float27 = this.m01 * float13 + this.m11 * float18 + this.m21 * float3;
        float float28 = this.m02 * float13 + this.m12 * float18 + this.m22 * float3;
        matrix4x3f1.m20 = this.m00 * float15 + this.m10 * float19 + this.m20 * float6;
        matrix4x3f1.m21 = this.m01 * float15 + this.m11 * float19 + this.m21 * float6;
        matrix4x3f1.m22 = this.m02 * float15 + this.m12 * float19 + this.m22 * float6;
        matrix4x3f1.m00 = float23;
        matrix4x3f1.m01 = float24;
        matrix4x3f1.m02 = float25;
        matrix4x3f1.m10 = float26;
        matrix4x3f1.m11 = float27;
        matrix4x3f1.m12 = float28;
        matrix4x3f1.properties = this.properties & -13;
        return matrix4x3f1;
    }

    public Matrix4x3f lookAtLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8) {
        return this.lookAtLH(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, this);
    }

    @Override
    public Matrix4x3f rotate(Quaternionfc arg0, Matrix4x3f arg1) {
        if ((this.properties & 4) != 0) {
            return arg1.rotation(arg0);
        } else {
            return (this.properties & 8) != 0 ? this.rotateTranslation(arg0, arg1) : this.rotateGeneric(arg0, arg1);
        }
    }

    private Matrix4x3f rotateGeneric(Quaternionfc quaternionfc, Matrix4x3f matrix4x3f1) {
        float float0 = quaternionfc.w() * quaternionfc.w();
        float float1 = quaternionfc.x() * quaternionfc.x();
        float float2 = quaternionfc.y() * quaternionfc.y();
        float float3 = quaternionfc.z() * quaternionfc.z();
        float float4 = quaternionfc.z() * quaternionfc.w();
        float float5 = float4 + float4;
        float float6 = quaternionfc.x() * quaternionfc.y();
        float float7 = float6 + float6;
        float float8 = quaternionfc.x() * quaternionfc.z();
        float float9 = float8 + float8;
        float float10 = quaternionfc.y() * quaternionfc.w();
        float float11 = float10 + float10;
        float float12 = quaternionfc.y() * quaternionfc.z();
        float float13 = float12 + float12;
        float float14 = quaternionfc.x() * quaternionfc.w();
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
        matrix4x3f1.m20 = this.m00 * float22 + this.m10 * float23 + this.m20 * float24;
        matrix4x3f1.m21 = this.m01 * float22 + this.m11 * float23 + this.m21 * float24;
        matrix4x3f1.m22 = this.m02 * float22 + this.m12 * float23 + this.m22 * float24;
        matrix4x3f1.m00 = float25;
        matrix4x3f1.m01 = float26;
        matrix4x3f1.m02 = float27;
        matrix4x3f1.m10 = float28;
        matrix4x3f1.m11 = float29;
        matrix4x3f1.m12 = float30;
        matrix4x3f1.m30 = this.m30;
        matrix4x3f1.m31 = this.m31;
        matrix4x3f1.m32 = this.m32;
        matrix4x3f1.properties = this.properties & -13;
        return matrix4x3f1;
    }

    public Matrix4x3f rotate(Quaternionfc arg0) {
        return this.rotate(arg0, this);
    }

    @Override
    public Matrix4x3f rotateTranslation(Quaternionfc arg0, Matrix4x3f arg1) {
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
        arg1.m20 = float22;
        arg1.m21 = float23;
        arg1.m22 = float24;
        arg1.m00 = float16;
        arg1.m01 = float17;
        arg1.m02 = float18;
        arg1.m10 = float19;
        arg1.m11 = float20;
        arg1.m12 = float21;
        arg1.m30 = this.m30;
        arg1.m31 = this.m31;
        arg1.m32 = this.m32;
        arg1.properties = this.properties & -13;
        return arg1;
    }

    @Override
    public Matrix4x3f rotateLocal(Quaternionfc arg0, Matrix4x3f arg1) {
        float float0 = arg0.w() * arg0.w();
        float float1 = arg0.x() * arg0.x();
        float float2 = arg0.y() * arg0.y();
        float float3 = arg0.z() * arg0.z();
        float float4 = arg0.z() * arg0.w();
        float float5 = arg0.x() * arg0.y();
        float float6 = arg0.x() * arg0.z();
        float float7 = arg0.y() * arg0.w();
        float float8 = arg0.y() * arg0.z();
        float float9 = arg0.x() * arg0.w();
        float float10 = float0 + float1 - float3 - float2;
        float float11 = float5 + float4 + float4 + float5;
        float float12 = float6 - float7 + float6 - float7;
        float float13 = -float4 + float5 - float4 + float5;
        float float14 = float2 - float3 + float0 - float1;
        float float15 = float8 + float8 + float9 + float9;
        float float16 = float7 + float6 + float6 + float7;
        float float17 = float8 + float8 - float9 - float9;
        float float18 = float3 - float2 - float1 + float0;
        float float19 = float10 * this.m00 + float13 * this.m01 + float16 * this.m02;
        float float20 = float11 * this.m00 + float14 * this.m01 + float17 * this.m02;
        float float21 = float12 * this.m00 + float15 * this.m01 + float18 * this.m02;
        float float22 = float10 * this.m10 + float13 * this.m11 + float16 * this.m12;
        float float23 = float11 * this.m10 + float14 * this.m11 + float17 * this.m12;
        float float24 = float12 * this.m10 + float15 * this.m11 + float18 * this.m12;
        float float25 = float10 * this.m20 + float13 * this.m21 + float16 * this.m22;
        float float26 = float11 * this.m20 + float14 * this.m21 + float17 * this.m22;
        float float27 = float12 * this.m20 + float15 * this.m21 + float18 * this.m22;
        float float28 = float10 * this.m30 + float13 * this.m31 + float16 * this.m32;
        float float29 = float11 * this.m30 + float14 * this.m31 + float17 * this.m32;
        float float30 = float12 * this.m30 + float15 * this.m31 + float18 * this.m32;
        arg1.m00 = float19;
        arg1.m01 = float20;
        arg1.m02 = float21;
        arg1.m10 = float22;
        arg1.m11 = float23;
        arg1.m12 = float24;
        arg1.m20 = float25;
        arg1.m21 = float26;
        arg1.m22 = float27;
        arg1.m30 = float28;
        arg1.m31 = float29;
        arg1.m32 = float30;
        arg1.properties = this.properties & -13;
        return arg1;
    }

    public Matrix4x3f rotateLocal(Quaternionfc arg0) {
        return this.rotateLocal(arg0, this);
    }

    public Matrix4x3f rotate(AxisAngle4f arg0) {
        return this.rotate(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    @Override
    public Matrix4x3f rotate(AxisAngle4f arg0, Matrix4x3f arg1) {
        return this.rotate(arg0.angle, arg0.x, arg0.y, arg0.z, arg1);
    }

    public Matrix4x3f rotate(float arg0, Vector3fc arg1) {
        return this.rotate(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    @Override
    public Matrix4x3f rotate(float arg0, Vector3fc arg1, Matrix4x3f arg2) {
        return this.rotate(arg0, arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    @Override
    public Matrix4x3f reflect(float arg0, float arg1, float arg2, float arg3, Matrix4x3f arg4) {
        if ((this.properties & 4) != 0) {
            return arg4.reflection(arg0, arg1, arg2, arg3);
        } else {
            float float0 = arg0 + arg0;
            float float1 = arg1 + arg1;
            float float2 = arg2 + arg2;
            float float3 = arg3 + arg3;
            float float4 = 1.0F - float0 * arg0;
            float float5 = -float0 * arg1;
            float float6 = -float0 * arg2;
            float float7 = -float1 * arg0;
            float float8 = 1.0F - float1 * arg1;
            float float9 = -float1 * arg2;
            float float10 = -float2 * arg0;
            float float11 = -float2 * arg1;
            float float12 = 1.0F - float2 * arg2;
            float float13 = -float3 * arg0;
            float float14 = -float3 * arg1;
            float float15 = -float3 * arg2;
            arg4.m30 = this.m00 * float13 + this.m10 * float14 + this.m20 * float15 + this.m30;
            arg4.m31 = this.m01 * float13 + this.m11 * float14 + this.m21 * float15 + this.m31;
            arg4.m32 = this.m02 * float13 + this.m12 * float14 + this.m22 * float15 + this.m32;
            float float16 = this.m00 * float4 + this.m10 * float5 + this.m20 * float6;
            float float17 = this.m01 * float4 + this.m11 * float5 + this.m21 * float6;
            float float18 = this.m02 * float4 + this.m12 * float5 + this.m22 * float6;
            float float19 = this.m00 * float7 + this.m10 * float8 + this.m20 * float9;
            float float20 = this.m01 * float7 + this.m11 * float8 + this.m21 * float9;
            float float21 = this.m02 * float7 + this.m12 * float8 + this.m22 * float9;
            arg4.m20 = this.m00 * float10 + this.m10 * float11 + this.m20 * float12;
            arg4.m21 = this.m01 * float10 + this.m11 * float11 + this.m21 * float12;
            arg4.m22 = this.m02 * float10 + this.m12 * float11 + this.m22 * float12;
            arg4.m00 = float16;
            arg4.m01 = float17;
            arg4.m02 = float18;
            arg4.m10 = float19;
            arg4.m11 = float20;
            arg4.m12 = float21;
            arg4.properties = this.properties & -13;
            return arg4;
        }
    }

    public Matrix4x3f reflect(float arg0, float arg1, float arg2, float arg3) {
        return this.reflect(arg0, arg1, arg2, arg3, this);
    }

    public Matrix4x3f reflect(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.reflect(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    @Override
    public Matrix4x3f reflect(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4x3f arg6) {
        float float0 = Math.invsqrt(arg0 * arg0 + arg1 * arg1 + arg2 * arg2);
        float float1 = arg0 * float0;
        float float2 = arg1 * float0;
        float float3 = arg2 * float0;
        return this.reflect(float1, float2, float3, -float1 * arg3 - float2 * arg4 - float3 * arg5, arg6);
    }

    public Matrix4x3f reflect(Vector3fc arg0, Vector3fc arg1) {
        return this.reflect(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix4x3f reflect(Quaternionfc arg0, Vector3fc arg1) {
        return this.reflect(arg0, arg1, this);
    }

    @Override
    public Matrix4x3f reflect(Quaternionfc arg0, Vector3fc arg1, Matrix4x3f arg2) {
        double double0 = arg0.x() + arg0.x();
        double double1 = arg0.y() + arg0.y();
        double double2 = arg0.z() + arg0.z();
        float float0 = (float)(arg0.x() * double2 + arg0.w() * double1);
        float float1 = (float)(arg0.y() * double2 - arg0.w() * double0);
        float float2 = (float)(1.0 - (arg0.x() * double0 + arg0.y() * double1));
        return this.reflect(float0, float1, float2, arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    @Override
    public Matrix4x3f reflect(Vector3fc arg0, Vector3fc arg1, Matrix4x3f arg2) {
        return this.reflect(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    public Matrix4x3f reflection(float arg0, float arg1, float arg2, float arg3) {
        float float0 = arg0 + arg0;
        float float1 = arg1 + arg1;
        float float2 = arg2 + arg2;
        float float3 = arg3 + arg3;
        this.m00 = 1.0F - float0 * arg0;
        this.m01 = -float0 * arg1;
        this.m02 = -float0 * arg2;
        this.m10 = -float1 * arg0;
        this.m11 = 1.0F - float1 * arg1;
        this.m12 = -float1 * arg2;
        this.m20 = -float2 * arg0;
        this.m21 = -float2 * arg1;
        this.m22 = 1.0F - float2 * arg2;
        this.m30 = -float3 * arg0;
        this.m31 = -float3 * arg1;
        this.m32 = -float3 * arg2;
        this.properties = 16;
        return this;
    }

    public Matrix4x3f reflection(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        float float0 = Math.invsqrt(arg0 * arg0 + arg1 * arg1 + arg2 * arg2);
        float float1 = arg0 * float0;
        float float2 = arg1 * float0;
        float float3 = arg2 * float0;
        return this.reflection(float1, float2, float3, -float1 * arg3 - float2 * arg4 - float3 * arg5);
    }

    public Matrix4x3f reflection(Vector3fc arg0, Vector3fc arg1) {
        return this.reflection(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix4x3f reflection(Quaternionfc arg0, Vector3fc arg1) {
        double double0 = arg0.x() + arg0.x();
        double double1 = arg0.y() + arg0.y();
        double double2 = arg0.z() + arg0.z();
        float float0 = (float)(arg0.x() * double2 + arg0.w() * double1);
        float float1 = (float)(arg0.y() * double2 - arg0.w() * double0);
        float float2 = (float)(1.0 - (arg0.x() * double0 + arg0.y() * double1));
        return this.reflection(float0, float1, float2, arg1.x(), arg1.y(), arg1.z());
    }

    @Override
    public Vector4f getRow(int arg0, Vector4f arg1) throws IndexOutOfBoundsException {
        switch (arg0) {
            case 0:
                arg1.x = this.m00;
                arg1.y = this.m10;
                arg1.z = this.m20;
                arg1.w = this.m30;
                break;
            case 1:
                arg1.x = this.m01;
                arg1.y = this.m11;
                arg1.z = this.m21;
                arg1.w = this.m31;
                break;
            case 2:
                arg1.x = this.m02;
                arg1.y = this.m12;
                arg1.z = this.m22;
                arg1.w = this.m32;
                break;
            default:
                throw new IndexOutOfBoundsException();
        }

        return arg1;
    }

    public Matrix4x3f setRow(int arg0, Vector4fc arg1) throws IndexOutOfBoundsException {
        switch (arg0) {
            case 0:
                this.m00 = arg1.x();
                this.m10 = arg1.y();
                this.m20 = arg1.z();
                this.m30 = arg1.w();
                break;
            case 1:
                this.m01 = arg1.x();
                this.m11 = arg1.y();
                this.m21 = arg1.z();
                this.m31 = arg1.w();
                break;
            case 2:
                this.m02 = arg1.x();
                this.m12 = arg1.y();
                this.m22 = arg1.z();
                this.m32 = arg1.w();
                break;
            default:
                throw new IndexOutOfBoundsException();
        }

        this.properties = 0;
        return this;
    }

    @Override
    public Vector3f getColumn(int arg0, Vector3f arg1) throws IndexOutOfBoundsException {
        switch (arg0) {
            case 0:
                arg1.x = this.m00;
                arg1.y = this.m01;
                arg1.z = this.m02;
                break;
            case 1:
                arg1.x = this.m10;
                arg1.y = this.m11;
                arg1.z = this.m12;
                break;
            case 2:
                arg1.x = this.m20;
                arg1.y = this.m21;
                arg1.z = this.m22;
                break;
            case 3:
                arg1.x = this.m30;
                arg1.y = this.m31;
                arg1.z = this.m32;
                break;
            default:
                throw new IndexOutOfBoundsException();
        }

        return arg1;
    }

    public Matrix4x3f setColumn(int arg0, Vector3fc arg1) throws IndexOutOfBoundsException {
        switch (arg0) {
            case 0:
                this.m00 = arg1.x();
                this.m01 = arg1.y();
                this.m02 = arg1.z();
                break;
            case 1:
                this.m10 = arg1.x();
                this.m11 = arg1.y();
                this.m12 = arg1.z();
                break;
            case 2:
                this.m20 = arg1.x();
                this.m21 = arg1.y();
                this.m22 = arg1.z();
                break;
            case 3:
                this.m30 = arg1.x();
                this.m31 = arg1.y();
                this.m32 = arg1.z();
                break;
            default:
                throw new IndexOutOfBoundsException();
        }

        this.properties = 0;
        return this;
    }

    public Matrix4x3f normal() {
        return this.normal(this);
    }

    @Override
    public Matrix4x3f normal(Matrix4x3f arg0) {
        if ((this.properties & 4) != 0) {
            return arg0.identity();
        } else {
            return (this.properties & 16) != 0 ? this.normalOrthonormal(arg0) : this.normalGeneric(arg0);
        }
    }

    private Matrix4x3f normalOrthonormal(Matrix4x3f matrix4x3f0) {
        if (matrix4x3f0 != this) {
            matrix4x3f0.set(this);
        }

        return matrix4x3f0._properties(16);
    }

    private Matrix4x3f normalGeneric(Matrix4x3f matrix4x3f1) {
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
        matrix4x3f1.m00 = float8;
        matrix4x3f1.m01 = float9;
        matrix4x3f1.m02 = float10;
        matrix4x3f1.m10 = float11;
        matrix4x3f1.m11 = float12;
        matrix4x3f1.m12 = float13;
        matrix4x3f1.m20 = float14;
        matrix4x3f1.m21 = float15;
        matrix4x3f1.m22 = float16;
        matrix4x3f1.m30 = 0.0F;
        matrix4x3f1.m31 = 0.0F;
        matrix4x3f1.m32 = 0.0F;
        matrix4x3f1.properties = this.properties & -9;
        return matrix4x3f1;
    }

    @Override
    public Matrix3f normal(Matrix3f arg0) {
        return (this.properties & 16) != 0 ? this.normalOrthonormal(arg0) : this.normalGeneric(arg0);
    }

    private Matrix3f normalOrthonormal(Matrix3f matrix3f) {
        return matrix3f.set(this);
    }

    private Matrix3f normalGeneric(Matrix3f matrix3f) {
        float float0 = this.m00 * this.m11;
        float float1 = this.m01 * this.m10;
        float float2 = this.m02 * this.m10;
        float float3 = this.m00 * this.m12;
        float float4 = this.m01 * this.m12;
        float float5 = this.m02 * this.m11;
        float float6 = (float0 - float1) * this.m22 + (float2 - float3) * this.m21 + (float4 - float5) * this.m20;
        float float7 = 1.0F / float6;
        matrix3f.m00((this.m11 * this.m22 - this.m21 * this.m12) * float7);
        matrix3f.m01((this.m20 * this.m12 - this.m10 * this.m22) * float7);
        matrix3f.m02((this.m10 * this.m21 - this.m20 * this.m11) * float7);
        matrix3f.m10((this.m21 * this.m02 - this.m01 * this.m22) * float7);
        matrix3f.m11((this.m00 * this.m22 - this.m20 * this.m02) * float7);
        matrix3f.m12((this.m20 * this.m01 - this.m00 * this.m21) * float7);
        matrix3f.m20((float4 - float5) * float7);
        matrix3f.m21((float2 - float3) * float7);
        matrix3f.m22((float0 - float1) * float7);
        return matrix3f;
    }

    public Matrix4x3f cofactor3x3() {
        return this.cofactor3x3(this);
    }

    @Override
    public Matrix3f cofactor3x3(Matrix3f arg0) {
        arg0.m00 = this.m11 * this.m22 - this.m21 * this.m12;
        arg0.m01 = this.m20 * this.m12 - this.m10 * this.m22;
        arg0.m02 = this.m10 * this.m21 - this.m20 * this.m11;
        arg0.m10 = this.m21 * this.m02 - this.m01 * this.m22;
        arg0.m11 = this.m00 * this.m22 - this.m20 * this.m02;
        arg0.m12 = this.m20 * this.m01 - this.m00 * this.m21;
        arg0.m20 = this.m01 * this.m12 - this.m02 * this.m11;
        arg0.m21 = this.m02 * this.m10 - this.m00 * this.m12;
        arg0.m22 = this.m00 * this.m11 - this.m01 * this.m10;
        return arg0;
    }

    @Override
    public Matrix4x3f cofactor3x3(Matrix4x3f arg0) {
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
        arg0.m30 = 0.0F;
        arg0.m31 = 0.0F;
        arg0.m32 = 0.0F;
        arg0.properties = this.properties & -9;
        return arg0;
    }

    public Matrix4x3f normalize3x3() {
        return this.normalize3x3(this);
    }

    @Override
    public Matrix4x3f normalize3x3(Matrix4x3f arg0) {
        float float0 = Math.invsqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
        float float1 = Math.invsqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
        float float2 = Math.invsqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
        arg0.m00 = this.m00 * float0;
        arg0.m01 = this.m01 * float0;
        arg0.m02 = this.m02 * float0;
        arg0.m10 = this.m10 * float1;
        arg0.m11 = this.m11 * float1;
        arg0.m12 = this.m12 * float1;
        arg0.m20 = this.m20 * float2;
        arg0.m21 = this.m21 * float2;
        arg0.m22 = this.m22 * float2;
        arg0.properties = this.properties;
        return arg0;
    }

    @Override
    public Matrix3f normalize3x3(Matrix3f arg0) {
        float float0 = Math.invsqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
        float float1 = Math.invsqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
        float float2 = Math.invsqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
        arg0.m00(this.m00 * float0);
        arg0.m01(this.m01 * float0);
        arg0.m02(this.m02 * float0);
        arg0.m10(this.m10 * float1);
        arg0.m11(this.m11 * float1);
        arg0.m12(this.m12 * float1);
        arg0.m20(this.m20 * float2);
        arg0.m21(this.m21 * float2);
        arg0.m22(this.m22 * float2);
        return arg0;
    }

    @Override
    public Vector4f frustumPlane(int arg0, Vector4f arg1) {
        switch (arg0) {
            case 0:
                arg1.set(this.m00, this.m10, this.m20, 1.0F + this.m30).normalize();
                break;
            case 1:
                arg1.set(-this.m00, -this.m10, -this.m20, 1.0F - this.m30).normalize();
                break;
            case 2:
                arg1.set(this.m01, this.m11, this.m21, 1.0F + this.m31).normalize();
                break;
            case 3:
                arg1.set(-this.m01, -this.m11, -this.m21, 1.0F - this.m31).normalize();
                break;
            case 4:
                arg1.set(this.m02, this.m12, this.m22, 1.0F + this.m32).normalize();
                break;
            case 5:
                arg1.set(-this.m02, -this.m12, -this.m22, 1.0F - this.m32).normalize();
                break;
            default:
                throw new IllegalArgumentException("which");
        }

        return arg1;
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
    public Vector3f origin(Vector3f arg0) {
        float float0 = this.m00 * this.m11 - this.m01 * this.m10;
        float float1 = this.m00 * this.m12 - this.m02 * this.m10;
        float float2 = this.m01 * this.m12 - this.m02 * this.m11;
        float float3 = this.m20 * this.m31 - this.m21 * this.m30;
        float float4 = this.m20 * this.m32 - this.m22 * this.m30;
        float float5 = this.m21 * this.m32 - this.m22 * this.m31;
        arg0.x = -this.m10 * float5 + this.m11 * float4 - this.m12 * float3;
        arg0.y = this.m00 * float5 - this.m01 * float4 + this.m02 * float3;
        arg0.z = -this.m30 * float2 + this.m31 * float1 - this.m32 * float0;
        return arg0;
    }

    public Matrix4x3f shadow(Vector4fc arg0, float arg1, float arg2, float arg3, float arg4) {
        return this.shadow(arg0.x(), arg0.y(), arg0.z(), arg0.w(), arg1, arg2, arg3, arg4, this);
    }

    @Override
    public Matrix4x3f shadow(Vector4fc arg0, float arg1, float arg2, float arg3, float arg4, Matrix4x3f arg5) {
        return this.shadow(arg0.x(), arg0.y(), arg0.z(), arg0.w(), arg1, arg2, arg3, arg4, arg5);
    }

    public Matrix4x3f shadow(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7) {
        return this.shadow(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, this);
    }

    @Override
    public Matrix4x3f shadow(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, Matrix4x3f arg8) {
        float float0 = Math.invsqrt(arg4 * arg4 + arg5 * arg5 + arg6 * arg6);
        float float1 = arg4 * float0;
        float float2 = arg5 * float0;
        float float3 = arg6 * float0;
        float float4 = arg7 * float0;
        float float5 = float1 * arg0 + float2 * arg1 + float3 * arg2 + float4 * arg3;
        float float6 = float5 - float1 * arg0;
        float float7 = -float1 * arg1;
        float float8 = -float1 * arg2;
        float float9 = -float1 * arg3;
        float float10 = -float2 * arg0;
        float float11 = float5 - float2 * arg1;
        float float12 = -float2 * arg2;
        float float13 = -float2 * arg3;
        float float14 = -float3 * arg0;
        float float15 = -float3 * arg1;
        float float16 = float5 - float3 * arg2;
        float float17 = -float3 * arg3;
        float float18 = -float4 * arg0;
        float float19 = -float4 * arg1;
        float float20 = -float4 * arg2;
        float float21 = float5 - float4 * arg3;
        float float22 = this.m00 * float6 + this.m10 * float7 + this.m20 * float8 + this.m30 * float9;
        float float23 = this.m01 * float6 + this.m11 * float7 + this.m21 * float8 + this.m31 * float9;
        float float24 = this.m02 * float6 + this.m12 * float7 + this.m22 * float8 + this.m32 * float9;
        float float25 = this.m00 * float10 + this.m10 * float11 + this.m20 * float12 + this.m30 * float13;
        float float26 = this.m01 * float10 + this.m11 * float11 + this.m21 * float12 + this.m31 * float13;
        float float27 = this.m02 * float10 + this.m12 * float11 + this.m22 * float12 + this.m32 * float13;
        float float28 = this.m00 * float14 + this.m10 * float15 + this.m20 * float16 + this.m30 * float17;
        float float29 = this.m01 * float14 + this.m11 * float15 + this.m21 * float16 + this.m31 * float17;
        float float30 = this.m02 * float14 + this.m12 * float15 + this.m22 * float16 + this.m32 * float17;
        arg8.m30 = this.m00 * float18 + this.m10 * float19 + this.m20 * float20 + this.m30 * float21;
        arg8.m31 = this.m01 * float18 + this.m11 * float19 + this.m21 * float20 + this.m31 * float21;
        arg8.m32 = this.m02 * float18 + this.m12 * float19 + this.m22 * float20 + this.m32 * float21;
        arg8.m00 = float22;
        arg8.m01 = float23;
        arg8.m02 = float24;
        arg8.m10 = float25;
        arg8.m11 = float26;
        arg8.m12 = float27;
        arg8.m20 = float28;
        arg8.m21 = float29;
        arg8.m22 = float30;
        arg8.properties = this.properties & -29;
        return arg8;
    }

    @Override
    public Matrix4x3f shadow(Vector4fc arg0, Matrix4x3fc arg1, Matrix4x3f arg2) {
        float float0 = arg1.m10();
        float float1 = arg1.m11();
        float float2 = arg1.m12();
        float float3 = -float0 * arg1.m30() - float1 * arg1.m31() - float2 * arg1.m32();
        return this.shadow(arg0.x(), arg0.y(), arg0.z(), arg0.w(), float0, float1, float2, float3, arg2);
    }

    public Matrix4x3f shadow(Vector4fc arg0, Matrix4x3fc arg1) {
        return this.shadow(arg0, arg1, this);
    }

    @Override
    public Matrix4x3f shadow(float arg0, float arg1, float arg2, float arg3, Matrix4x3fc arg4, Matrix4x3f arg5) {
        float float0 = arg4.m10();
        float float1 = arg4.m11();
        float float2 = arg4.m12();
        float float3 = -float0 * arg4.m30() - float1 * arg4.m31() - float2 * arg4.m32();
        return this.shadow(arg0, arg1, arg2, arg3, float0, float1, float2, float3, arg5);
    }

    public Matrix4x3f shadow(float arg0, float arg1, float arg2, float arg3, Matrix4x3f arg4) {
        return this.shadow(arg0, arg1, arg2, arg3, arg4, this);
    }

    public Matrix4x3f billboardCylindrical(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2) {
        float float0 = arg1.x() - arg0.x();
        float float1 = arg1.y() - arg0.y();
        float float2 = arg1.z() - arg0.z();
        float float3 = arg2.y() * float2 - arg2.z() * float1;
        float float4 = arg2.z() * float0 - arg2.x() * float2;
        float float5 = arg2.x() * float1 - arg2.y() * float0;
        float float6 = Math.invsqrt(float3 * float3 + float4 * float4 + float5 * float5);
        float3 *= float6;
        float4 *= float6;
        float5 *= float6;
        float0 = float4 * arg2.z() - float5 * arg2.y();
        float1 = float5 * arg2.x() - float3 * arg2.z();
        float2 = float3 * arg2.y() - float4 * arg2.x();
        float float7 = Math.invsqrt(float0 * float0 + float1 * float1 + float2 * float2);
        float0 *= float7;
        float1 *= float7;
        float2 *= float7;
        this.m00 = float3;
        this.m01 = float4;
        this.m02 = float5;
        this.m10 = arg2.x();
        this.m11 = arg2.y();
        this.m12 = arg2.z();
        this.m20 = float0;
        this.m21 = float1;
        this.m22 = float2;
        this.m30 = arg0.x();
        this.m31 = arg0.y();
        this.m32 = arg0.z();
        this.properties = 16;
        return this;
    }

    public Matrix4x3f billboardSpherical(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2) {
        float float0 = arg1.x() - arg0.x();
        float float1 = arg1.y() - arg0.y();
        float float2 = arg1.z() - arg0.z();
        float float3 = Math.invsqrt(float0 * float0 + float1 * float1 + float2 * float2);
        float0 *= float3;
        float1 *= float3;
        float2 *= float3;
        float float4 = arg2.y() * float2 - arg2.z() * float1;
        float float5 = arg2.z() * float0 - arg2.x() * float2;
        float float6 = arg2.x() * float1 - arg2.y() * float0;
        float float7 = Math.invsqrt(float4 * float4 + float5 * float5 + float6 * float6);
        float4 *= float7;
        float5 *= float7;
        float6 *= float7;
        float float8 = float1 * float6 - float2 * float5;
        float float9 = float2 * float4 - float0 * float6;
        float float10 = float0 * float5 - float1 * float4;
        this.m00 = float4;
        this.m01 = float5;
        this.m02 = float6;
        this.m10 = float8;
        this.m11 = float9;
        this.m12 = float10;
        this.m20 = float0;
        this.m21 = float1;
        this.m22 = float2;
        this.m30 = arg0.x();
        this.m31 = arg0.y();
        this.m32 = arg0.z();
        this.properties = 16;
        return this;
    }

    public Matrix4x3f billboardSpherical(Vector3fc arg0, Vector3fc arg1) {
        float float0 = arg1.x() - arg0.x();
        float float1 = arg1.y() - arg0.y();
        float float2 = arg1.z() - arg0.z();
        float float3 = -float1;
        float float4 = Math.sqrt(float0 * float0 + float1 * float1 + float2 * float2) + float2;
        float float5 = Math.invsqrt(float3 * float3 + float0 * float0 + float4 * float4);
        float3 *= float5;
        float float6 = float0 * float5;
        float4 *= float5;
        float float7 = (float3 + float3) * float3;
        float float8 = (float6 + float6) * float6;
        float float9 = (float3 + float3) * float6;
        float float10 = (float3 + float3) * float4;
        float float11 = (float6 + float6) * float4;
        this.m00 = 1.0F - float8;
        this.m01 = float9;
        this.m02 = -float11;
        this.m10 = float9;
        this.m11 = 1.0F - float7;
        this.m12 = float10;
        this.m20 = float11;
        this.m21 = -float10;
        this.m22 = 1.0F - float8 - float7;
        this.m30 = arg0.x();
        this.m31 = arg0.y();
        this.m32 = arg0.z();
        this.properties = 16;
        return this;
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
        int0 = 31 * int0 + Float.floatToIntBits(this.m22);
        int0 = 31 * int0 + Float.floatToIntBits(this.m30);
        int0 = 31 * int0 + Float.floatToIntBits(this.m31);
        return 31 * int0 + Float.floatToIntBits(this.m32);
    }

    @Override
    public boolean equals(Object arg0) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (!(arg0 instanceof Matrix4x3f matrix4x3f)) {
            return false;
        } else if (Float.floatToIntBits(this.m00) != Float.floatToIntBits(matrix4x3f.m00)) {
            return false;
        } else if (Float.floatToIntBits(this.m01) != Float.floatToIntBits(matrix4x3f.m01)) {
            return false;
        } else if (Float.floatToIntBits(this.m02) != Float.floatToIntBits(matrix4x3f.m02)) {
            return false;
        } else if (Float.floatToIntBits(this.m10) != Float.floatToIntBits(matrix4x3f.m10)) {
            return false;
        } else if (Float.floatToIntBits(this.m11) != Float.floatToIntBits(matrix4x3f.m11)) {
            return false;
        } else if (Float.floatToIntBits(this.m12) != Float.floatToIntBits(matrix4x3f.m12)) {
            return false;
        } else if (Float.floatToIntBits(this.m20) != Float.floatToIntBits(matrix4x3f.m20)) {
            return false;
        } else if (Float.floatToIntBits(this.m21) != Float.floatToIntBits(matrix4x3f.m21)) {
            return false;
        } else if (Float.floatToIntBits(this.m22) != Float.floatToIntBits(matrix4x3f.m22)) {
            return false;
        } else if (Float.floatToIntBits(this.m30) != Float.floatToIntBits(matrix4x3f.m30)) {
            return false;
        } else {
            return Float.floatToIntBits(this.m31) != Float.floatToIntBits(matrix4x3f.m31)
                ? false
                : Float.floatToIntBits(this.m32) == Float.floatToIntBits(matrix4x3f.m32);
        }
    }

    @Override
    public boolean equals(Matrix4x3fc arg0, float arg1) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (!(arg0 instanceof Matrix4x3f)) {
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
        } else if (!Runtime.equals(this.m21, arg0.m21(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m22, arg0.m22(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m30, arg0.m30(), arg1)) {
            return false;
        } else {
            return !Runtime.equals(this.m31, arg0.m31(), arg1) ? false : Runtime.equals(this.m32, arg0.m32(), arg1);
        }
    }

    @Override
    public Matrix4x3f pick(float float5, float float7, float float1, float float3, int[] ints, Matrix4x3f matrix4x3f1) {
        float float0 = ints[2] / float1;
        float float2 = ints[3] / float3;
        float float4 = (ints[2] + 2.0F * (ints[0] - float5)) / float1;
        float float6 = (ints[3] + 2.0F * (ints[1] - float7)) / float3;
        matrix4x3f1.m30 = this.m00 * float4 + this.m10 * float6 + this.m30;
        matrix4x3f1.m31 = this.m01 * float4 + this.m11 * float6 + this.m31;
        matrix4x3f1.m32 = this.m02 * float4 + this.m12 * float6 + this.m32;
        matrix4x3f1.m00 = this.m00 * float0;
        matrix4x3f1.m01 = this.m01 * float0;
        matrix4x3f1.m02 = this.m02 * float0;
        matrix4x3f1.m10 = this.m10 * float2;
        matrix4x3f1.m11 = this.m11 * float2;
        matrix4x3f1.m12 = this.m12 * float2;
        matrix4x3f1.properties = 0;
        return matrix4x3f1;
    }

    public Matrix4x3f pick(float float0, float float1, float float2, float float3, int[] ints) {
        return this.pick(float0, float1, float2, float3, ints, this);
    }

    public Matrix4x3f swap(Matrix4x3f arg0) {
        MemUtil.INSTANCE.swap(this, arg0);
        int int0 = this.properties;
        this.properties = arg0.properties;
        arg0.properties = int0;
        return this;
    }

    @Override
    public Matrix4x3f arcball(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4x3f arg6) {
        float float0 = this.m20 * -arg0 + this.m30;
        float float1 = this.m21 * -arg0 + this.m31;
        float float2 = this.m22 * -arg0 + this.m32;
        float float3 = Math.sin(arg4);
        float float4 = Math.cosFromSin(float3, arg4);
        float float5 = this.m10 * float4 + this.m20 * float3;
        float float6 = this.m11 * float4 + this.m21 * float3;
        float float7 = this.m12 * float4 + this.m22 * float3;
        float float8 = this.m20 * float4 - this.m10 * float3;
        float float9 = this.m21 * float4 - this.m11 * float3;
        float float10 = this.m22 * float4 - this.m12 * float3;
        float3 = Math.sin(arg5);
        float4 = Math.cosFromSin(float3, arg5);
        float float11 = this.m00 * float4 - float8 * float3;
        float float12 = this.m01 * float4 - float9 * float3;
        float float13 = this.m02 * float4 - float10 * float3;
        float float14 = this.m00 * float3 + float8 * float4;
        float float15 = this.m01 * float3 + float9 * float4;
        float float16 = this.m02 * float3 + float10 * float4;
        arg6.m30 = -float11 * arg1 - float5 * arg2 - float14 * arg3 + float0;
        arg6.m31 = -float12 * arg1 - float6 * arg2 - float15 * arg3 + float1;
        arg6.m32 = -float13 * arg1 - float7 * arg2 - float16 * arg3 + float2;
        arg6.m20 = float14;
        arg6.m21 = float15;
        arg6.m22 = float16;
        arg6.m10 = float5;
        arg6.m11 = float6;
        arg6.m12 = float7;
        arg6.m00 = float11;
        arg6.m01 = float12;
        arg6.m02 = float13;
        arg6.properties = this.properties & -13;
        return arg6;
    }

    @Override
    public Matrix4x3f arcball(float arg0, Vector3fc arg1, float arg2, float arg3, Matrix4x3f arg4) {
        return this.arcball(arg0, arg1.x(), arg1.y(), arg1.z(), arg2, arg3, arg4);
    }

    public Matrix4x3f arcball(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.arcball(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    public Matrix4x3f arcball(float arg0, Vector3fc arg1, float arg2, float arg3) {
        return this.arcball(arg0, arg1.x(), arg1.y(), arg1.z(), arg2, arg3, this);
    }

    @Override
    public Matrix4x3f transformAab(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Vector3f arg6, Vector3f arg7) {
        float float0 = this.m00 * arg0;
        float float1 = this.m01 * arg0;
        float float2 = this.m02 * arg0;
        float float3 = this.m00 * arg3;
        float float4 = this.m01 * arg3;
        float float5 = this.m02 * arg3;
        float float6 = this.m10 * arg1;
        float float7 = this.m11 * arg1;
        float float8 = this.m12 * arg1;
        float float9 = this.m10 * arg4;
        float float10 = this.m11 * arg4;
        float float11 = this.m12 * arg4;
        float float12 = this.m20 * arg2;
        float float13 = this.m21 * arg2;
        float float14 = this.m22 * arg2;
        float float15 = this.m20 * arg5;
        float float16 = this.m21 * arg5;
        float float17 = this.m22 * arg5;
        float float18;
        float float19;
        if (float0 < float3) {
            float18 = float0;
            float19 = float3;
        } else {
            float18 = float3;
            float19 = float0;
        }

        float float20;
        float float21;
        if (float1 < float4) {
            float20 = float1;
            float21 = float4;
        } else {
            float20 = float4;
            float21 = float1;
        }

        float float22;
        float float23;
        if (float2 < float5) {
            float22 = float2;
            float23 = float5;
        } else {
            float22 = float5;
            float23 = float2;
        }

        float float24;
        float float25;
        if (float6 < float9) {
            float24 = float6;
            float25 = float9;
        } else {
            float24 = float9;
            float25 = float6;
        }

        float float26;
        float float27;
        if (float7 < float10) {
            float26 = float7;
            float27 = float10;
        } else {
            float26 = float10;
            float27 = float7;
        }

        float float28;
        float float29;
        if (float8 < float11) {
            float28 = float8;
            float29 = float11;
        } else {
            float28 = float11;
            float29 = float8;
        }

        float float30;
        float float31;
        if (float12 < float15) {
            float30 = float12;
            float31 = float15;
        } else {
            float30 = float15;
            float31 = float12;
        }

        float float32;
        float float33;
        if (float13 < float16) {
            float32 = float13;
            float33 = float16;
        } else {
            float32 = float16;
            float33 = float13;
        }

        float float34;
        float float35;
        if (float14 < float17) {
            float34 = float14;
            float35 = float17;
        } else {
            float34 = float17;
            float35 = float14;
        }

        arg6.x = float18 + float24 + float30 + this.m30;
        arg6.y = float20 + float26 + float32 + this.m31;
        arg6.z = float22 + float28 + float34 + this.m32;
        arg7.x = float19 + float25 + float31 + this.m30;
        arg7.y = float21 + float27 + float33 + this.m31;
        arg7.z = float23 + float29 + float35 + this.m32;
        return this;
    }

    @Override
    public Matrix4x3f transformAab(Vector3fc arg0, Vector3fc arg1, Vector3f arg2, Vector3f arg3) {
        return this.transformAab(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2, arg3);
    }

    public Matrix4x3f lerp(Matrix4x3fc arg0, float arg1) {
        return this.lerp(arg0, arg1, this);
    }

    @Override
    public Matrix4x3f lerp(Matrix4x3fc arg0, float arg1, Matrix4x3f arg2) {
        arg2.m00 = Math.fma(arg0.m00() - this.m00, arg1, this.m00);
        arg2.m01 = Math.fma(arg0.m01() - this.m01, arg1, this.m01);
        arg2.m02 = Math.fma(arg0.m02() - this.m02, arg1, this.m02);
        arg2.m10 = Math.fma(arg0.m10() - this.m10, arg1, this.m10);
        arg2.m11 = Math.fma(arg0.m11() - this.m11, arg1, this.m11);
        arg2.m12 = Math.fma(arg0.m12() - this.m12, arg1, this.m12);
        arg2.m20 = Math.fma(arg0.m20() - this.m20, arg1, this.m20);
        arg2.m21 = Math.fma(arg0.m21() - this.m21, arg1, this.m21);
        arg2.m22 = Math.fma(arg0.m22() - this.m22, arg1, this.m22);
        arg2.m30 = Math.fma(arg0.m30() - this.m30, arg1, this.m30);
        arg2.m31 = Math.fma(arg0.m31() - this.m31, arg1, this.m31);
        arg2.m32 = Math.fma(arg0.m32() - this.m32, arg1, this.m32);
        arg2.properties = this.properties & arg0.properties();
        return arg2;
    }

    @Override
    public Matrix4x3f rotateTowards(Vector3fc arg0, Vector3fc arg1, Matrix4x3f arg2) {
        return this.rotateTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    public Matrix4x3f rotateTowards(Vector3fc arg0, Vector3fc arg1) {
        return this.rotateTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), this);
    }

    public Matrix4x3f rotateTowards(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.rotateTowards(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    @Override
    public Matrix4x3f rotateTowards(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4x3f arg6) {
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
        arg6.m30 = this.m30;
        arg6.m31 = this.m31;
        arg6.m32 = this.m32;
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
        arg6.properties = this.properties & -13;
        return arg6;
    }

    public Matrix4x3f rotationTowards(Vector3fc arg0, Vector3fc arg1) {
        return this.rotationTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix4x3f rotationTowards(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
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
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.properties = 16;
        return this;
    }

    public Matrix4x3f translationRotateTowards(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2) {
        return this.translationRotateTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z());
    }

    public Matrix4x3f translationRotateTowards(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8) {
        float float0 = Math.invsqrt(arg3 * arg3 + arg4 * arg4 + arg5 * arg5);
        float float1 = arg3 * float0;
        float float2 = arg4 * float0;
        float float3 = arg5 * float0;
        float float4 = arg7 * float3 - arg8 * float2;
        float float5 = arg8 * float1 - arg6 * float3;
        float float6 = arg6 * float2 - arg7 * float1;
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
        this.m30 = arg0;
        this.m31 = arg1;
        this.m32 = arg2;
        this.properties = 16;
        return this;
    }

    @Override
    public Vector3f getEulerAnglesZYX(Vector3f arg0) {
        arg0.x = Math.atan2(this.m12, this.m22);
        arg0.y = Math.atan2(-this.m02, Math.sqrt(this.m12 * this.m12 + this.m22 * this.m22));
        arg0.z = Math.atan2(this.m01, this.m00);
        return arg0;
    }

    public Matrix4x3f obliqueZ(float arg0, float arg1) {
        this.m20 = this.m00 * arg0 + this.m10 * arg1 + this.m20;
        this.m21 = this.m01 * arg0 + this.m11 * arg1 + this.m21;
        this.m22 = this.m02 * arg0 + this.m12 * arg1 + this.m22;
        this.properties = 0;
        return this;
    }

    @Override
    public Matrix4x3f obliqueZ(float arg0, float arg1, Matrix4x3f arg2) {
        arg2.m00 = this.m00;
        arg2.m01 = this.m01;
        arg2.m02 = this.m02;
        arg2.m10 = this.m10;
        arg2.m11 = this.m11;
        arg2.m12 = this.m12;
        arg2.m20 = this.m00 * arg0 + this.m10 * arg1 + this.m20;
        arg2.m21 = this.m01 * arg0 + this.m11 * arg1 + this.m21;
        arg2.m22 = this.m02 * arg0 + this.m12 * arg1 + this.m22;
        arg2.m30 = this.m30;
        arg2.m31 = this.m31;
        arg2.m32 = this.m32;
        arg2.properties = 0;
        return arg2;
    }

    public Matrix4x3f withLookAtUp(Vector3fc arg0) {
        return this.withLookAtUp(arg0.x(), arg0.y(), arg0.z(), this);
    }

    @Override
    public Matrix4x3f withLookAtUp(Vector3fc arg0, Matrix4x3f arg1) {
        return this.withLookAtUp(arg0.x(), arg0.y(), arg0.z());
    }

    public Matrix4x3f withLookAtUp(float arg0, float arg1, float arg2) {
        return this.withLookAtUp(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4x3f withLookAtUp(float arg0, float arg1, float arg2, Matrix4x3f arg3) {
        float float0 = (arg1 * this.m21 - arg2 * this.m11) * this.m02
            + (arg2 * this.m01 - arg0 * this.m21) * this.m12
            + (arg0 * this.m11 - arg1 * this.m01) * this.m22;
        float float1 = arg0 * this.m01 + arg1 * this.m11 + arg2 * this.m21;
        if ((this.properties & 16) == 0) {
            float1 *= Math.sqrt(this.m01 * this.m01 + this.m11 * this.m11 + this.m21 * this.m21);
        }

        float float2 = Math.invsqrt(float0 * float0 + float1 * float1);
        float float3 = float1 * float2;
        float float4 = float0 * float2;
        float float5 = float3 * this.m00 - float4 * this.m01;
        float float6 = float3 * this.m10 - float4 * this.m11;
        float float7 = float3 * this.m20 - float4 * this.m21;
        float float8 = float4 * this.m30 + float3 * this.m31;
        float float9 = float4 * this.m00 + float3 * this.m01;
        float float10 = float4 * this.m10 + float3 * this.m11;
        float float11 = float4 * this.m20 + float3 * this.m21;
        float float12 = float3 * this.m30 - float4 * this.m31;
        arg3._m00(float5)._m10(float6)._m20(float7)._m30(float12)._m01(float9)._m11(float10)._m21(float11)._m31(float8);
        if (arg3 != this) {
            arg3._m02(this.m02)._m12(this.m12)._m22(this.m22)._m32(this.m32);
        }

        arg3.properties = this.properties & -13;
        return arg3;
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
            && Math.isFinite(this.m22)
            && Math.isFinite(this.m30)
            && Math.isFinite(this.m31)
            && Math.isFinite(this.m32);
    }
}
