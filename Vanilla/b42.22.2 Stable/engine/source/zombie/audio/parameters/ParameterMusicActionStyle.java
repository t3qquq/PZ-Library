// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.core.Core;

public final class ParameterMusicActionStyle extends FMODGlobalParameter {
    public ParameterMusicActionStyle() {
        super("MusicActionStyle");
    }

    @Override
    public float calculateCurrentValue() {
        return Core.getInstance().getOptionMusicActionStyle() == 2
            ? ParameterMusicActionStyle.State.Legacy.label
            : ParameterMusicActionStyle.State.Official.label;
    }

    public enum State {
        Official(0),
        Legacy(1);

        final int label;

        State(final int label) {
            this.label = label;
        }
    }
}
