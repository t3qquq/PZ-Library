// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;
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
import zombie.network.packets.AddXp;

@CommandName(
    name = "addxp"
)
@CommandArgs(
    required = {"(.+)", "(\\S+)"}
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_AddXp"
)
@RequiredRight(
    requiredRights = 60
)
public class AddXPCommand extends CommandBase {
    public AddXPCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        String string0 = this.getCommandArg(0);
        String string1 = this.getCommandArg(1);
        IsoPlayer player0 = GameServer.getPlayerByUserNameForCommand(string0);
        if (player0 == null) {
            return "No such user";
        } else {
            String string2 = player0.getDisplayName();
            Object object = null;
            int int0 = 0;
            String[] strings = string1.split("=", 2);
            if (strings.length != 2) {
                return this.getHelp();
            } else {
                object = strings[0].trim();
                if (PerkFactory.Perks.FromString((String)object) == PerkFactory.Perks.MAX) {
                    String string3 = this.connection == null ? "\n" : " LINE ";
                    StringBuilder stringBuilder = new StringBuilder();

                    for (int int1 = 0; int1 < PerkFactory.PerkList.size(); int1++) {
                        if (PerkFactory.PerkList.get(int1) != PerkFactory.Perks.Passiv) {
                            stringBuilder.append(PerkFactory.PerkList.get(int1));
                            if (int1 < PerkFactory.PerkList.size()) {
                                stringBuilder.append(string3);
                            }
                        }
                    }

                    return "List of available perks :" + string3 + stringBuilder.toString();
                } else {
                    try {
                        int0 = Integer.parseInt(strings[1]);
                    } catch (NumberFormatException numberFormatException) {
                        return this.getHelp();
                    }

                    IsoPlayer player1 = GameServer.getPlayerByUserNameForCommand(string2);
                    if (player1 != null) {
                        string2 = player1.getDisplayName();
                        UdpConnection udpConnection = GameServer.getConnectionFromPlayer(player1);
                        if (udpConnection != null) {
                            AddXp addXp = new AddXp();
                            addXp.set(player0, PerkFactory.Perks.FromString((String)object), int0);
                            ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
                            PacketTypes.PacketType.AddXP.doPacket(byteBufferWriter);
                            addXp.write(byteBufferWriter);
                            PacketTypes.PacketType.AddXP.send(udpConnection);
                            LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " added " + int0 + " " + object + " xp's to " + string2);
                            return "Added " + int0 + " " + object + " xp's to " + string2;
                        }
                    }

                    return "User " + string2 + " not found.";
                }
            }
        }
    }
}
