// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap;

import java.util.ArrayList;
import zombie.ZomboidFileSystem;
import zombie.asset.Asset;
import zombie.asset.AssetStateObserver;
import zombie.inventory.types.MapItem;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.util.StringUtils;
import zombie.worldMap.symbols.MapSymbolDefinitions;

public final class WorldMap implements AssetStateObserver {
    public final ArrayList<WorldMapData> m_data = new ArrayList<>();
    public final ArrayList<WorldMapImages> m_images = new ArrayList<>();
    public int m_minDataX;
    public int m_minDataY;
    public int m_maxDataX;
    public int m_maxDataY;
    public int m_minX;
    public int m_minY;
    public int m_maxX;
    public int m_maxY;
    private boolean m_boundsFromData = false;
    public final ArrayList<WorldMapData> m_lastDataInDirectory = new ArrayList<>();

    public void setBoundsInCells(int minX, int minY, int maxX, int maxY) {
        this.setBoundsInSquares(minX * 300, minY * 300, maxX * 300 + 299, maxY * 300 + 299);
    }

    public void setBoundsInSquares(int minX, int minY, int maxX, int maxY) {
        this.m_minX = minX;
        this.m_minY = minY;
        this.m_maxX = maxX;
        this.m_maxY = maxY;
    }

    public void setBoundsFromData() {
        this.m_boundsFromData = true;
        this.setBoundsInCells(this.m_minDataX, this.m_minDataY, this.m_maxDataX, this.m_maxDataY);
    }

    public void setBoundsFromWorld() {
        IsoMetaGrid metaGrid = IsoWorld.instance.getMetaGrid();
        this.setBoundsInCells(metaGrid.getMinX(), metaGrid.getMinY(), metaGrid.getMaxX(), metaGrid.getMaxY());
    }

    public void addData(String fileName) {
        if (!StringUtils.isNullOrWhitespace(fileName)) {
            String string = ZomboidFileSystem.instance.getString(fileName);
            WorldMapData worldMapData = WorldMapData.getOrCreateData(string);
            if (worldMapData != null && !this.m_data.contains(worldMapData)) {
                worldMapData.m_relativeFileName = fileName;
                this.m_data.add(worldMapData);
                worldMapData.getObserverCb().add(this);
                if (worldMapData.isReady()) {
                    this.updateDataBounds();
                }
            }
        }
    }

    public int getDataCount() {
        return this.m_data.size();
    }

    public WorldMapData getDataByIndex(int index) {
        return this.m_data.get(index);
    }

    public void clearData() {
        for (WorldMapData worldMapData : this.m_data) {
            worldMapData.getObserverCb().remove(this);
        }

        this.m_data.clear();
        this.m_lastDataInDirectory.clear();
        this.updateDataBounds();
    }

    public void endDirectoryData() {
        if (this.hasData()) {
            WorldMapData worldMapData = this.getDataByIndex(this.getDataCount() - 1);
            if (!this.m_lastDataInDirectory.contains(worldMapData)) {
                this.m_lastDataInDirectory.add(worldMapData);
            }
        }
    }

    public boolean isLastDataInDirectory(WorldMapData data) {
        return this.m_lastDataInDirectory.contains(data);
    }

    private void updateDataBounds() {
        this.m_minDataX = Integer.MAX_VALUE;
        this.m_minDataY = Integer.MAX_VALUE;
        this.m_maxDataX = Integer.MIN_VALUE;
        this.m_maxDataY = Integer.MIN_VALUE;

        for (int int0 = 0; int0 < this.m_data.size(); int0++) {
            WorldMapData worldMapData = this.m_data.get(int0);
            if (worldMapData.isReady()) {
                this.m_minDataX = Math.min(this.m_minDataX, worldMapData.m_minX);
                this.m_minDataY = Math.min(this.m_minDataY, worldMapData.m_minY);
                this.m_maxDataX = Math.max(this.m_maxDataX, worldMapData.m_maxX);
                this.m_maxDataY = Math.max(this.m_maxDataY, worldMapData.m_maxY);
            }
        }

        if (this.m_minDataX > this.m_maxDataX) {
            this.m_minDataX = this.m_maxDataX = this.m_minDataY = this.m_maxDataY = 0;
        }
    }

    public boolean hasData() {
        return !this.m_data.isEmpty();
    }

    public void addImages(String directory) {
        if (!StringUtils.isNullOrWhitespace(directory)) {
            WorldMapImages worldMapImages = WorldMapImages.getOrCreate(directory);
            if (worldMapImages != null && !this.m_images.contains(worldMapImages)) {
                this.m_images.add(worldMapImages);
            }
        }
    }

    public boolean hasImages() {
        return !this.m_images.isEmpty();
    }

    public int getImagesCount() {
        return this.m_images.size();
    }

    public WorldMapImages getImagesByIndex(int index) {
        return this.m_images.get(index);
    }

    public int getMinXInCells() {
        return this.m_minX / 300;
    }

    public int getMinYInCells() {
        return this.m_minY / 300;
    }

    public int getMaxXInCells() {
        return this.m_maxX / 300;
    }

    public int getMaxYInCells() {
        return this.m_maxY / 300;
    }

    public int getWidthInCells() {
        return this.getMaxXInCells() - this.getMinXInCells() + 1;
    }

    public int getHeightInCells() {
        return this.getMaxYInCells() - this.getMinYInCells() + 1;
    }

    public int getMinXInSquares() {
        return this.m_minX;
    }

    public int getMinYInSquares() {
        return this.m_minY;
    }

    public int getMaxXInSquares() {
        return this.m_maxX;
    }

    public int getMaxYInSquares() {
        return this.m_maxY;
    }

    public int getWidthInSquares() {
        return this.m_maxX - this.m_minX + 1;
    }

    public int getHeightInSquares() {
        return this.m_maxY - this.m_minY + 1;
    }

    public WorldMapCell getCell(int x, int y) {
        for (int int0 = 0; int0 < this.m_data.size(); int0++) {
            WorldMapData worldMapData = this.m_data.get(int0);
            if (worldMapData.isReady()) {
                WorldMapCell worldMapCell = worldMapData.getCell(x, y);
                if (worldMapCell != null) {
                    return worldMapCell;
                }
            }
        }

        return null;
    }

    public int getDataWidthInCells() {
        return this.m_maxDataX - this.m_minDataX + 1;
    }

    public int getDataHeightInCells() {
        return this.m_maxDataY - this.m_minDataY + 1;
    }

    public int getDataWidthInSquares() {
        return this.getDataWidthInCells() * 300;
    }

    public int getDataHeightInSquares() {
        return this.getDataHeightInCells() * 300;
    }

    public static void Reset() {
        WorldMapSettings.Reset();
        WorldMapVisited.Reset();
        WorldMapData.Reset();
        WorldMapImages.Reset();
        MapSymbolDefinitions.Reset();
        MapItem.Reset();
    }

    @Override
    public void onStateChanged(Asset.State oldState, Asset.State newState, Asset asset) {
        this.updateDataBounds();
        if (this.m_boundsFromData) {
            this.setBoundsInCells(this.m_minDataX, this.m_minDataY, this.m_maxDataX, this.m_maxDataY);
        }
    }
}
