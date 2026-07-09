// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.StorySounds;

import zombie.characters.IsoPlayer;
import zombie.iso.Vector2;

/**
 * Turbo
 */
public final class StorySound {
    protected String name = null;
    protected float baseVolume = 1.0F;

    public StorySound(String _name, float baseVol) {
        this.name = _name;
        this.baseVolume = baseVol;
    }

    public long playSound() {
        Vector2 vector = SLSoundManager.getInstance().getRandomBorderPosition();
        return SLSoundManager.Emitter
            .playSound(this.name, this.baseVolume, vector.x, vector.y, 0.0F, 100.0F, SLSoundManager.getInstance().getRandomBorderRange());
    }

    public long playSound(float volumeOverride) {
        return SLSoundManager.Emitter
            .playSound(this.name, volumeOverride, IsoPlayer.getInstance().x, IsoPlayer.getInstance().y, IsoPlayer.getInstance().z, 10.0F, 50.0F);
    }

    public long playSound(float x, float y, float z, float minRange, float maxRange) {
        return this.playSound(this.baseVolume, x, y, z, minRange, maxRange);
    }

    public long playSound(float volumeMod, float x, float y, float z, float minRange, float maxRange) {
        return SLSoundManager.Emitter.playSound(this.name, this.baseVolume * volumeMod, x, y, z, minRange, maxRange);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    public float getBaseVolume() {
        return this.baseVolume;
    }

    public void setBaseVolume(float _baseVolume) {
        this.baseVolume = _baseVolume;
    }

    public StorySound getClone() {
        return new StorySound(this.name, this.baseVolume);
    }
}
