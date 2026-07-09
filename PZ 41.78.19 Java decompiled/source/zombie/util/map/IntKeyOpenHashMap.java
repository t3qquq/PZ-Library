// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import zombie.util.IntIterator;
import zombie.util.hash.DefaultIntHashFunction;
import zombie.util.hash.IntHashFunction;
import zombie.util.hash.Primes;
import zombie.util.set.AbstractIntSet;
import zombie.util.set.IntSet;
import zombie.util.util.Exceptions;

public class IntKeyOpenHashMap<V> extends AbstractIntKeyMap<V> implements IntKeyMap<V>, Cloneable, Serializable {
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
    private transient int[] keys;
    private transient Object[] values;
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
    private transient IntSet ckeys;
    private transient Collection<V> cvalues;

    private IntKeyOpenHashMap(IntHashFunction intHashFunction, int int0, int int2, double double0, int int1, double double1) {
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
        this.keys = new int[int0];
        this.values = new Object[int0];
        this.states = new byte[int0];
        this.size = 0;
        this.expandAt = (int)Math.round(double1 * int0);
        this.used = 0;
        this.growthPolicy = int2;
        this.growthFactor = double0;
        this.growthChunk = int1;
        this.loadFactor = double1;
    }

    private IntKeyOpenHashMap(int int0, int int1, double double0, int int2, double double1) {
        this(DefaultIntHashFunction.INSTANCE, int0, int1, double0, int2, double1);
    }

    public IntKeyOpenHashMap() {
        this(11);
    }

    public IntKeyOpenHashMap(IntKeyMap<V> intKeyMap) {
        this();
        this.putAll(intKeyMap);
    }

    public IntKeyOpenHashMap(int int0) {
        this(int0, 0, 1.0, 10, 0.75);
    }

    public IntKeyOpenHashMap(double double0) {
        this(11, 0, 1.0, 10, double0);
    }

    public IntKeyOpenHashMap(int int0, double double0) {
        this(int0, 0, 1.0, 10, double0);
    }

    public IntKeyOpenHashMap(int int0, double double1, double double0) {
        this(int0, 0, double0, 10, double1);
    }

    public IntKeyOpenHashMap(int int0, double double0, int int1) {
        this(int0, 1, 1.0, int1, double0);
    }

    public IntKeyOpenHashMap(IntHashFunction intHashFunction) {
        this(intHashFunction, 11, 0, 1.0, 10, 0.75);
    }

    public IntKeyOpenHashMap(IntHashFunction intHashFunction, int int0) {
        this(intHashFunction, int0, 0, 1.0, 10, 0.75);
    }

    public IntKeyOpenHashMap(IntHashFunction intHashFunction, double double0) {
        this(intHashFunction, 11, 0, 1.0, 10, double0);
    }

    public IntKeyOpenHashMap(IntHashFunction intHashFunction, int int0, double double0) {
        this(intHashFunction, int0, 0, 1.0, 10, double0);
    }

    public IntKeyOpenHashMap(IntHashFunction intHashFunction, int int0, double double1, double double0) {
        this(intHashFunction, int0, 0, double0, 10, double1);
    }

    public IntKeyOpenHashMap(IntHashFunction intHashFunction, int int0, double double0, int int1) {
        this(intHashFunction, int0, 1, 1.0, int1, double0);
    }

    private void ensureCapacity(int int0) {
        if (int0 >= this.expandAt) {
            int int1;
            if (this.growthPolicy == 0) {
                int1 = (int)(this.keys.length * (1.0 + this.growthFactor));
            } else {
                int1 = this.keys.length + this.growthChunk;
            }

            if (int1 * this.loadFactor < int0) {
                int1 = (int)Math.round(int0 / this.loadFactor);
            }

            int1 = Primes.nextPrime(int1);
            this.expandAt = (int)Math.round(this.loadFactor * int1);
            int[] ints = new int[int1];
            Object[] objects = new Object[int1];
            byte[] bytes = new byte[int1];
            this.used = 0;

            for (int int2 = 0; int2 < this.keys.length; int2++) {
                if (this.states[int2] == 1) {
                    this.used++;
                    int int3 = this.keys[int2];
                    Object object = this.values[int2];
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
                    objects[int5] = object;
                    ints[int5] = int3;
                }
            }

            this.keys = ints;
            this.values = objects;
            this.states = bytes;
        }
    }

    @Override
    public IntSet keySet() {
        if (this.ckeys == null) {
            this.ckeys = new IntKeyOpenHashMap.KeySet();
        }

        return this.ckeys;
    }

    @Override
    public V put(int int1, V object1) {
        int int0 = Math.abs(this.keyhash.hash(int1));
        int int2 = int0 % this.keys.length;
        if (this.states[int2] == 1) {
            if (this.keys[int2] == int1) {
                Object object0 = this.values[int2];
                this.values[int2] = object1;
                return (V)object0;
            }

            int int3 = 1 + int0 % (this.keys.length - 2);

            while (true) {
                int2 -= int3;
                if (int2 < 0) {
                    int2 += this.keys.length;
                }

                if (this.states[int2] == 0 || this.states[int2] == 2) {
                    break;
                }

                if (this.states[int2] == 1 && this.keys[int2] == int1) {
                    Object object2 = this.values[int2];
                    this.values[int2] = object1;
                    return (V)object2;
                }
            }
        }

        if (this.states[int2] == 0) {
            this.used++;
        }

        this.states[int2] = 1;
        this.keys[int2] = int1;
        this.values[int2] = object1;
        this.size++;
        this.ensureCapacity(this.used);
        return null;
    }

    @Override
    public Collection<V> values() {
        if (this.cvalues == null) {
            this.cvalues = new IntKeyOpenHashMap.ValueCollection();
        }

        return this.cvalues;
    }

    @Override
    public Object clone() {
        try {
            IntKeyOpenHashMap intKeyOpenHashMap0 = (IntKeyOpenHashMap)super.clone();
            intKeyOpenHashMap0.keys = new int[this.keys.length];
            System.arraycopy(this.keys, 0, intKeyOpenHashMap0.keys, 0, this.keys.length);
            intKeyOpenHashMap0.values = new Object[this.values.length];
            System.arraycopy(this.values, 0, intKeyOpenHashMap0.values, 0, this.values.length);
            intKeyOpenHashMap0.states = new byte[this.states.length];
            System.arraycopy(this.states, 0, intKeyOpenHashMap0.states, 0, this.states.length);
            intKeyOpenHashMap0.cvalues = null;
            intKeyOpenHashMap0.ckeys = null;
            return intKeyOpenHashMap0;
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            Exceptions.cloning();
            return null;
        }
    }

    @Override
    public IntKeyMapIterator<V> entries() {
        return new IntKeyMapIterator<V>() {
            int nextEntry = this.nextEntry(0);
            int lastEntry = -1;

            int nextEntry(int int0) {
                while (int0 < IntKeyOpenHashMap.this.keys.length && IntKeyOpenHashMap.this.states[int0] != 1) {
                    int0++;
                }

                return int0;
            }

            @Override
            public boolean hasNext() {
                return this.nextEntry < IntKeyOpenHashMap.this.keys.length;
            }

            @Override
            public void next() {
                if (!this.hasNext()) {
                    Exceptions.endOfIterator();
                }

                this.lastEntry = this.nextEntry;
                this.nextEntry = this.nextEntry(this.nextEntry + 1);
            }

            @Override
            public void remove() {
                if (this.lastEntry == -1) {
                    Exceptions.noElementToRemove();
                }

                IntKeyOpenHashMap.this.states[this.lastEntry] = 2;
                IntKeyOpenHashMap.this.values[this.lastEntry] = null;
                IntKeyOpenHashMap.this.size--;
                this.lastEntry = -1;
            }

            @Override
            public int getKey() {
                if (this.lastEntry == -1) {
                    Exceptions.noElementToGet();
                }

                return IntKeyOpenHashMap.this.keys[this.lastEntry];
            }

            @Override
            public V getValue() {
                if (this.lastEntry == -1) {
                    Exceptions.noElementToGet();
                }

                return (V)IntKeyOpenHashMap.this.values[this.lastEntry];
            }
        };
    }

    @Override
    public void clear() {
        Arrays.fill(this.states, (byte)0);
        Arrays.fill(this.values, null);
        this.size = 0;
        this.used = 0;
    }

    @Override
    public boolean containsKey(int int1) {
        int int0 = Math.abs(this.keyhash.hash(int1));
        int int2 = int0 % this.keys.length;
        if (this.states[int2] == 0) {
            return false;
        } else if (this.states[int2] == 1 && this.keys[int2] == int1) {
            return true;
        } else {
            int int3 = 1 + int0 % (this.keys.length - 2);

            do {
                int2 -= int3;
                if (int2 < 0) {
                    int2 += this.keys.length;
                }

                if (this.states[int2] == 0) {
                    return false;
                }
            } while (this.states[int2] != 1 || this.keys[int2] != int1);

            return true;
        }
    }

    @Override
    public boolean containsValue(Object object) {
        if (object == null) {
            for (int int0 = 0; int0 < this.states.length; int0++) {
                if (this.states[int0] == 1 && this.values[int0] == null) {
                    return true;
                }
            }
        } else {
            for (int int1 = 0; int1 < this.states.length; int1++) {
                if (this.states[int1] == 1 && object.equals(this.values[int1])) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public V get(int int1) {
        int int0 = Math.abs(this.keyhash.hash(int1));
        int int2 = int0 % this.keys.length;
        if (this.states[int2] == 0) {
            return null;
        } else if (this.states[int2] == 1 && this.keys[int2] == int1) {
            return (V)this.values[int2];
        } else {
            int int3 = 1 + int0 % (this.keys.length - 2);

            do {
                int2 -= int3;
                if (int2 < 0) {
                    int2 += this.keys.length;
                }

                if (this.states[int2] == 0) {
                    return null;
                }
            } while (this.states[int2] != 1 || this.keys[int2] != int1);

            return (V)this.values[int2];
        }
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public V remove(int int1) {
        int int0 = Math.abs(this.keyhash.hash(int1));
        int int2 = int0 % this.keys.length;
        if (this.states[int2] == 0) {
            return null;
        } else if (this.states[int2] == 1 && this.keys[int2] == int1) {
            Object object0 = this.values[int2];
            this.values[int2] = null;
            this.states[int2] = 2;
            this.size--;
            return (V)object0;
        } else {
            int int3 = 1 + int0 % (this.keys.length - 2);

            do {
                int2 -= int3;
                if (int2 < 0) {
                    int2 += this.keys.length;
                }

                if (this.states[int2] == 0) {
                    return null;
                }
            } while (this.states[int2] != 1 || this.keys[int2] != int1);

            Object object1 = this.values[int2];
            this.values[int2] = null;
            this.states[int2] = 2;
            this.size--;
            return (V)object1;
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeInt(this.keys.length);
        IntKeyMapIterator intKeyMapIterator = this.entries();

        while (intKeyMapIterator.hasNext()) {
            intKeyMapIterator.next();
            objectOutputStream.writeInt(intKeyMapIterator.getKey());
            objectOutputStream.writeObject(intKeyMapIterator.getValue());
        }
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.keys = new int[objectInputStream.readInt()];
        this.states = new byte[this.keys.length];
        this.values = new Object[this.keys.length];
        this.used = this.size;

        for (int int0 = 0; int0 < this.size; int0++) {
            int int1 = objectInputStream.readInt();
            Object object = objectInputStream.readObject();
            int int2 = Math.abs(this.keyhash.hash(int1));
            int int3 = int2 % this.keys.length;
            if (this.states[int3] != 0) {
                int int4 = 1 + int2 % (this.keys.length - 2);

                do {
                    int3 -= int4;
                    if (int3 < 0) {
                        int3 += this.keys.length;
                    }
                } while (this.states[int3] != 0);
            }

            this.states[int3] = 1;
            this.keys[int3] = int1;
            this.values[int3] = object;
        }
    }

    private class KeySet extends AbstractIntSet {
        @Override
        public void clear() {
            IntKeyOpenHashMap.this.clear();
        }

        @Override
        public boolean contains(int int0) {
            return IntKeyOpenHashMap.this.containsKey(int0);
        }

        @Override
        public IntIterator iterator() {
            return new IntIterator() {
                int nextEntry = this.nextEntry(0);
                int lastEntry = -1;

                int nextEntry(int int0) {
                    while (int0 < IntKeyOpenHashMap.this.keys.length && IntKeyOpenHashMap.this.states[int0] != 1) {
                        int0++;
                    }

                    return int0;
                }

                @Override
                public boolean hasNext() {
                    return this.nextEntry < IntKeyOpenHashMap.this.keys.length;
                }

                @Override
                public int next() {
                    if (!this.hasNext()) {
                        Exceptions.endOfIterator();
                    }

                    this.lastEntry = this.nextEntry;
                    this.nextEntry = this.nextEntry(this.nextEntry + 1);
                    return IntKeyOpenHashMap.this.keys[this.lastEntry];
                }

                @Override
                public void remove() {
                    if (this.lastEntry == -1) {
                        Exceptions.noElementToRemove();
                    }

                    IntKeyOpenHashMap.this.states[this.lastEntry] = 2;
                    IntKeyOpenHashMap.this.values[this.lastEntry] = null;
                    IntKeyOpenHashMap.this.size--;
                    this.lastEntry = -1;
                }
            };
        }

        @Override
        public boolean remove(int int0) {
            boolean boolean0 = IntKeyOpenHashMap.this.containsKey(int0);
            if (boolean0) {
                IntKeyOpenHashMap.this.remove(int0);
            }

            return boolean0;
        }

        @Override
        public int size() {
            return IntKeyOpenHashMap.this.size;
        }
    }

    private class ValueCollection extends AbstractCollection<V> {
        @Override
        public void clear() {
            IntKeyOpenHashMap.this.clear();
        }

        @Override
        public boolean contains(Object object) {
            return IntKeyOpenHashMap.this.containsValue(object);
        }

        @Override
        public Iterator<V> iterator() {
            return new Iterator<V>() {
                int nextEntry = this.nextEntry(0);
                int lastEntry = -1;

                int nextEntry(int int0) {
                    while (int0 < IntKeyOpenHashMap.this.keys.length && IntKeyOpenHashMap.this.states[int0] != 1) {
                        int0++;
                    }

                    return int0;
                }

                @Override
                public boolean hasNext() {
                    return this.nextEntry < IntKeyOpenHashMap.this.keys.length;
                }

                @Override
                public V next() {
                    if (!this.hasNext()) {
                        Exceptions.endOfIterator();
                    }

                    this.lastEntry = this.nextEntry;
                    this.nextEntry = this.nextEntry(this.nextEntry + 1);
                    return (V)IntKeyOpenHashMap.this.values[this.lastEntry];
                }

                @Override
                public void remove() {
                    if (this.lastEntry == -1) {
                        Exceptions.noElementToRemove();
                    }

                    IntKeyOpenHashMap.this.states[this.lastEntry] = 2;
                    IntKeyOpenHashMap.this.values[this.lastEntry] = null;
                    IntKeyOpenHashMap.this.size--;
                    this.lastEntry = -1;
                }
            };
        }

        @Override
        public int size() {
            return IntKeyOpenHashMap.this.size;
        }
    }
}
