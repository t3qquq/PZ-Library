// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap.markers;

import java.util.ArrayList;
import java.util.Objects;
import zombie.Lua.LuaManager;
import zombie.worldMap.UIWorldMap;

public class WorldMapMarkersV1 {
    private final UIWorldMap m_ui;
    private final ArrayList<WorldMapMarkersV1.WorldMapMarkerV1> m_markers = new ArrayList<>();

    public WorldMapMarkersV1(UIWorldMap ui) {
        Objects.requireNonNull(ui);
        this.m_ui = ui;
    }

    public WorldMapMarkersV1.WorldMapGridSquareMarkerV1 addGridSquareMarker(int worldX, int worldY, int radius, float r, float g, float b, float a) {
        WorldMapGridSquareMarker worldMapGridSquareMarker = this.m_ui.getAPIv1().getMarkers().addGridSquareMarker(worldX, worldY, radius, r, g, b, a);
        WorldMapMarkersV1.WorldMapGridSquareMarkerV1 worldMapGridSquareMarkerV1 = new WorldMapMarkersV1.WorldMapGridSquareMarkerV1(worldMapGridSquareMarker);
        this.m_markers.add(worldMapGridSquareMarkerV1);
        return worldMapGridSquareMarkerV1;
    }

    public void removeMarker(WorldMapMarkersV1.WorldMapMarkerV1 marker) {
        if (this.m_markers.remove(marker)) {
            this.m_ui.getAPIv1().getMarkers().removeMarker(marker.m_marker);
        }
    }

    public void clear() {
        this.m_ui.getAPIv1().getMarkers().clear();
        this.m_markers.clear();
    }

    public static void setExposed(LuaManager.Exposer exposer) {
        exposer.setExposed(WorldMapMarkersV1.class);
        exposer.setExposed(WorldMapMarkersV1.WorldMapMarkerV1.class);
        exposer.setExposed(WorldMapMarkersV1.WorldMapGridSquareMarkerV1.class);
    }

    public static final class WorldMapGridSquareMarkerV1 extends WorldMapMarkersV1.WorldMapMarkerV1 {
        final WorldMapGridSquareMarker m_gridSquareMarker;

        WorldMapGridSquareMarkerV1(WorldMapGridSquareMarker worldMapGridSquareMarker) {
            super(worldMapGridSquareMarker);
            this.m_gridSquareMarker = worldMapGridSquareMarker;
        }

        public void setBlink(boolean blink) {
            this.m_gridSquareMarker.setBlink(blink);
        }

        public void setMinScreenRadius(int pixels) {
            this.m_gridSquareMarker.setMinScreenRadius(pixels);
        }
    }

    public static class WorldMapMarkerV1 {
        final WorldMapMarker m_marker;

        WorldMapMarkerV1(WorldMapMarker worldMapMarker) {
            this.m_marker = worldMapMarker;
        }
    }
}
