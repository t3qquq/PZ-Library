/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.integration.expose.caller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import se.krka.kahlua.integration.expose.ReturnValues;
import se.krka.kahlua.integration.expose.caller.AbstractCaller;
import se.krka.kahlua.integration.processor.DescriptorUtil;

public class ConstructorCaller
extends AbstractCaller {
    private final Constructor<?> constructor;

    public ConstructorCaller(Constructor<?> constructor) {
        super(constructor.getParameterTypes(), constructor.isVarArgs());
        this.constructor = constructor;
        constructor.setAccessible(true);
        if (this.needsMultipleReturnValues()) {
            throw new RuntimeException("Constructor can not return multiple values");
        }
    }

    @Override
    public void call(Object object, ReturnValues returnValues, Object[] objectArray) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        returnValues.push(this.constructor.newInstance(objectArray));
    }

    @Override
    public boolean hasSelf() {
        return false;
    }

    @Override
    public String getDescriptor() {
        return DescriptorUtil.getDescriptor(this.constructor);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        ConstructorCaller constructorCaller = (ConstructorCaller)object;
        return this.constructor.equals(constructorCaller.constructor);
    }

    public int hashCode() {
        return this.constructor.hashCode();
    }
}

