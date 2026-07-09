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
import zombie.core.secure.PZcrypt;
import zombie.network.ServerWorldDatabase;

@CommandName(
    name = "adduser"
)
@CommandArgs(
    required = {"(.+)", "(.+)"}
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_AddUser"
)
@RequiredRight(
    requiredRights = 48
)
public class AddUserCommand extends CommandBase {
    public AddUserCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        String string0 = this.getCommandArg(0);
        String string1 = PZcrypt.hash(ServerWorldDatabase.encrypt(this.getCommandArg(1)));
        if (!ServerWorldDatabase.isValidUserName(string0)) {
            return "Invalid username \"" + string0 + "\"";
        } else {
            LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " created user " + string0.trim() + " with password " + string1.trim());

            try {
                return ServerWorldDatabase.instance.addUser(string0.trim(), string1.trim());
            } catch (SQLException sQLException) {
                sQLException.printStackTrace();
                return "exception occurs";
            }
        }
    }
}
