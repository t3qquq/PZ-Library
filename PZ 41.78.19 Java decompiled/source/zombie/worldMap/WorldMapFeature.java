// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap;

import java.util.ArrayList;

public final class WorldMapFeature {
    public final WorldMapCell m_cell;
    public final ArrayList<WorldMapGeometry> m_geometries = new ArrayList<>();
    public WorldMapProperties m_properties = null;

    WorldMapFeature(WorldMapCell worldMapCell) {
        this.m_cell = worldMapCell;
    }

    public boolean hasLineString() {
        for (int int0 = 0; int0 < this.m_geometries.size(); int0++) {
            if (this.m_geometries.get(int0).m_type == WorldMapGeometry.Type.LineString) {
                return true;
            }
        }

        return false;
    }

    public boolean hasPoint() {
        for (int int0 = 0; int0 < this.m_geometries.size(); int0++) {
            if (this.m_geometries.get(int0).m_type == WorldMapGeometry.Type.Point) {
                return true;
            }
        }

        return false;
    }

    public boolean hasPolygon() {
        for (int int0 = 0; int0 < this.m_geometries.size(); int0++) {
            if (this.m_geometries.get(int0).m_type == WorldMapGeometry.Type.Polygon) {
                return true;
            }
        }

        return false;
    }

    public boolean containsPoint(float x, float y) {
        for (int int0 = 0; int0 < this.m_geometries.size(); int0++) {
            WorldMapGeometry worldMapGeometry = this.m_geometries.get(int0);
            if (worldMapGeometry.containsPoint(x, y)) {
                return true;
            }
        }

        return false;
    }

    public void dispose() {
        for (WorldMapGeometry worldMapGeometry : this.m_geometries) {
            worldMapGeometry.dispose();
        }

        this.m_properties.clear();
    }
}
