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
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.network.PacketTypes;

@CommandNames({@CommandName(
        name = "teleport"
    ), @CommandName(
        name = "tp"
    )})
@AltCommandArgs({@CommandArgs(
        required = {"(.+)"},
        argName = "just port to user"
    ), @CommandArgs(
        required = {"(.+)", "(.+)"},
        argName = "teleport user1 to user 2"
    )})
@CommandHelp(
    helpText = "UI_ServerOptionDesc_Teleport"
)
@RequiredRight(
    requiredRights = 62
)
public class TeleportCommand extends CommandBase {
    public static final String justToUser = "just port to user";
    public static final String portUserToUser = "teleport user1 to user 2";
    private String username1;
    private String username2;

    public TeleportCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        String string = this.argsName;
        switch (string) {
            case "just port to user":
                this.username1 = this.getCommandArg(0);
                return this.TeleportMeToUser();
            case "teleport user1 to user 2":
                this.username1 = this.getCommandArg(0);
                this.username2 = this.getCommandArg(1);
                return this.TeleportUser1ToUser2();
            default:
                return this.CommandArgumentsNotMatch();
        }
    }

    private String TeleportMeToUser() {
        if (this.connection == null) {
            return "Need player to teleport to, ex /teleport user1 user2";
        } else {
            IsoPlayer player = GameServer.getPlayerByUserNameForCommand(this.username1);
            if (player != null) {
                this.username1 = player.getDisplayName();
                ByteBufferWriter byteBufferWriter = this.connection.startPacket();
                PacketTypes.PacketType.Teleport.doPacket(byteBufferWriter);
                byteBufferWriter.putByte((byte)0);
                byteBufferWriter.putFloat(player.getX());
                byteBufferWriter.putFloat(player.getY());
                byteBufferWriter.putFloat(player.getZ());
                PacketTypes.PacketType.Teleport.send(this.connection);
                if (this.connection.players[0] != null && this.connection.players[0].getNetworkCharacterAI() != null) {
                    this.connection.players[0].getNetworkCharacterAI().resetSpeedLimiter();
                }

                LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " teleport to " + this.username1);
                return "teleported to " + this.username1 + " please wait two seconds to show the map around you.";
            } else {
                return "Can't find player " + this.username1;
            }
        }
    }

    private String TeleportUser1ToUser2() {
        if (this.getAccessLevel() == 2 && !this.username1.equals(this.getExecutorUsername())) {
            return "An Observer can only teleport himself";
        } else {
            IsoPlayer player0 = GameServer.getPlayerByUserNameForCommand(this.username1);
            IsoPlayer player1 = GameServer.getPlayerByUserNameForCommand(this.username2);
            if (player0 == null) {
                return "Can't find player " + this.username1;
            } else if (player1 == null) {
                return "Can't find player " + this.username2;
            } else {
                this.username1 = player0.getDisplayName();
                this.username2 = player1.getDisplayName();
                UdpConnection udpConnection = GameServer.getConnectionFromPlayer(player0);
                if (udpConnection == null) {
                    return "No connection for player " + this.username1;
                } else {
                    ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
                    PacketTypes.PacketType.Teleport.doPacket(byteBufferWriter);
                    byteBufferWriter.putByte((byte)player0.PlayerIndex);
                    byteBufferWriter.putFloat(player1.getX());
                    byteBufferWriter.putFloat(player1.getY());
                    byteBufferWriter.putFloat(player1.getZ());
                    PacketTypes.PacketType.Teleport.send(udpConnection);
                    if (player0.getNetworkCharacterAI() != null) {
                        player0.getNetworkCharacterAI().resetSpeedLimiter();
                    }

                    LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " teleported " + this.username1 + " to " + this.username2);
                    return "teleported " + this.username1 + " to " + this.username2;
                }
            }
        }
    }

    private String CommandArgumentsNotMatch() {
        return this.getHelp();
    }
}
