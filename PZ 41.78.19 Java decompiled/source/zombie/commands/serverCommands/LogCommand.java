// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import java.util.ArrayList;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.Translator;
import zombie.core.raknet.UdpConnection;
import zombie.core.znet.ZNet;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.debug.LogSeverity;

@CommandName(
    name = "log"
)
@CommandArgs(
    required = {"(.+)", "(.+)"}
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_SetLogLevel"
)
@RequiredRight(
    requiredRights = 32
)
public class LogCommand extends CommandBase {
    public LogCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    public static DebugType getDebugType(String string) {
        ArrayList arrayList = new ArrayList();

        for (DebugType debugType : DebugType.values()) {
            if (debugType.name().toLowerCase().startsWith(string.toLowerCase())) {
                arrayList.add(debugType);
            }
        }

        return arrayList.size() == 1 ? (DebugType)arrayList.get(0) : null;
    }

    public static LogSeverity getLogSeverity(String string) {
        ArrayList arrayList = new ArrayList();

        for (LogSeverity logSeverity : LogSeverity.values()) {
            if (logSeverity.name().toLowerCase().startsWith(string.toLowerCase())) {
                arrayList.add(logSeverity);
            }
        }

        return arrayList.size() == 1 ? (LogSeverity)arrayList.get(0) : null;
    }

    @Override
    protected String Command() {
        DebugType debugType = getDebugType(this.getCommandArg(0));
        LogSeverity logSeverity = getLogSeverity(this.getCommandArg(1));
        if (debugType != null && logSeverity != null) {
            DebugLog.enableLog(debugType, logSeverity);
            if (DebugType.Network.equals(debugType)) {
                ZNet.SetLogLevel(logSeverity);
            }

            return String.format("Server \"%s\" log level is \"%s\"", debugType.name().toLowerCase(), logSeverity.name().toLowerCase());
        } else {
            return Translator.getText(
                "UI_ServerOptionDesc_SetLogLevel",
                debugType == null ? "\"type\"" : debugType.name().toLowerCase(),
                logSeverity == null ? "\"severity\"" : logSeverity.name().toLowerCase()
            );
        }
    }
}
