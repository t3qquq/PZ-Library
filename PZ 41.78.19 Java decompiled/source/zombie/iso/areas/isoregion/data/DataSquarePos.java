// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas.isoregion.data;

import java.util.ArrayDeque;
import zombie.iso.areas.isoregion.IsoRegions;

public final class DataSquarePos {
    public static boolean DEBUG_POOL = true;
    private static final ArrayDeque<DataSquarePos> pool = new ArrayDeque<>();
    public int x;
    public int y;
    public int z;

    static DataSquarePos alloc(int int0, int int1, int int2) {
        DataSquarePos dataSquarePos = !pool.isEmpty() ? pool.pop() : new DataSquarePos();
        dataSquarePos.set(int0, int1, int2);
        return dataSquarePos;
    }

    static void release(DataSquarePos dataSquarePos) {
        assert !pool.contains(dataSquarePos);

        if (DEBUG_POOL && pool.contains(dataSquarePos)) {
            IsoRegions.warn("DataSquarePos.release Trying to release a DataSquarePos twice.");
        } else {
            pool.push(dataSquarePos.reset());
        }
    }

    private DataSquarePos() {
    }

    private DataSquarePos reset() {
        return this;
    }

    public void set(int int0, int int1, int int2) {
        this.x = int0;
        this.y = int1;
        this.z = int2;
    }
}
