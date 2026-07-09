// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import zombie.Lua.LuaManager;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.Rand;
import zombie.core.logger.LoggerManager;
import zombie.core.math.PZMath;
import zombie.core.raknet.UdpConnection;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.util.StringUtils;

@CommandName(
    name = "createhorde2"
)
@CommandArgs(
    varArgs = true
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_CreateHorde2"
)
@RequiredRight(
    requiredRights = 56
)
public class CreateHorde2Command extends CommandBase {
    public CreateHorde2Command(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        int int0 = -1;
        int int1 = -1;
        int int2 = -1;
        int int3 = -1;
        int int4 = -1;
        boolean boolean0 = false;
        boolean boolean1 = false;
        boolean boolean2 = false;
        boolean boolean3 = false;
        float float0 = 1.0F;
        String string0 = null;

        for (byte byte0 = 0; byte0 < this.getCommandArgsCount() - 1; byte0 += 2) {
            String string1 = this.getCommandArg(Integer.valueOf(byte0));
            String string2 = this.getCommandArg(byte0 + 1);
            switch (string1) {
                case "-count":
                    int0 = PZMath.tryParseInt(string2, -1);
                    break;
                case "-radius":
                    int1 = PZMath.tryParseInt(string2, -1);
                    break;
                case "-x":
                    int2 = PZMath.tryParseInt(string2, -1);
                    break;
                case "-y":
                    int3 = PZMath.tryParseInt(string2, -1);
                    break;
                case "-z":
                    int4 = PZMath.tryParseInt(string2, -1);
                    break;
                case "-outfit":
                    string0 = StringUtils.discardNullOrWhitespace(string2);
                    break;
                case "-crawler":
                    boolean0 = !"false".equals(string2);
                    break;
                case "-isFallOnFront":
                    boolean1 = !"false".equals(string2);
                    break;
                case "-isFakeDead":
                    boolean2 = !"false".equals(string2);
                    break;
                case "-knockedDown":
                    boolean3 = !"false".equals(string2);
                    break;
                case "-health":
                    float0 = Float.valueOf(string2);
                    break;
                default:
                    return this.getHelp();
            }
        }

        int0 = PZMath.clamp(int0, 1, 500);
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int2, int3, int4);
        if (square == null) {
            return "invalid location";
        } else if (string0 != null && OutfitManager.instance.FindMaleOutfit(string0) == null && OutfitManager.instance.FindFemaleOutfit(string0) == null) {
            return "invalid outfit";
        } else {
            Integer integer = null;
            if (string0 != null) {
                if (OutfitManager.instance.FindFemaleOutfit(string0) == null) {
                    integer = Integer.MIN_VALUE;
                } else if (OutfitManager.instance.FindMaleOutfit(string0) == null) {
                    integer = Integer.MAX_VALUE;
                }
            }

            for (int int5 = 0; int5 < int0; int5++) {
                int int6 = int1 <= 0 ? int2 : Rand.Next(int2 - int1, int2 + int1 + 1);
                int int7 = int1 <= 0 ? int3 : Rand.Next(int3 - int1, int3 + int1 + 1);
                LuaManager.GlobalObject.addZombiesInOutfit(int6, int7, int4, 1, string0, integer, boolean0, boolean1, boolean2, boolean3, float0);
            }

            LoggerManager.getLogger("admin")
                .write(this.getExecutorUsername() + " created a horde of " + int0 + " zombies near " + int2 + "," + int3, "IMPORTANT");
            return "Horde spawned.";
        }
    }
}
