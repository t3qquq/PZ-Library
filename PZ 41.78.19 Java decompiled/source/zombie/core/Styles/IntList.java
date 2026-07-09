// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.Styles;

import java.io.Serializable;

public class IntList implements Serializable {
    private static final long serialVersionUID = 1L;
    private int[] value;
    private int count = 0;
    private final boolean fastExpand;

    public IntList() {
        this(0);
    }

    public IntList(int int0) {
        this(true, int0);
    }

    public IntList(boolean boolean0, int int0) {
        this.fastExpand = boolean0;
        this.value = new int[int0];
    }

    public int add(short short0) {
        if (this.count == this.value.length) {
            int[] ints = this.value;
            if (this.fastExpand) {
                this.value = new int[(ints.length << 1) + 1];
            } else {
                this.value = new int[ints.length + 1];
            }

            System.arraycopy(ints, 0, this.value, 0, ints.length);
        }

        this.value[this.count] = short0;
        return this.count++;
    }

    public int remove(int int0) {
        if (int0 < this.count && int0 >= 0) {
            int int1 = this.value[int0];
            if (int0 < this.count - 1) {
                System.arraycopy(this.value, int0 + 1, this.value, int0, this.count - int0 - 1);
            }

            this.count--;
            return int1;
        } else {
            throw new IndexOutOfBoundsException("Referenced " + int0 + ", size=" + this.count);
        }
    }

    public void addAll(short[] shorts) {
        this.ensureCapacity(this.count + shorts.length);
        System.arraycopy(shorts, 0, this.value, this.count, shorts.length);
        this.count += shorts.length;
    }

    public void addAll(IntList intList1) {
        this.ensureCapacity(this.count + intList1.count);
        System.arraycopy(intList1.value, 0, this.value, this.count, intList1.count);
        this.count = this.count + intList1.count;
    }

    public int[] array() {
        return this.value;
    }

    public int capacity() {
        return this.value.length;
    }

    public void clear() {
        this.count = 0;
    }

    public void ensureCapacity(int int0) {
        if (this.value.length < int0) {
            int[] ints = this.value;
            this.value = new int[int0];
            System.arraycopy(ints, 0, this.value, 0, ints.length);
        }
    }

    public int get(int int0) {
        return this.value[int0];
    }

    public boolean isEmpty() {
        return this.count == 0;
    }

    public int size() {
        return this.count;
    }

    public short[] toArray(short[] shorts) {
        if (shorts == null) {
            shorts = new short[this.count];
        }

        System.arraycopy(this.value, 0, shorts, 0, this.count);
        return shorts;
    }

    public void trimToSize() {
        if (this.count != this.value.length) {
            int[] ints = this.value;
            this.value = new int[this.count];
            System.arraycopy(ints, 0, this.value, 0, this.count);
        }
    }
}
