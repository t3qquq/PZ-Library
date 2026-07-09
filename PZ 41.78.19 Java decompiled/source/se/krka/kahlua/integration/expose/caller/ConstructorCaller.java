// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration.expose.caller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import se.krka.kahlua.integration.expose.ReturnValues;
import se.krka.kahlua.integration.processor.DescriptorUtil;

public class ConstructorCaller extends AbstractCaller {
    private final Constructor<?> constructor;

    public ConstructorCaller(Constructor<?> constructorx) {
        super(constructorx.getParameterTypes(), constructorx.isVarArgs());
        this.constructor = constructorx;
        constructorx.setAccessible(true);
        if (this.needsMultipleReturnValues()) {
            throw new RuntimeException("Constructor can not return multiple values");
        }
    }

    @Override
    public void call(Object var1, ReturnValues returnValues, Object[] objects) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        returnValues.push(this.constructor.newInstance(objects));
    }

    @Override
    public boolean hasSelf() {
        return false;
    }

    @Override
    public String getDescriptor() {
        return DescriptorUtil.getDescriptor(this.constructor);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            ConstructorCaller constructorCaller1 = (ConstructorCaller)object;
            return this.constructor.equals(constructorCaller1.constructor);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.constructor.hashCode();
    }
}
