// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public interface Vector3fc {
    float x();

    float y();

    float z();

    FloatBuffer get(FloatBuffer arg0);

    FloatBuffer get(int arg0, FloatBuffer arg1);

    ByteBuffer get(ByteBuffer arg0);

    ByteBuffer get(int arg0, ByteBuffer arg1);

    Vector3fc getToAddress(long arg0);

    Vector3f sub(Vector3fc arg0, Vector3f arg1);

    Vector3f sub(float arg0, float arg1, float arg2, Vector3f arg3);

    Vector3f add(Vector3fc arg0, Vector3f arg1);

    Vector3f add(float arg0, float arg1, float arg2, Vector3f arg3);

    Vector3f fma(Vector3fc arg0, Vector3fc arg1, Vector3f arg2);

    Vector3f fma(float arg0, Vector3fc arg1, Vector3f arg2);

    Vector3f mulAdd(Vector3fc arg0, Vector3fc arg1, Vector3f arg2);

    Vector3f mulAdd(float arg0, Vector3fc arg1, Vector3f arg2);

    Vector3f mul(Vector3fc arg0, Vector3f arg1);

    Vector3f div(Vector3fc arg0, Vector3f arg1);

    Vector3f mulProject(Matrix4fc arg0, Vector3f arg1);

    Vector3f mulProject(Matrix4fc arg0, float arg1, Vector3f arg2);

    Vector3f mul(Matrix3fc arg0, Vector3f arg1);

    Vector3f mul(Matrix3dc arg0, Vector3f arg1);

    Vector3f mul(Matrix3x2fc arg0, Vector3f arg1);

    Vector3f mulTranspose(Matrix3fc arg0, Vector3f arg1);

    Vector3f mulPosition(Matrix4fc arg0, Vector3f arg1);

    Vector3f mulPosition(Matrix4x3fc arg0, Vector3f arg1);

    Vector3f mulTransposePosition(Matrix4fc arg0, Vector3f arg1);

    float mulPositionW(Matrix4fc arg0, Vector3f arg1);

    Vector3f mulDirection(Matrix4dc arg0, Vector3f arg1);

    Vector3f mulDirection(Matrix4fc arg0, Vector3f arg1);

    Vector3f mulDirection(Matrix4x3fc arg0, Vector3f arg1);

    Vector3f mulTransposeDirection(Matrix4fc arg0, Vector3f arg1);

    Vector3f mul(float arg0, Vector3f arg1);

    Vector3f mul(float arg0, float arg1, float arg2, Vector3f arg3);

    Vector3f div(float arg0, Vector3f arg1);

    Vector3f div(float arg0, float arg1, float arg2, Vector3f arg3);

    Vector3f rotate(Quaternionfc arg0, Vector3f arg1);

    Quaternionf rotationTo(Vector3fc arg0, Quaternionf arg1);

    Quaternionf rotationTo(float arg0, float arg1, float arg2, Quaternionf arg3);

    Vector3f rotateAxis(float arg0, float arg1, float arg2, float arg3, Vector3f arg4);

    Vector3f rotateX(float arg0, Vector3f arg1);

    Vector3f rotateY(float arg0, Vector3f arg1);

    Vector3f rotateZ(float arg0, Vector3f arg1);

    float lengthSquared();

    float length();

    Vector3f normalize(Vector3f arg0);

    Vector3f normalize(float arg0, Vector3f arg1);

    Vector3f cross(Vector3fc arg0, Vector3f arg1);

    Vector3f cross(float arg0, float arg1, float arg2, Vector3f arg3);

    float distance(Vector3fc arg0);

    float distance(float arg0, float arg1, float arg2);

    float distanceSquared(Vector3fc arg0);

    float distanceSquared(float arg0, float arg1, float arg2);

    float dot(Vector3fc arg0);

    float dot(float arg0, float arg1, float arg2);

    float angleCos(Vector3fc arg0);

    float angle(Vector3fc arg0);

    float angleSigned(Vector3fc arg0, Vector3fc arg1);

    float angleSigned(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5);

    Vector3f min(Vector3fc arg0, Vector3f arg1);

    Vector3f max(Vector3fc arg0, Vector3f arg1);

    Vector3f negate(Vector3f arg0);

    Vector3f absolute(Vector3f arg0);

    Vector3f reflect(Vector3fc arg0, Vector3f arg1);

    Vector3f reflect(float arg0, float arg1, float arg2, Vector3f arg3);

    Vector3f half(Vector3fc arg0, Vector3f arg1);

    Vector3f half(float arg0, float arg1, float arg2, Vector3f arg3);

    Vector3f smoothStep(Vector3fc arg0, float arg1, Vector3f arg2);

    Vector3f hermite(Vector3fc arg0, Vector3fc arg1, Vector3fc arg2, float arg3, Vector3f arg4);

    Vector3f lerp(Vector3fc arg0, float arg1, Vector3f arg2);

    float get(int arg0) throws IllegalArgumentException;

    Vector3i get(int arg0, Vector3i arg1);

    Vector3f get(Vector3f arg0);

    Vector3d get(Vector3d arg0);

    int maxComponent();

    int minComponent();

    Vector3f orthogonalize(Vector3fc arg0, Vector3f arg1);

    Vector3f orthogonalizeUnit(Vector3fc arg0, Vector3f arg1);

    Vector3f floor(Vector3f arg0);

    Vector3f ceil(Vector3f arg0);

    Vector3f round(Vector3f arg0);

    boolean isFinite();

    boolean equals(Vector3fc arg0, float arg1);

    boolean equals(float arg0, float arg1, float arg2);
}
