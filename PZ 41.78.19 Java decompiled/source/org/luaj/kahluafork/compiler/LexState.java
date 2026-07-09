// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.luaj.kahluafork.compiler;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import se.krka.kahlua.vm.KahluaException;
import se.krka.kahlua.vm.Prototype;
import zombie.core.Core;

public class LexState {
    public int nCcalls;
    protected static final String RESERVED_LOCAL_VAR_FOR_CONTROL = "(for control)";
    protected static final String RESERVED_LOCAL_VAR_FOR_STATE = "(for state)";
    protected static final String RESERVED_LOCAL_VAR_FOR_GENERATOR = "(for generator)";
    protected static final String RESERVED_LOCAL_VAR_FOR_STEP = "(for step)";
    protected static final String RESERVED_LOCAL_VAR_FOR_LIMIT = "(for limit)";
    protected static final String RESERVED_LOCAL_VAR_FOR_INDEX = "(for index)";
    protected static final String[] RESERVED_LOCAL_VAR_KEYWORDS = new String[]{
        "(for control)", "(for generator)", "(for index)", "(for limit)", "(for state)", "(for step)"
    };
    private static final Hashtable RESERVED_LOCAL_VAR_KEYWORDS_TABLE = new Hashtable();
    private static final int EOZ = -1;
    private static final int MAXSRC = 80;
    private static final int MAX_INT = 2147483645;
    private static final int UCHAR_MAX = 255;
    private static final int LUAI_MAXCCALLS = 200;
    static final int NO_JUMP = -1;
    static final int OPR_ADD = 0;
    static final int OPR_SUB = 1;
    static final int OPR_MUL = 2;
    static final int OPR_DIV = 3;
    static final int OPR_MOD = 4;
    static final int OPR_POW = 5;
    static final int OPR_CONCAT = 6;
    static final int OPR_NE = 7;
    static final int OPR_EQ = 8;
    static final int OPR_LT = 9;
    static final int OPR_LE = 10;
    static final int OPR_GT = 11;
    static final int OPR_GE = 12;
    static final int OPR_AND = 13;
    static final int OPR_OR = 14;
    static final int OPR_NOBINOPR = 15;
    static final int OPR_MINUS = 0;
    static final int OPR_NOT = 1;
    static final int OPR_LEN = 2;
    static final int OPR_NOUNOPR = 3;
    static final int VVOID = 0;
    static final int VNIL = 1;
    static final int VTRUE = 2;
    static final int VFALSE = 3;
    static final int VK = 4;
    static final int VKNUM = 5;
    static final int VLOCAL = 6;
    static final int VUPVAL = 7;
    static final int VGLOBAL = 8;
    static final int VINDEXED = 9;
    static final int VJMP = 10;
    static final int VRELOCABLE = 11;
    static final int VNONRELOC = 12;
    static final int VCALL = 13;
    static final int VVARARG = 14;
    int current;
    int linenumber;
    int lastline;
    final Token t = new Token();
    final Token lookahead = new Token();
    FuncState fs;
    Reader z;
    byte[] buff;
    int nbuff;
    String source;
    static final String[] luaX_tokens;
    static final int TK_AND = 257;
    static final int TK_BREAK = 258;
    static final int TK_DO = 259;
    static final int TK_ELSE = 260;
    static final int TK_ELSEIF = 261;
    static final int TK_END = 262;
    static final int TK_FALSE = 263;
    static final int TK_FOR = 264;
    static final int TK_FUNCTION = 265;
    static final int TK_IF = 266;
    static final int TK_IN = 267;
    static final int TK_LOCAL = 268;
    static final int TK_NIL = 269;
    static final int TK_NOT = 270;
    static final int TK_OR = 271;
    static final int TK_REPEAT = 272;
    static final int TK_RETURN = 273;
    static final int TK_THEN = 274;
    static final int TK_TRUE = 275;
    static final int TK_UNTIL = 276;
    static final int TK_WHILE = 277;
    static final int TK_CONCAT = 278;
    static final int TK_DOTS = 279;
    static final int TK_EQ = 280;
    static final int TK_GE = 281;
    static final int TK_LE = 282;
    static final int TK_NE = 283;
    static final int TK_NUMBER = 284;
    static final int TK_NAME = 285;
    static final int TK_STRING = 286;
    static final int TK_EOS = 287;
    static final int FIRST_RESERVED = 257;
    static final int NUM_RESERVED = 21;
    static final Hashtable RESERVED;
    static final int[] priorityLeft;
    static final int[] priorityRight;
    static final int UNARY_PRIORITY = 8;

    private static final String LUA_QS(String string) {
        return "'" + string + "'";
    }

    private static final String LUA_QL(Object object) {
        return LUA_QS(String.valueOf(object));
    }

    public static boolean isReservedKeyword(String string) {
        return RESERVED_LOCAL_VAR_KEYWORDS_TABLE.containsKey(string);
    }

    private boolean isalnum(int int0) {
        return int0 >= 48 && int0 <= 57 || int0 >= 97 && int0 <= 122 || int0 >= 65 && int0 <= 90 || int0 == 95;
    }

    private boolean isalpha(int int0) {
        return int0 >= 97 && int0 <= 122 || int0 >= 65 && int0 <= 90;
    }

    private boolean isdigit(int int0) {
        return int0 >= 48 && int0 <= 57;
    }

    private boolean isspace(int int0) {
        return int0 <= 32;
    }

    public static Prototype compile(int int0, Reader reader, String string0, String string1) {
        if (string0 != null) {
            string1 = string0;
        } else {
            string0 = "stdin";
            string1 = "[string \"" + trim(string1, 80) + "\"]";
        }

        LexState lexState = new LexState(reader, int0, string1);
        FuncState funcState = new FuncState(lexState);
        funcState.isVararg = 2;
        funcState.f.name = string0;
        lexState.next();
        lexState.chunk();
        lexState.check(287);
        lexState.close_func();
        FuncState._assert(funcState.prev == null);
        FuncState._assert(funcState.f.numUpvalues == 0);
        FuncState._assert(lexState.fs == null);
        return funcState.f;
    }

    public LexState(Reader reader, int int0, String string) {
        this.z = reader;
        this.buff = new byte[32];
        this.lookahead.token = 287;
        this.fs = null;
        this.linenumber = 1;
        this.lastline = 1;
        this.source = string;
        this.nbuff = 0;
        this.current = int0;
        this.skipShebang();
    }

    void nextChar() {
        try {
            this.current = this.z.read();
        } catch (IOException iOException) {
            iOException.printStackTrace();
            this.current = -1;
        }
    }

    boolean currIsNewline() {
        return this.current == 10 || this.current == 13;
    }

    void save_and_next() {
        this.save(this.current);
        this.nextChar();
    }

    void save(int int0) {
        if (this.buff == null || this.nbuff + 1 > this.buff.length) {
            this.buff = FuncState.realloc(this.buff, this.nbuff * 2 + 1);
        }

        this.buff[this.nbuff++] = (byte)int0;
    }

    String token2str(int int0) {
        if (int0 < 257) {
            return iscntrl(int0) ? "char(" + int0 + ")" : String.valueOf((char)int0);
        } else {
            return luaX_tokens[int0 - 257];
        }
    }

    private static boolean iscntrl(int int0) {
        return int0 < 32;
    }

    String txtToken(int int0) {
        switch (int0) {
            case 284:
            case 285:
            case 286:
                return new String(this.buff, 0, this.nbuff);
            default:
                return this.token2str(int0);
        }
    }

    void lexerror(String string2, int int0) {
        String string0 = this.source;
        String string1;
        if (int0 != 0) {
            string1 = string0 + ":" + this.linenumber + ": " + string2 + " near `" + this.txtToken(int0) + "`";
        } else {
            string1 = string0 + ":" + this.linenumber + ": " + string2;
        }

        throw new KahluaException(string1);
    }

    private static String trim(String string, int int0) {
        return string.length() > int0 ? string.substring(0, int0 - 3) + "..." : string;
    }

    void syntaxerror(String string) {
        this.lexerror(string, this.t.token);
    }

    String newstring(byte[] bytes, int int0, int int1) {
        try {
            return new String(bytes, int0, int1, "UTF-8");
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            return null;
        }
    }

    void inclinenumber() {
        int int0 = this.current;
        FuncState._assert(this.currIsNewline());
        this.nextChar();
        if (this.currIsNewline() && this.current != int0) {
            this.nextChar();
        }

        if (++this.linenumber >= 2147483645) {
            this.syntaxerror("chunk has too many lines");
        }
    }

    private void skipShebang() {
        if (this.current == 35) {
            while (!this.currIsNewline() && this.current != -1) {
                this.nextChar();
            }
        }
    }

    boolean check_next(String string) {
        if (string.indexOf(this.current) < 0) {
            return false;
        } else {
            this.save_and_next();
            return true;
        }
    }

    void str2d(String string, Token token) {
        try {
            double double0;
            if (string.startsWith("0x")) {
                double0 = Long.parseLong(string.substring(2), 16);
            } else {
                double0 = Double.parseDouble(string);
            }

            token.r = double0;
        } catch (NumberFormatException numberFormatException) {
            this.lexerror("malformed number", 284);
        }
    }

    void read_numeral(Token token) {
        FuncState._assert(this.isdigit(this.current));

        do {
            this.save_and_next();
        } while (this.isdigit(this.current) || this.current == 46);

        if (this.check_next("Ee")) {
            this.check_next("+-");
        }

        while (this.isalnum(this.current) || this.current == 95) {
            this.save_and_next();
        }

        String string = new String(this.buff, 0, this.nbuff);
        this.str2d(string, token);
    }

    int skip_sep() {
        int int0 = 0;
        int int1 = this.current;
        FuncState._assert(int1 == 91 || int1 == 93);
        this.save_and_next();

        while (this.current == 61) {
            this.save_and_next();
            int0++;
        }

        return this.current == int1 ? int0 : -int0 - 1;
    }

    void read_long_string(Token token, int int1) {
        int int0 = 0;
        this.save_and_next();
        if (this.currIsNewline()) {
            this.inclinenumber();
        }

        boolean boolean0 = false;

        while (!boolean0) {
            switch (this.current) {
                case -1:
                    this.lexerror(token != null ? "unfinished long string" : "unfinished long comment", 287);
                    break;
                case 10:
                case 13:
                    this.save(10);
                    this.inclinenumber();
                    if (token == null) {
                        this.nbuff = 0;
                    }
                    break;
                case 91:
                    if (this.skip_sep() == int1) {
                        this.save_and_next();
                        int0++;
                    }
                    break;
                case 93:
                    if (this.skip_sep() == int1) {
                        this.save_and_next();
                        boolean0 = true;
                    }
                    break;
                default:
                    if (token != null) {
                        this.save_and_next();
                    } else {
                        this.nextChar();
                    }
            }
        }

        if (token != null) {
            token.ts = this.newstring(this.buff, 2 + int1, this.nbuff - 2 * (2 + int1));
        }
    }

    void read_string(int int1, Token token) {
        this.save_and_next();

        while (true) {
            int int0;
            label50:
            while (true) {
                if (this.current == int1) {
                    this.save_and_next();
                    token.ts = this.newstring(this.buff, 1, this.nbuff - 2);
                    return;
                }

                switch (this.current) {
                    case -1:
                        this.lexerror("unfinished string", 287);
                        break;
                    case 10:
                    case 13:
                        this.lexerror("unfinished string", 286);
                        break;
                    case 92:
                        this.nextChar();
                        int int2;
                        switch (this.current) {
                            case -1:
                                continue;
                            case 10:
                            case 13:
                                this.save(10);
                                this.inclinenumber();
                                continue;
                            case 97:
                                int0 = 7;
                                break label50;
                            case 98:
                                int0 = 8;
                                break label50;
                            case 102:
                                int0 = 12;
                                break label50;
                            case 110:
                                int0 = 10;
                                break label50;
                            case 114:
                                int0 = 13;
                                break label50;
                            case 116:
                                int0 = 9;
                                break label50;
                            case 118:
                                int0 = 11;
                                break label50;
                            default:
                                if (!this.isdigit(this.current)) {
                                    this.save_and_next();
                                    continue;
                                }

                                int2 = 0;
                                int0 = 0;
                        }

                        do {
                            int0 = 10 * int0 + (this.current - 48);
                            this.nextChar();
                        } while (++int2 < 3 && this.isdigit(this.current));

                        if (int0 > 255) {
                            this.lexerror("escape sequence too large", 286);
                        }

                        this.save(int0);
                        break;
                    default:
                        this.save_and_next();
                }
            }

            this.save(int0);
            this.nextChar();
        }
    }

    int llex(Token token) {
        this.nbuff = 0;

        while (true) {
            switch (this.current) {
                case -1:
                    return 287;
                case 10:
                case 13:
                    this.inclinenumber();
                    break;
                case 34:
                case 39:
                    this.read_string(this.current, token);
                    return 286;
                case 45:
                    this.nextChar();
                    if (this.current != 45) {
                        return 45;
                    }

                    this.nextChar();
                    if (this.current == 91) {
                        int int2 = this.skip_sep();
                        this.nbuff = 0;
                        if (int2 >= 0) {
                            this.read_long_string(null, int2);
                            this.nbuff = 0;
                            break;
                        }
                    }

                    while (!this.currIsNewline() && this.current != -1) {
                        this.nextChar();
                    }
                    break;
                case 46:
                    this.save_and_next();
                    if (this.check_next(".")) {
                        if (this.check_next(".")) {
                            return 279;
                        }

                        return 278;
                    }

                    if (!this.isdigit(this.current)) {
                        return 46;
                    }

                    this.read_numeral(token);
                    return 284;
                case 60:
                    this.nextChar();
                    if (this.current != 61) {
                        return 60;
                    }

                    this.nextChar();
                    return 282;
                case 62:
                    this.nextChar();
                    if (this.current != 61) {
                        return 62;
                    }

                    this.nextChar();
                    return 281;
                case 91:
                    int int0 = this.skip_sep();
                    if (int0 >= 0) {
                        this.read_long_string(token, int0);
                        return 286;
                    }

                    if (int0 == -1) {
                        return 91;
                    }

                    this.lexerror("invalid long string delimiter", 286);
                case 61:
                    this.nextChar();
                    if (this.current != 61) {
                        return 61;
                    }

                    this.nextChar();
                    return 280;
                case 126:
                    this.nextChar();
                    if (this.current != 61) {
                        return 126;
                    }

                    this.nextChar();
                    return 283;
                default:
                    if (!this.isspace(this.current)) {
                        if (this.isdigit(this.current)) {
                            this.read_numeral(token);
                            return 284;
                        }

                        if (!this.isalpha(this.current) && this.current != 95) {
                            int int1 = this.current;
                            this.nextChar();
                            return int1;
                        }

                        do {
                            this.save_and_next();
                        } while (this.isalnum(this.current) || this.current == 95);

                        String string = this.newstring(this.buff, 0, this.nbuff);
                        if (RESERVED.containsKey(string)) {
                            return (Integer)RESERVED.get(string);
                        }

                        token.ts = string;
                        return 285;
                    }

                    FuncState._assert(!this.currIsNewline());
                    this.nextChar();
            }
        }
    }

    void next() {
        this.lastline = this.linenumber;
        if (this.lookahead.token != 287) {
            this.t.set(this.lookahead);
            this.lookahead.token = 287;
        } else {
            this.t.token = this.llex(this.t);
        }
    }

    void lookahead() {
        FuncState._assert(this.lookahead.token == 287);
        this.lookahead.token = this.llex(this.lookahead);
    }

    boolean hasmultret(int int0) {
        return int0 == 13 || int0 == 14;
    }

    void error_expected(int int0) {
        this.syntaxerror(LUA_QS(this.token2str(int0)) + " expected");
    }

    boolean testnext(int int0) {
        if (this.t.token == int0) {
            this.next();
            return true;
        } else {
            return false;
        }
    }

    void check(int int0) {
        if (this.t.token != int0) {
            this.error_expected(int0);
        }
    }

    void checknext(int int0) {
        this.check(int0);
        this.next();
    }

    void check_condition(boolean boolean0, String string) {
        if (!boolean0) {
            this.syntaxerror(string);
        }
    }

    void check_match(int int0, int int2, int int1) {
        if (!this.testnext(int0)) {
            if (int1 == this.linenumber) {
                this.error_expected(int0);
            } else {
                this.syntaxerror(LUA_QS(this.token2str(int0)) + " expected (to close " + LUA_QS(this.token2str(int2)) + " at line " + int1 + ")");
            }
        }
    }

    String str_checkname() {
        this.check(285);
        String string = this.t.ts;
        this.next();
        return string;
    }

    void codestring(ExpDesc expDesc, String string) {
        expDesc.init(4, this.fs.stringK(string));
    }

    void checkname(ExpDesc expDesc) {
        this.codestring(expDesc, this.str_checkname());
    }

    int registerlocalvar(String string) {
        FuncState funcState = this.fs;
        if (funcState.locvars == null || funcState.nlocvars + 1 > funcState.locvars.length) {
            funcState.locvars = FuncState.realloc(funcState.locvars, funcState.nlocvars * 2 + 1);
        }

        funcState.locvars[funcState.nlocvars] = string;
        return funcState.nlocvars++;
    }

    void new_localvarliteral(String string, int int0) {
        this.new_localvar(string, int0);
    }

    void new_localvar(String string, int int0, int var3) {
        FuncState funcState = this.fs;
        funcState.checklimit(funcState.nactvar + int0 + 1, 200, "local variables");
        funcState.actvar[funcState.nactvar + int0] = (short)this.registerlocalvar(string);
        if (Core.bDebug) {
            funcState.actvarline[funcState.actvar[funcState.nactvar + int0]] = this.linenumber;
        }
    }

    void new_localvar(String string, int int0) {
        FuncState funcState = this.fs;
        funcState.checklimit(funcState.nactvar + int0 + 1, 200, "local variables");
        funcState.actvar[funcState.nactvar + int0] = (short)this.registerlocalvar(string);
        if (Core.bDebug) {
            funcState.actvarline[funcState.actvar[funcState.nactvar + int0]] = this.linenumber;
        }
    }

    void adjustlocalvars(int int0) {
        FuncState funcState = this.fs;
        funcState.nactvar += int0;
    }

    void removevars(int int0) {
        FuncState funcState = this.fs;
        funcState.nactvar = int0;
    }

    void singlevar(ExpDesc expDesc) {
        String string = this.str_checkname();
        FuncState funcState = this.fs;
        if (funcState.singlevaraux(string, expDesc, 1) == 8) {
            expDesc.info = funcState.stringK(string);
        }
    }

    void adjust_assign(int int1, int int2, ExpDesc expDesc) {
        FuncState funcState = this.fs;
        int int0 = int1 - int2;
        if (this.hasmultret(expDesc.k)) {
            if (++int0 < 0) {
                int0 = 0;
            }

            funcState.setreturns(expDesc, int0);
            if (int0 > 1) {
                funcState.reserveregs(int0 - 1);
            }
        } else {
            if (expDesc.k != 0) {
                funcState.exp2nextreg(expDesc);
            }

            if (int0 > 0) {
                int int3 = funcState.freereg;
                funcState.reserveregs(int0);
                funcState.nil(int3, int0);
            }
        }
    }

    void enterlevel() {
        if (++this.nCcalls > 200) {
            this.lexerror("chunk has too many syntax levels", 0);
        }
    }

    void leavelevel() {
        this.nCcalls--;
    }

    void pushclosure(FuncState funcState1, ExpDesc expDesc) {
        FuncState funcState0 = this.fs;
        Prototype prototype = funcState0.f;
        if (prototype.prototypes == null || funcState0.np + 1 > prototype.prototypes.length) {
            prototype.prototypes = FuncState.realloc(prototype.prototypes, funcState0.np * 2 + 1);
        }

        prototype.prototypes[funcState0.np++] = funcState1.f;
        expDesc.init(11, funcState0.codeABx(36, 0, funcState0.np - 1));

        for (int int0 = 0; int0 < funcState1.f.numUpvalues; int0++) {
            int int1 = funcState1.upvalues_k[int0] == 6 ? 0 : 4;
            funcState0.codeABC(int1, 0, funcState1.upvalues_info[int0], 0);
        }
    }

    void close_func() {
        FuncState funcState = this.fs;
        Prototype prototype = funcState.f;
        prototype.isVararg = funcState.isVararg != 0;
        this.removevars(0);
        funcState.ret(0, 0);
        prototype.code = FuncState.realloc(prototype.code, funcState.pc);
        prototype.lines = FuncState.realloc(prototype.lines, funcState.pc);
        prototype.constants = FuncState.realloc(prototype.constants, funcState.nk);
        prototype.prototypes = FuncState.realloc(prototype.prototypes, funcState.np);
        funcState.locvars = FuncState.realloc(funcState.locvars, funcState.nlocvars);
        if (Core.bDebug) {
            prototype.locvars = funcState.locvars;
            prototype.locvarlines = funcState.actvarline;
        }

        funcState.upvalues = FuncState.realloc(funcState.upvalues, prototype.numUpvalues);
        FuncState._assert(funcState.bl == null);
        this.fs = funcState.prev;
    }

    void field(ExpDesc expDesc1) {
        FuncState funcState = this.fs;
        ExpDesc expDesc0 = new ExpDesc();
        funcState.exp2anyreg(expDesc1);
        this.next();
        this.checkname(expDesc0);
        funcState.indexed(expDesc1, expDesc0);
    }

    void yindex(ExpDesc expDesc) {
        this.next();
        this.expr(expDesc);
        this.fs.exp2val(expDesc);
        this.checknext(93);
    }

    void recfield(ConsControl consControl) {
        FuncState funcState = this.fs;
        int int0 = this.fs.freereg;
        ExpDesc expDesc0 = new ExpDesc();
        ExpDesc expDesc1 = new ExpDesc();
        if (this.t.token == 285) {
            funcState.checklimit(consControl.nh, 2147483645, "items in a constructor");
            this.checkname(expDesc0);
        } else {
            this.yindex(expDesc0);
        }

        consControl.nh++;
        this.checknext(61);
        int int1 = funcState.exp2RK(expDesc0);
        this.expr(expDesc1);
        funcState.codeABC(9, consControl.t.info, int1, funcState.exp2RK(expDesc1));
        funcState.freereg = int0;
    }

    void listfield(ConsControl consControl) {
        this.expr(consControl.v);
        this.fs.checklimit(consControl.na, 2147483645, "items in a constructor");
        consControl.na++;
        consControl.tostore++;
    }

    void constructor(ExpDesc expDesc) {
        FuncState funcState = this.fs;
        int int0 = this.linenumber;
        int int1 = funcState.codeABC(10, 0, 0, 0);
        ConsControl consControl = new ConsControl();
        consControl.na = consControl.nh = consControl.tostore = 0;
        consControl.t = expDesc;
        expDesc.init(11, int1);
        consControl.v.init(0, 0);
        funcState.exp2nextreg(expDesc);
        this.checknext(123);

        do {
            FuncState._assert(consControl.v.k == 0 || consControl.tostore > 0);
            if (this.t.token == 125) {
                break;
            }

            funcState.closelistfield(consControl);
            switch (this.t.token) {
                case 91:
                    this.recfield(consControl);
                    break;
                case 285:
                    this.lookahead();
                    if (this.lookahead.token != 61) {
                        this.listfield(consControl);
                    } else {
                        this.recfield(consControl);
                    }
                    break;
                default:
                    this.listfield(consControl);
            }
        } while (this.testnext(44) || this.testnext(59));

        this.check_match(125, 123, int0);
        funcState.lastlistfield(consControl);
        InstructionPtr instructionPtr = new InstructionPtr(funcState.f.code, int1);
        FuncState.SETARG_B(instructionPtr, luaO_int2fb(consControl.na));
        FuncState.SETARG_C(instructionPtr, luaO_int2fb(consControl.nh));
    }

    static int luaO_int2fb(int int1) {
        int int0;
        for (int0 = 0; int1 >= 16; int0++) {
            int1 = int1 + 1 >> 1;
        }

        return int1 < 8 ? int1 : int0 + 1 << 3 | int1 - 8;
    }

    void parlist() {
        FuncState funcState = this.fs;
        Prototype prototype = funcState.f;
        int int0 = 0;
        funcState.isVararg = 0;
        if (this.t.token != 41) {
            do {
                switch (this.t.token) {
                    case 279:
                        this.next();
                        funcState.isVararg |= 2;
                        break;
                    case 285:
                        this.new_localvar(this.str_checkname(), int0++);
                        break;
                    default:
                        this.syntaxerror("<name> or " + LUA_QL("...") + " expected");
                }
            } while (funcState.isVararg == 0 && this.testnext(44));
        }

        this.adjustlocalvars(int0);
        prototype.numParams = funcState.nactvar - (funcState.isVararg & 1);
        funcState.reserveregs(funcState.nactvar);
    }

    void body(ExpDesc expDesc, boolean boolean0, int int0) {
        FuncState funcState = new FuncState(this, this.t.ts);
        funcState.linedefined = int0;
        this.checknext(40);
        if (boolean0) {
            this.new_localvarliteral("self", 0);
            this.adjustlocalvars(1);
        }

        this.parlist();
        this.checknext(41);
        this.chunk();
        funcState.lastlinedefined = this.linenumber;
        this.check_match(262, 265, int0);
        this.close_func();
        this.pushclosure(funcState, expDesc);
    }

    int explist1(ExpDesc expDesc) {
        int int0 = 1;
        this.expr(expDesc);

        while (this.testnext(44)) {
            this.fs.exp2nextreg(expDesc);
            this.expr(expDesc);
            int0++;
        }

        return int0;
    }

    void funcargs(ExpDesc expDesc1) {
        FuncState funcState = this.fs;
        ExpDesc expDesc0 = new ExpDesc();
        int int0 = this.linenumber;
        switch (this.t.token) {
            case 40:
                if (int0 != this.lastline) {
                    this.syntaxerror("ambiguous syntax (function call x new statement)");
                }

                this.next();
                if (this.t.token == 41) {
                    expDesc0.k = 0;
                } else {
                    this.explist1(expDesc0);
                    funcState.setmultret(expDesc0);
                }

                this.check_match(41, 40, int0);
                break;
            case 123:
                this.constructor(expDesc0);
                break;
            case 286:
                this.codestring(expDesc0, this.t.ts);
                this.next();
                break;
            default:
                this.syntaxerror("function arguments expected");
                return;
        }

        FuncState._assert(expDesc1.k == 12);
        int int1 = expDesc1.info;
        int int2;
        if (this.hasmultret(expDesc0.k)) {
            int2 = -1;
        } else {
            if (expDesc0.k != 0) {
                funcState.exp2nextreg(expDesc0);
            }

            int2 = funcState.freereg - (int1 + 1);
        }

        expDesc1.init(13, funcState.codeABC(28, int1, int2 + 1, 2));
        funcState.fixline(int0);
        funcState.freereg = int1 + 1;
    }

    void prefixexp(ExpDesc expDesc) {
        switch (this.t.token) {
            case 40:
                int int0 = this.linenumber;
                this.next();
                this.expr(expDesc);
                this.check_match(41, 40, int0);
                this.fs.dischargevars(expDesc);
                return;
            case 285:
                this.singlevar(expDesc);
                return;
            default:
                this.syntaxerror("unexpected symbol");
        }
    }

    void primaryexp(ExpDesc expDesc0) {
        FuncState funcState = this.fs;
        this.prefixexp(expDesc0);

        while (true) {
            switch (this.t.token) {
                case 40:
                case 123:
                case 286:
                    funcState.exp2nextreg(expDesc0);
                    this.funcargs(expDesc0);
                    break;
                case 46:
                    this.field(expDesc0);
                    break;
                case 58:
                    ExpDesc expDesc2 = new ExpDesc();
                    this.next();
                    this.checkname(expDesc2);
                    funcState.self(expDesc0, expDesc2);
                    this.funcargs(expDesc0);
                    break;
                case 91:
                    ExpDesc expDesc1 = new ExpDesc();
                    funcState.exp2anyreg(expDesc0);
                    this.yindex(expDesc1);
                    funcState.indexed(expDesc0, expDesc1);
                    break;
                default:
                    return;
            }
        }
    }

    void simpleexp(ExpDesc expDesc) {
        switch (this.t.token) {
            case 123:
                this.constructor(expDesc);
                return;
            case 263:
                expDesc.init(3, 0);
                break;
            case 265:
                this.next();
                this.body(expDesc, false, this.linenumber);
                return;
            case 269:
                expDesc.init(1, 0);
                break;
            case 275:
                expDesc.init(2, 0);
                break;
            case 279:
                FuncState funcState = this.fs;
                this.check_condition(funcState.isVararg != 0, "cannot use " + LUA_QL("...") + " outside a vararg function");
                funcState.isVararg &= -5;
                expDesc.init(14, funcState.codeABC(37, 0, 1, 0));
                break;
            case 284:
                expDesc.init(5, 0);
                expDesc.setNval(this.t.r);
                break;
            case 286:
                this.codestring(expDesc, this.t.ts);
                break;
            default:
                this.primaryexp(expDesc);
                return;
        }

        this.next();
    }

    int getunopr(int int0) {
        switch (int0) {
            case 35:
                return 2;
            case 45:
                return 0;
            case 270:
                return 1;
            default:
                return 3;
        }
    }

    int getbinopr(int int0) {
        switch (int0) {
            case 37:
                return 4;
            case 42:
                return 2;
            case 43:
                return 0;
            case 45:
                return 1;
            case 47:
                return 3;
            case 60:
                return 9;
            case 62:
                return 11;
            case 94:
                return 5;
            case 257:
                return 13;
            case 271:
                return 14;
            case 278:
                return 6;
            case 280:
                return 8;
            case 281:
                return 12;
            case 282:
                return 10;
            case 283:
                return 7;
            default:
                return 15;
        }
    }

    int subexpr(ExpDesc expDesc0, int int2) {
        this.enterlevel();
        int int0 = this.getunopr(this.t.token);
        if (int0 != 3) {
            this.next();
            this.subexpr(expDesc0, 8);
            this.fs.prefix(int0, expDesc0);
        } else {
            this.simpleexp(expDesc0);
        }

        int int1 = this.getbinopr(this.t.token);

        while (int1 != 15 && priorityLeft[int1] > int2) {
            ExpDesc expDesc1 = new ExpDesc();
            this.next();
            this.fs.infix(int1, expDesc0);
            int int3 = this.subexpr(expDesc1, priorityRight[int1]);
            this.fs.posfix(int1, expDesc0, expDesc1);
            int1 = int3;
        }

        this.leavelevel();
        return int1;
    }

    void expr(ExpDesc expDesc) {
        this.subexpr(expDesc, 0);
    }

    boolean block_follow(int int0) {
        switch (int0) {
            case 260:
            case 261:
            case 262:
            case 276:
            case 287:
                return true;
            default:
                return false;
        }
    }

    void block() {
        FuncState funcState = this.fs;
        BlockCnt blockCnt = new BlockCnt();
        funcState.enterblock(blockCnt, false);
        this.chunk();
        FuncState._assert(blockCnt.breaklist == -1);
        funcState.leaveblock();
    }

    void check_conflict(LHS_assign lHS_assign, ExpDesc expDesc) {
        FuncState funcState = this.fs;
        int int0 = funcState.freereg;
        boolean boolean0 = false;

        while (lHS_assign != null) {
            if (lHS_assign.v.k == 9) {
                if (lHS_assign.v.info == expDesc.info) {
                    boolean0 = true;
                    lHS_assign.v.info = int0;
                }

                if (lHS_assign.v.aux == expDesc.info) {
                    boolean0 = true;
                    lHS_assign.v.aux = int0;
                }
            }

            lHS_assign = lHS_assign.prev;
        }

        if (boolean0) {
            funcState.codeABC(0, funcState.freereg, expDesc.info, 0);
            funcState.reserveregs(1);
        }
    }

    void assignment(LHS_assign lHS_assign0, int int0) {
        ExpDesc expDesc = new ExpDesc();
        this.check_condition(6 <= lHS_assign0.v.k && lHS_assign0.v.k <= 9, "syntax error");
        if (this.testnext(44)) {
            LHS_assign lHS_assign1 = new LHS_assign();
            lHS_assign1.prev = lHS_assign0;
            this.primaryexp(lHS_assign1.v);
            if (lHS_assign1.v.k == 6) {
                this.check_conflict(lHS_assign0, lHS_assign1.v);
            }

            this.assignment(lHS_assign1, int0 + 1);
        } else {
            this.checknext(61);
            int int1 = this.explist1(expDesc);
            if (int1 == int0) {
                this.fs.setoneret(expDesc);
                this.fs.storevar(lHS_assign0.v, expDesc);
                return;
            }

            this.adjust_assign(int0, int1, expDesc);
            if (int1 > int0) {
                this.fs.freereg -= int1 - int0;
            }
        }

        expDesc.init(12, this.fs.freereg - 1);
        this.fs.storevar(lHS_assign0.v, expDesc);
    }

    int cond() {
        ExpDesc expDesc = new ExpDesc();
        this.expr(expDesc);
        if (expDesc.k == 1) {
            expDesc.k = 3;
        }

        this.fs.goiftrue(expDesc);
        return expDesc.f;
    }

    void breakstat() {
        FuncState funcState = this.fs;
        BlockCnt blockCnt = funcState.bl;

        boolean boolean0;
        for (boolean0 = false; blockCnt != null && !blockCnt.isbreakable; blockCnt = blockCnt.previous) {
            boolean0 |= blockCnt.upval;
        }

        if (blockCnt == null) {
            this.syntaxerror("no loop to break");
        }

        if (boolean0) {
            funcState.codeABC(35, blockCnt.nactvar, 0, 0);
        }

        blockCnt.breaklist = funcState.concat(blockCnt.breaklist, funcState.jump());
    }

    void whilestat(int int2) {
        FuncState funcState = this.fs;
        BlockCnt blockCnt = new BlockCnt();
        this.next();
        int int0 = funcState.getlabel();
        int int1 = this.cond();
        funcState.enterblock(blockCnt, true);
        this.checknext(259);
        this.block();
        funcState.patchlist(funcState.jump(), int0);
        this.check_match(262, 277, int2);
        funcState.leaveblock();
        funcState.patchtohere(int1);
    }

    void repeatstat(int int1) {
        FuncState funcState = this.fs;
        int int0 = funcState.getlabel();
        BlockCnt blockCnt0 = new BlockCnt();
        BlockCnt blockCnt1 = new BlockCnt();
        funcState.enterblock(blockCnt0, true);
        funcState.enterblock(blockCnt1, false);
        this.next();
        this.chunk();
        this.check_match(276, 272, int1);
        int int2 = this.cond();
        if (!blockCnt1.upval) {
            funcState.leaveblock();
            funcState.patchlist(int2, int0);
        } else {
            this.breakstat();
            funcState.patchtohere(int2);
            funcState.leaveblock();
            funcState.patchlist(funcState.jump(), int0);
        }

        funcState.leaveblock();
    }

    int exp1() {
        ExpDesc expDesc = new ExpDesc();
        this.expr(expDesc);
        int int0 = expDesc.k;
        this.fs.exp2nextreg(expDesc);
        return int0;
    }

    void forbody(int int1, int int4, int int2, boolean boolean0) {
        BlockCnt blockCnt = new BlockCnt();
        FuncState funcState = this.fs;
        this.adjustlocalvars(3);
        this.checknext(259);
        int int0 = boolean0 ? funcState.codeAsBx(32, int1, -1) : funcState.jump();
        funcState.enterblock(blockCnt, false);
        this.adjustlocalvars(int2);
        funcState.reserveregs(int2);
        this.block();
        funcState.leaveblock();
        funcState.patchtohere(int0);
        int int3 = boolean0 ? funcState.codeAsBx(31, int1, -1) : funcState.codeABC(33, int1, 0, int2);
        funcState.fixline(int4);
        funcState.patchlist(boolean0 ? int3 : funcState.jump(), int0 + 1);
    }

    void fornum(String string, int int1) {
        FuncState funcState = this.fs;
        int int0 = funcState.freereg;
        this.new_localvarliteral("(for index)", 0);
        this.new_localvarliteral("(for limit)", 1);
        this.new_localvarliteral("(for step)", 2);
        this.new_localvar(string, 3);
        this.checknext(61);
        this.exp1();
        this.checknext(44);
        this.exp1();
        if (this.testnext(44)) {
            this.exp1();
        } else {
            funcState.codeABx(1, funcState.freereg, funcState.numberK(1.0));
            funcState.reserveregs(1);
        }

        this.forbody(int0, int1, 1, true);
    }

    void forlist(String string) {
        FuncState funcState = this.fs;
        ExpDesc expDesc = new ExpDesc();
        int int0 = 0;
        int int1 = funcState.freereg;
        this.new_localvarliteral("(for generator)", int0++);
        this.new_localvarliteral("(for state)", int0++);
        this.new_localvarliteral("(for control)", int0++);
        this.new_localvar(string, int0++);

        while (this.testnext(44)) {
            this.new_localvar(this.str_checkname(), int0++);
        }

        this.checknext(267);
        int int2 = this.linenumber;
        this.adjust_assign(3, this.explist1(expDesc), expDesc);
        funcState.checkstack(3);
        this.forbody(int1, int2, int0 - 3, false);
    }

    void forstat(int int0) {
        FuncState funcState = this.fs;
        BlockCnt blockCnt = new BlockCnt();
        funcState.enterblock(blockCnt, true);
        this.next();
        String string = this.str_checkname();
        switch (this.t.token) {
            case 44:
            case 267:
                this.forlist(string);
                break;
            case 61:
                this.fornum(string, int0);
                break;
            default:
                this.syntaxerror(LUA_QL("=") + " or " + LUA_QL("in") + " expected");
        }

        this.check_match(262, 264, int0);
        funcState.leaveblock();
    }

    int test_then_block() {
        this.next();
        int int0 = this.cond();
        this.checknext(274);
        this.block();
        return int0;
    }

    void ifstat(int int2) {
        FuncState funcState = this.fs;
        int int0 = -1;

        int int1;
        for (int1 = this.test_then_block(); this.t.token == 261; int1 = this.test_then_block()) {
            int0 = funcState.concat(int0, funcState.jump());
            funcState.patchtohere(int1);
        }

        if (this.t.token == 260) {
            int0 = funcState.concat(int0, funcState.jump());
            funcState.patchtohere(int1);
            this.next();
            this.block();
        } else {
            int0 = funcState.concat(int0, int1);
        }

        funcState.patchtohere(int0);
        this.check_match(262, 266, int2);
    }

    void localfunc() {
        ExpDesc expDesc0 = new ExpDesc();
        ExpDesc expDesc1 = new ExpDesc();
        FuncState funcState = this.fs;
        this.new_localvar(this.str_checkname(), 0);
        expDesc0.init(6, funcState.freereg);
        funcState.reserveregs(1);
        this.adjustlocalvars(1);
        this.body(expDesc1, false, this.linenumber);
        funcState.storevar(expDesc0, expDesc1);
    }

    void localstat(int int1) {
        int int0 = 0;
        ExpDesc expDesc = new ExpDesc();

        do {
            this.new_localvar(this.str_checkname(), int0++, int1);
        } while (this.testnext(44));

        int int2;
        if (this.testnext(61)) {
            int2 = this.explist1(expDesc);
        } else {
            expDesc.k = 0;
            int2 = 0;
        }

        this.adjust_assign(int0, int2, expDesc);
        this.adjustlocalvars(int0);
    }

    boolean funcname(ExpDesc expDesc) {
        boolean boolean0 = false;
        this.singlevar(expDesc);

        while (this.t.token == 46) {
            this.field(expDesc);
        }

        if (this.t.token == 58) {
            boolean0 = true;
            this.field(expDesc);
        }

        return boolean0;
    }

    void funcstat(int int0) {
        ExpDesc expDesc0 = new ExpDesc();
        ExpDesc expDesc1 = new ExpDesc();
        this.next();
        boolean boolean0 = this.funcname(expDesc0);
        this.body(expDesc1, boolean0, int0);
        this.fs.storevar(expDesc0, expDesc1);
        this.fs.fixline(int0);
    }

    void exprstat() {
        FuncState funcState = this.fs;
        LHS_assign lHS_assign = new LHS_assign();
        this.primaryexp(lHS_assign.v);
        if (lHS_assign.v.k == 13) {
            FuncState.SETARG_C(funcState.getcodePtr(lHS_assign.v), 1);
        } else {
            lHS_assign.prev = null;
            this.assignment(lHS_assign, 1);
        }
    }

    void retstat() {
        FuncState funcState = this.fs;
        ExpDesc expDesc = new ExpDesc();
        this.next();
        int int0;
        int int1;
        if (!this.block_follow(this.t.token) && this.t.token != 59) {
            int1 = this.explist1(expDesc);
            if (this.hasmultret(expDesc.k)) {
                funcState.setmultret(expDesc);
                if (expDesc.k == 13 && int1 == 1) {
                    FuncState.SET_OPCODE(funcState.getcodePtr(expDesc), 29);
                    FuncState._assert(FuncState.GETARG_A(funcState.getcode(expDesc)) == funcState.nactvar);
                }

                int0 = funcState.nactvar;
                int1 = -1;
            } else if (int1 == 1) {
                int0 = funcState.exp2anyreg(expDesc);
            } else {
                funcState.exp2nextreg(expDesc);
                int0 = funcState.nactvar;
                FuncState._assert(int1 == funcState.freereg - int0);
            }
        } else {
            int1 = 0;
            int0 = 0;
        }

        funcState.ret(int0, int1);
    }

    boolean statement() {
        int int0 = this.linenumber;
        switch (this.t.token) {
            case 258:
                this.next();
                this.breakstat();
                return true;
            case 259:
                this.next();
                this.block();
                this.check_match(262, 259, int0);
                return false;
            case 260:
            case 261:
            case 262:
            case 263:
            case 267:
            case 269:
            case 270:
            case 271:
            case 274:
            case 275:
            case 276:
            default:
                this.exprstat();
                return false;
            case 264:
                this.forstat(int0);
                return false;
            case 265:
                this.funcstat(int0);
                return false;
            case 266:
                this.ifstat(int0);
                return false;
            case 268:
                this.next();
                if (this.testnext(265)) {
                    this.localfunc();
                } else {
                    this.localstat(int0);
                }

                return false;
            case 272:
                this.repeatstat(int0);
                return false;
            case 273:
                this.retstat();
                return true;
            case 277:
                this.whilestat(int0);
                return false;
        }
    }

    void chunk() {
        boolean boolean0 = false;
        this.enterlevel();

        while (!boolean0 && !this.block_follow(this.t.token)) {
            boolean0 = this.statement();
            this.testnext(59);
            FuncState._assert(this.fs.f.maxStacksize >= this.fs.freereg && this.fs.freereg >= this.fs.nactvar);
            this.fs.freereg = this.fs.nactvar;
        }

        this.leavelevel();
    }

    static {
        for (int int0 = 0; int0 < RESERVED_LOCAL_VAR_KEYWORDS.length; int0++) {
            RESERVED_LOCAL_VAR_KEYWORDS_TABLE.put(RESERVED_LOCAL_VAR_KEYWORDS[int0], Boolean.TRUE);
        }

        luaX_tokens = new String[]{
            "and",
            "break",
            "do",
            "else",
            "elseif",
            "end",
            "false",
            "for",
            "function",
            "if",
            "in",
            "local",
            "nil",
            "not",
            "or",
            "repeat",
            "return",
            "then",
            "true",
            "until",
            "while",
            "..",
            "...",
            "==",
            ">=",
            "<=",
            "~=",
            "<number>",
            "<name>",
            "<string>",
            "<eof>"
        };
        RESERVED = new Hashtable();

        for (int int1 = 0; int1 < 21; int1++) {
            String string = luaX_tokens[int1];
            RESERVED.put(string, new Integer(257 + int1));
        }

        priorityLeft = new int[]{6, 6, 7, 7, 7, 10, 5, 3, 3, 3, 3, 3, 3, 2, 1};
        priorityRight = new int[]{6, 6, 7, 7, 7, 9, 4, 3, 3, 3, 3, 3, 3, 2, 1};
    }
}
