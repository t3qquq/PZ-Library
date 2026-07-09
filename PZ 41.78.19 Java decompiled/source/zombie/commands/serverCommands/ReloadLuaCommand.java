// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import zombie.Lua.LuaManager;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;

@CommandName(
    name = "reloadlua"
)
@CommandArgs(
    required = {"(\\S+)"}
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_ReloadLua"
)
@RequiredRight(
    requiredRights = 32
)
public class ReloadLuaCommand extends CommandBase {
    public ReloadLuaCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        String string0 = this.getCommandArg(0);

        for (String string1 : LuaManager.loaded) {
            if (string1.endsWith(string0)) {
                LuaManager.loaded.remove(string1);
                LuaManager.RunLua(string1, true);
                return "Lua file reloaded";
            }
        }

        return "Unknown Lua file";
    }
}
