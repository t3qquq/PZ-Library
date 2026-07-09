// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.list;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public final class PZConvertArray<S, T> extends AbstractList<T> implements RandomAccess {
    private final S[] m_array;
    private final Function<S, T> m_converterST;
    private final Function<T, S> m_converterTS;

    public PZConvertArray(S[] objects, Function<S, T> function) {
        this((S[])objects, function, null);
    }

    public PZConvertArray(S[] objects, Function<S, T> function0, Function<T, S> function1) {
        this.m_array = (S[])Objects.requireNonNull(objects);
        this.m_converterST = function0;
        this.m_converterTS = function1;
    }

    public boolean isReadonly() {
        return this.m_converterTS == null;
    }

    @Override
    public int size() {
        return this.m_array.length;
    }

    @Override
    public Object[] toArray() {
        return Arrays.asList(this.m_array).toArray();
    }

    @Override
    public <R> R[] toArray(R[] objects) {
        int int0 = this.size();

        for (int int1 = 0; int1 < int0 && int1 < objects.length; int1++) {
            Object object = this.get(int1);
            objects[int1] = object;
        }

        if (objects.length > int0) {
            objects[int0] = null;
        }

        return (R[])objects;
    }

    @Override
    public T get(int int0) {
        return this.convertST(this.m_array[int0]);
    }

    @Override
    public T set(int int0, T object1) {
        Object object0 = this.get(int0);
        this.setS(int0, this.convertTS((T)object1));
        return (T)object0;
    }

    public S setS(int int0, S object1) {
        Object object0 = this.m_array[int0];
        this.m_array[int0] = (S)object1;
        return (S)object0;
    }

    @Override
    public int indexOf(Object object) {
        int int0 = -1;
        int int1 = 0;

        for (int int2 = this.size(); int1 < int2; int1++) {
            if (objectsEqual(object, this.get(int1))) {
                int0 = int1;
                break;
            }
        }

        return int0;
    }

    private static boolean objectsEqual(Object object0, Object object1) {
        return object0 == object1 || object0 != null && object0.equals(object1);
    }

    @Override
    public boolean contains(Object object) {
        return this.indexOf(object) != -1;
    }

    @Override
    public void forEach(Consumer<? super T> consumer) {
        int int0 = 0;

        for (int int1 = this.size(); int0 < int1; int0++) {
            consumer.accept(this.get(int0));
        }
    }

    @Override
    public void replaceAll(UnaryOperator<T> unaryOperator) {
        Objects.requireNonNull(unaryOperator);
        Object[] objects = this.m_array;

        for (int int0 = 0; int0 < objects.length; int0++) {
            Object object0 = this.get(int0);
            Object object1 = unaryOperator.apply(object0);
            objects[int0] = this.convertTS((T)object1);
        }
    }

    @Override
    public void sort(Comparator<? super T> comparator) {
        Arrays.sort(this.m_array, (object1, object0) -> comparator.compare(this.convertST((S)object1), this.convertST((S)object0)));
    }

    private T convertST(S object) {
        return this.m_converterST.apply((S)object);
    }

    private S convertTS(T object) {
        return this.m_converterTS.apply((T)object);
    }
}
