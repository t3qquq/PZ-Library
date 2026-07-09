// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.Collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import sun.misc.Unsafe;

public class NonBlockingHashMap<TypeK, TypeV> extends AbstractMap<TypeK, TypeV> implements ConcurrentMap<TypeK, TypeV>, Cloneable, Serializable {
    private static final long serialVersionUID = 1234123412341234123L;
    private static final int REPROBE_LIMIT = 10;
    private static final Unsafe _unsafe = UtilUnsafe.getUnsafe();
    private static final int _Obase = _unsafe.arrayBaseOffset(Object[].class);
    private static final int _Oscale = _unsafe.arrayIndexScale(Object[].class);
    private static final long _kvs_offset;
    private transient Object[] _kvs;
    private transient long _last_resize_milli;
    private static final int MIN_SIZE_LOG = 3;
    private static final int MIN_SIZE = 8;
    private static final Object NO_MATCH_OLD;
    private static final Object MATCH_ANY;
    private static final Object TOMBSTONE;
    private static final NonBlockingHashMap.Prime TOMBPRIME;
    private transient Counter _reprobes = new Counter();

    private static long rawIndex(Object[] objects, int int0) {
        assert int0 >= 0 && int0 < objects.length;

        return _Obase + int0 * _Oscale;
    }

    private final boolean CAS_kvs(Object[] objects0, Object[] objects1) {
        return _unsafe.compareAndSwapObject(this, _kvs_offset, objects0, objects1);
    }

    private static final int hash(Object object) {
        int int0 = object.hashCode();
        int0 += int0 << 15 ^ -12931;
        int0 ^= int0 >>> 10;
        int0 += int0 << 3;
        int0 ^= int0 >>> 6;
        int0 += (int0 << 2) + (int0 << 14);
        return int0 ^ int0 >>> 16;
    }

    private static final NonBlockingHashMap.CHM chm(Object[] objects) {
        return (NonBlockingHashMap.CHM)objects[0];
    }

    private static final int[] hashes(Object[] objects) {
        return (int[])objects[1];
    }

    private static final int len(Object[] objects) {
        return objects.length - 2 >> 1;
    }

    private static final Object key(Object[] objects, int int0) {
        return objects[(int0 << 1) + 2];
    }

    private static final Object val(Object[] objects, int int0) {
        return objects[(int0 << 1) + 3];
    }

    private static final boolean CAS_key(Object[] objects, int int0, Object object0, Object object1) {
        return _unsafe.compareAndSwapObject(objects, rawIndex(objects, (int0 << 1) + 2), object0, object1);
    }

    private static final boolean CAS_val(Object[] objects, int int0, Object object0, Object object1) {
        return _unsafe.compareAndSwapObject(objects, rawIndex(objects, (int0 << 1) + 3), object0, object1);
    }

    public final void print() {
        System.out.println("=========");
        this.print2(this._kvs);
        System.out.println("=========");
    }

    private final void print(Object[] objects0) {
        for (int int0 = 0; int0 < len(objects0); int0++) {
            Object object0 = key(objects0, int0);
            if (object0 != null) {
                String string0 = object0 == TOMBSTONE ? "XXX" : object0.toString();
                Object object1 = val(objects0, int0);
                Object object2 = NonBlockingHashMap.Prime.unbox(object1);
                String string1 = object1 == object2 ? "" : "prime_";
                String string2 = object2 == TOMBSTONE ? "tombstone" : object2.toString();
                System.out.println(int0 + " (" + string0 + "," + string1 + string2 + ")");
            }
        }

        Object[] objects1 = chm(objects0)._newkvs;
        if (objects1 != null) {
            System.out.println("----");
            this.print(objects1);
        }
    }

    private final void print2(Object[] objects0) {
        for (int int0 = 0; int0 < len(objects0); int0++) {
            Object object0 = key(objects0, int0);
            Object object1 = val(objects0, int0);
            Object object2 = NonBlockingHashMap.Prime.unbox(object1);
            if (object0 != null && object0 != TOMBSTONE && object1 != null && object2 != TOMBSTONE) {
                String string = object1 == object2 ? "" : "prime_";
                System.out.println(int0 + " (" + object0 + "," + string + object1 + ")");
            }
        }

        Object[] objects1 = chm(objects0)._newkvs;
        if (objects1 != null) {
            System.out.println("----");
            this.print2(objects1);
        }
    }

    public long reprobes() {
        long long0 = this._reprobes.get();
        this._reprobes = new Counter();
        return long0;
    }

    private static final int reprobe_limit(int int0) {
        return 10 + (int0 >> 2);
    }

    public NonBlockingHashMap() {
        this(8);
    }

    public NonBlockingHashMap(int int0) {
        this.initialize(int0);
    }

    private final void initialize(int int0) {
        if (int0 < 0) {
            throw new IllegalArgumentException();
        } else {
            if (int0 > 1048576) {
                int0 = 1048576;
            }

            int int1 = 3;

            while (1 << int1 < int0 << 2) {
                int1++;
            }

            this._kvs = new Object[(1 << int1 << 1) + 2];
            this._kvs[0] = new NonBlockingHashMap.CHM(new Counter());
            this._kvs[1] = new int[1 << int1];
            this._last_resize_milli = System.currentTimeMillis();
        }
    }

    protected final void initialize() {
        this.initialize(8);
    }

    @Override
    public int size() {
        return chm(this._kvs).size();
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public boolean containsKey(Object object) {
        return this.get(object) != null;
    }

    public boolean contains(Object object) {
        return this.containsValue(object);
    }

    @Override
    public TypeV put(TypeK object0, TypeV object1) {
        return this.putIfMatch(object0, object1, NO_MATCH_OLD);
    }

    @Override
    public TypeV putIfAbsent(TypeK object0, TypeV object1) {
        return this.putIfMatch(object0, object1, TOMBSTONE);
    }

    @Override
    public TypeV remove(Object object) {
        return this.putIfMatch(object, TOMBSTONE, NO_MATCH_OLD);
    }

    @Override
    public boolean remove(Object object1, Object object2) {
        Object object0 = this.putIfMatch(object1, TOMBSTONE, object2);
        return object2 == null ? object0 == object2 : object2.equals(object0);
    }

    @Override
    public TypeV replace(TypeK object0, TypeV object1) {
        return this.putIfMatch(object0, object1, MATCH_ANY);
    }

    @Override
    public boolean replace(TypeK object1, TypeV object3, TypeV object2) {
        Object object0 = this.putIfMatch(object1, object2, object3);
        return object3 == null ? object0 == object3 : object3.equals(object0);
    }

    private final TypeV putIfMatch(Object object3, Object object0, Object object1) {
        if (object1 != null && object0 != null) {
            Object object2 = putIfMatch(this, this._kvs, object3, object0, object1);

            assert !(object2 instanceof NonBlockingHashMap.Prime);

            assert object2 != null;

            return (TypeV)(object2 == TOMBSTONE ? null : object2);
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    public void putAll(Map<? extends TypeK, ? extends TypeV> map) {
        for (Entry entry : map.entrySet()) {
            this.put((TypeK)entry.getKey(), (TypeV)entry.getValue());
        }
    }

    @Override
    public void clear() {
        Object[] objects = (new NonBlockingHashMap(8))._kvs;

        while (!this.CAS_kvs(this._kvs, objects)) {
        }
    }

    @Override
    public boolean containsValue(Object object0) {
        if (object0 == null) {
            throw new NullPointerException();
        } else {
            for (Object object1 : this.values()) {
                if (object1 == object0 || object1.equals(object0)) {
                    return true;
                }
            }

            return false;
        }
    }

    protected void rehash() {
    }

    @Override
    public Object clone() {
        try {
            NonBlockingHashMap nonBlockingHashMap0 = (NonBlockingHashMap)super.clone();
            nonBlockingHashMap0.clear();

            for (Object object0 : this.keySet()) {
                Object object1 = this.get(object0);
                nonBlockingHashMap0.put(object0, object1);
            }

            return nonBlockingHashMap0;
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new InternalError();
        }
    }

    @Override
    public String toString() {
        Iterator iterator = this.entrySet().iterator();
        if (!iterator.hasNext()) {
            return "{}";
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('{');

            while (true) {
                Entry entry = (Entry)iterator.next();
                Object object0 = entry.getKey();
                Object object1 = entry.getValue();
                stringBuilder.append(object0 == this ? "(this Map)" : object0);
                stringBuilder.append('=');
                stringBuilder.append(object1 == this ? "(this Map)" : object1);
                if (!iterator.hasNext()) {
                    return stringBuilder.append('}').toString();
                }

                stringBuilder.append(", ");
            }
        }
    }

    private static boolean keyeq(Object object1, Object object0, int[] ints, int int1, int int0) {
        return object1 == object0 || (ints[int1] == 0 || ints[int1] == int0) && object1 != TOMBSTONE && object0.equals(object1);
    }

    @Override
    public TypeV get(Object object0) {
        int int0 = hash(object0);
        Object object1 = get_impl(this, this._kvs, object0, int0);

        assert !(object1 instanceof NonBlockingHashMap.Prime);

        return (TypeV)object1;
    }

    private static final Object get_impl(NonBlockingHashMap nonBlockingHashMap, Object[] objects0, Object object2, int int2) {
        int int0 = len(objects0);
        NonBlockingHashMap.CHM chm = chm(objects0);
        int[] ints = hashes(objects0);
        int int1 = int2 & int0 - 1;
        int int3 = 0;

        while (true) {
            Object object0 = key(objects0, int1);
            Object object1 = val(objects0, int1);
            if (object0 == null) {
                return null;
            }

            Object[] objects1 = chm._newkvs;
            if (keyeq(object0, object2, ints, int1, int2)) {
                if (!(object1 instanceof NonBlockingHashMap.Prime)) {
                    return object1 == TOMBSTONE ? null : object1;
                }

                return get_impl(nonBlockingHashMap, chm.copy_slot_and_check(nonBlockingHashMap, objects0, int1, object2), object2, int2);
            }

            if (++int3 >= reprobe_limit(int0) || object2 == TOMBSTONE) {
                return objects1 == null ? null : get_impl(nonBlockingHashMap, nonBlockingHashMap.help_copy(objects1), object2, int2);
            }

            int1 = int1 + 1 & int0 - 1;
        }
    }

    private static final Object putIfMatch(NonBlockingHashMap nonBlockingHashMap, Object[] objects0, Object object2, Object object0, Object object1) {
        assert object0 != null;

        assert !(object0 instanceof NonBlockingHashMap.Prime);

        assert !(object1 instanceof NonBlockingHashMap.Prime);

        int int0 = hash(object2);
        int int1 = len(objects0);
        NonBlockingHashMap.CHM chm = chm(objects0);
        int[] ints = hashes(objects0);
        int int2 = int0 & int1 - 1;
        int int3 = 0;
        Object object3 = null;
        Object object4 = null;
        Object[] objects1 = null;

        while (true) {
            object4 = val(objects0, int2);
            object3 = key(objects0, int2);
            if (object3 == null) {
                if (object0 == TOMBSTONE) {
                    return object0;
                }

                if (CAS_key(objects0, int2, null, object2)) {
                    chm._slots.add(1L);
                    ints[int2] = int0;
                    break;
                }

                object3 = key(objects0, int2);

                assert object3 != null;
            }

            objects1 = chm._newkvs;
            if (!keyeq(object3, object2, ints, int2, int0)) {
                if (++int3 < reprobe_limit(int1) && object2 != TOMBSTONE) {
                    int2 = int2 + 1 & int1 - 1;
                    continue;
                }

                objects1 = chm.resize(nonBlockingHashMap, objects0);
                if (object1 != null) {
                    nonBlockingHashMap.help_copy(objects1);
                }

                return putIfMatch(nonBlockingHashMap, objects1, object2, object0, object1);
            }
            break;
        }

        if (object0 == object4) {
            return object4;
        } else {
            if (objects1 == null && (object4 == null && chm.tableFull(int3, int1) || object4 instanceof NonBlockingHashMap.Prime)) {
                objects1 = chm.resize(nonBlockingHashMap, objects0);
            }

            if (objects1 != null) {
                return putIfMatch(nonBlockingHashMap, chm.copy_slot_and_check(nonBlockingHashMap, objects0, int2, object1), object2, object0, object1);
            } else {
                while ($assertionsDisabled || !(object4 instanceof NonBlockingHashMap.Prime)) {
                    if (object1 != NO_MATCH_OLD
                        && object4 != object1
                        && (object1 != MATCH_ANY || object4 == TOMBSTONE || object4 == null)
                        && (object4 != null || object1 != TOMBSTONE)
                        && (object1 == null || !object1.equals(object4))) {
                        return object4;
                    }

                    if (CAS_val(objects0, int2, object4, object0)) {
                        if (object1 != null) {
                            if ((object4 == null || object4 == TOMBSTONE) && object0 != TOMBSTONE) {
                                chm._size.add(1L);
                            }

                            if (object4 != null && object4 != TOMBSTONE && object0 == TOMBSTONE) {
                                chm._size.add(-1L);
                            }
                        }

                        return object4 == null && object1 != null ? TOMBSTONE : object4;
                    }

                    object4 = val(objects0, int2);
                    if (object4 instanceof NonBlockingHashMap.Prime) {
                        return putIfMatch(nonBlockingHashMap, chm.copy_slot_and_check(nonBlockingHashMap, objects0, int2, object1), object2, object0, object1);
                    }
                }

                throw new AssertionError();
            }
        }
    }

    private final Object[] help_copy(Object[] objects1) {
        Object[] objects0 = this._kvs;
        NonBlockingHashMap.CHM chm = chm(objects0);
        if (chm._newkvs == null) {
            return objects1;
        } else {
            chm.help_copy_impl(this, objects0, false);
            return objects1;
        }
    }

    public Enumeration<TypeV> elements() {
        return new NonBlockingHashMap.SnapshotV();
    }

    @Override
    public Collection<TypeV> values() {
        return new AbstractCollection<TypeV>() {
            @Override
            public void clear() {
                NonBlockingHashMap.this.clear();
            }

            @Override
            public int size() {
                return NonBlockingHashMap.this.size();
            }

            @Override
            public boolean contains(Object object) {
                return NonBlockingHashMap.this.containsValue(object);
            }

            @Override
            public Iterator<TypeV> iterator() {
                return NonBlockingHashMap.this.new SnapshotV();
            }
        };
    }

    public Enumeration<TypeK> keys() {
        return new NonBlockingHashMap.SnapshotK();
    }

    @Override
    public Set<TypeK> keySet() {
        return new AbstractSet<TypeK>() {
            @Override
            public void clear() {
                NonBlockingHashMap.this.clear();
            }

            @Override
            public int size() {
                return NonBlockingHashMap.this.size();
            }

            @Override
            public boolean contains(Object object) {
                return NonBlockingHashMap.this.containsKey(object);
            }

            @Override
            public boolean remove(Object object) {
                return NonBlockingHashMap.this.remove(object) != null;
            }

            @Override
            public Iterator<TypeK> iterator() {
                return NonBlockingHashMap.this.new SnapshotK();
            }
        };
    }

    @Override
    public Set<Entry<TypeK, TypeV>> entrySet() {
        return new AbstractSet<Entry<TypeK, TypeV>>() {
            @Override
            public void clear() {
                NonBlockingHashMap.this.clear();
            }

            @Override
            public int size() {
                return NonBlockingHashMap.this.size();
            }

            @Override
            public boolean remove(Object object) {
                return !(object instanceof Entry entry) ? false : NonBlockingHashMap.this.remove(entry.getKey(), entry.getValue());
            }

            @Override
            public boolean contains(Object object0) {
                if (!(object0 instanceof Entry entry)) {
                    return false;
                } else {
                    Object object1 = NonBlockingHashMap.this.get(entry.getKey());
                    return object1.equals(entry.getValue());
                }
            }

            @Override
            public Iterator<Entry<TypeK, TypeV>> iterator() {
                return NonBlockingHashMap.this.new SnapshotE();
            }
        };
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();

        for (Object object0 : this.keySet()) {
            Object object1 = this.get(object0);
            objectOutputStream.writeObject(object0);
            objectOutputStream.writeObject(object1);
        }

        objectOutputStream.writeObject(null);
        objectOutputStream.writeObject(null);
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.initialize(8);

        while (true) {
            Object object0 = objectInputStream.readObject();
            Object object1 = objectInputStream.readObject();
            if (object0 == null) {
                return;
            }

            this.put((TypeK)object0, (TypeV)object1);
        }
    }

    static {
        Object object = null;

        try {
            object = NonBlockingHashMap.class.getDeclaredField("_kvs");
        } catch (NoSuchFieldException noSuchFieldException) {
            throw new RuntimeException(noSuchFieldException);
        }

        _kvs_offset = _unsafe.objectFieldOffset((Field)object);
        NO_MATCH_OLD = new Object();
        MATCH_ANY = new Object();
        TOMBSTONE = new Object();
        TOMBPRIME = new NonBlockingHashMap.Prime(TOMBSTONE);
    }

    private static final class CHM<TypeK, TypeV> {
        private final Counter _size;
        private final Counter _slots;
        volatile Object[] _newkvs;
        private final AtomicReferenceFieldUpdater<NonBlockingHashMap.CHM, Object[]> _newkvsUpdater = AtomicReferenceFieldUpdater.newUpdater(
            NonBlockingHashMap.CHM.class, Object[].class, "_newkvs"
        );
        volatile long _resizers;
        private static final AtomicLongFieldUpdater<NonBlockingHashMap.CHM> _resizerUpdater = AtomicLongFieldUpdater.newUpdater(
            NonBlockingHashMap.CHM.class, "_resizers"
        );
        volatile long _copyIdx = 0L;
        private static final AtomicLongFieldUpdater<NonBlockingHashMap.CHM> _copyIdxUpdater = AtomicLongFieldUpdater.newUpdater(
            NonBlockingHashMap.CHM.class, "_copyIdx"
        );
        volatile long _copyDone = 0L;
        private static final AtomicLongFieldUpdater<NonBlockingHashMap.CHM> _copyDoneUpdater = AtomicLongFieldUpdater.newUpdater(
            NonBlockingHashMap.CHM.class, "_copyDone"
        );

        public int size() {
            return (int)this._size.get();
        }

        public int slots() {
            return (int)this._slots.get();
        }

        boolean CAS_newkvs(Object[] objects) {
            while (this._newkvs == null) {
                if (this._newkvsUpdater.compareAndSet(this, null, objects)) {
                    return true;
                }
            }

            return false;
        }

        CHM(Counter counter) {
            this._size = counter;
            this._slots = new Counter();
        }

        private final boolean tableFull(int int1, int int0) {
            return int1 >= 10 && this._slots.estimate_get() >= NonBlockingHashMap.reprobe_limit(int0);
        }

        private final Object[] resize(NonBlockingHashMap nonBlockingHashMap, Object[] objects0) {
            assert NonBlockingHashMap.chm(objects0) == this;

            Object[] objects1 = this._newkvs;
            if (objects1 != null) {
                return objects1;
            } else {
                int int0 = NonBlockingHashMap.len(objects0);
                int int1 = this.size();
                int int2 = int1;
                if (int1 >= int0 >> 2) {
                    int2 = int0 << 1;
                    if (int1 >= int0 >> 1) {
                        int2 = int0 << 2;
                    }
                }

                long long0 = System.currentTimeMillis();
                long long1 = 0L;
                if (int2 <= int0 && long0 <= nonBlockingHashMap._last_resize_milli + 10000L && this._slots.estimate_get() >= int1 << 1) {
                    int2 = int0 << 1;
                }

                if (int2 < int0) {
                    int2 = int0;
                }

                int int3 = 3;

                while (1 << int3 < int2) {
                    int3++;
                }

                long long2 = this._resizers;

                while (!_resizerUpdater.compareAndSet(this, long2, long2 + 1L)) {
                    long2 = this._resizers;
                }

                int int4 = (1 << int3 << 1) + 4 << 3 >> 20;
                if (long2 >= 2L && int4 > 0) {
                    objects1 = this._newkvs;
                    if (objects1 != null) {
                        return objects1;
                    }

                    try {
                        Thread.sleep(8 * int4);
                    } catch (Exception exception) {
                    }
                }

                objects1 = this._newkvs;
                if (objects1 != null) {
                    return objects1;
                } else {
                    objects1 = new Object[(1 << int3 << 1) + 2];
                    objects1[0] = new NonBlockingHashMap.CHM(this._size);
                    objects1[1] = new int[1 << int3];
                    if (this._newkvs != null) {
                        return this._newkvs;
                    } else {
                        if (this.CAS_newkvs(objects1)) {
                            nonBlockingHashMap.rehash();
                        } else {
                            objects1 = this._newkvs;
                        }

                        return objects1;
                    }
                }
            }
        }

        private final void help_copy_impl(NonBlockingHashMap nonBlockingHashMap, Object[] objects0, boolean boolean0) {
            assert NonBlockingHashMap.chm(objects0) == this;

            Object[] objects1 = this._newkvs;

            assert objects1 != null;

            int int0 = NonBlockingHashMap.len(objects0);
            int int1 = Math.min(int0, 1024);
            int int2 = -1;
            int int3 = -9999;

            while (this._copyDone < int0) {
                if (int2 == -1) {
                    int3 = (int)this._copyIdx;

                    while (int3 < int0 << 1 && !_copyIdxUpdater.compareAndSet(this, int3, int3 + int1)) {
                        int3 = (int)this._copyIdx;
                    }

                    if (int3 >= int0 << 1) {
                        int2 = int3;
                    }
                }

                int int4 = 0;

                for (int int5 = 0; int5 < int1; int5++) {
                    if (this.copy_slot(nonBlockingHashMap, int3 + int5 & int0 - 1, objects0, objects1)) {
                        int4++;
                    }
                }

                if (int4 > 0) {
                    this.copy_check_and_promote(nonBlockingHashMap, objects0, int4);
                }

                int3 += int1;
                if (!boolean0 && int2 == -1) {
                    return;
                }
            }

            this.copy_check_and_promote(nonBlockingHashMap, objects0, 0);
        }

        private final Object[] copy_slot_and_check(NonBlockingHashMap nonBlockingHashMap, Object[] objects0, int int0, Object object) {
            assert NonBlockingHashMap.chm(objects0) == this;

            Object[] objects1 = this._newkvs;

            assert objects1 != null;

            if (this.copy_slot(nonBlockingHashMap, int0, objects0, this._newkvs)) {
                this.copy_check_and_promote(nonBlockingHashMap, objects0, 1);
            }

            return object == null ? objects1 : nonBlockingHashMap.help_copy(objects1);
        }

        private final void copy_check_and_promote(NonBlockingHashMap nonBlockingHashMap, Object[] objects, int int1) {
            assert NonBlockingHashMap.chm(objects) == this;

            int int0 = NonBlockingHashMap.len(objects);
            long long0 = this._copyDone;

            assert long0 + (long)int1 <= (long)int0;

            if (int1 > 0) {
                while (!_copyDoneUpdater.compareAndSet(this, long0, long0 + int1)) {
                    long0 = this._copyDone;

                    assert long0 + (long)int1 <= (long)int0;
                }
            }

            if (long0 + int1 == int0 && nonBlockingHashMap._kvs == objects && nonBlockingHashMap.CAS_kvs(objects, this._newkvs)) {
                nonBlockingHashMap._last_resize_milli = System.currentTimeMillis();
            }
        }

        private boolean copy_slot(NonBlockingHashMap nonBlockingHashMap, int int0, Object[] objects0, Object[] objects1) {
            Object object0;
            while ((object0 = NonBlockingHashMap.key(objects0, int0)) == null) {
                NonBlockingHashMap.CAS_key(objects0, int0, null, NonBlockingHashMap.TOMBSTONE);
            }

            Object object1;
            for (object1 = NonBlockingHashMap.val(objects0, int0);
                !(object1 instanceof NonBlockingHashMap.Prime);
                object1 = NonBlockingHashMap.val(objects0, int0)
            ) {
                NonBlockingHashMap.Prime prime = object1 != null && object1 != NonBlockingHashMap.TOMBSTONE
                    ? new NonBlockingHashMap.Prime(object1)
                    : NonBlockingHashMap.TOMBPRIME;
                if (NonBlockingHashMap.CAS_val(objects0, int0, object1, prime)) {
                    if (prime == NonBlockingHashMap.TOMBPRIME) {
                        return true;
                    }

                    object1 = prime;
                    break;
                }
            }

            if (object1 == NonBlockingHashMap.TOMBPRIME) {
                return false;
            } else {
                Object object2 = ((NonBlockingHashMap.Prime)object1)._V;

                assert object2 != NonBlockingHashMap.TOMBSTONE;

                boolean boolean0 = NonBlockingHashMap.putIfMatch(nonBlockingHashMap, objects1, object0, object2, null) == null;

                while (!NonBlockingHashMap.CAS_val(objects0, int0, object1, NonBlockingHashMap.TOMBPRIME)) {
                    object1 = NonBlockingHashMap.val(objects0, int0);
                }

                return boolean0;
            }
        }
    }

    private class NBHMEntry extends AbstractEntry<TypeK, TypeV> {
        NBHMEntry(TypeK object0, TypeV object1) {
            super((TypeK)object0, (TypeV)object1);
        }

        @Override
        public TypeV setValue(TypeV object) {
            if (object == null) {
                throw new NullPointerException();
            } else {
                this._val = (TypeV)object;
                return NonBlockingHashMap.this.put(this._key, (TypeV)object);
            }
        }
    }

    private static final class Prime {
        final Object _V;

        Prime(Object object) {
            this._V = object;
        }

        static Object unbox(Object object) {
            return object instanceof NonBlockingHashMap.Prime ? ((NonBlockingHashMap.Prime)object)._V : object;
        }
    }

    private class SnapshotE implements Iterator<Entry<TypeK, TypeV>> {
        final NonBlockingHashMap<TypeK, TypeV>.SnapshotV _ss = NonBlockingHashMap.this.new SnapshotV();

        public SnapshotE() {
        }

        @Override
        public void remove() {
            this._ss.remove();
        }

        public Entry<TypeK, TypeV> next() {
            this._ss.next();
            return NonBlockingHashMap.this.new NBHMEntry(this._ss._prevK, this._ss._prevV);
        }

        @Override
        public boolean hasNext() {
            return this._ss.hasNext();
        }
    }

    private class SnapshotK implements Iterator<TypeK>, Enumeration<TypeK> {
        final NonBlockingHashMap<TypeK, TypeV>.SnapshotV _ss = NonBlockingHashMap.this.new SnapshotV();

        public SnapshotK() {
        }

        @Override
        public void remove() {
            this._ss.remove();
        }

        @Override
        public TypeK next() {
            this._ss.next();
            return (TypeK)this._ss._prevK;
        }

        @Override
        public boolean hasNext() {
            return this._ss.hasNext();
        }

        @Override
        public TypeK nextElement() {
            return (TypeK)this.next();
        }

        @Override
        public boolean hasMoreElements() {
            return this.hasNext();
        }
    }

    private class SnapshotV implements Iterator<TypeV>, Enumeration<TypeV> {
        final Object[] _sskvs;
        private int _idx;
        private Object _nextK;
        private Object _prevK;
        private TypeV _nextV;
        private TypeV _prevV;

        public SnapshotV() {
            while (true) {
                Object[] objects = NonBlockingHashMap.this._kvs;
                NonBlockingHashMap.CHM chm = NonBlockingHashMap.chm(objects);
                if (chm._newkvs == null) {
                    this._sskvs = objects;
                    this.next();
                    return;
                }

                chm.help_copy_impl(NonBlockingHashMap.this, objects, true);
            }
        }

        int length() {
            return NonBlockingHashMap.len(this._sskvs);
        }

        Object key(int int0) {
            return NonBlockingHashMap.key(this._sskvs, int0);
        }

        @Override
        public boolean hasNext() {
            return this._nextV != null;
        }

        @Override
        public TypeV next() {
            if (this._idx != 0 && this._nextV == null) {
                throw new NoSuchElementException();
            } else {
                this._prevK = this._nextK;
                this._prevV = this._nextV;
                this._nextV = null;

                while (this._idx < this.length()) {
                    this._nextK = this.key(this._idx++);
                    if (this._nextK != null && this._nextK != NonBlockingHashMap.TOMBSTONE && (this._nextV = NonBlockingHashMap.this.get(this._nextK)) != null) {
                        break;
                    }
                }

                return this._prevV;
            }
        }

        @Override
        public void remove() {
            if (this._prevV == null) {
                throw new IllegalStateException();
            } else {
                NonBlockingHashMap.putIfMatch(NonBlockingHashMap.this, this._sskvs, this._prevK, NonBlockingHashMap.TOMBSTONE, this._prevV);
                this._prevV = null;
            }
        }

        @Override
        public TypeV nextElement() {
            return (TypeV)this.next();
        }

        @Override
        public boolean hasMoreElements() {
            return this.hasNext();
        }
    }
}
