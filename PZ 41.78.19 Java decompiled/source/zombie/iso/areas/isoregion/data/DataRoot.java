// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas.isoregion.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import zombie.core.Colors;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.areas.isoregion.regions.IsoChunkRegion;
import zombie.iso.areas.isoregion.regions.IsoRegionManager;
import zombie.iso.areas.isoregion.regions.IsoWorldRegion;

/**
 * TurboTuTone.
 */
public final class DataRoot {
    private final Map<Integer, DataCell> cellMap = new HashMap<>();
    public final DataRoot.SelectInfo select = new DataRoot.SelectInfo(this);
    private final DataRoot.SelectInfo selectInternal = new DataRoot.SelectInfo(this);
    public final IsoRegionManager regionManager;
    private final ArrayList<IsoWorldRegion> dirtyIsoWorldRegions = new ArrayList<>();
    private final ArrayList<DataChunk> dirtyChunks = new ArrayList<>();
    protected static int recalcs;
    protected static int floodFills;
    protected static int merges;
    private static final long[] t_start = new long[5];
    private static final long[] t_end = new long[5];
    private static final long[] t_time = new long[5];

    public DataRoot() {
        this.regionManager = new IsoRegionManager(this);
    }

    public void getAllChunks(List<DataChunk> list) {
        for (Entry entry : this.cellMap.entrySet()) {
            ((DataCell)entry.getValue()).getAllChunks(list);
        }
    }

    private DataCell getCell(int int0) {
        return this.cellMap.get(int0);
    }

    private DataCell addCell(int int0, int int1, int int2) {
        DataCell dataCell = new DataCell(this, int0, int1, int2);
        this.cellMap.put(int2, dataCell);
        return dataCell;
    }

    public DataChunk getDataChunk(int chunkx, int chunky) {
        int int0 = IsoRegions.hash(chunkx / 30, chunky / 30);
        DataCell dataCell = this.cellMap.get(int0);
        if (dataCell != null) {
            int int1 = IsoRegions.hash(chunkx, chunky);
            return dataCell.getChunk(int1);
        } else {
            return null;
        }
    }

    private void setDataChunk(DataChunk dataChunk) {
        int int0 = IsoRegions.hash(dataChunk.getChunkX() / 30, dataChunk.getChunkY() / 30);
        DataCell dataCell = this.cellMap.get(int0);
        if (dataCell == null) {
            dataCell = this.addCell(dataChunk.getChunkX() / 30, dataChunk.getChunkY() / 30, int0);
        }

        dataCell.setChunk(dataChunk);
    }

    public IsoWorldRegion getIsoWorldRegion(int x, int y, int z) {
        this.selectInternal.reset(x, y, z, false);
        if (this.selectInternal.chunk != null) {
            IsoChunkRegion chunkRegion = this.selectInternal.chunk.getIsoChunkRegion(this.selectInternal.chunkSquareX, this.selectInternal.chunkSquareY, z);
            if (chunkRegion != null) {
                return chunkRegion.getIsoWorldRegion();
            }
        }

        return null;
    }

    public byte getSquareFlags(int x, int y, int z) {
        this.selectInternal.reset(x, y, z, false);
        return this.selectInternal.square;
    }

    public IsoChunkRegion getIsoChunkRegion(int x, int y, int z) {
        this.selectInternal.reset(x, y, z, false);
        return this.selectInternal.chunk != null
            ? this.selectInternal.chunk.getIsoChunkRegion(this.selectInternal.chunkSquareX, this.selectInternal.chunkSquareY, z)
            : null;
    }

    public void resetAllData() {
        ArrayList arrayList = new ArrayList();

        for (Entry entry0 : this.cellMap.entrySet()) {
            DataCell dataCell = (DataCell)entry0.getValue();

            for (Entry entry1 : dataCell.dataChunks.entrySet()) {
                DataChunk dataChunk = (DataChunk)entry1.getValue();

                for (int int0 = 0; int0 < 8; int0++) {
                    for (IsoChunkRegion chunkRegion : dataChunk.getChunkRegions(int0)) {
                        if (chunkRegion.getIsoWorldRegion() != null && !arrayList.contains(chunkRegion.getIsoWorldRegion())) {
                            arrayList.add(chunkRegion.getIsoWorldRegion());
                        }

                        chunkRegion.setIsoWorldRegion(null);
                        this.regionManager.releaseIsoChunkRegion(chunkRegion);
                    }
                }
            }

            dataCell.dataChunks.clear();
        }

        this.cellMap.clear();

        for (IsoWorldRegion worldRegion : arrayList) {
            this.regionManager.releaseIsoWorldRegion(worldRegion);
        }
    }

    public void EnqueueDirtyDataChunk(DataChunk chunk) {
        if (!this.dirtyChunks.contains(chunk)) {
            this.dirtyChunks.add(chunk);
        }
    }

    public void EnqueueDirtyIsoWorldRegion(IsoWorldRegion mr) {
        if (!this.dirtyIsoWorldRegions.contains(mr)) {
            this.dirtyIsoWorldRegions.add(mr);
        }
    }

    public void DequeueDirtyIsoWorldRegion(IsoWorldRegion mr) {
        this.dirtyIsoWorldRegions.remove(mr);
    }

    public void updateExistingSquare(int x, int y, int z, byte flags) {
        this.select.reset(x, y, z, false);
        if (this.select.chunk != null) {
            byte byte0 = -1;
            if (this.select.square != -1) {
                byte0 = this.select.square;
            }

            if (flags == byte0) {
                return;
            }

            this.select.chunk.setOrAddSquare(this.select.chunkSquareX, this.select.chunkSquareY, this.select.z, flags, true);
        } else {
            IsoRegions.warn("DataRoot.updateExistingSquare -> trying to change a square on a unknown chunk");
        }
    }

    public void processDirtyChunks() {
        if (this.dirtyChunks.size() > 0) {
            long long0 = System.nanoTime();
            recalcs = 0;
            floodFills = 0;
            merges = 0;
            t_start[0] = System.nanoTime();

            for (int int0 = 0; int0 < this.dirtyChunks.size(); int0++) {
                DataChunk dataChunk0 = this.dirtyChunks.get(int0);
                dataChunk0.recalculate();
                recalcs++;
            }

            t_end[0] = System.nanoTime();
            t_start[1] = System.nanoTime();

            for (int int1 = 0; int1 < this.dirtyChunks.size(); int1++) {
                DataChunk dataChunk1 = this.dirtyChunks.get(int1);
                DataChunk dataChunk2 = this.getDataChunk(dataChunk1.getChunkX(), dataChunk1.getChunkY() - 1);
                DataChunk dataChunk3 = this.getDataChunk(dataChunk1.getChunkX() - 1, dataChunk1.getChunkY());
                DataChunk dataChunk4 = this.getDataChunk(dataChunk1.getChunkX(), dataChunk1.getChunkY() + 1);
                DataChunk dataChunk5 = this.getDataChunk(dataChunk1.getChunkX() + 1, dataChunk1.getChunkY());
                dataChunk1.link(dataChunk2, dataChunk3, dataChunk4, dataChunk5);
            }

            t_end[1] = System.nanoTime();
            t_start[2] = System.nanoTime();

            for (int int2 = 0; int2 < this.dirtyChunks.size(); int2++) {
                DataChunk dataChunk6 = this.dirtyChunks.get(int2);
                dataChunk6.interConnect();
            }

            t_end[2] = System.nanoTime();
            t_start[3] = System.nanoTime();

            for (int int3 = 0; int3 < this.dirtyChunks.size(); int3++) {
                DataChunk dataChunk7 = this.dirtyChunks.get(int3);
                dataChunk7.recalcRoofs();
                dataChunk7.unsetDirtyAll();
            }

            t_end[3] = System.nanoTime();
            t_start[4] = System.nanoTime();
            if (this.dirtyIsoWorldRegions.size() > 0) {
                for (int int4 = 0; int4 < this.dirtyIsoWorldRegions.size(); int4++) {
                    IsoWorldRegion worldRegion0 = this.dirtyIsoWorldRegions.get(int4);
                    worldRegion0.unlinkNeighbors();
                }

                for (int int5 = 0; int5 < this.dirtyIsoWorldRegions.size(); int5++) {
                    IsoWorldRegion worldRegion1 = this.dirtyIsoWorldRegions.get(int5);
                    worldRegion1.linkNeighbors();
                }

                this.dirtyIsoWorldRegions.clear();
            }

            t_end[4] = System.nanoTime();
            this.dirtyChunks.clear();
            long long1 = System.nanoTime();
            long long2 = long1 - long0;
            if (IsoRegions.PRINT_D) {
                t_time[0] = t_end[0] - t_start[0];
                t_time[1] = t_end[1] - t_start[1];
                t_time[2] = t_end[2] - t_start[2];
                t_time[3] = t_end[3] - t_start[3];
                t_time[4] = t_end[4] - t_start[4];
                IsoRegions.log(
                    "--- IsoRegion update: "
                        + String.format("%.6f", long2 / 1000000.0)
                        + " ms, recalc: "
                        + String.format("%.6f", t_time[0] / 1000000.0)
                        + " ms, link: "
                        + String.format("%.6f", t_time[1] / 1000000.0)
                        + " ms, interconnect: "
                        + String.format("%.6f", t_time[2] / 1000000.0)
                        + " ms, roofs: "
                        + String.format("%.6f", t_time[3] / 1000000.0)
                        + " ms, worldRegion: "
                        + String.format("%.6f", t_time[4] / 1000000.0)
                        + " ms, recalcs = "
                        + recalcs
                        + ", merges = "
                        + merges
                        + ", floodfills = "
                        + floodFills,
                    Colors.CornFlowerBlue
                );
            }
        }
    }

    public static final class SelectInfo {
        public int x;
        public int y;
        public int z;
        public int chunkSquareX;
        public int chunkSquareY;
        public int chunkx;
        public int chunky;
        public int cellx;
        public int celly;
        public int chunkID;
        public int cellID;
        public DataCell cell;
        public DataChunk chunk;
        public byte square;
        private final DataRoot root;

        private SelectInfo(DataRoot dataRoot) {
            this.root = dataRoot;
        }

        public void reset(int _x, int _y, int _z, boolean createSquare) {
            this.reset(_x, _y, _z, createSquare, createSquare);
        }

        public void reset(int _x, int _y, int _z, boolean createChunk, boolean createSquare) {
            this.x = _x;
            this.y = _y;
            this.z = _z;
            this.chunkSquareX = _x % 10;
            this.chunkSquareY = _y % 10;
            this.chunkx = _x / 10;
            this.chunky = _y / 10;
            this.cellx = _x / 300;
            this.celly = _y / 300;
            this.chunkID = IsoRegions.hash(this.chunkx, this.chunky);
            this.cellID = IsoRegions.hash(this.cellx, this.celly);
            this.cell = null;
            this.chunk = null;
            this.square = -1;
            this.ensureSquare(createSquare);
            if (this.chunk == null && createChunk) {
                this.ensureChunk(createChunk);
            }
        }

        private void ensureCell(boolean boolean0) {
            if (this.cell == null) {
                this.cell = this.root.getCell(this.cellID);
            }

            if (this.cell == null && boolean0) {
                this.cell = this.root.addCell(this.cellx, this.celly, this.cellID);
            }
        }

        private void ensureChunk(boolean boolean0) {
            this.ensureCell(boolean0);
            if (this.cell != null) {
                if (this.chunk == null) {
                    this.chunk = this.cell.getChunk(this.chunkID);
                }

                if (this.chunk == null && boolean0) {
                    this.chunk = this.cell.addChunk(this.chunkx, this.chunky, this.chunkID);
                }
            }
        }

        private void ensureSquare(boolean boolean0) {
            this.ensureCell(boolean0);
            if (this.cell != null) {
                this.ensureChunk(boolean0);
                if (this.chunk != null) {
                    if (this.square == -1) {
                        this.square = this.chunk.getSquare(this.chunkSquareX, this.chunkSquareY, this.z, true);
                    }

                    if (this.square == -1 && boolean0) {
                        this.square = this.chunk.setOrAddSquare(this.chunkSquareX, this.chunkSquareY, this.z, (byte)0, true);
                    }
                }
            }
        }
    }
}
