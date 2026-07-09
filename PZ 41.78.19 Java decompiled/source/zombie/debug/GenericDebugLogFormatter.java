// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.debug;

class GenericDebugLogFormatter implements IDebugLogFormatter {
    private final DebugType debugType;

    public GenericDebugLogFormatter(DebugType debugTypex) {
        this.debugType = debugTypex;
    }

    @Override
    public boolean isLogEnabled(LogSeverity logSeverity) {
        return DebugLog.isLogEnabled(logSeverity, this.debugType);
    }

    @Override
    public boolean isLogSeverityEnabled(LogSeverity logSeverity) {
        return DebugLog.isLogEnabled(this.debugType, logSeverity);
    }

    @Override
    public String format(LogSeverity logSeverity, String string0, String string1, String string2) {
        return DebugLog.formatString(this.debugType, logSeverity, string0, string1, string2);
    }

    @Override
    public String format(LogSeverity logSeverity, String string0, String string1, String string2, Object object) {
        return DebugLog.formatString(this.debugType, logSeverity, string0, string1, string2, object);
    }

    @Override
    public String format(LogSeverity logSeverity, String string0, String string1, String string2, Object object0, Object object1) {
        return DebugLog.formatString(this.debugType, logSeverity, string0, string1, string2, object0, object1);
    }

    @Override
    public String format(LogSeverity logSeverity, String string0, String string1, String string2, Object object0, Object object1, Object object2) {
        return DebugLog.formatString(this.debugType, logSeverity, string0, string1, string2, object0, object1, object2);
    }

    @Override
    public String format(
        LogSeverity logSeverity, String string0, String string1, String string2, Object object0, Object object1, Object object2, Object object3
    ) {
        return DebugLog.formatString(this.debugType, logSeverity, string0, string1, string2, object0, object1, object2, object3);
    }

    @Override
    public String format(
        LogSeverity logSeverity, String string0, String string1, String string2, Object object0, Object object1, Object object2, Object object3, Object object4
    ) {
        return DebugLog.formatString(this.debugType, logSeverity, string0, string1, string2, object0, object1, object2, object3, object4);
    }

    @Override
    public String format(
        LogSeverity logSeverity,
        String string0,
        String string1,
        String string2,
        Object object0,
        Object object1,
        Object object2,
        Object object3,
        Object object4,
        Object object5
    ) {
        return DebugLog.formatString(this.debugType, logSeverity, string0, string1, string2, object0, object1, object2, object3, object4, object5);
    }

    @Override
    public String format(
        LogSeverity logSeverity,
        String string0,
        String string1,
        String string2,
        Object object0,
        Object object1,
        Object object2,
        Object object3,
        Object object4,
        Object object5,
        Object object6
    ) {
        return DebugLog.formatString(this.debugType, logSeverity, string0, string1, string2, object0, object1, object2, object3, object4, object5, object6);
    }

    @Override
    public String format(
        LogSeverity logSeverity,
        String string0,
        String string1,
        String string2,
        Object object0,
        Object object1,
        Object object2,
        Object object3,
        Object object4,
        Object object5,
        Object object6,
        Object object7
    ) {
        return DebugLog.formatString(
            this.debugType, logSeverity, string0, string1, string2, object0, object1, object2, object3, object4, object5, object6, object7
        );
    }

    @Override
    public String format(
        LogSeverity logSeverity,
        String string0,
        String string1,
        String string2,
        Object object0,
        Object object1,
        Object object2,
        Object object3,
        Object object4,
        Object object5,
        Object object6,
        Object object7,
        Object object8
    ) {
        return DebugLog.formatString(
            this.debugType, logSeverity, string0, string1, string2, object0, object1, object2, object3, object4, object5, object6, object7, object8
        );
    }
}
