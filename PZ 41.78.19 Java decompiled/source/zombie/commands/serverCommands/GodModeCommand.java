// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.commands.AltCommandArgs;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.CommandNames;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;

@CommandNames({@CommandName(
        name = "godmod"
    ), @CommandName(
        name = "godmode"
    )})
@AltCommandArgs({@CommandArgs(
        required = {"(.+)"},
        optional = "(-true|-false)"
    ), @CommandArgs(
        optional = "(-true|-false)"
    )})
@CommandHelp(
    helpText = "UI_ServerOptionDesc_GodMod"
)
@RequiredRight(
    requiredRights = 62
)
public class GodModeCommand extends CommandBase {
    public GodModeCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        String string0 = this.getExecutorUsername();
        String string1 = this.getCommandArg(0);
        String string2 = this.getCommandArg(1);
        if (this.getCommandArgsCount() == 2 || this.getCommandArgsCount() == 1 && !string1.equals("-true") && !string1.equals("-false")) {
            string0 = string1;
            if (this.connection != null && this.connection.accessLevel == 2 && !string1.equals(string1)) {
                return "An Observer can only toggle god mode on himself";
            }
        }

        IsoPlayer player = GameServer.getPlayerByUserNameForCommand(string0);
        if (player != null) {
            string0 = player.getDisplayName();
            if (string2 != null) {
                player.setGodMod("-true".equals(string2));
            } else {
                player.setGodMod(!player.isGodMod());
            }

            GameServer.sendPlayerExtraInfo(player, this.connection);
            if (player.isGodMod()) {
                LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " enabled godmode on " + string0);
                return "User " + string0 + " is now invincible.";
            } else {
                LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " disabled godmode on " + string0);
                return "User " + string0 + " is no more invincible.";
            }
        } else {
            return "User " + string0 + " not found.";
        }
    }
}
