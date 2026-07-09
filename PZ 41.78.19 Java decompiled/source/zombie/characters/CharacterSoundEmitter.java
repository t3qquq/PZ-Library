// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import fmod.fmod.EmitterType;
import fmod.fmod.FMODManager;
import fmod.fmod.FMODSoundBank;
import fmod.fmod.FMODSoundEmitter;
import fmod.fmod.FMODVoice;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import zombie.SoundManager;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.interfaces.ICommonSoundEmitter;
import zombie.iso.IsoObject;
import zombie.network.GameServer;

/**
 * Created by LEMMYMAIN on 18/09/2014.
 */
public final class CharacterSoundEmitter extends BaseCharacterSoundEmitter implements ICommonSoundEmitter {
    float currentPriority;
    final FMODSoundEmitter vocals = new FMODSoundEmitter();
    final FMODSoundEmitter footsteps = new FMODSoundEmitter();
    final FMODSoundEmitter extra = new FMODSoundEmitter();
    private long footstep1 = 0L;
    private long footstep2 = 0L;

    public CharacterSoundEmitter(IsoGameCharacter chr) {
        super(chr);
        this.vocals.emitterType = EmitterType.Voice;
        this.vocals.parent = this.character;
        this.vocals.parameterUpdater = chr;
        this.footsteps.emitterType = EmitterType.Footstep;
        this.footsteps.parent = this.character;
        this.footsteps.parameterUpdater = chr;
        this.extra.emitterType = EmitterType.Extra;
        this.extra.parent = this.character;
        this.extra.parameterUpdater = chr;
    }

    @Override
    public void register() {
        SoundManager.instance.registerEmitter(this.vocals);
        SoundManager.instance.registerEmitter(this.footsteps);
        SoundManager.instance.registerEmitter(this.extra);
    }

    @Override
    public void unregister() {
        SoundManager.instance.unregisterEmitter(this.vocals);
        SoundManager.instance.unregisterEmitter(this.footsteps);
        SoundManager.instance.unregisterEmitter(this.extra);
    }

    @Override
    public long playVocals(String file) {
        if (GameServer.bServer) {
            return 0L;
        } else {
            FMODVoice fMODVoice = FMODSoundBank.instance.getVoice(file);
            if (fMODVoice == null) {
                return this.vocals.playSoundImpl(file, false, null);
            } else {
                float float0 = fMODVoice.priority;
                long long0 = this.vocals.playSound(fMODVoice.sound, this.character);
                this.currentPriority = float0;
                return long0;
            }
        }
    }

    CharacterSoundEmitter.footstep getFootstepToPlay() {
        if (FMODManager.instance.getNumListeners() == 1) {
            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                IsoPlayer player = IsoPlayer.players[int0];
                if (player != null && player != this.character && !player.Traits.Deaf.isSet()) {
                    if ((int)player.getZ() < (int)this.character.getZ()) {
                        return CharacterSoundEmitter.footstep.upstairs;
                    }
                    break;
                }
            }
        }

        IsoObject object = this.character.getCurrentSquare().getFloor();
        if (object != null && object.getSprite() != null && object.getSprite().getName() != null) {
            String string = object.getSprite().getName();
            if (string.endsWith("blends_natural_01_5")
                || string.endsWith("blends_natural_01_6")
                || string.endsWith("blends_natural_01_7")
                || string.endsWith("blends_natural_01_0")) {
                return CharacterSoundEmitter.footstep.gravel;
            } else if (string.endsWith("blends_street_01_48")
                || string.endsWith("blends_street_01_53")
                || string.endsWith("blends_street_01_54")
                || string.endsWith("blends_street_01_55")) {
                return CharacterSoundEmitter.footstep.gravel;
            } else if (string.startsWith("blends_natural_01")) {
                return CharacterSoundEmitter.footstep.grass;
            } else if (string.startsWith("floors_interior_tilesandwood_01_")) {
                int int1 = Integer.parseInt(string.replaceFirst("floors_interior_tilesandwood_01_", ""));
                return int1 > 40 && int1 < 48 ? CharacterSoundEmitter.footstep.wood : CharacterSoundEmitter.footstep.concrete;
            } else if (string.startsWith("carpentry_02_")) {
                return CharacterSoundEmitter.footstep.wood;
            } else {
                return string.startsWith("floors_interior_carpet_") ? CharacterSoundEmitter.footstep.wood : CharacterSoundEmitter.footstep.concrete;
            }
        } else {
            return CharacterSoundEmitter.footstep.concrete;
        }
    }

    @Override
    public void playFootsteps(String file, float volume) {
        if (!GameServer.bServer) {
            boolean boolean0 = this.footsteps.isPlaying(this.footstep1);
            boolean boolean1 = this.footsteps.isPlaying(this.footstep2);
            if (boolean0 && boolean1) {
                long long0 = this.footstep1;
                this.footstep1 = this.footstep2;
                this.footstep2 = long0;
                if (this.footsteps.restart(this.footstep2)) {
                    return;
                }

                this.footsteps.stopSoundLocal(this.footstep2);
                this.footstep2 = 0L;
            } else if (boolean1) {
                this.footstep1 = this.footstep2;
                this.footstep2 = 0L;
                boolean0 = true;
                boolean1 = false;
            }

            long long1 = this.footsteps.playSoundImpl(file, false, null);
            if (!boolean0) {
                this.footstep1 = long1;
            } else {
                this.footstep2 = long1;
            }
        }
    }

    @Override
    public long playSound(String file) {
        if (this.character.isInvisible() && !DebugOptions.instance.Character.Debug.PlaySoundWhenInvisible.getValue()) {
            return 0L;
        } else {
            if (DebugLog.isEnabled(DebugType.Sound)) {
                DebugLog.Sound.debugln("Playing sound: " + file + (this.character.isZombie() ? " for zombie" : " for player"));
            }

            return this.extra.playSound(file);
        }
    }

    @Override
    public long playSound(String file, boolean doWorldSound) {
        if (this.character.isInvisible() && !DebugOptions.instance.Character.Debug.PlaySoundWhenInvisible.getValue()) {
            return 0L;
        } else {
            if (DebugLog.isEnabled(DebugType.Sound)) {
                DebugLog.Sound.debugln("Playing sound: " + file + (this.character.isZombie() ? " for zombie" : " for player"));
            }

            return this.extra.playSound(file, doWorldSound);
        }
    }

    @Override
    public long playSound(String file, IsoObject proxy) {
        if (this.character.isInvisible() && !DebugOptions.instance.Character.Debug.PlaySoundWhenInvisible.getValue()) {
            return 0L;
        } else {
            if (DebugLog.isEnabled(DebugType.Sound)) {
                DebugLog.Sound.debugln("Playing sound: " + file + (this.character.isZombie() ? " for zombie" : " for player"));
            }

            return GameServer.bServer ? 0L : this.extra.playSound(file, proxy);
        }
    }

    @Override
    public long playSoundImpl(String file, IsoObject proxy) {
        if (this.character instanceof IsoPlayer && ((IsoPlayer)this.character).bRemote && this.character.isInvisible()) {
            return 0L;
        } else {
            if (DebugLog.isEnabled(DebugType.Sound)) {
                DebugLog.Sound.debugln("Playing sound: " + file + (this.character.isZombie() ? " for zombie" : " for player"));
            }

            return this.extra.playSoundImpl(file, false, proxy);
        }
    }

    @Override
    public void tick() {
        this.vocals.tick();
        this.footsteps.tick();
        this.extra.tick();
    }

    @Override
    public void setPos(float x, float y, float z) {
        this.set(x, y, z);
    }

    @Override
    public void set(float x, float y, float z) {
        this.vocals.x = this.footsteps.x = this.extra.x = x;
        this.vocals.y = this.footsteps.y = this.extra.y = y;
        this.vocals.z = this.footsteps.z = this.extra.z = z;
    }

    @Override
    public boolean isEmpty() {
        return this.isClear();
    }

    @Override
    public boolean isClear() {
        return this.vocals.isEmpty() && this.footsteps.isEmpty() && this.extra.isEmpty();
    }

    @Override
    public void setPitch(long handle, float pitch) {
        this.extra.setPitch(handle, pitch);
        this.footsteps.setPitch(handle, pitch);
        this.vocals.setPitch(handle, pitch);
    }

    @Override
    public void setVolume(long handle, float volume) {
        this.extra.setVolume(handle, volume);
        this.footsteps.setVolume(handle, volume);
        this.vocals.setVolume(handle, volume);
    }

    @Override
    public boolean hasSustainPoints(long handle) {
        if (this.extra.isPlaying(handle)) {
            return this.extra.hasSustainPoints(handle);
        } else if (this.footsteps.isPlaying(handle)) {
            return this.footsteps.hasSustainPoints(handle);
        } else {
            return this.vocals.isPlaying(handle) ? this.vocals.hasSustainPoints(handle) : false;
        }
    }

    @Override
    public void triggerCue(long handle) {
        if (this.extra.isPlaying(handle)) {
            this.extra.triggerCue(handle);
        } else if (this.footsteps.isPlaying(handle)) {
            this.footsteps.triggerCue(handle);
        } else if (this.vocals.isPlaying(handle)) {
            this.vocals.triggerCue(handle);
        }
    }

    @Override
    public int stopSound(long eventInstance) {
        this.extra.stopSound(eventInstance);
        this.footsteps.stopSound(eventInstance);
        this.vocals.stopSound(eventInstance);
        return 0;
    }

    @Override
    public void stopSoundLocal(long handle) {
        this.extra.stopSoundLocal(handle);
        this.footsteps.stopSoundLocal(handle);
        this.vocals.stopSoundLocal(handle);
    }

    @Override
    public void stopOrTriggerSound(long eventInstance) {
        this.extra.stopOrTriggerSound(eventInstance);
        this.footsteps.stopOrTriggerSound(eventInstance);
        this.vocals.stopOrTriggerSound(eventInstance);
    }

    @Override
    public void stopOrTriggerSoundByName(String name) {
        this.extra.stopOrTriggerSoundByName(name);
        this.footsteps.stopOrTriggerSoundByName(name);
        this.vocals.stopOrTriggerSoundByName(name);
    }

    @Override
    public void stopAll() {
        this.extra.stopAll();
        this.footsteps.stopAll();
        this.vocals.stopAll();
    }

    @Override
    public int stopSoundByName(String soundName) {
        this.extra.stopSoundByName(soundName);
        this.footsteps.stopSoundByName(soundName);
        this.vocals.stopSoundByName(soundName);
        return 0;
    }

    @Override
    public boolean hasSoundsToStart() {
        return this.extra.hasSoundsToStart() || this.footsteps.hasSoundsToStart() || this.vocals.hasSoundsToStart();
    }

    @Override
    public boolean isPlaying(long eventInstance) {
        return this.extra.isPlaying(eventInstance) || this.footsteps.isPlaying(eventInstance) || this.vocals.isPlaying(eventInstance);
    }

    @Override
    public boolean isPlaying(String alias) {
        return this.extra.isPlaying(alias) || this.footsteps.isPlaying(alias) || this.vocals.isPlaying(alias);
    }

    @Override
    public void setParameterValue(long soundRef, FMOD_STUDIO_PARAMETER_DESCRIPTION parameterDescription, float value) {
        this.extra.setParameterValue(soundRef, parameterDescription, value);
    }

    static enum footstep {
        upstairs,
        grass,
        wood,
        concrete,
        gravel,
        snow;
    }
}
