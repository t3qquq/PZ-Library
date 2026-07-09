// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration;

import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.vm.KahluaThread;

public class LuaCaller {
    private final KahluaConverterManager converterManager;

    public LuaCaller(KahluaConverterManager arg0) {
        this.converterManager = arg0;
    }

    public void pcallvoid(KahluaThread arg0, Object arg1, Object arg2) {
        arg0.pcallvoid(arg1, arg2);
    }

    public void pcallvoid(KahluaThread arg0, Object arg1, Object arg2, Object arg3) {
        arg0.pcallvoid(arg1, arg2, arg3);
    }

    public void pcallvoid(KahluaThread arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        arg0.pcallvoid(arg1, arg2, arg3, arg4);
    }

    public Boolean pcallBoolean(KahluaThread arg0, Object arg1, Object arg2, Object arg3) {
        return arg0.pcallBoolean(arg1, arg2, arg3);
    }

    public Boolean pcallBoolean(KahluaThread arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        return arg0.pcallBoolean(arg1, arg2, arg3, arg4);
    }

    public void pcallvoid(KahluaThread arg0, Object arg1, Object[] arg2) {
        if (arg2 != null) {
            for (int int0 = arg2.length - 1; int0 >= 0; int0--) {
                arg2[int0] = this.converterManager.fromJavaToLua(arg2[int0]);
            }
        }

        arg0.pcallvoid(arg1, arg2);
    }

    public Object[] pcall(KahluaThread kahluaThread, Object object, Object... objects) {
        if (objects != null) {
            for (int int0 = objects.length - 1; int0 >= 0; int0--) {
                objects[int0] = this.converterManager.fromJavaToLua(objects[int0]);
            }
        }

        return kahluaThread.pcall(object, objects);
    }

    public Object[] pcall(KahluaThread kahluaThread, Object object1, Object object0) {
        if (object0 != null) {
            object0 = this.converterManager.fromJavaToLua(object0);
        }

        return kahluaThread.pcall(object1, new Object[]{object0});
    }

    public Boolean protectedCallBoolean(KahluaThread arg0, Object arg1, Object arg2) {
        arg2 = this.converterManager.fromJavaToLua(arg2);
        return arg0.pcallBoolean(arg1, arg2);
    }

    public Boolean protectedCallBoolean(KahluaThread arg0, Object arg1, Object arg2, Object arg3) {
        arg2 = this.converterManager.fromJavaToLua(arg2);
        arg3 = this.converterManager.fromJavaToLua(arg3);
        return arg0.pcallBoolean(arg1, arg2, arg3);
    }

    public Boolean protectedCallBoolean(KahluaThread arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        arg2 = this.converterManager.fromJavaToLua(arg2);
        arg3 = this.converterManager.fromJavaToLua(arg3);
        arg4 = this.converterManager.fromJavaToLua(arg4);
        return arg0.pcallBoolean(arg1, arg2, arg3, arg4);
    }

    public Boolean pcallBoolean(KahluaThread kahluaThread, Object object, Object[] objects) {
        if (objects != null) {
            for (int int0 = objects.length - 1; int0 >= 0; int0--) {
                objects[int0] = this.converterManager.fromJavaToLua(objects[int0]);
            }
        }

        return kahluaThread.pcallBoolean(object, objects);
    }

    public LuaReturn protectedCall(KahluaThread kahluaThread, Object object, Object... objects) {
        return LuaReturn.createReturn(this.pcall(kahluaThread, object, objects));
    }

    public void protectedCallVoid(KahluaThread arg0, Object arg1, Object arg2) {
        arg2 = this.converterManager.fromJavaToLua(arg2);
        arg0.pcallvoid(arg1, arg2);
    }

    public void protectedCallVoid(KahluaThread arg0, Object arg1, Object arg2, Object arg3) {
        arg2 = this.converterManager.fromJavaToLua(arg2);
        arg3 = this.converterManager.fromJavaToLua(arg3);
        arg0.pcallvoid(arg1, arg2, arg3);
    }

    public void protectedCallVoid(KahluaThread arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        arg2 = this.converterManager.fromJavaToLua(arg2);
        arg3 = this.converterManager.fromJavaToLua(arg3);
        arg4 = this.converterManager.fromJavaToLua(arg4);
        arg0.pcallvoid(arg1, arg2, arg3, arg4);
    }

    public void protectedCallVoid(KahluaThread arg0, Object arg1, Object[] arg2) {
        this.pcallvoid(arg0, arg1, arg2);
    }

    public Boolean protectedCallBoolean(KahluaThread arg0, Object arg1, Object[] arg2) {
        return this.pcallBoolean(arg0, arg1, arg2);
    }
}
