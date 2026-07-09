// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.popman;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ObjectPool<T> {
    private final ObjectPool.Allocator<T> allocator;
    private final ArrayList<T> pool = new ArrayList<T>() {
        @Override
        public boolean contains(Object object) {
            for (int int0 = 0; int0 < ObjectPool.this.pool.size(); int0++) {
                if (ObjectPool.this.pool.get(int0) == object) {
                    return true;
                }
            }

            return false;
        }
    };

    public ObjectPool() {
        this(null);
    }

    public ObjectPool(ObjectPool.Allocator<T> alloc) {
        this.allocator = alloc;
    }

    public T alloc() {
        return this.pool.isEmpty() ? this.makeObject() : this.pool.remove(this.pool.size() - 1);
    }

    public void release(T object) {
        assert object != null;

        assert !this.pool.contains(object);

        this.pool.add((T)object);
    }

    public void release(List<T> objs) {
        for (int int0 = 0; int0 < objs.size(); int0++) {
            if (objs.get(int0) != null) {
                this.release((T)objs.get(int0));
            }
        }
    }

    public void release(Iterable<T> objs) {
        for (Object object : objs) {
            if (object != null) {
                this.release((T)object);
            }
        }
    }

    public void release(T[] objects) {
        if (objects != null) {
            for (int int0 = 0; int0 < objects.length; int0++) {
                if (objects[int0] != null) {
                    this.release((T)objects[int0]);
                }
            }
        }
    }

    public void releaseAll(List<T> objs) {
        for (int int0 = 0; int0 < objs.size(); int0++) {
            if (objs.get(int0) != null) {
                this.release((T)objs.get(int0));
            }
        }
    }

    public void clear() {
        this.pool.clear();
    }

    protected T makeObject() {
        if (this.allocator != null) {
            return this.allocator.allocate();
        } else {
            throw new UnsupportedOperationException(
                "Allocator is null. The ObjectPool is intended to be used with an allocator, or with the function makeObject overridden in a subclass."
            );
        }
    }

    public void forEach(Consumer<T> consumer) {
        for (int int0 = 0; int0 < this.pool.size(); int0++) {
            consumer.accept(this.pool.get(int0));
        }
    }

    public interface Allocator<T> {
        T allocate();
    }
}
