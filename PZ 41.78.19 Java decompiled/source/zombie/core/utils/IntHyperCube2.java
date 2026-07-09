// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.utils;

import java.util.Arrays;

public class IntHyperCube2 {
    private static final long serialVersionUID = 1L;
    private final int width;
    private final int height;
    private final int depth;
    private final int quanta;
    private final int wxh;
    private final int wxhxd;
    private final int[][][][] value;

    public IntHyperCube2(int int0, int int1, int int2, int int3) {
        this.width = int0;
        this.height = int1;
        this.depth = int2;
        this.quanta = int3;
        this.wxh = int0 * int1;
        this.wxhxd = this.wxh * int2;
        this.value = new int[int0][int1][int2][int3];
    }

    public void clear() {
        Arrays.fill(this.value, Integer.valueOf(0));
    }

    public void fill(int int0) {
        Arrays.fill(this.value, Integer.valueOf(int0));
    }

    private int getIndex(int int2, int int3, int int1, int int0) {
        return int2 >= 0 && int3 >= 0 && int1 >= 0 && int0 >= 0 && int2 < this.width && int3 < this.height && int1 < this.depth && int0 < this.quanta
            ? int2 + int3 * this.width + int1 * this.wxh + int0 * this.wxhxd
            : -1;
    }

    public int getValue(int int3, int int2, int int1, int int0) {
        return int3 >= 0 && int2 >= 0 && int1 >= 0 && int0 >= 0 && int3 < this.width && int2 < this.height && int1 < this.depth && int0 < this.quanta
            ? this.value[int3][int2][int1][int0]
            : 0;
    }

    public void setValue(int int4, int int3, int int2, int int1, int int0) {
        this.value[int4][int3][int2][int1] = int0;
    }

    public final int getWidth() {
        return this.width;
    }

    public final int getHeight() {
        return this.height;
    }

    public final int getDepth() {
        return this.depth;
    }

    public final int getQuanta() {
        return this.quanta;
    }
}
