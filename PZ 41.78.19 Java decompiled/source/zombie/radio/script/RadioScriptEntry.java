// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.script;

public final class RadioScriptEntry {
    private int chanceMin = 0;
    private int chanceMax = 100;
    private String scriptName = "";
    private int Delay = 0;

    public RadioScriptEntry(String string, int int0) {
        this(string, int0, 0, 100);
    }

    public RadioScriptEntry(String string, int int2, int int0, int int1) {
        this.scriptName = string;
        this.setChanceMin(int0);
        this.setChanceMax(int1);
        this.setDelay(int2);
    }

    public void setChanceMin(int int0) {
        this.chanceMin = int0 < 0 ? 0 : (int0 > 100 ? 100 : int0);
    }

    public int getChanceMin() {
        return this.chanceMin;
    }

    public void setChanceMax(int int0) {
        this.chanceMax = int0 < 0 ? 0 : (int0 > 100 ? 100 : int0);
    }

    public int getChanceMax() {
        return this.chanceMax;
    }

    public String getScriptName() {
        return this.scriptName;
    }

    public void setScriptName(String string) {
        this.scriptName = string;
    }

    public int getDelay() {
        return this.Delay;
    }

    public void setDelay(int int0) {
        this.Delay = int0 >= 0 ? int0 : 0;
    }
}
