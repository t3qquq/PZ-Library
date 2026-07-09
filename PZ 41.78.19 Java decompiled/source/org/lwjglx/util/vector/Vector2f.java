// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.vector;

import java.io.Serializable;
import java.nio.FloatBuffer;

public class Vector2f extends Vector implements Serializable, ReadableVector2f, WritableVector2f {
    private static final long serialVersionUID = 1L;
    public float x;
    public float y;

    public Vector2f() {
    }

    public Vector2f(ReadableVector2f readableVector2f) {
        this.set(readableVector2f);
    }

    public Vector2f(float float0, float float1) {
        this.set(float0, float1);
    }

    @Override
    public void set(float float0, float float1) {
        this.x = float0;
        this.y = float1;
    }

    public Vector2f set(ReadableVector2f readableVector2f) {
        this.x = readableVector2f.getX();
        this.y = readableVector2f.getY();
        return this;
    }

    @Override
    public float lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public Vector2f translate(float float0, float float1) {
        this.x += float0;
        this.y += float1;
        return this;
    }

    @Override
    public Vector negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    public Vector2f negate(Vector2f vector2f0) {
        if (vector2f0 == null) {
            vector2f0 = new Vector2f();
        }

        vector2f0.x = -this.x;
        vector2f0.y = -this.y;
        return vector2f0;
    }

    public Vector2f normalise(Vector2f vector2f1) {
        float float0 = this.length();
        if (vector2f1 == null) {
            vector2f1 = new Vector2f(this.x / float0, this.y / float0);
        } else {
            vector2f1.set(this.x / float0, this.y / float0);
        }

        return vector2f1;
    }

    public static float dot(Vector2f vector2f1, Vector2f vector2f0) {
        return vector2f1.x * vector2f0.x + vector2f1.y * vector2f0.y;
    }

    public static float angle(Vector2f vector2f1, Vector2f vector2f0) {
        float float0 = dot(vector2f1, vector2f0) / (vector2f1.length() * vector2f0.length());
        if (float0 < -1.0F) {
            float0 = -1.0F;
        } else if (float0 > 1.0F) {
            float0 = 1.0F;
        }

        return (float)Math.acos(float0);
    }

    public static Vector2f add(Vector2f vector2f2, Vector2f vector2f1, Vector2f vector2f0) {
        if (vector2f0 == null) {
            return new Vector2f(vector2f2.x + vector2f1.x, vector2f2.y + vector2f1.y);
        } else {
            vector2f0.set(vector2f2.x + vector2f1.x, vector2f2.y + vector2f1.y);
            return vector2f0;
        }
    }

    public static Vector2f sub(Vector2f vector2f2, Vector2f vector2f1, Vector2f vector2f0) {
        if (vector2f0 == null) {
            return new Vector2f(vector2f2.x - vector2f1.x, vector2f2.y - vector2f1.y);
        } else {
            vector2f0.set(vector2f2.x - vector2f1.x, vector2f2.y - vector2f1.y);
            return vector2f0;
        }
    }

    @Override
    public Vector store(FloatBuffer floatBuffer) {
        floatBuffer.put(this.x);
        floatBuffer.put(this.y);
        return this;
    }

    @Override
    public Vector load(FloatBuffer floatBuffer) {
        this.x = floatBuffer.get();
        this.y = floatBuffer.get();
        return this;
    }

    @Override
    public Vector scale(float float0) {
        this.x *= float0;
        this.y *= float0;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(64);
        stringBuilder.append("Vector2f[");
        stringBuilder.append(this.x);
        stringBuilder.append(", ");
        stringBuilder.append(this.y);
        stringBuilder.append(']');
        return stringBuilder.toString();
    }

    @Override
    public final float getX() {
        return this.x;
    }

    @Override
    public final float getY() {
        return this.y;
    }

    @Override
    public final void setX(float float0) {
        this.x = float0;
    }

    @Override
    public final void setY(float float0) {
        this.y = float0;
    }
}
