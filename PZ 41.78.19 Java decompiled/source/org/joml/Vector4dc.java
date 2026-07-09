// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

public interface Vector4dc {
    double x();

    double y();

    double z();

    double w();

    ByteBuffer get(ByteBuffer arg0);

    ByteBuffer get(int arg0, ByteBuffer arg1);

    DoubleBuffer get(DoubleBuffer arg0);

    DoubleBuffer get(int arg0, DoubleBuffer arg1);

    FloatBuffer get(FloatBuffer arg0);

    FloatBuffer get(int arg0, FloatBuffer arg1);

    ByteBuffer getf(ByteBuffer arg0);

    ByteBuffer getf(int arg0, ByteBuffer arg1);

    Vector4dc getToAddress(long arg0);

    Vector4d sub(Vector4dc arg0, Vector4d arg1);

    Vector4d sub(Vector4fc arg0, Vector4d arg1);

    Vector4d sub(double arg0, double arg1, double arg2, double arg3, Vector4d arg4);

    Vector4d add(Vector4dc arg0, Vector4d arg1);

    Vector4d add(Vector4fc arg0, Vector4d arg1);

    Vector4d add(double arg0, double arg1, double arg2, double arg3, Vector4d arg4);

    Vector4d fma(Vector4dc arg0, Vector4dc arg1, Vector4d arg2);

    Vector4d fma(double arg0, Vector4dc arg1, Vector4d arg2);

    Vector4d mul(Vector4dc arg0, Vector4d arg1);

    Vector4d mul(Vector4fc arg0, Vector4d arg1);

    Vector4d div(Vector4dc arg0, Vector4d arg1);

    Vector4d mul(Matrix4dc arg0, Vector4d arg1);

    Vector4d mul(Matrix4x3dc arg0, Vector4d arg1);

    Vector4d mul(Matrix4x3fc arg0, Vector4d arg1);

    Vector4d mul(Matrix4fc arg0, Vector4d arg1);

    Vector4d mulTranspose(Matrix4dc arg0, Vector4d arg1);

    Vector4d mulAffine(Matrix4dc arg0, Vector4d arg1);

    Vector4d mulAffineTranspose(Matrix4dc arg0, Vector4d arg1);

    Vector4d mulProject(Matrix4dc arg0, Vector4d arg1);

    Vector3d mulProject(Matrix4dc arg0, Vector3d arg1);

    Vector4d mulAdd(Vector4dc arg0, Vector4dc arg1, Vector4d arg2);

    Vector4d mulAdd(double arg0, Vector4dc arg1, Vector4d arg2);

    Vector4d mul(double arg0, Vector4d arg1);

    Vector4d div(double arg0, Vector4d arg1);

    Vector4d rotate(Quaterniondc arg0, Vector4d arg1);

    Vector4d rotateAxis(double arg0, double arg1, double arg2, double arg3, Vector4d arg4);

    Vector4d rotateX(double arg0, Vector4d arg1);

    Vector4d rotateY(double arg0, Vector4d arg1);

    Vector4d rotateZ(double arg0, Vector4d arg1);

    double lengthSquared();

    double length();

    Vector4d normalize(Vector4d arg0);

    Vector4d normalize(double arg0, Vector4d arg1);

    Vector4d normalize3(Vector4d arg0);

    double distance(Vector4dc arg0);

    double distance(double arg0, double arg1, double arg2, double arg3);

    double distanceSquared(Vector4dc arg0);

    double distanceSquared(double arg0, double arg1, double arg2, double arg3);

    double dot(Vector4dc arg0);

    double dot(double arg0, double arg1, double arg2, double arg3);

    double angleCos(Vector4dc arg0);

    double angle(Vector4dc arg0);

    Vector4d negate(Vector4d arg0);

    Vector4d min(Vector4dc arg0, Vector4d arg1);

    Vector4d max(Vector4dc arg0, Vector4d arg1);

    Vector4d smoothStep(Vector4dc arg0, double arg1, Vector4d arg2);

    Vector4d hermite(Vector4dc arg0, Vector4dc arg1, Vector4dc arg2, double arg3, Vector4d arg4);

    Vector4d lerp(Vector4dc arg0, double arg1, Vector4d arg2);

    double get(int arg0) throws IllegalArgumentException;

    Vector4i get(int arg0, Vector4i arg1);

    Vector4f get(Vector4f arg0);

    Vector4d get(Vector4d arg0);

    int maxComponent();

    int minComponent();

    Vector4d floor(Vector4d arg0);

    Vector4d ceil(Vector4d arg0);

    Vector4d round(Vector4d arg0);

    boolean isFinite();

    Vector4d absolute(Vector4d arg0);

    boolean equals(Vector4dc arg0, double arg1);

    boolean equals(double arg0, double arg1, double arg2, double arg3);
}
