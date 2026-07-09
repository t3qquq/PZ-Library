// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas.isoregion.regions;

import java.util.ArrayDeque;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.areas.isoregion.data.DataRoot;

/**
 * TurboTuTone.
 */
public final class IsoRegionManager {
    private final ArrayDeque<IsoWorldRegion> poolIsoWorldRegion = new ArrayDeque<>();
    private final ArrayDeque<IsoChunkRegion> poolIsoChunkRegion = new ArrayDeque<>();
    private final DataRoot dataRoot;
    private final ArrayDeque<Integer> regionIdStack = new ArrayDeque<>();
    private int nextID = 0;
    private int colorIndex = 0;
    private int worldRegionCount = 0;
    private int chunkRegionCount = 0;

    public IsoRegionManager(DataRoot _dataRoot) {
        this.dataRoot = _dataRoot;
    }

    public IsoWorldRegion allocIsoWorldRegion() {
        IsoWorldRegion worldRegion = !this.poolIsoWorldRegion.isEmpty() ? this.poolIsoWorldRegion.pop() : new IsoWorldRegion(this);
        int int0 = this.regionIdStack.isEmpty() ? this.nextID++ : this.regionIdStack.pop();
        worldRegion.init(int0);
        this.worldRegionCount++;
        return worldRegion;
    }

    public void releaseIsoWorldRegion(IsoWorldRegion worldRegion) {
        this.dataRoot.DequeueDirtyIsoWorldRegion(worldRegion);
        if (!worldRegion.isInPool()) {
            this.regionIdStack.push(worldRegion.getID());
            worldRegion.reset();
            this.poolIsoWorldRegion.push(worldRegion);
            this.worldRegionCount--;
        } else {
            IsoRegions.warn("IsoRegionManager -> Trying to release a MasterRegion twice.");
        }
    }

    public IsoChunkRegion allocIsoChunkRegion(int zLayer) {
        IsoChunkRegion chunkRegion = !this.poolIsoChunkRegion.isEmpty() ? this.poolIsoChunkRegion.pop() : new IsoChunkRegion(this);
        int int0 = this.regionIdStack.isEmpty() ? this.nextID++ : this.regionIdStack.pop();
        chunkRegion.init(int0, zLayer);
        this.chunkRegionCount++;
        return chunkRegion;
    }

    public void releaseIsoChunkRegion(IsoChunkRegion chunkRegion) {
        if (!chunkRegion.isInPool()) {
            this.regionIdStack.push(chunkRegion.getID());
            chunkRegion.reset();
            this.poolIsoChunkRegion.push(chunkRegion);
            this.chunkRegionCount--;
        } else {
            IsoRegions.warn("IsoRegionManager -> Trying to release a ChunkRegion twice.");
        }
    }

    public Color getColor() {
        Color color = Colors.GetColorFromIndex(this.colorIndex++);
        if (this.colorIndex >= Colors.GetColorsCount()) {
            this.colorIndex = 0;
        }

        return color;
    }

    public int getWorldRegionCount() {
        return this.worldRegionCount;
    }

    public int getChunkRegionCount() {
        return this.chunkRegionCount;
    }
}
