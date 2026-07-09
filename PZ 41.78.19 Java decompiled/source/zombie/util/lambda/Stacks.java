// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.lambda;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import zombie.util.IPooledObject;
import zombie.util.Lambda;
import zombie.util.Pool;
import zombie.util.PooledObject;

public final class Stacks {
    public abstract static class GenericStack extends PooledObject {
        private final List<IPooledObject> m_stackItems = new ArrayList<>();

        public abstract void invoke();

        public void invokeAndRelease() {
            try {
                this.invoke();
            } finally {
                this.release();
            }
        }

        private <E> E push(E object) {
            this.m_stackItems.add((IPooledObject)object);
            return (E)object;
        }

        @Override
        public void onReleased() {
            this.m_stackItems.forEach(Pool::tryRelease);
            this.m_stackItems.clear();
        }

        public <E, T1> Predicate<E> predicate(T1 object, Predicates.Params1.ICallback<E, T1> iCallback) {
            return this.push(Lambda.predicate(object, iCallback));
        }

        public <E, T1, T2> Predicate<E> predicate(T1 object0, T2 object1, Predicates.Params2.ICallback<E, T1, T2> iCallback) {
            return this.push(Lambda.predicate(object0, object1, iCallback));
        }

        public <E, T1, T2, T3> Predicate<E> predicate(T1 object0, T2 object1, T3 object2, Predicates.Params3.ICallback<E, T1, T2, T3> iCallback) {
            return this.push(Lambda.predicate(object0, object1, object2, iCallback));
        }

        public <E, T1> Comparator<E> comparator(T1 object, Comparators.Params1.ICallback<E, T1> iCallback) {
            return this.push(Lambda.comparator(object, iCallback));
        }

        public <E, T1, T2> Comparator<E> comparator(T1 object0, T2 object1, Comparators.Params2.ICallback<E, T1, T2> iCallback) {
            return this.push(Lambda.comparator(object0, object1, iCallback));
        }

        public <E, T1> Consumer<E> consumer(T1 object, Consumers.Params1.ICallback<E, T1> iCallback) {
            return this.push(Lambda.consumer(object, iCallback));
        }

        public <E, T1, T2> Consumer<E> consumer(T1 object0, T2 object1, Consumers.Params2.ICallback<E, T1, T2> iCallback) {
            return this.push(Lambda.consumer(object0, object1, iCallback));
        }

        public <T1> Runnable invoker(T1 object, Invokers.Params1.ICallback<T1> iCallback) {
            return this.push(Lambda.invoker(object, iCallback));
        }

        public <T1, T2> Runnable invoker(T1 object0, T2 object1, Invokers.Params2.ICallback<T1, T2> iCallback) {
            return this.push(Lambda.invoker(object0, object1, iCallback));
        }

        public <T1, T2, T3> Runnable invoker(T1 object0, T2 object1, T3 object2, Invokers.Params3.ICallback<T1, T2, T3> iCallback) {
            return this.push(Lambda.invoker(object0, object1, object2, iCallback));
        }

        public <T1, T2, T3, T4> Runnable invoker(T1 object0, T2 object1, T3 object2, T4 object3, Invokers.Params4.ICallback<T1, T2, T3, T4> iCallback) {
            return this.push(Lambda.invoker(object0, object1, object2, object3, iCallback));
        }
    }

    public static final class Params1 {
        public static final class CallbackStackItem<T1> extends Stacks.Params1.StackItem<T1> {
            private Stacks.Params1.ICallback<T1> callback;
            private static final Pool<Stacks.Params1.CallbackStackItem<Object>> s_pool = new Pool<>(Stacks.Params1.CallbackStackItem::new);

            @Override
            public void invoke() {
                this.callback.accept(this, this.val1);
            }

            public static <T1> Stacks.Params1.CallbackStackItem<T1> alloc(T1 object, Stacks.Params1.ICallback<T1> iCallback) {
                Stacks.Params1.CallbackStackItem callbackStackItem = s_pool.alloc();
                callbackStackItem.val1 = (T1)object;
                callbackStackItem.callback = iCallback;
                return callbackStackItem;
            }

            @Override
            public void onReleased() {
                this.val1 = null;
                this.callback = null;
                super.onReleased();
            }
        }

        public interface ICallback<T1> {
            void accept(Stacks.GenericStack var1, T1 var2);
        }

        private abstract static class StackItem<T1> extends Stacks.GenericStack {
            T1 val1;
        }
    }

    public static final class Params2 {
        public static final class CallbackStackItem<T1, T2> extends Stacks.Params2.StackItem<T1, T2> {
            private Stacks.Params2.ICallback<T1, T2> callback;
            private static final Pool<Stacks.Params2.CallbackStackItem<Object, Object>> s_pool = new Pool<>(Stacks.Params2.CallbackStackItem::new);

            @Override
            public void invoke() {
                this.callback.accept(this, this.val1, this.val2);
            }

            public static <T1, T2> Stacks.Params2.CallbackStackItem<T1, T2> alloc(T1 object0, T2 object1, Stacks.Params2.ICallback<T1, T2> iCallback) {
                Stacks.Params2.CallbackStackItem callbackStackItem = s_pool.alloc();
                callbackStackItem.val1 = (T1)object0;
                callbackStackItem.val2 = (T2)object1;
                callbackStackItem.callback = iCallback;
                return callbackStackItem;
            }

            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.callback = null;
                super.onReleased();
            }
        }

        public interface ICallback<T1, T2> {
            void accept(Stacks.GenericStack var1, T1 var2, T2 var3);
        }

        private abstract static class StackItem<T1, T2> extends Stacks.GenericStack {
            T1 val1;
            T2 val2;
        }
    }

    public static final class Params3 {
        public static final class CallbackStackItem<T1, T2, T3> extends Stacks.Params3.StackItem<T1, T2, T3> {
            private Stacks.Params3.ICallback<T1, T2, T3> callback;
            private static final Pool<Stacks.Params3.CallbackStackItem<Object, Object, Object>> s_pool = new Pool<>(Stacks.Params3.CallbackStackItem::new);

            @Override
            public void invoke() {
                this.callback.accept(this, this.val1, this.val2, this.val3);
            }

            public static <T1, T2, T3> Stacks.Params3.CallbackStackItem<T1, T2, T3> alloc(
                T1 object0, T2 object1, T3 object2, Stacks.Params3.ICallback<T1, T2, T3> iCallback
            ) {
                Stacks.Params3.CallbackStackItem callbackStackItem = s_pool.alloc();
                callbackStackItem.val1 = (T1)object0;
                callbackStackItem.val2 = (T2)object1;
                callbackStackItem.val3 = (T3)object2;
                callbackStackItem.callback = iCallback;
                return callbackStackItem;
            }

            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.val3 = null;
                this.callback = null;
                super.onReleased();
            }
        }

        public interface ICallback<T1, T2, T3> {
            void accept(Stacks.GenericStack var1, T1 var2, T2 var3, T3 var4);
        }

        private abstract static class StackItem<T1, T2, T3> extends Stacks.GenericStack {
            T1 val1;
            T2 val2;
            T3 val3;
        }
    }

    public static final class Params4 {
        public static final class CallbackStackItem<T1, T2, T3, T4> extends Stacks.Params4.StackItem<T1, T2, T3, T4> {
            private Stacks.Params4.ICallback<T1, T2, T3, T4> callback;
            private static final Pool<Stacks.Params4.CallbackStackItem<Object, Object, Object, Object>> s_pool = new Pool<>(
                Stacks.Params4.CallbackStackItem::new
            );

            @Override
            public void invoke() {
                this.callback.accept(this, this.val1, this.val2, this.val3, this.val4);
            }

            public static <T1, T2, T3, T4> Stacks.Params4.CallbackStackItem<T1, T2, T3, T4> alloc(
                T1 object0, T2 object1, T3 object2, T4 object3, Stacks.Params4.ICallback<T1, T2, T3, T4> iCallback
            ) {
                Stacks.Params4.CallbackStackItem callbackStackItem = s_pool.alloc();
                callbackStackItem.val1 = (T1)object0;
                callbackStackItem.val2 = (T2)object1;
                callbackStackItem.val3 = (T3)object2;
                callbackStackItem.val4 = (T4)object3;
                callbackStackItem.callback = iCallback;
                return callbackStackItem;
            }

            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.val3 = null;
                this.val4 = null;
                this.callback = null;
                super.onReleased();
            }
        }

        public interface ICallback<T1, T2, T3, T4> {
            void accept(Stacks.GenericStack var1, T1 var2, T2 var3, T3 var4, T4 var5);
        }

        private abstract static class StackItem<T1, T2, T3, T4> extends Stacks.GenericStack {
            T1 val1;
            T2 val2;
            T3 val3;
            T4 val4;
        }
    }

    public static final class Params5 {
        public static final class CallbackStackItem<T1, T2, T3, T4, T5> extends Stacks.Params5.StackItem<T1, T2, T3, T4, T5> {
            private Stacks.Params5.ICallback<T1, T2, T3, T4, T5> callback;
            private static final Pool<Stacks.Params5.CallbackStackItem<Object, Object, Object, Object, Object>> s_pool = new Pool<>(
                Stacks.Params5.CallbackStackItem::new
            );

            @Override
            public void invoke() {
                this.callback.accept(this, this.val1, this.val2, this.val3, this.val4, this.val5);
            }

            public static <T1, T2, T3, T4, T5> Stacks.Params5.CallbackStackItem<T1, T2, T3, T4, T5> alloc(
                T1 object0, T2 object1, T3 object2, T4 object3, T5 object4, Stacks.Params5.ICallback<T1, T2, T3, T4, T5> iCallback
            ) {
                Stacks.Params5.CallbackStackItem callbackStackItem = s_pool.alloc();
                callbackStackItem.val1 = (T1)object0;
                callbackStackItem.val2 = (T2)object1;
                callbackStackItem.val3 = (T3)object2;
                callbackStackItem.val4 = (T4)object3;
                callbackStackItem.val5 = (T5)object4;
                callbackStackItem.callback = iCallback;
                return callbackStackItem;
            }

            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.val3 = null;
                this.val4 = null;
                this.val5 = null;
                this.callback = null;
                super.onReleased();
            }
        }

        public interface ICallback<T1, T2, T3, T4, T5> {
            void accept(Stacks.GenericStack var1, T1 var2, T2 var3, T3 var4, T4 var5, T5 var6);
        }

        private abstract static class StackItem<T1, T2, T3, T4, T5> extends Stacks.GenericStack {
            T1 val1;
            T2 val2;
            T3 val3;
            T4 val4;
            T5 val5;
        }
    }

    public static final class Params6 {
        public static final class CallbackStackItem<T1, T2, T3, T4, T5, T6> extends Stacks.Params6.StackItem<T1, T2, T3, T4, T5, T6> {
            private Stacks.Params6.ICallback<T1, T2, T3, T4, T5, T6> callback;
            private static final Pool<Stacks.Params6.CallbackStackItem<Object, Object, Object, Object, Object, Object>> s_pool = new Pool<>(
                Stacks.Params6.CallbackStackItem::new
            );

            @Override
            public void invoke() {
                this.callback.accept(this, this.val1, this.val2, this.val3, this.val4, this.val5, this.val6);
            }

            public static <T1, T2, T3, T4, T5, T6> Stacks.Params6.CallbackStackItem<T1, T2, T3, T4, T5, T6> alloc(
                T1 object0, T2 object1, T3 object2, T4 object3, T5 object4, T6 object5, Stacks.Params6.ICallback<T1, T2, T3, T4, T5, T6> iCallback
            ) {
                Stacks.Params6.CallbackStackItem callbackStackItem = s_pool.alloc();
                callbackStackItem.val1 = (T1)object0;
                callbackStackItem.val2 = (T2)object1;
                callbackStackItem.val3 = (T3)object2;
                callbackStackItem.val4 = (T4)object3;
                callbackStackItem.val5 = (T5)object4;
                callbackStackItem.val6 = (T6)object5;
                callbackStackItem.callback = iCallback;
                return callbackStackItem;
            }

            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.val3 = null;
                this.val4 = null;
                this.val5 = null;
                this.val6 = null;
                this.callback = null;
                super.onReleased();
            }
        }

        public interface ICallback<T1, T2, T3, T4, T5, T6> {
            void accept(Stacks.GenericStack var1, T1 var2, T2 var3, T3 var4, T4 var5, T5 var6, T6 var7);
        }

        private abstract static class StackItem<T1, T2, T3, T4, T5, T6> extends Stacks.GenericStack {
            T1 val1;
            T2 val2;
            T3 val3;
            T4 val4;
            T5 val5;
            T6 val6;
        }
    }
}
