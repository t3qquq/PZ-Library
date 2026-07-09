// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import zombie.ai.states.FishingState;
import zombie.core.Core;
import zombie.iso.Vector2;
import zombie.network.GameClient;

public class NetworkPlayerVariables {
    static Vector2 deferredMovement = new Vector2();

    public static int getBooleanVariables(IsoPlayer player) {
        int int0 = 0;
        int0 |= player.isSneaking() ? 1 : 0;
        int0 |= player.isOnFire() ? 2 : 0;
        int0 |= player.isAsleep() ? 4 : 0;
        int0 |= FishingState.instance().equals(player.getCurrentState()) ? 8 : 0;
        int0 |= player.isRunning() ? 16 : 0;
        int0 |= player.isSprinting() ? 32 : 0;
        int0 |= player.isAiming() ? 64 : 0;
        int0 |= player.isCharging ? 128 : 0;
        int0 |= player.isChargingLT ? 256 : 0;
        int0 |= player.bDoShove ? 512 : 0;
        player.getDeferredMovement(deferredMovement);
        int0 |= deferredMovement.getLength() > 0.0F ? 1024 : 0;
        int0 |= player.isOnFloor() ? 2048 : 0;
        int0 |= player.isGhostMode() ? 4096 : 0;
        int0 |= Core.bDebug ? 8192 : 0;
        int0 |= player.isNoClip() ? 16384 : 0;
        if (GameClient.bClient && GameClient.connection.accessLevel != 1 || !player.isAccessLevel("None")) {
            int0 |= 32768;
        }

        int0 |= player.isSitOnGround() ? 131072 : 0;
        return int0 | ("fall".equals(player.getVariableString("ClimbFenceOutcome")) ? 262144 : 0);
    }

    public static void setBooleanVariables(IsoPlayer player, int int0) {
        player.setSneaking((int0 & 1) != 0);
        if ((int0 & 2) != 0) {
            player.SetOnFire();
        } else {
            player.StopBurning();
        }

        player.setAsleep((int0 & 4) != 0);
        boolean boolean0 = (int0 & 8) != 0;
        if (FishingState.instance().equals(player.getCurrentState()) && !boolean0) {
            player.SetVariable("FishingFinished", "true");
        }

        player.setRunning((int0 & 16) != 0);
        player.setSprinting((int0 & 32) != 0);
        player.setIsAiming((int0 & 64) != 0);
        player.isCharging = (int0 & 128) != 0;
        player.isChargingLT = (int0 & 256) != 0;
        if (!player.bDoShove && (int0 & 512) != 0) {
            player.setDoShove((int0 & 512) != 0);
        }

        player.networkAI.moving = (int0 & 1024) != 0;
        player.setOnFloor((int0 & 2048) != 0);
        player.setSitOnGround((int0 & 131072) != 0);
        player.networkAI.climbFenceOutcomeFall = (int0 & 262144) != 0;
    }
}
