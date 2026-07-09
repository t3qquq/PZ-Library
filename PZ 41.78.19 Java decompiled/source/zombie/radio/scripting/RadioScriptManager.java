// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.scripting;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.radio.ZomboidRadio;

/**
 * Turbo
 */
public final class RadioScriptManager {
    private final Map<Integer, RadioChannel> channels = new LinkedHashMap<>();
    private static RadioScriptManager instance;
    private int currentTimeStamp = 0;
    private ArrayList<RadioChannel> channelsList = new ArrayList<>();

    public static boolean hasInstance() {
        return instance != null;
    }

    public static RadioScriptManager getInstance() {
        if (instance == null) {
            instance = new RadioScriptManager();
        }

        return instance;
    }

    private RadioScriptManager() {
    }

    public void init(int savedWorldVersion) {
    }

    public Map<Integer, RadioChannel> getChannels() {
        return this.channels;
    }

    public ArrayList getChannelsList() {
        this.channelsList.clear();

        for (Entry entry : this.channels.entrySet()) {
            this.channelsList.add((RadioChannel)entry.getValue());
        }

        return this.channelsList;
    }

    public RadioChannel getRadioChannel(String uuid) {
        for (Entry entry : this.channels.entrySet()) {
            if (((RadioChannel)entry.getValue()).getGUID().equals(uuid)) {
                return (RadioChannel)entry.getValue();
            }
        }

        return null;
    }

    public void simulateScriptsUntil(int days, boolean force) {
        for (Entry entry : this.channels.entrySet()) {
            this.simulateChannelUntil(((RadioChannel)entry.getValue()).GetFrequency(), days, force);
        }
    }

    public void simulateChannelUntil(int frequency, int days, boolean force) {
        if (this.channels.containsKey(frequency)) {
            RadioChannel radioChannel = this.channels.get(frequency);
            if (radioChannel.isTimeSynced() && !force) {
                return;
            }

            for (int int0 = 0; int0 < days; int0++) {
                int int1 = int0 * 24 * 60;
                radioChannel.UpdateScripts(this.currentTimeStamp, int1);
            }

            radioChannel.setTimeSynced(true);
        }
    }

    public int getCurrentTimeStamp() {
        return this.currentTimeStamp;
    }

    public void PlayerListensChannel(int chanfrequency, boolean mode, boolean sourceIsTV) {
        if (this.channels.containsKey(chanfrequency) && this.channels.get(chanfrequency).IsTv() == sourceIsTV) {
            this.channels.get(chanfrequency).SetPlayerIsListening(mode);
        }
    }

    public void AddChannel(RadioChannel channel, boolean overwrite) {
        if (channel == null || !overwrite && this.channels.containsKey(channel.GetFrequency())) {
            String string0 = channel != null ? channel.GetName() : "null";
            DebugLog.log(DebugType.Radio, "Error adding radiochannel (" + string0 + "), channel is null or frequency key already exists");
        } else {
            this.channels.put(channel.GetFrequency(), channel);
            String string1 = channel.GetCategory().name();
            ZomboidRadio.getInstance().addChannelName(channel.GetName(), channel.GetFrequency(), string1, overwrite);
        }
    }

    public void RemoveChannel(int frequency) {
        if (this.channels.containsKey(frequency)) {
            this.channels.remove(frequency);
            ZomboidRadio.getInstance().removeChannelName(frequency);
        }
    }

    public void UpdateScripts(int day, int hour, int mins) {
        this.currentTimeStamp = day * 24 * 60 + hour * 60 + mins;

        for (Entry entry : this.channels.entrySet()) {
            ((RadioChannel)entry.getValue()).UpdateScripts(this.currentTimeStamp, day);
        }
    }

    public void update() {
        for (Entry entry : this.channels.entrySet()) {
            ((RadioChannel)entry.getValue()).update();
        }
    }

    public void reset() {
        instance = null;
    }

    public void Save(Writer w) throws IOException {
        for (Entry entry : this.channels.entrySet()) {
            w.write(
                entry.getKey()
                    + ","
                    + ((RadioChannel)entry.getValue()).getCurrentScriptLoop()
                    + ","
                    + ((RadioChannel)entry.getValue()).getCurrentScriptMaxLoops()
            );
            RadioScript radioScript = ((RadioChannel)entry.getValue()).getCurrentScript();
            if (radioScript != null) {
                w.write("," + radioScript.GetName() + "," + radioScript.getStartDay());
            }

            RadioBroadCast radioBroadCast = ((RadioChannel)entry.getValue()).getAiringBroadcast();
            if (radioBroadCast != null) {
                w.write("," + radioBroadCast.getID());
            } else if (((RadioChannel)entry.getValue()).getLastBroadcastID() != null) {
                w.write("," + ((RadioChannel)entry.getValue()).getLastBroadcastID());
            } else {
                w.write(",none");
            }

            w.write("," + (radioBroadCast != null ? radioBroadCast.getCurrentLineNumber() + "" : "-1"));
            w.write(System.lineSeparator());
        }
    }

    public void Load(List<String> channelLines) throws IOException, NumberFormatException {
        int int0 = 1;
        int int1 = 1;

        for (String string0 : channelLines) {
            RadioChannel radioChannel = null;
            if (string0 != null) {
                string0 = string0.trim();
                String[] strings = string0.split(",");
                if (strings.length >= 3) {
                    int int2 = Integer.parseInt(strings[0]);
                    int0 = Integer.parseInt(strings[1]);
                    int1 = Integer.parseInt(strings[2]);
                    if (this.channels.containsKey(int2)) {
                        radioChannel = this.channels.get(int2);
                        radioChannel.setTimeSynced(true);
                    }
                }

                if (radioChannel != null && strings.length >= 5) {
                    String string1 = strings[3];
                    int int3 = Integer.parseInt(strings[4]);
                    if (radioChannel != null) {
                        radioChannel.setActiveScript(string1, int3, int0, int1);
                    }
                }

                if (radioChannel != null && strings.length >= 7) {
                    String string2 = strings[5];
                    if (!string2.equals("none")) {
                        int int4 = Integer.parseInt(strings[6]);
                        radioChannel.LoadAiringBroadcast(string2, int4);
                    }
                }
            }
        }
    }
}
