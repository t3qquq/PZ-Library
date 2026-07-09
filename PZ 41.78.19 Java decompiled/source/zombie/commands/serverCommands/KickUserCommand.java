// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

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
import zombie.network.ServerOptions;
import zombie.network.ServerWorldDatabase;
import zombie.network.Userlog;

@CommandNames({@CommandName(
        name = "kick"
    ), @CommandName(
        name = "kickuser"
    )})
@AltCommandArgs({@CommandArgs(
        required = {"(.+)"}
    ), @CommandArgs(
        required = {"(.+)", "-r", "(.+)"}
    )})
@CommandHelp(
    helpText = "UI_ServerOptionDesc_Kick"
)
@RequiredRight(
    requiredRights = 56
)
public class KickUserCommand extends CommandBase {
    private String reason = "";

    public KickUserCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        String string = this.getCommandArg(0);
        if (this.hasOptionalArg(1)) {
            this.reason = this.getCommandArg(1);
        }

        LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " kicked user " + string);
        ServerWorldDatabase.instance.addUserlog(string, Userlog.UserlogType.Kicked, this.reason, this.getExecutorUsername(), 1);
        boolean boolean0 = false;

        for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
            UdpConnection udpConnection = GameServer.udpEngine.connections.get(int0);

            for (int int1 = 0; int1 < 4; int1++) {
                if (string.equals(udpConnection.usernames[int1])) {
                    boolean0 = true;
                    if ("".equals(this.reason)) {
                        GameServer.kick(udpConnection, "UI_Policy_Kick", null);
                    } else {
                        GameServer.kick(udpConnection, "You have been kicked from this server for the following reason: " + this.reason, null);
                    }

                    udpConnection.forceDisconnect("command-kick");
                    GameServer.addDisconnect(udpConnection);
                    break;
                }
            }
        }

        if (boolean0 && ServerOptions.instance.BanKickGlobalSound.getValue()) {
            GameServer.PlaySoundAtEveryPlayer("RumbleThunder");
        }

        return boolean0 ? "User " + string + " kicked." : "User " + string + " doesn't exist.";
    }
}
