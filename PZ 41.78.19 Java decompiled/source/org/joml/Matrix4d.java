// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Matrix4d implements Externalizable, Matrix4dc {
    private static final long serialVersionUID = 1L;
    double m00;
    double m01;
    double m02;
    double m03;
    double m10;
    double m11;
    double m12;
    double m13;
    double m20;
    double m21;
    double m22;
    double m23;
    double m30;
    double m31;
    double m32;
    double m33;
    int properties;

    public Matrix4d() {
        this._m00(1.0)._m11(1.0)._m22(1.0)._m33(1.0).properties = 30;
    }

    public Matrix4d(Matrix4dc arg0) {
        this.set(arg0);
    }

    public Matrix4d(Matrix4fc arg0) {
        this.set(arg0);
    }

    public Matrix4d(Matrix4x3dc arg0) {
        this.set(arg0);
    }

    public Matrix4d(Matrix4x3fc arg0) {
        this.set(arg0);
    }

    public Matrix4d(Matrix3dc arg0) {
        this.set(arg0);
    }

    public Matrix4d(
        double arg0,
        double arg1,
        double arg2,
        double arg3,
        double arg4,
        double arg5,
        double arg6,
        double arg7,
        double arg8,
        double arg9,
        double arg10,
        double arg11,
        double arg12,
        double arg13,
        double arg14,
        double arg15
    ) {
        this.m00 = arg0;
        this.m01 = arg1;
        this.m02 = arg2;
        this.m03 = arg3;
        this.m10 = arg4;
        this.m11 = arg5;
        this.m12 = arg6;
        this.m13 = arg7;
        this.m20 = arg8;
        this.m21 = arg9;
        this.m22 = arg10;
        this.m23 = arg11;
        this.m30 = arg12;
        this.m31 = arg13;
        this.m32 = arg14;
        this.m33 = arg15;
        this.determineProperties();
    }

    public Matrix4d(DoubleBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        this.determineProperties();
    }

    public Matrix4d(Vector4d arg0, Vector4d arg1, Vector4d arg2, Vector4d arg3) {
        this.set(arg0, arg1, arg2, arg3);
    }

    public Matrix4d assume(int arg0) {
        this.properties = (byte)arg0;
        return this;
    }

    public Matrix4d determineProperties() {
        byte byte0 = 0;
        if (this.m03 == 0.0 && this.m13 == 0.0) {
            if (this.m23 == 0.0 && this.m33 == 1.0) {
                byte0 |= 2;
                if (this.m00 == 1.0
                    && this.m01 == 0.0
                    && this.m02 == 0.0
                    && this.m10 == 0.0
                    && this.m11 == 1.0
                    && this.m12 == 0.0
                    && this.m20 == 0.0
                    && this.m21 == 0.0
                    && this.m22 == 1.0) {
                    byte0 |= 24;
                    if (this.m30 == 0.0 && this.m31 == 0.0 && this.m32 == 0.0) {
                        byte0 |= 4;
                    }
                }
            } else if (this.m01 == 0.0
                && this.m02 == 0.0
                && this.m10 == 0.0
                && this.m12 == 0.0
                && this.m20 == 0.0
                && this.m21 == 0.0
                && this.m30 == 0.0
                && this.m31 == 0.0
                && this.m33 == 0.0) {
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
    public double m00() {
        return this.m00;
    }

    @Override
    public double m01() {
        return this.m01;
    }

    @Override
    public double m02() {
        return this.m02;
    }

    @Override
    public double m03() {
        return this.m03;
    }

    @Override
    public double m10() {
        return this.m10;
    }

    @Override
    public double m11() {
        return this.m11;
    }

    @Override
    public double m12() {
        return this.m12;
    }

    @Override
    public double m13() {
        return this.m13;
    }

    @Override
    public double m20() {
        return this.m20;
    }

    @Override
    public double m21() {
        return this.m21;
    }

    @Override
    public double m22() {
        return this.m22;
    }

    @Override
    public double m23() {
        return this.m23;
    }

    @Override
    public double m30() {
        return this.m30;
    }

    @Override
    public double m31() {
        return this.m31;
    }

    @Override
    public double m32() {
        return this.m32;
    }

    @Override
    public double m33() {
        return this.m33;
    }

    public Matrix4d m00(double arg0) {
        this.m00 = arg0;
        this.properties &= -17;
        if (arg0 != 1.0) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4d m01(double arg0) {
        this.m01 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0) {
            this.properties &= -14;
        }

        return this;
    }

    public Matrix4d m02(double arg0) {
        this.m02 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0) {
            this.properties &= -14;
        }

        return this;
    }

    public Matrix4d m03(double arg0) {
        this.m03 = arg0;
        if (arg0 != 0.0) {
            this.properties = 0;
        }

        return this;
    }

    public Matrix4d m10(double arg0) {
        this.m10 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0) {
            this.properties &= -14;
        }

        return this;
    }

    public Matrix4d m11(double arg0) {
        this.m11 = arg0;
        this.properties &= -17;
        if (arg0 != 1.0) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4d m12(double arg0) {
        this.m12 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0) {
            this.properties &= -14;
        }

        return this;
    }

    public Matrix4d m13(double arg0) {
        this.m13 = arg0;
        if (this.m03 != 0.0) {
            this.properties = 0;
        }

        return this;
    }

    public Matrix4d m20(double arg0) {
        this.m20 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0) {
            this.properties &= -14;
        }

        return this;
    }

    public Matrix4d m21(double arg0) {
        this.m21 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0) {
            this.properties &= -14;
        }

        return this;
    }

    public Matrix4d m22(double arg0) {
        this.m22 = arg0;
        this.properties &= -17;
        if (arg0 != 1.0) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4d m23(double arg0) {
        this.m23 = arg0;
        if (arg0 != 0.0) {
            this.properties &= -31;
        }

        return this;
    }

    public Matrix4d m30(double arg0) {
        this.m30 = arg0;
        if (arg0 != 0.0) {
            this.properties &= -6;
        }

        return this;
    }

    public Matrix4d m31(double arg0) {
        this.m31 = arg0;
        if (arg0 != 0.0) {
            this.properties &= -6;
        }

        return this;
    }

    public Matrix4d m32(double arg0) {
        this.m32 = arg0;
        if (arg0 != 0.0) {
            this.properties &= -6;
        }

        return this;
    }

    public Matrix4d m33(double arg0) {
        this.m33 = arg0;
        if (arg0 != 0.0) {
            this.properties &= -2;
        }

        if (arg0 != 1.0) {
            this.properties &= -31;
        }

        return this;
    }

    Matrix4d _properties(int int0) {
        this.properties = int0;
        return this;
    }

    Matrix4d _m00(double double0) {
        this.m00 = double0;
        return this;
    }

    Matrix4d _m01(double double0) {
        this.m01 = double0;
        return this;
    }

    Matrix4d _m02(double double0) {
        this.m02 = double0;
        return this;
    }

    Matrix4d _m03(double double0) {
        this.m03 = double0;
        return this;
    }

    Matrix4d _m10(double double0) {
        this.m10 = double0;
        return this;
    }

    Matrix4d _m11(double double0) {
        this.m11 = double0;
        return this;
    }

    Matrix4d _m12(double double0) {
        this.m12 = double0;
        return this;
    }

    Matrix4d _m13(double double0) {
        this.m13 = double0;
        return this;
    }

    Matrix4d _m20(double double0) {
        this.m20 = double0;
        return this;
    }

    Matrix4d _m21(double double0) {
        this.m21 = double0;
        return this;
    }

    Matrix4d _m22(double double0) {
        this.m22 = double0;
        return this;
    }

    Matrix4d _m23(double double0) {
        this.m23 = double0;
        return this;
    }

    Matrix4d _m30(double double0) {
        this.m30 = double0;
        return this;
    }

    Matrix4d _m31(double double0) {
        this.m31 = double0;
        return this;
    }

    Matrix4d _m32(double double0) {
        this.m32 = double0;
        return this;
    }

    Matrix4d _m33(double double0) {
        this.m33 = double0;
        return this;
    }

    public Matrix4d identity() {
        if ((this.properties & 4) != 0) {
            return this;
        } else {
            this._identity();
            this.properties = 30;
            return this;
        }
    }

    private void _identity() {
        this._m00(1.0)
            ._m10(0.0)
            ._m20(0.0)
            ._m30(0.0)
            ._m01(0.0)
            ._m11(1.0)
            ._m21(0.0)
            ._m31(0.0)
            ._m02(0.0)
            ._m12(0.0)
            ._m22(1.0)
            ._m32(0.0)
            ._m03(0.0)
            ._m13(0.0)
            ._m23(0.0)
            ._m33(1.0);
    }

    public Matrix4d set(Matrix4dc arg0) {
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

    public Matrix4d set(Matrix4fc arg0) {
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

    public Matrix4d setTransposed(Matrix4dc arg0) {
        return (arg0.properties() & 4) != 0 ? this.identity() : this.setTransposedInternal(arg0);
    }

    private Matrix4d setTransposedInternal(Matrix4dc matrix4dc) {
        double double0 = matrix4dc.m01();
        double double1 = matrix4dc.m21();
        double double2 = matrix4dc.m31();
        double double3 = matrix4dc.m02();
        double double4 = matrix4dc.m12();
        double double5 = matrix4dc.m03();
        double double6 = matrix4dc.m13();
        double double7 = matrix4dc.m23();
        return this._m00(matrix4dc.m00())
            ._m01(matrix4dc.m10())
            ._m02(matrix4dc.m20())
            ._m03(matrix4dc.m30())
            ._m10(double0)
            ._m11(matrix4dc.m11())
            ._m12(double1)
            ._m13(double2)
            ._m20(double3)
            ._m21(double4)
            ._m22(matrix4dc.m22())
            ._m23(matrix4dc.m32())
            ._m30(double5)
            ._m31(double6)
            ._m32(double7)
            ._m33(matrix4dc.m33())
            ._properties(matrix4dc.properties() & 4);
    }

    public Matrix4d set(Matrix4x3dc arg0) {
        return this._m00(arg0.m00())
            ._m01(arg0.m01())
            ._m02(arg0.m02())
            ._m03(0.0)
            ._m10(arg0.m10())
            ._m11(arg0.m11())
            ._m12(arg0.m12())
            ._m13(0.0)
            ._m20(arg0.m20())
            ._m21(arg0.m21())
            ._m22(arg0.m22())
            ._m23(0.0)
            ._m30(arg0.m30())
            ._m31(arg0.m31())
            ._m32(arg0.m32())
            ._m33(1.0)
            ._properties(arg0.properties() | 2);
    }

    public Matrix4d set(Matrix4x3fc arg0) {
        return this._m00(arg0.m00())
            ._m01(arg0.m01())
            ._m02(arg0.m02())
            ._m03(0.0)
            ._m10(arg0.m10())
            ._m11(arg0.m11())
            ._m12(arg0.m12())
            ._m13(0.0)
            ._m20(arg0.m20())
            ._m21(arg0.m21())
            ._m22(arg0.m22())
            ._m23(0.0)
            ._m30(arg0.m30())
            ._m31(arg0.m31())
            ._m32(arg0.m32())
            ._m33(1.0)
            ._properties(arg0.properties() | 2);
    }

    public Matrix4d set(Matrix3dc arg0) {
        return this._m00(arg0.m00())
            ._m01(arg0.m01())
            ._m02(arg0.m02())
            ._m03(0.0)
            ._m10(arg0.m10())
            ._m11(arg0.m11())
            ._m12(arg0.m12())
            ._m13(0.0)
            ._m20(arg0.m20())
            ._m21(arg0.m21())
            ._m22(arg0.m22())
            ._m23(0.0)
            ._m30(0.0)
            ._m31(0.0)
            ._m32(0.0)
            ._m33(1.0)
            ._properties(2);
    }

    public Matrix4d set3x3(Matrix4dc arg0) {
        return this._m00(arg0.m00())
            ._m01(arg0.m01())
            ._m02(arg0.m02())
            ._m10(arg0.m10())
            ._m11(arg0.m11())
            ._m12(arg0.m12())
            ._m20(arg0.m20())
            ._m21(arg0.m21())
            ._m22(arg0.m22())
            ._properties(this.properties & arg0.properties() & -2);
    }

    public Matrix4d set4x3(Matrix4x3dc arg0) {
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

    public Matrix4d set4x3(Matrix4x3fc arg0) {
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

    public Matrix4d set4x3(Matrix4dc arg0) {
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

    public Matrix4d set(AxisAngle4f arg0) {
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
        this._m00(double6 + double0 * double0 * double7)._m11(double6 + double1 * double1 * double7)._m22(double6 + double2 * double2 * double7);
        double double8 = double0 * double1 * double7;
        double double9 = double2 * double5;
        this._m10(double8 - double9)._m01(double8 + double9);
        double8 = double0 * double2 * double7;
        double9 = double1 * double5;
        this._m20(double8 + double9)._m02(double8 - double9);
        double8 = double1 * double2 * double7;
        double9 = double0 * double5;
        this._m21(double8 - double9)._m12(double8 + double9)._m03(0.0)._m13(0.0)._m23(0.0)._m30(0.0)._m31(0.0)._m32(0.0)._m33(1.0).properties = 18;
        return this;
    }

    public Matrix4d set(AxisAngle4d arg0) {
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
        this._m00(double6 + double0 * double0 * double7)._m11(double6 + double1 * double1 * double7)._m22(double6 + double2 * double2 * double7);
        double double8 = double0 * double1 * double7;
        double double9 = double2 * double5;
        this._m10(double8 - double9)._m01(double8 + double9);
        double8 = double0 * double2 * double7;
        double9 = double1 * double5;
        this._m20(double8 + double9)._m02(double8 - double9);
        double8 = double1 * double2 * double7;
        double9 = double0 * double5;
        this._m21(double8 - double9)._m12(double8 + double9)._m03(0.0)._m13(0.0)._m23(0.0)._m30(0.0)._m31(0.0)._m32(0.0)._m33(1.0).properties = 18;
        return this;
    }

    public Matrix4d set(Quaternionfc arg0) {
        return this.rotation(arg0);
    }

    public Matrix4d set(Quaterniondc arg0) {
        return this.rotation(arg0);
    }

    public Matrix4d mul(Matrix4dc arg0) {
        return this.mul(arg0, this);
    }

    @Override
    public Matrix4d mul(Matrix4dc arg0, Matrix4d arg1) {
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

    public Matrix4d mul0(Matrix4dc arg0) {
        return this.mul0(arg0, this);
    }

    @Override
    public Matrix4d mul0(Matrix4dc arg0, Matrix4d arg1) {
        double double0 = Math.fma(this.m00, arg0.m00(), Math.fma(this.m10, arg0.m01(), Math.fma(this.m20, arg0.m02(), this.m30 * arg0.m03())));
        double double1 = Math.fma(this.m01, arg0.m00(), Math.fma(this.m11, arg0.m01(), Math.fma(this.m21, arg0.m02(), this.m31 * arg0.m03())));
        double double2 = Math.fma(this.m02, arg0.m00(), Math.fma(this.m12, arg0.m01(), Math.fma(this.m22, arg0.m02(), this.m32 * arg0.m03())));
        double double3 = Math.fma(this.m03, arg0.m00(), Math.fma(this.m13, arg0.m01(), Math.fma(this.m23, arg0.m02(), this.m33 * arg0.m03())));
        double double4 = Math.fma(this.m00, arg0.m10(), Math.fma(this.m10, arg0.m11(), Math.fma(this.m20, arg0.m12(), this.m30 * arg0.m13())));
        double double5 = Math.fma(this.m01, arg0.m10(), Math.fma(this.m11, arg0.m11(), Math.fma(this.m21, arg0.m12(), this.m31 * arg0.m13())));
        double double6 = Math.fma(this.m02, arg0.m10(), Math.fma(this.m12, arg0.m11(), Math.fma(this.m22, arg0.m12(), this.m32 * arg0.m13())));
        double double7 = Math.fma(this.m03, arg0.m10(), Math.fma(this.m13, arg0.m11(), Math.fma(this.m23, arg0.m12(), this.m33 * arg0.m13())));
        double double8 = Math.fma(this.m00, arg0.m20(), Math.fma(this.m10, arg0.m21(), Math.fma(this.m20, arg0.m22(), this.m30 * arg0.m23())));
        double double9 = Math.fma(this.m01, arg0.m20(), Math.fma(this.m11, arg0.m21(), Math.fma(this.m21, arg0.m22(), this.m31 * arg0.m23())));
        double double10 = Math.fma(this.m02, arg0.m20(), Math.fma(this.m12, arg0.m21(), Math.fma(this.m22, arg0.m22(), this.m32 * arg0.m23())));
        double double11 = Math.fma(this.m03, arg0.m20(), Math.fma(this.m13, arg0.m21(), Math.fma(this.m23, arg0.m22(), this.m33 * arg0.m23())));
        double double12 = Math.fma(this.m00, arg0.m30(), Math.fma(this.m10, arg0.m31(), Math.fma(this.m20, arg0.m32(), this.m30 * arg0.m33())));
        double double13 = Math.fma(this.m01, arg0.m30(), Math.fma(this.m11, arg0.m31(), Math.fma(this.m21, arg0.m32(), this.m31 * arg0.m33())));
        double double14 = Math.fma(this.m02, arg0.m30(), Math.fma(this.m12, arg0.m31(), Math.fma(this.m22, arg0.m32(), this.m32 * arg0.m33())));
        double double15 = Math.fma(this.m03, arg0.m30(), Math.fma(this.m13, arg0.m31(), Math.fma(this.m23, arg0.m32(), this.m33 * arg0.m33())));
        return arg1._m00(double0)
            ._m01(double1)
            ._m02(double2)
            ._m03(double3)
            ._m10(double4)
            ._m11(double5)
            ._m12(double6)
            ._m13(double7)
            ._m20(double8)
            ._m21(double9)
            ._m22(double10)
            ._m23(double11)
            ._m30(double12)
            ._m31(double13)
            ._m32(double14)
            ._m33(double15)
            ._properties(0);
    }

    public Matrix4d mul(
        double arg0,
        double arg1,
        double arg2,
        double arg3,
        double arg4,
        double arg5,
        double arg6,
        double arg7,
        double arg8,
        double arg9,
        double arg10,
        double arg11,
        double arg12,
        double arg13,
        double arg14,
        double arg15
    ) {
        return this.mul(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, this);
    }

    @Override
    public Matrix4d mul(
        double arg0,
        double arg1,
        double arg2,
        double arg3,
        double arg4,
        double arg5,
        double arg6,
        double arg7,
        double arg8,
        double arg9,
        double arg10,
        double arg11,
        double arg12,
        double arg13,
        double arg14,
        double arg15,
        Matrix4d arg16
    ) {
        if ((this.properties & 4) != 0) {
            return arg16.set(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
        } else {
            return (this.properties & 2) != 0
                ? this.mulAffineL(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16)
                : this.mulGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);
        }
    }

    private Matrix4d mulAffineL(
        double double1,
        double double2,
        double double3,
        double double4,
        double double8,
        double double9,
        double double10,
        double double11,
        double double15,
        double double16,
        double double17,
        double double18,
        double double22,
        double double23,
        double double24,
        double double25,
        Matrix4d matrix4d1
    ) {
        double double0 = Math.fma(this.m00, double1, Math.fma(this.m10, double2, Math.fma(this.m20, double3, this.m30 * double4)));
        double double5 = Math.fma(this.m01, double1, Math.fma(this.m11, double2, Math.fma(this.m21, double3, this.m31 * double4)));
        double double6 = Math.fma(this.m02, double1, Math.fma(this.m12, double2, Math.fma(this.m22, double3, this.m32 * double4)));
        double double7 = Math.fma(this.m00, double8, Math.fma(this.m10, double9, Math.fma(this.m20, double10, this.m30 * double11)));
        double double12 = Math.fma(this.m01, double8, Math.fma(this.m11, double9, Math.fma(this.m21, double10, this.m31 * double11)));
        double double13 = Math.fma(this.m02, double8, Math.fma(this.m12, double9, Math.fma(this.m22, double10, this.m32 * double11)));
        double double14 = Math.fma(this.m00, double15, Math.fma(this.m10, double16, Math.fma(this.m20, double17, this.m30 * double18)));
        double double19 = Math.fma(this.m01, double15, Math.fma(this.m11, double16, Math.fma(this.m21, double17, this.m31 * double18)));
        double double20 = Math.fma(this.m02, double15, Math.fma(this.m12, double16, Math.fma(this.m22, double17, this.m32 * double18)));
        double double21 = Math.fma(this.m00, double22, Math.fma(this.m10, double23, Math.fma(this.m20, double24, this.m30 * double25)));
        double double26 = Math.fma(this.m01, double22, Math.fma(this.m11, double23, Math.fma(this.m21, double24, this.m31 * double25)));
        double double27 = Math.fma(this.m02, double22, Math.fma(this.m12, double23, Math.fma(this.m22, double24, this.m32 * double25)));
        return matrix4d1._m00(double0)
            ._m01(double5)
            ._m02(double6)
            ._m03(double4)
            ._m10(double7)
            ._m11(double12)
            ._m12(double13)
            ._m13(double11)
            ._m20(double14)
            ._m21(double19)
            ._m22(double20)
            ._m23(double18)
            ._m30(double21)
            ._m31(double26)
            ._m32(double27)
            ._m33(double25)
            ._properties(2);
    }

    private Matrix4d mulGeneric(
        double double1,
        double double2,
        double double3,
        double double4,
        double double9,
        double double10,
        double double11,
        double double12,
        double double17,
        double double18,
        double double19,
        double double20,
        double double25,
        double double26,
        double double27,
        double double28,
        Matrix4d matrix4d1
    ) {
        double double0 = Math.fma(this.m00, double1, Math.fma(this.m10, double2, Math.fma(this.m20, double3, this.m30 * double4)));
        double double5 = Math.fma(this.m01, double1, Math.fma(this.m11, double2, Math.fma(this.m21, double3, this.m31 * double4)));
        double double6 = Math.fma(this.m02, double1, Math.fma(this.m12, double2, Math.fma(this.m22, double3, this.m32 * double4)));
        double double7 = Math.fma(this.m03, double1, Math.fma(this.m13, double2, Math.fma(this.m23, double3, this.m33 * double4)));
        double double8 = Math.fma(this.m00, double9, Math.fma(this.m10, double10, Math.fma(this.m20, double11, this.m30 * double12)));
        double double13 = Math.fma(this.m01, double9, Math.fma(this.m11, double10, Math.fma(this.m21, double11, this.m31 * double12)));
        double double14 = Math.fma(this.m02, double9, Math.fma(this.m12, double10, Math.fma(this.m22, double11, this.m32 * double12)));
        double double15 = Math.fma(this.m03, double9, Math.fma(this.m13, double10, Math.fma(this.m23, double11, this.m33 * double12)));
        double double16 = Math.fma(this.m00, double17, Math.fma(this.m10, double18, Math.fma(this.m20, double19, this.m30 * double20)));
        double double21 = Math.fma(this.m01, double17, Math.fma(this.m11, double18, Math.fma(this.m21, double19, this.m31 * double20)));
        double double22 = Math.fma(this.m02, double17, Math.fma(this.m12, double18, Math.fma(this.m22, double19, this.m32 * double20)));
        double double23 = Math.fma(this.m03, double17, Math.fma(this.m13, double18, Math.fma(this.m23, double19, this.m33 * double20)));
        double double24 = Math.fma(this.m00, double25, Math.fma(this.m10, double26, Math.fma(this.m20, double27, this.m30 * double28)));
        double double29 = Math.fma(this.m01, double25, Math.fma(this.m11, double26, Math.fma(this.m21, double27, this.m31 * double28)));
        double double30 = Math.fma(this.m02, double25, Math.fma(this.m12, double26, Math.fma(this.m22, double27, this.m32 * double28)));
        double double31 = Math.fma(this.m03, double25, Math.fma(this.m13, double26, Math.fma(this.m23, double27, this.m33 * double28)));
        return matrix4d1._m00(double0)
            ._m01(double5)
            ._m02(double6)
            ._m03(double7)
            ._m10(double8)
            ._m11(double13)
            ._m12(double14)
            ._m13(double15)
            ._m20(double16)
            ._m21(double21)
            ._m22(double22)
            ._m23(double23)
            ._m30(double24)
            ._m31(double29)
            ._m32(double30)
            ._m33(double31)
            ._properties(0);
    }

    public Matrix4d mul3x3(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        return this.mul3x3(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, this);
    }

    @Override
    public Matrix4d mul3x3(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, Matrix4d arg9) {
        return (this.properties & 4) != 0
            ? arg9.set(arg0, arg1, arg2, 0.0, arg3, arg4, arg5, 0.0, arg6, arg7, arg8, 0.0, 0.0, 0.0, 0.0, 1.0)
            : this.mulGeneric3x3(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
    }

    private Matrix4d mulGeneric3x3(
        double double1,
        double double2,
        double double3,
        double double8,
        double double9,
        double double10,
        double double15,
        double double16,
        double double17,
        Matrix4d matrix4d1
    ) {
        double double0 = Math.fma(this.m00, double1, Math.fma(this.m10, double2, this.m20 * double3));
        double double4 = Math.fma(this.m01, double1, Math.fma(this.m11, double2, this.m21 * double3));
        double double5 = Math.fma(this.m02, double1, Math.fma(this.m12, double2, this.m22 * double3));
        double double6 = Math.fma(this.m03, double1, Math.fma(this.m13, double2, this.m23 * double3));
        double double7 = Math.fma(this.m00, double8, Math.fma(this.m10, double9, this.m20 * double10));
        double double11 = Math.fma(this.m01, double8, Math.fma(this.m11, double9, this.m21 * double10));
        double double12 = Math.fma(this.m02, double8, Math.fma(this.m12, double9, this.m22 * double10));
        double double13 = Math.fma(this.m03, double8, Math.fma(this.m13, double9, this.m23 * double10));
        double double14 = Math.fma(this.m00, double15, Math.fma(this.m10, double16, this.m20 * double17));
        double double18 = Math.fma(this.m01, double15, Math.fma(this.m11, double16, this.m21 * double17));
        double double19 = Math.fma(this.m02, double15, Math.fma(this.m12, double16, this.m22 * double17));
        double double20 = Math.fma(this.m03, double15, Math.fma(this.m13, double16, this.m23 * double17));
        return matrix4d1._m00(double0)
            ._m01(double4)
            ._m02(double5)
            ._m03(double6)
            ._m10(double7)
            ._m11(double11)
            ._m12(double12)
            ._m13(double13)
            ._m20(double14)
            ._m21(double18)
            ._m22(double19)
            ._m23(double20)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & 2);
    }

    public Matrix4d mulLocal(Matrix4dc arg0) {
        return this.mulLocal(arg0, this);
    }

    @Override
    public Matrix4d mulLocal(Matrix4dc arg0, Matrix4d arg1) {
        if ((this.properties & 4) != 0) {
            return arg1.set(arg0);
        } else if ((arg0.properties() & 4) != 0) {
            return arg1.set(this);
        } else {
            return (this.properties & 2) != 0 && (arg0.properties() & 2) != 0 ? this.mulLocalAffine(arg0, arg1) : this.mulLocalGeneric(arg0, arg1);
        }
    }

    private Matrix4d mulLocalGeneric(Matrix4dc matrix4dc, Matrix4d matrix4d1) {
        double double0 = Math.fma(
            matrix4dc.m00(), this.m00, Math.fma(matrix4dc.m10(), this.m01, Math.fma(matrix4dc.m20(), this.m02, matrix4dc.m30() * this.m03))
        );
        double double1 = Math.fma(
            matrix4dc.m01(), this.m00, Math.fma(matrix4dc.m11(), this.m01, Math.fma(matrix4dc.m21(), this.m02, matrix4dc.m31() * this.m03))
        );
        double double2 = Math.fma(
            matrix4dc.m02(), this.m00, Math.fma(matrix4dc.m12(), this.m01, Math.fma(matrix4dc.m22(), this.m02, matrix4dc.m32() * this.m03))
        );
        double double3 = Math.fma(
            matrix4dc.m03(), this.m00, Math.fma(matrix4dc.m13(), this.m01, Math.fma(matrix4dc.m23(), this.m02, matrix4dc.m33() * this.m03))
        );
        double double4 = Math.fma(
            matrix4dc.m00(), this.m10, Math.fma(matrix4dc.m10(), this.m11, Math.fma(matrix4dc.m20(), this.m12, matrix4dc.m30() * this.m13))
        );
        double double5 = Math.fma(
            matrix4dc.m01(), this.m10, Math.fma(matrix4dc.m11(), this.m11, Math.fma(matrix4dc.m21(), this.m12, matrix4dc.m31() * this.m13))
        );
        double double6 = Math.fma(
            matrix4dc.m02(), this.m10, Math.fma(matrix4dc.m12(), this.m11, Math.fma(matrix4dc.m22(), this.m12, matrix4dc.m32() * this.m13))
        );
        double double7 = Math.fma(
            matrix4dc.m03(), this.m10, Math.fma(matrix4dc.m13(), this.m11, Math.fma(matrix4dc.m23(), this.m12, matrix4dc.m33() * this.m13))
        );
        double double8 = Math.fma(
            matrix4dc.m00(), this.m20, Math.fma(matrix4dc.m10(), this.m21, Math.fma(matrix4dc.m20(), this.m22, matrix4dc.m30() * this.m23))
        );
        double double9 = Math.fma(
            matrix4dc.m01(), this.m20, Math.fma(matrix4dc.m11(), this.m21, Math.fma(matrix4dc.m21(), this.m22, matrix4dc.m31() * this.m23))
        );
        double double10 = Math.fma(
            matrix4dc.m02(), this.m20, Math.fma(matrix4dc.m12(), this.m21, Math.fma(matrix4dc.m22(), this.m22, matrix4dc.m32() * this.m23))
        );
        double double11 = Math.fma(
            matrix4dc.m03(), this.m20, Math.fma(matrix4dc.m13(), this.m21, Math.fma(matrix4dc.m23(), this.m22, matrix4dc.m33() * this.m23))
        );
        double double12 = Math.fma(
            matrix4dc.m00(), this.m30, Math.fma(matrix4dc.m10(), this.m31, Math.fma(matrix4dc.m20(), this.m32, matrix4dc.m30() * this.m33))
        );
        double double13 = Math.fma(
            matrix4dc.m01(), this.m30, Math.fma(matrix4dc.m11(), this.m31, Math.fma(matrix4dc.m21(), this.m32, matrix4dc.m31() * this.m33))
        );
        double double14 = Math.fma(
            matrix4dc.m02(), this.m30, Math.fma(matrix4dc.m12(), this.m31, Math.fma(matrix4dc.m22(), this.m32, matrix4dc.m32() * this.m33))
        );
        double double15 = Math.fma(
            matrix4dc.m03(), this.m30, Math.fma(matrix4dc.m13(), this.m31, Math.fma(matrix4dc.m23(), this.m32, matrix4dc.m33() * this.m33))
        );
        return matrix4d1._m00(double0)
            ._m01(double1)
            ._m02(double2)
            ._m03(double3)
            ._m10(double4)
            ._m11(double5)
            ._m12(double6)
            ._m13(double7)
            ._m20(double8)
            ._m21(double9)
            ._m22(double10)
            ._m23(double11)
            ._m30(double12)
            ._m31(double13)
            ._m32(double14)
            ._m33(double15)
            ._properties(0);
    }

    public Matrix4d mulLocalAffine(Matrix4dc arg0) {
        return this.mulLocalAffine(arg0, this);
    }

    @Override
    public Matrix4d mulLocalAffine(Matrix4dc arg0, Matrix4d arg1) {
        double double0 = arg0.m00() * this.m00 + arg0.m10() * this.m01 + arg0.m20() * this.m02;
        double double1 = arg0.m01() * this.m00 + arg0.m11() * this.m01 + arg0.m21() * this.m02;
        double double2 = arg0.m02() * this.m00 + arg0.m12() * this.m01 + arg0.m22() * this.m02;
        double double3 = arg0.m03();
        double double4 = arg0.m00() * this.m10 + arg0.m10() * this.m11 + arg0.m20() * this.m12;
        double double5 = arg0.m01() * this.m10 + arg0.m11() * this.m11 + arg0.m21() * this.m12;
        double double6 = arg0.m02() * this.m10 + arg0.m12() * this.m11 + arg0.m22() * this.m12;
        double double7 = arg0.m13();
        double double8 = arg0.m00() * this.m20 + arg0.m10() * this.m21 + arg0.m20() * this.m22;
        double double9 = arg0.m01() * this.m20 + arg0.m11() * this.m21 + arg0.m21() * this.m22;
        double double10 = arg0.m02() * this.m20 + arg0.m12() * this.m21 + arg0.m22() * this.m22;
        double double11 = arg0.m23();
        double double12 = arg0.m00() * this.m30 + arg0.m10() * this.m31 + arg0.m20() * this.m32 + arg0.m30();
        double double13 = arg0.m01() * this.m30 + arg0.m11() * this.m31 + arg0.m21() * this.m32 + arg0.m31();
        double double14 = arg0.m02() * this.m30 + arg0.m12() * this.m31 + arg0.m22() * this.m32 + arg0.m32();
        double double15 = arg0.m33();
        arg1._m00(double0)
            ._m01(double1)
            ._m02(double2)
            ._m03(double3)
            ._m10(double4)
            ._m11(double5)
            ._m12(double6)
            ._m13(double7)
            ._m20(double8)
            ._m21(double9)
            ._m22(double10)
            ._m23(double11)
            ._m30(double12)
            ._m31(double13)
            ._m32(double14)
            ._m33(double15)
            ._properties(2);
        return arg1;
    }

    public Matrix4d mul(Matrix4x3dc arg0) {
        return this.mul(arg0, this);
    }

    @Override
    public Matrix4d mul(Matrix4x3dc arg0, Matrix4d arg1) {
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

    private Matrix4d mulTranslation(Matrix4x3dc matrix4x3dc, Matrix4d matrix4d1) {
        return matrix4d1._m00(matrix4x3dc.m00())
            ._m01(matrix4x3dc.m01())
            ._m02(matrix4x3dc.m02())
            ._m03(this.m03)
            ._m10(matrix4x3dc.m10())
            ._m11(matrix4x3dc.m11())
            ._m12(matrix4x3dc.m12())
            ._m13(this.m13)
            ._m20(matrix4x3dc.m20())
            ._m21(matrix4x3dc.m21())
            ._m22(matrix4x3dc.m22())
            ._m23(this.m23)
            ._m30(matrix4x3dc.m30() + this.m30)
            ._m31(matrix4x3dc.m31() + this.m31)
            ._m32(matrix4x3dc.m32() + this.m32)
            ._m33(this.m33)
            ._properties(2 | matrix4x3dc.properties() & 16);
    }

    private Matrix4d mulAffine(Matrix4x3dc matrix4x3dc, Matrix4d matrix4d1) {
        double double0 = this.m00;
        double double1 = this.m01;
        double double2 = this.m02;
        double double3 = this.m10;
        double double4 = this.m11;
        double double5 = this.m12;
        double double6 = this.m20;
        double double7 = this.m21;
        double double8 = this.m22;
        double double9 = matrix4x3dc.m00();
        double double10 = matrix4x3dc.m01();
        double double11 = matrix4x3dc.m02();
        double double12 = matrix4x3dc.m10();
        double double13 = matrix4x3dc.m11();
        double double14 = matrix4x3dc.m12();
        double double15 = matrix4x3dc.m20();
        double double16 = matrix4x3dc.m21();
        double double17 = matrix4x3dc.m22();
        double double18 = matrix4x3dc.m30();
        double double19 = matrix4x3dc.m31();
        double double20 = matrix4x3dc.m32();
        return matrix4d1._m00(Math.fma(double0, double9, Math.fma(double3, double10, double6 * double11)))
            ._m01(Math.fma(double1, double9, Math.fma(double4, double10, double7 * double11)))
            ._m02(Math.fma(double2, double9, Math.fma(double5, double10, double8 * double11)))
            ._m03(this.m03)
            ._m10(Math.fma(double0, double12, Math.fma(double3, double13, double6 * double14)))
            ._m11(Math.fma(double1, double12, Math.fma(double4, double13, double7 * double14)))
            ._m12(Math.fma(double2, double12, Math.fma(double5, double13, double8 * double14)))
            ._m13(this.m13)
            ._m20(Math.fma(double0, double15, Math.fma(double3, double16, double6 * double17)))
            ._m21(Math.fma(double1, double15, Math.fma(double4, double16, double7 * double17)))
            ._m22(Math.fma(double2, double15, Math.fma(double5, double16, double8 * double17)))
            ._m23(this.m23)
            ._m30(Math.fma(double0, double18, Math.fma(double3, double19, Math.fma(double6, double20, this.m30))))
            ._m31(Math.fma(double1, double18, Math.fma(double4, double19, Math.fma(double7, double20, this.m31))))
            ._m32(Math.fma(double2, double18, Math.fma(double5, double19, Math.fma(double8, double20, this.m32))))
            ._m33(this.m33)
            ._properties(2 | this.properties & matrix4x3dc.properties() & 16);
    }

    private Matrix4d mulGeneric(Matrix4x3dc matrix4x3dc, Matrix4d matrix4d1) {
        double double0 = Math.fma(this.m00, matrix4x3dc.m00(), Math.fma(this.m10, matrix4x3dc.m01(), this.m20 * matrix4x3dc.m02()));
        double double1 = Math.fma(this.m01, matrix4x3dc.m00(), Math.fma(this.m11, matrix4x3dc.m01(), this.m21 * matrix4x3dc.m02()));
        double double2 = Math.fma(this.m02, matrix4x3dc.m00(), Math.fma(this.m12, matrix4x3dc.m01(), this.m22 * matrix4x3dc.m02()));
        double double3 = Math.fma(this.m03, matrix4x3dc.m00(), Math.fma(this.m13, matrix4x3dc.m01(), this.m23 * matrix4x3dc.m02()));
        double double4 = Math.fma(this.m00, matrix4x3dc.m10(), Math.fma(this.m10, matrix4x3dc.m11(), this.m20 * matrix4x3dc.m12()));
        double double5 = Math.fma(this.m01, matrix4x3dc.m10(), Math.fma(this.m11, matrix4x3dc.m11(), this.m21 * matrix4x3dc.m12()));
        double double6 = Math.fma(this.m02, matrix4x3dc.m10(), Math.fma(this.m12, matrix4x3dc.m11(), this.m22 * matrix4x3dc.m12()));
        double double7 = Math.fma(this.m03, matrix4x3dc.m10(), Math.fma(this.m13, matrix4x3dc.m11(), this.m23 * matrix4x3dc.m12()));
        double double8 = Math.fma(this.m00, matrix4x3dc.m20(), Math.fma(this.m10, matrix4x3dc.m21(), this.m20 * matrix4x3dc.m22()));
        double double9 = Math.fma(this.m01, matrix4x3dc.m20(), Math.fma(this.m11, matrix4x3dc.m21(), this.m21 * matrix4x3dc.m22()));
        double double10 = Math.fma(this.m02, matrix4x3dc.m20(), Math.fma(this.m12, matrix4x3dc.m21(), this.m22 * matrix4x3dc.m22()));
        double double11 = Math.fma(this.m03, matrix4x3dc.m20(), Math.fma(this.m13, matrix4x3dc.m21(), this.m23 * matrix4x3dc.m22()));
        double double12 = Math.fma(this.m00, matrix4x3dc.m30(), Math.fma(this.m10, matrix4x3dc.m31(), Math.fma(this.m20, matrix4x3dc.m32(), this.m30)));
        double double13 = Math.fma(this.m01, matrix4x3dc.m30(), Math.fma(this.m11, matrix4x3dc.m31(), Math.fma(this.m21, matrix4x3dc.m32(), this.m31)));
        double double14 = Math.fma(this.m02, matrix4x3dc.m30(), Math.fma(this.m12, matrix4x3dc.m31(), Math.fma(this.m22, matrix4x3dc.m32(), this.m32)));
        double double15 = Math.fma(this.m03, matrix4x3dc.m30(), Math.fma(this.m13, matrix4x3dc.m31(), Math.fma(this.m23, matrix4x3dc.m32(), this.m33)));
        matrix4d1._m00(double0)
            ._m01(double1)
            ._m02(double2)
            ._m03(double3)
            ._m10(double4)
            ._m11(double5)
            ._m12(double6)
            ._m13(double7)
            ._m20(double8)
            ._m21(double9)
            ._m22(double10)
            ._m23(double11)
            ._m30(double12)
            ._m31(double13)
            ._m32(double14)
            ._m33(double15)
            ._properties(this.properties & -30);
        return matrix4d1;
    }

    @Override
    public Matrix4d mulPerspectiveAffine(Matrix4x3dc arg0, Matrix4d arg1) {
        double double0 = this.m00;
        double double1 = this.m11;
        double double2 = this.m22;
        double double3 = this.m23;
        arg1._m00(double0 * arg0.m00())
            ._m01(double1 * arg0.m01())
            ._m02(double2 * arg0.m02())
            ._m03(double3 * arg0.m02())
            ._m10(double0 * arg0.m10())
            ._m11(double1 * arg0.m11())
            ._m12(double2 * arg0.m12())
            ._m13(double3 * arg0.m12())
            ._m20(double0 * arg0.m20())
            ._m21(double1 * arg0.m21())
            ._m22(double2 * arg0.m22())
            ._m23(double3 * arg0.m22())
            ._m30(double0 * arg0.m30())
            ._m31(double1 * arg0.m31())
            ._m32(double2 * arg0.m32() + this.m32)
            ._m33(double3 * arg0.m32())
            ._properties(0);
        return arg1;
    }

    @Override
    public Matrix4d mul(Matrix4x3fc arg0, Matrix4d arg1) {
        if ((this.properties & 4) != 0) {
            return arg1.set(arg0);
        } else {
            return (arg0.properties() & 4) != 0 ? arg1.set(this) : this.mulGeneric(arg0, arg1);
        }
    }

    private Matrix4d mulGeneric(Matrix4x3fc matrix4x3fc, Matrix4d matrix4d1) {
        double double0 = Math.fma(this.m00, (double)matrix4x3fc.m00(), Math.fma(this.m10, (double)matrix4x3fc.m01(), this.m20 * matrix4x3fc.m02()));
        double double1 = Math.fma(this.m01, (double)matrix4x3fc.m00(), Math.fma(this.m11, (double)matrix4x3fc.m01(), this.m21 * matrix4x3fc.m02()));
        double double2 = Math.fma(this.m02, (double)matrix4x3fc.m00(), Math.fma(this.m12, (double)matrix4x3fc.m01(), this.m22 * matrix4x3fc.m02()));
        double double3 = Math.fma(this.m03, (double)matrix4x3fc.m00(), Math.fma(this.m13, (double)matrix4x3fc.m01(), this.m23 * matrix4x3fc.m02()));
        double double4 = Math.fma(this.m00, (double)matrix4x3fc.m10(), Math.fma(this.m10, (double)matrix4x3fc.m11(), this.m20 * matrix4x3fc.m12()));
        double double5 = Math.fma(this.m01, (double)matrix4x3fc.m10(), Math.fma(this.m11, (double)matrix4x3fc.m11(), this.m21 * matrix4x3fc.m12()));
        double double6 = Math.fma(this.m02, (double)matrix4x3fc.m10(), Math.fma(this.m12, (double)matrix4x3fc.m11(), this.m22 * matrix4x3fc.m12()));
        double double7 = Math.fma(this.m03, (double)matrix4x3fc.m10(), Math.fma(this.m13, (double)matrix4x3fc.m11(), this.m23 * matrix4x3fc.m12()));
        double double8 = Math.fma(this.m00, (double)matrix4x3fc.m20(), Math.fma(this.m10, (double)matrix4x3fc.m21(), this.m20 * matrix4x3fc.m22()));
        double double9 = Math.fma(this.m01, (double)matrix4x3fc.m20(), Math.fma(this.m11, (double)matrix4x3fc.m21(), this.m21 * matrix4x3fc.m22()));
        double double10 = Math.fma(this.m02, (double)matrix4x3fc.m20(), Math.fma(this.m12, (double)matrix4x3fc.m21(), this.m22 * matrix4x3fc.m22()));
        double double11 = Math.fma(this.m03, (double)matrix4x3fc.m20(), Math.fma(this.m13, (double)matrix4x3fc.m21(), this.m23 * matrix4x3fc.m22()));
        double double12 = Math.fma(
            this.m00, (double)matrix4x3fc.m30(), Math.fma(this.m10, (double)matrix4x3fc.m31(), Math.fma(this.m20, (double)matrix4x3fc.m32(), this.m30))
        );
        double double13 = Math.fma(
            this.m01, (double)matrix4x3fc.m30(), Math.fma(this.m11, (double)matrix4x3fc.m31(), Math.fma(this.m21, (double)matrix4x3fc.m32(), this.m31))
        );
        double double14 = Math.fma(
            this.m02, (double)matrix4x3fc.m30(), Math.fma(this.m12, (double)matrix4x3fc.m31(), Math.fma(this.m22, (double)matrix4x3fc.m32(), this.m32))
        );
        double double15 = Math.fma(
            this.m03, (double)matrix4x3fc.m30(), Math.fma(this.m13, (double)matrix4x3fc.m31(), Math.fma(this.m23, (double)matrix4x3fc.m32(), this.m33))
        );
        matrix4d1._m00(double0)
            ._m01(double1)
            ._m02(double2)
            ._m03(double3)
            ._m10(double4)
            ._m11(double5)
            ._m12(double6)
            ._m13(double7)
            ._m20(double8)
            ._m21(double9)
            ._m22(double10)
            ._m23(double11)
            ._m30(double12)
            ._m31(double13)
            ._m32(double14)
            ._m33(double15)
            ._properties(this.properties & -30);
        return matrix4d1;
    }

    public Matrix4d mul(Matrix3x2dc arg0) {
        return this.mul(arg0, this);
    }

    @Override
    public Matrix4d mul(Matrix3x2dc arg0, Matrix4d arg1) {
        double double0 = this.m00 * arg0.m00() + this.m10 * arg0.m01();
        double double1 = this.m01 * arg0.m00() + this.m11 * arg0.m01();
        double double2 = this.m02 * arg0.m00() + this.m12 * arg0.m01();
        double double3 = this.m03 * arg0.m00() + this.m13 * arg0.m01();
        double double4 = this.m00 * arg0.m10() + this.m10 * arg0.m11();
        double double5 = this.m01 * arg0.m10() + this.m11 * arg0.m11();
        double double6 = this.m02 * arg0.m10() + this.m12 * arg0.m11();
        double double7 = this.m03 * arg0.m10() + this.m13 * arg0.m11();
        double double8 = this.m00 * arg0.m20() + this.m10 * arg0.m21() + this.m30;
        double double9 = this.m01 * arg0.m20() + this.m11 * arg0.m21() + this.m31;
        double double10 = this.m02 * arg0.m20() + this.m12 * arg0.m21() + this.m32;
        double double11 = this.m03 * arg0.m20() + this.m13 * arg0.m21() + this.m33;
        arg1._m00(double0)
            ._m01(double1)
            ._m02(double2)
            ._m03(double3)
            ._m10(double4)
            ._m11(double5)
            ._m12(double6)
            ._m13(double7)
            ._m20(this.m20)
            ._m21(this.m21)
            ._m22(this.m22)
            ._m23(this.m23)
            ._m30(double8)
            ._m31(double9)
            ._m32(double10)
            ._m33(double11)
            ._properties(this.properties & -30);
        return arg1;
    }

    public Matrix4d mul(Matrix3x2fc arg0) {
        return this.mul(arg0, this);
    }

    @Override
    public Matrix4d mul(Matrix3x2fc arg0, Matrix4d arg1) {
        double double0 = this.m00 * arg0.m00() + this.m10 * arg0.m01();
        double double1 = this.m01 * arg0.m00() + this.m11 * arg0.m01();
        double double2 = this.m02 * arg0.m00() + this.m12 * arg0.m01();
        double double3 = this.m03 * arg0.m00() + this.m13 * arg0.m01();
        double double4 = this.m00 * arg0.m10() + this.m10 * arg0.m11();
        double double5 = this.m01 * arg0.m10() + this.m11 * arg0.m11();
        double double6 = this.m02 * arg0.m10() + this.m12 * arg0.m11();
        double double7 = this.m03 * arg0.m10() + this.m13 * arg0.m11();
        double double8 = this.m00 * arg0.m20() + this.m10 * arg0.m21() + this.m30;
        double double9 = this.m01 * arg0.m20() + this.m11 * arg0.m21() + this.m31;
        double double10 = this.m02 * arg0.m20() + this.m12 * arg0.m21() + this.m32;
        double double11 = this.m03 * arg0.m20() + this.m13 * arg0.m21() + this.m33;
        arg1._m00(double0)
            ._m01(double1)
            ._m02(double2)
            ._m03(double3)
            ._m10(double4)
            ._m11(double5)
            ._m12(double6)
            ._m13(double7)
            ._m20(this.m20)
            ._m21(this.m21)
            ._m22(this.m22)
            ._m23(this.m23)
            ._m30(double8)
            ._m31(double9)
            ._m32(double10)
            ._m33(double11)
            ._properties(this.properties & -30);
        return arg1;
    }

    public Matrix4d mul(Matrix4f arg0) {
        return this.mul(arg0, this);
    }

    @Override
    public Matrix4d mul(Matrix4fc arg0, Matrix4d arg1) {
        if ((this.properties & 4) != 0) {
            return arg1.set(arg0);
        } else {
            return (arg0.properties() & 4) != 0 ? arg1.set(this) : this.mulGeneric(arg0, arg1);
        }
    }

    private Matrix4d mulGeneric(Matrix4fc matrix4fc, Matrix4d matrix4d1) {
        double double0 = this.m00 * matrix4fc.m00() + this.m10 * matrix4fc.m01() + this.m20 * matrix4fc.m02() + this.m30 * matrix4fc.m03();
        double double1 = this.m01 * matrix4fc.m00() + this.m11 * matrix4fc.m01() + this.m21 * matrix4fc.m02() + this.m31 * matrix4fc.m03();
        double double2 = this.m02 * matrix4fc.m00() + this.m12 * matrix4fc.m01() + this.m22 * matrix4fc.m02() + this.m32 * matrix4fc.m03();
        double double3 = this.m03 * matrix4fc.m00() + this.m13 * matrix4fc.m01() + this.m23 * matrix4fc.m02() + this.m33 * matrix4fc.m03();
        double double4 = this.m00 * matrix4fc.m10() + this.m10 * matrix4fc.m11() + this.m20 * matrix4fc.m12() + this.m30 * matrix4fc.m13();
        double double5 = this.m01 * matrix4fc.m10() + this.m11 * matrix4fc.m11() + this.m21 * matrix4fc.m12() + this.m31 * matrix4fc.m13();
        double double6 = this.m02 * matrix4fc.m10() + this.m12 * matrix4fc.m11() + this.m22 * matrix4fc.m12() + this.m32 * matrix4fc.m13();
        double double7 = this.m03 * matrix4fc.m10() + this.m13 * matrix4fc.m11() + this.m23 * matrix4fc.m12() + this.m33 * matrix4fc.m13();
        double double8 = this.m00 * matrix4fc.m20() + this.m10 * matrix4fc.m21() + this.m20 * matrix4fc.m22() + this.m30 * matrix4fc.m23();
        double double9 = this.m01 * matrix4fc.m20() + this.m11 * matrix4fc.m21() + this.m21 * matrix4fc.m22() + this.m31 * matrix4fc.m23();
        double double10 = this.m02 * matrix4fc.m20() + this.m12 * matrix4fc.m21() + this.m22 * matrix4fc.m22() + this.m32 * matrix4fc.m23();
        double double11 = this.m03 * matrix4fc.m20() + this.m13 * matrix4fc.m21() + this.m23 * matrix4fc.m22() + this.m33 * matrix4fc.m23();
        double double12 = this.m00 * matrix4fc.m30() + this.m10 * matrix4fc.m31() + this.m20 * matrix4fc.m32() + this.m30 * matrix4fc.m33();
        double double13 = this.m01 * matrix4fc.m30() + this.m11 * matrix4fc.m31() + this.m21 * matrix4fc.m32() + this.m31 * matrix4fc.m33();
        double double14 = this.m02 * matrix4fc.m30() + this.m12 * matrix4fc.m31() + this.m22 * matrix4fc.m32() + this.m32 * matrix4fc.m33();
        double double15 = this.m03 * matrix4fc.m30() + this.m13 * matrix4fc.m31() + this.m23 * matrix4fc.m32() + this.m33 * matrix4fc.m33();
        matrix4d1._m00(double0)
            ._m01(double1)
            ._m02(double2)
            ._m03(double3)
            ._m10(double4)
            ._m11(double5)
            ._m12(double6)
            ._m13(double7)
            ._m20(double8)
            ._m21(double9)
            ._m22(double10)
            ._m23(double11)
            ._m30(double12)
            ._m31(double13)
            ._m32(double14)
            ._m33(double15)
            ._properties(0);
        return matrix4d1;
    }

    public Matrix4d mulPerspectiveAffine(Matrix4dc arg0) {
        return this.mulPerspectiveAffine(arg0, this);
    }

    @Override
    public Matrix4d mulPerspectiveAffine(Matrix4dc arg0, Matrix4d arg1) {
        double double0 = this.m00 * arg0.m00();
        double double1 = this.m11 * arg0.m01();
        double double2 = this.m22 * arg0.m02();
        double double3 = this.m23 * arg0.m02();
        double double4 = this.m00 * arg0.m10();
        double double5 = this.m11 * arg0.m11();
        double double6 = this.m22 * arg0.m12();
        double double7 = this.m23 * arg0.m12();
        double double8 = this.m00 * arg0.m20();
        double double9 = this.m11 * arg0.m21();
        double double10 = this.m22 * arg0.m22();
        double double11 = this.m23 * arg0.m22();
        double double12 = this.m00 * arg0.m30();
        double double13 = this.m11 * arg0.m31();
        double double14 = this.m22 * arg0.m32() + this.m32;
        double double15 = this.m23 * arg0.m32();
        return arg1._m00(double0)
            ._m01(double1)
            ._m02(double2)
            ._m03(double3)
            ._m10(double4)
            ._m11(double5)
            ._m12(double6)
            ._m13(double7)
            ._m20(double8)
            ._m21(double9)
            ._m22(double10)
            ._m23(double11)
            ._m30(double12)
            ._m31(double13)
            ._m32(double14)
            ._m33(double15)
            ._properties(0);
    }

    public Matrix4d mulAffineR(Matrix4dc arg0) {
        return this.mulAffineR(arg0, this);
    }

    @Override
    public Matrix4d mulAffineR(Matrix4dc arg0, Matrix4d arg1) {
        double double0 = Math.fma(this.m00, arg0.m00(), Math.fma(this.m10, arg0.m01(), this.m20 * arg0.m02()));
        double double1 = Math.fma(this.m01, arg0.m00(), Math.fma(this.m11, arg0.m01(), this.m21 * arg0.m02()));
        double double2 = Math.fma(this.m02, arg0.m00(), Math.fma(this.m12, arg0.m01(), this.m22 * arg0.m02()));
        double double3 = Math.fma(this.m03, arg0.m00(), Math.fma(this.m13, arg0.m01(), this.m23 * arg0.m02()));
        double double4 = Math.fma(this.m00, arg0.m10(), Math.fma(this.m10, arg0.m11(), this.m20 * arg0.m12()));
        double double5 = Math.fma(this.m01, arg0.m10(), Math.fma(this.m11, arg0.m11(), this.m21 * arg0.m12()));
        double double6 = Math.fma(this.m02, arg0.m10(), Math.fma(this.m12, arg0.m11(), this.m22 * arg0.m12()));
        double double7 = Math.fma(this.m03, arg0.m10(), Math.fma(this.m13, arg0.m11(), this.m23 * arg0.m12()));
        double double8 = Math.fma(this.m00, arg0.m20(), Math.fma(this.m10, arg0.m21(), this.m20 * arg0.m22()));
        double double9 = Math.fma(this.m01, arg0.m20(), Math.fma(this.m11, arg0.m21(), this.m21 * arg0.m22()));
        double double10 = Math.fma(this.m02, arg0.m20(), Math.fma(this.m12, arg0.m21(), this.m22 * arg0.m22()));
        double double11 = Math.fma(this.m03, arg0.m20(), Math.fma(this.m13, arg0.m21(), this.m23 * arg0.m22()));
        double double12 = Math.fma(this.m00, arg0.m30(), Math.fma(this.m10, arg0.m31(), Math.fma(this.m20, arg0.m32(), this.m30)));
        double double13 = Math.fma(this.m01, arg0.m30(), Math.fma(this.m11, arg0.m31(), Math.fma(this.m21, arg0.m32(), this.m31)));
        double double14 = Math.fma(this.m02, arg0.m30(), Math.fma(this.m12, arg0.m31(), Math.fma(this.m22, arg0.m32(), this.m32)));
        double double15 = Math.fma(this.m03, arg0.m30(), Math.fma(this.m13, arg0.m31(), Math.fma(this.m23, arg0.m32(), this.m33)));
        arg1._m00(double0)
            ._m01(double1)
            ._m02(double2)
            ._m03(double3)
            ._m10(double4)
            ._m11(double5)
            ._m12(double6)
            ._m13(double7)
            ._m20(double8)
            ._m21(double9)
            ._m22(double10)
            ._m23(double11)
            ._m30(double12)
            ._m31(double13)
            ._m32(double14)
            ._m33(double15)
            ._properties(this.properties & -30);
        return arg1;
    }

    public Matrix4d mulAffine(Matrix4dc arg0) {
        return this.mulAffine(arg0, this);
    }

    @Override
    public Matrix4d mulAffine(Matrix4dc arg0, Matrix4d arg1) {
        double double0 = this.m00;
        double double1 = this.m01;
        double double2 = this.m02;
        double double3 = this.m10;
        double double4 = this.m11;
        double double5 = this.m12;
        double double6 = this.m20;
        double double7 = this.m21;
        double double8 = this.m22;
        double double9 = arg0.m00();
        double double10 = arg0.m01();
        double double11 = arg0.m02();
        double double12 = arg0.m10();
        double double13 = arg0.m11();
        double double14 = arg0.m12();
        double double15 = arg0.m20();
        double double16 = arg0.m21();
        double double17 = arg0.m22();
        double double18 = arg0.m30();
        double double19 = arg0.m31();
        double double20 = arg0.m32();
        return arg1._m00(Math.fma(double0, double9, Math.fma(double3, double10, double6 * double11)))
            ._m01(Math.fma(double1, double9, Math.fma(double4, double10, double7 * double11)))
            ._m02(Math.fma(double2, double9, Math.fma(double5, double10, double8 * double11)))
            ._m03(this.m03)
            ._m10(Math.fma(double0, double12, Math.fma(double3, double13, double6 * double14)))
            ._m11(Math.fma(double1, double12, Math.fma(double4, double13, double7 * double14)))
            ._m12(Math.fma(double2, double12, Math.fma(double5, double13, double8 * double14)))
            ._m13(this.m13)
            ._m20(Math.fma(double0, double15, Math.fma(double3, double16, double6 * double17)))
            ._m21(Math.fma(double1, double15, Math.fma(double4, double16, double7 * double17)))
            ._m22(Math.fma(double2, double15, Math.fma(double5, double16, double8 * double17)))
            ._m23(this.m23)
            ._m30(Math.fma(double0, double18, Math.fma(double3, double19, Math.fma(double6, double20, this.m30))))
            ._m31(Math.fma(double1, double18, Math.fma(double4, double19, Math.fma(double7, double20, this.m31))))
            ._m32(Math.fma(double2, double18, Math.fma(double5, double19, Math.fma(double8, double20, this.m32))))
            ._m33(this.m33)
            ._properties(2 | this.properties & arg0.properties() & 16);
    }

    @Override
    public Matrix4d mulTranslationAffine(Matrix4dc arg0, Matrix4d arg1) {
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

    public Matrix4d mulOrthoAffine(Matrix4dc arg0) {
        return this.mulOrthoAffine(arg0, this);
    }

    @Override
    public Matrix4d mulOrthoAffine(Matrix4dc arg0, Matrix4d arg1) {
        double double0 = this.m00 * arg0.m00();
        double double1 = this.m11 * arg0.m01();
        double double2 = this.m22 * arg0.m02();
        double double3 = 0.0;
        double double4 = this.m00 * arg0.m10();
        double double5 = this.m11 * arg0.m11();
        double double6 = this.m22 * arg0.m12();
        double double7 = 0.0;
        double double8 = this.m00 * arg0.m20();
        double double9 = this.m11 * arg0.m21();
        double double10 = this.m22 * arg0.m22();
        double double11 = 0.0;
        double double12 = this.m00 * arg0.m30() + this.m30;
        double double13 = this.m11 * arg0.m31() + this.m31;
        double double14 = this.m22 * arg0.m32() + this.m32;
        double double15 = 1.0;
        arg1._m00(double0)
            ._m01(double1)
            ._m02(double2)
            ._m03(double3)
            ._m10(double4)
            ._m11(double5)
            ._m12(double6)
            ._m13(double7)
            ._m20(double8)
            ._m21(double9)
            ._m22(double10)
            ._m23(double11)
            ._m30(double12)
            ._m31(double13)
            ._m32(double14)
            ._m33(double15)
            ._properties(2);
        return arg1;
    }

    public Matrix4d fma4x3(Matrix4dc arg0, double arg1) {
        return this.fma4x3(arg0, arg1, this);
    }

    @Override
    public Matrix4d fma4x3(Matrix4dc arg0, double arg1, Matrix4d arg2) {
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

    public Matrix4d add(Matrix4dc arg0) {
        return this.add(arg0, this);
    }

    @Override
    public Matrix4d add(Matrix4dc arg0, Matrix4d arg1) {
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

    public Matrix4d sub(Matrix4dc arg0) {
        return this.sub(arg0, this);
    }

    @Override
    public Matrix4d sub(Matrix4dc arg0, Matrix4d arg1) {
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

    public Matrix4d mulComponentWise(Matrix4dc arg0) {
        return this.mulComponentWise(arg0, this);
    }

    @Override
    public Matrix4d mulComponentWise(Matrix4dc arg0, Matrix4d arg1) {
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

    public Matrix4d add4x3(Matrix4dc arg0) {
        return this.add4x3(arg0, this);
    }

    @Override
    public Matrix4d add4x3(Matrix4dc arg0, Matrix4d arg1) {
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

    public Matrix4d add4x3(Matrix4fc arg0) {
        return this.add4x3(arg0, this);
    }

    @Override
    public Matrix4d add4x3(Matrix4fc arg0, Matrix4d arg1) {
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

    public Matrix4d sub4x3(Matrix4dc arg0) {
        return this.sub4x3(arg0, this);
    }

    @Override
    public Matrix4d sub4x3(Matrix4dc arg0, Matrix4d arg1) {
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

    public Matrix4d mul4x3ComponentWise(Matrix4dc arg0) {
        return this.mul4x3ComponentWise(arg0, this);
    }

    @Override
    public Matrix4d mul4x3ComponentWise(Matrix4dc arg0, Matrix4d arg1) {
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

    public Matrix4d set(
        double arg0,
        double arg1,
        double arg2,
        double arg3,
        double arg4,
        double arg5,
        double arg6,
        double arg7,
        double arg8,
        double arg9,
        double arg10,
        double arg11,
        double arg12,
        double arg13,
        double arg14,
        double arg15
    ) {
        this.m00 = arg0;
        this.m10 = arg4;
        this.m20 = arg8;
        this.m30 = arg12;
        this.m01 = arg1;
        this.m11 = arg5;
        this.m21 = arg9;
        this.m31 = arg13;
        this.m02 = arg2;
        this.m12 = arg6;
        this.m22 = arg10;
        this.m32 = arg14;
        this.m03 = arg3;
        this.m13 = arg7;
        this.m23 = arg11;
        this.m33 = arg15;
        return this.determineProperties();
    }

    public Matrix4d set(double[] doubles, int int0) {
        return this._m00(doubles[int0 + 0])
            ._m01(doubles[int0 + 1])
            ._m02(doubles[int0 + 2])
            ._m03(doubles[int0 + 3])
            ._m10(doubles[int0 + 4])
            ._m11(doubles[int0 + 5])
            ._m12(doubles[int0 + 6])
            ._m13(doubles[int0 + 7])
            ._m20(doubles[int0 + 8])
            ._m21(doubles[int0 + 9])
            ._m22(doubles[int0 + 10])
            ._m23(doubles[int0 + 11])
            ._m30(doubles[int0 + 12])
            ._m31(doubles[int0 + 13])
            ._m32(doubles[int0 + 14])
            ._m33(doubles[int0 + 15])
            .determineProperties();
    }

    public Matrix4d set(double[] doubles) {
        return this.set(doubles, 0);
    }

    public Matrix4d set(float[] floats, int int0) {
        return this._m00(floats[int0 + 0])
            ._m01(floats[int0 + 1])
            ._m02(floats[int0 + 2])
            ._m03(floats[int0 + 3])
            ._m10(floats[int0 + 4])
            ._m11(floats[int0 + 5])
            ._m12(floats[int0 + 6])
            ._m13(floats[int0 + 7])
            ._m20(floats[int0 + 8])
            ._m21(floats[int0 + 9])
            ._m22(floats[int0 + 10])
            ._m23(floats[int0 + 11])
            ._m30(floats[int0 + 12])
            ._m31(floats[int0 + 13])
            ._m32(floats[int0 + 14])
            ._m33(floats[int0 + 15])
            .determineProperties();
    }

    public Matrix4d set(float[] floats) {
        return this.set(floats, 0);
    }

    public Matrix4d set(DoubleBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this.determineProperties();
    }

    public Matrix4d set(FloatBuffer arg0) {
        MemUtil.INSTANCE.getf(this, arg0.position(), arg0);
        return this.determineProperties();
    }

    public Matrix4d set(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this.determineProperties();
    }

    public Matrix4d setFloats(ByteBuffer arg0) {
        MemUtil.INSTANCE.getf(this, arg0.position(), arg0);
        return this.determineProperties();
    }

    public Matrix4d setFromAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.get(this, arg0);
            return this.determineProperties();
        }
    }

    public Matrix4d set(Vector4d arg0, Vector4d arg1, Vector4d arg2, Vector4d arg3) {
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
    public double determinant() {
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
    public double determinant3x3() {
        return (this.m00 * this.m11 - this.m01 * this.m10) * this.m22
            + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21
            + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
    }

    @Override
    public double determinantAffine() {
        return (this.m00 * this.m11 - this.m01 * this.m10) * this.m22
            + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21
            + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
    }

    public Matrix4d invert() {
        return this.invert(this);
    }

    @Override
    public Matrix4d invert(Matrix4d arg0) {
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

    private Matrix4d invertTranslation(Matrix4d matrix4d0) {
        if (matrix4d0 != this) {
            matrix4d0.set(this);
        }

        matrix4d0._m30(-this.m30)._m31(-this.m31)._m32(-this.m32)._properties(26);
        return matrix4d0;
    }

    private Matrix4d invertOrthonormal(Matrix4d matrix4d1) {
        double double0 = -(this.m00 * this.m30 + this.m01 * this.m31 + this.m02 * this.m32);
        double double1 = -(this.m10 * this.m30 + this.m11 * this.m31 + this.m12 * this.m32);
        double double2 = -(this.m20 * this.m30 + this.m21 * this.m31 + this.m22 * this.m32);
        double double3 = this.m01;
        double double4 = this.m02;
        double double5 = this.m12;
        matrix4d1._m00(this.m00)
            ._m01(this.m10)
            ._m02(this.m20)
            ._m03(0.0)
            ._m10(double3)
            ._m11(this.m11)
            ._m12(this.m21)
            ._m13(0.0)
            ._m20(double4)
            ._m21(double5)
            ._m22(this.m22)
            ._m23(0.0)
            ._m30(double0)
            ._m31(double1)
            ._m32(double2)
            ._m33(1.0)
            ._properties(18);
        return matrix4d1;
    }

    private Matrix4d invertGeneric(Matrix4d matrix4d1) {
        return this != matrix4d1 ? this.invertGenericNonThis(matrix4d1) : this.invertGenericThis(matrix4d1);
    }

    private Matrix4d invertGenericNonThis(Matrix4d matrix4d1) {
        double double0 = this.m00 * this.m11 - this.m01 * this.m10;
        double double1 = this.m00 * this.m12 - this.m02 * this.m10;
        double double2 = this.m00 * this.m13 - this.m03 * this.m10;
        double double3 = this.m01 * this.m12 - this.m02 * this.m11;
        double double4 = this.m01 * this.m13 - this.m03 * this.m11;
        double double5 = this.m02 * this.m13 - this.m03 * this.m12;
        double double6 = this.m20 * this.m31 - this.m21 * this.m30;
        double double7 = this.m20 * this.m32 - this.m22 * this.m30;
        double double8 = this.m20 * this.m33 - this.m23 * this.m30;
        double double9 = this.m21 * this.m32 - this.m22 * this.m31;
        double double10 = this.m21 * this.m33 - this.m23 * this.m31;
        double double11 = this.m22 * this.m33 - this.m23 * this.m32;
        double double12 = double0 * double11 - double1 * double10 + double2 * double9 + double3 * double8 - double4 * double7 + double5 * double6;
        double12 = 1.0 / double12;
        return matrix4d1._m00(Math.fma(this.m11, double11, Math.fma(-this.m12, double10, this.m13 * double9)) * double12)
            ._m01(Math.fma(-this.m01, double11, Math.fma(this.m02, double10, -this.m03 * double9)) * double12)
            ._m02(Math.fma(this.m31, double5, Math.fma(-this.m32, double4, this.m33 * double3)) * double12)
            ._m03(Math.fma(-this.m21, double5, Math.fma(this.m22, double4, -this.m23 * double3)) * double12)
            ._m10(Math.fma(-this.m10, double11, Math.fma(this.m12, double8, -this.m13 * double7)) * double12)
            ._m11(Math.fma(this.m00, double11, Math.fma(-this.m02, double8, this.m03 * double7)) * double12)
            ._m12(Math.fma(-this.m30, double5, Math.fma(this.m32, double2, -this.m33 * double1)) * double12)
            ._m13(Math.fma(this.m20, double5, Math.fma(-this.m22, double2, this.m23 * double1)) * double12)
            ._m20(Math.fma(this.m10, double10, Math.fma(-this.m11, double8, this.m13 * double6)) * double12)
            ._m21(Math.fma(-this.m00, double10, Math.fma(this.m01, double8, -this.m03 * double6)) * double12)
            ._m22(Math.fma(this.m30, double4, Math.fma(-this.m31, double2, this.m33 * double0)) * double12)
            ._m23(Math.fma(-this.m20, double4, Math.fma(this.m21, double2, -this.m23 * double0)) * double12)
            ._m30(Math.fma(-this.m10, double9, Math.fma(this.m11, double7, -this.m12 * double6)) * double12)
            ._m31(Math.fma(this.m00, double9, Math.fma(-this.m01, double7, this.m02 * double6)) * double12)
            ._m32(Math.fma(-this.m30, double3, Math.fma(this.m31, double1, -this.m32 * double0)) * double12)
            ._m33(Math.fma(this.m20, double3, Math.fma(-this.m21, double1, this.m22 * double0)) * double12)
            ._properties(0);
    }

    private Matrix4d invertGenericThis(Matrix4d matrix4d1) {
        double double0 = this.m00 * this.m11 - this.m01 * this.m10;
        double double1 = this.m00 * this.m12 - this.m02 * this.m10;
        double double2 = this.m00 * this.m13 - this.m03 * this.m10;
        double double3 = this.m01 * this.m12 - this.m02 * this.m11;
        double double4 = this.m01 * this.m13 - this.m03 * this.m11;
        double double5 = this.m02 * this.m13 - this.m03 * this.m12;
        double double6 = this.m20 * this.m31 - this.m21 * this.m30;
        double double7 = this.m20 * this.m32 - this.m22 * this.m30;
        double double8 = this.m20 * this.m33 - this.m23 * this.m30;
        double double9 = this.m21 * this.m32 - this.m22 * this.m31;
        double double10 = this.m21 * this.m33 - this.m23 * this.m31;
        double double11 = this.m22 * this.m33 - this.m23 * this.m32;
        double double12 = double0 * double11 - double1 * double10 + double2 * double9 + double3 * double8 - double4 * double7 + double5 * double6;
        double12 = 1.0 / double12;
        double double13 = Math.fma(this.m11, double11, Math.fma(-this.m12, double10, this.m13 * double9)) * double12;
        double double14 = Math.fma(-this.m01, double11, Math.fma(this.m02, double10, -this.m03 * double9)) * double12;
        double double15 = Math.fma(this.m31, double5, Math.fma(-this.m32, double4, this.m33 * double3)) * double12;
        double double16 = Math.fma(-this.m21, double5, Math.fma(this.m22, double4, -this.m23 * double3)) * double12;
        double double17 = Math.fma(-this.m10, double11, Math.fma(this.m12, double8, -this.m13 * double7)) * double12;
        double double18 = Math.fma(this.m00, double11, Math.fma(-this.m02, double8, this.m03 * double7)) * double12;
        double double19 = Math.fma(-this.m30, double5, Math.fma(this.m32, double2, -this.m33 * double1)) * double12;
        double double20 = Math.fma(this.m20, double5, Math.fma(-this.m22, double2, this.m23 * double1)) * double12;
        double double21 = Math.fma(this.m10, double10, Math.fma(-this.m11, double8, this.m13 * double6)) * double12;
        double double22 = Math.fma(-this.m00, double10, Math.fma(this.m01, double8, -this.m03 * double6)) * double12;
        double double23 = Math.fma(this.m30, double4, Math.fma(-this.m31, double2, this.m33 * double0)) * double12;
        double double24 = Math.fma(-this.m20, double4, Math.fma(this.m21, double2, -this.m23 * double0)) * double12;
        double double25 = Math.fma(-this.m10, double9, Math.fma(this.m11, double7, -this.m12 * double6)) * double12;
        double double26 = Math.fma(this.m00, double9, Math.fma(-this.m01, double7, this.m02 * double6)) * double12;
        double double27 = Math.fma(-this.m30, double3, Math.fma(this.m31, double1, -this.m32 * double0)) * double12;
        double double28 = Math.fma(this.m20, double3, Math.fma(-this.m21, double1, this.m22 * double0)) * double12;
        return matrix4d1._m00(double13)
            ._m01(double14)
            ._m02(double15)
            ._m03(double16)
            ._m10(double17)
            ._m11(double18)
            ._m12(double19)
            ._m13(double20)
            ._m20(double21)
            ._m21(double22)
            ._m22(double23)
            ._m23(double24)
            ._m30(double25)
            ._m31(double26)
            ._m32(double27)
            ._m33(double28)
            ._properties(0);
    }

    @Override
    public Matrix4d invertPerspective(Matrix4d arg0) {
        double double0 = 1.0 / (this.m00 * this.m11);
        double double1 = -1.0 / (this.m23 * this.m32);
        arg0.set(
            this.m11 * double0,
            0.0,
            0.0,
            0.0,
            0.0,
            this.m00 * double0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            -this.m23 * double1,
            0.0,
            0.0,
            -this.m32 * double1,
            this.m22 * double1
        );
        return arg0;
    }

    public Matrix4d invertPerspective() {
        return this.invertPerspective(this);
    }

    @Override
    public Matrix4d invertFrustum(Matrix4d arg0) {
        double double0 = 1.0 / this.m00;
        double double1 = 1.0 / this.m11;
        double double2 = 1.0 / this.m23;
        double double3 = 1.0 / this.m32;
        arg0.set(
            double0,
            0.0,
            0.0,
            0.0,
            0.0,
            double1,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            double3,
            -this.m20 * double0 * double2,
            -this.m21 * double1 * double2,
            double2,
            -this.m22 * double2 * double3
        );
        return arg0;
    }

    public Matrix4d invertFrustum() {
        return this.invertFrustum(this);
    }

    @Override
    public Matrix4d invertOrtho(Matrix4d arg0) {
        double double0 = 1.0 / this.m00;
        double double1 = 1.0 / this.m11;
        double double2 = 1.0 / this.m22;
        arg0.set(double0, 0.0, 0.0, 0.0, 0.0, double1, 0.0, 0.0, 0.0, 0.0, double2, 0.0, -this.m30 * double0, -this.m31 * double1, -this.m32 * double2, 1.0)
            ._properties(2 | this.properties & 16);
        return arg0;
    }

    public Matrix4d invertOrtho() {
        return this.invertOrtho(this);
    }

    @Override
    public Matrix4d invertPerspectiveView(Matrix4dc arg0, Matrix4d arg1) {
        double double0 = 1.0 / (this.m00 * this.m11);
        double double1 = -1.0 / (this.m23 * this.m32);
        double double2 = this.m11 * double0;
        double double3 = this.m00 * double0;
        double double4 = -this.m23 * double1;
        double double5 = -this.m32 * double1;
        double double6 = this.m22 * double1;
        double double7 = -arg0.m00() * arg0.m30() - arg0.m01() * arg0.m31() - arg0.m02() * arg0.m32();
        double double8 = -arg0.m10() * arg0.m30() - arg0.m11() * arg0.m31() - arg0.m12() * arg0.m32();
        double double9 = -arg0.m20() * arg0.m30() - arg0.m21() * arg0.m31() - arg0.m22() * arg0.m32();
        double double10 = arg0.m01() * double3;
        double double11 = arg0.m02() * double5 + double7 * double6;
        double double12 = arg0.m12() * double5 + double8 * double6;
        double double13 = arg0.m22() * double5 + double9 * double6;
        return arg1._m00(arg0.m00() * double2)
            ._m01(arg0.m10() * double2)
            ._m02(arg0.m20() * double2)
            ._m03(0.0)
            ._m10(double10)
            ._m11(arg0.m11() * double3)
            ._m12(arg0.m21() * double3)
            ._m13(0.0)
            ._m20(double7 * double4)
            ._m21(double8 * double4)
            ._m22(double9 * double4)
            ._m23(double4)
            ._m30(double11)
            ._m31(double12)
            ._m32(double13)
            ._m33(double6)
            ._properties(0);
    }

    @Override
    public Matrix4d invertPerspectiveView(Matrix4x3dc arg0, Matrix4d arg1) {
        double double0 = 1.0 / (this.m00 * this.m11);
        double double1 = -1.0 / (this.m23 * this.m32);
        double double2 = this.m11 * double0;
        double double3 = this.m00 * double0;
        double double4 = -this.m23 * double1;
        double double5 = -this.m32 * double1;
        double double6 = this.m22 * double1;
        double double7 = -arg0.m00() * arg0.m30() - arg0.m01() * arg0.m31() - arg0.m02() * arg0.m32();
        double double8 = -arg0.m10() * arg0.m30() - arg0.m11() * arg0.m31() - arg0.m12() * arg0.m32();
        double double9 = -arg0.m20() * arg0.m30() - arg0.m21() * arg0.m31() - arg0.m22() * arg0.m32();
        return arg1._m00(arg0.m00() * double2)
            ._m01(arg0.m10() * double2)
            ._m02(arg0.m20() * double2)
            ._m03(0.0)
            ._m10(arg0.m01() * double3)
            ._m11(arg0.m11() * double3)
            ._m12(arg0.m21() * double3)
            ._m13(0.0)
            ._m20(double7 * double4)
            ._m21(double8 * double4)
            ._m22(double9 * double4)
            ._m23(double4)
            ._m30(arg0.m02() * double5 + double7 * double6)
            ._m31(arg0.m12() * double5 + double8 * double6)
            ._m32(arg0.m22() * double5 + double9 * double6)
            ._m33(double6)
            ._properties(0);
    }

    @Override
    public Matrix4d invertAffine(Matrix4d arg0) {
        double double0 = this.m00 * this.m11;
        double double1 = this.m01 * this.m10;
        double double2 = this.m02 * this.m10;
        double double3 = this.m00 * this.m12;
        double double4 = this.m01 * this.m12;
        double double5 = this.m02 * this.m11;
        double double6 = 1.0 / ((double0 - double1) * this.m22 + (double2 - double3) * this.m21 + (double4 - double5) * this.m20);
        double double7 = this.m10 * this.m22;
        double double8 = this.m10 * this.m21;
        double double9 = this.m11 * this.m22;
        double double10 = this.m11 * this.m20;
        double double11 = this.m12 * this.m21;
        double double12 = this.m12 * this.m20;
        double double13 = this.m20 * this.m02;
        double double14 = this.m20 * this.m01;
        double double15 = this.m21 * this.m02;
        double double16 = this.m21 * this.m00;
        double double17 = this.m22 * this.m01;
        double double18 = this.m22 * this.m00;
        double double19 = (double9 - double11) * double6;
        double double20 = (double15 - double17) * double6;
        double double21 = (double4 - double5) * double6;
        double double22 = (double12 - double7) * double6;
        double double23 = (double18 - double13) * double6;
        double double24 = (double2 - double3) * double6;
        double double25 = (double8 - double10) * double6;
        double double26 = (double14 - double16) * double6;
        double double27 = (double0 - double1) * double6;
        double double28 = (double7 * this.m31 - double8 * this.m32 + double10 * this.m32 - double9 * this.m30 + double11 * this.m30 - double12 * this.m31)
            * double6;
        double double29 = (double13 * this.m31 - double14 * this.m32 + double16 * this.m32 - double15 * this.m30 + double17 * this.m30 - double18 * this.m31)
            * double6;
        double double30 = (double5 * this.m30 - double4 * this.m30 + double3 * this.m31 - double2 * this.m31 + double1 * this.m32 - double0 * this.m32)
            * double6;
        arg0._m00(double19)
            ._m01(double20)
            ._m02(double21)
            ._m03(0.0)
            ._m10(double22)
            ._m11(double23)
            ._m12(double24)
            ._m13(0.0)
            ._m20(double25)
            ._m21(double26)
            ._m22(double27)
            ._m23(0.0)
            ._m30(double28)
            ._m31(double29)
            ._m32(double30)
            ._m33(1.0)
            ._properties(2);
        return arg0;
    }

    public Matrix4d invertAffine() {
        return this.invertAffine(this);
    }

    public Matrix4d transpose() {
        return this.transpose(this);
    }

    @Override
    public Matrix4d transpose(Matrix4d arg0) {
        if ((this.properties & 4) != 0) {
            return arg0.identity();
        } else {
            return this != arg0 ? this.transposeNonThisGeneric(arg0) : this.transposeThisGeneric(arg0);
        }
    }

    private Matrix4d transposeNonThisGeneric(Matrix4d matrix4d1) {
        return matrix4d1._m00(this.m00)
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

    private Matrix4d transposeThisGeneric(Matrix4d matrix4d1) {
        double double0 = this.m01;
        double double1 = this.m02;
        double double2 = this.m12;
        double double3 = this.m03;
        double double4 = this.m13;
        double double5 = this.m23;
        return matrix4d1._m01(this.m10)
            ._m02(this.m20)
            ._m03(this.m30)
            ._m10(double0)
            ._m12(this.m21)
            ._m13(this.m31)
            ._m20(double1)
            ._m21(double2)
            ._m23(this.m32)
            ._m30(double3)
            ._m31(double4)
            ._m32(double5)
            ._properties(0);
    }

    public Matrix4d transpose3x3() {
        return this.transpose3x3(this);
    }

    @Override
    public Matrix4d transpose3x3(Matrix4d arg0) {
        double double0 = this.m01;
        double double1 = this.m02;
        double double2 = this.m12;
        return arg0._m00(this.m00)
            ._m01(this.m10)
            ._m02(this.m20)
            ._m10(double0)
            ._m11(this.m11)
            ._m12(this.m21)
            ._m20(double1)
            ._m21(double2)
            ._m22(this.m22)
            ._properties(this.properties & 30);
    }

    @Override
    public Matrix3d transpose3x3(Matrix3d arg0) {
        return arg0._m00(this.m00)._m01(this.m10)._m02(this.m20)._m10(this.m01)._m11(this.m11)._m12(this.m21)._m20(this.m02)._m21(this.m12)._m22(this.m22);
    }

    public Matrix4d translation(double arg0, double arg1, double arg2) {
        if ((this.properties & 4) == 0) {
            this._identity();
        }

        return this._m30(arg0)._m31(arg1)._m32(arg2)._m33(1.0)._properties(26);
    }

    public Matrix4d translation(Vector3fc arg0) {
        return this.translation(arg0.x(), arg0.y(), arg0.z());
    }

    public Matrix4d translation(Vector3dc arg0) {
        return this.translation(arg0.x(), arg0.y(), arg0.z());
    }

    public Matrix4d setTranslation(double arg0, double arg1, double arg2) {
        this._m30(arg0)._m31(arg1)._m32(arg2).properties &= -6;
        return this;
    }

    public Matrix4d setTranslation(Vector3dc arg0) {
        return this.setTranslation(arg0.x(), arg0.y(), arg0.z());
    }

    @Override
    public Vector3d getTranslation(Vector3d arg0) {
        arg0.x = this.m30;
        arg0.y = this.m31;
        arg0.z = this.m32;
        return arg0;
    }

    @Override
    public Vector3d getScale(Vector3d arg0) {
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
    public Matrix4d get(Matrix4d arg0) {
        return arg0.set(this);
    }

    @Override
    public Matrix4x3d get4x3(Matrix4x3d arg0) {
        return arg0.set(this);
    }

    @Override
    public Matrix3d get3x3(Matrix3d arg0) {
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
    public DoubleBuffer get(DoubleBuffer arg0) {
        MemUtil.INSTANCE.put(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public DoubleBuffer get(int arg0, DoubleBuffer arg1) {
        MemUtil.INSTANCE.put(this, arg0, arg1);
        return arg1;
    }

    @Override
    public FloatBuffer get(FloatBuffer arg0) {
        MemUtil.INSTANCE.putf(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public FloatBuffer get(int arg0, FloatBuffer arg1) {
        MemUtil.INSTANCE.putf(this, arg0, arg1);
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
    public ByteBuffer getFloats(ByteBuffer arg0) {
        MemUtil.INSTANCE.putf(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public ByteBuffer getFloats(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.putf(this, arg0, arg1);
        return arg1;
    }

    @Override
    public Matrix4dc getToAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.put(this, arg0);
            return this;
        }
    }

    @Override
    public double[] get(double[] doubles, int int0) {
        doubles[int0 + 0] = this.m00;
        doubles[int0 + 1] = this.m01;
        doubles[int0 + 2] = this.m02;
        doubles[int0 + 3] = this.m03;
        doubles[int0 + 4] = this.m10;
        doubles[int0 + 5] = this.m11;
        doubles[int0 + 6] = this.m12;
        doubles[int0 + 7] = this.m13;
        doubles[int0 + 8] = this.m20;
        doubles[int0 + 9] = this.m21;
        doubles[int0 + 10] = this.m22;
        doubles[int0 + 11] = this.m23;
        doubles[int0 + 12] = this.m30;
        doubles[int0 + 13] = this.m31;
        doubles[int0 + 14] = this.m32;
        doubles[int0 + 15] = this.m33;
        return doubles;
    }

    @Override
    public double[] get(double[] doubles) {
        return this.get(doubles, 0);
    }

    @Override
    public float[] get(float[] floats, int int0) {
        floats[int0 + 0] = (float)this.m00;
        floats[int0 + 1] = (float)this.m01;
        floats[int0 + 2] = (float)this.m02;
        floats[int0 + 3] = (float)this.m03;
        floats[int0 + 4] = (float)this.m10;
        floats[int0 + 5] = (float)this.m11;
        floats[int0 + 6] = (float)this.m12;
        floats[int0 + 7] = (float)this.m13;
        floats[int0 + 8] = (float)this.m20;
        floats[int0 + 9] = (float)this.m21;
        floats[int0 + 10] = (float)this.m22;
        floats[int0 + 11] = (float)this.m23;
        floats[int0 + 12] = (float)this.m30;
        floats[int0 + 13] = (float)this.m31;
        floats[int0 + 14] = (float)this.m32;
        floats[int0 + 15] = (float)this.m33;
        return floats;
    }

    @Override
    public float[] get(float[] floats) {
        return this.get(floats, 0);
    }

    @Override
    public DoubleBuffer getTransposed(DoubleBuffer arg0) {
        MemUtil.INSTANCE.putTransposed(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public DoubleBuffer getTransposed(int arg0, DoubleBuffer arg1) {
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
    public DoubleBuffer get4x3Transposed(DoubleBuffer arg0) {
        MemUtil.INSTANCE.put4x3Transposed(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public DoubleBuffer get4x3Transposed(int arg0, DoubleBuffer arg1) {
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

    public Matrix4d zero() {
        return this._m00(0.0)
            ._m01(0.0)
            ._m02(0.0)
            ._m03(0.0)
            ._m10(0.0)
            ._m11(0.0)
            ._m12(0.0)
            ._m13(0.0)
            ._m20(0.0)
            ._m21(0.0)
            ._m22(0.0)
            ._m23(0.0)
            ._m30(0.0)
            ._m31(0.0)
            ._m32(0.0)
            ._m33(0.0)
            ._properties(0);
    }

    public Matrix4d scaling(double arg0) {
        return this.scaling(arg0, arg0, arg0);
    }

    public Matrix4d scaling(double arg0, double arg1, double arg2) {
        if ((this.properties & 4) == 0) {
            this.identity();
        }

        boolean boolean0 = Math.absEqualsOne(arg0) && Math.absEqualsOne(arg1) && Math.absEqualsOne(arg2);
        this._m00(arg0)._m11(arg1)._m22(arg2).properties = 2 | (boolean0 ? 16 : 0);
        return this;
    }

    public Matrix4d scaling(Vector3dc arg0) {
        return this.scaling(arg0.x(), arg0.y(), arg0.z());
    }

    public Matrix4d rotation(double arg0, double arg1, double arg2, double arg3) {
        if (arg2 == 0.0 && arg3 == 0.0 && Math.absEqualsOne(arg1)) {
            return this.rotationX(arg1 * arg0);
        } else if (arg1 == 0.0 && arg3 == 0.0 && Math.absEqualsOne(arg2)) {
            return this.rotationY(arg2 * arg0);
        } else {
            return arg1 == 0.0 && arg2 == 0.0 && Math.absEqualsOne(arg3) ? this.rotationZ(arg3 * arg0) : this.rotationInternal(arg0, arg1, arg2, arg3);
        }
    }

    private Matrix4d rotationInternal(double double1, double double5, double double6, double double8) {
        double double0 = Math.sin(double1);
        double double2 = Math.cosFromSin(double0, double1);
        double double3 = 1.0 - double2;
        double double4 = double5 * double6;
        double double7 = double5 * double8;
        double double9 = double6 * double8;
        if ((this.properties & 4) == 0) {
            this._identity();
        }

        this._m00(double2 + double5 * double5 * double3)
            ._m10(double4 * double3 - double8 * double0)
            ._m20(double7 * double3 + double6 * double0)
            ._m01(double4 * double3 + double8 * double0)
            ._m11(double2 + double6 * double6 * double3)
            ._m21(double9 * double3 - double5 * double0)
            ._m02(double7 * double3 - double6 * double0)
            ._m12(double9 * double3 + double5 * double0)
            ._m22(double2 + double8 * double8 * double3)
            .properties = 18;
        return this;
    }

    public Matrix4d rotationX(double arg0) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        if ((this.properties & 4) == 0) {
            this._identity();
        }

        this._m11(double1)._m12(double0)._m21(-double0)._m22(double1).properties = 18;
        return this;
    }

    public Matrix4d rotationY(double arg0) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        if ((this.properties & 4) == 0) {
            this._identity();
        }

        this._m00(double1)._m02(-double0)._m20(double0)._m22(double1).properties = 18;
        return this;
    }

    public Matrix4d rotationZ(double arg0) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        if ((this.properties & 4) == 0) {
            this._identity();
        }

        this._m00(double1)._m01(double0)._m10(-double0)._m11(double1).properties = 18;
        return this;
    }

    public Matrix4d rotationTowardsXY(double arg0, double arg1) {
        if ((this.properties & 4) == 0) {
            this._identity();
        }

        this.m00 = arg1;
        this.m01 = arg0;
        this.m10 = -arg0;
        this.m11 = arg1;
        this.properties = 18;
        return this;
    }

    public Matrix4d rotationXYZ(double arg0, double arg1, double arg2) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = Math.sin(arg1);
        double double3 = Math.cosFromSin(double2, arg1);
        double double4 = Math.sin(arg2);
        double double5 = Math.cosFromSin(double4, arg2);
        double double6 = -double0;
        double double7 = -double2;
        double double8 = -double4;
        if ((this.properties & 4) == 0) {
            this._identity();
        }

        double double9 = double6 * double7;
        double double10 = double1 * double7;
        this._m20(double2)
            ._m21(double6 * double3)
            ._m22(double1 * double3)
            ._m00(double3 * double5)
            ._m01(double9 * double5 + double1 * double4)
            ._m02(double10 * double5 + double0 * double4)
            ._m10(double3 * double8)
            ._m11(double9 * double8 + double1 * double5)
            ._m12(double10 * double8 + double0 * double5)
            .properties = 18;
        return this;
    }

    public Matrix4d rotationZYX(double arg0, double arg1, double arg2) {
        double double0 = Math.sin(arg2);
        double double1 = Math.cosFromSin(double0, arg2);
        double double2 = Math.sin(arg1);
        double double3 = Math.cosFromSin(double2, arg1);
        double double4 = Math.sin(arg0);
        double double5 = Math.cosFromSin(double4, arg0);
        double double6 = -double4;
        double double7 = -double2;
        double double8 = -double0;
        if ((this.properties & 4) == 0) {
            this._identity();
        }

        double double9 = double5 * double2;
        double double10 = double4 * double2;
        this._m00(double5 * double3)
            ._m01(double4 * double3)
            ._m02(double7)
            ._m10(double6 * double1 + double9 * double0)
            ._m11(double5 * double1 + double10 * double0)
            ._m12(double3 * double0)
            ._m20(double6 * double8 + double9 * double1)
            ._m21(double5 * double8 + double10 * double1)
            ._m22(double3 * double1)
            .properties = 18;
        return this;
    }

    public Matrix4d rotationYXZ(double arg0, double arg1, double arg2) {
        double double0 = Math.sin(arg1);
        double double1 = Math.cosFromSin(double0, arg1);
        double double2 = Math.sin(arg0);
        double double3 = Math.cosFromSin(double2, arg0);
        double double4 = Math.sin(arg2);
        double double5 = Math.cosFromSin(double4, arg2);
        double double6 = -double2;
        double double7 = -double0;
        double double8 = -double4;
        double double9 = double2 * double0;
        double double10 = double3 * double0;
        this._m20(double2 * double1)
            ._m21(double7)
            ._m22(double3 * double1)
            ._m23(0.0)
            ._m00(double3 * double5 + double9 * double4)
            ._m01(double1 * double4)
            ._m02(double6 * double5 + double10 * double4)
            ._m03(0.0)
            ._m10(double3 * double8 + double9 * double5)
            ._m11(double1 * double5)
            ._m12(double6 * double8 + double10 * double5)
            ._m13(0.0)
            ._m30(0.0)
            ._m31(0.0)
            ._m32(0.0)
            ._m33(1.0)
            .properties = 18;
        return this;
    }

    public Matrix4d setRotationXYZ(double arg0, double arg1, double arg2) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = Math.sin(arg1);
        double double3 = Math.cosFromSin(double2, arg1);
        double double4 = Math.sin(arg2);
        double double5 = Math.cosFromSin(double4, arg2);
        double double6 = -double0;
        double double7 = -double2;
        double double8 = -double4;
        double double9 = double6 * double7;
        double double10 = double1 * double7;
        this._m20(double2)
            ._m21(double6 * double3)
            ._m22(double1 * double3)
            ._m00(double3 * double5)
            ._m01(double9 * double5 + double1 * double4)
            ._m02(double10 * double5 + double0 * double4)
            ._m10(double3 * double8)
            ._m11(double9 * double8 + double1 * double5)
            ._m12(double10 * double8 + double0 * double5)
            .properties &= -14;
        return this;
    }

    public Matrix4d setRotationZYX(double arg0, double arg1, double arg2) {
        double double0 = Math.sin(arg2);
        double double1 = Math.cosFromSin(double0, arg2);
        double double2 = Math.sin(arg1);
        double double3 = Math.cosFromSin(double2, arg1);
        double double4 = Math.sin(arg0);
        double double5 = Math.cosFromSin(double4, arg0);
        double double6 = -double4;
        double double7 = -double2;
        double double8 = -double0;
        double double9 = double5 * double2;
        double double10 = double4 * double2;
        this._m00(double5 * double3)
            ._m01(double4 * double3)
            ._m02(double7)
            ._m10(double6 * double1 + double9 * double0)
            ._m11(double5 * double1 + double10 * double0)
            ._m12(double3 * double0)
            ._m20(double6 * double8 + double9 * double1)
            ._m21(double5 * double8 + double10 * double1)
            ._m22(double3 * double1)
            .properties &= -14;
        return this;
    }

    public Matrix4d setRotationYXZ(double arg0, double arg1, double arg2) {
        double double0 = Math.sin(arg1);
        double double1 = Math.cosFromSin(double0, arg1);
        double double2 = Math.sin(arg0);
        double double3 = Math.cosFromSin(double2, arg0);
        double double4 = Math.sin(arg2);
        double double5 = Math.cosFromSin(double4, arg2);
        double double6 = -double2;
        double double7 = -double0;
        double double8 = -double4;
        double double9 = double2 * double0;
        double double10 = double3 * double0;
        this._m20(double2 * double1)
            ._m21(double7)
            ._m22(double3 * double1)
            ._m00(double3 * double5 + double9 * double4)
            ._m01(double1 * double4)
            ._m02(double6 * double5 + double10 * double4)
            ._m10(double3 * double8 + double9 * double5)
            ._m11(double1 * double5)
            ._m12(double6 * double8 + double10 * double5)
            .properties &= -14;
        return this;
    }

    public Matrix4d rotation(double arg0, Vector3dc arg1) {
        return this.rotation(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix4d rotation(double arg0, Vector3fc arg1) {
        return this.rotation(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    @Override
    public Vector4d transform(Vector4d arg0) {
        return arg0.mul(this);
    }

    @Override
    public Vector4d transform(Vector4dc arg0, Vector4d arg1) {
        return arg0.mul(this, arg1);
    }

    @Override
    public Vector4d transform(double arg0, double arg1, double arg2, double arg3, Vector4d arg4) {
        return arg4.set(
            this.m00 * arg0 + this.m10 * arg1 + this.m20 * arg2 + this.m30 * arg3,
            this.m01 * arg0 + this.m11 * arg1 + this.m21 * arg2 + this.m31 * arg3,
            this.m02 * arg0 + this.m12 * arg1 + this.m22 * arg2 + this.m32 * arg3,
            this.m03 * arg0 + this.m13 * arg1 + this.m23 * arg2 + this.m33 * arg3
        );
    }

    @Override
    public Vector4d transformTranspose(Vector4d arg0) {
        return arg0.mulTranspose(this);
    }

    @Override
    public Vector4d transformTranspose(Vector4dc arg0, Vector4d arg1) {
        return arg0.mulTranspose(this, arg1);
    }

    @Override
    public Vector4d transformTranspose(double arg0, double arg1, double arg2, double arg3, Vector4d arg4) {
        return arg4.set(arg0, arg1, arg2, arg3).mulTranspose(this);
    }

    @Override
    public Vector4d transformProject(Vector4d arg0) {
        return arg0.mulProject(this);
    }

    @Override
    public Vector4d transformProject(Vector4dc arg0, Vector4d arg1) {
        return arg0.mulProject(this, arg1);
    }

    @Override
    public Vector4d transformProject(double arg0, double arg1, double arg2, double arg3, Vector4d arg4) {
        double double0 = 1.0 / (this.m03 * arg0 + this.m13 * arg1 + this.m23 * arg2 + this.m33 * arg3);
        return arg4.set(
            (this.m00 * arg0 + this.m10 * arg1 + this.m20 * arg2 + this.m30 * arg3) * double0,
            (this.m01 * arg0 + this.m11 * arg1 + this.m21 * arg2 + this.m31 * arg3) * double0,
            (this.m02 * arg0 + this.m12 * arg1 + this.m22 * arg2 + this.m32 * arg3) * double0,
            1.0
        );
    }

    @Override
    public Vector3d transformProject(Vector3d arg0) {
        return arg0.mulProject(this);
    }

    @Override
    public Vector3d transformProject(Vector3dc arg0, Vector3d arg1) {
        return arg0.mulProject(this, arg1);
    }

    @Override
    public Vector3d transformProject(double arg0, double arg1, double arg2, Vector3d arg3) {
        double double0 = 1.0 / (this.m03 * arg0 + this.m13 * arg1 + this.m23 * arg2 + this.m33);
        return arg3.set(
            (this.m00 * arg0 + this.m10 * arg1 + this.m20 * arg2 + this.m30) * double0,
            (this.m01 * arg0 + this.m11 * arg1 + this.m21 * arg2 + this.m31) * double0,
            (this.m02 * arg0 + this.m12 * arg1 + this.m22 * arg2 + this.m32) * double0
        );
    }

    @Override
    public Vector3d transformProject(Vector4dc arg0, Vector3d arg1) {
        return arg0.mulProject(this, arg1);
    }

    @Override
    public Vector3d transformProject(double arg0, double arg1, double arg2, double arg3, Vector3d arg4) {
        arg4.x = arg0;
        arg4.y = arg1;
        arg4.z = arg2;
        return arg4.mulProject(this, arg3, arg4);
    }

    @Override
    public Vector3d transformPosition(Vector3d arg0) {
        return arg0.set(
            this.m00 * arg0.x + this.m10 * arg0.y + this.m20 * arg0.z + this.m30,
            this.m01 * arg0.x + this.m11 * arg0.y + this.m21 * arg0.z + this.m31,
            this.m02 * arg0.x + this.m12 * arg0.y + this.m22 * arg0.z + this.m32
        );
    }

    @Override
    public Vector3d transformPosition(Vector3dc arg0, Vector3d arg1) {
        return this.transformPosition(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Vector3d transformPosition(double arg0, double arg1, double arg2, Vector3d arg3) {
        return arg3.set(
            this.m00 * arg0 + this.m10 * arg1 + this.m20 * arg2 + this.m30,
            this.m01 * arg0 + this.m11 * arg1 + this.m21 * arg2 + this.m31,
            this.m02 * arg0 + this.m12 * arg1 + this.m22 * arg2 + this.m32
        );
    }

    @Override
    public Vector3d transformDirection(Vector3d arg0) {
        return arg0.set(
            this.m00 * arg0.x + this.m10 * arg0.y + this.m20 * arg0.z,
            this.m01 * arg0.x + this.m11 * arg0.y + this.m21 * arg0.z,
            this.m02 * arg0.x + this.m12 * arg0.y + this.m22 * arg0.z
        );
    }

    @Override
    public Vector3d transformDirection(Vector3dc arg0, Vector3d arg1) {
        return arg1.set(
            this.m00 * arg0.x() + this.m10 * arg0.y() + this.m20 * arg0.z(),
            this.m01 * arg0.x() + this.m11 * arg0.y() + this.m21 * arg0.z(),
            this.m02 * arg0.x() + this.m12 * arg0.y() + this.m22 * arg0.z()
        );
    }

    @Override
    public Vector3d transformDirection(double arg0, double arg1, double arg2, Vector3d arg3) {
        return arg3.set(
            this.m00 * arg0 + this.m10 * arg1 + this.m20 * arg2,
            this.m01 * arg0 + this.m11 * arg1 + this.m21 * arg2,
            this.m02 * arg0 + this.m12 * arg1 + this.m22 * arg2
        );
    }

    @Override
    public Vector3f transformDirection(Vector3f arg0) {
        return arg0.mulDirection(this);
    }

    @Override
    public Vector3f transformDirection(Vector3fc arg0, Vector3f arg1) {
        return arg0.mulDirection(this, arg1);
    }

    @Override
    public Vector3f transformDirection(double arg0, double arg1, double arg2, Vector3f arg3) {
        float float0 = (float)(this.m00 * arg0 + this.m10 * arg1 + this.m20 * arg2);
        float float1 = (float)(this.m01 * arg0 + this.m11 * arg1 + this.m21 * arg2);
        float float2 = (float)(this.m02 * arg0 + this.m12 * arg1 + this.m22 * arg2);
        arg3.x = float0;
        arg3.y = float1;
        arg3.z = float2;
        return arg3;
    }

    @Override
    public Vector4d transformAffine(Vector4d arg0) {
        return arg0.mulAffine(this, arg0);
    }

    @Override
    public Vector4d transformAffine(Vector4dc arg0, Vector4d arg1) {
        return this.transformAffine(arg0.x(), arg0.y(), arg0.z(), arg0.w(), arg1);
    }

    @Override
    public Vector4d transformAffine(double arg0, double arg1, double arg2, double arg3, Vector4d arg4) {
        double double0 = this.m00 * arg0 + this.m10 * arg1 + this.m20 * arg2 + this.m30 * arg3;
        double double1 = this.m01 * arg0 + this.m11 * arg1 + this.m21 * arg2 + this.m31 * arg3;
        double double2 = this.m02 * arg0 + this.m12 * arg1 + this.m22 * arg2 + this.m32 * arg3;
        arg4.x = double0;
        arg4.y = double1;
        arg4.z = double2;
        arg4.w = arg3;
        return arg4;
    }

    public Matrix4d set3x3(Matrix3dc arg0) {
        return this._m00(arg0.m00())
            ._m01(arg0.m01())
            ._m02(arg0.m02())
            ._m10(arg0.m10())
            ._m11(arg0.m11())
            ._m12(arg0.m12())
            ._m20(arg0.m20())
            ._m21(arg0.m21())
            ._m22(arg0.m22())
            ._properties(this.properties & -30);
    }

    @Override
    public Matrix4d scale(Vector3dc arg0, Matrix4d arg1) {
        return this.scale(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    public Matrix4d scale(Vector3dc arg0) {
        return this.scale(arg0.x(), arg0.y(), arg0.z(), this);
    }

    @Override
    public Matrix4d scale(double arg0, double arg1, double arg2, Matrix4d arg3) {
        return (this.properties & 4) != 0 ? arg3.scaling(arg0, arg1, arg2) : this.scaleGeneric(arg0, arg1, arg2, arg3);
    }

    private Matrix4d scaleGeneric(double double2, double double1, double double0, Matrix4d matrix4d1) {
        boolean boolean0 = Math.absEqualsOne(double2) && Math.absEqualsOne(double1) && Math.absEqualsOne(double0);
        matrix4d1._m00(this.m00 * double2)
            ._m01(this.m01 * double2)
            ._m02(this.m02 * double2)
            ._m03(this.m03 * double2)
            ._m10(this.m10 * double1)
            ._m11(this.m11 * double1)
            ._m12(this.m12 * double1)
            ._m13(this.m13 * double1)
            ._m20(this.m20 * double0)
            ._m21(this.m21 * double0)
            ._m22(this.m22 * double0)
            ._m23(this.m23 * double0)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & ~(13 | (boolean0 ? 0 : 16)));
        return matrix4d1;
    }

    public Matrix4d scale(double arg0, double arg1, double arg2) {
        return this.scale(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4d scale(double arg0, Matrix4d arg1) {
        return this.scale(arg0, arg0, arg0, arg1);
    }

    public Matrix4d scale(double arg0) {
        return this.scale(arg0, arg0, arg0);
    }

    @Override
    public Matrix4d scaleXY(double arg0, double arg1, Matrix4d arg2) {
        return this.scale(arg0, arg1, 1.0, arg2);
    }

    public Matrix4d scaleXY(double arg0, double arg1) {
        return this.scale(arg0, arg1, 1.0);
    }

    @Override
    public Matrix4d scaleAround(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6) {
        double double0 = this.m00 * arg3 + this.m10 * arg4 + this.m20 * arg5 + this.m30;
        double double1 = this.m01 * arg3 + this.m11 * arg4 + this.m21 * arg5 + this.m31;
        double double2 = this.m02 * arg3 + this.m12 * arg4 + this.m22 * arg5 + this.m32;
        double double3 = this.m03 * arg3 + this.m13 * arg4 + this.m23 * arg5 + this.m33;
        boolean boolean0 = Math.absEqualsOne(arg0) && Math.absEqualsOne(arg1) && Math.absEqualsOne(arg2);
        arg6._m00(this.m00 * arg0)
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
            ._m30(-this.m00 * arg3 - this.m10 * arg4 - this.m20 * arg5 + double0)
            ._m31(-this.m01 * arg3 - this.m11 * arg4 - this.m21 * arg5 + double1)
            ._m32(-this.m02 * arg3 - this.m12 * arg4 - this.m22 * arg5 + double2)
            ._m33(-this.m03 * arg3 - this.m13 * arg4 - this.m23 * arg5 + double3)
            ._properties(this.properties & ~(13 | (boolean0 ? 0 : 16)));
        return arg6;
    }

    public Matrix4d scaleAround(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.scaleAround(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    public Matrix4d scaleAround(double arg0, double arg1, double arg2, double arg3) {
        return this.scaleAround(arg0, arg0, arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix4d scaleAround(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4) {
        return this.scaleAround(arg0, arg0, arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public Matrix4d scaleLocal(double arg0, double arg1, double arg2, Matrix4d arg3) {
        return (this.properties & 4) != 0 ? arg3.scaling(arg0, arg1, arg2) : this.scaleLocalGeneric(arg0, arg1, arg2, arg3);
    }

    private Matrix4d scaleLocalGeneric(double double1, double double3, double double5, Matrix4d matrix4d1) {
        double double0 = double1 * this.m00;
        double double2 = double3 * this.m01;
        double double4 = double5 * this.m02;
        double double6 = double1 * this.m10;
        double double7 = double3 * this.m11;
        double double8 = double5 * this.m12;
        double double9 = double1 * this.m20;
        double double10 = double3 * this.m21;
        double double11 = double5 * this.m22;
        double double12 = double1 * this.m30;
        double double13 = double3 * this.m31;
        double double14 = double5 * this.m32;
        boolean boolean0 = Math.absEqualsOne(double1) && Math.absEqualsOne(double3) && Math.absEqualsOne(double5);
        matrix4d1._m00(double0)
            ._m01(double2)
            ._m02(double4)
            ._m03(this.m03)
            ._m10(double6)
            ._m11(double7)
            ._m12(double8)
            ._m13(this.m13)
            ._m20(double9)
            ._m21(double10)
            ._m22(double11)
            ._m23(this.m23)
            ._m30(double12)
            ._m31(double13)
            ._m32(double14)
            ._m33(this.m33)
            ._properties(this.properties & ~(13 | (boolean0 ? 0 : 16)));
        return matrix4d1;
    }

    @Override
    public Matrix4d scaleLocal(double arg0, Matrix4d arg1) {
        return this.scaleLocal(arg0, arg0, arg0, arg1);
    }

    public Matrix4d scaleLocal(double arg0) {
        return this.scaleLocal(arg0, this);
    }

    public Matrix4d scaleLocal(double arg0, double arg1, double arg2) {
        return this.scaleLocal(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4d scaleAroundLocal(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6) {
        boolean boolean0 = Math.absEqualsOne(arg0) && Math.absEqualsOne(arg1) && Math.absEqualsOne(arg2);
        arg6._m00(arg0 * (this.m00 - arg3 * this.m03) + arg3 * this.m03)
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
        return arg6;
    }

    public Matrix4d scaleAroundLocal(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.scaleAroundLocal(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    public Matrix4d scaleAroundLocal(double arg0, double arg1, double arg2, double arg3) {
        return this.scaleAroundLocal(arg0, arg0, arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix4d scaleAroundLocal(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4) {
        return this.scaleAroundLocal(arg0, arg0, arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public Matrix4d rotate(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4) {
        if ((this.properties & 4) != 0) {
            return arg4.rotation(arg0, arg1, arg2, arg3);
        } else if ((this.properties & 8) != 0) {
            return this.rotateTranslation(arg0, arg1, arg2, arg3, arg4);
        } else {
            return (this.properties & 2) != 0 ? this.rotateAffine(arg0, arg1, arg2, arg3, arg4) : this.rotateGeneric(arg0, arg1, arg2, arg3, arg4);
        }
    }

    private Matrix4d rotateGeneric(double double3, double double0, double double2, double double1, Matrix4d matrix4d1) {
        if (double2 == 0.0 && double1 == 0.0 && Math.absEqualsOne(double0)) {
            return this.rotateX(double0 * double3, matrix4d1);
        } else if (double0 == 0.0 && double1 == 0.0 && Math.absEqualsOne(double2)) {
            return this.rotateY(double2 * double3, matrix4d1);
        } else {
            return double0 == 0.0 && double2 == 0.0 && Math.absEqualsOne(double1)
                ? this.rotateZ(double1 * double3, matrix4d1)
                : this.rotateGenericInternal(double3, double0, double2, double1, matrix4d1);
        }
    }

    private Matrix4d rotateGenericInternal(double double1, double double5, double double7, double double9, Matrix4d matrix4d1) {
        double double0 = Math.sin(double1);
        double double2 = Math.cosFromSin(double0, double1);
        double double3 = 1.0 - double2;
        double double4 = double5 * double5;
        double double6 = double5 * double7;
        double double8 = double5 * double9;
        double double10 = double7 * double7;
        double double11 = double7 * double9;
        double double12 = double9 * double9;
        double double13 = double4 * double3 + double2;
        double double14 = double6 * double3 + double9 * double0;
        double double15 = double8 * double3 - double7 * double0;
        double double16 = double6 * double3 - double9 * double0;
        double double17 = double10 * double3 + double2;
        double double18 = double11 * double3 + double5 * double0;
        double double19 = double8 * double3 + double7 * double0;
        double double20 = double11 * double3 - double5 * double0;
        double double21 = double12 * double3 + double2;
        double double22 = this.m00 * double13 + this.m10 * double14 + this.m20 * double15;
        double double23 = this.m01 * double13 + this.m11 * double14 + this.m21 * double15;
        double double24 = this.m02 * double13 + this.m12 * double14 + this.m22 * double15;
        double double25 = this.m03 * double13 + this.m13 * double14 + this.m23 * double15;
        double double26 = this.m00 * double16 + this.m10 * double17 + this.m20 * double18;
        double double27 = this.m01 * double16 + this.m11 * double17 + this.m21 * double18;
        double double28 = this.m02 * double16 + this.m12 * double17 + this.m22 * double18;
        double double29 = this.m03 * double16 + this.m13 * double17 + this.m23 * double18;
        matrix4d1._m20(this.m00 * double19 + this.m10 * double20 + this.m20 * double21)
            ._m21(this.m01 * double19 + this.m11 * double20 + this.m21 * double21)
            ._m22(this.m02 * double19 + this.m12 * double20 + this.m22 * double21)
            ._m23(this.m03 * double19 + this.m13 * double20 + this.m23 * double21)
            ._m00(double22)
            ._m01(double23)
            ._m02(double24)
            ._m03(double25)
            ._m10(double26)
            ._m11(double27)
            ._m12(double28)
            ._m13(double29)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return matrix4d1;
    }

    public Matrix4d rotate(double arg0, double arg1, double arg2, double arg3) {
        return this.rotate(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix4d rotateTranslation(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4) {
        double double0 = this.m30;
        double double1 = this.m31;
        double double2 = this.m32;
        if (arg2 == 0.0 && arg3 == 0.0 && Math.absEqualsOne(arg1)) {
            return arg4.rotationX(arg1 * arg0).setTranslation(double0, double1, double2);
        } else if (arg1 == 0.0 && arg3 == 0.0 && Math.absEqualsOne(arg2)) {
            return arg4.rotationY(arg2 * arg0).setTranslation(double0, double1, double2);
        } else {
            return arg1 == 0.0 && arg2 == 0.0 && Math.absEqualsOne(arg3)
                ? arg4.rotationZ(arg3 * arg0).setTranslation(double0, double1, double2)
                : this.rotateTranslationInternal(arg0, arg1, arg2, arg3, arg4);
        }
    }

    private Matrix4d rotateTranslationInternal(double double1, double double5, double double7, double double9, Matrix4d matrix4d1) {
        double double0 = Math.sin(double1);
        double double2 = Math.cosFromSin(double0, double1);
        double double3 = 1.0 - double2;
        double double4 = double5 * double5;
        double double6 = double5 * double7;
        double double8 = double5 * double9;
        double double10 = double7 * double7;
        double double11 = double7 * double9;
        double double12 = double9 * double9;
        double double13 = double4 * double3 + double2;
        double double14 = double6 * double3 + double9 * double0;
        double double15 = double8 * double3 - double7 * double0;
        double double16 = double6 * double3 - double9 * double0;
        double double17 = double10 * double3 + double2;
        double double18 = double11 * double3 + double5 * double0;
        double double19 = double8 * double3 + double7 * double0;
        double double20 = double11 * double3 - double5 * double0;
        double double21 = double12 * double3 + double2;
        matrix4d1._m20(double19)
            ._m21(double20)
            ._m22(double21)
            ._m00(double13)
            ._m01(double14)
            ._m02(double15)
            ._m03(0.0)
            ._m10(double16)
            ._m11(double17)
            ._m12(double18)
            ._m13(0.0)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return matrix4d1;
    }

    @Override
    public Matrix4d rotateAffine(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4) {
        if (arg2 == 0.0 && arg3 == 0.0 && Math.absEqualsOne(arg1)) {
            return this.rotateX(arg1 * arg0, arg4);
        } else if (arg1 == 0.0 && arg3 == 0.0 && Math.absEqualsOne(arg2)) {
            return this.rotateY(arg2 * arg0, arg4);
        } else {
            return arg1 == 0.0 && arg2 == 0.0 && Math.absEqualsOne(arg3)
                ? this.rotateZ(arg3 * arg0, arg4)
                : this.rotateAffineInternal(arg0, arg1, arg2, arg3, arg4);
        }
    }

    private Matrix4d rotateAffineInternal(double double1, double double5, double double7, double double9, Matrix4d matrix4d1) {
        double double0 = Math.sin(double1);
        double double2 = Math.cosFromSin(double0, double1);
        double double3 = 1.0 - double2;
        double double4 = double5 * double5;
        double double6 = double5 * double7;
        double double8 = double5 * double9;
        double double10 = double7 * double7;
        double double11 = double7 * double9;
        double double12 = double9 * double9;
        double double13 = double4 * double3 + double2;
        double double14 = double6 * double3 + double9 * double0;
        double double15 = double8 * double3 - double7 * double0;
        double double16 = double6 * double3 - double9 * double0;
        double double17 = double10 * double3 + double2;
        double double18 = double11 * double3 + double5 * double0;
        double double19 = double8 * double3 + double7 * double0;
        double double20 = double11 * double3 - double5 * double0;
        double double21 = double12 * double3 + double2;
        double double22 = this.m00 * double13 + this.m10 * double14 + this.m20 * double15;
        double double23 = this.m01 * double13 + this.m11 * double14 + this.m21 * double15;
        double double24 = this.m02 * double13 + this.m12 * double14 + this.m22 * double15;
        double double25 = this.m00 * double16 + this.m10 * double17 + this.m20 * double18;
        double double26 = this.m01 * double16 + this.m11 * double17 + this.m21 * double18;
        double double27 = this.m02 * double16 + this.m12 * double17 + this.m22 * double18;
        matrix4d1._m20(this.m00 * double19 + this.m10 * double20 + this.m20 * double21)
            ._m21(this.m01 * double19 + this.m11 * double20 + this.m21 * double21)
            ._m22(this.m02 * double19 + this.m12 * double20 + this.m22 * double21)
            ._m23(0.0)
            ._m00(double22)
            ._m01(double23)
            ._m02(double24)
            ._m03(0.0)
            ._m10(double25)
            ._m11(double26)
            ._m12(double27)
            ._m13(0.0)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return matrix4d1;
    }

    public Matrix4d rotateAffine(double arg0, double arg1, double arg2, double arg3) {
        return this.rotateAffine(arg0, arg1, arg2, arg3, this);
    }

    public Matrix4d rotateAround(Quaterniondc arg0, double arg1, double arg2, double arg3) {
        return this.rotateAround(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix4d rotateAroundAffine(Quaterniondc arg0, double arg1, double arg2, double arg3, Matrix4d arg4) {
        double double0 = arg0.w() * arg0.w();
        double double1 = arg0.x() * arg0.x();
        double double2 = arg0.y() * arg0.y();
        double double3 = arg0.z() * arg0.z();
        double double4 = arg0.z() * arg0.w();
        double double5 = double4 + double4;
        double double6 = arg0.x() * arg0.y();
        double double7 = double6 + double6;
        double double8 = arg0.x() * arg0.z();
        double double9 = double8 + double8;
        double double10 = arg0.y() * arg0.w();
        double double11 = double10 + double10;
        double double12 = arg0.y() * arg0.z();
        double double13 = double12 + double12;
        double double14 = arg0.x() * arg0.w();
        double double15 = double14 + double14;
        double double16 = double0 + double1 - double3 - double2;
        double double17 = double7 + double5;
        double double18 = double9 - double11;
        double double19 = -double5 + double7;
        double double20 = double2 - double3 + double0 - double1;
        double double21 = double13 + double15;
        double double22 = double11 + double9;
        double double23 = double13 - double15;
        double double24 = double3 - double2 - double1 + double0;
        double double25 = this.m00 * arg1 + this.m10 * arg2 + this.m20 * arg3 + this.m30;
        double double26 = this.m01 * arg1 + this.m11 * arg2 + this.m21 * arg3 + this.m31;
        double double27 = this.m02 * arg1 + this.m12 * arg2 + this.m22 * arg3 + this.m32;
        double double28 = this.m00 * double16 + this.m10 * double17 + this.m20 * double18;
        double double29 = this.m01 * double16 + this.m11 * double17 + this.m21 * double18;
        double double30 = this.m02 * double16 + this.m12 * double17 + this.m22 * double18;
        double double31 = this.m00 * double19 + this.m10 * double20 + this.m20 * double21;
        double double32 = this.m01 * double19 + this.m11 * double20 + this.m21 * double21;
        double double33 = this.m02 * double19 + this.m12 * double20 + this.m22 * double21;
        arg4._m20(this.m00 * double22 + this.m10 * double23 + this.m20 * double24)
            ._m21(this.m01 * double22 + this.m11 * double23 + this.m21 * double24)
            ._m22(this.m02 * double22 + this.m12 * double23 + this.m22 * double24)
            ._m23(0.0)
            ._m00(double28)
            ._m01(double29)
            ._m02(double30)
            ._m03(0.0)
            ._m10(double31)
            ._m11(double32)
            ._m12(double33)
            ._m13(0.0)
            ._m30(-double28 * arg1 - double31 * arg2 - this.m20 * arg3 + double25)
            ._m31(-double29 * arg1 - double32 * arg2 - this.m21 * arg3 + double26)
            ._m32(-double30 * arg1 - double33 * arg2 - this.m22 * arg3 + double27)
            ._m33(1.0)
            ._properties(this.properties & -14);
        return arg4;
    }

    @Override
    public Matrix4d rotateAround(Quaterniondc arg0, double arg1, double arg2, double arg3, Matrix4d arg4) {
        if ((this.properties & 4) != 0) {
            return this.rotationAround(arg0, arg1, arg2, arg3);
        } else {
            return (this.properties & 2) != 0 ? this.rotateAroundAffine(arg0, arg1, arg2, arg3, this) : this.rotateAroundGeneric(arg0, arg1, arg2, arg3, this);
        }
    }

    private Matrix4d rotateAroundGeneric(Quaterniondc quaterniondc, double double28, double double27, double double26, Matrix4d matrix4d1) {
        double double0 = quaterniondc.w() * quaterniondc.w();
        double double1 = quaterniondc.x() * quaterniondc.x();
        double double2 = quaterniondc.y() * quaterniondc.y();
        double double3 = quaterniondc.z() * quaterniondc.z();
        double double4 = quaterniondc.z() * quaterniondc.w();
        double double5 = double4 + double4;
        double double6 = quaterniondc.x() * quaterniondc.y();
        double double7 = double6 + double6;
        double double8 = quaterniondc.x() * quaterniondc.z();
        double double9 = double8 + double8;
        double double10 = quaterniondc.y() * quaterniondc.w();
        double double11 = double10 + double10;
        double double12 = quaterniondc.y() * quaterniondc.z();
        double double13 = double12 + double12;
        double double14 = quaterniondc.x() * quaterniondc.w();
        double double15 = double14 + double14;
        double double16 = double0 + double1 - double3 - double2;
        double double17 = double7 + double5;
        double double18 = double9 - double11;
        double double19 = -double5 + double7;
        double double20 = double2 - double3 + double0 - double1;
        double double21 = double13 + double15;
        double double22 = double11 + double9;
        double double23 = double13 - double15;
        double double24 = double3 - double2 - double1 + double0;
        double double25 = this.m00 * double28 + this.m10 * double27 + this.m20 * double26 + this.m30;
        double double29 = this.m01 * double28 + this.m11 * double27 + this.m21 * double26 + this.m31;
        double double30 = this.m02 * double28 + this.m12 * double27 + this.m22 * double26 + this.m32;
        double double31 = this.m00 * double16 + this.m10 * double17 + this.m20 * double18;
        double double32 = this.m01 * double16 + this.m11 * double17 + this.m21 * double18;
        double double33 = this.m02 * double16 + this.m12 * double17 + this.m22 * double18;
        double double34 = this.m03 * double16 + this.m13 * double17 + this.m23 * double18;
        double double35 = this.m00 * double19 + this.m10 * double20 + this.m20 * double21;
        double double36 = this.m01 * double19 + this.m11 * double20 + this.m21 * double21;
        double double37 = this.m02 * double19 + this.m12 * double20 + this.m22 * double21;
        double double38 = this.m03 * double19 + this.m13 * double20 + this.m23 * double21;
        matrix4d1._m20(this.m00 * double22 + this.m10 * double23 + this.m20 * double24)
            ._m21(this.m01 * double22 + this.m11 * double23 + this.m21 * double24)
            ._m22(this.m02 * double22 + this.m12 * double23 + this.m22 * double24)
            ._m23(this.m03 * double22 + this.m13 * double23 + this.m23 * double24)
            ._m00(double31)
            ._m01(double32)
            ._m02(double33)
            ._m03(double34)
            ._m10(double35)
            ._m11(double36)
            ._m12(double37)
            ._m13(double38)
            ._m30(-double31 * double28 - double35 * double27 - this.m20 * double26 + double25)
            ._m31(-double32 * double28 - double36 * double27 - this.m21 * double26 + double29)
            ._m32(-double33 * double28 - double37 * double27 - this.m22 * double26 + double30)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return matrix4d1;
    }

    public Matrix4d rotationAround(Quaterniondc arg0, double arg1, double arg2, double arg3) {
        double double0 = arg0.w() * arg0.w();
        double double1 = arg0.x() * arg0.x();
        double double2 = arg0.y() * arg0.y();
        double double3 = arg0.z() * arg0.z();
        double double4 = arg0.z() * arg0.w();
        double double5 = double4 + double4;
        double double6 = arg0.x() * arg0.y();
        double double7 = double6 + double6;
        double double8 = arg0.x() * arg0.z();
        double double9 = double8 + double8;
        double double10 = arg0.y() * arg0.w();
        double double11 = double10 + double10;
        double double12 = arg0.y() * arg0.z();
        double double13 = double12 + double12;
        double double14 = arg0.x() * arg0.w();
        double double15 = double14 + double14;
        this._m20(double11 + double9);
        this._m21(double13 - double15);
        this._m22(double3 - double2 - double1 + double0);
        this._m23(0.0);
        this._m00(double0 + double1 - double3 - double2);
        this._m01(double7 + double5);
        this._m02(double9 - double11);
        this._m03(0.0);
        this._m10(-double5 + double7);
        this._m11(double2 - double3 + double0 - double1);
        this._m12(double13 + double15);
        this._m13(0.0);
        this._m30(-this.m00 * arg1 - this.m10 * arg2 - this.m20 * arg3 + arg1);
        this._m31(-this.m01 * arg1 - this.m11 * arg2 - this.m21 * arg3 + arg2);
        this._m32(-this.m02 * arg1 - this.m12 * arg2 - this.m22 * arg3 + arg3);
        this._m33(1.0);
        this.properties = 18;
        return this;
    }

    @Override
    public Matrix4d rotateLocal(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4) {
        return (this.properties & 4) != 0 ? arg4.rotation(arg0, arg1, arg2, arg3) : this.rotateLocalGeneric(arg0, arg1, arg2, arg3, arg4);
    }

    private Matrix4d rotateLocalGeneric(double double3, double double0, double double2, double double1, Matrix4d matrix4d1) {
        if (double2 == 0.0 && double1 == 0.0 && Math.absEqualsOne(double0)) {
            return this.rotateLocalX(double0 * double3, matrix4d1);
        } else if (double0 == 0.0 && double1 == 0.0 && Math.absEqualsOne(double2)) {
            return this.rotateLocalY(double2 * double3, matrix4d1);
        } else {
            return double0 == 0.0 && double2 == 0.0 && Math.absEqualsOne(double1)
                ? this.rotateLocalZ(double1 * double3, matrix4d1)
                : this.rotateLocalGenericInternal(double3, double0, double2, double1, matrix4d1);
        }
    }

    private Matrix4d rotateLocalGenericInternal(double double1, double double5, double double7, double double9, Matrix4d matrix4d1) {
        double double0 = Math.sin(double1);
        double double2 = Math.cosFromSin(double0, double1);
        double double3 = 1.0 - double2;
        double double4 = double5 * double5;
        double double6 = double5 * double7;
        double double8 = double5 * double9;
        double double10 = double7 * double7;
        double double11 = double7 * double9;
        double double12 = double9 * double9;
        double double13 = double4 * double3 + double2;
        double double14 = double6 * double3 + double9 * double0;
        double double15 = double8 * double3 - double7 * double0;
        double double16 = double6 * double3 - double9 * double0;
        double double17 = double10 * double3 + double2;
        double double18 = double11 * double3 + double5 * double0;
        double double19 = double8 * double3 + double7 * double0;
        double double20 = double11 * double3 - double5 * double0;
        double double21 = double12 * double3 + double2;
        double double22 = double13 * this.m00 + double16 * this.m01 + double19 * this.m02;
        double double23 = double14 * this.m00 + double17 * this.m01 + double20 * this.m02;
        double double24 = double15 * this.m00 + double18 * this.m01 + double21 * this.m02;
        double double25 = double13 * this.m10 + double16 * this.m11 + double19 * this.m12;
        double double26 = double14 * this.m10 + double17 * this.m11 + double20 * this.m12;
        double double27 = double15 * this.m10 + double18 * this.m11 + double21 * this.m12;
        double double28 = double13 * this.m20 + double16 * this.m21 + double19 * this.m22;
        double double29 = double14 * this.m20 + double17 * this.m21 + double20 * this.m22;
        double double30 = double15 * this.m20 + double18 * this.m21 + double21 * this.m22;
        double double31 = double13 * this.m30 + double16 * this.m31 + double19 * this.m32;
        double double32 = double14 * this.m30 + double17 * this.m31 + double20 * this.m32;
        double double33 = double15 * this.m30 + double18 * this.m31 + double21 * this.m32;
        matrix4d1._m00(double22)
            ._m01(double23)
            ._m02(double24)
            ._m03(this.m03)
            ._m10(double25)
            ._m11(double26)
            ._m12(double27)
            ._m13(this.m13)
            ._m20(double28)
            ._m21(double29)
            ._m22(double30)
            ._m23(this.m23)
            ._m30(double31)
            ._m31(double32)
            ._m32(double33)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return matrix4d1;
    }

    public Matrix4d rotateLocal(double arg0, double arg1, double arg2, double arg3) {
        return this.rotateLocal(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix4d rotateAroundLocal(Quaterniondc arg0, double arg1, double arg2, double arg3, Matrix4d arg4) {
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
        double double10 = double0 + double1 - double3 - double2;
        double double11 = double5 + double4 + double4 + double5;
        double double12 = double6 - double7 + double6 - double7;
        double double13 = -double4 + double5 - double4 + double5;
        double double14 = double2 - double3 + double0 - double1;
        double double15 = double8 + double8 + double9 + double9;
        double double16 = double7 + double6 + double6 + double7;
        double double17 = double8 + double8 - double9 - double9;
        double double18 = double3 - double2 - double1 + double0;
        double double19 = this.m00 - arg1 * this.m03;
        double double20 = this.m01 - arg2 * this.m03;
        double double21 = this.m02 - arg3 * this.m03;
        double double22 = this.m10 - arg1 * this.m13;
        double double23 = this.m11 - arg2 * this.m13;
        double double24 = this.m12 - arg3 * this.m13;
        double double25 = this.m20 - arg1 * this.m23;
        double double26 = this.m21 - arg2 * this.m23;
        double double27 = this.m22 - arg3 * this.m23;
        double double28 = this.m30 - arg1 * this.m33;
        double double29 = this.m31 - arg2 * this.m33;
        double double30 = this.m32 - arg3 * this.m33;
        arg4._m00(double10 * double19 + double13 * double20 + double16 * double21 + arg1 * this.m03)
            ._m01(double11 * double19 + double14 * double20 + double17 * double21 + arg2 * this.m03)
            ._m02(double12 * double19 + double15 * double20 + double18 * double21 + arg3 * this.m03)
            ._m03(this.m03)
            ._m10(double10 * double22 + double13 * double23 + double16 * double24 + arg1 * this.m13)
            ._m11(double11 * double22 + double14 * double23 + double17 * double24 + arg2 * this.m13)
            ._m12(double12 * double22 + double15 * double23 + double18 * double24 + arg3 * this.m13)
            ._m13(this.m13)
            ._m20(double10 * double25 + double13 * double26 + double16 * double27 + arg1 * this.m23)
            ._m21(double11 * double25 + double14 * double26 + double17 * double27 + arg2 * this.m23)
            ._m22(double12 * double25 + double15 * double26 + double18 * double27 + arg3 * this.m23)
            ._m23(this.m23)
            ._m30(double10 * double28 + double13 * double29 + double16 * double30 + arg1 * this.m33)
            ._m31(double11 * double28 + double14 * double29 + double17 * double30 + arg2 * this.m33)
            ._m32(double12 * double28 + double15 * double29 + double18 * double30 + arg3 * this.m33)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return arg4;
    }

    public Matrix4d rotateAroundLocal(Quaterniondc arg0, double arg1, double arg2, double arg3) {
        return this.rotateAroundLocal(arg0, arg1, arg2, arg3, this);
    }

    public Matrix4d translate(Vector3dc arg0) {
        return this.translate(arg0.x(), arg0.y(), arg0.z());
    }

    @Override
    public Matrix4d translate(Vector3dc arg0, Matrix4d arg1) {
        return this.translate(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    public Matrix4d translate(Vector3fc arg0) {
        return this.translate(arg0.x(), arg0.y(), arg0.z());
    }

    @Override
    public Matrix4d translate(Vector3fc arg0, Matrix4d arg1) {
        return this.translate(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Matrix4d translate(double arg0, double arg1, double arg2, Matrix4d arg3) {
        return (this.properties & 4) != 0 ? arg3.translation(arg0, arg1, arg2) : this.translateGeneric(arg0, arg1, arg2, arg3);
    }

    private Matrix4d translateGeneric(double double0, double double1, double double2, Matrix4d matrix4d1) {
        matrix4d1._m00(this.m00)
            ._m01(this.m01)
            ._m02(this.m02)
            ._m03(this.m03)
            ._m10(this.m10)
            ._m11(this.m11)
            ._m12(this.m12)
            ._m13(this.m13)
            ._m20(this.m20)
            ._m21(this.m21)
            ._m22(this.m22)
            ._m23(this.m23)
            ._m30(Math.fma(this.m00, double0, Math.fma(this.m10, double1, Math.fma(this.m20, double2, this.m30))))
            ._m31(Math.fma(this.m01, double0, Math.fma(this.m11, double1, Math.fma(this.m21, double2, this.m31))))
            ._m32(Math.fma(this.m02, double0, Math.fma(this.m12, double1, Math.fma(this.m22, double2, this.m32))))
            ._m33(Math.fma(this.m03, double0, Math.fma(this.m13, double1, Math.fma(this.m23, double2, this.m33))))
            ._properties(this.properties & -6);
        return matrix4d1;
    }

    public Matrix4d translate(double arg0, double arg1, double arg2) {
        if ((this.properties & 4) != 0) {
            return this.translation(arg0, arg1, arg2);
        } else {
            this._m30(Math.fma(this.m00, arg0, Math.fma(this.m10, arg1, Math.fma(this.m20, arg2, this.m30))));
            this._m31(Math.fma(this.m01, arg0, Math.fma(this.m11, arg1, Math.fma(this.m21, arg2, this.m31))));
            this._m32(Math.fma(this.m02, arg0, Math.fma(this.m12, arg1, Math.fma(this.m22, arg2, this.m32))));
            this._m33(Math.fma(this.m03, arg0, Math.fma(this.m13, arg1, Math.fma(this.m23, arg2, this.m33))));
            this.properties &= -6;
            return this;
        }
    }

    public Matrix4d translateLocal(Vector3fc arg0) {
        return this.translateLocal(arg0.x(), arg0.y(), arg0.z());
    }

    @Override
    public Matrix4d translateLocal(Vector3fc arg0, Matrix4d arg1) {
        return this.translateLocal(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    public Matrix4d translateLocal(Vector3dc arg0) {
        return this.translateLocal(arg0.x(), arg0.y(), arg0.z());
    }

    @Override
    public Matrix4d translateLocal(Vector3dc arg0, Matrix4d arg1) {
        return this.translateLocal(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Matrix4d translateLocal(double arg0, double arg1, double arg2, Matrix4d arg3) {
        return (this.properties & 4) != 0 ? arg3.translation(arg0, arg1, arg2) : this.translateLocalGeneric(arg0, arg1, arg2, arg3);
    }

    private Matrix4d translateLocalGeneric(double double1, double double3, double double5, Matrix4d matrix4d1) {
        double double0 = this.m00 + double1 * this.m03;
        double double2 = this.m01 + double3 * this.m03;
        double double4 = this.m02 + double5 * this.m03;
        double double6 = this.m10 + double1 * this.m13;
        double double7 = this.m11 + double3 * this.m13;
        double double8 = this.m12 + double5 * this.m13;
        double double9 = this.m20 + double1 * this.m23;
        double double10 = this.m21 + double3 * this.m23;
        double double11 = this.m22 + double5 * this.m23;
        double double12 = this.m30 + double1 * this.m33;
        double double13 = this.m31 + double3 * this.m33;
        double double14 = this.m32 + double5 * this.m33;
        return matrix4d1._m00(double0)
            ._m01(double2)
            ._m02(double4)
            ._m03(this.m03)
            ._m10(double6)
            ._m11(double7)
            ._m12(double8)
            ._m13(this.m13)
            ._m20(double9)
            ._m21(double10)
            ._m22(double11)
            ._m23(this.m23)
            ._m30(double12)
            ._m31(double13)
            ._m32(double14)
            ._m33(this.m33)
            ._properties(this.properties & -6);
    }

    public Matrix4d translateLocal(double arg0, double arg1, double arg2) {
        return this.translateLocal(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4d rotateLocalX(double arg0, Matrix4d arg1) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = double0 * this.m01 + double1 * this.m02;
        double double3 = double0 * this.m11 + double1 * this.m12;
        double double4 = double0 * this.m21 + double1 * this.m22;
        double double5 = double0 * this.m31 + double1 * this.m32;
        arg1._m00(this.m00)
            ._m01(double1 * this.m01 - double0 * this.m02)
            ._m02(double2)
            ._m03(this.m03)
            ._m10(this.m10)
            ._m11(double1 * this.m11 - double0 * this.m12)
            ._m12(double3)
            ._m13(this.m13)
            ._m20(this.m20)
            ._m21(double1 * this.m21 - double0 * this.m22)
            ._m22(double4)
            ._m23(this.m23)
            ._m30(this.m30)
            ._m31(double1 * this.m31 - double0 * this.m32)
            ._m32(double5)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return arg1;
    }

    public Matrix4d rotateLocalX(double arg0) {
        return this.rotateLocalX(arg0, this);
    }

    @Override
    public Matrix4d rotateLocalY(double arg0, Matrix4d arg1) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = -double0 * this.m00 + double1 * this.m02;
        double double3 = -double0 * this.m10 + double1 * this.m12;
        double double4 = -double0 * this.m20 + double1 * this.m22;
        double double5 = -double0 * this.m30 + double1 * this.m32;
        arg1._m00(double1 * this.m00 + double0 * this.m02)
            ._m01(this.m01)
            ._m02(double2)
            ._m03(this.m03)
            ._m10(double1 * this.m10 + double0 * this.m12)
            ._m11(this.m11)
            ._m12(double3)
            ._m13(this.m13)
            ._m20(double1 * this.m20 + double0 * this.m22)
            ._m21(this.m21)
            ._m22(double4)
            ._m23(this.m23)
            ._m30(double1 * this.m30 + double0 * this.m32)
            ._m31(this.m31)
            ._m32(double5)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return arg1;
    }

    public Matrix4d rotateLocalY(double arg0) {
        return this.rotateLocalY(arg0, this);
    }

    @Override
    public Matrix4d rotateLocalZ(double arg0, Matrix4d arg1) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = double0 * this.m00 + double1 * this.m01;
        double double3 = double0 * this.m10 + double1 * this.m11;
        double double4 = double0 * this.m20 + double1 * this.m21;
        double double5 = double0 * this.m30 + double1 * this.m31;
        arg1._m00(double1 * this.m00 - double0 * this.m01)
            ._m01(double2)
            ._m02(this.m02)
            ._m03(this.m03)
            ._m10(double1 * this.m10 - double0 * this.m11)
            ._m11(double3)
            ._m12(this.m12)
            ._m13(this.m13)
            ._m20(double1 * this.m20 - double0 * this.m21)
            ._m21(double4)
            ._m22(this.m22)
            ._m23(this.m23)
            ._m30(double1 * this.m30 - double0 * this.m31)
            ._m31(double5)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return arg1;
    }

    public Matrix4d rotateLocalZ(double arg0) {
        return this.rotateLocalZ(arg0, this);
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        arg0.writeDouble(this.m00);
        arg0.writeDouble(this.m01);
        arg0.writeDouble(this.m02);
        arg0.writeDouble(this.m03);
        arg0.writeDouble(this.m10);
        arg0.writeDouble(this.m11);
        arg0.writeDouble(this.m12);
        arg0.writeDouble(this.m13);
        arg0.writeDouble(this.m20);
        arg0.writeDouble(this.m21);
        arg0.writeDouble(this.m22);
        arg0.writeDouble(this.m23);
        arg0.writeDouble(this.m30);
        arg0.writeDouble(this.m31);
        arg0.writeDouble(this.m32);
        arg0.writeDouble(this.m33);
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException {
        this._m00(arg0.readDouble())
            ._m01(arg0.readDouble())
            ._m02(arg0.readDouble())
            ._m03(arg0.readDouble())
            ._m10(arg0.readDouble())
            ._m11(arg0.readDouble())
            ._m12(arg0.readDouble())
            ._m13(arg0.readDouble())
            ._m20(arg0.readDouble())
            ._m21(arg0.readDouble())
            ._m22(arg0.readDouble())
            ._m23(arg0.readDouble())
            ._m30(arg0.readDouble())
            ._m31(arg0.readDouble())
            ._m32(arg0.readDouble())
            ._m33(arg0.readDouble())
            .determineProperties();
    }

    @Override
    public Matrix4d rotateX(double arg0, Matrix4d arg1) {
        if ((this.properties & 4) != 0) {
            return arg1.rotationX(arg0);
        } else if ((this.properties & 8) != 0) {
            double double0 = this.m30;
            double double1 = this.m31;
            double double2 = this.m32;
            return arg1.rotationX(arg0).setTranslation(double0, double1, double2);
        } else {
            return this.rotateXInternal(arg0, arg1);
        }
    }

    private Matrix4d rotateXInternal(double double1, Matrix4d matrix4d1) {
        double double0 = Math.sin(double1);
        double double2 = Math.cosFromSin(double0, double1);
        double double3 = -double0;
        double double4 = this.m10 * double2 + this.m20 * double0;
        double double5 = this.m11 * double2 + this.m21 * double0;
        double double6 = this.m12 * double2 + this.m22 * double0;
        double double7 = this.m13 * double2 + this.m23 * double0;
        matrix4d1._m20(this.m10 * double3 + this.m20 * double2)
            ._m21(this.m11 * double3 + this.m21 * double2)
            ._m22(this.m12 * double3 + this.m22 * double2)
            ._m23(this.m13 * double3 + this.m23 * double2)
            ._m10(double4)
            ._m11(double5)
            ._m12(double6)
            ._m13(double7)
            ._m00(this.m00)
            ._m01(this.m01)
            ._m02(this.m02)
            ._m03(this.m03)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return matrix4d1;
    }

    public Matrix4d rotateX(double arg0) {
        return this.rotateX(arg0, this);
    }

    @Override
    public Matrix4d rotateY(double arg0, Matrix4d arg1) {
        if ((this.properties & 4) != 0) {
            return arg1.rotationY(arg0);
        } else if ((this.properties & 8) != 0) {
            double double0 = this.m30;
            double double1 = this.m31;
            double double2 = this.m32;
            return arg1.rotationY(arg0).setTranslation(double0, double1, double2);
        } else {
            return this.rotateYInternal(arg0, arg1);
        }
    }

    private Matrix4d rotateYInternal(double double1, Matrix4d matrix4d1) {
        double double0 = Math.sin(double1);
        double double2 = Math.cosFromSin(double0, double1);
        double double3 = -double0;
        double double4 = this.m00 * double2 + this.m20 * double3;
        double double5 = this.m01 * double2 + this.m21 * double3;
        double double6 = this.m02 * double2 + this.m22 * double3;
        double double7 = this.m03 * double2 + this.m23 * double3;
        matrix4d1._m20(this.m00 * double0 + this.m20 * double2)
            ._m21(this.m01 * double0 + this.m21 * double2)
            ._m22(this.m02 * double0 + this.m22 * double2)
            ._m23(this.m03 * double0 + this.m23 * double2)
            ._m00(double4)
            ._m01(double5)
            ._m02(double6)
            ._m03(double7)
            ._m10(this.m10)
            ._m11(this.m11)
            ._m12(this.m12)
            ._m13(this.m13)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return matrix4d1;
    }

    public Matrix4d rotateY(double arg0) {
        return this.rotateY(arg0, this);
    }

    @Override
    public Matrix4d rotateZ(double arg0, Matrix4d arg1) {
        if ((this.properties & 4) != 0) {
            return arg1.rotationZ(arg0);
        } else if ((this.properties & 8) != 0) {
            double double0 = this.m30;
            double double1 = this.m31;
            double double2 = this.m32;
            return arg1.rotationZ(arg0).setTranslation(double0, double1, double2);
        } else {
            return this.rotateZInternal(arg0, arg1);
        }
    }

    private Matrix4d rotateZInternal(double double1, Matrix4d matrix4d1) {
        double double0 = Math.sin(double1);
        double double2 = Math.cosFromSin(double0, double1);
        return this.rotateTowardsXY(double0, double2, matrix4d1);
    }

    public Matrix4d rotateZ(double arg0) {
        return this.rotateZ(arg0, this);
    }

    public Matrix4d rotateTowardsXY(double arg0, double arg1) {
        return this.rotateTowardsXY(arg0, arg1, this);
    }

    @Override
    public Matrix4d rotateTowardsXY(double arg0, double arg1, Matrix4d arg2) {
        if ((this.properties & 4) != 0) {
            return arg2.rotationTowardsXY(arg0, arg1);
        } else {
            double double0 = -arg0;
            double double1 = this.m00 * arg1 + this.m10 * arg0;
            double double2 = this.m01 * arg1 + this.m11 * arg0;
            double double3 = this.m02 * arg1 + this.m12 * arg0;
            double double4 = this.m03 * arg1 + this.m13 * arg0;
            arg2._m10(this.m00 * double0 + this.m10 * arg1)
                ._m11(this.m01 * double0 + this.m11 * arg1)
                ._m12(this.m02 * double0 + this.m12 * arg1)
                ._m13(this.m03 * double0 + this.m13 * arg1)
                ._m00(double1)
                ._m01(double2)
                ._m02(double3)
                ._m03(double4)
                ._m20(this.m20)
                ._m21(this.m21)
                ._m22(this.m22)
                ._m23(this.m23)
                ._m30(this.m30)
                ._m31(this.m31)
                ._m32(this.m32)
                ._m33(this.m33)
                ._properties(this.properties & -14);
            return arg2;
        }
    }

    public Matrix4d rotateXYZ(Vector3d arg0) {
        return this.rotateXYZ(arg0.x, arg0.y, arg0.z);
    }

    public Matrix4d rotateXYZ(double arg0, double arg1, double arg2) {
        return this.rotateXYZ(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4d rotateXYZ(double arg0, double arg1, double arg2, Matrix4d arg3) {
        if ((this.properties & 4) != 0) {
            return arg3.rotationXYZ(arg0, arg1, arg2);
        } else if ((this.properties & 8) != 0) {
            double double0 = this.m30;
            double double1 = this.m31;
            double double2 = this.m32;
            return arg3.rotationXYZ(arg0, arg1, arg2).setTranslation(double0, double1, double2);
        } else {
            return (this.properties & 2) != 0 ? arg3.rotateAffineXYZ(arg0, arg1, arg2) : this.rotateXYZInternal(arg0, arg1, arg2, arg3);
        }
    }

    private Matrix4d rotateXYZInternal(double double1, double double4, double double7, Matrix4d matrix4d1) {
        double double0 = Math.sin(double1);
        double double2 = Math.cosFromSin(double0, double1);
        double double3 = Math.sin(double4);
        double double5 = Math.cosFromSin(double3, double4);
        double double6 = Math.sin(double7);
        double double8 = Math.cosFromSin(double6, double7);
        double double9 = -double0;
        double double10 = -double3;
        double double11 = -double6;
        double double12 = this.m10 * double2 + this.m20 * double0;
        double double13 = this.m11 * double2 + this.m21 * double0;
        double double14 = this.m12 * double2 + this.m22 * double0;
        double double15 = this.m13 * double2 + this.m23 * double0;
        double double16 = this.m10 * double9 + this.m20 * double2;
        double double17 = this.m11 * double9 + this.m21 * double2;
        double double18 = this.m12 * double9 + this.m22 * double2;
        double double19 = this.m13 * double9 + this.m23 * double2;
        double double20 = this.m00 * double5 + double16 * double10;
        double double21 = this.m01 * double5 + double17 * double10;
        double double22 = this.m02 * double5 + double18 * double10;
        double double23 = this.m03 * double5 + double19 * double10;
        matrix4d1._m20(this.m00 * double3 + double16 * double5)
            ._m21(this.m01 * double3 + double17 * double5)
            ._m22(this.m02 * double3 + double18 * double5)
            ._m23(this.m03 * double3 + double19 * double5)
            ._m00(double20 * double8 + double12 * double6)
            ._m01(double21 * double8 + double13 * double6)
            ._m02(double22 * double8 + double14 * double6)
            ._m03(double23 * double8 + double15 * double6)
            ._m10(double20 * double11 + double12 * double8)
            ._m11(double21 * double11 + double13 * double8)
            ._m12(double22 * double11 + double14 * double8)
            ._m13(double23 * double11 + double15 * double8)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return matrix4d1;
    }

    public Matrix4d rotateAffineXYZ(double arg0, double arg1, double arg2) {
        return this.rotateAffineXYZ(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4d rotateAffineXYZ(double arg0, double arg1, double arg2, Matrix4d arg3) {
        if ((this.properties & 4) != 0) {
            return arg3.rotationXYZ(arg0, arg1, arg2);
        } else if ((this.properties & 8) != 0) {
            double double0 = this.m30;
            double double1 = this.m31;
            double double2 = this.m32;
            return arg3.rotationXYZ(arg0, arg1, arg2).setTranslation(double0, double1, double2);
        } else {
            return this.rotateAffineXYZInternal(arg0, arg1, arg2, arg3);
        }
    }

    private Matrix4d rotateAffineXYZInternal(double double1, double double4, double double7, Matrix4d matrix4d1) {
        double double0 = Math.sin(double1);
        double double2 = Math.cosFromSin(double0, double1);
        double double3 = Math.sin(double4);
        double double5 = Math.cosFromSin(double3, double4);
        double double6 = Math.sin(double7);
        double double8 = Math.cosFromSin(double6, double7);
        double double9 = -double0;
        double double10 = -double3;
        double double11 = -double6;
        double double12 = this.m10 * double2 + this.m20 * double0;
        double double13 = this.m11 * double2 + this.m21 * double0;
        double double14 = this.m12 * double2 + this.m22 * double0;
        double double15 = this.m10 * double9 + this.m20 * double2;
        double double16 = this.m11 * double9 + this.m21 * double2;
        double double17 = this.m12 * double9 + this.m22 * double2;
        double double18 = this.m00 * double5 + double15 * double10;
        double double19 = this.m01 * double5 + double16 * double10;
        double double20 = this.m02 * double5 + double17 * double10;
        matrix4d1._m20(this.m00 * double3 + double15 * double5)
            ._m21(this.m01 * double3 + double16 * double5)
            ._m22(this.m02 * double3 + double17 * double5)
            ._m23(0.0)
            ._m00(double18 * double8 + double12 * double6)
            ._m01(double19 * double8 + double13 * double6)
            ._m02(double20 * double8 + double14 * double6)
            ._m03(0.0)
            ._m10(double18 * double11 + double12 * double8)
            ._m11(double19 * double11 + double13 * double8)
            ._m12(double20 * double11 + double14 * double8)
            ._m13(0.0)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return matrix4d1;
    }

    public Matrix4d rotateZYX(Vector3d arg0) {
        return this.rotateZYX(arg0.z, arg0.y, arg0.x);
    }

    public Matrix4d rotateZYX(double arg0, double arg1, double arg2) {
        return this.rotateZYX(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4d rotateZYX(double arg0, double arg1, double arg2, Matrix4d arg3) {
        if ((this.properties & 4) != 0) {
            return arg3.rotationZYX(arg0, arg1, arg2);
        } else if ((this.properties & 8) != 0) {
            double double0 = this.m30;
            double double1 = this.m31;
            double double2 = this.m32;
            return arg3.rotationZYX(arg0, arg1, arg2).setTranslation(double0, double1, double2);
        } else {
            return (this.properties & 2) != 0 ? arg3.rotateAffineZYX(arg0, arg1, arg2) : this.rotateZYXInternal(arg0, arg1, arg2, arg3);
        }
    }

    private Matrix4d rotateZYXInternal(double double7, double double4, double double1, Matrix4d matrix4d1) {
        double double0 = Math.sin(double1);
        double double2 = Math.cosFromSin(double0, double1);
        double double3 = Math.sin(double4);
        double double5 = Math.cosFromSin(double3, double4);
        double double6 = Math.sin(double7);
        double double8 = Math.cosFromSin(double6, double7);
        double double9 = -double6;
        double double10 = -double3;
        double double11 = -double0;
        double double12 = this.m00 * double8 + this.m10 * double6;
        double double13 = this.m01 * double8 + this.m11 * double6;
        double double14 = this.m02 * double8 + this.m12 * double6;
        double double15 = this.m03 * double8 + this.m13 * double6;
        double double16 = this.m00 * double9 + this.m10 * double8;
        double double17 = this.m01 * double9 + this.m11 * double8;
        double double18 = this.m02 * double9 + this.m12 * double8;
        double double19 = this.m03 * double9 + this.m13 * double8;
        double double20 = double12 * double3 + this.m20 * double5;
        double double21 = double13 * double3 + this.m21 * double5;
        double double22 = double14 * double3 + this.m22 * double5;
        double double23 = double15 * double3 + this.m23 * double5;
        matrix4d1._m00(double12 * double5 + this.m20 * double10)
            ._m01(double13 * double5 + this.m21 * double10)
            ._m02(double14 * double5 + this.m22 * double10)
            ._m03(double15 * double5 + this.m23 * double10)
            ._m10(double16 * double2 + double20 * double0)
            ._m11(double17 * double2 + double21 * double0)
            ._m12(double18 * double2 + double22 * double0)
            ._m13(double19 * double2 + double23 * double0)
            ._m20(double16 * double11 + double20 * double2)
            ._m21(double17 * double11 + double21 * double2)
            ._m22(double18 * double11 + double22 * double2)
            ._m23(double19 * double11 + double23 * double2)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return matrix4d1;
    }

    public Matrix4d rotateAffineZYX(double arg0, double arg1, double arg2) {
        return this.rotateAffineZYX(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4d rotateAffineZYX(double arg0, double arg1, double arg2, Matrix4d arg3) {
        double double0 = Math.sin(arg2);
        double double1 = Math.cosFromSin(double0, arg2);
        double double2 = Math.sin(arg1);
        double double3 = Math.cosFromSin(double2, arg1);
        double double4 = Math.sin(arg0);
        double double5 = Math.cosFromSin(double4, arg0);
        double double6 = -double4;
        double double7 = -double2;
        double double8 = -double0;
        double double9 = this.m00 * double5 + this.m10 * double4;
        double double10 = this.m01 * double5 + this.m11 * double4;
        double double11 = this.m02 * double5 + this.m12 * double4;
        double double12 = this.m00 * double6 + this.m10 * double5;
        double double13 = this.m01 * double6 + this.m11 * double5;
        double double14 = this.m02 * double6 + this.m12 * double5;
        double double15 = double9 * double2 + this.m20 * double3;
        double double16 = double10 * double2 + this.m21 * double3;
        double double17 = double11 * double2 + this.m22 * double3;
        arg3._m00(double9 * double3 + this.m20 * double7)
            ._m01(double10 * double3 + this.m21 * double7)
            ._m02(double11 * double3 + this.m22 * double7)
            ._m03(0.0)
            ._m10(double12 * double1 + double15 * double0)
            ._m11(double13 * double1 + double16 * double0)
            ._m12(double14 * double1 + double17 * double0)
            ._m13(0.0)
            ._m20(double12 * double8 + double15 * double1)
            ._m21(double13 * double8 + double16 * double1)
            ._m22(double14 * double8 + double17 * double1)
            ._m23(0.0)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return arg3;
    }

    public Matrix4d rotateYXZ(Vector3d arg0) {
        return this.rotateYXZ(arg0.y, arg0.x, arg0.z);
    }

    public Matrix4d rotateYXZ(double arg0, double arg1, double arg2) {
        return this.rotateYXZ(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4d rotateYXZ(double arg0, double arg1, double arg2, Matrix4d arg3) {
        if ((this.properties & 4) != 0) {
            return arg3.rotationYXZ(arg0, arg1, arg2);
        } else if ((this.properties & 8) != 0) {
            double double0 = this.m30;
            double double1 = this.m31;
            double double2 = this.m32;
            return arg3.rotationYXZ(arg0, arg1, arg2).setTranslation(double0, double1, double2);
        } else {
            return (this.properties & 2) != 0 ? arg3.rotateAffineYXZ(arg0, arg1, arg2) : this.rotateYXZInternal(arg0, arg1, arg2, arg3);
        }
    }

    private Matrix4d rotateYXZInternal(double double4, double double1, double double7, Matrix4d matrix4d1) {
        double double0 = Math.sin(double1);
        double double2 = Math.cosFromSin(double0, double1);
        double double3 = Math.sin(double4);
        double double5 = Math.cosFromSin(double3, double4);
        double double6 = Math.sin(double7);
        double double8 = Math.cosFromSin(double6, double7);
        double double9 = -double3;
        double double10 = -double0;
        double double11 = -double6;
        double double12 = this.m00 * double3 + this.m20 * double5;
        double double13 = this.m01 * double3 + this.m21 * double5;
        double double14 = this.m02 * double3 + this.m22 * double5;
        double double15 = this.m03 * double3 + this.m23 * double5;
        double double16 = this.m00 * double5 + this.m20 * double9;
        double double17 = this.m01 * double5 + this.m21 * double9;
        double double18 = this.m02 * double5 + this.m22 * double9;
        double double19 = this.m03 * double5 + this.m23 * double9;
        double double20 = this.m10 * double2 + double12 * double0;
        double double21 = this.m11 * double2 + double13 * double0;
        double double22 = this.m12 * double2 + double14 * double0;
        double double23 = this.m13 * double2 + double15 * double0;
        matrix4d1._m20(this.m10 * double10 + double12 * double2)
            ._m21(this.m11 * double10 + double13 * double2)
            ._m22(this.m12 * double10 + double14 * double2)
            ._m23(this.m13 * double10 + double15 * double2)
            ._m00(double16 * double8 + double20 * double6)
            ._m01(double17 * double8 + double21 * double6)
            ._m02(double18 * double8 + double22 * double6)
            ._m03(double19 * double8 + double23 * double6)
            ._m10(double16 * double11 + double20 * double8)
            ._m11(double17 * double11 + double21 * double8)
            ._m12(double18 * double11 + double22 * double8)
            ._m13(double19 * double11 + double23 * double8)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return matrix4d1;
    }

    public Matrix4d rotateAffineYXZ(double arg0, double arg1, double arg2) {
        return this.rotateAffineYXZ(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4d rotateAffineYXZ(double arg0, double arg1, double arg2, Matrix4d arg3) {
        double double0 = Math.sin(arg1);
        double double1 = Math.cosFromSin(double0, arg1);
        double double2 = Math.sin(arg0);
        double double3 = Math.cosFromSin(double2, arg0);
        double double4 = Math.sin(arg2);
        double double5 = Math.cosFromSin(double4, arg2);
        double double6 = -double2;
        double double7 = -double0;
        double double8 = -double4;
        double double9 = this.m00 * double2 + this.m20 * double3;
        double double10 = this.m01 * double2 + this.m21 * double3;
        double double11 = this.m02 * double2 + this.m22 * double3;
        double double12 = this.m00 * double3 + this.m20 * double6;
        double double13 = this.m01 * double3 + this.m21 * double6;
        double double14 = this.m02 * double3 + this.m22 * double6;
        double double15 = this.m10 * double1 + double9 * double0;
        double double16 = this.m11 * double1 + double10 * double0;
        double double17 = this.m12 * double1 + double11 * double0;
        arg3._m20(this.m10 * double7 + double9 * double1)
            ._m21(this.m11 * double7 + double10 * double1)
            ._m22(this.m12 * double7 + double11 * double1)
            ._m23(0.0)
            ._m00(double12 * double5 + double15 * double4)
            ._m01(double13 * double5 + double16 * double4)
            ._m02(double14 * double5 + double17 * double4)
            ._m03(0.0)
            ._m10(double12 * double8 + double15 * double5)
            ._m11(double13 * double8 + double16 * double5)
            ._m12(double14 * double8 + double17 * double5)
            ._m13(0.0)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return arg3;
    }

    public Matrix4d rotation(AxisAngle4f arg0) {
        return this.rotation(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    public Matrix4d rotation(AxisAngle4d arg0) {
        return this.rotation(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    public Matrix4d rotation(Quaterniondc arg0) {
        double double0 = arg0.w() * arg0.w();
        double double1 = arg0.x() * arg0.x();
        double double2 = arg0.y() * arg0.y();
        double double3 = arg0.z() * arg0.z();
        double double4 = arg0.z() * arg0.w();
        double double5 = double4 + double4;
        double double6 = arg0.x() * arg0.y();
        double double7 = double6 + double6;
        double double8 = arg0.x() * arg0.z();
        double double9 = double8 + double8;
        double double10 = arg0.y() * arg0.w();
        double double11 = double10 + double10;
        double double12 = arg0.y() * arg0.z();
        double double13 = double12 + double12;
        double double14 = arg0.x() * arg0.w();
        double double15 = double14 + double14;
        if ((this.properties & 4) == 0) {
            this._identity();
        }

        this._m00(double0 + double1 - double3 - double2)
            ._m01(double7 + double5)
            ._m02(double9 - double11)
            ._m10(-double5 + double7)
            ._m11(double2 - double3 + double0 - double1)
            ._m12(double13 + double15)
            ._m20(double11 + double9)
            ._m21(double13 - double15)
            ._m22(double3 - double2 - double1 + double0)
            ._properties(18);
        return this;
    }

    public Matrix4d rotation(Quaternionfc arg0) {
        double double0 = arg0.w() * arg0.w();
        double double1 = arg0.x() * arg0.x();
        double double2 = arg0.y() * arg0.y();
        double double3 = arg0.z() * arg0.z();
        double double4 = arg0.z() * arg0.w();
        double double5 = double4 + double4;
        double double6 = arg0.x() * arg0.y();
        double double7 = double6 + double6;
        double double8 = arg0.x() * arg0.z();
        double double9 = double8 + double8;
        double double10 = arg0.y() * arg0.w();
        double double11 = double10 + double10;
        double double12 = arg0.y() * arg0.z();
        double double13 = double12 + double12;
        double double14 = arg0.x() * arg0.w();
        double double15 = double14 + double14;
        if ((this.properties & 4) == 0) {
            this._identity();
        }

        this._m00(double0 + double1 - double3 - double2)
            ._m01(double7 + double5)
            ._m02(double9 - double11)
            ._m10(-double5 + double7)
            ._m11(double2 - double3 + double0 - double1)
            ._m12(double13 + double15)
            ._m20(double11 + double9)
            ._m21(double13 - double15)
            ._m22(double3 - double2 - double1 + double0)
            ._properties(18);
        return this;
    }

    public Matrix4d translationRotateScale(
        double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9
    ) {
        double double0 = arg3 + arg3;
        double double1 = arg4 + arg4;
        double double2 = arg5 + arg5;
        double double3 = double0 * arg3;
        double double4 = double1 * arg4;
        double double5 = double2 * arg5;
        double double6 = double0 * arg4;
        double double7 = double0 * arg5;
        double double8 = double0 * arg6;
        double double9 = double1 * arg5;
        double double10 = double1 * arg6;
        double double11 = double2 * arg6;
        boolean boolean0 = Math.absEqualsOne(arg7) && Math.absEqualsOne(arg8) && Math.absEqualsOne(arg9);
        this._m00(arg7 - (double4 + double5) * arg7)
            ._m01((double6 + double11) * arg7)
            ._m02((double7 - double10) * arg7)
            ._m03(0.0)
            ._m10((double6 - double11) * arg8)
            ._m11(arg8 - (double5 + double3) * arg8)
            ._m12((double9 + double8) * arg8)
            ._m13(0.0)
            ._m20((double7 + double10) * arg9)
            ._m21((double9 - double8) * arg9)
            ._m22(arg9 - (double4 + double3) * arg9)
            ._m23(0.0)
            ._m30(arg0)
            ._m31(arg1)
            ._m32(arg2)
            ._m33(1.0)
            .properties = 2 | (boolean0 ? 16 : 0);
        return this;
    }

    public Matrix4d translationRotateScale(Vector3fc arg0, Quaternionfc arg1, Vector3fc arg2) {
        return this.translationRotateScale(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg1.w(), arg2.x(), arg2.y(), arg2.z());
    }

    public Matrix4d translationRotateScale(Vector3dc arg0, Quaterniondc arg1, Vector3dc arg2) {
        return this.translationRotateScale(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg1.w(), arg2.x(), arg2.y(), arg2.z());
    }

    public Matrix4d translationRotateScale(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7) {
        return this.translationRotateScale(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg7, arg7);
    }

    public Matrix4d translationRotateScale(Vector3dc arg0, Quaterniondc arg1, double arg2) {
        return this.translationRotateScale(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg1.w(), arg2, arg2, arg2);
    }

    public Matrix4d translationRotateScale(Vector3fc arg0, Quaternionfc arg1, double arg2) {
        return this.translationRotateScale(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg1.w(), arg2, arg2, arg2);
    }

    public Matrix4d translationRotateScaleInvert(
        double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9
    ) {
        boolean boolean0 = Math.absEqualsOne(arg7) && Math.absEqualsOne(arg8) && Math.absEqualsOne(arg9);
        if (boolean0) {
            return this.translationRotateScale(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9).invertOrthonormal(this);
        } else {
            double double0 = -arg3;
            double double1 = -arg4;
            double double2 = -arg5;
            double double3 = double0 + double0;
            double double4 = double1 + double1;
            double double5 = double2 + double2;
            double double6 = double3 * double0;
            double double7 = double4 * double1;
            double double8 = double5 * double2;
            double double9 = double3 * double1;
            double double10 = double3 * double2;
            double double11 = double3 * arg6;
            double double12 = double4 * double2;
            double double13 = double4 * arg6;
            double double14 = double5 * arg6;
            double double15 = 1.0 / arg7;
            double double16 = 1.0 / arg8;
            double double17 = 1.0 / arg9;
            this._m00(double15 * (1.0 - double7 - double8))
                ._m01(double16 * (double9 + double14))
                ._m02(double17 * (double10 - double13))
                ._m03(0.0)
                ._m10(double15 * (double9 - double14))
                ._m11(double16 * (1.0 - double8 - double6))
                ._m12(double17 * (double12 + double11))
                ._m13(0.0)
                ._m20(double15 * (double10 + double13))
                ._m21(double16 * (double12 - double11))
                ._m22(double17 * (1.0 - double7 - double6))
                ._m23(0.0)
                ._m30(-this.m00 * arg0 - this.m10 * arg1 - this.m20 * arg2)
                ._m31(-this.m01 * arg0 - this.m11 * arg1 - this.m21 * arg2)
                ._m32(-this.m02 * arg0 - this.m12 * arg1 - this.m22 * arg2)
                ._m33(1.0)
                .properties = 2;
            return this;
        }
    }

    public Matrix4d translationRotateScaleInvert(Vector3dc arg0, Quaterniondc arg1, Vector3dc arg2) {
        return this.translationRotateScaleInvert(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg1.w(), arg2.x(), arg2.y(), arg2.z());
    }

    public Matrix4d translationRotateScaleInvert(Vector3fc arg0, Quaternionfc arg1, Vector3fc arg2) {
        return this.translationRotateScaleInvert(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg1.w(), arg2.x(), arg2.y(), arg2.z());
    }

    public Matrix4d translationRotateScaleInvert(Vector3dc arg0, Quaterniondc arg1, double arg2) {
        return this.translationRotateScaleInvert(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg1.w(), arg2, arg2, arg2);
    }

    public Matrix4d translationRotateScaleInvert(Vector3fc arg0, Quaternionfc arg1, double arg2) {
        return this.translationRotateScaleInvert(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg1.w(), arg2, arg2, arg2);
    }

    public Matrix4d translationRotateScaleMulAffine(
        double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, Matrix4d arg10
    ) {
        double double0 = arg6 * arg6;
        double double1 = arg3 * arg3;
        double double2 = arg4 * arg4;
        double double3 = arg5 * arg5;
        double double4 = arg5 * arg6;
        double double5 = arg3 * arg4;
        double double6 = arg3 * arg5;
        double double7 = arg4 * arg6;
        double double8 = arg4 * arg5;
        double double9 = arg3 * arg6;
        double double10 = double0 + double1 - double3 - double2;
        double double11 = double5 + double4 + double4 + double5;
        double double12 = double6 - double7 + double6 - double7;
        double double13 = -double4 + double5 - double4 + double5;
        double double14 = double2 - double3 + double0 - double1;
        double double15 = double8 + double8 + double9 + double9;
        double double16 = double7 + double6 + double6 + double7;
        double double17 = double8 + double8 - double9 - double9;
        double double18 = double3 - double2 - double1 + double0;
        double double19 = double10 * arg10.m00 + double13 * arg10.m01 + double16 * arg10.m02;
        double double20 = double11 * arg10.m00 + double14 * arg10.m01 + double17 * arg10.m02;
        this.m02 = double12 * arg10.m00 + double15 * arg10.m01 + double18 * arg10.m02;
        this.m00 = double19;
        this.m01 = double20;
        this.m03 = 0.0;
        double double21 = double10 * arg10.m10 + double13 * arg10.m11 + double16 * arg10.m12;
        double double22 = double11 * arg10.m10 + double14 * arg10.m11 + double17 * arg10.m12;
        this.m12 = double12 * arg10.m10 + double15 * arg10.m11 + double18 * arg10.m12;
        this.m10 = double21;
        this.m11 = double22;
        this.m13 = 0.0;
        double double23 = double10 * arg10.m20 + double13 * arg10.m21 + double16 * arg10.m22;
        double double24 = double11 * arg10.m20 + double14 * arg10.m21 + double17 * arg10.m22;
        this.m22 = double12 * arg10.m20 + double15 * arg10.m21 + double18 * arg10.m22;
        this.m20 = double23;
        this.m21 = double24;
        this.m23 = 0.0;
        double double25 = double10 * arg10.m30 + double13 * arg10.m31 + double16 * arg10.m32 + arg0;
        double double26 = double11 * arg10.m30 + double14 * arg10.m31 + double17 * arg10.m32 + arg1;
        this.m32 = double12 * arg10.m30 + double15 * arg10.m31 + double18 * arg10.m32 + arg2;
        this.m30 = double25;
        this.m31 = double26;
        this.m33 = 1.0;
        boolean boolean0 = Math.absEqualsOne(arg7) && Math.absEqualsOne(arg8) && Math.absEqualsOne(arg9);
        this.properties = 2 | (boolean0 && (arg10.properties & 16) != 0 ? 16 : 0);
        return this;
    }

    public Matrix4d translationRotateScaleMulAffine(Vector3fc arg0, Quaterniondc arg1, Vector3fc arg2, Matrix4d arg3) {
        return this.translationRotateScaleMulAffine(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg1.w(), arg2.x(), arg2.y(), arg2.z(), arg3);
    }

    public Matrix4d translationRotate(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6) {
        double double0 = arg6 * arg6;
        double double1 = arg3 * arg3;
        double double2 = arg4 * arg4;
        double double3 = arg5 * arg5;
        double double4 = arg5 * arg6;
        double double5 = arg3 * arg4;
        double double6 = arg3 * arg5;
        double double7 = arg4 * arg6;
        double double8 = arg4 * arg5;
        double double9 = arg3 * arg6;
        this.m00 = double0 + double1 - double3 - double2;
        this.m01 = double5 + double4 + double4 + double5;
        this.m02 = double6 - double7 + double6 - double7;
        this.m10 = -double4 + double5 - double4 + double5;
        this.m11 = double2 - double3 + double0 - double1;
        this.m12 = double8 + double8 + double9 + double9;
        this.m20 = double7 + double6 + double6 + double7;
        this.m21 = double8 + double8 - double9 - double9;
        this.m22 = double3 - double2 - double1 + double0;
        this.m30 = arg0;
        this.m31 = arg1;
        this.m32 = arg2;
        this.m33 = 1.0;
        this.properties = 18;
        return this;
    }

    public Matrix4d translationRotate(double arg0, double arg1, double arg2, Quaterniondc arg3) {
        return this.translationRotate(arg0, arg1, arg2, arg3.x(), arg3.y(), arg3.z(), arg3.w());
    }

    @Override
    public Matrix4d rotate(Quaterniondc arg0, Matrix4d arg1) {
        if ((this.properties & 4) != 0) {
            return arg1.rotation(arg0);
        } else if ((this.properties & 8) != 0) {
            return this.rotateTranslation(arg0, arg1);
        } else {
            return (this.properties & 2) != 0 ? this.rotateAffine(arg0, arg1) : this.rotateGeneric(arg0, arg1);
        }
    }

    private Matrix4d rotateGeneric(Quaterniondc quaterniondc, Matrix4d matrix4d1) {
        double double0 = quaterniondc.w() * quaterniondc.w();
        double double1 = quaterniondc.x() * quaterniondc.x();
        double double2 = quaterniondc.y() * quaterniondc.y();
        double double3 = quaterniondc.z() * quaterniondc.z();
        double double4 = quaterniondc.z() * quaterniondc.w();
        double double5 = double4 + double4;
        double double6 = quaterniondc.x() * quaterniondc.y();
        double double7 = double6 + double6;
        double double8 = quaterniondc.x() * quaterniondc.z();
        double double9 = double8 + double8;
        double double10 = quaterniondc.y() * quaterniondc.w();
        double double11 = double10 + double10;
        double double12 = quaterniondc.y() * quaterniondc.z();
        double double13 = double12 + double12;
        double double14 = quaterniondc.x() * quaterniondc.w();
        double double15 = double14 + double14;
        double double16 = double0 + double1 - double3 - double2;
        double double17 = double7 + double5;
        double double18 = double9 - double11;
        double double19 = -double5 + double7;
        double double20 = double2 - double3 + double0 - double1;
        double double21 = double13 + double15;
        double double22 = double11 + double9;
        double double23 = double13 - double15;
        double double24 = double3 - double2 - double1 + double0;
        double double25 = this.m00 * double16 + this.m10 * double17 + this.m20 * double18;
        double double26 = this.m01 * double16 + this.m11 * double17 + this.m21 * double18;
        double double27 = this.m02 * double16 + this.m12 * double17 + this.m22 * double18;
        double double28 = this.m03 * double16 + this.m13 * double17 + this.m23 * double18;
        double double29 = this.m00 * double19 + this.m10 * double20 + this.m20 * double21;
        double double30 = this.m01 * double19 + this.m11 * double20 + this.m21 * double21;
        double double31 = this.m02 * double19 + this.m12 * double20 + this.m22 * double21;
        double double32 = this.m03 * double19 + this.m13 * double20 + this.m23 * double21;
        matrix4d1._m20(this.m00 * double22 + this.m10 * double23 + this.m20 * double24)
            ._m21(this.m01 * double22 + this.m11 * double23 + this.m21 * double24)
            ._m22(this.m02 * double22 + this.m12 * double23 + this.m22 * double24)
            ._m23(this.m03 * double22 + this.m13 * double23 + this.m23 * double24)
            ._m00(double25)
            ._m01(double26)
            ._m02(double27)
            ._m03(double28)
            ._m10(double29)
            ._m11(double30)
            ._m12(double31)
            ._m13(double32)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return matrix4d1;
    }

    @Override
    public Matrix4d rotate(Quaternionfc arg0, Matrix4d arg1) {
        if ((this.properties & 4) != 0) {
            return arg1.rotation(arg0);
        } else if ((this.properties & 8) != 0) {
            return this.rotateTranslation(arg0, arg1);
        } else {
            return (this.properties & 2) != 0 ? this.rotateAffine(arg0, arg1) : this.rotateGeneric(arg0, arg1);
        }
    }

    private Matrix4d rotateGeneric(Quaternionfc quaternionfc, Matrix4d matrix4d1) {
        double double0 = quaternionfc.w() * quaternionfc.w();
        double double1 = quaternionfc.x() * quaternionfc.x();
        double double2 = quaternionfc.y() * quaternionfc.y();
        double double3 = quaternionfc.z() * quaternionfc.z();
        double double4 = quaternionfc.z() * quaternionfc.w();
        double double5 = quaternionfc.x() * quaternionfc.y();
        double double6 = quaternionfc.x() * quaternionfc.z();
        double double7 = quaternionfc.y() * quaternionfc.w();
        double double8 = quaternionfc.y() * quaternionfc.z();
        double double9 = quaternionfc.x() * quaternionfc.w();
        double double10 = double0 + double1 - double3 - double2;
        double double11 = double5 + double4 + double4 + double5;
        double double12 = double6 - double7 + double6 - double7;
        double double13 = -double4 + double5 - double4 + double5;
        double double14 = double2 - double3 + double0 - double1;
        double double15 = double8 + double8 + double9 + double9;
        double double16 = double7 + double6 + double6 + double7;
        double double17 = double8 + double8 - double9 - double9;
        double double18 = double3 - double2 - double1 + double0;
        double double19 = this.m00 * double10 + this.m10 * double11 + this.m20 * double12;
        double double20 = this.m01 * double10 + this.m11 * double11 + this.m21 * double12;
        double double21 = this.m02 * double10 + this.m12 * double11 + this.m22 * double12;
        double double22 = this.m03 * double10 + this.m13 * double11 + this.m23 * double12;
        double double23 = this.m00 * double13 + this.m10 * double14 + this.m20 * double15;
        double double24 = this.m01 * double13 + this.m11 * double14 + this.m21 * double15;
        double double25 = this.m02 * double13 + this.m12 * double14 + this.m22 * double15;
        double double26 = this.m03 * double13 + this.m13 * double14 + this.m23 * double15;
        matrix4d1._m20(this.m00 * double16 + this.m10 * double17 + this.m20 * double18)
            ._m21(this.m01 * double16 + this.m11 * double17 + this.m21 * double18)
            ._m22(this.m02 * double16 + this.m12 * double17 + this.m22 * double18)
            ._m23(this.m03 * double16 + this.m13 * double17 + this.m23 * double18)
            ._m00(double19)
            ._m01(double20)
            ._m02(double21)
            ._m03(double22)
            ._m10(double23)
            ._m11(double24)
            ._m12(double25)
            ._m13(double26)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return matrix4d1;
    }

    public Matrix4d rotate(Quaterniondc arg0) {
        return this.rotate(arg0, this);
    }

    public Matrix4d rotate(Quaternionfc arg0) {
        return this.rotate(arg0, this);
    }

    @Override
    public Matrix4d rotateAffine(Quaterniondc arg0, Matrix4d arg1) {
        double double0 = arg0.w() * arg0.w();
        double double1 = arg0.x() * arg0.x();
        double double2 = arg0.y() * arg0.y();
        double double3 = arg0.z() * arg0.z();
        double double4 = arg0.z() * arg0.w();
        double double5 = double4 + double4;
        double double6 = arg0.x() * arg0.y();
        double double7 = double6 + double6;
        double double8 = arg0.x() * arg0.z();
        double double9 = double8 + double8;
        double double10 = arg0.y() * arg0.w();
        double double11 = double10 + double10;
        double double12 = arg0.y() * arg0.z();
        double double13 = double12 + double12;
        double double14 = arg0.x() * arg0.w();
        double double15 = double14 + double14;
        double double16 = double0 + double1 - double3 - double2;
        double double17 = double7 + double5;
        double double18 = double9 - double11;
        double double19 = -double5 + double7;
        double double20 = double2 - double3 + double0 - double1;
        double double21 = double13 + double15;
        double double22 = double11 + double9;
        double double23 = double13 - double15;
        double double24 = double3 - double2 - double1 + double0;
        double double25 = this.m00 * double16 + this.m10 * double17 + this.m20 * double18;
        double double26 = this.m01 * double16 + this.m11 * double17 + this.m21 * double18;
        double double27 = this.m02 * double16 + this.m12 * double17 + this.m22 * double18;
        double double28 = this.m00 * double19 + this.m10 * double20 + this.m20 * double21;
        double double29 = this.m01 * double19 + this.m11 * double20 + this.m21 * double21;
        double double30 = this.m02 * double19 + this.m12 * double20 + this.m22 * double21;
        arg1._m20(this.m00 * double22 + this.m10 * double23 + this.m20 * double24)
            ._m21(this.m01 * double22 + this.m11 * double23 + this.m21 * double24)
            ._m22(this.m02 * double22 + this.m12 * double23 + this.m22 * double24)
            ._m23(0.0)
            ._m00(double25)
            ._m01(double26)
            ._m02(double27)
            ._m03(0.0)
            ._m10(double28)
            ._m11(double29)
            ._m12(double30)
            ._m13(0.0)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return arg1;
    }

    public Matrix4d rotateAffine(Quaterniondc arg0) {
        return this.rotateAffine(arg0, this);
    }

    @Override
    public Matrix4d rotateTranslation(Quaterniondc arg0, Matrix4d arg1) {
        double double0 = arg0.w() * arg0.w();
        double double1 = arg0.x() * arg0.x();
        double double2 = arg0.y() * arg0.y();
        double double3 = arg0.z() * arg0.z();
        double double4 = arg0.z() * arg0.w();
        double double5 = double4 + double4;
        double double6 = arg0.x() * arg0.y();
        double double7 = double6 + double6;
        double double8 = arg0.x() * arg0.z();
        double double9 = double8 + double8;
        double double10 = arg0.y() * arg0.w();
        double double11 = double10 + double10;
        double double12 = arg0.y() * arg0.z();
        double double13 = double12 + double12;
        double double14 = arg0.x() * arg0.w();
        double double15 = double14 + double14;
        double double16 = double0 + double1 - double3 - double2;
        double double17 = double7 + double5;
        double double18 = double9 - double11;
        double double19 = -double5 + double7;
        double double20 = double2 - double3 + double0 - double1;
        double double21 = double13 + double15;
        double double22 = double11 + double9;
        double double23 = double13 - double15;
        double double24 = double3 - double2 - double1 + double0;
        arg1._m20(double22)
            ._m21(double23)
            ._m22(double24)
            ._m23(0.0)
            ._m00(double16)
            ._m01(double17)
            ._m02(double18)
            ._m03(0.0)
            ._m10(double19)
            ._m11(double20)
            ._m12(double21)
            ._m13(0.0)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(1.0)
            ._properties(this.properties & -14);
        return arg1;
    }

    @Override
    public Matrix4d rotateTranslation(Quaternionfc arg0, Matrix4d arg1) {
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
        double double10 = double0 + double1 - double3 - double2;
        double double11 = double5 + double4 + double4 + double5;
        double double12 = double6 - double7 + double6 - double7;
        double double13 = -double4 + double5 - double4 + double5;
        double double14 = double2 - double3 + double0 - double1;
        double double15 = double8 + double8 + double9 + double9;
        double double16 = double7 + double6 + double6 + double7;
        double double17 = double8 + double8 - double9 - double9;
        double double18 = double3 - double2 - double1 + double0;
        arg1._m20(double16)
            ._m21(double17)
            ._m22(double18)
            ._m23(0.0)
            ._m00(double10)
            ._m01(double11)
            ._m02(double12)
            ._m03(0.0)
            ._m10(double13)
            ._m11(double14)
            ._m12(double15)
            ._m13(0.0)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(1.0)
            ._properties(this.properties & -14);
        return arg1;
    }

    @Override
    public Matrix4d rotateLocal(Quaterniondc arg0, Matrix4d arg1) {
        double double0 = arg0.w() * arg0.w();
        double double1 = arg0.x() * arg0.x();
        double double2 = arg0.y() * arg0.y();
        double double3 = arg0.z() * arg0.z();
        double double4 = arg0.z() * arg0.w();
        double double5 = double4 + double4;
        double double6 = arg0.x() * arg0.y();
        double double7 = double6 + double6;
        double double8 = arg0.x() * arg0.z();
        double double9 = double8 + double8;
        double double10 = arg0.y() * arg0.w();
        double double11 = double10 + double10;
        double double12 = arg0.y() * arg0.z();
        double double13 = double12 + double12;
        double double14 = arg0.x() * arg0.w();
        double double15 = double14 + double14;
        double double16 = double0 + double1 - double3 - double2;
        double double17 = double7 + double5;
        double double18 = double9 - double11;
        double double19 = -double5 + double7;
        double double20 = double2 - double3 + double0 - double1;
        double double21 = double13 + double15;
        double double22 = double11 + double9;
        double double23 = double13 - double15;
        double double24 = double3 - double2 - double1 + double0;
        double double25 = double16 * this.m00 + double19 * this.m01 + double22 * this.m02;
        double double26 = double17 * this.m00 + double20 * this.m01 + double23 * this.m02;
        double double27 = double18 * this.m00 + double21 * this.m01 + double24 * this.m02;
        double double28 = this.m03;
        double double29 = double16 * this.m10 + double19 * this.m11 + double22 * this.m12;
        double double30 = double17 * this.m10 + double20 * this.m11 + double23 * this.m12;
        double double31 = double18 * this.m10 + double21 * this.m11 + double24 * this.m12;
        double double32 = this.m13;
        double double33 = double16 * this.m20 + double19 * this.m21 + double22 * this.m22;
        double double34 = double17 * this.m20 + double20 * this.m21 + double23 * this.m22;
        double double35 = double18 * this.m20 + double21 * this.m21 + double24 * this.m22;
        double double36 = this.m23;
        double double37 = double16 * this.m30 + double19 * this.m31 + double22 * this.m32;
        double double38 = double17 * this.m30 + double20 * this.m31 + double23 * this.m32;
        double double39 = double18 * this.m30 + double21 * this.m31 + double24 * this.m32;
        arg1._m00(double25)
            ._m01(double26)
            ._m02(double27)
            ._m03(double28)
            ._m10(double29)
            ._m11(double30)
            ._m12(double31)
            ._m13(double32)
            ._m20(double33)
            ._m21(double34)
            ._m22(double35)
            ._m23(double36)
            ._m30(double37)
            ._m31(double38)
            ._m32(double39)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return arg1;
    }

    public Matrix4d rotateLocal(Quaterniondc arg0) {
        return this.rotateLocal(arg0, this);
    }

    @Override
    public Matrix4d rotateAffine(Quaternionfc arg0, Matrix4d arg1) {
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
        double double10 = double0 + double1 - double3 - double2;
        double double11 = double5 + double4 + double4 + double5;
        double double12 = double6 - double7 + double6 - double7;
        double double13 = -double4 + double5 - double4 + double5;
        double double14 = double2 - double3 + double0 - double1;
        double double15 = double8 + double8 + double9 + double9;
        double double16 = double7 + double6 + double6 + double7;
        double double17 = double8 + double8 - double9 - double9;
        double double18 = double3 - double2 - double1 + double0;
        double double19 = this.m00 * double10 + this.m10 * double11 + this.m20 * double12;
        double double20 = this.m01 * double10 + this.m11 * double11 + this.m21 * double12;
        double double21 = this.m02 * double10 + this.m12 * double11 + this.m22 * double12;
        double double22 = this.m00 * double13 + this.m10 * double14 + this.m20 * double15;
        double double23 = this.m01 * double13 + this.m11 * double14 + this.m21 * double15;
        double double24 = this.m02 * double13 + this.m12 * double14 + this.m22 * double15;
        arg1._m20(this.m00 * double16 + this.m10 * double17 + this.m20 * double18)
            ._m21(this.m01 * double16 + this.m11 * double17 + this.m21 * double18)
            ._m22(this.m02 * double16 + this.m12 * double17 + this.m22 * double18)
            ._m23(0.0)
            ._m00(double19)
            ._m01(double20)
            ._m02(double21)
            ._m03(0.0)
            ._m10(double22)
            ._m11(double23)
            ._m12(double24)
            ._m13(0.0)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return arg1;
    }

    public Matrix4d rotateAffine(Quaternionfc arg0) {
        return this.rotateAffine(arg0, this);
    }

    @Override
    public Matrix4d rotateLocal(Quaternionfc arg0, Matrix4d arg1) {
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
        double double10 = double0 + double1 - double3 - double2;
        double double11 = double5 + double4 + double4 + double5;
        double double12 = double6 - double7 + double6 - double7;
        double double13 = -double4 + double5 - double4 + double5;
        double double14 = double2 - double3 + double0 - double1;
        double double15 = double8 + double8 + double9 + double9;
        double double16 = double7 + double6 + double6 + double7;
        double double17 = double8 + double8 - double9 - double9;
        double double18 = double3 - double2 - double1 + double0;
        double double19 = double10 * this.m00 + double13 * this.m01 + double16 * this.m02;
        double double20 = double11 * this.m00 + double14 * this.m01 + double17 * this.m02;
        double double21 = double12 * this.m00 + double15 * this.m01 + double18 * this.m02;
        double double22 = this.m03;
        double double23 = double10 * this.m10 + double13 * this.m11 + double16 * this.m12;
        double double24 = double11 * this.m10 + double14 * this.m11 + double17 * this.m12;
        double double25 = double12 * this.m10 + double15 * this.m11 + double18 * this.m12;
        double double26 = this.m13;
        double double27 = double10 * this.m20 + double13 * this.m21 + double16 * this.m22;
        double double28 = double11 * this.m20 + double14 * this.m21 + double17 * this.m22;
        double double29 = double12 * this.m20 + double15 * this.m21 + double18 * this.m22;
        double double30 = this.m23;
        double double31 = double10 * this.m30 + double13 * this.m31 + double16 * this.m32;
        double double32 = double11 * this.m30 + double14 * this.m31 + double17 * this.m32;
        double double33 = double12 * this.m30 + double15 * this.m31 + double18 * this.m32;
        arg1._m00(double19)
            ._m01(double20)
            ._m02(double21)
            ._m03(double22)
            ._m10(double23)
            ._m11(double24)
            ._m12(double25)
            ._m13(double26)
            ._m20(double27)
            ._m21(double28)
            ._m22(double29)
            ._m23(double30)
            ._m30(double31)
            ._m31(double32)
            ._m32(double33)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return arg1;
    }

    public Matrix4d rotateLocal(Quaternionfc arg0) {
        return this.rotateLocal(arg0, this);
    }

    public Matrix4d rotate(AxisAngle4f arg0) {
        return this.rotate(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    @Override
    public Matrix4d rotate(AxisAngle4f arg0, Matrix4d arg1) {
        return this.rotate(arg0.angle, arg0.x, arg0.y, arg0.z, arg1);
    }

    public Matrix4d rotate(AxisAngle4d arg0) {
        return this.rotate(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    @Override
    public Matrix4d rotate(AxisAngle4d arg0, Matrix4d arg1) {
        return this.rotate(arg0.angle, arg0.x, arg0.y, arg0.z, arg1);
    }

    public Matrix4d rotate(double arg0, Vector3dc arg1) {
        return this.rotate(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    @Override
    public Matrix4d rotate(double arg0, Vector3dc arg1, Matrix4d arg2) {
        return this.rotate(arg0, arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    public Matrix4d rotate(double arg0, Vector3fc arg1) {
        return this.rotate(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    @Override
    public Matrix4d rotate(double arg0, Vector3fc arg1, Matrix4d arg2) {
        return this.rotate(arg0, arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    @Override
    public Vector4d getRow(int arg0, Vector4d arg1) throws IndexOutOfBoundsException {
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
            case 3:
                arg1.x = this.m03;
                arg1.y = this.m13;
                arg1.z = this.m23;
                arg1.w = this.m33;
                break;
            default:
                throw new IndexOutOfBoundsException();
        }

        return arg1;
    }

    @Override
    public Vector3d getRow(int arg0, Vector3d arg1) throws IndexOutOfBoundsException {
        switch (arg0) {
            case 0:
                arg1.x = this.m00;
                arg1.y = this.m10;
                arg1.z = this.m20;
                break;
            case 1:
                arg1.x = this.m01;
                arg1.y = this.m11;
                arg1.z = this.m21;
                break;
            case 2:
                arg1.x = this.m02;
                arg1.y = this.m12;
                arg1.z = this.m22;
                break;
            case 3:
                arg1.x = this.m03;
                arg1.y = this.m13;
                arg1.z = this.m23;
                break;
            default:
                throw new IndexOutOfBoundsException();
        }

        return arg1;
    }

    public Matrix4d setRow(int arg0, Vector4dc arg1) throws IndexOutOfBoundsException {
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
    public Vector4d getColumn(int arg0, Vector4d arg1) throws IndexOutOfBoundsException {
        switch (arg0) {
            case 0:
                arg1.x = this.m00;
                arg1.y = this.m01;
                arg1.z = this.m02;
                arg1.w = this.m03;
                break;
            case 1:
                arg1.x = this.m10;
                arg1.y = this.m11;
                arg1.z = this.m12;
                arg1.w = this.m13;
                break;
            case 2:
                arg1.x = this.m20;
                arg1.y = this.m21;
                arg1.z = this.m22;
                arg1.w = this.m23;
                break;
            case 3:
                arg1.x = this.m30;
                arg1.y = this.m31;
                arg1.z = this.m32;
                arg1.w = this.m33;
                break;
            default:
                throw new IndexOutOfBoundsException();
        }

        return arg1;
    }

    @Override
    public Vector3d getColumn(int arg0, Vector3d arg1) throws IndexOutOfBoundsException {
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

    public Matrix4d setColumn(int arg0, Vector4dc arg1) throws IndexOutOfBoundsException {
        switch (arg0) {
            case 0:
                return this._m00(arg1.x())._m01(arg1.y())._m02(arg1.z())._m03(arg1.w())._properties(0);
            case 1:
                return this._m10(arg1.x())._m11(arg1.y())._m12(arg1.z())._m13(arg1.w())._properties(0);
            case 2:
                return this._m20(arg1.x())._m21(arg1.y())._m22(arg1.z())._m23(arg1.w())._properties(0);
            case 3:
                return this._m30(arg1.x())._m31(arg1.y())._m32(arg1.z())._m33(arg1.w())._properties(0);
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public double get(int arg0, int arg1) {
        return MemUtil.INSTANCE.get(this, arg0, arg1);
    }

    public Matrix4d set(int arg0, int arg1, double arg2) {
        return MemUtil.INSTANCE.set(this, arg0, arg1, arg2);
    }

    @Override
    public double getRowColumn(int arg0, int arg1) {
        return MemUtil.INSTANCE.get(this, arg1, arg0);
    }

    public Matrix4d setRowColumn(int arg0, int arg1, double arg2) {
        return MemUtil.INSTANCE.set(this, arg1, arg0, arg2);
    }

    public Matrix4d normal() {
        return this.normal(this);
    }

    @Override
    public Matrix4d normal(Matrix4d arg0) {
        if ((this.properties & 4) != 0) {
            return arg0.identity();
        } else {
            return (this.properties & 16) != 0 ? this.normalOrthonormal(arg0) : this.normalGeneric(arg0);
        }
    }

    private Matrix4d normalOrthonormal(Matrix4d matrix4d0) {
        if (matrix4d0 != this) {
            matrix4d0.set(this);
        }

        return matrix4d0._properties(18);
    }

    private Matrix4d normalGeneric(Matrix4d matrix4d1) {
        double double0 = this.m00 * this.m11;
        double double1 = this.m01 * this.m10;
        double double2 = this.m02 * this.m10;
        double double3 = this.m00 * this.m12;
        double double4 = this.m01 * this.m12;
        double double5 = this.m02 * this.m11;
        double double6 = (double0 - double1) * this.m22 + (double2 - double3) * this.m21 + (double4 - double5) * this.m20;
        double double7 = 1.0 / double6;
        double double8 = (this.m11 * this.m22 - this.m21 * this.m12) * double7;
        double double9 = (this.m20 * this.m12 - this.m10 * this.m22) * double7;
        double double10 = (this.m10 * this.m21 - this.m20 * this.m11) * double7;
        double double11 = (this.m21 * this.m02 - this.m01 * this.m22) * double7;
        double double12 = (this.m00 * this.m22 - this.m20 * this.m02) * double7;
        double double13 = (this.m20 * this.m01 - this.m00 * this.m21) * double7;
        double double14 = (double4 - double5) * double7;
        double double15 = (double2 - double3) * double7;
        double double16 = (double0 - double1) * double7;
        return matrix4d1._m00(double8)
            ._m01(double9)
            ._m02(double10)
            ._m03(0.0)
            ._m10(double11)
            ._m11(double12)
            ._m12(double13)
            ._m13(0.0)
            ._m20(double14)
            ._m21(double15)
            ._m22(double16)
            ._m23(0.0)
            ._m30(0.0)
            ._m31(0.0)
            ._m32(0.0)
            ._m33(1.0)
            ._properties((this.properties | 2) & -10);
    }

    @Override
    public Matrix3d normal(Matrix3d arg0) {
        return (this.properties & 16) != 0 ? this.normalOrthonormal(arg0) : this.normalGeneric(arg0);
    }

    private Matrix3d normalOrthonormal(Matrix3d matrix3d) {
        matrix3d.set(this);
        return matrix3d;
    }

    private Matrix3d normalGeneric(Matrix3d matrix3d) {
        double double0 = this.m00 * this.m11;
        double double1 = this.m01 * this.m10;
        double double2 = this.m02 * this.m10;
        double double3 = this.m00 * this.m12;
        double double4 = this.m01 * this.m12;
        double double5 = this.m02 * this.m11;
        double double6 = (double0 - double1) * this.m22 + (double2 - double3) * this.m21 + (double4 - double5) * this.m20;
        double double7 = 1.0 / double6;
        return matrix3d._m00((this.m11 * this.m22 - this.m21 * this.m12) * double7)
            ._m01((this.m20 * this.m12 - this.m10 * this.m22) * double7)
            ._m02((this.m10 * this.m21 - this.m20 * this.m11) * double7)
            ._m10((this.m21 * this.m02 - this.m01 * this.m22) * double7)
            ._m11((this.m00 * this.m22 - this.m20 * this.m02) * double7)
            ._m12((this.m20 * this.m01 - this.m00 * this.m21) * double7)
            ._m20((double4 - double5) * double7)
            ._m21((double2 - double3) * double7)
            ._m22((double0 - double1) * double7);
    }

    public Matrix4d cofactor3x3() {
        return this.cofactor3x3(this);
    }

    @Override
    public Matrix3d cofactor3x3(Matrix3d arg0) {
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
    public Matrix4d cofactor3x3(Matrix4d arg0) {
        double double0 = this.m21 * this.m02 - this.m01 * this.m22;
        double double1 = this.m00 * this.m22 - this.m20 * this.m02;
        double double2 = this.m20 * this.m01 - this.m00 * this.m21;
        double double3 = this.m01 * this.m12 - this.m11 * this.m02;
        double double4 = this.m02 * this.m10 - this.m12 * this.m00;
        double double5 = this.m00 * this.m11 - this.m10 * this.m01;
        return arg0._m00(this.m11 * this.m22 - this.m21 * this.m12)
            ._m01(this.m20 * this.m12 - this.m10 * this.m22)
            ._m02(this.m10 * this.m21 - this.m20 * this.m11)
            ._m03(0.0)
            ._m10(double0)
            ._m11(double1)
            ._m12(double2)
            ._m13(0.0)
            ._m20(double3)
            ._m21(double4)
            ._m22(double5)
            ._m23(0.0)
            ._m30(0.0)
            ._m31(0.0)
            ._m32(0.0)
            ._m33(1.0)
            ._properties((this.properties | 2) & -10);
    }

    public Matrix4d normalize3x3() {
        return this.normalize3x3(this);
    }

    @Override
    public Matrix4d normalize3x3(Matrix4d arg0) {
        double double0 = Math.invsqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
        double double1 = Math.invsqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
        double double2 = Math.invsqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
        arg0._m00(this.m00 * double0)
            ._m01(this.m01 * double0)
            ._m02(this.m02 * double0)
            ._m10(this.m10 * double1)
            ._m11(this.m11 * double1)
            ._m12(this.m12 * double1)
            ._m20(this.m20 * double2)
            ._m21(this.m21 * double2)
            ._m22(this.m22 * double2)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties);
        return arg0;
    }

    @Override
    public Matrix3d normalize3x3(Matrix3d arg0) {
        double double0 = Math.invsqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
        double double1 = Math.invsqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
        double double2 = Math.invsqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
        arg0.m00(this.m00 * double0);
        arg0.m01(this.m01 * double0);
        arg0.m02(this.m02 * double0);
        arg0.m10(this.m10 * double1);
        arg0.m11(this.m11 * double1);
        arg0.m12(this.m12 * double1);
        arg0.m20(this.m20 * double2);
        arg0.m21(this.m21 * double2);
        arg0.m22(this.m22 * double2);
        return arg0;
    }

    @Override
    public Vector4d unproject(double double30, double double32, double double34, int[] ints, Vector4d vector4d) {
        double double0 = this.m00 * this.m11 - this.m01 * this.m10;
        double double1 = this.m00 * this.m12 - this.m02 * this.m10;
        double double2 = this.m00 * this.m13 - this.m03 * this.m10;
        double double3 = this.m01 * this.m12 - this.m02 * this.m11;
        double double4 = this.m01 * this.m13 - this.m03 * this.m11;
        double double5 = this.m02 * this.m13 - this.m03 * this.m12;
        double double6 = this.m20 * this.m31 - this.m21 * this.m30;
        double double7 = this.m20 * this.m32 - this.m22 * this.m30;
        double double8 = this.m20 * this.m33 - this.m23 * this.m30;
        double double9 = this.m21 * this.m32 - this.m22 * this.m31;
        double double10 = this.m21 * this.m33 - this.m23 * this.m31;
        double double11 = this.m22 * this.m33 - this.m23 * this.m32;
        double double12 = double0 * double11 - double1 * double10 + double2 * double9 + double3 * double8 - double4 * double7 + double5 * double6;
        double12 = 1.0 / double12;
        double double13 = (this.m11 * double11 - this.m12 * double10 + this.m13 * double9) * double12;
        double double14 = (-this.m01 * double11 + this.m02 * double10 - this.m03 * double9) * double12;
        double double15 = (this.m31 * double5 - this.m32 * double4 + this.m33 * double3) * double12;
        double double16 = (-this.m21 * double5 + this.m22 * double4 - this.m23 * double3) * double12;
        double double17 = (-this.m10 * double11 + this.m12 * double8 - this.m13 * double7) * double12;
        double double18 = (this.m00 * double11 - this.m02 * double8 + this.m03 * double7) * double12;
        double double19 = (-this.m30 * double5 + this.m32 * double2 - this.m33 * double1) * double12;
        double double20 = (this.m20 * double5 - this.m22 * double2 + this.m23 * double1) * double12;
        double double21 = (this.m10 * double10 - this.m11 * double8 + this.m13 * double6) * double12;
        double double22 = (-this.m00 * double10 + this.m01 * double8 - this.m03 * double6) * double12;
        double double23 = (this.m30 * double4 - this.m31 * double2 + this.m33 * double0) * double12;
        double double24 = (-this.m20 * double4 + this.m21 * double2 - this.m23 * double0) * double12;
        double double25 = (-this.m10 * double9 + this.m11 * double7 - this.m12 * double6) * double12;
        double double26 = (this.m00 * double9 - this.m01 * double7 + this.m02 * double6) * double12;
        double double27 = (-this.m30 * double3 + this.m31 * double1 - this.m32 * double0) * double12;
        double double28 = (this.m20 * double3 - this.m21 * double1 + this.m22 * double0) * double12;
        double double29 = (double30 - ints[0]) / ints[2] * 2.0 - 1.0;
        double double31 = (double32 - ints[1]) / ints[3] * 2.0 - 1.0;
        double double33 = double34 + double34 - 1.0;
        double double35 = 1.0 / (double16 * double29 + double20 * double31 + double24 * double33 + double28);
        vector4d.x = (double13 * double29 + double17 * double31 + double21 * double33 + double25) * double35;
        vector4d.y = (double14 * double29 + double18 * double31 + double22 * double33 + double26) * double35;
        vector4d.z = (double15 * double29 + double19 * double31 + double23 * double33 + double27) * double35;
        vector4d.w = 1.0;
        return vector4d;
    }

    @Override
    public Vector3d unproject(double double30, double double32, double double34, int[] ints, Vector3d vector3d) {
        double double0 = this.m00 * this.m11 - this.m01 * this.m10;
        double double1 = this.m00 * this.m12 - this.m02 * this.m10;
        double double2 = this.m00 * this.m13 - this.m03 * this.m10;
        double double3 = this.m01 * this.m12 - this.m02 * this.m11;
        double double4 = this.m01 * this.m13 - this.m03 * this.m11;
        double double5 = this.m02 * this.m13 - this.m03 * this.m12;
        double double6 = this.m20 * this.m31 - this.m21 * this.m30;
        double double7 = this.m20 * this.m32 - this.m22 * this.m30;
        double double8 = this.m20 * this.m33 - this.m23 * this.m30;
        double double9 = this.m21 * this.m32 - this.m22 * this.m31;
        double double10 = this.m21 * this.m33 - this.m23 * this.m31;
        double double11 = this.m22 * this.m33 - this.m23 * this.m32;
        double double12 = double0 * double11 - double1 * double10 + double2 * double9 + double3 * double8 - double4 * double7 + double5 * double6;
        double12 = 1.0 / double12;
        double double13 = (this.m11 * double11 - this.m12 * double10 + this.m13 * double9) * double12;
        double double14 = (-this.m01 * double11 + this.m02 * double10 - this.m03 * double9) * double12;
        double double15 = (this.m31 * double5 - this.m32 * double4 + this.m33 * double3) * double12;
        double double16 = (-this.m21 * double5 + this.m22 * double4 - this.m23 * double3) * double12;
        double double17 = (-this.m10 * double11 + this.m12 * double8 - this.m13 * double7) * double12;
        double double18 = (this.m00 * double11 - this.m02 * double8 + this.m03 * double7) * double12;
        double double19 = (-this.m30 * double5 + this.m32 * double2 - this.m33 * double1) * double12;
        double double20 = (this.m20 * double5 - this.m22 * double2 + this.m23 * double1) * double12;
        double double21 = (this.m10 * double10 - this.m11 * double8 + this.m13 * double6) * double12;
        double double22 = (-this.m00 * double10 + this.m01 * double8 - this.m03 * double6) * double12;
        double double23 = (this.m30 * double4 - this.m31 * double2 + this.m33 * double0) * double12;
        double double24 = (-this.m20 * double4 + this.m21 * double2 - this.m23 * double0) * double12;
        double double25 = (-this.m10 * double9 + this.m11 * double7 - this.m12 * double6) * double12;
        double double26 = (this.m00 * double9 - this.m01 * double7 + this.m02 * double6) * double12;
        double double27 = (-this.m30 * double3 + this.m31 * double1 - this.m32 * double0) * double12;
        double double28 = (this.m20 * double3 - this.m21 * double1 + this.m22 * double0) * double12;
        double double29 = (double30 - ints[0]) / ints[2] * 2.0 - 1.0;
        double double31 = (double32 - ints[1]) / ints[3] * 2.0 - 1.0;
        double double33 = double34 + double34 - 1.0;
        double double35 = 1.0 / (double16 * double29 + double20 * double31 + double24 * double33 + double28);
        vector3d.x = (double13 * double29 + double17 * double31 + double21 * double33 + double25) * double35;
        vector3d.y = (double14 * double29 + double18 * double31 + double22 * double33 + double26) * double35;
        vector3d.z = (double15 * double29 + double19 * double31 + double23 * double33 + double27) * double35;
        return vector3d;
    }

    @Override
    public Vector4d unproject(Vector3dc vector3dc, int[] ints, Vector4d vector4d) {
        return this.unproject(vector3dc.x(), vector3dc.y(), vector3dc.z(), ints, vector4d);
    }

    @Override
    public Vector3d unproject(Vector3dc vector3dc, int[] ints, Vector3d vector3d) {
        return this.unproject(vector3dc.x(), vector3dc.y(), vector3dc.z(), ints, vector3d);
    }

    @Override
    public Matrix4d unprojectRay(double double30, double double32, int[] ints, Vector3d vector3d0, Vector3d vector3d1) {
        double double0 = this.m00 * this.m11 - this.m01 * this.m10;
        double double1 = this.m00 * this.m12 - this.m02 * this.m10;
        double double2 = this.m00 * this.m13 - this.m03 * this.m10;
        double double3 = this.m01 * this.m12 - this.m02 * this.m11;
        double double4 = this.m01 * this.m13 - this.m03 * this.m11;
        double double5 = this.m02 * this.m13 - this.m03 * this.m12;
        double double6 = this.m20 * this.m31 - this.m21 * this.m30;
        double double7 = this.m20 * this.m32 - this.m22 * this.m30;
        double double8 = this.m20 * this.m33 - this.m23 * this.m30;
        double double9 = this.m21 * this.m32 - this.m22 * this.m31;
        double double10 = this.m21 * this.m33 - this.m23 * this.m31;
        double double11 = this.m22 * this.m33 - this.m23 * this.m32;
        double double12 = double0 * double11 - double1 * double10 + double2 * double9 + double3 * double8 - double4 * double7 + double5 * double6;
        double12 = 1.0 / double12;
        double double13 = (this.m11 * double11 - this.m12 * double10 + this.m13 * double9) * double12;
        double double14 = (-this.m01 * double11 + this.m02 * double10 - this.m03 * double9) * double12;
        double double15 = (this.m31 * double5 - this.m32 * double4 + this.m33 * double3) * double12;
        double double16 = (-this.m21 * double5 + this.m22 * double4 - this.m23 * double3) * double12;
        double double17 = (-this.m10 * double11 + this.m12 * double8 - this.m13 * double7) * double12;
        double double18 = (this.m00 * double11 - this.m02 * double8 + this.m03 * double7) * double12;
        double double19 = (-this.m30 * double5 + this.m32 * double2 - this.m33 * double1) * double12;
        double double20 = (this.m20 * double5 - this.m22 * double2 + this.m23 * double1) * double12;
        double double21 = (this.m10 * double10 - this.m11 * double8 + this.m13 * double6) * double12;
        double double22 = (-this.m00 * double10 + this.m01 * double8 - this.m03 * double6) * double12;
        double double23 = (this.m30 * double4 - this.m31 * double2 + this.m33 * double0) * double12;
        double double24 = (-this.m20 * double4 + this.m21 * double2 - this.m23 * double0) * double12;
        double double25 = (-this.m10 * double9 + this.m11 * double7 - this.m12 * double6) * double12;
        double double26 = (this.m00 * double9 - this.m01 * double7 + this.m02 * double6) * double12;
        double double27 = (-this.m30 * double3 + this.m31 * double1 - this.m32 * double0) * double12;
        double double28 = (this.m20 * double3 - this.m21 * double1 + this.m22 * double0) * double12;
        double double29 = (double30 - ints[0]) / ints[2] * 2.0 - 1.0;
        double double31 = (double32 - ints[1]) / ints[3] * 2.0 - 1.0;
        double double33 = double13 * double29 + double17 * double31 + double25;
        double double34 = double14 * double29 + double18 * double31 + double26;
        double double35 = double15 * double29 + double19 * double31 + double27;
        double double36 = 1.0 / (double16 * double29 + double20 * double31 - double24 + double28);
        double double37 = (double33 - double21) * double36;
        double double38 = (double34 - double22) * double36;
        double double39 = (double35 - double23) * double36;
        double double40 = 1.0 / (double16 * double29 + double20 * double31 + double28);
        double double41 = double33 * double40;
        double double42 = double34 * double40;
        double double43 = double35 * double40;
        vector3d0.x = double37;
        vector3d0.y = double38;
        vector3d0.z = double39;
        vector3d1.x = double41 - double37;
        vector3d1.y = double42 - double38;
        vector3d1.z = double43 - double39;
        return this;
    }

    @Override
    public Matrix4d unprojectRay(Vector2dc vector2dc, int[] ints, Vector3d vector3d0, Vector3d vector3d1) {
        return this.unprojectRay(vector2dc.x(), vector2dc.y(), ints, vector3d0, vector3d1);
    }

    @Override
    public Vector4d unprojectInv(Vector3dc vector3dc, int[] ints, Vector4d vector4d) {
        return this.unprojectInv(vector3dc.x(), vector3dc.y(), vector3dc.z(), ints, vector4d);
    }

    @Override
    public Vector4d unprojectInv(double double1, double double3, double double5, int[] ints, Vector4d vector4d) {
        double double0 = (double1 - ints[0]) / ints[2] * 2.0 - 1.0;
        double double2 = (double3 - ints[1]) / ints[3] * 2.0 - 1.0;
        double double4 = double5 + double5 - 1.0;
        double double6 = 1.0 / (this.m03 * double0 + this.m13 * double2 + this.m23 * double4 + this.m33);
        vector4d.x = (this.m00 * double0 + this.m10 * double2 + this.m20 * double4 + this.m30) * double6;
        vector4d.y = (this.m01 * double0 + this.m11 * double2 + this.m21 * double4 + this.m31) * double6;
        vector4d.z = (this.m02 * double0 + this.m12 * double2 + this.m22 * double4 + this.m32) * double6;
        vector4d.w = 1.0;
        return vector4d;
    }

    @Override
    public Vector3d unprojectInv(Vector3dc vector3dc, int[] ints, Vector3d vector3d) {
        return this.unprojectInv(vector3dc.x(), vector3dc.y(), vector3dc.z(), ints, vector3d);
    }

    @Override
    public Vector3d unprojectInv(double double1, double double3, double double5, int[] ints, Vector3d vector3d) {
        double double0 = (double1 - ints[0]) / ints[2] * 2.0 - 1.0;
        double double2 = (double3 - ints[1]) / ints[3] * 2.0 - 1.0;
        double double4 = double5 + double5 - 1.0;
        double double6 = 1.0 / (this.m03 * double0 + this.m13 * double2 + this.m23 * double4 + this.m33);
        vector3d.x = (this.m00 * double0 + this.m10 * double2 + this.m20 * double4 + this.m30) * double6;
        vector3d.y = (this.m01 * double0 + this.m11 * double2 + this.m21 * double4 + this.m31) * double6;
        vector3d.z = (this.m02 * double0 + this.m12 * double2 + this.m22 * double4 + this.m32) * double6;
        return vector3d;
    }

    @Override
    public Matrix4d unprojectInvRay(Vector2dc vector2dc, int[] ints, Vector3d vector3d0, Vector3d vector3d1) {
        return this.unprojectInvRay(vector2dc.x(), vector2dc.y(), ints, vector3d0, vector3d1);
    }

    @Override
    public Matrix4d unprojectInvRay(double double1, double double3, int[] ints, Vector3d vector3d0, Vector3d vector3d1) {
        double double0 = (double1 - ints[0]) / ints[2] * 2.0 - 1.0;
        double double2 = (double3 - ints[1]) / ints[3] * 2.0 - 1.0;
        double double4 = this.m00 * double0 + this.m10 * double2 + this.m30;
        double double5 = this.m01 * double0 + this.m11 * double2 + this.m31;
        double double6 = this.m02 * double0 + this.m12 * double2 + this.m32;
        double double7 = 1.0 / (this.m03 * double0 + this.m13 * double2 - this.m23 + this.m33);
        double double8 = (double4 - this.m20) * double7;
        double double9 = (double5 - this.m21) * double7;
        double double10 = (double6 - this.m22) * double7;
        double double11 = 1.0 / (this.m03 * double0 + this.m13 * double2 + this.m33);
        double double12 = double4 * double11;
        double double13 = double5 * double11;
        double double14 = double6 * double11;
        vector3d0.x = double8;
        vector3d0.y = double9;
        vector3d0.z = double10;
        vector3d1.x = double12 - double8;
        vector3d1.y = double13 - double9;
        vector3d1.z = double14 - double10;
        return this;
    }

    @Override
    public Vector4d project(double double3, double double2, double double1, int[] ints, Vector4d vector4d) {
        double double0 = 1.0 / (this.m03 * double3 + this.m13 * double2 + this.m23 * double1 + this.m33);
        double double4 = (this.m00 * double3 + this.m10 * double2 + this.m20 * double1 + this.m30) * double0;
        double double5 = (this.m01 * double3 + this.m11 * double2 + this.m21 * double1 + this.m31) * double0;
        double double6 = (this.m02 * double3 + this.m12 * double2 + this.m22 * double1 + this.m32) * double0;
        vector4d.x = (double4 * 0.5 + 0.5) * ints[2] + ints[0];
        vector4d.y = (double5 * 0.5 + 0.5) * ints[3] + ints[1];
        vector4d.z = (1.0 + double6) * 0.5;
        vector4d.w = 1.0;
        return vector4d;
    }

    @Override
    public Vector3d project(double double3, double double2, double double1, int[] ints, Vector3d vector3d) {
        double double0 = 1.0 / (this.m03 * double3 + this.m13 * double2 + this.m23 * double1 + this.m33);
        double double4 = (this.m00 * double3 + this.m10 * double2 + this.m20 * double1 + this.m30) * double0;
        double double5 = (this.m01 * double3 + this.m11 * double2 + this.m21 * double1 + this.m31) * double0;
        double double6 = (this.m02 * double3 + this.m12 * double2 + this.m22 * double1 + this.m32) * double0;
        vector3d.x = (double4 * 0.5 + 0.5) * ints[2] + ints[0];
        vector3d.y = (double5 * 0.5 + 0.5) * ints[3] + ints[1];
        vector3d.z = (1.0 + double6) * 0.5;
        return vector3d;
    }

    @Override
    public Vector4d project(Vector3dc vector3dc, int[] ints, Vector4d vector4d) {
        return this.project(vector3dc.x(), vector3dc.y(), vector3dc.z(), ints, vector4d);
    }

    @Override
    public Vector3d project(Vector3dc vector3dc, int[] ints, Vector3d vector3d) {
        return this.project(vector3dc.x(), vector3dc.y(), vector3dc.z(), ints, vector3d);
    }

    @Override
    public Matrix4d reflect(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4) {
        if ((this.properties & 4) != 0) {
            return arg4.reflection(arg0, arg1, arg2, arg3);
        } else if ((this.properties & 4) != 0) {
            return arg4.reflection(arg0, arg1, arg2, arg3);
        } else {
            return (this.properties & 2) != 0 ? this.reflectAffine(arg0, arg1, arg2, arg3, arg4) : this.reflectGeneric(arg0, arg1, arg2, arg3, arg4);
        }
    }

    private Matrix4d reflectAffine(double double1, double double3, double double5, double double7, Matrix4d matrix4d1) {
        double double0 = double1 + double1;
        double double2 = double3 + double3;
        double double4 = double5 + double5;
        double double6 = double7 + double7;
        double double8 = 1.0 - double0 * double1;
        double double9 = -double0 * double3;
        double double10 = -double0 * double5;
        double double11 = -double2 * double1;
        double double12 = 1.0 - double2 * double3;
        double double13 = -double2 * double5;
        double double14 = -double4 * double1;
        double double15 = -double4 * double3;
        double double16 = 1.0 - double4 * double5;
        double double17 = -double6 * double1;
        double double18 = -double6 * double3;
        double double19 = -double6 * double5;
        double double20 = this.m00 * double8 + this.m10 * double9 + this.m20 * double10;
        double double21 = this.m01 * double8 + this.m11 * double9 + this.m21 * double10;
        double double22 = this.m02 * double8 + this.m12 * double9 + this.m22 * double10;
        double double23 = this.m00 * double11 + this.m10 * double12 + this.m20 * double13;
        double double24 = this.m01 * double11 + this.m11 * double12 + this.m21 * double13;
        double double25 = this.m02 * double11 + this.m12 * double12 + this.m22 * double13;
        matrix4d1._m30(this.m00 * double17 + this.m10 * double18 + this.m20 * double19 + this.m30)
            ._m31(this.m01 * double17 + this.m11 * double18 + this.m21 * double19 + this.m31)
            ._m32(this.m02 * double17 + this.m12 * double18 + this.m22 * double19 + this.m32)
            ._m33(this.m33)
            ._m20(this.m00 * double14 + this.m10 * double15 + this.m20 * double16)
            ._m21(this.m01 * double14 + this.m11 * double15 + this.m21 * double16)
            ._m22(this.m02 * double14 + this.m12 * double15 + this.m22 * double16)
            ._m23(0.0)
            ._m00(double20)
            ._m01(double21)
            ._m02(double22)
            ._m03(0.0)
            ._m10(double23)
            ._m11(double24)
            ._m12(double25)
            ._m13(0.0)
            ._properties(this.properties & -14);
        return matrix4d1;
    }

    private Matrix4d reflectGeneric(double double1, double double3, double double5, double double7, Matrix4d matrix4d1) {
        double double0 = double1 + double1;
        double double2 = double3 + double3;
        double double4 = double5 + double5;
        double double6 = double7 + double7;
        double double8 = 1.0 - double0 * double1;
        double double9 = -double0 * double3;
        double double10 = -double0 * double5;
        double double11 = -double2 * double1;
        double double12 = 1.0 - double2 * double3;
        double double13 = -double2 * double5;
        double double14 = -double4 * double1;
        double double15 = -double4 * double3;
        double double16 = 1.0 - double4 * double5;
        double double17 = -double6 * double1;
        double double18 = -double6 * double3;
        double double19 = -double6 * double5;
        double double20 = this.m00 * double8 + this.m10 * double9 + this.m20 * double10;
        double double21 = this.m01 * double8 + this.m11 * double9 + this.m21 * double10;
        double double22 = this.m02 * double8 + this.m12 * double9 + this.m22 * double10;
        double double23 = this.m03 * double8 + this.m13 * double9 + this.m23 * double10;
        double double24 = this.m00 * double11 + this.m10 * double12 + this.m20 * double13;
        double double25 = this.m01 * double11 + this.m11 * double12 + this.m21 * double13;
        double double26 = this.m02 * double11 + this.m12 * double12 + this.m22 * double13;
        double double27 = this.m03 * double11 + this.m13 * double12 + this.m23 * double13;
        matrix4d1._m30(this.m00 * double17 + this.m10 * double18 + this.m20 * double19 + this.m30)
            ._m31(this.m01 * double17 + this.m11 * double18 + this.m21 * double19 + this.m31)
            ._m32(this.m02 * double17 + this.m12 * double18 + this.m22 * double19 + this.m32)
            ._m33(this.m03 * double17 + this.m13 * double18 + this.m23 * double19 + this.m33)
            ._m20(this.m00 * double14 + this.m10 * double15 + this.m20 * double16)
            ._m21(this.m01 * double14 + this.m11 * double15 + this.m21 * double16)
            ._m22(this.m02 * double14 + this.m12 * double15 + this.m22 * double16)
            ._m23(this.m03 * double14 + this.m13 * double15 + this.m23 * double16)
            ._m00(double20)
            ._m01(double21)
            ._m02(double22)
            ._m03(double23)
            ._m10(double24)
            ._m11(double25)
            ._m12(double26)
            ._m13(double27)
            ._properties(this.properties & -14);
        return matrix4d1;
    }

    public Matrix4d reflect(double arg0, double arg1, double arg2, double arg3) {
        return this.reflect(arg0, arg1, arg2, arg3, this);
    }

    public Matrix4d reflect(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.reflect(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    @Override
    public Matrix4d reflect(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6) {
        double double0 = Math.invsqrt(arg0 * arg0 + arg1 * arg1 + arg2 * arg2);
        double double1 = arg0 * double0;
        double double2 = arg1 * double0;
        double double3 = arg2 * double0;
        return this.reflect(double1, double2, double3, -double1 * arg3 - double2 * arg4 - double3 * arg5, arg6);
    }

    public Matrix4d reflect(Vector3dc arg0, Vector3dc arg1) {
        return this.reflect(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix4d reflect(Quaterniondc arg0, Vector3dc arg1) {
        return this.reflect(arg0, arg1, this);
    }

    @Override
    public Matrix4d reflect(Quaterniondc arg0, Vector3dc arg1, Matrix4d arg2) {
        double double0 = arg0.x() + arg0.x();
        double double1 = arg0.y() + arg0.y();
        double double2 = arg0.z() + arg0.z();
        double double3 = arg0.x() * double2 + arg0.w() * double1;
        double double4 = arg0.y() * double2 - arg0.w() * double0;
        double double5 = 1.0 - (arg0.x() * double0 + arg0.y() * double1);
        return this.reflect(double3, double4, double5, arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    @Override
    public Matrix4d reflect(Vector3dc arg0, Vector3dc arg1, Matrix4d arg2) {
        return this.reflect(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    public Matrix4d reflection(double arg0, double arg1, double arg2, double arg3) {
        double double0 = arg0 + arg0;
        double double1 = arg1 + arg1;
        double double2 = arg2 + arg2;
        double double3 = arg3 + arg3;
        this._m00(1.0 - double0 * arg0)
            ._m01(-double0 * arg1)
            ._m02(-double0 * arg2)
            ._m03(0.0)
            ._m10(-double1 * arg0)
            ._m11(1.0 - double1 * arg1)
            ._m12(-double1 * arg2)
            ._m13(0.0)
            ._m20(-double2 * arg0)
            ._m21(-double2 * arg1)
            ._m22(1.0 - double2 * arg2)
            ._m23(0.0)
            ._m30(-double3 * arg0)
            ._m31(-double3 * arg1)
            ._m32(-double3 * arg2)
            ._m33(1.0)
            .properties = 18;
        return this;
    }

    public Matrix4d reflection(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        double double0 = Math.invsqrt(arg0 * arg0 + arg1 * arg1 + arg2 * arg2);
        double double1 = arg0 * double0;
        double double2 = arg1 * double0;
        double double3 = arg2 * double0;
        return this.reflection(double1, double2, double3, -double1 * arg3 - double2 * arg4 - double3 * arg5);
    }

    public Matrix4d reflection(Vector3dc arg0, Vector3dc arg1) {
        return this.reflection(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix4d reflection(Quaterniondc arg0, Vector3dc arg1) {
        double double0 = arg0.x() + arg0.x();
        double double1 = arg0.y() + arg0.y();
        double double2 = arg0.z() + arg0.z();
        double double3 = arg0.x() * double2 + arg0.w() * double1;
        double double4 = arg0.y() * double2 - arg0.w() * double0;
        double double5 = 1.0 - (arg0.x() * double0 + arg0.y() * double1);
        return this.reflection(double3, double4, double5, arg1.x(), arg1.y(), arg1.z());
    }

    @Override
    public Matrix4d ortho(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6, Matrix4d arg7) {
        return (this.properties & 4) != 0
            ? arg7.setOrtho(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
            : this.orthoGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }

    private Matrix4d orthoGeneric(
        double double2, double double1, double double5, double double4, double double7, double double8, boolean boolean0, Matrix4d matrix4d1
    ) {
        double double0 = 2.0 / (double1 - double2);
        double double3 = 2.0 / (double4 - double5);
        double double6 = (boolean0 ? 1.0 : 2.0) / (double7 - double8);
        double double9 = (double2 + double1) / (double2 - double1);
        double double10 = (double4 + double5) / (double5 - double4);
        double double11 = (boolean0 ? double7 : double8 + double7) / (double7 - double8);
        matrix4d1._m30(this.m00 * double9 + this.m10 * double10 + this.m20 * double11 + this.m30)
            ._m31(this.m01 * double9 + this.m11 * double10 + this.m21 * double11 + this.m31)
            ._m32(this.m02 * double9 + this.m12 * double10 + this.m22 * double11 + this.m32)
            ._m33(this.m03 * double9 + this.m13 * double10 + this.m23 * double11 + this.m33)
            ._m00(this.m00 * double0)
            ._m01(this.m01 * double0)
            ._m02(this.m02 * double0)
            ._m03(this.m03 * double0)
            ._m10(this.m10 * double3)
            ._m11(this.m11 * double3)
            ._m12(this.m12 * double3)
            ._m13(this.m13 * double3)
            ._m20(this.m20 * double6)
            ._m21(this.m21 * double6)
            ._m22(this.m22 * double6)
            ._m23(this.m23 * double6)
            ._properties(this.properties & -30);
        return matrix4d1;
    }

    @Override
    public Matrix4d ortho(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6) {
        return this.ortho(arg0, arg1, arg2, arg3, arg4, arg5, false, arg6);
    }

    public Matrix4d ortho(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6) {
        return this.ortho(arg0, arg1, arg2, arg3, arg4, arg5, arg6, this);
    }

    public Matrix4d ortho(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.ortho(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    @Override
    public Matrix4d orthoLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6, Matrix4d arg7) {
        return (this.properties & 4) != 0
            ? arg7.setOrthoLH(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
            : this.orthoLHGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }

    private Matrix4d orthoLHGeneric(
        double double2, double double1, double double5, double double4, double double8, double double7, boolean boolean0, Matrix4d matrix4d1
    ) {
        double double0 = 2.0 / (double1 - double2);
        double double3 = 2.0 / (double4 - double5);
        double double6 = (boolean0 ? 1.0 : 2.0) / (double7 - double8);
        double double9 = (double2 + double1) / (double2 - double1);
        double double10 = (double4 + double5) / (double5 - double4);
        double double11 = (boolean0 ? double8 : double7 + double8) / (double8 - double7);
        matrix4d1._m30(this.m00 * double9 + this.m10 * double10 + this.m20 * double11 + this.m30)
            ._m31(this.m01 * double9 + this.m11 * double10 + this.m21 * double11 + this.m31)
            ._m32(this.m02 * double9 + this.m12 * double10 + this.m22 * double11 + this.m32)
            ._m33(this.m03 * double9 + this.m13 * double10 + this.m23 * double11 + this.m33)
            ._m00(this.m00 * double0)
            ._m01(this.m01 * double0)
            ._m02(this.m02 * double0)
            ._m03(this.m03 * double0)
            ._m10(this.m10 * double3)
            ._m11(this.m11 * double3)
            ._m12(this.m12 * double3)
            ._m13(this.m13 * double3)
            ._m20(this.m20 * double6)
            ._m21(this.m21 * double6)
            ._m22(this.m22 * double6)
            ._m23(this.m23 * double6)
            ._properties(this.properties & -30);
        return matrix4d1;
    }

    @Override
    public Matrix4d orthoLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6) {
        return this.orthoLH(arg0, arg1, arg2, arg3, arg4, arg5, false, arg6);
    }

    public Matrix4d orthoLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6) {
        return this.orthoLH(arg0, arg1, arg2, arg3, arg4, arg5, arg6, this);
    }

    public Matrix4d orthoLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.orthoLH(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    public Matrix4d setOrtho(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6) {
        if ((this.properties & 4) == 0) {
            this._identity();
        }

        this._m00(2.0 / (arg1 - arg0))
            ._m11(2.0 / (arg3 - arg2))
            ._m22((arg6 ? 1.0 : 2.0) / (arg4 - arg5))
            ._m30((arg1 + arg0) / (arg0 - arg1))
            ._m31((arg3 + arg2) / (arg2 - arg3))
            ._m32((arg6 ? arg4 : arg5 + arg4) / (arg4 - arg5))
            .properties = 2;
        return this;
    }

    public Matrix4d setOrtho(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.setOrtho(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    public Matrix4d setOrthoLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6) {
        if ((this.properties & 4) == 0) {
            this._identity();
        }

        this._m00(2.0 / (arg1 - arg0))
            ._m11(2.0 / (arg3 - arg2))
            ._m22((arg6 ? 1.0 : 2.0) / (arg5 - arg4))
            ._m30((arg1 + arg0) / (arg0 - arg1))
            ._m31((arg3 + arg2) / (arg2 - arg3))
            ._m32((arg6 ? arg4 : arg5 + arg4) / (arg4 - arg5))
            .properties = 2;
        return this;
    }

    public Matrix4d setOrthoLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.setOrthoLH(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    @Override
    public Matrix4d orthoSymmetric(double arg0, double arg1, double arg2, double arg3, boolean arg4, Matrix4d arg5) {
        return (this.properties & 4) != 0
            ? arg5.setOrthoSymmetric(arg0, arg1, arg2, arg3, arg4)
            : this.orthoSymmetricGeneric(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    private Matrix4d orthoSymmetricGeneric(double double1, double double3, double double5, double double6, boolean boolean0, Matrix4d matrix4d1) {
        double double0 = 2.0 / double1;
        double double2 = 2.0 / double3;
        double double4 = (boolean0 ? 1.0 : 2.0) / (double5 - double6);
        double double7 = (boolean0 ? double5 : double6 + double5) / (double5 - double6);
        matrix4d1._m30(this.m20 * double7 + this.m30)
            ._m31(this.m21 * double7 + this.m31)
            ._m32(this.m22 * double7 + this.m32)
            ._m33(this.m23 * double7 + this.m33)
            ._m00(this.m00 * double0)
            ._m01(this.m01 * double0)
            ._m02(this.m02 * double0)
            ._m03(this.m03 * double0)
            ._m10(this.m10 * double2)
            ._m11(this.m11 * double2)
            ._m12(this.m12 * double2)
            ._m13(this.m13 * double2)
            ._m20(this.m20 * double4)
            ._m21(this.m21 * double4)
            ._m22(this.m22 * double4)
            ._m23(this.m23 * double4)
            ._properties(this.properties & -30);
        return matrix4d1;
    }

    @Override
    public Matrix4d orthoSymmetric(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4) {
        return this.orthoSymmetric(arg0, arg1, arg2, arg3, false, arg4);
    }

    public Matrix4d orthoSymmetric(double arg0, double arg1, double arg2, double arg3, boolean arg4) {
        return this.orthoSymmetric(arg0, arg1, arg2, arg3, arg4, this);
    }

    public Matrix4d orthoSymmetric(double arg0, double arg1, double arg2, double arg3) {
        return this.orthoSymmetric(arg0, arg1, arg2, arg3, false, this);
    }

    @Override
    public Matrix4d orthoSymmetricLH(double arg0, double arg1, double arg2, double arg3, boolean arg4, Matrix4d arg5) {
        return (this.properties & 4) != 0
            ? arg5.setOrthoSymmetricLH(arg0, arg1, arg2, arg3, arg4)
            : this.orthoSymmetricLHGeneric(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    private Matrix4d orthoSymmetricLHGeneric(double double1, double double3, double double6, double double5, boolean boolean0, Matrix4d matrix4d1) {
        double double0 = 2.0 / double1;
        double double2 = 2.0 / double3;
        double double4 = (boolean0 ? 1.0 : 2.0) / (double5 - double6);
        double double7 = (boolean0 ? double6 : double5 + double6) / (double6 - double5);
        matrix4d1._m30(this.m20 * double7 + this.m30)
            ._m31(this.m21 * double7 + this.m31)
            ._m32(this.m22 * double7 + this.m32)
            ._m33(this.m23 * double7 + this.m33)
            ._m00(this.m00 * double0)
            ._m01(this.m01 * double0)
            ._m02(this.m02 * double0)
            ._m03(this.m03 * double0)
            ._m10(this.m10 * double2)
            ._m11(this.m11 * double2)
            ._m12(this.m12 * double2)
            ._m13(this.m13 * double2)
            ._m20(this.m20 * double4)
            ._m21(this.m21 * double4)
            ._m22(this.m22 * double4)
            ._m23(this.m23 * double4)
            ._properties(this.properties & -30);
        return matrix4d1;
    }

    @Override
    public Matrix4d orthoSymmetricLH(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4) {
        return this.orthoSymmetricLH(arg0, arg1, arg2, arg3, false, arg4);
    }

    public Matrix4d orthoSymmetricLH(double arg0, double arg1, double arg2, double arg3, boolean arg4) {
        return this.orthoSymmetricLH(arg0, arg1, arg2, arg3, arg4, this);
    }

    public Matrix4d orthoSymmetricLH(double arg0, double arg1, double arg2, double arg3) {
        return this.orthoSymmetricLH(arg0, arg1, arg2, arg3, false, this);
    }

    public Matrix4d setOrthoSymmetric(double arg0, double arg1, double arg2, double arg3, boolean arg4) {
        if ((this.properties & 4) == 0) {
            this._identity();
        }

        this._m00(2.0 / arg0)._m11(2.0 / arg1)._m22((arg4 ? 1.0 : 2.0) / (arg2 - arg3))._m32((arg4 ? arg2 : arg3 + arg2) / (arg2 - arg3)).properties = 2;
        return this;
    }

    public Matrix4d setOrthoSymmetric(double arg0, double arg1, double arg2, double arg3) {
        return this.setOrthoSymmetric(arg0, arg1, arg2, arg3, false);
    }

    public Matrix4d setOrthoSymmetricLH(double arg0, double arg1, double arg2, double arg3, boolean arg4) {
        if ((this.properties & 4) == 0) {
            this._identity();
        }

        this._m00(2.0 / arg0)._m11(2.0 / arg1)._m22((arg4 ? 1.0 : 2.0) / (arg3 - arg2))._m32((arg4 ? arg2 : arg3 + arg2) / (arg2 - arg3)).properties = 2;
        return this;
    }

    public Matrix4d setOrthoSymmetricLH(double arg0, double arg1, double arg2, double arg3) {
        return this.setOrthoSymmetricLH(arg0, arg1, arg2, arg3, false);
    }

    @Override
    public Matrix4d ortho2D(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4) {
        return (this.properties & 4) != 0 ? arg4.setOrtho2D(arg0, arg1, arg2, arg3) : this.ortho2DGeneric(arg0, arg1, arg2, arg3, arg4);
    }

    private Matrix4d ortho2DGeneric(double double2, double double1, double double5, double double4, Matrix4d matrix4d1) {
        double double0 = 2.0 / (double1 - double2);
        double double3 = 2.0 / (double4 - double5);
        double double6 = (double1 + double2) / (double2 - double1);
        double double7 = (double4 + double5) / (double5 - double4);
        matrix4d1._m30(this.m00 * double6 + this.m10 * double7 + this.m30)
            ._m31(this.m01 * double6 + this.m11 * double7 + this.m31)
            ._m32(this.m02 * double6 + this.m12 * double7 + this.m32)
            ._m33(this.m03 * double6 + this.m13 * double7 + this.m33)
            ._m00(this.m00 * double0)
            ._m01(this.m01 * double0)
            ._m02(this.m02 * double0)
            ._m03(this.m03 * double0)
            ._m10(this.m10 * double3)
            ._m11(this.m11 * double3)
            ._m12(this.m12 * double3)
            ._m13(this.m13 * double3)
            ._m20(-this.m20)
            ._m21(-this.m21)
            ._m22(-this.m22)
            ._m23(-this.m23)
            ._properties(this.properties & -30);
        return matrix4d1;
    }

    public Matrix4d ortho2D(double arg0, double arg1, double arg2, double arg3) {
        return this.ortho2D(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix4d ortho2DLH(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4) {
        return (this.properties & 4) != 0 ? arg4.setOrtho2DLH(arg0, arg1, arg2, arg3) : this.ortho2DLHGeneric(arg0, arg1, arg2, arg3, arg4);
    }

    private Matrix4d ortho2DLHGeneric(double double2, double double1, double double5, double double4, Matrix4d matrix4d1) {
        double double0 = 2.0 / (double1 - double2);
        double double3 = 2.0 / (double4 - double5);
        double double6 = (double1 + double2) / (double2 - double1);
        double double7 = (double4 + double5) / (double5 - double4);
        matrix4d1._m30(this.m00 * double6 + this.m10 * double7 + this.m30)
            ._m31(this.m01 * double6 + this.m11 * double7 + this.m31)
            ._m32(this.m02 * double6 + this.m12 * double7 + this.m32)
            ._m33(this.m03 * double6 + this.m13 * double7 + this.m33)
            ._m00(this.m00 * double0)
            ._m01(this.m01 * double0)
            ._m02(this.m02 * double0)
            ._m03(this.m03 * double0)
            ._m10(this.m10 * double3)
            ._m11(this.m11 * double3)
            ._m12(this.m12 * double3)
            ._m13(this.m13 * double3)
            ._m20(this.m20)
            ._m21(this.m21)
            ._m22(this.m22)
            ._m23(this.m23)
            ._properties(this.properties & -30);
        return matrix4d1;
    }

    public Matrix4d ortho2DLH(double arg0, double arg1, double arg2, double arg3) {
        return this.ortho2DLH(arg0, arg1, arg2, arg3, this);
    }

    public Matrix4d setOrtho2D(double arg0, double arg1, double arg2, double arg3) {
        if ((this.properties & 4) == 0) {
            this._identity();
        }

        this._m00(2.0 / (arg1 - arg0))._m11(2.0 / (arg3 - arg2))._m22(-1.0)._m30((arg1 + arg0) / (arg0 - arg1))._m31((arg3 + arg2) / (arg2 - arg3)).properties = 2;
        return this;
    }

    public Matrix4d setOrtho2DLH(double arg0, double arg1, double arg2, double arg3) {
        if ((this.properties & 4) == 0) {
            this._identity();
        }

        this._m00(2.0 / (arg1 - arg0))._m11(2.0 / (arg3 - arg2))._m30((arg1 + arg0) / (arg0 - arg1))._m31((arg3 + arg2) / (arg2 - arg3)).properties = 2;
        return this;
    }

    public Matrix4d lookAlong(Vector3dc arg0, Vector3dc arg1) {
        return this.lookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), this);
    }

    @Override
    public Matrix4d lookAlong(Vector3dc arg0, Vector3dc arg1, Matrix4d arg2) {
        return this.lookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    @Override
    public Matrix4d lookAlong(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6) {
        return (this.properties & 4) != 0
            ? arg6.setLookAlong(arg0, arg1, arg2, arg3, arg4, arg5)
            : this.lookAlongGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
    }

    private Matrix4d lookAlongGeneric(double double3, double double2, double double1, double double8, double double6, double double5, Matrix4d matrix4d1) {
        double double0 = Math.invsqrt(double3 * double3 + double2 * double2 + double1 * double1);
        double3 *= -double0;
        double2 *= -double0;
        double1 *= -double0;
        double double4 = double6 * double1 - double5 * double2;
        double double7 = double5 * double3 - double8 * double1;
        double double9 = double8 * double2 - double6 * double3;
        double double10 = Math.invsqrt(double4 * double4 + double7 * double7 + double9 * double9);
        double4 *= double10;
        double7 *= double10;
        double9 *= double10;
        double double11 = double2 * double9 - double1 * double7;
        double double12 = double1 * double4 - double3 * double9;
        double double13 = double3 * double7 - double2 * double4;
        double double14 = this.m00 * double4 + this.m10 * double11 + this.m20 * double3;
        double double15 = this.m01 * double4 + this.m11 * double11 + this.m21 * double3;
        double double16 = this.m02 * double4 + this.m12 * double11 + this.m22 * double3;
        double double17 = this.m03 * double4 + this.m13 * double11 + this.m23 * double3;
        double double18 = this.m00 * double7 + this.m10 * double12 + this.m20 * double2;
        double double19 = this.m01 * double7 + this.m11 * double12 + this.m21 * double2;
        double double20 = this.m02 * double7 + this.m12 * double12 + this.m22 * double2;
        double double21 = this.m03 * double7 + this.m13 * double12 + this.m23 * double2;
        matrix4d1._m20(this.m00 * double9 + this.m10 * double13 + this.m20 * double1)
            ._m21(this.m01 * double9 + this.m11 * double13 + this.m21 * double1)
            ._m22(this.m02 * double9 + this.m12 * double13 + this.m22 * double1)
            ._m23(this.m03 * double9 + this.m13 * double13 + this.m23 * double1)
            ._m00(double14)
            ._m01(double15)
            ._m02(double16)
            ._m03(double17)
            ._m10(double18)
            ._m11(double19)
            ._m12(double20)
            ._m13(double21)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(this.properties & -14);
        return matrix4d1;
    }

    public Matrix4d lookAlong(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.lookAlong(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    public Matrix4d setLookAlong(Vector3dc arg0, Vector3dc arg1) {
        return this.setLookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix4d setLookAlong(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        double double0 = Math.invsqrt(arg0 * arg0 + arg1 * arg1 + arg2 * arg2);
        arg0 *= -double0;
        arg1 *= -double0;
        arg2 *= -double0;
        double double1 = arg4 * arg2 - arg5 * arg1;
        double double2 = arg5 * arg0 - arg3 * arg2;
        double double3 = arg3 * arg1 - arg4 * arg0;
        double double4 = Math.invsqrt(double1 * double1 + double2 * double2 + double3 * double3);
        double1 *= double4;
        double2 *= double4;
        double3 *= double4;
        double double5 = arg1 * double3 - arg2 * double2;
        double double6 = arg2 * double1 - arg0 * double3;
        double double7 = arg0 * double2 - arg1 * double1;
        this._m00(double1)
            ._m01(double5)
            ._m02(arg0)
            ._m03(0.0)
            ._m10(double2)
            ._m11(double6)
            ._m12(arg1)
            ._m13(0.0)
            ._m20(double3)
            ._m21(double7)
            ._m22(arg2)
            ._m23(0.0)
            ._m30(0.0)
            ._m31(0.0)
            ._m32(0.0)
            ._m33(1.0)
            .properties = 18;
        return this;
    }

    public Matrix4d setLookAt(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2) {
        return this.setLookAt(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z());
    }

    public Matrix4d setLookAt(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        double double0 = arg0 - arg3;
        double double1 = arg1 - arg4;
        double double2 = arg2 - arg5;
        double double3 = Math.invsqrt(double0 * double0 + double1 * double1 + double2 * double2);
        double0 *= double3;
        double1 *= double3;
        double2 *= double3;
        double double4 = arg7 * double2 - arg8 * double1;
        double double5 = arg8 * double0 - arg6 * double2;
        double double6 = arg6 * double1 - arg7 * double0;
        double double7 = Math.invsqrt(double4 * double4 + double5 * double5 + double6 * double6);
        double4 *= double7;
        double5 *= double7;
        double6 *= double7;
        double double8 = double1 * double6 - double2 * double5;
        double double9 = double2 * double4 - double0 * double6;
        double double10 = double0 * double5 - double1 * double4;
        return this._m00(double4)
            ._m01(double8)
            ._m02(double0)
            ._m03(0.0)
            ._m10(double5)
            ._m11(double9)
            ._m12(double1)
            ._m13(0.0)
            ._m20(double6)
            ._m21(double10)
            ._m22(double2)
            ._m23(0.0)
            ._m30(-(double4 * arg0 + double5 * arg1 + double6 * arg2))
            ._m31(-(double8 * arg0 + double9 * arg1 + double10 * arg2))
            ._m32(-(double0 * arg0 + double1 * arg1 + double2 * arg2))
            ._m33(1.0)
            ._properties(18);
    }

    @Override
    public Matrix4d lookAt(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2, Matrix4d arg3) {
        return this.lookAt(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z(), arg3);
    }

    public Matrix4d lookAt(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2) {
        return this.lookAt(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z(), this);
    }

    @Override
    public Matrix4d lookAt(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, Matrix4d arg9) {
        if ((this.properties & 4) != 0) {
            return arg9.setLookAt(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
        } else {
            return (this.properties & 1) != 0
                ? this.lookAtPerspective(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
                : this.lookAtGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
        }
    }

    private Matrix4d lookAtGeneric(
        double double1,
        double double4,
        double double7,
        double double2,
        double double5,
        double double8,
        double double14,
        double double12,
        double double11,
        Matrix4d matrix4d1
    ) {
        double double0 = double1 - double2;
        double double3 = double4 - double5;
        double double6 = double7 - double8;
        double double9 = Math.invsqrt(double0 * double0 + double3 * double3 + double6 * double6);
        double0 *= double9;
        double3 *= double9;
        double6 *= double9;
        double double10 = double12 * double6 - double11 * double3;
        double double13 = double11 * double0 - double14 * double6;
        double double15 = double14 * double3 - double12 * double0;
        double double16 = Math.invsqrt(double10 * double10 + double13 * double13 + double15 * double15);
        double10 *= double16;
        double13 *= double16;
        double15 *= double16;
        double double17 = double3 * double15 - double6 * double13;
        double double18 = double6 * double10 - double0 * double15;
        double double19 = double0 * double13 - double3 * double10;
        double double20 = -(double10 * double1 + double13 * double4 + double15 * double7);
        double double21 = -(double17 * double1 + double18 * double4 + double19 * double7);
        double double22 = -(double0 * double1 + double3 * double4 + double6 * double7);
        double double23 = this.m00 * double10 + this.m10 * double17 + this.m20 * double0;
        double double24 = this.m01 * double10 + this.m11 * double17 + this.m21 * double0;
        double double25 = this.m02 * double10 + this.m12 * double17 + this.m22 * double0;
        double double26 = this.m03 * double10 + this.m13 * double17 + this.m23 * double0;
        double double27 = this.m00 * double13 + this.m10 * double18 + this.m20 * double3;
        double double28 = this.m01 * double13 + this.m11 * double18 + this.m21 * double3;
        double double29 = this.m02 * double13 + this.m12 * double18 + this.m22 * double3;
        double double30 = this.m03 * double13 + this.m13 * double18 + this.m23 * double3;
        matrix4d1._m30(this.m00 * double20 + this.m10 * double21 + this.m20 * double22 + this.m30)
            ._m31(this.m01 * double20 + this.m11 * double21 + this.m21 * double22 + this.m31)
            ._m32(this.m02 * double20 + this.m12 * double21 + this.m22 * double22 + this.m32)
            ._m33(this.m03 * double20 + this.m13 * double21 + this.m23 * double22 + this.m33)
            ._m20(this.m00 * double15 + this.m10 * double19 + this.m20 * double6)
            ._m21(this.m01 * double15 + this.m11 * double19 + this.m21 * double6)
            ._m22(this.m02 * double15 + this.m12 * double19 + this.m22 * double6)
            ._m23(this.m03 * double15 + this.m13 * double19 + this.m23 * double6)
            ._m00(double23)
            ._m01(double24)
            ._m02(double25)
            ._m03(double26)
            ._m10(double27)
            ._m11(double28)
            ._m12(double29)
            ._m13(double30)
            ._properties(this.properties & -14);
        return matrix4d1;
    }

    public Matrix4d lookAt(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        return this.lookAt(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, this);
    }

    @Override
    public Matrix4d lookAtPerspective(
        double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, Matrix4d arg9
    ) {
        double double0 = arg0 - arg3;
        double double1 = arg1 - arg4;
        double double2 = arg2 - arg5;
        double double3 = Math.invsqrt(double0 * double0 + double1 * double1 + double2 * double2);
        double0 *= double3;
        double1 *= double3;
        double2 *= double3;
        double double4 = arg7 * double2 - arg8 * double1;
        double double5 = arg8 * double0 - arg6 * double2;
        double double6 = arg6 * double1 - arg7 * double0;
        double double7 = Math.invsqrt(double4 * double4 + double5 * double5 + double6 * double6);
        double4 *= double7;
        double5 *= double7;
        double6 *= double7;
        double double8 = double1 * double6 - double2 * double5;
        double double9 = double2 * double4 - double0 * double6;
        double double10 = double0 * double5 - double1 * double4;
        double double11 = -(double4 * arg0 + double5 * arg1 + double6 * arg2);
        double double12 = -(double8 * arg0 + double9 * arg1 + double10 * arg2);
        double double13 = -(double0 * arg0 + double1 * arg1 + double2 * arg2);
        double double14 = this.m00 * double5;
        double double15 = this.m00 * double6;
        double double16 = this.m11 * double10;
        double double17 = this.m00 * double11;
        double double18 = this.m11 * double12;
        double double19 = this.m22 * double13 + this.m32;
        double double20 = this.m23 * double13;
        return arg9._m00(this.m00 * double4)
            ._m01(this.m11 * double8)
            ._m02(this.m22 * double0)
            ._m03(this.m23 * double0)
            ._m10(double14)
            ._m11(this.m11 * double9)
            ._m12(this.m22 * double1)
            ._m13(this.m23 * double1)
            ._m20(double15)
            ._m21(double16)
            ._m22(this.m22 * double2)
            ._m23(this.m23 * double2)
            ._m30(double17)
            ._m31(double18)
            ._m32(double19)
            ._m33(double20)
            ._properties(0);
    }

    public Matrix4d setLookAtLH(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2) {
        return this.setLookAtLH(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z());
    }

    public Matrix4d setLookAtLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        double double0 = arg3 - arg0;
        double double1 = arg4 - arg1;
        double double2 = arg5 - arg2;
        double double3 = Math.invsqrt(double0 * double0 + double1 * double1 + double2 * double2);
        double0 *= double3;
        double1 *= double3;
        double2 *= double3;
        double double4 = arg7 * double2 - arg8 * double1;
        double double5 = arg8 * double0 - arg6 * double2;
        double double6 = arg6 * double1 - arg7 * double0;
        double double7 = Math.invsqrt(double4 * double4 + double5 * double5 + double6 * double6);
        double4 *= double7;
        double5 *= double7;
        double6 *= double7;
        double double8 = double1 * double6 - double2 * double5;
        double double9 = double2 * double4 - double0 * double6;
        double double10 = double0 * double5 - double1 * double4;
        this._m00(double4)
            ._m01(double8)
            ._m02(double0)
            ._m03(0.0)
            ._m10(double5)
            ._m11(double9)
            ._m12(double1)
            ._m13(0.0)
            ._m20(double6)
            ._m21(double10)
            ._m22(double2)
            ._m23(0.0)
            ._m30(-(double4 * arg0 + double5 * arg1 + double6 * arg2))
            ._m31(-(double8 * arg0 + double9 * arg1 + double10 * arg2))
            ._m32(-(double0 * arg0 + double1 * arg1 + double2 * arg2))
            ._m33(1.0)
            .properties = 18;
        return this;
    }

    @Override
    public Matrix4d lookAtLH(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2, Matrix4d arg3) {
        return this.lookAtLH(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z(), arg3);
    }

    public Matrix4d lookAtLH(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2) {
        return this.lookAtLH(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z(), this);
    }

    @Override
    public Matrix4d lookAtLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, Matrix4d arg9) {
        if ((this.properties & 4) != 0) {
            return arg9.setLookAtLH(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
        } else {
            return (this.properties & 1) != 0
                ? this.lookAtPerspectiveLH(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
                : this.lookAtLHGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
        }
    }

    private Matrix4d lookAtLHGeneric(
        double double2,
        double double5,
        double double8,
        double double1,
        double double4,
        double double7,
        double double14,
        double double12,
        double double11,
        Matrix4d matrix4d1
    ) {
        double double0 = double1 - double2;
        double double3 = double4 - double5;
        double double6 = double7 - double8;
        double double9 = Math.invsqrt(double0 * double0 + double3 * double3 + double6 * double6);
        double0 *= double9;
        double3 *= double9;
        double6 *= double9;
        double double10 = double12 * double6 - double11 * double3;
        double double13 = double11 * double0 - double14 * double6;
        double double15 = double14 * double3 - double12 * double0;
        double double16 = Math.invsqrt(double10 * double10 + double13 * double13 + double15 * double15);
        double10 *= double16;
        double13 *= double16;
        double15 *= double16;
        double double17 = double3 * double15 - double6 * double13;
        double double18 = double6 * double10 - double0 * double15;
        double double19 = double0 * double13 - double3 * double10;
        double double20 = -(double10 * double2 + double13 * double5 + double15 * double8);
        double double21 = -(double17 * double2 + double18 * double5 + double19 * double8);
        double double22 = -(double0 * double2 + double3 * double5 + double6 * double8);
        double double23 = this.m00 * double10 + this.m10 * double17 + this.m20 * double0;
        double double24 = this.m01 * double10 + this.m11 * double17 + this.m21 * double0;
        double double25 = this.m02 * double10 + this.m12 * double17 + this.m22 * double0;
        double double26 = this.m03 * double10 + this.m13 * double17 + this.m23 * double0;
        double double27 = this.m00 * double13 + this.m10 * double18 + this.m20 * double3;
        double double28 = this.m01 * double13 + this.m11 * double18 + this.m21 * double3;
        double double29 = this.m02 * double13 + this.m12 * double18 + this.m22 * double3;
        double double30 = this.m03 * double13 + this.m13 * double18 + this.m23 * double3;
        matrix4d1._m30(this.m00 * double20 + this.m10 * double21 + this.m20 * double22 + this.m30)
            ._m31(this.m01 * double20 + this.m11 * double21 + this.m21 * double22 + this.m31)
            ._m32(this.m02 * double20 + this.m12 * double21 + this.m22 * double22 + this.m32)
            ._m33(this.m03 * double20 + this.m13 * double21 + this.m23 * double22 + this.m33)
            ._m20(this.m00 * double15 + this.m10 * double19 + this.m20 * double6)
            ._m21(this.m01 * double15 + this.m11 * double19 + this.m21 * double6)
            ._m22(this.m02 * double15 + this.m12 * double19 + this.m22 * double6)
            ._m23(this.m03 * double15 + this.m13 * double19 + this.m23 * double6)
            ._m00(double23)
            ._m01(double24)
            ._m02(double25)
            ._m03(double26)
            ._m10(double27)
            ._m11(double28)
            ._m12(double29)
            ._m13(double30)
            ._properties(this.properties & -14);
        return matrix4d1;
    }

    public Matrix4d lookAtLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        return this.lookAtLH(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, this);
    }

    @Override
    public Matrix4d lookAtPerspectiveLH(
        double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, Matrix4d arg9
    ) {
        double double0 = arg3 - arg0;
        double double1 = arg4 - arg1;
        double double2 = arg5 - arg2;
        double double3 = Math.invsqrt(double0 * double0 + double1 * double1 + double2 * double2);
        double0 *= double3;
        double1 *= double3;
        double2 *= double3;
        double double4 = arg7 * double2 - arg8 * double1;
        double double5 = arg8 * double0 - arg6 * double2;
        double double6 = arg6 * double1 - arg7 * double0;
        double double7 = Math.invsqrt(double4 * double4 + double5 * double5 + double6 * double6);
        double4 *= double7;
        double5 *= double7;
        double6 *= double7;
        double double8 = double1 * double6 - double2 * double5;
        double double9 = double2 * double4 - double0 * double6;
        double double10 = double0 * double5 - double1 * double4;
        double double11 = -(double4 * arg0 + double5 * arg1 + double6 * arg2);
        double double12 = -(double8 * arg0 + double9 * arg1 + double10 * arg2);
        double double13 = -(double0 * arg0 + double1 * arg1 + double2 * arg2);
        double double14 = this.m00 * double4;
        double double15 = this.m11 * double8;
        double double16 = this.m22 * double0;
        double double17 = this.m23 * double0;
        double double18 = this.m00 * double5;
        double double19 = this.m11 * double9;
        double double20 = this.m22 * double1;
        double double21 = this.m23 * double1;
        double double22 = this.m00 * double6;
        double double23 = this.m11 * double10;
        double double24 = this.m22 * double2;
        double double25 = this.m23 * double2;
        double double26 = this.m00 * double11;
        double double27 = this.m11 * double12;
        double double28 = this.m22 * double13 + this.m32;
        double double29 = this.m23 * double13;
        arg9._m00(double14)
            ._m01(double15)
            ._m02(double16)
            ._m03(double17)
            ._m10(double18)
            ._m11(double19)
            ._m12(double20)
            ._m13(double21)
            ._m20(double22)
            ._m21(double23)
            ._m22(double24)
            ._m23(double25)
            ._m30(double26)
            ._m31(double27)
            ._m32(double28)
            ._m33(double29)
            ._properties(0);
        return arg9;
    }

    @Override
    public Matrix4d perspective(double arg0, double arg1, double arg2, double arg3, boolean arg4, Matrix4d arg5) {
        return (this.properties & 4) != 0 ? arg5.setPerspective(arg0, arg1, arg2, arg3, arg4) : this.perspectiveGeneric(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    private Matrix4d perspectiveGeneric(double double1, double double3, double double6, double double5, boolean boolean2, Matrix4d matrix4d1) {
        double double0 = Math.tan(double1 * 0.5);
        double double2 = 1.0 / (double0 * double3);
        double double4 = 1.0 / double0;
        boolean boolean0 = double5 > 0.0 && Double.isInfinite(double5);
        boolean boolean1 = double6 > 0.0 && Double.isInfinite(double6);
        double double7;
        double double8;
        if (boolean0) {
            double double9 = 1.0E-6;
            double7 = double9 - 1.0;
            double8 = (double9 - (boolean2 ? 1.0 : 2.0)) * double6;
        } else if (boolean1) {
            double double10 = 1.0E-6;
            double7 = (boolean2 ? 0.0 : 1.0) - double10;
            double8 = ((boolean2 ? 1.0 : 2.0) - double10) * double5;
        } else {
            double7 = (boolean2 ? double5 : double5 + double6) / (double6 - double5);
            double8 = (boolean2 ? double5 : double5 + double5) * double6 / (double6 - double5);
        }

        double double11 = this.m20 * double7 - this.m30;
        double double12 = this.m21 * double7 - this.m31;
        double double13 = this.m22 * double7 - this.m32;
        double double14 = this.m23 * double7 - this.m33;
        matrix4d1._m00(this.m00 * double2)
            ._m01(this.m01 * double2)
            ._m02(this.m02 * double2)
            ._m03(this.m03 * double2)
            ._m10(this.m10 * double4)
            ._m11(this.m11 * double4)
            ._m12(this.m12 * double4)
            ._m13(this.m13 * double4)
            ._m30(this.m20 * double8)
            ._m31(this.m21 * double8)
            ._m32(this.m22 * double8)
            ._m33(this.m23 * double8)
            ._m20(double11)
            ._m21(double12)
            ._m22(double13)
            ._m23(double14)
            ._properties(this.properties & -31);
        return matrix4d1;
    }

    @Override
    public Matrix4d perspective(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4) {
        return this.perspective(arg0, arg1, arg2, arg3, false, arg4);
    }

    public Matrix4d perspective(double arg0, double arg1, double arg2, double arg3, boolean arg4) {
        return this.perspective(arg0, arg1, arg2, arg3, arg4, this);
    }

    public Matrix4d perspective(double arg0, double arg1, double arg2, double arg3) {
        return this.perspective(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix4d perspectiveRect(double arg0, double arg1, double arg2, double arg3, boolean arg4, Matrix4d arg5) {
        return (this.properties & 4) != 0
            ? arg5.setPerspectiveRect(arg0, arg1, arg2, arg3, arg4)
            : this.perspectiveRectGeneric(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    private Matrix4d perspectiveRectGeneric(double double1, double double4, double double2, double double5, boolean boolean2, Matrix4d matrix4d1) {
        double double0 = (double2 + double2) / double1;
        double double3 = (double2 + double2) / double4;
        boolean boolean0 = double5 > 0.0 && Double.isInfinite(double5);
        boolean boolean1 = double2 > 0.0 && Double.isInfinite(double2);
        double double6;
        double double7;
        if (boolean0) {
            double double8 = 1.0E-6F;
            double6 = double8 - 1.0;
            double7 = (double8 - (boolean2 ? 1.0 : 2.0)) * double2;
        } else if (boolean1) {
            double double9 = 1.0E-6F;
            double6 = (boolean2 ? 0.0 : 1.0) - double9;
            double7 = ((boolean2 ? 1.0 : 2.0) - double9) * double5;
        } else {
            double6 = (boolean2 ? double5 : double5 + double2) / (double2 - double5);
            double7 = (boolean2 ? double5 : double5 + double5) * double2 / (double2 - double5);
        }

        double double10 = this.m20 * double6 - this.m30;
        double double11 = this.m21 * double6 - this.m31;
        double double12 = this.m22 * double6 - this.m32;
        double double13 = this.m23 * double6 - this.m33;
        matrix4d1._m00(this.m00 * double0)
            ._m01(this.m01 * double0)
            ._m02(this.m02 * double0)
            ._m03(this.m03 * double0)
            ._m10(this.m10 * double3)
            ._m11(this.m11 * double3)
            ._m12(this.m12 * double3)
            ._m13(this.m13 * double3)
            ._m30(this.m20 * double7)
            ._m31(this.m21 * double7)
            ._m32(this.m22 * double7)
            ._m33(this.m23 * double7)
            ._m20(double10)
            ._m21(double11)
            ._m22(double12)
            ._m23(double13)
            ._properties(this.properties & -31);
        return matrix4d1;
    }

    @Override
    public Matrix4d perspectiveRect(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4) {
        return this.perspectiveRect(arg0, arg1, arg2, arg3, false, arg4);
    }

    @Override
    public Matrix4d perspectiveRect(double arg0, double arg1, double arg2, double arg3, boolean arg4) {
        return this.perspectiveRect(arg0, arg1, arg2, arg3, arg4, this);
    }

    @Override
    public Matrix4d perspectiveRect(double arg0, double arg1, double arg2, double arg3) {
        return this.perspectiveRect(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix4d perspectiveOffCenter(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6, Matrix4d arg7) {
        return (this.properties & 4) != 0
            ? arg7.setPerspectiveOffCenter(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
            : this.perspectiveOffCenterGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }

    private Matrix4d perspectiveOffCenterGeneric(
        double double1, double double6, double double8, double double3, double double12, double double11, boolean boolean2, Matrix4d matrix4d1
    ) {
        double double0 = Math.tan(double1 * 0.5);
        double double2 = 1.0 / (double0 * double3);
        double double4 = 1.0 / double0;
        double double5 = Math.tan(double6);
        double double7 = Math.tan(double8);
        double double9 = double5 * double2;
        double double10 = double7 * double4;
        boolean boolean0 = double11 > 0.0 && Double.isInfinite(double11);
        boolean boolean1 = double12 > 0.0 && Double.isInfinite(double12);
        double double13;
        double double14;
        if (boolean0) {
            double double15 = 1.0E-6;
            double13 = double15 - 1.0;
            double14 = (double15 - (boolean2 ? 1.0 : 2.0)) * double12;
        } else if (boolean1) {
            double double16 = 1.0E-6;
            double13 = (boolean2 ? 0.0 : 1.0) - double16;
            double14 = ((boolean2 ? 1.0 : 2.0) - double16) * double11;
        } else {
            double13 = (boolean2 ? double11 : double11 + double12) / (double12 - double11);
            double14 = (boolean2 ? double11 : double11 + double11) * double12 / (double12 - double11);
        }

        double double17 = this.m00 * double9 + this.m10 * double10 + this.m20 * double13 - this.m30;
        double double18 = this.m01 * double9 + this.m11 * double10 + this.m21 * double13 - this.m31;
        double double19 = this.m02 * double9 + this.m12 * double10 + this.m22 * double13 - this.m32;
        double double20 = this.m03 * double9 + this.m13 * double10 + this.m23 * double13 - this.m33;
        matrix4d1._m00(this.m00 * double2)
            ._m01(this.m01 * double2)
            ._m02(this.m02 * double2)
            ._m03(this.m03 * double2)
            ._m10(this.m10 * double4)
            ._m11(this.m11 * double4)
            ._m12(this.m12 * double4)
            ._m13(this.m13 * double4)
            ._m30(this.m20 * double14)
            ._m31(this.m21 * double14)
            ._m32(this.m22 * double14)
            ._m33(this.m23 * double14)
            ._m20(double17)
            ._m21(double18)
            ._m22(double19)
            ._m23(double20)
            ._properties(this.properties & ~(30 | (double9 == 0.0 && double10 == 0.0 ? 0 : 1)));
        return matrix4d1;
    }

    @Override
    public Matrix4d perspectiveOffCenter(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6) {
        return this.perspectiveOffCenter(arg0, arg1, arg2, arg3, arg4, arg5, false, arg6);
    }

    @Override
    public Matrix4d perspectiveOffCenter(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6) {
        return this.perspectiveOffCenter(arg0, arg1, arg2, arg3, arg4, arg5, arg6, this);
    }

    @Override
    public Matrix4d perspectiveOffCenter(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.perspectiveOffCenter(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    public Matrix4d setPerspective(double arg0, double arg1, double arg2, double arg3, boolean arg4) {
        double double0 = Math.tan(arg0 * 0.5);
        this._m00(1.0 / (double0 * arg1))._m01(0.0)._m02(0.0)._m03(0.0)._m10(0.0)._m11(1.0 / double0)._m12(0.0)._m13(0.0)._m20(0.0)._m21(0.0);
        boolean boolean0 = arg3 > 0.0 && Double.isInfinite(arg3);
        boolean boolean1 = arg2 > 0.0 && Double.isInfinite(arg2);
        if (boolean0) {
            double double1 = 1.0E-6;
            this._m22(double1 - 1.0)._m32((double1 - (arg4 ? 1.0 : 2.0)) * arg2);
        } else if (boolean1) {
            double double2 = 1.0E-6;
            this._m22((arg4 ? 0.0 : 1.0) - double2)._m32(((arg4 ? 1.0 : 2.0) - double2) * arg3);
        } else {
            this._m22((arg4 ? arg3 : arg3 + arg2) / (arg2 - arg3))._m32((arg4 ? arg3 : arg3 + arg3) * arg2 / (arg2 - arg3));
        }

        this._m23(-1.0)._m30(0.0)._m31(0.0)._m33(0.0).properties = 1;
        return this;
    }

    public Matrix4d setPerspective(double arg0, double arg1, double arg2, double arg3) {
        return this.setPerspective(arg0, arg1, arg2, arg3, false);
    }

    public Matrix4d setPerspectiveRect(double arg0, double arg1, double arg2, double arg3, boolean arg4) {
        this.zero();
        this._m00((arg2 + arg2) / arg0);
        this._m11((arg2 + arg2) / arg1);
        boolean boolean0 = arg3 > 0.0 && Double.isInfinite(arg3);
        boolean boolean1 = arg2 > 0.0 && Double.isInfinite(arg2);
        if (boolean0) {
            double double0 = 1.0E-6;
            this._m22(double0 - 1.0);
            this._m32((double0 - (arg4 ? 1.0 : 2.0)) * arg2);
        } else if (boolean1) {
            double double1 = 1.0E-6F;
            this._m22((arg4 ? 0.0 : 1.0) - double1);
            this._m32(((arg4 ? 1.0 : 2.0) - double1) * arg3);
        } else {
            this._m22((arg4 ? arg3 : arg3 + arg2) / (arg2 - arg3));
            this._m32((arg4 ? arg3 : arg3 + arg3) * arg2 / (arg2 - arg3));
        }

        this._m23(-1.0);
        this.properties = 1;
        return this;
    }

    public Matrix4d setPerspectiveRect(double arg0, double arg1, double arg2, double arg3) {
        return this.setPerspectiveRect(arg0, arg1, arg2, arg3, false);
    }

    public Matrix4d setPerspectiveOffCenter(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.setPerspectiveOffCenter(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    public Matrix4d setPerspectiveOffCenter(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6) {
        this.zero();
        double double0 = Math.tan(arg0 * 0.5);
        double double1 = 1.0 / (double0 * arg3);
        double double2 = 1.0 / double0;
        this._m00(double1)._m11(double2);
        double double3 = Math.tan(arg1);
        double double4 = Math.tan(arg2);
        this._m20(double3 * double1)._m21(double4 * double2);
        boolean boolean0 = arg5 > 0.0 && Double.isInfinite(arg5);
        boolean boolean1 = arg4 > 0.0 && Double.isInfinite(arg4);
        if (boolean0) {
            double double5 = 1.0E-6;
            this._m22(double5 - 1.0)._m32((double5 - (arg6 ? 1.0 : 2.0)) * arg4);
        } else if (boolean1) {
            double double6 = 1.0E-6;
            this._m22((arg6 ? 0.0 : 1.0) - double6)._m32(((arg6 ? 1.0 : 2.0) - double6) * arg5);
        } else {
            this._m22((arg6 ? arg5 : arg5 + arg4) / (arg4 - arg5))._m32((arg6 ? arg5 : arg5 + arg5) * arg4 / (arg4 - arg5));
        }

        this._m23(-1.0)._m30(0.0)._m31(0.0)._m33(0.0).properties = arg1 == 0.0 && arg2 == 0.0 ? 1 : 0;
        return this;
    }

    @Override
    public Matrix4d perspectiveLH(double arg0, double arg1, double arg2, double arg3, boolean arg4, Matrix4d arg5) {
        return (this.properties & 4) != 0 ? arg5.setPerspectiveLH(arg0, arg1, arg2, arg3, arg4) : this.perspectiveLHGeneric(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    private Matrix4d perspectiveLHGeneric(double double1, double double3, double double6, double double5, boolean boolean2, Matrix4d matrix4d1) {
        double double0 = Math.tan(double1 * 0.5);
        double double2 = 1.0 / (double0 * double3);
        double double4 = 1.0 / double0;
        boolean boolean0 = double5 > 0.0 && Double.isInfinite(double5);
        boolean boolean1 = double6 > 0.0 && Double.isInfinite(double6);
        double double7;
        double double8;
        if (boolean0) {
            double double9 = 1.0E-6;
            double7 = 1.0 - double9;
            double8 = (double9 - (boolean2 ? 1.0 : 2.0)) * double6;
        } else if (boolean1) {
            double double10 = 1.0E-6;
            double7 = (boolean2 ? 0.0 : 1.0) - double10;
            double8 = ((boolean2 ? 1.0 : 2.0) - double10) * double5;
        } else {
            double7 = (boolean2 ? double5 : double5 + double6) / (double5 - double6);
            double8 = (boolean2 ? double5 : double5 + double5) * double6 / (double6 - double5);
        }

        double double11 = this.m20 * double7 + this.m30;
        double double12 = this.m21 * double7 + this.m31;
        double double13 = this.m22 * double7 + this.m32;
        double double14 = this.m23 * double7 + this.m33;
        matrix4d1._m00(this.m00 * double2)
            ._m01(this.m01 * double2)
            ._m02(this.m02 * double2)
            ._m03(this.m03 * double2)
            ._m10(this.m10 * double4)
            ._m11(this.m11 * double4)
            ._m12(this.m12 * double4)
            ._m13(this.m13 * double4)
            ._m30(this.m20 * double8)
            ._m31(this.m21 * double8)
            ._m32(this.m22 * double8)
            ._m33(this.m23 * double8)
            ._m20(double11)
            ._m21(double12)
            ._m22(double13)
            ._m23(double14)
            ._properties(this.properties & -31);
        return matrix4d1;
    }

    public Matrix4d perspectiveLH(double arg0, double arg1, double arg2, double arg3, boolean arg4) {
        return this.perspectiveLH(arg0, arg1, arg2, arg3, arg4, this);
    }

    @Override
    public Matrix4d perspectiveLH(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4) {
        return this.perspectiveLH(arg0, arg1, arg2, arg3, false, arg4);
    }

    public Matrix4d perspectiveLH(double arg0, double arg1, double arg2, double arg3) {
        return this.perspectiveLH(arg0, arg1, arg2, arg3, this);
    }

    public Matrix4d setPerspectiveLH(double arg0, double arg1, double arg2, double arg3, boolean arg4) {
        double double0 = Math.tan(arg0 * 0.5);
        this._m00(1.0 / (double0 * arg1))._m01(0.0)._m02(0.0)._m03(0.0)._m10(0.0)._m11(1.0 / double0)._m12(0.0)._m13(0.0)._m20(0.0)._m21(0.0);
        boolean boolean0 = arg3 > 0.0 && Double.isInfinite(arg3);
        boolean boolean1 = arg2 > 0.0 && Double.isInfinite(arg2);
        if (boolean0) {
            double double1 = 1.0E-6;
            this._m22(1.0 - double1)._m32((double1 - (arg4 ? 1.0 : 2.0)) * arg2);
        } else if (boolean1) {
            double double2 = 1.0E-6;
            this._m22((arg4 ? 0.0 : 1.0) - double2)._m32(((arg4 ? 1.0 : 2.0) - double2) * arg3);
        } else {
            this._m22((arg4 ? arg3 : arg3 + arg2) / (arg3 - arg2))._m32((arg4 ? arg3 : arg3 + arg3) * arg2 / (arg2 - arg3));
        }

        this._m23(1.0)._m30(0.0)._m31(0.0)._m33(0.0).properties = 1;
        return this;
    }

    public Matrix4d setPerspectiveLH(double arg0, double arg1, double arg2, double arg3) {
        return this.setPerspectiveLH(arg0, arg1, arg2, arg3, false);
    }

    @Override
    public Matrix4d frustum(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6, Matrix4d arg7) {
        return (this.properties & 4) != 0
            ? arg7.setFrustum(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
            : this.frustumGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }

    private Matrix4d frustumGeneric(
        double double2, double double1, double double6, double double5, double double3, double double9, boolean boolean2, Matrix4d matrix4d1
    ) {
        double double0 = (double3 + double3) / (double1 - double2);
        double double4 = (double3 + double3) / (double5 - double6);
        double double7 = (double1 + double2) / (double1 - double2);
        double double8 = (double5 + double6) / (double5 - double6);
        boolean boolean0 = double9 > 0.0 && Double.isInfinite(double9);
        boolean boolean1 = double3 > 0.0 && Double.isInfinite(double3);
        double double10;
        double double11;
        if (boolean0) {
            double double12 = 1.0E-6;
            double10 = double12 - 1.0;
            double11 = (double12 - (boolean2 ? 1.0 : 2.0)) * double3;
        } else if (boolean1) {
            double double13 = 1.0E-6;
            double10 = (boolean2 ? 0.0 : 1.0) - double13;
            double11 = ((boolean2 ? 1.0 : 2.0) - double13) * double9;
        } else {
            double10 = (boolean2 ? double9 : double9 + double3) / (double3 - double9);
            double11 = (boolean2 ? double9 : double9 + double9) * double3 / (double3 - double9);
        }

        double double14 = this.m00 * double7 + this.m10 * double8 + this.m20 * double10 - this.m30;
        double double15 = this.m01 * double7 + this.m11 * double8 + this.m21 * double10 - this.m31;
        double double16 = this.m02 * double7 + this.m12 * double8 + this.m22 * double10 - this.m32;
        double double17 = this.m03 * double7 + this.m13 * double8 + this.m23 * double10 - this.m33;
        matrix4d1._m00(this.m00 * double0)
            ._m01(this.m01 * double0)
            ._m02(this.m02 * double0)
            ._m03(this.m03 * double0)
            ._m10(this.m10 * double4)
            ._m11(this.m11 * double4)
            ._m12(this.m12 * double4)
            ._m13(this.m13 * double4)
            ._m30(this.m20 * double11)
            ._m31(this.m21 * double11)
            ._m32(this.m22 * double11)
            ._m33(this.m23 * double11)
            ._m20(double14)
            ._m21(double15)
            ._m22(double16)
            ._m23(double17)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(0);
        return matrix4d1;
    }

    @Override
    public Matrix4d frustum(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6) {
        return this.frustum(arg0, arg1, arg2, arg3, arg4, arg5, false, arg6);
    }

    public Matrix4d frustum(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6) {
        return this.frustum(arg0, arg1, arg2, arg3, arg4, arg5, arg6, this);
    }

    public Matrix4d frustum(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.frustum(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    public Matrix4d setFrustum(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6) {
        if ((this.properties & 4) == 0) {
            this._identity();
        }

        this._m00((arg4 + arg4) / (arg1 - arg0))._m11((arg4 + arg4) / (arg3 - arg2))._m20((arg1 + arg0) / (arg1 - arg0))._m21((arg3 + arg2) / (arg3 - arg2));
        boolean boolean0 = arg5 > 0.0 && Double.isInfinite(arg5);
        boolean boolean1 = arg4 > 0.0 && Double.isInfinite(arg4);
        if (boolean0) {
            double double0 = 1.0E-6;
            this._m22(double0 - 1.0)._m32((double0 - (arg6 ? 1.0 : 2.0)) * arg4);
        } else if (boolean1) {
            double double1 = 1.0E-6;
            this._m22((arg6 ? 0.0 : 1.0) - double1)._m32(((arg6 ? 1.0 : 2.0) - double1) * arg5);
        } else {
            this._m22((arg6 ? arg5 : arg5 + arg4) / (arg4 - arg5))._m32((arg6 ? arg5 : arg5 + arg5) * arg4 / (arg4 - arg5));
        }

        this._m23(-1.0)._m33(0.0).properties = this.m20 == 0.0 && this.m21 == 0.0 ? 1 : 0;
        return this;
    }

    public Matrix4d setFrustum(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.setFrustum(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    @Override
    public Matrix4d frustumLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6, Matrix4d arg7) {
        return (this.properties & 4) != 0
            ? arg7.setFrustumLH(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
            : this.frustumLHGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }

    private Matrix4d frustumLHGeneric(
        double double2, double double1, double double6, double double5, double double3, double double9, boolean boolean2, Matrix4d matrix4d1
    ) {
        double double0 = (double3 + double3) / (double1 - double2);
        double double4 = (double3 + double3) / (double5 - double6);
        double double7 = (double1 + double2) / (double1 - double2);
        double double8 = (double5 + double6) / (double5 - double6);
        boolean boolean0 = double9 > 0.0 && Double.isInfinite(double9);
        boolean boolean1 = double3 > 0.0 && Double.isInfinite(double3);
        double double10;
        double double11;
        if (boolean0) {
            double double12 = 1.0E-6;
            double10 = 1.0 - double12;
            double11 = (double12 - (boolean2 ? 1.0 : 2.0)) * double3;
        } else if (boolean1) {
            double double13 = 1.0E-6;
            double10 = (boolean2 ? 0.0 : 1.0) - double13;
            double11 = ((boolean2 ? 1.0 : 2.0) - double13) * double9;
        } else {
            double10 = (boolean2 ? double9 : double9 + double3) / (double9 - double3);
            double11 = (boolean2 ? double9 : double9 + double9) * double3 / (double3 - double9);
        }

        double double14 = this.m00 * double7 + this.m10 * double8 + this.m20 * double10 + this.m30;
        double double15 = this.m01 * double7 + this.m11 * double8 + this.m21 * double10 + this.m31;
        double double16 = this.m02 * double7 + this.m12 * double8 + this.m22 * double10 + this.m32;
        double double17 = this.m03 * double7 + this.m13 * double8 + this.m23 * double10 + this.m33;
        matrix4d1._m00(this.m00 * double0)
            ._m01(this.m01 * double0)
            ._m02(this.m02 * double0)
            ._m03(this.m03 * double0)
            ._m10(this.m10 * double4)
            ._m11(this.m11 * double4)
            ._m12(this.m12 * double4)
            ._m13(this.m13 * double4)
            ._m30(this.m20 * double11)
            ._m31(this.m21 * double11)
            ._m32(this.m22 * double11)
            ._m33(this.m23 * double11)
            ._m20(double14)
            ._m21(double15)
            ._m22(double16)
            ._m23(double17)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._properties(0);
        return matrix4d1;
    }

    public Matrix4d frustumLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6) {
        return this.frustumLH(arg0, arg1, arg2, arg3, arg4, arg5, arg6, this);
    }

    @Override
    public Matrix4d frustumLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6) {
        return this.frustumLH(arg0, arg1, arg2, arg3, arg4, arg5, false, arg6);
    }

    public Matrix4d frustumLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.frustumLH(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    public Matrix4d setFrustumLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6) {
        if ((this.properties & 4) == 0) {
            this._identity();
        }

        this._m00((arg4 + arg4) / (arg1 - arg0))._m11((arg4 + arg4) / (arg3 - arg2))._m20((arg1 + arg0) / (arg1 - arg0))._m21((arg3 + arg2) / (arg3 - arg2));
        boolean boolean0 = arg5 > 0.0 && Double.isInfinite(arg5);
        boolean boolean1 = arg4 > 0.0 && Double.isInfinite(arg4);
        if (boolean0) {
            double double0 = 1.0E-6;
            this._m22(1.0 - double0)._m32((double0 - (arg6 ? 1.0 : 2.0)) * arg4);
        } else if (boolean1) {
            double double1 = 1.0E-6;
            this._m22((arg6 ? 0.0 : 1.0) - double1)._m32(((arg6 ? 1.0 : 2.0) - double1) * arg5);
        } else {
            this._m22((arg6 ? arg5 : arg5 + arg4) / (arg5 - arg4))._m32((arg6 ? arg5 : arg5 + arg5) * arg4 / (arg4 - arg5));
        }

        this._m23(1.0)._m33(0.0).properties = this.m20 == 0.0 && this.m21 == 0.0 ? 1 : 0;
        return this;
    }

    public Matrix4d setFrustumLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.setFrustumLH(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    public Matrix4d setFromIntrinsic(double arg0, double arg1, double arg2, double arg3, double arg4, int arg5, int arg6, double arg7, double arg8) {
        double double0 = 2.0 / arg5;
        double double1 = 2.0 / arg6;
        double double2 = 2.0 / (arg7 - arg8);
        this.m00 = double0 * arg0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m03 = 0.0;
        this.m10 = double0 * arg2;
        this.m11 = double1 * arg1;
        this.m12 = 0.0;
        this.m13 = 0.0;
        this.m20 = double0 * arg3 - 1.0;
        this.m21 = double1 * arg4 - 1.0;
        this.m22 = double2 * -(arg7 + arg8) + (arg8 + arg7) / (arg7 - arg8);
        this.m23 = -1.0;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = double2 * -arg7 * arg8;
        this.m33 = 0.0;
        this.properties = 1;
        return this;
    }

    @Override
    public Vector4d frustumPlane(int arg0, Vector4d arg1) {
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
    public Vector3d frustumCorner(int arg0, Vector3d arg1) {
        double double0;
        double double1;
        double double2;
        double double3;
        double double4;
        double double5;
        double double6;
        double double7;
        double double8;
        double double9;
        double double10;
        double double11;
        switch (arg0) {
            case 0:
                double3 = this.m03 + this.m00;
                double4 = this.m13 + this.m10;
                double5 = this.m23 + this.m20;
                double0 = this.m33 + this.m30;
                double6 = this.m03 + this.m01;
                double7 = this.m13 + this.m11;
                double8 = this.m23 + this.m21;
                double1 = this.m33 + this.m31;
                double9 = this.m03 + this.m02;
                double10 = this.m13 + this.m12;
                double11 = this.m23 + this.m22;
                double2 = this.m33 + this.m32;
                break;
            case 1:
                double3 = this.m03 - this.m00;
                double4 = this.m13 - this.m10;
                double5 = this.m23 - this.m20;
                double0 = this.m33 - this.m30;
                double6 = this.m03 + this.m01;
                double7 = this.m13 + this.m11;
                double8 = this.m23 + this.m21;
                double1 = this.m33 + this.m31;
                double9 = this.m03 + this.m02;
                double10 = this.m13 + this.m12;
                double11 = this.m23 + this.m22;
                double2 = this.m33 + this.m32;
                break;
            case 2:
                double3 = this.m03 - this.m00;
                double4 = this.m13 - this.m10;
                double5 = this.m23 - this.m20;
                double0 = this.m33 - this.m30;
                double6 = this.m03 - this.m01;
                double7 = this.m13 - this.m11;
                double8 = this.m23 - this.m21;
                double1 = this.m33 - this.m31;
                double9 = this.m03 + this.m02;
                double10 = this.m13 + this.m12;
                double11 = this.m23 + this.m22;
                double2 = this.m33 + this.m32;
                break;
            case 3:
                double3 = this.m03 + this.m00;
                double4 = this.m13 + this.m10;
                double5 = this.m23 + this.m20;
                double0 = this.m33 + this.m30;
                double6 = this.m03 - this.m01;
                double7 = this.m13 - this.m11;
                double8 = this.m23 - this.m21;
                double1 = this.m33 - this.m31;
                double9 = this.m03 + this.m02;
                double10 = this.m13 + this.m12;
                double11 = this.m23 + this.m22;
                double2 = this.m33 + this.m32;
                break;
            case 4:
                double3 = this.m03 - this.m00;
                double4 = this.m13 - this.m10;
                double5 = this.m23 - this.m20;
                double0 = this.m33 - this.m30;
                double6 = this.m03 + this.m01;
                double7 = this.m13 + this.m11;
                double8 = this.m23 + this.m21;
                double1 = this.m33 + this.m31;
                double9 = this.m03 - this.m02;
                double10 = this.m13 - this.m12;
                double11 = this.m23 - this.m22;
                double2 = this.m33 - this.m32;
                break;
            case 5:
                double3 = this.m03 + this.m00;
                double4 = this.m13 + this.m10;
                double5 = this.m23 + this.m20;
                double0 = this.m33 + this.m30;
                double6 = this.m03 + this.m01;
                double7 = this.m13 + this.m11;
                double8 = this.m23 + this.m21;
                double1 = this.m33 + this.m31;
                double9 = this.m03 - this.m02;
                double10 = this.m13 - this.m12;
                double11 = this.m23 - this.m22;
                double2 = this.m33 - this.m32;
                break;
            case 6:
                double3 = this.m03 + this.m00;
                double4 = this.m13 + this.m10;
                double5 = this.m23 + this.m20;
                double0 = this.m33 + this.m30;
                double6 = this.m03 - this.m01;
                double7 = this.m13 - this.m11;
                double8 = this.m23 - this.m21;
                double1 = this.m33 - this.m31;
                double9 = this.m03 - this.m02;
                double10 = this.m13 - this.m12;
                double11 = this.m23 - this.m22;
                double2 = this.m33 - this.m32;
                break;
            case 7:
                double3 = this.m03 - this.m00;
                double4 = this.m13 - this.m10;
                double5 = this.m23 - this.m20;
                double0 = this.m33 - this.m30;
                double6 = this.m03 - this.m01;
                double7 = this.m13 - this.m11;
                double8 = this.m23 - this.m21;
                double1 = this.m33 - this.m31;
                double9 = this.m03 - this.m02;
                double10 = this.m13 - this.m12;
                double11 = this.m23 - this.m22;
                double2 = this.m33 - this.m32;
                break;
            default:
                throw new IllegalArgumentException("corner");
        }

        double double12 = double7 * double11 - double8 * double10;
        double double13 = double8 * double9 - double6 * double11;
        double double14 = double6 * double10 - double7 * double9;
        double double15 = double10 * double5 - double11 * double4;
        double double16 = double11 * double3 - double9 * double5;
        double double17 = double9 * double4 - double10 * double3;
        double double18 = double4 * double8 - double5 * double7;
        double double19 = double5 * double6 - double3 * double8;
        double double20 = double3 * double7 - double4 * double6;
        double double21 = 1.0 / (double3 * double12 + double4 * double13 + double5 * double14);
        arg1.x = (-double12 * double0 - double15 * double1 - double18 * double2) * double21;
        arg1.y = (-double13 * double0 - double16 * double1 - double19 * double2) * double21;
        arg1.z = (-double14 * double0 - double17 * double1 - double20 * double2) * double21;
        return arg1;
    }

    @Override
    public Vector3d perspectiveOrigin(Vector3d arg0) {
        double double0 = this.m03 + this.m00;
        double double1 = this.m13 + this.m10;
        double double2 = this.m23 + this.m20;
        double double3 = this.m33 + this.m30;
        double double4 = this.m03 - this.m00;
        double double5 = this.m13 - this.m10;
        double double6 = this.m23 - this.m20;
        double double7 = this.m33 - this.m30;
        double double8 = this.m03 - this.m01;
        double double9 = this.m13 - this.m11;
        double double10 = this.m23 - this.m21;
        double double11 = this.m33 - this.m31;
        double double12 = double5 * double10 - double6 * double9;
        double double13 = double6 * double8 - double4 * double10;
        double double14 = double4 * double9 - double5 * double8;
        double double15 = double9 * double2 - double10 * double1;
        double double16 = double10 * double0 - double8 * double2;
        double double17 = double8 * double1 - double9 * double0;
        double double18 = double1 * double6 - double2 * double5;
        double double19 = double2 * double4 - double0 * double6;
        double double20 = double0 * double5 - double1 * double4;
        double double21 = 1.0 / (double0 * double12 + double1 * double13 + double2 * double14);
        arg0.x = (-double12 * double3 - double15 * double7 - double18 * double11) * double21;
        arg0.y = (-double13 * double3 - double16 * double7 - double19 * double11) * double21;
        arg0.z = (-double14 * double3 - double17 * double7 - double20 * double11) * double21;
        return arg0;
    }

    @Override
    public Vector3d perspectiveInvOrigin(Vector3d arg0) {
        double double0 = 1.0 / this.m23;
        arg0.x = this.m20 * double0;
        arg0.y = this.m21 * double0;
        arg0.z = this.m22 * double0;
        return arg0;
    }

    @Override
    public double perspectiveFov() {
        double double0 = this.m03 + this.m01;
        double double1 = this.m13 + this.m11;
        double double2 = this.m23 + this.m21;
        double double3 = this.m01 - this.m03;
        double double4 = this.m11 - this.m13;
        double double5 = this.m21 - this.m23;
        double double6 = Math.sqrt(double0 * double0 + double1 * double1 + double2 * double2);
        double double7 = Math.sqrt(double3 * double3 + double4 * double4 + double5 * double5);
        return Math.acos((double0 * double3 + double1 * double4 + double2 * double5) / (double6 * double7));
    }

    @Override
    public double perspectiveNear() {
        return this.m32 / (this.m23 + this.m22);
    }

    @Override
    public double perspectiveFar() {
        return this.m32 / (this.m22 - this.m23);
    }

    @Override
    public Vector3d frustumRayDir(double arg0, double arg1, Vector3d arg2) {
        double double0 = this.m10 * this.m23;
        double double1 = this.m13 * this.m21;
        double double2 = this.m10 * this.m21;
        double double3 = this.m11 * this.m23;
        double double4 = this.m13 * this.m20;
        double double5 = this.m11 * this.m20;
        double double6 = this.m03 * this.m20;
        double double7 = this.m01 * this.m23;
        double double8 = this.m01 * this.m20;
        double double9 = this.m03 * this.m21;
        double double10 = this.m00 * this.m23;
        double double11 = this.m00 * this.m21;
        double double12 = this.m00 * this.m13;
        double double13 = this.m03 * this.m11;
        double double14 = this.m00 * this.m11;
        double double15 = this.m01 * this.m13;
        double double16 = this.m03 * this.m10;
        double double17 = this.m01 * this.m10;
        double double18 = (double3 + double4 + double5 - double0 - double1 - double2) * (1.0 - arg1)
            + (double0 - double1 - double2 + double3 - double4 + double5) * arg1;
        double double19 = (double9 + double10 + double11 - double6 - double7 - double8) * (1.0 - arg1)
            + (double6 - double7 - double8 + double9 - double10 + double11) * arg1;
        double double20 = (double15 + double16 + double17 - double12 - double13 - double14) * (1.0 - arg1)
            + (double12 - double13 - double14 + double15 - double16 + double17) * arg1;
        double double21 = (double1 - double2 - double3 + double4 + double5 - double0) * (1.0 - arg1)
            + (double0 + double1 - double2 - double3 - double4 + double5) * arg1;
        double double22 = (double7 - double8 - double9 + double10 + double11 - double6) * (1.0 - arg1)
            + (double6 + double7 - double8 - double9 - double10 + double11) * arg1;
        double double23 = (double13 - double14 - double15 + double16 + double17 - double12) * (1.0 - arg1)
            + (double12 + double13 - double14 - double15 - double16 + double17) * arg1;
        arg2.x = double18 * (1.0 - arg0) + double21 * arg0;
        arg2.y = double19 * (1.0 - arg0) + double22 * arg0;
        arg2.z = double20 * (1.0 - arg0) + double23 * arg0;
        return arg2.normalize(arg2);
    }

    @Override
    public Vector3d positiveZ(Vector3d arg0) {
        return (this.properties & 16) != 0 ? this.normalizedPositiveZ(arg0) : this.positiveZGeneric(arg0);
    }

    private Vector3d positiveZGeneric(Vector3d vector3d) {
        return vector3d.set(this.m10 * this.m21 - this.m11 * this.m20, this.m20 * this.m01 - this.m21 * this.m00, this.m00 * this.m11 - this.m01 * this.m10)
            .normalize();
    }

    @Override
    public Vector3d normalizedPositiveZ(Vector3d arg0) {
        return arg0.set(this.m02, this.m12, this.m22);
    }

    @Override
    public Vector3d positiveX(Vector3d arg0) {
        return (this.properties & 16) != 0 ? this.normalizedPositiveX(arg0) : this.positiveXGeneric(arg0);
    }

    private Vector3d positiveXGeneric(Vector3d vector3d) {
        return vector3d.set(this.m11 * this.m22 - this.m12 * this.m21, this.m02 * this.m21 - this.m01 * this.m22, this.m01 * this.m12 - this.m02 * this.m11)
            .normalize();
    }

    @Override
    public Vector3d normalizedPositiveX(Vector3d arg0) {
        return arg0.set(this.m00, this.m10, this.m20);
    }

    @Override
    public Vector3d positiveY(Vector3d arg0) {
        return (this.properties & 16) != 0 ? this.normalizedPositiveY(arg0) : this.positiveYGeneric(arg0);
    }

    private Vector3d positiveYGeneric(Vector3d vector3d) {
        return vector3d.set(this.m12 * this.m20 - this.m10 * this.m22, this.m00 * this.m22 - this.m02 * this.m20, this.m02 * this.m10 - this.m00 * this.m12)
            .normalize();
    }

    @Override
    public Vector3d normalizedPositiveY(Vector3d arg0) {
        return arg0.set(this.m01, this.m11, this.m21);
    }

    @Override
    public Vector3d originAffine(Vector3d arg0) {
        double double0 = this.m00 * this.m11 - this.m01 * this.m10;
        double double1 = this.m00 * this.m12 - this.m02 * this.m10;
        double double2 = this.m01 * this.m12 - this.m02 * this.m11;
        double double3 = this.m20 * this.m31 - this.m21 * this.m30;
        double double4 = this.m20 * this.m32 - this.m22 * this.m30;
        double double5 = this.m21 * this.m32 - this.m22 * this.m31;
        arg0.x = -this.m10 * double5 + this.m11 * double4 - this.m12 * double3;
        arg0.y = this.m00 * double5 - this.m01 * double4 + this.m02 * double3;
        arg0.z = -this.m30 * double2 + this.m31 * double1 - this.m32 * double0;
        return arg0;
    }

    @Override
    public Vector3d origin(Vector3d arg0) {
        return (this.properties & 2) != 0 ? this.originAffine(arg0) : this.originGeneric(arg0);
    }

    private Vector3d originGeneric(Vector3d vector3d) {
        double double0 = this.m00 * this.m11 - this.m01 * this.m10;
        double double1 = this.m00 * this.m12 - this.m02 * this.m10;
        double double2 = this.m00 * this.m13 - this.m03 * this.m10;
        double double3 = this.m01 * this.m12 - this.m02 * this.m11;
        double double4 = this.m01 * this.m13 - this.m03 * this.m11;
        double double5 = this.m02 * this.m13 - this.m03 * this.m12;
        double double6 = this.m20 * this.m31 - this.m21 * this.m30;
        double double7 = this.m20 * this.m32 - this.m22 * this.m30;
        double double8 = this.m20 * this.m33 - this.m23 * this.m30;
        double double9 = this.m21 * this.m32 - this.m22 * this.m31;
        double double10 = this.m21 * this.m33 - this.m23 * this.m31;
        double double11 = this.m22 * this.m33 - this.m23 * this.m32;
        double double12 = double0 * double11 - double1 * double10 + double2 * double9 + double3 * double8 - double4 * double7 + double5 * double6;
        double double13 = 1.0 / double12;
        double double14 = (-this.m10 * double9 + this.m11 * double7 - this.m12 * double6) * double13;
        double double15 = (this.m00 * double9 - this.m01 * double7 + this.m02 * double6) * double13;
        double double16 = (-this.m30 * double3 + this.m31 * double1 - this.m32 * double0) * double13;
        double double17 = double12 / (this.m20 * double3 - this.m21 * double1 + this.m22 * double0);
        double double18 = double14 * double17;
        double double19 = double15 * double17;
        double double20 = double16 * double17;
        return vector3d.set(double18, double19, double20);
    }

    public Matrix4d shadow(Vector4dc arg0, double arg1, double arg2, double arg3, double arg4) {
        return this.shadow(arg0.x(), arg0.y(), arg0.z(), arg0.w(), arg1, arg2, arg3, arg4, this);
    }

    @Override
    public Matrix4d shadow(Vector4dc arg0, double arg1, double arg2, double arg3, double arg4, Matrix4d arg5) {
        return this.shadow(arg0.x(), arg0.y(), arg0.z(), arg0.w(), arg1, arg2, arg3, arg4, arg5);
    }

    public Matrix4d shadow(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7) {
        return this.shadow(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, this);
    }

    @Override
    public Matrix4d shadow(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, Matrix4d arg8) {
        double double0 = Math.invsqrt(arg4 * arg4 + arg5 * arg5 + arg6 * arg6);
        double double1 = arg4 * double0;
        double double2 = arg5 * double0;
        double double3 = arg6 * double0;
        double double4 = arg7 * double0;
        double double5 = double1 * arg0 + double2 * arg1 + double3 * arg2 + double4 * arg3;
        double double6 = double5 - double1 * arg0;
        double double7 = -double1 * arg1;
        double double8 = -double1 * arg2;
        double double9 = -double1 * arg3;
        double double10 = -double2 * arg0;
        double double11 = double5 - double2 * arg1;
        double double12 = -double2 * arg2;
        double double13 = -double2 * arg3;
        double double14 = -double3 * arg0;
        double double15 = -double3 * arg1;
        double double16 = double5 - double3 * arg2;
        double double17 = -double3 * arg3;
        double double18 = -double4 * arg0;
        double double19 = -double4 * arg1;
        double double20 = -double4 * arg2;
        double double21 = double5 - double4 * arg3;
        double double22 = this.m00 * double6 + this.m10 * double7 + this.m20 * double8 + this.m30 * double9;
        double double23 = this.m01 * double6 + this.m11 * double7 + this.m21 * double8 + this.m31 * double9;
        double double24 = this.m02 * double6 + this.m12 * double7 + this.m22 * double8 + this.m32 * double9;
        double double25 = this.m03 * double6 + this.m13 * double7 + this.m23 * double8 + this.m33 * double9;
        double double26 = this.m00 * double10 + this.m10 * double11 + this.m20 * double12 + this.m30 * double13;
        double double27 = this.m01 * double10 + this.m11 * double11 + this.m21 * double12 + this.m31 * double13;
        double double28 = this.m02 * double10 + this.m12 * double11 + this.m22 * double12 + this.m32 * double13;
        double double29 = this.m03 * double10 + this.m13 * double11 + this.m23 * double12 + this.m33 * double13;
        double double30 = this.m00 * double14 + this.m10 * double15 + this.m20 * double16 + this.m30 * double17;
        double double31 = this.m01 * double14 + this.m11 * double15 + this.m21 * double16 + this.m31 * double17;
        double double32 = this.m02 * double14 + this.m12 * double15 + this.m22 * double16 + this.m32 * double17;
        double double33 = this.m03 * double14 + this.m13 * double15 + this.m23 * double16 + this.m33 * double17;
        arg8._m30(this.m00 * double18 + this.m10 * double19 + this.m20 * double20 + this.m30 * double21)
            ._m31(this.m01 * double18 + this.m11 * double19 + this.m21 * double20 + this.m31 * double21)
            ._m32(this.m02 * double18 + this.m12 * double19 + this.m22 * double20 + this.m32 * double21)
            ._m33(this.m03 * double18 + this.m13 * double19 + this.m23 * double20 + this.m33 * double21)
            ._m00(double22)
            ._m01(double23)
            ._m02(double24)
            ._m03(double25)
            ._m10(double26)
            ._m11(double27)
            ._m12(double28)
            ._m13(double29)
            ._m20(double30)
            ._m21(double31)
            ._m22(double32)
            ._m23(double33)
            ._properties(this.properties & -30);
        return arg8;
    }

    @Override
    public Matrix4d shadow(Vector4dc arg0, Matrix4dc arg1, Matrix4d arg2) {
        double double0 = arg1.m10();
        double double1 = arg1.m11();
        double double2 = arg1.m12();
        double double3 = -double0 * arg1.m30() - double1 * arg1.m31() - double2 * arg1.m32();
        return this.shadow(arg0.x(), arg0.y(), arg0.z(), arg0.w(), double0, double1, double2, double3, arg2);
    }

    public Matrix4d shadow(Vector4d arg0, Matrix4d arg1) {
        return this.shadow(arg0, arg1, this);
    }

    @Override
    public Matrix4d shadow(double arg0, double arg1, double arg2, double arg3, Matrix4dc arg4, Matrix4d arg5) {
        double double0 = arg4.m10();
        double double1 = arg4.m11();
        double double2 = arg4.m12();
        double double3 = -double0 * arg4.m30() - double1 * arg4.m31() - double2 * arg4.m32();
        return this.shadow(arg0, arg1, arg2, arg3, double0, double1, double2, double3, arg5);
    }

    public Matrix4d shadow(double arg0, double arg1, double arg2, double arg3, Matrix4dc arg4) {
        return this.shadow(arg0, arg1, arg2, arg3, arg4, this);
    }

    public Matrix4d billboardCylindrical(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2) {
        double double0 = arg1.x() - arg0.x();
        double double1 = arg1.y() - arg0.y();
        double double2 = arg1.z() - arg0.z();
        double double3 = arg2.y() * double2 - arg2.z() * double1;
        double double4 = arg2.z() * double0 - arg2.x() * double2;
        double double5 = arg2.x() * double1 - arg2.y() * double0;
        double double6 = Math.invsqrt(double3 * double3 + double4 * double4 + double5 * double5);
        double3 *= double6;
        double4 *= double6;
        double5 *= double6;
        double0 = double4 * arg2.z() - double5 * arg2.y();
        double1 = double5 * arg2.x() - double3 * arg2.z();
        double2 = double3 * arg2.y() - double4 * arg2.x();
        double double7 = Math.invsqrt(double0 * double0 + double1 * double1 + double2 * double2);
        double0 *= double7;
        double1 *= double7;
        double2 *= double7;
        this._m00(double3)
            ._m01(double4)
            ._m02(double5)
            ._m03(0.0)
            ._m10(arg2.x())
            ._m11(arg2.y())
            ._m12(arg2.z())
            ._m13(0.0)
            ._m20(double0)
            ._m21(double1)
            ._m22(double2)
            ._m23(0.0)
            ._m30(arg0.x())
            ._m31(arg0.y())
            ._m32(arg0.z())
            ._m33(1.0)
            .properties = 18;
        return this;
    }

    public Matrix4d billboardSpherical(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2) {
        double double0 = arg1.x() - arg0.x();
        double double1 = arg1.y() - arg0.y();
        double double2 = arg1.z() - arg0.z();
        double double3 = Math.invsqrt(double0 * double0 + double1 * double1 + double2 * double2);
        double0 *= double3;
        double1 *= double3;
        double2 *= double3;
        double double4 = arg2.y() * double2 - arg2.z() * double1;
        double double5 = arg2.z() * double0 - arg2.x() * double2;
        double double6 = arg2.x() * double1 - arg2.y() * double0;
        double double7 = Math.invsqrt(double4 * double4 + double5 * double5 + double6 * double6);
        double4 *= double7;
        double5 *= double7;
        double6 *= double7;
        double double8 = double1 * double6 - double2 * double5;
        double double9 = double2 * double4 - double0 * double6;
        double double10 = double0 * double5 - double1 * double4;
        this._m00(double4)
            ._m01(double5)
            ._m02(double6)
            ._m03(0.0)
            ._m10(double8)
            ._m11(double9)
            ._m12(double10)
            ._m13(0.0)
            ._m20(double0)
            ._m21(double1)
            ._m22(double2)
            ._m23(0.0)
            ._m30(arg0.x())
            ._m31(arg0.y())
            ._m32(arg0.z())
            ._m33(1.0)
            .properties = 18;
        return this;
    }

    public Matrix4d billboardSpherical(Vector3dc arg0, Vector3dc arg1) {
        double double0 = arg1.x() - arg0.x();
        double double1 = arg1.y() - arg0.y();
        double double2 = arg1.z() - arg0.z();
        double double3 = -double1;
        double double4 = Math.sqrt(double0 * double0 + double1 * double1 + double2 * double2) + double2;
        double double5 = Math.invsqrt(double3 * double3 + double0 * double0 + double4 * double4);
        double3 *= double5;
        double double6 = double0 * double5;
        double4 *= double5;
        double double7 = (double3 + double3) * double3;
        double double8 = (double6 + double6) * double6;
        double double9 = (double3 + double3) * double6;
        double double10 = (double3 + double3) * double4;
        double double11 = (double6 + double6) * double4;
        this._m00(1.0 - double8)
            ._m01(double9)
            ._m02(-double11)
            ._m03(0.0)
            ._m10(double9)
            ._m11(1.0 - double7)
            ._m12(double10)
            ._m13(0.0)
            ._m20(double11)
            ._m21(-double10)
            ._m22(1.0 - double8 - double7)
            ._m23(0.0)
            ._m30(arg0.x())
            ._m31(arg0.y())
            ._m32(arg0.z())
            ._m33(1.0)
            .properties = 18;
        return this;
    }

    @Override
    public int hashCode() {
        int int0 = 1;
        long long0 = Double.doubleToLongBits(this.m00);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m01);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m02);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m03);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m10);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m11);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m12);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m13);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m20);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m21);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m22);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m23);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m30);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m31);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m32);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m33);
        return 31 * int0 + (int)(long0 ^ long0 >>> 32);
    }

    @Override
    public boolean equals(Object arg0) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (!(arg0 instanceof Matrix4d matrix4d)) {
            return false;
        } else if (Double.doubleToLongBits(this.m00) != Double.doubleToLongBits(matrix4d.m00)) {
            return false;
        } else if (Double.doubleToLongBits(this.m01) != Double.doubleToLongBits(matrix4d.m01)) {
            return false;
        } else if (Double.doubleToLongBits(this.m02) != Double.doubleToLongBits(matrix4d.m02)) {
            return false;
        } else if (Double.doubleToLongBits(this.m03) != Double.doubleToLongBits(matrix4d.m03)) {
            return false;
        } else if (Double.doubleToLongBits(this.m10) != Double.doubleToLongBits(matrix4d.m10)) {
            return false;
        } else if (Double.doubleToLongBits(this.m11) != Double.doubleToLongBits(matrix4d.m11)) {
            return false;
        } else if (Double.doubleToLongBits(this.m12) != Double.doubleToLongBits(matrix4d.m12)) {
            return false;
        } else if (Double.doubleToLongBits(this.m13) != Double.doubleToLongBits(matrix4d.m13)) {
            return false;
        } else if (Double.doubleToLongBits(this.m20) != Double.doubleToLongBits(matrix4d.m20)) {
            return false;
        } else if (Double.doubleToLongBits(this.m21) != Double.doubleToLongBits(matrix4d.m21)) {
            return false;
        } else if (Double.doubleToLongBits(this.m22) != Double.doubleToLongBits(matrix4d.m22)) {
            return false;
        } else if (Double.doubleToLongBits(this.m23) != Double.doubleToLongBits(matrix4d.m23)) {
            return false;
        } else if (Double.doubleToLongBits(this.m30) != Double.doubleToLongBits(matrix4d.m30)) {
            return false;
        } else if (Double.doubleToLongBits(this.m31) != Double.doubleToLongBits(matrix4d.m31)) {
            return false;
        } else {
            return Double.doubleToLongBits(this.m32) != Double.doubleToLongBits(matrix4d.m32)
                ? false
                : Double.doubleToLongBits(this.m33) == Double.doubleToLongBits(matrix4d.m33);
        }
    }

    @Override
    public boolean equals(Matrix4dc arg0, double arg1) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (!(arg0 instanceof Matrix4d)) {
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
    public Matrix4d pick(double double5, double double7, double double1, double double3, int[] ints, Matrix4d matrix4d1) {
        double double0 = ints[2] / double1;
        double double2 = ints[3] / double3;
        double double4 = (ints[2] + 2.0 * (ints[0] - double5)) / double1;
        double double6 = (ints[3] + 2.0 * (ints[1] - double7)) / double3;
        matrix4d1._m30(this.m00 * double4 + this.m10 * double6 + this.m30)
            ._m31(this.m01 * double4 + this.m11 * double6 + this.m31)
            ._m32(this.m02 * double4 + this.m12 * double6 + this.m32)
            ._m33(this.m03 * double4 + this.m13 * double6 + this.m33)
            ._m00(this.m00 * double0)
            ._m01(this.m01 * double0)
            ._m02(this.m02 * double0)
            ._m03(this.m03 * double0)
            ._m10(this.m10 * double2)
            ._m11(this.m11 * double2)
            ._m12(this.m12 * double2)
            ._m13(this.m13 * double2)
            ._properties(0);
        return matrix4d1;
    }

    public Matrix4d pick(double double0, double double1, double double2, double double3, int[] ints) {
        return this.pick(double0, double1, double2, double3, ints, this);
    }

    @Override
    public boolean isAffine() {
        return this.m03 == 0.0 && this.m13 == 0.0 && this.m23 == 0.0 && this.m33 == 1.0;
    }

    public Matrix4d swap(Matrix4d arg0) {
        double double0 = this.m00;
        this.m00 = arg0.m00;
        arg0.m00 = double0;
        double0 = this.m01;
        this.m01 = arg0.m01;
        arg0.m01 = double0;
        double0 = this.m02;
        this.m02 = arg0.m02;
        arg0.m02 = double0;
        double0 = this.m03;
        this.m03 = arg0.m03;
        arg0.m03 = double0;
        double0 = this.m10;
        this.m10 = arg0.m10;
        arg0.m10 = double0;
        double0 = this.m11;
        this.m11 = arg0.m11;
        arg0.m11 = double0;
        double0 = this.m12;
        this.m12 = arg0.m12;
        arg0.m12 = double0;
        double0 = this.m13;
        this.m13 = arg0.m13;
        arg0.m13 = double0;
        double0 = this.m20;
        this.m20 = arg0.m20;
        arg0.m20 = double0;
        double0 = this.m21;
        this.m21 = arg0.m21;
        arg0.m21 = double0;
        double0 = this.m22;
        this.m22 = arg0.m22;
        arg0.m22 = double0;
        double0 = this.m23;
        this.m23 = arg0.m23;
        arg0.m23 = double0;
        double0 = this.m30;
        this.m30 = arg0.m30;
        arg0.m30 = double0;
        double0 = this.m31;
        this.m31 = arg0.m31;
        arg0.m31 = double0;
        double0 = this.m32;
        this.m32 = arg0.m32;
        arg0.m32 = double0;
        double0 = this.m33;
        this.m33 = arg0.m33;
        arg0.m33 = double0;
        int int0 = this.properties;
        this.properties = arg0.properties;
        arg0.properties = int0;
        return this;
    }

    @Override
    public Matrix4d arcball(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6) {
        double double0 = this.m20 * -arg0 + this.m30;
        double double1 = this.m21 * -arg0 + this.m31;
        double double2 = this.m22 * -arg0 + this.m32;
        double double3 = this.m23 * -arg0 + this.m33;
        double double4 = Math.sin(arg4);
        double double5 = Math.cosFromSin(double4, arg4);
        double double6 = this.m10 * double5 + this.m20 * double4;
        double double7 = this.m11 * double5 + this.m21 * double4;
        double double8 = this.m12 * double5 + this.m22 * double4;
        double double9 = this.m13 * double5 + this.m23 * double4;
        double double10 = this.m20 * double5 - this.m10 * double4;
        double double11 = this.m21 * double5 - this.m11 * double4;
        double double12 = this.m22 * double5 - this.m12 * double4;
        double double13 = this.m23 * double5 - this.m13 * double4;
        double4 = Math.sin(arg5);
        double5 = Math.cosFromSin(double4, arg5);
        double double14 = this.m00 * double5 - double10 * double4;
        double double15 = this.m01 * double5 - double11 * double4;
        double double16 = this.m02 * double5 - double12 * double4;
        double double17 = this.m03 * double5 - double13 * double4;
        double double18 = this.m00 * double4 + double10 * double5;
        double double19 = this.m01 * double4 + double11 * double5;
        double double20 = this.m02 * double4 + double12 * double5;
        double double21 = this.m03 * double4 + double13 * double5;
        arg6._m30(-double14 * arg1 - double6 * arg2 - double18 * arg3 + double0)
            ._m31(-double15 * arg1 - double7 * arg2 - double19 * arg3 + double1)
            ._m32(-double16 * arg1 - double8 * arg2 - double20 * arg3 + double2)
            ._m33(-double17 * arg1 - double9 * arg2 - double21 * arg3 + double3)
            ._m20(double18)
            ._m21(double19)
            ._m22(double20)
            ._m23(double21)
            ._m10(double6)
            ._m11(double7)
            ._m12(double8)
            ._m13(double9)
            ._m00(double14)
            ._m01(double15)
            ._m02(double16)
            ._m03(double17)
            ._properties(this.properties & -14);
        return arg6;
    }

    @Override
    public Matrix4d arcball(double arg0, Vector3dc arg1, double arg2, double arg3, Matrix4d arg4) {
        return this.arcball(arg0, arg1.x(), arg1.y(), arg1.z(), arg2, arg3, arg4);
    }

    public Matrix4d arcball(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.arcball(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    public Matrix4d arcball(double arg0, Vector3dc arg1, double arg2, double arg3) {
        return this.arcball(arg0, arg1.x(), arg1.y(), arg1.z(), arg2, arg3, this);
    }

    public Matrix4d frustumAabb(Vector3d arg0, Vector3d arg1) {
        double double0 = Double.POSITIVE_INFINITY;
        double double1 = Double.POSITIVE_INFINITY;
        double double2 = Double.POSITIVE_INFINITY;
        double double3 = Double.NEGATIVE_INFINITY;
        double double4 = Double.NEGATIVE_INFINITY;
        double double5 = Double.NEGATIVE_INFINITY;

        for (int int0 = 0; int0 < 8; int0++) {
            double double6 = ((int0 & 1) << 1) - 1.0;
            double double7 = ((int0 >>> 1 & 1) << 1) - 1.0;
            double double8 = ((int0 >>> 2 & 1) << 1) - 1.0;
            double double9 = 1.0 / (this.m03 * double6 + this.m13 * double7 + this.m23 * double8 + this.m33);
            double double10 = (this.m00 * double6 + this.m10 * double7 + this.m20 * double8 + this.m30) * double9;
            double double11 = (this.m01 * double6 + this.m11 * double7 + this.m21 * double8 + this.m31) * double9;
            double double12 = (this.m02 * double6 + this.m12 * double7 + this.m22 * double8 + this.m32) * double9;
            double0 = double0 < double10 ? double0 : double10;
            double1 = double1 < double11 ? double1 : double11;
            double2 = double2 < double12 ? double2 : double12;
            double3 = double3 > double10 ? double3 : double10;
            double4 = double4 > double11 ? double4 : double11;
            double5 = double5 > double12 ? double5 : double12;
        }

        arg0.x = double0;
        arg0.y = double1;
        arg0.z = double2;
        arg1.x = double3;
        arg1.y = double4;
        arg1.z = double5;
        return this;
    }

    @Override
    public Matrix4d projectedGridRange(Matrix4dc arg0, double arg1, double arg2, Matrix4d arg3) {
        double double0 = Double.POSITIVE_INFINITY;
        double double1 = Double.POSITIVE_INFINITY;
        double double2 = Double.NEGATIVE_INFINITY;
        double double3 = Double.NEGATIVE_INFINITY;
        boolean boolean0 = false;

        for (int int0 = 0; int0 < 12; int0++) {
            double double4;
            double double5;
            double double6;
            double double7;
            double double8;
            double double9;
            if (int0 < 4) {
                double4 = -1.0;
                double7 = 1.0;
                double5 = double8 = ((int0 & 1) << 1) - 1.0;
                double6 = double9 = ((int0 >>> 1 & 1) << 1) - 1.0;
            } else if (int0 < 8) {
                double5 = -1.0;
                double8 = 1.0;
                double4 = double7 = ((int0 & 1) << 1) - 1.0;
                double6 = double9 = ((int0 >>> 1 & 1) << 1) - 1.0;
            } else {
                double6 = -1.0;
                double9 = 1.0;
                double4 = double7 = ((int0 & 1) << 1) - 1.0;
                double5 = double8 = ((int0 >>> 1 & 1) << 1) - 1.0;
            }

            double double10 = 1.0 / (this.m03 * double4 + this.m13 * double5 + this.m23 * double6 + this.m33);
            double double11 = (this.m00 * double4 + this.m10 * double5 + this.m20 * double6 + this.m30) * double10;
            double double12 = (this.m01 * double4 + this.m11 * double5 + this.m21 * double6 + this.m31) * double10;
            double double13 = (this.m02 * double4 + this.m12 * double5 + this.m22 * double6 + this.m32) * double10;
            double10 = 1.0 / (this.m03 * double7 + this.m13 * double8 + this.m23 * double9 + this.m33);
            double double14 = (this.m00 * double7 + this.m10 * double8 + this.m20 * double9 + this.m30) * double10;
            double double15 = (this.m01 * double7 + this.m11 * double8 + this.m21 * double9 + this.m31) * double10;
            double double16 = (this.m02 * double7 + this.m12 * double8 + this.m22 * double9 + this.m32) * double10;
            double double17 = double14 - double11;
            double double18 = double15 - double12;
            double double19 = double16 - double13;
            double double20 = 1.0 / double18;

            for (int int1 = 0; int1 < 2; int1++) {
                double double21 = -(double12 + (int1 == 0 ? arg1 : arg2)) * double20;
                if (double21 >= 0.0 && double21 <= 1.0) {
                    boolean0 = true;
                    double double22 = double11 + double21 * double17;
                    double double23 = double13 + double21 * double19;
                    double10 = 1.0 / (arg0.m03() * double22 + arg0.m23() * double23 + arg0.m33());
                    double double24 = (arg0.m00() * double22 + arg0.m20() * double23 + arg0.m30()) * double10;
                    double double25 = (arg0.m01() * double22 + arg0.m21() * double23 + arg0.m31()) * double10;
                    double0 = double0 < double24 ? double0 : double24;
                    double1 = double1 < double25 ? double1 : double25;
                    double2 = double2 > double24 ? double2 : double24;
                    double3 = double3 > double25 ? double3 : double25;
                }
            }
        }

        if (!boolean0) {
            return null;
        } else {
            arg3.set(double2 - double0, 0.0, 0.0, 0.0, 0.0, double3 - double1, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, double0, double1, 0.0, 1.0)._properties(2);
            return arg3;
        }
    }

    @Override
    public Matrix4d perspectiveFrustumSlice(double arg0, double arg1, Matrix4d arg2) {
        double double0 = (this.m23 + this.m22) / this.m32;
        double double1 = 1.0 / (arg0 - arg1);
        arg2._m00(this.m00 * double0 * arg0)
            ._m01(this.m01)
            ._m02(this.m02)
            ._m03(this.m03)
            ._m10(this.m10)
            ._m11(this.m11 * double0 * arg0)
            ._m12(this.m12)
            ._m13(this.m13)
            ._m20(this.m20)
            ._m21(this.m21)
            ._m22((arg1 + arg0) * double1)
            ._m23(this.m23)
            ._m30(this.m30)
            ._m31(this.m31)
            ._m32((arg1 + arg1) * arg0 * double1)
            ._m33(this.m33)
            ._properties(this.properties & -29);
        return arg2;
    }

    @Override
    public Matrix4d orthoCrop(Matrix4dc arg0, Matrix4d arg1) {
        double double0 = Double.POSITIVE_INFINITY;
        double double1 = Double.NEGATIVE_INFINITY;
        double double2 = Double.POSITIVE_INFINITY;
        double double3 = Double.NEGATIVE_INFINITY;
        double double4 = Double.POSITIVE_INFINITY;
        double double5 = Double.NEGATIVE_INFINITY;

        for (int int0 = 0; int0 < 8; int0++) {
            double double6 = ((int0 & 1) << 1) - 1.0;
            double double7 = ((int0 >>> 1 & 1) << 1) - 1.0;
            double double8 = ((int0 >>> 2 & 1) << 1) - 1.0;
            double double9 = 1.0 / (this.m03 * double6 + this.m13 * double7 + this.m23 * double8 + this.m33);
            double double10 = (this.m00 * double6 + this.m10 * double7 + this.m20 * double8 + this.m30) * double9;
            double double11 = (this.m01 * double6 + this.m11 * double7 + this.m21 * double8 + this.m31) * double9;
            double double12 = (this.m02 * double6 + this.m12 * double7 + this.m22 * double8 + this.m32) * double9;
            double9 = 1.0 / (arg0.m03() * double10 + arg0.m13() * double11 + arg0.m23() * double12 + arg0.m33());
            double double13 = arg0.m00() * double10 + arg0.m10() * double11 + arg0.m20() * double12 + arg0.m30();
            double double14 = arg0.m01() * double10 + arg0.m11() * double11 + arg0.m21() * double12 + arg0.m31();
            double double15 = (arg0.m02() * double10 + arg0.m12() * double11 + arg0.m22() * double12 + arg0.m32()) * double9;
            double0 = double0 < double13 ? double0 : double13;
            double1 = double1 > double13 ? double1 : double13;
            double2 = double2 < double14 ? double2 : double14;
            double3 = double3 > double14 ? double3 : double14;
            double4 = double4 < double15 ? double4 : double15;
            double5 = double5 > double15 ? double5 : double15;
        }

        return arg1.setOrtho(double0, double1, double2, double3, -double5, -double4);
    }

    public Matrix4d trapezoidCrop(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7) {
        double double0 = arg3 - arg1;
        double double1 = arg0 - arg2;
        double double2 = -double0;
        double double3 = double0 * arg1 - double1 * arg0;
        double double4 = -(double0 * arg0 + double1 * arg1);
        double double5 = double1 * arg6 + double2 * arg7 + double3;
        double double6 = double0 * arg6 + double1 * arg7 + double4;
        double double7 = -double5 / double6;
        double double8 = double1 + double7 * double0;
        double2 += double7 * double1;
        double3 += double7 * double4;
        double double9 = double8 * arg2 + double2 * arg3 + double3;
        double double10 = double8 * arg4 + double2 * arg5 + double3;
        double double11 = double9 * double6 / (double10 - double9);
        double4 += double11;
        double double12 = 2.0 / double10;
        double double13 = 1.0 / (double6 + double11);
        double double14 = (double13 + double13) * double11 / (1.0 - double13 * double11);
        double double15 = double0 * double13;
        double double16 = double1 * double13;
        double double17 = double4 * double13;
        double double18 = (double14 + 1.0) * double15;
        double double19 = (double14 + 1.0) * double16;
        double4 = (double14 + 1.0) * double17 - double14;
        double8 = double12 * double8 - double15;
        double2 = double12 * double2 - double16;
        double3 = double12 * double3 - double17;
        this.set(double8, double18, 0.0, double15, double2, double19, 0.0, double16, 0.0, 0.0, 1.0, 0.0, double3, double4, 0.0, double17);
        this.properties = 0;
        return this;
    }

    @Override
    public Matrix4d transformAab(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Vector3d arg6, Vector3d arg7) {
        double double0 = this.m00 * arg0;
        double double1 = this.m01 * arg0;
        double double2 = this.m02 * arg0;
        double double3 = this.m00 * arg3;
        double double4 = this.m01 * arg3;
        double double5 = this.m02 * arg3;
        double double6 = this.m10 * arg1;
        double double7 = this.m11 * arg1;
        double double8 = this.m12 * arg1;
        double double9 = this.m10 * arg4;
        double double10 = this.m11 * arg4;
        double double11 = this.m12 * arg4;
        double double12 = this.m20 * arg2;
        double double13 = this.m21 * arg2;
        double double14 = this.m22 * arg2;
        double double15 = this.m20 * arg5;
        double double16 = this.m21 * arg5;
        double double17 = this.m22 * arg5;
        double double19;
        double double18;
        if (double0 < double3) {
            double19 = double0;
            double18 = double3;
        } else {
            double19 = double3;
            double18 = double0;
        }

        double double21;
        double double20;
        if (double1 < double4) {
            double21 = double1;
            double20 = double4;
        } else {
            double21 = double4;
            double20 = double1;
        }

        double double23;
        double double22;
        if (double2 < double5) {
            double23 = double2;
            double22 = double5;
        } else {
            double23 = double5;
            double22 = double2;
        }

        double double25;
        double double24;
        if (double6 < double9) {
            double25 = double6;
            double24 = double9;
        } else {
            double25 = double9;
            double24 = double6;
        }

        double double27;
        double double26;
        if (double7 < double10) {
            double27 = double7;
            double26 = double10;
        } else {
            double27 = double10;
            double26 = double7;
        }

        double double29;
        double double28;
        if (double8 < double11) {
            double29 = double8;
            double28 = double11;
        } else {
            double29 = double11;
            double28 = double8;
        }

        double double31;
        double double30;
        if (double12 < double15) {
            double31 = double12;
            double30 = double15;
        } else {
            double31 = double15;
            double30 = double12;
        }

        double double32;
        double double33;
        if (double13 < double16) {
            double32 = double13;
            double33 = double16;
        } else {
            double32 = double16;
            double33 = double13;
        }

        double double34;
        double double35;
        if (double14 < double17) {
            double34 = double14;
            double35 = double17;
        } else {
            double34 = double17;
            double35 = double14;
        }

        arg6.x = double19 + double25 + double31 + this.m30;
        arg6.y = double21 + double27 + double32 + this.m31;
        arg6.z = double23 + double29 + double34 + this.m32;
        arg7.x = double18 + double24 + double30 + this.m30;
        arg7.y = double20 + double26 + double33 + this.m31;
        arg7.z = double22 + double28 + double35 + this.m32;
        return this;
    }

    @Override
    public Matrix4d transformAab(Vector3dc arg0, Vector3dc arg1, Vector3d arg2, Vector3d arg3) {
        return this.transformAab(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2, arg3);
    }

    public Matrix4d lerp(Matrix4dc arg0, double arg1) {
        return this.lerp(arg0, arg1, this);
    }

    @Override
    public Matrix4d lerp(Matrix4dc arg0, double arg1, Matrix4d arg2) {
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
    public Matrix4d rotateTowards(Vector3dc arg0, Vector3dc arg1, Matrix4d arg2) {
        return this.rotateTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    public Matrix4d rotateTowards(Vector3dc arg0, Vector3dc arg1) {
        return this.rotateTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), this);
    }

    public Matrix4d rotateTowards(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.rotateTowards(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    @Override
    public Matrix4d rotateTowards(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6) {
        double double0 = Math.invsqrt(arg0 * arg0 + arg1 * arg1 + arg2 * arg2);
        double double1 = arg0 * double0;
        double double2 = arg1 * double0;
        double double3 = arg2 * double0;
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
        double double11 = this.m00 * double4 + this.m10 * double5 + this.m20 * double6;
        double double12 = this.m01 * double4 + this.m11 * double5 + this.m21 * double6;
        double double13 = this.m02 * double4 + this.m12 * double5 + this.m22 * double6;
        double double14 = this.m03 * double4 + this.m13 * double5 + this.m23 * double6;
        double double15 = this.m00 * double8 + this.m10 * double9 + this.m20 * double10;
        double double16 = this.m01 * double8 + this.m11 * double9 + this.m21 * double10;
        double double17 = this.m02 * double8 + this.m12 * double9 + this.m22 * double10;
        double double18 = this.m03 * double8 + this.m13 * double9 + this.m23 * double10;
        arg6._m30(this.m30)
            ._m31(this.m31)
            ._m32(this.m32)
            ._m33(this.m33)
            ._m20(this.m00 * double1 + this.m10 * double2 + this.m20 * double3)
            ._m21(this.m01 * double1 + this.m11 * double2 + this.m21 * double3)
            ._m22(this.m02 * double1 + this.m12 * double2 + this.m22 * double3)
            ._m23(this.m03 * double1 + this.m13 * double2 + this.m23 * double3)
            ._m00(double11)
            ._m01(double12)
            ._m02(double13)
            ._m03(double14)
            ._m10(double15)
            ._m11(double16)
            ._m12(double17)
            ._m13(double18)
            ._properties(this.properties & -14);
        return arg6;
    }

    public Matrix4d rotationTowards(Vector3dc arg0, Vector3dc arg1) {
        return this.rotationTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix4d rotationTowards(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        double double0 = Math.invsqrt(arg0 * arg0 + arg1 * arg1 + arg2 * arg2);
        double double1 = arg0 * double0;
        double double2 = arg1 * double0;
        double double3 = arg2 * double0;
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
        if ((this.properties & 4) == 0) {
            this._identity();
        }

        this.m00 = double4;
        this.m01 = double5;
        this.m02 = double6;
        this.m10 = double8;
        this.m11 = double9;
        this.m12 = double10;
        this.m20 = double1;
        this.m21 = double2;
        this.m22 = double3;
        this.properties = 18;
        return this;
    }

    public Matrix4d translationRotateTowards(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2) {
        return this.translationRotateTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z());
    }

    public Matrix4d translationRotateTowards(
        double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8
    ) {
        double double0 = Math.invsqrt(arg3 * arg3 + arg4 * arg4 + arg5 * arg5);
        double double1 = arg3 * double0;
        double double2 = arg4 * double0;
        double double3 = arg5 * double0;
        double double4 = arg7 * double3 - arg8 * double2;
        double double5 = arg8 * double1 - arg6 * double3;
        double double6 = arg6 * double2 - arg7 * double1;
        double double7 = Math.invsqrt(double4 * double4 + double5 * double5 + double6 * double6);
        double4 *= double7;
        double5 *= double7;
        double6 *= double7;
        double double8 = double2 * double6 - double3 * double5;
        double double9 = double3 * double4 - double1 * double6;
        double double10 = double1 * double5 - double2 * double4;
        this.m00 = double4;
        this.m01 = double5;
        this.m02 = double6;
        this.m03 = 0.0;
        this.m10 = double8;
        this.m11 = double9;
        this.m12 = double10;
        this.m13 = 0.0;
        this.m20 = double1;
        this.m21 = double2;
        this.m22 = double3;
        this.m23 = 0.0;
        this.m30 = arg0;
        this.m31 = arg1;
        this.m32 = arg2;
        this.m33 = 1.0;
        this.properties = 18;
        return this;
    }

    @Override
    public Vector3d getEulerAnglesZYX(Vector3d arg0) {
        arg0.x = Math.atan2(this.m12, this.m22);
        arg0.y = Math.atan2(-this.m02, Math.sqrt(this.m12 * this.m12 + this.m22 * this.m22));
        arg0.z = Math.atan2(this.m01, this.m00);
        return arg0;
    }

    public Matrix4d affineSpan(Vector3d arg0, Vector3d arg1, Vector3d arg2, Vector3d arg3) {
        double double0 = this.m10 * this.m22;
        double double1 = this.m10 * this.m21;
        double double2 = this.m10 * this.m02;
        double double3 = this.m10 * this.m01;
        double double4 = this.m11 * this.m22;
        double double5 = this.m11 * this.m20;
        double double6 = this.m11 * this.m02;
        double double7 = this.m11 * this.m00;
        double double8 = this.m12 * this.m21;
        double double9 = this.m12 * this.m20;
        double double10 = this.m12 * this.m01;
        double double11 = this.m12 * this.m00;
        double double12 = this.m20 * this.m02;
        double double13 = this.m20 * this.m01;
        double double14 = this.m21 * this.m02;
        double double15 = this.m21 * this.m00;
        double double16 = this.m22 * this.m01;
        double double17 = this.m22 * this.m00;
        double double18 = 1.0 / (this.m00 * this.m11 - this.m01 * this.m10) * this.m22
            + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21
            + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
        double double19 = (double4 - double8) * double18;
        double double20 = (double14 - double16) * double18;
        double double21 = (double10 - double6) * double18;
        double double22 = (double9 - double0) * double18;
        double double23 = (double17 - double12) * double18;
        double double24 = (double2 - double11) * double18;
        double double25 = (double1 - double5) * double18;
        double double26 = (double13 - double15) * double18;
        double double27 = (double7 - double3) * double18;
        arg0.x = -double19
            - double22
            - double25
            + (double0 * this.m31 - double1 * this.m32 + double5 * this.m32 - double4 * this.m30 + double8 * this.m30 - double9 * this.m31) * double18;
        arg0.y = -double20
            - double23
            - double26
            + (double12 * this.m31 - double13 * this.m32 + double15 * this.m32 - double14 * this.m30 + double16 * this.m30 - double17 * this.m31) * double18;
        arg0.z = -double21
            - double24
            - double27
            + (double6 * this.m30 - double10 * this.m30 + double11 * this.m31 - double2 * this.m31 + double3 * this.m32 - double7 * this.m32) * double18;
        arg1.x = 2.0 * double19;
        arg1.y = 2.0 * double20;
        arg1.z = 2.0 * double21;
        arg2.x = 2.0 * double22;
        arg2.y = 2.0 * double23;
        arg2.z = 2.0 * double24;
        arg3.x = 2.0 * double25;
        arg3.y = 2.0 * double26;
        arg3.z = 2.0 * double27;
        return this;
    }

    @Override
    public boolean testPoint(double arg0, double arg1, double arg2) {
        double double0 = this.m03 + this.m00;
        double double1 = this.m13 + this.m10;
        double double2 = this.m23 + this.m20;
        double double3 = this.m33 + this.m30;
        double double4 = this.m03 - this.m00;
        double double5 = this.m13 - this.m10;
        double double6 = this.m23 - this.m20;
        double double7 = this.m33 - this.m30;
        double double8 = this.m03 + this.m01;
        double double9 = this.m13 + this.m11;
        double double10 = this.m23 + this.m21;
        double double11 = this.m33 + this.m31;
        double double12 = this.m03 - this.m01;
        double double13 = this.m13 - this.m11;
        double double14 = this.m23 - this.m21;
        double double15 = this.m33 - this.m31;
        double double16 = this.m03 + this.m02;
        double double17 = this.m13 + this.m12;
        double double18 = this.m23 + this.m22;
        double double19 = this.m33 + this.m32;
        double double20 = this.m03 - this.m02;
        double double21 = this.m13 - this.m12;
        double double22 = this.m23 - this.m22;
        double double23 = this.m33 - this.m32;
        return double0 * arg0 + double1 * arg1 + double2 * arg2 + double3 >= 0.0
            && double4 * arg0 + double5 * arg1 + double6 * arg2 + double7 >= 0.0
            && double8 * arg0 + double9 * arg1 + double10 * arg2 + double11 >= 0.0
            && double12 * arg0 + double13 * arg1 + double14 * arg2 + double15 >= 0.0
            && double16 * arg0 + double17 * arg1 + double18 * arg2 + double19 >= 0.0
            && double20 * arg0 + double21 * arg1 + double22 * arg2 + double23 >= 0.0;
    }

    @Override
    public boolean testSphere(double arg0, double arg1, double arg2, double arg3) {
        double double0 = this.m03 + this.m00;
        double double1 = this.m13 + this.m10;
        double double2 = this.m23 + this.m20;
        double double3 = this.m33 + this.m30;
        double double4 = Math.invsqrt(double0 * double0 + double1 * double1 + double2 * double2);
        double0 *= double4;
        double1 *= double4;
        double2 *= double4;
        double3 *= double4;
        double double5 = this.m03 - this.m00;
        double double6 = this.m13 - this.m10;
        double double7 = this.m23 - this.m20;
        double double8 = this.m33 - this.m30;
        double4 = Math.invsqrt(double5 * double5 + double6 * double6 + double7 * double7);
        double5 *= double4;
        double6 *= double4;
        double7 *= double4;
        double8 *= double4;
        double double9 = this.m03 + this.m01;
        double double10 = this.m13 + this.m11;
        double double11 = this.m23 + this.m21;
        double double12 = this.m33 + this.m31;
        double4 = Math.invsqrt(double9 * double9 + double10 * double10 + double11 * double11);
        double9 *= double4;
        double10 *= double4;
        double11 *= double4;
        double12 *= double4;
        double double13 = this.m03 - this.m01;
        double double14 = this.m13 - this.m11;
        double double15 = this.m23 - this.m21;
        double double16 = this.m33 - this.m31;
        double4 = Math.invsqrt(double13 * double13 + double14 * double14 + double15 * double15);
        double13 *= double4;
        double14 *= double4;
        double15 *= double4;
        double16 *= double4;
        double double17 = this.m03 + this.m02;
        double double18 = this.m13 + this.m12;
        double double19 = this.m23 + this.m22;
        double double20 = this.m33 + this.m32;
        double4 = Math.invsqrt(double17 * double17 + double18 * double18 + double19 * double19);
        double17 *= double4;
        double18 *= double4;
        double19 *= double4;
        double20 *= double4;
        double double21 = this.m03 - this.m02;
        double double22 = this.m13 - this.m12;
        double double23 = this.m23 - this.m22;
        double double24 = this.m33 - this.m32;
        double4 = Math.invsqrt(double21 * double21 + double22 * double22 + double23 * double23);
        double21 *= double4;
        double22 *= double4;
        double23 *= double4;
        double24 *= double4;
        return double0 * arg0 + double1 * arg1 + double2 * arg2 + double3 >= -arg3
            && double5 * arg0 + double6 * arg1 + double7 * arg2 + double8 >= -arg3
            && double9 * arg0 + double10 * arg1 + double11 * arg2 + double12 >= -arg3
            && double13 * arg0 + double14 * arg1 + double15 * arg2 + double16 >= -arg3
            && double17 * arg0 + double18 * arg1 + double19 * arg2 + double20 >= -arg3
            && double21 * arg0 + double22 * arg1 + double23 * arg2 + double24 >= -arg3;
    }

    @Override
    public boolean testAab(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        double double0 = this.m03 + this.m00;
        double double1 = this.m13 + this.m10;
        double double2 = this.m23 + this.m20;
        double double3 = this.m33 + this.m30;
        double double4 = this.m03 - this.m00;
        double double5 = this.m13 - this.m10;
        double double6 = this.m23 - this.m20;
        double double7 = this.m33 - this.m30;
        double double8 = this.m03 + this.m01;
        double double9 = this.m13 + this.m11;
        double double10 = this.m23 + this.m21;
        double double11 = this.m33 + this.m31;
        double double12 = this.m03 - this.m01;
        double double13 = this.m13 - this.m11;
        double double14 = this.m23 - this.m21;
        double double15 = this.m33 - this.m31;
        double double16 = this.m03 + this.m02;
        double double17 = this.m13 + this.m12;
        double double18 = this.m23 + this.m22;
        double double19 = this.m33 + this.m32;
        double double20 = this.m03 - this.m02;
        double double21 = this.m13 - this.m12;
        double double22 = this.m23 - this.m22;
        double double23 = this.m33 - this.m32;
        return double0 * (double0 < 0.0 ? arg0 : arg3) + double1 * (double1 < 0.0 ? arg1 : arg4) + double2 * (double2 < 0.0 ? arg2 : arg5) >= -double3
            && double4 * (double4 < 0.0 ? arg0 : arg3) + double5 * (double5 < 0.0 ? arg1 : arg4) + double6 * (double6 < 0.0 ? arg2 : arg5) >= -double7
            && double8 * (double8 < 0.0 ? arg0 : arg3) + double9 * (double9 < 0.0 ? arg1 : arg4) + double10 * (double10 < 0.0 ? arg2 : arg5) >= -double11
            && double12 * (double12 < 0.0 ? arg0 : arg3) + double13 * (double13 < 0.0 ? arg1 : arg4) + double14 * (double14 < 0.0 ? arg2 : arg5) >= -double15
            && double16 * (double16 < 0.0 ? arg0 : arg3) + double17 * (double17 < 0.0 ? arg1 : arg4) + double18 * (double18 < 0.0 ? arg2 : arg5) >= -double19
            && double20 * (double20 < 0.0 ? arg0 : arg3) + double21 * (double21 < 0.0 ? arg1 : arg4) + double22 * (double22 < 0.0 ? arg2 : arg5) >= -double23;
    }

    public Matrix4d obliqueZ(double arg0, double arg1) {
        this.m20 = this.m00 * arg0 + this.m10 * arg1 + this.m20;
        this.m21 = this.m01 * arg0 + this.m11 * arg1 + this.m21;
        this.m22 = this.m02 * arg0 + this.m12 * arg1 + this.m22;
        this.properties &= 2;
        return this;
    }

    @Override
    public Matrix4d obliqueZ(double arg0, double arg1, Matrix4d arg2) {
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

    public static void projViewFromRectangle(
        Vector3d arg0, Vector3d arg1, Vector3d arg2, Vector3d arg3, double arg4, boolean arg5, Matrix4d arg6, Matrix4d arg7
    ) {
        double double0 = arg3.y * arg2.z - arg3.z * arg2.y;
        double double1 = arg3.z * arg2.x - arg3.x * arg2.z;
        double double2 = arg3.x * arg2.y - arg3.y * arg2.x;
        double double3 = double0 * (arg1.x - arg0.x) + double1 * (arg1.y - arg0.y) + double2 * (arg1.z - arg0.z);
        double double4 = double3 >= 0.0 ? 1.0 : -1.0;
        double0 *= double4;
        double1 *= double4;
        double2 *= double4;
        double3 *= double4;
        arg7.setLookAt(arg0.x, arg0.y, arg0.z, arg0.x + double0, arg0.y + double1, arg0.z + double2, arg3.x, arg3.y, arg3.z);
        double double5 = arg7.m00 * arg1.x + arg7.m10 * arg1.y + arg7.m20 * arg1.z + arg7.m30;
        double double6 = arg7.m01 * arg1.x + arg7.m11 * arg1.y + arg7.m21 * arg1.z + arg7.m31;
        double double7 = arg7.m00 * arg2.x + arg7.m10 * arg2.y + arg7.m20 * arg2.z;
        double double8 = arg7.m01 * arg3.x + arg7.m11 * arg3.y + arg7.m21 * arg3.z;
        double double9 = Math.sqrt(double0 * double0 + double1 * double1 + double2 * double2);
        double double10 = double3 / double9;
        double double11;
        if (Double.isInfinite(arg4) && arg4 < 0.0) {
            double11 = double10;
            double10 = Double.POSITIVE_INFINITY;
        } else if (Double.isInfinite(arg4) && arg4 > 0.0) {
            double11 = Double.POSITIVE_INFINITY;
        } else if (arg4 < 0.0) {
            double11 = double10;
            double10 += arg4;
        } else {
            double11 = double10 + arg4;
        }

        arg6.setFrustum(double5, double5 + double7, double6, double6 + double8, double10, double11, arg5);
    }

    public Matrix4d withLookAtUp(Vector3dc arg0) {
        return this.withLookAtUp(arg0.x(), arg0.y(), arg0.z(), this);
    }

    @Override
    public Matrix4d withLookAtUp(Vector3dc arg0, Matrix4d arg1) {
        return this.withLookAtUp(arg0.x(), arg0.y(), arg0.z());
    }

    public Matrix4d withLookAtUp(double arg0, double arg1, double arg2) {
        return this.withLookAtUp(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4d withLookAtUp(double arg0, double arg1, double arg2, Matrix4d arg3) {
        double double0 = (arg1 * this.m21 - arg2 * this.m11) * this.m02
            + (arg2 * this.m01 - arg0 * this.m21) * this.m12
            + (arg0 * this.m11 - arg1 * this.m01) * this.m22;
        double double1 = arg0 * this.m01 + arg1 * this.m11 + arg2 * this.m21;
        if ((this.properties & 16) == 0) {
            double1 *= Math.sqrt(this.m01 * this.m01 + this.m11 * this.m11 + this.m21 * this.m21);
        }

        double double2 = Math.invsqrt(double0 * double0 + double1 * double1);
        double double3 = double1 * double2;
        double double4 = double0 * double2;
        double double5 = double3 * this.m00 - double4 * this.m01;
        double double6 = double3 * this.m10 - double4 * this.m11;
        double double7 = double3 * this.m20 - double4 * this.m21;
        double double8 = double4 * this.m30 + double3 * this.m31;
        double double9 = double4 * this.m00 + double3 * this.m01;
        double double10 = double4 * this.m10 + double3 * this.m11;
        double double11 = double4 * this.m20 + double3 * this.m21;
        double double12 = double3 * this.m30 - double4 * this.m31;
        arg3._m00(double5)._m10(double6)._m20(double7)._m30(double12)._m01(double9)._m11(double10)._m21(double11)._m31(double8);
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
