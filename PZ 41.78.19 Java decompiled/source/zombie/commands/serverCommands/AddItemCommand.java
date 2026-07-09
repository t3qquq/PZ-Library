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
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;

@CommandName(
    name = "additem"
)
@AltCommandArgs({@CommandArgs(
        required = {"(.+)", "([a-zA-Z0-9.-]*[a-zA-Z][a-zA-Z0-9_.-]*)"},
        optional = "(\\d+)",
        argName = "add item to player"
    ), @CommandArgs(
        required = {"([a-zA-Z0-9.-]*[a-zA-Z][a-zA-Z0-9_.-]*)"},
        optional = "(\\d+)",
        argName = "add item to me"
    )})
@CommandHelp(
    helpText = "UI_ServerOptionDesc_AddItem"
)
@RequiredRight(
    requiredRights = 60
)
public class AddItemCommand extends CommandBase {
    public static final String toMe = "add item to me";
    public static final String toPlayer = "add item to player";

    public AddItemCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        int int0 = 1;
        if (this.argsName.equals("add item to me") && this.connection == null) {
            return "Pass username";
        } else {
            if (this.getCommandArgsCount() > 1) {
                int int1 = this.getCommandArgsCount();
                if (this.argsName.equals("add item to me") && int1 == 2 || this.argsName.equals("add item to player") && int1 == 3) {
                    int0 = Integer.parseInt(this.getCommandArg(this.getCommandArgsCount() - 1));
                }
            }

            String string0;
            if (this.argsName.equals("add item to player")) {
                IsoPlayer player0 = GameServer.getPlayerByUserNameForCommand(this.getCommandArg(0));
                if (player0 == null) {
                    return "No such user";
                }

                string0 = player0.getDisplayName();
            } else {
                IsoPlayer player1 = GameServer.getPlayerByRealUserName(this.getExecutorUsername());
                if (player1 == null) {
                    return "No such user";
                }

                string0 = player1.getDisplayName();
            }

            String string1;
            if (this.argsName.equals("add item to me")) {
                string1 = this.getCommandArg(0);
            } else {
                string1 = this.getCommandArg(1);
            }

            Item item = ScriptManager.instance.FindItem(string1);
            if (item == null) {
                return "Item " + string1 + " doesn't exist.";
            } else {
                IsoPlayer player2 = GameServer.getPlayerByUserNameForCommand(string0);
                if (player2 != null) {
                    string0 = player2.getDisplayName();
                    UdpConnection udpConnection = GameServer.getConnectionByPlayerOnlineID(player2.OnlineID);
                    if (udpConnection != null) {
                        ByteBufferWriter byteBufferWriter0 = udpConnection.startPacket();
                        PacketTypes.PacketType.AddItemInInventory.doPacket(byteBufferWriter0);
                        byteBufferWriter0.putShort(player2.OnlineID);
                        byteBufferWriter0.putUTF(string1);
                        byteBufferWriter0.putInt(int0);
                        PacketTypes.PacketType.AddItemInInventory.send(udpConnection);
                        LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " added item " + string1 + " in " + string0 + "'s inventory");
                        ByteBufferWriter byteBufferWriter1 = udpConnection.startPacket();
                        PacketTypes.PacketType.RequestInventory.doPacket(byteBufferWriter1);
                        byteBufferWriter1.putShort(player2.OnlineID);
                        PacketTypes.PacketType.RequestInventory.send(udpConnection);
                        return "Item " + string1 + " Added in " + string0 + "'s inventory.";
                    }
                }

                return "User " + string0 + " not found.";
            }
        }
    }
}
