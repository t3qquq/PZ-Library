// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.erosion;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.utils.Bits;
import zombie.debug.DebugLog;
import zombie.erosion.season.ErosionIceQueen;
import zombie.erosion.season.ErosionSeason;
import zombie.erosion.utils.Noise2D;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;

public final class ErosionMain {
    private static ErosionMain instance;
    private ErosionConfig cfg;
    private boolean debug;
    private IsoSpriteManager sprMngr;
    private ErosionIceQueen IceQueen;
    private boolean isSnow;
    private String world;
    private String cfgPath;
    private IsoChunk chunk;
    private ErosionData.Chunk chunkModData;
    private Noise2D noiseMain;
    private Noise2D noiseMoisture;
    private Noise2D noiseMinerals;
    private Noise2D noiseKudzu;
    private ErosionWorld World;
    private ErosionSeason Season;
    private int tickUnit = 144;
    private int ticks = 0;
    private int eTicks = 0;
    private int day = 0;
    private int month = 0;
    private int year = 0;
    private int epoch = 0;
    private static final int[][] soilTable = new int[][]{
        {1, 1, 1, 1, 1, 4, 4, 4, 4, 4},
        {1, 1, 1, 1, 2, 5, 4, 4, 4, 4},
        {1, 1, 1, 2, 2, 5, 5, 4, 4, 4},
        {1, 1, 2, 2, 3, 6, 5, 5, 4, 4},
        {1, 2, 2, 3, 3, 6, 6, 5, 5, 4},
        {7, 8, 8, 9, 9, 12, 12, 11, 11, 10},
        {7, 7, 8, 8, 9, 12, 11, 11, 10, 10},
        {7, 7, 7, 8, 8, 11, 11, 10, 10, 10},
        {7, 7, 7, 7, 8, 11, 10, 10, 10, 10},
        {7, 7, 7, 7, 7, 10, 10, 10, 10, 10}
    };
    private int snowFrac = 0;
    private int snowFracYesterday = 0;
    private int[] snowFracOnDay;

    public static ErosionMain getInstance() {
        return instance;
    }

    public ErosionMain(IsoSpriteManager _sprMngr, boolean _debug) {
        instance = this;
        this.sprMngr = _sprMngr;
        this.debug = _debug;
        this.start();
    }

    public ErosionConfig getConfig() {
        return this.cfg;
    }

    public ErosionSeason getSeasons() {
        return this.Season;
    }

    public int getEtick() {
        return this.eTicks;
    }

    public IsoSpriteManager getSpriteManager() {
        return this.sprMngr;
    }

    public void mainTimer() {
        if (GameClient.bClient) {
            if (Core.bDebug) {
                this.cfg.writeFile(this.cfgPath);
            }
        } else {
            int int0 = SandboxOptions.instance.ErosionDays.getValue();
            if (this.debug) {
                this.eTicks++;
            } else if (int0 < 0) {
                this.eTicks = 0;
            } else if (int0 > 0) {
                this.ticks++;
                this.eTicks = (int)(this.ticks / 144.0F / int0 * 100.0F);
            } else {
                this.ticks++;
                if (this.ticks >= this.tickUnit) {
                    this.ticks = 0;
                    this.eTicks++;
                }
            }

            if (this.eTicks < 0) {
                this.eTicks = Integer.MAX_VALUE;
            }

            GameTime gameTime = GameTime.getInstance();
            if (gameTime.getDay() != this.day || gameTime.getMonth() != this.month || gameTime.getYear() != this.year) {
                this.month = gameTime.getMonth();
                this.year = gameTime.getYear();
                this.day = gameTime.getDay();
                this.epoch++;
                this.Season.setDay(this.day, this.month, this.year);
                this.snowCheck();
            }

            if (GameServer.bServer) {
                for (int int1 = 0; int1 < ServerMap.instance.LoadedCells.size(); int1++) {
                    ServerMap.ServerCell serverCell = ServerMap.instance.LoadedCells.get(int1);
                    if (serverCell.bLoaded) {
                        for (int int2 = 0; int2 < 5; int2++) {
                            for (int int3 = 0; int3 < 5; int3++) {
                                IsoChunk chunk0 = serverCell.chunks[int3][int2];
                                if (chunk0 != null) {
                                    ErosionData.Chunk chunk1 = chunk0.getErosionData();
                                    if (chunk1.eTickStamp != this.eTicks || chunk1.epoch != this.epoch) {
                                        for (int int4 = 0; int4 < 10; int4++) {
                                            for (int int5 = 0; int5 < 10; int5++) {
                                                IsoGridSquare square = chunk0.getGridSquare(int5, int4, 0);
                                                if (square != null) {
                                                    this.loadGridsquare(square);
                                                }
                                            }
                                        }

                                        chunk1.eTickStamp = this.eTicks;
                                        chunk1.epoch = this.epoch;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            this.cfg.time.ticks = this.ticks;
            this.cfg.time.eticks = this.eTicks;
            this.cfg.time.epoch = this.epoch;
            this.cfg.writeFile(this.cfgPath);
        }
    }

    public void snowCheck() {
    }

    public int getSnowFraction() {
        return this.snowFrac;
    }

    public int getSnowFractionYesterday() {
        return this.snowFracYesterday;
    }

    public boolean isSnow() {
        return this.isSnow;
    }

    public void sendState(ByteBuffer bb) {
        if (GameServer.bServer) {
            bb.putInt(this.eTicks);
            bb.putInt(this.ticks);
            bb.putInt(this.epoch);
            bb.put((byte)this.getSnowFraction());
            bb.put((byte)this.getSnowFractionYesterday());
            bb.putFloat(GameTime.getInstance().getTimeOfDay());
        }
    }

    public void receiveState(ByteBuffer bb) {
        if (GameClient.bClient) {
            int int0 = this.eTicks;
            int int1 = this.epoch;
            this.eTicks = bb.getInt();
            this.ticks = bb.getInt();
            this.epoch = bb.getInt();
            this.cfg.time.ticks = this.ticks;
            this.cfg.time.eticks = this.eTicks;
            this.cfg.time.epoch = this.epoch;
            byte byte0 = bb.get();
            byte byte1 = bb.get();
            float float0 = bb.getFloat();
            GameTime gameTime = GameTime.getInstance();
            if (gameTime.getDay() != this.day || gameTime.getMonth() != this.month || gameTime.getYear() != this.year) {
                this.month = gameTime.getMonth();
                this.year = gameTime.getYear();
                this.day = gameTime.getDay();
                this.Season.setDay(this.day, this.month, this.year);
            }

            if (int0 != this.eTicks || int1 != this.epoch) {
                this.updateMapNow();
            }
        }
    }

    private void loadGridsquare(IsoGridSquare square0) {
        if (square0 != null && square0.chunk != null && square0.getZ() == 0) {
            this.getChunk(square0);
            ErosionData.Square square1 = square0.getErosionData();
            if (!square1.init) {
                this.initGridSquare(square0, square1);
                this.World.validateSpawn(square0, square1, this.chunkModData);
            }

            if (square1.doNothing) {
                return;
            }

            if (this.chunkModData.eTickStamp >= this.eTicks && this.chunkModData.epoch == this.epoch) {
                return;
            }

            this.World.update(square0, square1, this.chunkModData, this.eTicks);
        }
    }

    private void initGridSquare(IsoGridSquare square0, ErosionData.Square square1) {
        int int0 = square0.getX();
        int int1 = square0.getY();
        float float0 = this.noiseMain.layeredNoise(int0 / 10.0F, int1 / 10.0F);
        square1.noiseMainByte = Bits.packFloatUnitToByte(float0);
        square1.noiseMain = float0;
        square1.noiseMainInt = (int)Math.floor(square1.noiseMain * 100.0F);
        square1.noiseKudzu = this.noiseKudzu.layeredNoise(int0 / 10.0F, int1 / 10.0F);
        square1.soil = this.chunkModData.soil;
        float float1 = square1.rand(int0, int1, 100) / 100.0F;
        square1.magicNumByte = Bits.packFloatUnitToByte(float1);
        square1.magicNum = float1;
        square1.regions.clear();
        square1.init = true;
    }

    private void getChunk(IsoGridSquare square) {
        this.chunk = square.getChunk();
        this.chunkModData = this.chunk.getErosionData();
        if (!this.chunkModData.init) {
            this.initChunk(this.chunk, this.chunkModData);
        }
    }

    private void initChunk(IsoChunk chunk1, ErosionData.Chunk chunk0) {
        chunk0.set(chunk1);
        float float0 = chunk0.x / 5.0F;
        float float1 = chunk0.y / 5.0F;
        float float2 = this.noiseMoisture.layeredNoise(float0, float1);
        float float3 = this.noiseMinerals.layeredNoise(float0, float1);
        int int0 = float2 < 1.0F ? (int)Math.floor(float2 * 10.0F) : 9;
        int int1 = float3 < 1.0F ? (int)Math.floor(float3 * 10.0F) : 9;
        chunk0.init = true;
        chunk0.eTickStamp = -1;
        chunk0.epoch = -1;
        chunk0.moisture = float2;
        chunk0.minerals = float3;
        chunk0.soil = soilTable[int0][int1] - 1;
    }

    private boolean initConfig() {
        String string = "erosion.ini";
        if (GameClient.bClient) {
            this.cfg = GameClient.instance.erosionConfig;

            assert this.cfg != null;

            GameClient.instance.erosionConfig = null;
            this.cfgPath = ZomboidFileSystem.instance.getFileNameInCurrentSave(string);
            return true;
        } else {
            this.cfg = new ErosionConfig();
            this.cfgPath = ZomboidFileSystem.instance.getFileNameInCurrentSave(string);
            File file0 = new File(this.cfgPath);
            if (file0.exists()) {
                DebugLog.log("erosion: reading " + file0.getAbsolutePath());
                if (this.cfg.readFile(file0.getAbsolutePath())) {
                    return true;
                }

                this.cfg = new ErosionConfig();
            }

            file0 = new File(ZomboidFileSystem.instance.getCacheDir() + File.separator + string);
            if (!file0.exists() && !Core.getInstance().isNoSave()) {
                File file1 = ZomboidFileSystem.instance.getMediaFile("data" + File.separator + string);
                if (file1.exists()) {
                    try {
                        DebugLog.log("erosion: copying " + file1.getAbsolutePath() + " to " + file0.getAbsolutePath());
                        Files.copy(file1.toPath(), file0.toPath());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }

            if (file0.exists()) {
                DebugLog.log("erosion: reading " + file0.getAbsolutePath());
                if (!this.cfg.readFile(file0.getAbsolutePath())) {
                    this.cfg = new ErosionConfig();
                }
            }

            int int0 = SandboxOptions.instance.getErosionSpeed();
            switch (int0) {
                case 1:
                    this.cfg.time.tickunit /= 5;
                    break;
                case 2:
                    this.cfg.time.tickunit /= 2;
                case 3:
                default:
                    break;
                case 4:
                    this.cfg.time.tickunit *= 2;
                    break;
                case 5:
                    this.cfg.time.tickunit *= 5;
            }

            float float0 = this.cfg.time.tickunit * 100 / 144.0F;
            float float1 = (SandboxOptions.instance.TimeSinceApo.getValue() - 1) * 30;
            this.cfg.time.eticks = (int)Math.floor(Math.min(1.0F, float1 / float0) * 100.0F);
            int int1 = SandboxOptions.instance.ErosionDays.getValue();
            if (int1 > 0) {
                this.cfg.time.tickunit = 144;
                this.cfg.time.eticks = (int)Math.floor(Math.min(1.0F, float1 / int1) * 100.0F);
            }

            return true;
        }
    }

    public void start() {
        if (this.initConfig()) {
            this.world = Core.GameSaveWorld;
            this.tickUnit = this.cfg.time.tickunit;
            this.ticks = this.cfg.time.ticks;
            this.eTicks = this.cfg.time.eticks;
            this.month = GameTime.getInstance().getMonth();
            this.year = GameTime.getInstance().getYear();
            this.day = GameTime.getInstance().getDay();
            this.debug = !GameServer.bServer && this.cfg.debug.enabled;
            this.cfg.consolePrint();
            this.noiseMain = new Noise2D();
            this.noiseMain.addLayer(this.cfg.seeds.seedMain_0, 0.5F, 3.0F);
            this.noiseMain.addLayer(this.cfg.seeds.seedMain_1, 2.0F, 5.0F);
            this.noiseMain.addLayer(this.cfg.seeds.seedMain_2, 5.0F, 8.0F);
            this.noiseMoisture = new Noise2D();
            this.noiseMoisture.addLayer(this.cfg.seeds.seedMoisture_0, 2.0F, 3.0F);
            this.noiseMoisture.addLayer(this.cfg.seeds.seedMoisture_1, 1.6F, 5.0F);
            this.noiseMoisture.addLayer(this.cfg.seeds.seedMoisture_2, 0.6F, 8.0F);
            this.noiseMinerals = new Noise2D();
            this.noiseMinerals.addLayer(this.cfg.seeds.seedMinerals_0, 2.0F, 3.0F);
            this.noiseMinerals.addLayer(this.cfg.seeds.seedMinerals_1, 1.6F, 5.0F);
            this.noiseMinerals.addLayer(this.cfg.seeds.seedMinerals_2, 0.6F, 8.0F);
            this.noiseKudzu = new Noise2D();
            this.noiseKudzu.addLayer(this.cfg.seeds.seedKudzu_0, 6.0F, 3.0F);
            this.noiseKudzu.addLayer(this.cfg.seeds.seedKudzu_1, 3.0F, 5.0F);
            this.noiseKudzu.addLayer(this.cfg.seeds.seedKudzu_2, 0.5F, 8.0F);
            this.Season = new ErosionSeason();
            ErosionConfig.Season season = this.cfg.season;
            int int0 = season.tempMin;
            int int1 = season.tempMax;
            if (SandboxOptions.instance.getTemperatureModifier() == 1) {
                int0 -= 10;
                int1 -= 10;
            } else if (SandboxOptions.instance.getTemperatureModifier() == 2) {
                int0 -= 5;
                int1 -= 5;
            } else if (SandboxOptions.instance.getTemperatureModifier() == 4) {
                int0 = (int)(int0 + 7.5);
                int1 += 4;
            } else if (SandboxOptions.instance.getTemperatureModifier() == 5) {
                int0 += 15;
                int1 += 8;
            }

            this.Season.init(season.lat, int1, int0, season.tempDiff, season.seasonLag, season.noon, season.seedA, season.seedB, season.seedC);
            this.Season
                .setRain(
                    season.jan,
                    season.feb,
                    season.mar,
                    season.apr,
                    season.may,
                    season.jun,
                    season.jul,
                    season.aug,
                    season.sep,
                    season.oct,
                    season.nov,
                    season.dec
                );
            this.Season.setDay(this.day, this.month, this.year);
            LuaEventManager.triggerEvent("OnInitSeasons", this.Season);
            this.IceQueen = new ErosionIceQueen(this.sprMngr);
            this.World = new ErosionWorld();
            if (this.World.init()) {
                this.snowCheck();
                if (this.debug) {
                }

                if (GameServer.bServer) {
                }
            }
        }
    }

    private void loadChunk(IsoChunk chunk1) {
        ErosionData.Chunk chunk0 = chunk1.getErosionData();
        if (!chunk0.init) {
            this.initChunk(chunk1, chunk0);
        }

        chunk0.eTickStamp = this.eTicks;
        chunk0.epoch = this.epoch;
    }

    public void DebugUpdateMapNow() {
        this.updateMapNow();
    }

    private void updateMapNow() {
        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.getChunkMap(int0);
            if (!chunkMap.ignore) {
                IsoChunkMap.bSettingChunk.lock();

                try {
                    for (int int1 = 0; int1 < IsoChunkMap.ChunkGridWidth; int1++) {
                        for (int int2 = 0; int2 < IsoChunkMap.ChunkGridWidth; int2++) {
                            IsoChunk chunk0 = chunkMap.getChunk(int2, int1);
                            if (chunk0 != null) {
                                ErosionData.Chunk chunk1 = chunk0.getErosionData();
                                if (chunk1.eTickStamp != this.eTicks || chunk1.epoch != this.epoch) {
                                    for (int int3 = 0; int3 < 10; int3++) {
                                        for (int int4 = 0; int4 < 10; int4++) {
                                            IsoGridSquare square = chunk0.getGridSquare(int4, int3, 0);
                                            if (square != null) {
                                                this.loadGridsquare(square);
                                            }
                                        }
                                    }

                                    chunk1.eTickStamp = this.eTicks;
                                    chunk1.epoch = this.epoch;
                                }
                            }
                        }
                    }
                } finally {
                    IsoChunkMap.bSettingChunk.unlock();
                }
            }
        }
    }

    public static void LoadGridsquare(IsoGridSquare _sq) {
        instance.loadGridsquare(_sq);
    }

    public static void ChunkLoaded(IsoChunk _chunk) {
        instance.loadChunk(_chunk);
    }

    public static void EveryTenMinutes() {
        instance.mainTimer();
    }

    public static void Reset() {
        instance = null;
    }
}
