// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.iso.weather.ClimateManager;

@CommandName(
    name = "stoprain"
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_StopRain"
)
@RequiredRight(
    requiredRights = 60
)
public class StopRainCommand extends CommandBase {
    public StopRainCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        ClimateManager.getInstance().transmitServerStopRain();
        LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " stopped rain");
        return "Rain stopped";
    }
}
