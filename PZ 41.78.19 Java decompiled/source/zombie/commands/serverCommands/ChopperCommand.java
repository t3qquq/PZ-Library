// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoWorld;

@CommandName(
    name = "chopper"
)
@CommandArgs(
    optional = "([a-zA-Z0-9.-]*[a-zA-Z][a-zA-Z0-9_.-]*)"
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_Chopper"
)
@RequiredRight(
    requiredRights = 60
)
public class ChopperCommand extends CommandBase {
    public ChopperCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        String string;
        if (this.getCommandArgsCount() == 1) {
            if ("stop".equals(this.getCommandArg(0))) {
                IsoWorld.instance.helicopter.deactivate();
                string = "Chopper deactivated";
            } else if ("start".equals(this.getCommandArg(0))) {
                IsoWorld.instance.helicopter.pickRandomTarget();
                string = "Chopper activated";
            } else {
                string = this.getHelp();
            }
        } else {
            IsoWorld.instance.helicopter.pickRandomTarget();
            string = "Chopper launched";
        }

        LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " did chopper");
        return string;
    }
}
