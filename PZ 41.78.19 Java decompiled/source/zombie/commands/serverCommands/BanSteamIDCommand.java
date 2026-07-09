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
    name = "banid"
)
@CommandArgs(
    required = {"(.+)"}
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_BanSteamId"
)
@RequiredRight(
    requiredRights = 48
)
public class BanSteamIDCommand extends CommandBase {
    public BanSteamIDCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() throws SQLException {
        String string = this.getCommandArg(0);
        if (!SteamUtils.isSteamModeEnabled()) {
            return "Server is not in Steam mode";
        } else if (!SteamUtils.isValidSteamID(string)) {
            return "Expected SteamID but got \"" + string + "\"";
        } else {
            LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " banned SteamID " + string, "IMPORTANT");
            ServerWorldDatabase.instance.banSteamID(string, "", true);
            long long0 = SteamUtils.convertStringToSteamID(string);

            for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
                UdpConnection udpConnection = GameServer.udpEngine.connections.get(int0);
                if (udpConnection.steamID == long0) {
                    GameServer.kick(udpConnection, "UI_Policy_Ban", null);
                    udpConnection.forceDisconnect("command-ban-sid");
                    break;
                }
            }

            return "SteamID " + string + " is now banned";
        }
    }
}
