// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.utils;

import java.io.Serializable;
import java.util.Arrays;

public class ObjectGrid<T> implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private final int width;
    private final int height;
    private final Object[] value;

    public ObjectGrid(int int0, int int1) {
        this.width = int0;
        this.height = int1;
        this.value = new Object[int0 * int1];
    }

    public ObjectGrid<T> clone() throws CloneNotSupportedException {
        ObjectGrid objectGrid0 = new ObjectGrid(this.width, this.height);
        System.arraycopy(this.value, 0, objectGrid0.value, 0, this.value.length);
        return objectGrid0;
    }

    public void clear() {
        Arrays.fill(this.value, Integer.valueOf(0));
    }

    public void fill(T object) {
        Arrays.fill(this.value, object);
    }

    private int getIndex(int int0, int int1) {
        return int0 >= 0 && int1 >= 0 && int0 < this.width && int1 < this.height ? int0 + int1 * this.width : -1;
    }

    public T getValue(int int1, int int2) {
        int int0 = this.getIndex(int1, int2);
        return (T)(int0 == -1 ? null : this.value[int0]);
    }

    public void setValue(int int1, int int2, T object) {
        int int0 = this.getIndex(int1, int2);
        if (int0 != -1) {
            this.value[int0] = object;
        }
    }

    public final int getWidth() {
        return this.width;
    }

    public final int getHeight() {
        return this.height;
    }
}
