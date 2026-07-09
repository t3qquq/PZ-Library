// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.Collections;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;

public class NonBlockingHashSet<E> extends AbstractSet<E> implements Serializable {
    private static final Object V = "";
    private final NonBlockingHashMap<E, Object> _map = new NonBlockingHashMap<>();

    @Override
    public boolean add(E object) {
        return this._map.putIfAbsent((E)object, V) != V;
    }

    @Override
    public boolean contains(Object object) {
        return this._map.containsKey(object);
    }

    @Override
    public boolean remove(Object object) {
        return this._map.remove(object) == V;
    }

    @Override
    public int size() {
        return this._map.size();
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public Iterator<E> iterator() {
        return this._map.keySet().iterator();
    }

    public void readOnly() {
        throw new RuntimeException("Unimplemented");
    }
}
