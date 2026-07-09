// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.scripting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import zombie.GameTime;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.radio.ChannelCategory;
import zombie.radio.RadioData;
import zombie.radio.ZomboidRadio;

/**
 * Turbo
 */
public class RadioChannel {
    private String GUID;
    private RadioData radioData;
    private boolean isTimeSynced = false;
    private Map<String, RadioScript> scripts = new HashMap<>();
    private int frequency = -1;
    private String name = "Unnamed channel";
    private boolean isTv = false;
    private ChannelCategory category = ChannelCategory.Undefined;
    private boolean playerIsListening = false;
    private RadioScript currentScript = null;
    private int currentScriptLoop = 1;
    private int currentScriptMaxLoops = 1;
    private RadioBroadCast airingBroadcast = null;
    private float airCounter = 0.0F;
    private String lastAiredLine = "";
    private String lastBroadcastID = null;
    private float airCounterMultiplier = 1.0F;
    private boolean louisvilleObfuscate = false;
    float minmod = 1.5F;
    float maxmod = 5.0F;

    public RadioChannel(String n, int freq, ChannelCategory c) {
        this(n, freq, c, UUID.randomUUID().toString());
    }

    public RadioChannel(String n, int freq, ChannelCategory c, String guid) {
        this.name = n;
        this.frequency = freq;
        this.category = c;
        this.isTv = this.category == ChannelCategory.Television;
        this.GUID = guid;
    }

    public String getGUID() {
        return this.GUID;
    }

    public int GetFrequency() {
        return this.frequency;
    }

    public String GetName() {
        return this.name;
    }

    public boolean IsTv() {
        return this.isTv;
    }

    public ChannelCategory GetCategory() {
        return this.category;
    }

    public RadioScript getCurrentScript() {
        return this.currentScript;
    }

    public RadioBroadCast getAiringBroadcast() {
        return this.airingBroadcast;
    }

    public String getLastAiredLine() {
        return this.lastAiredLine;
    }

    public int getCurrentScriptLoop() {
        return this.currentScriptLoop;
    }

    public int getCurrentScriptMaxLoops() {
        return this.currentScriptMaxLoops;
    }

    public String getLastBroadcastID() {
        return this.lastBroadcastID;
    }

    public RadioData getRadioData() {
        return this.radioData;
    }

    public void setRadioData(RadioData _radioData) {
        this.radioData = _radioData;
    }

    public boolean isTimeSynced() {
        return this.isTimeSynced;
    }

    public void setTimeSynced(boolean _isTimeSynced) {
        this.isTimeSynced = _isTimeSynced;
    }

    public boolean isVanilla() {
        return this.radioData == null || this.radioData.isVanilla();
    }

    public void setLouisvilleObfuscate(boolean b) {
        this.louisvilleObfuscate = b;
    }

    public void LoadAiringBroadcast(String guid, int line) {
        if (this.currentScript != null) {
            this.airingBroadcast = this.currentScript.getBroadcastWithID(guid);
            if (line < 0) {
                this.airingBroadcast = null;
            }

            if (this.airingBroadcast != null && line >= 0) {
                this.airingBroadcast.resetLineCounter();
                this.airingBroadcast.setCurrentLineNumber(line);
                this.airCounter = 120.0F;
                this.playerIsListening = true;
            }
        }
    }

    public void SetPlayerIsListening(boolean isListening) {
        this.playerIsListening = isListening;
        if (this.playerIsListening && this.airingBroadcast == null && this.currentScript != null) {
            this.airingBroadcast = this.currentScript.getValidAirBroadcast();
            if (this.airingBroadcast != null) {
                this.airingBroadcast.resetLineCounter();
            }

            this.airCounter = 0.0F;
        }
    }

    public boolean GetPlayerIsListening() {
        return this.playerIsListening;
    }

    public void setActiveScriptNull() {
        this.currentScript = null;
        this.airingBroadcast = null;
    }

    public void setActiveScript(String scriptName, int day) {
        this.setActiveScript(scriptName, day, 1, -1);
    }

    public void setActiveScript(String scriptName, int day, int loop, int maxloops) {
        if (scriptName != null && this.scripts.containsKey(scriptName)) {
            this.currentScript = this.scripts.get(scriptName);
            if (this.currentScript != null) {
                this.currentScript.Reset();
                this.currentScript.setStartDayStamp(day);
                this.currentScriptLoop = loop;
                if (maxloops == -1) {
                    int int0 = this.currentScript.getLoopMin();
                    int int1 = this.currentScript.getLoopMax();
                    if (int0 != int1 && int0 <= int1) {
                        maxloops = Rand.Next(int0, int1);
                    } else {
                        maxloops = int0;
                    }
                }

                this.currentScriptMaxLoops = maxloops;
                if (DebugLog.isEnabled(DebugType.Radio)) {
                    DebugLog.Radio
                        .println(
                            "Script: "
                                + scriptName
                                + ", day = "
                                + day
                                + ", minloops = "
                                + this.currentScript.getLoopMin()
                                + ", maxloops = "
                                + this.currentScriptMaxLoops
                        );
                }
            }
        }
    }

    private void getNextScript(int int0) {
        if (this.currentScript != null) {
            if (this.currentScriptLoop < this.currentScriptMaxLoops) {
                this.currentScriptLoop++;
                this.currentScript.Reset();
                this.currentScript.setStartDayStamp(int0);
            } else {
                RadioScript.ExitOption exitOption = this.currentScript.getNextScript();
                this.currentScript = null;
                if (exitOption != null) {
                    this.setActiveScript(exitOption.getScriptname(), int0 + exitOption.getStartDelay());
                }
            }
        }
    }

    public void UpdateScripts(int timestamp, int day) {
        this.playerIsListening = false;
        if (this.currentScript != null && !this.currentScript.UpdateScript(timestamp)) {
            this.getNextScript(day + 1);
        }
    }

    public void update() {
        if (this.airingBroadcast != null) {
            this.airCounter = this.airCounter - 1.25F * GameTime.getInstance().getMultiplier();
            if (this.airCounter < 0.0F) {
                RadioLine radioLine = this.airingBroadcast.getNextLine();
                if (radioLine == null) {
                    this.lastBroadcastID = this.airingBroadcast.getID();
                    this.airingBroadcast = null;
                    this.playerIsListening = false;
                } else {
                    this.lastAiredLine = radioLine.getText();
                    if (!ZomboidRadio.DISABLE_BROADCASTING) {
                        String string = radioLine.getText();
                        if (this.louisvilleObfuscate && ZomboidRadio.LOUISVILLE_OBFUSCATION) {
                            string = ZomboidRadio.getInstance().scrambleString(string, 85, true, null);
                            ZomboidRadio.getInstance().SendTransmission(0, 0, this.frequency, string, null, "", 0.7F, 0.5F, 0.5F, -1, this.isTv);
                        } else {
                            ZomboidRadio.getInstance()
                                .SendTransmission(
                                    0,
                                    0,
                                    this.frequency,
                                    string,
                                    null,
                                    radioLine.getEffectsString(),
                                    radioLine.getR(),
                                    radioLine.getG(),
                                    radioLine.getB(),
                                    -1,
                                    this.isTv
                                );
                        }
                    }

                    if (radioLine.isCustomAirTime()) {
                        this.airCounter = radioLine.getAirTime() * 60.0F;
                    } else {
                        this.airCounter = radioLine.getText().length() / 10.0F * 60.0F;
                        if (this.airCounter < 60.0F * this.minmod) {
                            this.airCounter = 60.0F * this.minmod;
                        } else if (this.airCounter > 60.0F * this.maxmod) {
                            this.airCounter = 60.0F * this.maxmod;
                        }

                        this.airCounter = this.airCounter * this.airCounterMultiplier;
                    }
                }
            }
        }
    }

    public void AddRadioScript(RadioScript script) {
        if (script != null && !this.scripts.containsKey(script.GetName())) {
            this.scripts.put(script.GetName(), script);
        } else {
            String string = script != null ? script.GetName() : "null";
            DebugLog.log(DebugType.Radio, "Error while attempting to add script (" + string + "), null or name already exists.");
        }
    }

    public RadioScript getRadioScript(String script) {
        return script != null && this.scripts.containsKey(script) ? this.scripts.get(script) : null;
    }

    public void setAiringBroadcast(RadioBroadCast bc) {
        this.airingBroadcast = bc;
    }

    public float getAirCounterMultiplier() {
        return this.airCounterMultiplier;
    }

    public void setAirCounterMultiplier(float _airCounterMultiplier) {
        this.airCounterMultiplier = PZMath.clamp(_airCounterMultiplier, 0.1F, 10.0F);
    }
}
