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
import zombie.network.ReplayManager;

@CommandName(
    name = "replay"
)
@AltCommandArgs({@CommandArgs(
        required = {"(.+)", "(-record|-play|-stop)", "(.+)"}
    ), @CommandArgs(
        required = {"(.+)", "(-stop)"}
    )})
@CommandHelp(
    helpText = "UI_ServerOptionDesc_Replay"
)
@RequiredRight(
    requiredRights = 32
)
public class ReplayCommands extends CommandBase {
    public static final String RecordPlay = "(-record|-play|-stop)";
    public static final String Stop = "(-stop)";

    public ReplayCommands(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        String string0 = this.getCommandArg(0);
        String string1 = this.getCommandArg(1);
        String string2 = this.getCommandArg(2);
        boolean boolean0 = false;
        boolean boolean1 = false;
        if ("-play".equals(string1)) {
            boolean1 = true;
        } else if ("-stop".equals(string1)) {
            boolean0 = true;
        }

        IsoPlayer player = GameServer.getPlayerByUserNameForCommand(string0);
        if (player != null) {
            if (player.replay == null) {
                player.replay = new ReplayManager(player);
            }

            if (boolean0) {
                ReplayManager.State state = player.replay.getState();
                if (state == ReplayManager.State.Stop) {
                    return "Nothing to stop.";
                } else if (state == ReplayManager.State.Recording) {
                    player.replay.stopRecordReplay();
                    LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " end record replay for " + string0);
                    return "Recording replay is stopped  for " + string0 + ".";
                } else {
                    player.replay.stopPlayReplay();
                    LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " end play replay for " + string0);
                    return "Playing replay is stopped  for " + string0 + ".";
                }
            } else if (boolean1) {
                if (!player.replay.startPlayReplay(player, string2, this.connection)) {
                    return "Can't play replay";
                } else {
                    LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " enabled play replay for " + string0);
                    return "Replay is playing for " + string0 + " to file \"" + string2 + "\" now.";
                }
            } else if (!player.replay.startRecordReplay(player, string2)) {
                return "Can't record replay";
            } else {
                LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " enabled record replay for " + string0);
                return "Replay is recording for " + string0 + " to file \"" + string2 + "\" now.";
            }
        } else {
            return "User " + string0 + " not found.";
        }
    }
}
