// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

public interface Matrix2dc {
    double m00();

    double m01();

    double m10();

    double m11();

    Matrix2d mul(Matrix2dc arg0, Matrix2d arg1);

    Matrix2d mul(Matrix2fc arg0, Matrix2d arg1);

    Matrix2d mulLocal(Matrix2dc arg0, Matrix2d arg1);

    double determinant();

    Matrix2d invert(Matrix2d arg0);

    Matrix2d transpose(Matrix2d arg0);

    Matrix2d get(Matrix2d arg0);

    Matrix3x2d get(Matrix3x2d arg0);

    Matrix3d get(Matrix3d arg0);

    double getRotation();

    DoubleBuffer get(DoubleBuffer arg0);

    DoubleBuffer get(int arg0, DoubleBuffer arg1);

    ByteBuffer get(ByteBuffer arg0);

    ByteBuffer get(int arg0, ByteBuffer arg1);

    DoubleBuffer getTransposed(DoubleBuffer arg0);

    DoubleBuffer getTransposed(int arg0, DoubleBuffer arg1);

    ByteBuffer getTransposed(ByteBuffer arg0);

    ByteBuffer getTransposed(int arg0, ByteBuffer arg1);

    Matrix2dc getToAddress(long arg0);

    double[] get(double[] var1, int var2);

    double[] get(double[] var1);

    Matrix2d scale(Vector2dc arg0, Matrix2d arg1);

    Matrix2d scale(double arg0, double arg1, Matrix2d arg2);

    Matrix2d scale(double arg0, Matrix2d arg1);

    Matrix2d scaleLocal(double arg0, double arg1, Matrix2d arg2);

    Vector2d transform(Vector2d arg0);

    Vector2d transform(Vector2dc arg0, Vector2d arg1);

    Vector2d transform(double arg0, double arg1, Vector2d arg2);

    Vector2d transformTranspose(Vector2d arg0);

    Vector2d transformTranspose(Vector2dc arg0, Vector2d arg1);

    Vector2d transformTranspose(double arg0, double arg1, Vector2d arg2);

    Matrix2d rotate(double arg0, Matrix2d arg1);

    Matrix2d rotateLocal(double arg0, Matrix2d arg1);

    Vector2d getRow(int arg0, Vector2d arg1) throws IndexOutOfBoundsException;

    Vector2d getColumn(int arg0, Vector2d arg1) throws IndexOutOfBoundsException;

    double get(int arg0, int arg1);

    Matrix2d normal(Matrix2d arg0);

    Vector2d getScale(Vector2d arg0);

    Vector2d positiveX(Vector2d arg0);

    Vector2d normalizedPositiveX(Vector2d arg0);

    Vector2d positiveY(Vector2d arg0);

    Vector2d normalizedPositiveY(Vector2d arg0);

    Matrix2d add(Matrix2dc arg0, Matrix2d arg1);

    Matrix2d sub(Matrix2dc arg0, Matrix2d arg1);

    Matrix2d mulComponentWise(Matrix2dc arg0, Matrix2d arg1);

    Matrix2d lerp(Matrix2dc arg0, double arg1, Matrix2d arg2);

    boolean equals(Matrix2dc arg0, double arg1);

    boolean isFinite();
}
