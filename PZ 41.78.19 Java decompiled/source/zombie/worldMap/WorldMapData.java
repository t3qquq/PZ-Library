// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;
import zombie.core.math.PZMath;

public final class WorldMapData extends Asset {
    public static final HashMap<String, WorldMapData> s_fileNameToData = new HashMap<>();
    public String m_relativeFileName;
    public final ArrayList<WorldMapCell> m_cells = new ArrayList<>();
    public final HashMap<Integer, WorldMapCell> m_cellLookup = new HashMap<>();
    public int m_minX;
    public int m_minY;
    public int m_maxX;
    public int m_maxY;
    public static final AssetType ASSET_TYPE = new AssetType("WorldMapData");

    public static WorldMapData getOrCreateData(String fileName) {
        WorldMapData worldMapData = s_fileNameToData.get(fileName);
        if (worldMapData == null && Files.exists(Paths.get(fileName))) {
            worldMapData = (WorldMapData)WorldMapDataAssetManager.instance.load(new AssetPath(fileName));
            s_fileNameToData.put(fileName, worldMapData);
        }

        return worldMapData;
    }

    public WorldMapData(AssetPath path, AssetManager manager) {
        super(path, manager);
    }

    public WorldMapData(AssetPath path, AssetManager manager, AssetManager.AssetParams params) {
        super(path, manager);
    }

    public void clear() {
        for (WorldMapCell worldMapCell : this.m_cells) {
            worldMapCell.dispose();
        }

        this.m_cells.clear();
        this.m_cellLookup.clear();
        this.m_minX = 0;
        this.m_minY = 0;
        this.m_maxX = 0;
        this.m_maxY = 0;
    }

    public int getWidthInCells() {
        return this.m_maxX - this.m_minX + 1;
    }

    public int getHeightInCells() {
        return this.m_maxY - this.m_minY + 1;
    }

    public int getWidthInSquares() {
        return this.getWidthInCells() * 300;
    }

    public int getHeightInSquares() {
        return this.getHeightInCells() * 300;
    }

    public void onLoaded() {
        this.m_minX = Integer.MAX_VALUE;
        this.m_minY = Integer.MAX_VALUE;
        this.m_maxX = Integer.MIN_VALUE;
        this.m_maxY = Integer.MIN_VALUE;
        this.m_cellLookup.clear();

        for (WorldMapCell worldMapCell : this.m_cells) {
            Integer integer = this.getCellKey(worldMapCell.m_x, worldMapCell.m_y);
            this.m_cellLookup.put(integer, worldMapCell);
            this.m_minX = Math.min(this.m_minX, worldMapCell.m_x);
            this.m_minY = Math.min(this.m_minY, worldMapCell.m_y);
            this.m_maxX = Math.max(this.m_maxX, worldMapCell.m_x);
            this.m_maxY = Math.max(this.m_maxY, worldMapCell.m_y);
        }
    }

    public WorldMapCell getCell(int x, int y) {
        Integer integer = this.getCellKey(x, y);
        return this.m_cellLookup.get(integer);
    }

    private Integer getCellKey(int int0, int int1) {
        return int0 + int1 * 1000;
    }

    public void hitTest(float x, float y, ArrayList<WorldMapFeature> features) {
        int int0 = (int)PZMath.floor(x / 300.0F);
        int int1 = (int)PZMath.floor(y / 300.0F);
        if (int0 >= this.m_minX && int0 <= this.m_maxX && int1 >= this.m_minY && int1 <= this.m_maxY) {
            WorldMapCell worldMapCell = this.getCell(int0, int1);
            if (worldMapCell != null) {
                worldMapCell.hitTest(x, y, features);
            }
        }
    }

    public static void Reset() {
    }

    @Override
    public AssetType getType() {
        return ASSET_TYPE;
    }

    @Override
    protected void onBeforeEmpty() {
        super.onBeforeEmpty();
        this.clear();
    }
}
