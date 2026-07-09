// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.Collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import sun.misc.Unsafe;

public class NonBlockingSetInt extends AbstractSet<Integer> implements Serializable {
    private static final long serialVersionUID = 1234123412341234123L;
    private static final Unsafe _unsafe = UtilUnsafe.getUnsafe();
    private static final long _nbsi_offset;
    private transient NonBlockingSetInt.NBSI _nbsi;

    private final boolean CAS_nbsi(NonBlockingSetInt.NBSI nbsi0, NonBlockingSetInt.NBSI nbsi1) {
        return _unsafe.compareAndSwapObject(this, _nbsi_offset, nbsi0, nbsi1);
    }

    public NonBlockingSetInt() {
        this._nbsi = new NonBlockingSetInt.NBSI(63, new Counter(), this);
    }

    private NonBlockingSetInt(NonBlockingSetInt nonBlockingSetInt2, NonBlockingSetInt nonBlockingSetInt1) {
        this._nbsi = new NonBlockingSetInt.NBSI(nonBlockingSetInt2._nbsi, nonBlockingSetInt1._nbsi, new Counter(), this);
    }

    public boolean add(Integer integer) {
        return this.add(integer.intValue());
    }

    @Override
    public boolean contains(Object object) {
        return object instanceof Integer ? this.contains(((Integer)object).intValue()) : false;
    }

    @Override
    public boolean remove(Object object) {
        return object instanceof Integer ? this.remove(((Integer)object).intValue()) : false;
    }

    public boolean add(int int0) {
        if (int0 < 0) {
            throw new IllegalArgumentException(int0 + "");
        } else {
            return this._nbsi.add(int0);
        }
    }

    public boolean contains(int int0) {
        return int0 < 0 ? false : this._nbsi.contains(int0);
    }

    public boolean remove(int int0) {
        return int0 < 0 ? false : this._nbsi.remove(int0);
    }

    @Override
    public int size() {
        return this._nbsi.size();
    }

    @Override
    public void clear() {
        NonBlockingSetInt.NBSI nbsi = new NonBlockingSetInt.NBSI(63, new Counter(), this);

        while (!this.CAS_nbsi(this._nbsi, nbsi)) {
        }
    }

    public int sizeInBytes() {
        return this._nbsi.sizeInBytes();
    }

    public NonBlockingSetInt intersect(NonBlockingSetInt nonBlockingSetInt2) {
        NonBlockingSetInt nonBlockingSetInt0 = new NonBlockingSetInt(this, nonBlockingSetInt2);
        nonBlockingSetInt0._nbsi.intersect(nonBlockingSetInt0._nbsi, this._nbsi, nonBlockingSetInt2._nbsi);
        return nonBlockingSetInt0;
    }

    public NonBlockingSetInt union(NonBlockingSetInt nonBlockingSetInt2) {
        NonBlockingSetInt nonBlockingSetInt0 = new NonBlockingSetInt(this, nonBlockingSetInt2);
        nonBlockingSetInt0._nbsi.union(nonBlockingSetInt0._nbsi, this._nbsi, nonBlockingSetInt2._nbsi);
        return nonBlockingSetInt0;
    }

    public void print() {
        this._nbsi.print(0);
    }

    @Override
    public Iterator<Integer> iterator() {
        return new NonBlockingSetInt.iter();
    }

    public IntIterator intIterator() {
        return new NonBlockingSetInt.NBSIIntIterator();
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        NonBlockingSetInt.NBSI nbsi = this._nbsi;
        int int0 = this._nbsi._bits.length << 6;
        objectOutputStream.writeInt(int0);

        for (int int1 = 0; int1 < int0; int1++) {
            objectOutputStream.writeBoolean(this._nbsi.contains(int1));
        }
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        int int0 = objectInputStream.readInt();
        this._nbsi = new NonBlockingSetInt.NBSI(int0, new Counter(), this);

        for (int int1 = 0; int1 < int0; int1++) {
            if (objectInputStream.readBoolean()) {
                this._nbsi.add(int1);
            }
        }
    }

    static {
        Field field = null;

        try {
            field = NonBlockingSetInt.class.getDeclaredField("_nbsi");
        } catch (NoSuchFieldException noSuchFieldException) {
        }

        _nbsi_offset = _unsafe.objectFieldOffset(field);
    }

    private static final class NBSI {
        private final transient NonBlockingSetInt _non_blocking_set_int;
        private final transient Counter _size;
        private final long[] _bits;
        private static final int _Lbase = NonBlockingSetInt._unsafe.arrayBaseOffset(long[].class);
        private static final int _Lscale = NonBlockingSetInt._unsafe.arrayIndexScale(long[].class);
        private NonBlockingSetInt.NBSI _new;
        private static final long _new_offset;
        private final transient AtomicInteger _copyIdx;
        private final transient AtomicInteger _copyDone;
        private final transient int _sum_bits_length;
        private final NonBlockingSetInt.NBSI _nbsi64;

        private static long rawIndex(long[] longs, int int0) {
            assert int0 >= 0 && int0 < longs.length;

            return _Lbase + int0 * _Lscale;
        }

        private final boolean CAS(int int0, long long0, long long1) {
            return NonBlockingSetInt._unsafe.compareAndSwapLong(this._bits, rawIndex(this._bits, int0), long0, long1);
        }

        private final boolean CAS_new(NonBlockingSetInt.NBSI nbsi1) {
            return NonBlockingSetInt._unsafe.compareAndSwapObject(this, _new_offset, null, nbsi1);
        }

        private static final long mask(int int0) {
            return 1L << (int0 & 63);
        }

        private NBSI(int int0, Counter counter, NonBlockingSetInt nonBlockingSetInt) {
            this._non_blocking_set_int = nonBlockingSetInt;
            this._size = counter;
            this._copyIdx = counter == null ? null : new AtomicInteger();
            this._copyDone = counter == null ? null : new AtomicInteger();
            this._bits = new long[(int)(int0 + 63L >>> 6)];
            this._nbsi64 = int0 + 1 >>> 6 == 0 ? null : new NonBlockingSetInt.NBSI(int0 + 1 >>> 6, null, null);
            this._sum_bits_length = this._bits.length + (this._nbsi64 == null ? 0 : this._nbsi64._sum_bits_length);
        }

        private NBSI(NonBlockingSetInt.NBSI nbsi2, NonBlockingSetInt.NBSI nbsi1, Counter counter, NonBlockingSetInt nonBlockingSetInt) {
            this._non_blocking_set_int = nonBlockingSetInt;
            this._size = counter;
            this._copyIdx = counter == null ? null : new AtomicInteger();
            this._copyDone = counter == null ? null : new AtomicInteger();
            if (!has_bits(nbsi2) && !has_bits(nbsi1)) {
                this._bits = null;
                this._nbsi64 = null;
                this._sum_bits_length = 0;
            } else {
                if (!has_bits(nbsi2)) {
                    this._bits = new long[nbsi1._bits.length];
                    this._nbsi64 = new NonBlockingSetInt.NBSI(null, nbsi1._nbsi64, null, null);
                } else if (!has_bits(nbsi1)) {
                    this._bits = new long[nbsi2._bits.length];
                    this._nbsi64 = new NonBlockingSetInt.NBSI(null, nbsi2._nbsi64, null, null);
                } else {
                    int int0 = nbsi2._bits.length > nbsi1._bits.length ? nbsi2._bits.length : nbsi1._bits.length;
                    this._bits = new long[int0];
                    this._nbsi64 = new NonBlockingSetInt.NBSI(nbsi2._nbsi64, nbsi1._nbsi64, null, null);
                }

                this._sum_bits_length = this._bits.length + this._nbsi64._sum_bits_length;
            }
        }

        private static boolean has_bits(NonBlockingSetInt.NBSI nbsi) {
            return nbsi != null && nbsi._bits != null;
        }

        public boolean add(int int0) {
            if (int0 >> 6 >= this._bits.length) {
                return this.install_larger_new_bits(int0).help_copy().add(int0);
            } else {
                NonBlockingSetInt.NBSI nbsi1 = this;

                int int1;
                for (int1 = int0; (int1 & 63) == 63; int1 >>= 6) {
                    nbsi1 = nbsi1._nbsi64;
                }

                long long0 = mask(int1);

                long long1;
                do {
                    long1 = nbsi1._bits[int1 >> 6];
                    if (long1 < 0L) {
                        return this.help_copy_impl(int0).help_copy().add(int0);
                    }

                    if ((long1 & long0) != 0L) {
                        return false;
                    }
                } while (!nbsi1.CAS(int1 >> 6, long1, long1 | long0));

                this._size.add(1L);
                return true;
            }
        }

        public boolean remove(int int0) {
            if (int0 >> 6 >= this._bits.length) {
                return this._new == null ? false : this.help_copy().remove(int0);
            } else {
                NonBlockingSetInt.NBSI nbsi1 = this;

                int int1;
                for (int1 = int0; (int1 & 63) == 63; int1 >>= 6) {
                    nbsi1 = nbsi1._nbsi64;
                }

                long long0 = mask(int1);

                long long1;
                do {
                    long1 = nbsi1._bits[int1 >> 6];
                    if (long1 < 0L) {
                        return this.help_copy_impl(int0).help_copy().remove(int0);
                    }

                    if ((long1 & long0) == 0L) {
                        return false;
                    }
                } while (!nbsi1.CAS(int1 >> 6, long1, long1 & ~long0));

                this._size.add(-1L);
                return true;
            }
        }

        public boolean contains(int int0) {
            if (int0 >> 6 >= this._bits.length) {
                return this._new == null ? false : this.help_copy().contains(int0);
            } else {
                NonBlockingSetInt.NBSI nbsi1 = this;

                int int1;
                for (int1 = int0; (int1 & 63) == 63; int1 >>= 6) {
                    nbsi1 = nbsi1._nbsi64;
                }

                long long0 = mask(int1);
                long long1 = nbsi1._bits[int1 >> 6];
                return long1 < 0L ? this.help_copy_impl(int0).help_copy().contains(int0) : (long1 & long0) != 0L;
            }
        }

        public boolean intersect(NonBlockingSetInt.NBSI nbsi2, NonBlockingSetInt.NBSI nbsi1, NonBlockingSetInt.NBSI nbsi0) {
            if (has_bits(nbsi1) && has_bits(nbsi0)) {
                for (int int0 = 0; int0 < nbsi2._bits.length; int0++) {
                    long long0 = nbsi1.safe_read_word(int0, 0L);
                    long long1 = nbsi0.safe_read_word(int0, 0L);
                    nbsi2._bits[int0] = long0 & long1 & Long.MAX_VALUE;
                }

                return this.intersect(nbsi2._nbsi64, nbsi1._nbsi64, nbsi0._nbsi64);
            } else {
                return true;
            }
        }

        public boolean union(NonBlockingSetInt.NBSI nbsi2, NonBlockingSetInt.NBSI nbsi1, NonBlockingSetInt.NBSI nbsi0) {
            if (!has_bits(nbsi1) && !has_bits(nbsi0)) {
                return true;
            } else {
                if (has_bits(nbsi1) || has_bits(nbsi0)) {
                    for (int int0 = 0; int0 < nbsi2._bits.length; int0++) {
                        long long0 = nbsi1.safe_read_word(int0, 0L);
                        long long1 = nbsi0.safe_read_word(int0, 0L);
                        nbsi2._bits[int0] = (long0 | long1) & Long.MAX_VALUE;
                    }
                }

                return this.union(nbsi2._nbsi64, nbsi1._nbsi64, nbsi0._nbsi64);
            }
        }

        private long safe_read_word(int int0, long long0) {
            if (int0 >= this._bits.length) {
                return long0;
            } else {
                long long1 = this._bits[int0];
                if (long1 < 0L) {
                    long1 = this.help_copy_impl(int0).help_copy()._bits[int0];
                }

                return long1;
            }
        }

        public int sizeInBytes() {
            return this._bits.length;
        }

        public int size() {
            return (int)this._size.get();
        }

        private NonBlockingSetInt.NBSI install_larger_new_bits(int var1) {
            if (this._new == null) {
                int int0 = this._bits.length << 6 << 1;
                this.CAS_new(new NonBlockingSetInt.NBSI(int0, this._size, this._non_blocking_set_int));
            }

            return this;
        }

        private NonBlockingSetInt.NBSI help_copy() {
            NonBlockingSetInt.NBSI nbsi0 = this._non_blocking_set_int._nbsi;
            int int0 = nbsi0._copyIdx.getAndAdd(512);

            for (int int1 = 0; int1 < 8; int1++) {
                int int2 = int0 + int1 * 64;
                int2 %= nbsi0._bits.length << 6;
                nbsi0.help_copy_impl(int2);
                nbsi0.help_copy_impl(int2 + 63);
            }

            if (nbsi0._copyDone.get() == nbsi0._sum_bits_length && this._non_blocking_set_int.CAS_nbsi(nbsi0, nbsi0._new)) {
            }

            return this._new;
        }

        private NonBlockingSetInt.NBSI help_copy_impl(int int1) {
            NonBlockingSetInt.NBSI nbsi0 = this;
            NonBlockingSetInt.NBSI nbsi2 = this._new;
            if (nbsi2 == null) {
                return this;
            } else {
                int int0;
                for (int0 = int1; (int0 & 63) == 63; int0 >>= 6) {
                    nbsi0 = nbsi0._nbsi64;
                    nbsi2 = nbsi2._nbsi64;
                }

                long long0;
                for (long0 = nbsi0._bits[int0 >> 6]; long0 >= 0L; long0 = nbsi0._bits[int0 >> 6]) {
                    long long1 = long0;
                    long0 |= mask(63);
                    if (nbsi0.CAS(int0 >> 6, long1, long0)) {
                        if (long1 == 0L) {
                            this._copyDone.addAndGet(1);
                        }
                        break;
                    }
                }

                if (long0 != mask(63)) {
                    long long2 = nbsi2._bits[int0 >> 6];
                    if (long2 == 0L) {
                        long2 = long0 & ~mask(63);
                        if (!nbsi2.CAS(int0 >> 6, 0L, long2)) {
                            long2 = nbsi2._bits[int0 >> 6];
                        }

                        assert long2 != 0L;
                    }

                    if (nbsi0.CAS(int0 >> 6, long0, mask(63))) {
                        this._copyDone.addAndGet(1);
                    }
                }

                return this;
            }
        }

        private void print(int int1, String string) {
            for (int int0 = 0; int0 < int1; int0++) {
                System.out.print("  ");
            }

            System.out.println(string);
        }

        private void print(int int0) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("NBSI - _bits.len=");

            for (NonBlockingSetInt.NBSI nbsi0 = this; nbsi0 != null; nbsi0 = nbsi0._nbsi64) {
                stringBuffer.append(" " + nbsi0._bits.length);
            }

            this.print(int0, stringBuffer.toString());
            NonBlockingSetInt.NBSI nbsi2 = this;

            while (nbsi2 != null) {
                for (int int1 = 0; int1 < nbsi2._bits.length; int1++) {
                    System.out.print(Long.toHexString(nbsi2._bits[int1]) + " ");
                }

                nbsi2 = nbsi2._nbsi64;
                System.out.println();
            }

            if (this._copyIdx.get() != 0 || this._copyDone.get() != 0) {
                this.print(int0, "_copyIdx=" + this._copyIdx.get() + " _copyDone=" + this._copyDone.get() + " _words_to_cpy=" + this._sum_bits_length);
            }

            if (this._new != null) {
                this.print(int0, "__has_new - ");
                this._new.print(int0 + 1);
            }
        }

        static {
            Field field = null;

            try {
                field = NonBlockingSetInt.NBSI.class.getDeclaredField("_new");
            } catch (NoSuchFieldException noSuchFieldException) {
            }

            _new_offset = NonBlockingSetInt._unsafe.objectFieldOffset(field);
        }
    }

    private class NBSIIntIterator implements IntIterator {
        NonBlockingSetInt.NBSI nbsi;
        int index = -1;
        int prev = -1;

        NBSIIntIterator() {
            this.nbsi = NonBlockingSetInt.this._nbsi;
            this.advance();
        }

        private void advance() {
            do {
                this.index++;

                while (this.index >> 6 >= this.nbsi._bits.length) {
                    if (this.nbsi._new == null) {
                        this.index = -2;
                        return;
                    }

                    this.nbsi = this.nbsi._new;
                }
            } while (!this.nbsi.contains(this.index));
        }

        @Override
        public int next() {
            if (this.index == -1) {
                throw new NoSuchElementException();
            } else {
                this.prev = this.index;
                this.advance();
                return this.prev;
            }
        }

        @Override
        public boolean hasNext() {
            return this.index != -2;
        }

        public void remove() {
            if (this.prev == -1) {
                throw new IllegalStateException();
            } else {
                this.nbsi.remove(this.prev);
                this.prev = -1;
            }
        }
    }

    private class iter implements Iterator<Integer> {
        NonBlockingSetInt.NBSIIntIterator intIterator = NonBlockingSetInt.this.new NBSIIntIterator();

        iter() {
        }

        @Override
        public boolean hasNext() {
            return this.intIterator.hasNext();
        }

        public Integer next() {
            return this.intIterator.next();
        }

        @Override
        public void remove() {
            this.intIterator.remove();
        }
    }
}
