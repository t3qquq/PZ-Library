// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas.isoregion.data;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import zombie.debug.DebugLog;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.areas.isoregion.regions.IsoChunkRegion;
import zombie.iso.areas.isoregion.regions.IsoWorldRegion;

/**
 * TurboTuTone.
 */
public final class DataChunk {
    private final DataCell cell;
    private final int hashId;
    private final int chunkX;
    private final int chunkY;
    protected int highestZ = 0;
    protected long lastUpdateStamp = 0L;
    private final boolean[] activeZLayers = new boolean[8];
    private final boolean[] dirtyZLayers = new boolean[8];
    private byte[] squareFlags;
    private byte[] regionIDs;
    private final ArrayList<ArrayList<IsoChunkRegion>> chunkRegions = new ArrayList<>(8);
    private static byte selectedFlags;
    private static final ArrayDeque<DataSquarePos> tmpSquares = new ArrayDeque<>();
    private static final HashSet<Integer> tmpLinkedChunks = new HashSet<>();
    private static final boolean[] exploredPositions = new boolean[100];
    private static IsoChunkRegion lastCurRegion;
    private static IsoChunkRegion lastOtherRegionFullConnect;
    private static ArrayList<IsoChunkRegion> oldList = new ArrayList<>();
    private static final ArrayDeque<IsoChunkRegion> chunkQueue = new ArrayDeque<>();

    protected DataChunk(int int1, int int2, DataCell dataCell, int int0) {
        this.cell = dataCell;
        this.hashId = int0 < 0 ? IsoRegions.hash(int1, int2) : int0;
        this.chunkX = int1;
        this.chunkY = int2;

        for (int int3 = 0; int3 < 8; int3++) {
            this.chunkRegions.add(new ArrayList<>());
        }
    }

    protected int getHashId() {
        return this.hashId;
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkY() {
        return this.chunkY;
    }

    protected ArrayList<IsoChunkRegion> getChunkRegions(int int0) {
        return this.chunkRegions.get(int0);
    }

    public long getLastUpdateStamp() {
        return this.lastUpdateStamp;
    }

    public void setLastUpdateStamp(long _lastUpdateStamp) {
        this.lastUpdateStamp = _lastUpdateStamp;
    }

    protected boolean isDirty(int int0) {
        return this.activeZLayers[int0] ? this.dirtyZLayers[int0] : false;
    }

    protected void setDirty(int int0) {
        if (this.activeZLayers[int0]) {
            this.dirtyZLayers[int0] = true;
            this.cell.dataRoot.EnqueueDirtyDataChunk(this);
        }
    }

    public void setDirtyAllActive() {
        boolean boolean0 = false;

        for (int int0 = 0; int0 < 8; int0++) {
            if (this.activeZLayers[int0]) {
                this.dirtyZLayers[int0] = true;
                if (!boolean0) {
                    this.cell.dataRoot.EnqueueDirtyDataChunk(this);
                    boolean0 = true;
                }
            }
        }
    }

    protected void unsetDirtyAll() {
        for (int int0 = 0; int0 < 8; int0++) {
            this.dirtyZLayers[int0] = false;
        }
    }

    private boolean validCoords(int int2, int int1, int int0) {
        return int2 >= 0 && int2 < 10 && int1 >= 0 && int1 < 10 && int0 >= 0 && int0 < this.highestZ + 1;
    }

    private int getCoord1D(int int0, int int1, int int2) {
        return int2 * 10 * 10 + int1 * 10 + int0;
    }

    public byte getSquare(int x, int y, int z) {
        return this.getSquare(x, y, z, false);
    }

    public byte getSquare(int x, int y, int z, boolean ignoreCoordCheck) {
        if (this.squareFlags != null && (ignoreCoordCheck || this.validCoords(x, y, z))) {
            return this.activeZLayers[z] ? this.squareFlags[this.getCoord1D(x, y, z)] : -1;
        } else {
            return -1;
        }
    }

    protected byte setOrAddSquare(int int0, int int1, int int2, byte byte0) {
        return this.setOrAddSquare(int0, int1, int2, byte0, false);
    }

    protected byte setOrAddSquare(int int0, int int1, int int2, byte byte0, boolean boolean0) {
        if (!boolean0 && !this.validCoords(int0, int1, int2)) {
            return -1;
        } else {
            this.ensureSquares(int2);
            int int3 = this.getCoord1D(int0, int1, int2);
            if (this.squareFlags[int3] != byte0) {
                this.setDirty(int2);
            }

            this.squareFlags[int3] = byte0;
            return byte0;
        }
    }

    private void ensureSquares(int int0) {
        if (int0 >= 0 && int0 < 8) {
            if (!this.activeZLayers[int0]) {
                this.ensureSquareArray(int0);
                this.activeZLayers[int0] = true;
                if (int0 > this.highestZ) {
                    this.highestZ = int0;
                }

                for (int int1 = 0; int1 < 10; int1++) {
                    for (int int2 = 0; int2 < 10; int2++) {
                        int int3 = this.getCoord1D(int2, int1, int0);
                        this.squareFlags[int3] = (byte)(int0 == 0 ? 16 : 0);
                    }
                }
            }
        }
    }

    private void ensureSquareArray(int int1) {
        int int0 = (int1 + 1) * 10 * 10;
        if (this.squareFlags == null || this.squareFlags.length < int0) {
            byte[] bytes0 = this.squareFlags;
            byte[] bytes1 = this.regionIDs;
            this.squareFlags = new byte[int0];
            this.regionIDs = new byte[int0];
            if (bytes0 != null) {
                for (int int2 = 0; int2 < bytes0.length; int2++) {
                    this.squareFlags[int2] = bytes0[int2];
                    this.regionIDs[int2] = bytes1[int2];
                }
            }
        }
    }

    /**
     * SAVE/LOAD
     */
    public void save(ByteBuffer bb) {
        try {
            int int0 = bb.position();
            bb.putInt(0);
            bb.putInt(this.highestZ);
            int int1 = (this.highestZ + 1) * 100;
            bb.putInt(int1);

            for (int int2 = 0; int2 < int1; int2++) {
                bb.put(this.squareFlags[int2]);
            }

            int int3 = bb.position();
            bb.position(int0);
            bb.putInt(int3 - int0);
            bb.position(int3);
        } catch (Exception exception) {
            DebugLog.log(exception.getMessage());
            exception.printStackTrace();
        }
    }

    public void load(ByteBuffer bb, int worldVersion, boolean readLength) {
        try {
            if (readLength) {
                bb.getInt();
            }

            this.highestZ = bb.getInt();

            for (int int0 = this.highestZ; int0 >= 0; int0--) {
                this.ensureSquares(int0);
            }

            int int1 = bb.getInt();

            for (int int2 = 0; int2 < int1; int2++) {
                this.squareFlags[int2] = bb.get();
            }
        } catch (Exception exception) {
            DebugLog.log(exception.getMessage());
            exception.printStackTrace();
        }
    }

    public void setSelectedFlags(int x, int y, int z) {
        if (z >= 0 && z <= this.highestZ) {
            selectedFlags = this.squareFlags[this.getCoord1D(x, y, z)];
        } else {
            selectedFlags = -1;
        }
    }

    public boolean selectedHasFlags(byte flags) {
        return (selectedFlags & flags) == flags;
    }

    protected boolean squareHasFlags(int int0, int int1, int int2, byte byte0) {
        return this.squareHasFlags(this.getCoord1D(int0, int1, int2), byte0);
    }

    private boolean squareHasFlags(int int0, byte byte1) {
        byte byte0 = this.squareFlags[int0];
        return (byte0 & byte1) == byte1;
    }

    public byte squareGetFlags(int x, int y, int z) {
        return this.squareGetFlags(this.getCoord1D(x, y, z));
    }

    private byte squareGetFlags(int int0) {
        return this.squareFlags[int0];
    }

    protected void squareAddFlags(int int0, int int1, int int2, byte byte0) {
        this.squareAddFlags(this.getCoord1D(int0, int1, int2), byte0);
    }

    private void squareAddFlags(int int0, byte byte0) {
        this.squareFlags[int0] = (byte)(this.squareFlags[int0] | byte0);
    }

    protected void squareRemoveFlags(int int0, int int1, int int2, byte byte0) {
        this.squareRemoveFlags(this.getCoord1D(int0, int1, int2), byte0);
    }

    private void squareRemoveFlags(int int0, byte byte0) {
        this.squareFlags[int0] = (byte)(this.squareFlags[int0] ^ byte0);
    }

    protected boolean squareCanConnect(int int1, int int2, int int0, byte byte0) {
        return this.squareCanConnect(this.getCoord1D(int1, int2, int0), int0, byte0);
    }

    private boolean squareCanConnect(int int1, int int0, byte byte0) {
        if (int0 >= 0 && int0 < this.highestZ + 1) {
            if (byte0 == 0) {
                return !this.squareHasFlags(int1, (byte)1);
            }

            if (byte0 == 1) {
                return !this.squareHasFlags(int1, (byte)2);
            }

            if (byte0 == 2) {
                return true;
            }

            if (byte0 == 3) {
                return true;
            }

            if (byte0 == 4) {
                return !this.squareHasFlags(int1, (byte)64);
            }

            if (byte0 == 5) {
                return !this.squareHasFlags(int1, (byte)16);
            }
        }

        return false;
    }

    public IsoChunkRegion getIsoChunkRegion(int x, int y, int z) {
        return this.getIsoChunkRegion(this.getCoord1D(x, y, z), z);
    }

    private IsoChunkRegion getIsoChunkRegion(int int1, int int0) {
        if (int0 >= 0 && int0 < this.highestZ + 1) {
            byte byte0 = this.regionIDs[int1];
            if (byte0 >= 0 && byte0 < this.chunkRegions.get(int0).size()) {
                return this.chunkRegions.get(int0).get(byte0);
            }
        }

        return null;
    }

    public void setRegion(int x, int y, int z, byte regionIndex) {
        this.regionIDs[this.getCoord1D(x, y, z)] = regionIndex;
    }

    protected void recalculate() {
        for (int int0 = 0; int0 <= this.highestZ; int0++) {
            if (this.dirtyZLayers[int0] && this.activeZLayers[int0]) {
                this.recalculate(int0);
            }
        }
    }

    private void recalculate(int int0) {
        ArrayList arrayList = this.chunkRegions.get(int0);

        for (int int1 = arrayList.size() - 1; int1 >= 0; int1--) {
            IsoChunkRegion chunkRegion0 = (IsoChunkRegion)arrayList.get(int1);
            IsoWorldRegion worldRegion = chunkRegion0.unlinkFromIsoWorldRegion();
            if (worldRegion != null && worldRegion.size() <= 0) {
                this.cell.dataRoot.regionManager.releaseIsoWorldRegion(worldRegion);
            }

            this.cell.dataRoot.regionManager.releaseIsoChunkRegion(chunkRegion0);
            arrayList.remove(int1);
        }

        arrayList.clear();
        byte byte0 = 100;
        Arrays.fill(this.regionIDs, int0 * byte0, int0 * byte0 + byte0, (byte)-1);

        for (int int2 = 0; int2 < 10; int2++) {
            for (int int3 = 0; int3 < 10; int3++) {
                if (this.regionIDs[this.getCoord1D(int3, int2, int0)] == -1) {
                    IsoChunkRegion chunkRegion1 = this.floodFill(int3, int2, int0);
                }
            }
        }
    }

    private IsoChunkRegion floodFill(int int1, int int2, int int0) {
        IsoChunkRegion chunkRegion0 = this.cell.dataRoot.regionManager.allocIsoChunkRegion(int0);
        byte byte0 = (byte)this.chunkRegions.get(int0).size();
        this.chunkRegions.get(int0).add(chunkRegion0);
        this.clearExploredPositions();
        tmpSquares.clear();
        tmpLinkedChunks.clear();
        tmpSquares.add(DataSquarePos.alloc(int1, int2, int0));

        DataSquarePos dataSquarePos0;
        while ((dataSquarePos0 = tmpSquares.poll()) != null) {
            int int3 = this.getCoord1D(dataSquarePos0.x, dataSquarePos0.y, dataSquarePos0.z);
            this.setExploredPosition(int3, dataSquarePos0.z);
            if (this.regionIDs[int3] == -1) {
                this.regionIDs[int3] = byte0;
                chunkRegion0.addSquareCount();

                for (byte byte1 = 0; byte1 < 4; byte1++) {
                    DataSquarePos dataSquarePos1 = this.getNeighbor(dataSquarePos0, byte1);
                    if (dataSquarePos1 != null) {
                        int int4 = this.getCoord1D(dataSquarePos1.x, dataSquarePos1.y, dataSquarePos1.z);
                        if (this.isExploredPosition(int4, dataSquarePos1.z)) {
                            DataSquarePos.release(dataSquarePos1);
                        } else {
                            if (this.squareCanConnect(int3, dataSquarePos0.z, byte1)
                                && this.squareCanConnect(int4, dataSquarePos1.z, IsoRegions.GetOppositeDir(byte1))) {
                                if (this.regionIDs[int4] == -1) {
                                    tmpSquares.add(dataSquarePos1);
                                    this.setExploredPosition(int4, dataSquarePos1.z);
                                    continue;
                                }
                            } else {
                                IsoChunkRegion chunkRegion1 = this.getIsoChunkRegion(int4, dataSquarePos1.z);
                                if (chunkRegion1 != null && chunkRegion1 != chunkRegion0) {
                                    if (!tmpLinkedChunks.contains(chunkRegion1.getID())) {
                                        chunkRegion0.addNeighbor(chunkRegion1);
                                        chunkRegion1.addNeighbor(chunkRegion0);
                                        tmpLinkedChunks.add(chunkRegion1.getID());
                                    }

                                    this.setExploredPosition(int4, dataSquarePos1.z);
                                    DataSquarePos.release(dataSquarePos1);
                                    continue;
                                }
                            }

                            DataSquarePos.release(dataSquarePos1);
                        }
                    } else if (this.squareCanConnect(int3, dataSquarePos0.z, byte1)) {
                        chunkRegion0.addChunkBorderSquaresCnt();
                    }
                }
            }
        }

        return chunkRegion0;
    }

    private boolean isExploredPosition(int int1, int int2) {
        int int0 = int1 - int2 * 10 * 10;
        return exploredPositions[int0];
    }

    private void setExploredPosition(int int1, int int2) {
        int int0 = int1 - int2 * 10 * 10;
        exploredPositions[int0] = true;
    }

    private void clearExploredPositions() {
        Arrays.fill(exploredPositions, false);
    }

    private DataSquarePos getNeighbor(DataSquarePos dataSquarePos, byte byte0) {
        int int0 = dataSquarePos.x;
        int int1 = dataSquarePos.y;
        if (byte0 == 1) {
            int0 = dataSquarePos.x - 1;
        } else if (byte0 == 3) {
            int0 = dataSquarePos.x + 1;
        }

        if (byte0 == 0) {
            int1 = dataSquarePos.y - 1;
        } else if (byte0 == 2) {
            int1 = dataSquarePos.y + 1;
        }

        return int0 >= 0 && int0 < 10 && int1 >= 0 && int1 < 10 ? DataSquarePos.alloc(int0, int1, dataSquarePos.z) : null;
    }

    protected void link(DataChunk dataChunk1, DataChunk dataChunk2, DataChunk dataChunk3, DataChunk dataChunk4) {
        for (int int0 = 0; int0 <= this.highestZ; int0++) {
            if (this.dirtyZLayers[int0] && this.activeZLayers[int0]) {
                this.linkRegionsOnSide(int0, dataChunk1, (byte)0);
                this.linkRegionsOnSide(int0, dataChunk2, (byte)1);
                this.linkRegionsOnSide(int0, dataChunk3, (byte)2);
                this.linkRegionsOnSide(int0, dataChunk4, (byte)3);
            }
        }
    }

    private void linkRegionsOnSide(int int4, DataChunk dataChunk0, byte byte0) {
        int int0;
        int int1;
        int int2;
        int int3;
        if (byte0 != 0 && byte0 != 2) {
            int0 = byte0 == 1 ? 0 : 9;
            int1 = int0 + 1;
            int2 = 0;
            int3 = 10;
        } else {
            int0 = 0;
            int1 = 10;
            int2 = byte0 == 0 ? 0 : 9;
            int3 = int2 + 1;
        }

        if (dataChunk0 != null && dataChunk0.isDirty(int4)) {
            dataChunk0.resetEnclosedSide(int4, IsoRegions.GetOppositeDir(byte0));
        }

        lastCurRegion = null;
        lastOtherRegionFullConnect = null;

        for (int int5 = int2; int5 < int3; int5++) {
            for (int int6 = int0; int6 < int1; int6++) {
                int int7;
                int int8;
                if (byte0 != 0 && byte0 != 2) {
                    int7 = byte0 == 1 ? 9 : 0;
                    int8 = int5;
                } else {
                    int7 = int6;
                    int8 = byte0 == 0 ? 9 : 0;
                }

                int int9 = this.getCoord1D(int6, int5, int4);
                int int10 = this.getCoord1D(int7, int8, int4);
                IsoChunkRegion chunkRegion0 = this.getIsoChunkRegion(int9, int4);
                IsoChunkRegion chunkRegion1 = dataChunk0 != null ? dataChunk0.getIsoChunkRegion(int10, int4) : null;
                if (chunkRegion0 == null) {
                    IsoRegions.warn("ds.getRegion()==null, shouldnt happen at this point.");
                } else {
                    if (lastCurRegion != null && lastCurRegion != chunkRegion0) {
                        lastOtherRegionFullConnect = null;
                    }

                    if (lastCurRegion == null || lastCurRegion != chunkRegion0 || chunkRegion1 == null || lastOtherRegionFullConnect != chunkRegion1) {
                        if (dataChunk0 != null && chunkRegion1 != null) {
                            if (this.squareCanConnect(int9, int4, byte0) && dataChunk0.squareCanConnect(int10, int4, IsoRegions.GetOppositeDir(byte0))) {
                                chunkRegion0.addConnectedNeighbor(chunkRegion1);
                                chunkRegion1.addConnectedNeighbor(chunkRegion0);
                                chunkRegion0.addNeighbor(chunkRegion1);
                                chunkRegion1.addNeighbor(chunkRegion0);
                                if (!chunkRegion1.getIsEnclosed()) {
                                    chunkRegion1.setEnclosed(IsoRegions.GetOppositeDir(byte0), true);
                                }

                                lastOtherRegionFullConnect = chunkRegion1;
                            } else {
                                chunkRegion0.addNeighbor(chunkRegion1);
                                chunkRegion1.addNeighbor(chunkRegion0);
                                if (!chunkRegion1.getIsEnclosed()) {
                                    chunkRegion1.setEnclosed(IsoRegions.GetOppositeDir(byte0), true);
                                }

                                lastOtherRegionFullConnect = null;
                            }
                        } else if (this.squareCanConnect(int9, int4, byte0)) {
                            chunkRegion0.setEnclosed(byte0, false);
                        }

                        lastCurRegion = chunkRegion0;
                    }
                }
            }
        }
    }

    private void resetEnclosedSide(int int0, byte byte0) {
        ArrayList arrayList = this.chunkRegions.get(int0);

        for (int int1 = 0; int1 < arrayList.size(); int1++) {
            IsoChunkRegion chunkRegion = (IsoChunkRegion)arrayList.get(int1);
            if (chunkRegion.getzLayer() == int0) {
                chunkRegion.setEnclosed(byte0, true);
            }
        }
    }

    protected void interConnect() {
        for (int int0 = 0; int0 <= this.highestZ; int0++) {
            if (this.dirtyZLayers[int0] && this.activeZLayers[int0]) {
                ArrayList arrayList = this.chunkRegions.get(int0);

                for (int int1 = 0; int1 < arrayList.size(); int1++) {
                    IsoChunkRegion chunkRegion0 = (IsoChunkRegion)arrayList.get(int1);
                    if (chunkRegion0.getzLayer() == int0 && chunkRegion0.getIsoWorldRegion() == null) {
                        if (chunkRegion0.getConnectedNeighbors().size() == 0) {
                            IsoWorldRegion worldRegion0 = this.cell.dataRoot.regionManager.allocIsoWorldRegion();
                            this.cell.dataRoot.EnqueueDirtyIsoWorldRegion(worldRegion0);
                            worldRegion0.addIsoChunkRegion(chunkRegion0);
                        } else {
                            IsoChunkRegion chunkRegion1 = chunkRegion0.getConnectedNeighborWithLargestIsoWorldRegion();
                            if (chunkRegion1 == null) {
                                IsoWorldRegion worldRegion1 = this.cell.dataRoot.regionManager.allocIsoWorldRegion();
                                this.cell.dataRoot.EnqueueDirtyIsoWorldRegion(worldRegion1);
                                this.floodFillExpandWorldRegion(chunkRegion0, worldRegion1);
                                DataRoot.floodFills++;
                            } else {
                                IsoWorldRegion worldRegion2 = chunkRegion1.getIsoWorldRegion();
                                oldList.clear();
                                oldList = worldRegion2.swapIsoChunkRegions(oldList);

                                for (int int2 = 0; int2 < oldList.size(); int2++) {
                                    IsoChunkRegion chunkRegion2 = oldList.get(int2);
                                    chunkRegion2.setIsoWorldRegion(null);
                                }

                                this.cell.dataRoot.regionManager.releaseIsoWorldRegion(worldRegion2);
                                IsoWorldRegion worldRegion3 = this.cell.dataRoot.regionManager.allocIsoWorldRegion();
                                this.cell.dataRoot.EnqueueDirtyIsoWorldRegion(worldRegion3);
                                this.floodFillExpandWorldRegion(chunkRegion0, worldRegion3);

                                for (int int3 = 0; int3 < oldList.size(); int3++) {
                                    IsoChunkRegion chunkRegion3 = oldList.get(int3);
                                    if (chunkRegion3.getIsoWorldRegion() == null) {
                                        IsoWorldRegion worldRegion4 = this.cell.dataRoot.regionManager.allocIsoWorldRegion();
                                        this.cell.dataRoot.EnqueueDirtyIsoWorldRegion(worldRegion4);
                                        this.floodFillExpandWorldRegion(chunkRegion3, worldRegion4);
                                    }
                                }

                                DataRoot.floodFills++;
                            }
                        }
                    }
                }
            }
        }
    }

    private void floodFillExpandWorldRegion(IsoChunkRegion chunkRegion0, IsoWorldRegion worldRegion) {
        chunkQueue.add(chunkRegion0);

        IsoChunkRegion chunkRegion1;
        while ((chunkRegion1 = chunkQueue.poll()) != null) {
            worldRegion.addIsoChunkRegion(chunkRegion1);
            if (chunkRegion1.getConnectedNeighbors().size() != 0) {
                for (int int0 = 0; int0 < chunkRegion1.getConnectedNeighbors().size(); int0++) {
                    IsoChunkRegion chunkRegion2 = chunkRegion1.getConnectedNeighbors().get(int0);
                    if (!chunkQueue.contains(chunkRegion2)) {
                        if (chunkRegion2.getIsoWorldRegion() == null) {
                            chunkQueue.add(chunkRegion2);
                        } else if (chunkRegion2.getIsoWorldRegion() != worldRegion) {
                            worldRegion.merge(chunkRegion2.getIsoWorldRegion());
                        }
                    }
                }
            }
        }
    }

    protected void recalcRoofs() {
        if (this.highestZ >= 1) {
            for (int int0 = 0; int0 < this.chunkRegions.size(); int0++) {
                for (int int1 = 0; int1 < this.chunkRegions.get(int0).size(); int1++) {
                    IsoChunkRegion chunkRegion0 = this.chunkRegions.get(int0).get(int1);
                    chunkRegion0.resetRoofCnt();
                }
            }

            int int2 = this.highestZ;

            for (int int3 = 0; int3 < 10; int3++) {
                for (int int4 = 0; int4 < 10; int4++) {
                    byte byte0 = this.getSquare(int4, int3, int2);
                    boolean boolean0 = false;
                    if (byte0 > 0) {
                        boolean0 = this.squareHasFlags(int4, int3, int2, (byte)16);
                    }

                    if (int2 >= 1) {
                        for (int int5 = int2 - 1; int5 >= 0; int5--) {
                            byte0 = this.getSquare(int4, int3, int5);
                            if (byte0 > 0) {
                                boolean0 = boolean0 || this.squareHasFlags(int4, int3, int5, (byte)32);
                                if (boolean0) {
                                    IsoChunkRegion chunkRegion1 = this.getIsoChunkRegion(int4, int3, int5);
                                    if (chunkRegion1 != null) {
                                        chunkRegion1.addRoof();
                                        if (chunkRegion1.getIsoWorldRegion() != null && !chunkRegion1.getIsoWorldRegion().isEnclosed()) {
                                            boolean0 = false;
                                        }
                                    } else {
                                        boolean0 = false;
                                    }
                                }

                                if (!boolean0) {
                                    boolean0 = this.squareHasFlags(int4, int3, int5, (byte)16);
                                }
                            } else {
                                boolean0 = false;
                            }
                        }
                    }
                }
            }
        }
    }
}
