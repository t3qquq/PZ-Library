// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

public interface Matrix3dc {
    double m00();

    double m01();

    double m02();

    double m10();

    double m11();

    double m12();

    double m20();

    double m21();

    double m22();

    Matrix3d mul(Matrix3dc arg0, Matrix3d arg1);

    Matrix3d mulLocal(Matrix3dc arg0, Matrix3d arg1);

    Matrix3d mul(Matrix3fc arg0, Matrix3d arg1);

    double determinant();

    Matrix3d invert(Matrix3d arg0);

    Matrix3d transpose(Matrix3d arg0);

    Matrix3d get(Matrix3d arg0);

    AxisAngle4f getRotation(AxisAngle4f arg0);

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

    Matrix3dc getToAddress(long arg0);

    double[] get(double[] var1, int var2);

    double[] get(double[] var1);

    float[] get(float[] var1, int var2);

    float[] get(float[] var1);

    Matrix3d scale(Vector3dc arg0, Matrix3d arg1);

    Matrix3d scale(double arg0, double arg1, double arg2, Matrix3d arg3);

    Matrix3d scale(double arg0, Matrix3d arg1);

    Matrix3d scaleLocal(double arg0, double arg1, double arg2, Matrix3d arg3);

    Vector3d transform(Vector3d arg0);

    Vector3d transform(Vector3dc arg0, Vector3d arg1);

    Vector3f transform(Vector3f arg0);

    Vector3f transform(Vector3fc arg0, Vector3f arg1);

    Vector3d transform(double arg0, double arg1, double arg2, Vector3d arg3);

    Vector3d transformTranspose(Vector3d arg0);

    Vector3d transformTranspose(Vector3dc arg0, Vector3d arg1);

    Vector3d transformTranspose(double arg0, double arg1, double arg2, Vector3d arg3);

    Matrix3d rotateX(double arg0, Matrix3d arg1);

    Matrix3d rotateY(double arg0, Matrix3d arg1);

    Matrix3d rotateZ(double arg0, Matrix3d arg1);

    Matrix3d rotateXYZ(double arg0, double arg1, double arg2, Matrix3d arg3);

    Matrix3d rotateZYX(double arg0, double arg1, double arg2, Matrix3d arg3);

    Matrix3d rotateYXZ(double arg0, double arg1, double arg2, Matrix3d arg3);

    Matrix3d rotate(double arg0, double arg1, double arg2, double arg3, Matrix3d arg4);

    Matrix3d rotateLocal(double arg0, double arg1, double arg2, double arg3, Matrix3d arg4);

    Matrix3d rotateLocalX(double arg0, Matrix3d arg1);

    Matrix3d rotateLocalY(double arg0, Matrix3d arg1);

    Matrix3d rotateLocalZ(double arg0, Matrix3d arg1);

    Matrix3d rotateLocal(Quaterniondc arg0, Matrix3d arg1);

    Matrix3d rotateLocal(Quaternionfc arg0, Matrix3d arg1);

    Matrix3d rotate(Quaterniondc arg0, Matrix3d arg1);

    Matrix3d rotate(Quaternionfc arg0, Matrix3d arg1);

    Matrix3d rotate(AxisAngle4f arg0, Matrix3d arg1);

    Matrix3d rotate(AxisAngle4d arg0, Matrix3d arg1);

    Matrix3d rotate(double arg0, Vector3dc arg1, Matrix3d arg2);

    Matrix3d rotate(double arg0, Vector3fc arg1, Matrix3d arg2);

    Vector3d getRow(int arg0, Vector3d arg1) throws IndexOutOfBoundsException;

    Vector3d getColumn(int arg0, Vector3d arg1) throws IndexOutOfBoundsException;

    double get(int arg0, int arg1);

    double getRowColumn(int arg0, int arg1);

    Matrix3d normal(Matrix3d arg0);

    Matrix3d cofactor(Matrix3d arg0);

    Matrix3d lookAlong(Vector3dc arg0, Vector3dc arg1, Matrix3d arg2);

    Matrix3d lookAlong(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix3d arg6);

    Vector3d getScale(Vector3d arg0);

    Vector3d positiveZ(Vector3d arg0);

    Vector3d normalizedPositiveZ(Vector3d arg0);

    Vector3d positiveX(Vector3d arg0);

    Vector3d normalizedPositiveX(Vector3d arg0);

    Vector3d positiveY(Vector3d arg0);

    Vector3d normalizedPositiveY(Vector3d arg0);

    Matrix3d add(Matrix3dc arg0, Matrix3d arg1);

    Matrix3d sub(Matrix3dc arg0, Matrix3d arg1);

    Matrix3d mulComponentWise(Matrix3dc arg0, Matrix3d arg1);

    Matrix3d lerp(Matrix3dc arg0, double arg1, Matrix3d arg2);

    Matrix3d rotateTowards(Vector3dc arg0, Vector3dc arg1, Matrix3d arg2);

    Matrix3d rotateTowards(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Matrix3d arg6);

    Vector3d getEulerAnglesZYX(Vector3d arg0);

    Matrix3d obliqueZ(double arg0, double arg1, Matrix3d arg2);

    boolean equals(Matrix3dc arg0, double arg1);

    Matrix3d reflect(double arg0, double arg1, double arg2, Matrix3d arg3);

    Matrix3d reflect(Quaterniondc arg0, Matrix3d arg1);

    Matrix3d reflect(Vector3dc arg0, Matrix3d arg1);

    boolean isFinite();

    double quadraticFormProduct(double arg0, double arg1, double arg2);

    double quadraticFormProduct(Vector3dc arg0);

    double quadraticFormProduct(Vector3fc arg0);
}
