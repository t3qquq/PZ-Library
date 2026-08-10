/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zombie.Lua.LuaManager
 *  zombie.core.Core
 */
package se.krka.kahlua.vm;

import java.util.ArrayList;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;
import se.krka.kahlua.vm.UpValue;
import zombie.Lua.LuaManager;
import zombie.core.Core;

public class Coroutine {
    private final Platform platform;
    private KahluaThread thread;
    private Coroutine parent;
    public KahluaTable environment;
    public String stackTrace = "";
    private final ArrayList<UpValue> liveUpvalues = new ArrayList();
    private static final int MAX_STACK_SIZE = 3000;
    private static final int INITIAL_STACK_SIZE = 1000;
    private static final int MAX_CALL_FRAME_STACK_SIZE = 1000;
    private static final int INITIAL_CALL_FRAME_STACK_SIZE = 200;
    public Object[] objectStack;
    private int top;
    private LuaCallFrame[] callFrameStack;
    private int callFrameTop;

    public Coroutine() {
        this.platform = null;
    }

    public Coroutine getParent() {
        return this.parent;
    }

    public Coroutine(Platform platform, KahluaTable kahluaTable, KahluaThread kahluaThread) {
        this.platform = platform;
        this.environment = kahluaTable;
        this.thread = kahluaThread;
        this.objectStack = new Object[1000];
        this.callFrameStack = new LuaCallFrame[200];
    }

    public Coroutine(Platform platform, KahluaTable kahluaTable) {
        this(platform, kahluaTable, null);
    }

    public final LuaCallFrame pushNewCallFrame(LuaClosure luaClosure, JavaFunction javaFunction, int n, int n2, int n3, boolean bl, boolean bl2) {
        this.setCallFrameStackTop(this.callFrameTop + 1);
        LuaCallFrame luaCallFrame = this.currentCallFrame();
        luaCallFrame.setup(luaClosure, javaFunction, n, n2, n3, bl, bl2);
        return luaCallFrame;
    }

    public void popCallFrame() {
        if (this.isDead()) {
            throw new RuntimeException("Stack underflow");
        }
        this.setCallFrameStackTop(this.callFrameTop - 1);
    }

    private final void ensureCallFrameStackSize(int n) {
        int n2;
        int n3;
        if (n > 1000) {
            throw new RuntimeException("Stack overflow");
        }
        for (n3 = n2 = this.callFrameStack.length; n3 <= n; n3 = 2 * n3) {
        }
        if (n3 > n2) {
            LuaCallFrame[] luaCallFrameArray = new LuaCallFrame[n3];
            System.arraycopy(this.callFrameStack, 0, luaCallFrameArray, 0, n2);
            this.callFrameStack = luaCallFrameArray;
        }
    }

    public final void setCallFrameStackTop(int n) {
        if (n > this.callFrameTop) {
            this.ensureCallFrameStackSize(n);
        } else {
            this.callFrameStackClear(n, this.callFrameTop - 1);
        }
        this.callFrameTop = n;
    }

    private void callFrameStackClear(int n, int n2) {
        while (n <= n2) {
            LuaCallFrame luaCallFrame = this.callFrameStack[n];
            if (luaCallFrame != null) {
                this.callFrameStack[n].closure = null;
                this.callFrameStack[n].javaFunction = null;
            }
            ++n;
        }
    }

    private final void ensureStacksize(int n) {
        int n2;
        int n3;
        if (n > 3000) {
            throw new RuntimeException("Stack overflow");
        }
        for (n3 = n2 = this.objectStack.length; n3 <= n; n3 = 2 * n3) {
        }
        if (n3 > n2) {
            Object[] objectArray = new Object[n3];
            System.arraycopy(this.objectStack, 0, objectArray, 0, n2);
            this.objectStack = objectArray;
        }
    }

    public final void setTop(int n) {
        if (this.top < n) {
            this.ensureStacksize(n);
        } else {
            this.stackClear(n, this.top - 1);
        }
        this.top = n;
    }

    public final void stackCopy(int n, int n2, int n3) {
        if (n3 > 0 && n != n2) {
            System.arraycopy(this.objectStack, n, this.objectStack, n2, n3);
            LuaCallFrame luaCallFrame = this.getParentNoAssert(1);
            if (Core.bDebug && luaCallFrame != null && luaCallFrame.closure != null && luaCallFrame.pc > 0) {
                for (int i = n2; i < n2 + n3; ++i) {
                    int n4;
                    boolean bl;
                    int n5 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc - 1];
                    boolean bl2 = bl = luaCallFrame.closure.prototype.lines[luaCallFrame.pc] != n5;
                    if (this.thread == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null) {
                        while (n5 > luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] != 0) {
                            ++luaCallFrame.localsAssigned;
                        }
                    }
                    if (!bl || this.thread != LuaManager.thread || luaCallFrame.closure.prototype.locvarlines == null || luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] != n5) continue;
                    ++luaCallFrame.localsAssigned;
                    String string = luaCallFrame.closure.prototype.locvars[n4];
                    luaCallFrame.setLocalVarToStack(string, i);
                }
            }
        }
    }

    public final void stackClear(int n, int n2) {
        while (n <= n2) {
            this.objectStack[n] = null;
            ++n;
        }
    }

    public final void closeUpvalues(int n) {
        int n2 = this.liveUpvalues.size();
        while (--n2 >= 0) {
            UpValue upValue = this.liveUpvalues.get(n2);
            if (upValue.getIndex() < n) {
                return;
            }
            upValue.close();
            this.liveUpvalues.remove(n2);
        }
    }

    public final UpValue findUpvalue(int n) {
        UpValue upValue;
        int n2 = this.liveUpvalues.size();
        while (--n2 >= 0) {
            upValue = this.liveUpvalues.get(n2);
            int n3 = upValue.getIndex();
            if (n3 == n) {
                return upValue;
            }
            if (n3 >= n) continue;
            break;
        }
        upValue = new UpValue(this, n);
        this.liveUpvalues.add(n2 + 1, upValue);
        return upValue;
    }

    public Object getObjectFromStack(int n) {
        return this.objectStack[n];
    }

    public int getObjectStackSize() {
        return this.top;
    }

    public LuaCallFrame getParentCallframe() {
        int n = this.callFrameTop - 1;
        if (n < 0) {
            return null;
        }
        return this.callFrameStack[n];
    }

    public final LuaCallFrame currentCallFrame() {
        if (this.isDead()) {
            return null;
        }
        LuaCallFrame luaCallFrame = this.callFrameStack[this.callFrameTop - 1];
        if (luaCallFrame == null) {
            this.callFrameStack[this.callFrameTop - 1] = luaCallFrame = new LuaCallFrame(this);
        }
        return luaCallFrame;
    }

    public int getTop() {
        return this.top;
    }

    public LuaCallFrame getParent(int n) {
        KahluaUtil.luaAssert(n >= 0, "Level must be non-negative");
        int n2 = this.callFrameTop - n - 1;
        KahluaUtil.luaAssert(n2 >= 0, "Level too high");
        return this.callFrameStack[n2];
    }

    public LuaCallFrame getParentNoAssert(int n) {
        int n2 = this.callFrameTop - n - 1;
        if (n2 < 0) {
            return null;
        }
        return this.callFrameStack[n2];
    }

    public String getCurrentStackTrace(int n, int n2, int n3) {
        if (n < 0) {
            n = 0;
        }
        if (n2 < 0) {
            n2 = 0;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = this.callFrameTop - 1 - n; i >= n3 && n2-- > 0; --i) {
            stringBuilder.append(this.getStackTrace(this.callFrameStack[i]));
        }
        return stringBuilder.toString();
    }

    public void cleanCallFrames(LuaCallFrame luaCallFrame) {
        LuaCallFrame luaCallFrame2;
        while ((luaCallFrame2 = this.currentCallFrame()) != null && luaCallFrame2 != luaCallFrame) {
            this.addStackTrace(luaCallFrame2);
            this.popCallFrame();
        }
    }

    public void addStackTrace(LuaCallFrame luaCallFrame) {
        this.stackTrace = this.stackTrace + this.getStackTrace(luaCallFrame);
    }

    private String getStackTrace(LuaCallFrame luaCallFrame) {
        if (luaCallFrame.isLua()) {
            int n;
            int[] nArray = luaCallFrame.closure.prototype.lines;
            if (nArray != null && (n = luaCallFrame.pc - 1) >= 0 && n < nArray.length) {
                return "at " + luaCallFrame.closure.prototype + ":" + nArray[n] + "\n";
            }
        } else {
            return "at " + luaCallFrame.javaFunction + "\n";
        }
        return "";
    }

    public boolean isDead() {
        return this.callFrameTop == 0;
    }

    public Platform getPlatform() {
        return this.platform;
    }

    public String getStatus() {
        if (this.parent == null) {
            if (this.isDead()) {
                return "dead";
            }
            return "suspended";
        }
        return "normal";
    }

    public boolean atBottom() {
        return this.callFrameTop == 1;
    }

    public int getCallframeTop() {
        return this.callFrameTop;
    }

    public LuaCallFrame[] getCallframeStack() {
        return this.callFrameStack;
    }

    public LuaCallFrame getCallFrame(int n) {
        if (n < 0) {
            n += this.callFrameTop;
        }
        return this.callFrameStack[n];
    }

    public static void yieldHelper(LuaCallFrame luaCallFrame, LuaCallFrame luaCallFrame2, int n) {
        KahluaUtil.luaAssert(luaCallFrame.canYield, "Can not yield outside of a coroutine");
        Coroutine coroutine = luaCallFrame.coroutine;
        KahluaThread kahluaThread = coroutine.getThread();
        Coroutine coroutine2 = coroutine.parent;
        KahluaUtil.luaAssert(coroutine2 != null, "Internal error, coroutine must be running");
        KahluaUtil.luaAssert(coroutine == kahluaThread.currentCoroutine, "Internal error, must yield current thread");
        coroutine.destroy();
        LuaCallFrame luaCallFrame3 = coroutine2.currentCallFrame();
        if (luaCallFrame3 == null) {
            coroutine2.setTop(n + 1);
            coroutine2.objectStack[0] = Boolean.TRUE;
            for (int i = 0; i < n; ++i) {
                coroutine2.objectStack[i + 1] = luaCallFrame2.get(i);
            }
        } else {
            luaCallFrame3.push(Boolean.TRUE);
            for (int i = 0; i < n; ++i) {
                Object object = luaCallFrame2.get(i);
                luaCallFrame3.push(object);
            }
        }
        kahluaThread.currentCoroutine = coroutine2;
    }

    public void resume(Coroutine coroutine) {
        this.parent = coroutine;
        this.thread = coroutine.thread;
    }

    public KahluaThread getThread() {
        return this.thread;
    }

    public void destroy() {
        this.parent = null;
        this.thread = null;
    }
}

