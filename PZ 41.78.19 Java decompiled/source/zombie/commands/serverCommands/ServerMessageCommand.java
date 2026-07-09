// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;
import zombie.network.chat.ChatServer;

@CommandName(
    name = "servermsg"
)
@CommandArgs(
    required = {"(.+)"}
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_ServerMsg"
)
@RequiredRight(
    requiredRights = 56
)
public class ServerMessageCommand extends CommandBase {
    public ServerMessageCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        String string0 = this.getCommandArg(0);
        if (this.connection == null) {
            ChatServer.getInstance().sendServerAlertMessageToServerChat(string0);
        } else {
            String string1 = this.getExecutorUsername();
            ChatServer.getInstance().sendServerAlertMessageToServerChat(string1, string0);
        }

        return "Message sent.";
    }
}
