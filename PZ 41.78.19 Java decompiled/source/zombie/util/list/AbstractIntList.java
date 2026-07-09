// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.list;

import zombie.util.AbstractIntCollection;
import zombie.util.IntCollection;
import zombie.util.IntIterator;
import zombie.util.hash.DefaultIntHashFunction;
import zombie.util.util.Exceptions;

public abstract class AbstractIntList extends AbstractIntCollection implements IntList {
    protected AbstractIntList() {
    }

    @Override
    public boolean add(int int0) {
        this.add(this.size(), int0);
        return true;
    }

    @Override
    public void add(int var1, int var2) {
        Exceptions.unsupported("add");
    }

    @Override
    public boolean addAll(int int0, IntCollection intCollection) {
        if (int0 < 0 || int0 > this.size()) {
            Exceptions.indexOutOfBounds(int0, 0, this.size());
        }

        IntIterator intIterator = intCollection.iterator();

        boolean boolean0;
        for (boolean0 = intIterator.hasNext(); intIterator.hasNext(); int0++) {
            this.add(int0, intIterator.next());
        }

        return boolean0;
    }

    @Override
    public int indexOf(int int0) {
        return this.indexOf(0, int0);
    }

    @Override
    public int indexOf(int int0, int int1) {
        IntListIterator intListIterator = this.listIterator(int0);

        while (intListIterator.hasNext()) {
            if (intListIterator.next() == int1) {
                return intListIterator.previousIndex();
            }
        }

        return -1;
    }

    @Override
    public IntIterator iterator() {
        return this.listIterator();
    }

    @Override
    public int lastIndexOf(int int0) {
        IntListIterator intListIterator = this.listIterator(this.size());

        while (intListIterator.hasPrevious()) {
            if (intListIterator.previous() == int0) {
                return intListIterator.nextIndex();
            }
        }

        return -1;
    }

    @Override
    public int lastIndexOf(int int0, int int1) {
        IntListIterator intListIterator = this.listIterator(int0);

        while (intListIterator.hasPrevious()) {
            if (intListIterator.previous() == int1) {
                return intListIterator.nextIndex();
            }
        }

        return -1;
    }

    @Override
    public IntListIterator listIterator() {
        return this.listIterator(0);
    }

    @Override
    public IntListIterator listIterator(final int int0) {
        if (int0 < 0 || int0 > this.size()) {
            Exceptions.indexOutOfBounds(int0, 0, this.size());
        }

        return new IntListIterator() {
            private int ptr = int0;
            private int lptr = -1;

            @Override
            public boolean hasNext() {
                return this.ptr < AbstractIntList.this.size();
            }

            @Override
            public int next() {
                if (this.ptr == AbstractIntList.this.size()) {
                    Exceptions.endOfIterator();
                }

                this.lptr = this.ptr++;
                return AbstractIntList.this.get(this.lptr);
            }

            @Override
            public void remove() {
                if (this.lptr == -1) {
                    Exceptions.noElementToRemove();
                }

                AbstractIntList.this.removeElementAt(this.lptr);
                if (this.lptr < this.ptr) {
                    this.ptr--;
                }

                this.lptr = -1;
            }

            @Override
            public void add(int int0x) {
                AbstractIntList.this.add(this.ptr++, int0x);
                this.lptr = -1;
            }

            @Override
            public boolean hasPrevious() {
                return this.ptr > 0;
            }

            @Override
            public int nextIndex() {
                return this.ptr;
            }

            @Override
            public int previous() {
                if (this.ptr == 0) {
                    Exceptions.startOfIterator();
                }

                this.ptr--;
                this.lptr = this.ptr;
                return AbstractIntList.this.get(this.ptr);
            }

            @Override
            public int previousIndex() {
                return this.ptr - 1;
            }

            @Override
            public void set(int int0x) {
                if (this.lptr == -1) {
                    Exceptions.noElementToSet();
                }

                AbstractIntList.this.set(this.lptr, int0x);
            }
        };
    }

    @Override
    public int removeElementAt(int var1) {
        Exceptions.unsupported("removeElementAt");
        throw new RuntimeException();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof IntList)) {
            return false;
        } else {
            IntListIterator intListIterator0 = this.listIterator();
            IntListIterator intListIterator1 = ((IntList)object).listIterator();

            while (intListIterator0.hasNext() && intListIterator1.hasNext()) {
                if (intListIterator0.next() != intListIterator1.next()) {
                    return false;
                }
            }

            return !intListIterator0.hasNext() && !intListIterator1.hasNext();
        }
    }

    @Override
    public int hashCode() {
        int int0 = 1;
        IntIterator intIterator = this.iterator();

        while (intIterator.hasNext()) {
            int0 = 31 * int0 + DefaultIntHashFunction.INSTANCE.hash(intIterator.next());
        }

        return int0;
    }
}
