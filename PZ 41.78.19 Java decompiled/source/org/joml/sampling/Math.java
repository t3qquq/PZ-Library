// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml.sampling;

class Math extends org.joml.Math {
    static final double PI = java.lang.Math.PI;
    static final double PI2 = java.lang.Math.PI * 2;
    static final double PIHalf = java.lang.Math.PI / 2;
    private static final double ONE_OVER_PI = 0.3183098861837907;
    private static final double s5 = Double.longBitsToDouble(4523227044276562163L);
    private static final double s4 = Double.longBitsToDouble(-4671934770969572232L);
    private static final double s3 = Double.longBitsToDouble(4575957211482072852L);
    private static final double s2 = Double.longBitsToDouble(-4628199223918090387L);
    private static final double s1 = Double.longBitsToDouble(4607182418589157889L);

    static double sin_roquen_9(double double1) {
        double double0 = java.lang.Math.rint(double1 * 0.3183098861837907);
        double double2 = double1 - double0 * java.lang.Math.PI;
        double double3 = 1 - 2 * ((int)double0 & 1);
        double double4 = double2 * double2;
        double2 = double3 * double2;
        double double5 = s5;
        double5 = double5 * double4 + s4;
        double5 = double5 * double4 + s3;
        double5 = double5 * double4 + s2;
        double5 = double5 * double4 + s1;
        return double2 * double5;
    }
}
