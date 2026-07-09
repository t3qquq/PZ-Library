// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import zombie.core.Color;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.model.VertexPositionNormalTangentTextureSkin;
import zombie.debug.DebugLog;
import zombie.popman.ObjectPool;
import zombie.util.list.PZArrayUtil;

public final class HelperFunctions {
    private static final Vector3f s_zero3 = new Vector3f(0.0F, 0.0F, 0.0F);
    private static final Quaternion s_identityQ = new Quaternion();
    private static final Stack<Matrix4f> MatrixStack = new Stack<>();
    private static final AtomicBoolean MatrixLock = new AtomicBoolean(false);
    private static final ObjectPool<Vector3f> VectorPool = new ObjectPool<>(Vector3f::new);

    public static int ToRgba(Color color) {
        return (int)color.a << 24 | (int)color.b << 16 | (int)color.g << 8 | (int)color.r;
    }

    public static void returnMatrix(Matrix4f matrix4f) {
        while (!MatrixLock.compareAndSet(false, true)) {
            Thread.onSpinWait();
        }

        assert !MatrixStack.contains(matrix4f);

        MatrixStack.push(matrix4f);
        MatrixLock.set(false);
    }

    public static Matrix4f getMatrix() {
        Matrix4f matrix4f = null;

        while (!MatrixLock.compareAndSet(false, true)) {
            Thread.onSpinWait();
        }

        if (MatrixStack.isEmpty()) {
            matrix4f = new Matrix4f();
        } else {
            matrix4f = MatrixStack.pop();
        }

        MatrixLock.set(false);
        return matrix4f;
    }

    public static Matrix4f getMatrix(Matrix4f matrix4f1) {
        Matrix4f matrix4f0 = getMatrix();
        matrix4f0.load(matrix4f1);
        return matrix4f0;
    }

    public static Vector3f getVector3f() {
        while (!MatrixLock.compareAndSet(false, true)) {
            Thread.onSpinWait();
        }

        Vector3f vector3f = VectorPool.alloc();
        MatrixLock.set(false);
        return vector3f;
    }

    public static void returnVector3f(Vector3f vector3f) {
        while (!MatrixLock.compareAndSet(false, true)) {
            Thread.onSpinWait();
        }

        VectorPool.release(vector3f);
        MatrixLock.set(false);
    }

    public static Matrix4f CreateFromQuaternion(Quaternion quaternion) {
        Matrix4f matrix4f = getMatrix();
        CreateFromQuaternion(quaternion, matrix4f);
        return matrix4f;
    }

    public static Matrix4f CreateFromQuaternion(Quaternion quaternion, Matrix4f matrix4f) {
        matrix4f.setIdentity();
        float float0 = quaternion.lengthSquared();
        if (float0 > 0.0F && float0 < 0.99999F || float0 > 1.00001F) {
            float float1 = (float)Math.sqrt(float0);
            float float2 = 1.0F / float1;
            quaternion.scale(float2);
        }

        float float3 = quaternion.x * quaternion.x;
        float float4 = quaternion.x * quaternion.y;
        float float5 = quaternion.x * quaternion.z;
        float float6 = quaternion.x * quaternion.w;
        float float7 = quaternion.y * quaternion.y;
        float float8 = quaternion.y * quaternion.z;
        float float9 = quaternion.y * quaternion.w;
        float float10 = quaternion.z * quaternion.z;
        float float11 = quaternion.z * quaternion.w;
        matrix4f.m00 = 1.0F - 2.0F * (float7 + float10);
        matrix4f.m10 = 2.0F * (float4 - float11);
        matrix4f.m20 = 2.0F * (float5 + float9);
        matrix4f.m30 = 0.0F;
        matrix4f.m01 = 2.0F * (float4 + float11);
        matrix4f.m11 = 1.0F - 2.0F * (float3 + float10);
        matrix4f.m21 = 2.0F * (float8 - float6) * 1.0F;
        matrix4f.m31 = 0.0F;
        matrix4f.m02 = 2.0F * (float5 - float9);
        matrix4f.m12 = 2.0F * (float8 + float6);
        matrix4f.m22 = 1.0F - 2.0F * (float3 + float7);
        matrix4f.m32 = 0.0F;
        matrix4f.m03 = 0.0F;
        matrix4f.m13 = 0.0F;
        matrix4f.m23 = 0.0F;
        matrix4f.m33 = 1.0F;
        matrix4f.m30 = 0.0F;
        matrix4f.m31 = 0.0F;
        matrix4f.m32 = 0.0F;
        matrix4f.transpose();
        return matrix4f;
    }

    public static Matrix4f CreateFromQuaternionPositionScale(Vector3f vector3f0, Quaternion quaternion, Vector3f vector3f1, Matrix4f matrix4f3) {
        Matrix4f matrix4f0 = getMatrix();
        Matrix4f matrix4f1 = getMatrix();
        Matrix4f matrix4f2 = getMatrix();
        CreateFromQuaternionPositionScale(vector3f0, quaternion, vector3f1, matrix4f3, matrix4f1, matrix4f2, matrix4f0);
        returnMatrix(matrix4f0);
        returnMatrix(matrix4f1);
        returnMatrix(matrix4f2);
        return matrix4f3;
    }

    public static void CreateFromQuaternionPositionScale(
        Vector3f vector3f0, Quaternion quaternion, Vector3f vector3f1, HelperFunctions.TransformResult_QPS transformResult_QPS
    ) {
        CreateFromQuaternionPositionScale(
            vector3f0, quaternion, vector3f1, transformResult_QPS.result, transformResult_QPS.trans, transformResult_QPS.rot, transformResult_QPS.scl
        );
    }

    private static void CreateFromQuaternionPositionScale(
        Vector3f vector3f1, Quaternion quaternion, Vector3f vector3f0, Matrix4f matrix4f3, Matrix4f matrix4f1, Matrix4f matrix4f2, Matrix4f matrix4f0
    ) {
        matrix4f0.setIdentity();
        matrix4f0.scale(vector3f0);
        matrix4f1.setIdentity();
        matrix4f1.translate(vector3f1);
        matrix4f1.transpose();
        CreateFromQuaternion(quaternion, matrix4f2);
        Matrix4f.mul(matrix4f0, matrix4f2, matrix4f2);
        Matrix4f.mul(matrix4f2, matrix4f1, matrix4f3);
    }

    public static void TransformVertices(VertexPositionNormalTangentTextureSkin[] vertexPositionNormalTangentTextureSkins, List<Matrix4f> list) {
        Vector3 vector30 = new Vector3();
        Vector3 vector31 = new Vector3();
        Vector4f vector4f = new Vector4f();

        for (VertexPositionNormalTangentTextureSkin vertexPositionNormalTangentTextureSkin : vertexPositionNormalTangentTextureSkins) {
            vector30.reset();
            vector31.reset();
            Vector3 vector32 = vertexPositionNormalTangentTextureSkin.Position;
            Vector3 vector33 = vertexPositionNormalTangentTextureSkin.Normal;
            ApplyBlendBone(
                vertexPositionNormalTangentTextureSkin.BlendWeights.x,
                (Matrix4f)list.get(vertexPositionNormalTangentTextureSkin.BlendIndices.X),
                vector32,
                vector33,
                vector4f,
                vector30,
                vector31
            );
            ApplyBlendBone(
                vertexPositionNormalTangentTextureSkin.BlendWeights.y,
                (Matrix4f)list.get(vertexPositionNormalTangentTextureSkin.BlendIndices.Y),
                vector32,
                vector33,
                vector4f,
                vector30,
                vector31
            );
            ApplyBlendBone(
                vertexPositionNormalTangentTextureSkin.BlendWeights.z,
                (Matrix4f)list.get(vertexPositionNormalTangentTextureSkin.BlendIndices.Z),
                vector32,
                vector33,
                vector4f,
                vector30,
                vector31
            );
            ApplyBlendBone(
                vertexPositionNormalTangentTextureSkin.BlendWeights.w,
                (Matrix4f)list.get(vertexPositionNormalTangentTextureSkin.BlendIndices.W),
                vector32,
                vector33,
                vector4f,
                vector30,
                vector31
            );
            vector32.set(vector30);
            vector33.set(vector31);
        }
    }

    public static void ApplyBlendBone(float float0, Matrix4f matrix4f, Vector3 vector30, Vector3 vector32, Vector4f var4, Vector3 vector31, Vector3 vector33) {
        if (float0 > 0.0F) {
            float float1 = vector30.x();
            float float2 = vector30.y();
            float float3 = vector30.z();
            float float4 = matrix4f.m00 * float1 + matrix4f.m01 * float2 + matrix4f.m02 * float3 + matrix4f.m03;
            float float5 = matrix4f.m10 * float1 + matrix4f.m11 * float2 + matrix4f.m12 * float3 + matrix4f.m13;
            float float6 = matrix4f.m20 * float1 + matrix4f.m21 * float2 + matrix4f.m22 * float3 + matrix4f.m23;
            vector31.add(float4 * float0, float5 * float0, float6 * float0);
            float1 = vector32.x();
            float2 = vector32.y();
            float3 = vector32.z();
            float4 = matrix4f.m00 * float1 + matrix4f.m01 * float2 + matrix4f.m02 * float3;
            float5 = matrix4f.m10 * float1 + matrix4f.m11 * float2 + matrix4f.m12 * float3;
            float6 = matrix4f.m20 * float1 + matrix4f.m21 * float2 + matrix4f.m22 * float3;
            vector33.add(float4 * float0, float5 * float0, float6 * float0);
        }
    }

    public static Vector3f getPosition(Matrix4f matrix4f, Vector3f vector3f) {
        vector3f.set(matrix4f.m03, matrix4f.m13, matrix4f.m23);
        return vector3f;
    }

    public static void setPosition(Matrix4f matrix4f, Vector3f vector3f) {
        matrix4f.m03 = vector3f.x;
        matrix4f.m13 = vector3f.y;
        matrix4f.m23 = vector3f.z;
    }

    public static Quaternion getRotation(Matrix4f matrix4f, Quaternion quaternion) {
        return Quaternion.setFromMatrix(matrix4f, quaternion);
    }

    public static void transform(Quaternion quaternion, Vector3f vector3f0, Vector3f vector3f1) {
        quaternion.normalise();
        float float0 = quaternion.w;
        float float1 = quaternion.x;
        float float2 = quaternion.y;
        float float3 = quaternion.z;
        float float4 = float0 * float0;
        float float5 = float1 * float1 + float2 * float2 + float3 * float3;
        float float6 = vector3f0.x;
        float float7 = vector3f0.y;
        float float8 = vector3f0.z;
        float float9 = float2 * float8 - float3 * float7;
        float float10 = float3 * float6 - float1 * float8;
        float float11 = float1 * float7 - float2 * float6;
        float float12 = float6 * float1 + float7 * float2 + float8 * float3;
        float float13 = (float4 - float5) * float6 + 2.0F * float0 * float9 + 2.0F * float1 * float12;
        float float14 = (float4 - float5) * float7 + 2.0F * float0 * float10 + 2.0F * float2 * float12;
        float float15 = (float4 - float5) * float8 + 2.0F * float0 * float11 + 2.0F * float3 * float12;
        vector3f1.set(float13, float14, float15);
    }

    private static Vector4f transform(Matrix4f matrix4f, Vector4f vector4f0, Vector4f vector4f1) {
        float float0 = matrix4f.m00 * vector4f0.x + matrix4f.m01 * vector4f0.y + matrix4f.m02 * vector4f0.z + matrix4f.m30 * vector4f0.w;
        float float1 = matrix4f.m10 * vector4f0.x + matrix4f.m11 * vector4f0.y + matrix4f.m12 * vector4f0.z + matrix4f.m31 * vector4f0.w;
        float float2 = matrix4f.m20 * vector4f0.x + matrix4f.m21 * vector4f0.y + matrix4f.m22 * vector4f0.z + matrix4f.m32 * vector4f0.w;
        float float3 = matrix4f.m03 * vector4f0.x + matrix4f.m13 * vector4f0.y + matrix4f.m23 * vector4f0.z + matrix4f.m33 * vector4f0.w;
        vector4f1.x = float0;
        vector4f1.y = float1;
        vector4f1.z = float2;
        vector4f1.w = float3;
        return vector4f1;
    }

    public static float getRotationY(Quaternion quaternion) {
        quaternion.normalise();
        float float0 = quaternion.w;
        float float1 = quaternion.x;
        float float2 = quaternion.y;
        float float3 = quaternion.z;
        float float4 = float0 * float0;
        float float5 = float1 * float1 + float2 * float2 + float3 * float3;
        float float6 = float2 * 0.0F - float3 * 0.0F;
        float float7 = float1 * 0.0F - float2 * 1.0F;
        float float8 = 1.0F * float1 + 0.0F * float2 + 0.0F * float3;
        float float9 = (float4 - float5) * 1.0F + 2.0F * float0 * float6 + 2.0F * float1 * float8;
        float float10 = (float4 - float5) * 0.0F + 2.0F * float0 * float7 + 2.0F * float3 * float8;
        float float11 = (float)Math.atan2(-float10, float9);
        return PZMath.wrap(float11, (float) -Math.PI, (float) Math.PI);
    }

    public static float getRotationZ(Quaternion quaternion) {
        float float0 = quaternion.w;
        float float1 = quaternion.x;
        float float2 = quaternion.y;
        float float3 = quaternion.z;
        float float4 = float0 * float0;
        float float5 = float1 * float1 + float2 * float2 + float3 * float3;
        float float6 = float3 * 1.0F;
        float float7 = 1.0F * float1;
        float float8 = (float4 - float5) * 1.0F + 2.0F * float1 * float7;
        float float9 = 2.0F * float0 * float6 + 2.0F * float2 * float7;
        return (float)Math.atan2(float9, float8);
    }

    public static Vector3f ToEulerAngles(Quaternion quaternion, Vector3f vector3f) {
        double double0 = 2.0 * (quaternion.w * quaternion.x + quaternion.y * quaternion.z);
        double double1 = 1.0 - 2.0 * (quaternion.x * quaternion.x + quaternion.y * quaternion.y);
        vector3f.x = (float)Math.atan2(double0, double1);
        double double2 = 2.0 * (quaternion.w * quaternion.y - quaternion.z * quaternion.x);
        if (Math.abs(double2) >= 1.0) {
            vector3f.y = (float)Math.copySign((float) (Math.PI / 2), double2);
        } else {
            vector3f.y = (float)Math.asin(double2);
        }

        double double3 = 2.0 * (quaternion.w * quaternion.z + quaternion.x * quaternion.y);
        double double4 = 1.0 - 2.0 * (quaternion.y * quaternion.y + quaternion.z * quaternion.z);
        vector3f.z = (float)Math.atan2(double3, double4);
        return vector3f;
    }

    public static Quaternion ToQuaternion(double double7, double double4, double double1, Quaternion quaternion) {
        double double0 = Math.cos(double1 * 0.5);
        double double2 = Math.sin(double1 * 0.5);
        double double3 = Math.cos(double4 * 0.5);
        double double5 = Math.sin(double4 * 0.5);
        double double6 = Math.cos(double7 * 0.5);
        double double8 = Math.sin(double7 * 0.5);
        quaternion.w = (float)(double0 * double3 * double6 + double2 * double5 * double8);
        quaternion.x = (float)(double0 * double3 * double8 - double2 * double5 * double6);
        quaternion.y = (float)(double2 * double3 * double8 + double0 * double5 * double6);
        quaternion.z = (float)(double2 * double3 * double6 - double0 * double5 * double8);
        return quaternion;
    }

    public static Vector3f getZero3() {
        s_zero3.set(0.0F, 0.0F, 0.0F);
        return s_zero3;
    }

    public static Quaternion getIdentityQ() {
        s_identityQ.setIdentity();
        return s_identityQ;
    }

    static {
        HelperFunctions.UnitTests.runAll();
    }

    public static class TransformResult_QPS {
        public final Matrix4f result;
        final Matrix4f trans;
        final Matrix4f rot;
        final Matrix4f scl;

        public TransformResult_QPS() {
            this.result = new Matrix4f();
            this.trans = new Matrix4f();
            this.rot = new Matrix4f();
            this.scl = new Matrix4f();
        }

        public TransformResult_QPS(Matrix4f _result) {
            this.result = _result;
            this.trans = new Matrix4f();
            this.rot = new Matrix4f();
            this.scl = new Matrix4f();
        }
    }

    private static final class UnitTests {
        private static final Runnable[] s_unitTests = new Runnable[0];

        private static void runAll() {
            PZArrayUtil.forEach(s_unitTests, Runnable::run);
        }

        private static final class getRotationMatrix {
            public static void run() {
                DebugLog.UnitTests.println("UnitTest_getRotationMatrix");
                DebugLog.UnitTests.println("q.x, q.y, q.z, q.w, q_out.x, q_out.y, q_out.z, q_out.w");
                Quaternion quaternion0 = new Quaternion();
                Vector4f vector4f = new Vector4f();
                Matrix4f matrix4f = new Matrix4f();
                Quaternion quaternion1 = new Quaternion();
                Quaternion quaternion2 = new Quaternion();

                for (byte byte0 = 0; byte0 < 360; byte0 += 10) {
                    float float0 = PZMath.wrap(byte0, -180.0F, 180.0F);
                    vector4f.set(1.0F, 0.0F, 0.0F, float0 * (float) (Math.PI / 180.0));
                    quaternion0.setFromAxisAngle(vector4f);
                    HelperFunctions.CreateFromQuaternion(quaternion0, matrix4f);
                    HelperFunctions.getRotation(matrix4f, quaternion1);
                    quaternion2.set(-quaternion1.x, -quaternion1.y, -quaternion1.z, -quaternion1.w);
                    boolean boolean0 = PZMath.equal(quaternion0.x, quaternion1.x, 0.01F)
                            && PZMath.equal(quaternion0.y, quaternion1.y, 0.01F)
                            && PZMath.equal(quaternion0.z, quaternion1.z, 0.01F)
                            && PZMath.equal(quaternion0.w, quaternion1.w, 0.01F)
                        || PZMath.equal(quaternion0.x, quaternion2.x, 0.01F)
                            && PZMath.equal(quaternion0.y, quaternion2.y, 0.01F)
                            && PZMath.equal(quaternion0.z, quaternion2.z, 0.01F)
                            && PZMath.equal(quaternion0.w, quaternion2.w, 0.01F);
                    DebugLog.UnitTests
                        .printUnitTest(
                            "%f,%f,%f,%f, %f,%f,%f,%f",
                            boolean0,
                            quaternion0.x,
                            quaternion0.y,
                            quaternion0.z,
                            quaternion0.w,
                            quaternion1.x,
                            quaternion1.y,
                            quaternion1.z,
                            quaternion1.w
                        );
                }

                DebugLog.UnitTests.println("UnitTest_getRotationMatrix. Complete");
            }
        }

        private static final class getRotationY {
            public static void run() {
                DebugLog.UnitTests.println("UnitTest_getRotationY");
                DebugLog.UnitTests.println("in, out, result");
                Quaternion quaternion = new Quaternion();

                for (int int0 = 0; int0 < 360; int0++) {
                    float float0 = PZMath.wrap(int0, -180.0F, 180.0F);
                    quaternion.setFromAxisAngle(new Vector4f(0.0F, 1.0F, 0.0F, float0 * (float) (Math.PI / 180.0)));
                    float float1 = HelperFunctions.getRotationY(quaternion) * (180.0F / (float)Math.PI);
                    boolean boolean0 = PZMath.equal(float0, float1, 0.001F);
                    DebugLog.UnitTests.printUnitTest("%f,%f", boolean0, float0, float1);
                }

                DebugLog.UnitTests.println("UnitTest_getRotationY. Complete");
            }
        }

        private static final class getRotationZ {
            public static void run() {
                DebugLog.UnitTests.println("UnitTest_getRotationZ");
                DebugLog.UnitTests.println("in, out, result");
                Quaternion quaternion = new Quaternion();

                for (int int0 = 0; int0 < 360; int0++) {
                    float float0 = PZMath.wrap(int0, -180.0F, 180.0F);
                    quaternion.setFromAxisAngle(new Vector4f(0.0F, 0.0F, 1.0F, float0 * (float) (Math.PI / 180.0)));
                    float float1 = HelperFunctions.getRotationZ(quaternion) * (180.0F / (float)Math.PI);
                    boolean boolean0 = PZMath.equal(float0, float1, 0.001F);
                    DebugLog.UnitTests.printUnitTest("%f,%f", boolean0, float0, float1);
                }

                DebugLog.UnitTests.println("UnitTest_getRotationZ. Complete");
            }
        }

        private static final class transformQuaternion {
            public static void run() {
                DebugLog.UnitTests.println("UnitTest_transformQuaternion");
                DebugLog.UnitTests.println("roll, pitch, yaw, out.x, out.y, out.z, cout.x, cout.y, cout.z, result");
                Quaternion quaternion = new Quaternion();
                new Vector3f(0.0F, 0.0F, 0.0F);
                new Vector3f(1.0F, 1.0F, 1.0F);
                Vector3f vector3f0 = new Vector3f();
                Vector3f vector3f1 = new Vector3f();
                Matrix4f matrix4f = new Matrix4f();
                Vector4f vector4f0 = new Vector4f();
                Vector4f vector4f1 = new Vector4f();
                Vector3f vector3f2 = new Vector3f(1.0F, 0.0F, 0.0F);
                Vector3f vector3f3 = new Vector3f(0.0F, 1.0F, 0.0F);
                Vector3f vector3f4 = new Vector3f(0.0F, 0.0F, 1.0F);
                runTest(0.0F, 0.0F, 90.0F, quaternion, vector3f0, vector3f1, matrix4f, vector4f0, vector4f1, vector3f2, vector3f3, vector3f4);
                runTest(0.0F, 0.0F, 5.0F, quaternion, vector3f0, vector3f1, matrix4f, vector4f0, vector4f1, vector3f2, vector3f3, vector3f4);

                for (int int0 = 0; int0 < 10; int0++) {
                    float float0 = PZMath.wrap(int0 / 10.0F * 360.0F, -180.0F, 180.0F);

                    for (int int1 = 0; int1 < 10; int1++) {
                        float float1 = PZMath.wrap(int1 / 10.0F * 360.0F, -180.0F, 180.0F);

                        for (int int2 = 0; int2 < 10; int2++) {
                            float float2 = PZMath.wrap(int2 / 10.0F * 360.0F, -180.0F, 180.0F);
                            runTest(float0, float1, float2, quaternion, vector3f0, vector3f1, matrix4f, vector4f0, vector4f1, vector3f2, vector3f3, vector3f4);
                        }
                    }
                }

                DebugLog.UnitTests.println("UnitTest_transformQuaternion. Complete");
            }

            public static void runTest(
                float float0,
                float float1,
                float float2,
                Quaternion quaternion,
                Vector3f vector3f4,
                Vector3f vector3f5,
                Matrix4f matrix4f,
                Vector4f vector4f0,
                Vector4f vector4f1,
                Vector3f vector3f1,
                Vector3f vector3f2,
                Vector3f vector3f3
            ) {
                Vector3f vector3f0 = new Vector3f(15.0F, 0.0F, 0.0F);
                matrix4f.setIdentity();
                matrix4f.translate(vector3f0);
                matrix4f.rotate(float0 * (float) (Math.PI / 180.0), vector3f1);
                matrix4f.rotate(float1 * (float) (Math.PI / 180.0), vector3f2);
                matrix4f.rotate(float2 * (float) (Math.PI / 180.0), vector3f3);
                HelperFunctions.getRotation(matrix4f, quaternion);
                vector3f4.set(1.0F, 0.0F, 0.0F);
                vector4f0.set(vector3f4.x, vector3f4.y, vector3f4.z, 1.0F);
                HelperFunctions.transform(matrix4f, vector4f0, vector4f1);
                HelperFunctions.transform(quaternion, vector3f4, vector3f5);
                vector3f5.x = vector3f5.x + vector3f0.x;
                vector3f5.y = vector3f5.y + vector3f0.y;
                vector3f5.z = vector3f5.z + vector3f0.z;
                boolean boolean0 = PZMath.equal(vector3f5.x, vector4f1.x, 0.01F)
                    && PZMath.equal(vector3f5.y, vector4f1.y, 0.01F)
                    && PZMath.equal(vector3f5.z, vector4f1.z, 0.01F);
                DebugLog.UnitTests
                    .printUnitTest(
                        "%f,%f,%f,%f,%f,%f,%f,%f,%f",
                        boolean0,
                        float0,
                        float1,
                        float2,
                        vector3f5.x,
                        vector3f5.y,
                        vector3f5.z,
                        vector4f1.x,
                        vector4f1.y,
                        vector4f1.z
                    );
            }
        }
    }
}
