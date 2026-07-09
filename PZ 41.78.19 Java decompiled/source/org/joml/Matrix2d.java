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

public class Matrix2d implements Externalizable, Matrix2dc {
    private static final long serialVersionUID = 1L;
    public double m00;
    public double m01;
    public double m10;
    public double m11;

    public Matrix2d() {
        this.m00 = 1.0;
        this.m11 = 1.0;
    }

    public Matrix2d(Matrix2dc arg0) {
        if (arg0 instanceof Matrix2d) {
            MemUtil.INSTANCE.copy((Matrix2d)arg0, this);
        } else {
            this.setMatrix2dc(arg0);
        }
    }

    public Matrix2d(Matrix2fc arg0) {
        this.m00 = arg0.m00();
        this.m01 = arg0.m01();
        this.m10 = arg0.m10();
        this.m11 = arg0.m11();
    }

    public Matrix2d(Matrix3dc arg0) {
        if (arg0 instanceof Matrix3d) {
            MemUtil.INSTANCE.copy((Matrix3d)arg0, this);
        } else {
            this.setMatrix3dc(arg0);
        }
    }

    public Matrix2d(Matrix3fc arg0) {
        this.m00 = arg0.m00();
        this.m01 = arg0.m01();
        this.m10 = arg0.m10();
        this.m11 = arg0.m11();
    }

    public Matrix2d(double arg0, double arg1, double arg2, double arg3) {
        this.m00 = arg0;
        this.m01 = arg1;
        this.m10 = arg2;
        this.m11 = arg3;
    }

    public Matrix2d(DoubleBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
    }

    public Matrix2d(Vector2dc arg0, Vector2dc arg1) {
        this.m00 = arg0.x();
        this.m01 = arg0.y();
        this.m10 = arg1.x();
        this.m11 = arg1.y();
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

    public Matrix2d m00(double arg0) {
        this.m00 = arg0;
        return this;
    }

    public Matrix2d m01(double arg0) {
        this.m01 = arg0;
        return this;
    }

    public Matrix2d m10(double arg0) {
        this.m10 = arg0;
        return this;
    }

    public Matrix2d m11(double arg0) {
        this.m11 = arg0;
        return this;
    }

    Matrix2d _m00(double double0) {
        this.m00 = double0;
        return this;
    }

    Matrix2d _m01(double double0) {
        this.m01 = double0;
        return this;
    }

    Matrix2d _m10(double double0) {
        this.m10 = double0;
        return this;
    }

    Matrix2d _m11(double double0) {
        this.m11 = double0;
        return this;
    }

    public Matrix2d set(Matrix2dc arg0) {
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

    public Matrix2d set(Matrix2fc arg0) {
        this.m00 = arg0.m00();
        this.m01 = arg0.m01();
        this.m10 = arg0.m10();
        this.m11 = arg0.m11();
        return this;
    }

    public Matrix2d set(Matrix3x2dc arg0) {
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
    }

    public Matrix2d set(Matrix3x2fc arg0) {
        this.m00 = arg0.m00();
        this.m01 = arg0.m01();
        this.m10 = arg0.m10();
        this.m11 = arg0.m11();
        return this;
    }

    public Matrix2d set(Matrix3dc arg0) {
        if (arg0 instanceof Matrix3d) {
            MemUtil.INSTANCE.copy((Matrix3d)arg0, this);
        } else {
            this.setMatrix3dc(arg0);
        }

        return this;
    }

    private void setMatrix3dc(Matrix3dc matrix3dc) {
        this.m00 = matrix3dc.m00();
        this.m01 = matrix3dc.m01();
        this.m10 = matrix3dc.m10();
        this.m11 = matrix3dc.m11();
    }

    public Matrix2d set(Matrix3fc arg0) {
        this.m00 = arg0.m00();
        this.m01 = arg0.m01();
        this.m10 = arg0.m10();
        this.m11 = arg0.m11();
        return this;
    }

    public Matrix2d mul(Matrix2dc arg0) {
        return this.mul(arg0, this);
    }

    @Override
    public Matrix2d mul(Matrix2dc arg0, Matrix2d arg1) {
        double double0 = this.m00 * arg0.m00() + this.m10 * arg0.m01();
        double double1 = this.m01 * arg0.m00() + this.m11 * arg0.m01();
        double double2 = this.m00 * arg0.m10() + this.m10 * arg0.m11();
        double double3 = this.m01 * arg0.m10() + this.m11 * arg0.m11();
        arg1.m00 = double0;
        arg1.m01 = double1;
        arg1.m10 = double2;
        arg1.m11 = double3;
        return arg1;
    }

    public Matrix2d mul(Matrix2fc arg0) {
        return this.mul(arg0, this);
    }

    @Override
    public Matrix2d mul(Matrix2fc arg0, Matrix2d arg1) {
        double double0 = this.m00 * arg0.m00() + this.m10 * arg0.m01();
        double double1 = this.m01 * arg0.m00() + this.m11 * arg0.m01();
        double double2 = this.m00 * arg0.m10() + this.m10 * arg0.m11();
        double double3 = this.m01 * arg0.m10() + this.m11 * arg0.m11();
        arg1.m00 = double0;
        arg1.m01 = double1;
        arg1.m10 = double2;
        arg1.m11 = double3;
        return arg1;
    }

    public Matrix2d mulLocal(Matrix2dc arg0) {
        return this.mulLocal(arg0, this);
    }

    @Override
    public Matrix2d mulLocal(Matrix2dc arg0, Matrix2d arg1) {
        double double0 = arg0.m00() * this.m00 + arg0.m10() * this.m01;
        double double1 = arg0.m01() * this.m00 + arg0.m11() * this.m01;
        double double2 = arg0.m00() * this.m10 + arg0.m10() * this.m11;
        double double3 = arg0.m01() * this.m10 + arg0.m11() * this.m11;
        arg1.m00 = double0;
        arg1.m01 = double1;
        arg1.m10 = double2;
        arg1.m11 = double3;
        return arg1;
    }

    public Matrix2d set(double arg0, double arg1, double arg2, double arg3) {
        this.m00 = arg0;
        this.m01 = arg1;
        this.m10 = arg2;
        this.m11 = arg3;
        return this;
    }

    public Matrix2d set(double[] doubles) {
        MemUtil.INSTANCE.copy(doubles, 0, this);
        return this;
    }

    public Matrix2d set(Vector2dc arg0, Vector2dc arg1) {
        this.m00 = arg0.x();
        this.m01 = arg0.y();
        this.m10 = arg1.x();
        this.m11 = arg1.y();
        return this;
    }

    @Override
    public double determinant() {
        return this.m00 * this.m11 - this.m10 * this.m01;
    }

    public Matrix2d invert() {
        return this.invert(this);
    }

    @Override
    public Matrix2d invert(Matrix2d arg0) {
        double double0 = 1.0 / this.determinant();
        double double1 = this.m11 * double0;
        double double2 = -this.m01 * double0;
        double double3 = -this.m10 * double0;
        double double4 = this.m00 * double0;
        arg0.m00 = double1;
        arg0.m01 = double2;
        arg0.m10 = double3;
        arg0.m11 = double4;
        return arg0;
    }

    public Matrix2d transpose() {
        return this.transpose(this);
    }

    @Override
    public Matrix2d transpose(Matrix2d arg0) {
        arg0.set(this.m00, this.m10, this.m01, this.m11);
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
            + "\n"
            + Runtime.format(this.m01, numberFormat)
            + " "
            + Runtime.format(this.m11, numberFormat)
            + "\n";
    }

    @Override
    public Matrix2d get(Matrix2d arg0) {
        return arg0.set(this);
    }

    @Override
    public Matrix3x2d get(Matrix3x2d arg0) {
        return arg0.set(this);
    }

    @Override
    public Matrix3d get(Matrix3d arg0) {
        return arg0.set(this);
    }

    @Override
    public double getRotation() {
        return Math.atan2(this.m01, this.m11);
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
    public DoubleBuffer getTransposed(DoubleBuffer arg0) {
        return this.get(arg0.position(), arg0);
    }

    @Override
    public DoubleBuffer getTransposed(int arg0, DoubleBuffer arg1) {
        MemUtil.INSTANCE.putTransposed(this, arg0, arg1);
        return arg1;
    }

    @Override
    public ByteBuffer getTransposed(ByteBuffer arg0) {
        return this.get(arg0.position(), arg0);
    }

    @Override
    public ByteBuffer getTransposed(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.putTransposed(this, arg0, arg1);
        return arg1;
    }

    @Override
    public Matrix2dc getToAddress(long arg0) {
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

    public Matrix2d set(DoubleBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Matrix2d set(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Matrix2d setFromAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.get(this, arg0);
            return this;
        }
    }

    public Matrix2d zero() {
        MemUtil.INSTANCE.zero(this);
        return this;
    }

    public Matrix2d identity() {
        this.m00 = 1.0;
        this.m01 = 0.0;
        this.m10 = 0.0;
        this.m11 = 1.0;
        return this;
    }

    @Override
    public Matrix2d scale(Vector2dc arg0, Matrix2d arg1) {
        return this.scale(arg0.x(), arg0.y(), arg1);
    }

    public Matrix2d scale(Vector2dc arg0) {
        return this.scale(arg0.x(), arg0.y(), this);
    }

    @Override
    public Matrix2d scale(double arg0, double arg1, Matrix2d arg2) {
        arg2.m00 = this.m00 * arg0;
        arg2.m01 = this.m01 * arg0;
        arg2.m10 = this.m10 * arg1;
        arg2.m11 = this.m11 * arg1;
        return arg2;
    }

    public Matrix2d scale(double arg0, double arg1) {
        return this.scale(arg0, arg1, this);
    }

    @Override
    public Matrix2d scale(double arg0, Matrix2d arg1) {
        return this.scale(arg0, arg0, arg1);
    }

    public Matrix2d scale(double arg0) {
        return this.scale(arg0, arg0);
    }

    @Override
    public Matrix2d scaleLocal(double arg0, double arg1, Matrix2d arg2) {
        arg2.m00 = arg0 * this.m00;
        arg2.m01 = arg1 * this.m01;
        arg2.m10 = arg0 * this.m10;
        arg2.m11 = arg1 * this.m11;
        return arg2;
    }

    public Matrix2d scaleLocal(double arg0, double arg1) {
        return this.scaleLocal(arg0, arg1, this);
    }

    public Matrix2d scaling(double arg0) {
        MemUtil.INSTANCE.zero(this);
        this.m00 = arg0;
        this.m11 = arg0;
        return this;
    }

    public Matrix2d scaling(double arg0, double arg1) {
        MemUtil.INSTANCE.zero(this);
        this.m00 = arg0;
        this.m11 = arg1;
        return this;
    }

    public Matrix2d scaling(Vector2dc arg0) {
        return this.scaling(arg0.x(), arg0.y());
    }

    public Matrix2d rotation(double arg0) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        this.m00 = double1;
        this.m01 = double0;
        this.m10 = -double0;
        this.m11 = double1;
        return this;
    }

    @Override
    public Vector2d transform(Vector2d arg0) {
        return arg0.mul(this);
    }

    @Override
    public Vector2d transform(Vector2dc arg0, Vector2d arg1) {
        arg0.mul(this, arg1);
        return arg1;
    }

    @Override
    public Vector2d transform(double arg0, double arg1, Vector2d arg2) {
        arg2.set(this.m00 * arg0 + this.m10 * arg1, this.m01 * arg0 + this.m11 * arg1);
        return arg2;
    }

    @Override
    public Vector2d transformTranspose(Vector2d arg0) {
        return arg0.mulTranspose(this);
    }

    @Override
    public Vector2d transformTranspose(Vector2dc arg0, Vector2d arg1) {
        arg0.mulTranspose(this, arg1);
        return arg1;
    }

    @Override
    public Vector2d transformTranspose(double arg0, double arg1, Vector2d arg2) {
        arg2.set(this.m00 * arg0 + this.m01 * arg1, this.m10 * arg0 + this.m11 * arg1);
        return arg2;
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        arg0.writeDouble(this.m00);
        arg0.writeDouble(this.m01);
        arg0.writeDouble(this.m10);
        arg0.writeDouble(this.m11);
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException {
        this.m00 = arg0.readDouble();
        this.m01 = arg0.readDouble();
        this.m10 = arg0.readDouble();
        this.m11 = arg0.readDouble();
    }

    public Matrix2d rotate(double arg0) {
        return this.rotate(arg0, this);
    }

    @Override
    public Matrix2d rotate(double arg0, Matrix2d arg1) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = this.m00 * double1 + this.m10 * double0;
        double double3 = this.m01 * double1 + this.m11 * double0;
        double double4 = this.m10 * double1 - this.m00 * double0;
        double double5 = this.m11 * double1 - this.m01 * double0;
        arg1.m00 = double2;
        arg1.m01 = double3;
        arg1.m10 = double4;
        arg1.m11 = double5;
        return arg1;
    }

    public Matrix2d rotateLocal(double arg0) {
        return this.rotateLocal(arg0, this);
    }

    @Override
    public Matrix2d rotateLocal(double arg0, Matrix2d arg1) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = double1 * this.m00 - double0 * this.m01;
        double double3 = double0 * this.m00 + double1 * this.m01;
        double double4 = double1 * this.m10 - double0 * this.m11;
        double double5 = double0 * this.m10 + double1 * this.m11;
        arg1.m00 = double2;
        arg1.m01 = double3;
        arg1.m10 = double4;
        arg1.m11 = double5;
        return arg1;
    }

    @Override
    public Vector2d getRow(int arg0, Vector2d arg1) throws IndexOutOfBoundsException {
        switch (arg0) {
            case 0:
                arg1.x = this.m00;
                arg1.y = this.m10;
                break;
            case 1:
                arg1.x = this.m01;
                arg1.y = this.m11;
                break;
            default:
                throw new IndexOutOfBoundsException();
        }

        return arg1;
    }

    public Matrix2d setRow(int arg0, Vector2dc arg1) throws IndexOutOfBoundsException {
        return this.setRow(arg0, arg1.x(), arg1.y());
    }

    public Matrix2d setRow(int arg0, double arg1, double arg2) throws IndexOutOfBoundsException {
        switch (arg0) {
            case 0:
                this.m00 = arg1;
                this.m10 = arg2;
                break;
            case 1:
                this.m01 = arg1;
                this.m11 = arg2;
                break;
            default:
                throw new IndexOutOfBoundsException();
        }

        return this;
    }

    @Override
    public Vector2d getColumn(int arg0, Vector2d arg1) throws IndexOutOfBoundsException {
        switch (arg0) {
            case 0:
                arg1.x = this.m00;
                arg1.y = this.m01;
                break;
            case 1:
                arg1.x = this.m10;
                arg1.y = this.m11;
                break;
            default:
                throw new IndexOutOfBoundsException();
        }

        return arg1;
    }

    public Matrix2d setColumn(int arg0, Vector2dc arg1) throws IndexOutOfBoundsException {
        return this.setColumn(arg0, arg1.x(), arg1.y());
    }

    public Matrix2d setColumn(int arg0, double arg1, double arg2) throws IndexOutOfBoundsException {
        switch (arg0) {
            case 0:
                this.m00 = arg1;
                this.m01 = arg2;
                break;
            case 1:
                this.m10 = arg1;
                this.m11 = arg2;
                break;
            default:
                throw new IndexOutOfBoundsException();
        }

        return this;
    }

    @Override
    public double get(int arg0, int arg1) {
        switch (arg0) {
            case 0:
                switch (arg1) {
                    case 0:
                        return this.m00;
                    case 1:
                        return this.m01;
                    default:
                        throw new IndexOutOfBoundsException();
                }
            case 1:
                switch (arg1) {
                    case 0:
                        return this.m10;
                    case 1:
                        return this.m11;
                }
        }

        throw new IndexOutOfBoundsException();
    }

    public Matrix2d set(int arg0, int arg1, double arg2) {
        switch (arg0) {
            case 0:
                switch (arg1) {
                    case 0:
                        this.m00 = arg2;
                        return this;
                    case 1:
                        this.m01 = arg2;
                        return this;
                    default:
                        throw new IndexOutOfBoundsException();
                }
            case 1:
                switch (arg1) {
                    case 0:
                        this.m10 = arg2;
                        return this;
                    case 1:
                        this.m11 = arg2;
                        return this;
                }
        }

        throw new IndexOutOfBoundsException();
    }

    public Matrix2d normal() {
        return this.normal(this);
    }

    @Override
    public Matrix2d normal(Matrix2d arg0) {
        double double0 = this.m00 * this.m11 - this.m10 * this.m01;
        double double1 = 1.0 / double0;
        double double2 = this.m11 * double1;
        double double3 = -this.m10 * double1;
        double double4 = -this.m01 * double1;
        double double5 = this.m00 * double1;
        arg0.m00 = double2;
        arg0.m01 = double3;
        arg0.m10 = double4;
        arg0.m11 = double5;
        return arg0;
    }

    @Override
    public Vector2d getScale(Vector2d arg0) {
        arg0.x = Math.sqrt(this.m00 * this.m00 + this.m01 * this.m01);
        arg0.y = Math.sqrt(this.m10 * this.m10 + this.m11 * this.m11);
        return arg0;
    }

    @Override
    public Vector2d positiveX(Vector2d arg0) {
        if (this.m00 * this.m11 < this.m01 * this.m10) {
            arg0.x = -this.m11;
            arg0.y = this.m01;
        } else {
            arg0.x = this.m11;
            arg0.y = -this.m01;
        }

        return arg0.normalize(arg0);
    }

    @Override
    public Vector2d normalizedPositiveX(Vector2d arg0) {
        if (this.m00 * this.m11 < this.m01 * this.m10) {
            arg0.x = -this.m11;
            arg0.y = this.m01;
        } else {
            arg0.x = this.m11;
            arg0.y = -this.m01;
        }

        return arg0;
    }

    @Override
    public Vector2d positiveY(Vector2d arg0) {
        if (this.m00 * this.m11 < this.m01 * this.m10) {
            arg0.x = this.m10;
            arg0.y = -this.m00;
        } else {
            arg0.x = -this.m10;
            arg0.y = this.m00;
        }

        return arg0.normalize(arg0);
    }

    @Override
    public Vector2d normalizedPositiveY(Vector2d arg0) {
        if (this.m00 * this.m11 < this.m01 * this.m10) {
            arg0.x = this.m10;
            arg0.y = -this.m00;
        } else {
            arg0.x = -this.m10;
            arg0.y = this.m00;
        }

        return arg0;
    }

    @Override
    public int hashCode() {
        int int0 = 1;
        long long0 = Double.doubleToLongBits(this.m00);
        int0 = 31 * int0 + (int)(long0 >>> 32 ^ long0);
        long0 = Double.doubleToLongBits(this.m01);
        int0 = 31 * int0 + (int)(long0 >>> 32 ^ long0);
        long0 = Double.doubleToLongBits(this.m10);
        int0 = 31 * int0 + (int)(long0 >>> 32 ^ long0);
        long0 = Double.doubleToLongBits(this.m11);
        return 31 * int0 + (int)(long0 >>> 32 ^ long0);
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
            Matrix2d matrix2d = (Matrix2d)arg0;
            if (Double.doubleToLongBits(this.m00) != Double.doubleToLongBits(matrix2d.m00)) {
                return false;
            } else if (Double.doubleToLongBits(this.m01) != Double.doubleToLongBits(matrix2d.m01)) {
                return false;
            } else {
                return Double.doubleToLongBits(this.m10) != Double.doubleToLongBits(matrix2d.m10)
                    ? false
                    : Double.doubleToLongBits(this.m11) == Double.doubleToLongBits(matrix2d.m11);
            }
        }
    }

    @Override
    public boolean equals(Matrix2dc arg0, double arg1) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (!(arg0 instanceof Matrix2d)) {
            return false;
        } else if (!Runtime.equals(this.m00, arg0.m00(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m01, arg0.m01(), arg1)) {
            return false;
        } else {
            return !Runtime.equals(this.m10, arg0.m10(), arg1) ? false : Runtime.equals(this.m11, arg0.m11(), arg1);
        }
    }

    public Matrix2d swap(Matrix2d arg0) {
        MemUtil.INSTANCE.swap(this, arg0);
        return this;
    }

    public Matrix2d add(Matrix2dc arg0) {
        return this.add(arg0, this);
    }

    @Override
    public Matrix2d add(Matrix2dc arg0, Matrix2d arg1) {
        arg1.m00 = this.m00 + arg0.m00();
        arg1.m01 = this.m01 + arg0.m01();
        arg1.m10 = this.m10 + arg0.m10();
        arg1.m11 = this.m11 + arg0.m11();
        return arg1;
    }

    public Matrix2d sub(Matrix2dc arg0) {
        return this.sub(arg0, this);
    }

    @Override
    public Matrix2d sub(Matrix2dc arg0, Matrix2d arg1) {
        arg1.m00 = this.m00 - arg0.m00();
        arg1.m01 = this.m01 - arg0.m01();
        arg1.m10 = this.m10 - arg0.m10();
        arg1.m11 = this.m11 - arg0.m11();
        return arg1;
    }

    public Matrix2d mulComponentWise(Matrix2dc arg0) {
        return this.sub(arg0, this);
    }

    @Override
    public Matrix2d mulComponentWise(Matrix2dc arg0, Matrix2d arg1) {
        arg1.m00 = this.m00 * arg0.m00();
        arg1.m01 = this.m01 * arg0.m01();
        arg1.m10 = this.m10 * arg0.m10();
        arg1.m11 = this.m11 * arg0.m11();
        return arg1;
    }

    public Matrix2d lerp(Matrix2dc arg0, double arg1) {
        return this.lerp(arg0, arg1, this);
    }

    @Override
    public Matrix2d lerp(Matrix2dc arg0, double arg1, Matrix2d arg2) {
        arg2.m00 = Math.fma(arg0.m00() - this.m00, arg1, this.m00);
        arg2.m01 = Math.fma(arg0.m01() - this.m01, arg1, this.m01);
        arg2.m10 = Math.fma(arg0.m10() - this.m10, arg1, this.m10);
        arg2.m11 = Math.fma(arg0.m11() - this.m11, arg1, this.m11);
        return arg2;
    }

    @Override
    public boolean isFinite() {
        return Math.isFinite(this.m00) && Math.isFinite(this.m01) && Math.isFinite(this.m10) && Math.isFinite(this.m11);
    }
}
