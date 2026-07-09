// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.devices;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameWindow;
import zombie.Lua.LuaManager;

/**
 * Turrubo
 */
public final class DevicePresets implements Cloneable {
    protected int maxPresets = 10;
    protected ArrayList<PresetEntry> presets = new ArrayList<>();

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public KahluaTable getPresetsLua() {
        KahluaTable table0 = LuaManager.platform.newTable();

        for (int int0 = 0; int0 < this.presets.size(); int0++) {
            PresetEntry presetEntry = this.presets.get(int0);
            KahluaTable table1 = LuaManager.platform.newTable();
            table1.rawset("name", presetEntry.name);
            table1.rawset("frequency", presetEntry.frequency);
            table0.rawset(int0, table1);
        }

        return table0;
    }

    public ArrayList<PresetEntry> getPresets() {
        return this.presets;
    }

    public void setPresets(ArrayList<PresetEntry> p) {
        this.presets = p;
    }

    public int getMaxPresets() {
        return this.maxPresets;
    }

    public void setMaxPresets(int m) {
        this.maxPresets = m;
    }

    public void addPreset(String name, int frequency) {
        if (this.presets.size() < this.maxPresets) {
            this.presets.add(new PresetEntry(name, frequency));
        }
    }

    public void removePreset(int id) {
        if (this.presets.size() != 0 && id >= 0 && id < this.presets.size()) {
            this.presets.remove(id);
        }
    }

    public String getPresetName(int id) {
        return this.presets.size() != 0 && id >= 0 && id < this.presets.size() ? this.presets.get(id).name : "";
    }

    public int getPresetFreq(int id) {
        return this.presets.size() != 0 && id >= 0 && id < this.presets.size() ? this.presets.get(id).frequency : -1;
    }

    public void setPresetName(int id, String name) {
        if (name == null) {
            name = "name-is-null";
        }

        if (this.presets.size() != 0 && id >= 0 && id < this.presets.size()) {
            PresetEntry presetEntry = this.presets.get(id);
            presetEntry.name = name;
        }
    }

    public void setPresetFreq(int id, int frequency) {
        if (this.presets.size() != 0 && id >= 0 && id < this.presets.size()) {
            PresetEntry presetEntry = this.presets.get(id);
            presetEntry.frequency = frequency;
        }
    }

    public void setPreset(int id, String name, int frequency) {
        if (name == null) {
            name = "name-is-null";
        }

        if (this.presets.size() != 0 && id >= 0 && id < this.presets.size()) {
            PresetEntry presetEntry = this.presets.get(id);
            presetEntry.name = name;
            presetEntry.frequency = frequency;
        }
    }

    public void clearPresets() {
        this.presets.clear();
    }

    public void save(ByteBuffer output, boolean net) throws IOException {
        output.putInt(this.maxPresets);
        output.putInt(this.presets.size());

        for (int int0 = 0; int0 < this.presets.size(); int0++) {
            PresetEntry presetEntry = this.presets.get(int0);
            GameWindow.WriteString(output, presetEntry.name);
            output.putInt(presetEntry.frequency);
        }
    }

    public void load(ByteBuffer input, int WorldVersion, boolean net) throws IOException {
        if (WorldVersion >= 69) {
            this.clearPresets();
            this.maxPresets = input.getInt();
            int int0 = input.getInt();

            for (int int1 = 0; int1 < int0; int1++) {
                String string = GameWindow.ReadString(input);
                int int2 = input.getInt();
                if (this.presets.size() < this.maxPresets) {
                    this.presets.add(new PresetEntry(string, int2));
                }
            }
        }
    }
}
