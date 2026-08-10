// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import zombie.characters.IsoPlayer;

public final class LineEffectMemory {
    private final Map<Integer, ArrayList<String>> memory = new HashMap<>();

    public void addLine(IsoPlayer player, String string) {
        int int0 = player.getDescriptor().getID();
        ArrayList arrayList;
        if (!this.memory.containsKey(int0)) {
            arrayList = new ArrayList();
            this.memory.put(int0, arrayList);
        } else {
            arrayList = this.memory.get(int0);
        }

        if (!arrayList.contains(string)) {
            arrayList.add(string);
        }
    }

    public boolean contains(IsoPlayer player, String string) {
        int int0 = player.getDescriptor().getID();
        return !this.memory.containsKey(int0) ? false : this.memory.get(int0).contains(string);
    }
}
