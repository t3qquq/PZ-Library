// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.ExceptionLogger;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.iso.weather.ClimateManager;

@CommandName(
    name = "startrain"
)
@CommandArgs(
    optional = "(\\d+)"
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_StartRain"
)
@RequiredRight(
    requiredRights = 60
)
public class StartRainCommand extends CommandBase {
    public StartRainCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        float float0 = 1.0F;
        if (this.getCommandArgsCount() == 1) {
            try {
                float0 = Float.parseFloat(this.getCommandArg(0)) / 100.0F;
            } catch (Throwable throwable) {
                ExceptionLogger.logException(throwable);
                return "Invalid intensity value";
            }
        }

        ClimateManager.getInstance().transmitServerStartRain(float0);
        LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " started rain");
        return "Rain started";
    }
}
