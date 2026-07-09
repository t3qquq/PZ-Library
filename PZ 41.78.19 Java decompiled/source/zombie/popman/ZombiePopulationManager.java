// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.popman;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.hash.TIntHashSet;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import se.krka.kahlua.vm.KahluaTable;
import zombie.DebugFileWatcher;
import zombie.GameTime;
import zombie.MapCollisionData;
import zombie.PersistentOutfits;
import zombie.PredicatedFileWatcher;
import zombie.SandboxOptions;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZomboidFileSystem;
import zombie.ai.states.PathFindState;
import zombie.ai.states.WalkTowardState;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.gameStates.ChooseGameInfo;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.util.PZXmlParserException;
import zombie.util.PZXmlUtil;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.PolygonalMap2;

public final class ZombiePopulationManager {
    public static final ZombiePopulationManager instance = new ZombiePopulationManager();
    protected static final int SQUARES_PER_CHUNK = 10;
    protected static final int CHUNKS_PER_CELL = 30;
    protected static final int SQUARES_PER_CELL = 300;
    protected static final byte OLD_ZOMBIE_CRAWLER_CAN_WALK = 1;
    protected static final byte OLD_ZOMBIE_FAKE_DEAD = 2;
    protected static final byte OLD_ZOMBIE_CRAWLER = 3;
    protected static final byte OLD_ZOMBIE_WALKER = 4;
    protected static final int ZOMBIE_STATE_INITIALIZED = 1;
    protected static final int ZOMBIE_STATE_CRAWLING = 2;
    protected static final int ZOMBIE_STATE_CAN_WALK = 4;
    protected static final int ZOMBIE_STATE_FAKE_DEAD = 8;
    protected static final int ZOMBIE_STATE_CRAWL_UNDER_VEHICLE = 16;
    protected int minX;
    protected int minY;
    protected int width;
    protected int height;
    protected boolean bStopped;
    protected boolean bClient;
    private final DebugCommands dbgCommands = new DebugCommands();
    public static boolean bDebugLoggingEnabled = false;
    private final LoadedAreas loadedAreas = new LoadedAreas(false);
    private final LoadedAreas loadedServerCells = new LoadedAreas(true);
    private final PlayerSpawns playerSpawns = new PlayerSpawns();
    private short[] realZombieCount;
    private short[] realZombieCount2;
    private long realZombieUpdateTime = 0L;
    private final ArrayList<IsoZombie> saveRealZombieHack = new ArrayList<>();
    private final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
    private final TIntHashSet newChunks = new TIntHashSet();
    private final ArrayList<ChooseGameInfo.SpawnOrigin> spawnOrigins = new ArrayList<>();
    public float[] radarXY;
    public int radarCount;
    public boolean radarRenderFlag;
    public boolean radarRequestFlag;
    private final ArrayList<IsoDirections> m_sittingDirections = new ArrayList<>();

    ZombiePopulationManager() {
        this.newChunks.setAutoCompactionFactor(0.0F);
    }

    private static native void n_init(boolean var0, boolean var1, int var2, int var3, int var4, int var5);

    private static native void n_config(float var0, float var1, float var2, int var3, float var4, float var5, float var6, float var7, int var8);

    private static native void n_setSpawnOrigins(int[] var0);

    private static native void n_setOutfitNames(String[] var0);

    private static native void n_updateMain(float var0, double var1);

    private static native boolean n_hasDataForThread();

    private static native void n_updateThread();

    private static native boolean n_shouldWait();

    private static native void n_beginSaveRealZombies(int var0);

    private static native void n_saveRealZombies(int var0, ByteBuffer var1);

    private static native void n_save();

    private static native void n_stop();

    private static native void n_addZombie(float var0, float var1, float var2, byte var3, int var4, int var5, int var6, int var7);

    private static native void n_aggroTarget(int var0, int var1, int var2);

    private static native void n_loadChunk(int var0, int var1, boolean var2);

    private static native void n_loadedAreas(int var0, int[] var1, boolean var2);

    protected static native void n_realZombieCount(short var0, short[] var1);

    protected static native void n_spawnHorde(int var0, int var1, int var2, int var3, float var4, float var5, int var6);

    private static native void n_worldSound(int var0, int var1, int var2, int var3);

    private static native int n_getAddZombieCount();

    private static native int n_getAddZombieData(int var0, ByteBuffer var1);

    private static native boolean n_hasRadarData();

    private static native void n_requestRadarData();

    private static native int n_getRadarZombieData(float[] var0);

    private static void noise(String string) {
        if (bDebugLoggingEnabled && (Core.bDebug || GameServer.bServer && GameServer.bDebug)) {
            DebugLog.log("ZPOP: " + string);
        }
    }

    public static void init() {
        String string = "";
        if ("1".equals(System.getProperty("zomboid.debuglibs.popman"))) {
            DebugLog.log("***** Loading debug version of PZPopMan");
            string = "d";
        }

        if (System.getProperty("os.name").contains("OS X")) {
            System.loadLibrary("PZPopMan");
        } else if (System.getProperty("sun.arch.data.model").equals("64")) {
            System.loadLibrary("PZPopMan64" + string);
        } else {
            System.loadLibrary("PZPopMan32" + string);
        }

        DebugFileWatcher.instance
            .add(new PredicatedFileWatcher(ZomboidFileSystem.instance.getMessagingDirSub("Trigger_Zombie.xml"), ZombiePopulationManager::onTriggeredZombieFile));
    }

    private static void onTriggeredZombieFile(String string) {
        DebugLog.General.println("ZombiePopulationManager.onTriggeredZombieFile(" + string + ">");

        ZombieTriggerXmlFile zombieTriggerXmlFile;
        try {
            zombieTriggerXmlFile = PZXmlUtil.parse(ZombieTriggerXmlFile.class, string);
        } catch (PZXmlParserException pZXmlParserException) {
            System.err.println("ZombiePopulationManager.onTriggeredZombieFile> Exception thrown. " + pZXmlParserException);
            pZXmlParserException.printStackTrace();
            return;
        }

        if (zombieTriggerXmlFile.spawnHorde > 0) {
            processTriggerSpawnHorde(zombieTriggerXmlFile);
        }

        if (zombieTriggerXmlFile.setDebugLoggingEnabled && bDebugLoggingEnabled != zombieTriggerXmlFile.bDebugLoggingEnabled) {
            bDebugLoggingEnabled = zombieTriggerXmlFile.bDebugLoggingEnabled;
            DebugLog.General.println("  bDebugLoggingEnabled: " + bDebugLoggingEnabled);
        }
    }

    private static void processTriggerSpawnHorde(ZombieTriggerXmlFile zombieTriggerXmlFile) {
        DebugLog.General.println("  spawnHorde: " + zombieTriggerXmlFile.spawnHorde);
        if (IsoPlayer.getInstance() != null) {
            IsoPlayer player = IsoPlayer.getInstance();
            instance.createHordeFromTo((int)player.x, (int)player.y, (int)player.x, (int)player.y, zombieTriggerXmlFile.spawnHorde);
        }
    }

    public void init(IsoMetaGrid metaGrid) {
        this.bClient = GameClient.bClient;
        if (!this.bClient) {
            this.minX = metaGrid.getMinX();
            this.minY = metaGrid.getMinY();
            this.width = metaGrid.getWidth();
            this.height = metaGrid.getHeight();
            this.bStopped = false;
            n_init(this.bClient, GameServer.bServer, this.minX, this.minY, this.width, this.height);
            this.onConfigReloaded();
            String[] strings = PersistentOutfits.instance.getOutfitNames().toArray(new String[0]);

            for (int int0 = 0; int0 < strings.length; int0++) {
                strings[int0] = strings[int0].toLowerCase();
            }

            n_setOutfitNames(strings);
            TIntArrayList tIntArrayList = new TIntArrayList();

            for (ChooseGameInfo.SpawnOrigin spawnOrigin : this.spawnOrigins) {
                tIntArrayList.add(spawnOrigin.x);
                tIntArrayList.add(spawnOrigin.y);
                tIntArrayList.add(spawnOrigin.w);
                tIntArrayList.add(spawnOrigin.h);
            }

            n_setSpawnOrigins(tIntArrayList.toArray());
        }
    }

    public void onConfigReloaded() {
        SandboxOptions.ZombieConfig zombieConfig = SandboxOptions.instance.zombieConfig;
        n_config(
            (float)zombieConfig.PopulationMultiplier.getValue(),
            (float)zombieConfig.PopulationStartMultiplier.getValue(),
            (float)zombieConfig.PopulationPeakMultiplier.getValue(),
            zombieConfig.PopulationPeakDay.getValue(),
            (float)zombieConfig.RespawnHours.getValue(),
            (float)zombieConfig.RespawnUnseenHours.getValue(),
            (float)zombieConfig.RespawnMultiplier.getValue() * 100.0F,
            (float)zombieConfig.RedistributeHours.getValue(),
            zombieConfig.FollowSoundDistance.getValue()
        );
    }

    public void registerSpawnOrigin(int int3, int int2, int int1, int int0, KahluaTable var5) {
        if (int3 >= 0 && int2 >= 0 && int1 >= 0 && int0 >= 0) {
            this.spawnOrigins.add(new ChooseGameInfo.SpawnOrigin(int3, int2, int1, int0));
        }
    }

    public void playerSpawnedAt(int int0, int int1, int int2) {
        this.playerSpawns.addSpawn(int0, int1, int2);
    }

    public void addChunkToWorld(IsoChunk chunk) {
        if (!this.bClient) {
            if (chunk.isNewChunk()) {
                int int0 = chunk.wy << 16 | chunk.wx;
                this.newChunks.add(int0);
            }

            n_loadChunk(chunk.wx, chunk.wy, true);
        }
    }

    public void removeChunkFromWorld(IsoChunk chunk) {
        if (!this.bClient) {
            if (!this.bStopped) {
                n_loadChunk(chunk.wx, chunk.wy, false);

                for (int int0 = 0; int0 < 8; int0++) {
                    for (int int1 = 0; int1 < 10; int1++) {
                        for (int int2 = 0; int2 < 10; int2++) {
                            IsoGridSquare square = chunk.getGridSquare(int2, int1, int0);
                            if (square != null && !square.getMovingObjects().isEmpty()) {
                                for (int int3 = 0; int3 < square.getMovingObjects().size(); int3++) {
                                    IsoMovingObject movingObject = square.getMovingObjects().get(int3);
                                    if (movingObject instanceof IsoZombie zombie0
                                        && (!GameServer.bServer || !zombie0.bIndoorZombie)
                                        && !zombie0.isReanimatedPlayer()) {
                                        int int4 = this.getZombieState(zombie0);
                                        if (int0 != 0
                                            || square.getRoom() != null
                                            || zombie0.getCurrentState() != WalkTowardState.instance() && zombie0.getCurrentState() != PathFindState.instance()
                                            )
                                         {
                                            n_addZombie(
                                                zombie0.x, zombie0.y, zombie0.z, (byte)zombie0.dir.index(), zombie0.getPersistentOutfitID(), int4, -1, -1
                                            );
                                        } else {
                                            n_addZombie(
                                                zombie0.x,
                                                zombie0.y,
                                                zombie0.z,
                                                (byte)zombie0.dir.index(),
                                                zombie0.getPersistentOutfitID(),
                                                int4,
                                                zombie0.getPathTargetX(),
                                                zombie0.getPathTargetY()
                                            );
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                int int5 = chunk.wy << 16 | chunk.wx;
                this.newChunks.remove(int5);
                if (GameServer.bServer) {
                    MapCollisionData.instance.notifyThread();
                }
            }
        }
    }

    public void virtualizeZombie(IsoZombie zombie0) {
        int int0 = this.getZombieState(zombie0);
        n_addZombie(
            zombie0.x,
            zombie0.y,
            zombie0.z,
            (byte)zombie0.dir.index(),
            zombie0.getPersistentOutfitID(),
            int0,
            zombie0.getPathTargetX(),
            zombie0.getPathTargetY()
        );
        zombie0.removeFromWorld();
        zombie0.removeFromSquare();
    }

    private int getZombieState(IsoZombie zombie0) {
        byte byte0 = 1;
        if (zombie0.isCrawling()) {
            byte0 |= 2;
        }

        if (zombie0.isCanWalk()) {
            byte0 |= 4;
        }

        if (zombie0.isFakeDead()) {
            byte0 |= 8;
        }

        if (zombie0.isCanCrawlUnderVehicle()) {
            byte0 |= 16;
        }

        return byte0;
    }

    public void setAggroTarget(int int0, int int1, int int2) {
        n_aggroTarget(int0, int1, int2);
    }

    public void createHordeFromTo(int int0, int int1, int int4, int int3, int int2) {
        n_spawnHorde(int0, int1, 0, 0, int4, int3, int2);
    }

    public void createHordeInAreaTo(int int0, int int1, int int2, int int3, int int6, int int5, int int4) {
        n_spawnHorde(int0, int1, int2, int3, int6, int5, int4);
    }

    public void addWorldSound(WorldSoundManager.WorldSound worldSound, boolean var2) {
        if (!this.bClient) {
            if (worldSound.radius >= 50) {
                if (!worldSound.sourceIsZombie) {
                    int int0 = SandboxOptions.instance.Lore.Hearing.getValue();
                    if (int0 == 4) {
                        int0 = 2;
                    }

                    float float0 = WorldSoundManager.instance.getHearingMultiplier(int0);
                    n_worldSound(worldSound.x, worldSound.y, (int)PZMath.ceil(worldSound.radius * float0), worldSound.volume);
                }
            }
        }
    }

    private void updateRealZombieCount() {
        if (this.realZombieCount == null || this.realZombieCount.length != this.width * this.height) {
            this.realZombieCount = new short[this.width * this.height];
            this.realZombieCount2 = new short[this.width * this.height * 2];
        }

        Arrays.fill(this.realZombieCount, (short)0);
        ArrayList arrayList = IsoWorld.instance.CurrentCell.getZombieList();

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            IsoZombie zombie0 = (IsoZombie)arrayList.get(int0);
            int int1 = (int)(zombie0.x / 300.0F) - this.minX;
            int int2 = (int)(zombie0.y / 300.0F) - this.minY;
            this.realZombieCount[int1 + int2 * this.width]++;
        }

        short short0 = 0;

        for (int int3 = 0; int3 < this.width * this.height; int3++) {
            if (this.realZombieCount[int3] > 0) {
                this.realZombieCount2[short0 * 2 + 0] = (short)int3;
                this.realZombieCount2[short0 * 2 + 1] = this.realZombieCount[int3];
                short0++;
            }
        }

        n_realZombieCount(short0, this.realZombieCount2);
    }

    public void updateMain() {
        if (!this.bClient) {
            long long0 = System.currentTimeMillis();
            n_updateMain(GameTime.getInstance().getMultiplier(), GameTime.getInstance().getWorldAgeHours());
            int int0 = 0;
            int int1 = 0;
            int int2 = n_getAddZombieCount();
            int int3 = 0;

            while (int3 < int2) {
                this.byteBuffer.clear();
                int int4 = n_getAddZombieData(int3, this.byteBuffer);
                int3 += int4;

                for (int int5 = 0; int5 < int4; int5++) {
                    float float0 = this.byteBuffer.getFloat();
                    float float1 = this.byteBuffer.getFloat();
                    float float2 = this.byteBuffer.getFloat();
                    IsoDirections directions = IsoDirections.fromIndex(this.byteBuffer.get());
                    int int6 = this.byteBuffer.getInt();
                    int int7 = this.byteBuffer.getInt();
                    int int8 = this.byteBuffer.getInt();
                    int int9 = this.byteBuffer.getInt();
                    int int10 = (int)float0 / 10;
                    int int11 = (int)float1 / 10;
                    int int12 = int11 << 16 | int10;
                    if (this.newChunks.contains(int12)) {
                        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((int)float0, (int)float1, (int)float2);
                        if (square != null && square.roomID != -1) {
                            continue;
                        }
                    }

                    if (int8 != -1 && this.loadedAreas.isOnEdge((int)float0, (int)float1)) {
                        int8 = -1;
                        int9 = -1;
                    }

                    if (int8 == -1) {
                        this.addZombieStanding(float0, float1, float2, directions, int6, int7);
                        int0++;
                    } else {
                        this.addZombieMoving(float0, float1, float2, directions, int6, int7, int8, int9);
                        int1++;
                    }
                }
            }

            if (int0 > 0) {
                noise("unloaded -> real " + int2);
            }

            if (int1 > 0) {
                noise("virtual -> real " + int2);
            }

            if (this.radarRenderFlag && this.radarXY != null) {
                if (this.radarRequestFlag) {
                    if (n_hasRadarData()) {
                        this.radarCount = n_getRadarZombieData(this.radarXY);
                        this.radarRenderFlag = false;
                        this.radarRequestFlag = false;
                    }
                } else {
                    n_requestRadarData();
                    this.radarRequestFlag = true;
                }
            }

            this.updateLoadedAreas();
            if (this.realZombieUpdateTime + 5000L < long0) {
                this.realZombieUpdateTime = long0;
                this.updateRealZombieCount();
            }

            if (GameServer.bServer) {
                MPDebugInfo.instance.serverUpdate();
            }

            boolean boolean0 = n_hasDataForThread();
            boolean boolean1 = MapCollisionData.instance.hasDataForThread();
            if (boolean0 || boolean1) {
                MapCollisionData.instance.notifyThread();
            }

            this.playerSpawns.update();
        }
    }

    private void addZombieStanding(float float2, float float1, float float0, IsoDirections directions, int int1, int int0) {
        IsoGridSquare square0 = IsoWorld.instance.CurrentCell.getGridSquare((int)float2, (int)float1, (int)float0);
        if (square0 != null && (square0.SolidFloorCached ? square0.SolidFloor : square0.TreatAsSolidFloor())) {
            if (!Core.bLastStand && !this.playerSpawns.allowZombie(square0)) {
                noise("removed zombie near player spawn " + (int)float2 + "," + (int)float1 + "," + (int)float0);
                return;
            }

            VirtualZombieManager.instance.choices.clear();
            IsoGridSquare square1 = null;
            if (!this.isCrawling(int0) && !this.isFakeDead(int0) && Rand.Next(3) == 0) {
                square1 = this.getSquareForSittingZombie(float2, float1, (int)float0);
            }

            if (square1 != null) {
                VirtualZombieManager.instance.choices.add(square1);
            } else {
                VirtualZombieManager.instance.choices.add(square0);
            }

            IsoZombie zombie0 = VirtualZombieManager.instance.createRealZombieAlways(int1, directions.index(), false);
            if (zombie0 != null) {
                if (square1 != null) {
                    this.sitAgainstWall(zombie0, square1);
                } else {
                    zombie0.setX(float2);
                    zombie0.setY(float1);
                }

                if (this.isFakeDead(int0)) {
                    zombie0.setHealth(0.5F + Rand.Next(0.0F, 0.3F));
                    zombie0.sprite = zombie0.legsSprite;
                    zombie0.setFakeDead(true);
                } else if (this.isCrawling(int0)) {
                    zombie0.setCrawler(true);
                    zombie0.setCanWalk(this.isCanWalk(int0));
                    zombie0.setOnFloor(true);
                    zombie0.setFallOnFront(true);
                    zombie0.walkVariant = "ZombieWalk";
                    zombie0.DoZombieStats();
                }

                if (this.isInitialized(int0)) {
                    zombie0.setCanCrawlUnderVehicle(this.isCanCrawlUnderVehicle(int0));
                } else {
                    this.firstTimeLoaded(zombie0, int0);
                }
            }
        } else {
            noise("real -> unloaded");
            n_addZombie(float2, float1, float0, (byte)directions.index(), int1, int0, -1, -1);
        }
    }

    private IsoGridSquare getSquareForSittingZombie(float float1, float float0, int int2) {
        byte byte0 = 3;

        for (int int0 = -byte0; int0 < byte0; int0++) {
            for (int int1 = -byte0; int1 < byte0; int1++) {
                IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((int)float1 + int0, (int)float0 + int1, int2);
                if (square != null && square.isFree(true) && square.getBuilding() == null) {
                    int int3 = square.getWallType();
                    if (int3 != 0 && !PolygonalMap2.instance.lineClearCollide(float1, float0, square.x + 0.5F, square.y + 0.5F, square.z, null, false, true)) {
                        return square;
                    }
                }
            }
        }

        return null;
    }

    public void sitAgainstWall(IsoZombie zombie0, IsoGridSquare square) {
        float float0 = square.x + 0.5F;
        float float1 = square.y + 0.5F;
        zombie0.setX(float0);
        zombie0.setY(float1);
        zombie0.setSitAgainstWall(true);
        int int0 = square.getWallType();
        if (int0 != 0) {
            this.m_sittingDirections.clear();
            if ((int0 & 1) != 0 && (int0 & 4) != 0) {
                this.m_sittingDirections.add(IsoDirections.SE);
            }

            if ((int0 & 1) != 0 && (int0 & 8) != 0) {
                this.m_sittingDirections.add(IsoDirections.SW);
            }

            if ((int0 & 2) != 0 && (int0 & 4) != 0) {
                this.m_sittingDirections.add(IsoDirections.NE);
            }

            if ((int0 & 2) != 0 && (int0 & 8) != 0) {
                this.m_sittingDirections.add(IsoDirections.NW);
            }

            if ((int0 & 1) != 0) {
                this.m_sittingDirections.add(IsoDirections.S);
            }

            if ((int0 & 2) != 0) {
                this.m_sittingDirections.add(IsoDirections.N);
            }

            if ((int0 & 4) != 0) {
                this.m_sittingDirections.add(IsoDirections.E);
            }

            if ((int0 & 8) != 0) {
                this.m_sittingDirections.add(IsoDirections.W);
            }

            IsoDirections directions = PZArrayUtil.pickRandom(this.m_sittingDirections);
            if (GameClient.bClient) {
                int int1 = (square.x & 1) + (square.y & 1);
                directions = this.m_sittingDirections.get(int1 % this.m_sittingDirections.size());
            }

            zombie0.setDir(directions);
            zombie0.setForwardDirection(directions.ToVector());
            if (zombie0.getAnimationPlayer() != null) {
                zombie0.getAnimationPlayer().SetForceDir(zombie0.getForwardDirection());
            }
        }
    }

    private void addZombieMoving(float float2, float float1, float float0, IsoDirections directions, int int0, int int1, int int3, int int2) {
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((int)float2, (int)float1, (int)float0);
        if (square != null && (square.SolidFloorCached ? square.SolidFloor : square.TreatAsSolidFloor())) {
            if (!Core.bLastStand && !this.playerSpawns.allowZombie(square)) {
                noise("removed zombie near player spawn " + (int)float2 + "," + (int)float1 + "," + (int)float0);
                return;
            }

            VirtualZombieManager.instance.choices.clear();
            VirtualZombieManager.instance.choices.add(square);
            IsoZombie zombie0 = VirtualZombieManager.instance.createRealZombieAlways(int0, directions.index(), false);
            if (zombie0 != null) {
                zombie0.setX(float2);
                zombie0.setY(float1);
                if (this.isCrawling(int1)) {
                    zombie0.setCrawler(true);
                    zombie0.setCanWalk(this.isCanWalk(int1));
                    zombie0.setOnFloor(true);
                    zombie0.setFallOnFront(true);
                    zombie0.walkVariant = "ZombieWalk";
                    zombie0.DoZombieStats();
                }

                if (this.isInitialized(int1)) {
                    zombie0.setCanCrawlUnderVehicle(this.isCanCrawlUnderVehicle(int1));
                } else {
                    this.firstTimeLoaded(zombie0, int1);
                }

                if (Math.abs(int3 - float2) > 1.0F || Math.abs(int2 - float1) > 1.0F) {
                    zombie0.AllowRepathDelay = -1.0F;
                    zombie0.pathToLocation(int3, int2, 0);
                }
            }
        } else {
            noise("real -> virtual " + float2 + "," + float1);
            n_addZombie(float2, float1, float0, (byte)directions.index(), int0, int1, int3, int2);
        }
    }

    private boolean isInitialized(int int0) {
        return (int0 & 1) != 0;
    }

    private boolean isCrawling(int int0) {
        return (int0 & 2) != 0;
    }

    private boolean isCanWalk(int int0) {
        return (int0 & 4) != 0;
    }

    private boolean isFakeDead(int int0) {
        return (int0 & 8) != 0;
    }

    private boolean isCanCrawlUnderVehicle(int int0) {
        return (int0 & 16) != 0;
    }

    private void firstTimeLoaded(IsoZombie var1, int var2) {
    }

    public void updateThread() {
        n_updateThread();
    }

    public boolean shouldWait() {
        synchronized (MapCollisionData.instance.renderLock) {
            return n_shouldWait();
        }
    }

    public void updateLoadedAreas() {
        if (this.loadedAreas.set()) {
            n_loadedAreas(this.loadedAreas.count, this.loadedAreas.areas, false);
        }

        if (GameServer.bServer && this.loadedServerCells.set()) {
            n_loadedAreas(this.loadedServerCells.count, this.loadedServerCells.areas, true);
        }
    }

    public void dbgSpawnTimeToZero(int int0, int int1) {
        if (!this.bClient || GameClient.connection.accessLevel == 32) {
            this.dbgCommands.SpawnTimeToZero(int0, int1);
        }
    }

    public void dbgClearZombies(int int0, int int1) {
        if (!this.bClient || GameClient.connection.accessLevel == 32) {
            this.dbgCommands.ClearZombies(int0, int1);
        }
    }

    public void dbgSpawnNow(int int0, int int1) {
        if (!this.bClient || GameClient.connection.accessLevel == 32) {
            this.dbgCommands.SpawnNow(int0, int1);
        }
    }

    public void beginSaveRealZombies() {
        if (!this.bClient) {
            this.saveRealZombieHack.clear();

            for (IsoZombie zombie0 : IsoWorld.instance.CurrentCell.getZombieList()) {
                if (!zombie0.isReanimatedPlayer() && (!GameServer.bServer || !zombie0.bIndoorZombie)) {
                    this.saveRealZombieHack.add(zombie0);
                }
            }

            int int0 = this.saveRealZombieHack.size();
            n_beginSaveRealZombies(int0);
            int int1 = 0;

            while (int1 < int0) {
                this.byteBuffer.clear();
                int int2 = 0;

                while (int1 < int0) {
                    int int3 = this.byteBuffer.position();
                    IsoZombie zombie1 = this.saveRealZombieHack.get(int1++);
                    this.byteBuffer.putFloat(zombie1.x);
                    this.byteBuffer.putFloat(zombie1.y);
                    this.byteBuffer.putFloat(zombie1.z);
                    this.byteBuffer.put((byte)zombie1.dir.index());
                    this.byteBuffer.putInt(zombie1.getPersistentOutfitID());
                    int int4 = this.getZombieState(zombie1);
                    this.byteBuffer.putInt(int4);
                    int2++;
                    int int5 = this.byteBuffer.position() - int3;
                    if (this.byteBuffer.position() + int5 > this.byteBuffer.capacity()) {
                        break;
                    }
                }

                n_saveRealZombies(int2, this.byteBuffer);
            }

            this.saveRealZombieHack.clear();
        }
    }

    public void endSaveRealZombies() {
        if (!this.bClient) {
            ;
        }
    }

    public void save() {
        if (!this.bClient) {
            n_save();
        }
    }

    public void stop() {
        if (!this.bClient) {
            this.bStopped = true;
            n_stop();
            this.loadedAreas.clear();
            this.newChunks.clear();
            this.spawnOrigins.clear();
            this.radarXY = null;
            this.radarCount = 0;
            this.radarRenderFlag = false;
            this.radarRequestFlag = false;
        }
    }
}
