// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import fmod.javafmod;
import fmod.javafmodJNI;
import fmod.fmod.FMODFootstep;
import fmod.fmod.FMODManager;
import fmod.fmod.FMODSoundBank;
import fmod.fmod.FMODVoice;
import fmod.fmod.FMOD_STUDIO_EVENT_DESCRIPTION;
import fmod.fmod.FMOD_STUDIO_PLAYBACK_STATE;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import zombie.audio.BaseSoundBank;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.characters.IsoPlayer;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.config.DoubleConfigOption;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.GameSoundScript;
import zombie.util.StringUtils;

public final class GameSounds {
    public static final int VERSION = 1;
    protected static final HashMap<String, GameSound> soundByName = new HashMap<>();
    protected static final ArrayList<GameSound> sounds = new ArrayList<>();
    private static final GameSounds.BankPreviewSound previewBank = new GameSounds.BankPreviewSound();
    private static final GameSounds.FilePreviewSound previewFile = new GameSounds.FilePreviewSound();
    public static boolean soundIsPaused = false;
    private static GameSounds.IPreviewSound previewSound;

    public static void addSound(GameSound sound) {
        initClipEvents(sound);

        assert !sounds.contains(sound);

        int int0 = sounds.size();
        if (soundByName.containsKey(sound.getName())) {
            int0 = 0;

            while (int0 < sounds.size() && !sounds.get(int0).getName().equals(sound.getName())) {
                int0++;
            }

            sounds.remove(int0);
        }

        sounds.add(int0, sound);
        soundByName.put(sound.getName(), sound);
    }

    private static void initClipEvents(GameSound gameSound) {
        if (!GameServer.bServer) {
            for (GameSoundClip gameSoundClip : gameSound.clips) {
                if (gameSoundClip.event != null && gameSoundClip.eventDescription == null) {
                    gameSoundClip.eventDescription = FMODManager.instance.getEventDescription("event:/" + gameSoundClip.event);
                    if (gameSoundClip.eventDescription == null) {
                        DebugLog.Sound.warn("No such FMOD event \"%s\" for GameSound \"%s\"", gameSoundClip.event, gameSound.getName());
                    }

                    gameSoundClip.eventDescriptionMP = FMODManager.instance.getEventDescription("event:/Remote/" + gameSoundClip.event);
                    if (gameSoundClip.eventDescriptionMP != null) {
                        DebugLog.Sound.println("MP event %s", gameSoundClip.eventDescriptionMP.path);
                    }
                }
            }
        }
    }

    public static boolean isKnownSound(String name) {
        return soundByName.containsKey(name);
    }

    public static GameSound getSound(String name) {
        return getOrCreateSound(name);
    }

    public static GameSound getOrCreateSound(String name) {
        if (StringUtils.isNullOrEmpty(name)) {
            return null;
        } else {
            GameSound gameSound = soundByName.get(name);
            if (gameSound == null) {
                DebugLog.General.warn("no GameSound called \"" + name + "\", adding a new one");
                gameSound = new GameSound();
                gameSound.name = name;
                gameSound.category = "AUTO";
                GameSoundClip gameSoundClip = new GameSoundClip(gameSound);
                gameSound.clips.add(gameSoundClip);
                sounds.add(gameSound);
                soundByName.put(name.replace(".wav", "").replace(".ogg", ""), gameSound);
                if (BaseSoundBank.instance instanceof FMODSoundBank) {
                    FMOD_STUDIO_EVENT_DESCRIPTION fmod_studio_event_description = FMODManager.instance.getEventDescription("event:/" + name);
                    if (fmod_studio_event_description != null) {
                        gameSoundClip.event = name;
                        gameSoundClip.eventDescription = fmod_studio_event_description;
                        gameSoundClip.eventDescriptionMP = FMODManager.instance.getEventDescription("event:/Remote/" + name);
                    } else {
                        String string = null;
                        if (ZomboidFileSystem.instance.getAbsolutePath("media/sound/" + name + ".ogg") != null) {
                            string = "media/sound/" + name + ".ogg";
                        } else if (ZomboidFileSystem.instance.getAbsolutePath("media/sound/" + name + ".wav") != null) {
                            string = "media/sound/" + name + ".wav";
                        }

                        if (string != null) {
                            long long0 = FMODManager.instance.loadSound(string);
                            if (long0 != 0L) {
                                gameSoundClip.file = string;
                            }
                        }
                    }

                    if (gameSoundClip.event == null && gameSoundClip.file == null) {
                        DebugLog.General.warn("couldn't find an FMOD event or .ogg or .wav file for sound \"" + name + "\"");
                    }
                }
            }

            return gameSound;
        }
    }

    private static void loadNonBankSounds() {
        if (BaseSoundBank.instance instanceof FMODSoundBank) {
            for (GameSound gameSound : sounds) {
                for (GameSoundClip gameSoundClip : gameSound.clips) {
                    if (gameSoundClip.getFile() != null && gameSoundClip.getFile().isEmpty()) {
                    }
                }
            }
        }
    }

    public static void ScriptsLoaded() {
        ArrayList arrayList0 = ScriptManager.instance.getAllGameSounds();

        for (int int0 = 0; int0 < arrayList0.size(); int0++) {
            GameSoundScript gameSoundScript = (GameSoundScript)arrayList0.get(int0);
            if (!gameSoundScript.gameSound.clips.isEmpty()) {
                addSound(gameSoundScript.gameSound);
            }
        }

        arrayList0.clear();
        loadNonBankSounds();
        loadINI();
        if (Core.bDebug && BaseSoundBank.instance instanceof FMODSoundBank) {
            HashSet hashSet = new HashSet();

            for (GameSound gameSound : sounds) {
                for (GameSoundClip gameSoundClip : gameSound.clips) {
                    if (gameSoundClip.getEvent() != null && !gameSoundClip.getEvent().isEmpty()) {
                        hashSet.add(gameSoundClip.getEvent());
                    }
                }
            }

            FMODSoundBank fMODSoundBank = (FMODSoundBank)BaseSoundBank.instance;

            for (FMODFootstep fMODFootstep : fMODSoundBank.footstepMap.values()) {
                hashSet.add(fMODFootstep.wood);
                hashSet.add(fMODFootstep.concrete);
                hashSet.add(fMODFootstep.grass);
                hashSet.add(fMODFootstep.upstairs);
                hashSet.add(fMODFootstep.woodCreak);
            }

            for (FMODVoice fMODVoice : fMODSoundBank.voiceMap.values()) {
                hashSet.add(fMODVoice.sound);
            }

            ArrayList arrayList1 = new ArrayList();
            long[] longs0 = new long[32];
            long[] longs1 = new long[1024];
            int int1 = javafmodJNI.FMOD_Studio_System_GetBankList(longs0);

            for (int int2 = 0; int2 < int1; int2++) {
                int int3 = javafmodJNI.FMOD_Studio_Bank_GetEventList(longs0[int2], longs1);

                for (int int4 = 0; int4 < int3; int4++) {
                    try {
                        String string0 = javafmodJNI.FMOD_Studio_EventDescription_GetPath(longs1[int4]);
                        string0 = string0.replace("event:/", "");
                        if (!hashSet.contains(string0)) {
                            arrayList1.add(string0);
                        }
                    } catch (Exception exception) {
                        DebugLog.General.warn("FMOD cannot get path for " + longs1[int4] + " event");
                    }
                }
            }

            arrayList1.sort(String::compareTo);

            for (String string1 : arrayList1) {
                DebugLog.General.warn("FMOD event \"%s\" not used by any GameSound", string1);
            }
        }
    }

    public static void ReloadFile(String fileName) {
        try {
            ScriptManager.instance.LoadFile(fileName, true);
            ArrayList arrayList = ScriptManager.instance.getAllGameSounds();

            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                GameSoundScript gameSoundScript = (GameSoundScript)arrayList.get(int0);
                if (sounds.contains(gameSoundScript.gameSound)) {
                    initClipEvents(gameSoundScript.gameSound);
                } else if (!gameSoundScript.gameSound.clips.isEmpty()) {
                    addSound(gameSoundScript.gameSound);
                }
            }
        } catch (Throwable throwable) {
            ExceptionLogger.logException(throwable);
        }
    }

    public static ArrayList<String> getCategories() {
        HashSet hashSet = new HashSet();

        for (GameSound gameSound : sounds) {
            hashSet.add(gameSound.getCategory());
        }

        ArrayList arrayList = new ArrayList(hashSet);
        Collections.sort(arrayList);
        return arrayList;
    }

    public static ArrayList<GameSound> getSoundsInCategory(String category) {
        ArrayList arrayList = new ArrayList();

        for (GameSound gameSound : sounds) {
            if (gameSound.getCategory().equals(category)) {
                arrayList.add(gameSound);
            }
        }

        return arrayList;
    }

    public static void loadINI() {
        String string = ZomboidFileSystem.instance.getCacheDir() + File.separator + "sounds.ini";
        ConfigFile configFile = new ConfigFile();
        if (configFile.read(string)) {
            if (configFile.getVersion() <= 1) {
                for (ConfigOption configOption : configFile.getOptions()) {
                    GameSound gameSound = soundByName.get(configOption.getName());
                    if (gameSound != null) {
                        gameSound.setUserVolume(PZMath.tryParseFloat(configOption.getValueAsString(), 1.0F));
                    }
                }
            }
        }
    }

    public static void saveINI() {
        ArrayList arrayList = new ArrayList();

        for (GameSound gameSound : sounds) {
            DoubleConfigOption doubleConfigOption = new DoubleConfigOption(gameSound.getName(), 0.0, 2.0, 0.0);
            doubleConfigOption.setValue(gameSound.getUserVolume());
            arrayList.add(doubleConfigOption);
        }

        String string = ZomboidFileSystem.instance.getCacheDir() + File.separator + "sounds.ini";
        ConfigFile configFile = new ConfigFile();
        if (configFile.write(string, 1, arrayList)) {
            arrayList.clear();
        }
    }

    public static void previewSound(String name) {
        if (!Core.SoundDisabled) {
            if (isKnownSound(name)) {
                GameSound gameSound = getSound(name);
                if (gameSound == null) {
                    DebugLog.log("no such GameSound " + name);
                } else {
                    GameSoundClip gameSoundClip = gameSound.getRandomClip();
                    if (gameSoundClip == null) {
                        DebugLog.log("GameSound.clips is empty");
                    } else {
                        if (soundIsPaused) {
                            if (!GameClient.bClient) {
                                long long0 = javafmod.FMOD_System_GetMasterChannelGroup();
                                javafmod.FMOD_ChannelGroup_SetVolume(long0, 1.0F);
                            }

                            soundIsPaused = false;
                        }

                        if (previewSound != null) {
                            previewSound.stop();
                        }

                        if (gameSoundClip.getEvent() != null) {
                            if (previewBank.play(gameSoundClip)) {
                                previewSound = previewBank;
                            }
                        } else if (gameSoundClip.getFile() != null && previewFile.play(gameSoundClip)) {
                            previewSound = previewFile;
                        }
                    }
                }
            }
        }
    }

    public static void stopPreview() {
        if (previewSound != null) {
            previewSound.stop();
            previewSound = null;
        }
    }

    public static boolean isPreviewPlaying() {
        if (previewSound == null) {
            return false;
        } else if (previewSound.update()) {
            previewSound = null;
            return false;
        } else {
            return previewSound.isPlaying();
        }
    }

    public static void fix3DListenerPosition(boolean inMenu) {
        if (!Core.SoundDisabled) {
            if (inMenu) {
                javafmod.FMOD_Studio_Listener3D(0, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F);
            } else {
                for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                    IsoPlayer player = IsoPlayer.players[int0];
                    if (player != null && !player.Traits.Deaf.isSet()) {
                        javafmod.FMOD_Studio_Listener3D(
                            int0,
                            player.x,
                            player.y,
                            player.z * 3.0F,
                            0.0F,
                            0.0F,
                            0.0F,
                            -1.0F / (float)Math.sqrt(2.0),
                            -1.0F / (float)Math.sqrt(2.0),
                            0.0F,
                            0.0F,
                            0.0F,
                            1.0F
                        );
                    }
                }
            }
        }
    }

    public static void Reset() {
        sounds.clear();
        soundByName.clear();
        if (previewSound != null) {
            previewSound.stop();
            previewSound = null;
        }
    }

    private static final class BankPreviewSound implements GameSounds.IPreviewSound {
        long instance;
        GameSoundClip clip;
        float effectiveGain;

        @Override
        public boolean play(GameSoundClip arg0) {
            if (arg0.eventDescription == null) {
                DebugLog.log("failed to get event " + arg0.getEvent());
                return false;
            } else {
                this.instance = javafmod.FMOD_Studio_System_CreateEventInstance(arg0.eventDescription.address);
                if (this.instance < 0L) {
                    DebugLog.log("failed to create EventInstance: error=" + this.instance);
                    this.instance = 0L;
                    return false;
                } else {
                    this.clip = arg0;
                    this.effectiveGain = arg0.getEffectiveVolumeInMenu();
                    javafmod.FMOD_Studio_EventInstance_SetVolume(this.instance, this.effectiveGain);
                    javafmod.FMOD_Studio_EventInstance_SetParameterByName(this.instance, "Occlusion", 0.0F);
                    javafmod.FMOD_Studio_StartEvent(this.instance);
                    if (arg0.gameSound.master == GameSound.MasterVolume.Music) {
                        javafmod.FMOD_Studio_EventInstance_SetParameterByName(this.instance, "Volume", 10.0F);
                    }

                    return true;
                }
            }
        }

        @Override
        public boolean isPlaying() {
            if (this.instance == 0L) {
                return false;
            } else {
                int int0 = javafmod.FMOD_Studio_GetPlaybackState(this.instance);
                return int0 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPING.index
                    ? true
                    : int0 != FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPED.index;
            }
        }

        @Override
        public boolean update() {
            if (this.instance == 0L) {
                return false;
            } else {
                int int0 = javafmod.FMOD_Studio_GetPlaybackState(this.instance);
                if (int0 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPING.index) {
                    return false;
                } else if (int0 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPED.index) {
                    javafmod.FMOD_Studio_ReleaseEventInstance(this.instance);
                    this.instance = 0L;
                    this.clip = null;
                    return true;
                } else {
                    float float0 = this.clip.getEffectiveVolumeInMenu();
                    if (this.effectiveGain != float0) {
                        this.effectiveGain = float0;
                        javafmod.FMOD_Studio_EventInstance_SetVolume(this.instance, this.effectiveGain);
                    }

                    return false;
                }
            }
        }

        @Override
        public void stop() {
            if (this.instance != 0L) {
                javafmod.FMOD_Studio_EventInstance_Stop(this.instance, false);
                javafmod.FMOD_Studio_ReleaseEventInstance(this.instance);
                this.instance = 0L;
                this.clip = null;
            }
        }
    }

    private static final class FilePreviewSound implements GameSounds.IPreviewSound {
        long channel;
        GameSoundClip clip;
        float effectiveGain;

        @Override
        public boolean play(GameSoundClip arg0) {
            GameSound gameSound = arg0.gameSound;
            long long0 = FMODManager.instance.loadSound(arg0.getFile(), gameSound.isLooped());
            if (long0 == 0L) {
                return false;
            } else {
                this.channel = javafmod.FMOD_System_PlaySound(long0, true);
                this.clip = arg0;
                this.effectiveGain = arg0.getEffectiveVolumeInMenu();
                javafmod.FMOD_Channel_SetVolume(this.channel, this.effectiveGain);
                javafmod.FMOD_Channel_SetPitch(this.channel, arg0.pitch);
                if (gameSound.isLooped()) {
                    javafmod.FMOD_Channel_SetMode(this.channel, FMODManager.FMOD_LOOP_NORMAL);
                }

                javafmod.FMOD_Channel_SetPaused(this.channel, false);
                return true;
            }
        }

        @Override
        public boolean isPlaying() {
            return this.channel == 0L ? false : javafmod.FMOD_Channel_IsPlaying(this.channel);
        }

        @Override
        public boolean update() {
            if (this.channel == 0L) {
                return false;
            } else if (!javafmod.FMOD_Channel_IsPlaying(this.channel)) {
                this.channel = 0L;
                this.clip = null;
                return true;
            } else {
                float float0 = this.clip.getEffectiveVolumeInMenu();
                if (this.effectiveGain != float0) {
                    this.effectiveGain = float0;
                    javafmod.FMOD_Channel_SetVolume(this.channel, this.effectiveGain);
                }

                return false;
            }
        }

        @Override
        public void stop() {
            if (this.channel != 0L) {
                javafmod.FMOD_Channel_Stop(this.channel);
                this.channel = 0L;
                this.clip = null;
            }
        }
    }

    private interface IPreviewSound {
        boolean play(GameSoundClip arg0);

        boolean isPlaying();

        boolean update();

        void stop();
    }
}
