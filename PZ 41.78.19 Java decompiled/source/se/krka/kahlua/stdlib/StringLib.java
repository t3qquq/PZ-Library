// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.stdlib;

import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaException;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;
import zombie.Lua.LuaManager;

public final class StringLib implements JavaFunction {
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

    public StringLib(int int0) {
        this.methodId = int0;
    }

    public static void register(Platform platform, KahluaTable table2) {
        KahluaTable table0 = platform.newTable();

        for (int int0 = 0; int0 < 14; int0++) {
            table0.rawset(names[int0], functions[int0]);
        }

        table0.rawset("__index", table0);
        KahluaTable table1 = KahluaUtil.getClassMetatables(platform, table2);
        table1.rawset(STRING_CLASS, table0);
        table2.rawset("string", table0);
    }

    @Override
    public String toString() {
        return names[this.methodId];
    }

    @Override
    public int call(LuaCallFrame luaCallFrame, int int0) {
        switch (this.methodId) {
            case 0:
                return this.sub(luaCallFrame, int0);
            case 1:
                return this.stringChar(luaCallFrame, int0);
            case 2:
                return this.stringByte(luaCallFrame, int0);
            case 3:
                return this.lower(luaCallFrame, int0);
            case 4:
                return this.upper(luaCallFrame, int0);
            case 5:
                return this.reverse(luaCallFrame, int0);
            case 6:
                return this.format(luaCallFrame, int0);
            case 7:
                return findAux(luaCallFrame, true);
            case 8:
                return findAux(luaCallFrame, false);
            case 9:
                return gsub(luaCallFrame, int0);
            case 10:
                return trim(luaCallFrame, int0);
            case 11:
                return split(luaCallFrame, int0);
            case 12:
                return sort(luaCallFrame, int0);
            case 13:
                return this.contains(luaCallFrame, int0);
            default:
                return 0;
        }
    }

    private long unsigned(long long0) {
        if (long0 < 0L) {
            long0 += 4294967296L;
        }

        return long0;
    }

    private int format(LuaCallFrame luaCallFrame, int var2) {
        String string0 = KahluaUtil.getStringArg(luaCallFrame, 1, names[6]);
        int int0 = string0.length();
        int int1 = 2;
        StringBuilder stringBuilder = new StringBuilder();

        label323:
        for (int int2 = 0; int2 < int0; int2++) {
            char char0 = string0.charAt(int2);
            if (char0 == '%') {
                int2++;
                KahluaUtil.luaAssert(int2 < int0, "incomplete option to 'format'");
                char0 = string0.charAt(int2);
                if (char0 == '%') {
                    stringBuilder.append('%');
                } else {
                    boolean boolean0 = false;
                    boolean boolean1 = false;
                    boolean boolean2 = false;
                    boolean boolean3 = false;
                    boolean boolean4 = false;

                    while (true) {
                        switch (char0) {
                            case ' ':
                                boolean4 = true;
                                break;
                            case '#':
                                boolean0 = true;
                                break;
                            case '+':
                                boolean3 = true;
                                break;
                            case '-':
                                boolean2 = true;
                                break;
                            case '0':
                                boolean1 = true;
                                break;
                            default:
                                int int3;
                                for (int3 = 0; char0 >= '0' && char0 <= '9'; char0 = string0.charAt(int2)) {
                                    int3 = 10 * int3 + char0 - 48;
                                    int2++;
                                    KahluaUtil.luaAssert(int2 < int0, "incomplete option to 'format'");
                                }

                                int int4 = 0;
                                boolean boolean5 = false;
                                if (char0 == '.') {
                                    boolean5 = true;
                                    int2++;
                                    KahluaUtil.luaAssert(int2 < int0, "incomplete option to 'format'");

                                    for (char0 = string0.charAt(int2); char0 >= '0' && char0 <= '9'; char0 = string0.charAt(int2)) {
                                        int4 = 10 * int4 + char0 - 48;
                                        int2++;
                                        KahluaUtil.luaAssert(int2 < int0, "incomplete option to 'format'");
                                    }
                                }

                                if (boolean2) {
                                    boolean1 = false;
                                }

                                byte byte0 = 10;
                                boolean boolean6 = false;
                                byte byte1 = 6;
                                String string1 = "";
                                switch (char0) {
                                    case 'E':
                                        boolean6 = true;
                                        break;
                                    case 'F':
                                    case 'H':
                                    case 'I':
                                    case 'J':
                                    case 'K':
                                    case 'L':
                                    case 'M':
                                    case 'N':
                                    case 'O':
                                    case 'P':
                                    case 'Q':
                                    case 'R':
                                    case 'S':
                                    case 'T':
                                    case 'U':
                                    case 'V':
                                    case 'W':
                                    case 'Y':
                                    case 'Z':
                                    case '[':
                                    case '\\':
                                    case ']':
                                    case '^':
                                    case '_':
                                    case '`':
                                    case 'a':
                                    case 'b':
                                    case 'h':
                                    case 'j':
                                    case 'k':
                                    case 'l':
                                    case 'm':
                                    case 'n':
                                    case 'p':
                                    case 'r':
                                    case 't':
                                    case 'v':
                                    case 'w':
                                    default:
                                        throw new RuntimeException("invalid option '%" + char0 + "' to 'format'");
                                    case 'G':
                                        boolean6 = true;
                                        break;
                                    case 'X':
                                        byte0 = 16;
                                        byte1 = 1;
                                        boolean6 = true;
                                        string1 = "0X";
                                        break;
                                    case 'c':
                                        boolean1 = false;
                                        break;
                                    case 'd':
                                    case 'i':
                                        byte1 = 1;
                                    case 'e':
                                    case 'f':
                                    case 'g':
                                        break;
                                    case 'o':
                                        byte0 = 8;
                                        byte1 = 1;
                                        string1 = "0";
                                        break;
                                    case 'q':
                                        int3 = 0;
                                        break;
                                    case 's':
                                        boolean1 = false;
                                        break;
                                    case 'u':
                                        byte1 = 1;
                                        break;
                                    case 'x':
                                        byte0 = 16;
                                        byte1 = 1;
                                        string1 = "0x";
                                }

                                if (!boolean5) {
                                    int4 = byte1;
                                }

                                if (boolean5 && byte0 != 10) {
                                    boolean1 = false;
                                }

                                int int5 = boolean1 ? 48 : 32;
                                int int6 = stringBuilder.length();
                                if (!boolean2) {
                                    this.extend(stringBuilder, int3, (char)int5);
                                }

                                switch (char0) {
                                    case 'E':
                                    case 'e':
                                    case 'f':
                                        Double double5 = this.getDoubleArg(luaCallFrame, int1);
                                        boolean boolean8 = double5.isInfinite() || double5.isNaN();
                                        double double6 = double5;
                                        if (KahluaUtil.isNegative(double6)) {
                                            if (!boolean8) {
                                                stringBuilder.append('-');
                                            }

                                            double6 = -double6;
                                        } else if (boolean3) {
                                            stringBuilder.append('+');
                                        } else if (boolean4) {
                                            stringBuilder.append(' ');
                                        }

                                        if (boolean8) {
                                            stringBuilder.append(KahluaUtil.numberToString(double5));
                                        } else if (char0 == 'f') {
                                            this.appendPrecisionNumber(stringBuilder, double6, int4, boolean0);
                                        } else {
                                            this.appendScientificNumber(stringBuilder, double6, int4, boolean0, false);
                                        }
                                        break;
                                    case 'F':
                                    case 'H':
                                    case 'I':
                                    case 'J':
                                    case 'K':
                                    case 'L':
                                    case 'M':
                                    case 'N':
                                    case 'O':
                                    case 'P':
                                    case 'Q':
                                    case 'R':
                                    case 'S':
                                    case 'T':
                                    case 'U':
                                    case 'V':
                                    case 'W':
                                    case 'Y':
                                    case 'Z':
                                    case '[':
                                    case '\\':
                                    case ']':
                                    case '^':
                                    case '_':
                                    case '`':
                                    case 'a':
                                    case 'b':
                                    case 'h':
                                    case 'j':
                                    case 'k':
                                    case 'l':
                                    case 'm':
                                    case 'n':
                                    case 'p':
                                    case 'r':
                                    case 't':
                                    case 'v':
                                    case 'w':
                                    default:
                                        throw new RuntimeException("Internal error");
                                    case 'G':
                                    case 'g':
                                        if (int4 <= 0) {
                                            int4 = 1;
                                        }

                                        Double double1 = this.getDoubleArg(luaCallFrame, int1);
                                        boolean boolean7 = double1.isInfinite() || double1.isNaN();
                                        double double2 = double1;
                                        if (KahluaUtil.isNegative(double2)) {
                                            if (!boolean7) {
                                                stringBuilder.append('-');
                                            }

                                            double2 = -double2;
                                        } else if (boolean3) {
                                            stringBuilder.append('+');
                                        } else if (boolean4) {
                                            stringBuilder.append(' ');
                                        }

                                        if (boolean7) {
                                            stringBuilder.append(KahluaUtil.numberToString(double1));
                                        } else {
                                            double double3 = roundToSignificantNumbers(double2, int4);
                                            if (double3 == 0.0 || double3 >= 1.0E-4 && double3 < KahluaUtil.ipow(10L, int4)) {
                                                int int10;
                                                if (double3 == 0.0) {
                                                    int10 = 1;
                                                } else if (Math.floor(double3) == 0.0) {
                                                    int10 = 0;
                                                } else {
                                                    double double4 = double3;

                                                    for (int10 = 1; double4 >= 10.0; int10++) {
                                                        double4 /= 10.0;
                                                    }
                                                }

                                                this.appendSignificantNumber(stringBuilder, double3, int4 - int10, boolean0);
                                                break;
                                            }

                                            this.appendScientificNumber(stringBuilder, double3, int4 - 1, boolean0, true);
                                        }
                                        break;
                                    case 'X':
                                    case 'o':
                                    case 'u':
                                    case 'x':
                                        long long1 = this.getDoubleArg(luaCallFrame, int1).longValue();
                                        long1 = this.unsigned(long1);
                                        if (boolean0) {
                                            if (byte0 == 8) {
                                                int int9 = 0;

                                                for (long long2 = long1; long2 > 0L; int9++) {
                                                    long2 /= 8L;
                                                }

                                                if (int4 <= int9) {
                                                    stringBuilder.append(string1);
                                                }
                                            } else if (byte0 == 16 && long1 != 0L) {
                                                stringBuilder.append(string1);
                                            }
                                        }

                                        if (long1 != 0L || int4 > 0) {
                                            stringBufferAppend(stringBuilder, long1, byte0, false, int4);
                                        }
                                        break;
                                    case 'c':
                                        stringBuilder.append((char)this.getDoubleArg(luaCallFrame, int1).shortValue());
                                        break;
                                    case 'd':
                                    case 'i':
                                        Double double0 = this.getDoubleArg(luaCallFrame, int1);
                                        long long0 = double0.longValue();
                                        if (long0 < 0L) {
                                            stringBuilder.append('-');
                                            long0 = -long0;
                                        } else if (boolean3) {
                                            stringBuilder.append('+');
                                        } else if (boolean4) {
                                            stringBuilder.append(' ');
                                        }

                                        if (long0 != 0L || int4 > 0) {
                                            stringBufferAppend(stringBuilder, long0, byte0, false, int4);
                                        }
                                        break;
                                    case 'q':
                                        String string3 = this.getStringArg(luaCallFrame, int1);
                                        stringBuilder.append('"');

                                        for (int int8 = 0; int8 < string3.length(); int8++) {
                                            char char1 = string3.charAt(int8);
                                            switch (char1) {
                                                case '\n':
                                                    stringBuilder.append("\\\n");
                                                    break;
                                                case '\r':
                                                    stringBuilder.append("\\r");
                                                    break;
                                                case '"':
                                                    stringBuilder.append("\\\"");
                                                    break;
                                                case '\\':
                                                    stringBuilder.append("\\");
                                                    break;
                                                default:
                                                    stringBuilder.append(char1);
                                            }
                                        }

                                        stringBuilder.append('"');
                                        break;
                                    case 's':
                                        String string2 = this.getStringArg(luaCallFrame, int1);
                                        int int7 = string2.length();
                                        if (boolean5) {
                                            int7 = Math.min(int4, string2.length());
                                        }

                                        this.append(stringBuilder, string2, 0, int7);
                                }

                                if (boolean2) {
                                    int int11 = stringBuilder.length();
                                    int int12 = int3 - (int11 - int6);
                                    if (int12 > 0) {
                                        this.extend(stringBuilder, int12, ' ');
                                    }
                                } else {
                                    int int13 = stringBuilder.length();
                                    int int14 = int13 - int6 - int3;
                                    int14 = Math.min(int14, int3);
                                    if (int14 > 0) {
                                        stringBuilder.delete(int6, int6 + int14);
                                    }

                                    if (boolean1) {
                                        int int15 = int6 + (int3 - int14);
                                        char char2 = stringBuilder.charAt(int15);
                                        if (char2 == '+' || char2 == '-' || char2 == ' ') {
                                            stringBuilder.setCharAt(int15, '0');
                                            stringBuilder.setCharAt(int6, char2);
                                        }
                                    }
                                }

                                if (boolean6) {
                                    this.stringBufferUpperCase(stringBuilder, int6);
                                }

                                int1++;
                                continue label323;
                        }

                        int2++;
                        KahluaUtil.luaAssert(int2 < int0, "incomplete option to 'format'");
                        char0 = string0.charAt(int2);
                    }
                }
            } else {
                stringBuilder.append(char0);
            }
        }

        luaCallFrame.push(stringBuilder.toString());
        return 1;
    }

    private void append(StringBuilder stringBuilder, String string, int int1, int int2) {
        for (int int0 = int1; int0 < int2; int0++) {
            stringBuilder.append(string.charAt(int0));
        }
    }

    private void extend(StringBuilder stringBuilder, int int1, char char0) {
        int int0 = stringBuilder.length();
        stringBuilder.setLength(int0 + int1);

        for (int int2 = int1 - 1; int2 >= 0; int2--) {
            stringBuilder.setCharAt(int0 + int2, char0);
        }
    }

    private void stringBufferUpperCase(StringBuilder stringBuilder, int int2) {
        int int0 = stringBuilder.length();

        for (int int1 = int2; int1 < int0; int1++) {
            char char0 = stringBuilder.charAt(int1);
            if (char0 >= 'a' && char0 <= 'z') {
                stringBuilder.setCharAt(int1, (char)(char0 - ' '));
            }
        }
    }

    private static void stringBufferAppend(StringBuilder stringBuilder, double double0, int int2, boolean boolean0, int int1) {
        int int0 = stringBuilder.length();

        while (double0 > 0.0 || int1 > 0) {
            double double1 = Math.floor(double0 / int2);
            stringBuilder.append(digits[(int)(double0 - double1 * int2)]);
            double0 = double1;
            int1--;
        }

        int int3 = stringBuilder.length() - 1;
        if (int0 > int3 && boolean0) {
            stringBuilder.append('0');
        } else {
            int int4 = (1 + int3 - int0) / 2;

            for (int int5 = int4 - 1; int5 >= 0; int5--) {
                int int6 = int0 + int5;
                int int7 = int3 - int5;
                char char0 = stringBuilder.charAt(int6);
                char char1 = stringBuilder.charAt(int7);
                stringBuilder.setCharAt(int6, char1);
                stringBuilder.setCharAt(int7, char0);
            }
        }
    }

    private void appendPrecisionNumber(StringBuilder stringBuilder, double double0, int int0, boolean boolean0) {
        double0 = roundToPrecision(double0, int0);
        double double1 = Math.floor(double0);
        double double2 = double0 - double1;

        for (int int1 = 0; int1 < int0; int1++) {
            double2 *= 10.0;
        }

        double2 = KahluaUtil.round(double1 + double2) - double1;
        stringBufferAppend(stringBuilder, double1, 10, true, 0);
        if (boolean0 || int0 > 0) {
            stringBuilder.append('.');
        }

        stringBufferAppend(stringBuilder, double2, 10, false, int0);
    }

    private void appendSignificantNumber(StringBuilder stringBuilder, double double1, int int0, boolean boolean1) {
        double double0 = Math.floor(double1);
        stringBufferAppend(stringBuilder, double0, 10, true, 0);
        double double2 = roundToSignificantNumbers(double1 - double0, int0);
        boolean boolean0 = double0 == 0.0 && double2 != 0.0;
        int int1 = 0;
        int int2 = int0;

        for (int int3 = 0; int3 < int2; int3++) {
            double2 *= 10.0;
            if (Math.floor(double2) == 0.0 && double2 != 0.0) {
                int1++;
                if (boolean0) {
                    int2++;
                }
            }
        }

        double2 = KahluaUtil.round(double2);
        if (!boolean1) {
            while (double2 > 0.0 && double2 % 10.0 == 0.0) {
                double2 /= 10.0;
                int0--;
            }
        }

        stringBuilder.append('.');
        int int4 = stringBuilder.length();
        this.extend(stringBuilder, int1, '0');
        int int5 = stringBuilder.length();
        stringBufferAppend(stringBuilder, double2, 10, false, 0);
        int int6 = stringBuilder.length();
        int int7 = int6 - int5;
        if (boolean1 && int7 < int0) {
            int int8 = int0 - int7 - int1;
            this.extend(stringBuilder, int8, '0');
        }

        if (!boolean1 && int4 == stringBuilder.length()) {
            stringBuilder.delete(int4 - 1, stringBuilder.length());
        }
    }

    private void appendScientificNumber(StringBuilder stringBuilder, double double0, int int2, boolean boolean1, boolean boolean0) {
        int int0 = 0;

        for (int int1 = 0; int1 < 2; int1++) {
            if (double0 >= 1.0) {
                while (double0 >= 10.0) {
                    double0 /= 10.0;
                    int0++;
                }
            } else {
                while (double0 > 0.0 && double0 < 1.0) {
                    double0 *= 10.0;
                    int0--;
                }
            }

            double0 = roundToPrecision(double0, int2);
        }

        int int3 = Math.abs(int0);
        char char0;
        if (int0 >= 0) {
            char0 = '+';
        } else {
            char0 = '-';
        }

        if (boolean0) {
            this.appendSignificantNumber(stringBuilder, double0, int2, boolean1);
        } else {
            this.appendPrecisionNumber(stringBuilder, double0, int2, boolean1);
        }

        stringBuilder.append('e');
        stringBuilder.append(char0);
        stringBufferAppend(stringBuilder, int3, 10, true, 2);
    }

    private String getStringArg(LuaCallFrame luaCallFrame, int int0) {
        return this.getStringArg(luaCallFrame, int0, names[6]);
    }

    private String getStringArg(LuaCallFrame luaCallFrame, int int0, String string) {
        return KahluaUtil.getStringArg(luaCallFrame, int0, string);
    }

    private Double getDoubleArg(LuaCallFrame luaCallFrame, int int0) {
        return this.getDoubleArg(luaCallFrame, int0, names[6]);
    }

    private Double getDoubleArg(LuaCallFrame luaCallFrame, int int0, String string) {
        return KahluaUtil.getNumberArg(luaCallFrame, int0, string);
    }

    private int lower(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "not enough arguments");
        String string = this.getStringArg(luaCallFrame, 1, names[3]);
        luaCallFrame.push(string.toLowerCase());
        return 1;
    }

    private int upper(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "not enough arguments");
        String string = this.getStringArg(luaCallFrame, 1, names[4]);
        luaCallFrame.push(string.toUpperCase());
        return 1;
    }

    private int contains(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 2, "not enough arguments");
        String string0 = this.getStringArg(luaCallFrame, 1, names[13]);
        String string1 = this.getStringArg(luaCallFrame, 2, names[13]);
        luaCallFrame.push(string0.contains(string1));
        return 1;
    }

    private int reverse(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "not enough arguments");
        String string = this.getStringArg(luaCallFrame, 1, names[5]);
        string = new StringBuilder(string).reverse().toString();
        luaCallFrame.push(string);
        return 1;
    }

    private int stringByte(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "not enough arguments");
        String string = this.getStringArg(luaCallFrame, 1, names[2]);
        int int1 = this.nullDefault(1, KahluaUtil.getOptionalNumberArg(luaCallFrame, 2));
        int int2 = this.nullDefault(int1, KahluaUtil.getOptionalNumberArg(luaCallFrame, 3));
        int int3 = string.length();
        if (int1 < 0) {
            int1 += int3 + 1;
        }

        if (int1 <= 0) {
            int1 = 1;
        }

        if (int2 < 0) {
            int2 += int3 + 1;
        } else if (int2 > int3) {
            int2 = int3;
        }

        int int4 = 1 + int2 - int1;
        if (int4 <= 0) {
            return 0;
        } else {
            luaCallFrame.setTop(int4);
            int int5 = int1 - 1;

            for (int int6 = 0; int6 < int4; int6++) {
                char char0 = string.charAt(int5 + int6);
                luaCallFrame.set(int6, KahluaUtil.toDouble((long)char0));
            }

            return int4;
        }
    }

    private int nullDefault(int int0, Double double0) {
        return double0 == null ? int0 : double0.intValue();
    }

    private int stringChar(LuaCallFrame luaCallFrame, int int1) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int int0 = 0; int0 < int1; int0++) {
            int int2 = this.getDoubleArg(luaCallFrame, int0 + 1, names[1]).intValue();
            stringBuilder.append((char)int2);
        }

        return luaCallFrame.push(stringBuilder.toString());
    }

    private int sub(LuaCallFrame luaCallFrame, int int0) {
        String string0 = this.getStringArg(luaCallFrame, 1, names[0]);
        double double0 = this.getDoubleArg(luaCallFrame, 2, names[0]);
        double double1 = -1.0;
        if (int0 >= 3) {
            double1 = this.getDoubleArg(luaCallFrame, 3, names[0]);
        }

        int int1 = (int)double0;
        int int2 = (int)double1;
        int int3 = string0.length();
        if (int1 < 0) {
            int1 = Math.max(int3 + int1 + 1, 1);
        } else if (int1 == 0) {
            int1 = 1;
        }

        if (int2 < 0) {
            int2 = Math.max(0, int2 + int3 + 1);
        } else if (int2 > int3) {
            int2 = int3;
        }

        if (int1 > int2) {
            return luaCallFrame.push("");
        } else {
            String string1 = string0.substring(int1 - 1, int2);
            return luaCallFrame.push(string1);
        }
    }

    public static double roundToPrecision(double double1, int int0) {
        double double0 = KahluaUtil.ipow(10L, int0);
        return KahluaUtil.round(double1 * double0) / double0;
    }

    public static double roundToSignificantNumbers(double double0, int int0) {
        if (double0 == 0.0) {
            return 0.0;
        } else if (double0 < 0.0) {
            return -roundToSignificantNumbers(-double0, int0);
        } else {
            double double1 = KahluaUtil.ipow(10L, int0 - 1);
            double double2 = double1 * 10.0;
            double double3 = 1.0;

            while (double3 * double0 < double1) {
                double3 *= 10.0;
            }

            while (double3 * double0 >= double2) {
                double3 /= 10.0;
            }

            return KahluaUtil.round(double0 * double3) / double3;
        }
    }

    private static Object push_onecapture(
        StringLib.MatchState matchState, int int0, StringLib.StringPointer stringPointer1, StringLib.StringPointer stringPointer0
    ) {
        if (int0 >= matchState.level) {
            if (int0 == 0) {
                String string0 = stringPointer1.string.substring(stringPointer1.index, stringPointer0.index);
                matchState.callFrame.push(string0);
                return string0;
            } else {
                throw new RuntimeException("invalid capture index");
            }
        } else {
            int int1 = matchState.capture[int0].len;
            if (int1 == -1) {
                throw new RuntimeException("unfinished capture");
            } else if (int1 == -2) {
                Double double0 = new Double(matchState.src_init.length() - matchState.capture[int0].init.length() + 1);
                matchState.callFrame.push(double0);
                return double0;
            } else {
                int int2 = matchState.capture[int0].init.index;
                String string1 = matchState.capture[int0].init.string.substring(int2, int2 + int1);
                matchState.callFrame.push(string1);
                return string1;
            }
        }
    }

    private static int push_captures(StringLib.MatchState matchState, StringLib.StringPointer stringPointer0, StringLib.StringPointer stringPointer1) {
        int int0 = matchState.level == 0 && stringPointer0 != null ? 1 : matchState.level;
        KahluaUtil.luaAssert(int0 <= 32, "too many captures");

        for (int int1 = 0; int1 < int0; int1++) {
            push_onecapture(matchState, int1, stringPointer0, stringPointer1);
        }

        return int0;
    }

    private static boolean noSpecialChars(String string) {
        for (int int0 = 0; int0 < string.length(); int0++) {
            char char0 = string.charAt(int0);
            if (char0 < 256 && SPECIALS[char0]) {
                return false;
            }
        }

        return true;
    }

    private static int findAux(LuaCallFrame luaCallFrame, boolean boolean0) {
        String string0 = boolean0 ? names[7] : names[8];
        String string1 = KahluaUtil.getStringArg(luaCallFrame, 1, string0);
        String string2 = KahluaUtil.getStringArg(luaCallFrame, 2, string0);
        Double double0 = KahluaUtil.getOptionalNumberArg(luaCallFrame, 3);
        boolean boolean1 = KahluaUtil.boolEval(KahluaUtil.getOptionalArg(luaCallFrame, 4));
        int int0 = double0 == null ? 0 : double0.intValue() - 1;
        if (int0 < 0) {
            int0 += string1.length();
            if (int0 < 0) {
                int0 = 0;
            }
        } else if (int0 > string1.length()) {
            int0 = string1.length();
        }

        if (!boolean0 || !boolean1 && !noSpecialChars(string2)) {
            StringLib.StringPointer stringPointer0 = new StringLib.StringPointer(string1);
            StringLib.StringPointer stringPointer1 = new StringLib.StringPointer(string2);
            boolean boolean2 = false;
            if (stringPointer1.getChar() == '^') {
                boolean2 = true;
                stringPointer1.postIncrString(1);
            }

            StringLib.StringPointer stringPointer2 = stringPointer0.getClone();
            stringPointer2.postIncrString(int0);
            StringLib.MatchState matchState = new StringLib.MatchState(luaCallFrame, stringPointer0.getClone(), stringPointer0.getStringLength());

            do {
                matchState.level = 0;
                StringLib.StringPointer stringPointer3;
                if ((stringPointer3 = match(matchState, stringPointer2, stringPointer1)) != null) {
                    if (boolean0) {
                        return luaCallFrame.push(
                                new Double(stringPointer0.length() - stringPointer2.length() + 1),
                                new Double(stringPointer0.length() - stringPointer3.length())
                            )
                            + push_captures(matchState, null, null);
                    }

                    return push_captures(matchState, stringPointer2, stringPointer3);
                }
            } while (stringPointer2.postIncrStringI(1) < matchState.endIndex && !boolean2);
        } else {
            int int1 = string1.indexOf(string2, int0);
            if (int1 > -1) {
                return luaCallFrame.push(KahluaUtil.toDouble((long)(int1 + 1)), KahluaUtil.toDouble((long)(int1 + string2.length())));
            }
        }

        return luaCallFrame.pushNil();
    }

    private static StringLib.StringPointer startCapture(
        StringLib.MatchState matchState, StringLib.StringPointer stringPointer0, StringLib.StringPointer stringPointer2, int int1
    ) {
        int int0 = matchState.level;
        KahluaUtil.luaAssert(int0 < 32, "too many captures");
        matchState.capture[int0].init = stringPointer0.getClone();
        matchState.capture[int0].init.setIndex(stringPointer0.getIndex());
        matchState.capture[int0].len = int1;
        matchState.level = int0 + 1;
        StringLib.StringPointer stringPointer1;
        if ((stringPointer1 = match(matchState, stringPointer0, stringPointer2)) == null) {
            matchState.level--;
        }

        return stringPointer1;
    }

    private static int captureToClose(StringLib.MatchState matchState) {
        int int0 = matchState.level;
        int0--;

        while (int0 >= 0) {
            if (matchState.capture[int0].len == -1) {
                return int0;
            }

            int0--;
        }

        throw new RuntimeException("invalid pattern capture");
    }

    private static StringLib.StringPointer endCapture(
        StringLib.MatchState matchState, StringLib.StringPointer stringPointer0, StringLib.StringPointer stringPointer2
    ) {
        int int0 = captureToClose(matchState);
        matchState.capture[int0].len = matchState.capture[int0].init.length() - stringPointer0.length();
        StringLib.StringPointer stringPointer1;
        if ((stringPointer1 = match(matchState, stringPointer0, stringPointer2)) == null) {
            matchState.capture[int0].len = -1;
        }

        return stringPointer1;
    }

    private static int checkCapture(StringLib.MatchState matchState, int int0) {
        int0 -= 49;
        KahluaUtil.luaAssert(int0 < 0 || int0 >= matchState.level || matchState.capture[int0].len == -1, "invalid capture index");
        return int0;
    }

    private static StringLib.StringPointer matchCapture(StringLib.MatchState matchState, StringLib.StringPointer stringPointer0, int int0) {
        int0 = checkCapture(matchState, int0);
        int int1 = matchState.capture[int0].len;
        if (matchState.endIndex - stringPointer0.length() >= int1 && matchState.capture[int0].init.compareTo(stringPointer0, int1) == 0) {
            StringLib.StringPointer stringPointer1 = stringPointer0.getClone();
            stringPointer1.postIncrString(int1);
            return stringPointer1;
        } else {
            return null;
        }
    }

    private static StringLib.StringPointer matchBalance(
        StringLib.MatchState matchState, StringLib.StringPointer stringPointer2, StringLib.StringPointer stringPointer0
    ) {
        KahluaUtil.luaAssert(stringPointer0.getChar() != 0 && stringPointer0.getChar(1) != 0, "unbalanced pattern");
        StringLib.StringPointer stringPointer1 = stringPointer2.getClone();
        if (stringPointer1.getChar() != stringPointer0.getChar()) {
            return null;
        } else {
            char char0 = stringPointer0.getChar();
            char char1 = stringPointer0.getChar(1);
            int int0 = 1;

            while (stringPointer1.preIncrStringI(1) < matchState.endIndex) {
                if (stringPointer1.getChar() == char1) {
                    if (--int0 == 0) {
                        StringLib.StringPointer stringPointer3 = stringPointer1.getClone();
                        stringPointer3.postIncrString(1);
                        return stringPointer3;
                    }
                } else if (stringPointer1.getChar() == char0) {
                    int0++;
                }
            }

            return null;
        }
    }

    private static StringLib.StringPointer classEnd(StringLib.StringPointer stringPointer1) {
        StringLib.StringPointer stringPointer0 = stringPointer1.getClone();
        switch (stringPointer0.postIncrString(1)) {
            case '%':
                KahluaUtil.luaAssert(stringPointer0.getChar() != 0, "malformed pattern (ends with '%')");
                stringPointer0.postIncrString(1);
                return stringPointer0;
            case '[':
                if (stringPointer0.getChar() == '^') {
                    stringPointer0.postIncrString(1);
                }

                do {
                    KahluaUtil.luaAssert(stringPointer0.getChar() != 0, "malformed pattern (missing ']')");
                    if (stringPointer0.postIncrString(1) == '%' && stringPointer0.getChar() != 0) {
                        stringPointer0.postIncrString(1);
                    }
                } while (stringPointer0.getChar() != ']');

                stringPointer0.postIncrString(1);
                return stringPointer0;
            default:
                return stringPointer0;
        }
    }

    private static boolean singleMatch(char char0, StringLib.StringPointer stringPointer0, StringLib.StringPointer stringPointer2) {
        switch (stringPointer0.getChar()) {
            case '%':
                return matchClass(stringPointer0.getChar(1), char0);
            case '.':
                return true;
            case '[':
                StringLib.StringPointer stringPointer1 = stringPointer2.getClone();
                stringPointer1.postIncrString(-1);
                return matchBracketClass(char0, stringPointer0, stringPointer1);
            default:
                return stringPointer0.getChar() == char0;
        }
    }

    private static StringLib.StringPointer minExpand(
        StringLib.MatchState matchState, StringLib.StringPointer stringPointer3, StringLib.StringPointer stringPointer5, StringLib.StringPointer stringPointer1
    ) {
        StringLib.StringPointer stringPointer0 = stringPointer1.getClone();
        StringLib.StringPointer stringPointer2 = stringPointer3.getClone();
        stringPointer0.postIncrString(1);

        while (true) {
            StringLib.StringPointer stringPointer4 = match(matchState, stringPointer2, stringPointer0);
            if (stringPointer4 != null) {
                return stringPointer4;
            }

            if (stringPointer2.getIndex() >= matchState.endIndex || !singleMatch(stringPointer2.getChar(), stringPointer5, stringPointer1)) {
                return null;
            }

            stringPointer2.postIncrString(1);
        }
    }

    private static StringLib.StringPointer maxExpand(
        StringLib.MatchState matchState, StringLib.StringPointer stringPointer2, StringLib.StringPointer stringPointer0, StringLib.StringPointer stringPointer1
    ) {
        int int0 = 0;

        while (stringPointer2.getIndex() + int0 < matchState.endIndex && singleMatch(stringPointer2.getChar(int0), stringPointer0, stringPointer1)) {
            int0++;
        }

        while (int0 >= 0) {
            StringLib.StringPointer stringPointer3 = stringPointer2.getClone();
            stringPointer3.postIncrString(int0);
            StringLib.StringPointer stringPointer4 = stringPointer1.getClone();
            stringPointer4.postIncrString(1);
            StringLib.StringPointer stringPointer5 = match(matchState, stringPointer3, stringPointer4);
            if (stringPointer5 != null) {
                return stringPointer5;
            }

            int0--;
        }

        return null;
    }

    private static boolean matchBracketClass(char char0, StringLib.StringPointer stringPointer1, StringLib.StringPointer stringPointer3) {
        StringLib.StringPointer stringPointer0 = stringPointer1.getClone();
        StringLib.StringPointer stringPointer2 = stringPointer3.getClone();
        boolean boolean0 = true;
        if (stringPointer0.getChar(1) == '^') {
            boolean0 = false;
            stringPointer0.postIncrString(1);
        }

        while (stringPointer0.preIncrStringI(1) < stringPointer2.getIndex()) {
            if (stringPointer0.getChar() == '%') {
                stringPointer0.postIncrString(1);
                if (matchClass(stringPointer0.getChar(), char0)) {
                    return boolean0;
                }
            } else if (stringPointer0.getChar(1) == '-' && stringPointer0.getIndex() + 2 < stringPointer2.getIndex()) {
                stringPointer0.postIncrString(2);
                if (stringPointer0.getChar(-2) <= char0 && char0 <= stringPointer0.getChar()) {
                    return boolean0;
                }
            } else if (stringPointer0.getChar() == char0) {
                return boolean0;
            }
        }

        return !boolean0;
    }

    private static StringLib.StringPointer match(
        StringLib.MatchState matchState, StringLib.StringPointer stringPointer1, StringLib.StringPointer stringPointer3
    ) {
        StringLib.StringPointer stringPointer0 = stringPointer1.getClone();
        StringLib.StringPointer stringPointer2 = stringPointer3.getClone();
        boolean boolean0 = true;
        boolean boolean1 = false;

        while (boolean0) {
            boolean0 = false;
            boolean1 = false;
            label87:
            switch (stringPointer2.getChar()) {
                case '\u0000':
                    return stringPointer0;
                case '$':
                    if (stringPointer2.getChar(1) == 0) {
                        return stringPointer0.getIndex() == matchState.endIndex ? stringPointer0 : null;
                    }
                default:
                    boolean1 = true;
                    break;
                case '%':
                    switch (stringPointer2.getChar(1)) {
                        case 'b':
                            StringLib.StringPointer stringPointer6 = stringPointer2.getClone();
                            stringPointer6.postIncrString(2);
                            stringPointer0 = matchBalance(matchState, stringPointer0, stringPointer6);
                            if (stringPointer0 == null) {
                                return null;
                            }

                            stringPointer2.postIncrString(4);
                            boolean0 = true;
                            continue;
                        case 'f':
                            stringPointer2.postIncrString(2);
                            KahluaUtil.luaAssert(stringPointer2.getChar() == '[', "missing '[' after '%%f' in pattern");
                            StringLib.StringPointer stringPointer7 = classEnd(stringPointer2);
                            char char0 = stringPointer0.getIndex() == matchState.src_init.getIndex() ? 0 : stringPointer0.getChar(-1);
                            StringLib.StringPointer stringPointer8 = stringPointer7.getClone();
                            stringPointer8.postIncrString(-1);
                            if (!matchBracketClass(char0, stringPointer2, stringPointer8)
                                && matchBracketClass(stringPointer0.getChar(), stringPointer2, stringPointer8)) {
                                stringPointer2 = stringPointer7;
                                boolean0 = true;
                                continue;
                            }

                            return null;
                        default:
                            if (Character.isDigit(stringPointer2.getChar(1))) {
                                stringPointer0 = matchCapture(matchState, stringPointer0, stringPointer2.getChar(1));
                                if (stringPointer0 == null) {
                                    return null;
                                }

                                stringPointer2.postIncrString(2);
                                boolean0 = true;
                                continue;
                            }

                            boolean1 = true;
                            break label87;
                    }
                case '(':
                    StringLib.StringPointer stringPointer5 = stringPointer2.getClone();
                    if (stringPointer2.getChar(1) == ')') {
                        stringPointer5.postIncrString(2);
                        return startCapture(matchState, stringPointer0, stringPointer5, -2);
                    }

                    stringPointer5.postIncrString(1);
                    return startCapture(matchState, stringPointer0, stringPointer5, -1);
                case ')':
                    StringLib.StringPointer stringPointer4 = stringPointer2.getClone();
                    stringPointer4.postIncrString(1);
                    return endCapture(matchState, stringPointer0, stringPointer4);
            }

            if (boolean1) {
                StringLib.StringPointer stringPointer9 = classEnd(stringPointer2);
                boolean boolean2 = stringPointer0.getIndex() < matchState.endIndex && singleMatch(stringPointer0.getChar(), stringPointer2, stringPointer9);
                switch (stringPointer9.getChar()) {
                    case '*':
                        return maxExpand(matchState, stringPointer0, stringPointer2, stringPointer9);
                    case '+':
                        StringLib.StringPointer stringPointer10 = stringPointer0.getClone();
                        stringPointer10.postIncrString(1);
                        return boolean2 ? maxExpand(matchState, stringPointer10, stringPointer2, stringPointer9) : null;
                    case '-':
                        return minExpand(matchState, stringPointer0, stringPointer2, stringPointer9);
                    case '?':
                        StringLib.StringPointer stringPointer11 = stringPointer0.getClone();
                        stringPointer11.postIncrString(1);
                        StringLib.StringPointer stringPointer12 = stringPointer9.getClone();
                        stringPointer12.postIncrString(1);
                        StringLib.StringPointer stringPointer13;
                        if (boolean2 && (stringPointer13 = match(matchState, stringPointer11, stringPointer12)) != null) {
                            return stringPointer13;
                        }

                        stringPointer2 = stringPointer9;
                        stringPointer9.postIncrString(1);
                        boolean0 = true;
                        break;
                    default:
                        if (!boolean2) {
                            return null;
                        }

                        stringPointer0.postIncrString(1);
                        stringPointer2 = stringPointer9;
                        boolean0 = true;
                }
            }
        }

        return null;
    }

    private static boolean matchClass(char char1, char char2) {
        char char0 = Character.toLowerCase(char1);
        boolean boolean0;
        switch (char0) {
            case 'a':
                boolean0 = Character.isLowerCase(char2) || Character.isUpperCase(char2);
                break;
            case 'b':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'm':
            case 'n':
            case 'o':
            case 'q':
            case 'r':
            case 't':
            case 'v':
            case 'y':
            default:
                return char1 == char2;
            case 'c':
                boolean0 = isControl(char2);
                break;
            case 'd':
                boolean0 = Character.isDigit(char2);
                break;
            case 'l':
                boolean0 = Character.isLowerCase(char2);
                break;
            case 'p':
                boolean0 = isPunct(char2);
                break;
            case 's':
                boolean0 = isSpace(char2);
                break;
            case 'u':
                boolean0 = Character.isUpperCase(char2);
                break;
            case 'w':
                boolean0 = Character.isLowerCase(char2) || Character.isUpperCase(char2) || Character.isDigit(char2);
                break;
            case 'x':
                boolean0 = isHex(char2);
                break;
            case 'z':
                boolean0 = char2 == 0;
        }

        return char0 == char1 == boolean0;
    }

    private static boolean isPunct(char char0) {
        return char0 >= '!' && char0 <= '/' || char0 >= ':' && char0 <= '@' || char0 >= '[' && char0 <= '`' || char0 >= '{' && char0 <= '~';
    }

    private static boolean isSpace(char char0) {
        return char0 >= '\t' && char0 <= '\r' || char0 == ' ';
    }

    private static boolean isControl(char char0) {
        return char0 >= 0 && char0 <= 31 || char0 == 127;
    }

    private static boolean isHex(char char0) {
        return char0 >= '0' && char0 <= '9' || char0 >= 'a' && char0 <= 'f' || char0 >= 'A' && char0 <= 'F';
    }

    private static int gsub(LuaCallFrame luaCallFrame, int var1) {
        String string0 = KahluaUtil.getStringArg(luaCallFrame, 1, names[9]);
        String string1 = KahluaUtil.getStringArg(luaCallFrame, 2, names[9]);
        Object object = KahluaUtil.getArg(luaCallFrame, 3, names[9]);
        String string2 = KahluaUtil.rawTostring(object);
        if (string2 != null) {
            object = string2;
        }

        Double double0 = KahluaUtil.getOptionalNumberArg(luaCallFrame, 4);
        int int0 = double0 == null ? Integer.MAX_VALUE : double0.intValue();
        StringLib.StringPointer stringPointer0 = new StringLib.StringPointer(string1);
        StringLib.StringPointer stringPointer1 = new StringLib.StringPointer(string0);
        boolean boolean0 = false;
        if (stringPointer0.getChar() == '^') {
            boolean0 = true;
            stringPointer0.postIncrString(1);
        }

        if (!(object instanceof Double)
            && !(object instanceof String)
            && !(object instanceof LuaClosure)
            && !(object instanceof JavaFunction)
            && !(object instanceof KahluaTable)) {
            KahluaUtil.fail("string/function/table expected, got " + object);
        }

        StringLib.MatchState matchState = new StringLib.MatchState(luaCallFrame, stringPointer1.getClone(), stringPointer1.length());
        int int1 = 0;
        StringBuilder stringBuilder = new StringBuilder();

        while (int1 < int0) {
            matchState.level = 0;
            StringLib.StringPointer stringPointer2 = match(matchState, stringPointer1, stringPointer0);
            if (stringPointer2 != null) {
                int1++;
                addValue(matchState, object, stringBuilder, stringPointer1, stringPointer2);
            }

            if (stringPointer2 != null && stringPointer2.getIndex() > stringPointer1.getIndex()) {
                stringPointer1.setIndex(stringPointer2.getIndex());
            } else {
                if (stringPointer1.getIndex() >= matchState.endIndex) {
                    break;
                }

                stringBuilder.append(stringPointer1.postIncrString(1));
            }

            if (boolean0) {
                break;
            }
        }

        return luaCallFrame.push(stringBuilder.append(stringPointer1.getString()).toString(), new Double(int1));
    }

    private static int trim(LuaCallFrame luaCallFrame, int var1) {
        String string = KahluaUtil.getStringArg(luaCallFrame, 1, names[10]);
        return luaCallFrame.push(string.trim());
    }

    private static int split(LuaCallFrame luaCallFrame, int var1) {
        String string0 = KahluaUtil.getStringArg(luaCallFrame, 1, names[11]);
        String string1 = KahluaUtil.getStringArg(luaCallFrame, 2, names[11]);
        String[] strings = string0.split(string1);
        KahluaTable table = LuaManager.platform.newTable();

        for (int int0 = 0; int0 < strings.length; int0++) {
            table.rawset(int0 + 1, strings[int0]);
        }

        return luaCallFrame.push(table);
    }

    private static int sort(LuaCallFrame luaCallFrame, int var1) {
        String string0 = KahluaUtil.getStringArg(luaCallFrame, 1, names[12]);
        String string1 = KahluaUtil.getStringArg(luaCallFrame, 2, names[12]);
        return luaCallFrame.push(string0.compareTo(string1) > 0);
    }

    private static void addValue(
        StringLib.MatchState matchState,
        Object object0,
        StringBuilder stringBuilder,
        StringLib.StringPointer stringPointer0,
        StringLib.StringPointer stringPointer1
    ) {
        String string0 = KahluaUtil.rawTostring(object0);
        if (string0 != null) {
            stringBuilder.append(addString(matchState, string0, stringPointer0, stringPointer1));
        } else {
            Object object1 = matchState.getCapture(0);
            String string1;
            if (object1 != null) {
                string1 = KahluaUtil.rawTostring(object1);
            } else {
                string1 = stringPointer0.getStringSubString(stringPointer1.getIndex() - stringPointer0.getIndex());
            }

            Object object2 = null;
            if (object0 instanceof KahluaTable) {
                object2 = ((KahluaTable)object0).rawget(string1);
            } else {
                object2 = matchState.callFrame.getThread().call(object0, string1, null, null);
            }

            if (object2 == null) {
                object2 = string1;
            }

            stringBuilder.append(KahluaUtil.rawTostring(object2));
        }
    }

    private static String addString(
        StringLib.MatchState matchState, String string, StringLib.StringPointer stringPointer2, StringLib.StringPointer stringPointer1
    ) {
        StringLib.StringPointer stringPointer0 = new StringLib.StringPointer(string);
        StringBuilder stringBuilder = new StringBuilder();

        for (int int0 = 0; int0 < string.length(); int0++) {
            char char0 = stringPointer0.getChar(int0);
            if (char0 != '%') {
                stringBuilder.append(char0);
            } else {
                char0 = stringPointer0.getChar(++int0);
                if (!Character.isDigit(char0)) {
                    stringBuilder.append(char0);
                } else if (char0 == '0') {
                    int int1 = stringPointer2.getStringLength() - stringPointer1.length();
                    stringBuilder.append(stringPointer2.getStringSubString(int1));
                } else {
                    Object object = matchState.getCapture(char0 - '1');
                    if (object == null) {
                        throw new KahluaException("invalid capture index");
                    }

                    stringBuilder.append(KahluaUtil.tostring(object, null));
                }
            }
        }

        return stringBuilder.toString();
    }

    static {
        String string = "^$*+?.([%-";

        for (int int0 = 0; int0 < string.length(); int0++) {
            SPECIALS[string.charAt(int0)] = true;
        }

        STRING_CLASS = "".getClass();
        names = new String[14];
        names[0] = "sub";
        names[1] = "char";
        names[2] = "byte";
        names[3] = "lower";
        names[4] = "upper";
        names[5] = "reverse";
        names[6] = "format";
        names[7] = "find";
        names[8] = "match";
        names[9] = "gsub";
        names[10] = "trim";
        names[11] = "split";
        names[12] = "sort";
        names[13] = "contains";
        functions = new StringLib[14];

        for (int int1 = 0; int1 < 14; int1++) {
            functions[int1] = new StringLib(int1);
        }

        digits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    }

    public static class MatchState {
        public final LuaCallFrame callFrame;
        public final StringLib.StringPointer src_init;
        public final int endIndex;
        public final StringLib.MatchState.Capture[] capture;
        public int level;

        public MatchState(LuaCallFrame luaCallFrame, StringLib.StringPointer stringPointer, int int0) {
            this.callFrame = luaCallFrame;
            this.src_init = stringPointer;
            this.endIndex = int0;
            this.capture = new StringLib.MatchState.Capture[32];

            for (int int1 = 0; int1 < 32; int1++) {
                this.capture[int1] = new StringLib.MatchState.Capture();
            }
        }

        public Object getCapture(int int0) {
            if (int0 >= this.level) {
                return null;
            } else {
                return this.capture[int0].len == -2
                    ? new Double(this.src_init.length() - this.capture[int0].init.length() + 1)
                    : this.capture[int0].init.getStringSubString(this.capture[int0].len);
            }
        }

        public static class Capture {
            public StringLib.StringPointer init;
            public int len;
        }
    }

    public static class StringPointer {
        private final String string;
        private int index = 0;

        public StringPointer(String stringx) {
            this.string = stringx;
        }

        public StringPointer(String stringx, int int0) {
            this.string = stringx;
            this.index = int0;
        }

        public StringLib.StringPointer getClone() {
            return new StringLib.StringPointer(this.string, this.index);
        }

        public int getIndex() {
            return this.index;
        }

        public void setIndex(int int0) {
            this.index = int0;
        }

        public String getString() {
            return this.index == 0 ? this.string : this.string.substring(this.index);
        }

        public int getStringLength() {
            return this.getStringLength(0);
        }

        public int getStringLength(int int0) {
            return this.string.length() - (this.index + int0);
        }

        public String getStringSubString(int int0) {
            return this.string.substring(this.index, this.index + int0);
        }

        public char getChar() {
            return this.getChar(0);
        }

        public char getChar(int int1) {
            int int0 = this.index + int1;
            return int0 >= this.string.length() ? '\u0000' : this.string.charAt(int0);
        }

        public int length() {
            return this.string.length() - this.index;
        }

        public int postIncrStringI(int int1) {
            int int0 = this.index;
            this.index += int1;
            return int0;
        }

        public int preIncrStringI(int int0) {
            this.index += int0;
            return this.index;
        }

        public char postIncrString(int int0) {
            char char0 = this.getChar();
            this.index += int0;
            return char0;
        }

        public int compareTo(StringLib.StringPointer stringPointer0, int int1) {
            for (int int0 = 0; int0 < int1; int0++) {
                int int2 = this.getChar(int0) - stringPointer0.getChar(int0);
                if (int2 != 0) {
                    return int2;
                }
            }

            return 0;
        }
    }
}
