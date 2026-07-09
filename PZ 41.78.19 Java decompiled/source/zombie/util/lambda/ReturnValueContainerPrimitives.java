// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.lambda;

import zombie.util.Pool;
import zombie.util.PooledObject;

public final class ReturnValueContainerPrimitives {
    public static final class RVBoolean extends PooledObject {
        public boolean ReturnVal;
        private static final Pool<ReturnValueContainerPrimitives.RVBoolean> s_pool = new Pool<>(ReturnValueContainerPrimitives.RVBoolean::new);

        @Override
        public void onReleased() {
            this.ReturnVal = false;
        }

        public static ReturnValueContainerPrimitives.RVBoolean alloc() {
            return s_pool.alloc();
        }
    }

    public static final class RVFloat extends PooledObject {
        public float ReturnVal;
        private static final Pool<ReturnValueContainerPrimitives.RVFloat> s_pool = new Pool<>(ReturnValueContainerPrimitives.RVFloat::new);

        @Override
        public void onReleased() {
            this.ReturnVal = 0.0F;
        }

        public static ReturnValueContainerPrimitives.RVFloat alloc() {
            return s_pool.alloc();
        }
    }

    public static final class RVInt extends PooledObject {
        public int ReturnVal;
        private static final Pool<ReturnValueContainerPrimitives.RVInt> s_pool = new Pool<>(ReturnValueContainerPrimitives.RVInt::new);

        @Override
        public void onReleased() {
            this.ReturnVal = 0;
        }

        public static ReturnValueContainerPrimitives.RVInt alloc() {
            return s_pool.alloc();
        }
    }
}
