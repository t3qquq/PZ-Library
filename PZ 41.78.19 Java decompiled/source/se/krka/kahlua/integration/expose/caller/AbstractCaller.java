// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration.expose.caller;

import se.krka.kahlua.integration.expose.ReturnValues;

public abstract class AbstractCaller implements Caller {
    protected final Class<?>[] parameters;
    protected final boolean needsMultipleReturnValues;
    protected final Class<?> varargType;

    protected AbstractCaller(Class<?>[] clazzs, boolean boolean1) {
        boolean boolean0 = false;
        if (clazzs.length > 0) {
            Class clazz0 = clazzs[0];
            if (clazz0 == ReturnValues.class) {
                boolean0 = true;
            }
        }

        if (boolean1) {
            Class clazz1 = clazzs[clazzs.length - 1];
            this.varargType = clazz1.getComponentType();
        } else {
            this.varargType = null;
        }

        this.needsMultipleReturnValues = boolean0;
        int int0 = boolean0 ? 1 : 0;
        int int1 = clazzs.length - (this.varargType == null ? 0 : 1);
        int int2 = int1 - int0;
        this.parameters = new Class[int2];
        System.arraycopy(clazzs, int0, this.parameters, 0, int2);
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
