// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.raknet;

import fmod.FMODSoundBuffer;
import fmod.FMOD_DriverInfo;
import fmod.FMOD_RESULT;
import fmod.javafmod;
import fmod.javafmodJNI;
import fmod.fmod.FMODManager;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.debug.DebugLog;
import zombie.debug.LogSeverity;
import zombie.input.GameKeyboard;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.Radio;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.network.FakeClientManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.MPStatistics;
import zombie.network.PacketTypes;
import zombie.network.ServerOptions;
import zombie.radio.devices.DeviceData;
import zombie.vehicles.VehiclePart;

public class VoiceManager {
    private static final int FMOD_SOUND_MODE = FMODManager.FMOD_OPENUSER | FMODManager.FMOD_LOOP_NORMAL | FMODManager.FMOD_CREATESTREAM;
    public static final int modePPT = 1;
    public static final int modeVAD = 2;
    public static final int modeMute = 3;
    public static final int VADModeQuality = 1;
    public static final int VADModeLowBitrate = 2;
    public static final int VADModeAggressive = 3;
    public static final int VADModeVeryAggressive = 4;
    public static final int AGCModeAdaptiveAnalog = 1;
    public static final int AGCModeAdaptiveDigital = 2;
    public static final int AGCModeFixedDigital = 3;
    private static final int bufferSize = 192;
    private static final int complexity = 1;
    private static boolean serverVOIPEnable = true;
    private static int sampleRate = 16000;
    private static int period = 300;
    private static int buffering = 8000;
    private static float minDistance;
    private static float maxDistance;
    private static boolean is3D = false;
    private boolean isEnable = true;
    private boolean isModeVAD = false;
    private boolean isModePPT = false;
    private int vadMode = 3;
    private int agcMode = 2;
    private int volumeMic;
    private int volumePlayers;
    public static boolean VoipDisabled = false;
    private boolean isServer;
    private static FMODSoundBuffer FMODReceiveBuffer;
    private int FMODVoiceRecordDriverId;
    private long FMODChannelGroup = 0L;
    private long FMODRecordSound = 0L;
    private Semaphore recDevSemaphore;
    private boolean initialiseRecDev = false;
    private boolean initialisedRecDev = false;
    private long indicatorIsVoice = 0L;
    private Thread thread;
    private boolean bQuit;
    private long timeLast;
    private boolean isDebug = false;
    private boolean isDebugLoopback = false;
    private boolean isDebugLoopbackLong = false;
    public static VoiceManager instance = new VoiceManager();
    byte[] buf = new byte[192];
    private final Object notifier = new Object();
    private boolean bIsClient = false;
    private boolean bTestingMicrophone = false;
    private long testingMicrophoneMS = 0L;
    private final Long recBuf_Current_read = new Long(0L);
    private static long timestamp = 0L;

    public static VoiceManager getInstance() {
        return instance;
    }

    public void DeinitRecSound() {
        this.initialisedRecDev = false;
        if (this.FMODRecordSound != 0L) {
            javafmod.FMOD_RecordSound_Release(this.FMODRecordSound);
            this.FMODRecordSound = 0L;
        }

        FMODReceiveBuffer = null;
    }

    public void ResetRecSound() {
        if (this.initialisedRecDev && this.FMODRecordSound != 0L) {
            int int0 = javafmod.FMOD_System_RecordStop(this.FMODVoiceRecordDriverId);
            if (int0 != FMOD_RESULT.FMOD_OK.ordinal()) {
                DebugLog.Voice.warn("FMOD_System_RecordStop result=%d", int0);
            }
        }

        this.DeinitRecSound();
        this.FMODRecordSound = javafmod.FMOD_System_CreateRecordSound(
            this.FMODVoiceRecordDriverId,
            FMODManager.FMOD_2D | FMODManager.FMOD_OPENUSER | FMODManager.FMOD_SOFTWARE,
            FMODManager.FMOD_SOUND_FORMAT_PCM16,
            sampleRate,
            this.agcMode
        );
        if (this.FMODRecordSound == 0L) {
            DebugLog.Voice.warn("FMOD_System_CreateSound result=%d", this.FMODRecordSound);
        }

        javafmod.FMOD_System_SetRecordVolume(1L - Math.round(Math.pow(1.4, 11 - this.volumeMic)));
        if (this.initialiseRecDev) {
            int int1 = javafmod.FMOD_System_RecordStart(this.FMODVoiceRecordDriverId, this.FMODRecordSound, true);
            if (int1 != FMOD_RESULT.FMOD_OK.ordinal()) {
                DebugLog.Voice.warn("FMOD_System_RecordStart result=%d", int1);
            }
        }

        javafmod.FMOD_System_SetVADMode(this.vadMode - 1);
        FMODReceiveBuffer = new FMODSoundBuffer(this.FMODRecordSound);
        this.initialisedRecDev = true;
    }

    public void VoiceRestartClient(boolean boolean0) {
        if (GameClient.connection != null) {
            if (boolean0) {
                this.loadConfig();
                this.VoiceConnectReq(GameClient.connection.getConnectedGUID());
            } else {
                this.threadSafeCode(this::DeinitRecSound);
                this.VoiceConnectClose(GameClient.connection.getConnectedGUID());
                this.loadConfig();
            }
        } else {
            this.loadConfig();
            if (boolean0) {
                this.InitRecDeviceForTest();
            } else {
                this.threadSafeCode(this::DeinitRecSound);
            }
        }
    }

    void VoiceInitClient() {
        this.isServer = false;
        this.recDevSemaphore = new Semaphore(1);
        FMODReceiveBuffer = null;
        RakVoice.RVInit(192);
        RakVoice.SetComplexity(1);
    }

    void VoiceInitServer(boolean boolean0, int int1, int int0, int int2, int int3, double double1, double double0, boolean boolean1) {
        this.isServer = true;
        if (!(int0 == 2 | int0 == 5 | int0 == 10 | int0 == 20 | int0 == 40 | int0 == 60)) {
            DebugLog.Voice.error("Invalid period=%d", int0);
        } else if (!(int1 == 8000 | int1 == 16000 | int1 == 24000)) {
            DebugLog.Voice.error("Invalid sample rate=%d", int1);
        } else if (int2 < 0 | int2 > 10) {
            DebugLog.Voice.error("Invalid quality=%d", int2);
        } else if (int3 < 0 | int3 > 32000) {
            DebugLog.Voice.error("Invalid buffering=%d", int3);
        } else {
            sampleRate = int1;
            RakVoice.RVInitServer(boolean0, int1, int0, int2, int3, (float)double1, (float)double0, boolean1);
        }
    }

    void VoiceConnectAccept(long long0) {
        if (this.isEnable) {
            DebugLog.Voice.debugln("uuid=%x", long0);
        }
    }

    void InitRecDeviceForTest() {
        this.threadSafeCode(this::ResetRecSound);
    }

    void VoiceOpenChannelReply(long long0, ByteBuffer byteBuffer) {
        if (this.isEnable) {
            DebugLog.Voice.debugln("uuid=%d", long0);
            if (this.isServer) {
                return;
            }

            try {
                if (GameClient.bClient) {
                    serverVOIPEnable = byteBuffer.getInt() != 0;
                    sampleRate = byteBuffer.getInt();
                    period = byteBuffer.getInt();
                    byteBuffer.getInt();
                    buffering = byteBuffer.getInt();
                    minDistance = byteBuffer.getFloat();
                    maxDistance = byteBuffer.getFloat();
                    is3D = byteBuffer.getInt() != 0;
                } else {
                    serverVOIPEnable = RakVoice.GetServerVOIPEnable();
                    sampleRate = RakVoice.GetSampleRate();
                    period = RakVoice.GetSendFramePeriod();
                    buffering = RakVoice.GetBuffering();
                    minDistance = RakVoice.GetMinDistance();
                    maxDistance = RakVoice.GetMaxDistance();
                    is3D = RakVoice.GetIs3D();
                }
            } catch (Exception exception) {
                DebugLog.Voice.printException(exception, "RakVoice params set failed", LogSeverity.Error);
                return;
            }

            DebugLog.Voice
                .debugln(
                    "enabled=%b, sample-rate=%d, period=%d, complexity=%d, buffering=%d, is3D=%b", serverVOIPEnable, sampleRate, period, 1, buffering, is3D
                );

            try {
                this.recDevSemaphore.acquire();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }

            int int0 = is3D ? FMODManager.FMOD_3D | FMOD_SOUND_MODE : FMOD_SOUND_MODE;

            for (VoiceManagerData voiceManagerData : VoiceManagerData.data) {
                if (voiceManagerData.userplaysound != 0L) {
                    javafmod.FMOD_Sound_SetMode(voiceManagerData.userplaysound, int0);
                }
            }

            long long1 = javafmod.FMOD_System_SetRawPlayBufferingPeriod(buffering);
            if (long1 != FMOD_RESULT.FMOD_OK.ordinal()) {
                DebugLog.Voice.warn("FMOD_System_SetRawPlayBufferingPeriod result=%d", long1);
            }

            this.ResetRecSound();
            this.recDevSemaphore.release();
            if (this.isDebug) {
                VoiceDebug.createAndShowGui();
            }
        }
    }

    public void VoiceConnectReq(long long0) {
        if (this.isEnable) {
            DebugLog.Voice.debugln("uuid=%x", long0);
            VoiceManagerData.data.clear();
            RakVoice.RequestVoiceChannel(long0);
        }
    }

    public void VoiceConnectClose(long long0) {
        if (this.isEnable) {
            DebugLog.Voice.debugln("uuid=%x", long0);
            RakVoice.CloseVoiceChannel(long0);
        }
    }

    public void setMode(int int0) {
        if (int0 == 3) {
            this.isModeVAD = false;
            this.isModePPT = false;
        } else if (int0 == 1) {
            this.isModeVAD = false;
            this.isModePPT = true;
        } else if (int0 == 2) {
            this.isModeVAD = true;
            this.isModePPT = false;
        }
    }

    public void setVADMode(int int0) {
        if (!(int0 < 1 | int0 > 4)) {
            this.vadMode = int0;
            if (this.initialisedRecDev) {
                this.threadSafeCode(() -> javafmod.FMOD_System_SetVADMode(this.vadMode - 1));
            }
        }
    }

    public void setAGCMode(int int0) {
        if (!(int0 < 1 | int0 > 3)) {
            this.agcMode = int0;
            if (this.initialisedRecDev) {
                this.threadSafeCode(this::ResetRecSound);
            }
        }
    }

    public void setVolumePlayers(int int0) {
        if (!(int0 < 0 | int0 > 11)) {
            if (int0 <= 10) {
                this.volumePlayers = int0;
            } else {
                this.volumePlayers = 12;
            }

            if (this.initialisedRecDev) {
                ArrayList arrayList = VoiceManagerData.data;

                for (int int1 = 0; int1 < arrayList.size(); int1++) {
                    VoiceManagerData voiceManagerData = (VoiceManagerData)arrayList.get(int1);
                    if (voiceManagerData != null && voiceManagerData.userplaychannel != 0L) {
                        javafmod.FMOD_Channel_SetVolume(voiceManagerData.userplaychannel, (float)(this.volumePlayers * 0.2));
                    }
                }
            }
        }
    }

    public void setVolumeMic(int int0) {
        if (!(int0 < 0 | int0 > 11)) {
            if (int0 <= 10) {
                this.volumeMic = int0;
            } else {
                this.volumeMic = 12;
            }

            if (this.initialisedRecDev) {
                this.threadSafeCode(() -> javafmod.FMOD_System_SetRecordVolume(1L - Math.round(Math.pow(1.4, 11 - this.volumeMic))));
            }
        }
    }

    public static void playerSetMute(String string) {
        ArrayList arrayList = GameClient.instance.getPlayers();

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            IsoPlayer player = (IsoPlayer)arrayList.get(int0);
            if (string.equals(player.username)) {
                VoiceManagerData voiceManagerData = VoiceManagerData.get(player.OnlineID);
                voiceManagerData.userplaymute = !voiceManagerData.userplaymute;
                player.isVoiceMute = voiceManagerData.userplaymute;
                break;
            }
        }
    }

    public static boolean playerGetMute(String string) {
        ArrayList arrayList = GameClient.instance.getPlayers();

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            IsoPlayer player = (IsoPlayer)arrayList.get(int0);
            if (string.equals(player.username)) {
                return VoiceManagerData.get(player.OnlineID).userplaymute;
            }
        }

        return true;
    }

    public void LuaRegister(Platform platform, KahluaTable table1) {
        KahluaTable table0 = platform.newTable();
        table0.rawset("playerSetMute", new JavaFunction() {
            @Override
            public int call(LuaCallFrame luaCallFrame, int var2) {
                Object object = luaCallFrame.get(1);
                VoiceManager.playerSetMute((String)object);
                return 1;
            }
        });
        table0.rawset("playerGetMute", new JavaFunction() {
            @Override
            public int call(LuaCallFrame luaCallFrame, int var2) {
                Object object = luaCallFrame.get(1);
                luaCallFrame.push(VoiceManager.playerGetMute((String)object));
                return 1;
            }
        });
        table0.rawset("RecordDevices", new JavaFunction() {
            @Override
            public int call(LuaCallFrame luaCallFrame, int var2) {
                if (!Core.SoundDisabled && !VoiceManager.VoipDisabled) {
                    int int0 = javafmod.FMOD_System_GetRecordNumDrivers();
                    KahluaTable table0 = luaCallFrame.getPlatform().newTable();

                    for (int int1 = 0; int1 < int0; int1++) {
                        FMOD_DriverInfo fMOD_DriverInfo = new FMOD_DriverInfo();
                        javafmod.FMOD_System_GetRecordDriverInfo(int1, fMOD_DriverInfo);
                        table0.rawset(int1 + 1, fMOD_DriverInfo.name);
                    }

                    luaCallFrame.push(table0);
                    return 1;
                } else {
                    KahluaTable table1 = luaCallFrame.getPlatform().newTable();
                    luaCallFrame.push(table1);
                    return 1;
                }
            }
        });
        table1.rawset("VoiceManager", table0);
    }

    private void setUserPlaySound(long long0, float float0) {
        float0 = IsoUtils.clamp(float0 * IsoUtils.lerp(this.volumePlayers, 0.0F, 12.0F), 0.0F, 1.0F);
        javafmod.FMOD_Channel_SetVolume(long0, float0);
    }

    private long getUserPlaySound(short short0) {
        VoiceManagerData voiceManagerData = VoiceManagerData.get(short0);
        if (voiceManagerData.userplaychannel == 0L) {
            voiceManagerData.userplaysound = 0L;
            int int0 = is3D ? FMODManager.FMOD_3D | FMOD_SOUND_MODE : FMOD_SOUND_MODE;
            voiceManagerData.userplaysound = javafmod.FMOD_System_CreateRAWPlaySound(int0, FMODManager.FMOD_SOUND_FORMAT_PCM16, sampleRate);
            if (voiceManagerData.userplaysound == 0L) {
                DebugLog.Voice.warn("FMOD_System_CreateSound result=%d", voiceManagerData.userplaysound);
            }

            voiceManagerData.userplaychannel = javafmod.FMOD_System_PlaySound(voiceManagerData.userplaysound, false);
            if (voiceManagerData.userplaychannel == 0L) {
                DebugLog.Voice.warn("FMOD_System_PlaySound result=%d", voiceManagerData.userplaychannel);
            }

            javafmod.FMOD_Channel_SetVolume(voiceManagerData.userplaychannel, (float)(this.volumePlayers * 0.2));
            if (is3D) {
                javafmod.FMOD_Channel_Set3DMinMaxDistance(voiceManagerData.userplaychannel, minDistance / 2.0F, maxDistance);
            }

            javafmod.FMOD_Channel_SetChannelGroup(voiceManagerData.userplaychannel, this.FMODChannelGroup);
        }

        return voiceManagerData.userplaysound;
    }

    public void InitVMClient() {
        if (!Core.SoundDisabled && !VoipDisabled) {
            int int0 = javafmod.FMOD_System_GetRecordNumDrivers();
            this.FMODVoiceRecordDriverId = Core.getInstance().getOptionVoiceRecordDevice() - 1;
            if (this.FMODVoiceRecordDriverId < 0 && int0 > 0) {
                Core.getInstance().setOptionVoiceRecordDevice(1);
                this.FMODVoiceRecordDriverId = Core.getInstance().getOptionVoiceRecordDevice() - 1;
            }

            if (int0 < 1) {
                DebugLog.Voice.debugln("Microphone not found");
                this.initialiseRecDev = false;
            } else if (this.FMODVoiceRecordDriverId < 0 | this.FMODVoiceRecordDriverId >= int0) {
                DebugLog.Voice.warn("Invalid record device");
                this.initialiseRecDev = false;
            } else {
                this.initialiseRecDev = true;
            }

            this.isEnable = Core.getInstance().getOptionVoiceEnable();
            this.setMode(Core.getInstance().getOptionVoiceMode());
            this.vadMode = Core.getInstance().getOptionVoiceVADMode();
            this.volumeMic = Core.getInstance().getOptionVoiceVolumeMic();
            this.volumePlayers = Core.getInstance().getOptionVoiceVolumePlayers();
            this.FMODChannelGroup = javafmod.FMOD_System_CreateChannelGroup("VOIP");
            this.VoiceInitClient();
            this.FMODRecordSound = 0L;
            if (this.isEnable) {
                this.InitRecDeviceForTest();
            }

            if (this.isDebug) {
                VoiceDebug.createAndShowGui();
            }

            this.timeLast = System.currentTimeMillis();
            this.bQuit = false;
            this.thread = new Thread() {
                @Override
                public void run() {
                    while (!VoiceManager.this.bQuit) {
                        try {
                            VoiceManager.this.UpdateVMClient();
                            sleep(VoiceManager.period / 2);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            };
            this.thread.setName("VoiceManagerClient");
            this.thread.start();
        } else {
            this.isEnable = false;
            this.initialiseRecDev = false;
            this.initialisedRecDev = false;
            DebugLog.Voice.debugln("Disabled");
        }
    }

    public void loadConfig() {
        this.isEnable = Core.getInstance().getOptionVoiceEnable();
        this.setMode(Core.getInstance().getOptionVoiceMode());
        this.vadMode = Core.getInstance().getOptionVoiceVADMode();
        this.volumeMic = Core.getInstance().getOptionVoiceVolumeMic();
        this.volumePlayers = Core.getInstance().getOptionVoiceVolumePlayers();
    }

    public void UpdateRecordDevice() {
        if (this.initialisedRecDev) {
            this.threadSafeCode(this::UpdateRecordDeviceInternal);
        }
    }

    private void UpdateRecordDeviceInternal() {
        int int0 = javafmod.FMOD_System_RecordStop(this.FMODVoiceRecordDriverId);
        if (int0 != FMOD_RESULT.FMOD_OK.ordinal()) {
            DebugLog.Voice.warn("FMOD_System_RecordStop result=%d", int0);
        }

        this.FMODVoiceRecordDriverId = Core.getInstance().getOptionVoiceRecordDevice() - 1;
        if (this.FMODVoiceRecordDriverId < 0) {
            DebugLog.Voice.error("No record device found");
        } else {
            int0 = javafmod.FMOD_System_RecordStart(this.FMODVoiceRecordDriverId, this.FMODRecordSound, true);
            if (int0 != FMOD_RESULT.FMOD_OK.ordinal()) {
                DebugLog.Voice.warn("FMOD_System_RecordStart result=%d", int0);
            }
        }
    }

    public void DeinitVMClient() {
        if (this.thread != null) {
            this.bQuit = true;
            synchronized (this.notifier) {
                this.notifier.notify();
            }

            while (this.thread.isAlive()) {
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException interruptedException) {
                }
            }

            this.thread = null;
        }

        this.DeinitRecSound();
        ArrayList arrayList = VoiceManagerData.data;

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            VoiceManagerData voiceManagerData = (VoiceManagerData)arrayList.get(int0);
            if (voiceManagerData.userplaychannel != 0L) {
                javafmod.FMOD_Channel_Stop(voiceManagerData.userplaychannel);
            }

            if (voiceManagerData.userplaysound != 0L) {
                javafmod.FMOD_RAWPlaySound_Release(voiceManagerData.userplaysound);
                voiceManagerData.userplaysound = 0L;
            }
        }

        VoiceManagerData.data.clear();
    }

    public void setTestingMicrophone(boolean boolean0) {
        if (boolean0) {
            this.testingMicrophoneMS = System.currentTimeMillis();
        }

        if (boolean0 != this.bTestingMicrophone) {
            this.bTestingMicrophone = boolean0;
            this.notifyThread();
        }
    }

    public void notifyThread() {
        synchronized (this.notifier) {
            this.notifier.notify();
        }
    }

    public void update() {
        if (!GameServer.bServer) {
            if (this.bTestingMicrophone) {
                long long0 = System.currentTimeMillis();
                if (long0 - this.testingMicrophoneMS > 1000L) {
                    this.setTestingMicrophone(false);
                }
            }

            if ((!GameClient.bClient || GameClient.connection == null) && !FakeClientManager.isVOIPEnabled()) {
                if (this.bIsClient) {
                    this.bIsClient = false;
                    this.notifyThread();
                }
            } else if (!this.bIsClient) {
                this.bIsClient = true;
                this.notifyThread();
            }
        }
    }

    private float getCanHearAllVolume(float float0) {
        return float0 > minDistance ? IsoUtils.clamp(1.0F - IsoUtils.lerp(float0, minDistance, maxDistance), 0.2F, 1.0F) : 1.0F;
    }

    private void threadSafeCode(Runnable runnable) {
        while (true) {
            try {
                this.recDevSemaphore.acquire();
            } catch (InterruptedException interruptedException) {
                continue;
            }

            try {
                runnable.run();
            } finally {
                this.recDevSemaphore.release();
            }

            return;
        }
    }

    synchronized void UpdateVMClient() throws InterruptedException {
        while (!this.bQuit && !this.bIsClient && !this.bTestingMicrophone) {
            synchronized (this.notifier) {
                try {
                    this.notifier.wait();
                } catch (InterruptedException interruptedException) {
                }
            }
        }

        if (serverVOIPEnable) {
            if (IsoPlayer.getInstance() != null) {
                IsoPlayer.getInstance().isSpeek = System.currentTimeMillis() - this.indicatorIsVoice <= 300L;
            }

            if (this.initialiseRecDev) {
                this.recDevSemaphore.acquire();
                javafmod.FMOD_System_GetRecordPosition(this.FMODVoiceRecordDriverId, this.recBuf_Current_read);
                if (FMODReceiveBuffer != null) {
                    while (FMODReceiveBuffer.pull(this.recBuf_Current_read)) {
                        if (IsoPlayer.getInstance() != null && GameClient.connection != null || FakeClientManager.isVOIPEnabled()) {
                            if (is3D && IsoPlayer.getInstance().isDead()) {
                                continue;
                            }

                            if (this.isModePPT) {
                                if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Enable voice transmit"))) {
                                    RakVoice.SendFrame(
                                        GameClient.connection.connectedGUID,
                                        IsoPlayer.getInstance().OnlineID,
                                        FMODReceiveBuffer.buf(),
                                        FMODReceiveBuffer.get_size()
                                    );
                                    this.indicatorIsVoice = System.currentTimeMillis();
                                } else if (FakeClientManager.isVOIPEnabled()) {
                                    RakVoice.SendFrame(
                                        FakeClientManager.getConnectedGUID(),
                                        FakeClientManager.getOnlineID(),
                                        FMODReceiveBuffer.buf(),
                                        FMODReceiveBuffer.get_size()
                                    );
                                    this.indicatorIsVoice = System.currentTimeMillis();
                                }
                            }

                            if (this.isModeVAD && FMODReceiveBuffer.get_vad() != 0L) {
                                RakVoice.SendFrame(
                                    GameClient.connection.connectedGUID,
                                    IsoPlayer.getInstance().OnlineID,
                                    FMODReceiveBuffer.buf(),
                                    FMODReceiveBuffer.get_size()
                                );
                                this.indicatorIsVoice = System.currentTimeMillis();
                            }
                        }

                        if (this.isDebug) {
                            if (GameClient.IDToPlayerMap.values().size() > 0) {
                                VoiceDebug.updateGui(null, FMODReceiveBuffer);
                            } else if (this.isDebugLoopback) {
                                VoiceDebug.updateGui(null, FMODReceiveBuffer);
                            } else {
                                VoiceDebug.updateGui(null, FMODReceiveBuffer);
                            }
                        }

                        if (this.isDebugLoopback) {
                            javafmod.FMOD_System_RAWPlayData(this.getUserPlaySound((short)0), FMODReceiveBuffer.buf(), FMODReceiveBuffer.get_size());
                        }
                    }
                }

                this.recDevSemaphore.release();
            }

            ArrayList arrayList0 = GameClient.instance.getPlayers();
            ArrayList arrayList1 = VoiceManagerData.data;

            for (int int0 = 0; int0 < arrayList1.size(); int0++) {
                VoiceManagerData voiceManagerData0 = (VoiceManagerData)arrayList1.get(int0);
                boolean boolean0 = false;

                for (int int1 = 0; int1 < arrayList0.size(); int1++) {
                    IsoPlayer player0 = (IsoPlayer)arrayList0.get(int1);
                    if (player0.OnlineID == voiceManagerData0.index) {
                        boolean0 = true;
                        break;
                    }
                }

                if (this.isDebugLoopback & voiceManagerData0.index == 0) {
                    break;
                }

                if (voiceManagerData0.userplaychannel != 0L & !boolean0) {
                    javafmod.FMOD_Channel_Stop(voiceManagerData0.userplaychannel);
                    voiceManagerData0.userplaychannel = 0L;
                }
            }

            long long0 = System.currentTimeMillis() - this.timeLast;
            if (long0 >= period) {
                this.timeLast += long0;
                if (IsoPlayer.getInstance() == null) {
                    return;
                }

                VoiceManagerData.VoiceDataSource voiceDataSource = VoiceManagerData.VoiceDataSource.Unknown;
                int int2 = 0;

                for (IsoPlayer player1 : arrayList0) {
                    IsoPlayer player2 = IsoPlayer.getInstance();
                    if (player1 != player2 && player1.getOnlineID() != -1) {
                        VoiceManagerData voiceManagerData1 = VoiceManagerData.get(player1.getOnlineID());

                        while (RakVoice.ReceiveFrame(player1.getOnlineID(), this.buf)) {
                            voiceManagerData1.voicetimeout = 10L;
                            if (!voiceManagerData1.userplaymute) {
                                float float0 = IsoUtils.DistanceTo(player2.getX(), player2.getY(), player1.getX(), player1.getY());
                                if (player2.isCanHearAll()) {
                                    javafmodJNI.FMOD_Channel_Set3DLevel(voiceManagerData1.userplaychannel, 0.0F);
                                    javafmod.FMOD_Channel_Set3DAttributes(voiceManagerData1.userplaychannel, player2.x, player2.y, player2.z, 0.0F, 0.0F, 0.0F);
                                    this.setUserPlaySound(voiceManagerData1.userplaychannel, this.getCanHearAllVolume(float0));
                                    voiceDataSource = VoiceManagerData.VoiceDataSource.Cheat;
                                    int2 = 0;
                                } else {
                                    VoiceManagerData.RadioData radioData = this.checkForNearbyRadios(voiceManagerData1);
                                    if (radioData != null && radioData.deviceData != null) {
                                        javafmodJNI.FMOD_Channel_Set3DLevel(voiceManagerData1.userplaychannel, 0.0F);
                                        javafmod.FMOD_Channel_Set3DAttributes(
                                            voiceManagerData1.userplaychannel, player2.x, player2.y, player2.z, 0.0F, 0.0F, 0.0F
                                        );
                                        this.setUserPlaySound(voiceManagerData1.userplaychannel, radioData.deviceData.getDeviceVolume());
                                        radioData.deviceData.doReceiveMPSignal(radioData.lastReceiveDistance);
                                        voiceDataSource = VoiceManagerData.VoiceDataSource.Radio;
                                        int2 = radioData.freq;
                                    } else {
                                        if (radioData == null) {
                                            javafmodJNI.FMOD_Channel_Set3DLevel(voiceManagerData1.userplaychannel, 0.0F);
                                            javafmod.FMOD_Channel_Set3DAttributes(
                                                voiceManagerData1.userplaychannel, player2.x, player2.y, player2.z, 0.0F, 0.0F, 0.0F
                                            );
                                            javafmod.FMOD_Channel_SetVolume(voiceManagerData1.userplaychannel, 0.0F);
                                            voiceDataSource = VoiceManagerData.VoiceDataSource.Unknown;
                                        } else {
                                            if (is3D) {
                                                javafmodJNI.FMOD_Channel_Set3DLevel(voiceManagerData1.userplaychannel, IsoUtils.lerp(float0, 0.0F, minDistance));
                                                javafmod.FMOD_Channel_Set3DAttributes(
                                                    voiceManagerData1.userplaychannel, player1.x, player1.y, player1.z, 0.0F, 0.0F, 0.0F
                                                );
                                            } else {
                                                javafmodJNI.FMOD_Channel_Set3DLevel(voiceManagerData1.userplaychannel, 0.0F);
                                                javafmod.FMOD_Channel_Set3DAttributes(
                                                    voiceManagerData1.userplaychannel, player2.x, player2.y, player2.z, 0.0F, 0.0F, 0.0F
                                                );
                                            }

                                            this.setUserPlaySound(
                                                voiceManagerData1.userplaychannel, IsoUtils.smoothstep(maxDistance, minDistance, radioData.lastReceiveDistance)
                                            );
                                            voiceDataSource = VoiceManagerData.VoiceDataSource.Voice;
                                        }

                                        int2 = 0;
                                        if (float0 > maxDistance) {
                                            logFrame(player2, player1, float0);
                                        }
                                    }
                                }

                                javafmod.FMOD_System_RAWPlayData(this.getUserPlaySound(player1.getOnlineID()), this.buf, (long)this.buf.length);
                                if (this.isDebugLoopbackLong) {
                                    RakVoice.SendFrame(GameClient.connection.connectedGUID, player2.getOnlineID(), this.buf, this.buf.length);
                                }
                            }
                        }

                        if (voiceManagerData1.voicetimeout == 0L) {
                            player1.isSpeek = false;
                        } else {
                            voiceManagerData1.voicetimeout--;
                            player1.isSpeek = true;
                        }
                    }
                }

                MPStatistics.setVOIPSource(voiceDataSource, int2);
            }
        }
    }

    private static void logFrame(IsoPlayer player1, IsoPlayer player0, float float0) {
        long long0 = System.currentTimeMillis();
        if (long0 > timestamp) {
            timestamp = long0 + 5000L;
            DebugLog.Multiplayer
                .warn(
                    String.format(
                        "\"%s\" (%b) received VOIP frame from \"%s\" (%b) at distance=%f",
                        player1.getUsername(),
                        player1.isCanHearAll(),
                        player0.getUsername(),
                        player0.isCanHearAll(),
                        float0
                    )
                );
        }
    }

    private VoiceManagerData.RadioData checkForNearbyRadios(VoiceManagerData voiceManagerData1) {
        IsoPlayer player = IsoPlayer.getInstance();
        VoiceManagerData voiceManagerData0 = VoiceManagerData.get(player.OnlineID);
        if (voiceManagerData0.isCanHearAll) {
            voiceManagerData0.radioData.get(0).lastReceiveDistance = 0.0F;
            return voiceManagerData0.radioData.get(0);
        } else {
            synchronized (voiceManagerData0.radioData) {
                for (int int0 = 1; int0 < voiceManagerData0.radioData.size(); int0++) {
                    synchronized (voiceManagerData1.radioData) {
                        for (int int1 = 1; int1 < voiceManagerData1.radioData.size(); int1++) {
                            if (voiceManagerData0.radioData.get(int0).freq == voiceManagerData1.radioData.get(int1).freq) {
                                float float0 = voiceManagerData0.radioData.get(int0).x - voiceManagerData1.radioData.get(int1).x;
                                float float1 = voiceManagerData0.radioData.get(int0).y - voiceManagerData1.radioData.get(int1).y;
                                voiceManagerData0.radioData.get(int0).lastReceiveDistance = (float)Math.sqrt(float0 * float0 + float1 * float1);
                                if (voiceManagerData0.radioData.get(int0).lastReceiveDistance < voiceManagerData1.radioData.get(int1).distance) {
                                    return voiceManagerData0.radioData.get(int0);
                                }
                            }
                        }
                    }
                }
            }

            synchronized (voiceManagerData0.radioData) {
                synchronized (voiceManagerData1.radioData) {
                    if (!voiceManagerData1.radioData.isEmpty() && !voiceManagerData0.radioData.isEmpty()) {
                        float float2 = voiceManagerData0.radioData.get(0).x - voiceManagerData1.radioData.get(0).x;
                        float float3 = voiceManagerData0.radioData.get(0).y - voiceManagerData1.radioData.get(0).y;
                        voiceManagerData0.radioData.get(0).lastReceiveDistance = (float)Math.sqrt(float2 * float2 + float3 * float3);
                        if (voiceManagerData0.radioData.get(0).lastReceiveDistance < voiceManagerData1.radioData.get(0).distance) {
                            return voiceManagerData0.radioData.get(0);
                        }
                    }
                }

                return null;
            }
        }
    }

    public void UpdateChannelsRoaming(UdpConnection udpConnection) {
        IsoPlayer player0 = IsoPlayer.getInstance();
        if (player0.OnlineID != -1) {
            VoiceManagerData voiceManagerData = VoiceManagerData.get(player0.OnlineID);
            boolean boolean0 = false;
            synchronized (voiceManagerData.radioData) {
                voiceManagerData.radioData.clear();

                for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                    IsoPlayer player1 = IsoPlayer.players[int0];
                    if (player1 != null) {
                        boolean0 |= player1.isCanHearAll();
                        voiceManagerData.radioData.add(new VoiceManagerData.RadioData(RakVoice.GetMaxDistance(), player1.x, player1.y));

                        for (int int1 = 0; int1 < player1.getInventory().getItems().size(); int1++) {
                            InventoryItem item = player1.getInventory().getItems().get(int1);
                            if (item instanceof Radio) {
                                DeviceData deviceData0 = ((Radio)item).getDeviceData();
                                if (deviceData0 != null && deviceData0.getIsTurnedOn()) {
                                    voiceManagerData.radioData.add(new VoiceManagerData.RadioData(deviceData0, player1.x, player1.y));
                                }
                            }
                        }

                        for (int int2 = (int)player1.getX() - 4; int2 < player1.getX() + 5.0F; int2++) {
                            for (int int3 = (int)player1.getY() - 4; int3 < player1.getY() + 5.0F; int3++) {
                                for (int int4 = (int)player1.getZ() - 1; int4 < player1.getZ() + 1.0F; int4++) {
                                    IsoGridSquare square = IsoCell.getInstance().getGridSquare(int2, int3, int4);
                                    if (square != null) {
                                        if (square.getObjects() != null) {
                                            for (int int5 = 0; int5 < square.getObjects().size(); int5++) {
                                                IsoObject object = square.getObjects().get(int5);
                                                if (object instanceof IsoRadio) {
                                                    DeviceData deviceData1 = ((IsoRadio)object).getDeviceData();
                                                    if (deviceData1 != null && deviceData1.getIsTurnedOn()) {
                                                        voiceManagerData.radioData.add(new VoiceManagerData.RadioData(deviceData1, square.x, square.y));
                                                    }
                                                }
                                            }
                                        }

                                        if (square.getWorldObjects() != null) {
                                            for (int int6 = 0; int6 < square.getWorldObjects().size(); int6++) {
                                                IsoWorldInventoryObject worldInventoryObject = square.getWorldObjects().get(int6);
                                                if (worldInventoryObject.getItem() != null && worldInventoryObject.getItem() instanceof Radio) {
                                                    DeviceData deviceData2 = ((Radio)worldInventoryObject.getItem()).getDeviceData();
                                                    if (deviceData2 != null && deviceData2.getIsTurnedOn()) {
                                                        voiceManagerData.radioData.add(new VoiceManagerData.RadioData(deviceData2, square.x, square.y));
                                                    }
                                                }
                                            }
                                        }

                                        if (square.getVehicleContainer() != null && square == square.getVehicleContainer().getSquare()) {
                                            VehiclePart part = square.getVehicleContainer().getPartById("Radio");
                                            if (part != null) {
                                                DeviceData deviceData3 = part.getDeviceData();
                                                if (deviceData3 != null && deviceData3.getIsTurnedOn()) {
                                                    voiceManagerData.radioData.add(new VoiceManagerData.RadioData(deviceData3, square.x, square.y));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
            PacketTypes.PacketType.SyncRadioData.doPacket(byteBufferWriter);
            byteBufferWriter.putByte((byte)(boolean0 ? 1 : 0));
            byteBufferWriter.putInt(voiceManagerData.radioData.size() * 4);

            for (VoiceManagerData.RadioData radioData : voiceManagerData.radioData) {
                byteBufferWriter.putInt(radioData.freq);
                byteBufferWriter.putInt((int)radioData.distance);
                byteBufferWriter.putInt(radioData.x);
                byteBufferWriter.putInt(radioData.y);
            }

            PacketTypes.PacketType.SyncRadioData.send(udpConnection);
        }
    }

    void InitVMServer() {
        this.VoiceInitServer(
            ServerOptions.instance.VoiceEnable.getValue(),
            24000,
            20,
            5,
            8000,
            ServerOptions.instance.VoiceMinDistance.getValue(),
            ServerOptions.instance.VoiceMaxDistance.getValue(),
            ServerOptions.instance.Voice3D.getValue()
        );
    }

    public int getMicVolumeIndicator() {
        return FMODReceiveBuffer == null ? 0 : (int)FMODReceiveBuffer.get_loudness();
    }

    public boolean getMicVolumeError() {
        return FMODReceiveBuffer == null ? true : FMODReceiveBuffer.get_interror();
    }

    public boolean getServerVOIPEnable() {
        return serverVOIPEnable;
    }

    public void VMServerBan(short short0, boolean boolean0) {
        RakVoice.SetVoiceBan(short0, boolean0);
    }
}
