// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.debug;

public interface IDebugLogFormatter {
    boolean isLogEnabled(LogSeverity logSeverity);

    boolean isLogSeverityEnabled(LogSeverity logSeverity);

    String format(LogSeverity logSeverity, String prefix, String affix, String formatNoParams);

    String format(LogSeverity logSeverity, String prefix, String affix, String format, Object param0);

    String format(LogSeverity logSeverity, String prefix, String affix, String format, Object param0, Object param1);

    String format(LogSeverity logSeverity, String prefix, String affix, String format, Object param0, Object param1, Object param2);

    String format(LogSeverity logSeverity, String prefix, String affix, String format, Object param0, Object param1, Object param2, Object param3);

    String format(
        LogSeverity logSeverity, String prefix, String affix, String format, Object param0, Object param1, Object param2, Object param3, Object param4
    );

    String format(
        LogSeverity logSeverity,
        String prefix,
        String affix,
        String format,
        Object param0,
        Object param1,
        Object param2,
        Object param3,
        Object param4,
        Object param5
    );

    String format(
        LogSeverity logSeverity,
        String prefix,
        String affix,
        String format,
        Object param0,
        Object param1,
        Object param2,
        Object param3,
        Object param4,
        Object param5,
        Object param6
    );

    String format(
        LogSeverity logSeverity,
        String prefix,
        String affix,
        String format,
        Object param0,
        Object param1,
        Object param2,
        Object param3,
        Object param4,
        Object param5,
        Object param6,
        Object param7
    );

    String format(
        LogSeverity logSeverity,
        String prefix,
        String affix,
        String format,
        Object param0,
        Object param1,
        Object param2,
        Object param3,
        Object param4,
        Object param5,
        Object param6,
        Object param7,
        Object param8
    );
}
