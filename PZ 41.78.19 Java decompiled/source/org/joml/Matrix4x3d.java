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

public class Matrix4x3d implements Externalizable, Matrix4x3dc {
    private static final long serialVersionUID = 1L;
    double m00;
    double m01;
    double m02;
    double m10;
    double m11;
    double m12;
    double m20;
    double m21;
    double m22;
    double m30;
    double m31;
    double m32;
    int properties;

    public Matrix4x3d() {
        this.m00 = 1.0;
        this.m11 = 1.0;
        this.m22 = 1.0;
        this.properties = 28;
    }

    public Matrix4x3d(Matrix4x3dc arg0) {
        this.set(arg0);
    }

    public Matrix4x3d(Matrix4x3fc arg0) {
        this.set(arg0);
    }

    public Matrix4x3d(Matrix3dc arg0) {
        this.set(arg0);
    }

    public Matrix4x3d(Matrix3fc arg0) {
        this.set(arg0);
    }

    public Matrix4x3d(
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
        double arg11
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

    public Matrix4x3d(DoubleBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        this.determineProperties();
    }

    public Matrix4x3d assume(int arg0) {
        this.properties = arg0;
        return this;
    }

    public Matrix4x3d determineProperties() {
        byte byte0 = 0;
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

    Matrix4x3d _properties(int int0) {
        this.properties = int0;
        return this;
    }

    Matrix4x3d _m00(double double0) {
        this.m00 = double0;
        return this;
    }

    Matrix4x3d _m01(double double0) {
        this.m01 = double0;
        return this;
    }

    Matrix4x3d _m02(double double0) {
        this.m02 = double0;
        return this;
    }

    Matrix4x3d _m10(double double0) {
        this.m10 = double0;
        return this;
    }

    Matrix4x3d _m11(double double0) {
        this.m11 = double0;
        return this;
    }

    Matrix4x3d _m12(double double0) {
        this.m12 = double0;
        return this;
    }

    Matrix4x3d _m20(double double0) {
        this.m20 = double0;
        return this;
    }

    Matrix4x3d _m21(double double0) {
        this.m21 = double0;
        return this;
    }

    Matrix4x3d _m22(double double0) {
        this.m22 = double0;
        return this;
    }

    Matrix4x3d _m30(double double0) {
        this.m30 = double0;
        return this;
    }

    Matrix4x3d _m31(double double0) {
        this.m31 = double0;
        return this;
    }

    Matrix4x3d _m32(double double0) {
        this.m32 = double0;
        return this;
    }

    public Matrix4x3d m00(double arg0) {
        this.m00 = arg0;
        this.properties &= -17;
        if (arg0 != 1.0) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4x3d m01(double arg0) {
        this.m01 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4x3d m02(double arg0) {
        this.m02 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4x3d m10(double arg0) {
        this.m10 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4x3d m11(double arg0) {
        this.m11 = arg0;
        this.properties &= -17;
        if (arg0 != 1.0) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4x3d m12(double arg0) {
        this.m12 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4x3d m20(double arg0) {
        this.m20 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4x3d m21(double arg0) {
        this.m21 = arg0;
        this.properties &= -17;
        if (arg0 != 0.0) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4x3d m22(double arg0) {
        this.m22 = arg0;
        this.properties &= -17;
        if (arg0 != 1.0) {
            this.properties &= -13;
        }

        return this;
    }

    public Matrix4x3d m30(double arg0) {
        this.m30 = arg0;
        if (arg0 != 0.0) {
            this.properties &= -5;
        }

        return this;
    }

    public Matrix4x3d m31(double arg0) {
        this.m31 = arg0;
        if (arg0 != 0.0) {
            this.properties &= -5;
        }

        return this;
    }

    public Matrix4x3d m32(double arg0) {
        this.m32 = arg0;
        if (arg0 != 0.0) {
            this.properties &= -5;
        }

        return this;
    }

    public Matrix4x3d identity() {
        if ((this.properties & 4) != 0) {
            return this;
        } else {
            this.m00 = 1.0;
            this.m01 = 0.0;
            this.m02 = 0.0;
            this.m10 = 0.0;
            this.m11 = 1.0;
            this.m12 = 0.0;
            this.m20 = 0.0;
            this.m21 = 0.0;
            this.m22 = 1.0;
            this.m30 = 0.0;
            this.m31 = 0.0;
            this.m32 = 0.0;
            this.properties = 28;
            return this;
        }
    }

    public Matrix4x3d set(Matrix4x3dc arg0) {
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

    public Matrix4x3d set(Matrix4x3fc arg0) {
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

    public Matrix4x3d set(Matrix4dc arg0) {
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
    public Matrix4d get(Matrix4d arg0) {
        return arg0.set4x3(this);
    }

    public Matrix4x3d set(Matrix3dc arg0) {
        this.m00 = arg0.m00();
        this.m01 = arg0.m01();
        this.m02 = arg0.m02();
        this.m10 = arg0.m10();
        this.m11 = arg0.m11();
        this.m12 = arg0.m12();
        this.m20 = arg0.m20();
        this.m21 = arg0.m21();
        this.m22 = arg0.m22();
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        return this.determineProperties();
    }

    public Matrix4x3d set(Matrix3fc arg0) {
        this.m00 = arg0.m00();
        this.m01 = arg0.m01();
        this.m02 = arg0.m02();
        this.m10 = arg0.m10();
        this.m11 = arg0.m11();
        this.m12 = arg0.m12();
        this.m20 = arg0.m20();
        this.m21 = arg0.m21();
        this.m22 = arg0.m22();
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        return this.determineProperties();
    }

    public Matrix4x3d set(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2, Vector3dc arg3) {
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

    public Matrix4x3d set3x3(Matrix4x3dc arg0) {
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

    public Matrix4x3d set(AxisAngle4f arg0) {
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
        this.m00 = double6 + double0 * double0 * double7;
        this.m11 = double6 + double1 * double1 * double7;
        this.m22 = double6 + double2 * double2 * double7;
        double double8 = double0 * double1 * double7;
        double double9 = double2 * double5;
        this.m10 = double8 - double9;
        this.m01 = double8 + double9;
        double8 = double0 * double2 * double7;
        double9 = double1 * double5;
        this.m20 = double8 + double9;
        this.m02 = double8 - double9;
        double8 = double1 * double2 * double7;
        double9 = double0 * double5;
        this.m21 = double8 - double9;
        this.m12 = double8 + double9;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.properties = 16;
        return this;
    }

    public Matrix4x3d set(AxisAngle4d arg0) {
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
        this.m00 = double6 + double0 * double0 * double7;
        this.m11 = double6 + double1 * double1 * double7;
        this.m22 = double6 + double2 * double2 * double7;
        double double8 = double0 * double1 * double7;
        double double9 = double2 * double5;
        this.m10 = double8 - double9;
        this.m01 = double8 + double9;
        double8 = double0 * double2 * double7;
        double9 = double1 * double5;
        this.m20 = double8 + double9;
        this.m02 = double8 - double9;
        double8 = double1 * double2 * double7;
        double9 = double0 * double5;
        this.m21 = double8 - double9;
        this.m12 = double8 + double9;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.properties = 16;
        return this;
    }

    public Matrix4x3d set(Quaternionfc arg0) {
        return this.rotation(arg0);
    }

    public Matrix4x3d set(Quaterniondc arg0) {
        return this.rotation(arg0);
    }

    public Matrix4x3d mul(Matrix4x3dc arg0) {
        return this.mul(arg0, this);
    }

    @Override
    public Matrix4x3d mul(Matrix4x3dc arg0, Matrix4x3d arg1) {
        if ((this.properties & 4) != 0) {
            return arg1.set(arg0);
        } else if ((arg0.properties() & 4) != 0) {
            return arg1.set(this);
        } else {
            return (this.properties & 8) != 0 ? this.mulTranslation(arg0, arg1) : this.mulGeneric(arg0, arg1);
        }
    }

    private Matrix4x3d mulGeneric(Matrix4x3dc matrix4x3dc, Matrix4x3d matrix4x3d1) {
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
        return matrix4x3d1._m00(Math.fma(double0, double9, Math.fma(double3, double10, double6 * double11)))
            ._m01(Math.fma(double1, double9, Math.fma(double4, double10, double7 * double11)))
            ._m02(Math.fma(double2, double9, Math.fma(double5, double10, double8 * double11)))
            ._m10(Math.fma(double0, double12, Math.fma(double3, double13, double6 * double14)))
            ._m11(Math.fma(double1, double12, Math.fma(double4, double13, double7 * double14)))
            ._m12(Math.fma(double2, double12, Math.fma(double5, double13, double8 * double14)))
            ._m20(Math.fma(double0, double15, Math.fma(double3, double16, double6 * double17)))
            ._m21(Math.fma(double1, double15, Math.fma(double4, double16, double7 * double17)))
            ._m22(Math.fma(double2, double15, Math.fma(double5, double16, double8 * double17)))
            ._m30(Math.fma(double0, double18, Math.fma(double3, double19, Math.fma(double6, double20, this.m30))))
            ._m31(Math.fma(double1, double18, Math.fma(double4, double19, Math.fma(double7, double20, this.m31))))
            ._m32(Math.fma(double2, double18, Math.fma(double5, double19, Math.fma(double8, double20, this.m32))))
            ._properties(this.properties & matrix4x3dc.properties() & 16);
    }

    public Matrix4x3d mul(Matrix4x3fc arg0) {
        return this.mul(arg0, this);
    }

    @Override
    public Matrix4x3d mul(Matrix4x3fc arg0, Matrix4x3d arg1) {
        if ((this.properties & 4) != 0) {
            return arg1.set(arg0);
        } else if ((arg0.properties() & 4) != 0) {
            return arg1.set(this);
        } else {
            return (this.properties & 8) != 0 ? this.mulTranslation(arg0, arg1) : this.mulGeneric(arg0, arg1);
        }
    }

    private Matrix4x3d mulGeneric(Matrix4x3fc matrix4x3fc, Matrix4x3d matrix4x3d1) {
        double double0 = this.m00;
        double double1 = this.m01;
        double double2 = this.m02;
        double double3 = this.m10;
        double double4 = this.m11;
        double double5 = this.m12;
        double double6 = this.m20;
        double double7 = this.m21;
        double double8 = this.m22;
        double double9 = matrix4x3fc.m00();
        double double10 = matrix4x3fc.m01();
        double double11 = matrix4x3fc.m02();
        double double12 = matrix4x3fc.m10();
        double double13 = matrix4x3fc.m11();
        double double14 = matrix4x3fc.m12();
        double double15 = matrix4x3fc.m20();
        double double16 = matrix4x3fc.m21();
        double double17 = matrix4x3fc.m22();
        double double18 = matrix4x3fc.m30();
        double double19 = matrix4x3fc.m31();
        double double20 = matrix4x3fc.m32();
        return matrix4x3d1._m00(Math.fma(double0, double9, Math.fma(double3, double10, double6 * double11)))
            ._m01(Math.fma(double1, double9, Math.fma(double4, double10, double7 * double11)))
            ._m02(Math.fma(double2, double9, Math.fma(double5, double10, double8 * double11)))
            ._m10(Math.fma(double0, double12, Math.fma(double3, double13, double6 * double14)))
            ._m11(Math.fma(double1, double12, Math.fma(double4, double13, double7 * double14)))
            ._m12(Math.fma(double2, double12, Math.fma(double5, double13, double8 * double14)))
            ._m20(Math.fma(double0, double15, Math.fma(double3, double16, double6 * double17)))
            ._m21(Math.fma(double1, double15, Math.fma(double4, double16, double7 * double17)))
            ._m22(Math.fma(double2, double15, Math.fma(double5, double16, double8 * double17)))
            ._m30(Math.fma(double0, double18, Math.fma(double3, double19, Math.fma(double6, double20, this.m30))))
            ._m31(Math.fma(double1, double18, Math.fma(double4, double19, Math.fma(double7, double20, this.m31))))
            ._m32(Math.fma(double2, double18, Math.fma(double5, double19, Math.fma(double8, double20, this.m32))))
            ._properties(this.properties & matrix4x3fc.properties() & 16);
    }

    @Override
    public Matrix4x3d mulTranslation(Matrix4x3dc arg0, Matrix4x3d arg1) {
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

    @Override
    public Matrix4x3d mulTranslation(Matrix4x3fc arg0, Matrix4x3d arg1) {
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

    public Matrix4x3d mulOrtho(Matrix4x3dc arg0) {
        return this.mulOrtho(arg0, this);
    }

    @Override
    public Matrix4x3d mulOrtho(Matrix4x3dc arg0, Matrix4x3d arg1) {
        double double0 = this.m00 * arg0.m00();
        double double1 = this.m11 * arg0.m01();
        double double2 = this.m22 * arg0.m02();
        double double3 = this.m00 * arg0.m10();
        double double4 = this.m11 * arg0.m11();
        double double5 = this.m22 * arg0.m12();
        double double6 = this.m00 * arg0.m20();
        double double7 = this.m11 * arg0.m21();
        double double8 = this.m22 * arg0.m22();
        double double9 = this.m00 * arg0.m30() + this.m30;
        double double10 = this.m11 * arg0.m31() + this.m31;
        double double11 = this.m22 * arg0.m32() + this.m32;
        arg1.m00 = double0;
        arg1.m01 = double1;
        arg1.m02 = double2;
        arg1.m10 = double3;
        arg1.m11 = double4;
        arg1.m12 = double5;
        arg1.m20 = double6;
        arg1.m21 = double7;
        arg1.m22 = double8;
        arg1.m30 = double9;
        arg1.m31 = double10;
        arg1.m32 = double11;
        arg1.properties = this.properties & arg0.properties() & 16;
        return arg1;
    }

    public Matrix4x3d fma(Matrix4x3dc arg0, double arg1) {
        return this.fma(arg0, arg1, this);
    }

    @Override
    public Matrix4x3d fma(Matrix4x3dc arg0, double arg1, Matrix4x3d arg2) {
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

    public Matrix4x3d fma(Matrix4x3fc arg0, double arg1) {
        return this.fma(arg0, arg1, this);
    }

    @Override
    public Matrix4x3d fma(Matrix4x3fc arg0, double arg1, Matrix4x3d arg2) {
        arg2._m00(Math.fma((double)arg0.m00(), arg1, this.m00))
            ._m01(Math.fma((double)arg0.m01(), arg1, this.m01))
            ._m02(Math.fma((double)arg0.m02(), arg1, this.m02))
            ._m10(Math.fma((double)arg0.m10(), arg1, this.m10))
            ._m11(Math.fma((double)arg0.m11(), arg1, this.m11))
            ._m12(Math.fma((double)arg0.m12(), arg1, this.m12))
            ._m20(Math.fma((double)arg0.m20(), arg1, this.m20))
            ._m21(Math.fma((double)arg0.m21(), arg1, this.m21))
            ._m22(Math.fma((double)arg0.m22(), arg1, this.m22))
            ._m30(Math.fma((double)arg0.m30(), arg1, this.m30))
            ._m31(Math.fma((double)arg0.m31(), arg1, this.m31))
            ._m32(Math.fma((double)arg0.m32(), arg1, this.m32))
            ._properties(0);
        return arg2;
    }

    public Matrix4x3d add(Matrix4x3dc arg0) {
        return this.add(arg0, this);
    }

    @Override
    public Matrix4x3d add(Matrix4x3dc arg0, Matrix4x3d arg1) {
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

    public Matrix4x3d add(Matrix4x3fc arg0) {
        return this.add(arg0, this);
    }

    @Override
    public Matrix4x3d add(Matrix4x3fc arg0, Matrix4x3d arg1) {
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

    public Matrix4x3d sub(Matrix4x3dc arg0) {
        return this.sub(arg0, this);
    }

    @Override
    public Matrix4x3d sub(Matrix4x3dc arg0, Matrix4x3d arg1) {
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

    public Matrix4x3d sub(Matrix4x3fc arg0) {
        return this.sub(arg0, this);
    }

    @Override
    public Matrix4x3d sub(Matrix4x3fc arg0, Matrix4x3d arg1) {
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

    public Matrix4x3d mulComponentWise(Matrix4x3dc arg0) {
        return this.mulComponentWise(arg0, this);
    }

    @Override
    public Matrix4x3d mulComponentWise(Matrix4x3dc arg0, Matrix4x3d arg1) {
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

    public Matrix4x3d set(
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
        double arg11
    ) {
        this.m00 = arg0;
        this.m10 = arg3;
        this.m20 = arg6;
        this.m30 = arg9;
        this.m01 = arg1;
        this.m11 = arg4;
        this.m21 = arg7;
        this.m31 = arg10;
        this.m02 = arg2;
        this.m12 = arg5;
        this.m22 = arg8;
        this.m32 = arg11;
        return this.determineProperties();
    }

    public Matrix4x3d set(double[] doubles, int int0) {
        this.m00 = doubles[int0 + 0];
        this.m01 = doubles[int0 + 1];
        this.m02 = doubles[int0 + 2];
        this.m10 = doubles[int0 + 3];
        this.m11 = doubles[int0 + 4];
        this.m12 = doubles[int0 + 5];
        this.m20 = doubles[int0 + 6];
        this.m21 = doubles[int0 + 7];
        this.m22 = doubles[int0 + 8];
        this.m30 = doubles[int0 + 9];
        this.m31 = doubles[int0 + 10];
        this.m32 = doubles[int0 + 11];
        return this.determineProperties();
    }

    public Matrix4x3d set(double[] doubles) {
        return this.set(doubles, 0);
    }

    public Matrix4x3d set(float[] floats, int int0) {
        this.m00 = floats[int0 + 0];
        this.m01 = floats[int0 + 1];
        this.m02 = floats[int0 + 2];
        this.m10 = floats[int0 + 3];
        this.m11 = floats[int0 + 4];
        this.m12 = floats[int0 + 5];
        this.m20 = floats[int0 + 6];
        this.m21 = floats[int0 + 7];
        this.m22 = floats[int0 + 8];
        this.m30 = floats[int0 + 9];
        this.m31 = floats[int0 + 10];
        this.m32 = floats[int0 + 11];
        return this.determineProperties();
    }

    public Matrix4x3d set(float[] floats) {
        return this.set(floats, 0);
    }

    public Matrix4x3d set(DoubleBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this.determineProperties();
    }

    public Matrix4x3d set(FloatBuffer arg0) {
        MemUtil.INSTANCE.getf(this, arg0.position(), arg0);
        return this.determineProperties();
    }

    public Matrix4x3d set(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this.determineProperties();
    }

    public Matrix4x3d setFloats(ByteBuffer arg0) {
        MemUtil.INSTANCE.getf(this, arg0.position(), arg0);
        return this.determineProperties();
    }

    public Matrix4x3d setFromAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.get(this, arg0);
            return this.determineProperties();
        }
    }

    @Override
    public double determinant() {
        return (this.m00 * this.m11 - this.m01 * this.m10) * this.m22
            + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21
            + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
    }

    public Matrix4x3d invert() {
        return this.invert(this);
    }

    @Override
    public Matrix4x3d invert(Matrix4x3d arg0) {
        if ((this.properties & 4) != 0) {
            return arg0.identity();
        } else {
            return (this.properties & 16) != 0 ? this.invertOrthonormal(arg0) : this.invertGeneric(arg0);
        }
    }

    private Matrix4x3d invertGeneric(Matrix4x3d matrix4x3d1) {
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
        matrix4x3d1.m00 = double19;
        matrix4x3d1.m01 = double20;
        matrix4x3d1.m02 = double21;
        matrix4x3d1.m10 = double22;
        matrix4x3d1.m11 = double23;
        matrix4x3d1.m12 = double24;
        matrix4x3d1.m20 = double25;
        matrix4x3d1.m21 = double26;
        matrix4x3d1.m22 = double27;
        matrix4x3d1.m30 = double28;
        matrix4x3d1.m31 = double29;
        matrix4x3d1.m32 = double30;
        matrix4x3d1.properties = 0;
        return matrix4x3d1;
    }

    private Matrix4x3d invertOrthonormal(Matrix4x3d matrix4x3d1) {
        double double0 = -(this.m00 * this.m30 + this.m01 * this.m31 + this.m02 * this.m32);
        double double1 = -(this.m10 * this.m30 + this.m11 * this.m31 + this.m12 * this.m32);
        double double2 = -(this.m20 * this.m30 + this.m21 * this.m31 + this.m22 * this.m32);
        double double3 = this.m01;
        double double4 = this.m02;
        double double5 = this.m12;
        matrix4x3d1.m00 = this.m00;
        matrix4x3d1.m01 = this.m10;
        matrix4x3d1.m02 = this.m20;
        matrix4x3d1.m10 = double3;
        matrix4x3d1.m11 = this.m11;
        matrix4x3d1.m12 = this.m21;
        matrix4x3d1.m20 = double4;
        matrix4x3d1.m21 = double5;
        matrix4x3d1.m22 = this.m22;
        matrix4x3d1.m30 = double0;
        matrix4x3d1.m31 = double1;
        matrix4x3d1.m32 = double2;
        matrix4x3d1.properties = 16;
        return matrix4x3d1;
    }

    @Override
    public Matrix4x3d invertOrtho(Matrix4x3d arg0) {
        double double0 = 1.0 / this.m00;
        double double1 = 1.0 / this.m11;
        double double2 = 1.0 / this.m22;
        arg0.set(double0, 0.0, 0.0, 0.0, double1, 0.0, 0.0, 0.0, double2, -this.m30 * double0, -this.m31 * double1, -this.m32 * double2);
        arg0.properties = 0;
        return arg0;
    }

    public Matrix4x3d invertOrtho() {
        return this.invertOrtho(this);
    }

    public Matrix4x3d transpose3x3() {
        return this.transpose3x3(this);
    }

    @Override
    public Matrix4x3d transpose3x3(Matrix4x3d arg0) {
        double double0 = this.m00;
        double double1 = this.m10;
        double double2 = this.m20;
        double double3 = this.m01;
        double double4 = this.m11;
        double double5 = this.m21;
        double double6 = this.m02;
        double double7 = this.m12;
        double double8 = this.m22;
        arg0.m00 = double0;
        arg0.m01 = double1;
        arg0.m02 = double2;
        arg0.m10 = double3;
        arg0.m11 = double4;
        arg0.m12 = double5;
        arg0.m20 = double6;
        arg0.m21 = double7;
        arg0.m22 = double8;
        arg0.properties = this.properties;
        return arg0;
    }

    @Override
    public Matrix3d transpose3x3(Matrix3d arg0) {
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

    public Matrix4x3d translation(double arg0, double arg1, double arg2) {
        if ((this.properties & 4) == 0) {
            this.identity();
        }

        this.m30 = arg0;
        this.m31 = arg1;
        this.m32 = arg2;
        this.properties = 24;
        return this;
    }

    public Matrix4x3d translation(Vector3fc arg0) {
        return this.translation(arg0.x(), arg0.y(), arg0.z());
    }

    public Matrix4x3d translation(Vector3dc arg0) {
        return this.translation(arg0.x(), arg0.y(), arg0.z());
    }

    public Matrix4x3d setTranslation(double arg0, double arg1, double arg2) {
        this.m30 = arg0;
        this.m31 = arg1;
        this.m32 = arg2;
        this.properties &= -5;
        return this;
    }

    public Matrix4x3d setTranslation(Vector3dc arg0) {
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
            + "\n";
    }

    @Override
    public Matrix4x3d get(Matrix4x3d arg0) {
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
        return this.get(arg0.position(), arg0);
    }

    @Override
    public DoubleBuffer get(int arg0, DoubleBuffer arg1) {
        MemUtil.INSTANCE.put(this, arg0, arg1);
        return arg1;
    }

    @Override
    public FloatBuffer get(FloatBuffer arg0) {
        return this.get(arg0.position(), arg0);
    }

    @Override
    public FloatBuffer get(int arg0, FloatBuffer arg1) {
        MemUtil.INSTANCE.putf(this, arg0, arg1);
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
    public ByteBuffer getFloats(ByteBuffer arg0) {
        return this.getFloats(arg0.position(), arg0);
    }

    @Override
    public ByteBuffer getFloats(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.putf(this, arg0, arg1);
        return arg1;
    }

    @Override
    public Matrix4x3dc getToAddress(long arg0) {
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
        doubles[int0 + 3] = this.m10;
        doubles[int0 + 4] = this.m11;
        doubles[int0 + 5] = this.m12;
        doubles[int0 + 6] = this.m20;
        doubles[int0 + 7] = this.m21;
        doubles[int0 + 8] = this.m22;
        doubles[int0 + 9] = this.m30;
        doubles[int0 + 10] = this.m31;
        doubles[int0 + 11] = this.m32;
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
        floats[int0 + 3] = (float)this.m10;
        floats[int0 + 4] = (float)this.m11;
        floats[int0 + 5] = (float)this.m12;
        floats[int0 + 6] = (float)this.m20;
        floats[int0 + 7] = (float)this.m21;
        floats[int0 + 8] = (float)this.m22;
        floats[int0 + 9] = (float)this.m30;
        floats[int0 + 10] = (float)this.m31;
        floats[int0 + 11] = (float)this.m32;
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
    public double[] get4x4(double[] doubles, int int0) {
        MemUtil.INSTANCE.copy4x4(this, doubles, int0);
        return doubles;
    }

    @Override
    public double[] get4x4(double[] doubles) {
        return this.get4x4(doubles, 0);
    }

    @Override
    public DoubleBuffer get4x4(DoubleBuffer arg0) {
        return this.get4x4(arg0.position(), arg0);
    }

    @Override
    public DoubleBuffer get4x4(int arg0, DoubleBuffer arg1) {
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
    public DoubleBuffer getTransposed(DoubleBuffer arg0) {
        return this.getTransposed(arg0.position(), arg0);
    }

    @Override
    public DoubleBuffer getTransposed(int arg0, DoubleBuffer arg1) {
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
    public FloatBuffer getTransposed(FloatBuffer arg0) {
        return this.getTransposed(arg0.position(), arg0);
    }

    @Override
    public FloatBuffer getTransposed(int arg0, FloatBuffer arg1) {
        MemUtil.INSTANCE.putfTransposed(this, arg0, arg1);
        return arg1;
    }

    @Override
    public ByteBuffer getTransposedFloats(ByteBuffer arg0) {
        return this.getTransposed(arg0.position(), arg0);
    }

    @Override
    public ByteBuffer getTransposedFloats(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.putfTransposed(this, arg0, arg1);
        return arg1;
    }

    @Override
    public double[] getTransposed(double[] doubles, int int0) {
        doubles[int0 + 0] = this.m00;
        doubles[int0 + 1] = this.m10;
        doubles[int0 + 2] = this.m20;
        doubles[int0 + 3] = this.m30;
        doubles[int0 + 4] = this.m01;
        doubles[int0 + 5] = this.m11;
        doubles[int0 + 6] = this.m21;
        doubles[int0 + 7] = this.m31;
        doubles[int0 + 8] = this.m02;
        doubles[int0 + 9] = this.m12;
        doubles[int0 + 10] = this.m22;
        doubles[int0 + 11] = this.m32;
        return doubles;
    }

    @Override
    public double[] getTransposed(double[] doubles) {
        return this.getTransposed(doubles, 0);
    }

    public Matrix4x3d zero() {
        this.m00 = 0.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = 0.0;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 0.0;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.properties = 0;
        return this;
    }

    public Matrix4x3d scaling(double arg0) {
        return this.scaling(arg0, arg0, arg0);
    }

    public Matrix4x3d scaling(double arg0, double arg1, double arg2) {
        if ((this.properties & 4) == 0) {
            this.identity();
        }

        this.m00 = arg0;
        this.m11 = arg1;
        this.m22 = arg2;
        boolean boolean0 = Math.absEqualsOne(arg0) && Math.absEqualsOne(arg1) && Math.absEqualsOne(arg2);
        this.properties = boolean0 ? 16 : 0;
        return this;
    }

    public Matrix4x3d scaling(Vector3dc arg0) {
        return this.scaling(arg0.x(), arg0.y(), arg0.z());
    }

    public Matrix4x3d rotation(double arg0, double arg1, double arg2, double arg3) {
        if (arg2 == 0.0 && arg3 == 0.0 && Math.absEqualsOne(arg1)) {
            return this.rotationX(arg1 * arg0);
        } else if (arg1 == 0.0 && arg3 == 0.0 && Math.absEqualsOne(arg2)) {
            return this.rotationY(arg2 * arg0);
        } else {
            return arg1 == 0.0 && arg2 == 0.0 && Math.absEqualsOne(arg3) ? this.rotationZ(arg3 * arg0) : this.rotationInternal(arg0, arg1, arg2, arg3);
        }
    }

    private Matrix4x3d rotationInternal(double double1, double double5, double double6, double double8) {
        double double0 = Math.sin(double1);
        double double2 = Math.cosFromSin(double0, double1);
        double double3 = 1.0 - double2;
        double double4 = double5 * double6;
        double double7 = double5 * double8;
        double double9 = double6 * double8;
        this.m00 = double2 + double5 * double5 * double3;
        this.m01 = double4 * double3 + double8 * double0;
        this.m02 = double7 * double3 - double6 * double0;
        this.m10 = double4 * double3 - double8 * double0;
        this.m11 = double2 + double6 * double6 * double3;
        this.m12 = double9 * double3 + double5 * double0;
        this.m20 = double7 * double3 + double6 * double0;
        this.m21 = double9 * double3 - double5 * double0;
        this.m22 = double2 + double8 * double8 * double3;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.properties = 16;
        return this;
    }

    public Matrix4x3d rotationX(double arg0) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        this.m00 = 1.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = double1;
        this.m12 = double0;
        this.m20 = 0.0;
        this.m21 = -double0;
        this.m22 = double1;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.properties = 16;
        return this;
    }

    public Matrix4x3d rotationY(double arg0) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        this.m00 = double1;
        this.m01 = 0.0;
        this.m02 = -double0;
        this.m10 = 0.0;
        this.m11 = 1.0;
        this.m12 = 0.0;
        this.m20 = double0;
        this.m21 = 0.0;
        this.m22 = double1;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.properties = 16;
        return this;
    }

    public Matrix4x3d rotationZ(double arg0) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        this.m00 = double1;
        this.m01 = double0;
        this.m02 = 0.0;
        this.m10 = -double0;
        this.m11 = double1;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.properties = 16;
        return this;
    }

    public Matrix4x3d rotationXYZ(double arg0, double arg1, double arg2) {
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
        this.m20 = double2;
        this.m21 = double6 * double3;
        this.m22 = double1 * double3;
        this.m00 = double3 * double5;
        this.m01 = double9 * double5 + double1 * double4;
        this.m02 = double10 * double5 + double0 * double4;
        this.m10 = double3 * double8;
        this.m11 = double9 * double8 + double1 * double5;
        this.m12 = double10 * double8 + double0 * double5;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.properties = 16;
        return this;
    }

    public Matrix4x3d rotationZYX(double arg0, double arg1, double arg2) {
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
        this.m00 = double5 * double3;
        this.m01 = double4 * double3;
        this.m02 = double7;
        this.m10 = double6 * double1 + double9 * double0;
        this.m11 = double5 * double1 + double10 * double0;
        this.m12 = double3 * double0;
        this.m20 = double6 * double8 + double9 * double1;
        this.m21 = double5 * double8 + double10 * double1;
        this.m22 = double3 * double1;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.properties = 16;
        return this;
    }

    public Matrix4x3d rotationYXZ(double arg0, double arg1, double arg2) {
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
        this.m20 = double2 * double1;
        this.m21 = double7;
        this.m22 = double3 * double1;
        this.m00 = double3 * double5 + double9 * double4;
        this.m01 = double1 * double4;
        this.m02 = double6 * double5 + double10 * double4;
        this.m10 = double3 * double8 + double9 * double5;
        this.m11 = double1 * double5;
        this.m12 = double6 * double8 + double10 * double5;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.properties = 16;
        return this;
    }

    public Matrix4x3d setRotationXYZ(double arg0, double arg1, double arg2) {
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
        this.m20 = double2;
        this.m21 = double6 * double3;
        this.m22 = double1 * double3;
        this.m00 = double3 * double5;
        this.m01 = double9 * double5 + double1 * double4;
        this.m02 = double10 * double5 + double0 * double4;
        this.m10 = double3 * double8;
        this.m11 = double9 * double8 + double1 * double5;
        this.m12 = double10 * double8 + double0 * double5;
        this.properties &= -13;
        return this;
    }

    public Matrix4x3d setRotationZYX(double arg0, double arg1, double arg2) {
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
        this.m00 = double5 * double3;
        this.m01 = double4 * double3;
        this.m02 = double7;
        this.m10 = double6 * double1 + double9 * double0;
        this.m11 = double5 * double1 + double10 * double0;
        this.m12 = double3 * double0;
        this.m20 = double6 * double8 + double9 * double1;
        this.m21 = double5 * double8 + double10 * double1;
        this.m22 = double3 * double1;
        this.properties &= -13;
        return this;
    }

    public Matrix4x3d setRotationYXZ(double arg0, double arg1, double arg2) {
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
        this.m20 = double2 * double1;
        this.m21 = double7;
        this.m22 = double3 * double1;
        this.m00 = double3 * double5 + double9 * double4;
        this.m01 = double1 * double4;
        this.m02 = double6 * double5 + double10 * double4;
        this.m10 = double3 * double8 + double9 * double5;
        this.m11 = double1 * double5;
        this.m12 = double6 * double8 + double10 * double5;
        this.properties &= -13;
        return this;
    }

    public Matrix4x3d rotation(double arg0, Vector3dc arg1) {
        return this.rotation(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix4x3d rotation(double arg0, Vector3fc arg1) {
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
    public Vector3d transformPosition(Vector3d arg0) {
        arg0.set(
            this.m00 * arg0.x + this.m10 * arg0.y + this.m20 * arg0.z + this.m30,
            this.m01 * arg0.x + this.m11 * arg0.y + this.m21 * arg0.z + this.m31,
            this.m02 * arg0.x + this.m12 * arg0.y + this.m22 * arg0.z + this.m32
        );
        return arg0;
    }

    @Override
    public Vector3d transformPosition(Vector3dc arg0, Vector3d arg1) {
        arg1.set(
            this.m00 * arg0.x() + this.m10 * arg0.y() + this.m20 * arg0.z() + this.m30,
            this.m01 * arg0.x() + this.m11 * arg0.y() + this.m21 * arg0.z() + this.m31,
            this.m02 * arg0.x() + this.m12 * arg0.y() + this.m22 * arg0.z() + this.m32
        );
        return arg1;
    }

    @Override
    public Vector3d transformDirection(Vector3d arg0) {
        arg0.set(
            this.m00 * arg0.x + this.m10 * arg0.y + this.m20 * arg0.z,
            this.m01 * arg0.x + this.m11 * arg0.y + this.m21 * arg0.z,
            this.m02 * arg0.x + this.m12 * arg0.y + this.m22 * arg0.z
        );
        return arg0;
    }

    @Override
    public Vector3d transformDirection(Vector3dc arg0, Vector3d arg1) {
        arg1.set(
            this.m00 * arg0.x() + this.m10 * arg0.y() + this.m20 * arg0.z(),
            this.m01 * arg0.x() + this.m11 * arg0.y() + this.m21 * arg0.z(),
            this.m02 * arg0.x() + this.m12 * arg0.y() + this.m22 * arg0.z()
        );
        return arg1;
    }

    public Matrix4x3d set3x3(Matrix3dc arg0) {
        this.m00 = arg0.m00();
        this.m01 = arg0.m01();
        this.m02 = arg0.m02();
        this.m10 = arg0.m10();
        this.m11 = arg0.m11();
        this.m12 = arg0.m12();
        this.m20 = arg0.m20();
        this.m21 = arg0.m21();
        this.m22 = arg0.m22();
        this.properties = 0;
        return this;
    }

    public Matrix4x3d set3x3(Matrix3fc arg0) {
        this.m00 = arg0.m00();
        this.m01 = arg0.m01();
        this.m02 = arg0.m02();
        this.m10 = arg0.m10();
        this.m11 = arg0.m11();
        this.m12 = arg0.m12();
        this.m20 = arg0.m20();
        this.m21 = arg0.m21();
        this.m22 = arg0.m22();
        this.properties = 0;
        return this;
    }

    @Override
    public Matrix4x3d scale(Vector3dc arg0, Matrix4x3d arg1) {
        return this.scale(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    public Matrix4x3d scale(Vector3dc arg0) {
        return this.scale(arg0.x(), arg0.y(), arg0.z(), this);
    }

    @Override
    public Matrix4x3d scale(double arg0, double arg1, double arg2, Matrix4x3d arg3) {
        return (this.properties & 4) != 0 ? arg3.scaling(arg0, arg1, arg2) : this.scaleGeneric(arg0, arg1, arg2, arg3);
    }

    private Matrix4x3d scaleGeneric(double double0, double double1, double double2, Matrix4x3d matrix4x3d1) {
        matrix4x3d1.m00 = this.m00 * double0;
        matrix4x3d1.m01 = this.m01 * double0;
        matrix4x3d1.m02 = this.m02 * double0;
        matrix4x3d1.m10 = this.m10 * double1;
        matrix4x3d1.m11 = this.m11 * double1;
        matrix4x3d1.m12 = this.m12 * double1;
        matrix4x3d1.m20 = this.m20 * double2;
        matrix4x3d1.m21 = this.m21 * double2;
        matrix4x3d1.m22 = this.m22 * double2;
        matrix4x3d1.m30 = this.m30;
        matrix4x3d1.m31 = this.m31;
        matrix4x3d1.m32 = this.m32;
        matrix4x3d1.properties = this.properties & -29;
        return matrix4x3d1;
    }

    public Matrix4x3d scale(double arg0, double arg1, double arg2) {
        return this.scale(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4x3d scale(double arg0, Matrix4x3d arg1) {
        return this.scale(arg0, arg0, arg0, arg1);
    }

    public Matrix4x3d scale(double arg0) {
        return this.scale(arg0, arg0, arg0);
    }

    @Override
    public Matrix4x3d scaleXY(double arg0, double arg1, Matrix4x3d arg2) {
        return this.scale(arg0, arg1, 1.0, arg2);
    }

    public Matrix4x3d scaleXY(double arg0, double arg1) {
        return this.scale(arg0, arg1, 1.0);
    }

    @Override
    public Matrix4x3d scaleLocal(double arg0, double arg1, double arg2, Matrix4x3d arg3) {
        if ((this.properties & 4) != 0) {
            return arg3.scaling(arg0, arg1, arg2);
        } else {
            double double0 = arg0 * this.m00;
            double double1 = arg1 * this.m01;
            double double2 = arg2 * this.m02;
            double double3 = arg0 * this.m10;
            double double4 = arg1 * this.m11;
            double double5 = arg2 * this.m12;
            double double6 = arg0 * this.m20;
            double double7 = arg1 * this.m21;
            double double8 = arg2 * this.m22;
            double double9 = arg0 * this.m30;
            double double10 = arg1 * this.m31;
            double double11 = arg2 * this.m32;
            arg3.m00 = double0;
            arg3.m01 = double1;
            arg3.m02 = double2;
            arg3.m10 = double3;
            arg3.m11 = double4;
            arg3.m12 = double5;
            arg3.m20 = double6;
            arg3.m21 = double7;
            arg3.m22 = double8;
            arg3.m30 = double9;
            arg3.m31 = double10;
            arg3.m32 = double11;
            arg3.properties = this.properties & -29;
            return arg3;
        }
    }

    public Matrix4x3d scaleLocal(double arg0, double arg1, double arg2) {
        return this.scaleLocal(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4x3d rotate(double arg0, double arg1, double arg2, double arg3, Matrix4x3d arg4) {
        if ((this.properties & 4) != 0) {
            return arg4.rotation(arg0, arg1, arg2, arg3);
        } else {
            return (this.properties & 8) != 0 ? this.rotateTranslation(arg0, arg1, arg2, arg3, arg4) : this.rotateGeneric(arg0, arg1, arg2, arg3, arg4);
        }
    }

    private Matrix4x3d rotateGeneric(double double3, double double0, double double2, double double1, Matrix4x3d matrix4x3d1) {
        if (double2 == 0.0 && double1 == 0.0 && Math.absEqualsOne(double0)) {
            return this.rotateX(double0 * double3, matrix4x3d1);
        } else if (double0 == 0.0 && double1 == 0.0 && Math.absEqualsOne(double2)) {
            return this.rotateY(double2 * double3, matrix4x3d1);
        } else {
            return double0 == 0.0 && double2 == 0.0 && Math.absEqualsOne(double1)
                ? this.rotateZ(double1 * double3, matrix4x3d1)
                : this.rotateGenericInternal(double3, double0, double2, double1, matrix4x3d1);
        }
    }

    private Matrix4x3d rotateGenericInternal(double double1, double double5, double double7, double double9, Matrix4x3d matrix4x3d1) {
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
        matrix4x3d1.m20 = this.m00 * double19 + this.m10 * double20 + this.m20 * double21;
        matrix4x3d1.m21 = this.m01 * double19 + this.m11 * double20 + this.m21 * double21;
        matrix4x3d1.m22 = this.m02 * double19 + this.m12 * double20 + this.m22 * double21;
        matrix4x3d1.m00 = double22;
        matrix4x3d1.m01 = double23;
        matrix4x3d1.m02 = double24;
        matrix4x3d1.m10 = double25;
        matrix4x3d1.m11 = double26;
        matrix4x3d1.m12 = double27;
        matrix4x3d1.m30 = this.m30;
        matrix4x3d1.m31 = this.m31;
        matrix4x3d1.m32 = this.m32;
        matrix4x3d1.properties = this.properties & -13;
        return matrix4x3d1;
    }

    public Matrix4x3d rotate(double arg0, double arg1, double arg2, double arg3) {
        return this.rotate(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix4x3d rotateTranslation(double arg0, double arg1, double arg2, double arg3, Matrix4x3d arg4) {
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

    private Matrix4x3d rotateTranslationInternal(double double1, double double5, double double7, double double9, Matrix4x3d matrix4x3d0) {
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
        matrix4x3d0.m20 = double19;
        matrix4x3d0.m21 = double20;
        matrix4x3d0.m22 = double21;
        matrix4x3d0.m00 = double13;
        matrix4x3d0.m01 = double14;
        matrix4x3d0.m02 = double15;
        matrix4x3d0.m10 = double16;
        matrix4x3d0.m11 = double17;
        matrix4x3d0.m12 = double18;
        matrix4x3d0.m30 = this.m30;
        matrix4x3d0.m31 = this.m31;
        matrix4x3d0.m32 = this.m32;
        matrix4x3d0.properties = this.properties & -13;
        return matrix4x3d0;
    }

    public Matrix4x3d rotateAround(Quaterniondc arg0, double arg1, double arg2, double arg3) {
        return this.rotateAround(arg0, arg1, arg2, arg3, this);
    }

    private Matrix4x3d rotateAroundAffine(Quaterniondc quaterniondc, double double28, double double27, double double26, Matrix4x3d matrix4x3d1) {
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
        double double19 = double7 - double5;
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
        double double34 = this.m00 * double19 + this.m10 * double20 + this.m20 * double21;
        double double35 = this.m01 * double19 + this.m11 * double20 + this.m21 * double21;
        double double36 = this.m02 * double19 + this.m12 * double20 + this.m22 * double21;
        matrix4x3d1._m20(this.m00 * double22 + this.m10 * double23 + this.m20 * double24)
            ._m21(this.m01 * double22 + this.m11 * double23 + this.m21 * double24)
            ._m22(this.m02 * double22 + this.m12 * double23 + this.m22 * double24)
            ._m00(double31)
            ._m01(double32)
            ._m02(double33)
            ._m10(double34)
            ._m11(double35)
            ._m12(double36)
            ._m30(-double31 * double28 - double34 * double27 - this.m20 * double26 + double25)
            ._m31(-double32 * double28 - double35 * double27 - this.m21 * double26 + double29)
            ._m32(-double33 * double28 - double36 * double27 - this.m22 * double26 + double30)
            ._properties(this.properties & -13);
        return matrix4x3d1;
    }

    @Override
    public Matrix4x3d rotateAround(Quaterniondc arg0, double arg1, double arg2, double arg3, Matrix4x3d arg4) {
        return (this.properties & 4) != 0 ? this.rotationAround(arg0, arg1, arg2, arg3) : this.rotateAroundAffine(arg0, arg1, arg2, arg3, arg4);
    }

    public Matrix4x3d rotationAround(Quaterniondc arg0, double arg1, double arg2, double arg3) {
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
        this._m00(double0 + double1 - double3 - double2);
        this._m01(double7 + double5);
        this._m02(double9 - double11);
        this._m10(double7 - double5);
        this._m11(double2 - double3 + double0 - double1);
        this._m12(double13 + double15);
        this._m30(-this.m00 * arg1 - this.m10 * arg2 - this.m20 * arg3 + arg1);
        this._m31(-this.m01 * arg1 - this.m11 * arg2 - this.m21 * arg3 + arg2);
        this._m32(-this.m02 * arg1 - this.m12 * arg2 - this.m22 * arg3 + arg3);
        this.properties = 16;
        return this;
    }

    @Override
    public Matrix4x3d rotateLocal(double arg0, double arg1, double arg2, double arg3, Matrix4x3d arg4) {
        if (arg2 == 0.0 && arg3 == 0.0 && Math.absEqualsOne(arg1)) {
            return this.rotateLocalX(arg1 * arg0, arg4);
        } else if (arg1 == 0.0 && arg3 == 0.0 && Math.absEqualsOne(arg2)) {
            return this.rotateLocalY(arg2 * arg0, arg4);
        } else {
            return arg1 == 0.0 && arg2 == 0.0 && Math.absEqualsOne(arg3)
                ? this.rotateLocalZ(arg3 * arg0, arg4)
                : this.rotateLocalInternal(arg0, arg1, arg2, arg3, arg4);
        }
    }

    private Matrix4x3d rotateLocalInternal(double double1, double double5, double double7, double double9, Matrix4x3d matrix4x3d1) {
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
        matrix4x3d1.m00 = double22;
        matrix4x3d1.m01 = double23;
        matrix4x3d1.m02 = double24;
        matrix4x3d1.m10 = double25;
        matrix4x3d1.m11 = double26;
        matrix4x3d1.m12 = double27;
        matrix4x3d1.m20 = double28;
        matrix4x3d1.m21 = double29;
        matrix4x3d1.m22 = double30;
        matrix4x3d1.m30 = double31;
        matrix4x3d1.m31 = double32;
        matrix4x3d1.m32 = double33;
        matrix4x3d1.properties = this.properties & -13;
        return matrix4x3d1;
    }

    public Matrix4x3d rotateLocal(double arg0, double arg1, double arg2, double arg3) {
        return this.rotateLocal(arg0, arg1, arg2, arg3, this);
    }

    public Matrix4x3d rotateLocalX(double arg0, Matrix4x3d arg1) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = double1 * this.m01 - double0 * this.m02;
        double double3 = double0 * this.m01 + double1 * this.m02;
        double double4 = double1 * this.m11 - double0 * this.m12;
        double double5 = double0 * this.m11 + double1 * this.m12;
        double double6 = double1 * this.m21 - double0 * this.m22;
        double double7 = double0 * this.m21 + double1 * this.m22;
        double double8 = double1 * this.m31 - double0 * this.m32;
        double double9 = double0 * this.m31 + double1 * this.m32;
        arg1.m00 = this.m00;
        arg1.m01 = double2;
        arg1.m02 = double3;
        arg1.m10 = this.m10;
        arg1.m11 = double4;
        arg1.m12 = double5;
        arg1.m20 = this.m20;
        arg1.m21 = double6;
        arg1.m22 = double7;
        arg1.m30 = this.m30;
        arg1.m31 = double8;
        arg1.m32 = double9;
        arg1.properties = this.properties & -13;
        return arg1;
    }

    public Matrix4x3d rotateLocalX(double arg0) {
        return this.rotateLocalX(arg0, this);
    }

    public Matrix4x3d rotateLocalY(double arg0, Matrix4x3d arg1) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = double1 * this.m00 + double0 * this.m02;
        double double3 = -double0 * this.m00 + double1 * this.m02;
        double double4 = double1 * this.m10 + double0 * this.m12;
        double double5 = -double0 * this.m10 + double1 * this.m12;
        double double6 = double1 * this.m20 + double0 * this.m22;
        double double7 = -double0 * this.m20 + double1 * this.m22;
        double double8 = double1 * this.m30 + double0 * this.m32;
        double double9 = -double0 * this.m30 + double1 * this.m32;
        arg1.m00 = double2;
        arg1.m01 = this.m01;
        arg1.m02 = double3;
        arg1.m10 = double4;
        arg1.m11 = this.m11;
        arg1.m12 = double5;
        arg1.m20 = double6;
        arg1.m21 = this.m21;
        arg1.m22 = double7;
        arg1.m30 = double8;
        arg1.m31 = this.m31;
        arg1.m32 = double9;
        arg1.properties = this.properties & -13;
        return arg1;
    }

    public Matrix4x3d rotateLocalY(double arg0) {
        return this.rotateLocalY(arg0, this);
    }

    public Matrix4x3d rotateLocalZ(double arg0, Matrix4x3d arg1) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = double1 * this.m00 - double0 * this.m01;
        double double3 = double0 * this.m00 + double1 * this.m01;
        double double4 = double1 * this.m10 - double0 * this.m11;
        double double5 = double0 * this.m10 + double1 * this.m11;
        double double6 = double1 * this.m20 - double0 * this.m21;
        double double7 = double0 * this.m20 + double1 * this.m21;
        double double8 = double1 * this.m30 - double0 * this.m31;
        double double9 = double0 * this.m30 + double1 * this.m31;
        arg1.m00 = double2;
        arg1.m01 = double3;
        arg1.m02 = this.m02;
        arg1.m10 = double4;
        arg1.m11 = double5;
        arg1.m12 = this.m12;
        arg1.m20 = double6;
        arg1.m21 = double7;
        arg1.m22 = this.m22;
        arg1.m30 = double8;
        arg1.m31 = double9;
        arg1.m32 = this.m32;
        arg1.properties = this.properties & -13;
        return arg1;
    }

    public Matrix4x3d rotateLocalZ(double arg0) {
        return this.rotateLocalZ(arg0, this);
    }

    public Matrix4x3d translate(Vector3dc arg0) {
        return this.translate(arg0.x(), arg0.y(), arg0.z());
    }

    @Override
    public Matrix4x3d translate(Vector3dc arg0, Matrix4x3d arg1) {
        return this.translate(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    public Matrix4x3d translate(Vector3fc arg0) {
        return this.translate(arg0.x(), arg0.y(), arg0.z());
    }

    @Override
    public Matrix4x3d translate(Vector3fc arg0, Matrix4x3d arg1) {
        return this.translate(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Matrix4x3d translate(double arg0, double arg1, double arg2, Matrix4x3d arg3) {
        return (this.properties & 4) != 0 ? arg3.translation(arg0, arg1, arg2) : this.translateGeneric(arg0, arg1, arg2, arg3);
    }

    private Matrix4x3d translateGeneric(double double2, double double1, double double0, Matrix4x3d matrix4x3d1) {
        matrix4x3d1.m00 = this.m00;
        matrix4x3d1.m01 = this.m01;
        matrix4x3d1.m02 = this.m02;
        matrix4x3d1.m10 = this.m10;
        matrix4x3d1.m11 = this.m11;
        matrix4x3d1.m12 = this.m12;
        matrix4x3d1.m20 = this.m20;
        matrix4x3d1.m21 = this.m21;
        matrix4x3d1.m22 = this.m22;
        matrix4x3d1.m30 = this.m00 * double2 + this.m10 * double1 + this.m20 * double0 + this.m30;
        matrix4x3d1.m31 = this.m01 * double2 + this.m11 * double1 + this.m21 * double0 + this.m31;
        matrix4x3d1.m32 = this.m02 * double2 + this.m12 * double1 + this.m22 * double0 + this.m32;
        matrix4x3d1.properties = this.properties & -5;
        return matrix4x3d1;
    }

    public Matrix4x3d translate(double arg0, double arg1, double arg2) {
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

    public Matrix4x3d translateLocal(Vector3fc arg0) {
        return this.translateLocal(arg0.x(), arg0.y(), arg0.z());
    }

    @Override
    public Matrix4x3d translateLocal(Vector3fc arg0, Matrix4x3d arg1) {
        return this.translateLocal(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    public Matrix4x3d translateLocal(Vector3dc arg0) {
        return this.translateLocal(arg0.x(), arg0.y(), arg0.z());
    }

    @Override
    public Matrix4x3d translateLocal(Vector3dc arg0, Matrix4x3d arg1) {
        return this.translateLocal(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    @Override
    public Matrix4x3d translateLocal(double arg0, double arg1, double arg2, Matrix4x3d arg3) {
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

    public Matrix4x3d translateLocal(double arg0, double arg1, double arg2) {
        return this.translateLocal(arg0, arg1, arg2, this);
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        arg0.writeDouble(this.m00);
        arg0.writeDouble(this.m01);
        arg0.writeDouble(this.m02);
        arg0.writeDouble(this.m10);
        arg0.writeDouble(this.m11);
        arg0.writeDouble(this.m12);
        arg0.writeDouble(this.m20);
        arg0.writeDouble(this.m21);
        arg0.writeDouble(this.m22);
        arg0.writeDouble(this.m30);
        arg0.writeDouble(this.m31);
        arg0.writeDouble(this.m32);
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException {
        this.m00 = arg0.readDouble();
        this.m01 = arg0.readDouble();
        this.m02 = arg0.readDouble();
        this.m10 = arg0.readDouble();
        this.m11 = arg0.readDouble();
        this.m12 = arg0.readDouble();
        this.m20 = arg0.readDouble();
        this.m21 = arg0.readDouble();
        this.m22 = arg0.readDouble();
        this.m30 = arg0.readDouble();
        this.m31 = arg0.readDouble();
        this.m32 = arg0.readDouble();
        this.determineProperties();
    }

    @Override
    public Matrix4x3d rotateX(double arg0, Matrix4x3d arg1) {
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

    private Matrix4x3d rotateXInternal(double double1, Matrix4x3d matrix4x3d1) {
        double double0 = Math.sin(double1);
        double double2 = Math.cosFromSin(double0, double1);
        double double3 = -double0;
        double double4 = this.m10 * double2 + this.m20 * double0;
        double double5 = this.m11 * double2 + this.m21 * double0;
        double double6 = this.m12 * double2 + this.m22 * double0;
        matrix4x3d1.m20 = this.m10 * double3 + this.m20 * double2;
        matrix4x3d1.m21 = this.m11 * double3 + this.m21 * double2;
        matrix4x3d1.m22 = this.m12 * double3 + this.m22 * double2;
        matrix4x3d1.m10 = double4;
        matrix4x3d1.m11 = double5;
        matrix4x3d1.m12 = double6;
        matrix4x3d1.m00 = this.m00;
        matrix4x3d1.m01 = this.m01;
        matrix4x3d1.m02 = this.m02;
        matrix4x3d1.m30 = this.m30;
        matrix4x3d1.m31 = this.m31;
        matrix4x3d1.m32 = this.m32;
        matrix4x3d1.properties = this.properties & -13;
        return matrix4x3d1;
    }

    public Matrix4x3d rotateX(double arg0) {
        return this.rotateX(arg0, this);
    }

    @Override
    public Matrix4x3d rotateY(double arg0, Matrix4x3d arg1) {
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

    private Matrix4x3d rotateYInternal(double double1, Matrix4x3d matrix4x3d1) {
        double double0 = Math.sin(double1);
        double double2 = Math.cosFromSin(double0, double1);
        double double3 = -double0;
        double double4 = this.m00 * double2 + this.m20 * double3;
        double double5 = this.m01 * double2 + this.m21 * double3;
        double double6 = this.m02 * double2 + this.m22 * double3;
        matrix4x3d1.m20 = this.m00 * double0 + this.m20 * double2;
        matrix4x3d1.m21 = this.m01 * double0 + this.m21 * double2;
        matrix4x3d1.m22 = this.m02 * double0 + this.m22 * double2;
        matrix4x3d1.m00 = double4;
        matrix4x3d1.m01 = double5;
        matrix4x3d1.m02 = double6;
        matrix4x3d1.m10 = this.m10;
        matrix4x3d1.m11 = this.m11;
        matrix4x3d1.m12 = this.m12;
        matrix4x3d1.m30 = this.m30;
        matrix4x3d1.m31 = this.m31;
        matrix4x3d1.m32 = this.m32;
        matrix4x3d1.properties = this.properties & -13;
        return matrix4x3d1;
    }

    public Matrix4x3d rotateY(double arg0) {
        return this.rotateY(arg0, this);
    }

    @Override
    public Matrix4x3d rotateZ(double arg0, Matrix4x3d arg1) {
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

    private Matrix4x3d rotateZInternal(double double1, Matrix4x3d matrix4x3d1) {
        double double0 = Math.sin(double1);
        double double2 = Math.cosFromSin(double0, double1);
        double double3 = -double0;
        double double4 = this.m00 * double2 + this.m10 * double0;
        double double5 = this.m01 * double2 + this.m11 * double0;
        double double6 = this.m02 * double2 + this.m12 * double0;
        matrix4x3d1.m10 = this.m00 * double3 + this.m10 * double2;
        matrix4x3d1.m11 = this.m01 * double3 + this.m11 * double2;
        matrix4x3d1.m12 = this.m02 * double3 + this.m12 * double2;
        matrix4x3d1.m00 = double4;
        matrix4x3d1.m01 = double5;
        matrix4x3d1.m02 = double6;
        matrix4x3d1.m20 = this.m20;
        matrix4x3d1.m21 = this.m21;
        matrix4x3d1.m22 = this.m22;
        matrix4x3d1.m30 = this.m30;
        matrix4x3d1.m31 = this.m31;
        matrix4x3d1.m32 = this.m32;
        matrix4x3d1.properties = this.properties & -13;
        return matrix4x3d1;
    }

    public Matrix4x3d rotateZ(double arg0) {
        return this.rotateZ(arg0, this);
    }

    public Matrix4x3d rotateXYZ(Vector3d arg0) {
        return this.rotateXYZ(arg0.x, arg0.y, arg0.z);
    }

    public Matrix4x3d rotateXYZ(double arg0, double arg1, double arg2) {
        return this.rotateXYZ(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4x3d rotateXYZ(double arg0, double arg1, double arg2, Matrix4x3d arg3) {
        if ((this.properties & 4) != 0) {
            return arg3.rotationXYZ(arg0, arg1, arg2);
        } else if ((this.properties & 8) != 0) {
            double double0 = this.m30;
            double double1 = this.m31;
            double double2 = this.m32;
            return arg3.rotationXYZ(arg0, arg1, arg2).setTranslation(double0, double1, double2);
        } else {
            return this.rotateXYZInternal(arg0, arg1, arg2, arg3);
        }
    }

    private Matrix4x3d rotateXYZInternal(double double1, double double4, double double7, Matrix4x3d matrix4x3d1) {
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
        matrix4x3d1.m20 = this.m00 * double3 + double15 * double5;
        matrix4x3d1.m21 = this.m01 * double3 + double16 * double5;
        matrix4x3d1.m22 = this.m02 * double3 + double17 * double5;
        matrix4x3d1.m00 = double18 * double8 + double12 * double6;
        matrix4x3d1.m01 = double19 * double8 + double13 * double6;
        matrix4x3d1.m02 = double20 * double8 + double14 * double6;
        matrix4x3d1.m10 = double18 * double11 + double12 * double8;
        matrix4x3d1.m11 = double19 * double11 + double13 * double8;
        matrix4x3d1.m12 = double20 * double11 + double14 * double8;
        matrix4x3d1.m30 = this.m30;
        matrix4x3d1.m31 = this.m31;
        matrix4x3d1.m32 = this.m32;
        matrix4x3d1.properties = this.properties & -13;
        return matrix4x3d1;
    }

    public Matrix4x3d rotateZYX(Vector3d arg0) {
        return this.rotateZYX(arg0.z, arg0.y, arg0.x);
    }

    public Matrix4x3d rotateZYX(double arg0, double arg1, double arg2) {
        return this.rotateZYX(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4x3d rotateZYX(double arg0, double arg1, double arg2, Matrix4x3d arg3) {
        if ((this.properties & 4) != 0) {
            return arg3.rotationZYX(arg0, arg1, arg2);
        } else if ((this.properties & 8) != 0) {
            double double0 = this.m30;
            double double1 = this.m31;
            double double2 = this.m32;
            return arg3.rotationZYX(arg0, arg1, arg2).setTranslation(double0, double1, double2);
        } else {
            return this.rotateZYXInternal(arg0, arg1, arg2, arg3);
        }
    }

    private Matrix4x3d rotateZYXInternal(double double7, double double4, double double1, Matrix4x3d matrix4x3d1) {
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
        double double15 = this.m00 * double9 + this.m10 * double8;
        double double16 = this.m01 * double9 + this.m11 * double8;
        double double17 = this.m02 * double9 + this.m12 * double8;
        double double18 = double12 * double3 + this.m20 * double5;
        double double19 = double13 * double3 + this.m21 * double5;
        double double20 = double14 * double3 + this.m22 * double5;
        matrix4x3d1.m00 = double12 * double5 + this.m20 * double10;
        matrix4x3d1.m01 = double13 * double5 + this.m21 * double10;
        matrix4x3d1.m02 = double14 * double5 + this.m22 * double10;
        matrix4x3d1.m10 = double15 * double2 + double18 * double0;
        matrix4x3d1.m11 = double16 * double2 + double19 * double0;
        matrix4x3d1.m12 = double17 * double2 + double20 * double0;
        matrix4x3d1.m20 = double15 * double11 + double18 * double2;
        matrix4x3d1.m21 = double16 * double11 + double19 * double2;
        matrix4x3d1.m22 = double17 * double11 + double20 * double2;
        matrix4x3d1.m30 = this.m30;
        matrix4x3d1.m31 = this.m31;
        matrix4x3d1.m32 = this.m32;
        matrix4x3d1.properties = this.properties & -13;
        return matrix4x3d1;
    }

    public Matrix4x3d rotateYXZ(Vector3d arg0) {
        return this.rotateYXZ(arg0.y, arg0.x, arg0.z);
    }

    public Matrix4x3d rotateYXZ(double arg0, double arg1, double arg2) {
        return this.rotateYXZ(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix4x3d rotateYXZ(double arg0, double arg1, double arg2, Matrix4x3d arg3) {
        if ((this.properties & 4) != 0) {
            return arg3.rotationYXZ(arg0, arg1, arg2);
        } else if ((this.properties & 8) != 0) {
            double double0 = this.m30;
            double double1 = this.m31;
            double double2 = this.m32;
            return arg3.rotationYXZ(arg0, arg1, arg2).setTranslation(double0, double1, double2);
        } else {
            return this.rotateYXZInternal(arg0, arg1, arg2, arg3);
        }
    }

    private Matrix4x3d rotateYXZInternal(double double4, double double1, double double7, Matrix4x3d matrix4x3d1) {
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
        double double15 = this.m00 * double5 + this.m20 * double9;
        double double16 = this.m01 * double5 + this.m21 * double9;
        double double17 = this.m02 * double5 + this.m22 * double9;
        double double18 = this.m10 * double2 + double12 * double0;
        double double19 = this.m11 * double2 + double13 * double0;
        double double20 = this.m12 * double2 + double14 * double0;
        matrix4x3d1.m20 = this.m10 * double10 + double12 * double2;
        matrix4x3d1.m21 = this.m11 * double10 + double13 * double2;
        matrix4x3d1.m22 = this.m12 * double10 + double14 * double2;
        matrix4x3d1.m00 = double15 * double8 + double18 * double6;
        matrix4x3d1.m01 = double16 * double8 + double19 * double6;
        matrix4x3d1.m02 = double17 * double8 + double20 * double6;
        matrix4x3d1.m10 = double15 * double11 + double18 * double8;
        matrix4x3d1.m11 = double16 * double11 + double19 * double8;
        matrix4x3d1.m12 = double17 * double11 + double20 * double8;
        matrix4x3d1.m30 = this.m30;
        matrix4x3d1.m31 = this.m31;
        matrix4x3d1.m32 = this.m32;
        matrix4x3d1.properties = this.properties & -13;
        return matrix4x3d1;
    }

    public Matrix4x3d rotation(AxisAngle4f arg0) {
        return this.rotation(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    public Matrix4x3d rotation(AxisAngle4d arg0) {
        return this.rotation(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    public Matrix4x3d rotation(Quaterniondc arg0) {
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
        this._m00(double0 + double1 - double3 - double2);
        this._m01(double7 + double5);
        this._m02(double9 - double11);
        this._m10(double7 - double5);
        this._m11(double2 - double3 + double0 - double1);
        this._m12(double13 + double15);
        this._m20(double11 + double9);
        this._m21(double13 - double15);
        this._m22(double3 - double2 - double1 + double0);
        this._m30(0.0);
        this._m31(0.0);
        this._m32(0.0);
        this.properties = 16;
        return this;
    }

    public Matrix4x3d rotation(Quaternionfc arg0) {
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
        this._m00(double0 + double1 - double3 - double2);
        this._m01(double7 + double5);
        this._m02(double9 - double11);
        this._m10(double7 - double5);
        this._m11(double2 - double3 + double0 - double1);
        this._m12(double13 + double15);
        this._m20(double11 + double9);
        this._m21(double13 - double15);
        this._m22(double3 - double2 - double1 + double0);
        this._m30(0.0);
        this._m31(0.0);
        this._m32(0.0);
        this.properties = 16;
        return this;
    }

    public Matrix4x3d translationRotateScale(
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
        this.m00 = arg7 - (double4 + double5) * arg7;
        this.m01 = (double6 + double11) * arg7;
        this.m02 = (double7 - double10) * arg7;
        this.m10 = (double6 - double11) * arg8;
        this.m11 = arg8 - (double5 + double3) * arg8;
        this.m12 = (double9 + double8) * arg8;
        this.m20 = (double7 + double10) * arg9;
        this.m21 = (double9 - double8) * arg9;
        this.m22 = arg9 - (double4 + double3) * arg9;
        this.m30 = arg0;
        this.m31 = arg1;
        this.m32 = arg2;
        this.properties = 0;
        return this;
    }

    public Matrix4x3d translationRotateScale(Vector3fc arg0, Quaternionfc arg1, Vector3fc arg2) {
        return this.translationRotateScale(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg1.w(), arg2.x(), arg2.y(), arg2.z());
    }

    public Matrix4x3d translationRotateScale(Vector3dc arg0, Quaterniondc arg1, Vector3dc arg2) {
        return this.translationRotateScale(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg1.w(), arg2.x(), arg2.y(), arg2.z());
    }

    public Matrix4x3d translationRotateScaleMul(
        double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, Matrix4x3dc arg10
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
        double double12 = arg7 - (double4 + double5) * arg7;
        double double13 = (double6 + double11) * arg7;
        double double14 = (double7 - double10) * arg7;
        double double15 = (double6 - double11) * arg8;
        double double16 = arg8 - (double5 + double3) * arg8;
        double double17 = (double9 + double8) * arg8;
        double double18 = (double7 + double10) * arg9;
        double double19 = (double9 - double8) * arg9;
        double double20 = arg9 - (double4 + double3) * arg9;
        double double21 = double12 * arg10.m00() + double15 * arg10.m01() + double18 * arg10.m02();
        double double22 = double13 * arg10.m00() + double16 * arg10.m01() + double19 * arg10.m02();
        this.m02 = double14 * arg10.m00() + double17 * arg10.m01() + double20 * arg10.m02();
        this.m00 = double21;
        this.m01 = double22;
        double double23 = double12 * arg10.m10() + double15 * arg10.m11() + double18 * arg10.m12();
        double double24 = double13 * arg10.m10() + double16 * arg10.m11() + double19 * arg10.m12();
        this.m12 = double14 * arg10.m10() + double17 * arg10.m11() + double20 * arg10.m12();
        this.m10 = double23;
        this.m11 = double24;
        double double25 = double12 * arg10.m20() + double15 * arg10.m21() + double18 * arg10.m22();
        double double26 = double13 * arg10.m20() + double16 * arg10.m21() + double19 * arg10.m22();
        this.m22 = double14 * arg10.m20() + double17 * arg10.m21() + double20 * arg10.m22();
        this.m20 = double25;
        this.m21 = double26;
        double double27 = double12 * arg10.m30() + double15 * arg10.m31() + double18 * arg10.m32() + arg0;
        double double28 = double13 * arg10.m30() + double16 * arg10.m31() + double19 * arg10.m32() + arg1;
        this.m32 = double14 * arg10.m30() + double17 * arg10.m31() + double20 * arg10.m32() + arg2;
        this.m30 = double27;
        this.m31 = double28;
        this.properties = 0;
        return this;
    }

    public Matrix4x3d translationRotateScaleMul(Vector3dc arg0, Quaterniondc arg1, Vector3dc arg2, Matrix4x3dc arg3) {
        return this.translationRotateScaleMul(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg1.w(), arg2.x(), arg2.y(), arg2.z(), arg3);
    }

    public Matrix4x3d translationRotate(double arg0, double arg1, double arg2, Quaterniondc arg3) {
        double double0 = arg3.x() + arg3.x();
        double double1 = arg3.y() + arg3.y();
        double double2 = arg3.z() + arg3.z();
        double double3 = double0 * arg3.x();
        double double4 = double1 * arg3.y();
        double double5 = double2 * arg3.z();
        double double6 = double0 * arg3.y();
        double double7 = double0 * arg3.z();
        double double8 = double0 * arg3.w();
        double double9 = double1 * arg3.z();
        double double10 = double1 * arg3.w();
        double double11 = double2 * arg3.w();
        this.m00 = 1.0 - (double4 + double5);
        this.m01 = double6 + double11;
        this.m02 = double7 - double10;
        this.m10 = double6 - double11;
        this.m11 = 1.0 - (double5 + double3);
        this.m12 = double9 + double8;
        this.m20 = double7 + double10;
        this.m21 = double9 - double8;
        this.m22 = 1.0 - (double4 + double3);
        this.m30 = arg0;
        this.m31 = arg1;
        this.m32 = arg2;
        this.properties = 16;
        return this;
    }

    public Matrix4x3d translationRotateMul(double arg0, double arg1, double arg2, Quaternionfc arg3, Matrix4x3dc arg4) {
        return this.translationRotateMul(arg0, arg1, arg2, arg3.x(), arg3.y(), arg3.z(), arg3.w(), arg4);
    }

    public Matrix4x3d translationRotateMul(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, Matrix4x3dc arg7) {
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
        this.m00 = double10 * arg7.m00() + double13 * arg7.m01() + double16 * arg7.m02();
        this.m01 = double11 * arg7.m00() + double14 * arg7.m01() + double17 * arg7.m02();
        this.m02 = double12 * arg7.m00() + double15 * arg7.m01() + double18 * arg7.m02();
        this.m10 = double10 * arg7.m10() + double13 * arg7.m11() + double16 * arg7.m12();
        this.m11 = double11 * arg7.m10() + double14 * arg7.m11() + double17 * arg7.m12();
        this.m12 = double12 * arg7.m10() + double15 * arg7.m11() + double18 * arg7.m12();
        this.m20 = double10 * arg7.m20() + double13 * arg7.m21() + double16 * arg7.m22();
        this.m21 = double11 * arg7.m20() + double14 * arg7.m21() + double17 * arg7.m22();
        this.m22 = double12 * arg7.m20() + double15 * arg7.m21() + double18 * arg7.m22();
        this.m30 = double10 * arg7.m30() + double13 * arg7.m31() + double16 * arg7.m32() + arg0;
        this.m31 = double11 * arg7.m30() + double14 * arg7.m31() + double17 * arg7.m32() + arg1;
        this.m32 = double12 * arg7.m30() + double15 * arg7.m31() + double18 * arg7.m32() + arg2;
        this.properties = 0;
        return this;
    }

    @Override
    public Matrix4x3d rotate(Quaterniondc arg0, Matrix4x3d arg1) {
        if ((this.properties & 4) != 0) {
            return arg1.rotation(arg0);
        } else {
            return (this.properties & 8) != 0 ? this.rotateTranslation(arg0, arg1) : this.rotateGeneric(arg0, arg1);
        }
    }

    private Matrix4x3d rotateGeneric(Quaterniondc quaterniondc, Matrix4x3d matrix4x3d1) {
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
        double double19 = double7 - double5;
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
        matrix4x3d1.m20 = this.m00 * double22 + this.m10 * double23 + this.m20 * double24;
        matrix4x3d1.m21 = this.m01 * double22 + this.m11 * double23 + this.m21 * double24;
        matrix4x3d1.m22 = this.m02 * double22 + this.m12 * double23 + this.m22 * double24;
        matrix4x3d1.m00 = double25;
        matrix4x3d1.m01 = double26;
        matrix4x3d1.m02 = double27;
        matrix4x3d1.m10 = double28;
        matrix4x3d1.m11 = double29;
        matrix4x3d1.m12 = double30;
        matrix4x3d1.m30 = this.m30;
        matrix4x3d1.m31 = this.m31;
        matrix4x3d1.m32 = this.m32;
        matrix4x3d1.properties = this.properties & -13;
        return matrix4x3d1;
    }

    @Override
    public Matrix4x3d rotate(Quaternionfc arg0, Matrix4x3d arg1) {
        if ((this.properties & 4) != 0) {
            return arg1.rotation(arg0);
        } else {
            return (this.properties & 8) != 0 ? this.rotateTranslation(arg0, arg1) : this.rotateGeneric(arg0, arg1);
        }
    }

    private Matrix4x3d rotateGeneric(Quaternionfc quaternionfc, Matrix4x3d matrix4x3d1) {
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
        double double22 = this.m00 * double13 + this.m10 * double14 + this.m20 * double15;
        double double23 = this.m01 * double13 + this.m11 * double14 + this.m21 * double15;
        double double24 = this.m02 * double13 + this.m12 * double14 + this.m22 * double15;
        matrix4x3d1.m20 = this.m00 * double16 + this.m10 * double17 + this.m20 * double18;
        matrix4x3d1.m21 = this.m01 * double16 + this.m11 * double17 + this.m21 * double18;
        matrix4x3d1.m22 = this.m02 * double16 + this.m12 * double17 + this.m22 * double18;
        matrix4x3d1.m00 = double19;
        matrix4x3d1.m01 = double20;
        matrix4x3d1.m02 = double21;
        matrix4x3d1.m10 = double22;
        matrix4x3d1.m11 = double23;
        matrix4x3d1.m12 = double24;
        matrix4x3d1.m30 = this.m30;
        matrix4x3d1.m31 = this.m31;
        matrix4x3d1.m32 = this.m32;
        matrix4x3d1.properties = this.properties & -13;
        return matrix4x3d1;
    }

    public Matrix4x3d rotate(Quaterniondc arg0) {
        return this.rotate(arg0, this);
    }

    public Matrix4x3d rotate(Quaternionfc arg0) {
        return this.rotate(arg0, this);
    }

    @Override
    public Matrix4x3d rotateTranslation(Quaterniondc arg0, Matrix4x3d arg1) {
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
        double double19 = double7 - double5;
        double double20 = double2 - double3 + double0 - double1;
        double double21 = double13 + double15;
        double double22 = double11 + double9;
        double double23 = double13 - double15;
        double double24 = double3 - double2 - double1 + double0;
        arg1.m20 = double22;
        arg1.m21 = double23;
        arg1.m22 = double24;
        arg1.m00 = double16;
        arg1.m01 = double17;
        arg1.m02 = double18;
        arg1.m10 = double19;
        arg1.m11 = double20;
        arg1.m12 = double21;
        arg1.m30 = this.m30;
        arg1.m31 = this.m31;
        arg1.m32 = this.m32;
        arg1.properties = this.properties & -13;
        return arg1;
    }

    @Override
    public Matrix4x3d rotateTranslation(Quaternionfc arg0, Matrix4x3d arg1) {
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
        arg1.m20 = double16;
        arg1.m21 = double17;
        arg1.m22 = double18;
        arg1.m00 = double10;
        arg1.m01 = double11;
        arg1.m02 = double12;
        arg1.m10 = double13;
        arg1.m11 = double14;
        arg1.m12 = double15;
        arg1.m30 = this.m30;
        arg1.m31 = this.m31;
        arg1.m32 = this.m32;
        arg1.properties = this.properties & -13;
        return arg1;
    }

    @Override
    public Matrix4x3d rotateLocal(Quaterniondc arg0, Matrix4x3d arg1) {
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
        double double19 = double7 - double5;
        double double20 = double2 - double3 + double0 - double1;
        double double21 = double13 + double15;
        double double22 = double11 + double9;
        double double23 = double13 - double15;
        double double24 = double3 - double2 - double1 + double0;
        double double25 = double16 * this.m00 + double19 * this.m01 + double22 * this.m02;
        double double26 = double17 * this.m00 + double20 * this.m01 + double23 * this.m02;
        double double27 = double18 * this.m00 + double21 * this.m01 + double24 * this.m02;
        double double28 = double16 * this.m10 + double19 * this.m11 + double22 * this.m12;
        double double29 = double17 * this.m10 + double20 * this.m11 + double23 * this.m12;
        double double30 = double18 * this.m10 + double21 * this.m11 + double24 * this.m12;
        double double31 = double16 * this.m20 + double19 * this.m21 + double22 * this.m22;
        double double32 = double17 * this.m20 + double20 * this.m21 + double23 * this.m22;
        double double33 = double18 * this.m20 + double21 * this.m21 + double24 * this.m22;
        double double34 = double16 * this.m30 + double19 * this.m31 + double22 * this.m32;
        double double35 = double17 * this.m30 + double20 * this.m31 + double23 * this.m32;
        double double36 = double18 * this.m30 + double21 * this.m31 + double24 * this.m32;
        arg1.m00 = double25;
        arg1.m01 = double26;
        arg1.m02 = double27;
        arg1.m10 = double28;
        arg1.m11 = double29;
        arg1.m12 = double30;
        arg1.m20 = double31;
        arg1.m21 = double32;
        arg1.m22 = double33;
        arg1.m30 = double34;
        arg1.m31 = double35;
        arg1.m32 = double36;
        arg1.properties = this.properties & -13;
        return arg1;
    }

    public Matrix4x3d rotateLocal(Quaterniondc arg0) {
        return this.rotateLocal(arg0, this);
    }

    @Override
    public Matrix4x3d rotateLocal(Quaternionfc arg0, Matrix4x3d arg1) {
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
        double double19 = double7 - double5;
        double double20 = double2 - double3 + double0 - double1;
        double double21 = double13 + double15;
        double double22 = double11 + double9;
        double double23 = double13 - double15;
        double double24 = double3 - double2 - double1 + double0;
        double double25 = double16 * this.m00 + double19 * this.m01 + double22 * this.m02;
        double double26 = double17 * this.m00 + double20 * this.m01 + double23 * this.m02;
        double double27 = double18 * this.m00 + double21 * this.m01 + double24 * this.m02;
        double double28 = double16 * this.m10 + double19 * this.m11 + double22 * this.m12;
        double double29 = double17 * this.m10 + double20 * this.m11 + double23 * this.m12;
        double double30 = double18 * this.m10 + double21 * this.m11 + double24 * this.m12;
        double double31 = double16 * this.m20 + double19 * this.m21 + double22 * this.m22;
        double double32 = double17 * this.m20 + double20 * this.m21 + double23 * this.m22;
        double double33 = double18 * this.m20 + double21 * this.m21 + double24 * this.m22;
        double double34 = double16 * this.m30 + double19 * this.m31 + double22 * this.m32;
        double double35 = double17 * this.m30 + double20 * this.m31 + double23 * this.m32;
        double double36 = double18 * this.m30 + double21 * this.m31 + double24 * this.m32;
        arg1.m00 = double25;
        arg1.m01 = double26;
        arg1.m02 = double27;
        arg1.m10 = double28;
        arg1.m11 = double29;
        arg1.m12 = double30;
        arg1.m20 = double31;
        arg1.m21 = double32;
        arg1.m22 = double33;
        arg1.m30 = double34;
        arg1.m31 = double35;
        arg1.m32 = double36;
        arg1.properties = this.properties & -13;
        return arg1;
    }

    public Matrix4x3d rotateLocal(Quaternionfc arg0) {
        return this.rotateLocal(arg0, this);
    }

    public Matrix4x3d rotate(AxisAngle4f arg0) {
        return this.rotate(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    @Override
    public Matrix4x3d rotate(AxisAngle4f arg0, Matrix4x3d arg1) {
        return this.rotate(arg0.angle, arg0.x, arg0.y, arg0.z, arg1);
    }

    public Matrix4x3d rotate(AxisAngle4d arg0) {
        return this.rotate(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    @Override
    public Matrix4x3d rotate(AxisAngle4d arg0, Matrix4x3d arg1) {
        return this.rotate(arg0.angle, arg0.x, arg0.y, arg0.z, arg1);
    }

    public Matrix4x3d rotate(double arg0, Vector3dc arg1) {
        return this.rotate(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    @Override
    public Matrix4x3d rotate(double arg0, Vector3dc arg1, Matrix4x3d arg2) {
        return this.rotate(arg0, arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    public Matrix4x3d rotate(double arg0, Vector3fc arg1) {
        return this.rotate(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    @Override
    public Matrix4x3d rotate(double arg0, Vector3fc arg1, Matrix4x3d arg2) {
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
            default:
                throw new IndexOutOfBoundsException();
        }

        return arg1;
    }

    public Matrix4x3d setRow(int arg0, Vector4dc arg1) throws IndexOutOfBoundsException {
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

    public Matrix4x3d setColumn(int arg0, Vector3dc arg1) throws IndexOutOfBoundsException {
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

    public Matrix4x3d normal() {
        return this.normal(this);
    }

    @Override
    public Matrix4x3d normal(Matrix4x3d arg0) {
        if ((this.properties & 4) != 0) {
            return arg0.identity();
        } else {
            return (this.properties & 16) != 0 ? this.normalOrthonormal(arg0) : this.normalGeneric(arg0);
        }
    }

    private Matrix4x3d normalOrthonormal(Matrix4x3d matrix4x3d0) {
        if (matrix4x3d0 != this) {
            matrix4x3d0.set(this);
        }

        return matrix4x3d0._properties(16);
    }

    private Matrix4x3d normalGeneric(Matrix4x3d matrix4x3d1) {
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
        matrix4x3d1.m00 = double8;
        matrix4x3d1.m01 = double9;
        matrix4x3d1.m02 = double10;
        matrix4x3d1.m10 = double11;
        matrix4x3d1.m11 = double12;
        matrix4x3d1.m12 = double13;
        matrix4x3d1.m20 = double14;
        matrix4x3d1.m21 = double15;
        matrix4x3d1.m22 = double16;
        matrix4x3d1.m30 = 0.0;
        matrix4x3d1.m31 = 0.0;
        matrix4x3d1.m32 = 0.0;
        matrix4x3d1.properties = this.properties & -9;
        return matrix4x3d1;
    }

    @Override
    public Matrix3d normal(Matrix3d arg0) {
        return (this.properties & 16) != 0 ? this.normalOrthonormal(arg0) : this.normalGeneric(arg0);
    }

    private Matrix3d normalOrthonormal(Matrix3d matrix3d) {
        return matrix3d.set(this);
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
        matrix3d.m00((this.m11 * this.m22 - this.m21 * this.m12) * double7);
        matrix3d.m01((this.m20 * this.m12 - this.m10 * this.m22) * double7);
        matrix3d.m02((this.m10 * this.m21 - this.m20 * this.m11) * double7);
        matrix3d.m10((this.m21 * this.m02 - this.m01 * this.m22) * double7);
        matrix3d.m11((this.m00 * this.m22 - this.m20 * this.m02) * double7);
        matrix3d.m12((this.m20 * this.m01 - this.m00 * this.m21) * double7);
        matrix3d.m20((double4 - double5) * double7);
        matrix3d.m21((double2 - double3) * double7);
        matrix3d.m22((double0 - double1) * double7);
        return matrix3d;
    }

    public Matrix4x3d cofactor3x3() {
        return this.cofactor3x3(this);
    }

    @Override
    public Matrix3d cofactor3x3(Matrix3d arg0) {
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
    public Matrix4x3d cofactor3x3(Matrix4x3d arg0) {
        double double0 = this.m11 * this.m22 - this.m21 * this.m12;
        double double1 = this.m20 * this.m12 - this.m10 * this.m22;
        double double2 = this.m10 * this.m21 - this.m20 * this.m11;
        double double3 = this.m21 * this.m02 - this.m01 * this.m22;
        double double4 = this.m00 * this.m22 - this.m20 * this.m02;
        double double5 = this.m20 * this.m01 - this.m00 * this.m21;
        double double6 = this.m01 * this.m12 - this.m11 * this.m02;
        double double7 = this.m02 * this.m10 - this.m12 * this.m00;
        double double8 = this.m00 * this.m11 - this.m10 * this.m01;
        arg0.m00 = double0;
        arg0.m01 = double1;
        arg0.m02 = double2;
        arg0.m10 = double3;
        arg0.m11 = double4;
        arg0.m12 = double5;
        arg0.m20 = double6;
        arg0.m21 = double7;
        arg0.m22 = double8;
        arg0.m30 = 0.0;
        arg0.m31 = 0.0;
        arg0.m32 = 0.0;
        arg0.properties = this.properties & -9;
        return arg0;
    }

    public Matrix4x3d normalize3x3() {
        return this.normalize3x3(this);
    }

    @Override
    public Matrix4x3d normalize3x3(Matrix4x3d arg0) {
        double double0 = Math.invsqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
        double double1 = Math.invsqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
        double double2 = Math.invsqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
        arg0.m00 = this.m00 * double0;
        arg0.m01 = this.m01 * double0;
        arg0.m02 = this.m02 * double0;
        arg0.m10 = this.m10 * double1;
        arg0.m11 = this.m11 * double1;
        arg0.m12 = this.m12 * double1;
        arg0.m20 = this.m20 * double2;
        arg0.m21 = this.m21 * double2;
        arg0.m22 = this.m22 * double2;
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
    public Matrix4x3d reflect(double arg0, double arg1, double arg2, double arg3, Matrix4x3d arg4) {
        if ((this.properties & 4) != 0) {
            return arg4.reflection(arg0, arg1, arg2, arg3);
        } else {
            double double0 = arg0 + arg0;
            double double1 = arg1 + arg1;
            double double2 = arg2 + arg2;
            double double3 = arg3 + arg3;
            double double4 = 1.0 - double0 * arg0;
            double double5 = -double0 * arg1;
            double double6 = -double0 * arg2;
            double double7 = -double1 * arg0;
            double double8 = 1.0 - double1 * arg1;
            double double9 = -double1 * arg2;
            double double10 = -double2 * arg0;
            double double11 = -double2 * arg1;
            double double12 = 1.0 - double2 * arg2;
            double double13 = -double3 * arg0;
            double double14 = -double3 * arg1;
            double double15 = -double3 * arg2;
            arg4.m30 = this.m00 * double13 + this.m10 * double14 + this.m20 * double15 + this.m30;
            arg4.m31 = this.m01 * double13 + this.m11 * double14 + this.m21 * double15 + this.m31;
            arg4.m32 = this.m02 * double13 + this.m12 * double14 + this.m22 * double15 + this.m32;
            double double16 = this.m00 * double4 + this.m10 * double5 + this.m20 * double6;
            double double17 = this.m01 * double4 + this.m11 * double5 + this.m21 * double6;
            double double18 = this.m02 * double4 + this.m12 * double5 + this.m22 * double6;
            double double19 = this.m00 * double7 + this.m10 * double8 + this.m20 * double9;
            double double20 = this.m01 * double7 + this.m11 * double8 + this.m21 * double9;
            double double21 = this.m02 * double7 + this.m12 * double8 + this.m22 * double9;
            arg4.m20 = this.m00 * double10 + this.m10 * double11 + this.m20 * double12;
            arg4.m21 = this.m01 * double10 + this.m11 * double11 + this.m21 * double12;
            arg4.m22 = this.m02 * double10 + this.m12 * double11 + this.m22 * double12;
            arg4.m00 = double16;
            arg4.m01 = double17;
            arg4.m02 = double18;
            arg4.m10 = double19;
            arg4.m11 = double20;
            arg4.m12 = double21;
            arg4.properties = this.properties & -13;
            return arg4;
        }
    }

    public Matrix4x3d reflect(double arg0, double arg1, double arg2, double arg3) {
        return this.reflect(arg0, arg1, arg2, arg3, this);
    }

    public Matrix4x3d reflect(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.reflect(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    @Override
    public Matrix4x3d reflect(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4x3d arg6) {
        double double0 = Math.invsqrt(arg0 * arg0 + arg1 * arg1 + arg2 * arg2);
        double double1 = arg0 * double0;
        double double2 = arg1 * double0;
        double double3 = arg2 * double0;
        return this.reflect(double1, double2, double3, -double1 * arg3 - double2 * arg4 - double3 * arg5, arg6);
    }

    public Matrix4x3d reflect(Vector3dc arg0, Vector3dc arg1) {
        return this.reflect(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix4x3d reflect(Quaterniondc arg0, Vector3dc arg1) {
        return this.reflect(arg0, arg1, this);
    }

    @Override
    public Matrix4x3d reflect(Quaterniondc arg0, Vector3dc arg1, Matrix4x3d arg2) {
        double double0 = arg0.x() + arg0.x();
        double double1 = arg0.y() + arg0.y();
        double double2 = arg0.z() + arg0.z();
        double double3 = arg0.x() * double2 + arg0.w() * double1;
        double double4 = arg0.y() * double2 - arg0.w() * double0;
        double double5 = 1.0 - (arg0.x() * double0 + arg0.y() * double1);
        return this.reflect(double3, double4, double5, arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    @Override
    public Matrix4x3d reflect(Vector3dc arg0, Vector3dc arg1, Matrix4x3d arg2) {
        return this.reflect(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    public Matrix4x3d reflection(double arg0, double arg1, double arg2, double arg3) {
        double double0 = arg0 + arg0;
        double double1 = arg1 + arg1;
        double double2 = arg2 + arg2;
        double double3 = arg3 + arg3;
        this.m00 = 1.0 - double0 * arg0;
        this.m01 = -double0 * arg1;
        this.m02 = -double0 * arg2;
        this.m10 = -double1 * arg0;
        this.m11 = 1.0 - double1 * arg1;
        this.m12 = -double1 * arg2;
        this.m20 = -double2 * arg0;
        this.m21 = -double2 * arg1;
        this.m22 = 1.0 - double2 * arg2;
        this.m30 = -double3 * arg0;
        this.m31 = -double3 * arg1;
        this.m32 = -double3 * arg2;
        this.properties = 16;
        return this;
    }

    public Matrix4x3d reflection(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        double double0 = Math.invsqrt(arg0 * arg0 + arg1 * arg1 + arg2 * arg2);
        double double1 = arg0 * double0;
        double double2 = arg1 * double0;
        double double3 = arg2 * double0;
        return this.reflection(double1, double2, double3, -double1 * arg3 - double2 * arg4 - double3 * arg5);
    }

    public Matrix4x3d reflection(Vector3dc arg0, Vector3dc arg1) {
        return this.reflection(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix4x3d reflection(Quaterniondc arg0, Vector3dc arg1) {
        double double0 = arg0.x() + arg0.x();
        double double1 = arg0.y() + arg0.y();
        double double2 = arg0.z() + arg0.z();
        double double3 = arg0.x() * double2 + arg0.w() * double1;
        double double4 = arg0.y() * double2 - arg0.w() * double0;
        double double5 = 1.0 - (arg0.x() * double0 + arg0.y() * double1);
        return this.reflection(double3, double4, double5, arg1.x(), arg1.y(), arg1.z());
    }

    @Override
    public Matrix4x3d ortho(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6, Matrix4x3d arg7) {
        double double0 = 2.0 / (arg1 - arg0);
        double double1 = 2.0 / (arg3 - arg2);
        double double2 = (arg6 ? 1.0 : 2.0) / (arg4 - arg5);
        double double3 = (arg0 + arg1) / (arg0 - arg1);
        double double4 = (arg3 + arg2) / (arg2 - arg3);
        double double5 = (arg6 ? arg4 : arg5 + arg4) / (arg4 - arg5);
        arg7.m30 = this.m00 * double3 + this.m10 * double4 + this.m20 * double5 + this.m30;
        arg7.m31 = this.m01 * double3 + this.m11 * double4 + this.m21 * double5 + this.m31;
        arg7.m32 = this.m02 * double3 + this.m12 * double4 + this.m22 * double5 + this.m32;
        arg7.m00 = this.m00 * double0;
        arg7.m01 = this.m01 * double0;
        arg7.m02 = this.m02 * double0;
        arg7.m10 = this.m10 * double1;
        arg7.m11 = this.m11 * double1;
        arg7.m12 = this.m12 * double1;
        arg7.m20 = this.m20 * double2;
        arg7.m21 = this.m21 * double2;
        arg7.m22 = this.m22 * double2;
        arg7.properties = this.properties & -29;
        return arg7;
    }

    @Override
    public Matrix4x3d ortho(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4x3d arg6) {
        return this.ortho(arg0, arg1, arg2, arg3, arg4, arg5, false, arg6);
    }

    public Matrix4x3d ortho(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6) {
        return this.ortho(arg0, arg1, arg2, arg3, arg4, arg5, arg6, this);
    }

    public Matrix4x3d ortho(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.ortho(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    @Override
    public Matrix4x3d orthoLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6, Matrix4x3d arg7) {
        double double0 = 2.0 / (arg1 - arg0);
        double double1 = 2.0 / (arg3 - arg2);
        double double2 = (arg6 ? 1.0 : 2.0) / (arg5 - arg4);
        double double3 = (arg0 + arg1) / (arg0 - arg1);
        double double4 = (arg3 + arg2) / (arg2 - arg3);
        double double5 = (arg6 ? arg4 : arg5 + arg4) / (arg4 - arg5);
        arg7.m30 = this.m00 * double3 + this.m10 * double4 + this.m20 * double5 + this.m30;
        arg7.m31 = this.m01 * double3 + this.m11 * double4 + this.m21 * double5 + this.m31;
        arg7.m32 = this.m02 * double3 + this.m12 * double4 + this.m22 * double5 + this.m32;
        arg7.m00 = this.m00 * double0;
        arg7.m01 = this.m01 * double0;
        arg7.m02 = this.m02 * double0;
        arg7.m10 = this.m10 * double1;
        arg7.m11 = this.m11 * double1;
        arg7.m12 = this.m12 * double1;
        arg7.m20 = this.m20 * double2;
        arg7.m21 = this.m21 * double2;
        arg7.m22 = this.m22 * double2;
        arg7.properties = this.properties & -29;
        return arg7;
    }

    @Override
    public Matrix4x3d orthoLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4x3d arg6) {
        return this.orthoLH(arg0, arg1, arg2, arg3, arg4, arg5, false, arg6);
    }

    public Matrix4x3d orthoLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6) {
        return this.orthoLH(arg0, arg1, arg2, arg3, arg4, arg5, arg6, this);
    }

    public Matrix4x3d orthoLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.orthoLH(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    public Matrix4x3d setOrtho(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6) {
        this.m00 = 2.0 / (arg1 - arg0);
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = 2.0 / (arg3 - arg2);
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = (arg6 ? 1.0 : 2.0) / (arg4 - arg5);
        this.m30 = (arg1 + arg0) / (arg0 - arg1);
        this.m31 = (arg3 + arg2) / (arg2 - arg3);
        this.m32 = (arg6 ? arg4 : arg5 + arg4) / (arg4 - arg5);
        this.properties = 0;
        return this;
    }

    public Matrix4x3d setOrtho(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.setOrtho(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    public Matrix4x3d setOrthoLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6) {
        this.m00 = 2.0 / (arg1 - arg0);
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = 2.0 / (arg3 - arg2);
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = (arg6 ? 1.0 : 2.0) / (arg5 - arg4);
        this.m30 = (arg1 + arg0) / (arg0 - arg1);
        this.m31 = (arg3 + arg2) / (arg2 - arg3);
        this.m32 = (arg6 ? arg4 : arg5 + arg4) / (arg4 - arg5);
        this.properties = 0;
        return this;
    }

    public Matrix4x3d setOrthoLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.setOrthoLH(arg0, arg1, arg2, arg3, arg4, arg5, false);
    }

    @Override
    public Matrix4x3d orthoSymmetric(double arg0, double arg1, double arg2, double arg3, boolean arg4, Matrix4x3d arg5) {
        double double0 = 2.0 / arg0;
        double double1 = 2.0 / arg1;
        double double2 = (arg4 ? 1.0 : 2.0) / (arg2 - arg3);
        double double3 = (arg4 ? arg2 : arg3 + arg2) / (arg2 - arg3);
        arg5.m30 = this.m20 * double3 + this.m30;
        arg5.m31 = this.m21 * double3 + this.m31;
        arg5.m32 = this.m22 * double3 + this.m32;
        arg5.m00 = this.m00 * double0;
        arg5.m01 = this.m01 * double0;
        arg5.m02 = this.m02 * double0;
        arg5.m10 = this.m10 * double1;
        arg5.m11 = this.m11 * double1;
        arg5.m12 = this.m12 * double1;
        arg5.m20 = this.m20 * double2;
        arg5.m21 = this.m21 * double2;
        arg5.m22 = this.m22 * double2;
        arg5.properties = this.properties & -29;
        return arg5;
    }

    @Override
    public Matrix4x3d orthoSymmetric(double arg0, double arg1, double arg2, double arg3, Matrix4x3d arg4) {
        return this.orthoSymmetric(arg0, arg1, arg2, arg3, false, arg4);
    }

    public Matrix4x3d orthoSymmetric(double arg0, double arg1, double arg2, double arg3, boolean arg4) {
        return this.orthoSymmetric(arg0, arg1, arg2, arg3, arg4, this);
    }

    public Matrix4x3d orthoSymmetric(double arg0, double arg1, double arg2, double arg3) {
        return this.orthoSymmetric(arg0, arg1, arg2, arg3, false, this);
    }

    @Override
    public Matrix4x3d orthoSymmetricLH(double arg0, double arg1, double arg2, double arg3, boolean arg4, Matrix4x3d arg5) {
        double double0 = 2.0 / arg0;
        double double1 = 2.0 / arg1;
        double double2 = (arg4 ? 1.0 : 2.0) / (arg3 - arg2);
        double double3 = (arg4 ? arg2 : arg3 + arg2) / (arg2 - arg3);
        arg5.m30 = this.m20 * double3 + this.m30;
        arg5.m31 = this.m21 * double3 + this.m31;
        arg5.m32 = this.m22 * double3 + this.m32;
        arg5.m00 = this.m00 * double0;
        arg5.m01 = this.m01 * double0;
        arg5.m02 = this.m02 * double0;
        arg5.m10 = this.m10 * double1;
        arg5.m11 = this.m11 * double1;
        arg5.m12 = this.m12 * double1;
        arg5.m20 = this.m20 * double2;
        arg5.m21 = this.m21 * double2;
        arg5.m22 = this.m22 * double2;
        arg5.properties = this.properties & -29;
        return arg5;
    }

    @Override
    public Matrix4x3d orthoSymmetricLH(double arg0, double arg1, double arg2, double arg3, Matrix4x3d arg4) {
        return this.orthoSymmetricLH(arg0, arg1, arg2, arg3, false, arg4);
    }

    public Matrix4x3d orthoSymmetricLH(double arg0, double arg1, double arg2, double arg3, boolean arg4) {
        return this.orthoSymmetricLH(arg0, arg1, arg2, arg3, arg4, this);
    }

    public Matrix4x3d orthoSymmetricLH(double arg0, double arg1, double arg2, double arg3) {
        return this.orthoSymmetricLH(arg0, arg1, arg2, arg3, false, this);
    }

    public Matrix4x3d setOrthoSymmetric(double arg0, double arg1, double arg2, double arg3, boolean arg4) {
        this.m00 = 2.0 / arg0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = 2.0 / arg1;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = (arg4 ? 1.0 : 2.0) / (arg2 - arg3);
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = (arg4 ? arg2 : arg3 + arg2) / (arg2 - arg3);
        this.properties = 0;
        return this;
    }

    public Matrix4x3d setOrthoSymmetric(double arg0, double arg1, double arg2, double arg3) {
        return this.setOrthoSymmetric(arg0, arg1, arg2, arg3, false);
    }

    public Matrix4x3d setOrthoSymmetricLH(double arg0, double arg1, double arg2, double arg3, boolean arg4) {
        this.m00 = 2.0 / arg0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = 2.0 / arg1;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = (arg4 ? 1.0 : 2.0) / (arg3 - arg2);
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = (arg4 ? arg2 : arg3 + arg2) / (arg2 - arg3);
        this.properties = 0;
        return this;
    }

    public Matrix4x3d setOrthoSymmetricLH(double arg0, double arg1, double arg2, double arg3) {
        return this.setOrthoSymmetricLH(arg0, arg1, arg2, arg3, false);
    }

    @Override
    public Matrix4x3d ortho2D(double arg0, double arg1, double arg2, double arg3, Matrix4x3d arg4) {
        double double0 = 2.0 / (arg1 - arg0);
        double double1 = 2.0 / (arg3 - arg2);
        double double2 = -(arg1 + arg0) / (arg1 - arg0);
        double double3 = -(arg3 + arg2) / (arg3 - arg2);
        arg4.m30 = this.m00 * double2 + this.m10 * double3 + this.m30;
        arg4.m31 = this.m01 * double2 + this.m11 * double3 + this.m31;
        arg4.m32 = this.m02 * double2 + this.m12 * double3 + this.m32;
        arg4.m00 = this.m00 * double0;
        arg4.m01 = this.m01 * double0;
        arg4.m02 = this.m02 * double0;
        arg4.m10 = this.m10 * double1;
        arg4.m11 = this.m11 * double1;
        arg4.m12 = this.m12 * double1;
        arg4.m20 = -this.m20;
        arg4.m21 = -this.m21;
        arg4.m22 = -this.m22;
        arg4.properties = this.properties & -29;
        return arg4;
    }

    public Matrix4x3d ortho2D(double arg0, double arg1, double arg2, double arg3) {
        return this.ortho2D(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix4x3d ortho2DLH(double arg0, double arg1, double arg2, double arg3, Matrix4x3d arg4) {
        double double0 = 2.0 / (arg1 - arg0);
        double double1 = 2.0 / (arg3 - arg2);
        double double2 = -(arg1 + arg0) / (arg1 - arg0);
        double double3 = -(arg3 + arg2) / (arg3 - arg2);
        arg4.m30 = this.m00 * double2 + this.m10 * double3 + this.m30;
        arg4.m31 = this.m01 * double2 + this.m11 * double3 + this.m31;
        arg4.m32 = this.m02 * double2 + this.m12 * double3 + this.m32;
        arg4.m00 = this.m00 * double0;
        arg4.m01 = this.m01 * double0;
        arg4.m02 = this.m02 * double0;
        arg4.m10 = this.m10 * double1;
        arg4.m11 = this.m11 * double1;
        arg4.m12 = this.m12 * double1;
        arg4.m20 = this.m20;
        arg4.m21 = this.m21;
        arg4.m22 = this.m22;
        arg4.properties = this.properties & -29;
        return arg4;
    }

    public Matrix4x3d ortho2DLH(double arg0, double arg1, double arg2, double arg3) {
        return this.ortho2DLH(arg0, arg1, arg2, arg3, this);
    }

    public Matrix4x3d setOrtho2D(double arg0, double arg1, double arg2, double arg3) {
        this.m00 = 2.0 / (arg1 - arg0);
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = 2.0 / (arg3 - arg2);
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = -1.0;
        this.m30 = -(arg1 + arg0) / (arg1 - arg0);
        this.m31 = -(arg3 + arg2) / (arg3 - arg2);
        this.m32 = 0.0;
        this.properties = 0;
        return this;
    }

    public Matrix4x3d setOrtho2DLH(double arg0, double arg1, double arg2, double arg3) {
        this.m00 = 2.0 / (arg1 - arg0);
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = 2.0 / (arg3 - arg2);
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
        this.m30 = -(arg1 + arg0) / (arg1 - arg0);
        this.m31 = -(arg3 + arg2) / (arg3 - arg2);
        this.m32 = 0.0;
        this.properties = 0;
        return this;
    }

    public Matrix4x3d lookAlong(Vector3dc arg0, Vector3dc arg1) {
        return this.lookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), this);
    }

    @Override
    public Matrix4x3d lookAlong(Vector3dc arg0, Vector3dc arg1, Matrix4x3d arg2) {
        return this.lookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    @Override
    public Matrix4x3d lookAlong(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4x3d arg6) {
        if ((this.properties & 4) != 0) {
            return this.setLookAlong(arg0, arg1, arg2, arg3, arg4, arg5);
        } else {
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
            double double8 = this.m00 * double1 + this.m10 * double5 + this.m20 * arg0;
            double double9 = this.m01 * double1 + this.m11 * double5 + this.m21 * arg0;
            double double10 = this.m02 * double1 + this.m12 * double5 + this.m22 * arg0;
            double double11 = this.m00 * double2 + this.m10 * double6 + this.m20 * arg1;
            double double12 = this.m01 * double2 + this.m11 * double6 + this.m21 * arg1;
            double double13 = this.m02 * double2 + this.m12 * double6 + this.m22 * arg1;
            arg6.m20 = this.m00 * double3 + this.m10 * double7 + this.m20 * arg2;
            arg6.m21 = this.m01 * double3 + this.m11 * double7 + this.m21 * arg2;
            arg6.m22 = this.m02 * double3 + this.m12 * double7 + this.m22 * arg2;
            arg6.m00 = double8;
            arg6.m01 = double9;
            arg6.m02 = double10;
            arg6.m10 = double11;
            arg6.m11 = double12;
            arg6.m12 = double13;
            arg6.m30 = this.m30;
            arg6.m31 = this.m31;
            arg6.m32 = this.m32;
            arg6.properties = this.properties & -13;
            return arg6;
        }
    }

    public Matrix4x3d lookAlong(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.lookAlong(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    public Matrix4x3d setLookAlong(Vector3dc arg0, Vector3dc arg1) {
        return this.setLookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix4x3d setLookAlong(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
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
        this.m00 = double1;
        this.m01 = double5;
        this.m02 = arg0;
        this.m10 = double2;
        this.m11 = double6;
        this.m12 = arg1;
        this.m20 = double3;
        this.m21 = double7;
        this.m22 = arg2;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.properties = 16;
        return this;
    }

    public Matrix4x3d setLookAt(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2) {
        return this.setLookAt(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z());
    }

    public Matrix4x3d setLookAt(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
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
        this.m00 = double4;
        this.m01 = double8;
        this.m02 = double0;
        this.m10 = double5;
        this.m11 = double9;
        this.m12 = double1;
        this.m20 = double6;
        this.m21 = double10;
        this.m22 = double2;
        this.m30 = -(double4 * arg0 + double5 * arg1 + double6 * arg2);
        this.m31 = -(double8 * arg0 + double9 * arg1 + double10 * arg2);
        this.m32 = -(double0 * arg0 + double1 * arg1 + double2 * arg2);
        this.properties = 16;
        return this;
    }

    @Override
    public Matrix4x3d lookAt(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2, Matrix4x3d arg3) {
        return this.lookAt(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z(), arg3);
    }

    public Matrix4x3d lookAt(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2) {
        return this.lookAt(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z(), this);
    }

    @Override
    public Matrix4x3d lookAt(
        double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, Matrix4x3d arg9
    ) {
        return (this.properties & 4) != 0
            ? arg9.setLookAt(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
            : this.lookAtGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
    }

    private Matrix4x3d lookAtGeneric(
        double double1,
        double double4,
        double double7,
        double double2,
        double double5,
        double double8,
        double double14,
        double double12,
        double double11,
        Matrix4x3d matrix4x3d1
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
        matrix4x3d1.m30 = this.m00 * double20 + this.m10 * double21 + this.m20 * double22 + this.m30;
        matrix4x3d1.m31 = this.m01 * double20 + this.m11 * double21 + this.m21 * double22 + this.m31;
        matrix4x3d1.m32 = this.m02 * double20 + this.m12 * double21 + this.m22 * double22 + this.m32;
        double double23 = this.m00 * double10 + this.m10 * double17 + this.m20 * double0;
        double double24 = this.m01 * double10 + this.m11 * double17 + this.m21 * double0;
        double double25 = this.m02 * double10 + this.m12 * double17 + this.m22 * double0;
        double double26 = this.m00 * double13 + this.m10 * double18 + this.m20 * double3;
        double double27 = this.m01 * double13 + this.m11 * double18 + this.m21 * double3;
        double double28 = this.m02 * double13 + this.m12 * double18 + this.m22 * double3;
        matrix4x3d1.m20 = this.m00 * double15 + this.m10 * double19 + this.m20 * double6;
        matrix4x3d1.m21 = this.m01 * double15 + this.m11 * double19 + this.m21 * double6;
        matrix4x3d1.m22 = this.m02 * double15 + this.m12 * double19 + this.m22 * double6;
        matrix4x3d1.m00 = double23;
        matrix4x3d1.m01 = double24;
        matrix4x3d1.m02 = double25;
        matrix4x3d1.m10 = double26;
        matrix4x3d1.m11 = double27;
        matrix4x3d1.m12 = double28;
        matrix4x3d1.properties = this.properties & -13;
        return matrix4x3d1;
    }

    public Matrix4x3d lookAt(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        return this.lookAt(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, this);
    }

    public Matrix4x3d setLookAtLH(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2) {
        return this.setLookAtLH(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z());
    }

    public Matrix4x3d setLookAtLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
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
        this.m00 = double4;
        this.m01 = double8;
        this.m02 = double0;
        this.m10 = double5;
        this.m11 = double9;
        this.m12 = double1;
        this.m20 = double6;
        this.m21 = double10;
        this.m22 = double2;
        this.m30 = -(double4 * arg0 + double5 * arg1 + double6 * arg2);
        this.m31 = -(double8 * arg0 + double9 * arg1 + double10 * arg2);
        this.m32 = -(double0 * arg0 + double1 * arg1 + double2 * arg2);
        this.properties = 16;
        return this;
    }

    @Override
    public Matrix4x3d lookAtLH(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2, Matrix4x3d arg3) {
        return this.lookAtLH(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z(), arg3);
    }

    public Matrix4x3d lookAtLH(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2) {
        return this.lookAtLH(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z(), this);
    }

    @Override
    public Matrix4x3d lookAtLH(
        double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, Matrix4x3d arg9
    ) {
        return (this.properties & 4) != 0
            ? arg9.setLookAtLH(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
            : this.lookAtLHGeneric(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
    }

    private Matrix4x3d lookAtLHGeneric(
        double double2,
        double double5,
        double double8,
        double double1,
        double double4,
        double double7,
        double double14,
        double double12,
        double double11,
        Matrix4x3d matrix4x3d1
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
        matrix4x3d1.m30 = this.m00 * double20 + this.m10 * double21 + this.m20 * double22 + this.m30;
        matrix4x3d1.m31 = this.m01 * double20 + this.m11 * double21 + this.m21 * double22 + this.m31;
        matrix4x3d1.m32 = this.m02 * double20 + this.m12 * double21 + this.m22 * double22 + this.m32;
        double double23 = this.m00 * double10 + this.m10 * double17 + this.m20 * double0;
        double double24 = this.m01 * double10 + this.m11 * double17 + this.m21 * double0;
        double double25 = this.m02 * double10 + this.m12 * double17 + this.m22 * double0;
        double double26 = this.m00 * double13 + this.m10 * double18 + this.m20 * double3;
        double double27 = this.m01 * double13 + this.m11 * double18 + this.m21 * double3;
        double double28 = this.m02 * double13 + this.m12 * double18 + this.m22 * double3;
        matrix4x3d1.m20 = this.m00 * double15 + this.m10 * double19 + this.m20 * double6;
        matrix4x3d1.m21 = this.m01 * double15 + this.m11 * double19 + this.m21 * double6;
        matrix4x3d1.m22 = this.m02 * double15 + this.m12 * double19 + this.m22 * double6;
        matrix4x3d1.m00 = double23;
        matrix4x3d1.m01 = double24;
        matrix4x3d1.m02 = double25;
        matrix4x3d1.m10 = double26;
        matrix4x3d1.m11 = double27;
        matrix4x3d1.m12 = double28;
        matrix4x3d1.properties = this.properties & -13;
        return matrix4x3d1;
    }

    public Matrix4x3d lookAtLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        return this.lookAtLH(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, this);
    }

    @Override
    public Vector4d frustumPlane(int arg0, Vector4d arg1) {
        switch (arg0) {
            case 0:
                arg1.set(this.m00, this.m10, this.m20, 1.0 + this.m30).normalize();
                break;
            case 1:
                arg1.set(-this.m00, -this.m10, -this.m20, 1.0 - this.m30).normalize();
                break;
            case 2:
                arg1.set(this.m01, this.m11, this.m21, 1.0 + this.m31).normalize();
                break;
            case 3:
                arg1.set(-this.m01, -this.m11, -this.m21, 1.0 - this.m31).normalize();
                break;
            case 4:
                arg1.set(this.m02, this.m12, this.m22, 1.0 + this.m32).normalize();
                break;
            case 5:
                arg1.set(-this.m02, -this.m12, -this.m22, 1.0 - this.m32).normalize();
                break;
            default:
                throw new IllegalArgumentException("which");
        }

        return arg1;
    }

    @Override
    public Vector3d positiveZ(Vector3d arg0) {
        arg0.x = this.m10 * this.m21 - this.m11 * this.m20;
        arg0.y = this.m20 * this.m01 - this.m21 * this.m00;
        arg0.z = this.m00 * this.m11 - this.m01 * this.m10;
        return arg0.normalize(arg0);
    }

    @Override
    public Vector3d normalizedPositiveZ(Vector3d arg0) {
        arg0.x = this.m02;
        arg0.y = this.m12;
        arg0.z = this.m22;
        return arg0;
    }

    @Override
    public Vector3d positiveX(Vector3d arg0) {
        arg0.x = this.m11 * this.m22 - this.m12 * this.m21;
        arg0.y = this.m02 * this.m21 - this.m01 * this.m22;
        arg0.z = this.m01 * this.m12 - this.m02 * this.m11;
        return arg0.normalize(arg0);
    }

    @Override
    public Vector3d normalizedPositiveX(Vector3d arg0) {
        arg0.x = this.m00;
        arg0.y = this.m10;
        arg0.z = this.m20;
        return arg0;
    }

    @Override
    public Vector3d positiveY(Vector3d arg0) {
        arg0.x = this.m12 * this.m20 - this.m10 * this.m22;
        arg0.y = this.m00 * this.m22 - this.m02 * this.m20;
        arg0.z = this.m02 * this.m10 - this.m00 * this.m12;
        return arg0.normalize(arg0);
    }

    @Override
    public Vector3d normalizedPositiveY(Vector3d arg0) {
        arg0.x = this.m01;
        arg0.y = this.m11;
        arg0.z = this.m21;
        return arg0;
    }

    @Override
    public Vector3d origin(Vector3d arg0) {
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

    public Matrix4x3d shadow(Vector4dc arg0, double arg1, double arg2, double arg3, double arg4) {
        return this.shadow(arg0.x(), arg0.y(), arg0.z(), arg0.w(), arg1, arg2, arg3, arg4, this);
    }

    @Override
    public Matrix4x3d shadow(Vector4dc arg0, double arg1, double arg2, double arg3, double arg4, Matrix4x3d arg5) {
        return this.shadow(arg0.x(), arg0.y(), arg0.z(), arg0.w(), arg1, arg2, arg3, arg4, arg5);
    }

    public Matrix4x3d shadow(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7) {
        return this.shadow(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, this);
    }

    @Override
    public Matrix4x3d shadow(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, Matrix4x3d arg8) {
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
        double double25 = this.m00 * double10 + this.m10 * double11 + this.m20 * double12 + this.m30 * double13;
        double double26 = this.m01 * double10 + this.m11 * double11 + this.m21 * double12 + this.m31 * double13;
        double double27 = this.m02 * double10 + this.m12 * double11 + this.m22 * double12 + this.m32 * double13;
        double double28 = this.m00 * double14 + this.m10 * double15 + this.m20 * double16 + this.m30 * double17;
        double double29 = this.m01 * double14 + this.m11 * double15 + this.m21 * double16 + this.m31 * double17;
        double double30 = this.m02 * double14 + this.m12 * double15 + this.m22 * double16 + this.m32 * double17;
        arg8.m30 = this.m00 * double18 + this.m10 * double19 + this.m20 * double20 + this.m30 * double21;
        arg8.m31 = this.m01 * double18 + this.m11 * double19 + this.m21 * double20 + this.m31 * double21;
        arg8.m32 = this.m02 * double18 + this.m12 * double19 + this.m22 * double20 + this.m32 * double21;
        arg8.m00 = double22;
        arg8.m01 = double23;
        arg8.m02 = double24;
        arg8.m10 = double25;
        arg8.m11 = double26;
        arg8.m12 = double27;
        arg8.m20 = double28;
        arg8.m21 = double29;
        arg8.m22 = double30;
        arg8.properties = this.properties & -29;
        return arg8;
    }

    @Override
    public Matrix4x3d shadow(Vector4dc arg0, Matrix4x3dc arg1, Matrix4x3d arg2) {
        double double0 = arg1.m10();
        double double1 = arg1.m11();
        double double2 = arg1.m12();
        double double3 = -double0 * arg1.m30() - double1 * arg1.m31() - double2 * arg1.m32();
        return this.shadow(arg0.x(), arg0.y(), arg0.z(), arg0.w(), double0, double1, double2, double3, arg2);
    }

    public Matrix4x3d shadow(Vector4dc arg0, Matrix4x3dc arg1) {
        return this.shadow(arg0, arg1, this);
    }

    @Override
    public Matrix4x3d shadow(double arg0, double arg1, double arg2, double arg3, Matrix4x3dc arg4, Matrix4x3d arg5) {
        double double0 = arg4.m10();
        double double1 = arg4.m11();
        double double2 = arg4.m12();
        double double3 = -double0 * arg4.m30() - double1 * arg4.m31() - double2 * arg4.m32();
        return this.shadow(arg0, arg1, arg2, arg3, double0, double1, double2, double3, arg5);
    }

    public Matrix4x3d shadow(double arg0, double arg1, double arg2, double arg3, Matrix4x3dc arg4) {
        return this.shadow(arg0, arg1, arg2, arg3, arg4, this);
    }

    public Matrix4x3d billboardCylindrical(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2) {
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
        this.m00 = double3;
        this.m01 = double4;
        this.m02 = double5;
        this.m10 = arg2.x();
        this.m11 = arg2.y();
        this.m12 = arg2.z();
        this.m20 = double0;
        this.m21 = double1;
        this.m22 = double2;
        this.m30 = arg0.x();
        this.m31 = arg0.y();
        this.m32 = arg0.z();
        this.properties = 16;
        return this;
    }

    public Matrix4x3d billboardSpherical(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2) {
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
        this.m00 = double4;
        this.m01 = double5;
        this.m02 = double6;
        this.m10 = double8;
        this.m11 = double9;
        this.m12 = double10;
        this.m20 = double0;
        this.m21 = double1;
        this.m22 = double2;
        this.m30 = arg0.x();
        this.m31 = arg0.y();
        this.m32 = arg0.z();
        this.properties = 16;
        return this;
    }

    public Matrix4x3d billboardSpherical(Vector3dc arg0, Vector3dc arg1) {
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
        this.m00 = 1.0 - double8;
        this.m01 = double9;
        this.m02 = -double11;
        this.m10 = double9;
        this.m11 = 1.0 - double7;
        this.m12 = double10;
        this.m20 = double11;
        this.m21 = -double10;
        this.m22 = 1.0 - double8 - double7;
        this.m30 = arg0.x();
        this.m31 = arg0.y();
        this.m32 = arg0.z();
        this.properties = 16;
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
        long0 = Double.doubleToLongBits(this.m10);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m11);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m12);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m20);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m21);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m22);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m30);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m31);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m32);
        return 31 * int0 + (int)(long0 ^ long0 >>> 32);
    }

    @Override
    public boolean equals(Object arg0) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (!(arg0 instanceof Matrix4x3d matrix4x3d)) {
            return false;
        } else if (Double.doubleToLongBits(this.m00) != Double.doubleToLongBits(matrix4x3d.m00)) {
            return false;
        } else if (Double.doubleToLongBits(this.m01) != Double.doubleToLongBits(matrix4x3d.m01)) {
            return false;
        } else if (Double.doubleToLongBits(this.m02) != Double.doubleToLongBits(matrix4x3d.m02)) {
            return false;
        } else if (Double.doubleToLongBits(this.m10) != Double.doubleToLongBits(matrix4x3d.m10)) {
            return false;
        } else if (Double.doubleToLongBits(this.m11) != Double.doubleToLongBits(matrix4x3d.m11)) {
            return false;
        } else if (Double.doubleToLongBits(this.m12) != Double.doubleToLongBits(matrix4x3d.m12)) {
            return false;
        } else if (Double.doubleToLongBits(this.m20) != Double.doubleToLongBits(matrix4x3d.m20)) {
            return false;
        } else if (Double.doubleToLongBits(this.m21) != Double.doubleToLongBits(matrix4x3d.m21)) {
            return false;
        } else if (Double.doubleToLongBits(this.m22) != Double.doubleToLongBits(matrix4x3d.m22)) {
            return false;
        } else if (Double.doubleToLongBits(this.m30) != Double.doubleToLongBits(matrix4x3d.m30)) {
            return false;
        } else {
            return Double.doubleToLongBits(this.m31) != Double.doubleToLongBits(matrix4x3d.m31)
                ? false
                : Double.doubleToLongBits(this.m32) == Double.doubleToLongBits(matrix4x3d.m32);
        }
    }

    @Override
    public boolean equals(Matrix4x3dc arg0, double arg1) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (!(arg0 instanceof Matrix4x3d)) {
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
    public Matrix4x3d pick(double double5, double double7, double double1, double double3, int[] ints, Matrix4x3d matrix4x3d1) {
        double double0 = ints[2] / double1;
        double double2 = ints[3] / double3;
        double double4 = (ints[2] + 2.0 * (ints[0] - double5)) / double1;
        double double6 = (ints[3] + 2.0 * (ints[1] - double7)) / double3;
        matrix4x3d1.m30 = this.m00 * double4 + this.m10 * double6 + this.m30;
        matrix4x3d1.m31 = this.m01 * double4 + this.m11 * double6 + this.m31;
        matrix4x3d1.m32 = this.m02 * double4 + this.m12 * double6 + this.m32;
        matrix4x3d1.m00 = this.m00 * double0;
        matrix4x3d1.m01 = this.m01 * double0;
        matrix4x3d1.m02 = this.m02 * double0;
        matrix4x3d1.m10 = this.m10 * double2;
        matrix4x3d1.m11 = this.m11 * double2;
        matrix4x3d1.m12 = this.m12 * double2;
        matrix4x3d1.properties = 0;
        return matrix4x3d1;
    }

    public Matrix4x3d pick(double double0, double double1, double double2, double double3, int[] ints) {
        return this.pick(double0, double1, double2, double3, ints, this);
    }

    public Matrix4x3d swap(Matrix4x3d arg0) {
        double double0 = this.m00;
        this.m00 = arg0.m00;
        arg0.m00 = double0;
        double0 = this.m01;
        this.m01 = arg0.m01;
        arg0.m01 = double0;
        double0 = this.m02;
        this.m02 = arg0.m02;
        arg0.m02 = double0;
        double0 = this.m10;
        this.m10 = arg0.m10;
        arg0.m10 = double0;
        double0 = this.m11;
        this.m11 = arg0.m11;
        arg0.m11 = double0;
        double0 = this.m12;
        this.m12 = arg0.m12;
        arg0.m12 = double0;
        double0 = this.m20;
        this.m20 = arg0.m20;
        arg0.m20 = double0;
        double0 = this.m21;
        this.m21 = arg0.m21;
        arg0.m21 = double0;
        double0 = this.m22;
        this.m22 = arg0.m22;
        arg0.m22 = double0;
        double0 = this.m30;
        this.m30 = arg0.m30;
        arg0.m30 = double0;
        double0 = this.m31;
        this.m31 = arg0.m31;
        arg0.m31 = double0;
        double0 = this.m32;
        this.m32 = arg0.m32;
        arg0.m32 = double0;
        int int0 = this.properties;
        this.properties = arg0.properties;
        arg0.properties = int0;
        return this;
    }

    @Override
    public Matrix4x3d arcball(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4x3d arg6) {
        double double0 = this.m20 * -arg0 + this.m30;
        double double1 = this.m21 * -arg0 + this.m31;
        double double2 = this.m22 * -arg0 + this.m32;
        double double3 = Math.sin(arg4);
        double double4 = Math.cosFromSin(double3, arg4);
        double double5 = this.m10 * double4 + this.m20 * double3;
        double double6 = this.m11 * double4 + this.m21 * double3;
        double double7 = this.m12 * double4 + this.m22 * double3;
        double double8 = this.m20 * double4 - this.m10 * double3;
        double double9 = this.m21 * double4 - this.m11 * double3;
        double double10 = this.m22 * double4 - this.m12 * double3;
        double3 = Math.sin(arg5);
        double4 = Math.cosFromSin(double3, arg5);
        double double11 = this.m00 * double4 - double8 * double3;
        double double12 = this.m01 * double4 - double9 * double3;
        double double13 = this.m02 * double4 - double10 * double3;
        double double14 = this.m00 * double3 + double8 * double4;
        double double15 = this.m01 * double3 + double9 * double4;
        double double16 = this.m02 * double3 + double10 * double4;
        arg6.m30 = -double11 * arg1 - double5 * arg2 - double14 * arg3 + double0;
        arg6.m31 = -double12 * arg1 - double6 * arg2 - double15 * arg3 + double1;
        arg6.m32 = -double13 * arg1 - double7 * arg2 - double16 * arg3 + double2;
        arg6.m20 = double14;
        arg6.m21 = double15;
        arg6.m22 = double16;
        arg6.m10 = double5;
        arg6.m11 = double6;
        arg6.m12 = double7;
        arg6.m00 = double11;
        arg6.m01 = double12;
        arg6.m02 = double13;
        arg6.properties = this.properties & -13;
        return arg6;
    }

    @Override
    public Matrix4x3d arcball(double arg0, Vector3dc arg1, double arg2, double arg3, Matrix4x3d arg4) {
        return this.arcball(arg0, arg1.x(), arg1.y(), arg1.z(), arg2, arg3, arg4);
    }

    public Matrix4x3d arcball(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.arcball(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    public Matrix4x3d arcball(double arg0, Vector3dc arg1, double arg2, double arg3) {
        return this.arcball(arg0, arg1.x(), arg1.y(), arg1.z(), arg2, arg3, this);
    }

    @Override
    public Matrix4x3d transformAab(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Vector3d arg6, Vector3d arg7) {
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
    public Matrix4x3d transformAab(Vector3dc arg0, Vector3dc arg1, Vector3d arg2, Vector3d arg3) {
        return this.transformAab(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2, arg3);
    }

    public Matrix4x3d lerp(Matrix4x3dc arg0, double arg1) {
        return this.lerp(arg0, arg1, this);
    }

    @Override
    public Matrix4x3d lerp(Matrix4x3dc arg0, double arg1, Matrix4x3d arg2) {
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
    public Matrix4x3d rotateTowards(Vector3dc arg0, Vector3dc arg1, Matrix4x3d arg2) {
        return this.rotateTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    public Matrix4x3d rotateTowards(Vector3dc arg0, Vector3dc arg1) {
        return this.rotateTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), this);
    }

    public Matrix4x3d rotateTowards(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.rotateTowards(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    @Override
    public Matrix4x3d rotateTowards(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4x3d arg6) {
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
        arg6.m30 = this.m30;
        arg6.m31 = this.m31;
        arg6.m32 = this.m32;
        double double11 = this.m00 * double4 + this.m10 * double5 + this.m20 * double6;
        double double12 = this.m01 * double4 + this.m11 * double5 + this.m21 * double6;
        double double13 = this.m02 * double4 + this.m12 * double5 + this.m22 * double6;
        double double14 = this.m00 * double8 + this.m10 * double9 + this.m20 * double10;
        double double15 = this.m01 * double8 + this.m11 * double9 + this.m21 * double10;
        double double16 = this.m02 * double8 + this.m12 * double9 + this.m22 * double10;
        arg6.m20 = this.m00 * double1 + this.m10 * double2 + this.m20 * double3;
        arg6.m21 = this.m01 * double1 + this.m11 * double2 + this.m21 * double3;
        arg6.m22 = this.m02 * double1 + this.m12 * double2 + this.m22 * double3;
        arg6.m00 = double11;
        arg6.m01 = double12;
        arg6.m02 = double13;
        arg6.m10 = double14;
        arg6.m11 = double15;
        arg6.m12 = double16;
        arg6.properties = this.properties & -13;
        return arg6;
    }

    public Matrix4x3d rotationTowards(Vector3dc arg0, Vector3dc arg1) {
        return this.rotationTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix4x3d rotationTowards(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
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
        this.m00 = double4;
        this.m01 = double5;
        this.m02 = double6;
        this.m10 = double8;
        this.m11 = double9;
        this.m12 = double10;
        this.m20 = double1;
        this.m21 = double2;
        this.m22 = double3;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.properties = 16;
        return this;
    }

    public Matrix4x3d translationRotateTowards(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2) {
        return this.translationRotateTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2.x(), arg2.y(), arg2.z());
    }

    public Matrix4x3d translationRotateTowards(
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
        this.m10 = double8;
        this.m11 = double9;
        this.m12 = double10;
        this.m20 = double1;
        this.m21 = double2;
        this.m22 = double3;
        this.m30 = arg0;
        this.m31 = arg1;
        this.m32 = arg2;
        this.properties = 16;
        return this;
    }

    @Override
    public Vector3d getEulerAnglesZYX(Vector3d arg0) {
        arg0.x = Math.atan2(this.m12, this.m22);
        arg0.y = Math.atan2(-this.m02, Math.sqrt(this.m12 * this.m12 + this.m22 * this.m22));
        arg0.z = Math.atan2(this.m01, this.m00);
        return arg0;
    }

    public Matrix4x3d obliqueZ(double arg0, double arg1) {
        this.m20 = this.m00 * arg0 + this.m10 * arg1 + this.m20;
        this.m21 = this.m01 * arg0 + this.m11 * arg1 + this.m21;
        this.m22 = this.m02 * arg0 + this.m12 * arg1 + this.m22;
        this.properties = 0;
        return this;
    }

    @Override
    public Matrix4x3d obliqueZ(double arg0, double arg1, Matrix4x3d arg2) {
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
