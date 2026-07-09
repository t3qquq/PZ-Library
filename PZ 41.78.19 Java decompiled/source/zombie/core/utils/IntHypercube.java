// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.utils;

import java.io.Serializable;
import java.util.Arrays;

public class IntHypercube implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private final int width;
    private final int height;
    private final int depth;
    private final int quanta;
    private final int wxh;
    private final int wxhxd;
    private final int[] value;

    public IntHypercube(int int0, int int1, int int2, int int3) {
        this.width = int0;
        this.height = int1;
        this.depth = int2;
        this.quanta = int3;
        this.wxh = int0 * int1;
        this.wxhxd = this.wxh * int2;
        this.value = new int[this.wxhxd * int3];
    }

    public IntHypercube clone() throws CloneNotSupportedException {
        IntHypercube intHypercube0 = new IntHypercube(this.width, this.height, this.depth, this.quanta);
        System.arraycopy(this.value, 0, intHypercube0.value, 0, this.value.length);
        return intHypercube0;
    }

    public void clear() {
        Arrays.fill(this.value, 0);
    }

    public void fill(int int0) {
        Arrays.fill(this.value, int0);
    }

    private int getIndex(int int2, int int3, int int1, int int0) {
        return int2 >= 0 && int3 >= 0 && int1 >= 0 && int0 >= 0 && int2 < this.width && int3 < this.height && int1 < this.depth && int0 < this.quanta
            ? int2 + int3 * this.width + int1 * this.wxh + int0 * this.wxhxd
            : -1;
    }

    public int getValue(int int1, int int2, int int3, int int4) {
        int int0 = this.getIndex(int1, int2, int3, int4);
        return int0 == -1 ? 0 : this.value[int0];
    }

    public void setValue(int int1, int int2, int int3, int int4, int int5) {
        int int0 = this.getIndex(int1, int2, int3, int4);
        if (int0 != -1) {
            this.value[int0] = int5;
        }
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
