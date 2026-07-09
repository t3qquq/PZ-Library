// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.characters.IsoPlayer;

public final class ParameterHardOfHearing extends FMODGlobalParameter {
    private int m_playerIndex = -1;

    public ParameterHardOfHearing() {
        super("HardOfHearing");
    }

    @Override
    public float calculateCurrentValue() {
        IsoPlayer player = this.choosePlayer();
        if (player != null) {
            return player.getCharacterTraits().HardOfHearing.isSet() ? 1.0F : 0.0F;
        } else {
            return 0.0F;
        }
    }

    private IsoPlayer choosePlayer() {
        if (this.m_playerIndex != -1) {
            IsoPlayer player0 = IsoPlayer.players[this.m_playerIndex];
            if (player0 == null) {
                this.m_playerIndex = -1;
            }
        }

        if (this.m_playerIndex != -1) {
            return IsoPlayer.players[this.m_playerIndex];
        } else {
            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                IsoPlayer player1 = IsoPlayer.players[int0];
                if (player1 != null) {
                    this.m_playerIndex = int0;
                    return player1;
                }
            }

            return null;
        }
    }
}
