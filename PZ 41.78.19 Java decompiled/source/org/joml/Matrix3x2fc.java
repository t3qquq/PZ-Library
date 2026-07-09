// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public interface Matrix3x2fc {
    float m00();

    float m01();

    float m10();

    float m11();

    float m20();

    float m21();

    Matrix3x2f mul(Matrix3x2fc arg0, Matrix3x2f arg1);

    Matrix3x2f mulLocal(Matrix3x2fc arg0, Matrix3x2f arg1);

    float determinant();

    Matrix3x2f invert(Matrix3x2f arg0);

    Matrix3x2f translate(float arg0, float arg1, Matrix3x2f arg2);

    Matrix3x2f translate(Vector2fc arg0, Matrix3x2f arg1);

    Matrix3x2f translateLocal(Vector2fc arg0, Matrix3x2f arg1);

    Matrix3x2f translateLocal(float arg0, float arg1, Matrix3x2f arg2);

    Matrix3x2f get(Matrix3x2f arg0);

    FloatBuffer get(FloatBuffer arg0);

    FloatBuffer get(int arg0, FloatBuffer arg1);

    ByteBuffer get(ByteBuffer arg0);

    ByteBuffer get(int arg0, ByteBuffer arg1);

    FloatBuffer get3x3(FloatBuffer arg0);

    FloatBuffer get3x3(int arg0, FloatBuffer arg1);

    ByteBuffer get3x3(ByteBuffer arg0);

    ByteBuffer get3x3(int arg0, ByteBuffer arg1);

    FloatBuffer get4x4(FloatBuffer arg0);

    FloatBuffer get4x4(int arg0, FloatBuffer arg1);

    ByteBuffer get4x4(ByteBuffer arg0);

    ByteBuffer get4x4(int arg0, ByteBuffer arg1);

    Matrix3x2fc getToAddress(long arg0);

    float[] get(float[] var1, int var2);

    float[] get(float[] var1);

    float[] get3x3(float[] var1, int var2);

    float[] get3x3(float[] var1);

    float[] get4x4(float[] var1, int var2);

    float[] get4x4(float[] var1);

    Matrix3x2f scale(float arg0, float arg1, Matrix3x2f arg2);

    Matrix3x2f scale(Vector2fc arg0, Matrix3x2f arg1);

    Matrix3x2f scaleAroundLocal(float arg0, float arg1, float arg2, float arg3, Matrix3x2f arg4);

    Matrix3x2f scaleAroundLocal(float arg0, float arg1, float arg2, Matrix3x2f arg3);

    Matrix3x2f scale(float arg0, Matrix3x2f arg1);

    Matrix3x2f scaleLocal(float arg0, Matrix3x2f arg1);

    Matrix3x2f scaleLocal(float arg0, float arg1, Matrix3x2f arg2);

    Matrix3x2f scaleAround(float arg0, float arg1, float arg2, float arg3, Matrix3x2f arg4);

    Matrix3x2f scaleAround(float arg0, float arg1, float arg2, Matrix3x2f arg3);

    Vector3f transform(Vector3f arg0);

    Vector3f transform(Vector3f arg0, Vector3f arg1);

    Vector3f transform(float arg0, float arg1, float arg2, Vector3f arg3);

    Vector2f transformPosition(Vector2f arg0);

    Vector2f transformPosition(Vector2fc arg0, Vector2f arg1);

    Vector2f transformPosition(float arg0, float arg1, Vector2f arg2);

    Vector2f transformDirection(Vector2f arg0);

    Vector2f transformDirection(Vector2fc arg0, Vector2f arg1);

    Vector2f transformDirection(float arg0, float arg1, Vector2f arg2);

    Matrix3x2f rotate(float arg0, Matrix3x2f arg1);

    Matrix3x2f rotateLocal(float arg0, Matrix3x2f arg1);

    Matrix3x2f rotateAbout(float arg0, float arg1, float arg2, Matrix3x2f arg3);

    Matrix3x2f rotateTo(Vector2fc arg0, Vector2fc arg1, Matrix3x2f arg2);

    Matrix3x2f view(float arg0, float arg1, float arg2, float arg3, Matrix3x2f arg4);

    Vector2f origin(Vector2f arg0);

    float[] viewArea(float[] var1);

    Vector2f positiveX(Vector2f arg0);

    Vector2f normalizedPositiveX(Vector2f arg0);

    Vector2f positiveY(Vector2f arg0);

    Vector2f normalizedPositiveY(Vector2f arg0);

    Vector2f unproject(float var1, float var2, int[] var3, Vector2f var4);

    Vector2f unprojectInv(float var1, float var2, int[] var3, Vector2f var4);

    boolean testPoint(float arg0, float arg1);

    boolean testCircle(float arg0, float arg1, float arg2);

    boolean testAar(float arg0, float arg1, float arg2, float arg3);

    boolean equals(Matrix3x2fc arg0, float arg1);

    boolean isFinite();
}
