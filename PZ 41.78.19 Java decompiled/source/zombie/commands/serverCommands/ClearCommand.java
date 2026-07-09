// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import zombie.commands.CommandBase;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;

@CommandName(
    name = "clear"
)
@RequiredRight(
    requiredRights = 32
)
public class ClearCommand extends CommandBase {
    public ClearCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        String string = "Console cleared";
        if (this.connection == null) {
            for (int int0 = 0; int0 < 100; int0++) {
                System.out.println();
            }
        } else {
            StringBuilder stringBuilder = new StringBuilder();

            for (int int1 = 0; int1 < 50; int1++) {
                stringBuilder.append("<LINE>");
            }

            string = stringBuilder.toString() + string;
        }

        return string;
    }
}
