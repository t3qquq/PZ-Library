/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zombie.GameWindow
 *  zombie.Lua.LuaManager
 *  zombie.core.Core
 *  zombie.core.logger.ExceptionLogger
 *  zombie.debug.DebugLog
 *  zombie.gameStates.IngameState
 *  zombie.ui.UIManager
 */
package se.krka.kahlua.vm;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.stdlib.BaseLib;
import se.krka.kahlua.vm.Coroutine;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaException;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;
import se.krka.kahlua.vm.Prototype;
import se.krka.kahlua.vm.UpValue;
import zombie.GameWindow;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.gameStates.IngameState;
import zombie.ui.UIManager;

public class KahluaThread {
    private static final int FIELDS_PER_FLUSH = 50;
    private static final int OP_MOVE = 0;
    private static final int OP_LOADK = 1;
    private static final int OP_LOADBOOL = 2;
    private static final int OP_LOADNIL = 3;
    private static final int OP_GETUPVAL = 4;
    private static final int OP_GETGLOBAL = 5;
    private static final int OP_GETTABLE = 6;
    private static final int OP_SETGLOBAL = 7;
    private static final int OP_SETUPVAL = 8;
    private static final int OP_SETTABLE = 9;
    private static final int OP_NEWTABLE = 10;
    private static final int OP_SELF = 11;
    private static final int OP_ADD = 12;
    private static final int OP_SUB = 13;
    private static final int OP_MUL = 14;
    private static final int OP_DIV = 15;
    private static final int OP_MOD = 16;
    private static final int OP_POW = 17;
    private static final int OP_UNM = 18;
    private static final int OP_NOT = 19;
    private static final int OP_LEN = 20;
    private static final int OP_CONCAT = 21;
    private static final int OP_JMP = 22;
    private static final int OP_EQ = 23;
    private static final int OP_LT = 24;
    private static final int OP_LE = 25;
    private static final int OP_TEST = 26;
    private static final int OP_TESTSET = 27;
    private static final int OP_CALL = 28;
    private static final int OP_TAILCALL = 29;
    private static final int OP_RETURN = 30;
    private static final int OP_FORLOOP = 31;
    private static final int OP_FORPREP = 32;
    private static final int OP_TFORLOOP = 33;
    private static final int OP_SETLIST = 34;
    private static final int OP_CLOSE = 35;
    private static final int OP_CLOSURE = 36;
    private static final int OP_VARARG = 37;
    private static final int MAX_INDEX_RECURSION = 100;
    private static final String[] meta_ops = new String[38];
    public static LuaCallFrame LastCallFrame;
    private final Coroutine rootCoroutine;
    public Coroutine currentCoroutine;
    private boolean doProfiling = false;
    private final PrintStream out;
    private final Platform platform;
    public boolean bStep = false;
    public String currentfile;
    public int currentLine;
    public int lastLine;
    public int lastCallFrame;
    public boolean bReset = false;
    public ArrayList<Entry> profileEntries = new ArrayList();
    public HashMap<String, Entry> profileEntryMap = new HashMap();
    public static int m_error_count;
    public static final ArrayList<String> m_errors_list;
    private final StringBuilder m_stringBuilder = new StringBuilder();
    private final StringWriter m_stringWriter = new StringWriter();
    private final PrintWriter m_printWriter = new PrintWriter(this.m_stringWriter);
    HashMap<String, ArrayList<Long>> BreakpointMap = new HashMap();
    HashMap<KahluaTable, ArrayList<Object>> BreakpointDataMap = new HashMap();
    HashMap<KahluaTable, ArrayList<Object>> BreakpointReadDataMap = new HashMap();
    public boolean bStepInto = false;

    public Coroutine getCurrentCoroutine() {
        return this.currentCoroutine;
    }

    public KahluaThread(Platform platform, KahluaTable kahluaTable) {
        this(System.out, platform, kahluaTable);
    }

    public KahluaThread(PrintStream printStream, Platform platform, KahluaTable kahluaTable) {
        this.platform = platform;
        this.out = printStream;
        this.currentCoroutine = this.rootCoroutine = new Coroutine(platform, kahluaTable, this);
    }

    public int call(int n) {
        int n2 = this.currentCoroutine.getTop();
        int n3 = n2 - n - 1;
        Object object = this.currentCoroutine.objectStack[n3];
        if (object == null) {
            throw new RuntimeException("tried to call nil");
        }
        try {
            if (object instanceof JavaFunction) {
                return this.callJava((JavaFunction)object, n3 + 1, n3, n);
            }
        }
        catch (Exception exception) {
            throw new RuntimeException(exception.getClass().getName() + " " + exception.getMessage() + " in " + (JavaFunction)object);
        }
        if (!(object instanceof LuaClosure)) {
            throw new RuntimeException("tried to call a non-function");
        }
        LuaCallFrame luaCallFrame = this.currentCoroutine.pushNewCallFrame((LuaClosure)object, null, n3 + 1, n3, n, false, false);
        luaCallFrame.init();
        this.luaMainloop();
        int n4 = this.currentCoroutine.getTop() - n3;
        this.currentCoroutine.stackTrace = "";
        return n4;
    }

    private int callJava(JavaFunction javaFunction, int n, int n2, int n3) {
        Coroutine coroutine = this.currentCoroutine;
        LuaCallFrame luaCallFrame = coroutine.pushNewCallFrame(null, javaFunction, n, n2, n3, false, false);
        int n4 = javaFunction.call(luaCallFrame, n3);
        int n5 = luaCallFrame.getTop();
        int n6 = n5 - n4;
        int n7 = n2 - n;
        luaCallFrame.stackCopy(n6, n7, n4);
        luaCallFrame.setTop(n4 + n7);
        coroutine.popCallFrame();
        return n4;
    }

    private final Object prepareMetatableCall(Object object) {
        if (object instanceof JavaFunction || object instanceof LuaClosure) {
            return object;
        }
        Object object2 = this.getMetaOp(object, "__call");
        return object2;
    }

    public boolean isCurrent(String string, int n) {
        return n == this.currentLine;
    }

    private final void luaMainloop() {
        Object object;
        Object object2;
        Object object3 = this.currentCoroutine.currentCallFrame();
        LuaClosure luaClosure = ((LuaCallFrame)object3).closure;
        Prototype prototype = luaClosure.prototype;
        int[] nArray = prototype.code;
        int n = ((LuaCallFrame)object3).returnBase;
        Object object4 = "";
        long l = System.nanoTime();
        if (this.doProfiling && Core.bDebug && this == LuaManager.thread) {
            Coroutine coroutine = this.getCurrentCoroutine();
            object2 = coroutine.objectStack[0].toString();
            object4 = object = coroutine.getThread().currentfile + " " + ((String)object2).substring(0, ((String)object2).indexOf(":"));
        }
        boolean bl = true;
        while (!this.bReset) {
            block184: {
                Object object5;
                if (Core.bDebug && this == LuaManager.thread && (object2 = this.getCurrentCoroutine()) != null) {
                    this.lastLine = this.currentLine;
                    object = ((Coroutine)object2).currentCallFrame();
                    if (((LuaCallFrame)object).closure != null) {
                        this.currentfile = ((LuaCallFrame)object).closure.prototype.filename;
                        this.currentLine = ((LuaCallFrame)object).closure.prototype.lines[((LuaCallFrame)object).pc];
                        if (this.bStep && this.currentLine != this.lastLine) {
                            if (this.bStepInto) {
                                this.bStep = false;
                                UIManager.debugBreakpoint((String)((LuaCallFrame)object).closure.prototype.filename, (long)((long)this.currentLine - 1L));
                                this.lastCallFrame = ((Coroutine)object2).getCallframeTop();
                                bl = true;
                            } else if (((Coroutine)object2).getCallframeTop() <= this.lastCallFrame) {
                                this.bStep = false;
                                this.lastCallFrame = ((Coroutine)object2).getCallframeTop();
                                UIManager.debugBreakpoint((String)((LuaCallFrame)object).closure.prototype.filename, (long)((long)this.currentLine - 1L));
                                bl = true;
                            }
                        }
                        if (this.BreakpointMap.containsKey(((LuaCallFrame)object).closure.prototype.filename) && ((ArrayList)(object5 = this.BreakpointMap.get(((LuaCallFrame)object).closure.prototype.filename))).contains(((LuaCallFrame)object).closure.prototype.lines[((LuaCallFrame)object).pc]) && (((LuaCallFrame)object).pc == 0 || ((LuaCallFrame)object).closure.prototype.lines[((LuaCallFrame)object).pc - 1] != ((LuaCallFrame)object).closure.prototype.lines[((LuaCallFrame)object).pc])) {
                            UIManager.debugBreakpoint((String)((LuaCallFrame)object).closure.prototype.filename, (long)((LuaCallFrame)object).closure.prototype.lines[((LuaCallFrame)object).pc]);
                        }
                    }
                }
                bl = true;
                try {
                    int n2;
                    if (this.bStep) {
                        n2 = 0;
                    }
                    n2 = nArray[((LuaCallFrame)object3).pc++];
                    int n3 = n2 & 0x3F;
                    switch (n3) {
                        case 0: {
                            int n4 = KahluaThread.getA8(n2);
                            int n5 = KahluaThread.getB9(n2);
                            ((LuaCallFrame)object3).set(n4, ((LuaCallFrame)object3).get(n5));
                            bl = false;
                            break;
                        }
                        case 1: {
                            int n6 = KahluaThread.getA8(n2);
                            int n7 = KahluaThread.getBx(n2);
                            if (Core.bDebug) {
                                boolean bl2;
                                int n8 = ((LuaCallFrame)object3).closure.prototype.lines[((LuaCallFrame)object3).pc - 1];
                                boolean bl3 = bl2 = ((LuaCallFrame)object3).closure.prototype.lines[((LuaCallFrame)object3).pc] != n8;
                                if (this == LuaManager.thread && ((LuaCallFrame)object3).closure.prototype.locvarlines != null) {
                                    while (n8 > ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] && ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] != 0) {
                                        ++((LuaCallFrame)object3).localsAssigned;
                                    }
                                }
                                if (bl2 && this == LuaManager.thread && ((LuaCallFrame)object3).closure.prototype.locvarlines != null && ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] == n8) {
                                    int n9;
                                    ++((LuaCallFrame)object3).localsAssigned;
                                    String string = ((LuaCallFrame)object3).closure.prototype.locvars[n9];
                                    ((LuaCallFrame)object3).setLocalVarToStack(string, ((LuaCallFrame)object3).localBase + n6);
                                }
                            }
                            ((LuaCallFrame)object3).set(n6, prototype.constants[n7]);
                            break;
                        }
                        case 2: {
                            Boolean bl4;
                            int n10 = KahluaThread.getA8(n2);
                            int n11 = KahluaThread.getB9(n2);
                            int n12 = KahluaThread.getC9(n2);
                            Boolean bl5 = bl4 = n11 == 0 ? Boolean.FALSE : Boolean.TRUE;
                            if (Core.bDebug) {
                                boolean bl6;
                                int n13 = ((LuaCallFrame)object3).closure.prototype.lines[((LuaCallFrame)object3).pc - 1];
                                boolean bl7 = bl6 = ((LuaCallFrame)object3).closure.prototype.lines[((LuaCallFrame)object3).pc] != n13;
                                if (this == LuaManager.thread && ((LuaCallFrame)object3).closure.prototype.locvarlines != null) {
                                    while (n13 > ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] && ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] != 0) {
                                        ++((LuaCallFrame)object3).localsAssigned;
                                    }
                                }
                                if (bl6 && this == LuaManager.thread && ((LuaCallFrame)object3).closure.prototype.locvarlines != null && ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] == n13) {
                                    int n14;
                                    ++((LuaCallFrame)object3).localsAssigned;
                                    String string = ((LuaCallFrame)object3).closure.prototype.locvars[n14];
                                    if (string.equals("group")) {
                                        boolean bl8 = false;
                                    }
                                    ((LuaCallFrame)object3).setLocalVarToStack(string, ((LuaCallFrame)object3).localBase + n10);
                                }
                            }
                            ((LuaCallFrame)object3).set(n10, bl4);
                            if (n12 == 0) break;
                            ++((LuaCallFrame)object3).pc;
                            break;
                        }
                        case 3: {
                            int n15 = KahluaThread.getA8(n2);
                            int n16 = KahluaThread.getB9(n2);
                            if (Core.bDebug) {
                                boolean bl9;
                                int n17 = ((LuaCallFrame)object3).closure.prototype.lines[((LuaCallFrame)object3).pc - 1];
                                boolean bl10 = bl9 = ((LuaCallFrame)object3).closure.prototype.lines[((LuaCallFrame)object3).pc] != n17;
                                if (this == LuaManager.thread && ((LuaCallFrame)object3).closure.prototype.locvarlines != null) {
                                    while (n17 > ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] && ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] != 0) {
                                        ++((LuaCallFrame)object3).localsAssigned;
                                    }
                                }
                                if (bl9 && this == LuaManager.thread && ((LuaCallFrame)object3).closure.prototype.locvarlines != null && ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] == n17) {
                                    int n18;
                                    ++((LuaCallFrame)object3).localsAssigned;
                                    String string = ((LuaCallFrame)object3).closure.prototype.locvars[n18];
                                    ((LuaCallFrame)object3).setLocalVarToStack(string, ((LuaCallFrame)object3).localBase + n15);
                                }
                            }
                            ((LuaCallFrame)object3).stackClear(n15, n16);
                            break;
                        }
                        case 4: {
                            int n19 = KahluaThread.getA8(n2);
                            int n20 = KahluaThread.getB9(n2);
                            UpValue upValue = luaClosure.upvalues[n20];
                            if (Core.bDebug) {
                                boolean bl11;
                                int n21 = ((LuaCallFrame)object3).closure.prototype.lines[((LuaCallFrame)object3).pc - 1];
                                boolean bl12 = bl11 = ((LuaCallFrame)object3).closure.prototype.lines[((LuaCallFrame)object3).pc] != n21;
                                if (this == LuaManager.thread && ((LuaCallFrame)object3).closure.prototype.locvarlines != null) {
                                    while (n21 > ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] && ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] != 0) {
                                        ++((LuaCallFrame)object3).localsAssigned;
                                    }
                                }
                                if (bl11 && this == LuaManager.thread && ((LuaCallFrame)object3).closure.prototype.locvarlines != null && ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] == n21) {
                                    int n22;
                                    ++((LuaCallFrame)object3).localsAssigned;
                                    String string = ((LuaCallFrame)object3).closure.prototype.locvars[n22];
                                    if (string.equals("group")) {
                                        boolean bl13 = false;
                                    }
                                    ((LuaCallFrame)object3).setLocalVarToStack(string, ((LuaCallFrame)object3).localBase + n19);
                                }
                            }
                            ((LuaCallFrame)object3).set(n19, upValue.getValue());
                            break;
                        }
                        case 5: {
                            int n23 = KahluaThread.getA8(n2);
                            int n24 = KahluaThread.getBx(n2);
                            Object object6 = this.tableget(luaClosure.env, prototype.constants[n24]);
                            if (Core.bDebug) {
                                boolean bl14;
                                int n25 = ((LuaCallFrame)object3).closure.prototype.lines[((LuaCallFrame)object3).pc - 1];
                                boolean bl15 = bl14 = ((LuaCallFrame)object3).closure.prototype.lines[((LuaCallFrame)object3).pc] != n25;
                                if (this == LuaManager.thread && ((LuaCallFrame)object3).closure.prototype.locvarlines != null) {
                                    while (n25 > ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] && ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] != 0) {
                                        ++((LuaCallFrame)object3).localsAssigned;
                                    }
                                }
                                if (bl14 && this == LuaManager.thread && ((LuaCallFrame)object3).closure.prototype.locvarlines != null && ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] == n25) {
                                    int n26;
                                    ++((LuaCallFrame)object3).localsAssigned;
                                    String string = ((LuaCallFrame)object3).closure.prototype.locvars[n26];
                                    if (string.equals("group")) {
                                        boolean bl16 = false;
                                    }
                                    ((LuaCallFrame)object3).setLocalVarToStack(string, ((LuaCallFrame)object3).localBase + n23);
                                }
                            }
                            ((LuaCallFrame)object3).set(n23, object6);
                            break;
                        }
                        case 6: {
                            Object object7;
                            int n27 = KahluaThread.getA8(n2);
                            int n28 = KahluaThread.getB9(n2);
                            int n29 = KahluaThread.getC9(n2);
                            Object object8 = ((LuaCallFrame)object3).get(n28);
                            Object object9 = this.getRegisterOrConstant((LuaCallFrame)object3, n29, prototype);
                            Object object10 = this.tableget(object8, object9);
                            if (Core.bDebug) {
                                boolean bl17;
                                int n30 = ((LuaCallFrame)object3).closure.prototype.lines[((LuaCallFrame)object3).pc - 1];
                                boolean bl18 = bl17 = ((LuaCallFrame)object3).closure.prototype.lines[((LuaCallFrame)object3).pc] != n30;
                                if (this == LuaManager.thread && ((LuaCallFrame)object3).closure.prototype.locvarlines != null) {
                                    while (n30 > ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] && ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] != 0) {
                                        ++((LuaCallFrame)object3).localsAssigned;
                                    }
                                }
                                if (bl17 && this == LuaManager.thread && ((LuaCallFrame)object3).closure.prototype.locvarlines != null && ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] == n30) {
                                    int n31;
                                    ++((LuaCallFrame)object3).localsAssigned;
                                    object7 = ((LuaCallFrame)object3).closure.prototype.locvars[n31];
                                    ((LuaCallFrame)object3).setLocalVarToStack((String)object7, ((LuaCallFrame)object3).localBase + n27);
                                }
                            }
                            ((LuaCallFrame)object3).set(n27, object10);
                            break;
                        }
                        case 7: {
                            int n32 = KahluaThread.getA8(n2);
                            int n33 = KahluaThread.getBx(n2);
                            Object object11 = ((LuaCallFrame)object3).get(n32);
                            Object object12 = prototype.constants[n33];
                            if (object11 instanceof LuaClosure && object12 instanceof String) {
                                ((LuaClosure)object11).debugName = object12.toString();
                            }
                            if (LuaCompiler.rewriteEvents) {
                                Object object13 = luaClosure.env.rawget(object12);
                                if (object13 instanceof KahluaTable && object13 != object11) {
                                    KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)object13;
                                    kahluaTableImpl.setRewriteTable(object11);
                                }
                                this.tableSet(luaClosure.env, object12, object11);
                                break;
                            }
                            this.tableSet(luaClosure.env, object12, object11);
                            break;
                        }
                        case 8: {
                            int n34 = KahluaThread.getA8(n2);
                            int n35 = KahluaThread.getB9(n2);
                            UpValue upValue = luaClosure.upvalues[n35];
                            if (Core.bDebug) {
                                boolean bl19;
                                int n36 = ((LuaCallFrame)object3).closure.prototype.lines[((LuaCallFrame)object3).pc - 1];
                                boolean bl20 = bl19 = ((LuaCallFrame)object3).closure.prototype.lines[((LuaCallFrame)object3).pc] != n36;
                                if (this == LuaManager.thread && ((LuaCallFrame)object3).closure.prototype.locvarlines != null) {
                                    while (n36 > ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] && ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] != 0) {
                                        ++((LuaCallFrame)object3).localsAssigned;
                                    }
                                }
                                if (bl19 && this == LuaManager.thread && ((LuaCallFrame)object3).closure.prototype.locvarlines != null && ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] == n36) {
                                    int n37;
                                    ++((LuaCallFrame)object3).localsAssigned;
                                    String string = ((LuaCallFrame)object3).closure.prototype.locvars[n37];
                                    ((LuaCallFrame)object3).setLocalVarToStack(string, ((LuaCallFrame)object3).localBase + n34);
                                }
                            }
                            upValue.setValue(((LuaCallFrame)object3).get(n34));
                            break;
                        }
                        case 9: {
                            int n38 = KahluaThread.getA8(n2);
                            int n39 = KahluaThread.getB9(n2);
                            int n40 = KahluaThread.getC9(n2);
                            Object object14 = ((LuaCallFrame)object3).get(n38);
                            Object object15 = this.getRegisterOrConstant((LuaCallFrame)object3, n39, prototype);
                            Object object16 = this.getRegisterOrConstant((LuaCallFrame)object3, n40, prototype);
                            this.tableSet(object14, object15, object16);
                            break;
                        }
                        case 10: {
                            int n41 = KahluaThread.getA8(n2);
                            KahluaTable kahluaTable = this.platform.newTable();
                            if (Core.bDebug) {
                                boolean bl21;
                                int n42 = ((LuaCallFrame)object3).closure.prototype.lines[((LuaCallFrame)object3).pc - 1];
                                boolean bl22 = bl21 = ((LuaCallFrame)object3).closure.prototype.lines[((LuaCallFrame)object3).pc] != n42;
                                if (this == LuaManager.thread && ((LuaCallFrame)object3).closure.prototype.locvarlines != null) {
                                    while (n42 > ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] && ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] != 0) {
                                        ++((LuaCallFrame)object3).localsAssigned;
                                    }
                                }
                                if (bl21 && this == LuaManager.thread && ((LuaCallFrame)object3).closure.prototype.locvarlines != null && ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] == n42) {
                                    int n43;
                                    ++((LuaCallFrame)object3).localsAssigned;
                                    String string = ((LuaCallFrame)object3).closure.prototype.locvars[n43];
                                    ((LuaCallFrame)object3).setLocalVarToStack(string, ((LuaCallFrame)object3).localBase + n41);
                                }
                            }
                            ((LuaCallFrame)object3).set(n41, kahluaTable);
                            break;
                        }
                        case 11: {
                            int n44 = KahluaThread.getA8(n2);
                            int n45 = KahluaThread.getB9(n2);
                            int n46 = KahluaThread.getC9(n2);
                            Object object17 = this.getRegisterOrConstant((LuaCallFrame)object3, n46, prototype);
                            Object object18 = ((LuaCallFrame)object3).get(n45);
                            LastCallFrame = object3;
                            Object object19 = this.tableget(object18, object17);
                            ((LuaCallFrame)object3).set(n44, object19);
                            ((LuaCallFrame)object3).set(n44 + 1, object18);
                            bl = false;
                            break;
                        }
                        case 12: 
                        case 13: 
                        case 14: 
                        case 15: 
                        case 16: 
                        case 17: {
                            Object object7;
                            int n47 = KahluaThread.getA8(n2);
                            int n48 = KahluaThread.getB9(n2);
                            int n49 = KahluaThread.getC9(n2);
                            Object object20 = this.getRegisterOrConstant((LuaCallFrame)object3, n48, prototype);
                            Object object21 = this.getRegisterOrConstant((LuaCallFrame)object3, n49, prototype);
                            Double d = null;
                            Double d2 = null;
                            Object object22 = null;
                            d = KahluaUtil.rawTonumber(object20);
                            if (d == null || (d2 = KahluaUtil.rawTonumber(object21)) == null) {
                                String string = meta_ops[n3];
                                object7 = this.getBinMetaOp(object20, object21, string);
                                if (object7 == null) {
                                    this.doStacktraceProper((LuaCallFrame)object3);
                                    String string2 = "unknown";
                                    if (luaClosure.debugName != null) {
                                        string2 = luaClosure.debugName;
                                    } else if (prototype.name != null) {
                                        string2 = prototype.name;
                                    }
                                    KahluaUtil.fail(string + " not defined for operands in " + string2);
                                }
                                object22 = this.call(object7, object20, object21, null);
                            } else {
                                object22 = this.primitiveMath(d, d2, n3);
                            }
                            if (Core.bDebug) {
                                boolean bl23;
                                int n50 = ((LuaCallFrame)object3).closure.prototype.lines[((LuaCallFrame)object3).pc - 1];
                                boolean bl24 = bl23 = ((LuaCallFrame)object3).closure.prototype.lines[((LuaCallFrame)object3).pc] != n50;
                                if (this == LuaManager.thread && ((LuaCallFrame)object3).closure.prototype.locvarlines != null) {
                                    while (n50 > ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] && ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] != 0) {
                                        ++((LuaCallFrame)object3).localsAssigned;
                                    }
                                }
                                if (bl23 && this == LuaManager.thread && ((LuaCallFrame)object3).closure.prototype.locvarlines != null && ((LuaCallFrame)object3).closure.prototype.locvarlines[((LuaCallFrame)object3).localsAssigned] == n50) {
                                    int n51;
                                    ++((LuaCallFrame)object3).localsAssigned;
                                    String string = ((LuaCallFrame)object3).closure.prototype.locvars[n51];
                                    ((LuaCallFrame)object3).setLocalVarToStack(string, ((LuaCallFrame)object3).localBase + n47);
                                }
                            }
                            ((LuaCallFrame)object3).set(n47, object22);
                            break;
                        }
                        case 18: {
                            Object object23;
                            int n52 = KahluaThread.getA8(n2);
                            int n53 = KahluaThread.getB9(n2);
                            Object object24 = ((LuaCallFrame)object3).get(n53);
                            Double d = KahluaUtil.rawTonumber(object24);
                            if (d != null) {
                                object23 = KahluaUtil.toDouble(-KahluaUtil.fromDouble(d));
                            } else {
                                Object object25 = this.getMetaOp(object24, "__unm");
                                object23 = this.call(object25, object24, null, null);
                            }
                            ((LuaCallFrame)object3).set(n52, object23);
                            break;
                        }
                        case 19: {
                            int n54 = KahluaThread.getA8(n2);
                            int n55 = KahluaThread.getB9(n2);
                            Object object26 = ((LuaCallFrame)object3).get(n55);
                            ((LuaCallFrame)object3).set(n54, KahluaUtil.toBoolean(!KahluaUtil.boolEval(object26)));
                            bl = false;
                            break;
                        }
                        case 20: {
                            Object object27;
                            int n56 = KahluaThread.getA8(n2);
                            int n57 = KahluaThread.getB9(n2);
                            Object object28 = ((LuaCallFrame)object3).get(n57);
                            if (object28 instanceof KahluaTable) {
                                var17_150 = (KahluaTable)object28;
                                object27 = KahluaUtil.toDouble(var17_150.len());
                            } else if (object28 instanceof String) {
                                var17_150 = (String)object28;
                                object27 = KahluaUtil.toDouble(((String)var17_150).length());
                            } else {
                                var17_150 = this.getMetaOp(object28, "__len");
                                if (var17_150 == null) {
                                    this.doStacktraceProper((LuaCallFrame)object3);
                                }
                                KahluaUtil.luaAssert(var17_150 != null, "__len not defined for operand");
                                object27 = this.call(var17_150, object28, null, null);
                            }
                            ((LuaCallFrame)object3).set(n56, object27);
                            bl = false;
                            break;
                        }
                        case 21: {
                            Object object7;
                            int n58 = KahluaThread.getA8(n2);
                            int n59 = KahluaThread.getB9(n2);
                            int n60 = KahluaThread.getC9(n2);
                            int n61 = n59;
                            int n62 = n60;
                            Object object29 = ((LuaCallFrame)object3).get(n62);
                            --n62;
                            while (n61 <= n62) {
                                Object object30 = KahluaUtil.rawTostring(object29);
                                if (object30 != null) {
                                    int n63 = 0;
                                    int n64 = n62;
                                    while (n61 <= n64) {
                                        object7 = ((LuaCallFrame)object3).get(n64);
                                        --n64;
                                        if (KahluaUtil.rawTostring(object7) == null) break;
                                        ++n63;
                                    }
                                    if (n63 > 0) {
                                        object7 = new StringBuilder();
                                        for (int i = n62 - n63 + 1; i <= n62; ++i) {
                                            ((StringBuilder)object7).append(KahluaUtil.rawTostring(((LuaCallFrame)object3).get(i)));
                                        }
                                        ((StringBuilder)object7).append((String)object30);
                                        object29 = ((StringBuilder)object7).toString();
                                        n62 -= n63;
                                    }
                                }
                                if (n61 > n62) continue;
                                object30 = ((LuaCallFrame)object3).get(n62);
                                Object object31 = this.getBinMetaOp(object30, object29, "__concat");
                                if (object31 == null) {
                                    KahluaUtil.fail("__concat not defined for operands: " + object30 + " and " + object29);
                                }
                                object29 = this.call(object31, object30, object29, null);
                                --n62;
                            }
                            ((LuaCallFrame)object3).set(n58, object29);
                            bl = false;
                            break;
                        }
                        case 22: {
                            ((LuaCallFrame)object3).pc += KahluaThread.getSBx(n2);
                            break;
                        }
                        case 23: 
                        case 24: 
                        case 25: {
                            Object object7;
                            int n65 = KahluaThread.getA8(n2);
                            int n66 = KahluaThread.getB9(n2);
                            int n67 = KahluaThread.getC9(n2);
                            Object object32 = this.getRegisterOrConstant((LuaCallFrame)object3, n66, prototype);
                            Object object33 = this.getRegisterOrConstant((LuaCallFrame)object3, n67, prototype);
                            if (object32 instanceof Double && object33 instanceof Double) {
                                double d = KahluaUtil.fromDouble(object32);
                                double d3 = KahluaUtil.fromDouble(object33);
                                if (n3 == 23) {
                                    if (d == d3 == (n65 == 0)) {
                                        ++((LuaCallFrame)object3).pc;
                                    }
                                } else if (n3 == 24) {
                                    if (d < d3 == (n65 == 0)) {
                                        ++((LuaCallFrame)object3).pc;
                                    }
                                } else if (d <= d3 == (n65 == 0)) {
                                    ++((LuaCallFrame)object3).pc;
                                }
                            } else if (object32 instanceof String && object33 instanceof String) {
                                if (n3 == 23) {
                                    if (object32.equals(object33) == (n65 == 0)) {
                                        ++((LuaCallFrame)object3).pc;
                                    }
                                } else {
                                    String string = (String)object32;
                                    String string3 = (String)object33;
                                    int n68 = string.compareTo(string3);
                                    if (n3 == 24) {
                                        if (n68 < 0 == (n65 == 0)) {
                                            ++((LuaCallFrame)object3).pc;
                                        }
                                    } else if (n68 <= 0 == (n65 == 0)) {
                                        ++((LuaCallFrame)object3).pc;
                                    }
                                }
                            } else {
                                boolean bl25;
                                if (object32 == object33 && n3 == 23) {
                                    bl25 = true;
                                } else {
                                    boolean bl26 = false;
                                    String string = meta_ops[n3];
                                    Object object34 = this.getCompMetaOp(object32, object33, string);
                                    if (object34 == null && n3 == 25) {
                                        object34 = this.getCompMetaOp(object32, object33, "__lt");
                                        object7 = object32;
                                        object32 = object33;
                                        object33 = object7;
                                        bl26 = true;
                                    }
                                    if (object34 == null && n3 == 23) {
                                        bl25 = BaseLib.luaEquals(object32, object33);
                                    } else {
                                        if (object34 == null) {
                                            this.doStacktraceProper((LuaCallFrame)object3);
                                            KahluaUtil.fail(string + " not defined for operand");
                                        }
                                        object7 = this.call(object34, object32, object33, null);
                                        bl25 = KahluaUtil.boolEval(object7);
                                    }
                                    if (bl26) {
                                        bl25 = !bl25;
                                    }
                                }
                                if (bl25 == (n65 == 0)) {
                                    ++((LuaCallFrame)object3).pc;
                                }
                            }
                            bl = false;
                            break;
                        }
                        case 26: {
                            int n69 = KahluaThread.getA8(n2);
                            int n70 = KahluaThread.getC9(n2);
                            Object object35 = ((LuaCallFrame)object3).get(n69);
                            if (KahluaUtil.boolEval(object35) != (n70 == 0)) break;
                            ++((LuaCallFrame)object3).pc;
                            break;
                        }
                        case 27: {
                            int n71 = KahluaThread.getA8(n2);
                            int n72 = KahluaThread.getB9(n2);
                            int n73 = KahluaThread.getC9(n2);
                            Object object36 = ((LuaCallFrame)object3).get(n72);
                            if (KahluaUtil.boolEval(object36) != (n73 == 0)) {
                                ((LuaCallFrame)object3).set(n71, object36);
                                break;
                            }
                            ++((LuaCallFrame)object3).pc;
                            break;
                        }
                        case 28: {
                            Object object37;
                            Object object7;
                            int n74 = KahluaThread.getA8(n2);
                            int n75 = KahluaThread.getB9(n2);
                            int n76 = KahluaThread.getC9(n2);
                            int n77 = n75 - 1;
                            if (n77 != -1) {
                                ((LuaCallFrame)object3).setTop(n74 + n77 + 1);
                            } else {
                                n77 = ((LuaCallFrame)object3).getTop() - n74 - 1;
                            }
                            ((LuaCallFrame)object3).restoreTop = n76 != 0;
                            int n78 = ((LuaCallFrame)object3).localBase;
                            int n79 = n78 + n74 + 1;
                            int n80 = n78 + n74;
                            Object object38 = ((LuaCallFrame)object3).get(n74);
                            if (object38 == null) {
                                boolean bl27 = false;
                                object38 = ((LuaCallFrame)object3).get(n74);
                            }
                            if (object38 == null) {
                                this.doStacktraceProper((LuaCallFrame)object3);
                                if (((LuaCallFrame)object3).getClosure().debugName != null) {
                                    KahluaUtil.fail("Object tried to call nil in " + ((LuaCallFrame)object3).getClosure().debugName);
                                } else if (((LuaCallFrame)object3).getClosure().prototype != null && ((LuaCallFrame)object3).getClosure().prototype.name != null) {
                                    KahluaUtil.fail("Object tried to call nil in " + ((LuaCallFrame)object3).getClosure().prototype.name);
                                } else {
                                    KahluaUtil.fail("Object tried to call nil in unknown");
                                }
                            }
                            if ((object37 = this.prepareMetatableCall(object38)) == null) {
                                KahluaUtil.fail("Object " + object38 + " did not have __call metatable set");
                            }
                            if (object37 != object38) {
                                n79 = n80;
                                ++n77;
                            }
                            if (object37 instanceof LuaClosure) {
                                object7 = this.currentCoroutine.pushNewCallFrame((LuaClosure)object37, null, n79, n80, n77, true, ((LuaCallFrame)object3).canYield);
                                ((LuaCallFrame)object7).init();
                                object3 = object7;
                                luaClosure = ((LuaCallFrame)object7).closure;
                                prototype = luaClosure.prototype;
                                nArray = prototype.code;
                                n = ((LuaCallFrame)object3).returnBase;
                                break;
                            }
                            if (object37 instanceof JavaFunction) {
                                this.callJava((JavaFunction)object37, n79, n80, n77);
                                object3 = this.currentCoroutine.currentCallFrame();
                                if (object3 == null || ((LuaCallFrame)object3).isJava()) {
                                    long l2 = System.nanoTime();
                                    return;
                                }
                                luaClosure = ((LuaCallFrame)object3).closure;
                                prototype = luaClosure.prototype;
                                nArray = prototype.code;
                                n = ((LuaCallFrame)object3).returnBase;
                                if (!((LuaCallFrame)object3).restoreTop) break;
                                ((LuaCallFrame)object3).setTop(prototype.maxStacksize);
                                break;
                            }
                            throw new RuntimeException("Tried to call a non-function: " + object37);
                        }
                        case 29: {
                            int n81 = ((LuaCallFrame)object3).localBase;
                            this.currentCoroutine.closeUpvalues(n81);
                            int n82 = KahluaThread.getA8(n2);
                            int n83 = KahluaThread.getB9(n2);
                            int n84 = n83 - 1;
                            if (n84 == -1) {
                                n84 = ((LuaCallFrame)object3).getTop() - n82 - 1;
                            }
                            ((LuaCallFrame)object3).restoreTop = false;
                            Object object39 = ((LuaCallFrame)object3).get(n82);
                            try {
                                KahluaUtil.luaAssert(object39 != null, "Tried to call nil");
                            }
                            catch (Exception exception) {
                                if (Core.bDebug && UIManager.defaultthread == LuaManager.thread) {
                                    UIManager.debugBreakpoint((String)LuaManager.thread.currentfile, (long)(LuaManager.thread.currentLine - 1));
                                }
                                this.debugException(exception);
                                this.doStacktraceProper((LuaCallFrame)object3);
                                KahluaUtil.fail("");
                            }
                            Object object40 = this.prepareMetatableCall(object39);
                            if (object40 == null) {
                                KahluaUtil.fail("Object did not have __call metatable set");
                            }
                            int n85 = n + 1;
                            if (object40 != object39) {
                                n85 = n;
                                ++n84;
                            }
                            this.currentCoroutine.stackCopy(n81 + n82, n, n84 + 1);
                            this.currentCoroutine.setTop(n + n84 + 1);
                            if (object40 instanceof LuaClosure) {
                                ((LuaCallFrame)object3).localBase = n85;
                                ((LuaCallFrame)object3).nArguments = n84;
                                ((LuaCallFrame)object3).closure = (LuaClosure)object40;
                                ((LuaCallFrame)object3).init();
                            } else {
                                if (!(object40 instanceof JavaFunction)) {
                                    KahluaUtil.fail("Tried to call a non-function: " + object40);
                                }
                                Coroutine coroutine = this.currentCoroutine;
                                this.callJava((JavaFunction)object40, n85, n, n84);
                                object3 = this.currentCoroutine.currentCallFrame();
                                coroutine.popCallFrame();
                                if (coroutine != this.currentCoroutine) {
                                    if (coroutine.isDead() && coroutine != this.rootCoroutine && this.currentCoroutine.getParent() == coroutine) {
                                        this.currentCoroutine.resume(coroutine.getParent());
                                        coroutine.destroy();
                                        this.currentCoroutine.getParent().currentCallFrame().push(Boolean.TRUE);
                                    }
                                    if (((LuaCallFrame)(object3 = this.currentCoroutine.currentCallFrame())).isJava()) {
                                        long l3 = System.nanoTime();
                                        return;
                                    }
                                } else {
                                    if (!((LuaCallFrame)object3).fromLua) {
                                        long l4 = System.nanoTime();
                                        return;
                                    }
                                    object3 = this.currentCoroutine.currentCallFrame();
                                    if (((LuaCallFrame)object3).restoreTop) {
                                        ((LuaCallFrame)object3).setTop(((LuaCallFrame)object3).closure.prototype.maxStacksize);
                                    }
                                }
                            }
                            luaClosure = ((LuaCallFrame)object3).closure;
                            prototype = luaClosure.prototype;
                            nArray = prototype.code;
                            n = ((LuaCallFrame)object3).returnBase;
                            break;
                        }
                        case 30: {
                            int n86 = KahluaThread.getA8(n2);
                            int n87 = KahluaThread.getB9(n2) - 1;
                            int n88 = ((LuaCallFrame)object3).localBase;
                            this.currentCoroutine.closeUpvalues(n88);
                            if (n87 == -1) {
                                n87 = ((LuaCallFrame)object3).getTop() - n86;
                            }
                            this.currentCoroutine.stackCopy(((LuaCallFrame)object3).localBase + n86, n, n87);
                            this.currentCoroutine.setTop(n + n87);
                            if (((LuaCallFrame)object3).fromLua) {
                                if (((LuaCallFrame)object3).canYield && this.currentCoroutine.atBottom()) {
                                    ((LuaCallFrame)object3).localBase = ((LuaCallFrame)object3).returnBase;
                                    Coroutine coroutine = this.currentCoroutine;
                                    Coroutine.yieldHelper((LuaCallFrame)object3, (LuaCallFrame)object3, n87);
                                    coroutine.popCallFrame();
                                    object3 = this.currentCoroutine.currentCallFrame();
                                    if (object3 == null || ((LuaCallFrame)object3).isJava()) {
                                        return;
                                    }
                                } else {
                                    this.currentCoroutine.popCallFrame();
                                }
                                object3 = this.currentCoroutine.currentCallFrame();
                                luaClosure = ((LuaCallFrame)object3).closure;
                                prototype = luaClosure.prototype;
                                nArray = prototype.code;
                                n = ((LuaCallFrame)object3).returnBase;
                                if (!((LuaCallFrame)object3).restoreTop) break;
                                ((LuaCallFrame)object3).setTop(prototype.maxStacksize);
                                break;
                            }
                            this.currentCoroutine.popCallFrame();
                            long l5 = System.nanoTime();
                            return;
                        }
                        case 32: {
                            int n89 = KahluaThread.getA8(n2);
                            int n90 = KahluaThread.getSBx(n2);
                            double d = KahluaUtil.fromDouble(((LuaCallFrame)object3).get(n89));
                            double d4 = KahluaUtil.fromDouble(((LuaCallFrame)object3).get(n89 + 2));
                            ((LuaCallFrame)object3).set(n89, KahluaUtil.toDouble(d - d4));
                            ((LuaCallFrame)object3).pc += n90;
                            break;
                        }
                        case 31: {
                            int n91 = KahluaThread.getA8(n2);
                            double d = KahluaUtil.fromDouble(((LuaCallFrame)object3).get(n91));
                            double d5 = KahluaUtil.fromDouble(((LuaCallFrame)object3).get(n91 + 1));
                            double d6 = KahluaUtil.fromDouble(((LuaCallFrame)object3).get(n91 + 2));
                            Object object7 = KahluaUtil.toDouble(d += d6);
                            ((LuaCallFrame)object3).set(n91, object7);
                            if (d6 > 0.0 ? d <= d5 : d >= d5) {
                                int n92 = KahluaThread.getSBx(n2);
                                ((LuaCallFrame)object3).pc += n92;
                                ((LuaCallFrame)object3).set(n91 + 3, object7);
                                break;
                            }
                            ((LuaCallFrame)object3).clearFromIndex(n91);
                            break;
                        }
                        case 33: {
                            int n93 = KahluaThread.getA8(n2);
                            int n94 = KahluaThread.getC9(n2);
                            ((LuaCallFrame)object3).setTop(n93 + 6);
                            ((LuaCallFrame)object3).stackCopy(n93, n93 + 3, 3);
                            this.call(2);
                            ((LuaCallFrame)object3).clearFromIndex(n93 + 3 + n94);
                            ((LuaCallFrame)object3).setPrototypeStacksize();
                            Object object41 = ((LuaCallFrame)object3).get(n93 + 3);
                            if (object41 != null) {
                                ((LuaCallFrame)object3).set(n93 + 2, object41);
                                break;
                            }
                            ++((LuaCallFrame)object3).pc;
                            break;
                        }
                        case 34: {
                            int n95 = KahluaThread.getA8(n2);
                            int n96 = KahluaThread.getB9(n2);
                            int n97 = KahluaThread.getC9(n2);
                            if (n96 == 0) {
                                n96 = ((LuaCallFrame)object3).getTop() - n95 - 1;
                            }
                            if (n97 == 0) {
                                n97 = nArray[((LuaCallFrame)object3).pc++];
                            }
                            int n98 = (n97 - 1) * 50;
                            KahluaTable kahluaTable = (KahluaTable)((LuaCallFrame)object3).get(n95);
                            for (int i = 1; i <= n96; ++i) {
                                Double d = KahluaUtil.toDouble(n98 + i);
                                Object object42 = ((LuaCallFrame)object3).get(n95 + i);
                                kahluaTable.rawset(d, object42);
                            }
                            break;
                        }
                        case 35: {
                            int n99 = KahluaThread.getA8(n2);
                            ((LuaCallFrame)object3).closeUpvalues(n99);
                            break;
                        }
                        case 36: {
                            int n100 = KahluaThread.getA8(n2);
                            int n101 = KahluaThread.getBx(n2);
                            Prototype prototype2 = prototype.prototypes[n101];
                            LuaClosure luaClosure2 = new LuaClosure(prototype2, luaClosure.env);
                            ((LuaCallFrame)object3).set(n100, luaClosure2);
                            int n102 = prototype2.numUpvalues;
                            block56: for (int i = 0; i < n102; ++i) {
                                n2 = nArray[((LuaCallFrame)object3).pc++];
                                n3 = n2 & 0x3F;
                                n101 = KahluaThread.getB9(n2);
                                switch (n3) {
                                    case 0: {
                                        luaClosure2.upvalues[i] = ((LuaCallFrame)object3).findUpvalue(n101);
                                        continue block56;
                                    }
                                    case 4: {
                                        luaClosure2.upvalues[i] = luaClosure.upvalues[n101];
                                        continue block56;
                                    }
                                }
                            }
                            break;
                        }
                        case 37: {
                            int n103 = KahluaThread.getA8(n2);
                            int n104 = KahluaThread.getB9(n2) - 1;
                            ((LuaCallFrame)object3).pushVarargs(n103, n104);
                            break;
                        }
                    }
                }
                catch (RuntimeException runtimeException) {
                    if (!Core.bDebug || UIManager.defaultthread == LuaManager.thread) {
                        // empty if block
                    }
                    if (runtimeException.getMessage() != null) {
                        ExceptionLogger.logException((Throwable)runtimeException);
                        this.debugException(runtimeException);
                    }
                    this.doStacktraceProper((LuaCallFrame)object3);
                    KahluaUtil.fail("");
                    boolean bl28 = true;
                    do {
                        if ((object3 = this.currentCoroutine.currentCallFrame()) == null) {
                            object5 = this.currentCoroutine.getParent();
                            if (object5 == null) break;
                            LuaCallFrame luaCallFrame = ((Coroutine)object5).currentCallFrame();
                            luaCallFrame.push(Boolean.FALSE);
                            luaCallFrame.push(runtimeException.getMessage());
                            luaCallFrame.push(this.currentCoroutine.stackTrace);
                            this.currentCoroutine.destroy();
                            this.currentCoroutine = object5;
                            object3 = this.currentCoroutine.currentCallFrame();
                            luaClosure = ((LuaCallFrame)object3).closure;
                            prototype = luaClosure.prototype;
                            nArray = prototype.code;
                            n = ((LuaCallFrame)object3).returnBase;
                            bl28 = false;
                            break;
                        }
                        this.currentCoroutine.addStackTrace((LuaCallFrame)object3);
                        this.currentCoroutine.popCallFrame();
                    } while (((LuaCallFrame)object3).fromLua);
                    if (object3 != null) {
                        ((LuaCallFrame)object3).closeUpvalues(0);
                    }
                    if (bl28) {
                        throw runtimeException;
                    }
                }
                catch (Exception exception) {
                    if (Core.bDebug && UIManager.defaultthread == LuaManager.thread) {
                        UIManager.debugBreakpoint((String)LuaManager.thread.currentfile, (long)(LuaManager.thread.currentLine - 1));
                    }
                    if (exception.getMessage() == null) break block184;
                    System.out.printf(exception.getMessage(), new Object[0]);
                }
            }
            if (!this.bReset) continue;
            throw new RuntimeException("lua was reset");
        }
        long l6 = System.nanoTime();
        this.DoProfileTiming((String)object4, l, l6);
    }

    private void DoProfileTiming(String string, long l, long l2) {
        if (!this.doProfiling) {
            return;
        }
        double d = (double)(l2 - l) / 1000000.0;
        if (GameWindow.states.current != IngameState.instance) {
            return;
        }
        Entry entry = null;
        if (this.profileEntryMap.containsKey(string)) {
            entry = this.profileEntryMap.get(string);
        } else {
            entry = new Entry();
            this.profileEntryMap.put(string, entry);
            this.profileEntries.add(entry);
            entry.file = string;
        }
        entry.time += d;
        Collections.sort(this.profileEntries, new ProfileEntryComparitor());
    }

    public StringBuilder startErrorMessage() {
        this.m_stringBuilder.setLength(0);
        return this.m_stringBuilder;
    }

    public void flushErrorMessage() {
        String string = this.m_stringBuilder.toString();
        DebugLog.log((String)string);
        while (m_errors_list.size() >= 40) {
            m_errors_list.remove(0);
        }
        m_errors_list.add(string);
        ++m_error_count;
    }

    public void doStacktraceProper(LuaCallFrame luaCallFrame) {
        if (luaCallFrame == null) {
            return;
        }
        StringBuilder stringBuilder = this.startErrorMessage();
        stringBuilder.append("-----------------------------------------\n");
        stringBuilder.append("STACK TRACE\n");
        stringBuilder.append("-----------------------------------------\n");
        int n = luaCallFrame.coroutine.getCallframeTop();
        for (int i = n - 1; i >= 0; --i) {
            LuaCallFrame luaCallFrame2 = luaCallFrame.coroutine.getCallFrame(i);
            stringBuilder.append(luaCallFrame2.toString2());
            stringBuilder.append("\n");
        }
        this.flushErrorMessage();
    }

    public void doStacktraceProper() {
        LuaCallFrame luaCallFrame = this.currentCoroutine.currentCallFrame();
        this.doStacktraceProper(luaCallFrame);
    }

    public void debugException(Exception exception) {
        this.m_stringWriter.getBuffer().setLength(0);
        exception.printStackTrace(this.m_printWriter);
        String string = this.m_stringWriter.toString();
        m_errors_list.add(string);
        ++m_error_count;
    }

    protected Object getMetaOp(Object object, String string) {
        KahluaTable kahluaTable = (KahluaTable)this.getmetatable(object, true);
        if (kahluaTable == null) {
            return null;
        }
        return kahluaTable.rawget(string);
    }

    private final Object getCompMetaOp(Object object, Object object2, String string) {
        Object object3;
        KahluaTable kahluaTable = (KahluaTable)this.getmetatable(object, true);
        KahluaTable kahluaTable2 = (KahluaTable)this.getmetatable(object2, true);
        if (kahluaTable == null || kahluaTable2 == null) {
            return null;
        }
        Object object4 = kahluaTable.rawget(string);
        if (object4 != (object3 = kahluaTable2.rawget(string)) || object4 == null) {
            return null;
        }
        return object4;
    }

    private final Object getBinMetaOp(Object object, Object object2, String string) {
        Object object3 = this.getMetaOp(object, string);
        if (object3 != null) {
            return object3;
        }
        return this.getMetaOp(object2, string);
    }

    private final Object getRegisterOrConstant(LuaCallFrame luaCallFrame, int n, Prototype prototype) {
        int n2 = n - 256;
        if (n2 < 0) {
            return luaCallFrame.get(n);
        }
        return prototype.constants[n2];
    }

    private static final int getA8(int n) {
        return n >>> 6 & 0xFF;
    }

    private static final int getC9(int n) {
        return n >>> 14 & 0x1FF;
    }

    private static final int getB9(int n) {
        return n >>> 23 & 0x1FF;
    }

    private static final int getBx(int n) {
        return n >>> 14;
    }

    private static final int getSBx(int n) {
        return (n >>> 14) - 131071;
    }

    private Double primitiveMath(Double d, Double d2, int n) {
        double d3 = KahluaUtil.fromDouble(d);
        double d4 = KahluaUtil.fromDouble(d2);
        double d5 = 0.0;
        switch (n) {
            case 12: {
                d5 = d3 + d4;
                break;
            }
            case 13: {
                d5 = d3 - d4;
                break;
            }
            case 14: {
                d5 = d3 * d4;
                break;
            }
            case 15: {
                d5 = d3 / d4;
                break;
            }
            case 16: {
                if (d4 == 0.0) {
                    d5 = Double.NaN;
                    break;
                }
                int n2 = (int)(d3 / d4);
                d5 = d3 - (double)n2 * d4;
                break;
            }
            case 17: {
                d5 = this.platform.pow(d3, d4);
                break;
            }
        }
        return KahluaUtil.toDouble(d5);
    }

    public Object call(Object object, Object object2, Object object3, Object object4) {
        int n = this.currentCoroutine.getTop();
        this.currentCoroutine.setTop(n + 1 + 3);
        this.currentCoroutine.objectStack[n] = object;
        this.currentCoroutine.objectStack[n + 1] = object2;
        this.currentCoroutine.objectStack[n + 2] = object3;
        this.currentCoroutine.objectStack[n + 3] = object4;
        int n2 = this.call(3);
        Object object5 = null;
        if (n2 >= 1) {
            object5 = this.currentCoroutine.objectStack[n];
        }
        this.currentCoroutine.setTop(n);
        return object5;
    }

    public Object call(Object object, Object[] objectArray) {
        int n;
        int n2 = this.currentCoroutine.getTop();
        int n3 = objectArray == null ? 0 : objectArray.length;
        this.currentCoroutine.setTop(n2 + 1 + n3);
        this.currentCoroutine.objectStack[n2] = object;
        for (n = 1; n <= n3; ++n) {
            this.currentCoroutine.objectStack[n2 + n] = objectArray[n - 1];
        }
        n = this.call(n3);
        Object object2 = null;
        if (n >= 1) {
            object2 = this.currentCoroutine.objectStack[n2];
        }
        this.currentCoroutine.setTop(n2);
        return object2;
    }

    public Object tableget(Object object, Object object2) {
        Object object3 = object;
        for (int i = 100; i > 0; --i) {
            Object object4;
            Object object5;
            boolean bl = object3 instanceof KahluaTable;
            if (bl && (object5 = (object4 = (KahluaTable)object3).rawget(object2)) != null) {
                return object5;
            }
            object4 = this.getMetaOp(object3, "__index");
            if (object4 == null) {
                if (bl) {
                    return null;
                }
                object5 = this.startErrorMessage();
                ((StringBuilder)object5).append("-------------------------------------------------------------\n");
                ((StringBuilder)object5).append("attempted index: " + object2 + " of non-table: " + object3 + "\n");
                this.flushErrorMessage();
                this.doStacktraceProper(this.currentCoroutine.currentCallFrame());
                throw new RuntimeException("attempted index: " + object2 + " of non-table: " + object3);
            }
            if (object4 instanceof JavaFunction || object4 instanceof LuaClosure) {
                object5 = this.call(object4, object, object2, null);
                return object5;
            }
            object3 = object4;
        }
        throw new RuntimeException("loop in gettable");
    }

    public void tableSet(Object object, Object object2, Object object3) {
        Object object4 = object;
        for (int i = 100; i > 0; --i) {
            Object object5;
            if (object4 instanceof KahluaTable) {
                KahluaTable kahluaTable = (KahluaTable)object4;
                if (kahluaTable.rawget(object2) != null) {
                    kahluaTable.rawset(object2, object3);
                    return;
                }
                object5 = this.getMetaOp(object4, "__newindex");
                if (object5 == null) {
                    kahluaTable.rawset(object2, object3);
                    return;
                }
            } else {
                object5 = this.getMetaOp(object4, "__newindex");
                if (object5 == null) {
                    this.doStacktraceProper(this.currentCoroutine.currentCallFrame());
                }
                KahluaUtil.luaAssert(object5 != null, "attempted index of non-table");
            }
            if (object5 instanceof JavaFunction || object5 instanceof LuaClosure) {
                this.call(object5, object, object2, object3);
                return;
            }
            object4 = object5;
        }
        throw new RuntimeException("loop in settable");
    }

    public void setmetatable(Object object, KahluaTable kahluaTable) {
        KahluaUtil.luaAssert(object != null, "Can't set metatable for nil");
        if (object instanceof KahluaTable) {
            KahluaTable kahluaTable2 = (KahluaTable)object;
            kahluaTable2.setMetatable(kahluaTable);
        } else {
            KahluaUtil.fail("Could not set metatable for object");
        }
    }

    public Object getmetatable(Object object, boolean bl) {
        Object object2;
        if (object == null) {
            return null;
        }
        KahluaTable kahluaTable = null;
        if (object instanceof KahluaTable) {
            object2 = (KahluaTable)object;
            kahluaTable = object2.getMetatable();
        } else if (kahluaTable == null) {
            object2 = KahluaUtil.getClassMetatables(this.platform, this.getEnvironment());
            kahluaTable = (KahluaTable)this.tableget(object2, object.getClass());
        }
        if (!bl && kahluaTable != null && (object2 = kahluaTable.rawget("__metatable")) != null) {
            return object2;
        }
        return kahluaTable;
    }

    public Object[] pcall(Object object, Object[] objectArray) {
        int n = objectArray == null ? 0 : objectArray.length;
        Coroutine coroutine = this.currentCoroutine;
        int n2 = coroutine.getTop();
        coroutine.setTop(n2 + 1 + n);
        coroutine.objectStack[n2] = object;
        if (n > 0) {
            System.arraycopy(objectArray, 0, coroutine.objectStack, n2 + 1, n);
        }
        int n3 = this.pcall(n);
        KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
        Object[] objectArray2 = null;
        objectArray2 = objectArray.length == n3 ? objectArray : new Object[n3];
        System.arraycopy(coroutine.objectStack, n2, objectArray2, 0, n3);
        coroutine.setTop(n2);
        return objectArray2;
    }

    public void pcallvoid(Object object, Object[] objectArray) {
        int n = objectArray == null ? 0 : objectArray.length;
        Coroutine coroutine = this.currentCoroutine;
        int n2 = coroutine.getTop();
        coroutine.setTop(n2 + 1 + n);
        coroutine.objectStack[n2] = object;
        if (n > 0) {
            System.arraycopy(objectArray, 0, coroutine.objectStack, n2 + 1, n);
        }
        int n3 = this.pcall(n);
        KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
        coroutine.setTop(n2);
    }

    public void pcallvoid(Object object, Object object2) {
        Coroutine coroutine = this.currentCoroutine;
        int n = coroutine.getTop();
        coroutine.setTop(n + 1 + 1);
        coroutine.objectStack[n] = object;
        coroutine.objectStack[n + 1] = object2;
        int n2 = this.pcall(1);
        KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
        coroutine.setTop(n);
    }

    public void pcallvoid(Object object, Object object2, Object object3) {
        Coroutine coroutine = this.currentCoroutine;
        int n = coroutine.getTop();
        coroutine.setTop(n + 1 + 2);
        coroutine.objectStack[n] = object;
        coroutine.objectStack[n + 1] = object2;
        coroutine.objectStack[n + 2] = object3;
        int n2 = this.pcall(2);
        KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
        coroutine.setTop(n);
    }

    public void pcallvoid(Object object, Object object2, Object object3, Object object4) {
        Coroutine coroutine = this.currentCoroutine;
        int n = coroutine.getTop();
        coroutine.setTop(n + 1 + 3);
        coroutine.objectStack[n] = object;
        coroutine.objectStack[n + 1] = object2;
        coroutine.objectStack[n + 2] = object3;
        coroutine.objectStack[n + 3] = object4;
        int n2 = this.pcall(3);
        KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
        coroutine.setTop(n);
    }

    public Boolean pcallBoolean(Object object, Object object2) {
        Object object3;
        Boolean bl;
        Coroutine coroutine = this.currentCoroutine;
        int n = coroutine.getTop();
        coroutine.setTop(n + 1 + 1);
        coroutine.objectStack[n] = object;
        coroutine.objectStack[n + 1] = object2;
        int n2 = this.pcall(1);
        KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
        Boolean bl2 = null;
        if (n2 > 1 && (bl = (Boolean)coroutine.objectStack[n]).booleanValue() && (object3 = coroutine.objectStack[n + 1]) instanceof Boolean) {
            bl2 = (Boolean)object3 != false ? Boolean.TRUE : Boolean.FALSE;
        }
        coroutine.setTop(n);
        return bl2;
    }

    public Boolean pcallBoolean(Object object, Object object2, Object object3) {
        Object object4;
        Boolean bl;
        Coroutine coroutine = this.currentCoroutine;
        int n = coroutine.getTop();
        coroutine.setTop(n + 1 + 2);
        coroutine.objectStack[n] = object;
        coroutine.objectStack[n + 1] = object2;
        coroutine.objectStack[n + 2] = object3;
        int n2 = this.pcall(2);
        KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
        Boolean bl2 = null;
        if (n2 > 1 && (bl = (Boolean)coroutine.objectStack[n]).booleanValue() && (object4 = coroutine.objectStack[n + 1]) instanceof Boolean) {
            bl2 = (Boolean)object4 != false ? Boolean.TRUE : Boolean.FALSE;
        }
        coroutine.setTop(n);
        return bl2;
    }

    public Boolean pcallBoolean(Object object, Object object2, Object object3, Object object4) {
        Object object5;
        Boolean bl;
        Coroutine coroutine = this.currentCoroutine;
        int n = coroutine.getTop();
        coroutine.setTop(n + 1 + 3);
        coroutine.objectStack[n] = object;
        coroutine.objectStack[n + 1] = object2;
        coroutine.objectStack[n + 2] = object3;
        coroutine.objectStack[n + 3] = object4;
        int n2 = this.pcall(3);
        KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
        Boolean bl2 = null;
        if (n2 > 1 && (bl = (Boolean)coroutine.objectStack[n]).booleanValue() && (object5 = coroutine.objectStack[n + 1]) instanceof Boolean) {
            bl2 = (Boolean)object5 != false ? Boolean.TRUE : Boolean.FALSE;
        }
        coroutine.setTop(n);
        return bl2;
    }

    public Boolean pcallBoolean(Object object, Object[] objectArray) {
        Object object2;
        Boolean bl;
        int n = objectArray == null ? 0 : objectArray.length;
        Coroutine coroutine = this.currentCoroutine;
        int n2 = coroutine.getTop();
        coroutine.setTop(n2 + 1 + n);
        coroutine.objectStack[n2] = object;
        if (n > 0) {
            System.arraycopy(objectArray, 0, coroutine.objectStack, n2 + 1, n);
        }
        int n3 = this.pcall(n);
        KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
        Boolean bl2 = null;
        if (n3 > 1 && (bl = (Boolean)coroutine.objectStack[n2]).booleanValue() && (object2 = coroutine.objectStack[n2 + 1]) instanceof Boolean) {
            bl2 = (Boolean)object2 != false ? Boolean.TRUE : Boolean.FALSE;
        }
        coroutine.setTop(n2);
        return bl2;
    }

    public Object[] pcall(Object object) {
        return this.pcall(object, null);
    }

    public int pcall(int n) {
        Object object;
        Throwable throwable;
        Coroutine coroutine = this.currentCoroutine;
        LuaCallFrame luaCallFrame = coroutine.currentCallFrame();
        coroutine.stackTrace = "";
        int n2 = coroutine.getTop() - n - 1;
        try {
            int n3;
            int n4 = coroutine.getCallframeTop();
            int n5 = this.call(n);
            int n6 = coroutine.getCallframeTop();
            if (n4 != n6) {
                n3 = 0;
            }
            KahluaUtil.luaAssert(n4 == n6, "error - call stack depth changed.");
            if (n4 != n6) {
                n3 = 0;
            }
            n3 = n2 + n5 + 1;
            coroutine.setTop(n3);
            coroutine.stackCopy(n2, n2 + 1, n5);
            coroutine.objectStack[n2] = Boolean.TRUE;
            return 1 + n5;
        }
        catch (KahluaException kahluaException) {
            throwable = kahluaException;
            object = kahluaException.errorMessage;
        }
        catch (Throwable throwable2) {
            throwable = throwable2;
            object = throwable2.getMessage() + " " + throwable2.getClass().getName();
        }
        KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
        if (luaCallFrame != null) {
            luaCallFrame.closeUpvalues(0);
        }
        coroutine.cleanCallFrames(luaCallFrame);
        if (object instanceof String) {
            object = (String)object;
        }
        coroutine.setTop(n2 + 4);
        coroutine.objectStack[n2] = Boolean.FALSE;
        coroutine.objectStack[n2 + 1] = object;
        coroutine.objectStack[n2 + 2] = coroutine.stackTrace;
        coroutine.objectStack[n2 + 3] = throwable;
        coroutine.stackTrace = "";
        return 4;
    }

    public KahluaTable getEnvironment() {
        return this.currentCoroutine.environment;
    }

    public PrintStream getOut() {
        return this.out;
    }

    public Platform getPlatform() {
        return this.platform;
    }

    public void breakpointToggle(String string, int n) {
        ArrayList<Object> arrayList;
        if (!this.BreakpointMap.containsKey(string)) {
            arrayList = new ArrayList();
            this.BreakpointMap.put(string, arrayList);
        } else {
            arrayList = this.BreakpointMap.get(string);
        }
        if (!arrayList.contains(n)) {
            arrayList.add(n);
        } else {
            arrayList.remove((Object)n);
        }
    }

    public boolean hasBreakpoint(String string, int n) {
        return this.BreakpointMap.containsKey(string) && this.BreakpointMap.get(string).contains(n);
    }

    public void toggleBreakOnChange(KahluaTable kahluaTable, Object object) {
        ArrayList<Object> arrayList;
        if (!this.BreakpointDataMap.containsKey(kahluaTable)) {
            arrayList = new ArrayList();
            this.BreakpointDataMap.put(kahluaTable, arrayList);
        } else {
            arrayList = this.BreakpointDataMap.get(kahluaTable);
        }
        if (!arrayList.contains(object)) {
            arrayList.add(object);
        } else {
            arrayList.remove(object);
        }
    }

    public void toggleBreakOnRead(KahluaTable kahluaTable, Object object) {
        ArrayList<Object> arrayList;
        if (!this.BreakpointReadDataMap.containsKey(kahluaTable)) {
            arrayList = new ArrayList();
            this.BreakpointReadDataMap.put(kahluaTable, arrayList);
        } else {
            arrayList = this.BreakpointReadDataMap.get(kahluaTable);
        }
        if (!arrayList.contains(object)) {
            arrayList.add(object);
        } else {
            arrayList.remove(object);
        }
    }

    public boolean hasDataBreakpoint(KahluaTable kahluaTable, Object object) {
        if (!this.BreakpointDataMap.containsKey(kahluaTable)) {
            return false;
        }
        ArrayList<Object> arrayList = this.BreakpointDataMap.get(kahluaTable);
        return arrayList.contains(object);
    }

    public boolean hasReadDataBreakpoint(KahluaTable kahluaTable, Object object) {
        if (!this.BreakpointReadDataMap.containsKey(kahluaTable)) {
            return false;
        }
        ArrayList<Object> arrayList = this.BreakpointReadDataMap.get(kahluaTable);
        return arrayList.contains(object);
    }

    static {
        KahluaThread.meta_ops[12] = "__add";
        KahluaThread.meta_ops[13] = "__sub";
        KahluaThread.meta_ops[14] = "__mul";
        KahluaThread.meta_ops[15] = "__div";
        KahluaThread.meta_ops[16] = "__mod";
        KahluaThread.meta_ops[17] = "__pow";
        KahluaThread.meta_ops[23] = "__eq";
        KahluaThread.meta_ops[24] = "__lt";
        KahluaThread.meta_ops[25] = "__le";
        LastCallFrame = null;
        m_error_count = 0;
        m_errors_list = new ArrayList();
    }

    public static class Entry {
        public String file;
        public double time;
    }

    private static class ProfileEntryComparitor
    implements Comparator<Entry> {
        @Override
        public int compare(Entry entry, Entry entry2) {
            double d = entry.time;
            double d2 = entry2.time;
            if (d > d2) {
                return -1;
            }
            if (d2 > d) {
                return 1;
            }
            return 0;
        }
    }
}

