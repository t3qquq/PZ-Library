/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.integration.expose.caller;

import se.krka.kahlua.integration.expose.ReturnValues;
import se.krka.kahlua.integration.expose.caller.Caller;

public abstract class AbstractCaller
implements Caller {
    protected final Class<?>[] parameters;
    protected final boolean needsMultipleReturnValues;
    protected final Class<?> varargType;

    protected AbstractCaller(Class<?>[] classArray, boolean bl) {
        Class<?> clazz;
        boolean bl2 = false;
        if (classArray.length > 0 && (clazz = classArray[0]) == ReturnValues.class) {
            bl2 = true;
        }
        if (bl) {
            clazz = classArray[classArray.length - 1];
            this.varargType = clazz.getComponentType();
        } else {
            this.varargType = null;
        }
        this.needsMultipleReturnValues = bl2;
        int n = bl2 ? 1 : 0;
        int n2 = classArray.length - (this.varargType == null ? 0 : 1);
        int n3 = n2 - n;
        this.parameters = new Class[n3];
        System.arraycopy(classArray, n, this.parameters, 0, n3);
    }

    @Override
    public final Class<?>[] getParameterTypes() {
        return this.parameters;
    }

    @Override
    public final Class<?> getVarargType() {
        return this.varargType;
    }

    @Override
    public final boolean hasVararg() {
        return this.varargType != null;
    }

    @Override
    public final boolean needsMultipleReturnValues() {
        return this.needsMultipleReturnValues;
    }
}

