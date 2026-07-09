// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

public interface Matrix4x3dc {
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

    double m00();

    double m01();

    double m02();

    double m10();

    double m11();

    double m12();

    double m20();

    double m21();

    double m22();

    double m30();

    double m31();

    double m32();

    Matrix4d get(Matrix4d arg0);

    Matrix4x3d mul(Matrix4x3dc arg0, Matrix4x3d arg1);

    Matrix4x3d mul(Matrix4x3fc arg0, Matrix4x3d arg1);

    Matrix4x3d mulTranslation(Matrix4x3dc arg0, Matrix4x3d arg1);

    Matrix4x3d mulTranslation(Matrix4x3fc arg0, Matrix4x3d arg1);

    Matrix4x3d mulOrtho(Matrix4x3dc arg0, Matrix4x3d arg1);

    Matrix4x3d fma(Matrix4x3dc arg0, double arg1, Matrix4x3d arg2);

    Matrix4x3d fma(Matrix4x3fc arg0, double arg1, Matrix4x3d arg2);

    Matrix4x3d add(Matrix4x3dc arg0, Matrix4x3d arg1);

    Matrix4x3d add(Matrix4x3fc arg0, Matrix4x3d arg1);

    Matrix4x3d sub(Matrix4x3dc arg0, Matrix4x3d arg1);

    Matrix4x3d sub(Matrix4x3fc arg0, Matrix4x3d arg1);

    Matrix4x3d mulComponentWise(Matrix4x3dc arg0, Matrix4x3d arg1);

    double determinant();

    Matrix4x3d invert(Matrix4x3d arg0);

    Matrix4x3d invertOrtho(Matrix4x3d arg0);

    Matrix4x3d transpose3x3(Matrix4x3d arg0);

    Matrix3d transpose3x3(Matrix3d arg0);

    Vector3d getTranslation(Vector3d arg0);

    Vector3d getScale(Vector3d arg0);

    Matrix4x3d get(Matrix4x3d arg0);

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

    ByteBuffer getFloats(ByteBuffer arg0);

    ByteBuffer getFloats(int arg0, ByteBuffer arg1);

    Matrix4x3dc getToAddress(long arg0);

    double[] get(double[] var1, int var2);

    double[] get(double[] var1);

    float[] get(float[] var1, int var2);

    float[] get(float[] var1);

    double[] get4x4(double[] var1, int var2);

    double[] get4x4(double[] var1);

    float[] get4x4(float[] var1, int var2);

    float[] get4x4(float[] var1);

    DoubleBuffer get4x4(DoubleBuffer arg0);

    DoubleBuffer get4x4(int arg0, DoubleBuffer arg1);

    ByteBuffer get4x4(ByteBuffer arg0);

    ByteBuffer get4x4(int arg0, ByteBuffer arg1);

    DoubleBuffer getTransposed(DoubleBuffer arg0);

    DoubleBuffer getTransposed(int arg0, DoubleBuffer arg1);

    ByteBuffer getTransposed(ByteBuffer arg0);

    ByteBuffer getTransposed(int arg0, ByteBuffer arg1);

    FloatBuffer getTransposed(FloatBuffer arg0);

    FloatBuffer getTransposed(int arg0, FloatBuffer arg1);

    ByteBuffer getTransposedFloats(ByteBuffer arg0);

    ByteBuffer getTransposedFloats(int arg0, ByteBuffer arg1);

    double[] getTransposed(double[] var1, int var2);

    double[] getTransposed(double[] var1);

    Vector4d transform(Vector4d arg0);

    Vector4d transform(Vector4dc arg0, Vector4d arg1);

    Vector3d transformPosition(Vector3d arg0);

    Vector3d transformPosition(Vector3dc arg0, Vector3d arg1);

    Vector3d transformDirection(Vector3d arg0);

    Vector3d transformDirection(Vector3dc arg0, Vector3d arg1);

    Matrix4x3d scale(Vector3dc arg0, Matrix4x3d arg1);

    Matrix4x3d scale(double arg0, double arg1, double arg2, Matrix4x3d arg3);

    Matrix4x3d scale(double arg0, Matrix4x3d arg1);

    Matrix4x3d scaleXY(double arg0, double arg1, Matrix4x3d arg2);

    Matrix4x3d scaleLocal(double arg0, double arg1, double arg2, Matrix4x3d arg3);

    Matrix4x3d rotate(double arg0, double arg1, double arg2, double arg3, Matrix4x3d arg4);

    Matrix4x3d rotateTranslation(double arg0, double arg1, double arg2, double arg3, Matrix4x3d arg4);

    Matrix4x3d rotateAround(Quaterniondc arg0, double arg1, double arg2, double arg3, Matrix4x3d arg4);

    Matrix4x3d rotateLocal(double arg0, double arg1, double arg2, double arg3, Matrix4x3d arg4);

    Matrix4x3d translate(Vector3dc arg0, Matrix4x3d arg1);

    Matrix4x3d translate(Vector3fc arg0, Matrix4x3d arg1);

    Matrix4x3d translate(double arg0, double arg1, double arg2, Matrix4x3d arg3);

    Matrix4x3d translateLocal(Vector3fc arg0, Matrix4x3d arg1);

    Matrix4x3d translateLocal(Vector3dc arg0, Matrix4x3d arg1);

    Matrix4x3d translateLocal(double arg0, double arg1, double arg2, Matrix4x3d arg3);

    Matrix4x3d rotateX(double arg0, Matrix4x3d arg1);

    Matrix4x3d rotateY(double arg0, Matrix4x3d arg1);

    Matrix4x3d rotateZ(double arg0, Matrix4x3d arg1);

    Matrix4x3d rotateXYZ(double arg0, double arg1, double arg2, Matrix4x3d arg3);

    Matrix4x3d rotateZYX(double arg0, double arg1, double arg2, Matrix4x3d arg3);

    Matrix4x3d rotateYXZ(double arg0, double arg1, double arg2, Matrix4x3d arg3);

    Matrix4x3d rotate(Quaterniondc arg0, Matrix4x3d arg1);

    Matrix4x3d rotate(Quaternionfc arg0, Matrix4x3d arg1);

    Matrix4x3d rotateTranslation(Quaterniondc arg0, Matrix4x3d arg1);

    Matrix4x3d rotateTranslation(Quaternionfc arg0, Matrix4x3d arg1);

    Matrix4x3d rotateLocal(Quaterniondc arg0, Matrix4x3d arg1);

    Matrix4x3d rotateLocal(Quaternionfc arg0, Matrix4x3d arg1);

    Matrix4x3d rotate(AxisAngle4f arg0, Matrix4x3d arg1);

    Matrix4x3d rotate(AxisAngle4d arg0, Matrix4x3d arg1);

    Matrix4x3d rotate(double arg0, Vector3dc arg1, Matrix4x3d arg2);

    Matrix4x3d rotate(double arg0, Vector3fc arg1, Matrix4x3d arg2);

    Vector4d getRow(int arg0, Vector4d arg1) throws IndexOutOfBoundsException;

    Vector3d getColumn(int arg0, Vector3d arg1) throws IndexOutOfBoundsException;

    Matrix4x3d normal(Matrix4x3d arg0);

    Matrix3d normal(Matrix3d arg0);

    Matrix3d cofactor3x3(Matrix3d arg0);

    Matrix4x3d cofactor3x3(Matrix4x3d arg0);

    Matrix4x3d normalize3x3(Matrix4x3d arg0);

    Matrix3d normalize3x3(Matrix3d arg0);

    Matrix4x3d reflect(double arg0, double arg1, double arg2, double arg3, Matrix4x3d arg4);

    Matrix4x3d reflect(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4x3d arg6);

    Matrix4x3d reflect(Quaterniondc arg0, Vector3dc arg1, Matrix4x3d arg2);

    Matrix4x3d reflect(Vector3dc arg0, Vector3dc arg1, Matrix4x3d arg2);

    Matrix4x3d ortho(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6, Matrix4x3d arg7);

    Matrix4x3d ortho(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4x3d arg6);

    Matrix4x3d orthoLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, boolean arg6, Matrix4x3d arg7);

    Matrix4x3d orthoLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4x3d arg6);

    Matrix4x3d orthoSymmetric(double arg0, double arg1, double arg2, double arg3, boolean arg4, Matrix4x3d arg5);

    Matrix4x3d orthoSymmetric(double arg0, double arg1, double arg2, double arg3, Matrix4x3d arg4);

    Matrix4x3d orthoSymmetricLH(double arg0, double arg1, double arg2, double arg3, boolean arg4, Matrix4x3d arg5);

    Matrix4x3d orthoSymmetricLH(double arg0, double arg1, double arg2, double arg3, Matrix4x3d arg4);

    Matrix4x3d ortho2D(double arg0, double arg1, double arg2, double arg3, Matrix4x3d arg4);

    Matrix4x3d ortho2DLH(double arg0, double arg1, double arg2, double arg3, Matrix4x3d arg4);

    Matrix4x3d lookAlong(Vector3dc arg0, Vector3dc arg1, Matrix4x3d arg2);

    Matrix4x3d lookAlong(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4x3d arg6);

    Matrix4x3d lookAt(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2, Matrix4x3d arg3);

    Matrix4x3d lookAt(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, Matrix4x3d arg9);

    Matrix4x3d lookAtLH(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2, Matrix4x3d arg3);

    Matrix4x3d lookAtLH(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, Matrix4x3d arg9);

    Vector4d frustumPlane(int arg0, Vector4d arg1);

    Vector3d positiveZ(Vector3d arg0);

    Vector3d normalizedPositiveZ(Vector3d arg0);

    Vector3d positiveX(Vector3d arg0);

    Vector3d normalizedPositiveX(Vector3d arg0);

    Vector3d positiveY(Vector3d arg0);

    Vector3d normalizedPositiveY(Vector3d arg0);

    Vector3d origin(Vector3d arg0);

    Matrix4x3d shadow(Vector4dc arg0, double arg1, double arg2, double arg3, double arg4, Matrix4x3d arg5);

    Matrix4x3d shadow(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, Matrix4x3d arg8);

    Matrix4x3d shadow(Vector4dc arg0, Matrix4x3dc arg1, Matrix4x3d arg2);

    Matrix4x3d shadow(double arg0, double arg1, double arg2, double arg3, Matrix4x3dc arg4, Matrix4x3d arg5);

    Matrix4x3d pick(double var1, double var3, double var5, double var7, int[] var9, Matrix4x3d var10);

    Matrix4x3d arcball(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4x3d arg6);

    Matrix4x3d arcball(double arg0, Vector3dc arg1, double arg2, double arg3, Matrix4x3d arg4);

    Matrix4x3d transformAab(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Vector3d arg6, Vector3d arg7);

    Matrix4x3d transformAab(Vector3dc arg0, Vector3dc arg1, Vector3d arg2, Vector3d arg3);

    Matrix4x3d lerp(Matrix4x3dc arg0, double arg1, Matrix4x3d arg2);

    Matrix4x3d rotateTowards(Vector3dc arg0, Vector3dc arg1, Matrix4x3d arg2);

    Matrix4x3d rotateTowards(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix4x3d arg6);

    Vector3d getEulerAnglesZYX(Vector3d arg0);

    Matrix4x3d obliqueZ(double arg0, double arg1, Matrix4x3d arg2);

    boolean equals(Matrix4x3dc arg0, double arg1);

    boolean isFinite();
}
