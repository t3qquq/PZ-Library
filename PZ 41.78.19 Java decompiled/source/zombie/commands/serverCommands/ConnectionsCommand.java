// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.CommandNames;
import zombie.commands.DisabledCommand;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;

@DisabledCommand
@CommandNames({@CommandName(
        name = "connections"
    ), @CommandName(
        name = "list"
    )})
@CommandHelp(
    helpText = "UI_ServerOptionDesc_Connections"
)
@RequiredRight(
    requiredRights = 56
)
public class ConnectionsCommand extends CommandBase {
    public ConnectionsCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        String string0 = "";
        String string1 = " <LINE> ";
        if (this.connection == null) {
            string1 = "\n";
        }

        for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
            UdpConnection udpConnection = GameServer.udpEngine.connections.get(int0);

            for (int int1 = 0; int1 < 4; int1++) {
                if (udpConnection.usernames[int1] != null) {
                    string0 = string0
                        + "connection="
                        + (int0 + 1)
                        + "/"
                        + GameServer.udpEngine.connections.size()
                        + " "
                        + udpConnection.idStr
                        + " player="
                        + (int1 + 1)
                        + "/4 id="
                        + udpConnection.playerIDs[int1]
                        + " username=\""
                        + udpConnection.usernames[int1]
                        + "\" fullyConnected="
                        + udpConnection.isFullyConnected()
                        + string1;
                }
            }
        }

        return string0 + "Players listed";
    }
}
