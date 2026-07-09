// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

import java.util.function.Function;

public class PooledArrayObject<T> extends PooledObject {
    private T[] m_array = null;

    public T[] array() {
        return this.m_array;
    }

    public int length() {
        return this.m_array.length;
    }

    public T get(int int0) {
        return this.m_array[int0];
    }

    public void set(int int0, T object) {
        this.m_array[int0] = (T)object;
    }

    protected void initCapacity(int int0, Function<Integer, T[]> function) {
        if (this.m_array == null || this.m_array.length != int0) {
            this.m_array = (T[])((Object[])function.apply(int0));
        }
    }

    public boolean isEmpty() {
        return this.m_array == null || this.m_array.length == 0;
    }
}
