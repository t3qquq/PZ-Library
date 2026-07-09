// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.gameStates;

import fmod.javafmod;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import zombie.AmbientStreamManager;
import zombie.DebugFileWatcher;
import zombie.FliesSound;
import zombie.GameSounds;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.LootRespawn;
import zombie.MapCollisionData;
import zombie.ReanimatedPlayers;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZombieSpawnRecorder;
import zombie.ZomboidFileSystem;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaHookManager;
import zombie.Lua.LuaManager;
import zombie.Lua.MapObjects;
import zombie.audio.ObjectAmbientEmitters;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorFactory;
import zombie.characters.AttachedItems.AttachedLocations;
import zombie.characters.WornItems.BodyLocations;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.skills.CustomPerks;
import zombie.characters.skills.PerkFactory;
import zombie.characters.traits.TraitFactory;
import zombie.chat.ChatElement;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.Languages;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.opengl.RenderSettings;
import zombie.core.opengl.RenderThread;
import zombie.core.physics.WorldSimulation;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.advancedanimation.AdvancedAnimator;
import zombie.core.skinnedmodel.advancedanimation.AnimationSet;
import zombie.core.skinnedmodel.model.ModelOutlines;
import zombie.core.skinnedmodel.model.WorldItemAtlas;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.core.skinnedmodel.population.ClothingDecals;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.stash.StashSystem;
import zombie.core.textures.Texture;
import zombie.core.znet.SteamFriends;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.erosion.ErosionGlobals;
import zombie.globalObjects.CGlobalObjects;
import zombie.globalObjects.SGlobalObjects;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.input.Mouse;
import zombie.inventory.ItemSoundManager;
import zombie.iso.BentFences;
import zombie.iso.BrokenFences;
import zombie.iso.BuildingDef;
import zombie.iso.ContainerOverlays;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMarkers;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.iso.LightingThread;
import zombie.iso.LotHeader;
import zombie.iso.SearchMode;
import zombie.iso.TileOverlays;
import zombie.iso.WorldMarkers;
import zombie.iso.WorldStreamer;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoWaveSignal;
import zombie.iso.objects.RainManager;
import zombie.iso.sprite.CorpseFlies;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.SkyBox;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.Temperature;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.meta.Meta;
import zombie.modding.ActiveMods;
import zombie.network.BodyDamageSync;
import zombie.network.ChunkChecksum;
import zombie.network.ClientServerMap;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ItemTransactionManager;
import zombie.network.MPStatistics;
import zombie.network.PassengerMap;
import zombie.network.ServerGUI;
import zombie.network.ServerOptions;
import zombie.popman.ZombiePopulationManager;
import zombie.radio.ZomboidRadio;
import zombie.sandbox.CustomSandboxOptions;
import zombie.savefile.ClientPlayerDB;
import zombie.savefile.PlayerDB;
import zombie.scripting.ScriptManager;
import zombie.spnetwork.SinglePlayerClient;
import zombie.spnetwork.SinglePlayerServer;
import zombie.text.templating.TemplateText;
import zombie.ui.ActionProgressBar;
import zombie.ui.FPSGraph;
import zombie.ui.TextDrawObject;
import zombie.ui.TextManager;
import zombie.ui.TutorialManager;
import zombie.ui.UIElement;
import zombie.ui.UIManager;
import zombie.util.StringUtils;
import zombie.vehicles.EditVehicleState;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleCache;
import zombie.vehicles.VehicleIDMap;
import zombie.vehicles.VehicleType;
import zombie.vehicles.VehiclesDB2;
import zombie.worldMap.WorldMap;
import zombie.worldMap.WorldMapVisited;
import zombie.worldMap.editor.WorldMapEditorState;

public final class IngameState extends GameState {
    public static int WaitMul = 20;
    public static IngameState instance;
    public static float draww;
    public static float drawh;
    public static Long GameID = 0L;
    static int last = -1;
    static float xPos;
    static float yPos;
    static float offx;
    static float offy;
    static float zoom;
    static HashMap<String, Integer> ContainerTypes = new HashMap<>();
    static int nSaveCycle = 1800;
    static boolean bDoChars = false;
    static boolean keySpacePreviousFrame = false;
    public long numberTicks = 0L;
    public boolean Paused = false;
    public float SaveDelay = 0.0F;
    boolean alt = false;
    int insanityScareCount = 5;
    int insanitypic = -1;
    int timesincelastinsanity = 10000000;
    GameState RedirectState = null;
    boolean bDidServerDisconnectState = false;
    boolean fpsKeyDown = false;
    private final ArrayList<Long> debugTimes = new ArrayList<>();
    private int tickCount = 0;
    private float SadisticMusicDirectorTime;
    public boolean showAnimationViewer = false;
    public boolean showAttachmentEditor = false;
    public boolean showChunkDebugger = false;
    public boolean showGlobalObjectDebugger = false;
    public String showVehicleEditor = null;
    public String showWorldMapEditor = null;

    public IngameState() {
        instance = this;
    }

    public static void renderDebugOverhead(IsoCell cell, int int12, int int4, int int2, int int3) {
        Mouse.update();
        int int0 = Mouse.getX();
        int int1 = Mouse.getY();
        int0 -= int2;
        int1 -= int3;
        int0 /= int4;
        int1 /= int4;
        SpriteRenderer.instance.renderi(null, int2, int3, int4 * cell.getWidthInTiles(), int4 * cell.getHeightInTiles(), 0.7F, 0.7F, 0.7F, 1.0F, null);
        IsoGridSquare square0 = cell.getGridSquare(int0 + cell.ChunkMap[0].getWorldXMinTiles(), int1 + cell.ChunkMap[0].getWorldYMinTiles(), 0);
        if (square0 != null) {
            int int5 = 48;
            int int6 = 48;
            TextManager.instance.DrawString(int6, int5, "SQUARE FLAGS", 1.0, 1.0, 1.0, 1.0);
            int5 += 20;
            int6 += 8;

            for (int int7 = 0; int7 < IsoFlagType.MAX.index(); int7++) {
                if (square0.Is(IsoFlagType.fromIndex(int7))) {
                    TextManager.instance.DrawString(int6, int5, IsoFlagType.fromIndex(int7).toString(), 0.6, 0.6, 0.8, 1.0);
                    int5 += 18;
                }
            }

            int int8 = 48;
            int5 += 16;
            TextManager.instance.DrawString(int8, int5, "SQUARE OBJECT TYPES", 1.0, 1.0, 1.0, 1.0);
            int5 += 20;
            int8 += 8;

            for (int int9 = 0; int9 < 64; int9++) {
                if (square0.getHasTypes().isSet(int9)) {
                    TextManager.instance.DrawString(int8, int5, IsoObjectType.fromIndex(int9).toString(), 0.6, 0.6, 0.8, 1.0);
                    int5 += 18;
                }
            }
        }

        for (int int10 = 0; int10 < cell.getWidthInTiles(); int10++) {
            for (int int11 = 0; int11 < cell.getHeightInTiles(); int11++) {
                IsoGridSquare square1 = cell.getGridSquare(int10 + cell.ChunkMap[0].getWorldXMinTiles(), int11 + cell.ChunkMap[0].getWorldYMinTiles(), int12);
                if (square1 != null) {
                    if (square1.getProperties().Is(IsoFlagType.solid) || square1.getProperties().Is(IsoFlagType.solidtrans)) {
                        SpriteRenderer.instance.renderi(null, int2 + int10 * int4, int3 + int11 * int4, int4, int4, 0.5F, 0.5F, 0.5F, 255.0F, null);
                    } else if (!square1.getProperties().Is(IsoFlagType.exterior)) {
                        SpriteRenderer.instance.renderi(null, int2 + int10 * int4, int3 + int11 * int4, int4, int4, 0.8F, 0.8F, 0.8F, 1.0F, null);
                    }

                    if (square1.Has(IsoObjectType.tree)) {
                        SpriteRenderer.instance.renderi(null, int2 + int10 * int4, int3 + int11 * int4, int4, int4, 0.4F, 0.8F, 0.4F, 1.0F, null);
                    }

                    if (square1.getProperties().Is(IsoFlagType.collideN)) {
                        SpriteRenderer.instance.renderi(null, int2 + int10 * int4, int3 + int11 * int4, int4, 1, 0.2F, 0.2F, 0.2F, 1.0F, null);
                    }

                    if (square1.getProperties().Is(IsoFlagType.collideW)) {
                        SpriteRenderer.instance.renderi(null, int2 + int10 * int4, int3 + int11 * int4, 1, int4, 0.2F, 0.2F, 0.2F, 1.0F, null);
                    }
                }
            }
        }
    }

    public static float translatePointX(float float0, float float1, float float2, float float3) {
        float0 -= float1;
        float0 *= float2;
        float0 += float3;
        return float0 + draww / 2.0F;
    }

    public static float invTranslatePointX(float float0, float float3, float float2, float float1) {
        float0 -= draww / 2.0F;
        float0 -= float1;
        float0 /= float2;
        return float0 + float3;
    }

    public static float invTranslatePointY(float float0, float float3, float float2, float float1) {
        float0 -= drawh / 2.0F;
        float0 -= float1;
        float0 /= float2;
        return float0 + float3;
    }

    public static float translatePointY(float float0, float float1, float float2, float float3) {
        float0 -= float1;
        float0 *= float2;
        float0 += float3;
        return float0 + drawh / 2.0F;
    }

    public static void renderRect(float float1, float float3, float float5, float float7, float float8, float float9, float float10, float float11) {
        float float0 = translatePointX(float1, xPos, zoom, offx);
        float float2 = translatePointY(float3, yPos, zoom, offy);
        float float4 = translatePointX(float1 + float5, xPos, zoom, offx);
        float float6 = translatePointY(float3 + float7, yPos, zoom, offy);
        float5 = float4 - float0;
        float7 = float6 - float2;
        if (!(float0 >= Core.getInstance().getScreenWidth()) && !(float4 < 0.0F) && !(float2 >= Core.getInstance().getScreenHeight()) && !(float6 < 0.0F)) {
            SpriteRenderer.instance.render(null, float0, float2, float5, float7, float8, float9, float10, float11, null);
        }
    }

    public static void renderLine(float float1, float float3, float float5, float float7, float float8, float float9, float float10, float float11) {
        float float0 = translatePointX(float1, xPos, zoom, offx);
        float float2 = translatePointY(float3, yPos, zoom, offy);
        float float4 = translatePointX(float5, xPos, zoom, offx);
        float float6 = translatePointY(float7, yPos, zoom, offy);
        if ((!(float0 >= Core.getInstance().getScreenWidth()) || !(float4 >= Core.getInstance().getScreenWidth()))
            && (!(float2 >= Core.getInstance().getScreenHeight()) || !(float6 >= Core.getInstance().getScreenHeight()))
            && (!(float0 < 0.0F) || !(float4 < 0.0F))
            && (!(float2 < 0.0F) || !(float6 < 0.0F))) {
            SpriteRenderer.instance.renderline(null, (int)float0, (int)float2, (int)float4, (int)float6, float8, float9, float10, float11);
        }
    }

    public static void renderDebugOverhead2(IsoCell cell, int int6, float float2, int int2, int int3, float float0, float float1, int int0, int int1) {
        draww = int0;
        drawh = int1;
        xPos = float0;
        yPos = float1;
        offx = int2;
        offy = int3;
        zoom = float2;
        float float3 = cell.ChunkMap[0].getWorldXMinTiles();
        float float4 = cell.ChunkMap[0].getWorldYMinTiles();
        float float5 = cell.ChunkMap[0].getWorldXMaxTiles();
        float float6 = cell.ChunkMap[0].getWorldYMaxTiles();
        renderRect(float3, float4, cell.getWidthInTiles(), cell.getWidthInTiles(), 0.7F, 0.7F, 0.7F, 1.0F);

        for (int int4 = 0; int4 < cell.getWidthInTiles(); int4++) {
            for (int int5 = 0; int5 < cell.getHeightInTiles(); int5++) {
                IsoGridSquare square = cell.getGridSquare(int4 + cell.ChunkMap[0].getWorldXMinTiles(), int5 + cell.ChunkMap[0].getWorldYMinTiles(), int6);
                float float7 = int4 + float3;
                float float8 = int5 + float4;
                if (square != null) {
                    if (square.getProperties().Is(IsoFlagType.solid) || square.getProperties().Is(IsoFlagType.solidtrans)) {
                        renderRect(float7, float8, 1.0F, 1.0F, 0.5F, 0.5F, 0.5F, 1.0F);
                    } else if (!square.getProperties().Is(IsoFlagType.exterior)) {
                        renderRect(float7, float8, 1.0F, 1.0F, 0.8F, 0.8F, 0.8F, 1.0F);
                    }

                    if (square.Has(IsoObjectType.tree)) {
                        renderRect(float7, float8, 1.0F, 1.0F, 0.4F, 0.8F, 0.4F, 1.0F);
                    }

                    if (square.getProperties().Is(IsoFlagType.collideN)) {
                        renderRect(float7, float8, 1.0F, 0.2F, 0.2F, 0.2F, 0.2F, 1.0F);
                    }

                    if (square.getProperties().Is(IsoFlagType.collideW)) {
                        renderRect(float7, float8, 0.2F, 1.0F, 0.2F, 0.2F, 0.2F, 1.0F);
                    }
                }
            }
        }

        IsoMetaGrid metaGrid = IsoWorld.instance.MetaGrid;
        renderRect(metaGrid.minX * 300, metaGrid.minY * 300, metaGrid.getWidth() * 300, metaGrid.getHeight() * 300, 1.0F, 1.0F, 1.0F, 0.05F);
        if (float2 > 0.1) {
            for (int int7 = metaGrid.minY; int7 <= metaGrid.maxY; int7++) {
                renderLine(metaGrid.minX * 300, int7 * 300, (metaGrid.maxX + 1) * 300, int7 * 300, 1.0F, 1.0F, 1.0F, 0.15F);
            }

            for (int int8 = metaGrid.minX; int8 <= metaGrid.maxX; int8++) {
                renderLine(int8 * 300, metaGrid.minY * 300, int8 * 300, (metaGrid.maxY + 1) * 300, 1.0F, 1.0F, 1.0F, 0.15F);
            }
        }

        IsoMetaCell[][] metaCells = IsoWorld.instance.MetaGrid.Grid;

        for (int int9 = 0; int9 < metaCells.length; int9++) {
            for (int int10 = 0; int10 < metaCells[0].length; int10++) {
                LotHeader lotHeader = metaCells[int9][int10].info;
                if (lotHeader == null) {
                    renderRect((metaGrid.minX + int9) * 300 + 1, (metaGrid.minY + int10) * 300 + 1, 298.0F, 298.0F, 0.2F, 0.0F, 0.0F, 0.3F);
                } else {
                    for (int int11 = 0; int11 < lotHeader.Buildings.size(); int11++) {
                        BuildingDef buildingDef = lotHeader.Buildings.get(int11);
                        if (buildingDef.bAlarmed) {
                            renderRect(buildingDef.getX(), buildingDef.getY(), buildingDef.getW(), buildingDef.getH(), 0.8F, 0.8F, 0.5F, 0.3F);
                        } else {
                            renderRect(buildingDef.getX(), buildingDef.getY(), buildingDef.getW(), buildingDef.getH(), 0.5F, 0.5F, 0.8F, 0.3F);
                        }
                    }
                }
            }
        }
    }

    public static void copyWorld(String string1, String string3) {
        String string0 = ZomboidFileSystem.instance.getGameModeCacheDir() + File.separator + string1 + File.separator;
        string0 = string0.replace("/", File.separator);
        string0 = string0.replace("\\", File.separator);
        String string2 = string0.substring(0, string0.lastIndexOf(File.separator));
        string2 = string2.replace("\\", "/");
        File file0 = new File(string2);
        string0 = ZomboidFileSystem.instance.getGameModeCacheDir() + File.separator + string3 + File.separator;
        string0 = string0.replace("/", File.separator);
        string0 = string0.replace("\\", File.separator);
        String string4 = string0.substring(0, string0.lastIndexOf(File.separator));
        string4 = string4.replace("\\", "/");
        File file1 = new File(string4);

        try {
            copyDirectory(file0, file1);
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    public static void copyDirectory(File file0, File file1) throws IOException {
        if (file0.isDirectory()) {
            if (!file1.exists()) {
                file1.mkdir();
            }

            String[] strings = file0.list();
            boolean boolean0 = GameLoadingState.convertingFileMax == -1;
            if (boolean0) {
                GameLoadingState.convertingFileMax = strings.length;
            }

            for (int int0 = 0; int0 < strings.length; int0++) {
                if (boolean0) {
                    GameLoadingState.convertingFileCount++;
                }

                copyDirectory(new File(file0, strings[int0]), new File(file1, strings[int0]));
            }
        } else {
            FileInputStream fileInputStream = new FileInputStream(file0);
            FileOutputStream fileOutputStream = new FileOutputStream(file1);
            fileOutputStream.getChannel().transferFrom(fileInputStream.getChannel(), 0L, file0.length());
            fileInputStream.close();
            fileOutputStream.close();
        }
    }

    public static void createWorld(String string0) {
        string0 = string0.replace(" ", "_").trim();
        String string1 = ZomboidFileSystem.instance.getGameModeCacheDir() + File.separator + string0 + File.separator;
        string1 = string1.replace("/", File.separator);
        string1 = string1.replace("\\", File.separator);
        String string2 = string1.substring(0, string1.lastIndexOf(File.separator));
        string2 = string2.replace("\\", "/");
        File file = new File(string2);
        if (!file.exists()) {
            file.mkdirs();
        }

        Core.GameSaveWorld = string0;
    }

    public void debugFullyStreamedIn(int int0, int int1) {
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, 0);
        if (square != null) {
            if (square.getBuilding() != null) {
                BuildingDef buildingDef = square.getBuilding().getDef();
                if (buildingDef != null) {
                    boolean boolean0 = buildingDef.isFullyStreamedIn();

                    for (byte byte0 = 0; byte0 < buildingDef.overlappedChunks.size(); byte0 += 2) {
                        short short0 = buildingDef.overlappedChunks.get(byte0);
                        short short1 = buildingDef.overlappedChunks.get(byte0 + 1);
                        if (boolean0) {
                            renderRect(short0 * 10, short1 * 10, 10.0F, 10.0F, 0.0F, 1.0F, 0.0F, 0.5F);
                        } else {
                            renderRect(short0 * 10, short1 * 10, 10.0F, 10.0F, 1.0F, 0.0F, 0.0F, 0.5F);
                        }
                    }
                }
            }
        }
    }

    public void UpdateStuff() {
        GameClient.bIngame = true;
        this.SaveDelay = this.SaveDelay + GameTime.instance.getMultiplier();
        if (this.SaveDelay / 60.0F > 30.0F) {
            this.SaveDelay = 0.0F;
        }

        GameTime.instance.LastLastTimeOfDay = GameTime.instance.getLastTimeOfDay();
        GameTime.instance.setLastTimeOfDay(GameTime.getInstance().getTimeOfDay());
        boolean boolean0 = false;
        if (!GameServer.bServer && IsoPlayer.getInstance() != null) {
            boolean0 = IsoPlayer.allPlayersAsleep();
        }

        GameTime.getInstance().update(boolean0 && UIManager.getFadeAlpha() == 1.0);
        if (!this.Paused) {
            ScriptManager.instance.update();
        }

        if (!this.Paused) {
            long long0 = System.nanoTime();

            try {
                WorldSoundManager.instance.update();
            } catch (Exception exception0) {
                ExceptionLogger.logException(exception0);
            }

            try {
                IsoFireManager.Update();
            } catch (Exception exception1) {
                ExceptionLogger.logException(exception1);
            }

            try {
                RainManager.Update();
            } catch (Exception exception2) {
                ExceptionLogger.logException(exception2);
            }

            Meta.instance.update();

            try {
                VirtualZombieManager.instance.update();
                MapCollisionData.instance.updateMain();
                ZombiePopulationManager.instance.updateMain();
                PolygonalMap2.instance.updateMain();
            } catch (Exception exception3) {
                ExceptionLogger.logException(exception3);
            } catch (Error error) {
                error.printStackTrace();
            }

            try {
                LootRespawn.update();
            } catch (Exception exception4) {
                ExceptionLogger.logException(exception4);
            }

            if (GameServer.bServer) {
                try {
                    AmbientStreamManager.instance.update();
                } catch (Exception exception5) {
                    ExceptionLogger.logException(exception5);
                }
            } else {
                ObjectAmbientEmitters.getInstance().update();
            }

            if (GameClient.bClient) {
                try {
                    BodyDamageSync.instance.update();
                } catch (Exception exception6) {
                    ExceptionLogger.logException(exception6);
                }
            }

            if (!GameServer.bServer) {
                try {
                    ItemSoundManager.update();
                    FliesSound.instance.update();
                    CorpseFlies.update();
                    LuaManager.call("SadisticMusicDirectorTick", null);
                    WorldMapVisited.update();
                } catch (Exception exception7) {
                    ExceptionLogger.logException(exception7);
                }
            }

            SearchMode.getInstance().update();
            RenderSettings.getInstance().update();
            long long1 = System.nanoTime();
        }
    }

    @Override
    public void enter() {
        UIManager.useUIFBO = Core.getInstance().supportsFBO() && Core.OptionUIFBO;
        if (!Core.getInstance().getUseShaders()) {
            Core.getInstance().RenderShader = null;
        }

        GameSounds.fix3DListenerPosition(false);
        IsoPlayer.getInstance().updateUsername();
        IsoPlayer.getInstance().setSceneCulled(false);
        IsoPlayer.getInstance().getInventory().addItemsToProcessItems();
        GameID = (long)Rand.Next(10000000);
        GameID = GameID + Rand.Next(10000000);
        GameID = GameID + Rand.Next(10000000);
        GameID = GameID + Rand.Next(10000000);
        GameID = GameID + Rand.Next(10000000);
        GameID = GameID + Rand.Next(10000000);
        GameID = GameID + Rand.Next(10000000);
        GameID = GameID + Rand.Next(10000000);
        GameID = GameID + Rand.Next(10000000);
        GameID = GameID + Rand.Next(10000000);
        GameID = GameID + Rand.Next(10000000);
        GameID = GameID + Rand.Next(10000000);
        GameID = GameID + Rand.Next(10000000);
        GameID = GameID + Rand.Next(10000000);
        GameID = GameID + Rand.Next(10000000);
        GameID = GameID + Rand.Next(10000000);
        GameID = GameID + Rand.Next(10000000);
        GameID = GameID + Rand.Next(10000000);
        GameID = GameID + Rand.Next(10000000);
        GameID = GameID + Rand.Next(10000000);
        GameID = GameID + Rand.Next(10000000);
        GameID = GameID + Rand.Next(10000000);
        ZombieSpawnRecorder.instance.init();
        if (!GameServer.bServer) {
            IsoWorld.instance.CurrentCell.ChunkMap[0].processAllLoadGridSquare();
            IsoWorld.instance.CurrentCell.ChunkMap[0].update();
            if (!GameClient.bClient) {
                LightingThread.instance.GameLoadingUpdate();
            }
        }

        try {
            MapCollisionData.instance.startGame();
        } catch (Throwable throwable) {
            ExceptionLogger.logException(throwable);
        }

        IsoWorld.instance.CurrentCell.putInVehicle(IsoPlayer.getInstance());
        SoundManager.instance.setMusicState("Tutorial".equals(Core.GameMode) ? "Tutorial" : "InGame");
        ClimateManager.getInstance().update();
        LuaEventManager.triggerEvent("OnGameStart");
        LuaEventManager.triggerEvent("OnLoad");
        if (GameClient.bClient) {
            GameClient.instance.sendPlayerConnect(IsoPlayer.getInstance());
            DebugLog.log("Waiting for player-connect response from server");

            for (; IsoPlayer.getInstance().OnlineID == -1; GameClient.instance.update()) {
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }

            ClimateManager.getInstance().update();
            LightingThread.instance.GameLoadingUpdate();
        }

        if (GameClient.bClient && SteamUtils.isSteamModeEnabled()) {
            SteamFriends.UpdateRichPresenceConnectionInfo("In game", "+connect " + GameClient.ip + ":" + GameClient.port);
        }
    }

    @Override
    public void exit() {
        DebugLog.log("EXITDEBUG: IngameState.exit 1");
        if (SteamUtils.isSteamModeEnabled()) {
            SteamFriends.UpdateRichPresenceConnectionInfo("", "");
        }

        UIManager.useUIFBO = false;
        if (FPSGraph.instance != null) {
            FPSGraph.instance.setVisible(false);
        }

        UIManager.updateBeforeFadeOut();
        SoundManager.instance.setMusicState("MainMenu");
        long long0 = System.currentTimeMillis();
        boolean boolean0 = UIManager.useUIFBO;
        UIManager.useUIFBO = false;
        DebugLog.log("EXITDEBUG: IngameState.exit 2");

        while (true) {
            float float0 = Math.min(1.0F, (float)(System.currentTimeMillis() - long0) / 500.0F);
            boolean boolean1 = true;

            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                if (IsoPlayer.players[int0] != null) {
                    IsoPlayer.setInstance(IsoPlayer.players[int0]);
                    IsoCamera.CamCharacter = IsoPlayer.players[int0];
                    IsoSprite.globalOffsetX = -1.0F;
                    Core.getInstance().StartFrame(int0, boolean1);
                    IsoCamera.frameState.set(int0);
                    IsoWorld.instance.render();
                    Core.getInstance().EndFrame(int0);
                    boolean1 = false;
                }
            }

            Core.getInstance().RenderOffScreenBuffer();
            Core.getInstance().StartFrameUI();
            UIManager.render();
            UIManager.DrawTexture(UIManager.getBlack(), 0.0, 0.0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), float0);
            Core.getInstance().EndFrameUI();
            DebugLog.log("EXITDEBUG: IngameState.exit 3 (alpha=" + float0 + ")");
            if (float0 >= 1.0F) {
                UIManager.useUIFBO = boolean0;
                DebugLog.log("EXITDEBUG: IngameState.exit 4");
                RenderThread.setWaitForRenderState(false);
                SpriteRenderer.instance.notifyRenderStateQueue();

                while (WorldStreamer.instance.isBusy()) {
                    try {
                        Thread.sleep(1L);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }

                DebugLog.log("EXITDEBUG: IngameState.exit 5");
                WorldStreamer.instance.stop();
                LightingThread.instance.stop();
                MapCollisionData.instance.stop();
                ZombiePopulationManager.instance.stop();
                PolygonalMap2.instance.stop();
                DebugLog.log("EXITDEBUG: IngameState.exit 6");

                for (int int1 = 0; int1 < IsoWorld.instance.CurrentCell.ChunkMap.length; int1++) {
                    IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[int1];

                    for (int int2 = 0; int2 < IsoChunkMap.ChunkGridWidth * IsoChunkMap.ChunkGridWidth; int2++) {
                        IsoChunk chunk = chunkMap.getChunk(int2 % IsoChunkMap.ChunkGridWidth, int2 / IsoChunkMap.ChunkGridWidth);
                        if (chunk != null && chunk.refs.contains(chunkMap)) {
                            chunk.refs.remove(chunkMap);
                            if (chunk.refs.isEmpty()) {
                                chunk.removeFromWorld();
                                chunk.doReuseGridsquares();
                            }
                        }
                    }
                }

                ModelManager.instance.Reset();

                for (int int3 = 0; int3 < 4; int3++) {
                    IsoPlayer.players[int3] = null;
                }

                IsoPlayer.Reset();
                ZombieSpawnRecorder.instance.quit();
                DebugLog.log("EXITDEBUG: IngameState.exit 7");
                IsoPlayer.numPlayers = 1;
                Core.getInstance().OffscreenBuffer.destroy();
                WeatherFxMask.destroy();
                IsoRegions.reset();
                Temperature.reset();
                WorldMarkers.instance.reset();
                IsoMarkers.instance.reset();
                SearchMode.reset();
                ZomboidRadio.getInstance().Reset();
                IsoWaveSignal.Reset();
                ErosionGlobals.Reset();
                IsoGenerator.Reset();
                StashSystem.Reset();
                LootRespawn.Reset();
                VehicleCache.Reset();
                VehicleIDMap.instance.Reset();
                IsoWorld.instance.KillCell();
                ItemSoundManager.Reset();
                IsoChunk.Reset();
                ChunkChecksum.Reset();
                ClientServerMap.Reset();
                SinglePlayerClient.Reset();
                SinglePlayerServer.Reset();
                PassengerMap.Reset();
                DeadBodyAtlas.instance.Reset();
                WorldItemAtlas.instance.Reset();
                CorpseFlies.Reset();
                if (PlayerDB.isAvailable()) {
                    PlayerDB.getInstance().close();
                }

                VehiclesDB2.instance.Reset();
                WorldMap.Reset();
                WorldStreamer.instance = new WorldStreamer();
                WorldSimulation.instance.destroy();
                WorldSimulation.instance = new WorldSimulation();
                DebugLog.log("EXITDEBUG: IngameState.exit 8");
                VirtualZombieManager.instance.Reset();
                VirtualZombieManager.instance = new VirtualZombieManager();
                ReanimatedPlayers.instance = new ReanimatedPlayers();
                ScriptManager.instance.Reset();
                GameSounds.Reset();
                VehicleType.Reset();
                TemplateText.Reset();
                LuaEventManager.Reset();
                MapObjects.Reset();
                CGlobalObjects.Reset();
                SGlobalObjects.Reset();
                AmbientStreamManager.instance.stop();
                SoundManager.instance.stop();
                IsoPlayer.setInstance(null);
                IsoCamera.CamCharacter = null;
                TutorialManager.instance.StealControl = false;
                UIManager.init();
                ScriptManager.instance.Reset();
                ClothingDecals.Reset();
                BeardStyles.Reset();
                HairStyles.Reset();
                OutfitManager.Reset();
                AnimationSet.Reset();
                GameSounds.Reset();
                SurvivorFactory.Reset();
                ProfessionFactory.Reset();
                TraitFactory.Reset();
                ChooseGameInfo.Reset();
                AttachedLocations.Reset();
                BodyLocations.Reset();
                ContainerOverlays.instance.Reset();
                BentFences.getInstance().Reset();
                BrokenFences.getInstance().Reset();
                TileOverlays.instance.Reset();
                LuaHookManager.Reset();
                CustomPerks.Reset();
                PerkFactory.Reset();
                CustomSandboxOptions.Reset();
                SandboxOptions.Reset();
                LuaManager.init();
                JoypadManager.instance.Reset();
                GameKeyboard.doLuaKeyPressed = true;
                GameWindow.ActivatedJoyPad = null;
                GameWindow.OkToSaveOnExit = false;
                GameWindow.bLoadedAsClient = false;
                Core.bLastStand = false;
                Core.ChallengeID = null;
                Core.bTutorial = false;
                Core.getInstance().setChallenge(false);
                Core.getInstance().setForceSnow(false);
                Core.getInstance().setZombieGroupSound(true);
                Core.getInstance().setFlashIsoCursor(false);
                SystemDisabler.Reset();
                Texture.nullTextures.clear();
                DebugLog.log("EXITDEBUG: IngameState.exit 9");
                ZomboidFileSystem.instance.Reset();
                if (!Core.SoundDisabled && !GameServer.bServer) {
                    javafmod.FMOD_System_Update();
                }

                try {
                    ZomboidFileSystem.instance.init();
                } catch (IOException iOException) {
                    ExceptionLogger.logException(iOException);
                }

                Core.OptionModsEnabled = true;
                DebugLog.log("EXITDEBUG: IngameState.exit 10");
                ZomboidFileSystem.instance.loadMods("default");
                ZomboidFileSystem.instance.loadModPackFiles();
                Languages.instance.init();
                Translator.loadFiles();
                DebugLog.log("EXITDEBUG: IngameState.exit 11");
                CustomPerks.instance.init();
                CustomPerks.instance.initLua();
                CustomSandboxOptions.instance.init();
                CustomSandboxOptions.instance.initInstance(SandboxOptions.instance);
                ScriptManager.instance.Load();
                ModelManager.instance.initAnimationMeshes(true);
                ModelManager.instance.loadModAnimations();
                ClothingDecals.init();
                BeardStyles.init();
                HairStyles.init();
                OutfitManager.init();
                DebugLog.log("EXITDEBUG: IngameState.exit 12");

                try {
                    TextManager.instance.Init();
                    LuaManager.LoadDirBase();
                } catch (Exception exception0) {
                    ExceptionLogger.logException(exception0);
                }

                ZomboidGlobals.Load();
                DebugLog.log("EXITDEBUG: IngameState.exit 13");
                LuaEventManager.triggerEvent("OnGameBoot");
                SoundManager.instance.resumeSoundAndMusic();

                for (IsoPlayer player : IsoPlayer.players) {
                    if (player != null) {
                        player.dirtyRecalcGridStack = true;
                    }
                }

                RenderThread.setWaitForRenderState(true);
                DebugLog.log("EXITDEBUG: IngameState.exit 14");
                return;
            }

            try {
                Thread.sleep(33L);
            } catch (Exception exception1) {
            }
        }
    }

    @Override
    public void yield() {
        SoundManager.instance.setMusicState("PauseMenu");
    }

    @Override
    public GameState redirectState() {
        if (this.RedirectState != null) {
            GameState gameState = this.RedirectState;
            this.RedirectState = null;
            return gameState;
        } else {
            return new MainScreenState();
        }
    }

    @Override
    public void reenter() {
        SoundManager.instance.setMusicState("InGame");
    }

    public void renderframetext(int int0) {
        IngameState.s_performance.renderFrameText.invokeAndMeasure(this, int0, IngameState::renderFrameTextInternal);
    }

    private void renderFrameTextInternal(int int1) {
        IndieGL.disableAlphaTest();
        IndieGL.glDisable(2929);
        ArrayList arrayList = UIManager.getUI();

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            UIElement uIElement = (UIElement)arrayList.get(int0);
            if (!(uIElement instanceof ActionProgressBar)
                && uIElement.isVisible()
                && uIElement.isFollowGameWorld()
                && (uIElement.getRenderThisPlayerOnly() == -1 || uIElement.getRenderThisPlayerOnly() == int1)) {
                uIElement.render();
            }
        }

        ActionProgressBar actionProgressBar = UIManager.getProgressBar(int1);
        if (actionProgressBar != null && actionProgressBar.isVisible()) {
            actionProgressBar.render();
        }

        WorldMarkers.instance.render();
        IsoMarkers.instance.render();
        TextDrawObject.RenderBatch((int)int1);
        ChatElement.RenderBatch((int)int1);

        try {
            Core.getInstance().EndFrameText((int)int1);
        } catch (Exception exception) {
        }
    }

    public void renderframe(int int0) {
        IngameState.s_performance.renderFrame.invokeAndMeasure(this, int0, IngameState::renderFrameInternal);
    }

    private void renderFrameInternal(int int0) {
        if (IsoPlayer.getInstance() == null) {
            IsoPlayer.setInstance(IsoPlayer.players[0]);
            IsoCamera.CamCharacter = IsoPlayer.getInstance();
        }

        RenderSettings.getInstance().applyRenderSettings((int)int0);
        ActionProgressBar actionProgressBar = UIManager.getProgressBar(int0);
        if (actionProgressBar != null) {
            actionProgressBar.update((int)int0);
        }

        IndieGL.disableAlphaTest();
        IndieGL.glDisable(2929);
        if (IsoPlayer.getInstance() != null && !IsoPlayer.getInstance().isAsleep() || UIManager.getFadeAlpha(int0) < 1.0F) {
            ModelOutlines.instance.startFrameMain((int)int0);
            IsoWorld.instance.render();
            ModelOutlines.instance.endFrameMain((int)int0);
            RenderSettings.getInstance().legacyPostRender((int)int0);
            LuaEventManager.triggerEvent("OnPostRender");
        }

        LineDrawer.clear();
        if (Core.bDebug && GameKeyboard.isKeyPressed(Core.getInstance().getKey("ToggleAnimationText"))) {
            DebugOptions.instance.Animation.Debug.setValue(!DebugOptions.instance.Animation.Debug.getValue());
        }

        try {
            Core.getInstance().EndFrame((int)int0);
        } catch (Exception exception) {
        }
    }

    public void renderframeui() {
        IngameState.s_performance.renderFrameUI.invokeAndMeasure(this, IngameState::renderFrameUI);
    }

    private void renderFrameUI() {
        if (Core.getInstance().StartFrameUI()) {
            TextManager.instance.DrawTextFromGameWorld();
            SkyBox.getInstance().draw();
            UIManager.render();
            ZomboidRadio.getInstance().render();
            if (Core.bDebug && IsoPlayer.getInstance() != null && IsoPlayer.getInstance().isGhostMode()) {
                IsoWorld.instance.CurrentCell.ChunkMap[0].drawDebugChunkMap();
            }

            DeadBodyAtlas.instance.renderUI();
            WorldItemAtlas.instance.renderUI();
            if (Core.bDebug) {
                if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Display FPS"))) {
                    if (!this.fpsKeyDown) {
                        this.fpsKeyDown = true;
                        if (FPSGraph.instance == null) {
                            FPSGraph.instance = new FPSGraph();
                        }

                        FPSGraph.instance.setVisible(!FPSGraph.instance.isVisible());
                    }
                } else {
                    this.fpsKeyDown = false;
                }

                if (FPSGraph.instance != null) {
                    FPSGraph.instance.render();
                }
            }

            if (!GameServer.bServer) {
                for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                    IsoPlayer player = IsoPlayer.players[int0];
                    if (player != null && !player.isDead() && player.isAsleep()) {
                        float float0 = GameClient.bFastForward ? GameTime.getInstance().ServerTimeOfDay : GameTime.getInstance().getTimeOfDay();
                        float float1 = (float0 - (int)float0) * 60.0F;
                        String string = "media/ui/SleepClock" + (int)float1 / 10 + ".png";
                        Texture texture = Texture.getSharedTexture(string);
                        if (texture == null) {
                            break;
                        }

                        int int1 = IsoCamera.getScreenLeft(int0);
                        int int2 = IsoCamera.getScreenTop(int0);
                        int int3 = IsoCamera.getScreenWidth(int0);
                        int int4 = IsoCamera.getScreenHeight(int0);
                        SpriteRenderer.instance
                            .renderi(
                                texture,
                                int1 + int3 / 2 - texture.getWidth() / 2,
                                int2 + int4 / 2 - texture.getHeight() / 2,
                                texture.getWidth(),
                                texture.getHeight(),
                                1.0F,
                                1.0F,
                                1.0F,
                                1.0F,
                                null
                            );
                    }
                }
            }

            ActiveMods.renderUI();
            JoypadManager.instance.renderUI();
        }

        if (Core.bDebug && DebugOptions.instance.Animation.AnimRenderPicker.getValue() && IsoPlayer.players[0] != null) {
            IsoPlayer.players[0].advancedAnimator.render();
        }

        if (Core.bDebug) {
            ModelOutlines.instance.renderDebug();
        }

        Core.getInstance().EndFrameUI();
    }

    @Override
    public void render() {
        IngameState.s_performance.render.invokeAndMeasure(this, IngameState::renderInternal);
    }

    private void renderInternal() {
        boolean boolean0 = true;

        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            if (IsoPlayer.players[int0] == null) {
                if (int0 == 0) {
                    SpriteRenderer.instance.prePopulating();
                }
            } else {
                IsoPlayer.setInstance(IsoPlayer.players[int0]);
                IsoCamera.CamCharacter = IsoPlayer.players[int0];
                Core.getInstance().StartFrame(int0, boolean0);
                IsoCamera.frameState.set(int0);
                boolean0 = false;
                IsoSprite.globalOffsetX = -1.0F;
                this.renderframe(int0);
            }
        }

        if (DebugOptions.instance.OffscreenBuffer.Render.getValue()) {
            Core.getInstance().RenderOffScreenBuffer();
        }

        for (int int1 = 0; int1 < IsoPlayer.numPlayers; int1++) {
            if (IsoPlayer.players[int1] != null) {
                IsoPlayer.setInstance(IsoPlayer.players[int1]);
                IsoCamera.CamCharacter = IsoPlayer.players[int1];
                IsoCamera.frameState.set(int1);
                Core.getInstance().StartFrameText(int1);
                this.renderframetext(int1);
            }
        }

        UIManager.resize();
        this.renderframeui();
    }

    @Override
    public GameStateMachine.StateAction update() {
        GameStateMachine.StateAction stateAction;
        try {
            IngameState.s_performance.update.start();
            stateAction = this.updateInternal();
        } finally {
            IngameState.s_performance.update.end();
        }

        return stateAction;
    }

    private GameStateMachine.StateAction updateInternal() {
        this.tickCount++;
        if (this.tickCount < 60) {
            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                if (IsoPlayer.players[int0] != null) {
                    IsoPlayer.players[int0].dirtyRecalcGridStackTime = 20.0F;
                }
            }
        }

        LuaEventManager.triggerEvent("OnTickEvenPaused", BoxedStaticValues.toDouble(this.numberTicks));
        DebugFileWatcher.instance.update();
        AdvancedAnimator.checkModifiedFiles();
        if (Core.bDebug) {
            this.debugTimes.clear();
            this.debugTimes.add(System.nanoTime());
        }

        if (Core.bExiting) {
            DebugLog.log("EXITDEBUG: IngameState.updateInternal 1");
            Core.bExiting = false;
            if (GameClient.bClient) {
                for (int int1 = 0; int1 < IsoPlayer.numPlayers; int1++) {
                    IsoPlayer player0 = IsoPlayer.players[int1];
                    if (player0 != null) {
                        ClientPlayerDB.getInstance().clientSendNetworkPlayerInt(player0);
                    }
                }

                try {
                    Thread.sleep(500L);
                } catch (InterruptedException interruptedException) {
                }

                WorldStreamer.instance.stop();
                GameClient.instance.doDisconnect("exiting");
            }

            DebugLog.log("EXITDEBUG: IngameState.updateInternal 2");
            if (PlayerDB.isAllow()) {
                PlayerDB.getInstance().saveLocalPlayersForce();
                PlayerDB.getInstance().m_canSavePlayers = false;
            }

            if (ClientPlayerDB.isAllow()) {
                ClientPlayerDB.getInstance().canSavePlayers = false;
            }

            try {
                GameWindow.save(true);
            } catch (Throwable throwable0) {
                ExceptionLogger.logException(throwable0);
            }

            DebugLog.log("EXITDEBUG: IngameState.updateInternal 3");

            try {
                LuaEventManager.triggerEvent("OnPostSave");
            } catch (Exception exception0) {
                ExceptionLogger.logException(exception0);
            }

            if (ClientPlayerDB.isAllow()) {
                ClientPlayerDB.getInstance().close();
            }

            return GameStateMachine.StateAction.Continue;
        } else if (GameWindow.bServerDisconnected) {
            TutorialManager.instance.StealControl = true;
            if (!this.bDidServerDisconnectState) {
                this.bDidServerDisconnectState = true;
                this.RedirectState = new ServerDisconnectState();
                return GameStateMachine.StateAction.Yield;
            } else {
                GameClient.connection = null;
                GameClient.instance.bConnected = false;
                GameClient.bClient = false;
                GameWindow.bServerDisconnected = false;
                return GameStateMachine.StateAction.Continue;
            }
        } else {
            if (Core.bDebug) {
                if (this.showGlobalObjectDebugger || GameKeyboard.isKeyPressed(60) && GameKeyboard.isKeyDown(29)) {
                    this.showGlobalObjectDebugger = false;
                    DebugLog.General.debugln("Activating DebugGlobalObjectState.");
                    this.RedirectState = new DebugGlobalObjectState();
                    return GameStateMachine.StateAction.Yield;
                }

                if (this.showChunkDebugger || GameKeyboard.isKeyPressed(60)) {
                    this.showChunkDebugger = false;
                    DebugLog.General.debugln("Activating DebugChunkState.");
                    this.RedirectState = DebugChunkState.checkInstance();
                    return GameStateMachine.StateAction.Yield;
                }

                if (this.showAnimationViewer || GameKeyboard.isKeyPressed(65) && GameKeyboard.isKeyDown(29)) {
                    this.showAnimationViewer = false;
                    DebugLog.General.debugln("Activating AnimationViewerState.");
                    AnimationViewerState animationViewerState = AnimationViewerState.checkInstance();
                    this.RedirectState = animationViewerState;
                    return GameStateMachine.StateAction.Yield;
                }

                if (this.showAttachmentEditor || GameKeyboard.isKeyPressed(65) && GameKeyboard.isKeyDown(42)) {
                    this.showAttachmentEditor = false;
                    DebugLog.General.debugln("Activating AttachmentEditorState.");
                    AttachmentEditorState attachmentEditorState = AttachmentEditorState.checkInstance();
                    this.RedirectState = attachmentEditorState;
                    return GameStateMachine.StateAction.Yield;
                }

                if (this.showVehicleEditor != null || GameKeyboard.isKeyPressed(65)) {
                    DebugLog.General.debugln("Activating EditVehicleState.");
                    EditVehicleState editVehicleState = EditVehicleState.checkInstance();
                    if (!StringUtils.isNullOrWhitespace(this.showVehicleEditor)) {
                        editVehicleState.setScript(this.showVehicleEditor);
                    }

                    this.showVehicleEditor = null;
                    this.RedirectState = editVehicleState;
                    return GameStateMachine.StateAction.Yield;
                }

                if (this.showWorldMapEditor != null || GameKeyboard.isKeyPressed(66)) {
                    WorldMapEditorState worldMapEditorState = WorldMapEditorState.checkInstance();
                    this.showWorldMapEditor = null;
                    this.RedirectState = worldMapEditorState;
                    return GameStateMachine.StateAction.Yield;
                }
            }

            if (Core.bDebug) {
                this.debugTimes.add(System.nanoTime());
            }

            this.timesincelastinsanity++;
            if (Core.bDebug) {
                this.debugTimes.add(System.nanoTime());
            }

            try {
                if (!GameServer.bServer && IsoPlayer.getInstance() != null && IsoPlayer.allPlayersDead()) {
                    if (IsoPlayer.getInstance() != null) {
                        UIManager.getSpeedControls().SetCurrentGameSpeed(1);
                    }

                    IsoCamera.update();
                }

                this.alt = !this.alt;
                if (!GameServer.bServer) {
                    WaitMul = 1;
                    if (UIManager.getSpeedControls() != null) {
                        if (UIManager.getSpeedControls().getCurrentGameSpeed() == 2) {
                            WaitMul = 15;
                        }

                        if (UIManager.getSpeedControls().getCurrentGameSpeed() == 3) {
                            WaitMul = 30;
                        }
                    }
                }

                if (Core.bDebug) {
                    this.debugTimes.add(System.nanoTime());
                }

                if (GameServer.bServer) {
                    if (GameServer.Players.isEmpty() && ServerOptions.instance.PauseEmpty.getValue()) {
                        this.Paused = true;
                    } else {
                        this.Paused = false;
                    }
                }

                if (!this.Paused || GameClient.bClient) {
                    try {
                        if (IsoCamera.CamCharacter != null && IsoWorld.instance.bDoChunkMapUpdate) {
                            for (int int2 = 0; int2 < IsoPlayer.numPlayers; int2++) {
                                if (IsoPlayer.players[int2] != null && !IsoWorld.instance.CurrentCell.ChunkMap[int2].ignore) {
                                    if (!GameServer.bServer) {
                                        IsoCamera.CamCharacter = IsoPlayer.players[int2];
                                        IsoPlayer.setInstance(IsoPlayer.players[int2]);
                                    }

                                    if (!GameServer.bServer) {
                                        IsoWorld.instance.CurrentCell.ChunkMap[int2].ProcessChunkPos(IsoCamera.CamCharacter);
                                    }
                                }
                            }
                        }

                        if (Core.bDebug) {
                            this.debugTimes.add(System.nanoTime());
                        }

                        IsoWorld.instance.update();
                        if (Core.bDebug) {
                            this.debugTimes.add(System.nanoTime());
                        }

                        ZomboidRadio.getInstance().update();
                        this.UpdateStuff();
                        LuaEventManager.triggerEvent("OnTick", (double)this.numberTicks);
                        this.numberTicks = Math.max(this.numberTicks + 1L, 0L);
                    } catch (Exception exception1) {
                        ExceptionLogger.logException(exception1);
                        if (!GameServer.bServer) {
                            if (GameClient.bClient) {
                                for (int int3 = 0; int3 < IsoPlayer.numPlayers; int3++) {
                                    IsoPlayer player1 = IsoPlayer.players[int3];
                                    if (player1 != null) {
                                        ClientPlayerDB.getInstance().clientSendNetworkPlayerInt(player1);
                                    }
                                }

                                WorldStreamer.instance.stop();
                            }

                            String string = Core.GameSaveWorld;
                            createWorld(Core.GameSaveWorld + "_crash");
                            copyWorld(string, Core.GameSaveWorld);
                            if (GameClient.bClient) {
                                if (PlayerDB.isAllow()) {
                                    PlayerDB.getInstance().saveLocalPlayersForce();
                                    PlayerDB.getInstance().m_canSavePlayers = false;
                                }

                                if (ClientPlayerDB.isAllow()) {
                                    ClientPlayerDB.getInstance().canSavePlayers = false;
                                }
                            }

                            try {
                                GameWindow.save(true);
                            } catch (Throwable throwable1) {
                                ExceptionLogger.logException(throwable1);
                            }

                            if (GameClient.bClient) {
                                try {
                                    LuaEventManager.triggerEvent("OnPostSave");
                                } catch (Exception exception2) {
                                    ExceptionLogger.logException(exception2);
                                }

                                if (ClientPlayerDB.isAllow()) {
                                    ClientPlayerDB.getInstance().close();
                                }
                            }
                        }

                        if (GameClient.bClient) {
                            GameClient.instance.doDisconnect("crash");
                        }

                        return GameStateMachine.StateAction.Continue;
                    }
                }
            } catch (Exception exception3) {
                System.err.println("IngameState.update caught an exception.");
                ExceptionLogger.logException(exception3);
            }

            if (Core.bDebug) {
                this.debugTimes.add(System.nanoTime());
            }

            if (!GameServer.bServer || ServerGUI.isCreated()) {
                ModelManager.instance.update();
            }

            if (Core.bDebug && FPSGraph.instance != null) {
                FPSGraph.instance.addUpdate(System.currentTimeMillis());
                FPSGraph.instance.update();
            }

            if (GameClient.bClient || GameServer.bServer) {
                ItemTransactionManager.update();
                MPStatistics.Update();
            }

            return GameStateMachine.StateAction.Remain;
        }
    }

    private static class s_performance {
        static final PerformanceProfileProbe render = new PerformanceProfileProbe("IngameState.render");
        static final PerformanceProfileProbe renderFrame = new PerformanceProfileProbe("IngameState.renderFrame");
        static final PerformanceProfileProbe renderFrameText = new PerformanceProfileProbe("IngameState.renderFrameText");
        static final PerformanceProfileProbe renderFrameUI = new PerformanceProfileProbe("IngameState.renderFrameUI");
        static final PerformanceProfileProbe update = new PerformanceProfileProbe("IngameState.update");
    }
}
