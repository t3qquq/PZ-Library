// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.vector;

import java.io.Serializable;
import java.nio.FloatBuffer;

public abstract class Vector implements Serializable, ReadableVector {
    protected Vector() {
    }

    @Override
    public final float length() {
        return (float)Math.sqrt(this.lengthSquared());
    }

    @Override
    public abstract float lengthSquared();

    public abstract Vector load(FloatBuffer var1);

    public abstract Vector negate();

    public final Vector normalise() {
        float float0 = this.length();
        if (float0 != 0.0F) {
            float float1 = 1.0F / float0;
            return this.scale(float1);
        } else {
            throw new IllegalStateException("Zero length vector");
        }
    }

    @Override
    public abstract Vector store(FloatBuffer var1);

    public abstract Vector scale(float var1);
}
