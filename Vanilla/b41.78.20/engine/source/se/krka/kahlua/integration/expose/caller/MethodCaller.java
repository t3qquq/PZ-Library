/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zombie.core.logger.ExceptionLogger
 *  zombie.ui.UIManager
 */
package se.krka.kahlua.integration.expose.caller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import se.krka.kahlua.integration.expose.ReturnValues;
import se.krka.kahlua.integration.expose.caller.AbstractCaller;
import se.krka.kahlua.integration.processor.DescriptorUtil;
import zombie.core.logger.ExceptionLogger;
import zombie.ui.UIManager;

public class MethodCaller
extends AbstractCaller {
    private final Method method;
    private final Object owner;
    private final boolean hasSelf;
    private final boolean hasReturnValue;

    public MethodCaller(Method method, Object object, boolean bl) {
        super(method.getParameterTypes(), method.isVarArgs());
        this.method = method;
        this.owner = object;
        this.hasSelf = bl;
        method.setAccessible(true);
        boolean bl2 = this.hasReturnValue = !method.getReturnType().equals(Void.TYPE);
        if (this.hasReturnValue && this.needsMultipleReturnValues()) {
            throw new IllegalArgumentException("Must have a void return type if first argument is a ReturnValues: got: " + method.getReturnType());
        }
    }

    @Override
    public void call(Object object, ReturnValues returnValues, Object[] objectArray) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (!this.hasSelf) {
            object = this.owner;
        }
        try {
            Object object2 = this.method.invoke(object, objectArray);
            if (this.hasReturnValue) {
                returnValues.push(object2);
            }
        }
        catch (Exception exception) {
            UIManager.defaultthread.doStacktraceProper();
            UIManager.defaultthread.debugException(exception);
            ExceptionLogger.logException((Throwable)exception);
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

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        MethodCaller methodCaller = (MethodCaller)object;
        if (!this.method.equals(methodCaller.method)) {
            return false;
        }
        return !(this.owner != null ? !this.owner.equals(methodCaller.owner) : methodCaller.owner != null);
    }

    public int hashCode() {
        int n = this.method.hashCode();
        n = 31 * n + (this.owner != null ? this.owner.hashCode() : 0);
        return n;
    }
}

