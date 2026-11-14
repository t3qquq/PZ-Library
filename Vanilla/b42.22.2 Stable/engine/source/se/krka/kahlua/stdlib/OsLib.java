// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
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
    private static final TimeZone tzone;
    public static final int TIME_DIVIDEND = 1000;
    public static final double TIME_DIVIDEND_INVERTED = 0.001;
    private static final int MILLIS_PER_DAY = 86400000;
    private static final int MILLIS_PER_WEEK = 604800000;
    private final int methodId;
    private static final String[] shortDayNames;
    private static final String[] longDayNames;
    private static final String[] shortMonthNames;
    private static final String[] longMonthNames;

    public static void register(Platform platform, KahluaTable environment) {
        KahluaTable os = platform.newTable();

        for (int i = 0; i < 3; i++) {
            os.rawset(funcnames[i], funcs[i]);
        }

        environment.rawset("os", os);
    }

    private OsLib(int methodId) {
        this.methodId = methodId;
    }

    @Override
    public int call(LuaCallFrame cf, int nargs) {
        switch (this.methodId) {
            case 0:
                return this.date(cf, nargs);
            case 1:
                return this.difftime(cf, nargs);
            case 2:
                return this.time(cf, nargs);
            default:
                throw new RuntimeException("Undefined method called on os.");
        }
    }

    private int time(LuaCallFrame cf, int nargs) {
        if (nargs == 0) {
            double t = System.currentTimeMillis() * 0.001;
            cf.push(KahluaUtil.toDouble(t));
        } else {
            KahluaTable table = (KahluaTable)KahluaUtil.getArg(cf, 1, "time");
            double t = getDateFromTable(table).getTime() * 0.001;
            cf.push(KahluaUtil.toDouble(t));
        }

        return 1;
    }

    private int difftime(LuaCallFrame cf, int nargs) {
        double t2 = KahluaUtil.getDoubleArg(cf, 1, "difftime");
        double t1 = KahluaUtil.getDoubleArg(cf, 2, "difftime");
        cf.push(KahluaUtil.toDouble(t2 - t1));
        return 1;
    }

    private int date(LuaCallFrame cf, int nArguments) {
        Platform platform = cf.getPlatform();
        if (nArguments == 0) {
            return cf.push(getdate("%c", platform));
        }

        String format = KahluaUtil.getStringArg(cf, 1, "date");
        if (nArguments == 1) {
            return cf.push(getdate(format, platform));
        }

        double t = KahluaUtil.getDoubleArg(cf, 2, "date");
        long time = (long)(t * 1000.0);
        return cf.push(getdate(format, time, platform));
    }

    public static Object getdate(String format, Platform platform) {
        return getdate(format, Calendar.getInstance().getTime().getTime(), platform);
    }

    public static Object getdate(String format, long time, Platform platform) {
        Calendar calendar = null;
        int si = 0;
        if (format.charAt(si) == '!') {
            calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            si++;
        } else {
            calendar = Calendar.getInstance(tzone);
        }

        calendar.setTime(new Date(time));
        if (calendar == null) {
            return null;
        } else {
            return format.startsWith("*t", si) ? getTableFromDate(calendar, platform) : formatTime(format.substring(si), calendar);
        }
    }

    public static String formatTime(String format, Calendar cal) {
        StringBuilder buffer = new StringBuilder();

        for (int stringIndex = 0; stringIndex < format.length(); stringIndex++) {
            if (format.charAt(stringIndex) == '%' && stringIndex + 1 != format.length()) {
                buffer.append(strftime(format.charAt(++stringIndex), cal));
            } else {
                buffer.append(format.charAt(stringIndex));
            }
        }

        return buffer.toString();
    }

    private static String format2Digits(int value) {
        String retval = Integer.toString(value);
        if (value < 10) {
            retval = "0" + retval;
        }

        return retval;
    }

    private static String strftime(char format, Calendar cal) {
        switch (format) {
            case 'A':
                return longDayNames[cal.get(7) - 1];
            case 'B':
                return longMonthNames[cal.get(2)];
            case 'C':
                return Integer.toString(cal.get(1) / 100);
            case 'D':
                return formatTime("%m/%d/%y", cal);
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
                return format2Digits(cal.get(11));
            case 'I':
                return format2Digits(cal.get(10));
            case 'M':
                return format2Digits(cal.get(12));
            case 'R':
                return formatTime("%H:%M", cal);
            case 'S':
                return format2Digits(cal.get(13));
            case 'U':
                return Integer.toString(getWeekOfYear(cal, true, false));
            case 'V':
                return Integer.toString(getWeekOfYear(cal, false, true));
            case 'W':
                return Integer.toString(getWeekOfYear(cal, false, false));
            case 'Y':
                return Integer.toString(cal.get(1));
            case 'Z':
                return cal.getTimeZone().getID();
            case 'a':
                return shortDayNames[cal.get(7) - 1];
            case 'b':
                return shortMonthNames[cal.get(2)];
            case 'c':
                return cal.getTime().toString();
            case 'd':
                return format2Digits(cal.get(5));
            case 'e':
                return cal.get(5) < 10 ? " " + strftime('d', cal) : strftime('d', cal);
            case 'h':
                return strftime('b', cal);
            case 'j':
                return Integer.toString(getDayOfYear(cal));
            case 'm':
                return format2Digits(cal.get(2) + 1);
            case 'n':
                return "\n";
            case 'p':
                return cal.get(9) == 0 ? "AM" : "PM";
            case 'r':
                return formatTime("%I:%M:%S %p", cal);
            case 'w':
                return Integer.toString(cal.get(7) - 1);
            case 'y':
                return Integer.toString(cal.get(1) % 100);
        }
    }

    public static KahluaTable getTableFromDate(Calendar c, Platform platform) {
        KahluaTable time = platform.newTable();
        time.rawset("year", KahluaUtil.toDouble(c.get(1)));
        time.rawset("month", KahluaUtil.toDouble(c.get(2) + 1));
        time.rawset("day", KahluaUtil.toDouble(c.get(5)));
        time.rawset("hour", KahluaUtil.toDouble(c.get(11)));
        time.rawset("min", KahluaUtil.toDouble(c.get(12)));
        time.rawset("sec", KahluaUtil.toDouble(c.get(13)));
        time.rawset("wday", KahluaUtil.toDouble(c.get(7)));
        time.rawset("yday", KahluaUtil.toDouble(getDayOfYear(c)));
        time.rawset(MILLISECOND, KahluaUtil.toDouble(c.get(14)));
        return time;
    }

    public static Date getDateFromTable(KahluaTable time) {
        Calendar c = Calendar.getInstance(tzone);
        c.set(1, (int)KahluaUtil.fromDouble(time.rawget("year")));
        c.set(2, (int)KahluaUtil.fromDouble(time.rawget("month")) - 1);
        c.set(5, (int)KahluaUtil.fromDouble(time.rawget("day")));
        Object hour = time.rawget("hour");
        Object minute = time.rawget("min");
        Object seconds = time.rawget("sec");
        Object milliseconds = time.rawget(MILLISECOND);
        if (hour != null) {
            c.set(11, (int)KahluaUtil.fromDouble(hour));
        } else {
            c.set(11, 0);
        }

        if (minute != null) {
            c.set(12, (int)KahluaUtil.fromDouble(minute));
        } else {
            c.set(12, 0);
        }

        if (seconds != null) {
            c.set(13, (int)KahluaUtil.fromDouble(seconds));
        } else {
            c.set(13, 0);
        }

        if (milliseconds != null) {
            c.set(14, (int)KahluaUtil.fromDouble(milliseconds));
        } else {
            c.set(14, 0);
        }

        return c.getTime();
    }

    public static int getDayOfYear(Calendar c) {
        Calendar c2 = Calendar.getInstance(c.getTimeZone());
        c2.setTime(c.getTime());
        c2.set(2, 0);
        c2.set(5, 1);
        long diff = c.getTime().getTime() - c2.getTime().getTime();
        return (int)Math.ceil(diff / 8.64E7);
    }

    public static int getWeekOfYear(Calendar c, boolean weekStartsSunday, boolean jan1midweek) {
        Calendar c2 = Calendar.getInstance(c.getTimeZone());
        c2.setTime(c.getTime());
        c2.set(2, 0);
        c2.set(5, 1);
        int dayOfWeek = c2.get(7);
        if (weekStartsSunday && dayOfWeek != 1) {
            c2.set(5, 7 - dayOfWeek + 1);
        } else if (dayOfWeek != 2) {
            c2.set(5, 7 - dayOfWeek + 1 + 1);
        }

        long diff = c.getTime().getTime() - c2.getTime().getTime();
        int w = (int)(diff / 604800000L);
        if (jan1midweek && 7 - dayOfWeek >= 4) {
            w++;
        }

        return w;
    }

    static {
        funcnames[0] = "date";
        funcnames[1] = "difftime";
        funcnames[2] = "time";

        for (int i = 0; i < 3; i++) {
            funcs[i] = new OsLib(i);
        }

        MILLISECOND = "milli";
        tzone = TimeZone.getTimeZone("UTC");
        shortDayNames = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        longDayNames = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        shortMonthNames = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        longMonthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    }
}
