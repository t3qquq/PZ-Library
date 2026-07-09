// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.Collections;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import sun.misc.Unsafe;

public class ConcurrentAutoTable implements Serializable {
    private volatile ConcurrentAutoTable.CAT _cat = new ConcurrentAutoTable.CAT(null, 4, 0L);
    private static final AtomicReferenceFieldUpdater<ConcurrentAutoTable, ConcurrentAutoTable.CAT> _catUpdater = AtomicReferenceFieldUpdater.newUpdater(
        ConcurrentAutoTable.class, ConcurrentAutoTable.CAT.class, "_cat"
    );

    public void add(long long0) {
        this.add_if_mask(long0, 0L);
    }

    public void decrement() {
        this.add_if_mask(-1L, 0L);
    }

    public void increment() {
        this.add_if_mask(1L, 0L);
    }

    public void set(long long0) {
        ConcurrentAutoTable.CAT cat = new ConcurrentAutoTable.CAT(null, 4, long0);

        while (!this.CAS_cat(this._cat, cat)) {
        }
    }

    public long get() {
        return this._cat.sum(0L);
    }

    public int intValue() {
        return (int)this._cat.sum(0L);
    }

    public long longValue() {
        return this._cat.sum(0L);
    }

    public long estimate_get() {
        return this._cat.estimate_sum(0L);
    }

    @Override
    public String toString() {
        return this._cat.toString(0L);
    }

    public void print() {
        this._cat.print();
    }

    public int internal_size() {
        return this._cat._t.length;
    }

    private long add_if_mask(long long0, long long1) {
        return this._cat.add_if_mask(long0, long1, hash(), this);
    }

    private boolean CAS_cat(ConcurrentAutoTable.CAT cat0, ConcurrentAutoTable.CAT cat1) {
        return _catUpdater.compareAndSet(this, cat0, cat1);
    }

    private static final int hash() {
        int int0 = System.identityHashCode(Thread.currentThread());
        int0 ^= int0 >>> 20 ^ int0 >>> 12;
        int0 ^= int0 >>> 7 ^ int0 >>> 4;
        return int0 << 2;
    }

    private static class CAT implements Serializable {
        private static final Unsafe _unsafe = UtilUnsafe.getUnsafe();
        private static final int _Lbase = _unsafe.arrayBaseOffset(long[].class);
        private static final int _Lscale = _unsafe.arrayIndexScale(long[].class);
        volatile long _resizers;
        private static final AtomicLongFieldUpdater<ConcurrentAutoTable.CAT> _resizerUpdater = AtomicLongFieldUpdater.newUpdater(
            ConcurrentAutoTable.CAT.class, "_resizers"
        );
        private final ConcurrentAutoTable.CAT _next;
        private volatile long _sum_cache;
        private volatile long _fuzzy_sum_cache;
        private volatile long _fuzzy_time;
        private static final int MAX_SPIN = 2;
        private long[] _t;

        private static long rawIndex(long[] longs, int int0) {
            assert int0 >= 0 && int0 < longs.length;

            return _Lbase + int0 * _Lscale;
        }

        private static final boolean CAS(long[] longs, int int0, long long0, long long1) {
            return _unsafe.compareAndSwapLong(longs, rawIndex(longs, int0), long0, long1);
        }

        CAT(ConcurrentAutoTable.CAT cat1, int int0, long long0) {
            this._next = cat1;
            this._sum_cache = Long.MIN_VALUE;
            this._t = new long[int0];
            this._t[0] = long0;
        }

        public long add_if_mask(long long1, long long2, int int1, ConcurrentAutoTable concurrentAutoTable) {
            long[] longs = this._t;
            int int0 = int1 & longs.length - 1;
            long long0 = longs[int0];
            boolean boolean0 = CAS(longs, int0, long0 & ~long2, long0 + long1);
            if (this._sum_cache != Long.MIN_VALUE) {
                this._sum_cache = Long.MIN_VALUE;
            }

            if (boolean0) {
                return long0;
            } else if ((long0 & long2) != 0L) {
                return long0;
            } else {
                int int2 = 0;

                while (true) {
                    long0 = longs[int0];
                    if ((long0 & long2) != 0L) {
                        return long0;
                    }

                    if (CAS(longs, int0, long0, long0 + long1)) {
                        if (int2 < 2) {
                            return long0;
                        }

                        if (longs.length >= 1048576) {
                            return long0;
                        }

                        long long3 = this._resizers;
                        int int3 = longs.length << 1 << 3;

                        while (!_resizerUpdater.compareAndSet(this, long3, long3 + int3)) {
                            long3 = this._resizers;
                        }

                        long3 += int3;
                        if (concurrentAutoTable._cat != this) {
                            return long0;
                        }

                        if (long3 >> 17 != 0L) {
                            try {
                                Thread.sleep(long3 >> 17);
                            } catch (InterruptedException interruptedException) {
                            }

                            if (concurrentAutoTable._cat != this) {
                                return long0;
                            }
                        }

                        ConcurrentAutoTable.CAT cat1 = new ConcurrentAutoTable.CAT(this, longs.length * 2, 0L);
                        concurrentAutoTable.CAS_cat(this, cat1);
                        return long0;
                    }

                    int2++;
                }
            }
        }

        public long sum(long long1) {
            long long0 = this._sum_cache;
            if (long0 != Long.MIN_VALUE) {
                return long0;
            } else {
                long0 = this._next == null ? 0L : this._next.sum(long1);
                long[] longs = this._t;

                for (int int0 = 0; int0 < longs.length; int0++) {
                    long0 += longs[int0] & ~long1;
                }

                this._sum_cache = long0;
                return long0;
            }
        }

        public long estimate_sum(long long0) {
            if (this._t.length <= 64) {
                return this.sum(long0);
            } else {
                long long1 = System.currentTimeMillis();
                if (this._fuzzy_time != long1) {
                    this._fuzzy_sum_cache = this.sum(long0);
                    this._fuzzy_time = long1;
                }

                return this._fuzzy_sum_cache;
            }
        }

        public void all_or(long long1) {
            long[] longs = this._t;

            for (int int0 = 0; int0 < longs.length; int0++) {
                boolean boolean0 = false;

                while (!boolean0) {
                    long long0 = longs[int0];
                    boolean0 = CAS(longs, int0, long0, long0 | long1);
                }
            }

            if (this._next != null) {
                this._next.all_or(long1);
            }

            if (this._sum_cache != Long.MIN_VALUE) {
                this._sum_cache = Long.MIN_VALUE;
            }
        }

        public void all_and(long long1) {
            long[] longs = this._t;

            for (int int0 = 0; int0 < longs.length; int0++) {
                boolean boolean0 = false;

                while (!boolean0) {
                    long long0 = longs[int0];
                    boolean0 = CAS(longs, int0, long0, long0 & long1);
                }
            }

            if (this._next != null) {
                this._next.all_and(long1);
            }

            if (this._sum_cache != Long.MIN_VALUE) {
                this._sum_cache = Long.MIN_VALUE;
            }
        }

        public void all_set(long long0) {
            long[] longs = this._t;

            for (int int0 = 0; int0 < longs.length; int0++) {
                longs[int0] = long0;
            }

            if (this._next != null) {
                this._next.all_set(long0);
            }

            if (this._sum_cache != Long.MIN_VALUE) {
                this._sum_cache = Long.MIN_VALUE;
            }
        }

        String toString(long long0) {
            return Long.toString(this.sum(long0));
        }

        public void print() {
            long[] longs = this._t;
            System.out.print("[sum=" + this._sum_cache + "," + longs[0]);

            for (int int0 = 1; int0 < longs.length; int0++) {
                System.out.print("," + longs[int0]);
            }

            System.out.print("]");
            if (this._next != null) {
                this._next.print();
            }
        }
    }
}
