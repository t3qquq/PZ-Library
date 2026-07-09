// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.characters.IsoPlayer;

public final class ParameterMusicWakeState extends FMODGlobalParameter {
    private int m_playerIndex = -1;
    private ParameterMusicWakeState.State m_state = ParameterMusicWakeState.State.Awake;

    public ParameterMusicWakeState() {
        super("MusicWakeState");
    }

    @Override
    public float calculateCurrentValue() {
        IsoPlayer player = this.choosePlayer();
        if (player != null && this.m_state == ParameterMusicWakeState.State.Awake && player.isAsleep()) {
            this.m_state = ParameterMusicWakeState.State.Sleeping;
        }

        return this.m_state.label;
    }

    public void setState(IsoPlayer player, ParameterMusicWakeState.State state) {
        if (player == this.choosePlayer()) {
            this.m_state = state;
        }
    }

    private IsoPlayer choosePlayer() {
        if (this.m_playerIndex != -1) {
            IsoPlayer player0 = IsoPlayer.players[this.m_playerIndex];
            if (player0 == null || player0.isDead()) {
                this.m_playerIndex = -1;
            }
        }

        if (this.m_playerIndex != -1) {
            return IsoPlayer.players[this.m_playerIndex];
        } else {
            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                IsoPlayer player1 = IsoPlayer.players[int0];
                if (player1 != null && !player1.isDead()) {
                    this.m_playerIndex = int0;
                    this.m_state = player1.isAsleep() ? ParameterMusicWakeState.State.Sleeping : ParameterMusicWakeState.State.Awake;
                    return player1;
                }
            }

            return null;
        }
    }

    public static enum State {
        Awake(0),
        Sleeping(1),
        WakeNormal(2),
        WakeNightmare(3),
        WakeZombies(4);

        final int label;

        private State(int int1) {
            this.label = int1;
        }
    }
}
