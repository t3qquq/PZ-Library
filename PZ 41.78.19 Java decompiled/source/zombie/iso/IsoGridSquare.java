// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.GL20;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.MapCollisionData;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.ZomboidBitFlag;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.states.ZombieIdleState;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.Moodles.MoodleType;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferWriter;
import zombie.core.opengl.RenderSettings;
import zombie.core.opengl.Shader;
import zombie.core.opengl.ShaderProgram;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.properties.PropertyContainer;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.erosion.ErosionData;
import zombie.erosion.categories.ErosionCategory;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.areas.SafeHouse;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.areas.isoregion.regions.IWorldRegion;
import zombie.iso.areas.isoregion.regions.IsoWorldRegion;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoBrokenGlass;
import zombie.iso.objects.IsoCarBatteryCharger;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoFire;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoMannequin;
import zombie.iso.objects.IsoRainSplash;
import zombie.iso.objects.IsoRaindrop;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTrap;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWaveSignal;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.objects.RainManager;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.sprite.shapers.FloorShaper;
import zombie.iso.sprite.shapers.FloorShaperAttachedSprites;
import zombie.iso.sprite.shapers.FloorShaperDeDiamond;
import zombie.iso.sprite.shapers.FloorShaperDiamond;
import zombie.iso.sprite.shapers.WallShaperN;
import zombie.iso.sprite.shapers.WallShaperW;
import zombie.iso.sprite.shapers.WallShaperWhole;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.meta.Meta;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerLOS;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.util.io.BitHeader;
import zombie.util.io.BitHeaderRead;
import zombie.util.io.BitHeaderWrite;
import zombie.util.list.PZArrayList;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PolygonalMap2;

public final class IsoGridSquare {
    private boolean hasTree;
    private ArrayList<Float> LightInfluenceB;
    private ArrayList<Float> LightInfluenceG;
    private ArrayList<Float> LightInfluenceR;
    public final IsoGridSquare[] nav = new IsoGridSquare[8];
    public int collideMatrix = -1;
    public int pathMatrix = -1;
    public int visionMatrix = -1;
    public IsoRoom room = null;
    public IsoGridSquare w;
    public IsoGridSquare nw;
    public IsoGridSquare sw;
    public IsoGridSquare s;
    public IsoGridSquare n;
    public IsoGridSquare ne;
    public IsoGridSquare se;
    public IsoGridSquare e;
    public boolean haveSheetRope = false;
    private IWorldRegion isoWorldRegion;
    private boolean hasSetIsoWorldRegion = false;
    public int ObjectsSyncCount = 0;
    public IsoBuilding roofHideBuilding;
    public boolean bFlattenGrassEtc;
    private static final long VisiFlagTimerPeriod_ms = 750L;
    private final boolean[] playerCutawayFlags = new boolean[4];
    private final long[] playerCutawayFlagLockUntilTimes = new long[4];
    private final boolean[] targetPlayerCutawayFlags = new boolean[4];
    private final boolean[] playerIsDissolvedFlags = new boolean[4];
    private final long[] playerIsDissolvedFlagLockUntilTimes = new long[4];
    private final boolean[] targetPlayerIsDissolvedFlags = new boolean[4];
    private IsoWaterGeometry water = null;
    private IsoPuddlesGeometry puddles = null;
    private float puddlesCacheSize = -1.0F;
    private float puddlesCacheLevel = -1.0F;
    public final IsoGridSquare.ILighting[] lighting = new IsoGridSquare.ILighting[4];
    public int x;
    public int y;
    public int z;
    private int CachedScreenValue = -1;
    public float CachedScreenX;
    public float CachedScreenY;
    private static long torchTimer = 0L;
    public boolean SolidFloorCached = false;
    public boolean SolidFloor = false;
    private boolean CacheIsFree = false;
    private boolean CachedIsFree = false;
    public IsoChunk chunk;
    public int roomID = -1;
    public Integer ID = -999;
    public IsoMetaGrid.Zone zone;
    private final ArrayList<IsoGameCharacter> DeferedCharacters = new ArrayList<>();
    private int DeferredCharacterTick = -1;
    private final ArrayList<IsoMovingObject> StaticMovingObjects = new ArrayList<>(0);
    private final ArrayList<IsoMovingObject> MovingObjects = new ArrayList<>(0);
    protected final PZArrayList<IsoObject> Objects = new PZArrayList<>(IsoObject.class, 2);
    protected final PZArrayList<IsoObject> localTemporaryObjects = new PZArrayList<>(IsoObject.class, 2);
    private final ArrayList<IsoWorldInventoryObject> WorldObjects = new ArrayList<>();
    final ZomboidBitFlag hasTypes = new ZomboidBitFlag(IsoObjectType.MAX.index());
    private final PropertyContainer Properties = new PropertyContainer();
    private final ArrayList<IsoObject> SpecialObjects = new ArrayList<>(0);
    public boolean haveRoof = false;
    private boolean burntOut = false;
    private boolean bHasFlies = false;
    private IsoGridOcclusionData OcclusionDataCache = null;
    private static final PZArrayList<IsoWorldInventoryObject> tempWorldInventoryObjects = new PZArrayList<>(IsoWorldInventoryObject.class, 16);
    public static final ConcurrentLinkedQueue<IsoGridSquare> isoGridSquareCache = new ConcurrentLinkedQueue<>();
    public static ArrayDeque<IsoGridSquare> loadGridSquareCache;
    private boolean overlayDone = false;
    private KahluaTable table = null;
    private int trapPositionX = -1;
    private int trapPositionY = -1;
    private int trapPositionZ = -1;
    private boolean haveElectricity = false;
    public static int gridSquareCacheEmptyTimer = 0;
    private static float darkStep = 0.06F;
    public static int RecalcLightTime = 0;
    private static int lightcache = 0;
    public static final ArrayList<IsoGridSquare> choices = new ArrayList<>();
    public static boolean USE_WALL_SHADER = true;
    private static final int cutawayY = 0;
    private static final int cutawayNWWidth = 66;
    private static final int cutawayNWHeight = 226;
    private static final int cutawaySEXCut = 1084;
    private static final int cutawaySEXUncut = 1212;
    private static final int cutawaySEWidth = 6;
    private static final int cutawaySEHeight = 196;
    private static final int cutawayNXFullyCut = 700;
    private static final int cutawayNXCutW = 444;
    private static final int cutawayNXUncut = 828;
    private static final int cutawayNXCutE = 956;
    private static final int cutawayWXFullyCut = 512;
    private static final int cutawayWXCutS = 768;
    private static final int cutawayWXUncut = 896;
    private static final int cutawayWXCutN = 256;
    private static final int cutawayFenceXOffset = 1;
    private static final int cutawayLogWallXOffset = 1;
    private static final int cutawayMedicalCurtainWXOffset = -3;
    private static final int cutawaySpiffoWindowXOffset = -24;
    private static final int cutawayRoof4XOffset = -60;
    private static final int cutawayRoof17XOffset = -46;
    private static final int cutawayRoof28XOffset = -60;
    private static final int cutawayRoof41XOffset = -46;
    private static final ColorInfo lightInfoTemp = new ColorInfo();
    private static final float doorWindowCutawayLightMin = 0.3F;
    private static boolean bWallCutawayW;
    private static boolean bWallCutawayN;
    public boolean isSolidFloorCache;
    public boolean isExteriorCache;
    public boolean isVegitationCache;
    public int hourLastSeen = Integer.MIN_VALUE;
    static IsoGridSquare lastLoaded = null;
    public static int IDMax = -1;
    static int col = -1;
    static int path = -1;
    static int pathdoor = -1;
    static int vision = -1;
    public long hashCodeObjects;
    static final Color tr = new Color(1, 1, 1, 1);
    static final Color tl = new Color(1, 1, 1, 1);
    static final Color br = new Color(1, 1, 1, 1);
    static final Color bl = new Color(1, 1, 1, 1);
    static final Color interp1 = new Color(1, 1, 1, 1);
    static final Color interp2 = new Color(1, 1, 1, 1);
    static final Color finalCol = new Color(1, 1, 1, 1);
    public static final IsoGridSquare.CellGetSquare cellGetSquare = new IsoGridSquare.CellGetSquare();
    public boolean propertiesDirty = true;
    public static boolean UseSlowCollision = false;
    private static boolean bDoSlowPathfinding = false;
    private static final Comparator<IsoMovingObject> comp = (movingObject0, movingObject1) -> movingObject0.compareToY(movingObject1);
    public static boolean isOnScreenLast = false;
    private float splashX;
    private float splashY;
    private float splashFrame = -1.0F;
    private int splashFrameNum;
    private final ColorInfo[] lightInfo = new ColorInfo[4];
    static String[] rainsplashCache = new String[50];
    private static final ColorInfo defColorInfo = new ColorInfo();
    private static final ColorInfo blackColorInfo = new ColorInfo();
    static int colu = 0;
    static int coll = 0;
    static int colr = 0;
    static int colu2 = 0;
    static int coll2 = 0;
    static int colr2 = 0;
    public static boolean CircleStencil = false;
    public static float rmod = 0.0F;
    public static float gmod = 0.0F;
    public static float bmod = 0.0F;
    static final Vector2 tempo = new Vector2();
    static final Vector2 tempo2 = new Vector2();
    private IsoRaindrop RainDrop = null;
    private IsoRainSplash RainSplash = null;
    private ErosionData.Square erosion;
    private static final int[] SURFACE_OFFSETS = new int[8];
    public static final int WALL_TYPE_N = 1;
    public static final int WALL_TYPE_S = 2;
    public static final int WALL_TYPE_W = 4;
    public static final int WALL_TYPE_E = 8;

    public static boolean getMatrixBit(int matrix, int _x, int _y, int _z) {
        return getMatrixBit(matrix, (byte)_x, (byte)_y, (byte)_z);
    }

    public static boolean getMatrixBit(int matrix, byte _x, byte _y, byte _z) {
        return (matrix >> _x + _y * 3 + _z * 9 & 1) != 0;
    }

    public static int setMatrixBit(int matrix, int _x, int _y, int _z, boolean val) {
        return setMatrixBit(matrix, (byte)_x, (byte)_y, (byte)_z, val);
    }

    public static int setMatrixBit(int matrix, byte _x, byte _y, byte _z, boolean val) {
        return val ? matrix | 1 << _x + _y * 3 + _z * 9 : matrix & ~(1 << _x + _y * 3 + _z * 9);
    }

    public void setPlayerCutawayFlag(int playerIndex, boolean bCutaway, long currentTimeMillis) {
        this.targetPlayerCutawayFlags[playerIndex] = bCutaway;
        if (currentTimeMillis > this.playerCutawayFlagLockUntilTimes[playerIndex]
            && this.playerCutawayFlags[playerIndex] != this.targetPlayerCutawayFlags[playerIndex]) {
            this.playerCutawayFlags[playerIndex] = this.targetPlayerCutawayFlags[playerIndex];
            this.playerCutawayFlagLockUntilTimes[playerIndex] = currentTimeMillis + 750L;
        }
    }

    public boolean getPlayerCutawayFlag(int playerIndex, long currentTimeMillis) {
        return currentTimeMillis > this.playerCutawayFlagLockUntilTimes[playerIndex]
            ? this.targetPlayerCutawayFlags[playerIndex]
            : this.playerCutawayFlags[playerIndex];
    }

    public void setIsDissolved(int playerIndex, boolean bDissolved, long currentTimeMillis) {
        this.targetPlayerIsDissolvedFlags[playerIndex] = bDissolved;
        if (currentTimeMillis > this.playerIsDissolvedFlagLockUntilTimes[playerIndex]
            && this.playerIsDissolvedFlags[playerIndex] != this.targetPlayerIsDissolvedFlags[playerIndex]) {
            this.playerIsDissolvedFlags[playerIndex] = this.targetPlayerIsDissolvedFlags[playerIndex];
            this.playerIsDissolvedFlagLockUntilTimes[playerIndex] = currentTimeMillis + 750L;
        }
    }

    public boolean getIsDissolved(int playerIndex, long currentTimeMillis) {
        return currentTimeMillis > this.playerIsDissolvedFlagLockUntilTimes[playerIndex]
            ? this.targetPlayerIsDissolvedFlags[playerIndex]
            : this.playerIsDissolvedFlags[playerIndex];
    }

    public IsoWaterGeometry getWater() {
        if (this.water != null && this.water.m_adjacentChunkLoadedCounter != this.chunk.m_adjacentChunkLoadedCounter) {
            this.water.m_adjacentChunkLoadedCounter = this.chunk.m_adjacentChunkLoadedCounter;
            if (this.water.hasWater || this.water.bShore) {
                this.clearWater();
            }
        }

        if (this.water == null) {
            try {
                this.water = IsoWaterGeometry.pool.alloc();
                this.water.m_adjacentChunkLoadedCounter = this.chunk.m_adjacentChunkLoadedCounter;
                if (this.water.init(this) == null) {
                    IsoWaterGeometry.pool.release(this.water);
                    this.water = null;
                }
            } catch (Exception exception) {
                this.clearWater();
            }
        }

        return this.water;
    }

    public void clearWater() {
        if (this.water != null) {
            IsoWaterGeometry.pool.release(this.water);
            this.water = null;
        }
    }

    public IsoPuddlesGeometry getPuddles() {
        if (this.puddles == null) {
            try {
                synchronized (IsoPuddlesGeometry.pool) {
                    this.puddles = IsoPuddlesGeometry.pool.alloc();
                }

                this.puddles.square = this;
                this.puddles.bRecalc = true;
            } catch (Exception exception) {
                this.clearPuddles();
            }
        }

        return this.puddles;
    }

    public void clearPuddles() {
        if (this.puddles != null) {
            this.puddles.square = null;
            synchronized (IsoPuddlesGeometry.pool) {
                IsoPuddlesGeometry.pool.release(this.puddles);
            }

            this.puddles = null;
        }
    }

    public float getPuddlesInGround() {
        if (this.isInARoom()) {
            return -1.0F;
        } else {
            if (Math.abs(
                    IsoPuddles.getInstance().getPuddlesSize()
                        + Core.getInstance().getPerfPuddles()
                        + IsoCamera.frameState.OffscreenWidth
                        - this.puddlesCacheSize
                )
                > 0.01) {
                this.puddlesCacheSize = IsoPuddles.getInstance().getPuddlesSize() + Core.getInstance().getPerfPuddles() + IsoCamera.frameState.OffscreenWidth;
                this.puddlesCacheLevel = IsoPuddlesCompute.computePuddle(this);
            }

            return this.puddlesCacheLevel;
        }
    }

    public IsoGridOcclusionData getOcclusionData() {
        return this.OcclusionDataCache;
    }

    public IsoGridOcclusionData getOrCreateOcclusionData() {
        assert !GameServer.bServer;

        if (this.OcclusionDataCache == null) {
            this.OcclusionDataCache = new IsoGridOcclusionData(this);
        }

        return this.OcclusionDataCache;
    }

    public void softClear() {
        this.zone = null;
        this.room = null;
        this.w = null;
        this.nw = null;
        this.sw = null;
        this.s = null;
        this.n = null;
        this.ne = null;
        this.se = null;
        this.e = null;
        this.isoWorldRegion = null;
        this.hasSetIsoWorldRegion = false;

        for (int int0 = 0; int0 < 8; int0++) {
            this.nav[int0] = null;
        }
    }

    /**
     * Check if there's any object on this grid that has a sneak modifier, we use this to check if we reduce the chance of being spotted while crouching
     */
    public float getGridSneakModifier(boolean onlySolidTrans) {
        if (!onlySolidTrans) {
            if (this.Properties.Is("CloseSneakBonus")) {
                return Integer.parseInt(this.Properties.Val("CloseSneakBonus")) / 100.0F;
            }

            if (this.Properties.Is(IsoFlagType.collideN)
                || this.Properties.Is(IsoFlagType.collideW)
                || this.Properties.Is(IsoFlagType.WindowN)
                || this.Properties.Is(IsoFlagType.WindowW)
                || this.Properties.Is(IsoFlagType.doorN)
                || this.Properties.Is(IsoFlagType.doorW)) {
                return 8.0F;
            }
        } else if (this.Properties.Is(IsoFlagType.solidtrans)) {
            return 4.0F;
        }

        return 1.0F;
    }

    public boolean isSomethingTo(IsoGridSquare other) {
        return this.isWallTo(other) || this.isWindowTo(other) || this.isDoorTo(other);
    }

    public IsoObject getTransparentWallTo(IsoGridSquare other) {
        if (other == null || other == this || !this.isWallTo(other)) {
            return null;
        } else if (other.x > this.x && other.Properties.Is(IsoFlagType.SpearOnlyAttackThrough) && !other.Properties.Is(IsoFlagType.WindowW)) {
            return other.getWall();
        } else if (this.x > other.x && this.Properties.Is(IsoFlagType.SpearOnlyAttackThrough) && !this.Properties.Is(IsoFlagType.WindowW)) {
            return this.getWall();
        } else if (other.y > this.y && other.Properties.Is(IsoFlagType.SpearOnlyAttackThrough) && !other.Properties.Is(IsoFlagType.WindowN)) {
            return other.getWall();
        } else if (this.y > other.y && this.Properties.Is(IsoFlagType.SpearOnlyAttackThrough) && !this.Properties.Is(IsoFlagType.WindowN)) {
            return this.getWall();
        } else {
            if (other.x != this.x && other.y != this.y) {
                IsoObject object0 = this.getTransparentWallTo(IsoWorld.instance.CurrentCell.getGridSquare(other.x, this.y, this.z));
                IsoObject object1 = this.getTransparentWallTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, other.y, this.z));
                if (object0 != null) {
                    return object0;
                }

                if (object1 != null) {
                    return object1;
                }

                object0 = other.getTransparentWallTo(IsoWorld.instance.CurrentCell.getGridSquare(other.x, this.y, this.z));
                object1 = other.getTransparentWallTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, other.y, this.z));
                if (object0 != null) {
                    return object0;
                }

                if (object1 != null) {
                    return object1;
                }
            }

            return null;
        }
    }

    public boolean isWallTo(IsoGridSquare other) {
        if (other == null || other == this) {
            return false;
        } else if (other.x > this.x && other.Properties.Is(IsoFlagType.collideW) && !other.Properties.Is(IsoFlagType.WindowW)) {
            return true;
        } else if (this.x > other.x && this.Properties.Is(IsoFlagType.collideW) && !this.Properties.Is(IsoFlagType.WindowW)) {
            return true;
        } else if (other.y > this.y && other.Properties.Is(IsoFlagType.collideN) && !other.Properties.Is(IsoFlagType.WindowN)) {
            return true;
        } else if (this.y > other.y && this.Properties.Is(IsoFlagType.collideN) && !this.Properties.Is(IsoFlagType.WindowN)) {
            return true;
        } else {
            if (other.x != this.x && other.y != this.y) {
                if (this.isWallTo(IsoWorld.instance.CurrentCell.getGridSquare(other.x, this.y, this.z))
                    || this.isWallTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, other.y, this.z))) {
                    return true;
                }

                if (other.isWallTo(IsoWorld.instance.CurrentCell.getGridSquare(other.x, this.y, this.z))
                    || other.isWallTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, other.y, this.z))) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean isWindowTo(IsoGridSquare other) {
        if (other == null || other == this) {
            return false;
        } else if (other.x > this.x && other.Properties.Is(IsoFlagType.windowW)) {
            return true;
        } else if (this.x > other.x && this.Properties.Is(IsoFlagType.windowW)) {
            return true;
        } else if (other.y > this.y && other.Properties.Is(IsoFlagType.windowN)) {
            return true;
        } else if (this.y > other.y && this.Properties.Is(IsoFlagType.windowN)) {
            return true;
        } else {
            if (other.x != this.x && other.y != this.y) {
                if (this.isWindowTo(IsoWorld.instance.CurrentCell.getGridSquare(other.x, this.y, this.z))
                    || this.isWindowTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, other.y, this.z))) {
                    return true;
                }

                if (other.isWindowTo(IsoWorld.instance.CurrentCell.getGridSquare(other.x, this.y, this.z))
                    || other.isWindowTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, other.y, this.z))) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean haveDoor() {
        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            if (this.Objects.get(int0) instanceof IsoDoor) {
                return true;
            }
        }

        return false;
    }

    public boolean hasDoorOnEdge(IsoDirections edge, boolean ignoreOpen) {
        for (int int0 = 0; int0 < this.SpecialObjects.size(); int0++) {
            IsoDoor door = Type.tryCastTo(this.SpecialObjects.get(int0), IsoDoor.class);
            if (door != null && door.getSpriteEdge(ignoreOpen) == edge) {
                return true;
            }

            IsoThumpable thumpable = Type.tryCastTo(this.SpecialObjects.get(int0), IsoThumpable.class);
            if (thumpable != null && thumpable.getSpriteEdge(ignoreOpen) == edge) {
                return true;
            }
        }

        return false;
    }

    public boolean hasClosedDoorOnEdge(IsoDirections directions) {
        boolean boolean0 = false;

        for (int int0 = 0; int0 < this.SpecialObjects.size(); int0++) {
            IsoDoor door = Type.tryCastTo(this.SpecialObjects.get(int0), IsoDoor.class);
            if (door != null && !door.IsOpen() && door.getSpriteEdge(boolean0) == directions) {
                return true;
            }

            IsoThumpable thumpable = Type.tryCastTo(this.SpecialObjects.get(int0), IsoThumpable.class);
            if (thumpable != null && !thumpable.IsOpen() && thumpable.getSpriteEdge(boolean0) == directions) {
                return true;
            }
        }

        return false;
    }

    public boolean hasOpenDoorOnEdge(IsoDirections directions) {
        boolean boolean0 = false;

        for (int int0 = 0; int0 < this.SpecialObjects.size(); int0++) {
            IsoDoor door = Type.tryCastTo(this.SpecialObjects.get(int0), IsoDoor.class);
            if (door != null && door.IsOpen() && door.getSpriteEdge(boolean0) == directions) {
                return true;
            }

            IsoThumpable thumpable = Type.tryCastTo(this.SpecialObjects.get(int0), IsoThumpable.class);
            if (thumpable != null && thumpable.IsOpen() && thumpable.getSpriteEdge(boolean0) == directions) {
                return true;
            }
        }

        return false;
    }

    public boolean isDoorTo(IsoGridSquare other) {
        if (other == null || other == this) {
            return false;
        } else if (other.x > this.x && other.Properties.Is(IsoFlagType.doorW)) {
            return true;
        } else if (this.x > other.x && this.Properties.Is(IsoFlagType.doorW)) {
            return true;
        } else if (other.y > this.y && other.Properties.Is(IsoFlagType.doorN)) {
            return true;
        } else if (this.y > other.y && this.Properties.Is(IsoFlagType.doorN)) {
            return true;
        } else {
            if (other.x != this.x && other.y != this.y) {
                if (this.isDoorTo(IsoWorld.instance.CurrentCell.getGridSquare(other.x, this.y, this.z))
                    || this.isDoorTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, other.y, this.z))) {
                    return true;
                }

                if (other.isDoorTo(IsoWorld.instance.CurrentCell.getGridSquare(other.x, this.y, this.z))
                    || other.isDoorTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, other.y, this.z))) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean isBlockedTo(IsoGridSquare other) {
        return this.isWallTo(other) || this.isWindowBlockedTo(other) || this.isDoorBlockedTo(other);
    }

    public boolean isWindowBlockedTo(IsoGridSquare other) {
        if (other == null) {
            return false;
        } else if (other.x > this.x && other.hasBlockedWindow(false)) {
            return true;
        } else if (this.x > other.x && this.hasBlockedWindow(false)) {
            return true;
        } else if (other.y > this.y && other.hasBlockedWindow(true)) {
            return true;
        } else if (this.y > other.y && this.hasBlockedWindow(true)) {
            return true;
        } else {
            if (other.x != this.x && other.y != this.y) {
                if (this.isWindowBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(other.x, this.y, this.z))
                    || this.isWindowBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, other.y, this.z))) {
                    return true;
                }

                if (other.isWindowBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(other.x, this.y, this.z))
                    || other.isWindowBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, other.y, this.z))) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean hasBlockedWindow(boolean north) {
        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            IsoObject object = this.Objects.get(int0);
            if (object instanceof IsoWindow window && window.getNorth() == north) {
                return !window.isDestroyed() && !window.open || window.isBarricaded();
            }
        }

        return false;
    }

    public boolean isDoorBlockedTo(IsoGridSquare other) {
        if (other == null) {
            return false;
        } else if (other.x > this.x && other.hasBlockedDoor(false)) {
            return true;
        } else if (this.x > other.x && this.hasBlockedDoor(false)) {
            return true;
        } else if (other.y > this.y && other.hasBlockedDoor(true)) {
            return true;
        } else if (this.y > other.y && this.hasBlockedDoor(true)) {
            return true;
        } else {
            if (other.x != this.x && other.y != this.y) {
                if (this.isDoorBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(other.x, this.y, this.z))
                    || this.isDoorBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, other.y, this.z))) {
                    return true;
                }

                if (other.isDoorBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(other.x, this.y, this.z))
                    || other.isDoorBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, other.y, this.z))) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean hasBlockedDoor(boolean north) {
        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            IsoObject object = this.Objects.get(int0);
            if (object instanceof IsoDoor door && door.getNorth() == north) {
                return !door.open || door.isBarricaded();
            }

            if (object instanceof IsoThumpable thumpable && thumpable.isDoor() && thumpable.getNorth() == north) {
                return !thumpable.open || thumpable.isBarricaded();
            }
        }

        return false;
    }

    public IsoCurtain getCurtain(IsoObjectType curtainType) {
        for (int int0 = 0; int0 < this.getSpecialObjects().size(); int0++) {
            IsoCurtain curtain = Type.tryCastTo(this.getSpecialObjects().get(int0), IsoCurtain.class);
            if (curtain != null && curtain.getType() == curtainType) {
                return curtain;
            }
        }

        return null;
    }

    public IsoObject getHoppable(boolean north) {
        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            IsoObject object = this.Objects.get(int0);
            PropertyContainer propertyContainer = object.getProperties();
            if (propertyContainer != null && propertyContainer.Is(north ? IsoFlagType.HoppableN : IsoFlagType.HoppableW)) {
                return object;
            }

            if (propertyContainer != null && propertyContainer.Is(north ? IsoFlagType.WindowN : IsoFlagType.WindowW)) {
                return object;
            }
        }

        return null;
    }

    public IsoObject getHoppableTo(IsoGridSquare next) {
        if (next != null && next != this) {
            if (next.x < this.x && next.y == this.y) {
                IsoObject object0 = this.getHoppable(false);
                if (object0 != null) {
                    return object0;
                }
            }

            if (next.x == this.x && next.y < this.y) {
                IsoObject object1 = this.getHoppable(true);
                if (object1 != null) {
                    return object1;
                }
            }

            if (next.x > this.x && next.y == this.y) {
                IsoObject object2 = next.getHoppable(false);
                if (object2 != null) {
                    return object2;
                }
            }

            if (next.x == this.x && next.y > this.y) {
                IsoObject object3 = next.getHoppable(true);
                if (object3 != null) {
                    return object3;
                }
            }

            if (next.x != this.x && next.y != this.y) {
                IsoGridSquare square0 = this.getCell().getGridSquare(this.x, next.y, this.z);
                IsoGridSquare square1 = this.getCell().getGridSquare(next.x, this.y, this.z);
                IsoObject object4 = this.getHoppableTo(square0);
                if (object4 != null) {
                    return object4;
                }

                object4 = this.getHoppableTo(square1);
                if (object4 != null) {
                    return object4;
                }

                object4 = next.getHoppableTo(square0);
                if (object4 != null) {
                    return object4;
                }

                object4 = next.getHoppableTo(square1);
                if (object4 != null) {
                    return object4;
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public boolean isHoppableTo(IsoGridSquare other) {
        if (other == null) {
            return false;
        } else if (other.x != this.x && other.y != this.y) {
            return false;
        } else if (other.x > this.x && other.Properties.Is(IsoFlagType.HoppableW)) {
            return true;
        } else if (this.x > other.x && this.Properties.Is(IsoFlagType.HoppableW)) {
            return true;
        } else {
            return other.y > this.y && other.Properties.Is(IsoFlagType.HoppableN) ? true : this.y > other.y && this.Properties.Is(IsoFlagType.HoppableN);
        }
    }

    public void discard() {
        this.hourLastSeen = -32768;
        this.chunk = null;
        this.zone = null;
        this.LightInfluenceB = null;
        this.LightInfluenceG = null;
        this.LightInfluenceR = null;
        this.room = null;
        this.w = null;
        this.nw = null;
        this.sw = null;
        this.s = null;
        this.n = null;
        this.ne = null;
        this.se = null;
        this.e = null;
        this.isoWorldRegion = null;
        this.hasSetIsoWorldRegion = false;
        this.nav[0] = null;
        this.nav[1] = null;
        this.nav[2] = null;
        this.nav[3] = null;
        this.nav[4] = null;
        this.nav[5] = null;
        this.nav[6] = null;
        this.nav[7] = null;

        for (int int0 = 0; int0 < 4; int0++) {
            if (this.lighting[int0] != null) {
                this.lighting[int0].reset();
            }
        }

        this.SolidFloorCached = false;
        this.SolidFloor = false;
        this.CacheIsFree = false;
        this.CachedIsFree = false;
        this.chunk = null;
        this.roomID = -1;
        this.DeferedCharacters.clear();
        this.DeferredCharacterTick = -1;
        this.StaticMovingObjects.clear();
        this.MovingObjects.clear();
        this.Objects.clear();
        this.WorldObjects.clear();
        this.hasTypes.clear();
        this.table = null;
        this.Properties.Clear();
        this.SpecialObjects.clear();
        this.RainDrop = null;
        this.RainSplash = null;
        this.overlayDone = false;
        this.haveRoof = false;
        this.burntOut = false;
        this.trapPositionX = this.trapPositionY = this.trapPositionZ = -1;
        this.haveElectricity = false;
        this.haveSheetRope = false;
        if (this.erosion != null) {
            this.erosion.reset();
        }

        if (this.OcclusionDataCache != null) {
            this.OcclusionDataCache.Reset();
        }

        this.roofHideBuilding = null;
        this.bHasFlies = false;
        isoGridSquareCache.add(this);
    }

    private static boolean validateUser(String string1, String string0) throws MalformedURLException, IOException {
        URL url = new URL("http://www.projectzomboid.com/scripts/auth.php?username=" + string1 + "&password=" + string0);
        URLConnection uRLConnection = url.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uRLConnection.getInputStream()));

        String string2;
        while ((string2 = bufferedReader.readLine()) != null) {
            if (string2.contains("success")) {
                return true;
            }
        }

        return false;
    }

    public float DistTo(int _x, int _y) {
        return IsoUtils.DistanceManhatten(_x + 0.5F, _y + 0.5F, this.x, this.y);
    }

    public float DistTo(IsoGridSquare sq) {
        return IsoUtils.DistanceManhatten(this.x + 0.5F, this.y + 0.5F, sq.x + 0.5F, sq.y + 0.5F);
    }

    public float DistToProper(IsoGridSquare sq) {
        return IsoUtils.DistanceTo(this.x + 0.5F, this.y + 0.5F, sq.x + 0.5F, sq.y + 0.5F);
    }

    public float DistTo(IsoMovingObject other) {
        return IsoUtils.DistanceManhatten(this.x + 0.5F, this.y + 0.5F, other.getX(), other.getY());
    }

    public float DistToProper(IsoMovingObject other) {
        return IsoUtils.DistanceTo(this.x + 0.5F, this.y + 0.5F, other.getX(), other.getY());
    }

    public boolean isSafeToSpawn() {
        choices.clear();
        this.isSafeToSpawn(this, 0);
        if (choices.size() > 7) {
            choices.clear();
            return true;
        } else {
            choices.clear();
            return false;
        }
    }

    public void isSafeToSpawn(IsoGridSquare sq, int depth) {
        if (depth <= 5) {
            choices.add(sq);
            if (sq.n != null && !choices.contains(sq.n)) {
                this.isSafeToSpawn(sq.n, depth + 1);
            }

            if (sq.s != null && !choices.contains(sq.s)) {
                this.isSafeToSpawn(sq.s, depth + 1);
            }

            if (sq.e != null && !choices.contains(sq.e)) {
                this.isSafeToSpawn(sq.e, depth + 1);
            }

            if (sq.w != null && !choices.contains(sq.w)) {
                this.isSafeToSpawn(sq.w, depth + 1);
            }
        }
    }

    public static boolean auth(String string0, char[] chars) {
        if (string0.length() > 64) {
            return false;
        } else {
            String string1 = chars.toString();
            if (string1.length() > 64) {
                return false;
            } else {
                try {
                    return validateUser(string0, string1);
                } catch (MalformedURLException malformedURLException) {
                    Logger.getLogger(IsoGridSquare.class.getName()).log(Level.SEVERE, null, malformedURLException);
                } catch (IOException iOException) {
                    Logger.getLogger(IsoGridSquare.class.getName()).log(Level.SEVERE, null, iOException);
                }

                return false;
            }
        }
    }

    private void renderAttachedSpritesWithNoWallLighting(IsoObject object, ColorInfo colorInfo) {
        if (object.AttachedAnimSprite != null && !object.AttachedAnimSprite.isEmpty()) {
            boolean boolean0 = false;

            for (int int0 = 0; int0 < object.AttachedAnimSprite.size(); int0++) {
                IsoSpriteInstance spriteInstance0 = object.AttachedAnimSprite.get(int0);
                if (spriteInstance0.parentSprite != null && spriteInstance0.parentSprite.Properties.Is(IsoFlagType.NoWallLighting)) {
                    boolean0 = true;
                    break;
                }
            }

            if (boolean0) {
                defColorInfo.r = colorInfo.r;
                defColorInfo.g = colorInfo.g;
                defColorInfo.b = colorInfo.b;
                float float0 = defColorInfo.a;
                if (CircleStencil) {
                }

                for (int int1 = 0; int1 < object.AttachedAnimSprite.size(); int1++) {
                    IsoSpriteInstance spriteInstance1 = object.AttachedAnimSprite.get(int1);
                    if (spriteInstance1.parentSprite != null && spriteInstance1.parentSprite.Properties.Is(IsoFlagType.NoWallLighting)) {
                        defColorInfo.a = spriteInstance1.alpha;
                        spriteInstance1.render(
                            object,
                            this.x,
                            this.y,
                            this.z,
                            object.dir,
                            object.offsetX,
                            object.offsetY + object.getRenderYOffset() * Core.TileScale,
                            defColorInfo
                        );
                        spriteInstance1.update();
                    }
                }

                defColorInfo.r = 1.0F;
                defColorInfo.g = 1.0F;
                defColorInfo.b = 1.0F;
                defColorInfo.a = float0;
            }
        }
    }

    public void DoCutawayShader(
        IsoObject obj,
        IsoDirections dir,
        boolean cutawaySW,
        boolean cutawayNW,
        boolean cutawayNE,
        boolean bHasDoorN,
        boolean bHasDoorW,
        boolean bHasWindowN,
        boolean bHasWindowW,
        WallShaperWhole texdModifier
    ) {
        Texture texture0 = Texture.getSharedTexture("media/wallcutaways.png");
        if (texture0 != null && texture0.getID() != -1) {
            boolean boolean0 = obj.sprite.getProperties().Is(IsoFlagType.NoWallLighting);
            int int0 = IsoCamera.frameState.playerIndex;
            ColorInfo colorInfo = this.lightInfo[int0];
            int int1 = 2 / Core.TileScale;

            try {
                Texture texture1 = obj.getCurrentFrameTex();
                float float0 = 0.0F;
                float float1 = obj.getCurrentFrameTex().getOffsetY();
                int int2 = 0;
                int int3 = 226 - texture1.getHeight() * int1;
                if (dir != IsoDirections.NW) {
                    int2 = 66 - texture1.getWidth() * int1;
                }

                if (obj.sprite.getProperties().Is(IsoFlagType.WallSE)) {
                    int2 = 6 - texture1.getWidth() * int1;
                    int3 = 196 - texture1.getHeight() * int1;
                }

                if (obj.sprite.name.contains("fencing_01_11")) {
                    float0 = 1.0F;
                } else if (obj.sprite.name.contains("carpentry_02_80")) {
                    float0 = 1.0F;
                } else if (obj.sprite.name.contains("spiffos_01_71")) {
                    float0 = -24.0F;
                } else if (obj.sprite.name.contains("location_community_medical")) {
                    String string0 = obj.sprite.name.replaceAll("(.*)_", "");
                    int int4 = Integer.parseInt(string0);
                    switch (int4) {
                        case 45:
                        case 46:
                        case 47:
                        case 147:
                        case 148:
                        case 149:
                            float0 = -3.0F;
                    }
                } else if (obj.sprite.name.contains("walls_exterior_roofs")) {
                    String string1 = obj.sprite.name.replaceAll("(.*)_", "");
                    int int5 = Integer.parseInt(string1);
                    if (int5 == 4) {
                        float0 = -60.0F;
                    } else if (int5 == 17) {
                        float0 = -46.0F;
                    } else if (int5 == 28 && !obj.sprite.name.contains("03")) {
                        float0 = -60.0F;
                    } else if (int5 == 41) {
                        float0 = -46.0F;
                    }
                }

                IsoGridSquare.CircleStencilShader circleStencilShader = IsoGridSquare.CircleStencilShader.instance;
                if (dir == IsoDirections.N || dir == IsoDirections.NW) {
                    short short0 = 700;
                    short short1 = 1084;
                    if (cutawayNW) {
                        short1 = 1212;
                        if (!cutawayNE) {
                            short0 = 444;
                        }
                    } else if (!cutawayNE) {
                        short0 = 828;
                    } else {
                        short0 = 956;
                    }

                    short short2 = 0;
                    if (bHasDoorN) {
                        short2 = 904;
                        if (obj.sprite.name.contains("garage") || obj.sprite.name.contains("industry_trucks")) {
                            int int6 = obj.sprite.tileSheetIndex;
                            if (int6 % 8 == 5) {
                                short2 = 1356;
                            } else if (int6 % 8 == 4) {
                                short2 = 1582;
                            } else if (int6 % 8 == 3) {
                                short2 = 1130;
                            }
                        }

                        if (obj.sprite.name.contains("community_church")) {
                            int int7 = obj.sprite.tileSheetIndex;
                            if (int7 == 19) {
                                short2 = 1356;
                            } else if (int7 == 18) {
                                short2 = 1130;
                            }
                        }
                    } else if (bHasWindowN) {
                        short2 = 226;
                        if (obj.sprite.name.contains("trailer")) {
                            int int8 = obj.sprite.tileSheetIndex;
                            if (int8 == 14 || int8 == 38) {
                                short2 = 678;
                            } else if (int8 == 15 || int8 == 39) {
                                short2 = 452;
                            }
                        }

                        if (obj.sprite.name.contains("sunstarmotel")) {
                            int int9 = obj.sprite.tileSheetIndex;
                            if (int9 == 22 || int9 == 18) {
                                short2 = 678;
                            } else if (int9 == 23 || int9 == 19) {
                                short2 = 452;
                            }
                        }
                    }

                    colu = this.getVertLight(0, int0);
                    coll = this.getVertLight(1, int0);
                    colu2 = this.getVertLight(4, int0);
                    coll2 = this.getVertLight(5, int0);
                    if (Core.bDebug && DebugOptions.instance.DebugDraw_SkipWorldShading.getValue()) {
                        coll2 = -1;
                        colu2 = -1;
                        coll = -1;
                        colu = -1;
                        colorInfo = defColorInfo;
                    }

                    if (obj.sprite.getProperties().Is(IsoFlagType.WallSE)) {
                        SpriteRenderer.instance.setCutawayTexture(texture0, short1 + (int)float0, short2 + (int)float1, 6 - int2, 196 - int3);
                    } else {
                        SpriteRenderer.instance.setCutawayTexture(texture0, short0 + (int)float0, short2 + (int)float1, 66 - int2, 226 - int3);
                    }

                    if (dir == IsoDirections.N) {
                        SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.All);
                    } else {
                        SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.RightOnly);
                    }

                    texdModifier.col[0] = colu2;
                    texdModifier.col[1] = coll2;
                    texdModifier.col[2] = coll;
                    texdModifier.col[3] = colu;
                    obj.renderWallTileOnly(this.x, this.y, this.z, boolean0 ? colorInfo : defColorInfo, circleStencilShader, texdModifier);
                }

                if (dir == IsoDirections.W || dir == IsoDirections.NW) {
                    short short3 = 512;
                    short short4 = 1084;
                    if (cutawaySW) {
                        if (!cutawayNW) {
                            short3 = 768;
                            short4 = 1212;
                        }
                    } else if (!cutawayNW) {
                        short3 = 896;
                        short4 = 1212;
                    } else {
                        short3 = 256;
                    }

                    short short5 = 0;
                    if (bHasDoorW) {
                        short5 = 904;
                        if (obj.sprite.name.contains("garage") || obj.sprite.name.contains("industry_trucks")) {
                            int int10 = obj.sprite.tileSheetIndex;
                            if (int10 % 8 == 0) {
                                short5 = 1356;
                            } else if (int10 % 8 == 1) {
                                short5 = 1582;
                            } else if (int10 % 8 == 2) {
                                short5 = 1130;
                            }
                        }

                        if (obj.sprite.name.contains("community_church")) {
                            int int11 = obj.sprite.tileSheetIndex;
                            if (int11 == 16) {
                                short5 = 1356;
                            } else if (int11 == 17) {
                                short5 = 1130;
                            }
                        }
                    } else if (bHasWindowW) {
                        short5 = 226;
                        if (obj.sprite.name.contains("trailer")) {
                            int int12 = obj.sprite.tileSheetIndex;
                            if (int12 == 13 || int12 == 37) {
                                short5 = 678;
                            } else if (int12 == 12 || int12 == 36) {
                                short5 = 452;
                            }
                        }

                        if (obj.sprite.name.contains("sunstarmotel")) {
                            int int13 = obj.sprite.tileSheetIndex;
                            if (int13 == 17) {
                                short5 = 678;
                            } else if (int13 == 16) {
                                short5 = 452;
                            }
                        }
                    }

                    colu = this.getVertLight(0, int0);
                    coll = this.getVertLight(3, int0);
                    colu2 = this.getVertLight(4, int0);
                    coll2 = this.getVertLight(7, int0);
                    if (Core.bDebug && DebugOptions.instance.DebugDraw_SkipWorldShading.getValue()) {
                        coll2 = -1;
                        colu2 = -1;
                        coll = -1;
                        colu = -1;
                        colorInfo = defColorInfo;
                    }

                    if (obj.sprite.getProperties().Is(IsoFlagType.WallSE)) {
                        SpriteRenderer.instance.setCutawayTexture(texture0, short4 + (int)float0, short5 + (int)float1, 6 - int2, 196 - int3);
                    } else {
                        SpriteRenderer.instance.setCutawayTexture(texture0, short3 + (int)float0, short5 + (int)float1, 66 - int2, 226 - int3);
                    }

                    if (dir == IsoDirections.W) {
                        SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.All);
                    } else {
                        SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.LeftOnly);
                    }

                    texdModifier.col[0] = coll2;
                    texdModifier.col[1] = colu2;
                    texdModifier.col[2] = colu;
                    texdModifier.col[3] = coll;
                    obj.renderWallTileOnly(this.x, this.y, this.z, boolean0 ? colorInfo : defColorInfo, circleStencilShader, texdModifier);
                }
            } finally {
                SpriteRenderer.instance.setExtraWallShaderParams(null);
                SpriteRenderer.instance.clearCutawayTexture();
                SpriteRenderer.instance.clearUseVertColorsArray();
            }

            obj.renderAttachedAndOverlaySprites(this.x, this.y, this.z, boolean0 ? colorInfo : defColorInfo, false, !boolean0, null, texdModifier);
        }
    }

    public void DoCutawayShaderSprite(IsoSprite sprite, IsoDirections dir, boolean cutawaySW, boolean cutawayNW, boolean cutawayNE) {
        IsoGridSquare.CircleStencilShader circleStencilShader = IsoGridSquare.CircleStencilShader.instance;
        WallShaperWhole wallShaperWhole = WallShaperWhole.instance;
        int int0 = IsoCamera.frameState.playerIndex;
        Texture texture0 = Texture.getSharedTexture("media/wallcutaways.png");
        if (texture0 != null && texture0.getID() != -1) {
            int int1 = 2 / Core.TileScale;

            try {
                Texture texture1 = sprite.CurrentAnim.Frames.get((int)sprite.def.Frame).getTexture(dir);
                float float0 = 0.0F;
                float float1 = texture1.getOffsetY();
                int int2 = 0;
                int int3 = 226 - texture1.getHeight() * int1;
                if (dir != IsoDirections.NW) {
                    int2 = 66 - texture1.getWidth() * int1;
                }

                if (sprite.getProperties().Is(IsoFlagType.WallSE)) {
                    int2 = 6 - texture1.getWidth() * int1;
                    int3 = 196 - texture1.getHeight() * int1;
                }

                if (sprite.name.contains("fencing_01_11")) {
                    float0 = 1.0F;
                } else if (sprite.name.contains("carpentry_02_80")) {
                    float0 = 1.0F;
                } else if (sprite.name.contains("spiffos_01_71")) {
                    float0 = -24.0F;
                } else if (sprite.name.contains("location_community_medical")) {
                    String string0 = sprite.name.replaceAll("(.*)_", "");
                    int int4 = Integer.parseInt(string0);
                    switch (int4) {
                        case 45:
                        case 46:
                        case 47:
                        case 147:
                        case 148:
                        case 149:
                            float0 = -3.0F;
                    }
                } else if (sprite.name.contains("walls_exterior_roofs")) {
                    String string1 = sprite.name.replaceAll("(.*)_", "");
                    int int5 = Integer.parseInt(string1);
                    if (int5 == 4) {
                        float0 = -60.0F;
                    } else if (int5 == 17) {
                        float0 = -46.0F;
                    } else if (int5 == 28 && !sprite.name.contains("03")) {
                        float0 = -60.0F;
                    } else if (int5 == 41) {
                        float0 = -46.0F;
                    }
                }

                if (dir == IsoDirections.N || dir == IsoDirections.NW) {
                    short short0 = 700;
                    short short1 = 1084;
                    if (cutawayNW) {
                        short1 = 1212;
                        if (!cutawayNE) {
                            short0 = 444;
                        }
                    } else if (!cutawayNE) {
                        short0 = 828;
                    } else {
                        short0 = 956;
                    }

                    colu = this.getVertLight(0, int0);
                    coll = this.getVertLight(1, int0);
                    colu2 = this.getVertLight(4, int0);
                    coll2 = this.getVertLight(5, int0);
                    if (sprite.getProperties().Is(IsoFlagType.WallSE)) {
                        SpriteRenderer.instance.setCutawayTexture(texture0, short1 + (int)float0, 0 + (int)float1, 6 - int2, 196 - int3);
                    } else {
                        SpriteRenderer.instance.setCutawayTexture(texture0, short0 + (int)float0, 0 + (int)float1, 66 - int2, 226 - int3);
                    }

                    if (dir == IsoDirections.N) {
                        SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.All);
                    } else {
                        SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.RightOnly);
                    }

                    wallShaperWhole.col[0] = colu2;
                    wallShaperWhole.col[1] = coll2;
                    wallShaperWhole.col[2] = coll;
                    wallShaperWhole.col[3] = colu;
                    IndieGL.bindShader(
                        circleStencilShader,
                        sprite,
                        dir,
                        wallShaperWhole,
                        (spritex, directions, wallShaperWholex) -> spritex.render(
                            null, this.x, this.y, this.z, directions, WeatherFxMask.offsetX, WeatherFxMask.offsetY, defColorInfo, false, wallShaperWholex
                        )
                    );
                }

                if (dir == IsoDirections.W || dir == IsoDirections.NW) {
                    short short2 = 512;
                    short short3 = 1084;
                    if (cutawaySW) {
                        if (!cutawayNW) {
                            short2 = 768;
                            short3 = 1212;
                        }
                    } else if (!cutawayNW) {
                        short2 = 896;
                        short3 = 1212;
                    } else {
                        short2 = 256;
                    }

                    colu = this.getVertLight(0, int0);
                    coll = this.getVertLight(3, int0);
                    colu2 = this.getVertLight(4, int0);
                    coll2 = this.getVertLight(7, int0);
                    if (sprite.getProperties().Is(IsoFlagType.WallSE)) {
                        SpriteRenderer.instance.setCutawayTexture(texture0, short3 + (int)float0, 0 + (int)float1, 6 - int2, 196 - int3);
                    } else {
                        SpriteRenderer.instance.setCutawayTexture(texture0, short2 + (int)float0, 0 + (int)float1, 66 - int2, 226 - int3);
                    }

                    if (dir == IsoDirections.W) {
                        SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.All);
                    } else {
                        SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.LeftOnly);
                    }

                    wallShaperWhole.col[0] = coll2;
                    wallShaperWhole.col[1] = colu2;
                    wallShaperWhole.col[2] = colu;
                    wallShaperWhole.col[3] = coll;
                    IndieGL.bindShader(
                        circleStencilShader,
                        sprite,
                        dir,
                        wallShaperWhole,
                        (spritex, directions, wallShaperWholex) -> spritex.render(
                            null, this.x, this.y, this.z, directions, WeatherFxMask.offsetX, WeatherFxMask.offsetY, defColorInfo, false, wallShaperWholex
                        )
                    );
                }
            } finally {
                SpriteRenderer.instance.setExtraWallShaderParams(null);
                SpriteRenderer.instance.clearCutawayTexture();
                SpriteRenderer.instance.clearUseVertColorsArray();
            }
        }
    }

    public int DoWallLightingNW(
        IsoObject obj,
        int stenciled,
        boolean cutawaySW,
        boolean cutawayNW,
        boolean cutawayNE,
        boolean bHasDoorN,
        boolean bHasDoorW,
        boolean bHasWindowN,
        boolean bHasWindowW,
        Shader wallRenderShader
    ) {
        if (!DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.NW.getValue()) {
            return stenciled;
        } else {
            boolean boolean0 = cutawaySW || cutawayNW || cutawayNE;
            IsoDirections directions = IsoDirections.NW;
            int int0 = IsoCamera.frameState.playerIndex;
            colu = this.getVertLight(0, int0);
            coll = this.getVertLight(3, int0);
            colr = this.getVertLight(1, int0);
            colu2 = this.getVertLight(4, int0);
            coll2 = this.getVertLight(7, int0);
            colr2 = this.getVertLight(5, int0);
            if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.LightingDebug.getValue()) {
                colu = -65536;
                coll = -16711936;
                colr = -16711681;
                colu2 = -16776961;
                coll2 = -65281;
                colr2 = -256;
            }

            boolean boolean1 = CircleStencil;
            if (this.z != (int)IsoCamera.CamCharacter.z) {
                boolean1 = false;
            }

            boolean boolean2 = obj.sprite.getType() == IsoObjectType.doorFrN || obj.sprite.getType() == IsoObjectType.doorN;
            boolean boolean3 = obj.sprite.getType() == IsoObjectType.doorFrW || obj.sprite.getType() == IsoObjectType.doorW;
            boolean boolean4 = false;
            boolean boolean5 = false;
            boolean boolean6 = (boolean2 || boolean4 || boolean3 || boolean4) && boolean0 || obj.sprite.getProperties().Is(IsoFlagType.NoWallLighting);
            boolean1 = this.calculateWallAlphaAndCircleStencilCorner(
                obj, cutawaySW, cutawayNW, cutawayNE, bHasDoorN, bHasDoorW, bHasWindowN, bHasWindowW, boolean1, int0, boolean2, boolean3, boolean4, boolean5
            );
            if (USE_WALL_SHADER && boolean1 && boolean0) {
                this.DoCutawayShader(obj, directions, cutawaySW, cutawayNW, cutawayNE, bHasDoorN, bHasDoorW, bHasWindowN, bHasWindowW, WallShaperWhole.instance);
                bWallCutawayN = true;
                bWallCutawayW = true;
                return stenciled;
            } else {
                WallShaperWhole.instance.col[0] = colu2;
                WallShaperWhole.instance.col[1] = colr2;
                WallShaperWhole.instance.col[2] = colr;
                WallShaperWhole.instance.col[3] = colu;
                WallShaperN wallShaperN = WallShaperN.instance;
                wallShaperN.col[0] = colu2;
                wallShaperN.col[1] = colr2;
                wallShaperN.col[2] = colr;
                wallShaperN.col[3] = colu;
                stenciled = this.performDrawWall(obj, stenciled, int0, boolean6, wallShaperN, wallRenderShader);
                WallShaperWhole.instance.col[0] = coll2;
                WallShaperWhole.instance.col[1] = colu2;
                WallShaperWhole.instance.col[2] = colu;
                WallShaperWhole.instance.col[3] = coll;
                WallShaperW wallShaperW = WallShaperW.instance;
                wallShaperW.col[0] = coll2;
                wallShaperW.col[1] = colu2;
                wallShaperW.col[2] = colu;
                wallShaperW.col[3] = coll;
                return this.performDrawWall(obj, stenciled, int0, boolean6, wallShaperW, wallRenderShader);
            }
        }
    }

    public int DoWallLightingN(
        IsoObject obj, int stenciled, boolean cutawayNW, boolean cutawayNE, boolean bHasDoorN, boolean bHasWindowN, Shader wallRenderShader
    ) {
        if (!DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.N.getValue()) {
            return stenciled;
        } else {
            boolean boolean0 = !bHasDoorN;
            boolean boolean1 = !bHasWindowN;
            IsoObjectType objectType0 = IsoObjectType.doorFrN;
            IsoObjectType objectType1 = IsoObjectType.doorN;
            boolean boolean2 = cutawayNW || cutawayNE;
            IsoFlagType flagType0 = IsoFlagType.transparentN;
            IsoFlagType flagType1 = IsoFlagType.WindowN;
            IsoFlagType flagType2 = IsoFlagType.HoppableN;
            IsoDirections directions = IsoDirections.N;
            boolean boolean3 = CircleStencil;
            int int0 = IsoCamera.frameState.playerIndex;
            colu = this.getVertLight(0, int0);
            coll = this.getVertLight(1, int0);
            colu2 = this.getVertLight(4, int0);
            coll2 = this.getVertLight(5, int0);
            if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.LightingDebug.getValue()) {
                colu = -65536;
                coll = -16711936;
                colu2 = -16776961;
                coll2 = -65281;
            }

            WallShaperWhole wallShaperWhole = WallShaperWhole.instance;
            wallShaperWhole.col[0] = colu2;
            wallShaperWhole.col[1] = coll2;
            wallShaperWhole.col[2] = coll;
            wallShaperWhole.col[3] = colu;
            return this.performDrawWallSegmentSingle(
                obj,
                stenciled,
                false,
                cutawayNW,
                false,
                false,
                cutawayNE,
                bHasDoorN,
                bHasWindowN,
                boolean0,
                boolean1,
                objectType0,
                objectType1,
                boolean2,
                flagType0,
                flagType1,
                flagType2,
                directions,
                boolean3,
                wallShaperWhole,
                wallRenderShader
            );
        }
    }

    public int DoWallLightingW(
        IsoObject obj, int stenciled, boolean cutawaySW, boolean cutawayNW, boolean bHasDoorW, boolean bHasWindowW, Shader wallRenderShader
    ) {
        if (!DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.W.getValue()) {
            return stenciled;
        } else {
            boolean boolean0 = !bHasDoorW;
            boolean boolean1 = !bHasWindowW;
            IsoObjectType objectType0 = IsoObjectType.doorFrW;
            IsoObjectType objectType1 = IsoObjectType.doorW;
            boolean boolean2 = cutawaySW || cutawayNW;
            IsoFlagType flagType0 = IsoFlagType.transparentW;
            IsoFlagType flagType1 = IsoFlagType.WindowW;
            IsoFlagType flagType2 = IsoFlagType.HoppableW;
            IsoDirections directions = IsoDirections.W;
            boolean boolean3 = CircleStencil;
            int int0 = IsoCamera.frameState.playerIndex;
            colu = this.getVertLight(0, int0);
            coll = this.getVertLight(3, int0);
            colu2 = this.getVertLight(4, int0);
            coll2 = this.getVertLight(7, int0);
            if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.LightingDebug.getValue()) {
                colu = -65536;
                coll = -16711936;
                colu2 = -16776961;
                coll2 = -65281;
            }

            WallShaperWhole wallShaperWhole = WallShaperWhole.instance;
            wallShaperWhole.col[0] = coll2;
            wallShaperWhole.col[1] = colu2;
            wallShaperWhole.col[2] = colu;
            wallShaperWhole.col[3] = coll;
            return this.performDrawWallSegmentSingle(
                obj,
                stenciled,
                cutawaySW,
                cutawayNW,
                bHasDoorW,
                bHasWindowW,
                false,
                false,
                false,
                boolean0,
                boolean1,
                objectType0,
                objectType1,
                boolean2,
                flagType0,
                flagType1,
                flagType2,
                directions,
                boolean3,
                wallShaperWhole,
                wallRenderShader
            );
        }
    }

    private int performDrawWallSegmentSingle(
        IsoObject object,
        int int1,
        boolean boolean7,
        boolean boolean8,
        boolean boolean11,
        boolean boolean13,
        boolean boolean9,
        boolean boolean10,
        boolean boolean12,
        boolean boolean5,
        boolean boolean6,
        IsoObjectType objectType1,
        IsoObjectType objectType0,
        boolean boolean4,
        IsoFlagType flagType0,
        IsoFlagType flagType1,
        IsoFlagType flagType2,
        IsoDirections directions,
        boolean boolean0,
        WallShaperWhole wallShaperWhole,
        Shader shader
    ) {
        int int0 = IsoCamera.frameState.playerIndex;
        if (this.z != (int)IsoCamera.CamCharacter.z) {
            boolean0 = false;
        }

        boolean boolean1 = object.sprite.getType() == objectType1 || object.sprite.getType() == objectType0;
        boolean boolean2 = object instanceof IsoWindow;
        boolean boolean3 = (boolean1 || boolean2) && boolean4 || object.sprite.getProperties().Is(IsoFlagType.NoWallLighting);
        boolean0 = this.calculateWallAlphaAndCircleStencilEdge(
            object, boolean5, boolean6, boolean4, flagType0, flagType1, flagType2, boolean0, int0, boolean1, boolean2
        );
        if (USE_WALL_SHADER && boolean0 && boolean4) {
            this.DoCutawayShader(object, directions, boolean7, boolean8, boolean9, boolean10, boolean11, boolean12, boolean13, wallShaperWhole);
            bWallCutawayN = bWallCutawayN | directions == IsoDirections.N;
            bWallCutawayW = bWallCutawayW | directions == IsoDirections.W;
            return int1;
        } else {
            return this.performDrawWall(object, int1, int0, boolean3, wallShaperWhole, shader);
        }
    }

    private int performDrawWallOnly(IsoObject object, int int0, int int1, boolean boolean0, Consumer<TextureDraw> consumer, Shader shader) {
        if (int0 == 0 && !boolean0) {
            int0 = this.getCell().getStencilValue(this.x, this.y, this.z);
        }

        IndieGL.enableAlphaTest();
        IndieGL.glAlphaFunc(516, 0.0F);
        IndieGL.glStencilFunc(519, int0, 127);
        if (!boolean0) {
            IndieGL.glStencilOp(7680, 7680, 7681);
        }

        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.Render.getValue()) {
            object.renderWallTile(this.x, this.y, this.z, boolean0 ? lightInfoTemp : defColorInfo, true, !boolean0, shader, consumer);
        }

        object.setAlpha(int1, 1.0F);
        if (boolean0) {
            IndieGL.glStencilFunc(519, 1, 255);
            IndieGL.glStencilOp(7680, 7680, 7680);
            return int0;
        } else {
            this.getCell().setStencilValue(this.x, this.y, this.z, int0);
            return int0 + 1;
        }
    }

    private int performDrawWall(IsoObject object, int int1, int int0, boolean boolean0, Consumer<TextureDraw> consumer, Shader shader) {
        lightInfoTemp.set(this.lightInfo[int0]);
        if (Core.bDebug && DebugOptions.instance.DebugDraw_SkipWorldShading.getValue()) {
            object.render(this.x, this.y, this.z, defColorInfo, true, !boolean0, null);
            return int1;
        } else {
            int int2 = this.performDrawWallOnly(object, int1, int0, boolean0, consumer, shader);
            if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.AttachedSprites.getValue()) {
                this.renderAttachedSpritesWithNoWallLighting(object, lightInfoTemp);
            }

            return int2;
        }
    }

    private void calculateWallAlphaCommon(IsoObject object, boolean boolean2, boolean boolean3, boolean boolean4, int int0, boolean boolean0, boolean boolean1) {
        if (boolean0 || boolean1) {
            if (boolean2) {
                object.setAlpha(int0, 0.4F);
                object.setTargetAlpha(int0, 0.4F);
                lightInfoTemp.r = Math.max(0.3F, lightInfoTemp.r);
                lightInfoTemp.g = Math.max(0.3F, lightInfoTemp.g);
                lightInfoTemp.b = Math.max(0.3F, lightInfoTemp.b);
                if (boolean0 && !boolean3) {
                    object.setAlpha(int0, 0.0F);
                    object.setTargetAlpha(int0, 0.0F);
                }

                if (boolean1 && !boolean4) {
                    object.setAlpha(int0, 0.0F);
                    object.setTargetAlpha(int0, 0.0F);
                }
            }
        }
    }

    private boolean calculateWallAlphaAndCircleStencilEdge(
        IsoObject object,
        boolean boolean5,
        boolean boolean4,
        boolean boolean3,
        IsoFlagType flagType1,
        IsoFlagType flagType0,
        IsoFlagType var7,
        boolean boolean2,
        int int0,
        boolean boolean0,
        boolean boolean1
    ) {
        if (boolean0 || boolean1) {
            if (!object.sprite.getProperties().Is("GarageDoor")) {
                boolean2 = false;
            }

            this.calculateWallAlphaCommon(object, boolean3, !boolean5, !boolean4, int0, boolean0, boolean1);
        }

        if (boolean2
            && object.sprite.getType() == IsoObjectType.wall
            && object.sprite.getProperties().Is(flagType1)
            && !object.getSprite().getProperties().Is(IsoFlagType.exterior)
            && !object.sprite.getProperties().Is(flagType0)) {
            boolean2 = false;
        }

        return boolean2;
    }

    private boolean calculateWallAlphaAndCircleStencilCorner(
        IsoObject object,
        boolean boolean10,
        boolean boolean4,
        boolean boolean5,
        boolean boolean0,
        boolean boolean6,
        boolean boolean1,
        boolean boolean7,
        boolean boolean11,
        int int0,
        boolean boolean2,
        boolean boolean8,
        boolean boolean3,
        boolean boolean9
    ) {
        this.calculateWallAlphaCommon(object, boolean4 || boolean5, boolean0, boolean1, int0, boolean2, boolean3);
        this.calculateWallAlphaCommon(object, boolean4 || boolean10, boolean6, boolean7, int0, boolean8, boolean9);
        boolean11 = boolean11 && !boolean2 && !boolean3;
        if (boolean11
            && object.sprite.getType() == IsoObjectType.wall
            && (object.sprite.getProperties().Is(IsoFlagType.transparentN) || object.sprite.getProperties().Is(IsoFlagType.transparentW))
            && !object.getSprite().getProperties().Is(IsoFlagType.exterior)
            && !object.sprite.getProperties().Is(IsoFlagType.WindowN)
            && !object.sprite.getProperties().Is(IsoFlagType.WindowW)) {
            boolean11 = false;
        }

        return boolean11;
    }

    public KahluaTable getLuaMovingObjectList() {
        KahluaTable tablex = LuaManager.platform.newTable();
        LuaManager.env.rawset("Objects", tablex);

        for (int int0 = 0; int0 < this.MovingObjects.size(); int0++) {
            tablex.rawset(int0 + 1, this.MovingObjects.get(int0));
        }

        return tablex;
    }

    public boolean Is(IsoFlagType flag) {
        return this.Properties.Is(flag);
    }

    public boolean Is(String flag) {
        return this.Properties.Is(flag);
    }

    public boolean Has(IsoObjectType type) {
        return this.hasTypes.isSet(type);
    }

    public void DeleteTileObject(IsoObject obj) {
        this.Objects.remove(obj);
        this.RecalcAllWithNeighbours(true);
    }

    public KahluaTable getLuaTileObjectList() {
        KahluaTable tablex = LuaManager.platform.newTable();
        LuaManager.env.rawset("Objects", tablex);

        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            tablex.rawset(int0 + 1, this.Objects.get(int0));
        }

        return tablex;
    }

    boolean HasDoor(boolean boolean0) {
        for (int int0 = 0; int0 < this.SpecialObjects.size(); int0++) {
            if (this.SpecialObjects.get(int0) instanceof IsoDoor && ((IsoDoor)this.SpecialObjects.get(int0)).north == boolean0) {
                return true;
            }

            if (this.SpecialObjects.get(int0) instanceof IsoThumpable
                && ((IsoThumpable)this.SpecialObjects.get(int0)).isDoor
                && ((IsoThumpable)this.SpecialObjects.get(int0)).north == boolean0) {
                return true;
            }
        }

        return false;
    }

    public boolean HasStairs() {
        return this.HasStairsNorth() || this.HasStairsWest();
    }

    public boolean HasStairsNorth() {
        return this.Has(IsoObjectType.stairsTN) || this.Has(IsoObjectType.stairsMN) || this.Has(IsoObjectType.stairsBN);
    }

    public boolean HasStairsWest() {
        return this.Has(IsoObjectType.stairsTW) || this.Has(IsoObjectType.stairsMW) || this.Has(IsoObjectType.stairsBW);
    }

    public boolean HasStairsBelow() {
        if (this.z == 0) {
            return false;
        } else {
            IsoGridSquare square1 = this.getCell().getGridSquare(this.x, this.y, this.z - 1);
            return square1 != null && square1.HasStairs();
        }
    }

    public boolean HasElevatedFloor() {
        return this.Has(IsoObjectType.stairsTN) || this.Has(IsoObjectType.stairsMN) || this.Has(IsoObjectType.stairsTW) || this.Has(IsoObjectType.stairsMW);
    }

    public boolean isSameStaircase(int _x, int _y, int _z) {
        if (_z != this.getZ()) {
            return false;
        } else {
            int int0 = this.getX();
            int int1 = this.getY();
            int int2 = int0;
            int int3 = int1;
            if (this.Has(IsoObjectType.stairsTN)) {
                int3 = int1 + 2;
            } else if (this.Has(IsoObjectType.stairsMN)) {
                int1--;
                int3++;
            } else if (this.Has(IsoObjectType.stairsBN)) {
                int1 -= 2;
            } else if (this.Has(IsoObjectType.stairsTW)) {
                int2 = int0 + 2;
            } else if (this.Has(IsoObjectType.stairsMW)) {
                int0--;
                int2++;
            } else {
                if (!this.Has(IsoObjectType.stairsBW)) {
                    return false;
                }

                int0 -= 2;
            }

            if (_x >= int0 && _y >= int1 && _x <= int2 && _y <= int3) {
                IsoGridSquare square = this.getCell().getGridSquare(_x, _y, _z);
                return square != null && square.HasStairs();
            } else {
                return false;
            }
        }
    }

    public boolean HasSlopedRoof() {
        return this.HasSlopedRoofWest() || this.HasSlopedRoofNorth();
    }

    public boolean HasSlopedRoofWest() {
        return this.Has(IsoObjectType.WestRoofB) || this.Has(IsoObjectType.WestRoofM) || this.Has(IsoObjectType.WestRoofT);
    }

    public boolean HasSlopedRoofNorth() {
        return this.Has(IsoObjectType.WestRoofB) || this.Has(IsoObjectType.WestRoofM) || this.Has(IsoObjectType.WestRoofT);
    }

    public boolean HasTree() {
        return this.hasTree;
    }

    public IsoTree getTree() {
        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            IsoTree tree = Type.tryCastTo(this.Objects.get(int0), IsoTree.class);
            if (tree != null) {
                return tree;
            }
        }

        return null;
    }

    private void fudgeShadowsToAlpha(IsoObject object, Color color) {
        float float0 = 1.0F - object.getAlpha();
        if (color.r < float0) {
            color.r = float0;
        }

        if (color.g < float0) {
            color.g = float0;
        }

        if (color.b < float0) {
            color.b = float0;
        }
    }

    public boolean shouldSave() {
        return !this.Objects.isEmpty();
    }

    public void save(ByteBuffer output, ObjectOutputStream outputObj) throws IOException {
        this.save(output, outputObj, false);
    }

    public void save(ByteBuffer output, ObjectOutputStream outputObj, boolean IS_DEBUG_SAVE) throws IOException {
        this.getErosionData().save(output);
        BitHeaderWrite bitHeaderWrite0 = BitHeader.allocWrite(BitHeader.HeaderSize.Byte, output);
        int int0 = this.Objects.size();
        if (this.Objects.size() > 0) {
            bitHeaderWrite0.addFlags(1);
            if (int0 == 2) {
                bitHeaderWrite0.addFlags(2);
            } else if (int0 == 3) {
                bitHeaderWrite0.addFlags(4);
            } else if (int0 >= 4) {
                bitHeaderWrite0.addFlags(8);
            }

            if (IS_DEBUG_SAVE) {
                GameWindow.WriteString(output, "Number of objects (" + int0 + ")");
            }

            if (int0 >= 4) {
                output.putShort((short)this.Objects.size());
            }

            for (int int1 = 0; int1 < this.Objects.size(); int1++) {
                int int2 = output.position();
                if (IS_DEBUG_SAVE) {
                    output.putInt(0);
                }

                byte byte0 = 0;
                if (this.SpecialObjects.contains(this.Objects.get(int1))) {
                    byte0 = (byte)(byte0 | 2);
                }

                if (this.WorldObjects.contains(this.Objects.get(int1))) {
                    byte0 = (byte)(byte0 | 4);
                }

                output.put(byte0);
                if (IS_DEBUG_SAVE) {
                    GameWindow.WriteStringUTF(output, this.Objects.get(int1).getClass().getName());
                }

                this.Objects.get(int1).save(output, IS_DEBUG_SAVE);
                if (IS_DEBUG_SAVE) {
                    int int3 = output.position();
                    output.position(int2);
                    output.putInt(int3 - int2);
                    output.position(int3);
                }
            }

            if (IS_DEBUG_SAVE) {
                output.put((byte)67);
                output.put((byte)82);
                output.put((byte)80);
                output.put((byte)83);
            }
        }

        if (this.isOverlayDone()) {
            bitHeaderWrite0.addFlags(16);
        }

        if (this.haveRoof) {
            bitHeaderWrite0.addFlags(32);
        }

        BitHeaderWrite bitHeaderWrite1 = BitHeader.allocWrite(BitHeader.HeaderSize.Byte, output);
        int int4 = 0;

        for (int int5 = 0; int5 < this.StaticMovingObjects.size(); int5++) {
            if (this.StaticMovingObjects.get(int5) instanceof IsoDeadBody) {
                int4++;
            }
        }

        if (int4 > 0) {
            bitHeaderWrite1.addFlags(1);
            if (IS_DEBUG_SAVE) {
                GameWindow.WriteString(output, "Number of bodies");
            }

            output.putShort((short)int4);

            for (int int6 = 0; int6 < this.StaticMovingObjects.size(); int6++) {
                IsoMovingObject movingObject = this.StaticMovingObjects.get(int6);
                if (movingObject instanceof IsoDeadBody) {
                    if (IS_DEBUG_SAVE) {
                        GameWindow.WriteStringUTF(output, movingObject.getClass().getName());
                    }

                    movingObject.save(output, IS_DEBUG_SAVE);
                }
            }
        }

        if (this.table != null && !this.table.isEmpty()) {
            bitHeaderWrite1.addFlags(2);
            this.table.save(output);
        }

        if (this.burntOut) {
            bitHeaderWrite1.addFlags(4);
        }

        if (this.getTrapPositionX() > 0) {
            bitHeaderWrite1.addFlags(8);
            output.putInt(this.getTrapPositionX());
            output.putInt(this.getTrapPositionY());
            output.putInt(this.getTrapPositionZ());
        }

        if (this.haveSheetRope) {
            bitHeaderWrite1.addFlags(16);
        }

        if (!bitHeaderWrite1.equals(0)) {
            bitHeaderWrite0.addFlags(64);
            bitHeaderWrite1.write();
        } else {
            output.position(bitHeaderWrite1.getStartPosition());
        }

        bitHeaderWrite0.write();
        bitHeaderWrite0.release();
        bitHeaderWrite1.release();
    }

    static void loadmatrix(boolean[][][] var0, DataInputStream var1) throws IOException {
    }

    static void savematrix(boolean[][][] booleans, DataOutputStream dataOutputStream) throws IOException {
        for (int int0 = 0; int0 < 3; int0++) {
            for (int int1 = 0; int1 < 3; int1++) {
                for (int int2 = 0; int2 < 3; int2++) {
                    dataOutputStream.writeBoolean(booleans[int0][int1][int2]);
                }
            }
        }
    }

    public boolean isCommonGrass() {
        if (this.Objects.isEmpty()) {
            return false;
        } else {
            IsoObject object = this.Objects.get(0);
            return object.sprite.getProperties().Is(IsoFlagType.solidfloor) && ("TileFloorExt_3".equals(object.tile) || "TileFloorExt_4".equals(object.tile));
        }
    }

    public static boolean toBoolean(byte[] bytes) {
        return bytes != null && bytes.length != 0 ? bytes[0] != 0 : false;
    }

    public void removeCorpse(IsoDeadBody body, boolean bRemote) {
        if (GameClient.bClient && !bRemote) {
            try {
                GameClient.instance.checkAddedRemovedItems(body);
            } catch (Exception exception) {
                GameClient.connection.cancelPacket();
                ExceptionLogger.logException(exception);
            }

            GameClient.sendRemoveCorpseFromMap(body);
        }

        body.removeFromWorld();
        body.removeFromSquare();
        if (!GameServer.bServer) {
            LuaEventManager.triggerEvent("OnContainerUpdate", this);
        }
    }

    public IsoDeadBody getDeadBody() {
        for (int int0 = 0; int0 < this.StaticMovingObjects.size(); int0++) {
            if (this.StaticMovingObjects.get(int0) instanceof IsoDeadBody) {
                return (IsoDeadBody)this.StaticMovingObjects.get(int0);
            }
        }

        return null;
    }

    public List<IsoDeadBody> getDeadBodys() {
        ArrayList arrayList = new ArrayList();

        for (int int0 = 0; int0 < this.StaticMovingObjects.size(); int0++) {
            if (this.StaticMovingObjects.get(int0) instanceof IsoDeadBody) {
                arrayList.add((IsoDeadBody)this.StaticMovingObjects.get(int0));
            }
        }

        return arrayList;
    }

    public void addCorpse(IsoDeadBody body, boolean bRemote) {
        if (GameClient.bClient && !bRemote) {
            ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
            PacketTypes.PacketType.AddCorpseToMap.doPacket(byteBufferWriter);
            byteBufferWriter.putShort(body.getObjectID());
            byteBufferWriter.putShort(body.getOnlineID());
            byteBufferWriter.putInt(this.x);
            byteBufferWriter.putInt(this.y);
            byteBufferWriter.putInt(this.z);
            body.writeToRemoteBuffer(byteBufferWriter);
            PacketTypes.PacketType.AddCorpseToMap.send(GameClient.connection);
        }

        if (!this.StaticMovingObjects.contains(body)) {
            this.StaticMovingObjects.add(body);
        }

        body.addToWorld();
        this.burntOut = false;
        this.Properties.UnSet(IsoFlagType.burntOut);
    }

    public IsoBrokenGlass getBrokenGlass() {
        for (int int0 = 0; int0 < this.SpecialObjects.size(); int0++) {
            IsoObject object = this.SpecialObjects.get(int0);
            if (object instanceof IsoBrokenGlass) {
                return (IsoBrokenGlass)object;
            }
        }

        return null;
    }

    public IsoBrokenGlass addBrokenGlass() {
        if (!this.isFree(false)) {
            return this.getBrokenGlass();
        } else {
            IsoBrokenGlass brokenGlass = this.getBrokenGlass();
            if (brokenGlass == null) {
                brokenGlass = new IsoBrokenGlass(this.getCell());
                brokenGlass.setSquare(this);
                this.AddSpecialObject(brokenGlass);
                if (GameServer.bServer) {
                    GameServer.transmitBrokenGlass(this);
                }
            }

            return brokenGlass;
        }
    }

    public void load(ByteBuffer b, int WorldVersion) throws IOException {
        this.load(b, WorldVersion, false);
    }

    public void load(ByteBuffer b, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        this.getErosionData().load(b, WorldVersion);
        BitHeaderRead bitHeaderRead0 = BitHeader.allocRead(BitHeader.HeaderSize.Byte, b);
        if (!bitHeaderRead0.equals(0)) {
            if (bitHeaderRead0.hasFlags(1)) {
                if (IS_DEBUG_SAVE) {
                    String string0 = GameWindow.ReadStringUTF(b);
                    DebugLog.log(string0);
                }

                short short0 = 1;
                if (bitHeaderRead0.hasFlags(2)) {
                    short0 = 2;
                } else if (bitHeaderRead0.hasFlags(4)) {
                    short0 = 3;
                } else if (bitHeaderRead0.hasFlags(8)) {
                    short0 = b.getShort();
                }

                for (int int0 = 0; int0 < short0; int0++) {
                    int int1 = b.position();
                    int int2 = 0;
                    if (IS_DEBUG_SAVE) {
                        int2 = b.getInt();
                    }

                    byte byte0 = b.get();
                    boolean boolean0 = (byte0 & 2) != 0;
                    boolean boolean1 = (byte0 & 4) != 0;
                    IsoObject object = null;
                    if (IS_DEBUG_SAVE) {
                        String string1 = GameWindow.ReadStringUTF(b);
                        DebugLog.log(string1);
                    }

                    object = IsoObject.factoryFromFileInput(this.getCell(), b);
                    if (object == null) {
                        if (IS_DEBUG_SAVE) {
                            int int3 = b.position();
                            if (int3 - int1 != int2) {
                                DebugLog.log(
                                    "***** Object loaded size "
                                        + (int3 - int1)
                                        + " != saved size "
                                        + int2
                                        + ", reading obj size: "
                                        + short0
                                        + ", Object == null"
                                );
                                if (object.getSprite() != null && object.getSprite().getName() != null) {
                                    DebugLog.log("Obj sprite = " + object.getSprite().getName());
                                }
                            }
                        }
                    } else {
                        object.square = this;

                        try {
                            object.load(b, WorldVersion, IS_DEBUG_SAVE);
                        } catch (Exception exception0) {
                            this.debugPrintGridSquare();
                            if (lastLoaded != null) {
                                lastLoaded.debugPrintGridSquare();
                            }

                            throw new RuntimeException(exception0);
                        }

                        if (IS_DEBUG_SAVE) {
                            int int4 = b.position();
                            if (int4 - int1 != int2) {
                                DebugLog.log("***** Object loaded size " + (int4 - int1) + " != saved size " + int2 + ", reading obj size: " + short0);
                                if (object.getSprite() != null && object.getSprite().getName() != null) {
                                    DebugLog.log("Obj sprite = " + object.getSprite().getName());
                                }
                            }
                        }

                        if (object instanceof IsoWorldInventoryObject) {
                            if (((IsoWorldInventoryObject)object).getItem() == null) {
                                continue;
                            }

                            String string2 = ((IsoWorldInventoryObject)object).getItem().getFullType();
                            Item item = ScriptManager.instance.FindItem(string2);
                            if (item != null && item.getObsolete()) {
                                continue;
                            }

                            String[] strings = string2.split("_");
                            if (((IsoWorldInventoryObject)object).dropTime > -1.0
                                && SandboxOptions.instance.HoursForWorldItemRemoval.getValue() > 0.0
                                && (
                                    SandboxOptions.instance.WorldItemRemovalList.getValue().contains(strings[0])
                                            && !SandboxOptions.instance.ItemRemovalListBlacklistToggle.getValue()
                                        || !SandboxOptions.instance.WorldItemRemovalList.getValue().contains(strings[0])
                                            && SandboxOptions.instance.ItemRemovalListBlacklistToggle.getValue()
                                )
                                && !((IsoWorldInventoryObject)object).isIgnoreRemoveSandbox()
                                && GameTime.instance.getWorldAgeHours()
                                    > ((IsoWorldInventoryObject)object).dropTime + SandboxOptions.instance.HoursForWorldItemRemoval.getValue()) {
                                continue;
                            }
                        }

                        if (!(object instanceof IsoWindow)
                            || object.getSprite() == null
                            || !"walls_special_01_8".equals(object.getSprite().getName()) && !"walls_special_01_9".equals(object.getSprite().getName())) {
                            this.Objects.add(object);
                            if (boolean0) {
                                this.SpecialObjects.add(object);
                            }

                            if (boolean1) {
                                if (Core.bDebug && !(object instanceof IsoWorldInventoryObject)) {
                                    DebugLog.log(
                                        "Bitflags = "
                                            + byte0
                                            + ", obj name = "
                                            + object.getObjectName()
                                            + ", sprite = "
                                            + (object.getSprite() != null ? object.getSprite().getName() : "unknown")
                                    );
                                }

                                this.WorldObjects.add((IsoWorldInventoryObject)object);
                                object.square.chunk.recalcHashCodeObjects();
                            }
                        }
                    }
                }

                if (IS_DEBUG_SAVE) {
                    byte byte1 = b.get();
                    byte byte2 = b.get();
                    byte byte3 = b.get();
                    byte byte4 = b.get();
                    if (byte1 != 67 || byte2 != 82 || byte3 != 80 || byte4 != 83) {
                        DebugLog.log("***** Expected CRPS here");
                    }
                }
            }

            this.setOverlayDone(bitHeaderRead0.hasFlags(16));
            this.haveRoof = bitHeaderRead0.hasFlags(32);
            if (bitHeaderRead0.hasFlags(64)) {
                BitHeaderRead bitHeaderRead1 = BitHeader.allocRead(BitHeader.HeaderSize.Byte, b);
                if (bitHeaderRead1.hasFlags(1)) {
                    if (IS_DEBUG_SAVE) {
                        String string3 = GameWindow.ReadStringUTF(b);
                        DebugLog.log(string3);
                    }

                    short short1 = b.getShort();

                    for (int int5 = 0; int5 < short1; int5++) {
                        IsoMovingObject movingObject = null;
                        if (IS_DEBUG_SAVE) {
                            String string4 = GameWindow.ReadStringUTF(b);
                            DebugLog.log(string4);
                        }

                        try {
                            movingObject = (IsoMovingObject)IsoObject.factoryFromFileInput(this.getCell(), b);
                        } catch (Exception exception1) {
                            this.debugPrintGridSquare();
                            if (lastLoaded != null) {
                                lastLoaded.debugPrintGridSquare();
                            }

                            throw new RuntimeException(exception1);
                        }

                        if (movingObject != null) {
                            movingObject.square = this;
                            movingObject.current = this;

                            try {
                                movingObject.load(b, WorldVersion, IS_DEBUG_SAVE);
                            } catch (Exception exception2) {
                                this.debugPrintGridSquare();
                                if (lastLoaded != null) {
                                    lastLoaded.debugPrintGridSquare();
                                }

                                throw new RuntimeException(exception2);
                            }

                            this.StaticMovingObjects.add(movingObject);
                            this.recalcHashCodeObjects();
                        }
                    }
                }

                if (bitHeaderRead1.hasFlags(2)) {
                    if (this.table == null) {
                        this.table = LuaManager.platform.newTable();
                    }

                    this.table.load(b, WorldVersion);
                }

                this.burntOut = bitHeaderRead1.hasFlags(4);
                if (bitHeaderRead1.hasFlags(8)) {
                    this.setTrapPositionX(b.getInt());
                    this.setTrapPositionY(b.getInt());
                    this.setTrapPositionZ(b.getInt());
                }

                this.haveSheetRope = bitHeaderRead1.hasFlags(16);
                bitHeaderRead1.release();
            }
        }

        bitHeaderRead0.release();
        lastLoaded = this;
    }

    private void debugPrintGridSquare() {
        System.out.println("x=" + this.x + " y=" + this.y + " z=" + this.z);
        System.out.println("objects");

        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            this.Objects.get(int0).debugPrintout();
        }

        System.out.println("staticmovingobjects");

        for (int int1 = 0; int1 < this.StaticMovingObjects.size(); int1++) {
            this.Objects.get(int1).debugPrintout();
        }
    }

    public float scoreAsWaypoint(int _x, int _y) {
        float float0 = 2.0F;
        return float0 - IsoUtils.DistanceManhatten(_x, _y, this.getX(), this.getY()) * 5.0F;
    }

    public void InvalidateSpecialObjectPaths() {
    }

    public boolean isSolid() {
        return this.Properties.Is(IsoFlagType.solid);
    }

    public boolean isSolidTrans() {
        return this.Properties.Is(IsoFlagType.solidtrans);
    }

    public boolean isFree(boolean bCountOtherCharacters) {
        if (bCountOtherCharacters && this.MovingObjects.size() > 0) {
            return false;
        } else if (this.CachedIsFree) {
            return this.CacheIsFree;
        } else {
            this.CachedIsFree = true;
            this.CacheIsFree = true;
            if (this.Properties.Is(IsoFlagType.solid) || this.Properties.Is(IsoFlagType.solidtrans) || this.Has(IsoObjectType.tree)) {
                this.CacheIsFree = false;
            }

            if (!this.Properties.Is(IsoFlagType.solidfloor)) {
                this.CacheIsFree = false;
            }

            if (this.Has(IsoObjectType.stairsBN) || this.Has(IsoObjectType.stairsMN) || this.Has(IsoObjectType.stairsTN)) {
                this.CacheIsFree = true;
            } else if (this.Has(IsoObjectType.stairsBW) || this.Has(IsoObjectType.stairsMW) || this.Has(IsoObjectType.stairsTW)) {
                this.CacheIsFree = true;
            }

            return this.CacheIsFree;
        }
    }

    public boolean isFreeOrMidair(boolean bCountOtherCharacters) {
        if (bCountOtherCharacters && this.MovingObjects.size() > 0) {
            return false;
        } else {
            boolean boolean0 = true;
            if (this.Properties.Is(IsoFlagType.solid) || this.Properties.Is(IsoFlagType.solidtrans) || this.Has(IsoObjectType.tree)) {
                boolean0 = false;
            }

            if (this.Has(IsoObjectType.stairsBN) || this.Has(IsoObjectType.stairsMN) || this.Has(IsoObjectType.stairsTN)) {
                boolean0 = true;
            } else if (this.Has(IsoObjectType.stairsBW) || this.Has(IsoObjectType.stairsMW) || this.Has(IsoObjectType.stairsTW)) {
                boolean0 = true;
            }

            return boolean0;
        }
    }

    public boolean isFreeOrMidair(boolean bCountOtherCharacters, boolean bDoZombie) {
        if (bCountOtherCharacters && this.MovingObjects.size() > 0) {
            if (!bDoZombie) {
                return false;
            }

            for (int int0 = 0; int0 < this.MovingObjects.size(); int0++) {
                IsoMovingObject movingObject = this.MovingObjects.get(int0);
                if (!(movingObject instanceof IsoDeadBody)) {
                    return false;
                }
            }
        }

        boolean boolean0 = true;
        if (this.Properties.Is(IsoFlagType.solid) || this.Properties.Is(IsoFlagType.solidtrans) || this.Has(IsoObjectType.tree)) {
            boolean0 = false;
        }

        if (this.Has(IsoObjectType.stairsBN) || this.Has(IsoObjectType.stairsMN) || this.Has(IsoObjectType.stairsTN)) {
            boolean0 = true;
        } else if (this.Has(IsoObjectType.stairsBW) || this.Has(IsoObjectType.stairsMW) || this.Has(IsoObjectType.stairsTW)) {
            boolean0 = true;
        }

        return boolean0;
    }

    /**
     * Check if there's at least one solid floor around this tile, used to build wooden floor
     */
    public boolean connectedWithFloor() {
        if (this.getZ() == 0) {
            return true;
        } else {
            Object object = null;
            object = this.getCell().getGridSquare(this.getX() - 1, this.getY(), this.getZ());
            if (object != null && ((IsoGridSquare)object).Properties.Is(IsoFlagType.solidfloor)) {
                return true;
            } else {
                object = this.getCell().getGridSquare(this.getX() + 1, this.getY(), this.getZ());
                if (object != null && ((IsoGridSquare)object).Properties.Is(IsoFlagType.solidfloor)) {
                    return true;
                } else {
                    object = this.getCell().getGridSquare(this.getX(), this.getY() - 1, this.getZ());
                    if (object != null && ((IsoGridSquare)object).Properties.Is(IsoFlagType.solidfloor)) {
                        return true;
                    } else {
                        object = this.getCell().getGridSquare(this.getX(), this.getY() + 1, this.getZ());
                        return object != null && ((IsoGridSquare)object).Properties.Is(IsoFlagType.solidfloor);
                    }
                }
            }
        }
    }

    /**
     * Check if a tile has a solid floor, used to build stuff at z level > 0  Also gonna check the tile "behind" the one w<e're trying to build something has a floor (only one is required)
     * 
     * @param north is the item we're trying to place facing north or not
     */
    public boolean hasFloor(boolean north) {
        if (this.Properties.Is(IsoFlagType.solidfloor)) {
            return true;
        } else {
            Object object = null;
            if (north) {
                object = this.getCell().getGridSquare(this.getX(), this.getY() - 1, this.getZ());
            } else {
                object = this.getCell().getGridSquare(this.getX() - 1, this.getY(), this.getZ());
            }

            return object != null && ((IsoGridSquare)object).Properties.Is(IsoFlagType.solidfloor);
        }
    }

    public boolean isNotBlocked(boolean bCountOtherCharacters) {
        if (!this.CachedIsFree) {
            this.CacheIsFree = true;
            this.CachedIsFree = true;
            if (this.Properties.Is(IsoFlagType.solid) || this.Properties.Is(IsoFlagType.solidtrans)) {
                this.CacheIsFree = false;
            }

            if (!this.Properties.Is(IsoFlagType.solidfloor)) {
                this.CacheIsFree = false;
            }
        } else if (!this.CacheIsFree) {
            return false;
        }

        return !bCountOtherCharacters || this.MovingObjects.size() <= 0;
    }

    public IsoObject getDoor(boolean north) {
        for (int int0 = 0; int0 < this.SpecialObjects.size(); int0++) {
            IsoObject object = this.SpecialObjects.get(int0);
            if (object instanceof IsoThumpable thumpable && thumpable.isDoor() && north == thumpable.north) {
                return thumpable;
            }

            if (object instanceof IsoDoor door && north == door.north) {
                return door;
            }
        }

        return null;
    }

    public IsoDoor getIsoDoor() {
        for (int int0 = 0; int0 < this.SpecialObjects.size(); int0++) {
            IsoObject object = this.SpecialObjects.get(int0);
            if (object instanceof IsoDoor) {
                return (IsoDoor)object;
            }
        }

        return null;
    }

    /**
     * Get the door between this grid and the next in parameter
     */
    public IsoObject getDoorTo(IsoGridSquare next) {
        if (next != null && next != this) {
            IsoObject object = null;
            if (next.x < this.x) {
                object = this.getDoor(false);
                if (object != null) {
                    return object;
                }
            }

            if (next.y < this.y) {
                object = this.getDoor(true);
                if (object != null) {
                    return object;
                }
            }

            if (next.x > this.x) {
                object = next.getDoor(false);
                if (object != null) {
                    return object;
                }
            }

            if (next.y > this.y) {
                object = next.getDoor(true);
                if (object != null) {
                    return object;
                }
            }

            if (next.x != this.x && next.y != this.y) {
                IsoGridSquare square0 = this.getCell().getGridSquare(this.x, next.y, this.z);
                IsoGridSquare square1 = this.getCell().getGridSquare(next.x, this.y, this.z);
                object = this.getDoorTo(square0);
                if (object != null) {
                    return object;
                }

                object = this.getDoorTo(square1);
                if (object != null) {
                    return object;
                }

                object = next.getDoorTo(square0);
                if (object != null) {
                    return object;
                }

                object = next.getDoorTo(square1);
                if (object != null) {
                    return object;
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public IsoWindow getWindow(boolean north) {
        for (int int0 = 0; int0 < this.SpecialObjects.size(); int0++) {
            IsoObject object = this.SpecialObjects.get(int0);
            if (object instanceof IsoWindow window && north == window.north) {
                return window;
            }
        }

        return null;
    }

    public IsoWindow getWindow() {
        for (int int0 = 0; int0 < this.SpecialObjects.size(); int0++) {
            IsoObject object = this.SpecialObjects.get(int0);
            if (object instanceof IsoWindow) {
                return (IsoWindow)object;
            }
        }

        return null;
    }

    /**
     * Get the IsoWindow window between this grid and the next in parameter
     */
    public IsoWindow getWindowTo(IsoGridSquare next) {
        if (next != null && next != this) {
            IsoWindow window = null;
            if (next.x < this.x) {
                window = this.getWindow(false);
                if (window != null) {
                    return window;
                }
            }

            if (next.y < this.y) {
                window = this.getWindow(true);
                if (window != null) {
                    return window;
                }
            }

            if (next.x > this.x) {
                window = next.getWindow(false);
                if (window != null) {
                    return window;
                }
            }

            if (next.y > this.y) {
                window = next.getWindow(true);
                if (window != null) {
                    return window;
                }
            }

            if (next.x != this.x && next.y != this.y) {
                IsoGridSquare square0 = this.getCell().getGridSquare(this.x, next.y, this.z);
                IsoGridSquare square1 = this.getCell().getGridSquare(next.x, this.y, this.z);
                window = this.getWindowTo(square0);
                if (window != null) {
                    return window;
                }

                window = this.getWindowTo(square1);
                if (window != null) {
                    return window;
                }

                window = next.getWindowTo(square0);
                if (window != null) {
                    return window;
                }

                window = next.getWindowTo(square1);
                if (window != null) {
                    return window;
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public boolean isAdjacentToWindow() {
        if (this.getWindow() != null) {
            return true;
        } else if (this.hasWindowFrame()) {
            return true;
        } else if (this.getThumpableWindow(false) == null && this.getThumpableWindow(true) == null) {
            IsoGridSquare square1 = this.nav[IsoDirections.S.index()];
            if (square1 == null || square1.getWindow(true) == null && square1.getWindowFrame(true) == null && square1.getThumpableWindow(true) == null) {
                IsoGridSquare square2 = this.nav[IsoDirections.E.index()];
                return square2 != null
                    && (square2.getWindow(false) != null || square2.getWindowFrame(false) != null || square2.getThumpableWindow(false) != null);
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public IsoThumpable getThumpableWindow(boolean north) {
        for (int int0 = 0; int0 < this.SpecialObjects.size(); int0++) {
            IsoObject object = this.SpecialObjects.get(int0);
            if (object instanceof IsoThumpable thumpable && thumpable.isWindow() && north == thumpable.north) {
                return thumpable;
            }
        }

        return null;
    }

    /**
     * Get the IsoThumpable window between this grid and the next in parameter
     */
    public IsoThumpable getWindowThumpableTo(IsoGridSquare next) {
        if (next != null && next != this) {
            IsoThumpable thumpable = null;
            if (next.x < this.x) {
                thumpable = this.getThumpableWindow(false);
                if (thumpable != null) {
                    return thumpable;
                }
            }

            if (next.y < this.y) {
                thumpable = this.getThumpableWindow(true);
                if (thumpable != null) {
                    return thumpable;
                }
            }

            if (next.x > this.x) {
                thumpable = next.getThumpableWindow(false);
                if (thumpable != null) {
                    return thumpable;
                }
            }

            if (next.y > this.y) {
                thumpable = next.getThumpableWindow(true);
                if (thumpable != null) {
                    return thumpable;
                }
            }

            if (next.x != this.x && next.y != this.y) {
                IsoGridSquare square0 = this.getCell().getGridSquare(this.x, next.y, this.z);
                IsoGridSquare square1 = this.getCell().getGridSquare(next.x, this.y, this.z);
                thumpable = this.getWindowThumpableTo(square0);
                if (thumpable != null) {
                    return thumpable;
                }

                thumpable = this.getWindowThumpableTo(square1);
                if (thumpable != null) {
                    return thumpable;
                }

                thumpable = next.getWindowThumpableTo(square0);
                if (thumpable != null) {
                    return thumpable;
                }

                thumpable = next.getWindowThumpableTo(square1);
                if (thumpable != null) {
                    return thumpable;
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public IsoThumpable getHoppableThumpable(boolean north) {
        for (int int0 = 0; int0 < this.SpecialObjects.size(); int0++) {
            IsoObject object = this.SpecialObjects.get(int0);
            if (object instanceof IsoThumpable thumpable && thumpable.isHoppable() && north == thumpable.north) {
                return thumpable;
            }
        }

        return null;
    }

    public IsoThumpable getHoppableThumpableTo(IsoGridSquare next) {
        if (next != null && next != this) {
            IsoThumpable thumpable = null;
            if (next.x < this.x) {
                thumpable = this.getHoppableThumpable(false);
                if (thumpable != null) {
                    return thumpable;
                }
            }

            if (next.y < this.y) {
                thumpable = this.getHoppableThumpable(true);
                if (thumpable != null) {
                    return thumpable;
                }
            }

            if (next.x > this.x) {
                thumpable = next.getHoppableThumpable(false);
                if (thumpable != null) {
                    return thumpable;
                }
            }

            if (next.y > this.y) {
                thumpable = next.getHoppableThumpable(true);
                if (thumpable != null) {
                    return thumpable;
                }
            }

            if (next.x != this.x && next.y != this.y) {
                IsoGridSquare square0 = this.getCell().getGridSquare(this.x, next.y, this.z);
                IsoGridSquare square1 = this.getCell().getGridSquare(next.x, this.y, this.z);
                thumpable = this.getHoppableThumpableTo(square0);
                if (thumpable != null) {
                    return thumpable;
                }

                thumpable = this.getHoppableThumpableTo(square1);
                if (thumpable != null) {
                    return thumpable;
                }

                thumpable = next.getHoppableThumpableTo(square0);
                if (thumpable != null) {
                    return thumpable;
                }

                thumpable = next.getHoppableThumpableTo(square1);
                if (thumpable != null) {
                    return thumpable;
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public IsoObject getWallHoppable(boolean north) {
        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            if (this.Objects.get(int0).isHoppable() && north == this.Objects.get(int0).isNorthHoppable()) {
                return this.Objects.get(int0);
            }
        }

        return null;
    }

    public IsoObject getWallHoppableTo(IsoGridSquare next) {
        if (next != null && next != this) {
            IsoObject object = null;
            if (next.x < this.x) {
                object = this.getWallHoppable(false);
                if (object != null) {
                    return object;
                }
            }

            if (next.y < this.y) {
                object = this.getWallHoppable(true);
                if (object != null) {
                    return object;
                }
            }

            if (next.x > this.x) {
                object = next.getWallHoppable(false);
                if (object != null) {
                    return object;
                }
            }

            if (next.y > this.y) {
                object = next.getWallHoppable(true);
                if (object != null) {
                    return object;
                }
            }

            if (next.x != this.x && next.y != this.y) {
                IsoGridSquare square0 = this.getCell().getGridSquare(this.x, next.y, this.z);
                IsoGridSquare square1 = this.getCell().getGridSquare(next.x, this.y, this.z);
                object = this.getWallHoppableTo(square0);
                if (object != null) {
                    return object;
                }

                object = this.getWallHoppableTo(square1);
                if (object != null) {
                    return object;
                }

                object = next.getWallHoppableTo(square0);
                if (object != null) {
                    return object;
                }

                object = next.getWallHoppableTo(square1);
                if (object != null) {
                    return object;
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public IsoObject getBedTo(IsoGridSquare next) {
        ArrayList arrayList = null;
        if (next.y >= this.y && next.x >= this.x) {
            arrayList = next.SpecialObjects;
        } else {
            arrayList = this.SpecialObjects;
        }

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            IsoObject object = (IsoObject)arrayList.get(int0);
            if (object.getProperties().Is(IsoFlagType.bed)) {
                return object;
            }
        }

        return null;
    }

    public IsoObject getWindowFrame(boolean north) {
        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            IsoObject object = this.Objects.get(int0);
            if (!(object instanceof IsoWorldInventoryObject) && IsoWindowFrame.isWindowFrame(object, north)) {
                return object;
            }
        }

        return null;
    }

    public IsoObject getWindowFrameTo(IsoGridSquare next) {
        if (next != null && next != this) {
            IsoObject object = null;
            if (next.x < this.x) {
                object = this.getWindowFrame(false);
                if (object != null) {
                    return object;
                }
            }

            if (next.y < this.y) {
                object = this.getWindowFrame(true);
                if (object != null) {
                    return object;
                }
            }

            if (next.x > this.x) {
                object = next.getWindowFrame(false);
                if (object != null) {
                    return object;
                }
            }

            if (next.y > this.y) {
                object = next.getWindowFrame(true);
                if (object != null) {
                    return object;
                }
            }

            if (next.x != this.x && next.y != this.y) {
                IsoGridSquare square0 = this.getCell().getGridSquare(this.x, next.y, this.z);
                IsoGridSquare square1 = this.getCell().getGridSquare(next.x, this.y, this.z);
                object = this.getWindowFrameTo(square0);
                if (object != null) {
                    return object;
                }

                object = this.getWindowFrameTo(square1);
                if (object != null) {
                    return object;
                }

                object = next.getWindowFrameTo(square0);
                if (object != null) {
                    return object;
                }

                object = next.getWindowFrameTo(square1);
                if (object != null) {
                    return object;
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public boolean hasWindowFrame() {
        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            IsoObject object = this.Objects.get(int0);
            if (!(object instanceof IsoWorldInventoryObject) && IsoWindowFrame.isWindowFrame(object)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasWindowOrWindowFrame() {
        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            IsoObject object = this.Objects.get(int0);
            if (!(object instanceof IsoWorldInventoryObject) && (this.isWindowOrWindowFrame(object, true) || this.isWindowOrWindowFrame(object, false))) {
                return true;
            }
        }

        return false;
    }

    private IsoObject getSpecialWall(boolean boolean0) {
        for (int int0 = this.SpecialObjects.size() - 1; int0 >= 0; int0--) {
            IsoObject object0 = this.SpecialObjects.get(int0);
            if (object0 instanceof IsoThumpable thumpable) {
                if (thumpable.isStairs()
                    || !thumpable.isThumpable() && !thumpable.isWindow() && !thumpable.isDoor()
                    || thumpable.isDoor() && thumpable.open
                    || thumpable.isBlockAllTheSquare()) {
                    continue;
                }

                if (boolean0 == thumpable.north && !thumpable.isCorner()) {
                    return thumpable;
                }
            }

            if (object0 instanceof IsoWindow window && boolean0 == window.north) {
                return window;
            }

            if (object0 instanceof IsoDoor door && boolean0 == door.north && !door.open) {
                return door;
            }
        }

        if ((!boolean0 || this.Is(IsoFlagType.WindowN)) && (boolean0 || this.Is(IsoFlagType.WindowW))) {
            IsoObject object1 = this.getWindowFrame(boolean0);
            return object1 != null ? object1 : null;
        } else {
            return null;
        }
    }

    public IsoObject getSheetRope() {
        for (int int0 = 0; int0 < this.getObjects().size(); int0++) {
            IsoObject object = this.getObjects().get(int0);
            if (object.sheetRope) {
                return object;
            }
        }

        return null;
    }

    public boolean damageSpriteSheetRopeFromBottom(IsoPlayer player, boolean north) {
        IsoGridSquare square = this;
        IsoFlagType flagType;
        if (north) {
            if (this.Is(IsoFlagType.climbSheetN)) {
                flagType = IsoFlagType.climbSheetN;
            } else {
                if (!this.Is(IsoFlagType.climbSheetS)) {
                    return false;
                }

                flagType = IsoFlagType.climbSheetS;
            }
        } else if (this.Is(IsoFlagType.climbSheetW)) {
            flagType = IsoFlagType.climbSheetW;
        } else {
            if (!this.Is(IsoFlagType.climbSheetE)) {
                return false;
            }

            flagType = IsoFlagType.climbSheetE;
        }

        while (square != null) {
            for (int int0 = 0; int0 < square.getObjects().size(); int0++) {
                IsoObject object = square.getObjects().get(int0);
                if (object.getProperties() != null && object.getProperties().Is(flagType)) {
                    int int1 = Integer.parseInt(object.getSprite().getName().split("_")[2]);
                    if (int1 > 14) {
                        return false;
                    }

                    String string = object.getSprite().getName().split("_")[0] + "_" + object.getSprite().getName().split("_")[1];
                    int1 += 40;
                    object.setSprite(IsoSpriteManager.instance.getSprite(string + "_" + int1));
                    object.transmitUpdatedSprite();
                    break;
                }
            }

            if (square.getZ() == 7) {
                break;
            }

            square = square.getCell().getGridSquare(square.getX(), square.getY(), square.getZ() + 1);
        }

        return true;
    }

    public boolean removeSheetRopeFromBottom(IsoPlayer player, boolean north) {
        IsoGridSquare square0 = this;
        IsoFlagType flagType0;
        IsoFlagType flagType1;
        if (north) {
            if (this.Is(IsoFlagType.climbSheetN)) {
                flagType0 = IsoFlagType.climbSheetTopN;
                flagType1 = IsoFlagType.climbSheetN;
            } else {
                if (!this.Is(IsoFlagType.climbSheetS)) {
                    return false;
                }

                flagType0 = IsoFlagType.climbSheetTopS;
                flagType1 = IsoFlagType.climbSheetS;
                String string0 = "crafted_01_4";

                for (int int0 = 0; int0 < square0.getObjects().size(); int0++) {
                    IsoObject object0 = square0.getObjects().get(int0);
                    if (object0.sprite != null && object0.sprite.getName() != null && object0.sprite.getName().equals(string0)) {
                        square0.transmitRemoveItemFromSquare(object0);
                        break;
                    }
                }
            }
        } else if (this.Is(IsoFlagType.climbSheetW)) {
            flagType0 = IsoFlagType.climbSheetTopW;
            flagType1 = IsoFlagType.climbSheetW;
        } else {
            if (!this.Is(IsoFlagType.climbSheetE)) {
                return false;
            }

            flagType0 = IsoFlagType.climbSheetTopE;
            flagType1 = IsoFlagType.climbSheetE;
            String string1 = "crafted_01_3";

            for (int int1 = 0; int1 < square0.getObjects().size(); int1++) {
                IsoObject object1 = square0.getObjects().get(int1);
                if (object1.sprite != null && object1.sprite.getName() != null && object1.sprite.getName().equals(string1)) {
                    square0.transmitRemoveItemFromSquare(object1);
                    break;
                }
            }
        }

        boolean boolean0 = false;

        IsoGridSquare square1;
        for (square1 = null; square0 != null; boolean0 = false) {
            for (int int2 = 0; int2 < square0.getObjects().size(); int2++) {
                IsoObject object2 = square0.getObjects().get(int2);
                if (object2.getProperties() != null && (object2.getProperties().Is(flagType0) || object2.getProperties().Is(flagType1))) {
                    square1 = square0;
                    boolean0 = true;
                    square0.transmitRemoveItemFromSquare(object2);
                    if (GameServer.bServer) {
                        if (player != null) {
                            player.sendObjectChange("addItemOfType", new Object[]{"type", object2.getName()});
                        }
                    } else if (player != null) {
                        player.getInventory().AddItem(object2.getName());
                    }
                    break;
                }
            }

            if (square0.getZ() == 7) {
                break;
            }

            square0 = square0.getCell().getGridSquare(square0.getX(), square0.getY(), square0.getZ() + 1);
        }

        if (!boolean0) {
            square0 = square1.getCell().getGridSquare(square1.getX(), square1.getY(), square1.getZ());
            IsoGridSquare square2 = north ? square0.nav[IsoDirections.S.index()] : square0.nav[IsoDirections.E.index()];
            if (square2 == null) {
                return true;
            }

            for (int int3 = 0; int3 < square2.getObjects().size(); int3++) {
                IsoObject object3 = square2.getObjects().get(int3);
                if (object3.getProperties() != null && (object3.getProperties().Is(flagType0) || object3.getProperties().Is(flagType1))) {
                    square2.transmitRemoveItemFromSquare(object3);
                    break;
                }
            }
        }

        return true;
    }

    private IsoObject getSpecialSolid() {
        for (int int0 = 0; int0 < this.SpecialObjects.size(); int0++) {
            IsoObject object0 = this.SpecialObjects.get(int0);
            if (object0 instanceof IsoThumpable thumpable && !thumpable.isStairs() && thumpable.isThumpable() && thumpable.isBlockAllTheSquare()) {
                if (thumpable.getProperties().Is(IsoFlagType.solidtrans) && this.isAdjacentToWindow()) {
                    return null;
                }

                return thumpable;
            }
        }

        for (int int1 = 0; int1 < this.Objects.size(); int1++) {
            IsoObject object1 = this.Objects.get(int1);
            if (object1.isMovedThumpable()) {
                if (this.isAdjacentToWindow()) {
                    return null;
                }

                return object1;
            }
        }

        return null;
    }

    public IsoObject testCollideSpecialObjects(IsoGridSquare next) {
        if (next == null || next == this) {
            return null;
        } else if (next.x < this.x && next.y == this.y) {
            if (next.z == this.z && this.Has(IsoObjectType.stairsTW)) {
                return null;
            } else {
                IsoObject object0 = this.getSpecialWall(false);
                if (object0 != null) {
                    return object0;
                } else if (this.isBlockedTo(next)) {
                    return null;
                } else {
                    object0 = next.getSpecialSolid();
                    return object0 != null ? object0 : null;
                }
            }
        } else if (next.x == this.x && next.y < this.y) {
            if (next.z == this.z && this.Has(IsoObjectType.stairsTN)) {
                return null;
            } else {
                IsoObject object1 = this.getSpecialWall(true);
                if (object1 != null) {
                    return object1;
                } else if (this.isBlockedTo(next)) {
                    return null;
                } else {
                    object1 = next.getSpecialSolid();
                    return object1 != null ? object1 : null;
                }
            }
        } else if (next.x > this.x && next.y == this.y) {
            IsoObject object2 = next.getSpecialWall(false);
            if (object2 != null) {
                return object2;
            } else if (this.isBlockedTo(next)) {
                return null;
            } else {
                object2 = next.getSpecialSolid();
                return object2 != null ? object2 : null;
            }
        } else if (next.x == this.x && next.y > this.y) {
            IsoObject object3 = next.getSpecialWall(true);
            if (object3 != null) {
                return object3;
            } else if (this.isBlockedTo(next)) {
                return null;
            } else {
                object3 = next.getSpecialSolid();
                return object3 != null ? object3 : null;
            }
        } else if (next.x < this.x && next.y < this.y) {
            IsoObject object4 = this.getSpecialWall(true);
            if (object4 != null) {
                return object4;
            } else {
                object4 = this.getSpecialWall(false);
                if (object4 != null) {
                    return object4;
                } else {
                    IsoGridSquare square0 = this.getCell().getGridSquare(this.x, this.y - 1, this.z);
                    if (square0 != null && !this.isBlockedTo(square0)) {
                        object4 = square0.getSpecialSolid();
                        if (object4 != null) {
                            return object4;
                        }

                        object4 = square0.getSpecialWall(false);
                        if (object4 != null) {
                            return object4;
                        }
                    }

                    IsoGridSquare square1 = this.getCell().getGridSquare(this.x - 1, this.y, this.z);
                    if (square1 != null && !this.isBlockedTo(square1)) {
                        object4 = square1.getSpecialSolid();
                        if (object4 != null) {
                            return object4;
                        }

                        object4 = square1.getSpecialWall(true);
                        if (object4 != null) {
                            return object4;
                        }
                    }

                    if (square0 == null || this.isBlockedTo(square0) || square1 == null || this.isBlockedTo(square1)) {
                        return null;
                    } else if (!square0.isBlockedTo(next) && !square1.isBlockedTo(next)) {
                        object4 = next.getSpecialSolid();
                        return object4 != null ? object4 : null;
                    } else {
                        return null;
                    }
                }
            }
        } else if (next.x > this.x && next.y < this.y) {
            IsoObject object5 = this.getSpecialWall(true);
            if (object5 != null) {
                return object5;
            } else {
                IsoGridSquare square2 = this.getCell().getGridSquare(this.x, this.y - 1, this.z);
                if (square2 != null && !this.isBlockedTo(square2)) {
                    object5 = square2.getSpecialSolid();
                    if (object5 != null) {
                        return object5;
                    }
                }

                IsoGridSquare square3 = this.getCell().getGridSquare(this.x + 1, this.y, this.z);
                if (square3 != null) {
                    object5 = square3.getSpecialWall(false);
                    if (object5 != null) {
                        return object5;
                    }

                    if (!this.isBlockedTo(square3)) {
                        object5 = square3.getSpecialSolid();
                        if (object5 != null) {
                            return object5;
                        }

                        object5 = square3.getSpecialWall(true);
                        if (object5 != null) {
                            return object5;
                        }
                    }
                }

                if (square2 != null && !this.isBlockedTo(square2) && square3 != null && !this.isBlockedTo(square3)) {
                    object5 = next.getSpecialWall(false);
                    if (object5 != null) {
                        return object5;
                    } else if (!square2.isBlockedTo(next) && !square3.isBlockedTo(next)) {
                        object5 = next.getSpecialSolid();
                        return object5 != null ? object5 : null;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        } else if (next.x > this.x && next.y > this.y) {
            IsoGridSquare square4 = this.getCell().getGridSquare(this.x, this.y + 1, this.z);
            if (square4 != null) {
                IsoObject object6 = square4.getSpecialWall(true);
                if (object6 != null) {
                    return object6;
                }

                if (!this.isBlockedTo(square4)) {
                    object6 = square4.getSpecialSolid();
                    if (object6 != null) {
                        return object6;
                    }
                }
            }

            IsoGridSquare square5 = this.getCell().getGridSquare(this.x + 1, this.y, this.z);
            if (square5 != null) {
                IsoObject object7 = square5.getSpecialWall(false);
                if (object7 != null) {
                    return object7;
                }

                if (!this.isBlockedTo(square5)) {
                    object7 = square5.getSpecialSolid();
                    if (object7 != null) {
                        return object7;
                    }
                }
            }

            if (square4 != null && !this.isBlockedTo(square4) && square5 != null && !this.isBlockedTo(square5)) {
                IsoObject object8 = next.getSpecialWall(false);
                if (object8 != null) {
                    return object8;
                } else {
                    object8 = next.getSpecialWall(true);
                    if (object8 != null) {
                        return object8;
                    } else if (!square4.isBlockedTo(next) && !square5.isBlockedTo(next)) {
                        object8 = next.getSpecialSolid();
                        return object8 != null ? object8 : null;
                    } else {
                        return null;
                    }
                }
            } else {
                return null;
            }
        } else if (next.x < this.x && next.y > this.y) {
            IsoObject object9 = this.getSpecialWall(false);
            if (object9 != null) {
                return object9;
            } else {
                IsoGridSquare square6 = this.getCell().getGridSquare(this.x, this.y + 1, this.z);
                if (square6 != null) {
                    object9 = square6.getSpecialWall(true);
                    if (object9 != null) {
                        return object9;
                    }

                    if (!this.isBlockedTo(square6)) {
                        object9 = square6.getSpecialSolid();
                        if (object9 != null) {
                            return object9;
                        }
                    }
                }

                IsoGridSquare square7 = this.getCell().getGridSquare(this.x - 1, this.y, this.z);
                if (square7 != null && !this.isBlockedTo(square7)) {
                    object9 = square7.getSpecialSolid();
                    if (object9 != null) {
                        return object9;
                    }
                }

                if (square6 != null && !this.isBlockedTo(square6) && square7 != null && !this.isBlockedTo(square7)) {
                    object9 = next.getSpecialWall(true);
                    if (object9 != null) {
                        return object9;
                    } else if (!square6.isBlockedTo(next) && !square7.isBlockedTo(next)) {
                        object9 = next.getSpecialSolid();
                        return object9 != null ? object9 : null;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    public IsoObject getDoorFrameTo(IsoGridSquare next) {
        ArrayList arrayList = null;
        if (next.y >= this.y && next.x >= this.x) {
            arrayList = next.SpecialObjects;
        } else {
            arrayList = this.SpecialObjects;
        }

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            if (arrayList.get(int0) instanceof IsoDoor) {
                IsoDoor door = (IsoDoor)arrayList.get(int0);
                boolean boolean0 = door.north;
                if (boolean0 && next.y != this.y) {
                    return door;
                }

                if (!boolean0 && next.x != this.x) {
                    return door;
                }
            } else if (arrayList.get(int0) instanceof IsoThumpable && ((IsoThumpable)arrayList.get(int0)).isDoor) {
                IsoThumpable thumpable = (IsoThumpable)arrayList.get(int0);
                boolean boolean1 = thumpable.north;
                if (boolean1 && next.y != this.y) {
                    return thumpable;
                }

                if (!boolean1 && next.x != this.x) {
                    return thumpable;
                }
            }
        }

        return null;
    }

    public static void getSquaresForThread(ArrayDeque<IsoGridSquare> isoGridSquareCacheDest, int count) {
        for (int int0 = 0; int0 < count; int0++) {
            IsoGridSquare square = isoGridSquareCache.poll();
            if (square == null) {
                isoGridSquareCacheDest.add(new IsoGridSquare(null, null, 0, 0, 0));
            } else {
                isoGridSquareCacheDest.add(square);
            }
        }
    }

    public static IsoGridSquare getNew(IsoCell cell, SliceY slice, int _x, int _y, int _z) {
        IsoGridSquare square = isoGridSquareCache.poll();
        if (square == null) {
            return new IsoGridSquare(cell, slice, _x, _y, _z);
        } else {
            square.x = _x;
            square.y = _y;
            square.z = _z;
            square.CachedScreenValue = -1;
            col = 0;
            path = 0;
            pathdoor = 0;
            vision = 0;
            square.collideMatrix = 134217727;
            square.pathMatrix = 134217727;
            square.visionMatrix = 0;
            return square;
        }
    }

    public static IsoGridSquare getNew(ArrayDeque<IsoGridSquare> _isoGridSquareCache, IsoCell cell, SliceY slice, int _x, int _y, int _z) {
        IsoGridSquare square = null;
        if (_isoGridSquareCache.isEmpty()) {
            return new IsoGridSquare(cell, slice, _x, _y, _z);
        } else {
            square = (IsoGridSquare)_isoGridSquareCache.pop();
            square.x = _x;
            square.y = _y;
            square.z = _z;
            square.CachedScreenValue = -1;
            col = 0;
            path = 0;
            pathdoor = 0;
            vision = 0;
            square.collideMatrix = 134217727;
            square.pathMatrix = 134217727;
            square.visionMatrix = 0;
            return square;
        }
    }

    @Deprecated
    public long getHashCodeObjects() {
        this.recalcHashCodeObjects();
        return this.hashCodeObjects;
    }

    @Deprecated
    public int getHashCodeObjectsInt() {
        this.recalcHashCodeObjects();
        return (int)this.hashCodeObjects;
    }

    @Deprecated
    public void recalcHashCodeObjects() {
        long long0 = 0L;
        this.hashCodeObjects = long0;
    }

    @Deprecated
    public int hashCodeNoOverride() {
        int int0 = 0;
        this.recalcHashCodeObjects();
        int0 = int0 * 2 + this.Objects.size();
        int0 = (int)(int0 + this.getHashCodeObjects());

        for (int int1 = 0; int1 < this.Objects.size(); int1++) {
            int0 = int0 * 2 + this.Objects.get(int1).hashCode();
        }

        int int2 = 0;

        for (int int3 = 0; int3 < this.StaticMovingObjects.size(); int3++) {
            if (this.StaticMovingObjects.get(int3) instanceof IsoDeadBody) {
                int2++;
            }
        }

        int0 = int0 * 2 + int2;

        for (int int4 = 0; int4 < this.StaticMovingObjects.size(); int4++) {
            IsoMovingObject movingObject = this.StaticMovingObjects.get(int4);
            if (movingObject instanceof IsoDeadBody) {
                int0 = int0 * 2 + movingObject.hashCode();
            }
        }

        if (this.table != null && !this.table.isEmpty()) {
            int0 = int0 * 2 + this.table.hashCode();
        }

        byte byte0 = 0;
        if (this.isOverlayDone()) {
            byte0 = (byte)(byte0 | 1);
        }

        if (this.haveRoof) {
            byte0 = (byte)(byte0 | 2);
        }

        if (this.burntOut) {
            byte0 = (byte)(byte0 | 4);
        }

        int0 = int0 * 2 + byte0;
        int0 = int0 * 2 + this.getErosionData().hashCode();
        if (this.getTrapPositionX() > 0) {
            int0 = int0 * 2 + this.getTrapPositionX();
            int0 = int0 * 2 + this.getTrapPositionY();
            int0 = int0 * 2 + this.getTrapPositionZ();
        }

        int0 = int0 * 2 + (this.haveElectricity() ? 1 : 0);
        return int0 * 2 + (this.haveSheetRope ? 1 : 0);
    }

    public IsoGridSquare(IsoCell cell, SliceY slice, int _x, int _y, int _z) {
        this.ID = ++IDMax;
        this.x = _x;
        this.y = _y;
        this.z = _z;
        this.CachedScreenValue = -1;
        col = 0;
        path = 0;
        pathdoor = 0;
        vision = 0;
        this.collideMatrix = 134217727;
        this.pathMatrix = 134217727;
        this.visionMatrix = 0;

        for (int int0 = 0; int0 < 4; int0++) {
            if (GameServer.bServer) {
                if (int0 == 0) {
                    this.lighting[int0] = new ServerLOS.ServerLighting();
                }
            } else if (LightingJNI.init) {
                this.lighting[int0] = new LightingJNI.JNILighting(int0, this);
            } else {
                this.lighting[int0] = new IsoGridSquare.Lighting();
            }
        }
    }

    public IsoGridSquare getTileInDirection(IsoDirections directions) {
        if (directions == IsoDirections.N) {
            return this.getCell().getGridSquare(this.x, this.y - 1, this.z);
        } else if (directions == IsoDirections.NE) {
            return this.getCell().getGridSquare(this.x + 1, this.y - 1, this.z);
        } else if (directions == IsoDirections.NW) {
            return this.getCell().getGridSquare(this.x - 1, this.y - 1, this.z);
        } else if (directions == IsoDirections.E) {
            return this.getCell().getGridSquare(this.x + 1, this.y, this.z);
        } else if (directions == IsoDirections.W) {
            return this.getCell().getGridSquare(this.x - 1, this.y, this.z);
        } else if (directions == IsoDirections.SE) {
            return this.getCell().getGridSquare(this.x + 1, this.y + 1, this.z);
        } else if (directions == IsoDirections.SW) {
            return this.getCell().getGridSquare(this.x - 1, this.y + 1, this.z);
        } else {
            return directions == IsoDirections.S ? this.getCell().getGridSquare(this.x, this.y + 1, this.z) : null;
        }
    }

    IsoObject getWall() {
        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            IsoObject object = this.Objects.get(int0);
            if (object != null && object.sprite != null && (object.sprite.cutW || object.sprite.cutN)) {
                return object;
            }
        }

        return null;
    }

    public IsoObject getThumpableWall(boolean bNorth) {
        IsoObject object = this.getWall(bNorth);
        return object != null && object instanceof IsoThumpable ? object : null;
    }

    public IsoObject getHoppableWall(boolean bNorth) {
        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            IsoObject object = this.Objects.get(int0);
            if (object != null && object.sprite != null) {
                PropertyContainer propertyContainer = object.getProperties();
                boolean boolean0 = propertyContainer.Is(IsoFlagType.TallHoppableW) && !propertyContainer.Is(IsoFlagType.WallWTrans);
                boolean boolean1 = propertyContainer.Is(IsoFlagType.TallHoppableN) && !propertyContainer.Is(IsoFlagType.WallNTrans);
                if (boolean0 && !bNorth || boolean1 && bNorth) {
                    return object;
                }
            }
        }

        return null;
    }

    public IsoObject getThumpableWallOrHoppable(boolean bNorth) {
        IsoObject object0 = this.getThumpableWall(bNorth);
        IsoObject object1 = this.getHoppableWall(bNorth);
        if (object0 != null && object1 != null && object0 == object1) {
            return object0;
        } else if (object0 == null && object1 != null) {
            return object1;
        } else {
            return object0 != null && object1 == null ? object0 : null;
        }
    }

    public Boolean getWallFull() {
        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            IsoObject object = this.Objects.get(int0);
            if (object != null
                && object.sprite != null
                && (
                    object.sprite.cutN
                        || object.sprite.cutW
                        || object.sprite.getProperties().Is(IsoFlagType.WallN)
                        || object.sprite.getProperties().Is(IsoFlagType.WallW)
                )) {
                return true;
            }
        }

        return false;
    }

    public IsoObject getWall(boolean bNorth) {
        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            IsoObject object = this.Objects.get(int0);
            if (object != null && object.sprite != null && (object.sprite.cutN && bNorth || object.sprite.cutW && !bNorth)) {
                return object;
            }
        }

        return null;
    }

    public IsoObject getWallSE() {
        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            IsoObject object = this.Objects.get(int0);
            if (object != null && object.sprite != null && object.sprite.getProperties().Is(IsoFlagType.WallSE)) {
                return object;
            }
        }

        return null;
    }

    public IsoObject getFloor() {
        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            IsoObject object = this.Objects.get(int0);
            if (object.sprite != null && object.sprite.getProperties().Is(IsoFlagType.solidfloor)) {
                return object;
            }
        }

        return null;
    }

    public IsoObject getPlayerBuiltFloor() {
        return this.getBuilding() == null && this.roofHideBuilding == null ? this.getFloor() : null;
    }

    public void interpolateLight(ColorInfo inf, float _x, float _y) {
        IsoCell cell = this.getCell();
        if (_x < 0.0F) {
            _x = 0.0F;
        }

        if (_x > 1.0F) {
            _x = 1.0F;
        }

        if (_y < 0.0F) {
            _y = 0.0F;
        }

        if (_y > 1.0F) {
            _y = 1.0F;
        }

        int int0 = IsoCamera.frameState.playerIndex;
        int int1 = this.getVertLight(0, int0);
        int int2 = this.getVertLight(1, int0);
        int int3 = this.getVertLight(2, int0);
        int int4 = this.getVertLight(3, int0);
        tl.fromColor(int1);
        bl.fromColor(int4);
        tr.fromColor(int2);
        br.fromColor(int3);
        tl.interp(tr, _x, interp1);
        bl.interp(br, _x, interp2);
        interp1.interp(interp2, _y, finalCol);
        inf.r = finalCol.r;
        inf.g = finalCol.g;
        inf.b = finalCol.b;
        inf.a = finalCol.a;
    }

    public void EnsureSurroundNotNull() {
        assert !GameServer.bServer;

        for (int int0 = -1; int0 <= 1; int0++) {
            for (int int1 = -1; int1 <= 1; int1++) {
                if ((int0 != 0 || int1 != 0)
                    && IsoWorld.instance.isValidSquare(this.x + int0, this.y + int1, this.z)
                    && this.getCell().getChunkForGridSquare(this.x + int0, this.y + int1, this.z) != null) {
                    IsoGridSquare square1 = this.getCell().getGridSquare(this.x + int0, this.y + int1, this.z);
                    if (square1 == null) {
                        square1 = getNew(this.getCell(), null, this.x + int0, this.y + int1, this.z);
                        IsoGridSquare square2 = this.getCell().ConnectNewSquare(square1, false);
                    }
                }
            }
        }
    }

    public IsoObject addFloor(String sprite) {
        IsoRegions.setPreviousFlags(this);
        IsoObject object0 = new IsoObject(this.getCell(), this, sprite);
        boolean boolean0 = false;

        for (int int0 = 0; int0 < this.getObjects().size(); int0++) {
            IsoObject object1 = this.getObjects().get(int0);
            IsoSprite _sprite = object1.sprite;
            if (_sprite != null
                && (
                    _sprite.getProperties().Is(IsoFlagType.solidfloor)
                        || _sprite.getProperties().Is(IsoFlagType.noStart)
                        || _sprite.getProperties().Is(IsoFlagType.vegitation) && object1.getType() != IsoObjectType.tree
                        || _sprite.getName() != null && _sprite.getName().startsWith("blends_grassoverlays")
                )) {
                if (_sprite.getName() != null && _sprite.getName().startsWith("floors_rugs")) {
                    boolean0 = true;
                } else {
                    this.transmitRemoveItemFromSquare(object1);
                    int0--;
                }
            }
        }

        object0.sprite.getProperties().Set(IsoFlagType.solidfloor);
        if (boolean0) {
            this.getObjects().add(0, object0);
        } else {
            this.getObjects().add(object0);
        }

        this.EnsureSurroundNotNull();
        this.RecalcProperties();
        this.getCell().checkHaveRoof(this.x, this.y);

        for (int int1 = 0; int1 < IsoPlayer.numPlayers; int1++) {
            LosUtil.cachecleared[int1] = true;
        }

        setRecalcLightTime(-1);
        GameTime.getInstance().lightSourceUpdate = 100.0F;
        object0.transmitCompleteItemToServer();
        this.RecalcAllWithNeighbours(true);

        for (int int2 = this.z - 1; int2 > 0; int2--) {
            IsoGridSquare square = this.getCell().getGridSquare(this.x, this.y, int2);
            if (square == null) {
                square = getNew(this.getCell(), null, this.x, this.y, int2);
                this.getCell().ConnectNewSquare(square, false);
            }

            square.EnsureSurroundNotNull();
            square.RecalcAllWithNeighbours(true);
        }

        this.setCachedIsFree(false);
        PolygonalMap2.instance.squareChanged(this);
        IsoGridOcclusionData.SquareChanged();
        IsoRegions.squareChanged(this);
        this.clearWater();
        return object0;
    }

    public IsoThumpable AddStairs(boolean north, int level, String sprite, String pillarSprite, KahluaTable _table) {
        IsoRegions.setPreviousFlags(this);
        this.EnsureSurroundNotNull();
        boolean boolean0 = !this.TreatAsSolidFloor() && !this.HasStairsBelow();
        this.CachedIsFree = false;
        IsoThumpable thumpable0 = new IsoThumpable(this.getCell(), this, sprite, north, _table);
        if (north) {
            if (level == 0) {
                thumpable0.setType(IsoObjectType.stairsBN);
            }

            if (level == 1) {
                thumpable0.setType(IsoObjectType.stairsMN);
            }

            if (level == 2) {
                thumpable0.setType(IsoObjectType.stairsTN);
                thumpable0.sprite.getProperties().Set(north ? IsoFlagType.cutN : IsoFlagType.cutW);
            }
        }

        if (!north) {
            if (level == 0) {
                thumpable0.setType(IsoObjectType.stairsBW);
            }

            if (level == 1) {
                thumpable0.setType(IsoObjectType.stairsMW);
            }

            if (level == 2) {
                thumpable0.setType(IsoObjectType.stairsTW);
                thumpable0.sprite.getProperties().Set(north ? IsoFlagType.cutN : IsoFlagType.cutW);
            }
        }

        this.AddSpecialObject(thumpable0);
        if (boolean0 && level == 2) {
            int int0 = this.z - 1;
            IsoGridSquare square0 = this.getCell().getGridSquare(this.x, this.y, int0);
            if (square0 == null) {
                square0 = new IsoGridSquare(this.getCell(), null, this.x, this.y, int0);
                this.getCell().ConnectNewSquare(square0, true);
            }

            while (int0 >= 0) {
                IsoThumpable thumpable1 = new IsoThumpable(this.getCell(), square0, pillarSprite, north, _table);
                square0.AddSpecialObject(thumpable1);
                thumpable1.transmitCompleteItemToServer();
                if (square0.TreatAsSolidFloor()) {
                    break;
                }

                if (this.getCell().getGridSquare(square0.x, square0.y, --int0) == null) {
                    square0 = new IsoGridSquare(this.getCell(), null, square0.x, square0.y, int0);
                    this.getCell().ConnectNewSquare(square0, true);
                } else {
                    square0 = this.getCell().getGridSquare(square0.x, square0.y, int0);
                }
            }
        }

        if (level == 2) {
            IsoGridSquare square1 = null;
            if (north) {
                if (IsoWorld.instance.isValidSquare(this.x, this.y - 1, this.z + 1)) {
                    square1 = this.getCell().getGridSquare(this.x, this.y - 1, this.z + 1);
                    if (square1 == null) {
                        square1 = new IsoGridSquare(this.getCell(), null, this.x, this.y - 1, this.z + 1);
                        this.getCell().ConnectNewSquare(square1, false);
                    }

                    if (!square1.Properties.Is(IsoFlagType.solidfloor)) {
                        square1.addFloor("carpentry_02_57");
                    }
                }
            } else if (IsoWorld.instance.isValidSquare(this.x - 1, this.y, this.z + 1)) {
                square1 = this.getCell().getGridSquare(this.x - 1, this.y, this.z + 1);
                if (square1 == null) {
                    square1 = new IsoGridSquare(this.getCell(), null, this.x - 1, this.y, this.z + 1);
                    this.getCell().ConnectNewSquare(square1, false);
                }

                if (!square1.Properties.Is(IsoFlagType.solidfloor)) {
                    square1.addFloor("carpentry_02_57");
                }
            }

            square1.getModData().rawset("ConnectedToStairs" + north, true);
            square1 = this.getCell().getGridSquare(this.x, this.y, this.z + 1);
            if (square1 == null) {
                square1 = new IsoGridSquare(this.getCell(), null, this.x, this.y, this.z + 1);
                this.getCell().ConnectNewSquare(square1, false);
            }
        }

        for (int int1 = this.getX() - 1; int1 <= this.getX() + 1; int1++) {
            for (int int2 = this.getY() - 1; int2 <= this.getY() + 1; int2++) {
                for (int int3 = this.getZ() - 1; int3 <= this.getZ() + 1; int3++) {
                    if (IsoWorld.instance.isValidSquare(int1, int2, int3)) {
                        IsoGridSquare square2 = this.getCell().getGridSquare(int1, int2, int3);
                        if (square2 == null) {
                            square2 = new IsoGridSquare(this.getCell(), null, int1, int2, int3);
                            this.getCell().ConnectNewSquare(square2, false);
                        }

                        square2.ReCalculateCollide(this);
                        square2.ReCalculateVisionBlocked(this);
                        square2.ReCalculatePathFind(this);
                        this.ReCalculateCollide(square2);
                        this.ReCalculatePathFind(square2);
                        this.ReCalculateVisionBlocked(square2);
                        square2.CachedIsFree = false;
                    }
                }
            }
        }

        return thumpable0;
    }

    void ReCalculateAll(IsoGridSquare square1) {
        this.ReCalculateAll(square1, cellGetSquare);
    }

    void ReCalculateAll(IsoGridSquare square0, IsoGridSquare.GetSquare getSquare) {
        if (square0 != null && square0 != this) {
            this.SolidFloorCached = false;
            square0.SolidFloorCached = false;
            this.RecalcPropertiesIfNeeded();
            square0.RecalcPropertiesIfNeeded();
            this.ReCalculateCollide(square0, getSquare);
            square0.ReCalculateCollide(this, getSquare);
            this.ReCalculatePathFind(square0, getSquare);
            square0.ReCalculatePathFind(this, getSquare);
            this.ReCalculateVisionBlocked(square0, getSquare);
            square0.ReCalculateVisionBlocked(this, getSquare);
            this.setBlockedGridPointers(getSquare);
            square0.setBlockedGridPointers(getSquare);
        }
    }

    void ReCalculateAll(boolean boolean0, IsoGridSquare square0, IsoGridSquare.GetSquare getSquare) {
        if (square0 != null && square0 != this) {
            this.SolidFloorCached = false;
            square0.SolidFloorCached = false;
            this.RecalcPropertiesIfNeeded();
            if (boolean0) {
                square0.RecalcPropertiesIfNeeded();
            }

            this.ReCalculateCollide(square0, getSquare);
            if (boolean0) {
                square0.ReCalculateCollide(this, getSquare);
            }

            this.ReCalculatePathFind(square0, getSquare);
            if (boolean0) {
                square0.ReCalculatePathFind(this, getSquare);
            }

            this.ReCalculateVisionBlocked(square0, getSquare);
            if (boolean0) {
                square0.ReCalculateVisionBlocked(this, getSquare);
            }

            this.setBlockedGridPointers(getSquare);
            if (boolean0) {
                square0.setBlockedGridPointers(getSquare);
            }
        }
    }

    void ReCalculateMineOnly(IsoGridSquare square1) {
        this.SolidFloorCached = false;
        this.RecalcProperties();
        this.ReCalculateCollide(square1);
        this.ReCalculatePathFind(square1);
        this.ReCalculateVisionBlocked(square1);
        this.setBlockedGridPointers(cellGetSquare);
    }

    public void RecalcAllWithNeighbours(boolean bDoReverse) {
        this.RecalcAllWithNeighbours(bDoReverse, cellGetSquare);
    }

    public void RecalcAllWithNeighbours(boolean bDoReverse, IsoGridSquare.GetSquare getter) {
        this.SolidFloorCached = false;
        this.RecalcPropertiesIfNeeded();

        for (int int0 = this.getX() - 1; int0 <= this.getX() + 1; int0++) {
            for (int int1 = this.getY() - 1; int1 <= this.getY() + 1; int1++) {
                for (int int2 = this.getZ() - 1; int2 <= this.getZ() + 1; int2++) {
                    if (IsoWorld.instance.isValidSquare(int0, int1, int2)) {
                        int int3 = int0 - this.getX();
                        int int4 = int1 - this.getY();
                        int int5 = int2 - this.getZ();
                        if (int3 != 0 || int4 != 0 || int5 != 0) {
                            IsoGridSquare square0 = getter.getGridSquare(int0, int1, int2);
                            if (square0 != null) {
                                square0.DirtySlice();
                                this.ReCalculateAll(bDoReverse, square0, getter);
                            }
                        }
                    }
                }
            }
        }

        IsoWorld.instance.CurrentCell.DoGridNav(this, getter);
        IsoGridSquare square1 = this.nav[IsoDirections.N.index()];
        IsoGridSquare square2 = this.nav[IsoDirections.S.index()];
        IsoGridSquare square3 = this.nav[IsoDirections.W.index()];
        IsoGridSquare square4 = this.nav[IsoDirections.E.index()];
        if (square1 != null && square3 != null) {
            square1.ReCalculateAll(square3, getter);
        }

        if (square1 != null && square4 != null) {
            square1.ReCalculateAll(square4, getter);
        }

        if (square2 != null && square3 != null) {
            square2.ReCalculateAll(square3, getter);
        }

        if (square2 != null && square4 != null) {
            square2.ReCalculateAll(square4, getter);
        }
    }

    public void RecalcAllWithNeighboursMineOnly() {
        this.SolidFloorCached = false;
        this.RecalcProperties();

        for (int int0 = this.getX() - 1; int0 <= this.getX() + 1; int0++) {
            for (int int1 = this.getY() - 1; int1 <= this.getY() + 1; int1++) {
                for (int int2 = this.getZ() - 1; int2 <= this.getZ() + 1; int2++) {
                    if (int2 >= 0) {
                        int int3 = int0 - this.getX();
                        int int4 = int1 - this.getY();
                        int int5 = int2 - this.getZ();
                        if (int3 != 0 || int4 != 0 || int5 != 0) {
                            IsoGridSquare square1 = this.getCell().getGridSquare(int0, int1, int2);
                            if (square1 != null) {
                                square1.DirtySlice();
                                this.ReCalculateMineOnly(square1);
                            }
                        }
                    }
                }
            }
        }
    }

    boolean IsWindow(int int2, int int1, int int0) {
        IsoGridSquare square0 = this.getCell().getGridSquare(this.x + int2, this.y + int1, this.z + int0);
        return this.getWindowTo(square0) != null || this.getWindowThumpableTo(square0) != null;
    }

    void RemoveAllWith(IsoFlagType flagType) {
        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            IsoObject object = this.Objects.get(int0);
            if (object.sprite != null && object.sprite.getProperties().Is(flagType)) {
                this.Objects.remove(object);
                this.SpecialObjects.remove(object);
                int0--;
            }
        }

        this.RecalcAllWithNeighbours(true);
    }

    public boolean hasSupport() {
        IsoGridSquare square0 = this.getCell().getGridSquare(this.x, this.y + 1, this.z);
        IsoGridSquare square2 = this.getCell().getGridSquare(this.x + 1, this.y, this.z);

        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            IsoObject object = this.Objects.get(int0);
            if (object.sprite != null
                && (
                    object.sprite.getProperties().Is(IsoFlagType.solid)
                        || (object.sprite.getProperties().Is(IsoFlagType.cutW) || object.sprite.getProperties().Is(IsoFlagType.cutN))
                            && !object.sprite.Properties.Is(IsoFlagType.halfheight)
                )) {
                return true;
            }
        }

        return square0 != null && square0.Properties.Is(IsoFlagType.cutN) && !square0.Properties.Is(IsoFlagType.halfheight)
            ? true
            : square2 != null && square2.Properties.Is(IsoFlagType.cutW) && !square0.Properties.Is(IsoFlagType.halfheight);
    }

    /**
     * @return the ID
     */
    public Integer getID() {
        return this.ID;
    }

    /**
     * 
     * @param _ID the ID to set
     */
    public void setID(int _ID) {
        this.ID = _ID;
    }

    private int savematrix(boolean[][][] booleans, byte[] bytes, int int3) {
        for (int int0 = 0; int0 <= 2; int0++) {
            for (int int1 = 0; int1 <= 2; int1++) {
                for (int int2 = 0; int2 <= 2; int2++) {
                    bytes[int3] = (byte)(booleans[int0][int1][int2] ? 1 : 0);
                    int3++;
                }
            }
        }

        return int3;
    }

    private int loadmatrix(boolean[][][] booleans, byte[] bytes, int int3) {
        for (int int0 = 0; int0 <= 2; int0++) {
            for (int int1 = 0; int1 <= 2; int1++) {
                for (int int2 = 0; int2 <= 2; int2++) {
                    booleans[int0][int1][int2] = bytes[int3] != 0;
                    int3++;
                }
            }
        }

        return int3;
    }

    private void savematrix(boolean[][][] booleans, ByteBuffer byteBuffer) {
        for (int int0 = 0; int0 <= 2; int0++) {
            for (int int1 = 0; int1 <= 2; int1++) {
                for (int int2 = 0; int2 <= 2; int2++) {
                    byteBuffer.put((byte)(booleans[int0][int1][int2] ? 1 : 0));
                }
            }
        }
    }

    private void loadmatrix(boolean[][][] booleans, ByteBuffer byteBuffer) {
        for (int int0 = 0; int0 <= 2; int0++) {
            for (int int1 = 0; int1 <= 2; int1++) {
                for (int int2 = 0; int2 <= 2; int2++) {
                    booleans[int0][int1][int2] = byteBuffer.get() != 0;
                }
            }
        }
    }

    public void DirtySlice() {
    }

    public void setHourSeenToCurrent() {
        this.hourLastSeen = (int)GameTime.instance.getWorldAgeHours();
    }

    public void splatBlood(int dist, float alpha) {
        alpha *= 2.0F;
        alpha *= 3.0F;
        if (alpha > 1.0F) {
            alpha = 1.0F;
        }

        IsoGridSquare square0 = this;
        IsoGridSquare square1 = this;

        for (int int0 = 0; int0 < dist; int0++) {
            if (square0 != null) {
                square0 = this.getCell().getGridSquare(this.getX(), this.getY() - int0, this.getZ());
            }

            if (square1 != null) {
                square1 = this.getCell().getGridSquare(this.getX() - int0, this.getY(), this.getZ());
            }

            float float0 = 0.0F;
            if (square1 != null && square1.testCollideAdjacent(null, -1, 0, 0)) {
                boolean boolean0 = false;
                boolean boolean1 = false;
                byte byte0 = 0;
                byte byte1 = 0;
                if (square1.getS() != null && square1.getS().testCollideAdjacent(null, -1, 0, 0)) {
                    boolean0 = true;
                }

                if (square1.getN() != null && square1.getN().testCollideAdjacent(null, -1, 0, 0)) {
                    boolean1 = true;
                }

                if (boolean0) {
                    byte0 = -1;
                }

                if (boolean1) {
                    byte1 = 1;
                }

                int int1 = byte1 - byte0;
                boolean boolean2 = false;
                byte byte2 = 0;
                byte byte3 = 0;
                if (int1 > 0 && Rand.Next(2) == 0) {
                    boolean2 = true;
                    if (int1 > 1) {
                        if (Rand.Next(2) == 0) {
                            byte2 = -1;
                            byte3 = 0;
                        } else {
                            byte2 = 0;
                            byte3 = 1;
                        }
                    } else {
                        byte2 = byte0;
                        byte3 = byte1;
                    }
                }

                float float1 = Rand.Next(100) / 300.0F;
                IsoGridSquare square2 = this.getCell().getGridSquare(square1.getX(), square1.getY() + byte2, square1.getZ());
                IsoGridSquare square3 = this.getCell().getGridSquare(square1.getX(), square1.getY() + byte3, square1.getZ());
                if (square2 == null
                    || square3 == null
                    || !square2.Is(IsoFlagType.cutW)
                    || !square3.Is(IsoFlagType.cutW)
                    || square2.getProperties().Is(IsoFlagType.WallSE)
                    || square3.getProperties().Is(IsoFlagType.WallSE)
                    || square2.Is(IsoFlagType.HoppableW)
                    || square3.Is(IsoFlagType.HoppableW)) {
                    boolean2 = false;
                }

                if (boolean2) {
                    int int2 = 24 + Rand.Next(2) * 2;
                    if (Rand.Next(2) == 0) {
                        int2 += 8;
                    }

                    square2.DoSplat("overlay_blood_wall_01_" + (int2 + 1), false, IsoFlagType.cutW, float0, float1, alpha);
                    square3.DoSplat("overlay_blood_wall_01_" + (int2 + 0), false, IsoFlagType.cutW, float0, float1, alpha);
                } else {
                    int int3 = 0;
                    switch (Rand.Next(3)) {
                        case 0:
                            int3 = 0 + Rand.Next(4);
                            break;
                        case 1:
                            int3 = 8 + Rand.Next(4);
                            break;
                        case 2:
                            int3 = 16 + Rand.Next(4);
                    }

                    if (int3 == 17 || int3 == 19) {
                        float1 = 0.0F;
                    }

                    if (square1.Is(IsoFlagType.HoppableW)) {
                        square1.DoSplat("overlay_blood_fence_01_" + int3, false, IsoFlagType.HoppableW, float0, 0.0F, alpha);
                    } else {
                        square1.DoSplat("overlay_blood_wall_01_" + int3, false, IsoFlagType.cutW, float0, float1, alpha);
                    }
                }

                square1 = null;
            }

            if (square0 != null && square0.testCollideAdjacent(null, 0, -1, 0)) {
                boolean boolean3 = false;
                boolean boolean4 = false;
                byte byte4 = 0;
                byte byte5 = 0;
                if (square0.getW() != null && square0.getW().testCollideAdjacent(null, 0, -1, 0)) {
                    boolean3 = true;
                }

                if (square0.getE() != null && square0.getE().testCollideAdjacent(null, 0, -1, 0)) {
                    boolean4 = true;
                }

                if (boolean3) {
                    byte4 = -1;
                }

                if (boolean4) {
                    byte5 = 1;
                }

                int int4 = byte5 - byte4;
                boolean boolean5 = false;
                byte byte6 = 0;
                byte byte7 = 0;
                if (int4 > 0 && Rand.Next(2) == 0) {
                    boolean5 = true;
                    if (int4 > 1) {
                        if (Rand.Next(2) == 0) {
                            byte6 = -1;
                            byte7 = 0;
                        } else {
                            byte6 = 0;
                            byte7 = 1;
                        }
                    } else {
                        byte6 = byte4;
                        byte7 = byte5;
                    }
                }

                float float2 = Rand.Next(100) / 300.0F;
                IsoGridSquare square4 = this.getCell().getGridSquare(square0.getX() + byte6, square0.getY(), square0.getZ());
                IsoGridSquare square5 = this.getCell().getGridSquare(square0.getX() + byte7, square0.getY(), square0.getZ());
                if (square4 == null
                    || square5 == null
                    || !square4.Is(IsoFlagType.cutN)
                    || !square5.Is(IsoFlagType.cutN)
                    || square4.getProperties().Is(IsoFlagType.WallSE)
                    || square5.getProperties().Is(IsoFlagType.WallSE)
                    || square4.Is(IsoFlagType.HoppableN)
                    || square5.Is(IsoFlagType.HoppableN)) {
                    boolean5 = false;
                }

                if (boolean5) {
                    int int5 = 28 + Rand.Next(2) * 2;
                    if (Rand.Next(2) == 0) {
                        int5 += 8;
                    }

                    square4.DoSplat("overlay_blood_wall_01_" + (int5 + 0), false, IsoFlagType.cutN, float0, float2, alpha);
                    square5.DoSplat("overlay_blood_wall_01_" + (int5 + 1), false, IsoFlagType.cutN, float0, float2, alpha);
                } else {
                    int int6 = 0;
                    switch (Rand.Next(3)) {
                        case 0:
                            int6 = 4 + Rand.Next(4);
                            break;
                        case 1:
                            int6 = 12 + Rand.Next(4);
                            break;
                        case 2:
                            int6 = 20 + Rand.Next(4);
                    }

                    if (int6 == 20 || int6 == 22) {
                        float2 = 0.0F;
                    }

                    if (square0.Is(IsoFlagType.HoppableN)) {
                        square0.DoSplat("overlay_blood_fence_01_" + int6, false, IsoFlagType.HoppableN, float0, float2, alpha);
                    } else {
                        square0.DoSplat("overlay_blood_wall_01_" + int6, false, IsoFlagType.cutN, float0, float2, alpha);
                    }
                }

                square0 = null;
            }
        }
    }

    public boolean haveBlood() {
        if (Core.OptionBloodDecals == 0) {
            return false;
        } else {
            for (int int0 = 0; int0 < this.getObjects().size(); int0++) {
                IsoObject object = this.getObjects().get(int0);
                if (object.wallBloodSplats != null && !object.wallBloodSplats.isEmpty()) {
                    return true;
                }
            }

            for (int int1 = 0; int1 < this.getChunk().FloorBloodSplats.size(); int1++) {
                IsoFloorBloodSplat floorBloodSplat = this.getChunk().FloorBloodSplats.get(int1);
                float float0 = floorBloodSplat.x + this.getChunk().wx * 10;
                float float1 = floorBloodSplat.y + this.getChunk().wy * 10;
                if ((int)float0 - 1 <= this.x && (int)float0 + 1 >= this.x && (int)float1 - 1 <= this.y && (int)float1 + 1 >= this.y) {
                    return true;
                }
            }

            return false;
        }
    }

    public void removeBlood(boolean remote, boolean onlyWall) {
        for (int int0 = 0; int0 < this.getObjects().size(); int0++) {
            IsoObject object = this.getObjects().get(int0);
            if (object.wallBloodSplats != null) {
                object.wallBloodSplats.clear();
            }
        }

        if (!onlyWall) {
            for (int int1 = 0; int1 < this.getChunk().FloorBloodSplats.size(); int1++) {
                IsoFloorBloodSplat floorBloodSplat = this.getChunk().FloorBloodSplats.get(int1);
                int int2 = (int)(this.getChunk().wx * 10 + floorBloodSplat.x);
                int int3 = (int)(this.getChunk().wy * 10 + floorBloodSplat.y);
                if (int2 >= this.getX() - 1 && int2 <= this.getX() + 1 && int3 >= this.getY() - 1 && int3 <= this.getY() + 1) {
                    this.getChunk().FloorBloodSplats.remove(int1);
                    int1--;
                }
            }
        }

        if (GameClient.bClient && !remote) {
            ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
            PacketTypes.PacketType.RemoveBlood.doPacket(byteBufferWriter);
            byteBufferWriter.putInt(this.x);
            byteBufferWriter.putInt(this.y);
            byteBufferWriter.putInt(this.z);
            byteBufferWriter.putBoolean(onlyWall);
            PacketTypes.PacketType.RemoveBlood.send(GameClient.connection);
        }
    }

    public void DoSplat(String id, boolean bFlip, IsoFlagType prop, float offX, float offZ, float alpha) {
        for (int int0 = 0; int0 < this.getObjects().size(); int0++) {
            IsoObject object = this.getObjects().get(int0);
            if (object.sprite != null && object.sprite.getProperties().Is(prop) && (!(object instanceof IsoWindow) || !object.isDestroyed())) {
                IsoSprite sprite = IsoSprite.getSprite(IsoSpriteManager.instance, id, 0);
                if (sprite == null) {
                    return;
                }

                if (object.wallBloodSplats == null) {
                    object.wallBloodSplats = new ArrayList<>();
                }

                IsoWallBloodSplat wallBloodSplat = new IsoWallBloodSplat((float)GameTime.getInstance().getWorldAgeHours(), sprite);
                object.wallBloodSplats.add(wallBloodSplat);
            }
        }
    }

    public void ClearTileObjects() {
        this.Objects.clear();
        this.RecalcProperties();
    }

    public void ClearTileObjectsExceptFloor() {
        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            IsoObject object = this.Objects.get(int0);
            if (object.sprite == null || !object.sprite.getProperties().Is(IsoFlagType.solidfloor)) {
                this.Objects.remove(object);
                int0--;
            }
        }

        this.RecalcProperties();
    }

    public int RemoveTileObject(IsoObject obj) {
        IsoRegions.setPreviousFlags(this);
        int int0 = this.Objects.indexOf(obj);
        if (!this.Objects.contains(obj)) {
            int0 = this.SpecialObjects.indexOf(obj);
        }

        if (obj != null && this.Objects.contains(obj)) {
            if (obj.isTableSurface()) {
                for (int int1 = this.Objects.indexOf(obj) + 1; int1 < this.Objects.size(); int1++) {
                    IsoObject object0 = this.Objects.get(int1);
                    if (object0.isTableTopObject() || object0.isTableSurface()) {
                        object0.setRenderYOffset(object0.getRenderYOffset() - obj.getSurfaceOffset());
                        object0.sx = 0.0F;
                        object0.sy = 0.0F;
                    }
                }
            }

            IsoObject object1 = this.getPlayerBuiltFloor();
            if (obj == object1) {
                IsoGridOcclusionData.SquareChanged();
            }

            LuaEventManager.triggerEvent("OnObjectAboutToBeRemoved", obj);
            if (!this.Objects.contains(obj)) {
                throw new IllegalArgumentException("OnObjectAboutToBeRemoved not allowed to remove the object");
            }

            int0 = this.Objects.indexOf(obj);
            obj.removeFromWorld();
            obj.removeFromSquare();

            assert !this.Objects.contains(obj);

            assert !this.SpecialObjects.contains(obj);

            if (!(obj instanceof IsoWorldInventoryObject)) {
                this.RecalcAllWithNeighbours(true);
                this.getCell().checkHaveRoof(this.getX(), this.getY());

                for (int int2 = 0; int2 < IsoPlayer.numPlayers; int2++) {
                    LosUtil.cachecleared[int2] = true;
                }

                setRecalcLightTime(-1);
                GameTime.instance.lightSourceUpdate = 100.0F;
            }

            this.fixPlacedItemRenderOffsets();
        }

        MapCollisionData.instance.squareChanged(this);
        LuaEventManager.triggerEvent("OnTileRemoved", obj);
        PolygonalMap2.instance.squareChanged(this);
        IsoRegions.squareChanged(this, true);
        return int0;
    }

    public int RemoveTileObjectErosionNoRecalc(IsoObject obj) {
        int int0 = this.Objects.indexOf(obj);
        IsoGridSquare square = obj.square;
        obj.removeFromWorld();
        obj.removeFromSquare();
        square.RecalcPropertiesIfNeeded();

        assert !this.Objects.contains(obj);

        assert !this.SpecialObjects.contains(obj);

        return int0;
    }

    public void AddSpecialObject(IsoObject obj) {
        this.AddSpecialObject(obj, -1);
    }

    public void AddSpecialObject(IsoObject obj, int index) {
        if (obj != null) {
            IsoRegions.setPreviousFlags(this);
            index = this.placeWallAndDoorCheck(obj, index);
            if (index != -1 && index >= 0 && index <= this.Objects.size()) {
                this.Objects.add(index, obj);
            } else {
                this.Objects.add(obj);
            }

            this.SpecialObjects.add(obj);
            this.burntOut = false;
            obj.addToWorld();
            if (!GameServer.bServer && !GameClient.bClient) {
                this.restackSheetRope();
            }

            this.RecalcAllWithNeighbours(true);
            this.fixPlacedItemRenderOffsets();
            if (!(obj instanceof IsoWorldInventoryObject)) {
                for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                    LosUtil.cachecleared[int0] = true;
                }

                setRecalcLightTime(-1);
                GameTime.instance.lightSourceUpdate = 100.0F;
                if (obj == this.getPlayerBuiltFloor()) {
                    IsoGridOcclusionData.SquareChanged();
                }
            }

            MapCollisionData.instance.squareChanged(this);
            PolygonalMap2.instance.squareChanged(this);
            IsoRegions.squareChanged(this);
        }
    }

    public void AddTileObject(IsoObject obj) {
        this.AddTileObject(obj, -1);
    }

    public void AddTileObject(IsoObject obj, int index) {
        if (obj != null) {
            IsoRegions.setPreviousFlags(this);
            index = this.placeWallAndDoorCheck(obj, index);
            if (index != -1 && index >= 0 && index <= this.Objects.size()) {
                this.Objects.add(index, obj);
            } else {
                this.Objects.add(obj);
            }

            this.burntOut = false;
            obj.addToWorld();
            this.RecalcAllWithNeighbours(true);
            this.fixPlacedItemRenderOffsets();
            if (!(obj instanceof IsoWorldInventoryObject)) {
                for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                    LosUtil.cachecleared[int0] = true;
                }

                setRecalcLightTime(-1);
                GameTime.instance.lightSourceUpdate = 100.0F;
                if (obj == this.getPlayerBuiltFloor()) {
                    IsoGridOcclusionData.SquareChanged();
                }
            }

            MapCollisionData.instance.squareChanged(this);
            PolygonalMap2.instance.squareChanged(this);
            IsoRegions.squareChanged(this);
        }
    }

    public int placeWallAndDoorCheck(IsoObject obj, int index) {
        int int0 = -1;
        if (obj.sprite != null) {
            IsoObjectType objectType = obj.sprite.getType();
            boolean boolean0 = objectType == IsoObjectType.doorN || objectType == IsoObjectType.doorW;
            boolean boolean1 = !boolean0
                && (
                    obj.sprite.cutW
                        || obj.sprite.cutN
                        || objectType == IsoObjectType.doorFrN
                        || objectType == IsoObjectType.doorFrW
                        || obj.sprite.treatAsWallOrder
                );
            if (boolean1 || boolean0) {
                for (int int1 = 0; int1 < this.Objects.size(); int1++) {
                    IsoObject object = this.Objects.get(int1);
                    objectType = IsoObjectType.MAX;
                    if (object.sprite != null) {
                        objectType = object.sprite.getType();
                        if (boolean1 && (objectType == IsoObjectType.doorN || objectType == IsoObjectType.doorW)) {
                            int0 = int1;
                        }

                        if (boolean0
                            && (
                                objectType == IsoObjectType.doorFrN
                                    || objectType == IsoObjectType.doorFrW
                                    || object.sprite.cutW
                                    || object.sprite.cutN
                                    || object.sprite.treatAsWallOrder
                            )) {
                            int0 = int1;
                        }
                    }
                }

                if (boolean0 && int0 > index) {
                    return int0 + 1;
                }

                if (boolean1 && int0 >= 0 && (int0 < index || index < 0)) {
                    return int0;
                }
            }
        }

        return index;
    }

    public void transmitAddObjectToSquare(IsoObject obj, int index) {
        if (obj != null && !this.Objects.contains(obj)) {
            this.AddTileObject(obj, index);
            if (GameClient.bClient) {
                obj.transmitCompleteItemToServer();
            }

            if (GameServer.bServer) {
                obj.transmitCompleteItemToClients();
            }
        }
    }

    public int transmitRemoveItemFromSquare(IsoObject obj) {
        if (obj != null && this.Objects.contains(obj)) {
            if (GameClient.bClient) {
                try {
                    GameClient.instance.checkAddedRemovedItems(obj);
                } catch (Exception exception) {
                    GameClient.connection.cancelPacket();
                    ExceptionLogger.logException(exception);
                }

                ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
                PacketTypes.PacketType.RemoveItemFromSquare.doPacket(byteBufferWriter);
                byteBufferWriter.putInt(this.getX());
                byteBufferWriter.putInt(this.getY());
                byteBufferWriter.putInt(this.getZ());
                byteBufferWriter.putInt(this.Objects.indexOf(obj));
                PacketTypes.PacketType.RemoveItemFromSquare.send(GameClient.connection);
            }

            return GameServer.bServer ? GameServer.RemoveItemFromMap(obj) : this.RemoveTileObject(obj);
        } else {
            return -1;
        }
    }

    public void transmitRemoveItemFromSquareOnServer(IsoObject obj) {
        if (obj != null && this.Objects.contains(obj)) {
            if (GameServer.bServer) {
                GameServer.RemoveItemFromMap(obj);
            }
        }
    }

    public void transmitModdata() {
        if (GameClient.bClient) {
            ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
            PacketTypes.PacketType.SendModData.doPacket(byteBufferWriter);
            byteBufferWriter.putInt(this.getX());
            byteBufferWriter.putInt(this.getY());
            byteBufferWriter.putInt(this.getZ());

            try {
                this.getModData().save(byteBufferWriter.bb);
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }

            PacketTypes.PacketType.SendModData.send(GameClient.connection);
        } else if (GameServer.bServer) {
            GameServer.loadModData(this);
        }
    }

    public void AddWorldInventoryItem(String String, float _x, float _y, float height, int nbr) {
        for (int int0 = 0; int0 < nbr; int0++) {
            this.AddWorldInventoryItem(String, _x, _y, height);
        }
    }

    public InventoryItem AddWorldInventoryItem(String String, float _x, float _y, float height) {
        InventoryItem item = InventoryItemFactory.CreateItem(String);
        if (item == null) {
            return null;
        } else {
            IsoWorldInventoryObject worldInventoryObject = new IsoWorldInventoryObject(item, this, _x, _y, height);
            item.setAutoAge();
            item.setWorldItem(worldInventoryObject);
            worldInventoryObject.setKeyId(item.getKeyId());
            worldInventoryObject.setName(item.getName());
            this.Objects.add(worldInventoryObject);
            this.WorldObjects.add(worldInventoryObject);
            worldInventoryObject.square.chunk.recalcHashCodeObjects();
            if (GameClient.bClient) {
                worldInventoryObject.transmitCompleteItemToServer();
            }

            if (GameServer.bServer) {
                worldInventoryObject.transmitCompleteItemToClients();
            }

            return item;
        }
    }

    public InventoryItem AddWorldInventoryItem(InventoryItem item, float _x, float _y, float height) {
        return this.AddWorldInventoryItem(item, _x, _y, height, true);
    }

    public InventoryItem AddWorldInventoryItem(InventoryItem item, float _x, float _y, float height, boolean transmit) {
        if (!item.getFullType().contains(".Corpse")) {
            if (item.getFullType().contains(".Generator")) {
                new IsoGenerator(item, IsoWorld.instance.CurrentCell, this);
                return item;
            } else {
                IsoWorldInventoryObject worldInventoryObject = new IsoWorldInventoryObject(item, this, _x, _y, height);
                worldInventoryObject.setName(item.getName());
                worldInventoryObject.setKeyId(item.getKeyId());
                this.Objects.add(worldInventoryObject);
                this.WorldObjects.add(worldInventoryObject);
                worldInventoryObject.square.chunk.recalcHashCodeObjects();
                item.setWorldItem(worldInventoryObject);
                worldInventoryObject.addToWorld();
                if (transmit) {
                    if (GameClient.bClient) {
                        worldInventoryObject.transmitCompleteItemToServer();
                    }

                    if (GameServer.bServer) {
                        worldInventoryObject.transmitCompleteItemToClients();
                    }
                }

                return item;
            }
        } else if (item.byteData == null) {
            IsoZombie zombie0 = new IsoZombie(IsoWorld.instance.CurrentCell);
            zombie0.setDir(IsoDirections.fromIndex(Rand.Next(8)));
            zombie0.getForwardDirection().set(zombie0.dir.ToVector());
            zombie0.setFakeDead(false);
            zombie0.setHealth(0.0F);
            zombie0.upKillCount = false;
            zombie0.setX(this.x + _x);
            zombie0.setY(this.y + _y);
            zombie0.setZ(this.z);
            zombie0.square = this;
            zombie0.current = this;
            zombie0.dressInRandomOutfit();
            zombie0.DoZombieInventory();
            IsoDeadBody deadBody0 = new IsoDeadBody(zombie0, true);
            this.addCorpse(deadBody0, false);
            if (GameServer.bServer) {
                GameServer.sendCorpse(deadBody0);
            }

            return item;
        } else {
            IsoDeadBody deadBody1 = new IsoDeadBody(IsoWorld.instance.CurrentCell);

            try {
                byte byte0 = item.byteData.get();
                byte byte1 = item.byteData.get();
                byte byte2 = item.byteData.get();
                byte byte3 = item.byteData.get();
                int int0 = 56;
                if (byte0 == 87 && byte1 == 86 && byte2 == 69 && byte3 == 82) {
                    int0 = item.byteData.getInt();
                } else {
                    item.byteData.rewind();
                }

                deadBody1.load(item.byteData, int0);
            } catch (IOException iOException) {
                iOException.printStackTrace();
                IsoZombie zombie1 = new IsoZombie(null);
                zombie1.dir = deadBody1.dir;
                zombie1.current = this;
                zombie1.x = deadBody1.x;
                zombie1.y = deadBody1.y;
                zombie1.z = deadBody1.z;
                deadBody1 = new IsoDeadBody(zombie1);
            }

            deadBody1.setX(this.x + _x);
            deadBody1.setY(this.y + _y);
            deadBody1.setZ(this.z);
            deadBody1.square = this;
            this.addCorpse(deadBody1, false);
            if (GameServer.bServer) {
                GameServer.sendCorpse(deadBody1);
            }

            return item;
        }
    }

    public void restackSheetRope() {
        if (this.Is(IsoFlagType.climbSheetW) || this.Is(IsoFlagType.climbSheetN) || this.Is(IsoFlagType.climbSheetE) || this.Is(IsoFlagType.climbSheetS)) {
            for (int int0 = 0; int0 < this.getObjects().size() - 1; int0++) {
                IsoObject object = this.getObjects().get(int0);
                if (object.getProperties() != null
                    && (
                        object.getProperties().Is(IsoFlagType.climbSheetW)
                            || object.getProperties().Is(IsoFlagType.climbSheetN)
                            || object.getProperties().Is(IsoFlagType.climbSheetE)
                            || object.getProperties().Is(IsoFlagType.climbSheetS)
                    )) {
                    if (GameServer.bServer) {
                        this.transmitRemoveItemFromSquare(object);
                        this.Objects.add(object);
                        object.transmitCompleteItemToClients();
                    } else if (!GameClient.bClient) {
                        this.Objects.remove(object);
                        this.Objects.add(object);
                    }
                    break;
                }
            }
        }
    }

    public void Burn() {
        if (!GameServer.bServer && !GameClient.bClient || !ServerOptions.instance.NoFire.getValue()) {
            if (this.getCell() != null) {
                this.BurnWalls(true);
                LuaEventManager.triggerEvent("OnGridBurnt", this);
            }
        }
    }

    public void Burn(boolean explode) {
        if (!GameServer.bServer && !GameClient.bClient || !ServerOptions.instance.NoFire.getValue()) {
            if (this.getCell() != null) {
                this.BurnWalls(explode);
            }
        }
    }

    public void BurnWalls(boolean explode) {
        if (!GameClient.bClient) {
            if (GameServer.bServer && SafeHouse.isSafeHouse(this, null, false) != null) {
                if (ServerOptions.instance.NoFire.getValue()) {
                    return;
                }

                if (!ServerOptions.instance.SafehouseAllowFire.getValue()) {
                    return;
                }
            }

            for (int int0 = 0; int0 < this.SpecialObjects.size(); int0++) {
                IsoObject object0 = this.SpecialObjects.get(int0);
                if (object0 instanceof IsoThumpable && ((IsoThumpable)object0).haveSheetRope()) {
                    ((IsoThumpable)object0).removeSheetRope(null);
                }

                if (object0 instanceof IsoWindow) {
                    if (((IsoWindow)object0).haveSheetRope()) {
                        ((IsoWindow)object0).removeSheetRope(null);
                    }

                    ((IsoWindow)object0).removeSheet(null);
                }

                if (IsoWindowFrame.isWindowFrame(object0) && IsoWindowFrame.haveSheetRope(object0)) {
                    IsoWindowFrame.removeSheetRope(object0, null);
                }

                if (object0 instanceof BarricadeAble) {
                    IsoBarricade barricade0 = ((BarricadeAble)object0).getBarricadeOnSameSquare();
                    IsoBarricade barricade1 = ((BarricadeAble)object0).getBarricadeOnOppositeSquare();
                    if (barricade0 != null) {
                        if (GameServer.bServer) {
                            GameServer.RemoveItemFromMap(barricade0);
                        } else {
                            this.RemoveTileObject(barricade0);
                        }
                    }

                    if (barricade1 != null) {
                        if (GameServer.bServer) {
                            GameServer.RemoveItemFromMap(barricade1);
                        } else {
                            barricade1.getSquare().RemoveTileObject(barricade1);
                        }
                    }
                }
            }

            this.SpecialObjects.clear();
            boolean boolean0 = false;
            if (!this.getProperties().Is(IsoFlagType.burntOut)) {
                byte byte0 = 0;

                for (int int1 = 0; int1 < this.Objects.size(); int1++) {
                    IsoObject object1 = this.Objects.get(int1);
                    boolean boolean1 = false;
                    if (object1.getSprite() != null
                        && object1.getSprite().getName() != null
                        && !object1.getSprite().getProperties().Is(IsoFlagType.water)
                        && !object1.getSprite().getName().contains("_burnt_")) {
                        if (object1 instanceof IsoThumpable && object1.getSprite().burntTile != null) {
                            IsoObject object2 = IsoObject.getNew();
                            object2.setSprite(IsoSpriteManager.instance.getSprite(object1.getSprite().burntTile));
                            object2.setSquare(this);
                            if (GameServer.bServer) {
                                object1.sendObjectChange("replaceWith", "object", object2);
                            }

                            object1.removeFromWorld();
                            this.Objects.set(int1, object2);
                        } else if (object1.getSprite().burntTile != null) {
                            object1.sprite = IsoSpriteManager.instance.getSprite(object1.getSprite().burntTile);
                            object1.RemoveAttachedAnims();
                            if (object1.Children != null) {
                                object1.Children.clear();
                            }

                            object1.transmitUpdatedSpriteToClients();
                            object1.setOverlaySprite(null);
                        } else if (object1.getType() == IsoObjectType.tree) {
                            object1.sprite = IsoSpriteManager.instance.getSprite("fencing_burnt_01_" + (Rand.Next(15, 19) + 1));
                            object1.RemoveAttachedAnims();
                            if (object1.Children != null) {
                                object1.Children.clear();
                            }

                            object1.transmitUpdatedSpriteToClients();
                            object1.setOverlaySprite(null);
                        } else if (!(object1 instanceof IsoTrap)) {
                            if (!(object1 instanceof IsoBarricade) && !(object1 instanceof IsoMannequin)) {
                                if (object1 instanceof IsoGenerator generator) {
                                    if (generator.getFuel() > 0.0F) {
                                        byte0 += 20;
                                    }

                                    if (generator.isActivated()) {
                                        generator.activated = false;
                                        generator.setSurroundingElectricity();
                                        if (GameServer.bServer) {
                                            generator.syncIsoObject(false, (byte)0, null, null);
                                        }
                                    }

                                    if (GameServer.bServer) {
                                        GameServer.RemoveItemFromMap(object1);
                                    } else {
                                        this.RemoveTileObject(object1);
                                    }

                                    int1--;
                                } else {
                                    if (object1.getType() == IsoObjectType.wall
                                        && !object1.getProperties().Is(IsoFlagType.DoorWallW)
                                        && !object1.getProperties().Is(IsoFlagType.DoorWallN)
                                        && !object1.getProperties().Is("WindowN")
                                        && !object1.getProperties().Is(IsoFlagType.WindowW)
                                        && !object1.getSprite().getName().startsWith("walls_exterior_roofs_")
                                        && !object1.getSprite().getName().startsWith("fencing_")
                                        && !object1.getSprite().getName().startsWith("fixtures_railings_")) {
                                        if (object1.getSprite().getProperties().Is(IsoFlagType.collideW)
                                            && !object1.getSprite().getProperties().Is(IsoFlagType.collideN)) {
                                            object1.sprite = IsoSpriteManager.instance.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "0" : "4"));
                                        } else if (object1.getSprite().getProperties().Is(IsoFlagType.collideN)
                                            && !object1.getSprite().getProperties().Is(IsoFlagType.collideW)) {
                                            object1.sprite = IsoSpriteManager.instance.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "1" : "5"));
                                        } else if (object1.getSprite().getProperties().Is(IsoFlagType.collideW)
                                            && object1.getSprite().getProperties().Is(IsoFlagType.collideN)) {
                                            object1.sprite = IsoSpriteManager.instance.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "2" : "6"));
                                        } else if (object1.getProperties().Is(IsoFlagType.WallSE)) {
                                            object1.sprite = IsoSpriteManager.instance.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "3" : "7"));
                                        }
                                    } else {
                                        if (object1 instanceof IsoDoor || object1 instanceof IsoWindow || object1 instanceof IsoCurtain) {
                                            if (GameServer.bServer) {
                                                GameServer.RemoveItemFromMap(object1);
                                            } else {
                                                this.RemoveTileObject(object1);
                                                boolean0 = true;
                                            }

                                            int1--;
                                            continue;
                                        }

                                        if (object1.getProperties().Is(IsoFlagType.WindowW)) {
                                            object1.sprite = IsoSpriteManager.instance.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "8" : "12"));
                                        } else if (object1.getProperties().Is("WindowN")) {
                                            object1.sprite = IsoSpriteManager.instance.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "9" : "13"));
                                        } else if (object1.getProperties().Is(IsoFlagType.DoorWallW)) {
                                            object1.sprite = IsoSpriteManager.instance.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "10" : "14"));
                                        } else if (object1.getProperties().Is(IsoFlagType.DoorWallN)) {
                                            object1.sprite = IsoSpriteManager.instance.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "11" : "15"));
                                        } else if (object1.getSprite().getProperties().Is(IsoFlagType.solidfloor)
                                            && !object1.getSprite().getProperties().Is(IsoFlagType.exterior)) {
                                            object1.sprite = IsoSpriteManager.instance.getSprite("floors_burnt_01_0");
                                        } else {
                                            if (object1 instanceof IsoWaveSignal) {
                                                if (GameServer.bServer) {
                                                    GameServer.RemoveItemFromMap(object1);
                                                } else {
                                                    this.RemoveTileObject(object1);
                                                    boolean0 = true;
                                                }

                                                int1--;
                                                continue;
                                            }

                                            if (object1.getContainer() != null && object1.getContainer().getItems() != null) {
                                                Object object3 = null;

                                                for (int int2 = 0; int2 < object1.getContainer().getItems().size(); int2++) {
                                                    object3 = object1.getContainer().getItems().get(int2);
                                                    if (object3 instanceof Food && ((Food)object3).isAlcoholic()
                                                        || ((InventoryItem)object3).getType().equals("PetrolCan")
                                                        || ((InventoryItem)object3).getType().equals("Bleach")) {
                                                        byte0 += 20;
                                                        if (byte0 > 100) {
                                                            byte0 = 100;
                                                            break;
                                                        }
                                                    }
                                                }

                                                object1.sprite = IsoSpriteManager.instance.getSprite("floors_burnt_01_" + Rand.Next(1, 2));

                                                for (int int3 = 0; int3 < object1.getContainerCount(); int3++) {
                                                    ItemContainer container = object1.getContainerByIndex(int3);
                                                    container.removeItemsFromProcessItems();
                                                    container.removeAllItems();
                                                }

                                                object1.removeAllContainers();
                                                if (object1.getOverlaySprite() != null) {
                                                    object1.setOverlaySprite(null);
                                                }

                                                boolean1 = true;
                                            } else if (object1.getSprite().getProperties().Is(IsoFlagType.solidtrans)
                                                || object1.getSprite().getProperties().Is(IsoFlagType.bed)
                                                || object1.getSprite().getProperties().Is(IsoFlagType.waterPiped)) {
                                                object1.sprite = IsoSpriteManager.instance.getSprite("floors_burnt_01_" + Rand.Next(1, 2));
                                                if (object1.getOverlaySprite() != null) {
                                                    object1.setOverlaySprite(null);
                                                }
                                            } else if (object1.getSprite().getName().startsWith("walls_exterior_roofs_")) {
                                                object1.sprite = IsoSpriteManager.instance
                                                    .getSprite(
                                                        "walls_burnt_roofs_01_"
                                                            + object1.getSprite()
                                                                .getName()
                                                                .substring(
                                                                    object1.getSprite().getName().lastIndexOf("_") + 1, object1.getSprite().getName().length()
                                                                )
                                                    );
                                            } else if (!object1.getSprite().getName().startsWith("roofs_accents")) {
                                                if (object1.getSprite().getName().startsWith("roofs_")) {
                                                    object1.sprite = IsoSpriteManager.instance
                                                        .getSprite(
                                                            "roofs_burnt_01_"
                                                                + object1.getSprite()
                                                                    .getName()
                                                                    .substring(
                                                                        object1.getSprite().getName().lastIndexOf("_") + 1,
                                                                        object1.getSprite().getName().length()
                                                                    )
                                                        );
                                                } else if ((
                                                        object1.getSprite().getName().startsWith("fencing_")
                                                            || object1.getSprite().getName().startsWith("fixtures_railings_")
                                                    )
                                                    && (
                                                        object1.getSprite().getProperties().Is(IsoFlagType.HoppableN)
                                                            || object1.getSprite().getProperties().Is(IsoFlagType.HoppableW)
                                                    )) {
                                                    if (object1.getSprite().getProperties().Is(IsoFlagType.transparentW)
                                                        && !object1.getSprite().getProperties().Is(IsoFlagType.transparentN)) {
                                                        object1.sprite = IsoSpriteManager.instance.getSprite("fencing_burnt_01_0");
                                                    } else if (object1.getSprite().getProperties().Is(IsoFlagType.transparentN)
                                                        && !object1.getSprite().getProperties().Is(IsoFlagType.transparentW)) {
                                                        object1.sprite = IsoSpriteManager.instance.getSprite("fencing_burnt_01_1");
                                                    } else {
                                                        object1.sprite = IsoSpriteManager.instance.getSprite("fencing_burnt_01_2");
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (!boolean1 && !(object1 instanceof IsoThumpable)) {
                                        object1.RemoveAttachedAnims();
                                        object1.transmitUpdatedSpriteToClients();
                                        object1.setOverlaySprite(null);
                                    } else {
                                        IsoObject object4 = IsoObject.getNew();
                                        object4.setSprite(object1.getSprite());
                                        object4.setSquare(this);
                                        if (GameServer.bServer) {
                                            object1.sendObjectChange("replaceWith", "object", object4);
                                        }

                                        this.Objects.set(int1, object4);
                                    }

                                    if (object1.emitter != null) {
                                        object1.emitter.stopAll();
                                        object1.emitter = null;
                                    }
                                }
                            } else {
                                if (GameServer.bServer) {
                                    GameServer.RemoveItemFromMap(object1);
                                } else {
                                    this.Objects.remove(object1);
                                }

                                int1--;
                            }
                        }
                    }
                }

                if (byte0 > 0 && explode) {
                    if (GameServer.bServer) {
                        GameServer.PlayWorldSoundServer("BurnedObjectExploded", false, this, 0.0F, 50.0F, 1.0F, false);
                    } else {
                        SoundManager.instance.PlayWorldSound("BurnedObjectExploded", this, 0.0F, 50.0F, 1.0F, false);
                    }

                    IsoFireManager.explode(this.getCell(), this, byte0);
                }
            }

            if (!boolean0) {
                this.RecalcProperties();
            }

            this.getProperties().Set(IsoFlagType.burntOut);
            this.burntOut = true;
            MapCollisionData.instance.squareChanged(this);
            PolygonalMap2.instance.squareChanged(this);
        }
    }

    public void BurnWallsTCOnly() {
        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            IsoObject object = this.Objects.get(int0);
            if (object.sprite == null) {
            }
        }
    }

    public void BurnTick() {
        if (!GameClient.bClient) {
            for (int int0 = 0; int0 < this.StaticMovingObjects.size(); int0++) {
                IsoMovingObject movingObject = this.StaticMovingObjects.get(int0);
                if (movingObject instanceof IsoDeadBody) {
                    ((IsoDeadBody)movingObject).Burn();
                    if (!this.StaticMovingObjects.contains(movingObject)) {
                        int0--;
                    }
                }
            }
        }
    }

    public boolean CalculateCollide(IsoGridSquare gridSquare, boolean bVision, boolean bPathfind, boolean bIgnoreSolidTrans) {
        return this.CalculateCollide(gridSquare, bVision, bPathfind, bIgnoreSolidTrans, false);
    }

    public boolean CalculateCollide(IsoGridSquare gridSquare, boolean bVision, boolean bPathfind, boolean bIgnoreSolidTrans, boolean bIgnoreSolid) {
        return this.CalculateCollide(gridSquare, bVision, bPathfind, bIgnoreSolidTrans, bIgnoreSolid, cellGetSquare);
    }

    public boolean CalculateCollide(
        IsoGridSquare gridSquare, boolean bVision, boolean bPathfind, boolean bIgnoreSolidTrans, boolean bIgnoreSolid, IsoGridSquare.GetSquare getter
    ) {
        if (gridSquare == null && bPathfind) {
            return true;
        } else if (gridSquare == null) {
            return false;
        } else {
            if (bVision && gridSquare.Properties.Is(IsoFlagType.trans)) {
            }

            boolean boolean0 = false;
            boolean boolean1 = false;
            boolean boolean2 = false;
            boolean boolean3 = false;
            if (gridSquare.x < this.x) {
                boolean0 = true;
            }

            if (gridSquare.y < this.y) {
                boolean2 = true;
            }

            if (gridSquare.x > this.x) {
                boolean1 = true;
            }

            if (gridSquare.y > this.y) {
                boolean3 = true;
            }

            if (!bIgnoreSolid && gridSquare.Properties.Is(IsoFlagType.solid)) {
                return this.Has(IsoObjectType.stairsTW) && !bPathfind && gridSquare.x < this.x && gridSquare.y == this.y && gridSquare.z == this.z
                    ? false
                    : !this.Has(IsoObjectType.stairsTN) || bPathfind || gridSquare.x != this.x || gridSquare.y >= this.y || gridSquare.z != this.z;
            } else {
                if (!bIgnoreSolidTrans && gridSquare.Properties.Is(IsoFlagType.solidtrans)) {
                    if (this.Has(IsoObjectType.stairsTW) && !bPathfind && gridSquare.x < this.x && gridSquare.y == this.y && gridSquare.z == this.z) {
                        return false;
                    }

                    if (this.Has(IsoObjectType.stairsTN) && !bPathfind && gridSquare.x == this.x && gridSquare.y < this.y && gridSquare.z == this.z) {
                        return false;
                    }

                    boolean boolean4 = false;
                    if (gridSquare.Properties.Is(IsoFlagType.windowW) || gridSquare.Properties.Is(IsoFlagType.windowN)) {
                        boolean4 = true;
                    }

                    if (!boolean4 && (gridSquare.Properties.Is(IsoFlagType.WindowW) || gridSquare.Properties.Is(IsoFlagType.WindowN))) {
                        boolean4 = true;
                    }

                    if (!boolean4) {
                        IsoGridSquare square0 = getter.getGridSquare(gridSquare.x, gridSquare.y + 1, this.z);
                        if (square0 != null && (square0.Is(IsoFlagType.windowN) || square0.Is(IsoFlagType.WindowN))) {
                            boolean4 = true;
                        }
                    }

                    if (!boolean4) {
                        IsoGridSquare square1 = getter.getGridSquare(gridSquare.x + 1, gridSquare.y, this.z);
                        if (square1 != null && (square1.Is(IsoFlagType.windowW) || square1.Is(IsoFlagType.WindowW))) {
                            boolean4 = true;
                        }
                    }

                    if (!boolean4) {
                        return true;
                    }
                }

                if (gridSquare.x != this.x && gridSquare.y != this.y && this.z != gridSquare.z && bPathfind) {
                    return true;
                } else if (!bPathfind || gridSquare.z >= this.z || (this.SolidFloorCached ? this.SolidFloor : this.TreatAsSolidFloor())) {
                    if (bPathfind && gridSquare.z == this.z) {
                        if (gridSquare.x > this.x && gridSquare.y == this.y && gridSquare.Properties.Is(IsoFlagType.windowW)) {
                            return false;
                        }

                        if (gridSquare.y > this.y && gridSquare.x == this.x && gridSquare.Properties.Is(IsoFlagType.windowN)) {
                            return false;
                        }

                        if (gridSquare.x < this.x && gridSquare.y == this.y && this.Properties.Is(IsoFlagType.windowW)) {
                            return false;
                        }

                        if (gridSquare.y < this.y && gridSquare.x == this.x && this.Properties.Is(IsoFlagType.windowN)) {
                            return false;
                        }
                    }

                    if (gridSquare.x > this.x && gridSquare.z < this.z && gridSquare.Has(IsoObjectType.stairsTW)) {
                        return false;
                    } else if (gridSquare.y > this.y && gridSquare.z < this.z && gridSquare.Has(IsoObjectType.stairsTN)) {
                        return false;
                    } else {
                        IsoGridSquare square2 = getter.getGridSquare(gridSquare.x, gridSquare.y, gridSquare.z - 1);
                        if (gridSquare.x == this.x
                            || gridSquare.z != this.z
                            || !gridSquare.Has(IsoObjectType.stairsTN)
                            || square2 != null && square2.Has(IsoObjectType.stairsTN) && !bPathfind) {
                            if (gridSquare.y <= this.y
                                || gridSquare.x != this.x
                                || gridSquare.z != this.z
                                || !gridSquare.Has(IsoObjectType.stairsTN)
                                || square2 != null && square2.Has(IsoObjectType.stairsTN) && !bPathfind) {
                                if (gridSquare.x <= this.x
                                    || gridSquare.y != this.y
                                    || gridSquare.z != this.z
                                    || !gridSquare.Has(IsoObjectType.stairsTW)
                                    || square2 != null && square2.Has(IsoObjectType.stairsTW) && !bPathfind) {
                                    if (gridSquare.y == this.y
                                        || gridSquare.z != this.z
                                        || !gridSquare.Has(IsoObjectType.stairsTW)
                                        || square2 != null && square2.Has(IsoObjectType.stairsTW) && !bPathfind) {
                                        if (gridSquare.x != this.x && gridSquare.z == this.z && gridSquare.Has(IsoObjectType.stairsMN)) {
                                            return true;
                                        } else if (gridSquare.y != this.y && gridSquare.z == this.z && gridSquare.Has(IsoObjectType.stairsMW)) {
                                            return true;
                                        } else if (gridSquare.x != this.x && gridSquare.z == this.z && gridSquare.Has(IsoObjectType.stairsBN)) {
                                            return true;
                                        } else if (gridSquare.y != this.y && gridSquare.z == this.z && gridSquare.Has(IsoObjectType.stairsBW)) {
                                            return true;
                                        } else if (gridSquare.x != this.x && gridSquare.z == this.z && this.Has(IsoObjectType.stairsTN)) {
                                            return true;
                                        } else if (gridSquare.y != this.y && gridSquare.z == this.z && this.Has(IsoObjectType.stairsTW)) {
                                            return true;
                                        } else if (gridSquare.x != this.x && gridSquare.z == this.z && this.Has(IsoObjectType.stairsMN)) {
                                            return true;
                                        } else if (gridSquare.y != this.y && gridSquare.z == this.z && this.Has(IsoObjectType.stairsMW)) {
                                            return true;
                                        } else if (gridSquare.x != this.x && gridSquare.z == this.z && this.Has(IsoObjectType.stairsBN)) {
                                            return true;
                                        } else if (gridSquare.y != this.y && gridSquare.z == this.z && this.Has(IsoObjectType.stairsBW)) {
                                            return true;
                                        } else if (gridSquare.y < this.y && gridSquare.x == this.x && gridSquare.z > this.z && this.Has(IsoObjectType.stairsTN)
                                            )
                                         {
                                            return false;
                                        } else if (gridSquare.x < this.x && gridSquare.y == this.y && gridSquare.z > this.z && this.Has(IsoObjectType.stairsTW)
                                            )
                                         {
                                            return false;
                                        } else if (gridSquare.y > this.y
                                            && gridSquare.x == this.x
                                            && gridSquare.z < this.z
                                            && gridSquare.Has(IsoObjectType.stairsTN)) {
                                            return false;
                                        } else if (gridSquare.x > this.x
                                            && gridSquare.y == this.y
                                            && gridSquare.z < this.z
                                            && gridSquare.Has(IsoObjectType.stairsTW)) {
                                            return false;
                                        } else if (gridSquare.z == this.z
                                            && (gridSquare.SolidFloorCached ? !gridSquare.SolidFloor : !gridSquare.TreatAsSolidFloor())
                                            && bPathfind) {
                                            return true;
                                        } else {
                                            if (gridSquare.z == this.z
                                                && (gridSquare.SolidFloorCached ? !gridSquare.SolidFloor : !gridSquare.TreatAsSolidFloor())
                                                && gridSquare.z > 0) {
                                                square2 = getter.getGridSquare(gridSquare.x, gridSquare.y, gridSquare.z - 1);
                                                if (square2 == null) {
                                                    return true;
                                                }
                                            }

                                            if (this.z == gridSquare.z) {
                                                boolean boolean5 = boolean2 && this.Properties.Is(IsoFlagType.collideN);
                                                boolean boolean6 = boolean0 && this.Properties.Is(IsoFlagType.collideW);
                                                boolean boolean7 = boolean3 && gridSquare.Properties.Is(IsoFlagType.collideN);
                                                boolean boolean8 = boolean1 && gridSquare.Properties.Is(IsoFlagType.collideW);
                                                if (boolean5 && bPathfind && this.Properties.Is(IsoFlagType.canPathN)) {
                                                    boolean5 = false;
                                                }

                                                if (boolean6 && bPathfind && this.Properties.Is(IsoFlagType.canPathW)) {
                                                    boolean6 = false;
                                                }

                                                if (boolean7 && bPathfind && gridSquare.Properties.Is(IsoFlagType.canPathN)) {
                                                    boolean7 = false;
                                                }

                                                if (boolean8 && bPathfind && gridSquare.Properties.Is(IsoFlagType.canPathW)) {
                                                    boolean8 = false;
                                                }

                                                if (boolean6 && this.Has(IsoObjectType.stairsTW) && !bPathfind) {
                                                    boolean6 = false;
                                                }

                                                if (boolean5 && this.Has(IsoObjectType.stairsTN) && !bPathfind) {
                                                    boolean5 = false;
                                                }

                                                if (!boolean5 && !boolean6 && !boolean7 && !boolean8) {
                                                    boolean boolean9 = gridSquare.x != this.x && gridSquare.y != this.y;
                                                    if (boolean9) {
                                                        IsoGridSquare square3 = getter.getGridSquare(this.x, gridSquare.y, this.z);
                                                        IsoGridSquare square4 = getter.getGridSquare(gridSquare.x, this.y, this.z);
                                                        if (square3 != null && square3 != this && square3 != gridSquare) {
                                                            square3.RecalcPropertiesIfNeeded();
                                                        }

                                                        if (square4 != null && square4 != this && square4 != gridSquare) {
                                                            square4.RecalcPropertiesIfNeeded();
                                                        }

                                                        if (gridSquare == this
                                                            || square3 == square4
                                                            || square3 == this
                                                            || square4 == this
                                                            || square3 == gridSquare
                                                            || square4 == gridSquare) {
                                                            return true;
                                                        }

                                                        if (gridSquare.x == this.x + 1
                                                            && gridSquare.y == this.y + 1
                                                            && square3 != null
                                                            && square4 != null
                                                            && square3.Is(IsoFlagType.windowN)
                                                            && square4.Is(IsoFlagType.windowW)) {
                                                            return true;
                                                        }

                                                        if (gridSquare.x == this.x - 1
                                                            && gridSquare.y == this.y - 1
                                                            && square3 != null
                                                            && square4 != null
                                                            && square3.Is(IsoFlagType.windowW)
                                                            && square4.Is(IsoFlagType.windowN)) {
                                                            return true;
                                                        }

                                                        if (this.CalculateCollide(square3, bVision, bPathfind, bIgnoreSolidTrans, false, getter)) {
                                                            return true;
                                                        }

                                                        if (this.CalculateCollide(square4, bVision, bPathfind, bIgnoreSolidTrans, false, getter)) {
                                                            return true;
                                                        }

                                                        if (gridSquare.CalculateCollide(square3, bVision, bPathfind, bIgnoreSolidTrans, false, getter)) {
                                                            return true;
                                                        }

                                                        if (gridSquare.CalculateCollide(square4, bVision, bPathfind, bIgnoreSolidTrans, false, getter)) {
                                                            return true;
                                                        }
                                                    }

                                                    return false;
                                                } else {
                                                    return true;
                                                }
                                            } else {
                                                return gridSquare.z >= this.z
                                                    || gridSquare.x != this.x
                                                    || gridSquare.y != this.y
                                                    || (this.SolidFloorCached ? this.SolidFloor : this.TreatAsSolidFloor());
                                            }
                                        }
                                    } else {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                } else {
                    return gridSquare.Has(IsoObjectType.stairsTN) || gridSquare.Has(IsoObjectType.stairsTW);
                }
            }
        }
    }

    public boolean CalculateVisionBlocked(IsoGridSquare gridSquare) {
        return this.CalculateVisionBlocked(gridSquare, cellGetSquare);
    }

    public boolean CalculateVisionBlocked(IsoGridSquare gridSquare, IsoGridSquare.GetSquare getter) {
        if (gridSquare == null) {
            return false;
        } else if (Math.abs(gridSquare.getX() - this.getX()) <= 1 && Math.abs(gridSquare.getY() - this.getY()) <= 1) {
            boolean boolean0 = false;
            boolean boolean1 = false;
            boolean boolean2 = false;
            boolean boolean3 = false;
            if (gridSquare.x < this.x) {
                boolean0 = true;
            }

            if (gridSquare.y < this.y) {
                boolean2 = true;
            }

            if (gridSquare.x > this.x) {
                boolean1 = true;
            }

            if (gridSquare.y > this.y) {
                boolean3 = true;
            }

            if (!gridSquare.Properties.Is(IsoFlagType.trans) && !this.Properties.Is(IsoFlagType.trans)) {
                if (this.z != gridSquare.z) {
                    if (gridSquare.z > this.z) {
                        if ((gridSquare.SolidFloorCached ? gridSquare.SolidFloor : gridSquare.TreatAsSolidFloor())
                            && !gridSquare.getProperties().Is(IsoFlagType.transparentFloor)) {
                            return true;
                        }

                        if (this.Properties.Is(IsoFlagType.noStart)) {
                            return true;
                        }

                        IsoGridSquare square0 = getter.getGridSquare(this.x, this.y, gridSquare.z);
                        if (square0 == null) {
                            return false;
                        }

                        if ((square0.SolidFloorCached ? square0.SolidFloor : square0.TreatAsSolidFloor())
                            && !square0.getProperties().Is(IsoFlagType.transparentFloor)) {
                            return true;
                        }
                    } else {
                        if ((this.SolidFloorCached ? this.SolidFloor : this.TreatAsSolidFloor()) && !this.getProperties().Is(IsoFlagType.transparentFloor)) {
                            return true;
                        }

                        if (this.Properties.Is(IsoFlagType.noStart)) {
                            return true;
                        }

                        IsoGridSquare square1 = getter.getGridSquare(gridSquare.x, gridSquare.y, this.z);
                        if (square1 == null) {
                            return false;
                        }

                        if ((square1.SolidFloorCached ? square1.SolidFloor : square1.TreatAsSolidFloor())
                            && !square1.getProperties().Is(IsoFlagType.transparentFloor)) {
                            return true;
                        }
                    }
                }

                if (gridSquare.x > this.x && gridSquare.Properties.Is(IsoFlagType.transparentW)) {
                    return false;
                } else if (gridSquare.y > this.y && gridSquare.Properties.Is(IsoFlagType.transparentN)) {
                    return false;
                } else if (gridSquare.x < this.x && this.Properties.Is(IsoFlagType.transparentW)) {
                    return false;
                } else if (gridSquare.y < this.y && this.Properties.Is(IsoFlagType.transparentN)) {
                    return false;
                } else if (gridSquare.x > this.x && gridSquare.Properties.Is(IsoFlagType.doorW)) {
                    return false;
                } else if (gridSquare.y > this.y && gridSquare.Properties.Is(IsoFlagType.doorN)) {
                    return false;
                } else if (gridSquare.x < this.x && this.Properties.Is(IsoFlagType.doorW)) {
                    return false;
                } else if (gridSquare.y < this.y && this.Properties.Is(IsoFlagType.doorN)) {
                    return false;
                } else {
                    boolean boolean4 = boolean2 && this.Properties.Is(IsoFlagType.collideN);
                    boolean boolean5 = boolean0 && this.Properties.Is(IsoFlagType.collideW);
                    boolean boolean6 = boolean3 && gridSquare.Properties.Is(IsoFlagType.collideN);
                    boolean boolean7 = boolean1 && gridSquare.Properties.Is(IsoFlagType.collideW);
                    if (!boolean4 && !boolean5 && !boolean6 && !boolean7) {
                        boolean boolean8 = gridSquare.x != this.x && gridSquare.y != this.y;
                        if (!gridSquare.Properties.Is(IsoFlagType.solid) && !gridSquare.Properties.Is(IsoFlagType.blocksight)) {
                            if (boolean8) {
                                IsoGridSquare square2 = getter.getGridSquare(this.x, gridSquare.y, this.z);
                                IsoGridSquare square3 = getter.getGridSquare(gridSquare.x, this.y, this.z);
                                if (square2 != null && square2 != this && square2 != gridSquare) {
                                    square2.RecalcPropertiesIfNeeded();
                                }

                                if (square3 != null && square3 != this && square3 != gridSquare) {
                                    square3.RecalcPropertiesIfNeeded();
                                }

                                if (this.CalculateVisionBlocked(square2)) {
                                    return true;
                                }

                                if (this.CalculateVisionBlocked(square3)) {
                                    return true;
                                }

                                if (gridSquare.CalculateVisionBlocked(square2)) {
                                    return true;
                                }

                                if (gridSquare.CalculateVisionBlocked(square3)) {
                                    return true;
                                }
                            }

                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public IsoGameCharacter FindFriend(IsoGameCharacter g, int range, Stack<IsoGameCharacter> EnemyList) {
        Stack stack = new Stack();

        for (int int0 = 0; int0 < g.getLocalList().size(); int0++) {
            IsoMovingObject movingObject = g.getLocalList().get(int0);
            if (movingObject != g
                && movingObject != g.getFollowingTarget()
                && movingObject instanceof IsoGameCharacter
                && !(movingObject instanceof IsoZombie)
                && !EnemyList.contains(movingObject)) {
                stack.add((IsoGameCharacter)movingObject);
            }
        }

        float float0 = 1000000.0F;
        IsoGameCharacter character0 = null;

        for (IsoGameCharacter character1 : stack) {
            float float1 = 0.0F;
            float1 += Math.abs(this.getX() - character1.getX());
            float1 += Math.abs(this.getY() - character1.getY());
            float1 += Math.abs(this.getZ() - character1.getZ());
            if (float1 < float0) {
                character0 = character1;
                float0 = float1;
            }

            if (character1 == IsoPlayer.getInstance()) {
                character0 = character1;
                float1 = 0.0F;
            }
        }

        return float0 > range ? null : character0;
    }

    public IsoGameCharacter FindEnemy(IsoGameCharacter g, int range, ArrayList<IsoMovingObject> EnemyList, IsoGameCharacter RangeTest, int TestRangeMax) {
        float float0 = 1000000.0F;
        IsoGameCharacter character0 = null;

        for (int int0 = 0; int0 < EnemyList.size(); int0++) {
            IsoGameCharacter character1 = (IsoGameCharacter)EnemyList.get(int0);
            float float1 = 0.0F;
            float1 += Math.abs(this.getX() - character1.getX());
            float1 += Math.abs(this.getY() - character1.getY());
            float1 += Math.abs(this.getZ() - character1.getZ());
            if (float1 < range && float1 < float0 && character1.DistTo(RangeTest) < TestRangeMax) {
                character0 = character1;
                float0 = float1;
            }
        }

        return float0 > range ? null : character0;
    }

    public IsoGameCharacter FindEnemy(IsoGameCharacter g, int range, ArrayList<IsoMovingObject> EnemyList) {
        float float0 = 1000000.0F;
        IsoGameCharacter character0 = null;

        for (int int0 = 0; int0 < EnemyList.size(); int0++) {
            IsoGameCharacter character1 = (IsoGameCharacter)EnemyList.get(int0);
            float float1 = 0.0F;
            float1 += Math.abs(this.getX() - character1.getX());
            float1 += Math.abs(this.getY() - character1.getY());
            float1 += Math.abs(this.getZ() - character1.getZ());
            if (float1 < float0) {
                character0 = character1;
                float0 = float1;
            }
        }

        return float0 > range ? null : character0;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public void RecalcProperties() {
        this.CachedIsFree = false;
        String string0 = null;
        if (this.Properties.Is("waterAmount")) {
            string0 = this.Properties.Val("waterAmount");
        }

        String string1 = null;
        if (this.Properties.Is("fuelAmount")) {
            string1 = this.Properties.Val("fuelAmount");
        }

        if (this.zone == null) {
            this.zone = IsoWorld.instance.MetaGrid.getZoneAt(this.x, this.y, this.z);
        }

        this.Properties.Clear();
        this.hasTypes.clear();
        this.hasTree = false;
        boolean boolean0 = false;
        boolean boolean1 = false;
        boolean boolean2 = false;
        boolean boolean3 = false;
        boolean boolean4 = false;
        boolean boolean5 = false;
        boolean boolean6 = false;
        boolean boolean7 = false;
        int int0 = this.Objects.size();
        IsoObject[] objects = this.Objects.getElements();

        for (int int1 = 0; int1 < int0; int1++) {
            IsoObject object = objects[int1];
            if (object != null) {
                PropertyContainer propertyContainer = object.getProperties();
                if (propertyContainer != null && !propertyContainer.Is(IsoFlagType.blueprint)) {
                    if (object.sprite.forceRender) {
                        boolean7 = true;
                    }

                    if (object.getType() == IsoObjectType.tree) {
                        this.hasTree = true;
                    }

                    this.hasTypes.set(object.getType(), true);
                    this.Properties.AddProperties(propertyContainer);
                    if (propertyContainer.Is(IsoFlagType.water)) {
                        boolean1 = false;
                    } else {
                        if (!boolean1 && propertyContainer.Is(IsoFlagType.solidfloor)) {
                            boolean1 = true;
                        }

                        if (!boolean0 && propertyContainer.Is(IsoFlagType.solidtrans)) {
                            boolean0 = true;
                        }

                        if (!boolean2 && propertyContainer.Is(IsoFlagType.solidfloor) && !propertyContainer.Is(IsoFlagType.transparentFloor)) {
                            boolean2 = true;
                        }
                    }

                    if (!boolean3 && propertyContainer.Is(IsoFlagType.collideN) && !propertyContainer.Is(IsoFlagType.HoppableN)) {
                        boolean3 = true;
                    }

                    if (!boolean4 && propertyContainer.Is(IsoFlagType.collideW) && !propertyContainer.Is(IsoFlagType.HoppableW)) {
                        boolean4 = true;
                    }

                    if (!boolean5 && propertyContainer.Is(IsoFlagType.cutN) && !propertyContainer.Is(IsoFlagType.transparentN)) {
                        boolean5 = true;
                    }

                    if (!boolean6 && propertyContainer.Is(IsoFlagType.cutW) && !propertyContainer.Is(IsoFlagType.transparentW)) {
                        boolean6 = true;
                    }
                }
            }
        }

        if (this.roomID == -1 && !this.haveRoof) {
            this.getProperties().Set(IsoFlagType.exterior);

            try {
                this.getPuddles().bRecalc = true;
            } catch (Exception exception0) {
                exception0.printStackTrace();
            }
        } else {
            this.getProperties().UnSet(IsoFlagType.exterior);

            try {
                this.getPuddles().bRecalc = true;
            } catch (Exception exception1) {
                exception1.printStackTrace();
            }
        }

        if (string0 != null) {
            this.getProperties().Set("waterAmount", string0, false);
        }

        if (string1 != null) {
            this.getProperties().Set("fuelAmount", string1, false);
        }

        if (this.RainDrop != null) {
            this.Properties.Set(IsoFlagType.HasRaindrop);
        }

        if (boolean7) {
            this.Properties.Set(IsoFlagType.forceRender);
        }

        if (this.RainSplash != null) {
            this.Properties.Set(IsoFlagType.HasRainSplashes);
        }

        if (this.burntOut) {
            this.Properties.Set(IsoFlagType.burntOut);
        }

        if (!boolean0 && boolean1 && this.Properties.Is(IsoFlagType.water)) {
            this.Properties.UnSet(IsoFlagType.solidtrans);
        }

        if (boolean2 && this.Properties.Is(IsoFlagType.transparentFloor)) {
            this.Properties.UnSet(IsoFlagType.transparentFloor);
        }

        if (boolean3 && this.Properties.Is(IsoFlagType.HoppableN)) {
            this.Properties.UnSet(IsoFlagType.canPathN);
            this.Properties.UnSet(IsoFlagType.HoppableN);
        }

        if (boolean4 && this.Properties.Is(IsoFlagType.HoppableW)) {
            this.Properties.UnSet(IsoFlagType.canPathW);
            this.Properties.UnSet(IsoFlagType.HoppableW);
        }

        if (boolean5 && this.Properties.Is(IsoFlagType.transparentN)) {
            this.Properties.UnSet(IsoFlagType.transparentN);
        }

        if (boolean6 && this.Properties.Is(IsoFlagType.transparentW)) {
            this.Properties.UnSet(IsoFlagType.transparentW);
        }

        this.propertiesDirty = this.chunk == null || this.chunk.bLoaded;
        if (this.chunk != null) {
            this.chunk.lightCheck[0] = this.chunk.lightCheck[1] = this.chunk.lightCheck[2] = this.chunk.lightCheck[3] = true;
        }

        if (this.chunk != null) {
            this.chunk.physicsCheck = true;
            this.chunk.collision.clear();
        }

        this.isExteriorCache = this.Is(IsoFlagType.exterior);
        this.isSolidFloorCache = this.Is(IsoFlagType.solidfloor);
        this.isVegitationCache = this.Is(IsoFlagType.vegitation);
    }

    public void RecalcPropertiesIfNeeded() {
        if (this.propertiesDirty) {
            this.RecalcProperties();
        }
    }

    public void ReCalculateCollide(IsoGridSquare square) {
        this.ReCalculateCollide(square, cellGetSquare);
    }

    public void ReCalculateCollide(IsoGridSquare square, IsoGridSquare.GetSquare getter) {
        if (1 + (square.x - this.x) < 0 || 1 + (square.y - this.y) < 0 || 1 + (square.z - this.z) < 0) {
            DebugLog.log("ERROR");
        }

        boolean boolean0 = this.CalculateCollide(square, false, false, false, false, getter);
        this.collideMatrix = setMatrixBit(this.collideMatrix, 1 + (square.x - this.x), 1 + (square.y - this.y), 1 + (square.z - this.z), boolean0);
    }

    public void ReCalculatePathFind(IsoGridSquare square) {
        this.ReCalculatePathFind(square, cellGetSquare);
    }

    public void ReCalculatePathFind(IsoGridSquare square, IsoGridSquare.GetSquare getter) {
        boolean boolean0 = this.CalculateCollide(square, false, true, false, false, getter);
        this.pathMatrix = setMatrixBit(this.pathMatrix, 1 + (square.x - this.x), 1 + (square.y - this.y), 1 + (square.z - this.z), boolean0);
    }

    public void ReCalculateVisionBlocked(IsoGridSquare square) {
        this.ReCalculateVisionBlocked(square, cellGetSquare);
    }

    public void ReCalculateVisionBlocked(IsoGridSquare square, IsoGridSquare.GetSquare getter) {
        boolean boolean0 = this.CalculateVisionBlocked(square, getter);
        this.visionMatrix = setMatrixBit(this.visionMatrix, 1 + (square.x - this.x), 1 + (square.y - this.y), 1 + (square.z - this.z), boolean0);
    }

    private static boolean testCollideSpecialObjects(IsoMovingObject movingObject, IsoGridSquare square1, IsoGridSquare square0) {
        for (int int0 = 0; int0 < square0.SpecialObjects.size(); int0++) {
            IsoObject object = square0.SpecialObjects.get(int0);
            if (object.TestCollide(movingObject, square1, square0)) {
                if (object instanceof IsoDoor) {
                    movingObject.setCollidedWithDoor(true);
                } else if (object instanceof IsoThumpable && ((IsoThumpable)object).isDoor) {
                    movingObject.setCollidedWithDoor(true);
                }

                movingObject.setCollidedObject(object);
                return true;
            }
        }

        return false;
    }

    public boolean testCollideAdjacent(IsoMovingObject collideObject, int _x, int _y, int _z) {
        if (collideObject instanceof IsoPlayer && ((IsoPlayer)collideObject).isNoClip()) {
            return false;
        } else if (this.collideMatrix == -1) {
            return true;
        } else if (_x >= -1 && _x <= 1 && _y >= -1 && _y <= 1 && _z >= -1 && _z <= 1) {
            if (this.x + _x >= 0 && this.y + _y >= 0 && IsoWorld.instance.MetaGrid.isValidChunk((this.x + _x) / 10, (this.y + _y) / 10)) {
                IsoGridSquare square0 = this.getCell().getGridSquare(this.x + _x, this.y + _y, this.z + _z);
                SafeHouse safeHouse = null;
                if ((GameServer.bServer || GameClient.bClient)
                    && collideObject instanceof IsoPlayer
                    && !ServerOptions.instance.SafehouseAllowTrepass.getValue()) {
                    IsoGridSquare square1 = this.getCell().getGridSquare(this.x + _x, this.y + _y, 0);
                    safeHouse = SafeHouse.isSafeHouse(square1, ((IsoPlayer)collideObject).getUsername(), true);
                }

                if (safeHouse != null) {
                    return true;
                } else {
                    if (square0 != null && collideObject != null) {
                        IsoObject object = this.testCollideSpecialObjects(square0);
                        if (object != null) {
                            collideObject.collideWith(object);
                            if (object instanceof IsoDoor) {
                                collideObject.setCollidedWithDoor(true);
                            } else if (object instanceof IsoThumpable && ((IsoThumpable)object).isDoor) {
                                collideObject.setCollidedWithDoor(true);
                            }

                            collideObject.setCollidedObject(object);
                            return true;
                        }
                    }

                    if (UseSlowCollision) {
                        return this.CalculateCollide(square0, false, false, false);
                    } else {
                        if (collideObject instanceof IsoPlayer && getMatrixBit(this.collideMatrix, _x + 1, _y + 1, _z + 1)) {
                            this.RecalcAllWithNeighbours(true);
                        }

                        return getMatrixBit(this.collideMatrix, _x + 1, _y + 1, _z + 1);
                    }
                }
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public boolean testCollideAdjacentAdvanced(int _x, int _y, int _z, boolean ignoreDoors) {
        if (this.collideMatrix == -1) {
            return true;
        } else if (_x >= -1 && _x <= 1 && _y >= -1 && _y <= 1 && _z >= -1 && _z <= 1) {
            IsoGridSquare square = this.getCell().getGridSquare(this.x + _x, this.y + _y, this.z + _z);
            if (square != null) {
                if (!square.SpecialObjects.isEmpty()) {
                    for (int int0 = 0; int0 < square.SpecialObjects.size(); int0++) {
                        IsoObject object0 = square.SpecialObjects.get(int0);
                        if (object0.TestCollide(null, this, square)) {
                            return true;
                        }
                    }
                }

                if (!this.SpecialObjects.isEmpty()) {
                    for (int int1 = 0; int1 < this.SpecialObjects.size(); int1++) {
                        IsoObject object1 = this.SpecialObjects.get(int1);
                        if (object1.TestCollide(null, this, square)) {
                            return true;
                        }
                    }
                }
            }

            return UseSlowCollision ? this.CalculateCollide(square, false, false, false) : getMatrixBit(this.collideMatrix, _x + 1, _y + 1, _z + 1);
        } else {
            return true;
        }
    }

    public static void setCollisionMode() {
        UseSlowCollision = !UseSlowCollision;
    }

    public boolean testPathFindAdjacent(IsoMovingObject mover, int _x, int _y, int _z) {
        return this.testPathFindAdjacent(mover, _x, _y, _z, cellGetSquare);
    }

    public boolean testPathFindAdjacent(IsoMovingObject mover, int _x, int _y, int _z, IsoGridSquare.GetSquare getter) {
        if (_x >= -1 && _x <= 1 && _y >= -1 && _y <= 1 && _z >= -1 && _z <= 1) {
            if (this.Has(IsoObjectType.stairsTN) || this.Has(IsoObjectType.stairsTW)) {
                IsoGridSquare square0 = getter.getGridSquare(_x + this.x, _y + this.y, _z + this.z);
                if (square0 == null) {
                    return true;
                }

                if (this.Has(IsoObjectType.stairsTN) && square0.y < this.y && square0.z == this.z) {
                    return true;
                }

                if (this.Has(IsoObjectType.stairsTW) && square0.x < this.x && square0.z == this.z) {
                    return true;
                }
            }

            if (bDoSlowPathfinding) {
                IsoGridSquare square1 = getter.getGridSquare(_x + this.x, _y + this.y, _z + this.z);
                return this.CalculateCollide(square1, false, true, false, false, getter);
            } else {
                return getMatrixBit(this.pathMatrix, _x + 1, _y + 1, _z + 1);
            }
        } else {
            return true;
        }
    }

    public LosUtil.TestResults testVisionAdjacent(int _x, int _y, int _z, boolean specialDiag, boolean bIgnoreDoors) {
        if (_x >= -1 && _x <= 1 && _y >= -1 && _y <= 1 && _z >= -1 && _z <= 1) {
            if (_z == 1 && (_x != 0 || _y != 0) && this.HasElevatedFloor()) {
                IsoGridSquare square0 = this.getCell().getGridSquare(this.x, this.y, this.z + _z);
                if (square0 != null) {
                    return square0.testVisionAdjacent(_x, _y, 0, specialDiag, bIgnoreDoors);
                }
            }

            if (_z == -1 && (_x != 0 || _y != 0)) {
                IsoGridSquare square1 = this.getCell().getGridSquare(this.x + _x, this.y + _y, this.z + _z);
                if (square1 != null && square1.HasElevatedFloor()) {
                    return this.testVisionAdjacent(_x, _y, 0, specialDiag, bIgnoreDoors);
                }
            }

            LosUtil.TestResults testResults0 = LosUtil.TestResults.Clear;
            if (_x != 0 && _y != 0 && specialDiag) {
                testResults0 = this.DoDiagnalCheck(_x, _y, _z, bIgnoreDoors);
                if (testResults0 == LosUtil.TestResults.Clear
                    || testResults0 == LosUtil.TestResults.ClearThroughWindow
                    || testResults0 == LosUtil.TestResults.ClearThroughOpenDoor
                    || testResults0 == LosUtil.TestResults.ClearThroughClosedDoor) {
                    IsoGridSquare square2 = this.getCell().getGridSquare(this.x + _x, this.y + _y, this.z + _z);
                    if (square2 != null) {
                        testResults0 = square2.DoDiagnalCheck(-_x, -_y, -_z, bIgnoreDoors);
                    }
                }

                return testResults0;
            } else {
                IsoGridSquare square3 = this.getCell().getGridSquare(this.x + _x, this.y + _y, this.z + _z);
                LosUtil.TestResults testResults1 = LosUtil.TestResults.Clear;
                if (square3 != null && square3.z == this.z) {
                    if (!this.SpecialObjects.isEmpty()) {
                        for (int int0 = 0; int0 < this.SpecialObjects.size(); int0++) {
                            IsoObject object0 = this.SpecialObjects.get(int0);
                            if (object0 == null) {
                                return LosUtil.TestResults.Clear;
                            }

                            IsoObject.VisionResult visionResult0 = object0.TestVision(this, square3);
                            if (visionResult0 != IsoObject.VisionResult.NoEffect) {
                                if (visionResult0 == IsoObject.VisionResult.Unblocked && object0 instanceof IsoDoor) {
                                    testResults1 = ((IsoDoor)object0).IsOpen()
                                        ? LosUtil.TestResults.ClearThroughOpenDoor
                                        : LosUtil.TestResults.ClearThroughClosedDoor;
                                } else if (visionResult0 == IsoObject.VisionResult.Unblocked
                                    && object0 instanceof IsoThumpable
                                    && ((IsoThumpable)object0).isDoor) {
                                    testResults1 = LosUtil.TestResults.ClearThroughOpenDoor;
                                } else if (visionResult0 == IsoObject.VisionResult.Unblocked && object0 instanceof IsoWindow) {
                                    testResults1 = LosUtil.TestResults.ClearThroughWindow;
                                } else {
                                    if (visionResult0 == IsoObject.VisionResult.Blocked && object0 instanceof IsoDoor && !bIgnoreDoors) {
                                        return LosUtil.TestResults.Blocked;
                                    }

                                    if (visionResult0 == IsoObject.VisionResult.Blocked
                                        && object0 instanceof IsoThumpable
                                        && ((IsoThumpable)object0).isDoor
                                        && !bIgnoreDoors) {
                                        return LosUtil.TestResults.Blocked;
                                    }

                                    if (visionResult0 == IsoObject.VisionResult.Blocked
                                        && object0 instanceof IsoThumpable
                                        && ((IsoThumpable)object0).isWindow()) {
                                        return LosUtil.TestResults.Blocked;
                                    }

                                    if (visionResult0 == IsoObject.VisionResult.Blocked && object0 instanceof IsoCurtain) {
                                        return LosUtil.TestResults.Blocked;
                                    }

                                    if (visionResult0 == IsoObject.VisionResult.Blocked && object0 instanceof IsoWindow) {
                                        return LosUtil.TestResults.Blocked;
                                    }

                                    if (visionResult0 == IsoObject.VisionResult.Blocked && object0 instanceof IsoBarricade) {
                                        return LosUtil.TestResults.Blocked;
                                    }
                                }
                            }
                        }
                    }

                    if (!square3.SpecialObjects.isEmpty()) {
                        for (int int1 = 0; int1 < square3.SpecialObjects.size(); int1++) {
                            IsoObject object1 = square3.SpecialObjects.get(int1);
                            if (object1 == null) {
                                return LosUtil.TestResults.Clear;
                            }

                            IsoObject.VisionResult visionResult1 = object1.TestVision(this, square3);
                            if (visionResult1 != IsoObject.VisionResult.NoEffect) {
                                if (visionResult1 == IsoObject.VisionResult.Unblocked && object1 instanceof IsoDoor) {
                                    testResults1 = ((IsoDoor)object1).IsOpen()
                                        ? LosUtil.TestResults.ClearThroughOpenDoor
                                        : LosUtil.TestResults.ClearThroughClosedDoor;
                                } else if (visionResult1 == IsoObject.VisionResult.Unblocked
                                    && object1 instanceof IsoThumpable
                                    && ((IsoThumpable)object1).isDoor) {
                                    testResults1 = LosUtil.TestResults.ClearThroughOpenDoor;
                                } else if (visionResult1 == IsoObject.VisionResult.Unblocked && object1 instanceof IsoWindow) {
                                    testResults1 = LosUtil.TestResults.ClearThroughWindow;
                                } else {
                                    if (visionResult1 == IsoObject.VisionResult.Blocked && object1 instanceof IsoDoor && !bIgnoreDoors) {
                                        return LosUtil.TestResults.Blocked;
                                    }

                                    if (visionResult1 == IsoObject.VisionResult.Blocked
                                        && object1 instanceof IsoThumpable
                                        && ((IsoThumpable)object1).isDoor
                                        && !bIgnoreDoors) {
                                        return LosUtil.TestResults.Blocked;
                                    }

                                    if (visionResult1 == IsoObject.VisionResult.Blocked
                                        && object1 instanceof IsoThumpable
                                        && ((IsoThumpable)object1).isWindow()) {
                                        return LosUtil.TestResults.Blocked;
                                    }

                                    if (visionResult1 == IsoObject.VisionResult.Blocked && object1 instanceof IsoCurtain) {
                                        return LosUtil.TestResults.Blocked;
                                    }

                                    if (visionResult1 == IsoObject.VisionResult.Blocked && object1 instanceof IsoWindow) {
                                        return LosUtil.TestResults.Blocked;
                                    }

                                    if (visionResult1 == IsoObject.VisionResult.Blocked && object1 instanceof IsoBarricade) {
                                        return LosUtil.TestResults.Blocked;
                                    }
                                }
                            }
                        }
                    }
                }

                return !getMatrixBit(this.visionMatrix, _x + 1, _y + 1, _z + 1) ? testResults1 : LosUtil.TestResults.Blocked;
            }
        } else {
            return LosUtil.TestResults.Blocked;
        }
    }

    public boolean TreatAsSolidFloor() {
        if (this.SolidFloorCached) {
            return this.SolidFloor;
        } else {
            if (!this.Properties.Is(IsoFlagType.solidfloor) && !this.HasStairs()) {
                this.SolidFloor = false;
            } else {
                this.SolidFloor = true;
            }

            this.SolidFloorCached = true;
            return this.SolidFloor;
        }
    }

    public void AddSpecialTileObject(IsoObject obj) {
        this.AddSpecialObject(obj);
    }

    public void renderCharacters(int maxZ, boolean deadRender, boolean doBlendFunc) {
        if (this.z < maxZ) {
            if (!isOnScreenLast) {
            }

            if (doBlendFunc) {
                IndieGL.glBlendFunc(770, 771);
            }

            if (this.MovingObjects.size() > 1) {
                Collections.sort(this.MovingObjects, comp);
            }

            int int0 = IsoCamera.frameState.playerIndex;
            ColorInfo colorInfo = this.lightInfo[int0];
            int int1 = this.StaticMovingObjects.size();

            for (int int2 = 0; int2 < int1; int2++) {
                IsoMovingObject movingObject0 = this.StaticMovingObjects.get(int2);
                if ((movingObject0.sprite != null || movingObject0 instanceof IsoDeadBody)
                    && (!deadRender || movingObject0 instanceof IsoDeadBody && !this.HasStairs())
                    && (deadRender || !(movingObject0 instanceof IsoDeadBody) || this.HasStairs())) {
                    movingObject0.render(movingObject0.getX(), movingObject0.getY(), movingObject0.getZ(), colorInfo, true, false, null);
                }
            }

            int1 = this.MovingObjects.size();

            for (int int3 = 0; int3 < int1; int3++) {
                IsoMovingObject movingObject1 = this.MovingObjects.get(int3);
                if (movingObject1 != null && movingObject1.sprite != null) {
                    boolean boolean0 = movingObject1.bOnFloor;
                    if (boolean0 && movingObject1 instanceof IsoZombie zombie0) {
                        boolean0 = zombie0.isProne();
                        if (!BaseVehicle.RENDER_TO_TEXTURE) {
                            boolean0 = false;
                        }
                    }

                    if ((!deadRender || boolean0) && (deadRender || !boolean0)) {
                        movingObject1.render(movingObject1.getX(), movingObject1.getY(), movingObject1.getZ(), colorInfo, true, false, null);
                    }
                }
            }
        }
    }

    public void renderDeferredCharacters(int maxZ) {
        if (!this.DeferedCharacters.isEmpty()) {
            if (this.DeferredCharacterTick != this.getCell().DeferredCharacterTick) {
                this.DeferedCharacters.clear();
            } else if (this.z >= maxZ) {
                this.DeferedCharacters.clear();
            } else if (PerformanceSettings.LightingFrameSkip != 3) {
                short short0 = this.getCell().getStencilValue2z(this.x, this.y, this.z - 1);
                this.getCell().setStencilValue2z(this.x, this.y, this.z - 1, short0);
                IndieGL.enableAlphaTest();
                IndieGL.glAlphaFunc(516, 0.0F);
                IndieGL.glStencilFunc(519, short0, 127);
                IndieGL.glStencilOp(7680, 7680, 7681);
                float float0 = IsoUtils.XToScreen(this.x, this.y, this.z, 0);
                float float1 = IsoUtils.YToScreen(this.x, this.y, this.z, 0);
                float0 -= IsoCamera.frameState.OffX;
                float1 -= IsoCamera.frameState.OffY;
                IndieGL.glColorMask(false, false, false, false);
                Texture.getWhite().renderwallnw(float0, float1, 64 * Core.TileScale, 32 * Core.TileScale, -1, -1, -1, -1, -1, -1);
                IndieGL.glColorMask(true, true, true, true);
                IndieGL.enableAlphaTest();
                IndieGL.glAlphaFunc(516, 0.0F);
                IndieGL.glStencilFunc(514, short0, 127);
                IndieGL.glStencilOp(7680, 7680, 7680);
                ColorInfo colorInfo = this.lightInfo[IsoCamera.frameState.playerIndex];
                Collections.sort(this.DeferedCharacters, comp);

                for (int int0 = 0; int0 < this.DeferedCharacters.size(); int0++) {
                    IsoGameCharacter character = this.DeferedCharacters.get(int0);
                    if (character.sprite != null) {
                        character.setbDoDefer(false);
                        character.render(character.getX(), character.getY(), character.getZ(), colorInfo, true, false, null);
                        character.renderObjectPicker(character.getX(), character.getY(), character.getZ(), colorInfo);
                        character.setbDoDefer(true);
                    }
                }

                this.DeferedCharacters.clear();
                IndieGL.glAlphaFunc(516, 0.0F);
                IndieGL.glStencilFunc(519, 1, 255);
                IndieGL.glStencilOp(7680, 7680, 7680);
            }
        }
    }

    public void switchLight(boolean active) {
        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            IsoObject object = this.Objects.get(int0);
            if (object instanceof IsoLightSwitch) {
                ((IsoLightSwitch)object).setActive(active);
            }
        }
    }

    public void removeLightSwitch() {
        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            IsoObject object = this.Objects.get(int0);
            if (object instanceof IsoLightSwitch) {
                this.Objects.remove(int0);
                int0--;
            }
        }
    }

    public boolean IsOnScreen() {
        return this.IsOnScreen(false);
    }

    public boolean IsOnScreen(boolean halfTileBorder) {
        if (this.CachedScreenValue != Core.TileScale) {
            this.CachedScreenX = IsoUtils.XToScreen(this.x, this.y, this.z, 0);
            this.CachedScreenY = IsoUtils.YToScreen(this.x, this.y, this.z, 0);
            this.CachedScreenValue = Core.TileScale;
        }

        float float0 = this.CachedScreenX;
        float float1 = this.CachedScreenY;
        float0 -= IsoCamera.frameState.OffX;
        float1 -= IsoCamera.frameState.OffY;
        int int0 = halfTileBorder ? 32 * Core.TileScale : 0;
        if (this.hasTree) {
            int int1 = 384 * Core.TileScale / 2 - 96 * Core.TileScale;
            int int2 = 256 * Core.TileScale - 32 * Core.TileScale;
            if (float0 + int1 <= 0 - int0) {
                return false;
            } else if (float1 + 32 * Core.TileScale <= 0 - int0) {
                return false;
            } else {
                return float0 - int1 >= IsoCamera.frameState.OffscreenWidth + int0 ? false : !(float1 - int2 >= IsoCamera.frameState.OffscreenHeight + int0);
            }
        } else if (float0 + 32 * Core.TileScale <= 0 - int0) {
            return false;
        } else if (float1 + 32 * Core.TileScale <= 0 - int0) {
            return false;
        } else {
            return float0 - 32 * Core.TileScale >= IsoCamera.frameState.OffscreenWidth + int0
                ? false
                : !(float1 - 96 * Core.TileScale >= IsoCamera.frameState.OffscreenHeight + int0);
        }
    }

    void cacheLightInfo() {
        int int0 = IsoCamera.frameState.playerIndex;
        this.lightInfo[int0] = this.lighting[int0].lightInfo();
    }

    public void setLightInfoServerGUIOnly(ColorInfo c) {
        this.lightInfo[0] = c;
    }

    int renderFloor(Shader shader) {
        int int0;
        try {
            IsoGridSquare.s_performance.renderFloor.start();
            int0 = this.renderFloorInternal(shader);
        } finally {
            IsoGridSquare.s_performance.renderFloor.end();
        }

        return int0;
    }

    private int renderFloorInternal(Shader shader) {
        int int0 = IsoCamera.frameState.playerIndex;
        ColorInfo colorInfo = this.lightInfo[int0];
        IsoGridSquare square1 = IsoCamera.frameState.CamCharacterSquare;
        boolean boolean0 = this.lighting[int0].bCouldSee();
        float float0 = this.lighting[int0].darkMulti();
        boolean boolean1 = GameClient.bClient && IsoPlayer.players[int0] != null && IsoPlayer.players[int0].isSeeNonPvpZone();
        boolean boolean2 = Core.bDebug && GameClient.bClient && SafeHouse.isSafeHouse(this, null, true) != null;
        boolean boolean3 = true;
        float float1 = 1.0F;
        float float2 = 1.0F;
        if (square1 != null) {
            int int1 = this.getRoomID();
            if (int1 != -1) {
                int int2 = IsoWorld.instance.CurrentCell.GetEffectivePlayerRoomId();
                if (int2 == -1 && IsoWorld.instance.CurrentCell.CanBuildingSquareOccludePlayer(this, int0)) {
                    boolean3 = false;
                    float1 = 1.0F;
                    float2 = 1.0F;
                } else if (!boolean0 && int1 != int2 && float0 < 0.5F) {
                    boolean3 = false;
                    float1 = 0.0F;
                    float2 = float0 * 2.0F;
                }
            }
        }

        IsoWaterGeometry waterGeometry = this.z == 0 ? this.getWater() : null;
        boolean boolean4 = waterGeometry != null && waterGeometry.bShore;
        float float3 = waterGeometry == null ? 0.0F : waterGeometry.depth[0];
        float float4 = waterGeometry == null ? 0.0F : waterGeometry.depth[3];
        float float5 = waterGeometry == null ? 0.0F : waterGeometry.depth[2];
        float float6 = waterGeometry == null ? 0.0F : waterGeometry.depth[1];
        byte byte0 = 0;
        int int3 = this.Objects.size();
        IsoObject[] objects = this.Objects.getElements();

        for (int int4 = 0; int4 < int3; int4++) {
            IsoObject object0 = objects[int4];
            if (boolean1 && (object0.highlightFlags & 1) == 0) {
                object0.setHighlighted(true);
                if (NonPvpZone.getNonPvpZone(this.x, this.y) != null) {
                    object0.setHighlightColor(0.6F, 0.6F, 1.0F, 0.5F);
                } else {
                    object0.setHighlightColor(1.0F, 0.6F, 0.6F, 0.5F);
                }
            }

            if (boolean2) {
                object0.setHighlighted(true);
                object0.setHighlightColor(1.0F, 0.0F, 0.0F, 1.0F);
            }

            boolean boolean5 = true;
            if (object0.sprite != null && !object0.sprite.solidfloor && object0.sprite.renderLayer != 1) {
                boolean5 = false;
                byte0 |= 4;
            }

            if (object0 instanceof IsoFire || object0 instanceof IsoCarBatteryCharger) {
                boolean5 = false;
                byte0 |= 4;
            }

            if (!boolean5) {
                boolean boolean6 = object0.sprite != null && (object0.sprite.isBush || object0.sprite.canBeRemoved || object0.sprite.attachedFloor);
                if (this.bFlattenGrassEtc && boolean6) {
                    byte0 |= 2;
                }
            } else {
                IndieGL.glAlphaFunc(516, 0.0F);
                object0.setTargetAlpha(int0, float2);
                if (boolean3) {
                    object0.setAlpha(int0, float1);
                }

                if (DebugOptions.instance.Terrain.RenderTiles.RenderGridSquares.getValue() && object0.sprite != null) {
                    IndieGL.StartShader(shader, int0);
                    FloorShaperAttachedSprites floorShaperAttachedSprites = FloorShaperAttachedSprites.instance;
                    Object object1;
                    if (!object0.getProperties().Is(IsoFlagType.diamondFloor) && !object0.getProperties().Is(IsoFlagType.water)) {
                        object1 = FloorShaperDeDiamond.instance;
                    } else {
                        object1 = FloorShaperDiamond.instance;
                    }

                    int int5 = this.getVertLight(0, int0);
                    int int6 = this.getVertLight(1, int0);
                    int int7 = this.getVertLight(2, int0);
                    int int8 = this.getVertLight(3, int0);
                    if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Floor.LightingDebug.getValue()) {
                        int5 = -65536;
                        int6 = -65536;
                        int7 = -16776961;
                        int8 = -16776961;
                    }

                    floorShaperAttachedSprites.setShore(boolean4);
                    floorShaperAttachedSprites.setWaterDepth(float3, float4, float5, float6);
                    floorShaperAttachedSprites.setVertColors(int5, int6, int7, int8);
                    ((FloorShaper)object1).setShore(boolean4);
                    ((FloorShaper)object1).setWaterDepth(float3, float4, float5, float6);
                    ((FloorShaper)object1).setVertColors(int5, int6, int7, int8);
                    object0.renderFloorTile(
                        this.x,
                        this.y,
                        this.z,
                        PerformanceSettings.LightingFrameSkip < 3 ? defColorInfo : colorInfo,
                        true,
                        false,
                        shader,
                        (Consumer<TextureDraw>)object1,
                        floorShaperAttachedSprites
                    );
                    IndieGL.StartShader(null);
                }

                byte0 |= 1;
                if ((object0.highlightFlags & 1) == 0) {
                    byte0 |= 8;
                }

                if ((object0.highlightFlags & 2) != 0) {
                    object0.highlightFlags &= -4;
                }
            }
        }

        if ((this.getCell().rainIntensity > 0 || RainManager.isRaining() && RainManager.RainIntensity > 0.0F)
            && this.isExteriorCache
            && !this.isVegitationCache
            && this.isSolidFloorCache
            && this.isCouldSee(int0)) {
            if (!IsoCamera.frameState.Paused) {
                int int9 = this.getCell().rainIntensity == 0
                    ? (int)Math.min(Math.floor(RainManager.RainIntensity / 0.2F) + 1.0, 5.0)
                    : this.getCell().rainIntensity;
                if (this.splashFrame < 0.0F && Rand.Next(Rand.AdjustForFramerate((int)(5.0F / int9) * 100)) == 0) {
                    this.splashFrame = 0.0F;
                }
            }

            if (this.splashFrame >= 0.0F) {
                int int10 = (int)(this.splashFrame * 4.0F);
                if (rainsplashCache[int10] == null) {
                    rainsplashCache[int10] = "RainSplash_00_" + int10;
                }

                Texture texture = Texture.getSharedTexture(rainsplashCache[int10]);
                if (texture != null) {
                    float float7 = IsoUtils.XToScreen(this.x + this.splashX, this.y + this.splashY, this.z, 0) - IsoCamera.frameState.OffX;
                    float float8 = IsoUtils.YToScreen(this.x + this.splashX, this.y + this.splashY, this.z, 0) - IsoCamera.frameState.OffY;
                    float7 -= texture.getWidth() / 2 * Core.TileScale;
                    float8 -= texture.getHeight() / 2 * Core.TileScale;
                    float float9 = 0.6F * (this.getCell().rainIntensity > 0 ? 1.0F : RainManager.RainIntensity);
                    float float10 = Core.getInstance().RenderShader != null ? 0.6F : 1.0F;
                    SpriteRenderer.instance
                        .render(
                            texture,
                            float7,
                            float8,
                            texture.getWidth() * Core.TileScale,
                            texture.getHeight() * Core.TileScale,
                            0.8F * colorInfo.r,
                            0.9F * colorInfo.g,
                            1.0F * colorInfo.b,
                            float9 * float10,
                            null
                        );
                }

                if (!IsoCamera.frameState.Paused && this.splashFrameNum != IsoCamera.frameState.frameCount) {
                    this.splashFrame = this.splashFrame + 0.08F * (30.0F / PerformanceSettings.getLockFPS());
                    if (this.splashFrame >= 1.0F) {
                        this.splashX = Rand.Next(0.1F, 0.9F);
                        this.splashY = Rand.Next(0.1F, 0.9F);
                        this.splashFrame = -1.0F;
                    }

                    this.splashFrameNum = IsoCamera.frameState.frameCount;
                }
            }
        } else {
            this.splashFrame = -1.0F;
        }

        return byte0;
    }

    private boolean isSpriteOnSouthOrEastWall(IsoObject object) {
        if (object instanceof IsoBarricade) {
            return object.getDir() == IsoDirections.S || object.getDir() == IsoDirections.E;
        } else if (object instanceof IsoCurtain curtain) {
            return curtain.getType() == IsoObjectType.curtainS || curtain.getType() == IsoObjectType.curtainE;
        } else {
            PropertyContainer propertyContainer = object.getProperties();
            return propertyContainer != null && (propertyContainer.Is(IsoFlagType.attachedE) || propertyContainer.Is(IsoFlagType.attachedS));
        }
    }

    public void RenderOpenDoorOnly() {
        int int0 = this.Objects.size();
        IsoObject[] objects = this.Objects.getElements();

        try {
            byte byte0 = 0;
            int int1 = int0 - 1;

            for (int int2 = byte0; int2 <= int1; int2++) {
                IsoObject object = objects[int2];
                if (object.sprite != null
                    && (object.sprite.getProperties().Is(IsoFlagType.attachedN) || object.sprite.getProperties().Is(IsoFlagType.attachedW))) {
                    object.renderFxMask(this.x, this.y, this.z, false);
                }
            }
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
        }
    }

    public boolean RenderMinusFloorFxMask(int maxZ, boolean doSE, boolean vegitationRender) {
        boolean boolean0 = false;
        int int0 = this.Objects.size();
        IsoObject[] objects = this.Objects.getElements();
        long long0 = System.currentTimeMillis();

        try {
            int int1 = doSE ? int0 - 1 : 0;
            int int2 = doSE ? 0 : int0 - 1;

            for (int int3 = int1; doSE ? int3 >= int2 : int3 <= int2; int3 += doSE ? -1 : 1) {
                IsoObject object = objects[int3];
                if (object.sprite != null) {
                    boolean boolean1 = true;
                    IsoObjectType objectType = object.sprite.getType();
                    if (object.sprite.solidfloor || object.sprite.renderLayer == 1) {
                        boolean1 = false;
                    }

                    if (this.z >= maxZ && !object.sprite.alwaysDraw) {
                        boolean1 = false;
                    }

                    boolean boolean2 = object.sprite.isBush || object.sprite.canBeRemoved || object.sprite.attachedFloor;
                    if ((!vegitationRender || boolean2 && this.bFlattenGrassEtc) && (vegitationRender || !boolean2 || !this.bFlattenGrassEtc)) {
                        if ((objectType == IsoObjectType.WestRoofB || objectType == IsoObjectType.WestRoofM || objectType == IsoObjectType.WestRoofT)
                            && this.z == maxZ - 1
                            && this.z == (int)IsoCamera.CamCharacter.getZ()) {
                            boolean1 = false;
                        }

                        if (this.isSpriteOnSouthOrEastWall(object)) {
                            if (!doSE) {
                                boolean1 = false;
                            }

                            boolean0 = true;
                        } else if (doSE) {
                            boolean1 = false;
                        }

                        if (boolean1) {
                            if (!object.sprite.cutW && !object.sprite.cutN) {
                                object.renderFxMask(this.x, this.y, this.z, false);
                            } else {
                                int int4 = IsoCamera.frameState.playerIndex;
                                boolean boolean3 = object.sprite.cutN;
                                boolean boolean4 = object.sprite.cutW;
                                IsoGridSquare square0 = this.nav[IsoDirections.S.index()];
                                IsoGridSquare square1 = this.nav[IsoDirections.E.index()];
                                boolean boolean5 = square0 != null && square0.getPlayerCutawayFlag(int4, long0);
                                boolean boolean6 = this.getPlayerCutawayFlag(int4, long0);
                                boolean boolean7 = square1 != null && square1.getPlayerCutawayFlag(int4, long0);
                                IsoDirections directions;
                                if (boolean3 && boolean4) {
                                    directions = IsoDirections.NW;
                                } else if (boolean3) {
                                    directions = IsoDirections.N;
                                } else if (boolean4) {
                                    directions = IsoDirections.W;
                                } else {
                                    directions = IsoDirections.W;
                                }

                                this.DoCutawayShaderSprite(object.sprite, directions, boolean5, boolean6, boolean7);
                            }
                        }
                    }
                }
            }
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
        }

        return boolean0;
    }

    private boolean isWindowOrWindowFrame(IsoObject object, boolean boolean0) {
        if (object != null && object.sprite != null) {
            if (boolean0 && object.sprite.getProperties().Is(IsoFlagType.windowN)) {
                return true;
            } else if (!boolean0 && object.sprite.getProperties().Is(IsoFlagType.windowW)) {
                return true;
            } else {
                IsoThumpable thumpable = Type.tryCastTo(object, IsoThumpable.class);
                return thumpable != null && thumpable.isWindow() ? boolean0 == thumpable.getNorth() : IsoWindowFrame.isWindowFrame(object, boolean0);
            }
        } else {
            return false;
        }
    }

    boolean renderMinusFloor(int int0, boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4, boolean boolean5, Shader shader) {
        boolean boolean0 = false;
        if (!this.localTemporaryObjects.isEmpty()) {
            boolean0 = this.renderMinusFloor(this.localTemporaryObjects, int0, boolean1, boolean2, boolean3, boolean4, boolean5, shader);
        }

        return this.renderMinusFloor(this.Objects, int0, boolean1, boolean2, boolean3, boolean4, boolean5, shader) || boolean0;
    }

    boolean renderMinusFloor(
        PZArrayList<IsoObject> pZArrayList,
        int int8,
        boolean boolean4,
        boolean boolean13,
        boolean boolean11,
        boolean boolean9,
        boolean boolean10,
        Shader shader
    ) {
        if (!DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.RenderMinusFloor.getValue()) {
            return false;
        } else {
            IndieGL.glBlendFunc(770, 771);
            int int0 = 0;
            isOnScreenLast = this.IsOnScreen();
            int int1 = IsoCamera.frameState.playerIndex;
            IsoGridSquare square1 = IsoCamera.frameState.CamCharacterSquare;
            ColorInfo colorInfo = this.lightInfo[int1];
            boolean boolean0 = this.lighting[int1].bCouldSee();
            float float0 = this.lighting[int1].darkMulti();
            boolean boolean1 = IsoWorld.instance.CurrentCell.CanBuildingSquareOccludePlayer(this, int1);
            colorInfo.a = 1.0F;
            defColorInfo.r = 1.0F;
            defColorInfo.g = 1.0F;
            defColorInfo.b = 1.0F;
            defColorInfo.a = 1.0F;
            if (Core.bDebug && DebugOptions.instance.DebugDraw_SkipWorldShading.getValue()) {
                colorInfo = defColorInfo;
            }

            float float1 = this.CachedScreenX - IsoCamera.frameState.OffX;
            float float2 = this.CachedScreenY - IsoCamera.frameState.OffY;
            boolean boolean2 = true;
            IsoCell cell = this.getCell();
            if (float1 + 32 * Core.TileScale <= cell.StencilX1
                || float1 - 32 * Core.TileScale >= cell.StencilX2
                || float2 + 32 * Core.TileScale <= cell.StencilY1
                || float2 - 96 * Core.TileScale >= cell.StencilY2) {
                boolean2 = false;
            }

            boolean boolean3 = false;
            int int2 = pZArrayList.size();
            IsoObject[] objects = (IsoObject[])pZArrayList.getElements();
            tempWorldInventoryObjects.clear();
            int int3 = boolean4 ? int2 - 1 : 0;
            int int4 = boolean4 ? 0 : int2 - 1;
            boolean boolean5 = false;
            boolean boolean6 = false;
            boolean boolean7 = false;
            boolean boolean8 = false;
            if (!boolean4) {
                for (int int5 = int3; int5 <= int4; int5++) {
                    IsoObject object0 = objects[int5];
                    if (this.isWindowOrWindowFrame(object0, true) && (boolean9 || boolean10)) {
                        IsoGridSquare square2 = this.nav[IsoDirections.N.index()];
                        boolean7 = boolean0 || square2 != null && square2.isCouldSee(int1);
                    }

                    if (this.isWindowOrWindowFrame(object0, false) && (boolean9 || boolean11)) {
                        IsoGridSquare square3 = this.nav[IsoDirections.W.index()];
                        boolean8 = boolean0 || square3 != null && square3.isCouldSee(int1);
                    }

                    if (object0.sprite != null
                        && (object0.sprite.getType() == IsoObjectType.doorFrN || object0.sprite.getType() == IsoObjectType.doorN)
                        && (boolean9 || boolean10)) {
                        IsoGridSquare square4 = this.nav[IsoDirections.N.index()];
                        boolean5 = boolean0 || square4 != null && square4.isCouldSee(int1);
                    }

                    if (object0.sprite != null
                        && (object0.sprite.getType() == IsoObjectType.doorFrW || object0.sprite.getType() == IsoObjectType.doorW)
                        && (boolean9 || boolean11)) {
                        IsoGridSquare square5 = this.nav[IsoDirections.W.index()];
                        boolean6 = boolean0 || square5 != null && square5.isCouldSee(int1);
                    }
                }
            }

            int int6 = IsoWorld.instance.CurrentCell.GetEffectivePlayerRoomId();
            bWallCutawayN = false;
            bWallCutawayW = false;

            for (int int7 = int3; boolean4 ? int7 >= int4 : int7 <= int4; int7 += boolean4 ? -1 : 1) {
                IsoObject object1 = objects[int7];
                boolean boolean12 = true;
                IsoObjectType objectType = IsoObjectType.MAX;
                if (object1.sprite != null) {
                    objectType = object1.sprite.getType();
                }

                CircleStencil = false;
                if (object1.sprite != null && (object1.sprite.solidfloor || object1.sprite.renderLayer == 1)) {
                    boolean12 = false;
                }

                if (object1 instanceof IsoFire) {
                    boolean12 = !boolean13;
                }

                if (this.z >= int8 && (object1.sprite == null || !object1.sprite.alwaysDraw)) {
                    boolean12 = false;
                }

                boolean boolean14 = object1.sprite != null && (object1.sprite.isBush || object1.sprite.canBeRemoved || object1.sprite.attachedFloor);
                if ((!boolean13 || boolean14 && this.bFlattenGrassEtc) && (boolean13 || !boolean14 || !this.bFlattenGrassEtc)) {
                    if (object1.sprite != null
                        && (objectType == IsoObjectType.WestRoofB || objectType == IsoObjectType.WestRoofM || objectType == IsoObjectType.WestRoofT)
                        && this.z == int8 - 1
                        && this.z == (int)IsoCamera.CamCharacter.getZ()) {
                        boolean12 = false;
                    }

                    boolean boolean15 = objectType == IsoObjectType.doorFrW
                        || objectType == IsoObjectType.doorW
                        || object1.sprite != null && object1.sprite.cutW;
                    boolean boolean16 = objectType == IsoObjectType.doorFrN
                        || objectType == IsoObjectType.doorN
                        || object1.sprite != null && object1.sprite.cutN;
                    boolean boolean17 = object1 instanceof IsoDoor && ((IsoDoor)object1).open
                        || object1 instanceof IsoThumpable && ((IsoThumpable)object1).open;
                    boolean boolean18 = object1.container != null;
                    boolean boolean19 = object1.sprite != null && object1.sprite.getProperties().Is(IsoFlagType.waterPiped);
                    if (object1.sprite != null
                        && objectType == IsoObjectType.MAX
                        && !(object1 instanceof IsoDoor)
                        && !(object1 instanceof IsoWindow)
                        && !boolean18
                        && !boolean19) {
                        if (boolean15 || !object1.sprite.getProperties().Is(IsoFlagType.attachedW) || !boolean1 && !boolean11 && !boolean9) {
                            if (!boolean16 && object1.sprite.getProperties().Is(IsoFlagType.attachedN) && (boolean1 || boolean9 || boolean10)) {
                                boolean12 = !bWallCutawayN;
                            }
                        } else {
                            boolean12 = !bWallCutawayW;
                        }
                    }

                    if (object1.sprite != null && !object1.sprite.solidfloor && IsoPlayer.getInstance().isClimbing()) {
                        boolean12 = true;
                    }

                    if (this.isSpriteOnSouthOrEastWall(object1)) {
                        if (!boolean4) {
                            boolean12 = false;
                        }

                        boolean3 = true;
                    } else if (boolean4) {
                        boolean12 = false;
                    }

                    if (boolean12) {
                        IndieGL.glAlphaFunc(516, 0.0F);
                        object1.bAlphaForced = false;
                        if (boolean17) {
                            object1.setTargetAlpha(int1, 0.6F);
                            object1.setAlpha(int1, 0.6F);
                        }

                        if (object1.sprite == null || !boolean15 && !boolean16) {
                            if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Objects.getValue()) {
                                if (this.getRoomID() != -1
                                    && this.getRoomID() != int6
                                    && IsoPlayer.players[int1].isSeatedInVehicle()
                                    && IsoPlayer.players[int1].getVehicle().getCurrentSpeedKmHour() >= 50.0F) {
                                    break;
                                }

                                if (objectType != IsoObjectType.WestRoofB && objectType != IsoObjectType.WestRoofM && objectType != IsoObjectType.WestRoofT
                                    || (
                                            this.getRoomID() != -1
                                                || !boolean10 && !boolean11
                                                || (!(IsoPlayer.players[int1].getX() < this.x) || !(IsoPlayer.players[int1].getY() < this.y))
                                                    && !(IsoPlayer.players[int1].getZ() < this.z)
                                        )
                                        && (
                                            this.getRoomID() == -1 && int6 == -1
                                                || !(IsoPlayer.players[int1].getX() + IsoPlayer.players[int1].getY() <= this.x + this.y + 6)
                                                || !(IsoPlayer.players[int1].getZ() < this.z + 1)
                                        )) {
                                    if (square1 != null && !boolean0 && this.getRoomID() != int6 && float0 < 0.5F) {
                                        object1.setTargetAlpha(int1, float0 * 2.0F);
                                    } else {
                                        if (!boolean17) {
                                            object1.setTargetAlpha(int1, 1.0F);
                                        }

                                        if (IsoPlayer.getInstance() != null
                                                && object1.getProperties() != null
                                                && (
                                                    object1.getProperties().Is(IsoFlagType.solid)
                                                        || object1.getProperties().Is(IsoFlagType.solidtrans)
                                                        || object1.getProperties().Is(IsoFlagType.attachedCeiling)
                                                        || object1.getSprite().getProperties().Is(IsoFlagType.attachedE)
                                                        || object1.getSprite().getProperties().Is(IsoFlagType.attachedS)
                                                )
                                            || objectType.index() > 2 && objectType.index() < 9 && IsoCamera.frameState.CamCharacterZ <= object1.getZ()) {
                                            byte byte0 = 2;
                                            float float3 = 0.75F;
                                            if (objectType.index() > 2 && objectType.index() < 9
                                                || object1.getSprite().getProperties().Is(IsoFlagType.attachedE)
                                                || object1.getSprite().getProperties().Is(IsoFlagType.attachedS)) {
                                                byte0 = 4;
                                                if (objectType.index() > 2 && objectType.index() < 9) {
                                                    float3 = 0.5F;
                                                }
                                            }

                                            int int9 = this.getX() - (int)IsoPlayer.getInstance().getX();
                                            int int10 = this.getY() - (int)IsoPlayer.getInstance().getY();
                                            if (int9 > 0 && int9 < byte0 && int10 >= 0 && int10 < byte0
                                                || int10 > 0 && int10 < byte0 && int9 >= 0 && int9 < byte0) {
                                                object1.setTargetAlpha(int1, float3);
                                            }

                                            IsoZombie zombie0 = IsoCell.getInstance().getNearestVisibleZombie(int1);
                                            if (zombie0 != null && zombie0.getCurrentSquare() != null && zombie0.getCurrentSquare().isCanSee(int1)) {
                                                int int11 = this.getX() - (int)zombie0.x;
                                                int int12 = this.getY() - (int)zombie0.y;
                                                if (int11 > 0 && int11 < byte0 && int12 >= 0 && int12 < byte0
                                                    || int12 > 0 && int12 < byte0 && int11 >= 0 && int11 < byte0) {
                                                    object1.setTargetAlpha(int1, float3);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    object1.setTargetAlpha(int1, 0.0F);
                                }

                                if (object1 instanceof IsoWindow window) {
                                    if (object1.getTargetAlpha(int1) < 1.0E-4F) {
                                        IsoGridSquare square6 = window.getOppositeSquare();
                                        if (square6 != null && square6 != this && square6.lighting[int1].bSeen()) {
                                            object1.setTargetAlpha(int1, square6.lighting[int1].darkMulti() * 2.0F);
                                        }
                                    }

                                    if (object1.getTargetAlpha(int1) > 0.4F
                                        && boolean9
                                        && (
                                            boolean10 && object1.sprite.getProperties().Is(IsoFlagType.windowN)
                                                || boolean11 && object1.sprite.getProperties().Is(IsoFlagType.windowW)
                                        )) {
                                        float float4 = 0.4F;
                                        float float5 = 0.1F;
                                        IsoPlayer player = IsoPlayer.players[int1];
                                        if (player != null) {
                                            float float6 = 5.0F;
                                            float float7 = Math.abs(player.x - this.x) * Math.abs(player.x - this.x)
                                                + Math.abs(player.y - this.y) * Math.abs(player.y - this.y);
                                            float float8 = float4 * (float)(1.0 - Math.sqrt(float7 / float6));
                                            object1.setTargetAlpha(int1, Math.max(float8, float5));
                                        } else {
                                            object1.setTargetAlpha(int1, float5);
                                        }

                                        if (boolean10) {
                                            bWallCutawayN = true;
                                        } else {
                                            bWallCutawayW = true;
                                        }
                                    }
                                }

                                if (object1 instanceof IsoTree) {
                                    if (boolean2
                                        && this.x >= (int)IsoCamera.frameState.CamCharacterX
                                        && this.y >= (int)IsoCamera.frameState.CamCharacterY
                                        && square1 != null
                                        && square1.Is(IsoFlagType.exterior)) {
                                        ((IsoTree)object1).bRenderFlag = true;
                                        object1.setTargetAlpha(int1, Math.min(0.99F, object1.getTargetAlpha(int1)));
                                    } else {
                                        ((IsoTree)object1).bRenderFlag = false;
                                    }
                                }

                                IsoWorldInventoryObject worldInventoryObject0 = Type.tryCastTo(object1, IsoWorldInventoryObject.class);
                                if (worldInventoryObject0 != null) {
                                    tempWorldInventoryObjects.add(worldInventoryObject0);
                                }

                                object1.render(this.x, this.y, this.z, colorInfo, true, false, null);
                            }
                        } else if (PerformanceSettings.LightingFrameSkip < 3) {
                            if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.DoorsAndWalls.getValue()) {
                                CircleStencil = true;
                                if (square1 != null && this.getRoomID() != -1 && int6 == -1 && boolean1) {
                                    object1.setTargetAlpha(int1, 0.5F);
                                    object1.setAlpha(int1, 0.5F);
                                } else if (this.getRoomID() == int6
                                    || boolean0
                                    || !object1.getProperties().Is(IsoFlagType.transparentN) && !object1.getProperties().Is(IsoFlagType.transparentW)) {
                                    if (!boolean17) {
                                        object1.setTargetAlpha(int1, 1.0F);
                                        object1.setAlpha(int1, 1.0F);
                                    }
                                } else {
                                    object1.setTargetAlpha(int1, 0.0F);
                                    object1.setAlpha(int1, 0.0F);
                                }

                                object1.bAlphaForced = true;
                                if (object1.sprite.cutW && object1.sprite.cutN) {
                                    int0 = this.DoWallLightingNW(object1, int0, boolean11, boolean9, boolean10, boolean5, boolean6, boolean7, boolean8, shader);
                                } else if (object1.sprite.getType() == IsoObjectType.doorFrW || objectType == IsoObjectType.doorW || object1.sprite.cutW) {
                                    int0 = this.DoWallLightingW(object1, int0, boolean11, boolean9, boolean6, boolean8, shader);
                                } else if (objectType == IsoObjectType.doorFrN || objectType == IsoObjectType.doorN || object1.sprite.cutN) {
                                    int0 = this.DoWallLightingN(object1, int0, boolean9, boolean10, boolean5, boolean7, shader);
                                }

                                if (object1 instanceof IsoWindow && object1.getTargetAlpha(int1) < 1.0F) {
                                    bWallCutawayN = bWallCutawayN | object1.sprite.cutN;
                                    bWallCutawayW = bWallCutawayW | object1.sprite.cutW;
                                }
                            }
                        } else if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.DoorsAndWalls_SimpleLighting.getValue()) {
                            if (this.z != (int)IsoCamera.frameState.CamCharacterZ
                                || objectType == IsoObjectType.doorFrW
                                || objectType == IsoObjectType.doorFrN
                                || object1 instanceof IsoWindow) {
                                boolean2 = false;
                            }

                            if (object1.getTargetAlpha(int1) < 1.0F) {
                                if (!boolean2) {
                                    object1.setTargetAlpha(int1, 1.0F);
                                }

                                object1.setAlphaToTarget(int1);
                                IsoObject.LowLightingQualityHack = false;
                                object1.render(this.x, this.y, this.z, colorInfo, true, false, null);
                                if (!IsoObject.LowLightingQualityHack) {
                                    object1.setTargetAlpha(int1, 1.0F);
                                }
                            } else {
                                object1.render(this.x, this.y, this.z, colorInfo, true, false, null);
                            }
                        }

                        if ((object1.highlightFlags & 2) != 0) {
                            object1.highlightFlags &= -4;
                        }
                    }
                }
            }

            Arrays.sort(tempWorldInventoryObjects.getElements(), 0, tempWorldInventoryObjects.size(), (worldInventoryObject0x, worldInventoryObject1x) -> {
                float float0x = worldInventoryObject0x.xoff * worldInventoryObject0x.xoff + worldInventoryObject0x.yoff * worldInventoryObject0x.yoff;
                float float1x = worldInventoryObject1x.xoff * worldInventoryObject1x.xoff + worldInventoryObject1x.yoff * worldInventoryObject1x.yoff;
                if (float0x == float1x) {
                    return 0;
                } else {
                    return float0x > float1x ? 1 : -1;
                }
            });

            for (int int13 = 0; int13 < tempWorldInventoryObjects.size(); int13++) {
                IsoWorldInventoryObject worldInventoryObject1 = tempWorldInventoryObjects.get(int13);
                worldInventoryObject1.render(this.x, this.y, this.z, colorInfo, true, false, null);
            }

            return boolean3;
        }
    }

    void RereouteWallMaskTo(IsoObject object1) {
        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            IsoObject object0 = this.Objects.get(int0);
            if (object0.sprite.getProperties().Is(IsoFlagType.collideW) || object0.sprite.getProperties().Is(IsoFlagType.collideN)) {
                object0.rerouteMask = object1;
            }
        }
    }

    void setBlockedGridPointers(IsoGridSquare.GetSquare getSquare) {
        this.w = getSquare.getGridSquare(this.x - 1, this.y, this.z);
        this.e = getSquare.getGridSquare(this.x + 1, this.y, this.z);
        this.s = getSquare.getGridSquare(this.x, this.y + 1, this.z);
        this.n = getSquare.getGridSquare(this.x, this.y - 1, this.z);
        this.ne = getSquare.getGridSquare(this.x + 1, this.y - 1, this.z);
        this.nw = getSquare.getGridSquare(this.x - 1, this.y - 1, this.z);
        this.se = getSquare.getGridSquare(this.x + 1, this.y + 1, this.z);
        this.sw = getSquare.getGridSquare(this.x - 1, this.y + 1, this.z);
        if (this.s != null && this.testPathFindAdjacent(null, this.s.x - this.x, this.s.y - this.y, this.s.z - this.z, getSquare)) {
            this.s = null;
        }

        if (this.w != null && this.testPathFindAdjacent(null, this.w.x - this.x, this.w.y - this.y, this.w.z - this.z, getSquare)) {
            this.w = null;
        }

        if (this.n != null && this.testPathFindAdjacent(null, this.n.x - this.x, this.n.y - this.y, this.n.z - this.z, getSquare)) {
            this.n = null;
        }

        if (this.e != null && this.testPathFindAdjacent(null, this.e.x - this.x, this.e.y - this.y, this.e.z - this.z, getSquare)) {
            this.e = null;
        }

        if (this.sw != null && this.testPathFindAdjacent(null, this.sw.x - this.x, this.sw.y - this.y, this.sw.z - this.z, getSquare)) {
            this.sw = null;
        }

        if (this.se != null && this.testPathFindAdjacent(null, this.se.x - this.x, this.se.y - this.y, this.se.z - this.z, getSquare)) {
            this.se = null;
        }

        if (this.nw != null && this.testPathFindAdjacent(null, this.nw.x - this.x, this.nw.y - this.y, this.nw.z - this.z, getSquare)) {
            this.nw = null;
        }

        if (this.ne != null && this.testPathFindAdjacent(null, this.ne.x - this.x, this.ne.y - this.y, this.ne.z - this.z, getSquare)) {
            this.ne = null;
        }
    }

    public IsoObject getContainerItem(String type) {
        int int0 = this.getObjects().size();
        IsoObject[] objects = this.getObjects().getElements();

        for (int int1 = 0; int1 < int0; int1++) {
            IsoObject object = objects[int1];
            if (object.getContainer() != null && type.equals(object.getContainer().getType())) {
                return object;
            }
        }

        return null;
    }

    @Deprecated
    public void StartFire() {
    }

    public void explode() {
        IsoFireManager.explode(this.getCell(), this, 100000);
    }

    public int getHourLastSeen() {
        return this.hourLastSeen;
    }

    public float getHoursSinceLastSeen() {
        return (float)GameTime.instance.getWorldAgeHours() - this.hourLastSeen;
    }

    public void CalcVisibility(int playerIndex) {
        IsoPlayer player = IsoPlayer.players[playerIndex];
        IsoGridSquare.ILighting iLighting = this.lighting[playerIndex];
        iLighting.bCanSee(false);
        iLighting.bCouldSee(false);
        if (GameServer.bServer || player != null && (!player.isDead() || player.ReanimatedCorpse != null)) {
            if (player != null) {
                IsoGameCharacter.LightInfo lightInfox = player.getLightInfo2();
                IsoGridSquare square = lightInfox.square;
                if (square != null) {
                    IsoChunk chunkx = this.getChunk();
                    if (chunkx != null) {
                        tempo.x = this.x + 0.5F;
                        tempo.y = this.y + 0.5F;
                        tempo2.x = lightInfox.x;
                        tempo2.y = lightInfox.y;
                        tempo2.x = tempo2.x - tempo.x;
                        tempo2.y = tempo2.y - tempo.y;
                        Vector2 vector = tempo;
                        float float0 = tempo2.getLength();
                        tempo2.normalize();
                        if (player instanceof IsoSurvivor) {
                            player.setForwardDirection(vector);
                            lightInfox.angleX = vector.x;
                            lightInfox.angleY = vector.y;
                        }

                        vector.x = lightInfox.angleX;
                        vector.y = lightInfox.angleY;
                        vector.normalize();
                        float float1 = tempo2.dot(vector);
                        if (square == this) {
                            float1 = -1.0F;
                        }

                        if (!GameServer.bServer) {
                            float float2 = player.getStats().fatigue - 0.6F;
                            if (float2 < 0.0F) {
                                float2 = 0.0F;
                            }

                            float2 *= 2.5F;
                            if (player.Traits.HardOfHearing.isSet() && float2 < 0.7F) {
                                float2 = 0.7F;
                            }

                            float float3 = 2.0F;
                            if (player.Traits.KeenHearing.isSet()) {
                                float3 += 3.0F;
                            }

                            if (float0 < float3 * (1.0F - float2) && !player.Traits.Deaf.isSet()) {
                                float1 = -1.0F;
                            }
                        }

                        LosUtil.TestResults testResults = LosUtil.lineClearCached(
                            this.getCell(), this.x, this.y, this.z, (int)lightInfox.x, (int)lightInfox.y, (int)lightInfox.z, false, playerIndex
                        );
                        float float4 = -0.2F;
                        float4 -= player.getStats().fatigue - 0.6F;
                        if (float4 > -0.2F) {
                            float4 = -0.2F;
                        }

                        if (player.getStats().fatigue >= 1.0F) {
                            float4 -= 0.2F;
                        }

                        if (player.getMoodles().getMoodleLevel(MoodleType.Panic) == 4) {
                            float4 -= 0.2F;
                        }

                        if (float4 < -0.9F) {
                            float4 = -0.9F;
                        }

                        if (player.Traits.EagleEyed.isSet()) {
                            float4 += 0.2F;
                        }

                        if (player instanceof IsoPlayer && player.getVehicle() != null) {
                            float4 = 1.0F;
                        }

                        if (!(float1 > float4) && testResults != LosUtil.TestResults.Blocked) {
                            iLighting.bCouldSee(true);
                            if (this.room != null && this.room.def != null && !this.room.def.bExplored) {
                                byte byte0 = 10;
                                if (lightInfox.square != null && lightInfox.square.getBuilding() == this.room.building) {
                                    byte0 = 50;
                                }

                                if ((!GameServer.bServer || !(player instanceof IsoPlayer) || !player.isGhostMode())
                                    && IsoUtils.DistanceManhatten(lightInfox.x, lightInfox.y, this.x, this.y) < byte0
                                    && this.z == (int)lightInfox.z) {
                                    if (GameServer.bServer) {
                                        DebugLog.log(DebugType.Zombie, "bExplored room=" + this.room.def.ID);
                                    }

                                    this.room.def.bExplored = true;
                                    this.room.onSee();
                                    this.room.seen = 0;
                                }
                            }

                            if (!GameClient.bClient) {
                                Meta.instance.dealWithSquareSeen(this);
                            }

                            iLighting.bCanSee(true);
                            iLighting.bSeen(true);
                            iLighting.targetDarkMulti(1.0F);
                        } else {
                            if (testResults == LosUtil.TestResults.Blocked) {
                                iLighting.bCouldSee(false);
                            } else {
                                iLighting.bCouldSee(true);
                            }

                            if (!GameServer.bServer) {
                                if (iLighting.bSeen()) {
                                    float float5 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex());
                                    if (!iLighting.bCouldSee()) {
                                        float5 *= 0.5F;
                                    } else {
                                        float5 *= 0.94F;
                                    }

                                    if (this.room == null && square.getRoom() == null) {
                                        iLighting.targetDarkMulti(float5);
                                    } else if (this.room != null && square.getRoom() != null && this.room.building == square.getRoom().building) {
                                        if (this.room != square.getRoom() && !iLighting.bCouldSee()) {
                                            iLighting.targetDarkMulti(0.0F);
                                        } else {
                                            iLighting.targetDarkMulti(float5);
                                        }
                                    } else if (this.room == null) {
                                        iLighting.targetDarkMulti(float5 / 2.0F);
                                    } else if (iLighting.lampostTotalR() + iLighting.lampostTotalG() + iLighting.lampostTotalB() == 0.0F) {
                                        iLighting.targetDarkMulti(0.0F);
                                    }

                                    if (this.room != null) {
                                        iLighting.targetDarkMulti(iLighting.targetDarkMulti() * 0.7F);
                                    }
                                } else {
                                    iLighting.targetDarkMulti(0.0F);
                                    iLighting.darkMulti(0.0F);
                                }
                            }
                        }

                        if (float1 > float4) {
                            iLighting.targetDarkMulti(iLighting.targetDarkMulti() * 0.85F);
                        }

                        if (!GameServer.bServer) {
                            for (int int0 = 0; int0 < lightInfox.torches.size(); int0++) {
                                IsoGameCharacter.TorchInfo torchInfo = lightInfox.torches.get(int0);
                                tempo2.x = torchInfo.x;
                                tempo2.y = torchInfo.y;
                                tempo2.x = tempo2.x - (this.x + 0.5F);
                                tempo2.y = tempo2.y - (this.y + 0.5F);
                                float0 = tempo2.getLength();
                                tempo2.normalize();
                                vector.x = torchInfo.angleX;
                                vector.y = torchInfo.angleY;
                                vector.normalize();
                                float1 = tempo2.dot(vector);
                                if ((int)torchInfo.x == this.getX() && (int)torchInfo.y == this.getY() && (int)torchInfo.z == this.getZ()) {
                                    float1 = -1.0F;
                                }

                                boolean boolean0 = false;
                                if (IsoUtils.DistanceManhatten(this.getX(), this.getY(), torchInfo.x, torchInfo.y) < torchInfo.dist
                                    && (torchInfo.bCone && float1 < -torchInfo.dot || float1 == -1.0F || !torchInfo.bCone && float1 < 0.8F)) {
                                    boolean0 = true;
                                }

                                if ((torchInfo.bCone && float0 < torchInfo.dist || !torchInfo.bCone && float0 < torchInfo.dist)
                                    && iLighting.bCanSee()
                                    && boolean0
                                    && this.z == (int)player.getZ()) {
                                    float float6 = float0 / torchInfo.dist;
                                    if (float6 > 1.0F) {
                                        float6 = 1.0F;
                                    }

                                    if (float6 < 0.0F) {
                                        float6 = 0.0F;
                                    }

                                    iLighting.targetDarkMulti(iLighting.targetDarkMulti() + torchInfo.strength * (1.0F - float6) * 3.0F);
                                    if (iLighting.targetDarkMulti() > 2.5F) {
                                        iLighting.targetDarkMulti(2.5F);
                                    }

                                    torchTimer = lightInfox.time;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            iLighting.bSeen(true);
            iLighting.bCanSee(true);
            iLighting.bCouldSee(true);
        }
    }

    private LosUtil.TestResults DoDiagnalCheck(int int0, int int2, int int1, boolean boolean0) {
        LosUtil.TestResults testResults0 = this.testVisionAdjacent(int0, 0, int1, false, boolean0);
        if (testResults0 == LosUtil.TestResults.Blocked) {
            return LosUtil.TestResults.Blocked;
        } else {
            LosUtil.TestResults testResults1 = this.testVisionAdjacent(0, int2, int1, false, boolean0);
            if (testResults1 == LosUtil.TestResults.Blocked) {
                return LosUtil.TestResults.Blocked;
            } else {
                return testResults0 != LosUtil.TestResults.ClearThroughWindow && testResults1 != LosUtil.TestResults.ClearThroughWindow
                    ? this.testVisionAdjacent(int0, int2, int1, false, boolean0)
                    : LosUtil.TestResults.ClearThroughWindow;
            }
        }
    }

    boolean HasNoCharacters() {
        for (int int0 = 0; int0 < this.MovingObjects.size(); int0++) {
            if (this.MovingObjects.get(int0) instanceof IsoGameCharacter) {
                return false;
            }
        }

        for (int int1 = 0; int1 < this.SpecialObjects.size(); int1++) {
            if (this.SpecialObjects.get(int1) instanceof IsoBarricade) {
                return false;
            }
        }

        return true;
    }

    public IsoZombie getZombie() {
        for (int int0 = 0; int0 < this.MovingObjects.size(); int0++) {
            if (this.MovingObjects.get(int0) instanceof IsoZombie) {
                return (IsoZombie)this.MovingObjects.get(int0);
            }
        }

        return null;
    }

    public IsoPlayer getPlayer() {
        for (int int0 = 0; int0 < this.MovingObjects.size(); int0++) {
            if (this.MovingObjects.get(int0) instanceof IsoPlayer) {
                return (IsoPlayer)this.MovingObjects.get(int0);
            }
        }

        return null;
    }

    /**
     * @return the darkStep
     */
    public static float getDarkStep() {
        return darkStep;
    }

    /**
     * 
     * @param aDarkStep the darkStep to set
     */
    public static void setDarkStep(float aDarkStep) {
        darkStep = aDarkStep;
    }

    /**
     * @return the RecalcLightTime
     */
    public static int getRecalcLightTime() {
        return RecalcLightTime;
    }

    /**
     * 
     * @param aRecalcLightTime the RecalcLightTime to set
     */
    public static void setRecalcLightTime(int aRecalcLightTime) {
        RecalcLightTime = aRecalcLightTime;
    }

    /**
     * @return the lightcache
     */
    public static int getLightcache() {
        return lightcache;
    }

    /**
     * 
     * @param aLightcache the lightcache to set
     */
    public static void setLightcache(int aLightcache) {
        lightcache = aLightcache;
    }

    /**
     * @return the bCouldSee
     */
    public boolean isCouldSee(int playerIndex) {
        return this.lighting[playerIndex].bCouldSee();
    }

    /**
     * 
     * @param playerIndex
     * @param bCouldSee the bCouldSee to set
     */
    public void setCouldSee(int playerIndex, boolean bCouldSee) {
        this.lighting[playerIndex].bCouldSee(bCouldSee);
    }

    /**
     * @return the canSee
     */
    public boolean isCanSee(int playerIndex) {
        return this.lighting[playerIndex].bCanSee();
    }

    /**
     * 
     * @param playerIndex
     * @param canSee the canSee to set
     */
    public void setCanSee(int playerIndex, boolean canSee) {
        this.lighting[playerIndex].bCanSee(canSee);
    }

    /**
     * @return the getCell()
     */
    public IsoCell getCell() {
        return IsoWorld.instance.CurrentCell;
    }

    /**
     * @return the e
     */
    public IsoGridSquare getE() {
        return this.e;
    }

    /**
     * 
     * @param _e the e to set
     */
    public void setE(IsoGridSquare _e) {
        this.e = _e;
    }

    /**
     * @return the LightInfluenceB
     */
    public ArrayList<Float> getLightInfluenceB() {
        return this.LightInfluenceB;
    }

    /**
     * 
     * @param _LightInfluenceB the LightInfluenceB to set
     */
    public void setLightInfluenceB(ArrayList<Float> _LightInfluenceB) {
        this.LightInfluenceB = _LightInfluenceB;
    }

    /**
     * @return the LightInfluenceG
     */
    public ArrayList<Float> getLightInfluenceG() {
        return this.LightInfluenceG;
    }

    /**
     * 
     * @param _LightInfluenceG the LightInfluenceG to set
     */
    public void setLightInfluenceG(ArrayList<Float> _LightInfluenceG) {
        this.LightInfluenceG = _LightInfluenceG;
    }

    /**
     * @return the LightInfluenceR
     */
    public ArrayList<Float> getLightInfluenceR() {
        return this.LightInfluenceR;
    }

    /**
     * 
     * @param _LightInfluenceR the LightInfluenceR to set
     */
    public void setLightInfluenceR(ArrayList<Float> _LightInfluenceR) {
        this.LightInfluenceR = _LightInfluenceR;
    }

    /**
     * @return the StaticMovingObjects
     */
    public ArrayList<IsoMovingObject> getStaticMovingObjects() {
        return this.StaticMovingObjects;
    }

    /**
     * @return the MovingObjects
     */
    public ArrayList<IsoMovingObject> getMovingObjects() {
        return this.MovingObjects;
    }

    /**
     * @return the n
     */
    public IsoGridSquare getN() {
        return this.n;
    }

    /**
     * 
     * @param _n the n to set
     */
    public void setN(IsoGridSquare _n) {
        this.n = _n;
    }

    /**
     * @return the Objects
     */
    public PZArrayList<IsoObject> getObjects() {
        return this.Objects;
    }

    /**
     * @return the Properties
     */
    public PropertyContainer getProperties() {
        return this.Properties;
    }

    /**
     * @return the room
     */
    public IsoRoom getRoom() {
        return this.roomID == -1 ? null : this.room;
    }

    /**
     * 
     * @param _room the room to set
     */
    public void setRoom(IsoRoom _room) {
        this.room = _room;
    }

    public IsoBuilding getBuilding() {
        IsoRoom roomx = this.getRoom();
        return roomx != null ? roomx.getBuilding() : null;
    }

    /**
     * @return the s
     */
    public IsoGridSquare getS() {
        return this.s;
    }

    /**
     * 
     * @param _s the s to set
     */
    public void setS(IsoGridSquare _s) {
        this.s = _s;
    }

    /**
     * @return the SpecialObjects
     */
    public ArrayList<IsoObject> getSpecialObjects() {
        return this.SpecialObjects;
    }

    /**
     * @return the w
     */
    public IsoGridSquare getW() {
        return this.w;
    }

    /**
     * 
     * @param _w the w to set
     */
    public void setW(IsoGridSquare _w) {
        this.w = _w;
    }

    /**
     * @return the lampostTotalR
     */
    public float getLampostTotalR() {
        return this.lighting[0].lampostTotalR();
    }

    /**
     * 
     * @param lampostTotalR the lampostTotalR to set
     */
    public void setLampostTotalR(float lampostTotalR) {
        this.lighting[0].lampostTotalR(lampostTotalR);
    }

    /**
     * @return the lampostTotalG
     */
    public float getLampostTotalG() {
        return this.lighting[0].lampostTotalG();
    }

    /**
     * 
     * @param lampostTotalG the lampostTotalG to set
     */
    public void setLampostTotalG(float lampostTotalG) {
        this.lighting[0].lampostTotalG(lampostTotalG);
    }

    /**
     * @return the lampostTotalB
     */
    public float getLampostTotalB() {
        return this.lighting[0].lampostTotalB();
    }

    /**
     * 
     * @param lampostTotalB the lampostTotalB to set
     */
    public void setLampostTotalB(float lampostTotalB) {
        this.lighting[0].lampostTotalB(lampostTotalB);
    }

    /**
     * @return the bSeen
     */
    public boolean isSeen(int playerIndex) {
        return this.lighting[playerIndex].bSeen();
    }

    /**
     * 
     * @param playerIndex
     * @param bSeen the bSeen to set
     */
    public void setIsSeen(int playerIndex, boolean bSeen) {
        this.lighting[playerIndex].bSeen(bSeen);
    }

    /**
     * @return the darkMulti
     */
    public float getDarkMulti(int playerIndex) {
        return this.lighting[playerIndex].darkMulti();
    }

    /**
     * 
     * @param playerIndex
     * @param darkMulti the darkMulti to set
     */
    public void setDarkMulti(int playerIndex, float darkMulti) {
        this.lighting[playerIndex].darkMulti(darkMulti);
    }

    /**
     * @return the targetDarkMulti
     */
    public float getTargetDarkMulti(int playerIndex) {
        return this.lighting[playerIndex].targetDarkMulti();
    }

    /**
     * 
     * @param playerIndex
     * @param targetDarkMulti the targetDarkMulti to set
     */
    public void setTargetDarkMulti(int playerIndex, float targetDarkMulti) {
        this.lighting[playerIndex].targetDarkMulti(targetDarkMulti);
    }

    /**
     * 
     * @param _x the x to set
     */
    public void setX(int _x) {
        this.x = _x;
        this.CachedScreenValue = -1;
    }

    /**
     * 
     * @param _y the y to set
     */
    public void setY(int _y) {
        this.y = _y;
        this.CachedScreenValue = -1;
    }

    /**
     * 
     * @param _z the z to set
     */
    public void setZ(int _z) {
        this.z = _z;
        this.CachedScreenValue = -1;
    }

    /**
     * @return the DeferedCharacters
     */
    public ArrayList<IsoGameCharacter> getDeferedCharacters() {
        return this.DeferedCharacters;
    }

    public void addDeferredCharacter(IsoGameCharacter chr) {
        if (this.DeferredCharacterTick != this.getCell().DeferredCharacterTick) {
            if (!this.DeferedCharacters.isEmpty()) {
                this.DeferedCharacters.clear();
            }

            this.DeferredCharacterTick = this.getCell().DeferredCharacterTick;
        }

        this.DeferedCharacters.add(chr);
    }

    /**
     * @return the CacheIsFree
     */
    public boolean isCacheIsFree() {
        return this.CacheIsFree;
    }

    /**
     * 
     * @param _CacheIsFree the CacheIsFree to set
     */
    public void setCacheIsFree(boolean _CacheIsFree) {
        this.CacheIsFree = _CacheIsFree;
    }

    /**
     * @return the CachedIsFree
     */
    public boolean isCachedIsFree() {
        return this.CachedIsFree;
    }

    /**
     * 
     * @param _CachedIsFree the CachedIsFree to set
     */
    public void setCachedIsFree(boolean _CachedIsFree) {
        this.CachedIsFree = _CachedIsFree;
    }

    /**
     * @return the bDoSlowPathfinding
     */
    public static boolean isbDoSlowPathfinding() {
        return bDoSlowPathfinding;
    }

    /**
     * 
     * @param abDoSlowPathfinding the bDoSlowPathfinding to set
     */
    public static void setbDoSlowPathfinding(boolean abDoSlowPathfinding) {
        bDoSlowPathfinding = abDoSlowPathfinding;
    }

    /**
     * @return the SolidFloorCached
     */
    public boolean isSolidFloorCached() {
        return this.SolidFloorCached;
    }

    /**
     * 
     * @param _SolidFloorCached the SolidFloorCached to set
     */
    public void setSolidFloorCached(boolean _SolidFloorCached) {
        this.SolidFloorCached = _SolidFloorCached;
    }

    /**
     * @return the SolidFloor
     */
    public boolean isSolidFloor() {
        return this.SolidFloor;
    }

    /**
     * 
     * @param _SolidFloor the SolidFloor to set
     */
    public void setSolidFloor(boolean _SolidFloor) {
        this.SolidFloor = _SolidFloor;
    }

    /**
     * @return the defColorInfo
     */
    public static ColorInfo getDefColorInfo() {
        return defColorInfo;
    }

    public boolean isOutside() {
        return this.Properties.Is(IsoFlagType.exterior);
    }

    public boolean HasPushable() {
        int int0 = this.MovingObjects.size();

        for (int int1 = 0; int1 < int0; int1++) {
            if (this.MovingObjects.get(int1) instanceof IsoPushableObject) {
                return true;
            }
        }

        return false;
    }

    public void setRoomID(int _roomID) {
        this.roomID = _roomID;
        if (_roomID != -1) {
            this.getProperties().UnSet(IsoFlagType.exterior);
            this.room = this.chunk.getRoom(_roomID);
        }
    }

    public int getRoomID() {
        return this.roomID;
    }

    public boolean getCanSee(int playerIndex) {
        return this.lighting[playerIndex].bCanSee();
    }

    public boolean getSeen(int playerIndex) {
        return this.lighting[playerIndex].bSeen();
    }

    public IsoChunk getChunk() {
        return this.chunk;
    }

    public IsoObject getDoorOrWindow(boolean north) {
        for (int int0 = this.SpecialObjects.size() - 1; int0 >= 0; int0--) {
            IsoObject object = this.SpecialObjects.get(int0);
            if (object instanceof IsoDoor && ((IsoDoor)object).north == north) {
                return object;
            }

            if (object instanceof IsoThumpable
                && ((IsoThumpable)object).north == north
                && (((IsoThumpable)object).isDoor() || ((IsoThumpable)object).isWindow())) {
                return object;
            }

            if (object instanceof IsoWindow && ((IsoWindow)object).north == north) {
                return object;
            }
        }

        return null;
    }

    public IsoObject getDoorOrWindowOrWindowFrame(IsoDirections dir, boolean ignoreOpen) {
        for (int int0 = this.Objects.size() - 1; int0 >= 0; int0--) {
            IsoObject object = this.Objects.get(int0);
            IsoDoor door = Type.tryCastTo(object, IsoDoor.class);
            IsoThumpable thumpable = Type.tryCastTo(object, IsoThumpable.class);
            IsoWindow window = Type.tryCastTo(object, IsoWindow.class);
            if (door != null && door.getSpriteEdge(ignoreOpen) == dir) {
                return object;
            }

            if (thumpable != null && thumpable.getSpriteEdge(ignoreOpen) == dir) {
                return object;
            }

            if (window != null) {
                if (window.north && dir == IsoDirections.N) {
                    return object;
                }

                if (!window.north && dir == IsoDirections.W) {
                    return object;
                }
            }

            if (IsoWindowFrame.isWindowFrame(object)) {
                if (IsoWindowFrame.isWindowFrame(object, true) && dir == IsoDirections.N) {
                    return object;
                }

                if (IsoWindowFrame.isWindowFrame(object, false) && dir == IsoDirections.W) {
                    return object;
                }
            }
        }

        return null;
    }

    public IsoObject getOpenDoor(IsoDirections dir) {
        for (int int0 = 0; int0 < this.SpecialObjects.size(); int0++) {
            IsoObject object = this.SpecialObjects.get(int0);
            IsoDoor door = Type.tryCastTo(object, IsoDoor.class);
            IsoThumpable thumpable = Type.tryCastTo(object, IsoThumpable.class);
            if (door != null && door.open && door.getSpriteEdge(false) == dir) {
                return door;
            }

            if (thumpable != null && thumpable.open && thumpable.getSpriteEdge(false) == dir) {
                return thumpable;
            }
        }

        return null;
    }

    public void removeWorldObject(IsoWorldInventoryObject object) {
        if (object != null) {
            object.removeFromWorld();
            object.removeFromSquare();
        }
    }

    public void removeAllWorldObjects() {
        for (int int0 = 0; int0 < this.getWorldObjects().size(); int0++) {
            IsoObject object = this.getWorldObjects().get(int0);
            object.removeFromWorld();
            object.removeFromSquare();
            int0--;
        }
    }

    public ArrayList<IsoWorldInventoryObject> getWorldObjects() {
        return this.WorldObjects;
    }

    public PZArrayList<IsoObject> getLocalTemporaryObjects() {
        return this.localTemporaryObjects;
    }

    public KahluaTable getModData() {
        if (this.table == null) {
            this.table = LuaManager.platform.newTable();
        }

        return this.table;
    }

    public boolean hasModData() {
        return this.table != null && !this.table.isEmpty();
    }

    public ZomboidBitFlag getHasTypes() {
        return this.hasTypes;
    }

    public void setVertLight(int i, int _col, int playerIndex) {
        this.lighting[playerIndex].lightverts(i, _col);
    }

    public int getVertLight(int i, int playerIndex) {
        return this.lighting[playerIndex].lightverts(i);
    }

    public void setRainDrop(IsoRaindrop drop) {
        this.RainDrop = drop;
    }

    public IsoRaindrop getRainDrop() {
        return this.RainDrop;
    }

    public void setRainSplash(IsoRainSplash splash) {
        this.RainSplash = splash;
    }

    public IsoRainSplash getRainSplash() {
        return this.RainSplash;
    }

    public IsoMetaGrid.Zone getZone() {
        return this.zone;
    }

    public String getZoneType() {
        return this.zone != null ? this.zone.getType() : null;
    }

    public boolean isOverlayDone() {
        return this.overlayDone;
    }

    public void setOverlayDone(boolean _overlayDone) {
        this.overlayDone = _overlayDone;
    }

    public ErosionData.Square getErosionData() {
        if (this.erosion == null) {
            this.erosion = new ErosionData.Square();
        }

        return this.erosion;
    }

    public void disableErosion() {
        ErosionData.Square square0 = this.getErosionData();
        if (square0 != null && !square0.doNothing) {
            square0.doNothing = true;
        }
    }

    public void removeErosionObject(String type) {
        if (this.erosion != null) {
            if ("WallVines".equals(type)) {
                for (int int0 = 0; int0 < this.erosion.regions.size(); int0++) {
                    ErosionCategory.Data data = this.erosion.regions.get(int0);
                    if (data.regionID == 2 && data.categoryID == 0) {
                        this.erosion.regions.remove(int0);
                        break;
                    }
                }
            }
        }
    }

    public void syncIsoTrap(HandWeapon weapon) {
        ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
        PacketTypes.PacketType.AddExplosiveTrap.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(this.getX());
        byteBufferWriter.putInt(this.getY());
        byteBufferWriter.putInt(this.getZ());

        try {
            weapon.saveWithSize(byteBufferWriter.bb, false);
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
        }

        PacketTypes.PacketType.AddExplosiveTrap.send(GameClient.connection);
    }

    public void drawCircleExplosion(int radius, IsoTrap trap, IsoTrap.ExplosionMode explosionMode) {
        if (radius > 15) {
            radius = 15;
        }

        for (int int0 = this.getX() - radius; int0 <= this.getX() + radius; int0++) {
            for (int int1 = this.getY() - radius; int1 <= this.getY() + radius; int1++) {
                if (!(IsoUtils.DistanceTo(int0 + 0.5F, int1 + 0.5F, this.getX() + 0.5F, this.getY() + 0.5F) > radius)) {
                    LosUtil.TestResults testResults = LosUtil.lineClear(
                        this.getCell(), (int)trap.getX(), (int)trap.getY(), (int)trap.getZ(), int0, int1, this.z, false
                    );
                    if (testResults != LosUtil.TestResults.Blocked && testResults != LosUtil.TestResults.ClearThroughClosedDoor) {
                        IsoGridSquare square = this.getCell().getGridSquare(int0, int1, this.getZ());
                        if (square != null && NonPvpZone.getNonPvpZone(square.getX(), square.getY()) == null) {
                            if (explosionMode == IsoTrap.ExplosionMode.Smoke) {
                                if (!GameClient.bClient && Rand.Next(2) == 0) {
                                    IsoFireManager.StartSmoke(this.getCell(), square, true, 40, 0);
                                }

                                square.smoke();
                            }

                            if (explosionMode == IsoTrap.ExplosionMode.Explosion) {
                                if (!GameClient.bClient && trap.getExplosionPower() > 0 && Rand.Next(80 - trap.getExplosionPower()) <= 0) {
                                    square.Burn();
                                }

                                square.explosion(trap);
                                if (!GameClient.bClient && trap.getExplosionPower() > 0 && Rand.Next(100 - trap.getExplosionPower()) == 0) {
                                    IsoFireManager.StartFire(this.getCell(), square, true, 20);
                                }
                            }

                            if (explosionMode == IsoTrap.ExplosionMode.Fire && !GameClient.bClient && Rand.Next(100 - trap.getFirePower()) == 0) {
                                IsoFireManager.StartFire(this.getCell(), square, true, 40);
                            }

                            if (explosionMode == IsoTrap.ExplosionMode.Sensor) {
                                square.setTrapPositionX(this.getX());
                                square.setTrapPositionY(this.getY());
                                square.setTrapPositionZ(this.getZ());
                            }
                        }
                    }
                }
            }
        }
    }

    public void explosion(IsoTrap trap) {
        if (!GameServer.bServer || !trap.isInstantExplosion()) {
            for (int int0 = 0; int0 < this.getMovingObjects().size(); int0++) {
                IsoMovingObject movingObject = this.getMovingObjects().get(int0);
                if (movingObject instanceof IsoGameCharacter) {
                    if (GameServer.bServer || !(movingObject instanceof IsoZombie) || ((IsoZombie)movingObject).isLocal()) {
                        int int1 = Math.min(trap.getExplosionPower(), 80);
                        movingObject.Hit(
                            (HandWeapon)InventoryItemFactory.CreateItem("Base.Axe"),
                            IsoWorld.instance.CurrentCell.getFakeZombieForHit(),
                            Rand.Next(int1 / 30.0F, int1 / 30.0F * 2.0F) + trap.getExtraDamage(),
                            false,
                            1.0F
                        );
                        if (trap.getExplosionPower() > 0) {
                            boolean boolean0 = !(movingObject instanceof IsoZombie);

                            while (boolean0) {
                                boolean0 = false;
                                BodyPart bodyPart = ((IsoGameCharacter)movingObject).getBodyDamage().getBodyPart(BodyPartType.FromIndex(Rand.Next(15)));
                                bodyPart.setBurned();
                                if (Rand.Next((100 - int1) / 2) == 0) {
                                    boolean0 = true;
                                }
                            }
                        }
                    }

                    if (GameClient.bClient && movingObject instanceof IsoZombie && ((IsoZombie)movingObject).isRemoteZombie()) {
                        movingObject.Hit(
                            (HandWeapon)InventoryItemFactory.CreateItem("Base.Axe"), IsoWorld.instance.CurrentCell.getFakeZombieForHit(), 0.0F, true, 0.0F
                        );
                    }
                }
            }
        }
    }

    public void smoke() {
        for (int int0 = 0; int0 < this.getMovingObjects().size(); int0++) {
            IsoMovingObject movingObject = this.getMovingObjects().get(int0);
            if (movingObject instanceof IsoZombie) {
                ((IsoZombie)movingObject).setTarget(null);
                ((IsoZombie)movingObject).changeState(ZombieIdleState.instance());
            }
        }
    }

    public void explodeTrap() {
        IsoGridSquare square0 = this.getCell().getGridSquare(this.getTrapPositionX(), this.getTrapPositionY(), this.getTrapPositionZ());
        if (square0 != null) {
            for (int int0 = 0; int0 < square0.getObjects().size(); int0++) {
                IsoObject object = square0.getObjects().get(int0);
                if (object instanceof IsoTrap trap) {
                    trap.triggerExplosion(false);
                    IsoGridSquare square2 = null;
                    int int1 = trap.getSensorRange();

                    for (int int2 = square0.getX() - int1; int2 <= square0.getX() + int1; int2++) {
                        for (int int3 = square0.getY() - int1; int3 <= square0.getY() + int1; int3++) {
                            if (IsoUtils.DistanceTo(int2 + 0.5F, int3 + 0.5F, square0.getX() + 0.5F, square0.getY() + 0.5F) <= int1) {
                                square2 = this.getCell().getGridSquare(int2, int3, this.getZ());
                                if (square2 != null) {
                                    square2.setTrapPositionX(-1);
                                    square2.setTrapPositionY(-1);
                                    square2.setTrapPositionZ(-1);
                                }
                            }
                        }
                    }

                    return;
                }
            }
        }
    }

    public int getTrapPositionX() {
        return this.trapPositionX;
    }

    public void setTrapPositionX(int _trapPositionX) {
        this.trapPositionX = _trapPositionX;
    }

    public int getTrapPositionY() {
        return this.trapPositionY;
    }

    public void setTrapPositionY(int _trapPositionY) {
        this.trapPositionY = _trapPositionY;
    }

    public int getTrapPositionZ() {
        return this.trapPositionZ;
    }

    public void setTrapPositionZ(int _trapPositionZ) {
        this.trapPositionZ = _trapPositionZ;
    }

    public boolean haveElectricity() {
        if ((this.chunk == null || !this.chunk.bLoaded) && this.haveElectricity) {
            return true;
        } else {
            return !SandboxOptions.getInstance().AllowExteriorGenerator.getValue() && this.Is(IsoFlagType.exterior)
                ? false
                : this.chunk != null && this.chunk.isGeneratorPoweringSquare(this.x, this.y, this.z);
        }
    }

    public void setHaveElectricity(boolean _haveElectricity) {
        if (!_haveElectricity) {
            this.haveElectricity = false;
        }

        if (this.getObjects() != null) {
            for (int int0 = 0; int0 < this.getObjects().size(); int0++) {
                if (this.getObjects().get(int0) instanceof IsoLightSwitch) {
                    ((IsoLightSwitch)this.getObjects().get(int0)).update();
                }
            }
        }
    }

    public IsoGenerator getGenerator() {
        if (this.getSpecialObjects() != null) {
            for (int int0 = 0; int0 < this.getSpecialObjects().size(); int0++) {
                if (this.getSpecialObjects().get(int0) instanceof IsoGenerator) {
                    return (IsoGenerator)this.getSpecialObjects().get(int0);
                }
            }
        }

        return null;
    }

    public void stopFire() {
        IsoFireManager.RemoveAllOn(this);
        this.getProperties().Set(IsoFlagType.burntOut);
        this.getProperties().UnSet(IsoFlagType.burning);
        this.burntOut = true;
    }

    public void transmitStopFire() {
        if (GameClient.bClient) {
            GameClient.sendStopFire(this);
        }
    }

    public long playSound(String file) {
        BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter(this.x + 0.5F, this.y + 0.5F, this.z);
        return baseSoundEmitter.playSound(file);
    }

    @Deprecated
    public long playSound(String file, boolean doWorldSound) {
        BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter(this.x + 0.5F, this.y + 0.5F, this.z);
        return baseSoundEmitter.playSound(file, doWorldSound);
    }

    public void FixStackableObjects() {
        IsoObject object0 = null;

        for (int int0 = 0; int0 < this.Objects.size(); int0++) {
            IsoObject object1 = this.Objects.get(int0);
            if (!(object1 instanceof IsoWorldInventoryObject) && object1.sprite != null) {
                PropertyContainer propertyContainer0 = object1.sprite.getProperties();
                if (propertyContainer0.getStackReplaceTileOffset() != 0) {
                    object1.sprite = IsoSprite.getSprite(IsoSpriteManager.instance, object1.sprite.ID + propertyContainer0.getStackReplaceTileOffset());
                    if (object1.sprite == null) {
                        continue;
                    }

                    propertyContainer0 = object1.sprite.getProperties();
                }

                if (propertyContainer0.isTable() || propertyContainer0.isTableTop()) {
                    float float0 = propertyContainer0.isSurfaceOffset() ? propertyContainer0.getSurface() : 0.0F;
                    if (object0 != null) {
                        object1.setRenderYOffset(object0.getRenderYOffset() + object0.getSurfaceOffset() - float0);
                    } else {
                        object1.setRenderYOffset(0.0F - float0);
                    }
                }

                if (propertyContainer0.isTable()) {
                    object0 = object1;
                }

                if (object1 instanceof IsoLightSwitch && propertyContainer0.isTableTop() && object0 != null && !propertyContainer0.Is("IgnoreSurfaceSnap")) {
                    int int1 = PZMath.tryParseInt(propertyContainer0.Val("Noffset"), 0);
                    int int2 = PZMath.tryParseInt(propertyContainer0.Val("Soffset"), 0);
                    int int3 = PZMath.tryParseInt(propertyContainer0.Val("Woffset"), 0);
                    int int4 = PZMath.tryParseInt(propertyContainer0.Val("Eoffset"), 0);
                    String string0 = propertyContainer0.Val("Facing");
                    PropertyContainer propertyContainer1 = object0.getProperties();
                    String string1 = propertyContainer1.Val("Facing");
                    if (!StringUtils.isNullOrWhitespace(string1) && !string1.equals(string0)) {
                        int int5 = 0;
                        if ("N".equals(string1)) {
                            if (int1 != 0) {
                                int5 = int1;
                            } else if (int2 != 0) {
                                int5 = int2;
                            }
                        } else if ("S".equals(string1)) {
                            if (int2 != 0) {
                                int5 = int2;
                            } else if (int1 != 0) {
                                int5 = int1;
                            }
                        } else if ("W".equals(string1)) {
                            if (int3 != 0) {
                                int5 = int3;
                            } else if (int4 != 0) {
                                int5 = int4;
                            }
                        } else if ("E".equals(string1)) {
                            if (int4 != 0) {
                                int5 = int4;
                            } else if (int3 != 0) {
                                int5 = int3;
                            }
                        }

                        if (int5 != 0) {
                            IsoSprite sprite = IsoSpriteManager.instance.getSprite(object1.sprite.ID + int5);
                            if (sprite != null) {
                                object1.setSprite(sprite);
                            }
                        }
                    }
                }
            }
        }
    }

    public void fixPlacedItemRenderOffsets() {
        IsoObject[] objects = this.Objects.getElements();
        int int0 = this.Objects.size();
        int int1 = 0;

        for (int int2 = 0; int2 < int0; int2++) {
            IsoObject object0 = objects[int2];
            int int3 = PZMath.roundToInt(object0.getSurfaceOffsetNoTable());
            if (!(int3 <= 0.0F) && !PZArrayUtil.contains(SURFACE_OFFSETS, int1, int3)) {
                SURFACE_OFFSETS[int1++] = int3;
            }
        }

        if (int1 == 0) {
            SURFACE_OFFSETS[int1++] = 0;
        }

        for (int int4 = 0; int4 < int0; int4++) {
            IsoObject object1 = objects[int4];
            IsoWorldInventoryObject worldInventoryObject = Type.tryCastTo(object1, IsoWorldInventoryObject.class);
            if (worldInventoryObject != null) {
                int int5 = PZMath.roundToInt(worldInventoryObject.zoff * 96.0F);
                int int6 = 0;

                for (int int7 = 0; int7 < int1; int7++) {
                    if (int5 <= SURFACE_OFFSETS[int7]) {
                        int6 = SURFACE_OFFSETS[int7];
                        break;
                    }

                    int6 = SURFACE_OFFSETS[int7];
                    if (int7 < int1 - 1 && int5 < SURFACE_OFFSETS[int7 + 1]) {
                        break;
                    }
                }

                worldInventoryObject.zoff = int6 / 96.0F;
            }
        }
    }

    public BaseVehicle getVehicleContainer() {
        int int0 = (int)((this.x - 4.0F) / 10.0F);
        int int1 = (int)((this.y - 4.0F) / 10.0F);
        int int2 = (int)Math.ceil((this.x + 4.0F) / 10.0F);
        int int3 = (int)Math.ceil((this.y + 4.0F) / 10.0F);

        for (int int4 = int1; int4 < int3; int4++) {
            for (int int5 = int0; int5 < int2; int5++) {
                IsoChunk chunkx = GameServer.bServer ? ServerMap.instance.getChunk(int5, int4) : IsoWorld.instance.CurrentCell.getChunk(int5, int4);
                if (chunkx != null) {
                    for (int int6 = 0; int6 < chunkx.vehicles.size(); int6++) {
                        BaseVehicle vehicle = chunkx.vehicles.get(int6);
                        if (vehicle.isIntersectingSquare(this.x, this.y, this.z)) {
                            return vehicle;
                        }
                    }
                }
            }
        }

        return null;
    }

    public boolean isVehicleIntersecting() {
        int int0 = (int)((this.x - 4.0F) / 10.0F);
        int int1 = (int)((this.y - 4.0F) / 10.0F);
        int int2 = (int)Math.ceil((this.x + 4.0F) / 10.0F);
        int int3 = (int)Math.ceil((this.y + 4.0F) / 10.0F);

        for (int int4 = int1; int4 < int3; int4++) {
            for (int int5 = int0; int5 < int2; int5++) {
                IsoChunk chunkx = GameServer.bServer ? ServerMap.instance.getChunk(int5, int4) : IsoWorld.instance.CurrentCell.getChunk(int5, int4);
                if (chunkx != null) {
                    for (int int6 = 0; int6 < chunkx.vehicles.size(); int6++) {
                        BaseVehicle vehicle = chunkx.vehicles.get(int6);
                        if (vehicle.isIntersectingSquare(this.x, this.y, this.z)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public IsoCompost getCompost() {
        if (this.getSpecialObjects() != null) {
            for (int int0 = 0; int0 < this.getSpecialObjects().size(); int0++) {
                if (this.getSpecialObjects().get(int0) instanceof IsoCompost) {
                    return (IsoCompost)this.getSpecialObjects().get(int0);
                }
            }
        }

        return null;
    }

    public void setIsoWorldRegion(IsoWorldRegion mr) {
        this.hasSetIsoWorldRegion = mr != null;
        this.isoWorldRegion = mr;
    }

    public IWorldRegion getIsoWorldRegion() {
        if (GameServer.bServer) {
            return IsoRegions.getIsoWorldRegion(this.x, this.y, this.z);
        } else {
            if (!this.hasSetIsoWorldRegion) {
                this.isoWorldRegion = IsoRegions.getIsoWorldRegion(this.x, this.y, this.z);
                this.hasSetIsoWorldRegion = true;
            }

            return this.isoWorldRegion;
        }
    }

    public void ResetIsoWorldRegion() {
        this.isoWorldRegion = null;
        this.hasSetIsoWorldRegion = false;
    }

    public boolean isInARoom() {
        return this.getRoom() != null || this.getIsoWorldRegion() != null && this.getIsoWorldRegion().isPlayerRoom();
    }

    public int getRoomSize() {
        if (this.getRoom() != null) {
            return this.getRoom().getSquares().size();
        } else {
            return this.getIsoWorldRegion() != null && this.getIsoWorldRegion().isPlayerRoom() ? this.getIsoWorldRegion().getSquareSize() : -1;
        }
    }

    public int getWallType() {
        byte byte0 = 0;
        if (this.getProperties().Is(IsoFlagType.WallN)) {
            byte0 |= 1;
        }

        if (this.getProperties().Is(IsoFlagType.WallW)) {
            byte0 |= 4;
        }

        if (this.getProperties().Is(IsoFlagType.WallNW)) {
            byte0 |= 5;
        }

        IsoGridSquare square1 = this.nav[IsoDirections.E.index()];
        if (square1 != null && (square1.getProperties().Is(IsoFlagType.WallW) || square1.getProperties().Is(IsoFlagType.WallNW))) {
            byte0 |= 8;
        }

        IsoGridSquare square2 = this.nav[IsoDirections.S.index()];
        if (square2 != null && (square2.getProperties().Is(IsoFlagType.WallN) || square2.getProperties().Is(IsoFlagType.WallNW))) {
            byte0 |= 2;
        }

        return byte0;
    }

    public int getPuddlesDir() {
        byte byte0 = IsoGridSquare.PuddlesDirection.PUDDLES_DIR_ALL;
        if (this.isInARoom()) {
            return IsoGridSquare.PuddlesDirection.PUDDLES_DIR_NONE;
        } else {
            for (int int0 = 0; int0 < this.getObjects().size(); int0++) {
                IsoObject object = this.getObjects().get(int0);
                if (object.AttachedAnimSprite != null) {
                    for (int int1 = 0; int1 < object.AttachedAnimSprite.size(); int1++) {
                        IsoSprite sprite = object.AttachedAnimSprite.get(int1).parentSprite;
                        if (sprite.name != null) {
                            if (sprite.name.equals("street_trafficlines_01_2")
                                || sprite.name.equals("street_trafficlines_01_6")
                                || sprite.name.equals("street_trafficlines_01_22")
                                || sprite.name.equals("street_trafficlines_01_32")) {
                                byte0 = IsoGridSquare.PuddlesDirection.PUDDLES_DIR_NW;
                            }

                            if (sprite.name.equals("street_trafficlines_01_4")
                                || sprite.name.equals("street_trafficlines_01_0")
                                || sprite.name.equals("street_trafficlines_01_16")) {
                                byte0 = IsoGridSquare.PuddlesDirection.PUDDLES_DIR_NE;
                            }
                        }
                    }
                }
            }

            return byte0;
        }
    }

    public boolean haveFire() {
        int int0 = this.Objects.size();
        IsoObject[] objects = this.Objects.getElements();

        for (int int1 = 0; int1 < int0; int1++) {
            IsoObject object = objects[int1];
            if (object instanceof IsoFire) {
                return true;
            }
        }

        return false;
    }

    public IsoBuilding getRoofHideBuilding() {
        return this.roofHideBuilding;
    }

    public IsoGridSquare getAdjacentSquare(IsoDirections dir) {
        return this.nav[dir.index()];
    }

    public IsoGridSquare getAdjacentPathSquare(IsoDirections dir) {
        switch (dir) {
            case NW:
                return this.nw;
            case N:
                return this.n;
            case NE:
                return this.ne;
            case W:
                return this.w;
            case E:
                return this.e;
            case SW:
                return this.sw;
            case S:
                return this.s;
            case SE:
                return this.se;
            default:
                return null;
        }
    }

    public float getApparentZ(float dx, float dy) {
        dx = PZMath.clamp(dx, 0.0F, 1.0F);
        dy = PZMath.clamp(dy, 0.0F, 1.0F);
        if (this.Has(IsoObjectType.stairsTN)) {
            return this.getZ() + PZMath.lerp(0.6666F, 1.0F, 1.0F - dy);
        } else if (this.Has(IsoObjectType.stairsTW)) {
            return this.getZ() + PZMath.lerp(0.6666F, 1.0F, 1.0F - dx);
        } else if (this.Has(IsoObjectType.stairsMN)) {
            return this.getZ() + PZMath.lerp(0.3333F, 0.6666F, 1.0F - dy);
        } else if (this.Has(IsoObjectType.stairsMW)) {
            return this.getZ() + PZMath.lerp(0.3333F, 0.6666F, 1.0F - dx);
        } else if (this.Has(IsoObjectType.stairsBN)) {
            return this.getZ() + PZMath.lerp(0.01F, 0.3333F, 1.0F - dy);
        } else {
            return this.Has(IsoObjectType.stairsBW) ? this.getZ() + PZMath.lerp(0.01F, 0.3333F, 1.0F - dx) : this.getZ();
        }
    }

    public float getTotalWeightOfItemsOnFloor() {
        float float0 = 0.0F;

        for (int int0 = 0; int0 < this.WorldObjects.size(); int0++) {
            InventoryItem item = this.WorldObjects.get(int0).getItem();
            if (item != null) {
                float0 += item.getUnequippedWeight();
            }
        }

        return float0;
    }

    public boolean getCollideMatrix(int dx, int dy, int dz) {
        return getMatrixBit(this.collideMatrix, dx + 1, dy + 1, dz + 1);
    }

    public boolean getPathMatrix(int dx, int dy, int dz) {
        return getMatrixBit(this.pathMatrix, dx + 1, dy + 1, dz + 1);
    }

    public boolean getVisionMatrix(int dx, int dy, int dz) {
        return getMatrixBit(this.visionMatrix, dx + 1, dy + 1, dz + 1);
    }

    public void checkRoomSeen(int playerIndex) {
        IsoRoom roomx = this.getRoom();
        if (roomx != null && roomx.def != null && !roomx.def.bExplored) {
            IsoPlayer player = IsoPlayer.players[playerIndex];
            if (player != null) {
                if (this.z == (int)player.z) {
                    byte byte0 = 10;
                    if (player.getBuilding() == roomx.building) {
                        byte0 = 50;
                    }

                    if (IsoUtils.DistanceToSquared(player.x, player.y, this.x + 0.5F, this.y + 0.5F) < byte0 * byte0) {
                        roomx.def.bExplored = true;
                        roomx.onSee();
                        roomx.seen = 0;
                    }
                }
            }
        }
    }

    public boolean hasFlies() {
        return this.bHasFlies;
    }

    public void setHasFlies(boolean hasFlies) {
        this.bHasFlies = hasFlies;
    }

    public float getLightLevel(int playerIndex) {
        return (this.lighting[playerIndex].lightInfo().r + this.lighting[playerIndex].lightInfo().g + this.lighting[playerIndex].lightInfo().b) / 3.0F;
    }

    public static class CellGetSquare implements IsoGridSquare.GetSquare {
        @Override
        public IsoGridSquare getGridSquare(int x, int y, int z) {
            return IsoWorld.instance.CurrentCell.getGridSquare(x, y, z);
        }
    }

    public static final class CircleStencilShader extends Shader {
        public static final IsoGridSquare.CircleStencilShader instance = new IsoGridSquare.CircleStencilShader();
        public int a_wallShadeColor = -1;

        public CircleStencilShader() {
            super("CircleStencil");
        }

        @Override
        protected void onCompileSuccess(ShaderProgram shaderProgram) {
            this.Start();
            this.a_wallShadeColor = GL20.glGetAttribLocation(this.getID(), "a_wallShadeColor");
            shaderProgram.setSamplerUnit("texture", 0);
            shaderProgram.setSamplerUnit("CutawayStencil", 1);
            this.End();
        }
    }

    public interface GetSquare {
        IsoGridSquare getGridSquare(int x, int y, int z);
    }

    public interface ILighting {
        int lightverts(int i);

        float lampostTotalR();

        float lampostTotalG();

        float lampostTotalB();

        boolean bSeen();

        boolean bCanSee();

        boolean bCouldSee();

        float darkMulti();

        float targetDarkMulti();

        ColorInfo lightInfo();

        void lightverts(int i, int value);

        void lampostTotalR(float r);

        void lampostTotalG(float g);

        void lampostTotalB(float b);

        void bSeen(boolean seen);

        void bCanSee(boolean canSee);

        void bCouldSee(boolean couldSee);

        void darkMulti(float f);

        void targetDarkMulti(float f);

        int resultLightCount();

        IsoGridSquare.ResultLight getResultLight(int index);

        void reset();
    }

    public static final class Lighting implements IsoGridSquare.ILighting {
        private final int[] lightverts = new int[8];
        private float lampostTotalR = 0.0F;
        private float lampostTotalG = 0.0F;
        private float lampostTotalB = 0.0F;
        private boolean bSeen;
        private boolean bCanSee;
        private boolean bCouldSee;
        private float darkMulti;
        private float targetDarkMulti;
        private final ColorInfo lightInfo = new ColorInfo();

        @Override
        public int lightverts(int int0) {
            return this.lightverts[int0];
        }

        @Override
        public float lampostTotalR() {
            return this.lampostTotalR;
        }

        @Override
        public float lampostTotalG() {
            return this.lampostTotalG;
        }

        @Override
        public float lampostTotalB() {
            return this.lampostTotalB;
        }

        @Override
        public boolean bSeen() {
            return this.bSeen;
        }

        @Override
        public boolean bCanSee() {
            return this.bCanSee;
        }

        @Override
        public boolean bCouldSee() {
            return this.bCouldSee;
        }

        @Override
        public float darkMulti() {
            return this.darkMulti;
        }

        @Override
        public float targetDarkMulti() {
            return this.targetDarkMulti;
        }

        @Override
        public ColorInfo lightInfo() {
            return this.lightInfo;
        }

        @Override
        public void lightverts(int int1, int int0) {
            this.lightverts[int1] = int0;
        }

        @Override
        public void lampostTotalR(float float0) {
            this.lampostTotalR = float0;
        }

        @Override
        public void lampostTotalG(float float0) {
            this.lampostTotalG = float0;
        }

        @Override
        public void lampostTotalB(float float0) {
            this.lampostTotalB = float0;
        }

        @Override
        public void bSeen(boolean boolean0) {
            this.bSeen = boolean0;
        }

        @Override
        public void bCanSee(boolean boolean0) {
            this.bCanSee = boolean0;
        }

        @Override
        public void bCouldSee(boolean boolean0) {
            this.bCouldSee = boolean0;
        }

        @Override
        public void darkMulti(float float0) {
            this.darkMulti = float0;
        }

        @Override
        public void targetDarkMulti(float float0) {
            this.targetDarkMulti = float0;
        }

        @Override
        public int resultLightCount() {
            return 0;
        }

        @Override
        public IsoGridSquare.ResultLight getResultLight(int var1) {
            return null;
        }

        @Override
        public void reset() {
            this.lampostTotalR = 0.0F;
            this.lampostTotalG = 0.0F;
            this.lampostTotalB = 0.0F;
            this.bSeen = false;
            this.bCouldSee = false;
            this.bCanSee = false;
            this.targetDarkMulti = 0.0F;
            this.darkMulti = 0.0F;
            this.lightInfo.r = 0.0F;
            this.lightInfo.g = 0.0F;
            this.lightInfo.b = 0.0F;
            this.lightInfo.a = 1.0F;
        }
    }

    public static final class NoCircleStencilShader {
        public static final IsoGridSquare.NoCircleStencilShader instance = new IsoGridSquare.NoCircleStencilShader();
        private ShaderProgram shaderProgram;
        public int ShaderID = -1;
        public int a_wallShadeColor = -1;

        private void initShader() {
            this.shaderProgram = ShaderProgram.createShaderProgram("NoCircleStencil", false, true);
            if (this.shaderProgram.isCompiled()) {
                this.ShaderID = this.shaderProgram.getShaderID();
                this.a_wallShadeColor = GL20.glGetAttribLocation(this.ShaderID, "a_wallShadeColor");
            }
        }
    }

    public static class PuddlesDirection {
        public static byte PUDDLES_DIR_NONE = 1;
        public static byte PUDDLES_DIR_NE = 2;
        public static byte PUDDLES_DIR_NW = 4;
        public static byte PUDDLES_DIR_ALL = 8;
    }

    private interface RenderWallCallback {
        void invoke(Texture var1, float var2, float var3);
    }

    public static final class ResultLight {
        public int id;
        public int x;
        public int y;
        public int z;
        public int radius;
        public float r;
        public float g;
        public float b;
        public static final int RLF_NONE = 0;
        public static final int RLF_ROOMLIGHT = 1;
        public static final int RLF_TORCH = 2;
        public int flags;

        public IsoGridSquare.ResultLight copyFrom(IsoGridSquare.ResultLight other) {
            this.id = other.id;
            this.x = other.x;
            this.y = other.y;
            this.z = other.z;
            this.radius = other.radius;
            this.r = other.r;
            this.g = other.g;
            this.b = other.b;
            this.flags = other.flags;
            return this;
        }
    }

    private static final class s_performance {
        static final PerformanceProfileProbe renderFloor = new PerformanceProfileProbe("IsoGridSquare.renderFloor", false);
    }
}
