// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.vector;

import java.io.Serializable;
import java.nio.FloatBuffer;

public class Vector3f extends Vector implements Serializable, ReadableVector3f, WritableVector3f {
    private static final long serialVersionUID = 1L;
    public float x;
    public float y;
    public float z;

    public Vector3f() {
    }

    public Vector3f(ReadableVector3f readableVector3f) {
        this.set(readableVector3f);
    }

    public Vector3f(float float0, float float1, float float2) {
        this.set(float0, float1, float2);
    }

    @Override
    public void set(float float0, float float1) {
        this.x = float0;
        this.y = float1;
    }

    @Override
    public void set(float float0, float float1, float float2) {
        this.x = float0;
        this.y = float1;
        this.z = float2;
    }

    public Vector3f set(ReadableVector3f readableVector3f) {
        this.x = readableVector3f.getX();
        this.y = readableVector3f.getY();
        this.z = readableVector3f.getZ();
        return this;
    }

    @Override
    public float lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public Vector3f translate(float float0, float float1, float float2) {
        this.x += float0;
        this.y += float1;
        this.z += float2;
        return this;
    }

    public static Vector3f add(Vector3f vector3f2, Vector3f vector3f1, Vector3f vector3f0) {
        if (vector3f0 == null) {
            return new Vector3f(vector3f2.x + vector3f1.x, vector3f2.y + vector3f1.y, vector3f2.z + vector3f1.z);
        } else {
            vector3f0.set(vector3f2.x + vector3f1.x, vector3f2.y + vector3f1.y, vector3f2.z + vector3f1.z);
            return vector3f0;
        }
    }

    public static Vector3f sub(Vector3f vector3f2, Vector3f vector3f1, Vector3f vector3f0) {
        if (vector3f0 == null) {
            return new Vector3f(vector3f2.x - vector3f1.x, vector3f2.y - vector3f1.y, vector3f2.z - vector3f1.z);
        } else {
            vector3f0.set(vector3f2.x - vector3f1.x, vector3f2.y - vector3f1.y, vector3f2.z - vector3f1.z);
            return vector3f0;
        }
    }

    public static Vector3f cross(Vector3f vector3f2, Vector3f vector3f1, Vector3f vector3f0) {
        if (vector3f0 == null) {
            vector3f0 = new Vector3f();
        }

        vector3f0.set(
            vector3f2.y * vector3f1.z - vector3f2.z * vector3f1.y,
            vector3f1.x * vector3f2.z - vector3f1.z * vector3f2.x,
            vector3f2.x * vector3f1.y - vector3f2.y * vector3f1.x
        );
        return vector3f0;
    }

    @Override
    public Vector negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    public Vector3f negate(Vector3f vector3f0) {
        if (vector3f0 == null) {
            vector3f0 = new Vector3f();
        }

        vector3f0.x = -this.x;
        vector3f0.y = -this.y;
        vector3f0.z = -this.z;
        return vector3f0;
    }

    public Vector3f normalise(Vector3f vector3f1) {
        float float0 = this.length();
        if (vector3f1 == null) {
            vector3f1 = new Vector3f(this.x / float0, this.y / float0, this.z / float0);
        } else {
            vector3f1.set(this.x / float0, this.y / float0, this.z / float0);
        }

        return vector3f1;
    }

    public static float dot(Vector3f vector3f1, Vector3f vector3f0) {
        return vector3f1.x * vector3f0.x + vector3f1.y * vector3f0.y + vector3f1.z * vector3f0.z;
    }

    public static float angle(Vector3f vector3f1, Vector3f vector3f0) {
        float float0 = dot(vector3f1, vector3f0) / (vector3f1.length() * vector3f0.length());
        if (float0 < -1.0F) {
            float0 = -1.0F;
        } else if (float0 > 1.0F) {
            float0 = 1.0F;
        }

        return (float)Math.acos(float0);
    }

    @Override
    public Vector load(FloatBuffer floatBuffer) {
        this.x = floatBuffer.get();
        this.y = floatBuffer.get();
        this.z = floatBuffer.get();
        return this;
    }

    @Override
    public Vector scale(float float0) {
        this.x *= float0;
        this.y *= float0;
        this.z *= float0;
        return this;
    }

    @Override
    public Vector store(FloatBuffer floatBuffer) {
        floatBuffer.put(this.x);
        floatBuffer.put(this.y);
        floatBuffer.put(this.z);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(64);
        stringBuilder.append("Vector3f[");
        stringBuilder.append(this.x);
        stringBuilder.append(", ");
        stringBuilder.append(this.y);
        stringBuilder.append(", ");
        stringBuilder.append(this.z);
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

    @Override
    public void setZ(float float0) {
        this.z = float0;
    }

    @Override
    public float getZ() {
        return this.z;
    }
}
