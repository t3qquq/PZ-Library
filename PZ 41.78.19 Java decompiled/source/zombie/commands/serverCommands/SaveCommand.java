// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.network.ServerMap;

@CommandName(
    name = "save"
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_Save"
)
@RequiredRight(
    requiredRights = 32
)
public class SaveCommand extends CommandBase {
    public SaveCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        ServerMap.instance.QueueSaveAll();
        GameServer.PauseAllClients();
        return "World saved";
    }
}
