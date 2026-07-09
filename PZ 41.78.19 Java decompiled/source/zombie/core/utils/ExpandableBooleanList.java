// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.utils;

import java.io.Serializable;
import java.util.Arrays;

public class ExpandableBooleanList implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private int width;
    private int bitWidth;
    private int[] value;

    public ExpandableBooleanList(int int0) {
        this.bitWidth = int0;
        this.width = int0 / 32 + (int0 % 32 != 0 ? 1 : 0);
        this.value = new int[this.width];
    }

    public ExpandableBooleanList clone() throws CloneNotSupportedException {
        ExpandableBooleanList expandableBooleanList0 = new ExpandableBooleanList(this.bitWidth);
        System.arraycopy(this.value, 0, expandableBooleanList0.value, 0, this.value.length);
        return expandableBooleanList0;
    }

    public void clear() {
        Arrays.fill(this.value, 0);
    }

    public void fill() {
        Arrays.fill(this.value, -1);
    }

    public boolean getValue(int int0) {
        if (int0 >= 0 && int0 < this.bitWidth) {
            int int1 = int0 >> 5;
            int int2 = 1 << (int0 & 31);
            int int3 = this.value[int1];
            return (int3 & int2) != 0;
        } else {
            return false;
        }
    }

    public void setValue(int int0, boolean boolean0) {
        if (int0 >= 0) {
            if (int0 >= this.bitWidth) {
                int[] ints = this.value;
                this.bitWidth = Math.max(this.bitWidth * 2, int0 + 1);
                this.width = this.bitWidth / 32 + (this.width % 32 != 0 ? 1 : 0);
                this.value = new int[this.width];
                System.arraycopy(ints, 0, this.value, 0, ints.length);
            }

            int int1 = int0 >> 5;
            int int2 = 1 << (int0 & 31);
            if (boolean0) {
                this.value[int1] = this.value[int1] | int2;
            } else {
                this.value[int1] = this.value[int1] & ~int2;
            }
        }
    }

    public final int getWidth() {
        return this.width;
    }
}
