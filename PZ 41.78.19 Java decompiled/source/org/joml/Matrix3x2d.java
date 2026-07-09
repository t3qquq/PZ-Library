// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Matrix3x2d implements Matrix3x2dc, Externalizable {
    private static final long serialVersionUID = 1L;
    public double m00;
    public double m01;
    public double m10;
    public double m11;
    public double m20;
    public double m21;

    public Matrix3x2d() {
        this.m00 = 1.0;
        this.m11 = 1.0;
    }

    public Matrix3x2d(Matrix2dc arg0) {
        if (arg0 instanceof Matrix2d) {
            MemUtil.INSTANCE.copy((Matrix2d)arg0, this);
        } else {
            this.setMatrix2dc(arg0);
        }
    }

    public Matrix3x2d(Matrix2fc arg0) {
        this.m00 = arg0.m00();
        this.m01 = arg0.m01();
        this.m10 = arg0.m10();
        this.m11 = arg0.m11();
    }

    public Matrix3x2d(Matrix3x2dc arg0) {
        if (arg0 instanceof Matrix3x2d) {
            MemUtil.INSTANCE.copy((Matrix3x2d)arg0, this);
        } else {
            this.setMatrix3x2dc(arg0);
        }
    }

    public Matrix3x2d(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        this.m00 = arg0;
        this.m01 = arg1;
        this.m10 = arg2;
        this.m11 = arg3;
        this.m20 = arg4;
        this.m21 = arg5;
    }

    public Matrix3x2d(DoubleBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
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
    public double m10() {
        return this.m10;
    }

    @Override
    public double m11() {
        return this.m11;
    }

    @Override
    public double m20() {
        return this.m20;
    }

    @Override
    public double m21() {
        return this.m21;
    }

    Matrix3x2d _m00(double double0) {
        this.m00 = double0;
        return this;
    }

    Matrix3x2d _m01(double double0) {
        this.m01 = double0;
        return this;
    }

    Matrix3x2d _m10(double double0) {
        this.m10 = double0;
        return this;
    }

    Matrix3x2d _m11(double double0) {
        this.m11 = double0;
        return this;
    }

    Matrix3x2d _m20(double double0) {
        this.m20 = double0;
        return this;
    }

    Matrix3x2d _m21(double double0) {
        this.m21 = double0;
        return this;
    }

    public Matrix3x2d set(Matrix3x2dc arg0) {
        if (arg0 instanceof Matrix3x2d) {
            MemUtil.INSTANCE.copy((Matrix3x2d)arg0, this);
        } else {
            this.setMatrix3x2dc(arg0);
        }

        return this;
    }

    private void setMatrix3x2dc(Matrix3x2dc matrix3x2dc) {
        this.m00 = matrix3x2dc.m00();
        this.m01 = matrix3x2dc.m01();
        this.m10 = matrix3x2dc.m10();
        this.m11 = matrix3x2dc.m11();
        this.m20 = matrix3x2dc.m20();
        this.m21 = matrix3x2dc.m21();
    }

    public Matrix3x2d set(Matrix2dc arg0) {
        if (arg0 instanceof Matrix2d) {
            MemUtil.INSTANCE.copy((Matrix2d)arg0, this);
        } else {
            this.setMatrix2dc(arg0);
        }

        return this;
    }

    private void setMatrix2dc(Matrix2dc matrix2dc) {
        this.m00 = matrix2dc.m00();
        this.m01 = matrix2dc.m01();
        this.m10 = matrix2dc.m10();
        this.m11 = matrix2dc.m11();
    }

    public Matrix3x2d set(Matrix2fc arg0) {
        this.m00 = arg0.m00();
        this.m01 = arg0.m01();
        this.m10 = arg0.m10();
        this.m11 = arg0.m11();
        return this;
    }

    public Matrix3x2d mul(Matrix3x2dc arg0) {
        return this.mul(arg0, this);
    }

    @Override
    public Matrix3x2d mul(Matrix3x2dc arg0, Matrix3x2d arg1) {
        double double0 = this.m00 * arg0.m00() + this.m10 * arg0.m01();
        double double1 = this.m01 * arg0.m00() + this.m11 * arg0.m01();
        double double2 = this.m00 * arg0.m10() + this.m10 * arg0.m11();
        double double3 = this.m01 * arg0.m10() + this.m11 * arg0.m11();
        double double4 = this.m00 * arg0.m20() + this.m10 * arg0.m21() + this.m20;
        double double5 = this.m01 * arg0.m20() + this.m11 * arg0.m21() + this.m21;
        arg1.m00 = double0;
        arg1.m01 = double1;
        arg1.m10 = double2;
        arg1.m11 = double3;
        arg1.m20 = double4;
        arg1.m21 = double5;
        return arg1;
    }

    public Matrix3x2d mulLocal(Matrix3x2dc arg0) {
        return this.mulLocal(arg0, this);
    }

    @Override
    public Matrix3x2d mulLocal(Matrix3x2dc arg0, Matrix3x2d arg1) {
        double double0 = arg0.m00() * this.m00 + arg0.m10() * this.m01;
        double double1 = arg0.m01() * this.m00 + arg0.m11() * this.m01;
        double double2 = arg0.m00() * this.m10 + arg0.m10() * this.m11;
        double double3 = arg0.m01() * this.m10 + arg0.m11() * this.m11;
        double double4 = arg0.m00() * this.m20 + arg0.m10() * this.m21 + arg0.m20();
        double double5 = arg0.m01() * this.m20 + arg0.m11() * this.m21 + arg0.m21();
        arg1.m00 = double0;
        arg1.m01 = double1;
        arg1.m10 = double2;
        arg1.m11 = double3;
        arg1.m20 = double4;
        arg1.m21 = double5;
        return arg1;
    }

    public Matrix3x2d set(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        this.m00 = arg0;
        this.m01 = arg1;
        this.m10 = arg2;
        this.m11 = arg3;
        this.m20 = arg4;
        this.m21 = arg5;
        return this;
    }

    public Matrix3x2d set(double[] doubles) {
        MemUtil.INSTANCE.copy(doubles, 0, this);
        return this;
    }

    @Override
    public double determinant() {
        return this.m00 * this.m11 - this.m01 * this.m10;
    }

    public Matrix3x2d invert() {
        return this.invert(this);
    }

    @Override
    public Matrix3x2d invert(Matrix3x2d arg0) {
        double double0 = 1.0 / (this.m00 * this.m11 - this.m01 * this.m10);
        double double1 = this.m11 * double0;
        double double2 = -this.m01 * double0;
        double double3 = -this.m10 * double0;
        double double4 = this.m00 * double0;
        double double5 = (this.m10 * this.m21 - this.m20 * this.m11) * double0;
        double double6 = (this.m20 * this.m01 - this.m00 * this.m21) * double0;
        arg0.m00 = double1;
        arg0.m01 = double2;
        arg0.m10 = double3;
        arg0.m11 = double4;
        arg0.m20 = double5;
        arg0.m21 = double6;
        return arg0;
    }

    public Matrix3x2d translation(double arg0, double arg1) {
        this.m00 = 1.0;
        this.m01 = 0.0;
        this.m10 = 0.0;
        this.m11 = 1.0;
        this.m20 = arg0;
        this.m21 = arg1;
        return this;
    }

    public Matrix3x2d translation(Vector2dc arg0) {
        return this.translation(arg0.x(), arg0.y());
    }

    public Matrix3x2d setTranslation(double arg0, double arg1) {
        this.m20 = arg0;
        this.m21 = arg1;
        return this;
    }

    public Matrix3x2d setTranslation(Vector2dc arg0) {
        return this.setTranslation(arg0.x(), arg0.y());
    }

    @Override
    public Matrix3x2d translate(double arg0, double arg1, Matrix3x2d arg2) {
        arg2.m20 = this.m00 * arg0 + this.m10 * arg1 + this.m20;
        arg2.m21 = this.m01 * arg0 + this.m11 * arg1 + this.m21;
        arg2.m00 = this.m00;
        arg2.m01 = this.m01;
        arg2.m10 = this.m10;
        arg2.m11 = this.m11;
        return arg2;
    }

    public Matrix3x2d translate(double arg0, double arg1) {
        return this.translate(arg0, arg1, this);
    }

    @Override
    public Matrix3x2d translate(Vector2dc arg0, Matrix3x2d arg1) {
        return this.translate(arg0.x(), arg0.y(), arg1);
    }

    public Matrix3x2d translate(Vector2dc arg0) {
        return this.translate(arg0.x(), arg0.y(), this);
    }

    public Matrix3x2d translateLocal(Vector2dc arg0) {
        return this.translateLocal(arg0.x(), arg0.y());
    }

    @Override
    public Matrix3x2d translateLocal(Vector2dc arg0, Matrix3x2d arg1) {
        return this.translateLocal(arg0.x(), arg0.y(), arg1);
    }

    @Override
    public Matrix3x2d translateLocal(double arg0, double arg1, Matrix3x2d arg2) {
        arg2.m00 = this.m00;
        arg2.m01 = this.m01;
        arg2.m10 = this.m10;
        arg2.m11 = this.m11;
        arg2.m20 = this.m20 + arg0;
        arg2.m21 = this.m21 + arg1;
        return arg2;
    }

    public Matrix3x2d translateLocal(double arg0, double arg1) {
        return this.translateLocal(arg0, arg1, this);
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
            + "\n";
    }

    @Override
    public Matrix3x2d get(Matrix3x2d arg0) {
        return arg0.set(this);
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
    public ByteBuffer get(ByteBuffer arg0) {
        return this.get(arg0.position(), arg0);
    }

    @Override
    public ByteBuffer get(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.put(this, arg0, arg1);
        return arg1;
    }

    @Override
    public DoubleBuffer get3x3(DoubleBuffer arg0) {
        MemUtil.INSTANCE.put3x3(this, 0, arg0);
        return arg0;
    }

    @Override
    public DoubleBuffer get3x3(int arg0, DoubleBuffer arg1) {
        MemUtil.INSTANCE.put3x3(this, arg0, arg1);
        return arg1;
    }

    @Override
    public ByteBuffer get3x3(ByteBuffer arg0) {
        MemUtil.INSTANCE.put3x3(this, 0, arg0);
        return arg0;
    }

    @Override
    public ByteBuffer get3x3(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.put3x3(this, arg0, arg1);
        return arg1;
    }

    @Override
    public DoubleBuffer get4x4(DoubleBuffer arg0) {
        MemUtil.INSTANCE.put4x4(this, 0, arg0);
        return arg0;
    }

    @Override
    public DoubleBuffer get4x4(int arg0, DoubleBuffer arg1) {
        MemUtil.INSTANCE.put4x4(this, arg0, arg1);
        return arg1;
    }

    @Override
    public ByteBuffer get4x4(ByteBuffer arg0) {
        MemUtil.INSTANCE.put4x4(this, 0, arg0);
        return arg0;
    }

    @Override
    public ByteBuffer get4x4(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.put4x4(this, arg0, arg1);
        return arg1;
    }

    @Override
    public Matrix3x2dc getToAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.put(this, arg0);
            return this;
        }
    }

    @Override
    public double[] get(double[] doubles, int int0) {
        MemUtil.INSTANCE.copy(this, doubles, int0);
        return doubles;
    }

    @Override
    public double[] get(double[] doubles) {
        return this.get(doubles, 0);
    }

    @Override
    public double[] get3x3(double[] doubles, int int0) {
        MemUtil.INSTANCE.copy3x3(this, doubles, int0);
        return doubles;
    }

    @Override
    public double[] get3x3(double[] doubles) {
        return this.get3x3(doubles, 0);
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

    public Matrix3x2d set(DoubleBuffer arg0) {
        int int0 = arg0.position();
        MemUtil.INSTANCE.get(this, int0, arg0);
        return this;
    }

    public Matrix3x2d set(ByteBuffer arg0) {
        int int0 = arg0.position();
        MemUtil.INSTANCE.get(this, int0, arg0);
        return this;
    }

    public Matrix3x2d setFromAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.get(this, arg0);
            return this;
        }
    }

    public Matrix3x2d zero() {
        MemUtil.INSTANCE.zero(this);
        return this;
    }

    public Matrix3x2d identity() {
        MemUtil.INSTANCE.identity(this);
        return this;
    }

    @Override
    public Matrix3x2d scale(double arg0, double arg1, Matrix3x2d arg2) {
        arg2.m00 = this.m00 * arg0;
        arg2.m01 = this.m01 * arg0;
        arg2.m10 = this.m10 * arg1;
        arg2.m11 = this.m11 * arg1;
        arg2.m20 = this.m20;
        arg2.m21 = this.m21;
        return arg2;
    }

    public Matrix3x2d scale(double arg0, double arg1) {
        return this.scale(arg0, arg1, this);
    }

    public Matrix3x2d scale(Vector2dc arg0) {
        return this.scale(arg0.x(), arg0.y(), this);
    }

    @Override
    public Matrix3x2d scale(Vector2dc arg0, Matrix3x2d arg1) {
        return this.scale(arg0.x(), arg0.y(), arg1);
    }

    public Matrix3x2d scale(Vector2fc arg0) {
        return this.scale(arg0.x(), arg0.y(), this);
    }

    @Override
    public Matrix3x2d scale(Vector2fc arg0, Matrix3x2d arg1) {
        return this.scale(arg0.x(), arg0.y(), arg1);
    }

    @Override
    public Matrix3x2d scale(double arg0, Matrix3x2d arg1) {
        return this.scale(arg0, arg0, arg1);
    }

    public Matrix3x2d scale(double arg0) {
        return this.scale(arg0, arg0);
    }

    @Override
    public Matrix3x2d scaleLocal(double arg0, double arg1, Matrix3x2d arg2) {
        arg2.m00 = arg0 * this.m00;
        arg2.m01 = arg1 * this.m01;
        arg2.m10 = arg0 * this.m10;
        arg2.m11 = arg1 * this.m11;
        arg2.m20 = arg0 * this.m20;
        arg2.m21 = arg1 * this.m21;
        return arg2;
    }

    public Matrix3x2d scaleLocal(double arg0, double arg1) {
        return this.scaleLocal(arg0, arg1, this);
    }

    @Override
    public Matrix3x2d scaleLocal(double arg0, Matrix3x2d arg1) {
        return this.scaleLocal(arg0, arg0, arg1);
    }

    public Matrix3x2d scaleLocal(double arg0) {
        return this.scaleLocal(arg0, arg0, this);
    }

    @Override
    public Matrix3x2d scaleAround(double arg0, double arg1, double arg2, double arg3, Matrix3x2d arg4) {
        double double0 = this.m00 * arg2 + this.m10 * arg3 + this.m20;
        double double1 = this.m01 * arg2 + this.m11 * arg3 + this.m21;
        arg4.m00 = this.m00 * arg0;
        arg4.m01 = this.m01 * arg0;
        arg4.m10 = this.m10 * arg1;
        arg4.m11 = this.m11 * arg1;
        arg4.m20 = arg4.m00 * -arg2 + arg4.m10 * -arg3 + double0;
        arg4.m21 = arg4.m01 * -arg2 + arg4.m11 * -arg3 + double1;
        return arg4;
    }

    public Matrix3x2d scaleAround(double arg0, double arg1, double arg2, double arg3) {
        return this.scaleAround(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix3x2d scaleAround(double arg0, double arg1, double arg2, Matrix3x2d arg3) {
        return this.scaleAround(arg0, arg0, arg1, arg2, this);
    }

    public Matrix3x2d scaleAround(double arg0, double arg1, double arg2) {
        return this.scaleAround(arg0, arg0, arg1, arg2, this);
    }

    @Override
    public Matrix3x2d scaleAroundLocal(double arg0, double arg1, double arg2, double arg3, Matrix3x2d arg4) {
        arg4.m00 = arg0 * this.m00;
        arg4.m01 = arg1 * this.m01;
        arg4.m10 = arg0 * this.m10;
        arg4.m11 = arg1 * this.m11;
        arg4.m20 = arg0 * this.m20 - arg0 * arg2 + arg2;
        arg4.m21 = arg1 * this.m21 - arg1 * arg3 + arg3;
        return arg4;
    }

    @Override
    public Matrix3x2d scaleAroundLocal(double arg0, double arg1, double arg2, Matrix3x2d arg3) {
        return this.scaleAroundLocal(arg0, arg0, arg1, arg2, arg3);
    }

    public Matrix3x2d scaleAroundLocal(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return this.scaleAroundLocal(arg0, arg1, arg3, arg4, this);
    }

    public Matrix3x2d scaleAroundLocal(double arg0, double arg1, double arg2) {
        return this.scaleAroundLocal(arg0, arg0, arg1, arg2, this);
    }

    public Matrix3x2d scaling(double arg0) {
        return this.scaling(arg0, arg0);
    }

    public Matrix3x2d scaling(double arg0, double arg1) {
        this.m00 = arg0;
        this.m01 = 0.0;
        this.m10 = 0.0;
        this.m11 = arg1;
        this.m20 = 0.0;
        this.m21 = 0.0;
        return this;
    }

    public Matrix3x2d rotation(double arg0) {
        double double0 = Math.cos(arg0);
        double double1 = Math.sin(arg0);
        this.m00 = double0;
        this.m10 = -double1;
        this.m20 = 0.0;
        this.m01 = double1;
        this.m11 = double0;
        this.m21 = 0.0;
        return this;
    }

    @Override
    public Vector3d transform(Vector3d arg0) {
        return arg0.mul(this);
    }

    @Override
    public Vector3d transform(Vector3dc arg0, Vector3d arg1) {
        return arg0.mul(this, arg1);
    }

    @Override
    public Vector3d transform(double arg0, double arg1, double arg2, Vector3d arg3) {
        return arg3.set(this.m00 * arg0 + this.m10 * arg1 + this.m20 * arg2, this.m01 * arg0 + this.m11 * arg1 + this.m21 * arg2, arg2);
    }

    @Override
    public Vector2d transformPosition(Vector2d arg0) {
        arg0.set(this.m00 * arg0.x + this.m10 * arg0.y + this.m20, this.m01 * arg0.x + this.m11 * arg0.y + this.m21);
        return arg0;
    }

    @Override
    public Vector2d transformPosition(Vector2dc arg0, Vector2d arg1) {
        arg1.set(this.m00 * arg0.x() + this.m10 * arg0.y() + this.m20, this.m01 * arg0.x() + this.m11 * arg0.y() + this.m21);
        return arg1;
    }

    @Override
    public Vector2d transformPosition(double arg0, double arg1, Vector2d arg2) {
        return arg2.set(this.m00 * arg0 + this.m10 * arg1 + this.m20, this.m01 * arg0 + this.m11 * arg1 + this.m21);
    }

    @Override
    public Vector2d transformDirection(Vector2d arg0) {
        arg0.set(this.m00 * arg0.x + this.m10 * arg0.y, this.m01 * arg0.x + this.m11 * arg0.y);
        return arg0;
    }

    @Override
    public Vector2d transformDirection(Vector2dc arg0, Vector2d arg1) {
        arg1.set(this.m00 * arg0.x() + this.m10 * arg0.y(), this.m01 * arg0.x() + this.m11 * arg0.y());
        return arg1;
    }

    @Override
    public Vector2d transformDirection(double arg0, double arg1, Vector2d arg2) {
        return arg2.set(this.m00 * arg0 + this.m10 * arg1, this.m01 * arg0 + this.m11 * arg1);
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        arg0.writeDouble(this.m00);
        arg0.writeDouble(this.m01);
        arg0.writeDouble(this.m10);
        arg0.writeDouble(this.m11);
        arg0.writeDouble(this.m20);
        arg0.writeDouble(this.m21);
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException {
        this.m00 = arg0.readDouble();
        this.m01 = arg0.readDouble();
        this.m10 = arg0.readDouble();
        this.m11 = arg0.readDouble();
        this.m20 = arg0.readDouble();
        this.m21 = arg0.readDouble();
    }

    public Matrix3x2d rotate(double arg0) {
        return this.rotate(arg0, this);
    }

    @Override
    public Matrix3x2d rotate(double arg0, Matrix3x2d arg1) {
        double double0 = Math.cos(arg0);
        double double1 = Math.sin(arg0);
        double double2 = -double1;
        double double3 = this.m00 * double0 + this.m10 * double1;
        double double4 = this.m01 * double0 + this.m11 * double1;
        arg1.m10 = this.m00 * double2 + this.m10 * double0;
        arg1.m11 = this.m01 * double2 + this.m11 * double0;
        arg1.m00 = double3;
        arg1.m01 = double4;
        arg1.m20 = this.m20;
        arg1.m21 = this.m21;
        return arg1;
    }

    @Override
    public Matrix3x2d rotateLocal(double arg0, Matrix3x2d arg1) {
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
        arg1.m10 = double4;
        arg1.m11 = double5;
        arg1.m20 = double6;
        arg1.m21 = double7;
        return arg1;
    }

    public Matrix3x2d rotateLocal(double arg0) {
        return this.rotateLocal(arg0, this);
    }

    public Matrix3x2d rotateAbout(double arg0, double arg1, double arg2) {
        return this.rotateAbout(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix3x2d rotateAbout(double arg0, double arg1, double arg2, Matrix3x2d arg3) {
        double double0 = this.m00 * arg1 + this.m10 * arg2 + this.m20;
        double double1 = this.m01 * arg1 + this.m11 * arg2 + this.m21;
        double double2 = Math.cos(arg0);
        double double3 = Math.sin(arg0);
        double double4 = this.m00 * double2 + this.m10 * double3;
        double double5 = this.m01 * double2 + this.m11 * double3;
        arg3.m10 = this.m00 * -double3 + this.m10 * double2;
        arg3.m11 = this.m01 * -double3 + this.m11 * double2;
        arg3.m00 = double4;
        arg3.m01 = double5;
        arg3.m20 = arg3.m00 * -arg1 + arg3.m10 * -arg2 + double0;
        arg3.m21 = arg3.m01 * -arg1 + arg3.m11 * -arg2 + double1;
        return arg3;
    }

    @Override
    public Matrix3x2d rotateTo(Vector2dc arg0, Vector2dc arg1, Matrix3x2d arg2) {
        double double0 = arg0.x() * arg1.x() + arg0.y() * arg1.y();
        double double1 = arg0.x() * arg1.y() - arg0.y() * arg1.x();
        double double2 = -double1;
        double double3 = this.m00 * double0 + this.m10 * double1;
        double double4 = this.m01 * double0 + this.m11 * double1;
        arg2.m10 = this.m00 * double2 + this.m10 * double0;
        arg2.m11 = this.m01 * double2 + this.m11 * double0;
        arg2.m00 = double3;
        arg2.m01 = double4;
        arg2.m20 = this.m20;
        arg2.m21 = this.m21;
        return arg2;
    }

    public Matrix3x2d rotateTo(Vector2dc arg0, Vector2dc arg1) {
        return this.rotateTo(arg0, arg1, this);
    }

    @Override
    public Matrix3x2d view(double arg0, double arg1, double arg2, double arg3, Matrix3x2d arg4) {
        double double0 = 2.0 / (arg1 - arg0);
        double double1 = 2.0 / (arg3 - arg2);
        double double2 = (arg0 + arg1) / (arg0 - arg1);
        double double3 = (arg2 + arg3) / (arg2 - arg3);
        arg4.m20 = this.m00 * double2 + this.m10 * double3 + this.m20;
        arg4.m21 = this.m01 * double2 + this.m11 * double3 + this.m21;
        arg4.m00 = this.m00 * double0;
        arg4.m01 = this.m01 * double0;
        arg4.m10 = this.m10 * double1;
        arg4.m11 = this.m11 * double1;
        return arg4;
    }

    public Matrix3x2d view(double arg0, double arg1, double arg2, double arg3) {
        return this.view(arg0, arg1, arg2, arg3, this);
    }

    public Matrix3x2d setView(double arg0, double arg1, double arg2, double arg3) {
        this.m00 = 2.0 / (arg1 - arg0);
        this.m01 = 0.0;
        this.m10 = 0.0;
        this.m11 = 2.0 / (arg3 - arg2);
        this.m20 = (arg0 + arg1) / (arg0 - arg1);
        this.m21 = (arg2 + arg3) / (arg2 - arg3);
        return this;
    }

    @Override
    public Vector2d origin(Vector2d arg0) {
        double double0 = 1.0 / (this.m00 * this.m11 - this.m01 * this.m10);
        arg0.x = (this.m10 * this.m21 - this.m20 * this.m11) * double0;
        arg0.y = (this.m20 * this.m01 - this.m00 * this.m21) * double0;
        return arg0;
    }

    @Override
    public double[] viewArea(double[] doubles) {
        double double0 = 1.0 / (this.m00 * this.m11 - this.m01 * this.m10);
        double double1 = this.m11 * double0;
        double double2 = -this.m01 * double0;
        double double3 = -this.m10 * double0;
        double double4 = this.m00 * double0;
        double double5 = (this.m10 * this.m21 - this.m20 * this.m11) * double0;
        double double6 = (this.m20 * this.m01 - this.m00 * this.m21) * double0;
        double double7 = -double1 - double3;
        double double8 = -double2 - double4;
        double double9 = double1 - double3;
        double double10 = double2 - double4;
        double double11 = -double1 + double3;
        double double12 = -double2 + double4;
        double double13 = double1 + double3;
        double double14 = double2 + double4;
        double double15 = double7 < double11 ? double7 : double11;
        double15 = double15 < double9 ? double15 : double9;
        double15 = double15 < double13 ? double15 : double13;
        double double16 = double8 < double12 ? double8 : double12;
        double16 = double16 < double10 ? double16 : double10;
        double16 = double16 < double14 ? double16 : double14;
        double double17 = double7 > double11 ? double7 : double11;
        double17 = double17 > double9 ? double17 : double9;
        double17 = double17 > double13 ? double17 : double13;
        double double18 = double8 > double12 ? double8 : double12;
        double18 = double18 > double10 ? double18 : double10;
        double18 = double18 > double14 ? double18 : double14;
        doubles[0] = double15 + double5;
        doubles[1] = double16 + double6;
        doubles[2] = double17 + double5;
        doubles[3] = double18 + double6;
        return doubles;
    }

    @Override
    public Vector2d positiveX(Vector2d arg0) {
        double double0 = this.m00 * this.m11 - this.m01 * this.m10;
        double0 = 1.0 / double0;
        arg0.x = this.m11 * double0;
        arg0.y = -this.m01 * double0;
        return arg0.normalize(arg0);
    }

    @Override
    public Vector2d normalizedPositiveX(Vector2d arg0) {
        arg0.x = this.m11;
        arg0.y = -this.m01;
        return arg0;
    }

    @Override
    public Vector2d positiveY(Vector2d arg0) {
        double double0 = this.m00 * this.m11 - this.m01 * this.m10;
        double0 = 1.0 / double0;
        arg0.x = -this.m10 * double0;
        arg0.y = this.m00 * double0;
        return arg0.normalize(arg0);
    }

    @Override
    public Vector2d normalizedPositiveY(Vector2d arg0) {
        arg0.x = -this.m10;
        arg0.y = this.m00;
        return arg0;
    }

    @Override
    public Vector2d unproject(double double8, double double10, int[] ints, Vector2d vector2d) {
        double double0 = 1.0 / (this.m00 * this.m11 - this.m01 * this.m10);
        double double1 = this.m11 * double0;
        double double2 = -this.m01 * double0;
        double double3 = -this.m10 * double0;
        double double4 = this.m00 * double0;
        double double5 = (this.m10 * this.m21 - this.m20 * this.m11) * double0;
        double double6 = (this.m20 * this.m01 - this.m00 * this.m21) * double0;
        double double7 = (double8 - ints[0]) / ints[2] * 2.0 - 1.0;
        double double9 = (double10 - ints[1]) / ints[3] * 2.0 - 1.0;
        vector2d.x = double1 * double7 + double3 * double9 + double5;
        vector2d.y = double2 * double7 + double4 * double9 + double6;
        return vector2d;
    }

    @Override
    public Vector2d unprojectInv(double double1, double double3, int[] ints, Vector2d vector2d) {
        double double0 = (double1 - ints[0]) / ints[2] * 2.0 - 1.0;
        double double2 = (double3 - ints[1]) / ints[3] * 2.0 - 1.0;
        vector2d.x = this.m00 * double0 + this.m10 * double2 + this.m20;
        vector2d.y = this.m01 * double0 + this.m11 * double2 + this.m21;
        return vector2d;
    }

    public Matrix3x2d span(Vector2d arg0, Vector2d arg1, Vector2d arg2) {
        double double0 = 1.0 / (this.m00 * this.m11 - this.m01 * this.m10);
        double double1 = this.m11 * double0;
        double double2 = -this.m01 * double0;
        double double3 = -this.m10 * double0;
        double double4 = this.m00 * double0;
        arg0.x = -double1 - double3 + (this.m10 * this.m21 - this.m20 * this.m11) * double0;
        arg0.y = -double2 - double4 + (this.m20 * this.m01 - this.m00 * this.m21) * double0;
        arg1.x = 2.0 * double1;
        arg1.y = 2.0 * double2;
        arg2.x = 2.0 * double3;
        arg2.y = 2.0 * double4;
        return this;
    }

    @Override
    public boolean testPoint(double arg0, double arg1) {
        double double0 = this.m00;
        double double1 = this.m10;
        double double2 = 1.0 + this.m20;
        double double3 = -this.m00;
        double double4 = -this.m10;
        double double5 = 1.0 - this.m20;
        double double6 = this.m01;
        double double7 = this.m11;
        double double8 = 1.0 + this.m21;
        double double9 = -this.m01;
        double double10 = -this.m11;
        double double11 = 1.0 - this.m21;
        return double0 * arg0 + double1 * arg1 + double2 >= 0.0
            && double3 * arg0 + double4 * arg1 + double5 >= 0.0
            && double6 * arg0 + double7 * arg1 + double8 >= 0.0
            && double9 * arg0 + double10 * arg1 + double11 >= 0.0;
    }

    @Override
    public boolean testCircle(double arg0, double arg1, double arg2) {
        double double0 = this.m00;
        double double1 = this.m10;
        double double2 = 1.0 + this.m20;
        double double3 = Math.invsqrt(double0 * double0 + double1 * double1);
        double0 *= double3;
        double1 *= double3;
        double2 *= double3;
        double double4 = -this.m00;
        double double5 = -this.m10;
        double double6 = 1.0 - this.m20;
        double3 = Math.invsqrt(double4 * double4 + double5 * double5);
        double4 *= double3;
        double5 *= double3;
        double6 *= double3;
        double double7 = this.m01;
        double double8 = this.m11;
        double double9 = 1.0 + this.m21;
        double3 = Math.invsqrt(double7 * double7 + double8 * double8);
        double7 *= double3;
        double8 *= double3;
        double9 *= double3;
        double double10 = -this.m01;
        double double11 = -this.m11;
        double double12 = 1.0 - this.m21;
        double3 = Math.invsqrt(double10 * double10 + double11 * double11);
        double10 *= double3;
        double11 *= double3;
        double12 *= double3;
        return double0 * arg0 + double1 * arg1 + double2 >= -arg2
            && double4 * arg0 + double5 * arg1 + double6 >= -arg2
            && double7 * arg0 + double8 * arg1 + double9 >= -arg2
            && double10 * arg0 + double11 * arg1 + double12 >= -arg2;
    }

    @Override
    public boolean testAar(double arg0, double arg1, double arg2, double arg3) {
        double double0 = this.m00;
        double double1 = this.m10;
        double double2 = 1.0 + this.m20;
        double double3 = -this.m00;
        double double4 = -this.m10;
        double double5 = 1.0 - this.m20;
        double double6 = this.m01;
        double double7 = this.m11;
        double double8 = 1.0 + this.m21;
        double double9 = -this.m01;
        double double10 = -this.m11;
        double double11 = 1.0 - this.m21;
        return double0 * (double0 < 0.0 ? arg0 : arg2) + double1 * (double1 < 0.0 ? arg1 : arg3) >= -double2
            && double3 * (double3 < 0.0 ? arg0 : arg2) + double4 * (double4 < 0.0 ? arg1 : arg3) >= -double5
            && double6 * (double6 < 0.0 ? arg0 : arg2) + double7 * (double7 < 0.0 ? arg1 : arg3) >= -double8
            && double9 * (double9 < 0.0 ? arg0 : arg2) + double10 * (double10 < 0.0 ? arg1 : arg3) >= -double11;
    }

    @Override
    public int hashCode() {
        int int0 = 1;
        long long0 = Double.doubleToLongBits(this.m00);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m01);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m10);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m11);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m20);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.m21);
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
            Matrix3x2d matrix3x2d = (Matrix3x2d)arg0;
            if (Double.doubleToLongBits(this.m00) != Double.doubleToLongBits(matrix3x2d.m00)) {
                return false;
            } else if (Double.doubleToLongBits(this.m01) != Double.doubleToLongBits(matrix3x2d.m01)) {
                return false;
            } else if (Double.doubleToLongBits(this.m10) != Double.doubleToLongBits(matrix3x2d.m10)) {
                return false;
            } else if (Double.doubleToLongBits(this.m11) != Double.doubleToLongBits(matrix3x2d.m11)) {
                return false;
            } else {
                return Double.doubleToLongBits(this.m20) != Double.doubleToLongBits(matrix3x2d.m20)
                    ? false
                    : Double.doubleToLongBits(this.m21) == Double.doubleToLongBits(matrix3x2d.m21);
            }
        }
    }

    @Override
    public boolean equals(Matrix3x2dc arg0, double arg1) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (!(arg0 instanceof Matrix3x2d)) {
            return false;
        } else if (!Runtime.equals(this.m00, arg0.m00(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m01, arg0.m01(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m10, arg0.m10(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m11, arg0.m11(), arg1)) {
            return false;
        } else {
            return !Runtime.equals(this.m20, arg0.m20(), arg1) ? false : Runtime.equals(this.m21, arg0.m21(), arg1);
        }
    }

    @Override
    public boolean isFinite() {
        return Math.isFinite(this.m00)
            && Math.isFinite(this.m01)
            && Math.isFinite(this.m10)
            && Math.isFinite(this.m11)
            && Math.isFinite(this.m20)
            && Math.isFinite(this.m21);
    }
}
