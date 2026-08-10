// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.list;

import java.util.Iterator;

public final class PZEmptyIterable<T> implements Iterable<T> {
    private static final PZEmptyIterable<Object> instance = new PZEmptyIterable<>();
    private final Iterator<T> s_it = new Iterator<T>() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public T next() {
            throw new ArrayIndexOutOfBoundsException("Empty Iterator. Has no data.");
        }
    };

    private PZEmptyIterable() {
    }

    public static <E> PZEmptyIterable<E> getInstance() {
        return (PZEmptyIterable<E>)instance;
    }

    @Override
    public Iterator<T> iterator() {
        return this.s_it;
    }
}
