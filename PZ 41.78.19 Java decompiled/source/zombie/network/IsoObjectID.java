// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import zombie.core.Rand;

public class IsoObjectID<T> implements Iterable<T> {
    public static final short incorrect = -1;
    private final ConcurrentHashMap<Short, T> IDToObjectMap = new ConcurrentHashMap<>();
    private final String objectType;
    private short nextID = (short)Rand.Next(32766);

    public IsoObjectID(Class<T> clazz) {
        this.objectType = clazz.getSimpleName();
    }

    public void put(short short0, T object) {
        if (short0 != -1) {
            this.IDToObjectMap.put(short0, (T)object);
        }
    }

    public void remove(short short0) {
        this.IDToObjectMap.remove(short0);
    }

    public void remove(T object) {
        this.IDToObjectMap.values().remove(object);
    }

    public T get(short short0) {
        return this.IDToObjectMap.get(short0);
    }

    public int size() {
        return this.IDToObjectMap.size();
    }

    public void clear() {
        this.IDToObjectMap.clear();
    }

    public short allocateID() {
        this.nextID++;
        if (this.nextID == -1) {
            this.nextID++;
        }

        return this.nextID;
    }

    @Override
    public Iterator<T> iterator() {
        return this.IDToObjectMap.values().iterator();
    }

    public void getObjects(Collection<T> collection) {
        collection.addAll(this.IDToObjectMap.values());
    }
}
