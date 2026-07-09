// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.Buffer;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import sun.misc.Unsafe;

abstract class MemUtil {
    public static final MemUtil INSTANCE = createInstance();

    private static MemUtil createInstance() {
        Object object;
        try {
            if (Options.NO_UNSAFE && Options.FORCE_UNSAFE) {
                throw new ConfigurationException("Cannot enable both -Djoml.nounsafe and -Djoml.forceUnsafe", null);
            }

            if (Options.NO_UNSAFE) {
                object = new MemUtil.MemUtilNIO();
            } else {
                object = new MemUtil.MemUtilUnsafe();
            }
        } catch (Throwable throwable) {
            if (Options.FORCE_UNSAFE) {
                throw new ConfigurationException("Unsafe is not supported but its use was forced via -Djoml.forceUnsafe", throwable);
            }

            object = new MemUtil.MemUtilNIO();
        }

        return (MemUtil)object;
    }

    public abstract void put(Matrix4f var1, int var2, FloatBuffer var3);

    public abstract void put(Matrix4f var1, int var2, ByteBuffer var3);

    public abstract void put(Matrix4x3f var1, int var2, FloatBuffer var3);

    public abstract void put(Matrix4x3f var1, int var2, ByteBuffer var3);

    public abstract void put4x4(Matrix4x3f var1, int var2, FloatBuffer var3);

    public abstract void put4x4(Matrix4x3f var1, int var2, ByteBuffer var3);

    public abstract void put4x4(Matrix4x3d var1, int var2, DoubleBuffer var3);

    public abstract void put4x4(Matrix4x3d var1, int var2, ByteBuffer var3);

    public abstract void put4x4(Matrix3x2f var1, int var2, FloatBuffer var3);

    public abstract void put4x4(Matrix3x2f var1, int var2, ByteBuffer var3);

    public abstract void put4x4(Matrix3x2d var1, int var2, DoubleBuffer var3);

    public abstract void put4x4(Matrix3x2d var1, int var2, ByteBuffer var3);

    public abstract void put3x3(Matrix3x2f var1, int var2, FloatBuffer var3);

    public abstract void put3x3(Matrix3x2f var1, int var2, ByteBuffer var3);

    public abstract void put3x3(Matrix3x2d var1, int var2, DoubleBuffer var3);

    public abstract void put3x3(Matrix3x2d var1, int var2, ByteBuffer var3);

    public abstract void put4x3(Matrix4f var1, int var2, FloatBuffer var3);

    public abstract void put4x3(Matrix4f var1, int var2, ByteBuffer var3);

    public abstract void put3x4(Matrix4f var1, int var2, FloatBuffer var3);

    public abstract void put3x4(Matrix4f var1, int var2, ByteBuffer var3);

    public abstract void put3x4(Matrix4x3f var1, int var2, FloatBuffer var3);

    public abstract void put3x4(Matrix4x3f var1, int var2, ByteBuffer var3);

    public abstract void put3x4(Matrix3f var1, int var2, FloatBuffer var3);

    public abstract void put3x4(Matrix3f var1, int var2, ByteBuffer var3);

    public abstract void putTransposed(Matrix4f var1, int var2, FloatBuffer var3);

    public abstract void putTransposed(Matrix4f var1, int var2, ByteBuffer var3);

    public abstract void put4x3Transposed(Matrix4f var1, int var2, FloatBuffer var3);

    public abstract void put4x3Transposed(Matrix4f var1, int var2, ByteBuffer var3);

    public abstract void putTransposed(Matrix4x3f var1, int var2, FloatBuffer var3);

    public abstract void putTransposed(Matrix4x3f var1, int var2, ByteBuffer var3);

    public abstract void putTransposed(Matrix3f var1, int var2, FloatBuffer var3);

    public abstract void putTransposed(Matrix3f var1, int var2, ByteBuffer var3);

    public abstract void putTransposed(Matrix2f var1, int var2, FloatBuffer var3);

    public abstract void putTransposed(Matrix2f var1, int var2, ByteBuffer var3);

    public abstract void put(Matrix4d var1, int var2, DoubleBuffer var3);

    public abstract void put(Matrix4d var1, int var2, ByteBuffer var3);

    public abstract void put(Matrix4x3d var1, int var2, DoubleBuffer var3);

    public abstract void put(Matrix4x3d var1, int var2, ByteBuffer var3);

    public abstract void putf(Matrix4d var1, int var2, FloatBuffer var3);

    public abstract void putf(Matrix4d var1, int var2, ByteBuffer var3);

    public abstract void putf(Matrix4x3d var1, int var2, FloatBuffer var3);

    public abstract void putf(Matrix4x3d var1, int var2, ByteBuffer var3);

    public abstract void putTransposed(Matrix4d var1, int var2, DoubleBuffer var3);

    public abstract void putTransposed(Matrix4d var1, int var2, ByteBuffer var3);

    public abstract void put4x3Transposed(Matrix4d var1, int var2, DoubleBuffer var3);

    public abstract void put4x3Transposed(Matrix4d var1, int var2, ByteBuffer var3);

    public abstract void putTransposed(Matrix4x3d var1, int var2, DoubleBuffer var3);

    public abstract void putTransposed(Matrix4x3d var1, int var2, ByteBuffer var3);

    public abstract void putTransposed(Matrix2d var1, int var2, DoubleBuffer var3);

    public abstract void putTransposed(Matrix2d var1, int var2, ByteBuffer var3);

    public abstract void putfTransposed(Matrix4d var1, int var2, FloatBuffer var3);

    public abstract void putfTransposed(Matrix4d var1, int var2, ByteBuffer var3);

    public abstract void putfTransposed(Matrix4x3d var1, int var2, FloatBuffer var3);

    public abstract void putfTransposed(Matrix4x3d var1, int var2, ByteBuffer var3);

    public abstract void putfTransposed(Matrix2d var1, int var2, FloatBuffer var3);

    public abstract void putfTransposed(Matrix2d var1, int var2, ByteBuffer var3);

    public abstract void put(Matrix3f var1, int var2, FloatBuffer var3);

    public abstract void put(Matrix3f var1, int var2, ByteBuffer var3);

    public abstract void put(Matrix3d var1, int var2, DoubleBuffer var3);

    public abstract void put(Matrix3d var1, int var2, ByteBuffer var3);

    public abstract void putf(Matrix3d var1, int var2, FloatBuffer var3);

    public abstract void putf(Matrix3d var1, int var2, ByteBuffer var3);

    public abstract void put(Matrix3x2f var1, int var2, FloatBuffer var3);

    public abstract void put(Matrix3x2f var1, int var2, ByteBuffer var3);

    public abstract void put(Matrix3x2d var1, int var2, DoubleBuffer var3);

    public abstract void put(Matrix3x2d var1, int var2, ByteBuffer var3);

    public abstract void put(Matrix2f var1, int var2, FloatBuffer var3);

    public abstract void put(Matrix2f var1, int var2, ByteBuffer var3);

    public abstract void put(Matrix2d var1, int var2, DoubleBuffer var3);

    public abstract void put(Matrix2d var1, int var2, ByteBuffer var3);

    public abstract void putf(Matrix2d var1, int var2, FloatBuffer var3);

    public abstract void putf(Matrix2d var1, int var2, ByteBuffer var3);

    public abstract void put(Vector4d var1, int var2, DoubleBuffer var3);

    public abstract void put(Vector4d var1, int var2, FloatBuffer var3);

    public abstract void put(Vector4d var1, int var2, ByteBuffer var3);

    public abstract void putf(Vector4d var1, int var2, ByteBuffer var3);

    public abstract void put(Vector4f var1, int var2, FloatBuffer var3);

    public abstract void put(Vector4f var1, int var2, ByteBuffer var3);

    public abstract void put(Vector4i var1, int var2, IntBuffer var3);

    public abstract void put(Vector4i var1, int var2, ByteBuffer var3);

    public abstract void put(Vector3f var1, int var2, FloatBuffer var3);

    public abstract void put(Vector3f var1, int var2, ByteBuffer var3);

    public abstract void put(Vector3d var1, int var2, DoubleBuffer var3);

    public abstract void put(Vector3d var1, int var2, FloatBuffer var3);

    public abstract void put(Vector3d var1, int var2, ByteBuffer var3);

    public abstract void putf(Vector3d var1, int var2, ByteBuffer var3);

    public abstract void put(Vector3i var1, int var2, IntBuffer var3);

    public abstract void put(Vector3i var1, int var2, ByteBuffer var3);

    public abstract void put(Vector2f var1, int var2, FloatBuffer var3);

    public abstract void put(Vector2f var1, int var2, ByteBuffer var3);

    public abstract void put(Vector2d var1, int var2, DoubleBuffer var3);

    public abstract void put(Vector2d var1, int var2, ByteBuffer var3);

    public abstract void put(Vector2i var1, int var2, IntBuffer var3);

    public abstract void put(Vector2i var1, int var2, ByteBuffer var3);

    public abstract void get(Matrix4f var1, int var2, FloatBuffer var3);

    public abstract void get(Matrix4f var1, int var2, ByteBuffer var3);

    public abstract void getTransposed(Matrix4f var1, int var2, FloatBuffer var3);

    public abstract void getTransposed(Matrix4f var1, int var2, ByteBuffer var3);

    public abstract void get(Matrix4x3f var1, int var2, FloatBuffer var3);

    public abstract void get(Matrix4x3f var1, int var2, ByteBuffer var3);

    public abstract void get(Matrix4d var1, int var2, DoubleBuffer var3);

    public abstract void get(Matrix4d var1, int var2, ByteBuffer var3);

    public abstract void get(Matrix4x3d var1, int var2, DoubleBuffer var3);

    public abstract void get(Matrix4x3d var1, int var2, ByteBuffer var3);

    public abstract void getf(Matrix4d var1, int var2, FloatBuffer var3);

    public abstract void getf(Matrix4d var1, int var2, ByteBuffer var3);

    public abstract void getf(Matrix4x3d var1, int var2, FloatBuffer var3);

    public abstract void getf(Matrix4x3d var1, int var2, ByteBuffer var3);

    public abstract void get(Matrix3f var1, int var2, FloatBuffer var3);

    public abstract void get(Matrix3f var1, int var2, ByteBuffer var3);

    public abstract void get(Matrix3d var1, int var2, DoubleBuffer var3);

    public abstract void get(Matrix3d var1, int var2, ByteBuffer var3);

    public abstract void get(Matrix3x2f var1, int var2, FloatBuffer var3);

    public abstract void get(Matrix3x2f var1, int var2, ByteBuffer var3);

    public abstract void get(Matrix3x2d var1, int var2, DoubleBuffer var3);

    public abstract void get(Matrix3x2d var1, int var2, ByteBuffer var3);

    public abstract void getf(Matrix3d var1, int var2, FloatBuffer var3);

    public abstract void getf(Matrix3d var1, int var2, ByteBuffer var3);

    public abstract void get(Matrix2f var1, int var2, FloatBuffer var3);

    public abstract void get(Matrix2f var1, int var2, ByteBuffer var3);

    public abstract void get(Matrix2d var1, int var2, DoubleBuffer var3);

    public abstract void get(Matrix2d var1, int var2, ByteBuffer var3);

    public abstract void getf(Matrix2d var1, int var2, FloatBuffer var3);

    public abstract void getf(Matrix2d var1, int var2, ByteBuffer var3);

    public abstract void get(Vector4d var1, int var2, DoubleBuffer var3);

    public abstract void get(Vector4d var1, int var2, ByteBuffer var3);

    public abstract void get(Vector4f var1, int var2, FloatBuffer var3);

    public abstract void get(Vector4f var1, int var2, ByteBuffer var3);

    public abstract void get(Vector4i var1, int var2, IntBuffer var3);

    public abstract void get(Vector4i var1, int var2, ByteBuffer var3);

    public abstract void get(Vector3f var1, int var2, FloatBuffer var3);

    public abstract void get(Vector3f var1, int var2, ByteBuffer var3);

    public abstract void get(Vector3d var1, int var2, DoubleBuffer var3);

    public abstract void get(Vector3d var1, int var2, ByteBuffer var3);

    public abstract void get(Vector3i var1, int var2, IntBuffer var3);

    public abstract void get(Vector3i var1, int var2, ByteBuffer var3);

    public abstract void get(Vector2f var1, int var2, FloatBuffer var3);

    public abstract void get(Vector2f var1, int var2, ByteBuffer var3);

    public abstract void get(Vector2d var1, int var2, DoubleBuffer var3);

    public abstract void get(Vector2d var1, int var2, ByteBuffer var3);

    public abstract void get(Vector2i var1, int var2, IntBuffer var3);

    public abstract void get(Vector2i var1, int var2, ByteBuffer var3);

    public abstract void putMatrix3f(Quaternionf var1, int var2, ByteBuffer var3);

    public abstract void putMatrix3f(Quaternionf var1, int var2, FloatBuffer var3);

    public abstract void putMatrix4f(Quaternionf var1, int var2, ByteBuffer var3);

    public abstract void putMatrix4f(Quaternionf var1, int var2, FloatBuffer var3);

    public abstract void putMatrix4x3f(Quaternionf var1, int var2, ByteBuffer var3);

    public abstract void putMatrix4x3f(Quaternionf var1, int var2, FloatBuffer var3);

    public abstract float get(Matrix4f var1, int var2, int var3);

    public abstract Matrix4f set(Matrix4f var1, int var2, int var3, float var4);

    public abstract double get(Matrix4d var1, int var2, int var3);

    public abstract Matrix4d set(Matrix4d var1, int var2, int var3, double var4);

    public abstract float get(Matrix3f var1, int var2, int var3);

    public abstract Matrix3f set(Matrix3f var1, int var2, int var3, float var4);

    public abstract double get(Matrix3d var1, int var2, int var3);

    public abstract Matrix3d set(Matrix3d var1, int var2, int var3, double var4);

    public abstract Vector4f getColumn(Matrix4f var1, int var2, Vector4f var3);

    public abstract Matrix4f setColumn(Vector4f var1, int var2, Matrix4f var3);

    public abstract Matrix4f setColumn(Vector4fc var1, int var2, Matrix4f var3);

    public abstract void copy(Matrix4f var1, Matrix4f var2);

    public abstract void copy(Matrix4x3f var1, Matrix4x3f var2);

    public abstract void copy(Matrix4f var1, Matrix4x3f var2);

    public abstract void copy(Matrix4x3f var1, Matrix4f var2);

    public abstract void copy(Matrix3f var1, Matrix3f var2);

    public abstract void copy(Matrix3f var1, Matrix4f var2);

    public abstract void copy(Matrix4f var1, Matrix3f var2);

    public abstract void copy(Matrix3f var1, Matrix4x3f var2);

    public abstract void copy(Matrix3x2f var1, Matrix3x2f var2);

    public abstract void copy(Matrix3x2d var1, Matrix3x2d var2);

    public abstract void copy(Matrix2f var1, Matrix2f var2);

    public abstract void copy(Matrix2d var1, Matrix2d var2);

    public abstract void copy(Matrix2f var1, Matrix3f var2);

    public abstract void copy(Matrix3f var1, Matrix2f var2);

    public abstract void copy(Matrix2f var1, Matrix3x2f var2);

    public abstract void copy(Matrix3x2f var1, Matrix2f var2);

    public abstract void copy(Matrix2d var1, Matrix3d var2);

    public abstract void copy(Matrix3d var1, Matrix2d var2);

    public abstract void copy(Matrix2d var1, Matrix3x2d var2);

    public abstract void copy(Matrix3x2d var1, Matrix2d var2);

    public abstract void copy3x3(Matrix4f var1, Matrix4f var2);

    public abstract void copy3x3(Matrix4x3f var1, Matrix4x3f var2);

    public abstract void copy3x3(Matrix3f var1, Matrix4x3f var2);

    public abstract void copy3x3(Matrix3f var1, Matrix4f var2);

    public abstract void copy4x3(Matrix4f var1, Matrix4f var2);

    public abstract void copy4x3(Matrix4x3f var1, Matrix4f var2);

    public abstract void copy(float[] var1, int var2, Matrix4f var3);

    public abstract void copyTransposed(float[] var1, int var2, Matrix4f var3);

    public abstract void copy(float[] var1, int var2, Matrix3f var3);

    public abstract void copy(float[] var1, int var2, Matrix4x3f var3);

    public abstract void copy(float[] var1, int var2, Matrix3x2f var3);

    public abstract void copy(double[] var1, int var2, Matrix3x2d var3);

    public abstract void copy(float[] var1, int var2, Matrix2f var3);

    public abstract void copy(double[] var1, int var2, Matrix2d var3);

    public abstract void copy(Matrix4f var1, float[] var2, int var3);

    public abstract void copy(Matrix3f var1, float[] var2, int var3);

    public abstract void copy(Matrix4x3f var1, float[] var2, int var3);

    public abstract void copy(Matrix3x2f var1, float[] var2, int var3);

    public abstract void copy(Matrix3x2d var1, double[] var2, int var3);

    public abstract void copy(Matrix2f var1, float[] var2, int var3);

    public abstract void copy(Matrix2d var1, double[] var2, int var3);

    public abstract void copy4x4(Matrix4x3f var1, float[] var2, int var3);

    public abstract void copy4x4(Matrix4x3d var1, float[] var2, int var3);

    public abstract void copy4x4(Matrix4x3d var1, double[] var2, int var3);

    public abstract void copy4x4(Matrix3x2f var1, float[] var2, int var3);

    public abstract void copy4x4(Matrix3x2d var1, double[] var2, int var3);

    public abstract void copy3x3(Matrix3x2f var1, float[] var2, int var3);

    public abstract void copy3x3(Matrix3x2d var1, double[] var2, int var3);

    public abstract void identity(Matrix4f var1);

    public abstract void identity(Matrix4x3f var1);

    public abstract void identity(Matrix3f var1);

    public abstract void identity(Matrix3x2f var1);

    public abstract void identity(Matrix3x2d var1);

    public abstract void identity(Matrix2f var1);

    public abstract void swap(Matrix4f var1, Matrix4f var2);

    public abstract void swap(Matrix4x3f var1, Matrix4x3f var2);

    public abstract void swap(Matrix3f var1, Matrix3f var2);

    public abstract void swap(Matrix2f var1, Matrix2f var2);

    public abstract void swap(Matrix2d var1, Matrix2d var2);

    public abstract void zero(Matrix4f var1);

    public abstract void zero(Matrix4x3f var1);

    public abstract void zero(Matrix3f var1);

    public abstract void zero(Matrix3x2f var1);

    public abstract void zero(Matrix3x2d var1);

    public abstract void zero(Matrix2f var1);

    public abstract void zero(Matrix2d var1);

    public static class MemUtilNIO extends MemUtil {
        public void put0(Matrix4f matrix4f, FloatBuffer floatBuffer) {
            floatBuffer.put(0, matrix4f.m00())
                .put(1, matrix4f.m01())
                .put(2, matrix4f.m02())
                .put(3, matrix4f.m03())
                .put(4, matrix4f.m10())
                .put(5, matrix4f.m11())
                .put(6, matrix4f.m12())
                .put(7, matrix4f.m13())
                .put(8, matrix4f.m20())
                .put(9, matrix4f.m21())
                .put(10, matrix4f.m22())
                .put(11, matrix4f.m23())
                .put(12, matrix4f.m30())
                .put(13, matrix4f.m31())
                .put(14, matrix4f.m32())
                .put(15, matrix4f.m33());
        }

        public void putN(Matrix4f matrix4f, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, matrix4f.m00())
                .put(int0 + 1, matrix4f.m01())
                .put(int0 + 2, matrix4f.m02())
                .put(int0 + 3, matrix4f.m03())
                .put(int0 + 4, matrix4f.m10())
                .put(int0 + 5, matrix4f.m11())
                .put(int0 + 6, matrix4f.m12())
                .put(int0 + 7, matrix4f.m13())
                .put(int0 + 8, matrix4f.m20())
                .put(int0 + 9, matrix4f.m21())
                .put(int0 + 10, matrix4f.m22())
                .put(int0 + 11, matrix4f.m23())
                .put(int0 + 12, matrix4f.m30())
                .put(int0 + 13, matrix4f.m31())
                .put(int0 + 14, matrix4f.m32())
                .put(int0 + 15, matrix4f.m33());
        }

        @Override
        public void put(Matrix4f matrix4f, int int0, FloatBuffer floatBuffer) {
            if (int0 == 0) {
                this.put0(matrix4f, floatBuffer);
            } else {
                this.putN(matrix4f, int0, floatBuffer);
            }
        }

        public void put0(Matrix4f matrix4f, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(0, matrix4f.m00())
                .putFloat(4, matrix4f.m01())
                .putFloat(8, matrix4f.m02())
                .putFloat(12, matrix4f.m03())
                .putFloat(16, matrix4f.m10())
                .putFloat(20, matrix4f.m11())
                .putFloat(24, matrix4f.m12())
                .putFloat(28, matrix4f.m13())
                .putFloat(32, matrix4f.m20())
                .putFloat(36, matrix4f.m21())
                .putFloat(40, matrix4f.m22())
                .putFloat(44, matrix4f.m23())
                .putFloat(48, matrix4f.m30())
                .putFloat(52, matrix4f.m31())
                .putFloat(56, matrix4f.m32())
                .putFloat(60, matrix4f.m33());
        }

        private void putN(Matrix4f matrix4f, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, matrix4f.m00())
                .putFloat(int0 + 4, matrix4f.m01())
                .putFloat(int0 + 8, matrix4f.m02())
                .putFloat(int0 + 12, matrix4f.m03())
                .putFloat(int0 + 16, matrix4f.m10())
                .putFloat(int0 + 20, matrix4f.m11())
                .putFloat(int0 + 24, matrix4f.m12())
                .putFloat(int0 + 28, matrix4f.m13())
                .putFloat(int0 + 32, matrix4f.m20())
                .putFloat(int0 + 36, matrix4f.m21())
                .putFloat(int0 + 40, matrix4f.m22())
                .putFloat(int0 + 44, matrix4f.m23())
                .putFloat(int0 + 48, matrix4f.m30())
                .putFloat(int0 + 52, matrix4f.m31())
                .putFloat(int0 + 56, matrix4f.m32())
                .putFloat(int0 + 60, matrix4f.m33());
        }

        @Override
        public void put(Matrix4f matrix4f, int int0, ByteBuffer byteBuffer) {
            if (int0 == 0) {
                this.put0(matrix4f, byteBuffer);
            } else {
                this.putN(matrix4f, int0, byteBuffer);
            }
        }

        public void put4x3_0(Matrix4f matrix4f, FloatBuffer floatBuffer) {
            floatBuffer.put(0, matrix4f.m00())
                .put(1, matrix4f.m01())
                .put(2, matrix4f.m02())
                .put(3, matrix4f.m10())
                .put(4, matrix4f.m11())
                .put(5, matrix4f.m12())
                .put(6, matrix4f.m20())
                .put(7, matrix4f.m21())
                .put(8, matrix4f.m22())
                .put(9, matrix4f.m30())
                .put(10, matrix4f.m31())
                .put(11, matrix4f.m32());
        }

        public void put4x3_N(Matrix4f matrix4f, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, matrix4f.m00())
                .put(int0 + 1, matrix4f.m01())
                .put(int0 + 2, matrix4f.m02())
                .put(int0 + 3, matrix4f.m10())
                .put(int0 + 4, matrix4f.m11())
                .put(int0 + 5, matrix4f.m12())
                .put(int0 + 6, matrix4f.m20())
                .put(int0 + 7, matrix4f.m21())
                .put(int0 + 8, matrix4f.m22())
                .put(int0 + 9, matrix4f.m30())
                .put(int0 + 10, matrix4f.m31())
                .put(int0 + 11, matrix4f.m32());
        }

        @Override
        public void put4x3(Matrix4f matrix4f, int int0, FloatBuffer floatBuffer) {
            if (int0 == 0) {
                this.put4x3_0(matrix4f, floatBuffer);
            } else {
                this.put4x3_N(matrix4f, int0, floatBuffer);
            }
        }

        public void put4x3_0(Matrix4f matrix4f, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(0, matrix4f.m00())
                .putFloat(4, matrix4f.m01())
                .putFloat(8, matrix4f.m02())
                .putFloat(12, matrix4f.m10())
                .putFloat(16, matrix4f.m11())
                .putFloat(20, matrix4f.m12())
                .putFloat(24, matrix4f.m20())
                .putFloat(28, matrix4f.m21())
                .putFloat(32, matrix4f.m22())
                .putFloat(36, matrix4f.m30())
                .putFloat(40, matrix4f.m31())
                .putFloat(44, matrix4f.m32());
        }

        private void put4x3_N(Matrix4f matrix4f, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, matrix4f.m00())
                .putFloat(int0 + 4, matrix4f.m01())
                .putFloat(int0 + 8, matrix4f.m02())
                .putFloat(int0 + 12, matrix4f.m10())
                .putFloat(int0 + 16, matrix4f.m11())
                .putFloat(int0 + 20, matrix4f.m12())
                .putFloat(int0 + 24, matrix4f.m20())
                .putFloat(int0 + 28, matrix4f.m21())
                .putFloat(int0 + 32, matrix4f.m22())
                .putFloat(int0 + 36, matrix4f.m30())
                .putFloat(int0 + 40, matrix4f.m31())
                .putFloat(int0 + 44, matrix4f.m32());
        }

        @Override
        public void put4x3(Matrix4f matrix4f, int int0, ByteBuffer byteBuffer) {
            if (int0 == 0) {
                this.put4x3_0(matrix4f, byteBuffer);
            } else {
                this.put4x3_N(matrix4f, int0, byteBuffer);
            }
        }

        public void put3x4_0(Matrix4f matrix4f, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(0, matrix4f.m00())
                .putFloat(4, matrix4f.m01())
                .putFloat(8, matrix4f.m02())
                .putFloat(12, matrix4f.m03())
                .putFloat(16, matrix4f.m10())
                .putFloat(20, matrix4f.m11())
                .putFloat(24, matrix4f.m12())
                .putFloat(28, matrix4f.m13())
                .putFloat(32, matrix4f.m20())
                .putFloat(36, matrix4f.m21())
                .putFloat(40, matrix4f.m22())
                .putFloat(44, matrix4f.m23());
        }

        private void put3x4_N(Matrix4f matrix4f, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, matrix4f.m00())
                .putFloat(int0 + 4, matrix4f.m01())
                .putFloat(int0 + 8, matrix4f.m02())
                .putFloat(int0 + 12, matrix4f.m03())
                .putFloat(int0 + 16, matrix4f.m10())
                .putFloat(int0 + 20, matrix4f.m11())
                .putFloat(int0 + 24, matrix4f.m12())
                .putFloat(int0 + 28, matrix4f.m13())
                .putFloat(int0 + 32, matrix4f.m20())
                .putFloat(int0 + 36, matrix4f.m21())
                .putFloat(int0 + 40, matrix4f.m22())
                .putFloat(int0 + 44, matrix4f.m23());
        }

        @Override
        public void put3x4(Matrix4f matrix4f, int int0, ByteBuffer byteBuffer) {
            if (int0 == 0) {
                this.put3x4_0(matrix4f, byteBuffer);
            } else {
                this.put3x4_N(matrix4f, int0, byteBuffer);
            }
        }

        public void put3x4_0(Matrix4f matrix4f, FloatBuffer floatBuffer) {
            floatBuffer.put(0, matrix4f.m00())
                .put(1, matrix4f.m01())
                .put(2, matrix4f.m02())
                .put(3, matrix4f.m03())
                .put(4, matrix4f.m10())
                .put(5, matrix4f.m11())
                .put(6, matrix4f.m12())
                .put(7, matrix4f.m13())
                .put(8, matrix4f.m20())
                .put(9, matrix4f.m21())
                .put(10, matrix4f.m22())
                .put(11, matrix4f.m23());
        }

        public void put3x4_N(Matrix4f matrix4f, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, matrix4f.m00())
                .put(int0 + 1, matrix4f.m01())
                .put(int0 + 2, matrix4f.m02())
                .put(int0 + 3, matrix4f.m03())
                .put(int0 + 4, matrix4f.m10())
                .put(int0 + 5, matrix4f.m11())
                .put(int0 + 6, matrix4f.m12())
                .put(int0 + 7, matrix4f.m13())
                .put(int0 + 8, matrix4f.m20())
                .put(int0 + 9, matrix4f.m21())
                .put(int0 + 10, matrix4f.m22())
                .put(int0 + 11, matrix4f.m23());
        }

        @Override
        public void put3x4(Matrix4f matrix4f, int int0, FloatBuffer floatBuffer) {
            if (int0 == 0) {
                this.put3x4_0(matrix4f, floatBuffer);
            } else {
                this.put3x4_N(matrix4f, int0, floatBuffer);
            }
        }

        public void put3x4_0(Matrix4x3f matrix4x3f, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(0, matrix4x3f.m00())
                .putFloat(4, matrix4x3f.m01())
                .putFloat(8, matrix4x3f.m02())
                .putFloat(12, 0.0F)
                .putFloat(16, matrix4x3f.m10())
                .putFloat(20, matrix4x3f.m11())
                .putFloat(24, matrix4x3f.m12())
                .putFloat(28, 0.0F)
                .putFloat(32, matrix4x3f.m20())
                .putFloat(36, matrix4x3f.m21())
                .putFloat(40, matrix4x3f.m22())
                .putFloat(44, 0.0F);
        }

        private void put3x4_N(Matrix4x3f matrix4x3f, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, matrix4x3f.m00())
                .putFloat(int0 + 4, matrix4x3f.m01())
                .putFloat(int0 + 8, matrix4x3f.m02())
                .putFloat(int0 + 12, 0.0F)
                .putFloat(int0 + 16, matrix4x3f.m10())
                .putFloat(int0 + 20, matrix4x3f.m11())
                .putFloat(int0 + 24, matrix4x3f.m12())
                .putFloat(int0 + 28, 0.0F)
                .putFloat(int0 + 32, matrix4x3f.m20())
                .putFloat(int0 + 36, matrix4x3f.m21())
                .putFloat(int0 + 40, matrix4x3f.m22())
                .putFloat(int0 + 44, 0.0F);
        }

        @Override
        public void put3x4(Matrix4x3f matrix4x3f, int int0, ByteBuffer byteBuffer) {
            if (int0 == 0) {
                this.put3x4_0(matrix4x3f, byteBuffer);
            } else {
                this.put3x4_N(matrix4x3f, int0, byteBuffer);
            }
        }

        public void put3x4_0(Matrix4x3f matrix4x3f, FloatBuffer floatBuffer) {
            floatBuffer.put(0, matrix4x3f.m00())
                .put(1, matrix4x3f.m01())
                .put(2, matrix4x3f.m02())
                .put(3, 0.0F)
                .put(4, matrix4x3f.m10())
                .put(5, matrix4x3f.m11())
                .put(6, matrix4x3f.m12())
                .put(7, 0.0F)
                .put(8, matrix4x3f.m20())
                .put(9, matrix4x3f.m21())
                .put(10, matrix4x3f.m22())
                .put(11, 0.0F);
        }

        public void put3x4_N(Matrix4x3f matrix4x3f, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, matrix4x3f.m00())
                .put(int0 + 1, matrix4x3f.m01())
                .put(int0 + 2, matrix4x3f.m02())
                .put(int0 + 3, 0.0F)
                .put(int0 + 4, matrix4x3f.m10())
                .put(int0 + 5, matrix4x3f.m11())
                .put(int0 + 6, matrix4x3f.m12())
                .put(int0 + 7, 0.0F)
                .put(int0 + 8, matrix4x3f.m20())
                .put(int0 + 9, matrix4x3f.m21())
                .put(int0 + 10, matrix4x3f.m22())
                .put(int0 + 11, 0.0F);
        }

        @Override
        public void put3x4(Matrix4x3f matrix4x3f, int int0, FloatBuffer floatBuffer) {
            if (int0 == 0) {
                this.put3x4_0(matrix4x3f, floatBuffer);
            } else {
                this.put3x4_N(matrix4x3f, int0, floatBuffer);
            }
        }

        public void put0(Matrix4x3f matrix4x3f, FloatBuffer floatBuffer) {
            floatBuffer.put(0, matrix4x3f.m00())
                .put(1, matrix4x3f.m01())
                .put(2, matrix4x3f.m02())
                .put(3, matrix4x3f.m10())
                .put(4, matrix4x3f.m11())
                .put(5, matrix4x3f.m12())
                .put(6, matrix4x3f.m20())
                .put(7, matrix4x3f.m21())
                .put(8, matrix4x3f.m22())
                .put(9, matrix4x3f.m30())
                .put(10, matrix4x3f.m31())
                .put(11, matrix4x3f.m32());
        }

        public void putN(Matrix4x3f matrix4x3f, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, matrix4x3f.m00())
                .put(int0 + 1, matrix4x3f.m01())
                .put(int0 + 2, matrix4x3f.m02())
                .put(int0 + 3, matrix4x3f.m10())
                .put(int0 + 4, matrix4x3f.m11())
                .put(int0 + 5, matrix4x3f.m12())
                .put(int0 + 6, matrix4x3f.m20())
                .put(int0 + 7, matrix4x3f.m21())
                .put(int0 + 8, matrix4x3f.m22())
                .put(int0 + 9, matrix4x3f.m30())
                .put(int0 + 10, matrix4x3f.m31())
                .put(int0 + 11, matrix4x3f.m32());
        }

        @Override
        public void put(Matrix4x3f matrix4x3f, int int0, FloatBuffer floatBuffer) {
            if (int0 == 0) {
                this.put0(matrix4x3f, floatBuffer);
            } else {
                this.putN(matrix4x3f, int0, floatBuffer);
            }
        }

        public void put0(Matrix4x3f matrix4x3f, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(0, matrix4x3f.m00())
                .putFloat(4, matrix4x3f.m01())
                .putFloat(8, matrix4x3f.m02())
                .putFloat(12, matrix4x3f.m10())
                .putFloat(16, matrix4x3f.m11())
                .putFloat(20, matrix4x3f.m12())
                .putFloat(24, matrix4x3f.m20())
                .putFloat(28, matrix4x3f.m21())
                .putFloat(32, matrix4x3f.m22())
                .putFloat(36, matrix4x3f.m30())
                .putFloat(40, matrix4x3f.m31())
                .putFloat(44, matrix4x3f.m32());
        }

        public void putN(Matrix4x3f matrix4x3f, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, matrix4x3f.m00())
                .putFloat(int0 + 4, matrix4x3f.m01())
                .putFloat(int0 + 8, matrix4x3f.m02())
                .putFloat(int0 + 12, matrix4x3f.m10())
                .putFloat(int0 + 16, matrix4x3f.m11())
                .putFloat(int0 + 20, matrix4x3f.m12())
                .putFloat(int0 + 24, matrix4x3f.m20())
                .putFloat(int0 + 28, matrix4x3f.m21())
                .putFloat(int0 + 32, matrix4x3f.m22())
                .putFloat(int0 + 36, matrix4x3f.m30())
                .putFloat(int0 + 40, matrix4x3f.m31())
                .putFloat(int0 + 44, matrix4x3f.m32());
        }

        @Override
        public void put(Matrix4x3f matrix4x3f, int int0, ByteBuffer byteBuffer) {
            if (int0 == 0) {
                this.put0(matrix4x3f, byteBuffer);
            } else {
                this.putN(matrix4x3f, int0, byteBuffer);
            }
        }

        @Override
        public void put4x4(Matrix4x3f matrix4x3f, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, matrix4x3f.m00())
                .put(int0 + 1, matrix4x3f.m01())
                .put(int0 + 2, matrix4x3f.m02())
                .put(int0 + 3, 0.0F)
                .put(int0 + 4, matrix4x3f.m10())
                .put(int0 + 5, matrix4x3f.m11())
                .put(int0 + 6, matrix4x3f.m12())
                .put(int0 + 7, 0.0F)
                .put(int0 + 8, matrix4x3f.m20())
                .put(int0 + 9, matrix4x3f.m21())
                .put(int0 + 10, matrix4x3f.m22())
                .put(int0 + 11, 0.0F)
                .put(int0 + 12, matrix4x3f.m30())
                .put(int0 + 13, matrix4x3f.m31())
                .put(int0 + 14, matrix4x3f.m32())
                .put(int0 + 15, 1.0F);
        }

        @Override
        public void put4x4(Matrix4x3f matrix4x3f, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, matrix4x3f.m00())
                .putFloat(int0 + 4, matrix4x3f.m01())
                .putFloat(int0 + 8, matrix4x3f.m02())
                .putFloat(int0 + 12, 0.0F)
                .putFloat(int0 + 16, matrix4x3f.m10())
                .putFloat(int0 + 20, matrix4x3f.m11())
                .putFloat(int0 + 24, matrix4x3f.m12())
                .putFloat(int0 + 28, 0.0F)
                .putFloat(int0 + 32, matrix4x3f.m20())
                .putFloat(int0 + 36, matrix4x3f.m21())
                .putFloat(int0 + 40, matrix4x3f.m22())
                .putFloat(int0 + 44, 0.0F)
                .putFloat(int0 + 48, matrix4x3f.m30())
                .putFloat(int0 + 52, matrix4x3f.m31())
                .putFloat(int0 + 56, matrix4x3f.m32())
                .putFloat(int0 + 60, 1.0F);
        }

        @Override
        public void put4x4(Matrix4x3d matrix4x3d, int int0, DoubleBuffer doubleBuffer) {
            doubleBuffer.put(int0, matrix4x3d.m00())
                .put(int0 + 1, matrix4x3d.m01())
                .put(int0 + 2, matrix4x3d.m02())
                .put(int0 + 3, 0.0)
                .put(int0 + 4, matrix4x3d.m10())
                .put(int0 + 5, matrix4x3d.m11())
                .put(int0 + 6, matrix4x3d.m12())
                .put(int0 + 7, 0.0)
                .put(int0 + 8, matrix4x3d.m20())
                .put(int0 + 9, matrix4x3d.m21())
                .put(int0 + 10, matrix4x3d.m22())
                .put(int0 + 11, 0.0)
                .put(int0 + 12, matrix4x3d.m30())
                .put(int0 + 13, matrix4x3d.m31())
                .put(int0 + 14, matrix4x3d.m32())
                .put(int0 + 15, 1.0);
        }

        @Override
        public void put4x4(Matrix4x3d matrix4x3d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putDouble(int0, matrix4x3d.m00())
                .putDouble(int0 + 8, matrix4x3d.m01())
                .putDouble(int0 + 16, matrix4x3d.m02())
                .putDouble(int0 + 24, 0.0)
                .putDouble(int0 + 32, matrix4x3d.m10())
                .putDouble(int0 + 40, matrix4x3d.m11())
                .putDouble(int0 + 48, matrix4x3d.m12())
                .putDouble(int0 + 56, 0.0)
                .putDouble(int0 + 64, matrix4x3d.m20())
                .putDouble(int0 + 72, matrix4x3d.m21())
                .putDouble(int0 + 80, matrix4x3d.m22())
                .putDouble(int0 + 88, 0.0)
                .putDouble(int0 + 96, matrix4x3d.m30())
                .putDouble(int0 + 104, matrix4x3d.m31())
                .putDouble(int0 + 112, matrix4x3d.m32())
                .putDouble(int0 + 120, 1.0);
        }

        @Override
        public void put4x4(Matrix3x2f matrix3x2f, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, matrix3x2f.m00())
                .put(int0 + 1, matrix3x2f.m01())
                .put(int0 + 2, 0.0F)
                .put(int0 + 3, 0.0F)
                .put(int0 + 4, matrix3x2f.m10())
                .put(int0 + 5, matrix3x2f.m11())
                .put(int0 + 6, 0.0F)
                .put(int0 + 7, 0.0F)
                .put(int0 + 8, 0.0F)
                .put(int0 + 9, 0.0F)
                .put(int0 + 10, 1.0F)
                .put(int0 + 11, 0.0F)
                .put(int0 + 12, matrix3x2f.m20())
                .put(int0 + 13, matrix3x2f.m21())
                .put(int0 + 14, 0.0F)
                .put(int0 + 15, 1.0F);
        }

        @Override
        public void put4x4(Matrix3x2f matrix3x2f, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, matrix3x2f.m00())
                .putFloat(int0 + 4, matrix3x2f.m01())
                .putFloat(int0 + 8, 0.0F)
                .putFloat(int0 + 12, 0.0F)
                .putFloat(int0 + 16, matrix3x2f.m10())
                .putFloat(int0 + 20, matrix3x2f.m11())
                .putFloat(int0 + 24, 0.0F)
                .putFloat(int0 + 28, 0.0F)
                .putFloat(int0 + 32, 0.0F)
                .putFloat(int0 + 36, 0.0F)
                .putFloat(int0 + 40, 1.0F)
                .putFloat(int0 + 44, 0.0F)
                .putFloat(int0 + 48, matrix3x2f.m20())
                .putFloat(int0 + 52, matrix3x2f.m21())
                .putFloat(int0 + 56, 0.0F)
                .putFloat(int0 + 60, 1.0F);
        }

        @Override
        public void put4x4(Matrix3x2d matrix3x2d, int int0, DoubleBuffer doubleBuffer) {
            doubleBuffer.put(int0, matrix3x2d.m00())
                .put(int0 + 1, matrix3x2d.m01())
                .put(int0 + 2, 0.0)
                .put(int0 + 3, 0.0)
                .put(int0 + 4, matrix3x2d.m10())
                .put(int0 + 5, matrix3x2d.m11())
                .put(int0 + 6, 0.0)
                .put(int0 + 7, 0.0)
                .put(int0 + 8, 0.0)
                .put(int0 + 9, 0.0)
                .put(int0 + 10, 1.0)
                .put(int0 + 11, 0.0)
                .put(int0 + 12, matrix3x2d.m20())
                .put(int0 + 13, matrix3x2d.m21())
                .put(int0 + 14, 0.0)
                .put(int0 + 15, 1.0);
        }

        @Override
        public void put4x4(Matrix3x2d matrix3x2d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putDouble(int0, matrix3x2d.m00())
                .putDouble(int0 + 8, matrix3x2d.m01())
                .putDouble(int0 + 16, 0.0)
                .putDouble(int0 + 24, 0.0)
                .putDouble(int0 + 32, matrix3x2d.m10())
                .putDouble(int0 + 40, matrix3x2d.m11())
                .putDouble(int0 + 48, 0.0)
                .putDouble(int0 + 56, 0.0)
                .putDouble(int0 + 64, 0.0)
                .putDouble(int0 + 72, 0.0)
                .putDouble(int0 + 80, 1.0)
                .putDouble(int0 + 88, 0.0)
                .putDouble(int0 + 96, matrix3x2d.m20())
                .putDouble(int0 + 104, matrix3x2d.m21())
                .putDouble(int0 + 112, 0.0)
                .putDouble(int0 + 120, 1.0);
        }

        @Override
        public void put3x3(Matrix3x2f matrix3x2f, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, matrix3x2f.m00())
                .put(int0 + 1, matrix3x2f.m01())
                .put(int0 + 2, 0.0F)
                .put(int0 + 3, matrix3x2f.m10())
                .put(int0 + 4, matrix3x2f.m11())
                .put(int0 + 5, 0.0F)
                .put(int0 + 6, matrix3x2f.m20())
                .put(int0 + 7, matrix3x2f.m21())
                .put(int0 + 8, 1.0F);
        }

        @Override
        public void put3x3(Matrix3x2f matrix3x2f, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, matrix3x2f.m00())
                .putFloat(int0 + 4, matrix3x2f.m01())
                .putFloat(int0 + 8, 0.0F)
                .putFloat(int0 + 12, matrix3x2f.m10())
                .putFloat(int0 + 16, matrix3x2f.m11())
                .putFloat(int0 + 20, 0.0F)
                .putFloat(int0 + 24, matrix3x2f.m20())
                .putFloat(int0 + 28, matrix3x2f.m21())
                .putFloat(int0 + 32, 1.0F);
        }

        @Override
        public void put3x3(Matrix3x2d matrix3x2d, int int0, DoubleBuffer doubleBuffer) {
            doubleBuffer.put(int0, matrix3x2d.m00())
                .put(int0 + 1, matrix3x2d.m01())
                .put(int0 + 2, 0.0)
                .put(int0 + 3, matrix3x2d.m10())
                .put(int0 + 4, matrix3x2d.m11())
                .put(int0 + 5, 0.0)
                .put(int0 + 6, matrix3x2d.m20())
                .put(int0 + 7, matrix3x2d.m21())
                .put(int0 + 8, 1.0);
        }

        @Override
        public void put3x3(Matrix3x2d matrix3x2d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putDouble(int0, matrix3x2d.m00())
                .putDouble(int0 + 8, matrix3x2d.m01())
                .putDouble(int0 + 16, 0.0)
                .putDouble(int0 + 24, matrix3x2d.m10())
                .putDouble(int0 + 32, matrix3x2d.m11())
                .putDouble(int0 + 40, 0.0)
                .putDouble(int0 + 48, matrix3x2d.m20())
                .putDouble(int0 + 56, matrix3x2d.m21())
                .putDouble(int0 + 64, 1.0);
        }

        private void putTransposedN(Matrix4f matrix4f, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, matrix4f.m00())
                .put(int0 + 1, matrix4f.m10())
                .put(int0 + 2, matrix4f.m20())
                .put(int0 + 3, matrix4f.m30())
                .put(int0 + 4, matrix4f.m01())
                .put(int0 + 5, matrix4f.m11())
                .put(int0 + 6, matrix4f.m21())
                .put(int0 + 7, matrix4f.m31())
                .put(int0 + 8, matrix4f.m02())
                .put(int0 + 9, matrix4f.m12())
                .put(int0 + 10, matrix4f.m22())
                .put(int0 + 11, matrix4f.m32())
                .put(int0 + 12, matrix4f.m03())
                .put(int0 + 13, matrix4f.m13())
                .put(int0 + 14, matrix4f.m23())
                .put(int0 + 15, matrix4f.m33());
        }

        private void putTransposed0(Matrix4f matrix4f, FloatBuffer floatBuffer) {
            floatBuffer.put(0, matrix4f.m00())
                .put(1, matrix4f.m10())
                .put(2, matrix4f.m20())
                .put(3, matrix4f.m30())
                .put(4, matrix4f.m01())
                .put(5, matrix4f.m11())
                .put(6, matrix4f.m21())
                .put(7, matrix4f.m31())
                .put(8, matrix4f.m02())
                .put(9, matrix4f.m12())
                .put(10, matrix4f.m22())
                .put(11, matrix4f.m32())
                .put(12, matrix4f.m03())
                .put(13, matrix4f.m13())
                .put(14, matrix4f.m23())
                .put(15, matrix4f.m33());
        }

        @Override
        public void putTransposed(Matrix4f matrix4f, int int0, FloatBuffer floatBuffer) {
            if (int0 == 0) {
                this.putTransposed0(matrix4f, floatBuffer);
            } else {
                this.putTransposedN(matrix4f, int0, floatBuffer);
            }
        }

        private void putTransposedN(Matrix4f matrix4f, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, matrix4f.m00())
                .putFloat(int0 + 4, matrix4f.m10())
                .putFloat(int0 + 8, matrix4f.m20())
                .putFloat(int0 + 12, matrix4f.m30())
                .putFloat(int0 + 16, matrix4f.m01())
                .putFloat(int0 + 20, matrix4f.m11())
                .putFloat(int0 + 24, matrix4f.m21())
                .putFloat(int0 + 28, matrix4f.m31())
                .putFloat(int0 + 32, matrix4f.m02())
                .putFloat(int0 + 36, matrix4f.m12())
                .putFloat(int0 + 40, matrix4f.m22())
                .putFloat(int0 + 44, matrix4f.m32())
                .putFloat(int0 + 48, matrix4f.m03())
                .putFloat(int0 + 52, matrix4f.m13())
                .putFloat(int0 + 56, matrix4f.m23())
                .putFloat(int0 + 60, matrix4f.m33());
        }

        private void putTransposed0(Matrix4f matrix4f, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(0, matrix4f.m00())
                .putFloat(4, matrix4f.m10())
                .putFloat(8, matrix4f.m20())
                .putFloat(12, matrix4f.m30())
                .putFloat(16, matrix4f.m01())
                .putFloat(20, matrix4f.m11())
                .putFloat(24, matrix4f.m21())
                .putFloat(28, matrix4f.m31())
                .putFloat(32, matrix4f.m02())
                .putFloat(36, matrix4f.m12())
                .putFloat(40, matrix4f.m22())
                .putFloat(44, matrix4f.m32())
                .putFloat(48, matrix4f.m03())
                .putFloat(52, matrix4f.m13())
                .putFloat(56, matrix4f.m23())
                .putFloat(60, matrix4f.m33());
        }

        @Override
        public void putTransposed(Matrix4f matrix4f, int int0, ByteBuffer byteBuffer) {
            if (int0 == 0) {
                this.putTransposed0(matrix4f, byteBuffer);
            } else {
                this.putTransposedN(matrix4f, int0, byteBuffer);
            }
        }

        @Override
        public void put4x3Transposed(Matrix4f matrix4f, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, matrix4f.m00())
                .put(int0 + 1, matrix4f.m10())
                .put(int0 + 2, matrix4f.m20())
                .put(int0 + 3, matrix4f.m30())
                .put(int0 + 4, matrix4f.m01())
                .put(int0 + 5, matrix4f.m11())
                .put(int0 + 6, matrix4f.m21())
                .put(int0 + 7, matrix4f.m31())
                .put(int0 + 8, matrix4f.m02())
                .put(int0 + 9, matrix4f.m12())
                .put(int0 + 10, matrix4f.m22())
                .put(int0 + 11, matrix4f.m32());
        }

        @Override
        public void put4x3Transposed(Matrix4f matrix4f, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, matrix4f.m00())
                .putFloat(int0 + 4, matrix4f.m10())
                .putFloat(int0 + 8, matrix4f.m20())
                .putFloat(int0 + 12, matrix4f.m30())
                .putFloat(int0 + 16, matrix4f.m01())
                .putFloat(int0 + 20, matrix4f.m11())
                .putFloat(int0 + 24, matrix4f.m21())
                .putFloat(int0 + 28, matrix4f.m31())
                .putFloat(int0 + 32, matrix4f.m02())
                .putFloat(int0 + 36, matrix4f.m12())
                .putFloat(int0 + 40, matrix4f.m22())
                .putFloat(int0 + 44, matrix4f.m32());
        }

        @Override
        public void putTransposed(Matrix4x3f matrix4x3f, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, matrix4x3f.m00())
                .put(int0 + 1, matrix4x3f.m10())
                .put(int0 + 2, matrix4x3f.m20())
                .put(int0 + 3, matrix4x3f.m30())
                .put(int0 + 4, matrix4x3f.m01())
                .put(int0 + 5, matrix4x3f.m11())
                .put(int0 + 6, matrix4x3f.m21())
                .put(int0 + 7, matrix4x3f.m31())
                .put(int0 + 8, matrix4x3f.m02())
                .put(int0 + 9, matrix4x3f.m12())
                .put(int0 + 10, matrix4x3f.m22())
                .put(int0 + 11, matrix4x3f.m32());
        }

        @Override
        public void putTransposed(Matrix4x3f matrix4x3f, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, matrix4x3f.m00())
                .putFloat(int0 + 4, matrix4x3f.m10())
                .putFloat(int0 + 8, matrix4x3f.m20())
                .putFloat(int0 + 12, matrix4x3f.m30())
                .putFloat(int0 + 16, matrix4x3f.m01())
                .putFloat(int0 + 20, matrix4x3f.m11())
                .putFloat(int0 + 24, matrix4x3f.m21())
                .putFloat(int0 + 28, matrix4x3f.m31())
                .putFloat(int0 + 32, matrix4x3f.m02())
                .putFloat(int0 + 36, matrix4x3f.m12())
                .putFloat(int0 + 40, matrix4x3f.m22())
                .putFloat(int0 + 44, matrix4x3f.m32());
        }

        @Override
        public void putTransposed(Matrix3f matrix3f, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, matrix3f.m00())
                .put(int0 + 1, matrix3f.m10())
                .put(int0 + 2, matrix3f.m20())
                .put(int0 + 3, matrix3f.m01())
                .put(int0 + 4, matrix3f.m11())
                .put(int0 + 5, matrix3f.m21())
                .put(int0 + 6, matrix3f.m02())
                .put(int0 + 7, matrix3f.m12())
                .put(int0 + 8, matrix3f.m22());
        }

        @Override
        public void putTransposed(Matrix3f matrix3f, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, matrix3f.m00())
                .putFloat(int0 + 4, matrix3f.m10())
                .putFloat(int0 + 8, matrix3f.m20())
                .putFloat(int0 + 12, matrix3f.m01())
                .putFloat(int0 + 16, matrix3f.m11())
                .putFloat(int0 + 20, matrix3f.m21())
                .putFloat(int0 + 24, matrix3f.m02())
                .putFloat(int0 + 28, matrix3f.m12())
                .putFloat(int0 + 32, matrix3f.m22());
        }

        @Override
        public void putTransposed(Matrix2f matrix2f, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, matrix2f.m00()).put(int0 + 1, matrix2f.m10()).put(int0 + 2, matrix2f.m01()).put(int0 + 3, matrix2f.m11());
        }

        @Override
        public void putTransposed(Matrix2f matrix2f, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, matrix2f.m00()).putFloat(int0 + 4, matrix2f.m10()).putFloat(int0 + 8, matrix2f.m01()).putFloat(int0 + 12, matrix2f.m11());
        }

        @Override
        public void put(Matrix4d matrix4d, int int0, DoubleBuffer doubleBuffer) {
            doubleBuffer.put(int0, matrix4d.m00())
                .put(int0 + 1, matrix4d.m01())
                .put(int0 + 2, matrix4d.m02())
                .put(int0 + 3, matrix4d.m03())
                .put(int0 + 4, matrix4d.m10())
                .put(int0 + 5, matrix4d.m11())
                .put(int0 + 6, matrix4d.m12())
                .put(int0 + 7, matrix4d.m13())
                .put(int0 + 8, matrix4d.m20())
                .put(int0 + 9, matrix4d.m21())
                .put(int0 + 10, matrix4d.m22())
                .put(int0 + 11, matrix4d.m23())
                .put(int0 + 12, matrix4d.m30())
                .put(int0 + 13, matrix4d.m31())
                .put(int0 + 14, matrix4d.m32())
                .put(int0 + 15, matrix4d.m33());
        }

        @Override
        public void put(Matrix4d matrix4d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putDouble(int0, matrix4d.m00())
                .putDouble(int0 + 8, matrix4d.m01())
                .putDouble(int0 + 16, matrix4d.m02())
                .putDouble(int0 + 24, matrix4d.m03())
                .putDouble(int0 + 32, matrix4d.m10())
                .putDouble(int0 + 40, matrix4d.m11())
                .putDouble(int0 + 48, matrix4d.m12())
                .putDouble(int0 + 56, matrix4d.m13())
                .putDouble(int0 + 64, matrix4d.m20())
                .putDouble(int0 + 72, matrix4d.m21())
                .putDouble(int0 + 80, matrix4d.m22())
                .putDouble(int0 + 88, matrix4d.m23())
                .putDouble(int0 + 96, matrix4d.m30())
                .putDouble(int0 + 104, matrix4d.m31())
                .putDouble(int0 + 112, matrix4d.m32())
                .putDouble(int0 + 120, matrix4d.m33());
        }

        @Override
        public void put(Matrix4x3d matrix4x3d, int int0, DoubleBuffer doubleBuffer) {
            doubleBuffer.put(int0, matrix4x3d.m00())
                .put(int0 + 1, matrix4x3d.m01())
                .put(int0 + 2, matrix4x3d.m02())
                .put(int0 + 3, matrix4x3d.m10())
                .put(int0 + 4, matrix4x3d.m11())
                .put(int0 + 5, matrix4x3d.m12())
                .put(int0 + 6, matrix4x3d.m20())
                .put(int0 + 7, matrix4x3d.m21())
                .put(int0 + 8, matrix4x3d.m22())
                .put(int0 + 9, matrix4x3d.m30())
                .put(int0 + 10, matrix4x3d.m31())
                .put(int0 + 11, matrix4x3d.m32());
        }

        @Override
        public void put(Matrix4x3d matrix4x3d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putDouble(int0, matrix4x3d.m00())
                .putDouble(int0 + 8, matrix4x3d.m01())
                .putDouble(int0 + 16, matrix4x3d.m02())
                .putDouble(int0 + 24, matrix4x3d.m10())
                .putDouble(int0 + 32, matrix4x3d.m11())
                .putDouble(int0 + 40, matrix4x3d.m12())
                .putDouble(int0 + 48, matrix4x3d.m20())
                .putDouble(int0 + 56, matrix4x3d.m21())
                .putDouble(int0 + 64, matrix4x3d.m22())
                .putDouble(int0 + 72, matrix4x3d.m30())
                .putDouble(int0 + 80, matrix4x3d.m31())
                .putDouble(int0 + 88, matrix4x3d.m32());
        }

        @Override
        public void putf(Matrix4d matrix4d, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, (float)matrix4d.m00())
                .put(int0 + 1, (float)matrix4d.m01())
                .put(int0 + 2, (float)matrix4d.m02())
                .put(int0 + 3, (float)matrix4d.m03())
                .put(int0 + 4, (float)matrix4d.m10())
                .put(int0 + 5, (float)matrix4d.m11())
                .put(int0 + 6, (float)matrix4d.m12())
                .put(int0 + 7, (float)matrix4d.m13())
                .put(int0 + 8, (float)matrix4d.m20())
                .put(int0 + 9, (float)matrix4d.m21())
                .put(int0 + 10, (float)matrix4d.m22())
                .put(int0 + 11, (float)matrix4d.m23())
                .put(int0 + 12, (float)matrix4d.m30())
                .put(int0 + 13, (float)matrix4d.m31())
                .put(int0 + 14, (float)matrix4d.m32())
                .put(int0 + 15, (float)matrix4d.m33());
        }

        @Override
        public void putf(Matrix4d matrix4d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, (float)matrix4d.m00())
                .putFloat(int0 + 4, (float)matrix4d.m01())
                .putFloat(int0 + 8, (float)matrix4d.m02())
                .putFloat(int0 + 12, (float)matrix4d.m03())
                .putFloat(int0 + 16, (float)matrix4d.m10())
                .putFloat(int0 + 20, (float)matrix4d.m11())
                .putFloat(int0 + 24, (float)matrix4d.m12())
                .putFloat(int0 + 28, (float)matrix4d.m13())
                .putFloat(int0 + 32, (float)matrix4d.m20())
                .putFloat(int0 + 36, (float)matrix4d.m21())
                .putFloat(int0 + 40, (float)matrix4d.m22())
                .putFloat(int0 + 44, (float)matrix4d.m23())
                .putFloat(int0 + 48, (float)matrix4d.m30())
                .putFloat(int0 + 52, (float)matrix4d.m31())
                .putFloat(int0 + 56, (float)matrix4d.m32())
                .putFloat(int0 + 60, (float)matrix4d.m33());
        }

        @Override
        public void putf(Matrix4x3d matrix4x3d, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, (float)matrix4x3d.m00())
                .put(int0 + 1, (float)matrix4x3d.m01())
                .put(int0 + 2, (float)matrix4x3d.m02())
                .put(int0 + 3, (float)matrix4x3d.m10())
                .put(int0 + 4, (float)matrix4x3d.m11())
                .put(int0 + 5, (float)matrix4x3d.m12())
                .put(int0 + 6, (float)matrix4x3d.m20())
                .put(int0 + 7, (float)matrix4x3d.m21())
                .put(int0 + 8, (float)matrix4x3d.m22())
                .put(int0 + 9, (float)matrix4x3d.m30())
                .put(int0 + 10, (float)matrix4x3d.m31())
                .put(int0 + 11, (float)matrix4x3d.m32());
        }

        @Override
        public void putf(Matrix4x3d matrix4x3d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, (float)matrix4x3d.m00())
                .putFloat(int0 + 4, (float)matrix4x3d.m01())
                .putFloat(int0 + 8, (float)matrix4x3d.m02())
                .putFloat(int0 + 12, (float)matrix4x3d.m10())
                .putFloat(int0 + 16, (float)matrix4x3d.m11())
                .putFloat(int0 + 20, (float)matrix4x3d.m12())
                .putFloat(int0 + 24, (float)matrix4x3d.m20())
                .putFloat(int0 + 28, (float)matrix4x3d.m21())
                .putFloat(int0 + 32, (float)matrix4x3d.m22())
                .putFloat(int0 + 36, (float)matrix4x3d.m30())
                .putFloat(int0 + 40, (float)matrix4x3d.m31())
                .putFloat(int0 + 44, (float)matrix4x3d.m32());
        }

        @Override
        public void putTransposed(Matrix4d matrix4d, int int0, DoubleBuffer doubleBuffer) {
            doubleBuffer.put(int0, matrix4d.m00())
                .put(int0 + 1, matrix4d.m10())
                .put(int0 + 2, matrix4d.m20())
                .put(int0 + 3, matrix4d.m30())
                .put(int0 + 4, matrix4d.m01())
                .put(int0 + 5, matrix4d.m11())
                .put(int0 + 6, matrix4d.m21())
                .put(int0 + 7, matrix4d.m31())
                .put(int0 + 8, matrix4d.m02())
                .put(int0 + 9, matrix4d.m12())
                .put(int0 + 10, matrix4d.m22())
                .put(int0 + 11, matrix4d.m32())
                .put(int0 + 12, matrix4d.m03())
                .put(int0 + 13, matrix4d.m13())
                .put(int0 + 14, matrix4d.m23())
                .put(int0 + 15, matrix4d.m33());
        }

        @Override
        public void putTransposed(Matrix4d matrix4d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putDouble(int0, matrix4d.m00())
                .putDouble(int0 + 8, matrix4d.m10())
                .putDouble(int0 + 16, matrix4d.m20())
                .putDouble(int0 + 24, matrix4d.m30())
                .putDouble(int0 + 32, matrix4d.m01())
                .putDouble(int0 + 40, matrix4d.m11())
                .putDouble(int0 + 48, matrix4d.m21())
                .putDouble(int0 + 56, matrix4d.m31())
                .putDouble(int0 + 64, matrix4d.m02())
                .putDouble(int0 + 72, matrix4d.m12())
                .putDouble(int0 + 80, matrix4d.m22())
                .putDouble(int0 + 88, matrix4d.m32())
                .putDouble(int0 + 96, matrix4d.m03())
                .putDouble(int0 + 104, matrix4d.m13())
                .putDouble(int0 + 112, matrix4d.m23())
                .putDouble(int0 + 120, matrix4d.m33());
        }

        @Override
        public void put4x3Transposed(Matrix4d matrix4d, int int0, DoubleBuffer doubleBuffer) {
            doubleBuffer.put(int0, matrix4d.m00())
                .put(int0 + 1, matrix4d.m10())
                .put(int0 + 2, matrix4d.m20())
                .put(int0 + 3, matrix4d.m30())
                .put(int0 + 4, matrix4d.m01())
                .put(int0 + 5, matrix4d.m11())
                .put(int0 + 6, matrix4d.m21())
                .put(int0 + 7, matrix4d.m31())
                .put(int0 + 8, matrix4d.m02())
                .put(int0 + 9, matrix4d.m12())
                .put(int0 + 10, matrix4d.m22())
                .put(int0 + 11, matrix4d.m32());
        }

        @Override
        public void put4x3Transposed(Matrix4d matrix4d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putDouble(int0, matrix4d.m00())
                .putDouble(int0 + 8, matrix4d.m10())
                .putDouble(int0 + 16, matrix4d.m20())
                .putDouble(int0 + 24, matrix4d.m30())
                .putDouble(int0 + 32, matrix4d.m01())
                .putDouble(int0 + 40, matrix4d.m11())
                .putDouble(int0 + 48, matrix4d.m21())
                .putDouble(int0 + 56, matrix4d.m31())
                .putDouble(int0 + 64, matrix4d.m02())
                .putDouble(int0 + 72, matrix4d.m12())
                .putDouble(int0 + 80, matrix4d.m22())
                .putDouble(int0 + 88, matrix4d.m32());
        }

        @Override
        public void putTransposed(Matrix4x3d matrix4x3d, int int0, DoubleBuffer doubleBuffer) {
            doubleBuffer.put(int0, matrix4x3d.m00())
                .put(int0 + 1, matrix4x3d.m10())
                .put(int0 + 2, matrix4x3d.m20())
                .put(int0 + 3, matrix4x3d.m30())
                .put(int0 + 4, matrix4x3d.m01())
                .put(int0 + 5, matrix4x3d.m11())
                .put(int0 + 6, matrix4x3d.m21())
                .put(int0 + 7, matrix4x3d.m31())
                .put(int0 + 8, matrix4x3d.m02())
                .put(int0 + 9, matrix4x3d.m12())
                .put(int0 + 10, matrix4x3d.m22())
                .put(int0 + 11, matrix4x3d.m32());
        }

        @Override
        public void putTransposed(Matrix4x3d matrix4x3d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putDouble(int0, matrix4x3d.m00())
                .putDouble(int0 + 8, matrix4x3d.m10())
                .putDouble(int0 + 16, matrix4x3d.m20())
                .putDouble(int0 + 24, matrix4x3d.m30())
                .putDouble(int0 + 32, matrix4x3d.m01())
                .putDouble(int0 + 40, matrix4x3d.m11())
                .putDouble(int0 + 48, matrix4x3d.m21())
                .putDouble(int0 + 56, matrix4x3d.m31())
                .putDouble(int0 + 64, matrix4x3d.m02())
                .putDouble(int0 + 72, matrix4x3d.m12())
                .putDouble(int0 + 80, matrix4x3d.m22())
                .putDouble(int0 + 88, matrix4x3d.m32());
        }

        @Override
        public void putTransposed(Matrix2d matrix2d, int int0, DoubleBuffer doubleBuffer) {
            doubleBuffer.put(int0, matrix2d.m00()).put(int0 + 1, matrix2d.m10()).put(int0 + 2, matrix2d.m01()).put(int0 + 3, matrix2d.m11());
        }

        @Override
        public void putTransposed(Matrix2d matrix2d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putDouble(int0, matrix2d.m00())
                .putDouble(int0 + 8, matrix2d.m10())
                .putDouble(int0 + 16, matrix2d.m01())
                .putDouble(int0 + 24, matrix2d.m11());
        }

        @Override
        public void putfTransposed(Matrix4x3d matrix4x3d, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, (float)matrix4x3d.m00())
                .put(int0 + 1, (float)matrix4x3d.m10())
                .put(int0 + 2, (float)matrix4x3d.m20())
                .put(int0 + 3, (float)matrix4x3d.m30())
                .put(int0 + 4, (float)matrix4x3d.m01())
                .put(int0 + 5, (float)matrix4x3d.m11())
                .put(int0 + 6, (float)matrix4x3d.m21())
                .put(int0 + 7, (float)matrix4x3d.m31())
                .put(int0 + 8, (float)matrix4x3d.m02())
                .put(int0 + 9, (float)matrix4x3d.m12())
                .put(int0 + 10, (float)matrix4x3d.m22())
                .put(int0 + 11, (float)matrix4x3d.m32());
        }

        @Override
        public void putfTransposed(Matrix4x3d matrix4x3d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, (float)matrix4x3d.m00())
                .putFloat(int0 + 4, (float)matrix4x3d.m10())
                .putFloat(int0 + 8, (float)matrix4x3d.m20())
                .putFloat(int0 + 12, (float)matrix4x3d.m30())
                .putFloat(int0 + 16, (float)matrix4x3d.m01())
                .putFloat(int0 + 20, (float)matrix4x3d.m11())
                .putFloat(int0 + 24, (float)matrix4x3d.m21())
                .putFloat(int0 + 28, (float)matrix4x3d.m31())
                .putFloat(int0 + 32, (float)matrix4x3d.m02())
                .putFloat(int0 + 36, (float)matrix4x3d.m12())
                .putFloat(int0 + 40, (float)matrix4x3d.m22())
                .putFloat(int0 + 44, (float)matrix4x3d.m32());
        }

        @Override
        public void putfTransposed(Matrix2d matrix2d, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, (float)matrix2d.m00())
                .put(int0 + 1, (float)matrix2d.m10())
                .put(int0 + 2, (float)matrix2d.m01())
                .put(int0 + 3, (float)matrix2d.m11());
        }

        @Override
        public void putfTransposed(Matrix2d matrix2d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, (float)matrix2d.m00())
                .putFloat(int0 + 4, (float)matrix2d.m10())
                .putFloat(int0 + 8, (float)matrix2d.m01())
                .putFloat(int0 + 12, (float)matrix2d.m11());
        }

        @Override
        public void putfTransposed(Matrix4d matrix4d, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, (float)matrix4d.m00())
                .put(int0 + 1, (float)matrix4d.m10())
                .put(int0 + 2, (float)matrix4d.m20())
                .put(int0 + 3, (float)matrix4d.m30())
                .put(int0 + 4, (float)matrix4d.m01())
                .put(int0 + 5, (float)matrix4d.m11())
                .put(int0 + 6, (float)matrix4d.m21())
                .put(int0 + 7, (float)matrix4d.m31())
                .put(int0 + 8, (float)matrix4d.m02())
                .put(int0 + 9, (float)matrix4d.m12())
                .put(int0 + 10, (float)matrix4d.m22())
                .put(int0 + 11, (float)matrix4d.m32())
                .put(int0 + 12, (float)matrix4d.m03())
                .put(int0 + 13, (float)matrix4d.m13())
                .put(int0 + 14, (float)matrix4d.m23())
                .put(int0 + 15, (float)matrix4d.m33());
        }

        @Override
        public void putfTransposed(Matrix4d matrix4d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, (float)matrix4d.m00())
                .putFloat(int0 + 4, (float)matrix4d.m10())
                .putFloat(int0 + 8, (float)matrix4d.m20())
                .putFloat(int0 + 12, (float)matrix4d.m30())
                .putFloat(int0 + 16, (float)matrix4d.m01())
                .putFloat(int0 + 20, (float)matrix4d.m11())
                .putFloat(int0 + 24, (float)matrix4d.m21())
                .putFloat(int0 + 28, (float)matrix4d.m31())
                .putFloat(int0 + 32, (float)matrix4d.m02())
                .putFloat(int0 + 36, (float)matrix4d.m12())
                .putFloat(int0 + 40, (float)matrix4d.m22())
                .putFloat(int0 + 44, (float)matrix4d.m32())
                .putFloat(int0 + 48, (float)matrix4d.m03())
                .putFloat(int0 + 52, (float)matrix4d.m13())
                .putFloat(int0 + 56, (float)matrix4d.m23())
                .putFloat(int0 + 60, (float)matrix4d.m33());
        }

        public void put0(Matrix3f matrix3f, FloatBuffer floatBuffer) {
            floatBuffer.put(0, matrix3f.m00())
                .put(1, matrix3f.m01())
                .put(2, matrix3f.m02())
                .put(3, matrix3f.m10())
                .put(4, matrix3f.m11())
                .put(5, matrix3f.m12())
                .put(6, matrix3f.m20())
                .put(7, matrix3f.m21())
                .put(8, matrix3f.m22());
        }

        public void putN(Matrix3f matrix3f, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, matrix3f.m00())
                .put(int0 + 1, matrix3f.m01())
                .put(int0 + 2, matrix3f.m02())
                .put(int0 + 3, matrix3f.m10())
                .put(int0 + 4, matrix3f.m11())
                .put(int0 + 5, matrix3f.m12())
                .put(int0 + 6, matrix3f.m20())
                .put(int0 + 7, matrix3f.m21())
                .put(int0 + 8, matrix3f.m22());
        }

        @Override
        public void put(Matrix3f matrix3f, int int0, FloatBuffer floatBuffer) {
            if (int0 == 0) {
                this.put0(matrix3f, floatBuffer);
            } else {
                this.putN(matrix3f, int0, floatBuffer);
            }
        }

        public void put0(Matrix3f matrix3f, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(0, matrix3f.m00())
                .putFloat(4, matrix3f.m01())
                .putFloat(8, matrix3f.m02())
                .putFloat(12, matrix3f.m10())
                .putFloat(16, matrix3f.m11())
                .putFloat(20, matrix3f.m12())
                .putFloat(24, matrix3f.m20())
                .putFloat(28, matrix3f.m21())
                .putFloat(32, matrix3f.m22());
        }

        public void putN(Matrix3f matrix3f, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, matrix3f.m00())
                .putFloat(int0 + 4, matrix3f.m01())
                .putFloat(int0 + 8, matrix3f.m02())
                .putFloat(int0 + 12, matrix3f.m10())
                .putFloat(int0 + 16, matrix3f.m11())
                .putFloat(int0 + 20, matrix3f.m12())
                .putFloat(int0 + 24, matrix3f.m20())
                .putFloat(int0 + 28, matrix3f.m21())
                .putFloat(int0 + 32, matrix3f.m22());
        }

        @Override
        public void put(Matrix3f matrix3f, int int0, ByteBuffer byteBuffer) {
            if (int0 == 0) {
                this.put0(matrix3f, byteBuffer);
            } else {
                this.putN(matrix3f, int0, byteBuffer);
            }
        }

        public void put3x4_0(Matrix3f matrix3f, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(0, matrix3f.m00())
                .putFloat(4, matrix3f.m01())
                .putFloat(8, matrix3f.m02())
                .putFloat(12, 0.0F)
                .putFloat(16, matrix3f.m10())
                .putFloat(20, matrix3f.m11())
                .putFloat(24, matrix3f.m12())
                .putFloat(28, 0.0F)
                .putFloat(32, matrix3f.m20())
                .putFloat(36, matrix3f.m21())
                .putFloat(40, matrix3f.m22())
                .putFloat(44, 0.0F);
        }

        private void put3x4_N(Matrix3f matrix3f, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, matrix3f.m00())
                .putFloat(int0 + 4, matrix3f.m01())
                .putFloat(int0 + 8, matrix3f.m02())
                .putFloat(int0 + 12, 0.0F)
                .putFloat(int0 + 16, matrix3f.m10())
                .putFloat(int0 + 20, matrix3f.m11())
                .putFloat(int0 + 24, matrix3f.m12())
                .putFloat(int0 + 28, 0.0F)
                .putFloat(int0 + 32, matrix3f.m20())
                .putFloat(int0 + 36, matrix3f.m21())
                .putFloat(int0 + 40, matrix3f.m22())
                .putFloat(int0 + 44, 0.0F);
        }

        @Override
        public void put3x4(Matrix3f matrix3f, int int0, ByteBuffer byteBuffer) {
            if (int0 == 0) {
                this.put3x4_0(matrix3f, byteBuffer);
            } else {
                this.put3x4_N(matrix3f, int0, byteBuffer);
            }
        }

        public void put3x4_0(Matrix3f matrix3f, FloatBuffer floatBuffer) {
            floatBuffer.put(0, matrix3f.m00())
                .put(1, matrix3f.m01())
                .put(2, matrix3f.m02())
                .put(3, 0.0F)
                .put(4, matrix3f.m10())
                .put(5, matrix3f.m11())
                .put(6, matrix3f.m12())
                .put(7, 0.0F)
                .put(8, matrix3f.m20())
                .put(9, matrix3f.m21())
                .put(10, matrix3f.m22())
                .put(11, 0.0F);
        }

        public void put3x4_N(Matrix3f matrix3f, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, matrix3f.m00())
                .put(int0 + 1, matrix3f.m01())
                .put(int0 + 2, matrix3f.m02())
                .put(int0 + 3, 0.0F)
                .put(int0 + 4, matrix3f.m10())
                .put(int0 + 5, matrix3f.m11())
                .put(int0 + 6, matrix3f.m12())
                .put(int0 + 7, 0.0F)
                .put(int0 + 8, matrix3f.m20())
                .put(int0 + 9, matrix3f.m21())
                .put(int0 + 10, matrix3f.m22())
                .put(int0 + 11, 0.0F);
        }

        @Override
        public void put3x4(Matrix3f matrix3f, int int0, FloatBuffer floatBuffer) {
            if (int0 == 0) {
                this.put3x4_0(matrix3f, floatBuffer);
            } else {
                this.put3x4_N(matrix3f, int0, floatBuffer);
            }
        }

        @Override
        public void put(Matrix3d matrix3d, int int0, DoubleBuffer doubleBuffer) {
            doubleBuffer.put(int0, matrix3d.m00())
                .put(int0 + 1, matrix3d.m01())
                .put(int0 + 2, matrix3d.m02())
                .put(int0 + 3, matrix3d.m10())
                .put(int0 + 4, matrix3d.m11())
                .put(int0 + 5, matrix3d.m12())
                .put(int0 + 6, matrix3d.m20())
                .put(int0 + 7, matrix3d.m21())
                .put(int0 + 8, matrix3d.m22());
        }

        @Override
        public void put(Matrix3d matrix3d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putDouble(int0, matrix3d.m00())
                .putDouble(int0 + 8, matrix3d.m01())
                .putDouble(int0 + 16, matrix3d.m02())
                .putDouble(int0 + 24, matrix3d.m10())
                .putDouble(int0 + 32, matrix3d.m11())
                .putDouble(int0 + 40, matrix3d.m12())
                .putDouble(int0 + 48, matrix3d.m20())
                .putDouble(int0 + 56, matrix3d.m21())
                .putDouble(int0 + 64, matrix3d.m22());
        }

        @Override
        public void put(Matrix3x2f matrix3x2f, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, matrix3x2f.m00())
                .put(int0 + 1, matrix3x2f.m01())
                .put(int0 + 2, matrix3x2f.m10())
                .put(int0 + 3, matrix3x2f.m11())
                .put(int0 + 4, matrix3x2f.m20())
                .put(int0 + 5, matrix3x2f.m21());
        }

        @Override
        public void put(Matrix3x2f matrix3x2f, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, matrix3x2f.m00())
                .putFloat(int0 + 4, matrix3x2f.m01())
                .putFloat(int0 + 8, matrix3x2f.m10())
                .putFloat(int0 + 12, matrix3x2f.m11())
                .putFloat(int0 + 16, matrix3x2f.m20())
                .putFloat(int0 + 20, matrix3x2f.m21());
        }

        @Override
        public void put(Matrix3x2d matrix3x2d, int int0, DoubleBuffer doubleBuffer) {
            doubleBuffer.put(int0, matrix3x2d.m00())
                .put(int0 + 1, matrix3x2d.m01())
                .put(int0 + 2, matrix3x2d.m10())
                .put(int0 + 3, matrix3x2d.m11())
                .put(int0 + 4, matrix3x2d.m20())
                .put(int0 + 5, matrix3x2d.m21());
        }

        @Override
        public void put(Matrix3x2d matrix3x2d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putDouble(int0, matrix3x2d.m00())
                .putDouble(int0 + 8, matrix3x2d.m01())
                .putDouble(int0 + 16, matrix3x2d.m10())
                .putDouble(int0 + 24, matrix3x2d.m11())
                .putDouble(int0 + 32, matrix3x2d.m20())
                .putDouble(int0 + 40, matrix3x2d.m21());
        }

        @Override
        public void putf(Matrix3d matrix3d, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, (float)matrix3d.m00())
                .put(int0 + 1, (float)matrix3d.m01())
                .put(int0 + 2, (float)matrix3d.m02())
                .put(int0 + 3, (float)matrix3d.m10())
                .put(int0 + 4, (float)matrix3d.m11())
                .put(int0 + 5, (float)matrix3d.m12())
                .put(int0 + 6, (float)matrix3d.m20())
                .put(int0 + 7, (float)matrix3d.m21())
                .put(int0 + 8, (float)matrix3d.m22());
        }

        @Override
        public void put(Matrix2f matrix2f, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, matrix2f.m00()).put(int0 + 1, matrix2f.m01()).put(int0 + 2, matrix2f.m10()).put(int0 + 3, matrix2f.m11());
        }

        @Override
        public void put(Matrix2f matrix2f, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, matrix2f.m00()).putFloat(int0 + 4, matrix2f.m01()).putFloat(int0 + 8, matrix2f.m10()).putFloat(int0 + 12, matrix2f.m11());
        }

        @Override
        public void put(Matrix2d matrix2d, int int0, DoubleBuffer doubleBuffer) {
            doubleBuffer.put(int0, matrix2d.m00()).put(int0 + 1, matrix2d.m01()).put(int0 + 2, matrix2d.m10()).put(int0 + 3, matrix2d.m11());
        }

        @Override
        public void put(Matrix2d matrix2d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putDouble(int0, matrix2d.m00())
                .putDouble(int0 + 8, matrix2d.m01())
                .putDouble(int0 + 16, matrix2d.m10())
                .putDouble(int0 + 24, matrix2d.m11());
        }

        @Override
        public void putf(Matrix2d matrix2d, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, (float)matrix2d.m00())
                .put(int0 + 1, (float)matrix2d.m01())
                .put(int0 + 2, (float)matrix2d.m10())
                .put(int0 + 3, (float)matrix2d.m11());
        }

        @Override
        public void putf(Matrix2d matrix2d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, (float)matrix2d.m00())
                .putFloat(int0 + 4, (float)matrix2d.m01())
                .putFloat(int0 + 8, (float)matrix2d.m10())
                .putFloat(int0 + 12, (float)matrix2d.m11());
        }

        @Override
        public void putf(Matrix3d matrix3d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, (float)matrix3d.m00())
                .putFloat(int0 + 4, (float)matrix3d.m01())
                .putFloat(int0 + 8, (float)matrix3d.m02())
                .putFloat(int0 + 12, (float)matrix3d.m10())
                .putFloat(int0 + 16, (float)matrix3d.m11())
                .putFloat(int0 + 20, (float)matrix3d.m12())
                .putFloat(int0 + 24, (float)matrix3d.m20())
                .putFloat(int0 + 28, (float)matrix3d.m21())
                .putFloat(int0 + 32, (float)matrix3d.m22());
        }

        @Override
        public void put(Vector4d vector4d, int int0, DoubleBuffer doubleBuffer) {
            doubleBuffer.put(int0, vector4d.x).put(int0 + 1, vector4d.y).put(int0 + 2, vector4d.z).put(int0 + 3, vector4d.w);
        }

        @Override
        public void put(Vector4d vector4d, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, (float)vector4d.x).put(int0 + 1, (float)vector4d.y).put(int0 + 2, (float)vector4d.z).put(int0 + 3, (float)vector4d.w);
        }

        @Override
        public void put(Vector4d vector4d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putDouble(int0, vector4d.x).putDouble(int0 + 8, vector4d.y).putDouble(int0 + 16, vector4d.z).putDouble(int0 + 24, vector4d.w);
        }

        @Override
        public void putf(Vector4d vector4d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, (float)vector4d.x)
                .putFloat(int0 + 4, (float)vector4d.y)
                .putFloat(int0 + 8, (float)vector4d.z)
                .putFloat(int0 + 12, (float)vector4d.w);
        }

        @Override
        public void put(Vector4f vector4f, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, vector4f.x).put(int0 + 1, vector4f.y).put(int0 + 2, vector4f.z).put(int0 + 3, vector4f.w);
        }

        @Override
        public void put(Vector4f vector4f, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, vector4f.x).putFloat(int0 + 4, vector4f.y).putFloat(int0 + 8, vector4f.z).putFloat(int0 + 12, vector4f.w);
        }

        @Override
        public void put(Vector4i vector4i, int int0, IntBuffer intBuffer) {
            intBuffer.put(int0, vector4i.x).put(int0 + 1, vector4i.y).put(int0 + 2, vector4i.z).put(int0 + 3, vector4i.w);
        }

        @Override
        public void put(Vector4i vector4i, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putInt(int0, vector4i.x).putInt(int0 + 4, vector4i.y).putInt(int0 + 8, vector4i.z).putInt(int0 + 12, vector4i.w);
        }

        @Override
        public void put(Vector3f vector3f, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, vector3f.x).put(int0 + 1, vector3f.y).put(int0 + 2, vector3f.z);
        }

        @Override
        public void put(Vector3f vector3f, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, vector3f.x).putFloat(int0 + 4, vector3f.y).putFloat(int0 + 8, vector3f.z);
        }

        @Override
        public void put(Vector3d vector3d, int int0, DoubleBuffer doubleBuffer) {
            doubleBuffer.put(int0, vector3d.x).put(int0 + 1, vector3d.y).put(int0 + 2, vector3d.z);
        }

        @Override
        public void put(Vector3d vector3d, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, (float)vector3d.x).put(int0 + 1, (float)vector3d.y).put(int0 + 2, (float)vector3d.z);
        }

        @Override
        public void put(Vector3d vector3d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putDouble(int0, vector3d.x).putDouble(int0 + 8, vector3d.y).putDouble(int0 + 16, vector3d.z);
        }

        @Override
        public void putf(Vector3d vector3d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, (float)vector3d.x).putFloat(int0 + 4, (float)vector3d.y).putFloat(int0 + 8, (float)vector3d.z);
        }

        @Override
        public void put(Vector3i vector3i, int int0, IntBuffer intBuffer) {
            intBuffer.put(int0, vector3i.x).put(int0 + 1, vector3i.y).put(int0 + 2, vector3i.z);
        }

        @Override
        public void put(Vector3i vector3i, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putInt(int0, vector3i.x).putInt(int0 + 4, vector3i.y).putInt(int0 + 8, vector3i.z);
        }

        @Override
        public void put(Vector2f vector2f, int int0, FloatBuffer floatBuffer) {
            floatBuffer.put(int0, vector2f.x).put(int0 + 1, vector2f.y);
        }

        @Override
        public void put(Vector2f vector2f, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(int0, vector2f.x).putFloat(int0 + 4, vector2f.y);
        }

        @Override
        public void put(Vector2d vector2d, int int0, DoubleBuffer doubleBuffer) {
            doubleBuffer.put(int0, vector2d.x).put(int0 + 1, vector2d.y);
        }

        @Override
        public void put(Vector2d vector2d, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putDouble(int0, vector2d.x).putDouble(int0 + 8, vector2d.y);
        }

        @Override
        public void put(Vector2i vector2i, int int0, IntBuffer intBuffer) {
            intBuffer.put(int0, vector2i.x).put(int0 + 1, vector2i.y);
        }

        @Override
        public void put(Vector2i vector2i, int int0, ByteBuffer byteBuffer) {
            byteBuffer.putInt(int0, vector2i.x).putInt(int0 + 4, vector2i.y);
        }

        @Override
        public void get(Matrix4f matrix4f, int int0, FloatBuffer floatBuffer) {
            matrix4f._m00(floatBuffer.get(int0))
                ._m01(floatBuffer.get(int0 + 1))
                ._m02(floatBuffer.get(int0 + 2))
                ._m03(floatBuffer.get(int0 + 3))
                ._m10(floatBuffer.get(int0 + 4))
                ._m11(floatBuffer.get(int0 + 5))
                ._m12(floatBuffer.get(int0 + 6))
                ._m13(floatBuffer.get(int0 + 7))
                ._m20(floatBuffer.get(int0 + 8))
                ._m21(floatBuffer.get(int0 + 9))
                ._m22(floatBuffer.get(int0 + 10))
                ._m23(floatBuffer.get(int0 + 11))
                ._m30(floatBuffer.get(int0 + 12))
                ._m31(floatBuffer.get(int0 + 13))
                ._m32(floatBuffer.get(int0 + 14))
                ._m33(floatBuffer.get(int0 + 15));
        }

        @Override
        public void get(Matrix4f matrix4f, int int0, ByteBuffer byteBuffer) {
            matrix4f._m00(byteBuffer.getFloat(int0))
                ._m01(byteBuffer.getFloat(int0 + 4))
                ._m02(byteBuffer.getFloat(int0 + 8))
                ._m03(byteBuffer.getFloat(int0 + 12))
                ._m10(byteBuffer.getFloat(int0 + 16))
                ._m11(byteBuffer.getFloat(int0 + 20))
                ._m12(byteBuffer.getFloat(int0 + 24))
                ._m13(byteBuffer.getFloat(int0 + 28))
                ._m20(byteBuffer.getFloat(int0 + 32))
                ._m21(byteBuffer.getFloat(int0 + 36))
                ._m22(byteBuffer.getFloat(int0 + 40))
                ._m23(byteBuffer.getFloat(int0 + 44))
                ._m30(byteBuffer.getFloat(int0 + 48))
                ._m31(byteBuffer.getFloat(int0 + 52))
                ._m32(byteBuffer.getFloat(int0 + 56))
                ._m33(byteBuffer.getFloat(int0 + 60));
        }

        @Override
        public void getTransposed(Matrix4f matrix4f, int int0, FloatBuffer floatBuffer) {
            matrix4f._m00(floatBuffer.get(int0))
                ._m10(floatBuffer.get(int0 + 1))
                ._m20(floatBuffer.get(int0 + 2))
                ._m30(floatBuffer.get(int0 + 3))
                ._m01(floatBuffer.get(int0 + 4))
                ._m11(floatBuffer.get(int0 + 5))
                ._m21(floatBuffer.get(int0 + 6))
                ._m31(floatBuffer.get(int0 + 7))
                ._m02(floatBuffer.get(int0 + 8))
                ._m12(floatBuffer.get(int0 + 9))
                ._m22(floatBuffer.get(int0 + 10))
                ._m32(floatBuffer.get(int0 + 11))
                ._m03(floatBuffer.get(int0 + 12))
                ._m13(floatBuffer.get(int0 + 13))
                ._m23(floatBuffer.get(int0 + 14))
                ._m33(floatBuffer.get(int0 + 15));
        }

        @Override
        public void getTransposed(Matrix4f matrix4f, int int0, ByteBuffer byteBuffer) {
            matrix4f._m00(byteBuffer.getFloat(int0))
                ._m10(byteBuffer.getFloat(int0 + 4))
                ._m20(byteBuffer.getFloat(int0 + 8))
                ._m30(byteBuffer.getFloat(int0 + 12))
                ._m01(byteBuffer.getFloat(int0 + 16))
                ._m11(byteBuffer.getFloat(int0 + 20))
                ._m21(byteBuffer.getFloat(int0 + 24))
                ._m31(byteBuffer.getFloat(int0 + 28))
                ._m02(byteBuffer.getFloat(int0 + 32))
                ._m12(byteBuffer.getFloat(int0 + 36))
                ._m22(byteBuffer.getFloat(int0 + 40))
                ._m32(byteBuffer.getFloat(int0 + 44))
                ._m03(byteBuffer.getFloat(int0 + 48))
                ._m13(byteBuffer.getFloat(int0 + 52))
                ._m23(byteBuffer.getFloat(int0 + 56))
                ._m33(byteBuffer.getFloat(int0 + 60));
        }

        @Override
        public void get(Matrix4x3f matrix4x3f, int int0, FloatBuffer floatBuffer) {
            matrix4x3f._m00(floatBuffer.get(int0))
                ._m01(floatBuffer.get(int0 + 1))
                ._m02(floatBuffer.get(int0 + 2))
                ._m10(floatBuffer.get(int0 + 3))
                ._m11(floatBuffer.get(int0 + 4))
                ._m12(floatBuffer.get(int0 + 5))
                ._m20(floatBuffer.get(int0 + 6))
                ._m21(floatBuffer.get(int0 + 7))
                ._m22(floatBuffer.get(int0 + 8))
                ._m30(floatBuffer.get(int0 + 9))
                ._m31(floatBuffer.get(int0 + 10))
                ._m32(floatBuffer.get(int0 + 11));
        }

        @Override
        public void get(Matrix4x3f matrix4x3f, int int0, ByteBuffer byteBuffer) {
            matrix4x3f._m00(byteBuffer.getFloat(int0))
                ._m01(byteBuffer.getFloat(int0 + 4))
                ._m02(byteBuffer.getFloat(int0 + 8))
                ._m10(byteBuffer.getFloat(int0 + 12))
                ._m11(byteBuffer.getFloat(int0 + 16))
                ._m12(byteBuffer.getFloat(int0 + 20))
                ._m20(byteBuffer.getFloat(int0 + 24))
                ._m21(byteBuffer.getFloat(int0 + 28))
                ._m22(byteBuffer.getFloat(int0 + 32))
                ._m30(byteBuffer.getFloat(int0 + 36))
                ._m31(byteBuffer.getFloat(int0 + 40))
                ._m32(byteBuffer.getFloat(int0 + 44));
        }

        @Override
        public void get(Matrix4d matrix4d, int int0, DoubleBuffer doubleBuffer) {
            matrix4d._m00(doubleBuffer.get(int0))
                ._m01(doubleBuffer.get(int0 + 1))
                ._m02(doubleBuffer.get(int0 + 2))
                ._m03(doubleBuffer.get(int0 + 3))
                ._m10(doubleBuffer.get(int0 + 4))
                ._m11(doubleBuffer.get(int0 + 5))
                ._m12(doubleBuffer.get(int0 + 6))
                ._m13(doubleBuffer.get(int0 + 7))
                ._m20(doubleBuffer.get(int0 + 8))
                ._m21(doubleBuffer.get(int0 + 9))
                ._m22(doubleBuffer.get(int0 + 10))
                ._m23(doubleBuffer.get(int0 + 11))
                ._m30(doubleBuffer.get(int0 + 12))
                ._m31(doubleBuffer.get(int0 + 13))
                ._m32(doubleBuffer.get(int0 + 14))
                ._m33(doubleBuffer.get(int0 + 15));
        }

        @Override
        public void get(Matrix4d matrix4d, int int0, ByteBuffer byteBuffer) {
            matrix4d._m00(byteBuffer.getDouble(int0))
                ._m01(byteBuffer.getDouble(int0 + 8))
                ._m02(byteBuffer.getDouble(int0 + 16))
                ._m03(byteBuffer.getDouble(int0 + 24))
                ._m10(byteBuffer.getDouble(int0 + 32))
                ._m11(byteBuffer.getDouble(int0 + 40))
                ._m12(byteBuffer.getDouble(int0 + 48))
                ._m13(byteBuffer.getDouble(int0 + 56))
                ._m20(byteBuffer.getDouble(int0 + 64))
                ._m21(byteBuffer.getDouble(int0 + 72))
                ._m22(byteBuffer.getDouble(int0 + 80))
                ._m23(byteBuffer.getDouble(int0 + 88))
                ._m30(byteBuffer.getDouble(int0 + 96))
                ._m31(byteBuffer.getDouble(int0 + 104))
                ._m32(byteBuffer.getDouble(int0 + 112))
                ._m33(byteBuffer.getDouble(int0 + 120));
        }

        @Override
        public void get(Matrix4x3d matrix4x3d, int int0, DoubleBuffer doubleBuffer) {
            matrix4x3d._m00(doubleBuffer.get(int0))
                ._m01(doubleBuffer.get(int0 + 1))
                ._m02(doubleBuffer.get(int0 + 2))
                ._m10(doubleBuffer.get(int0 + 3))
                ._m11(doubleBuffer.get(int0 + 4))
                ._m12(doubleBuffer.get(int0 + 5))
                ._m20(doubleBuffer.get(int0 + 6))
                ._m21(doubleBuffer.get(int0 + 7))
                ._m22(doubleBuffer.get(int0 + 8))
                ._m30(doubleBuffer.get(int0 + 9))
                ._m31(doubleBuffer.get(int0 + 10))
                ._m32(doubleBuffer.get(int0 + 11));
        }

        @Override
        public void get(Matrix4x3d matrix4x3d, int int0, ByteBuffer byteBuffer) {
            matrix4x3d._m00(byteBuffer.getDouble(int0))
                ._m01(byteBuffer.getDouble(int0 + 8))
                ._m02(byteBuffer.getDouble(int0 + 16))
                ._m10(byteBuffer.getDouble(int0 + 24))
                ._m11(byteBuffer.getDouble(int0 + 32))
                ._m12(byteBuffer.getDouble(int0 + 40))
                ._m20(byteBuffer.getDouble(int0 + 48))
                ._m21(byteBuffer.getDouble(int0 + 56))
                ._m22(byteBuffer.getDouble(int0 + 64))
                ._m30(byteBuffer.getDouble(int0 + 72))
                ._m31(byteBuffer.getDouble(int0 + 80))
                ._m32(byteBuffer.getDouble(int0 + 88));
        }

        @Override
        public void getf(Matrix4d matrix4d, int int0, FloatBuffer floatBuffer) {
            matrix4d._m00(floatBuffer.get(int0))
                ._m01(floatBuffer.get(int0 + 1))
                ._m02(floatBuffer.get(int0 + 2))
                ._m03(floatBuffer.get(int0 + 3))
                ._m10(floatBuffer.get(int0 + 4))
                ._m11(floatBuffer.get(int0 + 5))
                ._m12(floatBuffer.get(int0 + 6))
                ._m13(floatBuffer.get(int0 + 7))
                ._m20(floatBuffer.get(int0 + 8))
                ._m21(floatBuffer.get(int0 + 9))
                ._m22(floatBuffer.get(int0 + 10))
                ._m23(floatBuffer.get(int0 + 11))
                ._m30(floatBuffer.get(int0 + 12))
                ._m31(floatBuffer.get(int0 + 13))
                ._m32(floatBuffer.get(int0 + 14))
                ._m33(floatBuffer.get(int0 + 15));
        }

        @Override
        public void getf(Matrix4d matrix4d, int int0, ByteBuffer byteBuffer) {
            matrix4d._m00(byteBuffer.getFloat(int0))
                ._m01(byteBuffer.getFloat(int0 + 4))
                ._m02(byteBuffer.getFloat(int0 + 8))
                ._m03(byteBuffer.getFloat(int0 + 12))
                ._m10(byteBuffer.getFloat(int0 + 16))
                ._m11(byteBuffer.getFloat(int0 + 20))
                ._m12(byteBuffer.getFloat(int0 + 24))
                ._m13(byteBuffer.getFloat(int0 + 28))
                ._m20(byteBuffer.getFloat(int0 + 32))
                ._m21(byteBuffer.getFloat(int0 + 36))
                ._m22(byteBuffer.getFloat(int0 + 40))
                ._m23(byteBuffer.getFloat(int0 + 44))
                ._m30(byteBuffer.getFloat(int0 + 48))
                ._m31(byteBuffer.getFloat(int0 + 52))
                ._m32(byteBuffer.getFloat(int0 + 56))
                ._m33(byteBuffer.getFloat(int0 + 60));
        }

        @Override
        public void getf(Matrix4x3d matrix4x3d, int int0, FloatBuffer floatBuffer) {
            matrix4x3d._m00(floatBuffer.get(int0))
                ._m01(floatBuffer.get(int0 + 1))
                ._m02(floatBuffer.get(int0 + 2))
                ._m10(floatBuffer.get(int0 + 3))
                ._m11(floatBuffer.get(int0 + 4))
                ._m12(floatBuffer.get(int0 + 5))
                ._m20(floatBuffer.get(int0 + 6))
                ._m21(floatBuffer.get(int0 + 7))
                ._m22(floatBuffer.get(int0 + 8))
                ._m30(floatBuffer.get(int0 + 9))
                ._m31(floatBuffer.get(int0 + 10))
                ._m32(floatBuffer.get(int0 + 11));
        }

        @Override
        public void getf(Matrix4x3d matrix4x3d, int int0, ByteBuffer byteBuffer) {
            matrix4x3d._m00(byteBuffer.getFloat(int0))
                ._m01(byteBuffer.getFloat(int0 + 4))
                ._m02(byteBuffer.getFloat(int0 + 8))
                ._m10(byteBuffer.getFloat(int0 + 12))
                ._m11(byteBuffer.getFloat(int0 + 16))
                ._m12(byteBuffer.getFloat(int0 + 20))
                ._m20(byteBuffer.getFloat(int0 + 24))
                ._m21(byteBuffer.getFloat(int0 + 28))
                ._m22(byteBuffer.getFloat(int0 + 32))
                ._m30(byteBuffer.getFloat(int0 + 36))
                ._m31(byteBuffer.getFloat(int0 + 40))
                ._m32(byteBuffer.getFloat(int0 + 44));
        }

        @Override
        public void get(Matrix3f matrix3f, int int0, FloatBuffer floatBuffer) {
            matrix3f._m00(floatBuffer.get(int0))
                ._m01(floatBuffer.get(int0 + 1))
                ._m02(floatBuffer.get(int0 + 2))
                ._m10(floatBuffer.get(int0 + 3))
                ._m11(floatBuffer.get(int0 + 4))
                ._m12(floatBuffer.get(int0 + 5))
                ._m20(floatBuffer.get(int0 + 6))
                ._m21(floatBuffer.get(int0 + 7))
                ._m22(floatBuffer.get(int0 + 8));
        }

        @Override
        public void get(Matrix3f matrix3f, int int0, ByteBuffer byteBuffer) {
            matrix3f._m00(byteBuffer.getFloat(int0))
                ._m01(byteBuffer.getFloat(int0 + 4))
                ._m02(byteBuffer.getFloat(int0 + 8))
                ._m10(byteBuffer.getFloat(int0 + 12))
                ._m11(byteBuffer.getFloat(int0 + 16))
                ._m12(byteBuffer.getFloat(int0 + 20))
                ._m20(byteBuffer.getFloat(int0 + 24))
                ._m21(byteBuffer.getFloat(int0 + 28))
                ._m22(byteBuffer.getFloat(int0 + 32));
        }

        @Override
        public void get(Matrix3d matrix3d, int int0, DoubleBuffer doubleBuffer) {
            matrix3d._m00(doubleBuffer.get(int0))
                ._m01(doubleBuffer.get(int0 + 1))
                ._m02(doubleBuffer.get(int0 + 2))
                ._m10(doubleBuffer.get(int0 + 3))
                ._m11(doubleBuffer.get(int0 + 4))
                ._m12(doubleBuffer.get(int0 + 5))
                ._m20(doubleBuffer.get(int0 + 6))
                ._m21(doubleBuffer.get(int0 + 7))
                ._m22(doubleBuffer.get(int0 + 8));
        }

        @Override
        public void get(Matrix3d matrix3d, int int0, ByteBuffer byteBuffer) {
            matrix3d._m00(byteBuffer.getDouble(int0))
                ._m01(byteBuffer.getDouble(int0 + 8))
                ._m02(byteBuffer.getDouble(int0 + 16))
                ._m10(byteBuffer.getDouble(int0 + 24))
                ._m11(byteBuffer.getDouble(int0 + 32))
                ._m12(byteBuffer.getDouble(int0 + 40))
                ._m20(byteBuffer.getDouble(int0 + 48))
                ._m21(byteBuffer.getDouble(int0 + 56))
                ._m22(byteBuffer.getDouble(int0 + 64));
        }

        @Override
        public void get(Matrix3x2f matrix3x2f, int int0, FloatBuffer floatBuffer) {
            matrix3x2f._m00(floatBuffer.get(int0))
                ._m01(floatBuffer.get(int0 + 1))
                ._m10(floatBuffer.get(int0 + 2))
                ._m11(floatBuffer.get(int0 + 3))
                ._m20(floatBuffer.get(int0 + 4))
                ._m21(floatBuffer.get(int0 + 5));
        }

        @Override
        public void get(Matrix3x2f matrix3x2f, int int0, ByteBuffer byteBuffer) {
            matrix3x2f._m00(byteBuffer.getFloat(int0))
                ._m01(byteBuffer.getFloat(int0 + 4))
                ._m10(byteBuffer.getFloat(int0 + 8))
                ._m11(byteBuffer.getFloat(int0 + 12))
                ._m20(byteBuffer.getFloat(int0 + 16))
                ._m21(byteBuffer.getFloat(int0 + 20));
        }

        @Override
        public void get(Matrix3x2d matrix3x2d, int int0, DoubleBuffer doubleBuffer) {
            matrix3x2d._m00(doubleBuffer.get(int0))
                ._m01(doubleBuffer.get(int0 + 1))
                ._m10(doubleBuffer.get(int0 + 2))
                ._m11(doubleBuffer.get(int0 + 3))
                ._m20(doubleBuffer.get(int0 + 4))
                ._m21(doubleBuffer.get(int0 + 5));
        }

        @Override
        public void get(Matrix3x2d matrix3x2d, int int0, ByteBuffer byteBuffer) {
            matrix3x2d._m00(byteBuffer.getDouble(int0))
                ._m01(byteBuffer.getDouble(int0 + 8))
                ._m10(byteBuffer.getDouble(int0 + 16))
                ._m11(byteBuffer.getDouble(int0 + 24))
                ._m20(byteBuffer.getDouble(int0 + 32))
                ._m21(byteBuffer.getDouble(int0 + 40));
        }

        @Override
        public void getf(Matrix3d matrix3d, int int0, FloatBuffer floatBuffer) {
            matrix3d._m00(floatBuffer.get(int0))
                ._m01(floatBuffer.get(int0 + 1))
                ._m02(floatBuffer.get(int0 + 2))
                ._m10(floatBuffer.get(int0 + 3))
                ._m11(floatBuffer.get(int0 + 4))
                ._m12(floatBuffer.get(int0 + 5))
                ._m20(floatBuffer.get(int0 + 6))
                ._m21(floatBuffer.get(int0 + 7))
                ._m22(floatBuffer.get(int0 + 8));
        }

        @Override
        public void getf(Matrix3d matrix3d, int int0, ByteBuffer byteBuffer) {
            matrix3d._m00(byteBuffer.getFloat(int0))
                ._m01(byteBuffer.getFloat(int0 + 4))
                ._m02(byteBuffer.getFloat(int0 + 8))
                ._m10(byteBuffer.getFloat(int0 + 12))
                ._m11(byteBuffer.getFloat(int0 + 16))
                ._m12(byteBuffer.getFloat(int0 + 20))
                ._m20(byteBuffer.getFloat(int0 + 24))
                ._m21(byteBuffer.getFloat(int0 + 28))
                ._m22(byteBuffer.getFloat(int0 + 32));
        }

        @Override
        public void get(Matrix2f matrix2f, int int0, FloatBuffer floatBuffer) {
            matrix2f._m00(floatBuffer.get(int0))._m01(floatBuffer.get(int0 + 1))._m10(floatBuffer.get(int0 + 2))._m11(floatBuffer.get(int0 + 3));
        }

        @Override
        public void get(Matrix2f matrix2f, int int0, ByteBuffer byteBuffer) {
            matrix2f._m00(byteBuffer.getFloat(int0))
                ._m01(byteBuffer.getFloat(int0 + 4))
                ._m10(byteBuffer.getFloat(int0 + 8))
                ._m11(byteBuffer.getFloat(int0 + 12));
        }

        @Override
        public void get(Matrix2d matrix2d, int int0, DoubleBuffer doubleBuffer) {
            matrix2d._m00(doubleBuffer.get(int0))._m01(doubleBuffer.get(int0 + 1))._m10(doubleBuffer.get(int0 + 2))._m11(doubleBuffer.get(int0 + 3));
        }

        @Override
        public void get(Matrix2d matrix2d, int int0, ByteBuffer byteBuffer) {
            matrix2d._m00(byteBuffer.getDouble(int0))
                ._m01(byteBuffer.getDouble(int0 + 8))
                ._m10(byteBuffer.getDouble(int0 + 16))
                ._m11(byteBuffer.getDouble(int0 + 24));
        }

        @Override
        public void getf(Matrix2d matrix2d, int int0, FloatBuffer floatBuffer) {
            matrix2d._m00(floatBuffer.get(int0))._m01(floatBuffer.get(int0 + 1))._m10(floatBuffer.get(int0 + 2))._m11(floatBuffer.get(int0 + 3));
        }

        @Override
        public void getf(Matrix2d matrix2d, int int0, ByteBuffer byteBuffer) {
            matrix2d._m00(byteBuffer.getFloat(int0))
                ._m01(byteBuffer.getFloat(int0 + 4))
                ._m10(byteBuffer.getFloat(int0 + 8))
                ._m11(byteBuffer.getFloat(int0 + 12));
        }

        @Override
        public void get(Vector4d vector4d, int int0, DoubleBuffer doubleBuffer) {
            vector4d.x = doubleBuffer.get(int0);
            vector4d.y = doubleBuffer.get(int0 + 1);
            vector4d.z = doubleBuffer.get(int0 + 2);
            vector4d.w = doubleBuffer.get(int0 + 3);
        }

        @Override
        public void get(Vector4d vector4d, int int0, ByteBuffer byteBuffer) {
            vector4d.x = byteBuffer.getDouble(int0);
            vector4d.y = byteBuffer.getDouble(int0 + 8);
            vector4d.z = byteBuffer.getDouble(int0 + 16);
            vector4d.w = byteBuffer.getDouble(int0 + 24);
        }

        @Override
        public void get(Vector4f vector4f, int int0, FloatBuffer floatBuffer) {
            vector4f.x = floatBuffer.get(int0);
            vector4f.y = floatBuffer.get(int0 + 1);
            vector4f.z = floatBuffer.get(int0 + 2);
            vector4f.w = floatBuffer.get(int0 + 3);
        }

        @Override
        public void get(Vector4f vector4f, int int0, ByteBuffer byteBuffer) {
            vector4f.x = byteBuffer.getFloat(int0);
            vector4f.y = byteBuffer.getFloat(int0 + 4);
            vector4f.z = byteBuffer.getFloat(int0 + 8);
            vector4f.w = byteBuffer.getFloat(int0 + 12);
        }

        @Override
        public void get(Vector4i vector4i, int int0, IntBuffer intBuffer) {
            vector4i.x = intBuffer.get(int0);
            vector4i.y = intBuffer.get(int0 + 1);
            vector4i.z = intBuffer.get(int0 + 2);
            vector4i.w = intBuffer.get(int0 + 3);
        }

        @Override
        public void get(Vector4i vector4i, int int0, ByteBuffer byteBuffer) {
            vector4i.x = byteBuffer.getInt(int0);
            vector4i.y = byteBuffer.getInt(int0 + 4);
            vector4i.z = byteBuffer.getInt(int0 + 8);
            vector4i.w = byteBuffer.getInt(int0 + 12);
        }

        @Override
        public void get(Vector3f vector3f, int int0, FloatBuffer floatBuffer) {
            vector3f.x = floatBuffer.get(int0);
            vector3f.y = floatBuffer.get(int0 + 1);
            vector3f.z = floatBuffer.get(int0 + 2);
        }

        @Override
        public void get(Vector3f vector3f, int int0, ByteBuffer byteBuffer) {
            vector3f.x = byteBuffer.getFloat(int0);
            vector3f.y = byteBuffer.getFloat(int0 + 4);
            vector3f.z = byteBuffer.getFloat(int0 + 8);
        }

        @Override
        public void get(Vector3d vector3d, int int0, DoubleBuffer doubleBuffer) {
            vector3d.x = doubleBuffer.get(int0);
            vector3d.y = doubleBuffer.get(int0 + 1);
            vector3d.z = doubleBuffer.get(int0 + 2);
        }

        @Override
        public void get(Vector3d vector3d, int int0, ByteBuffer byteBuffer) {
            vector3d.x = byteBuffer.getDouble(int0);
            vector3d.y = byteBuffer.getDouble(int0 + 8);
            vector3d.z = byteBuffer.getDouble(int0 + 16);
        }

        @Override
        public void get(Vector3i vector3i, int int0, IntBuffer intBuffer) {
            vector3i.x = intBuffer.get(int0);
            vector3i.y = intBuffer.get(int0 + 1);
            vector3i.z = intBuffer.get(int0 + 2);
        }

        @Override
        public void get(Vector3i vector3i, int int0, ByteBuffer byteBuffer) {
            vector3i.x = byteBuffer.getInt(int0);
            vector3i.y = byteBuffer.getInt(int0 + 4);
            vector3i.z = byteBuffer.getInt(int0 + 8);
        }

        @Override
        public void get(Vector2f vector2f, int int0, FloatBuffer floatBuffer) {
            vector2f.x = floatBuffer.get(int0);
            vector2f.y = floatBuffer.get(int0 + 1);
        }

        @Override
        public void get(Vector2f vector2f, int int0, ByteBuffer byteBuffer) {
            vector2f.x = byteBuffer.getFloat(int0);
            vector2f.y = byteBuffer.getFloat(int0 + 4);
        }

        @Override
        public void get(Vector2d vector2d, int int0, DoubleBuffer doubleBuffer) {
            vector2d.x = doubleBuffer.get(int0);
            vector2d.y = doubleBuffer.get(int0 + 1);
        }

        @Override
        public void get(Vector2d vector2d, int int0, ByteBuffer byteBuffer) {
            vector2d.x = byteBuffer.getDouble(int0);
            vector2d.y = byteBuffer.getDouble(int0 + 8);
        }

        @Override
        public void get(Vector2i vector2i, int int0, IntBuffer intBuffer) {
            vector2i.x = intBuffer.get(int0);
            vector2i.y = intBuffer.get(int0 + 1);
        }

        @Override
        public void get(Vector2i vector2i, int int0, ByteBuffer byteBuffer) {
            vector2i.x = byteBuffer.getInt(int0);
            vector2i.y = byteBuffer.getInt(int0 + 4);
        }

        @Override
        public float get(Matrix4f matrix4f, int int0, int int1) {
            switch (int0) {
                case 0:
                    switch (int1) {
                        case 0:
                            return matrix4f.m00;
                        case 1:
                            return matrix4f.m01;
                        case 2:
                            return matrix4f.m02;
                        case 3:
                            return matrix4f.m03;
                        default:
                            throw new IllegalArgumentException();
                    }
                case 1:
                    switch (int1) {
                        case 0:
                            return matrix4f.m10;
                        case 1:
                            return matrix4f.m11;
                        case 2:
                            return matrix4f.m12;
                        case 3:
                            return matrix4f.m13;
                        default:
                            throw new IllegalArgumentException();
                    }
                case 2:
                    switch (int1) {
                        case 0:
                            return matrix4f.m20;
                        case 1:
                            return matrix4f.m21;
                        case 2:
                            return matrix4f.m22;
                        case 3:
                            return matrix4f.m23;
                        default:
                            throw new IllegalArgumentException();
                    }
                case 3:
                    switch (int1) {
                        case 0:
                            return matrix4f.m30;
                        case 1:
                            return matrix4f.m31;
                        case 2:
                            return matrix4f.m32;
                        case 3:
                            return matrix4f.m33;
                    }
            }

            throw new IllegalArgumentException();
        }

        @Override
        public Matrix4f set(Matrix4f matrix4f, int int0, int int1, float float0) {
            switch (int0) {
                case 0:
                    switch (int1) {
                        case 0:
                            return matrix4f.m00(float0);
                        case 1:
                            return matrix4f.m01(float0);
                        case 2:
                            return matrix4f.m02(float0);
                        case 3:
                            return matrix4f.m03(float0);
                        default:
                            throw new IllegalArgumentException();
                    }
                case 1:
                    switch (int1) {
                        case 0:
                            return matrix4f.m10(float0);
                        case 1:
                            return matrix4f.m11(float0);
                        case 2:
                            return matrix4f.m12(float0);
                        case 3:
                            return matrix4f.m13(float0);
                        default:
                            throw new IllegalArgumentException();
                    }
                case 2:
                    switch (int1) {
                        case 0:
                            return matrix4f.m20(float0);
                        case 1:
                            return matrix4f.m21(float0);
                        case 2:
                            return matrix4f.m22(float0);
                        case 3:
                            return matrix4f.m23(float0);
                        default:
                            throw new IllegalArgumentException();
                    }
                case 3:
                    switch (int1) {
                        case 0:
                            return matrix4f.m30(float0);
                        case 1:
                            return matrix4f.m31(float0);
                        case 2:
                            return matrix4f.m32(float0);
                        case 3:
                            return matrix4f.m33(float0);
                    }
            }

            throw new IllegalArgumentException();
        }

        @Override
        public double get(Matrix4d matrix4d, int int0, int int1) {
            switch (int0) {
                case 0:
                    switch (int1) {
                        case 0:
                            return matrix4d.m00;
                        case 1:
                            return matrix4d.m01;
                        case 2:
                            return matrix4d.m02;
                        case 3:
                            return matrix4d.m03;
                        default:
                            throw new IllegalArgumentException();
                    }
                case 1:
                    switch (int1) {
                        case 0:
                            return matrix4d.m10;
                        case 1:
                            return matrix4d.m11;
                        case 2:
                            return matrix4d.m12;
                        case 3:
                            return matrix4d.m13;
                        default:
                            throw new IllegalArgumentException();
                    }
                case 2:
                    switch (int1) {
                        case 0:
                            return matrix4d.m20;
                        case 1:
                            return matrix4d.m21;
                        case 2:
                            return matrix4d.m22;
                        case 3:
                            return matrix4d.m23;
                        default:
                            throw new IllegalArgumentException();
                    }
                case 3:
                    switch (int1) {
                        case 0:
                            return matrix4d.m30;
                        case 1:
                            return matrix4d.m31;
                        case 2:
                            return matrix4d.m32;
                        case 3:
                            return matrix4d.m33;
                    }
            }

            throw new IllegalArgumentException();
        }

        @Override
        public Matrix4d set(Matrix4d matrix4d, int int0, int int1, double double0) {
            switch (int0) {
                case 0:
                    switch (int1) {
                        case 0:
                            return matrix4d.m00(double0);
                        case 1:
                            return matrix4d.m01(double0);
                        case 2:
                            return matrix4d.m02(double0);
                        case 3:
                            return matrix4d.m03(double0);
                        default:
                            throw new IllegalArgumentException();
                    }
                case 1:
                    switch (int1) {
                        case 0:
                            return matrix4d.m10(double0);
                        case 1:
                            return matrix4d.m11(double0);
                        case 2:
                            return matrix4d.m12(double0);
                        case 3:
                            return matrix4d.m13(double0);
                        default:
                            throw new IllegalArgumentException();
                    }
                case 2:
                    switch (int1) {
                        case 0:
                            return matrix4d.m20(double0);
                        case 1:
                            return matrix4d.m21(double0);
                        case 2:
                            return matrix4d.m22(double0);
                        case 3:
                            return matrix4d.m23(double0);
                        default:
                            throw new IllegalArgumentException();
                    }
                case 3:
                    switch (int1) {
                        case 0:
                            return matrix4d.m30(double0);
                        case 1:
                            return matrix4d.m31(double0);
                        case 2:
                            return matrix4d.m32(double0);
                        case 3:
                            return matrix4d.m33(double0);
                    }
            }

            throw new IllegalArgumentException();
        }

        @Override
        public float get(Matrix3f matrix3f, int int0, int int1) {
            switch (int0) {
                case 0:
                    switch (int1) {
                        case 0:
                            return matrix3f.m00;
                        case 1:
                            return matrix3f.m01;
                        case 2:
                            return matrix3f.m02;
                        default:
                            throw new IllegalArgumentException();
                    }
                case 1:
                    switch (int1) {
                        case 0:
                            return matrix3f.m10;
                        case 1:
                            return matrix3f.m11;
                        case 2:
                            return matrix3f.m12;
                        default:
                            throw new IllegalArgumentException();
                    }
                case 2:
                    switch (int1) {
                        case 0:
                            return matrix3f.m20;
                        case 1:
                            return matrix3f.m21;
                        case 2:
                            return matrix3f.m22;
                    }
            }

            throw new IllegalArgumentException();
        }

        @Override
        public Matrix3f set(Matrix3f matrix3f, int int0, int int1, float float0) {
            switch (int0) {
                case 0:
                    switch (int1) {
                        case 0:
                            return matrix3f.m00(float0);
                        case 1:
                            return matrix3f.m01(float0);
                        case 2:
                            return matrix3f.m02(float0);
                        default:
                            throw new IllegalArgumentException();
                    }
                case 1:
                    switch (int1) {
                        case 0:
                            return matrix3f.m10(float0);
                        case 1:
                            return matrix3f.m11(float0);
                        case 2:
                            return matrix3f.m12(float0);
                        default:
                            throw new IllegalArgumentException();
                    }
                case 2:
                    switch (int1) {
                        case 0:
                            return matrix3f.m20(float0);
                        case 1:
                            return matrix3f.m21(float0);
                        case 2:
                            return matrix3f.m22(float0);
                    }
            }

            throw new IllegalArgumentException();
        }

        @Override
        public double get(Matrix3d matrix3d, int int0, int int1) {
            switch (int0) {
                case 0:
                    switch (int1) {
                        case 0:
                            return matrix3d.m00;
                        case 1:
                            return matrix3d.m01;
                        case 2:
                            return matrix3d.m02;
                        default:
                            throw new IllegalArgumentException();
                    }
                case 1:
                    switch (int1) {
                        case 0:
                            return matrix3d.m10;
                        case 1:
                            return matrix3d.m11;
                        case 2:
                            return matrix3d.m12;
                        default:
                            throw new IllegalArgumentException();
                    }
                case 2:
                    switch (int1) {
                        case 0:
                            return matrix3d.m20;
                        case 1:
                            return matrix3d.m21;
                        case 2:
                            return matrix3d.m22;
                    }
            }

            throw new IllegalArgumentException();
        }

        @Override
        public Matrix3d set(Matrix3d matrix3d, int int0, int int1, double double0) {
            switch (int0) {
                case 0:
                    switch (int1) {
                        case 0:
                            return matrix3d.m00(double0);
                        case 1:
                            return matrix3d.m01(double0);
                        case 2:
                            return matrix3d.m02(double0);
                        default:
                            throw new IllegalArgumentException();
                    }
                case 1:
                    switch (int1) {
                        case 0:
                            return matrix3d.m10(double0);
                        case 1:
                            return matrix3d.m11(double0);
                        case 2:
                            return matrix3d.m12(double0);
                        default:
                            throw new IllegalArgumentException();
                    }
                case 2:
                    switch (int1) {
                        case 0:
                            return matrix3d.m20(double0);
                        case 1:
                            return matrix3d.m21(double0);
                        case 2:
                            return matrix3d.m22(double0);
                    }
            }

            throw new IllegalArgumentException();
        }

        @Override
        public Vector4f getColumn(Matrix4f matrix4f, int int0, Vector4f vector4f) {
            switch (int0) {
                case 0:
                    return vector4f.set(matrix4f.m00, matrix4f.m01, matrix4f.m02, matrix4f.m03);
                case 1:
                    return vector4f.set(matrix4f.m10, matrix4f.m11, matrix4f.m12, matrix4f.m13);
                case 2:
                    return vector4f.set(matrix4f.m20, matrix4f.m21, matrix4f.m22, matrix4f.m23);
                case 3:
                    return vector4f.set(matrix4f.m30, matrix4f.m31, matrix4f.m32, matrix4f.m33);
                default:
                    throw new IndexOutOfBoundsException();
            }
        }

        @Override
        public Matrix4f setColumn(Vector4f vector4f, int int0, Matrix4f matrix4f) {
            switch (int0) {
                case 0:
                    return matrix4f._m00(vector4f.x)._m01(vector4f.y)._m02(vector4f.z)._m03(vector4f.w);
                case 1:
                    return matrix4f._m10(vector4f.x)._m11(vector4f.y)._m12(vector4f.z)._m13(vector4f.w);
                case 2:
                    return matrix4f._m20(vector4f.x)._m21(vector4f.y)._m22(vector4f.z)._m23(vector4f.w);
                case 3:
                    return matrix4f._m30(vector4f.x)._m31(vector4f.y)._m32(vector4f.z)._m33(vector4f.w);
                default:
                    throw new IndexOutOfBoundsException();
            }
        }

        @Override
        public Matrix4f setColumn(Vector4fc vector4fc, int int0, Matrix4f matrix4f) {
            switch (int0) {
                case 0:
                    return matrix4f._m00(vector4fc.x())._m01(vector4fc.y())._m02(vector4fc.z())._m03(vector4fc.w());
                case 1:
                    return matrix4f._m10(vector4fc.x())._m11(vector4fc.y())._m12(vector4fc.z())._m13(vector4fc.w());
                case 2:
                    return matrix4f._m20(vector4fc.x())._m21(vector4fc.y())._m22(vector4fc.z())._m23(vector4fc.w());
                case 3:
                    return matrix4f._m30(vector4fc.x())._m31(vector4fc.y())._m32(vector4fc.z())._m33(vector4fc.w());
                default:
                    throw new IndexOutOfBoundsException();
            }
        }

        @Override
        public void copy(Matrix4f matrix4f0, Matrix4f matrix4f1) {
            matrix4f1._m00(matrix4f0.m00())
                ._m01(matrix4f0.m01())
                ._m02(matrix4f0.m02())
                ._m03(matrix4f0.m03())
                ._m10(matrix4f0.m10())
                ._m11(matrix4f0.m11())
                ._m12(matrix4f0.m12())
                ._m13(matrix4f0.m13())
                ._m20(matrix4f0.m20())
                ._m21(matrix4f0.m21())
                ._m22(matrix4f0.m22())
                ._m23(matrix4f0.m23())
                ._m30(matrix4f0.m30())
                ._m31(matrix4f0.m31())
                ._m32(matrix4f0.m32())
                ._m33(matrix4f0.m33());
        }

        @Override
        public void copy(Matrix3f matrix3f, Matrix4f matrix4f) {
            matrix4f._m00(matrix3f.m00())
                ._m01(matrix3f.m01())
                ._m02(matrix3f.m02())
                ._m03(0.0F)
                ._m10(matrix3f.m10())
                ._m11(matrix3f.m11())
                ._m12(matrix3f.m12())
                ._m13(0.0F)
                ._m20(matrix3f.m20())
                ._m21(matrix3f.m21())
                ._m22(matrix3f.m22())
                ._m23(0.0F)
                ._m30(0.0F)
                ._m31(0.0F)
                ._m32(0.0F)
                ._m33(1.0F);
        }

        @Override
        public void copy(Matrix4f matrix4f, Matrix3f matrix3f) {
            matrix3f._m00(matrix4f.m00())
                ._m01(matrix4f.m01())
                ._m02(matrix4f.m02())
                ._m10(matrix4f.m10())
                ._m11(matrix4f.m11())
                ._m12(matrix4f.m12())
                ._m20(matrix4f.m20())
                ._m21(matrix4f.m21())
                ._m22(matrix4f.m22());
        }

        @Override
        public void copy(Matrix3f matrix3f, Matrix4x3f matrix4x3f) {
            matrix4x3f._m00(matrix3f.m00())
                ._m01(matrix3f.m01())
                ._m02(matrix3f.m02())
                ._m10(matrix3f.m10())
                ._m11(matrix3f.m11())
                ._m12(matrix3f.m12())
                ._m20(matrix3f.m20())
                ._m21(matrix3f.m21())
                ._m22(matrix3f.m22())
                ._m30(0.0F)
                ._m31(0.0F)
                ._m32(0.0F);
        }

        @Override
        public void copy(Matrix3x2f matrix3x2f0, Matrix3x2f matrix3x2f1) {
            matrix3x2f1._m00(matrix3x2f0.m00())
                ._m01(matrix3x2f0.m01())
                ._m10(matrix3x2f0.m10())
                ._m11(matrix3x2f0.m11())
                ._m20(matrix3x2f0.m20())
                ._m21(matrix3x2f0.m21());
        }

        @Override
        public void copy(Matrix3x2d matrix3x2d0, Matrix3x2d matrix3x2d1) {
            matrix3x2d1._m00(matrix3x2d0.m00())
                ._m01(matrix3x2d0.m01())
                ._m10(matrix3x2d0.m10())
                ._m11(matrix3x2d0.m11())
                ._m20(matrix3x2d0.m20())
                ._m21(matrix3x2d0.m21());
        }

        @Override
        public void copy(Matrix2f matrix2f0, Matrix2f matrix2f1) {
            matrix2f1._m00(matrix2f0.m00())._m01(matrix2f0.m01())._m10(matrix2f0.m10())._m11(matrix2f0.m11());
        }

        @Override
        public void copy(Matrix2d matrix2d0, Matrix2d matrix2d1) {
            matrix2d1._m00(matrix2d0.m00())._m01(matrix2d0.m01())._m10(matrix2d0.m10())._m11(matrix2d0.m11());
        }

        @Override
        public void copy(Matrix2f matrix2f, Matrix3f matrix3f) {
            matrix3f._m00(matrix2f.m00())._m01(matrix2f.m01())._m02(0.0F)._m10(matrix2f.m10())._m11(matrix2f.m11())._m12(0.0F)._m20(0.0F)._m21(0.0F)._m22(1.0F);
        }

        @Override
        public void copy(Matrix3f matrix3f, Matrix2f matrix2f) {
            matrix2f._m00(matrix3f.m00())._m01(matrix3f.m01())._m10(matrix3f.m10())._m11(matrix3f.m11());
        }

        @Override
        public void copy(Matrix2f matrix2f, Matrix3x2f matrix3x2f) {
            matrix3x2f._m00(matrix2f.m00())._m01(matrix2f.m01())._m10(matrix2f.m10())._m11(matrix2f.m11())._m20(0.0F)._m21(0.0F);
        }

        @Override
        public void copy(Matrix3x2f matrix3x2f, Matrix2f matrix2f) {
            matrix2f._m00(matrix3x2f.m00())._m01(matrix3x2f.m01())._m10(matrix3x2f.m10())._m11(matrix3x2f.m11());
        }

        @Override
        public void copy(Matrix2d matrix2d, Matrix3d matrix3d) {
            matrix3d._m00(matrix2d.m00())._m01(matrix2d.m01())._m02(0.0)._m10(matrix2d.m10())._m11(matrix2d.m11())._m12(0.0)._m20(0.0)._m21(0.0)._m22(1.0);
        }

        @Override
        public void copy(Matrix3d matrix3d, Matrix2d matrix2d) {
            matrix2d._m00(matrix3d.m00())._m01(matrix3d.m01())._m10(matrix3d.m10())._m11(matrix3d.m11());
        }

        @Override
        public void copy(Matrix2d matrix2d, Matrix3x2d matrix3x2d) {
            matrix3x2d._m00(matrix2d.m00())._m01(matrix2d.m01())._m10(matrix2d.m10())._m11(matrix2d.m11())._m20(0.0)._m21(0.0);
        }

        @Override
        public void copy(Matrix3x2d matrix3x2d, Matrix2d matrix2d) {
            matrix2d._m00(matrix3x2d.m00())._m01(matrix3x2d.m01())._m10(matrix3x2d.m10())._m11(matrix3x2d.m11());
        }

        @Override
        public void copy3x3(Matrix4f matrix4f0, Matrix4f matrix4f1) {
            matrix4f1._m00(matrix4f0.m00())
                ._m01(matrix4f0.m01())
                ._m02(matrix4f0.m02())
                ._m10(matrix4f0.m10())
                ._m11(matrix4f0.m11())
                ._m12(matrix4f0.m12())
                ._m20(matrix4f0.m20())
                ._m21(matrix4f0.m21())
                ._m22(matrix4f0.m22());
        }

        @Override
        public void copy3x3(Matrix4x3f matrix4x3f0, Matrix4x3f matrix4x3f1) {
            matrix4x3f1._m00(matrix4x3f0.m00())
                ._m01(matrix4x3f0.m01())
                ._m02(matrix4x3f0.m02())
                ._m10(matrix4x3f0.m10())
                ._m11(matrix4x3f0.m11())
                ._m12(matrix4x3f0.m12())
                ._m20(matrix4x3f0.m20())
                ._m21(matrix4x3f0.m21())
                ._m22(matrix4x3f0.m22());
        }

        @Override
        public void copy3x3(Matrix3f matrix3f, Matrix4x3f matrix4x3f) {
            matrix4x3f._m00(matrix3f.m00())
                ._m01(matrix3f.m01())
                ._m02(matrix3f.m02())
                ._m10(matrix3f.m10())
                ._m11(matrix3f.m11())
                ._m12(matrix3f.m12())
                ._m20(matrix3f.m20())
                ._m21(matrix3f.m21())
                ._m22(matrix3f.m22());
        }

        @Override
        public void copy3x3(Matrix3f matrix3f, Matrix4f matrix4f) {
            matrix4f._m00(matrix3f.m00())
                ._m01(matrix3f.m01())
                ._m02(matrix3f.m02())
                ._m10(matrix3f.m10())
                ._m11(matrix3f.m11())
                ._m12(matrix3f.m12())
                ._m20(matrix3f.m20())
                ._m21(matrix3f.m21())
                ._m22(matrix3f.m22());
        }

        @Override
        public void copy4x3(Matrix4x3f matrix4x3f, Matrix4f matrix4f) {
            matrix4f._m00(matrix4x3f.m00())
                ._m01(matrix4x3f.m01())
                ._m02(matrix4x3f.m02())
                ._m10(matrix4x3f.m10())
                ._m11(matrix4x3f.m11())
                ._m12(matrix4x3f.m12())
                ._m20(matrix4x3f.m20())
                ._m21(matrix4x3f.m21())
                ._m22(matrix4x3f.m22())
                ._m30(matrix4x3f.m30())
                ._m31(matrix4x3f.m31())
                ._m32(matrix4x3f.m32());
        }

        @Override
        public void copy4x3(Matrix4f matrix4f0, Matrix4f matrix4f1) {
            matrix4f1._m00(matrix4f0.m00())
                ._m01(matrix4f0.m01())
                ._m02(matrix4f0.m02())
                ._m10(matrix4f0.m10())
                ._m11(matrix4f0.m11())
                ._m12(matrix4f0.m12())
                ._m20(matrix4f0.m20())
                ._m21(matrix4f0.m21())
                ._m22(matrix4f0.m22())
                ._m30(matrix4f0.m30())
                ._m31(matrix4f0.m31())
                ._m32(matrix4f0.m32());
        }

        @Override
        public void copy(Matrix4f matrix4f, Matrix4x3f matrix4x3f) {
            matrix4x3f._m00(matrix4f.m00())
                ._m01(matrix4f.m01())
                ._m02(matrix4f.m02())
                ._m10(matrix4f.m10())
                ._m11(matrix4f.m11())
                ._m12(matrix4f.m12())
                ._m20(matrix4f.m20())
                ._m21(matrix4f.m21())
                ._m22(matrix4f.m22())
                ._m30(matrix4f.m30())
                ._m31(matrix4f.m31())
                ._m32(matrix4f.m32());
        }

        @Override
        public void copy(Matrix4x3f matrix4x3f, Matrix4f matrix4f) {
            matrix4f._m00(matrix4x3f.m00())
                ._m01(matrix4x3f.m01())
                ._m02(matrix4x3f.m02())
                ._m03(0.0F)
                ._m10(matrix4x3f.m10())
                ._m11(matrix4x3f.m11())
                ._m12(matrix4x3f.m12())
                ._m13(0.0F)
                ._m20(matrix4x3f.m20())
                ._m21(matrix4x3f.m21())
                ._m22(matrix4x3f.m22())
                ._m23(0.0F)
                ._m30(matrix4x3f.m30())
                ._m31(matrix4x3f.m31())
                ._m32(matrix4x3f.m32())
                ._m33(1.0F);
        }

        @Override
        public void copy(Matrix4x3f matrix4x3f0, Matrix4x3f matrix4x3f1) {
            matrix4x3f1._m00(matrix4x3f0.m00())
                ._m01(matrix4x3f0.m01())
                ._m02(matrix4x3f0.m02())
                ._m10(matrix4x3f0.m10())
                ._m11(matrix4x3f0.m11())
                ._m12(matrix4x3f0.m12())
                ._m20(matrix4x3f0.m20())
                ._m21(matrix4x3f0.m21())
                ._m22(matrix4x3f0.m22())
                ._m30(matrix4x3f0.m30())
                ._m31(matrix4x3f0.m31())
                ._m32(matrix4x3f0.m32());
        }

        @Override
        public void copy(Matrix3f matrix3f0, Matrix3f matrix3f1) {
            matrix3f1._m00(matrix3f0.m00())
                ._m01(matrix3f0.m01())
                ._m02(matrix3f0.m02())
                ._m10(matrix3f0.m10())
                ._m11(matrix3f0.m11())
                ._m12(matrix3f0.m12())
                ._m20(matrix3f0.m20())
                ._m21(matrix3f0.m21())
                ._m22(matrix3f0.m22());
        }

        @Override
        public void copy(float[] floats, int int0, Matrix4f matrix4f) {
            matrix4f._m00(floats[int0 + 0])
                ._m01(floats[int0 + 1])
                ._m02(floats[int0 + 2])
                ._m03(floats[int0 + 3])
                ._m10(floats[int0 + 4])
                ._m11(floats[int0 + 5])
                ._m12(floats[int0 + 6])
                ._m13(floats[int0 + 7])
                ._m20(floats[int0 + 8])
                ._m21(floats[int0 + 9])
                ._m22(floats[int0 + 10])
                ._m23(floats[int0 + 11])
                ._m30(floats[int0 + 12])
                ._m31(floats[int0 + 13])
                ._m32(floats[int0 + 14])
                ._m33(floats[int0 + 15]);
        }

        @Override
        public void copyTransposed(float[] floats, int int0, Matrix4f matrix4f) {
            matrix4f._m00(floats[int0 + 0])
                ._m10(floats[int0 + 1])
                ._m20(floats[int0 + 2])
                ._m30(floats[int0 + 3])
                ._m01(floats[int0 + 4])
                ._m11(floats[int0 + 5])
                ._m21(floats[int0 + 6])
                ._m31(floats[int0 + 7])
                ._m02(floats[int0 + 8])
                ._m12(floats[int0 + 9])
                ._m22(floats[int0 + 10])
                ._m32(floats[int0 + 11])
                ._m03(floats[int0 + 12])
                ._m13(floats[int0 + 13])
                ._m23(floats[int0 + 14])
                ._m33(floats[int0 + 15]);
        }

        @Override
        public void copy(float[] floats, int int0, Matrix3f matrix3f) {
            matrix3f._m00(floats[int0 + 0])
                ._m01(floats[int0 + 1])
                ._m02(floats[int0 + 2])
                ._m10(floats[int0 + 3])
                ._m11(floats[int0 + 4])
                ._m12(floats[int0 + 5])
                ._m20(floats[int0 + 6])
                ._m21(floats[int0 + 7])
                ._m22(floats[int0 + 8]);
        }

        @Override
        public void copy(float[] floats, int int0, Matrix4x3f matrix4x3f) {
            matrix4x3f._m00(floats[int0 + 0])
                ._m01(floats[int0 + 1])
                ._m02(floats[int0 + 2])
                ._m10(floats[int0 + 3])
                ._m11(floats[int0 + 4])
                ._m12(floats[int0 + 5])
                ._m20(floats[int0 + 6])
                ._m21(floats[int0 + 7])
                ._m22(floats[int0 + 8])
                ._m30(floats[int0 + 9])
                ._m31(floats[int0 + 10])
                ._m32(floats[int0 + 11]);
        }

        @Override
        public void copy(float[] floats, int int0, Matrix3x2f matrix3x2f) {
            matrix3x2f._m00(floats[int0 + 0])
                ._m01(floats[int0 + 1])
                ._m10(floats[int0 + 2])
                ._m11(floats[int0 + 3])
                ._m20(floats[int0 + 4])
                ._m21(floats[int0 + 5]);
        }

        @Override
        public void copy(double[] doubles, int int0, Matrix3x2d matrix3x2d) {
            matrix3x2d._m00(doubles[int0 + 0])
                ._m01(doubles[int0 + 1])
                ._m10(doubles[int0 + 2])
                ._m11(doubles[int0 + 3])
                ._m20(doubles[int0 + 4])
                ._m21(doubles[int0 + 5]);
        }

        @Override
        public void copy(float[] floats, int int0, Matrix2f matrix2f) {
            matrix2f._m00(floats[int0 + 0])._m01(floats[int0 + 1])._m10(floats[int0 + 2])._m11(floats[int0 + 3]);
        }

        @Override
        public void copy(double[] doubles, int int0, Matrix2d matrix2d) {
            matrix2d._m00(doubles[int0 + 0])._m01(doubles[int0 + 1])._m10(doubles[int0 + 2])._m11(doubles[int0 + 3]);
        }

        @Override
        public void copy(Matrix4f matrix4f, float[] floats, int int0) {
            floats[int0 + 0] = matrix4f.m00();
            floats[int0 + 1] = matrix4f.m01();
            floats[int0 + 2] = matrix4f.m02();
            floats[int0 + 3] = matrix4f.m03();
            floats[int0 + 4] = matrix4f.m10();
            floats[int0 + 5] = matrix4f.m11();
            floats[int0 + 6] = matrix4f.m12();
            floats[int0 + 7] = matrix4f.m13();
            floats[int0 + 8] = matrix4f.m20();
            floats[int0 + 9] = matrix4f.m21();
            floats[int0 + 10] = matrix4f.m22();
            floats[int0 + 11] = matrix4f.m23();
            floats[int0 + 12] = matrix4f.m30();
            floats[int0 + 13] = matrix4f.m31();
            floats[int0 + 14] = matrix4f.m32();
            floats[int0 + 15] = matrix4f.m33();
        }

        @Override
        public void copy(Matrix3f matrix3f, float[] floats, int int0) {
            floats[int0 + 0] = matrix3f.m00();
            floats[int0 + 1] = matrix3f.m01();
            floats[int0 + 2] = matrix3f.m02();
            floats[int0 + 3] = matrix3f.m10();
            floats[int0 + 4] = matrix3f.m11();
            floats[int0 + 5] = matrix3f.m12();
            floats[int0 + 6] = matrix3f.m20();
            floats[int0 + 7] = matrix3f.m21();
            floats[int0 + 8] = matrix3f.m22();
        }

        @Override
        public void copy(Matrix4x3f matrix4x3f, float[] floats, int int0) {
            floats[int0 + 0] = matrix4x3f.m00();
            floats[int0 + 1] = matrix4x3f.m01();
            floats[int0 + 2] = matrix4x3f.m02();
            floats[int0 + 3] = matrix4x3f.m10();
            floats[int0 + 4] = matrix4x3f.m11();
            floats[int0 + 5] = matrix4x3f.m12();
            floats[int0 + 6] = matrix4x3f.m20();
            floats[int0 + 7] = matrix4x3f.m21();
            floats[int0 + 8] = matrix4x3f.m22();
            floats[int0 + 9] = matrix4x3f.m30();
            floats[int0 + 10] = matrix4x3f.m31();
            floats[int0 + 11] = matrix4x3f.m32();
        }

        @Override
        public void copy(Matrix3x2f matrix3x2f, float[] floats, int int0) {
            floats[int0 + 0] = matrix3x2f.m00();
            floats[int0 + 1] = matrix3x2f.m01();
            floats[int0 + 2] = matrix3x2f.m10();
            floats[int0 + 3] = matrix3x2f.m11();
            floats[int0 + 4] = matrix3x2f.m20();
            floats[int0 + 5] = matrix3x2f.m21();
        }

        @Override
        public void copy(Matrix3x2d matrix3x2d, double[] doubles, int int0) {
            doubles[int0 + 0] = matrix3x2d.m00();
            doubles[int0 + 1] = matrix3x2d.m01();
            doubles[int0 + 2] = matrix3x2d.m10();
            doubles[int0 + 3] = matrix3x2d.m11();
            doubles[int0 + 4] = matrix3x2d.m20();
            doubles[int0 + 5] = matrix3x2d.m21();
        }

        @Override
        public void copy(Matrix2f matrix2f, float[] floats, int int0) {
            floats[int0 + 0] = matrix2f.m00();
            floats[int0 + 1] = matrix2f.m01();
            floats[int0 + 2] = matrix2f.m10();
            floats[int0 + 3] = matrix2f.m11();
        }

        @Override
        public void copy(Matrix2d matrix2d, double[] doubles, int int0) {
            doubles[int0 + 0] = matrix2d.m00();
            doubles[int0 + 1] = matrix2d.m01();
            doubles[int0 + 2] = matrix2d.m10();
            doubles[int0 + 3] = matrix2d.m11();
        }

        @Override
        public void copy4x4(Matrix4x3f matrix4x3f, float[] floats, int int0) {
            floats[int0 + 0] = matrix4x3f.m00();
            floats[int0 + 1] = matrix4x3f.m01();
            floats[int0 + 2] = matrix4x3f.m02();
            floats[int0 + 3] = 0.0F;
            floats[int0 + 4] = matrix4x3f.m10();
            floats[int0 + 5] = matrix4x3f.m11();
            floats[int0 + 6] = matrix4x3f.m12();
            floats[int0 + 7] = 0.0F;
            floats[int0 + 8] = matrix4x3f.m20();
            floats[int0 + 9] = matrix4x3f.m21();
            floats[int0 + 10] = matrix4x3f.m22();
            floats[int0 + 11] = 0.0F;
            floats[int0 + 12] = matrix4x3f.m30();
            floats[int0 + 13] = matrix4x3f.m31();
            floats[int0 + 14] = matrix4x3f.m32();
            floats[int0 + 15] = 1.0F;
        }

        @Override
        public void copy4x4(Matrix4x3d matrix4x3d, float[] floats, int int0) {
            floats[int0 + 0] = (float)matrix4x3d.m00();
            floats[int0 + 1] = (float)matrix4x3d.m01();
            floats[int0 + 2] = (float)matrix4x3d.m02();
            floats[int0 + 3] = 0.0F;
            floats[int0 + 4] = (float)matrix4x3d.m10();
            floats[int0 + 5] = (float)matrix4x3d.m11();
            floats[int0 + 6] = (float)matrix4x3d.m12();
            floats[int0 + 7] = 0.0F;
            floats[int0 + 8] = (float)matrix4x3d.m20();
            floats[int0 + 9] = (float)matrix4x3d.m21();
            floats[int0 + 10] = (float)matrix4x3d.m22();
            floats[int0 + 11] = 0.0F;
            floats[int0 + 12] = (float)matrix4x3d.m30();
            floats[int0 + 13] = (float)matrix4x3d.m31();
            floats[int0 + 14] = (float)matrix4x3d.m32();
            floats[int0 + 15] = 1.0F;
        }

        @Override
        public void copy4x4(Matrix4x3d matrix4x3d, double[] doubles, int int0) {
            doubles[int0 + 0] = matrix4x3d.m00();
            doubles[int0 + 1] = matrix4x3d.m01();
            doubles[int0 + 2] = matrix4x3d.m02();
            doubles[int0 + 3] = 0.0;
            doubles[int0 + 4] = matrix4x3d.m10();
            doubles[int0 + 5] = matrix4x3d.m11();
            doubles[int0 + 6] = matrix4x3d.m12();
            doubles[int0 + 7] = 0.0;
            doubles[int0 + 8] = matrix4x3d.m20();
            doubles[int0 + 9] = matrix4x3d.m21();
            doubles[int0 + 10] = matrix4x3d.m22();
            doubles[int0 + 11] = 0.0;
            doubles[int0 + 12] = matrix4x3d.m30();
            doubles[int0 + 13] = matrix4x3d.m31();
            doubles[int0 + 14] = matrix4x3d.m32();
            doubles[int0 + 15] = 1.0;
        }

        @Override
        public void copy3x3(Matrix3x2f matrix3x2f, float[] floats, int int0) {
            floats[int0 + 0] = matrix3x2f.m00();
            floats[int0 + 1] = matrix3x2f.m01();
            floats[int0 + 2] = 0.0F;
            floats[int0 + 3] = matrix3x2f.m10();
            floats[int0 + 4] = matrix3x2f.m11();
            floats[int0 + 5] = 0.0F;
            floats[int0 + 6] = matrix3x2f.m20();
            floats[int0 + 7] = matrix3x2f.m21();
            floats[int0 + 8] = 1.0F;
        }

        @Override
        public void copy3x3(Matrix3x2d matrix3x2d, double[] doubles, int int0) {
            doubles[int0 + 0] = matrix3x2d.m00();
            doubles[int0 + 1] = matrix3x2d.m01();
            doubles[int0 + 2] = 0.0;
            doubles[int0 + 3] = matrix3x2d.m10();
            doubles[int0 + 4] = matrix3x2d.m11();
            doubles[int0 + 5] = 0.0;
            doubles[int0 + 6] = matrix3x2d.m20();
            doubles[int0 + 7] = matrix3x2d.m21();
            doubles[int0 + 8] = 1.0;
        }

        @Override
        public void copy4x4(Matrix3x2f matrix3x2f, float[] floats, int int0) {
            floats[int0 + 0] = matrix3x2f.m00();
            floats[int0 + 1] = matrix3x2f.m01();
            floats[int0 + 2] = 0.0F;
            floats[int0 + 3] = 0.0F;
            floats[int0 + 4] = matrix3x2f.m10();
            floats[int0 + 5] = matrix3x2f.m11();
            floats[int0 + 6] = 0.0F;
            floats[int0 + 7] = 0.0F;
            floats[int0 + 8] = 0.0F;
            floats[int0 + 9] = 0.0F;
            floats[int0 + 10] = 1.0F;
            floats[int0 + 11] = 0.0F;
            floats[int0 + 12] = matrix3x2f.m20();
            floats[int0 + 13] = matrix3x2f.m21();
            floats[int0 + 14] = 0.0F;
            floats[int0 + 15] = 1.0F;
        }

        @Override
        public void copy4x4(Matrix3x2d matrix3x2d, double[] doubles, int int0) {
            doubles[int0 + 0] = matrix3x2d.m00();
            doubles[int0 + 1] = matrix3x2d.m01();
            doubles[int0 + 2] = 0.0;
            doubles[int0 + 3] = 0.0;
            doubles[int0 + 4] = matrix3x2d.m10();
            doubles[int0 + 5] = matrix3x2d.m11();
            doubles[int0 + 6] = 0.0;
            doubles[int0 + 7] = 0.0;
            doubles[int0 + 8] = 0.0;
            doubles[int0 + 9] = 0.0;
            doubles[int0 + 10] = 1.0;
            doubles[int0 + 11] = 0.0;
            doubles[int0 + 12] = matrix3x2d.m20();
            doubles[int0 + 13] = matrix3x2d.m21();
            doubles[int0 + 14] = 0.0;
            doubles[int0 + 15] = 1.0;
        }

        @Override
        public void identity(Matrix4f matrix4f) {
            matrix4f._m00(1.0F)
                ._m01(0.0F)
                ._m02(0.0F)
                ._m03(0.0F)
                ._m10(0.0F)
                ._m11(1.0F)
                ._m12(0.0F)
                ._m13(0.0F)
                ._m20(0.0F)
                ._m21(0.0F)
                ._m22(1.0F)
                ._m23(0.0F)
                ._m30(0.0F)
                ._m31(0.0F)
                ._m32(0.0F)
                ._m33(1.0F);
        }

        @Override
        public void identity(Matrix4x3f matrix4x3f) {
            matrix4x3f._m00(1.0F)._m01(0.0F)._m02(0.0F)._m10(0.0F)._m11(1.0F)._m12(0.0F)._m20(0.0F)._m21(0.0F)._m22(1.0F)._m30(0.0F)._m31(0.0F)._m32(0.0F);
        }

        @Override
        public void identity(Matrix3f matrix3f) {
            matrix3f._m00(1.0F)._m01(0.0F)._m02(0.0F)._m10(0.0F)._m11(1.0F)._m12(0.0F)._m20(0.0F)._m21(0.0F)._m22(1.0F);
        }

        @Override
        public void identity(Matrix3x2f matrix3x2f) {
            matrix3x2f._m00(1.0F)._m01(0.0F)._m10(0.0F)._m11(1.0F)._m20(0.0F)._m21(0.0F);
        }

        @Override
        public void identity(Matrix3x2d matrix3x2d) {
            matrix3x2d._m00(1.0)._m01(0.0)._m10(0.0)._m11(1.0)._m20(0.0)._m21(0.0);
        }

        @Override
        public void identity(Matrix2f matrix2f) {
            matrix2f._m00(1.0F)._m01(0.0F)._m10(0.0F)._m11(1.0F);
        }

        @Override
        public void swap(Matrix4f matrix4f0, Matrix4f matrix4f1) {
            float float0 = matrix4f0.m00();
            matrix4f0._m00(matrix4f1.m00());
            matrix4f1._m00(float0);
            float0 = matrix4f0.m01();
            matrix4f0._m01(matrix4f1.m01());
            matrix4f1._m01(float0);
            float0 = matrix4f0.m02();
            matrix4f0._m02(matrix4f1.m02());
            matrix4f1._m02(float0);
            float0 = matrix4f0.m03();
            matrix4f0._m03(matrix4f1.m03());
            matrix4f1._m03(float0);
            float0 = matrix4f0.m10();
            matrix4f0._m10(matrix4f1.m10());
            matrix4f1._m10(float0);
            float0 = matrix4f0.m11();
            matrix4f0._m11(matrix4f1.m11());
            matrix4f1._m11(float0);
            float0 = matrix4f0.m12();
            matrix4f0._m12(matrix4f1.m12());
            matrix4f1._m12(float0);
            float0 = matrix4f0.m13();
            matrix4f0._m13(matrix4f1.m13());
            matrix4f1._m13(float0);
            float0 = matrix4f0.m20();
            matrix4f0._m20(matrix4f1.m20());
            matrix4f1._m20(float0);
            float0 = matrix4f0.m21();
            matrix4f0._m21(matrix4f1.m21());
            matrix4f1._m21(float0);
            float0 = matrix4f0.m22();
            matrix4f0._m22(matrix4f1.m22());
            matrix4f1._m22(float0);
            float0 = matrix4f0.m23();
            matrix4f0._m23(matrix4f1.m23());
            matrix4f1._m23(float0);
            float0 = matrix4f0.m30();
            matrix4f0._m30(matrix4f1.m30());
            matrix4f1._m30(float0);
            float0 = matrix4f0.m31();
            matrix4f0._m31(matrix4f1.m31());
            matrix4f1._m31(float0);
            float0 = matrix4f0.m32();
            matrix4f0._m32(matrix4f1.m32());
            matrix4f1._m32(float0);
            float0 = matrix4f0.m33();
            matrix4f0._m33(matrix4f1.m33());
            matrix4f1._m33(float0);
        }

        @Override
        public void swap(Matrix4x3f matrix4x3f0, Matrix4x3f matrix4x3f1) {
            float float0 = matrix4x3f0.m00();
            matrix4x3f0._m00(matrix4x3f1.m00());
            matrix4x3f1._m00(float0);
            float0 = matrix4x3f0.m01();
            matrix4x3f0._m01(matrix4x3f1.m01());
            matrix4x3f1._m01(float0);
            float0 = matrix4x3f0.m02();
            matrix4x3f0._m02(matrix4x3f1.m02());
            matrix4x3f1._m02(float0);
            float0 = matrix4x3f0.m10();
            matrix4x3f0._m10(matrix4x3f1.m10());
            matrix4x3f1._m10(float0);
            float0 = matrix4x3f0.m11();
            matrix4x3f0._m11(matrix4x3f1.m11());
            matrix4x3f1._m11(float0);
            float0 = matrix4x3f0.m12();
            matrix4x3f0._m12(matrix4x3f1.m12());
            matrix4x3f1._m12(float0);
            float0 = matrix4x3f0.m20();
            matrix4x3f0._m20(matrix4x3f1.m20());
            matrix4x3f1._m20(float0);
            float0 = matrix4x3f0.m21();
            matrix4x3f0._m21(matrix4x3f1.m21());
            matrix4x3f1._m21(float0);
            float0 = matrix4x3f0.m22();
            matrix4x3f0._m22(matrix4x3f1.m22());
            matrix4x3f1._m22(float0);
            float0 = matrix4x3f0.m30();
            matrix4x3f0._m30(matrix4x3f1.m30());
            matrix4x3f1._m30(float0);
            float0 = matrix4x3f0.m31();
            matrix4x3f0._m31(matrix4x3f1.m31());
            matrix4x3f1._m31(float0);
            float0 = matrix4x3f0.m32();
            matrix4x3f0._m32(matrix4x3f1.m32());
            matrix4x3f1._m32(float0);
        }

        @Override
        public void swap(Matrix3f matrix3f0, Matrix3f matrix3f1) {
            float float0 = matrix3f0.m00();
            matrix3f0._m00(matrix3f1.m00());
            matrix3f1._m00(float0);
            float0 = matrix3f0.m01();
            matrix3f0._m01(matrix3f1.m01());
            matrix3f1._m01(float0);
            float0 = matrix3f0.m02();
            matrix3f0._m02(matrix3f1.m02());
            matrix3f1._m02(float0);
            float0 = matrix3f0.m10();
            matrix3f0._m10(matrix3f1.m10());
            matrix3f1._m10(float0);
            float0 = matrix3f0.m11();
            matrix3f0._m11(matrix3f1.m11());
            matrix3f1._m11(float0);
            float0 = matrix3f0.m12();
            matrix3f0._m12(matrix3f1.m12());
            matrix3f1._m12(float0);
            float0 = matrix3f0.m20();
            matrix3f0._m20(matrix3f1.m20());
            matrix3f1._m20(float0);
            float0 = matrix3f0.m21();
            matrix3f0._m21(matrix3f1.m21());
            matrix3f1._m21(float0);
            float0 = matrix3f0.m22();
            matrix3f0._m22(matrix3f1.m22());
            matrix3f1._m22(float0);
        }

        @Override
        public void swap(Matrix2f matrix2f0, Matrix2f matrix2f1) {
            float float0 = matrix2f0.m00();
            matrix2f0._m00(matrix2f1.m00());
            matrix2f1._m00(float0);
            float0 = matrix2f0.m01();
            matrix2f0._m00(matrix2f1.m01());
            matrix2f1._m01(float0);
            float0 = matrix2f0.m10();
            matrix2f0._m00(matrix2f1.m10());
            matrix2f1._m10(float0);
            float0 = matrix2f0.m11();
            matrix2f0._m00(matrix2f1.m11());
            matrix2f1._m11(float0);
        }

        @Override
        public void swap(Matrix2d matrix2d0, Matrix2d matrix2d1) {
            double double0 = matrix2d0.m00();
            matrix2d0._m00(matrix2d1.m00());
            matrix2d1._m00(double0);
            double0 = matrix2d0.m01();
            matrix2d0._m00(matrix2d1.m01());
            matrix2d1._m01(double0);
            double0 = matrix2d0.m10();
            matrix2d0._m00(matrix2d1.m10());
            matrix2d1._m10(double0);
            double0 = matrix2d0.m11();
            matrix2d0._m00(matrix2d1.m11());
            matrix2d1._m11(double0);
        }

        @Override
        public void zero(Matrix4f matrix4f) {
            matrix4f._m00(0.0F)
                ._m01(0.0F)
                ._m02(0.0F)
                ._m03(0.0F)
                ._m10(0.0F)
                ._m11(0.0F)
                ._m12(0.0F)
                ._m13(0.0F)
                ._m20(0.0F)
                ._m21(0.0F)
                ._m22(0.0F)
                ._m23(0.0F)
                ._m30(0.0F)
                ._m31(0.0F)
                ._m32(0.0F)
                ._m33(0.0F);
        }

        @Override
        public void zero(Matrix4x3f matrix4x3f) {
            matrix4x3f._m00(0.0F)._m01(0.0F)._m02(0.0F)._m10(0.0F)._m11(0.0F)._m12(0.0F)._m20(0.0F)._m21(0.0F)._m22(0.0F)._m30(0.0F)._m31(0.0F)._m32(0.0F);
        }

        @Override
        public void zero(Matrix3f matrix3f) {
            matrix3f._m00(0.0F)._m01(0.0F)._m02(0.0F)._m10(0.0F)._m11(0.0F)._m12(0.0F)._m20(0.0F)._m21(0.0F)._m22(0.0F);
        }

        @Override
        public void zero(Matrix3x2f matrix3x2f) {
            matrix3x2f._m00(0.0F)._m01(0.0F)._m10(0.0F)._m11(0.0F)._m20(0.0F)._m21(0.0F);
        }

        @Override
        public void zero(Matrix3x2d matrix3x2d) {
            matrix3x2d._m00(0.0)._m01(0.0)._m10(0.0)._m11(0.0)._m20(0.0)._m21(0.0);
        }

        @Override
        public void zero(Matrix2f matrix2f) {
            matrix2f._m00(0.0F)._m01(0.0F)._m10(0.0F)._m11(0.0F);
        }

        @Override
        public void zero(Matrix2d matrix2d) {
            matrix2d._m00(0.0)._m01(0.0)._m10(0.0)._m11(0.0);
        }

        @Override
        public void putMatrix3f(Quaternionf quaternionf, int int0, ByteBuffer byteBuffer) {
            float float0 = quaternionf.w * quaternionf.w;
            float float1 = quaternionf.x * quaternionf.x;
            float float2 = quaternionf.y * quaternionf.y;
            float float3 = quaternionf.z * quaternionf.z;
            float float4 = quaternionf.z * quaternionf.w;
            float float5 = quaternionf.x * quaternionf.y;
            float float6 = quaternionf.x * quaternionf.z;
            float float7 = quaternionf.y * quaternionf.w;
            float float8 = quaternionf.y * quaternionf.z;
            float float9 = quaternionf.x * quaternionf.w;
            byteBuffer.putFloat(int0, float0 + float1 - float3 - float2)
                .putFloat(int0 + 4, float5 + float4 + float4 + float5)
                .putFloat(int0 + 8, float6 - float7 + float6 - float7)
                .putFloat(int0 + 12, -float4 + float5 - float4 + float5)
                .putFloat(int0 + 16, float2 - float3 + float0 - float1)
                .putFloat(int0 + 20, float8 + float8 + float9 + float9)
                .putFloat(int0 + 24, float7 + float6 + float6 + float7)
                .putFloat(int0 + 28, float8 + float8 - float9 - float9)
                .putFloat(int0 + 32, float3 - float2 - float1 + float0);
        }

        @Override
        public void putMatrix3f(Quaternionf quaternionf, int int0, FloatBuffer floatBuffer) {
            float float0 = quaternionf.w * quaternionf.w;
            float float1 = quaternionf.x * quaternionf.x;
            float float2 = quaternionf.y * quaternionf.y;
            float float3 = quaternionf.z * quaternionf.z;
            float float4 = quaternionf.z * quaternionf.w;
            float float5 = quaternionf.x * quaternionf.y;
            float float6 = quaternionf.x * quaternionf.z;
            float float7 = quaternionf.y * quaternionf.w;
            float float8 = quaternionf.y * quaternionf.z;
            float float9 = quaternionf.x * quaternionf.w;
            floatBuffer.put(int0, float0 + float1 - float3 - float2)
                .put(int0 + 1, float5 + float4 + float4 + float5)
                .put(int0 + 2, float6 - float7 + float6 - float7)
                .put(int0 + 3, -float4 + float5 - float4 + float5)
                .put(int0 + 4, float2 - float3 + float0 - float1)
                .put(int0 + 5, float8 + float8 + float9 + float9)
                .put(int0 + 6, float7 + float6 + float6 + float7)
                .put(int0 + 7, float8 + float8 - float9 - float9)
                .put(int0 + 8, float3 - float2 - float1 + float0);
        }

        @Override
        public void putMatrix4f(Quaternionf quaternionf, int int0, ByteBuffer byteBuffer) {
            float float0 = quaternionf.w * quaternionf.w;
            float float1 = quaternionf.x * quaternionf.x;
            float float2 = quaternionf.y * quaternionf.y;
            float float3 = quaternionf.z * quaternionf.z;
            float float4 = quaternionf.z * quaternionf.w;
            float float5 = quaternionf.x * quaternionf.y;
            float float6 = quaternionf.x * quaternionf.z;
            float float7 = quaternionf.y * quaternionf.w;
            float float8 = quaternionf.y * quaternionf.z;
            float float9 = quaternionf.x * quaternionf.w;
            byteBuffer.putFloat(int0, float0 + float1 - float3 - float2)
                .putFloat(int0 + 4, float5 + float4 + float4 + float5)
                .putFloat(int0 + 8, float6 - float7 + float6 - float7)
                .putFloat(int0 + 12, 0.0F)
                .putFloat(int0 + 16, -float4 + float5 - float4 + float5)
                .putFloat(int0 + 20, float2 - float3 + float0 - float1)
                .putFloat(int0 + 24, float8 + float8 + float9 + float9)
                .putFloat(int0 + 28, 0.0F)
                .putFloat(int0 + 32, float7 + float6 + float6 + float7)
                .putFloat(int0 + 36, float8 + float8 - float9 - float9)
                .putFloat(int0 + 40, float3 - float2 - float1 + float0)
                .putFloat(int0 + 44, 0.0F)
                .putLong(int0 + 48, 0L)
                .putLong(int0 + 56, 4575657221408423936L);
        }

        @Override
        public void putMatrix4f(Quaternionf quaternionf, int int0, FloatBuffer floatBuffer) {
            float float0 = quaternionf.w * quaternionf.w;
            float float1 = quaternionf.x * quaternionf.x;
            float float2 = quaternionf.y * quaternionf.y;
            float float3 = quaternionf.z * quaternionf.z;
            float float4 = quaternionf.z * quaternionf.w;
            float float5 = quaternionf.x * quaternionf.y;
            float float6 = quaternionf.x * quaternionf.z;
            float float7 = quaternionf.y * quaternionf.w;
            float float8 = quaternionf.y * quaternionf.z;
            float float9 = quaternionf.x * quaternionf.w;
            floatBuffer.put(int0, float0 + float1 - float3 - float2)
                .put(int0 + 1, float5 + float4 + float4 + float5)
                .put(int0 + 2, float6 - float7 + float6 - float7)
                .put(int0 + 3, 0.0F)
                .put(int0 + 4, -float4 + float5 - float4 + float5)
                .put(int0 + 5, float2 - float3 + float0 - float1)
                .put(int0 + 6, float8 + float8 + float9 + float9)
                .put(int0 + 7, 0.0F)
                .put(int0 + 8, float7 + float6 + float6 + float7)
                .put(int0 + 9, float8 + float8 - float9 - float9)
                .put(int0 + 10, float3 - float2 - float1 + float0)
                .put(int0 + 11, 0.0F)
                .put(int0 + 12, 0.0F)
                .put(int0 + 13, 0.0F)
                .put(int0 + 14, 0.0F)
                .put(int0 + 15, 1.0F);
        }

        @Override
        public void putMatrix4x3f(Quaternionf quaternionf, int int0, ByteBuffer byteBuffer) {
            float float0 = quaternionf.w * quaternionf.w;
            float float1 = quaternionf.x * quaternionf.x;
            float float2 = quaternionf.y * quaternionf.y;
            float float3 = quaternionf.z * quaternionf.z;
            float float4 = quaternionf.z * quaternionf.w;
            float float5 = quaternionf.x * quaternionf.y;
            float float6 = quaternionf.x * quaternionf.z;
            float float7 = quaternionf.y * quaternionf.w;
            float float8 = quaternionf.y * quaternionf.z;
            float float9 = quaternionf.x * quaternionf.w;
            byteBuffer.putFloat(int0, float0 + float1 - float3 - float2)
                .putFloat(int0 + 4, float5 + float4 + float4 + float5)
                .putFloat(int0 + 8, float6 - float7 + float6 - float7)
                .putFloat(int0 + 12, -float4 + float5 - float4 + float5)
                .putFloat(int0 + 16, float2 - float3 + float0 - float1)
                .putFloat(int0 + 20, float8 + float8 + float9 + float9)
                .putFloat(int0 + 24, float7 + float6 + float6 + float7)
                .putFloat(int0 + 28, float8 + float8 - float9 - float9)
                .putFloat(int0 + 32, float3 - float2 - float1 + float0)
                .putLong(int0 + 36, 0L)
                .putFloat(int0 + 44, 0.0F);
        }

        @Override
        public void putMatrix4x3f(Quaternionf quaternionf, int int0, FloatBuffer floatBuffer) {
            float float0 = quaternionf.w * quaternionf.w;
            float float1 = quaternionf.x * quaternionf.x;
            float float2 = quaternionf.y * quaternionf.y;
            float float3 = quaternionf.z * quaternionf.z;
            float float4 = quaternionf.z * quaternionf.w;
            float float5 = quaternionf.x * quaternionf.y;
            float float6 = quaternionf.x * quaternionf.z;
            float float7 = quaternionf.y * quaternionf.w;
            float float8 = quaternionf.y * quaternionf.z;
            float float9 = quaternionf.x * quaternionf.w;
            floatBuffer.put(int0, float0 + float1 - float3 - float2)
                .put(int0 + 1, float5 + float4 + float4 + float5)
                .put(int0 + 2, float6 - float7 + float6 - float7)
                .put(int0 + 3, -float4 + float5 - float4 + float5)
                .put(int0 + 4, float2 - float3 + float0 - float1)
                .put(int0 + 5, float8 + float8 + float9 + float9)
                .put(int0 + 6, float7 + float6 + float6 + float7)
                .put(int0 + 7, float8 + float8 - float9 - float9)
                .put(int0 + 8, float3 - float2 - float1 + float0)
                .put(int0 + 9, 0.0F)
                .put(int0 + 10, 0.0F)
                .put(int0 + 11, 0.0F);
        }
    }

    public static class MemUtilUnsafe extends MemUtil.MemUtilNIO {
        public static final Unsafe UNSAFE = getUnsafeInstance();
        public static final long ADDRESS;
        public static final long Matrix2f_m00;
        public static final long Matrix3f_m00;
        public static final long Matrix3d_m00;
        public static final long Matrix4f_m00;
        public static final long Matrix4d_m00;
        public static final long Matrix4x3f_m00;
        public static final long Matrix3x2f_m00;
        public static final long Vector4f_x;
        public static final long Vector4i_x;
        public static final long Vector3f_x;
        public static final long Vector3i_x;
        public static final long Vector2f_x;
        public static final long Vector2i_x;
        public static final long Quaternionf_x;
        public static final long floatArrayOffset;

        private static long findBufferAddress() {
            try {
                return UNSAFE.objectFieldOffset(getDeclaredField(Buffer.class, "address"));
            } catch (Exception exception) {
                throw new UnsupportedOperationException(exception);
            }
        }

        private static long checkMatrix4f() throws NoSuchFieldException, SecurityException {
            Field field = Matrix4f.class.getDeclaredField("m00");
            long long0 = UNSAFE.objectFieldOffset(field);

            for (int int0 = 1; int0 < 16; int0++) {
                int int1 = int0 >>> 2;
                int int2 = int0 & 3;
                field = Matrix4f.class.getDeclaredField("m" + int1 + int2);
                long long1 = UNSAFE.objectFieldOffset(field);
                if (long1 != long0 + (int0 << 2)) {
                    throw new UnsupportedOperationException("Unexpected Matrix4f element offset");
                }
            }

            return long0;
        }

        private static long checkMatrix4d() throws NoSuchFieldException, SecurityException {
            Field field = Matrix4d.class.getDeclaredField("m00");
            long long0 = UNSAFE.objectFieldOffset(field);

            for (int int0 = 1; int0 < 16; int0++) {
                int int1 = int0 >>> 2;
                int int2 = int0 & 3;
                field = Matrix4d.class.getDeclaredField("m" + int1 + int2);
                long long1 = UNSAFE.objectFieldOffset(field);
                if (long1 != long0 + (int0 << 3)) {
                    throw new UnsupportedOperationException("Unexpected Matrix4d element offset");
                }
            }

            return long0;
        }

        private static long checkMatrix4x3f() throws NoSuchFieldException, SecurityException {
            Field field = Matrix4x3f.class.getDeclaredField("m00");
            long long0 = UNSAFE.objectFieldOffset(field);

            for (int int0 = 1; int0 < 12; int0++) {
                int int1 = int0 / 3;
                int int2 = int0 % 3;
                field = Matrix4x3f.class.getDeclaredField("m" + int1 + int2);
                long long1 = UNSAFE.objectFieldOffset(field);
                if (long1 != long0 + (int0 << 2)) {
                    throw new UnsupportedOperationException("Unexpected Matrix4x3f element offset");
                }
            }

            return long0;
        }

        private static long checkMatrix3f() throws NoSuchFieldException, SecurityException {
            Field field = Matrix3f.class.getDeclaredField("m00");
            long long0 = UNSAFE.objectFieldOffset(field);

            for (int int0 = 1; int0 < 9; int0++) {
                int int1 = int0 / 3;
                int int2 = int0 % 3;
                field = Matrix3f.class.getDeclaredField("m" + int1 + int2);
                long long1 = UNSAFE.objectFieldOffset(field);
                if (long1 != long0 + (int0 << 2)) {
                    throw new UnsupportedOperationException("Unexpected Matrix3f element offset");
                }
            }

            return long0;
        }

        private static long checkMatrix3d() throws NoSuchFieldException, SecurityException {
            Field field = Matrix3d.class.getDeclaredField("m00");
            long long0 = UNSAFE.objectFieldOffset(field);

            for (int int0 = 1; int0 < 9; int0++) {
                int int1 = int0 / 3;
                int int2 = int0 % 3;
                field = Matrix3d.class.getDeclaredField("m" + int1 + int2);
                long long1 = UNSAFE.objectFieldOffset(field);
                if (long1 != long0 + (int0 << 3)) {
                    throw new UnsupportedOperationException("Unexpected Matrix3d element offset");
                }
            }

            return long0;
        }

        private static long checkMatrix3x2f() throws NoSuchFieldException, SecurityException {
            Field field = Matrix3x2f.class.getDeclaredField("m00");
            long long0 = UNSAFE.objectFieldOffset(field);

            for (int int0 = 1; int0 < 6; int0++) {
                int int1 = int0 / 2;
                int int2 = int0 % 2;
                field = Matrix3x2f.class.getDeclaredField("m" + int1 + int2);
                long long1 = UNSAFE.objectFieldOffset(field);
                if (long1 != long0 + (int0 << 2)) {
                    throw new UnsupportedOperationException("Unexpected Matrix3x2f element offset");
                }
            }

            return long0;
        }

        private static long checkMatrix2f() throws NoSuchFieldException, SecurityException {
            Field field = Matrix2f.class.getDeclaredField("m00");
            long long0 = UNSAFE.objectFieldOffset(field);

            for (int int0 = 1; int0 < 4; int0++) {
                int int1 = int0 / 2;
                int int2 = int0 % 2;
                field = Matrix2f.class.getDeclaredField("m" + int1 + int2);
                long long1 = UNSAFE.objectFieldOffset(field);
                if (long1 != long0 + (int0 << 2)) {
                    throw new UnsupportedOperationException("Unexpected Matrix2f element offset");
                }
            }

            return long0;
        }

        private static long checkVector4f() throws NoSuchFieldException, SecurityException {
            Field field = Vector4f.class.getDeclaredField("x");
            long long0 = UNSAFE.objectFieldOffset(field);
            String[] strings = new String[]{"y", "z", "w"};

            for (int int0 = 1; int0 < 4; int0++) {
                field = Vector4f.class.getDeclaredField(strings[int0 - 1]);
                long long1 = UNSAFE.objectFieldOffset(field);
                if (long1 != long0 + (int0 << 2)) {
                    throw new UnsupportedOperationException("Unexpected Vector4f element offset");
                }
            }

            return long0;
        }

        private static long checkVector4i() throws NoSuchFieldException, SecurityException {
            Field field = Vector4i.class.getDeclaredField("x");
            long long0 = UNSAFE.objectFieldOffset(field);
            String[] strings = new String[]{"y", "z", "w"};

            for (int int0 = 1; int0 < 4; int0++) {
                field = Vector4i.class.getDeclaredField(strings[int0 - 1]);
                long long1 = UNSAFE.objectFieldOffset(field);
                if (long1 != long0 + (int0 << 2)) {
                    throw new UnsupportedOperationException("Unexpected Vector4i element offset");
                }
            }

            return long0;
        }

        private static long checkVector3f() throws NoSuchFieldException, SecurityException {
            Field field = Vector3f.class.getDeclaredField("x");
            long long0 = UNSAFE.objectFieldOffset(field);
            String[] strings = new String[]{"y", "z"};

            for (int int0 = 1; int0 < 3; int0++) {
                field = Vector3f.class.getDeclaredField(strings[int0 - 1]);
                long long1 = UNSAFE.objectFieldOffset(field);
                if (long1 != long0 + (int0 << 2)) {
                    throw new UnsupportedOperationException("Unexpected Vector3f element offset");
                }
            }

            return long0;
        }

        private static long checkVector3i() throws NoSuchFieldException, SecurityException {
            Field field = Vector3i.class.getDeclaredField("x");
            long long0 = UNSAFE.objectFieldOffset(field);
            String[] strings = new String[]{"y", "z"};

            for (int int0 = 1; int0 < 3; int0++) {
                field = Vector3i.class.getDeclaredField(strings[int0 - 1]);
                long long1 = UNSAFE.objectFieldOffset(field);
                if (long1 != long0 + (int0 << 2)) {
                    throw new UnsupportedOperationException("Unexpected Vector3i element offset");
                }
            }

            return long0;
        }

        private static long checkVector2f() throws NoSuchFieldException, SecurityException {
            Field field = Vector2f.class.getDeclaredField("x");
            long long0 = UNSAFE.objectFieldOffset(field);
            field = Vector2f.class.getDeclaredField("y");
            long long1 = UNSAFE.objectFieldOffset(field);
            if (long1 != long0 + 4L) {
                throw new UnsupportedOperationException("Unexpected Vector2f element offset");
            } else {
                return long0;
            }
        }

        private static long checkVector2i() throws NoSuchFieldException, SecurityException {
            Field field = Vector2i.class.getDeclaredField("x");
            long long0 = UNSAFE.objectFieldOffset(field);
            field = Vector2i.class.getDeclaredField("y");
            long long1 = UNSAFE.objectFieldOffset(field);
            if (long1 != long0 + 4L) {
                throw new UnsupportedOperationException("Unexpected Vector2i element offset");
            } else {
                return long0;
            }
        }

        private static long checkQuaternionf() throws NoSuchFieldException, SecurityException {
            Field field = Quaternionf.class.getDeclaredField("x");
            long long0 = UNSAFE.objectFieldOffset(field);
            String[] strings = new String[]{"y", "z", "w"};

            for (int int0 = 1; int0 < 4; int0++) {
                field = Quaternionf.class.getDeclaredField(strings[int0 - 1]);
                long long1 = UNSAFE.objectFieldOffset(field);
                if (long1 != long0 + (int0 << 2)) {
                    throw new UnsupportedOperationException("Unexpected Quaternionf element offset");
                }
            }

            return long0;
        }

        private static Field getDeclaredField(Class clazz1, String string) throws NoSuchFieldException {
            Class clazz0 = clazz1;

            do {
                try {
                    return clazz0.getDeclaredField(string);
                } catch (NoSuchFieldException noSuchFieldException) {
                    clazz0 = clazz0.getSuperclass();
                } catch (SecurityException securityException) {
                    clazz0 = clazz0.getSuperclass();
                }
            } while (clazz0 != null);

            throw new NoSuchFieldException(string + " does not exist in " + clazz1.getName() + " or any of its superclasses.");
        }

        public static Unsafe getUnsafeInstance() throws SecurityException {
            Field[] fields = Unsafe.class.getDeclaredFields();

            for (int int0 = 0; int0 < fields.length; int0++) {
                Field field = fields[int0];
                if (field.getType().equals(Unsafe.class)) {
                    int int1 = field.getModifiers();
                    if (Modifier.isStatic(int1) && Modifier.isFinal(int1)) {
                        field.setAccessible(true);

                        try {
                            return (Unsafe)field.get(null);
                        } catch (IllegalAccessException illegalAccessException) {
                            break;
                        }
                    }
                }
            }

            throw new UnsupportedOperationException();
        }

        public static void put(Matrix4f matrix4f, long long0) {
            for (int int0 = 0; int0 < 8; int0++) {
                UNSAFE.putLong(null, long0 + (int0 << 3), UNSAFE.getLong(matrix4f, Matrix4f_m00 + (int0 << 3)));
            }
        }

        public static void put4x3(Matrix4f matrix4f, long long0) {
            Unsafe unsafe = UNSAFE;

            for (int int0 = 0; int0 < 4; int0++) {
                unsafe.putLong(null, long0 + 12 * int0, unsafe.getLong(matrix4f, Matrix4f_m00 + (int0 << 4)));
            }

            unsafe.putFloat(null, long0 + 8L, matrix4f.m02());
            unsafe.putFloat(null, long0 + 20L, matrix4f.m12());
            unsafe.putFloat(null, long0 + 32L, matrix4f.m22());
            unsafe.putFloat(null, long0 + 44L, matrix4f.m32());
        }

        public static void put3x4(Matrix4f matrix4f, long long0) {
            for (int int0 = 0; int0 < 6; int0++) {
                UNSAFE.putLong(null, long0 + (int0 << 3), UNSAFE.getLong(matrix4f, Matrix4f_m00 + (int0 << 3)));
            }
        }

        public static void put(Matrix4x3f matrix4x3f, long long0) {
            for (int int0 = 0; int0 < 6; int0++) {
                UNSAFE.putLong(null, long0 + (int0 << 3), UNSAFE.getLong(matrix4x3f, Matrix4x3f_m00 + (int0 << 3)));
            }
        }

        public static void put4x4(Matrix4x3f matrix4x3f, long long0) {
            for (int int0 = 0; int0 < 4; int0++) {
                UNSAFE.putLong(null, long0 + (int0 << 4), UNSAFE.getLong(matrix4x3f, Matrix4x3f_m00 + 12 * int0));
                long long1 = UNSAFE.getInt(matrix4x3f, Matrix4x3f_m00 + 8L + 12 * int0) & 4294967295L;
                UNSAFE.putLong(null, long0 + 8L + (int0 << 4), long1);
            }

            UNSAFE.putFloat(null, long0 + 60L, 1.0F);
        }

        public static void put3x4(Matrix4x3f matrix4x3f, long long0) {
            for (int int0 = 0; int0 < 3; int0++) {
                UNSAFE.putLong(null, long0 + (int0 << 4), UNSAFE.getLong(matrix4x3f, Matrix4x3f_m00 + 12 * int0));
                UNSAFE.putFloat(null, long0 + (int0 << 4) + 8L, UNSAFE.getFloat(matrix4x3f, Matrix4x3f_m00 + 8L + 12 * int0));
                UNSAFE.putFloat(null, long0 + (int0 << 4) + 12L, 0.0F);
            }
        }

        public static void put4x4(Matrix4x3d matrix4x3d, long long0) {
            Unsafe unsafe = UNSAFE;
            unsafe.putDouble(null, long0, matrix4x3d.m00());
            unsafe.putDouble(null, long0 + 8L, matrix4x3d.m01());
            unsafe.putDouble(null, long0 + 16L, matrix4x3d.m02());
            unsafe.putDouble(null, long0 + 24L, 0.0);
            unsafe.putDouble(null, long0 + 32L, matrix4x3d.m10());
            unsafe.putDouble(null, long0 + 40L, matrix4x3d.m11());
            unsafe.putDouble(null, long0 + 48L, matrix4x3d.m12());
            unsafe.putDouble(null, long0 + 56L, 0.0);
            unsafe.putDouble(null, long0 + 64L, matrix4x3d.m20());
            unsafe.putDouble(null, long0 + 72L, matrix4x3d.m21());
            unsafe.putDouble(null, long0 + 80L, matrix4x3d.m22());
            unsafe.putDouble(null, long0 + 88L, 0.0);
            unsafe.putDouble(null, long0 + 96L, matrix4x3d.m30());
            unsafe.putDouble(null, long0 + 104L, matrix4x3d.m31());
            unsafe.putDouble(null, long0 + 112L, matrix4x3d.m32());
            unsafe.putDouble(null, long0 + 120L, 1.0);
        }

        public static void put4x4(Matrix3x2f matrix3x2f, long long0) {
            Unsafe unsafe = UNSAFE;
            unsafe.putLong(null, long0, unsafe.getLong(matrix3x2f, Matrix3x2f_m00));
            unsafe.putLong(null, long0 + 8L, 0L);
            unsafe.putLong(null, long0 + 16L, unsafe.getLong(matrix3x2f, Matrix3x2f_m00 + 8L));
            unsafe.putLong(null, long0 + 24L, 0L);
            unsafe.putLong(null, long0 + 32L, 0L);
            unsafe.putLong(null, long0 + 40L, 1065353216L);
            unsafe.putLong(null, long0 + 48L, unsafe.getLong(matrix3x2f, Matrix3x2f_m00 + 16L));
            unsafe.putLong(null, long0 + 56L, 4575657221408423936L);
        }

        public static void put4x4(Matrix3x2d matrix3x2d, long long0) {
            Unsafe unsafe = UNSAFE;
            unsafe.putDouble(null, long0, matrix3x2d.m00());
            unsafe.putDouble(null, long0 + 8L, matrix3x2d.m01());
            unsafe.putDouble(null, long0 + 16L, 0.0);
            unsafe.putDouble(null, long0 + 24L, 0.0);
            unsafe.putDouble(null, long0 + 32L, matrix3x2d.m10());
            unsafe.putDouble(null, long0 + 40L, matrix3x2d.m11());
            unsafe.putDouble(null, long0 + 48L, 0.0);
            unsafe.putDouble(null, long0 + 56L, 0.0);
            unsafe.putDouble(null, long0 + 64L, 0.0);
            unsafe.putDouble(null, long0 + 72L, 0.0);
            unsafe.putDouble(null, long0 + 80L, 1.0);
            unsafe.putDouble(null, long0 + 88L, 0.0);
            unsafe.putDouble(null, long0 + 96L, matrix3x2d.m20());
            unsafe.putDouble(null, long0 + 104L, matrix3x2d.m21());
            unsafe.putDouble(null, long0 + 112L, 0.0);
            unsafe.putDouble(null, long0 + 120L, 1.0);
        }

        public static void put3x3(Matrix3x2f matrix3x2f, long long0) {
            Unsafe unsafe = UNSAFE;
            unsafe.putLong(null, long0, unsafe.getLong(matrix3x2f, Matrix3x2f_m00));
            unsafe.putInt(null, long0 + 8L, 0);
            unsafe.putLong(null, long0 + 12L, unsafe.getLong(matrix3x2f, Matrix3x2f_m00 + 8L));
            unsafe.putInt(null, long0 + 20L, 0);
            unsafe.putLong(null, long0 + 24L, unsafe.getLong(matrix3x2f, Matrix3x2f_m00 + 16L));
            unsafe.putFloat(null, long0 + 32L, 0.0F);
        }

        public static void put3x3(Matrix3x2d matrix3x2d, long long0) {
            Unsafe unsafe = UNSAFE;
            unsafe.putDouble(null, long0, matrix3x2d.m00());
            unsafe.putDouble(null, long0 + 8L, matrix3x2d.m01());
            unsafe.putDouble(null, long0 + 16L, 0.0);
            unsafe.putDouble(null, long0 + 24L, matrix3x2d.m10());
            unsafe.putDouble(null, long0 + 32L, matrix3x2d.m11());
            unsafe.putDouble(null, long0 + 40L, 0.0);
            unsafe.putDouble(null, long0 + 48L, matrix3x2d.m20());
            unsafe.putDouble(null, long0 + 56L, matrix3x2d.m21());
            unsafe.putDouble(null, long0 + 64L, 1.0);
        }

        public static void putTransposed(Matrix4f matrix4f, long long0) {
            Unsafe unsafe = UNSAFE;
            unsafe.putFloat(null, long0, matrix4f.m00());
            unsafe.putFloat(null, long0 + 4L, matrix4f.m10());
            unsafe.putFloat(null, long0 + 8L, matrix4f.m20());
            unsafe.putFloat(null, long0 + 12L, matrix4f.m30());
            unsafe.putFloat(null, long0 + 16L, matrix4f.m01());
            unsafe.putFloat(null, long0 + 20L, matrix4f.m11());
            unsafe.putFloat(null, long0 + 24L, matrix4f.m21());
            unsafe.putFloat(null, long0 + 28L, matrix4f.m31());
            unsafe.putFloat(null, long0 + 32L, matrix4f.m02());
            unsafe.putFloat(null, long0 + 36L, matrix4f.m12());
            unsafe.putFloat(null, long0 + 40L, matrix4f.m22());
            unsafe.putFloat(null, long0 + 44L, matrix4f.m32());
            unsafe.putFloat(null, long0 + 48L, matrix4f.m03());
            unsafe.putFloat(null, long0 + 52L, matrix4f.m13());
            unsafe.putFloat(null, long0 + 56L, matrix4f.m23());
            unsafe.putFloat(null, long0 + 60L, matrix4f.m33());
        }

        public static void put4x3Transposed(Matrix4f matrix4f, long long0) {
            Unsafe unsafe = UNSAFE;
            unsafe.putFloat(null, long0, matrix4f.m00());
            unsafe.putFloat(null, long0 + 4L, matrix4f.m10());
            unsafe.putFloat(null, long0 + 8L, matrix4f.m20());
            unsafe.putFloat(null, long0 + 12L, matrix4f.m30());
            unsafe.putFloat(null, long0 + 16L, matrix4f.m01());
            unsafe.putFloat(null, long0 + 20L, matrix4f.m11());
            unsafe.putFloat(null, long0 + 24L, matrix4f.m21());
            unsafe.putFloat(null, long0 + 28L, matrix4f.m31());
            unsafe.putFloat(null, long0 + 32L, matrix4f.m02());
            unsafe.putFloat(null, long0 + 36L, matrix4f.m12());
            unsafe.putFloat(null, long0 + 40L, matrix4f.m22());
            unsafe.putFloat(null, long0 + 44L, matrix4f.m32());
        }

        public static void putTransposed(Matrix4x3f matrix4x3f, long long0) {
            Unsafe unsafe = UNSAFE;
            unsafe.putFloat(null, long0, matrix4x3f.m00());
            unsafe.putFloat(null, long0 + 4L, matrix4x3f.m10());
            unsafe.putFloat(null, long0 + 8L, matrix4x3f.m20());
            unsafe.putFloat(null, long0 + 12L, matrix4x3f.m30());
            unsafe.putFloat(null, long0 + 16L, matrix4x3f.m01());
            unsafe.putFloat(null, long0 + 20L, matrix4x3f.m11());
            unsafe.putFloat(null, long0 + 24L, matrix4x3f.m21());
            unsafe.putFloat(null, long0 + 28L, matrix4x3f.m31());
            unsafe.putFloat(null, long0 + 32L, matrix4x3f.m02());
            unsafe.putFloat(null, long0 + 36L, matrix4x3f.m12());
            unsafe.putFloat(null, long0 + 40L, matrix4x3f.m22());
            unsafe.putFloat(null, long0 + 44L, matrix4x3f.m32());
        }

        public static void putTransposed(Matrix3f matrix3f, long long0) {
            Unsafe unsafe = UNSAFE;
            unsafe.putFloat(null, long0, matrix3f.m00());
            unsafe.putFloat(null, long0 + 4L, matrix3f.m10());
            unsafe.putFloat(null, long0 + 8L, matrix3f.m20());
            unsafe.putFloat(null, long0 + 12L, matrix3f.m01());
            unsafe.putFloat(null, long0 + 16L, matrix3f.m11());
            unsafe.putFloat(null, long0 + 20L, matrix3f.m21());
            unsafe.putFloat(null, long0 + 24L, matrix3f.m02());
            unsafe.putFloat(null, long0 + 28L, matrix3f.m12());
            unsafe.putFloat(null, long0 + 32L, matrix3f.m22());
        }

        public static void putTransposed(Matrix2f matrix2f, long long0) {
            UNSAFE.putFloat(null, long0, matrix2f.m00());
            UNSAFE.putFloat(null, long0 + 4L, matrix2f.m10());
            UNSAFE.putFloat(null, long0 + 8L, matrix2f.m01());
            UNSAFE.putFloat(null, long0 + 12L, matrix2f.m11());
        }

        public static void put(Matrix4d matrix4d, long long0) {
            Unsafe unsafe = UNSAFE;
            unsafe.putDouble(null, long0, matrix4d.m00());
            unsafe.putDouble(null, long0 + 8L, matrix4d.m01());
            unsafe.putDouble(null, long0 + 16L, matrix4d.m02());
            unsafe.putDouble(null, long0 + 24L, matrix4d.m03());
            unsafe.putDouble(null, long0 + 32L, matrix4d.m10());
            unsafe.putDouble(null, long0 + 40L, matrix4d.m11());
            unsafe.putDouble(null, long0 + 48L, matrix4d.m12());
            unsafe.putDouble(null, long0 + 56L, matrix4d.m13());
            unsafe.putDouble(null, long0 + 64L, matrix4d.m20());
            unsafe.putDouble(null, long0 + 72L, matrix4d.m21());
            unsafe.putDouble(null, long0 + 80L, matrix4d.m22());
            unsafe.putDouble(null, long0 + 88L, matrix4d.m23());
            unsafe.putDouble(null, long0 + 96L, matrix4d.m30());
            unsafe.putDouble(null, long0 + 104L, matrix4d.m31());
            unsafe.putDouble(null, long0 + 112L, matrix4d.m32());
            unsafe.putDouble(null, long0 + 120L, matrix4d.m33());
        }

        public static void put(Matrix4x3d matrix4x3d, long long0) {
            Unsafe unsafe = UNSAFE;
            unsafe.putDouble(null, long0, matrix4x3d.m00());
            unsafe.putDouble(null, long0 + 8L, matrix4x3d.m01());
            unsafe.putDouble(null, long0 + 16L, matrix4x3d.m02());
            unsafe.putDouble(null, long0 + 24L, matrix4x3d.m10());
            unsafe.putDouble(null, long0 + 32L, matrix4x3d.m11());
            unsafe.putDouble(null, long0 + 40L, matrix4x3d.m12());
            unsafe.putDouble(null, long0 + 48L, matrix4x3d.m20());
            unsafe.putDouble(null, long0 + 56L, matrix4x3d.m21());
            unsafe.putDouble(null, long0 + 64L, matrix4x3d.m22());
            unsafe.putDouble(null, long0 + 72L, matrix4x3d.m30());
            unsafe.putDouble(null, long0 + 80L, matrix4x3d.m31());
            unsafe.putDouble(null, long0 + 88L, matrix4x3d.m32());
        }

        public static void putTransposed(Matrix4d matrix4d, long long0) {
            Unsafe unsafe = UNSAFE;
            unsafe.putDouble(null, long0, matrix4d.m00());
            unsafe.putDouble(null, long0 + 8L, matrix4d.m10());
            unsafe.putDouble(null, long0 + 16L, matrix4d.m20());
            unsafe.putDouble(null, long0 + 24L, matrix4d.m30());
            unsafe.putDouble(null, long0 + 32L, matrix4d.m01());
            unsafe.putDouble(null, long0 + 40L, matrix4d.m11());
            unsafe.putDouble(null, long0 + 48L, matrix4d.m21());
            unsafe.putDouble(null, long0 + 56L, matrix4d.m31());
            unsafe.putDouble(null, long0 + 64L, matrix4d.m02());
            unsafe.putDouble(null, long0 + 72L, matrix4d.m12());
            unsafe.putDouble(null, long0 + 80L, matrix4d.m22());
            unsafe.putDouble(null, long0 + 88L, matrix4d.m32());
            unsafe.putDouble(null, long0 + 96L, matrix4d.m03());
            unsafe.putDouble(null, long0 + 104L, matrix4d.m13());
            unsafe.putDouble(null, long0 + 112L, matrix4d.m23());
            unsafe.putDouble(null, long0 + 120L, matrix4d.m33());
        }

        public static void putfTransposed(Matrix4d matrix4d, long long0) {
            Unsafe unsafe = UNSAFE;
            unsafe.putFloat(null, long0, (float)matrix4d.m00());
            unsafe.putFloat(null, long0 + 4L, (float)matrix4d.m10());
            unsafe.putFloat(null, long0 + 8L, (float)matrix4d.m20());
            unsafe.putFloat(null, long0 + 12L, (float)matrix4d.m30());
            unsafe.putFloat(null, long0 + 16L, (float)matrix4d.m01());
            unsafe.putFloat(null, long0 + 20L, (float)matrix4d.m11());
            unsafe.putFloat(null, long0 + 24L, (float)matrix4d.m21());
            unsafe.putFloat(null, long0 + 28L, (float)matrix4d.m31());
            unsafe.putFloat(null, long0 + 32L, (float)matrix4d.m02());
            unsafe.putFloat(null, long0 + 36L, (float)matrix4d.m12());
            unsafe.putFloat(null, long0 + 40L, (float)matrix4d.m22());
            unsafe.putFloat(null, long0 + 44L, (float)matrix4d.m32());
            unsafe.putFloat(null, long0 + 48L, (float)matrix4d.m03());
            unsafe.putFloat(null, long0 + 52L, (float)matrix4d.m13());
            unsafe.putFloat(null, long0 + 56L, (float)matrix4d.m23());
            unsafe.putFloat(null, long0 + 60L, (float)matrix4d.m33());
        }

        public static void put4x3Transposed(Matrix4d matrix4d, long long0) {
            Unsafe unsafe = UNSAFE;
            unsafe.putDouble(null, long0, matrix4d.m00());
            unsafe.putDouble(null, long0 + 8L, matrix4d.m10());
            unsafe.putDouble(null, long0 + 16L, matrix4d.m20());
            unsafe.putDouble(null, long0 + 24L, matrix4d.m30());
            unsafe.putDouble(null, long0 + 32L, matrix4d.m01());
            unsafe.putDouble(null, long0 + 40L, matrix4d.m11());
            unsafe.putDouble(null, long0 + 48L, matrix4d.m21());
            unsafe.putDouble(null, long0 + 56L, matrix4d.m31());
            unsafe.putDouble(null, long0 + 64L, matrix4d.m02());
            unsafe.putDouble(null, long0 + 72L, matrix4d.m12());
            unsafe.putDouble(null, long0 + 80L, matrix4d.m22());
            unsafe.putDouble(null, long0 + 88L, matrix4d.m32());
        }

        public static void putTransposed(Matrix4x3d matrix4x3d, long long0) {
            Unsafe unsafe = UNSAFE;
            unsafe.putDouble(null, long0, matrix4x3d.m00());
            unsafe.putDouble(null, long0 + 8L, matrix4x3d.m10());
            unsafe.putDouble(null, long0 + 16L, matrix4x3d.m20());
            unsafe.putDouble(null, long0 + 24L, matrix4x3d.m30());
            unsafe.putDouble(null, long0 + 32L, matrix4x3d.m01());
            unsafe.putDouble(null, long0 + 40L, matrix4x3d.m11());
            unsafe.putDouble(null, long0 + 48L, matrix4x3d.m21());
            unsafe.putDouble(null, long0 + 56L, matrix4x3d.m31());
            unsafe.putDouble(null, long0 + 64L, matrix4x3d.m02());
            unsafe.putDouble(null, long0 + 72L, matrix4x3d.m12());
            unsafe.putDouble(null, long0 + 80L, matrix4x3d.m22());
            unsafe.putDouble(null, long0 + 88L, matrix4x3d.m32());
        }

        public static void putTransposed(Matrix2d matrix2d, long long0) {
            UNSAFE.putDouble(null, long0, matrix2d.m00());
            UNSAFE.putDouble(null, long0 + 8L, matrix2d.m10());
            UNSAFE.putDouble(null, long0 + 16L, matrix2d.m10());
            UNSAFE.putDouble(null, long0 + 24L, matrix2d.m10());
        }

        public static void putfTransposed(Matrix4x3d matrix4x3d, long long0) {
            Unsafe unsafe = UNSAFE;
            unsafe.putFloat(null, long0, (float)matrix4x3d.m00());
            unsafe.putFloat(null, long0 + 4L, (float)matrix4x3d.m10());
            unsafe.putFloat(null, long0 + 8L, (float)matrix4x3d.m20());
            unsafe.putFloat(null, long0 + 12L, (float)matrix4x3d.m30());
            unsafe.putFloat(null, long0 + 16L, (float)matrix4x3d.m01());
            unsafe.putFloat(null, long0 + 20L, (float)matrix4x3d.m11());
            unsafe.putFloat(null, long0 + 24L, (float)matrix4x3d.m21());
            unsafe.putFloat(null, long0 + 28L, (float)matrix4x3d.m31());
            unsafe.putFloat(null, long0 + 32L, (float)matrix4x3d.m02());
            unsafe.putFloat(null, long0 + 36L, (float)matrix4x3d.m12());
            unsafe.putFloat(null, long0 + 40L, (float)matrix4x3d.m22());
            unsafe.putFloat(null, long0 + 44L, (float)matrix4x3d.m32());
        }

        public static void putfTransposed(Matrix2d matrix2d, long long0) {
            UNSAFE.putFloat(null, long0, (float)matrix2d.m00());
            UNSAFE.putFloat(null, long0 + 4L, (float)matrix2d.m00());
            UNSAFE.putFloat(null, long0 + 8L, (float)matrix2d.m00());
            UNSAFE.putFloat(null, long0 + 12L, (float)matrix2d.m00());
        }

        public static void putf(Matrix4d matrix4d, long long0) {
            Unsafe unsafe = UNSAFE;
            unsafe.putFloat(null, long0, (float)matrix4d.m00());
            unsafe.putFloat(null, long0 + 4L, (float)matrix4d.m01());
            unsafe.putFloat(null, long0 + 8L, (float)matrix4d.m02());
            unsafe.putFloat(null, long0 + 12L, (float)matrix4d.m03());
            unsafe.putFloat(null, long0 + 16L, (float)matrix4d.m10());
            unsafe.putFloat(null, long0 + 20L, (float)matrix4d.m11());
            unsafe.putFloat(null, long0 + 24L, (float)matrix4d.m12());
            unsafe.putFloat(null, long0 + 28L, (float)matrix4d.m13());
            unsafe.putFloat(null, long0 + 32L, (float)matrix4d.m20());
            unsafe.putFloat(null, long0 + 36L, (float)matrix4d.m21());
            unsafe.putFloat(null, long0 + 40L, (float)matrix4d.m22());
            unsafe.putFloat(null, long0 + 44L, (float)matrix4d.m23());
            unsafe.putFloat(null, long0 + 48L, (float)matrix4d.m30());
            unsafe.putFloat(null, long0 + 52L, (float)matrix4d.m31());
            unsafe.putFloat(null, long0 + 56L, (float)matrix4d.m32());
            unsafe.putFloat(null, long0 + 60L, (float)matrix4d.m33());
        }

        public static void putf(Matrix4x3d matrix4x3d, long long0) {
            Unsafe unsafe = UNSAFE;
            unsafe.putFloat(null, long0, (float)matrix4x3d.m00());
            unsafe.putFloat(null, long0 + 4L, (float)matrix4x3d.m01());
            unsafe.putFloat(null, long0 + 8L, (float)matrix4x3d.m02());
            unsafe.putFloat(null, long0 + 12L, (float)matrix4x3d.m10());
            unsafe.putFloat(null, long0 + 16L, (float)matrix4x3d.m11());
            unsafe.putFloat(null, long0 + 20L, (float)matrix4x3d.m12());
            unsafe.putFloat(null, long0 + 24L, (float)matrix4x3d.m20());
            unsafe.putFloat(null, long0 + 28L, (float)matrix4x3d.m21());
            unsafe.putFloat(null, long0 + 32L, (float)matrix4x3d.m22());
            unsafe.putFloat(null, long0 + 36L, (float)matrix4x3d.m30());
            unsafe.putFloat(null, long0 + 40L, (float)matrix4x3d.m31());
            unsafe.putFloat(null, long0 + 44L, (float)matrix4x3d.m32());
        }

        public static void put(Matrix3f matrix3f, long long0) {
            for (int int0 = 0; int0 < 4; int0++) {
                UNSAFE.putLong(null, long0 + (int0 << 3), UNSAFE.getLong(matrix3f, Matrix3f_m00 + (int0 << 3)));
            }

            UNSAFE.putFloat(null, long0 + 32L, matrix3f.m22());
        }

        public static void put3x4(Matrix3f matrix3f, long long0) {
            for (int int0 = 0; int0 < 3; int0++) {
                UNSAFE.putLong(null, long0 + (int0 << 4), UNSAFE.getLong(matrix3f, Matrix3f_m00 + 12 * int0));
                UNSAFE.putFloat(null, long0 + (int0 << 4) + 8L, UNSAFE.getFloat(matrix3f, Matrix3f_m00 + 8L + 12 * int0));
                UNSAFE.putFloat(null, long0 + 12 * int0, 0.0F);
            }
        }

        public static void put(Matrix3d matrix3d, long long0) {
            Unsafe unsafe = UNSAFE;
            unsafe.putDouble(null, long0, matrix3d.m00());
            unsafe.putDouble(null, long0 + 8L, matrix3d.m01());
            unsafe.putDouble(null, long0 + 16L, matrix3d.m02());
            unsafe.putDouble(null, long0 + 24L, matrix3d.m10());
            unsafe.putDouble(null, long0 + 32L, matrix3d.m11());
            unsafe.putDouble(null, long0 + 40L, matrix3d.m12());
            unsafe.putDouble(null, long0 + 48L, matrix3d.m20());
            unsafe.putDouble(null, long0 + 56L, matrix3d.m21());
            unsafe.putDouble(null, long0 + 64L, matrix3d.m22());
        }

        public static void put(Matrix3x2f matrix3x2f, long long0) {
            for (int int0 = 0; int0 < 3; int0++) {
                UNSAFE.putLong(null, long0 + (int0 << 3), UNSAFE.getLong(matrix3x2f, Matrix3x2f_m00 + (int0 << 3)));
            }
        }

        public static void put(Matrix3x2d matrix3x2d, long long0) {
            Unsafe unsafe = UNSAFE;
            unsafe.putDouble(null, long0, matrix3x2d.m00());
            unsafe.putDouble(null, long0 + 8L, matrix3x2d.m01());
            unsafe.putDouble(null, long0 + 16L, matrix3x2d.m10());
            unsafe.putDouble(null, long0 + 24L, matrix3x2d.m11());
            unsafe.putDouble(null, long0 + 32L, matrix3x2d.m20());
            unsafe.putDouble(null, long0 + 40L, matrix3x2d.m21());
        }

        public static void putf(Matrix3d matrix3d, long long0) {
            Unsafe unsafe = UNSAFE;
            unsafe.putFloat(null, long0, (float)matrix3d.m00());
            unsafe.putFloat(null, long0 + 4L, (float)matrix3d.m01());
            unsafe.putFloat(null, long0 + 8L, (float)matrix3d.m02());
            unsafe.putFloat(null, long0 + 12L, (float)matrix3d.m10());
            unsafe.putFloat(null, long0 + 16L, (float)matrix3d.m11());
            unsafe.putFloat(null, long0 + 20L, (float)matrix3d.m12());
            unsafe.putFloat(null, long0 + 24L, (float)matrix3d.m20());
            unsafe.putFloat(null, long0 + 28L, (float)matrix3d.m21());
            unsafe.putFloat(null, long0 + 32L, (float)matrix3d.m22());
        }

        public static void put(Matrix2f matrix2f, long long0) {
            UNSAFE.putLong(null, long0, UNSAFE.getLong(matrix2f, Matrix2f_m00));
            UNSAFE.putLong(null, long0 + 8L, UNSAFE.getLong(matrix2f, Matrix2f_m00 + 8L));
        }

        public static void put(Matrix2d matrix2d, long long0) {
            UNSAFE.putDouble(null, long0, matrix2d.m00());
            UNSAFE.putDouble(null, long0 + 8L, matrix2d.m01());
            UNSAFE.putDouble(null, long0 + 16L, matrix2d.m10());
            UNSAFE.putDouble(null, long0 + 24L, matrix2d.m11());
        }

        public static void putf(Matrix2d matrix2d, long long0) {
            UNSAFE.putFloat(null, long0, (float)matrix2d.m00());
            UNSAFE.putFloat(null, long0 + 4L, (float)matrix2d.m01());
            UNSAFE.putFloat(null, long0 + 8L, (float)matrix2d.m10());
            UNSAFE.putFloat(null, long0 + 12L, (float)matrix2d.m11());
        }

        public static void put(Vector4d vector4d, long long0) {
            UNSAFE.putDouble(null, long0, vector4d.x);
            UNSAFE.putDouble(null, long0 + 8L, vector4d.y);
            UNSAFE.putDouble(null, long0 + 16L, vector4d.z);
            UNSAFE.putDouble(null, long0 + 24L, vector4d.w);
        }

        public static void putf(Vector4d vector4d, long long0) {
            UNSAFE.putFloat(null, long0, (float)vector4d.x);
            UNSAFE.putFloat(null, long0 + 4L, (float)vector4d.y);
            UNSAFE.putFloat(null, long0 + 8L, (float)vector4d.z);
            UNSAFE.putFloat(null, long0 + 12L, (float)vector4d.w);
        }

        public static void put(Vector4f vector4f, long long0) {
            UNSAFE.putLong(null, long0, UNSAFE.getLong(vector4f, Vector4f_x));
            UNSAFE.putLong(null, long0 + 8L, UNSAFE.getLong(vector4f, Vector4f_x + 8L));
        }

        public static void put(Vector4i vector4i, long long0) {
            UNSAFE.putLong(null, long0, UNSAFE.getLong(vector4i, Vector4i_x));
            UNSAFE.putLong(null, long0 + 8L, UNSAFE.getLong(vector4i, Vector4i_x + 8L));
        }

        public static void put(Vector3f vector3f, long long0) {
            UNSAFE.putLong(null, long0, UNSAFE.getLong(vector3f, Vector3f_x));
            UNSAFE.putFloat(null, long0 + 8L, vector3f.z);
        }

        public static void put(Vector3d vector3d, long long0) {
            UNSAFE.putDouble(null, long0, vector3d.x);
            UNSAFE.putDouble(null, long0 + 8L, vector3d.y);
            UNSAFE.putDouble(null, long0 + 16L, vector3d.z);
        }

        public static void putf(Vector3d vector3d, long long0) {
            UNSAFE.putFloat(null, long0, (float)vector3d.x);
            UNSAFE.putFloat(null, long0 + 4L, (float)vector3d.y);
            UNSAFE.putFloat(null, long0 + 8L, (float)vector3d.z);
        }

        public static void put(Vector3i vector3i, long long0) {
            UNSAFE.putLong(null, long0, UNSAFE.getLong(vector3i, Vector3i_x));
            UNSAFE.putInt(null, long0 + 8L, vector3i.z);
        }

        public static void put(Vector2f vector2f, long long0) {
            UNSAFE.putLong(null, long0, UNSAFE.getLong(vector2f, Vector2f_x));
        }

        public static void put(Vector2d vector2d, long long0) {
            UNSAFE.putDouble(null, long0, vector2d.x);
            UNSAFE.putDouble(null, long0 + 8L, vector2d.y);
        }

        public static void put(Vector2i vector2i, long long0) {
            UNSAFE.putLong(null, long0, UNSAFE.getLong(vector2i, Vector2i_x));
        }

        public static void get(Matrix4f matrix4f, long long0) {
            for (int int0 = 0; int0 < 8; int0++) {
                UNSAFE.putLong(matrix4f, Matrix4f_m00 + (int0 << 3), UNSAFE.getLong(long0 + (int0 << 3)));
            }
        }

        public static void getTransposed(Matrix4f matrix4f, long long0) {
            matrix4f._m00(UNSAFE.getFloat(long0))
                ._m10(UNSAFE.getFloat(long0 + 4L))
                ._m20(UNSAFE.getFloat(long0 + 8L))
                ._m30(UNSAFE.getFloat(long0 + 12L))
                ._m01(UNSAFE.getFloat(long0 + 16L))
                ._m11(UNSAFE.getFloat(long0 + 20L))
                ._m21(UNSAFE.getFloat(long0 + 24L))
                ._m31(UNSAFE.getFloat(long0 + 28L))
                ._m02(UNSAFE.getFloat(long0 + 32L))
                ._m12(UNSAFE.getFloat(long0 + 36L))
                ._m22(UNSAFE.getFloat(long0 + 40L))
                ._m32(UNSAFE.getFloat(long0 + 44L))
                ._m03(UNSAFE.getFloat(long0 + 48L))
                ._m13(UNSAFE.getFloat(long0 + 52L))
                ._m23(UNSAFE.getFloat(long0 + 56L))
                ._m33(UNSAFE.getFloat(long0 + 60L));
        }

        public static void get(Matrix4x3f matrix4x3f, long long0) {
            for (int int0 = 0; int0 < 6; int0++) {
                UNSAFE.putLong(matrix4x3f, Matrix4x3f_m00 + (int0 << 3), UNSAFE.getLong(long0 + (int0 << 3)));
            }
        }

        public static void get(Matrix4d matrix4d, long long0) {
            Unsafe unsafe = UNSAFE;
            matrix4d._m00(unsafe.getDouble(null, long0))
                ._m01(unsafe.getDouble(null, long0 + 8L))
                ._m02(unsafe.getDouble(null, long0 + 16L))
                ._m03(unsafe.getDouble(null, long0 + 24L))
                ._m10(unsafe.getDouble(null, long0 + 32L))
                ._m11(unsafe.getDouble(null, long0 + 40L))
                ._m12(unsafe.getDouble(null, long0 + 48L))
                ._m13(unsafe.getDouble(null, long0 + 56L))
                ._m20(unsafe.getDouble(null, long0 + 64L))
                ._m21(unsafe.getDouble(null, long0 + 72L))
                ._m22(unsafe.getDouble(null, long0 + 80L))
                ._m23(unsafe.getDouble(null, long0 + 88L))
                ._m30(unsafe.getDouble(null, long0 + 96L))
                ._m31(unsafe.getDouble(null, long0 + 104L))
                ._m32(unsafe.getDouble(null, long0 + 112L))
                ._m33(unsafe.getDouble(null, long0 + 120L));
        }

        public static void get(Matrix4x3d matrix4x3d, long long0) {
            Unsafe unsafe = UNSAFE;
            matrix4x3d._m00(unsafe.getDouble(null, long0))
                ._m01(unsafe.getDouble(null, long0 + 8L))
                ._m02(unsafe.getDouble(null, long0 + 16L))
                ._m10(unsafe.getDouble(null, long0 + 24L))
                ._m11(unsafe.getDouble(null, long0 + 32L))
                ._m12(unsafe.getDouble(null, long0 + 40L))
                ._m20(unsafe.getDouble(null, long0 + 48L))
                ._m21(unsafe.getDouble(null, long0 + 56L))
                ._m22(unsafe.getDouble(null, long0 + 64L))
                ._m30(unsafe.getDouble(null, long0 + 72L))
                ._m31(unsafe.getDouble(null, long0 + 80L))
                ._m32(unsafe.getDouble(null, long0 + 88L));
        }

        public static void getf(Matrix4d matrix4d, long long0) {
            Unsafe unsafe = UNSAFE;
            matrix4d._m00(unsafe.getFloat(null, long0))
                ._m01(unsafe.getFloat(null, long0 + 4L))
                ._m02(unsafe.getFloat(null, long0 + 8L))
                ._m03(unsafe.getFloat(null, long0 + 12L))
                ._m10(unsafe.getFloat(null, long0 + 16L))
                ._m11(unsafe.getFloat(null, long0 + 20L))
                ._m12(unsafe.getFloat(null, long0 + 24L))
                ._m13(unsafe.getFloat(null, long0 + 28L))
                ._m20(unsafe.getFloat(null, long0 + 32L))
                ._m21(unsafe.getFloat(null, long0 + 36L))
                ._m22(unsafe.getFloat(null, long0 + 40L))
                ._m23(unsafe.getFloat(null, long0 + 44L))
                ._m30(unsafe.getFloat(null, long0 + 48L))
                ._m31(unsafe.getFloat(null, long0 + 52L))
                ._m32(unsafe.getFloat(null, long0 + 56L))
                ._m33(unsafe.getFloat(null, long0 + 60L));
        }

        public static void getf(Matrix4x3d matrix4x3d, long long0) {
            Unsafe unsafe = UNSAFE;
            matrix4x3d._m00(unsafe.getFloat(null, long0))
                ._m01(unsafe.getFloat(null, long0 + 4L))
                ._m02(unsafe.getFloat(null, long0 + 8L))
                ._m10(unsafe.getFloat(null, long0 + 12L))
                ._m11(unsafe.getFloat(null, long0 + 16L))
                ._m12(unsafe.getFloat(null, long0 + 20L))
                ._m20(unsafe.getFloat(null, long0 + 24L))
                ._m21(unsafe.getFloat(null, long0 + 28L))
                ._m22(unsafe.getFloat(null, long0 + 32L))
                ._m30(unsafe.getFloat(null, long0 + 36L))
                ._m31(unsafe.getFloat(null, long0 + 40L))
                ._m32(unsafe.getFloat(null, long0 + 44L));
        }

        public static void get(Matrix3f matrix3f, long long0) {
            for (int int0 = 0; int0 < 4; int0++) {
                UNSAFE.putLong(matrix3f, Matrix3f_m00 + (int0 << 3), UNSAFE.getLong(null, long0 + (int0 << 3)));
            }

            matrix3f._m22(UNSAFE.getFloat(null, long0 + 32L));
        }

        public static void get(Matrix3d matrix3d, long long0) {
            Unsafe unsafe = UNSAFE;
            matrix3d._m00(unsafe.getDouble(null, long0))
                ._m01(unsafe.getDouble(null, long0 + 8L))
                ._m02(unsafe.getDouble(null, long0 + 16L))
                ._m10(unsafe.getDouble(null, long0 + 24L))
                ._m11(unsafe.getDouble(null, long0 + 32L))
                ._m12(unsafe.getDouble(null, long0 + 40L))
                ._m20(unsafe.getDouble(null, long0 + 48L))
                ._m21(unsafe.getDouble(null, long0 + 56L))
                ._m22(unsafe.getDouble(null, long0 + 64L));
        }

        public static void get(Matrix3x2f matrix3x2f, long long0) {
            for (int int0 = 0; int0 < 3; int0++) {
                UNSAFE.putLong(matrix3x2f, Matrix3x2f_m00 + (int0 << 3), UNSAFE.getLong(null, long0 + (int0 << 3)));
            }
        }

        public static void get(Matrix3x2d matrix3x2d, long long0) {
            Unsafe unsafe = UNSAFE;
            matrix3x2d._m00(unsafe.getDouble(null, long0))
                ._m01(unsafe.getDouble(null, long0 + 8L))
                ._m10(unsafe.getDouble(null, long0 + 16L))
                ._m11(unsafe.getDouble(null, long0 + 24L))
                ._m20(unsafe.getDouble(null, long0 + 32L))
                ._m21(unsafe.getDouble(null, long0 + 40L));
        }

        public static void getf(Matrix3d matrix3d, long long0) {
            Unsafe unsafe = UNSAFE;
            matrix3d._m00(unsafe.getFloat(null, long0))
                ._m01(unsafe.getFloat(null, long0 + 4L))
                ._m02(unsafe.getFloat(null, long0 + 8L))
                ._m10(unsafe.getFloat(null, long0 + 12L))
                ._m11(unsafe.getFloat(null, long0 + 16L))
                ._m12(unsafe.getFloat(null, long0 + 20L))
                ._m20(unsafe.getFloat(null, long0 + 24L))
                ._m21(unsafe.getFloat(null, long0 + 28L))
                ._m22(unsafe.getFloat(null, long0 + 32L));
        }

        public static void get(Matrix2f matrix2f, long long0) {
            UNSAFE.putLong(matrix2f, Matrix2f_m00, UNSAFE.getLong(null, long0));
            UNSAFE.putLong(matrix2f, Matrix2f_m00 + 8L, UNSAFE.getLong(null, long0 + 8L));
        }

        public static void get(Matrix2d matrix2d, long long0) {
            matrix2d._m00(UNSAFE.getDouble(null, long0))
                ._m01(UNSAFE.getDouble(null, long0 + 8L))
                ._m10(UNSAFE.getDouble(null, long0 + 16L))
                ._m11(UNSAFE.getDouble(null, long0 + 24L));
        }

        public static void getf(Matrix2d matrix2d, long long0) {
            matrix2d._m00(UNSAFE.getFloat(null, long0))
                ._m01(UNSAFE.getFloat(null, long0 + 4L))
                ._m10(UNSAFE.getFloat(null, long0 + 8L))
                ._m11(UNSAFE.getFloat(null, long0 + 12L));
        }

        public static void get(Vector4d vector4d, long long0) {
            vector4d.x = UNSAFE.getDouble(null, long0);
            vector4d.y = UNSAFE.getDouble(null, long0 + 8L);
            vector4d.z = UNSAFE.getDouble(null, long0 + 16L);
            vector4d.w = UNSAFE.getDouble(null, long0 + 24L);
        }

        public static void get(Vector4f vector4f, long long0) {
            vector4f.x = UNSAFE.getFloat(null, long0);
            vector4f.y = UNSAFE.getFloat(null, long0 + 4L);
            vector4f.z = UNSAFE.getFloat(null, long0 + 8L);
            vector4f.w = UNSAFE.getFloat(null, long0 + 12L);
        }

        public static void get(Vector4i vector4i, long long0) {
            vector4i.x = UNSAFE.getInt(null, long0);
            vector4i.y = UNSAFE.getInt(null, long0 + 4L);
            vector4i.z = UNSAFE.getInt(null, long0 + 8L);
            vector4i.w = UNSAFE.getInt(null, long0 + 12L);
        }

        public static void get(Vector3f vector3f, long long0) {
            vector3f.x = UNSAFE.getFloat(null, long0);
            vector3f.y = UNSAFE.getFloat(null, long0 + 4L);
            vector3f.z = UNSAFE.getFloat(null, long0 + 8L);
        }

        public static void get(Vector3d vector3d, long long0) {
            vector3d.x = UNSAFE.getDouble(null, long0);
            vector3d.y = UNSAFE.getDouble(null, long0 + 8L);
            vector3d.z = UNSAFE.getDouble(null, long0 + 16L);
        }

        public static void get(Vector3i vector3i, long long0) {
            vector3i.x = UNSAFE.getInt(null, long0);
            vector3i.y = UNSAFE.getInt(null, long0 + 4L);
            vector3i.z = UNSAFE.getInt(null, long0 + 8L);
        }

        public static void get(Vector2f vector2f, long long0) {
            vector2f.x = UNSAFE.getFloat(null, long0);
            vector2f.y = UNSAFE.getFloat(null, long0 + 4L);
        }

        public static void get(Vector2d vector2d, long long0) {
            vector2d.x = UNSAFE.getDouble(null, long0);
            vector2d.y = UNSAFE.getDouble(null, long0 + 8L);
        }

        public static void get(Vector2i vector2i, long long0) {
            vector2i.x = UNSAFE.getInt(null, long0);
            vector2i.y = UNSAFE.getInt(null, long0 + 4L);
        }

        public static void putMatrix3f(Quaternionf quaternionf, long long0) {
            float float0 = quaternionf.x + quaternionf.x;
            float float1 = quaternionf.y + quaternionf.y;
            float float2 = quaternionf.z + quaternionf.z;
            float float3 = float0 * quaternionf.x;
            float float4 = float1 * quaternionf.y;
            float float5 = float2 * quaternionf.z;
            float float6 = float0 * quaternionf.y;
            float float7 = float0 * quaternionf.z;
            float float8 = float0 * quaternionf.w;
            float float9 = float1 * quaternionf.z;
            float float10 = float1 * quaternionf.w;
            float float11 = float2 * quaternionf.w;
            Unsafe unsafe = UNSAFE;
            unsafe.putFloat(null, long0, 1.0F - float4 - float5);
            unsafe.putFloat(null, long0 + 4L, float6 + float11);
            unsafe.putFloat(null, long0 + 8L, float7 - float10);
            unsafe.putFloat(null, long0 + 12L, float6 - float11);
            unsafe.putFloat(null, long0 + 16L, 1.0F - float5 - float3);
            unsafe.putFloat(null, long0 + 20L, float9 + float8);
            unsafe.putFloat(null, long0 + 24L, float7 + float10);
            unsafe.putFloat(null, long0 + 28L, float9 - float8);
            unsafe.putFloat(null, long0 + 32L, 1.0F - float4 - float3);
        }

        public static void putMatrix4f(Quaternionf quaternionf, long long0) {
            float float0 = quaternionf.x + quaternionf.x;
            float float1 = quaternionf.y + quaternionf.y;
            float float2 = quaternionf.z + quaternionf.z;
            float float3 = float0 * quaternionf.x;
            float float4 = float1 * quaternionf.y;
            float float5 = float2 * quaternionf.z;
            float float6 = float0 * quaternionf.y;
            float float7 = float0 * quaternionf.z;
            float float8 = float0 * quaternionf.w;
            float float9 = float1 * quaternionf.z;
            float float10 = float1 * quaternionf.w;
            float float11 = float2 * quaternionf.w;
            Unsafe unsafe = UNSAFE;
            unsafe.putFloat(null, long0, 1.0F - float4 - float5);
            unsafe.putFloat(null, long0 + 4L, float6 + float11);
            unsafe.putLong(null, long0 + 8L, Float.floatToRawIntBits(float7 - float10) & 4294967295L);
            unsafe.putFloat(null, long0 + 16L, float6 - float11);
            unsafe.putFloat(null, long0 + 20L, 1.0F - float5 - float3);
            unsafe.putLong(null, long0 + 24L, Float.floatToRawIntBits(float9 + float8) & 4294967295L);
            unsafe.putFloat(null, long0 + 32L, float7 + float10);
            unsafe.putFloat(null, long0 + 36L, float9 - float8);
            unsafe.putLong(null, long0 + 40L, Float.floatToRawIntBits(1.0F - float4 - float3) & 4294967295L);
            unsafe.putLong(null, long0 + 48L, 0L);
            unsafe.putLong(null, long0 + 56L, 4575657221408423936L);
        }

        public static void putMatrix4x3f(Quaternionf quaternionf, long long0) {
            float float0 = quaternionf.x + quaternionf.x;
            float float1 = quaternionf.y + quaternionf.y;
            float float2 = quaternionf.z + quaternionf.z;
            float float3 = float0 * quaternionf.x;
            float float4 = float1 * quaternionf.y;
            float float5 = float2 * quaternionf.z;
            float float6 = float0 * quaternionf.y;
            float float7 = float0 * quaternionf.z;
            float float8 = float0 * quaternionf.w;
            float float9 = float1 * quaternionf.z;
            float float10 = float1 * quaternionf.w;
            float float11 = float2 * quaternionf.w;
            Unsafe unsafe = UNSAFE;
            unsafe.putFloat(null, long0, 1.0F - float4 - float5);
            unsafe.putFloat(null, long0 + 4L, float6 + float11);
            unsafe.putFloat(null, long0 + 8L, float7 - float10);
            unsafe.putFloat(null, long0 + 12L, float6 - float11);
            unsafe.putFloat(null, long0 + 16L, 1.0F - float5 - float3);
            unsafe.putFloat(null, long0 + 20L, float9 + float8);
            unsafe.putFloat(null, long0 + 24L, float7 + float10);
            unsafe.putFloat(null, long0 + 28L, float9 - float8);
            unsafe.putFloat(null, long0 + 32L, 1.0F - float4 - float3);
            unsafe.putLong(null, long0 + 36L, 0L);
            unsafe.putFloat(null, long0 + 44L, 0.0F);
        }

        private static void throwNoDirectBufferException() {
            throw new IllegalArgumentException("Must use a direct buffer");
        }

        @Override
        public void putMatrix3f(Quaternionf quaternionf, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 36);
            }

            putMatrix3f(quaternionf, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void putMatrix3f(Quaternionf quaternionf, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 9);
            }

            putMatrix3f(quaternionf, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        private static void checkPut(int int2, boolean boolean0, int int1, int int0) {
            if (!boolean0) {
                throwNoDirectBufferException();
            }

            if (int1 - int2 < int0) {
                throw new BufferOverflowException();
            }
        }

        @Override
        public void putMatrix4f(Quaternionf quaternionf, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 64);
            }

            putMatrix4f(quaternionf, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void putMatrix4f(Quaternionf quaternionf, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 16);
            }

            putMatrix4f(quaternionf, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void putMatrix4x3f(Quaternionf quaternionf, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
            }

            putMatrix4x3f(quaternionf, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void putMatrix4x3f(Quaternionf quaternionf, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
            }

            putMatrix4x3f(quaternionf, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put(Matrix4f matrix4f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 16);
            }

            put(matrix4f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put(Matrix4f matrix4f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 64);
            }

            put(matrix4f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put4x3(Matrix4f matrix4f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
            }

            put4x3(matrix4f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put4x3(Matrix4f matrix4f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
            }

            put4x3(matrix4f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put3x4(Matrix4f matrix4f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
            }

            put3x4(matrix4f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put3x4(Matrix4f matrix4f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
            }

            put3x4(matrix4f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put(Matrix4x3f matrix4x3f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
            }

            put(matrix4x3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put(Matrix4x3f matrix4x3f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
            }

            put(matrix4x3f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put4x4(Matrix4x3f matrix4x3f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 16);
            }

            put4x4(matrix4x3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put4x4(Matrix4x3f matrix4x3f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 64);
            }

            put4x4(matrix4x3f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put3x4(Matrix4x3f matrix4x3f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
            }

            put3x4(matrix4x3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put3x4(Matrix4x3f matrix4x3f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
            }

            put3x4(matrix4x3f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put4x4(Matrix4x3d matrix4x3d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 16);
            }

            put4x4(matrix4x3d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void put4x4(Matrix4x3d matrix4x3d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 128);
            }

            put4x4(matrix4x3d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put4x4(Matrix3x2f matrix3x2f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 16);
            }

            put4x4(matrix3x2f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put4x4(Matrix3x2f matrix3x2f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 64);
            }

            put4x4(matrix3x2f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put4x4(Matrix3x2d matrix3x2d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 16);
            }

            put4x4(matrix3x2d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void put4x4(Matrix3x2d matrix3x2d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 128);
            }

            put4x4(matrix3x2d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put3x3(Matrix3x2f matrix3x2f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 9);
            }

            put3x3(matrix3x2f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put3x3(Matrix3x2f matrix3x2f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 36);
            }

            put3x3(matrix3x2f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put3x3(Matrix3x2d matrix3x2d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 9);
            }

            put3x3(matrix3x2d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void put3x3(Matrix3x2d matrix3x2d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 72);
            }

            put3x3(matrix3x2d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void putTransposed(Matrix4f matrix4f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 16);
            }

            putTransposed(matrix4f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void putTransposed(Matrix4f matrix4f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 64);
            }

            putTransposed(matrix4f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put4x3Transposed(Matrix4f matrix4f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
            }

            put4x3Transposed(matrix4f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put4x3Transposed(Matrix4f matrix4f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
            }

            put4x3Transposed(matrix4f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void putTransposed(Matrix4x3f matrix4x3f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
            }

            putTransposed(matrix4x3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void putTransposed(Matrix4x3f matrix4x3f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
            }

            putTransposed(matrix4x3f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void putTransposed(Matrix3f matrix3f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 9);
            }

            putTransposed(matrix3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void putTransposed(Matrix3f matrix3f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 36);
            }

            putTransposed(matrix3f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void putTransposed(Matrix2f matrix2f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 4);
            }

            putTransposed(matrix2f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void putTransposed(Matrix2f matrix2f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
            }

            putTransposed(matrix2f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put(Matrix4d matrix4d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 16);
            }

            put(matrix4d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void put(Matrix4d matrix4d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 128);
            }

            put(matrix4d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put(Matrix4x3d matrix4x3d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 12);
            }

            put(matrix4x3d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void put(Matrix4x3d matrix4x3d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 96);
            }

            put(matrix4x3d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void putf(Matrix4d matrix4d, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 16);
            }

            putf(matrix4d, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void putf(Matrix4d matrix4d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 64);
            }

            putf(matrix4d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void putf(Matrix4x3d matrix4x3d, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
            }

            putf(matrix4x3d, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void putf(Matrix4x3d matrix4x3d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
            }

            putf(matrix4x3d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void putTransposed(Matrix4d matrix4d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 16);
            }

            putTransposed(matrix4d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void putTransposed(Matrix4d matrix4d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 128);
            }

            putTransposed(matrix4d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put4x3Transposed(Matrix4d matrix4d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 12);
            }

            put4x3Transposed(matrix4d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void put4x3Transposed(Matrix4d matrix4d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 96);
            }

            put4x3Transposed(matrix4d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void putTransposed(Matrix4x3d matrix4x3d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 12);
            }

            putTransposed(matrix4x3d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void putTransposed(Matrix4x3d matrix4x3d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 96);
            }

            putTransposed(matrix4x3d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void putTransposed(Matrix2d matrix2d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 4);
            }

            putTransposed(matrix2d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void putTransposed(Matrix2d matrix2d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 32);
            }

            putTransposed(matrix2d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void putfTransposed(Matrix4d matrix4d, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 16);
            }

            putfTransposed(matrix4d, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void putfTransposed(Matrix4d matrix4d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 64);
            }

            putfTransposed(matrix4d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void putfTransposed(Matrix4x3d matrix4x3d, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
            }

            putfTransposed(matrix4x3d, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void putfTransposed(Matrix4x3d matrix4x3d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
            }

            putfTransposed(matrix4x3d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void putfTransposed(Matrix2d matrix2d, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 4);
            }

            putfTransposed(matrix2d, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void putfTransposed(Matrix2d matrix2d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
            }

            putfTransposed(matrix2d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put(Matrix3f matrix3f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 9);
            }

            put(matrix3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put(Matrix3f matrix3f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 36);
            }

            put(matrix3f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put3x4(Matrix3f matrix3f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
            }

            put3x4(matrix3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put3x4(Matrix3f matrix3f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
            }

            put3x4(matrix3f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put(Matrix3d matrix3d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 9);
            }

            put(matrix3d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void put(Matrix3d matrix3d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 72);
            }

            put(matrix3d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put(Matrix3x2f matrix3x2f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 6);
            }

            put(matrix3x2f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put(Matrix3x2f matrix3x2f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 24);
            }

            put(matrix3x2f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put(Matrix3x2d matrix3x2d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 6);
            }

            put(matrix3x2d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void put(Matrix3x2d matrix3x2d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
            }

            put(matrix3x2d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void putf(Matrix3d matrix3d, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 9);
            }

            putf(matrix3d, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void putf(Matrix3d matrix3d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 36);
            }

            putf(matrix3d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put(Matrix2f matrix2f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 4);
            }

            put(matrix2f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put(Matrix2f matrix2f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
            }

            put(matrix2f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put(Matrix2d matrix2d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 4);
            }

            put(matrix2d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void put(Matrix2d matrix2d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
            }

            put(matrix2d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void putf(Matrix2d matrix2d, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 4);
            }

            putf(matrix2d, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void putf(Matrix2d matrix2d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
            }

            putf(matrix2d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put(Vector4d vector4d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 4);
            }

            put(vector4d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void put(Vector4d vector4d, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 4);
            }

            putf(vector4d, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put(Vector4d vector4d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 32);
            }

            put(vector4d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void putf(Vector4d vector4d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
            }

            putf(vector4d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put(Vector4f vector4f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 4);
            }

            put(vector4f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put(Vector4f vector4f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
            }

            put(vector4f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put(Vector4i vector4i, int int0, IntBuffer intBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, intBuffer.isDirect(), intBuffer.capacity(), 4);
            }

            put(vector4i, UNSAFE.getLong(intBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put(Vector4i vector4i, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
            }

            put(vector4i, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put(Vector3f vector3f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 3);
            }

            put(vector3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put(Vector3f vector3f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 12);
            }

            put(vector3f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put(Vector3d vector3d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 3);
            }

            put(vector3d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void put(Vector3d vector3d, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 3);
            }

            putf(vector3d, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put(Vector3d vector3d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 24);
            }

            put(vector3d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void putf(Vector3d vector3d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 12);
            }

            putf(vector3d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put(Vector3i vector3i, int int0, IntBuffer intBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, intBuffer.isDirect(), intBuffer.capacity(), 3);
            }

            put(vector3i, UNSAFE.getLong(intBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put(Vector3i vector3i, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 12);
            }

            put(vector3i, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put(Vector2f vector2f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 2);
            }

            put(vector2f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put(Vector2f vector2f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 8);
            }

            put(vector2f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put(Vector2d vector2d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 2);
            }

            put(vector2d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void put(Vector2d vector2d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
            }

            put(vector2d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void put(Vector2i vector2i, int int0, IntBuffer intBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, intBuffer.isDirect(), intBuffer.capacity(), 2);
            }

            put(vector2i, UNSAFE.getLong(intBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void put(Vector2i vector2i, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkPut(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 8);
            }

            put(vector2i, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void get(Matrix4f matrix4f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 16);
            }

            get(matrix4f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void get(Matrix4f matrix4f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 64);
            }

            get(matrix4f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public float get(Matrix4f matrix4f, int int1, int int0) {
            return UNSAFE.getFloat(matrix4f, Matrix4f_m00 + (int1 << 4) + (int0 << 2));
        }

        @Override
        public Matrix4f set(Matrix4f matrix4f, int int1, int int0, float float0) {
            UNSAFE.putFloat(matrix4f, Matrix4f_m00 + (int1 << 4) + (int0 << 2), float0);
            return matrix4f;
        }

        @Override
        public double get(Matrix4d matrix4d, int int1, int int0) {
            return UNSAFE.getDouble(matrix4d, Matrix4d_m00 + (int1 << 5) + (int0 << 3));
        }

        @Override
        public Matrix4d set(Matrix4d matrix4d, int int1, int int0, double double0) {
            UNSAFE.putDouble(matrix4d, Matrix4d_m00 + (int1 << 5) + (int0 << 3), double0);
            return matrix4d;
        }

        @Override
        public float get(Matrix3f matrix3f, int int1, int int0) {
            return UNSAFE.getFloat(matrix3f, Matrix3f_m00 + int1 * 12 + (int0 << 2));
        }

        @Override
        public Matrix3f set(Matrix3f matrix3f, int int1, int int0, float float0) {
            UNSAFE.putFloat(matrix3f, Matrix3f_m00 + int1 * 12 + (int0 << 2), float0);
            return matrix3f;
        }

        @Override
        public double get(Matrix3d matrix3d, int int1, int int0) {
            return UNSAFE.getDouble(matrix3d, Matrix3d_m00 + int1 * 24 + (int0 << 3));
        }

        @Override
        public Matrix3d set(Matrix3d matrix3d, int int1, int int0, double double0) {
            UNSAFE.putDouble(matrix3d, Matrix3d_m00 + int1 * 24 + (int0 << 3), double0);
            return matrix3d;
        }

        @Override
        public void get(Matrix4x3f matrix4x3f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
            }

            get(matrix4x3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void get(Matrix4x3f matrix4x3f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
            }

            get(matrix4x3f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void get(Matrix4d matrix4d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 16);
            }

            get(matrix4d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void get(Matrix4d matrix4d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 128);
            }

            get(matrix4d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void get(Matrix4x3d matrix4x3d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 12);
            }

            get(matrix4x3d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void get(Matrix4x3d matrix4x3d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 96);
            }

            get(matrix4x3d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void getf(Matrix4d matrix4d, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 16);
            }

            getf(matrix4d, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void getf(Matrix4d matrix4d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 64);
            }

            getf(matrix4d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void getf(Matrix4x3d matrix4x3d, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
            }

            getf(matrix4x3d, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        private static void checkGet(int int2, boolean boolean0, int int1, int int0) {
            if (!boolean0) {
                throwNoDirectBufferException();
            }

            if (int1 - int2 < int0) {
                throw new BufferUnderflowException();
            }
        }

        @Override
        public void getf(Matrix4x3d matrix4x3d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
            }

            getf(matrix4x3d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void get(Matrix3f matrix3f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 9);
            }

            get(matrix3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void get(Matrix3f matrix3f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 36);
            }

            get(matrix3f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void get(Matrix3d matrix3d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 9);
            }

            get(matrix3d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void get(Matrix3d matrix3d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 72);
            }

            get(matrix3d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void get(Matrix3x2f matrix3x2f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 6);
            }

            get(matrix3x2f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void get(Matrix3x2f matrix3x2f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 24);
            }

            get(matrix3x2f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void get(Matrix3x2d matrix3x2d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 6);
            }

            get(matrix3x2d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void get(Matrix3x2d matrix3x2d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
            }

            get(matrix3x2d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void getf(Matrix3d matrix3d, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 9);
            }

            getf(matrix3d, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void getf(Matrix3d matrix3d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 36);
            }

            getf(matrix3d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void get(Matrix2f matrix2f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 4);
            }

            get(matrix2f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void get(Matrix2f matrix2f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
            }

            get(matrix2f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void get(Matrix2d matrix2d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 4);
            }

            get(matrix2d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void get(Matrix2d matrix2d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 32);
            }

            get(matrix2d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void getf(Matrix2d matrix2d, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 4);
            }

            getf(matrix2d, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void getf(Matrix2d matrix2d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
            }

            getf(matrix2d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void get(Vector4d vector4d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 4);
            }

            get(vector4d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void get(Vector4d vector4d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 32);
            }

            get(vector4d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void get(Vector4f vector4f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 4);
            }

            get(vector4f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void get(Vector4f vector4f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
            }

            get(vector4f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void get(Vector4i vector4i, int int0, IntBuffer intBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, intBuffer.isDirect(), intBuffer.capacity(), 4);
            }

            get(vector4i, UNSAFE.getLong(intBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void get(Vector4i vector4i, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
            }

            get(vector4i, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void get(Vector3f vector3f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 3);
            }

            get(vector3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void get(Vector3f vector3f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 12);
            }

            get(vector3f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void get(Vector3d vector3d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 3);
            }

            get(vector3d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void get(Vector3d vector3d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 24);
            }

            get(vector3d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void get(Vector3i vector3i, int int0, IntBuffer intBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, intBuffer.isDirect(), intBuffer.capacity(), 3);
            }

            get(vector3i, UNSAFE.getLong(intBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void get(Vector3i vector3i, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 12);
            }

            get(vector3i, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void get(Vector2f vector2f, int int0, FloatBuffer floatBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, floatBuffer.isDirect(), floatBuffer.capacity(), 2);
            }

            get(vector2f, UNSAFE.getLong(floatBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void get(Vector2f vector2f, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 8);
            }

            get(vector2f, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void get(Vector2d vector2d, int int0, DoubleBuffer doubleBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, doubleBuffer.isDirect(), doubleBuffer.capacity(), 2);
            }

            get(vector2d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (int0 << 3));
        }

        @Override
        public void get(Vector2d vector2d, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
            }

            get(vector2d, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        @Override
        public void get(Vector2i vector2i, int int0, IntBuffer intBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, intBuffer.isDirect(), intBuffer.capacity(), 2);
            }

            get(vector2i, UNSAFE.getLong(intBuffer, ADDRESS) + (int0 << 2));
        }

        @Override
        public void get(Vector2i vector2i, int int0, ByteBuffer byteBuffer) {
            if (Options.DEBUG) {
                checkGet(int0, byteBuffer.isDirect(), byteBuffer.capacity(), 8);
            }

            get(vector2i, UNSAFE.getLong(byteBuffer, ADDRESS) + int0);
        }

        static {
            try {
                ADDRESS = findBufferAddress();
                Matrix4f_m00 = checkMatrix4f();
                Matrix4d_m00 = checkMatrix4d();
                Matrix4x3f_m00 = checkMatrix4x3f();
                Matrix3f_m00 = checkMatrix3f();
                Matrix3d_m00 = checkMatrix3d();
                Matrix3x2f_m00 = checkMatrix3x2f();
                Matrix2f_m00 = checkMatrix2f();
                Vector4f_x = checkVector4f();
                Vector4i_x = checkVector4i();
                Vector3f_x = checkVector3f();
                Vector3i_x = checkVector3i();
                Vector2f_x = checkVector2f();
                Vector2i_x = checkVector2i();
                Quaternionf_x = checkQuaternionf();
                floatArrayOffset = UNSAFE.arrayBaseOffset(float[].class);
                Unsafe.class.getDeclaredMethod("getLong", Object.class, long.class);
                Unsafe.class.getDeclaredMethod("putLong", Object.class, long.class, long.class);
            } catch (NoSuchFieldException noSuchFieldException) {
                throw new UnsupportedOperationException(noSuchFieldException);
            } catch (NoSuchMethodException noSuchMethodException) {
                throw new UnsupportedOperationException(noSuchMethodException);
            }
        }
    }
}
