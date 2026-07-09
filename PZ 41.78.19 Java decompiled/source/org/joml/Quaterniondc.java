// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

public interface Quaterniondc {
    double x();

    double y();

    double z();

    double w();

    Quaterniond normalize(Quaterniond arg0);

    Quaterniond add(double arg0, double arg1, double arg2, double arg3, Quaterniond arg4);

    Quaterniond add(Quaterniondc arg0, Quaterniond arg1);

    double dot(Quaterniondc arg0);

    double angle();

    Matrix3d get(Matrix3d arg0);

    Matrix3f get(Matrix3f arg0);

    Matrix4d get(Matrix4d arg0);

    Matrix4f get(Matrix4f arg0);

    AxisAngle4f get(AxisAngle4f arg0);

    AxisAngle4d get(AxisAngle4d arg0);

    Quaterniond get(Quaterniond arg0);

    Quaternionf get(Quaternionf arg0);

    Quaterniond mul(Quaterniondc arg0, Quaterniond arg1);

    Quaterniond mul(double arg0, double arg1, double arg2, double arg3, Quaterniond arg4);

    Quaterniond premul(Quaterniondc arg0, Quaterniond arg1);

    Quaterniond premul(double arg0, double arg1, double arg2, double arg3, Quaterniond arg4);

    Vector3d transform(Vector3d arg0);

    Vector3d transformInverse(Vector3d arg0);

    Vector3d transformUnit(Vector3d arg0);

    Vector3d transformInverseUnit(Vector3d arg0);

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

    Vector3f transform(Vector3f arg0);

    Vector3f transformInverse(Vector3f arg0);

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

    Vector3f transformUnit(Vector3f arg0);

    Vector3f transformInverseUnit(Vector3f arg0);

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

    Vector3f transform(double arg0, double arg1, double arg2, Vector3f arg3);

    Vector3f transformInverse(double arg0, double arg1, double arg2, Vector3f arg3);

    Vector4f transform(Vector4fc arg0, Vector4f arg1);

    Vector4f transformInverse(Vector4fc arg0, Vector4f arg1);

    Vector4f transform(double arg0, double arg1, double arg2, Vector4f arg3);

    Vector4f transformInverse(double arg0, double arg1, double arg2, Vector4f arg3);

    Vector4f transformUnit(Vector4f arg0);

    Vector4f transformInverseUnit(Vector4f arg0);

    Vector3f transformUnit(Vector3fc arg0, Vector3f arg1);

    Vector3f transformInverseUnit(Vector3fc arg0, Vector3f arg1);

    Vector3f transformUnit(double arg0, double arg1, double arg2, Vector3f arg3);

    Vector3f transformInverseUnit(double arg0, double arg1, double arg2, Vector3f arg3);

    Vector4f transformUnit(Vector4fc arg0, Vector4f arg1);

    Vector4f transformInverseUnit(Vector4fc arg0, Vector4f arg1);

    Vector4f transformUnit(double arg0, double arg1, double arg2, Vector4f arg3);

    Vector4f transformInverseUnit(double arg0, double arg1, double arg2, Vector4f arg3);

    Quaterniond invert(Quaterniond arg0);

    Quaterniond div(Quaterniondc arg0, Quaterniond arg1);

    Quaterniond conjugate(Quaterniond arg0);

    double lengthSquared();

    Quaterniond slerp(Quaterniondc arg0, double arg1, Quaterniond arg2);

    Quaterniond scale(double arg0, Quaterniond arg1);

    Quaterniond integrate(double arg0, double arg1, double arg2, double arg3, Quaterniond arg4);

    Quaterniond nlerp(Quaterniondc arg0, double arg1, Quaterniond arg2);

    Quaterniond nlerpIterative(Quaterniondc arg0, double arg1, double arg2, Quaterniond arg3);

    Quaterniond lookAlong(Vector3dc arg0, Vector3dc arg1, Quaterniond arg2);

    Quaterniond lookAlong(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Quaterniond arg6);

    Quaterniond difference(Quaterniondc arg0, Quaterniond arg1);

    Quaterniond rotateTo(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, Quaterniond arg6);

    Quaterniond rotateTo(Vector3dc arg0, Vector3dc arg1, Quaterniond arg2);

    Quaterniond rotateX(double arg0, Quaterniond arg1);

    Quaterniond rotateY(double arg0, Quaterniond arg1);

    Quaterniond rotateZ(double arg0, Quaterniond arg1);

    Quaterniond rotateLocalX(double arg0, Quaterniond arg1);

    Quaterniond rotateLocalY(double arg0, Quaterniond arg1);

    Quaterniond rotateLocalZ(double arg0, Quaterniond arg1);

    Quaterniond rotateXYZ(double arg0, double arg1, double arg2, Quaterniond arg3);

    Quaterniond rotateZYX(double arg0, double arg1, double arg2, Quaterniond arg3);

    Quaterniond rotateYXZ(double arg0, double arg1, double arg2, Quaterniond arg3);

    Vector3d getEulerAnglesXYZ(Vector3d arg0);

    Quaterniond rotateAxis(double arg0, double arg1, double arg2, double arg3, Quaterniond arg4);

    Quaterniond rotateAxis(double arg0, Vector3dc arg1, Quaterniond arg2);

    Vector3d positiveX(Vector3d arg0);

    Vector3d normalizedPositiveX(Vector3d arg0);

    Vector3d positiveY(Vector3d arg0);

    Vector3d normalizedPositiveY(Vector3d arg0);

    Vector3d positiveZ(Vector3d arg0);

    Vector3d normalizedPositiveZ(Vector3d arg0);

    Quaterniond conjugateBy(Quaterniondc arg0, Quaterniond arg1);

    boolean isFinite();

    boolean equals(Quaterniondc arg0, double arg1);

    boolean equals(double arg0, double arg1, double arg2, double arg3);
}
