// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.core.Core;

public final class ParameterMusicLibrary extends FMODGlobalParameter {
    public ParameterMusicLibrary() {
        super("MusicLibrary");
    }

    @Override
    public float calculateCurrentValue() {
        return switch (Core.getInstance().getOptionMusicLibrary()) {
            case 2 -> ParameterMusicLibrary.Library.EarlyAccess.label;
            case 3 -> ParameterMusicLibrary.Library.Random.label;
            default -> ParameterMusicLibrary.Library.Official.label;
        };
    }

    public static enum Library {
        Official(0),
        EarlyAccess(1),
        Random(2);

        final int label;

        private Library(int int1) {
            this.label = int1;
        }
    }
}
