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
import zombie.network.ServerWorldDatabase;

@CommandName(
    name = "removeuserfromwhitelist"
)
@CommandArgs(
    required = {"(.+)"}
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_RemoveWhitelist"
)
@RequiredRight(
    requiredRights = 48
)
public class RemoveUserFromWhiteList extends CommandBase {
    public RemoveUserFromWhiteList(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() throws SQLException {
        String string = this.getCommandArg(0);
        LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " removed user " + string + " from whitelist");
        return ServerWorldDatabase.instance.removeUser(string);
    }
}
