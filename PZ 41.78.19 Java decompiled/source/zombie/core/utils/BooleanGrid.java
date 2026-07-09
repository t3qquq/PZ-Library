// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.utils;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Boolean grid
 */
public class BooleanGrid implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private final int width;
    private final int height;
    private final int bitWidth;
    private final int[] value;

    /**
     * C'tor
     */
    public BooleanGrid(int _width, int _height) {
        this.bitWidth = _width;
        this.width = _width / 32 + (_width % 32 != 0 ? 1 : 0);
        this.height = _height;
        this.value = new int[this.width * this.height];
    }

    public BooleanGrid clone() throws CloneNotSupportedException {
        BooleanGrid booleanGrid0 = new BooleanGrid(this.bitWidth, this.height);
        System.arraycopy(this.value, 0, booleanGrid0.value, 0, this.value.length);
        return booleanGrid0;
    }

    public void copy(BooleanGrid src) {
        if (src.bitWidth == this.bitWidth && src.height == this.height) {
            System.arraycopy(src.value, 0, this.value, 0, src.value.length);
        } else {
            throw new IllegalArgumentException("src must be same size as this: " + src + " cannot be copied into " + this);
        }
    }

    public void clear() {
        Arrays.fill(this.value, 0);
    }

    public void fill() {
        Arrays.fill(this.value, -1);
    }

    private int getIndex(int int0, int int1) {
        return int0 >= 0 && int1 >= 0 && int0 < this.width && int1 < this.height ? int0 + int1 * this.width : -1;
    }

    public boolean getValue(int x, int y) {
        if (x < this.bitWidth && x >= 0 && y < this.height && y >= 0) {
            int int0 = x / 32;
            int int1 = 1 << (x & 31);
            int int2 = this.getIndex(int0, y);
            if (int2 == -1) {
                return false;
            } else {
                int int3 = this.value[int2];
                return (int3 & int1) != 0;
            }
        } else {
            return false;
        }
    }

    public void setValue(int x, int y, boolean newValue) {
        if (x < this.bitWidth && x >= 0 && y < this.height && y >= 0) {
            int int0 = x / 32;
            int int1 = 1 << (x & 31);
            int int2 = this.getIndex(int0, y);
            if (int2 != -1) {
                if (newValue) {
                    this.value[int2] = this.value[int2] | int1;
                } else {
                    this.value[int2] = this.value[int2] & ~int1;
                }
            }
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

    @Override
    public String toString() {
        return "BooleanGrid [width=" + this.width + ", height=" + this.height + ", bitWidth=" + this.bitWidth + "]";
    }

    public void LoadFromByteBuffer(ByteBuffer cache) {
        int int0 = this.width * this.height;

        for (int int1 = 0; int1 < int0; int1++) {
            this.value[int1] = cache.getInt();
        }
    }

    public void PutToByteBuffer(ByteBuffer cache) {
        int int0 = this.width * this.height;

        for (int int1 = 0; int1 < int0; int1++) {
            cache.putInt(this.value[int1]);
        }
    }
}
