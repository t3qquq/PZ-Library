// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import java.util.ArrayList;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;

@CommandName(
    name = "players"
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_Players"
)
@RequiredRight(
    requiredRights = 62
)
public class PlayersCommand extends CommandBase {
    public PlayersCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        ArrayList arrayList = new ArrayList();

        for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
            UdpConnection udpConnection = GameServer.udpEngine.connections.get(int0);

            for (int int1 = 0; int1 < 4; int1++) {
                if (udpConnection.usernames[int1] != null) {
                    arrayList.add(udpConnection.usernames[int1]);
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder("Players connected (" + arrayList.size() + "): ");
        String string0 = " <LINE> ";
        if (this.connection == null) {
            string0 = "\n";
        }

        stringBuilder.append(string0);

        for (String string1 : arrayList) {
            stringBuilder.append("-").append(string1).append(string0);
        }

        return stringBuilder.toString();
    }
}
