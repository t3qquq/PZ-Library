// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.scripting.objects;

import java.util.HashMap;
import zombie.core.math.PZMath;
import zombie.scripting.ScriptParser;

public final class SoundTimelineScript extends BaseScriptObject {
    private String eventName;
    private HashMap<String, Integer> positionByName = new HashMap<>();

    public void Load(String name, String totalFile) {
        this.eventName = name;
        ScriptParser.Block block = ScriptParser.parse(totalFile);
        block = block.children.get(0);

        for (ScriptParser.Value value : block.values) {
            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
            this.positionByName.put(string0, PZMath.tryParseInt(string1, 0));
        }
    }

    public String getEventName() {
        return this.eventName;
    }

    public int getPosition(String id) {
        return this.positionByName.containsKey(id) ? this.positionByName.get(id) : -1;
    }

    public void reset() {
        this.positionByName.clear();
    }
}
