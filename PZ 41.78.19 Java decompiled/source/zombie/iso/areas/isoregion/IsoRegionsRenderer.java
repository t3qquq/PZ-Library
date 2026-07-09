// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas.isoregion;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import zombie.MapCollisionData;
import zombie.ZomboidFileSystem;
import zombie.characters.IsoPlayer;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.utils.Bits;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.iso.LotHeader;
import zombie.iso.RoomDef;
import zombie.iso.areas.isoregion.data.DataChunk;
import zombie.iso.areas.isoregion.data.DataRoot;
import zombie.iso.areas.isoregion.regions.IsoChunkRegion;
import zombie.iso.areas.isoregion.regions.IsoWorldRegion;
import zombie.iso.objects.IsoThumpable;
import zombie.ui.TextManager;
import zombie.ui.UIElement;
import zombie.ui.UIFont;

/**
 * TurboTuTone.  Base functionality copied from ZombiePopulationRenderer
 */
public class IsoRegionsRenderer {
    private final List<DataChunk> tempChunkList = new ArrayList<>();
    private final List<String> debugLines = new ArrayList<>();
    private float xPos;
    private float yPos;
    private float offx;
    private float offy;
    private float zoom;
    private float draww;
    private float drawh;
    private boolean hasSelected = false;
    private boolean validSelection = false;
    private int selectedX;
    private int selectedY;
    private int selectedZ;
    private final HashSet<Integer> drawnCells = new HashSet<>();
    private boolean editSquareInRange = false;
    private int editSquareX;
    private int editSquareY;
    private final ArrayList<ConfigOption> editOptions = new ArrayList<>();
    private boolean EditingEnabled = false;
    private final IsoRegionsRenderer.BooleanDebugOption EditWallN = new IsoRegionsRenderer.BooleanDebugOption(this.editOptions, "Edit.WallN", false);
    private final IsoRegionsRenderer.BooleanDebugOption EditWallW = new IsoRegionsRenderer.BooleanDebugOption(this.editOptions, "Edit.WallW", false);
    private final IsoRegionsRenderer.BooleanDebugOption EditDoorN = new IsoRegionsRenderer.BooleanDebugOption(this.editOptions, "Edit.DoorN", false);
    private final IsoRegionsRenderer.BooleanDebugOption EditDoorW = new IsoRegionsRenderer.BooleanDebugOption(this.editOptions, "Edit.DoorW", false);
    private final IsoRegionsRenderer.BooleanDebugOption EditFloor = new IsoRegionsRenderer.BooleanDebugOption(this.editOptions, "Edit.Floor", false);
    private final ArrayList<ConfigOption> zLevelOptions = new ArrayList<>();
    private final IsoRegionsRenderer.BooleanDebugOption zLevelPlayer = new IsoRegionsRenderer.BooleanDebugOption(this.zLevelOptions, "zLevel.Player", true);
    private final IsoRegionsRenderer.BooleanDebugOption zLevel0 = new IsoRegionsRenderer.BooleanDebugOption(this.zLevelOptions, "zLevel.0", false, 0);
    private final IsoRegionsRenderer.BooleanDebugOption zLevel1 = new IsoRegionsRenderer.BooleanDebugOption(this.zLevelOptions, "zLevel.1", false, 1);
    private final IsoRegionsRenderer.BooleanDebugOption zLevel2 = new IsoRegionsRenderer.BooleanDebugOption(this.zLevelOptions, "zLevel.2", false, 2);
    private final IsoRegionsRenderer.BooleanDebugOption zLevel3 = new IsoRegionsRenderer.BooleanDebugOption(this.zLevelOptions, "zLevel.3", false, 3);
    private final IsoRegionsRenderer.BooleanDebugOption zLevel4 = new IsoRegionsRenderer.BooleanDebugOption(this.zLevelOptions, "zLevel.4", false, 4);
    private final IsoRegionsRenderer.BooleanDebugOption zLevel5 = new IsoRegionsRenderer.BooleanDebugOption(this.zLevelOptions, "zLevel.5", false, 5);
    private final IsoRegionsRenderer.BooleanDebugOption zLevel6 = new IsoRegionsRenderer.BooleanDebugOption(this.zLevelOptions, "zLevel.6", false, 6);
    private final IsoRegionsRenderer.BooleanDebugOption zLevel7 = new IsoRegionsRenderer.BooleanDebugOption(this.zLevelOptions, "zLevel.7", false, 7);
    private static final int VERSION = 1;
    private final ArrayList<ConfigOption> options = new ArrayList<>();
    private final IsoRegionsRenderer.BooleanDebugOption CellGrid = new IsoRegionsRenderer.BooleanDebugOption(this.options, "CellGrid", true);
    private final IsoRegionsRenderer.BooleanDebugOption MetaGridBuildings = new IsoRegionsRenderer.BooleanDebugOption(this.options, "MetaGrid.Buildings", true);
    private final IsoRegionsRenderer.BooleanDebugOption IsoRegionRender = new IsoRegionsRenderer.BooleanDebugOption(this.options, "IsoRegion.Render", true);
    private final IsoRegionsRenderer.BooleanDebugOption IsoRegionRenderChunks = new IsoRegionsRenderer.BooleanDebugOption(
        this.options, "IsoRegion.RenderChunks", false
    );
    private final IsoRegionsRenderer.BooleanDebugOption IsoRegionRenderChunksPlus = new IsoRegionsRenderer.BooleanDebugOption(
        this.options, "IsoRegion.RenderChunksPlus", false
    );

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

    public void renderStringUI(float x, float y, String str, Color c) {
        this.renderStringUI(x, y, str, c.r, c.g, c.b, c.a);
    }

    public void renderStringUI(float x, float y, String str, double r, double g, double b, double a) {
        float float0 = this.offx + x;
        float float1 = this.offy + y;
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

    public void renderZombie(float x, float y, float r, float g, float b) {
        float float0 = 1.0F / this.zoom + 0.5F;
        this.renderRect(x - float0 / 2.0F, y - float0 / 2.0F, float0, float0, r, g, b, 1.0F);
    }

    public void renderSquare(float x, float y, float r, float g, float b, float alpha) {
        float float0 = 1.0F;
        this.renderRect(x, y, float0, float0, r, g, b, alpha);
    }

    public void renderEntity(float size, float x, float y, float r, float g, float b, float a) {
        float float0 = size / this.zoom + 0.5F;
        this.renderRect(x - float0 / 2.0F, y - float0 / 2.0F, float0, float0, r, g, b, a);
    }

    public void render(UIElement ui, float _zoom, float _xPos, float _yPos) {
        synchronized (MapCollisionData.instance.renderLock) {
            this._render(ui, _zoom, _xPos, _yPos);
        }
    }

    private void debugLine(String string) {
        this.debugLines.add(string);
    }

    public void recalcSurroundings() {
        IsoRegions.forceRecalcSurroundingChunks();
    }

    public boolean hasChunkRegion(int x, int y) {
        int int0 = this.getZLevel();
        DataRoot dataRoot = IsoRegions.getDataRoot();
        return dataRoot.getIsoChunkRegion(x, y, int0) != null;
    }

    public IsoChunkRegion getChunkRegion(int x, int y) {
        int int0 = this.getZLevel();
        DataRoot dataRoot = IsoRegions.getDataRoot();
        return dataRoot.getIsoChunkRegion(x, y, int0);
    }

    public void setSelected(int x, int y) {
        this.setSelectedWorld((int)this.uiToWorldX(x), (int)this.uiToWorldY(y));
    }

    public void setSelectedWorld(int x, int y) {
        this.selectedZ = this.getZLevel();
        this.hasSelected = true;
        this.selectedX = x;
        this.selectedY = y;
    }

    public void unsetSelected() {
        this.hasSelected = false;
    }

    public boolean isHasSelected() {
        return this.hasSelected;
    }

    private void _render(UIElement uIElement, float float2, float float0, float float1) {
        this.debugLines.clear();
        this.drawnCells.clear();
        this.draww = uIElement.getWidth().intValue();
        this.drawh = uIElement.getHeight().intValue();
        this.xPos = float0;
        this.yPos = float1;
        this.offx = uIElement.getAbsoluteX().intValue();
        this.offy = uIElement.getAbsoluteY().intValue();
        this.zoom = float2;
        this.debugLine("Zoom: " + float2);
        this.debugLine("zLevel: " + this.getZLevel());
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
        float float3 = Math.max(1.0F - float2 / 2.0F, 0.1F);
        IsoChunkRegion chunkRegion0 = null;
        IsoWorldRegion worldRegion0 = null;
        this.validSelection = false;
        if (this.IsoRegionRender.getValue()) {
            IsoPlayer player0 = IsoPlayer.getInstance();
            DataRoot dataRoot0 = IsoRegions.getDataRoot();
            this.tempChunkList.clear();
            dataRoot0.getAllChunks(this.tempChunkList);
            this.debugLine("DataChunks: " + this.tempChunkList.size());
            this.debugLine("IsoChunkRegions: " + dataRoot0.regionManager.getChunkRegionCount());
            this.debugLine("IsoWorldRegions: " + dataRoot0.regionManager.getWorldRegionCount());
            if (this.hasSelected) {
                chunkRegion0 = dataRoot0.getIsoChunkRegion(this.selectedX, this.selectedY, this.selectedZ);
                worldRegion0 = dataRoot0.getIsoWorldRegion(this.selectedX, this.selectedY, this.selectedZ);
                if (worldRegion0 != null
                    && !worldRegion0.isEnclosed()
                    && (!this.IsoRegionRenderChunks.getValue() || !this.IsoRegionRenderChunksPlus.getValue())) {
                    worldRegion0 = null;
                    chunkRegion0 = null;
                }

                if (chunkRegion0 != null) {
                    this.validSelection = true;
                }
            }

            for (int int4 = 0; int4 < this.tempChunkList.size(); int4++) {
                DataChunk dataChunk0 = this.tempChunkList.get(int4);
                int int5 = dataChunk0.getChunkX() * 10;
                int int6 = dataChunk0.getChunkY() * 10;
                if (float2 > 0.1F) {
                    float float4 = this.worldToScreenX(int5);
                    float float5 = this.worldToScreenY(int6);
                    float float6 = this.worldToScreenX(int5 + 10);
                    float float7 = this.worldToScreenY(int6 + 10);
                    if (!(float4 >= this.offx + this.draww) && !(float6 < this.offx) && !(float5 >= this.offy + this.drawh) && !(float7 < this.offy)) {
                        this.renderRect(int5, int6, 10.0F, 10.0F, 0.0F, float3, 0.0F, 1.0F);
                    }
                }
            }
        }

        if (this.MetaGridBuildings.getValue()) {
            float float8 = PZMath.clamp(0.3F * (float2 / 5.0F), 0.15F, 0.3F);

            for (int int7 = int0; int7 < int2; int7++) {
                for (int int8 = int1; int8 < int3; int8++) {
                    LotHeader lotHeader = metaCells[int7][int8].info;
                    if (lotHeader != null) {
                        for (int int9 = 0; int9 < lotHeader.Buildings.size(); int9++) {
                            BuildingDef buildingDef = lotHeader.Buildings.get(int9);

                            for (int int10 = 0; int10 < buildingDef.rooms.size(); int10++) {
                                if (buildingDef.rooms.get(int10).level <= 0) {
                                    ArrayList arrayList = buildingDef.rooms.get(int10).getRects();

                                    for (int int11 = 0; int11 < arrayList.size(); int11++) {
                                        RoomDef.RoomRect roomRect = (RoomDef.RoomRect)arrayList.get(int11);
                                        if (buildingDef.bAlarmed) {
                                            this.renderRect(
                                                roomRect.getX(),
                                                roomRect.getY(),
                                                roomRect.getW(),
                                                roomRect.getH(),
                                                0.8F * float8,
                                                0.8F * float8,
                                                0.5F * float8,
                                                1.0F
                                            );
                                        } else {
                                            this.renderRect(
                                                roomRect.getX(),
                                                roomRect.getY(),
                                                roomRect.getW(),
                                                roomRect.getH(),
                                                0.5F * float8,
                                                0.5F * float8,
                                                0.8F * float8,
                                                1.0F
                                            );
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (this.IsoRegionRender.getValue()) {
            int int12 = this.getZLevel();
            DataRoot dataRoot1 = IsoRegions.getDataRoot();
            this.tempChunkList.clear();
            dataRoot1.getAllChunks(this.tempChunkList);
            float float9 = 1.0F;

            for (int int13 = 0; int13 < this.tempChunkList.size(); int13++) {
                DataChunk dataChunk1 = this.tempChunkList.get(int13);
                int int14 = dataChunk1.getChunkX() * 10;
                int int15 = dataChunk1.getChunkY() * 10;
                if (float2 <= 0.1F) {
                    int int16 = int14 / 300;
                    int int17 = int15 / 300;
                    int int18 = IsoRegions.hash(int16, int17);
                    if (!this.drawnCells.contains(int18)) {
                        this.drawnCells.add(int18);
                        this.renderRect(int16 * 300, int17 * 300, 300.0F, 300.0F, 0.0F, float3, 0.0F, 1.0F);
                    }
                } else if (!(float2 < 1.0F)) {
                    float float10 = this.worldToScreenX(int14);
                    float float11 = this.worldToScreenY(int15);
                    float float12 = this.worldToScreenX(int14 + 10);
                    float float13 = this.worldToScreenY(int15 + 10);
                    if (!(float10 >= this.offx + this.draww) && !(float12 < this.offx) && !(float11 >= this.offy + this.drawh) && !(float13 < this.offy)) {
                        for (int int19 = 0; int19 < 10; int19++) {
                            for (int int20 = 0; int20 < 10; int20++) {
                                int int21 = int12 > 0 ? int12 - 1 : int12;

                                for (int int22 = int21; int22 <= int12; int22++) {
                                    float float14 = int22 < int12 ? 0.25F : 1.0F;
                                    byte byte0 = dataChunk1.getSquare(int19, int20, int22);
                                    if (byte0 >= 0) {
                                        IsoChunkRegion chunkRegion1 = dataChunk1.getIsoChunkRegion(int19, int20, int22);
                                        if (chunkRegion1 != null) {
                                            if (float2 > 6.0F && this.IsoRegionRenderChunks.getValue() && this.IsoRegionRenderChunksPlus.getValue()) {
                                                Color color0 = chunkRegion1.getColor();
                                                float9 = 1.0F;
                                                if (chunkRegion0 != null && chunkRegion1 != chunkRegion0) {
                                                    float9 = 0.25F;
                                                }

                                                this.renderSquare(int14 + int19, int15 + int20, color0.r, color0.g, color0.b, float9 * float14);
                                            } else {
                                                IsoWorldRegion worldRegion1 = chunkRegion1.getIsoWorldRegion();
                                                if (worldRegion1 != null && worldRegion1.isEnclosed()) {
                                                    float9 = 1.0F;
                                                    Color color1;
                                                    if (this.IsoRegionRenderChunks.getValue()) {
                                                        color1 = chunkRegion1.getColor();
                                                        if (chunkRegion0 != null && chunkRegion1 != chunkRegion0) {
                                                            float9 = 0.25F;
                                                        }
                                                    } else {
                                                        color1 = worldRegion1.getColor();
                                                        if (worldRegion0 != null && worldRegion1 != worldRegion0) {
                                                            float9 = 0.25F;
                                                        }
                                                    }

                                                    this.renderSquare(int14 + int19, int15 + int20, color1.r, color1.g, color1.b, float9 * float14);
                                                }
                                            }
                                        }

                                        if (int22 > 0 && int22 == int12) {
                                            chunkRegion1 = dataChunk1.getIsoChunkRegion(int19, int20, int22);
                                            IsoWorldRegion worldRegion2 = chunkRegion1 != null ? chunkRegion1.getIsoWorldRegion() : null;
                                            boolean boolean0 = chunkRegion1 == null || worldRegion2 == null || !worldRegion2.isEnclosed();
                                            if (boolean0 && Bits.hasFlags(byte0, 16)) {
                                                this.renderSquare(int14 + int19, int15 + int20, 0.5F, 0.5F, 0.5F, 1.0F);
                                            }
                                        }

                                        if (Bits.hasFlags(byte0, 1) || Bits.hasFlags(byte0, 4)) {
                                            this.renderRect(int14 + int19, int15 + int20, 1.0F, 0.1F, 1.0F, 1.0F, 1.0F, 1.0F * float14);
                                        }

                                        if (Bits.hasFlags(byte0, 2) || Bits.hasFlags(byte0, 8)) {
                                            this.renderRect(int14 + int19, int15 + int20, 0.1F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F * float14);
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
            float float15 = 1.0F;
            if (float2 < 0.1F) {
                float15 = Math.max(float2 / 0.1F, 0.25F);
            }

            for (int int23 = int1; int23 <= int3; int23++) {
                this.renderLine(
                    metaGrid.minX * 300,
                    (metaGrid.minY + int23) * 300,
                    (metaGrid.maxX + 1) * 300,
                    (metaGrid.minY + int23) * 300,
                    1.0F,
                    1.0F,
                    1.0F,
                    0.15F * float15
                );
                if (float2 > 1.0F) {
                    for (int int24 = 1; int24 < 30; int24++) {
                        this.renderLine(
                            metaGrid.minX * 300,
                            (metaGrid.minY + int23) * 300 + int24 * 10,
                            (metaGrid.maxX + 1) * 300,
                            (metaGrid.minY + int23) * 300 + int24 * 10,
                            1.0F,
                            1.0F,
                            1.0F,
                            0.0325F
                        );
                    }
                } else if (float2 > 0.15F) {
                    this.renderLine(
                        metaGrid.minX * 300,
                        (metaGrid.minY + int23) * 300 + 100,
                        (metaGrid.maxX + 1) * 300,
                        (metaGrid.minY + int23) * 300 + 100,
                        1.0F,
                        1.0F,
                        1.0F,
                        0.075F
                    );
                    this.renderLine(
                        metaGrid.minX * 300,
                        (metaGrid.minY + int23) * 300 + 200,
                        (metaGrid.maxX + 1) * 300,
                        (metaGrid.minY + int23) * 300 + 200,
                        1.0F,
                        1.0F,
                        1.0F,
                        0.075F
                    );
                }
            }

            for (int int25 = int0; int25 <= int2; int25++) {
                this.renderLine(
                    (metaGrid.minX + int25) * 300,
                    metaGrid.minY * 300,
                    (metaGrid.minX + int25) * 300,
                    (metaGrid.maxY + 1) * 300,
                    1.0F,
                    1.0F,
                    1.0F,
                    0.15F * float15
                );
                if (float2 > 1.0F) {
                    for (int int26 = 1; int26 < 30; int26++) {
                        this.renderLine(
                            (metaGrid.minX + int25) * 300 + int26 * 10,
                            metaGrid.minY * 300,
                            (metaGrid.minX + int25) * 300 + int26 * 10,
                            (metaGrid.maxY + 1) * 300,
                            1.0F,
                            1.0F,
                            1.0F,
                            0.0325F
                        );
                    }
                } else if (float2 > 0.15F) {
                    this.renderLine(
                        (metaGrid.minX + int25) * 300 + 100,
                        metaGrid.minY * 300,
                        (metaGrid.minX + int25) * 300 + 100,
                        (metaGrid.maxY + 1) * 300,
                        1.0F,
                        1.0F,
                        1.0F,
                        0.075F
                    );
                    this.renderLine(
                        (metaGrid.minX + int25) * 300 + 200,
                        metaGrid.minY * 300,
                        (metaGrid.minX + int25) * 300 + 200,
                        (metaGrid.maxY + 1) * 300,
                        1.0F,
                        1.0F,
                        1.0F,
                        0.075F
                    );
                }
            }
        }

        for (int int27 = 0; int27 < IsoPlayer.numPlayers; int27++) {
            IsoPlayer player1 = IsoPlayer.players[int27];
            if (player1 != null) {
                this.renderZombie(player1.x, player1.y, 0.0F, 0.5F, 0.0F);
            }
        }

        if (this.isEditingEnabled()) {
            float float16 = this.editSquareInRange ? 0.0F : 1.0F;
            float float17 = this.editSquareInRange ? 1.0F : 0.0F;
            if (this.EditWallN.getValue() || this.EditDoorN.getValue()) {
                this.renderRect(this.editSquareX, this.editSquareY, 1.0F, 0.25F, float16, float17, 0.0F, 0.5F);
                this.renderRect(this.editSquareX, this.editSquareY, 1.0F, 0.05F, float16, float17, 0.0F, 1.0F);
                this.renderRect(this.editSquareX, this.editSquareY, 0.05F, 0.25F, float16, float17, 0.0F, 1.0F);
                this.renderRect(this.editSquareX, this.editSquareY + 0.2F, 1.0F, 0.05F, float16, float17, 0.0F, 1.0F);
                this.renderRect(this.editSquareX + 0.95F, this.editSquareY, 0.05F, 0.25F, float16, float17, 0.0F, 1.0F);
            } else if (!this.EditWallW.getValue() && !this.EditDoorW.getValue()) {
                this.renderRect(this.editSquareX, this.editSquareY, 1.0F, 1.0F, float16, float17, 0.0F, 0.5F);
                this.renderRect(this.editSquareX, this.editSquareY, 1.0F, 0.05F, float16, float17, 0.0F, 1.0F);
                this.renderRect(this.editSquareX, this.editSquareY, 0.05F, 1.0F, float16, float17, 0.0F, 1.0F);
                this.renderRect(this.editSquareX, this.editSquareY + 0.95F, 1.0F, 0.05F, float16, float17, 0.0F, 1.0F);
                this.renderRect(this.editSquareX + 0.95F, this.editSquareY, 0.05F, 1.0F, float16, float17, 0.0F, 1.0F);
            } else {
                this.renderRect(this.editSquareX, this.editSquareY, 0.25F, 1.0F, float16, float17, 0.0F, 0.5F);
                this.renderRect(this.editSquareX, this.editSquareY, 0.25F, 0.05F, float16, float17, 0.0F, 1.0F);
                this.renderRect(this.editSquareX, this.editSquareY, 0.05F, 1.0F, float16, float17, 0.0F, 1.0F);
                this.renderRect(this.editSquareX, this.editSquareY + 0.95F, 0.25F, 0.05F, float16, float17, 0.0F, 1.0F);
                this.renderRect(this.editSquareX + 0.2F, this.editSquareY, 0.05F, 1.0F, float16, float17, 0.0F, 1.0F);
            }
        }

        if (chunkRegion0 != null) {
            this.debugLine("- ChunkRegion -");
            this.debugLine("ID: " + chunkRegion0.getID());
            this.debugLine("Squares: " + chunkRegion0.getSquareSize());
            this.debugLine("Roofs: " + chunkRegion0.getRoofCnt());
            this.debugLine("Neighbors: " + chunkRegion0.getNeighborCount());
            this.debugLine("ConnectedNeighbors: " + chunkRegion0.getConnectedNeighbors().size());
            this.debugLine("FullyEnclosed: " + chunkRegion0.getIsEnclosed());
        }

        if (worldRegion0 != null) {
            this.debugLine("- WorldRegion -");
            this.debugLine("ID: " + worldRegion0.getID());
            this.debugLine("Squares: " + worldRegion0.getSquareSize());
            this.debugLine("Roofs: " + worldRegion0.getRoofCnt());
            this.debugLine("IsFullyRoofed: " + worldRegion0.isFullyRoofed());
            this.debugLine("RoofPercentage: " + worldRegion0.getRoofedPercentage());
            this.debugLine("IsEnclosed: " + worldRegion0.isEnclosed());
            this.debugLine("Neighbors: " + worldRegion0.getNeighbors().size());
            this.debugLine("ChunkRegionCount: " + worldRegion0.size());
        }

        byte byte1 = 15;

        for (int int28 = 0; int28 < this.debugLines.size(); int28++) {
            this.renderStringUI(10.0F, byte1, this.debugLines.get(int28), Colors.CornFlowerBlue);
            byte1 += 18;
        }
    }

    public void setEditSquareCoord(int x, int y) {
        this.editSquareX = x;
        this.editSquareY = y;
        this.editSquareInRange = false;
        if (this.editCoordInRange(x, y)) {
            this.editSquareInRange = true;
        }
    }

    private boolean editCoordInRange(int int0, int int1) {
        IsoGridSquare square = IsoWorld.instance.getCell().getGridSquare(int0, int1, 0);
        return square != null;
    }

    public void editSquare(int x, int y) {
        if (this.isEditingEnabled()) {
            int int0 = this.getZLevel();
            IsoGridSquare square = IsoWorld.instance.getCell().getGridSquare(x, y, int0);
            DataRoot dataRoot = IsoRegions.getDataRoot();
            byte byte0 = dataRoot.getSquareFlags(x, y, int0);
            if (this.editCoordInRange(x, y)) {
                if (square == null) {
                    square = IsoWorld.instance.getCell().createNewGridSquare(x, y, int0, true);
                    if (square == null) {
                        return;
                    }
                }

                this.editSquareInRange = true;

                for (int int1 = 0; int1 < this.editOptions.size(); int1++) {
                    IsoRegionsRenderer.BooleanDebugOption booleanDebugOption = (IsoRegionsRenderer.BooleanDebugOption)this.editOptions.get(int1);
                    if (booleanDebugOption.getValue()) {
                        String string = booleanDebugOption.getName();
                        switch (string) {
                            case "Edit.WallW":
                            case "Edit.WallN":
                                IsoThumpable thumpable1;
                                if (booleanDebugOption.getName().equals("Edit.WallN")) {
                                    if (byte0 > 0 && Bits.hasFlags(byte0, 1)) {
                                        return;
                                    }

                                    thumpable1 = new IsoThumpable(IsoWorld.instance.getCell(), square, "walls_exterior_wooden_01_25", true, null);
                                } else {
                                    if (byte0 > 0 && Bits.hasFlags(byte0, 2)) {
                                        return;
                                    }

                                    thumpable1 = new IsoThumpable(IsoWorld.instance.getCell(), square, "walls_exterior_wooden_01_24", true, null);
                                }

                                thumpable1.setMaxHealth(100);
                                thumpable1.setName("Wall Debug");
                                thumpable1.setBreakSound("BreakObject");
                                square.AddSpecialObject(thumpable1);
                                square.RecalcAllWithNeighbours(true);
                                thumpable1.transmitCompleteItemToServer();
                                if (square.getZone() != null) {
                                    square.getZone().setHaveConstruction(true);
                                }
                                break;
                            case "Edit.DoorW":
                            case "Edit.DoorN":
                                IsoThumpable thumpable0;
                                if (booleanDebugOption.getName().equals("Edit.DoorN")) {
                                    if (byte0 > 0 && Bits.hasFlags(byte0, 1)) {
                                        return;
                                    }

                                    thumpable0 = new IsoThumpable(IsoWorld.instance.getCell(), square, "walls_exterior_wooden_01_35", true, null);
                                } else {
                                    if (byte0 > 0 && Bits.hasFlags(byte0, 2)) {
                                        return;
                                    }

                                    thumpable0 = new IsoThumpable(IsoWorld.instance.getCell(), square, "walls_exterior_wooden_01_34", true, null);
                                }

                                thumpable0.setMaxHealth(100);
                                thumpable0.setName("Door Frame Debug");
                                thumpable0.setBreakSound("BreakObject");
                                square.AddSpecialObject(thumpable0);
                                square.RecalcAllWithNeighbours(true);
                                thumpable0.transmitCompleteItemToServer();
                                if (square.getZone() != null) {
                                    square.getZone().setHaveConstruction(true);
                                }
                                break;
                            case "Edit.Floor":
                                if (byte0 > 0 && Bits.hasFlags(byte0, 16)) {
                                    return;
                                }

                                if (int0 == 0) {
                                    return;
                                }

                                square.addFloor("carpentry_02_56");
                                if (square.getZone() != null) {
                                    square.getZone().setHaveConstruction(true);
                                }
                        }
                    }
                }
            } else {
                this.editSquareInRange = false;
            }
        }
    }

    public boolean isEditingEnabled() {
        return this.EditingEnabled;
    }

    public void editRotate() {
        if (this.EditWallN.getValue()) {
            this.EditWallN.setValue(false);
            this.EditWallW.setValue(true);
        } else if (this.EditWallW.getValue()) {
            this.EditWallW.setValue(false);
            this.EditWallN.setValue(true);
        }

        if (this.EditDoorN.getValue()) {
            this.EditDoorN.setValue(false);
            this.EditDoorW.setValue(true);
        } else if (this.EditDoorW.getValue()) {
            this.EditDoorW.setValue(false);
            this.EditDoorN.setValue(true);
        }
    }

    public ConfigOption getEditOptionByName(String name) {
        for (int int0 = 0; int0 < this.editOptions.size(); int0++) {
            ConfigOption configOption = this.editOptions.get(int0);
            if (configOption.getName().equals(name)) {
                return configOption;
            }
        }

        return null;
    }

    public int getEditOptionCount() {
        return this.editOptions.size();
    }

    public ConfigOption getEditOptionByIndex(int index) {
        return this.editOptions.get(index);
    }

    public void setEditOption(int index, boolean b) {
        for (int int0 = 0; int0 < this.editOptions.size(); int0++) {
            IsoRegionsRenderer.BooleanDebugOption booleanDebugOption = (IsoRegionsRenderer.BooleanDebugOption)this.editOptions.get(int0);
            if (int0 != index) {
                booleanDebugOption.setValue(false);
            } else {
                booleanDebugOption.setValue(b);
                this.EditingEnabled = b;
            }
        }
    }

    public int getZLevel() {
        if (this.zLevelPlayer.getValue()) {
            return (int)IsoPlayer.getInstance().getZ();
        } else {
            for (int int0 = 0; int0 < this.zLevelOptions.size(); int0++) {
                IsoRegionsRenderer.BooleanDebugOption booleanDebugOption = (IsoRegionsRenderer.BooleanDebugOption)this.zLevelOptions.get(int0);
                if (booleanDebugOption.getValue()) {
                    return booleanDebugOption.zLevel;
                }
            }

            return 0;
        }
    }

    public ConfigOption getZLevelOptionByName(String name) {
        for (int int0 = 0; int0 < this.zLevelOptions.size(); int0++) {
            ConfigOption configOption = this.zLevelOptions.get(int0);
            if (configOption.getName().equals(name)) {
                return configOption;
            }
        }

        return null;
    }

    public int getZLevelOptionCount() {
        return this.zLevelOptions.size();
    }

    public ConfigOption getZLevelOptionByIndex(int index) {
        return this.zLevelOptions.get(index);
    }

    public void setZLevelOption(int index, boolean b) {
        for (int int0 = 0; int0 < this.zLevelOptions.size(); int0++) {
            IsoRegionsRenderer.BooleanDebugOption booleanDebugOption = (IsoRegionsRenderer.BooleanDebugOption)this.zLevelOptions.get(int0);
            if (int0 != index) {
                booleanDebugOption.setValue(false);
            } else {
                booleanDebugOption.setValue(b);
            }
        }

        if (!b) {
            this.zLevelPlayer.setValue(true);
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
        String string = ZomboidFileSystem.instance.getCacheDir() + File.separator + "isoregions-options.ini";
        ConfigFile configFile = new ConfigFile();
        configFile.write(string, 1, this.options);
    }

    public void load() {
        String string = ZomboidFileSystem.instance.getCacheDir() + File.separator + "isoregions-options.ini";
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
    }

    public static class BooleanDebugOption extends BooleanConfigOption {
        private int index;
        private int zLevel = 0;

        public BooleanDebugOption(ArrayList<ConfigOption> optionList, String name, boolean defaultValue, int _zLevel) {
            super(name, defaultValue);
            this.index = optionList.size();
            this.zLevel = _zLevel;
            optionList.add(this);
        }

        public BooleanDebugOption(ArrayList<ConfigOption> optionList, String name, boolean defaultValue) {
            super(name, defaultValue);
            this.index = optionList.size();
            optionList.add(this);
        }

        public int getIndex() {
            return this.index;
        }
    }
}
