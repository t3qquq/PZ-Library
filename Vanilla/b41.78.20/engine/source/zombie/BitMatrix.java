// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

public class BitMatrix {
    public static boolean Is(int int3, int int2, int int1, int int0) {
        return (1 << (int2 + 1) * 9 + (int1 + 1) * 3 + int0 + 1 & int3) == 1 << (int2 + 1) * 9 + (int1 + 1) * 3 + int0 + 1;
    }

    public static int Set(int int0, int int3, int int2, int int1, boolean boolean0) {
        if (boolean0) {
            int0 |= 1 << (int3 + 1) * 9 + (int2 + 1) * 3 + int1 + 1;
        } else {
            int0 &= ~(1 << (int3 + 1) * 9 + (int2 + 1) * 3 + int1 + 1);
        }

        return int0;
    }
}
