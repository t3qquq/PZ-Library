// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatElement;
import zombie.chat.ChatMessage;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.VoiceManagerData;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.types.Radio;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.radio.StorySounds.SLSoundManager;
import zombie.radio.devices.DeviceData;
import zombie.radio.devices.WaveSignalDevice;
import zombie.radio.media.RecordedMedia;
import zombie.radio.scripting.RadioChannel;
import zombie.radio.scripting.RadioScript;
import zombie.radio.scripting.RadioScriptManager;

public final class ZomboidRadio {
    public static final String SAVE_FILE = "RADIO_SAVE.txt";
    private final ArrayList<WaveSignalDevice> devices = new ArrayList<>();
    private final ArrayList<WaveSignalDevice> broadcastDevices = new ArrayList<>();
    private RadioScriptManager scriptManager;
    private int DaysSinceStart = 0;
    private int lastRecordedHour;
    private final String[] playerLastLine = new String[4];
    private final Map<Integer, String> channelNames = new HashMap<>();
    private final Map<String, Map<Integer, String>> categorizedChannels = new HashMap<>();
    private final List<Integer> knownFrequencies = new ArrayList<>();
    private RadioDebugConsole debugConsole;
    private boolean hasRecievedServerData = false;
    private SLSoundManager storySoundManager = null;
    private static final String[] staticSounds = new String[]{"<bzzt>", "<fzzt>", "<wzzt>", "<szzt>"};
    public static boolean DEBUG_MODE = false;
    public static boolean DEBUG_XML = false;
    public static boolean DEBUG_SOUND = false;
    public static boolean POST_RADIO_SILENCE = false;
    public static boolean DISABLE_BROADCASTING = false;
    private static ZomboidRadio instance;
    private static RecordedMedia recordedMedia;
    public static boolean LOUISVILLE_OBFUSCATION = false;
    private String lastSaveFile;
    private String lastSaveContent;
    private HashMap<Integer, ZomboidRadio.FreqListEntry> freqlist = new HashMap<>();
    private boolean hasAppliedRangeDistortion = false;
    private StringBuilder stringBuilder = new StringBuilder();
    private boolean hasAppliedInterference = false;
    private static int[] obfuscateChannels = new int[]{200, 201, 204, 93200, 98000, 101200};

    public static boolean hasInstance() {
        return instance != null;
    }

    public static ZomboidRadio getInstance() {
        if (instance == null) {
            instance = new ZomboidRadio();
        }

        return instance;
    }

    private ZomboidRadio() {
        this.lastRecordedHour = GameTime.instance.getHour();
        SLSoundManager.DEBUG = DEBUG_SOUND;

        for (int int0 = 0; int0 < staticSounds.length; int0++) {
            ChatElement.addNoLogText(staticSounds[int0]);
        }

        ChatElement.addNoLogText("~");
        recordedMedia = new RecordedMedia();
    }

    public static boolean isStaticSound(String str) {
        if (str != null) {
            for (String string : staticSounds) {
                if (str.equals(string)) {
                    return true;
                }
            }
        }

        return false;
    }

    public RadioScriptManager getScriptManager() {
        return this.scriptManager;
    }

    public int getDaysSinceStart() {
        return this.DaysSinceStart;
    }

    public ArrayList<WaveSignalDevice> getDevices() {
        return this.devices;
    }

    public ArrayList<WaveSignalDevice> getBroadcastDevices() {
        return this.broadcastDevices;
    }

    public void setHasRecievedServerData(boolean state) {
        this.hasRecievedServerData = state;
    }

    public void addChannelName(String name, int frequency, String category) {
        this.addChannelName(name, frequency, category, true);
    }

    public void addChannelName(String name, int frequency, String category, boolean overwrite) {
        if (overwrite || !this.channelNames.containsKey(frequency)) {
            if (!this.categorizedChannels.containsKey(category)) {
                this.categorizedChannels.put(category, new HashMap<>());
            }

            this.categorizedChannels.get(category).put(frequency, name);
            this.channelNames.put(frequency, name);
            this.knownFrequencies.add(frequency);
        }
    }

    public void removeChannelName(int frequency) {
        if (this.channelNames.containsKey(frequency)) {
            this.channelNames.remove(frequency);

            for (Entry entry : this.categorizedChannels.entrySet()) {
                if (((Map)entry.getValue()).containsKey(frequency)) {
                    ((Map)entry.getValue()).remove(frequency);
                }
            }
        }
    }

    public Map<Integer, String> GetChannelList(String category) {
        return this.categorizedChannels.containsKey(category) ? this.categorizedChannels.get(category) : null;
    }

    public String getChannelName(int frequency) {
        return this.channelNames.containsKey(frequency) ? this.channelNames.get(frequency) : null;
    }

    public int getRandomFrequency() {
        return this.getRandomFrequency(88000, 108000);
    }

    public int getRandomFrequency(int rangemin, int rangemax) {
        int int0 = 91100;

        do {
            int0 = Rand.Next(rangemin, rangemax);
            int0 /= 200;
            int0 *= 200;
        } while (this.knownFrequencies.contains(int0));

        return int0;
    }

    public Map<String, Map<Integer, String>> getFullChannelList() {
        return this.categorizedChannels;
    }

    public void WriteRadioServerDataPacket(ByteBufferWriter bb) {
        bb.putInt(this.categorizedChannels.size());

        for (Entry entry0 : this.categorizedChannels.entrySet()) {
            GameWindow.WriteString(bb.bb, (String)entry0.getKey());
            bb.putInt(((Map)entry0.getValue()).size());

            for (Entry entry1 : ((Map)entry0.getValue()).entrySet()) {
                bb.putInt((Integer)entry1.getKey());
                GameWindow.WriteString(bb.bb, (String)entry1.getValue());
            }
        }

        bb.putByte((byte)(POST_RADIO_SILENCE ? 1 : 0));
    }

    public void Init(int savedWorldVersion) {
        POST_RADIO_SILENCE = false;
        boolean boolean0 = false;
        boolean boolean1 = DebugLog.isEnabled(DebugType.Radio);
        if (boolean1) {
            DebugLog.Radio.println("");
            DebugLog.Radio.println("################## Radio Init ##################");
        }

        RadioAPI.getInstance();
        recordedMedia.init();
        this.lastRecordedHour = GameTime.instance.getHour();
        GameMode gameMode = this.getGameMode();
        if (DEBUG_MODE && !gameMode.equals(GameMode.Server)) {
            DebugLog.setLogEnabled(DebugType.Radio, true);
            this.debugConsole = new RadioDebugConsole();
        }

        if (gameMode.equals(GameMode.Client)) {
            GameClient.sendRadioServerDataRequest();
            if (boolean1) {
                DebugLog.Radio.println("Radio (Client) loaded.");
                DebugLog.Radio.println("################################################");
            }

            this.scriptManager = null;
        } else {
            this.scriptManager = RadioScriptManager.getInstance();
            this.scriptManager.init(savedWorldVersion);

            try {
                if (!Core.getInstance().isNoSave()) {
                    ZomboidFileSystem.instance.getFileInCurrentSave("radio", "data").mkdirs();
                }

                for (RadioData radioData : RadioData.fetchAllRadioData()) {
                    for (RadioChannel radioChannel0 : radioData.getRadioChannels()) {
                        ObfuscateChannelCheck(radioChannel0);
                        RadioChannel radioChannel1 = null;
                        if (this.scriptManager.getChannels().containsKey(radioChannel0.GetFrequency())) {
                            radioChannel1 = this.scriptManager.getChannels().get(radioChannel0.GetFrequency());
                        }

                        if (radioChannel1 != null && (!radioChannel1.getRadioData().isVanilla() || radioChannel0.getRadioData().isVanilla())) {
                            if (boolean1) {
                                DebugLog.Radio
                                    .println("Unable to add channel: " + radioChannel0.GetName() + ", frequency '" + radioChannel0.GetFrequency() + "' taken.");
                            }
                        } else {
                            this.scriptManager.AddChannel(radioChannel0, true);
                        }
                    }
                }

                LuaEventManager.triggerEvent("OnLoadRadioScripts", this.scriptManager, savedWorldVersion == -1);
                if (savedWorldVersion == -1) {
                    if (boolean1) {
                        DebugLog.Radio.println("Radio setting new game start times");
                    }

                    SandboxOptions sandboxOptions0 = SandboxOptions.instance;
                    int int0 = sandboxOptions0.TimeSinceApo.getValue() - 1;
                    if (int0 < 0) {
                        int0 = 0;
                    }

                    if (boolean1) {
                        DebugLog.log(DebugType.Radio, "Time since the apocalypse: " + sandboxOptions0.TimeSinceApo.getValue());
                    }

                    if (int0 > 0) {
                        this.DaysSinceStart = (int)(int0 * 30.5F);
                        if (boolean1) {
                            DebugLog.Radio.println("Time since the apocalypse in days: " + this.DaysSinceStart);
                        }

                        this.scriptManager.simulateScriptsUntil(this.DaysSinceStart, true);
                    }

                    this.checkGameModeSpecificStart();
                } else {
                    boolean boolean2 = this.Load();
                    if (!boolean2) {
                        SandboxOptions sandboxOptions1 = SandboxOptions.instance;
                        int int1 = sandboxOptions1.TimeSinceApo.getValue() - 1;
                        if (int1 < 0) {
                            int1 = 0;
                        }

                        this.DaysSinceStart = (int)(int1 * 30.5F);
                        this.DaysSinceStart = this.DaysSinceStart + GameTime.instance.getNightsSurvived();
                    }

                    if (this.DaysSinceStart > 0) {
                        this.scriptManager.simulateScriptsUntil(this.DaysSinceStart, false);
                    }
                }

                boolean0 = true;
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
            }

            if (boolean1) {
                if (boolean0) {
                    DebugLog.Radio.println("Radio loaded.");
                }

                DebugLog.Radio.println("################################################");
                DebugLog.Radio.println("");
            }
        }
    }

    private void checkGameModeSpecificStart() {
        if (Core.GameMode.equals("Initial Infection")) {
            for (Entry entry0 : this.scriptManager.getChannels().entrySet()) {
                RadioScript radioScript = ((RadioChannel)entry0.getValue()).getRadioScript("init_infection");
                if (radioScript != null) {
                    radioScript.clearExitOptions();
                    radioScript.AddExitOption(((RadioChannel)entry0.getValue()).getCurrentScript().GetName(), 100, 0);
                    ((RadioChannel)entry0.getValue()).setActiveScript("init_infection", this.DaysSinceStart);
                } else {
                    ((RadioChannel)entry0.getValue()).getCurrentScript().setStartDayStamp(this.DaysSinceStart + 1);
                }
            }
        } else if (Core.GameMode.equals("Six Months Later")) {
            for (Entry entry1 : this.scriptManager.getChannels().entrySet()) {
                if (((RadioChannel)entry1.getValue()).GetName().equals("Classified M1A1")) {
                    ((RadioChannel)entry1.getValue()).setActiveScript("numbers", this.DaysSinceStart);
                } else if (((RadioChannel)entry1.getValue()).GetName().equals("NNR Radio")) {
                    ((RadioChannel)entry1.getValue()).setActiveScript("pastor", this.DaysSinceStart);
                }
            }
        }
    }

    public void Save() throws FileNotFoundException, IOException {
        if (!Core.getInstance().isNoSave()) {
            GameMode gameMode = this.getGameMode();
            if ((gameMode.equals(GameMode.Server) || gameMode.equals(GameMode.SinglePlayer)) && this.scriptManager != null) {
                File file0 = ZomboidFileSystem.instance.getFileInCurrentSave("radio", "data");
                if (file0.exists() && file0.isDirectory()) {
                    String string0 = ZomboidFileSystem.instance.getFileNameInCurrentSave("radio", "data", "RADIO_SAVE.txt");

                    String string1;
                    try (StringWriter stringWriter = new StringWriter(1024)) {
                        stringWriter.write("DaysSinceStart = " + this.DaysSinceStart + System.lineSeparator());
                        stringWriter.write("LvObfuscation = " + LOUISVILLE_OBFUSCATION + System.lineSeparator());
                        this.scriptManager.Save(stringWriter);
                        string1 = stringWriter.toString();
                    } catch (IOException iOException) {
                        ExceptionLogger.logException(iOException);
                        return;
                    }

                    if (string0.equals(this.lastSaveFile) && string1.equals(this.lastSaveContent)) {
                        return;
                    }

                    this.lastSaveFile = string0;
                    this.lastSaveContent = string1;
                    File file1 = new File(string0);
                    if (DebugLog.isEnabled(DebugType.Radio)) {
                        DebugLog.Radio.println("Saving radio: " + string0);
                    }

                    try (FileWriter fileWriter = new FileWriter(file1, false)) {
                        fileWriter.write(string1);
                    } catch (Exception exception0) {
                        ExceptionLogger.logException(exception0);
                    }
                }
            }

            if (recordedMedia != null) {
                try {
                    recordedMedia.save();
                } catch (Exception exception1) {
                    exception1.printStackTrace();
                }
            }
        }
    }

    public boolean Load() throws FileNotFoundException, IOException {
        boolean boolean0 = false;
        GameMode gameMode = this.getGameMode();
        if (gameMode.equals(GameMode.Server) || gameMode.equals(GameMode.SinglePlayer)) {
            for (Entry entry : this.scriptManager.getChannels().entrySet()) {
                ((RadioChannel)entry.getValue()).setActiveScriptNull();
            }

            ArrayList arrayList = new ArrayList();
            String string0 = ZomboidFileSystem.instance.getFileNameInCurrentSave("radio", "data", "RADIO_SAVE.txt");
            File file = new File(string0);
            if (!file.exists()) {
                return false;
            } else {
                if (DebugLog.isEnabled(DebugType.Radio)) {
                    DebugLog.log(DebugType.Radio, "Loading radio save:" + string0);
                }

                String string1;
                try (
                    FileReader fileReader = new FileReader(file);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                ) {
                    while ((string1 = bufferedReader.readLine()) != null) {
                        string1 = string1.trim();
                        if (!string1.startsWith("DaysSinceStart") && !string1.startsWith("LvObfuscation")) {
                            arrayList.add(string1);
                        } else {
                            if (string1.startsWith("DaysSinceStart")) {
                                String[] strings0 = string1.split("=");
                                this.DaysSinceStart = Integer.parseInt(strings0[1].trim());
                            }

                            if (string1.startsWith("LvObfuscation")) {
                                String[] strings1 = string1.split("=");
                                LOUISVILLE_OBFUSCATION = Boolean.parseBoolean(strings1[1].trim());
                            }
                        }
                    }
                } catch (Exception exception0) {
                    exception0.printStackTrace();
                    return false;
                }

                boolean boolean1;
                try {
                    DebugLog.log("Radio Loading channels...");
                    this.scriptManager.Load(arrayList);
                    return boolean0;
                } catch (Exception exception1) {
                    exception1.printStackTrace();
                    boolean1 = false;
                } finally {
                    boolean0 = true;
                }

                return boolean1;
            }
        } else {
            return boolean0;
        }
    }

    public void Reset() {
        instance = null;
        if (this.scriptManager != null) {
            this.scriptManager.reset();
        }
    }

    public void UpdateScripts(int hour, int mins) {
        GameMode gameMode = this.getGameMode();
        if (gameMode.equals(GameMode.Server) || gameMode.equals(GameMode.SinglePlayer)) {
            if (hour == 0 && this.lastRecordedHour != 0) {
                this.DaysSinceStart++;
            }

            this.lastRecordedHour = hour;
            if (this.scriptManager != null) {
                this.scriptManager.UpdateScripts(this.DaysSinceStart, hour, mins);
            }

            try {
                this.Save();
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }

        if (gameMode.equals(GameMode.Client) || gameMode.equals(GameMode.SinglePlayer)) {
            for (int int0 = 0; int0 < this.devices.size(); int0++) {
                WaveSignalDevice waveSignalDevice = this.devices.get(int0);
                if (waveSignalDevice.getDeviceData().getIsTurnedOn() && waveSignalDevice.HasPlayerInRange()) {
                    waveSignalDevice.getDeviceData().TriggerPlayerListening(true);
                }
            }
        }

        if (gameMode.equals(GameMode.Client) && !this.hasRecievedServerData) {
            GameClient.sendRadioServerDataRequest();
        }
    }

    public void render() {
        GameMode gameMode = this.getGameMode();
        if (DEBUG_MODE && !gameMode.equals(GameMode.Server) && this.debugConsole != null) {
            this.debugConsole.render();
        }

        if (!gameMode.equals(GameMode.Server) && this.storySoundManager != null) {
            this.storySoundManager.render();
        }
    }

    private void addFrequencyListEntry(boolean boolean0, DeviceData deviceData, int int0, int int1) {
        if (deviceData != null) {
            if (!this.freqlist.containsKey(deviceData.getChannel())) {
                this.freqlist.put(deviceData.getChannel(), new ZomboidRadio.FreqListEntry(boolean0, deviceData, int0, int1));
            } else if (this.freqlist.get(deviceData.getChannel()).deviceData.getTransmitRange() < deviceData.getTransmitRange()) {
                ZomboidRadio.FreqListEntry freqListEntry = this.freqlist.get(deviceData.getChannel());
                freqListEntry.isInvItem = boolean0;
                freqListEntry.deviceData = deviceData;
                freqListEntry.sourceX = int0;
                freqListEntry.sourceY = int1;
            }
        }
    }

    public void update() {
        this.LouisvilleObfuscationCheck();
        if (DEBUG_MODE && this.debugConsole != null) {
            this.debugConsole.update();
        }

        GameMode gameMode = this.getGameMode();
        if ((gameMode.equals(GameMode.Server) || gameMode.equals(GameMode.SinglePlayer)) && this.DaysSinceStart > 14 && !POST_RADIO_SILENCE) {
            POST_RADIO_SILENCE = true;
            if (GameServer.bServer) {
                GameServer.sendRadioPostSilence();
            }
        }

        if (!gameMode.equals(GameMode.Server) && this.storySoundManager != null) {
            this.storySoundManager.update(this.DaysSinceStart, GameTime.instance.getHour(), GameTime.instance.getMinutes());
        }

        if ((gameMode.equals(GameMode.Server) || gameMode.equals(GameMode.SinglePlayer)) && this.scriptManager != null) {
            this.scriptManager.update();
        }

        if (gameMode.equals(GameMode.SinglePlayer) || gameMode.equals(GameMode.Client)) {
            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                IsoPlayer player = IsoPlayer.players[int0];
                if (player != null
                    && player.getLastSpokenLine() != null
                    && (this.playerLastLine[int0] == null || !this.playerLastLine[int0].equals(player.getLastSpokenLine()))) {
                    String string = player.getLastSpokenLine();
                    this.playerLastLine[int0] = string;
                    if (!gameMode.equals(GameMode.Client)
                        || (
                                !player.accessLevel.equals("admin")
                                        && !player.accessLevel.equals("gm")
                                        && !player.accessLevel.equals("overseer")
                                        && !player.accessLevel.equals("moderator")
                                    || !ServerOptions.instance.DisableRadioStaff.getValue()
                                        && (!ServerOptions.instance.DisableRadioAdmin.getValue() || !player.accessLevel.equals("admin"))
                                        && (!ServerOptions.instance.DisableRadioGM.getValue() || !player.accessLevel.equals("gm"))
                                        && (!ServerOptions.instance.DisableRadioOverseer.getValue() || !player.accessLevel.equals("overseer"))
                                        && (!ServerOptions.instance.DisableRadioModerator.getValue() || !player.accessLevel.equals("moderator"))
                            )
                            && (!ServerOptions.instance.DisableRadioInvisible.getValue() || !player.isInvisible())) {
                        this.freqlist.clear();
                        if (!GameClient.bClient && !GameServer.bServer) {
                            for (int int1 = 0; int1 < IsoPlayer.numPlayers; int1++) {
                                this.checkPlayerForDevice(IsoPlayer.players[int1], player);
                            }
                        } else if (GameClient.bClient) {
                            ArrayList arrayList = GameClient.instance.getPlayers();

                            for (int int2 = 0; int2 < arrayList.size(); int2++) {
                                this.checkPlayerForDevice((IsoPlayer)arrayList.get(int2), player);
                            }
                        }

                        for (WaveSignalDevice waveSignalDevice : this.broadcastDevices) {
                            if (waveSignalDevice != null
                                && waveSignalDevice.getDeviceData() != null
                                && waveSignalDevice.getDeviceData().getIsTurnedOn()
                                && waveSignalDevice.getDeviceData().getIsTwoWay()
                                && waveSignalDevice.HasPlayerInRange()
                                && !waveSignalDevice.getDeviceData().getMicIsMuted()
                                && this.GetDistance((int)player.getX(), (int)player.getY(), (int)waveSignalDevice.getX(), (int)waveSignalDevice.getY())
                                    < waveSignalDevice.getDeviceData().getMicRange()) {
                                this.addFrequencyListEntry(true, waveSignalDevice.getDeviceData(), (int)waveSignalDevice.getX(), (int)waveSignalDevice.getY());
                            }
                        }

                        if (this.freqlist.size() > 0) {
                            Color color = player.getSpeakColour();

                            for (Entry entry : this.freqlist.entrySet()) {
                                ZomboidRadio.FreqListEntry freqListEntry = (ZomboidRadio.FreqListEntry)entry.getValue();
                                this.SendTransmission(
                                    freqListEntry.sourceX,
                                    freqListEntry.sourceY,
                                    (Integer)entry.getKey(),
                                    this.playerLastLine[int0],
                                    null,
                                    null,
                                    color.r,
                                    color.g,
                                    color.b,
                                    freqListEntry.deviceData.getTransmitRange(),
                                    false
                                );
                            }
                        }
                    }
                }
            }
        }
    }

    private void checkPlayerForDevice(IsoPlayer player0, IsoPlayer player1) {
        boolean boolean0 = player0 == player1;
        if (player0 != null) {
            Radio radio = player0.getEquipedRadio();
            if (radio != null
                && radio.getDeviceData() != null
                && radio.getDeviceData().getIsPortable()
                && radio.getDeviceData().getIsTwoWay()
                && radio.getDeviceData().getIsTurnedOn()
                && !radio.getDeviceData().getMicIsMuted()
                && (
                    boolean0
                        || this.GetDistance((int)player1.getX(), (int)player1.getY(), (int)player0.getX(), (int)player0.getY())
                            < radio.getDeviceData().getMicRange()
                )) {
                this.addFrequencyListEntry(true, radio.getDeviceData(), (int)player0.getX(), (int)player0.getY());
            }
        }
    }

    private boolean DeviceInRange(int int3, int int1, int int4, int int2, int int0) {
        return int3 > int4 - int0
            && int3 < int4 + int0
            && int1 > int2 - int0
            && int1 < int2 + int0
            && Math.sqrt(Math.pow(int3 - int4, 2.0) + Math.pow(int1 - int2, 2.0)) < int0;
    }

    private int GetDistance(int int2, int int0, int int3, int int1) {
        return (int)Math.sqrt(Math.pow(int2 - int3, 2.0) + Math.pow(int0 - int1, 2.0));
    }

    private void DistributeToPlayerOnClient(
        IsoPlayer player,
        int int1,
        int int2,
        int int0,
        String string0,
        String string1,
        String string2,
        float float0,
        float float1,
        float float2,
        int int3,
        boolean var12
    ) {
        if (player != null && player.getOnlineID() != -1) {
            VoiceManagerData voiceManagerData = VoiceManagerData.get(player.getOnlineID());
            synchronized (voiceManagerData.radioData) {
                for (VoiceManagerData.RadioData radioData : voiceManagerData.radioData) {
                    if (radioData.isReceivingAvailable(int0)) {
                        this.DistributeToPlayerInternal(
                            radioData.getDeviceData().getParent(), player, int1, int2, string0, string1, string2, float0, float1, float2, int3
                        );
                    }
                }
            }
        }
    }

    private void DistributeToPlayer(
        IsoPlayer player,
        int int1,
        int int2,
        int int0,
        String string0,
        String string1,
        String string2,
        float float0,
        float float1,
        float float2,
        int int3,
        boolean var12
    ) {
        if (player != null) {
            Radio radio = player.getEquipedRadio();
            if (radio != null
                && radio.getDeviceData() != null
                && radio.getDeviceData().getIsPortable()
                && radio.getDeviceData().getIsTurnedOn()
                && radio.getDeviceData().getChannel() == int0) {
                if (radio.getDeviceData().getDeviceVolume() <= 0.0F) {
                    return;
                }

                if (radio.getDeviceData().isPlayingMedia() || radio.getDeviceData().isNoTransmit()) {
                    return;
                }

                this.DistributeToPlayerInternal(radio, player, int1, int2, string0, string1, string2, float0, float1, float2, int3);
            }
        }
    }

    private void DistributeToPlayerInternal(
        WaveSignalDevice waveSignalDevice,
        IsoPlayer player,
        int int2,
        int int3,
        String string0,
        String string1,
        String string2,
        float float0,
        float float1,
        float float2,
        int int1
    ) {
        boolean boolean0 = false;
        int int0 = -1;
        if (int1 < 0) {
            boolean0 = true;
        } else {
            int0 = this.GetDistance((int)player.getX(), (int)player.getY(), int2, int3);
            if (int0 > 3 && int0 < int1) {
                boolean0 = true;
            }
        }

        if (boolean0) {
            if (int1 > 0) {
                this.hasAppliedRangeDistortion = false;
                string0 = this.doDeviceRangeDistortion(string0, int1, int0);
            }

            if (!this.hasAppliedRangeDistortion) {
                waveSignalDevice.AddDeviceText(player, string0, float0, float1, float2, string1, string2, int0);
            } else {
                waveSignalDevice.AddDeviceText(string0, 0.5F, 0.5F, 0.5F, string1, string2, int0);
            }
        }
    }

    private void DistributeTransmission(
        int int1, int int2, int int3, String string0, String string1, String string2, float float0, float float1, float float2, int int4, boolean boolean0
    ) {
        if (!boolean0) {
            if (!GameClient.bClient && !GameServer.bServer) {
                for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                    this.DistributeToPlayer(IsoPlayer.players[int0], int1, int2, int3, string0, string1, string2, float0, float1, float2, int4, boolean0);
                }
            } else if (GameClient.bClient) {
                for (IsoPlayer player : IsoPlayer.players) {
                    this.DistributeToPlayerOnClient(player, int1, int2, int3, string0, string1, string2, float0, float1, float2, int4, boolean0);
                }

                return;
            }
        }

        if (this.devices.size() != 0) {
            for (int int5 = 0; int5 < this.devices.size(); int5++) {
                WaveSignalDevice waveSignalDevice = this.devices.get(int5);
                if (waveSignalDevice != null
                    && waveSignalDevice.getDeviceData() != null
                    && waveSignalDevice.getDeviceData().getIsTurnedOn()
                    && boolean0 == waveSignalDevice.getDeviceData().getIsTelevision()) {
                    if (waveSignalDevice.getDeviceData().isPlayingMedia() || waveSignalDevice.getDeviceData().isNoTransmit()) {
                        return;
                    }

                    if (int3 == waveSignalDevice.getDeviceData().getChannel()) {
                        boolean boolean1 = false;
                        if (int4 == -1) {
                            boolean1 = true;
                        } else if (int1 != (int)waveSignalDevice.getX() && int2 != (int)waveSignalDevice.getY()) {
                            boolean1 = true;
                        }

                        if (boolean1) {
                            int int6 = -1;
                            if (int4 > 0) {
                                this.hasAppliedRangeDistortion = false;
                                int6 = this.GetDistance((int)waveSignalDevice.getX(), (int)waveSignalDevice.getY(), int1, int2);
                                string0 = this.doDeviceRangeDistortion(string0, int4, int6);
                            }

                            if (!this.hasAppliedRangeDistortion) {
                                waveSignalDevice.AddDeviceText(string0, float0, float1, float2, string1, string2, int6);
                            } else {
                                waveSignalDevice.AddDeviceText(string0, 0.5F, 0.5F, 0.5F, string1, string2, int6);
                            }
                        }
                    }
                }
            }
        }
    }

    private String doDeviceRangeDistortion(String string, int int0, int int1) {
        float float0 = int0 * 0.9F;
        if (float0 < int0 && int1 > float0) {
            float float1 = 100.0F * ((int1 - float0) / (int0 - float0));
            string = this.scrambleString(string, (int)float1, false);
            this.hasAppliedRangeDistortion = true;
        }

        return string;
    }

    public GameMode getGameMode() {
        if (!GameClient.bClient && !GameServer.bServer) {
            return GameMode.SinglePlayer;
        } else {
            return GameServer.bServer ? GameMode.Server : GameMode.Client;
        }
    }

    public String getRandomBzztFzzt() {
        int int0 = Rand.Next(staticSounds.length);
        return staticSounds[int0];
    }

    private String applyWeatherInterference(String string, int int1) {
        if (ClimateManager.getInstance().getWeatherInterference() <= 0.0F) {
            return string;
        } else {
            int int0 = (int)(ClimateManager.getInstance().getWeatherInterference() * 100.0F);
            return this.scrambleString(string, int0, int1 == -1);
        }
    }

    private String scrambleString(String string, int int0, boolean boolean0) {
        return this.scrambleString(string, int0, boolean0, null);
    }

    public String scrambleString(String msg, int intensity, boolean ignoreBBcode, String customScramble) {
        this.hasAppliedInterference = false;
        StringBuilder stringBuilderx = this.stringBuilder;
        stringBuilderx.setLength(0);
        if (intensity <= 0) {
            return msg;
        } else if (intensity >= 100) {
            return customScramble != null ? customScramble : this.getRandomBzztFzzt();
        } else {
            this.hasAppliedInterference = true;
            if (ignoreBBcode) {
                char[] chars = msg.toCharArray();
                boolean boolean0 = false;
                boolean boolean1 = false;
                String string0 = "";

                for (int int0 = 0; int0 < chars.length; int0++) {
                    char char0 = chars[int0];
                    if (boolean1) {
                        string0 = string0 + char0;
                        if (char0 == ']') {
                            stringBuilderx.append(string0);
                            string0 = "";
                            boolean1 = false;
                        }
                    } else if (char0 == '[' || Character.isWhitespace(char0) && int0 > 0 && !Character.isWhitespace(chars[int0 - 1])) {
                        int int1 = Rand.Next(100);
                        if (int1 > intensity) {
                            stringBuilderx.append(string0).append(" ");
                            boolean0 = false;
                        } else if (!boolean0) {
                            stringBuilderx.append(customScramble != null ? customScramble : this.getRandomBzztFzzt()).append(" ");
                            boolean0 = true;
                        }

                        if (char0 == '[') {
                            string0 = "[";
                            boolean1 = true;
                        } else {
                            string0 = "";
                        }
                    } else {
                        string0 = string0 + char0;
                    }
                }

                if (string0 != null && string0.length() > 0) {
                    stringBuilderx.append(string0);
                }
            } else {
                boolean boolean2 = false;
                String[] strings = msg.split("\\s+");

                for (int int2 = 0; int2 < strings.length; int2++) {
                    String string1 = strings[int2];
                    int int3 = Rand.Next(100);
                    if (int3 > intensity) {
                        stringBuilderx.append(string1).append(" ");
                        boolean2 = false;
                    } else if (!boolean2) {
                        stringBuilderx.append(customScramble != null ? customScramble : this.getRandomBzztFzzt()).append(" ");
                        boolean2 = true;
                    }
                }
            }

            return stringBuilderx.toString();
        }
    }

    public void ReceiveTransmission(
        int sourceX, int sourceY, int channel, String msg, String guid, String codes, float r, float g, float b, int signalStrength, boolean isTV
    ) {
        GameMode gameMode = this.getGameMode();
        if (gameMode.equals(GameMode.Server)) {
            this.SendTransmission(sourceX, sourceY, channel, msg, guid, codes, r, g, b, signalStrength, isTV);
        } else {
            this.DistributeTransmission(sourceX, sourceY, channel, msg, guid, codes, r, g, b, signalStrength, isTV);
        }
    }

    public void SendTransmission(int sourceX, int sourceY, ChatMessage msg, int signalStrength) {
        Color color = msg.getTextColor();
        int int0 = msg.getRadioChannel();
        this.SendTransmission(sourceX, sourceY, int0, msg.getText(), null, null, color.r, color.g, color.b, signalStrength, false);
    }

    public void SendTransmission(
        int sourceX, int sourceY, int channel, String msg, String guid, String codes, float r, float g, float b, int signalStrength, boolean isTV
    ) {
        this.SendTransmission(-1L, sourceX, sourceY, channel, msg, guid, codes, r, g, b, signalStrength, isTV);
    }

    public void SendTransmission(
        long source, int sourceX, int sourceY, int channel, String msg, String guid, String codes, float r, float g, float b, int signalStrength, boolean isTV
    ) {
        GameMode gameMode = this.getGameMode();
        if (!isTV && (gameMode == GameMode.Server || gameMode == GameMode.SinglePlayer)) {
            this.hasAppliedInterference = false;
            msg = this.applyWeatherInterference(msg, signalStrength);
            if (this.hasAppliedInterference) {
                r = 0.5F;
                g = 0.5F;
                b = 0.5F;
                codes = "";
            }
        }

        if (gameMode.equals(GameMode.SinglePlayer)) {
            this.ReceiveTransmission(sourceX, sourceY, channel, msg, guid, codes, r, g, b, signalStrength, isTV);
        } else if (gameMode.equals(GameMode.Server)) {
            GameServer.sendIsoWaveSignal(source, sourceX, sourceY, channel, msg, guid, codes, r, g, b, signalStrength, isTV);
        } else if (gameMode.equals(GameMode.Client)) {
            GameClient.sendIsoWaveSignal(sourceX, sourceY, channel, msg, guid, codes, r, g, b, signalStrength, isTV);
        }
    }

    public void PlayerListensChannel(int channel, boolean listenmode, boolean isTV) {
        GameMode gameMode = this.getGameMode();
        if (!gameMode.equals(GameMode.SinglePlayer) && !gameMode.equals(GameMode.Server)) {
            if (gameMode.equals(GameMode.Client)) {
                GameClient.sendPlayerListensChannel(channel, listenmode, isTV);
            }
        } else if (this.scriptManager != null) {
            this.scriptManager.PlayerListensChannel(channel, listenmode, isTV);
        }
    }

    public void RegisterDevice(WaveSignalDevice device) {
        if (device != null) {
            if (!GameServer.bServer && !this.devices.contains(device)) {
                this.devices.add(device);
            }

            if (!GameServer.bServer && device.getDeviceData().getIsTwoWay() && !this.broadcastDevices.contains(device)) {
                this.broadcastDevices.add(device);
            }
        }
    }

    public void UnRegisterDevice(WaveSignalDevice device) {
        if (device != null) {
            if (!GameServer.bServer && this.devices.contains(device)) {
                this.devices.remove(device);
            }

            if (!GameServer.bServer && device.getDeviceData().getIsTwoWay() && this.broadcastDevices.contains(device)) {
                this.broadcastDevices.remove(device);
            }
        }
    }

    @Override
    public Object clone() {
        return null;
    }

    public String computerize(String str) {
        StringBuilder stringBuilderx = this.stringBuilder;
        stringBuilderx.setLength(0);

        for (char char0 : str.toCharArray()) {
            if (Character.isLetter(char0)) {
                stringBuilderx.append(Rand.NextBool(2) ? Character.toLowerCase(char0) : Character.toUpperCase(char0));
            } else {
                stringBuilderx.append(char0);
            }
        }

        return stringBuilderx.toString();
    }

    public RecordedMedia getRecordedMedia() {
        return recordedMedia;
    }

    public void setDisableBroadcasting(boolean b) {
        DISABLE_BROADCASTING = b;
    }

    public boolean getDisableBroadcasting() {
        return DISABLE_BROADCASTING;
    }

    public void setDisableMediaLineLearning(boolean b) {
        RecordedMedia.DISABLE_LINE_LEARNING = b;
    }

    public boolean getDisableMediaLineLearning() {
        return RecordedMedia.DISABLE_LINE_LEARNING;
    }

    private void LouisvilleObfuscationCheck() {
        if (!GameClient.bClient && !GameServer.bServer) {
            IsoPlayer player = IsoPlayer.getInstance();
            if (player != null && player.getY() < 3550.0F) {
                LOUISVILLE_OBFUSCATION = true;
            }
        }
    }

    public static void ObfuscateChannelCheck(RadioChannel channel) {
        if (channel.isVanilla()) {
            int int0 = channel.GetFrequency();

            for (int int1 = 0; int1 < obfuscateChannels.length; int1++) {
                if (int0 == obfuscateChannels[int1]) {
                    channel.setLouisvilleObfuscate(true);
                }
            }
        }
    }

    private static final class FreqListEntry {
        public boolean isInvItem = false;
        public DeviceData deviceData;
        public int sourceX = 0;
        public int sourceY = 0;

        public FreqListEntry(boolean boolean0, DeviceData deviceDatax, int int0, int int1) {
            this.isInvItem = boolean0;
            this.deviceData = deviceDatax;
            this.sourceX = int0;
            this.sourceY = int1;
        }
    }
}
