// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;

public class ConnectionManager {
    private static final SimpleDateFormat s_logSdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss.SSS");

    public static void log(String string0, String string1, UdpConnection udpConnection) {
        DebugLog.Network
            .println(
                "[%s] > ConnectionManager: [%s] \"%s\" connection: %s",
                s_logSdf.format(Calendar.getInstance().getTime()),
                string0,
                string1,
                GameClient.bClient ? GameClient.connection : udpConnection
            );
    }
}
