// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

public interface Vector2dc {
    double x();

    double y();

    ByteBuffer get(ByteBuffer arg0);

    ByteBuffer get(int arg0, ByteBuffer arg1);

    DoubleBuffer get(DoubleBuffer arg0);

    DoubleBuffer get(int arg0, DoubleBuffer arg1);

    Vector2dc getToAddress(long arg0);

    Vector2d sub(double arg0, double arg1, Vector2d arg2);

    Vector2d sub(Vector2dc arg0, Vector2d arg1);

    Vector2d sub(Vector2fc arg0, Vector2d arg1);

    Vector2d mul(double arg0, Vector2d arg1);

    Vector2d mul(double arg0, double arg1, Vector2d arg2);

    Vector2d mul(Vector2dc arg0, Vector2d arg1);

    Vector2d div(double arg0, Vector2d arg1);

    Vector2d div(double arg0, double arg1, Vector2d arg2);

    Vector2d div(Vector2fc arg0, Vector2d arg1);

    Vector2d div(Vector2dc arg0, Vector2d arg1);

    Vector2d mul(Matrix2dc arg0, Vector2d arg1);

    Vector2d mul(Matrix2fc arg0, Vector2d arg1);

    Vector2d mulTranspose(Matrix2dc arg0, Vector2d arg1);

    Vector2d mulTranspose(Matrix2fc arg0, Vector2d arg1);

    Vector2d mulPosition(Matrix3x2dc arg0, Vector2d arg1);

    Vector2d mulDirection(Matrix3x2dc arg0, Vector2d arg1);

    double dot(Vector2dc arg0);

    double angle(Vector2dc arg0);

    double lengthSquared();

    double length();

    double distance(Vector2dc arg0);

    double distanceSquared(Vector2dc arg0);

    double distance(Vector2fc arg0);

    double distanceSquared(Vector2fc arg0);

    double distance(double arg0, double arg1);

    double distanceSquared(double arg0, double arg1);

    Vector2d normalize(Vector2d arg0);

    Vector2d normalize(double arg0, Vector2d arg1);

    Vector2d add(double arg0, double arg1, Vector2d arg2);

    Vector2d add(Vector2dc arg0, Vector2d arg1);

    Vector2d add(Vector2fc arg0, Vector2d arg1);

    Vector2d negate(Vector2d arg0);

    Vector2d lerp(Vector2dc arg0, double arg1, Vector2d arg2);

    Vector2d fma(Vector2dc arg0, Vector2dc arg1, Vector2d arg2);

    Vector2d fma(double arg0, Vector2dc arg1, Vector2d arg2);

    Vector2d min(Vector2dc arg0, Vector2d arg1);

    Vector2d max(Vector2dc arg0, Vector2d arg1);

    int maxComponent();

    int minComponent();

    double get(int arg0) throws IllegalArgumentException;

    Vector2i get(int arg0, Vector2i arg1);

    Vector2f get(Vector2f arg0);

    Vector2d get(Vector2d arg0);

    Vector2d floor(Vector2d arg0);

    Vector2d ceil(Vector2d arg0);

    Vector2d round(Vector2d arg0);

    boolean isFinite();

    Vector2d absolute(Vector2d arg0);

    boolean equals(Vector2dc arg0, double arg1);

    boolean equals(double arg0, double arg1);
}
