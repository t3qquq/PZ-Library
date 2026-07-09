// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas.isoregion;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import zombie.ZomboidFileSystem;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.Core;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.isoregion.data.DataChunk;
import zombie.iso.areas.isoregion.data.DataRoot;
import zombie.iso.areas.isoregion.data.DataSquarePos;
import zombie.iso.areas.isoregion.regions.IChunkRegion;
import zombie.iso.areas.isoregion.regions.IWorldRegion;
import zombie.network.GameClient;
import zombie.network.GameServer;

/**
 * TurboTuTone.
 */
public final class IsoRegions {
    public static final int SINGLE_CHUNK_PACKET_SIZE = 1024;
    public static final int CHUNKS_DATA_PACKET_SIZE = 65536;
    public static boolean PRINT_D = false;
    public static final int CELL_DIM = 300;
    public static final int CELL_CHUNK_DIM = 30;
    public static final int CHUNK_DIM = 10;
    public static final int CHUNK_MAX_Z = 8;
    public static final byte BIT_EMPTY = 0;
    public static final byte BIT_WALL_N = 1;
    public static final byte BIT_WALL_W = 2;
    public static final byte BIT_PATH_WALL_N = 4;
    public static final byte BIT_PATH_WALL_W = 8;
    public static final byte BIT_HAS_FLOOR = 16;
    public static final byte BIT_STAIRCASE = 32;
    public static final byte BIT_HAS_ROOF = 64;
    public static final byte DIR_NONE = -1;
    public static final byte DIR_N = 0;
    public static final byte DIR_W = 1;
    public static final byte DIR_2D_NW = 2;
    public static final byte DIR_S = 2;
    public static final byte DIR_E = 3;
    public static final byte DIR_2D_MAX = 4;
    public static final byte DIR_TOP = 4;
    public static final byte DIR_BOT = 5;
    public static final byte DIR_MAX = 6;
    protected static final int CHUNK_LOAD_DIMENSIONS = 7;
    protected static boolean DEBUG_LOAD_ALL_CHUNKS = false;
    public static final String FILE_PRE = "datachunk_";
    public static final String FILE_SEP = "_";
    public static final String FILE_EXT = ".bin";
    public static final String FILE_DIR = "isoregiondata";
    private static final int SQUARE_CHANGE_WARN_THRESHOLD = 20;
    private static int SQUARE_CHANGE_PER_TICK = 0;
    private static String cacheDir;
    private static File cacheDirFile;
    private static File headDataFile;
    private static final Map<Integer, File> chunkFileNames = new HashMap<>();
    private static IsoRegionWorker regionWorker;
    private static DataRoot dataRoot;
    private static IsoRegionsLogger logger;
    protected static int lastChunkX = -1;
    protected static int lastChunkY = -1;
    private static byte previousFlags = 0;

    public static File getHeaderFile() {
        return headDataFile;
    }

    public static File getDirectory() {
        return cacheDirFile;
    }

    public static File getChunkFile(int chunkX, int chunkY) {
        int int0 = hash(chunkX, chunkY);
        if (chunkFileNames.containsKey(int0)) {
            File file0 = chunkFileNames.get(int0);
            if (file0 != null) {
                return chunkFileNames.get(int0);
            }
        }

        String string = cacheDir + "datachunk_" + chunkX + "_" + chunkY + ".bin";
        File file1 = new File(string);
        chunkFileNames.put(int0, file1);
        return file1;
    }

    public static byte GetOppositeDir(byte dir) {
        if (dir == 0) {
            return 2;
        } else if (dir == 1) {
            return 3;
        } else if (dir == 2) {
            return 0;
        } else if (dir == 3) {
            return 1;
        } else if (dir == 4) {
            return 5;
        } else {
            return (byte)(dir == 5 ? 4 : -1);
        }
    }

    public static void setDebugLoadAllChunks(boolean b) {
        DEBUG_LOAD_ALL_CHUNKS = b;
    }

    public static boolean isDebugLoadAllChunks() {
        return DEBUG_LOAD_ALL_CHUNKS;
    }

    public static int hash(int x, int y) {
        return y << 16 ^ x;
    }

    protected static DataRoot getDataRoot() {
        return dataRoot;
    }

    public static void init() {
        if (!Core.bDebug) {
            PRINT_D = false;
            DataSquarePos.DEBUG_POOL = false;
        }

        logger = new IsoRegionsLogger(PRINT_D);
        chunkFileNames.clear();
        cacheDir = ZomboidFileSystem.instance.getFileNameInCurrentSave("isoregiondata") + File.separator;
        cacheDirFile = new File(cacheDir);
        if (!cacheDirFile.exists()) {
            cacheDirFile.mkdir();
        }

        String string = cacheDir + "RegionHeader.bin";
        headDataFile = new File(string);
        previousFlags = 0;
        dataRoot = new DataRoot();
        regionWorker = new IsoRegionWorker();
        regionWorker.create();
        regionWorker.load();
    }

    public static IsoRegionsLogger getLogger() {
        return logger;
    }

    public static void log(String str) {
        logger.log(str);
    }

    public static void log(String str, Color col) {
        logger.log(str, col);
    }

    public static void warn(String str) {
        logger.warn(str);
    }

    public static void reset() {
        previousFlags = 0;
        regionWorker.stop();
        regionWorker = null;
        dataRoot = null;
        chunkFileNames.clear();
    }

    public static void receiveServerUpdatePacket(ByteBuffer input) {
        if (regionWorker == null) {
            logger.warn("IsoRegion cannot receive server packet, regionWorker == null.");
        } else {
            if (GameClient.bClient) {
                regionWorker.readServerUpdatePacket(input);
            }
        }
    }

    public static void receiveClientRequestFullDataChunks(ByteBuffer input, UdpConnection conn) {
        if (regionWorker == null) {
            logger.warn("IsoRegion cannot receive client packet, regionWorker == null.");
        } else {
            if (GameServer.bServer) {
                regionWorker.readClientRequestFullUpdatePacket(input, conn);
            }
        }
    }

    public static void update() {
        if (Core.bDebug && SQUARE_CHANGE_PER_TICK > 20) {
            logger.warn("IsoRegion Warning -> " + SQUARE_CHANGE_PER_TICK + " squares have been changed in one tick.");
        }

        SQUARE_CHANGE_PER_TICK = 0;
        if (IsoRegionWorker.isRequestingBufferSwap.get()) {
            logger.log("IsoRegion Swapping DataRoot");
            DataRoot dataRootx = dataRoot;
            dataRoot = regionWorker.getRootBuffer();
            regionWorker.setRootBuffer(dataRootx);
            IsoRegionWorker.isRequestingBufferSwap.set(false);
            if (!GameServer.bServer) {
                clientResetCachedRegionReferences();
            }
        }

        if (!GameClient.bClient && !GameServer.bServer && DEBUG_LOAD_ALL_CHUNKS && Core.bDebug) {
            int int0 = (int)IsoPlayer.getInstance().getX() / 10;
            int int1 = (int)IsoPlayer.getInstance().getY() / 10;
            if (lastChunkX != int0 || lastChunkY != int1) {
                lastChunkX = int0;
                lastChunkY = int1;
                regionWorker.readSurroundingChunks(int0, int1, IsoChunkMap.ChunkGridWidth - 2, true);
            }
        }

        regionWorker.update();
        logger.update();
    }

    protected static void forceRecalcSurroundingChunks() {
        if (Core.bDebug && !GameClient.bClient) {
            logger.log("[DEBUG] Forcing a full load/recalculate of chunks surrounding player.", Colors.Gold);
            int int0 = (int)IsoPlayer.getInstance().getX() / 10;
            int int1 = (int)IsoPlayer.getInstance().getY() / 10;
            regionWorker.readSurroundingChunks(int0, int1, IsoChunkMap.ChunkGridWidth - 2, true, true);
        }
    }

    public static byte getSquareFlags(int x, int y, int z) {
        return dataRoot.getSquareFlags(x, y, z);
    }

    /**
     * Returns a IWorldRegion for the square.  Note: Returned objects from this function should not be retained as the DataRoot may get swapped.  Note: The IWorldRegion does get cached in IsoGridSquare for optimizing purposes but this gets handled in 'clientResetCachedRegionReferences()'
     * @return can be null.
     */
    public static IWorldRegion getIsoWorldRegion(int x, int y, int z) {
        return dataRoot.getIsoWorldRegion(x, y, z);
    }

    /**
     * Returns a DataChunk for the square.  Note: Returned objects from this function should not be retained as the DataRoot may get swapped.
     * @return can be null.
     */
    public static DataChunk getDataChunk(int chunkx, int chunky) {
        return dataRoot.getDataChunk(chunkx, chunky);
    }

    /**
     * Returns a IChunkRegion for the square.  Note: Returned objects from this function should not be retained as the DataRoot may get swapped.
     * @return can be null.
     */
    public static IChunkRegion getChunkRegion(int x, int y, int z) {
        return dataRoot.getIsoChunkRegion(x, y, z);
    }

    public static void ResetAllDataDebug() {
        if (Core.bDebug) {
            if (!GameServer.bServer && !GameClient.bClient) {
                regionWorker.addDebugResetJob();
            }
        }
    }

    private static void clientResetCachedRegionReferences() {
        if (!GameServer.bServer) {
            byte byte0 = 0;
            byte byte1 = 0;
            int int0 = IsoChunkMap.ChunkGridWidth;
            int int1 = IsoChunkMap.ChunkGridWidth;
            IsoChunkMap chunkMap = IsoWorld.instance.getCell().getChunkMap(IsoPlayer.getPlayerIndex());
            if (chunkMap != null) {
                for (int int2 = byte0; int2 < int0; int2++) {
                    for (int int3 = byte1; int3 < int1; int3++) {
                        IsoChunk chunk = chunkMap.getChunk(int2, int3);
                        if (chunk != null) {
                            for (int int4 = 0; int4 <= chunk.maxLevel; int4++) {
                                for (int int5 = 0; int5 < chunk.squares[0].length; int5++) {
                                    IsoGridSquare square = chunk.squares[int4][int5];
                                    if (square != null) {
                                        square.setIsoWorldRegion(null);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Needs to be called before a player manipulates the grid.  Records bitFlags for the state of the square that are compared to bitFlags for the state of the square after manipulation to detect relevant changes.
     */
    public static void setPreviousFlags(IsoGridSquare gs) {
        previousFlags = calculateSquareFlags(gs);
    }

    /**
     * Called after the grid has been manipulated by a player.  NOTE: setPreviousFlags needs to be called prior to the grid being manipulated by a player.
     */
    public static void squareChanged(IsoGridSquare gs) {
        squareChanged(gs, false);
    }

    /**
     * Called after the grid has been manipulated by a player.  NOTE: setPreviousFlags needs to be called prior to the grid being manipulated by a player.
     */
    public static void squareChanged(IsoGridSquare gs, boolean isRemoval) {
        if (!GameClient.bClient) {
            if (gs != null) {
                byte byte0 = calculateSquareFlags(gs);
                if (byte0 != previousFlags) {
                    regionWorker.addSquareChangedJob(gs.getX(), gs.getY(), gs.getZ(), isRemoval, byte0);
                    SQUARE_CHANGE_PER_TICK++;
                    previousFlags = 0;
                }
            }
        }
    }

    protected static byte calculateSquareFlags(IsoGridSquare square) {
        byte byte0 = 0;
        if (square != null) {
            if (square.Is(IsoFlagType.solidfloor)) {
                byte0 |= 16;
            }

            if (square.Is(IsoFlagType.cutN) || square.Has(IsoObjectType.doorFrN)) {
                byte0 |= 1;
                if (square.Is(IsoFlagType.WindowN) || square.Is(IsoFlagType.windowN) || square.Is(IsoFlagType.DoorWallN)) {
                    byte0 |= 4;
                }
            }

            if (!square.Is(IsoFlagType.WallSE) && (square.Is(IsoFlagType.cutW) || square.Has(IsoObjectType.doorFrW))) {
                byte0 |= 2;
                if (square.Is(IsoFlagType.WindowW) || square.Is(IsoFlagType.windowW) || square.Is(IsoFlagType.DoorWallW)) {
                    byte0 |= 8;
                }
            }

            if (square.HasStairsNorth() || square.HasStairsWest()) {
                byte0 |= 32;
            }
        }

        return (byte)byte0;
    }

    protected static IsoRegionWorker getRegionWorker() {
        return regionWorker;
    }
}
