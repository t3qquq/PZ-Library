// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package fmod.fmod;

import zombie.audio.BaseSoundEmitter;

public class FMODAudio implements Audio {
    public BaseSoundEmitter emitter;

    public FMODAudio(BaseSoundEmitter arg0) {
        this.emitter = arg0;
    }

    @Override
    public boolean isPlaying() {
        return !this.emitter.isEmpty();
    }

    @Override
    public void setVolume(float arg0) {
        this.emitter.setVolumeAll(arg0);
    }

    @Override
    public void start() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void stop() {
        this.emitter.stopAll();
    }

    @Override
    public void setName(String arg0) {
    }

    @Override
    public String getName() {
        return null;
    }
}
