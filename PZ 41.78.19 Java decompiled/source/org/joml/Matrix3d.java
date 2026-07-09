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

public class Matrix3d implements Externalizable, Matrix3dc {
    private static final long serialVersionUID = 1L;
    public double m00;
    public double m01;
    public double m02;
    public double m10;
    public double m11;
    public double m12;
    public double m20;
    public double m21;
    public double m22;

    public Matrix3d() {
        this.m00 = 1.0;
        this.m11 = 1.0;
        this.m22 = 1.0;
    }

    public Matrix3d(Matrix2dc arg0) {
        this.set(arg0);
    }

    public Matrix3d(Matrix2fc arg0) {
        this.set(arg0);
    }

    public Matrix3d(Matrix3dc arg0) {
        this.set(arg0);
    }

    public Matrix3d(Matrix3fc arg0) {
        this.set(arg0);
    }

    public Matrix3d(Matrix4fc arg0) {
        this.set(arg0);
    }

    public Matrix3d(Matrix4dc arg0) {
        this.set(arg0);
    }

    public Matrix3d(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
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

    public Matrix3d(DoubleBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
    }

    public Matrix3d(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2) {
        this.set(arg0, arg1, arg2);
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

    public Matrix3d m00(double arg0) {
        this.m00 = arg0;
        return this;
    }

    public Matrix3d m01(double arg0) {
        this.m01 = arg0;
        return this;
    }

    public Matrix3d m02(double arg0) {
        this.m02 = arg0;
        return this;
    }

    public Matrix3d m10(double arg0) {
        this.m10 = arg0;
        return this;
    }

    public Matrix3d m11(double arg0) {
        this.m11 = arg0;
        return this;
    }

    public Matrix3d m12(double arg0) {
        this.m12 = arg0;
        return this;
    }

    public Matrix3d m20(double arg0) {
        this.m20 = arg0;
        return this;
    }

    public Matrix3d m21(double arg0) {
        this.m21 = arg0;
        return this;
    }

    public Matrix3d m22(double arg0) {
        this.m22 = arg0;
        return this;
    }

    Matrix3d _m00(double double0) {
        this.m00 = double0;
        return this;
    }

    Matrix3d _m01(double double0) {
        this.m01 = double0;
        return this;
    }

    Matrix3d _m02(double double0) {
        this.m02 = double0;
        return this;
    }

    Matrix3d _m10(double double0) {
        this.m10 = double0;
        return this;
    }

    Matrix3d _m11(double double0) {
        this.m11 = double0;
        return this;
    }

    Matrix3d _m12(double double0) {
        this.m12 = double0;
        return this;
    }

    Matrix3d _m20(double double0) {
        this.m20 = double0;
        return this;
    }

    Matrix3d _m21(double double0) {
        this.m21 = double0;
        return this;
    }

    Matrix3d _m22(double double0) {
        this.m22 = double0;
        return this;
    }

    public Matrix3d set(Matrix3dc arg0) {
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

    public Matrix3d setTransposed(Matrix3dc arg0) {
        double double0 = arg0.m01();
        double double1 = arg0.m21();
        double double2 = arg0.m02();
        double double3 = arg0.m12();
        return this._m00(arg0.m00())
            ._m01(arg0.m10())
            ._m02(arg0.m20())
            ._m10(double0)
            ._m11(arg0.m11())
            ._m12(double1)
            ._m20(double2)
            ._m21(double3)
            ._m22(arg0.m22());
    }

    public Matrix3d set(Matrix3fc arg0) {
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

    public Matrix3d setTransposed(Matrix3fc arg0) {
        float float0 = arg0.m01();
        float float1 = arg0.m21();
        float float2 = arg0.m02();
        float float3 = arg0.m12();
        return this._m00(arg0.m00())._m01(arg0.m10())._m02(arg0.m20())._m10(float0)._m11(arg0.m11())._m12(float1)._m20(float2)._m21(float3)._m22(arg0.m22());
    }

    public Matrix3d set(Matrix4x3dc arg0) {
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

    public Matrix3d set(Matrix4fc arg0) {
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

    public Matrix3d set(Matrix4dc arg0) {
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

    public Matrix3d set(Matrix2fc arg0) {
        this.m00 = arg0.m00();
        this.m01 = arg0.m01();
        this.m02 = 0.0;
        this.m10 = arg0.m10();
        this.m11 = arg0.m11();
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
        return this;
    }

    public Matrix3d set(Matrix2dc arg0) {
        this.m00 = arg0.m00();
        this.m01 = arg0.m01();
        this.m02 = 0.0;
        this.m10 = arg0.m10();
        this.m11 = arg0.m11();
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
        return this;
    }

    public Matrix3d set(AxisAngle4f arg0) {
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
        return this;
    }

    public Matrix3d set(AxisAngle4d arg0) {
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
        return this;
    }

    public Matrix3d set(Quaternionfc arg0) {
        return this.rotation(arg0);
    }

    public Matrix3d set(Quaterniondc arg0) {
        return this.rotation(arg0);
    }

    public Matrix3d mul(Matrix3dc arg0) {
        return this.mul(arg0, this);
    }

    @Override
    public Matrix3d mul(Matrix3dc arg0, Matrix3d arg1) {
        double double0 = Math.fma(this.m00, arg0.m00(), Math.fma(this.m10, arg0.m01(), this.m20 * arg0.m02()));
        double double1 = Math.fma(this.m01, arg0.m00(), Math.fma(this.m11, arg0.m01(), this.m21 * arg0.m02()));
        double double2 = Math.fma(this.m02, arg0.m00(), Math.fma(this.m12, arg0.m01(), this.m22 * arg0.m02()));
        double double3 = Math.fma(this.m00, arg0.m10(), Math.fma(this.m10, arg0.m11(), this.m20 * arg0.m12()));
        double double4 = Math.fma(this.m01, arg0.m10(), Math.fma(this.m11, arg0.m11(), this.m21 * arg0.m12()));
        double double5 = Math.fma(this.m02, arg0.m10(), Math.fma(this.m12, arg0.m11(), this.m22 * arg0.m12()));
        double double6 = Math.fma(this.m00, arg0.m20(), Math.fma(this.m10, arg0.m21(), this.m20 * arg0.m22()));
        double double7 = Math.fma(this.m01, arg0.m20(), Math.fma(this.m11, arg0.m21(), this.m21 * arg0.m22()));
        double double8 = Math.fma(this.m02, arg0.m20(), Math.fma(this.m12, arg0.m21(), this.m22 * arg0.m22()));
        arg1.m00 = double0;
        arg1.m01 = double1;
        arg1.m02 = double2;
        arg1.m10 = double3;
        arg1.m11 = double4;
        arg1.m12 = double5;
        arg1.m20 = double6;
        arg1.m21 = double7;
        arg1.m22 = double8;
        return arg1;
    }

    public Matrix3d mulLocal(Matrix3dc arg0) {
        return this.mulLocal(arg0, this);
    }

    @Override
    public Matrix3d mulLocal(Matrix3dc arg0, Matrix3d arg1) {
        double double0 = arg0.m00() * this.m00 + arg0.m10() * this.m01 + arg0.m20() * this.m02;
        double double1 = arg0.m01() * this.m00 + arg0.m11() * this.m01 + arg0.m21() * this.m02;
        double double2 = arg0.m02() * this.m00 + arg0.m12() * this.m01 + arg0.m22() * this.m02;
        double double3 = arg0.m00() * this.m10 + arg0.m10() * this.m11 + arg0.m20() * this.m12;
        double double4 = arg0.m01() * this.m10 + arg0.m11() * this.m11 + arg0.m21() * this.m12;
        double double5 = arg0.m02() * this.m10 + arg0.m12() * this.m11 + arg0.m22() * this.m12;
        double double6 = arg0.m00() * this.m20 + arg0.m10() * this.m21 + arg0.m20() * this.m22;
        double double7 = arg0.m01() * this.m20 + arg0.m11() * this.m21 + arg0.m21() * this.m22;
        double double8 = arg0.m02() * this.m20 + arg0.m12() * this.m21 + arg0.m22() * this.m22;
        arg1.m00 = double0;
        arg1.m01 = double1;
        arg1.m02 = double2;
        arg1.m10 = double3;
        arg1.m11 = double4;
        arg1.m12 = double5;
        arg1.m20 = double6;
        arg1.m21 = double7;
        arg1.m22 = double8;
        return arg1;
    }

    public Matrix3d mul(Matrix3fc arg0) {
        return this.mul(arg0, this);
    }

    @Override
    public Matrix3d mul(Matrix3fc arg0, Matrix3d arg1) {
        double double0 = Math.fma(this.m00, (double)arg0.m00(), Math.fma(this.m10, (double)arg0.m01(), this.m20 * arg0.m02()));
        double double1 = Math.fma(this.m01, (double)arg0.m00(), Math.fma(this.m11, (double)arg0.m01(), this.m21 * arg0.m02()));
        double double2 = Math.fma(this.m02, (double)arg0.m00(), Math.fma(this.m12, (double)arg0.m01(), this.m22 * arg0.m02()));
        double double3 = Math.fma(this.m00, (double)arg0.m10(), Math.fma(this.m10, (double)arg0.m11(), this.m20 * arg0.m12()));
        double double4 = Math.fma(this.m01, (double)arg0.m10(), Math.fma(this.m11, (double)arg0.m11(), this.m21 * arg0.m12()));
        double double5 = Math.fma(this.m02, (double)arg0.m10(), Math.fma(this.m12, (double)arg0.m11(), this.m22 * arg0.m12()));
        double double6 = Math.fma(this.m00, (double)arg0.m20(), Math.fma(this.m10, (double)arg0.m21(), this.m20 * arg0.m22()));
        double double7 = Math.fma(this.m01, (double)arg0.m20(), Math.fma(this.m11, (double)arg0.m21(), this.m21 * arg0.m22()));
        double double8 = Math.fma(this.m02, (double)arg0.m20(), Math.fma(this.m12, (double)arg0.m21(), this.m22 * arg0.m22()));
        arg1.m00 = double0;
        arg1.m01 = double1;
        arg1.m02 = double2;
        arg1.m10 = double3;
        arg1.m11 = double4;
        arg1.m12 = double5;
        arg1.m20 = double6;
        arg1.m21 = double7;
        arg1.m22 = double8;
        return arg1;
    }

    public Matrix3d set(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
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

    public Matrix3d set(double[] doubles) {
        this.m00 = doubles[0];
        this.m01 = doubles[1];
        this.m02 = doubles[2];
        this.m10 = doubles[3];
        this.m11 = doubles[4];
        this.m12 = doubles[5];
        this.m20 = doubles[6];
        this.m21 = doubles[7];
        this.m22 = doubles[8];
        return this;
    }

    public Matrix3d set(float[] floats) {
        this.m00 = floats[0];
        this.m01 = floats[1];
        this.m02 = floats[2];
        this.m10 = floats[3];
        this.m11 = floats[4];
        this.m12 = floats[5];
        this.m20 = floats[6];
        this.m21 = floats[7];
        this.m22 = floats[8];
        return this;
    }

    @Override
    public double determinant() {
        return (this.m00 * this.m11 - this.m01 * this.m10) * this.m22
            + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21
            + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
    }

    public Matrix3d invert() {
        return this.invert(this);
    }

    @Override
    public Matrix3d invert(Matrix3d arg0) {
        double double0 = Math.fma(this.m00, this.m11, -this.m01 * this.m10);
        double double1 = Math.fma(this.m02, this.m10, -this.m00 * this.m12);
        double double2 = Math.fma(this.m01, this.m12, -this.m02 * this.m11);
        double double3 = Math.fma(double0, this.m22, Math.fma(double1, this.m21, double2 * this.m20));
        double double4 = 1.0 / double3;
        double double5 = Math.fma(this.m11, this.m22, -this.m21 * this.m12) * double4;
        double double6 = Math.fma(this.m21, this.m02, -this.m01 * this.m22) * double4;
        double double7 = double2 * double4;
        double double8 = Math.fma(this.m20, this.m12, -this.m10 * this.m22) * double4;
        double double9 = Math.fma(this.m00, this.m22, -this.m20 * this.m02) * double4;
        double double10 = double1 * double4;
        double double11 = Math.fma(this.m10, this.m21, -this.m20 * this.m11) * double4;
        double double12 = Math.fma(this.m20, this.m01, -this.m00 * this.m21) * double4;
        double double13 = double0 * double4;
        arg0.m00 = double5;
        arg0.m01 = double6;
        arg0.m02 = double7;
        arg0.m10 = double8;
        arg0.m11 = double9;
        arg0.m12 = double10;
        arg0.m20 = double11;
        arg0.m21 = double12;
        arg0.m22 = double13;
        return arg0;
    }

    public Matrix3d transpose() {
        return this.transpose(this);
    }

    @Override
    public Matrix3d transpose(Matrix3d arg0) {
        arg0.set(this.m00, this.m10, this.m20, this.m01, this.m11, this.m21, this.m02, this.m12, this.m22);
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
    public Matrix3d get(Matrix3d arg0) {
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
    public Matrix3dc getToAddress(long arg0) {
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
        return floats;
    }

    @Override
    public float[] get(float[] floats) {
        return this.get(floats, 0);
    }

    public Matrix3d set(DoubleBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Matrix3d set(FloatBuffer arg0) {
        MemUtil.INSTANCE.getf(this, arg0.position(), arg0);
        return this;
    }

    public Matrix3d set(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Matrix3d setFloats(ByteBuffer arg0) {
        MemUtil.INSTANCE.getf(this, arg0.position(), arg0);
        return this;
    }

    public Matrix3d setFromAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.get(this, arg0);
            return this;
        }
    }

    public Matrix3d set(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2) {
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

    public Matrix3d zero() {
        this.m00 = 0.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = 0.0;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 0.0;
        return this;
    }

    public Matrix3d identity() {
        this.m00 = 1.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = 1.0;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
        return this;
    }

    public Matrix3d scaling(double arg0) {
        this.m00 = arg0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = arg0;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = arg0;
        return this;
    }

    public Matrix3d scaling(double arg0, double arg1, double arg2) {
        this.m00 = arg0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = arg1;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = arg2;
        return this;
    }

    public Matrix3d scaling(Vector3dc arg0) {
        this.m00 = arg0.x();
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = arg0.y();
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = arg0.z();
        return this;
    }

    @Override
    public Matrix3d scale(Vector3dc arg0, Matrix3d arg1) {
        return this.scale(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    public Matrix3d scale(Vector3dc arg0) {
        return this.scale(arg0.x(), arg0.y(), arg0.z(), this);
    }

    @Override
    public Matrix3d scale(double arg0, double arg1, double arg2, Matrix3d arg3) {
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

    public Matrix3d scale(double arg0, double arg1, double arg2) {
        return this.scale(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix3d scale(double arg0, Matrix3d arg1) {
        return this.scale(arg0, arg0, arg0, arg1);
    }

    public Matrix3d scale(double arg0) {
        return this.scale(arg0, arg0, arg0);
    }

    @Override
    public Matrix3d scaleLocal(double arg0, double arg1, double arg2, Matrix3d arg3) {
        double double0 = arg0 * this.m00;
        double double1 = arg1 * this.m01;
        double double2 = arg2 * this.m02;
        double double3 = arg0 * this.m10;
        double double4 = arg1 * this.m11;
        double double5 = arg2 * this.m12;
        double double6 = arg0 * this.m20;
        double double7 = arg1 * this.m21;
        double double8 = arg2 * this.m22;
        arg3.m00 = double0;
        arg3.m01 = double1;
        arg3.m02 = double2;
        arg3.m10 = double3;
        arg3.m11 = double4;
        arg3.m12 = double5;
        arg3.m20 = double6;
        arg3.m21 = double7;
        arg3.m22 = double8;
        return arg3;
    }

    public Matrix3d scaleLocal(double arg0, double arg1, double arg2) {
        return this.scaleLocal(arg0, arg1, arg2, this);
    }

    public Matrix3d rotation(double arg0, Vector3dc arg1) {
        return this.rotation(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix3d rotation(double arg0, Vector3fc arg1) {
        return this.rotation(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix3d rotation(AxisAngle4f arg0) {
        return this.rotation(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    public Matrix3d rotation(AxisAngle4d arg0) {
        return this.rotation(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    public Matrix3d rotation(double arg0, double arg1, double arg2, double arg3) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = 1.0 - double1;
        double double3 = arg1 * arg2;
        double double4 = arg1 * arg3;
        double double5 = arg2 * arg3;
        this.m00 = double1 + arg1 * arg1 * double2;
        this.m10 = double3 * double2 - arg3 * double0;
        this.m20 = double4 * double2 + arg2 * double0;
        this.m01 = double3 * double2 + arg3 * double0;
        this.m11 = double1 + arg2 * arg2 * double2;
        this.m21 = double5 * double2 - arg1 * double0;
        this.m02 = double4 * double2 - arg2 * double0;
        this.m12 = double5 * double2 + arg1 * double0;
        this.m22 = double1 + arg3 * arg3 * double2;
        return this;
    }

    public Matrix3d rotationX(double arg0) {
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
        return this;
    }

    public Matrix3d rotationY(double arg0) {
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
        return this;
    }

    public Matrix3d rotationZ(double arg0) {
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
        return this;
    }

    public Matrix3d rotationXYZ(double arg0, double arg1, double arg2) {
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
        return this;
    }

    public Matrix3d rotationZYX(double arg0, double arg1, double arg2) {
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
        return this;
    }

    public Matrix3d rotationYXZ(double arg0, double arg1, double arg2) {
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
        return this;
    }

    public Matrix3d rotation(Quaterniondc arg0) {
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
        this.m00 = double0 + double1 - double3 - double2;
        this.m01 = double7 + double5;
        this.m02 = double9 - double11;
        this.m10 = -double5 + double7;
        this.m11 = double2 - double3 + double0 - double1;
        this.m12 = double13 + double15;
        this.m20 = double11 + double9;
        this.m21 = double13 - double15;
        this.m22 = double3 - double2 - double1 + double0;
        return this;
    }

    public Matrix3d rotation(Quaternionfc arg0) {
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
        this.m00 = double0 + double1 - double3 - double2;
        this.m01 = double7 + double5;
        this.m02 = double9 - double11;
        this.m10 = -double5 + double7;
        this.m11 = double2 - double3 + double0 - double1;
        this.m12 = double13 + double15;
        this.m20 = double11 + double9;
        this.m21 = double13 - double15;
        this.m22 = double3 - double2 - double1 + double0;
        return this;
    }

    @Override
    public Vector3d transform(Vector3d arg0) {
        return arg0.mul(this);
    }

    @Override
    public Vector3d transform(Vector3dc arg0, Vector3d arg1) {
        arg0.mul(this, arg1);
        return arg1;
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
    public Vector3d transform(double arg0, double arg1, double arg2, Vector3d arg3) {
        return arg3.set(
            Math.fma(this.m00, arg0, Math.fma(this.m10, arg1, this.m20 * arg2)),
            Math.fma(this.m01, arg0, Math.fma(this.m11, arg1, this.m21 * arg2)),
            Math.fma(this.m02, arg0, Math.fma(this.m12, arg1, this.m22 * arg2))
        );
    }

    @Override
    public Vector3d transformTranspose(Vector3d arg0) {
        return arg0.mulTranspose(this);
    }

    @Override
    public Vector3d transformTranspose(Vector3dc arg0, Vector3d arg1) {
        return arg0.mulTranspose(this, arg1);
    }

    @Override
    public Vector3d transformTranspose(double arg0, double arg1, double arg2, Vector3d arg3) {
        return arg3.set(
            Math.fma(this.m00, arg0, Math.fma(this.m01, arg1, this.m02 * arg2)),
            Math.fma(this.m10, arg0, Math.fma(this.m11, arg1, this.m12 * arg2)),
            Math.fma(this.m20, arg0, Math.fma(this.m21, arg1, this.m22 * arg2))
        );
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
    }

    @Override
    public Matrix3d rotateX(double arg0, Matrix3d arg1) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = -double0;
        double double3 = this.m10 * double1 + this.m20 * double0;
        double double4 = this.m11 * double1 + this.m21 * double0;
        double double5 = this.m12 * double1 + this.m22 * double0;
        arg1.m20 = this.m10 * double2 + this.m20 * double1;
        arg1.m21 = this.m11 * double2 + this.m21 * double1;
        arg1.m22 = this.m12 * double2 + this.m22 * double1;
        arg1.m10 = double3;
        arg1.m11 = double4;
        arg1.m12 = double5;
        arg1.m00 = this.m00;
        arg1.m01 = this.m01;
        arg1.m02 = this.m02;
        return arg1;
    }

    public Matrix3d rotateX(double arg0) {
        return this.rotateX(arg0, this);
    }

    @Override
    public Matrix3d rotateY(double arg0, Matrix3d arg1) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = -double0;
        double double3 = this.m00 * double1 + this.m20 * double2;
        double double4 = this.m01 * double1 + this.m21 * double2;
        double double5 = this.m02 * double1 + this.m22 * double2;
        arg1.m20 = this.m00 * double0 + this.m20 * double1;
        arg1.m21 = this.m01 * double0 + this.m21 * double1;
        arg1.m22 = this.m02 * double0 + this.m22 * double1;
        arg1.m00 = double3;
        arg1.m01 = double4;
        arg1.m02 = double5;
        arg1.m10 = this.m10;
        arg1.m11 = this.m11;
        arg1.m12 = this.m12;
        return arg1;
    }

    public Matrix3d rotateY(double arg0) {
        return this.rotateY(arg0, this);
    }

    @Override
    public Matrix3d rotateZ(double arg0, Matrix3d arg1) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = -double0;
        double double3 = this.m00 * double1 + this.m10 * double0;
        double double4 = this.m01 * double1 + this.m11 * double0;
        double double5 = this.m02 * double1 + this.m12 * double0;
        arg1.m10 = this.m00 * double2 + this.m10 * double1;
        arg1.m11 = this.m01 * double2 + this.m11 * double1;
        arg1.m12 = this.m02 * double2 + this.m12 * double1;
        arg1.m00 = double3;
        arg1.m01 = double4;
        arg1.m02 = double5;
        arg1.m20 = this.m20;
        arg1.m21 = this.m21;
        arg1.m22 = this.m22;
        return arg1;
    }

    public Matrix3d rotateZ(double arg0) {
        return this.rotateZ(arg0, this);
    }

    public Matrix3d rotateXYZ(double arg0, double arg1, double arg2) {
        return this.rotateXYZ(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix3d rotateXYZ(double arg0, double arg1, double arg2, Matrix3d arg3) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = Math.sin(arg1);
        double double3 = Math.cosFromSin(double2, arg1);
        double double4 = Math.sin(arg2);
        double double5 = Math.cosFromSin(double4, arg2);
        double double6 = -double0;
        double double7 = -double2;
        double double8 = -double4;
        double double9 = this.m10 * double1 + this.m20 * double0;
        double double10 = this.m11 * double1 + this.m21 * double0;
        double double11 = this.m12 * double1 + this.m22 * double0;
        double double12 = this.m10 * double6 + this.m20 * double1;
        double double13 = this.m11 * double6 + this.m21 * double1;
        double double14 = this.m12 * double6 + this.m22 * double1;
        double double15 = this.m00 * double3 + double12 * double7;
        double double16 = this.m01 * double3 + double13 * double7;
        double double17 = this.m02 * double3 + double14 * double7;
        arg3.m20 = this.m00 * double2 + double12 * double3;
        arg3.m21 = this.m01 * double2 + double13 * double3;
        arg3.m22 = this.m02 * double2 + double14 * double3;
        arg3.m00 = double15 * double5 + double9 * double4;
        arg3.m01 = double16 * double5 + double10 * double4;
        arg3.m02 = double17 * double5 + double11 * double4;
        arg3.m10 = double15 * double8 + double9 * double5;
        arg3.m11 = double16 * double8 + double10 * double5;
        arg3.m12 = double17 * double8 + double11 * double5;
        return arg3;
    }

    public Matrix3d rotateZYX(double arg0, double arg1, double arg2) {
        return this.rotateZYX(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix3d rotateZYX(double arg0, double arg1, double arg2, Matrix3d arg3) {
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
        arg3.m00 = double9 * double3 + this.m20 * double7;
        arg3.m01 = double10 * double3 + this.m21 * double7;
        arg3.m02 = double11 * double3 + this.m22 * double7;
        arg3.m10 = double12 * double1 + double15 * double0;
        arg3.m11 = double13 * double1 + double16 * double0;
        arg3.m12 = double14 * double1 + double17 * double0;
        arg3.m20 = double12 * double8 + double15 * double1;
        arg3.m21 = double13 * double8 + double16 * double1;
        arg3.m22 = double14 * double8 + double17 * double1;
        return arg3;
    }

    public Matrix3d rotateYXZ(Vector3d arg0) {
        return this.rotateYXZ(arg0.y, arg0.x, arg0.z);
    }

    public Matrix3d rotateYXZ(double arg0, double arg1, double arg2) {
        return this.rotateYXZ(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix3d rotateYXZ(double arg0, double arg1, double arg2, Matrix3d arg3) {
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
        arg3.m20 = this.m10 * double7 + double9 * double1;
        arg3.m21 = this.m11 * double7 + double10 * double1;
        arg3.m22 = this.m12 * double7 + double11 * double1;
        arg3.m00 = double12 * double5 + double15 * double4;
        arg3.m01 = double13 * double5 + double16 * double4;
        arg3.m02 = double14 * double5 + double17 * double4;
        arg3.m10 = double12 * double8 + double15 * double5;
        arg3.m11 = double13 * double8 + double16 * double5;
        arg3.m12 = double14 * double8 + double17 * double5;
        return arg3;
    }

    public Matrix3d rotate(double arg0, double arg1, double arg2, double arg3) {
        return this.rotate(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix3d rotate(double arg0, double arg1, double arg2, double arg3, Matrix3d arg4) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = 1.0 - double1;
        double double3 = arg1 * arg1;
        double double4 = arg1 * arg2;
        double double5 = arg1 * arg3;
        double double6 = arg2 * arg2;
        double double7 = arg2 * arg3;
        double double8 = arg3 * arg3;
        double double9 = double3 * double2 + double1;
        double double10 = double4 * double2 + arg3 * double0;
        double double11 = double5 * double2 - arg2 * double0;
        double double12 = double4 * double2 - arg3 * double0;
        double double13 = double6 * double2 + double1;
        double double14 = double7 * double2 + arg1 * double0;
        double double15 = double5 * double2 + arg2 * double0;
        double double16 = double7 * double2 - arg1 * double0;
        double double17 = double8 * double2 + double1;
        double double18 = this.m00 * double9 + this.m10 * double10 + this.m20 * double11;
        double double19 = this.m01 * double9 + this.m11 * double10 + this.m21 * double11;
        double double20 = this.m02 * double9 + this.m12 * double10 + this.m22 * double11;
        double double21 = this.m00 * double12 + this.m10 * double13 + this.m20 * double14;
        double double22 = this.m01 * double12 + this.m11 * double13 + this.m21 * double14;
        double double23 = this.m02 * double12 + this.m12 * double13 + this.m22 * double14;
        arg4.m20 = this.m00 * double15 + this.m10 * double16 + this.m20 * double17;
        arg4.m21 = this.m01 * double15 + this.m11 * double16 + this.m21 * double17;
        arg4.m22 = this.m02 * double15 + this.m12 * double16 + this.m22 * double17;
        arg4.m00 = double18;
        arg4.m01 = double19;
        arg4.m02 = double20;
        arg4.m10 = double21;
        arg4.m11 = double22;
        arg4.m12 = double23;
        return arg4;
    }

    @Override
    public Matrix3d rotateLocal(double arg0, double arg1, double arg2, double arg3, Matrix3d arg4) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = 1.0 - double1;
        double double3 = arg1 * arg1;
        double double4 = arg1 * arg2;
        double double5 = arg1 * arg3;
        double double6 = arg2 * arg2;
        double double7 = arg2 * arg3;
        double double8 = arg3 * arg3;
        double double9 = double3 * double2 + double1;
        double double10 = double4 * double2 + arg3 * double0;
        double double11 = double5 * double2 - arg2 * double0;
        double double12 = double4 * double2 - arg3 * double0;
        double double13 = double6 * double2 + double1;
        double double14 = double7 * double2 + arg1 * double0;
        double double15 = double5 * double2 + arg2 * double0;
        double double16 = double7 * double2 - arg1 * double0;
        double double17 = double8 * double2 + double1;
        double double18 = double9 * this.m00 + double12 * this.m01 + double15 * this.m02;
        double double19 = double10 * this.m00 + double13 * this.m01 + double16 * this.m02;
        double double20 = double11 * this.m00 + double14 * this.m01 + double17 * this.m02;
        double double21 = double9 * this.m10 + double12 * this.m11 + double15 * this.m12;
        double double22 = double10 * this.m10 + double13 * this.m11 + double16 * this.m12;
        double double23 = double11 * this.m10 + double14 * this.m11 + double17 * this.m12;
        double double24 = double9 * this.m20 + double12 * this.m21 + double15 * this.m22;
        double double25 = double10 * this.m20 + double13 * this.m21 + double16 * this.m22;
        double double26 = double11 * this.m20 + double14 * this.m21 + double17 * this.m22;
        arg4.m00 = double18;
        arg4.m01 = double19;
        arg4.m02 = double20;
        arg4.m10 = double21;
        arg4.m11 = double22;
        arg4.m12 = double23;
        arg4.m20 = double24;
        arg4.m21 = double25;
        arg4.m22 = double26;
        return arg4;
    }

    public Matrix3d rotateLocal(double arg0, double arg1, double arg2, double arg3) {
        return this.rotateLocal(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix3d rotateLocalX(double arg0, Matrix3d arg1) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = double1 * this.m01 - double0 * this.m02;
        double double3 = double0 * this.m01 + double1 * this.m02;
        double double4 = double1 * this.m11 - double0 * this.m12;
        double double5 = double0 * this.m11 + double1 * this.m12;
        double double6 = double1 * this.m21 - double0 * this.m22;
        double double7 = double0 * this.m21 + double1 * this.m22;
        arg1.m00 = this.m00;
        arg1.m01 = double2;
        arg1.m02 = double3;
        arg1.m10 = this.m10;
        arg1.m11 = double4;
        arg1.m12 = double5;
        arg1.m20 = this.m20;
        arg1.m21 = double6;
        arg1.m22 = double7;
        return arg1;
    }

    public Matrix3d rotateLocalX(double arg0) {
        return this.rotateLocalX(arg0, this);
    }

    @Override
    public Matrix3d rotateLocalY(double arg0, Matrix3d arg1) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = double1 * this.m00 + double0 * this.m02;
        double double3 = -double0 * this.m00 + double1 * this.m02;
        double double4 = double1 * this.m10 + double0 * this.m12;
        double double5 = -double0 * this.m10 + double1 * this.m12;
        double double6 = double1 * this.m20 + double0 * this.m22;
        double double7 = -double0 * this.m20 + double1 * this.m22;
        arg1.m00 = double2;
        arg1.m01 = this.m01;
        arg1.m02 = double3;
        arg1.m10 = double4;
        arg1.m11 = this.m11;
        arg1.m12 = double5;
        arg1.m20 = double6;
        arg1.m21 = this.m21;
        arg1.m22 = double7;
        return arg1;
    }

    public Matrix3d rotateLocalY(double arg0) {
        return this.rotateLocalY(arg0, this);
    }

    @Override
    public Matrix3d rotateLocalZ(double arg0, Matrix3d arg1) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = double1 * this.m00 - double0 * this.m01;
        double double3 = double0 * this.m00 + double1 * this.m01;
        double double4 = double1 * this.m10 - double0 * this.m11;
        double double5 = double0 * this.m10 + double1 * this.m11;
        double double6 = double1 * this.m20 - double0 * this.m21;
        double double7 = double0 * this.m20 + double1 * this.m21;
        arg1.m00 = double2;
        arg1.m01 = double3;
        arg1.m02 = this.m02;
        arg1.m10 = double4;
        arg1.m11 = double5;
        arg1.m12 = this.m12;
        arg1.m20 = double6;
        arg1.m21 = double7;
        arg1.m22 = this.m22;
        return arg1;
    }

    public Matrix3d rotateLocalZ(double arg0) {
        return this.rotateLocalZ(arg0, this);
    }

    @Override
    public Matrix3d rotateLocal(Quaterniondc arg0, Matrix3d arg1) {
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
        arg1.m00 = double25;
        arg1.m01 = double26;
        arg1.m02 = double27;
        arg1.m10 = double28;
        arg1.m11 = double29;
        arg1.m12 = double30;
        arg1.m20 = double31;
        arg1.m21 = double32;
        arg1.m22 = double33;
        return arg1;
    }

    public Matrix3d rotateLocal(Quaterniondc arg0) {
        return this.rotateLocal(arg0, this);
    }

    @Override
    public Matrix3d rotateLocal(Quaternionfc arg0, Matrix3d arg1) {
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
        arg1.m00 = double25;
        arg1.m01 = double26;
        arg1.m02 = double27;
        arg1.m10 = double28;
        arg1.m11 = double29;
        arg1.m12 = double30;
        arg1.m20 = double31;
        arg1.m21 = double32;
        arg1.m22 = double33;
        return arg1;
    }

    public Matrix3d rotateLocal(Quaternionfc arg0) {
        return this.rotateLocal(arg0, this);
    }

    public Matrix3d rotate(Quaterniondc arg0) {
        return this.rotate(arg0, this);
    }

    @Override
    public Matrix3d rotate(Quaterniondc arg0, Matrix3d arg1) {
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
        double double25 = this.m00 * double16 + this.m10 * double17 + this.m20 * double18;
        double double26 = this.m01 * double16 + this.m11 * double17 + this.m21 * double18;
        double double27 = this.m02 * double16 + this.m12 * double17 + this.m22 * double18;
        double double28 = this.m00 * double19 + this.m10 * double20 + this.m20 * double21;
        double double29 = this.m01 * double19 + this.m11 * double20 + this.m21 * double21;
        double double30 = this.m02 * double19 + this.m12 * double20 + this.m22 * double21;
        arg1.m20 = this.m00 * double22 + this.m10 * double23 + this.m20 * double24;
        arg1.m21 = this.m01 * double22 + this.m11 * double23 + this.m21 * double24;
        arg1.m22 = this.m02 * double22 + this.m12 * double23 + this.m22 * double24;
        arg1.m00 = double25;
        arg1.m01 = double26;
        arg1.m02 = double27;
        arg1.m10 = double28;
        arg1.m11 = double29;
        arg1.m12 = double30;
        return arg1;
    }

    public Matrix3d rotate(Quaternionfc arg0) {
        return this.rotate(arg0, this);
    }

    @Override
    public Matrix3d rotate(Quaternionfc arg0, Matrix3d arg1) {
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
        double double25 = this.m00 * double16 + this.m10 * double17 + this.m20 * double18;
        double double26 = this.m01 * double16 + this.m11 * double17 + this.m21 * double18;
        double double27 = this.m02 * double16 + this.m12 * double17 + this.m22 * double18;
        double double28 = this.m00 * double19 + this.m10 * double20 + this.m20 * double21;
        double double29 = this.m01 * double19 + this.m11 * double20 + this.m21 * double21;
        double double30 = this.m02 * double19 + this.m12 * double20 + this.m22 * double21;
        arg1.m20 = this.m00 * double22 + this.m10 * double23 + this.m20 * double24;
        arg1.m21 = this.m01 * double22 + this.m11 * double23 + this.m21 * double24;
        arg1.m22 = this.m02 * double22 + this.m12 * double23 + this.m22 * double24;
        arg1.m00 = double25;
        arg1.m01 = double26;
        arg1.m02 = double27;
        arg1.m10 = double28;
        arg1.m11 = double29;
        arg1.m12 = double30;
        return arg1;
    }

    public Matrix3d rotate(AxisAngle4f arg0) {
        return this.rotate(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    @Override
    public Matrix3d rotate(AxisAngle4f arg0, Matrix3d arg1) {
        return this.rotate(arg0.angle, arg0.x, arg0.y, arg0.z, arg1);
    }

    public Matrix3d rotate(AxisAngle4d arg0) {
        return this.rotate(arg0.angle, arg0.x, arg0.y, arg0.z);
    }

    @Override
    public Matrix3d rotate(AxisAngle4d arg0, Matrix3d arg1) {
        return this.rotate(arg0.angle, arg0.x, arg0.y, arg0.z, arg1);
    }

    public Matrix3d rotate(double arg0, Vector3dc arg1) {
        return this.rotate(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    @Override
    public Matrix3d rotate(double arg0, Vector3dc arg1, Matrix3d arg2) {
        return this.rotate(arg0, arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    public Matrix3d rotate(double arg0, Vector3fc arg1) {
        return this.rotate(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    @Override
    public Matrix3d rotate(double arg0, Vector3fc arg1, Matrix3d arg2) {
        return this.rotate(arg0, arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    @Override
    public Vector3d getRow(int arg0, Vector3d arg1) throws IndexOutOfBoundsException {
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

    public Matrix3d setRow(int arg0, Vector3dc arg1) throws IndexOutOfBoundsException {
        return this.setRow(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix3d setRow(int arg0, double arg1, double arg2, double arg3) throws IndexOutOfBoundsException {
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
    public Vector3d getColumn(int arg0, Vector3d arg1) throws IndexOutOfBoundsException {
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

    public Matrix3d setColumn(int arg0, Vector3dc arg1) throws IndexOutOfBoundsException {
        return this.setColumn(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix3d setColumn(int arg0, double arg1, double arg2, double arg3) throws IndexOutOfBoundsException {
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
    public double get(int arg0, int arg1) {
        return MemUtil.INSTANCE.get(this, arg0, arg1);
    }

    public Matrix3d set(int arg0, int arg1, double arg2) {
        return MemUtil.INSTANCE.set(this, arg0, arg1, arg2);
    }

    @Override
    public double getRowColumn(int arg0, int arg1) {
        return MemUtil.INSTANCE.get(this, arg1, arg0);
    }

    public Matrix3d setRowColumn(int arg0, int arg1, double arg2) {
        return MemUtil.INSTANCE.set(this, arg1, arg0, arg2);
    }

    public Matrix3d normal() {
        return this.normal(this);
    }

    @Override
    public Matrix3d normal(Matrix3d arg0) {
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
        arg0.m00 = double8;
        arg0.m01 = double9;
        arg0.m02 = double10;
        arg0.m10 = double11;
        arg0.m11 = double12;
        arg0.m12 = double13;
        arg0.m20 = double14;
        arg0.m21 = double15;
        arg0.m22 = double16;
        return arg0;
    }

    public Matrix3d cofactor() {
        return this.cofactor(this);
    }

    @Override
    public Matrix3d cofactor(Matrix3d arg0) {
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
        return arg0;
    }

    public Matrix3d lookAlong(Vector3dc arg0, Vector3dc arg1) {
        return this.lookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), this);
    }

    @Override
    public Matrix3d lookAlong(Vector3dc arg0, Vector3dc arg1, Matrix3d arg2) {
        return this.lookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    @Override
    public Matrix3d lookAlong(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix3d arg6) {
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
        return arg6;
    }

    public Matrix3d lookAlong(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.lookAlong(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    public Matrix3d setLookAlong(Vector3dc arg0, Vector3dc arg1) {
        return this.setLookAlong(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix3d setLookAlong(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
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
        return this;
    }

    @Override
    public Vector3d getScale(Vector3d arg0) {
        arg0.x = Math.sqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02);
        arg0.y = Math.sqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12);
        arg0.z = Math.sqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22);
        return arg0;
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
            Matrix3d matrix3d = (Matrix3d)arg0;
            if (Double.doubleToLongBits(this.m00) != Double.doubleToLongBits(matrix3d.m00)) {
                return false;
            } else if (Double.doubleToLongBits(this.m01) != Double.doubleToLongBits(matrix3d.m01)) {
                return false;
            } else if (Double.doubleToLongBits(this.m02) != Double.doubleToLongBits(matrix3d.m02)) {
                return false;
            } else if (Double.doubleToLongBits(this.m10) != Double.doubleToLongBits(matrix3d.m10)) {
                return false;
            } else if (Double.doubleToLongBits(this.m11) != Double.doubleToLongBits(matrix3d.m11)) {
                return false;
            } else if (Double.doubleToLongBits(this.m12) != Double.doubleToLongBits(matrix3d.m12)) {
                return false;
            } else if (Double.doubleToLongBits(this.m20) != Double.doubleToLongBits(matrix3d.m20)) {
                return false;
            } else {
                return Double.doubleToLongBits(this.m21) != Double.doubleToLongBits(matrix3d.m21)
                    ? false
                    : Double.doubleToLongBits(this.m22) == Double.doubleToLongBits(matrix3d.m22);
            }
        }
    }

    @Override
    public boolean equals(Matrix3dc arg0, double arg1) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (!(arg0 instanceof Matrix3d)) {
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

    public Matrix3d swap(Matrix3d arg0) {
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
        return this;
    }

    public Matrix3d add(Matrix3dc arg0) {
        return this.add(arg0, this);
    }

    @Override
    public Matrix3d add(Matrix3dc arg0, Matrix3d arg1) {
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

    public Matrix3d sub(Matrix3dc arg0) {
        return this.sub(arg0, this);
    }

    @Override
    public Matrix3d sub(Matrix3dc arg0, Matrix3d arg1) {
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

    public Matrix3d mulComponentWise(Matrix3dc arg0) {
        return this.mulComponentWise(arg0, this);
    }

    @Override
    public Matrix3d mulComponentWise(Matrix3dc arg0, Matrix3d arg1) {
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

    public Matrix3d setSkewSymmetric(double arg0, double arg1, double arg2) {
        this.m00 = this.m11 = this.m22 = 0.0;
        this.m01 = -arg0;
        this.m02 = arg1;
        this.m10 = arg0;
        this.m12 = -arg2;
        this.m20 = -arg1;
        this.m21 = arg2;
        return this;
    }

    public Matrix3d lerp(Matrix3dc arg0, double arg1) {
        return this.lerp(arg0, arg1, this);
    }

    @Override
    public Matrix3d lerp(Matrix3dc arg0, double arg1, Matrix3d arg2) {
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
    public Matrix3d rotateTowards(Vector3dc arg0, Vector3dc arg1, Matrix3d arg2) {
        return this.rotateTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), arg2);
    }

    public Matrix3d rotateTowards(Vector3dc arg0, Vector3dc arg1) {
        return this.rotateTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z(), this);
    }

    public Matrix3d rotateTowards(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.rotateTowards(arg0, arg1, arg2, arg3, arg4, arg5, this);
    }

    @Override
    public Matrix3d rotateTowards(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix3d arg6) {
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
        return arg6;
    }

    public Matrix3d rotationTowards(Vector3dc arg0, Vector3dc arg1) {
        return this.rotationTowards(arg0.x(), arg0.y(), arg0.z(), arg1.x(), arg1.y(), arg1.z());
    }

    public Matrix3d rotationTowards(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
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
        return this;
    }

    @Override
    public Vector3d getEulerAnglesZYX(Vector3d arg0) {
        arg0.x = (float)Math.atan2(this.m12, this.m22);
        arg0.y = (float)Math.atan2(-this.m02, Math.sqrt(this.m12 * this.m12 + this.m22 * this.m22));
        arg0.z = (float)Math.atan2(this.m01, this.m00);
        return arg0;
    }

    public Matrix3d obliqueZ(double arg0, double arg1) {
        this.m20 = this.m00 * arg0 + this.m10 * arg1 + this.m20;
        this.m21 = this.m01 * arg0 + this.m11 * arg1 + this.m21;
        this.m22 = this.m02 * arg0 + this.m12 * arg1 + this.m22;
        return this;
    }

    @Override
    public Matrix3d obliqueZ(double arg0, double arg1, Matrix3d arg2) {
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
    public Matrix3d reflect(double arg0, double arg1, double arg2, Matrix3d arg3) {
        double double0 = arg0 + arg0;
        double double1 = arg1 + arg1;
        double double2 = arg2 + arg2;
        double double3 = 1.0 - double0 * arg0;
        double double4 = -double0 * arg1;
        double double5 = -double0 * arg2;
        double double6 = -double1 * arg0;
        double double7 = 1.0 - double1 * arg1;
        double double8 = -double1 * arg2;
        double double9 = -double2 * arg0;
        double double10 = -double2 * arg1;
        double double11 = 1.0 - double2 * arg2;
        double double12 = this.m00 * double3 + this.m10 * double4 + this.m20 * double5;
        double double13 = this.m01 * double3 + this.m11 * double4 + this.m21 * double5;
        double double14 = this.m02 * double3 + this.m12 * double4 + this.m22 * double5;
        double double15 = this.m00 * double6 + this.m10 * double7 + this.m20 * double8;
        double double16 = this.m01 * double6 + this.m11 * double7 + this.m21 * double8;
        double double17 = this.m02 * double6 + this.m12 * double7 + this.m22 * double8;
        return arg3._m20(this.m00 * double9 + this.m10 * double10 + this.m20 * double11)
            ._m21(this.m01 * double9 + this.m11 * double10 + this.m21 * double11)
            ._m22(this.m02 * double9 + this.m12 * double10 + this.m22 * double11)
            ._m00(double12)
            ._m01(double13)
            ._m02(double14)
            ._m10(double15)
            ._m11(double16)
            ._m12(double17);
    }

    public Matrix3d reflect(double arg0, double arg1, double arg2) {
        return this.reflect(arg0, arg1, arg2, this);
    }

    public Matrix3d reflect(Vector3dc arg0) {
        return this.reflect(arg0.x(), arg0.y(), arg0.z());
    }

    public Matrix3d reflect(Quaterniondc arg0) {
        return this.reflect(arg0, this);
    }

    @Override
    public Matrix3d reflect(Quaterniondc arg0, Matrix3d arg1) {
        double double0 = arg0.x() + arg0.x();
        double double1 = arg0.y() + arg0.y();
        double double2 = arg0.z() + arg0.z();
        double double3 = arg0.x() * double2 + arg0.w() * double1;
        double double4 = arg0.y() * double2 - arg0.w() * double0;
        double double5 = 1.0 - (arg0.x() * double0 + arg0.y() * double1);
        return this.reflect(double3, double4, double5, arg1);
    }

    @Override
    public Matrix3d reflect(Vector3dc arg0, Matrix3d arg1) {
        return this.reflect(arg0.x(), arg0.y(), arg0.z(), arg1);
    }

    public Matrix3d reflection(double arg0, double arg1, double arg2) {
        double double0 = arg0 + arg0;
        double double1 = arg1 + arg1;
        double double2 = arg2 + arg2;
        this._m00(1.0 - double0 * arg0);
        this._m01(-double0 * arg1);
        this._m02(-double0 * arg2);
        this._m10(-double1 * arg0);
        this._m11(1.0 - double1 * arg1);
        this._m12(-double1 * arg2);
        this._m20(-double2 * arg0);
        this._m21(-double2 * arg1);
        this._m22(1.0 - double2 * arg2);
        return this;
    }

    public Matrix3d reflection(Vector3dc arg0) {
        return this.reflection(arg0.x(), arg0.y(), arg0.z());
    }

    public Matrix3d reflection(Quaterniondc arg0) {
        double double0 = arg0.x() + arg0.x();
        double double1 = arg0.y() + arg0.y();
        double double2 = arg0.z() + arg0.z();
        double double3 = arg0.x() * double2 + arg0.w() * double1;
        double double4 = arg0.y() * double2 - arg0.w() * double0;
        double double5 = 1.0 - (arg0.x() * double0 + arg0.y() * double1);
        return this.reflection(double3, double4, double5);
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
    public double quadraticFormProduct(double arg0, double arg1, double arg2) {
        double double0 = this.m00 * arg0 + this.m10 * arg1 + this.m20 * arg2;
        double double1 = this.m01 * arg0 + this.m11 * arg1 + this.m21 * arg2;
        double double2 = this.m02 * arg0 + this.m12 * arg1 + this.m22 * arg2;
        return arg0 * double0 + arg1 * double1 + arg2 * double2;
    }

    @Override
    public double quadraticFormProduct(Vector3dc arg0) {
        return this.quadraticFormProduct(arg0.x(), arg0.y(), arg0.z());
    }

    @Override
    public double quadraticFormProduct(Vector3fc arg0) {
        return this.quadraticFormProduct(arg0.x(), arg0.y(), arg0.z());
    }
}
