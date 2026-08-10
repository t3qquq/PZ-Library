// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

public interface IntCollection {
    boolean add(int var1);

    boolean addAll(IntCollection var1);

    void clear();

    boolean contains(int var1);

    boolean containsAll(IntCollection var1);

    @Override
    boolean equals(Object var1);

    @Override
    int hashCode();

    boolean isEmpty();

    IntIterator iterator();

    boolean remove(int var1);

    boolean removeAll(IntCollection var1);

    boolean retainAll(IntCollection var1);

    int size();

    int[] toArray();

    int[] toArray(int[] var1);

    void trimToSize();
}
