// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.list;

import zombie.util.IntIterator;

public interface IntListIterator extends IntIterator {
    void add(int var1);

    boolean hasPrevious();

    int nextIndex();

    int previous();

    int previousIndex();

    void set(int var1);
}
