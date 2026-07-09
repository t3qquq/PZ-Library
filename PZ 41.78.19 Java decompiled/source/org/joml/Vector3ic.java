// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public interface Vector3ic {
    int x();

    int y();

    int z();

    IntBuffer get(IntBuffer arg0);

    IntBuffer get(int arg0, IntBuffer arg1);

    ByteBuffer get(ByteBuffer arg0);

    ByteBuffer get(int arg0, ByteBuffer arg1);

    Vector3ic getToAddress(long arg0);

    Vector3i sub(Vector3ic arg0, Vector3i arg1);

    Vector3i sub(int arg0, int arg1, int arg2, Vector3i arg3);

    Vector3i add(Vector3ic arg0, Vector3i arg1);

    Vector3i add(int arg0, int arg1, int arg2, Vector3i arg3);

    Vector3i mul(int arg0, Vector3i arg1);

    Vector3i mul(Vector3ic arg0, Vector3i arg1);

    Vector3i mul(int arg0, int arg1, int arg2, Vector3i arg3);

    Vector3i div(float arg0, Vector3i arg1);

    Vector3i div(int arg0, Vector3i arg1);

    long lengthSquared();

    double length();

    double distance(Vector3ic arg0);

    double distance(int arg0, int arg1, int arg2);

    long gridDistance(Vector3ic arg0);

    long gridDistance(int arg0, int arg1, int arg2);

    long distanceSquared(Vector3ic arg0);

    long distanceSquared(int arg0, int arg1, int arg2);

    Vector3i negate(Vector3i arg0);

    Vector3i min(Vector3ic arg0, Vector3i arg1);

    Vector3i max(Vector3ic arg0, Vector3i arg1);

    int get(int arg0) throws IllegalArgumentException;

    int maxComponent();

    int minComponent();

    Vector3i absolute(Vector3i arg0);

    boolean equals(int arg0, int arg1, int arg2);
}
