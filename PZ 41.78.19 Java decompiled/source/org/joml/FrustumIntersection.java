// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

public class FrustumIntersection {
    public static final int PLANE_NX = 0;
    public static final int PLANE_PX = 1;
    public static final int PLANE_NY = 2;
    public static final int PLANE_PY = 3;
    public static final int PLANE_NZ = 4;
    public static final int PLANE_PZ = 5;
    public static final int INTERSECT = -1;
    public static final int INSIDE = -2;
    public static final int OUTSIDE = -3;
    public static final int PLANE_MASK_NX = 1;
    public static final int PLANE_MASK_PX = 2;
    public static final int PLANE_MASK_NY = 4;
    public static final int PLANE_MASK_PY = 8;
    public static final int PLANE_MASK_NZ = 16;
    public static final int PLANE_MASK_PZ = 32;
    private float nxX;
    private float nxY;
    private float nxZ;
    private float nxW;
    private float pxX;
    private float pxY;
    private float pxZ;
    private float pxW;
    private float nyX;
    private float nyY;
    private float nyZ;
    private float nyW;
    private float pyX;
    private float pyY;
    private float pyZ;
    private float pyW;
    private float nzX;
    private float nzY;
    private float nzZ;
    private float nzW;
    private float pzX;
    private float pzY;
    private float pzZ;
    private float pzW;
    private final Vector4f[] planes = new Vector4f[6];

    public FrustumIntersection() {
        for (int int0 = 0; int0 < 6; int0++) {
            this.planes[int0] = new Vector4f();
        }
    }

    public FrustumIntersection(Matrix4fc matrix4fc) {
        for (int int0 = 0; int0 < 6; int0++) {
            this.planes[int0] = new Vector4f();
        }

        this.set(matrix4fc, true);
    }

    public FrustumIntersection(Matrix4fc matrix4fc, boolean boolean0) {
        for (int int0 = 0; int0 < 6; int0++) {
            this.planes[int0] = new Vector4f();
        }

        this.set(matrix4fc, boolean0);
    }

    public FrustumIntersection set(Matrix4fc matrix4fc) {
        return this.set(matrix4fc, true);
    }

    public FrustumIntersection set(Matrix4fc matrix4fc, boolean boolean0) {
        this.nxX = matrix4fc.m03() + matrix4fc.m00();
        this.nxY = matrix4fc.m13() + matrix4fc.m10();
        this.nxZ = matrix4fc.m23() + matrix4fc.m20();
        this.nxW = matrix4fc.m33() + matrix4fc.m30();
        if (boolean0) {
            float float0 = Math.invsqrt(this.nxX * this.nxX + this.nxY * this.nxY + this.nxZ * this.nxZ);
            this.nxX *= float0;
            this.nxY *= float0;
            this.nxZ *= float0;
            this.nxW *= float0;
        }

        this.planes[0].set(this.nxX, this.nxY, this.nxZ, this.nxW);
        this.pxX = matrix4fc.m03() - matrix4fc.m00();
        this.pxY = matrix4fc.m13() - matrix4fc.m10();
        this.pxZ = matrix4fc.m23() - matrix4fc.m20();
        this.pxW = matrix4fc.m33() - matrix4fc.m30();
        if (boolean0) {
            float float1 = Math.invsqrt(this.pxX * this.pxX + this.pxY * this.pxY + this.pxZ * this.pxZ);
            this.pxX *= float1;
            this.pxY *= float1;
            this.pxZ *= float1;
            this.pxW *= float1;
        }

        this.planes[1].set(this.pxX, this.pxY, this.pxZ, this.pxW);
        this.nyX = matrix4fc.m03() + matrix4fc.m01();
        this.nyY = matrix4fc.m13() + matrix4fc.m11();
        this.nyZ = matrix4fc.m23() + matrix4fc.m21();
        this.nyW = matrix4fc.m33() + matrix4fc.m31();
        if (boolean0) {
            float float2 = Math.invsqrt(this.nyX * this.nyX + this.nyY * this.nyY + this.nyZ * this.nyZ);
            this.nyX *= float2;
            this.nyY *= float2;
            this.nyZ *= float2;
            this.nyW *= float2;
        }

        this.planes[2].set(this.nyX, this.nyY, this.nyZ, this.nyW);
        this.pyX = matrix4fc.m03() - matrix4fc.m01();
        this.pyY = matrix4fc.m13() - matrix4fc.m11();
        this.pyZ = matrix4fc.m23() - matrix4fc.m21();
        this.pyW = matrix4fc.m33() - matrix4fc.m31();
        if (boolean0) {
            float float3 = Math.invsqrt(this.pyX * this.pyX + this.pyY * this.pyY + this.pyZ * this.pyZ);
            this.pyX *= float3;
            this.pyY *= float3;
            this.pyZ *= float3;
            this.pyW *= float3;
        }

        this.planes[3].set(this.pyX, this.pyY, this.pyZ, this.pyW);
        this.nzX = matrix4fc.m03() + matrix4fc.m02();
        this.nzY = matrix4fc.m13() + matrix4fc.m12();
        this.nzZ = matrix4fc.m23() + matrix4fc.m22();
        this.nzW = matrix4fc.m33() + matrix4fc.m32();
        if (boolean0) {
            float float4 = Math.invsqrt(this.nzX * this.nzX + this.nzY * this.nzY + this.nzZ * this.nzZ);
            this.nzX *= float4;
            this.nzY *= float4;
            this.nzZ *= float4;
            this.nzW *= float4;
        }

        this.planes[4].set(this.nzX, this.nzY, this.nzZ, this.nzW);
        this.pzX = matrix4fc.m03() - matrix4fc.m02();
        this.pzY = matrix4fc.m13() - matrix4fc.m12();
        this.pzZ = matrix4fc.m23() - matrix4fc.m22();
        this.pzW = matrix4fc.m33() - matrix4fc.m32();
        if (boolean0) {
            float float5 = Math.invsqrt(this.pzX * this.pzX + this.pzY * this.pzY + this.pzZ * this.pzZ);
            this.pzX *= float5;
            this.pzY *= float5;
            this.pzZ *= float5;
            this.pzW *= float5;
        }

        this.planes[5].set(this.pzX, this.pzY, this.pzZ, this.pzW);
        return this;
    }

    public boolean testPoint(Vector3fc vector3fc) {
        return this.testPoint(vector3fc.x(), vector3fc.y(), vector3fc.z());
    }

    public boolean testPoint(float float2, float float1, float float0) {
        return this.nxX * float2 + this.nxY * float1 + this.nxZ * float0 + this.nxW >= 0.0F
            && this.pxX * float2 + this.pxY * float1 + this.pxZ * float0 + this.pxW >= 0.0F
            && this.nyX * float2 + this.nyY * float1 + this.nyZ * float0 + this.nyW >= 0.0F
            && this.pyX * float2 + this.pyY * float1 + this.pyZ * float0 + this.pyW >= 0.0F
            && this.nzX * float2 + this.nzY * float1 + this.nzZ * float0 + this.nzW >= 0.0F
            && this.pzX * float2 + this.pzY * float1 + this.pzZ * float0 + this.pzW >= 0.0F;
    }

    public boolean testSphere(Vector3fc vector3fc, float float0) {
        return this.testSphere(vector3fc.x(), vector3fc.y(), vector3fc.z(), float0);
    }

    public boolean testSphere(float float3, float float2, float float1, float float0) {
        return this.nxX * float3 + this.nxY * float2 + this.nxZ * float1 + this.nxW >= -float0
            && this.pxX * float3 + this.pxY * float2 + this.pxZ * float1 + this.pxW >= -float0
            && this.nyX * float3 + this.nyY * float2 + this.nyZ * float1 + this.nyW >= -float0
            && this.pyX * float3 + this.pyY * float2 + this.pyZ * float1 + this.pyW >= -float0
            && this.nzX * float3 + this.nzY * float2 + this.nzZ * float1 + this.nzW >= -float0
            && this.pzX * float3 + this.pzY * float2 + this.pzZ * float1 + this.pzW >= -float0;
    }

    public int intersectSphere(Vector3fc vector3fc, float float0) {
        return this.intersectSphere(vector3fc.x(), vector3fc.y(), vector3fc.z(), float0);
    }

    public int intersectSphere(float float3, float float2, float float1, float float4) {
        boolean boolean0 = true;
        float float0 = this.nxX * float3 + this.nxY * float2 + this.nxZ * float1 + this.nxW;
        if (float0 >= -float4) {
            boolean0 &= float0 >= float4;
            float0 = this.pxX * float3 + this.pxY * float2 + this.pxZ * float1 + this.pxW;
            if (float0 >= -float4) {
                boolean0 &= float0 >= float4;
                float0 = this.nyX * float3 + this.nyY * float2 + this.nyZ * float1 + this.nyW;
                if (float0 >= -float4) {
                    boolean0 &= float0 >= float4;
                    float0 = this.pyX * float3 + this.pyY * float2 + this.pyZ * float1 + this.pyW;
                    if (float0 >= -float4) {
                        boolean0 &= float0 >= float4;
                        float0 = this.nzX * float3 + this.nzY * float2 + this.nzZ * float1 + this.nzW;
                        if (float0 >= -float4) {
                            boolean0 &= float0 >= float4;
                            float0 = this.pzX * float3 + this.pzY * float2 + this.pzZ * float1 + this.pzW;
                            if (float0 >= -float4) {
                                boolean0 &= float0 >= float4;
                                return boolean0 ? -2 : -1;
                            }
                        }
                    }
                }
            }
        }

        return -3;
    }

    public boolean testAab(Vector3fc vector3fc1, Vector3fc vector3fc0) {
        return this.testAab(vector3fc1.x(), vector3fc1.y(), vector3fc1.z(), vector3fc0.x(), vector3fc0.y(), vector3fc0.z());
    }

    public boolean testAab(float float4, float float2, float float0, float float5, float float3, float float1) {
        return this.nxX * (this.nxX < 0.0F ? float4 : float5) + this.nxY * (this.nxY < 0.0F ? float2 : float3) + this.nxZ * (this.nxZ < 0.0F ? float0 : float1)
                >= -this.nxW
            && this.pxX * (this.pxX < 0.0F ? float4 : float5) + this.pxY * (this.pxY < 0.0F ? float2 : float3) + this.pxZ * (this.pxZ < 0.0F ? float0 : float1)
                >= -this.pxW
            && this.nyX * (this.nyX < 0.0F ? float4 : float5) + this.nyY * (this.nyY < 0.0F ? float2 : float3) + this.nyZ * (this.nyZ < 0.0F ? float0 : float1)
                >= -this.nyW
            && this.pyX * (this.pyX < 0.0F ? float4 : float5) + this.pyY * (this.pyY < 0.0F ? float2 : float3) + this.pyZ * (this.pyZ < 0.0F ? float0 : float1)
                >= -this.pyW
            && this.nzX * (this.nzX < 0.0F ? float4 : float5) + this.nzY * (this.nzY < 0.0F ? float2 : float3) + this.nzZ * (this.nzZ < 0.0F ? float0 : float1)
                >= -this.nzW
            && this.pzX * (this.pzX < 0.0F ? float4 : float5) + this.pzY * (this.pzY < 0.0F ? float2 : float3) + this.pzZ * (this.pzZ < 0.0F ? float0 : float1)
                >= -this.pzW;
    }

    public boolean testPlaneXY(Vector2fc vector2fc1, Vector2fc vector2fc0) {
        return this.testPlaneXY(vector2fc1.x(), vector2fc1.y(), vector2fc0.x(), vector2fc0.y());
    }

    public boolean testPlaneXY(float float2, float float0, float float3, float float1) {
        return this.nxX * (this.nxX < 0.0F ? float2 : float3) + this.nxY * (this.nxY < 0.0F ? float0 : float1) >= -this.nxW
            && this.pxX * (this.pxX < 0.0F ? float2 : float3) + this.pxY * (this.pxY < 0.0F ? float0 : float1) >= -this.pxW
            && this.nyX * (this.nyX < 0.0F ? float2 : float3) + this.nyY * (this.nyY < 0.0F ? float0 : float1) >= -this.nyW
            && this.pyX * (this.pyX < 0.0F ? float2 : float3) + this.pyY * (this.pyY < 0.0F ? float0 : float1) >= -this.pyW
            && this.nzX * (this.nzX < 0.0F ? float2 : float3) + this.nzY * (this.nzY < 0.0F ? float0 : float1) >= -this.nzW
            && this.pzX * (this.pzX < 0.0F ? float2 : float3) + this.pzY * (this.pzY < 0.0F ? float0 : float1) >= -this.pzW;
    }

    public boolean testPlaneXZ(float float2, float float0, float float3, float float1) {
        return this.nxX * (this.nxX < 0.0F ? float2 : float3) + this.nxZ * (this.nxZ < 0.0F ? float0 : float1) >= -this.nxW
            && this.pxX * (this.pxX < 0.0F ? float2 : float3) + this.pxZ * (this.pxZ < 0.0F ? float0 : float1) >= -this.pxW
            && this.nyX * (this.nyX < 0.0F ? float2 : float3) + this.nyZ * (this.nyZ < 0.0F ? float0 : float1) >= -this.nyW
            && this.pyX * (this.pyX < 0.0F ? float2 : float3) + this.pyZ * (this.pyZ < 0.0F ? float0 : float1) >= -this.pyW
            && this.nzX * (this.nzX < 0.0F ? float2 : float3) + this.nzZ * (this.nzZ < 0.0F ? float0 : float1) >= -this.nzW
            && this.pzX * (this.pzX < 0.0F ? float2 : float3) + this.pzZ * (this.pzZ < 0.0F ? float0 : float1) >= -this.pzW;
    }

    public int intersectAab(Vector3fc vector3fc1, Vector3fc vector3fc0) {
        return this.intersectAab(vector3fc1.x(), vector3fc1.y(), vector3fc1.z(), vector3fc0.x(), vector3fc0.y(), vector3fc0.z());
    }

    public int intersectAab(float float4, float float2, float float0, float float5, float float3, float float1) {
        byte byte0 = 0;
        boolean boolean0 = true;
        if (this.nxX * (this.nxX < 0.0F ? float4 : float5) + this.nxY * (this.nxY < 0.0F ? float2 : float3) + this.nxZ * (this.nxZ < 0.0F ? float0 : float1)
            >= -this.nxW) {
            byte0 = 1;
            boolean0 &= this.nxX * (this.nxX < 0.0F ? float5 : float4)
                    + this.nxY * (this.nxY < 0.0F ? float3 : float2)
                    + this.nxZ * (this.nxZ < 0.0F ? float1 : float0)
                >= -this.nxW;
            if (this.pxX * (this.pxX < 0.0F ? float4 : float5)
                    + this.pxY * (this.pxY < 0.0F ? float2 : float3)
                    + this.pxZ * (this.pxZ < 0.0F ? float0 : float1)
                >= -this.pxW) {
                byte0 = 2;
                boolean0 &= this.pxX * (this.pxX < 0.0F ? float5 : float4)
                        + this.pxY * (this.pxY < 0.0F ? float3 : float2)
                        + this.pxZ * (this.pxZ < 0.0F ? float1 : float0)
                    >= -this.pxW;
                if (this.nyX * (this.nyX < 0.0F ? float4 : float5)
                        + this.nyY * (this.nyY < 0.0F ? float2 : float3)
                        + this.nyZ * (this.nyZ < 0.0F ? float0 : float1)
                    >= -this.nyW) {
                    byte0 = 3;
                    boolean0 &= this.nyX * (this.nyX < 0.0F ? float5 : float4)
                            + this.nyY * (this.nyY < 0.0F ? float3 : float2)
                            + this.nyZ * (this.nyZ < 0.0F ? float1 : float0)
                        >= -this.nyW;
                    if (this.pyX * (this.pyX < 0.0F ? float4 : float5)
                            + this.pyY * (this.pyY < 0.0F ? float2 : float3)
                            + this.pyZ * (this.pyZ < 0.0F ? float0 : float1)
                        >= -this.pyW) {
                        byte0 = 4;
                        boolean0 &= this.pyX * (this.pyX < 0.0F ? float5 : float4)
                                + this.pyY * (this.pyY < 0.0F ? float3 : float2)
                                + this.pyZ * (this.pyZ < 0.0F ? float1 : float0)
                            >= -this.pyW;
                        if (this.nzX * (this.nzX < 0.0F ? float4 : float5)
                                + this.nzY * (this.nzY < 0.0F ? float2 : float3)
                                + this.nzZ * (this.nzZ < 0.0F ? float0 : float1)
                            >= -this.nzW) {
                            byte0 = 5;
                            boolean0 &= this.nzX * (this.nzX < 0.0F ? float5 : float4)
                                    + this.nzY * (this.nzY < 0.0F ? float3 : float2)
                                    + this.nzZ * (this.nzZ < 0.0F ? float1 : float0)
                                >= -this.nzW;
                            if (this.pzX * (this.pzX < 0.0F ? float4 : float5)
                                    + this.pzY * (this.pzY < 0.0F ? float2 : float3)
                                    + this.pzZ * (this.pzZ < 0.0F ? float0 : float1)
                                >= -this.pzW) {
                                boolean0 &= this.pzX * (this.pzX < 0.0F ? float5 : float4)
                                        + this.pzY * (this.pzY < 0.0F ? float3 : float2)
                                        + this.pzZ * (this.pzZ < 0.0F ? float1 : float0)
                                    >= -this.pzW;
                                return boolean0 ? -2 : -1;
                            }
                        }
                    }
                }
            }
        }

        return byte0;
    }

    public float distanceToPlane(float float5, float float3, float float1, float float4, float float2, float float0, int int0) {
        return this.planes[int0].x * (this.planes[int0].x < 0.0F ? float4 : float5)
            + this.planes[int0].y * (this.planes[int0].y < 0.0F ? float2 : float3)
            + this.planes[int0].z * (this.planes[int0].z < 0.0F ? float0 : float1)
            + this.planes[int0].w;
    }

    public int intersectAab(Vector3fc vector3fc1, Vector3fc vector3fc0, int int0) {
        return this.intersectAab(vector3fc1.x(), vector3fc1.y(), vector3fc1.z(), vector3fc0.x(), vector3fc0.y(), vector3fc0.z(), int0);
    }

    public int intersectAab(float float4, float float2, float float0, float float5, float float3, float float1, int int0) {
        byte byte0 = 0;
        boolean boolean0 = true;
        if ((int0 & 1) == 0
            || this.nxX * (this.nxX < 0.0F ? float4 : float5) + this.nxY * (this.nxY < 0.0F ? float2 : float3) + this.nxZ * (this.nxZ < 0.0F ? float0 : float1)
                >= -this.nxW) {
            byte0 = 1;
            boolean0 &= this.nxX * (this.nxX < 0.0F ? float5 : float4)
                    + this.nxY * (this.nxY < 0.0F ? float3 : float2)
                    + this.nxZ * (this.nxZ < 0.0F ? float1 : float0)
                >= -this.nxW;
            if ((int0 & 2) == 0
                || this.pxX * (this.pxX < 0.0F ? float4 : float5)
                        + this.pxY * (this.pxY < 0.0F ? float2 : float3)
                        + this.pxZ * (this.pxZ < 0.0F ? float0 : float1)
                    >= -this.pxW) {
                byte0 = 2;
                boolean0 &= this.pxX * (this.pxX < 0.0F ? float5 : float4)
                        + this.pxY * (this.pxY < 0.0F ? float3 : float2)
                        + this.pxZ * (this.pxZ < 0.0F ? float1 : float0)
                    >= -this.pxW;
                if ((int0 & 4) == 0
                    || this.nyX * (this.nyX < 0.0F ? float4 : float5)
                            + this.nyY * (this.nyY < 0.0F ? float2 : float3)
                            + this.nyZ * (this.nyZ < 0.0F ? float0 : float1)
                        >= -this.nyW) {
                    byte0 = 3;
                    boolean0 &= this.nyX * (this.nyX < 0.0F ? float5 : float4)
                            + this.nyY * (this.nyY < 0.0F ? float3 : float2)
                            + this.nyZ * (this.nyZ < 0.0F ? float1 : float0)
                        >= -this.nyW;
                    if ((int0 & 8) == 0
                        || this.pyX * (this.pyX < 0.0F ? float4 : float5)
                                + this.pyY * (this.pyY < 0.0F ? float2 : float3)
                                + this.pyZ * (this.pyZ < 0.0F ? float0 : float1)
                            >= -this.pyW) {
                        byte0 = 4;
                        boolean0 &= this.pyX * (this.pyX < 0.0F ? float5 : float4)
                                + this.pyY * (this.pyY < 0.0F ? float3 : float2)
                                + this.pyZ * (this.pyZ < 0.0F ? float1 : float0)
                            >= -this.pyW;
                        if ((int0 & 16) == 0
                            || this.nzX * (this.nzX < 0.0F ? float4 : float5)
                                    + this.nzY * (this.nzY < 0.0F ? float2 : float3)
                                    + this.nzZ * (this.nzZ < 0.0F ? float0 : float1)
                                >= -this.nzW) {
                            byte0 = 5;
                            boolean0 &= this.nzX * (this.nzX < 0.0F ? float5 : float4)
                                    + this.nzY * (this.nzY < 0.0F ? float3 : float2)
                                    + this.nzZ * (this.nzZ < 0.0F ? float1 : float0)
                                >= -this.nzW;
                            if ((int0 & 32) == 0
                                || this.pzX * (this.pzX < 0.0F ? float4 : float5)
                                        + this.pzY * (this.pzY < 0.0F ? float2 : float3)
                                        + this.pzZ * (this.pzZ < 0.0F ? float0 : float1)
                                    >= -this.pzW) {
                                boolean0 &= this.pzX * (this.pzX < 0.0F ? float5 : float4)
                                        + this.pzY * (this.pzY < 0.0F ? float3 : float2)
                                        + this.pzZ * (this.pzZ < 0.0F ? float1 : float0)
                                    >= -this.pzW;
                                return boolean0 ? -2 : -1;
                            }
                        }
                    }
                }
            }
        }

        return byte0;
    }

    public int intersectAab(Vector3fc vector3fc1, Vector3fc vector3fc0, int int0, int int1) {
        return this.intersectAab(vector3fc1.x(), vector3fc1.y(), vector3fc1.z(), vector3fc0.x(), vector3fc0.y(), vector3fc0.z(), int0, int1);
    }

    public int intersectAab(float float4, float float2, float float0, float float5, float float3, float float1, int int2, int int1) {
        int int0 = int1;
        boolean boolean0 = true;
        Vector4f vector4f = this.planes[int1];
        if ((int2 & 1 << int1) != 0
            && vector4f.x * (vector4f.x < 0.0F ? float4 : float5)
                    + vector4f.y * (vector4f.y < 0.0F ? float2 : float3)
                    + vector4f.z * (vector4f.z < 0.0F ? float0 : float1)
                < -vector4f.w) {
            return int1;
        } else {
            if ((int2 & 1) == 0
                || this.nxX * (this.nxX < 0.0F ? float4 : float5)
                        + this.nxY * (this.nxY < 0.0F ? float2 : float3)
                        + this.nxZ * (this.nxZ < 0.0F ? float0 : float1)
                    >= -this.nxW) {
                int0 = 1;
                boolean0 &= this.nxX * (this.nxX < 0.0F ? float5 : float4)
                        + this.nxY * (this.nxY < 0.0F ? float3 : float2)
                        + this.nxZ * (this.nxZ < 0.0F ? float1 : float0)
                    >= -this.nxW;
                if ((int2 & 2) == 0
                    || this.pxX * (this.pxX < 0.0F ? float4 : float5)
                            + this.pxY * (this.pxY < 0.0F ? float2 : float3)
                            + this.pxZ * (this.pxZ < 0.0F ? float0 : float1)
                        >= -this.pxW) {
                    int0 = 2;
                    boolean0 &= this.pxX * (this.pxX < 0.0F ? float5 : float4)
                            + this.pxY * (this.pxY < 0.0F ? float3 : float2)
                            + this.pxZ * (this.pxZ < 0.0F ? float1 : float0)
                        >= -this.pxW;
                    if ((int2 & 4) == 0
                        || this.nyX * (this.nyX < 0.0F ? float4 : float5)
                                + this.nyY * (this.nyY < 0.0F ? float2 : float3)
                                + this.nyZ * (this.nyZ < 0.0F ? float0 : float1)
                            >= -this.nyW) {
                        int0 = 3;
                        boolean0 &= this.nyX * (this.nyX < 0.0F ? float5 : float4)
                                + this.nyY * (this.nyY < 0.0F ? float3 : float2)
                                + this.nyZ * (this.nyZ < 0.0F ? float1 : float0)
                            >= -this.nyW;
                        if ((int2 & 8) == 0
                            || this.pyX * (this.pyX < 0.0F ? float4 : float5)
                                    + this.pyY * (this.pyY < 0.0F ? float2 : float3)
                                    + this.pyZ * (this.pyZ < 0.0F ? float0 : float1)
                                >= -this.pyW) {
                            int0 = 4;
                            boolean0 &= this.pyX * (this.pyX < 0.0F ? float5 : float4)
                                    + this.pyY * (this.pyY < 0.0F ? float3 : float2)
                                    + this.pyZ * (this.pyZ < 0.0F ? float1 : float0)
                                >= -this.pyW;
                            if ((int2 & 16) == 0
                                || this.nzX * (this.nzX < 0.0F ? float4 : float5)
                                        + this.nzY * (this.nzY < 0.0F ? float2 : float3)
                                        + this.nzZ * (this.nzZ < 0.0F ? float0 : float1)
                                    >= -this.nzW) {
                                int0 = 5;
                                boolean0 &= this.nzX * (this.nzX < 0.0F ? float5 : float4)
                                        + this.nzY * (this.nzY < 0.0F ? float3 : float2)
                                        + this.nzZ * (this.nzZ < 0.0F ? float1 : float0)
                                    >= -this.nzW;
                                if ((int2 & 32) == 0
                                    || this.pzX * (this.pzX < 0.0F ? float4 : float5)
                                            + this.pzY * (this.pzY < 0.0F ? float2 : float3)
                                            + this.pzZ * (this.pzZ < 0.0F ? float0 : float1)
                                        >= -this.pzW) {
                                    boolean0 &= this.pzX * (this.pzX < 0.0F ? float5 : float4)
                                            + this.pzY * (this.pzY < 0.0F ? float3 : float2)
                                            + this.pzZ * (this.pzZ < 0.0F ? float1 : float0)
                                        >= -this.pzW;
                                    return boolean0 ? -2 : -1;
                                }
                            }
                        }
                    }
                }
            }

            return int0;
        }
    }
}
