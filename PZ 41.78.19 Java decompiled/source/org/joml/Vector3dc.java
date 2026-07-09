// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

public interface Vector3dc {
    double x();

    double y();

    double z();

    ByteBuffer get(ByteBuffer arg0);

    ByteBuffer get(int arg0, ByteBuffer arg1);

    DoubleBuffer get(DoubleBuffer arg0);

    DoubleBuffer get(int arg0, DoubleBuffer arg1);

    FloatBuffer get(FloatBuffer arg0);

    FloatBuffer get(int arg0, FloatBuffer arg1);

    ByteBuffer getf(ByteBuffer arg0);

    ByteBuffer getf(int arg0, ByteBuffer arg1);

    Vector3dc getToAddress(long arg0);

    Vector3d sub(Vector3dc arg0, Vector3d arg1);

    Vector3d sub(Vector3fc arg0, Vector3d arg1);

    Vector3d sub(double arg0, double arg1, double arg2, Vector3d arg3);

    Vector3d add(Vector3dc arg0, Vector3d arg1);

    Vector3d add(Vector3fc arg0, Vector3d arg1);

    Vector3d add(double arg0, double arg1, double arg2, Vector3d arg3);

    Vector3d fma(Vector3dc arg0, Vector3dc arg1, Vector3d arg2);

    Vector3d fma(double arg0, Vector3dc arg1, Vector3d arg2);

    Vector3d fma(Vector3dc arg0, Vector3fc arg1, Vector3d arg2);

    Vector3d fma(Vector3fc arg0, Vector3fc arg1, Vector3d arg2);

    Vector3d fma(double arg0, Vector3fc arg1, Vector3d arg2);

    Vector3d mulAdd(Vector3dc arg0, Vector3dc arg1, Vector3d arg2);

    Vector3d mulAdd(double arg0, Vector3dc arg1, Vector3d arg2);

    Vector3d mulAdd(Vector3fc arg0, Vector3dc arg1, Vector3d arg2);

    Vector3d mul(Vector3fc arg0, Vector3d arg1);

    Vector3d mul(Vector3dc arg0, Vector3d arg1);

    Vector3d div(Vector3fc arg0, Vector3d arg1);

    Vector3d div(Vector3dc arg0, Vector3d arg1);

    Vector3d mulProject(Matrix4dc arg0, double arg1, Vector3d arg2);

    Vector3d mulProject(Matrix4dc arg0, Vector3d arg1);

    Vector3d mulProject(Matrix4fc arg0, Vector3d arg1);

    Vector3d mul(Matrix3dc arg0, Vector3d arg1);

    Vector3f mul(Matrix3dc arg0, Vector3f arg1);

    Vector3d mul(Matrix3fc arg0, Vector3d arg1);

    Vector3d mul(Matrix3x2dc arg0, Vector3d arg1);

    Vector3d mul(Matrix3x2fc arg0, Vector3d arg1);

    Vector3d mulTranspose(Matrix3dc arg0, Vector3d arg1);

    Vector3d mulTranspose(Matrix3fc arg0, Vector3d arg1);

    Vector3d mulPosition(Matrix4dc arg0, Vector3d arg1);

    Vector3d mulPosition(Matrix4fc arg0, Vector3d arg1);

    Vector3d mulPosition(Matrix4x3dc arg0, Vector3d arg1);

    Vector3d mulPosition(Matrix4x3fc arg0, Vector3d arg1);

    Vector3d mulTransposePosition(Matrix4dc arg0, Vector3d arg1);

    Vector3d mulTransposePosition(Matrix4fc arg0, Vector3d arg1);

    double mulPositionW(Matrix4fc arg0, Vector3d arg1);

    double mulPositionW(Matrix4dc arg0, Vector3d arg1);

    Vector3d mulDirection(Matrix4dc arg0, Vector3d arg1);

    Vector3d mulDirection(Matrix4fc arg0, Vector3d arg1);

    Vector3d mulDirection(Matrix4x3dc arg0, Vector3d arg1);

    Vector3d mulDirection(Matrix4x3fc arg0, Vector3d arg1);

    Vector3d mulTransposeDirection(Matrix4dc arg0, Vector3d arg1);

    Vector3d mulTransposeDirection(Matrix4fc arg0, Vector3d arg1);

    Vector3d mul(double arg0, Vector3d arg1);

    Vector3d mul(double arg0, double arg1, double arg2, Vector3d arg3);

    Vector3d rotate(Quaterniondc arg0, Vector3d arg1);

    Quaterniond rotationTo(Vector3dc arg0, Quaterniond arg1);

    Quaterniond rotationTo(double arg0, double arg1, double arg2, Quaterniond arg3);

    Vector3d rotateAxis(double arg0, double arg1, double arg2, double arg3, Vector3d arg4);

    Vector3d rotateX(double arg0, Vector3d arg1);

    Vector3d rotateY(double arg0, Vector3d arg1);

    Vector3d rotateZ(double arg0, Vector3d arg1);

    Vector3d div(double arg0, Vector3d arg1);

    Vector3d div(double arg0, double arg1, double arg2, Vector3d arg3);

    double lengthSquared();

    double length();

    Vector3d normalize(Vector3d arg0);

    Vector3d normalize(double arg0, Vector3d arg1);

    Vector3d cross(Vector3dc arg0, Vector3d arg1);

    Vector3d cross(double arg0, double arg1, double arg2, Vector3d arg3);

    double distance(Vector3dc arg0);

    double distance(double arg0, double arg1, double arg2);

    double distanceSquared(Vector3dc arg0);

    double distanceSquared(double arg0, double arg1, double arg2);

    double dot(Vector3dc arg0);

    double dot(double arg0, double arg1, double arg2);

    double angleCos(Vector3dc arg0);

    double angle(Vector3dc arg0);

    double angleSigned(Vector3dc arg0, Vector3dc arg1);

    double angleSigned(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5);

    Vector3d min(Vector3dc arg0, Vector3d arg1);

    Vector3d max(Vector3dc arg0, Vector3d arg1);

    Vector3d negate(Vector3d arg0);

    Vector3d absolute(Vector3d arg0);

    Vector3d reflect(Vector3dc arg0, Vector3d arg1);

    Vector3d reflect(double arg0, double arg1, double arg2, Vector3d arg3);

    Vector3d half(Vector3dc arg0, Vector3d arg1);

    Vector3d half(double arg0, double arg1, double arg2, Vector3d arg3);

    Vector3d smoothStep(Vector3dc arg0, double arg1, Vector3d arg2);

    Vector3d hermite(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2, double arg3, Vector3d arg4);

    Vector3d lerp(Vector3dc arg0, double arg1, Vector3d arg2);

    double get(int arg0) throws IllegalArgumentException;

    Vector3i get(int arg0, Vector3i arg1);

    Vector3f get(Vector3f arg0);

    Vector3d get(Vector3d arg0);

    int maxComponent();

    int minComponent();

    Vector3d orthogonalize(Vector3dc arg0, Vector3d arg1);

    Vector3d orthogonalizeUnit(Vector3dc arg0, Vector3d arg1);

    Vector3d floor(Vector3d arg0);

    Vector3d ceil(Vector3d arg0);

    Vector3d round(Vector3d arg0);

    boolean isFinite();

    boolean equals(Vector3dc arg0, double arg1);

    boolean equals(double arg0, double arg1, double arg2);
}
