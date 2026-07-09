// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.Vector2i;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaClosure;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.MovingObjectUpdateScheduler;
import zombie.ReanimatedPlayers;
import zombie.SandboxOptions;
import zombie.VirtualZombieManager;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaHookManager;
import zombie.Lua.LuaManager;
import zombie.ai.astar.Mover;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.physics.WorldSimulation;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.profiling.PerformanceProfileProbeList;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.core.utils.IntGrid;
import zombie.core.utils.OnceEvery;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.debug.LineDrawer;
import zombie.erosion.utils.Noise2D;
import zombie.gameStates.GameLoadingState;
import zombie.input.JoypadManager;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.BuildingScore;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.sprite.CorpseFlies;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.sprite.shapers.FloorShaper;
import zombie.iso.sprite.shapers.FloorShaperAttachedSprites;
import zombie.iso.sprite.shapers.FloorShaperDiamond;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.fog.ImprovedFog;
import zombie.iso.weather.fx.IsoWeatherFX;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.popman.NetworkZombieSimulator;
import zombie.savefile.ClientPlayerDB;
import zombie.savefile.PlayerDB;
import zombie.scripting.objects.VehicleScript;
import zombie.ui.UIManager;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;

/**
 * Loaded area 'reality bubble' around the player(s). Don't confuse this with map cells - the name is a relic from when it did actually represent these. Only one instance should ever exist. Instantiating this class during gameplay will likely immediately crash.
 */
public final class IsoCell {
    public static int MaxHeight = 8;
    private static Shader m_floorRenderShader;
    private static Shader m_wallRenderShader;
    public ArrayList<IsoGridSquare> Trees = new ArrayList<>();
    static final ArrayList<IsoGridSquare> stchoices = new ArrayList<>();
    public final IsoChunkMap[] ChunkMap = new IsoChunkMap[4];
    public final ArrayList<IsoBuilding> BuildingList = new ArrayList<>();
    private final ArrayList<IsoWindow> WindowList = new ArrayList<>();
    private final ArrayList<IsoMovingObject> ObjectList = new ArrayList<>();
    private final ArrayList<IsoPushableObject> PushableObjectList = new ArrayList<>();
    private final HashMap<Integer, BuildingScore> BuildingScores = new HashMap<>();
    private final ArrayList<IsoRoom> RoomList = new ArrayList<>();
    private final ArrayList<IsoObject> StaticUpdaterObjectList = new ArrayList<>();
    private final ArrayList<IsoZombie> ZombieList = new ArrayList<>();
    private final ArrayList<IsoGameCharacter> RemoteSurvivorList = new ArrayList<>();
    private final ArrayList<IsoMovingObject> removeList = new ArrayList<>();
    private final ArrayList<IsoMovingObject> addList = new ArrayList<>();
    private final ArrayList<IsoObject> ProcessIsoObject = new ArrayList<>();
    private final ArrayList<IsoObject> ProcessIsoObjectRemove = new ArrayList<>();
    private final ArrayList<InventoryItem> ProcessItems = new ArrayList<>();
    private final ArrayList<InventoryItem> ProcessItemsRemove = new ArrayList<>();
    private final ArrayList<IsoWorldInventoryObject> ProcessWorldItems = new ArrayList<>();
    public final ArrayList<IsoWorldInventoryObject> ProcessWorldItemsRemove = new ArrayList<>();
    private final IsoGridSquare[][] gridSquares = new IsoGridSquare[4][IsoChunkMap.ChunkWidthInTiles * IsoChunkMap.ChunkWidthInTiles * 8];
    public static final boolean ENABLE_SQUARE_CACHE = true;
    private int height;
    private int width;
    private int worldX;
    private int worldY;
    public IntGrid DangerScore;
    private boolean safeToAdd = true;
    private final Stack<IsoLightSource> LamppostPositions = new Stack<>();
    public final ArrayList<IsoRoomLight> roomLights = new ArrayList<>();
    private final ArrayList<IsoHeatSource> heatSources = new ArrayList<>();
    public final ArrayList<BaseVehicle> addVehicles = new ArrayList<>();
    public final ArrayList<BaseVehicle> vehicles = new ArrayList<>();
    public static final int ISOANGLEFACTOR = 3;
    private static final int ZOMBIESCANBUDGET = 10;
    private static final float NEARESTZOMBIEDISTSQRMAX = 150.0F;
    private int zombieScanCursor = 0;
    private final IsoZombie[] nearestVisibleZombie = new IsoZombie[4];
    private final float[] nearestVisibleZombieDistSqr = new float[4];
    private static Stack<BuildingScore> buildingscores = new Stack<>();
    static ArrayList<IsoGridSquare> GridStack = null;
    public static final int RTF_SolidFloor = 1;
    public static final int RTF_VegetationCorpses = 2;
    public static final int RTF_MinusFloorCharacters = 4;
    public static final int RTF_ShadedFloor = 8;
    public static final int RTF_Shadows = 16;
    private static final ArrayList<IsoGridSquare> ShadowSquares = new ArrayList<>(1000);
    private static final ArrayList<IsoGridSquare> MinusFloorCharacters = new ArrayList<>(1000);
    private static final ArrayList<IsoGridSquare> SolidFloor = new ArrayList<>(5000);
    private static final ArrayList<IsoGridSquare> ShadedFloor = new ArrayList<>(5000);
    private static final ArrayList<IsoGridSquare> VegetationCorpses = new ArrayList<>(5000);
    public static final IsoCell.PerPlayerRender[] perPlayerRender = new IsoCell.PerPlayerRender[4];
    private final int[] StencilXY = new int[]{0, 0, -1, 0, 0, -1, -1, -1, -2, -1, -1, -2, -2, -2, -3, -2, -2, -3, -3, -3};
    private final int[] StencilXY2z = new int[]{
        0, 0, -1, 0, 0, -1, -1, -1, -2, -1, -1, -2, -2, -2, -3, -2, -2, -3, -3, -3, -4, -3, -3, -4, -4, -4, -5, -4, -4, -5, -5, -5, -6, -5, -5, -6, -6, -6
    };
    public int StencilX1;
    public int StencilY1;
    public int StencilX2;
    public int StencilY2;
    private Texture m_stencilTexture = null;
    private final DiamondMatrixIterator diamondMatrixIterator = new DiamondMatrixIterator(123);
    private final Vector2i diamondMatrixPos = new Vector2i();
    public int DeferredCharacterTick = 0;
    private boolean hasSetupSnowGrid = false;
    private IsoCell.SnowGridTiles snowGridTiles_Square;
    private IsoCell.SnowGridTiles[] snowGridTiles_Strip;
    private IsoCell.SnowGridTiles[] snowGridTiles_Edge;
    private IsoCell.SnowGridTiles[] snowGridTiles_Cove;
    private IsoCell.SnowGridTiles snowGridTiles_Enclosed;
    private int m_snowFirstNonSquare = -1;
    private Noise2D snowNoise2D = new Noise2D();
    private IsoCell.SnowGrid snowGridCur;
    private IsoCell.SnowGrid snowGridPrev;
    private int snowFracTarget = 0;
    private long snowFadeTime = 0L;
    private float snowTransitionTime = 5000.0F;
    private int raport = 0;
    private static final int SNOWSHORE_NONE = 0;
    private static final int SNOWSHORE_N = 1;
    private static final int SNOWSHORE_E = 2;
    private static final int SNOWSHORE_S = 4;
    private static final int SNOWSHORE_W = 8;
    public boolean recalcFloors = false;
    static int wx;
    static int wy;
    final KahluaTable[] drag = new KahluaTable[4];
    final ArrayList<IsoSurvivor> SurvivorList = new ArrayList<>();
    private static Texture texWhite;
    private static IsoCell instance;
    private int currentLX = 0;
    private int currentLY = 0;
    private int currentLZ = 0;
    int recalcShading = 30;
    int lastMinX = -1234567;
    int lastMinY = -1234567;
    private float rainScroll;
    private int[] rainX = new int[4];
    private int[] rainY = new int[4];
    private Texture[] rainTextures = new Texture[5];
    private long[] rainFileTime = new long[5];
    private float rainAlphaMax = 0.6F;
    private float[] rainAlpha = new float[4];
    protected int rainIntensity = 0;
    protected int rainSpeed = 6;
    int lightUpdateCount = 11;
    public boolean bRendering = false;
    final boolean[] bHideFloors = new boolean[4];
    final int[] unhideFloorsCounter = new int[4];
    boolean bOccludedByOrphanStructureFlag = false;
    int playerPeekedRoomId = -1;
    final ArrayList<ArrayList<IsoBuilding>> playerOccluderBuildings = new ArrayList<>(4);
    final IsoBuilding[][] playerOccluderBuildingsArr = new IsoBuilding[4][];
    final int[] playerWindowPeekingRoomId = new int[4];
    final boolean[] playerHidesOrphanStructures = new boolean[4];
    final boolean[] playerCutawaysDirty = new boolean[4];
    final Vector2 tempCutawaySqrVector = new Vector2();
    ArrayList<Integer> tempPrevPlayerCutawayRoomIDs = new ArrayList<>();
    ArrayList<Integer> tempPlayerCutawayRoomIDs = new ArrayList<>();
    final IsoGridSquare[] lastPlayerSquare = new IsoGridSquare[4];
    final boolean[] lastPlayerSquareHalf = new boolean[4];
    final IsoDirections[] lastPlayerDir = new IsoDirections[4];
    final Vector2[] lastPlayerAngle = new Vector2[4];
    int hidesOrphanStructuresAbove = MaxHeight;
    final Rectangle buildingRectTemp = new Rectangle();
    final ArrayList<ArrayList<IsoBuilding>> zombieOccluderBuildings = new ArrayList<>(4);
    final IsoBuilding[][] zombieOccluderBuildingsArr = new IsoBuilding[4][];
    final IsoGridSquare[] lastZombieSquare = new IsoGridSquare[4];
    final boolean[] lastZombieSquareHalf = new boolean[4];
    final ArrayList<ArrayList<IsoBuilding>> otherOccluderBuildings = new ArrayList<>(4);
    final IsoBuilding[][] otherOccluderBuildingsArr = new IsoBuilding[4][];
    final int mustSeeSquaresRadius = 4;
    final int mustSeeSquaresGridSize = 10;
    final ArrayList<IsoGridSquare> gridSquaresTempLeft = new ArrayList<>(100);
    final ArrayList<IsoGridSquare> gridSquaresTempRight = new ArrayList<>(100);
    private IsoWeatherFX weatherFX;
    private int minX;
    private int maxX;
    private int minY;
    private int maxY;
    private int minZ;
    private int maxZ;
    private OnceEvery dangerUpdate = new OnceEvery(0.4F, false);
    private Thread LightInfoUpdate = null;
    private final Stack<IsoRoom> SpottedRooms = new Stack<>();
    private IsoZombie fakeZombieForHit;

    /**
     * @return the MaxHeight
     */
    public static int getMaxHeight() {
        return MaxHeight;
    }

    public LotHeader getCurrentLotHeader() {
        IsoChunk chunk = this.getChunkForGridSquare((int)IsoCamera.CamCharacter.x, (int)IsoCamera.CamCharacter.y, (int)IsoCamera.CamCharacter.z);
        return chunk.lotheader;
    }

    public IsoChunkMap getChunkMap(int pl) {
        return this.ChunkMap[pl];
    }

    public IsoGridSquare getFreeTile(RoomDef def) {
        stchoices.clear();

        for (int int0 = 0; int0 < def.rects.size(); int0++) {
            RoomDef.RoomRect roomRect = def.rects.get(int0);

            for (int int1 = roomRect.x; int1 < roomRect.x + roomRect.w; int1++) {
                for (int int2 = roomRect.y; int2 < roomRect.y + roomRect.h; int2++) {
                    IsoGridSquare square0 = this.getGridSquare(int1, int2, def.level);
                    if (square0 != null) {
                        square0.setCachedIsFree(false);
                        square0.setCacheIsFree(false);
                        if (square0.isFree(false)) {
                            stchoices.add(square0);
                        }
                    }
                }
            }
        }

        if (stchoices.isEmpty()) {
            return null;
        } else {
            IsoGridSquare square1 = stchoices.get(Rand.Next(stchoices.size()));
            stchoices.clear();
            return square1;
        }
    }

    /**
     * @return the getBuildings
     */
    public static Stack<BuildingScore> getBuildings() {
        return buildingscores;
    }

    public static void setBuildings(Stack<BuildingScore> scores) {
        buildingscores = scores;
    }

    public IsoZombie getNearestVisibleZombie(int playerIndex) {
        return this.nearestVisibleZombie[playerIndex];
    }

    public IsoChunk getChunkForGridSquare(int x, int y, int z) {
        int int0 = x;
        int int1 = y;

        for (int int2 = 0; int2 < IsoPlayer.numPlayers; int2++) {
            if (!this.ChunkMap[int2].ignore) {
                x = int0 - this.ChunkMap[int2].getWorldXMinTiles();
                y = int1 - this.ChunkMap[int2].getWorldYMinTiles();
                if (x >= 0 && y >= 0) {
                    IsoChunkMap chunkMap = this.ChunkMap[int2];
                    x /= 10;
                    chunkMap = this.ChunkMap[int2];
                    y /= 10;
                    IsoChunk chunk = null;
                    chunk = this.ChunkMap[int2].getChunk(x, y);
                    if (chunk != null) {
                        return chunk;
                    }
                }
            }
        }

        return null;
    }

    public IsoChunk getChunk(int _wx, int _wy) {
        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            IsoChunkMap chunkMap = this.ChunkMap[int0];
            if (!chunkMap.ignore) {
                IsoChunk chunk = chunkMap.getChunk(_wx - chunkMap.getWorldXMin(), _wy - chunkMap.getWorldYMin());
                if (chunk != null) {
                    return chunk;
                }
            }
        }

        return null;
    }

    public IsoCell(int _width, int _height) {
        IsoWorld.instance.CurrentCell = this;
        instance = this;
        this.width = _width;
        this.height = _height;

        for (int int0 = 0; int0 < 4; int0++) {
            this.ChunkMap[int0] = new IsoChunkMap(this);
            this.ChunkMap[int0].PlayerID = int0;
            this.ChunkMap[int0].ignore = int0 > 0;
            this.playerOccluderBuildings.add(new ArrayList<>(5));
            this.zombieOccluderBuildings.add(new ArrayList<>(5));
            this.otherOccluderBuildings.add(new ArrayList<>(5));
        }

        WorldReuserThread.instance.run();
    }

    public short getStencilValue(int x, int y, int z) {
        short[][][] shorts0 = perPlayerRender[IsoCamera.frameState.playerIndex].StencilValues;
        int int0 = 0;
        int int1 = 0;

        for (byte byte0 = 0; byte0 < this.StencilXY.length; byte0 += 2) {
            int int2 = -z * 3;
            int int3 = x + int2 + this.StencilXY[byte0];
            int int4 = y + int2 + this.StencilXY[byte0 + 1];
            if (int3 >= this.minX && int3 < this.maxX && int4 >= this.minY && int4 < this.maxY) {
                short[] shorts1 = shorts0[int3 - this.minX][int4 - this.minY];
                if (shorts1[0] != 0) {
                    if (int0 == 0) {
                        int0 = shorts1[0];
                        int1 = shorts1[1];
                    } else {
                        int0 = Math.min(shorts1[0], int0);
                        int1 = Math.max(shorts1[1], int1);
                    }
                }
            }
        }

        if (int0 == 0) {
            return 1;
        } else {
            return int0 > 10 ? (short)(int0 - 10) : (short)(int1 + 1);
        }
    }

    public void setStencilValue(int x, int y, int z, int stencil) {
        short[][][] shorts0 = perPlayerRender[IsoCamera.frameState.playerIndex].StencilValues;

        for (byte byte0 = 0; byte0 < this.StencilXY.length; byte0 += 2) {
            int int0 = -z * 3;
            int int1 = x + int0 + this.StencilXY[byte0];
            int int2 = y + int0 + this.StencilXY[byte0 + 1];
            if (int1 >= this.minX && int1 < this.maxX && int2 >= this.minY && int2 < this.maxY) {
                short[] shorts1 = shorts0[int1 - this.minX][int2 - this.minY];
                if (shorts1[0] == 0) {
                    shorts1[0] = (short)stencil;
                    shorts1[1] = (short)stencil;
                } else {
                    shorts1[0] = (short)Math.min(shorts1[0], stencil);
                    shorts1[1] = (short)Math.max(shorts1[1], stencil);
                }
            }
        }
    }

    public short getStencilValue2z(int x, int y, int z) {
        short[][][] shorts0 = perPlayerRender[IsoCamera.frameState.playerIndex].StencilValues;
        int int0 = 0;
        int int1 = 0;
        int int2 = -z * 3;

        for (byte byte0 = 0; byte0 < this.StencilXY2z.length; byte0 += 2) {
            int int3 = x + int2 + this.StencilXY2z[byte0];
            int int4 = y + int2 + this.StencilXY2z[byte0 + 1];
            if (int3 >= this.minX && int3 < this.maxX && int4 >= this.minY && int4 < this.maxY) {
                short[] shorts1 = shorts0[int3 - this.minX][int4 - this.minY];
                if (shorts1[0] != 0) {
                    if (int0 == 0) {
                        int0 = shorts1[0];
                        int1 = shorts1[1];
                    } else {
                        int0 = Math.min(shorts1[0], int0);
                        int1 = Math.max(shorts1[1], int1);
                    }
                }
            }
        }

        if (int0 == 0) {
            return 1;
        } else {
            return int0 > 10 ? (short)(int0 - 10) : (short)(int1 + 1);
        }
    }

    public void setStencilValue2z(int x, int y, int z, int stencil) {
        short[][][] shorts0 = perPlayerRender[IsoCamera.frameState.playerIndex].StencilValues;
        int int0 = -z * 3;

        for (byte byte0 = 0; byte0 < this.StencilXY2z.length; byte0 += 2) {
            int int1 = x + int0 + this.StencilXY2z[byte0];
            int int2 = y + int0 + this.StencilXY2z[byte0 + 1];
            if (int1 >= this.minX && int1 < this.maxX && int2 >= this.minY && int2 < this.maxY) {
                short[] shorts1 = shorts0[int1 - this.minX][int2 - this.minY];
                if (shorts1[0] == 0) {
                    shorts1[0] = (short)stencil;
                    shorts1[1] = (short)stencil;
                } else {
                    shorts1[0] = (short)Math.min(shorts1[0], stencil);
                    shorts1[1] = (short)Math.max(shorts1[1], stencil);
                }
            }
        }
    }

    public void CalculateVertColoursForTile(IsoGridSquare sqThis, int x, int y, int zz, int playerIndex) {
        IsoGridSquare square0 = !IsoGridSquare.getMatrixBit(sqThis.visionMatrix, 0, 0, 1) ? sqThis.nav[IsoDirections.NW.index()] : null;
        IsoGridSquare square1 = !IsoGridSquare.getMatrixBit(sqThis.visionMatrix, 1, 0, 1) ? sqThis.nav[IsoDirections.N.index()] : null;
        IsoGridSquare square2 = !IsoGridSquare.getMatrixBit(sqThis.visionMatrix, 2, 0, 1) ? sqThis.nav[IsoDirections.NE.index()] : null;
        IsoGridSquare square3 = !IsoGridSquare.getMatrixBit(sqThis.visionMatrix, 2, 1, 1) ? sqThis.nav[IsoDirections.E.index()] : null;
        IsoGridSquare square4 = !IsoGridSquare.getMatrixBit(sqThis.visionMatrix, 2, 2, 1) ? sqThis.nav[IsoDirections.SE.index()] : null;
        IsoGridSquare square5 = !IsoGridSquare.getMatrixBit(sqThis.visionMatrix, 1, 2, 1) ? sqThis.nav[IsoDirections.S.index()] : null;
        IsoGridSquare square6 = !IsoGridSquare.getMatrixBit(sqThis.visionMatrix, 0, 2, 1) ? sqThis.nav[IsoDirections.SW.index()] : null;
        IsoGridSquare square7 = !IsoGridSquare.getMatrixBit(sqThis.visionMatrix, 0, 1, 1) ? sqThis.nav[IsoDirections.W.index()] : null;
        this.CalculateColor(square0, square1, square7, sqThis, 0, playerIndex);
        this.CalculateColor(square1, square2, square3, sqThis, 1, playerIndex);
        this.CalculateColor(square4, square5, square3, sqThis, 2, playerIndex);
        this.CalculateColor(square6, square5, square7, sqThis, 3, playerIndex);
    }

    private Texture getStencilTexture() {
        if (this.m_stencilTexture == null) {
            this.m_stencilTexture = Texture.getSharedTexture("media/mask_circledithernew.png");
        }

        return this.m_stencilTexture;
    }

    public void DrawStencilMask() {
        Texture texture = this.getStencilTexture();
        if (texture != null) {
            IndieGL.glStencilMask(255);
            IndieGL.glClear(1280);
            int int0 = IsoCamera.getOffscreenWidth(IsoPlayer.getPlayerIndex()) / 2;
            int int1 = IsoCamera.getOffscreenHeight(IsoPlayer.getPlayerIndex()) / 2;
            int0 -= texture.getWidth() / (2 / Core.TileScale);
            int1 -= texture.getHeight() / (2 / Core.TileScale);
            IndieGL.enableStencilTest();
            IndieGL.enableAlphaTest();
            IndieGL.glAlphaFunc(516, 0.1F);
            IndieGL.glStencilFunc(519, 128, 255);
            IndieGL.glStencilOp(7680, 7680, 7681);
            IndieGL.glColorMask(false, false, false, false);
            texture.renderstrip(
                int0 - (int)IsoCamera.getRightClickOffX(),
                int1 - (int)IsoCamera.getRightClickOffY(),
                texture.getWidth() * Core.TileScale,
                texture.getHeight() * Core.TileScale,
                1.0F,
                1.0F,
                1.0F,
                1.0F,
                null
            );
            IndieGL.glColorMask(true, true, true, true);
            IndieGL.glStencilFunc(519, 0, 255);
            IndieGL.glStencilOp(7680, 7680, 7680);
            IndieGL.glStencilMask(127);
            IndieGL.glAlphaFunc(519, 0.0F);
            this.StencilX1 = int0 - (int)IsoCamera.getRightClickOffX();
            this.StencilY1 = int1 - (int)IsoCamera.getRightClickOffY();
            this.StencilX2 = this.StencilX1 + texture.getWidth() * Core.TileScale;
            this.StencilY2 = this.StencilY1 + texture.getHeight() * Core.TileScale;
        }
    }

    public void RenderTiles(int _MaxHeight) {
        IsoCell.s_performance.isoCellRenderTiles.invokeAndMeasure(this, _MaxHeight, IsoCell::renderTilesInternal);
    }

    private void renderTilesInternal(int int1) {
        if (DebugOptions.instance.Terrain.RenderTiles.Enable.getValue()) {
            if (m_floorRenderShader == null) {
                RenderThread.invokeOnRenderContext(this::initTileShaders);
            }

            int int0 = IsoCamera.frameState.playerIndex;
            IsoPlayer player = IsoPlayer.players[int0];
            player.dirtyRecalcGridStackTime = player.dirtyRecalcGridStackTime - GameTime.getInstance().getMultiplier() / 4.0F;
            IsoCell.PerPlayerRender perPlayerRenderx = this.getPerPlayerRenderAt(int0);
            perPlayerRenderx.setSize(this.maxX - this.minX + 1, this.maxY - this.minY + 1);
            long long0 = System.currentTimeMillis();
            if (this.minX != perPlayerRenderx.minX
                || this.minY != perPlayerRenderx.minY
                || this.maxX != perPlayerRenderx.maxX
                || this.maxY != perPlayerRenderx.maxY) {
                perPlayerRenderx.minX = this.minX;
                perPlayerRenderx.minY = this.minY;
                perPlayerRenderx.maxX = this.maxX;
                perPlayerRenderx.maxY = this.maxY;
                player.dirtyRecalcGridStack = true;
                WeatherFxMask.forceMaskUpdate(int0);
            }

            IsoCell.s_performance.renderTiles.recalculateAnyGridStacks.start();
            boolean boolean0 = player.dirtyRecalcGridStack;
            this.recalculateAnyGridStacks(perPlayerRenderx, (int)int1, int0, long0);
            IsoCell.s_performance.renderTiles.recalculateAnyGridStacks.end();
            this.DeferredCharacterTick++;
            IsoCell.s_performance.renderTiles.flattenAnyFoliage.start();
            this.flattenAnyFoliage(perPlayerRenderx, int0);
            IsoCell.s_performance.renderTiles.flattenAnyFoliage.end();
            if (this.SetCutawayRoomsForPlayer() || boolean0) {
                IsoGridStack gridStack = perPlayerRenderx.GridStacks;

                for (int int2 = 0; int2 < int1 + 1; int2++) {
                    GridStack = gridStack.Squares.get(int2);

                    for (int int3 = 0; int3 < GridStack.size(); int3++) {
                        IsoGridSquare square = GridStack.get(int3);
                        square.setPlayerCutawayFlag(int0, this.IsCutawaySquare(square, long0), long0);
                    }
                }
            }

            IsoCell.s_performance.renderTiles.performRenderTiles.start();
            this.performRenderTiles(perPlayerRenderx, (int)int1, int0, long0);
            IsoCell.s_performance.renderTiles.performRenderTiles.end();
            this.playerCutawaysDirty[int0] = false;
            ShadowSquares.clear();
            MinusFloorCharacters.clear();
            ShadedFloor.clear();
            SolidFloor.clear();
            VegetationCorpses.clear();
            IsoCell.s_performance.renderTiles.renderDebugPhysics.start();
            this.renderDebugPhysics(int0);
            IsoCell.s_performance.renderTiles.renderDebugPhysics.end();
            IsoCell.s_performance.renderTiles.renderDebugLighting.start();
            this.renderDebugLighting(perPlayerRenderx, (int)int1);
            IsoCell.s_performance.renderTiles.renderDebugLighting.end();
        }
    }

    private void initTileShaders() {
        if (DebugLog.isEnabled(DebugType.Shader)) {
            DebugLog.Shader.debugln("Loading shader: \"floorTile\"");
        }

        m_floorRenderShader = new Shader("floorTile");
        if (DebugLog.isEnabled(DebugType.Shader)) {
            DebugLog.Shader.debugln("Loading shader: \"wallTile\"");
        }

        m_wallRenderShader = new Shader("wallTile");
    }

    private IsoCell.PerPlayerRender getPerPlayerRenderAt(int int0) {
        if (perPlayerRender[int0] == null) {
            perPlayerRender[int0] = new IsoCell.PerPlayerRender();
        }

        return perPlayerRender[int0];
    }

    private void recalculateAnyGridStacks(IsoCell.PerPlayerRender perPlayerRenderx, int int5, int int0, long long0) {
        IsoPlayer player = IsoPlayer.players[int0];
        if (player.dirtyRecalcGridStack) {
            player.dirtyRecalcGridStack = false;
            IsoGridStack gridStack = perPlayerRenderx.GridStacks;
            boolean[][][] booleans0 = perPlayerRenderx.VisiOccludedFlags;
            boolean[][] booleans1 = perPlayerRenderx.VisiCulledFlags;
            int int1 = -1;
            int int2 = -1;
            int int3 = -1;
            WeatherFxMask.setDiamondIterDone(int0);

            for (int int4 = int5; int4 >= 0; int4--) {
                GridStack = gridStack.Squares.get(int4);
                GridStack.clear();
                if (int4 < this.maxZ) {
                    if (DebugOptions.instance.Terrain.RenderTiles.NewRender.getValue()) {
                        DiamondMatrixIterator diamondMatrixIteratorx = this.diamondMatrixIterator.reset(this.maxX - this.minX);
                        IsoGridSquare square0 = null;
                        Vector2i vector2i = this.diamondMatrixPos;

                        while (diamondMatrixIteratorx.next(vector2i)) {
                            if (vector2i.y < this.maxY - this.minY + 1) {
                                square0 = this.ChunkMap[int0].getGridSquare(vector2i.x + this.minX, vector2i.y + this.minY, int4);
                                if (int4 == 0) {
                                    booleans0[vector2i.x][vector2i.y][0] = false;
                                    booleans0[vector2i.x][vector2i.y][1] = false;
                                    booleans1[vector2i.x][vector2i.y] = false;
                                }

                                if (square0 == null) {
                                    WeatherFxMask.addMaskLocation(null, vector2i.x + this.minX, vector2i.y + this.minY, int4);
                                } else {
                                    IsoChunk chunk0 = square0.getChunk();
                                    if (chunk0 != null && square0.IsOnScreen(true)) {
                                        WeatherFxMask.addMaskLocation(square0, vector2i.x + this.minX, vector2i.y + this.minY, int4);
                                        boolean boolean0 = this.IsDissolvedSquare(square0, int0);
                                        square0.setIsDissolved(int0, boolean0, long0);
                                        if (!square0.getIsDissolved(int0, long0)) {
                                            square0.cacheLightInfo();
                                            GridStack.add(square0);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        for (int int6 = this.minY; int6 < this.maxY; int6++) {
                            int int7 = this.minX;
                            IsoGridSquare square1 = this.ChunkMap[int0].getGridSquare(int7, int6, int4);
                            int int8 = IsoDirections.E.index();

                            while (int7 < this.maxX) {
                                if (int4 == 0) {
                                    booleans0[int7 - this.minX][int6 - this.minY][0] = false;
                                    booleans0[int7 - this.minX][int6 - this.minY][1] = false;
                                    booleans1[int7 - this.minX][int6 - this.minY] = false;
                                }

                                if (square1 != null && square1.getY() != int6) {
                                    square1 = null;
                                }

                                byte byte0 = -1;
                                byte byte1 = -1;
                                IsoChunkMap chunkMap0 = this.ChunkMap[int0];
                                int int9 = this.ChunkMap[int0].WorldX - IsoChunkMap.ChunkGridWidth / 2;
                                chunkMap0 = this.ChunkMap[int0];
                                int int10 = int7 - int9 * 10;
                                chunkMap0 = this.ChunkMap[int0];
                                int9 = this.ChunkMap[int0].WorldY - IsoChunkMap.ChunkGridWidth / 2;
                                chunkMap0 = this.ChunkMap[int0];
                                int int11 = int6 - int9 * 10;
                                IsoChunkMap chunkMap1 = this.ChunkMap[int0];
                                int10 /= 10;
                                IsoChunkMap chunkMap2 = this.ChunkMap[int0];
                                int11 /= 10;
                                if (int10 != int1 || int11 != int2) {
                                    IsoChunk chunk1 = this.ChunkMap[int0].getChunkForGridSquare(int7, int6);
                                    if (chunk1 != null) {
                                        int3 = chunk1.maxLevel;
                                    }
                                }

                                int1 = int10;
                                int2 = int11;
                                if (int3 < int4) {
                                    int7++;
                                } else {
                                    if (square1 == null) {
                                        square1 = this.getGridSquare(int7, int6, int4);
                                        if (square1 == null) {
                                            square1 = this.ChunkMap[int0].getGridSquare(int7, int6, int4);
                                            if (square1 == null) {
                                                int7++;
                                                continue;
                                            }
                                        }
                                    }

                                    IsoChunk chunk2 = square1.getChunk();
                                    if (chunk2 != null && square1.IsOnScreen(true)) {
                                        WeatherFxMask.addMaskLocation(square1, square1.x, square1.y, int4);
                                        boolean boolean1 = this.IsDissolvedSquare(square1, int0);
                                        square1.setIsDissolved(int0, boolean1, long0);
                                        if (!square1.getIsDissolved(int0, long0)) {
                                            square1.cacheLightInfo();
                                            GridStack.add(square1);
                                        }
                                    }

                                    square1 = square1.nav[int8];
                                    int7++;
                                }
                            }
                        }
                    }
                }
            }

            this.CullFullyOccludedSquares(gridStack, booleans0, booleans1);
        }
    }

    private void flattenAnyFoliage(IsoCell.PerPlayerRender perPlayerRenderx, int int3) {
        short[][][] shorts = perPlayerRenderx.StencilValues;
        boolean[][] booleans = perPlayerRenderx.FlattenGrassEtc;

        for (int int0 = this.minY; int0 <= this.maxY; int0++) {
            for (int int1 = this.minX; int1 <= this.maxX; int1++) {
                shorts[int1 - this.minX][int0 - this.minY][0] = 0;
                shorts[int1 - this.minX][int0 - this.minY][1] = 0;
                booleans[int1 - this.minX][int0 - this.minY] = false;
            }
        }

        for (int int2 = 0; int2 < this.vehicles.size(); int2++) {
            BaseVehicle vehicle = this.vehicles.get(int2);
            if (!(vehicle.getAlpha(int3) <= 0.0F)) {
                for (int int4 = -2; int4 < 5; int4++) {
                    for (int int5 = -2; int5 < 5; int5++) {
                        int int6 = (int)vehicle.x + int5;
                        int int7 = (int)vehicle.y + int4;
                        if (int6 >= this.minX && int6 <= this.maxX && int7 >= this.minY && int7 <= this.maxY) {
                            booleans[int6 - this.minX][int7 - this.minY] = true;
                        }
                    }
                }
            }
        }
    }

    private void performRenderTiles(IsoCell.PerPlayerRender perPlayerRenderx, int int1, int int3, long long0) {
        IsoGridStack gridStack = perPlayerRenderx.GridStacks;
        boolean[][] booleans = perPlayerRenderx.FlattenGrassEtc;
        Shader shader0;
        Shader shader1;
        if (Core.bDebug && !DebugOptions.instance.Terrain.RenderTiles.UseShaders.getValue()) {
            shader0 = null;
            shader1 = null;
        } else {
            shader0 = m_floorRenderShader;
            shader1 = m_wallRenderShader;
        }

        for (int int0 = 0; int0 < int1 + 1; int0++) {
            IsoCell.s_performance.renderTiles.PperformRenderTilesLayer pperformRenderTilesLayer = IsoCell.s_performance.renderTiles.performRenderTilesLayers
                .start(int0);
            GridStack = gridStack.Squares.get(int0);
            ShadowSquares.clear();
            SolidFloor.clear();
            ShadedFloor.clear();
            VegetationCorpses.clear();
            MinusFloorCharacters.clear();
            IndieGL.glClear(256);
            if (int0 == 0 && DebugOptions.instance.Terrain.RenderTiles.Water.getValue() && DebugOptions.instance.Terrain.RenderTiles.WaterBody.getValue()) {
                pperformRenderTilesLayer.renderIsoWater.start();
                IsoWater.getInstance().render(GridStack, false);
                pperformRenderTilesLayer.renderIsoWater.end();
            }

            pperformRenderTilesLayer.renderFloor.start();

            for (int int2 = 0; int2 < GridStack.size(); int2++) {
                IsoGridSquare square0 = GridStack.get(int2);
                if (square0.chunk == null || !square0.chunk.bLightingNeverDone[int3]) {
                    square0.bFlattenGrassEtc = int0 == 0 && booleans[square0.x - this.minX][square0.y - this.minY];
                    int int4 = square0.renderFloor(shader0);
                    if (!square0.getStaticMovingObjects().isEmpty()) {
                        int4 |= 2;
                        int4 |= 16;
                        if (square0.HasStairs()) {
                            int4 |= 4;
                        }
                    }

                    if (!square0.getWorldObjects().isEmpty()) {
                        int4 |= 2;
                    }

                    if (!square0.getLocalTemporaryObjects().isEmpty()) {
                        int4 |= 4;
                    }

                    for (int int5 = 0; int5 < square0.getMovingObjects().size(); int5++) {
                        IsoMovingObject movingObject = square0.getMovingObjects().get(int5);
                        boolean boolean0 = movingObject.bOnFloor;
                        if (boolean0 && movingObject instanceof IsoZombie zombie0) {
                            boolean0 = zombie0.isProne();
                            if (!BaseVehicle.RENDER_TO_TEXTURE) {
                                boolean0 = false;
                            }
                        }

                        if (boolean0) {
                            int4 |= 2;
                        } else {
                            int4 |= 4;
                        }

                        int4 |= 16;
                    }

                    if (!square0.getDeferedCharacters().isEmpty()) {
                        int4 |= 4;
                    }

                    if (square0.hasFlies()) {
                        int4 |= 4;
                    }

                    if ((int4 & 1) != 0) {
                        SolidFloor.add(square0);
                    }

                    if ((int4 & 8) != 0) {
                        ShadedFloor.add(square0);
                    }

                    if ((int4 & 2) != 0) {
                        VegetationCorpses.add(square0);
                    }

                    if ((int4 & 4) != 0) {
                        MinusFloorCharacters.add(square0);
                    }

                    if ((int4 & 16) != 0) {
                        ShadowSquares.add(square0);
                    }
                }
            }

            pperformRenderTilesLayer.renderFloor.end();
            pperformRenderTilesLayer.renderPuddles.start();
            IsoPuddles.getInstance().render(SolidFloor, int0);
            pperformRenderTilesLayer.renderPuddles.end();
            if (int0 == 0 && DebugOptions.instance.Terrain.RenderTiles.Water.getValue() && DebugOptions.instance.Terrain.RenderTiles.WaterShore.getValue()) {
                pperformRenderTilesLayer.renderShore.start();
                IsoWater.getInstance().render(null, true);
                pperformRenderTilesLayer.renderShore.end();
            }

            if (!SolidFloor.isEmpty()) {
                pperformRenderTilesLayer.renderSnow.start();
                this.RenderSnow(int0);
                pperformRenderTilesLayer.renderSnow.end();
            }

            if (!GridStack.isEmpty()) {
                pperformRenderTilesLayer.renderBlood.start();
                this.ChunkMap[int3].renderBloodForChunks(int0);
                pperformRenderTilesLayer.renderBlood.end();
            }

            if (!ShadedFloor.isEmpty()) {
                pperformRenderTilesLayer.renderFloorShading.start();
                this.RenderFloorShading(int0);
                pperformRenderTilesLayer.renderFloorShading.end();
            }

            WorldMarkers.instance.renderGridSquareMarkers(perPlayerRenderx, int0, int3);
            if (DebugOptions.instance.Terrain.RenderTiles.Shadows.getValue()) {
                pperformRenderTilesLayer.renderShadows.start();
                this.renderShadows();
                pperformRenderTilesLayer.renderShadows.end();
            }

            if (DebugOptions.instance.Terrain.RenderTiles.Lua.getValue()) {
                pperformRenderTilesLayer.luaOnPostFloorLayerDraw.start();
                LuaEventManager.triggerEvent("OnPostFloorLayerDraw", int0);
                pperformRenderTilesLayer.luaOnPostFloorLayerDraw.end();
            }

            IsoMarkers.instance.renderIsoMarkers(perPlayerRenderx, int0, int3);
            IsoMarkers.instance.renderCircleIsoMarkers(perPlayerRenderx, int0, int3);
            if (DebugOptions.instance.Terrain.RenderTiles.VegetationCorpses.getValue()) {
                pperformRenderTilesLayer.vegetationCorpses.start();

                for (int int6 = 0; int6 < VegetationCorpses.size(); int6++) {
                    IsoGridSquare square1 = VegetationCorpses.get(int6);
                    square1.renderMinusFloor(this.maxZ, false, true, false, false, false, shader1);
                    square1.renderCharacters(this.maxZ, true, true);
                }

                pperformRenderTilesLayer.vegetationCorpses.end();
            }

            ImprovedFog.startRender(int3, int0);
            if (DebugOptions.instance.Terrain.RenderTiles.MinusFloorCharacters.getValue()) {
                pperformRenderTilesLayer.minusFloorCharacters.start();

                for (int int7 = 0; int7 < MinusFloorCharacters.size(); int7++) {
                    IsoGridSquare square2 = MinusFloorCharacters.get(int7);
                    IsoGridSquare square3 = square2.nav[IsoDirections.S.index()];
                    IsoGridSquare square4 = square2.nav[IsoDirections.E.index()];
                    boolean boolean1 = square3 != null && square3.getPlayerCutawayFlag(int3, long0);
                    boolean boolean2 = square2.getPlayerCutawayFlag(int3, long0);
                    boolean boolean3 = square4 != null && square4.getPlayerCutawayFlag(int3, long0);
                    this.currentLY = square2.getY() - this.minY;
                    this.currentLZ = int0;
                    ImprovedFog.renderRowsBehind(square2);
                    boolean boolean4 = square2.renderMinusFloor(this.maxZ, false, false, boolean1, boolean2, boolean3, shader1);
                    square2.renderDeferredCharacters(this.maxZ);
                    square2.renderCharacters(this.maxZ, false, true);
                    if (square2.hasFlies()) {
                        CorpseFlies.render(square2.x, square2.y, square2.z);
                    }

                    if (boolean4) {
                        square2.renderMinusFloor(this.maxZ, true, false, boolean1, boolean2, boolean3, shader1);
                    }
                }

                pperformRenderTilesLayer.minusFloorCharacters.end();
            }

            IsoMarkers.instance.renderIsoMarkersDeferred(perPlayerRenderx, int0, int3);
            ImprovedFog.endRender();
            pperformRenderTilesLayer.end();
        }
    }

    private void renderShadows() {
        boolean boolean0 = Core.getInstance().getOptionCorpseShadows();

        for (int int0 = 0; int0 < ShadowSquares.size(); int0++) {
            IsoGridSquare square = ShadowSquares.get(int0);

            for (int int1 = 0; int1 < square.getMovingObjects().size(); int1++) {
                IsoMovingObject movingObject0 = square.getMovingObjects().get(int1);
                IsoGameCharacter character = Type.tryCastTo(movingObject0, IsoGameCharacter.class);
                if (character != null) {
                    character.renderShadow(character.getX(), character.getY(), character.getZ());
                } else {
                    BaseVehicle vehicle = Type.tryCastTo(movingObject0, BaseVehicle.class);
                    if (vehicle != null) {
                        vehicle.renderShadow();
                    }
                }
            }

            if (boolean0) {
                for (int int2 = 0; int2 < square.getStaticMovingObjects().size(); int2++) {
                    IsoMovingObject movingObject1 = square.getStaticMovingObjects().get(int2);
                    IsoDeadBody deadBody = Type.tryCastTo(movingObject1, IsoDeadBody.class);
                    if (deadBody != null) {
                        deadBody.renderShadow();
                    }
                }
            }
        }
    }

    private void renderDebugPhysics(int int0) {
        if (Core.bDebug && DebugOptions.instance.PhysicsRender.getValue()) {
            TextureDraw.GenericDrawer genericDrawer = WorldSimulation.getDrawer(int0);
            SpriteRenderer.instance.drawGeneric(genericDrawer);
        }
    }

    private void renderDebugLighting(IsoCell.PerPlayerRender perPlayerRenderx, int int1) {
        if (Core.bDebug && DebugOptions.instance.LightingRender.getValue()) {
            IsoGridStack gridStack = perPlayerRenderx.GridStacks;
            byte byte0 = 1;

            for (int int0 = 0; int0 < int1 + 1; int0++) {
                GridStack = gridStack.Squares.get(int0);

                for (int int2 = 0; int2 < GridStack.size(); int2++) {
                    IsoGridSquare square = GridStack.get(int2);
                    float float0 = IsoUtils.XToScreenExact(square.x + 0.3F, square.y, 0.0F, 0);
                    float float1 = IsoUtils.YToScreenExact(square.x + 0.3F, square.y, 0.0F, 0);
                    float float2 = IsoUtils.XToScreenExact(square.x + 0.6F, square.y, 0.0F, 0);
                    float float3 = IsoUtils.YToScreenExact(square.x + 0.6F, square.y, 0.0F, 0);
                    float float4 = IsoUtils.XToScreenExact(square.x + 1, square.y + 0.3F, 0.0F, 0);
                    float float5 = IsoUtils.YToScreenExact(square.x + 1, square.y + 0.3F, 0.0F, 0);
                    float float6 = IsoUtils.XToScreenExact(square.x + 1, square.y + 0.6F, 0.0F, 0);
                    float float7 = IsoUtils.YToScreenExact(square.x + 1, square.y + 0.6F, 0.0F, 0);
                    float float8 = IsoUtils.XToScreenExact(square.x + 0.6F, square.y + 1, 0.0F, 0);
                    float float9 = IsoUtils.YToScreenExact(square.x + 0.6F, square.y + 1, 0.0F, 0);
                    float float10 = IsoUtils.XToScreenExact(square.x + 0.3F, square.y + 1, 0.0F, 0);
                    float float11 = IsoUtils.YToScreenExact(square.x + 0.3F, square.y + 1, 0.0F, 0);
                    float float12 = IsoUtils.XToScreenExact(square.x, square.y + 0.6F, 0.0F, 0);
                    float float13 = IsoUtils.YToScreenExact(square.x, square.y + 0.6F, 0.0F, 0);
                    float float14 = IsoUtils.XToScreenExact(square.x, square.y + 0.3F, 0.0F, 0);
                    float float15 = IsoUtils.YToScreenExact(square.x, square.y + 0.3F, 0.0F, 0);
                    if (IsoGridSquare.getMatrixBit(square.visionMatrix, 0, 0, byte0)) {
                        LineDrawer.drawLine(float0, float1, float2, float3, 1.0F, 0.0F, 0.0F, 1.0F, 0);
                    }

                    if (IsoGridSquare.getMatrixBit(square.visionMatrix, 0, 1, byte0)) {
                        LineDrawer.drawLine(float2, float3, float4, float5, 1.0F, 0.0F, 0.0F, 1.0F, 0);
                    }

                    if (IsoGridSquare.getMatrixBit(square.visionMatrix, 0, 2, byte0)) {
                        LineDrawer.drawLine(float4, float5, float6, float7, 1.0F, 0.0F, 0.0F, 1.0F, 0);
                    }

                    if (IsoGridSquare.getMatrixBit(square.visionMatrix, 1, 2, byte0)) {
                        LineDrawer.drawLine(float6, float7, float8, float9, 1.0F, 0.0F, 0.0F, 1.0F, 0);
                    }

                    if (IsoGridSquare.getMatrixBit(square.visionMatrix, 2, 2, byte0)) {
                        LineDrawer.drawLine(float8, float9, float10, float11, 1.0F, 0.0F, 0.0F, 1.0F, 0);
                    }

                    if (IsoGridSquare.getMatrixBit(square.visionMatrix, 2, 1, byte0)) {
                        LineDrawer.drawLine(float10, float11, float12, float13, 1.0F, 0.0F, 0.0F, 1.0F, 0);
                    }

                    if (IsoGridSquare.getMatrixBit(square.visionMatrix, 2, 0, byte0)) {
                        LineDrawer.drawLine(float12, float13, float14, float15, 1.0F, 0.0F, 0.0F, 1.0F, 0);
                    }

                    if (IsoGridSquare.getMatrixBit(square.visionMatrix, 1, 0, byte0)) {
                        LineDrawer.drawLine(float14, float15, float0, float1, 1.0F, 0.0F, 0.0F, 1.0F, 0);
                    }
                }
            }
        }
    }

    private void CullFullyOccludedSquares(IsoGridStack gridStack, boolean[][][] booleans1, boolean[][] booleans0) {
        int int0 = 0;

        for (int int1 = 1; int1 < MaxHeight + 1; int1++) {
            int0 += gridStack.Squares.get(int1).size();
        }

        if (int0 >= 500) {
            int int2 = 0;

            for (int int3 = MaxHeight; int3 >= 0; int3--) {
                GridStack = gridStack.Squares.get(int3);

                for (int int4 = GridStack.size() - 1; int4 >= 0; int4--) {
                    IsoGridSquare square = GridStack.get(int4);
                    int int5 = square.getX() - int3 * 3 - this.minX;
                    int int6 = square.getY() - int3 * 3 - this.minY;
                    if (int5 < 0 || int5 >= booleans0.length) {
                        GridStack.remove(int4);
                    } else if (int6 >= 0 && int6 < booleans0[0].length) {
                        if (int3 < MaxHeight) {
                            boolean boolean0 = !booleans0[int5][int6];
                            if (boolean0) {
                                boolean0 = false;
                                if (int5 > 2) {
                                    if (int6 > 2) {
                                        boolean0 = !booleans1[int5 - 3][int6 - 3][0]
                                            || !booleans1[int5 - 3][int6 - 3][1]
                                            || !booleans1[int5 - 3][int6 - 2][0]
                                            || !booleans1[int5 - 2][int6 - 3][1]
                                            || !booleans1[int5 - 2][int6 - 2][0]
                                            || !booleans1[int5 - 2][int6 - 2][1]
                                            || !booleans1[int5 - 2][int6 - 1][0]
                                            || !booleans1[int5 - 1][int6 - 2][0]
                                            || !booleans1[int5 - 1][int6 - 1][1]
                                            || !booleans1[int5 - 1][int6 - 1][0]
                                            || !booleans1[int5 - 1][int6][0]
                                            || !booleans1[int5][int6 - 1][1]
                                            || !booleans1[int5][int6][0]
                                            || !booleans1[int5][int6][1];
                                    } else if (int6 > 1) {
                                        boolean0 = !booleans1[int5 - 3][int6 - 2][0]
                                            || !booleans1[int5 - 2][int6 - 2][0]
                                            || !booleans1[int5 - 2][int6 - 2][1]
                                            || !booleans1[int5 - 2][int6 - 1][0]
                                            || !booleans1[int5 - 1][int6 - 2][0]
                                            || !booleans1[int5 - 1][int6 - 1][1]
                                            || !booleans1[int5 - 1][int6 - 1][0]
                                            || !booleans1[int5 - 1][int6][0]
                                            || !booleans1[int5][int6 - 1][1]
                                            || !booleans1[int5][int6][0]
                                            || !booleans1[int5][int6][1];
                                    } else if (int6 > 0) {
                                        boolean0 = !booleans1[int5 - 2][int6 - 1][0]
                                            || !booleans1[int5 - 1][int6 - 1][1]
                                            || !booleans1[int5 - 1][int6 - 1][0]
                                            || !booleans1[int5 - 1][int6][0]
                                            || !booleans1[int5][int6 - 1][1]
                                            || !booleans1[int5][int6][0]
                                            || !booleans1[int5][int6][1];
                                    } else {
                                        boolean0 = !booleans1[int5 - 1][int6][0] || !booleans1[int5][int6][0] || !booleans1[int5][int6][1];
                                    }
                                } else if (int5 > 1) {
                                    if (int6 > 2) {
                                        boolean0 = !booleans1[int5 - 2][int6 - 3][1]
                                            || !booleans1[int5 - 2][int6 - 2][0]
                                            || !booleans1[int5 - 2][int6 - 2][1]
                                            || !booleans1[int5 - 2][int6 - 1][0]
                                            || !booleans1[int5 - 1][int6 - 2][0]
                                            || !booleans1[int5 - 1][int6 - 1][1]
                                            || !booleans1[int5 - 1][int6 - 1][0]
                                            || !booleans1[int5 - 1][int6][0]
                                            || !booleans1[int5][int6 - 1][1]
                                            || !booleans1[int5][int6][0]
                                            || !booleans1[int5][int6][1];
                                    } else if (int6 > 1) {
                                        boolean0 = !booleans1[int5 - 2][int6 - 2][0]
                                            || !booleans1[int5 - 2][int6 - 2][1]
                                            || !booleans1[int5 - 2][int6 - 1][0]
                                            || !booleans1[int5 - 1][int6 - 2][0]
                                            || !booleans1[int5 - 1][int6 - 1][1]
                                            || !booleans1[int5 - 1][int6 - 1][0]
                                            || !booleans1[int5 - 1][int6][0]
                                            || !booleans1[int5][int6 - 1][1]
                                            || !booleans1[int5][int6][0]
                                            || !booleans1[int5][int6][1];
                                    } else if (int6 > 0) {
                                        boolean0 = !booleans1[int5 - 2][int6 - 1][0]
                                            || !booleans1[int5 - 1][int6 - 1][1]
                                            || !booleans1[int5 - 1][int6 - 1][0]
                                            || !booleans1[int5 - 1][int6][0]
                                            || !booleans1[int5][int6 - 1][1]
                                            || !booleans1[int5][int6][0]
                                            || !booleans1[int5][int6][1];
                                    } else {
                                        boolean0 = !booleans1[int5 - 1][int6][0] || !booleans1[int5][int6][0] || !booleans1[int5][int6][1];
                                    }
                                } else if (int5 > 0) {
                                    if (int6 > 2) {
                                        boolean0 = !booleans1[int5 - 1][int6 - 2][0]
                                            || !booleans1[int5 - 1][int6 - 1][1]
                                            || !booleans1[int5 - 1][int6 - 1][0]
                                            || !booleans1[int5 - 1][int6][0]
                                            || !booleans1[int5][int6 - 1][1]
                                            || !booleans1[int5][int6][0]
                                            || !booleans1[int5][int6][1];
                                    } else if (int6 > 1) {
                                        boolean0 = !booleans1[int5 - 1][int6 - 2][0]
                                            || !booleans1[int5 - 1][int6 - 1][1]
                                            || !booleans1[int5 - 1][int6 - 1][0]
                                            || !booleans1[int5 - 1][int6][0]
                                            || !booleans1[int5][int6 - 1][1]
                                            || !booleans1[int5][int6][0]
                                            || !booleans1[int5][int6][1];
                                    } else if (int6 > 0) {
                                        boolean0 = !booleans1[int5 - 1][int6 - 1][1]
                                            || !booleans1[int5 - 1][int6 - 1][0]
                                            || !booleans1[int5 - 1][int6][0]
                                            || !booleans1[int5][int6 - 1][1]
                                            || !booleans1[int5][int6][0]
                                            || !booleans1[int5][int6][1];
                                    } else {
                                        boolean0 = !booleans1[int5 - 1][int6][0] || !booleans1[int5][int6][0] || !booleans1[int5][int6][1];
                                    }
                                } else if (int6 > 2) {
                                    boolean0 = !booleans1[int5][int6 - 1][1] || !booleans1[int5][int6][0] || !booleans1[int5][int6][1];
                                } else if (int6 > 1) {
                                    boolean0 = !booleans1[int5][int6 - 1][1] || !booleans1[int5][int6][0] || !booleans1[int5][int6][1];
                                } else if (int6 > 0) {
                                    boolean0 = !booleans1[int5][int6 - 1][1] || !booleans1[int5][int6][0] || !booleans1[int5][int6][1];
                                } else {
                                    boolean0 = !booleans1[int5][int6][0] || !booleans1[int5][int6][1];
                                }
                            }

                            if (!boolean0) {
                                GridStack.remove(int4);
                                booleans0[int5][int6] = true;
                                continue;
                            }
                        }

                        int2++;
                        boolean boolean1 = IsoGridSquare.getMatrixBit(square.visionMatrix, 0, 1, 1) && square.getProperties().Is(IsoFlagType.cutW);
                        boolean boolean2 = IsoGridSquare.getMatrixBit(square.visionMatrix, 1, 0, 1) && square.getProperties().Is(IsoFlagType.cutN);
                        boolean boolean3 = false;
                        if (boolean1 || boolean2) {
                            boolean3 = (square.x > IsoCamera.frameState.CamCharacterX || square.y > IsoCamera.frameState.CamCharacterY)
                                && square.z >= (int)IsoCamera.frameState.CamCharacterZ;
                            if (boolean3) {
                                int int7 = (int)(square.CachedScreenX - IsoCamera.frameState.OffX);
                                int int8 = (int)(square.CachedScreenY - IsoCamera.frameState.OffY);
                                if (int7 + 32 * Core.TileScale <= this.StencilX1
                                    || int7 - 32 * Core.TileScale >= this.StencilX2
                                    || int8 + 32 * Core.TileScale <= this.StencilY1
                                    || int8 - 96 * Core.TileScale >= this.StencilY2) {
                                    boolean3 = false;
                                }
                            }
                        }

                        int int9 = 0;
                        if (boolean1 && !boolean3) {
                            int9++;
                            if (int5 > 0) {
                                booleans1[int5 - 1][int6][0] = true;
                                if (int6 > 0) {
                                    booleans1[int5 - 1][int6 - 1][1] = true;
                                }
                            }

                            if (int5 > 1 && int6 > 0) {
                                booleans1[int5 - 2][int6 - 1][0] = true;
                                if (int6 > 1) {
                                    booleans1[int5 - 2][int6 - 2][1] = true;
                                }
                            }

                            if (int5 > 2 && int6 > 1) {
                                booleans1[int5 - 3][int6 - 2][0] = true;
                                if (int6 > 2) {
                                    booleans1[int5 - 3][int6 - 3][1] = true;
                                }
                            }
                        }

                        if (boolean2 && !boolean3) {
                            int9++;
                            if (int6 > 0) {
                                booleans1[int5][int6 - 1][1] = true;
                                if (int5 > 0) {
                                    booleans1[int5 - 1][int6 - 1][0] = true;
                                }
                            }

                            if (int6 > 1 && int5 > 0) {
                                booleans1[int5 - 1][int6 - 2][1] = true;
                                if (int5 > 1) {
                                    booleans1[int5 - 2][int6 - 2][0] = true;
                                }
                            }

                            if (int6 > 2 && int5 > 1) {
                                booleans1[int5 - 2][int6 - 3][1] = true;
                                if (int5 > 2) {
                                    booleans1[int5 - 3][int6 - 3][0] = true;
                                }
                            }
                        }

                        if (IsoGridSquare.getMatrixBit(square.visionMatrix, 1, 1, 0)) {
                            int9++;
                            booleans1[int5][int6][0] = true;
                            booleans1[int5][int6][1] = true;
                        }

                        if (int9 == 3) {
                            booleans0[int5][int6] = true;
                        }
                    } else {
                        GridStack.remove(int4);
                    }
                }
            }
        }
    }

    public void RenderFloorShading(int zza) {
        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Floor.LightingOld.getValue()
            && !DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Floor.Lighting.getValue()) {
            if (zza < this.maxZ && PerformanceSettings.LightingFrameSkip < 3) {
                if (!Core.bDebug || !DebugOptions.instance.DebugDraw_SkipWorldShading.getValue()) {
                    if (texWhite == null) {
                        texWhite = Texture.getWhite();
                    }

                    Texture texture = texWhite;
                    if (texture != null) {
                        int int0 = IsoCamera.frameState.playerIndex;
                        int int1 = (int)IsoCamera.frameState.OffX;
                        int int2 = (int)IsoCamera.frameState.OffY;

                        for (int int3 = 0; int3 < ShadedFloor.size(); int3++) {
                            IsoGridSquare square = ShadedFloor.get(int3);
                            if (square.getProperties().Is(IsoFlagType.solidfloor)) {
                                float float0 = 0.0F;
                                float float1 = 0.0F;
                                float float2 = 0.0F;
                                if (square.getProperties().Is(IsoFlagType.FloorHeightOneThird)) {
                                    float1 = -1.0F;
                                    float0 = -1.0F;
                                } else if (square.getProperties().Is(IsoFlagType.FloorHeightTwoThirds)) {
                                    float1 = -2.0F;
                                    float0 = -2.0F;
                                }

                                float float3 = IsoUtils.XToScreen(square.getX() + float0, square.getY() + float1, zza + float2, 0);
                                float float4 = IsoUtils.YToScreen(square.getX() + float0, square.getY() + float1, zza + float2, 0);
                                float3 -= int1;
                                float4 -= int2;
                                int int4 = square.getVertLight(0, int0);
                                int int5 = square.getVertLight(1, int0);
                                int int6 = square.getVertLight(2, int0);
                                int int7 = square.getVertLight(3, int0);
                                if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Floor.LightingDebug.getValue()) {
                                    int4 = -65536;
                                    int5 = -65536;
                                    int6 = -16776961;
                                    int7 = -16776961;
                                }

                                texture.renderdiamond(
                                    float3 - 32 * Core.TileScale,
                                    float4 + 16 * Core.TileScale,
                                    64 * Core.TileScale,
                                    32 * Core.TileScale,
                                    int7,
                                    int4,
                                    int5,
                                    int6
                                );
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean IsPlayerWindowPeeking(int playerIndex) {
        return this.playerWindowPeekingRoomId[playerIndex] != -1;
    }

    public boolean CanBuildingSquareOccludePlayer(IsoGridSquare square, int playerIndex) {
        ArrayList arrayList = this.playerOccluderBuildings.get(playerIndex);

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            IsoBuilding building = (IsoBuilding)arrayList.get(int0);
            int int1 = building.getDef().getX();
            int int2 = building.getDef().getY();
            int int3 = building.getDef().getX2() - int1;
            int int4 = building.getDef().getY2() - int2;
            this.buildingRectTemp.setBounds(int1 - 1, int2 - 1, int3 + 2, int4 + 2);
            if (this.buildingRectTemp.contains(square.getX(), square.getY())) {
                return true;
            }
        }

        return false;
    }

    public int GetEffectivePlayerRoomId() {
        int int0 = IsoCamera.frameState.playerIndex;
        int int1 = this.playerWindowPeekingRoomId[int0];
        if (IsoPlayer.players[int0] != null && IsoPlayer.players[int0].isClimbing()) {
            int1 = -1;
        }

        if (int1 != -1) {
            return int1;
        } else {
            IsoGridSquare square = IsoPlayer.players[int0].current;
            return square != null ? square.getRoomID() : -1;
        }
    }

    private boolean SetCutawayRoomsForPlayer() {
        int int0 = IsoCamera.frameState.playerIndex;
        IsoPlayer player = IsoPlayer.players[int0];
        ArrayList arrayList = this.tempPrevPlayerCutawayRoomIDs;
        this.tempPrevPlayerCutawayRoomIDs = this.tempPlayerCutawayRoomIDs;
        this.tempPlayerCutawayRoomIDs = arrayList;
        this.tempPlayerCutawayRoomIDs.clear();
        IsoGridSquare square0 = player.getSquare();
        if (square0 == null) {
            return false;
        } else {
            IsoBuilding building = square0.getBuilding();
            int int1 = square0.getRoomID();
            boolean boolean0 = false;
            if (int1 == -1) {
                if (this.playerWindowPeekingRoomId[int0] != -1) {
                    this.tempPlayerCutawayRoomIDs.add(this.playerWindowPeekingRoomId[int0]);
                } else {
                    boolean0 = this.playerCutawaysDirty[int0];
                }
            } else {
                int int2 = (int)(player.getX() - 1.5F);
                int int3 = (int)(player.getY() - 1.5F);
                int int4 = (int)(player.getX() + 1.5F);
                int int5 = (int)(player.getY() + 1.5F);

                for (int int6 = int2; int6 <= int4; int6++) {
                    for (int int7 = int3; int7 <= int5; int7++) {
                        IsoGridSquare square1 = this.getGridSquare(int6, int7, square0.getZ());
                        if (square1 != null) {
                            int int8 = square1.getRoomID();
                            if (square1.getCanSee(int0) && int8 != -1 && !this.tempPlayerCutawayRoomIDs.contains(int8)) {
                                this.tempCutawaySqrVector.set(square1.getX() + 0.5F - player.getX(), square1.getY() + 0.5F - player.getY());
                                if (square0 == square1 || player.getForwardDirection().dot(this.tempCutawaySqrVector) > 0.0F) {
                                    this.tempPlayerCutawayRoomIDs.add(int8);
                                }
                            }
                        }
                    }
                }

                Collections.sort(this.tempPlayerCutawayRoomIDs);
            }

            return boolean0 || !this.tempPlayerCutawayRoomIDs.equals(this.tempPrevPlayerCutawayRoomIDs);
        }
    }

    private boolean IsCutawaySquare(IsoGridSquare square0, long long0) {
        int int0 = IsoCamera.frameState.playerIndex;
        IsoPlayer player = IsoPlayer.players[int0];
        if (player.current == null) {
            return false;
        } else if (square0 == null) {
            return false;
        } else {
            IsoGridSquare square1 = player.current;
            if (square1.getZ() != square0.getZ()) {
                return false;
            } else {
                if (this.tempPlayerCutawayRoomIDs.isEmpty()) {
                    IsoGridSquare square2 = square0.nav[IsoDirections.N.index()];
                    IsoGridSquare square3 = square0.nav[IsoDirections.W.index()];
                    if (this.IsCollapsibleBuildingSquare(square0)) {
                        if (player.getZ() == 0.0F) {
                            return true;
                        }

                        if (square0.getBuilding() != null && (square1.getX() < square0.getBuilding().def.x || square1.getY() < square0.getBuilding().def.y)) {
                            return true;
                        }

                        IsoGridSquare square4 = square0;

                        for (int int1 = 0; int1 < 3; int1++) {
                            square4 = square4.nav[IsoDirections.NW.index()];
                            if (square4 == null) {
                                break;
                            }

                            if (square4.isCanSee(int0)) {
                                return true;
                            }
                        }
                    }

                    if (square2 != null && square2.getRoomID() == -1 && square3 != null && square3.getRoomID() == -1) {
                        return this.DoesSquareHaveValidCutaways(square1, square0, int0, long0);
                    }
                } else {
                    IsoGridSquare square5 = square0.nav[IsoDirections.N.index()];
                    IsoGridSquare square6 = square0.nav[IsoDirections.E.index()];
                    IsoGridSquare square7 = square0.nav[IsoDirections.S.index()];
                    IsoGridSquare square8 = square0.nav[IsoDirections.W.index()];
                    IsoGridSquare square9 = square1.nav[IsoDirections.N.index()];
                    IsoGridSquare square10 = square1.nav[IsoDirections.E.index()];
                    IsoGridSquare square11 = square1.nav[IsoDirections.S.index()];
                    IsoGridSquare square12 = square1.nav[IsoDirections.W.index()];
                    boolean boolean0 = false;
                    boolean boolean1 = false;

                    for (int int2 = 0; int2 < 8; int2++) {
                        if (square0.nav[int2] != null && square0.nav[int2].getRoomID() != square0.getRoomID()) {
                            boolean0 = true;
                            break;
                        }
                    }

                    if (!this.tempPlayerCutawayRoomIDs.contains(square0.getRoomID())) {
                        boolean1 = true;
                    }

                    if (boolean0 || boolean1 || square0.getWall() != null) {
                        IsoGridSquare square13 = square0;

                        for (int int3 = 0; int3 < 3; int3++) {
                            square13 = square13.nav[IsoDirections.NW.index()];
                            if (square13 == null) {
                                break;
                            }

                            if (square13.getRoomID() != -1 && this.tempPlayerCutawayRoomIDs.contains(square13.getRoomID())) {
                                if ((boolean0 || boolean1) && square13.getCanSee(int0)) {
                                    return true;
                                }

                                if (square0.getWall() != null && square13.isCouldSee(int0)) {
                                    return true;
                                }
                            }
                        }
                    }

                    if (square5 != null
                        && square8 != null
                        && (
                            square5.getThumpableWallOrHoppable(false) != null
                                || square8.getThumpableWallOrHoppable(true) != null
                                || square0.getThumpableWallOrHoppable(true) != null
                                || square0.getThumpableWallOrHoppable(false) != null
                        )) {
                        return this.DoesSquareHaveValidCutaways(square1, square0, int0, long0);
                    }

                    if (square1.getRoomID() == -1
                        && (
                            square9 != null && square9.getRoomID() != -1
                                || square10 != null && square10.getRoomID() != -1
                                || square11 != null && square11.getRoomID() != -1
                                || square12 != null && square12.getRoomID() != -1
                        )) {
                        int int4 = square1.x - square0.x;
                        int int5 = square1.y - square0.y;
                        if (int4 < 0 && int5 < 0) {
                            if (int4 >= -3) {
                                if (int5 >= -3) {
                                    return true;
                                }

                                if (square5 != null
                                    && square7 != null
                                    && square0.getWall(false) != null
                                    && square5.getWall(false) != null
                                    && square7.getWall(false) != null
                                    && square7.getPlayerCutawayFlag(int0, long0)) {
                                    return true;
                                }
                            } else if (square6 != null && square8 != null) {
                                if (square0.getWall(true) != null
                                    && square8.getWall(true) != null
                                    && square6.getWall(true) != null
                                    && square6.getPlayerCutawayFlag(int0, long0)) {
                                    return true;
                                }

                                if (square0.getWall(true) != null
                                    && square8.getWall(true) != null
                                    && square6.getWall(true) != null
                                    && square6.getPlayerCutawayFlag(int0, long0)) {
                                    return true;
                                }
                            }
                        }
                    }
                }

                return false;
            }
        }
    }

    private boolean DoesSquareHaveValidCutaways(IsoGridSquare square6, IsoGridSquare square1, int int1, long long0) {
        IsoGridSquare square0 = square1.nav[IsoDirections.N.index()];
        IsoGridSquare square2 = square1.nav[IsoDirections.E.index()];
        IsoGridSquare square3 = square1.nav[IsoDirections.S.index()];
        IsoGridSquare square4 = square1.nav[IsoDirections.W.index()];
        IsoObject object0 = square1.getWall(true);
        IsoObject object1 = square1.getWall(false);
        IsoObject object2 = null;
        IsoObject object3 = null;
        if (square0 != null && square0.nav[IsoDirections.W.index()] != null && square0.nav[IsoDirections.W.index()].getRoomID() == square0.getRoomID()) {
            object3 = square0.getWall(false);
        }

        if (square4 != null && square4.nav[IsoDirections.N.index()] != null && square4.nav[IsoDirections.N.index()].getRoomID() == square4.getRoomID()) {
            object2 = square4.getWall(true);
        }

        if (object1 != null || object0 != null || object3 != null || object2 != null) {
            IsoGridSquare square5 = square1.nav[IsoDirections.NW.index()];

            for (int int0 = 0; int0 < 2 && square5 != null && square5.getRoomID() == square6.getRoomID(); int0++) {
                IsoGridSquare square7 = square5.nav[IsoDirections.S.index()];
                IsoGridSquare square8 = square5.nav[IsoDirections.E.index()];
                if (square7 != null && square7.getBuilding() != null || square8 != null && square8.getBuilding() != null) {
                    break;
                }

                if (square5.isCanSee(int1) && square5.isCouldSee(int1) && square5.DistTo(square6) <= 6 - (int0 + 1)) {
                    return true;
                }

                if (square5.getBuilding() == null) {
                    square5 = square5.nav[IsoDirections.NW.index()];
                }
            }
        }

        int int2 = square6.x - square1.x;
        int int3 = square6.y - square1.y;
        if ((object0 == null || !object0.sprite.name.contains("fencing")) && (object1 == null || !object1.sprite.name.contains("fencing"))) {
            if (square1.DistTo(square6) <= 6.0F
                && square1.nav[IsoDirections.NW.index()] != null
                && square1.nav[IsoDirections.NW.index()].getRoomID() == square1.getRoomID()
                && (square1.getWall(true) == null || square1.getWall(true) == object0)
                && (square1.getWall(false) == null || square1.getWall(false) == object1)) {
                if (square3 != null && square0 != null && int3 != 0) {
                    if (int3 > 0
                        && object1 != null
                        && square3.getWall(false) != null
                        && square0.getWall(false) != null
                        && square3.getPlayerCutawayFlag(int1, long0)) {
                        return true;
                    }

                    if (int3 < 0 && object1 != null && square0.getWall(false) != null && square0.getPlayerCutawayFlag(int1, long0)) {
                        return true;
                    }
                }

                if (square2 != null && square4 != null && int2 != 0) {
                    if (int2 > 0
                        && object0 != null
                        && square2.getWall(true) != null
                        && square4.getWall(true) != null
                        && square2.getPlayerCutawayFlag(int1, long0)) {
                        return true;
                    }

                    if (int2 < 0 && object0 != null && square4.getWall(true) != null && square4.getPlayerCutawayFlag(int1, long0)) {
                        return true;
                    }
                }
            }
        } else {
            if (object0 != null && object2 != null && int3 >= -6 && int3 < 0) {
                return true;
            }

            if (object1 != null && object3 != null && int2 >= -6 && int2 < 0) {
                return true;
            }
        }

        if (square1 == square6 && square1.nav[IsoDirections.NW.index()] != null && square1.nav[IsoDirections.NW.index()].getRoomID() == square1.getRoomID()) {
            if (object0 != null && square0 != null && square0.getWall(false) == null && square0.isCanSee(int1) && square0.isCouldSee(int1)) {
                return true;
            }

            if (object1 != null && square4 != null && square4.getWall(true) != null && square4.isCanSee(int1) && square4.isCouldSee(int1)) {
                return true;
            }
        }

        return square0 != null
                && square4 != null
                && int2 != 0
                && int3 != 0
                && object3 != null
                && object2 != null
                && square0.getPlayerCutawayFlag(int1, long0)
                && square4.getPlayerCutawayFlag(int1, long0)
            ? true
            : int2 < 0
                && int2 >= -6
                && int3 < 0
                && int3 >= -6
                && (object1 != null && square1.getWall(true) == null || object0 != null && square1.getWall(false) == null);
    }

    private boolean IsCollapsibleBuildingSquare(IsoGridSquare square) {
        if (square.getProperties().Is(IsoFlagType.forceRender)) {
            return false;
        } else {
            for (int int0 = 0; int0 < 4; int0++) {
                short short0 = 500;

                for (int int1 = 0; int1 < short0 && this.playerOccluderBuildingsArr[int0] != null; int1++) {
                    IsoBuilding building0 = this.playerOccluderBuildingsArr[int0][int1];
                    if (building0 == null) {
                        break;
                    }

                    BuildingDef buildingDef0 = building0.getDef();
                    if (this.collapsibleBuildingSquareAlgorithm(buildingDef0, square, IsoPlayer.players[int0].getSquare())) {
                        return true;
                    }

                    if (square.getY() - buildingDef0.getY2() == 1 && square.getWall(true) != null) {
                        return true;
                    }

                    if (square.getX() - buildingDef0.getX2() == 1 && square.getWall(false) != null) {
                        return true;
                    }
                }
            }

            int int2 = IsoCamera.frameState.playerIndex;
            IsoPlayer player = IsoPlayer.players[int2];
            if (player.getVehicle() != null) {
                return false;
            } else {
                for (int int3 = 0; int3 < 500 && this.zombieOccluderBuildingsArr[int2] != null; int3++) {
                    IsoBuilding building1 = this.zombieOccluderBuildingsArr[int2][int3];
                    if (building1 == null) {
                        break;
                    }

                    BuildingDef buildingDef1 = building1.getDef();
                    if (this.collapsibleBuildingSquareAlgorithm(buildingDef1, square, player.getSquare())) {
                        return true;
                    }
                }

                for (int int4 = 0; int4 < 500 && this.otherOccluderBuildingsArr[int2] != null; int4++) {
                    IsoBuilding building2 = this.otherOccluderBuildingsArr[int2][int4];
                    if (building2 == null) {
                        break;
                    }

                    BuildingDef buildingDef2 = building2.getDef();
                    if (this.collapsibleBuildingSquareAlgorithm(buildingDef2, square, player.getSquare())) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    private boolean collapsibleBuildingSquareAlgorithm(BuildingDef buildingDef, IsoGridSquare square2, IsoGridSquare square0) {
        int int0 = buildingDef.getX();
        int int1 = buildingDef.getY();
        int int2 = buildingDef.getX2() - int0;
        int int3 = buildingDef.getY2() - int1;
        this.buildingRectTemp.setBounds(int0, int1, int2, int3);
        if (square0.getRoomID() == -1 && this.buildingRectTemp.contains(square0.getX(), square0.getY())) {
            this.buildingRectTemp.setBounds(int0 - 1, int1 - 1, int2 + 2, int3 + 2);
            IsoGridSquare square1 = square2.nav[IsoDirections.N.index()];
            IsoGridSquare square3 = square2.nav[IsoDirections.W.index()];
            IsoGridSquare square4 = square2.nav[IsoDirections.NW.index()];
            if (square4 != null && square1 != null && square3 != null) {
                boolean boolean0 = square2.getRoomID() == -1;
                boolean boolean1 = square1.getRoomID() == -1;
                boolean boolean2 = square3.getRoomID() == -1;
                boolean boolean3 = square4.getRoomID() == -1;
                boolean boolean4 = square0.getY() < square2.getY();
                boolean boolean5 = square0.getX() < square2.getX();
                return this.buildingRectTemp.contains(square2.getX(), square2.getY())
                    && (
                        square0.getZ() < square2.getZ()
                            || boolean0 && (!boolean1 && boolean4 || !boolean2 && boolean5)
                            || boolean0 && boolean1 && boolean2 && !boolean3
                            || !boolean0 && (boolean3 || boolean1 == boolean2 || boolean1 && boolean5 || boolean2 && boolean4)
                    );
            } else {
                return false;
            }
        } else {
            this.buildingRectTemp.setBounds(int0 - 1, int1 - 1, int2 + 2, int3 + 2);
            return this.buildingRectTemp.contains(square2.getX(), square2.getY());
        }
    }

    private boolean IsDissolvedSquare(IsoGridSquare square1, int int0) {
        IsoPlayer player = IsoPlayer.players[int0];
        if (player.current == null) {
            return false;
        } else {
            IsoGridSquare square0 = player.current;
            if (square0.getZ() >= square1.getZ()) {
                return false;
            } else if (!PerformanceSettings.NewRoofHiding) {
                return this.bHideFloors[int0] && square1.getZ() >= this.maxZ;
            } else {
                if (square1.getZ() > this.hidesOrphanStructuresAbove) {
                    IsoBuilding building = square1.getBuilding();
                    if (building == null) {
                        building = square1.roofHideBuilding;
                    }

                    for (int int1 = square1.getZ() - 1; int1 >= 0 && building == null; int1--) {
                        IsoGridSquare square2 = this.getGridSquare(square1.x, square1.y, int1);
                        if (square2 != null) {
                            building = square2.getBuilding();
                            if (building == null) {
                                building = square2.roofHideBuilding;
                            }
                        }
                    }

                    if (building == null) {
                        if (square1.isSolidFloor()) {
                            return true;
                        }

                        IsoGridSquare square3 = square1.nav[IsoDirections.N.index()];
                        if (square3 != null && square3.getBuilding() == null) {
                            if (square3.getPlayerBuiltFloor() != null) {
                                return true;
                            }

                            if (square3.HasStairsBelow()) {
                                return true;
                            }
                        }

                        IsoGridSquare square4 = square1.nav[IsoDirections.W.index()];
                        if (square4 != null && square4.getBuilding() == null) {
                            if (square4.getPlayerBuiltFloor() != null) {
                                return true;
                            }

                            if (square4.HasStairsBelow()) {
                                return true;
                            }
                        }

                        if (square1.Is(IsoFlagType.WallSE)) {
                            IsoGridSquare square5 = square1.nav[IsoDirections.NW.index()];
                            if (square5 != null && square5.getBuilding() == null) {
                                if (square5.getPlayerBuiltFloor() != null) {
                                    return true;
                                }

                                if (square5.HasStairsBelow()) {
                                    return true;
                                }
                            }
                        }
                    }
                }

                return this.IsCollapsibleBuildingSquare(square1);
            }
        }
    }

    private int GetBuildingHeightAt(IsoBuilding building, int int2, int int3, int int1) {
        for (int int0 = MaxHeight; int0 > int1; int0--) {
            IsoGridSquare square = this.getGridSquare(int2, int3, int0);
            if (square != null && square.getBuilding() == building) {
                return int0;
            }
        }

        return int1;
    }

    private void updateSnow(int int0) {
        if (this.snowGridCur == null) {
            this.snowGridCur = new IsoCell.SnowGrid(int0);
            this.snowGridPrev = new IsoCell.SnowGrid(0);
        } else {
            if (int0 != this.snowGridCur.frac) {
                this.snowGridPrev.init(this.snowGridCur.frac);
                this.snowGridCur.init(int0);
                this.snowFadeTime = System.currentTimeMillis();
                DebugLog.log("snow from " + this.snowGridPrev.frac + " to " + this.snowGridCur.frac);
            }
        }
    }

    public void setSnowTarget(int target) {
        if (!SandboxOptions.instance.EnableSnowOnGround.getValue()) {
            target = 0;
        }

        this.snowFracTarget = target;
    }

    public boolean gridSquareIsSnow(int x, int y, int z) {
        IsoGridSquare square = this.getGridSquare(x, y, z);
        if (square != null) {
            if (!square.getProperties().Is(IsoFlagType.solidfloor)) {
                return false;
            } else if (square.getProperties().Is(IsoFlagType.water)) {
                return false;
            } else if (square.getProperties().Is(IsoFlagType.exterior) && square.room == null && !square.isInARoom()) {
                int int0 = square.getX() % this.snowGridCur.w;
                int int1 = square.getY() % this.snowGridCur.h;
                return this.snowGridCur.check(int0, int1);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void RenderSnow(int int6) {
        if (DebugOptions.instance.Weather.Snow.getValue()) {
            this.updateSnow(this.snowFracTarget);
            IsoCell.SnowGrid snowGrid0 = this.snowGridCur;
            if (snowGrid0 != null) {
                IsoCell.SnowGrid snowGrid1 = this.snowGridPrev;
                if (snowGrid0.frac > 0 || snowGrid1.frac > 0) {
                    float float0 = 1.0F;
                    float float1 = 0.0F;
                    long long0 = System.currentTimeMillis();
                    long long1 = long0 - this.snowFadeTime;
                    if ((float)long1 < this.snowTransitionTime) {
                        float float2 = (float)long1 / this.snowTransitionTime;
                        float0 = float2;
                        float1 = 1.0F - float2;
                    }

                    Shader shader = null;
                    if (DebugOptions.instance.Terrain.RenderTiles.UseShaders.getValue()) {
                        shader = m_floorRenderShader;
                    }

                    FloorShaperAttachedSprites.instance.setShore(false);
                    FloorShaperDiamond.instance.setShore(false);
                    IndieGL.StartShader(shader, IsoCamera.frameState.playerIndex);
                    int int0 = (int)IsoCamera.frameState.OffX;
                    int int1 = (int)IsoCamera.frameState.OffY;

                    for (int int2 = 0; int2 < SolidFloor.size(); int2++) {
                        IsoGridSquare square = SolidFloor.get(int2);
                        if (square.room == null && square.getProperties().Is(IsoFlagType.exterior) && square.getProperties().Is(IsoFlagType.solidfloor)) {
                            int int3;
                            if (square.getProperties().Is(IsoFlagType.water)) {
                                int3 = getShoreInt(square);
                                if (int3 == 0) {
                                    continue;
                                }
                            } else {
                                int3 = 0;
                            }

                            int int4 = square.getX() % snowGrid0.w;
                            int int5 = square.getY() % snowGrid0.h;
                            float float3 = IsoUtils.XToScreen(square.getX(), square.getY(), int6, 0);
                            float float4 = IsoUtils.YToScreen(square.getX(), square.getY(), int6, 0);
                            float3 -= int0;
                            float4 -= int1;
                            float float5 = 32 * Core.TileScale;
                            float float6 = 96 * Core.TileScale;
                            float3 -= float5;
                            float4 -= float6;
                            int int7 = IsoCamera.frameState.playerIndex;
                            int int8 = square.getVertLight(0, int7);
                            int int9 = square.getVertLight(1, int7);
                            int int10 = square.getVertLight(2, int7);
                            int int11 = square.getVertLight(3, int7);
                            if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Floor.LightingDebug.getValue()) {
                                int8 = -65536;
                                int9 = -65536;
                                int10 = -16776961;
                                int11 = -16776961;
                            }

                            FloorShaperAttachedSprites.instance.setVertColors(int8, int9, int10, int11);
                            FloorShaperDiamond.instance.setVertColors(int8, int9, int10, int11);

                            for (int int12 = 0; int12 < 2; int12++) {
                                if (float1 > float0) {
                                    this.renderSnowTileGeneral(snowGrid0, float0, square, int3, int4, int5, (int)float3, (int)float4, int12);
                                    this.renderSnowTileGeneral(snowGrid1, float1, square, int3, int4, int5, (int)float3, (int)float4, int12);
                                } else {
                                    this.renderSnowTileGeneral(snowGrid1, float1, square, int3, int4, int5, (int)float3, (int)float4, int12);
                                    this.renderSnowTileGeneral(snowGrid0, float0, square, int3, int4, int5, (int)float3, (int)float4, int12);
                                }
                            }
                        }
                    }

                    IndieGL.StartShader(null);
                }
            }
        }
    }

    private void renderSnowTileGeneral(
        IsoCell.SnowGrid snowGrid, float float0, IsoGridSquare square, int int3, int int2, int int1, int int4, int int5, int int0
    ) {
        if (!(float0 <= 0.0F)) {
            Texture texture = snowGrid.grid[int2][int1][int0];
            if (texture != null) {
                if (int0 == 0) {
                    this.renderSnowTile(snowGrid, int2, int1, int0, square, int3, texture, int4, int5, float0);
                } else if (int3 == 0) {
                    byte byte0 = snowGrid.gridType[int2][int1][int0];
                    this.renderSnowTileBase(texture, int4, int5, float0, byte0 < this.m_snowFirstNonSquare);
                }
            }
        }
    }

    private void renderSnowTileBase(Texture texture, int int1, int int0, float float0, boolean boolean0) {
        Object object = boolean0 ? FloorShaperDiamond.instance : FloorShaperAttachedSprites.instance;
        ((FloorShaper)object).setAlpha4(float0);
        texture.render(int1, int0, texture.getWidth(), texture.getHeight(), 1.0F, 1.0F, 1.0F, float0, (Consumer<TextureDraw>)object);
    }

    private void renderSnowTile(
        IsoCell.SnowGrid snowGrid, int int3, int int2, int int1, IsoGridSquare square, int int0, Texture texture, int int4, int int5, float float0
    ) {
        if (int0 == 0) {
            byte byte0 = snowGrid.gridType[int3][int2][int1];
            this.renderSnowTileBase(texture, int4, int5, float0, byte0 < this.m_snowFirstNonSquare);
        } else {
            int int6 = 0;
            boolean boolean0 = snowGrid.check(int3, int2);
            boolean boolean1 = (int0 & 1) == 1 && (boolean0 || snowGrid.check(int3, int2 - 1));
            boolean boolean2 = (int0 & 2) == 2 && (boolean0 || snowGrid.check(int3 + 1, int2));
            boolean boolean3 = (int0 & 4) == 4 && (boolean0 || snowGrid.check(int3, int2 + 1));
            boolean boolean4 = (int0 & 8) == 8 && (boolean0 || snowGrid.check(int3 - 1, int2));
            if (boolean1) {
                int6++;
            }

            if (boolean3) {
                int6++;
            }

            if (boolean2) {
                int6++;
            }

            if (boolean4) {
                int6++;
            }

            IsoCell.SnowGridTiles snowGridTiles0 = null;
            IsoCell.SnowGridTiles snowGridTiles1 = null;
            boolean boolean5 = false;
            if (int6 != 0) {
                if (int6 == 1) {
                    if (boolean1) {
                        snowGridTiles0 = this.snowGridTiles_Strip[0];
                    } else if (boolean3) {
                        snowGridTiles0 = this.snowGridTiles_Strip[1];
                    } else if (boolean2) {
                        snowGridTiles0 = this.snowGridTiles_Strip[3];
                    } else if (boolean4) {
                        snowGridTiles0 = this.snowGridTiles_Strip[2];
                    }
                } else if (int6 == 2) {
                    if (boolean1 && boolean3) {
                        snowGridTiles0 = this.snowGridTiles_Strip[0];
                        snowGridTiles1 = this.snowGridTiles_Strip[1];
                    } else if (boolean2 && boolean4) {
                        snowGridTiles0 = this.snowGridTiles_Strip[2];
                        snowGridTiles1 = this.snowGridTiles_Strip[3];
                    } else if (boolean1) {
                        snowGridTiles0 = this.snowGridTiles_Edge[boolean4 ? 0 : 3];
                    } else if (boolean3) {
                        snowGridTiles0 = this.snowGridTiles_Edge[boolean4 ? 2 : 1];
                    } else if (boolean4) {
                        snowGridTiles0 = this.snowGridTiles_Edge[boolean1 ? 0 : 2];
                    } else if (boolean2) {
                        snowGridTiles0 = this.snowGridTiles_Edge[boolean1 ? 3 : 1];
                    }
                } else if (int6 == 3) {
                    if (!boolean1) {
                        snowGridTiles0 = this.snowGridTiles_Cove[1];
                    } else if (!boolean3) {
                        snowGridTiles0 = this.snowGridTiles_Cove[0];
                    } else if (!boolean2) {
                        snowGridTiles0 = this.snowGridTiles_Cove[2];
                    } else if (!boolean4) {
                        snowGridTiles0 = this.snowGridTiles_Cove[3];
                    }

                    boolean5 = true;
                } else if (int6 == 4) {
                    snowGridTiles0 = this.snowGridTiles_Enclosed;
                    boolean5 = true;
                }

                if (snowGridTiles0 != null) {
                    int int7 = (square.getX() + square.getY()) % snowGridTiles0.size();
                    texture = snowGridTiles0.get(int7);
                    if (texture != null) {
                        this.renderSnowTileBase(texture, int4, int5, float0, boolean5);
                    }

                    if (snowGridTiles1 != null) {
                        texture = snowGridTiles1.get(int7);
                        if (texture != null) {
                            this.renderSnowTileBase(texture, int4, int5, float0, false);
                        }
                    }
                }
            }
        }
    }

    private static int getShoreInt(IsoGridSquare square) {
        byte byte0 = 0;
        if (isSnowShore(square, 0, -1)) {
            byte0 |= 1;
        }

        if (isSnowShore(square, 1, 0)) {
            byte0 |= 2;
        }

        if (isSnowShore(square, 0, 1)) {
            byte0 |= 4;
        }

        if (isSnowShore(square, -1, 0)) {
            byte0 |= 8;
        }

        return byte0;
    }

    private static boolean isSnowShore(IsoGridSquare square1, int int1, int int0) {
        IsoGridSquare square0 = IsoWorld.instance.getCell().getGridSquare(square1.getX() + int1, square1.getY() + int0, 0);
        return square0 != null && !square0.getProperties().Is(IsoFlagType.water);
    }

    public IsoBuilding getClosestBuildingExcept(IsoGameCharacter chr, IsoRoom except) {
        IsoBuilding building0 = null;
        float float0 = 1000000.0F;

        for (int int0 = 0; int0 < this.BuildingList.size(); int0++) {
            IsoBuilding building1 = this.BuildingList.get(int0);

            for (int int1 = 0; int1 < building1.Exits.size(); int1++) {
                float float1 = chr.DistTo(building1.Exits.get(int1).x, building1.Exits.get(int1).y);
                if (float1 < float0 && (except == null || except.building != building1)) {
                    building0 = building1;
                    float0 = float1;
                }
            }
        }

        return building0;
    }

    public int getDangerScore(int x, int y) {
        return x >= 0 && y >= 0 && x < this.width && y < this.height ? this.DangerScore.getValue(x, y) : 1000000;
    }

    private void ObjectDeletionAddition() {
        for (int int0 = 0; int0 < this.removeList.size(); int0++) {
            IsoMovingObject movingObject0 = this.removeList.get(int0);
            if (movingObject0 instanceof IsoZombie) {
                VirtualZombieManager.instance.RemoveZombie((IsoZombie)movingObject0);
            }

            if (!(movingObject0 instanceof IsoPlayer) || ((IsoPlayer)movingObject0).isDead()) {
                MovingObjectUpdateScheduler.instance.removeObject(movingObject0);
                this.ObjectList.remove(movingObject0);
                if (movingObject0.getCurrentSquare() != null) {
                    movingObject0.getCurrentSquare().getMovingObjects().remove(movingObject0);
                }

                if (movingObject0.getLastSquare() != null) {
                    movingObject0.getLastSquare().getMovingObjects().remove(movingObject0);
                }
            }
        }

        this.removeList.clear();

        for (int int1 = 0; int1 < this.addList.size(); int1++) {
            IsoMovingObject movingObject1 = this.addList.get(int1);
            this.ObjectList.add(movingObject1);
        }

        this.addList.clear();

        for (int int2 = 0; int2 < this.addVehicles.size(); int2++) {
            BaseVehicle vehicle = this.addVehicles.get(int2);
            if (!this.ObjectList.contains(vehicle)) {
                this.ObjectList.add(vehicle);
            }

            if (!this.vehicles.contains(vehicle)) {
                this.vehicles.add(vehicle);
            }
        }

        this.addVehicles.clear();
    }

    private void ProcessItems(Iterator<InventoryItem> var1) {
        int int0 = this.ProcessItems.size();

        for (int int1 = 0; int1 < int0; int1++) {
            InventoryItem item = this.ProcessItems.get(int1);
            item.update();
            if (item.finishupdate()) {
                this.ProcessItemsRemove.add(item);
            }
        }

        int0 = this.ProcessWorldItems.size();

        for (int int2 = 0; int2 < int0; int2++) {
            IsoWorldInventoryObject worldInventoryObject = this.ProcessWorldItems.get(int2);
            worldInventoryObject.update();
            if (worldInventoryObject.finishupdate()) {
                this.ProcessWorldItemsRemove.add(worldInventoryObject);
            }
        }
    }

    private void ProcessIsoObject() {
        this.ProcessIsoObject.removeAll(this.ProcessIsoObjectRemove);
        this.ProcessIsoObjectRemove.clear();
        int int0 = this.ProcessIsoObject.size();

        for (int int1 = 0; int1 < int0; int1++) {
            IsoObject object = this.ProcessIsoObject.get(int1);
            if (object != null) {
                object.update();
                if (int0 > this.ProcessIsoObject.size()) {
                    int1--;
                    int0--;
                }
            }
        }
    }

    private void ProcessObjects(Iterator<IsoMovingObject> var1) {
        MovingObjectUpdateScheduler.instance.update();

        for (int int0 = 0; int0 < this.ZombieList.size(); int0++) {
            IsoZombie zombie0 = this.ZombieList.get(int0);
            zombie0.updateVocalProperties();
        }
    }

    private void ProcessRemoveItems(Iterator<InventoryItem> var1) {
        this.ProcessItems.removeAll(this.ProcessItemsRemove);
        this.ProcessWorldItems.removeAll(this.ProcessWorldItemsRemove);
        this.ProcessItemsRemove.clear();
        this.ProcessWorldItemsRemove.clear();
    }

    private void ProcessStaticUpdaters() {
        int int0 = this.StaticUpdaterObjectList.size();

        for (int int1 = 0; int1 < int0; int1++) {
            try {
                this.StaticUpdaterObjectList.get(int1).update();
            } catch (Exception exception) {
                Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, exception);
            }

            if (int0 > this.StaticUpdaterObjectList.size()) {
                int1--;
                int0--;
            }
        }
    }

    public void addToProcessIsoObject(IsoObject object) {
        if (object != null) {
            this.ProcessIsoObjectRemove.remove(object);
            if (!this.ProcessIsoObject.contains(object)) {
                this.ProcessIsoObject.add(object);
            }
        }
    }

    public void addToProcessIsoObjectRemove(IsoObject object) {
        if (object != null) {
            if (this.ProcessIsoObject.contains(object)) {
                if (!this.ProcessIsoObjectRemove.contains(object)) {
                    this.ProcessIsoObjectRemove.add(object);
                }
            }
        }
    }

    public void addToStaticUpdaterObjectList(IsoObject object) {
        if (object != null) {
            if (!this.StaticUpdaterObjectList.contains(object)) {
                this.StaticUpdaterObjectList.add(object);
            }
        }
    }

    public void addToProcessItems(InventoryItem item) {
        if (item != null) {
            this.ProcessItemsRemove.remove(item);
            if (!this.ProcessItems.contains(item)) {
                this.ProcessItems.add(item);
            }
        }
    }

    public void addToProcessItems(ArrayList<InventoryItem> items) {
        if (items != null) {
            for (int int0 = 0; int0 < items.size(); int0++) {
                InventoryItem item = (InventoryItem)items.get(int0);
                if (item != null) {
                    this.ProcessItemsRemove.remove(item);
                    if (!this.ProcessItems.contains(item)) {
                        this.ProcessItems.add(item);
                    }
                }
            }
        }
    }

    public void addToProcessItemsRemove(InventoryItem item) {
        if (item != null) {
            if (!this.ProcessItemsRemove.contains(item)) {
                this.ProcessItemsRemove.add(item);
            }
        }
    }

    public void addToProcessItemsRemove(ArrayList<InventoryItem> items) {
        if (items != null) {
            for (int int0 = 0; int0 < items.size(); int0++) {
                InventoryItem item = (InventoryItem)items.get(int0);
                if (item != null && !this.ProcessItemsRemove.contains(item)) {
                    this.ProcessItemsRemove.add(item);
                }
            }
        }
    }

    public void addToProcessWorldItems(IsoWorldInventoryObject worldItem) {
        if (worldItem != null) {
            this.ProcessWorldItemsRemove.remove(worldItem);
            if (!this.ProcessWorldItems.contains(worldItem)) {
                this.ProcessWorldItems.add(worldItem);
            }
        }
    }

    public void addToProcessWorldItemsRemove(IsoWorldInventoryObject worldItem) {
        if (worldItem != null) {
            if (!this.ProcessWorldItemsRemove.contains(worldItem)) {
                this.ProcessWorldItemsRemove.add(worldItem);
            }
        }
    }

    public IsoSurvivor getNetworkPlayer(int RemoteID) {
        int int0 = this.RemoteSurvivorList.size();

        for (int int1 = 0; int1 < int0; int1++) {
            if (this.RemoteSurvivorList.get(int1).getRemoteID() == RemoteID) {
                return (IsoSurvivor)this.RemoteSurvivorList.get(int1);
            }
        }

        return null;
    }

    IsoGridSquare ConnectNewSquare(IsoGridSquare square, boolean var2, boolean var3) {
        int int0 = square.getX();
        int int1 = square.getY();
        int int2 = square.getZ();
        this.setCacheGridSquare(int0, int1, int2, square);
        this.DoGridNav(square, IsoGridSquare.cellGetSquare);
        return square;
    }

    public void DoGridNav(IsoGridSquare newSquare, IsoGridSquare.GetSquare getter) {
        int int0 = newSquare.getX();
        int int1 = newSquare.getY();
        int int2 = newSquare.getZ();
        newSquare.nav[IsoDirections.N.index()] = getter.getGridSquare(int0, int1 - 1, int2);
        newSquare.nav[IsoDirections.NW.index()] = getter.getGridSquare(int0 - 1, int1 - 1, int2);
        newSquare.nav[IsoDirections.W.index()] = getter.getGridSquare(int0 - 1, int1, int2);
        newSquare.nav[IsoDirections.SW.index()] = getter.getGridSquare(int0 - 1, int1 + 1, int2);
        newSquare.nav[IsoDirections.S.index()] = getter.getGridSquare(int0, int1 + 1, int2);
        newSquare.nav[IsoDirections.SE.index()] = getter.getGridSquare(int0 + 1, int1 + 1, int2);
        newSquare.nav[IsoDirections.E.index()] = getter.getGridSquare(int0 + 1, int1, int2);
        newSquare.nav[IsoDirections.NE.index()] = getter.getGridSquare(int0 + 1, int1 - 1, int2);
        if (newSquare.nav[IsoDirections.N.index()] != null) {
            newSquare.nav[IsoDirections.N.index()].nav[IsoDirections.S.index()] = newSquare;
        }

        if (newSquare.nav[IsoDirections.NW.index()] != null) {
            newSquare.nav[IsoDirections.NW.index()].nav[IsoDirections.SE.index()] = newSquare;
        }

        if (newSquare.nav[IsoDirections.W.index()] != null) {
            newSquare.nav[IsoDirections.W.index()].nav[IsoDirections.E.index()] = newSquare;
        }

        if (newSquare.nav[IsoDirections.SW.index()] != null) {
            newSquare.nav[IsoDirections.SW.index()].nav[IsoDirections.NE.index()] = newSquare;
        }

        if (newSquare.nav[IsoDirections.S.index()] != null) {
            newSquare.nav[IsoDirections.S.index()].nav[IsoDirections.N.index()] = newSquare;
        }

        if (newSquare.nav[IsoDirections.SE.index()] != null) {
            newSquare.nav[IsoDirections.SE.index()].nav[IsoDirections.NW.index()] = newSquare;
        }

        if (newSquare.nav[IsoDirections.E.index()] != null) {
            newSquare.nav[IsoDirections.E.index()].nav[IsoDirections.W.index()] = newSquare;
        }

        if (newSquare.nav[IsoDirections.NE.index()] != null) {
            newSquare.nav[IsoDirections.NE.index()].nav[IsoDirections.SW.index()] = newSquare;
        }
    }

    public IsoGridSquare ConnectNewSquare(IsoGridSquare newSquare, boolean bDoSurrounds) {
        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            if (!this.ChunkMap[int0].ignore) {
                this.ChunkMap[int0].setGridSquare(newSquare, newSquare.getX(), newSquare.getY(), newSquare.getZ());
            }
        }

        return this.ConnectNewSquare(newSquare, bDoSurrounds, false);
    }

    public void PlaceLot(String filename, int sx, int sy, int sz, boolean bClearExisting) {
    }

    public void PlaceLot(IsoLot lot, int sx, int sy, int sz, boolean bClearExisting) {
        int int0 = Math.min(sz + lot.info.levels, sz + 8);

        for (int int1 = sx; int1 < sx + lot.info.width; int1++) {
            for (int int2 = sy; int2 < sy + lot.info.height; int2++) {
                for (int int3 = sz; int3 < int0; int3++) {
                    int int4 = int1 - sx;
                    int int5 = int2 - sy;
                    int int6 = int3 - sz;
                    if (int1 < this.width && int2 < this.height && int1 >= 0 && int2 >= 0 && int3 >= 0) {
                        int int7 = int4 + int5 * 10 + int6 * 100;
                        int int8 = lot.m_offsetInData[int7];
                        if (int8 != -1) {
                            int int9 = lot.m_data.getQuick(int8);
                            if (int9 > 0) {
                                boolean boolean0 = false;

                                for (int int10 = 0; int10 < int9; int10++) {
                                    String string = lot.info.tilesUsed.get(lot.m_data.getQuick(int8 + 1 + int10));
                                    IsoSprite sprite = IsoSpriteManager.instance.NamedMap.get(string);
                                    if (sprite == null) {
                                        Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, "Missing tile definition: " + string);
                                    } else {
                                        IsoGridSquare square = this.getGridSquare(int1, int2, int3);
                                        if (square == null) {
                                            if (IsoGridSquare.loadGridSquareCache != null) {
                                                square = IsoGridSquare.getNew(IsoGridSquare.loadGridSquareCache, this, null, int1, int2, int3);
                                            } else {
                                                square = IsoGridSquare.getNew(this, null, int1, int2, int3);
                                            }

                                            this.ChunkMap[IsoPlayer.getPlayerIndex()].setGridSquare(square, int1, int2, int3);
                                        } else {
                                            if (bClearExisting
                                                && int10 == 0
                                                && sprite.getProperties().Is(IsoFlagType.solidfloor)
                                                && (!sprite.Properties.Is(IsoFlagType.hidewalls) || int9 > 1)) {
                                                boolean0 = true;
                                            }

                                            if (boolean0 && int10 == 0) {
                                                square.getObjects().clear();
                                            }
                                        }

                                        CellLoader.DoTileObjectCreation(sprite, sprite.getType(), square, this, int1, int2, int3, string);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void PlaceLot(IsoLot lot, int sx, int sy, int sz, IsoChunk ch, int WX, int WY) {
        WX *= 10;
        WY *= 10;
        IsoMetaGrid metaGrid = IsoWorld.instance.getMetaGrid();
        int int0 = Math.min(sz + lot.info.levels, sz + 8);

        try {
            for (int int1 = WX + sx; int1 < WX + sx + 10; int1++) {
                for (int int2 = WY + sy; int2 < WY + sy + 10; int2++) {
                    for (int int3 = sz; int3 < int0; int3++) {
                        int int4 = int1 - WX - sx;
                        int int5 = int2 - WY - sy;
                        int int6 = int3 - sz;
                        if (int1 < WX + 10 && int2 < WY + 10 && int1 >= WX && int2 >= WY && int3 >= 0) {
                            int int7 = int4 + int5 * 10 + int6 * 100;
                            int int8 = lot.m_offsetInData[int7];
                            if (int8 != -1) {
                                int int9 = lot.m_data.getQuick(int8);
                                if (int9 > 0) {
                                    IsoGridSquare square0 = ch.getGridSquare(int1 - WX, int2 - WY, int3);
                                    if (square0 == null) {
                                        if (IsoGridSquare.loadGridSquareCache != null) {
                                            square0 = IsoGridSquare.getNew(IsoGridSquare.loadGridSquareCache, this, null, int1, int2, int3);
                                        } else {
                                            square0 = IsoGridSquare.getNew(this, null, int1, int2, int3);
                                        }

                                        square0.setX(int1);
                                        square0.setY(int2);
                                        square0.setZ(int3);
                                        ch.setSquare(int1 - WX, int2 - WY, int3, square0);
                                    }

                                    for (int int10 = -1; int10 <= 1; int10++) {
                                        for (int int11 = -1; int11 <= 1; int11++) {
                                            if ((int10 != 0 || int11 != 0)
                                                && int10 + int1 - WX >= 0
                                                && int10 + int1 - WX < 10
                                                && int11 + int2 - WY >= 0
                                                && int11 + int2 - WY < 10) {
                                                IsoGridSquare square1 = ch.getGridSquare(int1 + int10 - WX, int2 + int11 - WY, int3);
                                                if (square1 == null) {
                                                    square1 = IsoGridSquare.getNew(this, null, int1 + int10, int2 + int11, int3);
                                                    ch.setSquare(int1 + int10 - WX, int2 + int11 - WY, int3, square1);
                                                }
                                            }
                                        }
                                    }

                                    RoomDef roomDef = metaGrid.getRoomAt(int1, int2, int3);
                                    int int12 = roomDef != null ? roomDef.ID : -1;
                                    square0.setRoomID(int12);
                                    square0.ResetIsoWorldRegion();
                                    roomDef = metaGrid.getEmptyOutsideAt(int1, int2, int3);
                                    if (roomDef != null) {
                                        IsoRoom room = ch.getRoom(roomDef.ID);
                                        square0.roofHideBuilding = room == null ? null : room.building;
                                    }

                                    boolean boolean0 = true;

                                    for (int int13 = 0; int13 < int9; int13++) {
                                        String string = lot.info.tilesUsed.get(lot.m_data.get(int8 + 1 + int13));
                                        if (!lot.info.bFixed2x) {
                                            string = IsoChunk.Fix2x(string);
                                        }

                                        IsoSprite sprite = IsoSpriteManager.instance.NamedMap.get(string);
                                        if (sprite == null) {
                                            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, "Missing tile definition: " + string);
                                        } else {
                                            if (int13 == 0
                                                && sprite.getProperties().Is(IsoFlagType.solidfloor)
                                                && (!sprite.Properties.Is(IsoFlagType.hidewalls) || int9 > 1)) {
                                                boolean0 = true;
                                            }

                                            if (boolean0 && int13 == 0) {
                                                square0.getObjects().clear();
                                            }

                                            CellLoader.DoTileObjectCreation(sprite, sprite.getType(), square0, this, int1, int2, int3, string);
                                        }
                                    }

                                    square0.FixStackableObjects();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception exception) {
            DebugLog.log("Failed to load chunk, blocking out area");
            ExceptionLogger.logException(exception);

            for (int int14 = WX + sx; int14 < WX + sx + 10; int14++) {
                for (int int15 = WY + sy; int15 < WY + sy + 10; int15++) {
                    for (int int16 = sz; int16 < int0; int16++) {
                        ch.setSquare(int14 - WX - sx, int15 - WY - sy, int16 - sz, null);
                        this.setCacheGridSquare(int14, int15, int16, null);
                    }
                }
            }
        }
    }

    public void setDrag(KahluaTable item, int player) {
        if (player >= 0 && player < 4) {
            if (this.drag[player] != null && this.drag[player] != item) {
                Object object = this.drag[player].rawget("deactivate");
                if (object instanceof JavaFunction || object instanceof LuaClosure) {
                    LuaManager.caller.pcallvoid(LuaManager.thread, object, this.drag[player]);
                }
            }

            this.drag[player] = item;
        }
    }

    public KahluaTable getDrag(int player) {
        return player >= 0 && player < 4 ? this.drag[player] : null;
    }

    public boolean DoBuilding(int player, boolean bRender) {
        boolean boolean0;
        try {
            IsoCell.s_performance.isoCellDoBuilding.start();
            boolean0 = this.doBuildingInternal(player, bRender);
        } finally {
            IsoCell.s_performance.isoCellDoBuilding.end();
        }

        return boolean0;
    }

    private boolean doBuildingInternal(int int0, boolean boolean0) {
        if (UIManager.getPickedTile() != null && this.drag[int0] != null && JoypadManager.instance.getFromPlayer(int0) == null) {
            if (!IsoWorld.instance.isValidSquare((int)UIManager.getPickedTile().x, (int)UIManager.getPickedTile().y, (int)IsoCamera.CamCharacter.getZ())) {
                return false;
            }

            IsoGridSquare square = this.getGridSquare((int)UIManager.getPickedTile().x, (int)UIManager.getPickedTile().y, (int)IsoCamera.CamCharacter.getZ());
            if (!boolean0) {
                if (square == null) {
                    square = this.createNewGridSquare(
                        (int)UIManager.getPickedTile().x, (int)UIManager.getPickedTile().y, (int)IsoCamera.CamCharacter.getZ(), true
                    );
                    if (square == null) {
                        return false;
                    }
                }

                square.EnsureSurroundNotNull();
            }

            LuaEventManager.triggerEvent(
                "OnDoTileBuilding2",
                this.drag[int0],
                boolean0,
                (int)UIManager.getPickedTile().x,
                (int)UIManager.getPickedTile().y,
                (int)IsoCamera.CamCharacter.getZ(),
                square
            );
        }

        if (this.drag[int0] != null && JoypadManager.instance.getFromPlayer(int0) != null) {
            LuaEventManager.triggerEvent(
                "OnDoTileBuilding3",
                this.drag[int0],
                boolean0,
                (int)IsoPlayer.players[int0].getX(),
                (int)IsoPlayer.players[int0].getY(),
                (int)IsoCamera.CamCharacter.getZ()
            );
        }

        if (boolean0) {
            IndieGL.glBlendFunc(770, 771);
        }

        return false;
    }

    public float DistanceFromSupport(int x, int y, int z) {
        return 0.0F;
    }

    /**
     * @return the BuildingList
     * @deprecated
     */
    public ArrayList<IsoBuilding> getBuildingList() {
        return this.BuildingList;
    }

    public ArrayList<IsoWindow> getWindowList() {
        return this.WindowList;
    }

    public void addToWindowList(IsoWindow window) {
        if (!GameServer.bServer) {
            if (window != null) {
                if (!this.WindowList.contains(window)) {
                    this.WindowList.add(window);
                }
            }
        }
    }

    public void removeFromWindowList(IsoWindow window) {
        this.WindowList.remove(window);
    }

    /**
     * @return the ObjectList
     */
    public ArrayList<IsoMovingObject> getObjectList() {
        return this.ObjectList;
    }

    public IsoRoom getRoom(int ID) {
        return this.ChunkMap[IsoPlayer.getPlayerIndex()].getRoom(ID);
    }

    /**
     * @return the PushableObjectList
     */
    public ArrayList<IsoPushableObject> getPushableObjectList() {
        return this.PushableObjectList;
    }

    /**
     * @return the BuildingScores
     */
    public HashMap<Integer, BuildingScore> getBuildingScores() {
        return this.BuildingScores;
    }

    /**
     * @return the RoomList
     */
    public ArrayList<IsoRoom> getRoomList() {
        return this.RoomList;
    }

    /**
     * @return the StaticUpdaterObjectList
     */
    public ArrayList<IsoObject> getStaticUpdaterObjectList() {
        return this.StaticUpdaterObjectList;
    }

    /**
     * List of every zombie currently in the world.
     * @return List of every zombie currently in the world.
     */
    public ArrayList<IsoZombie> getZombieList() {
        return this.ZombieList;
    }

    /**
     * @return the RemoteSurvivorList
     * @deprecated
     */
    public ArrayList<IsoGameCharacter> getRemoteSurvivorList() {
        return this.RemoteSurvivorList;
    }

    /**
     * @return the removeList
     */
    public ArrayList<IsoMovingObject> getRemoveList() {
        return this.removeList;
    }

    /**
     * @return the addList
     */
    public ArrayList<IsoMovingObject> getAddList() {
        return this.addList;
    }

    public void addMovingObject(IsoMovingObject o) {
        this.addList.add(o);
    }

    /**
     * @return the ProcessItems
     */
    public ArrayList<InventoryItem> getProcessItems() {
        return this.ProcessItems;
    }

    public ArrayList<IsoWorldInventoryObject> getProcessWorldItems() {
        return this.ProcessWorldItems;
    }

    public ArrayList<IsoObject> getProcessIsoObjects() {
        return this.ProcessIsoObject;
    }

    /**
     * @return the ProcessItemsRemove
     */
    public ArrayList<InventoryItem> getProcessItemsRemove() {
        return this.ProcessItemsRemove;
    }

    public ArrayList<BaseVehicle> getVehicles() {
        return this.vehicles;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * 
     * @param _height the height to set
     */
    public void setHeight(int _height) {
        this.height = _height;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * 
     * @param _width the width to set
     */
    public void setWidth(int _width) {
        this.width = _width;
    }

    /**
     * @return the worldX
     */
    public int getWorldX() {
        return this.worldX;
    }

    /**
     * 
     * @param _worldX the worldX to set
     */
    public void setWorldX(int _worldX) {
        this.worldX = _worldX;
    }

    /**
     * @return the worldY
     */
    public int getWorldY() {
        return this.worldY;
    }

    /**
     * 
     * @param _worldY the worldY to set
     */
    public void setWorldY(int _worldY) {
        this.worldY = _worldY;
    }

    /**
     * @return the safeToAdd
     */
    public boolean isSafeToAdd() {
        return this.safeToAdd;
    }

    /**
     * 
     * @param _safeToAdd the safeToAdd to set
     */
    public void setSafeToAdd(boolean _safeToAdd) {
        this.safeToAdd = _safeToAdd;
    }

    /**
     * @return the LamppostPositions
     */
    public Stack<IsoLightSource> getLamppostPositions() {
        return this.LamppostPositions;
    }

    public IsoLightSource getLightSourceAt(int x, int y, int z) {
        for (int int0 = 0; int0 < this.LamppostPositions.size(); int0++) {
            IsoLightSource lightSource = this.LamppostPositions.get(int0);
            if (lightSource.getX() == x && lightSource.getY() == y && lightSource.getZ() == z) {
                return lightSource;
            }
        }

        return null;
    }

    public void addLamppost(IsoLightSource light) {
        if (light != null && !this.LamppostPositions.contains(light)) {
            this.LamppostPositions.add(light);
            IsoGridSquare.RecalcLightTime = -1;
            GameTime.instance.lightSourceUpdate = 100.0F;
        }
    }

    public IsoLightSource addLamppost(int x, int y, int z, float r, float g, float b, int rad) {
        IsoLightSource lightSource = new IsoLightSource(x, y, z, r, g, b, rad);
        this.LamppostPositions.add(lightSource);
        IsoGridSquare.RecalcLightTime = -1;
        GameTime.instance.lightSourceUpdate = 100.0F;
        return lightSource;
    }

    public void removeLamppost(int x, int y, int z) {
        for (int int0 = 0; int0 < this.LamppostPositions.size(); int0++) {
            IsoLightSource lightSource = this.LamppostPositions.get(int0);
            if (lightSource.getX() == x && lightSource.getY() == y && lightSource.getZ() == z) {
                lightSource.clearInfluence();
                this.LamppostPositions.remove(lightSource);
                IsoGridSquare.RecalcLightTime = -1;
                GameTime.instance.lightSourceUpdate = 100.0F;
                return;
            }
        }
    }

    public void removeLamppost(IsoLightSource light) {
        light.life = 0;
        IsoGridSquare.RecalcLightTime = -1;
        GameTime.instance.lightSourceUpdate = 100.0F;
    }

    /**
     * @return the currentLX
     */
    public int getCurrentLightX() {
        return this.currentLX;
    }

    /**
     * 
     * @param _currentLX the currentLX to set
     */
    public void setCurrentLightX(int _currentLX) {
        this.currentLX = _currentLX;
    }

    /**
     * @return the currentLY
     */
    public int getCurrentLightY() {
        return this.currentLY;
    }

    /**
     * 
     * @param _currentLY the currentLY to set
     */
    public void setCurrentLightY(int _currentLY) {
        this.currentLY = _currentLY;
    }

    /**
     * @return the currentLZ
     */
    public int getCurrentLightZ() {
        return this.currentLZ;
    }

    /**
     * 
     * @param _currentLZ the currentLZ to set
     */
    public void setCurrentLightZ(int _currentLZ) {
        this.currentLZ = _currentLZ;
    }

    /**
     * @return the minX
     */
    public int getMinX() {
        return this.minX;
    }

    /**
     * 
     * @param _minX the minX to set
     */
    public void setMinX(int _minX) {
        this.minX = _minX;
    }

    /**
     * @return the maxX
     */
    public int getMaxX() {
        return this.maxX;
    }

    /**
     * 
     * @param _maxX the maxX to set
     */
    public void setMaxX(int _maxX) {
        this.maxX = _maxX;
    }

    /**
     * @return the minY
     */
    public int getMinY() {
        return this.minY;
    }

    /**
     * 
     * @param _minY the minY to set
     */
    public void setMinY(int _minY) {
        this.minY = _minY;
    }

    /**
     * @return the maxY
     */
    public int getMaxY() {
        return this.maxY;
    }

    /**
     * 
     * @param _maxY the maxY to set
     */
    public void setMaxY(int _maxY) {
        this.maxY = _maxY;
    }

    /**
     * @return the minZ
     */
    public int getMinZ() {
        return this.minZ;
    }

    /**
     * 
     * @param _minZ the minZ to set
     */
    public void setMinZ(int _minZ) {
        this.minZ = _minZ;
    }

    /**
     * @return the maxZ
     */
    public int getMaxZ() {
        return this.maxZ;
    }

    /**
     * 
     * @param _maxZ the maxZ to set
     */
    public void setMaxZ(int _maxZ) {
        this.maxZ = _maxZ;
    }

    /**
     * @return the dangerUpdate
     */
    public OnceEvery getDangerUpdate() {
        return this.dangerUpdate;
    }

    /**
     * 
     * @param _dangerUpdate the dangerUpdate to set
     */
    public void setDangerUpdate(OnceEvery _dangerUpdate) {
        this.dangerUpdate = _dangerUpdate;
    }

    /**
     * @return the LightInfoUpdate
     */
    public Thread getLightInfoUpdate() {
        return this.LightInfoUpdate;
    }

    /**
     * 
     * @param _LightInfoUpdate the LightInfoUpdate to set
     */
    public void setLightInfoUpdate(Thread _LightInfoUpdate) {
        this.LightInfoUpdate = _LightInfoUpdate;
    }

    public ArrayList<IsoSurvivor> getSurvivorList() {
        return this.SurvivorList;
    }

    public static int getRComponent(int col) {
        return col & 0xFF;
    }

    public static int getGComponent(int col) {
        return (col & 0xFF00) >> 8;
    }

    public static int getBComponent(int col) {
        return (col & 0xFF0000) >> 16;
    }

    public static int toIntColor(float r, float g, float b, float a) {
        return (int)(r * 255.0F) << 0 | (int)(g * 255.0F) << 8 | (int)(b * 255.0F) << 16 | (int)(a * 255.0F) << 24;
    }

    public IsoGridSquare getRandomOutdoorTile() {
        IsoGridSquare square = null;

        do {
            square = this.getGridSquare(
                this.ChunkMap[IsoPlayer.getPlayerIndex()].getWorldXMin() * 10 + Rand.Next(this.width),
                this.ChunkMap[IsoPlayer.getPlayerIndex()].getWorldYMin() * 10 + Rand.Next(this.height),
                0
            );
            if (square != null) {
                square.setCachedIsFree(false);
            }
        } while (square == null || !square.isFree(false) || square.getRoom() != null);

        return square;
    }

    private static void InsertAt(int int1, BuildingScore buildingScore, BuildingScore[] buildingScores) {
        for (int int0 = buildingScores.length - 1; int0 > int1; int0--) {
            buildingScores[int0] = buildingScores[int0 - 1];
        }

        buildingScores[int1] = buildingScore;
    }

    static void Place(BuildingScore buildingScore, BuildingScore[] buildingScores, IsoCell.BuildingSearchCriteria buildingSearchCriteria) {
        for (int int0 = 0; int0 < buildingScores.length; int0++) {
            if (buildingScores[int0] != null) {
                boolean boolean0 = false;
                if (buildingScores[int0] == null) {
                    boolean0 = true;
                } else {
                    switch (buildingSearchCriteria) {
                        case General:
                            if (buildingScores[int0].food + buildingScores[int0].defense + buildingScores[int0].size + buildingScores[int0].weapons
                                < buildingScore.food + buildingScore.defense + buildingScore.size + buildingScore.weapons) {
                                boolean0 = true;
                            }
                            break;
                        case Food:
                            if (buildingScores[int0].food < buildingScore.food) {
                                boolean0 = true;
                            }
                            break;
                        case Wood:
                            if (buildingScores[int0].wood < buildingScore.wood) {
                                boolean0 = true;
                            }
                            break;
                        case Weapons:
                            if (buildingScores[int0].weapons < buildingScore.weapons) {
                                boolean0 = true;
                            }
                            break;
                        case Defense:
                            if (buildingScores[int0].defense < buildingScore.defense) {
                                boolean0 = true;
                            }
                    }
                }

                if (boolean0) {
                    InsertAt(int0, buildingScore, buildingScores);
                    return;
                }
            }
        }
    }

    public Stack<BuildingScore> getBestBuildings(IsoCell.BuildingSearchCriteria criteria, int count) {
        BuildingScore[] buildingScores = new BuildingScore[count];
        if (this.BuildingScores.isEmpty()) {
            int int0 = this.BuildingList.size();

            for (int int1 = 0; int1 < int0; int1++) {
                this.BuildingList.get(int1).update();
            }
        }

        int int2 = this.BuildingScores.size();

        for (int int3 = 0; int3 < int2; int3++) {
            BuildingScore buildingScore = this.BuildingScores.get(int3);
            Place(buildingScore, buildingScores, criteria);
        }

        buildingscores.clear();
        buildingscores.addAll(Arrays.asList(buildingScores));
        return buildingscores;
    }

    public boolean blocked(Mover mover, int x, int y, int z, int lx, int ly, int lz) {
        IsoGridSquare square = this.getGridSquare(lx, ly, lz);
        if (square == null) {
            return true;
        } else {
            if (mover instanceof IsoMovingObject) {
                if (square.testPathFindAdjacent((IsoMovingObject)mover, x - lx, y - ly, z - lz)) {
                    return true;
                }
            } else if (square.testPathFindAdjacent(null, x - lx, y - ly, z - lz)) {
                return true;
            }

            return false;
        }
    }

    public void Dispose() {
        for (int int0 = 0; int0 < this.ObjectList.size(); int0++) {
            IsoMovingObject movingObject = this.ObjectList.get(int0);
            if (movingObject instanceof IsoZombie) {
                movingObject.setCurrent(null);
                movingObject.setLast(null);
                VirtualZombieManager.instance.addToReusable((IsoZombie)movingObject);
            }
        }

        for (int int1 = 0; int1 < this.RoomList.size(); int1++) {
            this.RoomList.get(int1).TileList.clear();
            this.RoomList.get(int1).Exits.clear();
            this.RoomList.get(int1).WaterSources.clear();
            this.RoomList.get(int1).lightSwitches.clear();
            this.RoomList.get(int1).Beds.clear();
        }

        for (int int2 = 0; int2 < this.BuildingList.size(); int2++) {
            this.BuildingList.get(int2).Exits.clear();
            this.BuildingList.get(int2).Rooms.clear();
            this.BuildingList.get(int2).container.clear();
            this.BuildingList.get(int2).Windows.clear();
        }

        LuaEventManager.clear();
        LuaHookManager.clear();
        this.LamppostPositions.clear();
        this.ProcessItems.clear();
        this.ProcessItemsRemove.clear();
        this.ProcessWorldItems.clear();
        this.ProcessWorldItemsRemove.clear();
        this.BuildingScores.clear();
        this.BuildingList.clear();
        this.WindowList.clear();
        this.PushableObjectList.clear();
        this.RoomList.clear();
        this.SurvivorList.clear();
        this.ObjectList.clear();
        this.ZombieList.clear();

        for (int int3 = 0; int3 < this.ChunkMap.length; int3++) {
            this.ChunkMap[int3].Dispose();
            this.ChunkMap[int3] = null;
        }

        for (int int4 = 0; int4 < this.gridSquares.length; int4++) {
            if (this.gridSquares[int4] != null) {
                Arrays.fill(this.gridSquares[int4], null);
                this.gridSquares[int4] = null;
            }
        }
    }

    @LuaMethod(
        name = "getGridSquare"
    )
    public IsoGridSquare getGridSquare(double x, double y, double z) {
        return GameServer.bServer ? ServerMap.instance.getGridSquare((int)x, (int)y, (int)z) : this.getGridSquare((int)x, (int)y, (int)z);
    }

    @LuaMethod(
        name = "getOrCreateGridSquare"
    )
    public IsoGridSquare getOrCreateGridSquare(double x, double y, double z) {
        if (GameServer.bServer) {
            IsoGridSquare square0 = ServerMap.instance.getGridSquare((int)x, (int)y, (int)z);
            if (square0 == null) {
                square0 = IsoGridSquare.getNew(this, null, (int)x, (int)y, (int)z);
                ServerMap.instance.setGridSquare((int)x, (int)y, (int)z, square0);
                this.ConnectNewSquare(square0, true);
            }

            return square0;
        } else {
            IsoGridSquare square1 = this.getGridSquare((int)x, (int)y, (int)z);
            if (square1 == null) {
                square1 = IsoGridSquare.getNew(this, null, (int)x, (int)y, (int)z);
                this.ConnectNewSquare(square1, true);
            }

            return square1;
        }
    }

    public void setCacheGridSquare(int x, int y, int z, IsoGridSquare square) {
        assert square == null || x == square.getX() && y == square.getY() && z == square.getZ();

        if (!GameServer.bServer) {
            assert this.getChunkForGridSquare(x, y, z) != null;

            int int0 = IsoChunkMap.ChunkWidthInTiles;

            for (int int1 = 0; int1 < IsoPlayer.numPlayers; int1++) {
                if (!this.ChunkMap[int1].ignore) {
                    this.ChunkMap[int1].YMinTiles = -1;
                    this.ChunkMap[int1].XMinTiles = -1;
                    this.ChunkMap[int1].YMaxTiles = -1;
                    this.ChunkMap[int1].XMaxTiles = -1;
                    int int2 = x - this.ChunkMap[int1].getWorldXMinTiles();
                    int int3 = y - this.ChunkMap[int1].getWorldYMinTiles();
                    if (z < 8 && z >= 0 && int2 >= 0 && int2 < int0 && int3 >= 0 && int3 < int0) {
                        this.gridSquares[int1][int2 + int3 * int0 + z * int0 * int0] = square;
                    }
                }
            }
        }
    }

    public void setCacheChunk(IsoChunk chunk) {
        if (!GameServer.bServer) {
            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                this.setCacheChunk(chunk, int0);
            }
        }
    }

    public void setCacheChunk(IsoChunk chunk, int playerIndex) {
        if (!GameServer.bServer) {
            int int0 = IsoChunkMap.ChunkWidthInTiles;
            IsoChunkMap chunkMap = this.ChunkMap[playerIndex];
            if (!chunkMap.ignore) {
                int int1 = chunk.wx - chunkMap.getWorldXMin();
                int int2 = chunk.wy - chunkMap.getWorldYMin();
                if (int1 >= 0 && int1 < IsoChunkMap.ChunkGridWidth && int2 >= 0 && int2 < IsoChunkMap.ChunkGridWidth) {
                    IsoGridSquare[] squares = this.gridSquares[playerIndex];

                    for (int int3 = 0; int3 < 8; int3++) {
                        for (int int4 = 0; int4 < 10; int4++) {
                            for (int int5 = 0; int5 < 10; int5++) {
                                IsoGridSquare square = chunk.squares[int3][int5 + int4 * 10];
                                int int6 = int1 * 10 + int5;
                                int int7 = int2 * 10 + int4;
                                squares[int6 + int7 * int0 + int3 * int0 * int0] = square;
                            }
                        }
                    }
                }
            }
        }
    }

    public void clearCacheGridSquare(int playerIndex) {
        if (!GameServer.bServer) {
            int int0 = IsoChunkMap.ChunkWidthInTiles;
            this.gridSquares[playerIndex] = new IsoGridSquare[int0 * int0 * 8];
        }
    }

    public void setCacheGridSquareLocal(int x, int y, int z, IsoGridSquare square, int playerIndex) {
        if (!GameServer.bServer) {
            int int0 = IsoChunkMap.ChunkWidthInTiles;
            if (z < 8 && z >= 0 && x >= 0 && x < int0 && y >= 0 && y < int0) {
                this.gridSquares[playerIndex][x + y * int0 + z * int0 * int0] = square;
            }
        }
    }

    public IsoGridSquare getGridSquare(Double x, Double y, Double z) {
        return this.getGridSquare(x.intValue(), y.intValue(), z.intValue());
    }

    public IsoGridSquare getGridSquare(int x, int y, int z) {
        if (GameServer.bServer) {
            return ServerMap.instance.getGridSquare(x, y, z);
        } else {
            int int0 = IsoChunkMap.ChunkWidthInTiles;

            for (int int1 = 0; int1 < IsoPlayer.numPlayers; int1++) {
                if (!this.ChunkMap[int1].ignore) {
                    if (z == 0) {
                        boolean boolean0 = false;
                    }

                    int int2 = x - this.ChunkMap[int1].getWorldXMinTiles();
                    int int3 = y - this.ChunkMap[int1].getWorldYMinTiles();
                    if (z < 8 && z >= 0 && int2 >= 0 && int2 < int0 && int3 >= 0 && int3 < int0) {
                        IsoGridSquare square = this.gridSquares[int1][int2 + int3 * int0 + z * int0 * int0];
                        if (square != null) {
                            return square;
                        }
                    }
                }
            }

            return null;
        }
    }

    public void EnsureSurroundNotNull(int xx, int yy, int zz) {
        for (int int0 = -1; int0 <= 1; int0++) {
            for (int int1 = -1; int1 <= 1; int1++) {
                this.createNewGridSquare(xx + int0, yy + int1, zz, false);
            }
        }
    }

    public void DeleteAllMovingObjects() {
        this.ObjectList.clear();
    }

    @LuaMethod(
        name = "getMaxFloors"
    )
    public int getMaxFloors() {
        return 8;
    }

    public KahluaTable getLuaObjectList() {
        KahluaTable table = LuaManager.platform.newTable();
        LuaManager.env.rawset("Objects", table);

        for (int int0 = 0; int0 < this.ObjectList.size(); int0++) {
            table.rawset(int0 + 1, this.ObjectList.get(int0));
        }

        return table;
    }

    public int getHeightInTiles() {
        return this.ChunkMap[IsoPlayer.getPlayerIndex()].getWidthInTiles();
    }

    public int getWidthInTiles() {
        return this.ChunkMap[IsoPlayer.getPlayerIndex()].getWidthInTiles();
    }

    public boolean isNull(int x, int y, int z) {
        IsoGridSquare square = this.getGridSquare(x, y, z);
        return square == null || !square.isFree(false);
    }

    public void Remove(IsoMovingObject obj) {
        if (!(obj instanceof IsoPlayer) || ((IsoPlayer)obj).isDead()) {
            this.removeList.add(obj);
        }
    }

    boolean isBlocked(IsoGridSquare square1, IsoGridSquare square0) {
        return square1.room != square0.room;
    }

    private int CalculateColor(IsoGridSquare square1, IsoGridSquare square2, IsoGridSquare square3, IsoGridSquare square0, int int1, int int0) {
        float float0 = 0.0F;
        float float1 = 0.0F;
        float float2 = 0.0F;
        float float3 = 1.0F;
        if (square0 == null) {
            return 0;
        } else {
            float float4 = 0.0F;
            boolean boolean0 = true;
            if (square1 != null && square0.room == square1.room && square1.getChunk() != null) {
                float4++;
                ColorInfo colorInfo0 = square1.lighting[int0].lightInfo();
                float0 += colorInfo0.r;
                float1 += colorInfo0.g;
                float2 += colorInfo0.b;
            }

            if (square2 != null && square0.room == square2.room && square2.getChunk() != null) {
                float4++;
                ColorInfo colorInfo1 = square2.lighting[int0].lightInfo();
                float0 += colorInfo1.r;
                float1 += colorInfo1.g;
                float2 += colorInfo1.b;
            }

            if (square3 != null && square0.room == square3.room && square3.getChunk() != null) {
                float4++;
                ColorInfo colorInfo2 = square3.lighting[int0].lightInfo();
                float0 += colorInfo2.r;
                float1 += colorInfo2.g;
                float2 += colorInfo2.b;
            }

            if (square0 != null) {
                float4++;
                ColorInfo colorInfo3 = square0.lighting[int0].lightInfo();
                float0 += colorInfo3.r;
                float1 += colorInfo3.g;
                float2 += colorInfo3.b;
            }

            if (float4 != 0.0F) {
                float0 /= float4;
                float1 /= float4;
                float2 /= float4;
            }

            if (float0 > 1.0F) {
                float0 = 1.0F;
            }

            if (float1 > 1.0F) {
                float1 = 1.0F;
            }

            if (float2 > 1.0F) {
                float2 = 1.0F;
            }

            if (float0 < 0.0F) {
                float0 = 0.0F;
            }

            if (float1 < 0.0F) {
                float1 = 0.0F;
            }

            if (float2 < 0.0F) {
                float2 = 0.0F;
            }

            if (square0 != null) {
                square0.setVertLight(int1, (int)(float0 * 255.0F) << 0 | (int)(float1 * 255.0F) << 8 | (int)(float2 * 255.0F) << 16 | 0xFF000000, int0);
                square0.setVertLight(int1 + 4, (int)(float0 * 255.0F) << 0 | (int)(float1 * 255.0F) << 8 | (int)(float2 * 255.0F) << 16 | 0xFF000000, int0);
            }

            return int1;
        }
    }

    public static IsoCell getInstance() {
        return instance;
    }

    public void render() {
        IsoCell.s_performance.isoCellRender.invokeAndMeasure(this, IsoCell::renderInternal);
    }

    private void renderInternal() {
        int int0 = IsoCamera.frameState.playerIndex;
        IsoPlayer player0 = IsoPlayer.players[int0];
        if (player0.dirtyRecalcGridStackTime > 0.0F) {
            player0.dirtyRecalcGridStack = true;
        } else {
            player0.dirtyRecalcGridStack = false;
        }

        if (!PerformanceSettings.NewRoofHiding) {
            if (this.bHideFloors[int0] && this.unhideFloorsCounter[int0] > 0) {
                this.unhideFloorsCounter[int0]--;
            }

            if (this.unhideFloorsCounter[int0] <= 0) {
                this.bHideFloors[int0] = false;
                this.unhideFloorsCounter[int0] = 60;
            }
        }

        int int1 = 8;
        if (int1 < 8) {
            int1++;
        }

        this.recalcShading--;
        byte byte0 = 0;
        byte byte1 = 0;
        int int2 = byte0 + IsoCamera.getOffscreenWidth(int0);
        int int3 = byte1 + IsoCamera.getOffscreenHeight(int0);
        float float0 = IsoUtils.XToIso(byte0, byte1, 0.0F);
        float float1 = IsoUtils.YToIso(int2, byte1, 0.0F);
        float float2 = IsoUtils.XToIso(int2, int3, 6.0F);
        float float3 = IsoUtils.YToIso(byte0, int3, 6.0F);
        this.minY = (int)float1;
        this.maxY = (int)float3;
        this.minX = (int)float0;
        this.maxX = (int)float2;
        this.minX -= 2;
        this.minY -= 2;
        this.maxZ = MaxHeight;
        if (IsoCamera.CamCharacter == null) {
            this.maxZ = 1;
        }

        byte byte2 = 0;
        byte2 = 4;
        if (GameTime.instance.FPSMultiplier > 1.5F) {
            byte2 = 6;
        }

        if (this.minX != this.lastMinX || this.minY != this.lastMinY) {
            this.lightUpdateCount = 10;
        }

        if (!PerformanceSettings.NewRoofHiding) {
            IsoGridSquare square0 = IsoCamera.CamCharacter == null ? null : IsoCamera.CamCharacter.getCurrentSquare();
            if (square0 != null) {
                IsoGridSquare square1 = this.getGridSquare(
                    (double)Math.round(IsoCamera.CamCharacter.getX()), (double)Math.round(IsoCamera.CamCharacter.getY()), (double)IsoCamera.CamCharacter.getZ()
                );
                if (square1 != null && this.IsBehindStuff(square1)) {
                    this.bHideFloors[int0] = true;
                }

                if (!this.bHideFloors[int0] && square0.getProperties().Is(IsoFlagType.hidewalls) || !square0.getProperties().Is(IsoFlagType.exterior)) {
                    this.bHideFloors[int0] = true;
                }
            }

            if (this.bHideFloors[int0]) {
                this.maxZ = (int)IsoCamera.CamCharacter.getZ() + 1;
            }
        }

        if (PerformanceSettings.LightingFrameSkip < 3) {
            this.DrawStencilMask();
        }

        if (PerformanceSettings.LightingFrameSkip == 3) {
            int int4 = IsoCamera.getOffscreenWidth(int0) / 2;
            int int5 = IsoCamera.getOffscreenHeight(int0) / 2;
            short short0 = 409;
            int4 -= short0 / (2 / Core.TileScale);
            int5 -= short0 / (2 / Core.TileScale);
            this.StencilX1 = int4 - (int)IsoCamera.cameras[int0].RightClickX;
            this.StencilY1 = int5 - (int)IsoCamera.cameras[int0].RightClickY;
            this.StencilX2 = this.StencilX1 + short0 * Core.TileScale;
            this.StencilY2 = this.StencilY1 + short0 * Core.TileScale;
        }

        if (PerformanceSettings.NewRoofHiding && player0.dirtyRecalcGridStack) {
            this.hidesOrphanStructuresAbove = int1;
            IsoGridSquare square2 = null;
            this.otherOccluderBuildings.get(int0).clear();
            if (this.otherOccluderBuildingsArr[int0] != null) {
                this.otherOccluderBuildingsArr[int0][0] = null;
            } else {
                this.otherOccluderBuildingsArr[int0] = new IsoBuilding[500];
            }

            if (IsoCamera.CamCharacter != null && IsoCamera.CamCharacter.getCurrentSquare() != null) {
                IsoGridSquare square3 = IsoCamera.CamCharacter.getCurrentSquare();
                int int6 = 10;
                if (this.ZombieList.size() < 10) {
                    int6 = this.ZombieList.size();
                }

                if (this.nearestVisibleZombie[int0] != null) {
                    if (this.nearestVisibleZombie[int0].isDead()) {
                        this.nearestVisibleZombie[int0] = null;
                    } else {
                        float float4 = this.nearestVisibleZombie[int0].x - IsoCamera.CamCharacter.x;
                        float float5 = this.nearestVisibleZombie[int0].y - IsoCamera.CamCharacter.y;
                        this.nearestVisibleZombieDistSqr[int0] = float4 * float4 + float5 * float5;
                    }
                }

                for (int int7 = 0; int7 < int6; this.zombieScanCursor++) {
                    if (this.zombieScanCursor >= this.ZombieList.size()) {
                        this.zombieScanCursor = 0;
                    }

                    IsoZombie zombie0 = this.ZombieList.get(this.zombieScanCursor);
                    if (zombie0 != null) {
                        IsoGridSquare square4 = zombie0.getCurrentSquare();
                        if (square4 != null && square3.z == square4.z && square4.getCanSee(int0)) {
                            if (this.nearestVisibleZombie[int0] == null) {
                                this.nearestVisibleZombie[int0] = zombie0;
                                float float6 = this.nearestVisibleZombie[int0].x - IsoCamera.CamCharacter.x;
                                float float7 = this.nearestVisibleZombie[int0].y - IsoCamera.CamCharacter.y;
                                this.nearestVisibleZombieDistSqr[int0] = float6 * float6 + float7 * float7;
                            } else {
                                float float8 = zombie0.x - IsoCamera.CamCharacter.x;
                                float float9 = zombie0.y - IsoCamera.CamCharacter.y;
                                float float10 = float8 * float8 + float9 * float9;
                                if (float10 < this.nearestVisibleZombieDistSqr[int0]) {
                                    this.nearestVisibleZombie[int0] = zombie0;
                                    this.nearestVisibleZombieDistSqr[int0] = float10;
                                }
                            }
                        }
                    }

                    int7++;
                }

                for (int int8 = 0; int8 < 4; int8++) {
                    IsoPlayer player1 = IsoPlayer.players[int8];
                    if (player1 != null && player1.getCurrentSquare() != null) {
                        IsoGridSquare square5 = player1.getCurrentSquare();
                        if (int8 == int0) {
                            square2 = square5;
                        }

                        double double0 = player1.x - Math.floor(player1.x);
                        double double1 = player1.y - Math.floor(player1.y);
                        boolean boolean0 = double0 > double1;
                        if (this.lastPlayerAngle[int8] == null) {
                            this.lastPlayerAngle[int8] = new Vector2(player1.getForwardDirection());
                            this.playerCutawaysDirty[int8] = true;
                        } else if (player1.getForwardDirection().dot(this.lastPlayerAngle[int8]) < 0.98F) {
                            this.lastPlayerAngle[int8].set(player1.getForwardDirection());
                            this.playerCutawaysDirty[int8] = true;
                        }

                        IsoDirections directions0 = IsoDirections.fromAngle(player1.getForwardDirection());
                        if (this.lastPlayerSquare[int8] != square5 || this.lastPlayerSquareHalf[int8] != boolean0 || this.lastPlayerDir[int8] != directions0) {
                            this.playerCutawaysDirty[int8] = true;
                            this.lastPlayerSquare[int8] = square5;
                            this.lastPlayerSquareHalf[int8] = boolean0;
                            this.lastPlayerDir[int8] = directions0;
                            IsoBuilding building0 = square5.getBuilding();
                            this.playerWindowPeekingRoomId[int8] = -1;
                            this.GetBuildingsInFrontOfCharacter(this.playerOccluderBuildings.get(int8), square5, boolean0);
                            if (this.playerOccluderBuildingsArr[int0] == null) {
                                this.playerOccluderBuildingsArr[int0] = new IsoBuilding[500];
                            }

                            this.playerHidesOrphanStructures[int8] = this.bOccludedByOrphanStructureFlag;
                            if (building0 == null && !player1.bRemote) {
                                building0 = this.GetPeekedInBuilding(square5, directions0);
                                if (building0 != null) {
                                    this.playerWindowPeekingRoomId[int8] = this.playerPeekedRoomId;
                                }
                            }

                            if (building0 != null) {
                                this.AddUniqueToBuildingList(this.playerOccluderBuildings.get(int8), building0);
                            }

                            ArrayList arrayList0 = this.playerOccluderBuildings.get(int8);

                            for (int int9 = 0; int9 < arrayList0.size(); int9++) {
                                IsoBuilding building1 = (IsoBuilding)arrayList0.get(int9);
                                this.playerOccluderBuildingsArr[int0][int9] = building1;
                            }

                            this.playerOccluderBuildingsArr[int0][arrayList0.size()] = null;
                        }

                        if (int8 == int0 && square2 != null) {
                            this.gridSquaresTempLeft.clear();
                            this.gridSquaresTempRight.clear();
                            this.GetSquaresAroundPlayerSquare(player1, square2, this.gridSquaresTempLeft, this.gridSquaresTempRight);

                            for (int int10 = 0; int10 < this.gridSquaresTempLeft.size(); int10++) {
                                IsoGridSquare square6 = this.gridSquaresTempLeft.get(int10);
                                if (square6.getCanSee(int0) && (square6.getBuilding() == null || square6.getBuilding() == square2.getBuilding())) {
                                    ArrayList arrayList1 = this.GetBuildingsInFrontOfMustSeeSquare(square6, IsoGridOcclusionData.OcclusionFilter.Right);

                                    for (int int11 = 0; int11 < arrayList1.size(); int11++) {
                                        this.AddUniqueToBuildingList(this.otherOccluderBuildings.get(int0), (IsoBuilding)arrayList1.get(int11));
                                    }

                                    this.playerHidesOrphanStructures[int0] = this.playerHidesOrphanStructures[int0] | this.bOccludedByOrphanStructureFlag;
                                }
                            }

                            for (int int12 = 0; int12 < this.gridSquaresTempRight.size(); int12++) {
                                IsoGridSquare square7 = this.gridSquaresTempRight.get(int12);
                                if (square7.getCanSee(int0) && (square7.getBuilding() == null || square7.getBuilding() == square2.getBuilding())) {
                                    ArrayList arrayList2 = this.GetBuildingsInFrontOfMustSeeSquare(square7, IsoGridOcclusionData.OcclusionFilter.Left);

                                    for (int int13 = 0; int13 < arrayList2.size(); int13++) {
                                        this.AddUniqueToBuildingList(this.otherOccluderBuildings.get(int0), (IsoBuilding)arrayList2.get(int13));
                                    }

                                    this.playerHidesOrphanStructures[int0] = this.playerHidesOrphanStructures[int0] | this.bOccludedByOrphanStructureFlag;
                                }
                            }

                            ArrayList arrayList3 = this.otherOccluderBuildings.get(int0);
                            if (this.otherOccluderBuildingsArr[int0] == null) {
                                this.otherOccluderBuildingsArr[int0] = new IsoBuilding[500];
                            }

                            for (int int14 = 0; int14 < arrayList3.size(); int14++) {
                                IsoBuilding building2 = (IsoBuilding)arrayList3.get(int14);
                                this.otherOccluderBuildingsArr[int0][int14] = building2;
                            }

                            this.otherOccluderBuildingsArr[int0][arrayList3.size()] = null;
                        }

                        if (this.playerHidesOrphanStructures[int8] && this.hidesOrphanStructuresAbove > square5.getZ()) {
                            this.hidesOrphanStructuresAbove = square5.getZ();
                        }
                    }
                }

                if (square2 != null && this.hidesOrphanStructuresAbove < square2.getZ()) {
                    this.hidesOrphanStructuresAbove = square2.getZ();
                }

                boolean boolean1 = false;
                if (this.nearestVisibleZombie[int0] != null && this.nearestVisibleZombieDistSqr[int0] < 150.0F) {
                    IsoGridSquare square8 = this.nearestVisibleZombie[int0].getCurrentSquare();
                    if (square8 != null && square8.getCanSee(int0)) {
                        double double2 = this.nearestVisibleZombie[int0].x - Math.floor(this.nearestVisibleZombie[int0].x);
                        double double3 = this.nearestVisibleZombie[int0].y - Math.floor(this.nearestVisibleZombie[int0].y);
                        boolean boolean2 = double2 > double3;
                        boolean1 = true;
                        if (this.lastZombieSquare[int0] != square8 || this.lastZombieSquareHalf[int0] != boolean2) {
                            this.lastZombieSquare[int0] = square8;
                            this.lastZombieSquareHalf[int0] = boolean2;
                            this.GetBuildingsInFrontOfCharacter(this.zombieOccluderBuildings.get(int0), square8, boolean2);
                            ArrayList arrayList4 = this.zombieOccluderBuildings.get(int0);
                            if (this.zombieOccluderBuildingsArr[int0] == null) {
                                this.zombieOccluderBuildingsArr[int0] = new IsoBuilding[500];
                            }

                            for (int int15 = 0; int15 < arrayList4.size(); int15++) {
                                IsoBuilding building3 = (IsoBuilding)arrayList4.get(int15);
                                this.zombieOccluderBuildingsArr[int0][int15] = building3;
                            }

                            this.zombieOccluderBuildingsArr[int0][arrayList4.size()] = null;
                        }
                    }
                }

                if (!boolean1) {
                    this.zombieOccluderBuildings.get(int0).clear();
                    if (this.zombieOccluderBuildingsArr[int0] != null) {
                        this.zombieOccluderBuildingsArr[int0][0] = null;
                    } else {
                        this.zombieOccluderBuildingsArr[int0] = new IsoBuilding[500];
                    }
                }
            } else {
                for (int int16 = 0; int16 < 4; int16++) {
                    this.playerOccluderBuildings.get(int16).clear();
                    if (this.playerOccluderBuildingsArr[int16] != null) {
                        this.playerOccluderBuildingsArr[int16][0] = null;
                    } else {
                        this.playerOccluderBuildingsArr[int16] = new IsoBuilding[500];
                    }

                    this.lastPlayerSquare[int16] = null;
                    this.playerCutawaysDirty[int16] = true;
                }

                this.playerWindowPeekingRoomId[int0] = -1;
                this.zombieOccluderBuildings.get(int0).clear();
                if (this.zombieOccluderBuildingsArr[int0] != null) {
                    this.zombieOccluderBuildingsArr[int0][0] = null;
                } else {
                    this.zombieOccluderBuildingsArr[int0] = new IsoBuilding[500];
                }

                this.lastZombieSquare[int0] = null;
            }
        }

        if (!PerformanceSettings.NewRoofHiding) {
            for (int int17 = 0; int17 < IsoPlayer.numPlayers; int17++) {
                this.playerWindowPeekingRoomId[int17] = -1;
                IsoPlayer player2 = IsoPlayer.players[int17];
                if (player2 != null) {
                    IsoBuilding building4 = player2.getCurrentBuilding();
                    if (building4 == null) {
                        IsoDirections directions1 = IsoDirections.fromAngle(player2.getForwardDirection());
                        building4 = this.GetPeekedInBuilding(player2.getCurrentSquare(), directions1);
                        if (building4 != null) {
                            this.playerWindowPeekingRoomId[int17] = this.playerPeekedRoomId;
                        }
                    }
                }
            }
        }

        if (IsoCamera.CamCharacter != null
            && IsoCamera.CamCharacter.getCurrentSquare() != null
            && IsoCamera.CamCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.hidewalls)) {
            this.maxZ = (int)IsoCamera.CamCharacter.getZ() + 1;
        }

        this.bRendering = true;

        try {
            this.RenderTiles(int1);
        } catch (Exception exception) {
            this.bRendering = false;
            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, exception);
        }

        this.bRendering = false;
        if (IsoGridSquare.getRecalcLightTime() < 0) {
            IsoGridSquare.setRecalcLightTime(60);
        }

        if (IsoGridSquare.getLightcache() <= 0) {
            IsoGridSquare.setLightcache(90);
        }

        for (int int18 = 0; int18 < this.ObjectList.size(); int18++) {
            IsoMovingObject movingObject = this.ObjectList.get(int18);
            movingObject.renderlast();
        }

        for (int int19 = 0; int19 < this.StaticUpdaterObjectList.size(); int19++) {
            IsoObject object = this.StaticUpdaterObjectList.get(int19);
            object.renderlast();
        }

        IsoTree.renderChopTreeIndicators();
        if (Core.bDebug) {
        }

        this.lastMinX = this.minX;
        this.lastMinY = this.minY;
        this.DoBuilding(IsoPlayer.getPlayerIndex(), true);
        this.renderRain();
    }

    public void invalidatePeekedRoom(int playerIndex) {
        this.lastPlayerDir[playerIndex] = IsoDirections.Max;
    }

    private boolean initWeatherFx() {
        if (GameServer.bServer) {
            return false;
        } else {
            if (this.weatherFX == null) {
                this.weatherFX = new IsoWeatherFX();
                this.weatherFX.init();
            }

            return true;
        }
    }

    private void updateWeatherFx() {
        if (this.initWeatherFx()) {
            this.weatherFX.update();
        }
    }

    private void renderWeatherFx() {
        if (this.initWeatherFx()) {
            this.weatherFX.render();
        }
    }

    public IsoWeatherFX getWeatherFX() {
        return this.weatherFX;
    }

    private void renderRain() {
    }

    public void setRainAlpha(int alpha) {
        this.rainAlphaMax = alpha / 100.0F;
    }

    public void setRainIntensity(int intensity) {
        this.rainIntensity = intensity;
    }

    public void setRainSpeed(int speed) {
        this.rainSpeed = speed;
    }

    public void reloadRainTextures() {
    }

    private void GetBuildingsInFrontOfCharacter(ArrayList<IsoBuilding> arrayList, IsoGridSquare square, boolean boolean0) {
        arrayList.clear();
        this.bOccludedByOrphanStructureFlag = false;
        if (square != null) {
            int int0 = square.getX();
            int int1 = square.getY();
            int int2 = square.getZ();
            this.GetBuildingsInFrontOfCharacterSquare(int0, int1, int2, boolean0, arrayList);
            if (int2 < MaxHeight) {
                this.GetBuildingsInFrontOfCharacterSquare(int0 - 1 + 3, int1 - 1 + 3, int2 + 1, boolean0, arrayList);
                this.GetBuildingsInFrontOfCharacterSquare(int0 - 2 + 3, int1 - 2 + 3, int2 + 1, boolean0, arrayList);
                if (boolean0) {
                    this.GetBuildingsInFrontOfCharacterSquare(int0 + 3, int1 - 1 + 3, int2 + 1, !boolean0, arrayList);
                    this.GetBuildingsInFrontOfCharacterSquare(int0 - 1 + 3, int1 - 2 + 3, int2 + 1, !boolean0, arrayList);
                } else {
                    this.GetBuildingsInFrontOfCharacterSquare(int0 - 1 + 3, int1 + 3, int2 + 1, !boolean0, arrayList);
                    this.GetBuildingsInFrontOfCharacterSquare(int0 - 2 + 3, int1 - 1 + 3, int2 + 1, !boolean0, arrayList);
                }
            }
        }
    }

    private void GetBuildingsInFrontOfCharacterSquare(int int0, int int1, int int2, boolean boolean0, ArrayList<IsoBuilding> arrayList0) {
        IsoGridSquare square = this.getGridSquare(int0, int1, int2);
        if (square == null) {
            if (int2 < MaxHeight) {
                this.GetBuildingsInFrontOfCharacterSquare(int0 + 3, int1 + 3, int2 + 1, boolean0, arrayList0);
            }
        } else {
            IsoGridOcclusionData gridOcclusionData = square.getOrCreateOcclusionData();
            IsoGridOcclusionData.OcclusionFilter occlusionFilter = boolean0
                ? IsoGridOcclusionData.OcclusionFilter.Right
                : IsoGridOcclusionData.OcclusionFilter.Left;
            this.bOccludedByOrphanStructureFlag = this.bOccludedByOrphanStructureFlag | gridOcclusionData.getCouldBeOccludedByOrphanStructures(occlusionFilter);
            ArrayList arrayList1 = gridOcclusionData.getBuildingsCouldBeOccluders(occlusionFilter);

            for (int int3 = 0; int3 < arrayList1.size(); int3++) {
                this.AddUniqueToBuildingList(arrayList0, (IsoBuilding)arrayList1.get(int3));
            }
        }
    }

    private ArrayList<IsoBuilding> GetBuildingsInFrontOfMustSeeSquare(IsoGridSquare square, IsoGridOcclusionData.OcclusionFilter occlusionFilter) {
        IsoGridOcclusionData gridOcclusionData = square.getOrCreateOcclusionData();
        this.bOccludedByOrphanStructureFlag = gridOcclusionData.getCouldBeOccludedByOrphanStructures(IsoGridOcclusionData.OcclusionFilter.All);
        return gridOcclusionData.getBuildingsCouldBeOccluders(occlusionFilter);
    }

    private IsoBuilding GetPeekedInBuilding(IsoGridSquare square0, IsoDirections directions) {
        this.playerPeekedRoomId = -1;
        if (square0 == null) {
            return null;
        } else {
            if ((directions == IsoDirections.NW || directions == IsoDirections.N || directions == IsoDirections.NE)
                && LosUtil.lineClear(this, square0.x, square0.y, square0.z, square0.x, square0.y - 1, square0.z, false) != LosUtil.TestResults.Blocked) {
                IsoGridSquare square1 = square0.nav[IsoDirections.N.index()];
                if (square1 != null) {
                    IsoBuilding building0 = square1.getBuilding();
                    if (building0 != null) {
                        this.playerPeekedRoomId = square1.getRoomID();
                        return building0;
                    }
                }
            }

            if ((directions == IsoDirections.SW || directions == IsoDirections.W || directions == IsoDirections.NW)
                && LosUtil.lineClear(this, square0.x, square0.y, square0.z, square0.x - 1, square0.y, square0.z, false) != LosUtil.TestResults.Blocked) {
                IsoGridSquare square2 = square0.nav[IsoDirections.W.index()];
                if (square2 != null) {
                    IsoBuilding building1 = square2.getBuilding();
                    if (building1 != null) {
                        this.playerPeekedRoomId = square2.getRoomID();
                        return building1;
                    }
                }
            }

            if ((directions == IsoDirections.SE || directions == IsoDirections.S || directions == IsoDirections.SW)
                && LosUtil.lineClear(this, square0.x, square0.y, square0.z, square0.x, square0.y + 1, square0.z, false) != LosUtil.TestResults.Blocked) {
                IsoGridSquare square3 = square0.nav[IsoDirections.S.index()];
                if (square3 != null) {
                    IsoBuilding building2 = square3.getBuilding();
                    if (building2 != null) {
                        this.playerPeekedRoomId = square3.getRoomID();
                        return building2;
                    }
                }
            }

            if ((directions == IsoDirections.NE || directions == IsoDirections.E || directions == IsoDirections.SE)
                && LosUtil.lineClear(this, square0.x, square0.y, square0.z, square0.x + 1, square0.y, square0.z, false) != LosUtil.TestResults.Blocked) {
                IsoGridSquare square4 = square0.nav[IsoDirections.E.index()];
                if (square4 != null) {
                    IsoBuilding building3 = square4.getBuilding();
                    if (building3 != null) {
                        this.playerPeekedRoomId = square4.getRoomID();
                        return building3;
                    }
                }
            }

            return null;
        }
    }

    void GetSquaresAroundPlayerSquare(IsoPlayer player, IsoGridSquare square0, ArrayList<IsoGridSquare> arrayList0, ArrayList<IsoGridSquare> arrayList1) {
        float float0 = player.x - 4.0F;
        float float1 = player.y - 4.0F;
        int int0 = (int)float0;
        int int1 = (int)float1;
        int int2 = square0.getZ();

        for (int int3 = int1; int3 < int1 + 10; int3++) {
            for (int int4 = int0; int4 < int0 + 10; int4++) {
                if ((int4 >= (int)player.x || int3 >= (int)player.y) && (int4 != (int)player.x || int3 != (int)player.y)) {
                    float float2 = int4 - player.x;
                    float float3 = int3 - player.y;
                    if (float3 < float2 + 4.5 && float3 > float2 - 4.5) {
                        IsoGridSquare square1 = this.getGridSquare(int4, int3, int2);
                        if (square1 != null) {
                            if (float3 >= float2) {
                                arrayList0.add(square1);
                            }

                            if (float3 <= float2) {
                                arrayList1.add(square1);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean IsBehindStuff(IsoGridSquare square0) {
        if (!square0.getProperties().Is(IsoFlagType.exterior)) {
            return true;
        } else {
            for (int int0 = 1; int0 < 8 && square0.getZ() + int0 < MaxHeight; int0++) {
                for (int int1 = -5; int1 <= 6; int1++) {
                    for (int int2 = -5; int2 <= 6; int2++) {
                        if (int2 >= int1 - 5 && int2 <= int1 + 5) {
                            IsoGridSquare square1 = this.getGridSquare(
                                square0.getX() + int2 + int0 * 3, square0.getY() + int1 + int0 * 3, square0.getZ() + int0
                            );
                            if (square1 != null && !square1.getObjects().isEmpty()) {
                                if (int0 != 1 || square1.getObjects().size() != 1) {
                                    return true;
                                }

                                IsoObject object = square1.getObjects().get(0);
                                if (object.sprite == null || object.sprite.name == null || !object.sprite.name.startsWith("lighting_outdoor")) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }

            return false;
        }
    }

    public static IsoDirections FromMouseTile() {
        IsoDirections directions = IsoDirections.N;
        float float0 = UIManager.getPickedTileLocal().x;
        float float1 = UIManager.getPickedTileLocal().y;
        float float2 = 0.5F - Math.abs(0.5F - float1);
        float float3 = 0.5F - Math.abs(0.5F - float0);
        if (float0 > 0.5F && float3 < float2) {
            directions = IsoDirections.E;
        } else if (float1 > 0.5F && float3 > float2) {
            directions = IsoDirections.S;
        } else if (float0 < 0.5F && float3 < float2) {
            directions = IsoDirections.W;
        } else if (float1 < 0.5F && float3 > float2) {
            directions = IsoDirections.N;
        }

        return directions;
    }

    public void update() {
        IsoCell.s_performance.isoCellUpdate.invokeAndMeasure(this, IsoCell::updateInternal);
    }

    private void updateInternal() {
        MovingObjectUpdateScheduler.instance.startFrame();
        IsoSprite.alphaStep = 0.075F * (GameTime.getInstance().getMultiplier() / 1.6F);
        IsoGridSquare.gridSquareCacheEmptyTimer++;
        this.ProcessSpottedRooms();
        if (!GameServer.bServer) {
            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                if (IsoPlayer.players[int0] != null && (!IsoPlayer.players[int0].isDead() || IsoPlayer.players[int0].ReanimatedCorpse != null)) {
                    IsoPlayer.setInstance(IsoPlayer.players[int0]);
                    IsoCamera.CamCharacter = IsoPlayer.players[int0];
                    this.ChunkMap[int0].update();
                }
            }
        }

        this.ProcessRemoveItems(null);
        this.ProcessItems(null);
        this.ProcessRemoveItems(null);
        this.ProcessIsoObject();
        this.safeToAdd = false;
        this.ProcessObjects(null);
        if (GameClient.bClient
            && (
                NetworkZombieSimulator.getInstance().anyUnknownZombies() && GameClient.instance.sendZombieRequestsTimer.Check()
                    || GameClient.instance.sendZombieTimer.Check()
            )) {
            NetworkZombieSimulator.getInstance().send();
            GameClient.instance.sendZombieTimer.Reset();
            GameClient.instance.sendZombieRequestsTimer.Reset();
        }

        this.safeToAdd = true;
        this.ProcessStaticUpdaters();
        this.ObjectDeletionAddition();
        IsoDeadBody.updateBodies();
        IsoGridSquare.setLightcache(IsoGridSquare.getLightcache() - 1);
        IsoGridSquare.setRecalcLightTime(IsoGridSquare.getRecalcLightTime() - 1);
        if (GameServer.bServer) {
            this.LamppostPositions.clear();
            this.roomLights.clear();
        }

        if (!GameTime.isGamePaused()) {
            this.rainScroll = this.rainScroll + this.rainSpeed / 10.0F * 0.075F * (30.0F / PerformanceSettings.getLockFPS());
            if (this.rainScroll > 1.0F) {
                this.rainScroll = 0.0F;
            }
        }

        if (!GameServer.bServer) {
            this.updateWeatherFx();
        }
    }

    IsoGridSquare getRandomFreeTile() {
        IsoGridSquare square = null;
        boolean boolean0 = true;

        do {
            boolean0 = true;
            square = this.getGridSquare(Rand.Next(this.width), Rand.Next(this.height), 0);
            if (square == null) {
                boolean0 = false;
            } else if (!square.isFree(false)) {
                boolean0 = false;
            } else if (square.getProperties().Is(IsoFlagType.solid) || square.getProperties().Is(IsoFlagType.solidtrans)) {
                boolean0 = false;
            } else if (square.getMovingObjects().size() > 0) {
                boolean0 = false;
            } else if (square.Has(IsoObjectType.stairsBN) || square.Has(IsoObjectType.stairsMN) || square.Has(IsoObjectType.stairsTN)) {
                boolean0 = false;
            } else if (square.Has(IsoObjectType.stairsBW) || square.Has(IsoObjectType.stairsMW) || square.Has(IsoObjectType.stairsTW)) {
                boolean0 = false;
            }
        } while (!boolean0);

        return square;
    }

    IsoGridSquare getRandomOutdoorFreeTile() {
        IsoGridSquare square = null;
        boolean boolean0 = true;

        do {
            boolean0 = true;
            square = this.getGridSquare(Rand.Next(this.width), Rand.Next(this.height), 0);
            if (square == null) {
                boolean0 = false;
            } else if (!square.isFree(false)) {
                boolean0 = false;
            } else if (square.getRoom() != null) {
                boolean0 = false;
            } else if (square.getProperties().Is(IsoFlagType.solid) || square.getProperties().Is(IsoFlagType.solidtrans)) {
                boolean0 = false;
            } else if (square.getMovingObjects().size() > 0) {
                boolean0 = false;
            } else if (square.Has(IsoObjectType.stairsBN) || square.Has(IsoObjectType.stairsMN) || square.Has(IsoObjectType.stairsTN)) {
                boolean0 = false;
            } else if (square.Has(IsoObjectType.stairsBW) || square.Has(IsoObjectType.stairsMW) || square.Has(IsoObjectType.stairsTW)) {
                boolean0 = false;
            }
        } while (!boolean0);

        return square;
    }

    public IsoGridSquare getRandomFreeTileInRoom() {
        Stack stack = new Stack();

        for (int int0 = 0; int0 < this.RoomList.size(); int0++) {
            if (this.RoomList.get(int0).TileList.size() > 9
                && !this.RoomList.get(int0).Exits.isEmpty()
                && this.RoomList.get(int0).TileList.get(0).getProperties().Is(IsoFlagType.solidfloor)) {
                stack.add(this.RoomList.get(int0));
            }
        }

        if (stack.isEmpty()) {
            return null;
        } else {
            IsoRoom room = (IsoRoom)stack.get(Rand.Next(stack.size()));
            return room.getFreeTile();
        }
    }

    public void roomSpotted(IsoRoom room) {
        synchronized (this.SpottedRooms) {
            if (!this.SpottedRooms.contains(room)) {
                this.SpottedRooms.push(room);
            }
        }
    }

    public void ProcessSpottedRooms() {
        synchronized (this.SpottedRooms) {
            for (int int0 = 0; int0 < this.SpottedRooms.size(); int0++) {
                IsoRoom room = this.SpottedRooms.get(int0);
                if (!room.def.bDoneSpawn) {
                    room.def.bDoneSpawn = true;
                    LuaEventManager.triggerEvent("OnSeeNewRoom", room);
                    VirtualZombieManager.instance.roomSpotted(room);
                    if (!GameClient.bClient
                        && !Core.bLastStand
                        && ("shed".equals(room.def.name) || "garagestorage".equals(room.def.name) || "storageunit".equals(room.def.name))) {
                        byte byte0 = 7;
                        if ("shed".equals(room.def.name) || "garagestorage".equals(room.def.name)) {
                            byte0 = 4;
                        }

                        switch (SandboxOptions.instance.GeneratorSpawning.getValue()) {
                            case 1:
                                byte0 += 3;
                                break;
                            case 2:
                                byte0 += 2;
                            case 3:
                            default:
                                break;
                            case 4:
                                byte0 -= 2;
                                break;
                            case 5:
                                byte0 -= 3;
                        }

                        if (Rand.Next((int)byte0) == 0) {
                            IsoGridSquare square = room.getRandomFreeSquare();
                            if (square != null) {
                                IsoGenerator generator = new IsoGenerator(InventoryItemFactory.CreateItem("Base.Generator"), this, square);
                                if (GameServer.bServer) {
                                    generator.transmitCompleteItemToClients();
                                }
                            }
                        }
                    }
                }
            }

            this.SpottedRooms.clear();
        }
    }

    public void savePlayer() throws IOException {
        if (IsoPlayer.players[0] != null && !IsoPlayer.players[0].isDead()) {
            IsoPlayer.players[0].save();
        }

        GameClient.instance.sendPlayerSave(IsoPlayer.players[0]);
    }

    public void save(DataOutputStream output, boolean bDoChars) throws IOException {
        while (ChunkSaveWorker.instance.bSaving) {
            try {
                Thread.sleep(30L);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }

        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            this.ChunkMap[int0].Save();
        }

        output.writeInt(this.width);
        output.writeInt(this.height);
        output.writeInt(MaxHeight);
        File file = ZomboidFileSystem.instance.getFileInCurrentSave("map_t.bin");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        output = new DataOutputStream(new BufferedOutputStream(fileOutputStream));
        GameTime.instance.save(output);
        output.flush();
        output.close();
        IsoWorld.instance.MetaGrid.save();
        if (PlayerDB.isAllow()) {
            PlayerDB.getInstance().savePlayers();
        }

        ReanimatedPlayers.instance.saveReanimatedPlayers();
    }

    public boolean LoadPlayer(int WorldVersion) throws FileNotFoundException, IOException {
        if (GameClient.bClient) {
            return ClientPlayerDB.getInstance().loadNetworkPlayer();
        } else {
            File file = ZomboidFileSystem.instance.getFileInCurrentSave("map_p.bin");
            if (!file.exists()) {
                PlayerDB.getInstance().importPlayersFromVehiclesDB();
                return PlayerDB.getInstance().loadLocalPlayer(1);
            } else {
                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                synchronized (SliceY.SliceBufferLock) {
                    SliceY.SliceBuffer.clear();
                    int int0 = bufferedInputStream.read(SliceY.SliceBuffer.array());
                    SliceY.SliceBuffer.limit(int0);
                    byte byte0 = SliceY.SliceBuffer.get();
                    byte byte1 = SliceY.SliceBuffer.get();
                    byte byte2 = SliceY.SliceBuffer.get();
                    byte byte3 = SliceY.SliceBuffer.get();
                    if (byte0 == 80 && byte1 == 76 && byte2 == 89 && byte3 == 82) {
                        WorldVersion = SliceY.SliceBuffer.getInt();
                    } else {
                        SliceY.SliceBuffer.rewind();
                    }

                    if (WorldVersion >= 69) {
                        String string = GameWindow.ReadString(SliceY.SliceBuffer);
                        if (GameClient.bClient && WorldVersion < 71) {
                            string = ServerOptions.instance.ServerPlayerID.getValue();
                        }

                        if (GameClient.bClient && !IsoPlayer.isServerPlayerIDValid(string)) {
                            GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_ServerPlayerIDMismatch");
                            GameLoadingState.playerWrongIP = true;
                            return false;
                        }
                    }

                    instance.ChunkMap[IsoPlayer.getPlayerIndex()].WorldX = SliceY.SliceBuffer.getInt() + IsoWorld.saveoffsetx * 30;
                    instance.ChunkMap[IsoPlayer.getPlayerIndex()].WorldY = SliceY.SliceBuffer.getInt() + IsoWorld.saveoffsety * 30;
                    SliceY.SliceBuffer.getInt();
                    SliceY.SliceBuffer.getInt();
                    SliceY.SliceBuffer.getInt();
                    if (IsoPlayer.getInstance() == null) {
                        IsoPlayer.setInstance(new IsoPlayer(instance));
                        IsoPlayer.players[0] = IsoPlayer.getInstance();
                    }

                    IsoPlayer.getInstance().load(SliceY.SliceBuffer, WorldVersion);
                    fileInputStream.close();
                }

                PlayerDB.getInstance().saveLocalPlayersForce();
                file.delete();
                PlayerDB.getInstance().uploadLocalPlayers2DB();
                return true;
            }
        }
    }

    public IsoGridSquare getRelativeGridSquare(int x, int y, int z) {
        int int0 = this.ChunkMap[0].getWorldXMin();
        IsoChunkMap chunkMap = this.ChunkMap[0];
        int int1 = int0 * 10;
        int0 = this.ChunkMap[0].getWorldYMin();
        chunkMap = this.ChunkMap[0];
        int int2 = int0 * 10;
        x += int1;
        y += int2;
        return this.getGridSquare(x, y, z);
    }

    public IsoGridSquare createNewGridSquare(int x, int y, int z, boolean recalcAll) {
        if (!IsoWorld.instance.isValidSquare(x, y, z)) {
            return null;
        } else {
            IsoGridSquare square = this.getGridSquare(x, y, z);
            if (square != null) {
                return square;
            } else {
                if (GameServer.bServer) {
                    int int0 = x / 10;
                    int int1 = y / 10;
                    if (ServerMap.instance.getChunk(int0, int1) != null) {
                        square = IsoGridSquare.getNew(this, null, x, y, z);
                        ServerMap.instance.setGridSquare(x, y, z, square);
                    }
                } else if (this.getChunkForGridSquare(x, y, z) != null) {
                    square = IsoGridSquare.getNew(this, null, x, y, z);
                    this.ConnectNewSquare(square, true);
                }

                if (square != null && recalcAll) {
                    square.RecalcAllWithNeighbours(true);
                }

                return square;
            }
        }
    }

    public IsoGridSquare getGridSquareDirect(int x, int y, int z, int playerIndex) {
        int int0 = IsoChunkMap.ChunkWidthInTiles;
        return this.gridSquares[playerIndex][x + y * int0 + z * int0 * int0];
    }

    public boolean isInChunkMap(int x, int y) {
        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            int int1 = this.ChunkMap[int0].getWorldXMinTiles();
            int int2 = this.ChunkMap[int0].getWorldXMaxTiles();
            int int3 = this.ChunkMap[int0].getWorldYMinTiles();
            int int4 = this.ChunkMap[int0].getWorldYMaxTiles();
            if (x >= int1 && x < int2 && y >= int3 && y < int4) {
                return true;
            }
        }

        return false;
    }

    public ArrayList<IsoObject> getProcessIsoObjectRemove() {
        return this.ProcessIsoObjectRemove;
    }

    public void checkHaveRoof(int x, int y) {
        boolean boolean0 = false;

        for (int int0 = 8; int0 >= 0; int0--) {
            IsoGridSquare square = this.getGridSquare(x, y, int0);
            if (square != null) {
                if (boolean0 != square.haveRoof) {
                    square.haveRoof = boolean0;
                    square.RecalcAllWithNeighbours(true);
                }

                if (square.Is(IsoFlagType.solidfloor)) {
                    boolean0 = true;
                }
            }
        }
    }

    public IsoZombie getFakeZombieForHit() {
        if (this.fakeZombieForHit == null) {
            this.fakeZombieForHit = new IsoZombie(this);
        }

        return this.fakeZombieForHit;
    }

    public void addHeatSource(IsoHeatSource heatSource) {
        if (!GameServer.bServer) {
            if (this.heatSources.contains(heatSource)) {
                DebugLog.log("ERROR addHeatSource called again with the same HeatSource");
            } else {
                this.heatSources.add(heatSource);
            }
        }
    }

    public void removeHeatSource(IsoHeatSource heatSource) {
        if (!GameServer.bServer) {
            this.heatSources.remove(heatSource);
        }
    }

    public void updateHeatSources() {
        if (!GameServer.bServer) {
            for (int int0 = this.heatSources.size() - 1; int0 >= 0; int0--) {
                IsoHeatSource heatSource = this.heatSources.get(int0);
                if (!heatSource.isInBounds()) {
                    this.heatSources.remove(int0);
                }
            }
        }
    }

    public int getHeatSourceTemperature(int x, int y, int z) {
        int int0 = 0;

        for (int int1 = 0; int1 < this.heatSources.size(); int1++) {
            IsoHeatSource heatSource = this.heatSources.get(int1);
            if (heatSource.getZ() == z) {
                float float0 = IsoUtils.DistanceToSquared(x, y, heatSource.getX(), heatSource.getY());
                if (float0 < heatSource.getRadius() * heatSource.getRadius()) {
                    LosUtil.TestResults testResults = LosUtil.lineClear(this, heatSource.getX(), heatSource.getY(), heatSource.getZ(), x, y, z, false);
                    if (testResults == LosUtil.TestResults.Clear || testResults == LosUtil.TestResults.ClearThroughOpenDoor) {
                        int0 = (int)(int0 + heatSource.getTemperature() * (1.0 - Math.sqrt(float0) / heatSource.getRadius()));
                    }
                }
            }
        }

        return int0;
    }

    public float getHeatSourceHighestTemperature(float surroundingAirTemperature, int x, int y, int z) {
        float float0 = surroundingAirTemperature;
        float float1 = surroundingAirTemperature;
        float float2 = 0.0F;
        Object object = null;
        float float3 = 0.0F;

        for (int int0 = 0; int0 < this.heatSources.size(); int0++) {
            IsoHeatSource heatSource = this.heatSources.get(int0);
            if (heatSource.getZ() == z) {
                float float4 = IsoUtils.DistanceToSquared(x, y, heatSource.getX(), heatSource.getY());
                object = this.getGridSquare(heatSource.getX(), heatSource.getY(), heatSource.getZ());
                float3 = 0.0F;
                if (object != null) {
                    if (!((IsoGridSquare)object).isInARoom()) {
                        float3 = float0 - 30.0F;
                        if (float3 < -15.0F) {
                            float3 = -15.0F;
                        } else if (float3 > 5.0F) {
                            float3 = 5.0F;
                        }
                    } else {
                        float3 = float0 - 30.0F;
                        if (float3 < -7.0F) {
                            float3 = -7.0F;
                        } else if (float3 > 7.0F) {
                            float3 = 7.0F;
                        }
                    }
                }

                float2 = ClimateManager.lerp((float)(1.0 - Math.sqrt(float4) / heatSource.getRadius()), float0, heatSource.getTemperature() + float3);
                if (!(float2 <= float1) && float4 < heatSource.getRadius() * heatSource.getRadius()) {
                    LosUtil.TestResults testResults = LosUtil.lineClear(this, heatSource.getX(), heatSource.getY(), heatSource.getZ(), x, y, z, false);
                    if (testResults == LosUtil.TestResults.Clear || testResults == LosUtil.TestResults.ClearThroughOpenDoor) {
                        float1 = float2;
                    }
                }
            }
        }

        return float1;
    }

    public void putInVehicle(IsoGameCharacter chr) {
        if (chr != null && chr.savedVehicleSeat != -1) {
            int int0 = ((int)chr.getX() - 4) / 10;
            int int1 = ((int)chr.getY() - 4) / 10;
            int int2 = ((int)chr.getX() + 4) / 10;
            int int3 = ((int)chr.getY() + 4) / 10;

            for (int int4 = int1; int4 <= int3; int4++) {
                for (int int5 = int0; int5 <= int2; int5++) {
                    IsoChunk chunk = this.getChunkForGridSquare(int5 * 10, int4 * 10, (int)chr.getZ());
                    if (chunk != null) {
                        for (int int6 = 0; int6 < chunk.vehicles.size(); int6++) {
                            BaseVehicle vehicle = chunk.vehicles.get(int6);
                            if ((int)vehicle.getZ() == (int)chr.getZ()
                                && IsoUtils.DistanceToSquared(vehicle.getX(), vehicle.getY(), chr.savedVehicleX, chr.savedVehicleY) < 0.010000001F) {
                                if (vehicle.VehicleID == -1) {
                                    return;
                                }

                                VehicleScript.Position position = vehicle.getPassengerPosition(chr.savedVehicleSeat, "inside");
                                if (position != null && !vehicle.isSeatOccupied(chr.savedVehicleSeat)) {
                                    vehicle.enter(chr.savedVehicleSeat, chr, position.offset);
                                    LuaEventManager.triggerEvent("OnEnterVehicle", chr);
                                    if (vehicle.getCharacter(chr.savedVehicleSeat) == chr && chr.savedVehicleRunning) {
                                        vehicle.resumeRunningAfterLoad();
                                    }
                                }

                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @Deprecated
    public void resumeVehicleSounds(IsoGameCharacter chr) {
        if (chr != null && chr.savedVehicleSeat != -1) {
            int int0 = ((int)chr.getX() - 4) / 10;
            int int1 = ((int)chr.getY() - 4) / 10;
            int int2 = ((int)chr.getX() + 4) / 10;
            int int3 = ((int)chr.getY() + 4) / 10;

            for (int int4 = int1; int4 <= int3; int4++) {
                for (int int5 = int0; int5 <= int2; int5++) {
                    IsoChunk chunk = this.getChunkForGridSquare(int5 * 10, int4 * 10, (int)chr.getZ());
                    if (chunk != null) {
                        for (int int6 = 0; int6 < chunk.vehicles.size(); int6++) {
                            BaseVehicle vehicle = chunk.vehicles.get(int6);
                            if (vehicle.lightbarSirenMode.isEnable()) {
                                vehicle.setLightbarSirenMode(vehicle.lightbarSirenMode.get());
                            }
                        }
                    }
                }
            }
        }
    }

    private void AddUniqueToBuildingList(ArrayList<IsoBuilding> arrayList, IsoBuilding building) {
        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            if (arrayList.get(int0) == building) {
                return;
            }
        }

        arrayList.add(building);
    }

    public IsoSpriteManager getSpriteManager() {
        return IsoSpriteManager.instance;
    }

    public static enum BuildingSearchCriteria {
        Food,
        Defense,
        Wood,
        Weapons,
        General;
    }

    public static final class PerPlayerRender {
        public final IsoGridStack GridStacks = new IsoGridStack(9);
        public boolean[][][] VisiOccludedFlags;
        public boolean[][] VisiCulledFlags;
        public short[][][] StencilValues;
        public boolean[][] FlattenGrassEtc;
        public int minX;
        public int minY;
        public int maxX;
        public int maxY;

        public void setSize(int w, int h) {
            if (this.VisiOccludedFlags == null || this.VisiOccludedFlags.length < w || this.VisiOccludedFlags[0].length < h) {
                this.VisiOccludedFlags = new boolean[w][h][2];
                this.VisiCulledFlags = new boolean[w][h];
                this.StencilValues = new short[w][h][2];
                this.FlattenGrassEtc = new boolean[w][h];
            }
        }
    }

    private class SnowGrid {
        public int w = 256;
        public int h = 256;
        public int frac = 0;
        public static final int N = 0;
        public static final int S = 1;
        public static final int W = 2;
        public static final int E = 3;
        public static final int A = 0;
        public static final int B = 1;
        public final Texture[][][] grid = new Texture[this.w][this.h][2];
        public final byte[][][] gridType = new byte[this.w][this.h][2];

        public SnowGrid(int arg1) {
            this.init(arg1);
        }

        public IsoCell.SnowGrid init(int arg0) {
            if (!IsoCell.this.hasSetupSnowGrid) {
                IsoCell.this.snowNoise2D = new Noise2D();
                IsoCell.this.snowNoise2D.addLayer(16, 0.5F, 3.0F);
                IsoCell.this.snowNoise2D.addLayer(32, 2.0F, 5.0F);
                IsoCell.this.snowNoise2D.addLayer(64, 5.0F, 8.0F);
                byte byte0 = 0;
                IsoCell.this.snowGridTiles_Square = IsoCell.this.new SnowGridTiles(byte0++);
                byte byte1 = 40;

                for (int int0 = 0; int0 < 4; int0++) {
                    IsoCell.this.snowGridTiles_Square.add(Texture.getSharedTexture("e_newsnow_ground_1_" + (byte1 + int0)));
                }

                IsoCell.this.snowGridTiles_Enclosed = IsoCell.this.new SnowGridTiles(byte0++);
                byte1 = 0;

                for (int int1 = 0; int1 < 4; int1++) {
                    IsoCell.this.snowGridTiles_Enclosed.add(Texture.getSharedTexture("e_newsnow_ground_1_" + (byte1 + int1)));
                }

                IsoCell.this.snowGridTiles_Cove = new IsoCell.SnowGridTiles[4];

                for (int int2 = 0; int2 < 4; int2++) {
                    IsoCell.this.snowGridTiles_Cove[int2] = IsoCell.this.new SnowGridTiles(byte0++);
                    if (int2 == 0) {
                        byte1 = 7;
                    }

                    if (int2 == 2) {
                        byte1 = 4;
                    }

                    if (int2 == 1) {
                        byte1 = 5;
                    }

                    if (int2 == 3) {
                        byte1 = 6;
                    }

                    for (int int3 = 0; int3 < 3; int3++) {
                        IsoCell.this.snowGridTiles_Cove[int2].add(Texture.getSharedTexture("e_newsnow_ground_1_" + (byte1 + int3 * 4)));
                    }
                }

                IsoCell.this.m_snowFirstNonSquare = byte0;
                IsoCell.this.snowGridTiles_Edge = new IsoCell.SnowGridTiles[4];

                for (int int4 = 0; int4 < 4; int4++) {
                    IsoCell.this.snowGridTiles_Edge[int4] = IsoCell.this.new SnowGridTiles(byte0++);
                    if (int4 == 0) {
                        byte1 = 16;
                    }

                    if (int4 == 2) {
                        byte1 = 18;
                    }

                    if (int4 == 1) {
                        byte1 = 17;
                    }

                    if (int4 == 3) {
                        byte1 = 19;
                    }

                    for (int int5 = 0; int5 < 3; int5++) {
                        IsoCell.this.snowGridTiles_Edge[int4].add(Texture.getSharedTexture("e_newsnow_ground_1_" + (byte1 + int5 * 4)));
                    }
                }

                IsoCell.this.snowGridTiles_Strip = new IsoCell.SnowGridTiles[4];

                for (int int6 = 0; int6 < 4; int6++) {
                    IsoCell.this.snowGridTiles_Strip[int6] = IsoCell.this.new SnowGridTiles(byte0++);
                    if (int6 == 0) {
                        byte1 = 28;
                    }

                    if (int6 == 2) {
                        byte1 = 29;
                    }

                    if (int6 == 1) {
                        byte1 = 31;
                    }

                    if (int6 == 3) {
                        byte1 = 30;
                    }

                    for (int int7 = 0; int7 < 3; int7++) {
                        IsoCell.this.snowGridTiles_Strip[int6].add(Texture.getSharedTexture("e_newsnow_ground_1_" + (byte1 + int7 * 4)));
                    }
                }

                IsoCell.this.hasSetupSnowGrid = true;
            }

            IsoCell.this.snowGridTiles_Square.resetCounter();
            IsoCell.this.snowGridTiles_Enclosed.resetCounter();

            for (int int8 = 0; int8 < 4; int8++) {
                IsoCell.this.snowGridTiles_Cove[int8].resetCounter();
                IsoCell.this.snowGridTiles_Edge[int8].resetCounter();
                IsoCell.this.snowGridTiles_Strip[int8].resetCounter();
            }

            this.frac = arg0;
            Noise2D noise2D = IsoCell.this.snowNoise2D;

            for (int int9 = 0; int9 < this.h; int9++) {
                for (int int10 = 0; int10 < this.w; int10++) {
                    for (int int11 = 0; int11 < 2; int11++) {
                        this.grid[int10][int9][int11] = null;
                        this.gridType[int10][int9][int11] = -1;
                    }

                    if (noise2D.layeredNoise(int10 / 10.0F, int9 / 10.0F) <= arg0 / 100.0F) {
                        this.grid[int10][int9][0] = IsoCell.this.snowGridTiles_Square.getNext();
                        this.gridType[int10][int9][0] = IsoCell.this.snowGridTiles_Square.ID;
                    }
                }
            }

            for (int int12 = 0; int12 < this.h; int12++) {
                for (int int13 = 0; int13 < this.w; int13++) {
                    Texture texture = this.grid[int13][int12][0];
                    if (texture == null) {
                        boolean boolean0 = this.check(int13, int12 - 1);
                        boolean boolean1 = this.check(int13, int12 + 1);
                        boolean boolean2 = this.check(int13 - 1, int12);
                        boolean boolean3 = this.check(int13 + 1, int12);
                        int int14 = 0;
                        if (boolean0) {
                            int14++;
                        }

                        if (boolean1) {
                            int14++;
                        }

                        if (boolean3) {
                            int14++;
                        }

                        if (boolean2) {
                            int14++;
                        }

                        if (int14 != 0) {
                            if (int14 == 1) {
                                if (boolean0) {
                                    this.set(int13, int12, 0, IsoCell.this.snowGridTiles_Strip[0]);
                                } else if (boolean1) {
                                    this.set(int13, int12, 0, IsoCell.this.snowGridTiles_Strip[1]);
                                } else if (boolean3) {
                                    this.set(int13, int12, 0, IsoCell.this.snowGridTiles_Strip[3]);
                                } else if (boolean2) {
                                    this.set(int13, int12, 0, IsoCell.this.snowGridTiles_Strip[2]);
                                }
                            } else if (int14 == 2) {
                                if (boolean0 && boolean1) {
                                    this.set(int13, int12, 0, IsoCell.this.snowGridTiles_Strip[0]);
                                    this.set(int13, int12, 1, IsoCell.this.snowGridTiles_Strip[1]);
                                } else if (boolean3 && boolean2) {
                                    this.set(int13, int12, 0, IsoCell.this.snowGridTiles_Strip[2]);
                                    this.set(int13, int12, 1, IsoCell.this.snowGridTiles_Strip[3]);
                                } else if (boolean0) {
                                    this.set(int13, int12, 0, IsoCell.this.snowGridTiles_Edge[boolean2 ? 0 : 3]);
                                } else if (boolean1) {
                                    this.set(int13, int12, 0, IsoCell.this.snowGridTiles_Edge[boolean2 ? 2 : 1]);
                                } else if (boolean2) {
                                    this.set(int13, int12, 0, IsoCell.this.snowGridTiles_Edge[boolean0 ? 0 : 2]);
                                } else if (boolean3) {
                                    this.set(int13, int12, 0, IsoCell.this.snowGridTiles_Edge[boolean0 ? 3 : 1]);
                                }
                            } else if (int14 == 3) {
                                if (!boolean0) {
                                    this.set(int13, int12, 0, IsoCell.this.snowGridTiles_Cove[1]);
                                } else if (!boolean1) {
                                    this.set(int13, int12, 0, IsoCell.this.snowGridTiles_Cove[0]);
                                } else if (!boolean3) {
                                    this.set(int13, int12, 0, IsoCell.this.snowGridTiles_Cove[2]);
                                } else if (!boolean2) {
                                    this.set(int13, int12, 0, IsoCell.this.snowGridTiles_Cove[3]);
                                }
                            } else if (int14 == 4) {
                                this.set(int13, int12, 0, IsoCell.this.snowGridTiles_Enclosed);
                            }
                        }
                    }
                }
            }

            return this;
        }

        public boolean check(int arg0, int arg1) {
            if (arg0 == this.w) {
                arg0 = 0;
            }

            if (arg0 == -1) {
                arg0 = this.w - 1;
            }

            if (arg1 == this.h) {
                arg1 = 0;
            }

            if (arg1 == -1) {
                arg1 = this.h - 1;
            }

            if (arg0 < 0 || arg0 >= this.w) {
                return false;
            } else if (arg1 >= 0 && arg1 < this.h) {
                Texture texture = this.grid[arg0][arg1][0];
                return IsoCell.this.snowGridTiles_Square.contains(texture);
            } else {
                return false;
            }
        }

        public boolean checkAny(int arg0, int arg1) {
            if (arg0 == this.w) {
                arg0 = 0;
            }

            if (arg0 == -1) {
                arg0 = this.w - 1;
            }

            if (arg1 == this.h) {
                arg1 = 0;
            }

            if (arg1 == -1) {
                arg1 = this.h - 1;
            }

            if (arg0 < 0 || arg0 >= this.w) {
                return false;
            } else {
                return arg1 >= 0 && arg1 < this.h ? this.grid[arg0][arg1][0] != null : false;
            }
        }

        public void set(int arg0, int arg1, int arg2, IsoCell.SnowGridTiles arg3) {
            if (arg0 == this.w) {
                arg0 = 0;
            }

            if (arg0 == -1) {
                arg0 = this.w - 1;
            }

            if (arg1 == this.h) {
                arg1 = 0;
            }

            if (arg1 == -1) {
                arg1 = this.h - 1;
            }

            if (arg0 >= 0 && arg0 < this.w) {
                if (arg1 >= 0 && arg1 < this.h) {
                    this.grid[arg0][arg1][arg2] = arg3.getNext();
                    this.gridType[arg0][arg1][arg2] = arg3.ID;
                }
            }
        }

        public void subtract(IsoCell.SnowGrid arg0) {
            for (int int0 = 0; int0 < this.h; int0++) {
                for (int int1 = 0; int1 < this.w; int1++) {
                    for (int int2 = 0; int2 < 2; int2++) {
                        if (arg0.gridType[int1][int0][int2] == this.gridType[int1][int0][int2]) {
                            this.grid[int1][int0][int2] = null;
                            this.gridType[int1][int0][int2] = -1;
                        }
                    }
                }
            }
        }
    }

    protected class SnowGridTiles {
        protected byte ID = -1;
        private int counter = -1;
        private final ArrayList<Texture> textures = new ArrayList<>();

        public SnowGridTiles(byte arg1) {
            this.ID = arg1;
        }

        protected void add(Texture texture) {
            this.textures.add(texture);
        }

        protected Texture getNext() {
            this.counter++;
            if (this.counter >= this.textures.size()) {
                this.counter = 0;
            }

            return this.textures.get(this.counter);
        }

        protected Texture get(int int0) {
            return this.textures.get(int0);
        }

        protected int size() {
            return this.textures.size();
        }

        protected Texture getRand() {
            return this.textures.get(Rand.Next(4));
        }

        protected boolean contains(Texture texture) {
            return this.textures.contains(texture);
        }

        protected void resetCounter() {
            this.counter = 0;
        }
    }

    private static class s_performance {
        static final PerformanceProfileProbe isoCellUpdate = new PerformanceProfileProbe("IsoCell.update");
        static final PerformanceProfileProbe isoCellRender = new PerformanceProfileProbe("IsoCell.render");
        static final PerformanceProfileProbe isoCellRenderTiles = new PerformanceProfileProbe("IsoCell.renderTiles");
        static final PerformanceProfileProbe isoCellDoBuilding = new PerformanceProfileProbe("IsoCell.doBuilding");

        static class renderTiles {
            static final PerformanceProfileProbe performRenderTiles = new PerformanceProfileProbe("performRenderTiles");
            static final PerformanceProfileProbe recalculateAnyGridStacks = new PerformanceProfileProbe("recalculateAnyGridStacks");
            static final PerformanceProfileProbe flattenAnyFoliage = new PerformanceProfileProbe("flattenAnyFoliage");
            static final PerformanceProfileProbe renderDebugPhysics = new PerformanceProfileProbe("renderDebugPhysics");
            static final PerformanceProfileProbe renderDebugLighting = new PerformanceProfileProbe("renderDebugLighting");
            static PerformanceProfileProbeList<IsoCell.s_performance.renderTiles.PperformRenderTilesLayer> performRenderTilesLayers = PerformanceProfileProbeList.construct(
                "performRenderTiles",
                8,
                IsoCell.s_performance.renderTiles.PperformRenderTilesLayer.class,
                IsoCell.s_performance.renderTiles.PperformRenderTilesLayer::new
            );

            static class PperformRenderTilesLayer extends PerformanceProfileProbe {
                final PerformanceProfileProbe renderIsoWater = new PerformanceProfileProbe("renderIsoWater");
                final PerformanceProfileProbe renderFloor = new PerformanceProfileProbe("renderFloor");
                final PerformanceProfileProbe renderPuddles = new PerformanceProfileProbe("renderPuddles");
                final PerformanceProfileProbe renderShore = new PerformanceProfileProbe("renderShore");
                final PerformanceProfileProbe renderSnow = new PerformanceProfileProbe("renderSnow");
                final PerformanceProfileProbe renderBlood = new PerformanceProfileProbe("renderBlood");
                final PerformanceProfileProbe vegetationCorpses = new PerformanceProfileProbe("vegetationCorpses");
                final PerformanceProfileProbe renderFloorShading = new PerformanceProfileProbe("renderFloorShading");
                final PerformanceProfileProbe renderShadows = new PerformanceProfileProbe("renderShadows");
                final PerformanceProfileProbe luaOnPostFloorLayerDraw = new PerformanceProfileProbe("luaOnPostFloorLayerDraw");
                final PerformanceProfileProbe minusFloorCharacters = new PerformanceProfileProbe("minusFloorCharacters");

                PperformRenderTilesLayer(String string) {
                    super(string);
                }
            }
        }
    }
}
