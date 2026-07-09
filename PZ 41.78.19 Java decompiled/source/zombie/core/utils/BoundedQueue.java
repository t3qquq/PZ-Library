// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.utils;

import java.util.NoSuchElementException;

public class BoundedQueue<E> {
    private int numElements;
    private int front;
    private int rear;
    private E[] elements;

    public BoundedQueue(int _numElements) {
        this.numElements = _numElements;
        int int0 = Math.max(_numElements, 16);
        int0 = Integer.highestOneBit(int0 - 1) << 1;
        this.elements = (E[])(new Object[int0]);
    }

    public void add(E object) {
        if (object == null) {
            throw new NullPointerException();
        } else {
            if (this.size() == this.numElements) {
                this.removeFirst();
            }

            this.elements[this.rear] = (E)object;
            this.rear = this.rear + 1 & this.elements.length - 1;
        }
    }

    public E removeFirst() {
        Object object = this.elements[this.front];
        if (object == null) {
            throw new NoSuchElementException();
        } else {
            this.elements[this.front] = null;
            this.front = this.front + 1 & this.elements.length - 1;
            return (E)object;
        }
    }

    public E remove(int int1) {
        int int0 = this.front + int1 & this.elements.length - 1;
        Object object = this.elements[int0];
        if (object == null) {
            throw new NoSuchElementException();
        } else {
            int int2 = int0;

            while (int2 != this.front) {
                int int3 = int2 - 1 & this.elements.length - 1;
                this.elements[int2] = this.elements[int3];
                int2 = int3;
            }

            this.front = this.front + 1 & this.elements.length - 1;
            this.elements[int2] = null;
            return (E)object;
        }
    }

    public E get(int int1) {
        int int0 = this.front + int1 & this.elements.length - 1;
        Object object = this.elements[int0];
        if (object == null) {
            throw new NoSuchElementException();
        } else {
            return (E)object;
        }
    }

    public void clear() {
        while (this.front != this.rear) {
            this.elements[this.front] = null;
            this.front = this.front + 1 & this.elements.length - 1;
        }

        this.front = this.rear = 0;
    }

    public int capacity() {
        return this.numElements;
    }

    public int size() {
        return this.front <= this.rear ? this.rear - this.front : this.rear + this.elements.length - this.front;
    }

    public boolean isEmpty() {
        return this.front == this.rear;
    }

    public boolean isFull() {
        return this.size() == this.capacity();
    }
}
