// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import fmod.javafmod;
import fmod.javafmodJNI;
import fmod.fmod.Audio;
import fmod.fmod.FMODAudio;
import fmod.fmod.FMODManager;
import fmod.fmod.FMODSoundEmitter;
import fmod.fmod.FMOD_STUDIO_EVENT_CALLBACK;
import fmod.fmod.FMOD_STUDIO_EVENT_CALLBACK_TYPE;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import fmod.fmod.FMOD_STUDIO_PLAYBACK_STATE;
import fmod.fmod.IFMODParameterUpdater;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.FMODParameter;
import zombie.audio.FMODParameterList;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.audio.parameters.ParameterMusicActionStyle;
import zombie.audio.parameters.ParameterMusicLibrary;
import zombie.audio.parameters.ParameterMusicState;
import zombie.audio.parameters.ParameterMusicWakeState;
import zombie.audio.parameters.ParameterMusicZombiesTargeting;
import zombie.audio.parameters.ParameterMusicZombiesVisible;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.gameStates.MainScreenState;
import zombie.input.GameKeyboard;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.ScriptModule;
import zombie.ui.TutorialManager;
import zombie.util.StringUtils;

public final class SoundManager extends BaseSoundManager implements IFMODParameterUpdater {
    public float SoundVolume = 0.8F;
    public float MusicVolume = 0.36F;
    public float AmbientVolume = 0.8F;
    public float VehicleEngineVolume = 0.5F;
    private final ParameterMusicActionStyle parameterMusicActionStyle = new ParameterMusicActionStyle();
    private final ParameterMusicLibrary parameterMusicLibrary = new ParameterMusicLibrary();
    private final ParameterMusicState parameterMusicState = new ParameterMusicState();
    private final ParameterMusicWakeState parameterMusicWakeState = new ParameterMusicWakeState();
    private final ParameterMusicZombiesTargeting parameterMusicZombiesTargeting = new ParameterMusicZombiesTargeting();
    private final ParameterMusicZombiesVisible parameterMusicZombiesVisible = new ParameterMusicZombiesVisible();
    private final FMODParameterList fmodParameters = new FMODParameterList();
    private boolean initialized = false;
    private long inGameGroupBus = 0L;
    private long musicGroupBus = 0L;
    private FMODSoundEmitter musicEmitter = null;
    private long musicCombinedEvent = 0L;
    private FMODSoundEmitter uiEmitter = null;
    private final SoundManager.Music music = new SoundManager.Music();
    public ArrayList<Audio> ambientPieces = new ArrayList<>();
    private boolean muted = false;
    private long[] bankList = new long[32];
    private long[] eventDescList = new long[256];
    private long[] eventInstList = new long[256];
    private long[] pausedEventInstances = new long[128];
    private float[] pausedEventVolumes = new float[128];
    private int pausedEventCount;
    private final HashSet<BaseSoundEmitter> emitters = new HashSet<>();
    private static ArrayList<SoundManager.AmbientSoundEffect> ambientSoundEffects = new ArrayList<>();
    public static BaseSoundManager instance;
    private String currentMusicName;
    private String currentMusicLibrary;
    private final FMOD_STUDIO_EVENT_CALLBACK musicEventCallback = new FMOD_STUDIO_EVENT_CALLBACK() {
        @Override
        public void timelineMarker(long var1, String string, int int0) {
            DebugLog.Sound.debugln("timelineMarker %s %d", string, int0);
            if ("Lightning".equals(string)) {
                MainScreenState.getInstance().lightningTimelineMarker = true;
            }
        }
    };

    @Override
    public FMODParameterList getFMODParameters() {
        return this.fmodParameters;
    }

    @Override
    public void startEvent(long eventInstance, GameSoundClip clip, BitSet parameterSet) {
        FMODParameterList fMODParameterList = this.getFMODParameters();
        ArrayList arrayList = clip.eventDescription.parameters;

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            FMOD_STUDIO_PARAMETER_DESCRIPTION fmod_studio_parameter_description = (FMOD_STUDIO_PARAMETER_DESCRIPTION)arrayList.get(int0);
            if (!parameterSet.get(fmod_studio_parameter_description.globalIndex)) {
                FMODParameter fMODParameter = fMODParameterList.get(fmod_studio_parameter_description);
                if (fMODParameter != null) {
                    fMODParameter.startEventInstance(eventInstance);
                }
            }
        }
    }

    @Override
    public void updateEvent(long eventInstance, GameSoundClip clip) {
    }

    @Override
    public void stopEvent(long eventInstance, GameSoundClip clip, BitSet parameterSet) {
        FMODParameterList fMODParameterList = this.getFMODParameters();
        ArrayList arrayList = clip.eventDescription.parameters;

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            FMOD_STUDIO_PARAMETER_DESCRIPTION fmod_studio_parameter_description = (FMOD_STUDIO_PARAMETER_DESCRIPTION)arrayList.get(int0);
            if (!parameterSet.get(fmod_studio_parameter_description.globalIndex)) {
                FMODParameter fMODParameter = fMODParameterList.get(fmod_studio_parameter_description);
                if (fMODParameter != null) {
                    fMODParameter.stopEventInstance(eventInstance);
                }
            }
        }
    }

    @Override
    public boolean isRemastered() {
        int int0 = Core.getInstance().getOptionMusicLibrary();
        return int0 == 1 || int0 == 3 && Rand.Next(2) == 0;
    }

    @Override
    public void BlendVolume(Audio audio, float targetVolume) {
    }

    @Override
    public void BlendVolume(Audio audio, float targetVolume, float blendSpeedAlpha) {
    }

    @Override
    public Audio BlendThenStart(Audio musicTrack, float f, String PrefMusic) {
        return null;
    }

    @Override
    public void FadeOutMusic(String name, int milli) {
    }

    @Override
    public void PlayAsMusic(String name, Audio musicTrack, float volume, boolean bloop) {
    }

    @Override
    public long playUISound(String name) {
        GameSound gameSound = GameSounds.getSound(name);
        if (gameSound != null && !gameSound.clips.isEmpty()) {
            GameSoundClip gameSoundClip = gameSound.getRandomClip();
            long long0 = this.uiEmitter.playClip(gameSoundClip, null);
            this.uiEmitter.tick();
            javafmod.FMOD_System_Update();
            return long0;
        } else {
            return 0L;
        }
    }

    @Override
    public boolean isPlayingUISound(String name) {
        return this.uiEmitter.isPlaying(name);
    }

    @Override
    public boolean isPlayingUISound(long eventInstance) {
        return this.uiEmitter.isPlaying(eventInstance);
    }

    @Override
    public void stopUISound(long eventInstance) {
        this.uiEmitter.stopSound(eventInstance);
    }

    @Override
    public boolean IsMusicPlaying() {
        return false;
    }

    @Override
    public boolean isPlayingMusic() {
        return this.music.isPlaying();
    }

    @Override
    public ArrayList<Audio> getAmbientPieces() {
        return this.ambientPieces;
    }

    private void gatherInGameEventInstances() {
        this.pausedEventCount = 0;
        int int0 = javafmodJNI.FMOD_Studio_System_GetBankCount();
        if (this.bankList.length < int0) {
            this.bankList = new long[int0];
        }

        int0 = javafmodJNI.FMOD_Studio_System_GetBankList(this.bankList);

        for (int int1 = 0; int1 < int0; int1++) {
            int int2 = javafmodJNI.FMOD_Studio_Bank_GetEventCount(this.bankList[int1]);
            if (this.eventDescList.length < int2) {
                this.eventDescList = new long[int2];
            }

            int2 = javafmodJNI.FMOD_Studio_Bank_GetEventList(this.bankList[int1], this.eventDescList);

            for (int int3 = 0; int3 < int2; int3++) {
                int int4 = javafmodJNI.FMOD_Studio_EventDescription_GetInstanceCount(this.eventDescList[int3]);
                if (this.eventInstList.length < int4) {
                    this.eventInstList = new long[int4];
                }

                int4 = javafmodJNI.FMOD_Studio_EventDescription_GetInstanceList(this.eventDescList[int3], this.eventInstList);

                for (int int5 = 0; int5 < int4; int5++) {
                    int int6 = javafmod.FMOD_Studio_GetPlaybackState(this.eventInstList[int5]);
                    if (int6 != FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPED.index) {
                        boolean boolean0 = javafmodJNI.FMOD_Studio_EventInstance_GetPaused(this.eventInstList[int5]);
                        if (!boolean0) {
                            if (this.pausedEventInstances.length < this.pausedEventCount + 1) {
                                this.pausedEventInstances = Arrays.copyOf(this.pausedEventInstances, this.pausedEventCount + 128);
                                this.pausedEventVolumes = Arrays.copyOf(this.pausedEventVolumes, this.pausedEventInstances.length);
                            }

                            this.pausedEventInstances[this.pausedEventCount] = this.eventInstList[int5];
                            this.pausedEventVolumes[this.pausedEventCount] = javafmodJNI.FMOD_Studio_EventInstance_GetVolume(this.eventInstList[int5]);
                            this.pausedEventCount++;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void pauseSoundAndMusic() {
        boolean boolean0 = true;
        if (GameClient.bClient) {
            this.muted = true;
            if (boolean0) {
                javafmod.FMOD_Studio_Bus_SetMute(this.inGameGroupBus, true);
                javafmod.FMOD_Studio_Bus_SetMute(this.musicGroupBus, true);
            } else {
                this.setSoundVolume(0.0F);
                this.setMusicVolume(0.0F);
                this.setAmbientVolume(0.0F);
                this.setVehicleEngineVolume(0.0F);
            }

            GameSounds.soundIsPaused = true;
        } else if (boolean0) {
            javafmod.FMOD_Studio_Bus_SetPaused(this.inGameGroupBus, true);
            javafmod.FMOD_Studio_Bus_SetPaused(this.musicGroupBus, true);
            javafmod.FMOD_Channel_SetPaused(FMODManager.instance.channelGroupInGameNonBankSounds, true);
            GameSounds.soundIsPaused = true;
        } else {
            long long0 = javafmod.FMOD_System_GetMasterChannelGroup();
            javafmod.FMOD_ChannelGroup_SetPaused(long0, true);
            javafmod.FMOD_ChannelGroup_SetVolume(long0, 0.0F);
            javafmodJNI.FMOD_Studio_System_FlushCommands();
            this.gatherInGameEventInstances();

            for (int int0 = 0; int0 < this.pausedEventCount; int0++) {
                javafmodJNI.FMOD_Studio_EventInstance_SetPaused(this.pausedEventInstances[int0], true);
            }

            javafmod.FMOD_Channel_SetPaused(FMODManager.instance.channelGroupInGameNonBankSounds, true);
            javafmod.FMOD_ChannelGroup_SetPaused(long0, false);
            javafmodJNI.FMOD_Studio_System_FlushCommands();
            javafmod.FMOD_ChannelGroup_SetVolume(long0, 1.0F);
            GameSounds.soundIsPaused = true;
        }
    }

    @Override
    public void resumeSoundAndMusic() {
        boolean boolean0 = true;
        if (this.muted) {
            this.muted = false;
            if (boolean0) {
                javafmod.FMOD_Studio_Bus_SetMute(this.inGameGroupBus, false);
                javafmod.FMOD_Studio_Bus_SetMute(this.musicGroupBus, false);
                javafmod.FMOD_ChannelGroup_SetPaused(FMODManager.instance.channelGroupInGameNonBankSounds, false);
            } else {
                this.setSoundVolume(Core.getInstance().getOptionSoundVolume() / 10.0F);
                this.setMusicVolume(Core.getInstance().getOptionMusicVolume() / 10.0F);
                this.setAmbientVolume(Core.getInstance().getOptionAmbientVolume() / 10.0F);
                this.setVehicleEngineVolume(Core.getInstance().getOptionVehicleEngineVolume() / 10.0F);
            }

            GameSounds.soundIsPaused = false;
        } else if (boolean0) {
            javafmod.FMOD_Studio_Bus_SetPaused(this.inGameGroupBus, false);
            javafmod.FMOD_Studio_Bus_SetPaused(this.musicGroupBus, false);
            javafmod.FMOD_ChannelGroup_SetPaused(FMODManager.instance.channelGroupInGameNonBankSounds, false);
            GameSounds.soundIsPaused = false;
        } else {
            long long0 = javafmod.FMOD_System_GetMasterChannelGroup();
            javafmod.FMOD_ChannelGroup_SetPaused(long0, true);
            javafmodJNI.FMOD_Studio_System_FlushCommands();

            for (int int0 = 0; int0 < this.pausedEventCount; int0++) {
                try {
                    javafmodJNI.FMOD_Studio_EventInstance_SetPaused(this.pausedEventInstances[int0], false);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            this.pausedEventCount = 0;
            javafmod.FMOD_ChannelGroup_SetPaused(long0, false);
            javafmod.FMOD_ChannelGroup_SetVolume(long0, 1.0F);
            javafmod.FMOD_ChannelGroup_SetPaused(FMODManager.instance.channelGroupInGameNonBankSounds, false);
            GameSounds.soundIsPaused = false;
        }
    }

    private void debugScriptSound(Item item, String string) {
        if (string != null && !string.isEmpty()) {
            if (!GameSounds.isKnownSound(string)) {
                DebugLog.General.warn("no such sound \"" + string + "\" in item " + item.getFullName());
            }
        }
    }

    @Override
    public void debugScriptSounds() {
        if (Core.bDebug) {
            for (ScriptModule scriptModule : ScriptManager.instance.ModuleMap.values()) {
                for (Item item : scriptModule.ItemMap.values()) {
                    this.debugScriptSound(item, item.getBreakSound());
                    this.debugScriptSound(item, item.getBulletOutSound());
                    this.debugScriptSound(item, item.getCloseSound());
                    this.debugScriptSound(item, item.getCustomEatSound());
                    this.debugScriptSound(item, item.getDoorHitSound());
                    this.debugScriptSound(item, item.getCountDownSound());
                    this.debugScriptSound(item, item.getExplosionSound());
                    this.debugScriptSound(item, item.getImpactSound());
                    this.debugScriptSound(item, item.getOpenSound());
                    this.debugScriptSound(item, item.getPutInSound());
                    this.debugScriptSound(item, item.getPlaceOneSound());
                    this.debugScriptSound(item, item.getPlaceMultipleSound());
                    this.debugScriptSound(item, item.getShellFallSound());
                    this.debugScriptSound(item, item.getSwingSound());
                    this.debugScriptSound(item, item.getInsertAmmoSound());
                    this.debugScriptSound(item, item.getInsertAmmoStartSound());
                    this.debugScriptSound(item, item.getInsertAmmoStopSound());
                    this.debugScriptSound(item, item.getEjectAmmoSound());
                    this.debugScriptSound(item, item.getEjectAmmoStartSound());
                    this.debugScriptSound(item, item.getEjectAmmoStopSound());
                }
            }
        }
    }

    @Override
    public void registerEmitter(BaseSoundEmitter emitter) {
        this.emitters.add(emitter);
    }

    @Override
    public void unregisterEmitter(BaseSoundEmitter emitter) {
        this.emitters.remove(emitter);
    }

    @Override
    public boolean isListenerInRange(float x, float y, float range) {
        if (GameServer.bServer) {
            return false;
        } else {
            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                IsoPlayer player = IsoPlayer.players[int0];
                if (player != null && !player.Traits.Deaf.isSet() && IsoUtils.DistanceToSquared(player.x, player.y, x, y) < range * range) {
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public void playNightAmbient(String choice) {
        DebugLog.log("playNightAmbient: " + choice);

        for (int int0 = 0; int0 < ambientSoundEffects.size(); int0++) {
            SoundManager.AmbientSoundEffect ambientSoundEffect0 = ambientSoundEffects.get(int0);
            if (ambientSoundEffect0.getName().equals(choice)) {
                ambientSoundEffect0.setVolume(Rand.Next(700, 1500) / 1000.0F);
                ambientSoundEffect0.start();
                this.ambientPieces.add(ambientSoundEffect0);
                return;
            }
        }

        SoundManager.AmbientSoundEffect ambientSoundEffect1 = new SoundManager.AmbientSoundEffect(choice);
        ambientSoundEffect1.setVolume(Rand.Next(700, 1500) / 1000.0F);
        ambientSoundEffect1.setName(choice);
        ambientSoundEffect1.start();
        this.ambientPieces.add(ambientSoundEffect1);
        ambientSoundEffects.add(ambientSoundEffect1);
    }

    @Override
    public void playMusic(String name) {
        this.DoMusic(name, false);
    }

    @Override
    public void playAmbient(String name) {
    }

    @Override
    public void playMusicNonTriggered(String name, float gain) {
    }

    @Override
    public void stopMusic(String name) {
        if (this.isPlayingMusic()) {
            if (StringUtils.isNullOrWhitespace(name) || name.equalsIgnoreCase(this.getCurrentMusicName())) {
                this.StopMusic();
            }
        }
    }

    @Override
    public void CheckDoMusic() {
    }

    @Override
    public float getMusicPosition() {
        return this.isPlayingMusic() ? this.music.getPosition() : 0.0F;
    }

    @Override
    public void DoMusic(String name, boolean bLoop) {
        if (this.AllowMusic && Core.getInstance().getOptionMusicVolume() != 0) {
            if (this.isPlayingMusic()) {
                this.StopMusic();
            }

            int int0 = Core.getInstance().getOptionMusicLibrary();
            boolean boolean0 = int0 == 1;
            GameSound gameSound = GameSounds.getSound(name);
            GameSoundClip gameSoundClip = null;
            if (gameSound != null && !gameSound.clips.isEmpty()) {
                gameSoundClip = gameSound.getRandomClip();
            }

            if (gameSoundClip != null && gameSoundClip.getEvent() != null) {
                if (gameSoundClip.eventDescription != null) {
                    long long0 = gameSoundClip.eventDescription.address;
                    javafmod.FMOD_Studio_LoadEventSampleData(long0);
                    this.music.instance = javafmod.FMOD_Studio_System_CreateEventInstance(long0);
                    this.music.clip = gameSoundClip;
                    this.music.effectiveVolume = gameSoundClip.getEffectiveVolume();
                    javafmod.FMOD_Studio_EventInstance_SetParameterByName(this.music.instance, "Volume", 10.0F);
                    javafmod.FMOD_Studio_EventInstance_SetVolume(this.music.instance, this.music.effectiveVolume);
                    javafmod.FMOD_Studio_StartEvent(this.music.instance);
                }
            } else if (gameSoundClip != null && gameSoundClip.getFile() != null) {
                long long1 = FMODManager.instance.loadSound(gameSoundClip.getFile());
                if (long1 > 0L) {
                    this.music.channel = javafmod.FMOD_System_PlaySound(long1, true);
                    this.music.clip = gameSoundClip;
                    this.music.effectiveVolume = gameSoundClip.getEffectiveVolume();
                    javafmod.FMOD_Channel_SetVolume(this.music.channel, this.music.effectiveVolume);
                    javafmod.FMOD_Channel_SetPitch(this.music.channel, gameSoundClip.pitch);
                    javafmod.FMOD_Channel_SetPaused(this.music.channel, false);
                }
            }

            this.currentMusicName = name;
            this.currentMusicLibrary = boolean0 ? "official" : "earlyaccess";
        }
    }

    @Override
    public void PlayAsMusic(String name, Audio musicTrack, boolean loop, float volume) {
    }

    @Override
    public void setMusicState(String stateName) {
        switch (stateName) {
            case "MainMenu":
                this.parameterMusicState.setState(ParameterMusicState.State.MainMenu);
                break;
            case "Loading":
                this.parameterMusicState.setState(ParameterMusicState.State.Loading);
                break;
            case "InGame":
                this.parameterMusicState.setState(ParameterMusicState.State.InGame);
                break;
            case "PauseMenu":
                this.parameterMusicState.setState(ParameterMusicState.State.PauseMenu);
                break;
            case "Tutorial":
                this.parameterMusicState.setState(ParameterMusicState.State.Tutorial);
                break;
            default:
                DebugLog.General.warn("unknown MusicState \"%s\"", stateName);
        }
    }

    @Override
    public void setMusicWakeState(IsoPlayer player, String stateName) {
        switch (stateName) {
            case "Awake":
                this.parameterMusicWakeState.setState(player, ParameterMusicWakeState.State.Awake);
                break;
            case "Sleeping":
                this.parameterMusicWakeState.setState(player, ParameterMusicWakeState.State.Sleeping);
                break;
            case "WakeNormal":
                this.parameterMusicWakeState.setState(player, ParameterMusicWakeState.State.WakeNormal);
                break;
            case "WakeNightmare":
                this.parameterMusicWakeState.setState(player, ParameterMusicWakeState.State.WakeNightmare);
                break;
            case "WakeZombies":
                this.parameterMusicWakeState.setState(player, ParameterMusicWakeState.State.WakeZombies);
                break;
            default:
                DebugLog.General.warn("unknown MusicWakeState \"%s\"", stateName);
        }
    }

    @Override
    public Audio PlayMusic(String n, String name, boolean loop, float maxGain) {
        return null;
    }

    @Override
    public Audio PlaySound(String name, boolean loop, float maxGain, float pitchVar) {
        return null;
    }

    @Override
    public Audio PlaySound(String name, boolean loop, float maxGain) {
        if (GameServer.bServer) {
            return null;
        } else if (IsoWorld.instance == null) {
            return null;
        } else {
            BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter();
            baseSoundEmitter.setPos(0.0F, 0.0F, 0.0F);
            long long0 = baseSoundEmitter.playSound(name);
            return long0 != 0L ? new FMODAudio(baseSoundEmitter) : null;
        }
    }

    @Override
    public Audio PlaySoundEvenSilent(String name, boolean loop, float maxGain) {
        return null;
    }

    @Override
    public Audio PlayJukeboxSound(String name, boolean loop, float maxGain) {
        return null;
    }

    @Override
    public Audio PlaySoundWav(String name, boolean loop, float maxGain, float pitchVar) {
        return null;
    }

    @Override
    public Audio PlaySoundWav(String name, boolean loop, float maxGain) {
        return null;
    }

    @Override
    public Audio PlaySoundWav(String name, int variations, boolean loop, float maxGain) {
        return null;
    }

    @Override
    public void update3D() {
    }

    @Override
    public Audio PlayWorldSound(String name, IsoGridSquare source, float pitchVar, float radius, float maxGain, boolean ignoreOutside) {
        return this.PlayWorldSound(name, false, source, pitchVar, radius, maxGain, ignoreOutside);
    }

    @Override
    public Audio PlayWorldSound(String name, boolean loop, IsoGridSquare source, float pitchVar, float radius, float maxGain, boolean ignoreOutside) {
        if (!GameServer.bServer && source != null) {
            if (GameClient.bClient) {
                GameClient.instance.PlayWorldSound(name, source.x, source.y, (byte)source.z);
            }

            return this.PlayWorldSoundImpl(name, loop, source.getX(), source.getY(), source.getZ(), pitchVar, radius, maxGain, ignoreOutside);
        } else {
            return null;
        }
    }

    @Override
    public Audio PlayWorldSoundImpl(String name, boolean loop, int sx, int sy, int sz, float pitchVar, float radius, float maxGain, boolean ignoreOutside) {
        BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter(sx + 0.5F, sy + 0.5F, sz);
        long long0 = baseSoundEmitter.playSoundImpl(name, (IsoObject)null);
        return new FMODAudio(baseSoundEmitter);
    }

    @Override
    public Audio PlayWorldSound(String name, IsoGridSquare source, float pitchVar, float radius, float maxGain, int choices, boolean ignoreOutside) {
        return this.PlayWorldSound(name, source, pitchVar, radius, maxGain, ignoreOutside);
    }

    @Override
    public Audio PlayWorldSoundWav(String name, IsoGridSquare source, float pitchVar, float radius, float maxGain, boolean ignoreOutside) {
        return this.PlayWorldSoundWav(name, false, source, pitchVar, radius, maxGain, ignoreOutside);
    }

    @Override
    public Audio PlayWorldSoundWav(String name, boolean loop, IsoGridSquare source, float pitchVar, float radius, float maxGain, boolean ignoreOutside) {
        if (!GameServer.bServer && source != null) {
            if (GameClient.bClient) {
                GameClient.instance.PlayWorldSound(name, source.getX(), source.getY(), (byte)source.getZ());
            }

            return this.PlayWorldSoundWavImpl(name, loop, source, pitchVar, radius, maxGain, ignoreOutside);
        } else {
            return null;
        }
    }

    @Override
    public Audio PlayWorldSoundWavImpl(String name, boolean loop, IsoGridSquare source, float pitchVar, float radius, float maxGain, boolean ignoreOutside) {
        BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter(source.getX() + 0.5F, source.getY() + 0.5F, source.getZ());
        baseSoundEmitter.playSound(name);
        return new FMODAudio(baseSoundEmitter);
    }

    @Override
    public void PlayWorldSoundWav(String name, IsoGridSquare source, float pitchVar, float radius, float maxGain, int choices, boolean ignoreOutside) {
        Integer integer = Rand.Next(choices) + 1;
        this.PlayWorldSoundWav(name + integer.toString(), source, pitchVar, radius, maxGain, ignoreOutside);
    }

    @Override
    public Audio PrepareMusic(String name) {
        return null;
    }

    @Override
    public Audio Start(Audio musicTrack, float f, String PrefMusic) {
        return null;
    }

    @Override
    public void Update() {
        if (!this.initialized) {
            this.initialized = true;
            this.inGameGroupBus = javafmod.FMOD_Studio_System_GetBus("bus:/InGame");
            this.musicGroupBus = javafmod.FMOD_Studio_System_GetBus("bus:/Music");
            this.musicEmitter = new FMODSoundEmitter();
            this.unregisterEmitter(this.musicEmitter);
            this.musicEmitter.parameterUpdater = this;
            this.fmodParameters.add(this.parameterMusicActionStyle);
            this.fmodParameters.add(this.parameterMusicLibrary);
            this.fmodParameters.add(this.parameterMusicState);
            this.fmodParameters.add(this.parameterMusicWakeState);
            this.fmodParameters.add(this.parameterMusicZombiesTargeting);
            this.fmodParameters.add(this.parameterMusicZombiesVisible);
            this.uiEmitter = new FMODSoundEmitter();
        }

        FMODSoundEmitter.update();
        this.updateMusic();
        this.uiEmitter.tick();

        for (int int0 = 0; int0 < this.ambientPieces.size(); int0++) {
            Audio audio = this.ambientPieces.get(int0);
            if (IsoPlayer.allPlayersDead()) {
                audio.stop();
            }

            if (!audio.isPlaying()) {
                audio.stop();
                this.ambientPieces.remove(audio);
                int0--;
            } else if (audio instanceof SoundManager.AmbientSoundEffect) {
                ((SoundManager.AmbientSoundEffect)audio).update();
            }
        }

        AmbientStreamManager.instance.update();
        if (!this.AllowMusic) {
            this.StopMusic();
        }

        if (this.music.isPlaying()) {
            this.music.update();
        }

        FMODManager.instance.tick();
    }

    @Override
    protected boolean HasMusic(Audio var1) {
        return false;
    }

    @Override
    public void Purge() {
    }

    @Override
    public void stop() {
        for (BaseSoundEmitter baseSoundEmitter : this.emitters) {
            baseSoundEmitter.stopAll();
        }

        this.emitters.clear();
        long long0 = javafmod.FMOD_System_GetMasterChannelGroup();
        javafmod.FMOD_ChannelGroup_Stop(long0);
        this.pausedEventCount = 0;
    }

    @Override
    public void StopMusic() {
        this.music.stop();
    }

    @Override
    public void StopSound(Audio SoundEffect) {
        SoundEffect.stop();
    }

    @Override
    public void CacheSound(String file) {
    }

    @Override
    public void update4() {
    }

    @Override
    public void update2() {
    }

    @Override
    public void update3() {
    }

    @Override
    public void update1() {
    }

    @Override
    public void setSoundVolume(float volume) {
        this.SoundVolume = volume;
    }

    @Override
    public float getSoundVolume() {
        return this.SoundVolume;
    }

    @Override
    public void setAmbientVolume(float volume) {
        this.AmbientVolume = volume;
    }

    @Override
    public float getAmbientVolume() {
        return this.AmbientVolume;
    }

    @Override
    public void setMusicVolume(float volume) {
        this.MusicVolume = volume;
        if (!this.muted) {
            ;
        }
    }

    @Override
    public float getMusicVolume() {
        return this.MusicVolume;
    }

    @Override
    public void setVehicleEngineVolume(float volume) {
        this.VehicleEngineVolume = volume;
    }

    @Override
    public float getVehicleEngineVolume() {
        return this.VehicleEngineVolume;
    }

    @Override
    public String getCurrentMusicName() {
        return this.isPlayingMusic() ? this.currentMusicName : null;
    }

    @Override
    public String getCurrentMusicLibrary() {
        return this.isPlayingMusic() ? this.currentMusicLibrary : null;
    }

    private void updateMusic() {
        this.fmodParameters.update();
        if (GameKeyboard.isKeyPressed(Core.getInstance().getKey("Toggle Music"))) {
            this.AllowMusic = !this.AllowMusic;
            if (!this.AllowMusic) {
                this.StopMusic();
                TutorialManager.instance.PrefMusic = null;
            }
        }

        if (!this.musicEmitter.isPlaying(this.musicCombinedEvent)) {
            this.musicCombinedEvent = this.musicEmitter.playSoundImpl("MusicCombined", (IsoObject)null);
            if (this.musicCombinedEvent != 0L && !System.getProperty("os.name").contains("OS X")) {
                javafmod.FMOD_Studio_EventInstance_SetCallback(
                    this.musicCombinedEvent, this.musicEventCallback, FMOD_STUDIO_EVENT_CALLBACK_TYPE.FMOD_STUDIO_EVENT_CALLBACK_TIMELINE_MARKER.bit
                );
            }
        }

        if (this.musicEmitter.isPlaying(this.musicCombinedEvent)) {
            this.musicEmitter.setVolume(this.musicCombinedEvent, this.AllowMusic ? 1.0F : 0.0F);
        }

        this.musicEmitter.tick();
    }

    public static final class AmbientSoundEffect implements Audio {
        public String name;
        public long eventInstance;
        public float gain;
        public GameSoundClip clip;
        public float effectiveVolume;

        public AmbientSoundEffect(String _name) {
            GameSound gameSound = GameSounds.getSound(_name);
            if (gameSound != null && !gameSound.clips.isEmpty()) {
                GameSoundClip gameSoundClip = gameSound.getRandomClip();
                if (gameSoundClip.getEvent() != null) {
                    if (gameSoundClip.eventDescription != null) {
                        this.eventInstance = javafmod.FMOD_Studio_System_CreateEventInstance(gameSoundClip.eventDescription.address);
                        if (this.eventInstance >= 0L) {
                            this.clip = gameSoundClip;
                        }
                    }
                }
            }
        }

        @Override
        public void setVolume(float volume) {
            if (this.eventInstance > 0L) {
                this.gain = volume;
                this.effectiveVolume = this.clip.getEffectiveVolume();
                javafmod.FMOD_Studio_EventInstance_SetVolume(this.eventInstance, this.gain * this.effectiveVolume);
            }
        }

        @Override
        public void start() {
            if (this.eventInstance > 0L) {
                javafmod.FMOD_Studio_StartEvent(this.eventInstance);
            }
        }

        @Override
        public void pause() {
        }

        @Override
        public void stop() {
            DebugLog.log("stop ambient " + this.name);
            if (this.eventInstance > 0L) {
                javafmod.FMOD_Studio_EventInstance_Stop(this.eventInstance, false);
            }
        }

        @Override
        public boolean isPlaying() {
            if (this.eventInstance <= 0L) {
                return false;
            } else {
                int int0 = javafmod.FMOD_Studio_GetPlaybackState(this.eventInstance);
                return int0 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STARTING.index
                    || int0 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_PLAYING.index
                    || int0 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_SUSTAINING.index;
            }
        }

        @Override
        public void setName(String choice) {
            this.name = choice;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public void update() {
            if (this.clip != null) {
                this.clip = this.clip.checkReloaded();
                float float0 = this.clip.getEffectiveVolume();
                if (this.effectiveVolume != float0) {
                    this.effectiveVolume = float0;
                    javafmod.FMOD_Studio_EventInstance_SetVolume(this.eventInstance, this.gain * this.effectiveVolume);
                }
            }
        }
    }

    private static final class Music {
        public GameSoundClip clip;
        public long instance;
        public long channel;
        public long sound;
        public float effectiveVolume;

        public boolean isPlaying() {
            if (this.instance != 0L) {
                int int0 = javafmod.FMOD_Studio_GetPlaybackState(this.instance);
                return int0 != FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPED.index
                    && int0 != FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPING.index;
            } else {
                return this.channel != 0L && javafmod.FMOD_Channel_IsPlaying(this.channel);
            }
        }

        public void update() {
            this.clip = this.clip.checkReloaded();
            float float0 = this.clip.getEffectiveVolume();
            if (this.effectiveVolume != float0) {
                this.effectiveVolume = float0;
                if (this.instance != 0L) {
                    javafmod.FMOD_Studio_EventInstance_SetVolume(this.instance, this.effectiveVolume);
                }

                if (this.channel != 0L) {
                    javafmod.FMOD_Channel_SetVolume(this.channel, this.effectiveVolume);
                }
            }
        }

        public float getPosition() {
            if (this.instance != 0L) {
                long long0 = javafmod.FMOD_Studio_GetTimelinePosition(this.instance);
                return (float)long0;
            } else if (this.channel != 0L) {
                long long1 = javafmod.FMOD_Channel_GetPosition(this.channel, FMODManager.FMOD_TIMEUNIT_MS);
                return (float)long1;
            } else {
                return 0.0F;
            }
        }

        public void stop() {
            if (this.instance != 0L) {
                javafmod.FMOD_Studio_EventInstance_Stop(this.instance, false);
                javafmod.FMOD_Studio_ReleaseEventInstance(this.instance);
                this.instance = 0L;
            }

            if (this.channel != 0L) {
                javafmod.FMOD_Channel_Stop(this.channel);
                this.channel = 0L;
                javafmod.FMOD_Sound_Release(this.sound);
                this.sound = 0L;
            }
        }
    }
}
