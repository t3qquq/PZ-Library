// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.globals;

import java.util.HashMap;
import java.util.Map;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;

public final class RadioGlobalsManager {
    private final Map<String, RadioGlobal> globals = new HashMap<>();
    private final RadioGlobalInt bufferInt = new RadioGlobalInt("bufferInt", 0);
    private final RadioGlobalString bufferString = new RadioGlobalString("bufferString", "");
    private final RadioGlobalBool bufferBoolean = new RadioGlobalBool("bufferBoolean", false);
    private final RadioGlobalFloat bufferFloat = new RadioGlobalFloat("bufferFloat", 0.0F);
    private static RadioGlobalsManager instance;

    public static RadioGlobalsManager getInstance() {
        if (instance == null) {
            instance = new RadioGlobalsManager();
        }

        return instance;
    }

    private RadioGlobalsManager() {
    }

    public void reset() {
        instance = null;
    }

    public boolean exists(String string) {
        return this.globals.containsKey(string);
    }

    public RadioGlobalType getType(String string) {
        return this.globals.containsKey(string) ? this.globals.get(string).getType() : RadioGlobalType.Invalid;
    }

    public String getString(String string) {
        RadioGlobal radioGlobal = this.getGlobal(string);
        return radioGlobal != null ? radioGlobal.getString() : null;
    }

    public boolean addGlobal(String string, RadioGlobal radioGlobal) {
        if (!this.exists(string) && radioGlobal != null) {
            this.globals.put(string, radioGlobal);
            return true;
        } else {
            DebugLog.log(DebugType.Radio, "Error adding global: " + string + " to globals (already exists or global==null)");
            return false;
        }
    }

    public boolean addGlobalString(String string0, String string1) {
        return this.addGlobal(string0, new RadioGlobalString(string0, string1));
    }

    public boolean addGlobalBool(String string, boolean boolean0) {
        return this.addGlobal(string, new RadioGlobalBool(string, boolean0));
    }

    public boolean addGlobalInt(String string, int int0) {
        return this.addGlobal(string, new RadioGlobalInt(string, int0));
    }

    public boolean addGlobalFloat(String string, float float0) {
        return this.addGlobal(string, new RadioGlobalFloat(string, float0));
    }

    public RadioGlobal getGlobal(String string) {
        return this.exists(string) ? this.globals.get(string) : null;
    }

    public RadioGlobalString getGlobalString(String string) {
        RadioGlobal radioGlobal = this.getGlobal(string);
        return radioGlobal != null && radioGlobal instanceof RadioGlobalString ? (RadioGlobalString)radioGlobal : null;
    }

    public RadioGlobalInt getGlobalInt(String string) {
        RadioGlobal radioGlobal = this.getGlobal(string);
        return radioGlobal != null && radioGlobal instanceof RadioGlobalInt ? (RadioGlobalInt)radioGlobal : null;
    }

    public RadioGlobalFloat getGlobalFloat(String string) {
        RadioGlobal radioGlobal = this.getGlobal(string);
        return radioGlobal != null && radioGlobal instanceof RadioGlobalFloat ? (RadioGlobalFloat)radioGlobal : null;
    }

    public RadioGlobalBool getGlobalBool(String string) {
        RadioGlobal radioGlobal = this.getGlobal(string);
        return radioGlobal != null && radioGlobal instanceof RadioGlobalBool ? (RadioGlobalBool)radioGlobal : null;
    }

    public boolean setGlobal(String string, RadioGlobal radioGlobal1, EditGlobalOps editGlobalOps) {
        RadioGlobal radioGlobal0 = this.getGlobal(string);
        return radioGlobal0 != null && radioGlobal1 != null ? radioGlobal0.setValue(radioGlobal1, editGlobalOps) : false;
    }

    public boolean setGlobal(String string1, String string0) {
        this.bufferString.setValue(string0);
        return this.setGlobal(string1, this.bufferString, EditGlobalOps.set);
    }

    public boolean setGlobal(String string, int int0) {
        this.bufferInt.setValue(int0);
        return this.setGlobal(string, this.bufferInt, EditGlobalOps.set);
    }

    public boolean setGlobal(String string, float float0) {
        this.bufferFloat.setValue(float0);
        return this.setGlobal(string, this.bufferFloat, EditGlobalOps.set);
    }

    public boolean setGlobal(String string, boolean boolean0) {
        this.bufferBoolean.setValue(boolean0);
        return this.setGlobal(string, this.bufferBoolean, EditGlobalOps.set);
    }

    public CompareResult compare(RadioGlobal radioGlobal0, RadioGlobal radioGlobal1, CompareMethod compareMethod) {
        return radioGlobal0 != null && radioGlobal1 != null && radioGlobal0.getType().equals(radioGlobal1.getType())
            ? radioGlobal0.compare(radioGlobal1, compareMethod)
            : CompareResult.Invalid;
    }

    public CompareResult compare(String string1, String string0, CompareMethod compareMethod) {
        return this.compare(this.getGlobal(string1), this.getGlobal(string0), compareMethod);
    }
}
