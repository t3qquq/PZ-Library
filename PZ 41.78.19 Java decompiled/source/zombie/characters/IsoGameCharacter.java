// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import fmod.fmod.FMODManager;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import fmod.fmod.IFMODParameterUpdater;
import gnu.trove.map.hash.THashMap;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.UUID;
import java.util.Map.Entry;
import org.joml.Vector3f;
import se.krka.kahlua.vm.KahluaTable;
import zombie.AmbientStreamManager;
import zombie.DebugFileWatcher;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.PersistentOutfits;
import zombie.PredicatedFileWatcher;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZomboidFileSystem;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaHookManager;
import zombie.Lua.LuaManager;
import zombie.ai.GameCharacterAIBrain;
import zombie.ai.MapKnowledge;
import zombie.ai.State;
import zombie.ai.StateMachine;
import zombie.ai.astar.AStarPathFinder;
import zombie.ai.astar.AStarPathFinderResult;
import zombie.ai.sadisticAIDirector.SleepingEventData;
import zombie.ai.states.AttackNetworkState;
import zombie.ai.states.AttackState;
import zombie.ai.states.BumpedState;
import zombie.ai.states.ClimbDownSheetRopeState;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbOverWallState;
import zombie.ai.states.ClimbSheetRopeState;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.CloseWindowState;
import zombie.ai.states.CollideWithWallState;
import zombie.ai.states.FakeDeadZombieState;
import zombie.ai.states.IdleState;
import zombie.ai.states.LungeNetworkState;
import zombie.ai.states.LungeState;
import zombie.ai.states.OpenWindowState;
import zombie.ai.states.PathFindState;
import zombie.ai.states.PlayerFallDownState;
import zombie.ai.states.PlayerGetUpState;
import zombie.ai.states.PlayerHitReactionPVPState;
import zombie.ai.states.PlayerHitReactionState;
import zombie.ai.states.PlayerKnockedDown;
import zombie.ai.states.PlayerOnGroundState;
import zombie.ai.states.SmashWindowState;
import zombie.ai.states.StaggerBackState;
import zombie.ai.states.SwipeStatePlayer;
import zombie.ai.states.ThumpState;
import zombie.ai.states.WalkTowardState;
import zombie.ai.states.ZombieFallDownState;
import zombie.ai.states.ZombieFallingState;
import zombie.ai.states.ZombieHitReactionState;
import zombie.ai.states.ZombieOnGroundState;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.FMODParameter;
import zombie.audio.FMODParameterList;
import zombie.audio.GameSoundClip;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characterTextures.BloodClothingType;
import zombie.characters.AttachedItems.AttachedItem;
import zombie.characters.AttachedItems.AttachedItems;
import zombie.characters.AttachedItems.AttachedLocationGroup;
import zombie.characters.AttachedItems.AttachedLocations;
import zombie.characters.BodyDamage.BodyDamage;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartLast;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.BodyDamage.Metabolics;
import zombie.characters.BodyDamage.Nutrition;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.characters.CharacterTimedActions.LuaTimedActionNew;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.Moodles.Moodles;
import zombie.characters.WornItems.BodyLocationGroup;
import zombie.characters.WornItems.BodyLocations;
import zombie.characters.WornItems.WornItem;
import zombie.characters.WornItems.WornItems;
import zombie.characters.action.ActionContext;
import zombie.characters.action.ActionState;
import zombie.characters.action.ActionStateSnapshot;
import zombie.characters.action.IActionStateChanged;
import zombie.characters.skills.PerkFactory;
import zombie.characters.traits.TraitCollection;
import zombie.characters.traits.TraitFactory;
import zombie.chat.ChatElement;
import zombie.chat.ChatElementOwner;
import zombie.chat.ChatManager;
import zombie.chat.ChatMessage;
import zombie.core.BoxedStaticValues;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.logger.LoggerManager;
import zombie.core.math.PZMath;
import zombie.core.opengl.Shader;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.raknet.UdpConnection;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.advancedanimation.AdvancedAnimator;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.core.skinnedmodel.advancedanimation.AnimLayer;
import zombie.core.skinnedmodel.advancedanimation.AnimNode;
import zombie.core.skinnedmodel.advancedanimation.AnimState;
import zombie.core.skinnedmodel.advancedanimation.AnimationSet;
import zombie.core.skinnedmodel.advancedanimation.AnimationVariableHandle;
import zombie.core.skinnedmodel.advancedanimation.AnimationVariableSlotCallbackBool;
import zombie.core.skinnedmodel.advancedanimation.AnimationVariableSlotCallbackFloat;
import zombie.core.skinnedmodel.advancedanimation.AnimationVariableSlotCallbackInt;
import zombie.core.skinnedmodel.advancedanimation.AnimationVariableSlotCallbackString;
import zombie.core.skinnedmodel.advancedanimation.AnimationVariableSource;
import zombie.core.skinnedmodel.advancedanimation.AnimationVariableType;
import zombie.core.skinnedmodel.advancedanimation.IAnimEventCallback;
import zombie.core.skinnedmodel.advancedanimation.IAnimatable;
import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableMap;
import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableSlot;
import zombie.core.skinnedmodel.advancedanimation.LiveAnimNode;
import zombie.core.skinnedmodel.advancedanimation.debug.AnimatorDebugMonitor;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.animation.AnimationMultiTrack;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.core.skinnedmodel.animation.debug.AnimationPlayerRecorder;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.core.skinnedmodel.model.ModelInstanceTextureCreator;
import zombie.core.skinnedmodel.population.BeardStyle;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.population.ClothingItemReference;
import zombie.core.skinnedmodel.population.HairStyle;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.core.skinnedmodel.population.IClothingItemListener;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.core.skinnedmodel.visual.BaseVisual;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.textures.ColorInfo;
import zombie.core.utils.UpdateLimit;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.debug.LineDrawer;
import zombie.debug.LogSeverity;
import zombie.gameStates.IngameState;
import zombie.input.Mouse;
import zombie.interfaces.IUpdater;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Literature;
import zombie.inventory.types.Radio;
import zombie.inventory.types.WeaponType;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoRoofFixer;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LightingJNI;
import zombie.iso.LosUtil;
import zombie.iso.RoomDef;
import zombie.iso.Vector2;
import zombie.iso.Vector3;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.objects.IsoBall;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoFallingClothing;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoMolotovCocktail;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.objects.RainManager;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.NetworkVariables;
import zombie.network.PacketValidator;
import zombie.network.ServerGUI;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.network.Userlog;
import zombie.network.chat.ChatServer;
import zombie.network.chat.ChatType;
import zombie.network.packets.hit.AttackVars;
import zombie.network.packets.hit.HitInfo;
import zombie.popman.ObjectPool;
import zombie.profanity.ProfanityFilter;
import zombie.radio.ZomboidRadio;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.Recipe;
import zombie.scripting.objects.VehicleScript;
import zombie.ui.ActionProgressBar;
import zombie.ui.TextDrawObject;
import zombie.ui.TextManager;
import zombie.ui.TutorialManager;
import zombie.ui.UIFont;
import zombie.ui.UIManager;
import zombie.util.Pool;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PathFindBehavior2;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleLight;
import zombie.vehicles.VehiclePart;

public abstract class IsoGameCharacter
    extends IsoMovingObject
    implements Talker,
    ChatElementOwner,
    IAnimatable,
    IAnimationVariableMap,
    IClothingItemListener,
    IActionStateChanged,
    IAnimEventCallback,
    IFMODParameterUpdater,
    ILuaVariableSource,
    ILuaGameCharacter {
    private boolean ignoreAimingInput = false;
    public boolean doRenderShadow = true;
    private boolean doDeathSound = true;
    private boolean canShout = true;
    public boolean doDirtBloodEtc = true;
    private static int IID = 0;
    public static final int RENDER_OFFSET_X = 1;
    public static final int RENDER_OFFSET_Y = -89;
    public static final float s_maxPossibleTwist = 70.0F;
    private static final HashMap<Integer, SurvivorDesc> SurvivorMap = new HashMap<>();
    private static final int[] LevelUpLevels = new int[]{
        25,
        75,
        150,
        225,
        300,
        400,
        500,
        600,
        700,
        800,
        900,
        1000,
        1200,
        1400,
        1600,
        1800,
        2000,
        2200,
        2400,
        2600,
        2800,
        3000,
        3200,
        3400,
        3600,
        3800,
        4000,
        4400,
        4800,
        5200,
        5600,
        6000
    };
    protected static final Vector2 tempo = new Vector2();
    protected static final ColorInfo inf = new ColorInfo();
    public long vocalEvent;
    public long removedFromWorldMS = 0L;
    private boolean bAutoWalk = false;
    private final Vector2 autoWalkDirection = new Vector2();
    private boolean bSneaking = false;
    protected static final Vector2 tempo2 = new Vector2();
    private static final Vector2 tempVector2_1 = new Vector2();
    private static final Vector2 tempVector2_2 = new Vector2();
    private static String sleepText = null;
    protected final ArrayList<InventoryItem> savedInventoryItems = new ArrayList<>();
    private final String instancename;
    protected GameCharacterAIBrain GameCharacterAIBrain;
    public final ArrayList<String> amputations = new ArrayList<>();
    public ModelInstance hair;
    public ModelInstance beard;
    public ModelInstance primaryHandModel;
    public ModelInstance secondaryHandModel;
    public final ActionContext actionContext = new ActionContext(this);
    public final BaseCharacterSoundEmitter emitter;
    private final FMODParameterList fmodParameters = new FMODParameterList();
    private final AnimationVariableSource m_GameVariables = new AnimationVariableSource();
    private AnimationVariableSource m_PlaybackGameVariables = null;
    private boolean bRunning = false;
    private boolean bSprinting = false;
    private boolean m_godMod = false;
    private boolean m_invisible = false;
    private boolean m_avoidDamage = false;
    public boolean callOut = false;
    public IsoGameCharacter ReanimatedCorpse;
    public int ReanimatedCorpseID = -1;
    private AnimationPlayer m_animPlayer = null;
    public final AdvancedAnimator advancedAnimator;
    public final HashMap<State, HashMap<Object, Object>> StateMachineParams = new HashMap<>();
    public long clientIgnoreCollision = 0L;
    private boolean isCrit = false;
    private boolean bKnockedDown = false;
    public int bumpNbr = 0;
    public boolean upKillCount = true;
    private final ArrayList<IsoGameCharacter.PerkInfo> PerkList = new ArrayList<>();
    private final Vector2 m_forwardDirection = new Vector2();
    public boolean Asleep = false;
    public boolean blockTurning = false;
    public float speedMod = 1.0F;
    public IsoSprite legsSprite;
    private boolean bFemale = true;
    public float knockbackAttackMod = 1.0F;
    public final boolean[] IsVisibleToPlayer = new boolean[4];
    public float savedVehicleX;
    public float savedVehicleY;
    public short savedVehicleSeat = -1;
    public boolean savedVehicleRunning;
    private static final float RecoilDelayDecrease = 0.625F;
    protected static final float BeenMovingForIncrease = 1.25F;
    protected static final float BeenMovingForDecrease = 0.625F;
    private IsoGameCharacter FollowingTarget = null;
    private final ArrayList<IsoMovingObject> LocalList = new ArrayList<>();
    private final ArrayList<IsoMovingObject> LocalNeutralList = new ArrayList<>();
    private final ArrayList<IsoMovingObject> LocalGroupList = new ArrayList<>();
    private final ArrayList<IsoMovingObject> LocalRelevantEnemyList = new ArrayList<>();
    private float dangerLevels = 0.0F;
    private static final Vector2 tempVector2 = new Vector2();
    private float leaveBodyTimedown = 0.0F;
    protected boolean AllowConversation = true;
    private float ReanimateTimer;
    private int ReanimAnimFrame;
    private int ReanimAnimDelay;
    private boolean Reanim = false;
    private boolean VisibleToNPCs = true;
    private int DieCount = 0;
    private float llx = 0.0F;
    private float lly = 0.0F;
    private float llz = 0.0F;
    protected int RemoteID = -1;
    protected int NumSurvivorsInVicinity = 0;
    private float LevelUpMultiplier = 2.5F;
    protected IsoGameCharacter.XP xp = null;
    private int LastLocalEnemies = 0;
    private final ArrayList<IsoMovingObject> VeryCloseEnemyList = new ArrayList<>();
    private final HashMap<String, IsoGameCharacter.Location> LastKnownLocation = new HashMap<>();
    protected IsoGameCharacter AttackedBy = null;
    protected boolean IgnoreStaggerBack = false;
    protected boolean AttackWasSuperAttack = false;
    private int TimeThumping = 0;
    private int PatienceMax = 150;
    private int PatienceMin = 20;
    private int Patience = 20;
    protected final Stack<BaseAction> CharacterActions = new Stack<>();
    private int ZombieKills = 0;
    private int SurvivorKills = 0;
    private int LastZombieKills = 0;
    protected boolean superAttack = false;
    protected float ForceWakeUpTime = -1.0F;
    private float fullSpeedMod = 1.0F;
    protected float runSpeedModifier = 1.0F;
    private float walkSpeedModifier = 1.0F;
    private float combatSpeedModifier = 1.0F;
    private boolean bRangedWeaponEmpty = false;
    public ArrayList<InventoryContainer> bagsWorn;
    protected boolean ForceWakeUp;
    protected final BodyDamage BodyDamage;
    private BodyDamage BodyDamageRemote = null;
    private State defaultState;
    protected WornItems wornItems = null;
    protected AttachedItems attachedItems = null;
    protected ClothingWetness clothingWetness = null;
    protected SurvivorDesc descriptor;
    private final Stack<IsoBuilding> FamiliarBuildings = new Stack<>();
    protected final AStarPathFinderResult finder = new AStarPathFinderResult();
    private float FireKillRate = 0.0038F;
    private int FireSpreadProbability = 6;
    protected float Health = 1.0F;
    protected boolean bDead = false;
    protected boolean bKill = false;
    protected boolean bPlayingDeathSound = false;
    private boolean bDeathDragDown = false;
    protected String hurtSound = "MaleZombieHurt";
    protected ItemContainer inventory = new ItemContainer();
    protected InventoryItem leftHandItem;
    private int NextWander = 200;
    private boolean OnFire = false;
    private int pathIndex = 0;
    protected InventoryItem rightHandItem;
    protected Color SpeakColour = new Color(1.0F, 1.0F, 1.0F, 1.0F);
    protected float slowFactor = 0.0F;
    protected float slowTimer = 0.0F;
    protected boolean bUseParts = false;
    protected boolean Speaking = false;
    private float SpeakTime = 0.0F;
    private float staggerTimeMod = 1.0F;
    protected final StateMachine stateMachine;
    protected final Moodles Moodles;
    protected final Stats stats = new Stats();
    private final Stack<String> UsedItemsOn = new Stack<>();
    protected HandWeapon useHandWeapon = null;
    protected IsoGridSquare attackTargetSquare;
    private float BloodImpactX = 0.0F;
    private float BloodImpactY = 0.0F;
    private float BloodImpactZ = 0.0F;
    private IsoSprite bloodSplat;
    private boolean bOnBed = false;
    private final Vector2 moveForwardVec = new Vector2();
    protected boolean pathing = false;
    protected ChatElement chatElement;
    private final Stack<IsoGameCharacter> LocalEnemyList = new Stack<>();
    protected final Stack<IsoGameCharacter> EnemyList = new Stack<>();
    public final IsoGameCharacter.CharacterTraits Traits = new IsoGameCharacter.CharacterTraits();
    private int maxWeight = 8;
    private int maxWeightBase = 8;
    private float SleepingTabletDelta = 1.0F;
    private float BetaEffect = 0.0F;
    private float DepressEffect = 0.0F;
    private float SleepingTabletEffect = 0.0F;
    private float BetaDelta = 0.0F;
    private float DepressDelta = 0.0F;
    private float DepressFirstTakeTime = -1.0F;
    private float PainEffect = 0.0F;
    private float PainDelta = 0.0F;
    private boolean bDoDefer = true;
    private float haloDispTime = 128.0F;
    protected TextDrawObject userName;
    private TextDrawObject haloNote;
    private final HashMap<String, String> namesPrefix = new HashMap<>();
    private static final String namePvpSuffix = " [img=media/ui/Skull.png]";
    private static final String nameCarKeySuffix = " [img=media/ui/CarKey.png";
    private static final String voiceSuffix = "[img=media/ui/voiceon.png] ";
    private static final String voiceMuteSuffix = "[img=media/ui/voicemuted.png] ";
    protected IsoPlayer isoPlayer = null;
    private boolean hasInitTextObjects = false;
    private boolean canSeeCurrent = false;
    private boolean drawUserName = false;
    private final IsoGameCharacter.Location LastHeardSound = new IsoGameCharacter.Location(-1, -1, -1);
    private float lrx = 0.0F;
    private float lry = 0.0F;
    protected boolean bClimbing = false;
    private boolean lastCollidedW = false;
    private boolean lastCollidedN = false;
    protected float fallTime = 0.0F;
    protected float lastFallSpeed = 0.0F;
    protected boolean bFalling = false;
    protected BaseVehicle vehicle = null;
    boolean isNPC = false;
    private long lastBump = 0L;
    private IsoGameCharacter bumpedChr = null;
    private boolean m_isCulled = true;
    private int age = 25;
    private int lastHitCount = 0;
    private Safety safety = new Safety(this);
    private float meleeDelay = 0.0F;
    private float RecoilDelay = 0.0F;
    private float BeenMovingFor = 0.0F;
    private float BeenSprintingFor = 0.0F;
    private boolean forceShove = false;
    private String clickSound = null;
    private float reduceInfectionPower = 0.0F;
    private final List<String> knownRecipes = new ArrayList<>();
    private final HashSet<String> knownMediaLines = new HashSet<>();
    private int lastHourSleeped = 0;
    protected float timeOfSleep = 0.0F;
    protected float delayToActuallySleep = 0.0F;
    private String bedType = "averageBed";
    private IsoObject bed = null;
    private boolean isReading = false;
    private float timeSinceLastSmoke = 0.0F;
    private boolean wasOnStairs = false;
    private ChatMessage lastChatMessage;
    private String lastSpokenLine;
    private boolean unlimitedEndurance = false;
    private boolean unlimitedCarry = false;
    private boolean buildCheat = false;
    private boolean farmingCheat = false;
    private boolean healthCheat = false;
    private boolean mechanicsCheat = false;
    private boolean movablesCheat = false;
    private boolean timedActionInstantCheat = false;
    private boolean showAdminTag = true;
    private long isAnimForecasted = 0L;
    private boolean fallOnFront = false;
    private boolean hitFromBehind = false;
    private String hitReaction = "";
    private String bumpType = "";
    private boolean m_isBumpDone = false;
    private boolean m_bumpFall = false;
    private boolean m_bumpStaggered = false;
    private String m_bumpFallType = "";
    private int sleepSpeechCnt = 0;
    private Radio equipedRadio;
    private InventoryItem leftHandCache;
    private InventoryItem rightHandCache;
    private final ArrayList<IsoGameCharacter.ReadBook> ReadBooks = new ArrayList<>();
    private final IsoGameCharacter.LightInfo lightInfo = new IsoGameCharacter.LightInfo();
    private final IsoGameCharacter.LightInfo lightInfo2 = new IsoGameCharacter.LightInfo();
    private PolygonalMap2.Path path2;
    private final MapKnowledge mapKnowledge = new MapKnowledge();
    public final AttackVars attackVars = new AttackVars();
    public final ArrayList<HitInfo> hitList = new ArrayList<>();
    private final PathFindBehavior2 pfb2 = new PathFindBehavior2(this);
    private final InventoryItem[] cacheEquiped = new InventoryItem[2];
    private boolean bAimAtFloor = false;
    protected int m_persistentOutfitId = 0;
    protected boolean m_bPersistentOutfitInit = false;
    private boolean bUpdateModelTextures = false;
    private ModelInstanceTextureCreator textureCreator = null;
    public boolean bUpdateEquippedTextures = false;
    private final ArrayList<ModelInstance> readyModelData = new ArrayList<>();
    private boolean sitOnGround = false;
    private boolean ignoreMovement = false;
    private boolean hideWeaponModel = false;
    private boolean isAiming = false;
    private float beardGrowTiming = -1.0F;
    private float hairGrowTiming = -1.0F;
    private float m_moveDelta = 1.0F;
    protected float m_turnDeltaNormal = 1.0F;
    protected float m_turnDeltaRunning = 0.8F;
    protected float m_turnDeltaSprinting = 0.75F;
    private float m_maxTwist = 15.0F;
    private boolean m_isMoving = false;
    private boolean m_isTurning = false;
    private boolean m_isTurningAround = false;
    private boolean m_isTurning90 = false;
    public long lastAutomaticShoot = 0L;
    public int shootInARow = 0;
    private boolean invincible = false;
    private float lungeFallTimer = 0.0F;
    private SleepingEventData m_sleepingEventData;
    private final int HAIR_GROW_TIME = 20;
    private final int BEARD_GROW_TIME = 5;
    public float realx = 0.0F;
    public float realy = 0.0F;
    public byte realz = 0;
    public NetworkVariables.ZombieState realState = NetworkVariables.ZombieState.Idle;
    public IsoDirections realdir = IsoDirections.fromIndex(0);
    public String overridePrimaryHandModel = null;
    public String overrideSecondaryHandModel = null;
    public boolean forceNullOverride = false;
    protected final UpdateLimit ulBeatenVehicle = new UpdateLimit(200L);
    private float m_momentumScalar = 0.0F;
    private final HashMap<String, State> m_stateUpdateLookup = new HashMap<>();
    private boolean attackAnim = false;
    private NetworkTeleport teleport = null;
    @Deprecated
    public ArrayList<Integer> invRadioFreq = new ArrayList<>();
    private final PredicatedFileWatcher m_animStateTriggerWatcher;
    private final AnimationPlayerRecorder m_animationRecorder;
    private final String m_UID;
    private boolean m_bDebugVariablesRegistered = false;
    private float effectiveEdibleBuffTimer = 0.0F;
    private float m_shadowFM = 0.0F;
    private float m_shadowBM = 0.0F;
    private long shadowTick = -1L;
    private static final ItemVisuals tempItemVisuals = new ItemVisuals();
    private static final ArrayList<IsoMovingObject> movingStatic = new ArrayList<>();
    private long m_muzzleFlash = -1L;
    private static final IsoGameCharacter.Bandages s_bandages = new IsoGameCharacter.Bandages();
    private static final Vector3 tempVector = new Vector3();
    private static final Vector3 tempVectorBonePos = new Vector3();
    public final NetworkCharacter networkCharacter = new NetworkCharacter();

    public IsoGameCharacter(IsoCell cell, float x, float y, float z) {
        super(cell, false);
        this.m_UID = String.format("%s-%s", this.getClass().getSimpleName(), UUID.randomUUID().toString());
        this.registerVariableCallbacks();
        this.instancename = "Character" + IID;
        IID++;
        if (!(this instanceof IsoSurvivor)) {
            this.emitter = (BaseCharacterSoundEmitter)(!Core.SoundDisabled && !GameServer.bServer
                ? new CharacterSoundEmitter(this)
                : new DummyCharacterSoundEmitter(this));
        } else {
            this.emitter = null;
        }

        if (x != 0.0F || y != 0.0F || z != 0.0F) {
            if (this.getCell().isSafeToAdd()) {
                this.getCell().getObjectList().add(this);
            } else {
                this.getCell().getAddList().add(this);
            }
        }

        if (this.def == null) {
            this.def = IsoSpriteInstance.get(this.sprite);
        }

        if (this instanceof IsoPlayer) {
            this.BodyDamage = new BodyDamage(this);
            this.Moodles = new Moodles(this);
            this.xp = new IsoGameCharacter.XP(this);
        } else {
            this.BodyDamage = null;
            this.Moodles = null;
            this.xp = null;
        }

        this.Patience = Rand.Next(this.PatienceMin, this.PatienceMax);
        this.x = x + 0.5F;
        this.y = y + 0.5F;
        this.z = z;
        this.scriptnx = this.lx = this.nx = x;
        this.scriptny = this.ly = this.ny = y;
        if (cell != null) {
            this.current = this.getCell().getGridSquare((int)x, (int)y, (int)z);
        }

        this.offsetY = 0.0F;
        this.offsetX = 0.0F;
        this.stateMachine = new StateMachine(this);
        this.setDefaultState(IdleState.instance());
        this.inventory.parent = this;
        this.inventory.setExplored(true);
        this.chatElement = new ChatElement(this, 1, "character");
        if (GameClient.bClient || GameServer.bServer) {
            this.namesPrefix.put("admin", "[col=255,0,0]Admin[/] ");
            this.namesPrefix.put("moderator", "[col=0,128,47]Moderator[/] ");
            this.namesPrefix.put("overseer", "[col=26,26,191]Overseer[/] ");
            this.namesPrefix.put("gm", "[col=213,123,23]GM[/] ");
            this.namesPrefix.put("observer", "[col=128,128,128]Observer[/] ");
        }

        this.m_animationRecorder = new AnimationPlayerRecorder(this);
        this.advancedAnimator = new AdvancedAnimator();
        this.advancedAnimator.init(this);
        this.advancedAnimator.animCallbackHandlers.add(this);
        this.advancedAnimator.SetAnimSet(AnimationSet.GetAnimationSet(this.GetAnimSetName(), false));
        this.advancedAnimator.setRecorder(this.m_animationRecorder);
        this.actionContext.onStateChanged.add(this);
        this.m_animStateTriggerWatcher = new PredicatedFileWatcher(
            ZomboidFileSystem.instance.getMessagingDirSub("Trigger_SetAnimState.xml"), AnimStateTriggerXmlFile.class, this::onTrigger_setAnimStateToTriggerFile
        );
    }

    private void registerVariableCallbacks() {
        this.setVariable("hitreaction", this::getHitReaction, this::setHitReaction);
        this.setVariable("collidetype", this::getCollideType, this::setCollideType);
        this.setVariable("footInjuryType", this::getFootInjuryType);
        this.setVariable("bumptype", this::getBumpType, this::setBumpType);
        this.setVariable("sitonground", this::isSitOnGround, this::setSitOnGround);
        this.setVariable("canclimbdownrope", this::canClimbDownSheetRopeInCurrentSquare);
        this.setVariable("frombehind", this::isHitFromBehind, this::setHitFromBehind);
        this.setVariable("fallonfront", this::isFallOnFront, this::setFallOnFront);
        this.setVariable("hashitreaction", this::hasHitReaction);
        this.setVariable("intrees", this::isInTreesNoBush);
        this.setVariable("bumped", this::isBumped);
        this.setVariable("BumpDone", false, this::isBumpDone, this::setBumpDone);
        this.setVariable("BumpFall", false, this::isBumpFall, this::setBumpFall);
        this.setVariable("BumpFallType", "", this::getBumpFallType, this::setBumpFallType);
        this.setVariable("BumpStaggered", false, this::isBumpStaggered, this::setBumpStaggered);
        this.setVariable("bonfloor", this::isOnFloor, this::setOnFloor);
        this.setVariable("rangedweaponempty", this::isRangedWeaponEmpty, this::setRangedWeaponEmpty);
        this.setVariable("footInjury", this::hasFootInjury);
        this.setVariable("ChopTreeSpeed", 1.0F, this::getChopTreeSpeed);
        this.setVariable("MoveDelta", 1.0F, this::getMoveDelta, this::setMoveDelta);
        this.setVariable("TurnDelta", 1.0F, this::getTurnDelta, this::setTurnDelta);
        this.setVariable("angle", this::getDirectionAngle, this::setDirectionAngle);
        this.setVariable("animAngle", this::getAnimAngle);
        this.setVariable("twist", this::getTwist);
        this.setVariable("targetTwist", this::getTargetTwist);
        this.setVariable("maxTwist", this.m_maxTwist, this::getMaxTwist, this::setMaxTwist);
        this.setVariable("shoulderTwist", this::getShoulderTwist);
        this.setVariable("excessTwist", this::getExcessTwist);
        this.setVariable("angleStepDelta", this::getAnimAngleStepDelta);
        this.setVariable("angleTwistDelta", this::getAnimAngleTwistDelta);
        this.setVariable("isTurning", false, this::isTurning, this::setTurning);
        this.setVariable("isTurning90", false, this::isTurning90, this::setTurning90);
        this.setVariable("isTurningAround", false, this::isTurningAround, this::setTurningAround);
        this.setVariable("bMoving", false, this::isMoving, this::setMoving);
        this.setVariable("beenMovingFor", this::getBeenMovingFor);
        this.setVariable("previousState", this::getPreviousActionContextStateName);
        this.setVariable("momentumScalar", this::getMomentumScalar, this::setMomentumScalar);
        this.setVariable("hasTimedActions", this::hasTimedActions);
        if (DebugOptions.instance.Character.Debug.RegisterDebugVariables.getValue()) {
            this.registerDebugGameVariables();
        }

        this.setVariable("CriticalHit", this::isCriticalHit, this::setCriticalHit);
        this.setVariable("bKnockedDown", this::isKnockedDown, this::setKnockedDown);
        this.setVariable("bdead", this::isDead);
    }

    public void updateRecoilVar() {
        this.setVariable("recoilVarY", 0.0F);
        this.setVariable("recoilVarX", 0.0F + this.getPerkLevel(PerkFactory.Perks.Aiming) / 10.0F);
    }

    private void registerDebugGameVariables() {
        for (int int0 = 0; int0 < 2; int0++) {
            for (int int1 = 0; int1 < 9; int1++) {
                this.dbgRegisterAnimTrackVariable(int0, int1);
            }
        }

        this.setVariable("dbg.anm.dx", () -> this.getDeferredMovement(tempo).x / GameTime.instance.getMultiplier());
        this.setVariable("dbg.anm.dy", () -> this.getDeferredMovement(tempo).y / GameTime.instance.getMultiplier());
        this.setVariable("dbg.anm.da", () -> this.getDeferredAngleDelta() / GameTime.instance.getMultiplier());
        this.setVariable("dbg.anm.daw", this::getDeferredRotationWeight);
        this.setVariable("dbg.forward", () -> this.getForwardDirection().x + "; " + this.getForwardDirection().y);
        this.setVariable("dbg.anm.blend.fbx_x", () -> DebugOptions.instance.Animation.BlendUseFbx.getValue() ? 1.0F : 0.0F);
        this.m_bDebugVariablesRegistered = true;
    }

    private void dbgRegisterAnimTrackVariable(int int0, int int1) {
        this.setVariable(String.format("dbg.anm.track%d%d", int0, int1), () -> this.dbgGetAnimTrackName(int0, int1));
        this.setVariable(String.format("dbg.anm.t.track%d%d", int0, int1), () -> this.dbgGetAnimTrackTime(int0, int1));
        this.setVariable(String.format("dbg.anm.w.track%d%d", int0, int1), () -> this.dbgGetAnimTrackWeight(int0, int1));
    }

    public float getMomentumScalar() {
        return this.m_momentumScalar;
    }

    public void setMomentumScalar(float val) {
        this.m_momentumScalar = val;
    }

    public Vector2 getDeferredMovement(Vector2 out_result) {
        if (this.m_animPlayer == null) {
            out_result.set(0.0F, 0.0F);
            return out_result;
        } else {
            this.m_animPlayer.getDeferredMovement(out_result);
            return out_result;
        }
    }

    public float getDeferredAngleDelta() {
        return this.m_animPlayer == null ? 0.0F : this.m_animPlayer.getDeferredAngleDelta() * (180.0F / (float)Math.PI);
    }

    public float getDeferredRotationWeight() {
        return this.m_animPlayer == null ? 0.0F : this.m_animPlayer.getDeferredRotationWeight();
    }

    public boolean isStrafing() {
        return this.getPath2() != null && this.pfb2.isStrafing() ? true : this.isAiming();
    }

    public AnimationTrack dbgGetAnimTrack(int layerIdx, int trackIdx) {
        if (this.m_animPlayer == null) {
            return null;
        } else {
            AnimationPlayer animationPlayer = this.m_animPlayer;
            AnimationMultiTrack animationMultiTrack = animationPlayer.getMultiTrack();
            List list = animationMultiTrack.getTracks();
            AnimationTrack animationTrack0 = null;
            int int0 = 0;
            int int1 = 0;

            for (int int2 = list.size(); int0 < int2; int0++) {
                AnimationTrack animationTrack1 = (AnimationTrack)list.get(int0);
                int int3 = animationTrack1.getLayerIdx();
                if (int3 == layerIdx) {
                    if (int1 == trackIdx) {
                        animationTrack0 = animationTrack1;
                        break;
                    }

                    int1++;
                }
            }

            return animationTrack0;
        }
    }

    public String dbgGetAnimTrackName(int layerIdx, int trackIdx) {
        AnimationTrack animationTrack = this.dbgGetAnimTrack(layerIdx, trackIdx);
        return animationTrack != null ? animationTrack.name : "";
    }

    public float dbgGetAnimTrackTime(int layerIdx, int trackIdx) {
        AnimationTrack animationTrack = this.dbgGetAnimTrack(layerIdx, trackIdx);
        return animationTrack != null ? animationTrack.getCurrentTime() : 0.0F;
    }

    public float dbgGetAnimTrackWeight(int layerIdx, int trackIdx) {
        AnimationTrack animationTrack = this.dbgGetAnimTrack(layerIdx, trackIdx);
        return animationTrack != null ? animationTrack.BlendDelta : 0.0F;
    }

    /**
     * The character's current twist angle, in degrees.
     */
    public float getTwist() {
        return this.m_animPlayer != null ? (180.0F / (float)Math.PI) * this.m_animPlayer.getTwistAngle() : 0.0F;
    }

    /**
     * The character's current shoulder-twist angle, in degrees.
     */
    public float getShoulderTwist() {
        return this.m_animPlayer != null ? (180.0F / (float)Math.PI) * this.m_animPlayer.getShoulderTwistAngle() : 0.0F;
    }

    /**
     * The maximum twist angle, in degrees.
     */
    public float getMaxTwist() {
        return this.m_maxTwist;
    }

    /**
     * Specify the maximum twist angle, in degrees.
     */
    public void setMaxTwist(float degrees) {
        this.m_maxTwist = degrees;
    }

    /**
     * The character's excess twist, in degrees.   The excess is > 0 if the character is trying to twist further than their current maximum twist.   ie. The amount that the desired twist exceeds the maximum twist.    eg. If the character is trying to twist by 90 degrees, but their maximum is set to 70, then excess = 20
     */
    public float getExcessTwist() {
        return this.m_animPlayer != null ? (180.0F / (float)Math.PI) * this.m_animPlayer.getExcessTwistAngle() : 0.0F;
    }

    public float getAbsoluteExcessTwist() {
        return Math.abs(this.getExcessTwist());
    }

    public float getAnimAngleTwistDelta() {
        return this.m_animPlayer != null ? this.m_animPlayer.angleTwistDelta : 0.0F;
    }

    public float getAnimAngleStepDelta() {
        return this.m_animPlayer != null ? this.m_animPlayer.angleStepDelta : 0.0F;
    }

    /**
     * The desired twist, unclamped, in degrees.
     */
    public float getTargetTwist() {
        return this.m_animPlayer != null ? (180.0F / (float)Math.PI) * this.m_animPlayer.getTargetTwistAngle() : 0.0F;
    }

    @Override
    public boolean isRangedWeaponEmpty() {
        return this.bRangedWeaponEmpty;
    }

    @Override
    public void setRangedWeaponEmpty(boolean val) {
        this.bRangedWeaponEmpty = val;
    }

    public boolean hasFootInjury() {
        return !StringUtils.isNullOrWhitespace(this.getFootInjuryType());
    }

    public boolean isInTrees2(boolean ignoreBush) {
        if (this.isCurrentState(BumpedState.instance())) {
            return false;
        } else {
            IsoGridSquare square = this.getCurrentSquare();
            if (square == null) {
                return false;
            } else {
                if (square.Has(IsoObjectType.tree)) {
                    IsoTree tree = square.getTree();
                    if (tree == null || ignoreBush && tree.getSize() > 2 || !ignoreBush) {
                        return true;
                    }
                }

                String string = square.getProperties().Val("Movement");
                return !"HedgeLow".equalsIgnoreCase(string) && !"HedgeHigh".equalsIgnoreCase(string) ? !ignoreBush && square.getProperties().Is("Bush") : true;
            }
        }
    }

    public boolean isInTreesNoBush() {
        return this.isInTrees2(true);
    }

    public boolean isInTrees() {
        return this.isInTrees2(false);
    }

    /**
     * @return the SurvivorMap
     */
    public static HashMap<Integer, SurvivorDesc> getSurvivorMap() {
        return SurvivorMap;
    }

    public static int[] getLevelUpLevels() {
        return LevelUpLevels;
    }

    /**
     * @return the tempo
     */
    public static Vector2 getTempo() {
        return tempo;
    }

    /**
     * @return the inf
     */
    public static ColorInfo getInf() {
        return inf;
    }

    public GameCharacterAIBrain getBrain() {
        return this.GameCharacterAIBrain;
    }

    public boolean getIsNPC() {
        return this.isNPC;
    }

    public void setIsNPC(boolean isAI) {
        this.isNPC = isAI;
    }

    @Override
    public BaseCharacterSoundEmitter getEmitter() {
        return this.emitter;
    }

    public void updateEmitter() {
        this.getFMODParameters().update();
        if (IsoWorld.instance.emitterUpdate || this.emitter.hasSoundsToStart()) {
            if (this.isZombie() && this.isProne()) {
                SwipeStatePlayer.getBoneWorldPos(this, "Bip01_Head", tempVectorBonePos);
                this.emitter.set(tempVectorBonePos.x, tempVectorBonePos.y, this.z);
                this.emitter.tick();
            } else {
                this.emitter.set(this.x, this.y, this.z);
                this.emitter.tick();
            }
        }
    }

    protected void doDeferredMovement() {
        if (GameClient.bClient && this.getHitReactionNetworkAI() != null) {
            if (this.getHitReactionNetworkAI().isStarted()) {
                this.getHitReactionNetworkAI().move();
                return;
            }

            if (this.isDead() && this.getHitReactionNetworkAI().isDoSkipMovement()) {
                return;
            }
        }

        AnimationPlayer animationPlayer = this.getAnimationPlayer();
        if (animationPlayer != null) {
            if (this.getPath2() != null && !this.isCurrentState(ClimbOverFenceState.instance()) && !this.isCurrentState(ClimbThroughWindowState.instance())) {
                if (this.isCurrentState(WalkTowardState.instance())) {
                    DebugLog.General.warn("WalkTowardState but path2 != null");
                    this.setPath2(null);
                }
            } else {
                if (GameClient.bClient) {
                    if (this instanceof IsoZombie && ((IsoZombie)this).isRemoteZombie()) {
                        if (this.getCurrentState() != ClimbOverFenceState.instance()
                            && this.getCurrentState() != ClimbThroughWindowState.instance()
                            && this.getCurrentState() != ClimbOverWallState.instance()
                            && this.getCurrentState() != StaggerBackState.instance()
                            && this.getCurrentState() != ZombieHitReactionState.instance()
                            && this.getCurrentState() != ZombieFallDownState.instance()
                            && this.getCurrentState() != ZombieFallingState.instance()
                            && this.getCurrentState() != ZombieOnGroundState.instance()
                            && this.getCurrentState() != AttackNetworkState.instance()) {
                            return;
                        }
                    } else if (this instanceof IsoPlayer
                        && !((IsoPlayer)this).isLocalPlayer()
                        && !this.isCurrentState(CollideWithWallState.instance())
                        && !this.isCurrentState(PlayerGetUpState.instance())
                        && !this.isCurrentState(BumpedState.instance())) {
                        return;
                    }
                }

                Vector2 vector = tempo;
                this.getDeferredMovement(vector);
                if (GameClient.bClient && this instanceof IsoZombie && this.isCurrentState(StaggerBackState.instance())) {
                    float float0 = vector.getLength();
                    vector.set(this.getHitDir());
                    vector.setLength(float0);
                }

                this.MoveUnmodded(vector);
            }
        }
    }

    @Override
    public ActionContext getActionContext() {
        return null;
    }

    public String getPreviousActionContextStateName() {
        ActionContext actionContextx = this.getActionContext();
        return actionContextx == null ? "" : actionContextx.getPreviousStateName();
    }

    public String getCurrentActionContextStateName() {
        ActionContext actionContextx = this.getActionContext();
        return actionContextx == null ? "" : actionContextx.getCurrentStateName();
    }

    public boolean hasAnimationPlayer() {
        return this.m_animPlayer != null;
    }

    @Override
    public AnimationPlayer getAnimationPlayer() {
        Model model = ModelManager.instance.getBodyModel(this);
        boolean boolean0 = false;
        if (this.m_animPlayer != null && this.m_animPlayer.getModel() != model) {
            boolean0 = this.m_animPlayer.getMultiTrack().getTrackCount() > 0;
            this.m_animPlayer = Pool.tryRelease(this.m_animPlayer);
        }

        if (this.m_animPlayer == null) {
            this.m_animPlayer = AnimationPlayer.alloc(model);
            this.onAnimPlayerCreated(this.m_animPlayer);
            if (boolean0) {
                this.getAdvancedAnimator().OnAnimDataChanged(false);
            }
        }

        return this.m_animPlayer;
    }

    public void releaseAnimationPlayer() {
        this.m_animPlayer = Pool.tryRelease(this.m_animPlayer);
    }

    protected void onAnimPlayerCreated(AnimationPlayer animationPlayer) {
        animationPlayer.setRecorder(this.m_animationRecorder);
        animationPlayer.setTwistBones("Bip01_Pelvis", "Bip01_Spine", "Bip01_Spine1", "Bip01_Neck", "Bip01_Head");
        animationPlayer.setCounterRotationBone("Bip01");
    }

    protected void updateAnimationRecorderState() {
        if (this.m_animPlayer != null) {
            if (IsoWorld.isAnimRecorderDiscardTriggered()) {
                this.m_animPlayer.discardRecording();
            }

            boolean boolean0 = IsoWorld.isAnimRecorderActive();
            boolean boolean1 = boolean0 && !this.isSceneCulled();
            if (boolean1) {
                this.getAnimationPlayerRecorder().logCharacterPos();
            }

            this.m_animPlayer.setRecording(boolean1);
        }
    }

    @Override
    public AdvancedAnimator getAdvancedAnimator() {
        return this.advancedAnimator;
    }

    @Override
    public ModelInstance getModelInstance() {
        if (this.legsSprite == null) {
            return null;
        } else {
            return this.legsSprite.modelSlot == null ? null : this.legsSprite.modelSlot.model;
        }
    }

    public String getCurrentStateName() {
        return this.stateMachine.getCurrent() == null ? null : this.stateMachine.getCurrent().getName();
    }

    public String getPreviousStateName() {
        return this.stateMachine.getPrevious() == null ? null : this.stateMachine.getPrevious().getName();
    }

    public String getAnimationDebug() {
        return this.advancedAnimator != null ? this.instancename + "\n" + this.advancedAnimator.GetDebug() : this.instancename + "\n - No Animator";
    }

    @Override
    public String getTalkerType() {
        return this.chatElement.getTalkerType();
    }

    public boolean isAnimForecasted() {
        return System.currentTimeMillis() < this.isAnimForecasted;
    }

    public void setAnimForecasted(int timeMs) {
        this.isAnimForecasted = System.currentTimeMillis() + timeMs;
    }

    @Override
    public void resetModel() {
        ModelManager.instance.Reset(this);
    }

    @Override
    public void resetModelNextFrame() {
        ModelManager.instance.ResetNextFrame(this);
    }

    protected void onTrigger_setClothingToXmlTriggerFile(TriggerXmlFile triggerXmlFile) {
        OutfitManager.Reload();
        if (!StringUtils.isNullOrWhitespace(triggerXmlFile.outfitName)) {
            String string0 = triggerXmlFile.outfitName;
            DebugLog.Clothing.debugln("Desired outfit name: " + string0);
            Outfit outfit;
            if (triggerXmlFile.isMale) {
                outfit = OutfitManager.instance.FindMaleOutfit(string0);
            } else {
                outfit = OutfitManager.instance.FindFemaleOutfit(string0);
            }

            if (outfit == null) {
                DebugLog.Clothing.error("Could not find outfit: " + string0);
                return;
            }

            if (this.bFemale == triggerXmlFile.isMale && this instanceof IHumanVisual) {
                ((IHumanVisual)this).getHumanVisual().clear();
            }

            this.bFemale = !triggerXmlFile.isMale;
            if (this.descriptor != null) {
                this.descriptor.setFemale(this.bFemale);
            }

            this.dressInNamedOutfit(outfit.m_Name);
            this.advancedAnimator.OnAnimDataChanged(false);
            if (this instanceof IsoPlayer) {
                LuaEventManager.triggerEvent("OnClothingUpdated", this);
            }
        } else if (!StringUtils.isNullOrWhitespace(triggerXmlFile.clothingItemGUID)) {
            String string1 = "game";
            this.dressInClothingItem(string1 + "-" + triggerXmlFile.clothingItemGUID);
            if (this instanceof IsoPlayer) {
                LuaEventManager.triggerEvent("OnClothingUpdated", this);
            }
        }

        ModelManager.instance.Reset(this);
    }

    protected void onTrigger_setAnimStateToTriggerFile(AnimStateTriggerXmlFile animStateTriggerXmlFile) {
        String string = this.GetAnimSetName();
        if (!StringUtils.equalsIgnoreCase(string, animStateTriggerXmlFile.animSet)) {
            this.setVariable("dbgForceAnim", false);
            this.restoreAnimatorStateToActionContext();
        } else {
            DebugOptions.instance.Animation.AnimLayer.AllowAnimNodeOverride.setValue(animStateTriggerXmlFile.forceAnim);
            if (this.advancedAnimator.containsState(animStateTriggerXmlFile.stateName)) {
                this.setVariable("dbgForceAnim", animStateTriggerXmlFile.forceAnim);
                this.setVariable("dbgForceAnimStateName", animStateTriggerXmlFile.stateName);
                this.setVariable("dbgForceAnimNodeName", animStateTriggerXmlFile.nodeName);
                this.setVariable("dbgForceAnimScalars", animStateTriggerXmlFile.setScalarValues);
                this.setVariable("dbgForceScalar", animStateTriggerXmlFile.scalarValue);
                this.setVariable("dbgForceScalar2", animStateTriggerXmlFile.scalarValue2);
                this.advancedAnimator.SetState(animStateTriggerXmlFile.stateName);
            } else {
                DebugLog.Animation.error("State not found: " + animStateTriggerXmlFile.stateName);
                this.restoreAnimatorStateToActionContext();
            }
        }
    }

    private void restoreAnimatorStateToActionContext() {
        if (this.actionContext.getCurrentState() != null) {
            this.advancedAnimator
                .SetState(
                    this.actionContext.getCurrentStateName(), PZArrayUtil.listConvert(this.actionContext.getChildStates(), actionState -> actionState.name)
                );
        }
    }

    /**
     * clothingItemChanged  Called when a ClothingItem file has changed on disk, causing the OutfitManager to broadcast this event.  Checks if this item is currently used by this player's Outfit.  Reloads and re-equips if so.
     * 
     * @param itemGuid The item's Globally Unique Identifier (GUID).
     */
    @Override
    public void clothingItemChanged(String itemGuid) {
        if (this.wornItems != null) {
            for (int int0 = 0; int0 < this.wornItems.size(); int0++) {
                InventoryItem item = this.wornItems.getItemByIndex(int0);
                ClothingItem clothingItem = item.getClothingItem();
                if (clothingItem != null && clothingItem.isReady() && clothingItem.m_GUID.equals(itemGuid)) {
                    ClothingItemReference clothingItemReference = new ClothingItemReference();
                    clothingItemReference.itemGUID = itemGuid;
                    clothingItemReference.randomize();
                    item.getVisual().synchWithOutfit(clothingItemReference);
                    item.synchWithVisual();
                    this.resetModelNextFrame();
                }
            }
        }
    }

    public void reloadOutfit() {
        ModelManager.instance.Reset(this);
    }

    /**
     * Is this character currently culled from the visible scene graph.  Eg. Zombies not seen by the player. Objects outside the rendered window etc.
     * @return TRUE if this character should be drawn. FALSE otherwise.
     */
    public boolean isSceneCulled() {
        return this.m_isCulled;
    }

    /**
     * Specify whether this character is currently not to be drawn, as it is outside the visible area.  Eg. Zombies not seen by the player. Objects outside the rendered window etc.
     */
    public void setSceneCulled(boolean isCulled) {
        if (this.isSceneCulled() != isCulled) {
            try {
                if (isCulled) {
                    ModelManager.instance.Remove(this);
                } else {
                    ModelManager.instance.Add(this);
                }
            } catch (Exception exception) {
                System.err.println("Error in IsoGameCharacter.setSceneCulled(" + isCulled + "):");
                ExceptionLogger.logException(exception);
                ModelManager.instance.Remove(this);
                this.legsSprite.modelSlot = null;
            }
        }
    }

    /**
     * Callback from ModelManager.Add/Remove functions.
     * 
     * @param modelManager Event sender.
     * @param isCulled Whether or not this object is culled from the visible scene or not.
     */
    public void onCullStateChanged(ModelManager modelManager, boolean isCulled) {
        this.m_isCulled = isCulled;
        if (!isCulled) {
            this.restoreAnimatorStateToActionContext();
            DebugFileWatcher.instance.add(this.m_animStateTriggerWatcher);
            OutfitManager.instance.addClothingItemListener(this);
        } else {
            DebugFileWatcher.instance.remove(this.m_animStateTriggerWatcher);
            OutfitManager.instance.removeClothingItemListener(this);
        }
    }

    /**
     * Picks a random outfit from the OutfitManager
     */
    public void dressInRandomOutfit() {
        if (DebugLog.isEnabled(DebugType.Clothing)) {
            DebugLog.Clothing.println("IsoGameCharacter.dressInRandomOutfit>");
        }

        Outfit outfit = OutfitManager.instance.GetRandomOutfit(this.isFemale());
        if (outfit != null) {
            this.dressInNamedOutfit(outfit.m_Name);
        }
    }

    @Override
    public void dressInNamedOutfit(String outfitName) {
    }

    @Override
    public void dressInPersistentOutfit(String outfitName) {
        int int0 = PersistentOutfits.instance.pickOutfit(outfitName, this.isFemale());
        this.dressInPersistentOutfitID(int0);
    }

    @Override
    public void dressInPersistentOutfitID(int outfitID) {
    }

    @Override
    public String getOutfitName() {
        if (this instanceof IHumanVisual) {
            HumanVisual humanVisual = ((IHumanVisual)this).getHumanVisual();
            Outfit outfit = humanVisual.getOutfit();
            return outfit == null ? null : outfit.m_Name;
        } else {
            return null;
        }
    }

    public void dressInClothingItem(String itemGUID) {
    }

    public Outfit getRandomDefaultOutfit() {
        IsoGridSquare square = this.getCurrentSquare();
        IsoRoom room = square == null ? null : square.getRoom();
        String string = room == null ? null : room.getName();
        return ZombiesZoneDefinition.getRandomDefaultOutfit(this.isFemale(), string);
    }

    public ModelInstance getModel() {
        return this.legsSprite != null && this.legsSprite.modelSlot != null ? this.legsSprite.modelSlot.model : null;
    }

    public boolean hasActiveModel() {
        return this.legsSprite != null && this.legsSprite.hasActiveModel();
    }

    @Override
    public boolean hasItems(String type, int count) {
        int int0 = this.inventory.getItemCount(type);
        return count <= int0;
    }

    public int getLevelUpLevels(int level) {
        return LevelUpLevels.length <= level ? LevelUpLevels[LevelUpLevels.length - 1] : LevelUpLevels[level];
    }

    public int getLevelMaxForXp() {
        return LevelUpLevels.length;
    }

    @Override
    public int getXpForLevel(int level) {
        return level < LevelUpLevels.length
            ? (int)(LevelUpLevels[level] * this.LevelUpMultiplier)
            : (int)((LevelUpLevels[LevelUpLevels.length - 1] + (level - LevelUpLevels.length + 1) * 400) * this.LevelUpMultiplier);
    }

    public void DoDeath(HandWeapon weapon, IsoGameCharacter wielder) {
        this.DoDeath(weapon, wielder, true);
    }

    public void DoDeath(HandWeapon weapon, IsoGameCharacter wielder, boolean bGory) {
        this.OnDeath();
        if (this.getAttackedBy() instanceof IsoPlayer && GameServer.bServer && this instanceof IsoPlayer) {
            String string0 = "";
            String string1 = "";
            if (SteamUtils.isSteamModeEnabled()) {
                string0 = " (" + ((IsoPlayer)this.getAttackedBy()).getSteamID() + ") ";
                string1 = " (" + ((IsoPlayer)this).getSteamID() + ") ";
            }

            LoggerManager.getLogger("pvp")
                .write(
                    "user "
                        + ((IsoPlayer)this.getAttackedBy()).username
                        + string0
                        + " killed "
                        + ((IsoPlayer)this).username
                        + string1
                        + " "
                        + LoggerManager.getPlayerCoords((IsoPlayer)this),
                    "IMPORTANT"
                );
            if (ServerOptions.instance.AnnounceDeath.getValue()) {
                ChatServer.getInstance().sendMessageToServerChat(((IsoPlayer)this.getAttackedBy()).username + " killed " + ((IsoPlayer)this).username + ".");
            }

            ChatServer.getInstance().sendMessageToAdminChat("user " + ((IsoPlayer)this.getAttackedBy()).username + " killed " + ((IsoPlayer)this).username);
        } else {
            if (GameServer.bServer && this instanceof IsoPlayer) {
                LoggerManager.getLogger("user")
                    .write("user " + ((IsoPlayer)this).username + " died at " + LoggerManager.getPlayerCoords((IsoPlayer)this) + " (non pvp)");
            }

            if (ServerOptions.instance.AnnounceDeath.getValue() && this instanceof IsoPlayer && GameServer.bServer) {
                ChatServer.getInstance().sendMessageToServerChat(((IsoPlayer)this).username + " is dead.");
            }
        }

        if (this.isDead()) {
            float float0 = 0.5F;
            if (this.isZombie() && (((IsoZombie)this).bCrawling || this.getCurrentState() == ZombieOnGroundState.instance())) {
                float0 = 0.2F;
            }

            if (GameServer.bServer && bGory) {
                boolean boolean0 = this.isOnFloor() && wielder instanceof IsoPlayer && weapon != null && "BareHands".equals(weapon.getType());
                GameServer.sendBloodSplatter(weapon, this.getX(), this.getY(), this.getZ() + float0, this.getHitDir(), this.isCloseKilled(), boolean0);
            }

            if (weapon != null && SandboxOptions.instance.BloodLevel.getValue() > 1 && bGory) {
                int int0 = weapon.getSplatNumber();
                if (int0 < 1) {
                    int0 = 1;
                }

                if (Core.bLastStand) {
                    int0 *= 3;
                }

                switch (SandboxOptions.instance.BloodLevel.getValue()) {
                    case 2:
                        int0 /= 2;
                    case 3:
                    default:
                        break;
                    case 4:
                        int0 *= 2;
                        break;
                    case 5:
                        int0 *= 5;
                }

                for (int int1 = 0; int1 < int0; int1++) {
                    this.splatBlood(3, 0.3F);
                }
            }

            if (weapon != null && SandboxOptions.instance.BloodLevel.getValue() > 1 && bGory) {
                this.splatBloodFloorBig();
            }

            if (wielder != null && wielder.xp != null) {
                wielder.xp.AddXP(weapon, 3);
            }

            if (SandboxOptions.instance.BloodLevel.getValue() > 1
                && this.isOnFloor()
                && wielder instanceof IsoPlayer
                && weapon == ((IsoPlayer)wielder).bareHands
                && bGory) {
                this.playBloodSplatterSound();

                for (int int2 = -1; int2 <= 1; int2++) {
                    for (int int3 = -1; int3 <= 1; int3++) {
                        if (int2 != 0 || int3 != 0) {
                            new IsoZombieGiblets(
                                IsoZombieGiblets.GibletType.A,
                                this.getCell(),
                                this.getX(),
                                this.getY(),
                                this.getZ() + float0,
                                int2 * Rand.Next(0.25F, 0.5F),
                                int3 * Rand.Next(0.25F, 0.5F)
                            );
                        }
                    }
                }

                new IsoZombieGiblets(
                    IsoZombieGiblets.GibletType.Eye,
                    this.getCell(),
                    this.getX(),
                    this.getY(),
                    this.getZ() + float0,
                    this.getHitDir().x * 0.8F,
                    this.getHitDir().y * 0.8F
                );
            } else if (SandboxOptions.instance.BloodLevel.getValue() > 1 && bGory) {
                this.playBloodSplatterSound();
                new IsoZombieGiblets(
                    IsoZombieGiblets.GibletType.A,
                    this.getCell(),
                    this.getX(),
                    this.getY(),
                    this.getZ() + float0,
                    this.getHitDir().x * 1.5F,
                    this.getHitDir().y * 1.5F
                );
                tempo.x = this.getHitDir().x;
                tempo.y = this.getHitDir().y;
                byte byte0 = 3;
                byte byte1 = 0;
                byte byte2 = 1;
                switch (SandboxOptions.instance.BloodLevel.getValue()) {
                    case 1:
                        byte2 = 0;
                        break;
                    case 2:
                        byte2 = 1;
                        byte0 = 5;
                        byte1 = 2;
                    case 3:
                    default:
                        break;
                    case 4:
                        byte2 = 3;
                        byte0 = 2;
                        break;
                    case 5:
                        byte2 = 10;
                        byte0 = 0;
                }

                for (int int4 = 0; int4 < byte2; int4++) {
                    if (Rand.Next(this.isCloseKilled() ? 8 : byte0) == 0) {
                        new IsoZombieGiblets(
                            IsoZombieGiblets.GibletType.A,
                            this.getCell(),
                            this.getX(),
                            this.getY(),
                            this.getZ() + float0,
                            this.getHitDir().x * 1.5F,
                            this.getHitDir().y * 1.5F
                        );
                    }

                    if (Rand.Next(this.isCloseKilled() ? 8 : byte0) == 0) {
                        new IsoZombieGiblets(
                            IsoZombieGiblets.GibletType.A,
                            this.getCell(),
                            this.getX(),
                            this.getY(),
                            this.getZ() + float0,
                            this.getHitDir().x * 1.5F,
                            this.getHitDir().y * 1.5F
                        );
                    }

                    if (Rand.Next(this.isCloseKilled() ? 8 : byte0) == 0) {
                        new IsoZombieGiblets(
                            IsoZombieGiblets.GibletType.A,
                            this.getCell(),
                            this.getX(),
                            this.getY(),
                            this.getZ() + float0,
                            this.getHitDir().x * 1.8F,
                            this.getHitDir().y * 1.8F
                        );
                    }

                    if (Rand.Next(this.isCloseKilled() ? 8 : byte0) == 0) {
                        new IsoZombieGiblets(
                            IsoZombieGiblets.GibletType.A,
                            this.getCell(),
                            this.getX(),
                            this.getY(),
                            this.getZ() + float0,
                            this.getHitDir().x * 1.9F,
                            this.getHitDir().y * 1.9F
                        );
                    }

                    if (Rand.Next(this.isCloseKilled() ? 4 : byte1) == 0) {
                        new IsoZombieGiblets(
                            IsoZombieGiblets.GibletType.A,
                            this.getCell(),
                            this.getX(),
                            this.getY(),
                            this.getZ() + float0,
                            this.getHitDir().x * 3.5F,
                            this.getHitDir().y * 3.5F
                        );
                    }

                    if (Rand.Next(this.isCloseKilled() ? 4 : byte1) == 0) {
                        new IsoZombieGiblets(
                            IsoZombieGiblets.GibletType.A,
                            this.getCell(),
                            this.getX(),
                            this.getY(),
                            this.getZ() + float0,
                            this.getHitDir().x * 3.8F,
                            this.getHitDir().y * 3.8F
                        );
                    }

                    if (Rand.Next(this.isCloseKilled() ? 4 : byte1) == 0) {
                        new IsoZombieGiblets(
                            IsoZombieGiblets.GibletType.A,
                            this.getCell(),
                            this.getX(),
                            this.getY(),
                            this.getZ() + float0,
                            this.getHitDir().x * 3.9F,
                            this.getHitDir().y * 3.9F
                        );
                    }

                    if (Rand.Next(this.isCloseKilled() ? 4 : byte1) == 0) {
                        new IsoZombieGiblets(
                            IsoZombieGiblets.GibletType.A,
                            this.getCell(),
                            this.getX(),
                            this.getY(),
                            this.getZ() + float0,
                            this.getHitDir().x * 1.5F,
                            this.getHitDir().y * 1.5F
                        );
                    }

                    if (Rand.Next(this.isCloseKilled() ? 4 : byte1) == 0) {
                        new IsoZombieGiblets(
                            IsoZombieGiblets.GibletType.A,
                            this.getCell(),
                            this.getX(),
                            this.getY(),
                            this.getZ() + float0,
                            this.getHitDir().x * 3.8F,
                            this.getHitDir().y * 3.8F
                        );
                    }

                    if (Rand.Next(this.isCloseKilled() ? 4 : byte1) == 0) {
                        new IsoZombieGiblets(
                            IsoZombieGiblets.GibletType.A,
                            this.getCell(),
                            this.getX(),
                            this.getY(),
                            this.getZ() + float0,
                            this.getHitDir().x * 3.9F,
                            this.getHitDir().y * 3.9F
                        );
                    }

                    if (Rand.Next(this.isCloseKilled() ? 9 : 6) == 0) {
                        new IsoZombieGiblets(
                            IsoZombieGiblets.GibletType.Eye,
                            this.getCell(),
                            this.getX(),
                            this.getY(),
                            this.getZ() + float0,
                            this.getHitDir().x * 0.8F,
                            this.getHitDir().y * 0.8F
                        );
                    }
                }
            }
        }

        if (this.isDoDeathSound()) {
            this.playDeadSound();
        }

        this.setDoDeathSound(false);
    }

    private boolean TestIfSeen(int int0) {
        IsoPlayer player = IsoPlayer.players[int0];
        if (player != null && this != player && !GameServer.bServer) {
            float float0 = this.DistToProper(player);
            if (float0 > GameTime.getInstance().getViewDist()) {
                return false;
            } else {
                boolean boolean0 = this.current.isCanSee(int0);
                if (!boolean0 && this.current.isCouldSee(int0)) {
                    boolean0 = float0 < player.getSeeNearbyCharacterDistance();
                }

                if (!boolean0) {
                    return false;
                } else {
                    ColorInfo colorInfo = this.getCurrentSquare().lighting[int0].lightInfo();
                    float float1 = (colorInfo.r + colorInfo.g + colorInfo.b) / 3.0F;
                    if (float1 > 0.6F) {
                        float1 = 1.0F;
                    }

                    float float2 = 1.0F - float0 / GameTime.getInstance().getViewDist();
                    if (float1 == 1.0F && float2 > 0.3F) {
                        float2 = 1.0F;
                    }

                    float float3 = player.getDotWithForwardDirection(this.getX(), this.getY());
                    if (float3 < 0.5F) {
                        float3 = 0.5F;
                    }

                    float1 *= float3;
                    if (float1 < 0.0F) {
                        float1 = 0.0F;
                    }

                    if (float0 <= 1.0F) {
                        float2 = 1.0F;
                        float1 *= 2.0F;
                    }

                    float1 *= float2;
                    float1 *= 100.0F;
                    return float1 > 0.025F;
                }
            }
        } else {
            return false;
        }
    }

    private void DoLand() {
        if (!(this.fallTime < 20.0F) && !this.isClimbing()) {
            if (this instanceof IsoPlayer) {
                if (GameServer.bServer) {
                    return;
                }

                if (GameClient.bClient && ((IsoPlayer)this).bRemote) {
                    return;
                }

                if (((IsoPlayer)this).isGhostMode()) {
                    return;
                }
            }

            if (this.isZombie()) {
                if (this.fallTime > 50.0F) {
                    this.hitDir.x = this.hitDir.y = 0.0F;
                    if (!((IsoZombie)this).bCrawling && (Rand.Next(100) < 80 || this.fallTime > 80.0F)) {
                        this.setVariable("bHardFall", true);
                    }

                    this.playHurtSound();
                    float float0 = Rand.Next(150) / 1000.0F;
                    this.Health = this.Health - float0 * this.fallTime / 50.0F;
                    this.setAttackedBy(null);
                }
            } else {
                boolean boolean0 = Rand.Next(80) == 0;
                float float1 = this.fallTime;
                float1 *= Math.min(1.8F, this.getInventory().getCapacityWeight() / this.getInventory().getMaxWeight());
                if (this.getCurrentSquare().getFloor() != null
                    && this.getCurrentSquare().getFloor().getSprite().getName() != null
                    && this.getCurrentSquare().getFloor().getSprite().getName().startsWith("blends_natural")) {
                    float1 *= 0.8F;
                    if (!boolean0) {
                        boolean0 = Rand.Next(65) == 0;
                    }
                }

                if (!boolean0) {
                    if (this.Traits.Obese.isSet() || this.Traits.Emaciated.isSet()) {
                        float1 *= 1.4F;
                    }

                    if (this.Traits.Overweight.isSet() || this.Traits.VeryUnderweight.isSet()) {
                        float1 *= 1.2F;
                    }

                    float1 *= Math.max(0.1F, 1.0F - this.getPerkLevel(PerkFactory.Perks.Fitness) * 0.1F);
                    if (this.fallTime > 135.0F) {
                        float1 = 1000.0F;
                    }

                    this.BodyDamage.ReduceGeneralHealth(float1);
                    LuaEventManager.triggerEvent("OnPlayerGetDamage", this, "FALLDOWN", float1);
                    if (this.fallTime > 70.0F) {
                        int int0 = 100 - (int)(this.fallTime * 0.6);
                        if (this.getInventory().getMaxWeight() - this.getInventory().getCapacityWeight() < 2.0F) {
                            int0 = (int)(int0 - this.getInventory().getCapacityWeight() / this.getInventory().getMaxWeight() * 100.0F / 5.0F);
                        }

                        if (this.Traits.Obese.isSet() || this.Traits.Emaciated.isSet()) {
                            int0 -= 20;
                        }

                        if (this.Traits.Overweight.isSet() || this.Traits.VeryUnderweight.isSet()) {
                            int0 -= 10;
                        }

                        if (this.getPerkLevel(PerkFactory.Perks.Fitness) > 4) {
                            int0 += (this.getPerkLevel(PerkFactory.Perks.Fitness) - 4) * 3;
                        }

                        if (Rand.Next(100) >= int0) {
                            if (!SandboxOptions.instance.BoneFracture.getValue()) {
                                return;
                            }

                            float float2 = Rand.Next(50, 80);
                            if (this.Traits.FastHealer.isSet()) {
                                float2 = Rand.Next(30, 50);
                            } else if (this.Traits.SlowHealer.isSet()) {
                                float2 = Rand.Next(80, 150);
                            }

                            switch (SandboxOptions.instance.InjurySeverity.getValue()) {
                                case 1:
                                    float2 *= 0.5F;
                                    break;
                                case 3:
                                    float2 *= 1.5F;
                            }

                            this.getBodyDamage()
                                .getBodyPart(
                                    BodyPartType.FromIndex(
                                        Rand.Next(BodyPartType.ToIndex(BodyPartType.UpperLeg_L), BodyPartType.ToIndex(BodyPartType.Foot_R) + 1)
                                    )
                                )
                                .setFractureTime(float2);
                        } else if (Rand.Next(100) >= int0 - 10) {
                            this.getBodyDamage()
                                .getBodyPart(
                                    BodyPartType.FromIndex(
                                        Rand.Next(BodyPartType.ToIndex(BodyPartType.UpperLeg_L), BodyPartType.ToIndex(BodyPartType.Foot_R) + 1)
                                    )
                                )
                                .generateDeepWound();
                        }
                    }
                }
            }
        }
    }

    /**
     * @return the FollowingTarget
     */
    public IsoGameCharacter getFollowingTarget() {
        return this.FollowingTarget;
    }

    /**
     * 
     * @param _FollowingTarget the FollowingTarget to set
     */
    public void setFollowingTarget(IsoGameCharacter _FollowingTarget) {
        this.FollowingTarget = _FollowingTarget;
    }

    /**
     * @return the LocalList
     */
    public ArrayList<IsoMovingObject> getLocalList() {
        return this.LocalList;
    }

    /**
     * @return the LocalNeutralList
     */
    public ArrayList<IsoMovingObject> getLocalNeutralList() {
        return this.LocalNeutralList;
    }

    /**
     * @return the LocalGroupList
     */
    public ArrayList<IsoMovingObject> getLocalGroupList() {
        return this.LocalGroupList;
    }

    /**
     * @return the LocalRelevantEnemyList
     */
    public ArrayList<IsoMovingObject> getLocalRelevantEnemyList() {
        return this.LocalRelevantEnemyList;
    }

    /**
     * @return the dangerLevels
     */
    public float getDangerLevels() {
        return this.dangerLevels;
    }

    /**
     * 
     * @param _dangerLevels the dangerLevels to set
     */
    public void setDangerLevels(float _dangerLevels) {
        this.dangerLevels = _dangerLevels;
    }

    /**
     * @return the PerkList
     */
    public ArrayList<IsoGameCharacter.PerkInfo> getPerkList() {
        return this.PerkList;
    }

    /**
     * @return the leaveBodyTimedown
     */
    public float getLeaveBodyTimedown() {
        return this.leaveBodyTimedown;
    }

    /**
     * 
     * @param _leaveBodyTimedown the leaveBodyTimedown to set
     */
    public void setLeaveBodyTimedown(float _leaveBodyTimedown) {
        this.leaveBodyTimedown = _leaveBodyTimedown;
    }

    /**
     * @return the AllowConversation
     */
    public boolean isAllowConversation() {
        return this.AllowConversation;
    }

    /**
     * 
     * @param _AllowConversation the AllowConversation to set
     */
    public void setAllowConversation(boolean _AllowConversation) {
        this.AllowConversation = _AllowConversation;
    }

    /**
     * @return the ReanimateTimer
     */
    public float getReanimateTimer() {
        return this.ReanimateTimer;
    }

    /**
     * 
     * @param _ReanimateTimer the ReanimateTimer to set
     */
    public void setReanimateTimer(float _ReanimateTimer) {
        this.ReanimateTimer = _ReanimateTimer;
    }

    /**
     * @return the ReanimAnimFrame
     */
    public int getReanimAnimFrame() {
        return this.ReanimAnimFrame;
    }

    /**
     * 
     * @param _ReanimAnimFrame the ReanimAnimFrame to set
     */
    public void setReanimAnimFrame(int _ReanimAnimFrame) {
        this.ReanimAnimFrame = _ReanimAnimFrame;
    }

    /**
     * @return the ReanimAnimDelay
     */
    public int getReanimAnimDelay() {
        return this.ReanimAnimDelay;
    }

    /**
     * 
     * @param _ReanimAnimDelay the ReanimAnimDelay to set
     */
    public void setReanimAnimDelay(int _ReanimAnimDelay) {
        this.ReanimAnimDelay = _ReanimAnimDelay;
    }

    /**
     * @return the Reanim
     */
    public boolean isReanim() {
        return this.Reanim;
    }

    /**
     * 
     * @param _Reanim the Reanim to set
     */
    public void setReanim(boolean _Reanim) {
        this.Reanim = _Reanim;
    }

    /**
     * @return the VisibleToNPCs
     */
    public boolean isVisibleToNPCs() {
        return this.VisibleToNPCs;
    }

    /**
     * 
     * @param _VisibleToNPCs the VisibleToNPCs to set
     */
    public void setVisibleToNPCs(boolean _VisibleToNPCs) {
        this.VisibleToNPCs = _VisibleToNPCs;
    }

    /**
     * @return the DieCount
     */
    public int getDieCount() {
        return this.DieCount;
    }

    /**
     * 
     * @param _DieCount the DieCount to set
     */
    public void setDieCount(int _DieCount) {
        this.DieCount = _DieCount;
    }

    /**
     * @return the llx
     */
    public float getLlx() {
        return this.llx;
    }

    /**
     * 
     * @param _llx the llx to set
     */
    public void setLlx(float _llx) {
        this.llx = _llx;
    }

    /**
     * @return the lly
     */
    public float getLly() {
        return this.lly;
    }

    /**
     * 
     * @param _lly the lly to set
     */
    public void setLly(float _lly) {
        this.lly = _lly;
    }

    /**
     * @return the llz
     */
    public float getLlz() {
        return this.llz;
    }

    /**
     * 
     * @param _llz the llz to set
     */
    public void setLlz(float _llz) {
        this.llz = _llz;
    }

    /**
     * @return the RemoteID
     */
    public int getRemoteID() {
        return this.RemoteID;
    }

    /**
     * 
     * @param _RemoteID the RemoteID to set
     */
    public void setRemoteID(int _RemoteID) {
        this.RemoteID = _RemoteID;
    }

    /**
     * @return the NumSurvivorsInVicinity
     */
    public int getNumSurvivorsInVicinity() {
        return this.NumSurvivorsInVicinity;
    }

    /**
     * 
     * @param _NumSurvivorsInVicinity the NumSurvivorsInVicinity to set
     */
    public void setNumSurvivorsInVicinity(int _NumSurvivorsInVicinity) {
        this.NumSurvivorsInVicinity = _NumSurvivorsInVicinity;
    }

    /**
     * @return the LevelUpMultiplier
     */
    public float getLevelUpMultiplier() {
        return this.LevelUpMultiplier;
    }

    /**
     * 
     * @param _LevelUpMultiplier the LevelUpMultiplier to set
     */
    public void setLevelUpMultiplier(float _LevelUpMultiplier) {
        this.LevelUpMultiplier = _LevelUpMultiplier;
    }

    /**
     * @return the xp
     */
    @Override
    public IsoGameCharacter.XP getXp() {
        return this.xp;
    }

    /**
     * 
     * @param _xp the xp to set
     */
    @Deprecated
    public void setXp(IsoGameCharacter.XP _xp) {
        this.xp = _xp;
    }

    /**
     * @return the LastLocalEnemies
     */
    public int getLastLocalEnemies() {
        return this.LastLocalEnemies;
    }

    /**
     * 
     * @param _LastLocalEnemies the LastLocalEnemies to set
     */
    public void setLastLocalEnemies(int _LastLocalEnemies) {
        this.LastLocalEnemies = _LastLocalEnemies;
    }

    /**
     * @return the VeryCloseEnemyList
     */
    public ArrayList<IsoMovingObject> getVeryCloseEnemyList() {
        return this.VeryCloseEnemyList;
    }

    /**
     * @return the LastKnownLocation
     */
    public HashMap<String, IsoGameCharacter.Location> getLastKnownLocation() {
        return this.LastKnownLocation;
    }

    /**
     * @return the AttackedBy
     */
    public IsoGameCharacter getAttackedBy() {
        return this.AttackedBy;
    }

    /**
     * 
     * @param _AttackedBy the AttackedBy to set
     */
    public void setAttackedBy(IsoGameCharacter _AttackedBy) {
        this.AttackedBy = _AttackedBy;
    }

    /**
     * @return the IgnoreStaggerBack
     */
    public boolean isIgnoreStaggerBack() {
        return this.IgnoreStaggerBack;
    }

    /**
     * 
     * @param _IgnoreStaggerBack the IgnoreStaggerBack to set
     */
    public void setIgnoreStaggerBack(boolean _IgnoreStaggerBack) {
        this.IgnoreStaggerBack = _IgnoreStaggerBack;
    }

    /**
     * @return the AttackWasSuperAttack
     */
    public boolean isAttackWasSuperAttack() {
        return this.AttackWasSuperAttack;
    }

    /**
     * 
     * @param _AttackWasSuperAttack the AttackWasSuperAttack to set
     */
    public void setAttackWasSuperAttack(boolean _AttackWasSuperAttack) {
        this.AttackWasSuperAttack = _AttackWasSuperAttack;
    }

    /**
     * @return the TimeThumping
     */
    public int getTimeThumping() {
        return this.TimeThumping;
    }

    /**
     * 
     * @param _TimeThumping the TimeThumping to set
     */
    public void setTimeThumping(int _TimeThumping) {
        this.TimeThumping = _TimeThumping;
    }

    /**
     * @return the PatienceMax
     */
    public int getPatienceMax() {
        return this.PatienceMax;
    }

    /**
     * 
     * @param _PatienceMax the PatienceMax to set
     */
    public void setPatienceMax(int _PatienceMax) {
        this.PatienceMax = _PatienceMax;
    }

    /**
     * @return the PatienceMin
     */
    public int getPatienceMin() {
        return this.PatienceMin;
    }

    /**
     * 
     * @param _PatienceMin the PatienceMin to set
     */
    public void setPatienceMin(int _PatienceMin) {
        this.PatienceMin = _PatienceMin;
    }

    /**
     * @return the Patience
     */
    public int getPatience() {
        return this.Patience;
    }

    /**
     * 
     * @param _Patience the Patience to set
     */
    public void setPatience(int _Patience) {
        this.Patience = _Patience;
    }

    /**
     * @return the CharacterActions
     */
    @Override
    public Stack<BaseAction> getCharacterActions() {
        return this.CharacterActions;
    }

    public boolean hasTimedActions() {
        return !this.CharacterActions.isEmpty() || this.getVariableBoolean("IsPerformingAnAction");
    }

    /**
     * @return the character's forward direction vector
     */
    public Vector2 getForwardDirection() {
        return this.m_forwardDirection;
    }

    /**
     * 
     * @param dir The character's new forward direction.
     */
    public void setForwardDirection(Vector2 dir) {
        if (dir != null) {
            this.setForwardDirection(dir.x, dir.y);
        }
    }

    public void setForwardDirection(float x, float y) {
        this.m_forwardDirection.x = x;
        this.m_forwardDirection.y = y;
    }

    public void zeroForwardDirectionX() {
        this.setForwardDirection(0.0F, 1.0F);
    }

    public void zeroForwardDirectionY() {
        this.setForwardDirection(1.0F, 0.0F);
    }

    /**
     * The forward direction angle, in degrees.
     */
    public float getDirectionAngle() {
        return (180.0F / (float)Math.PI) * this.getForwardDirection().getDirection();
    }

    public void setDirectionAngle(float angleDegrees) {
        float float0 = (float) (Math.PI / 180.0) * angleDegrees;
        Vector2 vector = this.getForwardDirection();
        vector.setDirection(float0);
    }

    public float getAnimAngle() {
        return this.m_animPlayer != null && this.m_animPlayer.isReady() && !this.m_animPlayer.isBoneTransformsNeedFirstFrame()
            ? (180.0F / (float)Math.PI) * this.m_animPlayer.getAngle()
            : this.getDirectionAngle();
    }

    public float getAnimAngleRadians() {
        return this.m_animPlayer != null && this.m_animPlayer.isReady() && !this.m_animPlayer.isBoneTransformsNeedFirstFrame()
            ? this.m_animPlayer.getAngle()
            : this.m_forwardDirection.getDirection();
    }

    public Vector2 getAnimVector(Vector2 out) {
        return out.setLengthAndDirection(this.getAnimAngleRadians(), 1.0F);
    }

    public float getLookAngleRadians() {
        return this.m_animPlayer != null && this.m_animPlayer.isReady()
            ? this.m_animPlayer.getAngle() + this.m_animPlayer.getTwistAngle()
            : this.getForwardDirection().getDirection();
    }

    public Vector2 getLookVector(Vector2 vector2) {
        return vector2.setLengthAndDirection(this.getLookAngleRadians(), 1.0F);
    }

    public float getDotWithForwardDirection(Vector3 bonePos) {
        return this.getDotWithForwardDirection(bonePos.x, bonePos.y);
    }

    public float getDotWithForwardDirection(float targetX, float targetY) {
        Vector2 vector0 = IsoGameCharacter.L_getDotWithForwardDirection.v1.set(targetX - this.getX(), targetY - this.getY());
        vector0.normalize();
        Vector2 vector1 = this.getLookVector(IsoGameCharacter.L_getDotWithForwardDirection.v2);
        vector1.normalize();
        return vector0.dot(vector1);
    }

    /**
     * @return the Asleep
     */
    @Override
    public boolean isAsleep() {
        return this.Asleep;
    }

    /**
     * 
     * @param _Asleep the Asleep to set
     */
    @Override
    public void setAsleep(boolean _Asleep) {
        this.Asleep = _Asleep;
    }

    /**
     * @return the ZombieKills
     */
    @Override
    public int getZombieKills() {
        return this.ZombieKills;
    }

    /**
     * 
     * @param _ZombieKills the ZombieKills to set
     */
    public void setZombieKills(int _ZombieKills) {
        this.ZombieKills = _ZombieKills;
    }

    /**
     * @return the LastZombieKills
     */
    public int getLastZombieKills() {
        return this.LastZombieKills;
    }

    /**
     * 
     * @param _LastZombieKills the LastZombieKills to set
     */
    public void setLastZombieKills(int _LastZombieKills) {
        this.LastZombieKills = _LastZombieKills;
    }

    /**
     * @return the superAttack
     */
    public boolean isSuperAttack() {
        return this.superAttack;
    }

    /**
     * 
     * @param _superAttack the superAttack to set
     */
    public void setSuperAttack(boolean _superAttack) {
        this.superAttack = _superAttack;
    }

    /**
     * @return the ForceWakeUpTime
     */
    public float getForceWakeUpTime() {
        return this.ForceWakeUpTime;
    }

    /**
     * 
     * @param _ForceWakeUpTime the ForceWakeUpTime to set
     */
    @Override
    public void setForceWakeUpTime(float _ForceWakeUpTime) {
        this.ForceWakeUpTime = _ForceWakeUpTime;
    }

    public void forceAwake() {
        if (this.isAsleep()) {
            this.ForceWakeUp = true;
        }
    }

    /**
     * @return the BodyDamage
     */
    @Override
    public BodyDamage getBodyDamage() {
        return this.BodyDamage;
    }

    @Override
    public BodyDamage getBodyDamageRemote() {
        if (this.BodyDamageRemote == null) {
            this.BodyDamageRemote = new BodyDamage(null);
        }

        return this.BodyDamageRemote;
    }

    public void resetBodyDamageRemote() {
        this.BodyDamageRemote = null;
    }

    /**
     * @return the defaultState
     */
    public State getDefaultState() {
        return this.defaultState;
    }

    /**
     * 
     * @param _defaultState the defaultState to set
     */
    public void setDefaultState(State _defaultState) {
        this.defaultState = _defaultState;
    }

    /**
     * @return the descriptor
     */
    @Override
    public SurvivorDesc getDescriptor() {
        return this.descriptor;
    }

    /**
     * 
     * @param _descriptor the descriptor to set
     */
    @Override
    public void setDescriptor(SurvivorDesc _descriptor) {
        this.descriptor = _descriptor;
    }

    @Override
    public String getFullName() {
        return this.descriptor != null ? this.descriptor.forename + " " + this.descriptor.surname : "Bob Smith";
    }

    @Override
    public BaseVisual getVisual() {
        throw new RuntimeException("subclasses must implement this");
    }

    public ItemVisuals getItemVisuals() {
        throw new RuntimeException("subclasses must implement this");
    }

    public void getItemVisuals(ItemVisuals itemVisuals) {
        this.getWornItems().getItemVisuals(itemVisuals);
    }

    public boolean isUsingWornItems() {
        return this.wornItems != null;
    }

    /**
     * @return the FamiliarBuildings
     */
    public Stack<IsoBuilding> getFamiliarBuildings() {
        return this.FamiliarBuildings;
    }

    /**
     * @return the finder
     */
    public AStarPathFinderResult getFinder() {
        return this.finder;
    }

    /**
     * @return the FireKillRate
     */
    public float getFireKillRate() {
        return this.FireKillRate;
    }

    /**
     * 
     * @param _FireKillRate the FireKillRate to set
     */
    public void setFireKillRate(float _FireKillRate) {
        this.FireKillRate = _FireKillRate;
    }

    /**
     * @return the FireSpreadProbability
     */
    public int getFireSpreadProbability() {
        return this.FireSpreadProbability;
    }

    /**
     * 
     * @param _FireSpreadProbability the FireSpreadProbability to set
     */
    public void setFireSpreadProbability(int _FireSpreadProbability) {
        this.FireSpreadProbability = _FireSpreadProbability;
    }

    /**
     * @return the Health
     */
    @Override
    public float getHealth() {
        return this.Health;
    }

    /**
     * 
     * @param _Health the Health to set
     */
    @Override
    public void setHealth(float _Health) {
        this.Health = _Health;
    }

    @Override
    public boolean isOnDeathDone() {
        return this.bDead;
    }

    @Override
    public void setOnDeathDone(boolean done) {
        this.bDead = done;
    }

    @Override
    public boolean isOnKillDone() {
        return this.bKill;
    }

    @Override
    public void setOnKillDone(boolean done) {
        this.bKill = done;
    }

    @Override
    public boolean isDeathDragDown() {
        return this.bDeathDragDown;
    }

    @Override
    public void setDeathDragDown(boolean dragDown) {
        this.bDeathDragDown = dragDown;
    }

    @Override
    public boolean isPlayingDeathSound() {
        return this.bPlayingDeathSound;
    }

    @Override
    public void setPlayingDeathSound(boolean playing) {
        this.bPlayingDeathSound = playing;
    }

    /**
     * @return the hurtSound
     */
    public String getHurtSound() {
        return this.hurtSound;
    }

    /**
     * 
     * @param _hurtSound the hurtSound to set
     */
    public void setHurtSound(String _hurtSound) {
        this.hurtSound = _hurtSound;
    }

    /**
     * @return the IgnoreMovementForDirection
     */
    @Deprecated
    public boolean isIgnoreMovementForDirection() {
        return false;
    }

    /**
     * @return the inventory
     */
    @Override
    public ItemContainer getInventory() {
        return this.inventory;
    }

    /**
     * 
     * @param _inventory the inventory to set
     */
    public void setInventory(ItemContainer _inventory) {
        _inventory.parent = this;
        this.inventory = _inventory;
        this.inventory.setExplored(true);
    }

    public boolean isPrimaryEquipped(String item) {
        return this.leftHandItem == null ? false : this.leftHandItem.getFullType().equals(item) || this.leftHandItem.getType().equals(item);
    }

    /**
     * @return the leftHandItem
     */
    @Override
    public InventoryItem getPrimaryHandItem() {
        return this.leftHandItem;
    }

    /**
     * 
     * @param _leftHandItem the leftHandItem to set
     */
    @Override
    public void setPrimaryHandItem(InventoryItem _leftHandItem) {
        this.setEquipParent(this.leftHandItem, _leftHandItem);
        this.leftHandItem = _leftHandItem;
        if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
            GameClient.instance.equip((IsoPlayer)this, 0);
        }

        LuaEventManager.triggerEvent("OnEquipPrimary", this, _leftHandItem);
        this.resetEquippedHandsModels();
        this.setVariable("Weapon", WeaponType.getWeaponType(this).type);
        if (_leftHandItem != null && _leftHandItem instanceof HandWeapon && !StringUtils.isNullOrEmpty(((HandWeapon)_leftHandItem).getFireMode())) {
            this.setVariable("FireMode", ((HandWeapon)_leftHandItem).getFireMode());
        } else {
            this.clearVariable("FireMode");
        }
    }

    protected void setEquipParent(InventoryItem item0, InventoryItem item1) {
        if (item0 != null) {
            item0.setEquipParent(null);
        }

        if (item1 != null) {
            item1.setEquipParent(this);
        }
    }

    public void initWornItems(String bodyLocationGroupName) {
        BodyLocationGroup bodyLocationGroup = BodyLocations.getGroup(bodyLocationGroupName);
        this.wornItems = new WornItems(bodyLocationGroup);
    }

    @Override
    public WornItems getWornItems() {
        return this.wornItems;
    }

    @Override
    public void setWornItems(WornItems other) {
        this.wornItems = new WornItems(other);
    }

    @Override
    public InventoryItem getWornItem(String location) {
        return this.wornItems.getItem(location);
    }

    @Override
    public void setWornItem(String location, InventoryItem item) {
        this.setWornItem(location, item, true);
    }

    public void setWornItem(String location, InventoryItem item, boolean forceDropTooHeavy) {
        InventoryItem _item = this.wornItems.getItem(location);
        if (item != _item) {
            IsoCell cell = IsoWorld.instance.CurrentCell;
            if (_item != null && cell != null) {
                cell.addToProcessItemsRemove(_item);
            }

            this.wornItems.setItem(location, item);
            if (item != null && cell != null) {
                if (item.getContainer() != null) {
                    item.getContainer().parent = this;
                }

                cell.addToProcessItems(item);
            }

            if (forceDropTooHeavy && _item != null && this instanceof IsoPlayer && !this.getInventory().hasRoomFor(this, _item)) {
                IsoGridSquare square = this.getCurrentSquare();
                square = this.getSolidFloorAt(square.x, square.y, square.z);
                if (square != null) {
                    float float0 = Rand.Next(0.1F, 0.9F);
                    float float1 = Rand.Next(0.1F, 0.9F);
                    float float2 = square.getApparentZ(float0, float1) - square.getZ();
                    square.AddWorldInventoryItem(_item, float0, float1, float2);
                    this.getInventory().Remove(_item);
                }
            }

            this.resetModelNextFrame();
            if (this.clothingWetness != null) {
                this.clothingWetness.changed = true;
            }

            if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
                GameClient.instance.sendClothing((IsoPlayer)this, location, item);
            }

            this.onWornItemsChanged();
        }
    }

    @Override
    public void removeWornItem(InventoryItem item) {
        this.removeWornItem(item, true);
    }

    public void removeWornItem(InventoryItem item, boolean forceDropTooHeavy) {
        String string = this.wornItems.getLocation(item);
        if (string != null) {
            this.setWornItem(string, null, forceDropTooHeavy);
        }
    }

    @Override
    public void clearWornItems() {
        if (this.wornItems != null) {
            this.wornItems.clear();
            if (this.clothingWetness != null) {
                this.clothingWetness.changed = true;
            }

            this.onWornItemsChanged();
        }
    }

    @Override
    public BodyLocationGroup getBodyLocationGroup() {
        return this.wornItems == null ? null : this.wornItems.getBodyLocationGroup();
    }

    public void onWornItemsChanged() {
    }

    public void initAttachedItems(String groupName) {
        AttachedLocationGroup attachedLocationGroup = AttachedLocations.getGroup(groupName);
        this.attachedItems = new AttachedItems(attachedLocationGroup);
    }

    @Override
    public AttachedItems getAttachedItems() {
        return this.attachedItems;
    }

    @Override
    public void setAttachedItems(AttachedItems other) {
        this.attachedItems = new AttachedItems(other);
    }

    @Override
    public InventoryItem getAttachedItem(String location) {
        return this.attachedItems.getItem(location);
    }

    @Override
    public void setAttachedItem(String location, InventoryItem item) {
        InventoryItem _item = this.attachedItems.getItem(location);
        IsoCell cell = IsoWorld.instance.CurrentCell;
        if (_item != null && cell != null) {
            cell.addToProcessItemsRemove(_item);
        }

        this.attachedItems.setItem(location, item);
        if (item != null && cell != null) {
            InventoryContainer inventoryContainer = Type.tryCastTo(item, InventoryContainer.class);
            if (inventoryContainer != null && inventoryContainer.getInventory() != null) {
                inventoryContainer.getInventory().parent = this;
            }

            cell.addToProcessItems(item);
        }

        this.resetEquippedHandsModels();
        IsoPlayer player = Type.tryCastTo(this, IsoPlayer.class);
        if (GameClient.bClient && player != null && player.isLocalPlayer()) {
            GameClient.instance.sendAttachedItem(player, location, item);
        }

        if (!GameServer.bServer && player != null && player.isLocalPlayer()) {
            LuaEventManager.triggerEvent("OnClothingUpdated", this);
        }
    }

    @Override
    public void removeAttachedItem(InventoryItem item) {
        String string = this.attachedItems.getLocation(item);
        if (string != null) {
            this.setAttachedItem(string, null);
        }
    }

    @Override
    public void clearAttachedItems() {
        if (this.attachedItems != null) {
            this.attachedItems.clear();
        }
    }

    @Override
    public AttachedLocationGroup getAttachedLocationGroup() {
        return this.attachedItems == null ? null : this.attachedItems.getGroup();
    }

    public ClothingWetness getClothingWetness() {
        return this.clothingWetness;
    }

    /**
     * @return the ClothingItem_Head
     */
    public InventoryItem getClothingItem_Head() {
        return this.getWornItem("Hat");
    }

    /**
     * 
     * @param item the ClothingItem_Head to set
     */
    @Override
    public void setClothingItem_Head(InventoryItem item) {
        this.setWornItem("Hat", item);
    }

    /**
     * @return the ClothingItem_Torso
     */
    public InventoryItem getClothingItem_Torso() {
        return this.getWornItem("Tshirt");
    }

    @Override
    public void setClothingItem_Torso(InventoryItem item) {
        this.setWornItem("Tshirt", item);
    }

    public InventoryItem getClothingItem_Back() {
        return this.getWornItem("Back");
    }

    @Override
    public void setClothingItem_Back(InventoryItem item) {
        this.setWornItem("Back", item);
    }

    /**
     * @return the ClothingItem_Hands
     */
    public InventoryItem getClothingItem_Hands() {
        return this.getWornItem("Hands");
    }

    /**
     * 
     * @param item the ClothingItem_Hands to set
     */
    @Override
    public void setClothingItem_Hands(InventoryItem item) {
        this.setWornItem("Hands", item);
    }

    /**
     * @return the ClothingItem_Legs
     */
    public InventoryItem getClothingItem_Legs() {
        return this.getWornItem("Pants");
    }

    /**
     * 
     * @param item the ClothingItem_Legs to set
     */
    @Override
    public void setClothingItem_Legs(InventoryItem item) {
        this.setWornItem("Pants", item);
    }

    /**
     * @return the ClothingItem_Feet
     */
    public InventoryItem getClothingItem_Feet() {
        return this.getWornItem("Shoes");
    }

    /**
     * 
     * @param item the ClothingItem_Feet to set
     */
    @Override
    public void setClothingItem_Feet(InventoryItem item) {
        this.setWornItem("Shoes", item);
    }

    /**
     * @return the NextWander
     */
    public int getNextWander() {
        return this.NextWander;
    }

    /**
     * 
     * @param _NextWander the NextWander to set
     */
    public void setNextWander(int _NextWander) {
        this.NextWander = _NextWander;
    }

    /**
     * @return the OnFire
     */
    @Override
    public boolean isOnFire() {
        return this.OnFire;
    }

    /**
     * 
     * @param _OnFire the OnFire to set
     */
    public void setOnFire(boolean _OnFire) {
        this.OnFire = _OnFire;
        if (GameServer.bServer) {
            if (_OnFire) {
                IsoFireManager.addCharacterOnFire(this);
            } else {
                IsoFireManager.deleteCharacterOnFire(this);
            }
        }
    }

    @Override
    public void removeFromWorld() {
        if (GameServer.bServer) {
            IsoFireManager.deleteCharacterOnFire(this);
        }

        super.removeFromWorld();
    }

    /**
     * @return the pathIndex
     */
    public int getPathIndex() {
        return this.pathIndex;
    }

    /**
     * 
     * @param _pathIndex the pathIndex to set
     */
    public void setPathIndex(int _pathIndex) {
        this.pathIndex = _pathIndex;
    }

    /**
     * @return the PathTargetX
     */
    public int getPathTargetX() {
        return (int)this.getPathFindBehavior2().getTargetX();
    }

    /**
     * @return the PathTargetY
     */
    public int getPathTargetY() {
        return (int)this.getPathFindBehavior2().getTargetY();
    }

    /**
     * @return the PathTargetZ
     */
    public int getPathTargetZ() {
        return (int)this.getPathFindBehavior2().getTargetZ();
    }

    /**
     * @return the rightHandItem
     */
    @Override
    public InventoryItem getSecondaryHandItem() {
        return this.rightHandItem;
    }

    /**
     * 
     * @param _rightHandItem the rightHandItem to set
     */
    @Override
    public void setSecondaryHandItem(InventoryItem _rightHandItem) {
        this.setEquipParent(this.rightHandItem, _rightHandItem);
        this.rightHandItem = _rightHandItem;
        if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
            GameClient.instance.equip((IsoPlayer)this, 1);
        }

        LuaEventManager.triggerEvent("OnEquipSecondary", this, _rightHandItem);
        this.resetEquippedHandsModels();
        this.setVariable("Weapon", WeaponType.getWeaponType(this).type);
    }

    @Override
    public boolean isHandItem(InventoryItem item) {
        return this.isPrimaryHandItem(item) || this.isSecondaryHandItem(item);
    }

    @Override
    public boolean isPrimaryHandItem(InventoryItem item) {
        return item != null && this.getPrimaryHandItem() == item;
    }

    @Override
    public boolean isSecondaryHandItem(InventoryItem item) {
        return item != null && this.getSecondaryHandItem() == item;
    }

    @Override
    public boolean isItemInBothHands(InventoryItem item) {
        return this.isPrimaryHandItem(item) && this.isSecondaryHandItem(item);
    }

    @Override
    public boolean removeFromHands(InventoryItem item) {
        boolean boolean0 = true;
        if (this.isPrimaryHandItem(item)) {
            this.setPrimaryHandItem(null);
        }

        if (this.isSecondaryHandItem(item)) {
            this.setSecondaryHandItem(null);
        }

        return boolean0;
    }

    /**
     * @return the SpeakColour
     */
    public Color getSpeakColour() {
        return this.SpeakColour;
    }

    /**
     * 
     * @param _SpeakColour the SpeakColour to set
     */
    public void setSpeakColour(Color _SpeakColour) {
        this.SpeakColour = _SpeakColour;
    }

    @Override
    public void setSpeakColourInfo(ColorInfo info) {
        this.SpeakColour = new Color(info.r, info.g, info.b, 1.0F);
    }

    /**
     * @return the slowFactor
     */
    public float getSlowFactor() {
        return this.slowFactor;
    }

    /**
     * 
     * @param _slowFactor the slowFactor to set
     */
    public void setSlowFactor(float _slowFactor) {
        this.slowFactor = _slowFactor;
    }

    /**
     * @return the slowTimer
     */
    public float getSlowTimer() {
        return this.slowTimer;
    }

    /**
     * 
     * @param _slowTimer the slowTimer to set
     */
    public void setSlowTimer(float _slowTimer) {
        this.slowTimer = _slowTimer;
    }

    /**
     * @return the bUseParts
     */
    public boolean isbUseParts() {
        return this.bUseParts;
    }

    /**
     * 
     * @param _bUseParts the bUseParts to set
     */
    public void setbUseParts(boolean _bUseParts) {
        this.bUseParts = _bUseParts;
    }

    /**
     * @return the Speaking
     */
    @Override
    public boolean isSpeaking() {
        return this.IsSpeaking();
    }

    /**
     * 
     * @param _Speaking the Speaking to set
     */
    public void setSpeaking(boolean _Speaking) {
        this.Speaking = _Speaking;
    }

    /**
     * @return the SpeakTime
     */
    public float getSpeakTime() {
        return this.SpeakTime;
    }

    /**
     * 
     * @param _SpeakTime the SpeakTime to set
     */
    public void setSpeakTime(int _SpeakTime) {
        this.SpeakTime = _SpeakTime;
    }

    /**
     * @return the speedMod
     */
    public float getSpeedMod() {
        return this.speedMod;
    }

    /**
     * 
     * @param _speedMod the speedMod to set
     */
    public void setSpeedMod(float _speedMod) {
        this.speedMod = _speedMod;
    }

    /**
     * @return the staggerTimeMod
     */
    public float getStaggerTimeMod() {
        return this.staggerTimeMod;
    }

    /**
     * 
     * @param _staggerTimeMod the staggerTimeMod to set
     */
    public void setStaggerTimeMod(float _staggerTimeMod) {
        this.staggerTimeMod = _staggerTimeMod;
    }

    /**
     * @return the stateMachine
     */
    public StateMachine getStateMachine() {
        return this.stateMachine;
    }

    /**
     * @return the Moodles
     */
    @Override
    public Moodles getMoodles() {
        return this.Moodles;
    }

    /**
     * @return the stats
     */
    @Override
    public Stats getStats() {
        return this.stats;
    }

    /**
     * @return the UsedItemsOn
     */
    public Stack<String> getUsedItemsOn() {
        return this.UsedItemsOn;
    }

    /**
     * @return the useHandWeapon
     */
    public HandWeapon getUseHandWeapon() {
        return this.useHandWeapon;
    }

    /**
     * 
     * @param _useHandWeapon the useHandWeapon to set
     */
    public void setUseHandWeapon(HandWeapon _useHandWeapon) {
        this.useHandWeapon = _useHandWeapon;
    }

    /**
     * @return the legsSprite
     */
    public IsoSprite getLegsSprite() {
        return this.legsSprite;
    }

    /**
     * 
     * @param _legsSprite the legsSprite to set
     */
    public void setLegsSprite(IsoSprite _legsSprite) {
        this.legsSprite = _legsSprite;
    }

    /**
     * @return the attackTargetSquare
     */
    public IsoGridSquare getAttackTargetSquare() {
        return this.attackTargetSquare;
    }

    /**
     * 
     * @param _attackTargetSquare the attackTargetSquare to set
     */
    public void setAttackTargetSquare(IsoGridSquare _attackTargetSquare) {
        this.attackTargetSquare = _attackTargetSquare;
    }

    /**
     * @return the BloodImpactX
     */
    public float getBloodImpactX() {
        return this.BloodImpactX;
    }

    /**
     * 
     * @param _BloodImpactX the BloodImpactX to set
     */
    public void setBloodImpactX(float _BloodImpactX) {
        this.BloodImpactX = _BloodImpactX;
    }

    /**
     * @return the BloodImpactY
     */
    public float getBloodImpactY() {
        return this.BloodImpactY;
    }

    /**
     * 
     * @param _BloodImpactY the BloodImpactY to set
     */
    public void setBloodImpactY(float _BloodImpactY) {
        this.BloodImpactY = _BloodImpactY;
    }

    /**
     * @return the BloodImpactZ
     */
    public float getBloodImpactZ() {
        return this.BloodImpactZ;
    }

    /**
     * 
     * @param _BloodImpactZ the BloodImpactZ to set
     */
    public void setBloodImpactZ(float _BloodImpactZ) {
        this.BloodImpactZ = _BloodImpactZ;
    }

    /**
     * @return the bloodSplat
     */
    public IsoSprite getBloodSplat() {
        return this.bloodSplat;
    }

    /**
     * 
     * @param _bloodSplat the bloodSplat to set
     */
    public void setBloodSplat(IsoSprite _bloodSplat) {
        this.bloodSplat = _bloodSplat;
    }

    /**
     * @return the bOnBed
     */
    public boolean isbOnBed() {
        return this.bOnBed;
    }

    /**
     * 
     * @param _bOnBed the bOnBed to set
     */
    public void setbOnBed(boolean _bOnBed) {
        this.bOnBed = _bOnBed;
    }

    /**
     * @return the moveForwardVec
     */
    public Vector2 getMoveForwardVec() {
        return this.moveForwardVec;
    }

    /**
     * 
     * @param _moveForwardVec the moveForwardVec to set
     */
    public void setMoveForwardVec(Vector2 _moveForwardVec) {
        this.moveForwardVec.set(_moveForwardVec);
    }

    /**
     * @return the pathing
     */
    public boolean isPathing() {
        return this.pathing;
    }

    /**
     * 
     * @param _pathing the pathing to set
     */
    public void setPathing(boolean _pathing) {
        this.pathing = _pathing;
    }

    /**
     * @return the LocalEnemyList
     */
    public Stack<IsoGameCharacter> getLocalEnemyList() {
        return this.LocalEnemyList;
    }

    /**
     * @return the EnemyList
     */
    public Stack<IsoGameCharacter> getEnemyList() {
        return this.EnemyList;
    }

    /**
     * @return the Traits
     */
    @Override
    public TraitCollection getTraits() {
        return this.getCharacterTraits();
    }

    /**
     * @return the CharacterTraits, a TraitCollection extended with direct links to known traits
     */
    public IsoGameCharacter.CharacterTraits getCharacterTraits() {
        return this.Traits;
    }

    /**
     * @return the maxWeight
     */
    @Override
    public int getMaxWeight() {
        return this.maxWeight;
    }

    /**
     * 
     * @param _maxWeight the maxWeight to set
     */
    public void setMaxWeight(int _maxWeight) {
        this.maxWeight = _maxWeight;
    }

    /**
     * @return the maxWeightBase
     */
    public int getMaxWeightBase() {
        return this.maxWeightBase;
    }

    /**
     * 
     * @param _maxWeightBase the maxWeightBase to set
     */
    public void setMaxWeightBase(int _maxWeightBase) {
        this.maxWeightBase = _maxWeightBase;
    }

    /**
     * @return the SleepingTabletDelta
     */
    public float getSleepingTabletDelta() {
        return this.SleepingTabletDelta;
    }

    /**
     * 
     * @param _SleepingTabletDelta the SleepingTabletDelta to set
     */
    public void setSleepingTabletDelta(float _SleepingTabletDelta) {
        this.SleepingTabletDelta = _SleepingTabletDelta;
    }

    /**
     * @return the BetaEffect
     */
    public float getBetaEffect() {
        return this.BetaEffect;
    }

    /**
     * 
     * @param _BetaEffect the BetaEffect to set
     */
    public void setBetaEffect(float _BetaEffect) {
        this.BetaEffect = _BetaEffect;
    }

    /**
     * @return the DepressEffect
     */
    public float getDepressEffect() {
        return this.DepressEffect;
    }

    /**
     * 
     * @param _DepressEffect the DepressEffect to set
     */
    public void setDepressEffect(float _DepressEffect) {
        this.DepressEffect = _DepressEffect;
    }

    /**
     * @return the SleepingTabletEffect
     */
    @Override
    public float getSleepingTabletEffect() {
        return this.SleepingTabletEffect;
    }

    /**
     * 
     * @param _SleepingTabletEffect the SleepingTabletEffect to set
     */
    @Override
    public void setSleepingTabletEffect(float _SleepingTabletEffect) {
        this.SleepingTabletEffect = _SleepingTabletEffect;
    }

    /**
     * @return the BetaDelta
     */
    public float getBetaDelta() {
        return this.BetaDelta;
    }

    /**
     * 
     * @param _BetaDelta the BetaDelta to set
     */
    public void setBetaDelta(float _BetaDelta) {
        this.BetaDelta = _BetaDelta;
    }

    /**
     * @return the DepressDelta
     */
    public float getDepressDelta() {
        return this.DepressDelta;
    }

    /**
     * 
     * @param _DepressDelta the DepressDelta to set
     */
    public void setDepressDelta(float _DepressDelta) {
        this.DepressDelta = _DepressDelta;
    }

    /**
     * @return the PainEffect
     */
    public float getPainEffect() {
        return this.PainEffect;
    }

    /**
     * 
     * @param _PainEffect the PainEffect to set
     */
    public void setPainEffect(float _PainEffect) {
        this.PainEffect = _PainEffect;
    }

    /**
     * @return the PainDelta
     */
    public float getPainDelta() {
        return this.PainDelta;
    }

    /**
     * 
     * @param _PainDelta the PainDelta to set
     */
    public void setPainDelta(float _PainDelta) {
        this.PainDelta = _PainDelta;
    }

    /**
     * @return the bDoDefer
     */
    public boolean isbDoDefer() {
        return this.bDoDefer;
    }

    /**
     * 
     * @param _bDoDefer the bDoDefer to set
     */
    public void setbDoDefer(boolean _bDoDefer) {
        this.bDoDefer = _bDoDefer;
    }

    /**
     * @return the LastHeardSound
     */
    public IsoGameCharacter.Location getLastHeardSound() {
        return this.LastHeardSound;
    }

    public void setLastHeardSound(int x, int y, int z) {
        this.LastHeardSound.x = x;
        this.LastHeardSound.y = y;
        this.LastHeardSound.z = z;
    }

    /**
     * @return the lrx
     */
    public float getLrx() {
        return this.lrx;
    }

    /**
     * 
     * @param _lrx the lrx to set
     */
    public void setLrx(float _lrx) {
        this.lrx = _lrx;
    }

    /**
     * @return the lry
     */
    public float getLry() {
        return this.lry;
    }

    /**
     * 
     * @param _lry the lry to set
     */
    public void setLry(float _lry) {
        this.lry = _lry;
    }

    /**
     * @return the bClimbing
     */
    public boolean isClimbing() {
        return this.bClimbing;
    }

    /**
     * 
     * @param _bClimbing the bClimbing to set
     */
    public void setbClimbing(boolean _bClimbing) {
        this.bClimbing = _bClimbing;
    }

    /**
     * @return the lastCollidedW
     */
    public boolean isLastCollidedW() {
        return this.lastCollidedW;
    }

    /**
     * 
     * @param _lastCollidedW the lastCollidedW to set
     */
    public void setLastCollidedW(boolean _lastCollidedW) {
        this.lastCollidedW = _lastCollidedW;
    }

    /**
     * @return the lastCollidedN
     */
    public boolean isLastCollidedN() {
        return this.lastCollidedN;
    }

    /**
     * 
     * @param _lastCollidedN the lastCollidedN to set
     */
    public void setLastCollidedN(boolean _lastCollidedN) {
        this.lastCollidedN = _lastCollidedN;
    }

    /**
     * @return the fallTime
     */
    public float getFallTime() {
        return this.fallTime;
    }

    /**
     * 
     * @param _fallTime the fallTime to set
     */
    public void setFallTime(float _fallTime) {
        this.fallTime = _fallTime;
    }

    /**
     * @return the lastFallSpeed
     */
    public float getLastFallSpeed() {
        return this.lastFallSpeed;
    }

    /**
     * 
     * @param _lastFallSpeed the lastFallSpeed to set
     */
    public void setLastFallSpeed(float _lastFallSpeed) {
        this.lastFallSpeed = _lastFallSpeed;
    }

    /**
     * @return the bFalling
     */
    public boolean isbFalling() {
        return this.bFalling;
    }

    /**
     * 
     * @param _bFalling the bFalling to set
     */
    public void setbFalling(boolean _bFalling) {
        this.bFalling = _bFalling;
    }

    @Override
    public IsoBuilding getCurrentBuilding() {
        if (this.current == null) {
            return null;
        } else {
            return this.current.getRoom() == null ? null : this.current.getRoom().building;
        }
    }

    public BuildingDef getCurrentBuildingDef() {
        if (this.current == null) {
            return null;
        } else if (this.current.getRoom() == null) {
            return null;
        } else {
            return this.current.getRoom().building != null ? this.current.getRoom().building.def : null;
        }
    }

    public RoomDef getCurrentRoomDef() {
        if (this.current == null) {
            return null;
        } else {
            return this.current.getRoom() != null ? this.current.getRoom().def : null;
        }
    }

    public float getTorchStrength() {
        return 0.0F;
    }

    @Override
    public void OnAnimEvent(AnimLayer sender, AnimEvent event) {
        if (event.m_EventName != null) {
            if (event.m_EventName.equalsIgnoreCase("SetVariable") && event.m_SetVariable1 != null) {
                this.setVariable(event.m_SetVariable1, event.m_SetVariable2);
            }

            if (event.m_EventName.equalsIgnoreCase("ClearVariable")) {
                this.clearVariable(event.m_ParameterValue);
            }

            if (event.m_EventName.equalsIgnoreCase("PlaySound")) {
                this.getEmitter().playSoundImpl(event.m_ParameterValue, this);
            }

            if (event.m_EventName.equalsIgnoreCase("Footstep")) {
                this.DoFootstepSound(event.m_ParameterValue);
            }

            if (event.m_EventName.equalsIgnoreCase("DamageWhileInTrees")) {
                this.damageWhileInTrees();
            }

            int int0 = sender.getDepth();
            this.actionContext.reportEvent(int0, event.m_EventName);
            this.stateMachine.stateAnimEvent(int0, event);
        }
    }

    private void damageWhileInTrees() {
        if (!this.isZombie() && !"Tutorial".equals(Core.GameMode)) {
            int int0 = 50;
            int int1 = Rand.Next(0, BodyPartType.ToIndex(BodyPartType.MAX));
            if (this.isRunning()) {
                int0 = 30;
            }

            if (this.Traits.Outdoorsman.isSet()) {
                int0 += 50;
            }

            int0 += (int)this.getBodyPartClothingDefense(int1, false, false);
            if (Rand.NextBool(int0)) {
                this.addHole(BloodBodyPartType.FromIndex(int1));
                byte byte0 = 6;
                if (this.Traits.ThickSkinned.isSet()) {
                    byte0 += 7;
                }

                if (this.Traits.ThinSkinned.isSet()) {
                    byte0 -= 3;
                }

                if (Rand.NextBool(byte0) && (int)this.getBodyPartClothingDefense(int1, false, false) < 100) {
                    BodyPart bodyPart = this.getBodyDamage().getBodyParts().get(int1);
                    if (Rand.NextBool(byte0 + 10)) {
                        bodyPart.setCut(true, true);
                    } else {
                        bodyPart.setScratched(true, true);
                    }
                }
            }
        }
    }

    @Override
    public float getHammerSoundMod() {
        int int0 = this.getPerkLevel(PerkFactory.Perks.Woodwork);
        if (int0 == 2) {
            return 0.8F;
        } else if (int0 == 3) {
            return 0.6F;
        } else if (int0 == 4) {
            return 0.4F;
        } else {
            return int0 >= 5 ? 0.4F : 1.0F;
        }
    }

    @Override
    public float getWeldingSoundMod() {
        int int0 = this.getPerkLevel(PerkFactory.Perks.MetalWelding);
        if (int0 == 2) {
            return 0.8F;
        } else if (int0 == 3) {
            return 0.6F;
        } else if (int0 == 4) {
            return 0.4F;
        } else {
            return int0 >= 5 ? 0.4F : 1.0F;
        }
    }

    public float getBarricadeTimeMod() {
        int int0 = this.getPerkLevel(PerkFactory.Perks.Woodwork);
        if (int0 == 1) {
            return 0.8F;
        } else if (int0 == 2) {
            return 0.7F;
        } else if (int0 == 3) {
            return 0.62F;
        } else if (int0 == 4) {
            return 0.56F;
        } else if (int0 == 5) {
            return 0.5F;
        } else if (int0 == 6) {
            return 0.42F;
        } else if (int0 == 7) {
            return 0.36F;
        } else if (int0 == 8) {
            return 0.3F;
        } else if (int0 == 9) {
            return 0.26F;
        } else {
            return int0 == 10 ? 0.2F : 0.7F;
        }
    }

    public float getMetalBarricadeStrengthMod() {
        switch (this.getPerkLevel(PerkFactory.Perks.MetalWelding)) {
            case 2:
                return 1.1F;
            case 3:
                return 1.14F;
            case 4:
                return 1.18F;
            case 5:
                return 1.22F;
            case 6:
                return 1.16F;
            case 7:
                return 1.3F;
            case 8:
                return 1.34F;
            case 9:
                return 1.4F;
            case 10:
                return 1.5F;
            default:
                int int0 = this.getPerkLevel(PerkFactory.Perks.Woodwork);
                if (int0 == 2) {
                    return 1.1F;
                } else if (int0 == 3) {
                    return 1.14F;
                } else if (int0 == 4) {
                    return 1.18F;
                } else if (int0 == 5) {
                    return 1.22F;
                } else if (int0 == 6) {
                    return 1.26F;
                } else if (int0 == 7) {
                    return 1.3F;
                } else if (int0 == 8) {
                    return 1.34F;
                } else if (int0 == 9) {
                    return 1.4F;
                } else {
                    return int0 == 10 ? 1.5F : 1.0F;
                }
        }
    }

    public float getBarricadeStrengthMod() {
        int int0 = this.getPerkLevel(PerkFactory.Perks.Woodwork);
        if (int0 == 2) {
            return 1.1F;
        } else if (int0 == 3) {
            return 1.14F;
        } else if (int0 == 4) {
            return 1.18F;
        } else if (int0 == 5) {
            return 1.22F;
        } else if (int0 == 6) {
            return 1.26F;
        } else if (int0 == 7) {
            return 1.3F;
        } else if (int0 == 8) {
            return 1.34F;
        } else if (int0 == 9) {
            return 1.4F;
        } else {
            return int0 == 10 ? 1.5F : 1.0F;
        }
    }

    public float getSneakSpotMod() {
        int int0 = this.getPerkLevel(PerkFactory.Perks.Sneak);
        float float0 = 0.95F;
        if (int0 == 1) {
            float0 = 0.9F;
        }

        if (int0 == 2) {
            float0 = 0.8F;
        }

        if (int0 == 3) {
            float0 = 0.75F;
        }

        if (int0 == 4) {
            float0 = 0.7F;
        }

        if (int0 == 5) {
            float0 = 0.65F;
        }

        if (int0 == 6) {
            float0 = 0.6F;
        }

        if (int0 == 7) {
            float0 = 0.55F;
        }

        if (int0 == 8) {
            float0 = 0.5F;
        }

        if (int0 == 9) {
            float0 = 0.45F;
        }

        if (int0 == 10) {
            float0 = 0.4F;
        }

        return float0 * 1.2F;
    }

    public float getNimbleMod() {
        int int0 = this.getPerkLevel(PerkFactory.Perks.Nimble);
        if (int0 == 1) {
            return 1.1F;
        } else if (int0 == 2) {
            return 1.14F;
        } else if (int0 == 3) {
            return 1.18F;
        } else if (int0 == 4) {
            return 1.22F;
        } else if (int0 == 5) {
            return 1.26F;
        } else if (int0 == 6) {
            return 1.3F;
        } else if (int0 == 7) {
            return 1.34F;
        } else if (int0 == 8) {
            return 1.38F;
        } else if (int0 == 9) {
            return 1.42F;
        } else {
            return int0 == 10 ? 1.5F : 1.0F;
        }
    }

    @Override
    public float getFatigueMod() {
        int int0 = this.getPerkLevel(PerkFactory.Perks.Fitness);
        if (int0 == 1) {
            return 0.95F;
        } else if (int0 == 2) {
            return 0.92F;
        } else if (int0 == 3) {
            return 0.89F;
        } else if (int0 == 4) {
            return 0.87F;
        } else if (int0 == 5) {
            return 0.85F;
        } else if (int0 == 6) {
            return 0.83F;
        } else if (int0 == 7) {
            return 0.81F;
        } else if (int0 == 8) {
            return 0.79F;
        } else if (int0 == 9) {
            return 0.77F;
        } else {
            return int0 == 10 ? 0.75F : 1.0F;
        }
    }

    public float getLightfootMod() {
        int int0 = this.getPerkLevel(PerkFactory.Perks.Lightfoot);
        if (int0 == 1) {
            return 0.9F;
        } else if (int0 == 2) {
            return 0.79F;
        } else if (int0 == 3) {
            return 0.71F;
        } else if (int0 == 4) {
            return 0.65F;
        } else if (int0 == 5) {
            return 0.59F;
        } else if (int0 == 6) {
            return 0.52F;
        } else if (int0 == 7) {
            return 0.45F;
        } else if (int0 == 8) {
            return 0.37F;
        } else if (int0 == 9) {
            return 0.3F;
        } else {
            return int0 == 10 ? 0.2F : 0.99F;
        }
    }

    public float getPacingMod() {
        int int0 = this.getPerkLevel(PerkFactory.Perks.Fitness);
        if (int0 == 1) {
            return 0.8F;
        } else if (int0 == 2) {
            return 0.75F;
        } else if (int0 == 3) {
            return 0.7F;
        } else if (int0 == 4) {
            return 0.65F;
        } else if (int0 == 5) {
            return 0.6F;
        } else if (int0 == 6) {
            return 0.57F;
        } else if (int0 == 7) {
            return 0.53F;
        } else if (int0 == 8) {
            return 0.49F;
        } else if (int0 == 9) {
            return 0.46F;
        } else {
            return int0 == 10 ? 0.43F : 0.9F;
        }
    }

    public float getHyperthermiaMod() {
        float float0 = 1.0F;
        if (this.getMoodles().getMoodleLevel(MoodleType.Hyperthermia) > 1) {
            float0 = 1.0F;
            if (this.getMoodles().getMoodleLevel(MoodleType.Hyperthermia) == 4) {
                float0 = 2.0F;
            }
        }

        return float0;
    }

    public float getHittingMod() {
        int int0 = this.getPerkLevel(PerkFactory.Perks.Strength);
        if (int0 == 1) {
            return 0.8F;
        } else if (int0 == 2) {
            return 0.85F;
        } else if (int0 == 3) {
            return 0.9F;
        } else if (int0 == 4) {
            return 0.95F;
        } else if (int0 == 5) {
            return 1.0F;
        } else if (int0 == 6) {
            return 1.05F;
        } else if (int0 == 7) {
            return 1.1F;
        } else if (int0 == 8) {
            return 1.15F;
        } else if (int0 == 9) {
            return 1.2F;
        } else {
            return int0 == 10 ? 1.25F : 0.75F;
        }
    }

    public float getShovingMod() {
        int int0 = this.getPerkLevel(PerkFactory.Perks.Strength);
        if (int0 == 1) {
            return 0.8F;
        } else if (int0 == 2) {
            return 0.85F;
        } else if (int0 == 3) {
            return 0.9F;
        } else if (int0 == 4) {
            return 0.95F;
        } else if (int0 == 5) {
            return 1.0F;
        } else if (int0 == 6) {
            return 1.05F;
        } else if (int0 == 7) {
            return 1.1F;
        } else if (int0 == 8) {
            return 1.15F;
        } else if (int0 == 9) {
            return 1.2F;
        } else {
            return int0 == 10 ? 1.25F : 0.75F;
        }
    }

    public float getRecoveryMod() {
        int int0 = this.getPerkLevel(PerkFactory.Perks.Fitness);
        float float0 = 0.0F;
        if (int0 == 0) {
            float0 = 0.7F;
        }

        if (int0 == 1) {
            float0 = 0.8F;
        }

        if (int0 == 2) {
            float0 = 0.9F;
        }

        if (int0 == 3) {
            float0 = 1.0F;
        }

        if (int0 == 4) {
            float0 = 1.1F;
        }

        if (int0 == 5) {
            float0 = 1.2F;
        }

        if (int0 == 6) {
            float0 = 1.3F;
        }

        if (int0 == 7) {
            float0 = 1.4F;
        }

        if (int0 == 8) {
            float0 = 1.5F;
        }

        if (int0 == 9) {
            float0 = 1.55F;
        }

        if (int0 == 10) {
            float0 = 1.6F;
        }

        if (this.Traits.Obese.isSet()) {
            float0 = (float)(float0 * 0.4);
        }

        if (this.Traits.Overweight.isSet()) {
            float0 = (float)(float0 * 0.7);
        }

        if (this.Traits.VeryUnderweight.isSet()) {
            float0 = (float)(float0 * 0.7);
        }

        if (this.Traits.Emaciated.isSet()) {
            float0 = (float)(float0 * 0.3);
        }

        if (this instanceof IsoPlayer) {
            if (((IsoPlayer)this).getNutrition().getLipids() < -1500.0F) {
                float0 = (float)(float0 * 0.2);
            } else if (((IsoPlayer)this).getNutrition().getLipids() < -1000.0F) {
                float0 = (float)(float0 * 0.5);
            }

            if (((IsoPlayer)this).getNutrition().getProteins() < -1500.0F) {
                float0 = (float)(float0 * 0.2);
            } else if (((IsoPlayer)this).getNutrition().getProteins() < -1000.0F) {
                float0 = (float)(float0 * 0.5);
            }
        }

        return float0;
    }

    public float getWeightMod() {
        int int0 = this.getPerkLevel(PerkFactory.Perks.Strength);
        if (int0 == 1) {
            return 0.9F;
        } else if (int0 == 2) {
            return 1.07F;
        } else if (int0 == 3) {
            return 1.24F;
        } else if (int0 == 4) {
            return 1.41F;
        } else if (int0 == 5) {
            return 1.58F;
        } else if (int0 == 6) {
            return 1.75F;
        } else if (int0 == 7) {
            return 1.92F;
        } else if (int0 == 8) {
            return 2.09F;
        } else if (int0 == 9) {
            return 2.26F;
        } else {
            return int0 == 10 ? 2.5F : 0.8F;
        }
    }

    public int getHitChancesMod() {
        int int0 = this.getPerkLevel(PerkFactory.Perks.Aiming);
        if (int0 == 1) {
            return 1;
        } else if (int0 == 2) {
            return 1;
        } else if (int0 == 3) {
            return 2;
        } else if (int0 == 4) {
            return 2;
        } else if (int0 == 5) {
            return 3;
        } else if (int0 == 6) {
            return 3;
        } else if (int0 == 7) {
            return 4;
        } else if (int0 == 8) {
            return 4;
        } else if (int0 == 9) {
            return 5;
        } else {
            return int0 == 10 ? 5 : 1;
        }
    }

    public float getSprintMod() {
        int int0 = this.getPerkLevel(PerkFactory.Perks.Sprinting);
        if (int0 == 1) {
            return 1.1F;
        } else if (int0 == 2) {
            return 1.15F;
        } else if (int0 == 3) {
            return 1.2F;
        } else if (int0 == 4) {
            return 1.25F;
        } else if (int0 == 5) {
            return 1.3F;
        } else if (int0 == 6) {
            return 1.35F;
        } else if (int0 == 7) {
            return 1.4F;
        } else if (int0 == 8) {
            return 1.45F;
        } else if (int0 == 9) {
            return 1.5F;
        } else {
            return int0 == 10 ? 1.6F : 0.9F;
        }
    }

    /**
     * Return the current lvl of a perk (skill)
     */
    @Override
    public int getPerkLevel(PerkFactory.Perk perks) {
        IsoGameCharacter.PerkInfo perkInfo = this.getPerkInfo(perks);
        return perkInfo != null ? perkInfo.level : 0;
    }

    @Override
    public void setPerkLevelDebug(PerkFactory.Perk perks, int level) {
        IsoGameCharacter.PerkInfo perkInfo = this.getPerkInfo(perks);
        if (perkInfo != null) {
            perkInfo.level = level;
        }

        if (GameClient.bClient && this instanceof IsoPlayer) {
            GameClient.sendPerks((IsoPlayer)this);
        }
    }

    @Override
    public void LoseLevel(PerkFactory.Perk perk) {
        IsoGameCharacter.PerkInfo perkInfo = this.getPerkInfo(perk);
        if (perkInfo != null) {
            perkInfo.level--;
            if (perkInfo.level < 0) {
                perkInfo.level = 0;
            }

            LuaEventManager.triggerEvent("LevelPerk", this, perk, perkInfo.level, false);
            if (perk == PerkFactory.Perks.Sneak && GameClient.bClient && this instanceof IsoPlayer) {
                GameClient.sendPerks((IsoPlayer)this);
            }
        } else {
            LuaEventManager.triggerEvent("LevelPerk", this, perk, 0, false);
        }
    }

    /**
     * Level up a perk (max lvl 5)
     * 
     * @param perk the perk to lvl up
     * @param removePick did we remove a skill pts ? (for example passiv skill automatically lvl up, without consuming                    skill pts)
     */
    @Override
    public void LevelPerk(PerkFactory.Perk perk, boolean removePick) {
        Objects.requireNonNull(perk, "perk is null");
        if (perk == PerkFactory.Perks.MAX) {
            throw new IllegalArgumentException("perk == Perks.MAX");
        } else {
            IsoPlayer player = Type.tryCastTo(this, IsoPlayer.class);
            IsoGameCharacter.PerkInfo perkInfo = this.getPerkInfo(perk);
            if (perkInfo != null) {
                perkInfo.level++;
                if (player != null && !"Tutorial".equals(Core.GameMode) && this.getHoursSurvived() > 0.016666666666666666) {
                    HaloTextHelper.addTextWithArrow(player, "+1 " + perk.getName(), true, HaloTextHelper.getColorGreen());
                }

                if (perkInfo.level > 10) {
                    perkInfo.level = 10;
                }

                if (GameClient.bClient && player != null) {
                    GameClient.instance.sendSyncXp(player);
                }

                LuaEventManager.triggerEventGarbage("LevelPerk", this, perk, perkInfo.level, true);
                if (GameClient.bClient && player != null) {
                    GameClient.sendPerks(player);
                }
            } else {
                perkInfo = new IsoGameCharacter.PerkInfo();
                perkInfo.perk = perk;
                perkInfo.level = 1;
                this.PerkList.add(perkInfo);
                if (player != null && !"Tutorial".equals(Core.GameMode) && this.getHoursSurvived() > 0.016666666666666666) {
                    HaloTextHelper.addTextWithArrow(player, "+1 " + perk.getName(), true, HaloTextHelper.getColorGreen());
                }

                if (GameClient.bClient && this instanceof IsoPlayer) {
                    GameClient.instance.sendSyncXp(player);
                }

                LuaEventManager.triggerEvent("LevelPerk", this, perk, perkInfo.level, true);
            }
        }
    }

    /**
     * Level up a perk (max lvl 5)
     * 
     * @param perk the perk to lvl up (a skill points is removed)
     */
    @Override
    public void LevelPerk(PerkFactory.Perk perk) {
        this.LevelPerk(perk, true);
    }

    public void level0(PerkFactory.Perk perk) {
        IsoGameCharacter.PerkInfo perkInfo = this.getPerkInfo(perk);
        if (perkInfo != null) {
            perkInfo.level = 0;
        }
    }

    public IsoGameCharacter.Location getLastKnownLocationOf(String character) {
        return this.LastKnownLocation.containsKey(character) ? this.LastKnownLocation.get(character) : null;
    }

    /**
     * Used when you read a book, magazine or newspaper
     * 
     * @param literature the book to read
     */
    @Override
    public void ReadLiterature(Literature literature) {
        this.stats.stress = this.stats.stress + literature.getStressChange();
        this.getBodyDamage().JustReadSomething(literature);
        if (literature.getTeachedRecipes() != null) {
            for (int int0 = 0; int0 < literature.getTeachedRecipes().size(); int0++) {
                if (!this.getKnownRecipes().contains(literature.getTeachedRecipes().get(int0))) {
                    this.getKnownRecipes().add(literature.getTeachedRecipes().get(int0));
                }
            }
        }

        literature.Use();
    }

    public void OnDeath() {
        LuaEventManager.triggerEvent("OnCharacterDeath", this);
    }

    public void splatBloodFloorBig() {
        if (this.getCurrentSquare() != null && this.getCurrentSquare().getChunk() != null) {
            this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, this.z, Rand.Next(20));
        }
    }

    public void splatBloodFloor() {
        if (this.getCurrentSquare() != null) {
            if (this.getCurrentSquare().getChunk() != null) {
                if (this.isDead() && Rand.Next(10) == 0) {
                    this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, this.z, Rand.Next(20));
                }

                if (Rand.Next(14) == 0) {
                    this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, this.z, Rand.Next(8));
                }

                if (Rand.Next(50) == 0) {
                    this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, this.z, Rand.Next(20));
                }
            }
        }
    }

    public int getThreatLevel() {
        int int0 = this.LocalRelevantEnemyList.size();
        int0 += this.VeryCloseEnemyList.size() * 10;
        if (int0 > 20) {
            return 3;
        } else if (int0 > 10) {
            return 2;
        } else {
            return int0 > 0 ? 1 : 0;
        }
    }

    public boolean isDead() {
        return this.Health <= 0.0F || this.BodyDamage != null && this.BodyDamage.getHealth() <= 0.0F;
    }

    public boolean isAlive() {
        return !this.isDead();
    }

    public void Seen(Stack<IsoMovingObject> SeenList) {
        synchronized (this.LocalList) {
            this.LocalList.clear();
            this.LocalList.addAll(SeenList);
        }
    }

    public boolean CanSee(IsoMovingObject obj) {
        return LosUtil.lineClear(this.getCell(), (int)this.getX(), (int)this.getY(), (int)this.getZ(), (int)obj.getX(), (int)obj.getY(), (int)obj.getZ(), false)
            != LosUtil.TestResults.Blocked;
    }

    public IsoGridSquare getLowDangerInVicinity(int attempts, int range) {
        float float0 = -1000000.0F;
        IsoGridSquare square0 = null;

        for (int int0 = 0; int0 < attempts; int0++) {
            float float1 = 0.0F;
            int int1 = Rand.Next(-range, range);
            int int2 = Rand.Next(-range, range);
            IsoGridSquare square1 = this.getCell().getGridSquare((int)this.getX() + int1, (int)this.getY() + int2, (int)this.getZ());
            if (square1 != null && square1.isFree(true)) {
                float float2 = square1.getMovingObjects().size();
                if (square1.getE() != null) {
                    float2 += square1.getE().getMovingObjects().size();
                }

                if (square1.getS() != null) {
                    float2 += square1.getS().getMovingObjects().size();
                }

                if (square1.getW() != null) {
                    float2 += square1.getW().getMovingObjects().size();
                }

                if (square1.getN() != null) {
                    float2 += square1.getN().getMovingObjects().size();
                }

                float1 -= float2 * 1000.0F;
                if (float1 > float0) {
                    float0 = float1;
                    square0 = square1;
                }
            }
        }

        return square0;
    }

    public void Anger(int amount) {
        float float0 = 10.0F;
        if (Rand.Next(100) < float0) {
            amount *= 2;
        }

        amount = (int)(amount * (this.stats.getStress() + 1.0F));
        amount = (int)(amount * (this.BodyDamage.getUnhappynessLevel() / 100.0F + 1.0F));
        this.stats.Anger += amount / 100.0F;
    }

    @Override
    public boolean hasEquipped(String itemType) {
        if (itemType.contains(".")) {
            itemType = itemType.split("\\.")[1];
        }

        return this.leftHandItem != null && this.leftHandItem.getType().equals(itemType)
            ? true
            : this.rightHandItem != null && this.rightHandItem.getType().equals(itemType);
    }

    @Override
    public boolean hasEquippedTag(String tag) {
        return this.leftHandItem != null && this.leftHandItem.hasTag(tag) ? true : this.rightHandItem != null && this.rightHandItem.hasTag(tag);
    }

    /**
     * 
     * @param directions the dir to set
     */
    @Override
    public void setDir(IsoDirections directions) {
        this.dir = directions;
        this.getVectorFromDirection(this.m_forwardDirection);
    }

    public void Callout(boolean doAnim) {
        if (this.isCanShout()) {
            this.Callout();
            if (doAnim) {
                this.playEmote("shout");
            }
        }
    }

    @Override
    public void Callout() {
        String string = "";
        byte byte0 = 30;
        if (Core.getInstance().getGameMode().equals("Tutorial")) {
            string = Translator.getText("IGUI_PlayerText_CalloutTutorial");
        } else if (this.isSneaking()) {
            byte0 = 6;
            switch (Rand.Next(3)) {
                case 0:
                    string = Translator.getText("IGUI_PlayerText_Callout1Sneak");
                    break;
                case 1:
                    string = Translator.getText("IGUI_PlayerText_Callout2Sneak");
                    break;
                case 2:
                    string = Translator.getText("IGUI_PlayerText_Callout3Sneak");
            }
        } else {
            switch (Rand.Next(3)) {
                case 0:
                    string = Translator.getText("IGUI_PlayerText_Callout1New");
                    break;
                case 1:
                    string = Translator.getText("IGUI_PlayerText_Callout2New");
                    break;
                case 2:
                    string = Translator.getText("IGUI_PlayerText_Callout3New");
            }
        }

        WorldSoundManager.instance.addSound(this, (int)this.x, (int)this.y, (int)this.z, byte0, byte0);
        this.SayShout(string);
        this.callOut = true;
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        super.load(input, WorldVersion, IS_DEBUG_SAVE);
        this.getVectorFromDirection(this.m_forwardDirection);
        if (input.get() == 1) {
            this.descriptor = new SurvivorDesc(true);
            this.descriptor.load(input, WorldVersion, this);
            this.bFemale = this.descriptor.isFemale();
        }

        this.getVisual().load(input, WorldVersion);
        ArrayList arrayList0 = this.inventory.load(input, WorldVersion);
        this.savedInventoryItems.clear();

        for (int int0 = 0; int0 < arrayList0.size(); int0++) {
            this.savedInventoryItems.add((InventoryItem)arrayList0.get(int0));
        }

        this.Asleep = input.get() == 1;
        this.ForceWakeUpTime = input.getFloat();
        if (!this.isZombie()) {
            this.stats.load(input, WorldVersion);
            this.BodyDamage.load(input, WorldVersion);
            this.xp.load(input, WorldVersion);
            ArrayList arrayList1 = this.inventory.IncludingObsoleteItems;
            int int1 = input.getInt();
            if (int1 >= 0 && int1 < arrayList1.size()) {
                this.leftHandItem = (InventoryItem)arrayList1.get(int1);
            }

            int1 = input.getInt();
            if (int1 >= 0 && int1 < arrayList1.size()) {
                this.rightHandItem = (InventoryItem)arrayList1.get(int1);
            }

            this.setEquipParent(null, this.leftHandItem);
            this.setEquipParent(null, this.rightHandItem);
        }

        boolean boolean0 = input.get() == 1;
        if (boolean0) {
            this.SetOnFire();
        }

        this.DepressEffect = input.getFloat();
        this.DepressFirstTakeTime = input.getFloat();
        this.BetaEffect = input.getFloat();
        this.BetaDelta = input.getFloat();
        this.PainEffect = input.getFloat();
        this.PainDelta = input.getFloat();
        this.SleepingTabletEffect = input.getFloat();
        this.SleepingTabletDelta = input.getFloat();
        int int2 = input.getInt();

        for (int int3 = 0; int3 < int2; int3++) {
            IsoGameCharacter.ReadBook readBook = new IsoGameCharacter.ReadBook();
            readBook.fullType = GameWindow.ReadString(input);
            readBook.alreadyReadPages = input.getInt();
            this.ReadBooks.add(readBook);
        }

        this.reduceInfectionPower = input.getFloat();
        int int4 = input.getInt();

        for (int int5 = 0; int5 < int4; int5++) {
            this.knownRecipes.add(GameWindow.ReadString(input));
        }

        this.lastHourSleeped = input.getInt();
        this.timeSinceLastSmoke = input.getFloat();
        this.beardGrowTiming = input.getFloat();
        this.hairGrowTiming = input.getFloat();
        this.setUnlimitedCarry(input.get() == 1);
        this.setBuildCheat(input.get() == 1);
        this.setHealthCheat(input.get() == 1);
        this.setMechanicsCheat(input.get() == 1);
        if (WorldVersion >= 176) {
            this.setMovablesCheat(input.get() == 1);
            this.setFarmingCheat(input.get() == 1);
            this.setTimedActionInstantCheat(input.get() == 1);
            this.setUnlimitedEndurance(input.get() == 1);
        }

        if (WorldVersion >= 161) {
            this.setSneaking(input.get() == 1);
            this.setDeathDragDown(input.get() == 1);
        }
    }

    @Override
    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        super.save(output, IS_DEBUG_SAVE);
        if (this.descriptor == null) {
            output.put((byte)0);
        } else {
            output.put((byte)1);
            this.descriptor.save(output);
        }

        this.getVisual().save(output);
        ArrayList arrayList = this.inventory.save(output, this);
        this.savedInventoryItems.clear();

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            this.savedInventoryItems.add((InventoryItem)arrayList.get(int0));
        }

        output.put((byte)(this.Asleep ? 1 : 0));
        output.putFloat(this.ForceWakeUpTime);
        if (!this.isZombie()) {
            this.stats.save(output);
            this.BodyDamage.save(output);
            this.xp.save(output);
            if (this.leftHandItem != null) {
                output.putInt(this.inventory.getItems().indexOf(this.leftHandItem));
            } else {
                output.putInt(-1);
            }

            if (this.rightHandItem != null) {
                output.putInt(this.inventory.getItems().indexOf(this.rightHandItem));
            } else {
                output.putInt(-1);
            }
        }

        output.put((byte)(this.OnFire ? 1 : 0));
        output.putFloat(this.DepressEffect);
        output.putFloat(this.DepressFirstTakeTime);
        output.putFloat(this.BetaEffect);
        output.putFloat(this.BetaDelta);
        output.putFloat(this.PainEffect);
        output.putFloat(this.PainDelta);
        output.putFloat(this.SleepingTabletEffect);
        output.putFloat(this.SleepingTabletDelta);
        output.putInt(this.ReadBooks.size());

        for (int int1 = 0; int1 < this.ReadBooks.size(); int1++) {
            IsoGameCharacter.ReadBook readBook = this.ReadBooks.get(int1);
            GameWindow.WriteString(output, readBook.fullType);
            output.putInt(readBook.alreadyReadPages);
        }

        output.putFloat(this.reduceInfectionPower);
        output.putInt(this.knownRecipes.size());

        for (int int2 = 0; int2 < this.knownRecipes.size(); int2++) {
            String string = this.knownRecipes.get(int2);
            GameWindow.WriteString(output, string);
        }

        output.putInt(this.lastHourSleeped);
        output.putFloat(this.timeSinceLastSmoke);
        output.putFloat(this.beardGrowTiming);
        output.putFloat(this.hairGrowTiming);
        output.put((byte)(this.isUnlimitedCarry() ? 1 : 0));
        output.put((byte)(this.isBuildCheat() ? 1 : 0));
        output.put((byte)(this.isHealthCheat() ? 1 : 0));
        output.put((byte)(this.isMechanicsCheat() ? 1 : 0));
        output.put((byte)(this.isMovablesCheat() ? 1 : 0));
        output.put((byte)(this.isFarmingCheat() ? 1 : 0));
        output.put((byte)(this.isTimedActionInstantCheat() ? 1 : 0));
        output.put((byte)(this.isUnlimitedEndurance() ? 1 : 0));
        output.put((byte)(this.isSneaking() ? 1 : 0));
        output.put((byte)(this.isDeathDragDown() ? 1 : 0));
    }

    public ChatElement getChatElement() {
        return this.chatElement;
    }

    @Override
    public void StartAction(BaseAction act) {
        this.CharacterActions.clear();
        this.CharacterActions.push(act);
        if (act.valid()) {
            act.waitToStart();
        }
    }

    public void QueueAction(BaseAction act) {
    }

    @Override
    public void StopAllActionQueue() {
        if (!this.CharacterActions.isEmpty()) {
            BaseAction baseAction = this.CharacterActions.get(0);
            if (baseAction.bStarted) {
                baseAction.stop();
            }

            this.CharacterActions.clear();
            if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
                UIManager.getProgressBar(((IsoPlayer)this).getPlayerNum()).setValue(0.0F);
            }
        }
    }

    public void StopAllActionQueueRunning() {
        if (!this.CharacterActions.isEmpty()) {
            BaseAction baseAction = this.CharacterActions.get(0);
            if (baseAction.StopOnRun) {
                if (baseAction.bStarted) {
                    baseAction.stop();
                }

                this.CharacterActions.clear();
                if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
                    UIManager.getProgressBar(((IsoPlayer)this).getPlayerNum()).setValue(0.0F);
                }
            }
        }
    }

    public void StopAllActionQueueAiming() {
        if (this.CharacterActions.size() != 0) {
            BaseAction baseAction = this.CharacterActions.get(0);
            if (baseAction.StopOnAim) {
                if (baseAction.bStarted) {
                    baseAction.stop();
                }

                this.CharacterActions.clear();
                if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
                    UIManager.getProgressBar(((IsoPlayer)this).getPlayerNum()).setValue(0.0F);
                }
            }
        }
    }

    public void StopAllActionQueueWalking() {
        if (this.CharacterActions.size() != 0) {
            BaseAction baseAction = this.CharacterActions.get(0);
            if (baseAction.StopOnWalk) {
                if (baseAction.bStarted) {
                    baseAction.stop();
                }

                this.CharacterActions.clear();
                if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
                    UIManager.getProgressBar(((IsoPlayer)this).getPlayerNum()).setValue(0.0F);
                }
            }
        }
    }

    @Override
    public String GetAnimSetName() {
        return "Base";
    }

    public void SleepingTablet(float _SleepingTabletDelta) {
        this.SleepingTabletEffect = 6600.0F;
        this.SleepingTabletDelta += _SleepingTabletDelta;
    }

    public void BetaBlockers(float delta) {
        this.BetaEffect = 6600.0F;
        this.BetaDelta += delta;
    }

    public void BetaAntiDepress(float delta) {
        if (this.DepressEffect == 0.0F) {
            this.DepressFirstTakeTime = 10000.0F;
        }

        this.DepressEffect = 6600.0F;
        this.DepressDelta += delta;
    }

    public void PainMeds(float delta) {
        this.PainEffect = 5400.0F;
        this.PainDelta += delta;
    }

    @Override
    public void initSpritePartsEmpty() {
        this.InitSpriteParts(this.descriptor);
    }

    public void InitSpriteParts(SurvivorDesc desc) {
        this.sprite.AnimMap.clear();
        this.sprite.AnimStack.clear();
        this.sprite.CurrentAnim = null;
        this.legsSprite = this.sprite;
        this.legsSprite.name = desc.torso;
        this.bUseParts = true;
    }

    @Override
    public boolean HasTrait(String trait) {
        return this.Traits.contains(trait);
    }

    public void ApplyInBedOffset(boolean apply) {
        if (apply) {
            if (!this.bOnBed) {
                this.offsetX -= 20.0F;
                this.offsetY += 21.0F;
                this.bOnBed = true;
            }
        } else if (this.bOnBed) {
            this.offsetX += 20.0F;
            this.offsetY -= 21.0F;
            this.bOnBed = false;
        }
    }

    @Override
    public void Dressup(SurvivorDesc desc) {
        if (!this.isZombie()) {
            if (this.wornItems != null) {
                ItemVisuals itemVisuals = new ItemVisuals();
                desc.getItemVisuals(itemVisuals);
                this.wornItems.setFromItemVisuals(itemVisuals);
                this.wornItems.addItemsToItemContainer(this.inventory);
                desc.wornItems.clear();
                this.onWornItemsChanged();
            }
        }
    }

    @Override
    public void PlayAnim(String string) {
    }

    @Override
    public void PlayAnimWithSpeed(String string, float framesSpeedPerFrame) {
    }

    @Override
    public void PlayAnimUnlooped(String string) {
    }

    public void DirectionFromVector(Vector2 vecA) {
        this.dir = IsoDirections.fromAngle(vecA);
    }

    public void DoFootstepSound(String type) {
        float float0 = 1.0F;
        switch (type) {
            case "sneak_walk":
                float0 = 0.2F;
                break;
            case "sneak_run":
                float0 = 0.5F;
                break;
            case "strafe":
                float0 = this.bSneaking ? 0.2F : 0.3F;
                break;
            case "walk":
                float0 = 0.5F;
                break;
            case "run":
                float0 = 1.3F;
                break;
            case "sprint":
                float0 = 1.8F;
        }

        this.DoFootstepSound(float0);
    }

    public void DoFootstepSound(float volume) {
        IsoPlayer player = Type.tryCastTo(this, IsoPlayer.class);
        if (GameClient.bClient && player != null && player.networkAI != null) {
            player.networkAI.footstepSoundRadius = 0;
        }

        if (player == null || !player.isGhostMode() || DebugOptions.instance.Character.Debug.PlaySoundWhenInvisible.getValue()) {
            if (this.getCurrentSquare() != null) {
                if (!(volume <= 0.0F)) {
                    volume *= 1.4F;
                    if (this.Traits.Graceful.isSet()) {
                        volume *= 0.6F;
                    }

                    if (this.Traits.Clumsy.isSet()) {
                        volume *= 1.2F;
                    }

                    if (this.getWornItem("Shoes") == null) {
                        volume *= 0.5F;
                    }

                    volume *= this.getLightfootMod();
                    volume *= 2.0F - this.getNimbleMod();
                    if (this.bSneaking) {
                        volume *= this.getSneakSpotMod();
                    }

                    if (volume > 0.0F) {
                        this.emitter.playFootsteps("HumanFootstepsCombined", volume);
                        if (player != null && player.isGhostMode()) {
                            return;
                        }

                        int int0 = (int)Math.ceil(volume * 10.0F);
                        if (this.bSneaking) {
                            int0 = Math.max(1, int0);
                        }

                        if (this.getCurrentSquare().getRoom() != null) {
                            int0 = (int)(int0 * 0.5F);
                        }

                        int int1 = 2;
                        if (this.bSneaking) {
                            int1 = Math.min(12, 4 + this.getPerkLevel(PerkFactory.Perks.Lightfoot));
                        }

                        if (GameClient.bClient && player != null && player.networkAI != null) {
                            player.networkAI.footstepSoundRadius = (byte)int0;
                        }

                        if (Rand.Next(int1) == 0) {
                            WorldSoundManager.instance
                                .addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), int0, int0, false, 0.0F, 1.0F, false, false, false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean Eat(InventoryItem info, float percentage) {
        Food food = Type.tryCastTo(info, Food.class);
        if (food == null) {
            return false;
        } else {
            percentage = PZMath.clamp(percentage, 0.0F, 1.0F);
            if (food.getRequireInHandOrInventory() != null) {
                InventoryItem item0 = null;

                for (int int0 = 0; int0 < food.getRequireInHandOrInventory().size(); int0++) {
                    String string0 = food.getRequireInHandOrInventory().get(int0);
                    item0 = this.getInventory().FindAndReturn(string0);
                    if (item0 != null) {
                        item0.Use();
                        break;
                    }
                }
            }

            float float0 = percentage;
            if (food.getBaseHunger() != 0.0F && food.getHungChange() != 0.0F) {
                float float1 = food.getBaseHunger() * percentage;
                float float2 = float1 / food.getHungChange();
                float2 = PZMath.clamp(float2, 0.0F, 1.0F);
                percentage = float2;
            }

            if (food.getHungChange() < 0.0F && food.getHungChange() * (1.0F - percentage) > -0.01F) {
                percentage = 1.0F;
            }

            if (food.getHungChange() == 0.0F && food.getThirstChange() < 0.0F && food.getThirstChange() * (1.0F - percentage) > -0.01F) {
                percentage = 1.0F;
            }

            this.stats.thirst = this.stats.thirst + food.getThirstChange() * percentage;
            if (this.stats.thirst < 0.0F) {
                this.stats.thirst = 0.0F;
            }

            this.stats.hunger = this.stats.hunger + food.getHungerChange() * percentage;
            this.stats.endurance = this.stats.endurance + food.getEnduranceChange() * percentage;
            this.stats.stress = this.stats.stress + food.getStressChange() * percentage;
            this.stats.fatigue = this.stats.fatigue + food.getFatigueChange() * percentage;
            IsoPlayer player = Type.tryCastTo(this, IsoPlayer.class);
            if (player != null) {
                Nutrition nutrition = player.getNutrition();
                nutrition.setCalories(nutrition.getCalories() + food.getCalories() * percentage);
                nutrition.setCarbohydrates(nutrition.getCarbohydrates() + food.getCarbohydrates() * percentage);
                nutrition.setProteins(nutrition.getProteins() + food.getProteins() * percentage);
                nutrition.setLipids(nutrition.getLipids() + food.getLipids() * percentage);
            }

            this.BodyDamage.setPainReduction(this.BodyDamage.getPainReduction() + food.getPainReduction() * percentage);
            this.BodyDamage.setColdReduction(this.BodyDamage.getColdReduction() + food.getFluReduction() * percentage);
            if (this.BodyDamage.getFoodSicknessLevel() > 0.0F && food.getReduceFoodSickness() > 0.0F && this.effectiveEdibleBuffTimer <= 0.0F) {
                float float3 = this.BodyDamage.getFoodSicknessLevel();
                this.BodyDamage.setFoodSicknessLevel(this.BodyDamage.getFoodSicknessLevel() - food.getReduceFoodSickness() * percentage);
                if (this.BodyDamage.getFoodSicknessLevel() < 0.0F) {
                    this.BodyDamage.setFoodSicknessLevel(0.0F);
                }

                float float4 = this.BodyDamage.getPoisonLevel();
                this.BodyDamage.setPoisonLevel(this.BodyDamage.getPoisonLevel() - food.getReduceFoodSickness() * percentage);
                if (this.BodyDamage.getPoisonLevel() < 0.0F) {
                    this.BodyDamage.setPoisonLevel(0.0F);
                }

                if (this.Traits.IronGut.isSet()) {
                    this.effectiveEdibleBuffTimer = Rand.Next(80.0F, 150.0F);
                } else if (this.Traits.WeakStomach.isSet()) {
                    this.effectiveEdibleBuffTimer = Rand.Next(120.0F, 230.0F);
                } else {
                    this.effectiveEdibleBuffTimer = Rand.Next(200.0F, 280.0F);
                }
            }

            this.BodyDamage.JustAteFood(food, percentage);
            if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
                GameClient.instance.eatFood((IsoPlayer)this, food, percentage);
            }

            if (food.getOnEat() != null) {
                Object object = LuaManager.getFunctionObject(food.getOnEat());
                if (object != null) {
                    LuaManager.caller.pcallvoid(LuaManager.thread, object, info, this, BoxedStaticValues.toDouble(percentage));
                }
            }

            if (percentage == 1.0F) {
                food.setHungChange(0.0F);
                food.UseItem();
            } else {
                float float5 = food.getHungChange();
                float float6 = food.getThirstChange();
                food.multiplyFoodValues(1.0F - percentage);
                if (float5 < 0.0F && food.getHungerChange() > -0.00999) {
                }

                if (float5 == 0.0F && float6 < 0.0F && food.getThirstChange() > -0.01F) {
                    food.setHungChange(0.0F);
                    food.UseItem();
                    return true;
                }

                float float7 = 0.0F;
                if (food.isCustomWeight()) {
                    String string1 = food.getReplaceOnUseFullType();
                    Item item1 = string1 == null ? null : ScriptManager.instance.getItem(string1);
                    if (item1 != null) {
                        float7 = item1.getActualWeight();
                    }

                    food.setWeight(food.getWeight() - float7 - float0 * (food.getWeight() - float7) + float7);
                }
            }

            return true;
        }
    }

    @Override
    public boolean Eat(InventoryItem info) {
        return this.Eat(info, 1.0F);
    }

    public void FireCheck() {
        if (!this.OnFire) {
            if (!GameServer.bServer || !(this instanceof IsoPlayer)) {
                if (!GameClient.bClient || !this.isZombie() || !(this instanceof IsoZombie) || !((IsoZombie)this).isRemoteZombie()) {
                    if (this.isZombie() && VirtualZombieManager.instance.isReused((IsoZombie)this)) {
                        DebugLog.log(DebugType.Zombie, "FireCheck running on REUSABLE ZOMBIE - IGNORED " + this);
                    } else if (this.getVehicle() == null) {
                        if (this.square != null
                            && !GameServer.bServer
                            && (
                                !GameClient.bClient
                                    || this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()
                                    || this instanceof IsoZombie && !((IsoZombie)this).isRemoteZombie()
                            )
                            && this.square.getProperties().Is(IsoFlagType.burning)) {
                            if ((!(this instanceof IsoPlayer) || Rand.Next(Rand.AdjustForFramerate(70)) != 0) && !this.isZombie()) {
                                if (!(this instanceof IsoPlayer)) {
                                    this.Health = this.Health - this.FireKillRate * GameTime.instance.getMultiplier() / 2.0F;
                                    this.setAttackedBy(null);
                                } else {
                                    float float0 = this.FireKillRate * GameTime.instance.getMultiplier() * GameTime.instance.getMinutesPerDay() / 1.6F / 2.0F;
                                    this.BodyDamage.ReduceGeneralHealth(float0);
                                    LuaEventManager.triggerEvent("OnPlayerGetDamage", this, "FIRE", float0);
                                    this.BodyDamage.OnFire(true);
                                    this.forceAwake();
                                }

                                if (this.isDead()) {
                                    IsoFireManager.RemoveBurningCharacter(this);
                                    if (this.isZombie()) {
                                        LuaEventManager.triggerEvent("OnZombieDead", this);
                                        if (GameClient.bClient) {
                                            this.setAttackedBy(IsoWorld.instance.CurrentCell.getFakeZombieForHit());
                                        }
                                    }
                                }
                            } else {
                                this.SetOnFire();
                            }
                        }
                    }
                }
            }
        }
    }

    public String getPrimaryHandType() {
        return this.leftHandItem == null ? null : this.leftHandItem.getType();
    }

    @Override
    public float getGlobalMovementMod(boolean bDoNoises) {
        return this.getCurrentState() != ClimbOverFenceState.instance()
                && this.getCurrentState() != ClimbThroughWindowState.instance()
                && this.getCurrentState() != ClimbOverWallState.instance()
            ? super.getGlobalMovementMod(bDoNoises)
            : 1.0F;
    }

    public float getMoveSpeed() {
        tempo2.x = this.getX() - this.getLx();
        tempo2.y = this.getY() - this.getLy();
        return tempo2.getLength();
    }

    public String getSecondaryHandType() {
        return this.rightHandItem == null ? null : this.rightHandItem.getType();
    }

    public boolean HasItem(String string) {
        return string == null
            ? true
            : string.equals(this.getSecondaryHandType()) || string.equals(this.getPrimaryHandType()) || this.inventory.contains(string);
    }

    @Override
    public void changeState(State state) {
        this.stateMachine.changeState(state, null);
    }

    @Override
    public State getCurrentState() {
        return this.stateMachine.getCurrent();
    }

    @Override
    public boolean isCurrentState(State state) {
        return this.stateMachine.isSubstate(state) ? true : this.stateMachine.getCurrent() == state;
    }

    public HashMap<Object, Object> getStateMachineParams(State state) {
        return this.StateMachineParams.computeIfAbsent(state, var0 -> new HashMap<>());
    }

    public void setStateMachineLocked(boolean val) {
        this.stateMachine.setLocked(val);
    }

    @Override
    public float Hit(HandWeapon weapon, IsoGameCharacter wielder, float damageSplit, boolean bIgnoreDamage, float modDelta) {
        return this.Hit(weapon, wielder, damageSplit, bIgnoreDamage, modDelta, false);
    }

    @Override
    public float Hit(HandWeapon weapon, IsoGameCharacter wielder, float damageSplit, boolean bIgnoreDamage, float modDelta, boolean bRemote) {
        if (wielder != null && weapon != null) {
            if (!bIgnoreDamage && this.isZombie()) {
                IsoZombie zombie0 = (IsoZombie)this;
                zombie0.setHitTime(zombie0.getHitTime() + 1);
                if (zombie0.getHitTime() >= 4 && !bRemote) {
                    damageSplit = (float)(damageSplit * ((zombie0.getHitTime() - 2) * 1.5));
                }
            }

            if (wielder instanceof IsoPlayer && ((IsoPlayer)wielder).bDoShove && !((IsoPlayer)wielder).isAimAtFloor()) {
                bIgnoreDamage = true;
                modDelta *= 1.5F;
            }

            LuaEventManager.triggerEvent("OnWeaponHitCharacter", wielder, this, weapon, damageSplit);
            LuaEventManager.triggerEvent("OnPlayerGetDamage", this, "WEAPONHIT", damageSplit);
            if (LuaHookManager.TriggerHook("WeaponHitCharacter", wielder, this, weapon, damageSplit)) {
                return 0.0F;
            } else if (this.m_avoidDamage) {
                this.m_avoidDamage = false;
                return 0.0F;
            } else {
                if (this.noDamage) {
                    bIgnoreDamage = true;
                    this.noDamage = false;
                }

                if (this instanceof IsoSurvivor && !this.EnemyList.contains(wielder)) {
                    this.EnemyList.add(wielder);
                }

                this.staggerTimeMod = weapon.getPushBackMod() * weapon.getKnockbackMod(wielder) * wielder.getShovingMod();
                if (this.isZombie() && Rand.Next(3) == 0 && GameServer.bServer) {
                }

                wielder.addWorldSoundUnlessInvisible(5, 1, false);
                this.hitDir.x = this.getX();
                this.hitDir.y = this.getY();
                this.hitDir.x = this.hitDir.x - wielder.getX();
                this.hitDir.y = this.hitDir.y - wielder.getY();
                this.getHitDir().normalize();
                this.hitDir.x = this.hitDir.x * weapon.getPushBackMod();
                this.hitDir.y = this.hitDir.y * weapon.getPushBackMod();
                this.hitDir.rotate(weapon.HitAngleMod);
                this.setAttackedBy(wielder);
                float float0 = damageSplit;
                if (!bRemote) {
                    float0 = this.processHitDamage(weapon, wielder, damageSplit, bIgnoreDamage, modDelta);
                }

                float float1 = 0.0F;
                if (weapon.isTwoHandWeapon() && (wielder.getPrimaryHandItem() != weapon || wielder.getSecondaryHandItem() != weapon)) {
                    float1 = weapon.getWeight() / 1.5F / 10.0F;
                }

                float float2 = (weapon.getWeight() * 0.28F * weapon.getFatigueMod(wielder) * this.getFatigueMod() * weapon.getEnduranceMod() * 0.3F + float1)
                    * 0.04F;
                if (wielder instanceof IsoPlayer && wielder.isAimAtFloor() && ((IsoPlayer)wielder).bDoShove) {
                    float2 *= 2.0F;
                }

                float float3;
                if (weapon.isAimedFirearm()) {
                    float3 = float0 * 0.7F;
                } else {
                    float3 = float0 * 0.15F;
                }

                if (this.getHealth() < float0) {
                    float3 = this.getHealth();
                }

                float float4 = float3 / weapon.getMaxDamage();
                if (float4 > 1.0F) {
                    float4 = 1.0F;
                }

                if (this.isCloseKilled()) {
                    float4 = 0.2F;
                }

                if (weapon.isUseEndurance()) {
                    if (float0 <= 0.0F) {
                        float4 = 1.0F;
                    }

                    wielder.getStats().endurance -= float2 * float4;
                }

                this.hitConsequences(weapon, wielder, bIgnoreDamage, float0, bRemote);
                return float0;
            }
        } else {
            return 0.0F;
        }
    }

    public float processHitDamage(HandWeapon weapon, IsoGameCharacter wielder, float damageSplit, boolean bIgnoreDamage, float modDelta) {
        float float0 = damageSplit * modDelta;
        float float1 = float0;
        if (bIgnoreDamage) {
            float1 = float0 / 2.7F;
        }

        float float2 = float1 * wielder.getShovingMod();
        if (float2 > 1.0F) {
            float2 = 1.0F;
        }

        this.setHitForce(float2);
        if (wielder.Traits.Strong.isSet() && !weapon.isRanged()) {
            this.setHitForce(this.getHitForce() * 1.4F);
        }

        if (wielder.Traits.Weak.isSet() && !weapon.isRanged()) {
            this.setHitForce(this.getHitForce() * 0.6F);
        }

        float float3 = IsoUtils.DistanceTo(wielder.getX(), wielder.getY(), this.getX(), this.getY());
        float3 -= weapon.getMinRange();
        float3 /= weapon.getMaxRange(wielder);
        float3 = 1.0F - float3;
        if (float3 > 1.0F) {
            float3 = 1.0F;
        }

        float float4 = wielder.stats.endurance;
        float4 *= wielder.knockbackAttackMod;
        if (float4 < 0.5F) {
            float4 *= 1.3F;
            if (float4 < 0.4F) {
                float4 = 0.4F;
            }

            this.setHitForce(this.getHitForce() * float4);
        }

        if (!weapon.isRangeFalloff()) {
            float3 = 1.0F;
        }

        if (!weapon.isShareDamage()) {
            damageSplit = 1.0F;
        }

        if (wielder instanceof IsoPlayer && !bIgnoreDamage) {
            this.setHitForce(this.getHitForce() * 2.0F);
        }

        if (wielder instanceof IsoPlayer && !((IsoPlayer)wielder).bDoShove) {
            Vector2 vector0 = tempVector2_1.set(this.getX(), this.getY());
            Vector2 vector1 = tempVector2_2.set(wielder.getX(), wielder.getY());
            vector0.x = vector0.x - vector1.x;
            vector0.y = vector0.y - vector1.y;
            Vector2 vector2 = this.getVectorFromDirection(tempVector2_2);
            vector0.normalize();
            float float5 = vector0.dot(vector2);
            if (float5 > -0.3F) {
                float0 *= 1.5F;
            }
        }

        if (this instanceof IsoPlayer) {
            float0 *= 0.4F;
        } else {
            float0 *= 1.5F;
        }

        int int0 = wielder.getWeaponLevel();
        switch (int0) {
            case -1:
                float0 *= 0.3F;
                break;
            case 0:
                float0 *= 0.3F;
                break;
            case 1:
                float0 *= 0.4F;
                break;
            case 2:
                float0 *= 0.5F;
                break;
            case 3:
                float0 *= 0.6F;
                break;
            case 4:
                float0 *= 0.7F;
                break;
            case 5:
                float0 *= 0.8F;
                break;
            case 6:
                float0 *= 0.9F;
                break;
            case 7:
                float0 *= 1.0F;
                break;
            case 8:
                float0 *= 1.1F;
                break;
            case 9:
                float0 *= 1.2F;
                break;
            case 10:
                float0 *= 1.3F;
        }

        if (wielder instanceof IsoPlayer && wielder.isAimAtFloor() && !bIgnoreDamage && !((IsoPlayer)wielder).bDoShove) {
            float0 *= Math.max(5.0F, weapon.getCritDmgMultiplier());
        }

        if (wielder.isCriticalHit() && !bIgnoreDamage) {
            float0 *= Math.max(2.0F, weapon.getCritDmgMultiplier());
        }

        if (weapon.isTwoHandWeapon() && !wielder.isItemInBothHands(weapon)) {
            float0 *= 0.5F;
        }

        return float0;
    }

    public void hitConsequences(HandWeapon weapon, IsoGameCharacter wielder, boolean bIgnoreDamage, float damage, boolean bRemote) {
        if (!bIgnoreDamage) {
            if (weapon.isAimedFirearm()) {
                this.Health -= damage * 0.7F;
            } else {
                this.Health -= damage * 0.15F;
            }
        }

        if (this.isDead()) {
            if (!this.isOnKillDone() && this.shouldDoInventory()) {
                this.Kill(wielder);
            }

            if (this instanceof IsoZombie && ((IsoZombie)this).upKillCount) {
                wielder.setZombieKills(wielder.getZombieKills() + 1);
            }
        } else {
            if (weapon.isSplatBloodOnNoDeath()) {
                this.splatBlood(2, 0.2F);
            }

            if (weapon.isKnockBackOnNoDeath() && wielder.xp != null) {
                wielder.xp.AddXP(PerkFactory.Perks.Strength, 2.0F);
            }
        }
    }

    public boolean IsAttackRange(float x, float y, float z) {
        float float0 = 1.0F;
        float float1 = 0.0F;
        if (this.leftHandItem != null) {
            InventoryItem item = this.leftHandItem;
            if (item instanceof HandWeapon) {
                float0 = ((HandWeapon)item).getMaxRange(this);
                float1 = ((HandWeapon)item).getMinRange();
                float0 *= ((HandWeapon)this.leftHandItem).getRangeMod(this);
            }
        }

        if (Math.abs(z - this.getZ()) > 0.3F) {
            return false;
        } else {
            float float2 = IsoUtils.DistanceTo(x, y, this.getX(), this.getY());
            return float2 < float0 && float2 > float1;
        }
    }

    /**
     * This use some prediction on the zombie, if he's lunging toward the player attacking it we gonna add more range to our weapon, to avoid playing the "miss" animation
     */
    public boolean IsAttackRange(HandWeapon we, IsoMovingObject obj, Vector3 bonePos, boolean extraRange) {
        if (we == null) {
            return false;
        } else {
            float float0 = Math.abs(obj.getZ() - this.getZ());
            if (!we.isRanged() && float0 >= 0.5F) {
                return false;
            } else if (float0 > 3.3F) {
                return false;
            } else {
                float float1 = we.getMaxRange(this);
                float1 *= we.getRangeMod(this);
                float float2 = IsoUtils.DistanceToSquared(this.x, this.y, bonePos.x, bonePos.y);
                if (extraRange) {
                    IsoZombie zombie0 = Type.tryCastTo(obj, IsoZombie.class);
                    if (zombie0 != null
                        && float2 < 4.0F
                        && zombie0.target == this
                        && (zombie0.isCurrentState(LungeState.instance()) || zombie0.isCurrentState(LungeNetworkState.instance()))) {
                        float1++;
                    }
                }

                return float2 < float1 * float1;
            }
        }
    }

    @Override
    public boolean IsSpeaking() {
        return this.chatElement.IsSpeaking();
    }

    public void MoveForward(float dist, float x, float y, float soundDelta) {
        if (!this.isCurrentState(SwipeStatePlayer.instance())) {
            float float0 = GameTime.instance.getMultiplier();
            this.setNx(this.getNx() + x * dist * float0);
            this.setNy(this.getNy() + y * dist * float0);
            this.DoFootstepSound(dist);
            if (!this.isZombie()) {
            }
        }
    }

    private void pathToAux(float float0, float float1, float float2) {
        boolean boolean0 = true;
        if ((int)float2 == (int)this.getZ() && IsoUtils.DistanceManhatten(float0, float1, this.x, this.y) <= 30.0F) {
            int int0 = (int)float0 / 10;
            int int1 = (int)float1 / 10;
            IsoChunk chunk = GameServer.bServer
                ? ServerMap.instance.getChunk(int0, int1)
                : IsoWorld.instance.CurrentCell.getChunkForGridSquare((int)float0, (int)float1, (int)float2);
            if (chunk != null) {
                int int2 = 1;
                int2 |= 2;
                if (!this.isZombie()) {
                    int2 |= 4;
                }

                boolean0 = !PolygonalMap2.instance
                    .lineClearCollide(this.getX(), this.getY(), float0, float1, (int)float2, this.getPathFindBehavior2().getTargetChar(), int2);
            }
        }

        if (boolean0 && this.current != null && this.current.HasStairs() && !this.current.isSameStaircase((int)float0, (int)float1, (int)float2)) {
            boolean0 = false;
        }

        if (boolean0) {
            this.setVariable("bPathfind", false);
            this.setMoving(true);
        } else {
            this.setVariable("bPathfind", true);
            this.setMoving(false);
        }
    }

    public void pathToCharacter(IsoGameCharacter target) {
        this.getPathFindBehavior2().pathToCharacter(target);
        this.pathToAux(target.getX(), target.getY(), target.getZ());
    }

    @Override
    public void pathToLocation(int x, int y, int z) {
        this.getPathFindBehavior2().pathToLocation(x, y, z);
        this.pathToAux(x + 0.5F, y + 0.5F, z);
    }

    @Override
    public void pathToLocationF(float x, float y, float z) {
        this.getPathFindBehavior2().pathToLocationF(x, y, z);
        this.pathToAux(x, y, z);
    }

    public void pathToSound(int x, int y, int z) {
        this.getPathFindBehavior2().pathToSound(x, y, z);
        this.pathToAux(x + 0.5F, y + 0.5F, z);
    }

    @Override
    public boolean CanAttack() {
        if (!this.isAttackAnim()
            && !this.getVariableBoolean("IsRacking")
            && !this.getVariableBoolean("IsUnloading")
            && StringUtils.isNullOrEmpty(this.getVariableString("RackWeapon"))) {
            if (!GameClient.bClient
                || !(this instanceof IsoPlayer)
                || !((IsoPlayer)this).isLocalPlayer()
                || !this.isCurrentState(PlayerHitReactionState.instance()) && !this.isCurrentState(PlayerHitReactionPVPState.instance())) {
                if (this.isSitOnGround()) {
                    return false;
                } else {
                    InventoryItem item = this.leftHandItem;
                    if (item instanceof HandWeapon && item.getSwingAnim() != null) {
                        this.useHandWeapon = (HandWeapon)item;
                    }

                    if (this.useHandWeapon == null) {
                        return true;
                    } else if (this.useHandWeapon.getCondition() <= 0) {
                        this.useHandWeapon = null;
                        if (this.rightHandItem == this.leftHandItem) {
                            this.setSecondaryHandItem(null);
                        }

                        this.setPrimaryHandItem(null);
                        if (this.getInventory() != null) {
                            this.getInventory().setDrawDirty(true);
                        }

                        return false;
                    } else {
                        float float0 = 12.0F;
                        int int0 = this.Moodles.getMoodleLevel(MoodleType.Endurance);
                        return !this.useHandWeapon.isCantAttackWithLowestEndurance() || int0 != 4;
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void ReduceHealthWhenBurning() {
        if (this.OnFire) {
            if (this.isGodMod()) {
                this.StopBurning();
            } else if (!GameClient.bClient || !this.isZombie() || !(this instanceof IsoZombie) || !((IsoZombie)this).isRemoteZombie()) {
                if (!GameClient.bClient || !(this instanceof IsoPlayer) || !((IsoPlayer)this).bRemote) {
                    if (this.isAlive()) {
                        if (!(this instanceof IsoPlayer)) {
                            if (this.isZombie()) {
                                this.Health = this.Health - this.FireKillRate / 20.0F * GameTime.instance.getMultiplier();
                                this.setAttackedBy(null);
                            } else {
                                this.Health = this.Health - this.FireKillRate * GameTime.instance.getMultiplier();
                            }
                        } else {
                            float float0 = this.FireKillRate * GameTime.instance.getMultiplier() * GameTime.instance.getMinutesPerDay() / 1.6F;
                            this.BodyDamage.ReduceGeneralHealth(float0);
                            LuaEventManager.triggerEvent("OnPlayerGetDamage", this, "FIRE", float0);
                            this.BodyDamage.OnFire(true);
                        }

                        if (this.isDead()) {
                            IsoFireManager.RemoveBurningCharacter(this);
                            if (this.isZombie()) {
                                LuaEventManager.triggerEvent("OnZombieDead", this);
                                if (GameClient.bClient) {
                                    this.setAttackedBy(IsoWorld.instance.CurrentCell.getFakeZombieForHit());
                                }
                            }
                        }
                    }

                    if (this instanceof IsoPlayer && Rand.Next(Rand.AdjustForFramerate(((IsoPlayer)this).IsRunning() ? 150 : 400)) == 0) {
                        this.StopBurning();
                    }
                }
            }
        }
    }

    public void DrawSneezeText() {
        if (this.BodyDamage.IsSneezingCoughing() > 0) {
            String string = null;
            if (this.BodyDamage.IsSneezingCoughing() == 1) {
                string = Translator.getText("IGUI_PlayerText_Sneeze");
            }

            if (this.BodyDamage.IsSneezingCoughing() == 2) {
                string = Translator.getText("IGUI_PlayerText_Cough");
            }

            if (this.BodyDamage.IsSneezingCoughing() == 3) {
                string = Translator.getText("IGUI_PlayerText_SneezeMuffled");
            }

            if (this.BodyDamage.IsSneezingCoughing() == 4) {
                string = Translator.getText("IGUI_PlayerText_CoughMuffled");
            }

            float float0 = this.sx;
            float float1 = this.sy;
            float0 = (int)float0;
            float1 = (int)float1;
            float0 -= (int)IsoCamera.getOffX();
            float1 -= (int)IsoCamera.getOffY();
            float1 -= 48.0F;
            if (string != null) {
                TextManager.instance
                    .DrawStringCentre(
                        UIFont.Dialogue, (int)float0, (int)float1, string, this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, this.SpeakColour.a
                    );
            }
        }
    }

    @Override
    public IsoSpriteInstance getSpriteDef() {
        if (this.def == null) {
            this.def = new IsoSpriteInstance();
        }

        return this.def;
    }

    @Override
    public void render(float x, float y, float z, ColorInfo col, boolean bDoChild, boolean bWallLightingPass, Shader shader) {
        if (!this.isAlphaAndTargetZero()) {
            if (!this.isSeatedInVehicle() || this.getVehicle().showPassenger(this)) {
                if (!this.isSpriteInvisible()) {
                    if (!this.isAlphaZero()) {
                        if (!this.bUseParts && this.def == null) {
                            this.def = new IsoSpriteInstance(this.sprite);
                        }

                        SpriteRenderer.instance.glDepthMask(true);
                        if (this.bDoDefer && z - (int)z > 0.2F) {
                            IsoGridSquare square0 = this.getCell().getGridSquare((int)x, (int)y, (int)z + 1);
                            if (square0 != null) {
                                square0.addDeferredCharacter(this);
                            }
                        }

                        IsoGridSquare square1 = this.getCurrentSquare();
                        if (PerformanceSettings.LightingFrameSkip < 3 && square1 != null) {
                            square1.interpolateLight(inf, x - square1.getX(), y - square1.getY());
                        } else {
                            inf.r = col.r;
                            inf.g = col.g;
                            inf.b = col.b;
                            inf.a = col.a;
                        }

                        if (Core.bDebug && DebugOptions.instance.PathfindRenderWaiting.getValue() && this.hasActiveModel()) {
                            if (this.getCurrentState() == PathFindState.instance() && this.finder.progress == AStarPathFinder.PathFindProgress.notyetfound) {
                                this.legsSprite.modelSlot.model.tintR = 1.0F;
                                this.legsSprite.modelSlot.model.tintG = 0.0F;
                                this.legsSprite.modelSlot.model.tintB = 0.0F;
                            } else {
                                this.legsSprite.modelSlot.model.tintR = 1.0F;
                                this.legsSprite.modelSlot.model.tintG = 1.0F;
                                this.legsSprite.modelSlot.model.tintB = 1.0F;
                            }
                        }

                        if (this.dir == IsoDirections.Max) {
                            this.dir = IsoDirections.N;
                        }

                        if (this.sprite != null && !this.legsSprite.hasActiveModel()) {
                            this.checkDrawWeaponPre(x, y, z, col);
                        }

                        lastRenderedRendered = lastRendered;
                        lastRendered = this;
                        this.checkUpdateModelTextures();
                        float float0 = Core.TileScale;
                        float float1 = this.offsetX + 1.0F * float0;
                        float float2 = this.offsetY + -89.0F * float0;
                        if (this.sprite != null) {
                            this.def.setScale(float0, float0);
                            if (!this.bUseParts) {
                                this.sprite.render(this.def, this, x, y, z, this.dir, float1, float2, inf, true);
                            } else if (this.legsSprite.hasActiveModel()) {
                                this.legsSprite.renderActiveModel();
                            } else if (!this.renderTextureInsteadOfModel(x, y)) {
                                this.def.Flip = false;
                                inf.r = 1.0F;
                                inf.g = 1.0F;
                                inf.b = 1.0F;
                                inf.a = this.def.alpha * 0.4F;
                                this.legsSprite.renderCurrentAnim(this.def, this, x, y, z, this.dir, float1, float2, inf, false, null);
                            }
                        }

                        if (this.AttachedAnimSprite != null) {
                            for (int int0 = 0; int0 < this.AttachedAnimSprite.size(); int0++) {
                                IsoSpriteInstance spriteInstance = this.AttachedAnimSprite.get(int0);
                                spriteInstance.update();
                                float float3 = inf.a;
                                inf.a = spriteInstance.alpha;
                                spriteInstance.SetTargetAlpha(this.getTargetAlpha());
                                spriteInstance.render(this, x, y, z, this.dir, float1, float2, inf);
                                inf.a = float3;
                            }
                        }

                        for (int int1 = 0; int1 < this.inventory.Items.size(); int1++) {
                            InventoryItem item = this.inventory.Items.get(int1);
                            if (item instanceof IUpdater) {
                                ((IUpdater)item).render();
                            }
                        }
                    }
                }
            }
        }
    }

    public void renderServerGUI() {
        if (this instanceof IsoPlayer) {
            this.setSceneCulled(false);
        }

        if (this.bUpdateModelTextures && this.hasActiveModel()) {
            this.bUpdateModelTextures = false;
            this.textureCreator = ModelInstanceTextureCreator.alloc();
            this.textureCreator.init(this);
        }

        float float0 = Core.TileScale;
        float float1 = this.offsetX + 1.0F * float0;
        float float2 = this.offsetY + -89.0F * float0;
        if (this.sprite != null) {
            this.def.setScale(float0, float0);
            inf.r = 1.0F;
            inf.g = 1.0F;
            inf.b = 1.0F;
            inf.a = this.def.alpha * 0.4F;
            if (!this.isbUseParts()) {
                this.sprite.render(this.def, this, this.x, this.y, this.z, this.dir, float1, float2, inf, true);
            } else {
                this.def.Flip = false;
                this.legsSprite.render(this.def, this, this.x, this.y, this.z, this.dir, float1, float2, inf, true);
            }
        }

        if (Core.bDebug && this.hasActiveModel()) {
            if (this instanceof IsoZombie) {
                int int0 = (int)IsoUtils.XToScreenExact(this.x, this.y, this.z, 0);
                int int1 = (int)IsoUtils.YToScreenExact(this.x, this.y, this.z, 0);
                TextManager.instance.DrawString(int0, int1, "ID: " + this.getOnlineID());
                TextManager.instance.DrawString(int0, int1 + 10, "State: " + this.getCurrentStateName());
                TextManager.instance.DrawString(int0, int1 + 20, "Health: " + this.getHealth());
            }

            Vector2 vector = tempo;
            this.getDeferredMovement(vector);
            this.drawDirectionLine(vector, 1000.0F * vector.getLength() / GameTime.instance.getMultiplier() * 2.0F, 1.0F, 0.5F, 0.5F);
        }
    }

    @Override
    protected float getAlphaUpdateRateMul() {
        float float0 = super.getAlphaUpdateRateMul();
        if (IsoCamera.CamCharacter.Traits.ShortSighted.isSet()) {
            float0 /= 2.0F;
        }

        if (IsoCamera.CamCharacter.Traits.EagleEyed.isSet()) {
            float0 *= 1.5F;
        }

        return float0;
    }

    @Override
    protected boolean isUpdateAlphaEnabled() {
        return !this.isTeleporting();
    }

    @Override
    protected boolean isUpdateAlphaDuringRender() {
        return false;
    }

    public boolean isSeatedInVehicle() {
        return this.vehicle != null && this.vehicle.getSeat(this) != -1;
    }

    @Override
    public void renderObjectPicker(float x, float y, float z, ColorInfo _lightInfo) {
        if (!this.bUseParts) {
            this.sprite.renderObjectPicker(this.def, this, this.dir);
        } else {
            this.legsSprite.renderObjectPicker(this.def, this, this.dir);
        }
    }

    static Vector2 closestpointonline(double double4, double double2, double double5, double double1, double double9, double double8, Vector2 vector) {
        double double0 = double1 - double2;
        double double3 = double4 - double5;
        double double6 = (double1 - double2) * double4 + (double4 - double5) * double2;
        double double7 = -double3 * double9 + double0 * double8;
        double double10 = double0 * double0 - -double3 * double3;
        double double11;
        double double12;
        if (double10 != 0.0) {
            double11 = (double0 * double6 - double3 * double7) / double10;
            double12 = (double0 * double7 - -double3 * double6) / double10;
        } else {
            double11 = double9;
            double12 = double8;
        }

        return vector.set((float)double11, (float)double12);
    }

    public void renderShadow(float x, float y, float z) {
        if (this.doRenderShadow) {
            if (!this.isAlphaAndTargetZero()) {
                if (!this.isSeatedInVehicle()) {
                    IsoGridSquare square = this.getCurrentSquare();
                    if (square != null) {
                        int int0 = IsoCamera.frameState.playerIndex;
                        Vector3f vector3f0 = IsoGameCharacter.L_renderShadow.forward;
                        Vector2 vector0 = this.getAnimVector(tempo2);
                        vector3f0.set(vector0.x, vector0.y, 0.0F);
                        float float0 = 0.45F;
                        float float1 = 1.4F;
                        float float2 = 1.125F;
                        float float3 = this.getAlpha(int0);
                        if (this.hasActiveModel() && this.hasAnimationPlayer() && this.getAnimationPlayer().isReady()) {
                            AnimationPlayer animationPlayer = this.getAnimationPlayer();
                            Vector3 vector1 = IsoGameCharacter.L_renderShadow.v1;
                            Model.BoneToWorldCoords(this, animationPlayer.getSkinningBoneIndex("Bip01_Head", -1), vector1);
                            float float4 = vector1.x;
                            float float5 = vector1.y;
                            Model.BoneToWorldCoords(this, animationPlayer.getSkinningBoneIndex("Bip01_L_Foot", -1), vector1);
                            float float6 = vector1.x;
                            float float7 = vector1.y;
                            Model.BoneToWorldCoords(this, animationPlayer.getSkinningBoneIndex("Bip01_R_Foot", -1), vector1);
                            float float8 = vector1.x;
                            float float9 = vector1.y;
                            Vector3f vector3f1 = IsoGameCharacter.L_renderShadow.v3;
                            float float10 = 0.0F;
                            float float11 = 0.0F;
                            Vector2 vector2 = closestpointonline(x, y, x + vector3f0.x, y + vector3f0.y, float4, float5, tempo);
                            float float12 = vector2.x;
                            float float13 = vector2.y;
                            float float14 = vector2.set(float12 - x, float13 - y).getLength();
                            if (float14 > 0.001F) {
                                vector3f1.set(float12 - x, float13 - y, 0.0F).normalize();
                                if (vector3f0.dot(vector3f1) > 0.0F) {
                                    float10 = Math.max(float10, float14);
                                } else {
                                    float11 = Math.max(float11, float14);
                                }
                            }

                            vector2 = closestpointonline(x, y, x + vector3f0.x, y + vector3f0.y, float6, float7, tempo);
                            float12 = vector2.x;
                            float13 = vector2.y;
                            float14 = vector2.set(float12 - x, float13 - y).getLength();
                            if (float14 > 0.001F) {
                                vector3f1.set(float12 - x, float13 - y, 0.0F).normalize();
                                if (vector3f0.dot(vector3f1) > 0.0F) {
                                    float10 = Math.max(float10, float14);
                                } else {
                                    float11 = Math.max(float11, float14);
                                }
                            }

                            vector2 = closestpointonline(x, y, x + vector3f0.x, y + vector3f0.y, float8, float9, tempo);
                            float12 = vector2.x;
                            float13 = vector2.y;
                            float14 = vector2.set(float12 - x, float13 - y).getLength();
                            if (float14 > 0.001F) {
                                vector3f1.set(float12 - x, float13 - y, 0.0F).normalize();
                                if (vector3f0.dot(vector3f1) > 0.0F) {
                                    float10 = Math.max(float10, float14);
                                } else {
                                    float11 = Math.max(float11, float14);
                                }
                            }

                            float1 = (float10 + 0.35F) * 1.35F;
                            float2 = (float11 + 0.35F) * 1.35F;
                            float float15 = 0.1F * (GameTime.getInstance().getMultiplier() / 1.6F);
                            float15 = PZMath.clamp(float15, 0.0F, 1.0F);
                            if (this.shadowTick != IngameState.instance.numberTicks - 1L) {
                                this.m_shadowFM = float1;
                                this.m_shadowBM = float2;
                            }

                            this.shadowTick = IngameState.instance.numberTicks;
                            this.m_shadowFM = PZMath.lerp(this.m_shadowFM, float1, float15);
                            float1 = this.m_shadowFM;
                            this.m_shadowBM = PZMath.lerp(this.m_shadowBM, float2, float15);
                            float2 = this.m_shadowBM;
                        } else if (this.isZombie() && this.isCurrentState(FakeDeadZombieState.instance())) {
                            float3 = 1.0F;
                        } else if (this.isSceneCulled()) {
                            return;
                        }

                        ColorInfo colorInfo = square.lighting[int0].lightInfo();
                        IsoDeadBody.renderShadow(x, y, z, vector3f0, float0, float1, float2, colorInfo, float3);
                    }
                }
            }
        }
    }

    public void checkUpdateModelTextures() {
        if (this.bUpdateModelTextures && this.hasActiveModel()) {
            this.bUpdateModelTextures = false;
            this.textureCreator = ModelInstanceTextureCreator.alloc();
            this.textureCreator.init(this);
        }

        if (this.bUpdateEquippedTextures && this.hasActiveModel()) {
            this.bUpdateEquippedTextures = false;
            if (this.primaryHandModel != null && this.primaryHandModel.getTextureInitializer() != null) {
                this.primaryHandModel.getTextureInitializer().setDirty();
            }

            if (this.secondaryHandModel != null && this.secondaryHandModel.getTextureInitializer() != null) {
                this.secondaryHandModel.getTextureInitializer().setDirty();
            }
        }
    }

    @Override
    public boolean isMaskClicked(int x, int y, boolean flip) {
        if (this.sprite == null) {
            return false;
        } else {
            return !this.bUseParts ? super.isMaskClicked(x, y, flip) : this.legsSprite.isMaskClicked(this.dir, x, y, flip);
        }
    }

    @Override
    public void setHaloNote(String str) {
        this.setHaloNote(str, this.haloDispTime);
    }

    @Override
    public void setHaloNote(String str, float dispTime) {
        this.setHaloNote(str, 0, 255, 0, dispTime);
    }

    @Override
    public void setHaloNote(String str, int r, int g, int b, float dispTime) {
        if (this.haloNote != null && str != null) {
            this.haloDispTime = dispTime;
            this.haloNote.setDefaultColors(r, g, b);
            this.haloNote.ReadString(str);
            this.haloNote.setInternalTickClock(this.haloDispTime);
        }
    }

    public float getHaloTimerCount() {
        return this.haloNote != null ? this.haloNote.getInternalClock() : 0.0F;
    }

    public void DoSneezeText() {
        if (this.BodyDamage != null) {
            if (this.BodyDamage.IsSneezingCoughing() > 0) {
                String string = null;
                int int0 = 0;
                if (this.BodyDamage.IsSneezingCoughing() == 1) {
                    string = Translator.getText("IGUI_PlayerText_Sneeze");
                    int0 = Rand.Next(2) + 1;
                    this.setVariable("Ext", "Sneeze" + int0);
                }

                if (this.BodyDamage.IsSneezingCoughing() == 2) {
                    string = Translator.getText("IGUI_PlayerText_Cough");
                    this.setVariable("Ext", "Cough");
                }

                if (this.BodyDamage.IsSneezingCoughing() == 3) {
                    string = Translator.getText("IGUI_PlayerText_SneezeMuffled");
                    int0 = Rand.Next(2) + 1;
                    this.setVariable("Ext", "Sneeze" + int0);
                }

                if (this.BodyDamage.IsSneezingCoughing() == 4) {
                    string = Translator.getText("IGUI_PlayerText_CoughMuffled");
                    this.setVariable("Ext", "Cough");
                }

                if (string != null) {
                    this.Say(string);
                    this.reportEvent("EventDoExt");
                    if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
                        GameClient.sendSneezingCoughing(this.getOnlineID(), this.BodyDamage.IsSneezingCoughing(), (byte)int0);
                    }
                }
            }
        }
    }

    @Override
    public String getSayLine() {
        return this.chatElement.getSayLine();
    }

    /**
     * 
     * @param sayLine the sayLine to set
     */
    public void setSayLine(String sayLine) {
        this.Say(sayLine);
    }

    public ChatMessage getLastChatMessage() {
        return this.lastChatMessage;
    }

    public void setLastChatMessage(ChatMessage _lastChatMessage) {
        this.lastChatMessage = _lastChatMessage;
    }

    public String getLastSpokenLine() {
        return this.lastSpokenLine;
    }

    public void setLastSpokenLine(String line) {
        this.lastSpokenLine = line;
    }

    protected void doSleepSpeech() {
        this.sleepSpeechCnt++;
        if (this.sleepSpeechCnt > 250 * PerformanceSettings.getLockFPS() / 30.0F) {
            this.sleepSpeechCnt = 0;
            if (sleepText == null) {
                sleepText = "ZzzZZZzzzz";
                ChatElement.addNoLogText(sleepText);
            }

            this.SayWhisper(sleepText);
        }
    }

    public void SayDebug(String text) {
        this.chatElement.SayDebug(0, text);
    }

    public void SayDebug(int n, String text) {
        this.chatElement.SayDebug(n, text);
    }

    public int getMaxChatLines() {
        return this.chatElement.getMaxChatLines();
    }

    @Override
    public void Say(String line) {
        if (!this.isZombie()) {
            this.ProcessSay(line, this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, 30.0F, 0, "default");
        }
    }

    @Override
    public void Say(String line, float r, float g, float b, UIFont font, float baseRange, String customTag) {
        this.ProcessSay(line, r, g, b, baseRange, 0, customTag);
    }

    public void SayWhisper(String line) {
        this.ProcessSay(line, this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, 10.0F, 0, "whisper");
    }

    public void SayShout(String line) {
        this.ProcessSay(line, this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, 60.0F, 0, "shout");
    }

    public void SayRadio(String line, float r, float g, float b, UIFont font, float baseRange, int channel, String customTag) {
        this.ProcessSay(line, r, g, b, baseRange, channel, customTag);
    }

    private void ProcessSay(String string0, float float0, float float1, float float2, float float3, int int0, String string1) {
        if (this.AllowConversation) {
            if (TutorialManager.instance.ProfanityFilter) {
                string0 = ProfanityFilter.getInstance().filterString(string0);
            }

            if (string1.equals("default")) {
                ChatManager.getInstance().showInfoMessage(((IsoPlayer)this).getUsername(), string0);
                this.lastSpokenLine = string0;
            } else if (string1.equals("whisper")) {
                this.lastSpokenLine = string0;
            } else if (string1.equals("shout")) {
                ChatManager.getInstance().sendMessageToChat(((IsoPlayer)this).getUsername(), ChatType.shout, string0);
                this.lastSpokenLine = string0;
            } else if (string1.equals("radio")) {
                UIFont uIFont = UIFont.Medium;
                boolean boolean0 = true;
                boolean boolean1 = true;
                boolean boolean2 = true;
                boolean boolean3 = false;
                boolean boolean4 = false;
                boolean boolean5 = true;
                this.chatElement
                    .addChatLine(string0, float0, float1, float2, uIFont, float3, string1, boolean0, boolean1, boolean2, boolean3, boolean4, boolean5);
                if (ZomboidRadio.isStaticSound(string0)) {
                    ChatManager.getInstance().showStaticRadioSound(string0);
                } else {
                    ChatManager.getInstance().showRadioMessage(string0, int0);
                }
            }
        }
    }

    public void addLineChatElement(String line) {
        this.addLineChatElement(line, 1.0F, 1.0F, 1.0F);
    }

    public void addLineChatElement(String line, float r, float g, float b) {
        this.addLineChatElement(line, r, g, b, UIFont.Dialogue, 30.0F, "default");
    }

    public void addLineChatElement(String line, float r, float g, float b, UIFont font, float baseRange, String customTag) {
        this.addLineChatElement(line, r, g, b, font, baseRange, customTag, false, false, false, false, false, true);
    }

    public void addLineChatElement(
        String line,
        float r,
        float g,
        float b,
        UIFont font,
        float baseRange,
        String customTag,
        boolean bbcode,
        boolean img,
        boolean icons,
        boolean colors,
        boolean fonts,
        boolean equalizeHeights
    ) {
        this.chatElement.addChatLine(line, r, g, b, font, baseRange, customTag, bbcode, img, icons, colors, fonts, equalizeHeights);
    }

    protected boolean playerIsSelf() {
        return IsoPlayer.getInstance() == this;
    }

    public int getUserNameHeight() {
        if (!GameClient.bClient) {
            return 0;
        } else {
            return this.userName != null ? this.userName.getHeight() : 0;
        }
    }

    protected void initTextObjects() {
        this.hasInitTextObjects = true;
        if (this instanceof IsoPlayer) {
            this.chatElement.setMaxChatLines(5);
            if (IsoPlayer.getInstance() != null) {
                System.out.println("FirstNAME:" + IsoPlayer.getInstance().username);
            }

            this.isoPlayer = (IsoPlayer)this;
            if (this.isoPlayer.username != null) {
                this.userName = new TextDrawObject();
                this.userName.setAllowAnyImage(true);
                this.userName.setDefaultFont(UIFont.Small);
                this.userName.setDefaultColors(255, 255, 255, 255);
                this.updateUserName();
            }

            if (this.haloNote == null) {
                this.haloNote = new TextDrawObject();
                this.haloNote.setDefaultFont(UIFont.Small);
                this.haloNote.setDefaultColors(0, 255, 0);
                this.haloNote.setDrawBackground(true);
                this.haloNote.setAllowImages(true);
                this.haloNote.setAllowAnyImage(true);
                this.haloNote.setOutlineColors(0.0F, 0.0F, 0.0F, 0.33F);
            }
        }
    }

    protected void updateUserName() {
        if (this.userName != null && this.isoPlayer != null) {
            String string = this.isoPlayer.getUsername(true);
            if (this != IsoPlayer.getInstance()
                && this.isInvisible()
                && IsoPlayer.getInstance() != null
                && IsoPlayer.getInstance().accessLevel.equals("")
                && (!Core.bDebug || !DebugOptions.instance.CheatPlayerSeeEveryone.getValue())) {
                this.userName.ReadString("");
                return;
            }

            Faction faction = Faction.getPlayerFaction(this.isoPlayer);
            if (faction != null) {
                if (!this.isoPlayer.showTag && this.isoPlayer != IsoPlayer.getInstance() && Faction.getPlayerFaction(IsoPlayer.getInstance()) != faction) {
                    this.isoPlayer.tagPrefix = "";
                } else {
                    this.isoPlayer.tagPrefix = faction.getTag();
                    if (faction.getTagColor() != null) {
                        this.isoPlayer.setTagColor(faction.getTagColor());
                    }
                }
            } else {
                this.isoPlayer.tagPrefix = "";
            }

            boolean boolean0 = this.isoPlayer != null && this.isoPlayer.bRemote || Core.getInstance().isShowYourUsername();
            if (IsoCamera.CamCharacter instanceof IsoPlayer && !((IsoPlayer)IsoCamera.CamCharacter).accessLevel.equals("")) {
                boolean boolean1 = true;
            } else {
                boolean boolean2 = false;
            }

            boolean boolean3 = IsoCamera.CamCharacter instanceof IsoPlayer && ((IsoPlayer)IsoCamera.CamCharacter).canSeeAll;
            if (!ServerOptions.instance.DisplayUserName.getValue() && !ServerOptions.instance.ShowFirstAndLastName.getValue() && !boolean3) {
                boolean0 = false;
            }

            if (!boolean0) {
                string = "";
            }

            if (boolean0 && this.isoPlayer.tagPrefix != null && !this.isoPlayer.tagPrefix.equals("")) {
                string = "[col="
                    + new Float(this.isoPlayer.getTagColor().r * 255.0F).intValue()
                    + ","
                    + new Float(this.isoPlayer.getTagColor().g * 255.0F).intValue()
                    + ","
                    + new Float(this.isoPlayer.getTagColor().b * 255.0F).intValue()
                    + "]["
                    + this.isoPlayer.tagPrefix
                    + "][/] "
                    + string;
            }

            if (boolean0 && !this.isoPlayer.accessLevel.equals("") && this.isoPlayer.isShowAdminTag()) {
                string = this.namesPrefix.get(this.isoPlayer.accessLevel) + string;
            }

            if (boolean0
                && !this.isoPlayer.getSafety().isEnabled()
                && ServerOptions.instance.ShowSafety.getValue()
                && NonPvpZone.getNonPvpZone(PZMath.fastfloor(this.isoPlayer.x), PZMath.fastfloor(this.isoPlayer.y)) == null) {
                string = string + " [img=media/ui/Skull.png]";
            }

            if (this.isoPlayer.isSpeek && !this.isoPlayer.isVoiceMute) {
                string = "[img=media/ui/voiceon.png] " + string;
            }

            if (this.isoPlayer.isVoiceMute) {
                string = "[img=media/ui/voicemuted.png] " + string;
            }

            BaseVehicle vehiclex = IsoCamera.CamCharacter == this.isoPlayer ? this.isoPlayer.getNearVehicle() : null;
            if (this.getVehicle() == null
                && vehiclex != null
                && (
                    this.isoPlayer.getInventory().haveThisKeyId(vehiclex.getKeyId()) != null
                        || vehiclex.isHotwired()
                        || SandboxOptions.getInstance().VehicleEasyUse.getValue()
                )) {
                Color color = Color.HSBtoRGB(vehiclex.colorHue, vehiclex.colorSaturation * 0.5F, vehiclex.colorValue);
                string = " [img=media/ui/CarKey.png," + color.getRedByte() + "," + color.getGreenByte() + "," + color.getBlueByte() + "]" + string;
            }

            if (!string.equals(this.userName.getOriginal())) {
                this.userName.ReadString(string);
            }
        }
    }

    public void updateTextObjects() {
        if (!GameServer.bServer) {
            if (!this.hasInitTextObjects) {
                this.initTextObjects();
            }

            if (!this.Speaking) {
                this.DoSneezeText();
                if (this.isAsleep() && this.getCurrentSquare() != null && this.getCurrentSquare().getCanSee(0)) {
                    this.doSleepSpeech();
                }
            }

            if (this.isoPlayer != null) {
                this.radioEquipedCheck();
            }

            this.Speaking = false;
            this.drawUserName = false;
            this.canSeeCurrent = false;
            if (this.haloNote != null && this.haloNote.getInternalClock() > 0.0F) {
                this.haloNote.updateInternalTickClock();
            }

            this.legsSprite.PlayAnim("ZombieWalk1");
            this.chatElement.update();
            this.Speaking = this.chatElement.IsSpeaking();
            if (!this.Speaking || this.isDead()) {
                this.Speaking = false;
                this.callOut = false;
            }
        }
    }

    @Override
    public void renderlast() {
        super.renderlast();
        int int0 = IsoCamera.frameState.playerIndex;
        float float0 = this.x;
        float float1 = this.y;
        if (this.sx == 0.0F && this.def != null) {
            this.sx = IsoUtils.XToScreen(float0 + this.def.offX, float1 + this.def.offY, this.z + this.def.offZ, 0);
            this.sy = IsoUtils.YToScreen(float0 + this.def.offX, float1 + this.def.offY, this.z + this.def.offZ, 0);
            this.sx = this.sx - (this.offsetX - 8.0F);
            this.sy = this.sy - (this.offsetY - 60.0F);
        }

        if (this.hasInitTextObjects && this.isoPlayer != null || this.chatElement.getHasChatToDisplay()) {
            float float2 = IsoUtils.XToScreen(float0, float1, this.getZ(), 0);
            float float3 = IsoUtils.YToScreen(float0, float1, this.getZ(), 0);
            float2 = float2 - IsoCamera.getOffX() - this.offsetX;
            float3 = float3 - IsoCamera.getOffY() - this.offsetY;
            float3 -= 128 / (2 / Core.TileScale);
            float float4 = Core.getInstance().getZoom(int0);
            float2 /= float4;
            float3 /= float4;
            this.canSeeCurrent = true;
            this.drawUserName = false;
            if (this.isoPlayer != null
                    && (this == IsoCamera.frameState.CamCharacter || this.getCurrentSquare() != null && this.getCurrentSquare().getCanSee(int0))
                || IsoPlayer.getInstance().isCanSeeAll()) {
                if (this == IsoPlayer.getInstance()) {
                    this.canSeeCurrent = true;
                }

                if (GameClient.bClient && this.userName != null && this.doRenderShadow) {
                    this.drawUserName = false;
                    if (ServerOptions.getInstance().MouseOverToSeeDisplayName.getValue()
                        && this != IsoPlayer.getInstance()
                        && !IsoPlayer.getInstance().isCanSeeAll()) {
                        IsoObjectPicker.ClickObject clickObject = IsoObjectPicker.Instance.ContextPick(Mouse.getXA(), Mouse.getYA());
                        if (clickObject != null && clickObject.tile != null) {
                            for (int int1 = clickObject.tile.square.getX() - 1; int1 < clickObject.tile.square.getX() + 2; int1++) {
                                for (int int2 = clickObject.tile.square.getY() - 1; int2 < clickObject.tile.square.getY() + 2; int2++) {
                                    IsoGridSquare square = IsoCell.getInstance().getGridSquare(int1, int2, clickObject.tile.square.getZ());
                                    if (square != null) {
                                        for (int int3 = 0; int3 < square.getMovingObjects().size(); int3++) {
                                            IsoMovingObject movingObject = square.getMovingObjects().get(int3);
                                            if (movingObject instanceof IsoPlayer && this == movingObject) {
                                                this.drawUserName = true;
                                                break;
                                            }
                                        }

                                        if (this.drawUserName) {
                                            break;
                                        }
                                    }

                                    if (this.drawUserName) {
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        this.drawUserName = true;
                    }

                    if (this.drawUserName) {
                        this.updateUserName();
                    }
                }

                if (!GameClient.bClient && this.isoPlayer != null && this.isoPlayer.getVehicle() == null) {
                    String string = "";
                    BaseVehicle vehiclex = this.isoPlayer.getNearVehicle();
                    if (this.getVehicle() == null
                        && vehiclex != null
                        && vehiclex.getPartById("Engine") != null
                        && (
                            this.isoPlayer.getInventory().haveThisKeyId(vehiclex.getKeyId()) != null
                                || vehiclex.isHotwired()
                                || SandboxOptions.getInstance().VehicleEasyUse.getValue()
                        )
                        && UIManager.VisibleAllUI) {
                        Color color0 = Color.HSBtoRGB(
                            vehiclex.colorHue, vehiclex.colorSaturation * 0.5F, vehiclex.colorValue, IsoGameCharacter.L_renderLast.color
                        );
                        string = " [img=media/ui/CarKey.png," + color0.getRedByte() + "," + color0.getGreenByte() + "," + color0.getBlueByte() + "]";
                    }

                    if (!string.equals("")) {
                        this.userName.ReadString(string);
                        this.drawUserName = true;
                    }
                }
            }

            if (this.isoPlayer != null && this.hasInitTextObjects && (this.playerIsSelf() || this.canSeeCurrent)) {
                if (this.canSeeCurrent && this.drawUserName) {
                    float3 -= this.userName.getHeight();
                    this.userName.AddBatchedDraw((int)float2, (int)float3, true);
                }

                if (this.playerIsSelf()) {
                    ActionProgressBar actionProgressBar = UIManager.getProgressBar(int0);
                    if (actionProgressBar != null && actionProgressBar.isVisible()) {
                        float3 -= actionProgressBar.getHeight().intValue() + 2;
                    }
                }

                if (this.playerIsSelf() && this.haloNote != null && this.haloNote.getInternalClock() > 0.0F) {
                    float float5 = this.haloNote.getInternalClock() / (this.haloDispTime / 4.0F);
                    float5 = PZMath.min(float5, 1.0F);
                    float3 -= this.haloNote.getHeight() + 2;
                    this.haloNote.AddBatchedDraw((int)float2, (int)float3, true, float5);
                }
            }

            boolean boolean0 = false;
            if (IsoPlayer.getInstance() != this
                && this.equipedRadio != null
                && this.equipedRadio.getDeviceData() != null
                && this.equipedRadio.getDeviceData().getHeadphoneType() >= 0) {
                boolean0 = true;
            }

            if (this.equipedRadio != null && this.equipedRadio.getDeviceData() != null && !this.equipedRadio.getDeviceData().getIsTurnedOn()) {
                boolean0 = true;
            }

            boolean boolean1 = GameClient.bClient && IsoCamera.CamCharacter instanceof IsoPlayer && !((IsoPlayer)IsoCamera.CamCharacter).accessLevel.equals("");
            if (!this.m_invisible || this == IsoCamera.frameState.CamCharacter || boolean1) {
                this.chatElement.renderBatched(IsoPlayer.getPlayerIndex(), (int)float2, (int)float3, boolean0);
            }
        }

        if (Core.bDebug && DebugOptions.instance.Character.Debug.Render.Angle.getValue() && this.hasActiveModel()) {
            Vector2 vector0 = tempo;
            AnimationPlayer animationPlayer0 = this.getAnimationPlayer();
            vector0.set(this.dir.ToVector());
            this.drawDirectionLine(vector0, 2.4F, 0.0F, 1.0F, 0.0F);
            vector0.setLengthAndDirection(this.getLookAngleRadians(), 1.0F);
            this.drawDirectionLine(vector0, 2.0F, 1.0F, 1.0F, 1.0F);
            vector0.setLengthAndDirection(this.getAnimAngleRadians(), 1.0F);
            this.drawDirectionLine(vector0, 2.0F, 1.0F, 1.0F, 0.0F);
            float float6 = this.getForwardDirection().getDirection();
            vector0.setLengthAndDirection(float6, 1.0F);
            this.drawDirectionLine(vector0, 2.0F, 0.0F, 0.0F, 1.0F);
        }

        if (Core.bDebug && DebugOptions.instance.Character.Debug.Render.DeferredMovement.getValue() && this.hasActiveModel()) {
            Vector2 vector1 = tempo;
            AnimationPlayer animationPlayer1 = this.getAnimationPlayer();
            this.getDeferredMovement(vector1);
            this.drawDirectionLine(vector1, 1000.0F * vector1.getLength() / GameTime.instance.getMultiplier() * 2.0F, 1.0F, 0.5F, 0.5F);
        }

        if (Core.bDebug && DebugOptions.instance.Character.Debug.Render.DeferredAngles.getValue() && this.hasActiveModel()) {
            Vector2 vector2 = tempo;
            AnimationPlayer animationPlayer2 = this.getAnimationPlayer();
            this.getDeferredMovement(vector2);
            this.drawDirectionLine(vector2, 1000.0F * vector2.getLength() / GameTime.instance.getMultiplier() * 2.0F, 1.0F, 0.5F, 0.5F);
        }

        if (Core.bDebug && DebugOptions.instance.Character.Debug.Render.AimCone.getValue()) {
            this.debugAim();
        }

        if (Core.bDebug && DebugOptions.instance.Character.Debug.Render.TestDotSide.getValue()) {
            this.debugTestDotSide();
        }

        if (Core.bDebug && DebugOptions.instance.Character.Debug.Render.Vision.getValue()) {
            this.debugVision();
        }

        if (Core.bDebug) {
            if (DebugOptions.instance.MultiplayerShowZombieMultiplier.getValue() && this instanceof IsoZombie zombie0) {
                byte byte0 = zombie0.canHaveMultipleHits();
                Color color1;
                if (byte0 == 0) {
                    color1 = Colors.Green;
                } else if (byte0 == 1) {
                    color1 = Colors.Yellow;
                } else {
                    color1 = Colors.Red;
                }

                LineDrawer.DrawIsoCircle(this.x, this.y, this.z, 0.45F, 4, color1.r, color1.g, color1.b, 0.5F);
                TextManager.instance
                    .DrawStringCentre(
                        UIFont.DebugConsole,
                        IsoUtils.XToScreenExact(this.x + 0.4F, this.y + 0.4F, this.z, 0),
                        IsoUtils.YToScreenExact(this.x + 0.4F, this.y - 1.4F, this.z, 0),
                        String.valueOf(zombie0.OnlineID),
                        color1.r,
                        color1.g,
                        color1.b,
                        color1.a
                    );
            }

            if (DebugOptions.instance.MultiplayerShowZombieOwner.getValue() && this instanceof IsoZombie zombie1) {
                Color color2;
                if (zombie1.isDead()) {
                    color2 = Colors.Yellow;
                } else if (zombie1.isRemoteZombie()) {
                    color2 = Colors.OrangeRed;
                } else {
                    color2 = Colors.Chartreuse;
                }

                LineDrawer.DrawIsoCircle(this.x, this.y, this.z, 0.45F, 4, color2.r, color2.g, color2.b, 0.5F);
                TextManager.instance
                    .DrawStringCentre(
                        UIFont.DebugConsole,
                        IsoUtils.XToScreenExact(this.x + 0.4F, this.y + 0.4F, this.z, 0),
                        IsoUtils.YToScreenExact(this.x + 0.4F, this.y - 1.4F, this.z, 0),
                        String.valueOf(zombie1.OnlineID),
                        color2.r,
                        color2.g,
                        color2.b,
                        color2.a
                    );
            }

            if (DebugOptions.instance.MultiplayerShowZombiePrediction.getValue() && this instanceof IsoZombie zombie2) {
                LineDrawer.DrawIsoTransform(
                    this.realx,
                    this.realy,
                    this.z,
                    this.realdir.ToVector().x,
                    this.realdir.ToVector().y,
                    0.35F,
                    16,
                    Colors.Blue.r,
                    Colors.Blue.g,
                    Colors.Blue.b,
                    0.35F,
                    1
                );
                if (zombie2.networkAI.DebugInterfaceActive) {
                    LineDrawer.DrawIsoCircle(this.x, this.y, this.z, 0.4F, 4, 1.0F, 0.1F, 0.1F, 0.35F);
                } else if (!zombie2.isRemoteZombie()) {
                    LineDrawer.DrawIsoCircle(this.x, this.y, this.z, 0.3F, 3, Colors.Magenta.r, Colors.Magenta.g, Colors.Magenta.b, 0.35F);
                } else {
                    LineDrawer.DrawIsoCircle(this.x, this.y, this.z, 0.3F, 5, Colors.Magenta.r, Colors.Magenta.g, Colors.Magenta.b, 0.35F);
                }

                LineDrawer.DrawIsoTransform(
                    zombie2.networkAI.targetX,
                    zombie2.networkAI.targetY,
                    this.z,
                    1.0F,
                    0.0F,
                    0.4F,
                    16,
                    Colors.LimeGreen.r,
                    Colors.LimeGreen.g,
                    Colors.LimeGreen.b,
                    0.35F,
                    1
                );
                LineDrawer.DrawIsoLine(
                    this.x,
                    this.y,
                    this.z,
                    zombie2.networkAI.targetX,
                    zombie2.networkAI.targetY,
                    this.z,
                    Colors.LimeGreen.r,
                    Colors.LimeGreen.g,
                    Colors.LimeGreen.b,
                    0.35F,
                    1
                );
                if (IsoUtils.DistanceToSquared(this.x, this.y, this.realx, this.realy) > 4.5F) {
                    LineDrawer.DrawIsoLine(
                        this.realx, this.realy, this.z, this.x, this.y, this.z, Colors.Magenta.r, Colors.Magenta.g, Colors.Magenta.b, 0.35F, 1
                    );
                } else {
                    LineDrawer.DrawIsoLine(this.realx, this.realy, this.z, this.x, this.y, this.z, Colors.Blue.r, Colors.Blue.g, Colors.Blue.b, 0.35F, 1);
                }
            }

            if (DebugOptions.instance.MultiplayerShowZombieDesync.getValue() && this instanceof IsoZombie zombie3) {
                float float7 = IsoUtils.DistanceTo(this.getX(), this.getY(), this.realx, this.realy);
                if (zombie3.isRemoteZombie() && float7 > 1.0F) {
                    LineDrawer.DrawIsoLine(this.realx, this.realy, this.z, this.x, this.y, this.z, Colors.Blue.r, Colors.Blue.g, Colors.Blue.b, 0.9F, 1);
                    LineDrawer.DrawIsoTransform(
                        this.realx,
                        this.realy,
                        this.z,
                        this.realdir.ToVector().x,
                        this.realdir.ToVector().y,
                        0.35F,
                        16,
                        Colors.Blue.r,
                        Colors.Blue.g,
                        Colors.Blue.b,
                        0.9F,
                        1
                    );
                    LineDrawer.DrawIsoCircle(this.x, this.y, this.z, 0.4F, 4, 1.0F, 1.0F, 1.0F, 0.9F);
                    float float8 = IsoUtils.DistanceTo(this.realx, this.realy, zombie3.networkAI.targetX, zombie3.networkAI.targetY);
                    float float9 = IsoUtils.DistanceTo(this.x, this.y, zombie3.networkAI.targetX, zombie3.networkAI.targetY) / float8;
                    float float10 = IsoUtils.XToScreenExact(this.x, this.y, this.z, 0);
                    float float11 = IsoUtils.YToScreenExact(this.x, this.y, this.z, 0);
                    TextManager.instance
                        .DrawStringCentre(
                            UIFont.DebugConsole,
                            float10,
                            float11,
                            String.format("dist:%f scale1:%f", float7, float9),
                            Colors.NavajoWhite.r,
                            Colors.NavajoWhite.g,
                            Colors.NavajoWhite.b,
                            0.9F
                        );
                }
            }

            if (DebugOptions.instance.MultiplayerShowHit.getValue() && this.getHitReactionNetworkAI() != null && this.getHitReactionNetworkAI().isSetup()) {
                LineDrawer.DrawIsoLine(
                    this.x,
                    this.y,
                    this.z,
                    this.x + this.getHitDir().getX(),
                    this.y + this.getHitDir().getY(),
                    this.z,
                    Colors.BlueViolet.r,
                    Colors.BlueViolet.g,
                    Colors.BlueViolet.b,
                    0.8F,
                    1
                );
                LineDrawer.DrawIsoLine(
                    this.getHitReactionNetworkAI().startPosition.x,
                    this.getHitReactionNetworkAI().startPosition.y,
                    this.z,
                    this.getHitReactionNetworkAI().finalPosition.x,
                    this.getHitReactionNetworkAI().finalPosition.y,
                    this.z,
                    Colors.Salmon.r,
                    Colors.Salmon.g,
                    Colors.Salmon.b,
                    0.8F,
                    1
                );
                float float12 = Colors.Salmon.r - 0.2F;
                float float13 = Colors.Salmon.g + 0.2F;
                LineDrawer.DrawIsoTransform(
                    this.getHitReactionNetworkAI().startPosition.x,
                    this.getHitReactionNetworkAI().startPosition.y,
                    this.z,
                    this.getHitReactionNetworkAI().startDirection.x,
                    this.getHitReactionNetworkAI().startDirection.y,
                    0.4F,
                    16,
                    float12,
                    float13,
                    Colors.Salmon.b,
                    0.8F,
                    1
                );
                float13 = Colors.Salmon.g - 0.2F;
                LineDrawer.DrawIsoTransform(
                    this.getHitReactionNetworkAI().finalPosition.x,
                    this.getHitReactionNetworkAI().finalPosition.y,
                    this.z,
                    this.getHitReactionNetworkAI().finalDirection.x,
                    this.getHitReactionNetworkAI().finalDirection.y,
                    0.4F,
                    16,
                    Colors.Salmon.r,
                    float13,
                    Colors.Salmon.b,
                    0.8F,
                    1
                );
            }

            if (DebugOptions.instance.MultiplayerShowPlayerPrediction.getValue() && this instanceof IsoPlayer) {
                if (this.isoPlayer != null && this.isoPlayer.networkAI != null && this.isoPlayer.networkAI.footstepSoundRadius != 0) {
                    LineDrawer.DrawIsoCircle(
                        this.x, this.y, this.z, this.isoPlayer.networkAI.footstepSoundRadius, 32, Colors.Violet.r, Colors.Violet.g, Colors.Violet.b, 0.5F
                    );
                }

                if (this.isoPlayer != null && this.isoPlayer.bRemote) {
                    LineDrawer.DrawIsoCircle(this.x, this.y, this.z, 0.3F, 16, Colors.OrangeRed.r, Colors.OrangeRed.g, Colors.OrangeRed.b, 0.5F);
                    tempo.set(this.realdir.ToVector());
                    LineDrawer.DrawIsoTransform(
                        this.realx, this.realy, this.z, tempo.x, tempo.y, 0.35F, 16, Colors.Blue.r, Colors.Blue.g, Colors.Blue.b, 0.5F, 1
                    );
                    LineDrawer.DrawIsoLine(this.realx, this.realy, this.z, this.x, this.y, this.z, Colors.Blue.r, Colors.Blue.g, Colors.Blue.b, 0.5F, 1);
                    tempo.set(((IsoPlayer)this).networkAI.targetX, ((IsoPlayer)this).networkAI.targetY);
                    LineDrawer.DrawIsoTransform(
                        tempo.x, tempo.y, this.z, 1.0F, 0.0F, 0.4F, 16, Colors.LimeGreen.r, Colors.LimeGreen.g, Colors.LimeGreen.b, 0.5F, 1
                    );
                    LineDrawer.DrawIsoLine(
                        this.x, this.y, this.z, tempo.x, tempo.y, this.z, Colors.LimeGreen.r, Colors.LimeGreen.g, Colors.LimeGreen.b, 0.5F, 1
                    );
                }
            }

            if (DebugOptions.instance.MultiplayerShowTeleport.getValue() && this.getNetworkCharacterAI() != null) {
                NetworkTeleport.NetworkTeleportDebug networkTeleportDebug = this.getNetworkCharacterAI().getTeleportDebug();
                if (networkTeleportDebug != null) {
                    LineDrawer.DrawIsoLine(
                        networkTeleportDebug.lx,
                        networkTeleportDebug.ly,
                        networkTeleportDebug.lz,
                        networkTeleportDebug.nx,
                        networkTeleportDebug.ny,
                        networkTeleportDebug.nz,
                        Colors.NavajoWhite.r,
                        Colors.NavajoWhite.g,
                        Colors.NavajoWhite.b,
                        0.7F,
                        3
                    );
                    LineDrawer.DrawIsoCircle(
                        networkTeleportDebug.nx,
                        networkTeleportDebug.ny,
                        networkTeleportDebug.nz,
                        0.2F,
                        16,
                        Colors.NavajoWhite.r,
                        Colors.NavajoWhite.g,
                        Colors.NavajoWhite.b,
                        0.7F
                    );
                    float float14 = IsoUtils.XToScreenExact(networkTeleportDebug.lx, networkTeleportDebug.ly, networkTeleportDebug.lz, 0);
                    float float15 = IsoUtils.YToScreenExact(networkTeleportDebug.lx, networkTeleportDebug.ly, networkTeleportDebug.lz, 0);
                    TextManager.instance
                        .DrawStringCentre(
                            UIFont.DebugConsole,
                            float14,
                            float15,
                            String.format(
                                "%s id=%d",
                                this instanceof IsoPlayer ? ((IsoPlayer)this).getUsername() : this.getClass().getSimpleName(),
                                networkTeleportDebug.id
                            ),
                            Colors.NavajoWhite.r,
                            Colors.NavajoWhite.g,
                            Colors.NavajoWhite.b,
                            0.7F
                        );
                    TextManager.instance
                        .DrawStringCentre(
                            UIFont.DebugConsole,
                            float14,
                            float15 + 10.0F,
                            networkTeleportDebug.type.name(),
                            Colors.NavajoWhite.r,
                            Colors.NavajoWhite.g,
                            Colors.NavajoWhite.b,
                            0.7F
                        );
                }
            } else if (this.getNetworkCharacterAI() != null) {
                this.getNetworkCharacterAI().clearTeleportDebug();
            }

            if (DebugOptions.instance.MultiplayerShowZombieStatus.getValue() && this instanceof IsoZombie
                || DebugOptions.instance.MultiplayerShowPlayerStatus.getValue() && this instanceof IsoPlayer && !((IsoPlayer)this).isGodMod()) {
                TextManager.StringDrawer stringDrawer = TextManager.instance::DrawString;
                if (this instanceof IsoPlayer && this.isLocal()) {
                    stringDrawer = TextManager.instance::DrawStringRight;
                }

                float float16 = IsoUtils.XToScreenExact(this.x, this.y, this.z, 0);
                float float17 = IsoUtils.YToScreenExact(this.x, this.y, this.z, 0);
                float float18 = 10.0F;
                Color color3 = Colors.GreenYellow;
                float float19;
                stringDrawer.draw(
                    UIFont.DebugConsole,
                    float16,
                    float17 + (float19 = float18 + 11.0F),
                    String.format(
                        "%d %s : %.03f / %.03f",
                        this.getOnlineID(),
                        this.isFemale() ? "F" : "M",
                        this.getHealth(),
                        this instanceof IsoZombie ? 0.0F : this.getBodyDamage().getOverallBodyHealth()
                    ),
                    color3.r,
                    color3.g,
                    color3.b,
                    color3.a
                );
                stringDrawer.draw(
                    UIFont.DebugConsole,
                    float16,
                    float17 + (float18 = float19 + 11.0F),
                    String.format("x=%09.3f ", this.x) + String.format("y=%09.3f ", this.y) + String.format("z=%d", (byte)this.z),
                    color3.r,
                    color3.g,
                    color3.b,
                    color3.a
                );
                if (this instanceof IsoPlayer player) {
                    Color color4 = Colors.NavajoWhite;
                    float float20;
                    stringDrawer.draw(
                        UIFont.DebugConsole,
                        float16,
                        float17 + (float20 = float18 + 18.0F),
                        String.format("IdleSpeed: %s , targetDist: %s ", player.getVariableString("IdleSpeed"), player.getVariableString("targetDist")),
                        color4.r,
                        color4.g,
                        color4.b,
                        1.0
                    );
                    float float21;
                    stringDrawer.draw(
                        UIFont.DebugConsole,
                        float16,
                        float17 + (float21 = float20 + 11.0F),
                        String.format("WalkInjury: %s , WalkSpeed: %s", player.getVariableString("WalkInjury"), player.getVariableString("WalkSpeed")),
                        color4.r,
                        color4.g,
                        color4.b,
                        1.0
                    );
                    float float22;
                    stringDrawer.draw(
                        UIFont.DebugConsole,
                        float16,
                        float17 + (float22 = float21 + 11.0F),
                        String.format("DeltaX: %s , DeltaY: %s", player.getVariableString("DeltaX"), player.getVariableString("DeltaY")),
                        color4.r,
                        color4.g,
                        color4.b,
                        1.0
                    );
                    float float23;
                    stringDrawer.draw(
                        UIFont.DebugConsole,
                        float16,
                        float17 + (float23 = float22 + 11.0F),
                        String.format(
                            "AttackVariationX: %s , AttackVariationY: %s",
                            player.getVariableString("AttackVariationX"),
                            player.getVariableString("AttackVariationY")
                        ),
                        color4.r,
                        color4.g,
                        color4.b,
                        1.0
                    );
                    float float24;
                    stringDrawer.draw(
                        UIFont.DebugConsole,
                        float16,
                        float17 + (float24 = float23 + 11.0F),
                        String.format(
                            "autoShootVarX: %s , autoShootVarY: %s", player.getVariableString("autoShootVarX"), player.getVariableString("autoShootVarY")
                        ),
                        color4.r,
                        color4.g,
                        color4.b,
                        1.0
                    );
                    float float25;
                    stringDrawer.draw(
                        UIFont.DebugConsole,
                        float16,
                        float17 + (float25 = float24 + 11.0F),
                        String.format("recoilVarX: %s , recoilVarY: %s", player.getVariableString("recoilVarX"), player.getVariableString("recoilVarY")),
                        color4.r,
                        color4.g,
                        color4.b,
                        1.0
                    );
                    stringDrawer.draw(
                        UIFont.DebugConsole,
                        float16,
                        float17 + (float18 = float25 + 11.0F),
                        String.format("ShoveAimX: %s , ShoveAimY: %s", player.getVariableString("ShoveAimX"), player.getVariableString("ShoveAimY")),
                        color4.r,
                        color4.g,
                        color4.b,
                        1.0
                    );
                }

                color3 = Colors.Yellow;
                float float26;
                stringDrawer.draw(
                    UIFont.DebugConsole,
                    float16,
                    float17 + (float26 = float18 + 18.0F),
                    String.format("isHitFromBehind=%b/%b", this.isHitFromBehind(), this.getVariableBoolean("frombehind")),
                    color3.r,
                    color3.g,
                    color3.b,
                    1.0
                );
                stringDrawer.draw(
                    UIFont.DebugConsole,
                    float16,
                    float17 + (float18 = float26 + 11.0F),
                    String.format("bKnockedDown=%b/%b", this.isKnockedDown(), this.getVariableBoolean("bknockeddown")),
                    color3.r,
                    color3.g,
                    color3.b,
                    1.0
                );
                float float27;
                stringDrawer.draw(
                    UIFont.DebugConsole,
                    float16,
                    float17 + (float27 = float18 + 11.0F),
                    String.format("isFallOnFront=%b/%b", this.isFallOnFront(), this.getVariableBoolean("fallonfront")),
                    color3.r,
                    color3.g,
                    color3.b,
                    1.0
                );
                stringDrawer.draw(
                    UIFont.DebugConsole,
                    float16,
                    float17 + (float18 = float27 + 11.0F),
                    String.format("isOnFloor=%b/%b", this.isOnFloor(), this.getVariableBoolean("bonfloor")),
                    color3.r,
                    color3.g,
                    color3.b,
                    1.0
                );
                float float28;
                stringDrawer.draw(
                    UIFont.DebugConsole,
                    float16,
                    float17 + (float28 = float18 + 11.0F),
                    String.format("isDead=%b/%b", this.isDead(), this.getVariableBoolean("bdead")),
                    color3.r,
                    color3.g,
                    color3.b,
                    1.0
                );
                if (this instanceof IsoZombie) {
                    float float29;
                    stringDrawer.draw(
                        UIFont.DebugConsole,
                        float16,
                        float17 + (float29 = float28 + 11.0F),
                        String.format("bThump=%b", this.getVariableString("bThump")),
                        color3.r,
                        color3.g,
                        color3.b,
                        1.0
                    );
                    float float30;
                    stringDrawer.draw(
                        UIFont.DebugConsole,
                        float16,
                        float17 + (float30 = float29 + 11.0F),
                        String.format("ThumpType=%s", this.getVariableString("ThumpType")),
                        color3.r,
                        color3.g,
                        color3.b,
                        1.0
                    );
                    stringDrawer.draw(
                        UIFont.DebugConsole,
                        float16,
                        float17 + (float18 = float30 + 11.0F),
                        String.format("onknees=%b", this.getVariableBoolean("onknees")),
                        color3.r,
                        color3.g,
                        color3.b,
                        1.0
                    );
                } else {
                    stringDrawer.draw(
                        UIFont.DebugConsole,
                        float16,
                        float17 + (float18 = float28 + 11.0F),
                        String.format("isBumped=%b/%s", this.isBumped(), this.getBumpType()),
                        color3.r,
                        color3.g,
                        color3.b,
                        1.0
                    );
                }

                color3 = Colors.OrangeRed;
                if (this.getReanimateTimer() <= 0.0F) {
                    color3 = Colors.LimeGreen;
                } else if (this.isBeingSteppedOn()) {
                    color3 = Colors.Blue;
                }

                float float31;
                stringDrawer.draw(
                    UIFont.DebugConsole,
                    float16,
                    float17 + (float31 = float18 + 18.0F),
                    "Reanimate: " + this.getReanimateTimer(),
                    color3.r,
                    color3.g,
                    color3.b,
                    1.0
                );
                if (this.advancedAnimator.getRootLayer() != null) {
                    color3 = Colors.Pink;
                    stringDrawer.draw(
                        UIFont.DebugConsole,
                        float16,
                        float17 + (float18 = float31 + 18.0F),
                        "Animation set: " + this.advancedAnimator.animSet.m_Name,
                        color3.r,
                        color3.g,
                        color3.b,
                        1.0
                    );
                    float float32;
                    stringDrawer.draw(
                        UIFont.DebugConsole,
                        float16,
                        float17 + (float32 = float18 + 11.0F),
                        "Animation state: " + this.advancedAnimator.getCurrentStateName(),
                        color3.r,
                        color3.g,
                        color3.b,
                        1.0
                    );
                    stringDrawer.draw(
                        UIFont.DebugConsole,
                        float16,
                        float17 + (float31 = float32 + 11.0F),
                        "Animation node: " + this.advancedAnimator.getRootLayer().getDebugNodeName(),
                        color3.r,
                        color3.g,
                        color3.b,
                        1.0
                    );
                }

                color3 = Colors.LightBlue;
                stringDrawer.draw(
                    UIFont.DebugConsole,
                    float16,
                    float17 + (float18 = float31 + 11.0F),
                    String.format("Previous state: %s ( %s )", this.getPreviousStateName(), this.getPreviousActionContextStateName()),
                    color3.r,
                    color3.g,
                    color3.b,
                    1.0
                );
                float float33;
                stringDrawer.draw(
                    UIFont.DebugConsole,
                    float16,
                    float17 + (float33 = float18 + 11.0F),
                    String.format("Current state: %s ( %s )", this.getCurrentStateName(), this.getCurrentActionContextStateName()),
                    color3.r,
                    color3.g,
                    color3.b,
                    1.0
                );
                stringDrawer.draw(
                    UIFont.DebugConsole,
                    float16,
                    float17 + (float18 = float33 + 11.0F),
                    String.format(
                        "Child state: %s",
                        this.getActionContext() != null
                                && this.getActionContext().getChildStates() != null
                                && this.getActionContext().getChildStates().size() > 0
                                && this.getActionContext().getChildStateAt(0) != null
                            ? this.getActionContext().getChildStateAt(0).getName()
                            : "\"\""
                    ),
                    color3.r,
                    color3.g,
                    color3.b,
                    1.0
                );
                if (this.CharacterActions != null) {
                    stringDrawer.draw(
                        UIFont.DebugConsole,
                        float16,
                        float17 + (float18 += 11.0F),
                        String.format("Character actions: %d", this.CharacterActions.size()),
                        color3.r,
                        color3.g,
                        color3.b,
                        1.0
                    );

                    for (BaseAction baseAction : this.CharacterActions) {
                        if (baseAction instanceof LuaTimedActionNew) {
                            stringDrawer.draw(
                                UIFont.DebugConsole,
                                float16,
                                float17 + (float18 += 11.0F),
                                String.format("Action: %s", ((LuaTimedActionNew)baseAction).getMetaType()),
                                color3.r,
                                color3.g,
                                color3.b,
                                1.0
                            );
                        }
                    }
                }

                if (this instanceof IsoZombie) {
                    color3 = Colors.GreenYellow;
                    IsoZombie zombie4 = (IsoZombie)this;
                    float float34;
                    stringDrawer.draw(
                        UIFont.DebugConsole,
                        float16,
                        float17 + (float34 = float18 + 18.0F),
                        "Prediction: " + this.getNetworkCharacterAI().predictionType,
                        color3.r,
                        color3.g,
                        color3.b,
                        1.0
                    );
                    stringDrawer.draw(
                        UIFont.DebugConsole,
                        float16,
                        float17 + (float18 = float34 + 11.0F),
                        String.format("Real state: %s", zombie4.realState),
                        color3.r,
                        color3.g,
                        color3.b,
                        1.0
                    );
                    if (zombie4.target instanceof IsoPlayer) {
                        float float35;
                        stringDrawer.draw(
                            UIFont.DebugConsole,
                            float16,
                            float17 + (float35 = float18 + 11.0F),
                            "Target: " + ((IsoPlayer)zombie4.target).username + "  =" + zombie4.vectorToTarget.getLength(),
                            color3.r,
                            color3.g,
                            color3.b,
                            1.0
                        );
                    } else {
                        float float36;
                        stringDrawer.draw(
                            UIFont.DebugConsole,
                            float16,
                            float17 + (float36 = float18 + 11.0F),
                            "Target: " + zombie4.target + "  =" + zombie4.vectorToTarget.getLength(),
                            color3.r,
                            color3.g,
                            color3.b,
                            1.0
                        );
                    }
                }
            }
        }

        if (this.inventory != null) {
            for (int int4 = 0; int4 < this.inventory.Items.size(); int4++) {
                InventoryItem item = this.inventory.Items.get(int4);
                if (item instanceof IUpdater) {
                    ((IUpdater)item).renderlast();
                }
            }

            if (Core.bDebug && DebugOptions.instance.PathfindRenderPath.getValue() && this.pfb2 != null) {
                this.pfb2.render();
            }

            if (Core.bDebug && DebugOptions.instance.CollideWithObstaclesRenderRadius.getValue()) {
                float float37 = 0.3F;
                float float38 = 1.0F;
                float float39 = 1.0F;
                float float40 = 1.0F;
                if (!this.isCollidable()) {
                    float40 = 0.0F;
                }

                if ((int)this.z != (int)IsoCamera.frameState.CamCharacterZ) {
                    float40 = 0.5F;
                    float39 = 0.5F;
                    float38 = 0.5F;
                }

                LineDrawer.DrawIsoCircle(this.x, this.y, this.z, float37, 16, float38, float39, float40, 1.0F);
            }

            if (DebugOptions.instance.Animation.Debug.getValue() && this.hasActiveModel()) {
                int int5 = (int)IsoUtils.XToScreenExact(this.x, this.y, this.z, 0);
                int int6 = (int)IsoUtils.YToScreenExact(this.x, this.y, this.z, 0);
                TextManager.instance.DrawString(int5, int6, this.getAnimationDebug());
            }

            if (this.getIsNPC() && this.GameCharacterAIBrain != null) {
                this.GameCharacterAIBrain.renderlast();
            }
        }
    }

    protected boolean renderTextureInsteadOfModel(float var1, float var2) {
        return false;
    }

    public void drawDirectionLine(Vector2 dir, float length, float r, float g, float b) {
        float float0 = this.x + dir.x * length;
        float float1 = this.y + dir.y * length;
        float float2 = IsoUtils.XToScreenExact(this.x, this.y, this.z, 0);
        float float3 = IsoUtils.YToScreenExact(this.x, this.y, this.z, 0);
        float float4 = IsoUtils.XToScreenExact(float0, float1, this.z, 0);
        float float5 = IsoUtils.YToScreenExact(float0, float1, this.z, 0);
        LineDrawer.drawLine(float2, float3, float4, float5, r, g, b, 0.5F, 1);
    }

    public void drawDebugTextBelow(String text) {
        int int0 = TextManager.instance.MeasureStringX(UIFont.Small, text) + 32;
        int int1 = TextManager.instance.getFontHeight(UIFont.Small);
        int int2 = (int)Math.ceil(int1 * 1.25);
        float float0 = IsoUtils.XToScreenExact(this.getX() + 0.25F, this.getY() + 0.25F, this.getZ(), 0);
        float float1 = IsoUtils.YToScreenExact(this.getX() + 0.25F, this.getY() + 0.25F, this.getZ(), 0);
        SpriteRenderer.instance.renderi(null, (int)(float0 - int0 / 2), (int)(float1 - (int2 - int1) / 2), int0, int2, 0.0F, 0.0F, 0.0F, 0.5F, null);
        TextManager.instance.DrawStringCentre(UIFont.Small, float0, float1, text, 1.0, 1.0, 1.0, 1.0);
    }

    public Radio getEquipedRadio() {
        return this.equipedRadio;
    }

    private void radioEquipedCheck() {
        if (this.leftHandItem != this.leftHandCache) {
            this.leftHandCache = this.leftHandItem;
            if (this.leftHandItem != null && (this.equipedRadio == null || this.equipedRadio != this.rightHandItem) && this.leftHandItem instanceof Radio) {
                this.equipedRadio = (Radio)this.leftHandItem;
            } else if (this.equipedRadio != null && this.equipedRadio != this.rightHandItem) {
                if (this.equipedRadio.getDeviceData() != null) {
                    this.equipedRadio.getDeviceData().cleanSoundsAndEmitter();
                }

                this.equipedRadio = null;
            }
        }

        if (this.rightHandItem != this.rightHandCache) {
            this.rightHandCache = this.rightHandItem;
            if (this.rightHandItem != null && this.rightHandItem instanceof Radio) {
                this.equipedRadio = (Radio)this.rightHandItem;
            } else if (this.equipedRadio != null && this.equipedRadio != this.leftHandItem) {
                if (this.equipedRadio.getDeviceData() != null) {
                    this.equipedRadio.getDeviceData().cleanSoundsAndEmitter();
                }

                this.equipedRadio = null;
            }
        }
    }

    private void debugAim() {
        if (this == IsoPlayer.getInstance()) {
            IsoPlayer player = (IsoPlayer)this;
            if (player.IsAiming()) {
                HandWeapon weapon = Type.tryCastTo(this.getPrimaryHandItem(), HandWeapon.class);
                if (weapon == null) {
                    weapon = player.bareHands;
                }

                float float0 = weapon.getMaxRange(player) * weapon.getRangeMod(player);
                float float1 = this.getLookAngleRadians();
                LineDrawer.drawDirectionLine(this.x, this.y, this.z, float0, float1, 1.0F, 1.0F, 1.0F, 0.5F, 1);
                float float2 = weapon.getMinAngle();
                float2 -= weapon.getAimingPerkMinAngleModifier() * (this.getPerkLevel(PerkFactory.Perks.Aiming) / 2.0F);
                LineDrawer.drawDotLines(this.x, this.y, this.z, float0, float1, float2, 1.0F, 1.0F, 1.0F, 0.5F, 1);
                float float3 = weapon.getMinRange();
                LineDrawer.drawArc(this.x, this.y, this.z, float3, float1, float2, 6, 1.0F, 1.0F, 1.0F, 0.5F);
                if (float3 != float0) {
                    LineDrawer.drawArc(this.x, this.y, this.z, float0, float1, float2, 6, 1.0F, 1.0F, 1.0F, 0.5F);
                }

                float float4 = PZMath.min(float0 + 1.0F, 2.0F);
                LineDrawer.drawArc(this.x, this.y, this.z, float4, float1, float2, 6, 0.75F, 0.75F, 0.75F, 0.5F);
                float float5 = Core.getInstance().getIgnoreProneZombieRange();
                if (float5 > 0.0F) {
                    LineDrawer.drawArc(this.x, this.y, this.z, float5, float1, 0.0F, 12, 0.0F, 0.0F, 1.0F, 0.25F);
                    LineDrawer.drawDotLines(this.x, this.y, this.z, float5, float1, 0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1);
                }

                AttackVars attackVarsx = new AttackVars();
                ArrayList arrayList = new ArrayList();
                SwipeStatePlayer.instance().CalcAttackVars((IsoLivingCharacter)this, attackVarsx);
                SwipeStatePlayer.instance().CalcHitList(this, false, attackVarsx, arrayList);
                if (attackVarsx.targetOnGround.getMovingObject() != null) {
                    HitInfo hitInfo0 = attackVarsx.targetsProne.get(0);
                    LineDrawer.DrawIsoCircle(hitInfo0.x, hitInfo0.y, hitInfo0.z, 0.1F, 8, 1.0F, 1.0F, 0.0F, 1.0F);
                } else if (attackVarsx.targetsStanding.size() > 0) {
                    HitInfo hitInfo1 = attackVarsx.targetsStanding.get(0);
                    LineDrawer.DrawIsoCircle(hitInfo1.x, hitInfo1.y, hitInfo1.z, 0.1F, 8, 1.0F, 1.0F, 0.0F, 1.0F);
                }

                for (int int0 = 0; int0 < arrayList.size(); int0++) {
                    HitInfo hitInfo2 = (HitInfo)arrayList.get(int0);
                    IsoMovingObject movingObject = hitInfo2.getObject();
                    if (movingObject != null) {
                        int int1 = hitInfo2.chance;
                        float float6 = 1.0F - int1 / 100.0F;
                        float float7 = 1.0F - float6;
                        float float8 = Math.max(0.2F, int1 / 100.0F) / 2.0F;
                        float float9 = IsoUtils.XToScreenExact(movingObject.x - float8, movingObject.y + float8, movingObject.z, 0);
                        float float10 = IsoUtils.YToScreenExact(movingObject.x - float8, movingObject.y + float8, movingObject.z, 0);
                        float float11 = IsoUtils.XToScreenExact(movingObject.x - float8, movingObject.y - float8, movingObject.z, 0);
                        float float12 = IsoUtils.YToScreenExact(movingObject.x - float8, movingObject.y - float8, movingObject.z, 0);
                        float float13 = IsoUtils.XToScreenExact(movingObject.x + float8, movingObject.y - float8, movingObject.z, 0);
                        float float14 = IsoUtils.YToScreenExact(movingObject.x + float8, movingObject.y - float8, movingObject.z, 0);
                        float float15 = IsoUtils.XToScreenExact(movingObject.x + float8, movingObject.y + float8, movingObject.z, 0);
                        float float16 = IsoUtils.YToScreenExact(movingObject.x + float8, movingObject.y + float8, movingObject.z, 0);
                        SpriteRenderer.instance.renderPoly(float9, float10, float11, float12, float13, float14, float15, float16, float6, float7, 0.0F, 0.5F);
                        UIFont uIFont = UIFont.DebugConsole;
                        TextManager.instance.DrawStringCentre(uIFont, float15, float16, String.valueOf(hitInfo2.dot), 1.0, 1.0, 1.0, 1.0);
                        TextManager.instance
                            .DrawStringCentre(uIFont, float15, float16 + TextManager.instance.getFontHeight(uIFont), hitInfo2.chance + "%", 1.0, 1.0, 1.0, 1.0);
                        float6 = 1.0F;
                        float7 = 1.0F;
                        float float17 = 1.0F;
                        float float18 = PZMath.sqrt(hitInfo2.distSq);
                        if (float18 < weapon.getMinRange()) {
                            float17 = 0.0F;
                            float6 = 0.0F;
                        }

                        TextManager.instance
                            .DrawStringCentre(
                                uIFont, float15, float16 + TextManager.instance.getFontHeight(uIFont) * 2, "DIST: " + float18, float6, float7, float17, 1.0
                            );
                    }

                    if (hitInfo2.window.getObject() != null) {
                        hitInfo2.window.getObject().setHighlighted(true);
                    }
                }
            }
        }
    }

    private void debugTestDotSide() {
        if (this == IsoPlayer.getInstance()) {
            float float0 = this.getLookAngleRadians();
            float float1 = 2.0F;
            float float2 = 0.7F;
            LineDrawer.drawDotLines(this.x, this.y, this.z, float1, float0, float2, 1.0F, 1.0F, 1.0F, 0.5F, 1);
            float2 = -0.5F;
            LineDrawer.drawDotLines(this.x, this.y, this.z, float1, float0, float2, 1.0F, 1.0F, 1.0F, 0.5F, 1);
            LineDrawer.drawArc(this.x, this.y, this.z, float1, float0, -1.0F, 16, 1.0F, 1.0F, 1.0F, 0.5F);
            ArrayList arrayList = this.getCell().getZombieList();

            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                IsoMovingObject movingObject = (IsoMovingObject)arrayList.get(int0);
                if (this.DistToSquared(movingObject) < float1 * float1) {
                    LineDrawer.DrawIsoCircle(movingObject.x, movingObject.y, movingObject.z, 0.3F, 1.0F, 1.0F, 1.0F, 1.0F);
                    float float3 = 0.2F;
                    float float4 = IsoUtils.XToScreenExact(movingObject.x + float3, movingObject.y + float3, movingObject.z, 0);
                    float float5 = IsoUtils.YToScreenExact(movingObject.x + float3, movingObject.y + float3, movingObject.z, 0);
                    UIFont uIFont = UIFont.DebugConsole;
                    int int1 = TextManager.instance.getFontHeight(uIFont);
                    TextManager.instance.DrawStringCentre(uIFont, float4, float5 + int1, "SIDE: " + this.testDotSide(movingObject), 1.0, 1.0, 1.0, 1.0);
                    Vector2 vector0 = this.getLookVector(tempo2);
                    Vector2 vector1 = tempo.set(movingObject.x - this.x, movingObject.y - this.y);
                    vector1.normalize();
                    float float6 = PZMath.wrap(vector1.getDirection() - vector0.getDirection(), 0.0F, (float) (Math.PI * 2));
                    TextManager.instance.DrawStringCentre(uIFont, float4, float5 + int1 * 2, "ANGLE (0-360): " + PZMath.radToDeg(float6), 1.0, 1.0, 1.0, 1.0);
                    float6 = (float)Math.acos(this.getDotWithForwardDirection(movingObject.x, movingObject.y));
                    TextManager.instance.DrawStringCentre(uIFont, float4, float5 + int1 * 3, "ANGLE (0-180): " + PZMath.radToDeg(float6), 1.0, 1.0, 1.0, 1.0);
                }
            }
        }
    }

    private void debugVision() {
        if (this == IsoPlayer.getInstance()) {
            float float0 = LightingJNI.calculateVisionCone(this);
            LineDrawer.drawDotLines(
                this.x, this.y, this.z, GameTime.getInstance().getViewDist(), this.getLookAngleRadians(), -float0, 1.0F, 1.0F, 1.0F, 0.5F, 1
            );
            LineDrawer.drawArc(this.x, this.y, this.z, GameTime.getInstance().getViewDist(), this.getLookAngleRadians(), -float0, 16, 1.0F, 1.0F, 1.0F, 0.5F);
            float float1 = 3.5F - this.stats.getFatigue();
            LineDrawer.drawArc(this.x, this.y, this.z, float1, this.getLookAngleRadians(), -1.0F, 32, 1.0F, 1.0F, 1.0F, 0.5F);
        }
    }

    public void setDefaultState() {
        this.stateMachine.changeState(this.defaultState, null);
    }

    public void SetOnFire() {
        if (!this.OnFire) {
            this.setOnFire(true);
            float float0 = Core.TileScale;
            this.AttachAnim(
                "Fire",
                "01",
                4,
                IsoFireManager.FireAnimDelay,
                (int)(-(this.offsetX + 1.0F * float0)) + (8 - Rand.Next(16)),
                (int)(-(this.offsetY + -89.0F * float0)) + (int)((10 + Rand.Next(20)) * float0),
                true,
                0,
                false,
                0.7F,
                IsoFireManager.FireTintMod
            );
            IsoFireManager.AddBurningCharacter(this);
            int int0 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.MAX));
            if (this instanceof IsoPlayer) {
                this.getBodyDamage().getBodyParts().get(int0).setBurned();
            }

            if (float0 == 2.0F) {
                int int1 = this.AttachedAnimSprite.size() - 1;
                this.AttachedAnimSprite.get(int1).setScale(float0, float0);
            }

            if (!this.getEmitter().isPlaying("BurningFlesh")) {
                this.getEmitter().playSoundImpl("BurningFlesh", this);
            }
        }
    }

    @Override
    public void StopBurning() {
        if (this.OnFire) {
            IsoFireManager.RemoveBurningCharacter(this);
            this.setOnFire(false);
            if (this.AttachedAnimSprite != null) {
                this.AttachedAnimSprite.clear();
            }

            this.getEmitter().stopOrTriggerSoundByName("BurningFlesh");
        }
    }

    @Override
    public void sendStopBurning() {
        if (GameClient.bClient) {
            if (this instanceof IsoPlayer player) {
                if (player.isLocalPlayer()) {
                    this.StopBurning();
                } else {
                    GameClient.sendStopFire(player);
                }
            }

            if (this.isZombie()) {
                IsoZombie zombie0 = (IsoZombie)this;
                GameClient.sendStopFire(zombie0);
            }
        }
    }

    public void SpreadFireMP() {
        if (this.OnFire && GameServer.bServer && SandboxOptions.instance.FireSpread.getValue()) {
            IsoGridSquare square = ServerMap.instance.getGridSquare((int)this.x, (int)this.y, (int)this.z);
            if (square != null && !square.getProperties().Is(IsoFlagType.burning) && Rand.Next(Rand.AdjustForFramerate(3000)) < this.FireSpreadProbability) {
                IsoFireManager.StartFire(this.getCell(), square, false, 80);
            }
        }
    }

    public void SpreadFire() {
        if (this.OnFire && !GameServer.bServer && !GameClient.bClient && SandboxOptions.instance.FireSpread.getValue()) {
            if (this.square != null
                && !this.square.getProperties().Is(IsoFlagType.burning)
                && Rand.Next(Rand.AdjustForFramerate(3000)) < this.FireSpreadProbability) {
                IsoFireManager.StartFire(this.getCell(), this.square, false, 80);
            }
        }
    }

    public void Throw(HandWeapon weapon) {
        if (this instanceof IsoPlayer && ((IsoPlayer)this).getJoypadBind() != -1) {
            Vector2 vector = tempo.set(this.m_forwardDirection);
            vector.setLength(weapon.getMaxRange());
            this.attackTargetSquare = this.getCell()
                .getGridSquare((double)(this.getX() + vector.getX()), (double)(this.getY() + vector.getY()), (double)this.getZ());
            if (this.attackTargetSquare == null) {
                this.attackTargetSquare = this.getCell().getGridSquare((double)(this.getX() + vector.getX()), (double)(this.getY() + vector.getY()), 0.0);
            }
        }

        float float0 = this.attackTargetSquare.getX() - this.getX();
        if (float0 > 0.0F) {
            if (this.attackTargetSquare.getX() - this.getX() > weapon.getMaxRange()) {
                float0 = weapon.getMaxRange();
            }
        } else if (this.attackTargetSquare.getX() - this.getX() < -weapon.getMaxRange()) {
            float0 = -weapon.getMaxRange();
        }

        float float1 = this.attackTargetSquare.getY() - this.getY();
        if (float1 > 0.0F) {
            if (this.attackTargetSquare.getY() - this.getY() > weapon.getMaxRange()) {
                float1 = weapon.getMaxRange();
            }
        } else if (this.attackTargetSquare.getY() - this.getY() < -weapon.getMaxRange()) {
            float1 = -weapon.getMaxRange();
        }

        if (weapon.getPhysicsObject().equals("Ball")) {
            new IsoBall(this.getCell(), this.getX(), this.getY(), this.getZ() + 0.6F, float0 * 0.4F, float1 * 0.4F, weapon, this);
        } else {
            new IsoMolotovCocktail(this.getCell(), this.getX(), this.getY(), this.getZ() + 0.6F, float0 * 0.4F, float1 * 0.4F, weapon, this);
        }

        if (this instanceof IsoPlayer) {
            ((IsoPlayer)this).setAttackAnimThrowTimer(0L);
        }
    }

    public void serverRemoveItemFromZombie(String item) {
        if (GameServer.bServer) {
            IsoZombie zombie0 = Type.tryCastTo(this, IsoZombie.class);
            this.getItemVisuals(tempItemVisuals);

            for (int int0 = 0; int0 < tempItemVisuals.size(); int0++) {
                ItemVisual itemVisual = tempItemVisuals.get(int0);
                Item _item = itemVisual.getScriptItem();
                if (_item != null && _item.name.equals(item)) {
                    tempItemVisuals.remove(int0--);
                    zombie0.itemVisuals.clear();
                    zombie0.itemVisuals.addAll(tempItemVisuals);
                }
            }
        }
    }

    public boolean helmetFall(boolean hitHead) {
        return this.helmetFall(hitHead, null);
    }

    public boolean helmetFall(boolean hitHead, String forcedItem) {
        IsoPlayer player = Type.tryCastTo(this, IsoPlayer.class);
        boolean boolean0 = false;
        InventoryItem item0 = null;
        IsoZombie zombie0 = Type.tryCastTo(this, IsoZombie.class);
        if (zombie0 != null && !zombie0.isUsingWornItems()) {
            this.getItemVisuals(tempItemVisuals);

            for (int int0 = 0; int0 < tempItemVisuals.size(); int0++) {
                ItemVisual itemVisual = tempItemVisuals.get(int0);
                Item item1 = itemVisual.getScriptItem();
                if (item1 != null && item1.getType() == Item.Type.Clothing && item1.getChanceToFall() > 0) {
                    int int1 = item1.getChanceToFall();
                    if (hitHead) {
                        int1 += 40;
                    }

                    if (item1.name.equals(forcedItem)) {
                        int1 = 100;
                    }

                    if (Rand.Next(100) > int1) {
                        InventoryItem item2 = InventoryItemFactory.CreateItem(item1.getFullName());
                        if (item2 != null) {
                            if (item2.getVisual() != null) {
                                item2.getVisual().copyFrom(itemVisual);
                                item2.synchWithVisual();
                            }

                            IsoFallingClothing fallingClothing0 = new IsoFallingClothing(
                                this.getCell(), this.getX(), this.getY(), PZMath.min(this.getZ() + 0.4F, (int)this.getZ() + 0.95F), 0.2F, 0.2F, item2
                            );
                            if (!StringUtils.isNullOrEmpty(forcedItem)) {
                                fallingClothing0.addWorldItem = false;
                            }

                            tempItemVisuals.remove(int0--);
                            zombie0.itemVisuals.clear();
                            zombie0.itemVisuals.addAll(tempItemVisuals);
                            this.resetModelNextFrame();
                            this.onWornItemsChanged();
                            boolean0 = true;
                            item0 = item2;
                        }
                    }
                }
            }
        } else if (this.getWornItems() != null && !this.getWornItems().isEmpty()) {
            for (int int2 = 0; int2 < this.getWornItems().size(); int2++) {
                WornItem wornItem = this.getWornItems().get(int2);
                InventoryItem item3 = wornItem.getItem();
                String string = wornItem.getLocation();
                if (item3 instanceof Clothing) {
                    int int3 = ((Clothing)item3).getChanceToFall();
                    if (hitHead) {
                        int3 += 40;
                    }

                    if (item3.getType().equals(forcedItem)) {
                        int3 = 100;
                    }

                    if (((Clothing)item3).getChanceToFall() > 0 && Rand.Next(100) <= int3) {
                        IsoFallingClothing fallingClothing1 = new IsoFallingClothing(
                            this.getCell(),
                            this.getX(),
                            this.getY(),
                            PZMath.min(this.getZ() + 0.4F, (int)this.getZ() + 0.95F),
                            Rand.Next(-0.2F, 0.2F),
                            Rand.Next(-0.2F, 0.2F),
                            item3
                        );
                        if (!StringUtils.isNullOrEmpty(forcedItem)) {
                            fallingClothing1.addWorldItem = false;
                        }

                        this.getInventory().Remove(item3);
                        this.getWornItems().remove(item3);
                        item0 = item3;
                        this.resetModelNextFrame();
                        this.onWornItemsChanged();
                        boolean0 = true;
                        if (GameClient.bClient && player != null && player.isLocalPlayer() && StringUtils.isNullOrEmpty(forcedItem)) {
                            GameClient.instance.sendClothing(player, string, null);
                        }
                    }
                }
            }
        }

        if (boolean0 && GameClient.bClient && StringUtils.isNullOrEmpty(forcedItem) && IsoPlayer.getInstance().isLocalPlayer()) {
            GameClient.sendZombieHelmetFall(IsoPlayer.getInstance(), this, item0);
        }

        if (boolean0 && player != null && player.isLocalPlayer()) {
            LuaEventManager.triggerEvent("OnClothingUpdated", this);
        }

        if (boolean0 && this.isZombie()) {
            PersistentOutfits.instance.setFallenHat(this, true);
        }

        return boolean0;
    }

    @Override
    public void smashCarWindow(VehiclePart part) {
        HashMap hashMap = this.getStateMachineParams(SmashWindowState.instance());
        hashMap.clear();
        hashMap.put(0, part.getWindow());
        hashMap.put(1, part.getVehicle());
        hashMap.put(2, part);
        this.actionContext.reportEvent("EventSmashWindow");
    }

    @Override
    public void smashWindow(IsoWindow w) {
        if (!w.isInvincible()) {
            HashMap hashMap = this.getStateMachineParams(SmashWindowState.instance());
            hashMap.clear();
            hashMap.put(0, w);
            this.actionContext.reportEvent("EventSmashWindow");
        }
    }

    @Override
    public void openWindow(IsoWindow w) {
        if (!w.isInvincible()) {
            OpenWindowState.instance().setParams(this, w);
            this.actionContext.reportEvent("EventOpenWindow");
        }
    }

    @Override
    public void closeWindow(IsoWindow w) {
        if (!w.isInvincible()) {
            HashMap hashMap = this.getStateMachineParams(CloseWindowState.instance());
            hashMap.clear();
            hashMap.put(0, w);
            this.actionContext.reportEvent("EventCloseWindow");
        }
    }

    @Override
    public void climbThroughWindow(IsoWindow w) {
        if (w.canClimbThrough(this)) {
            float float0 = this.x - (int)this.x;
            float float1 = this.y - (int)this.y;
            byte byte0 = 0;
            byte byte1 = 0;
            if (w.getX() > this.x && !w.north) {
                byte0 = -1;
            }

            if (w.getY() > this.y && w.north) {
                byte1 = -1;
            }

            this.x = w.getX() + float0 + byte0;
            this.y = w.getY() + float1 + byte1;
            ClimbThroughWindowState.instance().setParams(this, w);
            this.actionContext.reportEvent("EventClimbWindow");
        }
    }

    @Override
    public void climbThroughWindow(IsoWindow w, Integer startingFrame) {
        if (w.canClimbThrough(this)) {
            ClimbThroughWindowState.instance().setParams(this, w);
            this.actionContext.reportEvent("EventClimbWindow");
        }
    }

    public boolean isClosingWindow(IsoWindow window) {
        if (window == null) {
            return false;
        } else {
            return !this.isCurrentState(CloseWindowState.instance()) ? false : CloseWindowState.instance().getWindow(this) == window;
        }
    }

    public boolean isClimbingThroughWindow(IsoWindow window) {
        if (window == null) {
            return false;
        } else if (!this.isCurrentState(ClimbThroughWindowState.instance())) {
            return false;
        } else {
            return !this.getVariableBoolean("BlockWindow") ? false : ClimbThroughWindowState.instance().getWindow(this) == window;
        }
    }

    @Override
    public void climbThroughWindowFrame(IsoObject obj) {
        if (IsoWindowFrame.canClimbThrough(obj, this)) {
            ClimbThroughWindowState.instance().setParams(this, obj);
            this.actionContext.reportEvent("EventClimbWindow");
        }
    }

    @Override
    public void climbSheetRope() {
        if (this.canClimbSheetRope(this.current)) {
            HashMap hashMap = this.getStateMachineParams(ClimbSheetRopeState.instance());
            hashMap.clear();
            this.actionContext.reportEvent("EventClimbRope");
        }
    }

    @Override
    public void climbDownSheetRope() {
        if (this.canClimbDownSheetRope(this.current)) {
            this.dropHeavyItems();
            HashMap hashMap = this.getStateMachineParams(ClimbDownSheetRopeState.instance());
            hashMap.clear();
            this.actionContext.reportEvent("EventClimbDownRope");
        }
    }

    @Override
    public boolean canClimbSheetRope(IsoGridSquare sq) {
        if (sq == null) {
            return false;
        } else {
            int int0 = sq.getZ();

            while (sq != null) {
                if (!IsoWindow.isSheetRopeHere(sq)) {
                    return false;
                }

                if (!IsoWindow.canClimbHere(sq)) {
                    return false;
                }

                if (sq.TreatAsSolidFloor() && sq.getZ() > int0) {
                    return false;
                }

                if (IsoWindow.isTopOfSheetRopeHere(sq)) {
                    return true;
                }

                sq = this.getCell().getGridSquare(sq.getX(), sq.getY(), sq.getZ() + 1);
            }

            return false;
        }
    }

    @Override
    public boolean canClimbDownSheetRopeInCurrentSquare() {
        return this.canClimbDownSheetRope(this.current);
    }

    @Override
    public boolean canClimbDownSheetRope(IsoGridSquare sq) {
        if (sq == null) {
            return false;
        } else {
            int int0 = sq.getZ();

            while (sq != null) {
                if (!IsoWindow.isSheetRopeHere(sq)) {
                    return false;
                }

                if (!IsoWindow.canClimbHere(sq)) {
                    return false;
                }

                if (sq.TreatAsSolidFloor()) {
                    return sq.getZ() < int0;
                }

                sq = this.getCell().getGridSquare(sq.getX(), sq.getY(), sq.getZ() - 1);
            }

            return false;
        }
    }

    @Override
    public void climbThroughWindow(IsoThumpable w) {
        if (w.canClimbThrough(this)) {
            float float0 = this.x - (int)this.x;
            float float1 = this.y - (int)this.y;
            byte byte0 = 0;
            byte byte1 = 0;
            if (w.getX() > this.x && !w.north) {
                byte0 = -1;
            }

            if (w.getY() > this.y && w.north) {
                byte1 = -1;
            }

            this.x = w.getX() + float0 + byte0;
            this.y = w.getY() + float1 + byte1;
            ClimbThroughWindowState.instance().setParams(this, w);
            this.actionContext.reportEvent("EventClimbWindow");
        }
    }

    @Override
    public void climbThroughWindow(IsoThumpable w, Integer startingFrame) {
        if (w.canClimbThrough(this)) {
            ClimbThroughWindowState.instance().setParams(this, w);
            this.actionContext.reportEvent("EventClimbWindow");
        }
    }

    @Override
    public void climbOverFence(IsoDirections dir) {
        if (this.current != null) {
            IsoGridSquare square = this.current.nav[dir.index()];
            if (IsoWindow.canClimbThroughHelper(this, this.current, square, dir == IsoDirections.N || dir == IsoDirections.S)) {
                ClimbOverFenceState.instance().setParams(this, dir);
                this.actionContext.reportEvent("EventClimbFence");
            }
        }
    }

    @Override
    public boolean isAboveTopOfStairs() {
        if (this.z != 0.0F && !(this.z - (int)this.z > 0.01) && (this.current == null || !this.current.TreatAsSolidFloor())) {
            IsoGridSquare square = this.getCell().getGridSquare((double)this.x, (double)this.y, (double)(this.z - 1.0F));
            return square != null && (square.Has(IsoObjectType.stairsTN) || square.Has(IsoObjectType.stairsTW));
        } else {
            return false;
        }
    }

    @Override
    public void preupdate() {
        super.preupdate();
        if (!this.m_bDebugVariablesRegistered && DebugOptions.instance.Character.Debug.RegisterDebugVariables.getValue()) {
            this.registerDebugGameVariables();
        }

        this.updateAnimationRecorderState();
        if (this.isAnimationRecorderActive()) {
            int int0 = IsoWorld.instance.getFrameNo();
            this.m_animationRecorder.beginLine(int0);
        }

        if (GameServer.bServer) {
            this.getXp().update();
        }
    }

    public void setTeleport(NetworkTeleport _teleport) {
        this.teleport = _teleport;
    }

    public NetworkTeleport getTeleport() {
        return this.teleport;
    }

    public boolean isTeleporting() {
        return this.teleport != null;
    }

    @Override
    public void update() {
        IsoGameCharacter.s_performance.update.invokeAndMeasure(this, IsoGameCharacter::updateInternal);
    }

    private void updateInternal() {
        if (this.current != null) {
            if (this.teleport != null) {
                this.teleport.process(IsoPlayer.getPlayerIndex());
            }

            this.updateAlpha();
            if (this.isNPC) {
                if (this.GameCharacterAIBrain == null) {
                    this.GameCharacterAIBrain = new GameCharacterAIBrain(this);
                }

                this.GameCharacterAIBrain.update();
            }

            if (this.sprite != null) {
                this.legsSprite = this.sprite;
            }

            if (!this.isDead() || this.current != null && this.current.getMovingObjects().contains(this)) {
                if (!GameClient.bClient
                    && !this.m_invisible
                    && this.getCurrentSquare().getTrapPositionX() > -1
                    && this.getCurrentSquare().getTrapPositionY() > -1
                    && this.getCurrentSquare().getTrapPositionZ() > -1) {
                    this.getCurrentSquare().explodeTrap();
                }

                if (this.getBodyDamage() != null && this.getCurrentBuilding() != null && this.getCurrentBuilding().isToxic()) {
                    float float0 = GameTime.getInstance().getMultiplier() / 1.6F;
                    if (this.getStats().getFatigue() < 1.0F) {
                        this.getStats().setFatigue(this.getStats().getFatigue() + 1.0E-4F * float0);
                    }

                    if (this.getStats().getFatigue() > 0.8) {
                        this.getBodyDamage().getBodyPart(BodyPartType.Head).ReduceHealth(0.1F * float0);
                    }

                    this.getBodyDamage().getBodyPart(BodyPartType.Torso_Upper).ReduceHealth(0.1F * float0);
                }

                if (this.lungeFallTimer > 0.0F) {
                    this.lungeFallTimer = this.lungeFallTimer - GameTime.getInstance().getMultiplier() / 1.6F;
                }

                if (this.getMeleeDelay() > 0.0F) {
                    this.setMeleeDelay(this.getMeleeDelay() - 0.625F * GameTime.getInstance().getMultiplier());
                }

                if (this.getRecoilDelay() > 0.0F) {
                    this.setRecoilDelay(this.getRecoilDelay() - 0.625F * GameTime.getInstance().getMultiplier());
                }

                this.sx = 0.0F;
                this.sy = 0.0F;
                if (this.current.getRoom() != null
                    && this.current.getRoom().building.def.bAlarmed
                    && (!this.isZombie() || Core.bTutorial)
                    && !GameClient.bClient) {
                    boolean boolean0 = false;
                    if (this instanceof IsoPlayer && (((IsoPlayer)this).isInvisible() || ((IsoPlayer)this).isGhostMode())) {
                        boolean0 = true;
                    }

                    if (!boolean0) {
                        AmbientStreamManager.instance.doAlarm(this.current.getRoom().def);
                    }
                }

                this.updateSeenVisibility();
                this.llx = this.getLx();
                this.lly = this.getLy();
                this.setLx(this.getX());
                this.setLy(this.getY());
                this.setLz(this.getZ());
                this.updateBeardAndHair();
                this.updateFalling();
                if (this.descriptor != null) {
                    this.descriptor.Instance = this;
                }

                if (!this.isZombie()) {
                    if (this.Traits.Agoraphobic.isSet() && !this.getCurrentSquare().isInARoom()) {
                        this.stats.Panic = this.stats.Panic + 0.5F * (GameTime.getInstance().getMultiplier() / 1.6F);
                    }

                    if (this.Traits.Claustophobic.isSet() && this.getCurrentSquare().isInARoom()) {
                        int int0 = this.getCurrentSquare().getRoomSize();
                        if (int0 > 0) {
                            float float1 = 1.0F;
                            float1 = 1.0F - int0 / 70.0F;
                            if (float1 < 0.0F) {
                                float1 = 0.0F;
                            }

                            float float2 = 0.6F * float1 * (GameTime.getInstance().getMultiplier() / 1.6F);
                            if (float2 > 0.6F) {
                                float2 = 0.6F;
                            }

                            this.stats.Panic += float2;
                        }
                    }

                    if (this.Moodles != null) {
                        this.Moodles.Update();
                    }

                    if (this.Asleep) {
                        this.BetaEffect = 0.0F;
                        this.SleepingTabletEffect = 0.0F;
                        this.StopAllActionQueue();
                    }

                    if (this.BetaEffect > 0.0F) {
                        this.BetaEffect = this.BetaEffect - GameTime.getInstance().getMultiplier() / 1.6F;
                        this.stats.Panic = this.stats.Panic - 0.6F * (GameTime.getInstance().getMultiplier() / 1.6F);
                        if (this.stats.Panic < 0.0F) {
                            this.stats.Panic = 0.0F;
                        }
                    } else {
                        this.BetaDelta = 0.0F;
                    }

                    if (this.DepressFirstTakeTime > 0.0F || this.DepressEffect > 0.0F) {
                        this.DepressFirstTakeTime = this.DepressFirstTakeTime - GameTime.getInstance().getMultiplier() / 1.6F;
                        if (this.DepressFirstTakeTime < 0.0F) {
                            this.DepressFirstTakeTime = -1.0F;
                            this.DepressEffect = this.DepressEffect - GameTime.getInstance().getMultiplier() / 1.6F;
                            this.getBodyDamage()
                                .setUnhappynessLevel(this.getBodyDamage().getUnhappynessLevel() - 0.03F * (GameTime.getInstance().getMultiplier() / 1.6F));
                            if (this.getBodyDamage().getUnhappynessLevel() < 0.0F) {
                                this.getBodyDamage().setUnhappynessLevel(0.0F);
                            }
                        }
                    }

                    if (this.DepressEffect < 0.0F) {
                        this.DepressEffect = 0.0F;
                    }

                    if (this.SleepingTabletEffect > 0.0F) {
                        this.SleepingTabletEffect = this.SleepingTabletEffect - GameTime.getInstance().getMultiplier() / 1.6F;
                        this.stats.fatigue = this.stats.fatigue + 0.0016666667F * this.SleepingTabletDelta * (GameTime.getInstance().getMultiplier() / 1.6F);
                    } else {
                        this.SleepingTabletDelta = 0.0F;
                    }

                    int int1 = this.Moodles.getMoodleLevel(MoodleType.Panic);
                    if (int1 == 2) {
                        this.stats.Sanity -= 3.2E-7F;
                    } else if (int1 == 3) {
                        this.stats.Sanity -= 4.8000004E-7F;
                    } else if (int1 == 4) {
                        this.stats.Sanity -= 8.0E-7F;
                    } else if (int1 == 0) {
                        this.stats.Sanity += 1.0E-7F;
                    }

                    int int2 = this.Moodles.getMoodleLevel(MoodleType.Tired);
                    if (int2 == 4) {
                        this.stats.Sanity -= 2.0E-6F;
                    }

                    if (this.stats.Sanity < 0.0F) {
                        this.stats.Sanity = 0.0F;
                    }

                    if (this.stats.Sanity > 1.0F) {
                        this.stats.Sanity = 1.0F;
                    }
                }

                if (!this.CharacterActions.isEmpty()) {
                    BaseAction baseAction = this.CharacterActions.get(0);
                    boolean boolean1 = baseAction.valid();
                    if (boolean1 && !baseAction.bStarted) {
                        baseAction.waitToStart();
                    } else if (boolean1 && !baseAction.finished() && !baseAction.forceComplete && !baseAction.forceStop) {
                        baseAction.update();
                    }

                    if (!boolean1 || baseAction.finished() || baseAction.forceComplete || baseAction.forceStop) {
                        if (baseAction.finished() || baseAction.forceComplete) {
                            baseAction.perform();
                            boolean1 = true;
                        }

                        if (baseAction.finished() && !baseAction.loopAction || baseAction.forceComplete || baseAction.forceStop || !boolean1) {
                            if (baseAction.bStarted && (baseAction.forceStop || !boolean1)) {
                                baseAction.stop();
                            }

                            this.CharacterActions.removeElement(baseAction);
                            if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
                                UIManager.getProgressBar(((IsoPlayer)this).getPlayerNum()).setValue(0.0F);
                            }
                        }
                    }

                    for (int int3 = 0; int3 < this.EnemyList.size(); int3++) {
                        IsoGameCharacter character1 = this.EnemyList.get(int3);
                        if (character1.isDead()) {
                            this.EnemyList.remove(character1);
                            int3--;
                        }
                    }
                }

                if (SystemDisabler.doCharacterStats && this.BodyDamage != null) {
                    this.BodyDamage.Update();
                    this.updateBandages();
                }

                if (this == IsoPlayer.getInstance()) {
                    if (this.leftHandItem != null && this.leftHandItem.getUses() <= 0) {
                        this.leftHandItem = null;
                    }

                    if (this.rightHandItem != null && this.rightHandItem.getUses() <= 0) {
                        this.rightHandItem = null;
                    }
                }

                if (SystemDisabler.doCharacterStats) {
                    this.calculateStats();
                }

                this.moveForwardVec.x = 0.0F;
                this.moveForwardVec.y = 0.0F;
                if (!this.Asleep || !(this instanceof IsoPlayer)) {
                    this.setLx(this.getX());
                    this.setLy(this.getY());
                    this.setLz(this.getZ());
                    this.square = this.getCurrentSquare();
                    if (this.sprite != null) {
                        if (!this.bUseParts) {
                            this.sprite.update(this.def);
                        } else {
                            this.legsSprite.update(this.def);
                        }
                    }

                    this.setStateEventDelayTimer(this.getStateEventDelayTimer() - GameTime.getInstance().getMultiplier() / 1.6F);
                }

                this.stateMachine.update();
                if (this.isZombie() && VirtualZombieManager.instance.isReused((IsoZombie)this)) {
                    DebugLog.log(DebugType.Zombie, "Zombie added to ReusableZombies after stateMachine.update - RETURNING " + this);
                } else {
                    if (this instanceof IsoPlayer) {
                        this.ensureOnTile();
                    }

                    if ((this instanceof IsoPlayer || this instanceof IsoSurvivor)
                        && this.RemoteID == -1
                        && this instanceof IsoPlayer
                        && ((IsoPlayer)this).isLocalPlayer()) {
                        RainManager.SetPlayerLocation(((IsoPlayer)this).getPlayerNum(), this.getCurrentSquare());
                    }

                    this.FireCheck();
                    this.SpreadFire();
                    this.ReduceHealthWhenBurning();
                    this.updateTextObjects();
                    if (this.stateMachine.getCurrent() == StaggerBackState.instance()) {
                        if (this.getStateEventDelayTimer() > 20.0F) {
                            this.BloodImpactX = this.getX();
                            this.BloodImpactY = this.getY();
                            this.BloodImpactZ = this.getZ();
                        }
                    } else {
                        this.BloodImpactX = this.getX();
                        this.BloodImpactY = this.getY();
                        this.BloodImpactZ = this.getZ();
                    }

                    if (!this.isZombie()) {
                        this.recursiveItemUpdater(this.inventory);
                    }

                    this.LastZombieKills = this.ZombieKills;
                    if (this.AttachedAnimSprite != null) {
                        int int4 = this.AttachedAnimSprite.size();

                        for (int int5 = 0; int5 < int4; int5++) {
                            IsoSpriteInstance spriteInstance = this.AttachedAnimSprite.get(int5);
                            IsoSprite sprite = spriteInstance.parentSprite;
                            spriteInstance.update();
                            spriteInstance.Frame = spriteInstance.Frame
                                + spriteInstance.AnimFrameIncrease * (GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F);
                            if ((int)spriteInstance.Frame >= sprite.CurrentAnim.Frames.size() && sprite.Loop && spriteInstance.Looped) {
                                spriteInstance.Frame = 0.0F;
                            }
                        }
                    }

                    if (this.isGodMod()) {
                        this.getStats().setFatigue(0.0F);
                        this.getStats().setEndurance(1.0F);
                        this.getBodyDamage().setTemperature(37.0F);
                        this.getStats().setHunger(0.0F);
                    }

                    this.updateMovementMomentum();
                    if (this.effectiveEdibleBuffTimer > 0.0F) {
                        this.effectiveEdibleBuffTimer = this.effectiveEdibleBuffTimer - GameTime.getInstance().getMultiplier() * 0.015F;
                        if (this.effectiveEdibleBuffTimer < 0.0F) {
                            this.effectiveEdibleBuffTimer = 0.0F;
                        }
                    }

                    if (!GameServer.bServer || GameClient.bClient) {
                        this.updateDirt();
                    }
                }
            }
        }
    }

    private void updateSeenVisibility() {
        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            this.updateSeenVisibility(int0);
        }
    }

    private void updateSeenVisibility(int int0) {
        IsoPlayer player = IsoPlayer.players[int0];
        if (player != null) {
            this.IsVisibleToPlayer[int0] = this.TestIfSeen(int0);
            if (!this.IsVisibleToPlayer[int0]) {
                if (!(this instanceof IsoPlayer)) {
                    if (!player.isSeeEveryone()) {
                        this.setTargetAlpha(int0, 0.0F);
                    }
                }
            }
        }
    }

    private void recursiveItemUpdater(ItemContainer container) {
        for (int int0 = 0; int0 < container.Items.size(); int0++) {
            InventoryItem item = container.Items.get(int0);
            if (item instanceof InventoryContainer) {
                this.recursiveItemUpdater((InventoryContainer)item);
            }

            if (item instanceof IUpdater) {
                item.update();
            }
        }
    }

    private void recursiveItemUpdater(InventoryContainer inventoryContainer) {
        for (int int0 = 0; int0 < inventoryContainer.getInventory().getItems().size(); int0++) {
            InventoryItem item = inventoryContainer.getInventory().getItems().get(int0);
            if (item instanceof InventoryContainer) {
                this.recursiveItemUpdater((InventoryContainer)item);
            }

            if (item instanceof IUpdater) {
                item.update();
            }
        }
    }

    private void updateDirt() {
        if (!this.isZombie() && this.getBodyDamage() != null) {
            int int0 = 0;
            if (this.isRunning() && Rand.NextBool(Rand.AdjustForFramerate(3500))) {
                int0 = 1;
            }

            if (this.isSprinting() && Rand.NextBool(Rand.AdjustForFramerate(2500))) {
                int0 += Rand.Next(1, 3);
            }

            if (this.getBodyDamage().getTemperature() > 37.0F && Rand.NextBool(Rand.AdjustForFramerate(5000))) {
                int0++;
            }

            if (this.getBodyDamage().getTemperature() > 38.0F && Rand.NextBool(Rand.AdjustForFramerate(3000))) {
                int0++;
            }

            float float0 = this.square == null ? 0.0F : this.square.getPuddlesInGround();
            if (this.isMoving() && float0 > 0.09F && Rand.NextBool(Rand.AdjustForFramerate(1500))) {
                int0++;
            }

            if (int0 > 0) {
                this.addDirt(null, int0, true);
            }

            IsoPlayer player = Type.tryCastTo(this, IsoPlayer.class);
            if (player != null && player.isPlayerMoving() || player == null && this.isMoving()) {
                int0 = 0;
                if (float0 > 0.09F && Rand.NextBool(Rand.AdjustForFramerate(1500))) {
                    int0++;
                }

                if (this.isInTrees() && Rand.NextBool(Rand.AdjustForFramerate(1500))) {
                    int0++;
                }

                if (int0 > 0) {
                    this.addDirt(null, int0, false);
                }
            }
        }
    }

    protected void updateMovementMomentum() {
        float float0 = GameTime.instance.getTimeDelta();
        if (this.isPlayerMoving() && !this.isAiming()) {
            float float1 = this.m_momentumScalar * 0.55F;
            if (float1 >= 0.55F) {
                this.m_momentumScalar = 1.0F;
                return;
            }

            float float2 = float1 + float0;
            float float3 = float2 / 0.55F;
            this.m_momentumScalar = PZMath.clamp(float3, 0.0F, 1.0F);
        } else {
            float float4 = (1.0F - this.m_momentumScalar) * 0.25F;
            if (float4 >= 0.25F) {
                this.m_momentumScalar = 0.0F;
                return;
            }

            float float5 = float4 + float0;
            float float6 = float5 / 0.25F;
            float float7 = PZMath.clamp(float6, 0.0F, 1.0F);
            this.m_momentumScalar = 1.0F - float7;
        }
    }

    @Override
    public double getHoursSurvived() {
        return GameTime.instance.getWorldAgeHours();
    }

    private void updateBeardAndHair() {
        if (!this.isZombie()) {
            if (!(this instanceof IsoPlayer) || ((IsoPlayer)this).isLocalPlayer()) {
                float float0 = (float)this.getHoursSurvived();
                if (this.beardGrowTiming < 0.0F || this.beardGrowTiming > float0) {
                    this.beardGrowTiming = float0;
                }

                if (this.hairGrowTiming < 0.0F || this.hairGrowTiming > float0) {
                    this.hairGrowTiming = float0;
                }

                boolean boolean0 = !GameClient.bClient && !GameServer.bServer
                    || ServerOptions.instance.SleepAllowed.getValue() && ServerOptions.instance.SleepNeeded.getValue();
                boolean boolean1 = false;
                if ((this.isAsleep() || !boolean0) && float0 - this.beardGrowTiming > 120.0F) {
                    this.beardGrowTiming = float0;
                    BeardStyle beardStyle = BeardStyles.instance.FindStyle(((HumanVisual)this.getVisual()).getBeardModel());
                    int int0 = 1;
                    if (beardStyle != null) {
                        int0 = beardStyle.level;
                    }

                    ArrayList arrayList0 = BeardStyles.instance.getAllStyles();

                    for (int int1 = 0; int1 < arrayList0.size(); int1++) {
                        if (((BeardStyle)arrayList0.get(int1)).growReference && ((BeardStyle)arrayList0.get(int1)).level == int0 + 1) {
                            ((HumanVisual)this.getVisual()).setBeardModel(((BeardStyle)arrayList0.get(int1)).name);
                            boolean1 = true;
                            break;
                        }
                    }
                }

                if ((this.isAsleep() || !boolean0) && float0 - this.hairGrowTiming > 480.0F) {
                    this.hairGrowTiming = float0;
                    HairStyle hairStyle0 = HairStyles.instance.FindMaleStyle(((HumanVisual)this.getVisual()).getHairModel());
                    if (this.isFemale()) {
                        hairStyle0 = HairStyles.instance.FindFemaleStyle(((HumanVisual)this.getVisual()).getHairModel());
                    }

                    int int2 = 1;
                    if (hairStyle0 != null) {
                        int2 = hairStyle0.level;
                    }

                    ArrayList arrayList1 = HairStyles.instance.m_MaleStyles;
                    if (this.isFemale()) {
                        arrayList1 = HairStyles.instance.m_FemaleStyles;
                    }

                    for (int int3 = 0; int3 < arrayList1.size(); int3++) {
                        HairStyle hairStyle1 = (HairStyle)arrayList1.get(int3);
                        if (hairStyle1.growReference && hairStyle1.level == int2 + 1) {
                            ((HumanVisual)this.getVisual()).setHairModel(hairStyle1.name);
                            ((HumanVisual)this.getVisual()).setNonAttachedHair(null);
                            boolean1 = true;
                            break;
                        }
                    }
                }

                if (boolean1) {
                    this.resetModelNextFrame();
                    LuaEventManager.triggerEvent("OnClothingUpdated", this);
                    if (GameClient.bClient) {
                        GameClient.instance.sendVisual((IsoPlayer)this);
                    }
                }
            }
        }
    }

    private void updateFalling() {
        if (this instanceof IsoPlayer && !this.isClimbing()) {
            IsoRoofFixer.FixRoofsAt(this.current);
        }

        if (this.isSeatedInVehicle()) {
            this.fallTime = 0.0F;
            this.lastFallSpeed = 0.0F;
            this.bFalling = false;
            this.wasOnStairs = false;
        } else {
            if (this.z > 0.0F) {
                IsoDirections directions = IsoDirections.Max;
                if (!this.isZombie() && this.isClimbing()) {
                    if (this.current.Is(IsoFlagType.climbSheetW) || this.current.Is(IsoFlagType.climbSheetTopW)) {
                        directions = IsoDirections.W;
                    }

                    if (this.current.Is(IsoFlagType.climbSheetE) || this.current.Is(IsoFlagType.climbSheetTopE)) {
                        directions = IsoDirections.E;
                    }

                    if (this.current.Is(IsoFlagType.climbSheetN) || this.current.Is(IsoFlagType.climbSheetTopN)) {
                        directions = IsoDirections.N;
                    }

                    if (this.current.Is(IsoFlagType.climbSheetS) || this.current.Is(IsoFlagType.climbSheetTopS)) {
                        directions = IsoDirections.S;
                    }
                }

                float float0 = 0.125F * (GameTime.getInstance().getMultiplier() / 1.6F);
                if (this.bClimbing) {
                    float0 = 0.0F;
                }

                if (this.getCurrentState() == ClimbOverFenceState.instance() || this.getCurrentState() == ClimbThroughWindowState.instance()) {
                    this.fallTime = 0.0F;
                    float0 = 0.0F;
                }

                this.lastFallSpeed = float0;
                if (!this.current.TreatAsSolidFloor()) {
                    if (directions != IsoDirections.Max) {
                        this.dir = directions;
                    }

                    float float1 = 6.0F * (GameTime.getInstance().getMultiplier() / 1.6F);
                    float float2 = this.getHeightAboveFloor();
                    if (float0 > float2) {
                        float1 *= float2 / float0;
                    }

                    this.fallTime += float1;
                    if (directions != IsoDirections.Max) {
                        this.fallTime = 0.0F;
                    }

                    if (this.fallTime < 20.0F && float2 < 0.2F) {
                        this.fallTime = 0.0F;
                    }

                    this.setZ(this.getZ() - float0);
                } else if (!(this.getZ() > (int)this.getZ()) && !(float0 < 0.0F)) {
                    this.DoLand();
                    this.fallTime = 0.0F;
                    this.bFalling = false;
                } else {
                    if (directions != IsoDirections.Max) {
                        this.dir = directions;
                    }

                    if (!this.current.HasStairs()) {
                        if (!this.wasOnStairs) {
                            float float3 = 6.0F * (GameTime.getInstance().getMultiplier() / 1.6F);
                            float float4 = this.getHeightAboveFloor();
                            if (float0 > float4) {
                                float3 *= float4 / float0;
                            }

                            this.fallTime += float3;
                            if (directions != IsoDirections.Max) {
                                this.fallTime = 0.0F;
                            }

                            this.setZ(this.getZ() - float0);
                            if (this.z < (int)this.llz) {
                                this.z = (int)this.llz;
                                this.DoLand();
                                this.fallTime = 0.0F;
                                this.bFalling = false;
                            }
                        } else {
                            this.wasOnStairs = false;
                        }
                    } else {
                        this.fallTime = 0.0F;
                        this.bFalling = false;
                        this.wasOnStairs = true;
                    }
                }
            } else {
                this.DoLand();
                this.fallTime = 0.0F;
                this.bFalling = false;
            }

            this.llz = this.lz;
        }
    }

    private float getHeightAboveFloor() {
        if (this.current == null) {
            return 1.0F;
        } else {
            if (this.current.HasStairs()) {
                float float0 = this.current.getApparentZ(this.x - (int)this.x, this.y - (int)this.y);
                if (this.getZ() >= float0) {
                    return this.getZ() - float0;
                }
            }

            if (this.current.TreatAsSolidFloor()) {
                return this.getZ() - (int)this.getZ();
            } else if (this.current.z == 0) {
                return this.getZ();
            } else {
                IsoGridSquare square = this.getCell().getGridSquare(this.current.x, this.current.y, this.current.z - 1);
                if (square != null && square.HasStairs()) {
                    float float1 = square.getApparentZ(this.x - (int)this.x, this.y - (int)this.y);
                    return this.getZ() - float1;
                } else {
                    return 1.0F;
                }
            }
        }
    }

    protected void updateMovementRates() {
    }

    protected float calculateIdleSpeed() {
        float float0 = 0.01F;
        float0 = (float)(float0 + this.getMoodles().getMoodleLevel(MoodleType.Endurance) * 2.5 / 10.0);
        return float0 * GameTime.getAnimSpeedFix();
    }

    public float calculateBaseSpeed() {
        float float0 = 0.8F;
        float float1 = 1.0F;
        if (this.getMoodles() != null) {
            float0 -= this.getMoodles().getMoodleLevel(MoodleType.Endurance) * 0.15F;
            float0 -= this.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 0.15F;
        }

        if (this.getMoodles().getMoodleLevel(MoodleType.Panic) >= 3 && this.Traits.AdrenalineJunkie.isSet()) {
            int int0 = this.getMoodles().getMoodleLevel(MoodleType.Panic) + 1;
            float0 += int0 / 20.0F;
        }

        for (int int1 = BodyPartType.ToIndex(BodyPartType.Torso_Upper); int1 < BodyPartType.ToIndex(BodyPartType.Neck) + 1; int1++) {
            BodyPart bodyPart0 = this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1));
            if (bodyPart0.HasInjury()) {
                float0 -= 0.1F;
            }

            if (bodyPart0.bandaged()) {
                float0 += 0.05F;
            }
        }

        BodyPart bodyPart1 = this.getBodyDamage().getBodyPart(BodyPartType.UpperLeg_L);
        if (bodyPart1.getAdditionalPain(true) > 20.0F) {
            float0 -= (bodyPart1.getAdditionalPain(true) - 20.0F) / 100.0F;
        }

        for (int int2 = 0; int2 < this.bagsWorn.size(); int2++) {
            InventoryContainer inventoryContainer = this.bagsWorn.get(int2);
            float1 += this.calcRunSpeedModByBag(inventoryContainer);
        }

        if (this.getPrimaryHandItem() != null && this.getPrimaryHandItem() instanceof InventoryContainer) {
            float1 += this.calcRunSpeedModByBag((InventoryContainer)this.getPrimaryHandItem());
        }

        if (this.getSecondaryHandItem() != null && this.getSecondaryHandItem() instanceof InventoryContainer) {
            float1 += this.calcRunSpeedModByBag((InventoryContainer)this.getSecondaryHandItem());
        }

        this.fullSpeedMod = this.runSpeedModifier + (float1 - 1.0F);
        return float0 * (1.0F - Math.abs(1.0F - this.fullSpeedMod) / 2.0F);
    }

    private float calcRunSpeedModByClothing() {
        float float0 = 0.0F;
        int int0 = 0;

        for (int int1 = 0; int1 < this.wornItems.size(); int1++) {
            InventoryItem item = this.wornItems.getItemByIndex(int1);
            if (item instanceof Clothing && ((Clothing)item).getRunSpeedModifier() != 1.0F) {
                float0 += ((Clothing)item).getRunSpeedModifier();
                int0++;
            }
        }

        if (float0 == 0.0F && int0 == 0) {
            float0 = 1.0F;
            int0 = 1;
        }

        if (this.getWornItem("Shoes") == null) {
            float0 *= 0.8F;
        }

        return float0 / int0;
    }

    private float calcRunSpeedModByBag(InventoryContainer inventoryContainer) {
        float float0 = inventoryContainer.getScriptItem().runSpeedModifier - 1.0F;
        float float1 = inventoryContainer.getContentsWeight() / inventoryContainer.getEffectiveCapacity(this);
        return float0 * (1.0F + float1 / 2.0F);
    }

    protected float calculateCombatSpeed() {
        boolean boolean0 = true;
        float float0 = 1.0F;
        HandWeapon weapon = null;
        if (this.getPrimaryHandItem() != null && this.getPrimaryHandItem() instanceof HandWeapon) {
            weapon = (HandWeapon)this.getPrimaryHandItem();
            float0 *= ((HandWeapon)this.getPrimaryHandItem()).getBaseSpeed();
        }

        WeaponType weaponType = WeaponType.getWeaponType(this);
        if (weapon != null && weapon.isTwoHandWeapon() && this.getSecondaryHandItem() != weapon) {
            float0 *= 0.77F;
        }

        if (weapon != null && this.Traits.Axeman.isSet() && weapon.getCategories().contains("Axe")) {
            float0 *= this.getChopTreeSpeed();
            boolean0 = false;
        }

        float0 -= this.getMoodles().getMoodleLevel(MoodleType.Endurance) * 0.07F;
        float0 -= this.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 0.07F;
        float0 += this.getWeaponLevel() * 0.03F;
        float0 += this.getPerkLevel(PerkFactory.Perks.Fitness) * 0.02F;
        if (this.getSecondaryHandItem() != null && this.getSecondaryHandItem() instanceof InventoryContainer) {
            float0 *= 0.95F;
        }

        float0 *= Rand.Next(1.1F, 1.2F);
        float0 *= this.combatSpeedModifier;
        float0 *= this.getArmsInjurySpeedModifier();
        if (this.getBodyDamage() != null && this.getBodyDamage().getThermoregulator() != null) {
            float0 *= this.getBodyDamage().getThermoregulator().getCombatModifier();
        }

        float0 = Math.min(1.6F, float0);
        float0 = Math.max(0.8F, float0);
        if (weapon != null && weapon.isTwoHandWeapon() && weaponType.type.equalsIgnoreCase("heavy")) {
            float0 *= 1.2F;
        }

        return float0 * (boolean0 ? GameTime.getAnimSpeedFix() : 1.0F);
    }

    private float getArmsInjurySpeedModifier() {
        float float0 = 1.0F;
        float float1 = 0.0F;
        BodyPart bodyPart = this.getBodyDamage().getBodyPart(BodyPartType.Hand_R);
        float1 = this.calculateInjurySpeed(bodyPart, true);
        if (float1 > 0.0F) {
            float0 -= float1;
        }

        bodyPart = this.getBodyDamage().getBodyPart(BodyPartType.ForeArm_R);
        float1 = this.calculateInjurySpeed(bodyPart, true);
        if (float1 > 0.0F) {
            float0 -= float1;
        }

        bodyPart = this.getBodyDamage().getBodyPart(BodyPartType.UpperArm_R);
        float1 = this.calculateInjurySpeed(bodyPart, true);
        if (float1 > 0.0F) {
            float0 -= float1;
        }

        return float0;
    }

    private float getFootInjurySpeedModifier() {
        float float0 = 0.0F;
        boolean boolean0 = true;
        float float1 = 0.0F;
        float float2 = 0.0F;

        for (int int0 = BodyPartType.ToIndex(BodyPartType.Groin); int0 < BodyPartType.ToIndex(BodyPartType.MAX); int0++) {
            float0 = this.calculateInjurySpeed(this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int0)), false);
            if (boolean0) {
                float1 += float0;
            } else {
                float2 += float0;
            }

            boolean0 = !boolean0;
        }

        return float1 > float2 ? -(float1 + float2) : float1 + float2;
    }

    private float calculateInjurySpeed(BodyPart bodyPart, boolean boolean0) {
        float float0 = bodyPart.getScratchSpeedModifier();
        float float1 = bodyPart.getCutSpeedModifier();
        float float2 = bodyPart.getBurnSpeedModifier();
        float float3 = bodyPart.getDeepWoundSpeedModifier();
        float float4 = 0.0F;
        if ((bodyPart.getType() == BodyPartType.Foot_L || bodyPart.getType() == BodyPartType.Foot_R)
            && (
                bodyPart.getBurnTime() > 5.0F
                    || bodyPart.getBiteTime() > 0.0F
                    || bodyPart.deepWounded()
                    || bodyPart.isSplint()
                    || bodyPart.getFractureTime() > 0.0F
                    || bodyPart.haveGlass()
            )) {
            float4 = 1.0F;
            if (bodyPart.bandaged()) {
                float4 = 0.7F;
            }

            if (bodyPart.getFractureTime() > 0.0F) {
                float4 = this.calcFractureInjurySpeed(bodyPart);
            }
        }

        if (bodyPart.haveBullet()) {
            return 1.0F;
        } else {
            if (bodyPart.getScratchTime() > 2.0F
                || bodyPart.getCutTime() > 5.0F
                || bodyPart.getBurnTime() > 0.0F
                || bodyPart.getDeepWoundTime() > 0.0F
                || bodyPart.isSplint()
                || bodyPart.getFractureTime() > 0.0F
                || bodyPart.getBiteTime() > 0.0F) {
                float4 += bodyPart.getScratchTime() / float0
                    + bodyPart.getCutTime() / float1
                    + bodyPart.getBurnTime() / float2
                    + bodyPart.getDeepWoundTime() / float3;
                float4 += bodyPart.getBiteTime() / 20.0F;
                if (bodyPart.bandaged()) {
                    float4 /= 2.0F;
                }

                if (bodyPart.getFractureTime() > 0.0F) {
                    float4 = this.calcFractureInjurySpeed(bodyPart);
                }
            }

            if (boolean0 && bodyPart.getPain() > 20.0F) {
                float4 += bodyPart.getPain() / 10.0F;
            }

            return float4;
        }
    }

    private float calcFractureInjurySpeed(BodyPart bodyPart) {
        float float0 = 0.4F;
        if (bodyPart.getFractureTime() > 10.0F) {
            float0 = 0.7F;
        }

        if (bodyPart.getFractureTime() > 20.0F) {
            float0 = 1.0F;
        }

        if (bodyPart.getSplintFactor() > 0.0F) {
            float0 -= 0.2F;
            float0 -= Math.min(bodyPart.getSplintFactor() / 10.0F, 0.8F);
        }

        return Math.max(0.0F, float0);
    }

    protected void calculateWalkSpeed() {
        if (!(this instanceof IsoPlayer) || ((IsoPlayer)this).isLocalPlayer()) {
            float float0 = 0.0F;
            float float1 = this.getFootInjurySpeedModifier();
            this.setVariable("WalkInjury", float1);
            float0 = this.calculateBaseSpeed();
            if (!this.bRunning && !this.bSprinting) {
                float0 *= this.walkSpeedModifier;
            } else {
                float0 -= 0.15F;
                float0 *= this.fullSpeedMod;
                float0 += this.getPerkLevel(PerkFactory.Perks.Sprinting) / 20.0F;
                float0 = (float)(float0 - Math.abs(float1 / 1.5));
                if ("Tutorial".equals(Core.GameMode)) {
                    float0 = Math.max(1.0F, float0);
                }
            }

            if (this.getSlowFactor() > 0.0F) {
                float0 *= 0.05F;
            }

            float0 = Math.min(1.0F, float0);
            if (this.getBodyDamage() != null && this.getBodyDamage().getThermoregulator() != null) {
                float0 *= this.getBodyDamage().getThermoregulator().getMovementModifier();
            }

            if (this.isAiming()) {
                float float2 = Math.min(0.9F + this.getPerkLevel(PerkFactory.Perks.Nimble) / 10.0F, 1.5F);
                float float3 = Math.min(float0 * 2.5F, 1.0F);
                float2 *= float3;
                float2 = Math.max(float2, 0.6F);
                this.setVariable("StrafeSpeed", float2 * GameTime.getAnimSpeedFix());
            }

            if (this.isInTreesNoBush()) {
                IsoGridSquare square = this.getCurrentSquare();
                if (square != null && square.Has(IsoObjectType.tree)) {
                    IsoTree tree = square.getTree();
                    if (tree != null) {
                        float0 *= tree.getSlowFactor(this);
                    }
                }
            }

            this.setVariable("WalkSpeed", float0 * GameTime.getAnimSpeedFix());
        }
    }

    public void updateSpeedModifiers() {
        this.runSpeedModifier = 1.0F;
        this.walkSpeedModifier = 1.0F;
        this.combatSpeedModifier = 1.0F;
        this.bagsWorn = new ArrayList<>();

        for (int int0 = 0; int0 < this.getWornItems().size(); int0++) {
            InventoryItem item0 = this.getWornItems().getItemByIndex(int0);
            if (item0 instanceof Clothing clothing) {
                this.combatSpeedModifier = this.combatSpeedModifier + (clothing.getCombatSpeedModifier() - 1.0F);
            }

            if (item0 instanceof InventoryContainer inventoryContainer) {
                this.combatSpeedModifier = this.combatSpeedModifier + (inventoryContainer.getScriptItem().combatSpeedModifier - 1.0F);
                this.bagsWorn.add(inventoryContainer);
            }
        }

        InventoryItem item1 = this.getWornItems().getItem("Shoes");
        if (item1 == null || item1.getCondition() == 0) {
            this.runSpeedModifier *= 0.85F;
            this.walkSpeedModifier *= 0.85F;
        }
    }

    public void DoFloorSplat(IsoGridSquare sq, String id, boolean bFlip, float offZ, float alpha) {
        if (sq != null) {
            sq.DirtySlice();
            IsoObject object0 = null;

            for (int int0 = 0; int0 < sq.getObjects().size(); int0++) {
                IsoObject object1 = sq.getObjects().get(int0);
                if (object1.sprite != null && object1.sprite.getProperties().Is(IsoFlagType.solidfloor) && object0 == null) {
                    object0 = object1;
                }
            }

            if (object0 != null
                && object0.sprite != null
                && (object0.sprite.getProperties().Is(IsoFlagType.vegitation) || object0.sprite.getProperties().Is(IsoFlagType.solidfloor))) {
                IsoSprite sprite = IsoSprite.getSprite(IsoSpriteManager.instance, id, 0);
                if (sprite == null) {
                    return;
                }

                if (object0.AttachedAnimSprite.size() > 7) {
                    return;
                }

                IsoSpriteInstance spriteInstance = IsoSpriteInstance.get(sprite);
                object0.AttachedAnimSprite.add(spriteInstance);
                object0.AttachedAnimSprite.get(object0.AttachedAnimSprite.size() - 1).Flip = bFlip;
                object0.AttachedAnimSprite.get(object0.AttachedAnimSprite.size() - 1).tintr = 0.5F + Rand.Next(100) / 2000.0F;
                object0.AttachedAnimSprite.get(object0.AttachedAnimSprite.size() - 1).tintg = 0.7F + Rand.Next(300) / 1000.0F;
                object0.AttachedAnimSprite.get(object0.AttachedAnimSprite.size() - 1).tintb = 0.7F + Rand.Next(300) / 1000.0F;
                object0.AttachedAnimSprite.get(object0.AttachedAnimSprite.size() - 1).SetAlpha(0.4F * alpha * 0.6F);
                object0.AttachedAnimSprite.get(object0.AttachedAnimSprite.size() - 1).SetTargetAlpha(0.4F * alpha * 0.6F);
                object0.AttachedAnimSprite.get(object0.AttachedAnimSprite.size() - 1).offZ = -offZ;
                object0.AttachedAnimSprite.get(object0.AttachedAnimSprite.size() - 1).offX = 0.0F;
            }
        }
    }

    void DoSplat(IsoGridSquare square, String string, boolean boolean0, IsoFlagType flagType, float float0, float float1, float float2) {
        if (square != null) {
            square.DoSplat(string, boolean0, flagType, float0, float1, float2);
        }
    }

    @Override
    public boolean onMouseLeftClick(int x, int y) {
        if (IsoCamera.CamCharacter != IsoPlayer.getInstance() && Core.bDebug) {
            IsoCamera.CamCharacter = this;
        }

        return super.onMouseLeftClick(x, y);
    }

    protected void calculateStats() {
        if (GameServer.bServer) {
            this.stats.fatigue = 0.0F;
        } else if (GameClient.bClient && (!ServerOptions.instance.SleepAllowed.getValue() || !ServerOptions.instance.SleepNeeded.getValue())) {
            this.stats.fatigue = 0.0F;
        }

        if (!LuaHookManager.TriggerHook("CalculateStats", this)) {
            this.updateEndurance();
            this.updateTripping();
            this.updateThirst();
            this.updateStress();
            this.updateStats_WakeState();
            this.stats.endurance = PZMath.clamp(this.stats.endurance, 0.0F, 1.0F);
            this.stats.hunger = PZMath.clamp(this.stats.hunger, 0.0F, 1.0F);
            this.stats.stress = PZMath.clamp(this.stats.stress, 0.0F, 1.0F);
            this.stats.fatigue = PZMath.clamp(this.stats.fatigue, 0.0F, 1.0F);
            this.updateMorale();
            this.updateFitness();
        }
    }

    protected void updateStats_WakeState() {
        if (IsoPlayer.getInstance() == this && this.Asleep) {
            this.updateStats_Sleeping();
        } else {
            this.updateStats_Awake();
        }
    }

    protected void updateStats_Sleeping() {
    }

    protected void updateStats_Awake() {
        this.stats.stress = (float)(
            this.stats.stress - ZomboidGlobals.StressReduction * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay()
        );
        float float0 = 1.0F - this.stats.endurance;
        if (float0 < 0.3F) {
            float0 = 0.3F;
        }

        float float1 = 1.0F;
        if (this.Traits.NeedsLessSleep.isSet()) {
            float1 = 0.7F;
        }

        if (this.Traits.NeedsMoreSleep.isSet()) {
            float1 = 1.3F;
        }

        double double0 = SandboxOptions.instance.getStatsDecreaseMultiplier();
        if (double0 < 1.0) {
            double0 = 1.0;
        }

        this.stats.fatigue = (float)(
            this.stats.fatigue
                + ZomboidGlobals.FatigueIncrease
                    * SandboxOptions.instance.getStatsDecreaseMultiplier()
                    * float0
                    * GameTime.instance.getMultiplier()
                    * GameTime.instance.getDeltaMinutesPerDay()
                    * float1
                    * this.getFatiqueMultiplier()
        );
        float float2 = this.getAppetiteMultiplier();
        if ((!(this instanceof IsoPlayer) || !((IsoPlayer)this).IsRunning() || !this.isPlayerMoving()) && !this.isCurrentState(SwipeStatePlayer.instance())) {
            if (this.Moodles.getMoodleLevel(MoodleType.FoodEaten) == 0) {
                this.stats.hunger = (float)(
                    this.stats.hunger
                        + ZomboidGlobals.HungerIncrease
                            * SandboxOptions.instance.getStatsDecreaseMultiplier()
                            * float2
                            * GameTime.instance.getMultiplier()
                            * GameTime.instance.getDeltaMinutesPerDay()
                            * this.getHungerMultiplier()
                );
            } else {
                this.stats.hunger = (float)(
                    this.stats.hunger
                        + (float)ZomboidGlobals.HungerIncreaseWhenWellFed
                            * SandboxOptions.instance.getStatsDecreaseMultiplier()
                            * GameTime.instance.getMultiplier()
                            * GameTime.instance.getDeltaMinutesPerDay()
                            * this.getHungerMultiplier()
                );
            }
        } else if (this.Moodles.getMoodleLevel(MoodleType.FoodEaten) == 0) {
            this.stats.hunger = (float)(
                this.stats.hunger
                    + ZomboidGlobals.HungerIncreaseWhenExercise
                        / 3.0
                        * SandboxOptions.instance.getStatsDecreaseMultiplier()
                        * float2
                        * GameTime.instance.getMultiplier()
                        * GameTime.instance.getDeltaMinutesPerDay()
                        * this.getHungerMultiplier()
            );
        } else {
            this.stats.hunger = (float)(
                this.stats.hunger
                    + ZomboidGlobals.HungerIncreaseWhenExercise
                        * SandboxOptions.instance.getStatsDecreaseMultiplier()
                        * float2
                        * GameTime.instance.getMultiplier()
                        * GameTime.instance.getDeltaMinutesPerDay()
                        * this.getHungerMultiplier()
            );
        }

        if (this.getCurrentSquare() == this.getLastSquare() && !this.isReading()) {
            this.stats.idleboredom = this.stats.idleboredom + 5.0E-5F * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay();
            this.stats.idleboredom = this.stats.idleboredom + 0.00125F * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay();
        }

        if (this.getCurrentSquare() != null
            && this.getLastSquare() != null
            && this.getCurrentSquare().getRoom() == this.getLastSquare().getRoom()
            && this.getCurrentSquare().getRoom() != null
            && !this.isReading()) {
            this.stats.idleboredom = this.stats.idleboredom + 1.0E-4F * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay();
            this.stats.idleboredom = this.stats.idleboredom + 0.00125F * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay();
        }
    }

    private void updateMorale() {
        float float0 = 1.0F - this.stats.getStress() - 0.5F;
        float0 *= 1.0E-4F;
        if (float0 > 0.0F) {
            float0 += 0.5F;
        }

        this.stats.morale += float0;
        this.stats.morale = PZMath.clamp(this.stats.morale, 0.0F, 1.0F);
    }

    private void updateFitness() {
        this.stats.fitness = this.getPerkLevel(PerkFactory.Perks.Fitness) / 5.0F - 1.0F;
        if (this.stats.fitness > 1.0F) {
            this.stats.fitness = 1.0F;
        }

        if (this.stats.fitness < -1.0F) {
            this.stats.fitness = -1.0F;
        }
    }

    private void updateTripping() {
        if (this.stats.Tripping) {
            this.stats.TrippingRotAngle += 0.06F;
        } else {
            this.stats.TrippingRotAngle += 0.0F;
        }
    }

    protected float getAppetiteMultiplier() {
        float float0 = 1.0F - this.stats.hunger;
        if (this.Traits.HeartyAppitite.isSet()) {
            float0 *= 1.5F;
        }

        if (this.Traits.LightEater.isSet()) {
            float0 *= 0.75F;
        }

        return float0;
    }

    private void updateStress() {
        float float0 = 1.0F;
        if (this.Traits.Cowardly.isSet()) {
            float0 = 2.0F;
        }

        if (this.Traits.Brave.isSet()) {
            float0 = 0.3F;
        }

        if (this.stats.Panic > 100.0F) {
            this.stats.Panic = 100.0F;
        }

        this.stats.stress = (float)(
            this.stats.stress
                + WorldSoundManager.instance.getStressFromSounds((int)this.getX(), (int)this.getY(), (int)this.getZ())
                    * ZomboidGlobals.StressFromSoundsMultiplier
        );
        if (this.BodyDamage.getNumPartsBitten() > 0) {
            this.stats.stress = (float)(
                this.stats.stress + ZomboidGlobals.StressFromBiteOrScratch * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay()
            );
        }

        if (this.BodyDamage.getNumPartsScratched() > 0) {
            this.stats.stress = (float)(
                this.stats.stress + ZomboidGlobals.StressFromBiteOrScratch * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay()
            );
        }

        if (this.BodyDamage.IsInfected() || this.BodyDamage.IsFakeInfected()) {
            this.stats.stress = (float)(
                this.stats.stress + ZomboidGlobals.StressFromBiteOrScratch * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay()
            );
        }

        if (this.Traits.Hemophobic.isSet()) {
            this.stats.stress = (float)(
                this.stats.stress
                    + this.getTotalBlood()
                        * ZomboidGlobals.StressFromHemophobic
                        * (GameTime.instance.getMultiplier() / 0.8F)
                        * GameTime.instance.getDeltaMinutesPerDay()
            );
        }

        if (this.Traits.Brooding.isSet()) {
            this.stats.Anger = (float)(
                this.stats.Anger
                    - ZomboidGlobals.AngerDecrease
                        * ZomboidGlobals.BroodingAngerDecreaseMultiplier
                        * GameTime.instance.getMultiplier()
                        * GameTime.instance.getDeltaMinutesPerDay()
            );
        } else {
            this.stats.Anger = (float)(
                this.stats.Anger - ZomboidGlobals.AngerDecrease * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay()
            );
        }

        this.stats.Anger = PZMath.clamp(this.stats.Anger, 0.0F, 1.0F);
    }

    private void updateEndurance() {
        this.stats.endurance = PZMath.clamp(this.stats.endurance, 0.0F, 1.0F);
        this.stats.endurancelast = this.stats.endurance;
        if (this.isUnlimitedEndurance()) {
            this.stats.endurance = 1.0F;
        }
    }

    private void updateThirst() {
        float float0 = 1.0F;
        if (this.Traits.HighThirst.isSet()) {
            float0 = (float)(float0 * 2.0);
        }

        if (this.Traits.LowThirst.isSet()) {
            float0 = (float)(float0 * 0.5);
        }

        if (IsoPlayer.getInstance() == this && !IsoPlayer.getInstance().isGhostMode()) {
            if (this.Asleep) {
                this.stats.thirst = (float)(
                    this.stats.thirst
                        + ZomboidGlobals.ThirstSleepingIncrease
                            * SandboxOptions.instance.getStatsDecreaseMultiplier()
                            * GameTime.instance.getMultiplier()
                            * GameTime.instance.getDeltaMinutesPerDay()
                            * float0
                );
            } else {
                this.stats.thirst = (float)(
                    this.stats.thirst
                        + ZomboidGlobals.ThirstIncrease
                            * SandboxOptions.instance.getStatsDecreaseMultiplier()
                            * GameTime.instance.getMultiplier()
                            * this.getRunningThirstReduction()
                            * GameTime.instance.getDeltaMinutesPerDay()
                            * float0
                            * this.getThirstMultiplier()
                );
            }

            if (this.stats.thirst > 1.0F) {
                this.stats.thirst = 1.0F;
            }
        }

        this.autoDrink();
    }

    private double getRunningThirstReduction() {
        return this == IsoPlayer.getInstance() && IsoPlayer.getInstance().IsRunning() ? 1.2 : 1.0;
    }

    public void faceLocation(float x, float y) {
        tempo.x = x + 0.5F;
        tempo.y = y + 0.5F;
        tempo.x = tempo.x - this.getX();
        tempo.y = tempo.y - this.getY();
        this.DirectionFromVector(tempo);
        this.getVectorFromDirection(this.m_forwardDirection);
        AnimationPlayer animationPlayer = this.getAnimationPlayer();
        if (animationPlayer != null && animationPlayer.isReady()) {
            animationPlayer.UpdateDir(this);
        }
    }

    public void faceLocationF(float x, float y) {
        tempo.x = x;
        tempo.y = y;
        tempo.x = tempo.x - this.getX();
        tempo.y = tempo.y - this.getY();
        if (tempo.getLengthSquared() != 0.0F) {
            this.DirectionFromVector(tempo);
            tempo.normalize();
            this.m_forwardDirection.set(tempo.x, tempo.y);
            AnimationPlayer animationPlayer = this.getAnimationPlayer();
            if (animationPlayer != null && animationPlayer.isReady()) {
                animationPlayer.UpdateDir(this);
            }
        }
    }

    public boolean isFacingLocation(float x, float y, float dot) {
        Vector2 vector0 = BaseVehicle.allocVector2().set(x - this.getX(), y - this.getY());
        vector0.normalize();
        Vector2 vector1 = this.getLookVector(BaseVehicle.allocVector2());
        float float0 = vector0.dot(vector1);
        BaseVehicle.releaseVector2(vector0);
        BaseVehicle.releaseVector2(vector1);
        return float0 >= dot;
    }

    public boolean isFacingObject(IsoObject object, float dot) {
        Vector2 vector = BaseVehicle.allocVector2();
        object.getFacingPosition(vector);
        boolean boolean0 = this.isFacingLocation(vector.x, vector.y, dot);
        BaseVehicle.releaseVector2(vector);
        return boolean0;
    }

    private void checkDrawWeaponPre(float var1, float var2, float var3, ColorInfo var4) {
        if (this.sprite != null) {
            if (this.sprite.CurrentAnim != null) {
                if (this.sprite.CurrentAnim.name != null) {
                    if (this.dir != IsoDirections.S
                        && this.dir != IsoDirections.SE
                        && this.dir != IsoDirections.E
                        && this.dir != IsoDirections.NE
                        && this.dir != IsoDirections.SW) {
                        if (this.sprite.CurrentAnim.name.contains("Attack_")) {
                            ;
                        }
                    }
                }
            }
        }
    }

    public void splatBlood(int dist, float alpha) {
        if (this.getCurrentSquare() != null) {
            this.getCurrentSquare().splatBlood(dist, alpha);
        }
    }

    @Override
    public boolean isOutside() {
        return this.getCurrentSquare() == null ? false : this.getCurrentSquare().isOutside();
    }

    @Override
    public boolean isFemale() {
        return this.bFemale;
    }

    @Override
    public void setFemale(boolean isFemale) {
        this.bFemale = isFemale;
    }

    @Override
    public boolean isZombie() {
        return false;
    }

    @Override
    public int getLastHitCount() {
        return this.lastHitCount;
    }

    @Override
    public void setLastHitCount(int hitCount) {
        this.lastHitCount = hitCount;
    }

    public int getSurvivorKills() {
        return this.SurvivorKills;
    }

    public void setSurvivorKills(int survivorKills) {
        this.SurvivorKills = survivorKills;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int _age) {
        this.age = _age;
    }

    public void exert(float f) {
        if (this.Traits.PlaysFootball.isSet()) {
            f *= 0.9F;
        }

        if (this.Traits.Jogger.isSet()) {
            f *= 0.9F;
        }

        this.stats.endurance -= f;
    }

    @Override
    public IsoGameCharacter.PerkInfo getPerkInfo(PerkFactory.Perk perk) {
        for (int int0 = 0; int0 < this.PerkList.size(); int0++) {
            IsoGameCharacter.PerkInfo perkInfo = this.PerkList.get(int0);
            if (perkInfo.perk == perk) {
                return perkInfo;
            }
        }

        return null;
    }

    @Override
    public boolean isEquipped(InventoryItem item) {
        return this.isEquippedClothing(item) || this.isHandItem(item);
    }

    @Override
    public boolean isEquippedClothing(InventoryItem item) {
        return this.wornItems.contains(item);
    }

    @Override
    public boolean isAttachedItem(InventoryItem item) {
        return this.getAttachedItems().contains(item);
    }

    @Override
    public void faceThisObject(IsoObject object) {
        if (object != null) {
            Vector2 vector = tempo;
            BaseVehicle vehiclex = Type.tryCastTo(object, BaseVehicle.class);
            BarricadeAble barricadeAble = Type.tryCastTo(object, BarricadeAble.class);
            if (vehiclex != null) {
                vehiclex.getFacingPosition(this, vector);
                vector.x = vector.x - this.getX();
                vector.y = vector.y - this.getY();
                this.DirectionFromVector(vector);
                vector.normalize();
                this.m_forwardDirection.set(vector.x, vector.y);
            } else if (barricadeAble != null && this.current == barricadeAble.getSquare()) {
                this.dir = barricadeAble.getNorth() ? IsoDirections.N : IsoDirections.W;
                this.getVectorFromDirection(this.m_forwardDirection);
            } else if (barricadeAble != null && this.current == barricadeAble.getOppositeSquare()) {
                this.dir = barricadeAble.getNorth() ? IsoDirections.S : IsoDirections.E;
                this.getVectorFromDirection(this.m_forwardDirection);
            } else {
                object.getFacingPosition(vector);
                vector.x = vector.x - this.getX();
                vector.y = vector.y - this.getY();
                this.DirectionFromVector(vector);
                this.getVectorFromDirection(this.m_forwardDirection);
            }

            AnimationPlayer animationPlayer = this.getAnimationPlayer();
            if (animationPlayer != null && animationPlayer.isReady()) {
                animationPlayer.UpdateDir(this);
            }
        }
    }

    @Override
    public void facePosition(int x, int y) {
        tempo.x = x;
        tempo.y = y;
        tempo.x = tempo.x - this.getX();
        tempo.y = tempo.y - this.getY();
        this.DirectionFromVector(tempo);
        this.getVectorFromDirection(this.m_forwardDirection);
        AnimationPlayer animationPlayer = this.getAnimationPlayer();
        if (animationPlayer != null && animationPlayer.isReady()) {
            animationPlayer.UpdateDir(this);
        }
    }

    @Override
    public void faceThisObjectAlt(IsoObject object) {
        if (object != null) {
            object.getFacingPositionAlt(tempo);
            tempo.x = tempo.x - this.getX();
            tempo.y = tempo.y - this.getY();
            this.DirectionFromVector(tempo);
            this.getVectorFromDirection(this.m_forwardDirection);
            AnimationPlayer animationPlayer = this.getAnimationPlayer();
            if (animationPlayer != null && animationPlayer.isReady()) {
                animationPlayer.UpdateDir(this);
            }
        }
    }

    public void setAnimated(boolean b) {
        this.legsSprite.Animate = true;
    }

    public void playHurtSound() {
        this.getEmitter().playVocals(this.getHurtSound());
    }

    public void playDeadSound() {
        if (this.isCloseKilled()) {
            this.getEmitter().playSoundImpl("HeadStab", this);
        } else {
            this.getEmitter().playSoundImpl("HeadSmash", this);
        }

        if (this.isZombie()) {
            ((IsoZombie)this).parameterZombieState.setState(ParameterZombieState.State.Death);
        }
    }

    @Override
    public void saveChange(String change, KahluaTable tbl, ByteBuffer bb) {
        super.saveChange(change, tbl, bb);
        if ("addItem".equals(change)) {
            if (tbl != null && tbl.rawget("item") instanceof InventoryItem) {
                InventoryItem item = (InventoryItem)tbl.rawget("item");

                try {
                    item.saveWithSize(bb, false);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        } else if ("addItemOfType".equals(change)) {
            if (tbl != null && tbl.rawget("type") instanceof String) {
                GameWindow.WriteStringUTF(bb, (String)tbl.rawget("type"));
                if (tbl.rawget("count") instanceof Double) {
                    bb.putShort(((Double)tbl.rawget("count")).shortValue());
                } else {
                    bb.putShort((short)1);
                }
            }
        } else if ("AddRandomDamageFromZombie".equals(change)) {
            if (tbl != null && tbl.rawget("zombie") instanceof Double) {
                bb.putShort(((Double)tbl.rawget("zombie")).shortValue());
            }
        } else if (!"AddZombieKill".equals(change)) {
            if ("DamageFromWeapon".equals(change)) {
                if (tbl != null && tbl.rawget("weapon") instanceof String) {
                    GameWindow.WriteStringUTF(bb, (String)tbl.rawget("weapon"));
                }
            } else if ("removeItem".equals(change)) {
                if (tbl != null && tbl.rawget("item") instanceof Double) {
                    bb.putInt(((Double)tbl.rawget("item")).intValue());
                }
            } else if ("removeItemID".equals(change)) {
                if (tbl != null && tbl.rawget("id") instanceof Double) {
                    bb.putInt(((Double)tbl.rawget("id")).intValue());
                }

                if (tbl != null && tbl.rawget("type") instanceof String) {
                    GameWindow.WriteStringUTF(bb, (String)tbl.rawget("type"));
                } else {
                    GameWindow.WriteStringUTF(bb, null);
                }
            } else if ("removeItemType".equals(change)) {
                if (tbl != null && tbl.rawget("type") instanceof String) {
                    GameWindow.WriteStringUTF(bb, (String)tbl.rawget("type"));
                    if (tbl.rawget("count") instanceof Double) {
                        bb.putShort(((Double)tbl.rawget("count")).shortValue());
                    } else {
                        bb.putShort((short)1);
                    }
                }
            } else if ("removeOneOf".equals(change)) {
                if (tbl != null && tbl.rawget("type") instanceof String) {
                    GameWindow.WriteStringUTF(bb, (String)tbl.rawget("type"));
                }
            } else if ("reanimatedID".equals(change)) {
                if (tbl != null && tbl.rawget("ID") instanceof Double) {
                    int int0 = ((Double)tbl.rawget("ID")).intValue();
                    bb.putInt(int0);
                }
            } else if ("Shove".equals(change)) {
                if (tbl != null && tbl.rawget("hitDirX") instanceof Double && tbl.rawget("hitDirY") instanceof Double && tbl.rawget("force") instanceof Double) {
                    bb.putFloat(((Double)tbl.rawget("hitDirX")).floatValue());
                    bb.putFloat(((Double)tbl.rawget("hitDirY")).floatValue());
                    bb.putFloat(((Double)tbl.rawget("force")).floatValue());
                }
            } else if ("addXp".equals(change)) {
                if (tbl != null && tbl.rawget("perk") instanceof Double && tbl.rawget("xp") instanceof Double) {
                    bb.putInt(((Double)tbl.rawget("perk")).intValue());
                    bb.putInt(((Double)tbl.rawget("xp")).intValue());
                    Object object = tbl.rawget("noMultiplier");
                    bb.put((byte)(Boolean.TRUE.equals(object) ? 1 : 0));
                }
            } else if (!"wakeUp".equals(change) && "mechanicActionDone".equals(change) && tbl != null) {
                bb.put((byte)(tbl.rawget("success") ? 1 : 0));
                bb.putInt(((Double)tbl.rawget("vehicleId")).intValue());
                GameWindow.WriteString(bb, (String)tbl.rawget("partId"));
                bb.put((byte)(tbl.rawget("installing") ? 1 : 0));
                bb.putLong(((Double)tbl.rawget("itemId")).longValue());
            }
        }
    }

    @Override
    public void loadChange(String change, ByteBuffer bb) {
        super.loadChange(change, bb);
        if ("addItem".equals(change)) {
            try {
                InventoryItem item0 = InventoryItem.loadItem(bb, 195);
                if (item0 != null) {
                    this.getInventory().AddItem(item0);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else if ("addItemOfType".equals(change)) {
            String string0 = GameWindow.ReadStringUTF(bb);
            short short0 = bb.getShort();

            for (int int0 = 0; int0 < short0; int0++) {
                this.getInventory().AddItem(string0);
            }
        } else if ("AddRandomDamageFromZombie".equals(change)) {
            short short1 = bb.getShort();
            IsoZombie zombie0 = GameClient.getZombie(short1);
            if (zombie0 != null && !this.isDead()) {
                this.getBodyDamage().AddRandomDamageFromZombie(zombie0, null);
                this.getBodyDamage().Update();
                if (this.isDead()) {
                    if (this.isFemale()) {
                        zombie0.getEmitter().playSound("FemaleBeingEatenDeath");
                    } else {
                        zombie0.getEmitter().playSound("MaleBeingEatenDeath");
                    }
                }
            }
        } else if ("AddZombieKill".equals(change)) {
            this.setZombieKills(this.getZombieKills() + 1);
        } else if ("DamageFromWeapon".equals(change)) {
            String string1 = GameWindow.ReadStringUTF(bb);
            InventoryItem item1 = InventoryItemFactory.CreateItem(string1);
            if (item1 instanceof HandWeapon) {
                this.getBodyDamage().DamageFromWeapon((HandWeapon)item1);
            }
        } else if ("exitVehicle".equals(change)) {
            BaseVehicle vehiclex = this.getVehicle();
            if (vehiclex != null) {
                vehiclex.exit(this);
                this.setVehicle(null);
            }
        } else if ("removeItem".equals(change)) {
            int int1 = bb.getInt();
            if (int1 >= 0 && int1 < this.getInventory().getItems().size()) {
                InventoryItem item2 = this.getInventory().getItems().get(int1);
                this.removeFromHands(item2);
                this.getInventory().Remove(item2);
            }
        } else if ("removeItemID".equals(change)) {
            int int2 = bb.getInt();
            String string2 = GameWindow.ReadStringUTF(bb);
            InventoryItem item3 = this.getInventory().getItemWithID(int2);
            if (item3 != null && item3.getFullType().equals(string2)) {
                this.removeFromHands(item3);
                this.getInventory().Remove(item3);
            }
        } else if ("removeItemType".equals(change)) {
            String string3 = GameWindow.ReadStringUTF(bb);
            short short2 = bb.getShort();

            for (int int3 = 0; int3 < short2; int3++) {
                this.getInventory().RemoveOneOf(string3);
            }
        } else if ("removeOneOf".equals(change)) {
            String string4 = GameWindow.ReadStringUTF(bb);
            this.getInventory().RemoveOneOf(string4);
        } else if ("reanimatedID".equals(change)) {
            this.ReanimatedCorpseID = bb.getInt();
        } else if (!"Shove".equals(change)) {
            if ("StopBurning".equals(change)) {
                this.StopBurning();
            } else if ("addXp".equals(change)) {
                PerkFactory.Perk perk = PerkFactory.Perks.fromIndex(bb.getInt());
                int int4 = bb.getInt();
                boolean boolean0 = bb.get() == 1;
                if (boolean0) {
                    this.getXp().AddXPNoMultiplier(perk, int4);
                } else {
                    this.getXp().AddXP(perk, (float)int4);
                }
            } else if ("wakeUp".equals(change)) {
                if (this.isAsleep()) {
                    this.Asleep = false;
                    this.ForceWakeUpTime = -1.0F;
                    TutorialManager.instance.StealControl = false;
                    if (this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
                        UIManager.setFadeBeforeUI(((IsoPlayer)this).getPlayerNum(), true);
                        UIManager.FadeIn(((IsoPlayer)this).getPlayerNum(), 2.0);
                        GameClient.instance.sendPlayer((IsoPlayer)this);
                    }
                }
            } else if ("mechanicActionDone".equals(change)) {
                boolean boolean1 = bb.get() == 1;
                int int5 = bb.getInt();
                String string5 = GameWindow.ReadString(bb);
                boolean boolean2 = bb.get() == 1;
                long long0 = bb.getLong();
                LuaEventManager.triggerEvent("OnMechanicActionDone", this, boolean1, int5, string5, long0, boolean2);
            } else if ("vehicleNoKey".equals(change)) {
                this.SayDebug(" [img=media/ui/CarKey_none.png]");
            }
        }
    }

    @Override
    public int getAlreadyReadPages(String fullType) {
        for (int int0 = 0; int0 < this.ReadBooks.size(); int0++) {
            IsoGameCharacter.ReadBook readBook = this.ReadBooks.get(int0);
            if (readBook.fullType.equals(fullType)) {
                return readBook.alreadyReadPages;
            }
        }

        return 0;
    }

    @Override
    public void setAlreadyReadPages(String fullType, int pages) {
        for (int int0 = 0; int0 < this.ReadBooks.size(); int0++) {
            IsoGameCharacter.ReadBook readBook0 = this.ReadBooks.get(int0);
            if (readBook0.fullType.equals(fullType)) {
                readBook0.alreadyReadPages = pages;
                return;
            }
        }

        IsoGameCharacter.ReadBook readBook1 = new IsoGameCharacter.ReadBook();
        readBook1.fullType = fullType;
        readBook1.alreadyReadPages = pages;
        this.ReadBooks.add(readBook1);
    }

    public void updateLightInfo() {
        if (GameServer.bServer) {
            if (!this.isZombie()) {
                synchronized (this.lightInfo) {
                    this.lightInfo.square = this.movingSq;
                    if (this.lightInfo.square == null) {
                        this.lightInfo.square = this.getCell().getGridSquare((int)this.x, (int)this.y, (int)this.z);
                    }

                    if (this.ReanimatedCorpse != null) {
                        this.lightInfo.square = this.getCell().getGridSquare((int)this.x, (int)this.y, (int)this.z);
                    }

                    this.lightInfo.x = this.getX();
                    this.lightInfo.y = this.getY();
                    this.lightInfo.z = this.getZ();
                    this.lightInfo.angleX = this.getForwardDirection().getX();
                    this.lightInfo.angleY = this.getForwardDirection().getY();
                    this.lightInfo.torches.clear();
                    this.lightInfo.night = GameTime.getInstance().getNight();
                }
            }
        }
    }

    public IsoGameCharacter.LightInfo initLightInfo2() {
        synchronized (this.lightInfo) {
            for (int int0 = 0; int0 < this.lightInfo2.torches.size(); int0++) {
                IsoGameCharacter.TorchInfo.release(this.lightInfo2.torches.get(int0));
            }

            this.lightInfo2.initFrom(this.lightInfo);
        }

        return this.lightInfo2;
    }

    public IsoGameCharacter.LightInfo getLightInfo2() {
        return this.lightInfo2;
    }

    @Override
    public void postupdate() {
        IsoGameCharacter.s_performance.postUpdate.invokeAndMeasure(this, IsoGameCharacter::postUpdateInternal);
    }

    private void postUpdateInternal() {
        super.postupdate();
        AnimationPlayer animationPlayer = this.getAnimationPlayer();
        animationPlayer.UpdateDir(this);
        boolean boolean0 = this.shouldBeTurning();
        this.setTurning(boolean0);
        boolean boolean1 = this.shouldBeTurning90();
        this.setTurning90(boolean1);
        boolean boolean2 = this.shouldBeTurningAround();
        this.setTurningAround(boolean2);
        this.actionContext.update();
        if (this.getCurrentSquare() != null) {
            this.advancedAnimator.update();
        }

        this.actionContext.clearEvent("ActiveAnimFinished");
        this.actionContext.clearEvent("ActiveAnimFinishing");
        this.actionContext.clearEvent("ActiveAnimLooped");
        animationPlayer = this.getAnimationPlayer();
        if (animationPlayer != null) {
            MoveDeltaModifiers moveDeltaModifiers = IsoGameCharacter.L_postUpdate.moveDeltas;
            moveDeltaModifiers.moveDelta = this.getMoveDelta();
            moveDeltaModifiers.turnDelta = this.getTurnDelta();
            boolean1 = this.hasPath();
            boolean2 = this instanceof IsoPlayer;
            if (boolean2 && boolean1 && this.isRunning()) {
                moveDeltaModifiers.turnDelta = Math.max(moveDeltaModifiers.turnDelta, 2.0F);
            }

            State state = this.getCurrentState();
            if (state != null) {
                state.getDeltaModifiers(this, moveDeltaModifiers);
            }

            if (moveDeltaModifiers.twistDelta == -1.0F) {
                moveDeltaModifiers.twistDelta = moveDeltaModifiers.turnDelta * 1.8F;
            }

            if (!this.isTurning()) {
                moveDeltaModifiers.turnDelta = 0.0F;
            }

            float float0 = Math.max(1.0F - moveDeltaModifiers.moveDelta / 2.0F, 0.0F);
            animationPlayer.angleStepDelta = float0 * moveDeltaModifiers.turnDelta;
            animationPlayer.angleTwistDelta = float0 * moveDeltaModifiers.twistDelta;
            animationPlayer.setMaxTwistAngle((float) (Math.PI / 180.0) * this.getMaxTwist());
        }

        if (this.hasActiveModel()) {
            try {
                ModelManager.ModelSlot modelSlot = this.legsSprite.modelSlot;
                modelSlot.Update();
            } catch (Throwable throwable0) {
                ExceptionLogger.logException(throwable0);
            }
        } else {
            animationPlayer = this.getAnimationPlayer();
            animationPlayer.bUpdateBones = false;
            boolean0 = PerformanceSettings.InterpolateAnims;
            PerformanceSettings.InterpolateAnims = false;

            try {
                animationPlayer.UpdateDir(this);
                animationPlayer.Update();
            } catch (Throwable throwable1) {
                ExceptionLogger.logException(throwable1);
            } finally {
                animationPlayer.bUpdateBones = true;
                PerformanceSettings.InterpolateAnims = boolean0;
            }
        }

        this.updateLightInfo();
        if (this.isAnimationRecorderActive()) {
            this.m_animationRecorder.logVariables(this);
            this.m_animationRecorder.endLine();
        }
    }

    public boolean shouldBeTurning() {
        float float0 = this.getTargetTwist();
        float float1 = PZMath.abs(float0);
        boolean boolean0 = float1 > 1.0F;
        if (this.isZombie() && this.getCurrentState() == ZombieFallDownState.instance()) {
            return false;
        } else if (this.blockTurning) {
            return false;
        } else if (this.isBehaviourMoving()) {
            return boolean0;
        } else if (this.isPlayerMoving()) {
            return boolean0;
        } else if (this.isAttacking()) {
            return !this.bAimAtFloor;
        } else {
            float float2 = this.getAbsoluteExcessTwist();
            if (float2 > 1.0F) {
                return true;
            } else {
                return this.isTurning() ? boolean0 : false;
            }
        }
    }

    public boolean shouldBeTurning90() {
        if (!this.isTurning()) {
            return false;
        } else if (this.isTurning90()) {
            return true;
        } else {
            float float0 = this.getTargetTwist();
            float float1 = Math.abs(float0);
            return float1 > 65.0F;
        }
    }

    public boolean shouldBeTurningAround() {
        if (!this.isTurning()) {
            return false;
        } else if (this.isTurningAround()) {
            return true;
        } else {
            float float0 = this.getTargetTwist();
            float float1 = Math.abs(float0);
            return float1 > 110.0F;
        }
    }

    private boolean isTurning() {
        return this.m_isTurning;
    }

    private void setTurning(boolean boolean0) {
        this.m_isTurning = boolean0;
    }

    private boolean isTurningAround() {
        return this.m_isTurningAround;
    }

    private void setTurningAround(boolean boolean0) {
        this.m_isTurningAround = boolean0;
    }

    private boolean isTurning90() {
        return this.m_isTurning90;
    }

    private void setTurning90(boolean boolean0) {
        this.m_isTurning90 = boolean0;
    }

    public boolean hasPath() {
        return this.getPath2() != null;
    }

    @Override
    public boolean isAnimationRecorderActive() {
        return this.m_animationRecorder != null && this.m_animationRecorder.isRecording();
    }

    @Override
    public AnimationPlayerRecorder getAnimationPlayerRecorder() {
        return this.m_animationRecorder;
    }

    @Override
    public float getMeleeDelay() {
        return this.meleeDelay;
    }

    @Override
    public void setMeleeDelay(float delay) {
        this.meleeDelay = Math.max(delay, 0.0F);
    }

    @Override
    public float getRecoilDelay() {
        return this.RecoilDelay;
    }

    @Override
    public void setRecoilDelay(float recoilDelay) {
        if (recoilDelay < 0.0F) {
            recoilDelay = 0.0F;
        }

        this.RecoilDelay = recoilDelay;
    }

    public float getBeenMovingFor() {
        return this.BeenMovingFor;
    }

    public void setBeenMovingFor(float beenMovingFor) {
        if (beenMovingFor < 0.0F) {
            beenMovingFor = 0.0F;
        }

        if (beenMovingFor > 70.0F) {
            beenMovingFor = 70.0F;
        }

        this.BeenMovingFor = beenMovingFor;
    }

    public boolean isForceShove() {
        return this.forceShove;
    }

    public void setForceShove(boolean _forceShove) {
        this.forceShove = _forceShove;
    }

    public String getClickSound() {
        return this.clickSound;
    }

    public void setClickSound(String _clickSound) {
        this.clickSound = _clickSound;
    }

    public int getMeleeCombatMod() {
        int int0 = this.getWeaponLevel();
        if (int0 == 1) {
            return -2;
        } else if (int0 == 2) {
            return 0;
        } else if (int0 == 3) {
            return 1;
        } else if (int0 == 4) {
            return 2;
        } else if (int0 == 5) {
            return 3;
        } else if (int0 == 6) {
            return 4;
        } else if (int0 == 7) {
            return 5;
        } else if (int0 == 8) {
            return 5;
        } else if (int0 == 9) {
            return 6;
        } else {
            return int0 == 10 ? 7 : -5;
        }
    }

    public int getWeaponLevel() {
        WeaponType weaponType = WeaponType.getWeaponType(this);
        int int0 = -1;
        if (weaponType != null && weaponType != WeaponType.barehand) {
            if (((HandWeapon)this.getPrimaryHandItem()).getCategories().contains("Axe")) {
                int0 = this.getPerkLevel(PerkFactory.Perks.Axe);
            }

            if (((HandWeapon)this.getPrimaryHandItem()).getCategories().contains("Spear")) {
                int0 += this.getPerkLevel(PerkFactory.Perks.Spear);
            }

            if (((HandWeapon)this.getPrimaryHandItem()).getCategories().contains("SmallBlade")) {
                int0 += this.getPerkLevel(PerkFactory.Perks.SmallBlade);
            }

            if (((HandWeapon)this.getPrimaryHandItem()).getCategories().contains("LongBlade")) {
                int0 += this.getPerkLevel(PerkFactory.Perks.LongBlade);
            }

            if (((HandWeapon)this.getPrimaryHandItem()).getCategories().contains("Blunt")) {
                int0 += this.getPerkLevel(PerkFactory.Perks.Blunt);
            }

            if (((HandWeapon)this.getPrimaryHandItem()).getCategories().contains("SmallBlunt")) {
                int0 += this.getPerkLevel(PerkFactory.Perks.SmallBlunt);
            }
        }

        return int0 == -1 ? 0 : int0;
    }

    @Override
    public int getMaintenanceMod() {
        int int0 = this.getPerkLevel(PerkFactory.Perks.Maintenance);
        int0 += this.getWeaponLevel() / 2;
        return int0 / 2;
    }

    @Override
    public BaseVehicle getVehicle() {
        return this.vehicle;
    }

    @Override
    public void setVehicle(BaseVehicle v) {
        this.vehicle = v;
    }

    public boolean isUnderVehicle() {
        int int0 = ((int)this.x - 4) / 10;
        int int1 = ((int)this.y - 4) / 10;
        int int2 = (int)Math.ceil((this.x + 4.0F) / 10.0F);
        int int3 = (int)Math.ceil((this.y + 4.0F) / 10.0F);
        Vector2 vector0 = BaseVehicle.TL_vector2_pool.get().alloc();

        for (int int4 = int1; int4 < int3; int4++) {
            for (int int5 = int0; int5 < int2; int5++) {
                IsoChunk chunk = GameServer.bServer
                    ? ServerMap.instance.getChunk(int5, int4)
                    : IsoWorld.instance.CurrentCell.getChunkForGridSquare(int5 * 10, int4 * 10, 0);
                if (chunk != null) {
                    for (int int6 = 0; int6 < chunk.vehicles.size(); int6++) {
                        BaseVehicle vehiclex = chunk.vehicles.get(int6);
                        Vector2 vector1 = vehiclex.testCollisionWithCharacter(this, 0.3F, vector0);
                        if (vector1 != null && vector1.x != -1.0F) {
                            BaseVehicle.TL_vector2_pool.get().release(vector0);
                            return true;
                        }
                    }
                }
            }
        }

        BaseVehicle.TL_vector2_pool.get().release(vector0);
        return false;
    }

    public boolean isProne() {
        return this.isOnFloor();
    }

    public boolean isBeingSteppedOn() {
        if (!this.isOnFloor()) {
            return false;
        } else {
            for (int int0 = -1; int0 <= 1; int0++) {
                for (int int1 = -1; int1 <= 1; int1++) {
                    IsoGridSquare square = this.getCell().getGridSquare((int)this.x + int1, (int)this.y + int0, (int)this.z);
                    if (square != null) {
                        ArrayList arrayList = square.getMovingObjects();

                        for (int int2 = 0; int2 < arrayList.size(); int2++) {
                            IsoMovingObject movingObject = (IsoMovingObject)arrayList.get(int2);
                            if (movingObject != this) {
                                IsoGameCharacter character1 = Type.tryCastTo(movingObject, IsoGameCharacter.class);
                                if (character1 != null
                                    && character1.getVehicle() == null
                                    && !movingObject.isOnFloor()
                                    && ZombieOnGroundState.isCharacterStandingOnOther(character1, this)) {
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

    @Override
    public float getTemperature() {
        return this.getBodyDamage().getTemperature();
    }

    @Override
    public void setTemperature(float t) {
        this.getBodyDamage().setTemperature(t);
    }

    @Override
    public float getReduceInfectionPower() {
        return this.reduceInfectionPower;
    }

    @Override
    public void setReduceInfectionPower(float _reduceInfectionPower) {
        this.reduceInfectionPower = _reduceInfectionPower;
    }

    @Override
    public float getInventoryWeight() {
        if (this.getInventory() == null) {
            return 0.0F;
        } else {
            float float0 = 0.0F;
            ArrayList arrayList = this.getInventory().getItems();

            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                InventoryItem item = (InventoryItem)arrayList.get(int0);
                if (item.getAttachedSlot() > -1 && !this.isEquipped(item)) {
                    float0 += item.getHotbarEquippedWeight();
                } else if (this.isEquipped(item)) {
                    float0 += item.getEquippedWeight();
                } else {
                    float0 += item.getUnequippedWeight();
                }
            }

            return float0;
        }
    }

    public void dropHandItems() {
        if (!"Tutorial".equals(Core.GameMode)) {
            if (!(this instanceof IsoPlayer) || ((IsoPlayer)this).isLocalPlayer()) {
                this.dropHeavyItems();
                IsoGridSquare square = this.getCurrentSquare();
                if (square != null) {
                    InventoryItem item0 = this.getPrimaryHandItem();
                    InventoryItem item1 = this.getSecondaryHandItem();
                    if (item0 != null || item1 != null) {
                        square = this.getSolidFloorAt(square.x, square.y, square.z);
                        if (square != null) {
                            float float0 = Rand.Next(0.1F, 0.9F);
                            float float1 = Rand.Next(0.1F, 0.9F);
                            float float2 = square.getApparentZ(float0, float1) - square.getZ();
                            boolean boolean0 = false;
                            if (item1 == item0) {
                                boolean0 = true;
                            }

                            if (item0 != null) {
                                this.setPrimaryHandItem(null);
                                this.getInventory().DoRemoveItem(item0);
                                square.AddWorldInventoryItem(item0, float0, float1, float2);
                                LuaEventManager.triggerEvent("OnContainerUpdate");
                                LuaEventManager.triggerEvent("onItemFall", item0);
                            }

                            if (item1 != null) {
                                this.setSecondaryHandItem(null);
                                if (!boolean0) {
                                    this.getInventory().DoRemoveItem(item1);
                                    square.AddWorldInventoryItem(item1, float0, float1, float2);
                                    LuaEventManager.triggerEvent("OnContainerUpdate");
                                    LuaEventManager.triggerEvent("onItemFall", item1);
                                }
                            }

                            this.resetEquippedHandsModels();
                        }
                    }
                }
            }
        }
    }

    public boolean shouldBecomeZombieAfterDeath() {
        switch (SandboxOptions.instance.Lore.Transmission.getValue()) {
            case 1:
                return !this.getBodyDamage().IsFakeInfected() && this.getBodyDamage().getInfectionLevel() >= 0.001F;
            case 2:
                return !this.getBodyDamage().IsFakeInfected() && this.getBodyDamage().getInfectionLevel() >= 0.001F;
            case 3:
                return true;
            case 4:
                return false;
            default:
                return false;
        }
    }

    public void applyTraits(ArrayList<String> luaTraits) {
        if (luaTraits != null) {
            HashMap hashMap0 = new HashMap();
            hashMap0.put(PerkFactory.Perks.Fitness, 5);
            hashMap0.put(PerkFactory.Perks.Strength, 5);

            for (int int0 = 0; int0 < luaTraits.size(); int0++) {
                String string = (String)luaTraits.get(int0);
                if (string != null && !string.isEmpty()) {
                    TraitFactory.Trait trait = TraitFactory.getTrait(string);
                    if (trait != null) {
                        if (!this.HasTrait(string)) {
                            this.getTraits().add(string);
                        }

                        HashMap hashMap1 = trait.getXPBoostMap();
                        if (hashMap1 != null) {
                            for (Entry entry0 : hashMap1.entrySet()) {
                                PerkFactory.Perk perk0 = (PerkFactory.Perk)entry0.getKey();
                                int int1 = (Integer)entry0.getValue();
                                if (hashMap0.containsKey(perk0)) {
                                    int1 += hashMap0.get(perk0);
                                }

                                hashMap0.put(perk0, int1);
                            }
                        }
                    }
                }
            }

            if (this instanceof IsoPlayer) {
                ((IsoPlayer)this).getNutrition().applyWeightFromTraits();
            }

            HashMap hashMap2 = this.getDescriptor().getXPBoostMap();

            for (Entry entry1 : hashMap2.entrySet()) {
                PerkFactory.Perk perk1 = (PerkFactory.Perk)entry1.getKey();
                int int2 = (Integer)entry1.getValue();
                if (hashMap0.containsKey(perk1)) {
                    int2 += hashMap0.get(perk1);
                }

                hashMap0.put(perk1, int2);
            }

            for (Entry entry2 : hashMap0.entrySet()) {
                PerkFactory.Perk perk2 = (PerkFactory.Perk)entry2.getKey();
                int int3 = (Integer)entry2.getValue();
                int3 = Math.max(0, int3);
                int3 = Math.min(10, int3);
                this.getDescriptor().getXPBoostMap().put(perk2, Math.min(3, int3));

                for (int int4 = 0; int4 < int3; int4++) {
                    this.LevelPerk(perk2);
                }

                this.getXp().setXPToLevel(perk2, this.getPerkLevel(perk2));
            }
        }
    }

    public void createKeyRing() {
        InventoryItem item0 = this.getInventory().AddItem("Base.KeyRing");
        if (item0 != null && item0 instanceof InventoryContainer inventoryContainer) {
            inventoryContainer.setName(Translator.getText("IGUI_KeyRingName", this.getDescriptor().getForename(), this.getDescriptor().getSurname()));
            if (Rand.Next(100) < 40) {
                RoomDef roomDef = IsoWorld.instance.MetaGrid.getRoomAt((int)this.getX(), (int)this.getY(), (int)this.getZ());
                if (roomDef != null && roomDef.getBuilding() != null) {
                    String string = "Base.Key" + (Rand.Next(5) + 1);
                    InventoryItem item1 = inventoryContainer.getInventory().AddItem(string);
                    item1.setKeyId(roomDef.getBuilding().getKeyId());
                }
            }
        }
    }

    public void autoDrink() {
        if (!GameServer.bServer) {
            if (!GameClient.bClient || ((IsoPlayer)this).isLocalPlayer()) {
                if (Core.getInstance().getOptionAutoDrink()) {
                    if (!LuaHookManager.TriggerHook("AutoDrink", this)) {
                        if (!(this.stats.thirst <= 0.1F)) {
                            InventoryItem item = this.getWaterSource(this.getInventory().getItems());
                            if (item != null) {
                                this.stats.thirst -= 0.1F;
                                if (GameClient.bClient) {
                                    GameClient.instance.drink((IsoPlayer)this, 0.1F);
                                }

                                item.Use();
                            }
                        }
                    }
                }
            }
        }
    }

    public InventoryItem getWaterSource(ArrayList<InventoryItem> items) {
        InventoryItem item0 = null;
        new ArrayList();

        for (int int0 = 0; int0 < items.size(); int0++) {
            InventoryItem item1 = (InventoryItem)items.get(int0);
            if (item1.isWaterSource() && !item1.isBeingFilled() && !item1.isTaintedWater()) {
                if (item1 instanceof Drainable) {
                    if (((Drainable)item1).getUsedDelta() > 0.0F) {
                        item0 = item1;
                        break;
                    }
                } else if (!(item1 instanceof InventoryContainer)) {
                    item0 = item1;
                    break;
                }
            }
        }

        return item0;
    }

    @Override
    public List<String> getKnownRecipes() {
        return this.knownRecipes;
    }

    @Override
    public boolean isRecipeKnown(Recipe recipe) {
        return DebugOptions.instance.CheatRecipeKnowAll.getValue()
            ? true
            : !recipe.needToBeLearn() || this.getKnownRecipes().contains(recipe.getOriginalname());
    }

    @Override
    public boolean isRecipeKnown(String name) {
        Recipe recipe = ScriptManager.instance.getRecipe(name);
        if (recipe == null) {
            return DebugOptions.instance.CheatRecipeKnowAll.getValue() ? true : this.getKnownRecipes().contains(name);
        } else {
            return this.isRecipeKnown(recipe);
        }
    }

    public boolean learnRecipe(String name) {
        if (!this.isRecipeKnown(name)) {
            this.getKnownRecipes().add(name);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void addKnownMediaLine(String guid) {
        if (!StringUtils.isNullOrWhitespace(guid)) {
            this.knownMediaLines.add(guid.trim());
        }
    }

    @Override
    public void removeKnownMediaLine(String guid) {
        if (!StringUtils.isNullOrWhitespace(guid)) {
            this.knownMediaLines.remove(guid.trim());
        }
    }

    @Override
    public void clearKnownMediaLines() {
        this.knownMediaLines.clear();
    }

    @Override
    public boolean isKnownMediaLine(String guid) {
        return StringUtils.isNullOrWhitespace(guid) ? false : this.knownMediaLines.contains(guid.trim());
    }

    protected void saveKnownMediaLines(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)this.knownMediaLines.size());

        for (String string : this.knownMediaLines) {
            GameWindow.WriteStringUTF(byteBuffer, string);
        }
    }

    protected void loadKnownMediaLines(ByteBuffer byteBuffer, int var2) {
        this.knownMediaLines.clear();
        short short0 = byteBuffer.getShort();

        for (int int0 = 0; int0 < short0; int0++) {
            String string = GameWindow.ReadStringUTF(byteBuffer);
            this.knownMediaLines.add(string);
        }
    }

    public boolean isMoving() {
        return this instanceof IsoPlayer && !((IsoPlayer)this).isAttackAnimThrowTimeOut() ? false : this.m_isMoving;
    }

    public boolean isBehaviourMoving() {
        State state = this.getCurrentState();
        return state != null && state.isMoving(this);
    }

    public boolean isPlayerMoving() {
        return false;
    }

    public void setMoving(boolean val) {
        this.m_isMoving = val;
        if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).bRemote) {
            ((IsoPlayer)this).m_isPlayerMoving = val;
            ((IsoPlayer)this).setJustMoved(val);
        }
    }

    private boolean isFacingNorthWesterly() {
        return this.dir == IsoDirections.W || this.dir == IsoDirections.NW || this.dir == IsoDirections.N || this.dir == IsoDirections.NE;
    }

    public boolean isAttacking() {
        return false;
    }

    public boolean isZombieAttacking() {
        return false;
    }

    public boolean isZombieAttacking(IsoMovingObject other) {
        return false;
    }

    private boolean isZombieThumping() {
        return this.isZombie() ? this.getCurrentState() == ThumpState.instance() : false;
    }

    public int compareMovePriority(IsoGameCharacter other) {
        if (other == null) {
            return 1;
        } else if (this.isZombieThumping() && !other.isZombieThumping()) {
            return 1;
        } else if (!this.isZombieThumping() && other.isZombieThumping()) {
            return -1;
        } else if (other instanceof IsoPlayer) {
            return GameClient.bClient && this.isZombieAttacking(other) ? -1 : 0;
        } else if (this.isZombieAttacking() && !other.isZombieAttacking()) {
            return 1;
        } else if (!this.isZombieAttacking() && other.isZombieAttacking()) {
            return -1;
        } else if (this.isBehaviourMoving() && !other.isBehaviourMoving()) {
            return 1;
        } else if (!this.isBehaviourMoving() && other.isBehaviourMoving()) {
            return -1;
        } else if (this.isFacingNorthWesterly() && !other.isFacingNorthWesterly()) {
            return 1;
        } else {
            return !this.isFacingNorthWesterly() && other.isFacingNorthWesterly() ? -1 : 0;
        }
    }

    @Override
    public long playSound(String file) {
        return this.getEmitter().playSound(file);
    }

    @Override
    public long playSoundLocal(String file) {
        return this.getEmitter().playSoundImpl(file, null);
    }

    @Override
    public void stopOrTriggerSound(long eventInstance) {
        this.getEmitter().stopOrTriggerSound(eventInstance);
    }

    @Override
    public void addWorldSoundUnlessInvisible(int radius, int volume, boolean bStressHumans) {
        if (!this.isInvisible()) {
            WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), radius, volume, bStressHumans);
        }
    }

    @Override
    public boolean isKnownPoison(InventoryItem item) {
        if (item.hasTag("NoDetect")) {
            return false;
        } else if (item instanceof Food food) {
            if (food.getPoisonPower() <= 0) {
                return false;
            } else if (food.getHerbalistType() != null && !food.getHerbalistType().isEmpty()) {
                return this.isRecipeKnown("Herbalist");
            } else {
                return food.getPoisonDetectionLevel() >= 0 && this.getPerkLevel(PerkFactory.Perks.Cooking) >= 10 - food.getPoisonDetectionLevel()
                    ? true
                    : food.getPoisonLevelForRecipe() != null;
            }
        } else {
            return false;
        }
    }

    @Override
    public int getLastHourSleeped() {
        return this.lastHourSleeped;
    }

    @Override
    public void setLastHourSleeped(int _lastHourSleeped) {
        this.lastHourSleeped = _lastHourSleeped;
    }

    @Override
    public void setTimeOfSleep(float _timeOfSleep) {
        this.timeOfSleep = _timeOfSleep;
    }

    public void setDelayToSleep(float delay) {
        this.delayToActuallySleep = delay;
    }

    @Override
    public String getBedType() {
        return this.bedType;
    }

    @Override
    public void setBedType(String _bedType) {
        this.bedType = _bedType;
    }

    public void enterVehicle(BaseVehicle v, int seat, Vector3f offset) {
        if (this.vehicle != null) {
            this.vehicle.exit(this);
        }

        if (v != null) {
            v.enter(seat, this, offset);
        }
    }

    @Override
    public float Hit(BaseVehicle _vehicle, float speed, boolean isHitFromBehind, float hitDirX, float hitDirY) {
        this.setHitFromBehind(isHitFromBehind);
        if (GameClient.bClient) {
            this.setAttackedBy(GameClient.IDToPlayerMap.get(_vehicle.getNetPlayerId()));
        } else if (GameServer.bServer) {
            this.setAttackedBy(GameServer.IDToPlayerMap.get(_vehicle.getNetPlayerId()));
        } else {
            this.setAttackedBy(_vehicle.getDriver());
        }

        this.getHitDir().set(hitDirX, hitDirY);
        if (!this.isKnockedDown()) {
            this.setHitForce(Math.max(0.5F, speed * 0.15F));
        } else {
            this.setHitForce(Math.min(2.5F, speed * 0.15F));
        }

        if (GameClient.bClient) {
            HitReactionNetworkAI.CalcHitReactionVehicle(this, _vehicle);
        }

        DebugLog.Damage
            .noise(
                "Vehicle id=%d hit %s id=%d: speed=%f force=%f hitDir=%s",
                _vehicle.getId(),
                this.getClass().getSimpleName(),
                this.getOnlineID(),
                speed,
                this.getHitForce(),
                this.getHitDir()
            );
        return this.getHealth();
    }

    @Override
    public PolygonalMap2.Path getPath2() {
        return this.path2;
    }

    @Override
    public void setPath2(PolygonalMap2.Path path) {
        this.path2 = path;
    }

    @Override
    public PathFindBehavior2 getPathFindBehavior2() {
        return this.pfb2;
    }

    public MapKnowledge getMapKnowledge() {
        return this.mapKnowledge;
    }

    @Override
    public IsoObject getBed() {
        return this.isAsleep() ? this.bed : null;
    }

    @Override
    public void setBed(IsoObject _bed) {
        this.bed = _bed;
    }

    public boolean avoidDamage() {
        return this.m_avoidDamage;
    }

    public void setAvoidDamage(boolean avoid) {
        this.m_avoidDamage = avoid;
    }

    @Override
    public boolean isReading() {
        return this.isReading;
    }

    @Override
    public void setReading(boolean _isReading) {
        this.isReading = _isReading;
    }

    @Override
    public float getTimeSinceLastSmoke() {
        return this.timeSinceLastSmoke;
    }

    @Override
    public void setTimeSinceLastSmoke(float _timeSinceLastSmoke) {
        this.timeSinceLastSmoke = PZMath.clamp(_timeSinceLastSmoke, 0.0F, 10.0F);
    }

    @Override
    public boolean isInvisible() {
        return this.m_invisible;
    }

    @Override
    public void setInvisible(boolean b) {
        this.m_invisible = b;
    }

    @Override
    public boolean isDriving() {
        return this.getVehicle() != null && this.getVehicle().getDriver() == this && !this.getVehicle().isStopped();
    }

    @Override
    public boolean isInARoom() {
        return this.square != null && this.square.isInARoom();
    }

    @Override
    public boolean isGodMod() {
        return this.m_godMod;
    }

    @Override
    public void setGodMod(boolean b) {
        if (!this.isDead()) {
            this.m_godMod = b;
            if (this instanceof IsoPlayer && GameClient.bClient && ((IsoPlayer)this).isLocalPlayer()) {
                this.updateMovementRates();
                GameClient.sendPlayerInjuries((IsoPlayer)this);
                GameClient.sendPlayerDamage((IsoPlayer)this);
            }
        }
    }

    @Override
    public boolean isUnlimitedCarry() {
        return this.unlimitedCarry;
    }

    @Override
    public void setUnlimitedCarry(boolean _unlimitedCarry) {
        this.unlimitedCarry = _unlimitedCarry;
    }

    @Override
    public boolean isBuildCheat() {
        return this.buildCheat;
    }

    @Override
    public void setBuildCheat(boolean _buildCheat) {
        this.buildCheat = _buildCheat;
    }

    @Override
    public boolean isFarmingCheat() {
        return this.farmingCheat;
    }

    @Override
    public void setFarmingCheat(boolean b) {
        this.farmingCheat = b;
    }

    @Override
    public boolean isHealthCheat() {
        return this.healthCheat;
    }

    @Override
    public void setHealthCheat(boolean _healthCheat) {
        this.healthCheat = _healthCheat;
    }

    @Override
    public boolean isMechanicsCheat() {
        return this.mechanicsCheat;
    }

    @Override
    public void setMechanicsCheat(boolean _mechanicsCheat) {
        this.mechanicsCheat = _mechanicsCheat;
    }

    @Override
    public boolean isMovablesCheat() {
        return this.movablesCheat;
    }

    @Override
    public void setMovablesCheat(boolean b) {
        this.movablesCheat = b;
    }

    @Override
    public boolean isTimedActionInstantCheat() {
        return this.timedActionInstantCheat;
    }

    @Override
    public void setTimedActionInstantCheat(boolean b) {
        this.timedActionInstantCheat = b;
    }

    @Override
    public boolean isTimedActionInstant() {
        return Core.bDebug && DebugOptions.instance.CheatTimedActionInstant.getValue() ? true : this.isTimedActionInstantCheat();
    }

    @Override
    public boolean isShowAdminTag() {
        return this.showAdminTag;
    }

    @Override
    public void setShowAdminTag(boolean _showAdminTag) {
        this.showAdminTag = _showAdminTag;
    }

    /**
     * Description copied from interface: IAnimationVariableSource
     */
    @Override
    public IAnimationVariableSlot getVariable(AnimationVariableHandle handle) {
        return this.getGameVariablesInternal().getVariable(handle);
    }

    /**
     * Description copied from interface: IAnimationVariableSource
     */
    @Override
    public IAnimationVariableSlot getVariable(String key) {
        return this.getGameVariablesInternal().getVariable(key);
    }

    /**
     * Description copied from interface: IAnimationVariableMap
     */
    @Override
    public IAnimationVariableSlot getOrCreateVariable(String key) {
        return this.getGameVariablesInternal().getOrCreateVariable(key);
    }

    /**
     * Description copied from interface: IAnimationVariableMap
     */
    @Override
    public void setVariable(IAnimationVariableSlot var) {
        this.getGameVariablesInternal().setVariable(var);
    }

    @Override
    public void setVariable(String key, String value) {
        this.getGameVariablesInternal().setVariable(key, value);
    }

    @Override
    public void setVariable(String key, boolean value) {
        this.getGameVariablesInternal().setVariable(key, value);
    }

    @Override
    public void setVariable(String key, float value) {
        this.getGameVariablesInternal().setVariable(key, value);
    }

    protected void setVariable(String string, AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped) {
        this.getGameVariablesInternal().setVariable(string, callbackGetStrongTyped);
    }

    protected void setVariable(
        String string,
        AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped,
        AnimationVariableSlotCallbackBool.CallbackSetStrongTyped callbackSetStrongTyped
    ) {
        this.getGameVariablesInternal().setVariable(string, callbackGetStrongTyped, callbackSetStrongTyped);
    }

    protected void setVariable(String string, AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped) {
        this.getGameVariablesInternal().setVariable(string, callbackGetStrongTyped);
    }

    protected void setVariable(
        String string,
        AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped,
        AnimationVariableSlotCallbackString.CallbackSetStrongTyped callbackSetStrongTyped
    ) {
        this.getGameVariablesInternal().setVariable(string, callbackGetStrongTyped, callbackSetStrongTyped);
    }

    protected void setVariable(String string, AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped) {
        this.getGameVariablesInternal().setVariable(string, callbackGetStrongTyped);
    }

    protected void setVariable(
        String string,
        AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped,
        AnimationVariableSlotCallbackFloat.CallbackSetStrongTyped callbackSetStrongTyped
    ) {
        this.getGameVariablesInternal().setVariable(string, callbackGetStrongTyped, callbackSetStrongTyped);
    }

    protected void setVariable(String string, AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped) {
        this.getGameVariablesInternal().setVariable(string, callbackGetStrongTyped);
    }

    protected void setVariable(
        String string,
        AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped,
        AnimationVariableSlotCallbackInt.CallbackSetStrongTyped callbackSetStrongTyped
    ) {
        this.getGameVariablesInternal().setVariable(string, callbackGetStrongTyped, callbackSetStrongTyped);
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(String key, boolean defaultVal, AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGet) {
        this.getGameVariablesInternal().setVariable(key, defaultVal, callbackGet);
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(
        String key,
        boolean defaultVal,
        AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGet,
        AnimationVariableSlotCallbackBool.CallbackSetStrongTyped callbackSet
    ) {
        this.getGameVariablesInternal().setVariable(key, defaultVal, callbackGet, callbackSet);
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(String key, String defaultVal, AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGet) {
        this.getGameVariablesInternal().setVariable(key, defaultVal, callbackGet);
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(
        String key,
        String defaultVal,
        AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGet,
        AnimationVariableSlotCallbackString.CallbackSetStrongTyped callbackSet
    ) {
        this.getGameVariablesInternal().setVariable(key, defaultVal, callbackGet, callbackSet);
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(String key, float defaultVal, AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGet) {
        this.getGameVariablesInternal().setVariable(key, defaultVal, callbackGet);
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(
        String key,
        float defaultVal,
        AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGet,
        AnimationVariableSlotCallbackFloat.CallbackSetStrongTyped callbackSet
    ) {
        this.getGameVariablesInternal().setVariable(key, defaultVal, callbackGet, callbackSet);
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(String key, int defaultVal, AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGet) {
        this.getGameVariablesInternal().setVariable(key, defaultVal, callbackGet);
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(
        String key,
        int defaultVal,
        AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGet,
        AnimationVariableSlotCallbackInt.CallbackSetStrongTyped callbackSet
    ) {
        this.getGameVariablesInternal().setVariable(key, defaultVal, callbackGet, callbackSet);
    }

    @Override
    public void clearVariable(String key) {
        this.getGameVariablesInternal().clearVariable(key);
    }

    @Override
    public void clearVariables() {
        this.getGameVariablesInternal().clearVariables();
    }

    /**
     * Description copied from interface: IAnimationVariableSource
     */
    @Override
    public String getVariableString(String name) {
        return this.getGameVariablesInternal().getVariableString(name);
    }

    private String getFootInjuryType() {
        if (!(this instanceof IsoPlayer)) {
            return "";
        } else {
            BodyPart bodyPart0 = this.getBodyDamage().getBodyPart(BodyPartType.Foot_L);
            BodyPart bodyPart1 = this.getBodyDamage().getBodyPart(BodyPartType.Foot_R);
            if (!this.bRunning) {
                if (bodyPart0.haveBullet()
                    || bodyPart0.getBurnTime() > 5.0F
                    || bodyPart0.bitten()
                    || bodyPart0.deepWounded()
                    || bodyPart0.isSplint()
                    || bodyPart0.getFractureTime() > 0.0F
                    || bodyPart0.haveGlass()) {
                    return "leftheavy";
                }

                if (bodyPart1.haveBullet()
                    || bodyPart1.getBurnTime() > 5.0F
                    || bodyPart1.bitten()
                    || bodyPart1.deepWounded()
                    || bodyPart1.isSplint()
                    || bodyPart1.getFractureTime() > 0.0F
                    || bodyPart1.haveGlass()) {
                    return "rightheavy";
                }
            }

            if (bodyPart0.getScratchTime() > 5.0F || bodyPart0.getCutTime() > 7.0F || bodyPart0.getBurnTime() > 0.0F) {
                return "leftlight";
            } else {
                return !(bodyPart1.getScratchTime() > 5.0F) && !(bodyPart1.getCutTime() > 7.0F) && !(bodyPart1.getBurnTime() > 0.0F) ? "" : "rightlight";
            }
        }
    }

    /**
     * Description copied from interface: IAnimationVariableSource
     */
    @Override
    public float getVariableFloat(String name, float defaultVal) {
        return this.getGameVariablesInternal().getVariableFloat(name, defaultVal);
    }

    /**
     * Description copied from interface: IAnimationVariableSource
     */
    @Override
    public boolean getVariableBoolean(String name) {
        return this.getGameVariablesInternal().getVariableBoolean(name);
    }

    /**
     * Returns the specified variable, as a boolean.  Attempts to convert the string variable to a boolean.  If that fails, or if variable not found, returns defaultVal
     */
    @Override
    public boolean getVariableBoolean(String key, boolean defaultVal) {
        return this.getGameVariablesInternal().getVariableBoolean(this.name, defaultVal);
    }

    /**
     * Compares (ignoring case) the value of the specified variable.  Returns TRUE if they match.
     */
    @Override
    public boolean isVariable(String name, String val) {
        return this.getGameVariablesInternal().isVariable(name, val);
    }

    @Override
    public boolean containsVariable(String name) {
        return this.getGameVariablesInternal().containsVariable(name);
    }

    /**
     * Description copied from interface: IAnimationVariableSource
     */
    @Override
    public Iterable<IAnimationVariableSlot> getGameVariables() {
        return this.getGameVariablesInternal().getGameVariables();
    }

    private AnimationVariableSource getGameVariablesInternal() {
        return this.m_PlaybackGameVariables != null ? this.m_PlaybackGameVariables : this.m_GameVariables;
    }

    public AnimationVariableSource startPlaybackGameVariables() {
        if (this.m_PlaybackGameVariables != null) {
            DebugLog.General.error("Error! PlaybackGameVariables is already active.");
            return this.m_PlaybackGameVariables;
        } else {
            AnimationVariableSource animationVariableSource = new AnimationVariableSource();

            for (IAnimationVariableSlot iAnimationVariableSlot : this.getGameVariables()) {
                AnimationVariableType animationVariableType = iAnimationVariableSlot.getType();
                switch (animationVariableType) {
                    case String:
                        animationVariableSource.setVariable(iAnimationVariableSlot.getKey(), iAnimationVariableSlot.getValueString());
                        break;
                    case Float:
                        animationVariableSource.setVariable(iAnimationVariableSlot.getKey(), iAnimationVariableSlot.getValueFloat());
                        break;
                    case Boolean:
                        animationVariableSource.setVariable(iAnimationVariableSlot.getKey(), iAnimationVariableSlot.getValueBool());
                    case Void:
                        break;
                    default:
                        DebugLog.General.error("Error! Variable type not handled: %s", animationVariableType.toString());
                }
            }

            this.m_PlaybackGameVariables = animationVariableSource;
            return this.m_PlaybackGameVariables;
        }
    }

    public void endPlaybackGameVariables(AnimationVariableSource playbackVars) {
        if (this.m_PlaybackGameVariables != playbackVars) {
            DebugLog.General.error("Error! Playback GameVariables do not match.");
        }

        this.m_PlaybackGameVariables = null;
    }

    public void playbackSetCurrentStateSnapshot(ActionStateSnapshot snapshot) {
        if (this.actionContext != null) {
            this.actionContext.setPlaybackStateSnapshot(snapshot);
        }
    }

    public ActionStateSnapshot playbackRecordCurrentStateSnapshot() {
        return this.actionContext == null ? null : this.actionContext.getPlaybackStateSnapshot();
    }

    @Override
    public String GetVariable(String key) {
        return this.getVariableString(key);
    }

    @Override
    public void SetVariable(String key, String value) {
        this.setVariable(key, value);
    }

    @Override
    public void ClearVariable(String key) {
        this.clearVariable(key);
    }

    @Override
    public void actionStateChanged(ActionContext sender) {
        ArrayList arrayList0 = IsoGameCharacter.L_actionStateChanged.stateNames;
        PZArrayUtil.listConvert(sender.getChildStates(), arrayList0, actionState -> actionState.name);
        this.advancedAnimator.SetState(sender.getCurrentStateName(), arrayList0);

        try {
            this.stateMachine.activeStateChanged++;
            State state = this.m_stateUpdateLookup.get(sender.getCurrentStateName().toLowerCase());
            if (state == null) {
                state = this.defaultState;
            }

            ArrayList arrayList1 = IsoGameCharacter.L_actionStateChanged.states;
            PZArrayUtil.listConvert(
                sender.getChildStates(), arrayList1, this.m_stateUpdateLookup, (actionState, hashMap) -> hashMap.get(actionState.name.toLowerCase())
            );
            this.stateMachine.changeState(state, arrayList1);
        } finally {
            this.stateMachine.activeStateChanged--;
        }
    }

    public boolean isFallOnFront() {
        return this.fallOnFront;
    }

    public void setFallOnFront(boolean _fallOnFront) {
        this.fallOnFront = _fallOnFront;
    }

    public boolean isHitFromBehind() {
        return this.hitFromBehind;
    }

    public void setHitFromBehind(boolean _hitFromBehind) {
        this.hitFromBehind = _hitFromBehind;
    }

    @Override
    public void reportEvent(String name) {
        this.actionContext.reportEvent(name);
    }

    @Override
    public void StartTimedActionAnim(String event) {
        this.StartTimedActionAnim(event, null);
    }

    @Override
    public void StartTimedActionAnim(String event, String type) {
        this.reportEvent(event);
        if (type != null) {
            this.setVariable("TimedActionType", type);
        }

        this.resetModelNextFrame();
    }

    @Override
    public void StopTimedActionAnim() {
        this.clearVariable("TimedActionType");
        this.reportEvent("Event_TA_Exit");
        this.resetModelNextFrame();
    }

    public boolean hasHitReaction() {
        return !StringUtils.isNullOrEmpty(this.getHitReaction());
    }

    public String getHitReaction() {
        return this.hitReaction;
    }

    public void setHitReaction(String _hitReaction) {
        this.hitReaction = _hitReaction;
    }

    public void CacheEquipped() {
        this.cacheEquiped[0] = this.getPrimaryHandItem();
        this.cacheEquiped[1] = this.getSecondaryHandItem();
    }

    public InventoryItem GetPrimaryEquippedCache() {
        return this.cacheEquiped[0] != null && this.inventory.contains(this.cacheEquiped[0]) ? this.cacheEquiped[0] : null;
    }

    public InventoryItem GetSecondaryEquippedCache() {
        return this.cacheEquiped[1] != null && this.inventory.contains(this.cacheEquiped[1]) ? this.cacheEquiped[1] : null;
    }

    public void ClearEquippedCache() {
        this.cacheEquiped[0] = null;
        this.cacheEquiped[1] = null;
    }

    public boolean isBehind(IsoGameCharacter chr) {
        Vector2 vector0 = tempVector2_1.set(this.getX(), this.getY());
        Vector2 vector1 = tempVector2_2.set(chr.getX(), chr.getY());
        vector1.x = vector1.x - vector0.x;
        vector1.y = vector1.y - vector0.y;
        Vector2 vector2 = chr.getForwardDirection();
        vector1.normalize();
        vector2.normalize();
        float float0 = vector1.dot(vector2);
        return float0 > 0.6;
    }

    public void resetEquippedHandsModels() {
        if (!GameServer.bServer || ServerGUI.isCreated()) {
            if (this.hasActiveModel()) {
                ModelManager.instance.ResetEquippedNextFrame(this);
            }
        }
    }

    @Override
    public AnimatorDebugMonitor getDebugMonitor() {
        return this.advancedAnimator.getDebugMonitor();
    }

    @Override
    public void setDebugMonitor(AnimatorDebugMonitor monitor) {
        this.advancedAnimator.setDebugMonitor(monitor);
    }

    public boolean isAimAtFloor() {
        return this.bAimAtFloor;
    }

    public void setAimAtFloor(boolean b) {
        this.bAimAtFloor = b;
    }

    public String testDotSide(IsoMovingObject target) {
        Vector2 vector0 = this.getLookVector(IsoGameCharacter.l_testDotSide.v1);
        Vector2 vector1 = IsoGameCharacter.l_testDotSide.v2.set(this.getX(), this.getY());
        Vector2 vector2 = IsoGameCharacter.l_testDotSide.v3.set(target.x - vector1.x, target.y - vector1.y);
        vector2.normalize();
        float float0 = Vector2.dot(vector2.x, vector2.y, vector0.x, vector0.y);
        if (float0 > 0.7) {
            return "FRONT";
        } else if (float0 < 0.0F && float0 < -0.5) {
            return "BEHIND";
        } else {
            float float1 = target.x;
            float float2 = target.y;
            float float3 = vector1.x;
            float float4 = vector1.y;
            float float5 = vector1.x + vector0.x;
            float float6 = vector1.y + vector0.y;
            float float7 = (float1 - float3) * (float6 - float4) - (float2 - float4) * (float5 - float3);
            return float7 > 0.0F ? "RIGHT" : "LEFT";
        }
    }

    public void addBasicPatch(BloodBodyPartType part) {
        if (this instanceof IHumanVisual) {
            if (part == null) {
                part = BloodBodyPartType.FromIndex(Rand.Next(0, BloodBodyPartType.MAX.index()));
            }

            HumanVisual humanVisual = ((IHumanVisual)this).getHumanVisual();
            this.getItemVisuals(tempItemVisuals);
            BloodClothingType.addBasicPatch(part, humanVisual, tempItemVisuals);
            this.bUpdateModelTextures = true;
            this.bUpdateEquippedTextures = true;
            if (!GameServer.bServer && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
                LuaEventManager.triggerEvent("OnClothingUpdated", this);
            }
        }
    }

    @Override
    public boolean addHole(BloodBodyPartType part) {
        return this.addHole(part, false);
    }

    public boolean addHole(BloodBodyPartType part, boolean allLayers) {
        if (!(this instanceof IHumanVisual)) {
            return false;
        } else {
            if (part == null) {
                part = BloodBodyPartType.FromIndex(OutfitRNG.Next(0, BloodBodyPartType.MAX.index()));
            }

            HumanVisual humanVisual = ((IHumanVisual)this).getHumanVisual();
            this.getItemVisuals(tempItemVisuals);
            boolean boolean0 = BloodClothingType.addHole(part, humanVisual, tempItemVisuals, allLayers);
            this.bUpdateModelTextures = true;
            if (!GameServer.bServer && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
                LuaEventManager.triggerEvent("OnClothingUpdated", this);
                if (GameClient.bClient) {
                    GameClient.instance.sendClothing((IsoPlayer)this, "", null);
                }
            }

            return boolean0;
        }
    }

    public void addDirt(BloodBodyPartType part, Integer nbr, boolean allLayers) {
        HumanVisual humanVisual = ((IHumanVisual)this).getHumanVisual();
        if (nbr == null) {
            nbr = OutfitRNG.Next(5, 10);
        }

        boolean boolean0 = false;
        if (part == null) {
            boolean0 = true;
        }

        this.getItemVisuals(tempItemVisuals);

        for (int int0 = 0; int0 < nbr; int0++) {
            if (boolean0) {
                part = BloodBodyPartType.FromIndex(OutfitRNG.Next(0, BloodBodyPartType.MAX.index()));
            }

            BloodClothingType.addDirt(part, humanVisual, tempItemVisuals, allLayers);
        }

        this.bUpdateModelTextures = true;
        if (!GameServer.bServer && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
            LuaEventManager.triggerEvent("OnClothingUpdated", this);
        }
    }

    @Override
    public void addBlood(BloodBodyPartType part, boolean scratched, boolean bitten, boolean allLayers) {
        HumanVisual humanVisual = ((IHumanVisual)this).getHumanVisual();
        int int0 = 1;
        boolean boolean0 = false;
        if (part == null) {
            boolean0 = true;
        }

        if (this.getPrimaryHandItem() instanceof HandWeapon) {
            int0 = ((HandWeapon)this.getPrimaryHandItem()).getSplatNumber();
            if (OutfitRNG.Next(15) < this.getWeaponLevel()) {
                int0--;
            }
        }

        if (bitten) {
            int0 = 20;
        }

        if (scratched) {
            int0 = 5;
        }

        if (this.isZombie()) {
            int0 += 8;
        }

        this.getItemVisuals(tempItemVisuals);

        for (int int1 = 0; int1 < int0; int1++) {
            if (boolean0) {
                part = BloodBodyPartType.FromIndex(OutfitRNG.Next(0, BloodBodyPartType.MAX.index()));
                if (this.getPrimaryHandItem() != null && this.getPrimaryHandItem() instanceof HandWeapon) {
                    HandWeapon weapon = (HandWeapon)this.getPrimaryHandItem();
                    if (weapon.getBloodLevel() < 1.0F) {
                        float float0 = weapon.getBloodLevel() + 0.02F;
                        weapon.setBloodLevel(float0);
                        this.bUpdateEquippedTextures = true;
                    }
                }
            }

            BloodClothingType.addBlood(part, humanVisual, tempItemVisuals, allLayers);
        }

        this.bUpdateModelTextures = true;
        if (!GameServer.bServer && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
            LuaEventManager.triggerEvent("OnClothingUpdated", this);
        }
    }

    public float getBodyPartClothingDefense(Integer part, boolean bite, boolean bullet) {
        float float0 = 0.0F;
        this.getItemVisuals(tempItemVisuals);

        for (int int0 = tempItemVisuals.size() - 1; int0 >= 0; int0--) {
            ItemVisual itemVisual = tempItemVisuals.get(int0);
            Item item0 = itemVisual.getScriptItem();
            if (item0 != null) {
                ArrayList arrayList0 = item0.getBloodClothingType();
                if (arrayList0 != null) {
                    ArrayList arrayList1 = BloodClothingType.getCoveredParts(arrayList0);
                    if (arrayList1 != null) {
                        InventoryItem item1 = itemVisual.getInventoryItem();
                        if (item1 == null) {
                            item1 = InventoryItemFactory.CreateItem(itemVisual.getItemType());
                            if (item1 == null) {
                                continue;
                            }
                        }

                        for (int int1 = 0; int1 < arrayList1.size(); int1++) {
                            if (item1 instanceof Clothing
                                && ((BloodBodyPartType)arrayList1.get(int1)).index() == part
                                && itemVisual.getHole((BloodBodyPartType)arrayList1.get(int1)) == 0.0F) {
                                Clothing clothing = (Clothing)item1;
                                float0 += clothing.getDefForPart((BloodBodyPartType)arrayList1.get(int1), bite, bullet);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return Math.min(100.0F, float0);
    }

    @Override
    public boolean isBumped() {
        return !StringUtils.isNullOrWhitespace(this.getBumpType());
    }

    public boolean isBumpDone() {
        return this.m_isBumpDone;
    }

    public void setBumpDone(boolean val) {
        this.m_isBumpDone = val;
    }

    public boolean isBumpFall() {
        return this.m_bumpFall;
    }

    public void setBumpFall(boolean val) {
        this.m_bumpFall = val;
    }

    public boolean isBumpStaggered() {
        return this.m_bumpStaggered;
    }

    public void setBumpStaggered(boolean val) {
        this.m_bumpStaggered = val;
    }

    @Override
    public String getBumpType() {
        return this.bumpType;
    }

    public void setBumpType(String _bumpType) {
        if (StringUtils.equalsIgnoreCase(this.bumpType, _bumpType)) {
            this.bumpType = _bumpType;
        } else {
            boolean boolean0 = this.isBumped();
            this.bumpType = _bumpType;
            boolean boolean1 = this.isBumped();
            if (boolean1 != boolean0) {
                this.setBumpStaggered(boolean1);
            }
        }
    }

    public String getBumpFallType() {
        return this.m_bumpFallType;
    }

    public void setBumpFallType(String val) {
        this.m_bumpFallType = val;
    }

    public IsoGameCharacter getBumpedChr() {
        return this.bumpedChr;
    }

    public void setBumpedChr(IsoGameCharacter _bumpedChr) {
        this.bumpedChr = _bumpedChr;
    }

    public long getLastBump() {
        return this.lastBump;
    }

    public void setLastBump(long _lastBump) {
        this.lastBump = _lastBump;
    }

    public boolean isSitOnGround() {
        return this.sitOnGround;
    }

    public void setSitOnGround(boolean _sitOnGround) {
        this.sitOnGround = _sitOnGround;
    }

    @Override
    public String getUID() {
        return this.m_UID;
    }

    protected HashMap<String, State> getStateUpdateLookup() {
        return this.m_stateUpdateLookup;
    }

    public boolean isRunning() {
        return this.getMoodles() != null && this.getMoodles().getMoodleLevel(MoodleType.Endurance) >= 3 ? false : this.bRunning;
    }

    public void setRunning(boolean _bRunning) {
        this.bRunning = _bRunning;
    }

    public boolean isSprinting() {
        return this.bSprinting && !this.canSprint() ? false : this.bSprinting;
    }

    public void setSprinting(boolean _bSprinting) {
        this.bSprinting = _bSprinting;
    }

    public boolean canSprint() {
        if (this instanceof IsoPlayer && !((IsoPlayer)this).isAllowSprint()) {
            return false;
        } else if ("Tutorial".equals(Core.GameMode)) {
            return true;
        } else {
            InventoryItem item = this.getPrimaryHandItem();
            if (item != null && item.isEquippedNoSprint()) {
                return false;
            } else {
                item = this.getSecondaryHandItem();
                return item != null && item.isEquippedNoSprint()
                    ? false
                    : this.getMoodles() == null || this.getMoodles().getMoodleLevel(MoodleType.Endurance) < 2;
            }
        }
    }

    public void postUpdateModelTextures() {
        this.bUpdateModelTextures = true;
    }

    public ModelInstanceTextureCreator getTextureCreator() {
        return this.textureCreator;
    }

    public void setTextureCreator(ModelInstanceTextureCreator _textureCreator) {
        this.textureCreator = _textureCreator;
    }

    public void postUpdateEquippedTextures() {
        this.bUpdateEquippedTextures = true;
    }

    public ArrayList<ModelInstance> getReadyModelData() {
        return this.readyModelData;
    }

    public boolean getIgnoreMovement() {
        return this.ignoreMovement;
    }

    public void setIgnoreMovement(boolean _ignoreMovement) {
        if (this instanceof IsoPlayer && _ignoreMovement) {
            ((IsoPlayer)this).networkAI.needToUpdate();
        }

        this.ignoreMovement = _ignoreMovement;
    }

    public boolean isAutoWalk() {
        return this.bAutoWalk;
    }

    public void setAutoWalk(boolean b) {
        this.bAutoWalk = b;
    }

    public void setAutoWalkDirection(Vector2 v) {
        this.autoWalkDirection.set(v);
    }

    public Vector2 getAutoWalkDirection() {
        return this.autoWalkDirection;
    }

    public boolean isSneaking() {
        return this.getVariableFloat("WalkInjury", 0.0F) > 0.5F ? false : this.bSneaking;
    }

    public void setSneaking(boolean _bSneaking) {
        this.bSneaking = _bSneaking;
    }

    public GameCharacterAIBrain getGameCharacterAIBrain() {
        return this.GameCharacterAIBrain;
    }

    public float getMoveDelta() {
        return this.m_moveDelta;
    }

    public void setMoveDelta(float moveDelta) {
        this.m_moveDelta = moveDelta;
    }

    public float getTurnDelta() {
        if (this.isSprinting()) {
            return this.m_turnDeltaSprinting;
        } else {
            return this.isRunning() ? this.m_turnDeltaRunning : this.m_turnDeltaNormal;
        }
    }

    public void setTurnDelta(float m_turnDelta) {
        this.m_turnDeltaNormal = m_turnDelta;
    }

    public float getChopTreeSpeed() {
        return (this.Traits.Axeman.isSet() ? 1.25F : 1.0F) * GameTime.getAnimSpeedFix();
    }

    /**
     * Test if we're able to defend a zombie bite  Can only happen if zombie is attacking from front  Calcul include current weapon skills, fitness & strength
     */
    public boolean testDefense(IsoZombie zomb) {
        if (this.testDotSide(zomb).equals("FRONT") && !zomb.bCrawling && this.getSurroundingAttackingZombies() <= 3) {
            int int0 = 0;
            if ("KnifeDeath".equals(this.getVariableString("ZombieHitReaction"))) {
                int0 += 30;
            }

            int0 += this.getWeaponLevel() * 3;
            int0 += this.getPerkLevel(PerkFactory.Perks.Fitness) * 2;
            int0 += this.getPerkLevel(PerkFactory.Perks.Strength) * 2;
            int0 -= this.getSurroundingAttackingZombies() * 5;
            int0 -= this.getMoodles().getMoodleLevel(MoodleType.Endurance) * 2;
            int0 -= this.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 2;
            int0 -= this.getMoodles().getMoodleLevel(MoodleType.Tired) * 3;
            if (SandboxOptions.instance.Lore.Strength.getValue() == 1) {
                int0 -= 7;
            }

            if (SandboxOptions.instance.Lore.Strength.getValue() == 3) {
                int0 += 7;
            }

            if (Rand.Next(100) < int0) {
                this.setAttackedBy(zomb);
                this.setHitReaction(zomb.getVariableString("PlayerHitReaction") + "Defended");
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public int getSurroundingAttackingZombies() {
        movingStatic.clear();
        IsoGridSquare square = this.getCurrentSquare();
        if (square == null) {
            return 0;
        } else {
            movingStatic.addAll(square.getMovingObjects());
            if (square.n != null) {
                movingStatic.addAll(square.n.getMovingObjects());
            }

            if (square.s != null) {
                movingStatic.addAll(square.s.getMovingObjects());
            }

            if (square.e != null) {
                movingStatic.addAll(square.e.getMovingObjects());
            }

            if (square.w != null) {
                movingStatic.addAll(square.w.getMovingObjects());
            }

            if (square.nw != null) {
                movingStatic.addAll(square.nw.getMovingObjects());
            }

            if (square.sw != null) {
                movingStatic.addAll(square.sw.getMovingObjects());
            }

            if (square.se != null) {
                movingStatic.addAll(square.se.getMovingObjects());
            }

            if (square.ne != null) {
                movingStatic.addAll(square.ne.getMovingObjects());
            }

            int int0 = 0;

            for (int int1 = 0; int1 < movingStatic.size(); int1++) {
                IsoZombie zombie0 = Type.tryCastTo(movingStatic.get(int1), IsoZombie.class);
                if (zombie0 != null
                    && zombie0.target == this
                    && !(this.DistToSquared(zombie0) >= 0.80999994F)
                    && (
                        zombie0.isCurrentState(AttackState.instance())
                            || zombie0.isCurrentState(AttackNetworkState.instance())
                            || zombie0.isCurrentState(LungeState.instance())
                            || zombie0.isCurrentState(LungeNetworkState.instance())
                    )) {
                    int0++;
                }
            }

            return int0;
        }
    }

    public float checkIsNearWall() {
        if (this.bSneaking && this.getCurrentSquare() != null) {
            IsoGridSquare square0 = this.getCurrentSquare().nav[IsoDirections.N.index()];
            IsoGridSquare square1 = this.getCurrentSquare().nav[IsoDirections.S.index()];
            IsoGridSquare square2 = this.getCurrentSquare().nav[IsoDirections.E.index()];
            IsoGridSquare square3 = this.getCurrentSquare().nav[IsoDirections.W.index()];
            float float0 = 0.0F;
            float float1 = 0.0F;
            if (square0 != null) {
                float0 = square0.getGridSneakModifier(true);
                if (float0 > 1.0F) {
                    this.setVariable("nearWallCrouching", true);
                    return float0;
                }
            }

            if (square1 != null) {
                float0 = square1.getGridSneakModifier(false);
                float1 = square1.getGridSneakModifier(true);
                if (float0 > 1.0F || float1 > 1.0F) {
                    this.setVariable("nearWallCrouching", true);
                    return float0 > 1.0F ? float0 : float1;
                }
            }

            if (square2 != null) {
                float0 = square2.getGridSneakModifier(false);
                float1 = square2.getGridSneakModifier(true);
                if (float0 > 1.0F || float1 > 1.0F) {
                    this.setVariable("nearWallCrouching", true);
                    return float0 > 1.0F ? float0 : float1;
                }
            }

            if (square3 != null) {
                float0 = square3.getGridSneakModifier(false);
                float1 = square3.getGridSneakModifier(true);
                if (float0 > 1.0F || float1 > 1.0F) {
                    this.setVariable("nearWallCrouching", true);
                    return float0 > 1.0F ? float0 : float1;
                }
            }

            float0 = this.getCurrentSquare().getGridSneakModifier(false);
            if (float0 > 1.0F) {
                this.setVariable("nearWallCrouching", true);
                return float0;
            } else if (this instanceof IsoPlayer && ((IsoPlayer)this).isNearVehicle()) {
                this.setVariable("nearWallCrouching", true);
                return 6.0F;
            } else {
                this.setVariable("nearWallCrouching", false);
                return 0.0F;
            }
        } else {
            this.setVariable("nearWallCrouching", false);
            return 0.0F;
        }
    }

    public float getBeenSprintingFor() {
        return this.BeenSprintingFor;
    }

    public void setBeenSprintingFor(float beenSprintingFor) {
        if (beenSprintingFor < 0.0F) {
            beenSprintingFor = 0.0F;
        }

        if (beenSprintingFor > 100.0F) {
            beenSprintingFor = 100.0F;
        }

        this.BeenSprintingFor = beenSprintingFor;
    }

    public boolean isHideWeaponModel() {
        return this.hideWeaponModel;
    }

    public void setHideWeaponModel(boolean _hideWeaponModel) {
        if (this.hideWeaponModel != _hideWeaponModel) {
            this.hideWeaponModel = _hideWeaponModel;
            this.resetEquippedHandsModels();
        }
    }

    public void setIsAiming(boolean aIsAiming) {
        if (this.ignoreAimingInput) {
            aIsAiming = false;
        }

        if (this instanceof IsoPlayer && !((IsoPlayer)this).isAttackAnimThrowTimeOut() || this.isAttackAnim() || this.getVariableBoolean("ShoveAnim")) {
            aIsAiming = true;
        }

        this.isAiming = aIsAiming;
    }

    @Override
    public boolean isAiming() {
        if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer() && DebugOptions.instance.MultiplayerAttackPlayer.getValue()) {
            return false;
        } else {
            return this.isNPC ? this.NPCGetAiming() : this.isAiming;
        }
    }

    @Override
    public void resetBeardGrowingTime() {
        this.beardGrowTiming = (float)this.getHoursSurvived();
        if (GameClient.bClient && this instanceof IsoPlayer) {
            GameClient.instance.sendVisual((IsoPlayer)this);
        }
    }

    @Override
    public void resetHairGrowingTime() {
        this.hairGrowTiming = (float)this.getHoursSurvived();
        if (GameClient.bClient && this instanceof IsoPlayer) {
            GameClient.instance.sendVisual((IsoPlayer)this);
        }
    }

    public void fallenOnKnees() {
        if (!(this instanceof IsoPlayer) || ((IsoPlayer)this).isLocalPlayer()) {
            if (!this.isInvincible()) {
                this.helmetFall(false);
                BloodBodyPartType bloodBodyPartType0 = BloodBodyPartType.FromIndex(
                    Rand.Next(BloodBodyPartType.Hand_L.index(), BloodBodyPartType.Torso_Upper.index())
                );
                if (Rand.NextBool(2)) {
                    bloodBodyPartType0 = BloodBodyPartType.FromIndex(Rand.Next(BloodBodyPartType.UpperLeg_L.index(), BloodBodyPartType.Back.index()));
                }

                for (int int0 = 0; int0 < 4; int0++) {
                    BloodBodyPartType bloodBodyPartType1 = BloodBodyPartType.FromIndex(
                        Rand.Next(BloodBodyPartType.Hand_L.index(), BloodBodyPartType.Torso_Upper.index())
                    );
                    if (Rand.NextBool(2)) {
                        bloodBodyPartType1 = BloodBodyPartType.FromIndex(Rand.Next(BloodBodyPartType.UpperLeg_L.index(), BloodBodyPartType.Back.index()));
                    }

                    this.addDirt(bloodBodyPartType1, Rand.Next(2, 6), false);
                }

                if (Rand.NextBool(2)) {
                    if (Rand.NextBool(4)) {
                        this.dropHandItems();
                    }

                    this.addHole(bloodBodyPartType0);
                    this.addBlood(bloodBodyPartType0, true, false, false);
                    BodyPart bodyPart = this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(bloodBodyPartType0.index()));
                    if (bodyPart.scratched()) {
                        bodyPart.generateDeepWound();
                    } else {
                        bodyPart.setScratched(true, true);
                    }
                }
            }
        }
    }

    public void addVisualDamage(String itemType) {
        this.addBodyVisualFromItemType("Base." + itemType);
    }

    protected ItemVisual addBodyVisualFromItemType(String string) {
        Item item = ScriptManager.instance.getItem(string);
        return item != null && !StringUtils.isNullOrWhitespace(item.getClothingItem()) ? this.addBodyVisualFromClothingItemName(item.getClothingItem()) : null;
    }

    protected ItemVisual addBodyVisualFromClothingItemName(String string1) {
        IHumanVisual iHumanVisual = Type.tryCastTo(this, IHumanVisual.class);
        if (iHumanVisual == null) {
            return null;
        } else {
            String string0 = ScriptManager.instance.getItemTypeForClothingItem(string1);
            if (string0 == null) {
                return null;
            } else {
                Item item = ScriptManager.instance.getItem(string0);
                if (item == null) {
                    return null;
                } else {
                    ClothingItem clothingItem = item.getClothingItemAsset();
                    if (clothingItem == null) {
                        return null;
                    } else {
                        ClothingItemReference clothingItemReference = new ClothingItemReference();
                        clothingItemReference.itemGUID = clothingItem.m_GUID;
                        clothingItemReference.randomize();
                        ItemVisual itemVisual = new ItemVisual();
                        itemVisual.setItemType(string0);
                        itemVisual.synchWithOutfit(clothingItemReference);
                        if (!this.isDuplicateBodyVisual(itemVisual)) {
                            ItemVisuals itemVisuals = iHumanVisual.getHumanVisual().getBodyVisuals();
                            itemVisuals.add(itemVisual);
                            return itemVisual;
                        } else {
                            return null;
                        }
                    }
                }
            }
        }
    }

    protected boolean isDuplicateBodyVisual(ItemVisual itemVisual1) {
        IHumanVisual iHumanVisual = Type.tryCastTo(this, IHumanVisual.class);
        if (iHumanVisual == null) {
            return false;
        } else {
            ItemVisuals itemVisuals = iHumanVisual.getHumanVisual().getBodyVisuals();

            for (int int0 = 0; int0 < itemVisuals.size(); int0++) {
                ItemVisual itemVisual0 = itemVisuals.get(int0);
                if (itemVisual1.getClothingItemName().equals(itemVisual0.getClothingItemName())
                    && itemVisual1.getTextureChoice() == itemVisual0.getTextureChoice()
                    && itemVisual1.getBaseTexture() == itemVisual0.getBaseTexture()) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean isCriticalHit() {
        return this.isCrit;
    }

    public void setCriticalHit(boolean _isCrit) {
        this.isCrit = _isCrit;
    }

    public float getRunSpeedModifier() {
        return this.runSpeedModifier;
    }

    public void startMuzzleFlash() {
        float float0 = GameTime.getInstance().getNight() * 0.8F;
        float0 = Math.max(float0, 0.2F);
        IsoLightSource lightSource = new IsoLightSource(
            (int)this.getX(), (int)this.getY(), (int)this.getZ(), 0.8F * float0, 0.8F * float0, 0.6F * float0, 18, 6
        );
        IsoWorld.instance.CurrentCell.getLamppostPositions().add(lightSource);
        this.m_muzzleFlash = System.currentTimeMillis();
    }

    public boolean isMuzzleFlash() {
        return Core.bDebug && DebugOptions.instance.ModelRenderMuzzleflash.getValue() ? true : this.m_muzzleFlash > System.currentTimeMillis() - 50L;
    }

    public boolean isNPC() {
        return this.isNPC;
    }

    public void setNPC(boolean newvalue) {
        if (newvalue && this.GameCharacterAIBrain == null) {
            this.GameCharacterAIBrain = new GameCharacterAIBrain(this);
        }

        this.isNPC = newvalue;
    }

    public void NPCSetRunning(boolean newvalue) {
        this.GameCharacterAIBrain.HumanControlVars.bRunning = newvalue;
    }

    public boolean NPCGetRunning() {
        return this.GameCharacterAIBrain.HumanControlVars.bRunning;
    }

    public void NPCSetJustMoved(boolean newvalue) {
        this.GameCharacterAIBrain.HumanControlVars.JustMoved = newvalue;
    }

    public void NPCSetAiming(boolean _isAiming) {
        this.GameCharacterAIBrain.HumanControlVars.bAiming = _isAiming;
    }

    public boolean NPCGetAiming() {
        return this.GameCharacterAIBrain.HumanControlVars.bAiming;
    }

    public void NPCSetAttack(boolean newvalue) {
        this.GameCharacterAIBrain.HumanControlVars.initiateAttack = newvalue;
    }

    public void NPCSetMelee(boolean newvalue) {
        this.GameCharacterAIBrain.HumanControlVars.bMelee = newvalue;
    }

    public void setMetabolicTarget(Metabolics m) {
        if (m != null) {
            this.setMetabolicTarget(m.getMet());
        }
    }

    public void setMetabolicTarget(float target) {
        if (this.getBodyDamage() != null && this.getBodyDamage().getThermoregulator() != null) {
            this.getBodyDamage().getThermoregulator().setMetabolicTarget(target);
        }
    }

    public double getThirstMultiplier() {
        return this.getBodyDamage() != null && this.getBodyDamage().getThermoregulator() != null
            ? this.getBodyDamage().getThermoregulator().getFluidsMultiplier()
            : 1.0;
    }

    public double getHungerMultiplier() {
        return 1.0;
    }

    public double getFatiqueMultiplier() {
        return this.getBodyDamage() != null && this.getBodyDamage().getThermoregulator() != null
            ? this.getBodyDamage().getThermoregulator().getFatigueMultiplier()
            : 1.0;
    }

    public float getTimedActionTimeModifier() {
        return 1.0F;
    }

    public boolean addHoleFromZombieAttacks(BloodBodyPartType part, boolean scratch) {
        this.getItemVisuals(tempItemVisuals);
        ItemVisual itemVisual0 = null;

        for (int int0 = tempItemVisuals.size() - 1; int0 >= 0; int0--) {
            ItemVisual itemVisual1 = tempItemVisuals.get(int0);
            Item item = itemVisual1.getScriptItem();
            if (item != null) {
                ArrayList arrayList0 = item.getBloodClothingType();
                if (arrayList0 != null) {
                    ArrayList arrayList1 = BloodClothingType.getCoveredParts(arrayList0);

                    for (int int1 = 0; int1 < arrayList1.size(); int1++) {
                        BloodBodyPartType bloodBodyPartType = (BloodBodyPartType)arrayList1.get(int1);
                        if (part == bloodBodyPartType) {
                            itemVisual0 = itemVisual1;
                            break;
                        }
                    }

                    if (itemVisual0 != null) {
                        break;
                    }
                }
            }
        }

        float float0 = 0.0F;
        boolean boolean0 = false;
        if (itemVisual0 != null && itemVisual0.getInventoryItem() != null && itemVisual0.getInventoryItem() instanceof Clothing) {
            Clothing clothing = (Clothing)itemVisual0.getInventoryItem();
            Clothing.ClothingPatch clothingPatch = clothing.getPatchType(part);
            float0 = Math.max(30.0F, 100.0F - clothing.getDefForPart(part, !scratch, false) / 1.5F);
        }

        if (Rand.Next(100) < float0) {
            boolean boolean1 = this.addHole(part);
            if (boolean1) {
                this.getEmitter().playSoundImpl("ZombieRipClothing", (IsoObject)null);
            }

            boolean0 = true;
        }

        return boolean0;
    }

    protected void updateBandages() {
        s_bandages.update(this);
    }

    public float getTotalBlood() {
        float float0 = 0.0F;
        if (this.getWornItems() == null) {
            return float0;
        } else {
            for (int int0 = 0; int0 < this.getWornItems().size(); int0++) {
                InventoryItem item = this.getWornItems().get(int0).getItem();
                if (item instanceof Clothing) {
                    float0 += ((Clothing)item).getBloodlevel();
                }
            }

            return float0 + ((HumanVisual)this.getVisual()).getTotalBlood();
        }
    }

    public void attackFromWindowsLunge(IsoZombie zombie) {
        if (!(this.lungeFallTimer > 0.0F)
            && (int)this.getZ() == (int)zombie.getZ()
            && !zombie.isDead()
            && this.getCurrentSquare() != null
            && !this.getCurrentSquare().isDoorBlockedTo(zombie.getCurrentSquare())
            && !this.getCurrentSquare().isWallTo(zombie.getCurrentSquare())
            && !this.getCurrentSquare().isWindowTo(zombie.getCurrentSquare())) {
            if (this.getVehicle() == null) {
                boolean boolean0 = this.DoSwingCollisionBoneCheck(zombie, zombie.getAnimationPlayer().getSkinningBoneIndex("Bip01_R_Hand", -1), 1.0F);
                if (boolean0) {
                    zombie.playSound("ZombieCrawlLungeHit");
                    this.lungeFallTimer = 200.0F;
                    this.setIsAiming(false);
                    boolean boolean1 = false;
                    int int0 = 30;
                    int0 += this.getMoodles().getMoodleLevel(MoodleType.Endurance) * 3;
                    int0 += this.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 5;
                    int0 -= this.getPerkLevel(PerkFactory.Perks.Fitness) * 2;
                    BodyPart bodyPart = this.getBodyDamage().getBodyPart(BodyPartType.Torso_Lower);
                    if (bodyPart.getAdditionalPain(true) > 20.0F) {
                        int0 = (int)(int0 + (bodyPart.getAdditionalPain(true) - 20.0F) / 10.0F);
                    }

                    if (this.Traits.Clumsy.isSet()) {
                        int0 += 10;
                    }

                    if (this.Traits.Graceful.isSet()) {
                        int0 -= 10;
                    }

                    if (this.Traits.VeryUnderweight.isSet()) {
                        int0 += 20;
                    }

                    if (this.Traits.Underweight.isSet()) {
                        int0 += 10;
                    }

                    if (this.Traits.Obese.isSet()) {
                        int0 -= 10;
                    }

                    if (this.Traits.Overweight.isSet()) {
                        int0 -= 5;
                    }

                    int0 = Math.max(5, int0);
                    this.clearVariable("BumpFallType");
                    this.setBumpType("stagger");
                    if (Rand.Next(100) < int0) {
                        boolean1 = true;
                    }

                    this.setBumpDone(false);
                    this.setBumpFall(boolean1);
                    if (zombie.isBehind(this)) {
                        this.setBumpFallType("pushedBehind");
                    } else {
                        this.setBumpFallType("pushedFront");
                    }

                    this.actionContext.reportEvent("wasBumped");
                }
            }
        }
    }

    public boolean DoSwingCollisionBoneCheck(IsoGameCharacter zombie, int bone, float tempoLengthTest) {
        Model.BoneToWorldCoords(zombie, bone, tempVectorBonePos);
        float float0 = IsoUtils.DistanceToSquared(tempVectorBonePos.x, tempVectorBonePos.y, this.x, this.y);
        return float0 < tempoLengthTest * tempoLengthTest;
    }

    public boolean isInvincible() {
        return this.invincible;
    }

    public void setInvincible(boolean _invincible) {
        this.invincible = _invincible;
    }

    public BaseVehicle getNearVehicle() {
        if (this.getVehicle() != null) {
            return null;
        } else {
            int int0 = ((int)this.x - 4) / 10 - 1;
            int int1 = ((int)this.y - 4) / 10 - 1;
            int int2 = (int)Math.ceil((this.x + 4.0F) / 10.0F) + 1;
            int int3 = (int)Math.ceil((this.y + 4.0F) / 10.0F) + 1;

            for (int int4 = int1; int4 < int3; int4++) {
                for (int int5 = int0; int5 < int2; int5++) {
                    IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int5, int4) : IsoWorld.instance.CurrentCell.getChunk(int5, int4);
                    if (chunk != null) {
                        for (int int6 = 0; int6 < chunk.vehicles.size(); int6++) {
                            BaseVehicle vehiclex = chunk.vehicles.get(int6);
                            if (vehiclex.getScript() != null
                                && (int)this.getZ() == (int)vehiclex.getZ()
                                && (
                                    !(this instanceof IsoPlayer)
                                        || !((IsoPlayer)this).isLocalPlayer()
                                        || vehiclex.getTargetAlpha(((IsoPlayer)this).PlayerIndex) != 0.0F
                                )
                                && !(this.DistToSquared((int)vehiclex.x, (int)vehiclex.y) >= 16.0F)) {
                                return vehiclex;
                            }
                        }
                    }
                }
            }

            return null;
        }
    }

    private IsoGridSquare getSolidFloorAt(int int1, int int2, int int0) {
        while (int0 >= 0) {
            IsoGridSquare square = this.getCell().getGridSquare(int1, int2, int0);
            if (square != null && square.TreatAsSolidFloor()) {
                return square;
            }

            int0--;
        }

        return null;
    }

    public void dropHeavyItems() {
        IsoGridSquare square = this.getCurrentSquare();
        if (square != null) {
            InventoryItem item0 = this.getPrimaryHandItem();
            InventoryItem item1 = this.getSecondaryHandItem();
            if (item0 != null || item1 != null) {
                square = this.getSolidFloorAt(square.x, square.y, square.z);
                if (square != null) {
                    boolean boolean0 = item0 == item1;
                    if (this.isHeavyItem(item0)) {
                        float float0 = Rand.Next(0.1F, 0.9F);
                        float float1 = Rand.Next(0.1F, 0.9F);
                        float float2 = square.getApparentZ(float0, float1) - square.getZ();
                        this.setPrimaryHandItem(null);
                        this.getInventory().DoRemoveItem(item0);
                        square.AddWorldInventoryItem(item0, float0, float1, float2);
                        LuaEventManager.triggerEvent("OnContainerUpdate");
                        LuaEventManager.triggerEvent("onItemFall", item0);
                    }

                    if (this.isHeavyItem(item1)) {
                        this.setSecondaryHandItem(null);
                        if (!boolean0) {
                            float float3 = Rand.Next(0.1F, 0.9F);
                            float float4 = Rand.Next(0.1F, 0.9F);
                            float float5 = square.getApparentZ(float3, float4) - square.getZ();
                            this.getInventory().DoRemoveItem(item1);
                            square.AddWorldInventoryItem(item1, float3, float4, float5);
                            LuaEventManager.triggerEvent("OnContainerUpdate");
                            LuaEventManager.triggerEvent("onItemFall", item1);
                        }
                    }
                }
            }
        }
    }

    public boolean isHeavyItem(InventoryItem item) {
        if (item == null) {
            return false;
        } else if (item instanceof InventoryContainer) {
            return true;
        } else if (item.hasTag("HeavyItem")) {
            return true;
        } else {
            return !item.getType().equals("CorpseMale") && !item.getType().equals("CorpseFemale") ? item.getType().equals("Generator") : true;
        }
    }

    public boolean isCanShout() {
        return this.canShout;
    }

    public void setCanShout(boolean _canShout) {
        this.canShout = _canShout;
    }

    public boolean isUnlimitedEndurance() {
        return this.unlimitedEndurance;
    }

    public void setUnlimitedEndurance(boolean _unlimitedEndurance) {
        this.unlimitedEndurance = _unlimitedEndurance;
    }

    private void addActiveLightItem(InventoryItem item, ArrayList<InventoryItem> arrayList) {
        if (item != null && item.isEmittingLight() && !arrayList.contains(item)) {
            arrayList.add(item);
        }
    }

    public ArrayList<InventoryItem> getActiveLightItems(ArrayList<InventoryItem> items) {
        this.addActiveLightItem(this.getSecondaryHandItem(), items);
        this.addActiveLightItem(this.getPrimaryHandItem(), items);
        AttachedItems attachedItemsx = this.getAttachedItems();

        for (int int0 = 0; int0 < attachedItemsx.size(); int0++) {
            InventoryItem item = attachedItemsx.getItemByIndex(int0);
            this.addActiveLightItem(item, items);
        }

        return items;
    }

    public SleepingEventData getOrCreateSleepingEventData() {
        if (this.m_sleepingEventData == null) {
            this.m_sleepingEventData = new SleepingEventData();
        }

        return this.m_sleepingEventData;
    }

    public void playEmote(String emote) {
        this.setVariable("emote", emote);
        this.actionContext.reportEvent("EventEmote");
    }

    public String getAnimationStateName() {
        return this.advancedAnimator.getCurrentStateName();
    }

    public String getActionStateName() {
        return this.actionContext.getCurrentStateName();
    }

    public boolean shouldWaitToStartTimedAction() {
        if (this.isSitOnGround()) {
            AdvancedAnimator advancedAnimatorx = this.getAdvancedAnimator();
            if (advancedAnimatorx.getRootLayer() == null) {
                return false;
            } else if (advancedAnimatorx.animSet != null && advancedAnimatorx.animSet.containsState("sitonground")) {
                AnimState animState = advancedAnimatorx.animSet.GetState("sitonground");
                if (!PZArrayUtil.contains(animState.m_Nodes, animNode -> "sit_action".equalsIgnoreCase(animNode.m_Name))) {
                    return false;
                } else {
                    LiveAnimNode liveAnimNode = PZArrayUtil.find(
                        advancedAnimatorx.getRootLayer().getLiveAnimNodes(),
                        liveAnimNodex -> liveAnimNodex.isActive() && "sit_action".equalsIgnoreCase(liveAnimNodex.getName())
                    );
                    return liveAnimNode == null || !liveAnimNode.isMainAnimActive();
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void setPersistentOutfitID(int outfitID) {
        this.setPersistentOutfitID(outfitID, false);
    }

    public void setPersistentOutfitID(int outfitID, boolean init) {
        this.m_persistentOutfitId = outfitID;
        this.m_bPersistentOutfitInit = init;
    }

    public int getPersistentOutfitID() {
        return this.m_persistentOutfitId;
    }

    public boolean isPersistentOutfitInit() {
        return this.m_bPersistentOutfitInit;
    }

    public boolean isDoingActionThatCanBeCancelled() {
        return false;
    }

    public boolean isDoDeathSound() {
        return this.doDeathSound;
    }

    public void setDoDeathSound(boolean _doDeathSound) {
        this.doDeathSound = _doDeathSound;
    }

    public void updateEquippedRadioFreq() {
        this.invRadioFreq.clear();

        for (int int0 = 0; int0 < this.getInventory().getItems().size(); int0++) {
            InventoryItem item = this.getInventory().getItems().get(int0);
            if (item instanceof Radio radio
                && radio.getDeviceData() != null
                && radio.getDeviceData().getIsTurnedOn()
                && !radio.getDeviceData().getMicIsMuted()
                && !this.invRadioFreq.contains(radio.getDeviceData().getChannel())) {
                this.invRadioFreq.add(radio.getDeviceData().getChannel());
            }
        }

        for (int int1 = 0; int1 < this.invRadioFreq.size(); int1++) {
            System.out.println(this.invRadioFreq.get(int1));
        }

        if (this instanceof IsoPlayer && GameClient.bClient) {
            GameClient.sendEquippedRadioFreq((IsoPlayer)this);
        }
    }

    @Override
    public FMODParameterList getFMODParameters() {
        return this.fmodParameters;
    }

    @Override
    public void startEvent(long eventInstance, GameSoundClip clip, BitSet parameterSet) {
        FMODParameterList fMODParameterList = this.getFMODParameters();
        ArrayList arrayList = clip.eventDescription.parameters;

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            FMOD_STUDIO_PARAMETER_DESCRIPTION fmod_studio_parameter_description = (FMOD_STUDIO_PARAMETER_DESCRIPTION)arrayList.get(int0);
            if (!parameterSet.get(fmod_studio_parameter_description.globalIndex)) {
                FMODParameter fMODParameter = fMODParameterList.get(fmod_studio_parameter_description);
                if (fMODParameter != null) {
                    fMODParameter.startEventInstance(eventInstance);
                }
            }
        }
    }

    @Override
    public void updateEvent(long eventInstance, GameSoundClip clip) {
    }

    @Override
    public void stopEvent(long eventInstance, GameSoundClip clip, BitSet parameterSet) {
        FMODParameterList fMODParameterList = this.getFMODParameters();
        ArrayList arrayList = clip.eventDescription.parameters;

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            FMOD_STUDIO_PARAMETER_DESCRIPTION fmod_studio_parameter_description = (FMOD_STUDIO_PARAMETER_DESCRIPTION)arrayList.get(int0);
            if (!parameterSet.get(fmod_studio_parameter_description.globalIndex)) {
                FMODParameter fMODParameter = fMODParameterList.get(fmod_studio_parameter_description);
                if (fMODParameter != null) {
                    fMODParameter.stopEventInstance(eventInstance);
                }
            }
        }
    }

    public void playBloodSplatterSound() {
        if (this.getEmitter().isPlaying("BloodSplatter")) {
        }

        this.getEmitter().playSoundImpl("BloodSplatter", this);
    }

    public void setIgnoreAimingInput(boolean b) {
        this.ignoreAimingInput = b;
    }

    public void addBlood(float speed) {
        if (!(Rand.Next(10) > speed)) {
            if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
                int int0 = Rand.Next(4, 10);
                if (int0 < 1) {
                    int0 = 1;
                }

                if (Core.bLastStand) {
                    int0 *= 3;
                }

                switch (SandboxOptions.instance.BloodLevel.getValue()) {
                    case 2:
                        int0 /= 2;
                    case 3:
                    default:
                        break;
                    case 4:
                        int0 *= 2;
                        break;
                    case 5:
                        int0 *= 5;
                }

                for (int int1 = 0; int1 < int0; int1++) {
                    this.splatBlood(2, 0.3F);
                }
            }

            if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
                this.splatBloodFloorBig();
                this.playBloodSplatterSound();
            }
        }
    }

    public boolean isKnockedDown() {
        return this.bKnockedDown;
    }

    public void setKnockedDown(boolean _bKnockedDown) {
        this.bKnockedDown = _bKnockedDown;
    }

    public void writeInventory(ByteBuffer b) {
        String string = this.isFemale() ? "inventoryfemale" : "inventorymale";
        GameWindow.WriteString(b, string);
        if (this.getInventory() != null) {
            b.put((byte)1);

            try {
                ArrayList arrayList = this.getInventory().save(b);
                WornItems wornItemsx = this.getWornItems();
                if (wornItemsx == null) {
                    byte byte0 = 0;
                    b.put((byte)byte0);
                } else {
                    int int0 = wornItemsx.size();
                    if (int0 > 127) {
                        DebugLog.Multiplayer.warn("Too many worn items");
                        int0 = 127;
                    }

                    b.put((byte)int0);

                    for (int int1 = 0; int1 < int0; int1++) {
                        WornItem wornItem = wornItemsx.get(int1);
                        GameWindow.WriteString(b, wornItem.getLocation());
                        b.putShort((short)arrayList.indexOf(wornItem.getItem()));
                    }
                }

                AttachedItems attachedItemsx = this.getAttachedItems();
                if (attachedItemsx == null) {
                    boolean boolean0 = false;
                    b.put((byte)0);
                } else {
                    int int2 = attachedItemsx.size();
                    if (int2 > 127) {
                        DebugLog.Multiplayer.warn("Too many attached items");
                        int2 = 127;
                    }

                    b.put((byte)int2);

                    for (int int3 = 0; int3 < int2; int3++) {
                        AttachedItem attachedItem = attachedItemsx.get(int3);
                        GameWindow.WriteString(b, attachedItem.getLocation());
                        b.putShort((short)arrayList.indexOf(attachedItem.getItem()));
                    }
                }
            } catch (IOException iOException) {
                DebugLog.Multiplayer.printException(iOException, "WriteInventory error for character " + this.getOnlineID(), LogSeverity.Error);
            }
        } else {
            b.put((byte)0);
        }
    }

    public String readInventory(ByteBuffer b) {
        String string0 = GameWindow.ReadString(b);
        boolean boolean0 = b.get() == 1;
        if (boolean0) {
            try {
                ArrayList arrayList = this.getInventory().load(b, IsoWorld.getWorldVersion());
                byte byte0 = b.get();

                for (int int0 = 0; int0 < byte0; int0++) {
                    String string1 = GameWindow.ReadStringUTF(b);
                    short short0 = b.getShort();
                    if (short0 >= 0 && short0 < arrayList.size() && this.getBodyLocationGroup().getLocation(string1) != null) {
                        this.getWornItems().setItem(string1, (InventoryItem)arrayList.get(short0));
                    }
                }

                byte byte1 = b.get();

                for (int int1 = 0; int1 < byte1; int1++) {
                    String string2 = GameWindow.ReadStringUTF(b);
                    short short1 = b.getShort();
                    if (short1 >= 0 && short1 < arrayList.size() && this.getAttachedLocationGroup().getLocation(string2) != null) {
                        this.getAttachedItems().setItem(string2, (InventoryItem)arrayList.get(short1));
                    }
                }
            } catch (IOException iOException) {
                DebugLog.Multiplayer.printException(iOException, "ReadInventory error for character " + this.getOnlineID(), LogSeverity.Error);
            }
        }

        return string0;
    }

    public void Kill(IsoGameCharacter killer) {
        DebugLog.Death.trace("id=%d", this.getOnlineID());
        this.setAttackedBy(killer);
        this.setHealth(0.0F);
        this.setOnKillDone(true);
    }

    public boolean shouldDoInventory() {
        return true;
    }

    public void die() {
        if (!this.isOnDeathDone()) {
            if (GameClient.bClient) {
                if (this.shouldDoInventory()) {
                    this.becomeCorpse();
                } else {
                    this.getNetworkCharacterAI().processDeadBody();
                }
            } else {
                this.becomeCorpse();
            }
        }
    }

    public void becomeCorpse() {
        DebugLog.Death.trace("id=%d", this.getOnlineID());
        this.Kill(this.getAttackedBy());
        this.setOnDeathDone(true);
    }

    public boolean shouldBecomeCorpse() {
        if (GameClient.bClient || GameServer.bServer) {
            if (this.getHitReactionNetworkAI().isSetup() || this.getHitReactionNetworkAI().isStarted()) {
                return false;
            }

            if (GameServer.bServer) {
                return this.getNetworkCharacterAI().isSetDeadBody();
            }

            if (GameClient.bClient) {
                return this.isCurrentState(ZombieOnGroundState.instance()) || this.isCurrentState(PlayerOnGroundState.instance());
            }
        }

        return true;
    }

    public HitReactionNetworkAI getHitReactionNetworkAI() {
        return null;
    }

    public NetworkCharacterAI getNetworkCharacterAI() {
        return null;
    }

    public boolean isLocal() {
        return !GameClient.bClient && !GameServer.bServer;
    }

    public boolean isVehicleCollisionActive(BaseVehicle testVehicle) {
        if (!GameClient.bClient) {
            return false;
        } else if (!this.isAlive()) {
            return false;
        } else if (testVehicle == null) {
            return false;
        } else if (!testVehicle.shouldCollideWithCharacters()) {
            return false;
        } else if (testVehicle.isNetPlayerAuthorization(BaseVehicle.Authorization.Server)) {
            return false;
        } else if (testVehicle.isEngineRunning()
            || testVehicle.getVehicleTowing() != null && testVehicle.getVehicleTowing().isEngineRunning()
            || testVehicle.getVehicleTowedBy() != null && testVehicle.getVehicleTowedBy().isEngineRunning()) {
            if (testVehicle.getDriver() != null
                || testVehicle.getVehicleTowing() != null && testVehicle.getVehicleTowing().getDriver() != null
                || testVehicle.getVehicleTowedBy() != null && testVehicle.getVehicleTowedBy().getDriver() != null) {
                return Math.abs(testVehicle.x - this.x) < 0.01F || Math.abs(testVehicle.y - this.y) < 0.01F
                    ? false
                    : (!this.isKnockedDown() || this.isOnFloor()) && (this.getHitReactionNetworkAI() == null || !this.getHitReactionNetworkAI().isStarted());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void doHitByVehicle(BaseVehicle baseVehicle, BaseVehicle.HitVars hitVars) {
        if (GameClient.bClient) {
            IsoPlayer player = GameClient.IDToPlayerMap.get(baseVehicle.getNetPlayerId());
            if (player != null) {
                if (player.isLocal()) {
                    SoundManager.instance.PlayWorldSound("VehicleHitCharacter", this.getCurrentSquare(), 0.0F, 20.0F, 0.9F, true);
                    float float0 = this.Hit(baseVehicle, hitVars.hitSpeed, hitVars.isTargetHitFromBehind, -hitVars.targetImpulse.x, -hitVars.targetImpulse.z);
                    GameClient.sendHitVehicle(
                        player,
                        this,
                        baseVehicle,
                        float0,
                        hitVars.isTargetHitFromBehind,
                        hitVars.vehicleDamage,
                        hitVars.hitSpeed,
                        hitVars.isVehicleHitFromFront
                    );
                } else {
                    this.getNetworkCharacterAI().resetVehicleHitTimeout();
                }
            }
        } else if (!GameServer.bServer) {
            BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter(this.x, this.y, this.z);
            long long0 = baseSoundEmitter.playSound("VehicleHitCharacter");
            baseSoundEmitter.setParameterValue(long0, FMODManager.instance.getParameterDescription("VehicleSpeed"), baseVehicle.getCurrentSpeedKmHour());
            this.Hit(baseVehicle, hitVars.hitSpeed, hitVars.isTargetHitFromBehind, -hitVars.targetImpulse.x, -hitVars.targetImpulse.z);
        }
    }

    public boolean isSkipResolveCollision() {
        return this instanceof IsoZombie
                && (
                    this.isCurrentState(ZombieHitReactionState.instance())
                        || this.isCurrentState(ZombieFallDownState.instance())
                        || this.isCurrentState(ZombieOnGroundState.instance())
                        || this.isCurrentState(StaggerBackState.instance())
                )
            || this instanceof IsoPlayer
                && !this.isLocal()
                && (
                    this.isCurrentState(PlayerFallDownState.instance())
                        || this.isCurrentState(BumpedState.instance())
                        || this.isCurrentState(PlayerKnockedDown.instance())
                        || this.isCurrentState(PlayerHitReactionState.instance())
                        || this.isCurrentState(PlayerHitReactionPVPState.instance())
                        || this.isCurrentState(PlayerOnGroundState.instance())
                );
    }

    public boolean isAttackAnim() {
        return this.attackAnim;
    }

    public void setAttackAnim(boolean _attackAnim) {
        this.attackAnim = _attackAnim;
    }

    public Float getNextAnimationTranslationLength() {
        if (this.getActionContext() != null && this.getAnimationPlayer() != null && this.getAdvancedAnimator() != null) {
            ActionState actionState = this.getActionContext().getNextState();
            if (actionState != null && !StringUtils.isNullOrEmpty(actionState.getName())) {
                ArrayList arrayList = new ArrayList();
                this.getAdvancedAnimator().animSet.GetState(actionState.getName()).getAnimNodes(this, arrayList);

                for (AnimNode animNode : arrayList) {
                    if (!StringUtils.isNullOrEmpty(animNode.m_AnimName)) {
                        AnimationClip animationClip = this.getAnimationPlayer().getSkinningData().AnimationClips.get(animNode.m_AnimName);
                        if (animationClip != null) {
                            return animationClip.getTranslationLength(animNode.m_deferredBoneAxis);
                        }
                    }
                }
            }
        }

        return null;
    }

    public Float calcHitDir(IsoGameCharacter wielder, HandWeapon weapon, Vector2 out) {
        Float float0 = this.getNextAnimationTranslationLength();
        out.set(this.getX() - wielder.getX(), this.getY() - wielder.getY()).normalize();
        if (float0 == null) {
            out.setLength(this.getHitForce() * 0.1F);
            out.scale(weapon.getPushBackMod());
            out.rotate(weapon.HitAngleMod);
        } else {
            out.scale(float0);
        }

        return null;
    }

    public void calcHitDir(Vector2 out) {
        out.set(this.getHitDir());
        out.setLength(this.getHitForce());
    }

    @Override
    public Safety getSafety() {
        return this.safety;
    }

    @Override
    public void setSafety(Safety _safety) {
        this.safety.copyFrom(_safety);
    }

    public void burnCorpse(IsoDeadBody corpse) {
        if (GameClient.bClient) {
            GameClient.sendBurnCorpse(this.getOnlineID(), corpse.getObjectID());
        } else {
            IsoFireManager.StartFire(corpse.getCell(), corpse.getSquare(), true, 100, 700);
        }
    }

    private static final class Bandages {
        final HashMap<String, String> bandageTypeMap = new HashMap<>();
        final THashMap<String, InventoryItem> itemMap = new THashMap<>();

        String getBloodBandageType(String string1) {
            String string0 = this.bandageTypeMap.get(string1);
            if (string0 == null) {
                this.bandageTypeMap.put(string1, string0 = string1 + "_Blood");
            }

            return string0;
        }

        void update(IsoGameCharacter character) {
            if (!GameServer.bServer) {
                BodyDamage bodyDamage = character.getBodyDamage();
                WornItems wornItems = character.getWornItems();
                if (bodyDamage != null && wornItems != null) {
                    assert !(character instanceof IsoZombie);

                    this.itemMap.clear();

                    for (int int0 = 0; int0 < wornItems.size(); int0++) {
                        InventoryItem item = wornItems.getItemByIndex(int0);
                        if (item != null) {
                            this.itemMap.put(item.getFullType(), item);
                        }
                    }

                    for (int int1 = 0; int1 < BodyPartType.ToIndex(BodyPartType.MAX); int1++) {
                        BodyPart bodyPart = bodyDamage.getBodyPart(BodyPartType.FromIndex(int1));
                        BodyPartLast bodyPartLast = bodyDamage.getBodyPartsLastState(BodyPartType.FromIndex(int1));
                        String string0 = bodyPart.getType().getBandageModel();
                        if (!StringUtils.isNullOrWhitespace(string0)) {
                            String string1 = this.getBloodBandageType(string0);
                            if (bodyPart.bandaged() != bodyPartLast.bandaged()) {
                                if (bodyPart.bandaged()) {
                                    if (bodyPart.isBandageDirty()) {
                                        this.removeBandageModel(character, string0);
                                        this.addBandageModel(character, string1);
                                    } else {
                                        this.removeBandageModel(character, string1);
                                        this.addBandageModel(character, string0);
                                    }
                                } else {
                                    this.removeBandageModel(character, string0);
                                    this.removeBandageModel(character, string1);
                                }
                            }

                            if (bodyPart.bitten() != bodyPartLast.bitten()) {
                                if (bodyPart.bitten()) {
                                    String string2 = bodyPart.getType().getBiteWoundModel(character.isFemale());
                                    if (StringUtils.isNullOrWhitespace(string2)) {
                                        continue;
                                    }

                                    this.addBandageModel(character, string2);
                                } else {
                                    this.removeBandageModel(character, bodyPart.getType().getBiteWoundModel(character.isFemale()));
                                }
                            }

                            if (bodyPart.scratched() != bodyPartLast.scratched()) {
                                if (bodyPart.scratched()) {
                                    String string3 = bodyPart.getType().getScratchWoundModel(character.isFemale());
                                    if (StringUtils.isNullOrWhitespace(string3)) {
                                        continue;
                                    }

                                    this.addBandageModel(character, string3);
                                } else {
                                    this.removeBandageModel(character, bodyPart.getType().getScratchWoundModel(character.isFemale()));
                                }
                            }

                            if (bodyPart.isCut() != bodyPartLast.isCut()) {
                                if (bodyPart.isCut()) {
                                    String string4 = bodyPart.getType().getCutWoundModel(character.isFemale());
                                    if (!StringUtils.isNullOrWhitespace(string4)) {
                                        this.addBandageModel(character, string4);
                                    }
                                } else {
                                    this.removeBandageModel(character, bodyPart.getType().getCutWoundModel(character.isFemale()));
                                }
                            }
                        }
                    }
                }
            }
        }

        protected void addBandageModel(IsoGameCharacter character, String string) {
            if (!this.itemMap.containsKey(string)) {
                if (InventoryItemFactory.CreateItem(string) instanceof Clothing clothing) {
                    character.getInventory().addItem(clothing);
                    character.setWornItem(clothing.getBodyLocation(), clothing);
                    character.resetModelNextFrame();
                }
            }
        }

        protected void removeBandageModel(IsoGameCharacter character, String string) {
            InventoryItem item = this.itemMap.get(string);
            if (item != null) {
                character.getWornItems().remove(item);
                character.getInventory().Remove(item);
                character.resetModelNextFrame();
                character.onWornItemsChanged();
                if (GameClient.bClient && character instanceof IsoPlayer && ((IsoPlayer)character).isLocalPlayer()) {
                    GameClient.instance.sendClothing((IsoPlayer)character, item.getBodyLocation(), item);
                }
            }
        }
    }

    public static enum BodyLocation {
        Head,
        Leg,
        Arm,
        Chest,
        Stomach,
        Foot,
        Hand;
    }

    public class CharacterTraits extends TraitCollection {
        public final TraitCollection.TraitSlot Obese = this.getTraitSlot("Obese");
        public final TraitCollection.TraitSlot Athletic = this.getTraitSlot("Athletic");
        public final TraitCollection.TraitSlot Overweight = this.getTraitSlot("Overweight");
        public final TraitCollection.TraitSlot Unfit = this.getTraitSlot("Unfit");
        public final TraitCollection.TraitSlot Emaciated = this.getTraitSlot("Emaciated");
        public final TraitCollection.TraitSlot Graceful = this.getTraitSlot("Graceful");
        public final TraitCollection.TraitSlot Clumsy = this.getTraitSlot("Clumsy");
        public final TraitCollection.TraitSlot Strong = this.getTraitSlot("Strong");
        public final TraitCollection.TraitSlot Weak = this.getTraitSlot("Weak");
        public final TraitCollection.TraitSlot VeryUnderweight = this.getTraitSlot("Very Underweight");
        public final TraitCollection.TraitSlot Underweight = this.getTraitSlot("Underweight");
        public final TraitCollection.TraitSlot FastHealer = this.getTraitSlot("FastHealer");
        public final TraitCollection.TraitSlot SlowHealer = this.getTraitSlot("SlowHealer");
        public final TraitCollection.TraitSlot ShortSighted = this.getTraitSlot("ShortSighted");
        public final TraitCollection.TraitSlot EagleEyed = this.getTraitSlot("EagleEyed");
        public final TraitCollection.TraitSlot Agoraphobic = this.getTraitSlot("Agoraphobic");
        public final TraitCollection.TraitSlot Claustophobic = this.getTraitSlot("Claustophobic");
        public final TraitCollection.TraitSlot AdrenalineJunkie = this.getTraitSlot("AdrenalineJunkie");
        public final TraitCollection.TraitSlot OutOfShape = this.getTraitSlot("Out of Shape");
        public final TraitCollection.TraitSlot HighThirst = this.getTraitSlot("HighThirst");
        public final TraitCollection.TraitSlot LowThirst = this.getTraitSlot("LowThirst");
        public final TraitCollection.TraitSlot HeartyAppitite = this.getTraitSlot("HeartyAppitite");
        public final TraitCollection.TraitSlot LightEater = this.getTraitSlot("LightEater");
        public final TraitCollection.TraitSlot Cowardly = this.getTraitSlot("Cowardly");
        public final TraitCollection.TraitSlot Brave = this.getTraitSlot("Brave");
        public final TraitCollection.TraitSlot Brooding = this.getTraitSlot("Brooding");
        public final TraitCollection.TraitSlot Insomniac = this.getTraitSlot("Insomniac");
        public final TraitCollection.TraitSlot NeedsLessSleep = this.getTraitSlot("NeedsLessSleep");
        public final TraitCollection.TraitSlot NeedsMoreSleep = this.getTraitSlot("NeedsMoreSleep");
        public final TraitCollection.TraitSlot Asthmatic = this.getTraitSlot("Asthmatic");
        public final TraitCollection.TraitSlot PlaysFootball = this.getTraitSlot("PlaysFootball");
        public final TraitCollection.TraitSlot Jogger = this.getTraitSlot("Jogger");
        public final TraitCollection.TraitSlot NightVision = this.getTraitSlot("NightVision");
        public final TraitCollection.TraitSlot FastLearner = this.getTraitSlot("FastLearner");
        public final TraitCollection.TraitSlot SlowLearner = this.getTraitSlot("SlowLearner");
        public final TraitCollection.TraitSlot Pacifist = this.getTraitSlot("Pacifist");
        public final TraitCollection.TraitSlot Feeble = this.getTraitSlot("Feeble");
        public final TraitCollection.TraitSlot Stout = this.getTraitSlot("Stout");
        public final TraitCollection.TraitSlot ShortTemper = this.getTraitSlot("ShortTemper");
        public final TraitCollection.TraitSlot Patient = this.getTraitSlot("Patient");
        public final TraitCollection.TraitSlot Injured = this.getTraitSlot("Injured");
        public final TraitCollection.TraitSlot Inconspicuous = this.getTraitSlot("Inconspicuous");
        public final TraitCollection.TraitSlot Conspicuous = this.getTraitSlot("Conspicuous");
        public final TraitCollection.TraitSlot Desensitized = this.getTraitSlot("Desensitized");
        public final TraitCollection.TraitSlot NightOwl = this.getTraitSlot("NightOwl");
        public final TraitCollection.TraitSlot Hemophobic = this.getTraitSlot("Hemophobic");
        public final TraitCollection.TraitSlot Burglar = this.getTraitSlot("Burglar");
        public final TraitCollection.TraitSlot KeenHearing = this.getTraitSlot("KeenHearing");
        public final TraitCollection.TraitSlot Deaf = this.getTraitSlot("Deaf");
        public final TraitCollection.TraitSlot HardOfHearing = this.getTraitSlot("HardOfHearing");
        public final TraitCollection.TraitSlot ThinSkinned = this.getTraitSlot("ThinSkinned");
        public final TraitCollection.TraitSlot ThickSkinned = this.getTraitSlot("ThickSkinned");
        public final TraitCollection.TraitSlot Marksman = this.getTraitSlot("Marksman");
        public final TraitCollection.TraitSlot Outdoorsman = this.getTraitSlot("Outdoorsman");
        public final TraitCollection.TraitSlot Lucky = this.getTraitSlot("Lucky");
        public final TraitCollection.TraitSlot Unlucky = this.getTraitSlot("Unlucky");
        public final TraitCollection.TraitSlot Nutritionist = this.getTraitSlot("Nutritionist");
        public final TraitCollection.TraitSlot Nutritionist2 = this.getTraitSlot("Nutritionist2");
        public final TraitCollection.TraitSlot Organized = this.getTraitSlot("Organized");
        public final TraitCollection.TraitSlot Disorganized = this.getTraitSlot("Disorganized");
        public final TraitCollection.TraitSlot Axeman = this.getTraitSlot("Axeman");
        public final TraitCollection.TraitSlot IronGut = this.getTraitSlot("IronGut");
        public final TraitCollection.TraitSlot WeakStomach = this.getTraitSlot("WeakStomach");
        public final TraitCollection.TraitSlot HeavyDrinker = this.getTraitSlot("HeavyDrinker");
        public final TraitCollection.TraitSlot LightDrinker = this.getTraitSlot("LightDrinker");
        public final TraitCollection.TraitSlot Resilient = this.getTraitSlot("Resilient");
        public final TraitCollection.TraitSlot ProneToIllness = this.getTraitSlot("ProneToIllness");
        public final TraitCollection.TraitSlot SpeedDemon = this.getTraitSlot("SpeedDemon");
        public final TraitCollection.TraitSlot SundayDriver = this.getTraitSlot("SundayDriver");
        public final TraitCollection.TraitSlot Smoker = this.getTraitSlot("Smoker");
        public final TraitCollection.TraitSlot Hypercondriac = this.getTraitSlot("Hypercondriac");
        public final TraitCollection.TraitSlot Illiterate = this.getTraitSlot("Illiterate");

        public boolean isIlliterate() {
            return this.Illiterate.isSet();
        }
    }

    private static final class L_actionStateChanged {
        static final ArrayList<String> stateNames = new ArrayList<>();
        static final ArrayList<State> states = new ArrayList<>();
    }

    private static final class L_getDotWithForwardDirection {
        static final Vector2 v1 = new Vector2();
        static final Vector2 v2 = new Vector2();
    }

    private static class L_postUpdate {
        static final MoveDeltaModifiers moveDeltas = new MoveDeltaModifiers();
    }

    private static final class L_renderLast {
        static final Color color = new Color();
    }

    private static final class L_renderShadow {
        static final Vector3f forward = new Vector3f();
        static final Vector3 v1 = new Vector3();
        static final Vector3f v3 = new Vector3f();
    }

    public static class LightInfo {
        public IsoGridSquare square;
        public float x;
        public float y;
        public float z;
        public float angleX;
        public float angleY;
        public ArrayList<IsoGameCharacter.TorchInfo> torches = new ArrayList<>();
        public long time;
        public float night;
        public float rmod;
        public float gmod;
        public float bmod;

        public void initFrom(IsoGameCharacter.LightInfo other) {
            this.square = other.square;
            this.x = other.x;
            this.y = other.y;
            this.z = other.z;
            this.angleX = other.angleX;
            this.angleY = other.angleY;
            this.torches.clear();
            this.torches.addAll(other.torches);
            this.time = (long)(System.nanoTime() / 1000000.0);
            this.night = other.night;
            this.rmod = other.rmod;
            this.gmod = other.gmod;
            this.bmod = other.bmod;
        }
    }

    public static class Location {
        public int x;
        public int y;
        public int z;

        public Location() {
        }

        public Location(int _x, int _y, int _z) {
            this.x = _x;
            this.y = _y;
            this.z = _z;
        }

        public IsoGameCharacter.Location set(int _x, int _y, int _z) {
            this.x = _x;
            this.y = _y;
            this.z = _z;
            return this;
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

        @Override
        public boolean equals(Object other) {
            return !(other instanceof IsoGameCharacter.Location location) ? false : this.x == location.x && this.y == location.y && this.z == location.z;
        }
    }

    public class PerkInfo {
        public int level = 0;
        public PerkFactory.Perk perk;

        public int getLevel() {
            return this.level;
        }
    }

    private static class ReadBook {
        String fullType;
        int alreadyReadPages;
    }

    public static class TorchInfo {
        private static final ObjectPool<IsoGameCharacter.TorchInfo> TorchInfoPool = new ObjectPool<>(IsoGameCharacter.TorchInfo::new);
        private static final Vector3f tempVector3f = new Vector3f();
        public int id;
        public float x;
        public float y;
        public float z;
        public float angleX;
        public float angleY;
        public float dist;
        public float strength;
        public boolean bCone;
        public float dot;
        public int focusing;

        public static IsoGameCharacter.TorchInfo alloc() {
            return TorchInfoPool.alloc();
        }

        public static void release(IsoGameCharacter.TorchInfo torchInfo) {
            TorchInfoPool.release(torchInfo);
        }

        public IsoGameCharacter.TorchInfo set(IsoPlayer player, InventoryItem item) {
            this.x = player.getX();
            this.y = player.getY();
            this.z = player.getZ();
            Vector2 vector = player.getLookVector(IsoGameCharacter.tempVector2);
            this.angleX = vector.x;
            this.angleY = vector.y;
            this.dist = item.getLightDistance();
            this.strength = item.getLightStrength();
            this.bCone = item.isTorchCone();
            this.dot = item.getTorchDot();
            this.focusing = 0;
            return this;
        }

        public IsoGameCharacter.TorchInfo set(VehiclePart part) {
            BaseVehicle vehicle = part.getVehicle();
            VehicleLight vehicleLight = part.getLight();
            VehicleScript vehicleScript = vehicle.getScript();
            Vector3f vector3f = tempVector3f;
            vector3f.set(vehicleLight.offset.x * vehicleScript.getExtents().x / 2.0F, 0.0F, vehicleLight.offset.y * vehicleScript.getExtents().z / 2.0F);
            vehicle.getWorldPos(vector3f, vector3f);
            this.x = vector3f.x;
            this.y = vector3f.y;
            this.z = vector3f.z;
            vector3f = vehicle.getForwardVector(vector3f);
            this.angleX = vector3f.x;
            this.angleY = vector3f.z;
            this.dist = part.getLightDistance();
            this.strength = part.getLightIntensity();
            this.bCone = true;
            this.dot = vehicleLight.dot;
            this.focusing = (int)part.getLightFocusing();
            return this;
        }
    }

    public class XP {
        public int level = 0;
        public int lastlevel = 0;
        public float TotalXP = 0.0F;
        public HashMap<PerkFactory.Perk, Float> XPMap = new HashMap<>();
        private float lastXPSumm = 0.0F;
        private long lastXPTime = System.currentTimeMillis();
        private float lastXPGrowthRate = 0.0F;
        public static final float MaxXPGrowthRate = 1000.0F;
        public HashMap<PerkFactory.Perk, IsoGameCharacter.XPMultiplier> XPMapMultiplier = new HashMap<>();
        IsoGameCharacter chr = null;

        public XP(IsoGameCharacter character1) {
            this.chr = character1;
        }

        public void update() {
            if (GameServer.bServer && this.chr instanceof IsoPlayer) {
                if (System.currentTimeMillis() - this.lastXPTime > 60000L) {
                    this.lastXPTime = System.currentTimeMillis();
                    float float0 = 0.0F;

                    for (Float float1 : this.XPMap.values()) {
                        float0 += float1;
                    }

                    this.lastXPGrowthRate = float0 - this.lastXPSumm;
                    this.lastXPSumm = float0;
                    if (this.lastXPGrowthRate
                        > 1000.0
                            * SandboxOptions.instance.XpMultiplier.getValue()
                            * ServerOptions.instance.AntiCheatProtectionType9ThresholdMultiplier.getValue()) {
                        UdpConnection udpConnection = GameServer.getConnectionFromPlayer((IsoPlayer)this.chr);
                        if (ServerOptions.instance.AntiCheatProtectionType9.getValue() && PacketValidator.checkUser(udpConnection)) {
                            PacketValidator.doKickUser(udpConnection, this.getClass().getSimpleName(), "Type9", null);
                        } else if (this.lastXPGrowthRate
                            > 1000.0
                                * SandboxOptions.instance.XpMultiplier.getValue()
                                * ServerOptions.instance.AntiCheatProtectionType9ThresholdMultiplier.getValue()
                                / 2.0) {
                            PacketValidator.doLogUser(udpConnection, Userlog.UserlogType.SuspiciousActivity, this.getClass().getSimpleName(), "Type9");
                        }
                    }
                }
            }
        }

        public void addXpMultiplier(PerkFactory.Perk perks, float multiplier, int minLevel, int maxLevel) {
            IsoGameCharacter.XPMultiplier xPMultiplier = this.XPMapMultiplier.get(perks);
            if (xPMultiplier == null) {
                xPMultiplier = new IsoGameCharacter.XPMultiplier();
            }

            xPMultiplier.multiplier = multiplier;
            xPMultiplier.minLevel = minLevel;
            xPMultiplier.maxLevel = maxLevel;
            this.XPMapMultiplier.put(perks, xPMultiplier);
        }

        public HashMap<PerkFactory.Perk, IsoGameCharacter.XPMultiplier> getMultiplierMap() {
            return this.XPMapMultiplier;
        }

        public float getMultiplier(PerkFactory.Perk perk) {
            IsoGameCharacter.XPMultiplier xPMultiplier = this.XPMapMultiplier.get(perk);
            return xPMultiplier == null ? 0.0F : xPMultiplier.multiplier;
        }

        public int getPerkBoost(PerkFactory.Perk type) {
            return IsoGameCharacter.this.getDescriptor().getXPBoostMap().get(type) != null
                ? IsoGameCharacter.this.getDescriptor().getXPBoostMap().get(type)
                : 0;
        }

        public void setPerkBoost(PerkFactory.Perk perk, int _level) {
            if (perk != null && perk != PerkFactory.Perks.None && perk != PerkFactory.Perks.MAX) {
                _level = PZMath.clamp(_level, 0, 10);
                if (_level == 0) {
                    IsoGameCharacter.this.getDescriptor().getXPBoostMap().remove(perk);
                } else {
                    IsoGameCharacter.this.getDescriptor().getXPBoostMap().put(perk, _level);
                }
            }
        }

        public int getLevel() {
            return this.level;
        }

        public void setLevel(int newlevel) {
            this.level = newlevel;
        }

        public float getTotalXp() {
            return this.TotalXP;
        }

        public void AddXP(PerkFactory.Perk type, float amount) {
            if (this.chr instanceof IsoPlayer && ((IsoPlayer)this.chr).isLocalPlayer()) {
                this.AddXP(type, amount, true, true, false);
            }
        }

        public void AddXPNoMultiplier(PerkFactory.Perk type, float amount) {
            IsoGameCharacter.XPMultiplier xPMultiplier = this.getMultiplierMap().remove(type);

            try {
                this.AddXP(type, amount);
            } finally {
                if (xPMultiplier != null) {
                    this.getMultiplierMap().put(type, xPMultiplier);
                }
            }
        }

        public void AddXP(PerkFactory.Perk type, float amount, boolean callLua, boolean doXPBoost, boolean remote) {
            if (!remote && GameClient.bClient && this.chr instanceof IsoPlayer) {
                GameClient.instance.sendAddXp((IsoPlayer)this.chr, type, (int)amount);
            }

            PerkFactory.Perk perk0 = null;

            for (int int0 = 0; int0 < PerkFactory.PerkList.size(); int0++) {
                PerkFactory.Perk perk1 = PerkFactory.PerkList.get(int0);
                if (perk1.getType() == type) {
                    perk0 = perk1;
                    break;
                }
            }

            if (perk0.getType() != PerkFactory.Perks.Fitness || !(this.chr instanceof IsoPlayer) || ((IsoPlayer)this.chr).getNutrition().canAddFitnessXp()) {
                if (perk0.getType() == PerkFactory.Perks.Strength && this.chr instanceof IsoPlayer) {
                    if (((IsoPlayer)this.chr).getNutrition().getProteins() > 50.0F && ((IsoPlayer)this.chr).getNutrition().getProteins() < 300.0F) {
                        amount = (float)(amount * 1.5);
                    }

                    if (((IsoPlayer)this.chr).getNutrition().getProteins() < -300.0F) {
                        amount = (float)(amount * 0.7);
                    }
                }

                float float0 = this.getXP(type);
                float float1 = perk0.getTotalXpForLevel(10);
                if (!(amount >= 0.0F) || !(float0 >= float1)) {
                    float float2 = 1.0F;
                    if (doXPBoost) {
                        boolean boolean0 = false;

                        for (Entry entry : IsoGameCharacter.this.getDescriptor().getXPBoostMap().entrySet()) {
                            if (entry.getKey() == perk0.getType()) {
                                boolean0 = true;
                                if ((Integer)entry.getValue() == 0 && !this.isSkillExcludedFromSpeedReduction((PerkFactory.Perk)entry.getKey())) {
                                    float2 *= 0.25F;
                                } else if ((Integer)entry.getValue() == 1 && entry.getKey() == PerkFactory.Perks.Sprinting) {
                                    float2 = (float)(float2 * 1.25);
                                } else if ((Integer)entry.getValue() == 1) {
                                    float2 = (float)(float2 * 1.0);
                                } else if ((Integer)entry.getValue() == 2 && !this.isSkillExcludedFromSpeedIncrease((PerkFactory.Perk)entry.getKey())) {
                                    float2 = (float)(float2 * 1.33);
                                } else if ((Integer)entry.getValue() >= 3 && !this.isSkillExcludedFromSpeedIncrease((PerkFactory.Perk)entry.getKey())) {
                                    float2 = (float)(float2 * 1.66);
                                }
                            }
                        }

                        if (!boolean0 && !this.isSkillExcludedFromSpeedReduction(perk0.getType())) {
                            float2 = 0.25F;
                        }

                        if (IsoGameCharacter.this.Traits.FastLearner.isSet() && !this.isSkillExcludedFromSpeedIncrease(perk0.getType())) {
                            float2 *= 1.3F;
                        }

                        if (IsoGameCharacter.this.Traits.SlowLearner.isSet() && !this.isSkillExcludedFromSpeedReduction(perk0.getType())) {
                            float2 *= 0.7F;
                        }

                        if (IsoGameCharacter.this.Traits.Pacifist.isSet()) {
                            if (perk0.getType() == PerkFactory.Perks.SmallBlade
                                || perk0.getType() == PerkFactory.Perks.LongBlade
                                || perk0.getType() == PerkFactory.Perks.SmallBlunt
                                || perk0.getType() == PerkFactory.Perks.Spear
                                || perk0.getType() == PerkFactory.Perks.Maintenance
                                || perk0.getType() == PerkFactory.Perks.Blunt
                                || perk0.getType() == PerkFactory.Perks.Axe) {
                                float2 *= 0.75F;
                            } else if (perk0.getType() == PerkFactory.Perks.Aiming) {
                                float2 *= 0.75F;
                            }
                        }

                        amount *= float2;
                        float float3 = this.getMultiplier(type);
                        if (float3 > 1.0F) {
                            amount *= float3;
                        }

                        if (!perk0.isPassiv()) {
                            amount = (float)(amount * SandboxOptions.instance.XpMultiplier.getValue());
                        } else if (perk0.isPassiv() && SandboxOptions.instance.XpMultiplierAffectsPassive.getValue()) {
                            amount = (float)(amount * SandboxOptions.instance.XpMultiplier.getValue());
                        }
                    }

                    float float4 = float0 + amount;
                    if (float4 < 0.0F) {
                        float4 = 0.0F;
                        amount = -float0;
                    }

                    if (float4 > float1) {
                        float4 = float1;
                        amount = float1 - float0;
                    }

                    this.XPMap.put(type, float4);

                    for (float float5 = perk0.getTotalXpForLevel(this.chr.getPerkLevel(perk0) + 1);
                        float0 < float5 && float4 >= float5;
                        float5 = perk0.getTotalXpForLevel(this.chr.getPerkLevel(perk0) + 1)
                    ) {
                        IsoGameCharacter.this.LevelPerk(type);
                        if (this.chr instanceof IsoPlayer && ((IsoPlayer)this.chr).isLocalPlayer() && !this.chr.getEmitter().isPlaying("GainExperienceLevel")) {
                            this.chr.getEmitter().playSoundImpl("GainExperienceLevel", (IsoObject)null);
                        }

                        if (this.chr.getPerkLevel(perk0) >= 10) {
                            break;
                        }
                    }

                    IsoGameCharacter.XPMultiplier xPMultiplier = this.getMultiplierMap().get(perk0);
                    if (xPMultiplier != null) {
                        float float6 = perk0.getTotalXpForLevel(xPMultiplier.minLevel - 1);
                        float float7 = perk0.getTotalXpForLevel(xPMultiplier.maxLevel);
                        if (float0 >= float6 && float4 < float6 || float0 < float7 && float4 >= float7) {
                            this.getMultiplierMap().remove(perk0);
                        }
                    }

                    if (callLua) {
                        LuaEventManager.triggerEventGarbage("AddXP", this.chr, type, amount);
                    }
                }
            }
        }

        private boolean isSkillExcludedFromSpeedReduction(PerkFactory.Perk perk) {
            if (perk == PerkFactory.Perks.Sprinting) {
                return true;
            } else {
                return perk == PerkFactory.Perks.Fitness ? true : perk == PerkFactory.Perks.Strength;
            }
        }

        private boolean isSkillExcludedFromSpeedIncrease(PerkFactory.Perk perk) {
            return perk == PerkFactory.Perks.Fitness ? true : perk == PerkFactory.Perks.Strength;
        }

        public float getXP(PerkFactory.Perk type) {
            return this.XPMap.containsKey(type) ? this.XPMap.get(type) : 0.0F;
        }

        @Deprecated
        public void AddXP(HandWeapon weapon, int amount) {
        }

        public void setTotalXP(float xp) {
            this.TotalXP = xp;
        }

        private void savePerk(ByteBuffer byteBuffer, PerkFactory.Perk perk) throws IOException {
            GameWindow.WriteStringUTF(byteBuffer, perk == null ? "" : perk.getId());
        }

        private PerkFactory.Perk loadPerk(ByteBuffer byteBuffer, int int0) throws IOException {
            if (int0 >= 152) {
                String string = GameWindow.ReadStringUTF(byteBuffer);
                PerkFactory.Perk perk0 = PerkFactory.Perks.FromString(string);
                return perk0 == PerkFactory.Perks.MAX ? null : perk0;
            } else {
                int int1 = byteBuffer.getInt();
                if (int1 >= 0 && int1 < PerkFactory.Perks.MAX.index()) {
                    PerkFactory.Perk perk1 = PerkFactory.Perks.fromIndex(int1);
                    return perk1 == PerkFactory.Perks.MAX ? null : perk1;
                } else {
                    return null;
                }
            }
        }

        public void recalcSumm() {
            float float0 = 0.0F;

            for (Float float1 : this.XPMap.values()) {
                float0 += float1;
            }

            this.lastXPSumm = float0;
            this.lastXPTime = System.currentTimeMillis();
            this.lastXPGrowthRate = 0.0F;
        }

        public void load(ByteBuffer input, int WorldVersion) throws IOException {
            int int0 = input.getInt();
            this.chr.Traits.clear();

            for (int int1 = 0; int1 < int0; int1++) {
                String string = GameWindow.ReadString(input);
                if (TraitFactory.getTrait(string) != null) {
                    if (!this.chr.Traits.contains(string)) {
                        this.chr.Traits.add(string);
                    }
                } else {
                    DebugLog.General.error("unknown trait \"" + string + "\"");
                }
            }

            this.TotalXP = input.getFloat();
            this.level = input.getInt();
            this.lastlevel = input.getInt();
            this.XPMap.clear();
            int int2 = input.getInt();

            for (int int3 = 0; int3 < int2; int3++) {
                PerkFactory.Perk perk0 = this.loadPerk(input, WorldVersion);
                float float0 = input.getFloat();
                if (perk0 != null) {
                    this.XPMap.put(perk0, float0);
                }
            }

            if (WorldVersion < 162) {
                int int4 = input.getInt();

                for (int int5 = 0; int5 < int4; int5++) {
                    PerkFactory.Perk perk1 = this.loadPerk(input, WorldVersion);
                }
            }

            IsoGameCharacter.this.PerkList.clear();
            int int6 = input.getInt();

            for (int int7 = 0; int7 < int6; int7++) {
                PerkFactory.Perk perk2 = this.loadPerk(input, WorldVersion);
                int int8 = input.getInt();
                if (perk2 != null) {
                    IsoGameCharacter.PerkInfo perkInfo = IsoGameCharacter.this.new PerkInfo();
                    perkInfo.perk = perk2;
                    perkInfo.level = int8;
                    IsoGameCharacter.this.PerkList.add(perkInfo);
                }
            }

            int int9 = input.getInt();

            for (int int10 = 0; int10 < int9; int10++) {
                PerkFactory.Perk perk3 = this.loadPerk(input, WorldVersion);
                float float1 = input.getFloat();
                byte byte0 = input.get();
                byte byte1 = input.get();
                if (perk3 != null) {
                    this.addXpMultiplier(perk3, float1, byte0, byte1);
                }
            }

            if (this.TotalXP > IsoGameCharacter.this.getXpForLevel(this.getLevel() + 1)) {
                this.setTotalXP(this.chr.getXpForLevel(this.getLevel()));
            }

            this.recalcSumm();
        }

        public void save(ByteBuffer output) throws IOException {
            output.putInt(this.chr.Traits.size());

            for (int int0 = 0; int0 < this.chr.Traits.size(); int0++) {
                GameWindow.WriteString(output, this.chr.Traits.get(int0));
            }

            output.putFloat(this.TotalXP);
            output.putInt(this.level);
            output.putInt(this.lastlevel);
            output.putInt(this.XPMap.size());
            Iterator iterator0 = this.XPMap.entrySet().iterator();

            while (iterator0 != null && iterator0.hasNext()) {
                Entry entry0 = (Entry)iterator0.next();
                this.savePerk(output, (PerkFactory.Perk)entry0.getKey());
                output.putFloat((Float)entry0.getValue());
            }

            output.putInt(IsoGameCharacter.this.PerkList.size());

            for (int int1 = 0; int1 < IsoGameCharacter.this.PerkList.size(); int1++) {
                IsoGameCharacter.PerkInfo perkInfo = IsoGameCharacter.this.PerkList.get(int1);
                this.savePerk(output, perkInfo.perk);
                output.putInt(perkInfo.level);
            }

            output.putInt(this.XPMapMultiplier.size());
            Iterator iterator1 = this.XPMapMultiplier.entrySet().iterator();

            while (iterator1 != null && iterator1.hasNext()) {
                Entry entry1 = (Entry)iterator1.next();
                this.savePerk(output, (PerkFactory.Perk)entry1.getKey());
                output.putFloat(((IsoGameCharacter.XPMultiplier)entry1.getValue()).multiplier);
                output.put((byte)((IsoGameCharacter.XPMultiplier)entry1.getValue()).minLevel);
                output.put((byte)((IsoGameCharacter.XPMultiplier)entry1.getValue()).maxLevel);
            }
        }

        public void setXPToLevel(PerkFactory.Perk key, int perkLevel) {
            PerkFactory.Perk perk0 = null;

            for (int int0 = 0; int0 < PerkFactory.PerkList.size(); int0++) {
                PerkFactory.Perk perk1 = PerkFactory.PerkList.get(int0);
                if (perk1.getType() == key) {
                    perk0 = perk1;
                    break;
                }
            }

            if (perk0 != null) {
                this.XPMap.put(key, perk0.getTotalXpForLevel(perkLevel));
            }
        }
    }

    public static class XPMultiplier {
        public float multiplier;
        public int minLevel;
        public int maxLevel;
    }

    protected static final class l_testDotSide {
        static final Vector2 v1 = new Vector2();
        static final Vector2 v2 = new Vector2();
        static final Vector2 v3 = new Vector2();
    }

    private static class s_performance {
        static final PerformanceProfileProbe postUpdate = new PerformanceProfileProbe("IsoGameCharacter.postUpdate");
        public static PerformanceProfileProbe update = new PerformanceProfileProbe("IsoGameCharacter.update");
    }
}
