// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import java.util.List;
import zombie.util.Pool;
import zombie.util.PooledArrayObject;
import zombie.util.list.PZArrayUtil;

public class PooledAnimBoneWeightArray extends PooledArrayObject<AnimBoneWeight> {
    private static final PooledAnimBoneWeightArray s_empty = new PooledAnimBoneWeightArray();
    private static final Pool<PooledAnimBoneWeightArray> s_pool = new Pool<>(PooledAnimBoneWeightArray::new);

    public static PooledAnimBoneWeightArray alloc(int int0) {
        if (int0 == 0) {
            return s_empty;
        } else {
            PooledAnimBoneWeightArray pooledAnimBoneWeightArray = s_pool.alloc();
            pooledAnimBoneWeightArray.initCapacity(int0, int0x -> new AnimBoneWeight[int0x]);
            return pooledAnimBoneWeightArray;
        }
    }

    public static PooledAnimBoneWeightArray toArray(List<AnimBoneWeight> list) {
        if (list == null) {
            return null;
        } else {
            PooledAnimBoneWeightArray pooledAnimBoneWeightArray = alloc(list.size());
            PZArrayUtil.arrayCopy(pooledAnimBoneWeightArray.array(), list);
            return pooledAnimBoneWeightArray;
        }
    }

    public static PooledAnimBoneWeightArray toArray(PooledArrayObject<AnimBoneWeight> pooledArrayObject) {
        if (pooledArrayObject == null) {
            return null;
        } else {
            PooledAnimBoneWeightArray pooledAnimBoneWeightArray = alloc(pooledArrayObject.length());
            PZArrayUtil.arrayCopy(pooledAnimBoneWeightArray.array(), (AnimBoneWeight[])pooledArrayObject.array());
            return pooledAnimBoneWeightArray;
        }
    }
}
