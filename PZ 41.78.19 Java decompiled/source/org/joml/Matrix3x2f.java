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

public class Matrix3x2f implements Matrix3x2fc, Externalizable {
    private static final long serialVersionUID = 1L;
    public float m00;
    public float m01;
    public float m10;
    public float m11;
    public float m20;
    public float m21;

    public Matrix3x2f() {
        this.m00 = 1.0F;
        this.m11 = 1.0F;
    }

    public Matrix3x2f(Matrix3x2fc arg0) {
        if (arg0 instanceof Matrix3x2f) {
            MemUtil.INSTANCE.copy((Matrix3x2f)arg0, this);
        } else {
            this.setMatrix3x2fc(arg0);
        }
    }

    public Matrix3x2f(Matrix2fc arg0) {
        if (arg0 instanceof Matrix2f) {
            MemUtil.INSTANCE.copy((Matrix2f)arg0, this);
        } else {
            this.setMatrix2fc(arg0);
        }
    }

    public Matrix3x2f(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        this.m00 = arg0;
        this.m01 = arg1;
        this.m10 = arg2;
        this.m11 = arg3;
        this.m20 = arg4;
        this.m21 = arg5;
    }

    public Matrix3x2f(FloatBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
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

    @Override
    public float m20() {
        return this.m20;
    }

    @Override
    public float m21() {
        return this.m21;
    }

    Matrix3x2f _m00(float float0) {
        this.m00 = float0;
        return this;
    }

    Matrix3x2f _m01(float float0) {
        this.m01 = float0;
        return this;
    }

    Matrix3x2f _m10(float float0) {
        this.m10 = float0;
        return this;
    }

    Matrix3x2f _m11(float float0) {
        this.m11 = float0;
        return this;
    }

    Matrix3x2f _m20(float float0) {
        this.m20 = float0;
        return this;
    }

    Matrix3x2f _m21(float float0) {
        this.m21 = float0;
        return this;
    }

    public Matrix3x2f set(Matrix3x2fc arg0) {
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
        this.m20 = matrix3x2fc.m20();
        this.m21 = matrix3x2fc.m21();
    }

    public Matrix3x2f set(Matrix2fc arg0) {
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

    public Matrix3x2f mul(Matrix3x2fc arg0) {
        return this.mul(arg0, this);
    }

    @Override
    public Matrix3x2f mul(Matrix3x2fc arg0, Matrix3x2f arg1) {
        float float0 = this.m00 * arg0.m00() + this.m10 * arg0.m01();
        float float1 = this.m01 * arg0.m00() + this.m11 * arg0.m01();
        float float2 = this.m00 * arg0.m10() + this.m10 * arg0.m11();
        float float3 = this.m01 * arg0.m10() + this.m11 * arg0.m11();
        float float4 = this.m00 * arg0.m20() + this.m10 * arg0.m21() + this.m20;
        float float5 = this.m01 * arg0.m20() + this.m11 * arg0.m21() + this.m21;
        arg1.m00 = float0;
        arg1.m01 = float1;
        arg1.m10 = float2;
        arg1.m11 = float3;
        arg1.m20 = float4;
        arg1.m21 = float5;
        return arg1;
    }

    public Matrix3x2f mulLocal(Matrix3x2fc arg0) {
        return this.mulLocal(arg0, this);
    }

    @Override
    public Matrix3x2f mulLocal(Matrix3x2fc arg0, Matrix3x2f arg1) {
        float float0 = arg0.m00() * this.m00 + arg0.m10() * this.m01;
        float float1 = arg0.m01() * this.m00 + arg0.m11() * this.m01;
        float float2 = arg0.m00() * this.m10 + arg0.m10() * this.m11;
        float float3 = arg0.m01() * this.m10 + arg0.m11() * this.m11;
        float float4 = arg0.m00() * this.m20 + arg0.m10() * this.m21 + arg0.m20();
        float float5 = arg0.m01() * this.m20 + arg0.m11() * this.m21 + arg0.m21();
        arg1.m00 = float0;
        arg1.m01 = float1;
        arg1.m10 = float2;
        arg1.m11 = float3;
        arg1.m20 = float4;
        arg1.m21 = float5;
        return arg1;
    }

    public Matrix3x2f set(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        this.m00 = arg0;
        this.m01 = arg1;
        this.m10 = arg2;
        this.m11 = arg3;
        this.m20 = arg4;
        this.m21 = arg5;
        return this;
    }

    public Matrix3x2f set(float[] floats) {
        MemUtil.INSTANCE.copy(floats, 0, this);
        return this;
    }

    @Override
    public float determinant() {
        return this.m00 * this.m11 - this.m01 * this.m10;
    }

    public Matrix3x2f invert() {
        return this.invert(this);
    }

    @Override
    public Matrix3x2f invert(Matrix3x2f arg0) {
        float float0 = 1.0F / (this.m00 * this.m11 - this.m01 * this.m10);
        float float1 = this.m11 * float0;
        float float2 = -this.m01 * float0;
        float float3 = -this.m10 * float0;
        float float4 = this.m00 * float0;
        float float5 = (this.m10 * this.m21 - this.m20 * this.m11) * float0;
        float float6 = (this.m20 * this.m01 - this.m00 * this.m21) * float0;
        arg0.m00 = float1;
        arg0.m01 = float2;
        arg0.m10 = float3;
        arg0.m11 = float4;
        arg0.m20 = float5;
        arg0.m21 = float6;
        return arg0;
    }

    public Matrix3x2f translation(float arg0, float arg1) {
        this.m00 = 1.0F;
        this.m01 = 0.0F;
        this.m10 = 0.0F;
        this.m11 = 1.0F;
        this.m20 = arg0;
        this.m21 = arg1;
        return this;
    }

    public Matrix3x2f translation(Vector2fc arg0) {
        return this.translation(arg0.x(), arg0.y());
    }

    public Matrix3x2f setTranslation(float arg0, float arg1) {
        this.m20 = arg0;
        this.m21 = arg1;
        return this;
    }

    public Matrix3x2f setTranslation(Vector2f arg0) {
        return this.setTranslation(arg0.x, arg0.y);
    }

    @Override
    public Matrix3x2f translate(float arg0, float arg1, Matrix3x2f arg2) {
        arg2.m20 = this.m00 * arg0 + this.m10 * arg1 + this.m20;
        arg2.m21 = this.m01 * arg0 + this.m11 * arg1 + this.m21;
        arg2.m00 = this.m00;
        arg2.m01 = this.m01;
        arg2.m10 = this.m10;
        arg2.m11 = this.m11;
        return arg2;
    }

    public Matrix3x2f translate(float arg0, float arg1) {
        return this.translate(arg0, arg1, this);
    }

    @Override
    public Matrix3x2f translate(Vector2fc arg0, Matrix3x2f arg1) {
        return this.translate(arg0.x(), arg0.y(), arg1);
    }

    public Matrix3x2f translate(Vector2fc arg0) {
        return this.translate(arg0.x(), arg0.y(), this);
    }

    public Matrix3x2f translateLocal(Vector2fc arg0) {
        return this.translateLocal(arg0.x(), arg0.y());
    }

    @Override
    public Matrix3x2f translateLocal(Vector2fc arg0, Matrix3x2f arg1) {
        return this.translateLocal(arg0.x(), arg0.y(), arg1);
    }

    @Override
    public Matrix3x2f translateLocal(float arg0, float arg1, Matrix3x2f arg2) {
        arg2.m00 = this.m00;
        arg2.m01 = this.m01;
        arg2.m10 = this.m10;
        arg2.m11 = this.m11;
        arg2.m20 = this.m20 + arg0;
        arg2.m21 = this.m21 + arg1;
        return arg2;
    }

    public Matrix3x2f translateLocal(float arg0, float arg1) {
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
    public Matrix3x2f get(Matrix3x2f arg0) {
        return arg0.set(this);
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
    public FloatBuffer get3x3(FloatBuffer arg0) {
        MemUtil.INSTANCE.put3x3(this, 0, arg0);
        return arg0;
    }

    @Override
    public FloatBuffer get3x3(int arg0, FloatBuffer arg1) {
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
    public FloatBuffer get4x4(FloatBuffer arg0) {
        MemUtil.INSTANCE.put4x4(this, 0, arg0);
        return arg0;
    }

    @Override
    public FloatBuffer get4x4(int arg0, FloatBuffer arg1) {
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
    public Matrix3x2fc getToAddress(long arg0) {
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
    public float[] get3x3(float[] floats, int int0) {
        MemUtil.INSTANCE.copy3x3(this, floats, int0);
        return floats;
    }

    @Override
    public float[] get3x3(float[] floats) {
        return this.get3x3(floats, 0);
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

    public Matrix3x2f set(FloatBuffer arg0) {
        int int0 = arg0.position();
        MemUtil.INSTANCE.get(this, int0, arg0);
        return this;
    }

    public Matrix3x2f set(ByteBuffer arg0) {
        int int0 = arg0.position();
        MemUtil.INSTANCE.get(this, int0, arg0);
        return this;
    }

    public Matrix3x2f setFromAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.get(this, arg0);
            return this;
        }
    }

    public Matrix3x2f zero() {
        MemUtil.INSTANCE.zero(this);
        return this;
    }

    public Matrix3x2f identity() {
        MemUtil.INSTANCE.identity(this);
        return this;
    }

    @Override
    public Matrix3x2f scale(float arg0, float arg1, Matrix3x2f arg2) {
        arg2.m00 = this.m00 * arg0;
        arg2.m01 = this.m01 * arg0;
        arg2.m10 = this.m10 * arg1;
        arg2.m11 = this.m11 * arg1;
        arg2.m20 = this.m20;
        arg2.m21 = this.m21;
        return arg2;
    }

    public Matrix3x2f scale(float arg0, float arg1) {
        return this.scale(arg0, arg1, this);
    }

    public Matrix3x2f scale(Vector2fc arg0) {
        return this.scale(arg0.x(), arg0.y(), this);
    }

    @Override
    public Matrix3x2f scale(Vector2fc arg0, Matrix3x2f arg1) {
        return this.scale(arg0.x(), arg0.y(), arg1);
    }

    @Override
    public Matrix3x2f scale(float arg0, Matrix3x2f arg1) {
        return this.scale(arg0, arg0, arg1);
    }

    public Matrix3x2f scale(float arg0) {
        return this.scale(arg0, arg0);
    }

    @Override
    public Matrix3x2f scaleLocal(float arg0, float arg1, Matrix3x2f arg2) {
        arg2.m00 = arg0 * this.m00;
        arg2.m01 = arg1 * this.m01;
        arg2.m10 = arg0 * this.m10;
        arg2.m11 = arg1 * this.m11;
        arg2.m20 = arg0 * this.m20;
        arg2.m21 = arg1 * this.m21;
        return arg2;
    }

    public Matrix3x2f scaleLocal(float arg0, float arg1) {
        return this.scaleLocal(arg0, arg1, this);
    }

    @Override
    public Matrix3x2f scaleLocal(float arg0, Matrix3x2f arg1) {
        return this.scaleLocal(arg0, arg0, arg1);
    }

    public Matrix3x2f scaleLocal(float arg0) {
        return this.scaleLocal(arg0, arg0, this);
    }

    @Override
    public Matrix3x2f scaleAround(float arg0, float arg1, float arg2, float arg3, Matrix3x2f arg4) {
        float float0 = this.m00 * arg2 + this.m10 * arg3 + this.m20;
        float float1 = this.m01 * arg2 + this.m11 * arg3 + this.m21;
        arg4.m00 = this.m00 * arg0;
        arg4.m01 = this.m01 * arg0;
        arg4.m10 = this.m10 * arg1;
        arg4.m11 = this.m11 * arg1;
        arg4.m20 = arg4.m00 * -arg2 + arg4.m10 * -arg3 + float0;
        arg4.m21 = arg4.m01 * -arg2 + arg4.m11 * -arg3 + float1;
        return arg4;
    }

    public Matrix3x2f scaleAround(float arg0, float arg1, float arg2, float arg3) {
        return this.scaleAround(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public Matrix3x2f scaleAround(float arg0, float arg1, float arg2, Matrix3x2f arg3) {
        return this.scaleAround(arg0, arg0, arg1, arg2, this);
    }

    public Matrix3x2f scaleAround(float arg0, float arg1, float arg2) {
        return this.scaleAround(arg0, arg0, arg1, arg2, this);
    }

    @Override
    public Matrix3x2f scaleAroundLocal(float arg0, float arg1, float arg2, float arg3, Matrix3x2f arg4) {
        arg4.m00 = arg0 * this.m00;
        arg4.m01 = arg1 * this.m01;
        arg4.m10 = arg0 * this.m10;
        arg4.m11 = arg1 * this.m11;
        arg4.m20 = arg0 * this.m20 - arg0 * arg2 + arg2;
        arg4.m21 = arg1 * this.m21 - arg1 * arg3 + arg3;
        return arg4;
    }

    @Override
    public Matrix3x2f scaleAroundLocal(float arg0, float arg1, float arg2, Matrix3x2f arg3) {
        return this.scaleAroundLocal(arg0, arg0, arg1, arg2, arg3);
    }

    public Matrix3x2f scaleAroundLocal(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        return this.scaleAroundLocal(arg0, arg1, arg3, arg4, this);
    }

    public Matrix3x2f scaleAroundLocal(float arg0, float arg1, float arg2) {
        return this.scaleAroundLocal(arg0, arg0, arg1, arg2, this);
    }

    public Matrix3x2f scaling(float arg0) {
        return this.scaling(arg0, arg0);
    }

    public Matrix3x2f scaling(float arg0, float arg1) {
        this.m00 = arg0;
        this.m01 = 0.0F;
        this.m10 = 0.0F;
        this.m11 = arg1;
        this.m20 = 0.0F;
        this.m21 = 0.0F;
        return this;
    }

    public Matrix3x2f rotation(float arg0) {
        float float0 = Math.cos(arg0);
        float float1 = Math.sin(arg0);
        this.m00 = float0;
        this.m10 = -float1;
        this.m20 = 0.0F;
        this.m01 = float1;
        this.m11 = float0;
        this.m21 = 0.0F;
        return this;
    }

    @Override
    public Vector3f transform(Vector3f arg0) {
        return arg0.mul(this);
    }

    @Override
    public Vector3f transform(Vector3f arg0, Vector3f arg1) {
        return arg0.mul(this, arg1);
    }

    @Override
    public Vector3f transform(float arg0, float arg1, float arg2, Vector3f arg3) {
        return arg3.set(this.m00 * arg0 + this.m10 * arg1 + this.m20 * arg2, this.m01 * arg0 + this.m11 * arg1 + this.m21 * arg2, arg2);
    }

    @Override
    public Vector2f transformPosition(Vector2f arg0) {
        arg0.set(this.m00 * arg0.x + this.m10 * arg0.y + this.m20, this.m01 * arg0.x + this.m11 * arg0.y + this.m21);
        return arg0;
    }

    @Override
    public Vector2f transformPosition(Vector2fc arg0, Vector2f arg1) {
        arg1.set(this.m00 * arg0.x() + this.m10 * arg0.y() + this.m20, this.m01 * arg0.x() + this.m11 * arg0.y() + this.m21);
        return arg1;
    }

    @Override
    public Vector2f transformPosition(float arg0, float arg1, Vector2f arg2) {
        return arg2.set(this.m00 * arg0 + this.m10 * arg1 + this.m20, this.m01 * arg0 + this.m11 * arg1 + this.m21);
    }

    @Override
    public Vector2f transformDirection(Vector2f arg0) {
        arg0.set(this.m00 * arg0.x + this.m10 * arg0.y, this.m01 * arg0.x + this.m11 * arg0.y);
        return arg0;
    }

    @Override
    public Vector2f transformDirection(Vector2fc arg0, Vector2f arg1) {
        arg1.set(this.m00 * arg0.x() + this.m10 * arg0.y(), this.m01 * arg0.x() + this.m11 * arg0.y());
        return arg1;
    }

    @Override
    public Vector2f transformDirection(float arg0, float arg1, Vector2f arg2) {
        return arg2.set(this.m00 * arg0 + this.m10 * arg1, this.m01 * arg0 + this.m11 * arg1);
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        arg0.writeFloat(this.m00);
        arg0.writeFloat(this.m01);
        arg0.writeFloat(this.m10);
        arg0.writeFloat(this.m11);
        arg0.writeFloat(this.m20);
        arg0.writeFloat(this.m21);
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException {
        this.m00 = arg0.readFloat();
        this.m01 = arg0.readFloat();
        this.m10 = arg0.readFloat();
        this.m11 = arg0.readFloat();
        this.m20 = arg0.readFloat();
        this.m21 = arg0.readFloat();
    }

    public Matrix3x2f rotate(float arg0) {
        return this.rotate(arg0, this);
    }

    @Override
    public Matrix3x2f rotate(float arg0, Matrix3x2f arg1) {
        float float0 = Math.cos(arg0);
        float float1 = Math.sin(arg0);
        float float2 = -float1;
        float float3 = this.m00 * float0 + this.m10 * float1;
        float float4 = this.m01 * float0 + this.m11 * float1;
        arg1.m10 = this.m00 * float2 + this.m10 * float0;
        arg1.m11 = this.m01 * float2 + this.m11 * float0;
        arg1.m00 = float3;
        arg1.m01 = float4;
        arg1.m20 = this.m20;
        arg1.m21 = this.m21;
        return arg1;
    }

    @Override
    public Matrix3x2f rotateLocal(float arg0, Matrix3x2f arg1) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = float1 * this.m00 - float0 * this.m01;
        float float3 = float0 * this.m00 + float1 * this.m01;
        float float4 = float1 * this.m10 - float0 * this.m11;
        float float5 = float0 * this.m10 + float1 * this.m11;
        float float6 = float1 * this.m20 - float0 * this.m21;
        float float7 = float0 * this.m20 + float1 * this.m21;
        arg1.m00 = float2;
        arg1.m01 = float3;
        arg1.m10 = float4;
        arg1.m11 = float5;
        arg1.m20 = float6;
        arg1.m21 = float7;
        return arg1;
    }

    public Matrix3x2f rotateLocal(float arg0) {
        return this.rotateLocal(arg0, this);
    }

    public Matrix3x2f rotateAbout(float arg0, float arg1, float arg2) {
        return this.rotateAbout(arg0, arg1, arg2, this);
    }

    @Override
    public Matrix3x2f rotateAbout(float arg0, float arg1, float arg2, Matrix3x2f arg3) {
        float float0 = this.m00 * arg1 + this.m10 * arg2 + this.m20;
        float float1 = this.m01 * arg1 + this.m11 * arg2 + this.m21;
        float float2 = Math.cos(arg0);
        float float3 = Math.sin(arg0);
        float float4 = this.m00 * float2 + this.m10 * float3;
        float float5 = this.m01 * float2 + this.m11 * float3;
        arg3.m10 = this.m00 * -float3 + this.m10 * float2;
        arg3.m11 = this.m01 * -float3 + this.m11 * float2;
        arg3.m00 = float4;
        arg3.m01 = float5;
        arg3.m20 = arg3.m00 * -arg1 + arg3.m10 * -arg2 + float0;
        arg3.m21 = arg3.m01 * -arg1 + arg3.m11 * -arg2 + float1;
        return arg3;
    }

    @Override
    public Matrix3x2f rotateTo(Vector2fc arg0, Vector2fc arg1, Matrix3x2f arg2) {
        float float0 = arg0.x() * arg1.x() + arg0.y() * arg1.y();
        float float1 = arg0.x() * arg1.y() - arg0.y() * arg1.x();
        float float2 = -float1;
        float float3 = this.m00 * float0 + this.m10 * float1;
        float float4 = this.m01 * float0 + this.m11 * float1;
        arg2.m10 = this.m00 * float2 + this.m10 * float0;
        arg2.m11 = this.m01 * float2 + this.m11 * float0;
        arg2.m00 = float3;
        arg2.m01 = float4;
        arg2.m20 = this.m20;
        arg2.m21 = this.m21;
        return arg2;
    }

    public Matrix3x2f rotateTo(Vector2fc arg0, Vector2fc arg1) {
        return this.rotateTo(arg0, arg1, this);
    }

    @Override
    public Matrix3x2f view(float arg0, float arg1, float arg2, float arg3, Matrix3x2f arg4) {
        float float0 = 2.0F / (arg1 - arg0);
        float float1 = 2.0F / (arg3 - arg2);
        float float2 = (arg0 + arg1) / (arg0 - arg1);
        float float3 = (arg2 + arg3) / (arg2 - arg3);
        arg4.m20 = this.m00 * float2 + this.m10 * float3 + this.m20;
        arg4.m21 = this.m01 * float2 + this.m11 * float3 + this.m21;
        arg4.m00 = this.m00 * float0;
        arg4.m01 = this.m01 * float0;
        arg4.m10 = this.m10 * float1;
        arg4.m11 = this.m11 * float1;
        return arg4;
    }

    public Matrix3x2f view(float arg0, float arg1, float arg2, float arg3) {
        return this.view(arg0, arg1, arg2, arg3, this);
    }

    public Matrix3x2f setView(float arg0, float arg1, float arg2, float arg3) {
        this.m00 = 2.0F / (arg1 - arg0);
        this.m01 = 0.0F;
        this.m10 = 0.0F;
        this.m11 = 2.0F / (arg3 - arg2);
        this.m20 = (arg0 + arg1) / (arg0 - arg1);
        this.m21 = (arg2 + arg3) / (arg2 - arg3);
        return this;
    }

    @Override
    public Vector2f origin(Vector2f arg0) {
        float float0 = 1.0F / (this.m00 * this.m11 - this.m01 * this.m10);
        arg0.x = (this.m10 * this.m21 - this.m20 * this.m11) * float0;
        arg0.y = (this.m20 * this.m01 - this.m00 * this.m21) * float0;
        return arg0;
    }

    @Override
    public float[] viewArea(float[] floats) {
        float float0 = 1.0F / (this.m00 * this.m11 - this.m01 * this.m10);
        float float1 = this.m11 * float0;
        float float2 = -this.m01 * float0;
        float float3 = -this.m10 * float0;
        float float4 = this.m00 * float0;
        float float5 = (this.m10 * this.m21 - this.m20 * this.m11) * float0;
        float float6 = (this.m20 * this.m01 - this.m00 * this.m21) * float0;
        float float7 = -float1 - float3;
        float float8 = -float2 - float4;
        float float9 = float1 - float3;
        float float10 = float2 - float4;
        float float11 = -float1 + float3;
        float float12 = -float2 + float4;
        float float13 = float1 + float3;
        float float14 = float2 + float4;
        float float15 = float7 < float11 ? float7 : float11;
        float15 = float15 < float9 ? float15 : float9;
        float15 = float15 < float13 ? float15 : float13;
        float float16 = float8 < float12 ? float8 : float12;
        float16 = float16 < float10 ? float16 : float10;
        float16 = float16 < float14 ? float16 : float14;
        float float17 = float7 > float11 ? float7 : float11;
        float17 = float17 > float9 ? float17 : float9;
        float17 = float17 > float13 ? float17 : float13;
        float float18 = float8 > float12 ? float8 : float12;
        float18 = float18 > float10 ? float18 : float10;
        float18 = float18 > float14 ? float18 : float14;
        floats[0] = float15 + float5;
        floats[1] = float16 + float6;
        floats[2] = float17 + float5;
        floats[3] = float18 + float6;
        return floats;
    }

    @Override
    public Vector2f positiveX(Vector2f arg0) {
        float float0 = this.m00 * this.m11 - this.m01 * this.m10;
        float0 = 1.0F / float0;
        arg0.x = this.m11 * float0;
        arg0.y = -this.m01 * float0;
        return arg0.normalize(arg0);
    }

    @Override
    public Vector2f normalizedPositiveX(Vector2f arg0) {
        arg0.x = this.m11;
        arg0.y = -this.m01;
        return arg0;
    }

    @Override
    public Vector2f positiveY(Vector2f arg0) {
        float float0 = this.m00 * this.m11 - this.m01 * this.m10;
        float0 = 1.0F / float0;
        arg0.x = -this.m10 * float0;
        arg0.y = this.m00 * float0;
        return arg0.normalize(arg0);
    }

    @Override
    public Vector2f normalizedPositiveY(Vector2f arg0) {
        arg0.x = -this.m10;
        arg0.y = this.m00;
        return arg0;
    }

    @Override
    public Vector2f unproject(float float8, float float10, int[] ints, Vector2f vector2f) {
        float float0 = 1.0F / (this.m00 * this.m11 - this.m01 * this.m10);
        float float1 = this.m11 * float0;
        float float2 = -this.m01 * float0;
        float float3 = -this.m10 * float0;
        float float4 = this.m00 * float0;
        float float5 = (this.m10 * this.m21 - this.m20 * this.m11) * float0;
        float float6 = (this.m20 * this.m01 - this.m00 * this.m21) * float0;
        float float7 = (float8 - ints[0]) / ints[2] * 2.0F - 1.0F;
        float float9 = (float10 - ints[1]) / ints[3] * 2.0F - 1.0F;
        vector2f.x = float1 * float7 + float3 * float9 + float5;
        vector2f.y = float2 * float7 + float4 * float9 + float6;
        return vector2f;
    }

    @Override
    public Vector2f unprojectInv(float float1, float float3, int[] ints, Vector2f vector2f) {
        float float0 = (float1 - ints[0]) / ints[2] * 2.0F - 1.0F;
        float float2 = (float3 - ints[1]) / ints[3] * 2.0F - 1.0F;
        vector2f.x = this.m00 * float0 + this.m10 * float2 + this.m20;
        vector2f.y = this.m01 * float0 + this.m11 * float2 + this.m21;
        return vector2f;
    }

    public Matrix3x2f shearX(float arg0) {
        return this.shearX(arg0, this);
    }

    public Matrix3x2f shearX(float arg0, Matrix3x2f arg1) {
        float float0 = this.m00 * arg0 + this.m10;
        float float1 = this.m01 * arg0 + this.m11;
        arg1.m00 = this.m00;
        arg1.m01 = this.m01;
        arg1.m10 = float0;
        arg1.m11 = float1;
        arg1.m20 = this.m20;
        arg1.m21 = this.m21;
        return arg1;
    }

    public Matrix3x2f shearY(float arg0) {
        return this.shearY(arg0, this);
    }

    public Matrix3x2f shearY(float arg0, Matrix3x2f arg1) {
        float float0 = this.m00 + this.m10 * arg0;
        float float1 = this.m01 + this.m11 * arg0;
        arg1.m00 = float0;
        arg1.m01 = float1;
        arg1.m10 = this.m10;
        arg1.m11 = this.m11;
        arg1.m20 = this.m20;
        arg1.m21 = this.m21;
        return arg1;
    }

    public Matrix3x2f span(Vector2f arg0, Vector2f arg1, Vector2f arg2) {
        float float0 = 1.0F / (this.m00 * this.m11 - this.m01 * this.m10);
        float float1 = this.m11 * float0;
        float float2 = -this.m01 * float0;
        float float3 = -this.m10 * float0;
        float float4 = this.m00 * float0;
        arg0.x = -float1 - float3 + (this.m10 * this.m21 - this.m20 * this.m11) * float0;
        arg0.y = -float2 - float4 + (this.m20 * this.m01 - this.m00 * this.m21) * float0;
        arg1.x = 2.0F * float1;
        arg1.y = 2.0F * float2;
        arg2.x = 2.0F * float3;
        arg2.y = 2.0F * float4;
        return this;
    }

    @Override
    public boolean testPoint(float arg0, float arg1) {
        float float0 = this.m00;
        float float1 = this.m10;
        float float2 = 1.0F + this.m20;
        float float3 = -this.m00;
        float float4 = -this.m10;
        float float5 = 1.0F - this.m20;
        float float6 = this.m01;
        float float7 = this.m11;
        float float8 = 1.0F + this.m21;
        float float9 = -this.m01;
        float float10 = -this.m11;
        float float11 = 1.0F - this.m21;
        return float0 * arg0 + float1 * arg1 + float2 >= 0.0F
            && float3 * arg0 + float4 * arg1 + float5 >= 0.0F
            && float6 * arg0 + float7 * arg1 + float8 >= 0.0F
            && float9 * arg0 + float10 * arg1 + float11 >= 0.0F;
    }

    @Override
    public boolean testCircle(float arg0, float arg1, float arg2) {
        float float0 = this.m00;
        float float1 = this.m10;
        float float2 = 1.0F + this.m20;
        float float3 = Math.invsqrt(float0 * float0 + float1 * float1);
        float0 *= float3;
        float1 *= float3;
        float2 *= float3;
        float float4 = -this.m00;
        float float5 = -this.m10;
        float float6 = 1.0F - this.m20;
        float3 = Math.invsqrt(float4 * float4 + float5 * float5);
        float4 *= float3;
        float5 *= float3;
        float6 *= float3;
        float float7 = this.m01;
        float float8 = this.m11;
        float float9 = 1.0F + this.m21;
        float3 = Math.invsqrt(float7 * float7 + float8 * float8);
        float7 *= float3;
        float8 *= float3;
        float9 *= float3;
        float float10 = -this.m01;
        float float11 = -this.m11;
        float float12 = 1.0F - this.m21;
        float3 = Math.invsqrt(float10 * float10 + float11 * float11);
        float10 *= float3;
        float11 *= float3;
        float12 *= float3;
        return float0 * arg0 + float1 * arg1 + float2 >= -arg2
            && float4 * arg0 + float5 * arg1 + float6 >= -arg2
            && float7 * arg0 + float8 * arg1 + float9 >= -arg2
            && float10 * arg0 + float11 * arg1 + float12 >= -arg2;
    }

    @Override
    public boolean testAar(float arg0, float arg1, float arg2, float arg3) {
        float float0 = this.m00;
        float float1 = this.m10;
        float float2 = 1.0F + this.m20;
        float float3 = -this.m00;
        float float4 = -this.m10;
        float float5 = 1.0F - this.m20;
        float float6 = this.m01;
        float float7 = this.m11;
        float float8 = 1.0F + this.m21;
        float float9 = -this.m01;
        float float10 = -this.m11;
        float float11 = 1.0F - this.m21;
        return float0 * (float0 < 0.0F ? arg0 : arg2) + float1 * (float1 < 0.0F ? arg1 : arg3) >= -float2
            && float3 * (float3 < 0.0F ? arg0 : arg2) + float4 * (float4 < 0.0F ? arg1 : arg3) >= -float5
            && float6 * (float6 < 0.0F ? arg0 : arg2) + float7 * (float7 < 0.0F ? arg1 : arg3) >= -float8
            && float9 * (float9 < 0.0F ? arg0 : arg2) + float10 * (float10 < 0.0F ? arg1 : arg3) >= -float11;
    }

    @Override
    public int hashCode() {
        int int0 = 1;
        int0 = 31 * int0 + Float.floatToIntBits(this.m00);
        int0 = 31 * int0 + Float.floatToIntBits(this.m01);
        int0 = 31 * int0 + Float.floatToIntBits(this.m10);
        int0 = 31 * int0 + Float.floatToIntBits(this.m11);
        int0 = 31 * int0 + Float.floatToIntBits(this.m20);
        return 31 * int0 + Float.floatToIntBits(this.m21);
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
            Matrix3x2f matrix3x2f = (Matrix3x2f)arg0;
            if (Float.floatToIntBits(this.m00) != Float.floatToIntBits(matrix3x2f.m00)) {
                return false;
            } else if (Float.floatToIntBits(this.m01) != Float.floatToIntBits(matrix3x2f.m01)) {
                return false;
            } else if (Float.floatToIntBits(this.m10) != Float.floatToIntBits(matrix3x2f.m10)) {
                return false;
            } else if (Float.floatToIntBits(this.m11) != Float.floatToIntBits(matrix3x2f.m11)) {
                return false;
            } else {
                return Float.floatToIntBits(this.m20) != Float.floatToIntBits(matrix3x2f.m20)
                    ? false
                    : Float.floatToIntBits(this.m21) == Float.floatToIntBits(matrix3x2f.m21);
            }
        }
    }

    @Override
    public boolean equals(Matrix3x2fc arg0, float arg1) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (!(arg0 instanceof Matrix3x2f)) {
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
