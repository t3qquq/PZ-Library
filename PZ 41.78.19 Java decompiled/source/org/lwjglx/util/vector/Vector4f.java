// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.vector;

import java.io.Serializable;
import java.nio.FloatBuffer;

public class Vector4f extends Vector implements Serializable, ReadableVector4f, WritableVector4f {
    private static final long serialVersionUID = 1L;
    public float x;
    public float y;
    public float z;
    public float w;

    public Vector4f() {
    }

    public Vector4f(ReadableVector4f readableVector4f) {
        this.set(readableVector4f);
    }

    public Vector4f(float float0, float float1, float float2, float float3) {
        this.set(float0, float1, float2, float3);
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

    @Override
    public void set(float float0, float float1, float float2, float float3) {
        this.x = float0;
        this.y = float1;
        this.z = float2;
        this.w = float3;
    }

    public Vector4f set(ReadableVector4f readableVector4f) {
        this.x = readableVector4f.getX();
        this.y = readableVector4f.getY();
        this.z = readableVector4f.getZ();
        this.w = readableVector4f.getW();
        return this;
    }

    @Override
    public float lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
    }

    public Vector4f translate(float float0, float float1, float float2, float float3) {
        this.x += float0;
        this.y += float1;
        this.z += float2;
        this.w += float3;
        return this;
    }

    public static Vector4f add(Vector4f vector4f2, Vector4f vector4f1, Vector4f vector4f0) {
        if (vector4f0 == null) {
            return new Vector4f(vector4f2.x + vector4f1.x, vector4f2.y + vector4f1.y, vector4f2.z + vector4f1.z, vector4f2.w + vector4f1.w);
        } else {
            vector4f0.set(vector4f2.x + vector4f1.x, vector4f2.y + vector4f1.y, vector4f2.z + vector4f1.z, vector4f2.w + vector4f1.w);
            return vector4f0;
        }
    }

    public static Vector4f sub(Vector4f vector4f2, Vector4f vector4f1, Vector4f vector4f0) {
        if (vector4f0 == null) {
            return new Vector4f(vector4f2.x - vector4f1.x, vector4f2.y - vector4f1.y, vector4f2.z - vector4f1.z, vector4f2.w - vector4f1.w);
        } else {
            vector4f0.set(vector4f2.x - vector4f1.x, vector4f2.y - vector4f1.y, vector4f2.z - vector4f1.z, vector4f2.w - vector4f1.w);
            return vector4f0;
        }
    }

    @Override
    public Vector negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.w = -this.w;
        return this;
    }

    public Vector4f negate(Vector4f vector4f0) {
        if (vector4f0 == null) {
            vector4f0 = new Vector4f();
        }

        vector4f0.x = -this.x;
        vector4f0.y = -this.y;
        vector4f0.z = -this.z;
        vector4f0.w = -this.w;
        return vector4f0;
    }

    public Vector4f normalise(Vector4f vector4f1) {
        float float0 = this.length();
        if (vector4f1 == null) {
            vector4f1 = new Vector4f(this.x / float0, this.y / float0, this.z / float0, this.w / float0);
        } else {
            vector4f1.set(this.x / float0, this.y / float0, this.z / float0, this.w / float0);
        }

        return vector4f1;
    }

    public static float dot(Vector4f vector4f1, Vector4f vector4f0) {
        return vector4f1.x * vector4f0.x + vector4f1.y * vector4f0.y + vector4f1.z * vector4f0.z + vector4f1.w * vector4f0.w;
    }

    public static float angle(Vector4f vector4f1, Vector4f vector4f0) {
        float float0 = dot(vector4f1, vector4f0) / (vector4f1.length() * vector4f0.length());
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
        this.w = floatBuffer.get();
        return this;
    }

    @Override
    public Vector scale(float float0) {
        this.x *= float0;
        this.y *= float0;
        this.z *= float0;
        this.w *= float0;
        return this;
    }

    @Override
    public Vector store(FloatBuffer floatBuffer) {
        floatBuffer.put(this.x);
        floatBuffer.put(this.y);
        floatBuffer.put(this.z);
        floatBuffer.put(this.w);
        return this;
    }

    @Override
    public String toString() {
        return "Vector4f: " + this.x + " " + this.y + " " + this.z + " " + this.w;
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

    @Override
    public void setW(float float0) {
        this.w = float0;
    }

    @Override
    public float getW() {
        return this.w;
    }
}
