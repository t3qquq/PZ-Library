// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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

    public static enum State {
        Official(0),
        Legacy(1);

        final int label;

        private State(int int1) {
            this.label = int1;
        }
    }
}
