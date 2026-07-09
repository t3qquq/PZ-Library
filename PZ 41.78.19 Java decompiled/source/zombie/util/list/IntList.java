// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.list;

import zombie.util.IntCollection;

public interface IntList extends IntCollection {
    void add(int var1, int var2);

    boolean addAll(int var1, IntCollection var2);

    int get(int var1);

    int indexOf(int var1);

    int indexOf(int var1, int var2);

    int lastIndexOf(int var1);

    int lastIndexOf(int var1, int var2);

    IntListIterator listIterator();

    IntListIterator listIterator(int var1);

    int removeElementAt(int var1);

    int set(int var1, int var2);
}
