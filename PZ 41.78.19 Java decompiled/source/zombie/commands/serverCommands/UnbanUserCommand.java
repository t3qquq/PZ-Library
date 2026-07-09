// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import java.sql.SQLException;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.core.znet.SteamUtils;
import zombie.network.GameServer;
import zombie.network.ServerWorldDatabase;

@CommandName(
    name = "unbanuser"
)
@CommandArgs(
    required = {"(.+)"}
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_UnBanUser"
)
@RequiredRight(
    requiredRights = 48
)
public class UnbanUserCommand extends CommandBase {
    public UnbanUserCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() throws SQLException {
        String string0 = this.getCommandArg(0);
        String string1 = ServerWorldDatabase.instance.banUser(string0, false);
        LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " unbanned user " + string0);
        if (!SteamUtils.isSteamModeEnabled()) {
            ServerWorldDatabase.instance.banIp(null, string0, null, false);

            for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
                UdpConnection udpConnection = GameServer.udpEngine.connections.get(int0);
                if (udpConnection.username.equals(string0)) {
                    ServerWorldDatabase.instance.banIp(udpConnection.ip, string0, null, false);
                    break;
                }
            }
        }

        return string1;
    }
}
