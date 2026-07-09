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

public class Matrix2f implements Externalizable, Matrix2fc {
    private static final long serialVersionUID = 1L;
    public float m00;
    public float m01;
    public float m10;
    public float m11;

    public Matrix2f() {
        this.m00 = 1.0F;
        this.m11 = 1.0F;
    }

    public Matrix2f(Matrix2fc arg0) {
        if (arg0 instanceof Matrix2f) {
            MemUtil.INSTANCE.copy((Matrix2f)arg0, this);
        } else {
            this.setMatrix2fc(arg0);
        }
    }

    public Matrix2f(Matrix3fc arg0) {
        if (arg0 instanceof Matrix3f) {
            MemUtil.INSTANCE.copy((Matrix3f)arg0, this);
        } else {
            this.setMatrix3fc(arg0);
        }
    }

    public Matrix2f(float arg0, float arg1, float arg2, float arg3) {
        this.m00 = arg0;
        this.m01 = arg1;
        this.m10 = arg2;
        this.m11 = arg3;
    }

    public Matrix2f(FloatBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
    }

    public Matrix2f(Vector2fc arg0, Vector2fc arg1) {
        this.m00 = arg0.x();
        this.m01 = arg0.y();
        this.m10 = arg1.x();
        this.m11 = arg1.y();
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
    public float m10() {
        return this.m10;
    }

    @Override
    public float m11() {
        return this.m11;
    }

    public Matrix2f m00(float arg0) {
        this.m00 = arg0;
        return this;
    }

    public Matrix2f m01(float arg0) {
        this.m01 = arg0;
        return this;
    }

    public Matrix2f m10(float arg0) {
        this.m10 = arg0;
        return this;
    }

    public Matrix2f m11(float arg0) {
        this.m11 = arg0;
        return this;
    }

    Matrix2f _m00(float float0) {
        this.m00 = float0;
        return this;
    }

    Matrix2f _m01(float float0) {
        this.m01 = float0;
        return this;
    }

    Matrix2f _m10(float float0) {
        this.m10 = float0;
        return this;
    }

    Matrix2f _m11(float float0) {
        this.m11 = float0;
        return this;
    }

    public Matrix2f set(Matrix2fc arg0) {
        if (arg0 instanceof Matrix2f) {
            MemUtil.INSTANCE.copy((Matrix2f)arg0, this);
        } else {
            this.setMatrix2fc(arg0);
        }

        return this;
    }

    private void setMatrix2fc(Matrix2fc matrix2fc) {
        this.m00 = matrix2fc.m00();
        this.m01 = matrix2fc.m01();
        this.m10 = matrix2fc.m10();
        this.m11 = matrix2fc.m11();
    }

    public Matrix2f set(Matrix3x2fc arg0) {
        if (arg0 instanceof Matrix3x2f) {
            MemUtil.INSTANCE.copy((Matrix3x2f)arg0, this);
        } else {
            this.setMatrix3x2fc(arg0);
        }

        return this;
    }

    private void setMatrix3x2fc(Matrix3x2fc matrix3x2fc) {
        this.m00 = matrix3x2fc.m00();
        this.m01 = matrix3x2fc.m01();
        this.m10 = matrix3x2fc.m10();
        this.m11 = matrix3x2fc.m11();
    }

    public Matrix2f set(Matrix3fc arg0) {
        if (arg0 instanceof Matrix3f) {
            MemUtil.INSTANCE.copy((Matrix3f)arg0, this);
        } else {
            this.setMatrix3fc(arg0);
        }

        return this;
    }

    private void setMatrix3fc(Matrix3fc matrix3fc) {
        this.m00 = matrix3fc.m00();
        this.m01 = matrix3fc.m01();
        this.m10 = matrix3fc.m10();
        this.m11 = matrix3fc.m11();
    }

    public Matrix2f mul(Matrix2fc arg0) {
        return this.mul(arg0, this);
    }

    @Override
    public Matrix2f mul(Matrix2fc arg0, Matrix2f arg1) {
        float float0 = this.m00 * arg0.m00() + this.m10 * arg0.m01();
        float float1 = this.m01 * arg0.m00() + this.m11 * arg0.m01();
        float float2 = this.m00 * arg0.m10() + this.m10 * arg0.m11();
        float float3 = this.m01 * arg0.m10() + this.m11 * arg0.m11();
        arg1.m00 = float0;
        arg1.m01 = float1;
        arg1.m10 = float2;
        arg1.m11 = float3;
        return arg1;
    }

    public Matrix2f mulLocal(Matrix2fc arg0) {
        return this.mulLocal(arg0, this);
    }

    @Override
    public Matrix2f mulLocal(Matrix2fc arg0, Matrix2f arg1) {
        float float0 = arg0.m00() * this.m00 + arg0.m10() * this.m01;
        float float1 = arg0.m01() * this.m00 + arg0.m11() * this.m01;
        float float2 = arg0.m00() * this.m10 + arg0.m10() * this.m11;
        float float3 = arg0.m01() * this.m10 + arg0.m11() * this.m11;
        arg1.m00 = float0;
        arg1.m01 = float1;
        arg1.m10 = float2;
        arg1.m11 = float3;
        return arg1;
    }

    public Matrix2f set(float arg0, float arg1, float arg2, float arg3) {
        this.m00 = arg0;
        this.m01 = arg1;
        this.m10 = arg2;
        this.m11 = arg3;
        return this;
    }

    public Matrix2f set(float[] floats) {
        MemUtil.INSTANCE.copy(floats, 0, this);
        return this;
    }

    public Matrix2f set(Vector2fc arg0, Vector2fc arg1) {
        this.m00 = arg0.x();
        this.m01 = arg0.y();
        this.m10 = arg1.x();
        this.m11 = arg1.y();
        return this;
    }

    @Override
    public float determinant() {
        return this.m00 * this.m11 - this.m10 * this.m01;
    }

    public Matrix2f invert() {
        return this.invert(this);
    }

    @Override
    public Matrix2f invert(Matrix2f arg0) {
        float float0 = 1.0F / this.determinant();
        float float1 = this.m11 * float0;
        float float2 = -this.m01 * float0;
        float float3 = -this.m10 * float0;
        float float4 = this.m00 * float0;
        arg0.m00 = float1;
        arg0.m01 = float2;
        arg0.m10 = float3;
        arg0.m11 = float4;
        return arg0;
    }

    public Matrix2f transpose() {
        return this.transpose(this);
    }

    @Override
    public Matrix2f transpose(Matrix2f arg0) {
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
    public Matrix2f get(Matrix2f arg0) {
        return arg0.set(this);
    }

    @Override
    public Matrix3x2f get(Matrix3x2f arg0) {
        return arg0.set(this);
    }

    @Override
    public Matrix3f get(Matrix3f arg0) {
        return arg0.set(this);
    }

    @Override
    public float getRotation() {
        return Math.atan2(this.m01, this.m11);
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
    public FloatBuffer getTransposed(FloatBuffer arg0) {
        return this.get(arg0.position(), arg0);
    }

    @Override
    public FloatBuffer getTransposed(int arg0, FloatBuffer arg1) {
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
    public Matrix2fc getToAddress(long arg0) {
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

    public Matrix2f set(FloatBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Matrix2f set(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Matrix2f setFromAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.get(this, arg0);
            return this;
        }
    }

    public Matrix2f zero() {
        MemUtil.INSTANCE.zero(this);
        return this;
    }

    public Matrix2f identity() {
        MemUtil.INSTANCE.identity(this);
        return this;
    }

    @Override
    public Matrix2f scale(Vector2fc arg0, Matrix2f arg1) {
        return this.scale(arg0.x(), arg0.y(), arg1);
    }

    public Matrix2f scale(Vector2fc arg0) {
        return this.scale(arg0.x(), arg0.y(), this);
    }

    @Override
    public Matrix2f scale(float arg0, float arg1, Matrix2f arg2) {
        arg2.m00 = this.m00 * arg0;
        arg2.m01 = this.m01 * arg0;
        arg2.m10 = this.m10 * arg1;
        arg2.m11 = this.m11 * arg1;
        return arg2;
    }

    public Matrix2f scale(float arg0, float arg1) {
        return this.scale(arg0, arg1, this);
    }

    @Override
    public Matrix2f scale(float arg0, Matrix2f arg1) {
        return this.scale(arg0, arg0, arg1);
    }

    public Matrix2f scale(float arg0) {
        return this.scale(arg0, arg0);
    }

    @Override
    public Matrix2f scaleLocal(float arg0, float arg1, Matrix2f arg2) {
        arg2.m00 = arg0 * this.m00;
        arg2.m01 = arg1 * this.m01;
        arg2.m10 = arg0 * this.m10;
        arg2.m11 = arg1 * this.m11;
        return arg2;
    }

    public Matrix2f scaleLocal(float arg0, float arg1) {
        return this.scaleLocal(arg0, arg1, this);
    }

    public Matrix2f scaling(float arg0) {
        MemUtil.INSTANCE.zero(this);
        this.m00 = arg0;
        this.m11 = arg0;
        return this;
    }

    public Matrix2f scaling(float arg0, float arg1) {
        MemUtil.INSTANCE.zero(this);
        this.m00 = arg0;
        this.m11 = arg1;
        return this;
    }

    public Matrix2f scaling(Vector2fc arg0) {
        return this.scaling(arg0.x(), arg0.y());
    }

    public Matrix2f rotation(float arg0) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        this.m00 = float1;
        this.m01 = float0;
        this.m10 = -float0;
        this.m11 = float1;
        return this;
    }

    @Override
    public Vector2f transform(Vector2f arg0) {
        return arg0.mul(this);
    }

    @Override
    public Vector2f transform(Vector2fc arg0, Vector2f arg1) {
        arg0.mul(this, arg1);
        return arg1;
    }

    @Override
    public Vector2f transform(float arg0, float arg1, Vector2f arg2) {
        arg2.set(this.m00 * arg0 + this.m10 * arg1, this.m01 * arg0 + this.m11 * arg1);
        return arg2;
    }

    @Override
    public Vector2f transformTranspose(Vector2f arg0) {
        return arg0.mulTranspose(this);
    }

    @Override
    public Vector2f transformTranspose(Vector2fc arg0, Vector2f arg1) {
        arg0.mulTranspose(this, arg1);
        return arg1;
    }

    @Override
    public Vector2f transformTranspose(float arg0, float arg1, Vector2f arg2) {
        arg2.set(this.m00 * arg0 + this.m01 * arg1, this.m10 * arg0 + this.m11 * arg1);
        return arg2;
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        arg0.writeFloat(this.m00);
        arg0.writeFloat(this.m01);
        arg0.writeFloat(this.m10);
        arg0.writeFloat(this.m11);
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException {
        this.m00 = arg0.readFloat();
        this.m01 = arg0.readFloat();
        this.m10 = arg0.readFloat();
        this.m11 = arg0.readFloat();
    }

    public Matrix2f rotate(float arg0) {
        return this.rotate(arg0, this);
    }

    @Override
    public Matrix2f rotate(float arg0, Matrix2f arg1) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = this.m00 * float1 + this.m10 * float0;
        float float3 = this.m01 * float1 + this.m11 * float0;
        float float4 = this.m10 * float1 - this.m00 * float0;
        float float5 = this.m11 * float1 - this.m01 * float0;
        arg1.m00 = float2;
        arg1.m01 = float3;
        arg1.m10 = float4;
        arg1.m11 = float5;
        return arg1;
    }

    public Matrix2f rotateLocal(float arg0) {
        return this.rotateLocal(arg0, this);
    }

    @Override
    public Matrix2f rotateLocal(float arg0, Matrix2f arg1) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = float1 * this.m00 - float0 * this.m01;
        float float3 = float0 * this.m00 + float1 * this.m01;
        float float4 = float1 * this.m10 - float0 * this.m11;
        float float5 = float0 * this.m10 + float1 * this.m11;
        arg1.m00 = float2;
        arg1.m01 = float3;
        arg1.m10 = float4;
        arg1.m11 = float5;
        return arg1;
    }

    @Override
    public Vector2f getRow(int arg0, Vector2f arg1) throws IndexOutOfBoundsException {
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

    public Matrix2f setRow(int arg0, Vector2fc arg1) throws IndexOutOfBoundsException {
        return this.setRow(arg0, arg1.x(), arg1.y());
    }

    public Matrix2f setRow(int arg0, float arg1, float arg2) throws IndexOutOfBoundsException {
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
    public Vector2f getColumn(int arg0, Vector2f arg1) throws IndexOutOfBoundsException {
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

    public Matrix2f setColumn(int arg0, Vector2fc arg1) throws IndexOutOfBoundsException {
        return this.setColumn(arg0, arg1.x(), arg1.y());
    }

    public Matrix2f setColumn(int arg0, float arg1, float arg2) throws IndexOutOfBoundsException {
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
    public float get(int arg0, int arg1) {
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

    public Matrix2f set(int arg0, int arg1, float arg2) {
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

    public Matrix2f normal() {
        return this.normal(this);
    }

    @Override
    public Matrix2f normal(Matrix2f arg0) {
        float float0 = this.m00 * this.m11 - this.m10 * this.m01;
        float float1 = 1.0F / float0;
        float float2 = this.m11 * float1;
        float float3 = -this.m10 * float1;
        float float4 = -this.m01 * float1;
        float float5 = this.m00 * float1;
        arg0.m00 = float2;
        arg0.m01 = float3;
        arg0.m10 = float4;
        arg0.m11 = float5;
        return arg0;
    }

    @Override
    public Vector2f getScale(Vector2f arg0) {
        arg0.x = Math.sqrt(this.m00 * this.m00 + this.m01 * this.m01);
        arg0.y = Math.sqrt(this.m10 * this.m10 + this.m11 * this.m11);
        return arg0;
    }

    @Override
    public Vector2f positiveX(Vector2f arg0) {
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
    public Vector2f normalizedPositiveX(Vector2f arg0) {
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
    public Vector2f positiveY(Vector2f arg0) {
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
    public Vector2f normalizedPositiveY(Vector2f arg0) {
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
        int0 = 31 * int0 + Float.floatToIntBits(this.m00);
        int0 = 31 * int0 + Float.floatToIntBits(this.m01);
        int0 = 31 * int0 + Float.floatToIntBits(this.m10);
        return 31 * int0 + Float.floatToIntBits(this.m11);
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
            Matrix2f matrix2f = (Matrix2f)arg0;
            if (Float.floatToIntBits(this.m00) != Float.floatToIntBits(matrix2f.m00)) {
                return false;
            } else if (Float.floatToIntBits(this.m01) != Float.floatToIntBits(matrix2f.m01)) {
                return false;
            } else {
                return Float.floatToIntBits(this.m10) != Float.floatToIntBits(matrix2f.m10)
                    ? false
                    : Float.floatToIntBits(this.m11) == Float.floatToIntBits(matrix2f.m11);
            }
        }
    }

    @Override
    public boolean equals(Matrix2fc arg0, float arg1) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (!(arg0 instanceof Matrix2f)) {
            return false;
        } else if (!Runtime.equals(this.m00, arg0.m00(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.m01, arg0.m01(), arg1)) {
            return false;
        } else {
            return !Runtime.equals(this.m10, arg0.m10(), arg1) ? false : Runtime.equals(this.m11, arg0.m11(), arg1);
        }
    }

    public Matrix2f swap(Matrix2f arg0) {
        MemUtil.INSTANCE.swap(this, arg0);
        return this;
    }

    public Matrix2f add(Matrix2fc arg0) {
        return this.add(arg0, this);
    }

    @Override
    public Matrix2f add(Matrix2fc arg0, Matrix2f arg1) {
        arg1.m00 = this.m00 + arg0.m00();
        arg1.m01 = this.m01 + arg0.m01();
        arg1.m10 = this.m10 + arg0.m10();
        arg1.m11 = this.m11 + arg0.m11();
        return arg1;
    }

    public Matrix2f sub(Matrix2fc arg0) {
        return this.sub(arg0, this);
    }

    @Override
    public Matrix2f sub(Matrix2fc arg0, Matrix2f arg1) {
        arg1.m00 = this.m00 - arg0.m00();
        arg1.m01 = this.m01 - arg0.m01();
        arg1.m10 = this.m10 - arg0.m10();
        arg1.m11 = this.m11 - arg0.m11();
        return arg1;
    }

    public Matrix2f mulComponentWise(Matrix2fc arg0) {
        return this.sub(arg0, this);
    }

    @Override
    public Matrix2f mulComponentWise(Matrix2fc arg0, Matrix2f arg1) {
        arg1.m00 = this.m00 * arg0.m00();
        arg1.m01 = this.m01 * arg0.m01();
        arg1.m10 = this.m10 * arg0.m10();
        arg1.m11 = this.m11 * arg0.m11();
        return arg1;
    }

    public Matrix2f lerp(Matrix2fc arg0, float arg1) {
        return this.lerp(arg0, arg1, this);
    }

    @Override
    public Matrix2f lerp(Matrix2fc arg0, float arg1, Matrix2f arg2) {
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
