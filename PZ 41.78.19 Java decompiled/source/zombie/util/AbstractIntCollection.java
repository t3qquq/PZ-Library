// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

import zombie.util.util.Display;
import zombie.util.util.Exceptions;

public abstract class AbstractIntCollection implements IntCollection {
    protected AbstractIntCollection() {
    }

    @Override
    public boolean add(int var1) {
        Exceptions.unsupported("add");
        return false;
    }

    @Override
    public boolean addAll(IntCollection intCollection) {
        IntIterator intIterator = intCollection.iterator();
        boolean boolean0 = false;

        while (intIterator.hasNext()) {
            boolean0 |= this.add(intIterator.next());
        }

        return boolean0;
    }

    @Override
    public void clear() {
        IntIterator intIterator = this.iterator();

        while (intIterator.hasNext()) {
            intIterator.next();
            intIterator.remove();
        }
    }

    @Override
    public boolean contains(int int0) {
        IntIterator intIterator = this.iterator();

        while (intIterator.hasNext()) {
            if (intIterator.next() == int0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean containsAll(IntCollection intCollection) {
        IntIterator intIterator = intCollection.iterator();

        while (intIterator.hasNext()) {
            if (!this.contains(intIterator.next())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public boolean remove(int int0) {
        IntIterator intIterator = this.iterator();
        boolean boolean0 = false;

        while (intIterator.hasNext()) {
            if (intIterator.next() == int0) {
                intIterator.remove();
                boolean0 = true;
                break;
            }
        }

        return boolean0;
    }

    @Override
    public boolean removeAll(IntCollection intCollection) {
        if (intCollection == null) {
            Exceptions.nullArgument("collection");
        }

        IntIterator intIterator = this.iterator();
        boolean boolean0 = false;

        while (intIterator.hasNext()) {
            if (intCollection.contains(intIterator.next())) {
                intIterator.remove();
                boolean0 = true;
            }
        }

        return boolean0;
    }

    @Override
    public boolean retainAll(IntCollection intCollection) {
        if (intCollection == null) {
            Exceptions.nullArgument("collection");
        }

        IntIterator intIterator = this.iterator();
        boolean boolean0 = false;

        while (intIterator.hasNext()) {
            if (!intCollection.contains(intIterator.next())) {
                intIterator.remove();
                boolean0 = true;
            }
        }

        return boolean0;
    }

    @Override
    public int size() {
        IntIterator intIterator = this.iterator();

        int int0;
        for (int0 = 0; intIterator.hasNext(); int0++) {
            intIterator.next();
        }

        return int0;
    }

    @Override
    public int[] toArray() {
        return this.toArray(null);
    }

    @Override
    public int[] toArray(int[] ints) {
        int int0 = this.size();
        if (ints == null || ints.length < int0) {
            ints = new int[int0];
        }

        IntIterator intIterator = this.iterator();

        for (int int1 = 0; intIterator.hasNext(); int1++) {
            ints[int1] = intIterator.next();
        }

        return ints;
    }

    @Override
    public void trimToSize() {
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[');

        for (IntIterator intIterator = this.iterator(); intIterator.hasNext(); stringBuilder.append(Display.display(intIterator.next()))) {
            if (stringBuilder.length() > 1) {
                stringBuilder.append(',');
            }
        }

        stringBuilder.append(']');
        return stringBuilder.toString();
    }
}
