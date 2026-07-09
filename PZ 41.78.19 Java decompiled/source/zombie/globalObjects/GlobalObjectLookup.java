// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.globalObjects;

import java.util.ArrayList;
import java.util.Arrays;
import zombie.debug.DebugLog;
import zombie.iso.IsoMetaGrid;
import zombie.network.GameClient;
import zombie.network.GameServer;

public final class GlobalObjectLookup {
    private static final int SQUARES_PER_CHUNK = 10;
    private static final int SQUARES_PER_CELL = 300;
    private static final int CHUNKS_PER_CELL = 30;
    private static IsoMetaGrid metaGrid;
    private static final GlobalObjectLookup.Shared sharedServer = new GlobalObjectLookup.Shared();
    private static final GlobalObjectLookup.Shared sharedClient = new GlobalObjectLookup.Shared();
    private final GlobalObjectSystem system;
    private final GlobalObjectLookup.Shared shared;
    private final GlobalObjectLookup.Cell[] cells;

    public GlobalObjectLookup(GlobalObjectSystem _system) {
        this.system = _system;
        this.shared = _system instanceof SGlobalObjectSystem ? sharedServer : sharedClient;
        this.cells = this.shared.cells;
    }

    private GlobalObjectLookup.Cell getCellAt(int int1, int int3, boolean boolean0) {
        int int0 = int1 - metaGrid.minX * 300;
        int int2 = int3 - metaGrid.minY * 300;
        if (int0 >= 0 && int2 >= 0 && int0 < metaGrid.getWidth() * 300 && int2 < metaGrid.getHeight() * 300) {
            int int4 = int0 / 300;
            int int5 = int2 / 300;
            int int6 = int4 + int5 * metaGrid.getWidth();
            if (this.cells[int6] == null && boolean0) {
                this.cells[int6] = new GlobalObjectLookup.Cell(metaGrid.minX + int4, metaGrid.minY + int5);
            }

            return this.cells[int6];
        } else {
            DebugLog.log("ERROR: GlobalObjectLookup.getCellForObject object location invalid " + int1 + "," + int3);
            return null;
        }
    }

    private GlobalObjectLookup.Cell getCellForObject(GlobalObject globalObject, boolean boolean0) {
        return this.getCellAt(globalObject.x, globalObject.y, boolean0);
    }

    private GlobalObjectLookup.Chunk getChunkForChunkPos(int int1, int int0, boolean boolean0) {
        GlobalObjectLookup.Cell cell = this.getCellAt(int1 * 10, int0 * 10, boolean0);
        return cell == null ? null : cell.getChunkAt(int1 * 10, int0 * 10, boolean0);
    }

    public void addObject(GlobalObject object) {
        GlobalObjectLookup.Cell cell = this.getCellForObject(object, true);
        if (cell == null) {
            DebugLog.log("ERROR: GlobalObjectLookup.addObject object location invalid " + object.x + "," + object.y);
        } else {
            cell.addObject(object);
        }
    }

    public void removeObject(GlobalObject object) {
        GlobalObjectLookup.Cell cell = this.getCellForObject(object, false);
        if (cell == null) {
            DebugLog.log("ERROR: GlobalObjectLookup.removeObject object location invalid " + object.x + "," + object.y);
        } else {
            cell.removeObject(object);
        }
    }

    public GlobalObject getObjectAt(int x, int y, int z) {
        GlobalObjectLookup.Cell cell = this.getCellAt(x, y, false);
        if (cell == null) {
            return null;
        } else {
            GlobalObjectLookup.Chunk chunk = cell.getChunkAt(x, y, false);
            if (chunk == null) {
                return null;
            } else {
                for (int int0 = 0; int0 < chunk.objects.size(); int0++) {
                    GlobalObject globalObject = chunk.objects.get(int0);
                    if (globalObject.system == this.system && globalObject.x == x && globalObject.y == y && globalObject.z == z) {
                        return globalObject;
                    }
                }

                return null;
            }
        }
    }

    public boolean hasObjectsInChunk(int wx, int wy) {
        GlobalObjectLookup.Chunk chunk = this.getChunkForChunkPos(wx, wy, false);
        if (chunk == null) {
            return false;
        } else {
            for (int int0 = 0; int0 < chunk.objects.size(); int0++) {
                GlobalObject globalObject = chunk.objects.get(int0);
                if (globalObject.system == this.system) {
                    return true;
                }
            }

            return false;
        }
    }

    public ArrayList<GlobalObject> getObjectsInChunk(int wx, int wy, ArrayList<GlobalObject> objects) {
        GlobalObjectLookup.Chunk chunk = this.getChunkForChunkPos(wx, wy, false);
        if (chunk == null) {
            return objects;
        } else {
            for (int int0 = 0; int0 < chunk.objects.size(); int0++) {
                GlobalObject globalObject = chunk.objects.get(int0);
                if (globalObject.system == this.system) {
                    objects.add(globalObject);
                }
            }

            return objects;
        }
    }

    public ArrayList<GlobalObject> getObjectsAdjacentTo(int x, int y, int z, ArrayList<GlobalObject> objects) {
        for (int int0 = -1; int0 <= 1; int0++) {
            for (int int1 = -1; int1 <= 1; int1++) {
                GlobalObject globalObject = this.getObjectAt(x + int1, y + int0, z);
                if (globalObject != null && globalObject.system == this.system) {
                    objects.add(globalObject);
                }
            }
        }

        return objects;
    }

    public static void init(IsoMetaGrid _metaGrid) {
        metaGrid = _metaGrid;
        if (GameServer.bServer) {
            sharedServer.init(_metaGrid);
        } else if (GameClient.bClient) {
            sharedClient.init(_metaGrid);
        } else {
            sharedServer.init(_metaGrid);
            sharedClient.init(_metaGrid);
        }
    }

    public static void Reset() {
        sharedServer.reset();
        sharedClient.reset();
    }

    private static final class Cell {
        final int cx;
        final int cy;
        final GlobalObjectLookup.Chunk[] chunks = new GlobalObjectLookup.Chunk[900];

        Cell(int int0, int int1) {
            this.cx = int0;
            this.cy = int1;
        }

        GlobalObjectLookup.Chunk getChunkAt(int int1, int int3, boolean boolean0) {
            int int0 = (int1 - this.cx * 300) / 10;
            int int2 = (int3 - this.cy * 300) / 10;
            int int4 = int0 + int2 * 30;
            if (this.chunks[int4] == null && boolean0) {
                this.chunks[int4] = new GlobalObjectLookup.Chunk();
            }

            return this.chunks[int4];
        }

        GlobalObjectLookup.Chunk getChunkForObject(GlobalObject globalObject, boolean boolean0) {
            return this.getChunkAt(globalObject.x, globalObject.y, boolean0);
        }

        void addObject(GlobalObject globalObject) {
            GlobalObjectLookup.Chunk chunk = this.getChunkForObject(globalObject, true);
            if (chunk.objects.contains(globalObject)) {
                throw new IllegalStateException("duplicate object");
            } else {
                chunk.objects.add(globalObject);
            }
        }

        void removeObject(GlobalObject globalObject) {
            GlobalObjectLookup.Chunk chunk = this.getChunkForObject(globalObject, false);
            if (chunk != null && chunk.objects.contains(globalObject)) {
                chunk.objects.remove(globalObject);
            } else {
                throw new IllegalStateException("chunk doesn't contain object");
            }
        }

        void Reset() {
            for (int int0 = 0; int0 < this.chunks.length; int0++) {
                GlobalObjectLookup.Chunk chunk = this.chunks[int0];
                if (chunk != null) {
                    chunk.Reset();
                }
            }

            Arrays.fill(this.chunks, null);
        }
    }

    private static final class Chunk {
        final ArrayList<GlobalObject> objects = new ArrayList<>();

        void Reset() {
            this.objects.clear();
        }
    }

    private static final class Shared {
        GlobalObjectLookup.Cell[] cells;

        void init(IsoMetaGrid metaGrid) {
            this.cells = new GlobalObjectLookup.Cell[metaGrid.getWidth() * metaGrid.getHeight()];
        }

        void reset() {
            if (this.cells != null) {
                for (int int0 = 0; int0 < this.cells.length; int0++) {
                    GlobalObjectLookup.Cell cell = this.cells[int0];
                    if (cell != null) {
                        cell.Reset();
                    }
                }

                this.cells = null;
            }
        }
    }
}
