// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.znet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.debug.LogSeverity;

public class ZNet {
    private static final SimpleDateFormat s_logSdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss.SSS");

    public static native void init();

    private static native void setLogLevel(int var0);

    public static void SetLogLevel(int int0) {
        DebugLog.enableLog(DebugType.Network, switch (int0) {
            case 0 -> LogSeverity.Warning;
            case 1 -> LogSeverity.General;
            case 2 -> LogSeverity.Debug;
            default -> LogSeverity.Error;
        });
    }

    public static void SetLogLevel(LogSeverity logSeverity) {
        setLogLevel(logSeverity.ordinal());
    }

    private static void logPutsCallback(String string1) {
        String string0 = s_logSdf.format(Calendar.getInstance().getTime());
        DebugLog.Network.print("[" + string0 + "] > " + string1);
        System.out.flush();
    }
}
