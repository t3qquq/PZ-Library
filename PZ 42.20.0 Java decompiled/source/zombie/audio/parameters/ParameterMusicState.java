// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
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

    public void setState(ParameterMusicState.State state) {
        this.state = state;
    }

    public enum State {
        MainMenu(0),
        Loading(1),
        InGame(2),
        PauseMenu(3),
        Tutorial(4);

        final int label;

        State(final int label) {
            this.label = label;
        }
    }
}
