// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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
    public static LuaCallFrame LastCallFrame = null;
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
    public ArrayList<KahluaThread.Entry> profileEntries = new ArrayList<>();
    public HashMap<String, KahluaThread.Entry> profileEntryMap = new HashMap<>();
    public static int m_error_count = 0;
    public static final ArrayList<String> m_errors_list = new ArrayList<>();
    private final StringBuilder m_stringBuilder = new StringBuilder();
    private final StringWriter m_stringWriter = new StringWriter();
    private final PrintWriter m_printWriter = new PrintWriter(this.m_stringWriter);
    HashMap<String, ArrayList<Long>> BreakpointMap = new HashMap<>();
    HashMap<KahluaTable, ArrayList<Object>> BreakpointDataMap = new HashMap<>();
    HashMap<KahluaTable, ArrayList<Object>> BreakpointReadDataMap = new HashMap<>();
    public boolean bStepInto = false;

    public Coroutine getCurrentCoroutine() {
        return this.currentCoroutine;
    }

    public KahluaThread(Platform arg0, KahluaTable arg1) {
        this(System.out, arg0, arg1);
    }

    public KahluaThread(PrintStream arg0, Platform arg1, KahluaTable arg2) {
        this.platform = arg1;
        this.out = arg0;
        this.rootCoroutine = new Coroutine(arg1, arg2, this);
        this.currentCoroutine = this.rootCoroutine;
    }

    public int call(int arg0) {
        int int0 = this.currentCoroutine.getTop();
        int int1 = int0 - arg0 - 1;
        Object object = this.currentCoroutine.objectStack[int1];
        if (object == null) {
            throw new RuntimeException("tried to call nil");
        } else {
            try {
                if (object instanceof JavaFunction) {
                    return this.callJava((JavaFunction)object, int1 + 1, int1, arg0);
                }
            } catch (Exception exception) {
                throw new RuntimeException(exception.getClass().getName() + " " + exception.getMessage() + " in " + (JavaFunction)object);
            }

            if (!(object instanceof LuaClosure)) {
                throw new RuntimeException("tried to call a non-function");
            } else {
                LuaCallFrame luaCallFrame = this.currentCoroutine.pushNewCallFrame((LuaClosure)object, null, int1 + 1, int1, arg0, false, false);
                luaCallFrame.init();
                this.luaMainloop();
                int int2 = this.currentCoroutine.getTop() - int1;
                this.currentCoroutine.stackTrace = "";
                return int2;
            }
        }
    }

    private int callJava(JavaFunction javaFunction, int int0, int int1, int int2) {
        Coroutine coroutine = this.currentCoroutine;
        LuaCallFrame luaCallFrame = coroutine.pushNewCallFrame(null, javaFunction, int0, int1, int2, false, false);
        int int3 = javaFunction.call(luaCallFrame, int2);
        int int4 = luaCallFrame.getTop();
        int int5 = int4 - int3;
        int int6 = int1 - int0;
        luaCallFrame.stackCopy(int5, int6, int3);
        luaCallFrame.setTop(int3 + int6);
        coroutine.popCallFrame();
        return int3;
    }

    private final Object prepareMetatableCall(Object object) {
        return !(object instanceof JavaFunction) && !(object instanceof LuaClosure) ? this.getMetaOp(object, "__call") : object;
    }

    public boolean isCurrent(String arg0, int arg1) {
        return arg1 == this.currentLine;
    }

    private final void luaMainloop() {
        LuaCallFrame luaCallFrame0 = this.currentCoroutine.currentCallFrame();
        LuaClosure luaClosure0 = luaCallFrame0.closure;
        Prototype prototype0 = luaClosure0.prototype;
        int[] ints = prototype0.code;
        int int0 = luaCallFrame0.returnBase;
        String string0 = "";
        long long0 = System.nanoTime();
        if (this.doProfiling && Core.bDebug && this == LuaManager.thread) {
            Coroutine coroutine0 = this.getCurrentCoroutine();
            String string1 = coroutine0.objectStack[0].toString();
            String string2 = coroutine0.getThread().currentfile + " " + string1.substring(0, string1.indexOf(":"));
            string0 = string2;
        }

        boolean boolean0 = true;

        while (!this.bReset) {
            if (Core.bDebug && this == LuaManager.thread) {
                Coroutine coroutine1 = this.getCurrentCoroutine();
                if (coroutine1 != null) {
                    this.lastLine = this.currentLine;
                    LuaCallFrame luaCallFrame1 = coroutine1.currentCallFrame();
                    if (luaCallFrame1.closure != null) {
                        this.currentfile = luaCallFrame1.closure.prototype.filename;
                        this.currentLine = luaCallFrame1.closure.prototype.lines[luaCallFrame1.pc];
                        if (this.bStep && this.currentLine != this.lastLine) {
                            if (this.bStepInto) {
                                this.bStep = false;
                                UIManager.debugBreakpoint(luaCallFrame1.closure.prototype.filename, this.currentLine - 1L);
                                this.lastCallFrame = coroutine1.getCallframeTop();
                                boolean0 = true;
                            } else if (coroutine1.getCallframeTop() <= this.lastCallFrame) {
                                this.bStep = false;
                                this.lastCallFrame = coroutine1.getCallframeTop();
                                UIManager.debugBreakpoint(luaCallFrame1.closure.prototype.filename, this.currentLine - 1L);
                                boolean0 = true;
                            }
                        }

                        if (this.BreakpointMap.containsKey(luaCallFrame1.closure.prototype.filename)) {
                            ArrayList arrayList = this.BreakpointMap.get(luaCallFrame1.closure.prototype.filename);
                            if (arrayList.contains((long)luaCallFrame1.closure.prototype.lines[luaCallFrame1.pc])
                                && (
                                    luaCallFrame1.pc == 0
                                        || luaCallFrame1.closure.prototype.lines[luaCallFrame1.pc - 1]
                                            != luaCallFrame1.closure.prototype.lines[luaCallFrame1.pc]
                                )) {
                                UIManager.debugBreakpoint(luaCallFrame1.closure.prototype.filename, luaCallFrame1.closure.prototype.lines[luaCallFrame1.pc]);
                            }
                        }
                    }
                }
            }

            boolean0 = true;

            try {
                if (this.bStep) {
                    boolean boolean1 = false;
                }

                int int1 = ints[luaCallFrame0.pc++];
                int int2 = int1 & 63;
                switch (int2) {
                    case 0:
                        int int3 = getA8(int1);
                        int int4 = getB9(int1);
                        luaCallFrame0.set(int3, luaCallFrame0.get(int4));
                        boolean0 = false;
                        break;
                    case 1:
                        int int93 = getA8(int1);
                        int int94 = getBx(int1);
                        if (Core.bDebug) {
                            int int95 = luaCallFrame0.closure.prototype.lines[luaCallFrame0.pc - 1];
                            boolean boolean16 = luaCallFrame0.closure.prototype.lines[luaCallFrame0.pc] != int95;
                            if (this == LuaManager.thread && luaCallFrame0.closure.prototype.locvarlines != null) {
                                while (
                                    int95 > luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned]
                                        && luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned] != 0
                                ) {
                                    luaCallFrame0.localsAssigned++;
                                }
                            }

                            if (boolean16
                                && this == LuaManager.thread
                                && luaCallFrame0.closure.prototype.locvarlines != null
                                && luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned] == int95) {
                                int int96 = luaCallFrame0.localsAssigned++;
                                String string18 = luaCallFrame0.closure.prototype.locvars[int96];
                                luaCallFrame0.setLocalVarToStack(string18, luaCallFrame0.localBase + int93);
                            }
                        }

                        luaCallFrame0.set(int93, prototype0.constants[int94]);
                        break;
                    case 2:
                        int int31 = getA8(int1);
                        int int32 = getB9(int1);
                        int int33 = getC9(int1);
                        Boolean boolean2 = int32 == 0 ? Boolean.FALSE : Boolean.TRUE;
                        if (Core.bDebug) {
                            int int34 = luaCallFrame0.closure.prototype.lines[luaCallFrame0.pc - 1];
                            boolean boolean3 = luaCallFrame0.closure.prototype.lines[luaCallFrame0.pc] != int34;
                            if (this == LuaManager.thread && luaCallFrame0.closure.prototype.locvarlines != null) {
                                while (
                                    int34 > luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned]
                                        && luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned] != 0
                                ) {
                                    luaCallFrame0.localsAssigned++;
                                }
                            }

                            if (boolean3
                                && this == LuaManager.thread
                                && luaCallFrame0.closure.prototype.locvarlines != null
                                && luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned] == int34) {
                                int int35 = luaCallFrame0.localsAssigned++;
                                String string4 = luaCallFrame0.closure.prototype.locvars[int35];
                                if (string4.equals("group")) {
                                    boolean boolean4 = false;
                                }

                                luaCallFrame0.setLocalVarToStack(string4, luaCallFrame0.localBase + int31);
                            }
                        }

                        luaCallFrame0.set(int31, boolean2);
                        if (int33 != 0) {
                            luaCallFrame0.pc++;
                        }
                        break;
                    case 3:
                        int int89 = getA8(int1);
                        int int90 = getB9(int1);
                        if (Core.bDebug) {
                            int int91 = luaCallFrame0.closure.prototype.lines[luaCallFrame0.pc - 1];
                            boolean boolean15 = luaCallFrame0.closure.prototype.lines[luaCallFrame0.pc] != int91;
                            if (this == LuaManager.thread && luaCallFrame0.closure.prototype.locvarlines != null) {
                                while (
                                    int91 > luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned]
                                        && luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned] != 0
                                ) {
                                    luaCallFrame0.localsAssigned++;
                                }
                            }

                            if (boolean15
                                && this == LuaManager.thread
                                && luaCallFrame0.closure.prototype.locvarlines != null
                                && luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned] == int91) {
                                int int92 = luaCallFrame0.localsAssigned++;
                                String string17 = luaCallFrame0.closure.prototype.locvars[int92];
                                luaCallFrame0.setLocalVarToStack(string17, luaCallFrame0.localBase + int89);
                            }
                        }

                        luaCallFrame0.stackClear(int89, int90);
                        break;
                    case 4:
                        int int85 = getA8(int1);
                        int int86 = getB9(int1);
                        UpValue upValue1 = luaClosure0.upvalues[int86];
                        if (Core.bDebug) {
                            int int87 = luaCallFrame0.closure.prototype.lines[luaCallFrame0.pc - 1];
                            boolean boolean13 = luaCallFrame0.closure.prototype.lines[luaCallFrame0.pc] != int87;
                            if (this == LuaManager.thread && luaCallFrame0.closure.prototype.locvarlines != null) {
                                while (
                                    int87 > luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned]
                                        && luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned] != 0
                                ) {
                                    luaCallFrame0.localsAssigned++;
                                }
                            }

                            if (boolean13
                                && this == LuaManager.thread
                                && luaCallFrame0.closure.prototype.locvarlines != null
                                && luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned] == int87) {
                                int int88 = luaCallFrame0.localsAssigned++;
                                String string16 = luaCallFrame0.closure.prototype.locvars[int88];
                                if (string16.equals("group")) {
                                    boolean boolean14 = false;
                                }

                                luaCallFrame0.setLocalVarToStack(string16, luaCallFrame0.localBase + int85);
                            }
                        }

                        luaCallFrame0.set(int85, upValue1.getValue());
                        break;
                    case 5:
                        int int81 = getA8(int1);
                        int int82 = getBx(int1);
                        Object object39 = this.tableget(luaClosure0.env, prototype0.constants[int82]);
                        if (Core.bDebug) {
                            int int83 = luaCallFrame0.closure.prototype.lines[luaCallFrame0.pc - 1];
                            boolean boolean11 = luaCallFrame0.closure.prototype.lines[luaCallFrame0.pc] != int83;
                            if (this == LuaManager.thread && luaCallFrame0.closure.prototype.locvarlines != null) {
                                while (
                                    int83 > luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned]
                                        && luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned] != 0
                                ) {
                                    luaCallFrame0.localsAssigned++;
                                }
                            }

                            if (boolean11
                                && this == LuaManager.thread
                                && luaCallFrame0.closure.prototype.locvarlines != null
                                && luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned] == int83) {
                                int int84 = luaCallFrame0.localsAssigned++;
                                String string15 = luaCallFrame0.closure.prototype.locvars[int84];
                                if (string15.equals("group")) {
                                    boolean boolean12 = false;
                                }

                                luaCallFrame0.setLocalVarToStack(string15, luaCallFrame0.localBase + int81);
                            }
                        }

                        luaCallFrame0.set(int81, object39);
                        break;
                    case 6:
                        int int76 = getA8(int1);
                        int int77 = getB9(int1);
                        int int78 = getC9(int1);
                        Object object36 = luaCallFrame0.get(int77);
                        Object object37 = this.getRegisterOrConstant(luaCallFrame0, int78, prototype0);
                        Object object38 = this.tableget(object36, object37);
                        if (Core.bDebug) {
                            int int79 = luaCallFrame0.closure.prototype.lines[luaCallFrame0.pc - 1];
                            boolean boolean10 = luaCallFrame0.closure.prototype.lines[luaCallFrame0.pc] != int79;
                            if (this == LuaManager.thread && luaCallFrame0.closure.prototype.locvarlines != null) {
                                while (
                                    int79 > luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned]
                                        && luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned] != 0
                                ) {
                                    luaCallFrame0.localsAssigned++;
                                }
                            }

                            if (boolean10
                                && this == LuaManager.thread
                                && luaCallFrame0.closure.prototype.locvarlines != null
                                && luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned] == int79) {
                                int int80 = luaCallFrame0.localsAssigned++;
                                String string14 = luaCallFrame0.closure.prototype.locvars[int80];
                                luaCallFrame0.setLocalVarToStack(string14, luaCallFrame0.localBase + int76);
                            }
                        }

                        luaCallFrame0.set(int76, object38);
                        break;
                    case 7:
                        int int29 = getA8(int1);
                        int int30 = getBx(int1);
                        Object object16 = luaCallFrame0.get(int29);
                        Object object17 = prototype0.constants[int30];
                        if (object16 instanceof LuaClosure && object17 instanceof String) {
                            ((LuaClosure)object16).debugName = object17.toString();
                        }

                        if (LuaCompiler.rewriteEvents) {
                            Object object18 = luaClosure0.env.rawget(object17);
                            if (object18 instanceof KahluaTable && object18 != object16) {
                                KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)object18;
                                kahluaTableImpl.setRewriteTable(object16);
                            }

                            this.tableSet(luaClosure0.env, object17, object16);
                        } else {
                            this.tableSet(luaClosure0.env, object17, object16);
                        }
                        break;
                    case 8:
                        int int72 = getA8(int1);
                        int int73 = getB9(int1);
                        UpValue upValue0 = luaClosure0.upvalues[int73];
                        if (Core.bDebug) {
                            int int74 = luaCallFrame0.closure.prototype.lines[luaCallFrame0.pc - 1];
                            boolean boolean9 = luaCallFrame0.closure.prototype.lines[luaCallFrame0.pc] != int74;
                            if (this == LuaManager.thread && luaCallFrame0.closure.prototype.locvarlines != null) {
                                while (
                                    int74 > luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned]
                                        && luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned] != 0
                                ) {
                                    luaCallFrame0.localsAssigned++;
                                }
                            }

                            if (boolean9
                                && this == LuaManager.thread
                                && luaCallFrame0.closure.prototype.locvarlines != null
                                && luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned] == int74) {
                                int int75 = luaCallFrame0.localsAssigned++;
                                String string13 = luaCallFrame0.closure.prototype.locvars[int75];
                                luaCallFrame0.setLocalVarToStack(string13, luaCallFrame0.localBase + int72);
                            }
                        }

                        upValue0.setValue(luaCallFrame0.get(int72));
                        break;
                    case 9:
                        int int5 = getA8(int1);
                        int int6 = getB9(int1);
                        int int7 = getC9(int1);
                        Object object0 = luaCallFrame0.get(int5);
                        Object object1 = this.getRegisterOrConstant(luaCallFrame0, int6, prototype0);
                        Object object2 = this.getRegisterOrConstant(luaCallFrame0, int7, prototype0);
                        this.tableSet(object0, object1, object2);
                        break;
                    case 10:
                        int int69 = getA8(int1);
                        KahluaTable table2 = this.platform.newTable();
                        if (Core.bDebug) {
                            int int70 = luaCallFrame0.closure.prototype.lines[luaCallFrame0.pc - 1];
                            boolean boolean8 = luaCallFrame0.closure.prototype.lines[luaCallFrame0.pc] != int70;
                            if (this == LuaManager.thread && luaCallFrame0.closure.prototype.locvarlines != null) {
                                while (
                                    int70 > luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned]
                                        && luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned] != 0
                                ) {
                                    luaCallFrame0.localsAssigned++;
                                }
                            }

                            if (boolean8
                                && this == LuaManager.thread
                                && luaCallFrame0.closure.prototype.locvarlines != null
                                && luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned] == int70) {
                                int int71 = luaCallFrame0.localsAssigned++;
                                String string12 = luaCallFrame0.closure.prototype.locvars[int71];
                                luaCallFrame0.setLocalVarToStack(string12, luaCallFrame0.localBase + int69);
                            }
                        }

                        luaCallFrame0.set(int69, table2);
                        break;
                    case 11:
                        int int8 = getA8(int1);
                        int int9 = getB9(int1);
                        int int10 = getC9(int1);
                        Object object3 = this.getRegisterOrConstant(luaCallFrame0, int10, prototype0);
                        Object object4 = luaCallFrame0.get(int9);
                        LastCallFrame = luaCallFrame0;
                        Object object5 = this.tableget(object4, object3);
                        luaCallFrame0.set(int8, object5);
                        luaCallFrame0.set(int8 + 1, object4);
                        boolean0 = false;
                        break;
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                        int int64 = getA8(int1);
                        int int65 = getB9(int1);
                        int int66 = getC9(int1);
                        Object object30 = this.getRegisterOrConstant(luaCallFrame0, int65, prototype0);
                        Object object31 = this.getRegisterOrConstant(luaCallFrame0, int66, prototype0);
                        Object object32 = null;
                        Object object33 = null;
                        Object object34 = null;
                        if ((object32 = KahluaUtil.rawTonumber(object30)) != null && (object33 = KahluaUtil.rawTonumber(object31)) != null) {
                            object34 = this.primitiveMath((Double)object32, (Double)object33, int2);
                        } else {
                            String string9 = meta_ops[int2];
                            Object object35 = this.getBinMetaOp(object30, object31, string9);
                            if (object35 == null) {
                                this.doStacktraceProper(luaCallFrame0);
                                String string10 = "unknown";
                                if (luaClosure0.debugName != null) {
                                    string10 = luaClosure0.debugName;
                                } else if (prototype0.name != null) {
                                    string10 = prototype0.name;
                                }

                                KahluaUtil.fail(string9 + " not defined for operands in " + string10);
                            }

                            object34 = this.call(object35, object30, object31, null);
                        }

                        if (Core.bDebug) {
                            int int67 = luaCallFrame0.closure.prototype.lines[luaCallFrame0.pc - 1];
                            boolean boolean7 = luaCallFrame0.closure.prototype.lines[luaCallFrame0.pc] != int67;
                            if (this == LuaManager.thread && luaCallFrame0.closure.prototype.locvarlines != null) {
                                while (
                                    int67 > luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned]
                                        && luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned] != 0
                                ) {
                                    luaCallFrame0.localsAssigned++;
                                }
                            }

                            if (boolean7
                                && this == LuaManager.thread
                                && luaCallFrame0.closure.prototype.locvarlines != null
                                && luaCallFrame0.closure.prototype.locvarlines[luaCallFrame0.localsAssigned] == int67) {
                                int int68 = luaCallFrame0.localsAssigned++;
                                String string11 = luaCallFrame0.closure.prototype.locvars[int68];
                                luaCallFrame0.setLocalVarToStack(string11, luaCallFrame0.localBase + int64);
                            }
                        }

                        luaCallFrame0.set(int64, object34);
                        break;
                    case 18:
                        int int27 = getA8(int1);
                        int int28 = getB9(int1);
                        Object object13 = luaCallFrame0.get(int28);
                        Double double2 = KahluaUtil.rawTonumber(object13);
                        Object object14;
                        if (double2 != null) {
                            object14 = KahluaUtil.toDouble(-KahluaUtil.fromDouble(double2));
                        } else {
                            Object object15 = this.getMetaOp(object13, "__unm");
                            object14 = this.call(object15, object13, null, null);
                        }

                        luaCallFrame0.set(int27, object14);
                        break;
                    case 19:
                        int int25 = getA8(int1);
                        int int26 = getB9(int1);
                        Object object12 = luaCallFrame0.get(int26);
                        luaCallFrame0.set(int25, KahluaUtil.toBoolean(!KahluaUtil.boolEval(object12)));
                        boolean0 = false;
                        break;
                    case 20:
                        int int23 = getA8(int1);
                        int int24 = getB9(int1);
                        Object object9 = luaCallFrame0.get(int24);
                        Object object10;
                        if (object9 instanceof KahluaTable table0) {
                            object10 = KahluaUtil.toDouble((long)table0.len());
                        } else if (object9 instanceof String string3) {
                            object10 = KahluaUtil.toDouble((long)string3.length());
                        } else {
                            Object object11 = this.getMetaOp(object9, "__len");
                            if (object11 == null) {
                                this.doStacktraceProper(luaCallFrame0);
                            }

                            KahluaUtil.luaAssert(object11 != null, "__len not defined for operand");
                            object10 = this.call(object11, object9, null, null);
                        }

                        luaCallFrame0.set(int23, object10);
                        boolean0 = false;
                        break;
                    case 21:
                        int int56 = getA8(int1);
                        int int57 = getB9(int1);
                        int int58 = getC9(int1);
                        int int59 = int57;
                        Object object27 = luaCallFrame0.get(int58);
                        int int60 = int58 - 1;

                        while (int59 <= int60) {
                            String string8 = KahluaUtil.rawTostring(object27);
                            if (string8 != null) {
                                int int61 = 0;

                                for (int int62 = int60; int59 <= int62; int61++) {
                                    Object object28 = luaCallFrame0.get(int62);
                                    int62--;
                                    if (KahluaUtil.rawTostring(object28) == null) {
                                        break;
                                    }
                                }

                                if (int61 > 0) {
                                    StringBuilder stringBuilder = new StringBuilder();

                                    for (int int63 = int60 - int61 + 1; int63 <= int60; int63++) {
                                        stringBuilder.append(KahluaUtil.rawTostring(luaCallFrame0.get(int63)));
                                    }

                                    stringBuilder.append(string8);
                                    object27 = stringBuilder.toString();
                                    int60 -= int61;
                                }
                            }

                            if (int59 <= int60) {
                                string8 = (String)luaCallFrame0.get(int60);
                                Object object29 = this.getBinMetaOp(string8, object27, "__concat");
                                if (object29 == null) {
                                    KahluaUtil.fail("__concat not defined for operands: " + string8 + " and " + object27);
                                }

                                object27 = this.call(object29, string8, object27, null);
                                int60--;
                            }
                        }

                        luaCallFrame0.set(int56, object27);
                        boolean0 = false;
                        break;
                    case 22:
                        luaCallFrame0.pc = luaCallFrame0.pc + getSBx(int1);
                        break;
                    case 23:
                    case 24:
                    case 25:
                        int int52 = getA8(int1);
                        int int53 = getB9(int1);
                        int int54 = getC9(int1);
                        Object object22 = this.getRegisterOrConstant(luaCallFrame0, int53, prototype0);
                        Object object23 = this.getRegisterOrConstant(luaCallFrame0, int54, prototype0);
                        if (object22 instanceof Double && object23 instanceof Double) {
                            double double8 = KahluaUtil.fromDouble(object22);
                            double double9 = KahluaUtil.fromDouble(object23);
                            if (int2 == 23) {
                                if (double8 == double9 == (int52 == 0)) {
                                    luaCallFrame0.pc++;
                                }
                            } else if (int2 == 24) {
                                if (double8 < double9 == (int52 == 0)) {
                                    luaCallFrame0.pc++;
                                }
                            } else if (double8 <= double9 == (int52 == 0)) {
                                luaCallFrame0.pc++;
                            }
                        } else if (object22 instanceof String && object23 instanceof String) {
                            if (int2 == 23) {
                                if (object22.equals(object23) == (int52 == 0)) {
                                    luaCallFrame0.pc++;
                                }
                            } else {
                                String string5 = (String)object22;
                                String string6 = (String)object23;
                                int int55 = string5.compareTo(string6);
                                if (int2 == 24) {
                                    if (int55 < 0 == (int52 == 0)) {
                                        luaCallFrame0.pc++;
                                    }
                                } else if (int55 <= 0 == (int52 == 0)) {
                                    luaCallFrame0.pc++;
                                }
                            }
                        } else {
                            boolean boolean5;
                            if (object22 == object23 && int2 == 23) {
                                boolean5 = true;
                            } else {
                                boolean boolean6 = false;
                                String string7 = meta_ops[int2];
                                Object object24 = this.getCompMetaOp(object22, object23, string7);
                                if (object24 == null && int2 == 25) {
                                    object24 = this.getCompMetaOp(object22, object23, "__lt");
                                    Object object25 = object22;
                                    object22 = object23;
                                    object23 = object25;
                                    boolean6 = true;
                                }

                                if (object24 == null && int2 == 23) {
                                    boolean5 = BaseLib.luaEquals(object22, object23);
                                } else {
                                    if (object24 == null) {
                                        this.doStacktraceProper(luaCallFrame0);
                                        KahluaUtil.fail(string7 + " not defined for operand");
                                    }

                                    Object object26 = this.call(object24, object22, object23, null);
                                    boolean5 = KahluaUtil.boolEval(object26);
                                }

                                if (boolean6) {
                                    boolean5 = !boolean5;
                                }
                            }

                            if (boolean5 == (int52 == 0)) {
                                luaCallFrame0.pc++;
                            }
                        }

                        boolean0 = false;
                        break;
                    case 26:
                        int int21 = getA8(int1);
                        int int22 = getC9(int1);
                        Object object8 = luaCallFrame0.get(int21);
                        if (KahluaUtil.boolEval(object8) == (int22 == 0)) {
                            luaCallFrame0.pc++;
                        }
                        break;
                    case 27:
                        int int18 = getA8(int1);
                        int int19 = getB9(int1);
                        int int20 = getC9(int1);
                        Object object7 = luaCallFrame0.get(int19);
                        if (KahluaUtil.boolEval(object7) != (int20 == 0)) {
                            luaCallFrame0.set(int18, object7);
                        } else {
                            luaCallFrame0.pc++;
                        }
                        break;
                    case 28:
                        int int100 = getA8(int1);
                        int int101 = getB9(int1);
                        int int102 = getC9(int1);
                        int int103 = int101 - 1;
                        if (int103 != -1) {
                            luaCallFrame0.setTop(int100 + int103 + 1);
                        } else {
                            int103 = luaCallFrame0.getTop() - int100 - 1;
                        }

                        luaCallFrame0.restoreTop = int102 != 0;
                        int int104 = luaCallFrame0.localBase;
                        int int105 = int104 + int100 + 1;
                        int int106 = int104 + int100;
                        Object object40 = luaCallFrame0.get(int100);
                        if (object40 == null) {
                            boolean boolean17 = false;
                            object40 = luaCallFrame0.get(int100);
                        }

                        if (object40 == null) {
                            this.doStacktraceProper(luaCallFrame0);
                            if (luaCallFrame0.getClosure().debugName != null) {
                                KahluaUtil.fail("Object tried to call nil in " + luaCallFrame0.getClosure().debugName);
                            } else if (luaCallFrame0.getClosure().prototype != null && luaCallFrame0.getClosure().prototype.name != null) {
                                KahluaUtil.fail("Object tried to call nil in " + luaCallFrame0.getClosure().prototype.name);
                            } else {
                                KahluaUtil.fail("Object tried to call nil in unknown");
                            }
                        }

                        Object object41 = this.prepareMetatableCall(object40);
                        if (object41 == null) {
                            KahluaUtil.fail("Object " + object40 + " did not have __call metatable set");
                        }

                        if (object41 != object40) {
                            int105 = int106;
                            int103++;
                        }

                        if (object41 instanceof LuaClosure) {
                            LuaCallFrame luaCallFrame2 = this.currentCoroutine
                                .pushNewCallFrame((LuaClosure)object41, null, int105, int106, int103, true, luaCallFrame0.canYield);
                            luaCallFrame2.init();
                            luaCallFrame0 = luaCallFrame2;
                            luaClosure0 = luaCallFrame2.closure;
                            prototype0 = luaClosure0.prototype;
                            ints = prototype0.code;
                            int0 = luaCallFrame2.returnBase;
                            break;
                        } else {
                            if (!(object41 instanceof JavaFunction)) {
                                throw new RuntimeException("Tried to call a non-function: " + object41);
                            }

                            this.callJava((JavaFunction)object41, int105, int106, int103);
                            luaCallFrame0 = this.currentCoroutine.currentCallFrame();
                            if (luaCallFrame0 != null && !luaCallFrame0.isJava()) {
                                luaClosure0 = luaCallFrame0.closure;
                                prototype0 = luaClosure0.prototype;
                                ints = prototype0.code;
                                int0 = luaCallFrame0.returnBase;
                                if (luaCallFrame0.restoreTop) {
                                    luaCallFrame0.setTop(prototype0.maxStacksize);
                                }
                                break;
                            }

                            long long4 = System.nanoTime();
                            return;
                        }
                    case 29:
                        int int36 = luaCallFrame0.localBase;
                        this.currentCoroutine.closeUpvalues(int36);
                        int int37 = getA8(int1);
                        int int38 = getB9(int1);
                        int int39 = int38 - 1;
                        if (int39 == -1) {
                            int39 = luaCallFrame0.getTop() - int37 - 1;
                        }

                        luaCallFrame0.restoreTop = false;
                        Object object19 = luaCallFrame0.get(int37);

                        try {
                            KahluaUtil.luaAssert(object19 != null, "Tried to call nil");
                        } catch (Exception exception0) {
                            if (Core.bDebug && UIManager.defaultthread == LuaManager.thread) {
                                UIManager.debugBreakpoint(LuaManager.thread.currentfile, LuaManager.thread.currentLine - 1);
                            }

                            this.debugException(exception0);
                            this.doStacktraceProper(luaCallFrame0);
                            KahluaUtil.fail("");
                        }

                        Object object20 = this.prepareMetatableCall(object19);
                        if (object20 == null) {
                            KahluaUtil.fail("Object did not have __call metatable set");
                        }

                        int int40 = int0 + 1;
                        if (object20 != object19) {
                            int40 = int0;
                            int39++;
                        }

                        this.currentCoroutine.stackCopy(int36 + int37, int0, int39 + 1);
                        this.currentCoroutine.setTop(int0 + int39 + 1);
                        if (object20 instanceof LuaClosure) {
                            luaCallFrame0.localBase = int40;
                            luaCallFrame0.nArguments = int39;
                            luaCallFrame0.closure = (LuaClosure)object20;
                            luaCallFrame0.init();
                        } else {
                            if (!(object20 instanceof JavaFunction)) {
                                KahluaUtil.fail("Tried to call a non-function: " + object20);
                            }

                            Coroutine coroutine2 = this.currentCoroutine;
                            this.callJava((JavaFunction)object20, int40, int0, int39);
                            luaCallFrame0 = this.currentCoroutine.currentCallFrame();
                            coroutine2.popCallFrame();
                            if (coroutine2 != this.currentCoroutine) {
                                if (coroutine2.isDead() && coroutine2 != this.rootCoroutine && this.currentCoroutine.getParent() == coroutine2) {
                                    this.currentCoroutine.resume(coroutine2.getParent());
                                    coroutine2.destroy();
                                    this.currentCoroutine.getParent().currentCallFrame().push(Boolean.TRUE);
                                }

                                luaCallFrame0 = this.currentCoroutine.currentCallFrame();
                                if (luaCallFrame0.isJava()) {
                                    long long1 = System.nanoTime();
                                    return;
                                }
                            } else {
                                if (!luaCallFrame0.fromLua) {
                                    long long2 = System.nanoTime();
                                    return;
                                }

                                luaCallFrame0 = this.currentCoroutine.currentCallFrame();
                                if (luaCallFrame0.restoreTop) {
                                    luaCallFrame0.setTop(luaCallFrame0.closure.prototype.maxStacksize);
                                }
                            }
                        }

                        luaClosure0 = luaCallFrame0.closure;
                        prototype0 = luaClosure0.prototype;
                        ints = prototype0.code;
                        int0 = luaCallFrame0.returnBase;
                        break;
                    case 30:
                        int int97 = getA8(int1);
                        int int98 = getB9(int1) - 1;
                        int int99 = luaCallFrame0.localBase;
                        this.currentCoroutine.closeUpvalues(int99);
                        if (int98 == -1) {
                            int98 = luaCallFrame0.getTop() - int97;
                        }

                        this.currentCoroutine.stackCopy(luaCallFrame0.localBase + int97, int0, int98);
                        this.currentCoroutine.setTop(int0 + int98);
                        if (!luaCallFrame0.fromLua) {
                            this.currentCoroutine.popCallFrame();
                            long long3 = System.nanoTime();
                            return;
                        }

                        if (luaCallFrame0.canYield && this.currentCoroutine.atBottom()) {
                            luaCallFrame0.localBase = luaCallFrame0.returnBase;
                            Coroutine coroutine3 = this.currentCoroutine;
                            Coroutine.yieldHelper(luaCallFrame0, luaCallFrame0, int98);
                            coroutine3.popCallFrame();
                            luaCallFrame0 = this.currentCoroutine.currentCallFrame();
                            if (luaCallFrame0 == null || luaCallFrame0.isJava()) {
                                return;
                            }
                        } else {
                            this.currentCoroutine.popCallFrame();
                        }

                        luaCallFrame0 = this.currentCoroutine.currentCallFrame();
                        luaClosure0 = luaCallFrame0.closure;
                        prototype0 = luaClosure0.prototype;
                        ints = prototype0.code;
                        int0 = luaCallFrame0.returnBase;
                        if (luaCallFrame0.restoreTop) {
                            luaCallFrame0.setTop(prototype0.maxStacksize);
                        }
                        break;
                    case 31:
                        int int50 = getA8(int1);
                        double double4 = KahluaUtil.fromDouble(luaCallFrame0.get(int50));
                        double double5 = KahluaUtil.fromDouble(luaCallFrame0.get(int50 + 1));
                        double double6 = KahluaUtil.fromDouble(luaCallFrame0.get(int50 + 2));
                        double4 += double6;
                        Double double7 = KahluaUtil.toDouble(double4);
                        luaCallFrame0.set(int50, double7);
                        if (double6 > 0.0 ? !(double4 <= double5) : !(double4 >= double5)) {
                            luaCallFrame0.clearFromIndex(int50);
                        } else {
                            int int51 = getSBx(int1);
                            luaCallFrame0.pc += int51;
                            luaCallFrame0.set(int50 + 3, double7);
                        }
                        break;
                    case 32:
                        int int11 = getA8(int1);
                        int int12 = getSBx(int1);
                        double double0 = KahluaUtil.fromDouble(luaCallFrame0.get(int11));
                        double double1 = KahluaUtil.fromDouble(luaCallFrame0.get(int11 + 2));
                        luaCallFrame0.set(int11, KahluaUtil.toDouble(double0 - double1));
                        luaCallFrame0.pc += int12;
                        break;
                    case 33:
                        int int16 = getA8(int1);
                        int int17 = getC9(int1);
                        luaCallFrame0.setTop(int16 + 6);
                        luaCallFrame0.stackCopy(int16, int16 + 3, 3);
                        this.call(2);
                        luaCallFrame0.clearFromIndex(int16 + 3 + int17);
                        luaCallFrame0.setPrototypeStacksize();
                        Object object6 = luaCallFrame0.get(int16 + 3);
                        if (object6 != null) {
                            luaCallFrame0.set(int16 + 2, object6);
                        } else {
                            luaCallFrame0.pc++;
                        }
                        break;
                    case 34:
                        int int45 = getA8(int1);
                        int int46 = getB9(int1);
                        int int47 = getC9(int1);
                        if (int46 == 0) {
                            int46 = luaCallFrame0.getTop() - int45 - 1;
                        }

                        if (int47 == 0) {
                            int47 = ints[luaCallFrame0.pc++];
                        }

                        int int48 = (int47 - 1) * 50;
                        KahluaTable table1 = (KahluaTable)luaCallFrame0.get(int45);

                        for (int int49 = 1; int49 <= int46; int49++) {
                            Double double3 = KahluaUtil.toDouble((long)(int48 + int49));
                            Object object21 = luaCallFrame0.get(int45 + int49);
                            table1.rawset(double3, object21);
                        }
                        break;
                    case 35:
                        int int13 = getA8(int1);
                        luaCallFrame0.closeUpvalues(int13);
                        break;
                    case 36:
                        int int41 = getA8(int1);
                        int int42 = getBx(int1);
                        Prototype prototype1 = prototype0.prototypes[int42];
                        LuaClosure luaClosure1 = new LuaClosure(prototype1, luaClosure0.env);
                        luaCallFrame0.set(int41, luaClosure1);
                        int int43 = prototype1.numUpvalues;

                        for (int int44 = 0; int44 < int43; int44++) {
                            int1 = ints[luaCallFrame0.pc++];
                            int2 = int1 & 63;
                            int42 = getB9(int1);
                            switch (int2) {
                                case 0:
                                    luaClosure1.upvalues[int44] = luaCallFrame0.findUpvalue(int42);
                                    break;
                                case 4:
                                    luaClosure1.upvalues[int44] = luaClosure0.upvalues[int42];
                            }
                        }
                        break;
                    case 37:
                        int int14 = getA8(int1);
                        int int15 = getB9(int1) - 1;
                        luaCallFrame0.pushVarargs(int14, int15);
                }
            } catch (RuntimeException runtimeException) {
                if (Core.bDebug && UIManager.defaultthread == LuaManager.thread) {
                }

                if (runtimeException.getMessage() != null) {
                    ExceptionLogger.logException(runtimeException);
                    this.debugException(runtimeException);
                }

                this.doStacktraceProper(luaCallFrame0);
                KahluaUtil.fail("");
                boolean boolean18 = true;

                do {
                    luaCallFrame0 = this.currentCoroutine.currentCallFrame();
                    if (luaCallFrame0 == null) {
                        Coroutine coroutine4 = this.currentCoroutine.getParent();
                        if (coroutine4 != null) {
                            LuaCallFrame luaCallFrame3 = coroutine4.currentCallFrame();
                            luaCallFrame3.push(Boolean.FALSE);
                            luaCallFrame3.push(runtimeException.getMessage());
                            luaCallFrame3.push(this.currentCoroutine.stackTrace);
                            this.currentCoroutine.destroy();
                            this.currentCoroutine = coroutine4;
                            luaCallFrame0 = this.currentCoroutine.currentCallFrame();
                            luaClosure0 = luaCallFrame0.closure;
                            prototype0 = luaClosure0.prototype;
                            ints = prototype0.code;
                            int0 = luaCallFrame0.returnBase;
                            boolean18 = false;
                        }
                        break;
                    }

                    this.currentCoroutine.addStackTrace(luaCallFrame0);
                    this.currentCoroutine.popCallFrame();
                } while (luaCallFrame0.fromLua);

                if (luaCallFrame0 != null) {
                    luaCallFrame0.closeUpvalues(0);
                }

                if (boolean18) {
                    throw runtimeException;
                }
            } catch (Exception exception1) {
                if (Core.bDebug && UIManager.defaultthread == LuaManager.thread) {
                    UIManager.debugBreakpoint(LuaManager.thread.currentfile, LuaManager.thread.currentLine - 1);
                }

                if (exception1.getMessage() != null) {
                    System.out.printf(exception1.getMessage());
                }
            }

            if (this.bReset) {
                throw new RuntimeException("lua was reset");
            }
        }

        long long5 = System.nanoTime();
        this.DoProfileTiming(string0, long0, long5);
    }

    private void DoProfileTiming(String string, long long1, long long0) {
        if (this.doProfiling) {
            double double0 = (long0 - long1) / 1000000.0;
            if (GameWindow.states.current == IngameState.instance) {
                KahluaThread.Entry entry = null;
                if (this.profileEntryMap.containsKey(string)) {
                    entry = this.profileEntryMap.get(string);
                } else {
                    entry = new KahluaThread.Entry();
                    this.profileEntryMap.put(string, entry);
                    this.profileEntries.add(entry);
                    entry.file = string;
                }

                entry.time += double0;
                Collections.sort(this.profileEntries, new KahluaThread.ProfileEntryComparitor());
            }
        }
    }

    public StringBuilder startErrorMessage() {
        this.m_stringBuilder.setLength(0);
        return this.m_stringBuilder;
    }

    public void flushErrorMessage() {
        String string = this.m_stringBuilder.toString();
        DebugLog.log(string);

        while (m_errors_list.size() >= 40) {
            m_errors_list.remove(0);
        }

        m_errors_list.add(string);
        m_error_count++;
    }

    public void doStacktraceProper(LuaCallFrame arg0) {
        if (arg0 != null) {
            StringBuilder stringBuilder = this.startErrorMessage();
            stringBuilder.append("-----------------------------------------\n");
            stringBuilder.append("STACK TRACE\n");
            stringBuilder.append("-----------------------------------------\n");
            int int0 = arg0.coroutine.getCallframeTop();

            for (int int1 = int0 - 1; int1 >= 0; int1--) {
                LuaCallFrame luaCallFrame = arg0.coroutine.getCallFrame(int1);
                stringBuilder.append(luaCallFrame.toString2());
                stringBuilder.append("\n");
            }

            this.flushErrorMessage();
        }
    }

    public void doStacktraceProper() {
        LuaCallFrame luaCallFrame = this.currentCoroutine.currentCallFrame();
        this.doStacktraceProper(luaCallFrame);
    }

    public void debugException(Exception arg0) {
        this.m_stringWriter.getBuffer().setLength(0);
        arg0.printStackTrace(this.m_printWriter);
        String string = this.m_stringWriter.toString();
        m_errors_list.add(string);
        m_error_count++;
    }

    protected Object getMetaOp(Object object, String string) {
        KahluaTable table = (KahluaTable)this.getmetatable(object, true);
        return table == null ? null : table.rawget(string);
    }

    private final Object getCompMetaOp(Object object0, Object object1, String string) {
        KahluaTable table0 = (KahluaTable)this.getmetatable(object0, true);
        KahluaTable table1 = (KahluaTable)this.getmetatable(object1, true);
        if (table0 != null && table1 != null) {
            Object object2 = table0.rawget(string);
            Object object3 = table1.rawget(string);
            return object2 == object3 && object2 != null ? object2 : null;
        } else {
            return null;
        }
    }

    private final Object getBinMetaOp(Object object1, Object object2, String string) {
        Object object0 = this.getMetaOp(object1, string);
        return object0 != null ? object0 : this.getMetaOp(object2, string);
    }

    private final Object getRegisterOrConstant(LuaCallFrame luaCallFrame, int int1, Prototype prototype) {
        int int0 = int1 - 256;
        return int0 < 0 ? luaCallFrame.get(int1) : prototype.constants[int0];
    }

    private static final int getA8(int int0) {
        return int0 >>> 6 & 0xFF;
    }

    private static final int getC9(int int0) {
        return int0 >>> 14 & 511;
    }

    private static final int getB9(int int0) {
        return int0 >>> 23 & 511;
    }

    private static final int getBx(int int0) {
        return int0 >>> 14;
    }

    private static final int getSBx(int int0) {
        return (int0 >>> 14) - 131071;
    }

    private Double primitiveMath(Double double1, Double double3, int int0) {
        double double0 = KahluaUtil.fromDouble(double1);
        double double2 = KahluaUtil.fromDouble(double3);
        double double4 = 0.0;
        switch (int0) {
            case 12:
                double4 = double0 + double2;
                break;
            case 13:
                double4 = double0 - double2;
                break;
            case 14:
                double4 = double0 * double2;
                break;
            case 15:
                double4 = double0 / double2;
                break;
            case 16:
                if (double2 == 0.0) {
                    double4 = Double.NaN;
                } else {
                    int int1 = (int)(double0 / double2);
                    double4 = double0 - int1 * double2;
                }
                break;
            case 17:
                double4 = this.platform.pow(double0, double2);
        }

        return KahluaUtil.toDouble(double4);
    }

    public Object call(Object arg0, Object arg1, Object arg2, Object arg3) {
        int int0 = this.currentCoroutine.getTop();
        this.currentCoroutine.setTop(int0 + 1 + 3);
        this.currentCoroutine.objectStack[int0] = arg0;
        this.currentCoroutine.objectStack[int0 + 1] = arg1;
        this.currentCoroutine.objectStack[int0 + 2] = arg2;
        this.currentCoroutine.objectStack[int0 + 3] = arg3;
        int int1 = this.call(3);
        Object object = null;
        if (int1 >= 1) {
            object = this.currentCoroutine.objectStack[int0];
        }

        this.currentCoroutine.setTop(int0);
        return object;
    }

    public Object call(Object object0, Object[] objects) {
        int int0 = this.currentCoroutine.getTop();
        int int1 = objects == null ? 0 : objects.length;
        this.currentCoroutine.setTop(int0 + 1 + int1);
        this.currentCoroutine.objectStack[int0] = object0;

        for (int int2 = 1; int2 <= int1; int2++) {
            this.currentCoroutine.objectStack[int0 + int2] = objects[int2 - 1];
        }

        int int3 = this.call(int1);
        Object object1 = null;
        if (int3 >= 1) {
            object1 = this.currentCoroutine.objectStack[int0];
        }

        this.currentCoroutine.setTop(int0);
        return object1;
    }

    public Object tableget(Object arg0, Object arg1) {
        Object object0 = arg0;

        for (int int0 = 100; int0 > 0; int0--) {
            boolean boolean0 = object0 instanceof KahluaTable;
            if (boolean0) {
                KahluaTable table = (KahluaTable)object0;
                Object object1 = table.rawget(arg1);
                if (object1 != null) {
                    return object1;
                }
            }

            Object object2 = this.getMetaOp(object0, "__index");
            if (object2 == null) {
                if (boolean0) {
                    return null;
                }

                StringBuilder stringBuilder = this.startErrorMessage();
                stringBuilder.append("-------------------------------------------------------------\n");
                stringBuilder.append("attempted index: " + arg1 + " of non-table: " + object0 + "\n");
                this.flushErrorMessage();
                this.doStacktraceProper(this.currentCoroutine.currentCallFrame());
                throw new RuntimeException("attempted index: " + arg1 + " of non-table: " + object0);
            }

            if (object2 instanceof JavaFunction || object2 instanceof LuaClosure) {
                return this.call(object2, arg0, arg1, null);
            }

            object0 = object2;
        }

        throw new RuntimeException("loop in gettable");
    }

    public void tableSet(Object arg0, Object arg1, Object arg2) {
        Object object0 = arg0;

        for (int int0 = 100; int0 > 0; int0--) {
            Object object1;
            if (object0 instanceof KahluaTable table) {
                if (table.rawget(arg1) != null) {
                    table.rawset(arg1, arg2);
                    return;
                }

                object1 = this.getMetaOp(object0, "__newindex");
                if (object1 == null) {
                    table.rawset(arg1, arg2);
                    return;
                }
            } else {
                object1 = this.getMetaOp(object0, "__newindex");
                if (object1 == null) {
                    this.doStacktraceProper(this.currentCoroutine.currentCallFrame());
                }

                KahluaUtil.luaAssert(object1 != null, "attempted index of non-table");
            }

            if (object1 instanceof JavaFunction || object1 instanceof LuaClosure) {
                this.call(object1, arg0, arg1, arg2);
                return;
            }

            object0 = object1;
        }

        throw new RuntimeException("loop in settable");
    }

    public void setmetatable(Object arg0, KahluaTable arg1) {
        KahluaUtil.luaAssert(arg0 != null, "Can't set metatable for nil");
        if (arg0 instanceof KahluaTable table) {
            table.setMetatable(arg1);
        } else {
            KahluaUtil.fail("Could not set metatable for object");
        }
    }

    public Object getmetatable(Object arg0, boolean arg1) {
        if (arg0 == null) {
            return null;
        } else {
            KahluaTable table0 = null;
            if (arg0 instanceof KahluaTable table1) {
                table0 = table1.getMetatable();
            } else if (table0 == null) {
                KahluaTable table2 = KahluaUtil.getClassMetatables(this.platform, this.getEnvironment());
                table0 = (KahluaTable)this.tableget(table2, arg0.getClass());
            }

            if (!arg1 && table0 != null) {
                Object object = table0.rawget("__metatable");
                if (object != null) {
                    return object;
                }
            }

            return table0;
        }
    }

    public Object[] pcall(Object object0, Object[] objects) {
        int int0 = objects == null ? 0 : objects.length;
        Coroutine coroutine = this.currentCoroutine;
        int int1 = coroutine.getTop();
        coroutine.setTop(int1 + 1 + int0);
        coroutine.objectStack[int1] = object0;
        if (int0 > 0) {
            System.arraycopy(objects, 0, coroutine.objectStack, int1 + 1, int0);
        }

        int int2 = this.pcall(int0);
        KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
        Object object1 = null;
        if (objects.length == int2) {
            object1 = objects;
        } else {
            object1 = new Object[int2];
        }

        System.arraycopy(coroutine.objectStack, int1, object1, 0, int2);
        coroutine.setTop(int1);
        return (Object[])object1;
    }

    public void pcallvoid(Object arg0, Object[] arg1) {
        int int0 = arg1 == null ? 0 : arg1.length;
        Coroutine coroutine = this.currentCoroutine;
        int int1 = coroutine.getTop();
        coroutine.setTop(int1 + 1 + int0);
        coroutine.objectStack[int1] = arg0;
        if (int0 > 0) {
            System.arraycopy(arg1, 0, coroutine.objectStack, int1 + 1, int0);
        }

        int int2 = this.pcall(int0);
        KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
        coroutine.setTop(int1);
    }

    public void pcallvoid(Object arg0, Object arg1) {
        Coroutine coroutine = this.currentCoroutine;
        int int0 = coroutine.getTop();
        coroutine.setTop(int0 + 1 + 1);
        coroutine.objectStack[int0] = arg0;
        coroutine.objectStack[int0 + 1] = arg1;
        int int1 = this.pcall(1);
        KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
        coroutine.setTop(int0);
    }

    public void pcallvoid(Object arg0, Object arg1, Object arg2) {
        Coroutine coroutine = this.currentCoroutine;
        int int0 = coroutine.getTop();
        coroutine.setTop(int0 + 1 + 2);
        coroutine.objectStack[int0] = arg0;
        coroutine.objectStack[int0 + 1] = arg1;
        coroutine.objectStack[int0 + 2] = arg2;
        int int1 = this.pcall(2);
        KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
        coroutine.setTop(int0);
    }

    public void pcallvoid(Object arg0, Object arg1, Object arg2, Object arg3) {
        Coroutine coroutine = this.currentCoroutine;
        int int0 = coroutine.getTop();
        coroutine.setTop(int0 + 1 + 3);
        coroutine.objectStack[int0] = arg0;
        coroutine.objectStack[int0 + 1] = arg1;
        coroutine.objectStack[int0 + 2] = arg2;
        coroutine.objectStack[int0 + 3] = arg3;
        int int1 = this.pcall(3);
        KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
        coroutine.setTop(int0);
    }

    public Boolean pcallBoolean(Object arg0, Object arg1) {
        Coroutine coroutine = this.currentCoroutine;
        int int0 = coroutine.getTop();
        coroutine.setTop(int0 + 1 + 1);
        coroutine.objectStack[int0] = arg0;
        coroutine.objectStack[int0 + 1] = arg1;
        int int1 = this.pcall(1);
        KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
        Boolean boolean0 = null;
        if (int1 > 1) {
            Boolean boolean1 = (Boolean)coroutine.objectStack[int0];
            if (boolean1) {
                Object object = coroutine.objectStack[int0 + 1];
                if (object instanceof Boolean) {
                    boolean0 = (Boolean)object ? Boolean.TRUE : Boolean.FALSE;
                }
            }
        }

        coroutine.setTop(int0);
        return boolean0;
    }

    public Boolean pcallBoolean(Object arg0, Object arg1, Object arg2) {
        Coroutine coroutine = this.currentCoroutine;
        int int0 = coroutine.getTop();
        coroutine.setTop(int0 + 1 + 2);
        coroutine.objectStack[int0] = arg0;
        coroutine.objectStack[int0 + 1] = arg1;
        coroutine.objectStack[int0 + 2] = arg2;
        int int1 = this.pcall(2);
        KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
        Boolean boolean0 = null;
        if (int1 > 1) {
            Boolean boolean1 = (Boolean)coroutine.objectStack[int0];
            if (boolean1) {
                Object object = coroutine.objectStack[int0 + 1];
                if (object instanceof Boolean) {
                    boolean0 = (Boolean)object ? Boolean.TRUE : Boolean.FALSE;
                }
            }
        }

        coroutine.setTop(int0);
        return boolean0;
    }

    public Boolean pcallBoolean(Object arg0, Object arg1, Object arg2, Object arg3) {
        Coroutine coroutine = this.currentCoroutine;
        int int0 = coroutine.getTop();
        coroutine.setTop(int0 + 1 + 3);
        coroutine.objectStack[int0] = arg0;
        coroutine.objectStack[int0 + 1] = arg1;
        coroutine.objectStack[int0 + 2] = arg2;
        coroutine.objectStack[int0 + 3] = arg3;
        int int1 = this.pcall(3);
        KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
        Boolean boolean0 = null;
        if (int1 > 1) {
            Boolean boolean1 = (Boolean)coroutine.objectStack[int0];
            if (boolean1) {
                Object object = coroutine.objectStack[int0 + 1];
                if (object instanceof Boolean) {
                    boolean0 = (Boolean)object ? Boolean.TRUE : Boolean.FALSE;
                }
            }
        }

        coroutine.setTop(int0);
        return boolean0;
    }

    public Boolean pcallBoolean(Object arg0, Object[] arg1) {
        int int0 = arg1 == null ? 0 : arg1.length;
        Coroutine coroutine = this.currentCoroutine;
        int int1 = coroutine.getTop();
        coroutine.setTop(int1 + 1 + int0);
        coroutine.objectStack[int1] = arg0;
        if (int0 > 0) {
            System.arraycopy(arg1, 0, coroutine.objectStack, int1 + 1, int0);
        }

        int int2 = this.pcall(int0);
        KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
        Boolean boolean0 = null;
        if (int2 > 1) {
            Boolean boolean1 = (Boolean)coroutine.objectStack[int1];
            if (boolean1) {
                Object object = coroutine.objectStack[int1 + 1];
                if (object instanceof Boolean) {
                    boolean0 = (Boolean)object ? Boolean.TRUE : Boolean.FALSE;
                }
            }
        }

        coroutine.setTop(int1);
        return boolean0;
    }

    public Object[] pcall(Object object) {
        return this.pcall(object, null);
    }

    public int pcall(int arg0) {
        Coroutine coroutine = this.currentCoroutine;
        LuaCallFrame luaCallFrame = coroutine.currentCallFrame();
        coroutine.stackTrace = "";
        int int0 = coroutine.getTop() - arg0 - 1;

        Object object0;
        Object object1;
        try {
            int int1 = coroutine.getCallframeTop();
            int int2 = this.call(arg0);
            int int3 = coroutine.getCallframeTop();
            if (int1 != int3) {
                boolean boolean0 = false;
            }

            KahluaUtil.luaAssert(int1 == int3, "error - call stack depth changed.");
            if (int1 != int3) {
                boolean boolean1 = false;
            }

            int int4 = int0 + int2 + 1;
            coroutine.setTop(int4);
            coroutine.stackCopy(int0, int0 + 1, int2);
            coroutine.objectStack[int0] = Boolean.TRUE;
            return 1 + int2;
        } catch (KahluaException kahluaException) {
            object1 = kahluaException;
            object0 = kahluaException.errorMessage;
        } catch (Throwable throwable) {
            object1 = throwable;
            object0 = throwable.getMessage() + " " + throwable.getClass().getName();
        }

        KahluaUtil.luaAssert(coroutine == this.currentCoroutine, "Internal Kahlua error - coroutine changed in pcall");
        if (luaCallFrame != null) {
            luaCallFrame.closeUpvalues(0);
        }

        coroutine.cleanCallFrames(luaCallFrame);
        if (object0 instanceof String) {
            object0 = (String)object0;
        }

        coroutine.setTop(int0 + 4);
        coroutine.objectStack[int0] = Boolean.FALSE;
        coroutine.objectStack[int0 + 1] = object0;
        coroutine.objectStack[int0 + 2] = coroutine.stackTrace;
        coroutine.objectStack[int0 + 3] = object1;
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

    public void breakpointToggle(String arg0, int arg1) {
        ArrayList arrayList;
        if (!this.BreakpointMap.containsKey(arg0)) {
            arrayList = new ArrayList();
            this.BreakpointMap.put(arg0, arrayList);
        } else {
            arrayList = this.BreakpointMap.get(arg0);
        }

        if (!arrayList.contains((long)arg1)) {
            arrayList.add((long)arg1);
        } else {
            arrayList.remove((long)arg1);
        }
    }

    public boolean hasBreakpoint(String arg0, int arg1) {
        return this.BreakpointMap.containsKey(arg0) && this.BreakpointMap.get(arg0).contains((long)arg1);
    }

    public void toggleBreakOnChange(KahluaTable arg0, Object arg1) {
        ArrayList arrayList;
        if (!this.BreakpointDataMap.containsKey(arg0)) {
            arrayList = new ArrayList();
            this.BreakpointDataMap.put(arg0, arrayList);
        } else {
            arrayList = this.BreakpointDataMap.get(arg0);
        }

        if (!arrayList.contains(arg1)) {
            arrayList.add(arg1);
        } else {
            arrayList.remove(arg1);
        }
    }

    public void toggleBreakOnRead(KahluaTable arg0, Object arg1) {
        ArrayList arrayList;
        if (!this.BreakpointReadDataMap.containsKey(arg0)) {
            arrayList = new ArrayList();
            this.BreakpointReadDataMap.put(arg0, arrayList);
        } else {
            arrayList = this.BreakpointReadDataMap.get(arg0);
        }

        if (!arrayList.contains(arg1)) {
            arrayList.add(arg1);
        } else {
            arrayList.remove(arg1);
        }
    }

    public boolean hasDataBreakpoint(KahluaTable arg0, Object arg1) {
        if (!this.BreakpointDataMap.containsKey(arg0)) {
            return false;
        } else {
            ArrayList arrayList = this.BreakpointDataMap.get(arg0);
            return arrayList.contains(arg1);
        }
    }

    public boolean hasReadDataBreakpoint(KahluaTable arg0, Object arg1) {
        if (!this.BreakpointReadDataMap.containsKey(arg0)) {
            return false;
        } else {
            ArrayList arrayList = this.BreakpointReadDataMap.get(arg0);
            return arrayList.contains(arg1);
        }
    }

    static {
        meta_ops[12] = "__add";
        meta_ops[13] = "__sub";
        meta_ops[14] = "__mul";
        meta_ops[15] = "__div";
        meta_ops[16] = "__mod";
        meta_ops[17] = "__pow";
        meta_ops[23] = "__eq";
        meta_ops[24] = "__lt";
        meta_ops[25] = "__le";
    }

    public static class Entry {
        public String file;
        public double time;
    }

    private static class ProfileEntryComparitor implements Comparator<KahluaThread.Entry> {
        public ProfileEntryComparitor() {
        }

        public int compare(KahluaThread.Entry entry0, KahluaThread.Entry entry1) {
            double double0 = entry0.time;
            double double1 = entry1.time;
            if (double0 > double1) {
                return -1;
            } else {
                return double1 > double0 ? 1 : 0;
            }
        }
    }
}
