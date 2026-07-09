// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import java.sql.SQLException;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.DisabledCommand;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.network.ServerWorldDatabase;

@DisabledCommand
@CommandName(
    name = "addusertowhitelist"
)
@CommandArgs(
    required = {"(.+)"}
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_AddWhitelist"
)
@RequiredRight(
    requiredRights = 48
)
public class AddUserToWhiteListCommand extends CommandBase {
    public AddUserToWhiteListCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() throws SQLException {
        String string = this.getCommandArg(0);
        if (!ServerWorldDatabase.isValidUserName(string)) {
            return "Invalid username \"" + string + "\"";
        } else {
            for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
                UdpConnection udpConnection = GameServer.udpEngine.connections.get(int0);
                if (udpConnection.username.equals(string)) {
                    if (udpConnection.password != null && !udpConnection.password.equals("")) {
                        LoggerManager.getLogger("admin")
                            .write(this.getExecutorUsername() + " created user " + udpConnection.username + " with password " + udpConnection.password);
                        return ServerWorldDatabase.instance.addUser(udpConnection.username, udpConnection.password);
                    }

                    return "User " + string + " doesn't have a password.";
                }
            }

            return "User " + string + " not found.";
        }
    }
}
