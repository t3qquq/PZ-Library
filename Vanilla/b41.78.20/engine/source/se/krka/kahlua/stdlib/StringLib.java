/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zombie.Lua.LuaManager
 */
package se.krka.kahlua.stdlib;

import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaException;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;
import zombie.Lua.LuaManager;

public final class StringLib
implements JavaFunction {
    private static final int SUB = 0;
    private static final int CHAR = 1;
    private static final int BYTE = 2;
    private static final int LOWER = 3;
    private static final int UPPER = 4;
    private static final int REVERSE = 5;
    private static final int FORMAT = 6;
    private static final int FIND = 7;
    private static final int MATCH = 8;
    private static final int GSUB = 9;
    private static final int TRIM = 10;
    private static final int SPLIT = 11;
    private static final int SORT = 12;
    private static final int CONTAINS = 13;
    private static final int NUM_FUNCTIONS = 14;
    private static final boolean[] SPECIALS = new boolean[256];
    private static final int LUA_MAXCAPTURES = 32;
    private static final char L_ESC = '%';
    private static final int CAP_UNFINISHED = -1;
    private static final int CAP_POSITION = -2;
    private static final String[] names;
    private static final StringLib[] functions;
    private static final Class STRING_CLASS;
    private final int methodId;
    private static final char[] digits;

    public StringLib(int n) {
        this.methodId = n;
    }

    public static void register(Platform platform, KahluaTable kahluaTable) {
        KahluaTable kahluaTable2 = platform.newTable();
        for (int i = 0; i < 14; ++i) {
            kahluaTable2.rawset(names[i], (Object)functions[i]);
        }
        kahluaTable2.rawset("__index", (Object)kahluaTable2);
        KahluaTable kahluaTable3 = KahluaUtil.getClassMetatables(platform, kahluaTable);
        kahluaTable3.rawset(STRING_CLASS, (Object)kahluaTable2);
        kahluaTable.rawset("string", (Object)kahluaTable2);
    }

    public String toString() {
        return names[this.methodId];
    }

    @Override
    public int call(LuaCallFrame luaCallFrame, int n) {
        switch (this.methodId) {
            case 0: {
                return this.sub(luaCallFrame, n);
            }
            case 1: {
                return this.stringChar(luaCallFrame, n);
            }
            case 2: {
                return this.stringByte(luaCallFrame, n);
            }
            case 3: {
                return this.lower(luaCallFrame, n);
            }
            case 4: {
                return this.upper(luaCallFrame, n);
            }
            case 5: {
                return this.reverse(luaCallFrame, n);
            }
            case 6: {
                return this.format(luaCallFrame, n);
            }
            case 7: {
                return StringLib.findAux(luaCallFrame, true);
            }
            case 8: {
                return StringLib.findAux(luaCallFrame, false);
            }
            case 9: {
                return StringLib.gsub(luaCallFrame, n);
            }
            case 10: {
                return StringLib.trim(luaCallFrame, n);
            }
            case 11: {
                return StringLib.split(luaCallFrame, n);
            }
            case 12: {
                return StringLib.sort(luaCallFrame, n);
            }
            case 13: {
                return this.contains(luaCallFrame, n);
            }
        }
        return 0;
    }

    private long unsigned(long l) {
        if (l < 0L) {
            l += 0x100000000L;
        }
        return l;
    }

    private int format(LuaCallFrame luaCallFrame, int n) {
        String string = KahluaUtil.getStringArg(luaCallFrame, 1, names[6]);
        int n2 = string.length();
        int n3 = 2;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < n2; ++i) {
            char c = string.charAt(i);
            if (c == '%') {
                int n4;
                int n5;
                KahluaUtil.luaAssert(++i < n2, "incomplete option to 'format'");
                c = string.charAt(i);
                if (c == '%') {
                    stringBuilder.append('%');
                    continue;
                }
                boolean bl = false;
                boolean bl2 = false;
                boolean bl3 = false;
                boolean bl4 = false;
                boolean bl5 = false;
                block38: while (true) {
                    switch (c) {
                        case '-': {
                            bl3 = true;
                            break;
                        }
                        case '+': {
                            bl4 = true;
                            break;
                        }
                        case ' ': {
                            bl5 = true;
                            break;
                        }
                        case '#': {
                            bl = true;
                            break;
                        }
                        case '0': {
                            bl2 = true;
                            break;
                        }
                        default: {
                            break block38;
                        }
                    }
                    KahluaUtil.luaAssert(++i < n2, "incomplete option to 'format'");
                    c = string.charAt(i);
                }
                int n6 = 0;
                while (c >= '0' && c <= '9') {
                    n6 = 10 * n6 + c - 48;
                    KahluaUtil.luaAssert(++i < n2, "incomplete option to 'format'");
                    c = string.charAt(i);
                }
                int n7 = 0;
                boolean bl6 = false;
                if (c == '.') {
                    bl6 = true;
                    KahluaUtil.luaAssert(++i < n2, "incomplete option to 'format'");
                    c = string.charAt(i);
                    while (c >= '0' && c <= '9') {
                        n7 = 10 * n7 + c - 48;
                        KahluaUtil.luaAssert(++i < n2, "incomplete option to 'format'");
                        c = string.charAt(i);
                    }
                }
                if (bl3) {
                    bl2 = false;
                }
                int n8 = 10;
                boolean bl7 = false;
                int n9 = 6;
                String string2 = "";
                switch (c) {
                    case 'c': {
                        bl2 = false;
                        break;
                    }
                    case 'o': {
                        n8 = 8;
                        n9 = 1;
                        string2 = "0";
                        break;
                    }
                    case 'x': {
                        n8 = 16;
                        n9 = 1;
                        string2 = "0x";
                        break;
                    }
                    case 'X': {
                        n8 = 16;
                        n9 = 1;
                        bl7 = true;
                        string2 = "0X";
                        break;
                    }
                    case 'u': {
                        n9 = 1;
                        break;
                    }
                    case 'd': 
                    case 'i': {
                        n9 = 1;
                        break;
                    }
                    case 'e': {
                        break;
                    }
                    case 'E': {
                        bl7 = true;
                        break;
                    }
                    case 'g': {
                        break;
                    }
                    case 'G': {
                        bl7 = true;
                        break;
                    }
                    case 'f': {
                        break;
                    }
                    case 's': {
                        bl2 = false;
                        break;
                    }
                    case 'q': {
                        n6 = 0;
                        break;
                    }
                    default: {
                        throw new RuntimeException("invalid option '%" + c + "' to 'format'");
                    }
                }
                if (!bl6) {
                    n7 = n9;
                }
                if (bl6 && n8 != 10) {
                    bl2 = false;
                }
                char c2 = bl2 ? (char)'0' : ' ';
                int n10 = stringBuilder.length();
                if (!bl3) {
                    this.extend(stringBuilder, n6, c2);
                }
                switch (c) {
                    case 'c': {
                        stringBuilder.append((char)this.getDoubleArg(luaCallFrame, n3).shortValue());
                        break;
                    }
                    case 'X': 
                    case 'o': 
                    case 'u': 
                    case 'x': {
                        long l = this.getDoubleArg(luaCallFrame, n3).longValue();
                        l = this.unsigned(l);
                        if (bl) {
                            if (n8 == 8) {
                                n5 = 0;
                                long l2 = l;
                                while (l2 > 0L) {
                                    l2 /= 8L;
                                    ++n5;
                                }
                                if (n7 <= n5) {
                                    stringBuilder.append(string2);
                                }
                            } else if (n8 == 16 && l != 0L) {
                                stringBuilder.append(string2);
                            }
                        }
                        if (l == 0L && n7 <= 0) break;
                        StringLib.stringBufferAppend(stringBuilder, l, n8, false, n7);
                        break;
                    }
                    case 'd': 
                    case 'i': {
                        Double d = this.getDoubleArg(luaCallFrame, n3);
                        long l = d.longValue();
                        if (l < 0L) {
                            stringBuilder.append('-');
                            l = -l;
                        } else if (bl4) {
                            stringBuilder.append('+');
                        } else if (bl5) {
                            stringBuilder.append(' ');
                        }
                        if (l == 0L && n7 <= 0) break;
                        StringLib.stringBufferAppend(stringBuilder, l, n8, false, n7);
                        break;
                    }
                    case 'E': 
                    case 'e': 
                    case 'f': {
                        Double d = this.getDoubleArg(luaCallFrame, n3);
                        n4 = d.isInfinite() || d.isNaN() ? 1 : 0;
                        double d2 = d;
                        if (KahluaUtil.isNegative(d2)) {
                            if (n4 == 0) {
                                stringBuilder.append('-');
                            }
                            d2 = -d2;
                        } else if (bl4) {
                            stringBuilder.append('+');
                        } else if (bl5) {
                            stringBuilder.append(' ');
                        }
                        if (n4 != 0) {
                            stringBuilder.append(KahluaUtil.numberToString(d));
                            break;
                        }
                        if (c == 'f') {
                            this.appendPrecisionNumber(stringBuilder, d2, n7, bl);
                            break;
                        }
                        this.appendScientificNumber(stringBuilder, d2, n7, bl, false);
                        break;
                    }
                    case 'G': 
                    case 'g': {
                        Double d;
                        if (n7 <= 0) {
                            n7 = 1;
                        }
                        n4 = (d = this.getDoubleArg(luaCallFrame, n3)).isInfinite() || d.isNaN() ? 1 : 0;
                        double d3 = d;
                        if (KahluaUtil.isNegative(d3)) {
                            if (n4 == 0) {
                                stringBuilder.append('-');
                            }
                            d3 = -d3;
                        } else if (bl4) {
                            stringBuilder.append('+');
                        } else if (bl5) {
                            stringBuilder.append(' ');
                        }
                        if (n4 != 0) {
                            stringBuilder.append(KahluaUtil.numberToString(d));
                            break;
                        }
                        double d4 = StringLib.roundToSignificantNumbers(d3, n7);
                        if (d4 == 0.0 || d4 >= 1.0E-4 && d4 < (double)KahluaUtil.ipow(10L, n7)) {
                            int n11;
                            if (d4 == 0.0) {
                                n11 = 1;
                            } else if (Math.floor(d4) == 0.0) {
                                n11 = 0;
                            } else {
                                double d5 = d4;
                                n11 = 1;
                                while (d5 >= 10.0) {
                                    d5 /= 10.0;
                                    ++n11;
                                }
                            }
                            this.appendSignificantNumber(stringBuilder, d4, n7 - n11, bl);
                            break;
                        }
                        this.appendScientificNumber(stringBuilder, d4, n7 - 1, bl, true);
                        break;
                    }
                    case 's': {
                        String string3 = this.getStringArg(luaCallFrame, n3);
                        n4 = string3.length();
                        if (bl6) {
                            n4 = Math.min(n7, string3.length());
                        }
                        this.append(stringBuilder, string3, 0, n4);
                        break;
                    }
                    case 'q': {
                        String string4 = this.getStringArg(luaCallFrame, n3);
                        stringBuilder.append('\"');
                        block43: for (n4 = 0; n4 < string4.length(); ++n4) {
                            n5 = string4.charAt(n4);
                            switch (n5) {
                                case 92: {
                                    stringBuilder.append("\\");
                                    continue block43;
                                }
                                case 10: {
                                    stringBuilder.append("\\\n");
                                    continue block43;
                                }
                                case 13: {
                                    stringBuilder.append("\\r");
                                    continue block43;
                                }
                                case 34: {
                                    stringBuilder.append("\\\"");
                                    continue block43;
                                }
                                default: {
                                    stringBuilder.append((char)n5);
                                }
                            }
                        }
                        stringBuilder.append('\"');
                        break;
                    }
                    default: {
                        throw new RuntimeException("Internal error");
                    }
                }
                if (bl3) {
                    var23_23 = stringBuilder.length();
                    n4 = n6 - (var23_23 - n10);
                    if (n4 > 0) {
                        this.extend(stringBuilder, n4, ' ');
                    }
                } else {
                    char c3;
                    var23_23 = stringBuilder.length();
                    n4 = var23_23 - n10 - n6;
                    if ((n4 = Math.min(n4, n6)) > 0) {
                        stringBuilder.delete(n10, n10 + n4);
                    }
                    if (bl2 && ((c3 = stringBuilder.charAt(n5 = n10 + (n6 - n4))) == '+' || c3 == '-' || c3 == ' ')) {
                        stringBuilder.setCharAt(n5, '0');
                        stringBuilder.setCharAt(n10, c3);
                    }
                }
                if (bl7) {
                    this.stringBufferUpperCase(stringBuilder, n10);
                }
                ++n3;
                continue;
            }
            stringBuilder.append(c);
        }
        luaCallFrame.push(stringBuilder.toString());
        return 1;
    }

    private void append(StringBuilder stringBuilder, String string, int n, int n2) {
        for (int i = n; i < n2; ++i) {
            stringBuilder.append(string.charAt(i));
        }
    }

    private void extend(StringBuilder stringBuilder, int n, char c) {
        int n2 = stringBuilder.length();
        stringBuilder.setLength(n2 + n);
        for (int i = n - 1; i >= 0; --i) {
            stringBuilder.setCharAt(n2 + i, c);
        }
    }

    private void stringBufferUpperCase(StringBuilder stringBuilder, int n) {
        int n2 = stringBuilder.length();
        for (int i = n; i < n2; ++i) {
            char c = stringBuilder.charAt(i);
            if (c < 'a' || c > 'z') continue;
            stringBuilder.setCharAt(i, (char)(c - 32));
        }
    }

    private static void stringBufferAppend(StringBuilder stringBuilder, double d, int n, boolean bl, int n2) {
        int n3 = stringBuilder.length();
        while (d > 0.0 || n2 > 0) {
            double d2 = Math.floor(d / (double)n);
            stringBuilder.append(digits[(int)(d - d2 * (double)n)]);
            d = d2;
            --n2;
        }
        int n4 = stringBuilder.length() - 1;
        if (n3 > n4 && bl) {
            stringBuilder.append('0');
        } else {
            int n5 = (1 + n4 - n3) / 2;
            for (int i = n5 - 1; i >= 0; --i) {
                int n6 = n3 + i;
                int n7 = n4 - i;
                char c = stringBuilder.charAt(n6);
                char c2 = stringBuilder.charAt(n7);
                stringBuilder.setCharAt(n6, c2);
                stringBuilder.setCharAt(n7, c);
            }
        }
    }

    private void appendPrecisionNumber(StringBuilder stringBuilder, double d, int n, boolean bl) {
        d = StringLib.roundToPrecision(d, n);
        double d2 = Math.floor(d);
        double d3 = d - d2;
        for (int i = 0; i < n; ++i) {
            d3 *= 10.0;
        }
        d3 = KahluaUtil.round(d2 + d3) - d2;
        StringLib.stringBufferAppend(stringBuilder, d2, 10, true, 0);
        if (bl || n > 0) {
            stringBuilder.append('.');
        }
        StringLib.stringBufferAppend(stringBuilder, d3, 10, false, n);
    }

    private void appendSignificantNumber(StringBuilder stringBuilder, double d, int n, boolean bl) {
        int n2;
        double d2 = Math.floor(d);
        StringLib.stringBufferAppend(stringBuilder, d2, 10, true, 0);
        double d3 = StringLib.roundToSignificantNumbers(d - d2, n);
        boolean bl2 = d2 == 0.0 && d3 != 0.0;
        int n3 = 0;
        int n4 = n;
        for (n2 = 0; n2 < n4; ++n2) {
            if (Math.floor(d3 *= 10.0) != 0.0 || d3 == 0.0) continue;
            ++n3;
            if (!bl2) continue;
            ++n4;
        }
        d3 = KahluaUtil.round(d3);
        if (!bl) {
            while (d3 > 0.0 && d3 % 10.0 == 0.0) {
                d3 /= 10.0;
                --n;
            }
        }
        stringBuilder.append('.');
        n2 = stringBuilder.length();
        this.extend(stringBuilder, n3, '0');
        int n5 = stringBuilder.length();
        StringLib.stringBufferAppend(stringBuilder, d3, 10, false, 0);
        int n6 = stringBuilder.length();
        int n7 = n6 - n5;
        if (bl && n7 < n) {
            int n8 = n - n7 - n3;
            this.extend(stringBuilder, n8, '0');
        }
        if (!bl && n2 == stringBuilder.length()) {
            stringBuilder.delete(n2 - 1, stringBuilder.length());
        }
    }

    private void appendScientificNumber(StringBuilder stringBuilder, double d, int n, boolean bl, boolean bl2) {
        int n2;
        int n3 = 0;
        for (n2 = 0; n2 < 2; ++n2) {
            if (d >= 1.0) {
                while (d >= 10.0) {
                    d /= 10.0;
                    ++n3;
                }
            } else {
                while (d > 0.0 && d < 1.0) {
                    d *= 10.0;
                    --n3;
                }
            }
            d = StringLib.roundToPrecision(d, n);
        }
        n2 = Math.abs(n3);
        char c = n3 >= 0 ? (char)'+' : '-';
        if (bl2) {
            this.appendSignificantNumber(stringBuilder, d, n, bl);
        } else {
            this.appendPrecisionNumber(stringBuilder, d, n, bl);
        }
        stringBuilder.append('e');
        stringBuilder.append(c);
        StringLib.stringBufferAppend(stringBuilder, n2, 10, true, 2);
    }

    private String getStringArg(LuaCallFrame luaCallFrame, int n) {
        return this.getStringArg(luaCallFrame, n, names[6]);
    }

    private String getStringArg(LuaCallFrame luaCallFrame, int n, String string) {
        return KahluaUtil.getStringArg(luaCallFrame, n, string);
    }

    private Double getDoubleArg(LuaCallFrame luaCallFrame, int n) {
        return this.getDoubleArg(luaCallFrame, n, names[6]);
    }

    private Double getDoubleArg(LuaCallFrame luaCallFrame, int n, String string) {
        return KahluaUtil.getNumberArg(luaCallFrame, n, string);
    }

    private int lower(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "not enough arguments");
        String string = this.getStringArg(luaCallFrame, 1, names[3]);
        luaCallFrame.push(string.toLowerCase());
        return 1;
    }

    private int upper(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "not enough arguments");
        String string = this.getStringArg(luaCallFrame, 1, names[4]);
        luaCallFrame.push(string.toUpperCase());
        return 1;
    }

    private int contains(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 2, "not enough arguments");
        String string = this.getStringArg(luaCallFrame, 1, names[13]);
        String string2 = this.getStringArg(luaCallFrame, 2, names[13]);
        luaCallFrame.push(string.contains(string2));
        return 1;
    }

    private int reverse(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "not enough arguments");
        String string = this.getStringArg(luaCallFrame, 1, names[5]);
        string = new StringBuilder(string).reverse().toString();
        luaCallFrame.push(string);
        return 1;
    }

    private int stringByte(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "not enough arguments");
        String string = this.getStringArg(luaCallFrame, 1, names[2]);
        int n2 = this.nullDefault(1, KahluaUtil.getOptionalNumberArg(luaCallFrame, 2));
        int n3 = this.nullDefault(n2, KahluaUtil.getOptionalNumberArg(luaCallFrame, 3));
        int n4 = string.length();
        if (n2 < 0) {
            n2 += n4 + 1;
        }
        if (n2 <= 0) {
            n2 = 1;
        }
        if (n3 < 0) {
            n3 += n4 + 1;
        } else if (n3 > n4) {
            n3 = n4;
        }
        int n5 = 1 + n3 - n2;
        if (n5 <= 0) {
            return 0;
        }
        luaCallFrame.setTop(n5);
        int n6 = n2 - 1;
        for (int i = 0; i < n5; ++i) {
            char c = string.charAt(n6 + i);
            luaCallFrame.set(i, KahluaUtil.toDouble(c));
        }
        return n5;
    }

    private int nullDefault(int n, Double d) {
        if (d == null) {
            return n;
        }
        return d.intValue();
    }

    private int stringChar(LuaCallFrame luaCallFrame, int n) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < n; ++i) {
            int n2 = this.getDoubleArg(luaCallFrame, i + 1, names[1]).intValue();
            stringBuilder.append((char)n2);
        }
        return luaCallFrame.push(stringBuilder.toString());
    }

    private int sub(LuaCallFrame luaCallFrame, int n) {
        String string = this.getStringArg(luaCallFrame, 1, names[0]);
        double d = this.getDoubleArg(luaCallFrame, 2, names[0]);
        double d2 = -1.0;
        if (n >= 3) {
            d2 = this.getDoubleArg(luaCallFrame, 3, names[0]);
        }
        int n2 = (int)d;
        int n3 = (int)d2;
        int n4 = string.length();
        if (n2 < 0) {
            n2 = Math.max(n4 + n2 + 1, 1);
        } else if (n2 == 0) {
            n2 = 1;
        }
        if (n3 < 0) {
            n3 = Math.max(0, n3 + n4 + 1);
        } else if (n3 > n4) {
            n3 = n4;
        }
        if (n2 > n3) {
            return luaCallFrame.push("");
        }
        String string2 = string.substring(n2 - 1, n3);
        return luaCallFrame.push(string2);
    }

    public static double roundToPrecision(double d, int n) {
        double d2 = KahluaUtil.ipow(10L, n);
        return KahluaUtil.round(d * d2) / d2;
    }

    public static double roundToSignificantNumbers(double d, int n) {
        if (d == 0.0) {
            return 0.0;
        }
        if (d < 0.0) {
            return -StringLib.roundToSignificantNumbers(-d, n);
        }
        double d2 = KahluaUtil.ipow(10L, n - 1);
        double d3 = d2 * 10.0;
        double d4 = 1.0;
        while (d4 * d < d2) {
            d4 *= 10.0;
        }
        while (d4 * d >= d3) {
            d4 /= 10.0;
        }
        return KahluaUtil.round(d * d4) / d4;
    }

    private static Object push_onecapture(MatchState matchState, int n, StringPointer stringPointer, StringPointer stringPointer2) {
        if (n >= matchState.level) {
            if (n == 0) {
                String string = stringPointer.string.substring(stringPointer.index, stringPointer2.index);
                matchState.callFrame.push(string);
                return string;
            }
            throw new RuntimeException("invalid capture index");
        }
        int n2 = matchState.capture[n].len;
        if (n2 == -1) {
            throw new RuntimeException("unfinished capture");
        }
        if (n2 == -2) {
            Double d = new Double(matchState.src_init.length() - matchState.capture[n].init.length() + 1);
            matchState.callFrame.push(d);
            return d;
        }
        int n3 = matchState.capture[n].init.index;
        String string = matchState.capture[n].init.string.substring(n3, n3 + n2);
        matchState.callFrame.push(string);
        return string;
    }

    private static int push_captures(MatchState matchState, StringPointer stringPointer, StringPointer stringPointer2) {
        int n = matchState.level == 0 && stringPointer != null ? 1 : matchState.level;
        KahluaUtil.luaAssert(n <= 32, "too many captures");
        for (int i = 0; i < n; ++i) {
            StringLib.push_onecapture(matchState, i, stringPointer, stringPointer2);
        }
        return n;
    }

    private static boolean noSpecialChars(String string) {
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c >= '\u0100' || !SPECIALS[c]) continue;
            return false;
        }
        return true;
    }

    private static int findAux(LuaCallFrame luaCallFrame, boolean bl) {
        int n;
        String string = bl ? names[7] : names[8];
        String string2 = KahluaUtil.getStringArg(luaCallFrame, 1, string);
        String string3 = KahluaUtil.getStringArg(luaCallFrame, 2, string);
        Double d = KahluaUtil.getOptionalNumberArg(luaCallFrame, 3);
        boolean bl2 = KahluaUtil.boolEval(KahluaUtil.getOptionalArg(luaCallFrame, 4));
        int n2 = n = d == null ? 0 : d.intValue() - 1;
        if (n < 0) {
            if ((n += string2.length()) < 0) {
                n = 0;
            }
        } else if (n > string2.length()) {
            n = string2.length();
        }
        if (bl && (bl2 || StringLib.noSpecialChars(string3))) {
            int n3 = string2.indexOf(string3, n);
            if (n3 > -1) {
                return luaCallFrame.push(KahluaUtil.toDouble(n3 + 1), KahluaUtil.toDouble(n3 + string3.length()));
            }
        } else {
            StringPointer stringPointer = new StringPointer(string2);
            StringPointer stringPointer2 = new StringPointer(string3);
            boolean bl3 = false;
            if (stringPointer2.getChar() == '^') {
                bl3 = true;
                stringPointer2.postIncrString(1);
            }
            StringPointer stringPointer3 = stringPointer.getClone();
            stringPointer3.postIncrString(n);
            MatchState matchState = new MatchState(luaCallFrame, stringPointer.getClone(), stringPointer.getStringLength());
            do {
                matchState.level = 0;
                StringPointer stringPointer4 = StringLib.match(matchState, stringPointer3, stringPointer2);
                if (stringPointer4 == null) continue;
                if (bl) {
                    return luaCallFrame.push(new Double(stringPointer.length() - stringPointer3.length() + 1), new Double(stringPointer.length() - stringPointer4.length())) + StringLib.push_captures(matchState, null, null);
                }
                return StringLib.push_captures(matchState, stringPointer3, stringPointer4);
            } while (stringPointer3.postIncrStringI(1) < matchState.endIndex && !bl3);
        }
        return luaCallFrame.pushNil();
    }

    private static StringPointer startCapture(MatchState matchState, StringPointer stringPointer, StringPointer stringPointer2, int n) {
        int n2 = matchState.level;
        KahluaUtil.luaAssert(n2 < 32, "too many captures");
        matchState.capture[n2].init = stringPointer.getClone();
        matchState.capture[n2].init.setIndex(stringPointer.getIndex());
        matchState.capture[n2].len = n;
        matchState.level = n2 + 1;
        StringPointer stringPointer3 = StringLib.match(matchState, stringPointer, stringPointer2);
        if (stringPointer3 == null) {
            --matchState.level;
        }
        return stringPointer3;
    }

    private static int captureToClose(MatchState matchState) {
        int n = matchState.level;
        --n;
        while (n >= 0) {
            if (matchState.capture[n].len == -1) {
                return n;
            }
            --n;
        }
        throw new RuntimeException("invalid pattern capture");
    }

    private static StringPointer endCapture(MatchState matchState, StringPointer stringPointer, StringPointer stringPointer2) {
        int n = StringLib.captureToClose(matchState);
        matchState.capture[n].len = matchState.capture[n].init.length() - stringPointer.length();
        StringPointer stringPointer3 = StringLib.match(matchState, stringPointer, stringPointer2);
        if (stringPointer3 == null) {
            matchState.capture[n].len = -1;
        }
        return stringPointer3;
    }

    private static int checkCapture(MatchState matchState, int n) {
        KahluaUtil.luaAssert((n -= 49) < 0 || n >= matchState.level || matchState.capture[n].len == -1, "invalid capture index");
        return n;
    }

    private static StringPointer matchCapture(MatchState matchState, StringPointer stringPointer, int n) {
        n = StringLib.checkCapture(matchState, n);
        int n2 = matchState.capture[n].len;
        if (matchState.endIndex - stringPointer.length() >= n2 && matchState.capture[n].init.compareTo(stringPointer, n2) == 0) {
            StringPointer stringPointer2 = stringPointer.getClone();
            stringPointer2.postIncrString(n2);
            return stringPointer2;
        }
        return null;
    }

    private static StringPointer matchBalance(MatchState matchState, StringPointer stringPointer, StringPointer stringPointer2) {
        KahluaUtil.luaAssert(stringPointer2.getChar() != '\u0000' && stringPointer2.getChar(1) != '\u0000', "unbalanced pattern");
        StringPointer stringPointer3 = stringPointer.getClone();
        if (stringPointer3.getChar() != stringPointer2.getChar()) {
            return null;
        }
        char c = stringPointer2.getChar();
        char c2 = stringPointer2.getChar(1);
        int n = 1;
        while (stringPointer3.preIncrStringI(1) < matchState.endIndex) {
            if (stringPointer3.getChar() == c2) {
                if (--n != 0) continue;
                StringPointer stringPointer4 = stringPointer3.getClone();
                stringPointer4.postIncrString(1);
                return stringPointer4;
            }
            if (stringPointer3.getChar() != c) continue;
            ++n;
        }
        return null;
    }

    private static StringPointer classEnd(StringPointer stringPointer) {
        StringPointer stringPointer2 = stringPointer.getClone();
        switch (stringPointer2.postIncrString(1)) {
            case '%': {
                KahluaUtil.luaAssert(stringPointer2.getChar() != '\u0000', "malformed pattern (ends with '%')");
                stringPointer2.postIncrString(1);
                return stringPointer2;
            }
            case '[': {
                if (stringPointer2.getChar() == '^') {
                    stringPointer2.postIncrString(1);
                }
                do {
                    KahluaUtil.luaAssert(stringPointer2.getChar() != '\u0000', "malformed pattern (missing ']')");
                    if (stringPointer2.postIncrString(1) != '%' || stringPointer2.getChar() == '\u0000') continue;
                    stringPointer2.postIncrString(1);
                } while (stringPointer2.getChar() != ']');
                stringPointer2.postIncrString(1);
                return stringPointer2;
            }
        }
        return stringPointer2;
    }

    private static boolean singleMatch(char c, StringPointer stringPointer, StringPointer stringPointer2) {
        switch (stringPointer.getChar()) {
            case '.': {
                return true;
            }
            case '%': {
                return StringLib.matchClass(stringPointer.getChar(1), c);
            }
            case '[': {
                StringPointer stringPointer3 = stringPointer2.getClone();
                stringPointer3.postIncrString(-1);
                return StringLib.matchBracketClass(c, stringPointer, stringPointer3);
            }
        }
        return stringPointer.getChar() == c;
    }

    private static StringPointer minExpand(MatchState matchState, StringPointer stringPointer, StringPointer stringPointer2, StringPointer stringPointer3) {
        StringPointer stringPointer4 = stringPointer3.getClone();
        StringPointer stringPointer5 = stringPointer.getClone();
        stringPointer4.postIncrString(1);
        while (true) {
            StringPointer stringPointer6;
            if ((stringPointer6 = StringLib.match(matchState, stringPointer5, stringPointer4)) != null) {
                return stringPointer6;
            }
            if (stringPointer5.getIndex() >= matchState.endIndex || !StringLib.singleMatch(stringPointer5.getChar(), stringPointer2, stringPointer3)) break;
            stringPointer5.postIncrString(1);
        }
        return null;
    }

    private static StringPointer maxExpand(MatchState matchState, StringPointer stringPointer, StringPointer stringPointer2, StringPointer stringPointer3) {
        int n = 0;
        while (stringPointer.getIndex() + n < matchState.endIndex && StringLib.singleMatch(stringPointer.getChar(n), stringPointer2, stringPointer3)) {
            ++n;
        }
        while (n >= 0) {
            StringPointer stringPointer4 = stringPointer.getClone();
            stringPointer4.postIncrString(n);
            StringPointer stringPointer5 = stringPointer3.getClone();
            stringPointer5.postIncrString(1);
            StringPointer stringPointer6 = StringLib.match(matchState, stringPointer4, stringPointer5);
            if (stringPointer6 != null) {
                return stringPointer6;
            }
            --n;
        }
        return null;
    }

    private static boolean matchBracketClass(char c, StringPointer stringPointer, StringPointer stringPointer2) {
        StringPointer stringPointer3 = stringPointer.getClone();
        StringPointer stringPointer4 = stringPointer2.getClone();
        boolean bl = true;
        if (stringPointer3.getChar(1) == '^') {
            bl = false;
            stringPointer3.postIncrString(1);
        }
        while (stringPointer3.preIncrStringI(1) < stringPointer4.getIndex()) {
            if (stringPointer3.getChar() == '%') {
                stringPointer3.postIncrString(1);
                if (!StringLib.matchClass(stringPointer3.getChar(), c)) continue;
                return bl;
            }
            if (stringPointer3.getChar(1) == '-' && stringPointer3.getIndex() + 2 < stringPointer4.getIndex()) {
                stringPointer3.postIncrString(2);
                if (stringPointer3.getChar(-2) > c || c > stringPointer3.getChar()) continue;
                return bl;
            }
            if (stringPointer3.getChar() != c) continue;
            return bl;
        }
        return !bl;
    }

    private static StringPointer match(MatchState matchState, StringPointer stringPointer, StringPointer stringPointer2) {
        StringPointer stringPointer3 = stringPointer.getClone();
        StringPointer stringPointer4 = stringPointer2.getClone();
        boolean bl = true;
        boolean bl2 = false;
        block17: while (bl) {
            StringPointer stringPointer5;
            char c;
            StringPointer stringPointer6;
            bl = false;
            bl2 = false;
            switch (stringPointer4.getChar()) {
                case '(': {
                    stringPointer6 = stringPointer4.getClone();
                    if (stringPointer4.getChar(1) == ')') {
                        stringPointer6.postIncrString(2);
                        return StringLib.startCapture(matchState, stringPointer3, stringPointer6, -2);
                    }
                    stringPointer6.postIncrString(1);
                    return StringLib.startCapture(matchState, stringPointer3, stringPointer6, -1);
                }
                case ')': {
                    stringPointer6 = stringPointer4.getClone();
                    stringPointer6.postIncrString(1);
                    return StringLib.endCapture(matchState, stringPointer3, stringPointer6);
                }
                case '%': {
                    switch (stringPointer4.getChar(1)) {
                        case 'b': {
                            stringPointer6 = stringPointer4.getClone();
                            stringPointer6.postIncrString(2);
                            stringPointer3 = StringLib.matchBalance(matchState, stringPointer3, stringPointer6);
                            if (stringPointer3 == null) {
                                return null;
                            }
                            stringPointer4.postIncrString(4);
                            bl = true;
                            continue block17;
                        }
                        case 'f': {
                            stringPointer4.postIncrString(2);
                            KahluaUtil.luaAssert(stringPointer4.getChar() == '[', "missing '[' after '%%f' in pattern");
                            stringPointer6 = StringLib.classEnd(stringPointer4);
                            c = stringPointer3.getIndex() == matchState.src_init.getIndex() ? (char)'\u0000' : stringPointer3.getChar(-1);
                            stringPointer5 = stringPointer6.getClone();
                            stringPointer5.postIncrString(-1);
                            if (StringLib.matchBracketClass(c, stringPointer4, stringPointer5) || !StringLib.matchBracketClass(stringPointer3.getChar(), stringPointer4, stringPointer5)) {
                                return null;
                            }
                            stringPointer4 = stringPointer6;
                            bl = true;
                            continue block17;
                        }
                    }
                    if (Character.isDigit(stringPointer4.getChar(1))) {
                        if ((stringPointer3 = StringLib.matchCapture(matchState, stringPointer3, stringPointer4.getChar(1))) == null) {
                            return null;
                        }
                        stringPointer4.postIncrString(2);
                        bl = true;
                        continue block17;
                    }
                    bl2 = true;
                    break;
                }
                case '\u0000': {
                    return stringPointer3;
                }
                case '$': {
                    if (stringPointer4.getChar(1) == '\u0000') {
                        return stringPointer3.getIndex() == matchState.endIndex ? stringPointer3 : null;
                    }
                }
                default: {
                    bl2 = true;
                }
            }
            if (!bl2) continue;
            stringPointer6 = StringLib.classEnd(stringPointer4);
            c = stringPointer3.getIndex() < matchState.endIndex && StringLib.singleMatch(stringPointer3.getChar(), stringPointer4, stringPointer6) ? (char)'\u0001' : '\u0000';
            switch (stringPointer6.getChar()) {
                case '?': {
                    StringPointer stringPointer7 = stringPointer3.getClone();
                    stringPointer7.postIncrString(1);
                    StringPointer stringPointer8 = stringPointer6.getClone();
                    stringPointer8.postIncrString(1);
                    if (c != '\u0000' && (stringPointer5 = StringLib.match(matchState, stringPointer7, stringPointer8)) != null) {
                        return stringPointer5;
                    }
                    stringPointer4 = stringPointer6;
                    stringPointer4.postIncrString(1);
                    bl = true;
                    continue block17;
                }
                case '*': {
                    return StringLib.maxExpand(matchState, stringPointer3, stringPointer4, stringPointer6);
                }
                case '+': {
                    stringPointer5 = stringPointer3.getClone();
                    stringPointer5.postIncrString(1);
                    return c != '\u0000' ? StringLib.maxExpand(matchState, stringPointer5, stringPointer4, stringPointer6) : null;
                }
                case '-': {
                    return StringLib.minExpand(matchState, stringPointer3, stringPointer4, stringPointer6);
                }
            }
            if (c == '\u0000') {
                return null;
            }
            stringPointer3.postIncrString(1);
            stringPointer4 = stringPointer6;
            bl = true;
        }
        return null;
    }

    private static boolean matchClass(char c, char c2) {
        boolean bl;
        char c3 = Character.toLowerCase(c);
        switch (c3) {
            case 'a': {
                bl = Character.isLowerCase(c2) || Character.isUpperCase(c2);
                break;
            }
            case 'c': {
                bl = StringLib.isControl(c2);
                break;
            }
            case 'd': {
                bl = Character.isDigit(c2);
                break;
            }
            case 'l': {
                bl = Character.isLowerCase(c2);
                break;
            }
            case 'p': {
                bl = StringLib.isPunct(c2);
                break;
            }
            case 's': {
                bl = StringLib.isSpace(c2);
                break;
            }
            case 'u': {
                bl = Character.isUpperCase(c2);
                break;
            }
            case 'w': {
                bl = Character.isLowerCase(c2) || Character.isUpperCase(c2) || Character.isDigit(c2);
                break;
            }
            case 'x': {
                bl = StringLib.isHex(c2);
                break;
            }
            case 'z': {
                bl = c2 == '\u0000';
                break;
            }
            default: {
                return c == c2;
            }
        }
        return c3 == c == bl;
    }

    private static boolean isPunct(char c) {
        return c >= '!' && c <= '/' || c >= ':' && c <= '@' || c >= '[' && c <= '`' || c >= '{' && c <= '~';
    }

    private static boolean isSpace(char c) {
        return c >= '\t' && c <= '\r' || c == ' ';
    }

    private static boolean isControl(char c) {
        return c >= '\u0000' && c <= '\u001f' || c == '\u007f';
    }

    private static boolean isHex(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F';
    }

    private static int gsub(LuaCallFrame luaCallFrame, int n) {
        String string = KahluaUtil.getStringArg(luaCallFrame, 1, names[9]);
        String string2 = KahluaUtil.getStringArg(luaCallFrame, 2, names[9]);
        Object object = KahluaUtil.getArg(luaCallFrame, 3, names[9]);
        Object object2 = KahluaUtil.rawTostring(object);
        if (object2 != null) {
            object = object2;
        }
        int n2 = (object2 = KahluaUtil.getOptionalNumberArg(luaCallFrame, 4)) == null ? Integer.MAX_VALUE : ((Double)object2).intValue();
        StringPointer stringPointer = new StringPointer(string2);
        StringPointer stringPointer2 = new StringPointer(string);
        boolean bl = false;
        if (stringPointer.getChar() == '^') {
            bl = true;
            stringPointer.postIncrString(1);
        }
        if (!(object instanceof Double || object instanceof String || object instanceof LuaClosure || object instanceof JavaFunction || object instanceof KahluaTable)) {
            KahluaUtil.fail("string/function/table expected, got " + object);
        }
        MatchState matchState = new MatchState(luaCallFrame, stringPointer2.getClone(), stringPointer2.length());
        int n3 = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while (n3 < n2) {
            matchState.level = 0;
            StringPointer stringPointer3 = StringLib.match(matchState, stringPointer2, stringPointer);
            if (stringPointer3 != null) {
                ++n3;
                StringLib.addValue(matchState, object, stringBuilder, stringPointer2, stringPointer3);
            }
            if (stringPointer3 != null && stringPointer3.getIndex() > stringPointer2.getIndex()) {
                stringPointer2.setIndex(stringPointer3.getIndex());
            } else {
                if (stringPointer2.getIndex() >= matchState.endIndex) break;
                stringBuilder.append(stringPointer2.postIncrString(1));
            }
            if (!bl) continue;
            break;
        }
        return luaCallFrame.push(stringBuilder.append(stringPointer2.getString()).toString(), new Double(n3));
    }

    private static int trim(LuaCallFrame luaCallFrame, int n) {
        String string = KahluaUtil.getStringArg(luaCallFrame, 1, names[10]);
        return luaCallFrame.push(string.trim());
    }

    private static int split(LuaCallFrame luaCallFrame, int n) {
        String string = KahluaUtil.getStringArg(luaCallFrame, 1, names[11]);
        String string2 = KahluaUtil.getStringArg(luaCallFrame, 2, names[11]);
        String[] stringArray = string.split(string2);
        KahluaTable kahluaTable = LuaManager.platform.newTable();
        for (int i = 0; i < stringArray.length; ++i) {
            kahluaTable.rawset(i + 1, (Object)stringArray[i]);
        }
        return luaCallFrame.push(kahluaTable);
    }

    private static int sort(LuaCallFrame luaCallFrame, int n) {
        String string;
        String string2 = KahluaUtil.getStringArg(luaCallFrame, 1, names[12]);
        return luaCallFrame.push(string2.compareTo(string = KahluaUtil.getStringArg(luaCallFrame, 2, names[12])) > 0);
    }

    private static void addValue(MatchState matchState, Object object, StringBuilder stringBuilder, StringPointer stringPointer, StringPointer stringPointer2) {
        String string = KahluaUtil.rawTostring(object);
        if (string != null) {
            stringBuilder.append(StringLib.addString(matchState, string, stringPointer, stringPointer2));
        } else {
            Object object2 = matchState.getCapture(0);
            String string2 = object2 != null ? KahluaUtil.rawTostring(object2) : stringPointer.getStringSubString(stringPointer2.getIndex() - stringPointer.getIndex());
            Object object3 = null;
            object3 = object instanceof KahluaTable ? ((KahluaTable)object).rawget(string2) : matchState.callFrame.getThread().call(object, string2, null, null);
            if (object3 == null) {
                object3 = string2;
            }
            stringBuilder.append(KahluaUtil.rawTostring(object3));
        }
    }

    private static String addString(MatchState matchState, String string, StringPointer stringPointer, StringPointer stringPointer2) {
        StringPointer stringPointer3 = new StringPointer(string);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < string.length(); ++i) {
            char c = stringPointer3.getChar(i);
            if (c != '%') {
                stringBuilder.append(c);
                continue;
            }
            if (!Character.isDigit(c = stringPointer3.getChar(++i))) {
                stringBuilder.append(c);
                continue;
            }
            if (c == '0') {
                int n = stringPointer.getStringLength() - stringPointer2.length();
                stringBuilder.append(stringPointer.getStringSubString(n));
                continue;
            }
            Object object = matchState.getCapture(c - 49);
            if (object == null) {
                throw new KahluaException((Object)"invalid capture index");
            }
            stringBuilder.append(KahluaUtil.tostring(object, null));
        }
        return stringBuilder.toString();
    }

    static {
        String string = "^$*+?.([%-";
        for (int i = 0; i < string.length(); ++i) {
            StringLib.SPECIALS[string.charAt((int)i)] = true;
        }
        STRING_CLASS = "".getClass();
        names = new String[14];
        StringLib.names[0] = "sub";
        StringLib.names[1] = "char";
        StringLib.names[2] = "byte";
        StringLib.names[3] = "lower";
        StringLib.names[4] = "upper";
        StringLib.names[5] = "reverse";
        StringLib.names[6] = "format";
        StringLib.names[7] = "find";
        StringLib.names[8] = "match";
        StringLib.names[9] = "gsub";
        StringLib.names[10] = "trim";
        StringLib.names[11] = "split";
        StringLib.names[12] = "sort";
        StringLib.names[13] = "contains";
        functions = new StringLib[14];
        for (int i = 0; i < 14; ++i) {
            StringLib.functions[i] = new StringLib(i);
        }
        digits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    }

    public static class MatchState {
        public final LuaCallFrame callFrame;
        public final StringPointer src_init;
        public final int endIndex;
        public final Capture[] capture;
        public int level;

        public MatchState(LuaCallFrame luaCallFrame, StringPointer stringPointer, int n) {
            this.callFrame = luaCallFrame;
            this.src_init = stringPointer;
            this.endIndex = n;
            this.capture = new Capture[32];
            for (int i = 0; i < 32; ++i) {
                this.capture[i] = new Capture();
            }
        }

        public Object getCapture(int n) {
            if (n >= this.level) {
                return null;
            }
            if (this.capture[n].len == -2) {
                return new Double(this.src_init.length() - this.capture[n].init.length() + 1);
            }
            return this.capture[n].init.getStringSubString(this.capture[n].len);
        }

        public static class Capture {
            public StringPointer init;
            public int len;
        }
    }

    public static class StringPointer {
        private final String string;
        private int index = 0;

        public StringPointer(String string) {
            this.string = string;
        }

        public StringPointer(String string, int n) {
            this.string = string;
            this.index = n;
        }

        public StringPointer getClone() {
            return new StringPointer(this.string, this.index);
        }

        public int getIndex() {
            return this.index;
        }

        public void setIndex(int n) {
            this.index = n;
        }

        public String getString() {
            if (this.index == 0) {
                return this.string;
            }
            return this.string.substring(this.index);
        }

        public int getStringLength() {
            return this.getStringLength(0);
        }

        public int getStringLength(int n) {
            return this.string.length() - (this.index + n);
        }

        public String getStringSubString(int n) {
            return this.string.substring(this.index, this.index + n);
        }

        public char getChar() {
            return this.getChar(0);
        }

        public char getChar(int n) {
            int n2 = this.index + n;
            if (n2 >= this.string.length()) {
                return '\u0000';
            }
            return this.string.charAt(n2);
        }

        public int length() {
            return this.string.length() - this.index;
        }

        public int postIncrStringI(int n) {
            int n2 = this.index;
            this.index += n;
            return n2;
        }

        public int preIncrStringI(int n) {
            this.index += n;
            return this.index;
        }

        public char postIncrString(int n) {
            char c = this.getChar();
            this.index += n;
            return c;
        }

        public int compareTo(StringPointer stringPointer, int n) {
            for (int i = 0; i < n; ++i) {
                int n2 = this.getChar(i) - stringPointer.getChar(i);
                if (n2 == 0) continue;
                return n2;
            }
            return 0;
        }
    }
}

