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

public class Vector3d implements Externalizable, Vector3dc {
    private static final long serialVersionUID = 1L;
    public double x;
    public double y;
    public double z;

    public Vector3d() {
    }

    public Vector3d(double arg0) {
        this.x = arg0;
        this.y = arg0;
        this.z = arg0;
    }

    public Vector3d(double arg0, double arg1, double arg2) {
        this.x = arg0;
        this.y = arg1;
        this.z = arg2;
    }

    public Vector3d(Vector3fc arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
    }

    public Vector3d(Vector3ic arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
    }

    public Vector3d(Vector2fc arg0, double arg1) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg1;
    }

    public Vector3d(Vector2ic arg0, double arg1) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg1;
    }

    public Vector3d(Vector3dc arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
    }

    public Vector3d(Vector2dc arg0, double arg1) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg1;
    }

    public Vector3d(double[] arg0) {
        this.x = arg0[0];
        this.y = arg0[1];
        this.z = arg0[2];
    }

    public Vector3d(float[] floats) {
        this.x = floats[0];
        this.y = floats[1];
        this.z = floats[2];
    }

    public Vector3d(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
    }

    public Vector3d(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
    }

    public Vector3d(DoubleBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
    }

    public Vector3d(int arg0, DoubleBuffer arg1) {
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

    public Vector3d set(Vector3dc arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        return this;
    }

    public Vector3d set(Vector3ic arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        return this;
    }

    public Vector3d set(Vector2dc arg0, double arg1) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg1;
        return this;
    }

    public Vector3d set(Vector2ic arg0, double arg1) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg1;
        return this;
    }

    public Vector3d set(Vector3fc arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        return this;
    }

    public Vector3d set(Vector2fc arg0, double arg1) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg1;
        return this;
    }

    public Vector3d set(double arg0) {
        this.x = arg0;
        this.y = arg0;
        this.z = arg0;
        return this;
    }

    public Vector3d set(double arg0, double arg1, double arg2) {
        this.x = arg0;
        this.y = arg1;
        this.z = arg2;
        return this;
    }

    public Vector3d set(double[] arg0) {
        this.x = arg0[0];
        this.y = arg0[1];
        this.z = arg0[2];
        return this;
    }

    public Vector3d set(float[] floats) {
        this.x = floats[0];
        this.y = floats[1];
        this.z = floats[2];
        return this;
    }

    public Vector3d set(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Vector3d set(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
        return this;
    }

    public Vector3d set(DoubleBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Vector3d set(int arg0, DoubleBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
        return this;
    }

    public Vector3d setFromAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.get(this, arg0);
            return this;
        }
    }

    public Vector3d setComponent(int arg0, double arg1) throws IllegalArgumentException {
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
    public Vector3dc getToAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.put(this, arg0);
            return this;
        }
    }

    public Vector3d sub(Vector3dc arg0) {
        this.x = this.x - arg0.x();
        this.y = this.y - arg0.y();
        this.z = this.z - arg0.z();
        return this;
    }

    @Override
    public Vector3d sub(Vector3dc arg0, Vector3d arg1) {
        arg1.x = this.x - arg0.x();
        arg1.y = this.y - arg0.y();
        arg1.z = this.z - arg0.z();
        return arg1;
    }

    public Vector3d sub(Vector3fc arg0) {
        this.x = this.x - arg0.x();
        this.y = this.y - arg0.y();
        this.z = this.z - arg0.z();
        return this;
    }

    @Override
    public Vector3d sub(Vector3fc arg0, Vector3d arg1) {
        arg1.x = this.x - arg0.x();
        arg1.y = this.y - arg0.y();
        arg1.z = this.z - arg0.z();
        return arg1;
    }

    public Vector3d sub(double arg0, double arg1, double arg2) {
        this.x -= arg0;
        this.y -= arg1;
        this.z -= arg2;
        return this;
    }

    @Override
    public Vector3d sub(double arg0, double arg1, double arg2, Vector3d arg3) {
        arg3.x = this.x - arg0;
        arg3.y = this.y - arg1;
        arg3.z = this.z - arg2;
        return arg3;
    }

    public Vector3d add(Vector3dc arg0) {
        this.x = this.x + arg0.x();
        this.y = this.y + arg0.y();
        this.z = this.z + arg0.z();
        return this;
    }

    @Override
    public Vector3d add(Vector3dc arg0, Vector3d arg1) {
        arg1.x = this.x + arg0.x();
        arg1.y = this.y + arg0.y();
        arg1.z = this.z + arg0.z();
        return arg1;
    }

    public Vector3d add(Vector3fc arg0) {
        this.x = this.x + arg0.x();
        this.y = this.y + arg0.y();
        this.z = this.z + arg0.z();
        return this;
    }

    @Override
    public Vector3d add(Vector3fc arg0, Vector3d arg1) {
        arg1.x = this.x + arg0.x();
        arg1.y = this.y + arg0.y();
        arg1.z = this.z + arg0.z();
        return arg1;
    }

    public Vector3d add(double arg0, double arg1, double arg2) {
        this.x += arg0;
        this.y += arg1;
        this.z += arg2;
        return this;
    }

    @Override
    public Vector3d add(double arg0, double arg1, double arg2, Vector3d arg3) {
        arg3.x = this.x + arg0;
        arg3.y = this.y + arg1;
        arg3.z = this.z + arg2;
        return arg3;
    }

    public Vector3d fma(Vector3dc arg0, Vector3dc arg1) {
        this.x = Math.fma(arg0.x(), arg1.x(), this.x);
        this.y = Math.fma(arg0.y(), arg1.y(), this.y);
        this.z = Math.fma(arg0.z(), arg1.z(), this.z);
        return this;
    }

    public Vector3d fma(double arg0, Vector3dc arg1) {
        this.x = Math.fma(arg0, arg1.x(), this.x);
        this.y = Math.fma(arg0, arg1.y(), this.y);
        this.z = Math.fma(arg0, arg1.z(), this.z);
        return this;
    }

    public Vector3d fma(Vector3fc arg0, Vector3fc arg1) {
        this.x = Math.fma((double)arg0.x(), (double)arg1.x(), this.x);
        this.y = Math.fma((double)arg0.y(), (double)arg1.y(), this.y);
        this.z = Math.fma((double)arg0.z(), (double)arg1.z(), this.z);
        return this;
    }

    @Override
    public Vector3d fma(Vector3fc arg0, Vector3fc arg1, Vector3d arg2) {
        arg2.x = Math.fma((double)arg0.x(), (double)arg1.x(), this.x);
        arg2.y = Math.fma((double)arg0.y(), (double)arg1.y(), this.y);
        arg2.z = Math.fma((double)arg0.z(), (double)arg1.z(), this.z);
        return arg2;
    }

    public Vector3d fma(double arg0, Vector3fc arg1) {
        this.x = Math.fma(arg0, (double)arg1.x(), this.x);
        this.y = Math.fma(arg0, (double)arg1.y(), this.y);
        this.z = Math.fma(arg0, (double)arg1.z(), this.z);
        return this;
    }

    @Override
    public Vector3d fma(Vector3dc arg0, Vector3dc arg1, Vector3d arg2) {
        arg2.x = Math.fma(arg0.x(), arg1.x(), this.x);
        arg2.y = Math.fma(arg0.y(), arg1.y(), this.y);
        arg2.z = Math.fma(arg0.z(), arg1.z(), this.z);
        return arg2;
    }

    @Override
    public Vector3d fma(double arg0, Vector3dc arg1, Vector3d arg2) {
        arg2.x = Math.fma(arg0, arg1.x(), this.x);
        arg2.y = Math.fma(arg0, arg1.y(), this.y);
        arg2.z = Math.fma(arg0, arg1.z(), this.z);
        return arg2;
    }

    @Override
    public Vector3d fma(Vector3dc arg0, Vector3fc arg1, Vector3d arg2) {
        arg2.x = Math.fma(arg0.x(), (double)arg1.x(), this.x);
        arg2.y = Math.fma(arg0.y(), (double)arg1.y(), this.y);
        arg2.z = Math.fma(arg0.z(), (double)arg1.z(), this.z);
        return arg2;
    }

    @Override
    public Vector3d fma(double arg0, Vector3fc arg1, Vector3d arg2) {
        arg2.x = Math.fma(arg0, (double)arg1.x(), this.x);
        arg2.y = Math.fma(arg0, (double)arg1.y(), this.y);
        arg2.z = Math.fma(arg0, (double)arg1.z(), this.z);
        return arg2;
    }

    public Vector3d mulAdd(Vector3dc arg0, Vector3dc arg1) {
        this.x = Math.fma(this.x, arg0.x(), arg1.x());
        this.y = Math.fma(this.y, arg0.y(), arg1.y());
        this.z = Math.fma(this.z, arg0.z(), arg1.z());
        return this;
    }

    public Vector3d mulAdd(double arg0, Vector3dc arg1) {
        this.x = Math.fma(this.x, arg0, arg1.x());
        this.y = Math.fma(this.y, arg0, arg1.y());
        this.z = Math.fma(this.z, arg0, arg1.z());
        return this;
    }

    @Override
    public Vector3d mulAdd(Vector3dc arg0, Vector3dc arg1, Vector3d arg2) {
        arg2.x = Math.fma(this.x, arg0.x(), arg1.x());
        arg2.y = Math.fma(this.y, arg0.y(), arg1.y());
        arg2.z = Math.fma(this.z, arg0.z(), arg1.z());
        return arg2;
    }

    @Override
    public Vector3d mulAdd(double arg0, Vector3dc arg1, Vector3d arg2) {
        arg2.x = Math.fma(this.x, arg0, arg1.x());
        arg2.y = Math.fma(this.y, arg0, arg1.y());
        arg2.z = Math.fma(this.z, arg0, arg1.z());
        return arg2;
    }

    @Override
    public Vector3d mulAdd(Vector3fc arg0, Vector3dc arg1, Vector3d arg2) {
        arg2.x = Math.fma(this.x, (double)arg0.x(), arg1.x());
        arg2.y = Math.fma(this.y, (double)arg0.y(), arg1.y());
        arg2.z = Math.fma(this.z, (double)arg0.z(), arg1.z());
        return arg2;
    }

    public Vector3d mul(Vector3dc arg0) {
        this.x = this.x * arg0.x();
        this.y = this.y * arg0.y();
        this.z = this.z * arg0.z();
        return this;
    }

    public Vector3d mul(Vector3fc arg0) {
        this.x = this.x * arg0.x();
        this.y = this.y * arg0.y();
        this.z = this.z * arg0.z();
        return this;
    }

    @Override
    public Vector3d mul(Vector3fc arg0, Vector3d arg1) {
        arg1.x = this.x * arg0.x();
        arg1.y = this.y * arg0.y();
        arg1.z = this.z * arg0.z();
        return arg1;
    }

    @Override
    public Vector3d mul(Vector3dc arg0, Vector3d arg1) {
        arg1.x = this.x * arg0.x();
        arg1.y = this.y * arg0.y();
        arg1.z = this.z * arg0.z();
        return arg1;
    }

    public Vector3d div(Vector3d arg0) {
        this.x = this.x / arg0.x();
        this.y = this.y / arg0.y();
        this.z = this.z / arg0.z();
        return this;
    }

    public Vector3d div(Vector3fc arg0) {
        this.x = this.x / arg0.x();
        this.y = this.y / arg0.y();
        this.z = this.z / arg0.z();
        return this;
    }

    @Override
    public Vector3d div(Vector3fc arg0, Vector3d arg1) {
        arg1.x = this.x / arg0.x();
        arg1.y = this.y / arg0.y();
        arg1.z = this.z / arg0.z();
        return arg1;
    }

    @Override
    public Vector3d div(Vector3dc arg0, Vector3d arg1) {
        arg1.x = this.x / arg0.x();
        arg1.y = this.y / arg0.y();
        arg1.z = this.z / arg0.z();
        return arg1;
    }

    @Override
    public Vector3d mulProject(Matrix4dc arg0, double arg1, Vector3d arg2) {
        double double0 = 1.0 / Math.fma(arg0.m03(), this.x, Math.fma(arg0.m13(), this.y, Math.fma(arg0.m23(), this.z, arg0.m33() * arg1)));
        double double1 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, Math.fma(arg0.m20(), this.z, arg0.m30() * arg1))) * double0;
        double double2 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, Math.fma(arg0.m21(), this.z, arg0.m31() * arg1))) * double0;
        double double3 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, Math.fma(arg0.m22(), this.z, arg0.m32() * arg1))) * double0;
        arg2.x = double1;
        arg2.y = double2;
        arg2.z = double3;
        return arg2;
    }

    @Override
    public Vector3d mulProject(Matrix4dc arg0, Vector3d arg1) {
        double double0 = 1.0 / Math.fma(arg0.m03(), this.x, Math.fma(arg0.m13(), this.y, Math.fma(arg0.m23(), this.z, arg0.m33())));
        double double1 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, Math.fma(arg0.m20(), this.z, arg0.m30()))) * double0;
        double double2 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, Math.fma(arg0.m21(), this.z, arg0.m31()))) * double0;
        double double3 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, Math.fma(arg0.m22(), this.z, arg0.m32()))) * double0;
        arg1.x = double1;
        arg1.y = double2;
        arg1.z = double3;
        return arg1;
    }

    public Vector3d mulProject(Matrix4dc arg0) {
        double double0 = 1.0 / Math.fma(arg0.m03(), this.x, Math.fma(arg0.m13(), this.y, Math.fma(arg0.m23(), this.z, arg0.m33())));
        double double1 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, Math.fma(arg0.m20(), this.z, arg0.m30()))) * double0;
        double double2 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, Math.fma(arg0.m21(), this.z, arg0.m31()))) * double0;
        double double3 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, Math.fma(arg0.m22(), this.z, arg0.m32()))) * double0;
        this.x = double1;
        this.y = double2;
        this.z = double3;
        return this;
    }

    @Override
    public Vector3d mulProject(Matrix4fc arg0, Vector3d arg1) {
        double double0 = 1.0
            / Math.fma((double)arg0.m03(), this.x, Math.fma((double)arg0.m13(), this.y, Math.fma((double)arg0.m23(), this.z, (double)arg0.m33())));
        double double1 = (arg0.m00() * this.x + arg0.m10() * this.y + arg0.m20() * this.z + arg0.m30()) * double0;
        double double2 = (arg0.m01() * this.x + arg0.m11() * this.y + arg0.m21() * this.z + arg0.m31()) * double0;
        double double3 = (arg0.m02() * this.x + arg0.m12() * this.y + arg0.m22() * this.z + arg0.m32()) * double0;
        arg1.x = double1;
        arg1.y = double2;
        arg1.z = double3;
        return arg1;
    }

    public Vector3d mulProject(Matrix4fc arg0) {
        double double0 = 1.0
            / Math.fma((double)arg0.m03(), this.x, Math.fma((double)arg0.m13(), this.y, Math.fma((double)arg0.m23(), this.z, (double)arg0.m33())));
        double double1 = (arg0.m00() * this.x + arg0.m10() * this.y + arg0.m20() * this.z + arg0.m30()) * double0;
        double double2 = (arg0.m01() * this.x + arg0.m11() * this.y + arg0.m21() * this.z + arg0.m31()) * double0;
        double double3 = (arg0.m02() * this.x + arg0.m12() * this.y + arg0.m22() * this.z + arg0.m32()) * double0;
        this.x = double1;
        this.y = double2;
        this.z = double3;
        return this;
    }

    public Vector3d mul(Matrix3fc arg0) {
        double double0 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m10(), this.y, arg0.m20() * this.z));
        double double1 = Math.fma((double)arg0.m01(), this.x, Math.fma((double)arg0.m11(), this.y, arg0.m21() * this.z));
        double double2 = Math.fma((double)arg0.m02(), this.x, Math.fma((double)arg0.m12(), this.y, arg0.m22() * this.z));
        this.x = double0;
        this.y = double1;
        this.z = double2;
        return this;
    }

    public Vector3d mul(Matrix3dc arg0) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, arg0.m20() * this.z));
        double double1 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, arg0.m21() * this.z));
        double double2 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, arg0.m22() * this.z));
        this.x = double0;
        this.y = double1;
        this.z = double2;
        return this;
    }

    @Override
    public Vector3d mul(Matrix3dc arg0, Vector3d arg1) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, arg0.m20() * this.z));
        double double1 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, arg0.m21() * this.z));
        double double2 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, arg0.m22() * this.z));
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = double2;
        return arg1;
    }

    @Override
    public Vector3f mul(Matrix3dc arg0, Vector3f arg1) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, arg0.m20() * this.z));
        double double1 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, arg0.m21() * this.z));
        double double2 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, arg0.m22() * this.z));
        arg1.x = (float)double0;
        arg1.y = (float)double1;
        arg1.z = (float)double2;
        return arg1;
    }

    @Override
    public Vector3d mul(Matrix3fc arg0, Vector3d arg1) {
        double double0 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m10(), this.y, arg0.m20() * this.z));
        double double1 = Math.fma((double)arg0.m01(), this.x, Math.fma((double)arg0.m11(), this.y, arg0.m21() * this.z));
        double double2 = Math.fma((double)arg0.m02(), this.x, Math.fma((double)arg0.m12(), this.y, arg0.m22() * this.z));
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = double2;
        return arg1;
    }

    public Vector3d mul(Matrix3x2dc arg0) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, arg0.m20() * this.z));
        double double1 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, arg0.m21() * this.z));
        this.x = double0;
        this.y = double1;
        return this;
    }

    @Override
    public Vector3d mul(Matrix3x2dc arg0, Vector3d arg1) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, arg0.m20() * this.z));
        double double1 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, arg0.m21() * this.z));
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = this.z;
        return arg1;
    }

    public Vector3d mul(Matrix3x2fc arg0) {
        double double0 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m10(), this.y, arg0.m20() * this.z));
        double double1 = Math.fma((double)arg0.m01(), this.x, Math.fma((double)arg0.m11(), this.y, arg0.m21() * this.z));
        this.x = double0;
        this.y = double1;
        return this;
    }

    @Override
    public Vector3d mul(Matrix3x2fc arg0, Vector3d arg1) {
        double double0 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m10(), this.y, arg0.m20() * this.z));
        double double1 = Math.fma((double)arg0.m01(), this.x, Math.fma((double)arg0.m11(), this.y, arg0.m21() * this.z));
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = this.z;
        return arg1;
    }

    public Vector3d mulTranspose(Matrix3dc arg0) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m01(), this.y, arg0.m02() * this.z));
        double double1 = Math.fma(arg0.m10(), this.x, Math.fma(arg0.m11(), this.y, arg0.m12() * this.z));
        double double2 = Math.fma(arg0.m20(), this.x, Math.fma(arg0.m21(), this.y, arg0.m22() * this.z));
        this.x = double0;
        this.y = double1;
        this.z = double2;
        return this;
    }

    @Override
    public Vector3d mulTranspose(Matrix3dc arg0, Vector3d arg1) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m01(), this.y, arg0.m02() * this.z));
        double double1 = Math.fma(arg0.m10(), this.x, Math.fma(arg0.m11(), this.y, arg0.m12() * this.z));
        double double2 = Math.fma(arg0.m20(), this.x, Math.fma(arg0.m21(), this.y, arg0.m22() * this.z));
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = double2;
        return arg1;
    }

    public Vector3d mulTranspose(Matrix3fc arg0) {
        double double0 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m01(), this.y, arg0.m02() * this.z));
        double double1 = Math.fma((double)arg0.m10(), this.x, Math.fma((double)arg0.m11(), this.y, arg0.m12() * this.z));
        double double2 = Math.fma((double)arg0.m20(), this.x, Math.fma((double)arg0.m21(), this.y, arg0.m22() * this.z));
        this.x = double0;
        this.y = double1;
        this.z = double2;
        return this;
    }

    @Override
    public Vector3d mulTranspose(Matrix3fc arg0, Vector3d arg1) {
        double double0 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m01(), this.y, arg0.m02() * this.z));
        double double1 = Math.fma((double)arg0.m10(), this.x, Math.fma((double)arg0.m11(), this.y, arg0.m12() * this.z));
        double double2 = Math.fma((double)arg0.m20(), this.x, Math.fma((double)arg0.m21(), this.y, arg0.m22() * this.z));
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = double2;
        return arg1;
    }

    public Vector3d mulPosition(Matrix4fc arg0) {
        double double0 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m10(), this.y, Math.fma((double)arg0.m20(), this.z, (double)arg0.m30())));
        double double1 = Math.fma((double)arg0.m01(), this.x, Math.fma((double)arg0.m11(), this.y, Math.fma((double)arg0.m21(), this.z, (double)arg0.m31())));
        double double2 = Math.fma((double)arg0.m02(), this.x, Math.fma((double)arg0.m12(), this.y, Math.fma((double)arg0.m22(), this.z, (double)arg0.m32())));
        this.x = double0;
        this.y = double1;
        this.z = double2;
        return this;
    }

    public Vector3d mulPosition(Matrix4dc arg0) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, Math.fma(arg0.m20(), this.z, arg0.m30())));
        double double1 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, Math.fma(arg0.m21(), this.z, arg0.m31())));
        double double2 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, Math.fma(arg0.m22(), this.z, arg0.m32())));
        this.x = double0;
        this.y = double1;
        this.z = double2;
        return this;
    }

    public Vector3d mulPosition(Matrix4x3dc arg0) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, Math.fma(arg0.m20(), this.z, arg0.m30())));
        double double1 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, Math.fma(arg0.m21(), this.z, arg0.m31())));
        double double2 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, Math.fma(arg0.m22(), this.z, arg0.m32())));
        this.x = double0;
        this.y = double1;
        this.z = double2;
        return this;
    }

    public Vector3d mulPosition(Matrix4x3fc arg0) {
        double double0 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m10(), this.y, Math.fma((double)arg0.m20(), this.z, (double)arg0.m30())));
        double double1 = Math.fma((double)arg0.m01(), this.x, Math.fma((double)arg0.m11(), this.y, Math.fma((double)arg0.m21(), this.z, (double)arg0.m31())));
        double double2 = Math.fma((double)arg0.m02(), this.x, Math.fma((double)arg0.m12(), this.y, Math.fma((double)arg0.m22(), this.z, (double)arg0.m32())));
        this.x = double0;
        this.y = double1;
        this.z = double2;
        return this;
    }

    @Override
    public Vector3d mulPosition(Matrix4dc arg0, Vector3d arg1) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, Math.fma(arg0.m20(), this.z, arg0.m30())));
        double double1 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, Math.fma(arg0.m21(), this.z, arg0.m31())));
        double double2 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, Math.fma(arg0.m22(), this.z, arg0.m32())));
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = double2;
        return arg1;
    }

    @Override
    public Vector3d mulPosition(Matrix4fc arg0, Vector3d arg1) {
        double double0 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m10(), this.y, Math.fma((double)arg0.m20(), this.z, (double)arg0.m30())));
        double double1 = Math.fma((double)arg0.m01(), this.x, Math.fma((double)arg0.m11(), this.y, Math.fma((double)arg0.m21(), this.z, (double)arg0.m31())));
        double double2 = Math.fma((double)arg0.m02(), this.x, Math.fma((double)arg0.m12(), this.y, Math.fma((double)arg0.m22(), this.z, (double)arg0.m32())));
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = double2;
        return arg1;
    }

    @Override
    public Vector3d mulPosition(Matrix4x3dc arg0, Vector3d arg1) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, Math.fma(arg0.m20(), this.z, arg0.m30())));
        double double1 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, Math.fma(arg0.m21(), this.z, arg0.m31())));
        double double2 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, Math.fma(arg0.m22(), this.z, arg0.m32())));
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = double2;
        return arg1;
    }

    @Override
    public Vector3d mulPosition(Matrix4x3fc arg0, Vector3d arg1) {
        double double0 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m10(), this.y, Math.fma((double)arg0.m20(), this.z, (double)arg0.m30())));
        double double1 = Math.fma((double)arg0.m01(), this.x, Math.fma((double)arg0.m11(), this.y, Math.fma((double)arg0.m21(), this.z, (double)arg0.m31())));
        double double2 = Math.fma((double)arg0.m02(), this.x, Math.fma((double)arg0.m12(), this.y, Math.fma((double)arg0.m22(), this.z, (double)arg0.m32())));
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = double2;
        return arg1;
    }

    public Vector3d mulTransposePosition(Matrix4dc arg0) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m01(), this.y, Math.fma(arg0.m02(), this.z, arg0.m03())));
        double double1 = Math.fma(arg0.m10(), this.x, Math.fma(arg0.m11(), this.y, Math.fma(arg0.m12(), this.z, arg0.m13())));
        double double2 = Math.fma(arg0.m20(), this.x, Math.fma(arg0.m21(), this.y, Math.fma(arg0.m22(), this.z, arg0.m23())));
        this.x = double0;
        this.y = double1;
        this.z = double2;
        return this;
    }

    @Override
    public Vector3d mulTransposePosition(Matrix4dc arg0, Vector3d arg1) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m01(), this.y, Math.fma(arg0.m02(), this.z, arg0.m03())));
        double double1 = Math.fma(arg0.m10(), this.x, Math.fma(arg0.m11(), this.y, Math.fma(arg0.m12(), this.z, arg0.m13())));
        double double2 = Math.fma(arg0.m20(), this.x, Math.fma(arg0.m21(), this.y, Math.fma(arg0.m22(), this.z, arg0.m23())));
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = double2;
        return arg1;
    }

    public Vector3d mulTransposePosition(Matrix4fc arg0) {
        double double0 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m01(), this.y, Math.fma((double)arg0.m02(), this.z, (double)arg0.m03())));
        double double1 = Math.fma((double)arg0.m10(), this.x, Math.fma((double)arg0.m11(), this.y, Math.fma((double)arg0.m12(), this.z, (double)arg0.m13())));
        double double2 = Math.fma((double)arg0.m20(), this.x, Math.fma((double)arg0.m21(), this.y, Math.fma((double)arg0.m22(), this.z, (double)arg0.m23())));
        this.x = double0;
        this.y = double1;
        this.z = double2;
        return this;
    }

    @Override
    public Vector3d mulTransposePosition(Matrix4fc arg0, Vector3d arg1) {
        double double0 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m01(), this.y, Math.fma((double)arg0.m02(), this.z, (double)arg0.m03())));
        double double1 = Math.fma((double)arg0.m10(), this.x, Math.fma((double)arg0.m11(), this.y, Math.fma((double)arg0.m12(), this.z, (double)arg0.m13())));
        double double2 = Math.fma((double)arg0.m20(), this.x, Math.fma((double)arg0.m21(), this.y, Math.fma((double)arg0.m22(), this.z, (double)arg0.m23())));
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = double2;
        return arg1;
    }

    public double mulPositionW(Matrix4fc arg0) {
        double double0 = Math.fma((double)arg0.m03(), this.x, Math.fma((double)arg0.m13(), this.y, Math.fma((double)arg0.m23(), this.z, (double)arg0.m33())));
        double double1 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m10(), this.y, Math.fma((double)arg0.m20(), this.z, (double)arg0.m30())));
        double double2 = Math.fma((double)arg0.m01(), this.x, Math.fma((double)arg0.m11(), this.y, Math.fma((double)arg0.m21(), this.z, (double)arg0.m31())));
        double double3 = Math.fma((double)arg0.m02(), this.x, Math.fma((double)arg0.m12(), this.y, Math.fma((double)arg0.m22(), this.z, (double)arg0.m32())));
        this.x = double1;
        this.y = double2;
        this.z = double3;
        return double0;
    }

    @Override
    public double mulPositionW(Matrix4fc arg0, Vector3d arg1) {
        double double0 = Math.fma((double)arg0.m03(), this.x, Math.fma((double)arg0.m13(), this.y, Math.fma((double)arg0.m23(), this.z, (double)arg0.m33())));
        double double1 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m10(), this.y, Math.fma((double)arg0.m20(), this.z, (double)arg0.m30())));
        double double2 = Math.fma((double)arg0.m01(), this.x, Math.fma((double)arg0.m11(), this.y, Math.fma((double)arg0.m21(), this.z, (double)arg0.m31())));
        double double3 = Math.fma((double)arg0.m02(), this.x, Math.fma((double)arg0.m12(), this.y, Math.fma((double)arg0.m22(), this.z, (double)arg0.m32())));
        arg1.x = double1;
        arg1.y = double2;
        arg1.z = double3;
        return double0;
    }

    public double mulPositionW(Matrix4dc arg0) {
        double double0 = Math.fma(arg0.m03(), this.x, Math.fma(arg0.m13(), this.y, Math.fma(arg0.m23(), this.z, arg0.m33())));
        double double1 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, Math.fma(arg0.m20(), this.z, arg0.m30())));
        double double2 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, Math.fma(arg0.m21(), this.z, arg0.m31())));
        double double3 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, Math.fma(arg0.m22(), this.z, arg0.m32())));
        this.x = double1;
        this.y = double2;
        this.z = double3;
        return double0;
    }

    @Override
    public double mulPositionW(Matrix4dc arg0, Vector3d arg1) {
        double double0 = Math.fma(arg0.m03(), this.x, Math.fma(arg0.m13(), this.y, Math.fma(arg0.m23(), this.z, arg0.m33())));
        double double1 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, Math.fma(arg0.m20(), this.z, arg0.m30())));
        double double2 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, Math.fma(arg0.m21(), this.z, arg0.m31())));
        double double3 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, Math.fma(arg0.m22(), this.z, arg0.m32())));
        arg1.x = double1;
        arg1.y = double2;
        arg1.z = double3;
        return double0;
    }

    public Vector3d mulDirection(Matrix4fc arg0) {
        double double0 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m10(), this.y, arg0.m20() * this.z));
        double double1 = Math.fma((double)arg0.m01(), this.x, Math.fma((double)arg0.m11(), this.y, arg0.m21() * this.z));
        double double2 = Math.fma((double)arg0.m02(), this.x, Math.fma((double)arg0.m12(), this.y, arg0.m22() * this.z));
        this.x = double0;
        this.y = double1;
        this.z = double2;
        return this;
    }

    public Vector3d mulDirection(Matrix4dc arg0) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, arg0.m20() * this.z));
        double double1 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, arg0.m21() * this.z));
        double double2 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, arg0.m22() * this.z));
        this.x = double0;
        this.y = double1;
        this.z = double2;
        return this;
    }

    public Vector3d mulDirection(Matrix4x3dc arg0) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, arg0.m20() * this.z));
        double double1 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, arg0.m21() * this.z));
        double double2 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, arg0.m22() * this.z));
        this.x = double0;
        this.y = double1;
        this.z = double2;
        return this;
    }

    public Vector3d mulDirection(Matrix4x3fc arg0) {
        double double0 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m10(), this.y, arg0.m20() * this.z));
        double double1 = Math.fma((double)arg0.m01(), this.x, Math.fma((double)arg0.m11(), this.y, arg0.m21() * this.z));
        double double2 = Math.fma((double)arg0.m02(), this.x, Math.fma((double)arg0.m12(), this.y, arg0.m22() * this.z));
        this.x = double0;
        this.y = double1;
        this.z = double2;
        return this;
    }

    @Override
    public Vector3d mulDirection(Matrix4dc arg0, Vector3d arg1) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, arg0.m20() * this.z));
        double double1 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, arg0.m21() * this.z));
        double double2 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, arg0.m22() * this.z));
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = double2;
        return arg1;
    }

    @Override
    public Vector3d mulDirection(Matrix4fc arg0, Vector3d arg1) {
        double double0 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m10(), this.y, arg0.m20() * this.z));
        double double1 = Math.fma((double)arg0.m01(), this.x, Math.fma((double)arg0.m11(), this.y, arg0.m21() * this.z));
        double double2 = Math.fma((double)arg0.m02(), this.x, Math.fma((double)arg0.m12(), this.y, arg0.m22() * this.z));
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = double2;
        return arg1;
    }

    @Override
    public Vector3d mulDirection(Matrix4x3dc arg0, Vector3d arg1) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m10(), this.y, arg0.m20() * this.z));
        double double1 = Math.fma(arg0.m01(), this.x, Math.fma(arg0.m11(), this.y, arg0.m21() * this.z));
        double double2 = Math.fma(arg0.m02(), this.x, Math.fma(arg0.m12(), this.y, arg0.m22() * this.z));
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = double2;
        return arg1;
    }

    @Override
    public Vector3d mulDirection(Matrix4x3fc arg0, Vector3d arg1) {
        double double0 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m10(), this.y, arg0.m20() * this.z));
        double double1 = Math.fma((double)arg0.m01(), this.x, Math.fma((double)arg0.m11(), this.y, arg0.m21() * this.z));
        double double2 = Math.fma((double)arg0.m02(), this.x, Math.fma((double)arg0.m12(), this.y, arg0.m22() * this.z));
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = double2;
        return arg1;
    }

    public Vector3d mulTransposeDirection(Matrix4dc arg0) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m01(), this.y, arg0.m02() * this.z));
        double double1 = Math.fma(arg0.m10(), this.x, Math.fma(arg0.m11(), this.y, arg0.m12() * this.z));
        double double2 = Math.fma(arg0.m20(), this.x, Math.fma(arg0.m21(), this.y, arg0.m22() * this.z));
        this.x = double0;
        this.y = double1;
        this.z = double2;
        return this;
    }

    @Override
    public Vector3d mulTransposeDirection(Matrix4dc arg0, Vector3d arg1) {
        double double0 = Math.fma(arg0.m00(), this.x, Math.fma(arg0.m01(), this.y, arg0.m02() * this.z));
        double double1 = Math.fma(arg0.m10(), this.x, Math.fma(arg0.m11(), this.y, arg0.m12() * this.z));
        double double2 = Math.fma(arg0.m20(), this.x, Math.fma(arg0.m21(), this.y, arg0.m22() * this.z));
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = double2;
        return arg1;
    }

    public Vector3d mulTransposeDirection(Matrix4fc arg0) {
        double double0 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m01(), this.y, arg0.m02() * this.z));
        double double1 = Math.fma((double)arg0.m10(), this.x, Math.fma((double)arg0.m11(), this.y, arg0.m12() * this.z));
        double double2 = Math.fma((double)arg0.m20(), this.x, Math.fma((double)arg0.m21(), this.y, arg0.m22() * this.z));
        this.x = double0;
        this.y = double1;
        this.z = double2;
        return this;
    }

    @Override
    public Vector3d mulTransposeDirection(Matrix4fc arg0, Vector3d arg1) {
        double double0 = Math.fma((double)arg0.m00(), this.x, Math.fma((double)arg0.m01(), this.y, arg0.m02() * this.z));
        double double1 = Math.fma((double)arg0.m10(), this.x, Math.fma((double)arg0.m11(), this.y, arg0.m12() * this.z));
        double double2 = Math.fma((double)arg0.m20(), this.x, Math.fma((double)arg0.m21(), this.y, arg0.m22() * this.z));
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = double2;
        return arg1;
    }

    public Vector3d mul(double arg0) {
        this.x *= arg0;
        this.y *= arg0;
        this.z *= arg0;
        return this;
    }

    @Override
    public Vector3d mul(double arg0, Vector3d arg1) {
        arg1.x = this.x * arg0;
        arg1.y = this.y * arg0;
        arg1.z = this.z * arg0;
        return arg1;
    }

    public Vector3d mul(double arg0, double arg1, double arg2) {
        this.x *= arg0;
        this.y *= arg1;
        this.z *= arg2;
        return this;
    }

    @Override
    public Vector3d mul(double arg0, double arg1, double arg2, Vector3d arg3) {
        arg3.x = this.x * arg0;
        arg3.y = this.y * arg1;
        arg3.z = this.z * arg2;
        return arg3;
    }

    public Vector3d rotate(Quaterniondc arg0) {
        return arg0.transform(this, this);
    }

    @Override
    public Vector3d rotate(Quaterniondc arg0, Vector3d arg1) {
        return arg0.transform(this, arg1);
    }

    @Override
    public Quaterniond rotationTo(Vector3dc arg0, Quaterniond arg1) {
        return arg1.rotationTo(this, arg0);
    }

    @Override
    public Quaterniond rotationTo(double arg0, double arg1, double arg2, Quaterniond arg3) {
        return arg3.rotationTo(this.x, this.y, this.z, arg0, arg1, arg2);
    }

    public Vector3d rotateAxis(double arg0, double arg1, double arg2, double arg3) {
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
    public Vector3d rotateAxis(double arg0, double arg1, double arg2, double arg3, Vector3d arg4) {
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

    private Vector3d rotateAxisInternal(double double1, double double4, double double6, double double8, Vector3d vector3d1) {
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
        vector3d1.x = double20;
        vector3d1.y = double21;
        vector3d1.z = double22;
        return vector3d1;
    }

    public Vector3d rotateX(double arg0) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = this.y * double1 - this.z * double0;
        double double3 = this.y * double0 + this.z * double1;
        this.y = double2;
        this.z = double3;
        return this;
    }

    @Override
    public Vector3d rotateX(double arg0, Vector3d arg1) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = this.y * double1 - this.z * double0;
        double double3 = this.y * double0 + this.z * double1;
        arg1.x = this.x;
        arg1.y = double2;
        arg1.z = double3;
        return arg1;
    }

    public Vector3d rotateY(double arg0) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = this.x * double1 + this.z * double0;
        double double3 = -this.x * double0 + this.z * double1;
        this.x = double2;
        this.z = double3;
        return this;
    }

    @Override
    public Vector3d rotateY(double arg0, Vector3d arg1) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = this.x * double1 + this.z * double0;
        double double3 = -this.x * double0 + this.z * double1;
        arg1.x = double2;
        arg1.y = this.y;
        arg1.z = double3;
        return arg1;
    }

    public Vector3d rotateZ(double arg0) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = this.x * double1 - this.y * double0;
        double double3 = this.x * double0 + this.y * double1;
        this.x = double2;
        this.y = double3;
        return this;
    }

    @Override
    public Vector3d rotateZ(double arg0, Vector3d arg1) {
        double double0 = Math.sin(arg0);
        double double1 = Math.cosFromSin(double0, arg0);
        double double2 = this.x * double1 - this.y * double0;
        double double3 = this.x * double0 + this.y * double1;
        arg1.x = double2;
        arg1.y = double3;
        arg1.z = this.z;
        return arg1;
    }

    public Vector3d div(double arg0) {
        double double0 = 1.0 / arg0;
        this.x *= double0;
        this.y *= double0;
        this.z *= double0;
        return this;
    }

    @Override
    public Vector3d div(double arg0, Vector3d arg1) {
        double double0 = 1.0 / arg0;
        arg1.x = this.x * double0;
        arg1.y = this.y * double0;
        arg1.z = this.z * double0;
        return arg1;
    }

    public Vector3d div(double arg0, double arg1, double arg2) {
        this.x /= arg0;
        this.y /= arg1;
        this.z /= arg2;
        return this;
    }

    @Override
    public Vector3d div(double arg0, double arg1, double arg2, Vector3d arg3) {
        arg3.x = this.x / arg0;
        arg3.y = this.y / arg1;
        arg3.z = this.z / arg2;
        return arg3;
    }

    @Override
    public double lengthSquared() {
        return Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z));
    }

    public static double lengthSquared(double arg0, double arg1, double arg2) {
        return Math.fma(arg0, arg0, Math.fma(arg1, arg1, arg2 * arg2));
    }

    @Override
    public double length() {
        return Math.sqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
    }

    public static double length(double arg0, double arg1, double arg2) {
        return Math.sqrt(Math.fma(arg0, arg0, Math.fma(arg1, arg1, arg2 * arg2)));
    }

    public Vector3d normalize() {
        double double0 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
        this.x *= double0;
        this.y *= double0;
        this.z *= double0;
        return this;
    }

    @Override
    public Vector3d normalize(Vector3d arg0) {
        double double0 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
        arg0.x = this.x * double0;
        arg0.y = this.y * double0;
        arg0.z = this.z * double0;
        return arg0;
    }

    public Vector3d normalize(double arg0) {
        double double0 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z))) * arg0;
        this.x *= double0;
        this.y *= double0;
        this.z *= double0;
        return this;
    }

    @Override
    public Vector3d normalize(double arg0, Vector3d arg1) {
        double double0 = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z))) * arg0;
        arg1.x = this.x * double0;
        arg1.y = this.y * double0;
        arg1.z = this.z * double0;
        return arg1;
    }

    public Vector3d cross(Vector3dc arg0) {
        double double0 = Math.fma(this.y, arg0.z(), -this.z * arg0.y());
        double double1 = Math.fma(this.z, arg0.x(), -this.x * arg0.z());
        double double2 = Math.fma(this.x, arg0.y(), -this.y * arg0.x());
        this.x = double0;
        this.y = double1;
        this.z = double2;
        return this;
    }

    public Vector3d cross(double arg0, double arg1, double arg2) {
        double double0 = Math.fma(this.y, arg2, -this.z * arg1);
        double double1 = Math.fma(this.z, arg0, -this.x * arg2);
        double double2 = Math.fma(this.x, arg1, -this.y * arg0);
        this.x = double0;
        this.y = double1;
        this.z = double2;
        return this;
    }

    @Override
    public Vector3d cross(Vector3dc arg0, Vector3d arg1) {
        double double0 = Math.fma(this.y, arg0.z(), -this.z * arg0.y());
        double double1 = Math.fma(this.z, arg0.x(), -this.x * arg0.z());
        double double2 = Math.fma(this.x, arg0.y(), -this.y * arg0.x());
        arg1.x = double0;
        arg1.y = double1;
        arg1.z = double2;
        return arg1;
    }

    @Override
    public Vector3d cross(double arg0, double arg1, double arg2, Vector3d arg3) {
        double double0 = Math.fma(this.y, arg2, -this.z * arg1);
        double double1 = Math.fma(this.z, arg0, -this.x * arg2);
        double double2 = Math.fma(this.x, arg1, -this.y * arg0);
        arg3.x = double0;
        arg3.y = double1;
        arg3.z = double2;
        return arg3;
    }

    @Override
    public double distance(Vector3dc arg0) {
        double double0 = this.x - arg0.x();
        double double1 = this.y - arg0.y();
        double double2 = this.z - arg0.z();
        return Math.sqrt(Math.fma(double0, double0, Math.fma(double1, double1, double2 * double2)));
    }

    @Override
    public double distance(double arg0, double arg1, double arg2) {
        double double0 = this.x - arg0;
        double double1 = this.y - arg1;
        double double2 = this.z - arg2;
        return Math.sqrt(Math.fma(double0, double0, Math.fma(double1, double1, double2 * double2)));
    }

    @Override
    public double distanceSquared(Vector3dc arg0) {
        double double0 = this.x - arg0.x();
        double double1 = this.y - arg0.y();
        double double2 = this.z - arg0.z();
        return Math.fma(double0, double0, Math.fma(double1, double1, double2 * double2));
    }

    @Override
    public double distanceSquared(double arg0, double arg1, double arg2) {
        double double0 = this.x - arg0;
        double double1 = this.y - arg1;
        double double2 = this.z - arg2;
        return Math.fma(double0, double0, Math.fma(double1, double1, double2 * double2));
    }

    public static double distance(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return Math.sqrt(distanceSquared(arg0, arg1, arg2, arg3, arg4, arg5));
    }

    public static double distanceSquared(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        double double0 = arg0 - arg3;
        double double1 = arg1 - arg4;
        double double2 = arg2 - arg5;
        return Math.fma(double0, double0, Math.fma(double1, double1, double2 * double2));
    }

    @Override
    public double dot(Vector3dc arg0) {
        return Math.fma(this.x, arg0.x(), Math.fma(this.y, arg0.y(), this.z * arg0.z()));
    }

    @Override
    public double dot(double arg0, double arg1, double arg2) {
        return Math.fma(this.x, arg0, Math.fma(this.y, arg1, this.z * arg2));
    }

    @Override
    public double angleCos(Vector3dc arg0) {
        double double0 = Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z));
        double double1 = Math.fma(arg0.x(), arg0.x(), Math.fma(arg0.y(), arg0.y(), arg0.z() * arg0.z()));
        double double2 = Math.fma(this.x, arg0.x(), Math.fma(this.y, arg0.y(), this.z * arg0.z()));
        return double2 / Math.sqrt(double0 * double1);
    }

    @Override
    public double angle(Vector3dc arg0) {
        double double0 = this.angleCos(arg0);
        double0 = double0 < 1.0 ? double0 : 1.0;
        double0 = double0 > -1.0 ? double0 : -1.0;
        return Math.acos(double0);
    }

    @Override
    public double angleSigned(Vector3dc arg0, Vector3dc arg1) {
        double double0 = arg0.x();
        double double1 = arg0.y();
        double double2 = arg0.z();
        return Math.atan2(
            (this.y * double2 - this.z * double1) * arg1.x()
                + (this.z * double0 - this.x * double2) * arg1.y()
                + (this.x * double1 - this.y * double0) * arg1.z(),
            this.x * double0 + this.y * double1 + this.z * double2
        );
    }

    @Override
    public double angleSigned(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) {
        return Math.atan2(
            (this.y * arg2 - this.z * arg1) * arg3 + (this.z * arg0 - this.x * arg2) * arg4 + (this.x * arg1 - this.y * arg0) * arg5,
            this.x * arg0 + this.y * arg1 + this.z * arg2
        );
    }

    public Vector3d min(Vector3dc arg0) {
        this.x = this.x < arg0.x() ? this.x : arg0.x();
        this.y = this.y < arg0.y() ? this.y : arg0.y();
        this.z = this.z < arg0.z() ? this.z : arg0.z();
        return this;
    }

    @Override
    public Vector3d min(Vector3dc arg0, Vector3d arg1) {
        arg1.x = this.x < arg0.x() ? this.x : arg0.x();
        arg1.y = this.y < arg0.y() ? this.y : arg0.y();
        arg1.z = this.z < arg0.z() ? this.z : arg0.z();
        return arg1;
    }

    public Vector3d max(Vector3dc arg0) {
        this.x = this.x > arg0.x() ? this.x : arg0.x();
        this.y = this.y > arg0.y() ? this.y : arg0.y();
        this.z = this.z > arg0.z() ? this.z : arg0.z();
        return this;
    }

    @Override
    public Vector3d max(Vector3dc arg0, Vector3d arg1) {
        arg1.x = this.x > arg0.x() ? this.x : arg0.x();
        arg1.y = this.y > arg0.y() ? this.y : arg0.y();
        arg1.z = this.z > arg0.z() ? this.z : arg0.z();
        return arg1;
    }

    public Vector3d zero() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        return this;
    }

    @Override
    public String toString() {
        return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
    }

    public String toString(NumberFormat numberFormat) {
        return "(" + Runtime.format(this.x, numberFormat) + " " + Runtime.format(this.y, numberFormat) + " " + Runtime.format(this.z, numberFormat) + ")";
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        arg0.writeDouble(this.x);
        arg0.writeDouble(this.y);
        arg0.writeDouble(this.z);
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        this.x = arg0.readDouble();
        this.y = arg0.readDouble();
        this.z = arg0.readDouble();
    }

    public Vector3d negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    @Override
    public Vector3d negate(Vector3d arg0) {
        arg0.x = -this.x;
        arg0.y = -this.y;
        arg0.z = -this.z;
        return arg0;
    }

    public Vector3d absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
        return this;
    }

    @Override
    public Vector3d absolute(Vector3d arg0) {
        arg0.x = Math.abs(this.x);
        arg0.y = Math.abs(this.y);
        arg0.z = Math.abs(this.z);
        return arg0;
    }

    @Override
    public int hashCode() {
        int int0 = 1;
        long long0 = Double.doubleToLongBits(this.x);
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
            Vector3d vector3d = (Vector3d)arg0;
            if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(vector3d.x)) {
                return false;
            } else {
                return Double.doubleToLongBits(this.y) != Double.doubleToLongBits(vector3d.y)
                    ? false
                    : Double.doubleToLongBits(this.z) == Double.doubleToLongBits(vector3d.z);
            }
        }
    }

    @Override
    public boolean equals(Vector3dc arg0, double arg1) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (!(arg0 instanceof Vector3dc)) {
            return false;
        } else if (!Runtime.equals(this.x, arg0.x(), arg1)) {
            return false;
        } else {
            return !Runtime.equals(this.y, arg0.y(), arg1) ? false : Runtime.equals(this.z, arg0.z(), arg1);
        }
    }

    @Override
    public boolean equals(double arg0, double arg1, double arg2) {
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(arg0)) {
            return false;
        } else {
            return Double.doubleToLongBits(this.y) != Double.doubleToLongBits(arg1) ? false : Double.doubleToLongBits(this.z) == Double.doubleToLongBits(arg2);
        }
    }

    public Vector3d reflect(Vector3dc arg0) {
        double double0 = arg0.x();
        double double1 = arg0.y();
        double double2 = arg0.z();
        double double3 = Math.fma(this.x, double0, Math.fma(this.y, double1, this.z * double2));
        this.x -= (double3 + double3) * double0;
        this.y -= (double3 + double3) * double1;
        this.z -= (double3 + double3) * double2;
        return this;
    }

    public Vector3d reflect(double arg0, double arg1, double arg2) {
        double double0 = Math.fma(this.x, arg0, Math.fma(this.y, arg1, this.z * arg2));
        this.x -= (double0 + double0) * arg0;
        this.y -= (double0 + double0) * arg1;
        this.z -= (double0 + double0) * arg2;
        return this;
    }

    @Override
    public Vector3d reflect(Vector3dc arg0, Vector3d arg1) {
        double double0 = arg0.x();
        double double1 = arg0.y();
        double double2 = arg0.z();
        double double3 = Math.fma(this.x, double0, Math.fma(this.y, double1, this.z * double2));
        arg1.x = this.x - (double3 + double3) * double0;
        arg1.y = this.y - (double3 + double3) * double1;
        arg1.z = this.z - (double3 + double3) * double2;
        return arg1;
    }

    @Override
    public Vector3d reflect(double arg0, double arg1, double arg2, Vector3d arg3) {
        double double0 = Math.fma(this.x, arg0, Math.fma(this.y, arg1, this.z * arg2));
        arg3.x = this.x - (double0 + double0) * arg0;
        arg3.y = this.y - (double0 + double0) * arg1;
        arg3.z = this.z - (double0 + double0) * arg2;
        return arg3;
    }

    public Vector3d half(Vector3dc arg0) {
        return this.set(this).add(arg0.x(), arg0.y(), arg0.z()).normalize();
    }

    public Vector3d half(double arg0, double arg1, double arg2) {
        return this.set(this).add(arg0, arg1, arg2).normalize();
    }

    @Override
    public Vector3d half(Vector3dc arg0, Vector3d arg1) {
        return arg1.set(this).add(arg0.x(), arg0.y(), arg0.z()).normalize();
    }

    @Override
    public Vector3d half(double arg0, double arg1, double arg2, Vector3d arg3) {
        return arg3.set(this).add(arg0, arg1, arg2).normalize();
    }

    @Override
    public Vector3d smoothStep(Vector3dc arg0, double arg1, Vector3d arg2) {
        double double0 = arg1 * arg1;
        double double1 = double0 * arg1;
        arg2.x = (this.x + this.x - arg0.x() - arg0.x()) * double1 + (3.0 * arg0.x() - 3.0 * this.x) * double0 + this.x * arg1 + this.x;
        arg2.y = (this.y + this.y - arg0.y() - arg0.y()) * double1 + (3.0 * arg0.y() - 3.0 * this.y) * double0 + this.y * arg1 + this.y;
        arg2.z = (this.z + this.z - arg0.z() - arg0.z()) * double1 + (3.0 * arg0.z() - 3.0 * this.z) * double0 + this.z * arg1 + this.z;
        return arg2;
    }

    @Override
    public Vector3d hermite(Vector3dc arg0, Vector3dc arg1, Vector3dc arg2, double arg3, Vector3d arg4) {
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
        return arg4;
    }

    public Vector3d lerp(Vector3dc arg0, double arg1) {
        this.x = Math.fma(arg0.x() - this.x, arg1, this.x);
        this.y = Math.fma(arg0.y() - this.y, arg1, this.y);
        this.z = Math.fma(arg0.z() - this.z, arg1, this.z);
        return this;
    }

    @Override
    public Vector3d lerp(Vector3dc arg0, double arg1, Vector3d arg2) {
        arg2.x = Math.fma(arg0.x() - this.x, arg1, this.x);
        arg2.y = Math.fma(arg0.y() - this.y, arg1, this.y);
        arg2.z = Math.fma(arg0.z() - this.z, arg1, this.z);
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
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public Vector3i get(int arg0, Vector3i arg1) {
        arg1.x = Math.roundUsing(this.x(), arg0);
        arg1.y = Math.roundUsing(this.y(), arg0);
        arg1.z = Math.roundUsing(this.z(), arg0);
        return arg1;
    }

    @Override
    public Vector3f get(Vector3f arg0) {
        arg0.x = (float)this.x();
        arg0.y = (float)this.y();
        arg0.z = (float)this.z();
        return arg0;
    }

    @Override
    public Vector3d get(Vector3d arg0) {
        arg0.x = this.x();
        arg0.y = this.y();
        arg0.z = this.z();
        return arg0;
    }

    @Override
    public int maxComponent() {
        double double0 = Math.abs(this.x);
        double double1 = Math.abs(this.y);
        double double2 = Math.abs(this.z);
        if (double0 >= double1 && double0 >= double2) {
            return 0;
        } else {
            return double1 >= double2 ? 1 : 2;
        }
    }

    @Override
    public int minComponent() {
        double double0 = Math.abs(this.x);
        double double1 = Math.abs(this.y);
        double double2 = Math.abs(this.z);
        if (double0 < double1 && double0 < double2) {
            return 0;
        } else {
            return double1 < double2 ? 1 : 2;
        }
    }

    @Override
    public Vector3d orthogonalize(Vector3dc arg0, Vector3d arg1) {
        double double0;
        double double1;
        double double2;
        if (Math.abs(arg0.x()) > Math.abs(arg0.z())) {
            double0 = -arg0.y();
            double1 = arg0.x();
            double2 = 0.0;
        } else {
            double0 = 0.0;
            double1 = -arg0.z();
            double2 = arg0.y();
        }

        double double3 = Math.invsqrt(double0 * double0 + double1 * double1 + double2 * double2);
        arg1.x = double0 * double3;
        arg1.y = double1 * double3;
        arg1.z = double2 * double3;
        return arg1;
    }

    public Vector3d orthogonalize(Vector3dc arg0) {
        return this.orthogonalize(arg0, this);
    }

    @Override
    public Vector3d orthogonalizeUnit(Vector3dc arg0, Vector3d arg1) {
        return this.orthogonalize(arg0, arg1);
    }

    public Vector3d orthogonalizeUnit(Vector3dc arg0) {
        return this.orthogonalizeUnit(arg0, this);
    }

    public Vector3d floor() {
        this.x = Math.floor(this.x);
        this.y = Math.floor(this.y);
        this.z = Math.floor(this.z);
        return this;
    }

    @Override
    public Vector3d floor(Vector3d arg0) {
        arg0.x = Math.floor(this.x);
        arg0.y = Math.floor(this.y);
        arg0.z = Math.floor(this.z);
        return arg0;
    }

    public Vector3d ceil() {
        this.x = Math.ceil(this.x);
        this.y = Math.ceil(this.y);
        this.z = Math.ceil(this.z);
        return this;
    }

    @Override
    public Vector3d ceil(Vector3d arg0) {
        arg0.x = Math.ceil(this.x);
        arg0.y = Math.ceil(this.y);
        arg0.z = Math.ceil(this.z);
        return arg0;
    }

    public Vector3d round() {
        this.x = Math.round(this.x);
        this.y = Math.round(this.y);
        this.z = Math.round(this.z);
        return this;
    }

    @Override
    public Vector3d round(Vector3d arg0) {
        arg0.x = Math.round(this.x);
        arg0.y = Math.round(this.y);
        arg0.z = Math.round(this.z);
        return arg0;
    }

    @Override
    public boolean isFinite() {
        return Math.isFinite(this.x) && Math.isFinite(this.y) && Math.isFinite(this.z);
    }
}
