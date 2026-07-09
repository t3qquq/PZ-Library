// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration.expose;

import java.util.ArrayList;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.ui.UIManager;

public class MethodArguments {
    private ReturnValues returnValues;
    private Object self;
    private Object[] params;
    private String failure;
    private boolean bValid = true;
    static ArrayList[] Lists = new ArrayList[30];

    public static MethodArguments get(int int0) {
        MethodArguments methodArguments = null;
        if (Lists[int0].isEmpty()) {
            methodArguments = new MethodArguments(int0);
        } else {
            methodArguments = (MethodArguments)Lists[int0].get(0);
            Lists[int0].remove(methodArguments);
        }

        return methodArguments;
    }

    public static void put(MethodArguments methodArguments) {
        if (!Lists[methodArguments.params.length].contains(methodArguments)) {
            Lists[methodArguments.params.length].add(methodArguments);
            methodArguments.bValid = true;
            methodArguments.self = null;
            methodArguments.failure = null;
            methodArguments.returnValues = null;

            for (int int0 = 0; int0 < methodArguments.params.length; int0++) {
                methodArguments.params[int0] = null;
            }
        }
    }

    public MethodArguments(int int0) {
        this.params = new Object[int0];
    }

    public ReturnValues getReturnValues() {
        return this.returnValues;
    }

    public Object getSelf() {
        return this.self;
    }

    public Object[] getParams() {
        return this.params;
    }

    public void fail(String string) {
        this.failure = string;
        this.bValid = false;
    }

    public void setSelf(Object object) {
        this.self = object;
    }

    public String getFailure() {
        return this.failure;
    }

    public void setReturnValues(ReturnValues returnValuesx) {
        this.returnValues = returnValuesx;
    }

    public void assertValid() {
        if (!this.isValid()) {
            if (Core.bDebug && UIManager.defaultthread == LuaManager.thread) {
                UIManager.debugBreakpoint(LuaManager.thread.currentfile, LuaManager.thread.currentLine - 1);
            }

            throw new RuntimeException(this.failure);
        }
    }

    public boolean isValid() {
        return this.bValid;
    }

    static {
        for (int int0 = 0; int0 < 30; int0++) {
            Lists[int0] = new ArrayList(1000);

            for (int int1 = 0; int1 < 1000; int1++) {
                Lists[int0].add(new MethodArguments(int0));
            }
        }
    }
}
