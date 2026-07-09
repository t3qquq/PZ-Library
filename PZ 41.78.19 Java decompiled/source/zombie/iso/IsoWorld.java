// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import fmod.fmod.FMODSoundEmitter;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.krka.kahlua.vm.KahluaTable;
import zombie.CollisionManager;
import zombie.DebugFileWatcher;
import zombie.FliesSound;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.MapCollisionData;
import zombie.PersistentOutfits;
import zombie.PredicatedFileWatcher;
import zombie.ReanimatedPlayers;
import zombie.SandboxOptions;
import zombie.SharedDescriptors;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZomboidFileSystem;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.ZombieGroupManager;
import zombie.ai.states.FakeDeadZombieState;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.DummySoundEmitter;
import zombie.audio.ObjectAmbientEmitters;
import zombie.characters.HaloTextHelper;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorDesc;
import zombie.characters.TriggerSetAnimationRecorderFile;
import zombie.characters.ZombieVocalsManager;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.traits.TraitFactory;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.TilePropertyAliasMap;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.physics.WorldSimulation;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.properties.PropertyContainer;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.skinnedmodel.model.WorldItemAtlas;
import zombie.core.stash.StashSystem;
import zombie.core.textures.Texture;
import zombie.core.utils.OnceEvery;
import zombie.debug.DebugLog;
import zombie.debug.LineDrawer;
import zombie.erosion.ErosionGlobals;
import zombie.gameStates.GameLoadingState;
import zombie.globalObjects.GlobalObjectLookup;
import zombie.input.Mouse;
import zombie.inventory.ItemPickerJava;
import zombie.inventory.types.MapItem;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.SafeHouse;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.ObjectRenderEffects;
import zombie.iso.objects.RainManager;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteGrid;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.sprite.SkyBox;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.WorldFlares;
import zombie.iso.weather.fog.ImprovedFog;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.network.BodyDamageSync;
import zombie.network.ClientServerMap;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.NetChecksum;
import zombie.network.PassengerMap;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.popman.ZombiePopulationManager;
import zombie.radio.ZomboidRadio;
import zombie.randomizedWorld.randomizedBuilding.RBBar;
import zombie.randomizedWorld.randomizedBuilding.RBBasic;
import zombie.randomizedWorld.randomizedBuilding.RBBurnt;
import zombie.randomizedWorld.randomizedBuilding.RBBurntCorpse;
import zombie.randomizedWorld.randomizedBuilding.RBBurntFireman;
import zombie.randomizedWorld.randomizedBuilding.RBCafe;
import zombie.randomizedWorld.randomizedBuilding.RBClinic;
import zombie.randomizedWorld.randomizedBuilding.RBHairSalon;
import zombie.randomizedWorld.randomizedBuilding.RBKateAndBaldspot;
import zombie.randomizedWorld.randomizedBuilding.RBLooted;
import zombie.randomizedWorld.randomizedBuilding.RBOffice;
import zombie.randomizedWorld.randomizedBuilding.RBOther;
import zombie.randomizedWorld.randomizedBuilding.RBPileOCrepe;
import zombie.randomizedWorld.randomizedBuilding.RBPizzaWhirled;
import zombie.randomizedWorld.randomizedBuilding.RBSafehouse;
import zombie.randomizedWorld.randomizedBuilding.RBSchool;
import zombie.randomizedWorld.randomizedBuilding.RBShopLooted;
import zombie.randomizedWorld.randomizedBuilding.RBSpiffo;
import zombie.randomizedWorld.randomizedBuilding.RBStripclub;
import zombie.randomizedWorld.randomizedBuilding.RandomizedBuildingBase;
import zombie.randomizedWorld.randomizedVehicleStory.RVSAmbulanceCrash;
import zombie.randomizedWorld.randomizedVehicleStory.RVSBanditRoad;
import zombie.randomizedWorld.randomizedVehicleStory.RVSBurntCar;
import zombie.randomizedWorld.randomizedVehicleStory.RVSCarCrash;
import zombie.randomizedWorld.randomizedVehicleStory.RVSCarCrashCorpse;
import zombie.randomizedWorld.randomizedVehicleStory.RVSChangingTire;
import zombie.randomizedWorld.randomizedVehicleStory.RVSConstructionSite;
import zombie.randomizedWorld.randomizedVehicleStory.RVSCrashHorde;
import zombie.randomizedWorld.randomizedVehicleStory.RVSFlippedCrash;
import zombie.randomizedWorld.randomizedVehicleStory.RVSPoliceBlockade;
import zombie.randomizedWorld.randomizedVehicleStory.RVSPoliceBlockadeShooting;
import zombie.randomizedWorld.randomizedVehicleStory.RVSTrailerCrash;
import zombie.randomizedWorld.randomizedVehicleStory.RVSUtilityVehicle;
import zombie.randomizedWorld.randomizedVehicleStory.RandomizedVehicleStoryBase;
import zombie.randomizedWorld.randomizedZoneStory.RZSBBQParty;
import zombie.randomizedWorld.randomizedZoneStory.RZSBaseball;
import zombie.randomizedWorld.randomizedZoneStory.RZSBeachParty;
import zombie.randomizedWorld.randomizedZoneStory.RZSBuryingCamp;
import zombie.randomizedWorld.randomizedZoneStory.RZSFishingTrip;
import zombie.randomizedWorld.randomizedZoneStory.RZSForestCamp;
import zombie.randomizedWorld.randomizedZoneStory.RZSForestCampEaten;
import zombie.randomizedWorld.randomizedZoneStory.RZSHunterCamp;
import zombie.randomizedWorld.randomizedZoneStory.RZSMusicFest;
import zombie.randomizedWorld.randomizedZoneStory.RZSMusicFestStage;
import zombie.randomizedWorld.randomizedZoneStory.RZSSexyTime;
import zombie.randomizedWorld.randomizedZoneStory.RZSTrapperCamp;
import zombie.randomizedWorld.randomizedZoneStory.RandomizedZoneStoryBase;
import zombie.savefile.ClientPlayerDB;
import zombie.savefile.PlayerDB;
import zombie.savefile.PlayerDBHelper;
import zombie.savefile.ServerPlayerDB;
import zombie.text.templating.TemplateText;
import zombie.ui.TutorialManager;
import zombie.util.AddCoopPlayer;
import zombie.util.SharedStrings;
import zombie.util.Type;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleIDMap;
import zombie.vehicles.VehicleManager;
import zombie.vehicles.VehiclesDB2;
import zombie.world.WorldDictionary;
import zombie.world.WorldDictionaryException;
import zombie.world.moddata.GlobalModData;

public final class IsoWorld {
    private String weather = "sunny";
    public final IsoMetaGrid MetaGrid = new IsoMetaGrid();
    private final ArrayList<RandomizedBuildingBase> randomizedBuildingList = new ArrayList<>();
    private final ArrayList<RandomizedZoneStoryBase> randomizedZoneList = new ArrayList<>();
    private final ArrayList<RandomizedVehicleStoryBase> randomizedVehicleStoryList = new ArrayList<>();
    private final RandomizedBuildingBase RBBasic = new RBBasic();
    private final HashMap<String, ArrayList<Double>> spawnedZombieZone = new HashMap<>();
    private final HashMap<String, ArrayList<String>> allTiles = new HashMap<>();
    private final ArrayList<String> tileImages = new ArrayList<>();
    private float flashIsoCursorA = 1.0F;
    private boolean flashIsoCursorInc = false;
    public SkyBox sky = null;
    private static PredicatedFileWatcher m_setAnimationRecordingTriggerWatcher;
    private static boolean m_animationRecorderActive = false;
    private static boolean m_animationRecorderDiscard = false;
    private int timeSinceLastSurvivorInHorde = 4000;
    private int m_frameNo = 0;
    public final Helicopter helicopter = new Helicopter();
    private boolean bHydroPowerOn = false;
    public final ArrayList<IsoGameCharacter> Characters = new ArrayList<>();
    private final ArrayDeque<BaseSoundEmitter> freeEmitters = new ArrayDeque<>();
    private final ArrayList<BaseSoundEmitter> currentEmitters = new ArrayList<>();
    private final HashMap<BaseSoundEmitter, IsoObject> emitterOwners = new HashMap<>();
    public int x = 50;
    public int y = 50;
    public IsoCell CurrentCell;
    public static IsoWorld instance = new IsoWorld();
    public int TotalSurvivorsDead = 0;
    public int TotalSurvivorNights = 0;
    public int SurvivorSurvivalRecord = 0;
    public HashMap<Integer, SurvivorDesc> SurvivorDescriptors = new HashMap<>();
    public ArrayList<AddCoopPlayer> AddCoopPlayers = new ArrayList<>();
    private static final IsoWorld.CompScoreToPlayer compScoreToPlayer = new IsoWorld.CompScoreToPlayer();
    static IsoWorld.CompDistToPlayer compDistToPlayer = new IsoWorld.CompDistToPlayer();
    public static String mapPath = "media/";
    public static boolean mapUseJar = true;
    boolean bLoaded = false;
    public static final HashMap<String, ArrayList<String>> PropertyValueMap = new HashMap<>();
    private static int WorldX = 0;
    private static int WorldY = 0;
    private SurvivorDesc luaDesc;
    private ArrayList<String> luatraits;
    private int luaSpawnCellX = -1;
    private int luaSpawnCellY = -1;
    private int luaPosX = -1;
    private int luaPosY = -1;
    private int luaPosZ = -1;
    public static final int WorldVersion = 195;
    public static final int WorldVersion_Barricade = 87;
    public static final int WorldVersion_SandboxOptions = 88;
    public static final int WorldVersion_FliesSound = 121;
    public static final int WorldVersion_LootRespawn = 125;
    public static final int WorldVersion_OverlappingGenerators = 127;
    public static final int WorldVersion_ItemContainerIdenticalItems = 128;
    public static final int WorldVersion_VehicleSirenStartTime = 129;
    public static final int WorldVersion_CompostLastUpdated = 130;
    public static final int WorldVersion_DayLengthHours = 131;
    public static final int WorldVersion_LampOnPillar = 132;
    public static final int WorldVersion_AlarmClockRingSince = 134;
    public static final int WorldVersion_ClimateAdded = 135;
    public static final int WorldVersion_VehicleLightFocusing = 135;
    public static final int WorldVersion_GeneratorFuelFloat = 138;
    public static final int WorldVersion_InfectionTime = 142;
    public static final int WorldVersion_ClimateColors = 143;
    public static final int WorldVersion_BodyLocation = 144;
    public static final int WorldVersion_CharacterModelData = 145;
    public static final int WorldVersion_CharacterModelData2 = 146;
    public static final int WorldVersion_CharacterModelData3 = 147;
    public static final int WorldVersion_HumanVisualBlood = 148;
    public static final int WorldVersion_ItemContainerIdenticalItemsInt = 149;
    public static final int WorldVersion_PerkName = 152;
    public static final int WorldVersion_Thermos = 153;
    public static final int WorldVersion_AllPatches = 155;
    public static final int WorldVersion_ZombieRotStage = 156;
    public static final int WorldVersion_NewSandboxLootModifier = 157;
    public static final int WorldVersion_KateBobStorm = 158;
    public static final int WorldVersion_DeadBodyAngle = 159;
    public static final int WorldVersion_ChunkSpawnedRooms = 160;
    public static final int WorldVersion_DeathDragDown = 161;
    public static final int WorldVersion_CanUpgradePerk = 162;
    public static final int WorldVersion_ItemVisualFullType = 164;
    public static final int WorldVersion_VehicleBlood = 165;
    public static final int WorldVersion_DeadBodyZombieRotStage = 166;
    public static final int WorldVersion_Fitness = 167;
    public static final int WorldVersion_DeadBodyFakeDead = 168;
    public static final int WorldVersion_Fitness2 = 169;
    public static final int WorldVersion_NewFog = 170;
    public static final int WorldVersion_DeadBodyPersistentOutfitID = 171;
    public static final int WorldVersion_VehicleTowingID = 172;
    public static final int WorldVersion_VehicleJNITransform = 173;
    public static final int WorldVersion_VehicleTowAttachment = 174;
    public static final int WorldVersion_ContainerMaxCapacity = 175;
    public static final int WorldVersion_TimedActionInstantCheat = 176;
    public static final int WorldVersion_ClothingPatchSaveLoad = 178;
    public static final int WorldVersion_AttachedSlotType = 179;
    public static final int WorldVersion_NoiseMakerDuration = 180;
    public static final int WorldVersion_ChunkVehicles = 91;
    public static final int WorldVersion_PlayerVehicleSeat = 91;
    public static final int WorldVersion_MediaDisksAndTapes = 181;
    public static final int WorldVersion_AlreadyReadBooks1 = 182;
    public static final int WorldVersion_LampOnPillar2 = 183;
    public static final int WorldVersion_AlreadyReadBooks2 = 184;
    public static final int WorldVersion_PolygonZone = 185;
    public static final int WorldVersion_PolylineZone = 186;
    public static final int WorldVersion_NaturalHairBeardColor = 187;
    public static final int WorldVersion_CruiseSpeedSaving = 188;
    public static final int WorldVersion_KnownMediaLines = 189;
    public static final int WorldVersion_DeadBodyAtlas = 190;
    public static final int WorldVersion_Scarecrow = 191;
    public static final int WorldVersion_DeadBodyID = 192;
    public static final int WorldVersion_IgnoreRemoveSandbox = 193;
    public static final int WorldVersion_MapMetaBounds = 194;
    public static final int WorldVersion_PreviouslyEntered = 195;
    public static int SavedWorldVersion = -1;
    private boolean bDrawWorld = true;
    private final ArrayList<IsoZombie> zombieWithModel = new ArrayList<>();
    private final ArrayList<IsoZombie> zombieWithoutModel = new ArrayList<>();
    public static boolean NoZombies = false;
    public static int TotalWorldVersion = -1;
    public static int saveoffsetx;
    public static int saveoffsety;
    public boolean bDoChunkMapUpdate = true;
    private long emitterUpdateMS;
    public boolean emitterUpdate;
    private int updateSafehousePlayers = 200;

    public IsoMetaGrid getMetaGrid() {
        return this.MetaGrid;
    }

    public IsoMetaGrid.Zone registerZone(String name, String type, int _x, int _y, int z, int width, int height) {
        return this.MetaGrid.registerZone(name, type, _x, _y, z, width, height);
    }

    public IsoMetaGrid.Zone registerZoneNoOverlap(String name, String type, int _x, int _y, int z, int width, int height) {
        return this.MetaGrid.registerZoneNoOverlap(name, type, _x, _y, z, width, height);
    }

    public void removeZonesForLotDirectory(String lotDir) {
        this.MetaGrid.removeZonesForLotDirectory(lotDir);
    }

    public BaseSoundEmitter getFreeEmitter() {
        Object object = null;
        if (this.freeEmitters.isEmpty()) {
            object = Core.SoundDisabled ? new DummySoundEmitter() : new FMODSoundEmitter();
        } else {
            object = this.freeEmitters.pop();
        }

        this.currentEmitters.add((BaseSoundEmitter)object);
        return (BaseSoundEmitter)object;
    }

    public BaseSoundEmitter getFreeEmitter(float _x, float _y, float z) {
        BaseSoundEmitter baseSoundEmitter = this.getFreeEmitter();
        baseSoundEmitter.setPos(_x, _y, z);
        return baseSoundEmitter;
    }

    public void takeOwnershipOfEmitter(BaseSoundEmitter emitter) {
        this.currentEmitters.remove(emitter);
    }

    public void setEmitterOwner(BaseSoundEmitter emitter, IsoObject object) {
        if (emitter != null && object != null) {
            if (!this.emitterOwners.containsKey(emitter)) {
                this.emitterOwners.put(emitter, object);
            }
        }
    }

    public void returnOwnershipOfEmitter(BaseSoundEmitter emitter) {
        if (emitter != null) {
            if (!this.currentEmitters.contains(emitter) && !this.freeEmitters.contains(emitter)) {
                if (emitter.isEmpty()) {
                    FMODSoundEmitter fMODSoundEmitter = Type.tryCastTo(emitter, FMODSoundEmitter.class);
                    if (fMODSoundEmitter != null) {
                        fMODSoundEmitter.clearParameters();
                    }

                    this.freeEmitters.add(emitter);
                } else {
                    this.currentEmitters.add(emitter);
                }
            }
        }
    }

    public IsoMetaGrid.Zone registerVehiclesZone(String name, String type, int _x, int _y, int z, int width, int height, KahluaTable properties) {
        return this.MetaGrid.registerVehiclesZone(name, type, _x, _y, z, width, height, properties);
    }

    public IsoMetaGrid.Zone registerMannequinZone(String name, String type, int _x, int _y, int z, int width, int height, KahluaTable properties) {
        return this.MetaGrid.registerMannequinZone(name, type, _x, _y, z, width, height, properties);
    }

    public void registerRoomTone(String name, String type, int _x, int _y, int z, int width, int height, KahluaTable properties) {
        this.MetaGrid.registerRoomTone(name, type, _x, _y, z, width, height, properties);
    }

    public void registerSpawnOrigin(int _x, int _y, int width, int height, KahluaTable properties) {
        ZombiePopulationManager.instance.registerSpawnOrigin(_x, _y, width, height, properties);
    }

    public void registerWaterFlow(float _x, float _y, float flow, float speed) {
        IsoWaterFlow.addFlow(_x, _y, flow, speed);
    }

    public void registerWaterZone(float x1, float y1, float x2, float y2, float shore, float water_ground) {
        IsoWaterFlow.addZone(x1, y1, x2, y2, shore, water_ground);
    }

    public void checkVehiclesZones() {
        this.MetaGrid.checkVehiclesZones();
    }

    public void setGameMode(String mode) {
        Core.GameMode = mode;
        Core.bLastStand = "LastStand".equals(mode);
        Core.getInstance().setChallenge(false);
        Core.ChallengeID = null;
    }

    public String getGameMode() {
        return Core.GameMode;
    }

    public void setWorld(String world) {
        Core.GameSaveWorld = world.trim();
    }

    public void setMap(String world) {
        Core.GameMap = world;
    }

    public String getMap() {
        return Core.GameMap;
    }

    public void renderTerrain() {
    }

    public int getFrameNo() {
        return this.m_frameNo;
    }

    public IsoObject getItemFromXYZIndexBuffer(ByteBuffer bb) {
        int int0 = bb.getInt();
        int int1 = bb.getInt();
        int int2 = bb.getInt();
        IsoGridSquare square = this.CurrentCell.getGridSquare(int0, int1, int2);
        if (square == null) {
            return null;
        } else {
            byte byte0 = bb.get();
            return byte0 >= 0 && byte0 < square.getObjects().size() ? square.getObjects().get(byte0) : null;
        }
    }

    public IsoWorld() {
        if (!GameServer.bServer) {
        }
    }

    private static void initMessaging() {
        if (m_setAnimationRecordingTriggerWatcher == null) {
            m_setAnimationRecordingTriggerWatcher = new PredicatedFileWatcher(
                ZomboidFileSystem.instance.getMessagingDirSub("Trigger_AnimationRecorder.xml"),
                TriggerSetAnimationRecorderFile.class,
                IsoWorld::onTrigger_setAnimationRecorderTriggerFile
            );
            DebugFileWatcher.instance.add(m_setAnimationRecordingTriggerWatcher);
        }
    }

    private static void onTrigger_setAnimationRecorderTriggerFile(TriggerSetAnimationRecorderFile triggerSetAnimationRecorderFile) {
        m_animationRecorderActive = triggerSetAnimationRecorderFile.isRecording;
        m_animationRecorderDiscard = triggerSetAnimationRecorderFile.discard;
    }

    public static boolean isAnimRecorderActive() {
        return m_animationRecorderActive;
    }

    public static boolean isAnimRecorderDiscardTriggered() {
        return m_animationRecorderDiscard;
    }

    public IsoSurvivor CreateRandomSurvivor(SurvivorDesc desc, IsoGridSquare sq, IsoPlayer player) {
        return null;
    }

    public void CreateSwarm(int num, int x1, int y1, int x2, int y2) {
    }

    public void ForceKillAllZombies() {
        GameTime.getInstance().RemoveZombiesIndiscriminate(1000);
    }

    public static int readInt(RandomAccessFile __in__) throws EOFException, IOException {
        int int0 = __in__.read();
        int int1 = __in__.read();
        int int2 = __in__.read();
        int int3 = __in__.read();
        if ((int0 | int1 | int2 | int3) < 0) {
            throw new EOFException();
        } else {
            return (int0 << 0) + (int1 << 8) + (int2 << 16) + (int3 << 24);
        }
    }

    public static String readString(RandomAccessFile __in__) throws EOFException, IOException {
        return __in__.readLine();
    }

    public static int readInt(InputStream __in__) throws EOFException, IOException {
        int int0 = __in__.read();
        int int1 = __in__.read();
        int int2 = __in__.read();
        int int3 = __in__.read();
        if ((int0 | int1 | int2 | int3) < 0) {
            throw new EOFException();
        } else {
            return (int0 << 0) + (int1 << 8) + (int2 << 16) + (int3 << 24);
        }
    }

    public static String readString(InputStream __in__) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        int int0 = -1;
        boolean boolean0 = false;

        while (!boolean0) {
            switch (int0 = __in__.read()) {
                case -1:
                case 10:
                    boolean0 = true;
                    break;
                case 13:
                    throw new IllegalStateException("\r\n unsupported");
                default:
                    stringBuilder.append((char)int0);
            }
        }

        return int0 == -1 && stringBuilder.length() == 0 ? null : stringBuilder.toString();
    }

    public void LoadTileDefinitions(IsoSpriteManager sprMan, String filename, int fileNumber) {
        DebugLog.log("tiledef: loading " + filename);
        boolean boolean0 = filename.endsWith(".patch.tiles");

        try (
            FileInputStream fileInputStream = new FileInputStream(filename);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        ) {
            int int0 = readInt(bufferedInputStream);
            int int1 = readInt(bufferedInputStream);
            int int2 = readInt(bufferedInputStream);
            SharedStrings sharedStrings = new SharedStrings();
            boolean boolean1 = false;
            boolean boolean2 = false;
            boolean boolean3 = Core.bDebug && Translator.getLanguage() == Translator.getDefaultLanguage();
            ArrayList arrayList0 = new ArrayList();
            HashMap hashMap0 = new HashMap();
            HashMap hashMap1 = new HashMap();
            String[] strings0 = new String[]{"N", "E", "S", "W"};

            for (int int3 = 0; int3 < strings0.length; int3++) {
                hashMap1.put(strings0[int3], new ArrayList());
            }

            ArrayList arrayList1 = new ArrayList();
            HashMap hashMap2 = new HashMap();
            int int4 = 0;
            int int5 = 0;
            int int6 = 0;
            int int7 = 0;
            HashSet hashSet = new HashSet();

            for (int int8 = 0; int8 < int2; int8++) {
                String string0 = readString(bufferedInputStream);
                String string1 = string0.trim();
                String string2 = readString(bufferedInputStream);
                int int9 = readInt(bufferedInputStream);
                int int10 = readInt(bufferedInputStream);
                int int11 = readInt(bufferedInputStream);
                int int12 = readInt(bufferedInputStream);

                for (int int13 = 0; int13 < int12; int13++) {
                    IsoSprite sprite0;
                    if (boolean0) {
                        sprite0 = sprMan.NamedMap.get(string1 + "_" + int13);
                        if (sprite0 == null) {
                            continue;
                        }
                    } else if (fileNumber < 2) {
                        sprite0 = sprMan.AddSprite(string1 + "_" + int13, fileNumber * 100 * 1000 + 10000 + int11 * 1000 + int13);
                    } else {
                        sprite0 = sprMan.AddSprite(string1 + "_" + int13, fileNumber * 512 * 512 + int11 * 512 + int13);
                    }

                    if (Core.bDebug) {
                        if (this.allTiles.containsKey(string1)) {
                            if (!boolean0) {
                                this.allTiles.get(string1).add(string1 + "_" + int13);
                            }
                        } else {
                            ArrayList arrayList2 = new ArrayList();
                            arrayList2.add(string1 + "_" + int13);
                            this.allTiles.put(string1, arrayList2);
                        }
                    }

                    arrayList0.add(sprite0);
                    if (!boolean0) {
                        sprite0.setName(string1 + "_" + int13);
                        sprite0.tileSheetIndex = int13;
                    }

                    if (sprite0.name.contains("damaged") || sprite0.name.contains("trash_")) {
                        sprite0.attachedFloor = true;
                        sprite0.getProperties().Set("attachedFloor", "true");
                    }

                    if (sprite0.name.startsWith("f_bushes") && int13 <= 31) {
                        sprite0.isBush = true;
                        sprite0.attachedFloor = true;
                    }

                    int int14 = readInt(bufferedInputStream);

                    for (int int15 = 0; int15 < int14; int15++) {
                        string0 = readString(bufferedInputStream);
                        String string3 = string0.trim();
                        string0 = readString(bufferedInputStream);
                        String string4 = string0.trim();
                        IsoObjectType objectType = IsoObjectType.FromString(string3);
                        if (objectType != IsoObjectType.MAX) {
                            if (sprite0.getType() != IsoObjectType.doorW && sprite0.getType() != IsoObjectType.doorN || objectType != IsoObjectType.wall) {
                                sprite0.setType(objectType);
                            }

                            if (objectType == IsoObjectType.doorW) {
                                sprite0.getProperties().Set(IsoFlagType.doorW);
                            } else if (objectType == IsoObjectType.doorN) {
                                sprite0.getProperties().Set(IsoFlagType.doorN);
                            }
                        } else {
                            string3 = sharedStrings.get(string3);
                            if (string3.equals("firerequirement")) {
                                sprite0.firerequirement = Integer.parseInt(string4);
                            } else if (string3.equals("fireRequirement")) {
                                sprite0.firerequirement = Integer.parseInt(string4);
                            } else if (string3.equals("BurntTile")) {
                                sprite0.burntTile = string4;
                            } else if (string3.equals("ForceAmbient")) {
                                sprite0.forceAmbient = true;
                                sprite0.getProperties().Set(string3, string4);
                            } else if (string3.equals("solidfloor")) {
                                sprite0.solidfloor = true;
                                sprite0.getProperties().Set(string3, string4);
                            } else if (string3.equals("canBeRemoved")) {
                                sprite0.canBeRemoved = true;
                                sprite0.getProperties().Set(string3, string4);
                            } else if (string3.equals("attachedFloor")) {
                                sprite0.attachedFloor = true;
                                sprite0.getProperties().Set(string3, string4);
                            } else if (string3.equals("cutW")) {
                                sprite0.cutW = true;
                                sprite0.getProperties().Set(string3, string4);
                            } else if (string3.equals("cutN")) {
                                sprite0.cutN = true;
                                sprite0.getProperties().Set(string3, string4);
                            } else if (string3.equals("solid")) {
                                sprite0.solid = true;
                                sprite0.getProperties().Set(string3, string4);
                            } else if (string3.equals("solidTrans")) {
                                sprite0.solidTrans = true;
                                sprite0.getProperties().Set(string3, string4);
                            } else if (string3.equals("invisible")) {
                                sprite0.invisible = true;
                                sprite0.getProperties().Set(string3, string4);
                            } else if (string3.equals("alwaysDraw")) {
                                sprite0.alwaysDraw = true;
                                sprite0.getProperties().Set(string3, string4);
                            } else if (string3.equals("forceRender")) {
                                sprite0.forceRender = true;
                                sprite0.getProperties().Set(string3, string4);
                            } else if ("FloorHeight".equals(string3)) {
                                if ("OneThird".equals(string4)) {
                                    sprite0.getProperties().Set(IsoFlagType.FloorHeightOneThird);
                                } else if ("TwoThirds".equals(string4)) {
                                    sprite0.getProperties().Set(IsoFlagType.FloorHeightTwoThirds);
                                }
                            } else if (string3.equals("MoveWithWind")) {
                                sprite0.moveWithWind = true;
                                sprite0.getProperties().Set(string3, string4);
                            } else if (string3.equals("WindType")) {
                                sprite0.windType = Integer.parseInt(string4);
                                sprite0.getProperties().Set(string3, string4);
                            } else if (string3.equals("RenderLayer")) {
                                sprite0.getProperties().Set(string3, string4);
                                if ("Default".equals(string4)) {
                                    sprite0.renderLayer = 0;
                                } else if ("Floor".equals(string4)) {
                                    sprite0.renderLayer = 1;
                                }
                            } else if (string3.equals("TreatAsWallOrder")) {
                                sprite0.treatAsWallOrder = true;
                                sprite0.getProperties().Set(string3, string4);
                            } else {
                                sprite0.getProperties().Set(string3, string4);
                                if ("WindowN".equals(string3) || "WindowW".equals(string3)) {
                                    sprite0.getProperties().Set(string3, string4, false);
                                }
                            }
                        }

                        if (objectType == IsoObjectType.tree) {
                            if (sprite0.name.equals("e_riverbirch_1_1")) {
                                string4 = "1";
                            }

                            sprite0.getProperties().Set("tree", string4);
                            sprite0.getProperties().UnSet(IsoFlagType.solid);
                            sprite0.getProperties().Set(IsoFlagType.blocksight);
                            int int16 = Integer.parseInt(string4);
                            if (string1.startsWith("vegetation_trees")) {
                                int16 = 4;
                            }

                            if (int16 < 1) {
                                int16 = 1;
                            }

                            if (int16 > 4) {
                                int16 = 4;
                            }

                            if (int16 == 1 || int16 == 2) {
                                sprite0.getProperties().UnSet(IsoFlagType.blocksight);
                            }
                        }

                        if (string3.equals("interior") && string4.equals("false")) {
                            sprite0.getProperties().Set(IsoFlagType.exterior);
                        }

                        if (string3.equals("HoppableN")) {
                            sprite0.getProperties().Set(IsoFlagType.collideN);
                            sprite0.getProperties().Set(IsoFlagType.canPathN);
                            sprite0.getProperties().Set(IsoFlagType.transparentN);
                        }

                        if (string3.equals("HoppableW")) {
                            sprite0.getProperties().Set(IsoFlagType.collideW);
                            sprite0.getProperties().Set(IsoFlagType.canPathW);
                            sprite0.getProperties().Set(IsoFlagType.transparentW);
                        }

                        if (string3.equals("WallN")) {
                            sprite0.getProperties().Set(IsoFlagType.collideN);
                            sprite0.getProperties().Set(IsoFlagType.cutN);
                            sprite0.setType(IsoObjectType.wall);
                            sprite0.cutN = true;
                            sprite0.getProperties().Set("WallN", "", false);
                        }

                        if (string3.equals("CantClimb")) {
                            sprite0.getProperties().Set(IsoFlagType.CantClimb);
                        } else if (string3.equals("container")) {
                            sprite0.getProperties().Set(string3, string4, false);
                        } else if (string3.equals("WallNTrans")) {
                            sprite0.getProperties().Set(IsoFlagType.collideN);
                            sprite0.getProperties().Set(IsoFlagType.cutN);
                            sprite0.getProperties().Set(IsoFlagType.transparentN);
                            sprite0.setType(IsoObjectType.wall);
                            sprite0.cutN = true;
                            sprite0.getProperties().Set("WallNTrans", "", false);
                        } else if (string3.equals("WallW")) {
                            sprite0.getProperties().Set(IsoFlagType.collideW);
                            sprite0.getProperties().Set(IsoFlagType.cutW);
                            sprite0.setType(IsoObjectType.wall);
                            sprite0.cutW = true;
                            sprite0.getProperties().Set("WallW", "", false);
                        } else if (string3.equals("windowN")) {
                            sprite0.getProperties().Set("WindowN", "WindowN");
                            sprite0.getProperties().Set(IsoFlagType.transparentN);
                            sprite0.getProperties().Set("WindowN", "WindowN", false);
                        } else if (string3.equals("windowW")) {
                            sprite0.getProperties().Set("WindowW", "WindowW");
                            sprite0.getProperties().Set(IsoFlagType.transparentW);
                            sprite0.getProperties().Set("WindowW", "WindowW", false);
                        } else if (string3.equals("cutW")) {
                            sprite0.getProperties().Set(IsoFlagType.cutW);
                            sprite0.cutW = true;
                        } else if (string3.equals("cutN")) {
                            sprite0.getProperties().Set(IsoFlagType.cutN);
                            sprite0.cutN = true;
                        } else if (string3.equals("WallWTrans")) {
                            sprite0.getProperties().Set(IsoFlagType.collideW);
                            sprite0.getProperties().Set(IsoFlagType.transparentW);
                            sprite0.getProperties().Set(IsoFlagType.cutW);
                            sprite0.setType(IsoObjectType.wall);
                            sprite0.cutW = true;
                            sprite0.getProperties().Set("WallWTrans", "", false);
                        } else if (string3.equals("DoorWallN")) {
                            sprite0.getProperties().Set(IsoFlagType.cutN);
                            sprite0.cutN = true;
                            sprite0.getProperties().Set("DoorWallN", "", false);
                        } else if (string3.equals("DoorWallNTrans")) {
                            sprite0.getProperties().Set(IsoFlagType.cutN);
                            sprite0.getProperties().Set(IsoFlagType.transparentN);
                            sprite0.cutN = true;
                            sprite0.getProperties().Set("DoorWallNTrans", "", false);
                        } else if (string3.equals("DoorWallW")) {
                            sprite0.getProperties().Set(IsoFlagType.cutW);
                            sprite0.cutW = true;
                            sprite0.getProperties().Set("DoorWallW", "", false);
                        } else if (string3.equals("DoorWallWTrans")) {
                            sprite0.getProperties().Set(IsoFlagType.cutW);
                            sprite0.getProperties().Set(IsoFlagType.transparentW);
                            sprite0.cutW = true;
                            sprite0.getProperties().Set("DoorWallWTrans", "", false);
                        } else if (string3.equals("WallNW")) {
                            sprite0.getProperties().Set(IsoFlagType.collideN);
                            sprite0.getProperties().Set(IsoFlagType.cutN);
                            sprite0.getProperties().Set(IsoFlagType.collideW);
                            sprite0.getProperties().Set(IsoFlagType.cutW);
                            sprite0.setType(IsoObjectType.wall);
                            sprite0.cutW = true;
                            sprite0.cutN = true;
                            sprite0.getProperties().Set("WallNW", "", false);
                        } else if (string3.equals("WallNWTrans")) {
                            sprite0.getProperties().Set(IsoFlagType.collideN);
                            sprite0.getProperties().Set(IsoFlagType.cutN);
                            sprite0.getProperties().Set(IsoFlagType.collideW);
                            sprite0.getProperties().Set(IsoFlagType.transparentN);
                            sprite0.getProperties().Set(IsoFlagType.transparentW);
                            sprite0.getProperties().Set(IsoFlagType.cutW);
                            sprite0.setType(IsoObjectType.wall);
                            sprite0.cutW = true;
                            sprite0.cutN = true;
                            sprite0.getProperties().Set("WallNWTrans", "", false);
                        } else if (string3.equals("WallSE")) {
                            sprite0.getProperties().Set(IsoFlagType.cutW);
                            sprite0.getProperties().Set(IsoFlagType.WallSE);
                            sprite0.getProperties().Set("WallSE", "WallSE");
                            sprite0.cutW = true;
                        } else if (string3.equals("WindowW")) {
                            sprite0.getProperties().Set(IsoFlagType.canPathW);
                            sprite0.getProperties().Set(IsoFlagType.collideW);
                            sprite0.getProperties().Set(IsoFlagType.cutW);
                            sprite0.getProperties().Set(IsoFlagType.transparentW);
                            sprite0.setType(IsoObjectType.windowFW);
                            if (sprite0.getProperties().Is(IsoFlagType.HoppableW)) {
                                if (Core.bDebug) {
                                    DebugLog.log("ERROR: WindowW sprite shouldn't have HoppableW (" + sprite0.getName() + ")");
                                }

                                sprite0.getProperties().UnSet(IsoFlagType.HoppableW);
                            }

                            sprite0.cutW = true;
                        } else if (string3.equals("WindowN")) {
                            sprite0.getProperties().Set(IsoFlagType.canPathN);
                            sprite0.getProperties().Set(IsoFlagType.collideN);
                            sprite0.getProperties().Set(IsoFlagType.cutN);
                            sprite0.getProperties().Set(IsoFlagType.transparentN);
                            sprite0.setType(IsoObjectType.windowFN);
                            if (sprite0.getProperties().Is(IsoFlagType.HoppableN)) {
                                if (Core.bDebug) {
                                    DebugLog.log("ERROR: WindowN sprite shouldn't have HoppableN (" + sprite0.getName() + ")");
                                }

                                sprite0.getProperties().UnSet(IsoFlagType.HoppableN);
                            }

                            sprite0.cutN = true;
                        } else if (string3.equals("UnbreakableWindowW")) {
                            sprite0.getProperties().Set(IsoFlagType.canPathW);
                            sprite0.getProperties().Set(IsoFlagType.collideW);
                            sprite0.getProperties().Set(IsoFlagType.cutW);
                            sprite0.getProperties().Set(IsoFlagType.transparentW);
                            sprite0.getProperties().Set(IsoFlagType.collideW);
                            sprite0.setType(IsoObjectType.wall);
                            sprite0.cutW = true;
                        } else if (string3.equals("UnbreakableWindowN")) {
                            sprite0.getProperties().Set(IsoFlagType.canPathN);
                            sprite0.getProperties().Set(IsoFlagType.collideN);
                            sprite0.getProperties().Set(IsoFlagType.cutN);
                            sprite0.getProperties().Set(IsoFlagType.transparentN);
                            sprite0.getProperties().Set(IsoFlagType.collideN);
                            sprite0.setType(IsoObjectType.wall);
                            sprite0.cutN = true;
                        } else if (string3.equals("UnbreakableWindowNW")) {
                            sprite0.getProperties().Set(IsoFlagType.cutN);
                            sprite0.getProperties().Set(IsoFlagType.transparentN);
                            sprite0.getProperties().Set(IsoFlagType.collideN);
                            sprite0.getProperties().Set(IsoFlagType.cutN);
                            sprite0.getProperties().Set(IsoFlagType.collideW);
                            sprite0.getProperties().Set(IsoFlagType.cutW);
                            sprite0.setType(IsoObjectType.wall);
                            sprite0.cutW = true;
                            sprite0.cutN = true;
                        } else if ("NoWallLighting".equals(string3)) {
                            sprite0.getProperties().Set(IsoFlagType.NoWallLighting);
                        } else if ("ForceAmbient".equals(string3)) {
                            sprite0.getProperties().Set(IsoFlagType.ForceAmbient);
                        }

                        if (string3.equals("name")) {
                            sprite0.setParentObjectName(string4);
                        }
                    }

                    if (sprite0.getProperties().Is("lightR") || sprite0.getProperties().Is("lightG") || sprite0.getProperties().Is("lightB")) {
                        if (!sprite0.getProperties().Is("lightR")) {
                            sprite0.getProperties().Set("lightR", "0");
                        }

                        if (!sprite0.getProperties().Is("lightG")) {
                            sprite0.getProperties().Set("lightG", "0");
                        }

                        if (!sprite0.getProperties().Is("lightB")) {
                            sprite0.getProperties().Set("lightB", "0");
                        }
                    }

                    sprite0.getProperties().CreateKeySet();
                    if (Core.bDebug && sprite0.getProperties().Is("SmashedTileOffset") && !sprite0.getProperties().Is("GlassRemovedOffset")) {
                        DebugLog.General.error("Window sprite has SmashedTileOffset but no GlassRemovedOffset (" + sprite0.getName() + ")");
                    }
                }

                this.setOpenDoorProperties(string1, arrayList0);
                hashMap0.clear();

                for (IsoSprite sprite1 : arrayList0) {
                    if (sprite1.getProperties().Is("StopCar")) {
                        sprite1.setType(IsoObjectType.isMoveAbleObject);
                    }

                    if (sprite1.getProperties().Is("IsMoveAble")) {
                        if (sprite1.getProperties().Is("CustomName") && !sprite1.getProperties().Val("CustomName").equals("")) {
                            int4++;
                            if (sprite1.getProperties().Is("GroupName")) {
                                String string5 = sprite1.getProperties().Val("GroupName") + " " + sprite1.getProperties().Val("CustomName");
                                if (!hashMap0.containsKey(string5)) {
                                    hashMap0.put(string5, new ArrayList());
                                }

                                ((ArrayList)hashMap0.get(string5)).add(sprite1);
                                hashSet.add(string5);
                            } else {
                                if (!hashMap2.containsKey(string1)) {
                                    hashMap2.put(string1, new ArrayList());
                                }

                                if (!((ArrayList)hashMap2.get(string1)).contains(sprite1.getProperties().Val("CustomName"))) {
                                    ((ArrayList)hashMap2.get(string1)).add(sprite1.getProperties().Val("CustomName"));
                                }

                                int5++;
                                hashSet.add(sprite1.getProperties().Val("CustomName"));
                            }
                        } else {
                            DebugLog.log("[IMPORTANT] MOVABLES: Object has no custom name defined: sheet = " + string1);
                        }
                    }
                }

                for (Entry entry : hashMap0.entrySet()) {
                    String string6 = (String)entry.getKey();
                    if (!hashMap2.containsKey(string1)) {
                        hashMap2.put(string1, new ArrayList());
                    }

                    if (!((ArrayList)hashMap2.get(string1)).contains(string6)) {
                        ((ArrayList)hashMap2.get(string1)).add(string6);
                    }

                    ArrayList arrayList3 = (ArrayList)entry.getValue();
                    if (arrayList3.size() == 1) {
                        DebugLog.log("MOVABLES: Object has only one face defined for group: (" + string6 + ") sheet = " + string1);
                    }

                    if (arrayList3.size() == 3) {
                        DebugLog.log("MOVABLES: Object only has 3 sprites, _might_ have a error in settings, group: (" + string6 + ") sheet = " + string1);
                    }

                    for (String string7 : strings0) {
                        ((ArrayList)hashMap1.get(string7)).clear();
                    }

                    boolean boolean4 = ((IsoSprite)arrayList3.get(0)).getProperties().Is("SpriteGridPos")
                        && !((IsoSprite)arrayList3.get(0)).getProperties().Val("SpriteGridPos").equals("None");
                    boolean boolean5 = true;

                    for (IsoSprite sprite2 : arrayList3) {
                        boolean boolean6 = sprite2.getProperties().Is("SpriteGridPos") && !sprite2.getProperties().Val("SpriteGridPos").equals("None");
                        if (boolean4 != boolean6) {
                            boolean5 = false;
                            DebugLog.log("MOVABLES: Difference in SpriteGrid settings for members of group: (" + string6 + ") sheet = " + string1);
                            break;
                        }

                        if (!sprite2.getProperties().Is("Facing")) {
                            boolean5 = false;
                        } else {
                            String string8 = sprite2.getProperties().Val("Facing");
                            switch (string8) {
                                case "N":
                                    ((ArrayList)hashMap1.get("N")).add(sprite2);
                                    break;
                                case "E":
                                    ((ArrayList)hashMap1.get("E")).add(sprite2);
                                    break;
                                case "S":
                                    ((ArrayList)hashMap1.get("S")).add(sprite2);
                                    break;
                                case "W":
                                    ((ArrayList)hashMap1.get("W")).add(sprite2);
                                    break;
                                default:
                                    DebugLog.log(
                                        "MOVABLES: Invalid face ("
                                            + sprite2.getProperties().Val("Facing")
                                            + ") for group: ("
                                            + string6
                                            + ") sheet = "
                                            + string1
                                    );
                                    boolean5 = false;
                            }
                        }

                        if (!boolean5) {
                            DebugLog.log("MOVABLES: Not all members have a valid face defined for group: (" + string6 + ") sheet = " + string1);
                            break;
                        }
                    }

                    if (boolean5) {
                        if (boolean4) {
                            int int17 = 0;
                            IsoSpriteGrid[] spriteGrids = new IsoSpriteGrid[strings0.length];

                            label676:
                            for (int int18 = 0; int18 < strings0.length; int18++) {
                                ArrayList arrayList4 = (ArrayList)hashMap1.get(strings0[int18]);
                                if (arrayList4.size() > 0) {
                                    if (int17 == 0) {
                                        int17 = arrayList4.size();
                                    }

                                    if (int17 != arrayList4.size()) {
                                        DebugLog.log("MOVABLES: Sprite count mismatch for multi sprite movable, group: (" + string6 + ") sheet = " + string1);
                                        boolean5 = false;
                                        break;
                                    }

                                    arrayList1.clear();
                                    int int19 = -1;
                                    int int20 = -1;
                                    Iterator iterator = arrayList4.iterator();

                                    while (true) {
                                        if (iterator.hasNext()) {
                                            IsoSprite sprite3 = (IsoSprite)iterator.next();
                                            String string9 = sprite3.getProperties().Val("SpriteGridPos");
                                            if (!arrayList1.contains(string9)) {
                                                arrayList1.add(string9);
                                                String[] strings1 = string9.split(",");
                                                if (strings1.length == 2) {
                                                    int int21 = Integer.parseInt(strings1[0]);
                                                    int int22 = Integer.parseInt(strings1[1]);
                                                    if (int21 > int19) {
                                                        int19 = int21;
                                                    }

                                                    if (int22 > int20) {
                                                        int20 = int22;
                                                    }
                                                    continue;
                                                }

                                                DebugLog.log(
                                                    "MOVABLES: SpriteGrid position error for multi sprite movable, group: (" + string6 + ") sheet = " + string1
                                                );
                                                boolean5 = false;
                                            } else {
                                                DebugLog.log(
                                                    "MOVABLES: double SpriteGrid position ("
                                                        + string9
                                                        + ") for multi sprite movable, group: ("
                                                        + string6
                                                        + ") sheet = "
                                                        + string1
                                                );
                                                boolean5 = false;
                                            }
                                        }

                                        if (int19 == -1 || int20 == -1 || (int19 + 1) * (int20 + 1) != arrayList4.size()) {
                                            DebugLog.log(
                                                "MOVABLES: SpriteGrid dimensions error for multi sprite movable, group: (" + string6 + ") sheet = " + string1
                                            );
                                            boolean5 = false;
                                            break label676;
                                        }

                                        if (!boolean5) {
                                            break label676;
                                        }

                                        spriteGrids[int18] = new IsoSpriteGrid(int19 + 1, int20 + 1);

                                        for (IsoSprite sprite4 : arrayList4) {
                                            String string10 = sprite4.getProperties().Val("SpriteGridPos");
                                            String[] strings2 = string10.split(",");
                                            int int23 = Integer.parseInt(strings2[0]);
                                            int int24 = Integer.parseInt(strings2[1]);
                                            spriteGrids[int18].setSprite(int23, int24, sprite4);
                                        }

                                        if (!spriteGrids[int18].validate()) {
                                            DebugLog.log(
                                                "MOVABLES: SpriteGrid didn't validate for multi sprite movable, group: (" + string6 + ") sheet = " + string1
                                            );
                                            boolean5 = false;
                                            break label676;
                                        }
                                        break;
                                    }
                                }
                            }

                            if (boolean5 && int17 != 0) {
                                int7++;

                                for (int int25 = 0; int25 < strings0.length; int25++) {
                                    IsoSpriteGrid spriteGrid = spriteGrids[int25];
                                    if (spriteGrid != null) {
                                        for (IsoSprite sprite5 : spriteGrid.getSprites()) {
                                            sprite5.setSpriteGrid(spriteGrid);

                                            for (int int26 = 0; int26 < strings0.length; int26++) {
                                                if (int26 != int25 && spriteGrids[int26] != null) {
                                                    sprite5.getProperties()
                                                        .Set(
                                                            strings0[int26] + "offset",
                                                            Integer.toString(
                                                                arrayList0.indexOf(spriteGrids[int26].getAnchorSprite()) - arrayList0.indexOf(sprite5)
                                                            )
                                                        );
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                DebugLog.log("MOVABLES: Error in multi sprite movable, group: (" + string6 + ") sheet = " + string1);
                            }
                        } else if (arrayList3.size() > 4) {
                            DebugLog.log("MOVABLES: Object has too many faces defined for group: (" + string6 + ") sheet = " + string1);
                        } else {
                            for (String string11 : strings0) {
                                if (((ArrayList)hashMap1.get(string11)).size() > 1) {
                                    DebugLog.log("MOVABLES: " + string11 + " face defined more than once for group: (" + string6 + ") sheet = " + string1);
                                    boolean5 = false;
                                }
                            }

                            if (boolean5) {
                                int6++;

                                for (IsoSprite sprite6 : arrayList3) {
                                    for (String string12 : strings0) {
                                        ArrayList arrayList5 = (ArrayList)hashMap1.get(string12);
                                        if (arrayList5.size() > 0 && arrayList5.get(0) != sprite6) {
                                            sprite6.getProperties()
                                                .Set(string12 + "offset", Integer.toString(arrayList0.indexOf(arrayList5.get(0)) - arrayList0.indexOf(sprite6)));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                arrayList0.clear();
            }

            if (boolean2) {
                ArrayList arrayList6 = new ArrayList(hashSet);
                Collections.sort(arrayList6);

                for (String string13 : arrayList6) {
                    System.out
                        .println(string13.replaceAll(" ", "_").replaceAll("-", "_").replaceAll("'", "").replaceAll("\\.", "") + " = \"" + string13 + "\",");
                }
            }

            if (boolean3) {
                ArrayList arrayList7 = new ArrayList(hashSet);
                Collections.sort(arrayList7);
                StringBuilder stringBuilder = new StringBuilder();

                for (String string14 : arrayList7) {
                    if (Translator.getMoveableDisplayNameOrNull(string14) == null) {
                        stringBuilder.append(
                            string14.replaceAll(" ", "_").replaceAll("-", "_").replaceAll("'", "").replaceAll("\\.", "") + " = \"" + string14 + "\",\n"
                        );
                    }
                }

                String string15 = stringBuilder.toString();
                if (!string15.isEmpty() && Core.bDebug) {
                    System.out.println("Missing translations in Moveables_EN.txt:\n" + string15);
                }
            }

            if (boolean1) {
                try {
                    this.saveMovableStats(hashMap2, fileNumber, int5, int6, int7, int4);
                } catch (Exception exception0) {
                }
            }
        } catch (Exception exception1) {
            ExceptionLogger.logException(exception1);
        }
    }

    private void GenerateTilePropertyLookupTables() {
        TilePropertyAliasMap.instance.Generate(PropertyValueMap);
        PropertyValueMap.clear();
    }

    public void LoadTileDefinitionsPropertyStrings(IsoSpriteManager sprMan, String filename, int fileNumber) {
        DebugLog.log("tiledef: loading " + filename);
        if (!GameServer.bServer) {
            Thread.yield();
            Core.getInstance().DoFrameReady();
        }

        try (
            FileInputStream fileInputStream = new FileInputStream(filename);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        ) {
            int int0 = readInt(bufferedInputStream);
            int int1 = readInt(bufferedInputStream);
            int int2 = readInt(bufferedInputStream);
            SharedStrings sharedStrings = new SharedStrings();

            for (int int3 = 0; int3 < int2; int3++) {
                String string0 = readString(bufferedInputStream);
                String string1 = string0.trim();
                String string2 = readString(bufferedInputStream);
                this.tileImages.add(string2);
                int int4 = readInt(bufferedInputStream);
                int int5 = readInt(bufferedInputStream);
                int int6 = readInt(bufferedInputStream);
                int int7 = readInt(bufferedInputStream);

                for (int int8 = 0; int8 < int7; int8++) {
                    int int9 = readInt(bufferedInputStream);

                    for (int int10 = 0; int10 < int9; int10++) {
                        string0 = readString(bufferedInputStream);
                        String string3 = string0.trim();
                        string0 = readString(bufferedInputStream);
                        String string4 = string0.trim();
                        IsoObjectType objectType = IsoObjectType.FromString(string3);
                        string3 = sharedStrings.get(string3);
                        ArrayList arrayList = null;
                        if (PropertyValueMap.containsKey(string3)) {
                            arrayList = PropertyValueMap.get(string3);
                        } else {
                            arrayList = new ArrayList();
                            PropertyValueMap.put(string3, arrayList);
                        }

                        if (!arrayList.contains(string4)) {
                            arrayList.add(string4);
                        }
                    }
                }
            }
        } catch (Exception exception) {
            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, exception);
        }
    }

    private void SetCustomPropertyValues() {
        PropertyValueMap.get("WindowN").add("WindowN");
        PropertyValueMap.get("WindowW").add("WindowW");
        PropertyValueMap.get("DoorWallN").add("DoorWallN");
        PropertyValueMap.get("DoorWallW").add("DoorWallW");
        PropertyValueMap.get("WallSE").add("WallSE");
        ArrayList arrayList = new ArrayList();

        for (int int0 = -96; int0 <= 96; int0++) {
            String string = Integer.toString(int0);
            arrayList.add(string);
        }

        PropertyValueMap.put("Noffset", arrayList);
        PropertyValueMap.put("Soffset", arrayList);
        PropertyValueMap.put("Woffset", arrayList);
        PropertyValueMap.put("Eoffset", arrayList);
        PropertyValueMap.get("tree").add("5");
        PropertyValueMap.get("tree").add("6");
        PropertyValueMap.get("lightR").add("0");
        PropertyValueMap.get("lightG").add("0");
        PropertyValueMap.get("lightB").add("0");
    }

    private void setOpenDoorProperties(String string2, ArrayList<IsoSprite> arrayList) {
        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            IsoSprite sprite0 = (IsoSprite)arrayList.get(int0);
            if ((sprite0.getType() == IsoObjectType.doorN || sprite0.getType() == IsoObjectType.doorW) && !sprite0.getProperties().Is(IsoFlagType.open)) {
                String string0 = sprite0.getProperties().Val("DoubleDoor");
                if (string0 != null) {
                    int int1 = PZMath.tryParseInt(string0, -1);
                    if (int1 >= 5) {
                        sprite0.getProperties().Set(IsoFlagType.open);
                    }
                } else {
                    String string1 = sprite0.getProperties().Val("GarageDoor");
                    if (string1 != null) {
                        int int2 = PZMath.tryParseInt(string1, -1);
                        if (int2 >= 4) {
                            sprite0.getProperties().Set(IsoFlagType.open);
                        }
                    } else {
                        IsoSprite sprite1 = IsoSpriteManager.instance.NamedMap.get(string2 + "_" + (sprite0.tileSheetIndex + 2));
                        if (sprite1 != null) {
                            sprite1.setType(sprite0.getType());
                            sprite1.getProperties().Set(sprite0.getType() == IsoObjectType.doorN ? IsoFlagType.doorN : IsoFlagType.doorW);
                            sprite1.getProperties().Set(IsoFlagType.open);
                        }
                    }
                }
            }
        }
    }

    private void saveMovableStats(Map<String, ArrayList<String>> map, int int0, int int1, int int2, int int3, int int4) throws FileNotFoundException, IOException {
        File file0 = new File(ZomboidFileSystem.instance.getCacheDir());
        if (file0.exists() && file0.isDirectory()) {
            File file1 = new File(ZomboidFileSystem.instance.getCacheDir() + File.separator + "movables_stats_" + int0 + ".txt");

            try (FileWriter fileWriter = new FileWriter(file1, false)) {
                fileWriter.write("### Movable objects ###" + System.lineSeparator());
                fileWriter.write("Single Face: " + int1 + System.lineSeparator());
                fileWriter.write("Multi Face: " + int2 + System.lineSeparator());
                fileWriter.write("Multi Face & Multi Sprite: " + int3 + System.lineSeparator());
                fileWriter.write("Total objects : " + (int1 + int2 + int3) + System.lineSeparator());
                fileWriter.write(" " + System.lineSeparator());
                fileWriter.write("Total sprites : " + int4 + System.lineSeparator());
                fileWriter.write(" " + System.lineSeparator());

                for (Entry entry : map.entrySet()) {
                    fileWriter.write((String)entry.getKey() + System.lineSeparator());

                    for (String string : (ArrayList)entry.getValue()) {
                        fileWriter.write("\t" + string + System.lineSeparator());
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void addJumboTreeTileset(IsoSpriteManager spriteManager, int int5, String string1, int int4, int int1, int int6) {
        byte byte0 = 2;

        for (int int0 = 0; int0 < int1; int0++) {
            for (int int2 = 0; int2 < byte0; int2++) {
                String string0 = "e_" + string1 + "JUMBO_1";
                int int3 = int0 * byte0 + int2;
                IsoSprite sprite = spriteManager.AddSprite(string0 + "_" + int3, int5 * 512 * 512 + int4 * 512 + int3);

                assert GameServer.bServer || !sprite.CurrentAnim.Frames.isEmpty() && sprite.CurrentAnim.Frames.get(0).getTexture(IsoDirections.N) != null;

                sprite.setName(string0 + "_" + int3);
                sprite.setType(IsoObjectType.tree);
                sprite.getProperties().Set("tree", int2 == 0 ? "5" : "6");
                sprite.getProperties().UnSet(IsoFlagType.solid);
                sprite.getProperties().Set(IsoFlagType.blocksight);
                sprite.getProperties().CreateKeySet();
                sprite.moveWithWind = true;
                sprite.windType = int6;
            }
        }
    }

    private void JumboTreeDefinitions(IsoSpriteManager spriteManager, int int0) {
        this.addJumboTreeTileset(spriteManager, int0, "americanholly", 1, 2, 3);
        this.addJumboTreeTileset(spriteManager, int0, "americanlinden", 2, 6, 2);
        this.addJumboTreeTileset(spriteManager, int0, "canadianhemlock", 3, 2, 3);
        this.addJumboTreeTileset(spriteManager, int0, "carolinasilverbell", 4, 6, 1);
        this.addJumboTreeTileset(spriteManager, int0, "cockspurhawthorn", 5, 6, 2);
        this.addJumboTreeTileset(spriteManager, int0, "dogwood", 6, 6, 2);
        this.addJumboTreeTileset(spriteManager, int0, "easternredbud", 7, 6, 2);
        this.addJumboTreeTileset(spriteManager, int0, "redmaple", 8, 6, 2);
        this.addJumboTreeTileset(spriteManager, int0, "riverbirch", 9, 6, 1);
        this.addJumboTreeTileset(spriteManager, int0, "virginiapine", 10, 2, 1);
        this.addJumboTreeTileset(spriteManager, int0, "yellowwood", 11, 6, 2);
        byte byte0 = 12;
        byte byte1 = 0;
        IsoSprite sprite = spriteManager.AddSprite("jumbo_tree_01_" + byte1, int0 * 512 * 512 + byte0 * 512 + byte1);
        sprite.setName("jumbo_tree_01_" + byte1);
        sprite.setType(IsoObjectType.tree);
        sprite.getProperties().Set("tree", "4");
        sprite.getProperties().UnSet(IsoFlagType.solid);
        sprite.getProperties().Set(IsoFlagType.blocksight);
    }

    private void loadedTileDefinitions() {
        CellLoader.smashedWindowSpriteMap.clear();

        for (IsoSprite sprite0 : IsoSpriteManager.instance.NamedMap.values()) {
            PropertyContainer propertyContainer = sprite0.getProperties();
            if (propertyContainer.Is(IsoFlagType.windowW) || propertyContainer.Is(IsoFlagType.windowN)) {
                String string = propertyContainer.Val("SmashedTileOffset");
                if (string != null) {
                    int int0 = PZMath.tryParseInt(string, 0);
                    if (int0 != 0) {
                        IsoSprite sprite1 = IsoSprite.getSprite(IsoSpriteManager.instance, sprite0, int0);
                        if (sprite1 != null) {
                            CellLoader.smashedWindowSpriteMap.put(sprite1, sprite0);
                        }
                    }
                }
            }
        }
    }

    public boolean LoadPlayerForInfo() throws FileNotFoundException, IOException {
        if (GameClient.bClient) {
            return ClientPlayerDB.getInstance().loadNetworkPlayerInfo(1);
        } else {
            File file = ZomboidFileSystem.instance.getFileInCurrentSave("map_p.bin");
            if (!file.exists()) {
                PlayerDB.getInstance().importPlayersFromVehiclesDB();
                return PlayerDB.getInstance().loadLocalPlayerInfo(1);
            } else {
                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                synchronized (SliceY.SliceBufferLock) {
                    SliceY.SliceBuffer.clear();
                    int int0 = bufferedInputStream.read(SliceY.SliceBuffer.array());
                    SliceY.SliceBuffer.limit(int0);
                    bufferedInputStream.close();
                    byte byte0 = SliceY.SliceBuffer.get();
                    byte byte1 = SliceY.SliceBuffer.get();
                    byte byte2 = SliceY.SliceBuffer.get();
                    byte byte3 = SliceY.SliceBuffer.get();
                    int int1 = -1;
                    if (byte0 == 80 && byte1 == 76 && byte2 == 89 && byte3 == 82) {
                        int1 = SliceY.SliceBuffer.getInt();
                    } else {
                        SliceY.SliceBuffer.rewind();
                    }

                    if (int1 >= 69) {
                        String string = GameWindow.ReadString(SliceY.SliceBuffer);
                        if (GameClient.bClient && int1 < 71) {
                            string = ServerOptions.instance.ServerPlayerID.getValue();
                        }

                        if (GameClient.bClient && !IsoPlayer.isServerPlayerIDValid(string)) {
                            GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_ServerPlayerIDMismatch");
                            GameLoadingState.playerWrongIP = true;
                            return false;
                        }
                    } else if (GameClient.bClient && ServerOptions.instance.ServerPlayerID.getValue().isEmpty()) {
                        GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_ServerPlayerIDMissing");
                        GameLoadingState.playerWrongIP = true;
                        return false;
                    }

                    WorldX = SliceY.SliceBuffer.getInt();
                    WorldY = SliceY.SliceBuffer.getInt();
                    IsoChunkMap.WorldXA = SliceY.SliceBuffer.getInt();
                    IsoChunkMap.WorldYA = SliceY.SliceBuffer.getInt();
                    IsoChunkMap.WorldZA = SliceY.SliceBuffer.getInt();
                    IsoChunkMap.WorldXA = IsoChunkMap.WorldXA + 300 * saveoffsetx;
                    IsoChunkMap.WorldYA = IsoChunkMap.WorldYA + 300 * saveoffsety;
                    IsoChunkMap.SWorldX[0] = WorldX;
                    IsoChunkMap.SWorldY[0] = WorldY;
                    IsoChunkMap.SWorldX[0] = IsoChunkMap.SWorldX[0] + 30 * saveoffsetx;
                    IsoChunkMap.SWorldY[0] = IsoChunkMap.SWorldY[0] + 30 * saveoffsety;
                    return true;
                }
            }
        }
    }

    public void init() throws FileNotFoundException, IOException, WorldDictionaryException {
        if (!Core.bTutorial) {
            this.randomizedBuildingList.add(new RBSafehouse());
            this.randomizedBuildingList.add(new RBBurnt());
            this.randomizedBuildingList.add(new RBOther());
            this.randomizedBuildingList.add(new RBLooted());
            this.randomizedBuildingList.add(new RBBurntFireman());
            this.randomizedBuildingList.add(new RBBurntCorpse());
            this.randomizedBuildingList.add(new RBShopLooted());
            this.randomizedBuildingList.add(new RBKateAndBaldspot());
            this.randomizedBuildingList.add(new RBStripclub());
            this.randomizedBuildingList.add(new RBSchool());
            this.randomizedBuildingList.add(new RBSpiffo());
            this.randomizedBuildingList.add(new RBPizzaWhirled());
            this.randomizedBuildingList.add(new RBPileOCrepe());
            this.randomizedBuildingList.add(new RBCafe());
            this.randomizedBuildingList.add(new RBBar());
            this.randomizedBuildingList.add(new RBOffice());
            this.randomizedBuildingList.add(new RBHairSalon());
            this.randomizedBuildingList.add(new RBClinic());
            this.randomizedVehicleStoryList.add(new RVSUtilityVehicle());
            this.randomizedVehicleStoryList.add(new RVSConstructionSite());
            this.randomizedVehicleStoryList.add(new RVSBurntCar());
            this.randomizedVehicleStoryList.add(new RVSPoliceBlockadeShooting());
            this.randomizedVehicleStoryList.add(new RVSPoliceBlockade());
            this.randomizedVehicleStoryList.add(new RVSCarCrash());
            this.randomizedVehicleStoryList.add(new RVSAmbulanceCrash());
            this.randomizedVehicleStoryList.add(new RVSCarCrashCorpse());
            this.randomizedVehicleStoryList.add(new RVSChangingTire());
            this.randomizedVehicleStoryList.add(new RVSFlippedCrash());
            this.randomizedVehicleStoryList.add(new RVSBanditRoad());
            this.randomizedVehicleStoryList.add(new RVSTrailerCrash());
            this.randomizedVehicleStoryList.add(new RVSCrashHorde());
            this.randomizedZoneList.add(new RZSForestCamp());
            this.randomizedZoneList.add(new RZSForestCampEaten());
            this.randomizedZoneList.add(new RZSBuryingCamp());
            this.randomizedZoneList.add(new RZSBeachParty());
            this.randomizedZoneList.add(new RZSFishingTrip());
            this.randomizedZoneList.add(new RZSBBQParty());
            this.randomizedZoneList.add(new RZSHunterCamp());
            this.randomizedZoneList.add(new RZSSexyTime());
            this.randomizedZoneList.add(new RZSTrapperCamp());
            this.randomizedZoneList.add(new RZSBaseball());
            this.randomizedZoneList.add(new RZSMusicFestStage());
            this.randomizedZoneList.add(new RZSMusicFest());
        }

        zombie.randomizedWorld.randomizedBuilding.RBBasic.getUniqueRDSSpawned().clear();
        if (!GameClient.bClient && !GameServer.bServer) {
            BodyDamageSync.instance = null;
        } else {
            BodyDamageSync.instance = new BodyDamageSync();
        }

        if (GameServer.bServer) {
            Core.GameSaveWorld = GameServer.ServerName;
            String string0 = ZomboidFileSystem.instance.getCurrentSaveDir();
            File file0 = new File(string0);
            if (!file0.exists()) {
                GameServer.ResetID = Rand.Next(10000000);
                ServerOptions.instance.putSaveOption("ResetID", String.valueOf(GameServer.ResetID));
            }

            LuaManager.GlobalObject.createWorld(Core.GameSaveWorld);
        }

        SavedWorldVersion = this.readWorldVersion();
        if (!GameServer.bServer) {
            File file1 = ZomboidFileSystem.instance.getFileInCurrentSave("map_ver.bin");

            try (
                FileInputStream fileInputStream = new FileInputStream(file1);
                DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            ) {
                int int0 = dataInputStream.readInt();
                if (int0 >= 25) {
                    String string1 = GameWindow.ReadString(dataInputStream);
                    if (!GameClient.bClient) {
                        Core.GameMap = string1;
                    }
                }

                if (int0 >= 74) {
                    this.setDifficulty(GameWindow.ReadString(dataInputStream));
                }
            } catch (FileNotFoundException fileNotFoundException) {
            }
        }

        if (!GameServer.bServer || !GameServer.bSoftReset) {
            this.MetaGrid.CreateStep1();
        }

        LuaEventManager.triggerEvent("OnPreDistributionMerge");
        LuaEventManager.triggerEvent("OnDistributionMerge");
        LuaEventManager.triggerEvent("OnPostDistributionMerge");
        ItemPickerJava.Parse();
        VehiclesDB2.instance.init();
        LuaEventManager.triggerEvent("OnInitWorld");
        if (!GameClient.bClient) {
            SandboxOptions.instance.load();
        }

        this.bHydroPowerOn = GameTime.getInstance().NightsSurvived < SandboxOptions.getInstance().getElecShutModifier();
        ZomboidGlobals.toLua();
        ItemPickerJava.InitSandboxLootSettings();
        this.SurvivorDescriptors.clear();
        IsoSpriteManager.instance.Dispose();
        if (GameClient.bClient && ServerOptions.instance.DoLuaChecksum.getValue()) {
            try {
                NetChecksum.comparer.beginCompare();
                GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_Checksum");
                long long0 = System.currentTimeMillis();
                long long1 = long0;

                while (!GameClient.checksumValid) {
                    if (GameWindow.bServerDisconnected) {
                        return;
                    }

                    if (System.currentTimeMillis() > long0 + 8000L) {
                        DebugLog.log("checksum: timed out waiting for the server to respond");
                        GameClient.connection.forceDisconnect("world-timeout-response");
                        GameWindow.bServerDisconnected = true;
                        GameWindow.kickReason = Translator.getText("UI_GameLoad_TimedOut");
                        return;
                    }

                    if (System.currentTimeMillis() > long1 + 1000L) {
                        DebugLog.log("checksum: waited one second");
                        long1 += 1000L;
                    }

                    NetChecksum.comparer.update();
                    if (GameClient.checksumValid) {
                        break;
                    }

                    Thread.sleep(100L);
                }
            } catch (Exception exception0) {
                exception0.printStackTrace();
            }
        }

        GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_LoadTileDef");
        IsoSpriteManager spriteManager = IsoSpriteManager.instance;
        this.tileImages.clear();
        ZomboidFileSystem zomboidFileSystem = ZomboidFileSystem.instance;
        this.LoadTileDefinitionsPropertyStrings(spriteManager, zomboidFileSystem.getMediaPath("tiledefinitions.tiles"), 0);
        this.LoadTileDefinitionsPropertyStrings(spriteManager, zomboidFileSystem.getMediaPath("newtiledefinitions.tiles"), 1);
        this.LoadTileDefinitionsPropertyStrings(spriteManager, zomboidFileSystem.getMediaPath("tiledefinitions_erosion.tiles"), 2);
        this.LoadTileDefinitionsPropertyStrings(spriteManager, zomboidFileSystem.getMediaPath("tiledefinitions_apcom.tiles"), 3);
        this.LoadTileDefinitionsPropertyStrings(spriteManager, zomboidFileSystem.getMediaPath("tiledefinitions_overlays.tiles"), 4);
        this.LoadTileDefinitionsPropertyStrings(spriteManager, zomboidFileSystem.getMediaPath("tiledefinitions_noiseworks.patch.tiles"), -1);
        ZomboidFileSystem.instance.loadModTileDefPropertyStrings();
        this.SetCustomPropertyValues();
        this.GenerateTilePropertyLookupTables();
        this.LoadTileDefinitions(spriteManager, zomboidFileSystem.getMediaPath("tiledefinitions.tiles"), 0);
        this.LoadTileDefinitions(spriteManager, zomboidFileSystem.getMediaPath("newtiledefinitions.tiles"), 1);
        this.LoadTileDefinitions(spriteManager, zomboidFileSystem.getMediaPath("tiledefinitions_erosion.tiles"), 2);
        this.LoadTileDefinitions(spriteManager, zomboidFileSystem.getMediaPath("tiledefinitions_apcom.tiles"), 3);
        this.LoadTileDefinitions(spriteManager, zomboidFileSystem.getMediaPath("tiledefinitions_overlays.tiles"), 4);
        this.LoadTileDefinitions(spriteManager, zomboidFileSystem.getMediaPath("tiledefinitions_noiseworks.patch.tiles"), -1);
        this.JumboTreeDefinitions(spriteManager, 5);
        ZomboidFileSystem.instance.loadModTileDefs();
        GameLoadingState.GameLoadingString = "";
        spriteManager.AddSprite("media/ui/missing-tile.png");
        LuaEventManager.triggerEvent("OnLoadedTileDefinitions", spriteManager);
        this.loadedTileDefinitions();
        if (GameServer.bServer && GameServer.bSoftReset) {
            WorldConverter.instance.softreset();
        }

        try {
            WeatherFxMask.init();
        } catch (Exception exception1) {
            System.out.print(exception1.getStackTrace());
        }

        TemplateText.Initialize();
        IsoRegions.init();
        ObjectRenderEffects.init();
        WorldConverter.instance.convert(Core.GameSaveWorld, spriteManager);
        if (!GameLoadingState.build23Stop) {
            SandboxOptions.instance.handleOldZombiesFile2();
            GameTime.getInstance().init();
            GameTime.getInstance().load();
            ImprovedFog.init();
            ZomboidRadio.getInstance().Init(SavedWorldVersion);
            GlobalModData.instance.init();
            if (GameServer.bServer && Core.getInstance().getPoisonousBerry() == null) {
                Core.getInstance().initPoisonousBerry();
            }

            if (GameServer.bServer && Core.getInstance().getPoisonousMushroom() == null) {
                Core.getInstance().initPoisonousMushroom();
            }

            ErosionGlobals.Boot(spriteManager);
            WorldDictionary.init();
            WorldMarkers.instance.init();
            if (GameServer.bServer) {
                SharedDescriptors.initSharedDescriptors();
            }

            PersistentOutfits.instance.init();
            VirtualZombieManager.instance.init();
            VehicleIDMap.instance.Reset();
            VehicleManager.instance = new VehicleManager();
            GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_InitMap");
            this.MetaGrid.CreateStep2();
            ClimateManager.getInstance().init(this.MetaGrid);
            SafeHouse.init();
            if (!GameClient.bClient) {
                StashSystem.init();
            }

            LuaEventManager.triggerEvent("OnLoadMapZones");
            this.MetaGrid.load();
            this.MetaGrid.loadZones();
            this.MetaGrid.processZones();
            LuaEventManager.triggerEvent("OnLoadedMapZones");
            if (GameServer.bServer) {
                ServerMap.instance.init(this.MetaGrid);
            }

            int int1 = 0;
            int int2 = 0;
            if (GameClient.bClient) {
                if (ClientPlayerDB.getInstance().clientLoadNetworkPlayer() && ClientPlayerDB.getInstance().isAliveMainNetworkPlayer()) {
                    int2 = 1;
                }
            } else {
                int2 = PlayerDBHelper.isPlayerAlive(ZomboidFileSystem.instance.getCurrentSaveDir(), 1);
            }

            if (GameServer.bServer) {
                ServerPlayerDB.setAllow(true);
            }

            if (!GameClient.bClient && !GameServer.bServer) {
                PlayerDB.setAllow(true);
            }

            int int3 = 0;
            int int4 = 0;
            int int5 = 0;
            if (int2) {
                int1 = (boolean)1;
                if (!this.LoadPlayerForInfo()) {
                    return;
                }

                WorldX = IsoChunkMap.SWorldX[IsoPlayer.getPlayerIndex()];
                WorldY = IsoChunkMap.SWorldY[IsoPlayer.getPlayerIndex()];
                int3 = IsoChunkMap.WorldXA;
                int4 = IsoChunkMap.WorldYA;
                int5 = IsoChunkMap.WorldZA;
            } else {
                int1 = (boolean)0;
                if (GameClient.bClient && !ServerOptions.instance.SpawnPoint.getValue().isEmpty()) {
                    String[] strings0 = ServerOptions.instance.SpawnPoint.getValue().split(",");
                    if (strings0.length == 3) {
                        try {
                            IsoChunkMap.MPWorldXA = new Integer(strings0[0].trim());
                            IsoChunkMap.MPWorldYA = new Integer(strings0[1].trim());
                            IsoChunkMap.MPWorldZA = new Integer(strings0[2].trim());
                        } catch (NumberFormatException numberFormatException0) {
                            DebugLog.log("ERROR: SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
                            IsoChunkMap.MPWorldXA = 0;
                            IsoChunkMap.MPWorldYA = 0;
                            IsoChunkMap.MPWorldZA = 0;
                        }
                    } else {
                        DebugLog.log("ERROR: SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
                    }
                }

                if (this.getLuaSpawnCellX() >= 0 && (!GameClient.bClient || IsoChunkMap.MPWorldXA == 0 && IsoChunkMap.MPWorldYA == 0)) {
                    IsoChunkMap.WorldXA = this.getLuaPosX() + 300 * this.getLuaSpawnCellX();
                    IsoChunkMap.WorldYA = this.getLuaPosY() + 300 * this.getLuaSpawnCellY();
                    IsoChunkMap.WorldZA = this.getLuaPosZ();
                    if (GameClient.bClient && ServerOptions.instance.SafehouseAllowRespawn.getValue()) {
                        for (int int6 = 0; int6 < SafeHouse.getSafehouseList().size(); int6++) {
                            SafeHouse safeHouse0 = SafeHouse.getSafehouseList().get(int6);
                            if (safeHouse0.getPlayers().contains(GameClient.username) && safeHouse0.isRespawnInSafehouse(GameClient.username)) {
                                IsoChunkMap.WorldXA = safeHouse0.getX() + safeHouse0.getH() / 2;
                                IsoChunkMap.WorldYA = safeHouse0.getY() + safeHouse0.getW() / 2;
                                IsoChunkMap.WorldZA = 0;
                            }
                        }
                    }

                    WorldX = IsoChunkMap.WorldXA / 10;
                    WorldY = IsoChunkMap.WorldYA / 10;
                } else if (GameClient.bClient) {
                    IsoChunkMap.WorldXA = IsoChunkMap.MPWorldXA;
                    IsoChunkMap.WorldYA = IsoChunkMap.MPWorldYA;
                    IsoChunkMap.WorldZA = IsoChunkMap.MPWorldZA;
                    WorldX = IsoChunkMap.WorldXA / 10;
                    WorldY = IsoChunkMap.WorldYA / 10;
                }
            }

            Core.getInstance();
            KahluaTable table0 = (KahluaTable)LuaManager.env.rawget("selectedDebugScenario");
            if (table0 != null) {
                KahluaTable table1 = (KahluaTable)table0.rawget("startLoc");
                int int7 = ((Double)table1.rawget("x")).intValue();
                int int8 = ((Double)table1.rawget("y")).intValue();
                int int9 = ((Double)table1.rawget("z")).intValue();
                IsoChunkMap.WorldXA = int7;
                IsoChunkMap.WorldYA = int8;
                IsoChunkMap.WorldZA = int9;
                WorldX = IsoChunkMap.WorldXA / 10;
                WorldY = IsoChunkMap.WorldYA / 10;
            }

            MapCollisionData.instance.init(instance.getMetaGrid());
            ZombiePopulationManager.instance.init(instance.getMetaGrid());
            PolygonalMap2.instance.init(instance.getMetaGrid());
            GlobalObjectLookup.init(instance.getMetaGrid());
            if (!GameServer.bServer) {
                SpawnPoints.instance.initSinglePlayer();
            }

            WorldStreamer.instance.create();
            this.CurrentCell = CellLoader.LoadCellBinaryChunk(spriteManager, WorldX, WorldY);
            ClimateManager.getInstance().postCellLoadSetSnow();
            GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_LoadWorld");
            MapCollisionData.instance.start();
            MapItem.LoadWorldMap();

            while (WorldStreamer.instance.isBusy()) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }

            ArrayList arrayList = new ArrayList();
            arrayList.addAll(IsoChunk.loadGridSquare);

            for (IsoChunk chunk : arrayList) {
                this.CurrentCell.ChunkMap[0].setChunkDirect(chunk, false);
            }

            IsoChunk.bDoServerRequests = true;
            if (int1 && SystemDisabler.doPlayerCreation) {
                this.CurrentCell.LoadPlayer(SavedWorldVersion);
                if (GameClient.bClient) {
                    IsoPlayer.getInstance().setUsername(GameClient.username);
                }

                ZomboidRadio.getInstance().getRecordedMedia().handleLegacyListenedLines(IsoPlayer.getInstance());
            } else {
                ZomboidRadio.getInstance().getRecordedMedia().handleLegacyListenedLines(null);
                IsoGridSquare square0 = null;
                if (IsoPlayer.numPlayers == 0) {
                    IsoPlayer.numPlayers = 1;
                }

                int int10 = IsoChunkMap.WorldXA;
                int int11 = IsoChunkMap.WorldYA;
                int int12 = IsoChunkMap.WorldZA;
                if (GameClient.bClient && !ServerOptions.instance.SpawnPoint.getValue().isEmpty()) {
                    String[] strings1 = ServerOptions.instance.SpawnPoint.getValue().split(",");
                    if (strings1.length != 3) {
                        DebugLog.log("ERROR: SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
                    } else {
                        try {
                            int int13 = new Integer(strings1[0].trim());
                            int int14 = new Integer(strings1[1].trim());
                            int int15 = new Integer(strings1[2].trim());
                            if (GameClient.bClient && ServerOptions.instance.SafehouseAllowRespawn.getValue()) {
                                for (int int16 = 0; int16 < SafeHouse.getSafehouseList().size(); int16++) {
                                    SafeHouse safeHouse1 = SafeHouse.getSafehouseList().get(int16);
                                    if (safeHouse1.getPlayers().contains(GameClient.username) && safeHouse1.isRespawnInSafehouse(GameClient.username)) {
                                        int13 = safeHouse1.getX() + safeHouse1.getH() / 2;
                                        int14 = safeHouse1.getY() + safeHouse1.getW() / 2;
                                        int15 = 0;
                                    }
                                }
                            }

                            if (this.CurrentCell.getGridSquare(int13, int14, int15) != null) {
                                int10 = int13;
                                int11 = int14;
                                int12 = int15;
                            }
                        } catch (NumberFormatException numberFormatException1) {
                            DebugLog.log("ERROR: SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
                        }
                    }
                }

                square0 = this.CurrentCell.getGridSquare(int10, int11, int12);
                if (SystemDisabler.doPlayerCreation && !GameServer.bServer) {
                    if (square0 != null && square0.isFree(false) && square0.getRoom() != null) {
                        IsoGridSquare square1 = square0;
                        square0 = square0.getRoom().getFreeTile();
                        if (square0 == null) {
                            square0 = square1;
                        }
                    }

                    IsoPlayer player0 = null;
                    if (this.getLuaPlayerDesc() != null) {
                        if (GameClient.bClient && ServerOptions.instance.SafehouseAllowRespawn.getValue()) {
                            square0 = this.CurrentCell.getGridSquare(IsoChunkMap.WorldXA, IsoChunkMap.WorldYA, IsoChunkMap.WorldZA);
                            if (square0 != null && square0.isFree(false) && square0.getRoom() != null) {
                                IsoGridSquare square2 = square0;
                                square0 = square0.getRoom().getFreeTile();
                                if (square0 == null) {
                                    square0 = square2;
                                }
                            }
                        }

                        if (square0 == null) {
                            throw new RuntimeException("can't create player at x,y,z=" + int10 + "," + int11 + "," + int12 + " because the square is null");
                        }

                        WorldSimulation.instance.create();
                        player0 = new IsoPlayer(instance.CurrentCell, this.getLuaPlayerDesc(), square0.getX(), square0.getY(), square0.getZ());
                        if (GameClient.bClient) {
                            player0.setUsername(GameClient.username);
                        }

                        player0.setDir(IsoDirections.SE);
                        player0.sqlID = 1;
                        IsoPlayer.players[0] = player0;
                        IsoPlayer.setInstance(player0);
                        IsoCamera.CamCharacter = player0;
                    }

                    IsoPlayer player1 = IsoPlayer.getInstance();
                    player1.applyTraits(this.getLuaTraits());
                    ProfessionFactory.Profession profession = ProfessionFactory.getProfession(player1.getDescriptor().getProfession());
                    if (profession != null && !profession.getFreeRecipes().isEmpty()) {
                        for (String string2 : profession.getFreeRecipes()) {
                            player1.getKnownRecipes().add(string2);
                        }
                    }

                    for (String string3 : this.getLuaTraits()) {
                        TraitFactory.Trait trait = TraitFactory.getTrait(string3);
                        if (trait != null && !trait.getFreeRecipes().isEmpty()) {
                            for (String string4 : trait.getFreeRecipes()) {
                                player1.getKnownRecipes().add(string4);
                            }
                        }
                    }

                    if (square0 != null && square0.getRoom() != null) {
                        square0.getRoom().def.setExplored(true);
                        square0.getRoom().building.setAllExplored(true);
                        if (!GameServer.bServer && !GameClient.bClient) {
                            ZombiePopulationManager.instance.playerSpawnedAt(square0.getX(), square0.getY(), square0.getZ());
                        }
                    }

                    player1.createKeyRing();
                    if (!GameClient.bClient) {
                        Core.getInstance().initPoisonousBerry();
                        Core.getInstance().initPoisonousMushroom();
                    }

                    LuaEventManager.triggerEvent("OnNewGame", player0, square0);
                }
            }

            if (PlayerDB.isAllow()) {
                PlayerDB.getInstance().m_canSavePlayers = true;
            }

            if (ClientPlayerDB.isAllow()) {
                ClientPlayerDB.getInstance().canSavePlayers = true;
            }

            TutorialManager.instance.ActiveControlZombies = false;
            ReanimatedPlayers.instance.loadReanimatedPlayers();
            if (IsoPlayer.getInstance() != null) {
                if (GameClient.bClient) {
                    int1 = (int)IsoPlayer.getInstance().getX();
                    int2 = (int)IsoPlayer.getInstance().getY();
                    int3 = (int)IsoPlayer.getInstance().getZ();

                    while (int3 > 0) {
                        IsoGridSquare square3 = this.CurrentCell.getGridSquare(int1, int2, int3);
                        if (square3 != null && square3.TreatAsSolidFloor()) {
                            break;
                        }

                        IsoPlayer.getInstance().setZ(--int3);
                    }
                }

                IsoPlayer.getInstance()
                    .setCurrent(
                        this.CurrentCell
                            .getGridSquare((int)IsoPlayer.getInstance().getX(), (int)IsoPlayer.getInstance().getY(), (int)IsoPlayer.getInstance().getZ())
                    );
            }

            if (!this.bLoaded) {
                if (!this.CurrentCell.getBuildingList().isEmpty()) {
                }

                if (!this.bLoaded) {
                    this.PopulateCellWithSurvivors();
                }
            }

            if (IsoPlayer.players[0] != null && !this.CurrentCell.getObjectList().contains(IsoPlayer.players[0])) {
                this.CurrentCell.getObjectList().add(IsoPlayer.players[0]);
            }

            LightingThread.instance.create();
            GameLoadingState.GameLoadingString = "";
            initMessaging();
            WorldDictionary.onWorldLoaded();
        }
    }

    int readWorldVersion() {
        if (GameServer.bServer) {
            File file0 = ZomboidFileSystem.instance.getFileInCurrentSave("map_t.bin");

            try (
                FileInputStream fileInputStream0 = new FileInputStream(file0);
                DataInputStream dataInputStream0 = new DataInputStream(fileInputStream0);
            ) {
                byte byte0 = dataInputStream0.readByte();
                byte byte1 = dataInputStream0.readByte();
                byte byte2 = dataInputStream0.readByte();
                byte byte3 = dataInputStream0.readByte();
                if (byte0 == 71 && byte1 == 77 && byte2 == 84 && byte3 == 77) {
                    return dataInputStream0.readInt();
                }

                return -1;
            } catch (FileNotFoundException fileNotFoundException0) {
            } catch (IOException iOException0) {
                ExceptionLogger.logException(iOException0);
            }

            return -1;
        } else {
            File file1 = ZomboidFileSystem.instance.getFileInCurrentSave("map_ver.bin");

            try {
                int int0;
                try (
                    FileInputStream fileInputStream1 = new FileInputStream(file1);
                    DataInputStream dataInputStream1 = new DataInputStream(fileInputStream1);
                ) {
                    int0 = dataInputStream1.readInt();
                }

                return int0;
            } catch (FileNotFoundException fileNotFoundException1) {
            } catch (IOException iOException1) {
                ExceptionLogger.logException(iOException1);
            }

            return -1;
        }
    }

    public ArrayList<String> getLuaTraits() {
        if (this.luatraits == null) {
            this.luatraits = new ArrayList<>();
        }

        return this.luatraits;
    }

    public void addLuaTrait(String trait) {
        this.getLuaTraits().add(trait);
    }

    public SurvivorDesc getLuaPlayerDesc() {
        return this.luaDesc;
    }

    public void setLuaPlayerDesc(SurvivorDesc desc) {
        this.luaDesc = desc;
    }

    public void KillCell() {
        this.helicopter.deactivate();
        CollisionManager.instance.ContactMap.clear();
        IsoDeadBody.Reset();
        FliesSound.instance.Reset();
        IsoObjectPicker.Instance.Init();
        IsoChunkMap.SharedChunks.clear();
        SoundManager.instance.StopMusic();
        WorldSoundManager.instance.KillCell();
        ZombieGroupManager.instance.Reset();
        this.CurrentCell.Dispose();
        IsoSpriteManager.instance.Dispose();
        this.CurrentCell = null;
        CellLoader.wanderRoom = null;
        IsoLot.Dispose();
        IsoGameCharacter.getSurvivorMap().clear();
        IsoPlayer.getInstance().setCurrent(null);
        IsoPlayer.getInstance().setLast(null);
        IsoPlayer.getInstance().square = null;
        RainManager.reset();
        IsoFireManager.Reset();
        ObjectAmbientEmitters.Reset();
        ZombieVocalsManager.Reset();
        IsoWaterFlow.Reset();
        this.MetaGrid.Dispose();
        instance = new IsoWorld();
    }

    public void setDrawWorld(boolean b) {
        this.bDrawWorld = b;
    }

    public void sceneCullZombies() {
        this.zombieWithModel.clear();
        this.zombieWithoutModel.clear();

        for (int int0 = 0; int0 < this.CurrentCell.getZombieList().size(); int0++) {
            IsoZombie zombie0 = this.CurrentCell.getZombieList().get(int0);
            boolean boolean0 = false;

            for (int int1 = 0; int1 < IsoPlayer.numPlayers; int1++) {
                IsoPlayer player = IsoPlayer.players[int1];
                if (player != null && zombie0.current != null) {
                    float float0 = zombie0.getScreenProperX(int1);
                    float float1 = zombie0.getScreenProperY(int1);
                    if (!(float0 < -100.0F)
                        && !(float1 < -100.0F)
                        && !(float0 > Core.getInstance().getOffscreenWidth(int1) + 100)
                        && !(float1 > Core.getInstance().getOffscreenHeight(int1) + 100)
                        && (zombie0.getAlpha(int1) != 0.0F && zombie0.legsSprite.def.alpha != 0.0F || zombie0.current.isCouldSee(int1))) {
                        boolean0 = true;
                        break;
                    }
                }
            }

            if (boolean0 && zombie0.isCurrentState(FakeDeadZombieState.instance())) {
                boolean0 = false;
            }

            if (boolean0) {
                this.zombieWithModel.add(zombie0);
            } else {
                this.zombieWithoutModel.add(zombie0);
            }
        }

        Collections.sort(this.zombieWithModel, compScoreToPlayer);
        int int2 = 0;
        int int3 = 0;
        int int4 = 0;
        short short0 = 510;
        PerformanceSettings.AnimationSkip = 0;

        for (int int5 = 0; int5 < this.zombieWithModel.size(); int5++) {
            IsoZombie zombie1 = this.zombieWithModel.get(int5);
            if (int4 < short0) {
                if (!zombie1.Ghost) {
                    int3++;
                    int4++;
                    zombie1.setSceneCulled(false);
                    if (zombie1.legsSprite != null && zombie1.legsSprite.modelSlot != null) {
                        if (int3 > PerformanceSettings.ZombieAnimationSpeedFalloffCount) {
                            int2++;
                            int3 = 0;
                        }

                        if (int4 < PerformanceSettings.ZombieBonusFullspeedFalloff) {
                            zombie1.legsSprite.modelSlot.model.setInstanceSkip(int3 / PerformanceSettings.ZombieBonusFullspeedFalloff);
                            int3 = 0;
                        } else {
                            zombie1.legsSprite.modelSlot.model.setInstanceSkip(int2 + PerformanceSettings.AnimationSkip);
                        }

                        if (zombie1.legsSprite.modelSlot.model.AnimPlayer != null) {
                            if (int4 < PerformanceSettings.numberZombiesBlended) {
                                zombie1.legsSprite.modelSlot.model.AnimPlayer.bDoBlending = !zombie1.isAlphaAndTargetZero(0)
                                    || !zombie1.isAlphaAndTargetZero(1)
                                    || !zombie1.isAlphaAndTargetZero(2)
                                    || !zombie1.isAlphaAndTargetZero(3);
                            } else {
                                zombie1.legsSprite.modelSlot.model.AnimPlayer.bDoBlending = false;
                            }
                        }
                    }
                }
            } else {
                zombie1.setSceneCulled(true);
                if (zombie1.hasAnimationPlayer()) {
                    zombie1.getAnimationPlayer().bDoBlending = false;
                }
            }
        }

        for (int int6 = 0; int6 < this.zombieWithoutModel.size(); int6++) {
            IsoZombie zombie2 = this.zombieWithoutModel.get(int6);
            if (zombie2.hasActiveModel()) {
                zombie2.setSceneCulled(true);
            }

            if (zombie2.hasAnimationPlayer()) {
                zombie2.getAnimationPlayer().bDoBlending = false;
            }
        }
    }

    public void render() {
        IsoWorld.s_performance.isoWorldRender.invokeAndMeasure(this, IsoWorld::renderInternal);
    }

    private void renderInternal() {
        if (this.bDrawWorld) {
            if (IsoCamera.CamCharacter != null) {
                SpriteRenderer.instance.doCoreIntParam(0, IsoCamera.CamCharacter.x);
                SpriteRenderer.instance.doCoreIntParam(1, IsoCamera.CamCharacter.y);
                SpriteRenderer.instance.doCoreIntParam(2, IsoCamera.CamCharacter.z);

                try {
                    this.sceneCullZombies();
                } catch (Throwable throwable0) {
                    ExceptionLogger.logException(throwable0);
                }

                try {
                    WeatherFxMask.initMask();
                    DeadBodyAtlas.instance.render();
                    WorldItemAtlas.instance.render();
                    this.CurrentCell.render();
                    this.DrawIsoCursorHelper();
                    DeadBodyAtlas.instance.renderDebug();
                    PolygonalMap2.instance.render();
                    WorldSoundManager.instance.render();
                    WorldFlares.debugRender();
                    WorldMarkers.instance.debugRender();
                    ObjectAmbientEmitters.getInstance().render();
                    ZombieVocalsManager.instance.render();
                    LineDrawer.render();
                    WeatherFxMask.renderFxMask(IsoCamera.frameState.playerIndex);
                    if (GameClient.bClient) {
                        ClientServerMap.render(IsoCamera.frameState.playerIndex);
                        PassengerMap.render(IsoCamera.frameState.playerIndex);
                    }

                    SkyBox.getInstance().render();
                } catch (Throwable throwable1) {
                    ExceptionLogger.logException(throwable1);
                }
            }
        }
    }

    private void DrawIsoCursorHelper() {
        if (Core.getInstance().getOffscreenBuffer() == null) {
            IsoPlayer player = IsoPlayer.getInstance();
            if (player != null && !player.isDead() && player.isAiming() && player.PlayerIndex == 0 && player.JoypadBind == -1) {
                if (!GameTime.isGamePaused()) {
                    float float0 = 0.05F;
                    switch (Core.getInstance().getIsoCursorVisibility()) {
                        case 0:
                            return;
                        case 1:
                            float0 = 0.05F;
                            break;
                        case 2:
                            float0 = 0.1F;
                            break;
                        case 3:
                            float0 = 0.15F;
                            break;
                        case 4:
                            float0 = 0.3F;
                            break;
                        case 5:
                            float0 = 0.5F;
                            break;
                        case 6:
                            float0 = 0.75F;
                    }

                    if (Core.getInstance().isFlashIsoCursor()) {
                        if (this.flashIsoCursorInc) {
                            this.flashIsoCursorA += 0.1F;
                            if (this.flashIsoCursorA >= 1.0F) {
                                this.flashIsoCursorInc = false;
                            }
                        } else {
                            this.flashIsoCursorA -= 0.1F;
                            if (this.flashIsoCursorA <= 0.0F) {
                                this.flashIsoCursorInc = true;
                            }
                        }

                        float0 = this.flashIsoCursorA;
                    }

                    Texture texture = Texture.getSharedTexture("media/ui/isocursor.png");
                    int int0 = (int)(texture.getWidth() * Core.TileScale / 2.0F);
                    int int1 = (int)(texture.getHeight() * Core.TileScale / 2.0F);
                    SpriteRenderer.instance.setDoAdditive(true);
                    SpriteRenderer.instance
                        .renderi(texture, Mouse.getX() - int0 / 2, Mouse.getY() - int1 / 2, int0, int1, float0, float0, float0, float0, null);
                    SpriteRenderer.instance.setDoAdditive(false);
                }
            }
        }
    }

    public void update() {
        IsoWorld.s_performance.isoWorldUpdate.invokeAndMeasure(this, IsoWorld::updateInternal);
    }

    private void updateInternal() {
        this.m_frameNo++;

        try {
            if (GameServer.bServer) {
                VehicleManager.instance.serverUpdate();
            }
        } catch (Exception exception0) {
            exception0.printStackTrace();
        }

        WorldSimulation.instance.update();
        ImprovedFog.update();
        this.helicopter.update();
        long long0 = System.currentTimeMillis();
        if (long0 - this.emitterUpdateMS >= 30L) {
            this.emitterUpdateMS = long0;
            this.emitterUpdate = true;
        } else {
            this.emitterUpdate = false;
        }

        for (int int0 = 0; int0 < this.currentEmitters.size(); int0++) {
            BaseSoundEmitter baseSoundEmitter = this.currentEmitters.get(int0);
            if (this.emitterUpdate || baseSoundEmitter.hasSoundsToStart()) {
                baseSoundEmitter.tick();
            }

            if (baseSoundEmitter.isEmpty()) {
                FMODSoundEmitter fMODSoundEmitter = Type.tryCastTo(baseSoundEmitter, FMODSoundEmitter.class);
                if (fMODSoundEmitter != null) {
                    fMODSoundEmitter.clearParameters();
                }

                this.currentEmitters.remove(int0);
                this.freeEmitters.push(baseSoundEmitter);
                IsoObject object = this.emitterOwners.remove(baseSoundEmitter);
                if (object != null && object.emitter == baseSoundEmitter) {
                    object.emitter = null;
                }

                int0--;
            }
        }

        if (!GameClient.bClient && !GameServer.bServer) {
            IsoMetaCell metaCell = this.MetaGrid.getCurrentCellData();
            if (metaCell != null) {
                metaCell.checkTriggers();
            }
        }

        WorldSoundManager.instance.initFrame();
        ZombieGroupManager.instance.preupdate();
        OnceEvery.update();
        CollisionManager.instance.initUpdate();

        for (int int1 = 0; int1 < this.CurrentCell.getBuildingList().size(); int1++) {
            this.CurrentCell.getBuildingList().get(int1).update();
        }

        ClimateManager.getInstance().update();
        ObjectRenderEffects.updateStatic();
        this.CurrentCell.update();
        IsoRegions.update();
        HaloTextHelper.update();
        CollisionManager.instance.ResolveContacts();

        for (int int2 = 0; int2 < this.AddCoopPlayers.size(); int2++) {
            AddCoopPlayer addCoopPlayer = this.AddCoopPlayers.get(int2);
            addCoopPlayer.update();
            if (addCoopPlayer.isFinished()) {
                this.AddCoopPlayers.remove(int2--);
            }
        }

        if (!GameServer.bServer) {
            IsoPlayer.UpdateRemovedEmitters();
        }

        try {
            if (PlayerDB.isAvailable()) {
                PlayerDB.getInstance().updateMain();
            }

            if (ClientPlayerDB.isAvailable()) {
                ClientPlayerDB.getInstance().updateMain();
            }

            VehiclesDB2.instance.updateMain();
        } catch (Exception exception1) {
            ExceptionLogger.logException(exception1);
        }

        if (this.updateSafehousePlayers > 0 && (GameServer.bServer || GameClient.bClient)) {
            this.updateSafehousePlayers--;
            if (this.updateSafehousePlayers == 0) {
                this.updateSafehousePlayers = 200;
                SafeHouse.updateSafehousePlayersConnected();
            }
        }

        m_animationRecorderDiscard = false;
    }

    public IsoCell getCell() {
        return this.CurrentCell;
    }

    private void PopulateCellWithSurvivors() {
    }

    public int getWorldSquareY() {
        return this.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldY * 10;
    }

    public int getWorldSquareX() {
        return this.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldX * 10;
    }

    public IsoMetaChunk getMetaChunk(int wx, int wy) {
        return this.MetaGrid.getChunkData(wx, wy);
    }

    public IsoMetaChunk getMetaChunkFromTile(int wx, int wy) {
        return this.MetaGrid.getChunkDataFromTile(wx, wy);
    }

    /**
     * Utility method for ClimateManager.getTemperature()
     * @return The current temperature.
     */
    public float getGlobalTemperature() {
        return ClimateManager.getInstance().getTemperature();
    }

    /**
     * setGlobalTemperature is now deprecated. Does nothing.    member globalTemperature is replaced with ClimateManager.getTemperature()
     */
    @Deprecated
    public void setGlobalTemperature(float globalTemperature) {
    }

    public String getWeather() {
        return this.weather;
    }

    public void setWeather(String _weather) {
        this.weather = _weather;
    }

    public int getLuaSpawnCellX() {
        return this.luaSpawnCellX;
    }

    public void setLuaSpawnCellX(int _luaSpawnCellX) {
        this.luaSpawnCellX = _luaSpawnCellX;
    }

    public int getLuaSpawnCellY() {
        return this.luaSpawnCellY;
    }

    public void setLuaSpawnCellY(int _luaSpawnCellY) {
        this.luaSpawnCellY = _luaSpawnCellY;
    }

    public int getLuaPosX() {
        return this.luaPosX;
    }

    public void setLuaPosX(int _luaPosX) {
        this.luaPosX = _luaPosX;
    }

    public int getLuaPosY() {
        return this.luaPosY;
    }

    public void setLuaPosY(int _luaPosY) {
        this.luaPosY = _luaPosY;
    }

    public int getLuaPosZ() {
        return this.luaPosZ;
    }

    public void setLuaPosZ(int _luaPosZ) {
        this.luaPosZ = _luaPosZ;
    }

    public String getWorld() {
        return Core.GameSaveWorld;
    }

    public void transmitWeather() {
        if (GameServer.bServer) {
            GameServer.sendWeather();
        }
    }

    public boolean isValidSquare(int _x, int _y, int z) {
        return z >= 0 && z < 8 ? this.MetaGrid.isValidSquare(_x, _y) : false;
    }

    public ArrayList<RandomizedZoneStoryBase> getRandomizedZoneList() {
        return this.randomizedZoneList;
    }

    public ArrayList<RandomizedBuildingBase> getRandomizedBuildingList() {
        return this.randomizedBuildingList;
    }

    public ArrayList<RandomizedVehicleStoryBase> getRandomizedVehicleStoryList() {
        return this.randomizedVehicleStoryList;
    }

    public RandomizedVehicleStoryBase getRandomizedVehicleStoryByName(String name) {
        for (int int0 = 0; int0 < this.randomizedVehicleStoryList.size(); int0++) {
            RandomizedVehicleStoryBase randomizedVehicleStoryBase = this.randomizedVehicleStoryList.get(int0);
            if (randomizedVehicleStoryBase.getName().equalsIgnoreCase(name)) {
                return randomizedVehicleStoryBase;
            }
        }

        return null;
    }

    public RandomizedBuildingBase getRBBasic() {
        return this.RBBasic;
    }

    public String getDifficulty() {
        return Core.getDifficulty();
    }

    public void setDifficulty(String difficulty) {
        Core.setDifficulty(difficulty);
    }

    public static boolean getZombiesDisabled() {
        return NoZombies || !SystemDisabler.doZombieCreation || SandboxOptions.instance.Zombies.getValue() == 6;
    }

    public static boolean getZombiesEnabled() {
        return !getZombiesDisabled();
    }

    public ClimateManager getClimateManager() {
        return ClimateManager.getInstance();
    }

    public IsoPuddles getPuddlesManager() {
        return IsoPuddles.getInstance();
    }

    public static int getWorldVersion() {
        return 195;
    }

    public HashMap<String, ArrayList<Double>> getSpawnedZombieZone() {
        return this.spawnedZombieZone;
    }

    public int getTimeSinceLastSurvivorInHorde() {
        return this.timeSinceLastSurvivorInHorde;
    }

    public void setTimeSinceLastSurvivorInHorde(int _timeSinceLastSurvivorInHorde) {
        this.timeSinceLastSurvivorInHorde = _timeSinceLastSurvivorInHorde;
    }

    public float getWorldAgeDays() {
        float float0 = (float)GameTime.getInstance().getWorldAgeHours() / 24.0F;
        return float0 + (SandboxOptions.instance.TimeSinceApo.getValue() - 1) * 30;
    }

    public HashMap<String, ArrayList<String>> getAllTiles() {
        return this.allTiles;
    }

    public ArrayList<String> getAllTilesName() {
        ArrayList arrayList = new ArrayList();
        Iterator iterator = this.allTiles.keySet().iterator();

        while (iterator.hasNext()) {
            arrayList.add((String)iterator.next());
        }

        Collections.sort(arrayList);
        return arrayList;
    }

    public ArrayList<String> getAllTiles(String filename) {
        return this.allTiles.get(filename);
    }

    public boolean isHydroPowerOn() {
        return this.bHydroPowerOn;
    }

    public void setHydroPowerOn(boolean on) {
        this.bHydroPowerOn = on;
    }

    public ArrayList<String> getTileImageNames() {
        return this.tileImages;
    }

    private static class CompDistToPlayer implements Comparator<IsoZombie> {
        public float px;
        public float py;

        public int compare(IsoZombie zombie0, IsoZombie zombie1) {
            float float0 = IsoUtils.DistanceManhatten((int)zombie0.x, (int)zombie0.y, this.px, this.py);
            float float1 = IsoUtils.DistanceManhatten((int)zombie1.x, (int)zombie1.y, this.px, this.py);
            if (float0 < float1) {
                return -1;
            } else {
                return float0 > float1 ? 1 : 0;
            }
        }
    }

    private static class CompScoreToPlayer implements Comparator<IsoZombie> {
        public int compare(IsoZombie arg0, IsoZombie arg1) {
            float float0 = this.getScore(arg0);
            float float1 = this.getScore(arg1);
            if (float0 < float1) {
                return 1;
            } else {
                return float0 > float1 ? -1 : 0;
            }
        }

        public float getScore(IsoZombie arg0) {
            float float0 = Float.MIN_VALUE;

            for (int int0 = 0; int0 < 4; int0++) {
                IsoPlayer player = IsoPlayer.players[int0];
                if (player != null && player.current != null) {
                    float float1 = player.getZombieRelevenceScore(arg0);
                    float0 = Math.max(float0, float1);
                }
            }

            return float0;
        }
    }

    public class Frame {
        public ArrayList<Integer> xPos = new ArrayList<>();
        public ArrayList<Integer> yPos = new ArrayList<>();
        public ArrayList<Integer> Type = new ArrayList<>();

        public Frame() {
            Iterator iterator = IsoWorld.instance.CurrentCell.getObjectList().iterator();

            while (iterator != null && iterator.hasNext()) {
                IsoMovingObject movingObject = (IsoMovingObject)iterator.next();
                byte byte0 = 2;
                if (movingObject instanceof IsoPlayer) {
                    byte0 = 0;
                } else if (movingObject instanceof IsoSurvivor) {
                    byte0 = 1;
                } else {
                    if (!(movingObject instanceof IsoZombie) || ((IsoZombie)movingObject).Ghost) {
                        continue;
                    }

                    byte0 = 2;
                }

                this.xPos.add((int)movingObject.getX());
                this.yPos.add((int)movingObject.getY());
                this.Type.add(Integer.valueOf(byte0));
            }
        }
    }

    public static class MetaCell {
        public int x;
        public int y;
        public int zombieCount;
        public IsoDirections zombieMigrateDirection;
        public int[][] from = new int[3][3];
    }

    private static class s_performance {
        static final PerformanceProfileProbe isoWorldUpdate = new PerformanceProfileProbe("IsoWorld.update");
        static final PerformanceProfileProbe isoWorldRender = new PerformanceProfileProbe("IsoWorld.render");
    }
}
