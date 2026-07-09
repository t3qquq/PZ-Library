// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas.isoregion.regions;

import java.util.ArrayList;
import zombie.core.Color;
import zombie.core.Core;
import zombie.iso.areas.isoregion.IsoRegions;

/**
 * TurboTuTone.
 */
public final class IsoChunkRegion implements IChunkRegion {
    private final IsoRegionManager manager;
    private boolean isInPool = false;
    private Color color;
    private int ID;
    private byte zLayer;
    private byte squareSize = 0;
    private byte roofCnt = 0;
    private byte chunkBorderSquaresCnt = 0;
    private final boolean[] enclosed = new boolean[4];
    private boolean enclosedCache = true;
    private final ArrayList<IsoChunkRegion> connectedNeighbors = new ArrayList<>();
    private final ArrayList<IsoChunkRegion> allNeighbors = new ArrayList<>();
    private boolean isDirtyEnclosed = false;
    private IsoWorldRegion isoWorldRegion;

    public int getID() {
        return this.ID;
    }

    public int getSquareSize() {
        return this.squareSize;
    }

    public Color getColor() {
        return this.color;
    }

    public int getzLayer() {
        return this.zLayer;
    }

    public IsoWorldRegion getIsoWorldRegion() {
        return this.isoWorldRegion;
    }

    public void setIsoWorldRegion(IsoWorldRegion mr) {
        this.isoWorldRegion = mr;
    }

    protected boolean isInPool() {
        return this.isInPool;
    }

    protected IsoChunkRegion(IsoRegionManager regionManager) {
        this.manager = regionManager;
    }

    protected void init(int int0, int int1) {
        this.isInPool = false;
        this.ID = int0;
        this.zLayer = (byte)int1;
        this.resetChunkBorderSquaresCnt();
        if (this.color == null) {
            this.color = this.manager.getColor();
        }

        this.squareSize = 0;
        this.roofCnt = 0;
        this.resetEnclosed();
    }

    protected IsoChunkRegion reset() {
        this.isInPool = true;
        this.unlinkNeighbors();
        IsoWorldRegion worldRegion = this.unlinkFromIsoWorldRegion();
        if (worldRegion != null && worldRegion.size() <= 0) {
            if (Core.bDebug) {
                throw new RuntimeException("ChunkRegion.reset IsoChunkRegion has IsoWorldRegion with 0 members.");
            }

            this.manager.releaseIsoWorldRegion(worldRegion);
            IsoRegions.warn("ChunkRegion.reset IsoChunkRegion has IsoWorldRegion with 0 members.");
        }

        this.resetChunkBorderSquaresCnt();
        this.ID = -1;
        this.squareSize = 0;
        this.roofCnt = 0;
        this.resetEnclosed();
        return this;
    }

    public IsoWorldRegion unlinkFromIsoWorldRegion() {
        if (this.isoWorldRegion != null) {
            IsoWorldRegion worldRegion = this.isoWorldRegion;
            this.isoWorldRegion.removeIsoChunkRegion(this);
            this.isoWorldRegion = null;
            return worldRegion;
        } else {
            return null;
        }
    }

    public int getRoofCnt() {
        return this.roofCnt;
    }

    public void addRoof() {
        this.roofCnt++;
        if (this.roofCnt > this.squareSize) {
            IsoRegions.warn("ChunkRegion.addRoof roofCount exceed squareSize.");
            this.roofCnt = this.squareSize;
        } else {
            if (this.isoWorldRegion != null) {
                this.isoWorldRegion.addRoof();
            }
        }
    }

    public void resetRoofCnt() {
        if (this.isoWorldRegion != null) {
            this.isoWorldRegion.removeRoofs(this.roofCnt);
        }

        this.roofCnt = 0;
    }

    public void addSquareCount() {
        this.squareSize++;
    }

    public int getChunkBorderSquaresCnt() {
        return this.chunkBorderSquaresCnt;
    }

    public void addChunkBorderSquaresCnt() {
        this.chunkBorderSquaresCnt++;
    }

    protected void removeChunkBorderSquaresCnt() {
        this.chunkBorderSquaresCnt--;
        if (this.chunkBorderSquaresCnt < 0) {
            this.chunkBorderSquaresCnt = 0;
        }
    }

    protected void resetChunkBorderSquaresCnt() {
        this.chunkBorderSquaresCnt = 0;
    }

    private void resetEnclosed() {
        for (byte byte0 = 0; byte0 < 4; byte0++) {
            this.enclosed[byte0] = true;
        }

        this.isDirtyEnclosed = false;
        this.enclosedCache = true;
    }

    public void setEnclosed(byte dir, boolean b) {
        this.isDirtyEnclosed = true;
        this.enclosed[dir] = b;
    }

    protected void setDirtyEnclosed() {
        this.isDirtyEnclosed = true;
        if (this.isoWorldRegion != null) {
            this.isoWorldRegion.setDirtyEnclosed();
        }
    }

    public boolean getIsEnclosed() {
        if (!this.isDirtyEnclosed) {
            return this.enclosedCache;
        } else {
            this.isDirtyEnclosed = false;
            this.enclosedCache = true;

            for (byte byte0 = 0; byte0 < 4; byte0++) {
                if (!this.enclosed[byte0]) {
                    this.enclosedCache = false;
                }
            }

            if (this.isoWorldRegion != null) {
                this.isoWorldRegion.setDirtyEnclosed();
            }

            return this.enclosedCache;
        }
    }

    public ArrayList<IsoChunkRegion> getConnectedNeighbors() {
        return this.connectedNeighbors;
    }

    public void addConnectedNeighbor(IsoChunkRegion neighbor) {
        if (neighbor != null) {
            if (!this.connectedNeighbors.contains(neighbor)) {
                this.connectedNeighbors.add(neighbor);
            }
        }
    }

    protected void removeConnectedNeighbor(IsoChunkRegion chunkRegion0) {
        this.connectedNeighbors.remove(chunkRegion0);
    }

    public int getNeighborCount() {
        return this.allNeighbors.size();
    }

    protected ArrayList<IsoChunkRegion> getAllNeighbors() {
        return this.allNeighbors;
    }

    public void addNeighbor(IsoChunkRegion neighbor) {
        if (neighbor != null) {
            if (!this.allNeighbors.contains(neighbor)) {
                this.allNeighbors.add(neighbor);
            }
        }
    }

    protected void removeNeighbor(IsoChunkRegion chunkRegion0) {
        this.allNeighbors.remove(chunkRegion0);
    }

    protected void unlinkNeighbors() {
        for (int int0 = 0; int0 < this.connectedNeighbors.size(); int0++) {
            IsoChunkRegion chunkRegion1 = this.connectedNeighbors.get(int0);
            chunkRegion1.removeConnectedNeighbor(this);
        }

        this.connectedNeighbors.clear();

        for (int int1 = 0; int1 < this.allNeighbors.size(); int1++) {
            IsoChunkRegion chunkRegion2 = this.allNeighbors.get(int1);
            chunkRegion2.removeNeighbor(this);
        }

        this.allNeighbors.clear();
    }

    public ArrayList<IsoChunkRegion> getDebugConnectedNeighborCopy() {
        ArrayList arrayList = new ArrayList();
        if (this.connectedNeighbors.size() == 0) {
            return arrayList;
        } else {
            arrayList.addAll(this.connectedNeighbors);
            return arrayList;
        }
    }

    public boolean containsConnectedNeighbor(IsoChunkRegion n) {
        return this.connectedNeighbors.contains(n);
    }

    public boolean containsConnectedNeighborID(int id) {
        if (this.connectedNeighbors.size() == 0) {
            return false;
        } else {
            for (int int0 = 0; int0 < this.connectedNeighbors.size(); int0++) {
                IsoChunkRegion chunkRegion = this.connectedNeighbors.get(int0);
                if (chunkRegion.getID() == id) {
                    return true;
                }
            }

            return false;
        }
    }

    public IsoChunkRegion getConnectedNeighborWithLargestIsoWorldRegion() {
        if (this.connectedNeighbors.size() == 0) {
            return null;
        } else {
            IsoWorldRegion worldRegion0 = null;
            IsoChunkRegion chunkRegion1 = null;

            for (int int0 = 0; int0 < this.connectedNeighbors.size(); int0++) {
                IsoChunkRegion chunkRegion2 = this.connectedNeighbors.get(int0);
                IsoWorldRegion worldRegion1 = chunkRegion2.getIsoWorldRegion();
                if (worldRegion1 != null && (worldRegion0 == null || worldRegion1.getSquareSize() > worldRegion0.getSquareSize())) {
                    worldRegion0 = worldRegion1;
                    chunkRegion1 = chunkRegion2;
                }
            }

            return chunkRegion1;
        }
    }

    protected IsoChunkRegion getFirstNeighborWithIsoWorldRegion() {
        if (this.connectedNeighbors.size() == 0) {
            return null;
        } else {
            for (int int0 = 0; int0 < this.connectedNeighbors.size(); int0++) {
                IsoChunkRegion chunkRegion1 = this.connectedNeighbors.get(int0);
                IsoWorldRegion worldRegion = chunkRegion1.getIsoWorldRegion();
                if (worldRegion != null) {
                    return chunkRegion1;
                }
            }

            return null;
        }
    }
}
