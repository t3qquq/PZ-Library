// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import zombie.util.lambda.Comparators;
import zombie.util.lambda.Consumers;
import zombie.util.lambda.IntSupplierFunction;
import zombie.util.lambda.Invokers;
import zombie.util.lambda.Predicates;
import zombie.util.lambda.ReturnValueContainer;
import zombie.util.lambda.ReturnValueContainerPrimitives;
import zombie.util.lambda.Stacks;

public final class Lambda {
    public static <E, T1> Predicate<E> predicate(T1 object, Predicates.Params1.ICallback<E, T1> iCallback) {
        return Predicates.Params1.CallbackStackItem.alloc(object, iCallback);
    }

    public static <E, T1, T2> Predicate<E> predicate(T1 object0, T2 object1, Predicates.Params2.ICallback<E, T1, T2> iCallback) {
        return Predicates.Params2.CallbackStackItem.alloc(object0, object1, iCallback);
    }

    public static <E, T1, T2, T3> Predicate<E> predicate(T1 object0, T2 object1, T3 object2, Predicates.Params3.ICallback<E, T1, T2, T3> iCallback) {
        return Predicates.Params3.CallbackStackItem.alloc(object0, object1, object2, iCallback);
    }

    public static <E, T1> Comparator<E> comparator(T1 object, Comparators.Params1.ICallback<E, T1> iCallback) {
        return Comparators.Params1.CallbackStackItem.alloc(object, iCallback);
    }

    public static <E, T1, T2> Comparator<E> comparator(T1 object0, T2 object1, Comparators.Params2.ICallback<E, T1, T2> iCallback) {
        return Comparators.Params2.CallbackStackItem.alloc(object0, object1, iCallback);
    }

    public static <E, T1> Consumer<E> consumer(T1 object, Consumers.Params1.ICallback<E, T1> iCallback) {
        return Consumers.Params1.CallbackStackItem.alloc(object, iCallback);
    }

    public static <E, T1, T2> Consumer<E> consumer(T1 object0, T2 object1, Consumers.Params2.ICallback<E, T1, T2> iCallback) {
        return Consumers.Params2.CallbackStackItem.alloc(object0, object1, iCallback);
    }

    public static <E, T1, T2, T3> Consumer<E> consumer(T1 object0, T2 object1, T3 object2, Consumers.Params3.ICallback<E, T1, T2, T3> iCallback) {
        return Consumers.Params3.CallbackStackItem.alloc(object0, object1, object2, iCallback);
    }

    public static <E, T1, T2, T3, T4> Consumer<E> consumer(
        T1 object0, T2 object1, T3 object2, T4 object3, Consumers.Params4.ICallback<E, T1, T2, T3, T4> iCallback
    ) {
        return Consumers.Params4.CallbackStackItem.alloc(object0, object1, object2, object3, iCallback);
    }

    public static <E, T1, T2, T3, T4, T5> Consumer<E> consumer(
        T1 object0, T2 object1, T3 object2, T4 object3, T5 object4, Consumers.Params5.ICallback<E, T1, T2, T3, T4, T5> iCallback
    ) {
        return Consumers.Params5.CallbackStackItem.alloc(object0, object1, object2, object3, object4, iCallback);
    }

    public static <T1> Runnable invoker(T1 object, Invokers.Params1.ICallback<T1> iCallback) {
        return Invokers.Params1.CallbackStackItem.alloc(object, iCallback);
    }

    public static <T1, T2> Runnable invoker(T1 object0, T2 object1, Invokers.Params2.ICallback<T1, T2> iCallback) {
        return Invokers.Params2.CallbackStackItem.alloc(object0, object1, iCallback);
    }

    public static <T1, T2, T3> Runnable invoker(T1 object0, T2 object1, T3 object2, Invokers.Params3.ICallback<T1, T2, T3> iCallback) {
        return Invokers.Params3.CallbackStackItem.alloc(object0, object1, object2, iCallback);
    }

    public static <T1, T2, T3, T4> Runnable invoker(T1 object0, T2 object1, T3 object2, T4 object3, Invokers.Params4.ICallback<T1, T2, T3, T4> iCallback) {
        return Invokers.Params4.CallbackStackItem.alloc(object0, object1, object2, object3, iCallback);
    }

    public static <T1> void capture(T1 object, Stacks.Params1.ICallback<T1> iCallback) {
        Stacks.Params1.CallbackStackItem callbackStackItem = Stacks.Params1.CallbackStackItem.alloc(object, iCallback);
        callbackStackItem.invokeAndRelease();
    }

    public static <T1, T2> void capture(T1 object0, T2 object1, Stacks.Params2.ICallback<T1, T2> iCallback) {
        Stacks.Params2.CallbackStackItem callbackStackItem = Stacks.Params2.CallbackStackItem.alloc(object0, object1, iCallback);
        callbackStackItem.invokeAndRelease();
    }

    public static <T1, T2, T3> void capture(T1 object0, T2 object1, T3 object2, Stacks.Params3.ICallback<T1, T2, T3> iCallback) {
        Stacks.Params3.CallbackStackItem callbackStackItem = Stacks.Params3.CallbackStackItem.alloc(object0, object1, object2, iCallback);
        callbackStackItem.invokeAndRelease();
    }

    public static <T1, T2, T3, T4> void capture(T1 object0, T2 object1, T3 object2, T4 object3, Stacks.Params4.ICallback<T1, T2, T3, T4> iCallback) {
        Stacks.Params4.CallbackStackItem callbackStackItem = Stacks.Params4.CallbackStackItem.alloc(object0, object1, object2, object3, iCallback);
        callbackStackItem.invokeAndRelease();
    }

    public static <T1, T2, T3, T4, T5> void capture(
        T1 object0, T2 object1, T3 object2, T4 object3, T5 object4, Stacks.Params5.ICallback<T1, T2, T3, T4, T5> iCallback
    ) {
        Stacks.Params5.CallbackStackItem callbackStackItem = Stacks.Params5.CallbackStackItem.alloc(object0, object1, object2, object3, object4, iCallback);
        callbackStackItem.invokeAndRelease();
    }

    public static <T1, T2, T3, T4, T5, T6> void capture(
        T1 object0, T2 object1, T3 object2, T4 object3, T5 object4, T6 object5, Stacks.Params6.ICallback<T1, T2, T3, T4, T5, T6> iCallback
    ) {
        Stacks.Params6.CallbackStackItem callbackStackItem = Stacks.Params6.CallbackStackItem.alloc(
            object0, object1, object2, object3, object4, object5, iCallback
        );
        callbackStackItem.invokeAndRelease();
    }

    public static <E, T1> void forEach(Consumer<Consumer<E>> consumer, T1 object, Consumers.Params1.ICallback<E, T1> iCallback) {
        capture(consumer, object, iCallback, (genericStack, consumerx, objectx, iCallbackx) -> consumerx.accept(genericStack.consumer(objectx, iCallbackx)));
    }

    public static <E, T1, T2> void forEach(Consumer<Consumer<E>> consumer, T1 object0, T2 object1, Consumers.Params2.ICallback<E, T1, T2> iCallback) {
        capture(
            consumer,
            object0,
            object1,
            iCallback,
            (genericStack, consumerx, object0x, object1x, iCallbackx) -> consumerx.accept(genericStack.consumer(object0x, object1x, iCallbackx))
        );
    }

    public static <E, T1> void forEachFrom(BiConsumer<List<E>, Consumer<E>> biConsumer, List<E> list, T1 object, Consumers.Params1.ICallback<E, T1> iCallback) {
        capture(
            biConsumer,
            list,
            object,
            iCallback,
            (genericStack, biConsumerx, listx, objectx, iCallbackx) -> biConsumerx.accept(listx, genericStack.consumer(objectx, iCallbackx))
        );
    }

    public static <E, T1, T2> void forEachFrom(
        BiConsumer<List<E>, Consumer<E>> biConsumer, List<E> list, T1 object0, T2 object1, Consumers.Params2.ICallback<E, T1, T2> iCallback
    ) {
        capture(
            biConsumer,
            list,
            object0,
            object1,
            iCallback,
            (genericStack, biConsumerx, listx, object0x, object1x, iCallbackx) -> biConsumerx.accept(
                listx, genericStack.consumer(object0x, object1x, iCallbackx)
            )
        );
    }

    public static <E, F, T1> void forEachFrom(BiConsumer<F, Consumer<E>> biConsumer, F object0, T1 object1, Consumers.Params1.ICallback<E, T1> iCallback) {
        capture(
            biConsumer,
            object0,
            object1,
            iCallback,
            (genericStack, biConsumerx, object0x, object1x, iCallbackx) -> biConsumerx.accept(object0x, genericStack.consumer(object1x, iCallbackx))
        );
    }

    public static <E, F, T1, T2> void forEachFrom(
        BiConsumer<F, Consumer<E>> biConsumer, F object0, T1 object1, T2 object2, Consumers.Params2.ICallback<E, T1, T2> iCallback
    ) {
        capture(
            biConsumer,
            object0,
            object1,
            object2,
            iCallback,
            (genericStack, biConsumerx, object0x, object1x, object2x, iCallbackx) -> biConsumerx.accept(
                object0x, genericStack.consumer(object1x, object2x, iCallbackx)
            )
        );
    }

    public static <E, T1, R> R find(Function<Predicate<E>, R> function, T1 object0, Predicates.Params1.ICallback<E, T1> iCallback) {
        ReturnValueContainer returnValueContainer = ReturnValueContainer.alloc();
        capture(
            function,
            object0,
            iCallback,
            returnValueContainer,
            (genericStack, functionx, object, iCallbackx, returnValueContainerx) -> returnValueContainerx.ReturnVal = (T)functionx.apply(
                genericStack.predicate(object, iCallbackx)
            )
        );
        Object object1 = returnValueContainer.ReturnVal;
        returnValueContainer.release();
        return (R)object1;
    }

    public static <E, T1> int indexOf(IntSupplierFunction<Predicate<E>> intSupplierFunction, T1 object, Predicates.Params1.ICallback<E, T1> iCallback) {
        ReturnValueContainerPrimitives.RVInt rVInt = ReturnValueContainerPrimitives.RVInt.alloc();
        capture(
            intSupplierFunction,
            object,
            iCallback,
            rVInt,
            (genericStack, intSupplierFunctionx, objectx, iCallbackx, rVIntx) -> rVIntx.ReturnVal = intSupplierFunctionx.getInt(
                genericStack.predicate(objectx, iCallbackx)
            )
        );
        int int0 = rVInt.ReturnVal;
        rVInt.release();
        return int0;
    }

    public static <E, T1> boolean contains(Predicate<Predicate<E>> predicate, T1 object, Predicates.Params1.ICallback<E, T1> iCallback) {
        ReturnValueContainerPrimitives.RVBoolean rVBoolean = ReturnValueContainerPrimitives.RVBoolean.alloc();
        capture(
            predicate,
            object,
            iCallback,
            rVBoolean,
            (genericStack, predicatex, objectx, iCallbackx, rVBooleanx) -> rVBooleanx.ReturnVal = predicatex.test(genericStack.predicate(objectx, iCallbackx))
        );
        Boolean boolean0 = rVBoolean.ReturnVal;
        rVBoolean.release();
        return boolean0;
    }

    public static <E, F extends Iterable<E>, T1> boolean containsFrom(
        BiPredicate<F, Predicate<E>> biPredicate, F iterable, T1 object, Predicates.Params1.ICallback<E, T1> iCallback
    ) {
        ReturnValueContainerPrimitives.RVBoolean rVBoolean = ReturnValueContainerPrimitives.RVBoolean.alloc();
        capture(
            biPredicate,
            iterable,
            object,
            iCallback,
            rVBoolean,
            (genericStack, biPredicatex, iterablex, objectx, iCallbackx, rVBooleanx) -> rVBooleanx.ReturnVal = biPredicatex.test(
                iterablex, genericStack.predicate(objectx, iCallbackx)
            )
        );
        Boolean boolean0 = rVBoolean.ReturnVal;
        rVBoolean.release();
        return boolean0;
    }

    public static <T1> void invoke(Consumer<Runnable> consumer, T1 object, Invokers.Params1.ICallback<T1> iCallback) {
        capture(consumer, object, iCallback, (genericStack, consumerx, objectx, iCallbackx) -> consumerx.accept(genericStack.invoker(objectx, iCallbackx)));
    }

    public static <T1, T2> void invoke(Consumer<Runnable> consumer, T1 object0, T2 object1, Invokers.Params2.ICallback<T1, T2> iCallback) {
        capture(
            consumer,
            object0,
            object1,
            iCallback,
            (genericStack, consumerx, object0x, object1x, iCallbackx) -> consumerx.accept(genericStack.invoker(object0x, object1x, iCallbackx))
        );
    }
}
