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
import zombie.core.raknet.VoiceManager;
import zombie.network.GameServer;

@CommandName(
    name = "voiceban"
)
@AltCommandArgs({@CommandArgs(
        required = {"(.+)"},
        optional = "(-true|-false)"
    ), @CommandArgs(
        optional = "(-true|-false)"
    )})
@CommandHelp(
    helpText = "UI_ServerOptionDesc_VoiceBan"
)
@RequiredRight(
    requiredRights = 48
)
public class VoiceBanCommand extends CommandBase {
    public VoiceBanCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        String string = this.getExecutorUsername();
        if (this.getCommandArgsCount() == 2
            || this.getCommandArgsCount() == 1 && !this.getCommandArg(0).equals("-true") && !this.getCommandArg(0).equals("-false")) {
            string = this.getCommandArg(0);
        }

        boolean boolean0 = true;
        if (this.getCommandArgsCount() > 0) {
            boolean0 = !this.getCommandArg(this.getCommandArgsCount() - 1).equals("-false");
        }

        IsoPlayer player = GameServer.getPlayerByUserNameForCommand(string);
        if (player != null) {
            string = player.getDisplayName();
            VoiceManager.instance.VMServerBan(player.OnlineID, boolean0);
            if (boolean0) {
                LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " ban voice " + string);
                return "User " + string + " voice is banned.";
            } else {
                LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " unban voice " + string);
                return "User " + string + " voice is unbanned.";
            }
        } else {
            return "User " + string + " not found.";
        }
    }
}
