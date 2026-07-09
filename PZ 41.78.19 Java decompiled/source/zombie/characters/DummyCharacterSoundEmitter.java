// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import java.util.HashMap;
import zombie.core.Rand;
import zombie.iso.IsoObject;
import zombie.network.GameClient;

public final class DummyCharacterSoundEmitter extends BaseCharacterSoundEmitter {
    public float x;
    public float y;
    public float z;
    private final HashMap<Long, String> sounds = new HashMap<>();

    public DummyCharacterSoundEmitter(IsoGameCharacter chr) {
        super(chr);
    }

    @Override
    public void register() {
    }

    @Override
    public void unregister() {
    }

    @Override
    public long playVocals(String file) {
        return 0L;
    }

    @Override
    public void playFootsteps(String file, float volume) {
    }

    @Override
    public long playSound(String file) {
        long long0 = Rand.Next(Integer.MAX_VALUE);
        this.sounds.put(long0, file);
        if (GameClient.bClient) {
            GameClient.instance.PlaySound(file, false, this.character);
        }

        return long0;
    }

    @Override
    public long playSound(String file, IsoObject proxy) {
        return this.playSound(file);
    }

    @Override
    public long playSoundImpl(String file, IsoObject proxy) {
        long long0 = Rand.Next(Long.MAX_VALUE);
        this.sounds.put(long0, file);
        return long0;
    }

    @Override
    public void tick() {
    }

    @Override
    public void set(float _x, float _y, float _z) {
        this.x = _x;
        this.y = _y;
        this.z = _z;
    }

    @Override
    public boolean isClear() {
        return this.sounds.isEmpty();
    }

    @Override
    public void setPitch(long handle, float pitch) {
    }

    @Override
    public void setVolume(long handle, float volume) {
    }

    @Override
    public int stopSound(long channel) {
        if (GameClient.bClient) {
            GameClient.instance.StopSound(this.character, this.sounds.get(channel), false);
        }

        this.sounds.remove(channel);
        return 0;
    }

    @Override
    public void stopSoundLocal(long handle) {
        this.sounds.remove(handle);
    }

    @Override
    public void stopOrTriggerSound(long handle) {
        if (GameClient.bClient) {
            GameClient.instance.StopSound(this.character, this.sounds.get(handle), true);
        }

        this.sounds.remove(handle);
    }

    @Override
    public void stopOrTriggerSoundByName(String name) {
        this.sounds.values().remove(name);
    }

    @Override
    public void stopAll() {
        if (GameClient.bClient) {
            for (String string : this.sounds.values()) {
                GameClient.instance.StopSound(this.character, string, false);
            }
        }

        this.sounds.clear();
    }

    @Override
    public int stopSoundByName(String soundName) {
        this.sounds.values().remove(soundName);
        return 0;
    }

    @Override
    public boolean hasSoundsToStart() {
        return false;
    }

    @Override
    public boolean isPlaying(long channel) {
        return this.sounds.containsKey(channel);
    }

    @Override
    public boolean isPlaying(String alias) {
        return this.sounds.containsValue(alias);
    }

    @Override
    public void setParameterValue(long soundRef, FMOD_STUDIO_PARAMETER_DESCRIPTION parameterDescription, float value) {
    }

    public boolean hasSustainPoints(long handle) {
        return false;
    }
}
