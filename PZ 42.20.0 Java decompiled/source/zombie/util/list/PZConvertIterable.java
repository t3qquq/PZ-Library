// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.util.list;

import java.util.Iterator;
import java.util.function.Function;

public final class PZConvertIterable<T, S> implements Iterable<T> {
    private final Iterable<S> srcIterable;
    private final Function<S, T> converter;

    public PZConvertIterable(Iterable<S> srcIterable, Function<S, T> converter) {
        this.srcIterable = srcIterable;
        this.converter = converter;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private final Iterator<S> srcIterator = PZConvertIterable.this.srcIterable.iterator();

            @Override
            public boolean hasNext() {
                return this.srcIterator.hasNext();
            }

            @Override
            public T next() {
                return PZConvertIterable.this.converter.apply(this.srcIterator.next());
            }
        };
    }
}
