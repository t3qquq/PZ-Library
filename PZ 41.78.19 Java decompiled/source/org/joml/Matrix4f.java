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

public class Matrix4f implements Externalizable, Matrix4fc {
    private static final long serialVersionUID = 1L;
    float m00;
    float m01;
    float m02;
    float m03;
    float m10;
    float m11;
    float m12;
    float m13;
    float m20;
    float m21;
    float m22;
    float m23;
    float m30;
    float m31;
    float m32;
    float m33;
    int properties;

    public Matrix4f() {
        this._m00(1.0F)._m11(1.0F)._m22(1.0F)._m33(1.0F)._properties(30);
    }

    public Matrix4f(Matrix3fc arg0) {
        this.set(arg0);
    }

    public Matrix4f(Matrix4fc arg0) {
        this.set(arg0);
    }

    public Matrix4f(Matrix4x3fc arg0) {
        this.set(arg0);
    }

    public Matrix4f(Matrix4dc arg0) {
        this.set(arg0);
    }

    public Matrix4f(
        float arg0,
        float arg1,
        float arg2,
        float arg3,
        float arg4,
        float arg5,
        float arg6,
        float arg7,
        float arg8,
        float arg9,
        float arg10,
        float arg11,
        float arg12,
        float arg13,
        float arg14,
        float arg15
    ) {
        this._m00(arg0)
            ._m01(arg1)
            ._m02(arg2)
            ._m03(arg3)
            ._m10(arg4)
            ._m11(arg5)
            ._m12(arg6)
            ._m13(arg7)
            ._m20(arg8)
            ._m21(arg9)
            ._m22(arg10)
            ._m23(arg11)
            ._m30(arg12)
            ._m31(arg13)
            ._m32(arg14)
            ._m33(arg15)
            .determineProperties();
    }

    public Matrix4f(FloatBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        this.determineProperties();
    }

    public Matrix4f(Vector4fc arg0, Vector4fc arg1, Vector4fc arg2, Vector4fc arg3) {
        this.set(arg0, arg1, arg2, arg3);
    }

    Matrix4f _properties(int int0) {
        this.properties = int0;
        return this;
    }

    public Matrix4f assume(int arg0) {
        this._properties(arg0);
        return this;
    }

    public Matrix4f determineProperties() {
        byte byte0 = 0;
        if (this.m03 == 0.0F && this.m13 == 0.0F) {
            if (this.m23 == 0.0F && this.m33 == 1.0F) {
                byte0 |= 2;
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
            } else if (this.m01 == 0.0F
                && this.m02 == 0.0F
                && this.m10 == 0.0F
                && this.m12 == 0.0F
                && this.m20 == 0.0F
                && this.m21 == 0.0F
                && this.m30 == 0.0F
                && this.m31 == 0.0F
                && this.m33 == 0.0F) {
                byte0 |= 1;
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
    public float m03() {
        return this.m03;
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
    public float m13() {
        return this.m13;
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
    public float m23() {
        return this.m23;
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

    @Override
    public float m33() {
        return this.m33;
    }

    public Matrix4f m00(float arg0) {
        this.m00 = arg0;
        this.properties &= -17;
        if (arg0 != 1.0F) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4f m01(float arg0) {
        this.m01 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0F) {
            this.properties &= -14;
        }

        return this;
    }

    public Matrix4f m02(float arg0) {
        this.m02 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0F) {
            this.properties &= -14;
        }

        return this;
    }

    public Matrix4f m03(float arg0) {
        this.m03 = arg0;
        if (arg0 != 0.0F) {
            this.properties = 0;
        }

        return this;
    }

    public Matrix4f m10(float arg0) {
        this.m10 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0F) {
            this.properties &= -14;
        }

        return this;
    }

    public Matrix4f m11(float arg0) {
        this.m11 = arg0;
        this.properties &= -17;
        if (arg0 != 1.0F) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4f m12(float arg0) {
        this.m12 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0F) {
            this.properties &= -14;
        }

        return this;
    }

    public Matrix4f m13(float arg0) {
        this.m13 = arg0;
        if (arg0 != 0.0F) {
            this.properties = 0;
        }

        return this;
    }

    public Matrix4f m20(float arg0) {
        this.m20 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0F) {
            this.properties &= -14;
        }

        return this;
    }

    public Matrix4f m21(float arg0) {
        this.m21 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0F) {
            this.properties &= -14;
        }

        return this;
    }

    public Matrix4f m22(float arg0) {
        this.m22 = arg0;
        this.properties &= -17;
        if (arg0 != 1.0F) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4f m23(float arg0) {
        this.m23 = arg0;
        if (arg0 != 0.0F) {
            this.properties &= -31;
        }

        return this;
    }

    public Matrix4f m30(float arg0) {
        this.m30 = arg0;
        if (arg0 != 0.0F) {
            this.properties &= -6;
        }

        return this;
    }

    public Matrix4f m31(float arg0) {
        this.m31 = arg0;
        if (arg0 != 0.0F) {
            this.properties &= -6;
        }

        return this;
    }

    public Matrix4f m32(float arg0) {
        this.m32 = arg0;
        if (arg0 != 0.0F) {
            this.properties &= -6;
        }

        return this;
    }

    public Matrix4f m33(float arg0) {
        this.m33 = arg0;
        if (arg0 != 0.0F) {
            this.properties &= -2;
        }

        if (arg0 != 1.0F) {
            this.properties &= -31;
        }

        return this;
    }

    Matrix4f _m00(float float0) {
        this.m00 = float0;
        return this;
    }

    Matrix4f _m01(float float0) {
        this.m01 = float0;
        return this;
    }

    Matrix4f _m02(float float0) {
        this.m02 = float0;
        return this;
    }

    Matrix4f _m03(float float0) {
        this.m03 = float0;
        return this;
    }

    Matrix4f _m10(float float0) {
        this.m10 = float0;
        return this;
    }

    Matrix4f _m11(float float0) {
        this.m11 = float0;
        return this;
    }

    Matrix4f _m12(float float0) {
        this.m12 = float0;
        return this;
    }

    Matrix4f _m13(float float0) {
        this.m13 = float0;
        return this;
    }

    Matrix4f _m20(float float0) {
        this.m20 = float0;
        return this;
    }

    Matrix4f _m21(float float0) {
        this.m21 = float0;
        return this;
    }

    Matrix4f _m22(float float0) {
        this.m22 = float0;
        return this;
    }

    Matrix4f _m23(float float0) {
        this.m23 = float0;
        return this;
    }

    Matrix4f _m30(float float0) {
        this.m30 = float0;
        return this;
    }

    Matrix4f _m31(float float0) {
        this.m31 = float0;
        return this;
    }

    Matrix4f _m32(float float0) {
        this.m32 = float0;
        return this;
    }

    Matrix4f _m33(float float0) {
        this.m33 = float0;
        return this;
    }

    public Matrix4f identity() {
        return (this.properties & 4) != 0
            ? this
            : this._m00(1.0F)
                ._m01(0.0F)
                ._m02(0.0F)
                ._m03(0.0F)
                ._m10(0.0F)
                ._m11(1.0F)
                ._m12(0.0F)
                ._m13(0.0F)
                ._m20(0.0F)
                ._m21(0.0F)
                ._m22(1.0F)
                ._m23(0.0F)
                ._m30(0.0F)
                ._m31(0.0F)
                ._m32(0.0F)
                ._m33(1.0F)
                ._properties(30);
    }

    public Matrix4f set(Matrix4fc arg0) {
        return this._m00(arg0.m00())
            ._m01(arg0.m01())
            ._m02(arg0.m02())
            ._m03(arg0.m03())
            ._m10(arg0.m10())
            ._m11(arg0.m11())
            ._m12(arg0.m12())
            ._m13(arg0.m13())
            ._m20(arg0.m20())
            ._m21(arg0.m21())
            ._m22(arg0.m22())
            ._m23(arg0.m23())
            ._m30(arg0.m30())
            ._m31(arg0.m31())
            ._m32(arg0.m32())
            ._m33(arg0.m33())
            ._properties(arg0.properties());
    }

    public Matrix4f setTransposed(Matrix4fc arg0) {
        return (arg0.properties() & 4) != 0 ? this.identity() : this.setTransposedInternal(arg0);
    }

    private Matrix4f setTransposedInternal(Matrix4fc matrix4fc) {
        float float0 = matrix4fc.m01();
        float float1 = matrix4fc.m21();
        float float2 = matrix4fc.m31();
        float float3 = matrix4fc.m02();
        float float4 = matrix4fc.m12();
        float float5 = matrix4fc.m03();
        float float6 = matrix4fc.m13();
        float float7 = matrix4fc.m23();
        return this._m00(matrix4fc.m00())
            ._m01(matrix4fc.m10())
            ._m02(matrix4fc.m20())
            ._m03(matrix4fc.m30())
            ._m10(float0)
            ._m11(matrix4fc.m11())
            ._m12(float1)
            ._m13(float2)
            ._m20(float3)
            ._m21(float4)
            ._m22(matrix4fc.m22())
            ._m23(matrix4fc.m32())
            ._m30(float5)
            ._m31(float6)
            ._m32(float7)
            ._m33(matrix4fc.m33())
            ._properties(matrix4fc.properties() & 4);
    }

    public Matrix4f set(Matrix4x3fc arg0) {
        return this._m00(arg0.m00())
            ._m01(arg0.m01())
            ._m02(arg0.m02())
            ._m03(0.0F)
            ._m10(arg0.m10())
            ._m11(arg0.m11())
            ._m12(arg0.m12())
            ._m13(0.0F)
            ._m20(arg0.m20())
            ._m21(arg0.m21())
            ._m22(arg0.m22())
            ._m23(0.0F)
            ._m30(arg0.m30())
            ._m31(arg0.m31())
            ._m32(arg0.m32())
            ._m33(1.0F)
            ._properties(arg0.properties() | 2);
    }

    public Matrix4f set(Matrix4dc arg0) {
        return this._m00((float)arg0.m00())
            ._m01((float)arg0.m01())
            ._m02((float)arg0.m02())
            ._m03((float)arg0.m03())
            ._m10((float)arg0.m10())
            ._m11((float)arg0.m11())
            ._m12((float)arg0.m12())
            ._m13((float)arg0.m13())
            ._m20((float)arg0.m20())
            ._m21((float)arg0.m21())
            ._m22((float)arg0.m22())
            ._m23((float)arg0.m23())
            ._m30((float)arg0.m30())
            ._m31((float)arg0.m31())
            ._m32((float)arg0.m32())
            ._m33((float)arg0.m33())
            ._properties(arg0.properties());
    }

    public Matrix4f set(Matrix3fc arg0) {
        return this._m00(arg0.m00())
            ._m01(arg0.m01())
            ._m02(arg0.m02())
            ._m03(0.0F)
            ._m10(arg0.m10())
            ._m11(arg0.m11())
            ._m12(arg0.m12())
            ._m13(0.0F)
            ._m20(arg0.m20())
            ._m21(arg0.m21())
            ._m22(arg0.m22())
            ._m23(0.0F)
            ._m30(0.0F)
            ._m31(0.0F)
            ._m32(0.0F)
            ._m33(1.0F)
            ._properties(2);
    }

    public Matrix4f set(AxisAngle4f arg0) {
        float float0 = arg0.x;
        float float1 = arg0.y;
        float float2 = arg0.z;
        float float3 = arg0.angle;
        double double0 = Math.sqrt(float0 * float0 + float1 * float1 + float2 * float2);
        double0 = 1.0 / double0;
        float0 = (float)(float0 * double0);
        float1 = (float)(float1 * double0);
        float2 = (float)(float2 * double0);
        float float4 = Math.sin(float3);
        float float5 = Math.cosFromSin(float4, float3);
        float float6 = 1.0F - float5;
        this._m00(float5 + float0 * float0 * float6)._m11(float5 + float1 * float1 * float6)._m22(float5 + float2 * float2 * float6);
        float float7 = float0 * float1 * float6;
        float float8 = float2 * float4;
        this._m10(float7 - float8)._m01(float7 + float8);
        float7 = float0 * float2 * float6;
        float8 = float1 * float4;
        this._m20(float7 + float8)._m02(float7 - float8);
        float7 = float1 * float2 * float6;
        float8 = float0 * float4;
        return this._m21(float7 - float8)._m12(float7 + float8)._m03(0.0F)._m13(0.0F)._m23(0.0F)._m30(0.0F)._m31(0.0F)._m32(0.0F)._m33(1.0F)._properties(18);
    }

    public Matrix4f set(AxisAngle4d arg0) {
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
        this._m00((float)(double6 + double0 * double0 * double7))
            ._m11((float)(double6 + double1 * double1 * double7))
            ._m22((float)(double6 + double2 * double2 * double7));
        double double8 = double0 * double1 * double7;
        double double9 = double2 * double5;
        this._m10((float)(double8 - double9))._m01((float)(double8 + double9));
        double8 = double0 * double2 * double7;
        double9 = double1 * double5;
        this._m20((float)(double8 + double9))._m02((float)(double8 - double9));
        double8 = double1 * double2 * double7;
        double9 = double0 * double5;
        return this._m21((float)(double8 - double9))
            ._m12((float)(double8 + double9))
            ._m03(0.0F)
            ._m13(0.0F)
            ._m23(0.0F)
            ._m30(0.0F)
            ._m31(0.0F)
            ._m32(0.0F)
            ._m33(1.0F)
            ._properties(18);
    }

    public Matrix4f set(Quaternionfc arg0) {
        return this.rotation(arg0);
    }

    public Matrix4f set(Quaterniondc arg0) {
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
        return this._m00((float)(double0 + double1 - double3 - double2))
            ._m01((float)(double5 + double4 + double4 + double5))
            ._m02((float)(double6 - double7 + double6 - double7))
            ._m03(0.0F)
            ._m10((float)(-double4 + double5 - double4 + double5))
            ._m11((float)(double2 - double3 + double0 - double1))
            ._m12((float)(double8 + double8 + double9 + double9))
            ._m13(0.0F)
            ._m20((float)(double7 + double6 + double6 + double7))
            ._m21((float)(double8 + double8 - double9 - double9))
            ._m22((float)(double3 - double2 - double1 + double0))
            ._m30(0.0F)
            ._m31(0.0F)
            ._m32(0.0F)
            ._m33(1.0F)
            ._properties(18);
    }

    public Matrix4f set3x3(Matrix4f arg0) {
        MemUtil.INSTANCE.copy3x3(arg0, this);
        return this._properties(this.properties & arg0.properties & -2);
    }

    public Matrix4f set4x3(Matrix4x3fc arg0) {
        return this._m00(arg0.m00())
            ._m01(arg0.m01())
            ._m02(arg0.m02())
            ._m10(arg0.m10())
            ._m11(arg0.m11())
            ._m12(arg0.m12())
            ._m20(arg0.m20())
            ._m21(arg0.m21())
            ._m22(arg0.m22())
            ._m30(arg0.m30())
            ._m31(arg0.m31())
            ._m32(arg0.m32())
            ._properties(this.properties & arg0.properties() & -2);
    }

    public Matrix4f set4x3(Matrix4f arg0) {
        MemUtil.INSTANCE.copy4x3(arg0, this);
        return this._properties(this.properties & arg0.properties & -2);
    }

    public Matrix4f mul(Matrix4fc arg0) {
        return this.mul(arg0, this);
    }

    @Override
    public Matrix4f mul(Matrix4fc arg0, Matrix4f arg1) {
        if ((this.properties & 4) != 0) {
            return arg1.set(arg0);
        } else if ((arg0.properties() & 4) != 0) {
            return arg1.set(this);
        } else if ((this.properties & 8) != 0 && (arg0.properties() & 2) != 0) {
            return this.mulTranslationAffine(arg0, arg1);
        } else if ((this.properties & 2) != 0 && (arg0.properties() & 2) != 0) {
            return this.mulAffine(arg0, arg1);
        } else if ((this.properties & 1) != 0 && (arg0.properties() & 2) != 0) {
            return this.mulPerspectiveAffine(arg0, arg1);
        } else {
            return (arg0.properties() & 2) != 0 ? this.mulAffineR(arg0, arg1) : this.mul0(arg0, arg1);
        }
    }

    public Matrix4f mul0(Matrix4fc arg0) {
        return this.mul0(arg0, this);
    }

    @Override
    public Matrix4f mul0(Matrix4fc arg0, Matrix4f arg1) {
        float float0 = Math.fma(this.m00, arg0.m00(), Math.fma(this.m10, arg0.m01(), Math.fma(this.m20, arg0.m02(), this.m30 * arg0.m03())));
        float float1 = Math.fma(this.m01, arg0.m00(), Math.fma(this.m11, arg0.m01(), Math.fma(this.m21, arg0.m02(), this.m31 * arg0.m03())));
        float float2 = Math.fma(this.m02, arg0.m00(), Math.fma(this.m12, arg0.m01(), Math.fma(this.m22, arg0.m02(), this.m32 * arg0.m03())));
        float float3 = Math.fma(this.m03, arg0.m00(), Math.fma(this.m13, arg0.m01(), Math.fma(this.m23, arg0.m02(), this.m33 * arg0.m03())));
        float float4 = Math.fma(this.m00, arg0.m10(), Math.fma(this.m10, arg0.m11(), Math.fma(this.m20, arg0.m12(), this.m30 * arg0.m13())));
        float float5 = Math.fma(this.m01, arg0.m10(), Math.fma(this.m11, arg0.m11(), Math.fma(this.m21, arg0.m12(), this.m31 * arg0.m13())));
        float float6 = Math.fma(this.m02, arg0.m10(), Math.fma(this.m12, arg0.m11(), Math.fma(this.m22, arg0.m12(), this.m32 * arg0.m13())));
        float float7 = Math.fma(this.m03, arg0.m10(), Math.fma(this.m13, arg0.m11(), Math.fma(this.m23, arg0.m12(), this.m33 * arg0.m13())));
        float float8 = Math.fma(this.m00, arg0.m20(), Math.fma(this.m10, arg0.m21(), Math.fma(this.m20, arg0.m22(), this.m30 * arg0.m23())));
        float float9 = Math.fma(this.m01, arg0.m20(), Math.fma(this.m11, arg0.m21(), Math.fma(this.m21, arg0.m22(), this.m31 * arg0.m23())));
        float float10 = Math.fma(this.m02, arg0.m20(), Math.fma(this.m12, arg0.m21(), Math.fma(this.m22, arg0.m22(), this.m32 * arg0.m23())));
        float float11 = Math.fma(this.m03, arg0.m20(), Math.fma(this.m13, arg0.m21(), Math.fma(this.m23, arg0.m22(), this.m33 * arg0.m23())));
        float float12 = Math.fma(this.m00, arg0.m30(), Math.fma(this.m10, arg0.m31(), Math.fma(this.m20, arg0.m32(), this.m30 * arg0.m33())));
        float float13 = Math.fma(this.m01, arg0.m30(), Math.fma(this.m11, arg0.m31(), Math.fma(this.m21, arg0.m32(), this.m31 * arg0.m33())));
        float float14 = Math.fma(this.m02, arg0.m30(), Math.fma(this.m12, arg0.m31(), Math.fma(this.m22, arg0.m32(), this.m32 * arg0.m33())));
        float float15 = Math.fma(this.m03, arg0.m30(), Math.fma(this.m13, arg0.m31(), Math.fma(this.m23, arg0.m32(), this.m33 * arg0.m33())));
        return arg1._m00(float0)
            ._m01(float1)
            ._m02(float2)
            ._m03(float3)
            ._m10(float4)
            ._m11(float5)
            ._m12(float6)
            ._m13(float7)
            ._m20(float8)
            ._m21(float9)
            ._m22(float10)
            ._m23(float11)
            ._m30(float12)
            ._m31(float13)
            ._m32(float14)
            ._m33(float15)
            ._properties(0);
    }

    public Matrix4f mul(
        float arg0,
        float arg1,
        float arg2,
        float arg3,
        float arg4,
        float arg5,
        float arg6,
        float arg7,
        float arg8,
        float arg9,
        float arg10,
        float arg11,
        float arg12,
        float arg13,
        float arg14,
        float arg15
    ) {
        return this.mul(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, this);
    }

    @Override
    public Matrix4f mul(
        float arg0,
        float arg1,
        float arg2,
        float arg3,
        float arg4,
        float arg5,
        float arg6,
        float arg7,
        float arg8,
        float arg9,
        float arg10,
        float arg11,
        float arg12,
        float arg13,
        float arg14,
        float arg15,
        Matrix4f arg16
    ) {
        if ((this.properties & 4) != 0) {
            return arg16.set(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
        } else {
            return (this.properties & 2) != 0
                ? this.mulAffineL(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16)
                : this.mulGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);
        }
    }

    private Matrix4f mulAffineL(
        float float1,
        float float2,
        float float3,
        float float4,
        float float8,
        float float9,
        float float10,
        float float11,
        float float15,
        float float16,
        float float17,
        float float18,
        float float22,
        float float23,
        float float24,
        float float25,
        Matrix4f matrix4f1
    ) {
        float float0 = Math.fma(this.m00, float1, Math.fma(this.m10, float2, Math.fma(this.m20, float3, this.m30 * float4)));
        float float5 = Math.fma(this.m01, float1, Math.fma(this.m11, float2, Math.fma(this.m21, float3, this.m31 * float4)));
        float float6 = Math.fma(this.m02, float1, Math.fma(this.m12, float2, Math.fma(this.m22, float3, this.m32 * float4)));
        float float7 = Math.fma(this.m00, float8, Math.fma(this.m10, float9, Math.fma(this.m20, float10, this.m30 * float11)));
        float float12 = Math.fma(this.m01, float8, Math.fma(this.m11, float9, Math.fma(this.m21, float10, this.m31 * float11)));
        float float13 = Math.fma(this.m02, float8, Math.fma(this.m12, float9, Math.fma(this.m22, float10, this.m32 * float11)));
        float float14 = Math.fma(this.m00, float15, Math.fma(this.m10, float16, Math.fma(this.m20, float17, this.m30 * float18)));
        float float19 = Math.fma(this.m01, float15, Math.fma(this.m11, float16, Math.fma(this.m21, float17, this.m31 * float18)));
        float float20 = Math.fma(this.m02, float15, Math.fma(this.m12, float16, Math.fma(this.m22, float17, this.m32 * float18)));
        float float21 = Math.fma(this.m00, float22, Math.fma(this.m10, float23, Math.fma(this.m20, float24, this.m30 * float25)));
        float float26 = Math.fma(this.m01, float22, Math.fma(this.m11, float23, Math.fma(this.m21, float24, this.m31 * float25)));
        float float27 = Math.fma(this.m02, float22, Math.fma(this.m12, float23, Math.fma(this.m22, float24, this.m32 * float25)));
        return matrix4f1._m00(float0)
            ._m01(float5)
            ._m02(float6)
            ._m03(float4)
            ._m10(float7)
            ._m11(float12)
            ._m12(float13)
            ._m13(float11)
            ._m20(float14)
            ._m21(float19)
            ._m22(float20)
            ._m23(float18)
            ._m30(float21)
            ._m31(float26)
            ._m32(float27)
            ._m33(float25)
            ._properties(2);
    }

    private Matrix4f mulGeneric(
        float float1,
        float float2,
        float float3,
        float float4,
        float float9,
        float float10,
        float float11,
        float float12,
        float float17,
        float float18,
        float float19,
        float float20,
        float float25,
        float float26,
        float float27,
        float float28,
        Matrix4f matrix4f1
    ) {
        float float0 = Math.fma(this.m00, float1, Math.fma(this.m10, float2, Math.fma(this.m20, float3, this.m30 * float4)));
        float float5 = Math.fma(this.m01, float1, Math.fma(this.m11, float2, Math.fma(this.m21, float3, this.m31 * float4)));
        float float6 = Math.fma(this.m02, float1, Math.fma(this.m12, float2, Math.fma(this.m22, float3, this.m32 * float4)));
        float float7 = Math.fma(this.m03, float1, Math.fma(this.m13, float2, Math.fma(this.m23, float3, this.m33 * float4)));
        float float8 = Math.fma(this.m00, float9, Math.fma(this.m10, float10, Math.fma(this.m20, float11, this.m30 * float12)));
        float float13 = Math.fma(this.m01, float9, Math.fma(this.m11, float10, Math.fma(this.m21, float11, this.m31 * float12)));
        float float14 = Math.fma(this.m02, float9, Math.fma(this.m12, float10, Math.fma(this.m22, float11, this.m32 * float12)));
        float float15 = Math.fma(this.m03, float9, Math.fma(this.m13, float10, Math.fma(this.m23, float11, this.m33 * float12)));
        float float16 = Math.fma(this.m00, float17, Math.fma(this.m10, float18, Math.fma(this.m20, float19, this.m30 * float20)));
        float float21 = Math.fma(this.m01, float17, Math.fma(this.m11, float18, Math.fma(this.m21, float19, this.m31 * float20)));
        float float22 = Math.fma(this.m02, float17, Math.fma(this.m12, float18, Math.fma(this.m22, float19, this.m32 * float20)));
        float float23 = Math.fma(this.m03, float17, Math.fma(this.m13, float18, Math.fma(this.m23, float19, this.m33 * float20)));
        float float24 = Math.fma(this.m00, float25, Math.fma(this.m10, float26, Math.fma(this.m20, float27, this.m30 * float28)));
        float float29 = Math.fma(this.m01, float25, Math.fma(this.m11, float26, Math.fma(this.m21, float27, this.m31 * float28)));
        float float30 = Math.fma(this.m02, float25, Math.fma(this.m12, float26, Math.fma(this.m22, float27, this.m32 * float28)));
        float float31 = Math.fma(this.m03, float25, Math.fma(this.m13, float26, Math.fma(this.m23, float27, this.m33 * float28)));
        return matrix4f1._m00(float0)
            ._m01(float5)
            ._m02(float6)
            ._m03(float7)
            ._m10(float8)
            ._m11(float13)
            ._m12(float14)
            ._m13(float15)
            ._m20(float16)
            ._m21(float21)
            ._m22(float22)
            ._m23(float23)
            ._m30(float24)
            ._m31(float29)
            ._m32(float30)
            ._m33(float31)
            ._properties(0);
    }

    public Matrix4f mul3x3(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8) {
        return this.mul3x3(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, this);
    }

    @Override
    public Matrix4f mul3x3(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8, Matrix4f arg9) {
        return (this.properties & 4) != 0
            ? arg9.set(arg0, arg1, arg2, 0.0F, arg3, arg4, arg5, 0.0F, arg6, arg7, arg8, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F)
            : this.mulGeneric3x3(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
    }

    private Matrix4f mulGeneric3x3(
        float float1, float float2, float float3, float float8, float float9, float float10, float float15, float float16, float float17, Matrix4f matrix4f1
    ) {
        float float0 = Math.fma(this.m00, float1, Math.fma(this.m10, float2, this.m20 * float3));
        float float4 = Math.fma(this.m01, float1, Math.fma(this.m11, float2, this.m21 * float3));
        float float5 = Math.fma(this.m02, float1, Math.fma(this.m12, float2, this.m22 * float3));
        float float6 = Math.fma(this.m03, float1, Math.fma(this.m13, float2, this.m23 * float3));
        float float7 = Math.fma(this.m00, float8, Math.fma(this.m10, float9, this.m20 * float10));
        float float11 = Math.fma(this.m01, float8, Math.fma(this.m11, float9, this.m21 * float10));
        float float12 = Math.fma(this.m02, float8, Math.fma(this.m12, float9, this.m22 * float10));
        float float13 = Math.fma(this.m03, float8, Math.fma(this.m13, float9, this.m23 * float10));
        float float14 = Math.fma(this.m00, float15, Math.fma(this.m10, float16, this.m20 * float17));
        float float18 = Math.fma(this.m01, float15, Math.fma(this.m11, float16, this.m21 * float17));
        float float19 = Math.fma(this.m02, float15, Math.fma(this.m12, float16, this.m22 * float17));
        float float20 = Math.fma(this.m03, float15, Math.fma(this.m13, float16, this.m23 * float17));
        return matrix4f1._m00(float0)
            ._m01(float4)
            ._m02(float5)
            ._m03(float6)
            ._m10(float7)
            ._m11(float11)
            ._m12(float12)
            ._m13(float13)
            ._m20(float14)
            ._m21(float18)
            ._m22(float19)
            ._m23(float20)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & 2);
    }

    public Matrix4f mulLocal(Matrix4fc arg0) {
        return this.mulLocal(arg0, this);
    }

    @Override
    public Matrix4f mulLocal(Matrix4fc arg0, Matrix4f arg1) {
        if ((this.properties & 4) != 0) {
            return arg1.set(arg0);
        } else if ((arg0.properties() & 4) != 0) {
            return arg1.set(this);
        } else {
            return (this.properties & 2) != 0 && (arg0.properties() & 2) != 0 ? this.mulLocalAffine(arg0, arg1) : this.mulLocalGeneric(arg0, arg1);
        }
    }

    private Matrix4f mulLocalGeneric(Matrix4fc matrix4fc, Matrix4f matrix4f1) {
        float float0 = Math.fma(matrix4fc.m00(), this.m00, Math.fma(matrix4fc.m10(), this.m01, Math.fma(matrix4fc.m20(), this.m02, matrix4fc.m30() * this.m03)));
        float float1 = Math.fma(matrix4fc.m01(), this.m00, Math.fma(matrix4fc.m11(), this.m01, Math.fma(matrix4fc.m21(), this.m02, matrix4fc.m31() * this.m03)));
        float float2 = Math.fma(matrix4fc.m02(), this.m00, Math.fma(matrix4fc.m12(), this.m01, Math.fma(matrix4fc.m22(), this.m02, matrix4fc.m32() * this.m03)));
        float float3 = Math.fma(matrix4fc.m03(), this.m00, Math.fma(matrix4fc.m13(), this.m01, Math.fma(matrix4fc.m23(), this.m02, matrix4fc.m33() * this.m03)));
        float float4 = Math.fma(matrix4fc.m00(), this.m10, Math.fma(matrix4fc.m10(), this.m11, Math.fma(matrix4fc.m20(), this.m12, matrix4fc.m30() * this.m13)));
        float float5 = Math.fma(matrix4fc.m01(), this.m10, Math.fma(matrix4fc.m11(), this.m11, Math.fma(matrix4fc.m21(), this.m12, matrix4fc.m31() * this.m13)));
        float float6 = Math.fma(matrix4fc.m02(), this.m10, Math.fma(matrix4fc.m12(), this.m11, Math.fma(matrix4fc.m22(), this.m12, matrix4fc.m32() * this.m13)));
        float float7 = Math.fma(matrix4fc.m03(), this.m10, Math.fma(matrix4fc.m13(), this.m11, Math.fma(matrix4fc.m23(), this.m12, matrix4fc.m33() * this.m13)));
        float float8 = Math.fma(matrix4fc.m00(), this.m20, Math.fma(matrix4fc.m10(), this.m21, Math.fma(matrix4fc.m20(), this.m22, matrix4fc.m30() * this.m23)));
        float float9 = Math.fma(matrix4fc.m01(), this.m20, Math.fma(matrix4fc.m11(), this.m21, Math.fma(matrix4fc.m21(), this.m22, matrix4fc.m31() * this.m23)));
        float float10 = Math.fma(
            matrix4fc.m02(), this.m20, Math.fma(matrix4fc.m12(), this.m21, Math.fma(matrix4fc.m22(), this.m22, matrix4fc.m32() * this.m23))
        );
        float float11 = Math.fma(
            matrix4fc.m03(), this.m20, Math.fma(matrix4fc.m13(), this.m21, Math.fma(matrix4fc.m23(), this.m22, matrix4fc.m33() * this.m23))
        );
        float float12 = Math.fma(
            matrix4fc.m00(), this.m30, Math.fma(matrix4fc.m10(), this.m31, Math.fma(matrix4fc.m20(), this.m32, matrix4fc.m30() * this.m33))
        );
        float float13 = Math.fma(
            matrix4fc.m01(), this.m30, Math.fma(matrix4fc.m11(), this.m31, Math.fma(matrix4fc.m21(), this.m32, matrix4fc.m31() * this.m33))
        );
        float float14 = Math.fma(
            matrix4fc.m02(), this.m30, Math.fma(matrix4fc.m12(), this.m31, Math.fma(matrix4fc.m22(), this.m32, matrix4fc.m32() * this.m33))
        );
        float float15 = Math.fma(
            matrix4fc.m03(), this.m30, Math.fma(matrix4fc.m13(), this.m31, Math.fma(matrix4fc.m23(), this.m32, matrix4fc.m33() * this.m33))
        );
        return matrix4f1._m00(float0)
            ._m01(float1)
            ._m02(float2)
            ._m03(float3)
            ._m10(float4)
            ._m11(float5)
            ._m12(float6)
            ._m13(float7)
            ._m20(float8)
            ._m21(float9)
            ._m22(float10)
            ._m23(float11)
            ._m30(float12)
            ._m31(float13)
            ._m32(float14)
            ._m33(float15)
            ._properties(0);
    }

    public Matrix4f mulLocalAffine(Matrix4fc arg0) {
        return this.mulLocalAffine(arg0, this);
    }

    @Override
    public Matrix4f mulLocalAffine(Matrix4fc arg0, Matrix4f arg1) {
        float float0 = arg0.m00() * this.m00 + arg0.m10() * this.m01 + arg0.m20() * this.m02;
        float float1 = arg0.m01() * this.m00 + arg0.m11() * this.m01 + arg0.m21() * this.m02;
        float float2 = arg0.m02() * this.m00 + arg0.m12() * this.m01 + arg0.m22() * this.m02;
        float float3 = arg0.m03();
        float float4 = arg0.m00() * this.m10 + arg0.m10() * this.m11 + arg0.m20() * this.m12;
        float float5 = arg0.m01() * this.m10 + arg0.m11() * this.m11 + arg0.m21() * this.m12;
        float float6 = arg0.m02() * this.m10 + arg0.m12() * this.m11 + arg0.m22() * this.m12;
        float float7 = arg0.m13();
        float float8 = arg0.m00() * this.m20 + arg0.m10() * this.m21 + arg0.m20() * this.m22;
        float float9 = arg0.m01() * this.m20 + arg0.m11() * this.m21 + arg0.m21() * this.m22;
        float float10 = arg0.m02() * this.m20 + arg0.m12() * this.m21 + arg0.m22() * this.m22;
        float float11 = arg0.m23();
        float float12 = arg0.m00() * this.m30 + arg0.m10() * this.m31 + arg0.m20() * this.m32 + arg0.m30();
        float float13 = arg0.m01() * this.m30 + arg0.m11() * this.m31 + arg0.m21() * this.m32 + arg0.m31();
        float float14 = arg0.m02() * this.m30 + arg0.m12() * this.m31 + arg0.m22() * this.m32 + arg0.m32();
        float float15 = arg0.m33();
        return arg1._m00(float0)
            ._m01(float1)
            ._m02(float2)
            ._m03(float3)
            ._m10(float4)
            ._m11(float5)
            ._m12(float6)
            ._m13(float7)
            ._m20(float8)
            ._m21(float9)
            ._m22(float10)
            ._m23(float11)
            ._m30(float12)
            ._m31(float13)
            ._m32(float14)
            ._m33(float15)
            ._properties(2 | this.properties() & arg0.properties() & 16);
    }

    public Matrix4f mul(Matrix4x3fc arg0) {
        return this.mul(arg0, this);
    }

    @Override
    public Matrix4f mul(Matrix4x3fc arg0, Matrix4f arg1) {
        if ((this.properties & 4) != 0) {
            return arg1.set(arg0);
        } else if ((arg0.properties() & 4) != 0) {
            return arg1.set(this);
        } else if ((this.properties & 8) != 0) {
            return this.mulTranslation(arg0, arg1);
        } else if ((this.properties & 2) != 0) {
            return this.mulAffine(arg0, arg1);
        } else {
            return (this.properties & 1) != 0 ? this.mulPerspectiveAffine(arg0, arg1) : this.mulGeneric(arg0, arg1);
        }
    }

    private Matrix4f mulTranslation(Matrix4x3fc matrix4x3fc, Matrix4f matrix4f1) {
        return matrix4f1._m00(matrix4x3fc.m00())
            ._m01(matrix4x3fc.m01())
            ._m02(matrix4x3fc.m02())
            ._m03(this.m03)
            ._m10(matrix4x3fc.m10())
            ._m11(matrix4x3fc.m11())
            ._m12(matrix4x3fc.m12())
            ._m13(this.m13)
            ._m20(matrix4x3fc.m20())
            ._m21(matrix4x3fc.m21())
            ._m22(matrix4x3fc.m22())
            ._m23(this.m23)
            ._m30(matrix4x3fc.m30() + this.m30)
            ._m31(matrix4x3fc.m31() + this.m31)
            ._m32(matrix4x3fc.m32() + this.m32)
            ._m33(this.m33)
            ._properties(2 | matrix4x3fc.properties() & 16);
    }

    private Matrix4f mulAffine(Matrix4x3fc matrix4x3fc, Matrix4f matrix4f1) {
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
        return matrix4f1._m00(Math.fma(float0, float9, Math.fma(float3, float10, float6 * float11)))
            ._m01(Math.fma(float1, float9, Math.fma(float4, float10, float7 * float11)))
            ._m02(Math.fma(float2, float9, Math.fma(float5, float10, float8 * float11)))
            ._m03(this.m03)
            ._m10(Math.fma(float0, float12, Math.fma(float3, float13, float6 * float14)))
            ._m11(Math.fma(float1, float12, Math.fma(float4, float13, float7 * float14)))
            ._m12(Math.fma(float2, float12, Math.fma(float5, float13, float8 * float14)))
            ._m13(this.m13)
            ._m20(Math.fma(float0, float15, Math.fma(float3, float16, float6 * float17)))
            ._m21(Math.fma(float1, float15, Math.fma(float4, float16, float7 * float17)))
            ._m22(Math.fma(float2, float15, Math.fma(float5, float16, float8 * float17)))
            ._m23(this.m23)
            ._m30(Math.fma(float0, float18, Math.fma(float3, float19, Math.fma(float6, float20, this.m30))))
            ._m31(Math.fma(float1, float18, Math.fma(float4, float19, Math.fma(float7, float20, this.m31))))
            ._m32(Math.fma(float2, float18, Math.fma(float5, float19, Math.fma(float8, float20, this.m32))))
            ._m33(this.m33)
            ._properties(2 | this.properties & matrix4x3fc.properties() & 16);
    }

    private Matrix4f mulGeneric(Matrix4x3fc matrix4x3fc, Matrix4f matrix4f1) {
        float float0 = Math.fma(this.m00, matrix4x3fc.m00(), Math.fma(this.m10, matrix4x3fc.m01(), this.m20 * matrix4x3fc.m02()));
        float float1 = Math.fma(this.m01, matrix4x3fc.m00(), Math.fma(this.m11, matrix4x3fc.m01(), this.m21 * matrix4x3fc.m02()));
        float float2 = Math.fma(this.m02, matrix4x3fc.m00(), Math.fma(this.m12, matrix4x3fc.m01(), this.m22 * matrix4x3fc.m02()));
        float float3 = Math.fma(this.m03, matrix4x3fc.m00(), Math.fma(this.m13, matrix4x3fc.m01(), this.m23 * matrix4x3fc.m02()));
        float float4 = Math.fma(this.m00, matrix4x3fc.m10(), Math.fma(this.m10, matrix4x3fc.m11(), this.m20 * matrix4x3fc.m12()));
        float float5 = Math.fma(this.m01, matrix4x3fc.m10(), Math.fma(this.m11, matrix4x3fc.m11(), this.m21 * matrix4x3fc.m12()));
        float float6 = Math.fma(this.m02, matrix4x3fc.m10(), Math.fma(this.m12, matrix4x3fc.m11(), this.m22 * matrix4x3fc.m12()));
        float float7 = Math.fma(this.m03, matrix4x3fc.m10(), Math.fma(this.m13, matrix4x3fc.m11(), this.m23 * matrix4x3fc.m12()));
        float float8 = Math.fma(this.m00, matrix4x3fc.m20(), Math.fma(this.m10, matrix4x3fc.m21(), this.m20 * matrix4x3fc.m22()));
        float float9 = Math.fma(this.m01, matrix4x3fc.m20(), Math.fma(this.m11, matrix4x3fc.m21(), this.m21 * matrix4x3fc.m22()));
        float float10 = Math.fma(this.m02, matrix4x3fc.m20(), Math.fma(this.m12, matrix4x3fc.m21(), this.m22 * matrix4x3fc.m22()));
        float float11 = Math.fma(this.m03, matrix4x3fc.m20(), Math.fma(this.m13, matrix4x3fc.m21(), this.m23 * matrix4x3fc.m22()));
        float float12 = Math.fma(this.m00, matrix4x3fc.m30(), Math.fma(this.m10, matrix4x3fc.m31(), Math.fma(this.m20, matrix4x3fc.m32(), this.m30)));
        float float13 = Math.fma(this.m01, matrix4x3fc.m30(), Math.fma(this.m11, matrix4x3fc.m31(), Math.fma(this.m21, matrix4x3fc.m32(), this.m31)));
        float float14 = Math.fma(this.m02, matrix4x3fc.m30(), Math.fma(this.m12, matrix4x3fc.m31(), Math.fma(this.m22, matrix4x3fc.m32(), this.m32)));
        float float15 = Math.fma(this.m03, matrix4x3fc.m30(), Math.fma(this.m13, matrix4x3fc.m31(), Math.fma(this.m23, matrix4x3fc.m32(), this.m33)));
        return matrix4f1._m00(float0)
            ._m01(float1)
            ._m02(float2)
            ._m03(float3)
            ._m10(float4)
            ._m11(float5)
            ._m12(float6)
            ._m13(float7)
            ._m20(float8)
            ._m21(float9)
            ._m22(float10)
            ._m23(float11)
            ._m30(float12)
            ._m31(float13)
            ._m32(float14)
            ._m33(float15)
            ._properties(this.properties & -30);
    }

    public Matrix4f mul(Matrix3x2fc arg0) {
        return this.mul(arg0, this);
    }

    @Override
    public Matrix4f mul(Matrix3x2fc arg0, Matrix4f arg1) {
        float float0 = this.m00 * arg0.m00() + this.m10 * arg0.m01();
        float float1 = this.m01 * arg0.m00() + this.m11 * arg0.m01();
        float float2 = this.m02 * arg0.m00() + this.m12 * arg0.m01();
        float float3 = this.m03 * arg0.m00() + this.m13 * arg0.m01();
        float float4 = this.m00 * arg0.m10() + this.m10 * arg0.m11();
        float float5 = this.m01 * arg0.m10() + this.m11 * arg0.m11();
        float float6 = this.m02 * arg0.m10() + this.m12 * arg0.m11();
        float float7 = this.m03 * arg0.m10() + this.m13 * arg0.m11();
        float float8 = this.m00 * arg0.m20() + this.m10 * arg0.m21() + this.m30;
        float float9 = this.m01 * arg0.m20() + this.m11 * arg0.m21() + this.m31;
        float float10 = this.m02 * arg0.m20() + this.m12 * arg0.m21() + this.m32;
        float float11 = this.m03 * arg0.m20() + this.m13 * arg0.m21() + this.m33;
        return arg1._m00(float0)
            ._m01(float1)
            ._m02(float2)
            ._m03(float3)
            ._m10(float4)
            ._m11(float5)
            ._m12(float6)
            ._m13(float7)
            ._m20(this.m20)
            ._m21(this.m21)
            ._m22(this.m22)
            ._m23(this.m23)
            ._m30(float8)
            ._m31(float9)
            ._m32(float10)
            ._m33(float11)
            ._properties(this.properties & -30);
    }

    public Matrix4f mulPerspectiveAffine(Matrix4fc arg0) {
        return this.mulPerspectiveAffine(arg0, this);
    }

    @Override
    public Matrix4f mulPerspectiveAffine(Matrix4fc arg0, Matrix4f arg1) {
        float float0 = this.m00 * arg0.m00();
        float float1 = this.m11 * arg0.m01();
        float float2 = this.m22 * arg0.m02();
        float float3 = this.m23 * arg0.m02();
        float float4 = this.m00 * arg0.m10();
        float float5 = this.m11 * arg0.m11();
        float float6 = this.m22 * arg0.m12();
        float float7 = this.m23 * arg0.m12();
        float float8 = this.m00 * arg0.m20();
        float float9 = this.m11 * arg0.m21();
        float float10 = this.m22 * arg0.m22();
        float float11 = this.m23 * arg0.m22();
        float float12 = this.m00 * arg0.m30();
        float float13 = this.m11 * arg0.m31();
        float float14 = this.m22 * arg0.m32() + this.m32;
        float float15 = this.m23 * arg0.m32();
        return arg1._m00(float0)
            ._m01(float1)
            ._m02(float2)
            ._m03(float3)
            ._m10(float4)
            ._m11(float5)
            ._m12(float6)
            ._m13(float7)
            ._m20(float8)
            ._m21(float9)
            ._m22(float10)
            ._m23(float11)
            ._m30(float12)
            ._m31(float13)
            ._m32(float14)
            ._m33(float15)
            ._properties(0);
    }

    public Matrix4f mulPerspectiveAffine(Matrix4x3fc arg0) {
        return this.mulPerspectiveAffine(arg0, this);
    }

    @Override
    public Matrix4f mulPerspectiveAffine(Matrix4x3fc arg0, Matrix4f arg1) {
        float float0 = this.m00;
        float float1 = this.m11;
        float float2 = this.m22;
        float float3 = this.m23;
        return arg1._m00(float0 * arg0.m00())
            ._m01(float1 * arg0.m01())
            ._m02(float2 * arg0.m02())
            ._m03(float3 * arg0.m02())
            ._m10(float0 * arg0.m10())
            ._m11(float1 * arg0.m11())
            ._m12(float2 * arg0.m12())
            ._m13(float3 * arg0.m12())
            ._m20(float0 * arg0.m20())
            ._m21(float1 * arg0.m21())
            ._m22(float2 * arg0.m22())
            ._m23(float3 * arg0.m22())
            ._m30(float0 * arg0.m30())
            ._m31(float1 * arg0.m31())
            ._m32(float2 * arg0.m32() + this.m32)
            ._m33(float3 * arg0.m32())
            ._properties(0);
    }

    public Matrix4f mulAffineR(Matrix4fc arg0) {
        return this.mulAffineR(arg0, this);
    }

    @Override
    public Matrix4f mulAffineR(Matrix4fc arg0, Matrix4f arg1) {
        float float0 = Math.fma(this.m00, arg0.m00(), Math.fma(this.m10, arg0.m01(), this.m20 * arg0.m02()));
        float float1 = Math.fma(this.m01, arg0.m00(), Math.fma(this.m11, arg0.m01(), this.m21 * arg0.m02()));
        float float2 = Math.fma(this.m02, arg0.m00(), Math.fma(this.m12, arg0.m01(), this.m22 * arg0.m02()));
        float float3 = Math.fma(this.m03, arg0.m00(), Math.fma(this.m13, arg0.m01(), this.m23 * arg0.m02()));
        float float4 = Math.fma(this.m00, arg0.m10(), Math.fma(this.m10, arg0.m11(), this.m20 * arg0.m12()));
        float float5 = Math.fma(this.m01, arg0.m10(), Math.fma(this.m11, arg0.m11(), this.m21 * arg0.m12()));
        float float6 = Math.fma(this.m02, arg0.m10(), Math.fma(this.m12, arg0.m11(), this.m22 * arg0.m12()));
        float float7 = Math.fma(this.m03, arg0.m10(), Math.fma(this.m13, arg0.m11(), this.m23 * arg0.m12()));
        float float8 = Math.fma(this.m00, arg0.m20(), Math.fma(this.m10, arg0.m21(), this.m20 * arg0.m22()));
        float float9 = Math.fma(this.m01, arg0.m20(), Math.fma(this.m11, arg0.m21(), this.m21 * arg0.m22()));
        float float10 = Math.fma(this.m02, arg0.m20(), Math.fma(this.m12, arg0.m21(), this.m22 * arg0.m22()));
        float float11 = Math.fma(this.m03, arg0.m20(), Math.fma(this.m13, arg0.m21(), this.m23 * arg0.m22()));
        float float12 = Math.fma(this.m00, arg0.m30(), Math.fma(this.m10, arg0.m31(), Math.fma(this.m20, arg0.m32(), this.m30)));
        float float13 = Math.fma(this.m01, arg0.m30(), Math.fma(this.m11, arg0.m31(), Math.fma(this.m21, arg0.m32(), this.m31)));
        float float14 = Math.fma(this.m02, arg0.m30(), Math.fma(this.m12, arg0.m31(), Math.fma(this.m22, arg0.m32(), this.m32)));
        float float15 = Math.fma(this.m03, arg0.m30(), Math.fma(this.m13, arg0.m31(), Math.fma(this.m23, arg0.m32(), this.m33)));
        return arg1._m00(float0)
            ._m01(float1)
            ._m02(float2)
            ._m03(float3)
            ._m10(float4)
            ._m11(float5)
            ._m12(float6)
            ._m13(float7)
            ._m20(float8)
            ._m21(float9)
            ._m22(float10)
            ._m23(float11)
            ._m30(float12)
            ._m31(float13)
            ._m32(float14)
            ._m33(float15)
            ._properties(this.properties & -30);
    }

    public Matrix4f mulAffine(Matrix4fc arg0) {
        return this.mulAffine(arg0, this);
    }

    @Override
    public Matrix4f mulAffine(Matrix4fc arg0, Matrix4f arg1) {
        float float0 = this.m00;
        float float1 = this.m01;
        float float2 = this.m02;
        float float3 = this.m10;
        float float4 = this.m11;
        float float5 = this.m12;
        float float6 = this.m20;
        float float7 = this.m21;
        float float8 = this.m22;
        float float9 = arg0.m00();
        float float10 = arg0.m01();
        float float11 = arg0.m02();
        float float12 = arg0.m10();
        float float13 = arg0.m11();
        float float14 = arg0.m12();
        float float15 = arg0.m20();
        float float16 = arg0.m21();
        float float17 = arg0.m22();
        float float18 = arg0.m30();
        float float19 = arg0.m31();
        float float20 = arg0.m32();
        return arg1._m00(Math.fma(float0, float9, Math.fma(float3, float10, float6 * float11)))
            ._m01(Math.fma(float1, float9, Math.fma(float4, float10, float7 * float11)))
            ._m02(Math.fma(float2, float9, Math.fma(float5, float10, float8 * float11)))
            ._m03(this.m03)
            ._m10(Math.fma(float0, float12, Math.fma(float3, float13, float6 * float14)))
            ._m11(Math.fma(float1, float12, Math.fma(float4, float13, float7 * float14)))
            ._m12(Math.fma(float2, float12, Math.fma(float5, float13, float8 * float14)))
            ._m13(this.m13)
            ._m20(Math.fma(float0, float15, Math.fma(float3, float16, float6 * float17)))
            ._m21(Math.fma(float1, float15, Math.fma(float4, float16, float7 * float17)))
            ._m22(Math.fma(float2, float15, Math.fma(float5, float16, float8 * float17)))
            ._m23(this.m23)
            ._m30(Math.fma(float0, float18, Math.fma(float3, float19, Math.fma(float6, float20, this.m30))))
            ._m31(Math.fma(float1, float18, Math.fma(float4, float19, Math.fma(float7, float20, this.m31))))
            ._m32(Math.fma(float2, float18, Math.fma(float5, float19, Math.fma(float8, float20, this.m32))))
            ._m33(this.m33)
            ._properties(2 | this.properties & arg0.properties() & 16);
    }

    @Override
    public Matrix4f mulTranslationAffine(Matrix4fc arg0, Matrix4f arg1) {
        return arg1._m00(arg0.m00())
            ._m01(arg0.m01())
            ._m02(arg0.m02())
            ._m03(this.m03)
            ._m10(arg0.m10())
            ._m11(arg0.m11())
            ._m12(arg0.m12())
            ._m13(this.m13)
            ._m20(arg0.m20())
            ._m21(arg0.m21())
            ._m22(arg0.m22())
            ._m23(this.m23)
            ._m30(arg0.m30() + this.m30)
            ._m31(arg0.m31() + this.m31)
            ._m32(arg0.m32() + this.m32)
            ._m33(this.m33)
            ._properties(2 | arg0.properties() & 16);
    }

    public Matrix4f mulOrthoAffine(Matrix4fc arg0) {
        return this.mulOrthoAffine(arg0, this);
    }

    @Override
    public Matrix4f mulOrthoAffine(Matrix4fc arg0, Matrix4f arg1) {
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
        return arg1._m00(float0)
            ._m01(float1)
            ._m02(float2)
            ._m03(0.0F)
            ._m10(float3)
            ._m11(float4)
            ._m12(float5)
            ._m13(0.0F)
            ._m20(float6)
            ._m21(float7)
            ._m22(float8)
            ._m23(0.0F)
            ._m30(float9)
            ._m31(float10)
            ._m32(float11)
            ._m33(1.0F)
            ._properties(2);
    }

    public Matrix4f fma4x3(Matrix4fc arg0, float arg1) {
        return this.fma4x3(arg0, arg1, this);
    }

    @Override
    public Matrix4f fma4x3(Matrix4fc arg0, float arg1, Matrix4f arg2) {
        arg2._m00(Math.fma(arg0.m00(), arg1, this.m00))
            ._m01(Math.fma(arg0.m01(), arg1, this.m01))
            ._m02(Math.fma(arg0.m02(), arg1, this.m02))
            ._m03(this.m03)
            ._m10(Math.fma(arg0.m10(), arg1, this.m10))
            ._m11(Math.fma(arg0.m11(), arg1, this.m11))
            ._m12(Math.fma(arg0.m12(), arg1, this.m12))
            ._m13(this.m13)
            ._m20(Math.fma(arg0.m20(), arg1, this.m20))
            ._m21(Math.fma(arg0.m21(), arg1, this.m21))
            ._m22(Math.fma(arg0.m22(), arg1, this.m22))
            ._m23(this.m23)
            ._m30(Math.fma(arg0.m30(), arg1, this.m30))
            ._m31(Math.fma(arg0.m31(), arg1, this.m31))
            ._m32(Math.fma(arg0.m32(), arg1, this.m32))
            ._m33(this.m33)
            ._properties(0);
        return arg2;
    }

    public Matrix4f add(Matrix4fc arg0) {
        return this.add(arg0, this);
    }

    @Override
    public Matrix4f add(Matrix4fc arg0, Matrix4f arg1) {
        arg1._m00(this.m00 + arg0.m00())
            ._m01(this.m01 + arg0.m01())
            ._m02(this.m02 + arg0.m02())
            ._m03(this.m03 + arg0.m03())
            ._m10(this.m10 + arg0.m10())
            ._m11(this.m11 + arg0.m11())
            ._m12(this.m12 + arg0.m12())
            ._m13(this.m13 + arg0.m13())
            ._m20(this.m20 + arg0.m20())
            ._m21(this.m21 + arg0.m21())
            ._m22(this.m22 + arg0.m22())
            ._m23(this.m23 + arg0.m23())
            ._m30(this.m30 + arg0.m30())
            ._m31(this.m31 + arg0.m31())
            ._m32(this.m32 + arg0.m32())
            ._m33(this.m33 + arg0.m33())
            ._properties(0);
        return arg1;
    }

    public Matrix4f sub(Matrix4fc arg0) {
        return this.sub(arg0, this);
    }

    @Override
    public Matrix4f sub(Matrix4fc arg0, Matrix4f arg1) {
        arg1._m00(this.m00 - arg0.m00())
            ._m01(this.m01 - arg0.m01())
            ._m02(this.m02 - arg0.m02())
            ._m03(this.m03 - arg0.m03())
            ._m10(this.m10 - arg0.m10())
            ._m11(this.m11 - arg0.m11())
            ._m12(this.m12 - arg0.m12())
            ._m13(this.m13 - arg0.m13())
            ._m20(this.m20 - arg0.m20())
            ._m21(this.m21 - arg0.m21())
            ._m22(this.m22 - arg0.m22())
            ._m23(this.m23 - arg0.m23())
            ._m30(this.m30 - arg0.m30())
            ._m31(this.m31 - arg0.m31())
            ._m32(this.m32 - arg0.m32())
            ._m33(this.m33 - arg0.m33())
            ._properties(0);
        return arg1;
    }

    public Matrix4f mulComponentWise(Matrix4fc arg0) {
        return this.mulComponentWise(arg0, this);
    }

    @Override
    public Matrix4f mulComponentWise(Matrix4fc arg0, Matrix4f arg1) {
        arg1._m00(this.m00 * arg0.m00())
            ._m01(this.m01 * arg0.m01())
            ._m02(this.m02 * arg0.m02())
            ._m03(this.m03 * arg0.m03())
            ._m10(this.m10 * arg0.m10())
            ._m11(this.m11 * arg0.m11())
            ._m12(this.m12 * arg0.m12())
            ._m13(this.m13 * arg0.m13())
            ._m20(this.m20 * arg0.m20())
            ._m21(this.m21 * arg0.m21())
            ._m22(this.m22 * arg0.m22())
            ._m23(this.m23 * arg0.m23())
            ._m30(this.m30 * arg0.m30())
            ._m31(this.m31 * arg0.m31())
            ._m32(this.m32 * arg0.m32())
            ._m33(this.m33 * arg0.m33())
            ._properties(0);
        return arg1;
    }

    public Matrix4f add4x3(Matrix4fc arg0) {
        return this.add4x3(arg0, this);
    }

    @Override
    public Matrix4f add4x3(Matrix4fc arg0, Matrix4f arg1) {
        arg1._m00(this.m00 + arg0.m00())
            ._m01(this.m01 + arg0.m01())
            ._m02(this.m02 + arg0.m02())
            ._m03(this.m03)
            ._m10(this.m10 + arg0.m10())
            ._m11(this.m11 + arg0.m11())
            ._m12(this.m12 + arg0.m12())
            ._m13(this.m13)
            ._m20(this.m20 + arg0.m20())
            ._m21(this.m21 + arg0.m21())
            ._m22(this.m22 + arg0.m22())
            ._m23(this.m23)
            ._m30(this.m30 + arg0.m30())
            ._m31(this.m31 + arg0.m31())
            ._m32(this.m32 + arg0.m32())
            ._m33(this.m33)
            ._properties(0);
        return arg1;
    }

    public Matrix4f sub4x3(Matrix4f arg0) {
        return this.sub4x3(arg0, this);
    }

    @Override
    public Matrix4f sub4x3(Matrix4fc arg0, Matrix4f arg1) {
        arg1._m00(this.m00 - arg0.m00())
            ._m01(this.m01 - arg0.m01())
            ._m02(this.m02 - arg0.m02())
            ._m03(this.m03)
            ._m10(this.m10 - arg0.m10())
            ._m11(this.m11 - arg0.m11())
            ._m12(this.m12 - arg0.m12())
            ._m13(this.m13)
            ._m20(this.m20 - arg0.m20())
            ._m21(this.m21 - arg0.m21())
            ._m22(this.m22 - arg0.m22())
            ._m23(this.m23)
            ._m30(this.m30 - arg0.m30())
            ._m31(this.m31 - arg0.m31())
            ._m32(this.m32 - arg0.m32())
            ._m33(this.m33)
            ._properties(0);
        return arg1;
    }

    public Matrix4f mul4x3ComponentWise(Matrix4fc arg0) {
        return this.mul4x3ComponentWise(arg0, this);
    }

    @Override
    public Matrix4f mul4x3ComponentWise(Matrix4fc arg0, Matrix4f arg1) {
        arg1._m00(this.m00 * arg0.m00())
            ._m01(this.m01 * arg0.m01())
            ._m02(this.m02 * arg0.m02())
            ._m03(this.m03)
            ._m10(this.m10 * arg0.m10())
            ._m11(this.m11 * arg0.m11())
            ._m12(this.m12 * arg0.m12())
            ._m13(this.m13)
            ._m20(this.m20 * arg0.m20())
            ._m21(this.m21 * arg0.m21())
            ._m22(this.m22 * arg0.m22())
            ._m23(this.m23)
            ._m30(this.m30 * arg0.m30())
            ._m31(this.m31 * arg0.m31())
            ._m32(this.m32 * arg0.m32())
            ._m33(this.m33)
            ._properties(0);
        return arg1;
    }

    public Matrix4f set(
        float arg0,
        float arg1,
        float arg2,
        float arg3,
        float arg4,
        float arg5,
        float arg6,
        float arg7,
        float arg8,
        float arg9,
        float arg10,
        float arg11,
        float arg12,
        float arg13,
        float arg14,
        float arg15
    ) {
        return this._m00(arg0)
            ._m10(arg4)
            ._m20(arg8)
            ._m30(arg12)
            ._m01(arg1)
            ._m11(arg5)
            ._m21(arg9)
            ._m31(arg13)
            ._m02(arg2)
            ._m12(arg6)
            ._m22(arg10)
            ._m32(arg14)
            ._m03(arg3)
            ._m13(arg7)
            ._m23(arg11)
            ._m33(arg15)
            .determineProperties();
    }

    public Matrix4f set(float[] floats, int int0) {
        MemUtil.INSTANCE.copy(floats, int0, this);
        return this.determineProperties();
    }

    public Matrix4f set(float[] floats) {
        return this.set(floats, 0);
    }

    public Matrix4f setTransposed(float[] floats, int int0) {
        MemUtil.INSTANCE.copyTransposed(floats, int0, this);
        return this.determineProperties();
    }

    public Matrix4f setTransposed(float[] floats) {
        return this.setTransposed(floats, 0);
    }

    public Matrix4f set(FloatBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this.determineProperties();
    }

    public Matrix4f set(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this.determineProperties();
    }

    public Matrix4f setTransposed(FloatBuffer arg0) {
        MemUtil.INSTANCE.getTransposed(this, arg0.position(), arg0);
        return this.determineProperties();
    }

    public Matrix4f setTransposed(ByteBuffer arg0) {
        MemUtil.INSTANCE.getTransposed(this, arg0.position(), arg0);
        return this.determineProperties();
    }

    public Matrix4f setFromAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.get(this, arg0);
            return this.determineProperties();
        }
    }

    public Matrix4f setTransposedFromAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.getTransposed(this, arg0);
            return this.determineProperties();
        }
    }

    public Matrix4f set(Vector4fc arg0, Vector4fc arg1, Vector4fc arg2, Vector4fc arg3) {
        return this._m00(arg0.x())
            ._m01(arg0.y())
            ._m02(arg0.z())
            ._m03(arg0.w())
            ._m10(arg1.x())
            ._m11(arg1.y())
            ._m12(arg1.z())
            ._m13(arg1.w())
            ._m20(arg2.x())
            ._m21(arg2.y())
            ._m22(arg2.z())
            ._m23(arg2.w())
            ._m30(arg3.x())
            ._m31(arg3.y())
            ._m32(arg3.z())
            ._m33(arg3.w())
            .determineProperties();
    }

    @Override
    public float determinant() {
        return (this.properties & 2) != 0
            ? this.determinantAffine()
            : (this.m00 * this.m11 - this.m01 * this.m10) * (this.m22 * this.m33 - this.m23 * this.m32)
                + (this.m02 * this.m10 - this.m00 * this.m12) * (this.m21 * this.m33 - this.m23 * this.m31)
                + (this.m00 * this.m13 - this.m03 * this.m10) * (this.m21 * this.m32 - this.m22 * this.m31)
                + (this.m01 * this.m12 - this.m02 * this.m11) * (this.m20 * this.m33 - this.m23 * this.m30)
                + (this.m03 * this.m11 - this.m01 * this.m13) * (this.m20 * this.m32 - this.m22 * this.m30)
                + (this.m02 * this.m13 - this.m03 * this.m12) * (this.m20 * this.m31 - this.m21 * this.m30);
    }

    @Override
    public float determinant3x3() {
        return (this.m00 * this.m11 - this.m01 * this.m10) * this.m22
            + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21
            + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
    }

    @Override
    public float determinantAffine() {
        return (this.m00 * this.m11 - this.m01 * this.m10) * this.m22
            + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21
            + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
    }

    @Override
    public Matrix4f invert(Matrix4f arg0) {
        if ((this.properties & 4) != 0) {
            return arg0.identity();
        } else if ((this.properties & 8) != 0) {
            return this.invertTranslation(arg0);
        } else if ((this.properties & 16) != 0) {
            return this.invertOrthonormal(arg0);
        } else if ((this.properties & 2) != 0) {
            return this.invertAffine(arg0);
        } else {
            return (this.properties & 1) != 0 ? this.invertPerspective(arg0) : this.invertGeneric(arg0);
        }
    }

    private Matrix4f invertTranslation(Matrix4f matrix4f0) {
        if (matrix4f0 != this) {
            matrix4f0.set(this);
        }

        return matrix4f0._m30(-this.m30)._m31(-this.m31)._m32(-this.m32)._properties(26);
    }

    private Matrix4f invertOrthonormal(Matrix4f matrix4f1) {
        float float0 = -(this.m00 * this.m30 + this.m01 * this.m31 + this.m02 * this.m32);
        float float1 = -(this.m10 * this.m30 + this.m11 * this.m31 + this.m12 * this.m32);
        float float2 = -(this.m20 * this.m30 + this.m21 * this.m31 + this.m22 * this.m32);
        float float3 = this.m01;
        float float4 = this.m02;
        float float5 = this.m12;
        return matrix4f1._m00(this.m00)
            ._m01(this.m10)
            ._m02(this.m20)
            ._m03(0.0F)
            ._m10(float3)
            ._m11(this.m11)
            ._m12(this.m21)
            ._m13(0.0F)
            ._m20(float4)
            ._m21(float5)
            ._m22(this.m22)
            ._m23(0.0F)
            ._m30(float0)
            ._m31(float1)
            ._m32(float2)
            ._m33(1.0F)
            ._properties(18);
    }

    private Matrix4f invertGeneric(Matrix4f matrix4f1) {
        return this != matrix4f1 ? this.invertGenericNonThis(matrix4f1) : this.invertGenericThis(matrix4f1);
    }

    private Matrix4f invertGenericNonThis(Matrix4f matrix4f1) {
        float float0 = this.m00 * this.m11 - this.m01 * this.m10;
        float float1 = this.m00 * this.m12 - this.m02 * this.m10;
        float float2 = this.m00 * this.m13 - this.m03 * this.m10;
        float float3 = this.m01 * this.m12 - this.m02 * this.m11;
        float float4 = this.m01 * this.m13 - this.m03 * this.m11;
        float float5 = this.m02 * this.m13 - this.m03 * this.m12;
        float float6 = this.m20 * this.m31 - this.m21 * this.m30;
        float float7 = this.m20 * this.m32 - this.m22 * this.m30;
        float float8 = this.m20 * this.m33 - this.m23 * this.m30;
        float float9 = this.m21 * this.m32 - this.m22 * this.m31;
        float float10 = this.m21 * this.m33 - this.m23 * this.m31;
        float float11 = this.m22 * this.m33 - this.m23 * this.m32;
        float float12 = float0 * float11 - float1 * float10 + float2 * float9 + float3 * float8 - float4 * float7 + float5 * float6;
        float12 = 1.0F / float12;
        return matrix4f1._m00(Math.fma(this.m11, float11, Math.fma(-this.m12, float10, this.m13 * float9)) * float12)
            ._m01(Math.fma(-this.m01, float11, Math.fma(this.m02, float10, -this.m03 * float9)) * float12)
            ._m02(Math.fma(this.m31, float5, Math.fma(-this.m32, float4, this.m33 * float3)) * float12)
            ._m03(Math.fma(-this.m21, float5, Math.fma(this.m22, float4, -this.m23 * float3)) * float12)
            ._m10(Math.fma(-this.m10, float11, Math.fma(this.m12, float8, -this.m13 * float7)) * float12)
            ._m11(Math.fma(this.m00, float11, Math.fma(-this.m02, float8, this.m03 * float7)) * float12)
            ._m12(Math.fma(-this.m30, float5, Math.fma(this.m32, float2, -this.m33 * float1)) * float12)
            ._m13(Math.fma(this.m20, float5, Math.fma(-this.m22, float2, this.m23 * float1)) * float12)
            ._m20(Math.fma(this.m10, float10, Math.fma(-this.m11, float8, this.m13 * float6)) * float12)
            ._m21(Math.fma(-this.m00, float10, Math.fma(this.m01, float8, -this.m03 * float6)) * float12)
            ._m22(Math.fma(this.m30, float4, Math.fma(-this.m31, float2, this.m33 * float0)) * float12)
            ._m23(Math.fma(-this.m20, float4, Math.fma(this.m21, float2, -this.m23 * float0)) * float12)
            ._m30(Math.fma(-this.m10, float9, Math.fma(this.m11, float7, -this.m12 * float6)) * float12)
            ._m31(Math.fma(this.m00, float9, Math.fma(-this.m01, float7, this.m02 * float6)) * float12)
            ._m32(Math.fma(-this.m30, float3, Math.fma(this.m31, float1, -this.m32 * float0)) * float12)
            ._m33(Math.fma(this.m20, float3, Math.fma(-this.m21, float1, this.m22 * float0)) * float12)
            ._properties(0);
    }

    private Matrix4f invertGenericThis(Matrix4f matrix4f1) {
        float float0 = this.m00 * this.m11 - this.m01 * this.m10;
        float float1 = this.m00 * this.m12 - this.m02 * this.m10;
        float float2 = this.m00 * this.m13 - this.m03 * this.m10;
        float float3 = this.m01 * this.m12 - this.m02 * this.m11;
        float float4 = this.m01 * this.m13 - this.m03 * this.m11;
        float float5 = this.m02 * this.m13 - this.m03 * this.m12;
        float float6 = this.m20 * this.m31 - this.m21 * this.m30;
        float float7 = this.m20 * this.m32 - this.m22 * this.m30;
        float float8 = this.m20 * this.m33 - this.m23 * this.m30;
        float float9 = this.m21 * this.m32 - this.m22 * this.m31;
        float float10 = this.m21 * this.m33 - this.m23 * this.m31;
        float float11 = this.m22 * this.m33 - this.m23 * this.m32;
        float float12 = float0 * float11 - float1 * float10 + float2 * float9 + float3 * float8 - float4 * float7 + float5 * float6;
        float12 = 1.0F / float12;
        float float13 = Math.fma(this.m11, float11, Math.fma(-this.m12, float10, this.m13 * float9)) * float12;
        float float14 = Math.fma(-this.m01, float11, Math.fma(this.m02, float10, -this.m03 * float9)) * float12;
        float float15 = Math.fma(this.m31, float5, Math.fma(-this.m32, float4, this.m33 * float3)) * float12;
        float float16 = Math.fma(-this.m21, float5, Math.fma(this.m22, float4, -this.m23 * float3)) * float12;
        float float17 = Math.fma(-this.m10, float11, Math.fma(this.m12, float8, -this.m13 * float7)) * float12;
        float float18 = Math.fma(this.m00, float11, Math.fma(-this.m02, float8, this.m03 * float7)) * float12;
        float float19 = Math.fma(-this.m30, float5, Math.fma(this.m32, float2, -this.m33 * float1)) * float12;
        float float20 = Math.fma(this.m20, float5, Math.fma(-this.m22, float2, this.m23 * float1)) * float12;
        float float21 = Math.fma(this.m10, float10, Math.fma(-this.m11, float8, this.m13 * float6)) * float12;
        float float22 = Math.fma(-this.m00, float10, Math.fma(this.m01, float8, -this.m03 * float6)) * float12;
        float float23 = Math.fma(this.m30, float4, Math.fma(-this.m31, float2, this.m33 * float0)) * float12;
        float float24 = Math.fma(-this.m20, float4, Math.fma(this.m21, float2, -this.m23 * float0)) * float12;
        float float25 = Math.fma(-this.m10, float9, Math.fma(this.m11, float7, -this.m12 * float6)) * float12;
        float float26 = Math.fma(this.m00, float9, Math.fma(-this.m01, float7, this.m02 * float6)) * float12;
        float float27 = Math.fma(-this.m30, float3, Math.fma(this.m31, float1, -this.m32 * float0)) * float12;
        float float28 = Math.fma(this.m20, float3, Math.fma(-this.m21, float1, this.m22 * float0)) * float12;
        return matrix4f1._m00(float13)
            ._m01(float14)
            ._m02(float15)
            ._m03(float16)
            ._m10(float17)
            ._m11(float18)
            ._m12(float19)
            ._m13(float20)
            ._m20(float21)
            ._m21(float22)
            ._m22(float23)
            ._m23(float24)
            ._m30(float25)
            ._m31(float26)
            ._m32(float27)
            ._m33(float28)
            ._properties(0);
    }

    public Matrix4f invert() {
        return this.invert(this);
    }

    @Override
    public Matrix4f invertPerspective(Matrix4f arg0) {
        float float0 = 1.0F / (this.m00 * this.m11);
        float float1 = -1.0F / (this.m23 * this.m32);
        return arg0.set(
                this.m11 * float0,
                0.0F,
                0.0F,
                0.0F,
                0.0F,
                this.m00 * float0,
                0.0F,
                0.0F,
                0.0F,
                0.0F,
                0.0F,
                -this.m23 * float1,
                0.0F,
                0.0F,
                -this.m32 * float1,
                this.m22 * float1
            )
            ._properties(0);
    }

    public Matrix4f invertPerspective() {
        return this.invertPerspective(this);
    }

    @Override
    public Matrix4f invertFrustum(Matrix4f arg0) {
        float float0 = 1.0F / this.m00;
        float float1 = 1.0F / this.m11;
        float float2 = 1.0F / this.m23;
        float float3 = 1.0F / this.m32;
        return arg0.set(
            float0,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            float1,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            float3,
            -this.m20 * float0 * float2,
            -this.m21 * float1 * float2,
            float2,
            -this.m22 * float2 * float3
        );
    }

    public Matrix4f invertFrustum() {
        return this.invertFrustum(this);
    }

    @Override
    public Matrix4f invertOrtho(Matrix4f arg0) {
        float float0 = 1.0F / this.m00;
        float float1 = 1.0F / this.m11;
        float float2 = 1.0F / this.m22;
        return arg0.set(
                float0, 0.0F, 0.0F, 0.0F, 0.0F, float1, 0.0F, 0.0F, 0.0F, 0.0F, float2, 0.0F, -this.m30 * float0, -this.m31 * float1, -this.m32 * float2, 1.0F
            )
            ._properties(2 | this.properties & 16);
    }

    public Matrix4f invertOrtho() {
        return this.invertOrtho(this);
    }

    @Override
    public Matrix4f invertPerspectiveView(Matrix4fc arg0, Matrix4f arg1) {
        float float0 = 1.0F / (this.m00 * this.m11);
        float float1 = -1.0F / (this.m23 * this.m32);
        float float2 = this.m11 * float0;
        float float3 = this.m00 * float0;
        float float4 = -this.m23 * float1;
        float float5 = -this.m32 * float1;
        float float6 = this.m22 * float1;
        float float7 = -arg0.m00() * arg0.m30() - arg0.m01() * arg0.m31() - arg0.m02() * arg0.m32();
        float float8 = -arg0.m10() * arg0.m30() - arg0.m11() * arg0.m31() - arg0.m12() * arg0.m32();
        float float9 = -arg0.m20() * arg0.m30() - arg0.m21() * arg0.m31() - arg0.m22() * arg0.m32();
        float float10 = arg0.m01() * float3;
        float float11 = arg0.m02() * float5 + float7 * float6;
        float float12 = arg0.m12() * float5 + float8 * float6;
        float float13 = arg0.m22() * float5 + float9 * float6;
        return arg1._m00(arg0.m00() * float2)
            ._m01(arg0.m10() * float2)
            ._m02(arg0.m20() * float2)
            ._m03(0.0F)
            ._m10(float10)
            ._m11(arg0.m11() * float3)
            ._m12(arg0.m21() * float3)
            ._m13(0.0F)
            ._m20(float7 * float4)
            ._m21(float8 * float4)
            ._m22(float9 * float4)
            ._m23(float4)
            ._m30(float11)
            ._m31(float12)
            ._m32(float13)
            ._m33(float6)
            ._properties(0);
    }

    @Override
    public Matrix4f invertPerspectiveView(Matrix4x3fc arg0, Matrix4f arg1) {
        float float0 = 1.0F / (this.m00 * this.m11);
        float float1 = -1.0F / (this.m23 * this.m32);
        float float2 = this.m11 * float0;
        float float3 = this.m00 * float0;
        float float4 = -this.m23 * float1;
        float float5 = -this.m32 * float1;
        float float6 = this.m22 * float1;
        float float7 = -arg0.m00() * arg0.m30() - arg0.m01() * arg0.m31() - arg0.m02() * arg0.m32();
        float float8 = -arg0.m10() * arg0.m30() - arg0.m11() * arg0.m31() - arg0.m12() * arg0.m32();
        float float9 = -arg0.m20() * arg0.m30() - arg0.m21() * arg0.m31() - arg0.m22() * arg0.m32();
        return arg1._m00(arg0.m00() * float2)
            ._m01(arg0.m10() * float2)
            ._m02(arg0.m20() * float2)
            ._m03(0.0F)
            ._m10(arg0.m01() * float3)
            ._m11(arg0.m11() * float3)
            ._m12(arg0.m21() * float3)
            ._m13(0.0F)
            ._m20(float7 * float4)
            ._m21(float8 * float4)
            ._m22(float9 * float4)
            ._m23(float4)
            ._m30(arg0.m02() * float5 + float7 * float6)
            ._m31(arg0.m12() * float5 + float8 * float6)
            ._m32(arg0.m22() * float5 + float9 * float6)
            ._m33(float6)
            ._properties(0);
    }

    @Override
    public Matrix4f invertAffine(Matrix4f arg0) {
        float float0 = this.m00 * this.m11;
        float float1 = this.m01 * this.m10;
        float float2 = this.m02 * this.m10;
        float float3 = this.m00 * this.m12;
        float float4 = this.m01 * this.m12;
        float float5 = this.m02 * this.m11;
        float float6 = (float0 - float1) * this.m22 + (float2 - float3) * this.m21 + (float4 - float5) * this.m20;
        float float7 = 1.0F / float6;
        float float8 = this.m10 * this.m22;
        float float9 = this.m10 * this.m21;
        float float10 = this.m11 * this.m22;
        float float11 = this.m11 * this.m20;
        float float12 = this.m12 * this.m21;
        float float13 = this.m12 * this.m20;
        float float14 = this.m20 * this.m02;
        float float15 = this.m20 * this.m01;
        float float16 = this.m21 * this.m02;
        float float17 = this.m21 * this.m00;
        float float18 = this.m22 * this.m01;
        float float19 = this.m22 * this.m00;
        float float20 = (float14 * this.m31 - float15 * this.m32 + float17 * this.m32 - float16 * this.m30 + float18 * this.m30 - float19 * this.m31) * float7;
        float float21 = (float5 * this.m30 - float4 * this.m30 + float3 * this.m31 - float2 * this.m31 + float1 * this.m32 - float0 * this.m32) * float7;
        return arg0._m00((float10 - float12) * float7)
            ._m01((float16 - float18) * float7)
            ._m02((float4 - float5) * float7)
            ._m03(0.0F)
            ._m10((float13 - float8) * float7)
            ._m11((float19 - float14) * float7)
            ._m12((float2 - float3) * float7)
            ._m13(0.0F)
            ._m20((float9 - float11) * float7)
            ._m21((float15 - float17) * float7)
            ._m22((float0 - float1) * float7)
            ._m23(0.0F)
            ._m30((float8 * this.m31 - float9 * this.m32 + float11 * this.m32 - float10 * this.m30 + float12 * this.m30 - float13 * this.m31) * float7)
            ._m31(float20)
            ._m32(float21)
            ._m33(1.0F)
            ._properties(2);
    }

    public Matrix4f invertAffine() {
        return this.invertAffine(this);
    }

    @Override
    public Matrix4f transpose(Matrix4f arg0) {
        if ((this.properties & 4) != 0) {
            return arg0.identity();
        } else {
            return this != arg0 ? this.transposeNonThisGeneric(arg0) : this.transposeThisGeneric(arg0);
        }
    }

    private Matrix4f transposeNonThisGeneric(Matrix4f matrix4f1) {
        return matrix4f1._m00(this.m00)
            ._m01(this.m10)
            ._m02(this.m20)
            ._m03(this.m30)
            ._m10(this.m01)
            ._m11(this.m11)
            ._m12(this.m21)
            ._m13(this.m31)
            ._m20(this.m02)
            ._m21(this.m12)
            ._m22(this.m22)
            ._m23(this.m32)
            ._m30(this.m03)
            ._m31(this.m13)
            ._m32(this.m23)
            ._m33(this.m33)
            ._properties(0);
    }

    private Matrix4f transposeThisGeneric(Matrix4f matrix4f1) {
        float float0 = this.m01;
        float float1 = this.m02;
        float float2 = this.m12;
        float float3 = this.m03;
        float float4 = this.m13;
        float float5 = this.m23;
        return matrix4f1._m01(this.m10)
            ._m02(this.m20)
            ._m03(this.m30)
            ._m10(float0)
            ._m12(this.m21)
            ._m13(this.m31)
            ._m20(float1)
            ._m21(float2)
            ._m23(this.m32)
            ._m30(float3)
            ._m31(float4)
            ._m32(float5)
            ._properties(0);
    }

    public Matrix4f transpose3x3() {
        return this.transpose3x3(this);
    }

    @Override
    public Matrix4f transpose3x3(Matrix4f arg0) {
        float float0 = this.m01;
        float float1 = this.m02;
        float float2 = this.m12;
        return arg0._m00(this.m00)
            ._m01(this.m10)
            ._m02(this.m20)
            ._m10(float0)
            ._m11(this.m11)
            ._m12(this.m21)
            ._m20(float1)
            ._m21(float2)
            ._m22(this.m22)
            ._properties(this.properties & 30);
    }

    @Override
    public Matrix3f transpose3x3(Matrix3f arg0) {
        return arg0._m00(this.m00)._m01(this.m10)._m02(this.m20)._m10(this.m01)._m11(this.m11)._m12(this.m21)._m20(this.m02)._m21(this.m12)._m22(this.m22);
    }

    public Matrix4f transpose() {
        return this.transpose(this);
    }

    public Matrix4f translation(float arg0, float arg1, float arg2) {
        if ((this.properties & 4) == 0) {
            MemUtil.INSTANCE.identity(this);
        }

        return this._m30(arg0)._m31(arg1)._m32(arg2)._properties(26);
    }

    public Matrix4f translation(Vector3fc arg0) {
        return this.translation(arg0.x(), arg0.y(), arg0.z());
    }

    public Matrix4f setTranslation(float arg0, float arg1, float arg2) {
        return this._m30(arg0)._m31(arg1)._m32(arg2)._properties(this.properties & -6);
    }

    public Matrix4f setTranslation(Vector3fc arg0) {
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
            + "\n"
            + Runtime.format(this.m03, numberFormat)
            + " "
            + Runtime.format(this.m13, numberFormat)
            + " "
            + Runtime.format(this.m23, numberFormat)
            + " "
            + Runtime.format(this.m33, numberFormat)
            + "\n";
    }

    @Override
    public Matrix4f get(Matrix4f arg0) {
        return arg0.set(this);
    }

    @Override
    public Matrix4x3f get4x3(Matrix4x3f arg0) {
        return arg0.set(this);
    }

    @Override
    public Matrix4d get(Matrix4d arg0) {
        return arg0.set(this);
    }

    @Override
    public Matrix3f get3x3(Matrix3f arg0) {
        return arg0.set(this);
    }

    @Override
    public Matrix3d get3x3(Matrix3d arg0) {
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
        MemUtil.INSTANCE.put(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public FloatBuffer get(int arg0, FloatBuffer arg1) {
        MemUtil.INSTANCE.put(this, arg0, arg1);
        return arg1;
    }

    @Override
    public ByteBuffer get(ByteBuffer arg0) {
        MemUtil.INSTANCE.put(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public ByteBuffer get(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.put(this, arg0, arg1);
        return arg1;
    }

    @Override
    public FloatBuffer get4x3(FloatBuffer arg0) {
        MemUtil.INSTANCE.put4x3(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public FloatBuffer get4x3(int arg0, FloatBuffer arg1) {
        MemUtil.INSTANCE.put4x3(this, arg0, arg1);
        return arg1;
    }

    @Override
    public ByteBuffer get4x3(ByteBuffer arg0) {
        MemUtil.INSTANCE.put4x3(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public ByteBuffer get4x3(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.put4x3(this, arg0, arg1);
        return arg1;
    }

    @Override
    public FloatBuffer get3x4(FloatBuffer arg0) {
        MemUtil.INSTANCE.put3x4(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public FloatBuffer get3x4(int arg0, FloatBuffer arg1) {
        MemUtil.INSTANCE.put3x4(this, arg0, arg1);
        return arg1;
    }

    @Override
    public ByteBuffer get3x4(ByteBuffer arg0) {
        MemUtil.INSTANCE.put3x4(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public ByteBuffer get3x4(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.put3x4(this, arg0, arg1);
        return arg1;
    }

    @Override
    public FloatBuffer getTransposed(FloatBuffer arg0) {
        MemUtil.INSTANCE.putTransposed(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public FloatBuffer getTransposed(int arg0, FloatBuffer arg1) {
        MemUtil.INSTANCE.putTransposed(this, arg0, arg1);
        return arg1;
    }

    @Override
    public ByteBuffer getTransposed(ByteBuffer arg0) {
        MemUtil.INSTANCE.putTransposed(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public ByteBuffer getTransposed(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.putTransposed(this, arg0, arg1);
        return arg1;
    }

    @Override
    public FloatBuffer get4x3Transposed(FloatBuffer arg0) {
        MemUtil.INSTANCE.put4x3Transposed(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public FloatBuffer get4x3Transposed(int arg0, FloatBuffer arg1) {
        MemUtil.INSTANCE.put4x3Transposed(this, arg0, arg1);
        return arg1;
    }

    @Override
    public ByteBuffer get4x3Transposed(ByteBuffer arg0) {
        MemUtil.INSTANCE.put4x3Transposed(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public ByteBuffer get4x3Transposed(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.put4x3Transposed(this, arg0, arg1);
        return arg1;
    }

    @Override
    public Matrix4fc getToAddress(long arg0) {
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
        MemUtil.INSTANCE.copy(this, floats, 0);
        return floats;
    }

    public Matrix4f zero() {
        MemUtil.INSTANCE.zero(this);
        return this._properties(0);
    }

    public Matrix4f scaling(float arg0) {
        return this.scaling(arg0, arg0, arg0);
    }

    public Matrix4f scaling(float arg0, float arg1, float arg2) {
        if ((this.properties & 4) == 0) {
            MemUtil.INSTANCE.identity(this);
        }

        boolean boolean0 = Math.absEqualsOne(arg0) && Math.absEqualsOne(arg1) && Math.absEqualsOne(arg2);
        return this._m00(arg0)._m11(arg1)._m22(arg2)._properties(2 | (boolean0 ? 16 : 0));
    }

    public Matrix4f scaling(Vector3fc arg0) {
        return this.scaling(arg0.x(), arg0.y(), arg0.z());
    }

    public Matrix4f rotation(float arg0, Vector3fc arg1) {
        return this.rotation(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix4f rotation(AxisAngle4f arg0) {
        return this.rotation(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    public Matrix4f rotation(float arg0, float arg1, float arg2, float arg3) {
        if (arg2 == 0.0F && arg3 == 0.0F && Math.absEqualsOne(arg1)) {
            return this.rotationX(arg1 * arg0);
        } else if (arg1 == 0.0F && arg3 == 0.0F && Math.absEqualsOne(arg2)) {
            return this.rotationY(arg2 * arg0);
        } else {
            return arg1 == 0.0F && arg2 == 0.0F && Math.absEqualsOne(arg3) ? this.rotationZ(arg3 * arg0) : this.rotationInternal(arg0, arg1, arg2, arg3);
        }
    }

    private Matrix4f rotationInternal(float float1, float float5, float float6, float float8) {
        float float0 = Math.sin(float1);
        float float2 = Math.cosFromSin(float0, float1);
        float float3 = 1.0F - float2;
        float float4 = float5 * float6;
        float float7 = float5 * float8;
        float float9 = float6 * float8;
        if ((this.properties & 4) == 0) {
            MemUtil.INSTANCE.identity(this);
        }

        return this._m00(float2 + float5 * float5 * float3)
            ._m10(float4 * float3 - float8 * float0)
            ._m20(float7 * float3 + float6 * float0)
            ._m01(float4 * float3 + float8 * float0)
            ._m11(float2 + float6 * float6 * float3)
            ._m21(float9 * float3 - float5 * float0)
            ._m02(float7 * float3 - float6 * float0)
            ._m12(float9 * float3 + float5 * float0)
            ._m22(float2 + float8 * float8 * float3)
            ._properties(18);
    }

    public Matrix4f rotationX(float arg0) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        if ((this.properties & 4) == 0) {
            MemUtil.INSTANCE.identity(this);
        }

        this._m11(float1)._m12(float0)._m21(-float0)._m22(float1)._properties(18);
        return this;
    }

    public Matrix4f rotationY(float arg0) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        if ((this.properties & 4) == 0) {
            MemUtil.INSTANCE.identity(this);
        }

        this._m00(float1)._m02(-float0)._m20(float0)._m22(float1)._properties(18);
        return this;
    }

    public Matrix4f rotationZ(float arg0) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        if ((this.properties & 4) == 0) {
            MemUtil.INSTANCE.identity(this);
        }

        return this._m00(float1)._m01(float0)._m10(-float0)._m11(float1)._properties(18);
    }

    public Matrix4f rotationTowardsXY(float arg0, float arg1) {
        if ((this.properties & 4) == 0) {
            MemUtil.INSTANCE.identity(this);
        }

        return this._m00(arg1)._m01(arg0)._m10(-arg0)._m11(arg1)._properties(18);
    }

    public Matrix4f rotationXYZ(float arg0, float arg1, float arg2) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = Math.sin(arg1);
        float float3 = Math.cosFromSin(float2, arg1);
        float float4 = Math.sin(arg2);
        float float5 = Math.cosFromSin(float4, arg2);
        if ((this.properties & 4) == 0) {
            MemUtil.INSTANCE.identity(this);
        }

        float float6 = -float0 * -float2;
        float float7 = float1 * -float2;
        return this._m20(float2)
            ._m21(-float0 * float3)
            ._m22(float1 * float3)
            ._m00(float3 * float5)
            ._m01(float6 * float5 + float1 * float4)
            ._m02(float7 * float5 + float0 * float4)
            ._m10(float3 * -float4)
            ._m11(float6 * -float4 + float1 * float5)
            ._m12(float7 * -float4 + float0 * float5)
            ._properties(18);
    }

    public Matrix4f rotationZYX(float arg0, float arg1, float arg2) {
        float float0 = Math.sin(arg2);
        float float1 = Math.cosFromSin(float0, arg2);
        float float2 = Math.sin(arg1);
        float float3 = Math.cosFromSin(float2, arg1);
        float float4 = Math.sin(arg0);
        float float5 = Math.cosFromSin(float4, arg0);
        float float6 = float5 * float2;
        float float7 = float4 * float2;
        return this._m00(float5 * float3)
            ._m01(float4 * float3)
            ._m02(-float2)
            ._m03(0.0F)
            ._m10(-float4 * float1 + float6 * float0)
            ._m11(float5 * float1 + float7 * float0)
            ._m12(float3 * float0)
            ._m13(0.0F)
            ._m20(-float4 * -float0 + float6 * float1)
            ._m21(float5 * -float0 + float7 * float1)
            ._m22(float3 * float1)
            ._m23(0.0F)
            ._m30(0.0F)
            ._m31(0.0F)
            ._m32(0.0F)
            ._m33(1.0F)
            ._properties(18);
    }

    public Matrix4f rotationYXZ(float arg0, float arg1, float arg2) {
        float float0 = Math.sin(arg1);
        float float1 = Math.cosFromSin(float0, arg1);
        float float2 = Math.sin(arg0);
        float float3 = Math.cosFromSin(float2, arg0);
        float float4 = Math.sin(arg2);
        float float5 = Math.cosFromSin(float4, arg2);
        float float6 = float2 * float0;
        float float7 = float3 * float0;
        return this._m20(float2 * float1)
            ._m21(-float0)
            ._m22(float3 * float1)
            ._m23(0.0F)
            ._m00(float3 * float5 + float6 * float4)
            ._m01(float1 * float4)
            ._m02(-float2 * float5 + float7 * float4)
            ._m03(0.0F)
            ._m10(float3 * -float4 + float6 * float5)
            ._m11(float1 * float5)
            ._m12(-float2 * -float4 + float7 * float5)
            ._m13(0.0F)
            ._m30(0.0F)
            ._m31(0.0F)
            ._m32(0.0F)
            ._m33(1.0F)
            ._properties(18);
    }

    public Matrix4f setRotationXYZ(float arg0, float arg1, float arg2) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = Math.sin(arg1);
        float float3 = Math.cosFromSin(float2, arg1);
        float float4 = Math.sin(arg2);
        float float5 = Math.cosFromSin(float4, arg2);
        float float6 = -float0 * -float2;
        float float7 = float1 * -float2;
        return this._m20(float2)
            ._m21(-float0 * float3)
            ._m22(float1 * float3)
            ._m00(float3 * float5)
            ._m01(float6 * float5 + float1 * float4)
            ._m02(float7 * float5 + float0 * float4)
            ._m10(float3 * -float4)
            ._m11(float6 * -float4 + float1 * float5)
            ._m12(float7 * -float4 + float0 * float5)
            ._properties(this.properties & -14);
    }

    public Matrix4f setRotationZYX(float arg0, float arg1, float arg2) {
        float float0 = Math.sin(arg2);
        float float1 = Math.cosFromSin(float0, arg2);
        float float2 = Math.sin(arg1);
        float float3 = Math.cosFromSin(float2, arg1);
        float float4 = Math.sin(arg0);
        float float5 = Math.cosFromSin(float4, arg0);
        float float6 = float5 * float2;
        float float7 = float4 * float2;
        return this._m00(float5 * float3)
            ._m01(float4 * float3)
            ._m02(-float2)
            ._m10(-float4 * float1 + float6 * float0)
            ._m11(float5 * float1 + float7 * float0)
            ._m12(float3 * float0)
            ._m20(-float4 * -float0 + float6 * float1)
            ._m21(float5 * -float0 + float7 * float1)
            ._m22(float3 * float1)
            ._properties(this.properties & -14);
    }

    public Matrix4f setRotationYXZ(float arg0, float arg1, float arg2) {
        float float0 = Math.sin(arg1);
        float float1 = Math.cosFromSin(float0, arg1);
        float float2 = Math.sin(arg0);
        float float3 = Math.cosFromSin(float2, arg0);
        float float4 = Math.sin(arg2);
        float float5 = Math.cosFromSin(float4, arg2);
        float float6 = float2 * float0;
        float float7 = float3 * float0;
        return this._m20(float2 * float1)
            ._m21(-float0)
            ._m22(float3 * float1)
            ._m00(float3 * float5 + float6 * float4)
            ._m01(float1 * float4)
            ._m02(-float2 * float5 + float7 * float4)
            ._m10(float3 * -float4 + float6 * float5)
            ._m11(float1 * float5)
            ._m12(-float2 * -float4 + float7 * float5)
            ._properties(this.properties & -14);
    }

    public Matrix4f rotation(Quaternionfc arg0) {
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
        if ((this.properties & 4) == 0) {
            MemUtil.INSTANCE.identity(this);
        }

        return this._m00(float0 + float1 - float3 - float2)
            ._m01(float7 + float5)
            ._m02(float9 - float11)
            ._m10(-float5 + float7)
            ._m11(float2 - float3 + float0 - float1)
            ._m12(float13 + float15)
            ._m20(float11 + float9)
            ._m21(float13 - float15)
            ._m22(float3 - float2 - float1 + float0)
            ._properties(18);
    }

    public Matrix4f translationRotateScale(
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
        boolean boolean0 = Math.absEqualsOne(arg7) && Math.absEqualsOne(arg8) && Math.absEqualsOne(arg9);
        return this._m00(arg7 - (float4 + float5) * arg7)
            ._m01((float6 + float11) * arg7)
            ._m02((float7 - float10) * arg7)
            ._m03(0.0F)
            ._m10((float6 - float11) * arg8)
            ._m11(arg8 - (float5 + float3) * arg8)
            ._m12((float9 + float8) * arg8)
            ._m13(0.0F)
            ._m20((float7 + float10) * arg9)
            ._m21((float9 - float8) * arg9)
            ._m22(arg9 - (float4 + float3) * arg9)
            ._m23(0.0F)
            ._m30(arg0)
            ._m31(arg1)
            ._m32(arg2)
            ._m33(1.0F)
            ._properties(2 | (boolean0 ? 16 : 0));
    }

    public Matrix4f translationRotateScale(Vector3fc arg0, Quaternionfc arg1, Vector3fc arg2) {
        return this.translationRotateScale(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg1.w(), arg2.x(), arg2.y(), arg2.z());
    }

    public Matrix4f translationRotateScale(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7) {
        return this.translationRotateScale(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg7, arg7);
    }

    public Matrix4f translationRotateScale(Vector3fc arg0, Quaternionfc arg1, float arg2) {
        return this.translationRotateScale(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg1.w(), arg2, arg2, arg2);
    }

    public Matrix4f translationRotateScaleInvert(
        float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8, float arg9
    ) {
        boolean boolean0 = Math.absEqualsOne(arg7) && Math.absEqualsOne(arg8) && Math.absEqualsOne(arg9);
        if (boolean0) {
            return this.translationRotateScale(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9).invertOrthonormal(this);
        } else {
            float float0 = -arg3;
            float float1 = -arg4;
            float float2 = -arg5;
            float float3 = float0 + float0;
            float float4 = float1 + float1;
            float float5 = float2 + float2;
            float float6 = float3 * float0;
            float float7 = float4 * float1;
            float float8 = float5 * float2;
            float float9 = float3 * float1;
            float float10 = float3 * float2;
            float float11 = float3 * arg6;
            float float12 = float4 * float2;
            float float13 = float4 * arg6;
            float float14 = float5 * arg6;
            float float15 = 1.0F / arg7;
            float float16 = 1.0F / arg8;
            float float17 = 1.0F / arg9;
            return this._m00(float15 * (1.0F - float7 - float8))
                ._m01(float16 * (float9 + float14))
                ._m02(float17 * (float10 - float13))
                ._m03(0.0F)
                ._m10(float15 * (float9 - float14))
                ._m11(float16 * (1.0F - float8 - float6))
                ._m12(float17 * (float12 + float11))
                ._m13(0.0F)
                ._m20(float15 * (float10 + float13))
                ._m21(float16 * (float12 - float11))
                ._m22(float17 * (1.0F - float7 - float6))
                ._m23(0.0F)
                ._m30(-this.m00 * arg0 - this.m10 * arg1 - this.m20 * arg2)
                ._m31(-this.m01 * arg0 - this.m11 * arg1 - this.m21 * arg2)
                ._m32(-this.m02 * arg0 - this.m12 * arg1 - this.m22 * arg2)
                ._m33(1.0F)
                ._properties(2);
        }
    }

    public Matrix4f translationRotateScaleInvert(Vector3fc arg0, Quaternionfc arg1, Vector3fc arg2) {
        return this.translationRotateScaleInvert(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg1.w(), arg2.x(), arg2.y(), arg2.z());
    }

    public Matrix4f translationRotateScaleInvert(Vector3fc arg0, Quaternionfc arg1, float arg2) {
        return this.translationRotateScaleInvert(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg1.w(), arg2, arg2, arg2);
    }

    public Matrix4f translationRotateScaleMulAffine(
        float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8, float arg9, Matrix4f arg10
    ) {
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
        float float19 = float10 * arg10.m00 + float13 * arg10.m01 + float16 * arg10.m02;
        float float20 = float11 * arg10.m00 + float14 * arg10.m01 + float17 * arg10.m02;
        this._m02(float12 * arg10.m00 + float15 * arg10.m01 + float18 * arg10.m02)._m00(float19)._m01(float20)._m03(0.0F);
        float float21 = float10 * arg10.m10 + float13 * arg10.m11 + float16 * arg10.m12;
        float float22 = float11 * arg10.m10 + float14 * arg10.m11 + float17 * arg10.m12;
        this._m12(float12 * arg10.m10 + float15 * arg10.m11 + float18 * arg10.m12)._m10(float21)._m11(float22)._m13(0.0F);
        float float23 = float10 * arg10.m20 + float13 * arg10.m21 + float16 * arg10.m22;
        float float24 = float11 * arg10.m20 + float14 * arg10.m21 + float17 * arg10.m22;
        this._m22(float12 * arg10.m20 + float15 * arg10.m21 + float18 * arg10.m22)._m20(float23)._m21(float24)._m23(0.0F);
        float float25 = float10 * arg10.m30 + float13 * arg10.m31 + float16 * arg10.m32 + arg0;
        float float26 = float11 * arg10.m30 + float14 * arg10.m31 + float17 * arg10.m32 + arg1;
        this._m32(float12 * arg10.m30 + float15 * arg10.m31 + float18 * arg10.m32 + arg2)._m30(float25)._m31(float26)._m33(1.0F);
        boolean boolean0 = Math.absEqualsOne(arg7) && Math.absEqualsOne(arg8) && Math.absEqualsOne(arg9);
        return this._properties(2 | (boolean0 && (arg10.properties & 16) != 0 ? 16 : 0));
    }

    public Matrix4f translationRotateScaleMulAffine(Vector3fc arg0, Quaternionfc arg1, Vector3fc arg2, Matrix4f arg3) {
        return this.translationRotateScaleMulAffine(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg1.w(), arg2.x(), arg2.y(), arg2.z(), arg3);
    }

    public Matrix4f translationRotate(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6) {
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
        return this._m00(float0 + float1 - float3 - float2)
            ._m01(float5 + float4 + float4 + float5)
            ._m02(float6 - float7 + float6 - float7)
            ._m10(-float4 + float5 - float4 + float5)
            ._m11(float2 - float3 + float0 - float1)
            ._m12(float8 + float8 + float9 + float9)
            ._m20(float7 + float6 + float6 + float7)
            ._m21(float8 + float8 - float9 - float9)
            ._m22(float3 - float2 - float1 + float0)
            ._m30(arg0)
            ._m31(arg1)
            ._m32(arg2)
            ._m33(1.0F)
            ._properties(18);
    }

    public Matrix4f translationRotate(float arg0, float arg1, float arg2, Quaternionfc arg3) {
        return this.translationRotate(arg0, arg1, arg2, arg3.x(), arg3.y(), arg3.z(), arg3.w());
    }

    public Matrix4f set3x3(Matrix3fc arg0) {
        return this.set3x3Matrix3fc(arg0)._properties(this.properties & -30);
    }

    private Matrix4f set3x3Matrix3fc(Matrix3fc matrix3fc) {
        return this._m00(matrix3fc.m00())
            ._m01(matrix3fc.m01())
            ._m02(matrix3fc.m02())
            ._m10(matrix3fc.m10())
            ._m11(matrix3fc.m11())
            ._m12(matrix3fc.m12())
            ._m20(matrix3fc.m20())
            ._m21(matrix3fc.m21())
            ._m22(matrix3fc.m22());
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
    public Vector4f transform(float arg0, float arg1, float arg2, float arg3, Vector4f arg4) {
        return arg4.set(arg0, arg1, arg2, arg3).mul(this);
    }

    @Override
    public Vector4f transformTranspose(Vector4f arg0) {
        return arg0.mulTranspose(this);
    }

    @Override
    public Vector4f transformTranspose(Vector4fc arg0, Vector4f arg1) {
        return arg0.mulTranspose(this, arg1);
    }

    @Override
    public Vector4f transformTranspose(float arg0, float arg1, float arg2, float arg3, Vector4f arg4) {
        return arg4.set(arg0, arg1, arg2, arg3).mulTranspose(this);
    }

    @Override
    public Vector4f transformProject(Vector4f arg0) {
        return arg0.mulProject(this);
    }

    @Override
    public Vector4f transformProject(Vector4fc arg0, Vector4f arg1) {
        return arg0.mulProject(this, arg1);
    }

    @Override
    public Vector4f transformProject(float arg0, float arg1, float arg2, float arg3, Vector4f arg4) {
        return arg4.set(arg0, arg1, arg2, arg3).mulProject(this);
    }

    @Override
    public Vector3f transformProject(Vector4fc arg0, Vector3f arg1) {
        return arg0.mulProject(this, arg1);
    }

    @Override
    public Vector3f transformProject(float arg0, float arg1, float arg2, float arg3, Vector3f arg4) {
        return arg4.set(arg0, arg1, arg2).mulProject(this, arg3, arg4);
    }

    @Override
    public Vector3f transformProject(Vector3f arg0) {
        return arg0.mulProject(this);
    }

    @Override
    public Vector3f transformProject(Vector3fc arg0, Vector3f arg1) {
        return arg0.mulProject(this, arg1);
    }

    @Override
    public Vector3f transformProject(float arg0, float arg1, float arg2, Vector3f arg3) {
        return arg3.set(arg0, arg1, arg2).mulProject(this);
    }

    @Override
    public Vector3f transformPosition(Vector3f arg0) {
        return arg0.mulPosition(this);
    }

    @Override
    public Vector3f transformPosition(Vector3fc arg0, Vector3f arg1) {
        return this.transformPosition(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Vector3f transformPosition(float arg0, float arg1, float arg2, Vector3f arg3) {
        return arg3.set(arg0, arg1, arg2).mulPosition(this);
    }

    @Override
    public Vector3f transformDirection(Vector3f arg0) {
        return this.transformDirection(arg0.x, arg0.y, arg0.z, arg0);
    }

    @Override
    public Vector3f transformDirection(Vector3fc arg0, Vector3f arg1) {
        return this.transformDirection(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Vector3f transformDirection(float arg0, float arg1, float arg2, Vector3f arg3) {
        return arg3.set(arg0, arg1, arg2).mulDirection(this);
    }

    @Override
    public Vector4f transformAffine(Vector4f arg0) {
        return arg0.mulAffine(this, arg0);
    }

    @Override
    public Vector4f transformAffine(Vector4fc arg0, Vector4f arg1) {
        return this.transformAffine(arg0.x(), arg0.y(), arg0.z(), arg0.w(), arg1);
    }

    @Override
    public Vector4f transformAffine(float arg0, float arg1, float arg2, float arg3, Vector4f arg4) {
        return arg4.set(arg0, arg1, arg2, arg3).mulAffine(this, arg4);
    }

    @Override
    public Matrix4f scale(Vector3fc arg0, Matrix4f arg1) {
        return this.scale(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    public Matrix4f scale(Vector3fc arg0) {
        return this.scale(arg0.x(), arg0.y(), arg0.z(), this);
    }

    @Override
    public Matrix4f scale(float arg0, Matrix4f arg1) {
        return this.scale(arg0, arg0, arg0, arg1);
    }

    public Matrix4f scale(float arg0) {
        return this.scale(arg0, arg0, arg0);
    }

    @Override
    public Matrix4f scaleXY(float arg0, float arg1, Matrix4f arg2) {
        return this.scale(arg0, arg1, 1.0F, arg2);
    }

    public Matrix4f scaleXY(float arg0, float arg1) {
        return this.scale(arg0, arg1, 1.0F);
    }

    @Override
    public Matrix4f scale(float arg0, float arg1, float arg2, Matrix4f arg3) {
        return (this.properties & 4) != 0 ? arg3.scaling(arg0, arg1, arg2) : this.scaleGeneric(arg0, arg1, arg2, arg3);
    }

    private Matrix4f scaleGeneric(float float2, float float1, float float0, Matrix4f matrix4f1) {
        boolean boolean0 = Math.absEqualsOne(float2) && Math.absEqualsOne(float1) && Math.absEqualsOne(float0);
        return matrix4f1._m00(this.m00 * float2)
            ._m01(this.m01 * float2)
            ._m02(this.m02 * float2)
            ._m03(this.m03 * float2)
            ._m10(this.m10 * float1)
            ._m11(this.m11 * float1)
            ._m12(this.m12 * float1)
            ._m13(this.m13 * float1)
            ._m20(this.m20 * float0)
            ._m21(this.m21 * float0)
            ._m22(this.m22 * float0)
            ._m23(this.m23 * float0)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & ~(13 | (boolean0 ? 0 : 16)));
    }

    public Matrix4f scale(float arg0, float arg1, float arg2) {
        return this.scale(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4f scaleAround(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6) {
        float float0 = this.m00 * arg3 + this.m10 * arg4 + this.m20 * arg5 + this.m30;
        float float1 = this.m01 * arg3 + this.m11 * arg4 + this.m21 * arg5 + this.m31;
        float float2 = this.m02 * arg3 + this.m12 * arg4 + this.m22 * arg5 + this.m32;
        float float3 = this.m03 * arg3 + this.m13 * arg4 + this.m23 * arg5 + this.m33;
        boolean boolean0 = Math.absEqualsOne(arg0) && Math.absEqualsOne(arg1) && Math.absEqualsOne(arg2);
        return arg6._m00(this.m00 * arg0)
            ._m01(this.m01 * arg0)
            ._m02(this.m02 * arg0)
            ._m03(this.m03 * arg0)
            ._m10(this.m10 * arg1)
            ._m11(this.m11 * arg1)
            ._m12(this.m12 * arg1)
            ._m13(this.m13 * arg1)
            ._m20(this.m20 * arg2)
            ._m21(this.m21 * arg2)
            ._m22(this.m22 * arg2)
            ._m23(this.m23 * arg2)
            ._m30(-this.m00 * arg3 - this.m10 * arg4 - this.m20 * arg5 + float0)
            ._m31(-this.m01 * arg3 - this.m11 * arg4 - this.m21 * arg5 + float1)
            ._m32(-this.m02 * arg3 - this.m12 * arg4 - this.m22 * arg5 + float2)
            ._m33(-this.m03 * arg3 - this.m13 * arg4 - this.m23 * arg5 + float3)
            ._properties(this.properties & ~(13 | (boolean0 ? 0 : 16)));
    }

    public Matrix4f scaleAround(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.scaleAround(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    public Matrix4f scaleAround(float arg0, float arg1, float arg2, float arg3) {
        return this.scaleAround(arg0, arg0, arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix4f scaleAround(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4) {
        return this.scaleAround(arg0, arg0, arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public Matrix4f scaleLocal(float arg0, float arg1, float arg2, Matrix4f arg3) {
        return (this.properties & 4) != 0 ? arg3.scaling(arg0, arg1, arg2) : this.scaleLocalGeneric(arg0, arg1, arg2, arg3);
    }

    private Matrix4f scaleLocalGeneric(float float1, float float3, float float5, Matrix4f matrix4f1) {
        float float0 = float1 * this.m00;
        float float2 = float3 * this.m01;
        float float4 = float5 * this.m02;
        float float6 = float1 * this.m10;
        float float7 = float3 * this.m11;
        float float8 = float5 * this.m12;
        float float9 = float1 * this.m20;
        float float10 = float3 * this.m21;
        float float11 = float5 * this.m22;
        float float12 = float1 * this.m30;
        float float13 = float3 * this.m31;
        float float14 = float5 * this.m32;
        boolean boolean0 = Math.absEqualsOne(float1) && Math.absEqualsOne(float3) && Math.absEqualsOne(float5);
        return matrix4f1._m00(float0)
            ._m01(float2)
            ._m02(float4)
            ._m03(this.m03)
            ._m10(float6)
            ._m11(float7)
            ._m12(float8)
            ._m13(this.m13)
            ._m20(float9)
            ._m21(float10)
            ._m22(float11)
            ._m23(this.m23)
            ._m30(float12)
            ._m31(float13)
            ._m32(float14)
            ._m33(this.m33)
            ._properties(this.properties & ~(13 | (boolean0 ? 0 : 16)));
    }

    @Override
    public Matrix4f scaleLocal(float arg0, Matrix4f arg1) {
        return this.scaleLocal(arg0, arg0, arg0, arg1);
    }

    public Matrix4f scaleLocal(float arg0) {
        return this.scaleLocal(arg0, this);
    }

    public Matrix4f scaleLocal(float arg0, float arg1, float arg2) {
        return this.scaleLocal(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4f scaleAroundLocal(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6) {
        boolean boolean0 = Math.absEqualsOne(arg0) && Math.absEqualsOne(arg1) && Math.absEqualsOne(arg2);
        return arg6._m00(arg0 * (this.m00 - arg3 * this.m03) + arg3 * this.m03)
            ._m01(arg1 * (this.m01 - arg4 * this.m03) + arg4 * this.m03)
            ._m02(arg2 * (this.m02 - arg5 * this.m03) + arg5 * this.m03)
            ._m03(this.m03)
            ._m10(arg0 * (this.m10 - arg3 * this.m13) + arg3 * this.m13)
            ._m11(arg1 * (this.m11 - arg4 * this.m13) + arg4 * this.m13)
            ._m12(arg2 * (this.m12 - arg5 * this.m13) + arg5 * this.m13)
            ._m13(this.m13)
            ._m20(arg0 * (this.m20 - arg3 * this.m23) + arg3 * this.m23)
            ._m21(arg1 * (this.m21 - arg4 * this.m23) + arg4 * this.m23)
            ._m22(arg2 * (this.m22 - arg5 * this.m23) + arg5 * this.m23)
            ._m23(this.m23)
            ._m30(arg0 * (this.m30 - arg3 * this.m33) + arg3 * this.m33)
            ._m31(arg1 * (this.m31 - arg4 * this.m33) + arg4 * this.m33)
            ._m32(arg2 * (this.m32 - arg5 * this.m33) + arg5 * this.m33)
            ._m33(this.m33)
            ._properties(this.properties & ~(13 | (boolean0 ? 0 : 16)));
    }

    public Matrix4f scaleAroundLocal(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.scaleAroundLocal(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    public Matrix4f scaleAroundLocal(float arg0, float arg1, float arg2, float arg3) {
        return this.scaleAroundLocal(arg0, arg0, arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix4f scaleAroundLocal(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4) {
        return this.scaleAroundLocal(arg0, arg0, arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public Matrix4f rotateX(float arg0, Matrix4f arg1) {
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

    private Matrix4f rotateXInternal(float float1, Matrix4f matrix4f1) {
        float float0 = Math.sin(float1);
        float float2 = Math.cosFromSin(float0, float1);
        float float3 = this.m10;
        float float4 = this.m11;
        float float5 = this.m12;
        float float6 = this.m13;
        float float7 = this.m20;
        float float8 = this.m21;
        float float9 = this.m22;
        float float10 = this.m23;
        return matrix4f1._m20(float3 * -float0 + float7 * float2)
            ._m21(float4 * -float0 + float8 * float2)
            ._m22(float5 * -float0 + float9 * float2)
            ._m23(float6 * -float0 + float10 * float2)
            ._m10(float3 * float2 + float7 * float0)
            ._m11(float4 * float2 + float8 * float0)
            ._m12(float5 * float2 + float9 * float0)
            ._m13(float6 * float2 + float10 * float0)
            ._m00(this.m00)
            ._m01(this.m01)
            ._m02(this.m02)
            ._m03(this.m03)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
    }

    public Matrix4f rotateX(float arg0) {
        return this.rotateX(arg0, this);
    }

    @Override
    public Matrix4f rotateY(float arg0, Matrix4f arg1) {
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

    private Matrix4f rotateYInternal(float float1, Matrix4f matrix4f1) {
        float float0 = Math.sin(float1);
        float float2 = Math.cosFromSin(float0, float1);
        float float3 = this.m00 * float2 + this.m20 * -float0;
        float float4 = this.m01 * float2 + this.m21 * -float0;
        float float5 = this.m02 * float2 + this.m22 * -float0;
        float float6 = this.m03 * float2 + this.m23 * -float0;
        return matrix4f1._m20(this.m00 * float0 + this.m20 * float2)
            ._m21(this.m01 * float0 + this.m21 * float2)
            ._m22(this.m02 * float0 + this.m22 * float2)
            ._m23(this.m03 * float0 + this.m23 * float2)
            ._m00(float3)
            ._m01(float4)
            ._m02(float5)
            ._m03(float6)
            ._m10(this.m10)
            ._m11(this.m11)
            ._m12(this.m12)
            ._m13(this.m13)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
    }

    public Matrix4f rotateY(float arg0) {
        return this.rotateY(arg0, this);
    }

    @Override
    public Matrix4f rotateZ(float arg0, Matrix4f arg1) {
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

    private Matrix4f rotateZInternal(float float1, Matrix4f matrix4f1) {
        float float0 = Math.sin(float1);
        float float2 = Math.cosFromSin(float0, float1);
        return this.rotateTowardsXY(float0, float2, matrix4f1);
    }

    public Matrix4f rotateZ(float arg0) {
        return this.rotateZ(arg0, this);
    }

    public Matrix4f rotateTowardsXY(float arg0, float arg1) {
        return this.rotateTowardsXY(arg0, arg1, this);
    }

    @Override
    public Matrix4f rotateTowardsXY(float arg0, float arg1, Matrix4f arg2) {
        if ((this.properties & 4) != 0) {
            return arg2.rotationTowardsXY(arg0, arg1);
        } else {
            float float0 = this.m00 * arg1 + this.m10 * arg0;
            float float1 = this.m01 * arg1 + this.m11 * arg0;
            float float2 = this.m02 * arg1 + this.m12 * arg0;
            float float3 = this.m03 * arg1 + this.m13 * arg0;
            return arg2._m10(this.m00 * -arg0 + this.m10 * arg1)
                ._m11(this.m01 * -arg0 + this.m11 * arg1)
                ._m12(this.m02 * -arg0 + this.m12 * arg1)
                ._m13(this.m03 * -arg0 + this.m13 * arg1)
                ._m00(float0)
                ._m01(float1)
                ._m02(float2)
                ._m03(float3)
                ._m20(this.m20)
                ._m21(this.m21)
                ._m22(this.m22)
                ._m23(this.m23)
                ._m30(this.m30)
                ._m31(this.m31)
                ._m32(this.m32)
                ._m33(this.m33)
                ._properties(this.properties & -14);
        }
    }

    public Matrix4f rotateXYZ(Vector3fc arg0) {
        return this.rotateXYZ(arg0.x(), arg0.y(), arg0.z());
    }

    public Matrix4f rotateXYZ(float arg0, float arg1, float arg2) {
        return this.rotateXYZ(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4f rotateXYZ(float arg0, float arg1, float arg2, Matrix4f arg3) {
        if ((this.properties & 4) != 0) {
            return arg3.rotationXYZ(arg0, arg1, arg2);
        } else if ((this.properties & 8) != 0) {
            float float0 = this.m30;
            float float1 = this.m31;
            float float2 = this.m32;
            return arg3.rotationXYZ(arg0, arg1, arg2).setTranslation(float0, float1, float2);
        } else {
            return (this.properties & 2) != 0 ? arg3.rotateAffineXYZ(arg0, arg1, arg2) : this.rotateXYZInternal(arg0, arg1, arg2, arg3);
        }
    }

    private Matrix4f rotateXYZInternal(float float1, float float4, float float7, Matrix4f matrix4f1) {
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
        float float15 = this.m13 * float2 + this.m23 * float0;
        float float16 = this.m10 * float9 + this.m20 * float2;
        float float17 = this.m11 * float9 + this.m21 * float2;
        float float18 = this.m12 * float9 + this.m22 * float2;
        float float19 = this.m13 * float9 + this.m23 * float2;
        float float20 = this.m00 * float5 + float16 * float10;
        float float21 = this.m01 * float5 + float17 * float10;
        float float22 = this.m02 * float5 + float18 * float10;
        float float23 = this.m03 * float5 + float19 * float10;
        return matrix4f1._m20(this.m00 * float3 + float16 * float5)
            ._m21(this.m01 * float3 + float17 * float5)
            ._m22(this.m02 * float3 + float18 * float5)
            ._m23(this.m03 * float3 + float19 * float5)
            ._m00(float20 * float8 + float12 * float6)
            ._m01(float21 * float8 + float13 * float6)
            ._m02(float22 * float8 + float14 * float6)
            ._m03(float23 * float8 + float15 * float6)
            ._m10(float20 * float11 + float12 * float8)
            ._m11(float21 * float11 + float13 * float8)
            ._m12(float22 * float11 + float14 * float8)
            ._m13(float23 * float11 + float15 * float8)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
    }

    public Matrix4f rotateAffineXYZ(float arg0, float arg1, float arg2) {
        return this.rotateAffineXYZ(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4f rotateAffineXYZ(float arg0, float arg1, float arg2, Matrix4f arg3) {
        if ((this.properties & 4) != 0) {
            return arg3.rotationXYZ(arg0, arg1, arg2);
        } else if ((this.properties & 8) != 0) {
            float float0 = this.m30;
            float float1 = this.m31;
            float float2 = this.m32;
            return arg3.rotationXYZ(arg0, arg1, arg2).setTranslation(float0, float1, float2);
        } else {
            return this.rotateAffineXYZInternal(arg0, arg1, arg2, arg3);
        }
    }

    private Matrix4f rotateAffineXYZInternal(float float1, float float4, float float7, Matrix4f matrix4f1) {
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
        return matrix4f1._m20(this.m00 * float3 + float15 * float5)
            ._m21(this.m01 * float3 + float16 * float5)
            ._m22(this.m02 * float3 + float17 * float5)
            ._m23(0.0F)
            ._m00(float18 * float8 + float12 * float6)
            ._m01(float19 * float8 + float13 * float6)
            ._m02(float20 * float8 + float14 * float6)
            ._m03(0.0F)
            ._m10(float18 * float11 + float12 * float8)
            ._m11(float19 * float11 + float13 * float8)
            ._m12(float20 * float11 + float14 * float8)
            ._m13(0.0F)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
    }

    public Matrix4f rotateZYX(Vector3f arg0) {
        return this.rotateZYX(arg0.z, arg0.y, arg0.x);
    }

    public Matrix4f rotateZYX(float arg0, float arg1, float arg2) {
        return this.rotateZYX(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4f rotateZYX(float arg0, float arg1, float arg2, Matrix4f arg3) {
        if ((this.properties & 4) != 0) {
            return arg3.rotationZYX(arg0, arg1, arg2);
        } else if ((this.properties & 8) != 0) {
            float float0 = this.m30;
            float float1 = this.m31;
            float float2 = this.m32;
            return arg3.rotationZYX(arg0, arg1, arg2).setTranslation(float0, float1, float2);
        } else {
            return (this.properties & 2) != 0 ? arg3.rotateAffineZYX(arg0, arg1, arg2) : this.rotateZYXInternal(arg0, arg1, arg2, arg3);
        }
    }

    private Matrix4f rotateZYXInternal(float float7, float float4, float float1, Matrix4f matrix4f1) {
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
        float float15 = this.m03 * float8 + this.m13 * float6;
        float float16 = this.m00 * float9 + this.m10 * float8;
        float float17 = this.m01 * float9 + this.m11 * float8;
        float float18 = this.m02 * float9 + this.m12 * float8;
        float float19 = this.m03 * float9 + this.m13 * float8;
        float float20 = float12 * float3 + this.m20 * float5;
        float float21 = float13 * float3 + this.m21 * float5;
        float float22 = float14 * float3 + this.m22 * float5;
        float float23 = float15 * float3 + this.m23 * float5;
        return matrix4f1._m00(float12 * float5 + this.m20 * float10)
            ._m01(float13 * float5 + this.m21 * float10)
            ._m02(float14 * float5 + this.m22 * float10)
            ._m03(float15 * float5 + this.m23 * float10)
            ._m10(float16 * float2 + float20 * float0)
            ._m11(float17 * float2 + float21 * float0)
            ._m12(float18 * float2 + float22 * float0)
            ._m13(float19 * float2 + float23 * float0)
            ._m20(float16 * float11 + float20 * float2)
            ._m21(float17 * float11 + float21 * float2)
            ._m22(float18 * float11 + float22 * float2)
            ._m23(float19 * float11 + float23 * float2)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
    }

    public Matrix4f rotateAffineZYX(float arg0, float arg1, float arg2) {
        return this.rotateAffineZYX(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4f rotateAffineZYX(float arg0, float arg1, float arg2, Matrix4f arg3) {
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
        return arg3._m00(float9 * float3 + this.m20 * float7)
            ._m01(float10 * float3 + this.m21 * float7)
            ._m02(float11 * float3 + this.m22 * float7)
            ._m03(0.0F)
            ._m10(float12 * float1 + float15 * float0)
            ._m11(float13 * float1 + float16 * float0)
            ._m12(float14 * float1 + float17 * float0)
            ._m13(0.0F)
            ._m20(float12 * float8 + float15 * float1)
            ._m21(float13 * float8 + float16 * float1)
            ._m22(float14 * float8 + float17 * float1)
            ._m23(0.0F)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
    }

    public Matrix4f rotateYXZ(Vector3f arg0) {
        return this.rotateYXZ(arg0.y, arg0.x, arg0.z);
    }

    public Matrix4f rotateYXZ(float arg0, float arg1, float arg2) {
        return this.rotateYXZ(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4f rotateYXZ(float arg0, float arg1, float arg2, Matrix4f arg3) {
        if ((this.properties & 4) != 0) {
            return arg3.rotationYXZ(arg0, arg1, arg2);
        } else if ((this.properties & 8) != 0) {
            float float0 = this.m30;
            float float1 = this.m31;
            float float2 = this.m32;
            return arg3.rotationYXZ(arg0, arg1, arg2).setTranslation(float0, float1, float2);
        } else {
            return (this.properties & 2) != 0 ? arg3.rotateAffineYXZ(arg0, arg1, arg2) : this.rotateYXZInternal(arg0, arg1, arg2, arg3);
        }
    }

    private Matrix4f rotateYXZInternal(float float4, float float1, float float7, Matrix4f matrix4f1) {
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
        float float15 = this.m03 * float3 + this.m23 * float5;
        float float16 = this.m00 * float5 + this.m20 * float9;
        float float17 = this.m01 * float5 + this.m21 * float9;
        float float18 = this.m02 * float5 + this.m22 * float9;
        float float19 = this.m03 * float5 + this.m23 * float9;
        float float20 = this.m10 * float2 + float12 * float0;
        float float21 = this.m11 * float2 + float13 * float0;
        float float22 = this.m12 * float2 + float14 * float0;
        float float23 = this.m13 * float2 + float15 * float0;
        return matrix4f1._m20(this.m10 * float10 + float12 * float2)
            ._m21(this.m11 * float10 + float13 * float2)
            ._m22(this.m12 * float10 + float14 * float2)
            ._m23(this.m13 * float10 + float15 * float2)
            ._m00(float16 * float8 + float20 * float6)
            ._m01(float17 * float8 + float21 * float6)
            ._m02(float18 * float8 + float22 * float6)
            ._m03(float19 * float8 + float23 * float6)
            ._m10(float16 * float11 + float20 * float8)
            ._m11(float17 * float11 + float21 * float8)
            ._m12(float18 * float11 + float22 * float8)
            ._m13(float19 * float11 + float23 * float8)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
    }

    public Matrix4f rotateAffineYXZ(float arg0, float arg1, float arg2) {
        return this.rotateAffineYXZ(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4f rotateAffineYXZ(float arg0, float arg1, float arg2, Matrix4f arg3) {
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
        return arg3._m20(this.m10 * float7 + float9 * float1)
            ._m21(this.m11 * float7 + float10 * float1)
            ._m22(this.m12 * float7 + float11 * float1)
            ._m23(0.0F)
            ._m00(float12 * float5 + float15 * float4)
            ._m01(float13 * float5 + float16 * float4)
            ._m02(float14 * float5 + float17 * float4)
            ._m03(0.0F)
            ._m10(float12 * float8 + float15 * float5)
            ._m11(float13 * float8 + float16 * float5)
            ._m12(float14 * float8 + float17 * float5)
            ._m13(0.0F)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
    }

    @Override
    public Matrix4f rotate(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4) {
        if ((this.properties & 4) != 0) {
            return arg4.rotation(arg0, arg1, arg2, arg3);
        } else if ((this.properties & 8) != 0) {
            return this.rotateTranslation(arg0, arg1, arg2, arg3, arg4);
        } else {
            return (this.properties & 2) != 0 ? this.rotateAffine(arg0, arg1, arg2, arg3, arg4) : this.rotateGeneric(arg0, arg1, arg2, arg3, arg4);
        }
    }

    private Matrix4f rotateGeneric(float float3, float float0, float float2, float float1, Matrix4f matrix4f1) {
        if (float2 == 0.0F && float1 == 0.0F && Math.absEqualsOne(float0)) {
            return this.rotateX(float0 * float3, matrix4f1);
        } else if (float0 == 0.0F && float1 == 0.0F && Math.absEqualsOne(float2)) {
            return this.rotateY(float2 * float3, matrix4f1);
        } else {
            return float0 == 0.0F && float2 == 0.0F && Math.absEqualsOne(float1)
                ? this.rotateZ(float1 * float3, matrix4f1)
                : this.rotateGenericInternal(float3, float0, float2, float1, matrix4f1);
        }
    }

    private Matrix4f rotateGenericInternal(float float1, float float5, float float7, float float9, Matrix4f matrix4f1) {
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
        float float25 = this.m03 * float13 + this.m13 * float14 + this.m23 * float15;
        float float26 = this.m00 * float16 + this.m10 * float17 + this.m20 * float18;
        float float27 = this.m01 * float16 + this.m11 * float17 + this.m21 * float18;
        float float28 = this.m02 * float16 + this.m12 * float17 + this.m22 * float18;
        float float29 = this.m03 * float16 + this.m13 * float17 + this.m23 * float18;
        return matrix4f1._m20(this.m00 * float19 + this.m10 * float20 + this.m20 * float21)
            ._m21(this.m01 * float19 + this.m11 * float20 + this.m21 * float21)
            ._m22(this.m02 * float19 + this.m12 * float20 + this.m22 * float21)
            ._m23(this.m03 * float19 + this.m13 * float20 + this.m23 * float21)
            ._m00(float22)
            ._m01(float23)
            ._m02(float24)
            ._m03(float25)
            ._m10(float26)
            ._m11(float27)
            ._m12(float28)
            ._m13(float29)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
    }

    public Matrix4f rotate(float arg0, float arg1, float arg2, float arg3) {
        return this.rotate(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix4f rotateTranslation(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4) {
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

    private Matrix4f rotateTranslationInternal(float float1, float float5, float float7, float float9, Matrix4f matrix4f1) {
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
        return matrix4f1._m20(float19)
            ._m21(float20)
            ._m22(float21)
            ._m00(float13)
            ._m01(float14)
            ._m02(float15)
            ._m03(0.0F)
            ._m10(float16)
            ._m11(float17)
            ._m12(float18)
            ._m13(0.0F)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(1.0F)
            ._properties(this.properties & -14);
    }

    @Override
    public Matrix4f rotateAffine(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4) {
        if (arg2 == 0.0F && arg3 == 0.0F && Math.absEqualsOne(arg1)) {
            return this.rotateX(arg1 * arg0, arg4);
        } else if (arg1 == 0.0F && arg3 == 0.0F && Math.absEqualsOne(arg2)) {
            return this.rotateY(arg2 * arg0, arg4);
        } else {
            return arg1 == 0.0F && arg2 == 0.0F && Math.absEqualsOne(arg3)
                ? this.rotateZ(arg3 * arg0, arg4)
                : this.rotateAffineInternal(arg0, arg1, arg2, arg3, arg4);
        }
    }

    private Matrix4f rotateAffineInternal(float float1, float float5, float float7, float float9, Matrix4f matrix4f1) {
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
        return matrix4f1._m20(this.m00 * float19 + this.m10 * float20 + this.m20 * float21)
            ._m21(this.m01 * float19 + this.m11 * float20 + this.m21 * float21)
            ._m22(this.m02 * float19 + this.m12 * float20 + this.m22 * float21)
            ._m23(0.0F)
            ._m00(float22)
            ._m01(float23)
            ._m02(float24)
            ._m03(0.0F)
            ._m10(float25)
            ._m11(float26)
            ._m12(float27)
            ._m13(0.0F)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(1.0F)
            ._properties(this.properties & -14);
    }

    public Matrix4f rotateAffine(float arg0, float arg1, float arg2, float arg3) {
        return this.rotateAffine(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix4f rotateLocal(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4) {
        return (this.properties & 4) != 0 ? arg4.rotation(arg0, arg1, arg2, arg3) : this.rotateLocalGeneric(arg0, arg1, arg2, arg3, arg4);
    }

    private Matrix4f rotateLocalGeneric(float float3, float float0, float float2, float float1, Matrix4f matrix4f1) {
        if (float2 == 0.0F && float1 == 0.0F && Math.absEqualsOne(float0)) {
            return this.rotateLocalX(float0 * float3, matrix4f1);
        } else if (float0 == 0.0F && float1 == 0.0F && Math.absEqualsOne(float2)) {
            return this.rotateLocalY(float2 * float3, matrix4f1);
        } else {
            return float0 == 0.0F && float2 == 0.0F && Math.absEqualsOne(float1)
                ? this.rotateLocalZ(float1 * float3, matrix4f1)
                : this.rotateLocalGenericInternal(float3, float0, float2, float1, matrix4f1);
        }
    }

    private Matrix4f rotateLocalGenericInternal(float float1, float float5, float float7, float float9, Matrix4f matrix4f1) {
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
        return matrix4f1._m00(float22)
            ._m01(float23)
            ._m02(float24)
            ._m03(this.m03)
            ._m10(float25)
            ._m11(float26)
            ._m12(float27)
            ._m13(this.m13)
            ._m20(float28)
            ._m21(float29)
            ._m22(float30)
            ._m23(this.m23)
            ._m30(float31)
            ._m31(float32)
            ._m32(float33)
            ._m33(this.m33)
            ._properties(this.properties & -14);
    }

    public Matrix4f rotateLocal(float arg0, float arg1, float arg2, float arg3) {
        return this.rotateLocal(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix4f rotateLocalX(float arg0, Matrix4f arg1) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = float0 * this.m01 + float1 * this.m02;
        float float3 = float0 * this.m11 + float1 * this.m12;
        float float4 = float0 * this.m21 + float1 * this.m22;
        float float5 = float0 * this.m31 + float1 * this.m32;
        return arg1._m00(this.m00)
            ._m01(float1 * this.m01 - float0 * this.m02)
            ._m02(float2)
            ._m03(this.m03)
            ._m10(this.m10)
            ._m11(float1 * this.m11 - float0 * this.m12)
            ._m12(float3)
            ._m13(this.m13)
            ._m20(this.m20)
            ._m21(float1 * this.m21 - float0 * this.m22)
            ._m22(float4)
            ._m23(this.m23)
            ._m30(this.m30)
            ._m31(float1 * this.m31 - float0 * this.m32)
            ._m32(float5)
            ._m33(this.m33)
            ._properties(this.properties & -14);
    }

    public Matrix4f rotateLocalX(float arg0) {
        return this.rotateLocalX(arg0, this);
    }

    @Override
    public Matrix4f rotateLocalY(float arg0, Matrix4f arg1) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = -float0 * this.m00 + float1 * this.m02;
        float float3 = -float0 * this.m10 + float1 * this.m12;
        float float4 = -float0 * this.m20 + float1 * this.m22;
        float float5 = -float0 * this.m30 + float1 * this.m32;
        return arg1._m00(float1 * this.m00 + float0 * this.m02)
            ._m01(this.m01)
            ._m02(float2)
            ._m03(this.m03)
            ._m10(float1 * this.m10 + float0 * this.m12)
            ._m11(this.m11)
            ._m12(float3)
            ._m13(this.m13)
            ._m20(float1 * this.m20 + float0 * this.m22)
            ._m21(this.m21)
            ._m22(float4)
            ._m23(this.m23)
            ._m30(float1 * this.m30 + float0 * this.m32)
            ._m31(this.m31)
            ._m32(float5)
            ._m33(this.m33)
            ._properties(this.properties & -14);
    }

    public Matrix4f rotateLocalY(float arg0) {
        return this.rotateLocalY(arg0, this);
    }

    @Override
    public Matrix4f rotateLocalZ(float arg0, Matrix4f arg1) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = float0 * this.m00 + float1 * this.m01;
        float float3 = float0 * this.m10 + float1 * this.m11;
        float float4 = float0 * this.m20 + float1 * this.m21;
        float float5 = float0 * this.m30 + float1 * this.m31;
        return arg1._m00(float1 * this.m00 - float0 * this.m01)
            ._m01(float2)
            ._m02(this.m02)
            ._m03(this.m03)
            ._m10(float1 * this.m10 - float0 * this.m11)
            ._m11(float3)
            ._m12(this.m12)
            ._m13(this.m13)
            ._m20(float1 * this.m20 - float0 * this.m21)
            ._m21(float4)
            ._m22(this.m22)
            ._m23(this.m23)
            ._m30(float1 * this.m30 - float0 * this.m31)
            ._m31(float5)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
    }

    public Matrix4f rotateLocalZ(float arg0) {
        return this.rotateLocalZ(arg0, this);
    }

    public Matrix4f translate(Vector3fc arg0) {
        return this.translate(arg0.x(), arg0.y(), arg0.z());
    }

    @Override
    public Matrix4f translate(Vector3fc arg0, Matrix4f arg1) {
        return this.translate(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Matrix4f translate(float arg0, float arg1, float arg2, Matrix4f arg3) {
        return (this.properties & 4) != 0 ? arg3.translation(arg0, arg1, arg2) : this.translateGeneric(arg0, arg1, arg2, arg3);
    }

    private Matrix4f translateGeneric(float float0, float float1, float float2, Matrix4f matrix4f1) {
        MemUtil.INSTANCE.copy(this, matrix4f1);
        return matrix4f1._m30(Math.fma(this.m00, float0, Math.fma(this.m10, float1, Math.fma(this.m20, float2, this.m30))))
            ._m31(Math.fma(this.m01, float0, Math.fma(this.m11, float1, Math.fma(this.m21, float2, this.m31))))
            ._m32(Math.fma(this.m02, float0, Math.fma(this.m12, float1, Math.fma(this.m22, float2, this.m32))))
            ._m33(Math.fma(this.m03, float0, Math.fma(this.m13, float1, Math.fma(this.m23, float2, this.m33))))
            ._properties(this.properties & -6);
    }

    public Matrix4f translate(float arg0, float arg1, float arg2) {
        return (this.properties & 4) != 0 ? this.translation(arg0, arg1, arg2) : this.translateGeneric(arg0, arg1, arg2);
    }

    private Matrix4f translateGeneric(float float0, float float1, float float2) {
        return this._m30(Math.fma(this.m00, float0, Math.fma(this.m10, float1, Math.fma(this.m20, float2, this.m30))))
            ._m31(Math.fma(this.m01, float0, Math.fma(this.m11, float1, Math.fma(this.m21, float2, this.m31))))
            ._m32(Math.fma(this.m02, float0, Math.fma(this.m12, float1, Math.fma(this.m22, float2, this.m32))))
            ._m33(Math.fma(this.m03, float0, Math.fma(this.m13, float1, Math.fma(this.m23, float2, this.m33))))
            ._properties(this.properties & -6);
    }

    public Matrix4f translateLocal(Vector3fc arg0) {
        return this.translateLocal(arg0.x(), arg0.y(), arg0.z());
    }

    @Override
    public Matrix4f translateLocal(Vector3fc arg0, Matrix4f arg1) {
        return this.translateLocal(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Matrix4f translateLocal(float arg0, float arg1, float arg2, Matrix4f arg3) {
        return (this.properties & 4) != 0 ? arg3.translation(arg0, arg1, arg2) : this.translateLocalGeneric(arg0, arg1, arg2, arg3);
    }

    private Matrix4f translateLocalGeneric(float float1, float float3, float float5, Matrix4f matrix4f1) {
        float float0 = this.m00 + float1 * this.m03;
        float float2 = this.m01 + float3 * this.m03;
        float float4 = this.m02 + float5 * this.m03;
        float float6 = this.m10 + float1 * this.m13;
        float float7 = this.m11 + float3 * this.m13;
        float float8 = this.m12 + float5 * this.m13;
        float float9 = this.m20 + float1 * this.m23;
        float float10 = this.m21 + float3 * this.m23;
        float float11 = this.m22 + float5 * this.m23;
        float float12 = this.m30 + float1 * this.m33;
        float float13 = this.m31 + float3 * this.m33;
        float float14 = this.m32 + float5 * this.m33;
        return matrix4f1._m00(float0)
            ._m01(float2)
            ._m02(float4)
            ._m03(this.m03)
            ._m10(float6)
            ._m11(float7)
            ._m12(float8)
            ._m13(this.m13)
            ._m20(float9)
            ._m21(float10)
            ._m22(float11)
            ._m23(this.m23)
            ._m30(float12)
            ._m31(float13)
            ._m32(float14)
            ._m33(this.m33)
            ._properties(this.properties & -6);
    }

    public Matrix4f translateLocal(float arg0, float arg1, float arg2) {
        return this.translateLocal(arg0, arg1, arg2, this);
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        arg0.writeFloat(this.m00);
        arg0.writeFloat(this.m01);
        arg0.writeFloat(this.m02);
        arg0.writeFloat(this.m03);
        arg0.writeFloat(this.m10);
        arg0.writeFloat(this.m11);
        arg0.writeFloat(this.m12);
        arg0.writeFloat(this.m13);
        arg0.writeFloat(this.m20);
        arg0.writeFloat(this.m21);
        arg0.writeFloat(this.m22);
        arg0.writeFloat(this.m23);
        arg0.writeFloat(this.m30);
        arg0.writeFloat(this.m31);
        arg0.writeFloat(this.m32);
        arg0.writeFloat(this.m33);
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException {
        this._m00(arg0.readFloat())
            ._m01(arg0.readFloat())
            ._m02(arg0.readFloat())
            ._m03(arg0.readFloat())
            ._m10(arg0.readFloat())
            ._m11(arg0.readFloat())
            ._m12(arg0.readFloat())
            ._m13(arg0.readFloat())
            ._m20(arg0.readFloat())
            ._m21(arg0.readFloat())
            ._m22(arg0.readFloat())
            ._m23(arg0.readFloat())
            ._m30(arg0.readFloat())
            ._m31(arg0.readFloat())
            ._m32(arg0.readFloat())
            ._m33(arg0.readFloat())
            .determineProperties();
    }

    @Override
    public Matrix4f ortho(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6, Matrix4f arg7) {
        return (this.properties & 4) != 0
            ? arg7.setOrtho(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
            : this.orthoGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }

    private Matrix4f orthoGeneric(float float2, float float1, float float5, float float4, float float7, float float8, boolean boolean0, Matrix4f matrix4f1) {
        float float0 = 2.0F / (float1 - float2);
        float float3 = 2.0F / (float4 - float5);
        float float6 = (boolean0 ? 1.0F : 2.0F) / (float7 - float8);
        float float9 = (float2 + float1) / (float2 - float1);
        float float10 = (float4 + float5) / (float5 - float4);
        float float11 = (boolean0 ? float7 : float8 + float7) / (float7 - float8);
        matrix4f1._m30(this.m00 * float9 + this.m10 * float10 + this.m20 * float11 + this.m30)
            ._m31(this.m01 * float9 + this.m11 * float10 + this.m21 * float11 + this.m31)
            ._m32(this.m02 * float9 + this.m12 * float10 + this.m22 * float11 + this.m32)
            ._m33(this.m03 * float9 + this.m13 * float10 + this.m23 * float11 + this.m33)
            ._m00(this.m00 * float0)
            ._m01(this.m01 * float0)
            ._m02(this.m02 * float0)
            ._m03(this.m03 * float0)
            ._m10(this.m10 * float3)
            ._m11(this.m11 * float3)
            ._m12(this.m12 * float3)
            ._m13(this.m13 * float3)
            ._m20(this.m20 * float6)
            ._m21(this.m21 * float6)
            ._m22(this.m22 * float6)
            ._m23(this.m23 * float6)
            ._properties(this.properties & -30);
        return matrix4f1;
    }

    @Override
    public Matrix4f ortho(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6) {
        return this.ortho(arg0, arg1, arg2, arg3, arg4, arg5, false, arg6);
    }

    public Matrix4f ortho(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6) {
        return this.ortho(arg0, arg1, arg2, arg3, arg4, arg5, arg6, this);
    }

    public Matrix4f ortho(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.ortho(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    @Override
    public Matrix4f orthoLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6, Matrix4f arg7) {
        return (this.properties & 4) != 0
            ? arg7.setOrthoLH(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
            : this.orthoLHGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }

    private Matrix4f orthoLHGeneric(float float2, float float1, float float5, float float4, float float8, float float7, boolean boolean0, Matrix4f matrix4f1) {
        float float0 = 2.0F / (float1 - float2);
        float float3 = 2.0F / (float4 - float5);
        float float6 = (boolean0 ? 1.0F : 2.0F) / (float7 - float8);
        float float9 = (float2 + float1) / (float2 - float1);
        float float10 = (float4 + float5) / (float5 - float4);
        float float11 = (boolean0 ? float8 : float7 + float8) / (float8 - float7);
        matrix4f1._m30(this.m00 * float9 + this.m10 * float10 + this.m20 * float11 + this.m30)
            ._m31(this.m01 * float9 + this.m11 * float10 + this.m21 * float11 + this.m31)
            ._m32(this.m02 * float9 + this.m12 * float10 + this.m22 * float11 + this.m32)
            ._m33(this.m03 * float9 + this.m13 * float10 + this.m23 * float11 + this.m33)
            ._m00(this.m00 * float0)
            ._m01(this.m01 * float0)
            ._m02(this.m02 * float0)
            ._m03(this.m03 * float0)
            ._m10(this.m10 * float3)
            ._m11(this.m11 * float3)
            ._m12(this.m12 * float3)
            ._m13(this.m13 * float3)
            ._m20(this.m20 * float6)
            ._m21(this.m21 * float6)
            ._m22(this.m22 * float6)
            ._m23(this.m23 * float6)
            ._properties(this.properties & -30);
        return matrix4f1;
    }

    @Override
    public Matrix4f orthoLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6) {
        return this.orthoLH(arg0, arg1, arg2, arg3, arg4, arg5, false, arg6);
    }

    public Matrix4f orthoLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6) {
        return this.orthoLH(arg0, arg1, arg2, arg3, arg4, arg5, arg6, this);
    }

    public Matrix4f orthoLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.orthoLH(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    public Matrix4f setOrtho(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6) {
        if ((this.properties & 4) == 0) {
            MemUtil.INSTANCE.identity(this);
        }

        this._m00(2.0F / (arg1 - arg0))
            ._m11(2.0F / (arg3 - arg2))
            ._m22((arg6 ? 1.0F : 2.0F) / (arg4 - arg5))
            ._m30((arg1 + arg0) / (arg0 - arg1))
            ._m31((arg3 + arg2) / (arg2 - arg3))
            ._m32((arg6 ? arg4 : arg5 + arg4) / (arg4 - arg5))
            ._properties(2);
        return this;
    }

    public Matrix4f setOrtho(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.setOrtho(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    public Matrix4f setOrthoLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6) {
        if ((this.properties & 4) == 0) {
            MemUtil.INSTANCE.identity(this);
        }

        this._m00(2.0F / (arg1 - arg0))
            ._m11(2.0F / (arg3 - arg2))
            ._m22((arg6 ? 1.0F : 2.0F) / (arg5 - arg4))
            ._m30((arg1 + arg0) / (arg0 - arg1))
            ._m31((arg3 + arg2) / (arg2 - arg3))
            ._m32((arg6 ? arg4 : arg5 + arg4) / (arg4 - arg5))
            ._properties(2);
        return this;
    }

    public Matrix4f setOrthoLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.setOrthoLH(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    @Override
    public Matrix4f orthoSymmetric(float arg0, float arg1, float arg2, float arg3, boolean arg4, Matrix4f arg5) {
        return (this.properties & 4) != 0
            ? arg5.setOrthoSymmetric(arg0, arg1, arg2, arg3, arg4)
            : this.orthoSymmetricGeneric(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    private Matrix4f orthoSymmetricGeneric(float float1, float float3, float float5, float float6, boolean boolean0, Matrix4f matrix4f1) {
        float float0 = 2.0F / float1;
        float float2 = 2.0F / float3;
        float float4 = (boolean0 ? 1.0F : 2.0F) / (float5 - float6);
        float float7 = (boolean0 ? float5 : float6 + float5) / (float5 - float6);
        matrix4f1._m30(this.m20 * float7 + this.m30)
            ._m31(this.m21 * float7 + this.m31)
            ._m32(this.m22 * float7 + this.m32)
            ._m33(this.m23 * float7 + this.m33)
            ._m00(this.m00 * float0)
            ._m01(this.m01 * float0)
            ._m02(this.m02 * float0)
            ._m03(this.m03 * float0)
            ._m10(this.m10 * float2)
            ._m11(this.m11 * float2)
            ._m12(this.m12 * float2)
            ._m13(this.m13 * float2)
            ._m20(this.m20 * float4)
            ._m21(this.m21 * float4)
            ._m22(this.m22 * float4)
            ._m23(this.m23 * float4)
            ._properties(this.properties & -30);
        return matrix4f1;
    }

    @Override
    public Matrix4f orthoSymmetric(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4) {
        return this.orthoSymmetric(arg0, arg1, arg2, arg3, false, arg4);
    }

    public Matrix4f orthoSymmetric(float arg0, float arg1, float arg2, float arg3, boolean arg4) {
        return this.orthoSymmetric(arg0, arg1, arg2, arg3, arg4, this);
    }

    public Matrix4f orthoSymmetric(float arg0, float arg1, float arg2, float arg3) {
        return this.orthoSymmetric(arg0, arg1, arg2, arg3, false, this);
    }

    @Override
    public Matrix4f orthoSymmetricLH(float arg0, float arg1, float arg2, float arg3, boolean arg4, Matrix4f arg5) {
        return (this.properties & 4) != 0
            ? arg5.setOrthoSymmetricLH(arg0, arg1, arg2, arg3, arg4)
            : this.orthoSymmetricLHGeneric(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    private Matrix4f orthoSymmetricLHGeneric(float float1, float float3, float float6, float float5, boolean boolean0, Matrix4f matrix4f1) {
        float float0 = 2.0F / float1;
        float float2 = 2.0F / float3;
        float float4 = (boolean0 ? 1.0F : 2.0F) / (float5 - float6);
        float float7 = (boolean0 ? float6 : float5 + float6) / (float6 - float5);
        matrix4f1._m30(this.m20 * float7 + this.m30)
            ._m31(this.m21 * float7 + this.m31)
            ._m32(this.m22 * float7 + this.m32)
            ._m33(this.m23 * float7 + this.m33)
            ._m00(this.m00 * float0)
            ._m01(this.m01 * float0)
            ._m02(this.m02 * float0)
            ._m03(this.m03 * float0)
            ._m10(this.m10 * float2)
            ._m11(this.m11 * float2)
            ._m12(this.m12 * float2)
            ._m13(this.m13 * float2)
            ._m20(this.m20 * float4)
            ._m21(this.m21 * float4)
            ._m22(this.m22 * float4)
            ._m23(this.m23 * float4)
            ._properties(this.properties & -30);
        return matrix4f1;
    }

    @Override
    public Matrix4f orthoSymmetricLH(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4) {
        return this.orthoSymmetricLH(arg0, arg1, arg2, arg3, false, arg4);
    }

    public Matrix4f orthoSymmetricLH(float arg0, float arg1, float arg2, float arg3, boolean arg4) {
        return this.orthoSymmetricLH(arg0, arg1, arg2, arg3, arg4, this);
    }

    public Matrix4f orthoSymmetricLH(float arg0, float arg1, float arg2, float arg3) {
        return this.orthoSymmetricLH(arg0, arg1, arg2, arg3, false, this);
    }

    public Matrix4f setOrthoSymmetric(float arg0, float arg1, float arg2, float arg3, boolean arg4) {
        if ((this.properties & 4) == 0) {
            MemUtil.INSTANCE.identity(this);
        }

        this._m00(2.0F / arg0)._m11(2.0F / arg1)._m22((arg4 ? 1.0F : 2.0F) / (arg2 - arg3))._m32((arg4 ? arg2 : arg3 + arg2) / (arg2 - arg3))._properties(2);
        return this;
    }

    public Matrix4f setOrthoSymmetric(float arg0, float arg1, float arg2, float arg3) {
        return this.setOrthoSymmetric(arg0, arg1, arg2, arg3, false);
    }

    public Matrix4f setOrthoSymmetricLH(float arg0, float arg1, float arg2, float arg3, boolean arg4) {
        if ((this.properties & 4) == 0) {
            MemUtil.INSTANCE.identity(this);
        }

        this._m00(2.0F / arg0)._m11(2.0F / arg1)._m22((arg4 ? 1.0F : 2.0F) / (arg3 - arg2))._m32((arg4 ? arg2 : arg3 + arg2) / (arg2 - arg3))._properties(2);
        return this;
    }

    public Matrix4f setOrthoSymmetricLH(float arg0, float arg1, float arg2, float arg3) {
        return this.setOrthoSymmetricLH(arg0, arg1, arg2, arg3, false);
    }

    @Override
    public Matrix4f ortho2D(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4) {
        return (this.properties & 4) != 0 ? arg4.setOrtho2D(arg0, arg1, arg2, arg3) : this.ortho2DGeneric(arg0, arg1, arg2, arg3, arg4);
    }

    private Matrix4f ortho2DGeneric(float float2, float float1, float float5, float float4, Matrix4f matrix4f1) {
        float float0 = 2.0F / (float1 - float2);
        float float3 = 2.0F / (float4 - float5);
        float float6 = (float1 + float2) / (float2 - float1);
        float float7 = (float4 + float5) / (float5 - float4);
        matrix4f1._m30(this.m00 * float6 + this.m10 * float7 + this.m30)
            ._m31(this.m01 * float6 + this.m11 * float7 + this.m31)
            ._m32(this.m02 * float6 + this.m12 * float7 + this.m32)
            ._m33(this.m03 * float6 + this.m13 * float7 + this.m33)
            ._m00(this.m00 * float0)
            ._m01(this.m01 * float0)
            ._m02(this.m02 * float0)
            ._m03(this.m03 * float0)
            ._m10(this.m10 * float3)
            ._m11(this.m11 * float3)
            ._m12(this.m12 * float3)
            ._m13(this.m13 * float3)
            ._m20(-this.m20)
            ._m21(-this.m21)
            ._m22(-this.m22)
            ._m23(-this.m23)
            ._properties(this.properties & -30);
        return matrix4f1;
    }

    public Matrix4f ortho2D(float arg0, float arg1, float arg2, float arg3) {
        return this.ortho2D(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix4f ortho2DLH(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4) {
        return (this.properties & 4) != 0 ? arg4.setOrtho2DLH(arg0, arg1, arg2, arg3) : this.ortho2DLHGeneric(arg0, arg1, arg2, arg3, arg4);
    }

    private Matrix4f ortho2DLHGeneric(float float2, float float1, float float5, float float4, Matrix4f matrix4f1) {
        float float0 = 2.0F / (float1 - float2);
        float float3 = 2.0F / (float4 - float5);
        float float6 = (float1 + float2) / (float2 - float1);
        float float7 = (float4 + float5) / (float5 - float4);
        matrix4f1._m30(this.m00 * float6 + this.m10 * float7 + this.m30)
            ._m31(this.m01 * float6 + this.m11 * float7 + this.m31)
            ._m32(this.m02 * float6 + this.m12 * float7 + this.m32)
            ._m33(this.m03 * float6 + this.m13 * float7 + this.m33)
            ._m00(this.m00 * float0)
            ._m01(this.m01 * float0)
            ._m02(this.m02 * float0)
            ._m03(this.m03 * float0)
            ._m10(this.m10 * float3)
            ._m11(this.m11 * float3)
            ._m12(this.m12 * float3)
            ._m13(this.m13 * float3)
            ._m20(this.m20)
            ._m21(this.m21)
            ._m22(this.m22)
            ._m23(this.m23)
            ._properties(this.properties & -30);
        return matrix4f1;
    }

    public Matrix4f ortho2DLH(float arg0, float arg1, float arg2, float arg3) {
        return this.ortho2DLH(arg0, arg1, arg2, arg3, this);
    }

    public Matrix4f setOrtho2D(float arg0, float arg1, float arg2, float arg3) {
        if ((this.properties & 4) == 0) {
            MemUtil.INSTANCE.identity(this);
        }

        this._m00(2.0F / (arg1 - arg0))
            ._m11(2.0F / (arg3 - arg2))
            ._m22(-1.0F)
            ._m30((arg1 + arg0) / (arg0 - arg1))
            ._m31((arg3 + arg2) / (arg2 - arg3))
            ._properties(2);
        return this;
    }

    public Matrix4f setOrtho2DLH(float arg0, float arg1, float arg2, float arg3) {
        if ((this.properties & 4) == 0) {
            MemUtil.INSTANCE.identity(this);
        }

        this._m00(2.0F / (arg1 - arg0))._m11(2.0F / (arg3 - arg2))._m30((arg1 + arg0) / (arg0 - arg1))._m31((arg3 + arg2) / (arg2 - arg3))._properties(2);
        return this;
    }

    public Matrix4f lookAlong(Vector3fc arg0, Vector3fc arg1) {
        return this.lookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), this);
    }

    @Override
    public Matrix4f lookAlong(Vector3fc arg0, Vector3fc arg1, Matrix4f arg2) {
        return this.lookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    @Override
    public Matrix4f lookAlong(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6) {
        return (this.properties & 4) != 0
            ? arg6.setLookAlong(arg0, arg1, arg2, arg3, arg4, arg5)
            : this.lookAlongGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
    }

    private Matrix4f lookAlongGeneric(float float3, float float2, float float1, float float8, float float6, float float5, Matrix4f matrix4f1) {
        float float0 = Math.invsqrt(float3 * float3 + float2 * float2 + float1 * float1);
        float3 *= -float0;
        float2 *= -float0;
        float1 *= -float0;
        float float4 = float6 * float1 - float5 * float2;
        float float7 = float5 * float3 - float8 * float1;
        float float9 = float8 * float2 - float6 * float3;
        float float10 = Math.invsqrt(float4 * float4 + float7 * float7 + float9 * float9);
        float4 *= float10;
        float7 *= float10;
        float9 *= float10;
        float float11 = float2 * float9 - float1 * float7;
        float float12 = float1 * float4 - float3 * float9;
        float float13 = float3 * float7 - float2 * float4;
        float float14 = this.m00 * float4 + this.m10 * float11 + this.m20 * float3;
        float float15 = this.m01 * float4 + this.m11 * float11 + this.m21 * float3;
        float float16 = this.m02 * float4 + this.m12 * float11 + this.m22 * float3;
        float float17 = this.m03 * float4 + this.m13 * float11 + this.m23 * float3;
        float float18 = this.m00 * float7 + this.m10 * float12 + this.m20 * float2;
        float float19 = this.m01 * float7 + this.m11 * float12 + this.m21 * float2;
        float float20 = this.m02 * float7 + this.m12 * float12 + this.m22 * float2;
        float float21 = this.m03 * float7 + this.m13 * float12 + this.m23 * float2;
        return matrix4f1._m20(this.m00 * float9 + this.m10 * float13 + this.m20 * float1)
            ._m21(this.m01 * float9 + this.m11 * float13 + this.m21 * float1)
            ._m22(this.m02 * float9 + this.m12 * float13 + this.m22 * float1)
            ._m23(this.m03 * float9 + this.m13 * float13 + this.m23 * float1)
            ._m00(float14)
            ._m01(float15)
            ._m02(float16)
            ._m03(float17)
            ._m10(float18)
            ._m11(float19)
            ._m12(float20)
            ._m13(float21)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
    }

    public Matrix4f lookAlong(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.lookAlong(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    public Matrix4f setLookAlong(Vector3fc arg0, Vector3fc arg1) {
        return this.setLookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix4f setLookAlong(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
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
        this._m00(float1)
            ._m01(arg1 * float3 - arg2 * float2)
            ._m02(arg0)
            ._m03(0.0F)
            ._m10(float2)
            ._m11(arg2 * float1 - arg0 * float3)
            ._m12(arg1)
            ._m13(0.0F)
            ._m20(float3)
            ._m21(arg0 * float2 - arg1 * float1)
            ._m22(arg2)
            ._m23(0.0F)
            ._m30(0.0F)
            ._m31(0.0F)
            ._m32(0.0F)
            ._m33(1.0F)
            ._properties(18);
        return this;
    }

    public Matrix4f setLookAt(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2) {
        return this.setLookAt(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z());
    }

    public Matrix4f setLookAt(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8) {
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
        return this._m00(float4)
            ._m01(float8)
            ._m02(float0)
            ._m03(0.0F)
            ._m10(float5)
            ._m11(float9)
            ._m12(float1)
            ._m13(0.0F)
            ._m20(float6)
            ._m21(float10)
            ._m22(float2)
            ._m23(0.0F)
            ._m30(-(float4 * arg0 + float5 * arg1 + float6 * arg2))
            ._m31(-(float8 * arg0 + float9 * arg1 + float10 * arg2))
            ._m32(-(float0 * arg0 + float1 * arg1 + float2 * arg2))
            ._m33(1.0F)
            ._properties(18);
    }

    @Override
    public Matrix4f lookAt(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2, Matrix4f arg3) {
        return this.lookAt(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z(), arg3);
    }

    public Matrix4f lookAt(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2) {
        return this.lookAt(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z(), this);
    }

    @Override
    public Matrix4f lookAt(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8, Matrix4f arg9) {
        if ((this.properties & 4) != 0) {
            return arg9.setLookAt(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
        } else {
            return (this.properties & 1) != 0
                ? this.lookAtPerspective(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
                : this.lookAtGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
        }
    }

    private Matrix4f lookAtGeneric(
        float float1, float float4, float float7, float float2, float float5, float float8, float float14, float float12, float float11, Matrix4f matrix4f1
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
        float float23 = this.m00 * float10 + this.m10 * float17 + this.m20 * float0;
        float float24 = this.m01 * float10 + this.m11 * float17 + this.m21 * float0;
        float float25 = this.m02 * float10 + this.m12 * float17 + this.m22 * float0;
        float float26 = this.m03 * float10 + this.m13 * float17 + this.m23 * float0;
        float float27 = this.m00 * float13 + this.m10 * float18 + this.m20 * float3;
        float float28 = this.m01 * float13 + this.m11 * float18 + this.m21 * float3;
        float float29 = this.m02 * float13 + this.m12 * float18 + this.m22 * float3;
        float float30 = this.m03 * float13 + this.m13 * float18 + this.m23 * float3;
        return matrix4f1._m30(this.m00 * float20 + this.m10 * float21 + this.m20 * float22 + this.m30)
            ._m31(this.m01 * float20 + this.m11 * float21 + this.m21 * float22 + this.m31)
            ._m32(this.m02 * float20 + this.m12 * float21 + this.m22 * float22 + this.m32)
            ._m33(this.m03 * float20 + this.m13 * float21 + this.m23 * float22 + this.m33)
            ._m20(this.m00 * float15 + this.m10 * float19 + this.m20 * float6)
            ._m21(this.m01 * float15 + this.m11 * float19 + this.m21 * float6)
            ._m22(this.m02 * float15 + this.m12 * float19 + this.m22 * float6)
            ._m23(this.m03 * float15 + this.m13 * float19 + this.m23 * float6)
            ._m00(float23)
            ._m01(float24)
            ._m02(float25)
            ._m03(float26)
            ._m10(float27)
            ._m11(float28)
            ._m12(float29)
            ._m13(float30)
            ._properties(this.properties & -14);
    }

    @Override
    public Matrix4f lookAtPerspective(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8, Matrix4f arg9) {
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
        float float11 = -(float4 * arg0 + float5 * arg1 + float6 * arg2);
        float float12 = -(float8 * arg0 + float9 * arg1 + float10 * arg2);
        float float13 = -(float0 * arg0 + float1 * arg1 + float2 * arg2);
        float float14 = this.m00 * float5;
        float float15 = this.m00 * float6;
        float float16 = this.m11 * float10;
        float float17 = this.m00 * float11;
        float float18 = this.m11 * float12;
        float float19 = this.m22 * float13 + this.m32;
        float float20 = this.m23 * float13;
        return arg9._m00(this.m00 * float4)
            ._m01(this.m11 * float8)
            ._m02(this.m22 * float0)
            ._m03(this.m23 * float0)
            ._m10(float14)
            ._m11(this.m11 * float9)
            ._m12(this.m22 * float1)
            ._m13(this.m23 * float1)
            ._m20(float15)
            ._m21(float16)
            ._m22(this.m22 * float2)
            ._m23(this.m23 * float2)
            ._m30(float17)
            ._m31(float18)
            ._m32(float19)
            ._m33(float20)
            ._properties(0);
    }

    public Matrix4f lookAt(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8) {
        return this.lookAt(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, this);
    }

    public Matrix4f setLookAtLH(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2) {
        return this.setLookAtLH(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z());
    }

    public Matrix4f setLookAtLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8) {
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
        this._m00(float4)
            ._m01(float8)
            ._m02(float0)
            ._m03(0.0F)
            ._m10(float5)
            ._m11(float9)
            ._m12(float1)
            ._m13(0.0F)
            ._m20(float6)
            ._m21(float10)
            ._m22(float2)
            ._m23(0.0F)
            ._m30(-(float4 * arg0 + float5 * arg1 + float6 * arg2))
            ._m31(-(float8 * arg0 + float9 * arg1 + float10 * arg2))
            ._m32(-(float0 * arg0 + float1 * arg1 + float2 * arg2))
            ._m33(1.0F)
            ._properties(18);
        return this;
    }

    @Override
    public Matrix4f lookAtLH(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2, Matrix4f arg3) {
        return this.lookAtLH(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z(), arg3);
    }

    public Matrix4f lookAtLH(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2) {
        return this.lookAtLH(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z(), this);
    }

    @Override
    public Matrix4f lookAtLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8, Matrix4f arg9) {
        if ((this.properties & 4) != 0) {
            return arg9.setLookAtLH(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
        } else {
            return (this.properties & 1) != 0
                ? this.lookAtPerspectiveLH(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
                : this.lookAtLHGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
        }
    }

    private Matrix4f lookAtLHGeneric(
        float float2, float float5, float float8, float float1, float float4, float float7, float float14, float float12, float float11, Matrix4f matrix4f1
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
        float float23 = this.m00 * float10 + this.m10 * float17 + this.m20 * float0;
        float float24 = this.m01 * float10 + this.m11 * float17 + this.m21 * float0;
        float float25 = this.m02 * float10 + this.m12 * float17 + this.m22 * float0;
        float float26 = this.m03 * float10 + this.m13 * float17 + this.m23 * float0;
        float float27 = this.m00 * float13 + this.m10 * float18 + this.m20 * float3;
        float float28 = this.m01 * float13 + this.m11 * float18 + this.m21 * float3;
        float float29 = this.m02 * float13 + this.m12 * float18 + this.m22 * float3;
        float float30 = this.m03 * float13 + this.m13 * float18 + this.m23 * float3;
        return matrix4f1._m30(this.m00 * float20 + this.m10 * float21 + this.m20 * float22 + this.m30)
            ._m31(this.m01 * float20 + this.m11 * float21 + this.m21 * float22 + this.m31)
            ._m32(this.m02 * float20 + this.m12 * float21 + this.m22 * float22 + this.m32)
            ._m33(this.m03 * float20 + this.m13 * float21 + this.m23 * float22 + this.m33)
            ._m20(this.m00 * float15 + this.m10 * float19 + this.m20 * float6)
            ._m21(this.m01 * float15 + this.m11 * float19 + this.m21 * float6)
            ._m22(this.m02 * float15 + this.m12 * float19 + this.m22 * float6)
            ._m23(this.m03 * float15 + this.m13 * float19 + this.m23 * float6)
            ._m00(float23)
            ._m01(float24)
            ._m02(float25)
            ._m03(float26)
            ._m10(float27)
            ._m11(float28)
            ._m12(float29)
            ._m13(float30)
            ._properties(this.properties & -14);
    }

    public Matrix4f lookAtLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8) {
        return this.lookAtLH(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, this);
    }

    @Override
    public Matrix4f lookAtPerspectiveLH(
        float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8, Matrix4f arg9
    ) {
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
        float float11 = -(float4 * arg0 + float5 * arg1 + float6 * arg2);
        float float12 = -(float8 * arg0 + float9 * arg1 + float10 * arg2);
        float float13 = -(float0 * arg0 + float1 * arg1 + float2 * arg2);
        float float14 = this.m00 * float4;
        float float15 = this.m11 * float8;
        float float16 = this.m22 * float0;
        float float17 = this.m23 * float0;
        float float18 = this.m00 * float5;
        float float19 = this.m11 * float9;
        float float20 = this.m22 * float1;
        float float21 = this.m23 * float1;
        float float22 = this.m00 * float6;
        float float23 = this.m11 * float10;
        float float24 = this.m22 * float2;
        float float25 = this.m23 * float2;
        float float26 = this.m00 * float11;
        float float27 = this.m11 * float12;
        float float28 = this.m22 * float13 + this.m32;
        float float29 = this.m23 * float13;
        return arg9._m00(float14)
            ._m01(float15)
            ._m02(float16)
            ._m03(float17)
            ._m10(float18)
            ._m11(float19)
            ._m12(float20)
            ._m13(float21)
            ._m20(float22)
            ._m21(float23)
            ._m22(float24)
            ._m23(float25)
            ._m30(float26)
            ._m31(float27)
            ._m32(float28)
            ._m33(float29)
            ._properties(0);
    }

    @Override
    public Matrix4f perspective(float arg0, float arg1, float arg2, float arg3, boolean arg4, Matrix4f arg5) {
        return (this.properties & 4) != 0 ? arg5.setPerspective(arg0, arg1, arg2, arg3, arg4) : this.perspectiveGeneric(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    private Matrix4f perspectiveGeneric(float float1, float float3, float float6, float float5, boolean boolean2, Matrix4f matrix4f1) {
        float float0 = Math.tan(float1 * 0.5F);
        float float2 = 1.0F / (float0 * float3);
        float float4 = 1.0F / float0;
        boolean boolean0 = float5 > 0.0F && Float.isInfinite(float5);
        boolean boolean1 = float6 > 0.0F && Float.isInfinite(float6);
        float float7;
        float float8;
        if (boolean0) {
            float float9 = 1.0E-6F;
            float7 = float9 - 1.0F;
            float8 = (float9 - (boolean2 ? 1.0F : 2.0F)) * float6;
        } else if (boolean1) {
            float float10 = 1.0E-6F;
            float7 = (boolean2 ? 0.0F : 1.0F) - float10;
            float8 = ((boolean2 ? 1.0F : 2.0F) - float10) * float5;
        } else {
            float7 = (boolean2 ? float5 : float5 + float6) / (float6 - float5);
            float8 = (boolean2 ? float5 : float5 + float5) * float6 / (float6 - float5);
        }

        float float11 = this.m20 * float7 - this.m30;
        float float12 = this.m21 * float7 - this.m31;
        float float13 = this.m22 * float7 - this.m32;
        float float14 = this.m23 * float7 - this.m33;
        matrix4f1._m00(this.m00 * float2)
            ._m01(this.m01 * float2)
            ._m02(this.m02 * float2)
            ._m03(this.m03 * float2)
            ._m10(this.m10 * float4)
            ._m11(this.m11 * float4)
            ._m12(this.m12 * float4)
            ._m13(this.m13 * float4)
            ._m30(this.m20 * float8)
            ._m31(this.m21 * float8)
            ._m32(this.m22 * float8)
            ._m33(this.m23 * float8)
            ._m20(float11)
            ._m21(float12)
            ._m22(float13)
            ._m23(float14)
            ._properties(this.properties & -31);
        return matrix4f1;
    }

    @Override
    public Matrix4f perspective(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4) {
        return this.perspective(arg0, arg1, arg2, arg3, false, arg4);
    }

    public Matrix4f perspective(float arg0, float arg1, float arg2, float arg3, boolean arg4) {
        return this.perspective(arg0, arg1, arg2, arg3, arg4, this);
    }

    public Matrix4f perspective(float arg0, float arg1, float arg2, float arg3) {
        return this.perspective(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix4f perspectiveRect(float arg0, float arg1, float arg2, float arg3, boolean arg4, Matrix4f arg5) {
        return (this.properties & 4) != 0
            ? arg5.setPerspectiveRect(arg0, arg1, arg2, arg3, arg4)
            : this.perspectiveRectGeneric(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    private Matrix4f perspectiveRectGeneric(float float1, float float4, float float2, float float5, boolean boolean2, Matrix4f matrix4f1) {
        float float0 = (float2 + float2) / float1;
        float float3 = (float2 + float2) / float4;
        boolean boolean0 = float5 > 0.0F && Float.isInfinite(float5);
        boolean boolean1 = float2 > 0.0F && Float.isInfinite(float2);
        float float6;
        float float7;
        if (boolean0) {
            float float8 = 1.0E-6F;
            float6 = float8 - 1.0F;
            float7 = (float8 - (boolean2 ? 1.0F : 2.0F)) * float2;
        } else if (boolean1) {
            float float9 = 1.0E-6F;
            float6 = (boolean2 ? 0.0F : 1.0F) - float9;
            float7 = ((boolean2 ? 1.0F : 2.0F) - float9) * float5;
        } else {
            float6 = (boolean2 ? float5 : float5 + float2) / (float2 - float5);
            float7 = (boolean2 ? float5 : float5 + float5) * float2 / (float2 - float5);
        }

        float float10 = this.m20 * float6 - this.m30;
        float float11 = this.m21 * float6 - this.m31;
        float float12 = this.m22 * float6 - this.m32;
        float float13 = this.m23 * float6 - this.m33;
        matrix4f1._m00(this.m00 * float0)
            ._m01(this.m01 * float0)
            ._m02(this.m02 * float0)
            ._m03(this.m03 * float0)
            ._m10(this.m10 * float3)
            ._m11(this.m11 * float3)
            ._m12(this.m12 * float3)
            ._m13(this.m13 * float3)
            ._m30(this.m20 * float7)
            ._m31(this.m21 * float7)
            ._m32(this.m22 * float7)
            ._m33(this.m23 * float7)
            ._m20(float10)
            ._m21(float11)
            ._m22(float12)
            ._m23(float13)
            ._properties(this.properties & -31);
        return matrix4f1;
    }

    @Override
    public Matrix4f perspectiveRect(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4) {
        return this.perspectiveRect(arg0, arg1, arg2, arg3, false, arg4);
    }

    @Override
    public Matrix4f perspectiveRect(float arg0, float arg1, float arg2, float arg3, boolean arg4) {
        return this.perspectiveRect(arg0, arg1, arg2, arg3, arg4, this);
    }

    @Override
    public Matrix4f perspectiveRect(float arg0, float arg1, float arg2, float arg3) {
        return this.perspectiveRect(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix4f perspectiveOffCenter(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6, Matrix4f arg7) {
        return (this.properties & 4) != 0
            ? arg7.setPerspectiveOffCenter(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
            : this.perspectiveOffCenterGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }

    private Matrix4f perspectiveOffCenterGeneric(
        float float1, float float6, float float8, float float3, float float12, float float11, boolean boolean2, Matrix4f matrix4f1
    ) {
        float float0 = Math.tan(float1 * 0.5F);
        float float2 = 1.0F / (float0 * float3);
        float float4 = 1.0F / float0;
        float float5 = Math.tan(float6);
        float float7 = Math.tan(float8);
        float float9 = float5 * float2;
        float float10 = float7 * float4;
        boolean boolean0 = float11 > 0.0F && Float.isInfinite(float11);
        boolean boolean1 = float12 > 0.0F && Float.isInfinite(float12);
        float float13;
        float float14;
        if (boolean0) {
            float float15 = 1.0E-6F;
            float13 = float15 - 1.0F;
            float14 = (float15 - (boolean2 ? 1.0F : 2.0F)) * float12;
        } else if (boolean1) {
            float float16 = 1.0E-6F;
            float13 = (boolean2 ? 0.0F : 1.0F) - float16;
            float14 = ((boolean2 ? 1.0F : 2.0F) - float16) * float11;
        } else {
            float13 = (boolean2 ? float11 : float11 + float12) / (float12 - float11);
            float14 = (boolean2 ? float11 : float11 + float11) * float12 / (float12 - float11);
        }

        float float17 = this.m00 * float9 + this.m10 * float10 + this.m20 * float13 - this.m30;
        float float18 = this.m01 * float9 + this.m11 * float10 + this.m21 * float13 - this.m31;
        float float19 = this.m02 * float9 + this.m12 * float10 + this.m22 * float13 - this.m32;
        float float20 = this.m03 * float9 + this.m13 * float10 + this.m23 * float13 - this.m33;
        matrix4f1._m00(this.m00 * float2)
            ._m01(this.m01 * float2)
            ._m02(this.m02 * float2)
            ._m03(this.m03 * float2)
            ._m10(this.m10 * float4)
            ._m11(this.m11 * float4)
            ._m12(this.m12 * float4)
            ._m13(this.m13 * float4)
            ._m30(this.m20 * float14)
            ._m31(this.m21 * float14)
            ._m32(this.m22 * float14)
            ._m33(this.m23 * float14)
            ._m20(float17)
            ._m21(float18)
            ._m22(float19)
            ._m23(float20)
            ._properties(this.properties & ~(30 | (float9 == 0.0F && float10 == 0.0F ? 0 : 1)));
        return matrix4f1;
    }

    @Override
    public Matrix4f perspectiveOffCenter(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6) {
        return this.perspectiveOffCenter(arg0, arg1, arg2, arg3, arg4, arg5, false, arg6);
    }

    @Override
    public Matrix4f perspectiveOffCenter(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6) {
        return this.perspectiveOffCenter(arg0, arg1, arg2, arg3, arg4, arg5, arg6, this);
    }

    @Override
    public Matrix4f perspectiveOffCenter(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.perspectiveOffCenter(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    public Matrix4f setPerspective(float arg0, float arg1, float arg2, float arg3, boolean arg4) {
        MemUtil.INSTANCE.zero(this);
        float float0 = Math.tan(arg0 * 0.5F);
        this._m00(1.0F / (float0 * arg1))._m11(1.0F / float0);
        boolean boolean0 = arg3 > 0.0F && Float.isInfinite(arg3);
        boolean boolean1 = arg2 > 0.0F && Float.isInfinite(arg2);
        if (boolean0) {
            float float1 = 1.0E-6F;
            this._m22(float1 - 1.0F)._m32((float1 - (arg4 ? 1.0F : 2.0F)) * arg2);
        } else if (boolean1) {
            float float2 = 1.0E-6F;
            this._m22((arg4 ? 0.0F : 1.0F) - float2)._m32(((arg4 ? 1.0F : 2.0F) - float2) * arg3);
        } else {
            this._m22((arg4 ? arg3 : arg3 + arg2) / (arg2 - arg3))._m32((arg4 ? arg3 : arg3 + arg3) * arg2 / (arg2 - arg3));
        }

        return this._m23(-1.0F)._properties(1);
    }

    public Matrix4f setPerspective(float arg0, float arg1, float arg2, float arg3) {
        return this.setPerspective(arg0, arg1, arg2, arg3, false);
    }

    public Matrix4f setPerspectiveRect(float arg0, float arg1, float arg2, float arg3, boolean arg4) {
        MemUtil.INSTANCE.zero(this);
        this._m00((arg2 + arg2) / arg0)._m11((arg2 + arg2) / arg1);
        boolean boolean0 = arg3 > 0.0F && Float.isInfinite(arg3);
        boolean boolean1 = arg2 > 0.0F && Float.isInfinite(arg2);
        if (boolean0) {
            float float0 = 1.0E-6F;
            this._m22(float0 - 1.0F)._m32((float0 - (arg4 ? 1.0F : 2.0F)) * arg2);
        } else if (boolean1) {
            float float1 = 1.0E-6F;
            this._m22((arg4 ? 0.0F : 1.0F) - float1)._m32(((arg4 ? 1.0F : 2.0F) - float1) * arg3);
        } else {
            this._m22((arg4 ? arg3 : arg3 + arg2) / (arg2 - arg3))._m32((arg4 ? arg3 : arg3 + arg3) * arg2 / (arg2 - arg3));
        }

        this._m23(-1.0F)._properties(1);
        return this;
    }

    public Matrix4f setPerspectiveRect(float arg0, float arg1, float arg2, float arg3) {
        return this.setPerspectiveRect(arg0, arg1, arg2, arg3, false);
    }

    public Matrix4f setPerspectiveOffCenter(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.setPerspectiveOffCenter(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    public Matrix4f setPerspectiveOffCenter(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6) {
        MemUtil.INSTANCE.zero(this);
        float float0 = Math.tan(arg0 * 0.5F);
        float float1 = 1.0F / (float0 * arg3);
        float float2 = 1.0F / float0;
        float float3 = Math.tan(arg1);
        float float4 = Math.tan(arg2);
        this._m00(float1)._m11(float2)._m20(float3 * float1)._m21(float4 * float2);
        boolean boolean0 = arg5 > 0.0F && Float.isInfinite(arg5);
        boolean boolean1 = arg4 > 0.0F && Float.isInfinite(arg4);
        if (boolean0) {
            float float5 = 1.0E-6F;
            this._m22(float5 - 1.0F)._m32((float5 - (arg6 ? 1.0F : 2.0F)) * arg4);
        } else if (boolean1) {
            float float6 = 1.0E-6F;
            this._m22((arg6 ? 0.0F : 1.0F) - float6)._m32(((arg6 ? 1.0F : 2.0F) - float6) * arg5);
        } else {
            this._m22((arg6 ? arg5 : arg5 + arg4) / (arg4 - arg5))._m32((arg6 ? arg5 : arg5 + arg5) * arg4 / (arg4 - arg5));
        }

        this._m23(-1.0F)._properties(arg1 == 0.0F && arg2 == 0.0F ? 1 : 0);
        return this;
    }

    @Override
    public Matrix4f perspectiveLH(float arg0, float arg1, float arg2, float arg3, boolean arg4, Matrix4f arg5) {
        return (this.properties & 4) != 0 ? arg5.setPerspectiveLH(arg0, arg1, arg2, arg3, arg4) : this.perspectiveLHGeneric(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    private Matrix4f perspectiveLHGeneric(float float1, float float3, float float6, float float5, boolean boolean2, Matrix4f matrix4f1) {
        float float0 = Math.tan(float1 * 0.5F);
        float float2 = 1.0F / (float0 * float3);
        float float4 = 1.0F / float0;
        boolean boolean0 = float5 > 0.0F && Float.isInfinite(float5);
        boolean boolean1 = float6 > 0.0F && Float.isInfinite(float6);
        float float7;
        float float8;
        if (boolean0) {
            float float9 = 1.0E-6F;
            float7 = 1.0F - float9;
            float8 = (float9 - (boolean2 ? 1.0F : 2.0F)) * float6;
        } else if (boolean1) {
            float float10 = 1.0E-6F;
            float7 = (boolean2 ? 0.0F : 1.0F) - float10;
            float8 = ((boolean2 ? 1.0F : 2.0F) - float10) * float5;
        } else {
            float7 = (boolean2 ? float5 : float5 + float6) / (float5 - float6);
            float8 = (boolean2 ? float5 : float5 + float5) * float6 / (float6 - float5);
        }

        float float11 = this.m20 * float7 + this.m30;
        float float12 = this.m21 * float7 + this.m31;
        float float13 = this.m22 * float7 + this.m32;
        float float14 = this.m23 * float7 + this.m33;
        matrix4f1._m00(this.m00 * float2)
            ._m01(this.m01 * float2)
            ._m02(this.m02 * float2)
            ._m03(this.m03 * float2)
            ._m10(this.m10 * float4)
            ._m11(this.m11 * float4)
            ._m12(this.m12 * float4)
            ._m13(this.m13 * float4)
            ._m30(this.m20 * float8)
            ._m31(this.m21 * float8)
            ._m32(this.m22 * float8)
            ._m33(this.m23 * float8)
            ._m20(float11)
            ._m21(float12)
            ._m22(float13)
            ._m23(float14)
            ._properties(this.properties & -31);
        return matrix4f1;
    }

    public Matrix4f perspectiveLH(float arg0, float arg1, float arg2, float arg3, boolean arg4) {
        return this.perspectiveLH(arg0, arg1, arg2, arg3, arg4, this);
    }

    @Override
    public Matrix4f perspectiveLH(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4) {
        return this.perspectiveLH(arg0, arg1, arg2, arg3, false, arg4);
    }

    public Matrix4f perspectiveLH(float arg0, float arg1, float arg2, float arg3) {
        return this.perspectiveLH(arg0, arg1, arg2, arg3, this);
    }

    public Matrix4f setPerspectiveLH(float arg0, float arg1, float arg2, float arg3, boolean arg4) {
        MemUtil.INSTANCE.zero(this);
        float float0 = Math.tan(arg0 * 0.5F);
        this._m00(1.0F / (float0 * arg1))._m11(1.0F / float0);
        boolean boolean0 = arg3 > 0.0F && Float.isInfinite(arg3);
        boolean boolean1 = arg2 > 0.0F && Float.isInfinite(arg2);
        if (boolean0) {
            float float1 = 1.0E-6F;
            this._m22(1.0F - float1)._m32((float1 - (arg4 ? 1.0F : 2.0F)) * arg2);
        } else if (boolean1) {
            float float2 = 1.0E-6F;
            this._m22((arg4 ? 0.0F : 1.0F) - float2)._m32(((arg4 ? 1.0F : 2.0F) - float2) * arg3);
        } else {
            this._m22((arg4 ? arg3 : arg3 + arg2) / (arg3 - arg2))._m32((arg4 ? arg3 : arg3 + arg3) * arg2 / (arg2 - arg3));
        }

        this._m23(1.0F)._properties(1);
        return this;
    }

    public Matrix4f setPerspectiveLH(float arg0, float arg1, float arg2, float arg3) {
        return this.setPerspectiveLH(arg0, arg1, arg2, arg3, false);
    }

    @Override
    public Matrix4f frustum(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6, Matrix4f arg7) {
        return (this.properties & 4) != 0
            ? arg7.setFrustum(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
            : this.frustumGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }

    private Matrix4f frustumGeneric(float float2, float float1, float float6, float float5, float float3, float float9, boolean boolean2, Matrix4f matrix4f1) {
        float float0 = (float3 + float3) / (float1 - float2);
        float float4 = (float3 + float3) / (float5 - float6);
        float float7 = (float1 + float2) / (float1 - float2);
        float float8 = (float5 + float6) / (float5 - float6);
        boolean boolean0 = float9 > 0.0F && Float.isInfinite(float9);
        boolean boolean1 = float3 > 0.0F && Float.isInfinite(float3);
        float float10;
        float float11;
        if (boolean0) {
            float float12 = 1.0E-6F;
            float10 = float12 - 1.0F;
            float11 = (float12 - (boolean2 ? 1.0F : 2.0F)) * float3;
        } else if (boolean1) {
            float float13 = 1.0E-6F;
            float10 = (boolean2 ? 0.0F : 1.0F) - float13;
            float11 = ((boolean2 ? 1.0F : 2.0F) - float13) * float9;
        } else {
            float10 = (boolean2 ? float9 : float9 + float3) / (float3 - float9);
            float11 = (boolean2 ? float9 : float9 + float9) * float3 / (float3 - float9);
        }

        float float14 = this.m00 * float7 + this.m10 * float8 + this.m20 * float10 - this.m30;
        float float15 = this.m01 * float7 + this.m11 * float8 + this.m21 * float10 - this.m31;
        float float16 = this.m02 * float7 + this.m12 * float8 + this.m22 * float10 - this.m32;
        float float17 = this.m03 * float7 + this.m13 * float8 + this.m23 * float10 - this.m33;
        matrix4f1._m00(this.m00 * float0)
            ._m01(this.m01 * float0)
            ._m02(this.m02 * float0)
            ._m03(this.m03 * float0)
            ._m10(this.m10 * float4)
            ._m11(this.m11 * float4)
            ._m12(this.m12 * float4)
            ._m13(this.m13 * float4)
            ._m30(this.m20 * float11)
            ._m31(this.m21 * float11)
            ._m32(this.m22 * float11)
            ._m33(this.m23 * float11)
            ._m20(float14)
            ._m21(float15)
            ._m22(float16)
            ._m23(float17)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(0);
        return matrix4f1;
    }

    @Override
    public Matrix4f frustum(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6) {
        return this.frustum(arg0, arg1, arg2, arg3, arg4, arg5, false, arg6);
    }

    public Matrix4f frustum(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6) {
        return this.frustum(arg0, arg1, arg2, arg3, arg4, arg5, arg6, this);
    }

    public Matrix4f frustum(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.frustum(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    public Matrix4f setFrustum(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6) {
        if ((this.properties & 4) == 0) {
            MemUtil.INSTANCE.identity(this);
        }

        this._m00((arg4 + arg4) / (arg1 - arg0))._m11((arg4 + arg4) / (arg3 - arg2))._m20((arg1 + arg0) / (arg1 - arg0))._m21((arg3 + arg2) / (arg3 - arg2));
        boolean boolean0 = arg5 > 0.0F && Float.isInfinite(arg5);
        boolean boolean1 = arg4 > 0.0F && Float.isInfinite(arg4);
        if (boolean0) {
            float float0 = 1.0E-6F;
            this._m22(float0 - 1.0F)._m32((float0 - (arg6 ? 1.0F : 2.0F)) * arg4);
        } else if (boolean1) {
            float float1 = 1.0E-6F;
            this._m22((arg6 ? 0.0F : 1.0F) - float1)._m32(((arg6 ? 1.0F : 2.0F) - float1) * arg5);
        } else {
            this._m22((arg6 ? arg5 : arg5 + arg4) / (arg4 - arg5))._m32((arg6 ? arg5 : arg5 + arg5) * arg4 / (arg4 - arg5));
        }

        this._m23(-1.0F)._m33(0.0F)._properties(this.m20 == 0.0F && this.m21 == 0.0F ? 1 : 0);
        return this;
    }

    public Matrix4f setFrustum(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.setFrustum(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    @Override
    public Matrix4f frustumLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6, Matrix4f arg7) {
        return (this.properties & 4) != 0
            ? arg7.setFrustumLH(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
            : this.frustumLHGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }

    private Matrix4f frustumLHGeneric(float float2, float float1, float float6, float float5, float float3, float float9, boolean boolean2, Matrix4f matrix4f1) {
        float float0 = (float3 + float3) / (float1 - float2);
        float float4 = (float3 + float3) / (float5 - float6);
        float float7 = (float1 + float2) / (float1 - float2);
        float float8 = (float5 + float6) / (float5 - float6);
        boolean boolean0 = float9 > 0.0F && Float.isInfinite(float9);
        boolean boolean1 = float3 > 0.0F && Float.isInfinite(float3);
        float float10;
        float float11;
        if (boolean0) {
            float float12 = 1.0E-6F;
            float10 = 1.0F - float12;
            float11 = (float12 - (boolean2 ? 1.0F : 2.0F)) * float3;
        } else if (boolean1) {
            float float13 = 1.0E-6F;
            float10 = (boolean2 ? 0.0F : 1.0F) - float13;
            float11 = ((boolean2 ? 1.0F : 2.0F) - float13) * float9;
        } else {
            float10 = (boolean2 ? float9 : float9 + float3) / (float9 - float3);
            float11 = (boolean2 ? float9 : float9 + float9) * float3 / (float3 - float9);
        }

        float float14 = this.m00 * float7 + this.m10 * float8 + this.m20 * float10 + this.m30;
        float float15 = this.m01 * float7 + this.m11 * float8 + this.m21 * float10 + this.m31;
        float float16 = this.m02 * float7 + this.m12 * float8 + this.m22 * float10 + this.m32;
        float float17 = this.m03 * float7 + this.m13 * float8 + this.m23 * float10 + this.m33;
        matrix4f1._m00(this.m00 * float0)
            ._m01(this.m01 * float0)
            ._m02(this.m02 * float0)
            ._m03(this.m03 * float0)
            ._m10(this.m10 * float4)
            ._m11(this.m11 * float4)
            ._m12(this.m12 * float4)
            ._m13(this.m13 * float4)
            ._m30(this.m20 * float11)
            ._m31(this.m21 * float11)
            ._m32(this.m22 * float11)
            ._m33(this.m23 * float11)
            ._m20(float14)
            ._m21(float15)
            ._m22(float16)
            ._m23(float17)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(0);
        return matrix4f1;
    }

    public Matrix4f frustumLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6) {
        return this.frustumLH(arg0, arg1, arg2, arg3, arg4, arg5, arg6, this);
    }

    @Override
    public Matrix4f frustumLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6) {
        return this.frustumLH(arg0, arg1, arg2, arg3, arg4, arg5, false, arg6);
    }

    public Matrix4f frustumLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.frustumLH(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    public Matrix4f setFrustumLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6) {
        if ((this.properties & 4) == 0) {
            MemUtil.INSTANCE.identity(this);
        }

        this._m00((arg4 + arg4) / (arg1 - arg0))._m11((arg4 + arg4) / (arg3 - arg2))._m20((arg1 + arg0) / (arg1 - arg0))._m21((arg3 + arg2) / (arg3 - arg2));
        boolean boolean0 = arg5 > 0.0F && Float.isInfinite(arg5);
        boolean boolean1 = arg4 > 0.0F && Float.isInfinite(arg4);
        if (boolean0) {
            float float0 = 1.0E-6F;
            this._m22(1.0F - float0)._m32((float0 - (arg6 ? 1.0F : 2.0F)) * arg4);
        } else if (boolean1) {
            float float1 = 1.0E-6F;
            this._m22((arg6 ? 0.0F : 1.0F) - float1)._m32(((arg6 ? 1.0F : 2.0F) - float1) * arg5);
        } else {
            this._m22((arg6 ? arg5 : arg5 + arg4) / (arg5 - arg4))._m32((arg6 ? arg5 : arg5 + arg5) * arg4 / (arg4 - arg5));
        }

        return this._m23(1.0F)._m33(0.0F)._properties(0);
    }

    public Matrix4f setFrustumLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.setFrustumLH(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    public Matrix4f setFromIntrinsic(float arg0, float arg1, float arg2, float arg3, float arg4, int arg5, int arg6, float arg7, float arg8) {
        float float0 = 2.0F / arg5;
        float float1 = 2.0F / arg6;
        float float2 = 2.0F / (arg7 - arg8);
        this.m00 = float0 * arg0;
        this.m01 = 0.0F;
        this.m02 = 0.0F;
        this.m03 = 0.0F;
        this.m10 = float0 * arg2;
        this.m11 = float1 * arg1;
        this.m12 = 0.0F;
        this.m13 = 0.0F;
        this.m20 = float0 * arg3 - 1.0F;
        this.m21 = float1 * arg4 - 1.0F;
        this.m22 = float2 * -(arg7 + arg8) + (arg8 + arg7) / (arg7 - arg8);
        this.m23 = -1.0F;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = float2 * -arg7 * arg8;
        this.m33 = 0.0F;
        this.properties = 1;
        return this;
    }

    @Override
    public Matrix4f rotate(Quaternionfc arg0, Matrix4f arg1) {
        if ((this.properties & 4) != 0) {
            return arg1.rotation(arg0);
        } else if ((this.properties & 8) != 0) {
            return this.rotateTranslation(arg0, arg1);
        } else {
            return (this.properties & 2) != 0 ? this.rotateAffine(arg0, arg1) : this.rotateGeneric(arg0, arg1);
        }
    }

    private Matrix4f rotateGeneric(Quaternionfc quaternionfc, Matrix4f matrix4f1) {
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
        float float19 = -float5 + float7;
        float float20 = float2 - float3 + float0 - float1;
        float float21 = float13 + float15;
        float float22 = float11 + float9;
        float float23 = float13 - float15;
        float float24 = float3 - float2 - float1 + float0;
        float float25 = this.m00 * float16 + this.m10 * float17 + this.m20 * float18;
        float float26 = this.m01 * float16 + this.m11 * float17 + this.m21 * float18;
        float float27 = this.m02 * float16 + this.m12 * float17 + this.m22 * float18;
        float float28 = this.m03 * float16 + this.m13 * float17 + this.m23 * float18;
        float float29 = this.m00 * float19 + this.m10 * float20 + this.m20 * float21;
        float float30 = this.m01 * float19 + this.m11 * float20 + this.m21 * float21;
        float float31 = this.m02 * float19 + this.m12 * float20 + this.m22 * float21;
        float float32 = this.m03 * float19 + this.m13 * float20 + this.m23 * float21;
        return matrix4f1._m20(this.m00 * float22 + this.m10 * float23 + this.m20 * float24)
            ._m21(this.m01 * float22 + this.m11 * float23 + this.m21 * float24)
            ._m22(this.m02 * float22 + this.m12 * float23 + this.m22 * float24)
            ._m23(this.m03 * float22 + this.m13 * float23 + this.m23 * float24)
            ._m00(float25)
            ._m01(float26)
            ._m02(float27)
            ._m03(float28)
            ._m10(float29)
            ._m11(float30)
            ._m12(float31)
            ._m13(float32)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
    }

    public Matrix4f rotate(Quaternionfc arg0) {
        return this.rotate(arg0, this);
    }

    @Override
    public Matrix4f rotateAffine(Quaternionfc arg0, Matrix4f arg1) {
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
        float float19 = -float5 + float7;
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
        return arg1._m20(this.m00 * float22 + this.m10 * float23 + this.m20 * float24)
            ._m21(this.m01 * float22 + this.m11 * float23 + this.m21 * float24)
            ._m22(this.m02 * float22 + this.m12 * float23 + this.m22 * float24)
            ._m23(0.0F)
            ._m00(float25)
            ._m01(float26)
            ._m02(float27)
            ._m03(0.0F)
            ._m10(float28)
            ._m11(float29)
            ._m12(float30)
            ._m13(0.0F)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
    }

    public Matrix4f rotateAffine(Quaternionfc arg0) {
        return this.rotateAffine(arg0, this);
    }

    @Override
    public Matrix4f rotateTranslation(Quaternionfc arg0, Matrix4f arg1) {
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
        float float19 = -float5 + float7;
        float float20 = float2 - float3 + float0 - float1;
        float float21 = float13 + float15;
        float float22 = float11 + float9;
        float float23 = float13 - float15;
        float float24 = float3 - float2 - float1 + float0;
        return arg1._m20(float22)
            ._m21(float23)
            ._m22(float24)
            ._m23(0.0F)
            ._m00(float16)
            ._m01(float17)
            ._m02(float18)
            ._m03(0.0F)
            ._m10(float19)
            ._m11(float20)
            ._m12(float21)
            ._m13(0.0F)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
    }

    public Matrix4f rotateAround(Quaternionfc arg0, float arg1, float arg2, float arg3) {
        return this.rotateAround(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix4f rotateAroundAffine(Quaternionfc arg0, float arg1, float arg2, float arg3, Matrix4f arg4) {
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
        float float19 = -float5 + float7;
        float float20 = float2 - float3 + float0 - float1;
        float float21 = float13 + float15;
        float float22 = float11 + float9;
        float float23 = float13 - float15;
        float float24 = float3 - float2 - float1 + float0;
        float float25 = this.m00 * arg1 + this.m10 * arg2 + this.m20 * arg3 + this.m30;
        float float26 = this.m01 * arg1 + this.m11 * arg2 + this.m21 * arg3 + this.m31;
        float float27 = this.m02 * arg1 + this.m12 * arg2 + this.m22 * arg3 + this.m32;
        float float28 = this.m00 * float16 + this.m10 * float17 + this.m20 * float18;
        float float29 = this.m01 * float16 + this.m11 * float17 + this.m21 * float18;
        float float30 = this.m02 * float16 + this.m12 * float17 + this.m22 * float18;
        float float31 = this.m00 * float19 + this.m10 * float20 + this.m20 * float21;
        float float32 = this.m01 * float19 + this.m11 * float20 + this.m21 * float21;
        float float33 = this.m02 * float19 + this.m12 * float20 + this.m22 * float21;
        arg4._m20(this.m00 * float22 + this.m10 * float23 + this.m20 * float24)
            ._m21(this.m01 * float22 + this.m11 * float23 + this.m21 * float24)
            ._m22(this.m02 * float22 + this.m12 * float23 + this.m22 * float24)
            ._m23(0.0F)
            ._m00(float28)
            ._m01(float29)
            ._m02(float30)
            ._m03(0.0F)
            ._m10(float31)
            ._m11(float32)
            ._m12(float33)
            ._m13(0.0F)
            ._m30(-float28 * arg1 - float31 * arg2 - this.m20 * arg3 + float25)
            ._m31(-float29 * arg1 - float32 * arg2 - this.m21 * arg3 + float26)
            ._m32(-float30 * arg1 - float33 * arg2 - this.m22 * arg3 + float27)
            ._m33(1.0F)
            ._properties(this.properties & -14);
        return arg4;
    }

    @Override
    public Matrix4f rotateAround(Quaternionfc arg0, float arg1, float arg2, float arg3, Matrix4f arg4) {
        if ((this.properties & 4) != 0) {
            return this.rotationAround(arg0, arg1, arg2, arg3);
        } else {
            return (this.properties & 2) != 0 ? this.rotateAroundAffine(arg0, arg1, arg2, arg3, arg4) : this.rotateAroundGeneric(arg0, arg1, arg2, arg3, arg4);
        }
    }

    private Matrix4f rotateAroundGeneric(Quaternionfc quaternionfc, float float28, float float27, float float26, Matrix4f matrix4f1) {
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
        float float19 = -float5 + float7;
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
        float float34 = this.m03 * float16 + this.m13 * float17 + this.m23 * float18;
        float float35 = this.m00 * float19 + this.m10 * float20 + this.m20 * float21;
        float float36 = this.m01 * float19 + this.m11 * float20 + this.m21 * float21;
        float float37 = this.m02 * float19 + this.m12 * float20 + this.m22 * float21;
        float float38 = this.m03 * float19 + this.m13 * float20 + this.m23 * float21;
        matrix4f1._m20(this.m00 * float22 + this.m10 * float23 + this.m20 * float24)
            ._m21(this.m01 * float22 + this.m11 * float23 + this.m21 * float24)
            ._m22(this.m02 * float22 + this.m12 * float23 + this.m22 * float24)
            ._m23(this.m03 * float22 + this.m13 * float23 + this.m23 * float24)
            ._m00(float31)
            ._m01(float32)
            ._m02(float33)
            ._m03(float34)
            ._m10(float35)
            ._m11(float36)
            ._m12(float37)
            ._m13(float38)
            ._m30(-float31 * float28 - float35 * float27 - this.m20 * float26 + float25)
            ._m31(-float32 * float28 - float36 * float27 - this.m21 * float26 + float29)
            ._m32(-float33 * float28 - float37 * float27 - this.m22 * float26 + float30)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return matrix4f1;
    }

    public Matrix4f rotationAround(Quaternionfc arg0, float arg1, float arg2, float arg3) {
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
        this._m20(float11 + float9)
            ._m21(float13 - float15)
            ._m22(float3 - float2 - float1 + float0)
            ._m23(0.0F)
            ._m00(float0 + float1 - float3 - float2)
            ._m01(float7 + float5)
            ._m02(float9 - float11)
            ._m03(0.0F)
            ._m10(-float5 + float7)
            ._m11(float2 - float3 + float0 - float1)
            ._m12(float13 + float15)
            ._m13(0.0F)
            ._m30(-this.m00 * arg1 - this.m10 * arg2 - this.m20 * arg3 + arg1)
            ._m31(-this.m01 * arg1 - this.m11 * arg2 - this.m21 * arg3 + arg2)
            ._m32(-this.m02 * arg1 - this.m12 * arg2 - this.m22 * arg3 + arg3)
            ._m33(1.0F)
            ._properties(18);
        return this;
    }

    @Override
    public Matrix4f rotateLocal(Quaternionfc arg0, Matrix4f arg1) {
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
        float float19 = -float5 + float7;
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
        float float34 = float16 * this.m30 + float19 * this.m31 + float22 * this.m32;
        float float35 = float17 * this.m30 + float20 * this.m31 + float23 * this.m32;
        float float36 = float18 * this.m30 + float21 * this.m31 + float24 * this.m32;
        return arg1._m00(float25)
            ._m01(float26)
            ._m02(float27)
            ._m03(this.m03)
            ._m10(float28)
            ._m11(float29)
            ._m12(float30)
            ._m13(this.m13)
            ._m20(float31)
            ._m21(float32)
            ._m22(float33)
            ._m23(this.m23)
            ._m30(float34)
            ._m31(float35)
            ._m32(float36)
            ._m33(this.m33)
            ._properties(this.properties & -14);
    }

    public Matrix4f rotateLocal(Quaternionfc arg0) {
        return this.rotateLocal(arg0, this);
    }

    @Override
    public Matrix4f rotateAroundLocal(Quaternionfc arg0, float arg1, float arg2, float arg3, Matrix4f arg4) {
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
        float float19 = this.m00 - arg1 * this.m03;
        float float20 = this.m01 - arg2 * this.m03;
        float float21 = this.m02 - arg3 * this.m03;
        float float22 = this.m10 - arg1 * this.m13;
        float float23 = this.m11 - arg2 * this.m13;
        float float24 = this.m12 - arg3 * this.m13;
        float float25 = this.m20 - arg1 * this.m23;
        float float26 = this.m21 - arg2 * this.m23;
        float float27 = this.m22 - arg3 * this.m23;
        float float28 = this.m30 - arg1 * this.m33;
        float float29 = this.m31 - arg2 * this.m33;
        float float30 = this.m32 - arg3 * this.m33;
        arg4._m00(float10 * float19 + float13 * float20 + float16 * float21 + arg1 * this.m03)
            ._m01(float11 * float19 + float14 * float20 + float17 * float21 + arg2 * this.m03)
            ._m02(float12 * float19 + float15 * float20 + float18 * float21 + arg3 * this.m03)
            ._m03(this.m03)
            ._m10(float10 * float22 + float13 * float23 + float16 * float24 + arg1 * this.m13)
            ._m11(float11 * float22 + float14 * float23 + float17 * float24 + arg2 * this.m13)
            ._m12(float12 * float22 + float15 * float23 + float18 * float24 + arg3 * this.m13)
            ._m13(this.m13)
            ._m20(float10 * float25 + float13 * float26 + float16 * float27 + arg1 * this.m23)
            ._m21(float11 * float25 + float14 * float26 + float17 * float27 + arg2 * this.m23)
            ._m22(float12 * float25 + float15 * float26 + float18 * float27 + arg3 * this.m23)
            ._m23(this.m23)
            ._m30(float10 * float28 + float13 * float29 + float16 * float30 + arg1 * this.m33)
            ._m31(float11 * float28 + float14 * float29 + float17 * float30 + arg2 * this.m33)
            ._m32(float12 * float28 + float15 * float29 + float18 * float30 + arg3 * this.m33)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return arg4;
    }

    public Matrix4f rotateAroundLocal(Quaternionfc arg0, float arg1, float arg2, float arg3) {
        return this.rotateAroundLocal(arg0, arg1, arg2, arg3, this);
    }

    public Matrix4f rotate(AxisAngle4f arg0) {
        return this.rotate(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    @Override
    public Matrix4f rotate(AxisAngle4f arg0, Matrix4f arg1) {
        return this.rotate(arg0.angle, arg0.x, arg0.y, arg0.z, arg1);
    }

    public Matrix4f rotate(float arg0, Vector3fc arg1) {
        return this.rotate(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    @Override
    public Matrix4f rotate(float arg0, Vector3fc arg1, Matrix4f arg2) {
        return this.rotate(arg0, arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    @Override
    public Vector4f unproject(float float30, float float32, float float34, int[] ints, Vector4f vector4f) {
        float float0 = this.m00 * this.m11 - this.m01 * this.m10;
        float float1 = this.m00 * this.m12 - this.m02 * this.m10;
        float float2 = this.m00 * this.m13 - this.m03 * this.m10;
        float float3 = this.m01 * this.m12 - this.m02 * this.m11;
        float float4 = this.m01 * this.m13 - this.m03 * this.m11;
        float float5 = this.m02 * this.m13 - this.m03 * this.m12;
        float float6 = this.m20 * this.m31 - this.m21 * this.m30;
        float float7 = this.m20 * this.m32 - this.m22 * this.m30;
        float float8 = this.m20 * this.m33 - this.m23 * this.m30;
        float float9 = this.m21 * this.m32 - this.m22 * this.m31;
        float float10 = this.m21 * this.m33 - this.m23 * this.m31;
        float float11 = this.m22 * this.m33 - this.m23 * this.m32;
        float float12 = float0 * float11 - float1 * float10 + float2 * float9 + float3 * float8 - float4 * float7 + float5 * float6;
        float12 = 1.0F / float12;
        float float13 = (this.m11 * float11 - this.m12 * float10 + this.m13 * float9) * float12;
        float float14 = (-this.m01 * float11 + this.m02 * float10 - this.m03 * float9) * float12;
        float float15 = (this.m31 * float5 - this.m32 * float4 + this.m33 * float3) * float12;
        float float16 = (-this.m21 * float5 + this.m22 * float4 - this.m23 * float3) * float12;
        float float17 = (-this.m10 * float11 + this.m12 * float8 - this.m13 * float7) * float12;
        float float18 = (this.m00 * float11 - this.m02 * float8 + this.m03 * float7) * float12;
        float float19 = (-this.m30 * float5 + this.m32 * float2 - this.m33 * float1) * float12;
        float float20 = (this.m20 * float5 - this.m22 * float2 + this.m23 * float1) * float12;
        float float21 = (this.m10 * float10 - this.m11 * float8 + this.m13 * float6) * float12;
        float float22 = (-this.m00 * float10 + this.m01 * float8 - this.m03 * float6) * float12;
        float float23 = (this.m30 * float4 - this.m31 * float2 + this.m33 * float0) * float12;
        float float24 = (-this.m20 * float4 + this.m21 * float2 - this.m23 * float0) * float12;
        float float25 = (-this.m10 * float9 + this.m11 * float7 - this.m12 * float6) * float12;
        float float26 = (this.m00 * float9 - this.m01 * float7 + this.m02 * float6) * float12;
        float float27 = (-this.m30 * float3 + this.m31 * float1 - this.m32 * float0) * float12;
        float float28 = (this.m20 * float3 - this.m21 * float1 + this.m22 * float0) * float12;
        float float29 = (float30 - ints[0]) / ints[2] * 2.0F - 1.0F;
        float float31 = (float32 - ints[1]) / ints[3] * 2.0F - 1.0F;
        float float33 = float34 + float34 - 1.0F;
        float float35 = 1.0F / (float16 * float29 + float20 * float31 + float24 * float33 + float28);
        return vector4f.set(
            (float13 * float29 + float17 * float31 + float21 * float33 + float25) * float35,
            (float14 * float29 + float18 * float31 + float22 * float33 + float26) * float35,
            (float15 * float29 + float19 * float31 + float23 * float33 + float27) * float35,
            1.0F
        );
    }

    @Override
    public Vector3f unproject(float float30, float float32, float float34, int[] ints, Vector3f vector3f) {
        float float0 = this.m00 * this.m11 - this.m01 * this.m10;
        float float1 = this.m00 * this.m12 - this.m02 * this.m10;
        float float2 = this.m00 * this.m13 - this.m03 * this.m10;
        float float3 = this.m01 * this.m12 - this.m02 * this.m11;
        float float4 = this.m01 * this.m13 - this.m03 * this.m11;
        float float5 = this.m02 * this.m13 - this.m03 * this.m12;
        float float6 = this.m20 * this.m31 - this.m21 * this.m30;
        float float7 = this.m20 * this.m32 - this.m22 * this.m30;
        float float8 = this.m20 * this.m33 - this.m23 * this.m30;
        float float9 = this.m21 * this.m32 - this.m22 * this.m31;
        float float10 = this.m21 * this.m33 - this.m23 * this.m31;
        float float11 = this.m22 * this.m33 - this.m23 * this.m32;
        float float12 = float0 * float11 - float1 * float10 + float2 * float9 + float3 * float8 - float4 * float7 + float5 * float6;
        float12 = 1.0F / float12;
        float float13 = (this.m11 * float11 - this.m12 * float10 + this.m13 * float9) * float12;
        float float14 = (-this.m01 * float11 + this.m02 * float10 - this.m03 * float9) * float12;
        float float15 = (this.m31 * float5 - this.m32 * float4 + this.m33 * float3) * float12;
        float float16 = (-this.m21 * float5 + this.m22 * float4 - this.m23 * float3) * float12;
        float float17 = (-this.m10 * float11 + this.m12 * float8 - this.m13 * float7) * float12;
        float float18 = (this.m00 * float11 - this.m02 * float8 + this.m03 * float7) * float12;
        float float19 = (-this.m30 * float5 + this.m32 * float2 - this.m33 * float1) * float12;
        float float20 = (this.m20 * float5 - this.m22 * float2 + this.m23 * float1) * float12;
        float float21 = (this.m10 * float10 - this.m11 * float8 + this.m13 * float6) * float12;
        float float22 = (-this.m00 * float10 + this.m01 * float8 - this.m03 * float6) * float12;
        float float23 = (this.m30 * float4 - this.m31 * float2 + this.m33 * float0) * float12;
        float float24 = (-this.m20 * float4 + this.m21 * float2 - this.m23 * float0) * float12;
        float float25 = (-this.m10 * float9 + this.m11 * float7 - this.m12 * float6) * float12;
        float float26 = (this.m00 * float9 - this.m01 * float7 + this.m02 * float6) * float12;
        float float27 = (-this.m30 * float3 + this.m31 * float1 - this.m32 * float0) * float12;
        float float28 = (this.m20 * float3 - this.m21 * float1 + this.m22 * float0) * float12;
        float float29 = (float30 - ints[0]) / ints[2] * 2.0F - 1.0F;
        float float31 = (float32 - ints[1]) / ints[3] * 2.0F - 1.0F;
        float float33 = float34 + float34 - 1.0F;
        float float35 = 1.0F / (float16 * float29 + float20 * float31 + float24 * float33 + float28);
        return vector3f.set(
            (float13 * float29 + float17 * float31 + float21 * float33 + float25) * float35,
            (float14 * float29 + float18 * float31 + float22 * float33 + float26) * float35,
            (float15 * float29 + float19 * float31 + float23 * float33 + float27) * float35
        );
    }

    @Override
    public Vector4f unproject(Vector3fc vector3fc, int[] ints, Vector4f vector4f) {
        return this.unproject(vector3fc.x(), vector3fc.y(), vector3fc.z(), ints, vector4f);
    }

    @Override
    public Vector3f unproject(Vector3fc vector3fc, int[] ints, Vector3f vector3f) {
        return this.unproject(vector3fc.x(), vector3fc.y(), vector3fc.z(), ints, vector3f);
    }

    @Override
    public Matrix4f unprojectRay(float float30, float float32, int[] ints, Vector3f vector3f0, Vector3f vector3f1) {
        float float0 = this.m00 * this.m11 - this.m01 * this.m10;
        float float1 = this.m00 * this.m12 - this.m02 * this.m10;
        float float2 = this.m00 * this.m13 - this.m03 * this.m10;
        float float3 = this.m01 * this.m12 - this.m02 * this.m11;
        float float4 = this.m01 * this.m13 - this.m03 * this.m11;
        float float5 = this.m02 * this.m13 - this.m03 * this.m12;
        float float6 = this.m20 * this.m31 - this.m21 * this.m30;
        float float7 = this.m20 * this.m32 - this.m22 * this.m30;
        float float8 = this.m20 * this.m33 - this.m23 * this.m30;
        float float9 = this.m21 * this.m32 - this.m22 * this.m31;
        float float10 = this.m21 * this.m33 - this.m23 * this.m31;
        float float11 = this.m22 * this.m33 - this.m23 * this.m32;
        float float12 = float0 * float11 - float1 * float10 + float2 * float9 + float3 * float8 - float4 * float7 + float5 * float6;
        float12 = 1.0F / float12;
        float float13 = (this.m11 * float11 - this.m12 * float10 + this.m13 * float9) * float12;
        float float14 = (-this.m01 * float11 + this.m02 * float10 - this.m03 * float9) * float12;
        float float15 = (this.m31 * float5 - this.m32 * float4 + this.m33 * float3) * float12;
        float float16 = (-this.m21 * float5 + this.m22 * float4 - this.m23 * float3) * float12;
        float float17 = (-this.m10 * float11 + this.m12 * float8 - this.m13 * float7) * float12;
        float float18 = (this.m00 * float11 - this.m02 * float8 + this.m03 * float7) * float12;
        float float19 = (-this.m30 * float5 + this.m32 * float2 - this.m33 * float1) * float12;
        float float20 = (this.m20 * float5 - this.m22 * float2 + this.m23 * float1) * float12;
        float float21 = (this.m10 * float10 - this.m11 * float8 + this.m13 * float6) * float12;
        float float22 = (-this.m00 * float10 + this.m01 * float8 - this.m03 * float6) * float12;
        float float23 = (this.m30 * float4 - this.m31 * float2 + this.m33 * float0) * float12;
        float float24 = (-this.m20 * float4 + this.m21 * float2 - this.m23 * float0) * float12;
        float float25 = (-this.m10 * float9 + this.m11 * float7 - this.m12 * float6) * float12;
        float float26 = (this.m00 * float9 - this.m01 * float7 + this.m02 * float6) * float12;
        float float27 = (-this.m30 * float3 + this.m31 * float1 - this.m32 * float0) * float12;
        float float28 = (this.m20 * float3 - this.m21 * float1 + this.m22 * float0) * float12;
        float float29 = (float30 - ints[0]) / ints[2] * 2.0F - 1.0F;
        float float31 = (float32 - ints[1]) / ints[3] * 2.0F - 1.0F;
        float float33 = float13 * float29 + float17 * float31 + float25;
        float float34 = float14 * float29 + float18 * float31 + float26;
        float float35 = float15 * float29 + float19 * float31 + float27;
        float float36 = 1.0F / (float16 * float29 + float20 * float31 - float24 + float28);
        float float37 = (float33 - float21) * float36;
        float float38 = (float34 - float22) * float36;
        float float39 = (float35 - float23) * float36;
        float float40 = 1.0F / (float16 * float29 + float20 * float31 + float28);
        float float41 = float33 * float40;
        float float42 = float34 * float40;
        float float43 = float35 * float40;
        vector3f0.x = float37;
        vector3f0.y = float38;
        vector3f0.z = float39;
        vector3f1.x = float41 - float37;
        vector3f1.y = float42 - float38;
        vector3f1.z = float43 - float39;
        return this;
    }

    @Override
    public Matrix4f unprojectRay(Vector2fc vector2fc, int[] ints, Vector3f vector3f0, Vector3f vector3f1) {
        return this.unprojectRay(vector2fc.x(), vector2fc.y(), ints, vector3f0, vector3f1);
    }

    @Override
    public Vector4f unprojectInv(Vector3fc vector3fc, int[] ints, Vector4f vector4f) {
        return this.unprojectInv(vector3fc.x(), vector3fc.y(), vector3fc.z(), ints, vector4f);
    }

    @Override
    public Vector4f unprojectInv(float float1, float float3, float float5, int[] ints, Vector4f vector4f) {
        float float0 = (float1 - ints[0]) / ints[2] * 2.0F - 1.0F;
        float float2 = (float3 - ints[1]) / ints[3] * 2.0F - 1.0F;
        float float4 = float5 + float5 - 1.0F;
        float float6 = 1.0F / (this.m03 * float0 + this.m13 * float2 + this.m23 * float4 + this.m33);
        return vector4f.set(
            (this.m00 * float0 + this.m10 * float2 + this.m20 * float4 + this.m30) * float6,
            (this.m01 * float0 + this.m11 * float2 + this.m21 * float4 + this.m31) * float6,
            (this.m02 * float0 + this.m12 * float2 + this.m22 * float4 + this.m32) * float6,
            1.0F
        );
    }

    @Override
    public Matrix4f unprojectInvRay(Vector2fc vector2fc, int[] ints, Vector3f vector3f0, Vector3f vector3f1) {
        return this.unprojectInvRay(vector2fc.x(), vector2fc.y(), ints, vector3f0, vector3f1);
    }

    @Override
    public Matrix4f unprojectInvRay(float float1, float float3, int[] ints, Vector3f vector3f0, Vector3f vector3f1) {
        float float0 = (float1 - ints[0]) / ints[2] * 2.0F - 1.0F;
        float float2 = (float3 - ints[1]) / ints[3] * 2.0F - 1.0F;
        float float4 = this.m00 * float0 + this.m10 * float2 + this.m30;
        float float5 = this.m01 * float0 + this.m11 * float2 + this.m31;
        float float6 = this.m02 * float0 + this.m12 * float2 + this.m32;
        float float7 = 1.0F / (this.m03 * float0 + this.m13 * float2 - this.m23 + this.m33);
        float float8 = (float4 - this.m20) * float7;
        float float9 = (float5 - this.m21) * float7;
        float float10 = (float6 - this.m22) * float7;
        float float11 = 1.0F / (this.m03 * float0 + this.m13 * float2 + this.m33);
        float float12 = float4 * float11;
        float float13 = float5 * float11;
        float float14 = float6 * float11;
        vector3f0.x = float8;
        vector3f0.y = float9;
        vector3f0.z = float10;
        vector3f1.x = float12 - float8;
        vector3f1.y = float13 - float9;
        vector3f1.z = float14 - float10;
        return this;
    }

    @Override
    public Vector3f unprojectInv(Vector3fc vector3fc, int[] ints, Vector3f vector3f) {
        return this.unprojectInv(vector3fc.x(), vector3fc.y(), vector3fc.z(), ints, vector3f);
    }

    @Override
    public Vector3f unprojectInv(float float1, float float3, float float5, int[] ints, Vector3f vector3f) {
        float float0 = (float1 - ints[0]) / ints[2] * 2.0F - 1.0F;
        float float2 = (float3 - ints[1]) / ints[3] * 2.0F - 1.0F;
        float float4 = float5 + float5 - 1.0F;
        float float6 = 1.0F / (this.m03 * float0 + this.m13 * float2 + this.m23 * float4 + this.m33);
        return vector3f.set(
            (this.m00 * float0 + this.m10 * float2 + this.m20 * float4 + this.m30) * float6,
            (this.m01 * float0 + this.m11 * float2 + this.m21 * float4 + this.m31) * float6,
            (this.m02 * float0 + this.m12 * float2 + this.m22 * float4 + this.m32) * float6
        );
    }

    @Override
    public Vector4f project(float float3, float float2, float float1, int[] ints, Vector4f vector4f) {
        float float0 = 1.0F / (this.m03 * float3 + this.m13 * float2 + this.m23 * float1 + this.m33);
        float float4 = (this.m00 * float3 + this.m10 * float2 + this.m20 * float1 + this.m30) * float0;
        float float5 = (this.m01 * float3 + this.m11 * float2 + this.m21 * float1 + this.m31) * float0;
        float float6 = (this.m02 * float3 + this.m12 * float2 + this.m22 * float1 + this.m32) * float0;
        return vector4f.set((float4 * 0.5F + 0.5F) * ints[2] + ints[0], (float5 * 0.5F + 0.5F) * ints[3] + ints[1], (1.0F + float6) * 0.5F, 1.0F);
    }

    @Override
    public Vector3f project(float float3, float float2, float float1, int[] ints, Vector3f vector3f) {
        float float0 = 1.0F / (this.m03 * float3 + this.m13 * float2 + this.m23 * float1 + this.m33);
        float float4 = (this.m00 * float3 + this.m10 * float2 + this.m20 * float1 + this.m30) * float0;
        float float5 = (this.m01 * float3 + this.m11 * float2 + this.m21 * float1 + this.m31) * float0;
        float float6 = (this.m02 * float3 + this.m12 * float2 + this.m22 * float1 + this.m32) * float0;
        vector3f.x = (float4 * 0.5F + 0.5F) * ints[2] + ints[0];
        vector3f.y = (float5 * 0.5F + 0.5F) * ints[3] + ints[1];
        vector3f.z = (1.0F + float6) * 0.5F;
        return vector3f;
    }

    @Override
    public Vector4f project(Vector3fc vector3fc, int[] ints, Vector4f vector4f) {
        return this.project(vector3fc.x(), vector3fc.y(), vector3fc.z(), ints, vector4f);
    }

    @Override
    public Vector3f project(Vector3fc vector3fc, int[] ints, Vector3f vector3f) {
        return this.project(vector3fc.x(), vector3fc.y(), vector3fc.z(), ints, vector3f);
    }

    @Override
    public Matrix4f reflect(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4) {
        if ((this.properties & 4) != 0) {
            return arg4.reflection(arg0, arg1, arg2, arg3);
        } else {
            return (this.properties & 2) != 0 ? this.reflectAffine(arg0, arg1, arg2, arg3, arg4) : this.reflectGeneric(arg0, arg1, arg2, arg3, arg4);
        }
    }

    private Matrix4f reflectAffine(float float1, float float3, float float5, float float7, Matrix4f matrix4f1) {
        float float0 = float1 + float1;
        float float2 = float3 + float3;
        float float4 = float5 + float5;
        float float6 = float7 + float7;
        float float8 = 1.0F - float0 * float1;
        float float9 = -float0 * float3;
        float float10 = -float0 * float5;
        float float11 = -float2 * float1;
        float float12 = 1.0F - float2 * float3;
        float float13 = -float2 * float5;
        float float14 = -float4 * float1;
        float float15 = -float4 * float3;
        float float16 = 1.0F - float4 * float5;
        float float17 = -float6 * float1;
        float float18 = -float6 * float3;
        float float19 = -float6 * float5;
        matrix4f1._m30(this.m00 * float17 + this.m10 * float18 + this.m20 * float19 + this.m30)
            ._m31(this.m01 * float17 + this.m11 * float18 + this.m21 * float19 + this.m31)
            ._m32(this.m02 * float17 + this.m12 * float18 + this.m22 * float19 + this.m32)
            ._m33(this.m33);
        float float20 = this.m00 * float8 + this.m10 * float9 + this.m20 * float10;
        float float21 = this.m01 * float8 + this.m11 * float9 + this.m21 * float10;
        float float22 = this.m02 * float8 + this.m12 * float9 + this.m22 * float10;
        float float23 = this.m00 * float11 + this.m10 * float12 + this.m20 * float13;
        float float24 = this.m01 * float11 + this.m11 * float12 + this.m21 * float13;
        float float25 = this.m02 * float11 + this.m12 * float12 + this.m22 * float13;
        matrix4f1._m20(this.m00 * float14 + this.m10 * float15 + this.m20 * float16)
            ._m21(this.m01 * float14 + this.m11 * float15 + this.m21 * float16)
            ._m22(this.m02 * float14 + this.m12 * float15 + this.m22 * float16)
            ._m23(0.0F)
            ._m00(float20)
            ._m01(float21)
            ._m02(float22)
            ._m03(0.0F)
            ._m10(float23)
            ._m11(float24)
            ._m12(float25)
            ._m13(0.0F)
            ._properties(this.properties & -14);
        return matrix4f1;
    }

    private Matrix4f reflectGeneric(float float1, float float3, float float5, float float7, Matrix4f matrix4f1) {
        float float0 = float1 + float1;
        float float2 = float3 + float3;
        float float4 = float5 + float5;
        float float6 = float7 + float7;
        float float8 = 1.0F - float0 * float1;
        float float9 = -float0 * float3;
        float float10 = -float0 * float5;
        float float11 = -float2 * float1;
        float float12 = 1.0F - float2 * float3;
        float float13 = -float2 * float5;
        float float14 = -float4 * float1;
        float float15 = -float4 * float3;
        float float16 = 1.0F - float4 * float5;
        float float17 = -float6 * float1;
        float float18 = -float6 * float3;
        float float19 = -float6 * float5;
        matrix4f1._m30(this.m00 * float17 + this.m10 * float18 + this.m20 * float19 + this.m30)
            ._m31(this.m01 * float17 + this.m11 * float18 + this.m21 * float19 + this.m31)
            ._m32(this.m02 * float17 + this.m12 * float18 + this.m22 * float19 + this.m32)
            ._m33(this.m03 * float17 + this.m13 * float18 + this.m23 * float19 + this.m33);
        float float20 = this.m00 * float8 + this.m10 * float9 + this.m20 * float10;
        float float21 = this.m01 * float8 + this.m11 * float9 + this.m21 * float10;
        float float22 = this.m02 * float8 + this.m12 * float9 + this.m22 * float10;
        float float23 = this.m03 * float8 + this.m13 * float9 + this.m23 * float10;
        float float24 = this.m00 * float11 + this.m10 * float12 + this.m20 * float13;
        float float25 = this.m01 * float11 + this.m11 * float12 + this.m21 * float13;
        float float26 = this.m02 * float11 + this.m12 * float12 + this.m22 * float13;
        float float27 = this.m03 * float11 + this.m13 * float12 + this.m23 * float13;
        matrix4f1._m20(this.m00 * float14 + this.m10 * float15 + this.m20 * float16)
            ._m21(this.m01 * float14 + this.m11 * float15 + this.m21 * float16)
            ._m22(this.m02 * float14 + this.m12 * float15 + this.m22 * float16)
            ._m23(this.m03 * float14 + this.m13 * float15 + this.m23 * float16)
            ._m00(float20)
            ._m01(float21)
            ._m02(float22)
            ._m03(float23)
            ._m10(float24)
            ._m11(float25)
            ._m12(float26)
            ._m13(float27)
            ._properties(this.properties & -14);
        return matrix4f1;
    }

    public Matrix4f reflect(float arg0, float arg1, float arg2, float arg3) {
        return this.reflect(arg0, arg1, arg2, arg3, this);
    }

    public Matrix4f reflect(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.reflect(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    @Override
    public Matrix4f reflect(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6) {
        float float0 = Math.invsqrt(arg0 * arg0 + arg1 * arg1 + arg2 * arg2);
        float float1 = arg0 * float0;
        float float2 = arg1 * float0;
        float float3 = arg2 * float0;
        return this.reflect(float1, float2, float3, -float1 * arg3 - float2 * arg4 - float3 * arg5, arg6);
    }

    public Matrix4f reflect(Vector3fc arg0, Vector3fc arg1) {
        return this.reflect(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix4f reflect(Quaternionfc arg0, Vector3fc arg1) {
        return this.reflect(arg0, arg1, this);
    }

    @Override
    public Matrix4f reflect(Quaternionfc arg0, Vector3fc arg1, Matrix4f arg2) {
        double double0 = arg0.x() + arg0.x();
        double double1 = arg0.y() + arg0.y();
        double double2 = arg0.z() + arg0.z();
        float float0 = (float)(arg0.x() * double2 + arg0.w() * double1);
        float float1 = (float)(arg0.y() * double2 - arg0.w() * double0);
        float float2 = (float)(1.0 - (arg0.x() * double0 + arg0.y() * double1));
        return this.reflect(float0, float1, float2, arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    @Override
    public Matrix4f reflect(Vector3fc arg0, Vector3fc arg1, Matrix4f arg2) {
        return this.reflect(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    public Matrix4f reflection(float arg0, float arg1, float arg2, float arg3) {
        float float0 = arg0 + arg0;
        float float1 = arg1 + arg1;
        float float2 = arg2 + arg2;
        float float3 = arg3 + arg3;
        this._m00(1.0F - float0 * arg0)
            ._m01(-float0 * arg1)
            ._m02(-float0 * arg2)
            ._m03(0.0F)
            ._m10(-float1 * arg0)
            ._m11(1.0F - float1 * arg1)
            ._m12(-float1 * arg2)
            ._m13(0.0F)
            ._m20(-float2 * arg0)
            ._m21(-float2 * arg1)
            ._m22(1.0F - float2 * arg2)
            ._m23(0.0F)
            ._m30(-float3 * arg0)
            ._m31(-float3 * arg1)
            ._m32(-float3 * arg2)
            ._m33(1.0F)
            ._properties(18);
        return this;
    }

    public Matrix4f reflection(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        float float0 = Math.invsqrt(arg0 * arg0 + arg1 * arg1 + arg2 * arg2);
        float float1 = arg0 * float0;
        float float2 = arg1 * float0;
        float float3 = arg2 * float0;
        return this.reflection(float1, float2, float3, -float1 * arg3 - float2 * arg4 - float3 * arg5);
    }

    public Matrix4f reflection(Vector3fc arg0, Vector3fc arg1) {
        return this.reflection(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix4f reflection(Quaternionfc arg0, Vector3fc arg1) {
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
                return arg1.set(this.m00, this.m10, this.m20, this.m30);
            case 1:
                return arg1.set(this.m01, this.m11, this.m21, this.m31);
            case 2:
                return arg1.set(this.m02, this.m12, this.m22, this.m32);
            case 3:
                return arg1.set(this.m03, this.m13, this.m23, this.m33);
            default:
                throw new IndexOutOfBoundsException();
        }
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
            case 3:
                return arg1.set(this.m03, this.m13, this.m23);
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    public Matrix4f setRow(int arg0, Vector4fc arg1) throws IndexOutOfBoundsException {
        switch (arg0) {
            case 0:
                return this._m00(arg1.x())._m10(arg1.y())._m20(arg1.z())._m30(arg1.w())._properties(0);
            case 1:
                return this._m01(arg1.x())._m11(arg1.y())._m21(arg1.z())._m31(arg1.w())._properties(0);
            case 2:
                return this._m02(arg1.x())._m12(arg1.y())._m22(arg1.z())._m32(arg1.w())._properties(0);
            case 3:
                return this._m03(arg1.x())._m13(arg1.y())._m23(arg1.z())._m33(arg1.w())._properties(0);
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public Vector4f getColumn(int arg0, Vector4f arg1) throws IndexOutOfBoundsException {
        return MemUtil.INSTANCE.getColumn(this, arg0, arg1);
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
            case 3:
                return arg1.set(this.m30, this.m31, this.m32);
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    public Matrix4f setColumn(int arg0, Vector4fc arg1) throws IndexOutOfBoundsException {
        return arg1 instanceof Vector4f
            ? MemUtil.INSTANCE.setColumn((Vector4f)arg1, arg0, this)._properties(0)
            : MemUtil.INSTANCE.setColumn(arg1, arg0, this)._properties(0);
    }

    @Override
    public float get(int arg0, int arg1) {
        return MemUtil.INSTANCE.get(this, arg0, arg1);
    }

    public Matrix4f set(int arg0, int arg1, float arg2) {
        return MemUtil.INSTANCE.set(this, arg0, arg1, arg2);
    }

    @Override
    public float getRowColumn(int arg0, int arg1) {
        return MemUtil.INSTANCE.get(this, arg1, arg0);
    }

    public Matrix4f setRowColumn(int arg0, int arg1, float arg2) {
        return MemUtil.INSTANCE.set(this, arg1, arg0, arg2);
    }

    public Matrix4f normal() {
        return this.normal(this);
    }

    @Override
    public Matrix4f normal(Matrix4f arg0) {
        if ((this.properties & 4) != 0) {
            return arg0.identity();
        } else {
            return (this.properties & 16) != 0 ? this.normalOrthonormal(arg0) : this.normalGeneric(arg0);
        }
    }

    private Matrix4f normalOrthonormal(Matrix4f matrix4f0) {
        if (matrix4f0 != this) {
            matrix4f0.set(this);
        }

        return matrix4f0._properties(18);
    }

    private Matrix4f normalGeneric(Matrix4f matrix4f1) {
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
        return matrix4f1._m00(float8)
            ._m01(float9)
            ._m02(float10)
            ._m03(0.0F)
            ._m10(float11)
            ._m11(float12)
            ._m12(float13)
            ._m13(0.0F)
            ._m20(float14)
            ._m21(float15)
            ._m22(float16)
            ._m23(0.0F)
            ._m30(0.0F)
            ._m31(0.0F)
            ._m32(0.0F)
            ._m33(1.0F)
            ._properties((this.properties | 2) & -10);
    }

    @Override
    public Matrix3f normal(Matrix3f arg0) {
        return (this.properties & 16) != 0 ? this.normalOrthonormal(arg0) : this.normalGeneric(arg0);
    }

    private Matrix3f normalOrthonormal(Matrix3f matrix3f) {
        matrix3f.set(this);
        return matrix3f;
    }

    private Matrix3f normalGeneric(Matrix3f matrix3f) {
        float float0 = (this.m00 * this.m11 - this.m01 * this.m10) * this.m22
            + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21
            + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
        float float1 = 1.0F / float0;
        return matrix3f._m00((this.m11 * this.m22 - this.m21 * this.m12) * float1)
            ._m01((this.m20 * this.m12 - this.m10 * this.m22) * float1)
            ._m02((this.m10 * this.m21 - this.m20 * this.m11) * float1)
            ._m10((this.m21 * this.m02 - this.m01 * this.m22) * float1)
            ._m11((this.m00 * this.m22 - this.m20 * this.m02) * float1)
            ._m12((this.m20 * this.m01 - this.m00 * this.m21) * float1)
            ._m20((this.m01 * this.m12 - this.m02 * this.m11) * float1)
            ._m21((this.m02 * this.m10 - this.m00 * this.m12) * float1)
            ._m22((this.m00 * this.m11 - this.m01 * this.m10) * float1);
    }

    public Matrix4f cofactor3x3() {
        return this.cofactor3x3(this);
    }

    @Override
    public Matrix3f cofactor3x3(Matrix3f arg0) {
        return arg0._m00(this.m11 * this.m22 - this.m21 * this.m12)
            ._m01(this.m20 * this.m12 - this.m10 * this.m22)
            ._m02(this.m10 * this.m21 - this.m20 * this.m11)
            ._m10(this.m21 * this.m02 - this.m01 * this.m22)
            ._m11(this.m00 * this.m22 - this.m20 * this.m02)
            ._m12(this.m20 * this.m01 - this.m00 * this.m21)
            ._m20(this.m01 * this.m12 - this.m02 * this.m11)
            ._m21(this.m02 * this.m10 - this.m00 * this.m12)
            ._m22(this.m00 * this.m11 - this.m01 * this.m10);
    }

    @Override
    public Matrix4f cofactor3x3(Matrix4f arg0) {
        float float0 = this.m21 * this.m02 - this.m01 * this.m22;
        float float1 = this.m00 * this.m22 - this.m20 * this.m02;
        float float2 = this.m20 * this.m01 - this.m00 * this.m21;
        float float3 = this.m01 * this.m12 - this.m11 * this.m02;
        float float4 = this.m02 * this.m10 - this.m12 * this.m00;
        float float5 = this.m00 * this.m11 - this.m10 * this.m01;
        return arg0._m00(this.m11 * this.m22 - this.m21 * this.m12)
            ._m01(this.m20 * this.m12 - this.m10 * this.m22)
            ._m02(this.m10 * this.m21 - this.m20 * this.m11)
            ._m03(0.0F)
            ._m10(float0)
            ._m11(float1)
            ._m12(float2)
            ._m13(0.0F)
            ._m20(float3)
            ._m21(float4)
            ._m22(float5)
            ._m23(0.0F)
            ._m30(0.0F)
            ._m31(0.0F)
            ._m32(0.0F)
            ._m33(1.0F)
            ._properties((this.properties | 2) & -10);
    }

    public Matrix4f normalize3x3() {
        return this.normalize3x3(this);
    }

    @Override
    public Matrix4f normalize3x3(Matrix4f arg0) {
        float float0 = Math.invsqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
        float float1 = Math.invsqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
        float float2 = Math.invsqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
        return arg0._m00(this.m00 * float0)
            ._m01(this.m01 * float0)
            ._m02(this.m02 * float0)
            ._m10(this.m10 * float1)
            ._m11(this.m11 * float1)
            ._m12(this.m12 * float1)
            ._m20(this.m20 * float2)
            ._m21(this.m21 * float2)
            ._m22(this.m22 * float2)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties);
    }

    @Override
    public Matrix3f normalize3x3(Matrix3f arg0) {
        float float0 = Math.invsqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
        float float1 = Math.invsqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
        float float2 = Math.invsqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
        return arg0._m00(this.m00 * float0)
            ._m01(this.m01 * float0)
            ._m02(this.m02 * float0)
            ._m10(this.m10 * float1)
            ._m11(this.m11 * float1)
            ._m12(this.m12 * float1)
            ._m20(this.m20 * float2)
            ._m21(this.m21 * float2)
            ._m22(this.m22 * float2);
    }

    @Override
    public Vector4f frustumPlane(int arg0, Vector4f arg1) {
        switch (arg0) {
            case 0:
                arg1.set(this.m03 + this.m00, this.m13 + this.m10, this.m23 + this.m20, this.m33 + this.m30).normalize3();
                break;
            case 1:
                arg1.set(this.m03 - this.m00, this.m13 - this.m10, this.m23 - this.m20, this.m33 - this.m30).normalize3();
                break;
            case 2:
                arg1.set(this.m03 + this.m01, this.m13 + this.m11, this.m23 + this.m21, this.m33 + this.m31).normalize3();
                break;
            case 3:
                arg1.set(this.m03 - this.m01, this.m13 - this.m11, this.m23 - this.m21, this.m33 - this.m31).normalize3();
                break;
            case 4:
                arg1.set(this.m03 + this.m02, this.m13 + this.m12, this.m23 + this.m22, this.m33 + this.m32).normalize3();
                break;
            case 5:
                arg1.set(this.m03 - this.m02, this.m13 - this.m12, this.m23 - this.m22, this.m33 - this.m32).normalize3();
                break;
            default:
                throw new IllegalArgumentException("dest");
        }

        return arg1;
    }

    @Override
    public Vector3f frustumCorner(int arg0, Vector3f arg1) {
        float float0;
        float float1;
        float float2;
        float float3;
        float float4;
        float float5;
        float float6;
        float float7;
        float float8;
        float float9;
        float float10;
        float float11;
        switch (arg0) {
            case 0:
                float3 = this.m03 + this.m00;
                float4 = this.m13 + this.m10;
                float5 = this.m23 + this.m20;
                float0 = this.m33 + this.m30;
                float6 = this.m03 + this.m01;
                float7 = this.m13 + this.m11;
                float8 = this.m23 + this.m21;
                float1 = this.m33 + this.m31;
                float9 = this.m03 + this.m02;
                float10 = this.m13 + this.m12;
                float11 = this.m23 + this.m22;
                float2 = this.m33 + this.m32;
                break;
            case 1:
                float3 = this.m03 - this.m00;
                float4 = this.m13 - this.m10;
                float5 = this.m23 - this.m20;
                float0 = this.m33 - this.m30;
                float6 = this.m03 + this.m01;
                float7 = this.m13 + this.m11;
                float8 = this.m23 + this.m21;
                float1 = this.m33 + this.m31;
                float9 = this.m03 + this.m02;
                float10 = this.m13 + this.m12;
                float11 = this.m23 + this.m22;
                float2 = this.m33 + this.m32;
                break;
            case 2:
                float3 = this.m03 - this.m00;
                float4 = this.m13 - this.m10;
                float5 = this.m23 - this.m20;
                float0 = this.m33 - this.m30;
                float6 = this.m03 - this.m01;
                float7 = this.m13 - this.m11;
                float8 = this.m23 - this.m21;
                float1 = this.m33 - this.m31;
                float9 = this.m03 + this.m02;
                float10 = this.m13 + this.m12;
                float11 = this.m23 + this.m22;
                float2 = this.m33 + this.m32;
                break;
            case 3:
                float3 = this.m03 + this.m00;
                float4 = this.m13 + this.m10;
                float5 = this.m23 + this.m20;
                float0 = this.m33 + this.m30;
                float6 = this.m03 - this.m01;
                float7 = this.m13 - this.m11;
                float8 = this.m23 - this.m21;
                float1 = this.m33 - this.m31;
                float9 = this.m03 + this.m02;
                float10 = this.m13 + this.m12;
                float11 = this.m23 + this.m22;
                float2 = this.m33 + this.m32;
                break;
            case 4:
                float3 = this.m03 - this.m00;
                float4 = this.m13 - this.m10;
                float5 = this.m23 - this.m20;
                float0 = this.m33 - this.m30;
                float6 = this.m03 + this.m01;
                float7 = this.m13 + this.m11;
                float8 = this.m23 + this.m21;
                float1 = this.m33 + this.m31;
                float9 = this.m03 - this.m02;
                float10 = this.m13 - this.m12;
                float11 = this.m23 - this.m22;
                float2 = this.m33 - this.m32;
                break;
            case 5:
                float3 = this.m03 + this.m00;
                float4 = this.m13 + this.m10;
                float5 = this.m23 + this.m20;
                float0 = this.m33 + this.m30;
                float6 = this.m03 + this.m01;
                float7 = this.m13 + this.m11;
                float8 = this.m23 + this.m21;
                float1 = this.m33 + this.m31;
                float9 = this.m03 - this.m02;
                float10 = this.m13 - this.m12;
                float11 = this.m23 - this.m22;
                float2 = this.m33 - this.m32;
                break;
            case 6:
                float3 = this.m03 + this.m00;
                float4 = this.m13 + this.m10;
                float5 = this.m23 + this.m20;
                float0 = this.m33 + this.m30;
                float6 = this.m03 - this.m01;
                float7 = this.m13 - this.m11;
                float8 = this.m23 - this.m21;
                float1 = this.m33 - this.m31;
                float9 = this.m03 - this.m02;
                float10 = this.m13 - this.m12;
                float11 = this.m23 - this.m22;
                float2 = this.m33 - this.m32;
                break;
            case 7:
                float3 = this.m03 - this.m00;
                float4 = this.m13 - this.m10;
                float5 = this.m23 - this.m20;
                float0 = this.m33 - this.m30;
                float6 = this.m03 - this.m01;
                float7 = this.m13 - this.m11;
                float8 = this.m23 - this.m21;
                float1 = this.m33 - this.m31;
                float9 = this.m03 - this.m02;
                float10 = this.m13 - this.m12;
                float11 = this.m23 - this.m22;
                float2 = this.m33 - this.m32;
                break;
            default:
                throw new IllegalArgumentException("corner");
        }

        float float12 = float7 * float11 - float8 * float10;
        float float13 = float8 * float9 - float6 * float11;
        float float14 = float6 * float10 - float7 * float9;
        float float15 = float10 * float5 - float11 * float4;
        float float16 = float11 * float3 - float9 * float5;
        float float17 = float9 * float4 - float10 * float3;
        float float18 = float4 * float8 - float5 * float7;
        float float19 = float5 * float6 - float3 * float8;
        float float20 = float3 * float7 - float4 * float6;
        float float21 = 1.0F / (float3 * float12 + float4 * float13 + float5 * float14);
        arg1.x = (-float12 * float0 - float15 * float1 - float18 * float2) * float21;
        arg1.y = (-float13 * float0 - float16 * float1 - float19 * float2) * float21;
        arg1.z = (-float14 * float0 - float17 * float1 - float20 * float2) * float21;
        return arg1;
    }

    @Override
    public Vector3f perspectiveOrigin(Vector3f arg0) {
        float float0 = this.m03 + this.m00;
        float float1 = this.m13 + this.m10;
        float float2 = this.m23 + this.m20;
        float float3 = this.m33 + this.m30;
        float float4 = this.m03 - this.m00;
        float float5 = this.m13 - this.m10;
        float float6 = this.m23 - this.m20;
        float float7 = this.m33 - this.m30;
        float float8 = this.m03 - this.m01;
        float float9 = this.m13 - this.m11;
        float float10 = this.m23 - this.m21;
        float float11 = this.m33 - this.m31;
        float float12 = float5 * float10 - float6 * float9;
        float float13 = float6 * float8 - float4 * float10;
        float float14 = float4 * float9 - float5 * float8;
        float float15 = float9 * float2 - float10 * float1;
        float float16 = float10 * float0 - float8 * float2;
        float float17 = float8 * float1 - float9 * float0;
        float float18 = float1 * float6 - float2 * float5;
        float float19 = float2 * float4 - float0 * float6;
        float float20 = float0 * float5 - float1 * float4;
        float float21 = 1.0F / (float0 * float12 + float1 * float13 + float2 * float14);
        arg0.x = (-float12 * float3 - float15 * float7 - float18 * float11) * float21;
        arg0.y = (-float13 * float3 - float16 * float7 - float19 * float11) * float21;
        arg0.z = (-float14 * float3 - float17 * float7 - float20 * float11) * float21;
        return arg0;
    }

    @Override
    public Vector3f perspectiveInvOrigin(Vector3f arg0) {
        float float0 = 1.0F / this.m23;
        arg0.x = this.m20 * float0;
        arg0.y = this.m21 * float0;
        arg0.z = this.m22 * float0;
        return arg0;
    }

    @Override
    public float perspectiveFov() {
        float float0 = this.m03 + this.m01;
        float float1 = this.m13 + this.m11;
        float float2 = this.m23 + this.m21;
        float float3 = this.m01 - this.m03;
        float float4 = this.m11 - this.m13;
        float float5 = this.m21 - this.m23;
        float float6 = Math.sqrt(float0 * float0 + float1 * float1 + float2 * float2);
        float float7 = Math.sqrt(float3 * float3 + float4 * float4 + float5 * float5);
        return Math.acos((float0 * float3 + float1 * float4 + float2 * float5) / (float6 * float7));
    }

    @Override
    public float perspectiveNear() {
        return this.m32 / (this.m23 + this.m22);
    }

    @Override
    public float perspectiveFar() {
        return this.m32 / (this.m22 - this.m23);
    }

    @Override
    public Vector3f frustumRayDir(float arg0, float arg1, Vector3f arg2) {
        float float0 = this.m10 * this.m23;
        float float1 = this.m13 * this.m21;
        float float2 = this.m10 * this.m21;
        float float3 = this.m11 * this.m23;
        float float4 = this.m13 * this.m20;
        float float5 = this.m11 * this.m20;
        float float6 = this.m03 * this.m20;
        float float7 = this.m01 * this.m23;
        float float8 = this.m01 * this.m20;
        float float9 = this.m03 * this.m21;
        float float10 = this.m00 * this.m23;
        float float11 = this.m00 * this.m21;
        float float12 = this.m00 * this.m13;
        float float13 = this.m03 * this.m11;
        float float14 = this.m00 * this.m11;
        float float15 = this.m01 * this.m13;
        float float16 = this.m03 * this.m10;
        float float17 = this.m01 * this.m10;
        float float18 = (float3 + float4 + float5 - float0 - float1 - float2) * (1.0F - arg1) + (float0 - float1 - float2 + float3 - float4 + float5) * arg1;
        float float19 = (float9 + float10 + float11 - float6 - float7 - float8) * (1.0F - arg1)
            + (float6 - float7 - float8 + float9 - float10 + float11) * arg1;
        float float20 = (float15 + float16 + float17 - float12 - float13 - float14) * (1.0F - arg1)
            + (float12 - float13 - float14 + float15 - float16 + float17) * arg1;
        float float21 = (float1 - float2 - float3 + float4 + float5 - float0) * (1.0F - arg1) + (float0 + float1 - float2 - float3 - float4 + float5) * arg1;
        float float22 = (float7 - float8 - float9 + float10 + float11 - float6) * (1.0F - arg1)
            + (float6 + float7 - float8 - float9 - float10 + float11) * arg1;
        float float23 = (float13 - float14 - float15 + float16 + float17 - float12) * (1.0F - arg1)
            + (float12 + float13 - float14 - float15 - float16 + float17) * arg1;
        arg2.x = float18 + (float21 - float18) * arg0;
        arg2.y = float19 + (float22 - float19) * arg0;
        arg2.z = float20 + (float23 - float20) * arg0;
        return arg2.normalize(arg2);
    }

    @Override
    public Vector3f positiveZ(Vector3f arg0) {
        return (this.properties & 16) != 0 ? this.normalizedPositiveZ(arg0) : this.positiveZGeneric(arg0);
    }

    private Vector3f positiveZGeneric(Vector3f vector3f) {
        return vector3f.set(this.m10 * this.m21 - this.m11 * this.m20, this.m20 * this.m01 - this.m21 * this.m00, this.m00 * this.m11 - this.m01 * this.m10)
            .normalize();
    }

    @Override
    public Vector3f normalizedPositiveZ(Vector3f arg0) {
        return arg0.set(this.m02, this.m12, this.m22);
    }

    @Override
    public Vector3f positiveX(Vector3f arg0) {
        return (this.properties & 16) != 0 ? this.normalizedPositiveX(arg0) : this.positiveXGeneric(arg0);
    }

    private Vector3f positiveXGeneric(Vector3f vector3f) {
        return vector3f.set(this.m11 * this.m22 - this.m12 * this.m21, this.m02 * this.m21 - this.m01 * this.m22, this.m01 * this.m12 - this.m02 * this.m11)
            .normalize();
    }

    @Override
    public Vector3f normalizedPositiveX(Vector3f arg0) {
        return arg0.set(this.m00, this.m10, this.m20);
    }

    @Override
    public Vector3f positiveY(Vector3f arg0) {
        return (this.properties & 16) != 0 ? this.normalizedPositiveY(arg0) : this.positiveYGeneric(arg0);
    }

    private Vector3f positiveYGeneric(Vector3f vector3f) {
        return vector3f.set(this.m12 * this.m20 - this.m10 * this.m22, this.m00 * this.m22 - this.m02 * this.m20, this.m02 * this.m10 - this.m00 * this.m12)
            .normalize();
    }

    @Override
    public Vector3f normalizedPositiveY(Vector3f arg0) {
        return arg0.set(this.m01, this.m11, this.m21);
    }

    @Override
    public Vector3f originAffine(Vector3f arg0) {
        float float0 = this.m00 * this.m11 - this.m01 * this.m10;
        float float1 = this.m00 * this.m12 - this.m02 * this.m10;
        float float2 = this.m01 * this.m12 - this.m02 * this.m11;
        float float3 = this.m20 * this.m31 - this.m21 * this.m30;
        float float4 = this.m20 * this.m32 - this.m22 * this.m30;
        float float5 = this.m21 * this.m32 - this.m22 * this.m31;
        return arg0.set(
            -this.m10 * float5 + this.m11 * float4 - this.m12 * float3,
            this.m00 * float5 - this.m01 * float4 + this.m02 * float3,
            -this.m30 * float2 + this.m31 * float1 - this.m32 * float0
        );
    }

    @Override
    public Vector3f origin(Vector3f arg0) {
        return (this.properties & 2) != 0 ? this.originAffine(arg0) : this.originGeneric(arg0);
    }

    private Vector3f originGeneric(Vector3f vector3f) {
        float float0 = this.m00 * this.m11 - this.m01 * this.m10;
        float float1 = this.m00 * this.m12 - this.m02 * this.m10;
        float float2 = this.m00 * this.m13 - this.m03 * this.m10;
        float float3 = this.m01 * this.m12 - this.m02 * this.m11;
        float float4 = this.m01 * this.m13 - this.m03 * this.m11;
        float float5 = this.m02 * this.m13 - this.m03 * this.m12;
        float float6 = this.m20 * this.m31 - this.m21 * this.m30;
        float float7 = this.m20 * this.m32 - this.m22 * this.m30;
        float float8 = this.m20 * this.m33 - this.m23 * this.m30;
        float float9 = this.m21 * this.m32 - this.m22 * this.m31;
        float float10 = this.m21 * this.m33 - this.m23 * this.m31;
        float float11 = this.m22 * this.m33 - this.m23 * this.m32;
        float float12 = float0 * float11 - float1 * float10 + float2 * float9 + float3 * float8 - float4 * float7 + float5 * float6;
        float float13 = 1.0F / float12;
        float float14 = (-this.m10 * float9 + this.m11 * float7 - this.m12 * float6) * float13;
        float float15 = (this.m00 * float9 - this.m01 * float7 + this.m02 * float6) * float13;
        float float16 = (-this.m30 * float3 + this.m31 * float1 - this.m32 * float0) * float13;
        float float17 = float12 / (this.m20 * float3 - this.m21 * float1 + this.m22 * float0);
        return vector3f.set(float14 * float17, float15 * float17, float16 * float17);
    }

    public Matrix4f shadow(Vector4f arg0, float arg1, float arg2, float arg3, float arg4) {
        return this.shadow(arg0.x, arg0.y, arg0.z, arg0.w, arg1, arg2, arg3, arg4, this);
    }

    @Override
    public Matrix4f shadow(Vector4f arg0, float arg1, float arg2, float arg3, float arg4, Matrix4f arg5) {
        return this.shadow(arg0.x, arg0.y, arg0.z, arg0.w, arg1, arg2, arg3, arg4, arg5);
    }

    public Matrix4f shadow(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7) {
        return this.shadow(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, this);
    }

    @Override
    public Matrix4f shadow(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, Matrix4f arg8) {
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
        float float25 = this.m03 * float6 + this.m13 * float7 + this.m23 * float8 + this.m33 * float9;
        float float26 = this.m00 * float10 + this.m10 * float11 + this.m20 * float12 + this.m30 * float13;
        float float27 = this.m01 * float10 + this.m11 * float11 + this.m21 * float12 + this.m31 * float13;
        float float28 = this.m02 * float10 + this.m12 * float11 + this.m22 * float12 + this.m32 * float13;
        float float29 = this.m03 * float10 + this.m13 * float11 + this.m23 * float12 + this.m33 * float13;
        float float30 = this.m00 * float14 + this.m10 * float15 + this.m20 * float16 + this.m30 * float17;
        float float31 = this.m01 * float14 + this.m11 * float15 + this.m21 * float16 + this.m31 * float17;
        float float32 = this.m02 * float14 + this.m12 * float15 + this.m22 * float16 + this.m32 * float17;
        float float33 = this.m03 * float14 + this.m13 * float15 + this.m23 * float16 + this.m33 * float17;
        arg8._m30(this.m00 * float18 + this.m10 * float19 + this.m20 * float20 + this.m30 * float21)
            ._m31(this.m01 * float18 + this.m11 * float19 + this.m21 * float20 + this.m31 * float21)
            ._m32(this.m02 * float18 + this.m12 * float19 + this.m22 * float20 + this.m32 * float21)
            ._m33(this.m03 * float18 + this.m13 * float19 + this.m23 * float20 + this.m33 * float21)
            ._m00(float22)
            ._m01(float23)
            ._m02(float24)
            ._m03(float25)
            ._m10(float26)
            ._m11(float27)
            ._m12(float28)
            ._m13(float29)
            ._m20(float30)
            ._m21(float31)
            ._m22(float32)
            ._m23(float33)
            ._properties(this.properties & -30);
        return arg8;
    }

    @Override
    public Matrix4f shadow(Vector4f arg0, Matrix4fc arg1, Matrix4f arg2) {
        float float0 = arg1.m10();
        float float1 = arg1.m11();
        float float2 = arg1.m12();
        float float3 = -float0 * arg1.m30() - float1 * arg1.m31() - float2 * arg1.m32();
        return this.shadow(arg0.x, arg0.y, arg0.z, arg0.w, float0, float1, float2, float3, arg2);
    }

    public Matrix4f shadow(Vector4f arg0, Matrix4f arg1) {
        return this.shadow(arg0, arg1, this);
    }

    @Override
    public Matrix4f shadow(float arg0, float arg1, float arg2, float arg3, Matrix4fc arg4, Matrix4f arg5) {
        float float0 = arg4.m10();
        float float1 = arg4.m11();
        float float2 = arg4.m12();
        float float3 = -float0 * arg4.m30() - float1 * arg4.m31() - float2 * arg4.m32();
        return this.shadow(arg0, arg1, arg2, arg3, float0, float1, float2, float3, arg5);
    }

    public Matrix4f shadow(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4) {
        return this.shadow(arg0, arg1, arg2, arg3, arg4, this);
    }

    public Matrix4f billboardCylindrical(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2) {
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
        this._m00(float3)
            ._m01(float4)
            ._m02(float5)
            ._m03(0.0F)
            ._m10(arg2.x())
            ._m11(arg2.y())
            ._m12(arg2.z())
            ._m13(0.0F)
            ._m20(float0)
            ._m21(float1)
            ._m22(float2)
            ._m23(0.0F)
            ._m30(arg0.x())
            ._m31(arg0.y())
            ._m32(arg0.z())
            ._m33(1.0F)
            ._properties(18);
        return this;
    }

    public Matrix4f billboardSpherical(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2) {
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
        this._m00(float4)
            ._m01(float5)
            ._m02(float6)
            ._m03(0.0F)
            ._m10(float8)
            ._m11(float9)
            ._m12(float10)
            ._m13(0.0F)
            ._m20(float0)
            ._m21(float1)
            ._m22(float2)
            ._m23(0.0F)
            ._m30(arg0.x())
            ._m31(arg0.y())
            ._m32(arg0.z())
            ._m33(1.0F)
            ._properties(18);
        return this;
    }

    public Matrix4f billboardSpherical(Vector3fc arg0, Vector3fc arg1) {
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
        this._m00(1.0F - float8)
            ._m01(float9)
            ._m02(-float11)
            ._m03(0.0F)
            ._m10(float9)
            ._m11(1.0F - float7)
            ._m12(float10)
            ._m13(0.0F)
            ._m20(float11)
            ._m21(-float10)
            ._m22(1.0F - float8 - float7)
            ._m23(0.0F)
            ._m30(arg0.x())
            ._m31(arg0.y())
            ._m32(arg0.z())
            ._m33(1.0F)
            ._properties(18);
        return this;
    }

    @Override
    public int hashCode() {
        int int0 = 1;
        int0 = 31 * int0 + Float.floatToIntBits(this.m00);
        int0 = 31 * int0 + Float.floatToIntBits(this.m01);
        int0 = 31 * int0 + Float.floatToIntBits(this.m02);
        int0 = 31 * int0 + Float.floatToIntBits(this.m03);
        int0 = 31 * int0 + Float.floatToIntBits(this.m10);
        int0 = 31 * int0 + Float.floatToIntBits(this.m11);
        int0 = 31 * int0 + Float.floatToIntBits(this.m12);
        int0 = 31 * int0 + Float.floatToIntBits(this.m13);
        int0 = 31 * int0 + Float.floatToIntBits(this.m20);
        int0 = 31 * int0 + Float.floatToIntBits(this.m21);
        int0 = 31 * int0 + Float.floatToIntBits(this.m22);
        int0 = 31 * int0 + Float.floatToIntBits(this.m23);
        int0 = 31 * int0 + Float.floatToIntBits(this.m30);
        int0 = 31 * int0 + Float.floatToIntBits(this.m31);
        int0 = 31 * int0 + Float.floatToIntBits(this.m32);
        return 31 * int0 + Float.floatToIntBits(this.m33);
    }

    @Override
    public boolean equals(Object arg0) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (!(arg0 instanceof Matrix4f)) {
            return false;
        } else {
            Matrix4fc matrix4fc = (Matrix4fc)arg0;
            if (Float.floatToIntBits(this.m00) != Float.floatToIntBits(matrix4fc.m00())) {
                return false;
            } else if (Float.floatToIntBits(this.m01) != Float.floatToIntBits(matrix4fc.m01())) {
                return false;
            } else if (Float.floatToIntBits(this.m02) != Float.floatToIntBits(matrix4fc.m02())) {
                return false;
            } else if (Float.floatToIntBits(this.m03) != Float.floatToIntBits(matrix4fc.m03())) {
                return false;
            } else if (Float.floatToIntBits(this.m10) != Float.floatToIntBits(matrix4fc.m10())) {
                return false;
            } else if (Float.floatToIntBits(this.m11) != Float.floatToIntBits(matrix4fc.m11())) {
                return false;
            } else if (Float.floatToIntBits(this.m12) != Float.floatToIntBits(matrix4fc.m12())) {
                return false;
            } else if (Float.floatToIntBits(this.m13) != Float.floatToIntBits(matrix4fc.m13())) {
                return false;
            } else if (Float.floatToIntBits(this.m20) != Float.floatToIntBits(matrix4fc.m20())) {
                return false;
            } else if (Float.floatToIntBits(this.m21) != Float.floatToIntBits(matrix4fc.m21())) {
                return false;
            } else if (Float.floatToIntBits(this.m22) != Float.floatToIntBits(matrix4fc.m22())) {
                return false;
            } else if (Float.floatToIntBits(this.m23) != Float.floatToIntBits(matrix4fc.m23())) {
                return false;
            } else if (Float.floatToIntBits(this.m30) != Float.floatToIntBits(matrix4fc.m30())) {
                return false;
            } else if (Float.floatToIntBits(this.m31) != Float.floatToIntBits(matrix4fc.m31())) {
                return false;
            } else {
                return Float.floatToIntBits(this.m32) != Float.floatToIntBits(matrix4fc.m32())
                    ? false
                    : Float.floatToIntBits(this.m33) == Float.floatToIntBits(matrix4fc.m33());
            }
        }
    }

    @Override
    public boolean equals(Matrix4fc arg0, float arg1) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (!(arg0 instanceof Matrix4f)) {
            return false;
        } else if (!Runtime.equals(this.m00, arg0.m00(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m01, arg0.m01(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m02, arg0.m02(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m03, arg0.m03(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m10, arg0.m10(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m11, arg0.m11(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m12, arg0.m12(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m13, arg0.m13(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m20, arg0.m20(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m21, arg0.m21(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m22, arg0.m22(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m23, arg0.m23(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m30, arg0.m30(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m31, arg0.m31(), arg1)) {
            return false;
        } else {
            return !Runtime.equals(this.m32, arg0.m32(), arg1) ? false : Runtime.equals(this.m33, arg0.m33(), arg1);
        }
    }

    @Override
    public Matrix4f pick(float float5, float float7, float float1, float float3, int[] ints, Matrix4f matrix4f1) {
        float float0 = ints[2] / float1;
        float float2 = ints[3] / float3;
        float float4 = (ints[2] + 2.0F * (ints[0] - float5)) / float1;
        float float6 = (ints[3] + 2.0F * (ints[1] - float7)) / float3;
        matrix4f1._m30(this.m00 * float4 + this.m10 * float6 + this.m30)
            ._m31(this.m01 * float4 + this.m11 * float6 + this.m31)
            ._m32(this.m02 * float4 + this.m12 * float6 + this.m32)
            ._m33(this.m03 * float4 + this.m13 * float6 + this.m33)
            ._m00(this.m00 * float0)
            ._m01(this.m01 * float0)
            ._m02(this.m02 * float0)
            ._m03(this.m03 * float0)
            ._m10(this.m10 * float2)
            ._m11(this.m11 * float2)
            ._m12(this.m12 * float2)
            ._m13(this.m13 * float2)
            ._properties(0);
        return matrix4f1;
    }

    public Matrix4f pick(float float0, float float1, float float2, float float3, int[] ints) {
        return this.pick(float0, float1, float2, float3, ints, this);
    }

    @Override
    public boolean isAffine() {
        return this.m03 == 0.0F && this.m13 == 0.0F && this.m23 == 0.0F && this.m33 == 1.0F;
    }

    public Matrix4f swap(Matrix4f arg0) {
        MemUtil.INSTANCE.swap(this, arg0);
        int int0 = this.properties;
        this.properties = arg0.properties();
        arg0.properties = int0;
        return this;
    }

    @Override
    public Matrix4f arcball(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6) {
        float float0 = this.m20 * -arg0 + this.m30;
        float float1 = this.m21 * -arg0 + this.m31;
        float float2 = this.m22 * -arg0 + this.m32;
        float float3 = this.m23 * -arg0 + this.m33;
        float float4 = Math.sin(arg4);
        float float5 = Math.cosFromSin(float4, arg4);
        float float6 = this.m10 * float5 + this.m20 * float4;
        float float7 = this.m11 * float5 + this.m21 * float4;
        float float8 = this.m12 * float5 + this.m22 * float4;
        float float9 = this.m13 * float5 + this.m23 * float4;
        float float10 = this.m20 * float5 - this.m10 * float4;
        float float11 = this.m21 * float5 - this.m11 * float4;
        float float12 = this.m22 * float5 - this.m12 * float4;
        float float13 = this.m23 * float5 - this.m13 * float4;
        float4 = Math.sin(arg5);
        float5 = Math.cosFromSin(float4, arg5);
        float float14 = this.m00 * float5 - float10 * float4;
        float float15 = this.m01 * float5 - float11 * float4;
        float float16 = this.m02 * float5 - float12 * float4;
        float float17 = this.m03 * float5 - float13 * float4;
        float float18 = this.m00 * float4 + float10 * float5;
        float float19 = this.m01 * float4 + float11 * float5;
        float float20 = this.m02 * float4 + float12 * float5;
        float float21 = this.m03 * float4 + float13 * float5;
        arg6._m30(-float14 * arg1 - float6 * arg2 - float18 * arg3 + float0)
            ._m31(-float15 * arg1 - float7 * arg2 - float19 * arg3 + float1)
            ._m32(-float16 * arg1 - float8 * arg2 - float20 * arg3 + float2)
            ._m33(-float17 * arg1 - float9 * arg2 - float21 * arg3 + float3)
            ._m20(float18)
            ._m21(float19)
            ._m22(float20)
            ._m23(float21)
            ._m10(float6)
            ._m11(float7)
            ._m12(float8)
            ._m13(float9)
            ._m00(float14)
            ._m01(float15)
            ._m02(float16)
            ._m03(float17)
            ._properties(this.properties & -14);
        return arg6;
    }

    @Override
    public Matrix4f arcball(float arg0, Vector3fc arg1, float arg2, float arg3, Matrix4f arg4) {
        return this.arcball(arg0, arg1.x(), arg1.y(), arg1.z(), arg2, arg3, arg4);
    }

    public Matrix4f arcball(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.arcball(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    public Matrix4f arcball(float arg0, Vector3fc arg1, float arg2, float arg3) {
        return this.arcball(arg0, arg1.x(), arg1.y(), arg1.z(), arg2, arg3, this);
    }

    @Override
    public Matrix4f frustumAabb(Vector3f arg0, Vector3f arg1) {
        float float0 = Float.POSITIVE_INFINITY;
        float float1 = Float.POSITIVE_INFINITY;
        float float2 = Float.POSITIVE_INFINITY;
        float float3 = Float.NEGATIVE_INFINITY;
        float float4 = Float.NEGATIVE_INFINITY;
        float float5 = Float.NEGATIVE_INFINITY;

        for (int int0 = 0; int0 < 8; int0++) {
            float float6 = ((int0 & 1) << 1) - 1.0F;
            float float7 = ((int0 >>> 1 & 1) << 1) - 1.0F;
            float float8 = ((int0 >>> 2 & 1) << 1) - 1.0F;
            float float9 = 1.0F / (this.m03 * float6 + this.m13 * float7 + this.m23 * float8 + this.m33);
            float float10 = (this.m00 * float6 + this.m10 * float7 + this.m20 * float8 + this.m30) * float9;
            float float11 = (this.m01 * float6 + this.m11 * float7 + this.m21 * float8 + this.m31) * float9;
            float float12 = (this.m02 * float6 + this.m12 * float7 + this.m22 * float8 + this.m32) * float9;
            float0 = float0 < float10 ? float0 : float10;
            float1 = float1 < float11 ? float1 : float11;
            float2 = float2 < float12 ? float2 : float12;
            float3 = float3 > float10 ? float3 : float10;
            float4 = float4 > float11 ? float4 : float11;
            float5 = float5 > float12 ? float5 : float12;
        }

        arg0.x = float0;
        arg0.y = float1;
        arg0.z = float2;
        arg1.x = float3;
        arg1.y = float4;
        arg1.z = float5;
        return this;
    }

    @Override
    public Matrix4f projectedGridRange(Matrix4fc arg0, float arg1, float arg2, Matrix4f arg3) {
        float float0 = Float.POSITIVE_INFINITY;
        float float1 = Float.POSITIVE_INFINITY;
        float float2 = Float.NEGATIVE_INFINITY;
        float float3 = Float.NEGATIVE_INFINITY;
        boolean boolean0 = false;

        for (int int0 = 0; int0 < 12; int0++) {
            float float4;
            float float5;
            float float6;
            float float7;
            float float8;
            float float9;
            if (int0 < 4) {
                float4 = -1.0F;
                float7 = 1.0F;
                float5 = float8 = ((int0 & 1) << 1) - 1.0F;
                float6 = float9 = ((int0 >>> 1 & 1) << 1) - 1.0F;
            } else if (int0 < 8) {
                float5 = -1.0F;
                float8 = 1.0F;
                float4 = float7 = ((int0 & 1) << 1) - 1.0F;
                float6 = float9 = ((int0 >>> 1 & 1) << 1) - 1.0F;
            } else {
                float6 = -1.0F;
                float9 = 1.0F;
                float4 = float7 = ((int0 & 1) << 1) - 1.0F;
                float5 = float8 = ((int0 >>> 1 & 1) << 1) - 1.0F;
            }

            float float10 = 1.0F / (this.m03 * float4 + this.m13 * float5 + this.m23 * float6 + this.m33);
            float float11 = (this.m00 * float4 + this.m10 * float5 + this.m20 * float6 + this.m30) * float10;
            float float12 = (this.m01 * float4 + this.m11 * float5 + this.m21 * float6 + this.m31) * float10;
            float float13 = (this.m02 * float4 + this.m12 * float5 + this.m22 * float6 + this.m32) * float10;
            float10 = 1.0F / (this.m03 * float7 + this.m13 * float8 + this.m23 * float9 + this.m33);
            float float14 = (this.m00 * float7 + this.m10 * float8 + this.m20 * float9 + this.m30) * float10;
            float float15 = (this.m01 * float7 + this.m11 * float8 + this.m21 * float9 + this.m31) * float10;
            float float16 = (this.m02 * float7 + this.m12 * float8 + this.m22 * float9 + this.m32) * float10;
            float float17 = float14 - float11;
            float float18 = float15 - float12;
            float float19 = float16 - float13;
            float float20 = 1.0F / float18;

            for (int int1 = 0; int1 < 2; int1++) {
                float float21 = -(float12 + (int1 == 0 ? arg1 : arg2)) * float20;
                if (float21 >= 0.0F && float21 <= 1.0F) {
                    boolean0 = true;
                    float float22 = float11 + float21 * float17;
                    float float23 = float13 + float21 * float19;
                    float10 = 1.0F / (arg0.m03() * float22 + arg0.m23() * float23 + arg0.m33());
                    float float24 = (arg0.m00() * float22 + arg0.m20() * float23 + arg0.m30()) * float10;
                    float float25 = (arg0.m01() * float22 + arg0.m21() * float23 + arg0.m31()) * float10;
                    float0 = float0 < float24 ? float0 : float24;
                    float1 = float1 < float25 ? float1 : float25;
                    float2 = float2 > float24 ? float2 : float24;
                    float3 = float3 > float25 ? float3 : float25;
                }
            }
        }

        if (!boolean0) {
            return null;
        } else {
            arg3.set(float2 - float0, 0.0F, 0.0F, 0.0F, 0.0F, float3 - float1, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, float0, float1, 0.0F, 1.0F);
            arg3._properties(2);
            return arg3;
        }
    }

    @Override
    public Matrix4f perspectiveFrustumSlice(float arg0, float arg1, Matrix4f arg2) {
        float float0 = (this.m23 + this.m22) / this.m32;
        float float1 = 1.0F / (arg0 - arg1);
        arg2._m00(this.m00 * float0 * arg0)
            ._m01(this.m01)
            ._m02(this.m02)
            ._m03(this.m03)
            ._m10(this.m10)
            ._m11(this.m11 * float0 * arg0)
            ._m12(this.m12)
            ._m13(this.m13)
            ._m20(this.m20)
            ._m21(this.m21)
            ._m22((arg1 + arg0) * float1)
            ._m23(this.m23)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32((arg1 + arg1) * arg0 * float1)
            ._m33(this.m33)
            ._properties(this.properties & -29);
        return arg2;
    }

    @Override
    public Matrix4f orthoCrop(Matrix4fc arg0, Matrix4f arg1) {
        float float0 = Float.POSITIVE_INFINITY;
        float float1 = Float.NEGATIVE_INFINITY;
        float float2 = Float.POSITIVE_INFINITY;
        float float3 = Float.NEGATIVE_INFINITY;
        float float4 = Float.POSITIVE_INFINITY;
        float float5 = Float.NEGATIVE_INFINITY;

        for (int int0 = 0; int0 < 8; int0++) {
            float float6 = ((int0 & 1) << 1) - 1.0F;
            float float7 = ((int0 >>> 1 & 1) << 1) - 1.0F;
            float float8 = ((int0 >>> 2 & 1) << 1) - 1.0F;
            float float9 = 1.0F / (this.m03 * float6 + this.m13 * float7 + this.m23 * float8 + this.m33);
            float float10 = (this.m00 * float6 + this.m10 * float7 + this.m20 * float8 + this.m30) * float9;
            float float11 = (this.m01 * float6 + this.m11 * float7 + this.m21 * float8 + this.m31) * float9;
            float float12 = (this.m02 * float6 + this.m12 * float7 + this.m22 * float8 + this.m32) * float9;
            float9 = 1.0F / (arg0.m03() * float10 + arg0.m13() * float11 + arg0.m23() * float12 + arg0.m33());
            float float13 = arg0.m00() * float10 + arg0.m10() * float11 + arg0.m20() * float12 + arg0.m30();
            float float14 = arg0.m01() * float10 + arg0.m11() * float11 + arg0.m21() * float12 + arg0.m31();
            float float15 = (arg0.m02() * float10 + arg0.m12() * float11 + arg0.m22() * float12 + arg0.m32()) * float9;
            float0 = float0 < float13 ? float0 : float13;
            float1 = float1 > float13 ? float1 : float13;
            float2 = float2 < float14 ? float2 : float14;
            float3 = float3 > float14 ? float3 : float14;
            float4 = float4 < float15 ? float4 : float15;
            float5 = float5 > float15 ? float5 : float15;
        }

        return arg1.setOrtho(float0, float1, float2, float3, -float5, -float4);
    }

    public Matrix4f trapezoidCrop(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7) {
        float float0 = arg3 - arg1;
        float float1 = arg0 - arg2;
        float float2 = -float0;
        float float3 = float0 * arg1 - float1 * arg0;
        float float4 = -(float0 * arg0 + float1 * arg1);
        float float5 = float1 * arg6 + float2 * arg7 + float3;
        float float6 = float0 * arg6 + float1 * arg7 + float4;
        float float7 = -float5 / float6;
        float float8 = float1 + float7 * float0;
        float2 += float7 * float1;
        float3 += float7 * float4;
        float float9 = float8 * arg2 + float2 * arg3 + float3;
        float float10 = float8 * arg4 + float2 * arg5 + float3;
        float float11 = float9 * float6 / (float10 - float9);
        float4 += float11;
        float float12 = 2.0F / float10;
        float float13 = 1.0F / (float6 + float11);
        float float14 = (float13 + float13) * float11 / (1.0F - float13 * float11);
        float float15 = float0 * float13;
        float float16 = float1 * float13;
        float float17 = float4 * float13;
        float float18 = (float14 + 1.0F) * float15;
        float float19 = (float14 + 1.0F) * float16;
        float4 = (float14 + 1.0F) * float17 - float14;
        float8 = float12 * float8 - float15;
        float2 = float12 * float2 - float16;
        float3 = float12 * float3 - float17;
        this.set(float8, float18, 0.0F, float15, float2, float19, 0.0F, float16, 0.0F, 0.0F, 1.0F, 0.0F, float3, float4, 0.0F, float17);
        this._properties(0);
        return this;
    }

    @Override
    public Matrix4f transformAab(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Vector3f arg6, Vector3f arg7) {
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
    public Matrix4f transformAab(Vector3fc arg0, Vector3fc arg1, Vector3f arg2, Vector3f arg3) {
        return this.transformAab(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2, arg3);
    }

    public Matrix4f lerp(Matrix4fc arg0, float arg1) {
        return this.lerp(arg0, arg1, this);
    }

    @Override
    public Matrix4f lerp(Matrix4fc arg0, float arg1, Matrix4f arg2) {
        arg2._m00(Math.fma(arg0.m00() - this.m00, arg1, this.m00))
            ._m01(Math.fma(arg0.m01() - this.m01, arg1, this.m01))
            ._m02(Math.fma(arg0.m02() - this.m02, arg1, this.m02))
            ._m03(Math.fma(arg0.m03() - this.m03, arg1, this.m03))
            ._m10(Math.fma(arg0.m10() - this.m10, arg1, this.m10))
            ._m11(Math.fma(arg0.m11() - this.m11, arg1, this.m11))
            ._m12(Math.fma(arg0.m12() - this.m12, arg1, this.m12))
            ._m13(Math.fma(arg0.m13() - this.m13, arg1, this.m13))
            ._m20(Math.fma(arg0.m20() - this.m20, arg1, this.m20))
            ._m21(Math.fma(arg0.m21() - this.m21, arg1, this.m21))
            ._m22(Math.fma(arg0.m22() - this.m22, arg1, this.m22))
            ._m23(Math.fma(arg0.m23() - this.m23, arg1, this.m23))
            ._m30(Math.fma(arg0.m30() - this.m30, arg1, this.m30))
            ._m31(Math.fma(arg0.m31() - this.m31, arg1, this.m31))
            ._m32(Math.fma(arg0.m32() - this.m32, arg1, this.m32))
            ._m33(Math.fma(arg0.m33() - this.m33, arg1, this.m33))
            ._properties(this.properties & arg0.properties());
        return arg2;
    }

    @Override
    public Matrix4f rotateTowards(Vector3fc arg0, Vector3fc arg1, Matrix4f arg2) {
        return this.rotateTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    public Matrix4f rotateTowards(Vector3fc arg0, Vector3fc arg1) {
        return this.rotateTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), this);
    }

    public Matrix4f rotateTowards(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.rotateTowards(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    @Override
    public Matrix4f rotateTowards(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6) {
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
        arg6._m30(this.m30)._m31(this.m31)._m32(this.m32)._m33(this.m33);
        float float11 = this.m00 * float4 + this.m10 * float5 + this.m20 * float6;
        float float12 = this.m01 * float4 + this.m11 * float5 + this.m21 * float6;
        float float13 = this.m02 * float4 + this.m12 * float5 + this.m22 * float6;
        float float14 = this.m03 * float4 + this.m13 * float5 + this.m23 * float6;
        float float15 = this.m00 * float8 + this.m10 * float9 + this.m20 * float10;
        float float16 = this.m01 * float8 + this.m11 * float9 + this.m21 * float10;
        float float17 = this.m02 * float8 + this.m12 * float9 + this.m22 * float10;
        float float18 = this.m03 * float8 + this.m13 * float9 + this.m23 * float10;
        arg6._m20(this.m00 * float1 + this.m10 * float2 + this.m20 * float3)
            ._m21(this.m01 * float1 + this.m11 * float2 + this.m21 * float3)
            ._m22(this.m02 * float1 + this.m12 * float2 + this.m22 * float3)
            ._m23(this.m03 * float1 + this.m13 * float2 + this.m23 * float3)
            ._m00(float11)
            ._m01(float12)
            ._m02(float13)
            ._m03(float14)
            ._m10(float15)
            ._m11(float16)
            ._m12(float17)
            ._m13(float18)
            ._properties(this.properties & -14);
        return arg6;
    }

    public Matrix4f rotationTowards(Vector3fc arg0, Vector3fc arg1) {
        return this.rotationTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix4f rotationTowards(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
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
        if ((this.properties & 4) == 0) {
            MemUtil.INSTANCE.identity(this);
        }

        this._m00(float4)._m01(float5)._m02(float6)._m10(float8)._m11(float9)._m12(float10)._m20(float1)._m21(float2)._m22(float3)._properties(18);
        return this;
    }

    public Matrix4f translationRotateTowards(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2) {
        return this.translationRotateTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z());
    }

    public Matrix4f translationRotateTowards(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8) {
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
        this._m00(float4)
            ._m01(float5)
            ._m02(float6)
            ._m03(0.0F)
            ._m10(float8)
            ._m11(float9)
            ._m12(float10)
            ._m13(0.0F)
            ._m20(float1)
            ._m21(float2)
            ._m22(float3)
            ._m23(0.0F)
            ._m30(arg0)
            ._m31(arg1)
            ._m32(arg2)
            ._m33(1.0F)
            ._properties(18);
        return this;
    }

    @Override
    public Vector3f getEulerAnglesZYX(Vector3f arg0) {
        arg0.x = Math.atan2(this.m12, this.m22);
        arg0.y = Math.atan2(-this.m02, Math.sqrt(this.m12 * this.m12 + this.m22 * this.m22));
        arg0.z = Math.atan2(this.m01, this.m00);
        return arg0;
    }

    public Matrix4f affineSpan(Vector3f arg0, Vector3f arg1, Vector3f arg2, Vector3f arg3) {
        float float0 = this.m10 * this.m22;
        float float1 = this.m10 * this.m21;
        float float2 = this.m10 * this.m02;
        float float3 = this.m10 * this.m01;
        float float4 = this.m11 * this.m22;
        float float5 = this.m11 * this.m20;
        float float6 = this.m11 * this.m02;
        float float7 = this.m11 * this.m00;
        float float8 = this.m12 * this.m21;
        float float9 = this.m12 * this.m20;
        float float10 = this.m12 * this.m01;
        float float11 = this.m12 * this.m00;
        float float12 = this.m20 * this.m02;
        float float13 = this.m20 * this.m01;
        float float14 = this.m21 * this.m02;
        float float15 = this.m21 * this.m00;
        float float16 = this.m22 * this.m01;
        float float17 = this.m22 * this.m00;
        float float18 = 1.0F / (this.m00 * this.m11 - this.m01 * this.m10) * this.m22
            + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21
            + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
        float float19 = (float4 - float8) * float18;
        float float20 = (float14 - float16) * float18;
        float float21 = (float10 - float6) * float18;
        float float22 = (float9 - float0) * float18;
        float float23 = (float17 - float12) * float18;
        float float24 = (float2 - float11) * float18;
        float float25 = (float1 - float5) * float18;
        float float26 = (float13 - float15) * float18;
        float float27 = (float7 - float3) * float18;
        arg0.x = -float19
            - float22
            - float25
            + (float0 * this.m31 - float1 * this.m32 + float5 * this.m32 - float4 * this.m30 + float8 * this.m30 - float9 * this.m31) * float18;
        arg0.y = -float20
            - float23
            - float26
            + (float12 * this.m31 - float13 * this.m32 + float15 * this.m32 - float14 * this.m30 + float16 * this.m30 - float17 * this.m31) * float18;
        arg0.z = -float21
            - float24
            - float27
            + (float6 * this.m30 - float10 * this.m30 + float11 * this.m31 - float2 * this.m31 + float3 * this.m32 - float7 * this.m32) * float18;
        arg1.x = 2.0F * float19;
        arg1.y = 2.0F * float20;
        arg1.z = 2.0F * float21;
        arg2.x = 2.0F * float22;
        arg2.y = 2.0F * float23;
        arg2.z = 2.0F * float24;
        arg3.x = 2.0F * float25;
        arg3.y = 2.0F * float26;
        arg3.z = 2.0F * float27;
        return this;
    }

    @Override
    public boolean testPoint(float arg0, float arg1, float arg2) {
        float float0 = this.m03 + this.m00;
        float float1 = this.m13 + this.m10;
        float float2 = this.m23 + this.m20;
        float float3 = this.m33 + this.m30;
        float float4 = this.m03 - this.m00;
        float float5 = this.m13 - this.m10;
        float float6 = this.m23 - this.m20;
        float float7 = this.m33 - this.m30;
        float float8 = this.m03 + this.m01;
        float float9 = this.m13 + this.m11;
        float float10 = this.m23 + this.m21;
        float float11 = this.m33 + this.m31;
        float float12 = this.m03 - this.m01;
        float float13 = this.m13 - this.m11;
        float float14 = this.m23 - this.m21;
        float float15 = this.m33 - this.m31;
        float float16 = this.m03 + this.m02;
        float float17 = this.m13 + this.m12;
        float float18 = this.m23 + this.m22;
        float float19 = this.m33 + this.m32;
        float float20 = this.m03 - this.m02;
        float float21 = this.m13 - this.m12;
        float float22 = this.m23 - this.m22;
        float float23 = this.m33 - this.m32;
        return float0 * arg0 + float1 * arg1 + float2 * arg2 + float3 >= 0.0F
            && float4 * arg0 + float5 * arg1 + float6 * arg2 + float7 >= 0.0F
            && float8 * arg0 + float9 * arg1 + float10 * arg2 + float11 >= 0.0F
            && float12 * arg0 + float13 * arg1 + float14 * arg2 + float15 >= 0.0F
            && float16 * arg0 + float17 * arg1 + float18 * arg2 + float19 >= 0.0F
            && float20 * arg0 + float21 * arg1 + float22 * arg2 + float23 >= 0.0F;
    }

    @Override
    public boolean testSphere(float arg0, float arg1, float arg2, float arg3) {
        float float0 = this.m03 + this.m00;
        float float1 = this.m13 + this.m10;
        float float2 = this.m23 + this.m20;
        float float3 = this.m33 + this.m30;
        float float4 = Math.invsqrt(float0 * float0 + float1 * float1 + float2 * float2);
        float0 *= float4;
        float1 *= float4;
        float2 *= float4;
        float3 *= float4;
        float float5 = this.m03 - this.m00;
        float float6 = this.m13 - this.m10;
        float float7 = this.m23 - this.m20;
        float float8 = this.m33 - this.m30;
        float4 = Math.invsqrt(float5 * float5 + float6 * float6 + float7 * float7);
        float5 *= float4;
        float6 *= float4;
        float7 *= float4;
        float8 *= float4;
        float float9 = this.m03 + this.m01;
        float float10 = this.m13 + this.m11;
        float float11 = this.m23 + this.m21;
        float float12 = this.m33 + this.m31;
        float4 = Math.invsqrt(float9 * float9 + float10 * float10 + float11 * float11);
        float9 *= float4;
        float10 *= float4;
        float11 *= float4;
        float12 *= float4;
        float float13 = this.m03 - this.m01;
        float float14 = this.m13 - this.m11;
        float float15 = this.m23 - this.m21;
        float float16 = this.m33 - this.m31;
        float4 = Math.invsqrt(float13 * float13 + float14 * float14 + float15 * float15);
        float13 *= float4;
        float14 *= float4;
        float15 *= float4;
        float16 *= float4;
        float float17 = this.m03 + this.m02;
        float float18 = this.m13 + this.m12;
        float float19 = this.m23 + this.m22;
        float float20 = this.m33 + this.m32;
        float4 = Math.invsqrt(float17 * float17 + float18 * float18 + float19 * float19);
        float17 *= float4;
        float18 *= float4;
        float19 *= float4;
        float20 *= float4;
        float float21 = this.m03 - this.m02;
        float float22 = this.m13 - this.m12;
        float float23 = this.m23 - this.m22;
        float float24 = this.m33 - this.m32;
        float4 = Math.invsqrt(float21 * float21 + float22 * float22 + float23 * float23);
        float21 *= float4;
        float22 *= float4;
        float23 *= float4;
        float24 *= float4;
        return float0 * arg0 + float1 * arg1 + float2 * arg2 + float3 >= -arg3
            && float5 * arg0 + float6 * arg1 + float7 * arg2 + float8 >= -arg3
            && float9 * arg0 + float10 * arg1 + float11 * arg2 + float12 >= -arg3
            && float13 * arg0 + float14 * arg1 + float15 * arg2 + float16 >= -arg3
            && float17 * arg0 + float18 * arg1 + float19 * arg2 + float20 >= -arg3
            && float21 * arg0 + float22 * arg1 + float23 * arg2 + float24 >= -arg3;
    }

    @Override
    public boolean testAab(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        float float0 = this.m03 + this.m00;
        float float1 = this.m13 + this.m10;
        float float2 = this.m23 + this.m20;
        float float3 = this.m33 + this.m30;
        float float4 = this.m03 - this.m00;
        float float5 = this.m13 - this.m10;
        float float6 = this.m23 - this.m20;
        float float7 = this.m33 - this.m30;
        float float8 = this.m03 + this.m01;
        float float9 = this.m13 + this.m11;
        float float10 = this.m23 + this.m21;
        float float11 = this.m33 + this.m31;
        float float12 = this.m03 - this.m01;
        float float13 = this.m13 - this.m11;
        float float14 = this.m23 - this.m21;
        float float15 = this.m33 - this.m31;
        float float16 = this.m03 + this.m02;
        float float17 = this.m13 + this.m12;
        float float18 = this.m23 + this.m22;
        float float19 = this.m33 + this.m32;
        float float20 = this.m03 - this.m02;
        float float21 = this.m13 - this.m12;
        float float22 = this.m23 - this.m22;
        float float23 = this.m33 - this.m32;
        return float0 * (float0 < 0.0F ? arg0 : arg3) + float1 * (float1 < 0.0F ? arg1 : arg4) + float2 * (float2 < 0.0F ? arg2 : arg5) >= -float3
            && float4 * (float4 < 0.0F ? arg0 : arg3) + float5 * (float5 < 0.0F ? arg1 : arg4) + float6 * (float6 < 0.0F ? arg2 : arg5) >= -float7
            && float8 * (float8 < 0.0F ? arg0 : arg3) + float9 * (float9 < 0.0F ? arg1 : arg4) + float10 * (float10 < 0.0F ? arg2 : arg5) >= -float11
            && float12 * (float12 < 0.0F ? arg0 : arg3) + float13 * (float13 < 0.0F ? arg1 : arg4) + float14 * (float14 < 0.0F ? arg2 : arg5) >= -float15
            && float16 * (float16 < 0.0F ? arg0 : arg3) + float17 * (float17 < 0.0F ? arg1 : arg4) + float18 * (float18 < 0.0F ? arg2 : arg5) >= -float19
            && float20 * (float20 < 0.0F ? arg0 : arg3) + float21 * (float21 < 0.0F ? arg1 : arg4) + float22 * (float22 < 0.0F ? arg2 : arg5) >= -float23;
    }

    public Matrix4f obliqueZ(float arg0, float arg1) {
        this.m20 = this.m00 * arg0 + this.m10 * arg1 + this.m20;
        this.m21 = this.m01 * arg0 + this.m11 * arg1 + this.m21;
        this.m22 = this.m02 * arg0 + this.m12 * arg1 + this.m22;
        this._properties(this.properties & 2);
        return this;
    }

    @Override
    public Matrix4f obliqueZ(float arg0, float arg1, Matrix4f arg2) {
        arg2._m00(this.m00)
            ._m01(this.m01)
            ._m02(this.m02)
            ._m03(this.m03)
            ._m10(this.m10)
            ._m11(this.m11)
            ._m12(this.m12)
            ._m13(this.m13)
            ._m20(this.m00 * arg0 + this.m10 * arg1 + this.m20)
            ._m21(this.m01 * arg0 + this.m11 * arg1 + this.m21)
            ._m22(this.m02 * arg0 + this.m12 * arg1 + this.m22)
            ._m23(this.m23)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & 2);
        return arg2;
    }

    public static void projViewFromRectangle(Vector3f arg0, Vector3f arg1, Vector3f arg2, Vector3f arg3, float arg4, boolean arg5, Matrix4f arg6, Matrix4f arg7) {
        float float0 = arg3.y * arg2.z - arg3.z * arg2.y;
        float float1 = arg3.z * arg2.x - arg3.x * arg2.z;
        float float2 = arg3.x * arg2.y - arg3.y * arg2.x;
        float float3 = float0 * (arg1.x - arg0.x) + float1 * (arg1.y - arg0.y) + float2 * (arg1.z - arg0.z);
        float float4 = float3 >= 0.0F ? 1.0F : -1.0F;
        float0 *= float4;
        float1 *= float4;
        float2 *= float4;
        float3 *= float4;
        arg7.setLookAt(arg0.x, arg0.y, arg0.z, arg0.x + float0, arg0.y + float1, arg0.z + float2, arg3.x, arg3.y, arg3.z);
        float float5 = arg7.m00 * arg1.x + arg7.m10 * arg1.y + arg7.m20 * arg1.z + arg7.m30;
        float float6 = arg7.m01 * arg1.x + arg7.m11 * arg1.y + arg7.m21 * arg1.z + arg7.m31;
        float float7 = arg7.m00 * arg2.x + arg7.m10 * arg2.y + arg7.m20 * arg2.z;
        float float8 = arg7.m01 * arg3.x + arg7.m11 * arg3.y + arg7.m21 * arg3.z;
        float float9 = Math.sqrt(float0 * float0 + float1 * float1 + float2 * float2);
        float float10 = float3 / float9;
        float float11;
        if (Float.isInfinite(arg4) && arg4 < 0.0F) {
            float11 = float10;
            float10 = Float.POSITIVE_INFINITY;
        } else if (Float.isInfinite(arg4) && arg4 > 0.0F) {
            float11 = Float.POSITIVE_INFINITY;
        } else if (arg4 < 0.0F) {
            float11 = float10;
            float10 += arg4;
        } else {
            float11 = float10 + arg4;
        }

        arg6.setFrustum(float5, float5 + float7, float6, float6 + float8, float10, float11, arg5);
    }

    public Matrix4f withLookAtUp(Vector3fc arg0) {
        return this.withLookAtUp(arg0.x(), arg0.y(), arg0.z(), this);
    }

    @Override
    public Matrix4f withLookAtUp(Vector3fc arg0, Matrix4f arg1) {
        return this.withLookAtUp(arg0.x(), arg0.y(), arg0.z());
    }

    public Matrix4f withLookAtUp(float arg0, float arg1, float arg2) {
        return this.withLookAtUp(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4f withLookAtUp(float arg0, float arg1, float arg2, Matrix4f arg3) {
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
            arg3._m02(this.m02)._m12(this.m12)._m22(this.m22)._m32(this.m32)._m03(this.m03)._m13(this.m13)._m23(this.m23)._m33(this.m33);
        }

        arg3._properties(this.properties & -14);
        return arg3;
    }

    @Override
    public boolean isFinite() {
        return Math.isFinite(this.m00)
            && Math.isFinite(this.m01)
            && Math.isFinite(this.m02)
            && Math.isFinite(this.m03)
            && Math.isFinite(this.m10)
            && Math.isFinite(this.m11)
            && Math.isFinite(this.m12)
            && Math.isFinite(this.m13)
            && Math.isFinite(this.m20)
            && Math.isFinite(this.m21)
            && Math.isFinite(this.m22)
            && Math.isFinite(this.m23)
            && Math.isFinite(this.m30)
            && Math.isFinite(this.m31)
            && Math.isFinite(this.m32)
            && Math.isFinite(this.m33);
    }
}
