// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration.expose.caller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import se.krka.kahlua.integration.expose.ReturnValues;
import se.krka.kahlua.integration.processor.DescriptorUtil;
import zombie.core.logger.ExceptionLogger;
import zombie.ui.UIManager;

public class MethodCaller extends AbstractCaller {
    private final Method method;
    private final Object owner;
    private final boolean hasSelf;
    private final boolean hasReturnValue;

    public MethodCaller(Method methodx, Object object, boolean boolean0) {
        super(methodx.getParameterTypes(), methodx.isVarArgs());
        this.method = methodx;
        this.owner = object;
        this.hasSelf = boolean0;
        methodx.setAccessible(true);
        this.hasReturnValue = !methodx.getReturnType().equals(void.class);
        if (this.hasReturnValue && this.needsMultipleReturnValues()) {
            throw new IllegalArgumentException("Must have a void return type if first argument is a ReturnValues: got: " + methodx.getReturnType());
        }
    }

    @Override
    public void call(Object object0, ReturnValues returnValues, Object[] objects) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (!this.hasSelf) {
            object0 = this.owner;
        }

        try {
            Object object1 = this.method.invoke(object0, objects);
            if (this.hasReturnValue) {
                returnValues.push(object1);
            }
        } catch (Exception exception) {
            UIManager.defaultthread.doStacktraceProper();
            UIManager.defaultthread.debugException(exception);
            ExceptionLogger.logException(exception);
        }
    }

    @Override
    public boolean hasSelf() {
        return this.hasSelf;
    }

    @Override
    public String getDescriptor() {
        return DescriptorUtil.getDescriptor(this.method);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            MethodCaller methodCaller1 = (MethodCaller)object;
            if (!this.method.equals(methodCaller1.method)) {
                return false;
            } else {
                return this.owner != null ? this.owner.equals(methodCaller1.owner) : methodCaller1.owner == null;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int int0 = this.method.hashCode();
        return 31 * int0 + (this.owner != null ? this.owner.hashCode() : 0);
    }
}
