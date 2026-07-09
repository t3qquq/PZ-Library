// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public interface Matrix4x3fc {
    int PLANE_NX = 0;
    int PLANE_PX = 1;
    int PLANE_NY = 2;
    int PLANE_PY = 3;
    int PLANE_NZ = 4;
    int PLANE_PZ = 5;
    byte PROPERTY_IDENTITY = 4;
    byte PROPERTY_TRANSLATION = 8;
    byte PROPERTY_ORTHONORMAL = 16;

    int properties();

    float m00();

    float m01();

    float m02();

    float m10();

    float m11();

    float m12();

    float m20();

    float m21();

    float m22();

    float m30();

    float m31();

    float m32();

    Matrix4f get(Matrix4f arg0);

    Matrix4d get(Matrix4d arg0);

    Matrix4x3f mul(Matrix4x3fc arg0, Matrix4x3f arg1);

    Matrix4x3f mulTranslation(Matrix4x3fc arg0, Matrix4x3f arg1);

    Matrix4x3f mulOrtho(Matrix4x3fc arg0, Matrix4x3f arg1);

    Matrix4x3f fma(Matrix4x3fc arg0, float arg1, Matrix4x3f arg2);

    Matrix4x3f add(Matrix4x3fc arg0, Matrix4x3f arg1);

    Matrix4x3f sub(Matrix4x3fc arg0, Matrix4x3f arg1);

    Matrix4x3f mulComponentWise(Matrix4x3fc arg0, Matrix4x3f arg1);

    float determinant();

    Matrix4x3f invert(Matrix4x3f arg0);

    Matrix4f invert(Matrix4f arg0);

    Matrix4x3f invertOrtho(Matrix4x3f arg0);

    Matrix4x3f transpose3x3(Matrix4x3f arg0);

    Matrix3f transpose3x3(Matrix3f arg0);

    Vector3f getTranslation(Vector3f arg0);

    Vector3f getScale(Vector3f arg0);

    Matrix4x3f get(Matrix4x3f arg0);

    Matrix4x3d get(Matrix4x3d arg0);

    AxisAngle4f getRotation(AxisAngle4f arg0);

    AxisAngle4d getRotation(AxisAngle4d arg0);

    Quaternionf getUnnormalizedRotation(Quaternionf arg0);

    Quaternionf getNormalizedRotation(Quaternionf arg0);

    Quaterniond getUnnormalizedRotation(Quaterniond arg0);

    Quaterniond getNormalizedRotation(Quaterniond arg0);

    FloatBuffer get(FloatBuffer arg0);

    FloatBuffer get(int arg0, FloatBuffer arg1);

    ByteBuffer get(ByteBuffer arg0);

    ByteBuffer get(int arg0, ByteBuffer arg1);

    Matrix4x3fc getToAddress(long arg0);

    float[] get(float[] var1, int var2);

    float[] get(float[] var1);

    float[] get4x4(float[] var1, int var2);

    float[] get4x4(float[] var1);

    FloatBuffer get4x4(FloatBuffer arg0);

    FloatBuffer get4x4(int arg0, FloatBuffer arg1);

    ByteBuffer get4x4(ByteBuffer arg0);

    ByteBuffer get4x4(int arg0, ByteBuffer arg1);

    FloatBuffer get3x4(FloatBuffer arg0);

    FloatBuffer get3x4(int arg0, FloatBuffer arg1);

    ByteBuffer get3x4(ByteBuffer arg0);

    ByteBuffer get3x4(int arg0, ByteBuffer arg1);

    FloatBuffer getTransposed(FloatBuffer arg0);

    FloatBuffer getTransposed(int arg0, FloatBuffer arg1);

    ByteBuffer getTransposed(ByteBuffer arg0);

    ByteBuffer getTransposed(int arg0, ByteBuffer arg1);

    float[] getTransposed(float[] var1, int var2);

    float[] getTransposed(float[] var1);

    Vector4f transform(Vector4f arg0);

    Vector4f transform(Vector4fc arg0, Vector4f arg1);

    Vector3f transformPosition(Vector3f arg0);

    Vector3f transformPosition(Vector3fc arg0, Vector3f arg1);

    Vector3f transformDirection(Vector3f arg0);

    Vector3f transformDirection(Vector3fc arg0, Vector3f arg1);

    Matrix4x3f scale(Vector3fc arg0, Matrix4x3f arg1);

    Matrix4x3f scale(float arg0, Matrix4x3f arg1);

    Matrix4x3f scaleXY(float arg0, float arg1, Matrix4x3f arg2);

    Matrix4x3f scale(float arg0, float arg1, float arg2, Matrix4x3f arg3);

    Matrix4x3f scaleLocal(float arg0, float arg1, float arg2, Matrix4x3f arg3);

    Matrix4x3f rotateX(float arg0, Matrix4x3f arg1);

    Matrix4x3f rotateY(float arg0, Matrix4x3f arg1);

    Matrix4x3f rotateZ(float arg0, Matrix4x3f arg1);

    Matrix4x3f rotateXYZ(float arg0, float arg1, float arg2, Matrix4x3f arg3);

    Matrix4x3f rotateZYX(float arg0, float arg1, float arg2, Matrix4x3f arg3);

    Matrix4x3f rotateYXZ(float arg0, float arg1, float arg2, Matrix4x3f arg3);

    Matrix4x3f rotate(float arg0, float arg1, float arg2, float arg3, Matrix4x3f arg4);

    Matrix4x3f rotateTranslation(float arg0, float arg1, float arg2, float arg3, Matrix4x3f arg4);

    Matrix4x3f rotateAround(Quaternionfc arg0, float arg1, float arg2, float arg3, Matrix4x3f arg4);

    Matrix4x3f rotateLocal(float arg0, float arg1, float arg2, float arg3, Matrix4x3f arg4);

    Matrix4x3f translate(Vector3fc arg0, Matrix4x3f arg1);

    Matrix4x3f translate(float arg0, float arg1, float arg2, Matrix4x3f arg3);

    Matrix4x3f translateLocal(Vector3fc arg0, Matrix4x3f arg1);

    Matrix4x3f translateLocal(float arg0, float arg1, float arg2, Matrix4x3f arg3);

    Matrix4x3f ortho(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6, Matrix4x3f arg7);

    Matrix4x3f ortho(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4x3f arg6);

    Matrix4x3f orthoLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6, Matrix4x3f arg7);

    Matrix4x3f orthoLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4x3f arg6);

    Matrix4x3f orthoSymmetric(float arg0, float arg1, float arg2, float arg3, boolean arg4, Matrix4x3f arg5);

    Matrix4x3f orthoSymmetric(float arg0, float arg1, float arg2, float arg3, Matrix4x3f arg4);

    Matrix4x3f orthoSymmetricLH(float arg0, float arg1, float arg2, float arg3, boolean arg4, Matrix4x3f arg5);

    Matrix4x3f orthoSymmetricLH(float arg0, float arg1, float arg2, float arg3, Matrix4x3f arg4);

    Matrix4x3f ortho2D(float arg0, float arg1, float arg2, float arg3, Matrix4x3f arg4);

    Matrix4x3f ortho2DLH(float arg0, float arg1, float arg2, float arg3, Matrix4x3f arg4);

    Matrix4x3f lookAlong(Vector3fc arg0, Vector3fc arg1, Matrix4x3f arg2);

    Matrix4x3f lookAlong(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4x3f arg6);

    Matrix4x3f lookAt(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2, Matrix4x3f arg3);

    Matrix4x3f lookAt(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8, Matrix4x3f arg9);

    Matrix4x3f lookAtLH(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2, Matrix4x3f arg3);

    Matrix4x3f lookAtLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8, Matrix4x3f arg9);

    Matrix4x3f rotate(Quaternionfc arg0, Matrix4x3f arg1);

    Matrix4x3f rotateTranslation(Quaternionfc arg0, Matrix4x3f arg1);

    Matrix4x3f rotateLocal(Quaternionfc arg0, Matrix4x3f arg1);

    Matrix4x3f rotate(AxisAngle4f arg0, Matrix4x3f arg1);

    Matrix4x3f rotate(float arg0, Vector3fc arg1, Matrix4x3f arg2);

    Matrix4x3f reflect(float arg0, float arg1, float arg2, float arg3, Matrix4x3f arg4);

    Matrix4x3f reflect(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4x3f arg6);

    Matrix4x3f reflect(Quaternionfc arg0, Vector3fc arg1, Matrix4x3f arg2);

    Matrix4x3f reflect(Vector3fc arg0, Vector3fc arg1, Matrix4x3f arg2);

    Vector4f getRow(int arg0, Vector4f arg1) throws IndexOutOfBoundsException;

    Vector3f getColumn(int arg0, Vector3f arg1) throws IndexOutOfBoundsException;

    Matrix4x3f normal(Matrix4x3f arg0);

    Matrix3f normal(Matrix3f arg0);

    Matrix3f cofactor3x3(Matrix3f arg0);

    Matrix4x3f cofactor3x3(Matrix4x3f arg0);

    Matrix4x3f normalize3x3(Matrix4x3f arg0);

    Matrix3f normalize3x3(Matrix3f arg0);

    Vector4f frustumPlane(int arg0, Vector4f arg1);

    Vector3f positiveZ(Vector3f arg0);

    Vector3f normalizedPositiveZ(Vector3f arg0);

    Vector3f positiveX(Vector3f arg0);

    Vector3f normalizedPositiveX(Vector3f arg0);

    Vector3f positiveY(Vector3f arg0);

    Vector3f normalizedPositiveY(Vector3f arg0);

    Vector3f origin(Vector3f arg0);

    Matrix4x3f shadow(Vector4fc arg0, float arg1, float arg2, float arg3, float arg4, Matrix4x3f arg5);

    Matrix4x3f shadow(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, Matrix4x3f arg8);

    Matrix4x3f shadow(Vector4fc arg0, Matrix4x3fc arg1, Matrix4x3f arg2);

    Matrix4x3f shadow(float arg0, float arg1, float arg2, float arg3, Matrix4x3fc arg4, Matrix4x3f arg5);

    Matrix4x3f pick(float var1, float var2, float var3, float var4, int[] var5, Matrix4x3f var6);

    Matrix4x3f arcball(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4x3f arg6);

    Matrix4x3f arcball(float arg0, Vector3fc arg1, float arg2, float arg3, Matrix4x3f arg4);

    Matrix4x3f transformAab(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Vector3f arg6, Vector3f arg7);

    Matrix4x3f transformAab(Vector3fc arg0, Vector3fc arg1, Vector3f arg2, Vector3f arg3);

    Matrix4x3f lerp(Matrix4x3fc arg0, float arg1, Matrix4x3f arg2);

    Matrix4x3f rotateTowards(Vector3fc arg0, Vector3fc arg1, Matrix4x3f arg2);

    Matrix4x3f rotateTowards(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4x3f arg6);

    Vector3f getEulerAnglesZYX(Vector3f arg0);

    Matrix4x3f obliqueZ(float arg0, float arg1, Matrix4x3f arg2);

    Matrix4x3f withLookAtUp(Vector3fc arg0, Matrix4x3f arg1);

    Matrix4x3f withLookAtUp(float arg0, float arg1, float arg2, Matrix4x3f arg3);

    boolean equals(Matrix4x3fc arg0, float arg1);

    boolean isFinite();
}
