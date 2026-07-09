// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

public interface Matrix3x2dc {
    double m00();

    double m01();

    double m10();

    double m11();

    double m20();

    double m21();

    Matrix3x2d mul(Matrix3x2dc arg0, Matrix3x2d arg1);

    Matrix3x2d mulLocal(Matrix3x2dc arg0, Matrix3x2d arg1);

    double determinant();

    Matrix3x2d invert(Matrix3x2d arg0);

    Matrix3x2d translate(double arg0, double arg1, Matrix3x2d arg2);

    Matrix3x2d translate(Vector2dc arg0, Matrix3x2d arg1);

    Matrix3x2d translateLocal(Vector2dc arg0, Matrix3x2d arg1);

    Matrix3x2d translateLocal(double arg0, double arg1, Matrix3x2d arg2);

    Matrix3x2d get(Matrix3x2d arg0);

    DoubleBuffer get(DoubleBuffer arg0);

    DoubleBuffer get(int arg0, DoubleBuffer arg1);

    ByteBuffer get(ByteBuffer arg0);

    ByteBuffer get(int arg0, ByteBuffer arg1);

    DoubleBuffer get3x3(DoubleBuffer arg0);

    DoubleBuffer get3x3(int arg0, DoubleBuffer arg1);

    ByteBuffer get3x3(ByteBuffer arg0);

    ByteBuffer get3x3(int arg0, ByteBuffer arg1);

    DoubleBuffer get4x4(DoubleBuffer arg0);

    DoubleBuffer get4x4(int arg0, DoubleBuffer arg1);

    ByteBuffer get4x4(ByteBuffer arg0);

    ByteBuffer get4x4(int arg0, ByteBuffer arg1);

    Matrix3x2dc getToAddress(long arg0);

    double[] get(double[] var1, int var2);

    double[] get(double[] var1);

    double[] get3x3(double[] var1, int var2);

    double[] get3x3(double[] var1);

    double[] get4x4(double[] var1, int var2);

    double[] get4x4(double[] var1);

    Matrix3x2d scale(double arg0, double arg1, Matrix3x2d arg2);

    Matrix3x2d scale(Vector2dc arg0, Matrix3x2d arg1);

    Matrix3x2d scale(Vector2fc arg0, Matrix3x2d arg1);

    Matrix3x2d scaleLocal(double arg0, Matrix3x2d arg1);

    Matrix3x2d scaleLocal(double arg0, double arg1, Matrix3x2d arg2);

    Matrix3x2d scaleAroundLocal(double arg0, double arg1, double arg2, double arg3, Matrix3x2d arg4);

    Matrix3x2d scaleAroundLocal(double arg0, double arg1, double arg2, Matrix3x2d arg3);

    Matrix3x2d scale(double arg0, Matrix3x2d arg1);

    Matrix3x2d scaleAround(double arg0, double arg1, double arg2, double arg3, Matrix3x2d arg4);

    Matrix3x2d scaleAround(double arg0, double arg1, double arg2, Matrix3x2d arg3);

    Vector3d transform(Vector3d arg0);

    Vector3d transform(Vector3dc arg0, Vector3d arg1);

    Vector3d transform(double arg0, double arg1, double arg2, Vector3d arg3);

    Vector2d transformPosition(Vector2d arg0);

    Vector2d transformPosition(Vector2dc arg0, Vector2d arg1);

    Vector2d transformPosition(double arg0, double arg1, Vector2d arg2);

    Vector2d transformDirection(Vector2d arg0);

    Vector2d transformDirection(Vector2dc arg0, Vector2d arg1);

    Vector2d transformDirection(double arg0, double arg1, Vector2d arg2);

    Matrix3x2d rotate(double arg0, Matrix3x2d arg1);

    Matrix3x2d rotateLocal(double arg0, Matrix3x2d arg1);

    Matrix3x2d rotateAbout(double arg0, double arg1, double arg2, Matrix3x2d arg3);

    Matrix3x2d rotateTo(Vector2dc arg0, Vector2dc arg1, Matrix3x2d arg2);

    Matrix3x2d view(double arg0, double arg1, double arg2, double arg3, Matrix3x2d arg4);

    Vector2d origin(Vector2d arg0);

    double[] viewArea(double[] var1);

    Vector2d positiveX(Vector2d arg0);

    Vector2d normalizedPositiveX(Vector2d arg0);

    Vector2d positiveY(Vector2d arg0);

    Vector2d normalizedPositiveY(Vector2d arg0);

    Vector2d unproject(double var1, double var3, int[] var5, Vector2d var6);

    Vector2d unprojectInv(double var1, double var3, int[] var5, Vector2d var6);

    boolean testPoint(double arg0, double arg1);

    boolean testCircle(double arg0, double arg1, double arg2);

    boolean testAar(double arg0, double arg1, double arg2, double arg3);

    boolean equals(Matrix3x2dc arg0, double arg1);

    boolean isFinite();
}
