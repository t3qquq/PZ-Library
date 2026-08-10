/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.stdlib;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;

public class OsLib
implements JavaFunction {
    private static final int DATE = 0;
    private static final int DIFFTIME = 1;
    private static final int TIME = 2;
    private static final int NUM_FUNCS = 3;
    private static final String[] funcnames = new String[3];
    private static final OsLib[] funcs;
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

    public static void register(Platform platform, KahluaTable kahluaTable) {
        KahluaTable kahluaTable2 = platform.newTable();
        for (int i = 0; i < 3; ++i) {
            kahluaTable2.rawset(funcnames[i], (Object)funcs[i]);
        }
        kahluaTable.rawset("os", (Object)kahluaTable2);
    }

    private OsLib(int n) {
        this.methodId = n;
    }

    @Override
    public int call(LuaCallFrame luaCallFrame, int n) {
        switch (this.methodId) {
            case 0: {
                return this.date(luaCallFrame, n);
            }
            case 1: {
                return this.difftime(luaCallFrame, n);
            }
            case 2: {
                return this.time(luaCallFrame, n);
            }
        }
        throw new RuntimeException("Undefined method called on os.");
    }

    private int time(LuaCallFrame luaCallFrame, int n) {
        if (n == 0) {
            double d = (double)System.currentTimeMillis() * 0.001;
            luaCallFrame.push(KahluaUtil.toDouble(d));
        } else {
            KahluaTable kahluaTable = (KahluaTable)KahluaUtil.getArg(luaCallFrame, 1, "time");
            double d = (double)OsLib.getDateFromTable(kahluaTable).getTime() * 0.001;
            luaCallFrame.push(KahluaUtil.toDouble(d));
        }
        return 1;
    }

    private int difftime(LuaCallFrame luaCallFrame, int n) {
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 1, "difftime");
        double d2 = KahluaUtil.getDoubleArg(luaCallFrame, 2, "difftime");
        luaCallFrame.push(KahluaUtil.toDouble(d - d2));
        return 1;
    }

    private int date(LuaCallFrame luaCallFrame, int n) {
        Platform platform = luaCallFrame.getPlatform();
        if (n == 0) {
            return luaCallFrame.push(OsLib.getdate(DEFAULT_FORMAT, platform));
        }
        String string = KahluaUtil.getStringArg(luaCallFrame, 1, "date");
        if (n == 1) {
            return luaCallFrame.push(OsLib.getdate(string, platform));
        }
        double d = KahluaUtil.getDoubleArg(luaCallFrame, 2, "date");
        long l = (long)(d * 1000.0);
        return luaCallFrame.push(OsLib.getdate(string, l, platform));
    }

    public static Object getdate(String string, Platform platform) {
        return OsLib.getdate(string, Calendar.getInstance().getTime().getTime(), platform);
    }

    public static Object getdate(String string, long l, Platform platform) {
        Calendar calendar = null;
        int n = 0;
        if (string.charAt(n) == '!') {
            calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            ++n;
        } else {
            calendar = Calendar.getInstance(tzone);
        }
        calendar.setTime(new Date(l));
        if (calendar == null) {
            return null;
        }
        if (string.substring(n, 2 + n).equals(TABLE_FORMAT)) {
            return OsLib.getTableFromDate(calendar, platform);
        }
        return OsLib.formatTime(string.substring(n), calendar);
    }

    public static String formatTime(String string, Calendar calendar) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < string.length(); ++i) {
            if (string.charAt(i) != '%' || i + 1 == string.length()) {
                stringBuilder.append(string.charAt(i));
                continue;
            }
            stringBuilder.append(OsLib.strftime(string.charAt(++i), calendar));
        }
        return stringBuilder.toString();
    }

    private static String format2Digits(int n) {
        Object object = Integer.toString(n);
        if (n < 10) {
            object = "0" + (String)object;
        }
        return object;
    }

    private static String strftime(char c, Calendar calendar) {
        switch (c) {
            case 'a': {
                return shortDayNames[calendar.get(7) - 1];
            }
            case 'A': {
                return longDayNames[calendar.get(7) - 1];
            }
            case 'b': {
                return shortMonthNames[calendar.get(2)];
            }
            case 'B': {
                return longMonthNames[calendar.get(2)];
            }
            case 'c': {
                return calendar.getTime().toString();
            }
            case 'C': {
                return Integer.toString(calendar.get(1) / 100);
            }
            case 'd': {
                return OsLib.format2Digits(calendar.get(5));
            }
            case 'D': {
                return OsLib.formatTime("%m/%d/%y", calendar);
            }
            case 'e': {
                return calendar.get(5) < 10 ? " " + OsLib.strftime('d', calendar) : OsLib.strftime('d', calendar);
            }
            case 'h': {
                return OsLib.strftime('b', calendar);
            }
            case 'H': {
                return OsLib.format2Digits(calendar.get(11));
            }
            case 'I': {
                return OsLib.format2Digits(calendar.get(10));
            }
            case 'j': {
                return Integer.toString(OsLib.getDayOfYear(calendar));
            }
            case 'm': {
                return OsLib.format2Digits(calendar.get(2) + 1);
            }
            case 'M': {
                return OsLib.format2Digits(calendar.get(12));
            }
            case 'n': {
                return "\n";
            }
            case 'p': {
                return calendar.get(9) == 0 ? "AM" : "PM";
            }
            case 'r': {
                return OsLib.formatTime("%I:%M:%S %p", calendar);
            }
            case 'R': {
                return OsLib.formatTime("%H:%M", calendar);
            }
            case 'S': {
                return OsLib.format2Digits(calendar.get(13));
            }
            case 'U': {
                return Integer.toString(OsLib.getWeekOfYear(calendar, true, false));
            }
            case 'V': {
                return Integer.toString(OsLib.getWeekOfYear(calendar, false, true));
            }
            case 'w': {
                return Integer.toString(calendar.get(7) - 1);
            }
            case 'W': {
                return Integer.toString(OsLib.getWeekOfYear(calendar, false, false));
            }
            case 'y': {
                return Integer.toString(calendar.get(1) % 100);
            }
            case 'Y': {
                return Integer.toString(calendar.get(1));
            }
            case 'Z': {
                return calendar.getTimeZone().getID();
            }
        }
        return null;
    }

    public static KahluaTable getTableFromDate(Calendar calendar, Platform platform) {
        KahluaTable kahluaTable = platform.newTable();
        kahluaTable.rawset(YEAR, (Object)KahluaUtil.toDouble(calendar.get(1)));
        kahluaTable.rawset(MONTH, (Object)KahluaUtil.toDouble(calendar.get(2) + 1));
        kahluaTable.rawset(DAY, (Object)KahluaUtil.toDouble(calendar.get(5)));
        kahluaTable.rawset(HOUR, (Object)KahluaUtil.toDouble(calendar.get(11)));
        kahluaTable.rawset(MIN, (Object)KahluaUtil.toDouble(calendar.get(12)));
        kahluaTable.rawset(SEC, (Object)KahluaUtil.toDouble(calendar.get(13)));
        kahluaTable.rawset(WDAY, (Object)KahluaUtil.toDouble(calendar.get(7)));
        kahluaTable.rawset(YDAY, (Object)KahluaUtil.toDouble(OsLib.getDayOfYear(calendar)));
        kahluaTable.rawset(MILLISECOND, (Object)KahluaUtil.toDouble(calendar.get(14)));
        return kahluaTable;
    }

    public static Date getDateFromTable(KahluaTable kahluaTable) {
        Calendar calendar = Calendar.getInstance(tzone);
        calendar.set(1, (int)KahluaUtil.fromDouble(kahluaTable.rawget(YEAR)));
        calendar.set(2, (int)KahluaUtil.fromDouble(kahluaTable.rawget(MONTH)) - 1);
        calendar.set(5, (int)KahluaUtil.fromDouble(kahluaTable.rawget(DAY)));
        Object object = kahluaTable.rawget(HOUR);
        Object object2 = kahluaTable.rawget(MIN);
        Object object3 = kahluaTable.rawget(SEC);
        Object object4 = kahluaTable.rawget(MILLISECOND);
        if (object != null) {
            calendar.set(11, (int)KahluaUtil.fromDouble(object));
        } else {
            calendar.set(11, 0);
        }
        if (object2 != null) {
            calendar.set(12, (int)KahluaUtil.fromDouble(object2));
        } else {
            calendar.set(12, 0);
        }
        if (object3 != null) {
            calendar.set(13, (int)KahluaUtil.fromDouble(object3));
        } else {
            calendar.set(13, 0);
        }
        if (object4 != null) {
            calendar.set(14, (int)KahluaUtil.fromDouble(object4));
        } else {
            calendar.set(14, 0);
        }
        return calendar.getTime();
    }

    public static int getDayOfYear(Calendar calendar) {
        Calendar calendar2 = Calendar.getInstance(calendar.getTimeZone());
        calendar2.setTime(calendar.getTime());
        calendar2.set(2, 0);
        calendar2.set(5, 1);
        long l = calendar.getTime().getTime() - calendar2.getTime().getTime();
        return (int)Math.ceil((double)l / 8.64E7);
    }

    public static int getWeekOfYear(Calendar calendar, boolean bl, boolean bl2) {
        Calendar calendar2 = Calendar.getInstance(calendar.getTimeZone());
        calendar2.setTime(calendar.getTime());
        calendar2.set(2, 0);
        calendar2.set(5, 1);
        int n = calendar2.get(7);
        if (bl && n != 1) {
            calendar2.set(5, 7 - n + 1);
        } else if (n != 2) {
            calendar2.set(5, 7 - n + 1 + 1);
        }
        long l = calendar.getTime().getTime() - calendar2.getTime().getTime();
        int n2 = (int)(l / 604800000L);
        if (bl2 && 7 - n >= 4) {
            ++n2;
        }
        return n2;
    }

    static {
        OsLib.funcnames[0] = "date";
        OsLib.funcnames[1] = "difftime";
        OsLib.funcnames[2] = "time";
        funcs = new OsLib[3];
        for (int i = 0; i < 3; ++i) {
            OsLib.funcs[i] = new OsLib(i);
        }
        MILLISECOND = "milli";
        tzone = TimeZone.getTimeZone("UTC");
        shortDayNames = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        longDayNames = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        shortMonthNames = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        longMonthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    }
}

