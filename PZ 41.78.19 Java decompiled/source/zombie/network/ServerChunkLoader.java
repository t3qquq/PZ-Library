// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.CRC32;
import zombie.GameTime;
import zombie.ZomboidFileSystem;
import zombie.core.logger.LoggerManager;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.WorldReuserThread;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;

public class ServerChunkLoader {
    private long debugSlowMapLoadingDelay = 0L;
    private boolean MapLoading = false;
    private ServerChunkLoader.LoaderThread threadLoad;
    private ServerChunkLoader.SaveChunkThread threadSave;
    private final CRC32 crcSave = new CRC32();
    private ServerChunkLoader.RecalcAllThread threadRecalc;

    public ServerChunkLoader() {
        this.threadLoad = new ServerChunkLoader.LoaderThread();
        this.threadLoad.setName("LoadChunk");
        this.threadLoad.setDaemon(true);
        this.threadLoad.start();
        this.threadRecalc = new ServerChunkLoader.RecalcAllThread();
        this.threadRecalc.setName("RecalcAll");
        this.threadRecalc.setDaemon(true);
        this.threadRecalc.setPriority(10);
        this.threadRecalc.start();
        this.threadSave = new ServerChunkLoader.SaveChunkThread();
        this.threadSave.setName("SaveChunk");
        this.threadSave.setDaemon(true);
        this.threadSave.start();
    }

    public void addJob(ServerMap.ServerCell serverCell) {
        this.MapLoading = DebugType.Do(DebugType.MapLoading);
        this.threadLoad.toThread.add(serverCell);
        MPStatistic.getInstance().LoaderThreadTasks.Added();
    }

    public void getLoaded(ArrayList<ServerMap.ServerCell> arrayList) {
        this.threadLoad.fromThread.drainTo(arrayList);
    }

    public void quit() {
        this.threadLoad.quit();

        while (this.threadLoad.isAlive()) {
            try {
                Thread.sleep(500L);
            } catch (InterruptedException interruptedException0) {
            }
        }

        this.threadSave.quit();

        while (this.threadSave.isAlive()) {
            try {
                Thread.sleep(500L);
            } catch (InterruptedException interruptedException1) {
            }
        }
    }

    public void addSaveUnloadedJob(IsoChunk chunk) {
        this.threadSave.addUnloadedJob(chunk);
    }

    public void addSaveLoadedJob(IsoChunk chunk) {
        this.threadSave.addLoadedJob(chunk);
    }

    public void saveLater(GameTime gameTime) {
        this.threadSave.saveLater(gameTime);
    }

    public void updateSaved() {
        this.threadSave.update();
    }

    public void addRecalcJob(ServerMap.ServerCell serverCell) {
        this.threadRecalc.toThread.add(serverCell);
        MPStatistic.getInstance().RecalcThreadTasks.Added();
    }

    public void getRecalc(ArrayList<ServerMap.ServerCell> arrayList) {
        MPStatistic.getInstance().ServerMapLoaded2.Added(this.threadRecalc.fromThread.size());
        this.threadRecalc.fromThread.drainTo(arrayList);
        MPStatistic.getInstance().RecalcThreadTasks.Processed();
    }

    private class GetSquare implements IsoGridSquare.GetSquare {
        ServerMap.ServerCell cell;

        @Override
        public IsoGridSquare getGridSquare(int int0, int int1, int int2) {
            int0 -= this.cell.WX * 50;
            int1 -= this.cell.WY * 50;
            if (int0 < 0 || int0 >= 50) {
                return null;
            } else if (int1 >= 0 && int1 < 50) {
                IsoChunk chunk = this.cell.chunks[int0 / 10][int1 / 10];
                return chunk == null ? null : chunk.getGridSquare(int0 % 10, int1 % 10, int2);
            } else {
                return null;
            }
        }

        public boolean contains(int int1, int int0, int var3) {
            return int1 < 0 || int1 >= 50 ? false : int0 >= 0 && int0 < 50;
        }

        public IsoChunk getChunkForSquare(int int0, int int1) {
            int0 -= this.cell.WX * 50;
            int1 -= this.cell.WY * 50;
            if (int0 < 0 || int0 >= 50) {
                return null;
            } else {
                return int1 >= 0 && int1 < 50 ? this.cell.chunks[int0 / 10][int1 / 10] : null;
            }
        }

        public void EnsureSurroundNotNull(int int6, int int5, int int4) {
            int int0 = this.cell.WX * 50;
            int int1 = this.cell.WY * 50;

            for (int int2 = -1; int2 <= 1; int2++) {
                for (int int3 = -1; int3 <= 1; int3++) {
                    if ((int2 != 0 || int3 != 0) && this.contains(int6 + int2, int5 + int3, int4)) {
                        IsoGridSquare square = this.getGridSquare(int0 + int6 + int2, int1 + int5 + int3, int4);
                        if (square == null) {
                            square = IsoGridSquare.getNew(IsoWorld.instance.CurrentCell, null, int0 + int6 + int2, int1 + int5 + int3, int4);
                            int int7 = (int6 + int2) / 10;
                            int int8 = (int5 + int3) / 10;
                            int int9 = (int6 + int2) % 10;
                            int int10 = (int5 + int3) % 10;
                            if (this.cell.chunks[int7][int8] != null) {
                                this.cell.chunks[int7][int8].setSquare(int9, int10, int4, square);
                            }
                        }
                    }
                }
            }
        }
    }

    private class LoaderThread extends Thread {
        private final LinkedBlockingQueue<ServerMap.ServerCell> toThread = new LinkedBlockingQueue<>();
        private final LinkedBlockingQueue<ServerMap.ServerCell> fromThread = new LinkedBlockingQueue<>();
        ArrayDeque<IsoGridSquare> isoGridSquareCache = new ArrayDeque<>();

        @Override
        public void run() {
            while (true) {
                try {
                    MPStatistic.getInstance().LoaderThread.End();
                    ServerMap.ServerCell serverCell = this.toThread.take();
                    MPStatistic.getInstance().LoaderThread.Start();
                    if (this.isoGridSquareCache.size() < 10000) {
                        IsoGridSquare.getSquaresForThread(this.isoGridSquareCache, 10000);
                        IsoGridSquare.loadGridSquareCache = this.isoGridSquareCache;
                    }

                    if (serverCell.WX == -1 && serverCell.WY == -1) {
                        return;
                    }

                    if (serverCell.bCancelLoading) {
                        if (ServerChunkLoader.this.MapLoading) {
                            DebugLog.log(DebugType.MapLoading, "LoaderThread: cancelled " + serverCell.WX + "," + serverCell.WY);
                        }

                        serverCell.bLoadingWasCancelled = true;
                    } else {
                        long long0 = System.nanoTime();

                        for (int int0 = 0; int0 < 5; int0++) {
                            for (int int1 = 0; int1 < 5; int1++) {
                                int int2 = serverCell.WX * 5 + int0;
                                int int3 = serverCell.WY * 5 + int1;
                                if (IsoWorld.instance.MetaGrid.isValidChunk(int2, int3)) {
                                    IsoChunk chunk = IsoChunkMap.chunkStore.poll();
                                    if (chunk == null) {
                                        chunk = new IsoChunk(null);
                                    } else {
                                        MPStatistics.decreaseStoredChunk();
                                    }

                                    ServerChunkLoader.this.threadSave.saveNow(int2, int3);

                                    try {
                                        if (chunk.LoadOrCreate(int2, int3, null)) {
                                            chunk.bLoaded = true;
                                        } else {
                                            ChunkChecksum.setChecksum(int2, int3, 0L);
                                            chunk.Blam(int2, int3);
                                            if (chunk.LoadBrandNew(int2, int3)) {
                                                chunk.bLoaded = true;
                                            }
                                        }
                                    } catch (Exception exception0) {
                                        exception0.printStackTrace();
                                        LoggerManager.getLogger("map").write(exception0);
                                    }

                                    if (chunk.bLoaded) {
                                        serverCell.chunks[int0][int1] = chunk;
                                    }
                                }
                            }
                        }

                        if (GameServer.bDebug && ServerChunkLoader.this.debugSlowMapLoadingDelay > 0L) {
                            Thread.sleep(ServerChunkLoader.this.debugSlowMapLoadingDelay);
                        }

                        float float0 = (float)(System.nanoTime() - long0) / 1000000.0F;
                        MPStatistic.getInstance().IncrementLoadCellFromDisk();
                        this.fromThread.add(serverCell);
                        MPStatistic.getInstance().LoaderThreadTasks.Processed();
                    }
                } catch (Exception exception1) {
                    exception1.printStackTrace();
                    LoggerManager.getLogger("map").write(exception1);
                }
            }
        }

        public void quit() {
            ServerMap.ServerCell serverCell = new ServerMap.ServerCell();
            serverCell.WX = -1;
            serverCell.WY = -1;
            this.toThread.add(serverCell);
            MPStatistic.getInstance().LoaderThreadTasks.Added();
        }
    }

    private class QuitThreadTask implements ServerChunkLoader.SaveTask {
        @Override
        public void save() throws Exception {
            ServerChunkLoader.this.threadSave.quit = true;
        }

        @Override
        public void release() {
        }

        @Override
        public int wx() {
            return 0;
        }

        @Override
        public int wy() {
            return 0;
        }
    }

    private class RecalcAllThread extends Thread {
        private final LinkedBlockingQueue<ServerMap.ServerCell> toThread = new LinkedBlockingQueue<>();
        private final LinkedBlockingQueue<ServerMap.ServerCell> fromThread = new LinkedBlockingQueue<>();
        private final ServerChunkLoader.GetSquare serverCellGetSquare = ServerChunkLoader.this.new GetSquare();

        @Override
        public void run() {
            while (true) {
                try {
                    this.runInner();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

        private void runInner() throws InterruptedException {
            MPStatistic.getInstance().RecalcAllThread.End();
            ServerMap.ServerCell serverCell = this.toThread.take();
            MPStatistic.getInstance().RecalcAllThread.Start();
            if (serverCell.bCancelLoading && !this.hasAnyBrandNewChunks(serverCell)) {
                for (int int0 = 0; int0 < 5; int0++) {
                    for (int int1 = 0; int1 < 5; int1++) {
                        IsoChunk chunk0 = serverCell.chunks[int1][int0];
                        if (chunk0 != null) {
                            serverCell.chunks[int1][int0] = null;
                            WorldReuserThread.instance.addReuseChunk(chunk0);
                        }
                    }
                }

                if (ServerChunkLoader.this.MapLoading) {
                    DebugLog.log(DebugType.MapLoading, "RecalcAllThread: cancelled " + serverCell.WX + "," + serverCell.WY);
                }

                serverCell.bLoadingWasCancelled = true;
            } else {
                long long0 = System.nanoTime();
                this.serverCellGetSquare.cell = serverCell;
                int int2 = serverCell.WX * 50;
                int int3 = serverCell.WY * 50;
                int int4 = int2 + 50;
                int int5 = int3 + 50;
                int int6 = 0;
                byte byte0 = 100;

                for (int int7 = 0; int7 < 5; int7++) {
                    for (int int8 = 0; int8 < 5; int8++) {
                        IsoChunk chunk1 = serverCell.chunks[int7][int8];
                        if (chunk1 != null) {
                            chunk1.bLoaded = false;

                            for (int int9 = 0; int9 < byte0; int9++) {
                                for (int int10 = 0; int10 <= chunk1.maxLevel; int10++) {
                                    IsoGridSquare square0 = chunk1.squares[int10][int9];
                                    if (int10 == 0) {
                                        if (square0 == null) {
                                            int int11 = chunk1.wx * 10 + int9 % 10;
                                            int int12 = chunk1.wy * 10 + int9 / 10;
                                            square0 = IsoGridSquare.getNew(IsoWorld.instance.CurrentCell, null, int11, int12, int10);
                                            chunk1.setSquare(int11 % 10, int12 % 10, int10, square0);
                                        }

                                        if (square0.getFloor() == null) {
                                            DebugLog.log("ERROR: added floor at " + square0.x + "," + square0.y + "," + square0.z + " because there wasn't one");
                                            IsoObject object = IsoObject.getNew();
                                            object.sprite = IsoSprite.getSprite(IsoSpriteManager.instance, "carpentry_02_58", 0);
                                            object.square = square0;
                                            square0.getObjects().add(0, object);
                                        }
                                    }

                                    if (square0 != null) {
                                        square0.RecalcProperties();
                                    }
                                }
                            }

                            if (chunk1.maxLevel > int6) {
                                int6 = chunk1.maxLevel;
                            }
                        }
                    }
                }

                for (int int13 = 0; int13 < 5; int13++) {
                    for (int int14 = 0; int14 < 5; int14++) {
                        IsoChunk chunk2 = serverCell.chunks[int13][int14];
                        if (chunk2 != null) {
                            for (int int15 = 0; int15 < byte0; int15++) {
                                for (int int16 = 0; int16 <= chunk2.maxLevel; int16++) {
                                    IsoGridSquare square1 = chunk2.squares[int16][int15];
                                    if (square1 != null) {
                                        if (int16 > 0 && !square1.getObjects().isEmpty()) {
                                            this.serverCellGetSquare.EnsureSurroundNotNull(square1.x - int2, square1.y - int3, int16);
                                        }

                                        square1.RecalcAllWithNeighbours(true, this.serverCellGetSquare);
                                    }
                                }
                            }
                        }
                    }
                }

                for (int int17 = 0; int17 < 5; int17++) {
                    for (int int18 = 0; int18 < 5; int18++) {
                        IsoChunk chunk3 = serverCell.chunks[int17][int18];
                        if (chunk3 != null) {
                            for (int int19 = 0; int19 < byte0; int19++) {
                                for (int int20 = chunk3.maxLevel; int20 > 0; int20--) {
                                    IsoGridSquare square2 = chunk3.squares[int20][int19];
                                    if (square2 != null && square2.Is(IsoFlagType.solidfloor)) {
                                        int20--;

                                        for (; int20 >= 0; int20--) {
                                            square2 = chunk3.squares[int20][int19];
                                            if (square2 != null) {
                                                square2.haveRoof = true;
                                                square2.getProperties().UnSet(IsoFlagType.exterior);
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                if (GameServer.bDebug && ServerChunkLoader.this.debugSlowMapLoadingDelay > 0L) {
                    Thread.sleep(ServerChunkLoader.this.debugSlowMapLoadingDelay);
                }

                float float0 = (float)(System.nanoTime() - long0) / 1000000.0F;
                if (ServerChunkLoader.this.MapLoading) {
                    DebugLog.log(DebugType.MapLoading, "RecalcAll for cell " + serverCell.WX + "," + serverCell.WY + " ms=" + float0);
                }

                this.fromThread.add(serverCell);
            }
        }

        private boolean hasAnyBrandNewChunks(ServerMap.ServerCell serverCell) {
            for (int int0 = 0; int0 < 5; int0++) {
                for (int int1 = 0; int1 < 5; int1++) {
                    IsoChunk chunk = serverCell.chunks[int1][int0];
                    if (chunk != null && !chunk.getErosionData().init) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    private class SaveChunkThread extends Thread {
        private final LinkedBlockingQueue<ServerChunkLoader.SaveTask> toThread = new LinkedBlockingQueue<>();
        private final LinkedBlockingQueue<ServerChunkLoader.SaveTask> fromThread = new LinkedBlockingQueue<>();
        private boolean quit = false;
        private final CRC32 crc32 = new CRC32();
        private final ClientChunkRequest ccr = new ClientChunkRequest();
        private final ArrayList<ServerChunkLoader.SaveTask> toSaveChunk = new ArrayList<>();
        private final ArrayList<ServerChunkLoader.SaveTask> savedChunks = new ArrayList<>();

        @Override
        public void run() {
            do {
                ServerChunkLoader.SaveTask saveTask = null;

                try {
                    MPStatistic.getInstance().SaveThread.End();
                    saveTask = this.toThread.take();
                    MPStatistic.getInstance().SaveThread.Start();
                    MPStatistic.getInstance().IncrementSaveCellToDisk();
                    saveTask.save();
                    this.fromThread.add(saveTask);
                    MPStatistic.getInstance().SaveTasks.Processed();
                } catch (InterruptedException interruptedException) {
                } catch (Exception exception) {
                    exception.printStackTrace();
                    if (saveTask != null) {
                        LoggerManager.getLogger("map").write("Error saving chunk " + saveTask.wx() + "," + saveTask.wy());
                    }

                    LoggerManager.getLogger("map").write(exception);
                }
            } while (!this.quit || !this.toThread.isEmpty());
        }

        public void addUnloadedJob(IsoChunk chunk) {
            this.toThread.add(ServerChunkLoader.this.new SaveUnloadedTask(chunk));
            MPStatistic.getInstance().SaveTasks.SaveUnloadedTasksAdded();
        }

        public void addLoadedJob(IsoChunk chunk1) {
            ClientChunkRequest.Chunk chunk0 = this.ccr.getChunk();
            chunk0.wx = chunk1.wx;
            chunk0.wy = chunk1.wy;
            this.ccr.getByteBuffer(chunk0);

            try {
                chunk1.SaveLoadedChunk(chunk0, this.crc32);
            } catch (Exception exception) {
                exception.printStackTrace();
                LoggerManager.getLogger("map").write(exception);
                this.ccr.releaseChunk(chunk0);
                return;
            }

            this.toThread.add(ServerChunkLoader.this.new SaveLoadedTask(this.ccr, chunk0));
            MPStatistic.getInstance().SaveTasks.SaveLoadedTasksAdded();
        }

        public void saveLater(GameTime gameTime) {
            this.toThread.add(ServerChunkLoader.this.new SaveGameTimeTask(gameTime));
            MPStatistic.getInstance().SaveTasks.SaveGameTimeTasksAdded();
        }

        public void saveNow(int int2, int int1) {
            this.toSaveChunk.clear();
            this.toThread.drainTo(this.toSaveChunk);

            for (int int0 = 0; int0 < this.toSaveChunk.size(); int0++) {
                ServerChunkLoader.SaveTask saveTask = this.toSaveChunk.get(int0);
                if (saveTask.wx() == int2 && saveTask.wy() == int1) {
                    try {
                        this.toSaveChunk.remove(int0--);
                        saveTask.save();
                        MPStatistic.getInstance().IncrementServerChunkThreadSaveNow();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        LoggerManager.getLogger("map").write("Error saving chunk " + int2 + "," + int1);
                        LoggerManager.getLogger("map").write(exception);
                    }

                    MPStatistic.getInstance().SaveTasks.Processed();
                    this.fromThread.add(saveTask);
                }
            }

            this.toThread.addAll(this.toSaveChunk);
        }

        public void quit() {
            this.toThread.add(ServerChunkLoader.this.new QuitThreadTask());
            MPStatistic.getInstance().SaveTasks.QuitThreadTasksAdded();
        }

        public void update() {
            this.savedChunks.clear();
            this.fromThread.drainTo(this.savedChunks);

            for (int int0 = 0; int0 < this.savedChunks.size(); int0++) {
                this.savedChunks.get(int0).release();
            }

            this.savedChunks.clear();
        }
    }

    private class SaveGameTimeTask implements ServerChunkLoader.SaveTask {
        private byte[] bytes;

        public SaveGameTimeTask(GameTime gameTime) {
            try {
                try (
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(32768);
                    DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
                ) {
                    gameTime.save(dataOutputStream);
                    dataOutputStream.close();
                    this.bytes = byteArrayOutputStream.toByteArray();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        @Override
        public void save() throws Exception {
            if (this.bytes != null) {
                File file = ZomboidFileSystem.instance.getFileInCurrentSave("map_t.bin");

                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    fileOutputStream.write(this.bytes);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    return;
                }
            }
        }

        @Override
        public void release() {
        }

        @Override
        public int wx() {
            return 0;
        }

        @Override
        public int wy() {
            return 0;
        }
    }

    private class SaveLoadedTask implements ServerChunkLoader.SaveTask {
        private final ClientChunkRequest ccr;
        private final ClientChunkRequest.Chunk chunk;

        public SaveLoadedTask(ClientChunkRequest clientChunkRequest, ClientChunkRequest.Chunk chunkx) {
            this.ccr = clientChunkRequest;
            this.chunk = chunkx;
        }

        @Override
        public void save() throws Exception {
            long long0 = ChunkChecksum.getChecksumIfExists(this.chunk.wx, this.chunk.wy);
            ServerChunkLoader.this.crcSave.reset();
            ServerChunkLoader.this.crcSave.update(this.chunk.bb.array(), 0, this.chunk.bb.position());
            if (long0 != ServerChunkLoader.this.crcSave.getValue()) {
                ChunkChecksum.setChecksum(this.chunk.wx, this.chunk.wy, ServerChunkLoader.this.crcSave.getValue());
                IsoChunk.SafeWrite("map_", this.chunk.wx, this.chunk.wy, this.chunk.bb);
            }
        }

        @Override
        public void release() {
            this.ccr.releaseChunk(this.chunk);
        }

        @Override
        public int wx() {
            return this.chunk.wx;
        }

        @Override
        public int wy() {
            return this.chunk.wy;
        }
    }

    private interface SaveTask {
        void save() throws Exception;

        void release();

        int wx();

        int wy();
    }

    private class SaveUnloadedTask implements ServerChunkLoader.SaveTask {
        private final IsoChunk chunk;

        public SaveUnloadedTask(IsoChunk chunkx) {
            this.chunk = chunkx;
        }

        @Override
        public void save() throws Exception {
            this.chunk.Save(false);
        }

        @Override
        public void release() {
            WorldReuserThread.instance.addReuseChunk(this.chunk);
        }

        @Override
        public int wx() {
            return this.chunk.wx;
        }

        @Override
        public int wy() {
            return this.chunk.wy;
        }
    }
}
