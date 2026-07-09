// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

public class FrustumRayBuilder {
    private float nxnyX;
    private float nxnyY;
    private float nxnyZ;
    private float pxnyX;
    private float pxnyY;
    private float pxnyZ;
    private float pxpyX;
    private float pxpyY;
    private float pxpyZ;
    private float nxpyX;
    private float nxpyY;
    private float nxpyZ;
    private float cx;
    private float cy;
    private float cz;

    public FrustumRayBuilder() {
    }

    public FrustumRayBuilder(Matrix4fc matrix4fc) {
        this.set(matrix4fc);
    }

    public FrustumRayBuilder set(Matrix4fc matrix4fc) {
        float float0 = matrix4fc.m03() + matrix4fc.m00();
        float float1 = matrix4fc.m13() + matrix4fc.m10();
        float float2 = matrix4fc.m23() + matrix4fc.m20();
        float float3 = matrix4fc.m33() + matrix4fc.m30();
        float float4 = matrix4fc.m03() - matrix4fc.m00();
        float float5 = matrix4fc.m13() - matrix4fc.m10();
        float float6 = matrix4fc.m23() - matrix4fc.m20();
        float float7 = matrix4fc.m33() - matrix4fc.m30();
        float float8 = matrix4fc.m03() + matrix4fc.m01();
        float float9 = matrix4fc.m13() + matrix4fc.m11();
        float float10 = matrix4fc.m23() + matrix4fc.m21();
        float float11 = matrix4fc.m03() - matrix4fc.m01();
        float float12 = matrix4fc.m13() - matrix4fc.m11();
        float float13 = matrix4fc.m23() - matrix4fc.m21();
        float float14 = matrix4fc.m33() - matrix4fc.m31();
        this.nxnyX = float9 * float2 - float10 * float1;
        this.nxnyY = float10 * float0 - float8 * float2;
        this.nxnyZ = float8 * float1 - float9 * float0;
        this.pxnyX = float5 * float10 - float6 * float9;
        this.pxnyY = float6 * float8 - float4 * float10;
        this.pxnyZ = float4 * float9 - float5 * float8;
        this.nxpyX = float1 * float13 - float2 * float12;
        this.nxpyY = float2 * float11 - float0 * float13;
        this.nxpyZ = float0 * float12 - float1 * float11;
        this.pxpyX = float12 * float6 - float13 * float5;
        this.pxpyY = float13 * float4 - float11 * float6;
        this.pxpyZ = float11 * float5 - float12 * float4;
        float float15 = float5 * float2 - float6 * float1;
        float float16 = float6 * float0 - float4 * float2;
        float float17 = float4 * float1 - float5 * float0;
        float float18 = 1.0F / (float0 * this.pxpyX + float1 * this.pxpyY + float2 * this.pxpyZ);
        this.cx = (-this.pxpyX * float3 - this.nxpyX * float7 - float15 * float14) * float18;
        this.cy = (-this.pxpyY * float3 - this.nxpyY * float7 - float16 * float14) * float18;
        this.cz = (-this.pxpyZ * float3 - this.nxpyZ * float7 - float17 * float14) * float18;
        return this;
    }

    public Vector3fc origin(Vector3f vector3f) {
        vector3f.x = this.cx;
        vector3f.y = this.cy;
        vector3f.z = this.cz;
        return vector3f;
    }

    public Vector3fc dir(float float8, float float1, Vector3f vector3f) {
        float float0 = this.nxnyX + (this.nxpyX - this.nxnyX) * float1;
        float float2 = this.nxnyY + (this.nxpyY - this.nxnyY) * float1;
        float float3 = this.nxnyZ + (this.nxpyZ - this.nxnyZ) * float1;
        float float4 = this.pxnyX + (this.pxpyX - this.pxnyX) * float1;
        float float5 = this.pxnyY + (this.pxpyY - this.pxnyY) * float1;
        float float6 = this.pxnyZ + (this.pxpyZ - this.pxnyZ) * float1;
        float float7 = float0 + (float4 - float0) * float8;
        float float9 = float2 + (float5 - float2) * float8;
        float float10 = float3 + (float6 - float3) * float8;
        float float11 = Math.invsqrt(float7 * float7 + float9 * float9 + float10 * float10);
        vector3f.x = float7 * float11;
        vector3f.y = float9 * float11;
        vector3f.z = float10 * float11;
        return vector3f;
    }
}
