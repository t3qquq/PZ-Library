// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.vector;

import java.io.Serializable;
import java.nio.FloatBuffer;

public class Matrix2f extends Matrix implements Serializable {
    private static final long serialVersionUID = 1L;
    public float m00;
    public float m01;
    public float m10;
    public float m11;

    public Matrix2f() {
        this.setIdentity();
    }

    public Matrix2f(Matrix2f matrix2f1) {
        this.load(matrix2f1);
    }

    public Matrix2f load(Matrix2f matrix2f0) {
        return load(matrix2f0, this);
    }

    public static Matrix2f load(Matrix2f matrix2f1, Matrix2f matrix2f0) {
        if (matrix2f0 == null) {
            matrix2f0 = new Matrix2f();
        }

        matrix2f0.m00 = matrix2f1.m00;
        matrix2f0.m01 = matrix2f1.m01;
        matrix2f0.m10 = matrix2f1.m10;
        matrix2f0.m11 = matrix2f1.m11;
        return matrix2f0;
    }

    @Override
    public Matrix load(FloatBuffer floatBuffer) {
        this.m00 = floatBuffer.get();
        this.m01 = floatBuffer.get();
        this.m10 = floatBuffer.get();
        this.m11 = floatBuffer.get();
        return this;
    }

    @Override
    public Matrix loadTranspose(FloatBuffer floatBuffer) {
        this.m00 = floatBuffer.get();
        this.m10 = floatBuffer.get();
        this.m01 = floatBuffer.get();
        this.m11 = floatBuffer.get();
        return this;
    }

    @Override
    public Matrix store(FloatBuffer floatBuffer) {
        floatBuffer.put(this.m00);
        floatBuffer.put(this.m01);
        floatBuffer.put(this.m10);
        floatBuffer.put(this.m11);
        return this;
    }

    @Override
    public Matrix storeTranspose(FloatBuffer floatBuffer) {
        floatBuffer.put(this.m00);
        floatBuffer.put(this.m10);
        floatBuffer.put(this.m01);
        floatBuffer.put(this.m11);
        return this;
    }

    public static Matrix2f add(Matrix2f matrix2f2, Matrix2f matrix2f1, Matrix2f matrix2f0) {
        if (matrix2f0 == null) {
            matrix2f0 = new Matrix2f();
        }

        matrix2f0.m00 = matrix2f2.m00 + matrix2f1.m00;
        matrix2f0.m01 = matrix2f2.m01 + matrix2f1.m01;
        matrix2f0.m10 = matrix2f2.m10 + matrix2f1.m10;
        matrix2f0.m11 = matrix2f2.m11 + matrix2f1.m11;
        return matrix2f0;
    }

    public static Matrix2f sub(Matrix2f matrix2f2, Matrix2f matrix2f1, Matrix2f matrix2f0) {
        if (matrix2f0 == null) {
            matrix2f0 = new Matrix2f();
        }

        matrix2f0.m00 = matrix2f2.m00 - matrix2f1.m00;
        matrix2f0.m01 = matrix2f2.m01 - matrix2f1.m01;
        matrix2f0.m10 = matrix2f2.m10 - matrix2f1.m10;
        matrix2f0.m11 = matrix2f2.m11 - matrix2f1.m11;
        return matrix2f0;
    }

    public static Matrix2f mul(Matrix2f matrix2f2, Matrix2f matrix2f1, Matrix2f matrix2f0) {
        if (matrix2f0 == null) {
            matrix2f0 = new Matrix2f();
        }

        float float0 = matrix2f2.m00 * matrix2f1.m00 + matrix2f2.m10 * matrix2f1.m01;
        float float1 = matrix2f2.m01 * matrix2f1.m00 + matrix2f2.m11 * matrix2f1.m01;
        float float2 = matrix2f2.m00 * matrix2f1.m10 + matrix2f2.m10 * matrix2f1.m11;
        float float3 = matrix2f2.m01 * matrix2f1.m10 + matrix2f2.m11 * matrix2f1.m11;
        matrix2f0.m00 = float0;
        matrix2f0.m01 = float1;
        matrix2f0.m10 = float2;
        matrix2f0.m11 = float3;
        return matrix2f0;
    }

    public static Vector2f transform(Matrix2f matrix2f, Vector2f vector2f1, Vector2f vector2f0) {
        if (vector2f0 == null) {
            vector2f0 = new Vector2f();
        }

        float float0 = matrix2f.m00 * vector2f1.x + matrix2f.m10 * vector2f1.y;
        float float1 = matrix2f.m01 * vector2f1.x + matrix2f.m11 * vector2f1.y;
        vector2f0.x = float0;
        vector2f0.y = float1;
        return vector2f0;
    }

    @Override
    public Matrix transpose() {
        return this.transpose(this);
    }

    public Matrix2f transpose(Matrix2f matrix2f1) {
        return transpose(this, matrix2f1);
    }

    public static Matrix2f transpose(Matrix2f matrix2f1, Matrix2f matrix2f0) {
        if (matrix2f0 == null) {
            matrix2f0 = new Matrix2f();
        }

        float float0 = matrix2f1.m10;
        float float1 = matrix2f1.m01;
        matrix2f0.m01 = float0;
        matrix2f0.m10 = float1;
        return matrix2f0;
    }

    @Override
    public Matrix invert() {
        return invert(this, this);
    }

    public static Matrix2f invert(Matrix2f matrix2f0, Matrix2f matrix2f1) {
        float float0 = matrix2f0.determinant();
        if (float0 != 0.0F) {
            if (matrix2f1 == null) {
                matrix2f1 = new Matrix2f();
            }

            float float1 = 1.0F / float0;
            float float2 = matrix2f0.m11 * float1;
            float float3 = -matrix2f0.m01 * float1;
            float float4 = matrix2f0.m00 * float1;
            float float5 = -matrix2f0.m10 * float1;
            matrix2f1.m00 = float2;
            matrix2f1.m01 = float3;
            matrix2f1.m10 = float5;
            matrix2f1.m11 = float4;
            return matrix2f1;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.m00).append(' ').append(this.m10).append(' ').append('\n');
        stringBuilder.append(this.m01).append(' ').append(this.m11).append(' ').append('\n');
        return stringBuilder.toString();
    }

    @Override
    public Matrix negate() {
        return this.negate(this);
    }

    public Matrix2f negate(Matrix2f matrix2f1) {
        return negate(this, matrix2f1);
    }

    public static Matrix2f negate(Matrix2f matrix2f1, Matrix2f matrix2f0) {
        if (matrix2f0 == null) {
            matrix2f0 = new Matrix2f();
        }

        matrix2f0.m00 = -matrix2f1.m00;
        matrix2f0.m01 = -matrix2f1.m01;
        matrix2f0.m10 = -matrix2f1.m10;
        matrix2f0.m11 = -matrix2f1.m11;
        return matrix2f0;
    }

    @Override
    public Matrix setIdentity() {
        return setIdentity(this);
    }

    public static Matrix2f setIdentity(Matrix2f matrix2f) {
        matrix2f.m00 = 1.0F;
        matrix2f.m01 = 0.0F;
        matrix2f.m10 = 0.0F;
        matrix2f.m11 = 1.0F;
        return matrix2f;
    }

    @Override
    public Matrix setZero() {
        return setZero(this);
    }

    public static Matrix2f setZero(Matrix2f matrix2f) {
        matrix2f.m00 = 0.0F;
        matrix2f.m01 = 0.0F;
        matrix2f.m10 = 0.0F;
        matrix2f.m11 = 0.0F;
        return matrix2f;
    }

    @Override
    public float determinant() {
        return this.m00 * this.m11 - this.m01 * this.m10;
    }
}
