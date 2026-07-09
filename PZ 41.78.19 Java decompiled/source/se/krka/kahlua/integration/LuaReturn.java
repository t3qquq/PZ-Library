// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration;

import java.util.AbstractList;

public abstract class LuaReturn extends AbstractList<Object> {
    protected final Object[] returnValues;

    protected LuaReturn(Object[] objects) {
        this.returnValues = objects;
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
    public Object get(int arg0) {
        int int0 = this.size();
        if (arg0 >= 0 && arg0 < int0) {
            return this.returnValues[arg0 + 1];
        } else {
            throw new IndexOutOfBoundsException("The index " + arg0 + " is outside the bounds [0, " + int0 + ")");
        }
    }

    @Override
    public int size() {
        return this.returnValues.length - 1;
    }

    public static LuaReturn createReturn(Object[] objects) {
        Boolean boolean0 = (Boolean)objects[0];
        return (LuaReturn)(boolean0 ? new LuaSuccess(objects) : new LuaFail(objects));
    }
}
