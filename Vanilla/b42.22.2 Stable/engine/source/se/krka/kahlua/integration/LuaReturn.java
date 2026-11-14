// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.integration;

import java.util.AbstractList;

public abstract class LuaReturn extends AbstractList<Object> {
    protected final Object[] returnValues;

    protected LuaReturn(Object[] returnValues) {
        this.returnValues = returnValues;
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
    public Object get(int index) {
        int n = this.size();
        if (index >= 0 && index < n) {
            return this.returnValues[index + 1];
        } else {
            throw new IndexOutOfBoundsException("The index " + index + " is outside the bounds [0, " + n + ")");
        }
    }

    @Override
    public int size() {
        return this.returnValues.length - 1;
    }

    public static LuaReturn createReturn(Object[] returnValues) {
        Boolean success = (Boolean)returnValues[0];
        return success ? new LuaSuccess(returnValues) : new LuaFail(returnValues);
    }
}
