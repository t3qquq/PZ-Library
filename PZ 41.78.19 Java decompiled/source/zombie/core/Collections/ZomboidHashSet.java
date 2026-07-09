// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.Collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class ZomboidHashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, Serializable {
    static final long serialVersionUID = -5024744406713321676L;
    private transient ZomboidHashMap<E, Object> map;
    private static final Object PRESENT = new Object();

    public ZomboidHashSet() {
        this.map = new ZomboidHashMap<>();
    }

    public ZomboidHashSet(Collection<? extends E> collection) {
        this.map = new ZomboidHashMap<>(Math.max((int)(collection.size() / 0.75F) + 1, 16));
        this.addAll(collection);
    }

    public ZomboidHashSet(int int0, float var2) {
        this.map = new ZomboidHashMap<>(int0);
    }

    public ZomboidHashSet(int int0) {
        this.map = new ZomboidHashMap<>(int0);
    }

    @Override
    public Iterator<E> iterator() {
        return this.map.keySet().iterator();
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean contains(Object object) {
        return this.map.containsKey(object);
    }

    @Override
    public boolean add(E object) {
        return this.map.put((E)object, PRESENT) == null;
    }

    @Override
    public boolean remove(Object object) {
        return this.map.remove(object) == PRESENT;
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Object clone() {
        try {
            ZomboidHashSet zomboidHashSet0 = (ZomboidHashSet)super.clone();
            zomboidHashSet0.map = (ZomboidHashMap<E, Object>)this.map.clone();
            return zomboidHashSet0;
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new InternalError();
        }
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeInt(this.map.size());
        Iterator iterator = this.map.keySet().iterator();

        while (iterator.hasNext()) {
            objectOutputStream.writeObject(iterator.next());
        }
    }

    private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
    }
}
