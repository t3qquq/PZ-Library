// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

public class QuaternionfInterpolator {
    private final QuaternionfInterpolator.SvdDecomposition3f svdDecomposition3f = new QuaternionfInterpolator.SvdDecomposition3f();
    private final float[] m = new float[9];
    private final Matrix3f u = new Matrix3f();
    private final Matrix3f v = new Matrix3f();

    public Quaternionf computeWeightedAverage(Quaternionfc[] quaternionfcs, float[] floats, int int1, Quaternionf quaternionf) {
        float float0 = 0.0F;
        float float1 = 0.0F;
        float float2 = 0.0F;
        float float3 = 0.0F;
        float float4 = 0.0F;
        float float5 = 0.0F;
        float float6 = 0.0F;
        float float7 = 0.0F;
        float float8 = 0.0F;

        for (int int0 = 0; int0 < quaternionfcs.length; int0++) {
            Quaternionfc quaternionfc = quaternionfcs[int0];
            float float9 = quaternionfc.x() + quaternionfc.x();
            float float10 = quaternionfc.y() + quaternionfc.y();
            float float11 = quaternionfc.z() + quaternionfc.z();
            float float12 = float9 * quaternionfc.x();
            float float13 = float10 * quaternionfc.y();
            float float14 = float11 * quaternionfc.z();
            float float15 = float9 * quaternionfc.y();
            float float16 = float9 * quaternionfc.z();
            float float17 = float9 * quaternionfc.w();
            float float18 = float10 * quaternionfc.z();
            float float19 = float10 * quaternionfc.w();
            float float20 = float11 * quaternionfc.w();
            float0 += floats[int0] * (1.0F - float13 - float14);
            float1 += floats[int0] * (float15 + float20);
            float2 += floats[int0] * (float16 - float19);
            float3 += floats[int0] * (float15 - float20);
            float4 += floats[int0] * (1.0F - float14 - float12);
            float5 += floats[int0] * (float18 + float17);
            float6 += floats[int0] * (float16 + float19);
            float7 += floats[int0] * (float18 - float17);
            float8 += floats[int0] * (1.0F - float13 - float12);
        }

        this.m[0] = float0;
        this.m[1] = float1;
        this.m[2] = float2;
        this.m[3] = float3;
        this.m[4] = float4;
        this.m[5] = float5;
        this.m[6] = float6;
        this.m[7] = float7;
        this.m[8] = float8;
        this.svdDecomposition3f.svd(this.m, int1, this.u, this.v);
        this.u.mul(this.v.transpose());
        return quaternionf.setFromNormalized(this.u).normalize();
    }

    private static class SvdDecomposition3f {
        private final float[] rv1 = new float[3];
        private final float[] w = new float[3];
        private final float[] v = new float[9];

        SvdDecomposition3f() {
        }

        private float SIGN(float float0, float float1) {
            return float1 >= 0.0 ? Math.abs(float0) : -Math.abs(float0);
        }

        void svd(float[] floats, int int31, Matrix3f matrix3f0, Matrix3f matrix3f1) {
            int int0 = 0;
            int int1 = 0;
            float float0 = 0.0F;
            float float1 = 0.0F;
            float float2 = 0.0F;

            for (int int2 = 0; int2 < 3; int2++) {
                int0 = int2 + 1;
                this.rv1[int2] = float2 * float1;
                float2 = 0.0F;
                float float3 = 0.0F;
                float1 = 0.0F;

                for (int int3 = int2; int3 < 3; int3++) {
                    float2 += Math.abs(floats[int3 + 3 * int2]);
                }

                if (float2 != 0.0F) {
                    for (int int4 = int2; int4 < 3; int4++) {
                        floats[int4 + 3 * int2] = floats[int4 + 3 * int2] / float2;
                        float3 += floats[int4 + 3 * int2] * floats[int4 + 3 * int2];
                    }

                    float float4 = floats[int2 + 3 * int2];
                    float1 = -this.SIGN(Math.sqrt(float3), float4);
                    float float5 = float4 * float1 - float3;
                    floats[int2 + 3 * int2] = float4 - float1;
                    if (int2 != 2) {
                        for (int int5 = int0; int5 < 3; int5++) {
                            float3 = 0.0F;

                            for (int int6 = int2; int6 < 3; int6++) {
                                float3 += floats[int6 + 3 * int2] * floats[int6 + 3 * int5];
                            }

                            float4 = float3 / float5;

                            for (int int7 = int2; int7 < 3; int7++) {
                                floats[int7 + 3 * int5] = floats[int7 + 3 * int5] + float4 * floats[int7 + 3 * int2];
                            }
                        }
                    }

                    for (int int8 = int2; int8 < 3; int8++) {
                        floats[int8 + 3 * int2] = floats[int8 + 3 * int2] * float2;
                    }
                }

                this.w[int2] = float2 * float1;
                float2 = 0.0F;
                float3 = 0.0F;
                float1 = 0.0F;
                if (int2 < 3 && int2 != 2) {
                    for (int int9 = int0; int9 < 3; int9++) {
                        float2 += Math.abs(floats[int2 + 3 * int9]);
                    }

                    if (float2 != 0.0F) {
                        for (int int10 = int0; int10 < 3; int10++) {
                            floats[int2 + 3 * int10] = floats[int2 + 3 * int10] / float2;
                            float3 += floats[int2 + 3 * int10] * floats[int2 + 3 * int10];
                        }

                        float float6 = floats[int2 + 3 * int0];
                        float1 = -this.SIGN(Math.sqrt(float3), float6);
                        float float7 = float6 * float1 - float3;
                        floats[int2 + 3 * int0] = float6 - float1;

                        for (int int11 = int0; int11 < 3; int11++) {
                            this.rv1[int11] = floats[int2 + 3 * int11] / float7;
                        }

                        if (int2 != 2) {
                            for (int int12 = int0; int12 < 3; int12++) {
                                float3 = 0.0F;

                                for (int int13 = int0; int13 < 3; int13++) {
                                    float3 += floats[int12 + 3 * int13] * floats[int2 + 3 * int13];
                                }

                                for (int int14 = int0; int14 < 3; int14++) {
                                    floats[int12 + 3 * int14] = floats[int12 + 3 * int14] + float3 * this.rv1[int14];
                                }
                            }
                        }

                        for (int int15 = int0; int15 < 3; int15++) {
                            floats[int2 + 3 * int15] = floats[int2 + 3 * int15] * float2;
                        }
                    }
                }

                float0 = Math.max(float0, Math.abs(this.w[int2]) + Math.abs(this.rv1[int2]));
            }

            for (int int16 = 2; int16 >= 0; int0 = int16--) {
                if (int16 < 2) {
                    if (float1 != 0.0F) {
                        for (int int17 = int0; int17 < 3; int17++) {
                            this.v[int17 + 3 * int16] = floats[int16 + 3 * int17] / floats[int16 + 3 * int0] / float1;
                        }

                        for (int int18 = int0; int18 < 3; int18++) {
                            float float8 = 0.0F;

                            for (int int19 = int0; int19 < 3; int19++) {
                                float8 += floats[int16 + 3 * int19] * this.v[int19 + 3 * int18];
                            }

                            for (int int20 = int0; int20 < 3; int20++) {
                                this.v[int20 + 3 * int18] = this.v[int20 + 3 * int18] + float8 * this.v[int20 + 3 * int16];
                            }
                        }
                    }

                    for (int int21 = int0; int21 < 3; int21++) {
                        this.v[int16 + 3 * int21] = this.v[int21 + 3 * int16] = 0.0F;
                    }
                }

                this.v[int16 + 3 * int16] = 1.0F;
                float1 = this.rv1[int16];
            }

            for (int int22 = 2; int22 >= 0; int22--) {
                int0 = int22 + 1;
                float1 = this.w[int22];
                if (int22 < 2) {
                    for (int int23 = int0; int23 < 3; int23++) {
                        floats[int22 + 3 * int23] = 0.0F;
                    }
                }

                if (float1 == 0.0F) {
                    for (int int24 = int22; int24 < 3; int24++) {
                        floats[int24 + 3 * int22] = 0.0F;
                    }
                } else {
                    float1 = 1.0F / float1;
                    if (int22 != 2) {
                        for (int int25 = int0; int25 < 3; int25++) {
                            float float9 = 0.0F;

                            for (int int26 = int0; int26 < 3; int26++) {
                                float9 += floats[int26 + 3 * int22] * floats[int26 + 3 * int25];
                            }

                            float float10 = float9 / floats[int22 + 3 * int22] * float1;

                            for (int int27 = int22; int27 < 3; int27++) {
                                floats[int27 + 3 * int25] = floats[int27 + 3 * int25] + float10 * floats[int27 + 3 * int22];
                            }
                        }
                    }

                    for (int int28 = int22; int28 < 3; int28++) {
                        floats[int28 + 3 * int22] = floats[int28 + 3 * int22] * float1;
                    }
                }

                floats[int22 + 3 * int22]++;
            }

            for (int int29 = 2; int29 >= 0; int29--) {
                for (int int30 = 0; int30 < int31; int30++) {
                    boolean boolean0 = true;

                    for (int0 = int29; int0 >= 0; int0--) {
                        int1 = int0 - 1;
                        if (Math.abs(this.rv1[int0]) + float0 == float0) {
                            boolean0 = false;
                            break;
                        }

                        if (Math.abs(this.w[int1]) + float0 == float0) {
                            break;
                        }
                    }

                    if (boolean0) {
                        float float11 = 0.0F;
                        float float12 = 1.0F;

                        for (int int32 = int0; int32 <= int29; int32++) {
                            float float13 = float12 * this.rv1[int32];
                            if (Math.abs(float13) + float0 != float0) {
                                float1 = this.w[int32];
                                float float14 = PYTHAG(float13, float1);
                                this.w[int32] = float14;
                                float14 = 1.0F / float14;
                                float11 = float1 * float14;
                                float12 = -float13 * float14;

                                for (int int33 = 0; int33 < 3; int33++) {
                                    float float15 = floats[int33 + 3 * int1];
                                    float float16 = floats[int33 + 3 * int32];
                                    floats[int33 + 3 * int1] = float15 * float11 + float16 * float12;
                                    floats[int33 + 3 * int32] = float16 * float11 - float15 * float12;
                                }
                            }
                        }
                    }

                    float float17 = this.w[int29];
                    if (int0 == int29) {
                        if (!(float17 < 0.0F)) {
                            break;
                        }

                        this.w[int29] = -float17;

                        for (int int34 = 0; int34 < 3; int34++) {
                            this.v[int34 + 3 * int29] = -this.v[int34 + 3 * int29];
                        }
                        break;
                    }

                    if (int30 == int31 - 1) {
                        throw new RuntimeException("No convergence after " + int31 + " iterations");
                    }

                    float float18 = this.w[int0];
                    int1 = int29 - 1;
                    float float19 = this.w[int1];
                    float1 = this.rv1[int1];
                    float float20 = this.rv1[int29];
                    float float21 = ((float19 - float17) * (float19 + float17) + (float1 - float20) * (float1 + float20)) / (2.0F * float20 * float19);
                    float1 = PYTHAG(float21, 1.0F);
                    float21 = ((float18 - float17) * (float18 + float17) + float20 * (float19 / (float21 + this.SIGN(float1, float21)) - float20)) / float18;
                    float float22 = 1.0F;
                    float float23 = 1.0F;

                    for (int int35 = int0; int35 <= int1; int35++) {
                        int int36 = int35 + 1;
                        float1 = this.rv1[int36];
                        float19 = this.w[int36];
                        float20 = float22 * float1;
                        float1 = float23 * float1;
                        float17 = PYTHAG(float21, float20);
                        this.rv1[int35] = float17;
                        float23 = float21 / float17;
                        float22 = float20 / float17;
                        float21 = float18 * float23 + float1 * float22;
                        float1 = float1 * float23 - float18 * float22;
                        float20 = float19 * float22;
                        float19 *= float23;

                        for (int int37 = 0; int37 < 3; int37++) {
                            float18 = this.v[int37 + 3 * int35];
                            float17 = this.v[int37 + 3 * int36];
                            this.v[int37 + 3 * int35] = float18 * float23 + float17 * float22;
                            this.v[int37 + 3 * int36] = float17 * float23 - float18 * float22;
                        }

                        float17 = PYTHAG(float21, float20);
                        this.w[int35] = float17;
                        if (float17 != 0.0F) {
                            float17 = 1.0F / float17;
                            float23 = float21 * float17;
                            float22 = float20 * float17;
                        }

                        float21 = float23 * float1 + float22 * float19;
                        float18 = float23 * float19 - float22 * float1;

                        for (int int38 = 0; int38 < 3; int38++) {
                            float19 = floats[int38 + 3 * int35];
                            float17 = floats[int38 + 3 * int36];
                            floats[int38 + 3 * int35] = float19 * float23 + float17 * float22;
                            floats[int38 + 3 * int36] = float17 * float23 - float19 * float22;
                        }
                    }

                    this.rv1[int0] = 0.0F;
                    this.rv1[int29] = float21;
                    this.w[int29] = float18;
                }
            }

            matrix3f0.set(floats);
            matrix3f1.set(this.v);
        }

        private static float PYTHAG(float float1, float float3) {
            float float0 = Math.abs(float1);
            float float2 = Math.abs(float3);
            float float4;
            if (float0 > float2) {
                float float5 = float2 / float0;
                float4 = float0 * (float)Math.sqrt(1.0 + float5 * float5);
            } else if (float2 > 0.0F) {
                float float6 = float0 / float2;
                float4 = float2 * (float)Math.sqrt(1.0 + float6 * float6);
            } else {
                float4 = 0.0F;
            }

            return float4;
        }
    }
}
