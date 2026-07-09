// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.list;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class PrimitiveFloatList extends AbstractList<Float> implements RandomAccess {
    private final float[] m_array;

    public PrimitiveFloatList(float[] floats) {
        this.m_array = Objects.requireNonNull(floats);
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
    public <T> T[] toArray(T[] objects) {
        int int0 = this.size();

        for (int int1 = 0; int1 < int0 && int1 < objects.length; int1++) {
            Float float0 = this.m_array[int1];
            objects[int1] = float0;
        }

        if (objects.length > int0) {
            objects[int0] = null;
        }

        return (T[])objects;
    }

    public Float get(int int0) {
        return this.m_array[int0];
    }

    public Float set(int int0, Float float0) {
        return this.set(int0, float0.floatValue());
    }

    public float set(int int0, float float1) {
        float float0 = this.m_array[int0];
        this.m_array[int0] = float1;
        return float0;
    }

    @Override
    public int indexOf(Object object) {
        if (object == null) {
            return -1;
        } else {
            return object instanceof Number ? this.indexOf(((Number)object).floatValue()) : -1;
        }
    }

    public int indexOf(float float0) {
        int int0 = -1;
        int int1 = 0;

        for (int int2 = this.size(); int1 < int2; int1++) {
            if (this.m_array[int1] == float0) {
                int0 = int1;
                break;
            }
        }

        return int0;
    }

    @Override
    public boolean contains(Object object) {
        return this.indexOf(object) != -1;
    }

    public boolean contains(float float0) {
        return this.indexOf(float0) != -1;
    }

    @Override
    public void forEach(Consumer<? super Float> consumer) {
        this.forEach(consumer::accept);
    }

    public void forEach(FloatConsumer floatConsumer) {
        int int0 = 0;

        for (int int1 = this.size(); int0 < int1; int0++) {
            floatConsumer.accept(this.m_array[int0]);
        }
    }

    @Override
    public void replaceAll(UnaryOperator<Float> unaryOperator) {
        Objects.requireNonNull(unaryOperator);
        float[] floats = this.m_array;

        for (int int0 = 0; int0 < floats.length; int0++) {
            floats[int0] = unaryOperator.apply(floats[int0]);
        }
    }

    @Override
    public void sort(Comparator<? super Float> var1) {
        this.sort();
    }

    public void sort() {
        Arrays.sort(this.m_array);
    }
}
