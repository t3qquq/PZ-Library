// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap.styles;

import java.util.ArrayList;

public final class WorldMapStyle {
    public final ArrayList<WorldMapStyleLayer> m_layers = new ArrayList<>();

    public void copyFrom(WorldMapStyle other) {
        this.m_layers.clear();
        this.m_layers.addAll(other.m_layers);
    }
}
