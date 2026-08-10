// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import zombie.AmbientStreamManager;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;

@CommandName(
    name = "gunshot"
)
@CommandHelp(
    helpText = "UI_ServerOptionDesc_Gunshot"
)
@RequiredRight(
    requiredRights = 60
)
public class GunShotCommand extends CommandBase {
    public GunShotCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        AmbientStreamManager.instance.doGunEvent();
        LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " did gunshot");
        return "Gunshot fired";
    }
}
