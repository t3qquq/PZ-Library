// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.commands.AltCommandArgs;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;

@CommandName(
    name = "invisible"
)
@AltCommandArgs({@CommandArgs(
        required = {"(.+)"},
        optional = "(-true|-false)"
    ), @CommandArgs(
        optional = "(-true|-false)"
    )})
@CommandHelp(
    helpText = "UI_ServerOptionDesc_Invisible"
)
@RequiredRight(
    requiredRights = 62
)
public class InvisibleCommand extends CommandBase {
    public InvisibleCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        String string0 = this.getExecutorUsername();
        String string1 = this.getCommandArg(0);
        String string2 = this.getCommandArg(1);
        if (this.getCommandArgsCount() == 2 || this.getCommandArgsCount() == 1 && !string1.equals("-true") && !string1.equals("-false")) {
            string0 = string1;
            if (this.connection.accessLevel == 2 && !string1.equals(this.getExecutorUsername())) {
                return "An Observer can only toggle invisible on himself";
            }
        }

        boolean boolean0 = false;
        boolean boolean1 = true;
        if ("-false".equals(string2)) {
            boolean1 = false;
            boolean0 = true;
        } else if ("-true".equals(string2)) {
            boolean0 = true;
        }

        IsoPlayer player = GameServer.getPlayerByUserNameForCommand(string0);
        if (player != null) {
            if (!boolean0) {
                boolean1 = !player.isInvisible();
            }

            string0 = player.getDisplayName();
            if (boolean0) {
                player.setInvisible(boolean1);
            } else {
                player.setInvisible(!player.isInvisible());
                boolean1 = player.isInvisible();
            }

            player.setGhostMode(boolean1);
            GameServer.sendPlayerExtraInfo(player, this.connection);
            if (boolean1) {
                LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " enabled invisibility on " + string0);
                return "User " + string0 + " is now invisible.";
            } else {
                LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " disabled invisibility on " + string0);
                return "User " + string0 + " is no more invisible.";
            }
        } else {
            return "User " + string0 + " not found.";
        }
    }
}
