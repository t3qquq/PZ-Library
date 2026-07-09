// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import zombie.AmbientStreamManager;
import zombie.characters.IsoPlayer;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;

@CommandName(
    name = "alarm"
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_Alarm"
)
@RequiredRight(
    requiredRights = 60
)
public class AlarmCommand extends CommandBase {
    public AlarmCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        IsoPlayer player = GameServer.getPlayerByUserName(this.getExecutorUsername());
        if (player != null && player.getSquare() != null && player.getSquare().getBuilding() != null) {
            player.getSquare().getBuilding().getDef().bAlarmed = true;
            AmbientStreamManager.instance.doAlarm(player.getSquare().getRoom().def);
            return "Alarm sounded";
        } else {
            return "Not in a room";
        }
    }
}
