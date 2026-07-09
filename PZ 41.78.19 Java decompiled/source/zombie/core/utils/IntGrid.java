// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.utils;

import java.io.Serializable;
import java.util.Arrays;

public class IntGrid implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private final int width;
    private final int height;
    private final int[] value;

    /**
     * C'tor
     */
    public IntGrid(int _width, int _height) {
        this.width = _width;
        this.height = _height;
        this.value = new int[_width * _height];
    }

    public IntGrid clone() throws CloneNotSupportedException {
        IntGrid intGrid0 = new IntGrid(this.width, this.height);
        System.arraycopy(this.value, 0, intGrid0.value, 0, this.value.length);
        return intGrid0;
    }

    public void clear() {
        Arrays.fill(this.value, 0);
    }

    public void fill(int newValue) {
        Arrays.fill(this.value, newValue);
    }

    private int getIndex(int int0, int int1) {
        return int0 >= 0 && int1 >= 0 && int0 < this.width && int1 < this.height ? int0 + int1 * this.width : -1;
    }

    public int getValue(int x, int y) {
        int int0 = this.getIndex(x, y);
        return int0 == -1 ? 0 : this.value[int0];
    }

    public void setValue(int x, int y, int newValue) {
        int int0 = this.getIndex(x, y);
        if (int0 != -1) {
            this.value[int0] = newValue;
        }
    }

    /**
     * @return the width
     */
    public final int getWidth() {
        return this.width;
    }

    /**
     * @return the height
     */
    public final int getHeight() {
        return this.height;
    }
}
