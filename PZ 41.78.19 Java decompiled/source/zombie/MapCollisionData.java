// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.gameStates.IngameState;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLot;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaChunk;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.MPStatistic;
import zombie.popman.ZombiePopulationManager;

public final class MapCollisionData {
    public static final MapCollisionData instance = new MapCollisionData();
    public static final byte BIT_SOLID = 1;
    public static final byte BIT_WALLN = 2;
    public static final byte BIT_WALLW = 4;
    public static final byte BIT_WATER = 8;
    public static final byte BIT_ROOM = 16;
    private static final int SQUARES_PER_CHUNK = 10;
    private static final int CHUNKS_PER_CELL = 30;
    private static final int SQUARES_PER_CELL = 300;
    private static int[] curXY = new int[2];
    public final Object renderLock = new Object();
    private final Stack<MapCollisionData.PathTask> freePathTasks = new Stack<>();
    private final ConcurrentLinkedQueue<MapCollisionData.PathTask> pathTaskQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<MapCollisionData.PathTask> pathResultQueue = new ConcurrentLinkedQueue<>();
    private final MapCollisionData.Sync sync = new MapCollisionData.Sync();
    private final byte[] squares = new byte[100];
    private final int SQUARE_UPDATE_SIZE = 9;
    private final ByteBuffer squareUpdateBuffer = ByteBuffer.allocateDirect(1024);
    private boolean bClient;
    private boolean bPaused;
    private boolean bNoSave;
    private MapCollisionData.MCDThread thread;
    private long lastUpdate;

    private static native void n_init(int var0, int var1, int var2, int var3);

    private static native void n_chunkUpdateTask(int var0, int var1, byte[] var2);

    private static native void n_squareUpdateTask(int var0, ByteBuffer var1);

    private static native int n_pathTask(int var0, int var1, int var2, int var3, int[] var4);

    private static native boolean n_hasDataForThread();

    private static native boolean n_shouldWait();

    private static native void n_update();

    private static native void n_save();

    private static native void n_stop();

    private static native void n_setGameState(String var0, boolean var1);

    private static native void n_setGameState(String var0, double var1);

    private static native void n_setGameState(String var0, float var1);

    private static native void n_setGameState(String var0, int var1);

    private static native void n_setGameState(String var0, String var1);

    private static native void n_initMetaGrid(int var0, int var1, int var2, int var3);

    private static native void n_initMetaCell(int var0, int var1, String var2);

    private static native void n_initMetaChunk(int var0, int var1, int var2, int var3, int var4);

    private static void writeToStdErr(String string) {
        System.err.println(string);
    }

    public void init(IsoMetaGrid metaGrid) {
        this.bClient = GameClient.bClient;
        if (!this.bClient) {
            int int0 = metaGrid.getMinX();
            int int1 = metaGrid.getMinY();
            int int2 = metaGrid.getWidth();
            int int3 = metaGrid.getHeight();
            n_setGameState("Core.GameMode", Core.getInstance().getGameMode());
            n_setGameState("Core.GameSaveWorld", Core.GameSaveWorld);
            n_setGameState("Core.bLastStand", Core.bLastStand);
            n_setGameState("Core.noSave", this.bNoSave = Core.getInstance().isNoSave());
            n_setGameState("GameWindow.CacheDir", ZomboidFileSystem.instance.getCacheDir());
            n_setGameState("GameWindow.GameModeCacheDir", ZomboidFileSystem.instance.getGameModeCacheDir() + File.separator);
            n_setGameState("GameWindow.SaveDir", ZomboidFileSystem.instance.getSaveDir());
            n_setGameState("SandboxOptions.Distribution", SandboxOptions.instance.Distribution.getValue());
            n_setGameState("SandboxOptions.Zombies", SandboxOptions.instance.Zombies.getValue());
            n_setGameState("World.ZombiesDisabled", IsoWorld.getZombiesDisabled());
            n_setGameState("PAUSED", this.bPaused = true);
            n_initMetaGrid(int0, int1, int2, int3);

            for (int int4 = int1; int4 < int1 + int3; int4++) {
                for (int int5 = int0; int5 < int0 + int2; int5++) {
                    IsoMetaCell metaCell = metaGrid.getCellData(int5, int4);
                    n_initMetaCell(int5, int4, IsoLot.InfoFileNames.get("chunkdata_" + int5 + "_" + int4 + ".bin"));
                    if (metaCell != null) {
                        for (int int6 = 0; int6 < 30; int6++) {
                            for (int int7 = 0; int7 < 30; int7++) {
                                IsoMetaChunk metaChunk = metaCell.getChunk(int7, int6);
                                if (metaChunk != null) {
                                    n_initMetaChunk(int5, int4, int7, int6, metaChunk.getUnadjustedZombieIntensity());
                                }
                            }
                        }
                    }
                }
            }

            n_init(int0, int1, int2, int3);
        }
    }

    public void start() {
        if (!this.bClient) {
            if (this.thread == null) {
                this.thread = new MapCollisionData.MCDThread();
                this.thread.setDaemon(true);
                this.thread.setName("MapCollisionDataJNI");
                if (GameServer.bServer) {
                    this.thread.start();
                }
            }
        }
    }

    public void startGame() {
        if (!GameClient.bClient) {
            this.updateMain();
            ZombiePopulationManager.instance.updateMain();
            n_update();
            ZombiePopulationManager.instance.updateThread();
            this.updateMain();
            ZombiePopulationManager.instance.updateMain();
            this.thread.start();
        }
    }

    public void updateMain() {
        if (!this.bClient) {
            for (MapCollisionData.PathTask pathTask = this.pathResultQueue.poll(); pathTask != null; pathTask = this.pathResultQueue.poll()) {
                pathTask.result.finished(pathTask.status, pathTask.curX, pathTask.curY);
                pathTask.release();
            }

            long long0 = System.currentTimeMillis();
            if (long0 - this.lastUpdate > 10000L) {
                this.lastUpdate = long0;
                this.notifyThread();
            }
        }
    }

    public boolean hasDataForThread() {
        if (this.squareUpdateBuffer.position() > 0) {
            try {
                n_squareUpdateTask(this.squareUpdateBuffer.position() / 9, this.squareUpdateBuffer);
            } finally {
                this.squareUpdateBuffer.clear();
            }
        }

        return n_hasDataForThread();
    }

    public void updateGameState() {
        boolean boolean0 = Core.getInstance().isNoSave();
        if (this.bNoSave != boolean0) {
            this.bNoSave = boolean0;
            n_setGameState("Core.noSave", this.bNoSave);
        }

        boolean boolean1 = GameTime.isGamePaused();
        if (GameWindow.states.current != IngameState.instance) {
            boolean1 = true;
        }

        if (GameServer.bServer) {
            boolean1 = IngameState.instance.Paused;
        }

        if (boolean1 != this.bPaused) {
            this.bPaused = boolean1;
            n_setGameState("PAUSED", this.bPaused);
        }
    }

    public void notifyThread() {
        synchronized (this.thread.notifier) {
            this.thread.notifier.notify();
        }
    }

    public void addChunkToWorld(IsoChunk chunk) {
        if (!this.bClient) {
            for (int int0 = 0; int0 < 10; int0++) {
                for (int int1 = 0; int1 < 10; int1++) {
                    IsoGridSquare square = chunk.getGridSquare(int1, int0, 0);
                    if (square == null) {
                        this.squares[int1 + int0 * 10] = 1;
                    } else {
                        byte byte0 = 0;
                        if (this.isSolid(square)) {
                            byte0 = (byte)(byte0 | 1);
                        }

                        if (this.isBlockedN(square)) {
                            byte0 = (byte)(byte0 | 2);
                        }

                        if (this.isBlockedW(square)) {
                            byte0 = (byte)(byte0 | 4);
                        }

                        if (this.isWater(square)) {
                            byte0 = (byte)(byte0 | 8);
                        }

                        if (this.isRoom(square)) {
                            byte0 = (byte)(byte0 | 16);
                        }

                        this.squares[int1 + int0 * 10] = byte0;
                    }
                }
            }

            n_chunkUpdateTask(chunk.wx, chunk.wy, this.squares);
        }
    }

    public void removeChunkFromWorld(IsoChunk var1) {
        if (!this.bClient) {
            ;
        }
    }

    public void squareChanged(IsoGridSquare square) {
        if (!this.bClient) {
            try {
                byte byte0 = 0;
                if (this.isSolid(square)) {
                    byte0 = (byte)(byte0 | 1);
                }

                if (this.isBlockedN(square)) {
                    byte0 = (byte)(byte0 | 2);
                }

                if (this.isBlockedW(square)) {
                    byte0 = (byte)(byte0 | 4);
                }

                if (this.isWater(square)) {
                    byte0 = (byte)(byte0 | 8);
                }

                if (this.isRoom(square)) {
                    byte0 = (byte)(byte0 | 16);
                }

                this.squareUpdateBuffer.putInt(square.x);
                this.squareUpdateBuffer.putInt(square.y);
                this.squareUpdateBuffer.put(byte0);
                if (this.squareUpdateBuffer.remaining() < 9) {
                    n_squareUpdateTask(this.squareUpdateBuffer.position() / 9, this.squareUpdateBuffer);
                    this.squareUpdateBuffer.clear();
                }
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
            }
        }
    }

    public void save() {
        if (!this.bClient) {
            ZombiePopulationManager.instance.beginSaveRealZombies();
            if (!this.thread.isAlive()) {
                n_save();
                ZombiePopulationManager.instance.save();
            } else {
                this.thread.bSave = true;
                synchronized (this.thread.notifier) {
                    this.thread.notifier.notify();
                }

                while (this.thread.bSave) {
                    try {
                        Thread.sleep(5L);
                    } catch (InterruptedException interruptedException) {
                    }
                }

                ZombiePopulationManager.instance.endSaveRealZombies();
            }
        }
    }

    public void stop() {
        if (!this.bClient) {
            this.thread.bStop = true;
            synchronized (this.thread.notifier) {
                this.thread.notifier.notify();
            }

            while (this.thread.isAlive()) {
                try {
                    Thread.sleep(5L);
                } catch (InterruptedException interruptedException) {
                }
            }

            n_stop();
            this.thread = null;
            this.pathTaskQueue.clear();
            this.pathResultQueue.clear();
            this.squareUpdateBuffer.clear();
        }
    }

    private boolean isSolid(IsoGridSquare square) {
        boolean boolean0 = square.isSolid() || square.isSolidTrans();
        if (square.HasStairs()) {
            boolean0 = true;
        }

        if (square.Is(IsoFlagType.water)) {
            boolean0 = false;
        }

        if (square.Has(IsoObjectType.tree)) {
            boolean0 = false;
        }

        return boolean0;
    }

    private boolean isBlockedN(IsoGridSquare square) {
        if (square.Is(IsoFlagType.HoppableN)) {
            return false;
        } else {
            boolean boolean0 = square.Is(IsoFlagType.collideN);
            if (square.Has(IsoObjectType.doorFrN)) {
                boolean0 = true;
            }

            if (square.getProperties().Is(IsoFlagType.DoorWallN)) {
                boolean0 = true;
            }

            if (square.Has(IsoObjectType.windowFN)) {
                boolean0 = true;
            }

            if (square.Is(IsoFlagType.windowN)) {
                boolean0 = true;
            }

            if (square.getProperties().Is(IsoFlagType.WindowN)) {
                boolean0 = true;
            }

            return boolean0;
        }
    }

    private boolean isBlockedW(IsoGridSquare square) {
        if (square.Is(IsoFlagType.HoppableW)) {
            return false;
        } else {
            boolean boolean0 = square.Is(IsoFlagType.collideW);
            if (square.Has(IsoObjectType.doorFrW)) {
                boolean0 = true;
            }

            if (square.getProperties().Is(IsoFlagType.DoorWallW)) {
                boolean0 = true;
            }

            if (square.Has(IsoObjectType.windowFW)) {
                boolean0 = true;
            }

            if (square.Is(IsoFlagType.windowW)) {
                boolean0 = true;
            }

            if (square.getProperties().Is(IsoFlagType.WindowW)) {
                boolean0 = true;
            }

            return boolean0;
        }
    }

    private boolean isWater(IsoGridSquare square) {
        return square.Is(IsoFlagType.water);
    }

    private boolean isRoom(IsoGridSquare square) {
        return square.getRoom() != null;
    }

    public interface IPathResult {
        void finished(int var1, int var2, int var3);
    }

    private final class MCDThread extends Thread {
        public final Object notifier = new Object();
        public boolean bStop;
        public volatile boolean bSave;
        public volatile boolean bWaiting;
        public Queue<MapCollisionData.PathTask> pathTasks = new ArrayDeque<>();

        @Override
        public void run() {
            while (!this.bStop) {
                try {
                    this.runInner();
                } catch (Exception exception) {
                    ExceptionLogger.logException(exception);
                }
            }
        }

        private void runInner() {
            MPStatistic.getInstance().MapCollisionThread.Start();
            MapCollisionData.this.sync.startFrame();
            synchronized (MapCollisionData.this.renderLock) {
                for (MapCollisionData.PathTask pathTask = MapCollisionData.this.pathTaskQueue.poll();
                    pathTask != null;
                    pathTask = MapCollisionData.this.pathTaskQueue.poll()
                ) {
                    pathTask.execute();
                    pathTask.release();
                }

                if (this.bSave) {
                    MapCollisionData.n_save();
                    ZombiePopulationManager.instance.save();
                    this.bSave = false;
                }

                MapCollisionData.n_update();
                ZombiePopulationManager.instance.updateThread();
            }

            MapCollisionData.this.sync.endFrame();
            MPStatistic.getInstance().MapCollisionThread.End();

            while (this.shouldWait()) {
                synchronized (this.notifier) {
                    this.bWaiting = true;

                    try {
                        this.notifier.wait();
                    } catch (InterruptedException interruptedException) {
                    }
                }
            }

            this.bWaiting = false;
        }

        private boolean shouldWait() {
            if (this.bStop || this.bSave) {
                return false;
            } else if (!MapCollisionData.n_shouldWait()) {
                return false;
            } else {
                return !ZombiePopulationManager.instance.shouldWait() ? false : MapCollisionData.this.pathTaskQueue.isEmpty() && this.pathTasks.isEmpty();
            }
        }
    }

    private final class PathTask {
        public int startX;
        public int startY;
        public int endX;
        public int endY;
        public int curX;
        public int curY;
        public int status;
        public MapCollisionData.IPathResult result;
        public boolean myThread;

        public void init(int int0, int int1, int int2, int int3, MapCollisionData.IPathResult iPathResult) {
            this.startX = int0;
            this.startY = int1;
            this.endX = int2;
            this.endY = int3;
            this.status = 0;
            this.result = iPathResult;
        }

        public void execute() {
            this.status = MapCollisionData.n_pathTask(this.startX, this.startY, this.endX, this.endY, MapCollisionData.curXY);
            this.curX = MapCollisionData.curXY[0];
            this.curY = MapCollisionData.curXY[1];
            if (this.myThread) {
                this.result.finished(this.status, this.curX, this.curY);
            } else {
                MapCollisionData.this.pathResultQueue.add(this);
            }
        }

        public void release() {
            MapCollisionData.this.freePathTasks.push(this);
        }
    }

    static class Sync {
        private int fps = 10;
        private long period = 1000000000L / this.fps;
        private long excess;
        private long beforeTime = System.nanoTime();
        private long overSleepTime = 0L;

        void begin() {
            this.beforeTime = System.nanoTime();
            this.overSleepTime = 0L;
        }

        void startFrame() {
            this.excess = 0L;
        }

        void endFrame() {
            long long0 = System.nanoTime();
            long long1 = long0 - this.beforeTime;
            long long2 = this.period - long1 - this.overSleepTime;
            if (long2 > 0L) {
                try {
                    Thread.sleep(long2 / 1000000L);
                } catch (InterruptedException interruptedException) {
                }

                this.overSleepTime = System.nanoTime() - long0 - long2;
            } else {
                this.excess -= long2;
                this.overSleepTime = 0L;
            }

            this.beforeTime = System.nanoTime();
        }
    }
}
