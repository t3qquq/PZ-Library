// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.popman;

import java.io.File;
import java.util.ArrayList;
import zombie.MapCollisionData;
import zombie.ZomboidFileSystem;
import zombie.ai.states.WalkTowardState;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.iso.LotHeader;
import zombie.iso.RoomDef;
import zombie.network.GameClient;
import zombie.ui.TextManager;
import zombie.ui.UIElement;
import zombie.ui.UIFont;
import zombie.vehicles.VehiclesDB2;

public final class ZombiePopulationRenderer {
    private float xPos;
    private float yPos;
    private float offx;
    private float offy;
    private float zoom;
    private float draww;
    private float drawh;
    private static final int VERSION = 1;
    private final ArrayList<ConfigOption> options = new ArrayList<>();
    private ZombiePopulationRenderer.BooleanDebugOption CellGrid = new ZombiePopulationRenderer.BooleanDebugOption("CellGrid", true);
    private ZombiePopulationRenderer.BooleanDebugOption MetaGridBuildings = new ZombiePopulationRenderer.BooleanDebugOption("MetaGrid.Buildings", true);
    private ZombiePopulationRenderer.BooleanDebugOption ZombiesStanding = new ZombiePopulationRenderer.BooleanDebugOption("Zombies.Standing", true);
    private ZombiePopulationRenderer.BooleanDebugOption ZombiesMoving = new ZombiePopulationRenderer.BooleanDebugOption("Zombies.Moving", true);
    private ZombiePopulationRenderer.BooleanDebugOption MCDObstacles = new ZombiePopulationRenderer.BooleanDebugOption("MapCollisionData.Obstacles", true);
    private ZombiePopulationRenderer.BooleanDebugOption MCDRegularChunkOutlines = new ZombiePopulationRenderer.BooleanDebugOption(
        "MapCollisionData.RegularChunkOutlines", true
    );
    private ZombiePopulationRenderer.BooleanDebugOption MCDRooms = new ZombiePopulationRenderer.BooleanDebugOption("MapCollisionData.Rooms", true);
    private ZombiePopulationRenderer.BooleanDebugOption Vehicles = new ZombiePopulationRenderer.BooleanDebugOption("Vehicles", true);

    private native void n_render(float var1, int var2, int var3, float var4, float var5, int var6, int var7);

    private native void n_setWallFollowerStart(int var1, int var2);

    private native void n_setWallFollowerEnd(int var1, int var2);

    private native void n_wallFollowerMouseMove(int var1, int var2);

    private native void n_setDebugOption(String var1, String var2);

    public float worldToScreenX(float x) {
        x -= this.xPos;
        x *= this.zoom;
        x += this.offx;
        return x + this.draww / 2.0F;
    }

    public float worldToScreenY(float y) {
        y -= this.yPos;
        y *= this.zoom;
        y += this.offy;
        return y + this.drawh / 2.0F;
    }

    public float uiToWorldX(float x) {
        x -= this.draww / 2.0F;
        x /= this.zoom;
        return x + this.xPos;
    }

    public float uiToWorldY(float y) {
        y -= this.drawh / 2.0F;
        y /= this.zoom;
        return y + this.yPos;
    }

    public void renderString(float x, float y, String str, double r, double g, double b, double a) {
        float float0 = this.worldToScreenX(x);
        float float1 = this.worldToScreenY(y);
        SpriteRenderer.instance
            .render(
                null,
                float0 - 2.0F,
                float1 - 2.0F,
                TextManager.instance.MeasureStringX(UIFont.Small, str) + 4,
                TextManager.instance.font.getLineHeight() + 4,
                0.0F,
                0.0F,
                0.0F,
                0.75F,
                null
            );
        TextManager.instance.DrawString(float0, float1, str, r, g, b, a);
    }

    public void renderRect(float x, float y, float w, float h, float r, float g, float b, float a) {
        float float0 = this.worldToScreenX(x);
        float float1 = this.worldToScreenY(y);
        float float2 = this.worldToScreenX(x + w);
        float float3 = this.worldToScreenY(y + h);
        w = float2 - float0;
        h = float3 - float1;
        if (!(float0 >= this.offx + this.draww) && !(float2 < this.offx) && !(float1 >= this.offy + this.drawh) && !(float3 < this.offy)) {
            SpriteRenderer.instance.render(null, float0, float1, w, h, r, g, b, a, null);
        }
    }

    public void renderLine(float x1, float y1, float x2, float y2, float r, float g, float b, float a) {
        float float0 = this.worldToScreenX(x1);
        float float1 = this.worldToScreenY(y1);
        float float2 = this.worldToScreenX(x2);
        float float3 = this.worldToScreenY(y2);
        if ((!(float0 >= Core.getInstance().getScreenWidth()) || !(float2 >= Core.getInstance().getScreenWidth()))
            && (!(float1 >= Core.getInstance().getScreenHeight()) || !(float3 >= Core.getInstance().getScreenHeight()))
            && (!(float0 < 0.0F) || !(float2 < 0.0F))
            && (!(float1 < 0.0F) || !(float3 < 0.0F))) {
            SpriteRenderer.instance.renderline(null, (int)float0, (int)float1, (int)float2, (int)float3, r, g, b, a);
        }
    }

    public void renderCircle(float x, float y, float radius, float r, float g, float b, float a) {
        byte byte0 = 32;
        double double0 = x + radius * Math.cos(Math.toRadians(0.0F / byte0));
        double double1 = y + radius * Math.sin(Math.toRadians(0.0F / byte0));

        for (int int0 = 1; int0 <= byte0; int0++) {
            double double2 = x + radius * Math.cos(Math.toRadians(int0 * 360.0F / byte0));
            double double3 = y + radius * Math.sin(Math.toRadians(int0 * 360.0F / byte0));
            int int1 = (int)this.worldToScreenX((float)double0);
            int int2 = (int)this.worldToScreenY((float)double1);
            int int3 = (int)this.worldToScreenX((float)double2);
            int int4 = (int)this.worldToScreenY((float)double3);
            SpriteRenderer.instance.renderline(null, int1, int2, int3, int4, r, g, b, a);
            double0 = double2;
            double1 = double3;
        }
    }

    public void renderZombie(float x, float y, float r, float g, float b) {
        float float0 = 1.0F / this.zoom + 0.5F;
        this.renderRect(x - float0 / 2.0F, y - float0 / 2.0F, float0, float0, r, g, b, 1.0F);
    }

    public void renderVehicle(int sqlid, float x, float y, float r, float g, float b) {
        float float0 = 2.0F / this.zoom + 0.5F;
        this.renderRect(x - float0 / 2.0F, y - float0 / 2.0F, float0, float0, r, g, b, 1.0F);
        this.renderString(x, y, String.format("%d", sqlid), r, g, b, 1.0);
    }

    public void outlineRect(float x, float y, float w, float h, float r, float g, float b, float a) {
        this.renderLine(x, y, x + w, y, r, g, b, a);
        this.renderLine(x + w, y, x + w, y + h, r, g, b, a);
        this.renderLine(x, y + h, x + w, y + h, r, g, b, a);
        this.renderLine(x, y, x, y + h, r, g, b, a);
    }

    public void renderCellInfo(int cellX, int cellY, int effectivePopulation, int targetPopulation, float lastRepopTime) {
        float float0 = this.worldToScreenX(cellX * 300) + 4.0F;
        float float1 = this.worldToScreenY(cellY * 300) + 4.0F;
        String string = effectivePopulation + " / " + targetPopulation;
        if (lastRepopTime > 0.0F) {
            string = string + String.format(" %.2f", lastRepopTime);
        }

        SpriteRenderer.instance
            .render(
                null,
                float0 - 2.0F,
                float1 - 2.0F,
                TextManager.instance.MeasureStringX(UIFont.Small, string) + 4,
                TextManager.instance.font.getLineHeight() + 4,
                0.0F,
                0.0F,
                0.0F,
                0.75F,
                null
            );
        TextManager.instance.DrawString(float0, float1, string, 1.0, 1.0, 1.0, 1.0);
    }

    public void render(UIElement ui, float _zoom, float _xPos, float _yPos) {
        synchronized (MapCollisionData.instance.renderLock) {
            this._render(ui, _zoom, _xPos, _yPos);
        }
    }

    private void _render(UIElement uIElement, float float2, float float0, float float1) {
        this.draww = uIElement.getWidth().intValue();
        this.drawh = uIElement.getHeight().intValue();
        this.xPos = float0;
        this.yPos = float1;
        this.offx = uIElement.getAbsoluteX().intValue();
        this.offy = uIElement.getAbsoluteY().intValue();
        this.zoom = float2;
        IsoCell cell = IsoWorld.instance.CurrentCell;
        IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[0];
        IsoMetaGrid metaGrid = IsoWorld.instance.MetaGrid;
        IsoMetaCell[][] metaCells = metaGrid.Grid;
        int int0 = (int)(this.uiToWorldX(0.0F) / 300.0F) - metaGrid.minX;
        int int1 = (int)(this.uiToWorldY(0.0F) / 300.0F) - metaGrid.minY;
        int int2 = (int)(this.uiToWorldX(this.draww) / 300.0F) + 1 - metaGrid.minX;
        int int3 = (int)(this.uiToWorldY(this.drawh) / 300.0F) + 1 - metaGrid.minY;
        int0 = PZMath.clamp(int0, 0, metaGrid.getWidth() - 1);
        int1 = PZMath.clamp(int1, 0, metaGrid.getHeight() - 1);
        int2 = PZMath.clamp(int2, 0, metaGrid.getWidth() - 1);
        int3 = PZMath.clamp(int3, 0, metaGrid.getHeight() - 1);
        if (this.MetaGridBuildings.getValue()) {
            for (int int4 = int0; int4 <= int2; int4++) {
                for (int int5 = int1; int5 <= int3; int5++) {
                    LotHeader lotHeader = metaCells[int4][int5].info;
                    if (lotHeader != null) {
                        for (int int6 = 0; int6 < lotHeader.Buildings.size(); int6++) {
                            BuildingDef buildingDef = lotHeader.Buildings.get(int6);

                            for (int int7 = 0; int7 < buildingDef.rooms.size(); int7++) {
                                if (buildingDef.rooms.get(int7).level <= 0) {
                                    ArrayList arrayList = buildingDef.rooms.get(int7).getRects();

                                    for (int int8 = 0; int8 < arrayList.size(); int8++) {
                                        RoomDef.RoomRect roomRect = (RoomDef.RoomRect)arrayList.get(int8);
                                        if (buildingDef.bAlarmed) {
                                            this.renderRect(roomRect.getX(), roomRect.getY(), roomRect.getW(), roomRect.getH(), 0.8F, 0.8F, 0.5F, 0.3F);
                                        } else {
                                            this.renderRect(roomRect.getX(), roomRect.getY(), roomRect.getW(), roomRect.getH(), 0.5F, 0.5F, 0.8F, 0.3F);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (this.CellGrid.getValue()) {
            for (int int9 = int1; int9 <= int3; int9++) {
                this.renderLine(
                    metaGrid.minX * 300, (metaGrid.minY + int9) * 300, (metaGrid.maxX + 1) * 300, (metaGrid.minY + int9) * 300, 1.0F, 1.0F, 1.0F, 0.15F
                );
            }

            for (int int10 = int0; int10 <= int2; int10++) {
                this.renderLine(
                    (metaGrid.minX + int10) * 300, metaGrid.minY * 300, (metaGrid.minX + int10) * 300, (metaGrid.maxY + 1) * 300, 1.0F, 1.0F, 1.0F, 0.15F
                );
            }
        }

        for (int int11 = 0; int11 < IsoWorld.instance.CurrentCell.getZombieList().size(); int11++) {
            IsoZombie zombie0 = IsoWorld.instance.CurrentCell.getZombieList().get(int11);
            float float3 = 1.0F;
            float float4 = 1.0F;
            float float5 = 0.0F;
            if (zombie0.isReanimatedPlayer()) {
                float3 = 0.0F;
            }

            this.renderZombie(zombie0.x, zombie0.y, float3, float4, float5);
            if (zombie0.getCurrentState() == WalkTowardState.instance()) {
                this.renderLine(zombie0.x, zombie0.y, zombie0.getPathTargetX(), zombie0.getPathTargetY(), 1.0F, 1.0F, 1.0F, 0.5F);
            }
        }

        for (int int12 = 0; int12 < IsoPlayer.numPlayers; int12++) {
            IsoPlayer player = IsoPlayer.players[int12];
            if (player != null) {
                this.renderZombie(player.x, player.y, 0.0F, 0.5F, 0.0F);
            }
        }

        if (GameClient.bClient) {
            MPDebugInfo.instance.render(this, float2);
        } else {
            if (this.Vehicles.getValue()) {
                VehiclesDB2.instance.renderDebug(this);
            }

            this.n_render(float2, (int)this.offx, (int)this.offy, float0, float1, (int)this.draww, (int)this.drawh);
        }
    }

    public void setWallFollowerStart(int x, int y) {
        if (!GameClient.bClient) {
            this.n_setWallFollowerStart(x, y);
        }
    }

    public void setWallFollowerEnd(int x, int y) {
        if (!GameClient.bClient) {
            this.n_setWallFollowerEnd(x, y);
        }
    }

    public void wallFollowerMouseMove(int x, int y) {
        if (!GameClient.bClient) {
            this.n_wallFollowerMouseMove(x, y);
        }
    }

    public ConfigOption getOptionByName(String name) {
        for (int int0 = 0; int0 < this.options.size(); int0++) {
            ConfigOption configOption = this.options.get(int0);
            if (configOption.getName().equals(name)) {
                return configOption;
            }
        }

        return null;
    }

    public int getOptionCount() {
        return this.options.size();
    }

    public ConfigOption getOptionByIndex(int index) {
        return this.options.get(index);
    }

    public void setBoolean(String name, boolean value) {
        ConfigOption configOption = this.getOptionByName(name);
        if (configOption instanceof BooleanConfigOption) {
            ((BooleanConfigOption)configOption).setValue(value);
        }
    }

    public boolean getBoolean(String name) {
        ConfigOption configOption = this.getOptionByName(name);
        return configOption instanceof BooleanConfigOption ? ((BooleanConfigOption)configOption).getValue() : false;
    }

    public void save() {
        String string = ZomboidFileSystem.instance.getCacheDir() + File.separator + "popman-options.ini";
        ConfigFile configFile = new ConfigFile();
        configFile.write(string, 1, this.options);

        for (int int0 = 0; int0 < this.options.size(); int0++) {
            ConfigOption configOption = this.options.get(int0);
            this.n_setDebugOption(configOption.getName(), configOption.getValueAsString());
        }
    }

    public void load() {
        String string = ZomboidFileSystem.instance.getCacheDir() + File.separator + "popman-options.ini";
        ConfigFile configFile = new ConfigFile();
        if (configFile.read(string)) {
            for (int int0 = 0; int0 < configFile.getOptions().size(); int0++) {
                ConfigOption configOption0 = configFile.getOptions().get(int0);
                ConfigOption configOption1 = this.getOptionByName(configOption0.getName());
                if (configOption1 != null) {
                    configOption1.parse(configOption0.getValueAsString());
                }
            }
        }

        for (int int1 = 0; int1 < this.options.size(); int1++) {
            ConfigOption configOption2 = this.options.get(int1);
            this.n_setDebugOption(configOption2.getName(), configOption2.getValueAsString());
        }
    }

    public class BooleanDebugOption extends BooleanConfigOption {
        public BooleanDebugOption(String string, boolean boolean0) {
            super(string, boolean0);
            ZombiePopulationRenderer.this.options.add(this);
        }
    }
}
