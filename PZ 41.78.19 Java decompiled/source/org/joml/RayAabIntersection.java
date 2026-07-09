// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

public class RayAabIntersection {
    private float originX;
    private float originY;
    private float originZ;
    private float dirX;
    private float dirY;
    private float dirZ;
    private float c_xy;
    private float c_yx;
    private float c_zy;
    private float c_yz;
    private float c_xz;
    private float c_zx;
    private float s_xy;
    private float s_yx;
    private float s_zy;
    private float s_yz;
    private float s_xz;
    private float s_zx;
    private byte classification;

    public RayAabIntersection() {
    }

    public RayAabIntersection(float float0, float float1, float float2, float float3, float float4, float float5) {
        this.set(float0, float1, float2, float3, float4, float5);
    }

    public void set(float float0, float float1, float float2, float float3, float float4, float float5) {
        this.originX = float0;
        this.originY = float1;
        this.originZ = float2;
        this.dirX = float3;
        this.dirY = float4;
        this.dirZ = float5;
        this.precomputeSlope();
    }

    private static int signum(float float0) {
        return float0 != 0.0F && !Float.isNaN(float0) ? (1 - Float.floatToIntBits(float0) >>> 31 << 1) - 1 : 0;
    }

    private void precomputeSlope() {
        float float0 = 1.0F / this.dirX;
        float float1 = 1.0F / this.dirY;
        float float2 = 1.0F / this.dirZ;
        this.s_yx = this.dirX * float1;
        this.s_xy = this.dirY * float0;
        this.s_zy = this.dirY * float2;
        this.s_yz = this.dirZ * float1;
        this.s_xz = this.dirZ * float0;
        this.s_zx = this.dirX * float2;
        this.c_xy = this.originY - this.s_xy * this.originX;
        this.c_yx = this.originX - this.s_yx * this.originY;
        this.c_zy = this.originY - this.s_zy * this.originZ;
        this.c_yz = this.originZ - this.s_yz * this.originY;
        this.c_xz = this.originZ - this.s_xz * this.originX;
        this.c_zx = this.originX - this.s_zx * this.originZ;
        int int0 = signum(this.dirX);
        int int1 = signum(this.dirY);
        int int2 = signum(this.dirZ);
        this.classification = (byte)(int2 + 1 << 4 | int1 + 1 << 2 | int0 + 1);
    }

    public boolean test(float float0, float float1, float float2, float float3, float float4, float float5) {
        switch (this.classification) {
            case 0:
                return this.MMM(float0, float1, float2, float3, float4, float5);
            case 1:
                return this.OMM(float0, float1, float2, float3, float4, float5);
            case 2:
                return this.PMM(float0, float1, float2, float3, float4, float5);
            case 3:
                return false;
            case 4:
                return this.MOM(float0, float1, float2, float3, float4, float5);
            case 5:
                return this.OOM(float0, float1, float2, float3, float4);
            case 6:
                return this.POM(float0, float1, float2, float3, float4, float5);
            case 7:
                return false;
            case 8:
                return this.MPM(float0, float1, float2, float3, float4, float5);
            case 9:
                return this.OPM(float0, float1, float2, float3, float4, float5);
            case 10:
                return this.PPM(float0, float1, float2, float3, float4, float5);
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                return false;
            case 16:
                return this.MMO(float0, float1, float2, float3, float4, float5);
            case 17:
                return this.OMO(float0, float1, float2, float3, float5);
            case 18:
                return this.PMO(float0, float1, float2, float3, float4, float5);
            case 19:
                return false;
            case 20:
                return this.MOO(float0, float1, float2, float4, float5);
            case 21:
                return false;
            case 22:
                return this.POO(float1, float2, float3, float4, float5);
            case 23:
                return false;
            case 24:
                return this.MPO(float0, float1, float2, float3, float4, float5);
            case 25:
                return this.OPO(float0, float2, float3, float4, float5);
            case 26:
                return this.PPO(float0, float1, float2, float3, float4, float5);
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
                return false;
            case 32:
                return this.MMP(float0, float1, float2, float3, float4, float5);
            case 33:
                return this.OMP(float0, float1, float2, float3, float4, float5);
            case 34:
                return this.PMP(float0, float1, float2, float3, float4, float5);
            case 35:
                return false;
            case 36:
                return this.MOP(float0, float1, float2, float3, float4, float5);
            case 37:
                return this.OOP(float0, float1, float3, float4, float5);
            case 38:
                return this.POP(float0, float1, float2, float3, float4, float5);
            case 39:
                return false;
            case 40:
                return this.MPP(float0, float1, float2, float3, float4, float5);
            case 41:
                return this.OPP(float0, float1, float2, float3, float4, float5);
            case 42:
                return this.PPP(float0, float1, float2, float3, float4, float5);
            default:
                return false;
        }
    }

    private boolean MMM(float float3, float float4, float float1, float float0, float float5, float float2) {
        return this.originX >= float3
            && this.originY >= float4
            && this.originZ >= float1
            && this.s_xy * float3 - float5 + this.c_xy <= 0.0F
            && this.s_yx * float4 - float0 + this.c_yx <= 0.0F
            && this.s_zy * float1 - float5 + this.c_zy <= 0.0F
            && this.s_yz * float4 - float2 + this.c_yz <= 0.0F
            && this.s_xz * float3 - float2 + this.c_xz <= 0.0F
            && this.s_zx * float1 - float0 + this.c_zx <= 0.0F;
    }

    private boolean OMM(float float5, float float1, float float3, float float4, float float2, float float0) {
        return this.originX >= float5
            && this.originX <= float4
            && this.originY >= float1
            && this.originZ >= float3
            && this.s_zy * float3 - float2 + this.c_zy <= 0.0F
            && this.s_yz * float1 - float0 + this.c_yz <= 0.0F;
    }

    private boolean PMM(float float0, float float4, float float1, float float3, float float5, float float2) {
        return this.originX <= float3
            && this.originY >= float4
            && this.originZ >= float1
            && this.s_xy * float3 - float5 + this.c_xy <= 0.0F
            && this.s_yx * float4 - float0 + this.c_yx >= 0.0F
            && this.s_zy * float1 - float5 + this.c_zy <= 0.0F
            && this.s_yz * float4 - float2 + this.c_yz <= 0.0F
            && this.s_xz * float3 - float2 + this.c_xz <= 0.0F
            && this.s_zx * float1 - float0 + this.c_zx >= 0.0F;
    }

    private boolean MOM(float float3, float float5, float float1, float float0, float float4, float float2) {
        return this.originY >= float5
            && this.originY <= float4
            && this.originX >= float3
            && this.originZ >= float1
            && this.s_xz * float3 - float2 + this.c_xz <= 0.0F
            && this.s_zx * float1 - float0 + this.c_zx <= 0.0F;
    }

    private boolean OOM(float float3, float float1, float float4, float float2, float float0) {
        return this.originZ >= float4 && this.originX >= float3 && this.originX <= float2 && this.originY >= float1 && this.originY <= float0;
    }

    private boolean POM(float float0, float float5, float float1, float float3, float float4, float float2) {
        return this.originY >= float5
            && this.originY <= float4
            && this.originX <= float3
            && this.originZ >= float1
            && this.s_xz * float3 - float2 + this.c_xz <= 0.0F
            && this.s_zx * float1 - float0 + this.c_zx >= 0.0F;
    }

    private boolean MPM(float float3, float float5, float float1, float float0, float float4, float float2) {
        return this.originX >= float3
            && this.originY <= float4
            && this.originZ >= float1
            && this.s_xy * float3 - float5 + this.c_xy >= 0.0F
            && this.s_yx * float4 - float0 + this.c_yx <= 0.0F
            && this.s_zy * float1 - float5 + this.c_zy >= 0.0F
            && this.s_yz * float4 - float2 + this.c_yz <= 0.0F
            && this.s_xz * float3 - float2 + this.c_xz <= 0.0F
            && this.s_zx * float1 - float0 + this.c_zx <= 0.0F;
    }

    private boolean OPM(float float5, float float2, float float3, float float4, float float1, float float0) {
        return this.originX >= float5
            && this.originX <= float4
            && this.originY <= float1
            && this.originZ >= float3
            && this.s_zy * float3 - float2 + this.c_zy >= 0.0F
            && this.s_yz * float1 - float0 + this.c_yz <= 0.0F;
    }

    private boolean PPM(float float0, float float5, float float1, float float3, float float4, float float2) {
        return this.originX <= float3
            && this.originY <= float4
            && this.originZ >= float1
            && this.s_xy * float3 - float5 + this.c_xy >= 0.0F
            && this.s_yx * float4 - float0 + this.c_yx >= 0.0F
            && this.s_zy * float1 - float5 + this.c_zy >= 0.0F
            && this.s_yz * float4 - float2 + this.c_yz <= 0.0F
            && this.s_xz * float3 - float2 + this.c_xz <= 0.0F
            && this.s_zx * float1 - float0 + this.c_zx >= 0.0F;
    }

    private boolean MMO(float float3, float float1, float float5, float float0, float float2, float float4) {
        return this.originZ >= float5
            && this.originZ <= float4
            && this.originX >= float3
            && this.originY >= float1
            && this.s_xy * float3 - float2 + this.c_xy <= 0.0F
            && this.s_yx * float1 - float0 + this.c_yx <= 0.0F;
    }

    private boolean OMO(float float3, float float4, float float1, float float2, float float0) {
        return this.originY >= float4 && this.originX >= float3 && this.originX <= float2 && this.originZ >= float1 && this.originZ <= float0;
    }

    private boolean PMO(float float0, float float1, float float5, float float3, float float2, float float4) {
        return this.originZ >= float5
            && this.originZ <= float4
            && this.originX <= float3
            && this.originY >= float1
            && this.s_xy * float3 - float2 + this.c_xy <= 0.0F
            && this.s_yx * float1 - float0 + this.c_yx >= 0.0F;
    }

    private boolean MOO(float float4, float float3, float float1, float float2, float float0) {
        return this.originX >= float4 && this.originY >= float3 && this.originY <= float2 && this.originZ >= float1 && this.originZ <= float0;
    }

    private boolean POO(float float3, float float1, float float4, float float2, float float0) {
        return this.originX <= float4 && this.originY >= float3 && this.originY <= float2 && this.originZ >= float1 && this.originZ <= float0;
    }

    private boolean MPO(float float3, float float2, float float5, float float0, float float1, float float4) {
        return this.originZ >= float5
            && this.originZ <= float4
            && this.originX >= float3
            && this.originY <= float1
            && this.s_xy * float3 - float2 + this.c_xy >= 0.0F
            && this.s_yx * float1 - float0 + this.c_yx <= 0.0F;
    }

    private boolean OPO(float float3, float float1, float float2, float float4, float float0) {
        return this.originY <= float4 && this.originX >= float3 && this.originX <= float2 && this.originZ >= float1 && this.originZ <= float0;
    }

    private boolean PPO(float float0, float float2, float float5, float float3, float float1, float float4) {
        return this.originZ >= float5
            && this.originZ <= float4
            && this.originX <= float3
            && this.originY <= float1
            && this.s_xy * float3 - float2 + this.c_xy >= 0.0F
            && this.s_yx * float1 - float0 + this.c_yx >= 0.0F;
    }

    private boolean MMP(float float3, float float4, float float2, float float0, float float5, float float1) {
        return this.originX >= float3
            && this.originY >= float4
            && this.originZ <= float1
            && this.s_xy * float3 - float5 + this.c_xy <= 0.0F
            && this.s_yx * float4 - float0 + this.c_yx <= 0.0F
            && this.s_zy * float1 - float5 + this.c_zy <= 0.0F
            && this.s_yz * float4 - float2 + this.c_yz >= 0.0F
            && this.s_xz * float3 - float2 + this.c_xz >= 0.0F
            && this.s_zx * float1 - float0 + this.c_zx <= 0.0F;
    }

    private boolean OMP(float float5, float float1, float float0, float float4, float float2, float float3) {
        return this.originX >= float5
            && this.originX <= float4
            && this.originY >= float1
            && this.originZ <= float3
            && this.s_zy * float3 - float2 + this.c_zy <= 0.0F
            && this.s_yz * float1 - float0 + this.c_yz >= 0.0F;
    }

    private boolean PMP(float float0, float float4, float float2, float float3, float float5, float float1) {
        return this.originX <= float3
            && this.originY >= float4
            && this.originZ <= float1
            && this.s_xy * float3 - float5 + this.c_xy <= 0.0F
            && this.s_yx * float4 - float0 + this.c_yx >= 0.0F
            && this.s_zy * float1 - float5 + this.c_zy <= 0.0F
            && this.s_yz * float4 - float2 + this.c_yz >= 0.0F
            && this.s_xz * float3 - float2 + this.c_xz >= 0.0F
            && this.s_zx * float1 - float0 + this.c_zx >= 0.0F;
    }

    private boolean MOP(float float3, float float5, float float2, float float0, float float4, float float1) {
        return this.originY >= float5
            && this.originY <= float4
            && this.originX >= float3
            && this.originZ <= float1
            && this.s_xz * float3 - float2 + this.c_xz >= 0.0F
            && this.s_zx * float1 - float0 + this.c_zx <= 0.0F;
    }

    private boolean OOP(float float3, float float1, float float2, float float0, float float4) {
        return this.originZ <= float4 && this.originX >= float3 && this.originX <= float2 && this.originY >= float1 && this.originY <= float0;
    }

    private boolean POP(float float0, float float5, float float2, float float3, float float4, float float1) {
        return this.originY >= float5
            && this.originY <= float4
            && this.originX <= float3
            && this.originZ <= float1
            && this.s_xz * float3 - float2 + this.c_xz >= 0.0F
            && this.s_zx * float1 - float0 + this.c_zx <= 0.0F;
    }

    private boolean MPP(float float3, float float5, float float2, float float0, float float4, float float1) {
        return this.originX >= float3
            && this.originY <= float4
            && this.originZ <= float1
            && this.s_xy * float3 - float5 + this.c_xy >= 0.0F
            && this.s_yx * float4 - float0 + this.c_yx <= 0.0F
            && this.s_zy * float1 - float5 + this.c_zy >= 0.0F
            && this.s_yz * float4 - float2 + this.c_yz >= 0.0F
            && this.s_xz * float3 - float2 + this.c_xz >= 0.0F
            && this.s_zx * float1 - float0 + this.c_zx <= 0.0F;
    }

    private boolean OPP(float float5, float float2, float float0, float float4, float float1, float float3) {
        return this.originX >= float5
            && this.originX <= float4
            && this.originY <= float1
            && this.originZ <= float3
            && this.s_zy * float3 - float2 + this.c_zy <= 0.0F
            && this.s_yz * float1 - float0 + this.c_yz <= 0.0F;
    }

    private boolean PPP(float float0, float float5, float float2, float float3, float float4, float float1) {
        return this.originX <= float3
            && this.originY <= float4
            && this.originZ <= float1
            && this.s_xy * float3 - float5 + this.c_xy >= 0.0F
            && this.s_yx * float4 - float0 + this.c_yx >= 0.0F
            && this.s_zy * float1 - float5 + this.c_zy >= 0.0F
            && this.s_yz * float4 - float2 + this.c_yz >= 0.0F
            && this.s_xz * float3 - float2 + this.c_xz >= 0.0F
            && this.s_zx * float1 - float0 + this.c_zx >= 0.0F;
    }
}
