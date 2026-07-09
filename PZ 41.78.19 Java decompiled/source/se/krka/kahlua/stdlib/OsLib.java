// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.stdlib;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;

public class OsLib implements JavaFunction {
    private static final int DATE = 0;
    private static final int DIFFTIME = 1;
    private static final int TIME = 2;
    private static final int NUM_FUNCS = 3;
    private static final String[] funcnames = new String[3];
    private static final OsLib[] funcs = new OsLib[3];
    private static final String TABLE_FORMAT = "*t";
    private static final String DEFAULT_FORMAT = "%c";
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";
    private static final String HOUR = "hour";
    private static final String MIN = "min";
    private static final String SEC = "sec";
    private static final String WDAY = "wday";
    private static final String YDAY = "yday";
    private static final Object MILLISECOND;
    private static TimeZone tzone;
    public static final int TIME_DIVIDEND = 1000;
    public static final double TIME_DIVIDEND_INVERTED = 0.001;
    private static final int MILLIS_PER_DAY = 86400000;
    private static final int MILLIS_PER_WEEK = 604800000;
    private int methodId;
    private static String[] shortDayNames;
    private static String[] longDayNames;
    private static String[] shortMonthNames;
    private static String[] longMonthNames;

    public static void register(Platform platform, KahluaTable table1) {
        KahluaTable table0 = platform.newTable();

        for (int int0 = 0; int0 < 3; int0++) {
            table0.rawset(funcnames[int0], funcs[int0]);
        }

        table1.rawset("os", table0);
    }

    private OsLib(int int0) {
        this.methodId = int0;
    }

    @Override
    public int call(LuaCallFrame luaCallFrame, int int0) {
        switch (this.methodId) {
            case 0:
                return this.date(luaCallFrame, int0);
            case 1:
                return this.difftime(luaCallFrame, int0);
            case 2:
                return this.time(luaCallFrame, int0);
            default:
                throw new RuntimeException("Undefined method called on os.");
        }
    }

    private int time(LuaCallFrame luaCallFrame, int int0) {
        if (int0 == 0) {
            double double0 = System.currentTimeMillis() * 0.001;
            luaCallFrame.push(KahluaUtil.toDouble(double0));
        } else {
            KahluaTable table = (KahluaTable)KahluaUtil.getArg(luaCallFrame, 1, "time");
            double double1 = getDateFromTable(table).getTime() * 0.001;
            luaCallFrame.push(KahluaUtil.toDouble(double1));
        }

        return 1;
    }

    private int difftime(LuaCallFrame luaCallFrame, int var2) {
        double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 1, "difftime");
        double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 2, "difftime");
        luaCallFrame.push(KahluaUtil.toDouble(double0 - double1));
        return 1;
    }

    private int date(LuaCallFrame luaCallFrame, int int0) {
        Platform platform = luaCallFrame.getPlatform();
        if (int0 == 0) {
            return luaCallFrame.push(getdate("%c", platform));
        } else {
            String string = KahluaUtil.getStringArg(luaCallFrame, 1, "date");
            if (int0 == 1) {
                return luaCallFrame.push(getdate(string, platform));
            } else {
                double double0 = KahluaUtil.getDoubleArg(luaCallFrame, 2, "date");
                long long0 = (long)(double0 * 1000.0);
                return luaCallFrame.push(getdate(string, long0, platform));
            }
        }
    }

    public static Object getdate(String string, Platform platform) {
        return getdate(string, Calendar.getInstance().getTime().getTime(), platform);
    }

    public static Object getdate(String string, long long0, Platform platform) {
        Calendar calendar = null;
        int int0 = 0;
        if (string.charAt(int0) == '!') {
            calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            int0++;
        } else {
            calendar = Calendar.getInstance(tzone);
        }

        calendar.setTime(new Date(long0));
        if (calendar == null) {
            return null;
        } else {
            return string.substring(int0, 2 + int0).equals("*t") ? getTableFromDate(calendar, platform) : formatTime(string.substring(int0), calendar);
        }
    }

    public static String formatTime(String string, Calendar calendar) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int int0 = 0; int0 < string.length(); int0++) {
            if (string.charAt(int0) == '%' && int0 + 1 != string.length()) {
                stringBuilder.append(strftime(string.charAt(++int0), calendar));
            } else {
                stringBuilder.append(string.charAt(int0));
            }
        }

        return stringBuilder.toString();
    }

    private static String format2Digits(int int0) {
        String string = Integer.toString(int0);
        if (int0 < 10) {
            string = "0" + string;
        }

        return string;
    }

    private static String strftime(char char0, Calendar calendar) {
        switch (char0) {
            case 'A':
                return longDayNames[calendar.get(7) - 1];
            case 'B':
                return longMonthNames[calendar.get(2)];
            case 'C':
                return Integer.toString(calendar.get(1) / 100);
            case 'D':
                return formatTime("%m/%d/%y", calendar);
            case 'E':
            case 'F':
            case 'G':
            case 'J':
            case 'K':
            case 'L':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'T':
            case 'X':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'f':
            case 'g':
            case 'i':
            case 'k':
            case 'l':
            case 'o':
            case 'q':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'x':
            default:
                return null;
            case 'H':
                return format2Digits(calendar.get(11));
            case 'I':
                return format2Digits(calendar.get(10));
            case 'M':
                return format2Digits(calendar.get(12));
            case 'R':
                return formatTime("%H:%M", calendar);
            case 'S':
                return format2Digits(calendar.get(13));
            case 'U':
                return Integer.toString(getWeekOfYear(calendar, true, false));
            case 'V':
                return Integer.toString(getWeekOfYear(calendar, false, true));
            case 'W':
                return Integer.toString(getWeekOfYear(calendar, false, false));
            case 'Y':
                return Integer.toString(calendar.get(1));
            case 'Z':
                return calendar.getTimeZone().getID();
            case 'a':
                return shortDayNames[calendar.get(7) - 1];
            case 'b':
                return shortMonthNames[calendar.get(2)];
            case 'c':
                return calendar.getTime().toString();
            case 'd':
                return format2Digits(calendar.get(5));
            case 'e':
                return calendar.get(5) < 10 ? " " + strftime('d', calendar) : strftime('d', calendar);
            case 'h':
                return strftime('b', calendar);
            case 'j':
                return Integer.toString(getDayOfYear(calendar));
            case 'm':
                return format2Digits(calendar.get(2) + 1);
            case 'n':
                return "\n";
            case 'p':
                return calendar.get(9) == 0 ? "AM" : "PM";
            case 'r':
                return formatTime("%I:%M:%S %p", calendar);
            case 'w':
                return Integer.toString(calendar.get(7) - 1);
            case 'y':
                return Integer.toString(calendar.get(1) % 100);
        }
    }

    public static KahluaTable getTableFromDate(Calendar calendar, Platform platform) {
        KahluaTable table = platform.newTable();
        table.rawset("year", KahluaUtil.toDouble((long)calendar.get(1)));
        table.rawset("month", KahluaUtil.toDouble((long)(calendar.get(2) + 1)));
        table.rawset("day", KahluaUtil.toDouble((long)calendar.get(5)));
        table.rawset("hour", KahluaUtil.toDouble((long)calendar.get(11)));
        table.rawset("min", KahluaUtil.toDouble((long)calendar.get(12)));
        table.rawset("sec", KahluaUtil.toDouble((long)calendar.get(13)));
        table.rawset("wday", KahluaUtil.toDouble((long)calendar.get(7)));
        table.rawset("yday", KahluaUtil.toDouble((long)getDayOfYear(calendar)));
        table.rawset(MILLISECOND, KahluaUtil.toDouble((long)calendar.get(14)));
        return table;
    }

    public static Date getDateFromTable(KahluaTable table) {
        Calendar calendar = Calendar.getInstance(tzone);
        calendar.set(1, (int)KahluaUtil.fromDouble(table.rawget("year")));
        calendar.set(2, (int)KahluaUtil.fromDouble(table.rawget("month")) - 1);
        calendar.set(5, (int)KahluaUtil.fromDouble(table.rawget("day")));
        Object object0 = table.rawget("hour");
        Object object1 = table.rawget("min");
        Object object2 = table.rawget("sec");
        Object object3 = table.rawget(MILLISECOND);
        if (object0 != null) {
            calendar.set(11, (int)KahluaUtil.fromDouble(object0));
        } else {
            calendar.set(11, 0);
        }

        if (object1 != null) {
            calendar.set(12, (int)KahluaUtil.fromDouble(object1));
        } else {
            calendar.set(12, 0);
        }

        if (object2 != null) {
            calendar.set(13, (int)KahluaUtil.fromDouble(object2));
        } else {
            calendar.set(13, 0);
        }

        if (object3 != null) {
            calendar.set(14, (int)KahluaUtil.fromDouble(object3));
        } else {
            calendar.set(14, 0);
        }

        return calendar.getTime();
    }

    public static int getDayOfYear(Calendar calendar1) {
        Calendar calendar0 = Calendar.getInstance(calendar1.getTimeZone());
        calendar0.setTime(calendar1.getTime());
        calendar0.set(2, 0);
        calendar0.set(5, 1);
        long long0 = calendar1.getTime().getTime() - calendar0.getTime().getTime();
        return (int)Math.ceil(long0 / 8.64E7);
    }

    public static int getWeekOfYear(Calendar calendar1, boolean boolean0, boolean boolean1) {
        Calendar calendar0 = Calendar.getInstance(calendar1.getTimeZone());
        calendar0.setTime(calendar1.getTime());
        calendar0.set(2, 0);
        calendar0.set(5, 1);
        int int0 = calendar0.get(7);
        if (boolean0 && int0 != 1) {
            calendar0.set(5, 7 - int0 + 1);
        } else if (int0 != 2) {
            calendar0.set(5, 7 - int0 + 1 + 1);
        }

        long long0 = calendar1.getTime().getTime() - calendar0.getTime().getTime();
        int int1 = (int)(long0 / 604800000L);
        if (boolean1 && 7 - int0 >= 4) {
            int1++;
        }

        return int1;
    }

    static {
        funcnames[0] = "date";
        funcnames[1] = "difftime";
        funcnames[2] = "time";

        for (int int0 = 0; int0 < 3; int0++) {
            funcs[int0] = new OsLib(int0);
        }

        MILLISECOND = "milli";
        tzone = TimeZone.getTimeZone("UTC");
        shortDayNames = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        longDayNames = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        shortMonthNames = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        longMonthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    }
}
