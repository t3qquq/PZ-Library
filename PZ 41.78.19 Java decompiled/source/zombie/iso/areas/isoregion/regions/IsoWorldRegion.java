// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas.isoregion.regions;

import java.util.ArrayList;
import zombie.core.Color;
import zombie.core.Core;
import zombie.iso.areas.isoregion.IsoRegions;

/**
 * TurboTuTone.
 */
public final class IsoWorldRegion implements IWorldRegion {
    private final IsoRegionManager manager;
    private boolean isInPool = false;
    private int ID;
    private Color color;
    private boolean enclosed = true;
    private ArrayList<IsoChunkRegion> isoChunkRegions = new ArrayList<>();
    private int squareSize = 0;
    private int roofCnt = 0;
    private boolean isDirtyEnclosed = false;
    private boolean isDirtyRoofed = false;
    private ArrayList<IsoWorldRegion> neighbors = new ArrayList<>();

    public int getID() {
        return this.ID;
    }

    public Color getColor() {
        return this.color;
    }

    public int size() {
        return this.isoChunkRegions.size();
    }

    @Override
    public int getSquareSize() {
        return this.squareSize;
    }

    protected boolean isInPool() {
        return this.isInPool;
    }

    protected IsoWorldRegion(IsoRegionManager regionManager) {
        this.manager = regionManager;
    }

    protected void init(int int0) {
        this.isInPool = false;
        this.ID = int0;
        if (this.color == null) {
            this.color = this.manager.getColor();
        }

        this.squareSize = 0;
        this.roofCnt = 0;
        this.enclosed = true;
        this.isDirtyEnclosed = false;
        this.isDirtyRoofed = false;
    }

    protected IsoWorldRegion reset() {
        this.isInPool = true;
        this.ID = -1;
        this.squareSize = 0;
        this.roofCnt = 0;
        this.enclosed = true;
        this.isDirtyRoofed = false;
        this.isDirtyEnclosed = false;
        this.unlinkNeighbors();
        if (this.isoChunkRegions.size() > 0) {
            if (Core.bDebug) {
                throw new RuntimeException("MasterRegion.reset Resetting master region which still has chunk regions");
            }

            IsoRegions.warn("MasterRegion.reset Resetting master region which still has chunk regions");

            for (int int0 = 0; int0 < this.isoChunkRegions.size(); int0++) {
                IsoChunkRegion chunkRegion = this.isoChunkRegions.get(int0);
                chunkRegion.setIsoWorldRegion(null);
            }

            this.isoChunkRegions.clear();
        }

        return this;
    }

    public void unlinkNeighbors() {
        for (int int0 = 0; int0 < this.neighbors.size(); int0++) {
            IsoWorldRegion worldRegion1 = this.neighbors.get(int0);
            worldRegion1.removeNeighbor(this);
        }

        this.neighbors.clear();
    }

    public void linkNeighbors() {
        for (int int0 = 0; int0 < this.isoChunkRegions.size(); int0++) {
            IsoChunkRegion chunkRegion0 = this.isoChunkRegions.get(int0);

            for (int int1 = 0; int1 < chunkRegion0.getAllNeighbors().size(); int1++) {
                IsoChunkRegion chunkRegion1 = chunkRegion0.getAllNeighbors().get(int1);
                if (chunkRegion1.getIsoWorldRegion() != null && chunkRegion1.getIsoWorldRegion() != this) {
                    this.addNeighbor(chunkRegion1.getIsoWorldRegion());
                    chunkRegion1.getIsoWorldRegion().addNeighbor(this);
                }
            }
        }
    }

    private void addNeighbor(IsoWorldRegion worldRegion0) {
        if (!this.neighbors.contains(worldRegion0)) {
            this.neighbors.add(worldRegion0);
        }
    }

    private void removeNeighbor(IsoWorldRegion worldRegion0) {
        this.neighbors.remove(worldRegion0);
    }

    @Override
    public ArrayList<IsoWorldRegion> getNeighbors() {
        return this.neighbors;
    }

    @Override
    public ArrayList<IsoWorldRegion> getDebugConnectedNeighborCopy() {
        ArrayList arrayList = new ArrayList();
        if (this.neighbors.size() == 0) {
            return arrayList;
        } else {
            arrayList.addAll(this.neighbors);
            return arrayList;
        }
    }

    @Override
    public boolean isFogMask() {
        return this.isEnclosed() && this.isFullyRoofed();
    }

    @Override
    public boolean isPlayerRoom() {
        return this.isFogMask();
    }

    @Override
    public boolean isFullyRoofed() {
        return this.roofCnt == this.squareSize;
    }

    public float getRoofedPercentage() {
        return this.squareSize == 0 ? 0.0F : (float)this.roofCnt / this.squareSize;
    }

    @Override
    public int getRoofCnt() {
        return this.roofCnt;
    }

    protected void addRoof() {
        this.roofCnt++;
        if (this.roofCnt > this.squareSize) {
            IsoRegions.warn("WorldRegion.addRoof roofCount exceed squareSize.");
            this.roofCnt = this.squareSize;
        }
    }

    protected void removeRoofs(int int0) {
        if (int0 > 0) {
            this.roofCnt -= int0;
            if (this.roofCnt < 0) {
                IsoRegions.warn("MasterRegion.removeRoofs Roofcount managed to get below zero.");
                this.roofCnt = 0;
            }
        }
    }

    public void addIsoChunkRegion(IsoChunkRegion region) {
        if (!this.isoChunkRegions.contains(region)) {
            this.squareSize = this.squareSize + region.getSquareSize();
            this.roofCnt = this.roofCnt + region.getRoofCnt();
            this.isDirtyEnclosed = true;
            this.isoChunkRegions.add(region);
            region.setIsoWorldRegion(this);
        }
    }

    protected void removeIsoChunkRegion(IsoChunkRegion chunkRegion) {
        if (this.isoChunkRegions.remove(chunkRegion)) {
            this.squareSize = this.squareSize - chunkRegion.getSquareSize();
            this.roofCnt = this.roofCnt - chunkRegion.getRoofCnt();
            this.isDirtyEnclosed = true;
            chunkRegion.setIsoWorldRegion(null);
        }
    }

    public boolean containsIsoChunkRegion(IsoChunkRegion region) {
        return this.isoChunkRegions.contains(region);
    }

    public ArrayList<IsoChunkRegion> swapIsoChunkRegions(ArrayList<IsoChunkRegion> newlist) {
        ArrayList arrayList = this.isoChunkRegions;
        this.isoChunkRegions = newlist;
        return arrayList;
    }

    protected void resetSquareSize() {
        this.squareSize = 0;
    }

    protected void setDirtyEnclosed() {
        this.isDirtyEnclosed = true;
    }

    public boolean isEnclosed() {
        if (this.isDirtyEnclosed) {
            this.recalcEnclosed();
        }

        return this.enclosed;
    }

    private void recalcEnclosed() {
        this.isDirtyEnclosed = false;
        this.enclosed = true;

        for (int int0 = 0; int0 < this.isoChunkRegions.size(); int0++) {
            IsoChunkRegion chunkRegion = this.isoChunkRegions.get(int0);
            if (!chunkRegion.getIsEnclosed()) {
                this.enclosed = false;
            }
        }
    }

    public void merge(IsoWorldRegion other) {
        if (other.isoChunkRegions.size() > 0) {
            for (int int0 = other.isoChunkRegions.size() - 1; int0 >= 0; int0--) {
                IsoChunkRegion chunkRegion = other.isoChunkRegions.get(int0);
                other.removeIsoChunkRegion(chunkRegion);
                this.addIsoChunkRegion(chunkRegion);
            }

            this.isDirtyEnclosed = true;
            other.isoChunkRegions.clear();
        }

        if (other.neighbors.size() > 0) {
            for (int int1 = 0; int1 < other.neighbors.size(); int1++) {
                IsoWorldRegion worldRegion = other.neighbors.get(int1);
                worldRegion.removeNeighbor(other);
                this.addNeighbor(worldRegion);
            }

            other.neighbors.clear();
        }

        this.manager.releaseIsoWorldRegion(other);
    }

    @Override
    public ArrayList<IsoChunkRegion> getDebugIsoChunkRegionCopy() {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(this.isoChunkRegions);
        return arrayList;
    }
}
