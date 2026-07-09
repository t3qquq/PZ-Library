// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;

public final class ParameterMusicState extends FMODGlobalParameter {
    private ParameterMusicState.State state = ParameterMusicState.State.MainMenu;

    public ParameterMusicState() {
        super("MusicState");
    }

    @Override
    public float calculateCurrentValue() {
        return this.state.label;
    }

    public void setState(ParameterMusicState.State _state) {
        this.state = _state;
    }

    public static enum State {
        MainMenu(0),
        Loading(1),
        InGame(2),
        PauseMenu(3),
        Tutorial(4);

        final int label;

        private State(int int1) {
            this.label = int1;
        }
    }
}
