// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap;

import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.characters.Faction;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.debug.DebugOptions;
import zombie.input.GameKeyboard;
import zombie.inventory.types.MapItem;
import zombie.iso.BuildingDef;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.areas.SafeHouse;
import zombie.network.GameClient;
import zombie.network.ServerOptions;
import zombie.ui.TextManager;
import zombie.ui.UIElement;
import zombie.ui.UIFont;
import zombie.util.StringUtils;
import zombie.worldMap.editor.WorldMapEditorState;
import zombie.worldMap.markers.WorldMapGridSquareMarker;
import zombie.worldMap.markers.WorldMapMarkers;
import zombie.worldMap.markers.WorldMapMarkersV1;
import zombie.worldMap.styles.WorldMapStyle;
import zombie.worldMap.styles.WorldMapStyleLayer;
import zombie.worldMap.styles.WorldMapStyleV1;
import zombie.worldMap.symbols.MapSymbolDefinitions;
import zombie.worldMap.symbols.WorldMapSymbols;
import zombie.worldMap.symbols.WorldMapSymbolsV1;

public class UIWorldMap extends UIElement {
    static final ArrayList<WorldMapFeature> s_tempFeatures = new ArrayList<>();
    protected final WorldMap m_worldMap = new WorldMap();
    protected final WorldMapStyle m_style = new WorldMapStyle();
    protected final WorldMapRenderer m_renderer = new WorldMapRenderer();
    protected final WorldMapMarkers m_markers = new WorldMapMarkers();
    protected WorldMapSymbols m_symbols = null;
    protected final WorldMapStyleLayer.RGBAf m_color = new WorldMapStyleLayer.RGBAf().init(0.85882354F, 0.84313726F, 0.7529412F, 1.0F);
    protected final UIWorldMapV1 m_APIv1 = new UIWorldMapV1(this);
    private boolean m_dataWasReady = false;
    private final ArrayList<BuildingDef> m_buildingsWithoutFeatures = new ArrayList<>();
    private boolean m_bBuildingsWithoutFeatures = false;

    public UIWorldMap(KahluaTable table) {
        super(table);
    }

    public UIWorldMapV1 getAPI() {
        return this.m_APIv1;
    }

    public UIWorldMapV1 getAPIv1() {
        return this.m_APIv1;
    }

    protected void setMapItem(MapItem mapItem) {
        this.m_symbols = mapItem.getSymbols();
    }

    @Override
    public void render() {
        if (this.isVisible()) {
            if (this.Parent == null || this.Parent.getMaxDrawHeight() == -1.0 || !(this.Parent.getMaxDrawHeight() <= this.getY())) {
                this.DrawTextureScaledColor(
                    null,
                    0.0,
                    0.0,
                    this.getWidth(),
                    this.getHeight(),
                    (double)this.m_color.r,
                    (double)this.m_color.g,
                    (double)this.m_color.b,
                    (double)this.m_color.a
                );
                if (!this.m_worldMap.hasData()) {
                }

                this.setStencilRect(0.0, 0.0, this.getWidth(), this.getHeight());
                this.m_renderer
                    .setMap(
                        this.m_worldMap,
                        this.getAbsoluteX().intValue(),
                        this.getAbsoluteY().intValue(),
                        this.getWidth().intValue(),
                        this.getHeight().intValue()
                    );
                this.m_renderer.updateView();
                float float0 = this.m_renderer.getDisplayZoomF();
                float float1 = this.m_renderer.getCenterWorldX();
                float float2 = this.m_renderer.getCenterWorldY();
                float float3 = this.m_APIv1.getWorldScale(float0);
                if (this.m_renderer.getBoolean("HideUnvisited") && WorldMapVisited.getInstance() != null) {
                    this.m_renderer.setVisited(WorldMapVisited.getInstance());
                } else {
                    this.m_renderer.setVisited(null);
                }

                this.m_renderer.render(this);
                if (this.m_renderer.getBoolean("Symbols")) {
                    this.m_symbols.render(this);
                }

                this.m_markers.render(this);
                this.renderLocalPlayers();
                this.renderRemotePlayers();
                int int0 = TextManager.instance.getFontHeight(UIFont.Small);
                if (Core.bDebug && this.m_renderer.getBoolean("DebugInfo")) {
                    this.DrawTextureScaledColor(null, 0.0, 0.0, 200.0, int0 * 4.0, 1.0, 1.0, 1.0, 1.0);
                    float float4 = this.m_APIv1.mouseToWorldX();
                    float float5 = this.m_APIv1.mouseToWorldY();
                    double double0 = 0.0;
                    double double1 = 0.0;
                    double double2 = 0.0;
                    double double3 = 1.0;
                    int int1 = 0;
                    this.DrawText("SQUARE = " + (int)float4 + "," + (int)float5, 0.0, int1, double0, double1, double2, double3);
                    int1 += int0;
                    this.DrawText("CELL = " + (int)(float4 / 300.0F) + "," + (int)(float5 / 300.0F), 0.0, int0, double0, double1, double2, double3);
                    int1 += int0;
                    this.DrawText("ZOOM = " + this.m_renderer.getDisplayZoomF(), 0.0, int1, double0, double1, double2, double3);
                    int1 += int0;
                    this.DrawText("SCALE = " + this.m_renderer.getWorldScale(this.m_renderer.getZoomF()), 0.0, int1, double0, double1, double2, double3);
                    int1 += int0;
                }

                this.clearStencilRect();
                this.repaintStencilRect(0.0, 0.0, this.width, this.height);
                if (Core.bDebug && DebugOptions.instance.UIRenderOutline.getValue()) {
                    Double double4 = -this.getXScroll();
                    Double double5 = -this.getYScroll();
                    double double6 = this.isMouseOver() ? 0.0 : 1.0;
                    this.DrawTextureScaledColor(null, double4, double5, 1.0, (double)this.height, double6, 1.0, 1.0, 0.5);
                    this.DrawTextureScaledColor(null, double4 + 1.0, double5, this.width - 2.0, 1.0, double6, 1.0, 1.0, 0.5);
                    this.DrawTextureScaledColor(null, double4 + this.width - 1.0, double5, 1.0, (double)this.height, double6, 1.0, 1.0, 0.5);
                    this.DrawTextureScaledColor(null, double4 + 1.0, double5 + this.height - 1.0, this.width - 2.0, 1.0, double6, 1.0, 1.0, 0.5);
                }

                if (Core.bDebug && this.m_renderer.getBoolean("HitTest")) {
                    float float6 = this.m_APIv1.mouseToWorldX();
                    float float7 = this.m_APIv1.mouseToWorldY();
                    s_tempFeatures.clear();

                    for (WorldMapData worldMapData : this.m_worldMap.m_data) {
                        if (worldMapData.isReady()) {
                            worldMapData.hitTest(float6, float7, s_tempFeatures);
                        }
                    }

                    if (!s_tempFeatures.isEmpty()) {
                        WorldMapFeature worldMapFeature = s_tempFeatures.get(s_tempFeatures.size() - 1);
                        int int2 = worldMapFeature.m_cell.m_x * 300;
                        int int3 = worldMapFeature.m_cell.m_y * 300;
                        int int4 = this.getAbsoluteX().intValue();
                        int int5 = this.getAbsoluteY().intValue();
                        WorldMapPoints worldMapPoints = worldMapFeature.m_geometries.get(0).m_points.get(0);

                        for (int int6 = 0; int6 < worldMapPoints.numPoints(); int6++) {
                            int int7 = worldMapPoints.getX(int6);
                            int int8 = worldMapPoints.getY(int6);
                            int int9 = worldMapPoints.getX((int6 + 1) % worldMapPoints.numPoints());
                            int int10 = worldMapPoints.getY((int6 + 1) % worldMapPoints.numPoints());
                            float float8 = this.m_APIv1.worldToUIX(int2 + int7, int3 + int8);
                            float float9 = this.m_APIv1.worldToUIY(int2 + int7, int3 + int8);
                            float float10 = this.m_APIv1.worldToUIX(int2 + int9, int3 + int10);
                            float float11 = this.m_APIv1.worldToUIY(int2 + int9, int3 + int10);
                            SpriteRenderer.instance
                                .renderline(null, int4 + (int)float8, int5 + (int)float9, int4 + (int)float10, int5 + (int)float11, 1.0F, 0.0F, 0.0F, 1.0F);
                        }
                    }
                }

                if (Core.bDebug && this.m_renderer.getBoolean("BuildingsWithoutFeatures") && !this.m_renderer.getBoolean("Isometric")) {
                    this.renderBuildingsWithoutFeatures();
                }

                super.render();
            }
        }
    }

    private void renderLocalPlayers() {
        if (this.m_renderer.getBoolean("Players")) {
            float float0 = this.m_renderer.getDisplayZoomF();
            if (!(float0 >= 20.0F)) {
                for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                    IsoPlayer player = IsoPlayer.players[int0];
                    if (player != null && !player.isDead()) {
                        float float1 = player.x;
                        float float2 = player.y;
                        if (player.getVehicle() != null) {
                            float1 = player.getVehicle().getX();
                            float2 = player.getVehicle().getY();
                        }

                        this.renderPlayer(float1, float2);
                        if (GameClient.bClient) {
                            this.renderPlayerName(float1, float2, player.getUsername());
                        }
                    }
                }
            }
        }
    }

    private void renderRemotePlayers() {
        if (GameClient.bClient) {
            if (this.m_renderer.getBoolean("Players")) {
                if (this.m_renderer.getBoolean("RemotePlayers")) {
                    ArrayList arrayList = WorldMapRemotePlayers.instance.getPlayers();

                    for (int int0 = 0; int0 < arrayList.size(); int0++) {
                        WorldMapRemotePlayer worldMapRemotePlayer = (WorldMapRemotePlayer)arrayList.get(int0);
                        if (this.shouldShowRemotePlayer(worldMapRemotePlayer)) {
                            this.renderPlayer(worldMapRemotePlayer.getX(), worldMapRemotePlayer.getY());
                            this.renderPlayerName(worldMapRemotePlayer.getX(), worldMapRemotePlayer.getY(), worldMapRemotePlayer.getUsername());
                        }
                    }
                }
            }
        }
    }

    private boolean shouldShowRemotePlayer(WorldMapRemotePlayer worldMapRemotePlayer) {
        if (!worldMapRemotePlayer.hasFullData()) {
            return false;
        } else if (worldMapRemotePlayer.isInvisible()) {
            return this.isAdminSeeRemotePlayers();
        } else if (ServerOptions.getInstance().MapRemotePlayerVisibility.getValue() == 3) {
            return true;
        } else if (this.isAdminSeeRemotePlayers()) {
            return true;
        } else if (ServerOptions.getInstance().MapRemotePlayerVisibility.getValue() == 1) {
            return false;
        } else {
            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                IsoPlayer player = IsoPlayer.players[int0];
                if (player != null) {
                    if (this.isInSameFaction(player, worldMapRemotePlayer)) {
                        return true;
                    }

                    if (this.isInSameSafehouse(player, worldMapRemotePlayer)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    private boolean isAdminSeeRemotePlayers() {
        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            IsoPlayer player = IsoPlayer.players[int0];
            if (player != null && !player.isAccessLevel("none")) {
                return true;
            }
        }

        return false;
    }

    private boolean isInSameFaction(IsoPlayer player, WorldMapRemotePlayer worldMapRemotePlayer) {
        Faction faction0 = Faction.getPlayerFaction(player);
        Faction faction1 = Faction.getPlayerFaction(worldMapRemotePlayer.getUsername());
        return faction0 != null && faction0 == faction1;
    }

    private boolean isInSameSafehouse(IsoPlayer player, WorldMapRemotePlayer worldMapRemotePlayer) {
        ArrayList arrayList = SafeHouse.getSafehouseList();

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            SafeHouse safeHouse = (SafeHouse)arrayList.get(int0);
            if (safeHouse.playerAllowed(player.getUsername()) && safeHouse.playerAllowed(worldMapRemotePlayer.getUsername())) {
                return true;
            }
        }

        return false;
    }

    private void renderPlayer(float float4, float float5) {
        float float0 = this.m_renderer.getDisplayZoomF();
        float float1 = this.m_renderer.getCenterWorldX();
        float float2 = this.m_renderer.getCenterWorldY();
        float float3 = this.m_APIv1
            .worldToUIX(float4, float5, float0, float1, float2, this.m_renderer.getProjectionMatrix(), this.m_renderer.getModelViewMatrix());
        float float6 = this.m_APIv1
            .worldToUIY(float4, float5, float0, float1, float2, this.m_renderer.getProjectionMatrix(), this.m_renderer.getModelViewMatrix());
        float3 = PZMath.floor(float3);
        float6 = PZMath.floor(float6);
        this.DrawTextureScaledColor(null, float3 - 3.0, float6 - 3.0, 6.0, 6.0, 1.0, 0.0, 0.0, 1.0);
    }

    private void renderPlayerName(float float4, float float5, String string) {
        if (this.m_renderer.getBoolean("PlayerNames")) {
            if (!StringUtils.isNullOrWhitespace(string)) {
                float float0 = this.m_renderer.getDisplayZoomF();
                float float1 = this.m_renderer.getCenterWorldX();
                float float2 = this.m_renderer.getCenterWorldY();
                float float3 = this.m_APIv1
                    .worldToUIX(float4, float5, float0, float1, float2, this.m_renderer.getProjectionMatrix(), this.m_renderer.getModelViewMatrix());
                float float6 = this.m_APIv1
                    .worldToUIY(float4, float5, float0, float1, float2, this.m_renderer.getProjectionMatrix(), this.m_renderer.getModelViewMatrix());
                float3 = PZMath.floor(float3);
                float6 = PZMath.floor(float6);
                int int0 = TextManager.instance.MeasureStringX(UIFont.Small, string) + 16;
                int int1 = TextManager.instance.font.getLineHeight();
                int int2 = (int)Math.ceil(int1 * 1.25);
                this.DrawTextureScaledColor(null, float3 - int0 / 2.0, float6 + 4.0, (double)int0, (double)int2, 0.5, 0.5, 0.5, 0.5);
                this.DrawTextCentre(string, float3, float6 + 4.0F + (int2 - int1) / 2.0, 0.0, 0.0, 0.0, 1.0);
            }
        }
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public Boolean onMouseDown(double x, double y) {
        if (GameKeyboard.isKeyDown(42)) {
            this.m_renderer.resetView();
        }

        return super.onMouseDown(x, y);
    }

    @Override
    public Boolean onMouseUp(double x, double y) {
        return super.onMouseUp(x, y);
    }

    @Override
    public void onMouseUpOutside(double x, double y) {
        super.onMouseUpOutside(x, y);
    }

    @Override
    public Boolean onMouseMove(double dx, double dy) {
        return super.onMouseMove(dx, dy);
    }

    @Override
    public Boolean onMouseWheel(double delta) {
        return super.onMouseWheel(delta);
    }

    public static void setExposed(LuaManager.Exposer exposer) {
        exposer.setExposed(MapItem.class);
        exposer.setExposed(MapSymbolDefinitions.class);
        exposer.setExposed(MapSymbolDefinitions.MapSymbolDefinition.class);
        exposer.setExposed(UIWorldMap.class);
        exposer.setExposed(UIWorldMapV1.class);
        exposer.setExposed(WorldMapGridSquareMarker.class);
        exposer.setExposed(WorldMapMarkers.class);
        exposer.setExposed(WorldMapRenderer.WorldMapBooleanOption.class);
        exposer.setExposed(WorldMapRenderer.WorldMapDoubleOption.class);
        exposer.setExposed(WorldMapVisited.class);
        WorldMapMarkersV1.setExposed(exposer);
        WorldMapStyleV1.setExposed(exposer);
        WorldMapSymbolsV1.setExposed(exposer);
        exposer.setExposed(WorldMapEditorState.class);
        exposer.setExposed(WorldMapSettings.class);
    }

    private void renderBuildingsWithoutFeatures() {
        if (this.m_bBuildingsWithoutFeatures) {
            for (BuildingDef buildingDef0 : this.m_buildingsWithoutFeatures) {
                this.debugRenderBuilding(buildingDef0, 1.0F, 0.0F, 0.0F, 1.0F);
            }
        } else {
            this.m_bBuildingsWithoutFeatures = true;
            this.m_buildingsWithoutFeatures.clear();
            IsoMetaGrid metaGrid = IsoWorld.instance.MetaGrid;

            for (int int0 = 0; int0 < metaGrid.Buildings.size(); int0++) {
                BuildingDef buildingDef1 = metaGrid.Buildings.get(int0);
                boolean boolean0 = false;

                for (int int1 = 0; int1 < buildingDef1.rooms.size(); int1++) {
                    RoomDef roomDef = buildingDef1.rooms.get(int1);
                    if (roomDef.level <= 0) {
                        ArrayList arrayList = roomDef.getRects();

                        for (int int2 = 0; int2 < arrayList.size(); int2++) {
                            RoomDef.RoomRect roomRect = (RoomDef.RoomRect)arrayList.get(int2);
                            s_tempFeatures.clear();

                            for (WorldMapData worldMapData : this.m_worldMap.m_data) {
                                if (worldMapData.isReady()) {
                                    worldMapData.hitTest(roomRect.x + roomRect.w / 2.0F, roomRect.y + roomRect.h / 2.0F, s_tempFeatures);
                                }
                            }

                            if (!s_tempFeatures.isEmpty()) {
                                boolean0 = true;
                                break;
                            }
                        }

                        if (boolean0) {
                            break;
                        }
                    }
                }

                if (!boolean0) {
                    this.m_buildingsWithoutFeatures.add(buildingDef1);
                }
            }
        }
    }

    private void debugRenderBuilding(BuildingDef buildingDef, float float7, float float6, float float5, float float4) {
        for (int int0 = 0; int0 < buildingDef.rooms.size(); int0++) {
            ArrayList arrayList = buildingDef.rooms.get(int0).getRects();

            for (int int1 = 0; int1 < arrayList.size(); int1++) {
                RoomDef.RoomRect roomRect = (RoomDef.RoomRect)arrayList.get(int1);
                float float0 = this.m_APIv1.worldToUIX(roomRect.x, roomRect.y);
                float float1 = this.m_APIv1.worldToUIY(roomRect.x, roomRect.y);
                float float2 = this.m_APIv1.worldToUIX(roomRect.getX2(), roomRect.getY2());
                float float3 = this.m_APIv1.worldToUIY(roomRect.getX2(), roomRect.getY2());
                this.DrawTextureScaledColor(
                    null,
                    (double)float0,
                    (double)float1,
                    (double)(float2 - float0),
                    (double)(float3 - float1),
                    (double)float7,
                    (double)float6,
                    (double)float5,
                    (double)float4
                );
            }
        }
    }
}
