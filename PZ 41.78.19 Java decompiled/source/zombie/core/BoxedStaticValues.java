// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core;

public class BoxedStaticValues {
    static Double[] doubles = new Double[10000];
    static Double[] negdoubles = new Double[10000];
    static Double[] doublesh = new Double[10000];
    static Double[] negdoublesh = new Double[10000];

    public static Double toDouble(double double0) {
        if (double0 >= 10000.0) {
            return double0;
        } else if (double0 <= -10000.0) {
            return double0;
        } else if ((int)Math.abs(double0) == Math.abs(double0)) {
            return double0 < 0.0 ? negdoubles[(int)(-double0)] : doubles[(int)double0];
        } else if ((int)Math.abs(double0) == Math.abs(double0) - 0.5) {
            return double0 < 0.0 ? negdoublesh[(int)(-double0)] : doublesh[(int)double0];
        } else {
            return double0;
        }
    }

    static {
        for (int int0 = 0; int0 < 10000; int0++) {
            doubles[int0] = (double)int0;
            negdoubles[int0] = -doubles[int0];
            doublesh[int0] = int0 + 0.5;
            negdoublesh[int0] = -(doubles[int0] + 0.5);
        }
    }
}
