// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio;

import fmod.fmod.FMODManager;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import java.util.ArrayList;
import zombie.SystemDisabler;
import zombie.core.Rand;

public final class GameSound {
    public String name;
    public String category = "General";
    public boolean loop = false;
    public boolean is3D = true;
    public final ArrayList<GameSoundClip> clips = new ArrayList<>();
    private float userVolume = 1.0F;
    public GameSound.MasterVolume master = GameSound.MasterVolume.Primary;
    public int maxInstancesPerEmitter = -1;
    public short reloadEpoch;

    public String getName() {
        return this.name;
    }

    public String getCategory() {
        return this.category;
    }

    public boolean isLooped() {
        return this.loop;
    }

    public void setUserVolume(float gain) {
        this.userVolume = Math.max(0.0F, Math.min(2.0F, gain));
    }

    public float getUserVolume() {
        return !SystemDisabler.getEnableAdvancedSoundOptions() ? 1.0F : this.userVolume;
    }

    public GameSoundClip getRandomClip() {
        return this.clips.get(Rand.Next(this.clips.size()));
    }

    public String getMasterName() {
        return this.master.name();
    }

    public int numClipsUsingParameter(String parameterName) {
        FMOD_STUDIO_PARAMETER_DESCRIPTION fmod_studio_parameter_description = FMODManager.instance.getParameterDescription(parameterName);
        if (fmod_studio_parameter_description == null) {
            return 0;
        } else {
            int int0 = 0;

            for (int int1 = 0; int1 < this.clips.size(); int1++) {
                GameSoundClip gameSoundClip = this.clips.get(int1);
                if (gameSoundClip.hasParameter(fmod_studio_parameter_description)) {
                    int0++;
                }
            }

            return int0;
        }
    }

    public void reset() {
        this.name = null;
        this.category = "General";
        this.loop = false;
        this.is3D = true;
        this.clips.clear();
        this.userVolume = 1.0F;
        this.master = GameSound.MasterVolume.Primary;
        this.maxInstancesPerEmitter = -1;
        this.reloadEpoch++;
    }

    public static enum MasterVolume {
        Primary,
        Ambient,
        Music,
        VehicleEngine;
    }
}
