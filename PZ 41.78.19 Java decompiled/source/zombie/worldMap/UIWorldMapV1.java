// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap;

import org.joml.Matrix4f;
import zombie.config.ConfigOption;
import zombie.input.Mouse;
import zombie.inventory.types.MapItem;
import zombie.worldMap.markers.WorldMapMarkers;
import zombie.worldMap.markers.WorldMapMarkersV1;
import zombie.worldMap.styles.WorldMapStyle;
import zombie.worldMap.styles.WorldMapStyleV1;
import zombie.worldMap.symbols.WorldMapSymbols;
import zombie.worldMap.symbols.WorldMapSymbolsV1;

public class UIWorldMapV1 {
    final UIWorldMap m_ui;
    protected final WorldMap m_worldMap;
    protected final WorldMapStyle m_style;
    protected final WorldMapRenderer m_renderer;
    protected final WorldMapMarkers m_markers;
    protected WorldMapSymbols m_symbols;
    protected WorldMapMarkersV1 m_markersV1 = null;
    protected WorldMapStyleV1 m_styleV1 = null;
    protected WorldMapSymbolsV1 m_symbolsV1 = null;

    public UIWorldMapV1(UIWorldMap ui) {
        this.m_ui = ui;
        this.m_worldMap = this.m_ui.m_worldMap;
        this.m_style = this.m_ui.m_style;
        this.m_renderer = this.m_ui.m_renderer;
        this.m_markers = this.m_ui.m_markers;
        this.m_symbols = this.m_ui.m_symbols;
    }

    public void setMapItem(MapItem mapItem) {
        this.m_ui.setMapItem(mapItem);
        this.m_symbols = this.m_ui.m_symbols;
    }

    public WorldMapRenderer getRenderer() {
        return this.m_renderer;
    }

    public WorldMapMarkers getMarkers() {
        return this.m_markers;
    }

    public WorldMapStyle getStyle() {
        return this.m_style;
    }

    public WorldMapMarkersV1 getMarkersAPI() {
        if (this.m_markersV1 == null) {
            this.m_markersV1 = new WorldMapMarkersV1(this.m_ui);
        }

        return this.m_markersV1;
    }

    public WorldMapStyleV1 getStyleAPI() {
        if (this.m_styleV1 == null) {
            this.m_styleV1 = new WorldMapStyleV1(this.m_ui);
        }

        return this.m_styleV1;
    }

    public WorldMapSymbolsV1 getSymbolsAPI() {
        if (this.m_symbolsV1 == null) {
            this.m_symbolsV1 = new WorldMapSymbolsV1(this.m_ui, this.m_symbols);
        }

        return this.m_symbolsV1;
    }

    public void addData(String fileName) {
        boolean boolean0 = this.m_worldMap.hasData();
        this.m_worldMap.addData(fileName);
        if (!boolean0) {
            this.m_renderer
                .setMap(
                    this.m_worldMap,
                    this.m_ui.getAbsoluteX().intValue(),
                    this.m_ui.getAbsoluteY().intValue(),
                    this.m_ui.getWidth().intValue(),
                    this.m_ui.getHeight().intValue()
                );
            this.resetView();
        }
    }

    public int getDataCount() {
        return this.m_worldMap.getDataCount();
    }

    public String getDataFileByIndex(int index) {
        WorldMapData worldMapData = this.m_worldMap.getDataByIndex(index);
        return worldMapData.m_relativeFileName;
    }

    public void clearData() {
        this.m_worldMap.clearData();
    }

    public void endDirectoryData() {
        this.m_worldMap.endDirectoryData();
    }

    public void addImages(String directory) {
        boolean boolean0 = this.m_worldMap.hasImages();
        this.m_worldMap.addImages(directory);
        if (!boolean0) {
            this.m_renderer
                .setMap(
                    this.m_worldMap,
                    this.m_ui.getAbsoluteX().intValue(),
                    this.m_ui.getAbsoluteY().intValue(),
                    this.m_ui.getWidth().intValue(),
                    this.m_ui.getHeight().intValue()
                );
            this.resetView();
        }
    }

    public int getImagesCount() {
        return this.m_worldMap.getImagesCount();
    }

    public void setBoundsInCells(int minX, int minY, int maxX, int maxY) {
        boolean boolean0 = minX * 300 != this.m_worldMap.m_minX
            || minY * 300 != this.m_worldMap.m_minY
            || maxX * 300 + 299 != this.m_worldMap.m_maxX
            || maxY + 300 + 299 != this.m_worldMap.m_maxY;
        this.m_worldMap.setBoundsInCells(minX, minY, maxX, maxY);
        if (boolean0 && this.m_worldMap.hasData()) {
            this.resetView();
        }
    }

    public void setBoundsInSquares(int minX, int minY, int maxX, int maxY) {
        boolean boolean0 = minX != this.m_worldMap.m_minX || minY != this.m_worldMap.m_minY || maxX != this.m_worldMap.m_maxX || maxY != this.m_worldMap.m_maxY;
        this.m_worldMap.setBoundsInSquares(minX, minY, maxX, maxY);
        if (boolean0 && this.m_worldMap.hasData()) {
            this.resetView();
        }
    }

    public void setBoundsFromWorld() {
        this.m_worldMap.setBoundsFromWorld();
    }

    public void setBoundsFromData() {
        this.m_worldMap.setBoundsFromData();
    }

    public int getMinXInCells() {
        return this.m_worldMap.getMinXInCells();
    }

    public int getMinYInCells() {
        return this.m_worldMap.getMinYInCells();
    }

    public int getMaxXInCells() {
        return this.m_worldMap.getMaxXInCells();
    }

    public int getMaxYInCells() {
        return this.m_worldMap.getMaxYInCells();
    }

    public int getWidthInCells() {
        return this.m_worldMap.getWidthInCells();
    }

    public int getHeightInCells() {
        return this.m_worldMap.getHeightInCells();
    }

    public int getMinXInSquares() {
        return this.m_worldMap.getMinXInSquares();
    }

    public int getMinYInSquares() {
        return this.m_worldMap.getMinYInSquares();
    }

    public int getMaxXInSquares() {
        return this.m_worldMap.getMaxXInSquares();
    }

    public int getMaxYInSquares() {
        return this.m_worldMap.getMaxYInSquares();
    }

    public int getWidthInSquares() {
        return this.m_worldMap.getWidthInSquares();
    }

    public int getHeightInSquares() {
        return this.m_worldMap.getHeightInSquares();
    }

    public float uiToWorldX(float uiX, float uiY, float zoomF, float centerWorldX, float centerWorldY) {
        return this.m_renderer
            .uiToWorldX(uiX, uiY, zoomF, centerWorldX, centerWorldY, this.m_renderer.getProjectionMatrix(), this.m_renderer.getModelViewMatrix());
    }

    public float uiToWorldY(float uiX, float uiY, float zoomF, float centerWorldX, float centerWorldY) {
        return this.m_renderer
            .uiToWorldY(uiX, uiY, zoomF, centerWorldX, centerWorldY, this.m_renderer.getProjectionMatrix(), this.m_renderer.getModelViewMatrix());
    }

    protected float worldToUIX(float float0, float float1, float float2, float float3, float float4, Matrix4f matrix4f0, Matrix4f matrix4f1) {
        return this.m_renderer.worldToUIX(float0, float1, float2, float3, float4, matrix4f0, matrix4f1);
    }

    protected float worldToUIY(float float0, float float1, float float2, float float3, float float4, Matrix4f matrix4f0, Matrix4f matrix4f1) {
        return this.m_renderer.worldToUIY(float0, float1, float2, float3, float4, matrix4f0, matrix4f1);
    }

    protected float worldOriginUIX(float float0, float float1) {
        return this.m_renderer.worldOriginUIX(float0, float1);
    }

    protected float worldOriginUIY(float float0, float float1) {
        return this.m_renderer.worldOriginUIY(float0, float1);
    }

    protected float zoomMult() {
        return this.m_renderer.zoomMult();
    }

    protected float getWorldScale(float float0) {
        return this.m_renderer.getWorldScale(float0);
    }

    public float worldOriginX() {
        return this.m_renderer.worldOriginUIX(this.m_renderer.getDisplayZoomF(), this.m_renderer.getCenterWorldX());
    }

    public float worldOriginY() {
        return this.m_renderer.worldOriginUIY(this.m_renderer.getDisplayZoomF(), this.m_renderer.getCenterWorldY());
    }

    public float getBaseZoom() {
        return this.m_renderer.getBaseZoom();
    }

    public float getZoomF() {
        return this.m_renderer.getDisplayZoomF();
    }

    public float getWorldScale() {
        return this.m_renderer.getWorldScale(this.m_renderer.getDisplayZoomF());
    }

    public float getCenterWorldX() {
        return this.m_renderer.getCenterWorldX();
    }

    public float getCenterWorldY() {
        return this.m_renderer.getCenterWorldY();
    }

    public float uiToWorldX(float uiX, float uiY) {
        return !this.m_worldMap.hasData() && !this.m_worldMap.hasImages()
            ? 0.0F
            : this.uiToWorldX(uiX, uiY, this.m_renderer.getDisplayZoomF(), this.m_renderer.getCenterWorldX(), this.m_renderer.getCenterWorldY());
    }

    public float uiToWorldY(float uiX, float uiY) {
        return !this.m_worldMap.hasData() && !this.m_worldMap.hasImages()
            ? 0.0F
            : this.uiToWorldY(uiX, uiY, this.m_renderer.getDisplayZoomF(), this.m_renderer.getCenterWorldY(), this.m_renderer.getCenterWorldY());
    }

    public float worldToUIX(float worldX, float worldY) {
        return !this.m_worldMap.hasData() && !this.m_worldMap.hasImages()
            ? 0.0F
            : this.worldToUIX(
                worldX,
                worldY,
                this.m_renderer.getDisplayZoomF(),
                this.m_renderer.getCenterWorldX(),
                this.m_renderer.getCenterWorldY(),
                this.m_renderer.getProjectionMatrix(),
                this.m_renderer.getModelViewMatrix()
            );
    }

    public float worldToUIY(float worldX, float worldY) {
        return !this.m_worldMap.hasData() && !this.m_worldMap.hasImages()
            ? 0.0F
            : this.worldToUIY(
                worldX,
                worldY,
                this.m_renderer.getDisplayZoomF(),
                this.m_renderer.getCenterWorldX(),
                this.m_renderer.getCenterWorldY(),
                this.m_renderer.getProjectionMatrix(),
                this.m_renderer.getModelViewMatrix()
            );
    }

    public void centerOn(float worldX, float worldY) {
        if (this.m_worldMap.hasData() || this.m_worldMap.hasImages()) {
            this.m_renderer.centerOn(worldX, worldY);
        }
    }

    public void moveView(float dx, float dy) {
        if (this.m_worldMap.hasData() || this.m_worldMap.hasImages()) {
            this.m_renderer.moveView((int)dx, (int)dy);
        }
    }

    public void zoomAt(float uiX, float uiY, float delta) {
        if (this.m_worldMap.hasData() || this.m_worldMap.hasImages()) {
            this.m_renderer.zoomAt((int)uiX, (int)uiY, -((int)delta));
        }
    }

    public void setZoom(float zoom) {
        this.m_renderer.setZoom(zoom);
    }

    public void resetView() {
        if (this.m_worldMap.hasData() || this.m_worldMap.hasImages()) {
            this.m_renderer.resetView();
        }
    }

    public float mouseToWorldX() {
        float float0 = Mouse.getXA() - this.m_ui.getAbsoluteX().intValue();
        float float1 = Mouse.getYA() - this.m_ui.getAbsoluteY().intValue();
        return this.uiToWorldX(float0, float1);
    }

    public float mouseToWorldY() {
        float float0 = Mouse.getXA() - this.m_ui.getAbsoluteX().intValue();
        float float1 = Mouse.getYA() - this.m_ui.getAbsoluteY().intValue();
        return this.uiToWorldY(float0, float1);
    }

    public void setBackgroundRGBA(float r, float g, float b, float a) {
        this.m_ui.m_color.init(r, g, b, a);
    }

    public void setDropShadowWidth(int width) {
        this.m_ui.m_renderer.setDropShadowWidth(width);
    }

    public void setUnvisitedRGBA(float r, float g, float b, float a) {
        WorldMapVisited.getInstance().setUnvisitedRGBA(r, g, b, a);
    }

    public void setUnvisitedGridRGBA(float r, float g, float b, float a) {
        WorldMapVisited.getInstance().setUnvisitedGridRGBA(r, g, b, a);
    }

    public int getOptionCount() {
        return this.m_renderer.getOptionCount();
    }

    public ConfigOption getOptionByIndex(int index) {
        return this.m_renderer.getOptionByIndex(index);
    }

    public void setBoolean(String name, boolean value) {
        this.m_renderer.setBoolean(name, value);
    }

    public boolean getBoolean(String name) {
        return this.m_renderer.getBoolean(name);
    }

    public void setDouble(String name, double value) {
        this.m_renderer.setDouble(name, value);
    }

    public double getDouble(String name, double defaultValue) {
        return this.m_renderer.getDouble(name, defaultValue);
    }
}
