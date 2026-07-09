// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

public class Interpolationd {
    public static double interpolateTriangle(
        double double7,
        double double13,
        double double19,
        double double5,
        double double1,
        double double18,
        double double4,
        double double2,
        double double17,
        double double11,
        double double9
    ) {
        double double0 = double1 - double2;
        double double3 = double4 - double5;
        double double6 = double7 - double4;
        double double8 = double9 - double2;
        double double10 = double11 - double4;
        double double12 = double13 - double2;
        double double14 = 1.0 / (double0 * double6 + double3 * double12);
        double double15 = (double0 * double10 + double3 * double8) * double14;
        double double16 = (double6 * double8 - double12 * double10) * double14;
        return double15 * double19 + double16 * double18 + (1.0 - double15 - double16) * double17;
    }

    public static Vector2d interpolateTriangle(
        double double7,
        double double13,
        double double20,
        double double23,
        double double5,
        double double1,
        double double19,
        double double22,
        double double4,
        double double2,
        double double18,
        double double21,
        double double11,
        double double9,
        Vector2d vector2d
    ) {
        double double0 = double1 - double2;
        double double3 = double4 - double5;
        double double6 = double7 - double4;
        double double8 = double9 - double2;
        double double10 = double11 - double4;
        double double12 = double13 - double2;
        double double14 = 1.0 / (double0 * double6 + double3 * double12);
        double double15 = (double0 * double10 + double3 * double8) * double14;
        double double16 = (double6 * double8 - double12 * double10) * double14;
        double double17 = 1.0 - double15 - double16;
        vector2d.x = double15 * double20 + double16 * double19 + double17 * double18;
        vector2d.y = double15 * double23 + double16 * double22 + double17 * double21;
        return vector2d;
    }

    public static Vector2d dFdxLinear(
        double double8,
        double double4,
        double double13,
        double double16,
        double double7,
        double double1,
        double double12,
        double double15,
        double double6,
        double double2,
        double double11,
        double double14,
        Vector2d vector2d
    ) {
        double double0 = double1 - double2;
        double double3 = double4 - double2;
        double double5 = double0 * (double8 - double6) + (double6 - double7) * double3;
        double double9 = double5 - double0 + double3;
        double double10 = 1.0 / double5;
        vector2d.x = double10 * (double0 * double13 - double3 * double12 + double9 * double11) - double11;
        vector2d.y = double10 * (double0 * double16 - double3 * double15 + double9 * double14) - double14;
        return vector2d;
    }

    public static Vector2d dFdyLinear(
        double double4,
        double double6,
        double double13,
        double double16,
        double double2,
        double double8,
        double double12,
        double double15,
        double double1,
        double double7,
        double double11,
        double double14,
        Vector2d vector2d
    ) {
        double double0 = double1 - double2;
        double double3 = double4 - double1;
        double double5 = (double8 - double7) * double3 + double0 * (double6 - double7);
        double double9 = double5 - double0 - double3;
        double double10 = 1.0 / double5;
        vector2d.x = double10 * (double0 * double13 + double3 * double12 + double9 * double11) - double11;
        vector2d.y = double10 * (double0 * double16 + double3 * double15 + double9 * double14) - double14;
        return vector2d;
    }

    public static Vector3d interpolateTriangle(
        double double0,
        double double1,
        double double16,
        double double13,
        double double10,
        double double2,
        double double3,
        double double15,
        double double12,
        double double9,
        double double4,
        double double5,
        double double14,
        double double11,
        double double8,
        double double6,
        double double7,
        Vector3d vector3d
    ) {
        interpolationFactorsTriangle(double0, double1, double2, double3, double4, double5, double6, double7, vector3d);
        return vector3d.set(
            vector3d.x * double16 + vector3d.y * double15 + vector3d.z * double14,
            vector3d.x * double13 + vector3d.y * double12 + vector3d.z * double11,
            vector3d.x * double10 + vector3d.y * double9 + vector3d.z * double8
        );
    }

    public static Vector3d interpolationFactorsTriangle(
        double double7, double double13, double double5, double double1, double double4, double double2, double double11, double double9, Vector3d vector3d
    ) {
        double double0 = double1 - double2;
        double double3 = double4 - double5;
        double double6 = double7 - double4;
        double double8 = double9 - double2;
        double double10 = double11 - double4;
        double double12 = double13 - double2;
        double double14 = 1.0 / (double0 * double6 + double3 * double12);
        vector3d.x = (double0 * double10 + double3 * double8) * double14;
        vector3d.y = (double6 * double8 - double12 * double10) * double14;
        vector3d.z = 1.0 - vector3d.x - vector3d.y;
        return vector3d;
    }
}
