// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.lambda;

import zombie.util.Pool;
import zombie.util.PooledObject;

public class Invokers {
    public static final class Params1 {
        public static final class CallbackStackItem<T1> extends Invokers.Params1.StackItem<T1> implements Runnable {
            private Invokers.Params1.ICallback<T1> invoker;
            private static final Pool<Invokers.Params1.CallbackStackItem<Object>> s_pool = new Pool<>(Invokers.Params1.CallbackStackItem::new);

            @Override
            public void run() {
                this.invoker.accept(this.val1);
            }

            public static <T1> Invokers.Params1.CallbackStackItem<T1> alloc(T1 object, Invokers.Params1.ICallback<T1> iCallback) {
                Invokers.Params1.CallbackStackItem callbackStackItem = s_pool.alloc();
                callbackStackItem.val1 = (T1)object;
                callbackStackItem.invoker = iCallback;
                return callbackStackItem;
            }

            @Override
            public void onReleased() {
                this.val1 = null;
                this.invoker = null;
            }
        }

        public interface ICallback<T1> {
            void accept(T1 var1);
        }

        private static class StackItem<T1> extends PooledObject {
            T1 val1;
        }
    }

    public static final class Params2 {
        public static final class CallbackStackItem<T1, T2> extends Invokers.Params2.StackItem<T1, T2> implements Runnable {
            private Invokers.Params2.ICallback<T1, T2> invoker;
            private static final Pool<Invokers.Params2.CallbackStackItem<Object, Object>> s_pool = new Pool<>(Invokers.Params2.CallbackStackItem::new);

            @Override
            public void run() {
                this.invoker.accept(this.val1, this.val2);
            }

            public static <T1, T2> Invokers.Params2.CallbackStackItem<T1, T2> alloc(T1 object0, T2 object1, Invokers.Params2.ICallback<T1, T2> iCallback) {
                Invokers.Params2.CallbackStackItem callbackStackItem = s_pool.alloc();
                callbackStackItem.val1 = (T1)object0;
                callbackStackItem.val2 = (T2)object1;
                callbackStackItem.invoker = iCallback;
                return callbackStackItem;
            }

            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.invoker = null;
            }
        }

        public interface ICallback<T1, T2> {
            void accept(T1 var1, T2 var2);
        }

        private static class StackItem<T1, T2> extends PooledObject {
            T1 val1;
            T2 val2;
        }
    }

    public static final class Params3 {
        public static final class CallbackStackItem<T1, T2, T3> extends Invokers.Params3.StackItem<T1, T2, T3> implements Runnable {
            private Invokers.Params3.ICallback<T1, T2, T3> invoker;
            private static final Pool<Invokers.Params3.CallbackStackItem<Object, Object, Object>> s_pool = new Pool<>(Invokers.Params3.CallbackStackItem::new);

            @Override
            public void run() {
                this.invoker.accept(this.val1, this.val2, this.val3);
            }

            public static <T1, T2, T3> Invokers.Params3.CallbackStackItem<T1, T2, T3> alloc(
                T1 object0, T2 object1, T3 object2, Invokers.Params3.ICallback<T1, T2, T3> iCallback
            ) {
                Invokers.Params3.CallbackStackItem callbackStackItem = s_pool.alloc();
                callbackStackItem.val1 = (T1)object0;
                callbackStackItem.val2 = (T2)object1;
                callbackStackItem.val3 = (T3)object2;
                callbackStackItem.invoker = iCallback;
                return callbackStackItem;
            }

            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.val3 = null;
                this.invoker = null;
            }
        }

        public interface ICallback<T1, T2, T3> {
            void accept(T1 var1, T2 var2, T3 var3);
        }

        private static class StackItem<T1, T2, T3> extends PooledObject {
            T1 val1;
            T2 val2;
            T3 val3;
        }
    }

    public static final class Params4 {
        public static final class CallbackStackItem<T1, T2, T3, T4> extends Invokers.Params4.StackItem<T1, T2, T3, T4> implements Runnable {
            private Invokers.Params4.ICallback<T1, T2, T3, T4> invoker;
            private static final Pool<Invokers.Params4.CallbackStackItem<Object, Object, Object, Object>> s_pool = new Pool<>(
                Invokers.Params4.CallbackStackItem::new
            );

            @Override
            public void run() {
                this.invoker.accept(this.val1, this.val2, this.val3, this.val4);
            }

            public static <T1, T2, T3, T4> Invokers.Params4.CallbackStackItem<T1, T2, T3, T4> alloc(
                T1 object0, T2 object1, T3 object2, T4 object3, Invokers.Params4.ICallback<T1, T2, T3, T4> iCallback
            ) {
                Invokers.Params4.CallbackStackItem callbackStackItem = s_pool.alloc();
                callbackStackItem.val1 = (T1)object0;
                callbackStackItem.val2 = (T2)object1;
                callbackStackItem.val3 = (T3)object2;
                callbackStackItem.val4 = (T4)object3;
                callbackStackItem.invoker = iCallback;
                return callbackStackItem;
            }

            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.val3 = null;
                this.val4 = null;
                this.invoker = null;
            }
        }

        public interface ICallback<T1, T2, T3, T4> {
            void accept(T1 var1, T2 var2, T3 var3, T4 var4);
        }

        private static class StackItem<T1, T2, T3, T4> extends PooledObject {
            T1 val1;
            T2 val2;
            T3 val3;
            T4 val4;
        }
    }
}
