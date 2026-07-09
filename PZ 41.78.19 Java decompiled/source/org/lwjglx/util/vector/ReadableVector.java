// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.vector;

import java.nio.FloatBuffer;

public interface ReadableVector {
    float length();

    float lengthSquared();

    Vector store(FloatBuffer var1);
}
