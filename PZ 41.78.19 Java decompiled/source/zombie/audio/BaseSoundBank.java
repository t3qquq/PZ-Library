// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio;

import fmod.fmod.FMODFootstep;
import fmod.fmod.FMODVoice;

public abstract class BaseSoundBank {
    public static BaseSoundBank instance;

    public abstract void addVoice(String alias, String sound, float priority);

    public abstract void addFootstep(String alias, String grass, String wood, String concrete, String upstairs);

    public abstract FMODVoice getVoice(String alias);

    public abstract FMODFootstep getFootstep(String alias);
}
