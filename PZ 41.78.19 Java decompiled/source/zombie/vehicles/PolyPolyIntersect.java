// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import org.joml.Vector2f;

public final class PolyPolyIntersect {
    private static Vector2f tempVector2f_1 = new Vector2f();
    private static Vector2f tempVector2f_2 = new Vector2f();
    private static Vector2f tempVector2f_3 = new Vector2f();

    public static boolean intersects(PolygonalMap2.VehiclePoly vehiclePoly1, PolygonalMap2.VehiclePoly vehiclePoly2) {
        for (int int0 = 0; int0 < 2; int0++) {
            PolygonalMap2.VehiclePoly vehiclePoly0 = int0 == 0 ? vehiclePoly1 : vehiclePoly2;

            for (int int1 = 0; int1 < 4; int1++) {
                int int2 = (int1 + 1) % 4;
                Vector2f vector2f0 = getPoint(vehiclePoly0, int1, tempVector2f_1);
                Vector2f vector2f1 = getPoint(vehiclePoly0, int2, tempVector2f_2);
                Vector2f vector2f2 = tempVector2f_3.set(vector2f1.y - vector2f0.y, vector2f0.x - vector2f1.x);
                double double0 = Double.MAX_VALUE;
                double double1 = Double.NEGATIVE_INFINITY;

                for (int int3 = 0; int3 < 4; int3++) {
                    Vector2f vector2f3 = getPoint(vehiclePoly1, int3, tempVector2f_1);
                    double double2 = vector2f2.x * vector2f3.x + vector2f2.y * vector2f3.y;
                    if (double2 < double0) {
                        double0 = double2;
                    }

                    if (double2 > double1) {
                        double1 = double2;
                    }
                }

                double double3 = Double.MAX_VALUE;
                double double4 = Double.NEGATIVE_INFINITY;

                for (int int4 = 0; int4 < 4; int4++) {
                    Vector2f vector2f4 = getPoint(vehiclePoly2, int4, tempVector2f_1);
                    double double5 = vector2f2.x * vector2f4.x + vector2f2.y * vector2f4.y;
                    if (double5 < double3) {
                        double3 = double5;
                    }

                    if (double5 > double4) {
                        double4 = double5;
                    }
                }

                if (double1 < double3 || double4 < double0) {
                    return false;
                }
            }
        }

        return true;
    }

    private static Vector2f getPoint(PolygonalMap2.VehiclePoly vehiclePoly, int int0, Vector2f vector2f) {
        if (int0 == 0) {
            return vector2f.set(vehiclePoly.x1, vehiclePoly.y1);
        } else if (int0 == 1) {
            return vector2f.set(vehiclePoly.x2, vehiclePoly.y2);
        } else if (int0 == 2) {
            return vector2f.set(vehiclePoly.x3, vehiclePoly.y3);
        } else {
            return int0 == 3 ? vector2f.set(vehiclePoly.x4, vehiclePoly.y4) : null;
        }
    }
}
