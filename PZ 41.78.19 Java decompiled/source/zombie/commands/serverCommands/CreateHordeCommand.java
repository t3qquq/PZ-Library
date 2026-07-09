// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import zombie.VirtualZombieManager;
import zombie.ZombieSpawnRecorder;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.Rand;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;

@CommandName(
    name = "createhorde"
)
@CommandArgs(
    required = {"(\\d+)"},
    optional = "(.+)"
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_CreateHorde"
)
@RequiredRight(
    requiredRights = 56
)
public class CreateHordeCommand extends CommandBase {
    public CreateHordeCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        Integer integer = Integer.parseInt(this.getCommandArg(0));
        String string = this.getCommandArg(1);
        IsoPlayer player = null;
        if (this.getCommandArgsCount() == 2) {
            player = GameServer.getPlayerByUserNameForCommand(string);
            if (player == null) {
                return "User \"" + string + "\" not found";
            }
        } else if (this.connection != null) {
            player = GameServer.getAnyPlayerFromConnection(this.connection);
        }

        if (integer == null) {
            return this.getHelp();
        } else {
            integer = Math.min(integer, 500);
            if (player != null) {
                for (int int0 = 0; int0 < integer; int0++) {
                    VirtualZombieManager.instance.choices.clear();
                    IsoGridSquare square = IsoWorld.instance
                        .CurrentCell
                        .getGridSquare(
                            (double)Rand.Next(player.getX() - 10.0F, player.getX() + 10.0F),
                            (double)Rand.Next(player.getY() - 10.0F, player.getY() + 10.0F),
                            (double)player.getZ()
                        );
                    VirtualZombieManager.instance.choices.add(square);
                    IsoZombie zombie0 = VirtualZombieManager.instance
                        .createRealZombieAlways(IsoDirections.fromIndex(Rand.Next(IsoDirections.Max.index())).index(), false);
                    if (zombie0 != null) {
                        ZombieSpawnRecorder.instance.record(zombie0, this.getClass().getSimpleName());
                    }
                }

                LoggerManager.getLogger("admin")
                    .write(this.getExecutorUsername() + " created a horde of " + integer + " zombies near " + player.getX() + "," + player.getY(), "IMPORTANT");
                return "Horde spawned.";
            } else {
                return "Specify a player to create the horde near to.";
            }
        }
    }
}
