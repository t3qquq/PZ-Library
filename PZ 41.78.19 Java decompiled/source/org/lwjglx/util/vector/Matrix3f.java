// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.vector;

import java.io.Serializable;
import java.nio.FloatBuffer;

public class Matrix3f extends Matrix implements Serializable {
    private static final long serialVersionUID = 1L;
    public float m00;
    public float m01;
    public float m02;
    public float m10;
    public float m11;
    public float m12;
    public float m20;
    public float m21;
    public float m22;

    public Matrix3f() {
        this.setIdentity();
    }

    public Matrix3f load(Matrix3f matrix3f0) {
        return load(matrix3f0, this);
    }

    public static Matrix3f load(Matrix3f matrix3f1, Matrix3f matrix3f0) {
        if (matrix3f0 == null) {
            matrix3f0 = new Matrix3f();
        }

        matrix3f0.m00 = matrix3f1.m00;
        matrix3f0.m10 = matrix3f1.m10;
        matrix3f0.m20 = matrix3f1.m20;
        matrix3f0.m01 = matrix3f1.m01;
        matrix3f0.m11 = matrix3f1.m11;
        matrix3f0.m21 = matrix3f1.m21;
        matrix3f0.m02 = matrix3f1.m02;
        matrix3f0.m12 = matrix3f1.m12;
        matrix3f0.m22 = matrix3f1.m22;
        return matrix3f0;
    }

    @Override
    public Matrix load(FloatBuffer floatBuffer) {
        this.m00 = floatBuffer.get();
        this.m01 = floatBuffer.get();
        this.m02 = floatBuffer.get();
        this.m10 = floatBuffer.get();
        this.m11 = floatBuffer.get();
        this.m12 = floatBuffer.get();
        this.m20 = floatBuffer.get();
        this.m21 = floatBuffer.get();
        this.m22 = floatBuffer.get();
        return this;
    }

    @Override
    public Matrix loadTranspose(FloatBuffer floatBuffer) {
        this.m00 = floatBuffer.get();
        this.m10 = floatBuffer.get();
        this.m20 = floatBuffer.get();
        this.m01 = floatBuffer.get();
        this.m11 = floatBuffer.get();
        this.m21 = floatBuffer.get();
        this.m02 = floatBuffer.get();
        this.m12 = floatBuffer.get();
        this.m22 = floatBuffer.get();
        return this;
    }

    @Override
    public Matrix store(FloatBuffer floatBuffer) {
        floatBuffer.put(this.m00);
        floatBuffer.put(this.m01);
        floatBuffer.put(this.m02);
        floatBuffer.put(this.m10);
        floatBuffer.put(this.m11);
        floatBuffer.put(this.m12);
        floatBuffer.put(this.m20);
        floatBuffer.put(this.m21);
        floatBuffer.put(this.m22);
        return this;
    }

    @Override
    public Matrix storeTranspose(FloatBuffer floatBuffer) {
        floatBuffer.put(this.m00);
        floatBuffer.put(this.m10);
        floatBuffer.put(this.m20);
        floatBuffer.put(this.m01);
        floatBuffer.put(this.m11);
        floatBuffer.put(this.m21);
        floatBuffer.put(this.m02);
        floatBuffer.put(this.m12);
        floatBuffer.put(this.m22);
        return this;
    }

    public static Matrix3f add(Matrix3f matrix3f2, Matrix3f matrix3f1, Matrix3f matrix3f0) {
        if (matrix3f0 == null) {
            matrix3f0 = new Matrix3f();
        }

        matrix3f0.m00 = matrix3f2.m00 + matrix3f1.m00;
        matrix3f0.m01 = matrix3f2.m01 + matrix3f1.m01;
        matrix3f0.m02 = matrix3f2.m02 + matrix3f1.m02;
        matrix3f0.m10 = matrix3f2.m10 + matrix3f1.m10;
        matrix3f0.m11 = matrix3f2.m11 + matrix3f1.m11;
        matrix3f0.m12 = matrix3f2.m12 + matrix3f1.m12;
        matrix3f0.m20 = matrix3f2.m20 + matrix3f1.m20;
        matrix3f0.m21 = matrix3f2.m21 + matrix3f1.m21;
        matrix3f0.m22 = matrix3f2.m22 + matrix3f1.m22;
        return matrix3f0;
    }

    public static Matrix3f sub(Matrix3f matrix3f2, Matrix3f matrix3f1, Matrix3f matrix3f0) {
        if (matrix3f0 == null) {
            matrix3f0 = new Matrix3f();
        }

        matrix3f0.m00 = matrix3f2.m00 - matrix3f1.m00;
        matrix3f0.m01 = matrix3f2.m01 - matrix3f1.m01;
        matrix3f0.m02 = matrix3f2.m02 - matrix3f1.m02;
        matrix3f0.m10 = matrix3f2.m10 - matrix3f1.m10;
        matrix3f0.m11 = matrix3f2.m11 - matrix3f1.m11;
        matrix3f0.m12 = matrix3f2.m12 - matrix3f1.m12;
        matrix3f0.m20 = matrix3f2.m20 - matrix3f1.m20;
        matrix3f0.m21 = matrix3f2.m21 - matrix3f1.m21;
        matrix3f0.m22 = matrix3f2.m22 - matrix3f1.m22;
        return matrix3f0;
    }

    public static Matrix3f mul(Matrix3f matrix3f2, Matrix3f matrix3f1, Matrix3f matrix3f0) {
        if (matrix3f0 == null) {
            matrix3f0 = new Matrix3f();
        }

        float float0 = matrix3f2.m00 * matrix3f1.m00 + matrix3f2.m10 * matrix3f1.m01 + matrix3f2.m20 * matrix3f1.m02;
        float float1 = matrix3f2.m01 * matrix3f1.m00 + matrix3f2.m11 * matrix3f1.m01 + matrix3f2.m21 * matrix3f1.m02;
        float float2 = matrix3f2.m02 * matrix3f1.m00 + matrix3f2.m12 * matrix3f1.m01 + matrix3f2.m22 * matrix3f1.m02;
        float float3 = matrix3f2.m00 * matrix3f1.m10 + matrix3f2.m10 * matrix3f1.m11 + matrix3f2.m20 * matrix3f1.m12;
        float float4 = matrix3f2.m01 * matrix3f1.m10 + matrix3f2.m11 * matrix3f1.m11 + matrix3f2.m21 * matrix3f1.m12;
        float float5 = matrix3f2.m02 * matrix3f1.m10 + matrix3f2.m12 * matrix3f1.m11 + matrix3f2.m22 * matrix3f1.m12;
        float float6 = matrix3f2.m00 * matrix3f1.m20 + matrix3f2.m10 * matrix3f1.m21 + matrix3f2.m20 * matrix3f1.m22;
        float float7 = matrix3f2.m01 * matrix3f1.m20 + matrix3f2.m11 * matrix3f1.m21 + matrix3f2.m21 * matrix3f1.m22;
        float float8 = matrix3f2.m02 * matrix3f1.m20 + matrix3f2.m12 * matrix3f1.m21 + matrix3f2.m22 * matrix3f1.m22;
        matrix3f0.m00 = float0;
        matrix3f0.m01 = float1;
        matrix3f0.m02 = float2;
        matrix3f0.m10 = float3;
        matrix3f0.m11 = float4;
        matrix3f0.m12 = float5;
        matrix3f0.m20 = float6;
        matrix3f0.m21 = float7;
        matrix3f0.m22 = float8;
        return matrix3f0;
    }

    public static Vector3f transform(Matrix3f matrix3f, Vector3f vector3f1, Vector3f vector3f0) {
        if (vector3f0 == null) {
            vector3f0 = new Vector3f();
        }

        float float0 = matrix3f.m00 * vector3f1.x + matrix3f.m10 * vector3f1.y + matrix3f.m20 * vector3f1.z;
        float float1 = matrix3f.m01 * vector3f1.x + matrix3f.m11 * vector3f1.y + matrix3f.m21 * vector3f1.z;
        float float2 = matrix3f.m02 * vector3f1.x + matrix3f.m12 * vector3f1.y + matrix3f.m22 * vector3f1.z;
        vector3f0.x = float0;
        vector3f0.y = float1;
        vector3f0.z = float2;
        return vector3f0;
    }

    @Override
    public Matrix transpose() {
        return transpose(this, this);
    }

    public Matrix3f transpose(Matrix3f matrix3f1) {
        return transpose(this, matrix3f1);
    }

    public static Matrix3f transpose(Matrix3f matrix3f1, Matrix3f matrix3f0) {
        if (matrix3f0 == null) {
            matrix3f0 = new Matrix3f();
        }

        float float0 = matrix3f1.m00;
        float float1 = matrix3f1.m10;
        float float2 = matrix3f1.m20;
        float float3 = matrix3f1.m01;
        float float4 = matrix3f1.m11;
        float float5 = matrix3f1.m21;
        float float6 = matrix3f1.m02;
        float float7 = matrix3f1.m12;
        float float8 = matrix3f1.m22;
        matrix3f0.m00 = float0;
        matrix3f0.m01 = float1;
        matrix3f0.m02 = float2;
        matrix3f0.m10 = float3;
        matrix3f0.m11 = float4;
        matrix3f0.m12 = float5;
        matrix3f0.m20 = float6;
        matrix3f0.m21 = float7;
        matrix3f0.m22 = float8;
        return matrix3f0;
    }

    @Override
    public float determinant() {
        return this.m00 * (this.m11 * this.m22 - this.m12 * this.m21)
            + this.m01 * (this.m12 * this.m20 - this.m10 * this.m22)
            + this.m02 * (this.m10 * this.m21 - this.m11 * this.m20);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.m00).append(' ').append(this.m10).append(' ').append(this.m20).append(' ').append('\n');
        stringBuilder.append(this.m01).append(' ').append(this.m11).append(' ').append(this.m21).append(' ').append('\n');
        stringBuilder.append(this.m02).append(' ').append(this.m12).append(' ').append(this.m22).append(' ').append('\n');
        return stringBuilder.toString();
    }

    @Override
    public Matrix invert() {
        return invert(this, this);
    }

    public static Matrix3f invert(Matrix3f matrix3f0, Matrix3f matrix3f1) {
        float float0 = matrix3f0.determinant();
        if (float0 != 0.0F) {
            if (matrix3f1 == null) {
                matrix3f1 = new Matrix3f();
            }

            float float1 = 1.0F / float0;
            float float2 = matrix3f0.m11 * matrix3f0.m22 - matrix3f0.m12 * matrix3f0.m21;
            float float3 = -matrix3f0.m10 * matrix3f0.m22 + matrix3f0.m12 * matrix3f0.m20;
            float float4 = matrix3f0.m10 * matrix3f0.m21 - matrix3f0.m11 * matrix3f0.m20;
            float float5 = -matrix3f0.m01 * matrix3f0.m22 + matrix3f0.m02 * matrix3f0.m21;
            float float6 = matrix3f0.m00 * matrix3f0.m22 - matrix3f0.m02 * matrix3f0.m20;
            float float7 = -matrix3f0.m00 * matrix3f0.m21 + matrix3f0.m01 * matrix3f0.m20;
            float float8 = matrix3f0.m01 * matrix3f0.m12 - matrix3f0.m02 * matrix3f0.m11;
            float float9 = -matrix3f0.m00 * matrix3f0.m12 + matrix3f0.m02 * matrix3f0.m10;
            float float10 = matrix3f0.m00 * matrix3f0.m11 - matrix3f0.m01 * matrix3f0.m10;
            matrix3f1.m00 = float2 * float1;
            matrix3f1.m11 = float6 * float1;
            matrix3f1.m22 = float10 * float1;
            matrix3f1.m01 = float5 * float1;
            matrix3f1.m10 = float3 * float1;
            matrix3f1.m20 = float4 * float1;
            matrix3f1.m02 = float8 * float1;
            matrix3f1.m12 = float9 * float1;
            matrix3f1.m21 = float7 * float1;
            return matrix3f1;
        } else {
            return null;
        }
    }

    @Override
    public Matrix negate() {
        return this.negate(this);
    }

    public Matrix3f negate(Matrix3f matrix3f1) {
        return negate(this, matrix3f1);
    }

    public static Matrix3f negate(Matrix3f matrix3f1, Matrix3f matrix3f0) {
        if (matrix3f0 == null) {
            matrix3f0 = new Matrix3f();
        }

        matrix3f0.m00 = -matrix3f1.m00;
        matrix3f0.m01 = -matrix3f1.m02;
        matrix3f0.m02 = -matrix3f1.m01;
        matrix3f0.m10 = -matrix3f1.m10;
        matrix3f0.m11 = -matrix3f1.m12;
        matrix3f0.m12 = -matrix3f1.m11;
        matrix3f0.m20 = -matrix3f1.m20;
        matrix3f0.m21 = -matrix3f1.m22;
        matrix3f0.m22 = -matrix3f1.m21;
        return matrix3f0;
    }

    @Override
    public Matrix setIdentity() {
        return setIdentity(this);
    }

    public static Matrix3f setIdentity(Matrix3f matrix3f) {
        matrix3f.m00 = 1.0F;
        matrix3f.m01 = 0.0F;
        matrix3f.m02 = 0.0F;
        matrix3f.m10 = 0.0F;
        matrix3f.m11 = 1.0F;
        matrix3f.m12 = 0.0F;
        matrix3f.m20 = 0.0F;
        matrix3f.m21 = 0.0F;
        matrix3f.m22 = 1.0F;
        return matrix3f;
    }

    @Override
    public Matrix setZero() {
        return setZero(this);
    }

    public static Matrix3f setZero(Matrix3f matrix3f) {
        matrix3f.m00 = 0.0F;
        matrix3f.m01 = 0.0F;
        matrix3f.m02 = 0.0F;
        matrix3f.m10 = 0.0F;
        matrix3f.m11 = 0.0F;
        matrix3f.m12 = 0.0F;
        matrix3f.m20 = 0.0F;
        matrix3f.m21 = 0.0F;
        matrix3f.m22 = 0.0F;
        return matrix3f;
    }
}
