// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.set;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import zombie.util.IntCollection;
import zombie.util.IntIterator;
import zombie.util.hash.DefaultIntHashFunction;
import zombie.util.hash.IntHashFunction;
import zombie.util.hash.Primes;
import zombie.util.util.Exceptions;

public class IntOpenHashSet extends AbstractIntSet implements IntSet, Cloneable, Serializable {
    private static final long serialVersionUID = 1L;
    private static final int GROWTH_POLICY_RELATIVE = 0;
    private static final int GROWTH_POLICY_ABSOLUTE = 1;
    private static final int DEFAULT_GROWTH_POLICY = 0;
    public static final double DEFAULT_GROWTH_FACTOR = 1.0;
    public static final int DEFAULT_GROWTH_CHUNK = 10;
    public static final int DEFAULT_CAPACITY = 11;
    public static final double DEFAULT_LOAD_FACTOR = 0.75;
    private IntHashFunction keyhash;
    private int size;
    private transient int[] data;
    private transient byte[] states;
    private static final byte EMPTY = 0;
    private static final byte OCCUPIED = 1;
    private static final byte REMOVED = 2;
    private transient int used;
    private int growthPolicy;
    private double growthFactor;
    private int growthChunk;
    private double loadFactor;
    private int expandAt;

    private IntOpenHashSet(IntHashFunction intHashFunction, int int0, int int2, double double0, int int1, double double1) {
        if (intHashFunction == null) {
            Exceptions.nullArgument("hash function");
        }

        if (int0 < 0) {
            Exceptions.negativeArgument("capacity", String.valueOf(int0));
        }

        if (double0 <= 0.0) {
            Exceptions.negativeOrZeroArgument("growthFactor", String.valueOf(double0));
        }

        if (int1 <= 0) {
            Exceptions.negativeOrZeroArgument("growthChunk", String.valueOf(int1));
        }

        if (double1 <= 0.0) {
            Exceptions.negativeOrZeroArgument("loadFactor", String.valueOf(double1));
        }

        this.keyhash = intHashFunction;
        int0 = Primes.nextPrime(int0);
        this.data = new int[int0];
        this.states = new byte[int0];
        this.size = 0;
        this.expandAt = (int)Math.round(double1 * int0);
        this.used = 0;
        this.growthPolicy = int2;
        this.growthFactor = double0;
        this.growthChunk = int1;
        this.loadFactor = double1;
    }

    private IntOpenHashSet(int int0, int int1, double double0, int int2, double double1) {
        this(DefaultIntHashFunction.INSTANCE, int0, int1, double0, int2, double1);
    }

    public IntOpenHashSet() {
        this(11);
    }

    public IntOpenHashSet(IntCollection intCollection) {
        this();
        this.addAll(intCollection);
    }

    public IntOpenHashSet(int[] ints) {
        this();

        for (int int0 : ints) {
            this.add(int0);
        }
    }

    public IntOpenHashSet(int int0) {
        this(int0, 0, 1.0, 10, 0.75);
    }

    public IntOpenHashSet(double double0) {
        this(11, 0, 1.0, 10, double0);
    }

    public IntOpenHashSet(int int0, double double0) {
        this(int0, 0, 1.0, 10, double0);
    }

    public IntOpenHashSet(int int0, double double1, double double0) {
        this(int0, 0, double0, 10, double1);
    }

    public IntOpenHashSet(int int0, double double0, int int1) {
        this(int0, 1, 1.0, int1, double0);
    }

    public IntOpenHashSet(IntHashFunction intHashFunction) {
        this(intHashFunction, 11, 0, 1.0, 10, 0.75);
    }

    public IntOpenHashSet(IntHashFunction intHashFunction, int int0) {
        this(intHashFunction, int0, 0, 1.0, 10, 0.75);
    }

    public IntOpenHashSet(IntHashFunction intHashFunction, double double0) {
        this(intHashFunction, 11, 0, 1.0, 10, double0);
    }

    public IntOpenHashSet(IntHashFunction intHashFunction, int int0, double double0) {
        this(intHashFunction, int0, 0, 1.0, 10, double0);
    }

    public IntOpenHashSet(IntHashFunction intHashFunction, int int0, double double1, double double0) {
        this(intHashFunction, int0, 0, double0, 10, double1);
    }

    public IntOpenHashSet(IntHashFunction intHashFunction, int int0, double double0, int int1) {
        this(intHashFunction, int0, 1, 1.0, int1, double0);
    }

    private void ensureCapacity(int int0) {
        if (int0 >= this.expandAt) {
            int int1;
            if (this.growthPolicy == 0) {
                int1 = (int)(this.data.length * (1.0 + this.growthFactor));
            } else {
                int1 = this.data.length + this.growthChunk;
            }

            if (int1 * this.loadFactor < int0) {
                int1 = (int)Math.round(int0 / this.loadFactor);
            }

            int1 = Primes.nextPrime(int1);
            this.expandAt = (int)Math.round(this.loadFactor * int1);
            int[] ints = new int[int1];
            byte[] bytes = new byte[int1];
            this.used = 0;

            for (int int2 = 0; int2 < this.data.length; int2++) {
                if (this.states[int2] == 1) {
                    this.used++;
                    int int3 = this.data[int2];
                    int int4 = Math.abs(this.keyhash.hash(int3));
                    int int5 = int4 % int1;
                    if (bytes[int5] == 1) {
                        int int6 = 1 + int4 % (int1 - 2);

                        do {
                            int5 -= int6;
                            if (int5 < 0) {
                                int5 += int1;
                            }
                        } while (bytes[int5] != 0);
                    }

                    bytes[int5] = 1;
                    ints[int5] = int3;
                }
            }

            this.data = ints;
            this.states = bytes;
        }
    }

    @Override
    public boolean add(int int1) {
        this.ensureCapacity(this.used + 1);
        int int0 = Math.abs(this.keyhash.hash(int1));
        int int2 = int0 % this.data.length;
        if (this.states[int2] == 1) {
            if (this.data[int2] == int1) {
                return false;
            }

            int int3 = 1 + int0 % (this.data.length - 2);

            while (true) {
                int2 -= int3;
                if (int2 < 0) {
                    int2 += this.data.length;
                }

                if (this.states[int2] == 0 || this.states[int2] == 2) {
                    break;
                }

                if (this.states[int2] == 1 && this.data[int2] == int1) {
                    return false;
                }
            }
        }

        if (this.states[int2] == 0) {
            this.used++;
        }

        this.states[int2] = 1;
        this.data[int2] = int1;
        this.size++;
        return true;
    }

    @Override
    public IntIterator iterator() {
        return new IntIterator() {
            int nextEntry = this.nextEntry(0);
            int lastEntry = -1;

            int nextEntry(int int0) {
                while (int0 < IntOpenHashSet.this.data.length && IntOpenHashSet.this.states[int0] != 1) {
                    int0++;
                }

                return int0;
            }

            @Override
            public boolean hasNext() {
                return this.nextEntry < IntOpenHashSet.this.data.length;
            }

            @Override
            public int next() {
                if (!this.hasNext()) {
                    Exceptions.endOfIterator();
                }

                this.lastEntry = this.nextEntry;
                this.nextEntry = this.nextEntry(this.nextEntry + 1);
                return IntOpenHashSet.this.data[this.lastEntry];
            }

            @Override
            public void remove() {
                if (this.lastEntry == -1) {
                    Exceptions.noElementToRemove();
                }

                IntOpenHashSet.this.states[this.lastEntry] = 2;
                IntOpenHashSet.this.size--;
                this.lastEntry = -1;
            }
        };
    }

    @Override
    public void trimToSize() {
    }

    @Override
    public Object clone() {
        try {
            IntOpenHashSet intOpenHashSet0 = (IntOpenHashSet)super.clone();
            intOpenHashSet0.data = new int[this.data.length];
            System.arraycopy(this.data, 0, intOpenHashSet0.data, 0, this.data.length);
            intOpenHashSet0.states = new byte[this.data.length];
            System.arraycopy(this.states, 0, intOpenHashSet0.states, 0, this.states.length);
            return intOpenHashSet0;
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            Exceptions.cloning();
            throw new RuntimeException();
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        this.size = 0;
        this.used = 0;
        Arrays.fill(this.states, (byte)0);
    }

    @Override
    public boolean contains(int int1) {
        int int0 = Math.abs(this.keyhash.hash(int1));
        int int2 = int0 % this.data.length;
        if (this.states[int2] == 0) {
            return false;
        } else if (this.states[int2] == 1 && this.data[int2] == int1) {
            return true;
        } else {
            int int3 = 1 + int0 % (this.data.length - 2);

            do {
                int2 -= int3;
                if (int2 < 0) {
                    int2 += this.data.length;
                }

                if (this.states[int2] == 0) {
                    return false;
                }
            } while (this.states[int2] != 1 || this.data[int2] != int1);

            return true;
        }
    }

    @Override
    public int hashCode() {
        int int0 = 0;

        for (int int1 = 0; int1 < this.data.length; int1++) {
            if (this.states[int1] == 1) {
                int0 += this.data[int1];
            }
        }

        return int0;
    }

    @Override
    public boolean remove(int int1) {
        int int0 = Math.abs(this.keyhash.hash(int1));
        int int2 = int0 % this.data.length;
        if (this.states[int2] == 0) {
            return false;
        } else if (this.states[int2] == 1 && this.data[int2] == int1) {
            this.states[int2] = 2;
            this.size--;
            return true;
        } else {
            int int3 = 1 + int0 % (this.data.length - 2);

            do {
                int2 -= int3;
                if (int2 < 0) {
                    int2 += this.data.length;
                }

                if (this.states[int2] == 0) {
                    return false;
                }
            } while (this.states[int2] != 1 || this.data[int2] != int1);

            this.states[int2] = 2;
            this.size--;
            return true;
        }
    }

    @Override
    public int[] toArray(int[] ints) {
        if (ints == null || ints.length < this.size) {
            ints = new int[this.size];
        }

        int int0 = 0;

        for (int int1 = 0; int1 < this.data.length; int1++) {
            if (this.states[int1] == 1) {
                ints[int0++] = this.data[int1];
            }
        }

        return ints;
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeInt(this.data.length);
        IntIterator intIterator = this.iterator();

        while (intIterator.hasNext()) {
            int int0 = intIterator.next();
            objectOutputStream.writeInt(int0);
        }
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.data = new int[objectInputStream.readInt()];
        this.states = new byte[this.data.length];
        this.used = this.size;

        for (int int0 = 0; int0 < this.size; int0++) {
            int int1 = objectInputStream.readInt();
            int int2 = Math.abs(this.keyhash.hash(int1));
            int int3 = int2 % this.data.length;
            if (this.states[int3] == 1) {
                int int4 = 1 + int2 % (this.data.length - 2);

                do {
                    int3 -= int4;
                    if (int3 < 0) {
                        int3 += this.data.length;
                    }
                } while (this.states[int3] != 0);
            }

            this.states[int3] = 1;
            this.data[int3] = int1;
        }
    }
}
