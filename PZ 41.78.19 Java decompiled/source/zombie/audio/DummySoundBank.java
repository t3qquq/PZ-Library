// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio;

import fmod.fmod.FMODFootstep;
import fmod.fmod.FMODVoice;

public class DummySoundBank extends BaseSoundBank {
    @Override
    public void addVoice(String alias, String sound, float priority) {
    }

    @Override
    public void addFootstep(String alias, String grass, String wood, String concrete, String upstairs) {
    }

    @Override
    public FMODVoice getVoice(String alias) {
        return null;
    }

    @Override
    public FMODFootstep getFootstep(String alias) {
        return null;
    }
}
