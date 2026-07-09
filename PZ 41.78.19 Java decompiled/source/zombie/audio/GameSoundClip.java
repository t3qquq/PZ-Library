// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio;

import fmod.fmod.FMOD_STUDIO_EVENT_DESCRIPTION;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import zombie.SoundManager;
import zombie.core.Core;

public final class GameSoundClip {
    public static short INIT_FLAG_DISTANCE_MIN = 1;
    public static short INIT_FLAG_DISTANCE_MAX = 2;
    public final GameSound gameSound;
    public String event;
    public FMOD_STUDIO_EVENT_DESCRIPTION eventDescription;
    public FMOD_STUDIO_EVENT_DESCRIPTION eventDescriptionMP;
    public String file;
    public float volume = 1.0F;
    public float pitch = 1.0F;
    public float distanceMin = 10.0F;
    public float distanceMax = 10.0F;
    public float reverbMaxRange = 10.0F;
    public float reverbFactor = 0.0F;
    public int priority = 5;
    public short initFlags = 0;
    public short reloadEpoch;

    public GameSoundClip(GameSound _gameSound) {
        this.gameSound = _gameSound;
        this.reloadEpoch = _gameSound.reloadEpoch;
    }

    public String getEvent() {
        return this.event;
    }

    public String getFile() {
        return this.file;
    }

    public float getVolume() {
        return this.volume;
    }

    public float getPitch() {
        return this.pitch;
    }

    public boolean hasMinDistance() {
        return (this.initFlags & INIT_FLAG_DISTANCE_MIN) != 0;
    }

    public boolean hasMaxDistance() {
        return (this.initFlags & INIT_FLAG_DISTANCE_MAX) != 0;
    }

    public float getMinDistance() {
        return this.distanceMin;
    }

    public float getMaxDistance() {
        return this.distanceMax;
    }

    public float getEffectiveVolume() {
        float float0 = 1.0F;
        switch (this.gameSound.master) {
            case Primary:
                float0 = SoundManager.instance.getSoundVolume();
                break;
            case Ambient:
                float0 = SoundManager.instance.getAmbientVolume();
                break;
            case Music:
                float0 = SoundManager.instance.getMusicVolume();
                break;
            case VehicleEngine:
                float0 = SoundManager.instance.getVehicleEngineVolume();
        }

        float0 *= this.volume;
        return float0 * this.gameSound.getUserVolume();
    }

    public float getEffectiveVolumeInMenu() {
        float float0 = 1.0F;
        switch (this.gameSound.master) {
            case Primary:
                float0 = Core.getInstance().getOptionSoundVolume() / 10.0F;
                break;
            case Ambient:
                float0 = Core.getInstance().getOptionAmbientVolume() / 10.0F;
                break;
            case Music:
                float0 = Core.getInstance().getOptionMusicVolume() / 10.0F;
                break;
            case VehicleEngine:
                float0 = Core.getInstance().getOptionVehicleEngineVolume() / 10.0F;
        }

        float0 *= this.volume;
        return float0 * this.gameSound.getUserVolume();
    }

    public GameSoundClip checkReloaded() {
        if (this.reloadEpoch == this.gameSound.reloadEpoch) {
            return this;
        } else {
            GameSoundClip gameSoundClip1 = null;

            for (int int0 = 0; int0 < this.gameSound.clips.size(); int0++) {
                GameSoundClip gameSoundClip2 = this.gameSound.clips.get(int0);
                if (gameSoundClip2 == this) {
                    return this;
                }

                if (gameSoundClip2.event != null && gameSoundClip2.event.equals(this.event)) {
                    gameSoundClip1 = gameSoundClip2;
                }

                if (gameSoundClip2.file != null && gameSoundClip2.file.equals(this.file)) {
                    gameSoundClip1 = gameSoundClip2;
                }
            }

            if (gameSoundClip1 == null) {
                this.reloadEpoch = this.gameSound.reloadEpoch;
                return this;
            } else {
                return gameSoundClip1;
            }
        }
    }

    public boolean hasSustainPoints() {
        return this.eventDescription != null && this.eventDescription.bHasSustainPoints;
    }

    public boolean hasParameter(FMOD_STUDIO_PARAMETER_DESCRIPTION parameterDescription) {
        return this.eventDescription != null && this.eventDescription.hasParameter(parameterDescription);
    }
}
