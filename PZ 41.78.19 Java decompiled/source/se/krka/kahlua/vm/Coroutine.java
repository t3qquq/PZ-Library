// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.vm;

import java.util.ArrayList;
import zombie.Lua.LuaManager;
import zombie.core.Core;

public class Coroutine {
    private final Platform platform;
    private KahluaThread thread;
    private Coroutine parent;
    public KahluaTable environment;
    public String stackTrace = "";
    private final ArrayList<UpValue> liveUpvalues = new ArrayList<>();
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

    public Coroutine(Platform arg0, KahluaTable arg1, KahluaThread arg2) {
        this.platform = arg0;
        this.environment = arg1;
        this.thread = arg2;
        this.objectStack = new Object[1000];
        this.callFrameStack = new LuaCallFrame[200];
    }

    public Coroutine(Platform arg0, KahluaTable arg1) {
        this(arg0, arg1, null);
    }

    public final LuaCallFrame pushNewCallFrame(LuaClosure arg0, JavaFunction arg1, int arg2, int arg3, int arg4, boolean arg5, boolean arg6) {
        this.setCallFrameStackTop(this.callFrameTop + 1);
        LuaCallFrame luaCallFrame = this.currentCallFrame();
        luaCallFrame.setup(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
        return luaCallFrame;
    }

    public void popCallFrame() {
        if (this.isDead()) {
            throw new RuntimeException("Stack underflow");
        } else {
            this.setCallFrameStackTop(this.callFrameTop - 1);
        }
    }

    private final void ensureCallFrameStackSize(int int0) {
        if (int0 > 1000) {
            throw new RuntimeException("Stack overflow");
        } else {
            int int1 = this.callFrameStack.length;
            int int2 = int1;

            while (int2 <= int0) {
                int2 = 2 * int2;
            }

            if (int2 > int1) {
                LuaCallFrame[] luaCallFrames = new LuaCallFrame[int2];
                System.arraycopy(this.callFrameStack, 0, luaCallFrames, 0, int1);
                this.callFrameStack = luaCallFrames;
            }
        }
    }

    public final void setCallFrameStackTop(int arg0) {
        if (arg0 > this.callFrameTop) {
            this.ensureCallFrameStackSize(arg0);
        } else {
            this.callFrameStackClear(arg0, this.callFrameTop - 1);
        }

        this.callFrameTop = arg0;
    }

    private void callFrameStackClear(int int0, int int1) {
        while (int0 <= int1) {
            LuaCallFrame luaCallFrame = this.callFrameStack[int0];
            if (luaCallFrame != null) {
                this.callFrameStack[int0].closure = null;
                this.callFrameStack[int0].javaFunction = null;
            }

            int0++;
        }
    }

    private final void ensureStacksize(int int0) {
        if (int0 > 3000) {
            throw new RuntimeException("Stack overflow");
        } else {
            int int1 = this.objectStack.length;
            int int2 = int1;

            while (int2 <= int0) {
                int2 = 2 * int2;
            }

            if (int2 > int1) {
                Object[] objects = new Object[int2];
                System.arraycopy(this.objectStack, 0, objects, 0, int1);
                this.objectStack = objects;
            }
        }
    }

    public final void setTop(int arg0) {
        if (this.top < arg0) {
            this.ensureStacksize(arg0);
        } else {
            this.stackClear(arg0, this.top - 1);
        }

        this.top = arg0;
    }

    public final void stackCopy(int arg0, int arg1, int arg2) {
        if (arg2 > 0 && arg0 != arg1) {
            System.arraycopy(this.objectStack, arg0, this.objectStack, arg1, arg2);
            LuaCallFrame luaCallFrame = this.getParentNoAssert(1);
            if (Core.bDebug && luaCallFrame != null && luaCallFrame.closure != null && luaCallFrame.pc > 0) {
                for (int int0 = arg1; int0 < arg1 + arg2; int0++) {
                    int int1 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc - 1];
                    boolean boolean0 = luaCallFrame.closure.prototype.lines[luaCallFrame.pc] != int1;
                    if (this.thread == LuaManager.thread && luaCallFrame.closure.prototype.locvarlines != null) {
                        while (
                            int1 > luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned]
                                && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] != 0
                        ) {
                            luaCallFrame.localsAssigned++;
                        }
                    }

                    if (boolean0
                        && this.thread == LuaManager.thread
                        && luaCallFrame.closure.prototype.locvarlines != null
                        && luaCallFrame.closure.prototype.locvarlines[luaCallFrame.localsAssigned] == int1) {
                        int int2 = luaCallFrame.localsAssigned++;
                        String string = luaCallFrame.closure.prototype.locvars[int2];
                        luaCallFrame.setLocalVarToStack(string, int0);
                    }
                }
            }
        }
    }

    public final void stackClear(int arg0, int arg1) {
        while (arg0 <= arg1) {
            this.objectStack[arg0] = null;
            arg0++;
        }
    }

    public final void closeUpvalues(int arg0) {
        int int0 = this.liveUpvalues.size();

        while (--int0 >= 0) {
            UpValue upValue = this.liveUpvalues.get(int0);
            if (upValue.getIndex() < arg0) {
                return;
            }

            upValue.close();
            this.liveUpvalues.remove(int0);
        }
    }

    public final UpValue findUpvalue(int arg0) {
        int int0 = this.liveUpvalues.size();

        while (--int0 >= 0) {
            UpValue upValue0 = this.liveUpvalues.get(int0);
            int int1 = upValue0.getIndex();
            if (int1 == arg0) {
                return upValue0;
            }

            if (int1 < arg0) {
                break;
            }
        }

        UpValue upValue1 = new UpValue(this, arg0);
        this.liveUpvalues.add(int0 + 1, upValue1);
        return upValue1;
    }

    public Object getObjectFromStack(int arg0) {
        return this.objectStack[arg0];
    }

    public int getObjectStackSize() {
        return this.top;
    }

    public LuaCallFrame getParentCallframe() {
        int int0 = this.callFrameTop - 1;
        return int0 < 0 ? null : this.callFrameStack[int0];
    }

    public final LuaCallFrame currentCallFrame() {
        if (this.isDead()) {
            return null;
        } else {
            LuaCallFrame luaCallFrame = this.callFrameStack[this.callFrameTop - 1];
            if (luaCallFrame == null) {
                luaCallFrame = new LuaCallFrame(this);
                this.callFrameStack[this.callFrameTop - 1] = luaCallFrame;
            }

            return luaCallFrame;
        }
    }

    public int getTop() {
        return this.top;
    }

    public LuaCallFrame getParent(int arg0) {
        KahluaUtil.luaAssert(arg0 >= 0, "Level must be non-negative");
        int int0 = this.callFrameTop - arg0 - 1;
        KahluaUtil.luaAssert(int0 >= 0, "Level too high");
        return this.callFrameStack[int0];
    }

    public LuaCallFrame getParentNoAssert(int arg0) {
        int int0 = this.callFrameTop - arg0 - 1;
        return int0 < 0 ? null : this.callFrameStack[int0];
    }

    public String getCurrentStackTrace(int arg0, int arg1, int arg2) {
        if (arg0 < 0) {
            arg0 = 0;
        }

        if (arg1 < 0) {
            arg1 = 0;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (int int0 = this.callFrameTop - 1 - arg0; int0 >= arg2 && arg1-- > 0; int0--) {
            stringBuilder.append(this.getStackTrace(this.callFrameStack[int0]));
        }

        return stringBuilder.toString();
    }

    public void cleanCallFrames(LuaCallFrame arg0) {
        while (true) {
            LuaCallFrame luaCallFrame = this.currentCallFrame();
            if (luaCallFrame == null || luaCallFrame == arg0) {
                return;
            }

            this.addStackTrace(luaCallFrame);
            this.popCallFrame();
        }
    }

    public void addStackTrace(LuaCallFrame arg0) {
        this.stackTrace = this.stackTrace + this.getStackTrace(arg0);
    }

    private String getStackTrace(LuaCallFrame luaCallFrame) {
        if (luaCallFrame.isLua()) {
            int[] ints = luaCallFrame.closure.prototype.lines;
            if (ints != null) {
                int int0 = luaCallFrame.pc - 1;
                if (int0 >= 0 && int0 < ints.length) {
                    return "at " + luaCallFrame.closure.prototype + ":" + ints[int0] + "\n";
                }
            }

            return "";
        } else {
            return "at " + luaCallFrame.javaFunction + "\n";
        }
    }

    public boolean isDead() {
        return this.callFrameTop == 0;
    }

    public Platform getPlatform() {
        return this.platform;
    }

    public String getStatus() {
        if (this.parent == null) {
            return this.isDead() ? "dead" : "suspended";
        } else {
            return "normal";
        }
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

    public LuaCallFrame getCallFrame(int arg0) {
        if (arg0 < 0) {
            arg0 += this.callFrameTop;
        }

        return this.callFrameStack[arg0];
    }

    public static void yieldHelper(LuaCallFrame arg0, LuaCallFrame arg1, int arg2) {
        KahluaUtil.luaAssert(arg0.canYield, "Can not yield outside of a coroutine");
        Coroutine coroutine0 = arg0.coroutine;
        KahluaThread kahluaThread = coroutine0.getThread();
        Coroutine coroutine1 = coroutine0.parent;
        KahluaUtil.luaAssert(coroutine1 != null, "Internal error, coroutine must be running");
        KahluaUtil.luaAssert(coroutine0 == kahluaThread.currentCoroutine, "Internal error, must yield current thread");
        coroutine0.destroy();
        LuaCallFrame luaCallFrame = coroutine1.currentCallFrame();
        if (luaCallFrame == null) {
            coroutine1.setTop(arg2 + 1);
            coroutine1.objectStack[0] = Boolean.TRUE;

            for (int int0 = 0; int0 < arg2; int0++) {
                coroutine1.objectStack[int0 + 1] = arg1.get(int0);
            }
        } else {
            luaCallFrame.push(Boolean.TRUE);

            for (int int1 = 0; int1 < arg2; int1++) {
                Object object = arg1.get(int1);
                luaCallFrame.push(object);
            }
        }

        kahluaThread.currentCoroutine = coroutine1;
    }

    public void resume(Coroutine arg0) {
        this.parent = arg0;
        this.thread = arg0.thread;
    }

    public KahluaThread getThread() {
        return this.thread;
    }

    public void destroy() {
        this.parent = null;
        this.thread = null;
    }
}
