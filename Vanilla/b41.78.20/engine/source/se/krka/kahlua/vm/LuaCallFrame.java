/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zombie.Lua.LuaManager
 *  zombie.core.Core
 *  zombie.core.utils.HashMap
 */
package se.krka.kahlua.vm;

import java.util.ArrayList;
import se.krka.kahlua.vm.Coroutine;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;
import se.krka.kahlua.vm.UpValue;
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
    public ArrayList<String> LocalVarNames = new ArrayList();

    public LuaCallFrame(Coroutine coroutine) {
        this.coroutine = coroutine;
        this.platform = coroutine.getPlatform();
    }

    public String getFilename() {
        if (this.closure != null) {
            return this.closure.prototype.filename;
        }
        return null;
    }

    public final void set(int n, Object object) {
        this.coroutine.objectStack[this.localBase + n] = object;
    }

    public final Object get(int n) {
        return this.coroutine.objectStack[this.localBase + n];
    }

    public int push(Object object) {
        int n = this.getTop();
        this.setTop(n + 1);
        this.set(n, object);
        return 1;
    }

    public int push(Object object, Object object2) {
        int n = this.getTop();
        this.setTop(n + 2);
        this.set(n, object);
        this.set(n + 1, object2);
        return 2;
    }

    public int pushNil() {
        return this.push(null);
    }

    public final void stackCopy(int n, int n2, int n3) {
        this.coroutine.stackCopy(this.localBase + n, this.localBase + n2, n3);
    }

    public void stackClear(int n, int n2) {
        while (n <= n2) {
            this.coroutine.objectStack[this.localBase + n] = null;
            ++n;
        }
    }

    public void clearFromIndex(int n) {
        if (this.getTop() < n) {
            this.setTop(n);
        }
        this.stackClear(n, this.getTop() - 1);
    }

    public final void setTop(int n) {
        this.coroutine.setTop(this.localBase + n);
    }

    public void closeUpvalues(int n) {
        this.coroutine.closeUpvalues(this.localBase + n);
    }

    public UpValue findUpvalue(int n) {
        return this.coroutine.findUpvalue(this.localBase + n);
    }

    public int getTop() {
        return this.coroutine.getTop() - this.localBase;
    }

    public void init() {
        if (this.isLua()) {
            this.pc = 0;
            if (this.closure.prototype.isVararg) {
                this.localBase += this.nArguments;
                this.setTop(this.closure.prototype.maxStacksize);
                int n = Math.min(this.nArguments, this.closure.prototype.numParams);
                this.stackCopy(-this.nArguments, 0, n);
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

    public void pushVarargs(int n, int n2) {
        int n3 = this.closure.prototype.numParams;
        int n4 = this.nArguments - n3;
        if (n4 < 0) {
            n4 = 0;
        }
        if (n2 == -1) {
            n2 = n4;
            this.setTop(n + n2);
        }
        if (n4 > n2) {
            n4 = n2;
        }
        this.stackCopy(-this.nArguments + n3, n, n4);
        int n5 = n2 - n4;
        if (n5 > 0) {
            this.stackClear(n + n4, n + n2 - 1);
        }
    }

    public KahluaTable getEnvironment() {
        if (this.isLua()) {
            return this.closure.env;
        }
        return this.coroutine.environment;
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
        }
        if (this.javaFunction != null) {
            return "Callframe at: " + this.javaFunction.toString();
        }
        return super.toString();
    }

    public String toString() {
        if (this.closure != null) {
            return "Callframe at: " + this.closure.toString();
        }
        if (this.javaFunction != null) {
            return "Callframe at: " + this.javaFunction.toString();
        }
        return super.toString();
    }

    public Platform getPlatform() {
        return this.platform;
    }

    void setup(LuaClosure luaClosure, JavaFunction javaFunction, int n, int n2, int n3, boolean bl, boolean bl2) {
        this.localBase = n;
        this.returnBase = n2;
        this.nArguments = n3;
        this.fromLua = bl;
        this.canYield = bl2;
        this.closure = luaClosure;
        this.javaFunction = javaFunction;
        LuaCallFrame luaCallFrame = this;
        luaCallFrame.localsAssigned = 0;
        luaCallFrame.LocalVarToStackMap.clear();
        luaCallFrame.LocalStackToVarMap.clear();
        luaCallFrame.LocalVarNames.clear();
        if (Core.bDebug && luaCallFrame != null && luaCallFrame.closure != null && this.getThread() == LuaManager.thread) {
            for (int i = n; i < n + n3; ++i) {
                int n4;
                int n5 = luaCallFrame.closure.prototype.lines[0];
                if (luaCallFrame.closure.prototype.locvarlines == null || luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] >= n5 || luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] == 0) continue;
                ++luaCallFrame.localsAssigned;
                String string = luaCallFrame.closure.prototype.locvars[n4];
                if (string.equals("group")) {
                    boolean bl3 = false;
                }
                luaCallFrame.setLocalVarToStack(string, i);
            }
        }
    }

    public KahluaThread getThread() {
        return this.coroutine.getThread();
    }

    public LuaClosure getClosure() {
        return this.closure;
    }

    public void setLocalVarToStack(String string, int n) {
        this.LocalVarToStackMap.put((Object)string, (Object)n);
        this.LocalStackToVarMap.put((Object)n, (Object)string);
        this.LocalVarNames.add(string);
    }

    public String getNameOfStack(int n) {
        if (this.LocalStackToVarMap.get((Object)n) instanceof String) {
            return (String)this.LocalStackToVarMap.get((Object)n);
        }
        return "";
    }

    public void printoutLocalVars() {
    }
}

