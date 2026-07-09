// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.map;

public interface IntKeyMapIterator<V> {
    boolean hasNext();

    void next();

    void remove();

    int getKey();

    V getValue();
}
