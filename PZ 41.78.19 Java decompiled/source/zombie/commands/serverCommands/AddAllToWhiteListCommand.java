// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import java.sql.SQLException;
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
    name = "addalltowhitelist"
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_AddAllWhitelist"
)
@RequiredRight(
    requiredRights = 48
)
public class AddAllToWhiteListCommand extends CommandBase {
    public AddAllToWhiteListCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        StringBuilder stringBuilder = new StringBuilder("");

        for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
            UdpConnection udpConnection = GameServer.udpEngine.connections.get(int0);
            if (udpConnection.password != null && !udpConnection.password.equals("")) {
                LoggerManager.getLogger("admin")
                    .write(this.getExecutorUsername() + " created user " + udpConnection.username + " with password " + udpConnection.password);

                try {
                    stringBuilder.append(ServerWorldDatabase.instance.addUser(udpConnection.username, udpConnection.password)).append(" <LINE> ");
                } catch (SQLException sQLException) {
                    sQLException.printStackTrace();
                }
            } else {
                stringBuilder.append("User ").append(udpConnection.username).append(" doesn't have a password. <LINE> ");
            }
        }

        stringBuilder.append("Done.");
        return stringBuilder.toString();
    }
}
