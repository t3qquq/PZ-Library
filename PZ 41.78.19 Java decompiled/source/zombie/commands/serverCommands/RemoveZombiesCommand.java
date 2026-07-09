// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import zombie.characters.IsoZombie;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.math.PZMath;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameServer;
import zombie.popman.NetworkZombiePacker;
import zombie.util.StringUtils;
import zombie.util.Type;

@CommandName(
    name = "removezombies"
)
@CommandArgs(
    varArgs = true
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_RemoveZombies"
)
@RequiredRight(
    requiredRights = 56
)
public class RemoveZombiesCommand extends CommandBase {
    public RemoveZombiesCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        int int0 = -1;
        int int1 = -1;
        int int2 = -1;
        int int3 = -1;
        boolean boolean0 = false;
        boolean boolean1 = false;
        boolean boolean2 = false;

        for (byte byte0 = 0; byte0 < this.getCommandArgsCount() - 1; byte0 += 2) {
            String string0 = this.getCommandArg(Integer.valueOf(byte0));
            String string1 = this.getCommandArg(byte0 + 1);
            switch (string0) {
                case "-radius":
                    int0 = PZMath.tryParseInt(string1, -1);
                    break;
                case "-reanimated":
                    boolean0 = StringUtils.tryParseBoolean(string1);
                    break;
                case "-x":
                    int1 = PZMath.tryParseInt(string1, -1);
                    break;
                case "-y":
                    int2 = PZMath.tryParseInt(string1, -1);
                    break;
                case "-z":
                    int3 = PZMath.tryParseInt(string1, -1);
                    break;
                case "-remove":
                    boolean1 = StringUtils.tryParseBoolean(string1);
                    break;
                case "-clear":
                    boolean2 = StringUtils.tryParseBoolean(string1);
                    break;
                default:
                    return this.getHelp();
            }
        }

        if (boolean1) {
            GameServer.removeZombiesConnection = this.connection;
            return "Zombies removed.";
        } else if (int3 >= 0 && int3 < 8) {
            for (int int4 = int2 - int0; int4 <= int2 + int0; int4++) {
                for (int int5 = int1 - int0; int5 <= int1 + int0; int5++) {
                    IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int5, int4, int3);
                    if (square != null) {
                        if (boolean2) {
                            if (!square.getStaticMovingObjects().isEmpty()) {
                                for (int int6 = square.getStaticMovingObjects().size() - 1; int6 >= 0; int6--) {
                                    IsoDeadBody deadBody = Type.tryCastTo(square.getStaticMovingObjects().get(int6), IsoDeadBody.class);
                                    if (deadBody != null) {
                                        GameServer.sendRemoveCorpseFromMap(deadBody);
                                        deadBody.removeFromWorld();
                                        deadBody.removeFromSquare();
                                    }
                                }
                            }
                        } else if (!square.getMovingObjects().isEmpty()) {
                            for (int int7 = square.getMovingObjects().size() - 1; int7 >= 0; int7--) {
                                IsoZombie zombie0 = Type.tryCastTo(square.getMovingObjects().get(int7), IsoZombie.class);
                                if (zombie0 != null && (boolean0 || !zombie0.isReanimatedPlayer())) {
                                    NetworkZombiePacker.getInstance().deleteZombie(zombie0);
                                    zombie0.removeFromWorld();
                                    zombie0.removeFromSquare();
                                }
                            }
                        }
                    }
                }
            }

            LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " removed zombies near " + int1 + "," + int2, "IMPORTANT");
            return "Zombies removed.";
        } else {
            return "invalid z";
        }
    }
}
