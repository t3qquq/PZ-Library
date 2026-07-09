// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import zombie.Lua.LuaManager;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;

@CommandName(
    name = "checkModsNeedUpdate"
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_CheckModsNeedUpdate"
)
@RequiredRight(
    requiredRights = 62
)
public class CheckModsNeedUpdate extends CommandBase {
    public CheckModsNeedUpdate(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        LuaManager.GlobalObject.checkModsNeedUpdate(this.connection);
        return "Checking started. The answer will be written in the log file and in the chat";
    }
}
