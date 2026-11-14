// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.CRC32;
import zombie.GameTime;
import zombie.ZomboidFileSystem;
import zombie.core.logger.LoggerManager;
import zombie.debug.DebugType;
import zombie.debug.LogSeverity;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.WorldReuserThread;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.areas.IsoRoom;

public class ServerChunkLoader {
    private final long debugSlowMapLoadingDelay = 0L;
    private boolean mapLoading;
    private final ServerChunkLoader.LoaderThread threadLoad = new ServerChunkLoader.LoaderThread();
    private final ServerChunkLoader.SaveChunkThread threadSave;
    private final ServerChunkLoader.RecalcAllThread threadRecalc;

    public ServerChunkLoader() {
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

    public void addJob(ServerMap.ServerCell cell) {
        this.mapLoading = DebugType.MapLoading.isEnabled();
        this.threadLoad.toThread.add(cell);
    }

    public void getLoaded(ArrayList<ServerMap.ServerCell> loaded) {
        this.threadLoad.fromThread.drainTo(loaded);
    }

    public void quit() {
        this.threadLoad.quit();

        while (this.threadLoad.isAlive()) {
            try {
                Thread.sleep(500L);
            } catch (InterruptedException var3) {
            }
        }

        this.threadSave.quit();

        while (this.threadSave.isAlive()) {
            try {
                Thread.sleep(500L);
            } catch (InterruptedException var2) {
            }
        }
    }

    private static void writeChunk(ClientChunkRequest.Chunk chunk) throws IOException {
        CRC32 crcSave = new CRC32();
        crcSave.update(chunk.bb.array(), 0, chunk.bb.position());
        if (ChunkChecksum.getChecksumIfExists(chunk.wx, chunk.wy) != crcSave.getValue()) {
            IsoChunk.SafeWrite(chunk.wx, chunk.wy, chunk.bb);
            ChunkChecksum.setChecksum(chunk.wx, chunk.wy, crcSave.getValue());
        }
    }

    private static ByteBuffer copyData(ClientChunkRequest.Chunk chunk) {
        return ByteBuffer.wrap(Arrays.copyOf(chunk.bb.array(), chunk.bb.position()));
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

    public void addRecalcJob(ServerMap.ServerCell cell) {
        this.threadRecalc.toThread.add(cell);
    }

    public void getRecalc(ArrayList<ServerMap.ServerCell> loaded) {
        this.threadRecalc.fromThread.drainTo(loaded);
    }

    private interface ChunkSaveTask extends ServerChunkLoader.SaveTask {
        ByteBuffer copyChunkData() throws IOException;
    }

    private class GetSquare implements IsoGridSquare.GetSquare {
        ServerMap.ServerCell cell;

        @Override
        public IsoGridSquare getGridSquare(int x, int y, int z) {
            x -= this.cell.wx * 64;
            y -= this.cell.wy * 64;
            if (x < 0 || x >= 64) {
                return null;
            } else if (y >= 0 && y < 64) {
                IsoChunk chunk = this.cell.chunks[x / 8][y / 8];
                return chunk == null ? null : chunk.getGridSquare(x % 8, y % 8, z);
            } else {
                return null;
            }
        }

        public boolean contains(int x, int y, int z) {
            return x >= 0 && x < 64 ? y >= 0 && y < 64 : false;
        }

        public IsoChunk getChunkForSquare(int x, int y) {
            x -= this.cell.wx * 64;
            y -= this.cell.wy * 64;
            if (x < 0 || x >= 64) {
                return null;
            } else {
                return y >= 0 && y < 64 ? this.cell.chunks[x / 8][y / 8] : null;
            }
        }

        public void EnsureSurroundNotNull(int x, int y, int z) {
            int minX = this.cell.wx * 64;
            int minY = this.cell.wy * 64;

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if ((dx != 0 || dy != 0) && this.contains(x + dx, y + dy, z)) {
                        IsoGridSquare sq2 = this.getGridSquare(minX + x + dx, minY + y + dy, z);
                        if (sq2 == null) {
                            sq2 = IsoGridSquare.getNew(IsoWorld.instance.currentCell, null, minX + x + dx, minY + y + dy, z);
                            int chx = (x + dx) / 8;
                            int chy = (y + dy) / 8;
                            int sqx = (x + dx) % 8;
                            int sqy = (y + dy) % 8;
                            if (this.cell.chunks[chx][chy] != null) {
                                this.cell.chunks[chx][chy].setSquare(sqx, sqy, z, sq2);
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
                    ServerMap.ServerCell cell = this.toThread.take();
                    if (this.isoGridSquareCache.size() < 10000) {
                        IsoGridSquare.getSquaresForThread(this.isoGridSquareCache, 10000);
                        IsoGridSquare.loadGridSquareCache = this.isoGridSquareCache;
                    }

                    if (cell.wx == -1 && cell.wy == -1) {
                        return;
                    }

                    if (cell.cancelLoading) {
                        if (ServerChunkLoader.this.mapLoading) {
                            DebugType.MapLoading.debugln("LoaderThread: cancelled " + cell.wx + "," + cell.wy);
                        }

                        cell.loadingWasCancelled = true;
                    } else {
                        long start = System.nanoTime();

                        for (int x = 0; x < 8; x++) {
                            for (int y = 0; y < 8; y++) {
                                int wx = cell.wx * 8 + x;
                                int wy = cell.wy * 8 + y;
                                if (IsoWorld.instance.metaGrid.isValidChunk(wx, wy)) {
                                    IsoChunk chunk = IsoChunkMap.chunkStore.poll();
                                    if (chunk == null) {
                                        chunk = new IsoChunk((IsoCell)null);
                                    }

                                    chunk.assignLoadID();
                                    chunk.loaded = chunk.LoadChunk(wx, wy, ServerChunkLoader.this.threadSave.getPendingChunkData(wx, wy));
                                    if (chunk.loaded) {
                                        cell.chunks[x][y] = chunk;
                                    }
                                }
                            }
                        }

                        if (GameServer.debug) {
                        }

                        float time = (float)(System.nanoTime() - start) / 1000000.0F;
                        this.fromThread.add(cell);
                    }
                } catch (Throwable e) {
                    DebugType.General.printException(e, LogSeverity.Error);
                    LoggerManager.getLogger("map").write(e);
                }
            }
        }

        public void quit() {
            ServerMap.ServerCell quitCell = new ServerMap.ServerCell();
            quitCell.wx = -1;
            quitCell.wy = -1;
            this.toThread.add(quitCell);
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
                } catch (Throwable e) {
                    DebugType.General.printException(e, LogSeverity.Error);
                }
            }
        }

        private void runInner() throws InterruptedException {
            ServerMap.ServerCell cell = this.toThread.take();
            if (cell.cancelLoading && !this.hasAnyBrandNewChunks(cell)) {
                for (int y = 0; y < 8; y++) {
                    for (int x = 0; x < 8; x++) {
                        IsoChunk chunk = cell.chunks[x][y];
                        if (chunk != null) {
                            cell.chunks[x][y] = null;
                            WorldReuserThread.instance.addReuseChunk(chunk);
                        }
                    }
                }

                if (ServerChunkLoader.this.mapLoading) {
                    DebugType.MapLoading.debugln("RecalcAllThread: cancelled " + cell.wx + "," + cell.wy);
                }

                cell.loadingWasCancelled = true;
            } else {
                long start = System.nanoTime();
                this.serverCellGetSquare.cell = cell;
                int sx = cell.wx * 64;
                int sy = cell.wy * 64;
                int ex = sx + 64;
                int ey = sy + 64;
                int maxZ = 0;
                int nSquares = 64;

                for (int cx = 0; cx < 8; cx++) {
                    for (int cy = 0; cy < 8; cy++) {
                        IsoChunk chunk = cell.chunks[cx][cy];
                        if (chunk != null) {
                            chunk.loaded = false;

                            for (int i = 0; i < 64; i++) {
                                for (int z = chunk.minLevel; z <= chunk.maxLevel; z++) {
                                    int zz = chunk.squaresIndexOfLevel(z);
                                    IsoGridSquare g = chunk.squares[zz][i];
                                    if (z == 0 && g == null) {
                                        int x = chunk.wx * 8 + i % 8;
                                        int y = chunk.wy * 8 + i / 8;
                                        g = IsoGridSquare.getNew(IsoWorld.instance.currentCell, null, x, y, z);
                                        chunk.setSquare(x % 8, y % 8, z, g);
                                    }

                                    if (g != null) {
                                        g.RecalcProperties();
                                        IsoRoom room = g.getRoom();
                                        if (room != null) {
                                            room.addSquare(g);
                                        }
                                    }
                                }
                            }

                            if (chunk.maxLevel > maxZ) {
                                maxZ = chunk.maxLevel;
                            }
                        }
                    }
                }

                for (int cx = 0; cx < 8; cx++) {
                    for (int cy = 0; cy < 8; cy++) {
                        IsoChunk chunk = cell.chunks[cx][cy];
                        if (chunk != null) {
                            for (int i = 0; i < 64; i++) {
                                for (int z = chunk.minLevel; z <= chunk.maxLevel; z++) {
                                    int squaresIndexOfLevel = chunk.squaresIndexOfLevel(z);
                                    IsoGridSquare g = chunk.squares[squaresIndexOfLevel][i];
                                    if (g != null) {
                                        if (z != 0 && !g.getObjects().isEmpty()) {
                                            this.serverCellGetSquare.EnsureSurroundNotNull(g.x - sx, g.y - sy, z);
                                        }

                                        g.RecalcAllWithNeighbours(false, this.serverCellGetSquare);
                                    }
                                }
                            }
                        }
                    }
                }

                for (int cx = 0; cx < 8; cx++) {
                    for (int cy = 0; cy < 8; cy++) {
                        IsoChunk chunk = cell.chunks[cx][cy];
                        if (chunk != null) {
                            for (int i = 0; i < 64; i++) {
                                for (int z = chunk.maxLevel; z > chunk.minLevel; z--) {
                                    int squaresIndexOfLevel = chunk.squaresIndexOfLevel(z);
                                    IsoGridSquare sq = chunk.squares[squaresIndexOfLevel][i];
                                    if (sq != null && sq.hasRainBlockingTile()) {
                                        z--;

                                        for (; z >= chunk.minLevel; z--) {
                                            squaresIndexOfLevel = chunk.squaresIndexOfLevel(z);
                                            sq = chunk.squares[squaresIndexOfLevel][i];
                                            if (sq != null) {
                                                sq.haveRoof = true;
                                                sq.getProperties().unset(IsoFlagType.exterior);
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                if (GameServer.debug) {
                }

                float time = (float)(System.nanoTime() - start) / 1000000.0F;
                if (ServerChunkLoader.this.mapLoading) {
                    DebugType.MapLoading.debugln("RecalcAll for cell " + cell.wx + "," + cell.wy + " ms=" + time);
                }

                this.fromThread.add(cell);
            }
        }

        private boolean hasAnyBrandNewChunks(ServerMap.ServerCell cell) {
            for (int y = 0; y < 8; y++) {
                for (int x = 0; x < 8; x++) {
                    IsoChunk chunk = cell.chunks[x][y];
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
        private final Object taskLock = new Object();
        private ServerChunkLoader.SaveTask currentTask;
        private boolean quit;
        private final ClientChunkRequest ccr = new ClientChunkRequest();
        private final ArrayList<ServerChunkLoader.SaveTask> savedChunks = new ArrayList<>();

        @Override
        public void run() {
            do {
                ServerChunkLoader.SaveTask task = null;

                try {
                    task = this.takeTask();
                    task.save();
                    this.fromThread.add(task);
                } catch (InterruptedException var7) {
                } catch (Throwable e) {
                    DebugType.General.printException(e, LogSeverity.Error);
                    if (task != null) {
                        LoggerManager.getLogger("map").write("Error saving chunk " + task.wx() + "," + task.wy());
                    }

                    LoggerManager.getLogger("map").write(e);
                } finally {
                    this.finishTask();
                }
            } while (!this.quit || !this.toThread.isEmpty());
        }

        public void addUnloadedJob(IsoChunk chunk) {
            this.addTask(ServerChunkLoader.this.new SaveUnloadedTask(this.ccr, chunk));
        }

        public void addLoadedJob(IsoChunk chunk) {
            ClientChunkRequest.Chunk reqChunk = this.ccr.getChunk();
            reqChunk.wx = chunk.wx;
            reqChunk.wy = chunk.wy;
            this.ccr.getByteBuffer(reqChunk);

            try {
                CRC32 crc32 = new CRC32();
                chunk.SaveLoadedChunk(reqChunk, crc32);
            } catch (Exception ex) {
                DebugType.General.printException(ex, LogSeverity.Error);
                LoggerManager.getLogger("map").write(ex);
                this.ccr.releaseChunk(reqChunk);
                return;
            }

            this.addTask(ServerChunkLoader.this.new SaveLoadedTask(this.ccr, reqChunk));
        }

        public void saveLater(GameTime gameTime) {
            this.addTask(ServerChunkLoader.this.new SaveGameTimeTask(gameTime));
        }

        public ByteBuffer getPendingChunkData(int wx, int wy) {
            ServerChunkLoader.ChunkSaveTask pendingTask = this.findPendingChunkTask(wx, wy);
            if (pendingTask == null) {
                return null;
            }

            try {
                return pendingTask.copyChunkData();
            } catch (Exception e) {
                DebugType.General.printException(e, LogSeverity.Error);
                LoggerManager.getLogger("map").write("Error copying pending save of chunk " + wx + "," + wy);
                LoggerManager.getLogger("map").write(e);
                return null;
            }
        }

        public void quit() {
            this.addTask(ServerChunkLoader.this.new QuitThreadTask());
        }

        private void addTask(ServerChunkLoader.SaveTask task) {
            synchronized (this.taskLock) {
                this.toThread.add(task);
                this.taskLock.notifyAll();
            }
        }

        private ServerChunkLoader.SaveTask takeTask() throws InterruptedException {
            synchronized (this.taskLock) {
                while (this.toThread.isEmpty()) {
                    this.taskLock.wait();
                }

                this.currentTask = this.toThread.poll();
                return this.currentTask;
            }
        }

        private void finishTask() {
            synchronized (this.taskLock) {
                this.currentTask = null;
            }
        }

        private ServerChunkLoader.ChunkSaveTask findPendingChunkTask(int wx, int wy) {
            synchronized (this.taskLock) {
                ServerChunkLoader.ChunkSaveTask pendingTask = null;
                if (this.currentTask instanceof ServerChunkLoader.ChunkSaveTask chunkTask && chunkTask.wx() == wx && chunkTask.wy() == wy) {
                    pendingTask = chunkTask;
                }

                for (ServerChunkLoader.SaveTask task : this.toThread) {
                    if (task instanceof ServerChunkLoader.ChunkSaveTask chunkTask && chunkTask.wx() == wx && chunkTask.wy() == wy) {
                        pendingTask = chunkTask;
                    }
                }

                return pendingTask;
            }
        }

        public void update() {
            this.savedChunks.clear();
            this.fromThread.drainTo(this.savedChunks);

            for (int i = 0; i < this.savedChunks.size(); i++) {
                this.savedChunks.get(i).release();
            }

            this.savedChunks.clear();
        }
    }

    private class SaveGameTimeTask implements ServerChunkLoader.SaveTask {
        private byte[] bytes;

        public SaveGameTimeTask(final GameTime gameTime) {
            try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream(32768);
                DataOutputStream dos = new DataOutputStream(baos);
            ) {
                gameTime.save(dos);
                dos.close();
                this.bytes = baos.toByteArray();
            } catch (Exception ex) {
                DebugType.General.printException(ex, LogSeverity.Error);
            }
        }

        @Override
        public void save() throws Exception {
            if (this.bytes != null) {
                File outFile = ZomboidFileSystem.instance.getFileInCurrentSave("map_t.bin");

                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    fos.write(this.bytes);
                } catch (Exception e) {
                    DebugType.General.printException(e, LogSeverity.Error);
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

    private class SaveLoadedTask implements ServerChunkLoader.ChunkSaveTask {
        private final ClientChunkRequest ccr;
        private final ClientChunkRequest.Chunk chunk;
        private boolean isReleased;

        public SaveLoadedTask(final ClientChunkRequest ccr, final ClientChunkRequest.Chunk chunk) {
            this.ccr = ccr;
            this.chunk = chunk;
        }

        @Override
        public void save() throws Exception {
            synchronized (IsoChunk.WriteLock) {
                ServerChunkLoader.writeChunk(this.chunk);
            }
        }

        @Override
        public synchronized ByteBuffer copyChunkData() {
            return this.isReleased ? null : ServerChunkLoader.copyData(this.chunk);
        }

        @Override
        public synchronized void release() {
            this.isReleased = true;
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

    private class SaveUnloadedTask implements ServerChunkLoader.ChunkSaveTask {
        private final ClientChunkRequest ccr;
        private final IsoChunk chunk;
        private ClientChunkRequest.Chunk chunkData;
        private boolean isReleased;

        public SaveUnloadedTask(final ClientChunkRequest ccr, final IsoChunk chunk) {
            this.ccr = ccr;
            this.chunk = chunk;
        }

        @Override
        public void save() throws Exception {
            ClientChunkRequest.Chunk data = this.serialize();
            synchronized (IsoChunk.WriteLock) {
                ServerChunkLoader.writeChunk(data);
            }
        }

        @Override
        public synchronized ByteBuffer copyChunkData() throws IOException {
            return this.isReleased ? null : ServerChunkLoader.copyData(this.serialize());
        }

        @Override
        public synchronized void release() {
            this.isReleased = true;
            if (this.chunkData != null) {
                this.ccr.releaseChunk(this.chunkData);
                this.chunkData = null;
            }

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

        private synchronized ClientChunkRequest.Chunk serialize() throws IOException {
            if (this.chunkData != null) {
                return this.chunkData;
            }

            ClientChunkRequest.Chunk data = this.ccr.getChunk();
            data.wx = this.chunk.wx;
            data.wy = this.chunk.wy;
            this.ccr.getByteBuffer(data);

            try {
                this.chunk.SaveLoadedChunk(data, new CRC32());
            } catch (IOException | RuntimeException e) {
                this.ccr.releaseChunk(data);
                throw e;
            }

            this.chunkData = data;
            return this.chunkData;
        }
    }
}
