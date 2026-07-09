// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.NumberFormat;

public class Vector2d implements Externalizable, Vector2dc {
    private static final long serialVersionUID = 1L;
    public double x;
    public double y;

    public Vector2d() {
    }

    public Vector2d(double arg0) {
        this.x = arg0;
        this.y = arg0;
    }

    public Vector2d(double arg0, double arg1) {
        this.x = arg0;
        this.y = arg1;
    }

    public Vector2d(Vector2dc arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
    }

    public Vector2d(Vector2fc arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
    }

    public Vector2d(Vector2ic arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
    }

    public Vector2d(double[] arg0) {
        this.x = arg0[0];
        this.y = arg0[1];
    }

    public Vector2d(float[] floats) {
        this.x = floats[0];
        this.y = floats[1];
    }

    public Vector2d(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
    }

    public Vector2d(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
    }

    public Vector2d(DoubleBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
    }

    public Vector2d(int arg0, DoubleBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
    }

    @Override
    public double x() {
        return this.x;
    }

    @Override
    public double y() {
        return this.y;
    }

    public Vector2d set(double arg0) {
        this.x = arg0;
        this.y = arg0;
        return this;
    }

    public Vector2d set(double arg0, double arg1) {
        this.x = arg0;
        this.y = arg1;
        return this;
    }

    public Vector2d set(Vector2dc arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        return this;
    }

    public Vector2d set(Vector2fc arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        return this;
    }

    public Vector2d set(Vector2ic arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        return this;
    }

    public Vector2d set(double[] arg0) {
        this.x = arg0[0];
        this.y = arg0[1];
        return this;
    }

    public Vector2d set(float[] floats) {
        this.x = floats[0];
        this.y = floats[1];
        return this;
    }

    public Vector2d set(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Vector2d set(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
        return this;
    }

    public Vector2d set(DoubleBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Vector2d set(int arg0, DoubleBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
        return this;
    }

    public Vector2d setFromAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.get(this, arg0);
            return this;
        }
    }

    @Override
    public double get(int arg0) throws IllegalArgumentException {
        switch (arg0) {
            case 0:
                return this.x;
            case 1:
                return this.y;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public Vector2i get(int arg0, Vector2i arg1) {
        arg1.x = Math.roundUsing(this.x(), arg0);
        arg1.y = Math.roundUsing(this.y(), arg0);
        return arg1;
    }

    @Override
    public Vector2f get(Vector2f arg0) {
        arg0.x = (float)this.x();
        arg0.y = (float)this.y();
        return arg0;
    }

    @Override
    public Vector2d get(Vector2d arg0) {
        arg0.x = this.x();
        arg0.y = this.y();
        return arg0;
    }

    public Vector2d setComponent(int arg0, double arg1) throws IllegalArgumentException {
        switch (arg0) {
            case 0:
                this.x = arg1;
                break;
            case 1:
                this.y = arg1;
                break;
            default:
                throw new IllegalArgumentException();
        }

        return this;
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
    public Vector2dc getToAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.put(this, arg0);
            return this;
        }
    }

    public Vector2d perpendicular() {
        double double0 = this.y;
        this.y = this.x * -1.0;
        this.x = double0;
        return this;
    }

    public Vector2d sub(Vector2dc arg0) {
        this.x = this.x - arg0.x();
        this.y = this.y - arg0.y();
        return this;
    }

    public Vector2d sub(double arg0, double arg1) {
        this.x -= arg0;
        this.y -= arg1;
        return this;
    }

    @Override
    public Vector2d sub(double arg0, double arg1, Vector2d arg2) {
        arg2.x = this.x - arg0;
        arg2.y = this.y - arg1;
        return arg2;
    }

    public Vector2d sub(Vector2fc arg0) {
        this.x = this.x - arg0.x();
        this.y = this.y - arg0.y();
        return this;
    }

    @Override
    public Vector2d sub(Vector2dc arg0, Vector2d arg1) {
        arg1.x = this.x - arg0.x();
        arg1.y = this.y - arg0.y();
        return arg1;
    }

    @Override
    public Vector2d sub(Vector2fc arg0, Vector2d arg1) {
        arg1.x = this.x - arg0.x();
        arg1.y = this.y - arg0.y();
        return arg1;
    }

    public Vector2d mul(double arg0) {
        this.x *= arg0;
        this.y *= arg0;
        return this;
    }

    @Override
    public Vector2d mul(double arg0, Vector2d arg1) {
        arg1.x = this.x * arg0;
        arg1.y = this.y * arg0;
        return arg1;
    }

    public Vector2d mul(double arg0, double arg1) {
        this.x *= arg0;
        this.y *= arg1;
        return this;
    }

    @Override
    public Vector2d mul(double arg0, double arg1, Vector2d arg2) {
        arg2.x = this.x * arg0;
        arg2.y = this.y * arg1;
        return arg2;
    }

    public Vector2d mul(Vector2dc arg0) {
        this.x = this.x * arg0.x();
        this.y = this.y * arg0.y();
        return this;
    }

    @Override
    public Vector2d mul(Vector2dc arg0, Vector2d arg1) {
        arg1.x = this.x * arg0.x();
        arg1.y = this.y * arg0.y();
        return arg1;
    }

    public Vector2d div(double arg0) {
        double double0 = 1.0 / arg0;
        this.x *= double0;
        this.y *= double0;
        return this;
    }

    @Override
    public Vector2d div(double arg0, Vector2d arg1) {
        double double0 = 1.0 / arg0;
        arg1.x = this.x * double0;
        arg1.y = this.y * double0;
        return arg1;
    }

    public Vector2d div(double arg0, double arg1) {
        this.x /= arg0;
        this.y /= arg1;
        return this;
    }

    @Override
    public Vector2d div(double arg0, double arg1, Vector2d arg2) {
        arg2.x = this.x / arg0;
        arg2.y = this.y / arg1;
        return arg2;
    }

    public Vector2d div(Vector2d arg0) {
        this.x = this.x / arg0.x();
        this.y = this.y / arg0.y();
        return this;
    }

    public Vector2d div(Vector2fc arg0) {
        this.x = this.x / arg0.x();
        this.y = this.y / arg0.y();
        return this;
    }

    @Override
    public Vector2d div(Vector2fc arg0, Vector2d arg1) {
        arg1.x = this.x / arg0.x();
        arg1.y = this.y / arg0.y();
        return arg1;
    }

    @Override
    public Vector2d div(Vector2dc arg0, Vector2d arg1) {
        arg1.x = this.x / arg0.x();
        arg1.y = this.y / arg0.y();
        return arg1;
    }

    public Vector2d mul(Matrix2fc arg0) {
        double double0 = arg0.m00() * this.x + arg0.m10() * this.y;
        double double1 = arg0.m01() * this.x + arg0.m11() * this.y;
        this.x = double0;
        this.y = double1;
        return this;
    }

    public Vector2d mul(Matrix2dc arg0) {
        double double0 = arg0.m00() * this.x + arg0.m10() * this.y;
        double double1 = arg0.m01() * this.x + arg0.m11() * this.y;
        this.x = double0;
        this.y = double1;
        return this;
    }

    @Override
    public Vector2d mul(Matrix2dc arg0, Vector2d arg1) {
        double double0 = arg0.m00() * this.x + arg0.m10() * this.y;
        double double1 = arg0.m01() * this.x + arg0.m11() * this.y;
        arg1.x = double0;
        arg1.y = double1;
        return arg1;
    }

    @Override
    public Vector2d mul(Matrix2fc arg0, Vector2d arg1) {
        double double0 = arg0.m00() * this.x + arg0.m10() * this.y;
        double double1 = arg0.m01() * this.x + arg0.m11() * this.y;
        arg1.x = double0;
        arg1.y = double1;
        return arg1;
    }

    public Vector2d mulTranspose(Matrix2dc arg0) {
        double double0 = arg0.m00() * this.x + arg0.m01() * this.y;
        double double1 = arg0.m10() * this.x + arg0.m11() * this.y;
        this.x = double0;
        this.y = double1;
        return this;
    }

    @Override
    public Vector2d mulTranspose(Matrix2dc arg0, Vector2d arg1) {
        double double0 = arg0.m00() * this.x + arg0.m01() * this.y;
        double double1 = arg0.m10() * this.x + arg0.m11() * this.y;
        arg1.x = double0;
        arg1.y = double1;
        return arg1;
    }

    public Vector2d mulTranspose(Matrix2fc arg0) {
        double double0 = arg0.m00() * this.x + arg0.m01() * this.y;
        double double1 = arg0.m10() * this.x + arg0.m11() * this.y;
        this.x = double0;
        this.y = double1;
        return this;
    }

    @Override
    public Vector2d mulTranspose(Matrix2fc arg0, Vector2d arg1) {
        double double0 = arg0.m00() * this.x + arg0.m01() * this.y;
        double double1 = arg0.m10() * this.x + arg0.m11() * this.y;
        arg1.x = double0;
        arg1.y = double1;
        return arg1;
    }

    public Vector2d mulPosition(Matrix3x2dc arg0) {
        double double0 = arg0.m00() * this.x + arg0.m10() * this.y + arg0.m20();
        double double1 = arg0.m01() * this.x + arg0.m11() * this.y + arg0.m21();
        this.x = double0;
        this.y = double1;
        return this;
    }

    @Override
    public Vector2d mulPosition(Matrix3x2dc arg0, Vector2d arg1) {
        double double0 = arg0.m00() * this.x + arg0.m10() * this.y + arg0.m20();
        double double1 = arg0.m01() * this.x + arg0.m11() * this.y + arg0.m21();
        arg1.x = double0;
        arg1.y = double1;
        return arg1;
    }

    public Vector2d mulDirection(Matrix3x2dc arg0) {
        double double0 = arg0.m00() * this.x + arg0.m10() * this.y;
        double double1 = arg0.m01() * this.x + arg0.m11() * this.y;
        this.x = double0;
        this.y = double1;
        return this;
    }

    @Override
    public Vector2d mulDirection(Matrix3x2dc arg0, Vector2d arg1) {
        double double0 = arg0.m00() * this.x + arg0.m10() * this.y;
        double double1 = arg0.m01() * this.x + arg0.m11() * this.y;
        arg1.x = double0;
        arg1.y = double1;
        return arg1;
    }

    @Override
    public double dot(Vector2dc arg0) {
        return this.x * arg0.x() + this.y * arg0.y();
    }

    @Override
    public double angle(Vector2dc arg0) {
        double double0 = this.x * arg0.x() + this.y * arg0.y();
        double double1 = this.x * arg0.y() - this.y * arg0.x();
        return Math.atan2(double1, double0);
    }

    @Override
    public double lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public static double lengthSquared(double arg0, double arg1) {
        return arg0 * arg0 + arg1 * arg1;
    }

    @Override
    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public static double length(double arg0, double arg1) {
        return Math.sqrt(arg0 * arg0 + arg1 * arg1);
    }

    @Override
    public double distance(Vector2dc arg0) {
        double double0 = this.x - arg0.x();
        double double1 = this.y - arg0.y();
        return Math.sqrt(double0 * double0 + double1 * double1);
    }

    @Override
    public double distanceSquared(Vector2dc arg0) {
        double double0 = this.x - arg0.x();
        double double1 = this.y - arg0.y();
        return double0 * double0 + double1 * double1;
    }

    @Override
    public double distance(Vector2fc arg0) {
        double double0 = this.x - arg0.x();
        double double1 = this.y - arg0.y();
        return Math.sqrt(double0 * double0 + double1 * double1);
    }

    @Override
    public double distanceSquared(Vector2fc arg0) {
        double double0 = this.x - arg0.x();
        double double1 = this.y - arg0.y();
        return double0 * double0 + double1 * double1;
    }

    @Override
    public double distance(double arg0, double arg1) {
        double double0 = this.x - arg0;
        double double1 = this.y - arg1;
        return Math.sqrt(double0 * double0 + double1 * double1);
    }

    @Override
    public double distanceSquared(double arg0, double arg1) {
        double double0 = this.x - arg0;
        double double1 = this.y - arg1;
        return double0 * double0 + double1 * double1;
    }

    public static double distance(double arg0, double arg1, double arg2, double arg3) {
        double double0 = arg0 - arg2;
        double double1 = arg1 - arg3;
        return Math.sqrt(double0 * double0 + double1 * double1);
    }

    public static double distanceSquared(double arg0, double arg1, double arg2, double arg3) {
        double double0 = arg0 - arg2;
        double double1 = arg1 - arg3;
        return double0 * double0 + double1 * double1;
    }

    public Vector2d normalize() {
        double double0 = Math.invsqrt(this.x * this.x + this.y * this.y);
        this.x *= double0;
        this.y *= double0;
        return this;
    }

    @Override
    public Vector2d normalize(Vector2d arg0) {
        double double0 = Math.invsqrt(this.x * this.x + this.y * this.y);
        arg0.x = this.x * double0;
        arg0.y = this.y * double0;
        return arg0;
    }

    public Vector2d normalize(double arg0) {
        double double0 = Math.invsqrt(this.x * this.x + this.y * this.y) * arg0;
        this.x *= double0;
        this.y *= double0;
        return this;
    }

    @Override
    public Vector2d normalize(double arg0, Vector2d arg1) {
        double double0 = Math.invsqrt(this.x * this.x + this.y * this.y) * arg0;
        arg1.x = this.x * double0;
        arg1.y = this.y * double0;
        return arg1;
    }

    public Vector2d add(Vector2dc arg0) {
        this.x = this.x + arg0.x();
        this.y = this.y + arg0.y();
        return this;
    }

    public Vector2d add(double arg0, double arg1) {
        this.x += arg0;
        this.y += arg1;
        return this;
    }

    @Override
    public Vector2d add(double arg0, double arg1, Vector2d arg2) {
        arg2.x = this.x + arg0;
        arg2.y = this.y + arg1;
        return arg2;
    }

    public Vector2d add(Vector2fc arg0) {
        this.x = this.x + arg0.x();
        this.y = this.y + arg0.y();
        return this;
    }

    @Override
    public Vector2d add(Vector2dc arg0, Vector2d arg1) {
        arg1.x = this.x + arg0.x();
        arg1.y = this.y + arg0.y();
        return arg1;
    }

    @Override
    public Vector2d add(Vector2fc arg0, Vector2d arg1) {
        arg1.x = this.x + arg0.x();
        arg1.y = this.y + arg0.y();
        return arg1;
    }

    public Vector2d zero() {
        this.x = 0.0;
        this.y = 0.0;
        return this;
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        arg0.writeDouble(this.x);
        arg0.writeDouble(this.y);
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        this.x = arg0.readDouble();
        this.y = arg0.readDouble();
    }

    public Vector2d negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    @Override
    public Vector2d negate(Vector2d arg0) {
        arg0.x = -this.x;
        arg0.y = -this.y;
        return arg0;
    }

    public Vector2d lerp(Vector2dc arg0, double arg1) {
        this.x = this.x + (arg0.x() - this.x) * arg1;
        this.y = this.y + (arg0.y() - this.y) * arg1;
        return this;
    }

    @Override
    public Vector2d lerp(Vector2dc arg0, double arg1, Vector2d arg2) {
        arg2.x = this.x + (arg0.x() - this.x) * arg1;
        arg2.y = this.y + (arg0.y() - this.y) * arg1;
        return arg2;
    }

    @Override
    public int hashCode() {
        int int0 = 1;
        long long0 = Double.doubleToLongBits(this.x);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.y);
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
            Vector2d vector2d = (Vector2d)arg0;
            return Double.doubleToLongBits(this.x) != Double.doubleToLongBits(vector2d.x)
                ? false
                : Double.doubleToLongBits(this.y) == Double.doubleToLongBits(vector2d.y);
        }
    }

    @Override
    public boolean equals(Vector2dc arg0, double arg1) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (!(arg0 instanceof Vector2dc)) {
            return false;
        } else {
            return !Runtime.equals(this.x, arg0.x(), arg1) ? false : Runtime.equals(this.y, arg0.y(), arg1);
        }
    }

    @Override
    public boolean equals(double arg0, double arg1) {
        return Double.doubleToLongBits(this.x) != Double.doubleToLongBits(arg0) ? false : Double.doubleToLongBits(this.y) == Double.doubleToLongBits(arg1);
    }

    @Override
    public String toString() {
        return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
    }

    public String toString(NumberFormat numberFormat) {
        return "(" + Runtime.format(this.x, numberFormat) + " " + Runtime.format(this.y, numberFormat) + ")";
    }

    public Vector2d fma(Vector2dc arg0, Vector2dc arg1) {
        this.x = this.x + arg0.x() * arg1.x();
        this.y = this.y + arg0.y() * arg1.y();
        return this;
    }

    public Vector2d fma(double arg0, Vector2dc arg1) {
        this.x = this.x + arg0 * arg1.x();
        this.y = this.y + arg0 * arg1.y();
        return this;
    }

    @Override
    public Vector2d fma(Vector2dc arg0, Vector2dc arg1, Vector2d arg2) {
        arg2.x = this.x + arg0.x() * arg1.x();
        arg2.y = this.y + arg0.y() * arg1.y();
        return arg2;
    }

    @Override
    public Vector2d fma(double arg0, Vector2dc arg1, Vector2d arg2) {
        arg2.x = this.x + arg0 * arg1.x();
        arg2.y = this.y + arg0 * arg1.y();
        return arg2;
    }

    public Vector2d min(Vector2dc arg0) {
        this.x = this.x < arg0.x() ? this.x : arg0.x();
        this.y = this.y < arg0.y() ? this.y : arg0.y();
        return this;
    }

    @Override
    public Vector2d min(Vector2dc arg0, Vector2d arg1) {
        arg1.x = this.x < arg0.x() ? this.x : arg0.x();
        arg1.y = this.y < arg0.y() ? this.y : arg0.y();
        return arg1;
    }

    public Vector2d max(Vector2dc arg0) {
        this.x = this.x > arg0.x() ? this.x : arg0.x();
        this.y = this.y > arg0.y() ? this.y : arg0.y();
        return this;
    }

    @Override
    public Vector2d max(Vector2dc arg0, Vector2d arg1) {
        arg1.x = this.x > arg0.x() ? this.x : arg0.x();
        arg1.y = this.y > arg0.y() ? this.y : arg0.y();
        return arg1;
    }

    @Override
    public int maxComponent() {
        double double0 = Math.abs(this.x);
        double double1 = Math.abs(this.y);
        return double0 >= double1 ? 0 : 1;
    }

    @Override
    public int minComponent() {
        double double0 = Math.abs(this.x);
        double double1 = Math.abs(this.y);
        return double0 < double1 ? 0 : 1;
    }

    public Vector2d floor() {
        this.x = Math.floor(this.x);
        this.y = Math.floor(this.y);
        return this;
    }

    @Override
    public Vector2d floor(Vector2d arg0) {
        arg0.x = Math.floor(this.x);
        arg0.y = Math.floor(this.y);
        return arg0;
    }

    public Vector2d ceil() {
        this.x = Math.ceil(this.x);
        this.y = Math.ceil(this.y);
        return this;
    }

    @Override
    public Vector2d ceil(Vector2d arg0) {
        arg0.x = Math.ceil(this.x);
        arg0.y = Math.ceil(this.y);
        return arg0;
    }

    public Vector2d round() {
        this.x = Math.round(this.x);
        this.y = Math.round(this.y);
        return this;
    }

    @Override
    public Vector2d round(Vector2d arg0) {
        arg0.x = Math.round(this.x);
        arg0.y = Math.round(this.y);
        return arg0;
    }

    @Override
    public boolean isFinite() {
        return Math.isFinite(this.x) && Math.isFinite(this.y);
    }

    public Vector2d absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        return this;
    }

    @Override
    public Vector2d absolute(Vector2d arg0) {
        arg0.x = Math.abs(this.x);
        arg0.y = Math.abs(this.y);
        return arg0;
    }
}
