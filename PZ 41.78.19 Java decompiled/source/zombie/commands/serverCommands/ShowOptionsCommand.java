// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import java.util.Iterator;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;
import zombie.network.ServerOptions;

@CommandName(
    name = "showoptions"
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_ShowOptions"
)
@RequiredRight(
    requiredRights = 63
)
public class ShowOptionsCommand extends CommandBase {
    public ShowOptionsCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        Iterator iterator = ServerOptions.instance.getPublicOptions().iterator();
        Object object = null;
        String string0 = " <LINE> ";
        if (this.connection == null) {
            string0 = "\n";
        }

        String string1 = "List of Server Options:" + string0;

        while (iterator.hasNext()) {
            object = (String)iterator.next();
            if (!object.equals("ServerWelcomeMessage")) {
                string1 = string1 + "* " + object + "=" + ServerOptions.instance.getOptionByName((String)object).asConfigOption().getValueAsString() + string0;
            }
        }

        return string1 + "* ServerWelcomeMessage=" + ServerOptions.instance.ServerWelcomeMessage.getValue();
    }
}
