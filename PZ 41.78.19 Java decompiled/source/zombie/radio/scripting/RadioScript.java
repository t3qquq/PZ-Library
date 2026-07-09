// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.scripting;

import java.util.ArrayList;
import java.util.UUID;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;

/**
 * Turbo
 */
public final class RadioScript {
    private final ArrayList<RadioBroadCast> broadcasts = new ArrayList<>();
    private final ArrayList<RadioScript.ExitOption> exitOptions = new ArrayList<>();
    private String GUID;
    private String name = "Unnamed radioscript";
    private int startDay = 0;
    private int startDayStamp = 0;
    private int loopMin = 1;
    private int loopMax = 1;
    private int internalStamp = 0;
    private RadioBroadCast currentBroadcast = null;
    private boolean currentHasAired = false;

    public RadioScript(String n, int loopmin, int loopmax) {
        this(n, loopmin, loopmax, UUID.randomUUID().toString());
    }

    public RadioScript(String n, int loopmin, int loopmax, String guid) {
        this.name = n;
        this.loopMin = loopmin;
        this.loopMax = loopmax;
        this.GUID = guid;
    }

    public String GetGUID() {
        return this.GUID;
    }

    public String GetName() {
        return this.name;
    }

    public int getStartDayStamp() {
        return this.startDayStamp;
    }

    public int getStartDay() {
        return this.startDay;
    }

    public int getLoopMin() {
        return this.loopMin;
    }

    public int getLoopMax() {
        return this.loopMax;
    }

    public RadioBroadCast getCurrentBroadcast() {
        return this.currentBroadcast;
    }

    public ArrayList<RadioBroadCast> getBroadcastList() {
        return this.broadcasts;
    }

    public void clearExitOptions() {
        this.exitOptions.clear();
    }

    public void setStartDayStamp(int day) {
        this.startDay = day;
        this.startDayStamp = day * 24 * 60;
    }

    public RadioBroadCast getValidAirBroadcast() {
        if (!this.currentHasAired
            && this.currentBroadcast != null
            && this.internalStamp >= this.currentBroadcast.getStartStamp()
            && this.internalStamp < this.currentBroadcast.getEndStamp()) {
            this.currentHasAired = true;
            return this.currentBroadcast;
        } else {
            return null;
        }
    }

    public void Reset() {
        this.currentBroadcast = null;
        this.currentHasAired = false;
    }

    private RadioBroadCast getNextBroadcast() {
        if (this.currentBroadcast != null && this.currentBroadcast.getEndStamp() > this.internalStamp) {
            return this.currentBroadcast;
        } else {
            for (int int0 = 0; int0 < this.broadcasts.size(); int0++) {
                RadioBroadCast radioBroadCast = this.broadcasts.get(int0);
                if (radioBroadCast.getEndStamp() > this.internalStamp) {
                    this.currentHasAired = false;
                    return radioBroadCast;
                }
            }

            return null;
        }
    }

    public RadioBroadCast getBroadcastWithID(String guid) {
        for (int int0 = 0; int0 < this.broadcasts.size(); int0++) {
            RadioBroadCast radioBroadCast = this.broadcasts.get(int0);
            if (radioBroadCast.getID().equals(guid)) {
                this.currentBroadcast = radioBroadCast;
                this.currentHasAired = true;
                return radioBroadCast;
            }
        }

        return null;
    }

    public boolean UpdateScript(int timeStamp) {
        this.internalStamp = timeStamp - this.startDayStamp;
        this.currentBroadcast = this.getNextBroadcast();
        return this.currentBroadcast != null;
    }

    public RadioScript.ExitOption getNextScript() {
        int int0 = 0;
        int int1 = Rand.Next(100);

        for (RadioScript.ExitOption exitOption : this.exitOptions) {
            if (int1 >= int0 && int1 < int0 + exitOption.getChance()) {
                return exitOption;
            }

            int0 += exitOption.getChance();
        }

        return null;
    }

    public void AddBroadcast(RadioBroadCast broadcast) {
        this.AddBroadcast(broadcast, false);
    }

    public void AddBroadcast(RadioBroadCast broadcast, boolean ignoreTimestamps) {
        boolean boolean0 = false;
        if (broadcast != null && broadcast.getID() != null) {
            if (ignoreTimestamps) {
                this.broadcasts.add(broadcast);
                boolean0 = true;
            } else if (broadcast.getStartStamp() >= 0 && broadcast.getEndStamp() > broadcast.getStartStamp()) {
                if (this.broadcasts.size() == 0 || this.broadcasts.get(this.broadcasts.size() - 1).getEndStamp() <= broadcast.getStartStamp()) {
                    this.broadcasts.add(broadcast);
                    boolean0 = true;
                } else if (this.broadcasts.size() > 0) {
                    DebugLog.log(
                        DebugType.Radio,
                        "startstamp = '"
                            + broadcast.getStartStamp()
                            + "', endstamp = '"
                            + broadcast.getEndStamp()
                            + "', previous endstamp = '"
                            + this.broadcasts.get(this.broadcasts.size() - 1).getEndStamp()
                            + "'."
                    );
                }
            } else {
                DebugLog.log(DebugType.Radio, "startstamp = '" + broadcast.getStartStamp() + "', endstamp = '" + broadcast.getEndStamp() + "'.");
            }
        }

        if (!boolean0) {
            String string = broadcast != null ? broadcast.getID() : "null";
            DebugLog.log(DebugType.Radio, "Error cannot add broadcast ID: '" + string + "' to script '" + this.name + "', null or timestamp error");
        }
    }

    public void AddExitOption(String scriptname, int chance, int startdelay) {
        int int0 = chance;

        for (RadioScript.ExitOption exitOption : this.exitOptions) {
            int0 += exitOption.getChance();
        }

        if (int0 <= 100) {
            this.exitOptions.add(new RadioScript.ExitOption(scriptname, chance, startdelay));
        } else {
            DebugLog.log(
                DebugType.Radio, "Error cannot add exitoption with scriptname '" + scriptname + "' to script '" + this.name + "', total chance exceeding 100"
            );
        }
    }

    public RadioBroadCast getValidAirBroadcastDebug() {
        if (this.currentBroadcast != null && this.currentBroadcast.getEndStamp() > this.internalStamp) {
            return this.currentBroadcast;
        } else {
            for (int int0 = 0; int0 < this.broadcasts.size(); int0++) {
                RadioBroadCast radioBroadCast = this.broadcasts.get(int0);
                if (radioBroadCast.getEndStamp() > this.internalStamp) {
                    return radioBroadCast;
                }
            }

            return null;
        }
    }

    public ArrayList<RadioScript.ExitOption> getExitOptions() {
        return this.exitOptions;
    }

    public static final class ExitOption {
        private String scriptname = "";
        private int chance = 0;
        private int startDelay = 0;

        public ExitOption(String name, int rollchance, int startdelay) {
            this.scriptname = name;
            this.chance = rollchance;
            this.startDelay = startdelay;
        }

        public String getScriptname() {
            return this.scriptname;
        }

        public int getChance() {
            return this.chance;
        }

        public int getStartDelay() {
            return this.startDelay;
        }
    }
}
