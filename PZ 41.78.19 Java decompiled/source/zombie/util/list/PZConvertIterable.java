// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.list;

import java.util.Iterator;
import java.util.function.Function;

public final class PZConvertIterable<T, S> implements Iterable<T> {
    private final Iterable<S> m_srcIterable;
    private final Function<S, T> m_converter;

    public PZConvertIterable(Iterable<S> iterable, Function<S, T> function) {
        this.m_srcIterable = iterable;
        this.m_converter = function;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Iterator<S> m_srcIterator = PZConvertIterable.this.m_srcIterable.iterator();

            @Override
            public boolean hasNext() {
                return this.m_srcIterator.hasNext();
            }

            @Override
            public T next() {
                return PZConvertIterable.this.m_converter.apply(this.m_srcIterator.next());
            }
        };
    }
}
