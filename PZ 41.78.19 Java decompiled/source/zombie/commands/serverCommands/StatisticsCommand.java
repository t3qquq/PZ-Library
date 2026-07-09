// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import zombie.commands.AltCommandArgs;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;
import zombie.network.MPStatistic;

@CommandName(
    name = "stats"
)
@AltCommandArgs({@CommandArgs(
        required = {"(none|file|console|all)"},
        optional = "(\\d+)"
    ), @CommandArgs(
        optional = "(\\d+)"
    )})
@CommandHelp(
    helpText = "UI_ServerOptionDesc_SetStatisticsPeriod"
)
@RequiredRight(
    requiredRights = 32
)
public class StatisticsCommand extends CommandBase {
    public StatisticsCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        if (this.getCommandArgsCount() != 1 && this.getCommandArgsCount() != 2) {
            return this.getHelp();
        } else {
            try {
                String string = this.getCommandArg(0);
                boolean boolean0;
                boolean boolean1;
                switch (string) {
                    case "none":
                        boolean0 = false;
                        boolean1 = false;
                        break;
                    case "all":
                        boolean0 = true;
                        boolean1 = true;
                        break;
                    case "file":
                        boolean0 = true;
                        boolean1 = false;
                        break;
                    case "console":
                        boolean0 = false;
                        boolean1 = true;
                        break;
                    default:
                        return this.getHelp();
                }

                int int0 = this.getCommandArgsCount() == 2 ? Integer.parseInt(this.getCommandArg(1)) : 10;
                if (int0 < 1) {
                    return this.getHelp();
                } else {
                    if (!boolean0 && !boolean1) {
                        int0 = 0;
                    }

                    MPStatistic.getInstance().writeEnabled(boolean0);
                    MPStatistic.getInstance().printEnabled(boolean1);
                    MPStatistic.getInstance().setPeriod(int0);
                    return "Server statistics has been cleared and file is set to "
                        + boolean0
                        + " and console is set to "
                        + boolean1
                        + " and period is set to "
                        + int0;
                }
            } catch (Exception exception) {
                return this.getHelp();
            }
        }
    }
}
