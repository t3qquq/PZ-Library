// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.list;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

public final class PZArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess {
    private E[] elements;
    private int numElements;
    private static final PZArrayList<Object> instance = new PZArrayList<>(Object.class, 0);

    public PZArrayList(Class<E> elementType, int initialCapacity) {
        this.elements = (E[])((Object[])Array.newInstance(elementType, initialCapacity));
    }

    @Override
    public E get(int int0) {
        if (int0 >= 0 && int0 < this.numElements) {
            return this.elements[int0];
        } else {
            throw new IndexOutOfBoundsException("Index: " + int0 + " Size: " + this.numElements);
        }
    }

    @Override
    public int size() {
        return this.numElements;
    }

    @Override
    public int indexOf(Object o) {
        for (int int0 = 0; int0 < this.numElements; int0++) {
            if (o == null && this.elements[int0] == null || o != null && o.equals(this.elements[int0])) {
                return int0;
            }
        }

        return -1;
    }

    @Override
    public boolean isEmpty() {
        return this.numElements == 0;
    }

    @Override
    public boolean contains(Object o) {
        return this.indexOf(o) >= 0;
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(E object) {
        if (this.numElements == this.elements.length) {
            int int0 = this.elements.length + (this.elements.length >> 1);
            if (int0 < this.numElements + 1) {
                int0 = this.numElements + 1;
            }

            this.elements = Arrays.copyOf(this.elements, int0);
        }

        this.elements[this.numElements] = (E)object;
        this.numElements++;
        return true;
    }

    @Override
    public void add(int int0, E object) {
        if (int0 >= 0 && int0 <= this.numElements) {
            if (this.numElements == this.elements.length) {
                int int1 = this.elements.length + this.elements.length >> 1;
                if (int1 < this.numElements + 1) {
                    int1 = this.numElements + 1;
                }

                this.elements = Arrays.copyOf(this.elements, int1);
            }

            System.arraycopy(this.elements, int0, this.elements, int0 + 1, this.numElements - int0);
            this.elements[int0] = (E)object;
            this.numElements++;
        } else {
            throw new IndexOutOfBoundsException("Index: " + int0 + " Size: " + this.numElements);
        }
    }

    @Override
    public E remove(int int0) {
        if (int0 >= 0 && int0 < this.numElements) {
            Object object = this.elements[int0];
            int int1 = this.numElements - int0 - 1;
            if (int1 > 0) {
                System.arraycopy(this.elements, int0 + 1, this.elements, int0, int1);
            }

            this.elements[this.numElements - 1] = null;
            this.numElements--;
            return (E)object;
        } else {
            throw new IndexOutOfBoundsException("Index: " + int0 + " Size: " + this.numElements);
        }
    }

    @Override
    public boolean remove(Object o) {
        for (int int0 = 0; int0 < this.numElements; int0++) {
            if (o == null && this.elements[int0] == null || o != null && o.equals(this.elements[int0])) {
                int int1 = this.numElements - int0 - 1;
                if (int1 > 0) {
                    System.arraycopy(this.elements, int0 + 1, this.elements, int0, int1);
                }

                this.elements[this.numElements - 1] = null;
                this.numElements--;
                return true;
            }
        }

        return false;
    }

    @Override
    public E set(int int0, E object1) {
        if (int0 >= 0 && int0 < this.numElements) {
            Object object0 = this.elements[int0];
            this.elements[int0] = (E)object1;
            return (E)object0;
        } else {
            throw new IndexOutOfBoundsException("Index: " + int0 + " Size: " + this.numElements);
        }
    }

    @Override
    public void clear() {
        for (int int0 = 0; int0 < this.numElements; int0++) {
            this.elements[int0] = null;
        }

        this.numElements = 0;
    }

    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "[]";
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('[');

            for (int int0 = 0; int0 < this.numElements; int0++) {
                Object object = this.elements[int0];
                stringBuilder.append(object == this ? "(self)" : object.toString());
                if (int0 == this.numElements - 1) {
                    break;
                }

                stringBuilder.append(',');
                stringBuilder.append(' ');
            }

            return stringBuilder.append(']').toString();
        }
    }

    public E[] getElements() {
        return this.elements;
    }

    public static <E> AbstractList<E> emptyList() {
        return (AbstractList<E>)instance;
    }
}
