// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import fmod.fmod.Audio;
import java.util.ArrayList;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoGridSquare;

public final class DummySoundManager extends BaseSoundManager {
    private static ArrayList<Audio> ambientPieces = new ArrayList<>();

    @Override
    public boolean isRemastered() {
        return false;
    }

    @Override
    public void update1() {
    }

    @Override
    public void update3() {
    }

    @Override
    public void update2() {
    }

    @Override
    public void update4() {
    }

    @Override
    public void CacheSound(String file) {
    }

    @Override
    public void StopSound(Audio SoundEffect) {
    }

    @Override
    public void StopMusic() {
    }

    @Override
    public void Purge() {
    }

    @Override
    public void stop() {
    }

    @Override
    protected boolean HasMusic(Audio var1) {
        return false;
    }

    @Override
    public void Update() {
    }

    @Override
    public Audio Start(Audio musicTrack, float f, String PrefMusic) {
        return null;
    }

    @Override
    public Audio PrepareMusic(String name) {
        return null;
    }

    @Override
    public void PlayWorldSoundWav(String name, IsoGridSquare source, float pitchVar, float radius, float maxGain, int choices, boolean ignoreOutside) {
    }

    @Override
    public Audio PlayWorldSoundWav(String name, boolean loop, IsoGridSquare source, float pitchVar, float radius, float maxGain, boolean ignoreOutside) {
        return null;
    }

    @Override
    public Audio PlayWorldSoundWav(String name, IsoGridSquare source, float pitchVar, float radius, float maxGain, boolean ignoreOutside) {
        return null;
    }

    @Override
    public Audio PlayWorldSound(String name, IsoGridSquare source, float pitchVar, float radius, float maxGain, int choices, boolean ignoreOutside) {
        return null;
    }

    @Override
    public Audio PlayWorldSound(String name, boolean loop, IsoGridSquare source, float pitchVar, float radius, float maxGain, boolean ignoreOutside) {
        return null;
    }

    @Override
    public Audio PlayWorldSoundImpl(String name, boolean loop, int sx, int sy, int sz, float pitchVar, float radius, float maxGain, boolean ignoreOutside) {
        return null;
    }

    @Override
    public Audio PlayWorldSound(String name, IsoGridSquare source, float pitchVar, float radius, float maxGain, boolean ignoreOutside) {
        return null;
    }

    @Override
    public void update3D() {
    }

    @Override
    public Audio PlaySoundWav(String name, int variations, boolean loop, float maxGain) {
        return null;
    }

    @Override
    public Audio PlaySoundWav(String name, boolean loop, float maxGain) {
        return null;
    }

    @Override
    public Audio PlaySoundWav(String name, boolean loop, float maxGain, float pitchVar) {
        return null;
    }

    @Override
    public Audio PlayJukeboxSound(String name, boolean loop, float maxGain) {
        return null;
    }

    @Override
    public Audio PlaySoundEvenSilent(String name, boolean loop, float maxGain) {
        return null;
    }

    @Override
    public Audio PlaySound(String name, boolean loop, float maxGain) {
        return null;
    }

    @Override
    public Audio PlaySound(String name, boolean loop, float pitchVar, float maxGain) {
        return null;
    }

    @Override
    public Audio PlayMusic(String n, String name, boolean loop, float maxGain) {
        return null;
    }

    @Override
    public void PlayAsMusic(String name, Audio musicTrack, boolean loop, float volume) {
    }

    @Override
    public void setMusicState(String stateName) {
    }

    @Override
    public void setMusicWakeState(IsoPlayer player, String stateName) {
    }

    @Override
    public void DoMusic(String name, boolean bLoop) {
    }

    @Override
    public float getMusicPosition() {
        return 0.0F;
    }

    @Override
    public void CheckDoMusic() {
    }

    @Override
    public void stopMusic(String name) {
    }

    @Override
    public void playMusicNonTriggered(String name, float gain) {
    }

    @Override
    public void playAmbient(String name) {
    }

    @Override
    public void playMusic(String name) {
    }

    @Override
    public boolean isPlayingMusic() {
        return false;
    }

    @Override
    public boolean IsMusicPlaying() {
        return false;
    }

    @Override
    public void PlayAsMusic(String name, Audio musicTrack, float volume, boolean bloop) {
    }

    @Override
    public long playUISound(String name) {
        return 0L;
    }

    @Override
    public boolean isPlayingUISound(String name) {
        return false;
    }

    @Override
    public boolean isPlayingUISound(long eventInstance) {
        return false;
    }

    @Override
    public void stopUISound(long eventInstance) {
    }

    @Override
    public void FadeOutMusic(String name, int milli) {
    }

    @Override
    public Audio BlendThenStart(Audio musicTrack, float f, String PrefMusic) {
        return null;
    }

    @Override
    public void BlendVolume(Audio audio, float targetVolume, float blendSpeedAlpha) {
    }

    @Override
    public void BlendVolume(Audio audio, float targetVolume) {
    }

    @Override
    public void setSoundVolume(float volume) {
    }

    @Override
    public float getSoundVolume() {
        return 0.0F;
    }

    @Override
    public void setMusicVolume(float volume) {
    }

    @Override
    public float getMusicVolume() {
        return 0.0F;
    }

    @Override
    public void setVehicleEngineVolume(float volume) {
    }

    @Override
    public float getVehicleEngineVolume() {
        return 0.0F;
    }

    @Override
    public void setAmbientVolume(float volume) {
    }

    @Override
    public float getAmbientVolume() {
        return 0.0F;
    }

    @Override
    public void playNightAmbient(String choice) {
    }

    @Override
    public ArrayList<Audio> getAmbientPieces() {
        return ambientPieces;
    }

    @Override
    public void pauseSoundAndMusic() {
    }

    @Override
    public void resumeSoundAndMusic() {
    }

    @Override
    public void debugScriptSounds() {
    }

    @Override
    public void registerEmitter(BaseSoundEmitter emitter) {
    }

    @Override
    public void unregisterEmitter(BaseSoundEmitter emitter) {
    }

    @Override
    public boolean isListenerInRange(float x, float y, float range) {
        return false;
    }

    @Override
    public Audio PlayWorldSoundWavImpl(String name, boolean loop, IsoGridSquare source, float pitchVar, float radius, float maxGain, boolean ignoreOutside) {
        return null;
    }

    @Override
    public String getCurrentMusicName() {
        return null;
    }

    @Override
    public String getCurrentMusicLibrary() {
        return null;
    }
}
