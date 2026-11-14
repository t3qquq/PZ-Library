// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.integration.expose.caller;

import java.lang.reflect.InvocationTargetException;
import se.krka.kahlua.integration.expose.ReturnValues;

public interface Caller {
    void call(Object arg0, ReturnValues arg1, Object[] arg2) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException;

    Class<?>[] getParameterTypes();

    boolean needsMultipleReturnValues();

    boolean hasSelf();

    Class<?> getVarargType();

    boolean hasVararg();

    String getDescriptor();
}
