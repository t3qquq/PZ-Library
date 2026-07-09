// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public interface Vector2ic {
    int x();

    int y();

    ByteBuffer get(ByteBuffer arg0);

    ByteBuffer get(int arg0, ByteBuffer arg1);

    IntBuffer get(IntBuffer arg0);

    IntBuffer get(int arg0, IntBuffer arg1);

    Vector2ic getToAddress(long arg0);

    Vector2i sub(Vector2ic arg0, Vector2i arg1);

    Vector2i sub(int arg0, int arg1, Vector2i arg2);

    long lengthSquared();

    double length();

    double distance(Vector2ic arg0);

    double distance(int arg0, int arg1);

    long distanceSquared(Vector2ic arg0);

    long distanceSquared(int arg0, int arg1);

    long gridDistance(Vector2ic arg0);

    long gridDistance(int arg0, int arg1);

    Vector2i add(Vector2ic arg0, Vector2i arg1);

    Vector2i add(int arg0, int arg1, Vector2i arg2);

    Vector2i mul(int arg0, Vector2i arg1);

    Vector2i mul(Vector2ic arg0, Vector2i arg1);

    Vector2i mul(int arg0, int arg1, Vector2i arg2);

    Vector2i div(float arg0, Vector2i arg1);

    Vector2i div(int arg0, Vector2i arg1);

    Vector2i negate(Vector2i arg0);

    Vector2i min(Vector2ic arg0, Vector2i arg1);

    Vector2i max(Vector2ic arg0, Vector2i arg1);

    int maxComponent();

    int minComponent();

    Vector2i absolute(Vector2i arg0);

    int get(int arg0) throws IllegalArgumentException;

    boolean equals(int arg0, int arg1);
}
