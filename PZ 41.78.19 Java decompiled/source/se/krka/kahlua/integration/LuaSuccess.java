// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration;

public class LuaSuccess extends LuaReturn {
    LuaSuccess(Object[] objects) {
        super(objects);
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public Object getErrorObject() {
        throw new UnsupportedOperationException("Not valid when isSuccess is true");
    }

    @Override
    public String getErrorString() {
        throw new UnsupportedOperationException("Not valid when isSuccess is true");
    }

    @Override
    public String getLuaStackTrace() {
        throw new UnsupportedOperationException("Not valid when isSuccess is true");
    }

    @Override
    public RuntimeException getJavaException() {
        throw new UnsupportedOperationException("Not valid when isSuccess is true");
    }
}
