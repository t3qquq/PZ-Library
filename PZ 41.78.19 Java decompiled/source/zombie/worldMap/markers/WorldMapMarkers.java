// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap.markers;

import java.util.ArrayList;
import zombie.util.Pool;
import zombie.worldMap.UIWorldMap;

public final class WorldMapMarkers {
    private static final Pool<WorldMapGridSquareMarker> s_gridSquareMarkerPool = new Pool<>(WorldMapGridSquareMarker::new);
    private final ArrayList<WorldMapMarker> m_markers = new ArrayList<>();

    public WorldMapGridSquareMarker addGridSquareMarker(int worldX, int worldY, int radius, float r, float g, float b, float a) {
        WorldMapGridSquareMarker worldMapGridSquareMarker = s_gridSquareMarkerPool.alloc().init(worldX, worldY, radius, r, g, b, a);
        this.m_markers.add(worldMapGridSquareMarker);
        return worldMapGridSquareMarker;
    }

    public void removeMarker(WorldMapMarker marker) {
        if (this.m_markers.contains(marker)) {
            this.m_markers.remove(marker);
            marker.release();
        }
    }

    public void clear() {
        for (int int0 = 0; int0 < this.m_markers.size(); int0++) {
            this.m_markers.get(int0).release();
        }

        this.m_markers.clear();
    }

    public void render(UIWorldMap ui) {
        for (int int0 = 0; int0 < this.m_markers.size(); int0++) {
            this.m_markers.get(int0).render(ui);
        }
    }
}
