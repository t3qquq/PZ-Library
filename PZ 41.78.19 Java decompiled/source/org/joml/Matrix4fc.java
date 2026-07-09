// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public interface Matrix4fc {
    int PLANE_NX = 0;
    int PLANE_PX = 1;
    int PLANE_NY = 2;
    int PLANE_PY = 3;
    int PLANE_NZ = 4;
    int PLANE_PZ = 5;
    int CORNER_NXNYNZ = 0;
    int CORNER_PXNYNZ = 1;
    int CORNER_PXPYNZ = 2;
    int CORNER_NXPYNZ = 3;
    int CORNER_PXNYPZ = 4;
    int CORNER_NXNYPZ = 5;
    int CORNER_NXPYPZ = 6;
    int CORNER_PXPYPZ = 7;
    byte PROPERTY_PERSPECTIVE = 1;
    byte PROPERTY_AFFINE = 2;
    byte PROPERTY_IDENTITY = 4;
    byte PROPERTY_TRANSLATION = 8;
    byte PROPERTY_ORTHONORMAL = 16;

    int properties();

    float m00();

    float m01();

    float m02();

    float m03();

    float m10();

    float m11();

    float m12();

    float m13();

    float m20();

    float m21();

    float m22();

    float m23();

    float m30();

    float m31();

    float m32();

    float m33();

    Matrix4f mul(Matrix4fc arg0, Matrix4f arg1);

    Matrix4f mul0(Matrix4fc arg0, Matrix4f arg1);

    Matrix4f mul(
        float arg0,
        float arg1,
        float arg2,
        float arg3,
        float arg4,
        float arg5,
        float arg6,
        float arg7,
        float arg8,
        float arg9,
        float arg10,
        float arg11,
        float arg12,
        float arg13,
        float arg14,
        float arg15,
        Matrix4f arg16
    );

    Matrix4f mul3x3(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8, Matrix4f arg9);

    Matrix4f mulLocal(Matrix4fc arg0, Matrix4f arg1);

    Matrix4f mulLocalAffine(Matrix4fc arg0, Matrix4f arg1);

    Matrix4f mul(Matrix3x2fc arg0, Matrix4f arg1);

    Matrix4f mul(Matrix4x3fc arg0, Matrix4f arg1);

    Matrix4f mulPerspectiveAffine(Matrix4fc arg0, Matrix4f arg1);

    Matrix4f mulPerspectiveAffine(Matrix4x3fc arg0, Matrix4f arg1);

    Matrix4f mulAffineR(Matrix4fc arg0, Matrix4f arg1);

    Matrix4f mulAffine(Matrix4fc arg0, Matrix4f arg1);

    Matrix4f mulTranslationAffine(Matrix4fc arg0, Matrix4f arg1);

    Matrix4f mulOrthoAffine(Matrix4fc arg0, Matrix4f arg1);

    Matrix4f fma4x3(Matrix4fc arg0, float arg1, Matrix4f arg2);

    Matrix4f add(Matrix4fc arg0, Matrix4f arg1);

    Matrix4f sub(Matrix4fc arg0, Matrix4f arg1);

    Matrix4f mulComponentWise(Matrix4fc arg0, Matrix4f arg1);

    Matrix4f add4x3(Matrix4fc arg0, Matrix4f arg1);

    Matrix4f sub4x3(Matrix4fc arg0, Matrix4f arg1);

    Matrix4f mul4x3ComponentWise(Matrix4fc arg0, Matrix4f arg1);

    float determinant();

    float determinant3x3();

    float determinantAffine();

    Matrix4f invert(Matrix4f arg0);

    Matrix4f invertPerspective(Matrix4f arg0);

    Matrix4f invertFrustum(Matrix4f arg0);

    Matrix4f invertOrtho(Matrix4f arg0);

    Matrix4f invertPerspectiveView(Matrix4fc arg0, Matrix4f arg1);

    Matrix4f invertPerspectiveView(Matrix4x3fc arg0, Matrix4f arg1);

    Matrix4f invertAffine(Matrix4f arg0);

    Matrix4f transpose(Matrix4f arg0);

    Matrix4f transpose3x3(Matrix4f arg0);

    Matrix3f transpose3x3(Matrix3f arg0);

    Vector3f getTranslation(Vector3f arg0);

    Vector3f getScale(Vector3f arg0);

    Matrix4f get(Matrix4f arg0);

    Matrix4x3f get4x3(Matrix4x3f arg0);

    Matrix4d get(Matrix4d arg0);

    Matrix3f get3x3(Matrix3f arg0);

    Matrix3d get3x3(Matrix3d arg0);

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

    FloatBuffer get4x3(FloatBuffer arg0);

    FloatBuffer get4x3(int arg0, FloatBuffer arg1);

    ByteBuffer get4x3(ByteBuffer arg0);

    ByteBuffer get4x3(int arg0, ByteBuffer arg1);

    FloatBuffer get3x4(FloatBuffer arg0);

    FloatBuffer get3x4(int arg0, FloatBuffer arg1);

    ByteBuffer get3x4(ByteBuffer arg0);

    ByteBuffer get3x4(int arg0, ByteBuffer arg1);

    FloatBuffer getTransposed(FloatBuffer arg0);

    FloatBuffer getTransposed(int arg0, FloatBuffer arg1);

    ByteBuffer getTransposed(ByteBuffer arg0);

    ByteBuffer getTransposed(int arg0, ByteBuffer arg1);

    FloatBuffer get4x3Transposed(FloatBuffer arg0);

    FloatBuffer get4x3Transposed(int arg0, FloatBuffer arg1);

    ByteBuffer get4x3Transposed(ByteBuffer arg0);

    ByteBuffer get4x3Transposed(int arg0, ByteBuffer arg1);

    Matrix4fc getToAddress(long arg0);

    float[] get(float[] var1, int var2);

    float[] get(float[] var1);

    Vector4f transform(Vector4f arg0);

    Vector4f transform(Vector4fc arg0, Vector4f arg1);

    Vector4f transform(float arg0, float arg1, float arg2, float arg3, Vector4f arg4);

    Vector4f transformTranspose(Vector4f arg0);

    Vector4f transformTranspose(Vector4fc arg0, Vector4f arg1);

    Vector4f transformTranspose(float arg0, float arg1, float arg2, float arg3, Vector4f arg4);

    Vector4f transformProject(Vector4f arg0);

    Vector4f transformProject(Vector4fc arg0, Vector4f arg1);

    Vector4f transformProject(float arg0, float arg1, float arg2, float arg3, Vector4f arg4);

    Vector3f transformProject(Vector3f arg0);

    Vector3f transformProject(Vector3fc arg0, Vector3f arg1);

    Vector3f transformProject(Vector4fc arg0, Vector3f arg1);

    Vector3f transformProject(float arg0, float arg1, float arg2, Vector3f arg3);

    Vector3f transformProject(float arg0, float arg1, float arg2, float arg3, Vector3f arg4);

    Vector3f transformPosition(Vector3f arg0);

    Vector3f transformPosition(Vector3fc arg0, Vector3f arg1);

    Vector3f transformPosition(float arg0, float arg1, float arg2, Vector3f arg3);

    Vector3f transformDirection(Vector3f arg0);

    Vector3f transformDirection(Vector3fc arg0, Vector3f arg1);

    Vector3f transformDirection(float arg0, float arg1, float arg2, Vector3f arg3);

    Vector4f transformAffine(Vector4f arg0);

    Vector4f transformAffine(Vector4fc arg0, Vector4f arg1);

    Vector4f transformAffine(float arg0, float arg1, float arg2, float arg3, Vector4f arg4);

    Matrix4f scale(Vector3fc arg0, Matrix4f arg1);

    Matrix4f scale(float arg0, Matrix4f arg1);

    Matrix4f scaleXY(float arg0, float arg1, Matrix4f arg2);

    Matrix4f scale(float arg0, float arg1, float arg2, Matrix4f arg3);

    Matrix4f scaleAround(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6);

    Matrix4f scaleAround(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4);

    Matrix4f scaleLocal(float arg0, Matrix4f arg1);

    Matrix4f scaleLocal(float arg0, float arg1, float arg2, Matrix4f arg3);

    Matrix4f scaleAroundLocal(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6);

    Matrix4f scaleAroundLocal(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4);

    Matrix4f rotateX(float arg0, Matrix4f arg1);

    Matrix4f rotateY(float arg0, Matrix4f arg1);

    Matrix4f rotateZ(float arg0, Matrix4f arg1);

    Matrix4f rotateTowardsXY(float arg0, float arg1, Matrix4f arg2);

    Matrix4f rotateXYZ(float arg0, float arg1, float arg2, Matrix4f arg3);

    Matrix4f rotateAffineXYZ(float arg0, float arg1, float arg2, Matrix4f arg3);

    Matrix4f rotateZYX(float arg0, float arg1, float arg2, Matrix4f arg3);

    Matrix4f rotateAffineZYX(float arg0, float arg1, float arg2, Matrix4f arg3);

    Matrix4f rotateYXZ(float arg0, float arg1, float arg2, Matrix4f arg3);

    Matrix4f rotateAffineYXZ(float arg0, float arg1, float arg2, Matrix4f arg3);

    Matrix4f rotate(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4);

    Matrix4f rotateTranslation(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4);

    Matrix4f rotateAffine(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4);

    Matrix4f rotateLocal(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4);

    Matrix4f rotateLocalX(float arg0, Matrix4f arg1);

    Matrix4f rotateLocalY(float arg0, Matrix4f arg1);

    Matrix4f rotateLocalZ(float arg0, Matrix4f arg1);

    Matrix4f translate(Vector3fc arg0, Matrix4f arg1);

    Matrix4f translate(float arg0, float arg1, float arg2, Matrix4f arg3);

    Matrix4f translateLocal(Vector3fc arg0, Matrix4f arg1);

    Matrix4f translateLocal(float arg0, float arg1, float arg2, Matrix4f arg3);

    Matrix4f ortho(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6, Matrix4f arg7);

    Matrix4f ortho(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6);

    Matrix4f orthoLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6, Matrix4f arg7);

    Matrix4f orthoLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6);

    Matrix4f orthoSymmetric(float arg0, float arg1, float arg2, float arg3, boolean arg4, Matrix4f arg5);

    Matrix4f orthoSymmetric(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4);

    Matrix4f orthoSymmetricLH(float arg0, float arg1, float arg2, float arg3, boolean arg4, Matrix4f arg5);

    Matrix4f orthoSymmetricLH(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4);

    Matrix4f ortho2D(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4);

    Matrix4f ortho2DLH(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4);

    Matrix4f lookAlong(Vector3fc arg0, Vector3fc arg1, Matrix4f arg2);

    Matrix4f lookAlong(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6);

    Matrix4f lookAt(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2, Matrix4f arg3);

    Matrix4f lookAt(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8, Matrix4f arg9);

    Matrix4f lookAtPerspective(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8, Matrix4f arg9);

    Matrix4f lookAtLH(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2, Matrix4f arg3);

    Matrix4f lookAtLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8, Matrix4f arg9);

    Matrix4f lookAtPerspectiveLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8, Matrix4f arg9);

    Matrix4f perspective(float arg0, float arg1, float arg2, float arg3, boolean arg4, Matrix4f arg5);

    Matrix4f perspective(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4);

    Matrix4f perspectiveRect(float arg0, float arg1, float arg2, float arg3, boolean arg4, Matrix4f arg5);

    Matrix4f perspectiveRect(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4);

    Matrix4f perspectiveRect(float arg0, float arg1, float arg2, float arg3, boolean arg4);

    Matrix4f perspectiveRect(float arg0, float arg1, float arg2, float arg3);

    Matrix4f perspectiveOffCenter(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6, Matrix4f arg7);

    Matrix4f perspectiveOffCenter(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6);

    Matrix4f perspectiveOffCenter(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6);

    Matrix4f perspectiveOffCenter(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5);

    Matrix4f perspectiveLH(float arg0, float arg1, float arg2, float arg3, boolean arg4, Matrix4f arg5);

    Matrix4f perspectiveLH(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4);

    Matrix4f frustum(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6, Matrix4f arg7);

    Matrix4f frustum(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6);

    Matrix4f frustumLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, boolean arg6, Matrix4f arg7);

    Matrix4f frustumLH(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6);

    Matrix4f rotate(Quaternionfc arg0, Matrix4f arg1);

    Matrix4f rotateAffine(Quaternionfc arg0, Matrix4f arg1);

    Matrix4f rotateTranslation(Quaternionfc arg0, Matrix4f arg1);

    Matrix4f rotateAroundAffine(Quaternionfc arg0, float arg1, float arg2, float arg3, Matrix4f arg4);

    Matrix4f rotateAround(Quaternionfc arg0, float arg1, float arg2, float arg3, Matrix4f arg4);

    Matrix4f rotateLocal(Quaternionfc arg0, Matrix4f arg1);

    Matrix4f rotateAroundLocal(Quaternionfc arg0, float arg1, float arg2, float arg3, Matrix4f arg4);

    Matrix4f rotate(AxisAngle4f arg0, Matrix4f arg1);

    Matrix4f rotate(float arg0, Vector3fc arg1, Matrix4f arg2);

    Vector4f unproject(float var1, float var2, float var3, int[] var4, Vector4f var5);

    Vector3f unproject(float var1, float var2, float var3, int[] var4, Vector3f var5);

    Vector4f unproject(Vector3fc var1, int[] var2, Vector4f var3);

    Vector3f unproject(Vector3fc var1, int[] var2, Vector3f var3);

    Matrix4f unprojectRay(float var1, float var2, int[] var3, Vector3f var4, Vector3f var5);

    Matrix4f unprojectRay(Vector2fc var1, int[] var2, Vector3f var3, Vector3f var4);

    Vector4f unprojectInv(Vector3fc var1, int[] var2, Vector4f var3);

    Vector4f unprojectInv(float var1, float var2, float var3, int[] var4, Vector4f var5);

    Matrix4f unprojectInvRay(Vector2fc var1, int[] var2, Vector3f var3, Vector3f var4);

    Matrix4f unprojectInvRay(float var1, float var2, int[] var3, Vector3f var4, Vector3f var5);

    Vector3f unprojectInv(Vector3fc var1, int[] var2, Vector3f var3);

    Vector3f unprojectInv(float var1, float var2, float var3, int[] var4, Vector3f var5);

    Vector4f project(float var1, float var2, float var3, int[] var4, Vector4f var5);

    Vector3f project(float var1, float var2, float var3, int[] var4, Vector3f var5);

    Vector4f project(Vector3fc var1, int[] var2, Vector4f var3);

    Vector3f project(Vector3fc var1, int[] var2, Vector3f var3);

    Matrix4f reflect(float arg0, float arg1, float arg2, float arg3, Matrix4f arg4);

    Matrix4f reflect(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6);

    Matrix4f reflect(Quaternionfc arg0, Vector3fc arg1, Matrix4f arg2);

    Matrix4f reflect(Vector3fc arg0, Vector3fc arg1, Matrix4f arg2);

    Vector4f getRow(int arg0, Vector4f arg1) throws IndexOutOfBoundsException;

    Vector3f getRow(int arg0, Vector3f arg1) throws IndexOutOfBoundsException;

    Vector4f getColumn(int arg0, Vector4f arg1) throws IndexOutOfBoundsException;

    Vector3f getColumn(int arg0, Vector3f arg1) throws IndexOutOfBoundsException;

    float get(int arg0, int arg1);

    float getRowColumn(int arg0, int arg1);

    Matrix4f normal(Matrix4f arg0);

    Matrix3f normal(Matrix3f arg0);

    Matrix3f cofactor3x3(Matrix3f arg0);

    Matrix4f cofactor3x3(Matrix4f arg0);

    Matrix4f normalize3x3(Matrix4f arg0);

    Matrix3f normalize3x3(Matrix3f arg0);

    Vector4f frustumPlane(int arg0, Vector4f arg1);

    Vector3f frustumCorner(int arg0, Vector3f arg1);

    Vector3f perspectiveOrigin(Vector3f arg0);

    Vector3f perspectiveInvOrigin(Vector3f arg0);

    float perspectiveFov();

    float perspectiveNear();

    float perspectiveFar();

    Vector3f frustumRayDir(float arg0, float arg1, Vector3f arg2);

    Vector3f positiveZ(Vector3f arg0);

    Vector3f normalizedPositiveZ(Vector3f arg0);

    Vector3f positiveX(Vector3f arg0);

    Vector3f normalizedPositiveX(Vector3f arg0);

    Vector3f positiveY(Vector3f arg0);

    Vector3f normalizedPositiveY(Vector3f arg0);

    Vector3f originAffine(Vector3f arg0);

    Vector3f origin(Vector3f arg0);

    Matrix4f shadow(Vector4f arg0, float arg1, float arg2, float arg3, float arg4, Matrix4f arg5);

    Matrix4f shadow(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, Matrix4f arg8);

    Matrix4f shadow(Vector4f arg0, Matrix4fc arg1, Matrix4f arg2);

    Matrix4f shadow(float arg0, float arg1, float arg2, float arg3, Matrix4fc arg4, Matrix4f arg5);

    Matrix4f pick(float var1, float var2, float var3, float var4, int[] var5, Matrix4f var6);

    boolean isAffine();

    Matrix4f arcball(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6);

    Matrix4f arcball(float arg0, Vector3fc arg1, float arg2, float arg3, Matrix4f arg4);

    Matrix4f frustumAabb(Vector3f arg0, Vector3f arg1);

    Matrix4f projectedGridRange(Matrix4fc arg0, float arg1, float arg2, Matrix4f arg3);

    Matrix4f perspectiveFrustumSlice(float arg0, float arg1, Matrix4f arg2);

    Matrix4f orthoCrop(Matrix4fc arg0, Matrix4f arg1);

    Matrix4f transformAab(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Vector3f arg6, Vector3f arg7);

    Matrix4f transformAab(Vector3fc arg0, Vector3fc arg1, Vector3f arg2, Vector3f arg3);

    Matrix4f lerp(Matrix4fc arg0, float arg1, Matrix4f arg2);

    Matrix4f rotateTowards(Vector3fc arg0, Vector3fc arg1, Matrix4f arg2);

    Matrix4f rotateTowards(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Matrix4f arg6);

    Vector3f getEulerAnglesZYX(Vector3f arg0);

    boolean testPoint(float arg0, float arg1, float arg2);

    boolean testSphere(float arg0, float arg1, float arg2, float arg3);

    boolean testAab(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5);

    Matrix4f obliqueZ(float arg0, float arg1, Matrix4f arg2);

    Matrix4f withLookAtUp(Vector3fc arg0, Matrix4f arg1);

    Matrix4f withLookAtUp(float arg0, float arg1, float arg2, Matrix4f arg3);

    boolean equals(Matrix4fc arg0, float arg1);

    boolean isFinite();
}
