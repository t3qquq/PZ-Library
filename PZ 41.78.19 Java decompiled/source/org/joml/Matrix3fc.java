// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public interface Matrix3fc {
    float m00();

    float m01();

    float m02();

    float m10();

    float m11();

    float m12();

    float m20();

    float m21();

    float m22();

    Matrix3f mul(Matrix3fc arg0, Matrix3f arg1);

    Matrix3f mulLocal(Matrix3fc arg0, Matrix3f arg1);

    float determinant();

    Matrix3f invert(Matrix3f arg0);

    Matrix3f transpose(Matrix3f arg0);

    Matrix3f get(Matrix3f arg0);

    Matrix4f get(Matrix4f arg0);

    AxisAngle4f getRotation(AxisAngle4f arg0);

    Quaternionf getUnnormalizedRotation(Quaternionf arg0);

    Quaternionf getNormalizedRotation(Quaternionf arg0);

    Quaterniond getUnnormalizedRotation(Quaterniond arg0);

    Quaterniond getNormalizedRotation(Quaterniond arg0);

    FloatBuffer get(FloatBuffer arg0);

    FloatBuffer get(int arg0, FloatBuffer arg1);

    ByteBuffer get(ByteBuffer arg0);

    ByteBuffer get(int arg0, ByteBuffer arg1);

    FloatBuffer get3x4(FloatBuffer arg0);

    FloatBuffer get3x4(int arg0, FloatBuffer arg1);

    ByteBuffer get3x4(ByteBuffer arg0);

    ByteBuffer get3x4(int arg0, ByteBuffer arg1);

    FloatBuffer getTransposed(FloatBuffer arg0);

    FloatBuffer getTransposed(int arg0, FloatBuffer arg1);

    ByteBuffer getTransposed(ByteBuffer arg0);

    ByteBuffer getTransposed(int arg0, ByteBuffer arg1);

    Matrix3fc getToAddress(long arg0);

    float[] get(float[] var1, int var2);

    float[] get(float[] var1);

    Matrix3f scale(Vector3fc arg0, Matrix3f arg1);

    Matrix3f scale(float arg0, float arg1, float arg2, Matrix3f arg3);

    Matrix3f scale(float arg0, Matrix3f arg1);

    Matrix3f scaleLocal(float arg0, float arg1, float arg2, Matrix3f arg3);

    Vector3f transform(Vector3f arg0);

    Vector3f transform(Vector3fc arg0, Vector3f arg1);

    Vector3f transform(float arg0, float arg1, float arg2, Vector3f arg3);

    Vector3f transformTranspose(Vector3f arg0);

    Vector3f transformTranspose(Vector3fc arg0, Vector3f arg1);

    Vector3f transformTranspose(float arg0, float arg1, float arg2, Vector3f arg3);

    Matrix3f rotateX(float arg0, Matrix3f arg1);

    Matrix3f rotateY(float arg0, Matrix3f arg1);

    Matrix3f rotateZ(float arg0, Matrix3f arg1);

    Matrix3f rotateXYZ(float arg0, float arg1, float arg2, Matrix3f arg3);

    Matrix3f rotateZYX(float arg0, float arg1, float arg2, Matrix3f arg3);

    Matrix3f rotateYXZ(float arg0, float arg1, float arg2, Matrix3f arg3);

    Matrix3f rotate(float arg0, float arg1, float arg2, float arg3, Matrix3f arg4);

    Matrix3f rotateLocal(float arg0, float arg1, float arg2, float arg3, Matrix3f arg4);

    Matrix3f rotateLocalX(float arg0, Matrix3f arg1);

    Matrix3f rotateLocalY(float arg0, Matrix3f arg1);

    Matrix3f rotateLocalZ(float arg0, Matrix3f arg1);

    Matrix3f rotate(Quaternionfc arg0, Matrix3f arg1);

    Matrix3f rotateLocal(Quaternionfc arg0, Matrix3f arg1);

    Matrix3f rotate(AxisAngle4f arg0, Matrix3f arg1);

    Matrix3f rotate(float arg0, Vector3fc arg1, Matrix3f arg2);

    Matrix3f lookAlong(Vector3fc arg0, Vector3fc arg1, Matrix3f arg2);

    Matrix3f lookAlong(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix3f arg6);

    Vector3f getRow(int arg0, Vector3f arg1) throws IndexOutOfBoundsException;

    Vector3f getColumn(int arg0, Vector3f arg1) throws IndexOutOfBoundsException;

    float get(int arg0, int arg1);

    float getRowColumn(int arg0, int arg1);

    Matrix3f normal(Matrix3f arg0);

    Matrix3f cofactor(Matrix3f arg0);

    Vector3f getScale(Vector3f arg0);

    Vector3f positiveZ(Vector3f arg0);

    Vector3f normalizedPositiveZ(Vector3f arg0);

    Vector3f positiveX(Vector3f arg0);

    Vector3f normalizedPositiveX(Vector3f arg0);

    Vector3f positiveY(Vector3f arg0);

    Vector3f normalizedPositiveY(Vector3f arg0);

    Matrix3f add(Matrix3fc arg0, Matrix3f arg1);

    Matrix3f sub(Matrix3fc arg0, Matrix3f arg1);

    Matrix3f mulComponentWise(Matrix3fc arg0, Matrix3f arg1);

    Matrix3f lerp(Matrix3fc arg0, float arg1, Matrix3f arg2);

    Matrix3f rotateTowards(Vector3fc arg0, Vector3fc arg1, Matrix3f arg2);

    Matrix3f rotateTowards(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix3f arg6);

    Vector3f getEulerAnglesZYX(Vector3f arg0);

    Matrix3f obliqueZ(float arg0, float arg1, Matrix3f arg2);

    boolean equals(Matrix3fc arg0, float arg1);

    Matrix3f reflect(float arg0, float arg1, float arg2, Matrix3f arg3);

    Matrix3f reflect(Quaternionfc arg0, Matrix3f arg1);

    Matrix3f reflect(Vector3fc arg0, Matrix3f arg1);

    boolean isFinite();

    float quadraticFormProduct(float arg0, float arg1, float arg2);

    float quadraticFormProduct(Vector3fc arg0);
}
