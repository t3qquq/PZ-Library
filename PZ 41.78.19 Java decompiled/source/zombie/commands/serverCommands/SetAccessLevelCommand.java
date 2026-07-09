// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import java.sql.SQLException;
import zombie.characters.IsoPlayer;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.PlayerType;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.network.ServerWorldDatabase;
import zombie.network.chat.ChatServer;

@CommandName(
    name = "setaccesslevel"
)
@CommandArgs(
    required = {"(.+)", "(\\w+)"}
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_SetAccessLevel"
)
@RequiredRight(
    requiredRights = 48
)
public class SetAccessLevelCommand extends CommandBase {
    public SetAccessLevelCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() throws SQLException {
        String string0 = this.getCommandArg(0);
        String string1 = "none".equals(this.getCommandArg(1)) ? "" : this.getCommandArg(1);
        return update(this.getExecutorUsername(), this.connection, string0, string1);
    }

    static String update(String string2, UdpConnection udpConnection0, String string0, String string1) throws SQLException {
        if ((udpConnection0 == null || !udpConnection0.isCoopHost) && !ServerWorldDatabase.instance.containsUser(string0) && udpConnection0 != null) {
            return "User \"" + string0 + "\" is not in the whitelist, use /adduser first";
        } else {
            IsoPlayer player = GameServer.getPlayerByUserName(string0);
            byte byte0 = PlayerType.fromString(string1.trim().toLowerCase());
            if (udpConnection0 != null && udpConnection0.accessLevel == 16 && byte0 == 32) {
                return "Moderators can't set Admin access level";
            } else if (byte0 == 0) {
                return "Access Level '" + byte0 + "' unknown, list of access level: player, admin, moderator, overseer, gm, observer";
            } else {
                if (player != null) {
                    if (player.networkAI != null) {
                        player.networkAI.setCheckAccessLevelDelay(5000L);
                    }

                    UdpConnection udpConnection1 = GameServer.getConnectionFromPlayer(player);
                    byte byte1;
                    if (udpConnection1 != null) {
                        byte1 = udpConnection1.accessLevel;
                    } else {
                        byte1 = PlayerType.fromString(player.accessLevel.toLowerCase());
                    }

                    if (byte1 != byte0) {
                        if (byte0 == 32) {
                            ChatServer.getInstance().joinAdminChat(player.OnlineID);
                        } else if (byte1 == 32 && byte0 != 32) {
                            ChatServer.getInstance().leaveAdminChat(player.OnlineID);
                        }
                    }

                    if (byte1 != 1 && byte0 == 1) {
                        player.setGhostMode(false);
                        player.setNoClip(false);
                    }

                    player.accessLevel = PlayerType.toString(byte0);
                    if (udpConnection1 != null) {
                        udpConnection1.accessLevel = byte0;
                    }

                    if ((byte0 & 62) != 0) {
                        player.setGodMod(true);
                        player.setGhostMode(true);
                        player.setInvisible(true);
                    } else {
                        player.setGodMod(false);
                        player.setGhostMode(false);
                        player.setInvisible(false);
                    }

                    GameServer.sendPlayerExtraInfo(player, null);
                }

                LoggerManager.getLogger("admin").write(string2 + " granted " + byte0 + " access level on " + string0);
                return udpConnection0 != null && udpConnection0.isCoopHost
                    ? "Your access level is now: " + byte0
                    : ServerWorldDatabase.instance.setAccessLevel(string0, PlayerType.toString(byte0));
            }
        }
    }
}
