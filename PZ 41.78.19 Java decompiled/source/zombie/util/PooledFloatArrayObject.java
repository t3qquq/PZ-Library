// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

import zombie.util.list.PZArrayUtil;

public final class PooledFloatArrayObject extends PooledObject {
    private static final Pool<PooledFloatArrayObject> s_pool = new Pool<>(PooledFloatArrayObject::new);
    private float[] m_array = PZArrayUtil.emptyFloatArray;

    public static PooledFloatArrayObject alloc(int count) {
        PooledFloatArrayObject pooledFloatArrayObject = s_pool.alloc();
        pooledFloatArrayObject.initCapacity(count);
        return pooledFloatArrayObject;
    }

    public static PooledFloatArrayObject toArray(PooledFloatArrayObject source) {
        if (source == null) {
            return null;
        } else {
            int int0 = source.length();
            PooledFloatArrayObject pooledFloatArrayObject = alloc(int0);
            if (int0 > 0) {
                System.arraycopy(source.array(), 0, pooledFloatArrayObject.array(), 0, int0);
            }

            return pooledFloatArrayObject;
        }
    }

    private void initCapacity(int int0) {
        if (this.m_array.length != int0) {
            this.m_array = new float[int0];
        }
    }

    public float[] array() {
        return this.m_array;
    }

    public float get(int idx) {
        return this.m_array[idx];
    }

    public void set(int idx, float val) {
        this.m_array[idx] = val;
    }

    public int length() {
        return this.m_array.length;
    }
}
