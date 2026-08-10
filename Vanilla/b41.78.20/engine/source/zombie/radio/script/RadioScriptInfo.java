// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import zombie.radio.globals.RadioGlobal;

public final class RadioScriptInfo {
    private final Map<String, RadioGlobal> onStartSetters = new HashMap<>();
    private final List<ExitOptionOld> exitOptions = new ArrayList<>();

    public RadioScriptEntry getNextScript() {
        RadioScriptEntry radioScriptEntry = null;

        for (ExitOptionOld exitOptionOld : this.exitOptions) {
            if (exitOptionOld != null) {
                radioScriptEntry = exitOptionOld.evaluate();
                if (radioScriptEntry != null) {
                    break;
                }
            }
        }

        return radioScriptEntry;
    }

    public void addExitOption(ExitOptionOld exitOptionOld) {
        if (exitOptionOld != null) {
            this.exitOptions.add(exitOptionOld);
        }
    }
}
