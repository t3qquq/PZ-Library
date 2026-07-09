// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public interface Quaternionfc {
    float x();

    float y();

    float z();

    float w();

    Quaternionf normalize(Quaternionf arg0);

    Quaternionf add(float arg0, float arg1, float arg2, float arg3, Quaternionf arg4);

    Quaternionf add(Quaternionfc arg0, Quaternionf arg1);

    float angle();

    Matrix3f get(Matrix3f arg0);

    Matrix3d get(Matrix3d arg0);

    Matrix4f get(Matrix4f arg0);

    Matrix4d get(Matrix4d arg0);

    Matrix4x3f get(Matrix4x3f arg0);

    Matrix4x3d get(Matrix4x3d arg0);

    AxisAngle4f get(AxisAngle4f arg0);

    AxisAngle4d get(AxisAngle4d arg0);

    Quaterniond get(Quaterniond arg0);

    Quaternionf get(Quaternionf arg0);

    ByteBuffer getAsMatrix3f(ByteBuffer arg0);

    FloatBuffer getAsMatrix3f(FloatBuffer arg0);

    ByteBuffer getAsMatrix4f(ByteBuffer arg0);

    FloatBuffer getAsMatrix4f(FloatBuffer arg0);

    ByteBuffer getAsMatrix4x3f(ByteBuffer arg0);

    FloatBuffer getAsMatrix4x3f(FloatBuffer arg0);

    Quaternionf mul(Quaternionfc arg0, Quaternionf arg1);

    Quaternionf mul(float arg0, float arg1, float arg2, float arg3, Quaternionf arg4);

    Quaternionf premul(Quaternionfc arg0, Quaternionf arg1);

    Quaternionf premul(float arg0, float arg1, float arg2, float arg3, Quaternionf arg4);

    Vector3f transform(Vector3f arg0);

    Vector3f transformInverse(Vector3f arg0);

    Vector3f transformUnit(Vector3f arg0);

    Vector3f transformPositiveX(Vector3f arg0);

    Vector4f transformPositiveX(Vector4f arg0);

    Vector3f transformUnitPositiveX(Vector3f arg0);

    Vector4f transformUnitPositiveX(Vector4f arg0);

    Vector3f transformPositiveY(Vector3f arg0);

    Vector4f transformPositiveY(Vector4f arg0);

    Vector3f transformUnitPositiveY(Vector3f arg0);

    Vector4f transformUnitPositiveY(Vector4f arg0);

    Vector3f transformPositiveZ(Vector3f arg0);

    Vector4f transformPositiveZ(Vector4f arg0);

    Vector3f transformUnitPositiveZ(Vector3f arg0);

    Vector4f transformUnitPositiveZ(Vector4f arg0);

    Vector4f transform(Vector4f arg0);

    Vector4f transformInverse(Vector4f arg0);

    Vector3f transform(Vector3fc arg0, Vector3f arg1);

    Vector3f transformInverse(Vector3fc arg0, Vector3f arg1);

    Vector3f transform(float arg0, float arg1, float arg2, Vector3f arg3);

    Vector3d transform(float arg0, float arg1, float arg2, Vector3d arg3);

    Vector3f transformInverse(float arg0, float arg1, float arg2, Vector3f arg3);

    Vector3d transformInverse(float arg0, float arg1, float arg2, Vector3d arg3);

    Vector3f transformInverseUnit(Vector3f arg0);

    Vector3f transformUnit(Vector3fc arg0, Vector3f arg1);

    Vector3f transformInverseUnit(Vector3fc arg0, Vector3f arg1);

    Vector3f transformUnit(float arg0, float arg1, float arg2, Vector3f arg3);

    Vector3d transformUnit(float arg0, float arg1, float arg2, Vector3d arg3);

    Vector3f transformInverseUnit(float arg0, float arg1, float arg2, Vector3f arg3);

    Vector3d transformInverseUnit(float arg0, float arg1, float arg2, Vector3d arg3);

    Vector4f transform(Vector4fc arg0, Vector4f arg1);

    Vector4f transformInverse(Vector4fc arg0, Vector4f arg1);

    Vector4f transform(float arg0, float arg1, float arg2, Vector4f arg3);

    Vector4f transformInverse(float arg0, float arg1, float arg2, Vector4f arg3);

    Vector4f transformUnit(Vector4fc arg0, Vector4f arg1);

    Vector4f transformUnit(Vector4f arg0);

    Vector4f transformInverseUnit(Vector4f arg0);

    Vector4f transformInverseUnit(Vector4fc arg0, Vector4f arg1);

    Vector4f transformUnit(float arg0, float arg1, float arg2, Vector4f arg3);

    Vector4f transformInverseUnit(float arg0, float arg1, float arg2, Vector4f arg3);

    Vector3d transform(Vector3d arg0);

    Vector3d transformInverse(Vector3d arg0);

    Vector3d transformPositiveX(Vector3d arg0);

    Vector4d transformPositiveX(Vector4d arg0);

    Vector3d transformUnitPositiveX(Vector3d arg0);

    Vector4d transformUnitPositiveX(Vector4d arg0);

    Vector3d transformPositiveY(Vector3d arg0);

    Vector4d transformPositiveY(Vector4d arg0);

    Vector3d transformUnitPositiveY(Vector3d arg0);

    Vector4d transformUnitPositiveY(Vector4d arg0);

    Vector3d transformPositiveZ(Vector3d arg0);

    Vector4d transformPositiveZ(Vector4d arg0);

    Vector3d transformUnitPositiveZ(Vector3d arg0);

    Vector4d transformUnitPositiveZ(Vector4d arg0);

    Vector4d transform(Vector4d arg0);

    Vector4d transformInverse(Vector4d arg0);

    Vector3d transform(Vector3dc arg0, Vector3d arg1);

    Vector3d transformInverse(Vector3dc arg0, Vector3d arg1);

    Vector3d transform(double arg0, double arg1, double arg2, Vector3d arg3);

    Vector3d transformInverse(double arg0, double arg1, double arg2, Vector3d arg3);

    Vector4d transform(Vector4dc arg0, Vector4d arg1);

    Vector4d transformInverse(Vector4dc arg0, Vector4d arg1);

    Vector4d transform(double arg0, double arg1, double arg2, Vector4d arg3);

    Vector4d transformInverse(double arg0, double arg1, double arg2, Vector4d arg3);

    Vector4d transformUnit(Vector4d arg0);

    Vector4d transformInverseUnit(Vector4d arg0);

    Vector3d transformUnit(Vector3dc arg0, Vector3d arg1);

    Vector3d transformInverseUnit(Vector3dc arg0, Vector3d arg1);

    Vector3d transformUnit(double arg0, double arg1, double arg2, Vector3d arg3);

    Vector3d transformInverseUnit(double arg0, double arg1, double arg2, Vector3d arg3);

    Vector4d transformUnit(Vector4dc arg0, Vector4d arg1);

    Vector4d transformInverseUnit(Vector4dc arg0, Vector4d arg1);

    Vector4d transformUnit(double arg0, double arg1, double arg2, Vector4d arg3);

    Vector4d transformInverseUnit(double arg0, double arg1, double arg2, Vector4d arg3);

    Quaternionf invert(Quaternionf arg0);

    Quaternionf div(Quaternionfc arg0, Quaternionf arg1);

    Quaternionf conjugate(Quaternionf arg0);

    Quaternionf rotateXYZ(float arg0, float arg1, float arg2, Quaternionf arg3);

    Quaternionf rotateZYX(float arg0, float arg1, float arg2, Quaternionf arg3);

    Quaternionf rotateYXZ(float arg0, float arg1, float arg2, Quaternionf arg3);

    Vector3f getEulerAnglesXYZ(Vector3f arg0);

    float lengthSquared();

    Quaternionf slerp(Quaternionfc arg0, float arg1, Quaternionf arg2);

    Quaternionf scale(float arg0, Quaternionf arg1);

    Quaternionf integrate(float arg0, float arg1, float arg2, float arg3, Quaternionf arg4);

    Quaternionf nlerp(Quaternionfc arg0, float arg1, Quaternionf arg2);

    Quaternionf nlerpIterative(Quaternionfc arg0, float arg1, float arg2, Quaternionf arg3);

    Quaternionf lookAlong(Vector3fc arg0, Vector3fc arg1, Quaternionf arg2);

    Quaternionf lookAlong(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Quaternionf arg6);

    Quaternionf rotateTo(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Quaternionf arg6);

    Quaternionf rotateTo(Vector3fc arg0, Vector3fc arg1, Quaternionf arg2);

    Quaternionf rotateX(float arg0, Quaternionf arg1);

    Quaternionf rotateY(float arg0, Quaternionf arg1);

    Quaternionf rotateZ(float arg0, Quaternionf arg1);

    Quaternionf rotateLocalX(float arg0, Quaternionf arg1);

    Quaternionf rotateLocalY(float arg0, Quaternionf arg1);

    Quaternionf rotateLocalZ(float arg0, Quaternionf arg1);

    Quaternionf rotateAxis(float arg0, float arg1, float arg2, float arg3, Quaternionf arg4);

    Quaternionf rotateAxis(float arg0, Vector3fc arg1, Quaternionf arg2);

    Quaternionf difference(Quaternionfc arg0, Quaternionf arg1);

    Vector3f positiveX(Vector3f arg0);

    Vector3f normalizedPositiveX(Vector3f arg0);

    Vector3f positiveY(Vector3f arg0);

    Vector3f normalizedPositiveY(Vector3f arg0);

    Vector3f positiveZ(Vector3f arg0);

    Vector3f normalizedPositiveZ(Vector3f arg0);

    Quaternionf conjugateBy(Quaternionfc arg0, Quaternionf arg1);

    boolean isFinite();

    boolean equals(Quaternionfc arg0, float arg1);

    boolean equals(float arg0, float arg1, float arg2, float arg3);
}
