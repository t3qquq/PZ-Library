// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import java.sql.SQLException;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;

@CommandName(
    name = "removeadmin"
)
@CommandArgs(
    required = {"(.+)"}
)
@RequiredRight(
    requiredRights = 32
)
public class RemoveAdminCommand extends CommandBase {
    public RemoveAdminCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() throws SQLException {
        return SetAccessLevelCommand.update(this.getExecutorUsername(), this.connection, this.getCommandArg(0), "");
    }
}
