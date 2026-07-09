// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.NumberFormat;

public class Vector2f implements Externalizable, Vector2fc {
    private static final long serialVersionUID = 1L;
    public float x;
    public float y;

    public Vector2f() {
    }

    public Vector2f(float arg0) {
        this.x = arg0;
        this.y = arg0;
    }

    public Vector2f(float arg0, float arg1) {
        this.x = arg0;
        this.y = arg1;
    }

    public Vector2f(Vector2fc arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
    }

    public Vector2f(Vector2ic arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
    }

    public Vector2f(float[] arg0) {
        this.x = arg0[0];
        this.y = arg0[1];
    }

    public Vector2f(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
    }

    public Vector2f(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
    }

    public Vector2f(FloatBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
    }

    public Vector2f(int arg0, FloatBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
    }

    @Override
    public float x() {
        return this.x;
    }

    @Override
    public float y() {
        return this.y;
    }

    public Vector2f set(float arg0) {
        this.x = arg0;
        this.y = arg0;
        return this;
    }

    public Vector2f set(float arg0, float arg1) {
        this.x = arg0;
        this.y = arg1;
        return this;
    }

    public Vector2f set(double arg0) {
        this.x = (float)arg0;
        this.y = (float)arg0;
        return this;
    }

    public Vector2f set(double arg0, double arg1) {
        this.x = (float)arg0;
        this.y = (float)arg1;
        return this;
    }

    public Vector2f set(Vector2fc arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        return this;
    }

    public Vector2f set(Vector2ic arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        return this;
    }

    public Vector2f set(Vector2dc arg0) {
        this.x = (float)arg0.x();
        this.y = (float)arg0.y();
        return this;
    }

    public Vector2f set(float[] arg0) {
        this.x = arg0[0];
        this.y = arg0[1];
        return this;
    }

    public Vector2f set(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Vector2f set(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
        return this;
    }

    public Vector2f set(FloatBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Vector2f set(int arg0, FloatBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
        return this;
    }

    public Vector2f setFromAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.get(this, arg0);
            return this;
        }
    }

    @Override
    public float get(int arg0) throws IllegalArgumentException {
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
        arg0.x = this.x();
        arg0.y = this.y();
        return arg0;
    }

    @Override
    public Vector2d get(Vector2d arg0) {
        arg0.x = this.x();
        arg0.y = this.y();
        return arg0;
    }

    public Vector2f setComponent(int arg0, float arg1) throws IllegalArgumentException {
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
    public Vector2fc getToAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.put(this, arg0);
            return this;
        }
    }

    public Vector2f perpendicular() {
        float float0 = this.y;
        this.y = this.x * -1.0F;
        this.x = float0;
        return this;
    }

    public Vector2f sub(Vector2fc arg0) {
        this.x = this.x - arg0.x();
        this.y = this.y - arg0.y();
        return this;
    }

    @Override
    public Vector2f sub(Vector2fc arg0, Vector2f arg1) {
        arg1.x = this.x - arg0.x();
        arg1.y = this.y - arg0.y();
        return arg1;
    }

    public Vector2f sub(float arg0, float arg1) {
        this.x -= arg0;
        this.y -= arg1;
        return this;
    }

    @Override
    public Vector2f sub(float arg0, float arg1, Vector2f arg2) {
        arg2.x = this.x - arg0;
        arg2.y = this.y - arg1;
        return arg2;
    }

    @Override
    public float dot(Vector2fc arg0) {
        return this.x * arg0.x() + this.y * arg0.y();
    }

    @Override
    public float angle(Vector2fc arg0) {
        float float0 = this.x * arg0.x() + this.y * arg0.y();
        float float1 = this.x * arg0.y() - this.y * arg0.x();
        return Math.atan2(float1, float0);
    }

    @Override
    public float lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public static float lengthSquared(float arg0, float arg1) {
        return arg0 * arg0 + arg1 * arg1;
    }

    @Override
    public float length() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public static float length(float arg0, float arg1) {
        return Math.sqrt(arg0 * arg0 + arg1 * arg1);
    }

    @Override
    public float distance(Vector2fc arg0) {
        float float0 = this.x - arg0.x();
        float float1 = this.y - arg0.y();
        return Math.sqrt(float0 * float0 + float1 * float1);
    }

    @Override
    public float distanceSquared(Vector2fc arg0) {
        float float0 = this.x - arg0.x();
        float float1 = this.y - arg0.y();
        return float0 * float0 + float1 * float1;
    }

    @Override
    public float distance(float arg0, float arg1) {
        float float0 = this.x - arg0;
        float float1 = this.y - arg1;
        return Math.sqrt(float0 * float0 + float1 * float1);
    }

    @Override
    public float distanceSquared(float arg0, float arg1) {
        float float0 = this.x - arg0;
        float float1 = this.y - arg1;
        return float0 * float0 + float1 * float1;
    }

    public static float distance(float arg0, float arg1, float arg2, float arg3) {
        float float0 = arg0 - arg2;
        float float1 = arg1 - arg3;
        return Math.sqrt(float0 * float0 + float1 * float1);
    }

    public static float distanceSquared(float arg0, float arg1, float arg2, float arg3) {
        float float0 = arg0 - arg2;
        float float1 = arg1 - arg3;
        return float0 * float0 + float1 * float1;
    }

    public Vector2f normalize() {
        float float0 = Math.invsqrt(this.x * this.x + this.y * this.y);
        this.x *= float0;
        this.y *= float0;
        return this;
    }

    @Override
    public Vector2f normalize(Vector2f arg0) {
        float float0 = Math.invsqrt(this.x * this.x + this.y * this.y);
        arg0.x = this.x * float0;
        arg0.y = this.y * float0;
        return arg0;
    }

    public Vector2f normalize(float arg0) {
        float float0 = Math.invsqrt(this.x * this.x + this.y * this.y) * arg0;
        this.x *= float0;
        this.y *= float0;
        return this;
    }

    @Override
    public Vector2f normalize(float arg0, Vector2f arg1) {
        float float0 = Math.invsqrt(this.x * this.x + this.y * this.y) * arg0;
        arg1.x = this.x * float0;
        arg1.y = this.y * float0;
        return arg1;
    }

    public Vector2f add(Vector2fc arg0) {
        this.x = this.x + arg0.x();
        this.y = this.y + arg0.y();
        return this;
    }

    @Override
    public Vector2f add(Vector2fc arg0, Vector2f arg1) {
        arg1.x = this.x + arg0.x();
        arg1.y = this.y + arg0.y();
        return arg1;
    }

    public Vector2f add(float arg0, float arg1) {
        return this.add(arg0, arg1, this);
    }

    @Override
    public Vector2f add(float arg0, float arg1, Vector2f arg2) {
        arg2.x = this.x + arg0;
        arg2.y = this.y + arg1;
        return arg2;
    }

    public Vector2f zero() {
        this.x = 0.0F;
        this.y = 0.0F;
        return this;
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        arg0.writeFloat(this.x);
        arg0.writeFloat(this.y);
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        this.x = arg0.readFloat();
        this.y = arg0.readFloat();
    }

    public Vector2f negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    @Override
    public Vector2f negate(Vector2f arg0) {
        arg0.x = -this.x;
        arg0.y = -this.y;
        return arg0;
    }

    public Vector2f mul(float arg0) {
        this.x *= arg0;
        this.y *= arg0;
        return this;
    }

    @Override
    public Vector2f mul(float arg0, Vector2f arg1) {
        arg1.x = this.x * arg0;
        arg1.y = this.y * arg0;
        return arg1;
    }

    public Vector2f mul(float arg0, float arg1) {
        this.x *= arg0;
        this.y *= arg1;
        return this;
    }

    @Override
    public Vector2f mul(float arg0, float arg1, Vector2f arg2) {
        arg2.x = this.x * arg0;
        arg2.y = this.y * arg1;
        return arg2;
    }

    public Vector2f mul(Vector2fc arg0) {
        this.x = this.x * arg0.x();
        this.y = this.y * arg0.y();
        return this;
    }

    @Override
    public Vector2f mul(Vector2fc arg0, Vector2f arg1) {
        arg1.x = this.x * arg0.x();
        arg1.y = this.y * arg0.y();
        return arg1;
    }

    public Vector2f div(Vector2fc arg0) {
        this.x = this.x / arg0.x();
        this.y = this.y / arg0.y();
        return this;
    }

    @Override
    public Vector2f div(Vector2fc arg0, Vector2f arg1) {
        arg1.x = this.x / arg0.x();
        arg1.y = this.y / arg0.y();
        return arg1;
    }

    public Vector2f div(float arg0) {
        float float0 = 1.0F / arg0;
        this.x *= float0;
        this.y *= float0;
        return this;
    }

    @Override
    public Vector2f div(float arg0, Vector2f arg1) {
        float float0 = 1.0F / arg0;
        arg1.x = this.x * float0;
        arg1.y = this.y * float0;
        return arg1;
    }

    public Vector2f div(float arg0, float arg1) {
        this.x /= arg0;
        this.y /= arg1;
        return this;
    }

    @Override
    public Vector2f div(float arg0, float arg1, Vector2f arg2) {
        arg2.x = this.x / arg0;
        arg2.y = this.y / arg1;
        return arg2;
    }

    public Vector2f mul(Matrix2fc arg0) {
        float float0 = arg0.m00() * this.x + arg0.m10() * this.y;
        float float1 = arg0.m01() * this.x + arg0.m11() * this.y;
        this.x = float0;
        this.y = float1;
        return this;
    }

    @Override
    public Vector2f mul(Matrix2fc arg0, Vector2f arg1) {
        float float0 = arg0.m00() * this.x + arg0.m10() * this.y;
        float float1 = arg0.m01() * this.x + arg0.m11() * this.y;
        arg1.x = float0;
        arg1.y = float1;
        return arg1;
    }

    public Vector2f mul(Matrix2dc arg0) {
        double double0 = arg0.m00() * this.x + arg0.m10() * this.y;
        double double1 = arg0.m01() * this.x + arg0.m11() * this.y;
        this.x = (float)double0;
        this.y = (float)double1;
        return this;
    }

    @Override
    public Vector2f mul(Matrix2dc arg0, Vector2f arg1) {
        double double0 = arg0.m00() * this.x + arg0.m10() * this.y;
        double double1 = arg0.m01() * this.x + arg0.m11() * this.y;
        arg1.x = (float)double0;
        arg1.y = (float)double1;
        return arg1;
    }

    public Vector2f mulTranspose(Matrix2fc arg0) {
        float float0 = arg0.m00() * this.x + arg0.m01() * this.y;
        float float1 = arg0.m10() * this.x + arg0.m11() * this.y;
        this.x = float0;
        this.y = float1;
        return this;
    }

    @Override
    public Vector2f mulTranspose(Matrix2fc arg0, Vector2f arg1) {
        float float0 = arg0.m00() * this.x + arg0.m01() * this.y;
        float float1 = arg0.m10() * this.x + arg0.m11() * this.y;
        arg1.x = float0;
        arg1.y = float1;
        return arg1;
    }

    public Vector2f mulPosition(Matrix3x2fc arg0) {
        this.x = arg0.m00() * this.x + arg0.m10() * this.y + arg0.m20();
        this.y = arg0.m01() * this.x + arg0.m11() * this.y + arg0.m21();
        return this;
    }

    @Override
    public Vector2f mulPosition(Matrix3x2fc arg0, Vector2f arg1) {
        arg1.x = arg0.m00() * this.x + arg0.m10() * this.y + arg0.m20();
        arg1.y = arg0.m01() * this.x + arg0.m11() * this.y + arg0.m21();
        return arg1;
    }

    public Vector2f mulDirection(Matrix3x2fc arg0) {
        this.x = arg0.m00() * this.x + arg0.m10() * this.y;
        this.y = arg0.m01() * this.x + arg0.m11() * this.y;
        return this;
    }

    @Override
    public Vector2f mulDirection(Matrix3x2fc arg0, Vector2f arg1) {
        arg1.x = arg0.m00() * this.x + arg0.m10() * this.y;
        arg1.y = arg0.m01() * this.x + arg0.m11() * this.y;
        return arg1;
    }

    public Vector2f lerp(Vector2fc arg0, float arg1) {
        this.x = this.x + (arg0.x() - this.x) * arg1;
        this.y = this.y + (arg0.y() - this.y) * arg1;
        return this;
    }

    @Override
    public Vector2f lerp(Vector2fc arg0, float arg1, Vector2f arg2) {
        arg2.x = this.x + (arg0.x() - this.x) * arg1;
        arg2.y = this.y + (arg0.y() - this.y) * arg1;
        return arg2;
    }

    @Override
    public int hashCode() {
        int int0 = 1;
        int0 = 31 * int0 + Float.floatToIntBits(this.x);
        return 31 * int0 + Float.floatToIntBits(this.y);
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
            Vector2f vector2f = (Vector2f)arg0;
            return Float.floatToIntBits(this.x) != Float.floatToIntBits(vector2f.x) ? false : Float.floatToIntBits(this.y) == Float.floatToIntBits(vector2f.y);
        }
    }

    @Override
    public boolean equals(Vector2fc arg0, float arg1) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (!(arg0 instanceof Vector2fc)) {
            return false;
        } else {
            return !Runtime.equals(this.x, arg0.x(), arg1) ? false : Runtime.equals(this.y, arg0.y(), arg1);
        }
    }

    @Override
    public boolean equals(float arg0, float arg1) {
        return Float.floatToIntBits(this.x) != Float.floatToIntBits(arg0) ? false : Float.floatToIntBits(this.y) == Float.floatToIntBits(arg1);
    }

    @Override
    public String toString() {
        return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
    }

    public String toString(NumberFormat numberFormat) {
        return "(" + Runtime.format(this.x, numberFormat) + " " + Runtime.format(this.y, numberFormat) + ")";
    }

    public Vector2f fma(Vector2fc arg0, Vector2fc arg1) {
        this.x = this.x + arg0.x() * arg1.x();
        this.y = this.y + arg0.y() * arg1.y();
        return this;
    }

    public Vector2f fma(float arg0, Vector2fc arg1) {
        this.x = this.x + arg0 * arg1.x();
        this.y = this.y + arg0 * arg1.y();
        return this;
    }

    @Override
    public Vector2f fma(Vector2fc arg0, Vector2fc arg1, Vector2f arg2) {
        arg2.x = this.x + arg0.x() * arg1.x();
        arg2.y = this.y + arg0.y() * arg1.y();
        return arg2;
    }

    @Override
    public Vector2f fma(float arg0, Vector2fc arg1, Vector2f arg2) {
        arg2.x = this.x + arg0 * arg1.x();
        arg2.y = this.y + arg0 * arg1.y();
        return arg2;
    }

    public Vector2f min(Vector2fc arg0) {
        this.x = this.x < arg0.x() ? this.x : arg0.x();
        this.y = this.y < arg0.y() ? this.y : arg0.y();
        return this;
    }

    @Override
    public Vector2f min(Vector2fc arg0, Vector2f arg1) {
        arg1.x = this.x < arg0.x() ? this.x : arg0.x();
        arg1.y = this.y < arg0.y() ? this.y : arg0.y();
        return arg1;
    }

    public Vector2f max(Vector2fc arg0) {
        this.x = this.x > arg0.x() ? this.x : arg0.x();
        this.y = this.y > arg0.y() ? this.y : arg0.y();
        return this;
    }

    @Override
    public Vector2f max(Vector2fc arg0, Vector2f arg1) {
        arg1.x = this.x > arg0.x() ? this.x : arg0.x();
        arg1.y = this.y > arg0.y() ? this.y : arg0.y();
        return arg1;
    }

    @Override
    public int maxComponent() {
        float float0 = Math.abs(this.x);
        float float1 = Math.abs(this.y);
        return float0 >= float1 ? 0 : 1;
    }

    @Override
    public int minComponent() {
        float float0 = Math.abs(this.x);
        float float1 = Math.abs(this.y);
        return float0 < float1 ? 0 : 1;
    }

    public Vector2f floor() {
        this.x = Math.floor(this.x);
        this.y = Math.floor(this.y);
        return this;
    }

    @Override
    public Vector2f floor(Vector2f arg0) {
        arg0.x = Math.floor(this.x);
        arg0.y = Math.floor(this.y);
        return arg0;
    }

    public Vector2f ceil() {
        this.x = Math.ceil(this.x);
        this.y = Math.ceil(this.y);
        return this;
    }

    @Override
    public Vector2f ceil(Vector2f arg0) {
        arg0.x = Math.ceil(this.x);
        arg0.y = Math.ceil(this.y);
        return arg0;
    }

    public Vector2f round() {
        this.x = Math.ceil(this.x);
        this.y = Math.ceil(this.y);
        return this;
    }

    @Override
    public Vector2f round(Vector2f arg0) {
        arg0.x = Math.round(this.x);
        arg0.y = Math.round(this.y);
        return arg0;
    }

    @Override
    public boolean isFinite() {
        return Math.isFinite(this.x) && Math.isFinite(this.y);
    }

    public Vector2f absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        return this;
    }

    @Override
    public Vector2f absolute(Vector2f arg0) {
        arg0.x = Math.abs(this.x);
        arg0.y = Math.abs(this.y);
        return arg0;
    }
}
