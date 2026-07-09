// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import java.sql.SQLException;
import zombie.commands.AltCommandArgs;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.core.znet.SteamUtils;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.network.ServerWorldDatabase;
import zombie.network.Userlog;

@CommandName(
    name = "banuser"
)
@AltCommandArgs({@CommandArgs(
        required = {"(.+)"},
        argName = "Ban User Only"
    ), @CommandArgs(
        required = {"(.+)", "-ip"},
        argName = "Ban User And IP"
    ), @CommandArgs(
        required = {"(.+)", "-r", "(.+)"},
        argName = "Ban User And Supply Reason"
    ), @CommandArgs(
        required = {"(.+)", "-ip", "-r", "(.+)"},
        argName = "Ban User And IP And Supply Reason"
    )})
@CommandHelp(
    helpText = "UI_ServerOptionDesc_BanUser"
)
@RequiredRight(
    requiredRights = 48
)
public class BanUserCommand extends CommandBase {
    private String reason = "";
    public static final String banUser = "Ban User Only";
    public static final String banWithIP = "Ban User And IP";
    public static final String banWithReason = "Ban User And Supply Reason";
    public static final String banWithReasonIP = "Ban User And IP And Supply Reason";

    public BanUserCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() throws SQLException {
        String string0 = this.getCommandArg(0);
        if (this.hasOptionalArg(1)) {
            this.reason = this.getCommandArg(1);
        }

        boolean boolean0 = false;
        String string1 = this.argsName;
        switch (string1) {
            case "Ban User And IP":
            case "Ban User And IP And Supply Reason":
                boolean0 = true;
            default:
                string1 = ServerWorldDatabase.instance.banUser(string0, true);
                ServerWorldDatabase.instance.addUserlog(string0, Userlog.UserlogType.Banned, this.reason, this.getExecutorUsername(), 1);
                LoggerManager.getLogger("admin")
                    .write(this.getExecutorUsername() + " banned user " + string0 + (this.reason != null ? this.reason : ""), "IMPORTANT");
                boolean boolean1 = false;

                for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
                    UdpConnection udpConnection = GameServer.udpEngine.connections.get(int0);
                    if (udpConnection.username.equals(string0)) {
                        boolean1 = true;
                        if (SteamUtils.isSteamModeEnabled()) {
                            LoggerManager.getLogger("admin")
                                .write(
                                    this.getExecutorUsername()
                                        + " banned steamid "
                                        + udpConnection.steamID
                                        + "("
                                        + udpConnection.username
                                        + ")"
                                        + (this.reason != null ? this.reason : ""),
                                    "IMPORTANT"
                                );
                            String string2 = SteamUtils.convertSteamIDToString(udpConnection.steamID);
                            ServerWorldDatabase.instance.banSteamID(string2, this.reason, true);
                        }

                        if (boolean0) {
                            LoggerManager.getLogger("admin")
                                .write(
                                    this.getExecutorUsername()
                                        + " banned ip "
                                        + udpConnection.ip
                                        + "("
                                        + udpConnection.username
                                        + ")"
                                        + (this.reason != null ? this.reason : ""),
                                    "IMPORTANT"
                                );
                            ServerWorldDatabase.instance.banIp(udpConnection.ip, string0, this.reason, true);
                        }

                        if ("".equals(this.reason)) {
                            GameServer.kick(udpConnection, "UI_Policy_Ban", null);
                        } else {
                            GameServer.kick(udpConnection, "You have been banned from this server for the following reason: " + this.reason, null);
                        }

                        udpConnection.forceDisconnect("command-ban-ip");
                        break;
                    }
                }

                if (boolean1 && ServerOptions.instance.BanKickGlobalSound.getValue()) {
                    GameServer.PlaySoundAtEveryPlayer("Thunder");
                }

                return string1;
        }
    }
}
