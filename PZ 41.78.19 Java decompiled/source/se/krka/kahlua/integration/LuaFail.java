// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration;

import se.krka.kahlua.vm.KahluaUtil;

public class LuaFail extends LuaReturn {
    LuaFail(Object[] objects) {
        super(objects);
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public Object getErrorObject() {
        return this.returnValues.length >= 2 ? this.returnValues[1] : null;
    }

    @Override
    public String getErrorString() {
        return this.returnValues.length >= 2 && this.returnValues[1] != null ? KahluaUtil.rawTostring(this.returnValues[1]) : "";
    }

    @Override
    public String getLuaStackTrace() {
        return this.returnValues.length >= 3 && this.returnValues[2] instanceof String ? (String)this.returnValues[2] : "";
    }

    @Override
    public RuntimeException getJavaException() {
        return this.returnValues.length >= 4 && this.returnValues[3] instanceof RuntimeException ? (RuntimeException)this.returnValues[3] : null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public String toString() {
        return this.getErrorString() + "\n" + this.getLuaStackTrace();
    }
}
