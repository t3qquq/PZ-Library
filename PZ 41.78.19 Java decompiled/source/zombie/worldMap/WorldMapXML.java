// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap;

import java.util.ArrayList;
import org.w3c.dom.Element;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.util.Lambda;
import zombie.util.PZXmlParserException;
import zombie.util.PZXmlUtil;
import zombie.util.SharedStrings;

public final class WorldMapXML {
    private final SharedStrings m_sharedStrings = new SharedStrings();
    private final WorldMapPoint m_point = new WorldMapPoint();
    private final WorldMapProperties m_properties = new WorldMapProperties();
    private final ArrayList<WorldMapProperties> m_sharedProperties = new ArrayList<>();

    public boolean read(String string, WorldMapData worldMapData) throws PZXmlParserException {
        Element element = PZXmlUtil.parseXml(string);
        if (element.getNodeName().equals("world")) {
            this.parseWorld(element, worldMapData);
            return true;
        } else {
            return false;
        }
    }

    private void parseWorld(Element element, WorldMapData worldMapData) {
        Lambda.forEachFrom(PZXmlUtil::forEachElement, element, worldMapData, (elementx, worldMapDatax) -> {
            if (!elementx.getNodeName().equals("cell")) {
                DebugLog.General.warn("Warning: Unrecognised element '" + elementx.getNodeName());
            } else {
                WorldMapCell worldMapCell = this.parseCell(elementx);
                worldMapDatax.m_cells.add(worldMapCell);
            }
        });
    }

    private WorldMapCell parseCell(Element element) {
        WorldMapCell worldMapCell = new WorldMapCell();
        worldMapCell.m_x = PZMath.tryParseInt(element.getAttribute("x"), 0);
        worldMapCell.m_y = PZMath.tryParseInt(element.getAttribute("y"), 0);
        Lambda.forEachFrom(PZXmlUtil::forEachElement, element, worldMapCell, (elementx, worldMapCell1) -> {
            try {
                String string = elementx.getNodeName();
                if ("feature".equalsIgnoreCase(string)) {
                    WorldMapFeature worldMapFeature = this.parseFeature(worldMapCell, elementx);
                    worldMapCell1.m_features.add(worldMapFeature);
                }
            } catch (Exception exception) {
                DebugLog.General.error("Error while parsing xml element: " + elementx.getNodeName());
                DebugLog.General.error(exception);
            }
        });
        return worldMapCell;
    }

    private WorldMapFeature parseFeature(WorldMapCell worldMapCell, Element element) {
        WorldMapFeature worldMapFeature = new WorldMapFeature(worldMapCell);
        Lambda.forEachFrom(PZXmlUtil::forEachElement, element, worldMapFeature, (elementx, worldMapFeaturex) -> {
            try {
                String string = elementx.getNodeName();
                if ("geometry".equalsIgnoreCase(string)) {
                    WorldMapGeometry worldMapGeometry = this.parseGeometry(elementx);
                    worldMapFeaturex.m_geometries.add(worldMapGeometry);
                }

                if ("properties".equalsIgnoreCase(string)) {
                    this.parseFeatureProperties(elementx, worldMapFeaturex);
                }
            } catch (Exception exception) {
                DebugLog.General.error("Error while parsing xml element: " + elementx.getNodeName());
                DebugLog.General.error(exception);
            }
        });
        return worldMapFeature;
    }

    private void parseFeatureProperties(Element element, WorldMapFeature worldMapFeature) {
        this.m_properties.clear();
        Lambda.forEachFrom(PZXmlUtil::forEachElement, element, worldMapFeature, (elementx, var2x) -> {
            try {
                String string0 = elementx.getNodeName();
                if ("property".equalsIgnoreCase(string0)) {
                    String string1 = this.m_sharedStrings.get(elementx.getAttribute("name"));
                    String string2 = this.m_sharedStrings.get(elementx.getAttribute("value"));
                    this.m_properties.put(string1, string2);
                }
            } catch (Exception exception) {
                DebugLog.General.error("Error while parsing xml element: " + elementx.getNodeName());
                DebugLog.General.error(exception);
            }
        });
        worldMapFeature.m_properties = this.getOrCreateProperties(this.m_properties);
    }

    private WorldMapProperties getOrCreateProperties(WorldMapProperties worldMapProperties0) {
        for (int int0 = 0; int0 < this.m_sharedProperties.size(); int0++) {
            if (this.m_sharedProperties.get(int0).equals(worldMapProperties0)) {
                return this.m_sharedProperties.get(int0);
            }
        }

        WorldMapProperties worldMapProperties1 = new WorldMapProperties();
        worldMapProperties1.putAll(worldMapProperties0);
        this.m_sharedProperties.add(worldMapProperties1);
        return worldMapProperties1;
    }

    private WorldMapGeometry parseGeometry(Element element) {
        WorldMapGeometry worldMapGeometry = new WorldMapGeometry();
        worldMapGeometry.m_type = WorldMapGeometry.Type.valueOf(element.getAttribute("type"));
        Lambda.forEachFrom(PZXmlUtil::forEachElement, element, worldMapGeometry, (elementx, worldMapGeometryx) -> {
            try {
                String string = elementx.getNodeName();
                if ("coordinates".equalsIgnoreCase(string)) {
                    WorldMapPoints worldMapPoints = new WorldMapPoints();
                    this.parseGeometryCoordinates(elementx, worldMapPoints);
                    worldMapGeometryx.m_points.add(worldMapPoints);
                }
            } catch (Exception exception) {
                DebugLog.General.error("Error while parsing xml element: " + elementx.getNodeName());
                DebugLog.General.error(exception);
            }
        });
        worldMapGeometry.calculateBounds();
        return worldMapGeometry;
    }

    private void parseGeometryCoordinates(Element element, WorldMapPoints worldMapPoints) {
        Lambda.forEachFrom(PZXmlUtil::forEachElement, element, worldMapPoints, (elementx, worldMapPointsx) -> {
            try {
                String string = elementx.getNodeName();
                if ("point".equalsIgnoreCase(string)) {
                    WorldMapPoint worldMapPoint = this.parsePoint(elementx, this.m_point);
                    worldMapPointsx.add(worldMapPoint.x);
                    worldMapPointsx.add(worldMapPoint.y);
                }
            } catch (Exception exception) {
                DebugLog.General.error("Error while parsing xml element: " + elementx.getNodeName());
                DebugLog.General.error(exception);
            }
        });
    }

    private WorldMapPoint parsePoint(Element element, WorldMapPoint worldMapPoint) {
        worldMapPoint.x = PZMath.tryParseInt(element.getAttribute("x"), 0);
        worldMapPoint.y = PZMath.tryParseInt(element.getAttribute("y"), 0);
        return worldMapPoint;
    }
}
