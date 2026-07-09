// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.lambda;

import java.util.Comparator;
import zombie.util.Pool;
import zombie.util.PooledObject;

public final class Comparators {
    public static final class Params1 {
        public static final class CallbackStackItem<E, T1> extends Comparators.Params1.StackItem<T1> implements Comparator<E> {
            private Comparators.Params1.ICallback<E, T1> comparator;
            private static final Pool<Comparators.Params1.CallbackStackItem<Object, Object>> s_pool = new Pool<>(Comparators.Params1.CallbackStackItem::new);

            @Override
            public int compare(E object0, E object1) {
                return this.comparator.compare((E)object0, (E)object1, this.val1);
            }

            public static <E, T1> Comparators.Params1.CallbackStackItem<E, T1> alloc(T1 object, Comparators.Params1.ICallback<E, T1> iCallback) {
                Comparators.Params1.CallbackStackItem callbackStackItem = s_pool.alloc();
                callbackStackItem.val1 = (T1)object;
                callbackStackItem.comparator = iCallback;
                return callbackStackItem;
            }

            @Override
            public void onReleased() {
                this.val1 = null;
                this.comparator = null;
            }
        }

        public interface ICallback<E, T1> {
            int compare(E var1, E var2, T1 var3);
        }

        private static class StackItem<T1> extends PooledObject {
            T1 val1;
        }
    }

    public static final class Params2 {
        public static final class CallbackStackItem<E, T1, T2> extends Comparators.Params2.StackItem<T1, T2> implements Comparator<E> {
            private Comparators.Params2.ICallback<E, T1, T2> comparator;
            private static final Pool<Comparators.Params2.CallbackStackItem<Object, Object, Object>> s_pool = new Pool<>(
                Comparators.Params2.CallbackStackItem::new
            );

            @Override
            public int compare(E object0, E object1) {
                return this.comparator.compare((E)object0, (E)object1, this.val1, this.val2);
            }

            public static <E, T1, T2> Comparators.Params2.CallbackStackItem<E, T1, T2> alloc(
                T1 object0, T2 object1, Comparators.Params2.ICallback<E, T1, T2> iCallback
            ) {
                Comparators.Params2.CallbackStackItem callbackStackItem = s_pool.alloc();
                callbackStackItem.val1 = (T1)object0;
                callbackStackItem.val2 = (T2)object1;
                callbackStackItem.comparator = iCallback;
                return callbackStackItem;
            }

            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.comparator = null;
            }
        }

        public interface ICallback<E, T1, T2> {
            int compare(E var1, E var2, T1 var3, T2 var4);
        }

        private static class StackItem<T1, T2> extends PooledObject {
            T1 val1;
            T2 val2;
        }
    }
}
