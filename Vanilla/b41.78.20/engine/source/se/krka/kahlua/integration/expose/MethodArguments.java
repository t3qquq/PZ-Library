/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zombie.Lua.LuaManager
 *  zombie.core.Core
 *  zombie.ui.UIManager
 */
package se.krka.kahlua.integration.expose;

import java.util.ArrayList;
import se.krka.kahlua.integration.expose.ReturnValues;
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

    public static MethodArguments get(int n) {
        MethodArguments methodArguments = null;
        if (Lists[n].isEmpty()) {
            methodArguments = new MethodArguments(n);
        } else {
            methodArguments = (MethodArguments)Lists[n].get(0);
            Lists[n].remove(methodArguments);
        }
        return methodArguments;
    }

    public static void put(MethodArguments methodArguments) {
        if (Lists[methodArguments.params.length].contains(methodArguments)) {
            return;
        }
        Lists[methodArguments.params.length].add(methodArguments);
        methodArguments.bValid = true;
        methodArguments.self = null;
        methodArguments.failure = null;
        methodArguments.returnValues = null;
        for (int i = 0; i < methodArguments.params.length; ++i) {
            methodArguments.params[i] = null;
        }
    }

    public MethodArguments(int n) {
        this.params = new Object[n];
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

    public void setReturnValues(ReturnValues returnValues) {
        this.returnValues = returnValues;
    }

    public void assertValid() {
        if (!this.isValid()) {
            if (Core.bDebug && UIManager.defaultthread == LuaManager.thread) {
                UIManager.debugBreakpoint((String)LuaManager.thread.currentfile, (long)(LuaManager.thread.currentLine - 1));
            }
            throw new RuntimeException(this.failure);
        }
    }

    public boolean isValid() {
        return this.bValid;
    }

    static {
        for (int i = 0; i < 30; ++i) {
            MethodArguments.Lists[i] = new ArrayList(1000);
            for (int j = 0; j < 1000; ++j) {
                Lists[i].add(new MethodArguments(i));
            }
        }
    }
}

