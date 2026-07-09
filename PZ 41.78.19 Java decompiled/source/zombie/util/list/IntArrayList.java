// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import zombie.util.IntCollection;
import zombie.util.hash.DefaultIntHashFunction;
import zombie.util.util.Exceptions;

public class IntArrayList extends AbstractIntList implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;
    private static final int GROWTH_POLICY_RELATIVE = 0;
    private static final int GROWTH_POLICY_ABSOLUTE = 1;
    private static final int DEFAULT_GROWTH_POLICY = 0;
    public static final double DEFAULT_GROWTH_FACTOR = 1.0;
    public static final int DEFAULT_GROWTH_CHUNK = 10;
    public static final int DEFAULT_CAPACITY = 10;
    private transient int[] data;
    private int size;
    private int growthPolicy;
    private double growthFactor;
    private int growthChunk;

    private IntArrayList(int int0, int int2, double double0, int int1) {
        if (int0 < 0) {
            Exceptions.negativeArgument("capacity", String.valueOf(int0));
        }

        if (double0 < 0.0) {
            Exceptions.negativeArgument("growthFactor", String.valueOf(double0));
        }

        if (int1 < 0) {
            Exceptions.negativeArgument("growthChunk", String.valueOf(int1));
        }

        this.data = new int[int0];
        this.size = 0;
        this.growthPolicy = int2;
        this.growthFactor = double0;
        this.growthChunk = int1;
    }

    public IntArrayList() {
        this(10);
    }

    public IntArrayList(IntCollection intCollection) {
        this(intCollection.size());
        this.addAll(intCollection);
    }

    public IntArrayList(int[] ints) {
        this(ints.length);
        System.arraycopy(ints, 0, this.data, 0, ints.length);
        this.size = ints.length;
    }

    public IntArrayList(int int0) {
        this(int0, 1.0);
    }

    public IntArrayList(int int0, double double0) {
        this(int0, 0, double0, 10);
    }

    public IntArrayList(int int0, int int1) {
        this(int0, 1, 1.0, int1);
    }

    private int computeCapacity(int int1) {
        int int0;
        if (this.growthPolicy == 0) {
            int0 = (int)(this.data.length * (1.0 + this.growthFactor));
        } else {
            int0 = this.data.length + this.growthChunk;
        }

        if (int0 < int1) {
            int0 = int1;
        }

        return int0;
    }

    public int ensureCapacity(int int0) {
        if (int0 > this.data.length) {
            int[] ints = new int[int0 = this.computeCapacity(int0)];
            System.arraycopy(this.data, 0, ints, 0, this.size);
            this.data = ints;
        }

        return int0;
    }

    public int capacity() {
        return this.data.length;
    }

    @Override
    public void add(int int0, int int2) {
        if (int0 < 0 || int0 > this.size) {
            Exceptions.indexOutOfBounds(int0, 0, this.size);
        }

        this.ensureCapacity(this.size + 1);
        int int1 = this.size - int0;
        if (int1 > 0) {
            System.arraycopy(this.data, int0, this.data, int0 + 1, int1);
        }

        this.data[int0] = int2;
        this.size++;
    }

    @Override
    public int get(int int0) {
        if (int0 < 0 || int0 >= this.size) {
            Exceptions.indexOutOfBounds(int0, 0, this.size - 1);
        }

        return this.data[int0];
    }

    @Override
    public int set(int int0, int int2) {
        if (int0 < 0 || int0 >= this.size) {
            Exceptions.indexOutOfBounds(int0, 0, this.size - 1);
        }

        int int1 = this.data[int0];
        this.data[int0] = int2;
        return int1;
    }

    @Override
    public int removeElementAt(int int0) {
        if (int0 < 0 || int0 >= this.size) {
            Exceptions.indexOutOfBounds(int0, 0, this.size - 1);
        }

        int int1 = this.data[int0];
        int int2 = this.size - (int0 + 1);
        if (int2 > 0) {
            System.arraycopy(this.data, int0 + 1, this.data, int0, int2);
        }

        this.size--;
        return int1;
    }

    @Override
    public void trimToSize() {
        if (this.data.length > this.size) {
            int[] ints = new int[this.size];
            System.arraycopy(this.data, 0, ints, 0, this.size);
            this.data = ints;
        }
    }

    @Override
    public Object clone() {
        try {
            IntArrayList intArrayList0 = (IntArrayList)super.clone();
            intArrayList0.data = new int[this.data.length];
            System.arraycopy(this.data, 0, intArrayList0.data, 0, this.size);
            return intArrayList0;
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            Exceptions.cloning();
            return null;
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public void clear() {
        this.size = 0;
    }

    @Override
    public boolean contains(int int1) {
        for (int int0 = 0; int0 < this.size; int0++) {
            if (this.data[int0] == int1) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int indexOf(int int1) {
        for (int int0 = 0; int0 < this.size; int0++) {
            if (this.data[int0] == int1) {
                return int0;
            }
        }

        return -1;
    }

    @Override
    public int indexOf(int int0, int int2) {
        if (int0 < 0 || int0 > this.size) {
            Exceptions.indexOutOfBounds(int0, 0, this.size);
        }

        for (int int1 = int0; int1 < this.size; int1++) {
            if (this.data[int1] == int2) {
                return int1;
            }
        }

        return -1;
    }

    @Override
    public int lastIndexOf(int int1) {
        for (int int0 = this.size - 1; int0 >= 0; int0--) {
            if (this.data[int0] == int1) {
                return int0;
            }
        }

        return -1;
    }

    @Override
    public boolean remove(int int1) {
        int int0 = this.indexOf(int1);
        if (int0 != -1) {
            this.removeElementAt(int0);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int[] toArray() {
        int[] ints = new int[this.size];
        System.arraycopy(this.data, 0, ints, 0, this.size);
        return ints;
    }

    @Override
    public int[] toArray(int[] ints) {
        if (ints == null || ints.length < this.size) {
            ints = new int[this.size];
        }

        System.arraycopy(this.data, 0, ints, 0, this.size);
        return ints;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof IntList)) {
            return false;
        } else {
            int int0 = 0;
            IntListIterator intListIterator = ((IntList)object).listIterator();

            while (int0 < this.size && intListIterator.hasNext()) {
                if (this.data[int0++] != intListIterator.next()) {
                    return false;
                }
            }

            return int0 >= this.size && !intListIterator.hasNext();
        }
    }

    @Override
    public int hashCode() {
        int int0 = 1;

        for (int int1 = 0; int1 < this.size; int1++) {
            int0 = 31 * int0 + DefaultIntHashFunction.INSTANCE.hash(this.data[int1]);
        }

        return int0;
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeInt(this.data.length);

        for (int int0 = 0; int0 < this.size; int0++) {
            objectOutputStream.writeInt(this.data[int0]);
        }
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.data = new int[objectInputStream.readInt()];

        for (int int0 = 0; int0 < this.size; int0++) {
            this.data[int0] = objectInputStream.readInt();
        }
    }
}
