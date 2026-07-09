// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;

public final class ParameterCameraZoom extends FMODGlobalParameter {
    public ParameterCameraZoom() {
        super("CameraZoom");
    }

    @Override
    public float calculateCurrentValue() {
        IsoPlayer player = this.getPlayer();
        if (player == null) {
            return 0.0F;
        } else {
            float float0 = Core.getInstance().getZoom(player.PlayerIndex) - Core.getInstance().OffscreenBuffer.getMinZoom();
            float float1 = Core.getInstance().OffscreenBuffer.getMaxZoom() - Core.getInstance().OffscreenBuffer.getMinZoom();
            return float0 / float1;
        }
    }

    private IsoPlayer getPlayer() {
        IsoPlayer player0 = null;

        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            IsoPlayer player1 = IsoPlayer.players[int0];
            if (player1 != null && (player0 == null || player0.isDead() && player1.isAlive())) {
                player0 = player1;
            }
        }

        return player0;
    }
}
