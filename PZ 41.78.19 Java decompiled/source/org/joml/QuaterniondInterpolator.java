// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

public class QuaterniondInterpolator {
    private final QuaterniondInterpolator.SvdDecomposition3d svdDecomposition3d = new QuaterniondInterpolator.SvdDecomposition3d();
    private final double[] m = new double[9];
    private final Matrix3d u = new Matrix3d();
    private final Matrix3d v = new Matrix3d();

    public Quaterniond computeWeightedAverage(Quaterniond[] quaternionds, double[] doubles, int int1, Quaterniond quaterniond1) {
        double double0 = 0.0;
        double double1 = 0.0;
        double double2 = 0.0;
        double double3 = 0.0;
        double double4 = 0.0;
        double double5 = 0.0;
        double double6 = 0.0;
        double double7 = 0.0;
        double double8 = 0.0;

        for (int int0 = 0; int0 < quaternionds.length; int0++) {
            Quaterniond quaterniond0 = quaternionds[int0];
            double double9 = quaterniond0.x + quaterniond0.x;
            double double10 = quaterniond0.y + quaterniond0.y;
            double double11 = quaterniond0.z + quaterniond0.z;
            double double12 = double9 * quaterniond0.x;
            double double13 = double10 * quaterniond0.y;
            double double14 = double11 * quaterniond0.z;
            double double15 = double9 * quaterniond0.y;
            double double16 = double9 * quaterniond0.z;
            double double17 = double9 * quaterniond0.w;
            double double18 = double10 * quaterniond0.z;
            double double19 = double10 * quaterniond0.w;
            double double20 = double11 * quaterniond0.w;
            double0 += doubles[int0] * (1.0 - double13 - double14);
            double1 += doubles[int0] * (double15 + double20);
            double2 += doubles[int0] * (double16 - double19);
            double3 += doubles[int0] * (double15 - double20);
            double4 += doubles[int0] * (1.0 - double14 - double12);
            double5 += doubles[int0] * (double18 + double17);
            double6 += doubles[int0] * (double16 + double19);
            double7 += doubles[int0] * (double18 - double17);
            double8 += doubles[int0] * (1.0 - double13 - double12);
        }

        this.m[0] = double0;
        this.m[1] = double1;
        this.m[2] = double2;
        this.m[3] = double3;
        this.m[4] = double4;
        this.m[5] = double5;
        this.m[6] = double6;
        this.m[7] = double7;
        this.m[8] = double8;
        this.svdDecomposition3d.svd(this.m, int1, this.u, this.v);
        this.u.mul(this.v.transpose());
        return quaterniond1.setFromNormalized(this.u).normalize();
    }

    private static class SvdDecomposition3d {
        private final double[] rv1 = new double[3];
        private final double[] w = new double[3];
        private final double[] v = new double[9];

        SvdDecomposition3d() {
        }

        private double SIGN(double double0, double double1) {
            return double1 >= 0.0 ? Math.abs(double0) : -Math.abs(double0);
        }

        void svd(double[] doubles, int int31, Matrix3d matrix3d0, Matrix3d matrix3d1) {
            int int0 = 0;
            int int1 = 0;
            double double0 = 0.0;
            double double1 = 0.0;
            double double2 = 0.0;

            for (int int2 = 0; int2 < 3; int2++) {
                int0 = int2 + 1;
                this.rv1[int2] = double2 * double1;
                double2 = 0.0;
                double double3 = 0.0;
                double1 = 0.0;

                for (int int3 = int2; int3 < 3; int3++) {
                    double2 += Math.abs(doubles[int3 + 3 * int2]);
                }

                if (double2 != 0.0) {
                    for (int int4 = int2; int4 < 3; int4++) {
                        doubles[int4 + 3 * int2] = doubles[int4 + 3 * int2] / double2;
                        double3 += doubles[int4 + 3 * int2] * doubles[int4 + 3 * int2];
                    }

                    double double4 = doubles[int2 + 3 * int2];
                    double1 = -this.SIGN(Math.sqrt(double3), double4);
                    double double5 = double4 * double1 - double3;
                    doubles[int2 + 3 * int2] = double4 - double1;
                    if (int2 != 2) {
                        for (int int5 = int0; int5 < 3; int5++) {
                            double3 = 0.0;

                            for (int int6 = int2; int6 < 3; int6++) {
                                double3 += doubles[int6 + 3 * int2] * doubles[int6 + 3 * int5];
                            }

                            double4 = double3 / double5;

                            for (int int7 = int2; int7 < 3; int7++) {
                                doubles[int7 + 3 * int5] = doubles[int7 + 3 * int5] + double4 * doubles[int7 + 3 * int2];
                            }
                        }
                    }

                    for (int int8 = int2; int8 < 3; int8++) {
                        doubles[int8 + 3 * int2] = doubles[int8 + 3 * int2] * double2;
                    }
                }

                this.w[int2] = double2 * double1;
                double2 = 0.0;
                double3 = 0.0;
                double1 = 0.0;
                if (int2 < 3 && int2 != 2) {
                    for (int int9 = int0; int9 < 3; int9++) {
                        double2 += Math.abs(doubles[int2 + 3 * int9]);
                    }

                    if (double2 != 0.0) {
                        for (int int10 = int0; int10 < 3; int10++) {
                            doubles[int2 + 3 * int10] = doubles[int2 + 3 * int10] / double2;
                            double3 += doubles[int2 + 3 * int10] * doubles[int2 + 3 * int10];
                        }

                        double double6 = doubles[int2 + 3 * int0];
                        double1 = -this.SIGN(Math.sqrt(double3), double6);
                        double double7 = double6 * double1 - double3;
                        doubles[int2 + 3 * int0] = double6 - double1;

                        for (int int11 = int0; int11 < 3; int11++) {
                            this.rv1[int11] = doubles[int2 + 3 * int11] / double7;
                        }

                        if (int2 != 2) {
                            for (int int12 = int0; int12 < 3; int12++) {
                                double3 = 0.0;

                                for (int int13 = int0; int13 < 3; int13++) {
                                    double3 += doubles[int12 + 3 * int13] * doubles[int2 + 3 * int13];
                                }

                                for (int int14 = int0; int14 < 3; int14++) {
                                    doubles[int12 + 3 * int14] = doubles[int12 + 3 * int14] + double3 * this.rv1[int14];
                                }
                            }
                        }

                        for (int int15 = int0; int15 < 3; int15++) {
                            doubles[int2 + 3 * int15] = doubles[int2 + 3 * int15] * double2;
                        }
                    }
                }

                double0 = Math.max(double0, Math.abs(this.w[int2]) + Math.abs(this.rv1[int2]));
            }

            for (int int16 = 2; int16 >= 0; int0 = int16--) {
                if (int16 < 2) {
                    if (double1 != 0.0) {
                        for (int int17 = int0; int17 < 3; int17++) {
                            this.v[int17 + 3 * int16] = doubles[int16 + 3 * int17] / doubles[int16 + 3 * int0] / double1;
                        }

                        for (int int18 = int0; int18 < 3; int18++) {
                            double double8 = 0.0;

                            for (int int19 = int0; int19 < 3; int19++) {
                                double8 += doubles[int16 + 3 * int19] * this.v[int19 + 3 * int18];
                            }

                            for (int int20 = int0; int20 < 3; int20++) {
                                this.v[int20 + 3 * int18] = this.v[int20 + 3 * int18] + double8 * this.v[int20 + 3 * int16];
                            }
                        }
                    }

                    for (int int21 = int0; int21 < 3; int21++) {
                        this.v[int16 + 3 * int21] = this.v[int21 + 3 * int16] = 0.0;
                    }
                }

                this.v[int16 + 3 * int16] = 1.0;
                double1 = this.rv1[int16];
            }

            for (int int22 = 2; int22 >= 0; int22--) {
                int0 = int22 + 1;
                double1 = this.w[int22];
                if (int22 < 2) {
                    for (int int23 = int0; int23 < 3; int23++) {
                        doubles[int22 + 3 * int23] = 0.0;
                    }
                }

                if (double1 == 0.0) {
                    for (int int24 = int22; int24 < 3; int24++) {
                        doubles[int24 + 3 * int22] = 0.0;
                    }
                } else {
                    double1 = 1.0 / double1;
                    if (int22 != 2) {
                        for (int int25 = int0; int25 < 3; int25++) {
                            double double9 = 0.0;

                            for (int int26 = int0; int26 < 3; int26++) {
                                double9 += doubles[int26 + 3 * int22] * doubles[int26 + 3 * int25];
                            }

                            double double10 = double9 / doubles[int22 + 3 * int22] * double1;

                            for (int int27 = int22; int27 < 3; int27++) {
                                doubles[int27 + 3 * int25] = doubles[int27 + 3 * int25] + double10 * doubles[int27 + 3 * int22];
                            }
                        }
                    }

                    for (int int28 = int22; int28 < 3; int28++) {
                        doubles[int28 + 3 * int22] = doubles[int28 + 3 * int22] * double1;
                    }
                }

                doubles[int22 + 3 * int22]++;
            }

            for (int int29 = 2; int29 >= 0; int29--) {
                for (int int30 = 0; int30 < int31; int30++) {
                    boolean boolean0 = true;

                    for (int0 = int29; int0 >= 0; int0--) {
                        int1 = int0 - 1;
                        if (Math.abs(this.rv1[int0]) + double0 == double0) {
                            boolean0 = false;
                            break;
                        }

                        if (Math.abs(this.w[int1]) + double0 == double0) {
                            break;
                        }
                    }

                    if (boolean0) {
                        double double11 = 0.0;
                        double double12 = 1.0;

                        for (int int32 = int0; int32 <= int29; int32++) {
                            double double13 = double12 * this.rv1[int32];
                            if (Math.abs(double13) + double0 != double0) {
                                double1 = this.w[int32];
                                double double14 = PYTHAG(double13, double1);
                                this.w[int32] = double14;
                                double14 = 1.0 / double14;
                                double11 = double1 * double14;
                                double12 = -double13 * double14;

                                for (int int33 = 0; int33 < 3; int33++) {
                                    double double15 = doubles[int33 + 3 * int1];
                                    double double16 = doubles[int33 + 3 * int32];
                                    doubles[int33 + 3 * int1] = double15 * double11 + double16 * double12;
                                    doubles[int33 + 3 * int32] = double16 * double11 - double15 * double12;
                                }
                            }
                        }
                    }

                    double double17 = this.w[int29];
                    if (int0 == int29) {
                        if (!(double17 < 0.0)) {
                            break;
                        }

                        this.w[int29] = -double17;

                        for (int int34 = 0; int34 < 3; int34++) {
                            this.v[int34 + 3 * int29] = -this.v[int34 + 3 * int29];
                        }
                        break;
                    }

                    if (int30 == int31 - 1) {
                        throw new RuntimeException("No convergence after " + int31 + " iterations");
                    }

                    double double18 = this.w[int0];
                    int1 = int29 - 1;
                    double double19 = this.w[int1];
                    double1 = this.rv1[int1];
                    double double20 = this.rv1[int29];
                    double double21 = ((double19 - double17) * (double19 + double17) + (double1 - double20) * (double1 + double20))
                        / (2.0 * double20 * double19);
                    double1 = PYTHAG(double21, 1.0);
                    double21 = ((double18 - double17) * (double18 + double17) + double20 * (double19 / (double21 + this.SIGN(double1, double21)) - double20))
                        / double18;
                    double double22 = 1.0;
                    double double23 = 1.0;

                    for (int int35 = int0; int35 <= int1; int35++) {
                        int int36 = int35 + 1;
                        double1 = this.rv1[int36];
                        double19 = this.w[int36];
                        double20 = double22 * double1;
                        double1 = double23 * double1;
                        double17 = PYTHAG(double21, double20);
                        this.rv1[int35] = double17;
                        double23 = double21 / double17;
                        double22 = double20 / double17;
                        double21 = double18 * double23 + double1 * double22;
                        double1 = double1 * double23 - double18 * double22;
                        double20 = double19 * double22;
                        double19 *= double23;

                        for (int int37 = 0; int37 < 3; int37++) {
                            double18 = this.v[int37 + 3 * int35];
                            double17 = this.v[int37 + 3 * int36];
                            this.v[int37 + 3 * int35] = double18 * double23 + double17 * double22;
                            this.v[int37 + 3 * int36] = double17 * double23 - double18 * double22;
                        }

                        double17 = PYTHAG(double21, double20);
                        this.w[int35] = double17;
                        if (double17 != 0.0) {
                            double17 = 1.0 / double17;
                            double23 = double21 * double17;
                            double22 = double20 * double17;
                        }

                        double21 = double23 * double1 + double22 * double19;
                        double18 = double23 * double19 - double22 * double1;

                        for (int int38 = 0; int38 < 3; int38++) {
                            double19 = doubles[int38 + 3 * int35];
                            double17 = doubles[int38 + 3 * int36];
                            doubles[int38 + 3 * int35] = double19 * double23 + double17 * double22;
                            doubles[int38 + 3 * int36] = double17 * double23 - double19 * double22;
                        }
                    }

                    this.rv1[int0] = 0.0;
                    this.rv1[int29] = double21;
                    this.w[int29] = double18;
                }
            }

            matrix3d0.set(doubles);
            matrix3d1.set(this.v);
        }

        private static double PYTHAG(double double1, double double3) {
            double double0 = Math.abs(double1);
            double double2 = Math.abs(double3);
            double double4;
            if (double0 > double2) {
                double double5 = double2 / double0;
                double4 = double0 * Math.sqrt(1.0 + double5 * double5);
            } else if (double2 > 0.0) {
                double double6 = double0 / double2;
                double4 = double2 * Math.sqrt(1.0 + double6 * double6);
            } else {
                double4 = 0.0;
            }

            return double4;
        }
    }
}
