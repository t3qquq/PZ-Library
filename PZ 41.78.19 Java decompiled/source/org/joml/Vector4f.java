// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.NumberFormat;

public class Vector4f implements Externalizable, Vector4fc {
    private static final long serialVersionUID = 1L;
    public float x;
    public float y;
    public float z;
    public float w;

    public Vector4f() {
        this.w = 1.0F;
    }

    public Vector4f(Vector4fc arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg0.w();
    }

    public Vector4f(Vector4ic arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg0.w();
    }

    public Vector4f(Vector3fc arg0, float arg1) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg1;
    }

    public Vector4f(Vector3ic arg0, float arg1) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg1;
    }

    public Vector4f(Vector2fc arg0, float arg1, float arg2) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg1;
        this.w = arg2;
    }

    public Vector4f(Vector2ic arg0, float arg1, float arg2) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg1;
        this.w = arg2;
    }

    public Vector4f(float arg0) {
        this.x = arg0;
        this.y = arg0;
        this.z = arg0;
        this.w = arg0;
    }

    public Vector4f(float arg0, float arg1, float arg2, float arg3) {
        this.x = arg0;
        this.y = arg1;
        this.z = arg2;
        this.w = arg3;
    }

    public Vector4f(float[] arg0) {
        this.x = arg0[0];
        this.y = arg0[1];
        this.z = arg0[2];
        this.w = arg0[3];
    }

    public Vector4f(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
    }

    public Vector4f(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
    }

    public Vector4f(FloatBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
    }

    public Vector4f(int arg0, FloatBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
    }

    @Override
    public float x() {
        return this.x;
    }

    @Override
    public float y() {
        return this.y;
    }

    @Override
    public float z() {
        return this.z;
    }

    @Override
    public float w() {
        return this.w;
    }

    public Vector4f set(Vector4fc arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg0.w();
        return this;
    }

    public Vector4f set(Vector4ic arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg0.w();
        return this;
    }

    public Vector4f set(Vector4dc arg0) {
        this.x = (float)arg0.x();
        this.y = (float)arg0.y();
        this.z = (float)arg0.z();
        this.w = (float)arg0.w();
        return this;
    }

    public Vector4f set(Vector3fc arg0, float arg1) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg1;
        return this;
    }

    public Vector4f set(Vector3ic arg0, float arg1) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg1;
        return this;
    }

    public Vector4f set(Vector2fc arg0, float arg1, float arg2) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg1;
        this.w = arg2;
        return this;
    }

    public Vector4f set(Vector2ic arg0, float arg1, float arg2) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg1;
        this.w = arg2;
        return this;
    }

    public Vector4f set(float arg0) {
        this.x = arg0;
        this.y = arg0;
        this.z = arg0;
        this.w = arg0;
        return this;
    }

    public Vector4f set(float arg0, float arg1, float arg2, float arg3) {
        this.x = arg0;
        this.y = arg1;
        this.z = arg2;
        this.w = arg3;
        return this;
    }

    public Vector4f set(float arg0, float arg1, float arg2) {
        this.x = arg0;
        this.y = arg1;
        this.z = arg2;
        return this;
    }

    public Vector4f set(double arg0) {
        this.x = (float)arg0;
        this.y = (float)arg0;
        this.z = (float)arg0;
        this.w = (float)arg0;
        return this;
    }

    public Vector4f set(double arg0, double arg1, double arg2, double arg3) {
        this.x = (float)arg0;
        this.y = (float)arg1;
        this.z = (float)arg2;
        this.w = (float)arg3;
        return this;
    }

    public Vector4f set(float[] arg0) {
        this.x = arg0[0];
        this.y = arg0[1];
        this.z = arg0[2];
        this.w = arg0[2];
        return this;
    }

    public Vector4f set(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Vector4f set(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
        return this;
    }

    public Vector4f set(FloatBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Vector4f set(int arg0, FloatBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
        return this;
    }

    public Vector4f setFromAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.get(this, arg0);
            return this;
        }
    }

    public Vector4f setComponent(int arg0, float arg1) throws IllegalArgumentException {
        switch (arg0) {
            case 0:
                this.x = arg1;
                break;
            case 1:
                this.y = arg1;
                break;
            case 2:
                this.z = arg1;
                break;
            case 3:
                this.w = arg1;
                break;
            default:
                throw new IllegalArgumentException();
        }

        return this;
    }

    @Override
    public FloatBuffer get(FloatBuffer arg0) {
        MemUtil.INSTANCE.put(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public FloatBuffer get(int arg0, FloatBuffer arg1) {
        MemUtil.INSTANCE.put(this, arg0, arg1);
        return arg1;
    }

    @Override
    public ByteBuffer get(ByteBuffer arg0) {
        MemUtil.INSTANCE.put(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public ByteBuffer get(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.put(this, arg0, arg1);
        return arg1;
    }

    @Override
    public Vector4fc getToAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.put(this, arg0);
            return this;
        }
    }

    public Vector4f sub(Vector4fc arg0) {
        this.x = this.x - arg0.x();
        this.y = this.y - arg0.y();
        this.z = this.z - arg0.z();
        this.w = this.w - arg0.w();
        return this;
    }

    public Vector4f sub(float arg0, float arg1, float arg2, float arg3) {
        this.x -= arg0;
        this.y -= arg1;
        this.z -= arg2;
        this.w -= arg3;
        return this;
    }

    @Override
    public Vector4f sub(Vector4fc arg0, Vector4f arg1) {
        arg1.x = this.x - arg0.x();
        arg1.y = this.y - arg0.y();
        arg1.z = this.z - arg0.z();
        arg1.w = this.w - arg0.w();
        return arg1;
    }

    @Override
    public Vector4f sub(float arg0, float arg1, float arg2, float arg3, Vector4f arg4) {
        arg4.x = this.x - arg0;
        arg4.y = this.y - arg1;
        arg4.z = this.z - arg2;
        arg4.w = this.w - arg3;
        return arg4;
    }

    public Vector4f add(Vector4fc arg0) {
        this.x = this.x + arg0.x();
        this.y = this.y + arg0.y();
        this.z = this.z + arg0.z();
        this.w = this.w + arg0.w();
        return this;
    }

    @Override
    public Vector4f add(Vector4fc arg0, Vector4f arg1) {
        arg1.x = this.x + arg0.x();
        arg1.y = this.y + arg0.y();
        arg1.z = this.z + arg0.z();
        arg1.w = this.w + arg0.w();
        return arg1;
    }

    public Vector4f add(float arg0, float arg1, float arg2, float arg3) {
        this.x += arg0;
        this.y += arg1;
        this.z += arg2;
        this.w += arg3;
        return this;
    }

    @Override
    public Vector4f add(float arg0, float arg1, float arg2, float arg3, Vector4f arg4) {
        arg4.x = this.x + arg0;
        arg4.y = this.y + arg1;
        arg4.z = this.z + arg2;
        arg4.w = this.w + arg3;
        return arg4;
    }

    public Vector4f fma(Vector4fc arg0, Vector4fc arg1) {
        this.x = Math.fma(arg0.x(), arg1.x(), this.x);
        this.y = Math.fma(arg0.y(), arg1.y(), this.y);
        this.z = Math.fma(arg0.z(), arg1.z(), this.z);
        this.w = Math.fma(arg0.w(), arg1.w(), this.w);
        return this;
    }

    public Vector4f fma(float arg0, Vector4fc arg1) {
        this.x = Math.fma(arg0, arg1.x(), this.x);
        this.y = Math.fma(arg0, arg1.y(), this.y);
        this.z = Math.fma(arg0, arg1.z(), this.z);
        this.w = Math.fma(arg0, arg1.w(), this.w);
        return this;
    }

    @Override
    public Vector4f fma(Vector4fc arg0, Vector4fc arg1, Vector4f arg2) {
        arg2.x = Math.fma(arg0.x(), arg1.x(), this.x);
        arg2.y = Math.fma(arg0.y(), arg1.y(), this.y);
        arg2.z = Math.fma(arg0.z(), arg1.z(), this.z);
        arg2.w = Math.fma(arg0.w(), arg1.w(), this.w);
        return arg2;
    }

    @Override
    public Vector4f fma(float arg0, Vector4fc arg1, Vector4f arg2) {
        arg2.x = Math.fma(arg0, arg1.x(), this.x);
        arg2.y = Math.fma(arg0, arg1.y(), this.y);
        arg2.z = Math.fma(arg0, arg1.z(), this.z);
        arg2.w = Math.fma(arg0, arg1.w(), this.w);
        return arg2;
    }

    public Vector4f mulAdd(Vector4fc arg0, Vector4fc arg1) {
        this.x = Math.fma(this.x, arg0.x(), arg1.x());
        this.y = Math.fma(this.y, arg0.y(), arg1.y());
        this.z = Math.fma(this.z, arg0.z(), arg1.z());
        return this;
    }

    public Vector4f mulAdd(float arg0, Vector4fc arg1) {
        this.x = Math.fma(this.x, arg0, arg1.x());
        this.y = Math.fma(this.y, arg0, arg1.y());
        this.z = Math.fma(this.z, arg0, arg1.z());
        return this;
    }

    @Override
    public Vector4f mulAdd(Vector4fc arg0, Vector4fc arg1, Vector4f arg2) {
        arg2.x = Math.fma(this.x, arg0.x(), arg1.x());
        arg2.y = Math.fma(this.y, arg0.y(), arg1.y());
        arg2.z = Math.fma(this.z, arg0.z(), arg1.z());
        return arg2;
    }

    @Override
    public Vector4f mulAdd(float arg0, Vector4fc arg1, Vector4f arg2) {
        arg2.x = Math.fma(this.x, arg0, arg1.x());
        arg2.y = Math.fma(this.y, arg0, arg1.y());
        arg2.z = Math.fma(this.z, arg0, arg1.z());
        return arg2;
    }

    public Vector4f mul(Vector4fc arg0) {
        this.x = this.x * arg0.x();
        this.y = this.y * arg0.y();
        this.z = this.z * arg0.z();
        this.w = this.w * arg0.w();
        return this;
    }

    @Override
    public Vector4f mul(Vector4fc arg0, Vector4f arg1) {
        arg1.x = this.x * arg0.x();
        arg1.y = this.y * arg0.y();
        arg1.z = this.z * arg0.z();
        arg1.w = this.w * arg0.w();
        return arg1;
    }

    public Vector4f div(Vector4fc arg0) {
        this.x = this.x / arg0.x();
        this.y = this.y / arg0.y();
        this.z = this.z / arg0.z();
        this.w = this.w / arg0.w();
        return this;
    }

    @Override
    public Vector4f div(Vector4fc arg0, Vector4f arg1) {
        arg1.x = this.x / arg0.x();
        arg1.y = this.y / arg0.y();
        arg1.z = this.z / arg0.z();
        arg1.w = this.w / arg0.w();
        return arg1;
    }

    public Vector4f mul(Matrix4fc arg0) {
        return (arg0.properties() & 2) != 0 ? this.mulAffine(arg0, this) : this.mulGeneric(arg0, this);
    }

    @Override
    public Vector4f mul(Matrix4fc arg0, Vector4f arg1) {
        return (arg0.properties() & 2) != 0 ? this.mulAffine(arg0, arg1) : this.mulGeneric(arg0, arg1);
    }

    public Vector4f mulTranspose(Matrix4fc arg0) {
        return (arg0.properties() & 2) != 0 ? this.mulAffineTranspose(arg0, this) : this.mulGenericTranspose(arg0, this);
    }

    @Override
    public Vector4f mulTranspose(Matrix4fc arg0, Vector4f arg1) {
        return (arg0.properties() & 2) != 0 ? this.mulAffineTranspose(arg0, arg1) : this.mulGenericTranspose(arg0, arg1);
    }

    @Override
    public Vector4f mulAffine(Matrix4fc arg0, Vector4f arg1) {
        float float0 = this.x;
        float float1 = this.y;
        float float2 = this.z;
        float float3 = this.w;
        arg1.x = Math.fma(arg0.m00(), float0, Math.fma(arg0.m10(), float1, Math.fma(arg0.m20(), float2, arg0.m30() * float3)));
        arg1.y = Math.fma(arg0.m01(), float0, Math.fma(arg0.m11(), float1, Math.fma(arg0.m21(), float2, arg0.m31() * float3)));
        arg1.z = Math.fma(arg0.m02(), float0, Math.fma(arg0.m12(), float1, Math.fma(arg0.m22(), float2, arg0.m32() * float3)));
        arg1.w = float3;
        return arg1;
    }

    private Vector4f mulGeneric(Matrix4fc matrix4fc, Vector4f vector4f1) {
        float float0 = this.x;
        float float1 = this.y;
        float float2 = this.z;
        float float3 = this.w;
        vector4f1.x = Math.fma(matrix4fc.m00(), float0, Math.fma(matrix4fc.m10(), float1, Math.fma(matrix4fc.m20(), float2, matrix4fc.m30() * float3)));
        vector4f1.y = Math.fma(matrix4fc.m01(), float0, Math.fma(matrix4fc.m11(), float1, Math.fma(matrix4fc.m21(), float2, matrix4fc.m31() * float3)));
        vector4f1.z = Math.fma(matrix4fc.m02(), float0, Math.fma(matrix4fc.m12(), float1, Math.fma(matrix4fc.m22(), float2, matrix4fc.m32() * float3)));
        vector4f1.w = Math.fma(matrix4fc.m03(), float0, Math.fma(matrix4fc.m13(), float1, Math.fma(matrix4fc.m23(), float2, matrix4fc.m33() * float3)));
        return vector4f1;
    }

    @Override
    public Vector4f mulAffineTranspose(Matrix4fc arg0, Vector4f arg1) {
        float float0 = this.x;
        float float1 = this.y;
        float float2 = this.z;
        float float3 = this.w;
        arg1.x = Math.fma(arg0.m00(), float0, Math.fma(arg0.m01(), float1, arg0.m02() * float2));
        arg1.y = Math.fma(arg0.m10(), float0, Math.fma(arg0.m11(), float1, arg0.m12() * float2));
        arg1.z = Math.fma(arg0.m20(), float0, Math.fma(arg0.m21(), float1, arg0.m22() * float2));
        arg1.w = Math.fma(arg0.m30(), float0, Math.fma(arg0.m31(), float1, arg0.m32() * float2 + float3));
        return arg1;
    }

    private Vector4f mulGenericTranspose(Matrix4fc matrix4fc, Vector4f vector4f1) {
        float float0 = this.x;
        float float1 = this.y;
        float float2 = this.z;
        float float3 = this.w;
        vector4f1.x = Math.fma(matrix4fc.m00(), float0, Math.fma(matrix4fc.m01(), float1, Math.fma(matrix4fc.m02(), float2, matrix4fc.m03() * float3)));
        vector4f1.y = Math.fma(matrix4fc.m10(), float0, Math.fma(matrix4fc.m11(), float1, Math.fma(matrix4fc.m12(), float2, matrix4fc.m13() * float3)));
        vector4f1.z = Math.fma(matrix4fc.m20(), float0, Math.fma(matrix4fc.m21(), float1, Math.fma(matrix4fc.m22(), float2, matrix4fc.m23() * float3)));
        vector4f1.w = Math.fma(matrix4fc.m30(), float0, Math.fma(matrix4fc.m31(), float1, Math.fma(matrix4fc.m32(), float2, matrix4fc.m33() * float3)));
        return vector4f1;
    }

    public Vector4f mul(Matrix4x3fc arg0) {
        float float0 = this.x;
        float float1 = this.y;
        float float2 = this.z;
        float float3 = this.w;
        this.x = Math.fma(arg0.m00(), float0, Math.fma(arg0.m10(), float1, Math.fma(arg0.m20(), float2, arg0.m30() * float3)));
        this.y = Math.fma(arg0.m01(), float0, Math.fma(arg0.m11(), float1, Math.fma(arg0.m21(), float2, arg0.m31() * float3)));
        this.z = Math.fma(arg0.m02(), float0, Math.fma(arg0.m12(), float1, Math.fma(arg0.m22(), float2, arg0.m32() * float3)));
        this.w = float3;
        return this;
    }

    @Override
    public Vector4f mul(Matrix4x3fc arg0, Vector4f arg1) {
        float float0 = this.x;
        float float1 = this.y;
        float float2 = this.z;
        float float3 = this.w;
        arg1.x = Math.fma(arg0.m00(), float0, Math.fma(arg0.m10(), float1, Math.fma(arg0.m20(), float2, arg0.m30() * float3)));
        arg1.y = Math.fma(arg0.m01(), float0, Math.fma(arg0.m11(), float1, Math.fma(arg0.m21(), float2, arg0.m31() * float3)));
        arg1.z = Math.fma(arg0.m02(), float0, Math.fma(arg0.m12(), float1, Math.fma(arg0.m22(), float2, arg0.m32() * float3)));
        arg1.w = float3;
        return arg1;
    }

    @Override
    public Vector4f mulProject(Matrix4fc arg0, Vector4f arg1) {
        float float0 = this.x;
        float float1 = this.y;
        float float2 = this.z;
        float float3 = this.w;
        float float4 = 1.0F / Math.fma(arg0.m03(), float0, Math.fma(arg0.m13(), float1, Math.fma(arg0.m23(), float2, arg0.m33() * float3)));
        arg1.x = Math.fma(arg0.m00(), float0, Math.fma(arg0.m10(), float1, Math.fma(arg0.m20(), float2, arg0.m30() * float3))) * float4;
        arg1.y = Math.fma(arg0.m01(), float0, Math.fma(arg0.m11(), float1, Math.fma(arg0.m21(), float2, arg0.m31() * float3))) * float4;
        arg1.z = Math.fma(arg0.m02(), float0, Math.fma(arg0.m12(), float1, Math.fma(arg0.m22(), float2, arg0.m32() * float3))) * float4;
        arg1.w = 1.0F;
        return arg1;
    }

    public Vector4f mulProject(Matrix4fc arg0) {
        float float0 = this.x;
        float float1 = this.y;
        float float2 = this.z;
        float float3 = this.w;
        float float4 = 1.0F / Math.fma(arg0.m03(), float0, Math.fma(arg0.m13(), float1, Math.fma(arg0.m23(), float2, arg0.m33() * float3)));
        this.x = Math.fma(arg0.m00(), float0, Math.fma(arg0.m10(), float1, Math.fma(arg0.m20(), float2, arg0.m30() * float3))) * float4;
        this.y = Math.fma(arg0.m01(), float0, Math.fma(arg0.m11(), float1, Math.fma(arg0.m21(), float2, arg0.m31() * float3))) * float4;
        this.z = Math.fma(arg0.m02(), float0, Math.fma(arg0.m12(), float1, Math.fma(arg0.m22(), float2, arg0.m32() * float3))) * float4;
        this.w = 1.0F;
        return this;
    }

    @Override
    public Vector3f mulProject(Matrix4fc arg0, Vector3f arg1) {
        float float0 = this.x;
        float float1 = this.y;
        float float2 = this.z;
        float float3 = this.w;
        float float4 = 1.0F / Math.fma(arg0.m03(), float0, Math.fma(arg0.m13(), float1, Math.fma(arg0.m23(), float2, arg0.m33() * float3)));
        arg1.x = Math.fma(arg0.m00(), float0, Math.fma(arg0.m10(), float1, Math.fma(arg0.m20(), float2, arg0.m30() * float3))) * float4;
        arg1.y = Math.fma(arg0.m01(), float0, Math.fma(arg0.m11(), float1, Math.fma(arg0.m21(), float2, arg0.m31() * float3))) * float4;
        arg1.z = Math.fma(arg0.m02(), float0, Math.fma(arg0.m12(), float1, Math.fma(arg0.m22(), float2, arg0.m32() * float3))) * float4;
        return arg1;
    }

    public Vector4f mul(float arg0) {
        this.x *= arg0;
        this.y *= arg0;
        this.z *= arg0;
        this.w *= arg0;
        return this;
    }

    @Override
    public Vector4f mul(float arg0, Vector4f arg1) {
        arg1.x = this.x * arg0;
        arg1.y = this.y * arg0;
        arg1.z = this.z * arg0;
        arg1.w = this.w * arg0;
        return arg1;
    }

    public Vector4f mul(float arg0, float arg1, float arg2, float arg3) {
        this.x *= arg0;
        this.y *= arg1;
        this.z *= arg2;
        this.w *= arg3;
        return this;
    }

    @Override
    public Vector4f mul(float arg0, float arg1, float arg2, float arg3, Vector4f arg4) {
        arg4.x = this.x * arg0;
        arg4.y = this.y * arg1;
        arg4.z = this.z * arg2;
        arg4.w = this.w * arg3;
        return arg4;
    }

    public Vector4f div(float arg0) {
        float float0 = 1.0F / arg0;
        this.x *= float0;
        this.y *= float0;
        this.z *= float0;
        this.w *= float0;
        return this;
    }

    @Override
    public Vector4f div(float arg0, Vector4f arg1) {
        float float0 = 1.0F / arg0;
        arg1.x = this.x * float0;
        arg1.y = this.y * float0;
        arg1.z = this.z * float0;
        arg1.w = this.w * float0;
        return arg1;
    }

    public Vector4f div(float arg0, float arg1, float arg2, float arg3) {
        this.x /= arg0;
        this.y /= arg1;
        this.z /= arg2;
        this.w /= arg3;
        return this;
    }

    @Override
    public Vector4f div(float arg0, float arg1, float arg2, float arg3, Vector4f arg4) {
        arg4.x = this.x / arg0;
        arg4.y = this.y / arg1;
        arg4.z = this.z / arg2;
        arg4.w = this.w / arg3;
        return arg4;
    }

    public Vector4f rotate(Quaternionfc arg0) {
        return arg0.transform(this, this);
    }

    @Override
    public Vector4f rotate(Quaternionfc arg0, Vector4f arg1) {
        return arg0.transform(this, arg1);
    }

    public Vector4f rotateAbout(float arg0, float arg1, float arg2, float arg3) {
        if (arg2 == 0.0F && arg3 == 0.0F && Math.absEqualsOne(arg1)) {
            return this.rotateX(arg1 * arg0, this);
        } else if (arg1 == 0.0F && arg3 == 0.0F && Math.absEqualsOne(arg2)) {
            return this.rotateY(arg2 * arg0, this);
        } else {
            return arg1 == 0.0F && arg2 == 0.0F && Math.absEqualsOne(arg3)
                ? this.rotateZ(arg3 * arg0, this)
                : this.rotateAxisInternal(arg0, arg1, arg2, arg3, this);
        }
    }

    @Override
    public Vector4f rotateAxis(float arg0, float arg1, float arg2, float arg3, Vector4f arg4) {
        if (arg2 == 0.0F && arg3 == 0.0F && Math.absEqualsOne(arg1)) {
            return this.rotateX(arg1 * arg0, arg4);
        } else if (arg1 == 0.0F && arg3 == 0.0F && Math.absEqualsOne(arg2)) {
            return this.rotateY(arg2 * arg0, arg4);
        } else {
            return arg1 == 0.0F && arg2 == 0.0F && Math.absEqualsOne(arg3)
                ? this.rotateZ(arg3 * arg0, arg4)
                : this.rotateAxisInternal(arg0, arg1, arg2, arg3, arg4);
        }
    }

    private Vector4f rotateAxisInternal(float float1, float float4, float float6, float float8, Vector4f vector4f1) {
        float float0 = float1 * 0.5F;
        float float2 = Math.sin(float0);
        float float3 = float4 * float2;
        float float5 = float6 * float2;
        float float7 = float8 * float2;
        float float9 = Math.cosFromSin(float2, float0);
        float float10 = float9 * float9;
        float float11 = float3 * float3;
        float float12 = float5 * float5;
        float float13 = float7 * float7;
        float float14 = float7 * float9;
        float float15 = float3 * float5;
        float float16 = float3 * float7;
        float float17 = float5 * float9;
        float float18 = float5 * float7;
        float float19 = float3 * float9;
        float float20 = this.x;
        float float21 = this.y;
        float float22 = this.z;
        vector4f1.x = (float10 + float11 - float13 - float12) * float20
            + (-float14 + float15 - float14 + float15) * float21
            + (float17 + float16 + float16 + float17) * float22;
        vector4f1.y = (float15 + float14 + float14 + float15) * float20
            + (float12 - float13 + float10 - float11) * float21
            + (float18 + float18 - float19 - float19) * float22;
        vector4f1.z = (float16 - float17 + float16 - float17) * float20
            + (float18 + float18 + float19 + float19) * float21
            + (float13 - float12 - float11 + float10) * float22;
        return vector4f1;
    }

    public Vector4f rotateX(float arg0) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = this.y * float1 - this.z * float0;
        float float3 = this.y * float0 + this.z * float1;
        this.y = float2;
        this.z = float3;
        return this;
    }

    @Override
    public Vector4f rotateX(float arg0, Vector4f arg1) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = this.y * float1 - this.z * float0;
        float float3 = this.y * float0 + this.z * float1;
        arg1.x = this.x;
        arg1.y = float2;
        arg1.z = float3;
        arg1.w = this.w;
        return arg1;
    }

    public Vector4f rotateY(float arg0) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = this.x * float1 + this.z * float0;
        float float3 = -this.x * float0 + this.z * float1;
        this.x = float2;
        this.z = float3;
        return this;
    }

    @Override
    public Vector4f rotateY(float arg0, Vector4f arg1) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = this.x * float1 + this.z * float0;
        float float3 = -this.x * float0 + this.z * float1;
        arg1.x = float2;
        arg1.y = this.y;
        arg1.z = float3;
        arg1.w = this.w;
        return arg1;
    }

    public Vector4f rotateZ(float arg0) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = this.x * float1 - this.y * float0;
        float float3 = this.x * float0 + this.y * float1;
        this.x = float2;
        this.y = float3;
        return this;
    }

    @Override
    public Vector4f rotateZ(float arg0, Vector4f arg1) {
        float float0 = Math.sin(arg0);
        float float1 = Math.cosFromSin(float0, arg0);
        float float2 = this.x * float1 - this.y * float0;
        float float3 = this.x * float0 + this.y * float1;
        arg1.x = float2;
        arg1.y = float3;
        arg1.z = this.z;
        arg1.w = this.w;
        return arg1;
    }

    @Override
    public float lengthSquared() {
        return Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
    }

    public static float lengthSquared(float arg0, float arg1, float arg2, float arg3) {
        return Math.fma(arg0, arg0, Math.fma(arg1, arg1, Math.fma(arg2, arg2, arg3 * arg3)));
    }

    public static float lengthSquared(int arg0, int arg1, int arg2, int arg3) {
        return Math.fma((float)arg0, (float)arg0, Math.fma((float)arg1, (float)arg1, Math.fma((float)arg2, (float)arg2, (float)(arg3 * arg3))));
    }

    @Override
    public float length() {
        return Math.sqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w))));
    }

    public static float length(float arg0, float arg1, float arg2, float arg3) {
        return Math.sqrt(Math.fma(arg0, arg0, Math.fma(arg1, arg1, Math.fma(arg2, arg2, arg3 * arg3))));
    }

    public Vector4f normalize() {
        float float0 = 1.0F / this.length();
        this.x *= float0;
        this.y *= float0;
        this.z *= float0;
        this.w *= float0;
        return this;
    }

    @Override
    public Vector4f normalize(Vector4f arg0) {
        float float0 = 1.0F / this.length();
        arg0.x = this.x * float0;
        arg0.y = this.y * float0;
        arg0.z = this.z * float0;
        arg0.w = this.w * float0;
        return arg0;
    }

    public Vector4f normalize(float arg0) {
        float float0 = 1.0F / this.length() * arg0;
        this.x *= float0;
        this.y *= float0;
        this.z *= float0;
        this.w *= float0;
        return this;
    }

    @Override
    public Vector4f normalize(float arg0, Vector4f arg1) {
        float float0 = 1.0F / this.length() * arg0;
        arg1.x = this.x * float0;
        arg1.y = this.y * float0;
        arg1.z = this.z * float0;
        arg1.w = this.w * float0;
        return arg1;
    }

    public Vector4f normalize3() {
        float float0 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
        this.x *= float0;
        this.y *= float0;
        this.z *= float0;
        this.w *= float0;
        return this;
    }

    @Override
    public Vector4f normalize3(Vector4f arg0) {
        float float0 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
        arg0.x = this.x * float0;
        arg0.y = this.y * float0;
        arg0.z = this.z * float0;
        arg0.w = this.w * float0;
        return arg0;
    }

    @Override
    public float distance(Vector4fc arg0) {
        float float0 = this.x - arg0.x();
        float float1 = this.y - arg0.y();
        float float2 = this.z - arg0.z();
        float float3 = this.w - arg0.w();
        return Math.sqrt(Math.fma(float0, float0, Math.fma(float1, float1, Math.fma(float2, float2, float3 * float3))));
    }

    @Override
    public float distance(float arg0, float arg1, float arg2, float arg3) {
        float float0 = this.x - arg0;
        float float1 = this.y - arg1;
        float float2 = this.z - arg2;
        float float3 = this.w - arg3;
        return Math.sqrt(Math.fma(float0, float0, Math.fma(float1, float1, Math.fma(float2, float2, float3 * float3))));
    }

    @Override
    public float distanceSquared(Vector4fc arg0) {
        float float0 = this.x - arg0.x();
        float float1 = this.y - arg0.y();
        float float2 = this.z - arg0.z();
        float float3 = this.w - arg0.w();
        return Math.fma(float0, float0, Math.fma(float1, float1, Math.fma(float2, float2, float3 * float3)));
    }

    @Override
    public float distanceSquared(float arg0, float arg1, float arg2, float arg3) {
        float float0 = this.x - arg0;
        float float1 = this.y - arg1;
        float float2 = this.z - arg2;
        float float3 = this.w - arg3;
        return Math.fma(float0, float0, Math.fma(float1, float1, Math.fma(float2, float2, float3 * float3)));
    }

    public static float distance(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7) {
        float float0 = arg0 - arg4;
        float float1 = arg1 - arg5;
        float float2 = arg2 - arg6;
        float float3 = arg3 - arg7;
        return Math.sqrt(Math.fma(float0, float0, Math.fma(float1, float1, Math.fma(float2, float2, float3 * float3))));
    }

    public static float distanceSquared(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7) {
        float float0 = arg0 - arg4;
        float float1 = arg1 - arg5;
        float float2 = arg2 - arg6;
        float float3 = arg3 - arg7;
        return Math.fma(float0, float0, Math.fma(float1, float1, Math.fma(float2, float2, float3 * float3)));
    }

    @Override
    public float dot(Vector4fc arg0) {
        return Math.fma(this.x, arg0.x(), Math.fma(this.y, arg0.y(), Math.fma(this.z, arg0.z(), this.w * arg0.w())));
    }

    @Override
    public float dot(float arg0, float arg1, float arg2, float arg3) {
        return Math.fma(this.x, arg0, Math.fma(this.y, arg1, Math.fma(this.z, arg2, this.w * arg3)));
    }

    @Override
    public float angleCos(Vector4fc arg0) {
        float float0 = this.x;
        float float1 = this.y;
        float float2 = this.z;
        float float3 = this.w;
        float float4 = Math.fma(float0, float0, Math.fma(float1, float1, Math.fma(float2, float2, float3 * float3)));
        float float5 = Math.fma(arg0.x(), arg0.x(), Math.fma(arg0.y(), arg0.y(), Math.fma(arg0.z(), arg0.z(), arg0.w() * arg0.w())));
        float float6 = Math.fma(float0, arg0.x(), Math.fma(float1, arg0.y(), Math.fma(float2, arg0.z(), float3 * arg0.w())));
        return float6 / Math.sqrt(float4 * float5);
    }

    @Override
    public float angle(Vector4fc arg0) {
        float float0 = this.angleCos(arg0);
        float0 = float0 < 1.0F ? float0 : 1.0F;
        float0 = float0 > -1.0F ? float0 : -1.0F;
        return Math.acos(float0);
    }

    public Vector4f zero() {
        this.x = 0.0F;
        this.y = 0.0F;
        this.z = 0.0F;
        this.w = 0.0F;
        return this;
    }

    public Vector4f negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.w = -this.w;
        return this;
    }

    @Override
    public Vector4f negate(Vector4f arg0) {
        arg0.x = -this.x;
        arg0.y = -this.y;
        arg0.z = -this.z;
        arg0.w = -this.w;
        return arg0;
    }

    @Override
    public String toString() {
        return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
    }

    public String toString(NumberFormat numberFormat) {
        return "("
            + Runtime.format(this.x, numberFormat)
            + " "
            + Runtime.format(this.y, numberFormat)
            + " "
            + Runtime.format(this.z, numberFormat)
            + " "
            + Runtime.format(this.w, numberFormat)
            + ")";
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        arg0.writeFloat(this.x);
        arg0.writeFloat(this.y);
        arg0.writeFloat(this.z);
        arg0.writeFloat(this.w);
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        this.set(arg0.readFloat(), arg0.readFloat(), arg0.readFloat(), arg0.readFloat());
    }

    public Vector4f min(Vector4fc arg0) {
        float float0 = this.x;
        float float1 = this.y;
        float float2 = this.z;
        float float3 = this.w;
        this.x = float0 < arg0.x() ? float0 : arg0.x();
        this.y = float1 < arg0.y() ? float1 : arg0.y();
        this.z = float2 < arg0.z() ? float2 : arg0.z();
        this.w = float3 < arg0.w() ? float3 : arg0.w();
        return this;
    }

    @Override
    public Vector4f min(Vector4fc arg0, Vector4f arg1) {
        float float0 = this.x;
        float float1 = this.y;
        float float2 = this.z;
        float float3 = this.w;
        arg1.x = float0 < arg0.x() ? float0 : arg0.x();
        arg1.y = float1 < arg0.y() ? float1 : arg0.y();
        arg1.z = float2 < arg0.z() ? float2 : arg0.z();
        arg1.w = float3 < arg0.w() ? float3 : arg0.w();
        return arg1;
    }

    public Vector4f max(Vector4fc arg0) {
        float float0 = this.x;
        float float1 = this.y;
        float float2 = this.z;
        float float3 = this.w;
        this.x = float0 > arg0.x() ? float0 : arg0.x();
        this.y = float1 > arg0.y() ? float1 : arg0.y();
        this.z = float2 > arg0.z() ? float2 : arg0.z();
        this.w = float3 > arg0.w() ? float3 : arg0.w();
        return this;
    }

    @Override
    public Vector4f max(Vector4fc arg0, Vector4f arg1) {
        float float0 = this.x;
        float float1 = this.y;
        float float2 = this.z;
        float float3 = this.w;
        arg1.x = float0 > arg0.x() ? float0 : arg0.x();
        arg1.y = float1 > arg0.y() ? float1 : arg0.y();
        arg1.z = float2 > arg0.z() ? float2 : arg0.z();
        arg1.w = float3 > arg0.w() ? float3 : arg0.w();
        return arg1;
    }

    @Override
    public int hashCode() {
        int int0 = 1;
        int0 = 31 * int0 + Float.floatToIntBits(this.w);
        int0 = 31 * int0 + Float.floatToIntBits(this.x);
        int0 = 31 * int0 + Float.floatToIntBits(this.y);
        return 31 * int0 + Float.floatToIntBits(this.z);
    }

    @Override
    public boolean equals(Object arg0) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (this.getClass() != arg0.getClass()) {
            return false;
        } else {
            Vector4f vector4f = (Vector4f)arg0;
            if (Float.floatToIntBits(this.w) != Float.floatToIntBits(vector4f.w)) {
                return false;
            } else if (Float.floatToIntBits(this.x) != Float.floatToIntBits(vector4f.x)) {
                return false;
            } else {
                return Float.floatToIntBits(this.y) != Float.floatToIntBits(vector4f.y)
                    ? false
                    : Float.floatToIntBits(this.z) == Float.floatToIntBits(vector4f.z);
            }
        }
    }

    @Override
    public boolean equals(Vector4fc arg0, float arg1) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (!(arg0 instanceof Vector4fc)) {
            return false;
        } else if (!Runtime.equals(this.x, arg0.x(), arg1)) {
            return false;
        } else if (!Runtime.equals(this.y, arg0.y(), arg1)) {
            return false;
        } else {
            return !Runtime.equals(this.z, arg0.z(), arg1) ? false : Runtime.equals(this.w, arg0.w(), arg1);
        }
    }

    @Override
    public boolean equals(float arg0, float arg1, float arg2, float arg3) {
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(arg0)) {
            return false;
        } else if (Float.floatToIntBits(this.y) != Float.floatToIntBits(arg1)) {
            return false;
        } else {
            return Float.floatToIntBits(this.z) != Float.floatToIntBits(arg2) ? false : Float.floatToIntBits(this.w) == Float.floatToIntBits(arg3);
        }
    }

    @Override
    public Vector4f smoothStep(Vector4fc arg0, float arg1, Vector4f arg2) {
        float float0 = arg1 * arg1;
        float float1 = float0 * arg1;
        float float2 = this.x;
        float float3 = this.y;
        float float4 = this.z;
        float float5 = this.w;
        arg2.x = (float2 + float2 - arg0.x() - arg0.x()) * float1 + (3.0F * arg0.x() - 3.0F * float2) * float0 + float2 * arg1 + float2;
        arg2.y = (float3 + float3 - arg0.y() - arg0.y()) * float1 + (3.0F * arg0.y() - 3.0F * float3) * float0 + float3 * arg1 + float3;
        arg2.z = (float4 + float4 - arg0.z() - arg0.z()) * float1 + (3.0F * arg0.z() - 3.0F * float4) * float0 + float4 * arg1 + float4;
        arg2.w = (float5 + float5 - arg0.w() - arg0.w()) * float1 + (3.0F * arg0.w() - 3.0F * float5) * float0 + float5 * arg1 + float5;
        return arg2;
    }

    @Override
    public Vector4f hermite(Vector4fc arg0, Vector4fc arg1, Vector4fc arg2, float arg3, Vector4f arg4) {
        float float0 = arg3 * arg3;
        float float1 = float0 * arg3;
        float float2 = this.x;
        float float3 = this.y;
        float float4 = this.z;
        float float5 = this.w;
        arg4.x = (float2 + float2 - arg1.x() - arg1.x() + arg2.x() + arg0.x()) * float1
            + (3.0F * arg1.x() - 3.0F * float2 - arg0.x() - arg0.x() - arg2.x()) * float0
            + float2 * arg3
            + float2;
        arg4.y = (float3 + float3 - arg1.y() - arg1.y() + arg2.y() + arg0.y()) * float1
            + (3.0F * arg1.y() - 3.0F * float3 - arg0.y() - arg0.y() - arg2.y()) * float0
            + float3 * arg3
            + float3;
        arg4.z = (float4 + float4 - arg1.z() - arg1.z() + arg2.z() + arg0.z()) * float1
            + (3.0F * arg1.z() - 3.0F * float4 - arg0.z() - arg0.z() - arg2.z()) * float0
            + float4 * arg3
            + float4;
        arg4.w = (float5 + float5 - arg1.w() - arg1.w() + arg2.w() + arg0.w()) * float1
            + (3.0F * arg1.w() - 3.0F * float5 - arg0.w() - arg0.w() - arg2.w()) * float0
            + float5 * arg3
            + float5;
        return arg4;
    }

    public Vector4f lerp(Vector4fc arg0, float arg1) {
        this.x = Math.fma(arg0.x() - this.x, arg1, this.x);
        this.y = Math.fma(arg0.y() - this.y, arg1, this.y);
        this.z = Math.fma(arg0.z() - this.z, arg1, this.z);
        this.w = Math.fma(arg0.w() - this.w, arg1, this.w);
        return this;
    }

    @Override
    public Vector4f lerp(Vector4fc arg0, float arg1, Vector4f arg2) {
        arg2.x = Math.fma(arg0.x() - this.x, arg1, this.x);
        arg2.y = Math.fma(arg0.y() - this.y, arg1, this.y);
        arg2.z = Math.fma(arg0.z() - this.z, arg1, this.z);
        arg2.w = Math.fma(arg0.w() - this.w, arg1, this.w);
        return arg2;
    }

    @Override
    public float get(int arg0) throws IllegalArgumentException {
        switch (arg0) {
            case 0:
                return this.x;
            case 1:
                return this.y;
            case 2:
                return this.z;
            case 3:
                return this.w;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public Vector4i get(int arg0, Vector4i arg1) {
        arg1.x = Math.roundUsing(this.x(), arg0);
        arg1.y = Math.roundUsing(this.y(), arg0);
        arg1.z = Math.roundUsing(this.z(), arg0);
        arg1.w = Math.roundUsing(this.w(), arg0);
        return arg1;
    }

    @Override
    public Vector4f get(Vector4f arg0) {
        arg0.x = this.x();
        arg0.y = this.y();
        arg0.z = this.z();
        arg0.w = this.w();
        return arg0;
    }

    @Override
    public Vector4d get(Vector4d arg0) {
        arg0.x = this.x();
        arg0.y = this.y();
        arg0.z = this.z();
        arg0.w = this.w();
        return arg0;
    }

    @Override
    public int maxComponent() {
        float float0 = Math.abs(this.x);
        float float1 = Math.abs(this.y);
        float float2 = Math.abs(this.z);
        float float3 = Math.abs(this.w);
        if (float0 >= float1 && float0 >= float2 && float0 >= float3) {
            return 0;
        } else if (float1 >= float2 && float1 >= float3) {
            return 1;
        } else {
            return float2 >= float3 ? 2 : 3;
        }
    }

    @Override
    public int minComponent() {
        float float0 = Math.abs(this.x);
        float float1 = Math.abs(this.y);
        float float2 = Math.abs(this.z);
        float float3 = Math.abs(this.w);
        if (float0 < float1 && float0 < float2 && float0 < float3) {
            return 0;
        } else if (float1 < float2 && float1 < float3) {
            return 1;
        } else {
            return float2 < float3 ? 2 : 3;
        }
    }

    public Vector4f floor() {
        this.x = Math.floor(this.x);
        this.y = Math.floor(this.y);
        this.z = Math.floor(this.z);
        this.w = Math.floor(this.w);
        return this;
    }

    @Override
    public Vector4f floor(Vector4f arg0) {
        arg0.x = Math.floor(this.x);
        arg0.y = Math.floor(this.y);
        arg0.z = Math.floor(this.z);
        arg0.w = Math.floor(this.w);
        return arg0;
    }

    public Vector4f ceil() {
        this.x = Math.ceil(this.x);
        this.y = Math.ceil(this.y);
        this.z = Math.ceil(this.z);
        this.w = Math.ceil(this.w);
        return this;
    }

    @Override
    public Vector4f ceil(Vector4f arg0) {
        arg0.x = Math.ceil(this.x);
        arg0.y = Math.ceil(this.y);
        arg0.z = Math.ceil(this.z);
        arg0.w = Math.ceil(this.w);
        return arg0;
    }

    public Vector4f round() {
        this.x = Math.round(this.x);
        this.y = Math.round(this.y);
        this.z = Math.round(this.z);
        this.w = Math.round(this.w);
        return this;
    }

    @Override
    public Vector4f round(Vector4f arg0) {
        arg0.x = Math.round(this.x);
        arg0.y = Math.round(this.y);
        arg0.z = Math.round(this.z);
        arg0.w = Math.round(this.w);
        return arg0;
    }

    @Override
    public boolean isFinite() {
        return Math.isFinite(this.x) && Math.isFinite(this.y) && Math.isFinite(this.z) && Math.isFinite(this.w);
    }

    public Vector4f absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
        this.w = Math.abs(this.w);
        return this;
    }

    @Override
    public Vector4f absolute(Vector4f arg0) {
        arg0.x = Math.abs(this.x);
        arg0.y = Math.abs(this.y);
        arg0.z = Math.abs(this.z);
        arg0.w = Math.abs(this.w);
        return arg0;
    }
}
