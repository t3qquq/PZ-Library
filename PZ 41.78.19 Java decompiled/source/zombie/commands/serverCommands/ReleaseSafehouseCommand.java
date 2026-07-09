// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import java.sql.SQLException;
import zombie.characters.IsoPlayer;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;
import zombie.iso.areas.SafeHouse;
import zombie.network.GameServer;

@CommandName(
    name = "releasesafehouse"
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_SafeHouse"
)
@RequiredRight(
    requiredRights = 63
)
public class ReleaseSafehouseCommand extends CommandBase {
    public ReleaseSafehouseCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() throws SQLException {
        if (this.isCommandComeFromServerConsole()) {
            return getCommandName(this.getClass()) + " can be executed only from the game";
        } else {
            String string = this.getExecutorUsername();
            IsoPlayer player = GameServer.getPlayerByUserNameForCommand(string);
            SafeHouse safeHouse = SafeHouse.hasSafehouse(string);
            if (safeHouse != null) {
                if (!safeHouse.isOwner(player)) {
                    return "Only owner can release safehouse";
                } else {
                    safeHouse.removeSafeHouse(player);
                    return "Your safehouse was released";
                }
            } else {
                return "You have no safehouse";
            }
        }
    }
}
