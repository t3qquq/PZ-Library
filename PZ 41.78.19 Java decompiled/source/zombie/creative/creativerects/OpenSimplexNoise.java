// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.creative.creativerects;

public class OpenSimplexNoise {
    private static final double STRETCH_CONSTANT_2D = -0.211324865405187;
    private static final double SQUISH_CONSTANT_2D = 0.366025403784439;
    private static final double STRETCH_CONSTANT_3D = -0.16666666666666666;
    private static final double SQUISH_CONSTANT_3D = 0.3333333333333333;
    private static final double STRETCH_CONSTANT_4D = -0.138196601125011;
    private static final double SQUISH_CONSTANT_4D = 0.309016994374947;
    private static final double NORM_CONSTANT_2D = 47.0;
    private static final double NORM_CONSTANT_3D = 103.0;
    private static final double NORM_CONSTANT_4D = 30.0;
    private static final long DEFAULT_SEED = 0L;
    private short[] perm;
    private short[] permGradIndex3D;
    private static byte[] gradients2D = new byte[]{5, 2, 2, 5, -5, 2, -2, 5, 5, -2, 2, -5, -5, -2, -2, -5};
    private static byte[] gradients3D = new byte[]{
        -11,
        4,
        4,
        -4,
        11,
        4,
        -4,
        4,
        11,
        11,
        4,
        4,
        4,
        11,
        4,
        4,
        4,
        11,
        -11,
        -4,
        4,
        -4,
        -11,
        4,
        -4,
        -4,
        11,
        11,
        -4,
        4,
        4,
        -11,
        4,
        4,
        -4,
        11,
        -11,
        4,
        -4,
        -4,
        11,
        -4,
        -4,
        4,
        -11,
        11,
        4,
        -4,
        4,
        11,
        -4,
        4,
        4,
        -11,
        -11,
        -4,
        -4,
        -4,
        -11,
        -4,
        -4,
        -4,
        -11,
        11,
        -4,
        -4,
        4,
        -11,
        -4,
        4,
        -4,
        -11
    };
    private static byte[] gradients4D = new byte[]{
        3,
        1,
        1,
        1,
        1,
        3,
        1,
        1,
        1,
        1,
        3,
        1,
        1,
        1,
        1,
        3,
        -3,
        1,
        1,
        1,
        -1,
        3,
        1,
        1,
        -1,
        1,
        3,
        1,
        -1,
        1,
        1,
        3,
        3,
        -1,
        1,
        1,
        1,
        -3,
        1,
        1,
        1,
        -1,
        3,
        1,
        1,
        -1,
        1,
        3,
        -3,
        -1,
        1,
        1,
        -1,
        -3,
        1,
        1,
        -1,
        -1,
        3,
        1,
        -1,
        -1,
        1,
        3,
        3,
        1,
        -1,
        1,
        1,
        3,
        -1,
        1,
        1,
        1,
        -3,
        1,
        1,
        1,
        -1,
        3,
        -3,
        1,
        -1,
        1,
        -1,
        3,
        -1,
        1,
        -1,
        1,
        -3,
        1,
        -1,
        1,
        -1,
        3,
        3,
        -1,
        -1,
        1,
        1,
        -3,
        -1,
        1,
        1,
        -1,
        -3,
        1,
        1,
        -1,
        -1,
        3,
        -3,
        -1,
        -1,
        1,
        -1,
        -3,
        -1,
        1,
        -1,
        -1,
        -3,
        1,
        -1,
        -1,
        -1,
        3,
        3,
        1,
        1,
        -1,
        1,
        3,
        1,
        -1,
        1,
        1,
        3,
        -1,
        1,
        1,
        1,
        -3,
        -3,
        1,
        1,
        -1,
        -1,
        3,
        1,
        -1,
        -1,
        1,
        3,
        -1,
        -1,
        1,
        1,
        -3,
        3,
        -1,
        1,
        -1,
        1,
        -3,
        1,
        -1,
        1,
        -1,
        3,
        -1,
        1,
        -1,
        1,
        -3,
        -3,
        -1,
        1,
        -1,
        -1,
        -3,
        1,
        -1,
        -1,
        -1,
        3,
        -1,
        -1,
        -1,
        1,
        -3,
        3,
        1,
        -1,
        -1,
        1,
        3,
        -1,
        -1,
        1,
        1,
        -3,
        -1,
        1,
        1,
        -1,
        -3,
        -3,
        1,
        -1,
        -1,
        -1,
        3,
        -1,
        -1,
        -1,
        1,
        -3,
        -1,
        -1,
        1,
        -1,
        -3,
        3,
        -1,
        -1,
        -1,
        1,
        -3,
        -1,
        -1,
        1,
        -1,
        -3,
        -1,
        1,
        -1,
        -1,
        -3,
        -3,
        -1,
        -1,
        -1,
        -1,
        -3,
        -1,
        -1,
        -1,
        -1,
        -3,
        -1,
        -1,
        -1,
        -1,
        -3
    };

    public OpenSimplexNoise() {
        this(0L);
    }

    public OpenSimplexNoise(short[] shorts) {
        this.perm = shorts;
        this.permGradIndex3D = new short[256];

        for (int int0 = 0; int0 < 256; int0++) {
            this.permGradIndex3D[int0] = (short)(shorts[int0] % (gradients3D.length / 3) * 3);
        }
    }

    public OpenSimplexNoise(long long0) {
        this.perm = new short[256];
        this.permGradIndex3D = new short[256];
        short[] shorts = new short[256];
        short short0 = 0;

        while (short0 < 256) {
            shorts[short0] = short0++;
        }

        long0 = long0 * 6364136223846793005L + 1442695040888963407L;
        long0 = long0 * 6364136223846793005L + 1442695040888963407L;
        long0 = long0 * 6364136223846793005L + 1442695040888963407L;

        for (int int0 = 255; int0 >= 0; int0--) {
            long0 = long0 * 6364136223846793005L + 1442695040888963407L;
            int int1 = (int)((long0 + 31L) % (int0 + 1));
            if (int1 < 0) {
                int1 += int0 + 1;
            }

            this.perm[int0] = shorts[int1];
            this.permGradIndex3D[int0] = (short)(this.perm[int0] % (gradients3D.length / 3) * 3);
            shorts[int1] = shorts[int0];
        }
    }

    public double eval(double double1, double double2) {
        double double0 = (double1 + double2) * -0.211324865405187;
        double double3 = double1 + double0;
        double double4 = double2 + double0;
        int int0 = fastFloor(double3);
        int int1 = fastFloor(double4);
        double double5 = (int0 + int1) * 0.366025403784439;
        double double6 = int0 + double5;
        double double7 = int1 + double5;
        double double8 = double3 - int0;
        double double9 = double4 - int1;
        double double10 = double8 + double9;
        double double11 = double1 - double6;
        double double12 = double2 - double7;
        double double13 = 0.0;
        double double14 = double11 - 1.0 - 0.366025403784439;
        double double15 = double12 - 0.0 - 0.366025403784439;
        double double16 = 2.0 - double14 * double14 - double15 * double15;
        if (double16 > 0.0) {
            double16 *= double16;
            double13 += double16 * double16 * this.extrapolate(int0 + 1, int1 + 0, double14, double15);
        }

        double double17 = double11 - 0.0 - 0.366025403784439;
        double double18 = double12 - 1.0 - 0.366025403784439;
        double double19 = 2.0 - double17 * double17 - double18 * double18;
        if (double19 > 0.0) {
            double19 *= double19;
            double13 += double19 * double19 * this.extrapolate(int0 + 0, int1 + 1, double17, double18);
        }

        double double20;
        double double21;
        int int2;
        int int3;
        if (double10 <= 1.0) {
            double double22 = 1.0 - double10;
            if (!(double22 > double8) && !(double22 > double9)) {
                int2 = int0 + 1;
                int3 = int1 + 1;
                double20 = double11 - 1.0 - 0.732050807568878;
                double21 = double12 - 1.0 - 0.732050807568878;
            } else if (double8 > double9) {
                int2 = int0 + 1;
                int3 = int1 - 1;
                double20 = double11 - 1.0;
                double21 = double12 + 1.0;
            } else {
                int2 = int0 - 1;
                int3 = int1 + 1;
                double20 = double11 + 1.0;
                double21 = double12 - 1.0;
            }
        } else {
            double double23 = 2.0 - double10;
            if (!(double23 < double8) && !(double23 < double9)) {
                double20 = double11;
                double21 = double12;
                int2 = int0;
                int3 = int1;
            } else if (double8 > double9) {
                int2 = int0 + 2;
                int3 = int1 + 0;
                double20 = double11 - 2.0 - 0.732050807568878;
                double21 = double12 + 0.0 - 0.732050807568878;
            } else {
                int2 = int0 + 0;
                int3 = int1 + 2;
                double20 = double11 + 0.0 - 0.732050807568878;
                double21 = double12 - 2.0 - 0.732050807568878;
            }

            int0++;
            int1++;
            double11 = double11 - 1.0 - 0.732050807568878;
            double12 = double12 - 1.0 - 0.732050807568878;
        }

        double double24 = 2.0 - double11 * double11 - double12 * double12;
        if (double24 > 0.0) {
            double24 *= double24;
            double13 += double24 * double24 * this.extrapolate(int0, int1, double11, double12);
        }

        double double25 = 2.0 - double20 * double20 - double21 * double21;
        if (double25 > 0.0) {
            double25 *= double25;
            double13 += double25 * double25 * this.extrapolate(int2, int3, double20, double21);
        }

        return double13 / 47.0;
    }

    public double eval(double double2, double double3, double double1) {
        double double0 = (double2 + double3 + double1) * -0.16666666666666666;
        double double4 = double2 + double0;
        double double5 = double3 + double0;
        double double6 = double1 + double0;
        int int0 = fastFloor(double4);
        int int1 = fastFloor(double5);
        int int2 = fastFloor(double6);
        double double7 = (int0 + int1 + int2) * 0.3333333333333333;
        double double8 = int0 + double7;
        double double9 = int1 + double7;
        double double10 = int2 + double7;
        double double11 = double4 - int0;
        double double12 = double5 - int1;
        double double13 = double6 - int2;
        double double14 = double11 + double12 + double13;
        double double15 = double2 - double8;
        double double16 = double3 - double9;
        double double17 = double1 - double10;
        double double18 = 0.0;
        double double19;
        double double20;
        double double21;
        double double22;
        double double23;
        double double24;
        int int3;
        int int4;
        int int5;
        int int6;
        int int7;
        int int8;
        if (double14 <= 1.0) {
            byte byte0 = 1;
            double double25 = double11;
            byte byte1 = 2;
            double double26 = double12;
            if (double11 >= double12 && double13 > double12) {
                double26 = double13;
                byte1 = 4;
            } else if (double11 < double12 && double13 > double11) {
                double25 = double13;
                byte0 = 4;
            }

            double double27 = 1.0 - double14;
            if (!(double27 > double25) && !(double27 > double26)) {
                byte byte2 = (byte)(byte0 | byte1);
                if ((byte2 & 1) == 0) {
                    int3 = int0;
                    int6 = int0 - 1;
                    double19 = double15 - 0.6666666666666666;
                    double22 = double15 + 1.0 - 0.3333333333333333;
                } else {
                    int3 = int6 = int0 + 1;
                    double19 = double15 - 1.0 - 0.6666666666666666;
                    double22 = double15 - 1.0 - 0.3333333333333333;
                }

                if ((byte2 & 2) == 0) {
                    int4 = int1;
                    int7 = int1 - 1;
                    double20 = double16 - 0.6666666666666666;
                    double23 = double16 + 1.0 - 0.3333333333333333;
                } else {
                    int4 = int7 = int1 + 1;
                    double20 = double16 - 1.0 - 0.6666666666666666;
                    double23 = double16 - 1.0 - 0.3333333333333333;
                }

                if ((byte2 & 4) == 0) {
                    int5 = int2;
                    int8 = int2 - 1;
                    double21 = double17 - 0.6666666666666666;
                    double24 = double17 + 1.0 - 0.3333333333333333;
                } else {
                    int5 = int8 = int2 + 1;
                    double21 = double17 - 1.0 - 0.6666666666666666;
                    double24 = double17 - 1.0 - 0.3333333333333333;
                }
            } else {
                byte byte3 = double26 > double25 ? byte1 : byte0;
                if ((byte3 & 1) == 0) {
                    int3 = int0 - 1;
                    int6 = int0;
                    double19 = double15 + 1.0;
                    double22 = double15;
                } else {
                    int3 = int6 = int0 + 1;
                    double19 = double22 = double15 - 1.0;
                }

                if ((byte3 & 2) == 0) {
                    int7 = int1;
                    int4 = int1;
                    double23 = double16;
                    double20 = double16;
                    if ((byte3 & 1) == 0) {
                        int7 = int1 - 1;
                        double23 = double16 + 1.0;
                    } else {
                        int4 = int1 - 1;
                        double20 = double16 + 1.0;
                    }
                } else {
                    int4 = int7 = int1 + 1;
                    double20 = double23 = double16 - 1.0;
                }

                if ((byte3 & 4) == 0) {
                    int5 = int2;
                    int8 = int2 - 1;
                    double21 = double17;
                    double24 = double17 + 1.0;
                } else {
                    int5 = int8 = int2 + 1;
                    double21 = double24 = double17 - 1.0;
                }
            }

            double double28 = 2.0 - double15 * double15 - double16 * double16 - double17 * double17;
            if (double28 > 0.0) {
                double28 *= double28;
                double18 += double28 * double28 * this.extrapolate(int0 + 0, int1 + 0, int2 + 0, double15, double16, double17);
            }

            double double29 = double15 - 1.0 - 0.3333333333333333;
            double double30 = double16 - 0.0 - 0.3333333333333333;
            double double31 = double17 - 0.0 - 0.3333333333333333;
            double double32 = 2.0 - double29 * double29 - double30 * double30 - double31 * double31;
            if (double32 > 0.0) {
                double32 *= double32;
                double18 += double32 * double32 * this.extrapolate(int0 + 1, int1 + 0, int2 + 0, double29, double30, double31);
            }

            double double33 = double15 - 0.0 - 0.3333333333333333;
            double double34 = double16 - 1.0 - 0.3333333333333333;
            double double35 = 2.0 - double33 * double33 - double34 * double34 - double31 * double31;
            if (double35 > 0.0) {
                double35 *= double35;
                double18 += double35 * double35 * this.extrapolate(int0 + 0, int1 + 1, int2 + 0, double33, double34, double31);
            }

            double double36 = double17 - 1.0 - 0.3333333333333333;
            double double37 = 2.0 - double33 * double33 - double30 * double30 - double36 * double36;
            if (double37 > 0.0) {
                double37 *= double37;
                double18 += double37 * double37 * this.extrapolate(int0 + 0, int1 + 0, int2 + 1, double33, double30, double36);
            }
        } else if (double14 >= 2.0) {
            byte byte4 = 6;
            double double38 = double11;
            byte byte5 = 5;
            double double39 = double12;
            if (double11 <= double12 && double13 < double12) {
                double39 = double13;
                byte5 = 3;
            } else if (double11 > double12 && double13 < double11) {
                double38 = double13;
                byte4 = 3;
            }

            double double40 = 3.0 - double14;
            if (!(double40 < double38) && !(double40 < double39)) {
                byte byte6 = (byte)(byte4 & byte5);
                if ((byte6 & 1) != 0) {
                    int3 = int0 + 1;
                    int6 = int0 + 2;
                    double19 = double15 - 1.0 - 0.3333333333333333;
                    double22 = double15 - 2.0 - 0.6666666666666666;
                } else {
                    int6 = int0;
                    int3 = int0;
                    double19 = double15 - 0.3333333333333333;
                    double22 = double15 - 0.6666666666666666;
                }

                if ((byte6 & 2) != 0) {
                    int4 = int1 + 1;
                    int7 = int1 + 2;
                    double20 = double16 - 1.0 - 0.3333333333333333;
                    double23 = double16 - 2.0 - 0.6666666666666666;
                } else {
                    int7 = int1;
                    int4 = int1;
                    double20 = double16 - 0.3333333333333333;
                    double23 = double16 - 0.6666666666666666;
                }

                if ((byte6 & 4) != 0) {
                    int5 = int2 + 1;
                    int8 = int2 + 2;
                    double21 = double17 - 1.0 - 0.3333333333333333;
                    double24 = double17 - 2.0 - 0.6666666666666666;
                } else {
                    int8 = int2;
                    int5 = int2;
                    double21 = double17 - 0.3333333333333333;
                    double24 = double17 - 0.6666666666666666;
                }
            } else {
                byte byte7 = double39 < double38 ? byte5 : byte4;
                if ((byte7 & 1) != 0) {
                    int3 = int0 + 2;
                    int6 = int0 + 1;
                    double19 = double15 - 2.0 - 1.0;
                    double22 = double15 - 1.0 - 1.0;
                } else {
                    int6 = int0;
                    int3 = int0;
                    double19 = double22 = double15 - 1.0;
                }

                if ((byte7 & 2) != 0) {
                    int4 = int7 = int1 + 1;
                    double20 = double23 = double16 - 1.0 - 1.0;
                    if ((byte7 & 1) != 0) {
                        int7++;
                        double23--;
                    } else {
                        int4++;
                        double20--;
                    }
                } else {
                    int7 = int1;
                    int4 = int1;
                    double20 = double23 = double16 - 1.0;
                }

                if ((byte7 & 4) != 0) {
                    int5 = int2 + 1;
                    int8 = int2 + 2;
                    double21 = double17 - 1.0 - 1.0;
                    double24 = double17 - 2.0 - 1.0;
                } else {
                    int8 = int2;
                    int5 = int2;
                    double21 = double24 = double17 - 1.0;
                }
            }

            double double41 = double15 - 1.0 - 0.6666666666666666;
            double double42 = double16 - 1.0 - 0.6666666666666666;
            double double43 = double17 - 0.0 - 0.6666666666666666;
            double double44 = 2.0 - double41 * double41 - double42 * double42 - double43 * double43;
            if (double44 > 0.0) {
                double44 *= double44;
                double18 += double44 * double44 * this.extrapolate(int0 + 1, int1 + 1, int2 + 0, double41, double42, double43);
            }

            double double45 = double16 - 0.0 - 0.6666666666666666;
            double double46 = double17 - 1.0 - 0.6666666666666666;
            double double47 = 2.0 - double41 * double41 - double45 * double45 - double46 * double46;
            if (double47 > 0.0) {
                double47 *= double47;
                double18 += double47 * double47 * this.extrapolate(int0 + 1, int1 + 0, int2 + 1, double41, double45, double46);
            }

            double double48 = double15 - 0.0 - 0.6666666666666666;
            double double49 = 2.0 - double48 * double48 - double42 * double42 - double46 * double46;
            if (double49 > 0.0) {
                double49 *= double49;
                double18 += double49 * double49 * this.extrapolate(int0 + 0, int1 + 1, int2 + 1, double48, double42, double46);
            }

            double15 = double15 - 1.0 - 1.0;
            double16 = double16 - 1.0 - 1.0;
            double17 = double17 - 1.0 - 1.0;
            double double50 = 2.0 - double15 * double15 - double16 * double16 - double17 * double17;
            if (double50 > 0.0) {
                double50 *= double50;
                double18 += double50 * double50 * this.extrapolate(int0 + 1, int1 + 1, int2 + 1, double15, double16, double17);
            }
        } else {
            double double51 = double11 + double12;
            byte byte8;
            double double52;
            boolean boolean0;
            if (double51 > 1.0) {
                double52 = double51 - 1.0;
                byte8 = 3;
                boolean0 = true;
            } else {
                double52 = 1.0 - double51;
                byte8 = 4;
                boolean0 = false;
            }

            double double53 = double11 + double13;
            boolean boolean1;
            double double54;
            byte byte9;
            if (double53 > 1.0) {
                double54 = double53 - 1.0;
                byte9 = 5;
                boolean1 = true;
            } else {
                double54 = 1.0 - double53;
                byte9 = 2;
                boolean1 = false;
            }

            double double55 = double12 + double13;
            if (double55 > 1.0) {
                double double56 = double55 - 1.0;
                if (double52 <= double54 && double52 < double56) {
                    byte8 = 6;
                    boolean0 = true;
                } else if (double52 > double54 && double54 < double56) {
                    byte9 = 6;
                    boolean1 = true;
                }
            } else {
                double double57 = 1.0 - double55;
                if (double52 <= double54 && double52 < double57) {
                    byte8 = 1;
                    boolean0 = false;
                } else if (double52 > double54 && double54 < double57) {
                    byte9 = 1;
                    boolean1 = false;
                }
            }

            if (boolean0 == boolean1) {
                if (boolean0) {
                    double19 = double15 - 1.0 - 1.0;
                    double20 = double16 - 1.0 - 1.0;
                    double21 = double17 - 1.0 - 1.0;
                    int3 = int0 + 1;
                    int4 = int1 + 1;
                    int5 = int2 + 1;
                    byte byte10 = (byte)(byte8 & byte9);
                    if ((byte10 & 1) != 0) {
                        double22 = double15 - 2.0 - 0.6666666666666666;
                        double23 = double16 - 0.6666666666666666;
                        double24 = double17 - 0.6666666666666666;
                        int6 = int0 + 2;
                        int7 = int1;
                        int8 = int2;
                    } else if ((byte10 & 2) != 0) {
                        double22 = double15 - 0.6666666666666666;
                        double23 = double16 - 2.0 - 0.6666666666666666;
                        double24 = double17 - 0.6666666666666666;
                        int6 = int0;
                        int7 = int1 + 2;
                        int8 = int2;
                    } else {
                        double22 = double15 - 0.6666666666666666;
                        double23 = double16 - 0.6666666666666666;
                        double24 = double17 - 2.0 - 0.6666666666666666;
                        int6 = int0;
                        int7 = int1;
                        int8 = int2 + 2;
                    }
                } else {
                    double19 = double15;
                    double20 = double16;
                    double21 = double17;
                    int3 = int0;
                    int4 = int1;
                    int5 = int2;
                    byte byte11 = (byte)(byte8 | byte9);
                    if ((byte11 & 1) == 0) {
                        double22 = double15 + 1.0 - 0.3333333333333333;
                        double23 = double16 - 1.0 - 0.3333333333333333;
                        double24 = double17 - 1.0 - 0.3333333333333333;
                        int6 = int0 - 1;
                        int7 = int1 + 1;
                        int8 = int2 + 1;
                    } else if ((byte11 & 2) == 0) {
                        double22 = double15 - 1.0 - 0.3333333333333333;
                        double23 = double16 + 1.0 - 0.3333333333333333;
                        double24 = double17 - 1.0 - 0.3333333333333333;
                        int6 = int0 + 1;
                        int7 = int1 - 1;
                        int8 = int2 + 1;
                    } else {
                        double22 = double15 - 1.0 - 0.3333333333333333;
                        double23 = double16 - 1.0 - 0.3333333333333333;
                        double24 = double17 + 1.0 - 0.3333333333333333;
                        int6 = int0 + 1;
                        int7 = int1 + 1;
                        int8 = int2 - 1;
                    }
                }
            } else {
                byte byte12;
                byte byte13;
                if (boolean0) {
                    byte13 = byte8;
                    byte12 = byte9;
                } else {
                    byte13 = byte9;
                    byte12 = byte8;
                }

                if ((byte13 & 1) == 0) {
                    double19 = double15 + 1.0 - 0.3333333333333333;
                    double20 = double16 - 1.0 - 0.3333333333333333;
                    double21 = double17 - 1.0 - 0.3333333333333333;
                    int3 = int0 - 1;
                    int4 = int1 + 1;
                    int5 = int2 + 1;
                } else if ((byte13 & 2) == 0) {
                    double19 = double15 - 1.0 - 0.3333333333333333;
                    double20 = double16 + 1.0 - 0.3333333333333333;
                    double21 = double17 - 1.0 - 0.3333333333333333;
                    int3 = int0 + 1;
                    int4 = int1 - 1;
                    int5 = int2 + 1;
                } else {
                    double19 = double15 - 1.0 - 0.3333333333333333;
                    double20 = double16 - 1.0 - 0.3333333333333333;
                    double21 = double17 + 1.0 - 0.3333333333333333;
                    int3 = int0 + 1;
                    int4 = int1 + 1;
                    int5 = int2 - 1;
                }

                double22 = double15 - 0.6666666666666666;
                double23 = double16 - 0.6666666666666666;
                double24 = double17 - 0.6666666666666666;
                int6 = int0;
                int7 = int1;
                int8 = int2;
                if ((byte12 & 1) != 0) {
                    double22 -= 2.0;
                    int6 = int0 + 2;
                } else if ((byte12 & 2) != 0) {
                    double23 -= 2.0;
                    int7 = int1 + 2;
                } else {
                    double24 -= 2.0;
                    int8 = int2 + 2;
                }
            }

            double double58 = double15 - 1.0 - 0.3333333333333333;
            double double59 = double16 - 0.0 - 0.3333333333333333;
            double double60 = double17 - 0.0 - 0.3333333333333333;
            double double61 = 2.0 - double58 * double58 - double59 * double59 - double60 * double60;
            if (double61 > 0.0) {
                double61 *= double61;
                double18 += double61 * double61 * this.extrapolate(int0 + 1, int1 + 0, int2 + 0, double58, double59, double60);
            }

            double double62 = double15 - 0.0 - 0.3333333333333333;
            double double63 = double16 - 1.0 - 0.3333333333333333;
            double double64 = 2.0 - double62 * double62 - double63 * double63 - double60 * double60;
            if (double64 > 0.0) {
                double64 *= double64;
                double18 += double64 * double64 * this.extrapolate(int0 + 0, int1 + 1, int2 + 0, double62, double63, double60);
            }

            double double65 = double17 - 1.0 - 0.3333333333333333;
            double double66 = 2.0 - double62 * double62 - double59 * double59 - double65 * double65;
            if (double66 > 0.0) {
                double66 *= double66;
                double18 += double66 * double66 * this.extrapolate(int0 + 0, int1 + 0, int2 + 1, double62, double59, double65);
            }

            double double67 = double15 - 1.0 - 0.6666666666666666;
            double double68 = double16 - 1.0 - 0.6666666666666666;
            double double69 = double17 - 0.0 - 0.6666666666666666;
            double double70 = 2.0 - double67 * double67 - double68 * double68 - double69 * double69;
            if (double70 > 0.0) {
                double70 *= double70;
                double18 += double70 * double70 * this.extrapolate(int0 + 1, int1 + 1, int2 + 0, double67, double68, double69);
            }

            double double71 = double16 - 0.0 - 0.6666666666666666;
            double double72 = double17 - 1.0 - 0.6666666666666666;
            double double73 = 2.0 - double67 * double67 - double71 * double71 - double72 * double72;
            if (double73 > 0.0) {
                double73 *= double73;
                double18 += double73 * double73 * this.extrapolate(int0 + 1, int1 + 0, int2 + 1, double67, double71, double72);
            }

            double double74 = double15 - 0.0 - 0.6666666666666666;
            double double75 = 2.0 - double74 * double74 - double68 * double68 - double72 * double72;
            if (double75 > 0.0) {
                double75 *= double75;
                double18 += double75 * double75 * this.extrapolate(int0 + 0, int1 + 1, int2 + 1, double74, double68, double72);
            }
        }

        double double76 = 2.0 - double19 * double19 - double20 * double20 - double21 * double21;
        if (double76 > 0.0) {
            double76 *= double76;
            double18 += double76 * double76 * this.extrapolate(int3, int4, int5, double19, double20, double21);
        }

        double double77 = 2.0 - double22 * double22 - double23 * double23 - double24 * double24;
        if (double77 > 0.0) {
            double77 *= double77;
            double18 += double77 * double77 * this.extrapolate(int6, int7, int8, double22, double23, double24);
        }

        return double18 / 103.0;
    }

    public double eval(double double3, double double4, double double2, double double1) {
        double double0 = (double3 + double4 + double2 + double1) * -0.138196601125011;
        double double5 = double3 + double0;
        double double6 = double4 + double0;
        double double7 = double2 + double0;
        double double8 = double1 + double0;
        int int0 = fastFloor(double5);
        int int1 = fastFloor(double6);
        int int2 = fastFloor(double7);
        int int3 = fastFloor(double8);
        double double9 = (int0 + int1 + int2 + int3) * 0.309016994374947;
        double double10 = int0 + double9;
        double double11 = int1 + double9;
        double double12 = int2 + double9;
        double double13 = int3 + double9;
        double double14 = double5 - int0;
        double double15 = double6 - int1;
        double double16 = double7 - int2;
        double double17 = double8 - int3;
        double double18 = double14 + double15 + double16 + double17;
        double double19 = double3 - double10;
        double double20 = double4 - double11;
        double double21 = double2 - double12;
        double double22 = double1 - double13;
        double double23 = 0.0;
        double double24;
        double double25;
        double double26;
        double double27;
        double double28;
        double double29;
        double double30;
        double double31;
        double double32;
        double double33;
        double double34;
        double double35;
        int int4;
        int int5;
        int int6;
        int int7;
        int int8;
        int int9;
        int int10;
        int int11;
        int int12;
        int int13;
        int int14;
        int int15;
        if (double18 <= 1.0) {
            byte byte0 = 1;
            double double36 = double14;
            byte byte1 = 2;
            double double37 = double15;
            if (double14 >= double15 && double16 > double15) {
                double37 = double16;
                byte1 = 4;
            } else if (double14 < double15 && double16 > double14) {
                double36 = double16;
                byte0 = 4;
            }

            if (double36 >= double37 && double17 > double37) {
                double37 = double17;
                byte1 = 8;
            } else if (double36 < double37 && double17 > double36) {
                double36 = double17;
                byte0 = 8;
            }

            double double38 = 1.0 - double18;
            if (!(double38 > double36) && !(double38 > double37)) {
                byte byte2 = (byte)(byte0 | byte1);
                if ((byte2 & 1) == 0) {
                    int12 = int0;
                    int4 = int0;
                    int8 = int0 - 1;
                    double24 = double19 - 0.618033988749894;
                    double28 = double19 + 1.0 - 0.309016994374947;
                    double32 = double19 - 0.309016994374947;
                } else {
                    int4 = int8 = int12 = int0 + 1;
                    double24 = double19 - 1.0 - 0.618033988749894;
                    double28 = double32 = double19 - 1.0 - 0.309016994374947;
                }

                if ((byte2 & 2) == 0) {
                    int13 = int1;
                    int9 = int1;
                    int5 = int1;
                    double25 = double20 - 0.618033988749894;
                    double29 = double33 = double20 - 0.309016994374947;
                    if ((byte2 & 1) == 1) {
                        int9 = int1 - 1;
                        double29++;
                    } else {
                        int13 = int1 - 1;
                        double33++;
                    }
                } else {
                    int5 = int9 = int13 = int1 + 1;
                    double25 = double20 - 1.0 - 0.618033988749894;
                    double29 = double33 = double20 - 1.0 - 0.309016994374947;
                }

                if ((byte2 & 4) == 0) {
                    int14 = int2;
                    int10 = int2;
                    int6 = int2;
                    double26 = double21 - 0.618033988749894;
                    double30 = double34 = double21 - 0.309016994374947;
                    if ((byte2 & 3) == 3) {
                        int10 = int2 - 1;
                        double30++;
                    } else {
                        int14 = int2 - 1;
                        double34++;
                    }
                } else {
                    int6 = int10 = int14 = int2 + 1;
                    double26 = double21 - 1.0 - 0.618033988749894;
                    double30 = double34 = double21 - 1.0 - 0.309016994374947;
                }

                if ((byte2 & 8) == 0) {
                    int11 = int3;
                    int7 = int3;
                    int15 = int3 - 1;
                    double27 = double22 - 0.618033988749894;
                    double31 = double22 - 0.309016994374947;
                    double35 = double22 + 1.0 - 0.309016994374947;
                } else {
                    int7 = int11 = int15 = int3 + 1;
                    double27 = double22 - 1.0 - 0.618033988749894;
                    double31 = double35 = double22 - 1.0 - 0.309016994374947;
                }
            } else {
                byte byte3 = double37 > double36 ? byte1 : byte0;
                if ((byte3 & 1) == 0) {
                    int4 = int0 - 1;
                    int12 = int0;
                    int8 = int0;
                    double24 = double19 + 1.0;
                    double32 = double19;
                    double28 = double19;
                } else {
                    int4 = int8 = int12 = int0 + 1;
                    double24 = double28 = double32 = double19 - 1.0;
                }

                if ((byte3 & 2) == 0) {
                    int13 = int1;
                    int9 = int1;
                    int5 = int1;
                    double33 = double20;
                    double29 = double20;
                    double25 = double20;
                    if ((byte3 & 1) == 1) {
                        int5 = int1 - 1;
                        double25 = double20 + 1.0;
                    } else {
                        int9 = int1 - 1;
                        double29 = double20 + 1.0;
                    }
                } else {
                    int5 = int9 = int13 = int1 + 1;
                    double25 = double29 = double33 = double20 - 1.0;
                }

                if ((byte3 & 4) == 0) {
                    int14 = int2;
                    int10 = int2;
                    int6 = int2;
                    double34 = double21;
                    double30 = double21;
                    double26 = double21;
                    if ((byte3 & 3) != 0) {
                        if ((byte3 & 3) == 3) {
                            int6 = int2 - 1;
                            double26 = double21 + 1.0;
                        } else {
                            int10 = int2 - 1;
                            double30 = double21 + 1.0;
                        }
                    } else {
                        int14 = int2 - 1;
                        double34 = double21 + 1.0;
                    }
                } else {
                    int6 = int10 = int14 = int2 + 1;
                    double26 = double30 = double34 = double21 - 1.0;
                }

                if ((byte3 & 8) == 0) {
                    int11 = int3;
                    int7 = int3;
                    int15 = int3 - 1;
                    double31 = double22;
                    double27 = double22;
                    double35 = double22 + 1.0;
                } else {
                    int7 = int11 = int15 = int3 + 1;
                    double27 = double31 = double35 = double22 - 1.0;
                }
            }

            double double39 = 2.0 - double19 * double19 - double20 * double20 - double21 * double21 - double22 * double22;
            if (double39 > 0.0) {
                double39 *= double39;
                double23 += double39 * double39 * this.extrapolate(int0 + 0, int1 + 0, int2 + 0, int3 + 0, double19, double20, double21, double22);
            }

            double double40 = double19 - 1.0 - 0.309016994374947;
            double double41 = double20 - 0.0 - 0.309016994374947;
            double double42 = double21 - 0.0 - 0.309016994374947;
            double double43 = double22 - 0.0 - 0.309016994374947;
            double double44 = 2.0 - double40 * double40 - double41 * double41 - double42 * double42 - double43 * double43;
            if (double44 > 0.0) {
                double44 *= double44;
                double23 += double44 * double44 * this.extrapolate(int0 + 1, int1 + 0, int2 + 0, int3 + 0, double40, double41, double42, double43);
            }

            double double45 = double19 - 0.0 - 0.309016994374947;
            double double46 = double20 - 1.0 - 0.309016994374947;
            double double47 = 2.0 - double45 * double45 - double46 * double46 - double42 * double42 - double43 * double43;
            if (double47 > 0.0) {
                double47 *= double47;
                double23 += double47 * double47 * this.extrapolate(int0 + 0, int1 + 1, int2 + 0, int3 + 0, double45, double46, double42, double43);
            }

            double double48 = double21 - 1.0 - 0.309016994374947;
            double double49 = 2.0 - double45 * double45 - double41 * double41 - double48 * double48 - double43 * double43;
            if (double49 > 0.0) {
                double49 *= double49;
                double23 += double49 * double49 * this.extrapolate(int0 + 0, int1 + 0, int2 + 1, int3 + 0, double45, double41, double48, double43);
            }

            double double50 = double22 - 1.0 - 0.309016994374947;
            double double51 = 2.0 - double45 * double45 - double41 * double41 - double42 * double42 - double50 * double50;
            if (double51 > 0.0) {
                double51 *= double51;
                double23 += double51 * double51 * this.extrapolate(int0 + 0, int1 + 0, int2 + 0, int3 + 1, double45, double41, double42, double50);
            }
        } else if (double18 >= 3.0) {
            byte byte4 = 14;
            double double52 = double14;
            byte byte5 = 13;
            double double53 = double15;
            if (double14 <= double15 && double16 < double15) {
                double53 = double16;
                byte5 = 11;
            } else if (double14 > double15 && double16 < double14) {
                double52 = double16;
                byte4 = 11;
            }

            if (double52 <= double53 && double17 < double53) {
                double53 = double17;
                byte5 = 7;
            } else if (double52 > double53 && double17 < double52) {
                double52 = double17;
                byte4 = 7;
            }

            double double54 = 4.0 - double18;
            if (!(double54 < double52) && !(double54 < double53)) {
                byte byte6 = (byte)(byte4 & byte5);
                if ((byte6 & 1) != 0) {
                    int4 = int12 = int0 + 1;
                    int8 = int0 + 2;
                    double24 = double19 - 1.0 - 0.618033988749894;
                    double28 = double19 - 2.0 - 0.927050983124841;
                    double32 = double19 - 1.0 - 0.927050983124841;
                } else {
                    int12 = int0;
                    int8 = int0;
                    int4 = int0;
                    double24 = double19 - 0.618033988749894;
                    double28 = double32 = double19 - 0.927050983124841;
                }

                if ((byte6 & 2) != 0) {
                    int5 = int9 = int13 = int1 + 1;
                    double25 = double20 - 1.0 - 0.618033988749894;
                    double29 = double33 = double20 - 1.0 - 0.927050983124841;
                    if ((byte6 & 1) != 0) {
                        int13++;
                        double33--;
                    } else {
                        int9++;
                        double29--;
                    }
                } else {
                    int13 = int1;
                    int9 = int1;
                    int5 = int1;
                    double25 = double20 - 0.618033988749894;
                    double29 = double33 = double20 - 0.927050983124841;
                }

                if ((byte6 & 4) != 0) {
                    int6 = int10 = int14 = int2 + 1;
                    double26 = double21 - 1.0 - 0.618033988749894;
                    double30 = double34 = double21 - 1.0 - 0.927050983124841;
                    if ((byte6 & 3) != 0) {
                        int14++;
                        double34--;
                    } else {
                        int10++;
                        double30--;
                    }
                } else {
                    int14 = int2;
                    int10 = int2;
                    int6 = int2;
                    double26 = double21 - 0.618033988749894;
                    double30 = double34 = double21 - 0.927050983124841;
                }

                if ((byte6 & 8) != 0) {
                    int7 = int11 = int3 + 1;
                    int15 = int3 + 2;
                    double27 = double22 - 1.0 - 0.618033988749894;
                    double31 = double22 - 1.0 - 0.927050983124841;
                    double35 = double22 - 2.0 - 0.927050983124841;
                } else {
                    int15 = int3;
                    int11 = int3;
                    int7 = int3;
                    double27 = double22 - 0.618033988749894;
                    double31 = double35 = double22 - 0.927050983124841;
                }
            } else {
                byte byte7 = double53 < double52 ? byte5 : byte4;
                if ((byte7 & 1) != 0) {
                    int4 = int0 + 2;
                    int8 = int12 = int0 + 1;
                    double24 = double19 - 2.0 - 1.236067977499788;
                    double28 = double32 = double19 - 1.0 - 1.236067977499788;
                } else {
                    int12 = int0;
                    int8 = int0;
                    int4 = int0;
                    double24 = double28 = double32 = double19 - 1.236067977499788;
                }

                if ((byte7 & 2) != 0) {
                    int5 = int9 = int13 = int1 + 1;
                    double25 = double29 = double33 = double20 - 1.0 - 1.236067977499788;
                    if ((byte7 & 1) != 0) {
                        int9++;
                        double29--;
                    } else {
                        int5++;
                        double25--;
                    }
                } else {
                    int13 = int1;
                    int9 = int1;
                    int5 = int1;
                    double25 = double29 = double33 = double20 - 1.236067977499788;
                }

                if ((byte7 & 4) != 0) {
                    int6 = int10 = int14 = int2 + 1;
                    double26 = double30 = double34 = double21 - 1.0 - 1.236067977499788;
                    if ((byte7 & 3) != 3) {
                        if ((byte7 & 3) == 0) {
                            int6++;
                            double26--;
                        } else {
                            int10++;
                            double30--;
                        }
                    } else {
                        int14++;
                        double34--;
                    }
                } else {
                    int14 = int2;
                    int10 = int2;
                    int6 = int2;
                    double26 = double30 = double34 = double21 - 1.236067977499788;
                }

                if ((byte7 & 8) != 0) {
                    int7 = int11 = int3 + 1;
                    int15 = int3 + 2;
                    double27 = double31 = double22 - 1.0 - 1.236067977499788;
                    double35 = double22 - 2.0 - 1.236067977499788;
                } else {
                    int15 = int3;
                    int11 = int3;
                    int7 = int3;
                    double27 = double31 = double35 = double22 - 1.236067977499788;
                }
            }

            double double55 = double19 - 1.0 - 0.927050983124841;
            double double56 = double20 - 1.0 - 0.927050983124841;
            double double57 = double21 - 1.0 - 0.927050983124841;
            double double58 = double22 - 0.927050983124841;
            double double59 = 2.0 - double55 * double55 - double56 * double56 - double57 * double57 - double58 * double58;
            if (double59 > 0.0) {
                double59 *= double59;
                double23 += double59 * double59 * this.extrapolate(int0 + 1, int1 + 1, int2 + 1, int3 + 0, double55, double56, double57, double58);
            }

            double double60 = double21 - 0.927050983124841;
            double double61 = double22 - 1.0 - 0.927050983124841;
            double double62 = 2.0 - double55 * double55 - double56 * double56 - double60 * double60 - double61 * double61;
            if (double62 > 0.0) {
                double62 *= double62;
                double23 += double62 * double62 * this.extrapolate(int0 + 1, int1 + 1, int2 + 0, int3 + 1, double55, double56, double60, double61);
            }

            double double63 = double20 - 0.927050983124841;
            double double64 = 2.0 - double55 * double55 - double63 * double63 - double57 * double57 - double61 * double61;
            if (double64 > 0.0) {
                double64 *= double64;
                double23 += double64 * double64 * this.extrapolate(int0 + 1, int1 + 0, int2 + 1, int3 + 1, double55, double63, double57, double61);
            }

            double double65 = double19 - 0.927050983124841;
            double double66 = 2.0 - double65 * double65 - double56 * double56 - double57 * double57 - double61 * double61;
            if (double66 > 0.0) {
                double66 *= double66;
                double23 += double66 * double66 * this.extrapolate(int0 + 0, int1 + 1, int2 + 1, int3 + 1, double65, double56, double57, double61);
            }

            double19 = double19 - 1.0 - 1.236067977499788;
            double20 = double20 - 1.0 - 1.236067977499788;
            double21 = double21 - 1.0 - 1.236067977499788;
            double22 = double22 - 1.0 - 1.236067977499788;
            double double67 = 2.0 - double19 * double19 - double20 * double20 - double21 * double21 - double22 * double22;
            if (double67 > 0.0) {
                double67 *= double67;
                double23 += double67 * double67 * this.extrapolate(int0 + 1, int1 + 1, int2 + 1, int3 + 1, double19, double20, double21, double22);
            }
        } else if (double18 <= 2.0) {
            boolean boolean0 = true;
            boolean boolean1 = true;
            byte byte8;
            double double68;
            if (double14 + double15 > double16 + double17) {
                double68 = double14 + double15;
                byte8 = 3;
            } else {
                double68 = double16 + double17;
                byte8 = 12;
            }

            double double69;
            byte byte9;
            if (double14 + double16 > double15 + double17) {
                double69 = double14 + double16;
                byte9 = 5;
            } else {
                double69 = double15 + double17;
                byte9 = 10;
            }

            if (double14 + double17 > double15 + double16) {
                double double70 = double14 + double17;
                if (double68 >= double69 && double70 > double69) {
                    double69 = double70;
                    byte9 = 9;
                } else if (double68 < double69 && double70 > double68) {
                    double68 = double70;
                    byte8 = 9;
                }
            } else {
                double double71 = double15 + double16;
                if (double68 >= double69 && double71 > double69) {
                    double69 = double71;
                    byte9 = 6;
                } else if (double68 < double69 && double71 > double68) {
                    double68 = double71;
                    byte8 = 6;
                }
            }

            double double72 = 2.0 - double18 + double14;
            if (double68 >= double69 && double72 > double69) {
                double69 = double72;
                byte9 = 1;
                boolean1 = false;
            } else if (double68 < double69 && double72 > double68) {
                double68 = double72;
                byte8 = 1;
                boolean0 = false;
            }

            double double73 = 2.0 - double18 + double15;
            if (double68 >= double69 && double73 > double69) {
                double69 = double73;
                byte9 = 2;
                boolean1 = false;
            } else if (double68 < double69 && double73 > double68) {
                double68 = double73;
                byte8 = 2;
                boolean0 = false;
            }

            double double74 = 2.0 - double18 + double16;
            if (double68 >= double69 && double74 > double69) {
                double69 = double74;
                byte9 = 4;
                boolean1 = false;
            } else if (double68 < double69 && double74 > double68) {
                double68 = double74;
                byte8 = 4;
                boolean0 = false;
            }

            double double75 = 2.0 - double18 + double17;
            if (double68 >= double69 && double75 > double69) {
                byte9 = 8;
                boolean1 = false;
            } else if (double68 < double69 && double75 > double68) {
                byte8 = 8;
                boolean0 = false;
            }

            if (boolean0 == boolean1) {
                if (boolean0) {
                    byte byte10 = (byte)(byte8 | byte9);
                    byte byte11 = (byte)(byte8 & byte9);
                    if ((byte10 & 1) == 0) {
                        int4 = int0;
                        int8 = int0 - 1;
                        double24 = double19 - 0.927050983124841;
                        double28 = double19 + 1.0 - 0.618033988749894;
                    } else {
                        int4 = int8 = int0 + 1;
                        double24 = double19 - 1.0 - 0.927050983124841;
                        double28 = double19 - 1.0 - 0.618033988749894;
                    }

                    if ((byte10 & 2) == 0) {
                        int5 = int1;
                        int9 = int1 - 1;
                        double25 = double20 - 0.927050983124841;
                        double29 = double20 + 1.0 - 0.618033988749894;
                    } else {
                        int5 = int9 = int1 + 1;
                        double25 = double20 - 1.0 - 0.927050983124841;
                        double29 = double20 - 1.0 - 0.618033988749894;
                    }

                    if ((byte10 & 4) == 0) {
                        int6 = int2;
                        int10 = int2 - 1;
                        double26 = double21 - 0.927050983124841;
                        double30 = double21 + 1.0 - 0.618033988749894;
                    } else {
                        int6 = int10 = int2 + 1;
                        double26 = double21 - 1.0 - 0.927050983124841;
                        double30 = double21 - 1.0 - 0.618033988749894;
                    }

                    if ((byte10 & 8) == 0) {
                        int7 = int3;
                        int11 = int3 - 1;
                        double27 = double22 - 0.927050983124841;
                        double31 = double22 + 1.0 - 0.618033988749894;
                    } else {
                        int7 = int11 = int3 + 1;
                        double27 = double22 - 1.0 - 0.927050983124841;
                        double31 = double22 - 1.0 - 0.618033988749894;
                    }

                    int12 = int0;
                    int13 = int1;
                    int14 = int2;
                    int15 = int3;
                    double32 = double19 - 0.618033988749894;
                    double33 = double20 - 0.618033988749894;
                    double34 = double21 - 0.618033988749894;
                    double35 = double22 - 0.618033988749894;
                    if ((byte11 & 1) != 0) {
                        int12 = int0 + 2;
                        double32 -= 2.0;
                    } else if ((byte11 & 2) != 0) {
                        int13 = int1 + 2;
                        double33 -= 2.0;
                    } else if ((byte11 & 4) != 0) {
                        int14 = int2 + 2;
                        double34 -= 2.0;
                    } else {
                        int15 = int3 + 2;
                        double35 -= 2.0;
                    }
                } else {
                    int12 = int0;
                    int13 = int1;
                    int14 = int2;
                    int15 = int3;
                    double32 = double19;
                    double33 = double20;
                    double34 = double21;
                    double35 = double22;
                    byte byte12 = (byte)(byte8 | byte9);
                    if ((byte12 & 1) == 0) {
                        int4 = int0 - 1;
                        int8 = int0;
                        double24 = double19 + 1.0 - 0.309016994374947;
                        double28 = double19 - 0.309016994374947;
                    } else {
                        int4 = int8 = int0 + 1;
                        double24 = double28 = double19 - 1.0 - 0.309016994374947;
                    }

                    if ((byte12 & 2) == 0) {
                        int9 = int1;
                        int5 = int1;
                        double25 = double29 = double20 - 0.309016994374947;
                        if ((byte12 & 1) == 1) {
                            int5 = int1 - 1;
                            double25++;
                        } else {
                            int9 = int1 - 1;
                            double29++;
                        }
                    } else {
                        int5 = int9 = int1 + 1;
                        double25 = double29 = double20 - 1.0 - 0.309016994374947;
                    }

                    if ((byte12 & 4) == 0) {
                        int10 = int2;
                        int6 = int2;
                        double26 = double30 = double21 - 0.309016994374947;
                        if ((byte12 & 3) == 3) {
                            int6 = int2 - 1;
                            double26++;
                        } else {
                            int10 = int2 - 1;
                            double30++;
                        }
                    } else {
                        int6 = int10 = int2 + 1;
                        double26 = double30 = double21 - 1.0 - 0.309016994374947;
                    }

                    if ((byte12 & 8) == 0) {
                        int7 = int3;
                        int11 = int3 - 1;
                        double27 = double22 - 0.309016994374947;
                        double31 = double22 + 1.0 - 0.309016994374947;
                    } else {
                        int7 = int11 = int3 + 1;
                        double27 = double31 = double22 - 1.0 - 0.309016994374947;
                    }
                }
            } else {
                byte byte13;
                byte byte14;
                if (boolean0) {
                    byte13 = byte8;
                    byte14 = byte9;
                } else {
                    byte13 = byte9;
                    byte14 = byte8;
                }

                if ((byte13 & 1) == 0) {
                    int4 = int0 - 1;
                    int8 = int0;
                    double24 = double19 + 1.0 - 0.309016994374947;
                    double28 = double19 - 0.309016994374947;
                } else {
                    int4 = int8 = int0 + 1;
                    double24 = double28 = double19 - 1.0 - 0.309016994374947;
                }

                if ((byte13 & 2) == 0) {
                    int9 = int1;
                    int5 = int1;
                    double25 = double29 = double20 - 0.309016994374947;
                    if ((byte13 & 1) == 1) {
                        int5 = int1 - 1;
                        double25++;
                    } else {
                        int9 = int1 - 1;
                        double29++;
                    }
                } else {
                    int5 = int9 = int1 + 1;
                    double25 = double29 = double20 - 1.0 - 0.309016994374947;
                }

                if ((byte13 & 4) == 0) {
                    int10 = int2;
                    int6 = int2;
                    double26 = double30 = double21 - 0.309016994374947;
                    if ((byte13 & 3) == 3) {
                        int6 = int2 - 1;
                        double26++;
                    } else {
                        int10 = int2 - 1;
                        double30++;
                    }
                } else {
                    int6 = int10 = int2 + 1;
                    double26 = double30 = double21 - 1.0 - 0.309016994374947;
                }

                if ((byte13 & 8) == 0) {
                    int7 = int3;
                    int11 = int3 - 1;
                    double27 = double22 - 0.309016994374947;
                    double31 = double22 + 1.0 - 0.309016994374947;
                } else {
                    int7 = int11 = int3 + 1;
                    double27 = double31 = double22 - 1.0 - 0.309016994374947;
                }

                int12 = int0;
                int13 = int1;
                int14 = int2;
                int15 = int3;
                double32 = double19 - 0.618033988749894;
                double33 = double20 - 0.618033988749894;
                double34 = double21 - 0.618033988749894;
                double35 = double22 - 0.618033988749894;
                if ((byte14 & 1) != 0) {
                    int12 = int0 + 2;
                    double32 -= 2.0;
                } else if ((byte14 & 2) != 0) {
                    int13 = int1 + 2;
                    double33 -= 2.0;
                } else if ((byte14 & 4) != 0) {
                    int14 = int2 + 2;
                    double34 -= 2.0;
                } else {
                    int15 = int3 + 2;
                    double35 -= 2.0;
                }
            }

            double double76 = double19 - 1.0 - 0.309016994374947;
            double double77 = double20 - 0.0 - 0.309016994374947;
            double double78 = double21 - 0.0 - 0.309016994374947;
            double double79 = double22 - 0.0 - 0.309016994374947;
            double double80 = 2.0 - double76 * double76 - double77 * double77 - double78 * double78 - double79 * double79;
            if (double80 > 0.0) {
                double80 *= double80;
                double23 += double80 * double80 * this.extrapolate(int0 + 1, int1 + 0, int2 + 0, int3 + 0, double76, double77, double78, double79);
            }

            double double81 = double19 - 0.0 - 0.309016994374947;
            double double82 = double20 - 1.0 - 0.309016994374947;
            double double83 = 2.0 - double81 * double81 - double82 * double82 - double78 * double78 - double79 * double79;
            if (double83 > 0.0) {
                double83 *= double83;
                double23 += double83 * double83 * this.extrapolate(int0 + 0, int1 + 1, int2 + 0, int3 + 0, double81, double82, double78, double79);
            }

            double double84 = double21 - 1.0 - 0.309016994374947;
            double double85 = 2.0 - double81 * double81 - double77 * double77 - double84 * double84 - double79 * double79;
            if (double85 > 0.0) {
                double85 *= double85;
                double23 += double85 * double85 * this.extrapolate(int0 + 0, int1 + 0, int2 + 1, int3 + 0, double81, double77, double84, double79);
            }

            double double86 = double22 - 1.0 - 0.309016994374947;
            double double87 = 2.0 - double81 * double81 - double77 * double77 - double78 * double78 - double86 * double86;
            if (double87 > 0.0) {
                double87 *= double87;
                double23 += double87 * double87 * this.extrapolate(int0 + 0, int1 + 0, int2 + 0, int3 + 1, double81, double77, double78, double86);
            }

            double double88 = double19 - 1.0 - 0.618033988749894;
            double double89 = double20 - 1.0 - 0.618033988749894;
            double double90 = double21 - 0.0 - 0.618033988749894;
            double double91 = double22 - 0.0 - 0.618033988749894;
            double double92 = 2.0 - double88 * double88 - double89 * double89 - double90 * double90 - double91 * double91;
            if (double92 > 0.0) {
                double92 *= double92;
                double23 += double92 * double92 * this.extrapolate(int0 + 1, int1 + 1, int2 + 0, int3 + 0, double88, double89, double90, double91);
            }

            double double93 = double19 - 1.0 - 0.618033988749894;
            double double94 = double20 - 0.0 - 0.618033988749894;
            double double95 = double21 - 1.0 - 0.618033988749894;
            double double96 = double22 - 0.0 - 0.618033988749894;
            double double97 = 2.0 - double93 * double93 - double94 * double94 - double95 * double95 - double96 * double96;
            if (double97 > 0.0) {
                double97 *= double97;
                double23 += double97 * double97 * this.extrapolate(int0 + 1, int1 + 0, int2 + 1, int3 + 0, double93, double94, double95, double96);
            }

            double double98 = double19 - 1.0 - 0.618033988749894;
            double double99 = double20 - 0.0 - 0.618033988749894;
            double double100 = double21 - 0.0 - 0.618033988749894;
            double double101 = double22 - 1.0 - 0.618033988749894;
            double double102 = 2.0 - double98 * double98 - double99 * double99 - double100 * double100 - double101 * double101;
            if (double102 > 0.0) {
                double102 *= double102;
                double23 += double102 * double102 * this.extrapolate(int0 + 1, int1 + 0, int2 + 0, int3 + 1, double98, double99, double100, double101);
            }

            double double103 = double19 - 0.0 - 0.618033988749894;
            double double104 = double20 - 1.0 - 0.618033988749894;
            double double105 = double21 - 1.0 - 0.618033988749894;
            double double106 = double22 - 0.0 - 0.618033988749894;
            double double107 = 2.0 - double103 * double103 - double104 * double104 - double105 * double105 - double106 * double106;
            if (double107 > 0.0) {
                double107 *= double107;
                double23 += double107 * double107 * this.extrapolate(int0 + 0, int1 + 1, int2 + 1, int3 + 0, double103, double104, double105, double106);
            }

            double double108 = double19 - 0.0 - 0.618033988749894;
            double double109 = double20 - 1.0 - 0.618033988749894;
            double double110 = double21 - 0.0 - 0.618033988749894;
            double double111 = double22 - 1.0 - 0.618033988749894;
            double double112 = 2.0 - double108 * double108 - double109 * double109 - double110 * double110 - double111 * double111;
            if (double112 > 0.0) {
                double112 *= double112;
                double23 += double112 * double112 * this.extrapolate(int0 + 0, int1 + 1, int2 + 0, int3 + 1, double108, double109, double110, double111);
            }

            double double113 = double19 - 0.0 - 0.618033988749894;
            double double114 = double20 - 0.0 - 0.618033988749894;
            double double115 = double21 - 1.0 - 0.618033988749894;
            double double116 = double22 - 1.0 - 0.618033988749894;
            double double117 = 2.0 - double113 * double113 - double114 * double114 - double115 * double115 - double116 * double116;
            if (double117 > 0.0) {
                double117 *= double117;
                double23 += double117 * double117 * this.extrapolate(int0 + 0, int1 + 0, int2 + 1, int3 + 1, double113, double114, double115, double116);
            }
        } else {
            boolean boolean2 = true;
            boolean boolean3 = true;
            double double118;
            byte byte15;
            if (double14 + double15 < double16 + double17) {
                double118 = double14 + double15;
                byte15 = 12;
            } else {
                double118 = double16 + double17;
                byte15 = 3;
            }

            double double119;
            byte byte16;
            if (double14 + double16 < double15 + double17) {
                double119 = double14 + double16;
                byte16 = 10;
            } else {
                double119 = double15 + double17;
                byte16 = 5;
            }

            if (double14 + double17 < double15 + double16) {
                double double120 = double14 + double17;
                if (double118 <= double119 && double120 < double119) {
                    double119 = double120;
                    byte16 = 6;
                } else if (double118 > double119 && double120 < double118) {
                    double118 = double120;
                    byte15 = 6;
                }
            } else {
                double double121 = double15 + double16;
                if (double118 <= double119 && double121 < double119) {
                    double119 = double121;
                    byte16 = 9;
                } else if (double118 > double119 && double121 < double118) {
                    double118 = double121;
                    byte15 = 9;
                }
            }

            double double122 = 3.0 - double18 + double14;
            if (double118 <= double119 && double122 < double119) {
                double119 = double122;
                byte16 = 14;
                boolean3 = false;
            } else if (double118 > double119 && double122 < double118) {
                double118 = double122;
                byte15 = 14;
                boolean2 = false;
            }

            double double123 = 3.0 - double18 + double15;
            if (double118 <= double119 && double123 < double119) {
                double119 = double123;
                byte16 = 13;
                boolean3 = false;
            } else if (double118 > double119 && double123 < double118) {
                double118 = double123;
                byte15 = 13;
                boolean2 = false;
            }

            double double124 = 3.0 - double18 + double16;
            if (double118 <= double119 && double124 < double119) {
                double119 = double124;
                byte16 = 11;
                boolean3 = false;
            } else if (double118 > double119 && double124 < double118) {
                double118 = double124;
                byte15 = 11;
                boolean2 = false;
            }

            double double125 = 3.0 - double18 + double17;
            if (double118 <= double119 && double125 < double119) {
                byte16 = 7;
                boolean3 = false;
            } else if (double118 > double119 && double125 < double118) {
                byte15 = 7;
                boolean2 = false;
            }

            if (boolean2 == boolean3) {
                if (boolean2) {
                    byte byte17 = (byte)(byte15 & byte16);
                    byte byte18 = (byte)(byte15 | byte16);
                    int8 = int0;
                    int4 = int0;
                    int9 = int1;
                    int5 = int1;
                    int10 = int2;
                    int6 = int2;
                    int11 = int3;
                    int7 = int3;
                    double24 = double19 - 0.309016994374947;
                    double25 = double20 - 0.309016994374947;
                    double26 = double21 - 0.309016994374947;
                    double27 = double22 - 0.309016994374947;
                    double28 = double19 - 0.618033988749894;
                    double29 = double20 - 0.618033988749894;
                    double30 = double21 - 0.618033988749894;
                    double31 = double22 - 0.618033988749894;
                    if ((byte17 & 1) != 0) {
                        int4 = int0 + 1;
                        double24--;
                        int8 = int0 + 2;
                        double28 -= 2.0;
                    } else if ((byte17 & 2) != 0) {
                        int5 = int1 + 1;
                        double25--;
                        int9 = int1 + 2;
                        double29 -= 2.0;
                    } else if ((byte17 & 4) != 0) {
                        int6 = int2 + 1;
                        double26--;
                        int10 = int2 + 2;
                        double30 -= 2.0;
                    } else {
                        int7 = int3 + 1;
                        double27--;
                        int11 = int3 + 2;
                        double31 -= 2.0;
                    }

                    int12 = int0 + 1;
                    int13 = int1 + 1;
                    int14 = int2 + 1;
                    int15 = int3 + 1;
                    double32 = double19 - 1.0 - 0.618033988749894;
                    double33 = double20 - 1.0 - 0.618033988749894;
                    double34 = double21 - 1.0 - 0.618033988749894;
                    double35 = double22 - 1.0 - 0.618033988749894;
                    if ((byte18 & 1) == 0) {
                        int12 -= 2;
                        double32 += 2.0;
                    } else if ((byte18 & 2) == 0) {
                        int13 -= 2;
                        double33 += 2.0;
                    } else if ((byte18 & 4) == 0) {
                        int14 -= 2;
                        double34 += 2.0;
                    } else {
                        int15 -= 2;
                        double35 += 2.0;
                    }
                } else {
                    int12 = int0 + 1;
                    int13 = int1 + 1;
                    int14 = int2 + 1;
                    int15 = int3 + 1;
                    double32 = double19 - 1.0 - 1.236067977499788;
                    double33 = double20 - 1.0 - 1.236067977499788;
                    double34 = double21 - 1.0 - 1.236067977499788;
                    double35 = double22 - 1.0 - 1.236067977499788;
                    byte byte19 = (byte)(byte15 & byte16);
                    if ((byte19 & 1) != 0) {
                        int4 = int0 + 2;
                        int8 = int0 + 1;
                        double24 = double19 - 2.0 - 0.927050983124841;
                        double28 = double19 - 1.0 - 0.927050983124841;
                    } else {
                        int8 = int0;
                        int4 = int0;
                        double24 = double28 = double19 - 0.927050983124841;
                    }

                    if ((byte19 & 2) != 0) {
                        int5 = int9 = int1 + 1;
                        double25 = double29 = double20 - 1.0 - 0.927050983124841;
                        if ((byte19 & 1) == 0) {
                            int5++;
                            double25--;
                        } else {
                            int9++;
                            double29--;
                        }
                    } else {
                        int9 = int1;
                        int5 = int1;
                        double25 = double29 = double20 - 0.927050983124841;
                    }

                    if ((byte19 & 4) != 0) {
                        int6 = int10 = int2 + 1;
                        double26 = double30 = double21 - 1.0 - 0.927050983124841;
                        if ((byte19 & 3) == 0) {
                            int6++;
                            double26--;
                        } else {
                            int10++;
                            double30--;
                        }
                    } else {
                        int10 = int2;
                        int6 = int2;
                        double26 = double30 = double21 - 0.927050983124841;
                    }

                    if ((byte19 & 8) != 0) {
                        int7 = int3 + 1;
                        int11 = int3 + 2;
                        double27 = double22 - 1.0 - 0.927050983124841;
                        double31 = double22 - 2.0 - 0.927050983124841;
                    } else {
                        int11 = int3;
                        int7 = int3;
                        double27 = double31 = double22 - 0.927050983124841;
                    }
                }
            } else {
                byte byte20;
                byte byte21;
                if (boolean2) {
                    byte20 = byte15;
                    byte21 = byte16;
                } else {
                    byte20 = byte16;
                    byte21 = byte15;
                }

                if ((byte20 & 1) != 0) {
                    int4 = int0 + 2;
                    int8 = int0 + 1;
                    double24 = double19 - 2.0 - 0.927050983124841;
                    double28 = double19 - 1.0 - 0.927050983124841;
                } else {
                    int8 = int0;
                    int4 = int0;
                    double24 = double28 = double19 - 0.927050983124841;
                }

                if ((byte20 & 2) != 0) {
                    int5 = int9 = int1 + 1;
                    double25 = double29 = double20 - 1.0 - 0.927050983124841;
                    if ((byte20 & 1) == 0) {
                        int5++;
                        double25--;
                    } else {
                        int9++;
                        double29--;
                    }
                } else {
                    int9 = int1;
                    int5 = int1;
                    double25 = double29 = double20 - 0.927050983124841;
                }

                if ((byte20 & 4) != 0) {
                    int6 = int10 = int2 + 1;
                    double26 = double30 = double21 - 1.0 - 0.927050983124841;
                    if ((byte20 & 3) == 0) {
                        int6++;
                        double26--;
                    } else {
                        int10++;
                        double30--;
                    }
                } else {
                    int10 = int2;
                    int6 = int2;
                    double26 = double30 = double21 - 0.927050983124841;
                }

                if ((byte20 & 8) != 0) {
                    int7 = int3 + 1;
                    int11 = int3 + 2;
                    double27 = double22 - 1.0 - 0.927050983124841;
                    double31 = double22 - 2.0 - 0.927050983124841;
                } else {
                    int11 = int3;
                    int7 = int3;
                    double27 = double31 = double22 - 0.927050983124841;
                }

                int12 = int0 + 1;
                int13 = int1 + 1;
                int14 = int2 + 1;
                int15 = int3 + 1;
                double32 = double19 - 1.0 - 0.618033988749894;
                double33 = double20 - 1.0 - 0.618033988749894;
                double34 = double21 - 1.0 - 0.618033988749894;
                double35 = double22 - 1.0 - 0.618033988749894;
                if ((byte21 & 1) == 0) {
                    int12 -= 2;
                    double32 += 2.0;
                } else if ((byte21 & 2) == 0) {
                    int13 -= 2;
                    double33 += 2.0;
                } else if ((byte21 & 4) == 0) {
                    int14 -= 2;
                    double34 += 2.0;
                } else {
                    int15 -= 2;
                    double35 += 2.0;
                }
            }

            double double126 = double19 - 1.0 - 0.927050983124841;
            double double127 = double20 - 1.0 - 0.927050983124841;
            double double128 = double21 - 1.0 - 0.927050983124841;
            double double129 = double22 - 0.927050983124841;
            double double130 = 2.0 - double126 * double126 - double127 * double127 - double128 * double128 - double129 * double129;
            if (double130 > 0.0) {
                double130 *= double130;
                double23 += double130 * double130 * this.extrapolate(int0 + 1, int1 + 1, int2 + 1, int3 + 0, double126, double127, double128, double129);
            }

            double double131 = double21 - 0.927050983124841;
            double double132 = double22 - 1.0 - 0.927050983124841;
            double double133 = 2.0 - double126 * double126 - double127 * double127 - double131 * double131 - double132 * double132;
            if (double133 > 0.0) {
                double133 *= double133;
                double23 += double133 * double133 * this.extrapolate(int0 + 1, int1 + 1, int2 + 0, int3 + 1, double126, double127, double131, double132);
            }

            double double134 = double20 - 0.927050983124841;
            double double135 = 2.0 - double126 * double126 - double134 * double134 - double128 * double128 - double132 * double132;
            if (double135 > 0.0) {
                double135 *= double135;
                double23 += double135 * double135 * this.extrapolate(int0 + 1, int1 + 0, int2 + 1, int3 + 1, double126, double134, double128, double132);
            }

            double double136 = double19 - 0.927050983124841;
            double double137 = 2.0 - double136 * double136 - double127 * double127 - double128 * double128 - double132 * double132;
            if (double137 > 0.0) {
                double137 *= double137;
                double23 += double137 * double137 * this.extrapolate(int0 + 0, int1 + 1, int2 + 1, int3 + 1, double136, double127, double128, double132);
            }

            double double138 = double19 - 1.0 - 0.618033988749894;
            double double139 = double20 - 1.0 - 0.618033988749894;
            double double140 = double21 - 0.0 - 0.618033988749894;
            double double141 = double22 - 0.0 - 0.618033988749894;
            double double142 = 2.0 - double138 * double138 - double139 * double139 - double140 * double140 - double141 * double141;
            if (double142 > 0.0) {
                double142 *= double142;
                double23 += double142 * double142 * this.extrapolate(int0 + 1, int1 + 1, int2 + 0, int3 + 0, double138, double139, double140, double141);
            }

            double double143 = double19 - 1.0 - 0.618033988749894;
            double double144 = double20 - 0.0 - 0.618033988749894;
            double double145 = double21 - 1.0 - 0.618033988749894;
            double double146 = double22 - 0.0 - 0.618033988749894;
            double double147 = 2.0 - double143 * double143 - double144 * double144 - double145 * double145 - double146 * double146;
            if (double147 > 0.0) {
                double147 *= double147;
                double23 += double147 * double147 * this.extrapolate(int0 + 1, int1 + 0, int2 + 1, int3 + 0, double143, double144, double145, double146);
            }

            double double148 = double19 - 1.0 - 0.618033988749894;
            double double149 = double20 - 0.0 - 0.618033988749894;
            double double150 = double21 - 0.0 - 0.618033988749894;
            double double151 = double22 - 1.0 - 0.618033988749894;
            double double152 = 2.0 - double148 * double148 - double149 * double149 - double150 * double150 - double151 * double151;
            if (double152 > 0.0) {
                double152 *= double152;
                double23 += double152 * double152 * this.extrapolate(int0 + 1, int1 + 0, int2 + 0, int3 + 1, double148, double149, double150, double151);
            }

            double double153 = double19 - 0.0 - 0.618033988749894;
            double double154 = double20 - 1.0 - 0.618033988749894;
            double double155 = double21 - 1.0 - 0.618033988749894;
            double double156 = double22 - 0.0 - 0.618033988749894;
            double double157 = 2.0 - double153 * double153 - double154 * double154 - double155 * double155 - double156 * double156;
            if (double157 > 0.0) {
                double157 *= double157;
                double23 += double157 * double157 * this.extrapolate(int0 + 0, int1 + 1, int2 + 1, int3 + 0, double153, double154, double155, double156);
            }

            double double158 = double19 - 0.0 - 0.618033988749894;
            double double159 = double20 - 1.0 - 0.618033988749894;
            double double160 = double21 - 0.0 - 0.618033988749894;
            double double161 = double22 - 1.0 - 0.618033988749894;
            double double162 = 2.0 - double158 * double158 - double159 * double159 - double160 * double160 - double161 * double161;
            if (double162 > 0.0) {
                double162 *= double162;
                double23 += double162 * double162 * this.extrapolate(int0 + 0, int1 + 1, int2 + 0, int3 + 1, double158, double159, double160, double161);
            }

            double double163 = double19 - 0.0 - 0.618033988749894;
            double double164 = double20 - 0.0 - 0.618033988749894;
            double double165 = double21 - 1.0 - 0.618033988749894;
            double double166 = double22 - 1.0 - 0.618033988749894;
            double double167 = 2.0 - double163 * double163 - double164 * double164 - double165 * double165 - double166 * double166;
            if (double167 > 0.0) {
                double167 *= double167;
                double23 += double167 * double167 * this.extrapolate(int0 + 0, int1 + 0, int2 + 1, int3 + 1, double163, double164, double165, double166);
            }
        }

        double double168 = 2.0 - double24 * double24 - double25 * double25 - double26 * double26 - double27 * double27;
        if (double168 > 0.0) {
            double168 *= double168;
            double23 += double168 * double168 * this.extrapolate(int4, int5, int6, int7, double24, double25, double26, double27);
        }

        double double169 = 2.0 - double28 * double28 - double29 * double29 - double30 * double30 - double31 * double31;
        if (double169 > 0.0) {
            double169 *= double169;
            double23 += double169 * double169 * this.extrapolate(int8, int9, int10, int11, double28, double29, double30, double31);
        }

        double double170 = 2.0 - double32 * double32 - double33 * double33 - double34 * double34 - double35 * double35;
        if (double170 > 0.0) {
            double170 *= double170;
            double23 += double170 * double170 * this.extrapolate(int12, int13, int14, int15, double32, double33, double34, double35);
        }

        return double23 / 30.0;
    }

    private double extrapolate(int int2, int int1, double double1, double double0) {
        int int0 = this.perm[this.perm[int2 & 0xFF] + int1 & 0xFF] & 14;
        return gradients2D[int0] * double1 + gradients2D[int0 + 1] * double0;
    }

    private double extrapolate(int int2, int int1, int int0, double double2, double double1, double double0) {
        short short0 = this.permGradIndex3D[this.perm[this.perm[int2 & 0xFF] + int1 & 0xFF] + int0 & 0xFF];
        return gradients3D[short0] * double2 + gradients3D[short0 + 1] * double1 + gradients3D[short0 + 2] * double0;
    }

    private double extrapolate(int int4, int int3, int int2, int int1, double double3, double double2, double double1, double double0) {
        int int0 = this.perm[this.perm[this.perm[this.perm[int4 & 0xFF] + int3 & 0xFF] + int2 & 0xFF] + int1 & 0xFF] & 252;
        return gradients4D[int0] * double3 + gradients4D[int0 + 1] * double2 + gradients4D[int0 + 2] * double1 + gradients4D[int0 + 3] * double0;
    }

    private static int fastFloor(double double0) {
        int int0 = (int)double0;
        return double0 < int0 ? int0 - 1 : int0;
    }

    public double evalOct(float float1, float float0, int int0) {
        boolean boolean0 = true;
        double double0 = this.eval(float1, float0, int0);

        for (int int1 = 2; int1 <= 64; int1++) {
            double0 += this.eval(float1 * int1 * float1, float0 * int1 * float0, int0 * int1 * int0);
        }

        return double0;
    }
}
