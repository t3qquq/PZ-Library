// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.vm;

import java.util.ArrayList;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.core.utils.HashMap;

public class LuaCallFrame {
    private final Platform platform;
    public final Coroutine coroutine;
    public LuaClosure closure;
    public JavaFunction javaFunction;
    public int pc;
    public int localBase;
    int returnBase;
    public int nArguments;
    boolean fromLua;
    public boolean canYield;
    boolean restoreTop;
    public int localsAssigned = 0;
    public HashMap LocalVarToStackMap = new HashMap();
    public HashMap LocalStackToVarMap = new HashMap();
    public ArrayList<String> LocalVarNames = new ArrayList<>();

    public LuaCallFrame(Coroutine arg0) {
        this.coroutine = arg0;
        this.platform = arg0.getPlatform();
    }

    public String getFilename() {
        return this.closure != null ? this.closure.prototype.filename : null;
    }

    public final void set(int arg0, Object arg1) {
        this.coroutine.objectStack[this.localBase + arg0] = arg1;
    }

    public final Object get(int arg0) {
        return this.coroutine.objectStack[this.localBase + arg0];
    }

    public int push(Object arg0) {
        int int0 = this.getTop();
        this.setTop(int0 + 1);
        this.set(int0, arg0);
        return 1;
    }

    public int push(Object arg0, Object arg1) {
        int int0 = this.getTop();
        this.setTop(int0 + 2);
        this.set(int0, arg0);
        this.set(int0 + 1, arg1);
        return 2;
    }

    public int pushNil() {
        return this.push(null);
    }

    public final void stackCopy(int arg0, int arg1, int arg2) {
        this.coroutine.stackCopy(this.localBase + arg0, this.localBase + arg1, arg2);
    }

    public void stackClear(int arg0, int arg1) {
        while (arg0 <= arg1) {
            this.coroutine.objectStack[this.localBase + arg0] = null;
            arg0++;
        }
    }

    public void clearFromIndex(int arg0) {
        if (this.getTop() < arg0) {
            this.setTop(arg0);
        }

        this.stackClear(arg0, this.getTop() - 1);
    }

    public final void setTop(int arg0) {
        this.coroutine.setTop(this.localBase + arg0);
    }

    public void closeUpvalues(int arg0) {
        this.coroutine.closeUpvalues(this.localBase + arg0);
    }

    public UpValue findUpvalue(int arg0) {
        return this.coroutine.findUpvalue(this.localBase + arg0);
    }

    public int getTop() {
        return this.coroutine.getTop() - this.localBase;
    }

    public void init() {
        if (this.isLua()) {
            this.pc = 0;
            if (this.closure.prototype.isVararg) {
                this.localBase = this.localBase + this.nArguments;
                this.setTop(this.closure.prototype.maxStacksize);
                int int0 = Math.min(this.nArguments, this.closure.prototype.numParams);
                this.stackCopy(-this.nArguments, 0, int0);
            } else {
                this.setTop(this.closure.prototype.maxStacksize);
                this.stackClear(this.closure.prototype.numParams, this.nArguments);
            }
        }
    }

    public void setPrototypeStacksize() {
        if (this.isLua()) {
            this.setTop(this.closure.prototype.maxStacksize);
        }
    }

    public void pushVarargs(int arg0, int arg1) {
        int int0 = this.closure.prototype.numParams;
        int int1 = this.nArguments - int0;
        if (int1 < 0) {
            int1 = 0;
        }

        if (arg1 == -1) {
            arg1 = int1;
            this.setTop(arg0 + int1);
        }

        if (int1 > arg1) {
            int1 = arg1;
        }

        this.stackCopy(-this.nArguments + int0, arg0, int1);
        int int2 = arg1 - int1;
        if (int2 > 0) {
            this.stackClear(arg0 + int1, arg0 + arg1 - 1);
        }
    }

    public KahluaTable getEnvironment() {
        return this.isLua() ? this.closure.env : this.coroutine.environment;
    }

    public boolean isJava() {
        return !this.isLua();
    }

    public boolean isLua() {
        return this.closure != null;
    }

    public String toString2() {
        if (this.closure != null) {
            return this.closure.toString2(this.pc);
        } else {
            return this.javaFunction != null ? "Callframe at: " + this.javaFunction.toString() : super.toString();
        }
    }

    @Override
    public String toString() {
        if (this.closure != null) {
            return "Callframe at: " + this.closure.toString();
        } else {
            return this.javaFunction != null ? "Callframe at: " + this.javaFunction.toString() : super.toString();
        }
    }

    public Platform getPlatform() {
        return this.platform;
    }

    void setup(LuaClosure luaClosure, JavaFunction javaFunctionx, int int0, int int1, int int2, boolean boolean0, boolean boolean1) {
        this.localBase = int0;
        this.returnBase = int1;
        this.nArguments = int2;
        this.fromLua = boolean0;
        this.canYield = boolean1;
        this.closure = luaClosure;
        this.javaFunction = javaFunctionx;
        LuaCallFrame luaCallFrame1 = this;
        this.localsAssigned = 0;
        this.LocalVarToStackMap.clear();
        this.LocalStackToVarMap.clear();
        this.LocalVarNames.clear();
        if (Core.bDebug && this != null && this.closure != null && this.getThread() == LuaManager.thread) {
            for (int int3 = int0; int3 < int0 + int2; int3++) {
                int int4 = luaCallFrame1.closure.prototype.lines[0];
                if (luaCallFrame1.closure.prototype.locvarlines != null
                    && luaCallFrame1.closure.prototype.locvarlines[luaCallFrame1.localsAssigned] < int4
                    && luaCallFrame1.closure.prototype.locvarlines[luaCallFrame1.localsAssigned] != 0) {
                    int int5 = luaCallFrame1.localsAssigned++;
                    String string = luaCallFrame1.closure.prototype.locvars[int5];
                    if (string.equals("group")) {
                        boolean boolean2 = false;
                    }

                    luaCallFrame1.setLocalVarToStack(string, int3);
                }
            }
        }
    }

    public KahluaThread getThread() {
        return this.coroutine.getThread();
    }

    public LuaClosure getClosure() {
        return this.closure;
    }

    public void setLocalVarToStack(String arg0, int arg1) {
        this.LocalVarToStackMap.put(arg0, arg1);
        this.LocalStackToVarMap.put(arg1, arg0);
        this.LocalVarNames.add(arg0);
    }

    public String getNameOfStack(int arg0) {
        return this.LocalStackToVarMap.get(arg0) instanceof String ? (String)this.LocalStackToVarMap.get(arg0) : "";
    }

    public void printoutLocalVars() {
    }
}
