/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.integration;

import java.util.AbstractList;
import se.krka.kahlua.integration.LuaFail;
import se.krka.kahlua.integration.LuaSuccess;

public abstract class LuaReturn
extends AbstractList<Object> {
    protected final Object[] returnValues;

    protected LuaReturn(Object[] objectArray) {
        this.returnValues = objectArray;
    }

    public abstract boolean isSuccess();

    public abstract Object getErrorObject();

    public abstract String getErrorString();

    public abstract String getLuaStackTrace();

    public abstract RuntimeException getJavaException();

    @Override
    public Object getFirst() {
        return this.get(0);
    }

    public Object getSecond() {
        return this.get(1);
    }

    public Object getThird() {
        return this.get(2);
    }

    @Override
    public Object get(int n) {
        int n2 = this.size();
        if (n < 0 || n >= n2) {
            throw new IndexOutOfBoundsException("The index " + n + " is outside the bounds [0, " + n2 + ")");
        }
        return this.returnValues[n + 1];
    }

    @Override
    public int size() {
        return this.returnValues.length - 1;
    }

    public static LuaReturn createReturn(Object[] objectArray) {
        Boolean bl = (Boolean)objectArray[0];
        if (bl.booleanValue()) {
            return new LuaSuccess(objectArray);
        }
        return new LuaFail(objectArray);
    }
}

