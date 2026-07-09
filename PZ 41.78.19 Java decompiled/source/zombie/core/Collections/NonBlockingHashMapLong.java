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
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import sun.misc.Unsafe;

public class NonBlockingHashMapLong<TypeV> extends AbstractMap<Long, TypeV> implements ConcurrentMap<Long, TypeV>, Serializable {
    private static final long serialVersionUID = 1234123412341234124L;
    private static final int REPROBE_LIMIT = 10;
    private static final Unsafe _unsafe = UtilUnsafe.getUnsafe();
    private static final int _Obase = _unsafe.arrayBaseOffset(Object[].class);
    private static final int _Oscale = _unsafe.arrayIndexScale(Object[].class);
    private static final int _Lbase = _unsafe.arrayBaseOffset(long[].class);
    private static final int _Lscale = _unsafe.arrayIndexScale(long[].class);
    private static final long _chm_offset;
    private static final long _val_1_offset;
    private transient NonBlockingHashMapLong.CHM _chm;
    private transient Object _val_1;
    private transient long _last_resize_milli;
    private final boolean _opt_for_space;
    private static final int MIN_SIZE_LOG = 4;
    private static final int MIN_SIZE = 16;
    private static final Object NO_MATCH_OLD;
    private static final Object MATCH_ANY;
    private static final Object TOMBSTONE;
    private static final NonBlockingHashMapLong.Prime TOMBPRIME;
    private static final long NO_KEY = 0L;
    private transient Counter _reprobes = new Counter();

    private static long rawIndex(Object[] objects, int int0) {
        assert int0 >= 0 && int0 < objects.length;

        return _Obase + int0 * _Oscale;
    }

    private static long rawIndex(long[] longs, int int0) {
        assert int0 >= 0 && int0 < longs.length;

        return _Lbase + int0 * _Lscale;
    }

    private final boolean CAS(long long0, Object object0, Object object1) {
        return _unsafe.compareAndSwapObject(this, long0, object0, object1);
    }

    public final void print() {
        System.out.println("=========");
        print_impl(-99, 0L, this._val_1);
        this._chm.print();
        System.out.println("=========");
    }

    private static final void print_impl(int int0, long long0, Object object0) {
        String string0 = object0 instanceof NonBlockingHashMapLong.Prime ? "prime_" : "";
        Object object1 = NonBlockingHashMapLong.Prime.unbox(object0);
        String string1 = object1 == TOMBSTONE ? "tombstone" : object1.toString();
        System.out.println("[" + int0 + "]=(" + long0 + "," + string0 + string1 + ")");
    }

    private final void print2() {
        System.out.println("=========");
        print2_impl(-99, 0L, this._val_1);
        this._chm.print();
        System.out.println("=========");
    }

    private static final void print2_impl(int int0, long long0, Object object) {
        if (object != null && NonBlockingHashMapLong.Prime.unbox(object) != TOMBSTONE) {
            print_impl(int0, long0, object);
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

    public NonBlockingHashMapLong() {
        this(16, true);
    }

    public NonBlockingHashMapLong(int int0) {
        this(int0, true);
    }

    public NonBlockingHashMapLong(boolean boolean0) {
        this(1, boolean0);
    }

    public NonBlockingHashMapLong(int int0, boolean boolean0) {
        this._opt_for_space = boolean0;
        this.initialize(int0);
    }

    private final void initialize(int int0) {
        if (int0 < 0) {
            throw new IllegalArgumentException();
        } else {
            int int1 = 4;

            while (1 << int1 < int0) {
                int1++;
            }

            this._chm = new NonBlockingHashMapLong.CHM(this, new Counter(), int1);
            this._val_1 = TOMBSTONE;
            this._last_resize_milli = System.currentTimeMillis();
        }
    }

    @Override
    public int size() {
        return (this._val_1 == TOMBSTONE ? 0 : 1) + this._chm.size();
    }

    public boolean containsKey(long long0) {
        return this.get(long0) != null;
    }

    public boolean contains(Object object) {
        return this.containsValue(object);
    }

    public TypeV put(long long0, TypeV object) {
        return this.putIfMatch(long0, object, NO_MATCH_OLD);
    }

    public TypeV putIfAbsent(long long0, TypeV object) {
        return this.putIfMatch(long0, object, TOMBSTONE);
    }

    public TypeV remove(long long0) {
        return this.putIfMatch(long0, TOMBSTONE, NO_MATCH_OLD);
    }

    public boolean remove(long long0, Object object) {
        return this.putIfMatch(long0, TOMBSTONE, object) == object;
    }

    public TypeV replace(long long0, TypeV object) {
        return this.putIfMatch(long0, object, MATCH_ANY);
    }

    public boolean replace(long long0, TypeV object0, TypeV object1) {
        return this.putIfMatch(long0, object1, object0) == object0;
    }

    private final TypeV putIfMatch(long long0, Object object0, Object object1) {
        if (object1 == null || object0 == null) {
            throw new NullPointerException();
        } else if (long0 != 0L) {
            Object object2 = this._chm.putIfMatch(long0, object0, object1);

            assert !(object2 instanceof NonBlockingHashMapLong.Prime);

            assert object2 != null;

            return (TypeV)(object2 == TOMBSTONE ? null : object2);
        } else {
            Object object3 = this._val_1;
            if (object1 == NO_MATCH_OLD || object3 == object1 || object1 == MATCH_ANY && object3 != TOMBSTONE || object1.equals(object3)) {
                this.CAS(_val_1_offset, object3, object0);
            }

            return (TypeV)(object3 == TOMBSTONE ? null : object3);
        }
    }

    @Override
    public void clear() {
        NonBlockingHashMapLong.CHM chm = new NonBlockingHashMapLong.CHM(this, new Counter(), 4);

        while (!this.CAS(_chm_offset, this._chm, chm)) {
        }

        this.CAS(_val_1_offset, this._val_1, TOMBSTONE);
    }

    @Override
    public boolean containsValue(Object object0) {
        if (object0 == null) {
            return false;
        } else if (object0 == this._val_1) {
            return true;
        } else {
            for (Object object1 : this.values()) {
                if (object1 == object0 || object1.equals(object0)) {
                    return true;
                }
            }

            return false;
        }
    }

    public final TypeV get(long long0) {
        if (long0 == 0L) {
            Object object0 = this._val_1;
            return (TypeV)(object0 == TOMBSTONE ? null : object0);
        } else {
            Object object1 = this._chm.get_impl(long0);

            assert !(object1 instanceof NonBlockingHashMapLong.Prime);

            assert object1 != TOMBSTONE;

            return (TypeV)object1;
        }
    }

    @Override
    public TypeV get(Object object) {
        return object instanceof Long ? this.get(((Long)object).longValue()) : null;
    }

    @Override
    public TypeV remove(Object object) {
        return object instanceof Long ? this.remove(((Long)object).longValue()) : null;
    }

    @Override
    public boolean remove(Object object1, Object object0) {
        return object1 instanceof Long ? this.remove(((Long)object1).longValue(), object0) : false;
    }

    @Override
    public boolean containsKey(Object object) {
        return object instanceof Long ? this.containsKey(((Long)object).longValue()) : false;
    }

    public TypeV putIfAbsent(Long long0, TypeV object) {
        return this.putIfAbsent(long0.longValue(), (TypeV)object);
    }

    public TypeV replace(Long long0, TypeV object) {
        return this.replace(long0.longValue(), (TypeV)object);
    }

    public TypeV put(Long long0, TypeV object) {
        return this.put(long0.longValue(), (TypeV)object);
    }

    public boolean replace(Long long0, TypeV object0, TypeV object1) {
        return this.replace(long0.longValue(), (TypeV)object0, (TypeV)object1);
    }

    private final void help_copy() {
        NonBlockingHashMapLong.CHM chm = this._chm;
        if (chm._newchm != null) {
            chm.help_copy_impl(false);
        }
    }

    public Enumeration<TypeV> elements() {
        return new NonBlockingHashMapLong.SnapshotV();
    }

    @Override
    public Collection<TypeV> values() {
        return new AbstractCollection<TypeV>() {
            @Override
            public void clear() {
                NonBlockingHashMapLong.this.clear();
            }

            @Override
            public int size() {
                return NonBlockingHashMapLong.this.size();
            }

            @Override
            public boolean contains(Object object) {
                return NonBlockingHashMapLong.this.containsValue(object);
            }

            @Override
            public Iterator<TypeV> iterator() {
                return NonBlockingHashMapLong.this.new SnapshotV();
            }
        };
    }

    public Enumeration<Long> keys() {
        return new NonBlockingHashMapLong.IteratorLong();
    }

    @Override
    public Set<Long> keySet() {
        return new AbstractSet<Long>() {
            @Override
            public void clear() {
                NonBlockingHashMapLong.this.clear();
            }

            @Override
            public int size() {
                return NonBlockingHashMapLong.this.size();
            }

            @Override
            public boolean contains(Object object) {
                return NonBlockingHashMapLong.this.containsKey(object);
            }

            @Override
            public boolean remove(Object object) {
                return NonBlockingHashMapLong.this.remove(object) != null;
            }

            public NonBlockingHashMapLong<TypeV>.IteratorLong iterator() {
                return NonBlockingHashMapLong.this.new IteratorLong();
            }
        };
    }

    @Override
    public Set<Entry<Long, TypeV>> entrySet() {
        return new AbstractSet<Entry<Long, TypeV>>() {
            @Override
            public void clear() {
                NonBlockingHashMapLong.this.clear();
            }

            @Override
            public int size() {
                return NonBlockingHashMapLong.this.size();
            }

            @Override
            public boolean remove(Object object) {
                return !(object instanceof Entry entry) ? false : NonBlockingHashMapLong.this.remove(entry.getKey(), entry.getValue());
            }

            @Override
            public boolean contains(Object object0) {
                if (!(object0 instanceof Entry entry)) {
                    return false;
                } else {
                    Object object1 = NonBlockingHashMapLong.this.get(entry.getKey());
                    return object1.equals(entry.getValue());
                }
            }

            @Override
            public Iterator<Entry<Long, TypeV>> iterator() {
                return NonBlockingHashMapLong.this.new SnapshotE();
            }
        };
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();

        for (long long0 : this.keySet()) {
            Object object = this.get(long0);
            objectOutputStream.writeLong(long0);
            objectOutputStream.writeObject(object);
        }

        objectOutputStream.writeLong(0L);
        objectOutputStream.writeObject(null);
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.initialize(16);

        while (true) {
            long long0 = objectInputStream.readLong();
            Object object = objectInputStream.readObject();
            if (long0 == 0L && object == null) {
                return;
            }

            this.put(long0, (TypeV)object);
        }
    }

    static {
        Object object = null;

        try {
            object = NonBlockingHashMapLong.class.getDeclaredField("_chm");
        } catch (NoSuchFieldException noSuchFieldException0) {
            throw new RuntimeException(noSuchFieldException0);
        }

        _chm_offset = _unsafe.objectFieldOffset((Field)object);

        try {
            object = NonBlockingHashMapLong.class.getDeclaredField("_val_1");
        } catch (NoSuchFieldException noSuchFieldException1) {
            throw new RuntimeException(noSuchFieldException1);
        }

        _val_1_offset = _unsafe.objectFieldOffset((Field)object);
        NO_MATCH_OLD = new Object();
        MATCH_ANY = new Object();
        TOMBSTONE = new Object();
        TOMBPRIME = new NonBlockingHashMapLong.Prime(TOMBSTONE);
    }

    private static final class CHM<TypeV> implements Serializable {
        final NonBlockingHashMapLong _nbhml;
        private final Counter _size;
        private final Counter _slots;
        volatile NonBlockingHashMapLong.CHM _newchm;
        private static final AtomicReferenceFieldUpdater<NonBlockingHashMapLong.CHM, NonBlockingHashMapLong.CHM> _newchmUpdater = AtomicReferenceFieldUpdater.newUpdater(
            NonBlockingHashMapLong.CHM.class, NonBlockingHashMapLong.CHM.class, "_newchm"
        );
        volatile long _resizers;
        private static final AtomicLongFieldUpdater<NonBlockingHashMapLong.CHM> _resizerUpdater = AtomicLongFieldUpdater.newUpdater(
            NonBlockingHashMapLong.CHM.class, "_resizers"
        );
        final long[] _keys;
        final Object[] _vals;
        volatile long _copyIdx = 0L;
        private static final AtomicLongFieldUpdater<NonBlockingHashMapLong.CHM> _copyIdxUpdater = AtomicLongFieldUpdater.newUpdater(
            NonBlockingHashMapLong.CHM.class, "_copyIdx"
        );
        volatile long _copyDone = 0L;
        private static final AtomicLongFieldUpdater<NonBlockingHashMapLong.CHM> _copyDoneUpdater = AtomicLongFieldUpdater.newUpdater(
            NonBlockingHashMapLong.CHM.class, "_copyDone"
        );

        public int size() {
            return (int)this._size.get();
        }

        public int slots() {
            return (int)this._slots.get();
        }

        boolean CAS_newchm(NonBlockingHashMapLong.CHM chm1) {
            return _newchmUpdater.compareAndSet(this, null, chm1);
        }

        private final boolean CAS_key(int int0, long long0, long long1) {
            return NonBlockingHashMapLong._unsafe.compareAndSwapLong(this._keys, NonBlockingHashMapLong.rawIndex(this._keys, int0), long0, long1);
        }

        private final boolean CAS_val(int int0, Object object0, Object object1) {
            return NonBlockingHashMapLong._unsafe.compareAndSwapObject(this._vals, NonBlockingHashMapLong.rawIndex(this._vals, int0), object0, object1);
        }

        CHM(NonBlockingHashMapLong nonBlockingHashMapLong, Counter counter, int int0) {
            this._nbhml = nonBlockingHashMapLong;
            this._size = counter;
            this._slots = new Counter();
            this._keys = new long[1 << int0];
            this._vals = new Object[1 << int0];
        }

        private final void print() {
            for (int int0 = 0; int0 < this._keys.length; int0++) {
                long long0 = this._keys[int0];
                if (long0 != 0L) {
                    NonBlockingHashMapLong.print_impl(int0, long0, this._vals[int0]);
                }
            }

            NonBlockingHashMapLong.CHM chm1 = this._newchm;
            if (chm1 != null) {
                System.out.println("----");
                chm1.print();
            }
        }

        private final void print2() {
            for (int int0 = 0; int0 < this._keys.length; int0++) {
                long long0 = this._keys[int0];
                if (long0 != 0L) {
                    NonBlockingHashMapLong.print2_impl(int0, long0, this._vals[int0]);
                }
            }

            NonBlockingHashMapLong.CHM chm1 = this._newchm;
            if (chm1 != null) {
                System.out.println("----");
                chm1.print2();
            }
        }

        private final Object get_impl(long long0) {
            int int0 = this._keys.length;
            int int1 = (int)(long0 & int0 - 1);
            int int2 = 0;

            while (true) {
                long long1 = this._keys[int1];
                Object object = this._vals[int1];
                if (long1 == 0L) {
                    return null;
                }

                if (long0 == long1) {
                    if (!(object instanceof NonBlockingHashMapLong.Prime)) {
                        if (object == NonBlockingHashMapLong.TOMBSTONE) {
                            return null;
                        }

                        NonBlockingHashMapLong.CHM chm1 = this._newchm;
                        return object;
                    }

                    return this.copy_slot_and_check(int1, long0).get_impl(long0);
                }

                if (++int2 >= NonBlockingHashMapLong.reprobe_limit(int0)) {
                    return this._newchm == null ? null : this.copy_slot_and_check(int1, long0).get_impl(long0);
                }

                int1 = int1 + 1 & int0 - 1;
            }
        }

        private final Object putIfMatch(long long0, Object object0, Object object1) {
            assert object0 != null;

            assert !(object0 instanceof NonBlockingHashMapLong.Prime);

            assert !(object1 instanceof NonBlockingHashMapLong.Prime);

            int int0 = this._keys.length;
            int int1 = (int)(long0 & int0 - 1);
            int int2 = 0;
            long long1 = 0L;
            Object object2 = null;

            while (true) {
                object2 = this._vals[int1];
                long1 = this._keys[int1];
                if (long1 == 0L) {
                    if (object0 == NonBlockingHashMapLong.TOMBSTONE) {
                        return object0;
                    }

                    if (this.CAS_key(int1, 0L, long0)) {
                        this._slots.add(1L);
                        break;
                    }

                    long1 = this._keys[int1];

                    assert long1 != 0L;
                }

                if (long1 == long0) {
                    break;
                }

                if (++int2 >= NonBlockingHashMapLong.reprobe_limit(int0)) {
                    NonBlockingHashMapLong.CHM chm1 = this.resize();
                    if (object1 != null) {
                        this._nbhml.help_copy();
                    }

                    return chm1.putIfMatch(long0, object0, object1);
                }

                int1 = int1 + 1 & int0 - 1;
            }

            if (object0 == object2) {
                return object2;
            } else if ((object2 != null || !this.tableFull(int2, int0)) && !(object2 instanceof NonBlockingHashMapLong.Prime)) {
                while ($assertionsDisabled || !(object2 instanceof NonBlockingHashMapLong.Prime)) {
                    if (object1 != NonBlockingHashMapLong.NO_MATCH_OLD
                        && object2 != object1
                        && (object1 != NonBlockingHashMapLong.MATCH_ANY || object2 == NonBlockingHashMapLong.TOMBSTONE || object2 == null)
                        && (object2 != null || object1 != NonBlockingHashMapLong.TOMBSTONE)
                        && (object1 == null || !object1.equals(object2))) {
                        return object2;
                    }

                    if (this.CAS_val(int1, object2, object0)) {
                        if (object1 != null) {
                            if ((object2 == null || object2 == NonBlockingHashMapLong.TOMBSTONE) && object0 != NonBlockingHashMapLong.TOMBSTONE) {
                                this._size.add(1L);
                            }

                            if (object2 != null && object2 != NonBlockingHashMapLong.TOMBSTONE && object0 == NonBlockingHashMapLong.TOMBSTONE) {
                                this._size.add(-1L);
                            }
                        }

                        return object2 == null && object1 != null ? NonBlockingHashMapLong.TOMBSTONE : object2;
                    }

                    object2 = this._vals[int1];
                    if (object2 instanceof NonBlockingHashMapLong.Prime) {
                        return this.copy_slot_and_check(int1, object1).putIfMatch(long0, object0, object1);
                    }
                }

                throw new AssertionError();
            } else {
                this.resize();
                return this.copy_slot_and_check(int1, object1).putIfMatch(long0, object0, object1);
            }
        }

        private final boolean tableFull(int int1, int int0) {
            return int1 >= 10 && this._slots.estimate_get() >= NonBlockingHashMapLong.reprobe_limit(int0);
        }

        private final NonBlockingHashMapLong.CHM resize() {
            NonBlockingHashMapLong.CHM chm0 = this._newchm;
            if (chm0 != null) {
                return chm0;
            } else {
                int int0 = this._keys.length;
                int int1 = this.size();
                int int2 = int1;
                if (this._nbhml._opt_for_space) {
                    if (int1 >= int0 >> 1) {
                        int2 = int0 << 1;
                    }
                } else if (int1 >= int0 >> 2) {
                    int2 = int0 << 1;
                    if (int1 >= int0 >> 1) {
                        int2 = int0 << 2;
                    }
                }

                long long0 = System.currentTimeMillis();
                long long1 = 0L;
                if (int2 <= int0 && long0 <= this._nbhml._last_resize_milli + 10000L) {
                    int2 = int0 << 1;
                }

                if (int2 < int0) {
                    int2 = int0;
                }

                int int3 = 4;

                while (1 << int3 < int2) {
                    int3++;
                }

                long long2 = this._resizers;

                while (!_resizerUpdater.compareAndSet(this, long2, long2 + 1L)) {
                    long2 = this._resizers;
                }

                int int4 = (1 << int3 << 1) + 4 << 3 >> 20;
                if (long2 >= 2L && int4 > 0) {
                    chm0 = this._newchm;
                    if (chm0 != null) {
                        return chm0;
                    }

                    try {
                        Thread.sleep(8 * int4);
                    } catch (Exception exception) {
                    }
                }

                chm0 = this._newchm;
                if (chm0 != null) {
                    return chm0;
                } else {
                    chm0 = new NonBlockingHashMapLong.CHM(this._nbhml, this._size, int3);
                    if (this._newchm != null) {
                        return this._newchm;
                    } else {
                        if (!this.CAS_newchm(chm0)) {
                            chm0 = this._newchm;
                        }

                        return chm0;
                    }
                }
            }
        }

        private final void help_copy_impl(boolean boolean0) {
            NonBlockingHashMapLong.CHM chm0 = this._newchm;

            assert chm0 != null;

            int int0 = this._keys.length;
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
                    if (this.copy_slot(int3 + int5 & int0 - 1)) {
                        int4++;
                    }
                }

                if (int4 > 0) {
                    this.copy_check_and_promote(int4);
                }

                int3 += int1;
                if (!boolean0 && int2 == -1) {
                    return;
                }
            }

            this.copy_check_and_promote(0);
        }

        private final NonBlockingHashMapLong.CHM copy_slot_and_check(int int0, Object object) {
            assert this._newchm != null;

            if (this.copy_slot(int0)) {
                this.copy_check_and_promote(1);
            }

            if (object != null) {
                this._nbhml.help_copy();
            }

            return this._newchm;
        }

        private final void copy_check_and_promote(int int1) {
            int int0 = this._keys.length;
            long long0 = this._copyDone;
            long long1 = long0 + int1;

            assert long1 <= (long)int0;

            if (int1 > 0) {
                while (!_copyDoneUpdater.compareAndSet(this, long0, long1)) {
                    long0 = this._copyDone;
                    long1 = long0 + int1;

                    assert long1 <= (long)int0;
                }
            }

            if (long1 == int0 && this._nbhml._chm == this && this._nbhml.CAS(NonBlockingHashMapLong._chm_offset, this, this._newchm)) {
                this._nbhml._last_resize_milli = System.currentTimeMillis();
            }
        }

        private boolean copy_slot(int int0) {
            long long0;
            while ((long0 = this._keys[int0]) == 0L) {
                this.CAS_key(int0, 0L, int0 + this._keys.length);
            }

            Object object0;
            for (object0 = this._vals[int0]; !(object0 instanceof NonBlockingHashMapLong.Prime); object0 = this._vals[int0]) {
                NonBlockingHashMapLong.Prime prime = object0 != null && object0 != NonBlockingHashMapLong.TOMBSTONE
                    ? new NonBlockingHashMapLong.Prime(object0)
                    : NonBlockingHashMapLong.TOMBPRIME;
                if (this.CAS_val(int0, object0, prime)) {
                    if (prime == NonBlockingHashMapLong.TOMBPRIME) {
                        return true;
                    }

                    object0 = prime;
                    break;
                }
            }

            if (object0 == NonBlockingHashMapLong.TOMBPRIME) {
                return false;
            } else {
                Object object1 = ((NonBlockingHashMapLong.Prime)object0)._V;

                assert object1 != NonBlockingHashMapLong.TOMBSTONE;

                boolean boolean0 = this._newchm.putIfMatch(long0, object1, null) == null;

                while (!this.CAS_val(int0, object0, NonBlockingHashMapLong.TOMBPRIME)) {
                    object0 = this._vals[int0];
                }

                return boolean0;
            }
        }
    }

    public class IteratorLong implements Iterator<Long>, Enumeration<Long> {
        private final NonBlockingHashMapLong<TypeV>.SnapshotV _ss = NonBlockingHashMapLong.this.new SnapshotV();

        @Override
        public void remove() {
            this._ss.remove();
        }

        public Long next() {
            this._ss.next();
            return this._ss._prevK;
        }

        public long nextLong() {
            this._ss.next();
            return this._ss._prevK;
        }

        @Override
        public boolean hasNext() {
            return this._ss.hasNext();
        }

        public Long nextElement() {
            return this.next();
        }

        @Override
        public boolean hasMoreElements() {
            return this.hasNext();
        }
    }

    private class NBHMLEntry extends AbstractEntry<Long, TypeV> {
        NBHMLEntry(Long long0, TypeV object) {
            super(long0, (TypeV)object);
        }

        @Override
        public TypeV setValue(TypeV object) {
            if (object == null) {
                throw new NullPointerException();
            } else {
                this._val = (TypeV)object;
                return NonBlockingHashMapLong.this.put(this._key, (TypeV)object);
            }
        }
    }

    private static final class Prime {
        final Object _V;

        Prime(Object object) {
            this._V = object;
        }

        static Object unbox(Object object) {
            return object instanceof NonBlockingHashMapLong.Prime ? ((NonBlockingHashMapLong.Prime)object)._V : object;
        }
    }

    private class SnapshotE implements Iterator<Entry<Long, TypeV>> {
        final NonBlockingHashMapLong<TypeV>.SnapshotV _ss = NonBlockingHashMapLong.this.new SnapshotV();

        public SnapshotE() {
        }

        @Override
        public void remove() {
            this._ss.remove();
        }

        public Entry<Long, TypeV> next() {
            this._ss.next();
            return NonBlockingHashMapLong.this.new NBHMLEntry(this._ss._prevK, this._ss._prevV);
        }

        @Override
        public boolean hasNext() {
            return this._ss.hasNext();
        }
    }

    private class SnapshotV implements Iterator<TypeV>, Enumeration<TypeV> {
        final NonBlockingHashMapLong.CHM _sschm;
        private int _idx;
        private long _nextK;
        private long _prevK;
        private TypeV _nextV;
        private TypeV _prevV;

        public SnapshotV() {
            while (true) {
                NonBlockingHashMapLong.CHM chm = NonBlockingHashMapLong.this._chm;
                if (chm._newchm == null) {
                    this._sschm = chm;
                    this._idx = -1;
                    this.next();
                    return;
                }

                chm.help_copy_impl(true);
            }
        }

        int length() {
            return this._sschm._keys.length;
        }

        long key(int int0) {
            return this._sschm._keys[int0];
        }

        @Override
        public boolean hasNext() {
            return this._nextV != null;
        }

        @Override
        public TypeV next() {
            if (this._idx != -1 && this._nextV == null) {
                throw new NoSuchElementException();
            } else {
                this._prevK = this._nextK;
                this._prevV = this._nextV;
                this._nextV = null;
                if (this._idx == -1) {
                    this._idx = 0;
                    this._nextK = 0L;
                    if ((this._nextV = NonBlockingHashMapLong.this.get(this._nextK)) != null) {
                        return this._prevV;
                    }
                }

                while (this._idx < this.length()) {
                    this._nextK = this.key(this._idx++);
                    if (this._nextK != 0L && (this._nextV = NonBlockingHashMapLong.this.get(this._nextK)) != null) {
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
                this._sschm.putIfMatch(this._prevK, NonBlockingHashMapLong.TOMBSTONE, this._prevV);
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
