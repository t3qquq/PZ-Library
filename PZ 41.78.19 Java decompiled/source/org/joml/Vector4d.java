// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.text.NumberFormat;

public class Vector4d implements Externalizable, Vector4dc {
    private static final long serialVersionUID = 1L;
    public double x;
    public double y;
    public double z;
    public double w;

    public Vector4d() {
        this.w = 1.0;
    }

    public Vector4d(Vector4dc arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg0.w();
    }

    public Vector4d(Vector4ic arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg0.w();
    }

    public Vector4d(Vector3dc arg0, double arg1) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg1;
    }

    public Vector4d(Vector3ic arg0, double arg1) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg1;
    }

    public Vector4d(Vector2dc arg0, double arg1, double arg2) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg1;
        this.w = arg2;
    }

    public Vector4d(Vector2ic arg0, double arg1, double arg2) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg1;
        this.w = arg2;
    }

    public Vector4d(double arg0) {
        this.x = arg0;
        this.y = arg0;
        this.z = arg0;
        this.w = arg0;
    }

    public Vector4d(Vector4fc arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg0.w();
    }

    public Vector4d(Vector3fc arg0, double arg1) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg1;
    }

    public Vector4d(Vector2fc arg0, double arg1, double arg2) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg1;
        this.w = arg2;
    }

    public Vector4d(double arg0, double arg1, double arg2, double arg3) {
        this.x = arg0;
        this.y = arg1;
        this.z = arg2;
        this.w = arg3;
    }

    public Vector4d(float[] floats) {
        this.x = floats[0];
        this.y = floats[1];
        this.z = floats[2];
        this.w = floats[3];
    }

    public Vector4d(double[] arg0) {
        this.x = arg0[0];
        this.y = arg0[1];
        this.z = arg0[2];
        this.w = arg0[3];
    }

    public Vector4d(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
    }

    public Vector4d(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
    }

    public Vector4d(DoubleBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
    }

    public Vector4d(int arg0, DoubleBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
    }

    @Override
    public double x() {
        return this.x;
    }

    @Override
    public double y() {
        return this.y;
    }

    @Override
    public double z() {
        return this.z;
    }

    @Override
    public double w() {
        return this.w;
    }

    public Vector4d set(Vector4dc arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg0.w();
        return this;
    }

    public Vector4d set(Vector4fc arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg0.w();
        return this;
    }

    public Vector4d set(Vector4ic arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg0.w();
        return this;
    }

    public Vector4d set(Vector3dc arg0, double arg1) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg1;
        return this;
    }

    public Vector4d set(Vector3ic arg0, double arg1) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg1;
        return this;
    }

    public Vector4d set(Vector3fc arg0, double arg1) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg1;
        return this;
    }

    public Vector4d set(Vector2dc arg0, double arg1, double arg2) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg1;
        this.w = arg2;
        return this;
    }

    public Vector4d set(Vector2ic arg0, double arg1, double arg2) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg1;
        this.w = arg2;
        return this;
    }

    public Vector4d set(double arg0) {
        this.x = arg0;
        this.y = arg0;
        this.z = arg0;
        this.w = arg0;
        return this;
    }

    public Vector4d set(Vector2fc arg0, double arg1, double arg2) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg1;
        this.w = arg2;
        return this;
    }

    public Vector4d set(double arg0, double arg1, double arg2, double arg3) {
        this.x = arg0;
        this.y = arg1;
        this.z = arg2;
        this.w = arg3;
        return this;
    }

    public Vector4d set(double arg0, double arg1, double arg2) {
        this.x = arg0;
        this.y = arg1;
        this.z = arg2;
        return this;
    }

    public Vector4d set(double[] arg0) {
        this.x = arg0[0];
        this.y = arg0[1];
        this.z = arg0[2];
        this.w = arg0[2];
        return this;
    }

    public Vector4d set(float[] floats) {
        this.x = floats[0];
        this.y = floats[1];
        this.z = floats[2];
        this.w = floats[2];
        return this;
    }

    public Vector4d set(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Vector4d set(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
        return this;
    }

    public Vector4d set(DoubleBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Vector4d set(int arg0, DoubleBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
        return this;
    }

    public Vector4d setFromAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.get(this, arg0);
            return this;
        }
    }

    public Vector4d setComponent(int arg0, double arg1) throws IllegalArgumentException {
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
    public DoubleBuffer get(DoubleBuffer arg0) {
        MemUtil.INSTANCE.put(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public DoubleBuffer get(int arg0, DoubleBuffer arg1) {
        MemUtil.INSTANCE.put(this, arg0, arg1);
        return arg1;
    }

    @Override
    public ByteBuffer getf(ByteBuffer arg0) {
        MemUtil.INSTANCE.putf(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public ByteBuffer getf(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.putf(this, arg0, arg1);
        return arg1;
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
    public Vector4dc getToAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.put(this, arg0);
            return this;
        }
    }

    public Vector4d sub(Vector4dc arg0) {
        this.x = this.x - arg0.x();
        this.y = this.y - arg0.y();
        this.z = this.z - arg0.z();
        this.w = this.w - arg0.w();
        return this;
    }

    @Override
    public Vector4d sub(Vector4dc arg0, Vector4d arg1) {
        arg1.x = this.x - arg0.x();
        arg1.y = this.y - arg0.y();
        arg1.z = this.z - arg0.z();
        arg1.w = this.w - arg0.w();
        return arg1;
    }

    public Vector4d sub(Vector4fc arg0) {
        this.x = this.x - arg0.x();
        this.y = this.y - arg0.y();
        this.z = this.z - arg0.z();
        this.w = this.w - arg0.w();
        return this;
    }

    @Override
    public Vector4d sub(Vector4fc arg0, Vector4d arg1) {
        arg1.x = this.x - arg0.x();
        arg1.y = this.y - arg0.y();
        arg1.z = this.z - arg0.z();
        arg1.w = this.w - arg0.w();
        return arg1;
    }

    public Vector4d sub(double arg0, double arg1, double arg2, double arg3) {
        this.x -= arg0;
        this.y -= arg1;
        this.z -= arg2;
        this.w -= arg3;
        return this;
    }

    @Override
    public Vector4d sub(double arg0, double arg1, double arg2, double arg3, Vector4d arg4) {
        arg4.x = this.x - arg0;
        arg4.y = this.y - arg1;
        arg4.z = this.z - arg2;
        arg4.w = this.w - arg3;
        return arg4;
    }

    public Vector4d add(Vector4dc arg0) {
        this.x = this.x + arg0.x();
        this.y = this.y + arg0.y();
        this.z = this.z + arg0.z();
        this.w = this.w + arg0.w();
        return this;
    }

    @Override
    public Vector4d add(Vector4dc arg0, Vector4d arg1) {
        arg1.x = this.x + arg0.x();
        arg1.y = this.y + arg0.y();
        arg1.z = this.z + arg0.z();
        arg1.w = this.w + arg0.w();
        return arg1;
    }

    @Override
    public Vector4d add(Vector4fc arg0, Vector4d arg1) {
        arg1.x = this.x + arg0.x();
        arg1.y = this.y + arg0.y();
        arg1.z = this.z + arg0.z();
        arg1.w = this.w + arg0.w();
        return arg1;
    }

    public Vector4d add(double arg0, double arg1, double arg2, double arg3) {
        this.x += arg0;
        this.y += arg1;
        this.z += arg2;
        this.w += arg3;
        return this;
    }

    @Override
    public Vector4d add(double arg0, double arg1, double arg2, double arg3, Vector4d arg4) {
        arg4.x = this.x + arg0;
        arg4.y = this.y + arg1;
        arg4.z = this.z + arg2;
        arg4.w = this.w + arg3;
        return arg4;
    }

    public Vector4d add(Vector4fc arg0) {
        this.x = this.x + arg0.x();
        this.y = this.y + arg0.y();
        this.z = this.z + arg0.z();
        this.w = this.w + arg0.w();
        return this;
    }

    public Vector4d fma(Vector4dc arg0, Vector4dc arg1) {
        this.x = Math.fma(arg0.x(), arg1.x(), this.x);
        this.y = Math.fma(arg0.y(), arg1.y(), this.y);
        this.z = Math.fma(arg0.z(), arg1.z(), this.z);
        this.w = Math.fma(arg0.w(), arg1.w(), this.w);
        return this;
    }

    public Vector4d fma(double arg0, Vector4dc arg1) {
        this.x = Math.fma(arg0, arg1.x(), this.x);
        this.y = Math.fma(arg0, arg1.y(), this.y);
        this.z = Math.fma(arg0, arg1.z(), this.z);
        this.w = Math.fma(arg0, arg1.w(), this.w);
        return this;
    }

    @Override
    public Vector4d fma(Vector4dc arg0, Vector4dc arg1, Vector4d arg2) {
        arg2.x = Math.fma(arg0.x(), arg1.x(), this.x);
        arg2.y = Math.fma(arg0.y(), arg1.y(), this.y);
        arg2.z = Math.fma(arg0.z(), arg1.z(), this.z);
        arg2.w = Math.fma(arg0.w(), arg1.w(), this.w);
        return arg2;
    }

    @Override
    public Vector4d fma(double arg0, Vector4dc arg1, Vector4d arg2) {
        arg2.x = Math.fma(arg0, arg1.x(), this.x);
        arg2.y = Math.fma(arg0, arg1.y(), this.y);
        arg2.z = Math.fma(arg0, arg1.z(), this.z);
        arg2.w = Math.fma(arg0, arg1.w(), this.w);
        return arg2;
    }

    public Vector4d mulAdd(Vector4dc arg0, Vector4dc arg1) {
        this.x = Math.fma(this.x, arg0.x(), arg1.x());
        this.y = Math.fma(this.y, arg0.y(), arg1.y());
        this.z = Math.fma(this.z, arg0.z(), arg1.z());
        return this;
    }

    public Vector4d mulAdd(double arg0, Vector4dc arg1) {
        this.x = Math.fma(this.x, arg0, arg1.x());
        this.y = Math.fma(this.y, arg0, arg1.y());
        this.z = Math.fma(this.z, arg0, arg1.z());
        return this;
    }

    @Override
    public Vector4d mulAdd(Vector4dc arg0, Vector4dc arg1, Vector4d arg2) {
        arg2.x = Math.fma(this.x, arg0.x(), arg1.x());
        arg2.y = Math.fma(this.y, arg0.y(), arg1.y());
        arg2.z = Math.fma(this.z, arg0.z(), arg1.z());
        return arg2;
    }

    @Override
    public Vector4d mulAdd(double arg0, Vector4dc arg1, Vector4d arg2) {
        arg2.x = Math.fma(this.x, arg0, arg1.x());
        arg2.y = Math.fma(this.y, arg0, arg1.y());
        arg2.z = Math.fma(this.z, arg0, arg1.z());
        return arg2;
    }

    public Vector4d mul(Vector4dc arg0) {
        this.x = this.x * arg0.x();
        this.y = this.y * arg0.y();
        this.z = this.z * arg0.z();
        this.w = this.w * arg0.w();
        return this;
    }

    @Override
    public Vector4d mul(Vector4dc arg0, Vector4d arg1) {
        arg1.x = this.x * arg0.x();
        arg1.y = this.y * arg0.y();
        arg1.z = this.z * arg0.z();
        arg1.w = this.w * arg0.w();
        return arg1;
    }

    public Vector4d div(Vector4dc arg0) {
        this.x = this.x / arg0.x();
        this.y = this.y / arg0.y();
        this.z = this.z / arg0.z();
        this.w = this.w / arg0.w();
        return this;
    }

    @Override
    public Vector4d div(Vector4dc arg0, Vector4d arg1) {
        arg1.x = this.x / arg0.x();
        arg1.y = this.y / arg0.y();
        arg1.z = this.z / arg0.z();
        arg1.w = this.w / arg0.w();
        return arg1;
    }

    public Vector4d mul(Vector4fc arg0) {
        this.x = this.x * arg0.x();
        this.y = this.y * arg0.y();
        this.z = this.z * arg0.z();
        this.w = this.w * arg0.w();
        return this;
    }

    @Override
    public Vector4d mul(Vector4fc arg0, Vector4d arg1) {
        arg1.x = this.x * arg0.x();
        arg1.y = this.y * arg0.y();
        arg1.z = this.z * arg0.z();
        arg1.w = this.w * arg0.w();
        return arg1;
    }

    public Vector4d mul(Matrix4dc arg0) {
        return (arg0.properties() & 2) != 0 ? this.mulAffine(arg0, this) : this.mulGeneric(arg0, this);
    }

    @Override
    public Vector4d mul(Matrix4dc arg0, Vector4d arg1) {
        return (arg0.properties() & 2) != 0 ? this.mulAffine(arg0, arg1) : this.mulGeneric(arg0, arg1);
    }

    public Vector4d mulTranspose(Matrix4dc arg0) {
        return (arg0.properties() & 2) != 0 ? this.mulAffineTranspose(arg0, this) : this.mulGenericTranspose(arg0, this);
    }

    @Override
    public Vector4d mulTranspose(Matrix4dc arg0, Vector4d arg1) {
        return (arg0.properties() & 2) != 0 ? this.mulAffineTranspose(arg0, arg1) : this.mulGenericTranspose(arg0, arg1);
    }

    @Override
    public Vector4d mulAffine(Matrix4dc arg0, Vector4d arg1) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, Math.fma(arg0.m20(), this.z, arg0.m30() * this.w)));
        double double1 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, Math.fma(arg0.m21(), this.z, arg0.m31() * this.w)));
        double double2 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, Math.fma(arg0.m22(), this.z, arg0.m32() * this.w)));
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = double2;
        arg1.w = this.w;
        return arg1;
    }

    private Vector4d mulGeneric(Matrix4dc matrix4dc, Vector4d vector4d1) {
        double double0 = Math.fma(matrix4dc.m00(), this.x, Math.fma(matrix4dc.m10(), this.y, Math.fma(matrix4dc.m20(), this.z, matrix4dc.m30() * this.w)));
        double double1 = Math.fma(matrix4dc.m01(), this.x, Math.fma(matrix4dc.m11(), this.y, Math.fma(matrix4dc.m21(), this.z, matrix4dc.m31() * this.w)));
        double double2 = Math.fma(matrix4dc.m02(), this.x, Math.fma(matrix4dc.m12(), this.y, Math.fma(matrix4dc.m22(), this.z, matrix4dc.m32() * this.w)));
        double double3 = Math.fma(matrix4dc.m03(), this.x, Math.fma(matrix4dc.m13(), this.y, Math.fma(matrix4dc.m23(), this.z, matrix4dc.m33() * this.w)));
        vector4d1.x = double0;
        vector4d1.y = double1;
        vector4d1.z = double2;
        vector4d1.w = double3;
        return vector4d1;
    }

    @Override
    public Vector4d mulAffineTranspose(Matrix4dc arg0, Vector4d arg1) {
        double double0 = this.x;
        double double1 = this.y;
        double double2 = this.z;
        double double3 = this.w;
        arg1.x = Math.fma(arg0.m00(), double0, Math.fma(arg0.m01(), double1, arg0.m02() * double2));
        arg1.y = Math.fma(arg0.m10(), double0, Math.fma(arg0.m11(), double1, arg0.m12() * double2));
        arg1.z = Math.fma(arg0.m20(), double0, Math.fma(arg0.m21(), double1, arg0.m22() * double2));
        arg1.w = Math.fma(arg0.m30(), double0, Math.fma(arg0.m31(), double1, arg0.m32() * double2 + double3));
        return arg1;
    }

    private Vector4d mulGenericTranspose(Matrix4dc matrix4dc, Vector4d vector4d1) {
        double double0 = this.x;
        double double1 = this.y;
        double double2 = this.z;
        double double3 = this.w;
        vector4d1.x = Math.fma(matrix4dc.m00(), double0, Math.fma(matrix4dc.m01(), double1, Math.fma(matrix4dc.m02(), double2, matrix4dc.m03() * double3)));
        vector4d1.y = Math.fma(matrix4dc.m10(), double0, Math.fma(matrix4dc.m11(), double1, Math.fma(matrix4dc.m12(), double2, matrix4dc.m13() * double3)));
        vector4d1.z = Math.fma(matrix4dc.m20(), double0, Math.fma(matrix4dc.m21(), double1, Math.fma(matrix4dc.m22(), double2, matrix4dc.m23() * double3)));
        vector4d1.w = Math.fma(matrix4dc.m30(), double0, Math.fma(matrix4dc.m31(), double1, Math.fma(matrix4dc.m32(), double2, matrix4dc.m33() * double3)));
        return vector4d1;
    }

    public Vector4d mul(Matrix4x3dc arg0) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, Math.fma(arg0.m20(), this.z, arg0.m30() * this.w)));
        double double1 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, Math.fma(arg0.m21(), this.z, arg0.m31() * this.w)));
        double double2 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, Math.fma(arg0.m22(), this.z, arg0.m32() * this.w)));
        this.x = double0;
        this.y = double1;
        this.z = double2;
        return this;
    }

    @Override
    public Vector4d mul(Matrix4x3dc arg0, Vector4d arg1) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, Math.fma(arg0.m20(), this.z, arg0.m30() * this.w)));
        double double1 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, Math.fma(arg0.m21(), this.z, arg0.m31() * this.w)));
        double double2 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, Math.fma(arg0.m22(), this.z, arg0.m32() * this.w)));
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = double2;
        arg1.w = this.w;
        return arg1;
    }

    public Vector4d mul(Matrix4x3fc arg0) {
        double double0 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m10(), this.y, Math.fma((double)arg0.m20(), this.z, arg0.m30() * this.w)));
        double double1 = Math.fma((double)arg0.m01(), this.x, Math.fma((double)arg0.m11(), this.y, Math.fma((double)arg0.m21(), this.z, arg0.m31() * this.w)));
        double double2 = Math.fma((double)arg0.m02(), this.x, Math.fma((double)arg0.m12(), this.y, Math.fma((double)arg0.m22(), this.z, arg0.m32() * this.w)));
        this.x = double0;
        this.y = double1;
        this.z = double2;
        return this;
    }

    @Override
    public Vector4d mul(Matrix4x3fc arg0, Vector4d arg1) {
        double double0 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m10(), this.y, Math.fma((double)arg0.m20(), this.z, arg0.m30() * this.w)));
        double double1 = Math.fma((double)arg0.m01(), this.x, Math.fma((double)arg0.m11(), this.y, Math.fma((double)arg0.m21(), this.z, arg0.m31() * this.w)));
        double double2 = Math.fma((double)arg0.m02(), this.x, Math.fma((double)arg0.m12(), this.y, Math.fma((double)arg0.m22(), this.z, arg0.m32() * this.w)));
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = double2;
        arg1.w = this.w;
        return arg1;
    }

    public Vector4d mul(Matrix4fc arg0) {
        return (arg0.properties() & 2) != 0 ? this.mulAffine(arg0, this) : this.mulGeneric(arg0, this);
    }

    @Override
    public Vector4d mul(Matrix4fc arg0, Vector4d arg1) {
        return (arg0.properties() & 2) != 0 ? this.mulAffine(arg0, arg1) : this.mulGeneric(arg0, arg1);
    }

    private Vector4d mulAffine(Matrix4fc matrix4fc, Vector4d vector4d1) {
        double double0 = Math.fma(
            (double)matrix4fc.m00(), this.x, Math.fma((double)matrix4fc.m10(), this.y, Math.fma((double)matrix4fc.m20(), this.z, matrix4fc.m30() * this.w))
        );
        double double1 = Math.fma(
            (double)matrix4fc.m01(), this.x, Math.fma((double)matrix4fc.m11(), this.y, Math.fma((double)matrix4fc.m21(), this.z, matrix4fc.m31() * this.w))
        );
        double double2 = Math.fma(
            (double)matrix4fc.m02(), this.x, Math.fma((double)matrix4fc.m12(), this.y, Math.fma((double)matrix4fc.m22(), this.z, matrix4fc.m32() * this.w))
        );
        vector4d1.x = double0;
        vector4d1.y = double1;
        vector4d1.z = double2;
        vector4d1.w = this.w;
        return vector4d1;
    }

    private Vector4d mulGeneric(Matrix4fc matrix4fc, Vector4d vector4d1) {
        double double0 = Math.fma(
            (double)matrix4fc.m00(), this.x, Math.fma((double)matrix4fc.m10(), this.y, Math.fma((double)matrix4fc.m20(), this.z, matrix4fc.m30() * this.w))
        );
        double double1 = Math.fma(
            (double)matrix4fc.m01(), this.x, Math.fma((double)matrix4fc.m11(), this.y, Math.fma((double)matrix4fc.m21(), this.z, matrix4fc.m31() * this.w))
        );
        double double2 = Math.fma(
            (double)matrix4fc.m02(), this.x, Math.fma((double)matrix4fc.m12(), this.y, Math.fma((double)matrix4fc.m22(), this.z, matrix4fc.m32() * this.w))
        );
        double double3 = Math.fma(
            (double)matrix4fc.m03(), this.x, Math.fma((double)matrix4fc.m13(), this.y, Math.fma((double)matrix4fc.m23(), this.z, matrix4fc.m33() * this.w))
        );
        vector4d1.x = double0;
        vector4d1.y = double1;
        vector4d1.z = double2;
        vector4d1.w = double3;
        return vector4d1;
    }

    @Override
    public Vector4d mulProject(Matrix4dc arg0, Vector4d arg1) {
        double double0 = 1.0 / Math.fma(arg0.m03(), this.x, Math.fma(arg0.m13(), this.y, Math.fma(arg0.m23(), this.z, arg0.m33() * this.w)));
        double double1 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, Math.fma(arg0.m20(), this.z, arg0.m30() * this.w))) * double0;
        double double2 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, Math.fma(arg0.m21(), this.z, arg0.m31() * this.w))) * double0;
        double double3 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, Math.fma(arg0.m22(), this.z, arg0.m32() * this.w))) * double0;
        arg1.x = double1;
        arg1.y = double2;
        arg1.z = double3;
        arg1.w = 1.0;
        return arg1;
    }

    public Vector4d mulProject(Matrix4dc arg0) {
        double double0 = 1.0 / Math.fma(arg0.m03(), this.x, Math.fma(arg0.m13(), this.y, Math.fma(arg0.m23(), this.z, arg0.m33() * this.w)));
        double double1 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, Math.fma(arg0.m20(), this.z, arg0.m30() * this.w))) * double0;
        double double2 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, Math.fma(arg0.m21(), this.z, arg0.m31() * this.w))) * double0;
        double double3 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, Math.fma(arg0.m22(), this.z, arg0.m32() * this.w))) * double0;
        this.x = double1;
        this.y = double2;
        this.z = double3;
        this.w = 1.0;
        return this;
    }

    @Override
    public Vector3d mulProject(Matrix4dc arg0, Vector3d arg1) {
        double double0 = 1.0 / Math.fma(arg0.m03(), this.x, Math.fma(arg0.m13(), this.y, Math.fma(arg0.m23(), this.z, arg0.m33() * this.w)));
        double double1 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, Math.fma(arg0.m20(), this.z, arg0.m30() * this.w))) * double0;
        double double2 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, Math.fma(arg0.m21(), this.z, arg0.m31() * this.w))) * double0;
        double double3 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, Math.fma(arg0.m22(), this.z, arg0.m32() * this.w))) * double0;
        arg1.x = double1;
        arg1.y = double2;
        arg1.z = double3;
        return arg1;
    }

    public Vector4d mul(double arg0) {
        this.x *= arg0;
        this.y *= arg0;
        this.z *= arg0;
        this.w *= arg0;
        return this;
    }

    @Override
    public Vector4d mul(double arg0, Vector4d arg1) {
        arg1.x = this.x * arg0;
        arg1.y = this.y * arg0;
        arg1.z = this.z * arg0;
        arg1.w = this.w * arg0;
        return arg1;
    }

    public Vector4d div(double arg0) {
        double double0 = 1.0 / arg0;
        this.x *= double0;
        this.y *= double0;
        this.z *= double0;
        this.w *= double0;
        return this;
    }

    @Override
    public Vector4d div(double arg0, Vector4d arg1) {
        double double0 = 1.0 / arg0;
        arg1.x = this.x * double0;
        arg1.y = this.y * double0;
        arg1.z = this.z * double0;
        arg1.w = this.w * double0;
        return arg1;
    }

    public Vector4d rotate(Quaterniondc arg0) {
        arg0.transform(this, this);
        return this;
    }

    @Override
    public Vector4d rotate(Quaterniondc arg0, Vector4d arg1) {
        arg0.transform(this, arg1);
        return arg1;
    }

    public Vector4d rotateAxis(double arg0, double arg1, double arg2, double arg3) {
        if (arg2 == 0.0 && arg3 == 0.0 && Math.absEqualsOne(arg1)) {
            return this.rotateX(arg1 * arg0, this);
        } else if (arg1 == 0.0 && arg3 == 0.0 && Math.absEqualsOne(arg2)) {
            return this.rotateY(arg2 * arg0, this);
        } else {
            return arg1 == 0.0 && arg2 == 0.0 && Math.absEqualsOne(arg3)
                ? this.rotateZ(arg3 * arg0, this)
                : this.rotateAxisInternal(arg0, arg1, arg2, arg3, this);
        }
    }

    @Override
    public Vector4d rotateAxis(double arg0, double arg1, double arg2, double arg3, Vector4d arg4) {
        if (arg2 == 0.0 && arg3 == 0.0 && Math.absEqualsOne(arg1)) {
            return this.rotateX(arg1 * arg0, arg4);
        } else if (arg1 == 0.0 && arg3 == 0.0 && Math.absEqualsOne(arg2)) {
            return this.rotateY(arg2 * arg0, arg4);
        } else {
            return arg1 == 0.0 && arg2 == 0.0 && Math.absEqualsOne(arg3)
                ? this.rotateZ(arg3 * arg0, arg4)
                : this.rotateAxisInternal(arg0, arg1, arg2, arg3, arg4);
        }
    }

    private Vector4d rotateAxisInternal(double double1, double double4, double double6, double double8, Vector4d vector4d1) {
        double double0 = double1 * 0.5;
        double double2 = Math.sin(double0);
        double double3 = double4 * double2;
        double double5 = double6 * double2;
        double double7 = double8 * double2;
        double double9 = Math.cosFromSin(double2, double0);
        double double10 = double9 * double9;
        double double11 = double3 * double3;
        double double12 = double5 * double5;
        double double13 = double7 * double7;
        double double14 = double7 * double9;
        double double15 = double3 * double5;
        double double16 = double3 * double7;
        double double17 = double5 * double9;
        double double18 = double5 * double7;
        double double19 = double3 * double9;
        double double20 = (double10 + double11 - double13 - double12) * this.x
            + (-double14 + double15 - double14 + double15) * this.y
            + (double17 + double16 + double16 + double17) * this.z;
        double double21 = (double15 + double14 + double14 + double15) * this.x
            + (double12 - double13 + double10 - double11) * this.y
            + (double18 + double18 - double19 - double19) * this.z;
        double double22 = (double16 - double17 + double16 - double17) * this.x
            + (double18 + double18 + double19 + double19) * this.y
            + (double13 - double12 - double11 + double10) * this.z;
        vector4d1.x = double20;
        vector4d1.y = double21;
        vector4d1.z = double22;
        return vector4d1;
    }

    public Vector4d rotateX(double arg0) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = this.y * double1 - this.z * double0;
        double double3 = this.y * double0 + this.z * double1;
        this.y = double2;
        this.z = double3;
        return this;
    }

    @Override
    public Vector4d rotateX(double arg0, Vector4d arg1) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = this.y * double1 - this.z * double0;
        double double3 = this.y * double0 + this.z * double1;
        arg1.x = this.x;
        arg1.y = double2;
        arg1.z = double3;
        arg1.w = this.w;
        return arg1;
    }

    public Vector4d rotateY(double arg0) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = this.x * double1 + this.z * double0;
        double double3 = -this.x * double0 + this.z * double1;
        this.x = double2;
        this.z = double3;
        return this;
    }

    @Override
    public Vector4d rotateY(double arg0, Vector4d arg1) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = this.x * double1 + this.z * double0;
        double double3 = -this.x * double0 + this.z * double1;
        arg1.x = double2;
        arg1.y = this.y;
        arg1.z = double3;
        arg1.w = this.w;
        return arg1;
    }

    public Vector4d rotateZ(double arg0) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = this.x * double1 - this.y * double0;
        double double3 = this.x * double0 + this.y * double1;
        this.x = double2;
        this.y = double3;
        return this;
    }

    @Override
    public Vector4d rotateZ(double arg0, Vector4d arg1) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = this.x * double1 - this.y * double0;
        double double3 = this.x * double0 + this.y * double1;
        arg1.x = double2;
        arg1.y = double3;
        arg1.z = this.z;
        arg1.w = this.w;
        return arg1;
    }

    @Override
    public double lengthSquared() {
        return Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
    }

    public static double lengthSquared(double arg0, double arg1, double arg2, double arg3) {
        return Math.fma(arg0, arg0, Math.fma(arg1, arg1, Math.fma(arg2, arg2, arg3 * arg3)));
    }

    @Override
    public double length() {
        return Math.sqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w))));
    }

    public static double length(double arg0, double arg1, double arg2, double arg3) {
        return Math.sqrt(Math.fma(arg0, arg0, Math.fma(arg1, arg1, Math.fma(arg2, arg2, arg3 * arg3))));
    }

    public Vector4d normalize() {
        double double0 = 1.0 / this.length();
        this.x *= double0;
        this.y *= double0;
        this.z *= double0;
        this.w *= double0;
        return this;
    }

    @Override
    public Vector4d normalize(Vector4d arg0) {
        double double0 = 1.0 / this.length();
        arg0.x = this.x * double0;
        arg0.y = this.y * double0;
        arg0.z = this.z * double0;
        arg0.w = this.w * double0;
        return arg0;
    }

    public Vector4d normalize(double arg0) {
        double double0 = 1.0 / this.length() * arg0;
        this.x *= double0;
        this.y *= double0;
        this.z *= double0;
        this.w *= double0;
        return this;
    }

    @Override
    public Vector4d normalize(double arg0, Vector4d arg1) {
        double double0 = 1.0 / this.length() * arg0;
        arg1.x = this.x * double0;
        arg1.y = this.y * double0;
        arg1.z = this.z * double0;
        arg1.w = this.w * double0;
        return arg1;
    }

    public Vector4d normalize3() {
        double double0 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
        this.x *= double0;
        this.y *= double0;
        this.z *= double0;
        this.w *= double0;
        return this;
    }

    @Override
    public Vector4d normalize3(Vector4d arg0) {
        double double0 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
        arg0.x = this.x * double0;
        arg0.y = this.y * double0;
        arg0.z = this.z * double0;
        arg0.w = this.w * double0;
        return arg0;
    }

    @Override
    public double distance(Vector4dc arg0) {
        double double0 = this.x - arg0.x();
        double double1 = this.y - arg0.y();
        double double2 = this.z - arg0.z();
        double double3 = this.w - arg0.w();
        return Math.sqrt(Math.fma(double0, double0, Math.fma(double1, double1, Math.fma(double2, double2, double3 * double3))));
    }

    @Override
    public double distance(double arg0, double arg1, double arg2, double arg3) {
        double double0 = this.x - arg0;
        double double1 = this.y - arg1;
        double double2 = this.z - arg2;
        double double3 = this.w - arg3;
        return Math.sqrt(Math.fma(double0, double0, Math.fma(double1, double1, Math.fma(double2, double2, double3 * double3))));
    }

    @Override
    public double distanceSquared(Vector4dc arg0) {
        double double0 = this.x - arg0.x();
        double double1 = this.y - arg0.y();
        double double2 = this.z - arg0.z();
        double double3 = this.w - arg0.w();
        return Math.fma(double0, double0, Math.fma(double1, double1, Math.fma(double2, double2, double3 * double3)));
    }

    @Override
    public double distanceSquared(double arg0, double arg1, double arg2, double arg3) {
        double double0 = this.x - arg0;
        double double1 = this.y - arg1;
        double double2 = this.z - arg2;
        double double3 = this.w - arg3;
        return Math.fma(double0, double0, Math.fma(double1, double1, Math.fma(double2, double2, double3 * double3)));
    }

    public static double distance(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7) {
        double double0 = arg0 - arg4;
        double double1 = arg1 - arg5;
        double double2 = arg2 - arg6;
        double double3 = arg3 - arg7;
        return Math.sqrt(Math.fma(double0, double0, Math.fma(double1, double1, Math.fma(double2, double2, double3 * double3))));
    }

    public static double distanceSquared(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7) {
        double double0 = arg0 - arg4;
        double double1 = arg1 - arg5;
        double double2 = arg2 - arg6;
        double double3 = arg3 - arg7;
        return Math.fma(double0, double0, Math.fma(double1, double1, Math.fma(double2, double2, double3 * double3)));
    }

    @Override
    public double dot(Vector4dc arg0) {
        return Math.fma(this.x, arg0.x(), Math.fma(this.y, arg0.y(), Math.fma(this.z, arg0.z(), this.w * arg0.w())));
    }

    @Override
    public double dot(double arg0, double arg1, double arg2, double arg3) {
        return Math.fma(this.x, arg0, Math.fma(this.y, arg1, Math.fma(this.z, arg2, this.w * arg3)));
    }

    @Override
    public double angleCos(Vector4dc arg0) {
        double double0 = Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
        double double1 = Math.fma(arg0.x(), arg0.x(), Math.fma(arg0.y(), arg0.y(), Math.fma(arg0.z(), arg0.z(), arg0.w() * arg0.w())));
        double double2 = Math.fma(this.x, arg0.x(), Math.fma(this.y, arg0.y(), Math.fma(this.z, arg0.z(), this.w * arg0.w())));
        return double2 / Math.sqrt(double0 * double1);
    }

    @Override
    public double angle(Vector4dc arg0) {
        double double0 = this.angleCos(arg0);
        double0 = double0 < 1.0 ? double0 : 1.0;
        double0 = double0 > -1.0 ? double0 : -1.0;
        return Math.acos(double0);
    }

    public Vector4d zero() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        this.w = 0.0;
        return this;
    }

    public Vector4d negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.w = -this.w;
        return this;
    }

    @Override
    public Vector4d negate(Vector4d arg0) {
        arg0.x = -this.x;
        arg0.y = -this.y;
        arg0.z = -this.z;
        arg0.w = -this.w;
        return arg0;
    }

    public Vector4d min(Vector4dc arg0) {
        this.x = this.x < arg0.x() ? this.x : arg0.x();
        this.y = this.y < arg0.y() ? this.y : arg0.y();
        this.z = this.z < arg0.z() ? this.z : arg0.z();
        this.w = this.w < arg0.w() ? this.w : arg0.w();
        return this;
    }

    @Override
    public Vector4d min(Vector4dc arg0, Vector4d arg1) {
        arg1.x = this.x < arg0.x() ? this.x : arg0.x();
        arg1.y = this.y < arg0.y() ? this.y : arg0.y();
        arg1.z = this.z < arg0.z() ? this.z : arg0.z();
        arg1.w = this.w < arg0.w() ? this.w : arg0.w();
        return arg1;
    }

    public Vector4d max(Vector4dc arg0) {
        this.x = this.x > arg0.x() ? this.x : arg0.x();
        this.y = this.y > arg0.y() ? this.y : arg0.y();
        this.z = this.z > arg0.z() ? this.z : arg0.z();
        this.w = this.w > arg0.w() ? this.w : arg0.w();
        return this;
    }

    @Override
    public Vector4d max(Vector4dc arg0, Vector4d arg1) {
        arg1.x = this.x > arg0.x() ? this.x : arg0.x();
        arg1.y = this.y > arg0.y() ? this.y : arg0.y();
        arg1.z = this.z > arg0.z() ? this.z : arg0.z();
        arg1.w = this.w > arg0.w() ? this.w : arg0.w();
        return arg1;
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
        arg0.writeDouble(this.x);
        arg0.writeDouble(this.y);
        arg0.writeDouble(this.z);
        arg0.writeDouble(this.w);
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        this.x = arg0.readDouble();
        this.y = arg0.readDouble();
        this.z = arg0.readDouble();
        this.w = arg0.readDouble();
    }

    @Override
    public int hashCode() {
        int int0 = 1;
        long long0 = Double.doubleToLongBits(this.w);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.x);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.y);
        int0 = 31 * int0 + (int)(long0 ^ long0 >>> 32);
        long0 = Double.doubleToLongBits(this.z);
        return 31 * int0 + (int)(long0 ^ long0 >>> 32);
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
            Vector4d vector4d = (Vector4d)arg0;
            if (Double.doubleToLongBits(this.w) != Double.doubleToLongBits(vector4d.w)) {
                return false;
            } else if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(vector4d.x)) {
                return false;
            } else {
                return Double.doubleToLongBits(this.y) != Double.doubleToLongBits(vector4d.y)
                    ? false
                    : Double.doubleToLongBits(this.z) == Double.doubleToLongBits(vector4d.z);
            }
        }
    }

    @Override
    public boolean equals(Vector4dc arg0, double arg1) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (!(arg0 instanceof Vector4dc)) {
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
    public boolean equals(double arg0, double arg1, double arg2, double arg3) {
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(arg0)) {
            return false;
        } else if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(arg1)) {
            return false;
        } else {
            return Double.doubleToLongBits(this.z) != Double.doubleToLongBits(arg2) ? false : Double.doubleToLongBits(this.w) == Double.doubleToLongBits(arg3);
        }
    }

    @Override
    public Vector4d smoothStep(Vector4dc arg0, double arg1, Vector4d arg2) {
        double double0 = arg1 * arg1;
        double double1 = double0 * arg1;
        arg2.x = (this.x + this.x - arg0.x() - arg0.x()) * double1 + (3.0 * arg0.x() - 3.0 * this.x) * double0 + this.x * arg1 + this.x;
        arg2.y = (this.y + this.y - arg0.y() - arg0.y()) * double1 + (3.0 * arg0.y() - 3.0 * this.y) * double0 + this.y * arg1 + this.y;
        arg2.z = (this.z + this.z - arg0.z() - arg0.z()) * double1 + (3.0 * arg0.z() - 3.0 * this.z) * double0 + this.z * arg1 + this.z;
        arg2.w = (this.w + this.w - arg0.w() - arg0.w()) * double1 + (3.0 * arg0.w() - 3.0 * this.w) * double0 + this.w * arg1 + this.w;
        return arg2;
    }

    @Override
    public Vector4d hermite(Vector4dc arg0, Vector4dc arg1, Vector4dc arg2, double arg3, Vector4d arg4) {
        double double0 = arg3 * arg3;
        double double1 = double0 * arg3;
        arg4.x = (this.x + this.x - arg1.x() - arg1.x() + arg2.x() + arg0.x()) * double1
            + (3.0 * arg1.x() - 3.0 * this.x - arg0.x() - arg0.x() - arg2.x()) * double0
            + this.x * arg3
            + this.x;
        arg4.y = (this.y + this.y - arg1.y() - arg1.y() + arg2.y() + arg0.y()) * double1
            + (3.0 * arg1.y() - 3.0 * this.y - arg0.y() - arg0.y() - arg2.y()) * double0
            + this.y * arg3
            + this.y;
        arg4.z = (this.z + this.z - arg1.z() - arg1.z() + arg2.z() + arg0.z()) * double1
            + (3.0 * arg1.z() - 3.0 * this.z - arg0.z() - arg0.z() - arg2.z()) * double0
            + this.z * arg3
            + this.z;
        arg4.w = (this.w + this.w - arg1.w() - arg1.w() + arg2.w() + arg0.w()) * double1
            + (3.0 * arg1.w() - 3.0 * this.w - arg0.w() - arg0.w() - arg2.w()) * double0
            + this.w * arg3
            + this.w;
        return arg4;
    }

    public Vector4d lerp(Vector4dc arg0, double arg1) {
        this.x = Math.fma(arg0.x() - this.x, arg1, this.x);
        this.y = Math.fma(arg0.y() - this.y, arg1, this.y);
        this.z = Math.fma(arg0.z() - this.z, arg1, this.z);
        this.w = Math.fma(arg0.w() - this.w, arg1, this.w);
        return this;
    }

    @Override
    public Vector4d lerp(Vector4dc arg0, double arg1, Vector4d arg2) {
        arg2.x = Math.fma(arg0.x() - this.x, arg1, this.x);
        arg2.y = Math.fma(arg0.y() - this.y, arg1, this.y);
        arg2.z = Math.fma(arg0.z() - this.z, arg1, this.z);
        arg2.w = Math.fma(arg0.w() - this.w, arg1, this.w);
        return arg2;
    }

    @Override
    public double get(int arg0) throws IllegalArgumentException {
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
        arg0.x = (float)this.x();
        arg0.y = (float)this.y();
        arg0.z = (float)this.z();
        arg0.w = (float)this.w();
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
        double double0 = Math.abs(this.x);
        double double1 = Math.abs(this.y);
        double double2 = Math.abs(this.z);
        double double3 = Math.abs(this.w);
        if (double0 >= double1 && double0 >= double2 && double0 >= double3) {
            return 0;
        } else if (double1 >= double2 && double1 >= double3) {
            return 1;
        } else {
            return double2 >= double3 ? 2 : 3;
        }
    }

    @Override
    public int minComponent() {
        double double0 = Math.abs(this.x);
        double double1 = Math.abs(this.y);
        double double2 = Math.abs(this.z);
        double double3 = Math.abs(this.w);
        if (double0 < double1 && double0 < double2 && double0 < double3) {
            return 0;
        } else if (double1 < double2 && double1 < double3) {
            return 1;
        } else {
            return double2 < double3 ? 2 : 3;
        }
    }

    public Vector4d floor() {
        this.x = Math.floor(this.x);
        this.y = Math.floor(this.y);
        this.z = Math.floor(this.z);
        this.w = Math.floor(this.w);
        return this;
    }

    @Override
    public Vector4d floor(Vector4d arg0) {
        arg0.x = Math.floor(this.x);
        arg0.y = Math.floor(this.y);
        arg0.z = Math.floor(this.z);
        arg0.w = Math.floor(this.w);
        return arg0;
    }

    public Vector4d ceil() {
        this.x = Math.ceil(this.x);
        this.y = Math.ceil(this.y);
        this.z = Math.ceil(this.z);
        this.w = Math.ceil(this.w);
        return this;
    }

    @Override
    public Vector4d ceil(Vector4d arg0) {
        arg0.x = Math.ceil(this.x);
        arg0.y = Math.ceil(this.y);
        arg0.z = Math.ceil(this.z);
        arg0.w = Math.ceil(this.w);
        return arg0;
    }

    public Vector4d round() {
        this.x = Math.round(this.x);
        this.y = Math.round(this.y);
        this.z = Math.round(this.z);
        this.w = Math.round(this.w);
        return this;
    }

    @Override
    public Vector4d round(Vector4d arg0) {
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

    public Vector4d absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
        this.w = Math.abs(this.w);
        return this;
    }

    @Override
    public Vector4d absolute(Vector4d arg0) {
        arg0.x = Math.abs(this.x);
        arg0.y = Math.abs(this.y);
        arg0.z = Math.abs(this.z);
        arg0.w = Math.abs(this.w);
        return arg0;
    }
}
