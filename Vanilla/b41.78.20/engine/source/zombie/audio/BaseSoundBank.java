// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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
