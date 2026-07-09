// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.animation;

import zombie.util.Pool;

public final class TwistableBoneTransform extends BoneTransform {
    public float BlendWeight = 0.0F;
    public float Twist = 0.0F;
    private static final Pool<TwistableBoneTransform> s_pool = new Pool<>(TwistableBoneTransform::new);

    protected TwistableBoneTransform() {
    }

    public static TwistableBoneTransform alloc() {
        return s_pool.alloc();
    }
}
