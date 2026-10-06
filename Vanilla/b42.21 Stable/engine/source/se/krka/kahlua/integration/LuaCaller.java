/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.integration;

import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.LuaReturn;
import se.krka.kahlua.vm.KahluaThread;

public class LuaCaller {
    private final KahluaConverterManager converterManager;

    public LuaCaller(KahluaConverterManager converterManager) {
        this.converterManager = converterManager;
    }

    public void pcallvoid(KahluaThread thread, Object functionObject, Object arg) {
        thread.pcallvoid(functionObject, arg);
    }

    public void pcallvoid(KahluaThread thread, Object functionObject, Object arg, Object arg2) {
        thread.pcallvoid(functionObject, arg, arg2);
    }

    public void pcallvoid(KahluaThread thread, Object functionObject, Object arg, Object arg2, Object arg3) {
        thread.pcallvoid(functionObject, arg, arg2, arg3);
    }

    public Boolean pcallBoolean(KahluaThread thread, Object functionObject, Object arg, Object arg2) {
        return thread.pcallBoolean(functionObject, arg, arg2);
    }

    public Boolean pcallBoolean(KahluaThread thread, Object functionObject, Object arg, Object arg2, Object arg3) {
        return thread.pcallBoolean(functionObject, arg, arg2, arg3);
    }

    public void pcallvoid(KahluaThread thread, Object functionObject, Object[] args) {
        if (args != null) {
            for (int i = args.length - 1; i >= 0; --i) {
                args[i] = this.converterManager.fromJavaToLua(args[i]);
            }
        }
        thread.pcallvoid(functionObject, args);
    }

    public Object[] pcall(KahluaThread thread, Object functionObject, Object ... args) {
        if (args != null) {
            for (int i = args.length - 1; i >= 0; --i) {
                args[i] = this.converterManager.fromJavaToLua(args[i]);
            }
        }
        Object[] results = thread.pcall(functionObject, args);
        return results;
    }

    public Object[] pcall(KahluaThread thread, Object functionObject, Object arg) {
        if (arg != null) {
            arg = this.converterManager.fromJavaToLua(arg);
        }
        Object[] results = thread.pcall(functionObject, new Object[]{arg});
        return results;
    }

    public Boolean protectedCallBoolean(KahluaThread thread, Object functionObject, Object arg) {
        arg = this.converterManager.fromJavaToLua(arg);
        return thread.pcallBoolean(functionObject, arg);
    }

    public Boolean protectedCallBoolean(KahluaThread thread, Object functionObject, Object arg, Object arg2) {
        arg = this.converterManager.fromJavaToLua(arg);
        arg2 = this.converterManager.fromJavaToLua(arg2);
        return thread.pcallBoolean(functionObject, arg, arg2);
    }

    public Boolean protectedCallBoolean(KahluaThread thread, Object functionObject, Object arg, Object arg2, Object arg3) {
        arg = this.converterManager.fromJavaToLua(arg);
        arg2 = this.converterManager.fromJavaToLua(arg2);
        arg3 = this.converterManager.fromJavaToLua(arg3);
        return thread.pcallBoolean(functionObject, arg, arg2, arg3);
    }

    public Boolean pcallBoolean(KahluaThread thread, Object functionObject, Object[] args) {
        if (args != null) {
            for (int i = args.length - 1; i >= 0; --i) {
                args[i] = this.converterManager.fromJavaToLua(args[i]);
            }
        }
        return thread.pcallBoolean(functionObject, args);
    }

    public LuaReturn protectedCall(KahluaThread thread, Object functionObject, Object ... args) {
        return LuaReturn.createReturn(this.pcall(thread, functionObject, args));
    }

    public void protectedCallVoid(KahluaThread thread, Object functionObject, Object arg) {
        arg = this.converterManager.fromJavaToLua(arg);
        thread.pcallvoid(functionObject, arg);
    }

    public void protectedCallVoid(KahluaThread thread, Object functionObject, Object arg, Object arg2) {
        arg = this.converterManager.fromJavaToLua(arg);
        arg2 = this.converterManager.fromJavaToLua(arg2);
        thread.pcallvoid(functionObject, arg, arg2);
    }

    public void protectedCallVoid(KahluaThread thread, Object functionObject, Object arg, Object arg2, Object arg3) {
        arg = this.converterManager.fromJavaToLua(arg);
        arg2 = this.converterManager.fromJavaToLua(arg2);
        arg3 = this.converterManager.fromJavaToLua(arg3);
        thread.pcallvoid(functionObject, arg, arg2, arg3);
    }

    public void protectedCallVoid(KahluaThread thread, Object functionObject, Object[] args) {
        this.pcallvoid(thread, functionObject, args);
    }

    public Boolean protectedCallBoolean(KahluaThread thread, Object functionObject, Object[] args) {
        return this.pcallBoolean(thread, functionObject, args);
    }
}

