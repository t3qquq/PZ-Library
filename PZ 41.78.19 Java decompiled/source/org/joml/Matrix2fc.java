// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public interface Matrix2fc {
    float m00();

    float m01();

    float m10();

    float m11();

    Matrix2f mul(Matrix2fc arg0, Matrix2f arg1);

    Matrix2f mulLocal(Matrix2fc arg0, Matrix2f arg1);

    float determinant();

    Matrix2f invert(Matrix2f arg0);

    Matrix2f transpose(Matrix2f arg0);

    Matrix2f get(Matrix2f arg0);

    Matrix3x2f get(Matrix3x2f arg0);

    Matrix3f get(Matrix3f arg0);

    float getRotation();

    FloatBuffer get(FloatBuffer arg0);

    FloatBuffer get(int arg0, FloatBuffer arg1);

    ByteBuffer get(ByteBuffer arg0);

    ByteBuffer get(int arg0, ByteBuffer arg1);

    FloatBuffer getTransposed(FloatBuffer arg0);

    FloatBuffer getTransposed(int arg0, FloatBuffer arg1);

    ByteBuffer getTransposed(ByteBuffer arg0);

    ByteBuffer getTransposed(int arg0, ByteBuffer arg1);

    Matrix2fc getToAddress(long arg0);

    float[] get(float[] var1, int var2);

    float[] get(float[] var1);

    Matrix2f scale(Vector2fc arg0, Matrix2f arg1);

    Matrix2f scale(float arg0, float arg1, Matrix2f arg2);

    Matrix2f scale(float arg0, Matrix2f arg1);

    Matrix2f scaleLocal(float arg0, float arg1, Matrix2f arg2);

    Vector2f transform(Vector2f arg0);

    Vector2f transform(Vector2fc arg0, Vector2f arg1);

    Vector2f transform(float arg0, float arg1, Vector2f arg2);

    Vector2f transformTranspose(Vector2f arg0);

    Vector2f transformTranspose(Vector2fc arg0, Vector2f arg1);

    Vector2f transformTranspose(float arg0, float arg1, Vector2f arg2);

    Matrix2f rotate(float arg0, Matrix2f arg1);

    Matrix2f rotateLocal(float arg0, Matrix2f arg1);

    Vector2f getRow(int arg0, Vector2f arg1) throws IndexOutOfBoundsException;

    Vector2f getColumn(int arg0, Vector2f arg1) throws IndexOutOfBoundsException;

    float get(int arg0, int arg1);

    Matrix2f normal(Matrix2f arg0);

    Vector2f getScale(Vector2f arg0);

    Vector2f positiveX(Vector2f arg0);

    Vector2f normalizedPositiveX(Vector2f arg0);

    Vector2f positiveY(Vector2f arg0);

    Vector2f normalizedPositiveY(Vector2f arg0);

    Matrix2f add(Matrix2fc arg0, Matrix2f arg1);

    Matrix2f sub(Matrix2fc arg0, Matrix2f arg1);

    Matrix2f mulComponentWise(Matrix2fc arg0, Matrix2f arg1);

    Matrix2f lerp(Matrix2fc arg0, float arg1, Matrix2f arg2);

    boolean equals(Matrix2fc arg0, float arg1);

    boolean isFinite();
}
