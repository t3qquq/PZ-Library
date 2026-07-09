// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.util.HashMap;

public class CollisionMatrixPrototypes {
    public static CollisionMatrixPrototypes instance = new CollisionMatrixPrototypes();
    public HashMap<Integer, boolean[][][]> Map = new HashMap<>();

    public int ToBitMatrix(boolean[][][] booleans) {
        int int0 = 0;

        for (int int1 = 0; int1 < 3; int1++) {
            for (int int2 = 0; int2 < 3; int2++) {
                for (int int3 = 0; int3 < 3; int3++) {
                    if (booleans[int1][int2][int3]) {
                        int0 = BitMatrix.Set(int0, int1 - 1, int2 - 1, int3 - 1, true);
                    }
                }
            }
        }

        return int0;
    }

    public boolean[][][] Add(int int0) {
        if (this.Map.containsKey(int0)) {
            return this.Map.get(int0);
        } else {
            boolean[][][] booleans = new boolean[3][3][3];

            for (int int1 = 0; int1 < 3; int1++) {
                for (int int2 = 0; int2 < 3; int2++) {
                    for (int int3 = 0; int3 < 3; int3++) {
                        booleans[int1][int2][int3] = BitMatrix.Is(int0, int1 - 1, int2 - 1, int3 - 1);
                    }
                }
            }

            this.Map.put(int0, booleans);
            return booleans;
        }
    }
}
