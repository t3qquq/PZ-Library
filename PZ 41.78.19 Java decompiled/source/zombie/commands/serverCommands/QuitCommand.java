// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.network.ServerMap;

@CommandName(
    name = "quit"
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_Quit"
)
@RequiredRight(
    requiredRights = 32
)
public class QuitCommand extends CommandBase {
    public QuitCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        DebugLog.Multiplayer.debugln(this.description);
        ServerMap.instance.QueueSaveAll();
        ServerMap.instance.QueueQuit();
        LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " closed server");
        return "Quit";
    }
}
