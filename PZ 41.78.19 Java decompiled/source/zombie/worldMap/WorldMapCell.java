// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap;

import java.util.ArrayList;

public final class WorldMapCell {
    public int m_x;
    public int m_y;
    public final ArrayList<WorldMapFeature> m_features = new ArrayList<>();

    public void hitTest(float x, float y, ArrayList<WorldMapFeature> features) {
        x -= this.m_x * 300;
        y -= this.m_y * 300;

        for (int int0 = 0; int0 < this.m_features.size(); int0++) {
            WorldMapFeature worldMapFeature = this.m_features.get(int0);
            if (worldMapFeature.containsPoint(x, y)) {
                features.add(worldMapFeature);
            }
        }
    }

    public void dispose() {
        for (WorldMapFeature worldMapFeature : this.m_features) {
            worldMapFeature.dispose();
        }

        this.m_features.clear();
    }
}
