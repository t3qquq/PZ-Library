// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.devices;

/**
 * turbo
 */
public final class PresetEntry {
    public String name = "New preset";
    public int frequency = 93200;

    public PresetEntry() {
    }

    public PresetEntry(String n, int f) {
        this.name = n;
        this.frequency = f;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String n) {
        this.name = n;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public void setFrequency(int f) {
        this.frequency = f;
    }
}
