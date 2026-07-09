// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.script;

import java.util.ArrayList;
import java.util.List;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.radio.globals.CompareResult;

public final class ExitOptionOld {
    private String parentScript;
    private String name;
    private ConditionContainer condition;
    private List<RadioScriptEntry> scriptEntries = new ArrayList<>();

    public ExitOptionOld(String string0, String string1) {
        this.parentScript = string0 != null ? string0 : "Noname";
        this.name = string1 != null ? string1 : "Noname";
    }

    public void setCondition(ConditionContainer conditionContainer) {
        this.condition = conditionContainer;
    }

    public void addScriptEntry(RadioScriptEntry radioScriptEntry) {
        if (radioScriptEntry != null) {
            this.scriptEntries.add(radioScriptEntry);
        } else {
            DebugLog.log(DebugType.Radio, "Error trying to add 'null' scriptentry in script: " + this.parentScript + ", exitoption: " + this.name);
        }
    }

    public RadioScriptEntry evaluate() {
        CompareResult compareResult = CompareResult.True;
        if (this.condition != null) {
            compareResult = this.condition.Evaluate();
        }

        if (compareResult.equals(CompareResult.True)) {
            if (this.scriptEntries != null && this.scriptEntries.size() > 0) {
                int int0 = Rand.Next(100);

                for (RadioScriptEntry radioScriptEntry : this.scriptEntries) {
                    if (radioScriptEntry != null) {
                        System.out.println("ScriptEntry " + radioScriptEntry.getScriptName());
                        System.out.println("Chance: " + int0 + " Min: " + radioScriptEntry.getChanceMin() + " Max: " + radioScriptEntry.getChanceMax());
                        if (int0 >= radioScriptEntry.getChanceMin() && int0 < radioScriptEntry.getChanceMax()) {
                            return radioScriptEntry;
                        }
                    }
                }
            }
        } else if (compareResult.equals(CompareResult.Invalid)) {
            System.out.println("Error occured evaluating condition: " + this.parentScript + ", exitoption: " + this.name);
            DebugLog.log(DebugType.Radio, "Error occured evaluating condition: " + this.parentScript + ", exitoption: " + this.name);
        }

        return null;
    }
}
