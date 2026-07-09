// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

class VecMathUtil {
    static int floatToIntBits(float float0) {
        return float0 == 0.0F ? 0 : Float.floatToIntBits(float0);
    }

    static long doubleToLongBits(double double0) {
        return double0 == 0.0 ? 0L : Double.doubleToLongBits(double0);
    }

    private VecMathUtil() {
    }
}
