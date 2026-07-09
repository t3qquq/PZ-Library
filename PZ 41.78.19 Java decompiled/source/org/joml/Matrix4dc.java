// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

public interface Matrix4dc {
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

    double m00();

    double m01();

    double m02();

    double m03();

    double m10();

    double m11();

    double m12();

    double m13();

    double m20();

    double m21();

    double m22();

    double m23();

    double m30();

    double m31();

    double m32();

    double m33();

    Matrix4d mul(Matrix4dc arg0, Matrix4d arg1);

    Matrix4d mul0(Matrix4dc arg0, Matrix4d arg1);

    Matrix4d mul(
        double arg0,
        double arg1,
        double arg2,
        double arg3,
        double arg4,
        double arg5,
        double arg6,
        double arg7,
        double arg8,
        double arg9,
        double arg10,
        double arg11,
        double arg12,
        double arg13,
        double arg14,
        double arg15,
        Matrix4d arg16
    );

    Matrix4d mul3x3(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, Matrix4d arg9);

    Matrix4d mulLocal(Matrix4dc arg0, Matrix4d arg1);

    Matrix4d mulLocalAffine(Matrix4dc arg0, Matrix4d arg1);

    Matrix4d mul(Matrix3x2dc arg0, Matrix4d arg1);

    Matrix4d mul(Matrix3x2fc arg0, Matrix4d arg1);

    Matrix4d mul(Matrix4x3dc arg0, Matrix4d arg1);

    Matrix4d mul(Matrix4x3fc arg0, Matrix4d arg1);

    Matrix4d mul(Matrix4fc arg0, Matrix4d arg1);

    Matrix4d mulPerspectiveAffine(Matrix4dc arg0, Matrix4d arg1);

    Matrix4d mulPerspectiveAffine(Matrix4x3dc arg0, Matrix4d arg1);

    Matrix4d mulAffineR(Matrix4dc arg0, Matrix4d arg1);

    Matrix4d mulAffine(Matrix4dc arg0, Matrix4d arg1);

    Matrix4d mulTranslationAffine(Matrix4dc arg0, Matrix4d arg1);

    Matrix4d mulOrthoAffine(Matrix4dc arg0, Matrix4d arg1);

    Matrix4d fma4x3(Matrix4dc arg0, double arg1, Matrix4d arg2);

    Matrix4d add(Matrix4dc arg0, Matrix4d arg1);

    Matrix4d sub(Matrix4dc arg0, Matrix4d arg1);

    Matrix4d mulComponentWise(Matrix4dc arg0, Matrix4d arg1);

    Matrix4d add4x3(Matrix4dc arg0, Matrix4d arg1);

    Matrix4d add4x3(Matrix4fc arg0, Matrix4d arg1);

    Matrix4d sub4x3(Matrix4dc arg0, Matrix4d arg1);

    Matrix4d mul4x3ComponentWise(Matrix4dc arg0, Matrix4d arg1);

    double determinant();

    double determinant3x3();

    double determinantAffine();

    Matrix4d invert(Matrix4d arg0);

    Matrix4d invertPerspective(Matrix4d arg0);

    Matrix4d invertFrustum(Matrix4d arg0);

    Matrix4d invertOrtho(Matrix4d arg0);

    Matrix4d invertPerspectiveView(Matrix4dc arg0, Matrix4d arg1);

    Matrix4d invertPerspectiveView(Matrix4x3dc arg0, Matrix4d arg1);

    Matrix4d invertAffine(Matrix4d arg0);

    Matrix4d transpose(Matrix4d arg0);

    Matrix4d transpose3x3(Matrix4d arg0);

    Matrix3d transpose3x3(Matrix3d arg0);

    Vector3d getTranslation(Vector3d arg0);

    Vector3d getScale(Vector3d arg0);

    Matrix4d get(Matrix4d arg0);

    Matrix4x3d get4x3(Matrix4x3d arg0);

    Matrix3d get3x3(Matrix3d arg0);

    Quaternionf getUnnormalizedRotation(Quaternionf arg0);

    Quaternionf getNormalizedRotation(Quaternionf arg0);

    Quaterniond getUnnormalizedRotation(Quaterniond arg0);

    Quaterniond getNormalizedRotation(Quaterniond arg0);

    DoubleBuffer get(DoubleBuffer arg0);

    DoubleBuffer get(int arg0, DoubleBuffer arg1);

    FloatBuffer get(FloatBuffer arg0);

    FloatBuffer get(int arg0, FloatBuffer arg1);

    ByteBuffer get(ByteBuffer arg0);

    ByteBuffer get(int arg0, ByteBuffer arg1);

    Matrix4dc getToAddress(long arg0);

    ByteBuffer getFloats(ByteBuffer arg0);

    ByteBuffer getFloats(int arg0, ByteBuffer arg1);

    double[] get(double[] var1, int var2);

    double[] get(double[] var1);

    float[] get(float[] var1, int var2);

    float[] get(float[] var1);

    DoubleBuffer getTransposed(DoubleBuffer arg0);

    DoubleBuffer getTransposed(int arg0, DoubleBuffer arg1);

    ByteBuffer getTransposed(ByteBuffer arg0);

    ByteBuffer getTransposed(int arg0, ByteBuffer arg1);

    DoubleBuffer get4x3Transposed(DoubleBuffer arg0);

    DoubleBuffer get4x3Transposed(int arg0, DoubleBuffer arg1);

    ByteBuffer get4x3Transposed(ByteBuffer arg0);

    ByteBuffer get4x3Transposed(int arg0, ByteBuffer arg1);

    Vector4d transform(Vector4d arg0);

    Vector4d transform(Vector4dc arg0, Vector4d arg1);

    Vector4d transform(double arg0, double arg1, double arg2, double arg3, Vector4d arg4);

    Vector4d transformTranspose(Vector4d arg0);

    Vector4d transformTranspose(Vector4dc arg0, Vector4d arg1);

    Vector4d transformTranspose(double arg0, double arg1, double arg2, double arg3, Vector4d arg4);

    Vector4d transformProject(Vector4d arg0);

    Vector4d transformProject(Vector4dc arg0, Vector4d arg1);

    Vector3d transformProject(Vector4dc arg0, Vector3d arg1);

    Vector4d transformProject(double arg0, double arg1, double arg2, double arg3, Vector4d arg4);

    Vector3d transformProject(Vector3d arg0);

    Vector3d transformProject(Vector3dc arg0, Vector3d arg1);

    Vector3d transformProject(double arg0, double arg1, double arg2, Vector3d arg3);

    Vector3d transformProject(double arg0, double arg1, double arg2, double arg3, Vector3d arg4);

    Vector3d transformPosition(Vector3d arg0);

    Vector3d transformPosition(Vector3dc arg0, Vector3d arg1);

    Vector3d transformPosition(double arg0, double arg1, double arg2, Vector3d arg3);

    Vector3d transformDirection(Vector3d arg0);

    Vector3d transformDirection(Vector3dc arg0, Vector3d arg1);

    Vector3f transformDirection(Vector3f arg0);

    Vector3f transformDirection(Vector3fc arg0, Vector3f arg1);

    Vector3d transformDirection(double arg0, double arg1, double arg2, Vector3d arg3);

    Vector3f transformDirection(double arg0, double arg1, double arg2, Vector3f arg3);

    Vector4d transformAffine(Vector4d arg0);

    Vector4d transformAffine(Vector4dc arg0, Vector4d arg1);

    Vector4d transformAffine(double arg0, double arg1, double arg2, double arg3, Vector4d arg4);

    Matrix4d scale(Vector3dc arg0, Matrix4d arg1);

    Matrix4d scale(double arg0, double arg1, double arg2, Matrix4d arg3);

    Matrix4d scale(double arg0, Matrix4d arg1);

    Matrix4d scaleXY(double arg0, double arg1, Matrix4d arg2);

    Matrix4d scaleAround(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6);

    Matrix4d scaleAround(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4);

    Matrix4d scaleLocal(double arg0, Matrix4d arg1);

    Matrix4d scaleLocal(double arg0, double arg1, double arg2, Matrix4d arg3);

    Matrix4d scaleAroundLocal(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6);

    Matrix4d scaleAroundLocal(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4);

    Matrix4d rotate(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4);

    Matrix4d rotateTranslation(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4);

    Matrix4d rotateAffine(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4);

    Matrix4d rotateAroundAffine(Quaterniondc arg0, double arg1, double arg2, double arg3, Matrix4d arg4);

    Matrix4d rotateAround(Quaterniondc arg0, double arg1, double arg2, double arg3, Matrix4d arg4);

    Matrix4d rotateLocal(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4);

    Matrix4d rotateLocalX(double arg0, Matrix4d arg1);

    Matrix4d rotateLocalY(double arg0, Matrix4d arg1);

    Matrix4d rotateLocalZ(double arg0, Matrix4d arg1);

    Matrix4d rotateAroundLocal(Quaterniondc arg0, double arg1, double arg2, double arg3, Matrix4d arg4);

    Matrix4d translate(Vector3dc arg0, Matrix4d arg1);

    Matrix4d translate(Vector3fc arg0, Matrix4d arg1);

    Matrix4d translate(double arg0, double arg1, double arg2, Matrix4d arg3);

    Matrix4d translateLocal(Vector3fc arg0, Matrix4d arg1);

    Matrix4d translateLocal(Vector3dc arg0, Matrix4d arg1);

    Matrix4d translateLocal(double arg0, double arg1, double arg2, Matrix4d arg3);

    Matrix4d rotateX(double arg0, Matrix4d arg1);

    Matrix4d rotateY(double arg0, Matrix4d arg1);

    Matrix4d rotateZ(double arg0, Matrix4d arg1);

    Matrix4d rotateTowardsXY(double arg0, double arg1, Matrix4d arg2);

    Matrix4d rotateXYZ(double arg0, double arg1, double arg2, Matrix4d arg3);

    Matrix4d rotateAffineXYZ(double arg0, double arg1, double arg2, Matrix4d arg3);

    Matrix4d rotateZYX(double arg0, double arg1, double arg2, Matrix4d arg3);

    Matrix4d rotateAffineZYX(double arg0, double arg1, double arg2, Matrix4d arg3);

    Matrix4d rotateYXZ(double arg0, double arg1, double arg2, Matrix4d arg3);

    Matrix4d rotateAffineYXZ(double arg0, double arg1, double arg2, Matrix4d arg3);

    Matrix4d rotate(Quaterniondc arg0, Matrix4d arg1);

    Matrix4d rotate(Quaternionfc arg0, Matrix4d arg1);

    Matrix4d rotateAffine(Quaterniondc arg0, Matrix4d arg1);

    Matrix4d rotateTranslation(Quaterniondc arg0, Matrix4d arg1);

    Matrix4d rotateTranslation(Quaternionfc arg0, Matrix4d arg1);

    Matrix4d rotateLocal(Quaterniondc arg0, Matrix4d arg1);

    Matrix4d rotateAffine(Quaternionfc arg0, Matrix4d arg1);

    Matrix4d rotateLocal(Quaternionfc arg0, Matrix4d arg1);

    Matrix4d rotate(AxisAngle4f arg0, Matrix4d arg1);

    Matrix4d rotate(AxisAngle4d arg0, Matrix4d arg1);

    Matrix4d rotate(double arg0, Vector3dc arg1, Matrix4d arg2);

    Matrix4d rotate(double arg0, Vector3fc arg1, Matrix4d arg2);

    Vector4d getRow(int arg0, Vector4d arg1) throws IndexOutOfBoundsException;

    Vector3d getRow(int arg0, Vector3d arg1) throws IndexOutOfBoundsException;

    Vector4d getColumn(int arg0, Vector4d arg1) throws IndexOutOfBoundsException;

    Vector3d getColumn(int arg0, Vector3d arg1) throws IndexOutOfBoundsException;

    double get(int arg0, int arg1);

    double getRowColumn(int arg0, int arg1);

    Matrix4d normal(Matrix4d arg0);

    Matrix3d normal(Matrix3d arg0);

    Matrix3d cofactor3x3(Matrix3d arg0);

    Matrix4d cofactor3x3(Matrix4d arg0);

    Matrix4d normalize3x3(Matrix4d arg0);

    Matrix3d normalize3x3(Matrix3d arg0);

    Vector4d unproject(double var1, double var3, double var5, int[] var7, Vector4d var8);

    Vector3d unproject(double var1, double var3, double var5, int[] var7, Vector3d var8);

    Vector4d unproject(Vector3dc var1, int[] var2, Vector4d var3);

    Vector3d unproject(Vector3dc var1, int[] var2, Vector3d var3);

    Matrix4d unprojectRay(double var1, double var3, int[] var5, Vector3d var6, Vector3d var7);

    Matrix4d unprojectRay(Vector2dc var1, int[] var2, Vector3d var3, Vector3d var4);

    Vector4d unprojectInv(Vector3dc var1, int[] var2, Vector4d var3);

    Vector4d unprojectInv(double var1, double var3, double var5, int[] var7, Vector4d var8);

    Vector3d unprojectInv(Vector3dc var1, int[] var2, Vector3d var3);

    Vector3d unprojectInv(double var1, double var3, double var5, int[] var7, Vector3d var8);

    Matrix4d unprojectInvRay(Vector2dc var1, int[] var2, Vector3d var3, Vector3d var4);

    Matrix4d unprojectInvRay(double var1, double var3, int[] var5, Vector3d var6, Vector3d var7);

    Vector4d project(double var1, double var3, double var5, int[] var7, Vector4d var8);

    Vector3d project(double var1, double var3, double var5, int[] var7, Vector3d var8);

    Vector4d project(Vector3dc var1, int[] var2, Vector4d var3);

    Vector3d project(Vector3dc var1, int[] var2, Vector3d var3);

    Matrix4d reflect(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4);

    Matrix4d reflect(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6);

    Matrix4d reflect(Quaterniondc arg0, Vector3dc arg1, Matrix4d arg2);

    Matrix4d reflect(Vector3dc arg0, Vector3dc arg1, Matrix4d arg2);

    Matrix4d ortho(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6, Matrix4d arg7);

    Matrix4d ortho(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6);

    Matrix4d orthoLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6, Matrix4d arg7);

    Matrix4d orthoLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6);

    Matrix4d orthoSymmetric(double arg0, double arg1, double arg2, double arg3, boolean arg4, Matrix4d arg5);

    Matrix4d orthoSymmetric(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4);

    Matrix4d orthoSymmetricLH(double arg0, double arg1, double arg2, double arg3, boolean arg4, Matrix4d arg5);

    Matrix4d orthoSymmetricLH(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4);

    Matrix4d ortho2D(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4);

    Matrix4d ortho2DLH(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4);

    Matrix4d lookAlong(Vector3dc arg0, Vector3dc arg1, Matrix4d arg2);

    Matrix4d lookAlong(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6);

    Matrix4d lookAt(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2, Matrix4d arg3);

    Matrix4d lookAt(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, Matrix4d arg9);

    Matrix4d lookAtPerspective(
        double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, Matrix4d arg9
    );

    Matrix4d lookAtLH(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2, Matrix4d arg3);

    Matrix4d lookAtLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, Matrix4d arg9);

    Matrix4d lookAtPerspectiveLH(
        double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, Matrix4d arg9
    );

    Matrix4d perspective(double arg0, double arg1, double arg2, double arg3, boolean arg4, Matrix4d arg5);

    Matrix4d perspective(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4);

    Matrix4d perspectiveRect(double arg0, double arg1, double arg2, double arg3, boolean arg4, Matrix4d arg5);

    Matrix4d perspectiveRect(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4);

    Matrix4d perspectiveRect(double arg0, double arg1, double arg2, double arg3, boolean arg4);

    Matrix4d perspectiveRect(double arg0, double arg1, double arg2, double arg3);

    Matrix4d perspectiveOffCenter(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6, Matrix4d arg7);

    Matrix4d perspectiveOffCenter(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6);

    Matrix4d perspectiveOffCenter(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6);

    Matrix4d perspectiveOffCenter(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5);

    Matrix4d perspectiveLH(double arg0, double arg1, double arg2, double arg3, boolean arg4, Matrix4d arg5);

    Matrix4d perspectiveLH(double arg0, double arg1, double arg2, double arg3, Matrix4d arg4);

    Matrix4d frustum(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6, Matrix4d arg7);

    Matrix4d frustum(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6);

    Matrix4d frustumLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6, Matrix4d arg7);

    Matrix4d frustumLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6);

    Vector4d frustumPlane(int arg0, Vector4d arg1);

    Vector3d frustumCorner(int arg0, Vector3d arg1);

    Vector3d perspectiveOrigin(Vector3d arg0);

    Vector3d perspectiveInvOrigin(Vector3d arg0);

    double perspectiveFov();

    double perspectiveNear();

    double perspectiveFar();

    Vector3d frustumRayDir(double arg0, double arg1, Vector3d arg2);

    Vector3d positiveZ(Vector3d arg0);

    Vector3d normalizedPositiveZ(Vector3d arg0);

    Vector3d positiveX(Vector3d arg0);

    Vector3d normalizedPositiveX(Vector3d arg0);

    Vector3d positiveY(Vector3d arg0);

    Vector3d normalizedPositiveY(Vector3d arg0);

    Vector3d originAffine(Vector3d arg0);

    Vector3d origin(Vector3d arg0);

    Matrix4d shadow(Vector4dc arg0, double arg1, double arg2, double arg3, double arg4, Matrix4d arg5);

    Matrix4d shadow(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, Matrix4d arg8);

    Matrix4d shadow(Vector4dc arg0, Matrix4dc arg1, Matrix4d arg2);

    Matrix4d shadow(double arg0, double arg1, double arg2, double arg3, Matrix4dc arg4, Matrix4d arg5);

    Matrix4d pick(double var1, double var3, double var5, double var7, int[] var9, Matrix4d var10);

    boolean isAffine();

    Matrix4d arcball(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6);

    Matrix4d arcball(double arg0, Vector3dc arg1, double arg2, double arg3, Matrix4d arg4);

    Matrix4d projectedGridRange(Matrix4dc arg0, double arg1, double arg2, Matrix4d arg3);

    Matrix4d perspectiveFrustumSlice(double arg0, double arg1, Matrix4d arg2);

    Matrix4d orthoCrop(Matrix4dc arg0, Matrix4d arg1);

    Matrix4d transformAab(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Vector3d arg6, Vector3d arg7);

    Matrix4d transformAab(Vector3dc arg0, Vector3dc arg1, Vector3d arg2, Vector3d arg3);

    Matrix4d lerp(Matrix4dc arg0, double arg1, Matrix4d arg2);

    Matrix4d rotateTowards(Vector3dc arg0, Vector3dc arg1, Matrix4d arg2);

    Matrix4d rotateTowards(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4d arg6);

    Vector3d getEulerAnglesZYX(Vector3d arg0);

    boolean testPoint(double arg0, double arg1, double arg2);

    boolean testSphere(double arg0, double arg1, double arg2, double arg3);

    boolean testAab(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5);

    Matrix4d obliqueZ(double arg0, double arg1, Matrix4d arg2);

    Matrix4d withLookAtUp(Vector3dc arg0, Matrix4d arg1);

    Matrix4d withLookAtUp(double arg0, double arg1, double arg2, Matrix4d arg3);

    boolean equals(Matrix4dc arg0, double arg1);

    boolean isFinite();
}
