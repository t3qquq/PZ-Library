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
import zombie.core.znet.SteamGameServer;
import zombie.core.znet.SteamUtils;
import zombie.network.CoopSlave;
import zombie.network.GameServer;
import zombie.network.ServerOptions;

@CommandName(
    name = "changeoption"
)
@CommandArgs(
    required = {"(\\w+)", "(.*)"}
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_ChangeOptions"
)
@RequiredRight(
    requiredRights = 32
)
public class ChangeOptionCommand extends CommandBase {
    public ChangeOptionCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() throws SQLException {
        String string0 = this.getCommandArg(0);
        String string1 = this.getCommandArg(1);
        String string2 = ServerOptions.instance.changeOption(string0, string1);
        if (string0.equals("Password")) {
            GameServer.udpEngine.SetServerPassword(GameServer.udpEngine.hashServerPassword(ServerOptions.instance.Password.getValue()));
        }

        if (string0.equals("ClientCommandFilter")) {
            GameServer.initClientCommandFilter();
        }

        if (SteamUtils.isSteamModeEnabled()) {
            SteamGameServer.SetServerName(ServerOptions.instance.PublicName.getValue());
            SteamGameServer.SetKeyValue("description", ServerOptions.instance.PublicDescription.getValue());
            SteamGameServer.SetKeyValue("open", ServerOptions.instance.Open.getValue() ? "1" : "0");
            SteamGameServer.SetKeyValue("public", ServerOptions.instance.Public.getValue() ? "1" : "0");
            SteamGameServer.SetKeyValue("mods", ServerOptions.instance.Mods.getValue());
            SteamGameServer.SetKeyValue("pvp", ServerOptions.instance.PVP.getValue() ? "1" : "0");
            if (ServerOptions.instance.Public.getValue()) {
                SteamGameServer.SetGameTags(CoopSlave.instance != null ? "hosted" : "");
            } else {
                SteamGameServer.SetGameTags("hidden" + (CoopSlave.instance != null ? ";hosted" : ""));
            }
        }

        LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " changed option " + string0 + "=" + string1);
        return string2;
    }
}
