// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import java.util.TreeMap;
import java.util.Map.Entry;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.network.ServerOptions;

@CommandName(
    name = "help"
)
@CommandArgs(
    optional = "(\\w+)"
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_Help"
)
@RequiredRight(
    requiredRights = 32
)
public class HelpCommand extends CommandBase {
    public HelpCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        String string0 = this.getCommandArg(0);
        if (string0 != null) {
            Class clazz0 = findCommandCls(string0);
            return clazz0 != null ? getHelp(clazz0) : "Unknown command /" + string0;
        } else {
            String string1 = " <LINE> ";
            StringBuilder stringBuilder = new StringBuilder();
            if (this.connection == null) {
                string1 = "\n";
            }

            if (!GameServer.bServer) {
                for (String string2 : ServerOptions.getClientCommandList(this.connection != null)) {
                    stringBuilder.append(string2);
                }
            }

            stringBuilder.append("List of ").append("server").append(" commands : ");
            String string3 = "";
            TreeMap treeMap = new TreeMap();

            for (Class clazz1 : getSubClasses()) {
                if (!isDisabled(clazz1)) {
                    string3 = getHelp(clazz1);
                    if (string3 != null) {
                        treeMap.put(getCommandName(clazz1), string3);
                    }
                }
            }

            for (Entry entry : treeMap.entrySet()) {
                stringBuilder.append(string1).append("* ").append((String)entry.getKey()).append(" : ").append((String)entry.getValue());
            }

            return stringBuilder.toString();
        }
    }
}
