// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core;

public class FastTrig {
    public static double cos(double double0) {
        return sin(double0 + (Math.PI / 2));
    }

    public static double sin(double double0) {
        double0 = reduceSinAngle(double0);
        return Math.abs(double0) <= Math.PI / 4 ? Math.sin(double0) : Math.cos((Math.PI / 2) - double0);
    }

    private static double reduceSinAngle(double double0) {
        double0 %= Math.PI * 2;
        if (Math.abs(double0) > Math.PI) {
            double0 -= Math.PI * 2;
        }

        if (Math.abs(double0) > Math.PI / 2) {
            double0 = Math.PI - double0;
        }

        return double0;
    }
}
