// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public interface Vector4ic {
    int x();

    int y();

    int z();

    int w();

    IntBuffer get(IntBuffer arg0);

    IntBuffer get(int arg0, IntBuffer arg1);

    ByteBuffer get(ByteBuffer arg0);

    ByteBuffer get(int arg0, ByteBuffer arg1);

    Vector4ic getToAddress(long arg0);

    Vector4i sub(Vector4ic arg0, Vector4i arg1);

    Vector4i sub(int arg0, int arg1, int arg2, int arg3, Vector4i arg4);

    Vector4i add(Vector4ic arg0, Vector4i arg1);

    Vector4i add(int arg0, int arg1, int arg2, int arg3, Vector4i arg4);

    Vector4i mul(Vector4ic arg0, Vector4i arg1);

    Vector4i div(Vector4ic arg0, Vector4i arg1);

    Vector4i mul(int arg0, Vector4i arg1);

    Vector4i div(float arg0, Vector4i arg1);

    Vector4i div(int arg0, Vector4i arg1);

    long lengthSquared();

    double length();

    double distance(Vector4ic arg0);

    double distance(int arg0, int arg1, int arg2, int arg3);

    long gridDistance(Vector4ic arg0);

    long gridDistance(int arg0, int arg1, int arg2, int arg3);

    int distanceSquared(Vector4ic arg0);

    int distanceSquared(int arg0, int arg1, int arg2, int arg3);

    int dot(Vector4ic arg0);

    Vector4i negate(Vector4i arg0);

    Vector4i min(Vector4ic arg0, Vector4i arg1);

    Vector4i max(Vector4ic arg0, Vector4i arg1);

    int get(int arg0) throws IllegalArgumentException;

    int maxComponent();

    int minComponent();

    Vector4i absolute(Vector4i arg0);

    boolean equals(int arg0, int arg1, int arg2, int arg3);
}
