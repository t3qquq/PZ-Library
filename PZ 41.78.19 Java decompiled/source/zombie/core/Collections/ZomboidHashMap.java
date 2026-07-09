// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.Collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

public class ZomboidHashMap<K, V> extends ZomboidAbstractMap<K, V> implements Map<K, V>, Cloneable, Serializable {
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final int MAXIMUM_CAPACITY = 1073741824;
    static final float DEFAULT_LOAD_FACTOR = 0.75F;
    transient ZomboidHashMap.Entry[] table;
    transient int size;
    int threshold;
    final float loadFactor;
    transient volatile int modCount;
    Stack<ZomboidHashMap.Entry<K, V>> entryStore = new Stack<>();
    private transient Set<java.util.Map.Entry<K, V>> entrySet = null;
    private static final long serialVersionUID = 362498820763181265L;

    public ZomboidHashMap(int int0, float float0) {
        if (int0 < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + int0);
        } else {
            if (int0 > 1073741824) {
                int0 = 1073741824;
            }

            if (!(float0 <= 0.0F) && !Float.isNaN(float0)) {
                byte byte0 = 1;

                while (byte0 < int0) {
                    byte0 <<= 1;
                }

                for (int int1 = 0; int1 < 100; int1++) {
                    this.entryStore.add(new ZomboidHashMap.Entry<>(0, null, null, null));
                }

                this.loadFactor = float0;
                this.threshold = (int)(byte0 * float0);
                this.table = new ZomboidHashMap.Entry[byte0];
                this.init();
            } else {
                throw new IllegalArgumentException("Illegal load factor: " + float0);
            }
        }
    }

    public ZomboidHashMap(int int0) {
        this(int0, 0.75F);
    }

    public ZomboidHashMap() {
        this.loadFactor = 0.75F;
        this.threshold = 12;
        this.table = new ZomboidHashMap.Entry[16];
        this.init();
    }

    public ZomboidHashMap(Map<? extends K, ? extends V> map) {
        this(Math.max((int)(map.size() / 0.75F) + 1, 16), 0.75F);
        this.putAllForCreate(map);
    }

    void init() {
    }

    static int hash(int int0) {
        int0 ^= int0 >>> 20 ^ int0 >>> 12;
        return int0 ^ int0 >>> 7 ^ int0 >>> 4;
    }

    static int indexFor(int int0, int int1) {
        return int0 & int1 - 1;
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
    public V get(Object object0) {
        if (object0 == null) {
            return this.getForNullKey();
        } else {
            int int0 = hash(object0.hashCode());

            for (ZomboidHashMap.Entry entry = this.table[indexFor(int0, this.table.length)]; entry != null; entry = entry.next) {
                if (entry.hash == int0) {
                    Object object1 = entry.key;
                    if (entry.key == object0 || object0.equals(object1)) {
                        return entry.value;
                    }
                }
            }

            return null;
        }
    }

    private V getForNullKey() {
        for (ZomboidHashMap.Entry entry = this.table[0]; entry != null; entry = entry.next) {
            if (entry.key == null) {
                return entry.value;
            }
        }

        return null;
    }

    @Override
    public boolean containsKey(Object object) {
        return this.getEntry(object) != null;
    }

    final ZomboidHashMap.Entry<K, V> getEntry(Object object0) {
        int int0 = object0 == null ? 0 : hash(object0.hashCode());

        for (ZomboidHashMap.Entry entry = this.table[indexFor(int0, this.table.length)]; entry != null; entry = entry.next) {
            if (entry.hash == int0) {
                Object object1 = entry.key;
                if (entry.key == object0 || object0 != null && object0.equals(object1)) {
                    return entry;
                }
            }
        }

        return null;
    }

    @Override
    public V put(K object0, V object1) {
        if (object0 == null) {
            return this.putForNullKey((V)object1);
        } else {
            int int0 = hash(object0.hashCode());
            int int1 = indexFor(int0, this.table.length);

            for (ZomboidHashMap.Entry entry = this.table[int1]; entry != null; entry = entry.next) {
                if (entry.hash == int0) {
                    Object object2 = entry.key;
                    if (entry.key == object0 || object0.equals(object2)) {
                        Object object3 = entry.value;
                        entry.value = (V)object1;
                        entry.recordAccess(this);
                        return (V)object3;
                    }
                }
            }

            this.modCount++;
            this.addEntry(int0, (K)object0, (V)object1, int1);
            return null;
        }
    }

    private V putForNullKey(V object1) {
        for (ZomboidHashMap.Entry entry = this.table[0]; entry != null; entry = entry.next) {
            if (entry.key == null) {
                Object object0 = entry.value;
                entry.value = (V)object1;
                entry.recordAccess(this);
                return (V)object0;
            }
        }

        this.modCount++;
        this.addEntry(0, null, (V)object1, 0);
        return null;
    }

    private void putForCreate(K object0, V object2) {
        int int0 = object0 == null ? 0 : hash(object0.hashCode());
        int int1 = indexFor(int0, this.table.length);

        for (ZomboidHashMap.Entry entry = this.table[int1]; entry != null; entry = entry.next) {
            if (entry.hash == int0) {
                Object object1 = entry.key;
                if (entry.key == object0 || object0 != null && object0.equals(object1)) {
                    entry.value = (V)object2;
                    return;
                }
            }
        }

        this.createEntry(int0, (K)object0, (V)object2, int1);
    }

    private void putAllForCreate(Map<? extends K, ? extends V> map) {
        for (java.util.Map.Entry entry : map.entrySet()) {
            this.putForCreate((K)entry.getKey(), (V)entry.getValue());
        }
    }

    void resize(int int1) {
        ZomboidHashMap.Entry[] entrys0 = this.table;
        int int0 = entrys0.length;
        if (int0 == 1073741824) {
            this.threshold = Integer.MAX_VALUE;
        } else {
            ZomboidHashMap.Entry[] entrys1 = new ZomboidHashMap.Entry[int1];
            this.transfer(entrys1);
            this.table = entrys1;
            this.threshold = (int)(int1 * this.loadFactor);
        }
    }

    void transfer(ZomboidHashMap.Entry[] entrys1) {
        ZomboidHashMap.Entry[] entrys0 = this.table;
        int int0 = entrys1.length;

        for (int int1 = 0; int1 < entrys0.length; int1++) {
            ZomboidHashMap.Entry entry0 = entrys0[int1];
            if (entry0 != null) {
                entrys0[int1] = null;

                while (true) {
                    ZomboidHashMap.Entry entry1 = entry0.next;
                    int int2 = indexFor(entry0.hash, int0);
                    entry0.next = entrys1[int2];
                    entrys1[int2] = entry0;
                    entry0 = entry1;
                    if (entry1 == null) {
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        int int0 = map.size();
        if (int0 != 0) {
            if (int0 > this.threshold) {
                int int1 = (int)(int0 / this.loadFactor + 1.0F);
                if (int1 > 1073741824) {
                    int1 = 1073741824;
                }

                int int2 = this.table.length;

                while (int2 < int1) {
                    int2 <<= 1;
                }

                if (int2 > this.table.length) {
                    this.resize(int2);
                }
            }

            for (java.util.Map.Entry entry : map.entrySet()) {
                this.put((K)entry.getKey(), (V)entry.getValue());
            }
        }
    }

    @Override
    public V remove(Object object) {
        ZomboidHashMap.Entry entry = this.removeEntryForKey(object);
        return entry == null ? null : entry.value;
    }

    final ZomboidHashMap.Entry<K, V> removeEntryForKey(Object object0) {
        int int0 = object0 == null ? 0 : hash(object0.hashCode());
        int int1 = indexFor(int0, this.table.length);
        ZomboidHashMap.Entry entry0 = this.table[int1];
        ZomboidHashMap.Entry entry1 = entry0;

        while (entry1 != null) {
            ZomboidHashMap.Entry entry2 = entry1.next;
            if (entry1.hash == int0) {
                Object object1 = entry1.key;
                if (entry1.key == object0 || object0 != null && object0.equals(object1)) {
                    this.modCount++;
                    this.size--;
                    if (entry0 == entry1) {
                        this.table[int1] = entry2;
                    } else {
                        entry0.next = entry2;
                    }

                    entry1.recordRemoval(this);
                    entry1.value = null;
                    entry1.next = null;
                    this.entryStore.push(entry1);
                    return entry1;
                }
            }

            entry0 = entry1;
            entry1 = entry2;
        }

        return entry1;
    }

    final ZomboidHashMap.Entry<K, V> removeMapping(Object object0) {
        if (!(object0 instanceof java.util.Map.Entry entry0)) {
            return null;
        } else {
            Object object1 = entry0.getKey();
            int int0 = object1 == null ? 0 : hash(object1.hashCode());
            int int1 = indexFor(int0, this.table.length);
            ZomboidHashMap.Entry entry1 = this.table[int1];
            ZomboidHashMap.Entry entry2 = entry1;

            while (entry2 != null) {
                ZomboidHashMap.Entry entry3 = entry2.next;
                if (entry2.hash == int0 && entry2.equals(entry0)) {
                    this.modCount++;
                    this.size--;
                    if (entry1 == entry2) {
                        this.table[int1] = entry3;
                    } else {
                        entry1.next = entry3;
                    }

                    entry2.recordRemoval(this);
                    entry2.value = null;
                    entry2.next = null;
                    this.entryStore.push(entry2);
                    return entry2;
                }

                entry1 = entry2;
                entry2 = entry3;
            }

            return entry2;
        }
    }

    @Override
    public void clear() {
        this.modCount++;
        ZomboidHashMap.Entry[] entrys = this.table;

        for (int int0 = 0; int0 < entrys.length; int0++) {
            if (entrys[int0] != null) {
                entrys[int0].value = null;
                entrys[int0].next = null;
                this.entryStore.push(entrys[int0]);
            }

            entrys[int0] = null;
        }

        this.size = 0;
    }

    @Override
    public boolean containsValue(Object object) {
        if (object == null) {
            return this.containsNullValue();
        } else {
            ZomboidHashMap.Entry[] entrys = this.table;

            for (int int0 = 0; int0 < entrys.length; int0++) {
                for (ZomboidHashMap.Entry entry = entrys[int0]; entry != null; entry = entry.next) {
                    if (object.equals(entry.value)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    private boolean containsNullValue() {
        ZomboidHashMap.Entry[] entrys = this.table;

        for (int int0 = 0; int0 < entrys.length; int0++) {
            for (ZomboidHashMap.Entry entry = entrys[int0]; entry != null; entry = entry.next) {
                if (entry.value == null) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Object clone() {
        ZomboidHashMap zomboidHashMap0 = null;

        try {
            zomboidHashMap0 = (ZomboidHashMap)super.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
        }

        zomboidHashMap0.table = new ZomboidHashMap.Entry[this.table.length];
        zomboidHashMap0.entrySet = null;
        zomboidHashMap0.modCount = 0;
        zomboidHashMap0.size = 0;
        zomboidHashMap0.init();
        zomboidHashMap0.putAllForCreate(this);
        return zomboidHashMap0;
    }

    void addEntry(int int2, K object0, V object1, int int0) {
        ZomboidHashMap.Entry entry0 = this.table[int0];
        if (this.entryStore.isEmpty()) {
            for (int int1 = 0; int1 < 100; int1++) {
                this.entryStore.add(new ZomboidHashMap.Entry<>(0, null, null, null));
            }
        }

        ZomboidHashMap.Entry entry1 = this.entryStore.pop();
        entry1.hash = int2;
        entry1.key = (K)object0;
        entry1.value = (V)object1;
        entry1.next = entry0;
        this.table[int0] = entry1;
        if (this.size++ >= this.threshold) {
            this.resize(2 * this.table.length);
        }
    }

    void createEntry(int int2, K object0, V object1, int int0) {
        ZomboidHashMap.Entry entry0 = this.table[int0];
        if (this.entryStore.isEmpty()) {
            for (int int1 = 0; int1 < 100; int1++) {
                this.entryStore.add(new ZomboidHashMap.Entry<>(0, null, null, null));
            }
        }

        ZomboidHashMap.Entry entry1 = this.entryStore.pop();
        entry1.hash = int2;
        entry1.key = (K)object0;
        entry1.value = (V)object1;
        entry1.next = entry0;
        this.table[int0] = entry1;
        this.size++;
    }

    Iterator<K> newKeyIterator() {
        return new ZomboidHashMap.KeyIterator();
    }

    Iterator<V> newValueIterator() {
        return new ZomboidHashMap.ValueIterator();
    }

    Iterator<java.util.Map.Entry<K, V>> newEntryIterator() {
        return new ZomboidHashMap.EntryIterator();
    }

    @Override
    public Set<K> keySet() {
        Set set = this.keySet;
        return set != null ? set : (this.keySet = new ZomboidHashMap.KeySet());
    }

    @Override
    public Collection<V> values() {
        Collection collection = this.values;
        return collection != null ? collection : (this.values = new ZomboidHashMap.Values());
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return this.entrySet0();
    }

    private Set<java.util.Map.Entry<K, V>> entrySet0() {
        Set set = this.entrySet;
        return set != null ? set : (this.entrySet = new ZomboidHashMap.EntrySet());
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        Iterator iterator = this.size > 0 ? this.entrySet0().iterator() : null;
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeInt(this.table.length);
        objectOutputStream.writeInt(this.size);
        if (iterator != null) {
            while (iterator.hasNext()) {
                java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
                objectOutputStream.writeObject(entry.getKey());
                objectOutputStream.writeObject(entry.getValue());
            }
        }
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        int int0 = objectInputStream.readInt();
        this.table = new ZomboidHashMap.Entry[int0];
        this.init();
        int int1 = objectInputStream.readInt();

        for (int int2 = 0; int2 < int1; int2++) {
            Object object0 = objectInputStream.readObject();
            Object object1 = objectInputStream.readObject();
            this.putForCreate((K)object0, (V)object1);
        }
    }

    int capacity() {
        return this.table.length;
    }

    float loadFactor() {
        return this.loadFactor;
    }

    static class Entry<K, V> implements java.util.Map.Entry<K, V> {
        K key;
        V value;
        ZomboidHashMap.Entry<K, V> next;
        int hash;

        Entry(int int0, K object1, V object0, ZomboidHashMap.Entry<K, V> entry1) {
            this.value = (V)object0;
            this.next = entry1;
            this.key = (K)object1;
            this.hash = int0;
        }

        @Override
        public final K getKey() {
            return this.key;
        }

        @Override
        public final V getValue() {
            return this.value;
        }

        @Override
        public final V setValue(V object1) {
            Object object0 = this.value;
            this.value = (V)object1;
            return (V)object0;
        }

        @Override
        public final boolean equals(Object object0) {
            if (!(object0 instanceof java.util.Map.Entry entry0)) {
                return false;
            } else {
                Object object1 = this.getKey();
                Object object2 = entry0.getKey();
                if (object1 == object2 || object1 != null && object1.equals(object2)) {
                    Object object3 = this.getValue();
                    Object object4 = entry0.getValue();
                    if (object3 == object4 || object3 != null && object3.equals(object4)) {
                        return true;
                    }
                }

                return false;
            }
        }

        @Override
        public final int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
        }

        @Override
        public final String toString() {
            return this.getKey() + "=" + this.getValue();
        }

        void recordAccess(ZomboidHashMap<K, V> var1) {
        }

        void recordRemoval(ZomboidHashMap<K, V> var1) {
        }
    }

    private final class EntryIterator extends ZomboidHashMap<K, V>.HashIterator<java.util.Map.Entry<K, V>> {
        public java.util.Map.Entry<K, V> next() {
            return this.nextEntry();
        }
    }

    private final class EntrySet extends AbstractSet<java.util.Map.Entry<K, V>> {
        @Override
        public Iterator<java.util.Map.Entry<K, V>> iterator() {
            return ZomboidHashMap.this.newEntryIterator();
        }

        @Override
        public boolean contains(Object object) {
            if (!(object instanceof java.util.Map.Entry entry0)) {
                return false;
            } else {
                ZomboidHashMap.Entry entry1 = ZomboidHashMap.this.getEntry(entry0.getKey());
                return entry1 != null && entry1.equals(entry0);
            }
        }

        @Override
        public boolean remove(Object object) {
            return ZomboidHashMap.this.removeMapping(object) != null;
        }

        @Override
        public int size() {
            return ZomboidHashMap.this.size;
        }

        @Override
        public void clear() {
            ZomboidHashMap.this.clear();
        }
    }

    private abstract class HashIterator<E> implements Iterator<E> {
        ZomboidHashMap.Entry<K, V> next;
        int expectedModCount = ZomboidHashMap.this.modCount;
        int index;
        ZomboidHashMap.Entry<K, V> current;

        HashIterator() {
            if (ZomboidHashMap.this.size > 0) {
                ZomboidHashMap.Entry[] entrys = ZomboidHashMap.this.table;

                while (this.index < entrys.length && (this.next = entrys[this.index++]) == null) {
                }
            }
        }

        @Override
        public final boolean hasNext() {
            return this.next != null;
        }

        final ZomboidHashMap.Entry<K, V> nextEntry() {
            if (ZomboidHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            } else {
                ZomboidHashMap.Entry entry = this.next;
                if (entry == null) {
                    throw new NoSuchElementException();
                } else {
                    if ((this.next = entry.next) == null) {
                        ZomboidHashMap.Entry[] entrys = ZomboidHashMap.this.table;

                        while (this.index < entrys.length && (this.next = entrys[this.index++]) == null) {
                        }
                    }

                    this.current = entry;
                    return entry;
                }
            }
        }

        @Override
        public void remove() {
            if (this.current == null) {
                throw new IllegalStateException();
            } else if (ZomboidHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            } else {
                Object object = this.current.key;
                this.current = null;
                ZomboidHashMap.this.removeEntryForKey(object);
                this.expectedModCount = ZomboidHashMap.this.modCount;
            }
        }
    }

    private final class KeyIterator extends ZomboidHashMap<K, V>.HashIterator<K> {
        @Override
        public K next() {
            return (K)this.nextEntry().getKey();
        }
    }

    private final class KeySet extends AbstractSet<K> {
        @Override
        public Iterator<K> iterator() {
            return ZomboidHashMap.this.newKeyIterator();
        }

        @Override
        public int size() {
            return ZomboidHashMap.this.size;
        }

        @Override
        public boolean contains(Object object) {
            return ZomboidHashMap.this.containsKey(object);
        }

        @Override
        public boolean remove(Object object) {
            return ZomboidHashMap.this.removeEntryForKey(object) != null;
        }

        @Override
        public void clear() {
            ZomboidHashMap.this.clear();
        }
    }

    private final class ValueIterator extends ZomboidHashMap<K, V>.HashIterator<V> {
        @Override
        public V next() {
            return this.nextEntry().value;
        }
    }

    private final class Values extends AbstractCollection<V> {
        @Override
        public Iterator<V> iterator() {
            return ZomboidHashMap.this.newValueIterator();
        }

        @Override
        public int size() {
            return ZomboidHashMap.this.size;
        }

        @Override
        public boolean contains(Object object) {
            return ZomboidHashMap.this.containsValue(object);
        }

        @Override
        public void clear() {
            ZomboidHashMap.this.clear();
        }
    }
}
