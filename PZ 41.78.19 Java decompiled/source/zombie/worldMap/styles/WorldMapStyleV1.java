// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap.styles;

import java.util.ArrayList;
import java.util.Objects;
import zombie.Lua.LuaManager;
import zombie.core.math.PZMath;
import zombie.core.textures.Texture;
import zombie.worldMap.UIWorldMap;
import zombie.worldMap.UIWorldMapV1;

public class WorldMapStyleV1 {
    public UIWorldMap m_ui;
    public UIWorldMapV1 m_api;
    public WorldMapStyle m_style;
    public final ArrayList<WorldMapStyleV1.WorldMapStyleLayerV1> m_layers = new ArrayList<>();

    public WorldMapStyleV1(UIWorldMap ui) {
        Objects.requireNonNull(ui);
        this.m_ui = ui;
        this.m_api = ui.getAPIv1();
        this.m_style = this.m_api.getStyle();
    }

    public WorldMapStyleV1.WorldMapStyleLayerV1 newLineLayer(String id) throws IllegalArgumentException {
        WorldMapStyleV1.WorldMapLineStyleLayerV1 worldMapLineStyleLayerV1 = new WorldMapStyleV1.WorldMapLineStyleLayerV1(this, id);
        this.m_layers.add(worldMapLineStyleLayerV1);
        return worldMapLineStyleLayerV1;
    }

    public WorldMapStyleV1.WorldMapStyleLayerV1 newPolygonLayer(String id) throws IllegalArgumentException {
        WorldMapStyleV1.WorldMapPolygonStyleLayerV1 worldMapPolygonStyleLayerV1 = new WorldMapStyleV1.WorldMapPolygonStyleLayerV1(this, id);
        this.m_layers.add(worldMapPolygonStyleLayerV1);
        return worldMapPolygonStyleLayerV1;
    }

    public WorldMapStyleV1.WorldMapStyleLayerV1 newTextureLayer(String id) throws IllegalArgumentException {
        WorldMapStyleV1.WorldMapTextureStyleLayerV1 worldMapTextureStyleLayerV1 = new WorldMapStyleV1.WorldMapTextureStyleLayerV1(this, id);
        this.m_layers.add(worldMapTextureStyleLayerV1);
        return worldMapTextureStyleLayerV1;
    }

    public int getLayerCount() {
        return this.m_layers.size();
    }

    public WorldMapStyleV1.WorldMapStyleLayerV1 getLayerByIndex(int index) {
        return this.m_layers.get(index);
    }

    public WorldMapStyleV1.WorldMapStyleLayerV1 getLayerByName(String id) {
        int int0 = this.indexOfLayer(id);
        return int0 == -1 ? null : this.m_layers.get(int0);
    }

    public int indexOfLayer(String id) {
        for (int int0 = 0; int0 < this.m_layers.size(); int0++) {
            WorldMapStyleV1.WorldMapStyleLayerV1 worldMapStyleLayerV1 = this.m_layers.get(int0);
            if (worldMapStyleLayerV1.m_layer.m_id.equals(id)) {
                return int0;
            }
        }

        return -1;
    }

    public void moveLayer(int indexFrom, int indexTo) {
        WorldMapStyleLayer worldMapStyleLayer = this.m_style.m_layers.remove(indexFrom);
        this.m_style.m_layers.add(indexTo, worldMapStyleLayer);
        WorldMapStyleV1.WorldMapStyleLayerV1 worldMapStyleLayerV1 = this.m_layers.remove(indexFrom);
        this.m_layers.add(indexTo, worldMapStyleLayerV1);
    }

    public void removeLayerById(String id) {
        int int0 = this.indexOfLayer(id);
        if (int0 != -1) {
            this.removeLayerByIndex(int0);
        }
    }

    public void removeLayerByIndex(int index) {
        this.m_style.m_layers.remove(index);
        this.m_layers.remove(index);
    }

    public void clear() {
        this.m_style.m_layers.clear();
        this.m_layers.clear();
    }

    public static void setExposed(LuaManager.Exposer exposer) {
        exposer.setExposed(WorldMapStyleV1.class);
        exposer.setExposed(WorldMapStyleV1.WorldMapStyleLayerV1.class);
        exposer.setExposed(WorldMapStyleV1.WorldMapLineStyleLayerV1.class);
        exposer.setExposed(WorldMapStyleV1.WorldMapPolygonStyleLayerV1.class);
        exposer.setExposed(WorldMapStyleV1.WorldMapTextureStyleLayerV1.class);
    }

    public static class WorldMapLineStyleLayerV1 extends WorldMapStyleV1.WorldMapStyleLayerV1 {
        WorldMapLineStyleLayer m_lineStyle = (WorldMapLineStyleLayer)this.m_layer;

        WorldMapLineStyleLayerV1(WorldMapStyleV1 worldMapStyleV1, String string) {
            super(worldMapStyleV1, new WorldMapLineStyleLayer(string));
        }

        public void setFilter(String string0, String string1) {
            this.m_lineStyle.m_filterKey = string0;
            this.m_lineStyle.m_filterValue = string1;
            this.m_lineStyle.m_filter = (worldMapFeature, var3) -> worldMapFeature.hasLineString() && string1.equals(worldMapFeature.m_properties.get(string0));
        }

        public void addFill(float float0, int int0, int int1, int int2, int int3) {
            this.m_lineStyle.m_fill.add(new WorldMapStyleLayer.ColorStop(float0, int0, int1, int2, int3));
        }

        public void addLineWidth(float float0, float float1) {
            this.m_lineStyle.m_lineWidth.add(new WorldMapStyleLayer.FloatStop(float0, float1));
        }
    }

    public static class WorldMapPolygonStyleLayerV1 extends WorldMapStyleV1.WorldMapStyleLayerV1 {
        WorldMapPolygonStyleLayer m_polygonStyle = (WorldMapPolygonStyleLayer)this.m_layer;

        WorldMapPolygonStyleLayerV1(WorldMapStyleV1 worldMapStyleV1, String string) {
            super(worldMapStyleV1, new WorldMapPolygonStyleLayer(string));
        }

        public void setFilter(String string0, String string1) {
            this.m_polygonStyle.m_filterKey = string0;
            this.m_polygonStyle.m_filterValue = string1;
            this.m_polygonStyle.m_filter = (worldMapFeature, var3) -> worldMapFeature.hasPolygon() && string1.equals(worldMapFeature.m_properties.get(string0));
        }

        public String getFilterKey() {
            return this.m_polygonStyle.m_filterKey;
        }

        public String getFilterValue() {
            return this.m_polygonStyle.m_filterValue;
        }

        public void addFill(float float0, int int0, int int1, int int2, int int3) {
            this.m_polygonStyle.m_fill.add(new WorldMapStyleLayer.ColorStop(float0, int0, int1, int2, int3));
        }

        public void addScale(float float0, float float1) {
            this.m_polygonStyle.m_scale.add(new WorldMapStyleLayer.FloatStop(float0, float1));
        }

        public void addTexture(float float0, String string) {
            this.m_polygonStyle.m_texture.add(new WorldMapStyleLayer.TextureStop(float0, string));
        }

        public void removeFill(int int0) {
            this.m_polygonStyle.m_fill.remove(int0);
        }

        public void removeTexture(int int0) {
            this.m_polygonStyle.m_texture.remove(int0);
        }

        public void moveFill(int int0, int int1) {
            WorldMapStyleLayer.ColorStop colorStop = this.m_polygonStyle.m_fill.remove(int0);
            this.m_polygonStyle.m_fill.add(int1, colorStop);
        }

        public void moveTexture(int int0, int int1) {
            WorldMapStyleLayer.TextureStop textureStop = this.m_polygonStyle.m_texture.remove(int0);
            this.m_polygonStyle.m_texture.add(int1, textureStop);
        }

        public int getFillStops() {
            return this.m_polygonStyle.m_fill.size();
        }

        public void setFillRGBA(int int1, int int0, int int2, int int3, int int4) {
            this.m_polygonStyle.m_fill.get(int1).r = int0;
            this.m_polygonStyle.m_fill.get(int1).g = int2;
            this.m_polygonStyle.m_fill.get(int1).b = int3;
            this.m_polygonStyle.m_fill.get(int1).a = int4;
        }

        public void setFillZoom(int int0, float float0) {
            this.m_polygonStyle.m_fill.get(int0).m_zoom = PZMath.clamp(float0, 0.0F, 24.0F);
        }

        public float getFillZoom(int int0) {
            return this.m_polygonStyle.m_fill.get(int0).m_zoom;
        }

        public int getFillRed(int int0) {
            return this.m_polygonStyle.m_fill.get(int0).r;
        }

        public int getFillGreen(int int0) {
            return this.m_polygonStyle.m_fill.get(int0).g;
        }

        public int getFillBlue(int int0) {
            return this.m_polygonStyle.m_fill.get(int0).b;
        }

        public int getFillAlpha(int int0) {
            return this.m_polygonStyle.m_fill.get(int0).a;
        }

        public int getTextureStops() {
            return this.m_polygonStyle.m_texture.size();
        }

        public void setTextureZoom(int int0, float float0) {
            this.m_polygonStyle.m_texture.get(int0).m_zoom = PZMath.clamp(float0, 0.0F, 24.0F);
        }

        public float getTextureZoom(int int0) {
            return this.m_polygonStyle.m_texture.get(int0).m_zoom;
        }

        public void setTexturePath(int int0, String string) {
            this.m_polygonStyle.m_texture.get(int0).texturePath = string;
            this.m_polygonStyle.m_texture.get(int0).texture = Texture.getTexture(string);
        }

        public String getTexturePath(int int0) {
            return this.m_polygonStyle.m_texture.get(int0).texturePath;
        }

        public Texture getTexture(int int0) {
            return this.m_polygonStyle.m_texture.get(int0).texture;
        }
    }

    public static class WorldMapStyleLayerV1 {
        WorldMapStyleV1 m_owner;
        WorldMapStyleLayer m_layer;

        WorldMapStyleLayerV1(WorldMapStyleV1 worldMapStyleV1, WorldMapStyleLayer worldMapStyleLayer) {
            this.m_owner = worldMapStyleV1;
            this.m_layer = worldMapStyleLayer;
            worldMapStyleV1.m_style.m_layers.add(this.m_layer);
        }

        public String getTypeString() {
            return this.m_layer.getTypeString();
        }

        public void setId(String id) {
            this.m_layer.m_id = id;
        }

        public String getId() {
            return this.m_layer.m_id;
        }

        public void setMinZoom(float minZoom) {
            this.m_layer.m_minZoom = minZoom;
        }

        public float getMinZoom() {
            return this.m_layer.m_minZoom;
        }
    }

    public static class WorldMapTextureStyleLayerV1 extends WorldMapStyleV1.WorldMapStyleLayerV1 {
        WorldMapTextureStyleLayer m_textureStyle = (WorldMapTextureStyleLayer)this.m_layer;

        WorldMapTextureStyleLayerV1(WorldMapStyleV1 worldMapStyleV1, String string) {
            super(worldMapStyleV1, new WorldMapTextureStyleLayer(string));
        }

        public void addFill(float float0, int int0, int int1, int int2, int int3) {
            this.m_textureStyle.m_fill.add(new WorldMapStyleLayer.ColorStop(float0, int0, int1, int2, int3));
        }

        public void addTexture(float float0, String string) {
            this.m_textureStyle.m_texture.add(new WorldMapStyleLayer.TextureStop(float0, string));
        }

        public void removeFill(int int0) {
            this.m_textureStyle.m_fill.remove(int0);
        }

        public void removeAllFill() {
            this.m_textureStyle.m_fill.clear();
        }

        public void removeTexture(int int0) {
            this.m_textureStyle.m_texture.remove(int0);
        }

        public void removeAllTexture() {
            this.m_textureStyle.m_texture.clear();
        }

        public void moveFill(int int0, int int1) {
            WorldMapStyleLayer.ColorStop colorStop = this.m_textureStyle.m_fill.remove(int0);
            this.m_textureStyle.m_fill.add(int1, colorStop);
        }

        public void moveTexture(int int0, int int1) {
            WorldMapStyleLayer.TextureStop textureStop = this.m_textureStyle.m_texture.remove(int0);
            this.m_textureStyle.m_texture.add(int1, textureStop);
        }

        public void setBoundsInSquares(int int0, int int1, int int2, int int3) {
            this.m_textureStyle.m_worldX1 = int0;
            this.m_textureStyle.m_worldY1 = int1;
            this.m_textureStyle.m_worldX2 = int2;
            this.m_textureStyle.m_worldY2 = int3;
        }

        public int getMinXInSquares() {
            return this.m_textureStyle.m_worldX1;
        }

        public int getMinYInSquares() {
            return this.m_textureStyle.m_worldY1;
        }

        public int getMaxXInSquares() {
            return this.m_textureStyle.m_worldX2;
        }

        public int getMaxYInSquares() {
            return this.m_textureStyle.m_worldY2;
        }

        public int getWidthInSquares() {
            return this.m_textureStyle.m_worldX2 - this.m_textureStyle.m_worldX1;
        }

        public int getHeightInSquares() {
            return this.m_textureStyle.m_worldY2 - this.m_textureStyle.m_worldY1;
        }

        public void setTile(boolean boolean0) {
            this.m_textureStyle.m_tile = boolean0;
        }

        public boolean isTile() {
            return this.m_textureStyle.m_tile;
        }

        public void setUseWorldBounds(boolean boolean0) {
            this.m_textureStyle.m_useWorldBounds = boolean0;
        }

        public boolean isUseWorldBounds() {
            return this.m_textureStyle.m_useWorldBounds;
        }

        public int getFillStops() {
            return this.m_textureStyle.m_fill.size();
        }

        public void setFillRGBA(int int1, int int0, int int2, int int3, int int4) {
            this.m_textureStyle.m_fill.get(int1).r = int0;
            this.m_textureStyle.m_fill.get(int1).g = int2;
            this.m_textureStyle.m_fill.get(int1).b = int3;
            this.m_textureStyle.m_fill.get(int1).a = int4;
        }

        public void setFillZoom(int int0, float float0) {
            this.m_textureStyle.m_fill.get(int0).m_zoom = PZMath.clamp(float0, 0.0F, 24.0F);
        }

        public float getFillZoom(int int0) {
            return this.m_textureStyle.m_fill.get(int0).m_zoom;
        }

        public int getFillRed(int int0) {
            return this.m_textureStyle.m_fill.get(int0).r;
        }

        public int getFillGreen(int int0) {
            return this.m_textureStyle.m_fill.get(int0).g;
        }

        public int getFillBlue(int int0) {
            return this.m_textureStyle.m_fill.get(int0).b;
        }

        public int getFillAlpha(int int0) {
            return this.m_textureStyle.m_fill.get(int0).a;
        }

        public int getTextureStops() {
            return this.m_textureStyle.m_texture.size();
        }

        public void setTextureZoom(int int0, float float0) {
            this.m_textureStyle.m_texture.get(int0).m_zoom = PZMath.clamp(float0, 0.0F, 24.0F);
        }

        public float getTextureZoom(int int0) {
            return this.m_textureStyle.m_texture.get(int0).m_zoom;
        }

        public void setTexturePath(int int0, String string) {
            this.m_textureStyle.m_texture.get(int0).texturePath = string;
            this.m_textureStyle.m_texture.get(int0).texture = Texture.getTexture(string);
        }

        public String getTexturePath(int int0) {
            return this.m_textureStyle.m_texture.get(int0).texturePath;
        }

        public Texture getTexture(int int0) {
            return this.m_textureStyle.m_texture.get(int0).texture;
        }
    }
}
