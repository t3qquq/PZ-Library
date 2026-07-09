// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.debug;

import java.io.File;
import java.util.ArrayList;
import zombie.DebugFileWatcher;
import zombie.GameWindow;
import zombie.PredicatedFileWatcher;
import zombie.ZomboidFileSystem;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.opengl.RenderThread;
import zombie.debug.options.Animation;
import zombie.debug.options.Character;
import zombie.debug.options.IDebugOption;
import zombie.debug.options.IDebugOptionGroup;
import zombie.debug.options.IsoSprite;
import zombie.debug.options.Network;
import zombie.debug.options.OffscreenBuffer;
import zombie.debug.options.OptionGroup;
import zombie.debug.options.Terrain;
import zombie.debug.options.Weather;
import zombie.gameStates.GameLoadingState;
import zombie.util.PZXmlParserException;
import zombie.util.PZXmlUtil;
import zombie.util.Type;
import zombie.util.list.PZArrayUtil;

public final class DebugOptions implements IDebugOptionGroup {
    public static final int VERSION = 1;
    public static final DebugOptions instance = new DebugOptions();
    private final ArrayList<BooleanDebugOption> options = new ArrayList<>();
    private final ArrayList<IDebugOption> m_options = new ArrayList<>();
    public final BooleanDebugOption AssetSlowLoad = this.newOption("Asset.SlowLoad", false);
    public final BooleanDebugOption MultiplayerShowZombieMultiplier = this.newDebugOnlyOption("Multiplayer.Debug.ZombieMultiplier", false);
    public final BooleanDebugOption MultiplayerShowZombieOwner = this.newDebugOnlyOption("Multiplayer.Debug.ZombieOwner", false);
    public final BooleanDebugOption MultiplayerShowPosition = this.newDebugOnlyOption("Multiplayer.Debug.Position", false);
    public final BooleanDebugOption MultiplayerShowTeleport = this.newDebugOnlyOption("Multiplayer.Debug.Teleport", false);
    public final BooleanDebugOption MultiplayerShowHit = this.newDebugOnlyOption("Multiplayer.Debug.Hit", false);
    public final BooleanDebugOption MultiplayerLogPrediction = this.newDebugOnlyOption("Multiplayer.Debug.LogPrediction", false);
    public final BooleanDebugOption MultiplayerShowPlayerPrediction = this.newDebugOnlyOption("Multiplayer.Debug.PlayerPrediction", false);
    public final BooleanDebugOption MultiplayerShowPlayerStatus = this.newDebugOnlyOption("Multiplayer.Debug.PlayerStatus", false);
    public final BooleanDebugOption MultiplayerShowZombiePrediction = this.newDebugOnlyOption("Multiplayer.Debug.ZombiePrediction", false);
    public final BooleanDebugOption MultiplayerShowZombieDesync = this.newDebugOnlyOption("Multiplayer.Debug.ZombieDesync", false);
    public final BooleanDebugOption MultiplayerShowZombieStatus = this.newDebugOnlyOption("Multiplayer.Debug.ZombieStatus", false);
    public final BooleanDebugOption MultiplayerCriticalHit = this.newDebugOnlyOption("Multiplayer.Debug.CriticalHit", false);
    public final BooleanDebugOption MultiplayerTorsoHit = this.newDebugOnlyOption("Multiplayer.Debug.TorsoHit", false);
    public final BooleanDebugOption MultiplayerZombieCrawler = this.newDebugOnlyOption("Multiplayer.Debug.ZombieCrawler", false);
    public final BooleanDebugOption MultiplayerHotKey = this.newDebugOnlyOption("Multiplayer.Debug.HotKey", false);
    public final BooleanDebugOption MultiplayerPlayerZombie = this.newDebugOnlyOption("Multiplayer.Debug.PlayerZombie", false);
    public final BooleanDebugOption MultiplayerAttackPlayer = this.newDebugOnlyOption("Multiplayer.Debug.Attack.Player", false);
    public final BooleanDebugOption MultiplayerFollowPlayer = this.newDebugOnlyOption("Multiplayer.Debug.Follow.Player", false);
    public final BooleanDebugOption MultiplayerAutoEquip = this.newDebugOnlyOption("Multiplayer.Debug.AutoEquip", false);
    public final BooleanDebugOption MultiplayerSeeNonPvpZones = this.newDebugOnlyOption("Multiplayer.Debug.SeeNonPvpZones", false);
    public final BooleanDebugOption MultiplayerFailChecksum = this.newDebugOnlyOption("Multiplayer.Debug.FailChecksum", false);
    public final BooleanDebugOption CheatClockVisible = this.newDebugOnlyOption("Cheat.Clock.Visible", false);
    public final BooleanDebugOption CheatDoorUnlock = this.newDebugOnlyOption("Cheat.Door.Unlock", false);
    public final BooleanDebugOption CheatPlayerStartInvisible = this.newDebugOnlyOption("Cheat.Player.StartInvisible", false);
    public final BooleanDebugOption CheatPlayerInvisibleSprint = this.newDebugOnlyOption("Cheat.Player.InvisibleSprint", false);
    public final BooleanDebugOption CheatPlayerSeeEveryone = this.newDebugOnlyOption("Cheat.Player.SeeEveryone", false);
    public final BooleanDebugOption CheatUnlimitedAmmo = this.newDebugOnlyOption("Cheat.Player.UnlimitedAmmo", false);
    public final BooleanDebugOption CheatRecipeKnowAll = this.newDebugOnlyOption("Cheat.Recipe.KnowAll", false);
    public final BooleanDebugOption CheatTimedActionInstant = this.newDebugOnlyOption("Cheat.TimedAction.Instant", false);
    public final BooleanDebugOption CheatVehicleMechanicsAnywhere = this.newDebugOnlyOption("Cheat.Vehicle.MechanicsAnywhere", false);
    public final BooleanDebugOption CheatVehicleStartWithoutKey = this.newDebugOnlyOption("Cheat.Vehicle.StartWithoutKey", false);
    public final BooleanDebugOption CheatWindowUnlock = this.newDebugOnlyOption("Cheat.Window.Unlock", false);
    public final BooleanDebugOption CollideWithObstaclesRenderRadius = this.newOption("CollideWithObstacles.Render.Radius", false);
    public final BooleanDebugOption CollideWithObstaclesRenderObstacles = this.newOption("CollideWithObstacles.Render.Obstacles", false);
    public final BooleanDebugOption CollideWithObstaclesRenderNormals = this.newOption("CollideWithObstacles.Render.Normals", false);
    public final BooleanDebugOption DeadBodyAtlasRender = this.newOption("DeadBodyAtlas.Render", false);
    public final BooleanDebugOption WorldItemAtlasRender = this.newDebugOnlyOption("WorldItemAtlas.Render", false);
    public final BooleanDebugOption DebugScenarioForceLaunch = this.newOption("DebugScenario.ForceLaunch", false);
    public final BooleanDebugOption MechanicsRenderHitbox = this.newOption("Mechanics.Render.Hitbox", false);
    public final BooleanDebugOption JoypadRenderUI = this.newDebugOnlyOption("Joypad.Render.UI", false);
    public final BooleanDebugOption ModelRenderAttachments = this.newOption("Model.Render.Attachments", false);
    public final BooleanDebugOption ModelRenderAxis = this.newOption("Model.Render.Axis", false);
    public final BooleanDebugOption ModelRenderBones = this.newOption("Model.Render.Bones", false);
    public final BooleanDebugOption ModelRenderBounds = this.newOption("Model.Render.Bounds", false);
    public final BooleanDebugOption ModelRenderLights = this.newOption("Model.Render.Lights", false);
    public final BooleanDebugOption ModelRenderMuzzleflash = this.newOption("Model.Render.Muzzleflash", false);
    public final BooleanDebugOption ModelRenderSkipVehicles = this.newOption("Model.Render.SkipVehicles", false);
    public final BooleanDebugOption ModelRenderWeaponHitPoint = this.newOption("Model.Render.WeaponHitPoint", false);
    public final BooleanDebugOption ModelRenderWireframe = this.newOption("Model.Render.Wireframe", false);
    public final BooleanDebugOption ModelSkeleton = this.newOption("Model.Force.Skeleton", false);
    public final BooleanDebugOption ModRenderLoaded = this.newDebugOnlyOption("Mod.Render.Loaded", false);
    public final BooleanDebugOption PathfindPathToMouseAllowCrawl = this.newOption("Pathfind.PathToMouse.AllowCrawl", false);
    public final BooleanDebugOption PathfindPathToMouseAllowThump = this.newOption("Pathfind.PathToMouse.AllowThump", false);
    public final BooleanDebugOption PathfindPathToMouseEnable = this.newOption("Pathfind.PathToMouse.Enable", false);
    public final BooleanDebugOption PathfindPathToMouseIgnoreCrawlCost = this.newOption("Pathfind.PathToMouse.IgnoreCrawlCost", false);
    public final BooleanDebugOption PathfindRenderPath = this.newOption("Pathfind.Render.Path", false);
    public final BooleanDebugOption PathfindRenderWaiting = this.newOption("Pathfind.Render.Waiting", false);
    public final BooleanDebugOption PhysicsRender = this.newOption("Physics.Render", false);
    public final BooleanDebugOption PolymapRenderClusters = this.newOption("Pathfind.Render.Clusters", false);
    public final BooleanDebugOption PolymapRenderConnections = this.newOption("Pathfind.Render.Connections", false);
    public final BooleanDebugOption PolymapRenderCrawling = this.newOption("Pathfind.Render.Crawling", false);
    public final BooleanDebugOption PolymapRenderLineClearCollide = this.newOption("Pathfind.Render.LineClearCollide", false);
    public final BooleanDebugOption PolymapRenderNodes = this.newOption("Pathfind.Render.Nodes", false);
    public final BooleanDebugOption TooltipInfo = this.newOption("Tooltip.Info", false);
    public final BooleanDebugOption TooltipModName = this.newDebugOnlyOption("Tooltip.ModName", false);
    public final BooleanDebugOption TranslationPrefix = this.newOption("Translation.Prefix", false);
    public final BooleanDebugOption UIRenderOutline = this.newOption("UI.Render.Outline", false);
    public final BooleanDebugOption UIDebugConsoleStartVisible = this.newOption("UI.DebugConsole.StartVisible", true);
    public final BooleanDebugOption UIDebugConsoleDebugLog = this.newOption("UI.DebugConsole.DebugLog", true);
    public final BooleanDebugOption UIDebugConsoleEchoCommand = this.newOption("UI.DebugConsole.EchoCommand", true);
    public final BooleanDebugOption UIDisableWelcomeMessage = this.newOption("UI.DisableWelcomeMessage", false);
    public final BooleanDebugOption VehicleCycleColor = this.newDebugOnlyOption("Vehicle.CycleColor", false);
    public final BooleanDebugOption VehicleRenderBlood0 = this.newDebugOnlyOption("Vehicle.Render.Blood0", false);
    public final BooleanDebugOption VehicleRenderBlood50 = this.newDebugOnlyOption("Vehicle.Render.Blood50", false);
    public final BooleanDebugOption VehicleRenderBlood100 = this.newDebugOnlyOption("Vehicle.Render.Blood100", false);
    public final BooleanDebugOption VehicleRenderDamage0 = this.newDebugOnlyOption("Vehicle.Render.Damage0", false);
    public final BooleanDebugOption VehicleRenderDamage1 = this.newDebugOnlyOption("Vehicle.Render.Damage1", false);
    public final BooleanDebugOption VehicleRenderDamage2 = this.newDebugOnlyOption("Vehicle.Render.Damage2", false);
    public final BooleanDebugOption VehicleRenderRust0 = this.newDebugOnlyOption("Vehicle.Render.Rust0", false);
    public final BooleanDebugOption VehicleRenderRust50 = this.newDebugOnlyOption("Vehicle.Render.Rust50", false);
    public final BooleanDebugOption VehicleRenderRust100 = this.newDebugOnlyOption("Vehicle.Render.Rust100", false);
    public final BooleanDebugOption VehicleRenderOutline = this.newOption("Vehicle.Render.Outline", false);
    public final BooleanDebugOption VehicleRenderArea = this.newOption("Vehicle.Render.Area", false);
    public final BooleanDebugOption VehicleRenderAuthorizations = this.newOption("Vehicle.Render.Authorizations", false);
    public final BooleanDebugOption VehicleRenderInterpolateBuffer = this.newOption("Vehicle.Render.InterpolateBuffer", false);
    public final BooleanDebugOption VehicleRenderAttackPositions = this.newOption("Vehicle.Render.AttackPositions", false);
    public final BooleanDebugOption VehicleRenderExit = this.newOption("Vehicle.Render.Exit", false);
    public final BooleanDebugOption VehicleRenderIntersectedSquares = this.newOption("Vehicle.Render.IntersectedSquares", false);
    public final BooleanDebugOption VehicleRenderTrailerPositions = this.newDebugOnlyOption("Vehicle.Render.TrailerPositions", false);
    public final BooleanDebugOption VehicleSpawnEverywhere = this.newDebugOnlyOption("Vehicle.Spawn.Everywhere", false);
    public final BooleanDebugOption WorldSoundRender = this.newOption("Sound.WorldSound.Render", false);
    public final BooleanDebugOption ObjectAmbientEmitterRender = this.newDebugOnlyOption("Sound.ObjectAmbientEmitter.Render", false);
    public final BooleanDebugOption LightingRender = this.newOption("Lighting.Render", false);
    public final BooleanDebugOption SkyboxShow = this.newOption("Skybox.Show", false);
    public final BooleanDebugOption WorldStreamerSlowLoad = this.newOption("WorldStreamer.SlowLoad", false);
    public final BooleanDebugOption DebugDraw_SkipVBODraw = this.newOption("DebugDraw.SkipVBODraw", false);
    public final BooleanDebugOption DebugDraw_SkipDrawNonSkinnedModel = this.newOption("DebugDraw.SkipDrawNonSkinnedModel", false);
    public final BooleanDebugOption DebugDraw_SkipWorldShading = this.newOption("DebugDraw.SkipWorldShading", false);
    public final BooleanDebugOption GameProfilerEnabled = this.newOption("GameProfiler.Enabled", false);
    public final BooleanDebugOption GameTimeSpeedHalf = this.newOption("GameTime.Speed.Half", false);
    public final BooleanDebugOption GameTimeSpeedQuarter = this.newOption("GameTime.Speed.Quarter", false);
    public final BooleanDebugOption ThreadCrash_Enabled = this.newDebugOnlyOption("ThreadCrash.Enable", false);
    public final BooleanDebugOption[] ThreadCrash_GameThread = new BooleanDebugOption[]{
        this.newDebugOnlyOption("ThreadCrash.MainThread.0", false),
        this.newDebugOnlyOption("ThreadCrash.MainThread.1", false),
        this.newDebugOnlyOption("ThreadCrash.MainThread.2", false)
    };
    public final BooleanDebugOption[] ThreadCrash_GameLoadingThread = new BooleanDebugOption[]{
        this.newDebugOnlyOption("ThreadCrash.GameLoadingThread.0", false)
    };
    public final BooleanDebugOption[] ThreadCrash_RenderThread = new BooleanDebugOption[]{
        this.newDebugOnlyOption("ThreadCrash.RenderThread.0", false),
        this.newDebugOnlyOption("ThreadCrash.RenderThread.1", false),
        this.newDebugOnlyOption("ThreadCrash.RenderThread.2", false)
    };
    public final BooleanDebugOption WorldChunkMap5x5 = this.newDebugOnlyOption("World.ChunkMap.5x5", false);
    public final BooleanDebugOption ZombieRenderCanCrawlUnderVehicle = this.newDebugOnlyOption("Zombie.Render.CanCrawlUnderVehicle", false);
    public final BooleanDebugOption ZombieRenderFakeDead = this.newDebugOnlyOption("Zombie.Render.FakeDead", false);
    public final BooleanDebugOption ZombieRenderMemory = this.newDebugOnlyOption("Zombie.Render.Memory", false);
    public final BooleanDebugOption ZombieOutfitRandom = this.newDebugOnlyOption("Zombie.Outfit.Random", false);
    public final DebugOptions.Checks Checks = this.newOptionGroup(new DebugOptions.Checks());
    public final IsoSprite IsoSprite = this.newOptionGroup(new IsoSprite());
    public final Network Network = this.newOptionGroup(new Network());
    public final OffscreenBuffer OffscreenBuffer = this.newOptionGroup(new OffscreenBuffer());
    public final Terrain Terrain = this.newOptionGroup(new Terrain());
    public final Weather Weather = this.newOptionGroup(new Weather());
    public final Animation Animation = this.newOptionGroup(new Animation());
    public final Character Character = this.newOptionGroup(new Character());
    private static PredicatedFileWatcher m_triggerWatcher;

    public void init() {
        this.load();
        this.initMessaging();
    }

    private void initMessaging() {
        if (m_triggerWatcher == null) {
            m_triggerWatcher = new PredicatedFileWatcher(
                ZomboidFileSystem.instance.getMessagingDirSub("Trigger_SetDebugOptions.xml"), this::onTrigger_SetDebugOptions
            );
            DebugFileWatcher.instance.add(m_triggerWatcher);
        }

        DebugOptionsXml debugOptionsXml = new DebugOptionsXml();
        debugOptionsXml.setDebugMode = true;
        debugOptionsXml.debugMode = Core.bDebug;

        for (BooleanDebugOption booleanDebugOption : this.options) {
            debugOptionsXml.options.add(new DebugOptionsXml.OptionNode(booleanDebugOption.getName(), booleanDebugOption.getValue()));
        }

        String string = ZomboidFileSystem.instance.getMessagingDirSub("DebugOptions_list.xml");
        PZXmlUtil.tryWrite(debugOptionsXml, new File(string));
    }

    private void onTrigger_SetDebugOptions(String var1) {
        try {
            DebugOptionsXml debugOptionsXml = PZXmlUtil.parse(
                DebugOptionsXml.class, ZomboidFileSystem.instance.getMessagingDirSub("Trigger_SetDebugOptions.xml")
            );

            for (DebugOptionsXml.OptionNode optionNode : debugOptionsXml.options) {
                this.setBoolean(optionNode.name, optionNode.value);
            }

            if (debugOptionsXml.setDebugMode) {
                DebugLog.General.println("DebugMode: %s", debugOptionsXml.debugMode ? "ON" : "OFF");
                Core.bDebug = debugOptionsXml.debugMode;
            }
        } catch (PZXmlParserException pZXmlParserException) {
            ExceptionLogger.logException(pZXmlParserException, "Exception thrown parsing Trigger_SetDebugOptions.xml");
        }
    }

    @Override
    public Iterable<IDebugOption> getChildren() {
        return PZArrayUtil.listConvert(this.options, booleanDebugOption -> booleanDebugOption);
    }

    @Override
    public void addChild(IDebugOption newChild) {
        this.m_options.add(newChild);
        newChild.setParent(this);
        this.onChildAdded(newChild);
    }

    @Override
    public void onChildAdded(IDebugOption newOption) {
        this.onDescendantAdded(newOption);
    }

    @Override
    public void onDescendantAdded(IDebugOption newOption) {
        this.addOption(newOption);
    }

    private void addOption(IDebugOption iDebugOption) {
        BooleanDebugOption booleanDebugOption = Type.tryCastTo(iDebugOption, BooleanDebugOption.class);
        if (booleanDebugOption != null) {
            this.options.add(booleanDebugOption);
        }

        IDebugOptionGroup iDebugOptionGroup = Type.tryCastTo(iDebugOption, IDebugOptionGroup.class);
        if (iDebugOptionGroup != null) {
            this.addDescendantOptions(iDebugOptionGroup);
        }
    }

    private void addDescendantOptions(IDebugOptionGroup iDebugOptionGroup) {
        for (IDebugOption iDebugOption : iDebugOptionGroup.getChildren()) {
            this.addOption(iDebugOption);
        }
    }

    @Override
    public String getName() {
        return "DebugOptions";
    }

    @Override
    public IDebugOptionGroup getParent() {
        return null;
    }

    @Override
    public void setParent(IDebugOptionGroup parent) {
        throw new UnsupportedOperationException("DebugOptions is a root not. Cannot have a parent.");
    }

    private BooleanDebugOption newOption(String string, boolean boolean0) {
        BooleanDebugOption booleanDebugOption = OptionGroup.newOption(string, boolean0);
        this.addChild(booleanDebugOption);
        return booleanDebugOption;
    }

    private BooleanDebugOption newDebugOnlyOption(String string, boolean boolean0) {
        BooleanDebugOption booleanDebugOption = OptionGroup.newDebugOnlyOption(string, boolean0);
        this.addChild(booleanDebugOption);
        return booleanDebugOption;
    }

    private <E extends IDebugOptionGroup> E newOptionGroup(E iDebugOptionGroup) {
        this.addChild(iDebugOptionGroup);
        return (E)iDebugOptionGroup;
    }

    public BooleanDebugOption getOptionByName(String name) {
        for (int int0 = 0; int0 < this.options.size(); int0++) {
            BooleanDebugOption booleanDebugOption = this.options.get(int0);
            if (booleanDebugOption.getName().equals(name)) {
                return booleanDebugOption;
            }
        }

        return null;
    }

    public int getOptionCount() {
        return this.options.size();
    }

    public BooleanDebugOption getOptionByIndex(int index) {
        return this.options.get(index);
    }

    public void setBoolean(String name, boolean value) {
        BooleanDebugOption booleanDebugOption = this.getOptionByName(name);
        if (booleanDebugOption != null) {
            booleanDebugOption.setValue(value);
        }
    }

    public boolean getBoolean(String name) {
        BooleanDebugOption booleanDebugOption = this.getOptionByName(name);
        return booleanDebugOption != null ? booleanDebugOption.getValue() : false;
    }

    public void save() {
        String string = ZomboidFileSystem.instance.getCacheDirSub("debug-options.ini");
        ConfigFile configFile = new ConfigFile();
        configFile.write(string, 1, this.options);
    }

    public void load() {
        String string = ZomboidFileSystem.instance.getCacheDirSub("debug-options.ini");
        ConfigFile configFile = new ConfigFile();
        if (configFile.read(string)) {
            for (int int0 = 0; int0 < configFile.getOptions().size(); int0++) {
                ConfigOption configOption = configFile.getOptions().get(int0);
                BooleanDebugOption booleanDebugOption = this.getOptionByName(configOption.getName());
                if (booleanDebugOption != null) {
                    booleanDebugOption.parse(configOption.getValueAsString());
                }
            }
        }
    }

    public static void testThreadCrash(int idx) {
        instance.testThreadCrashInternal(idx);
    }

    private void testThreadCrashInternal(int int0) {
        if (Core.bDebug) {
            if (this.ThreadCrash_Enabled.getValue()) {
                Thread thread = Thread.currentThread();
                BooleanDebugOption[] booleanDebugOptions;
                if (thread == RenderThread.RenderThread) {
                    booleanDebugOptions = this.ThreadCrash_RenderThread;
                } else if (thread == GameWindow.GameThread) {
                    booleanDebugOptions = this.ThreadCrash_GameThread;
                } else {
                    if (thread != GameLoadingState.loader) {
                        return;
                    }

                    booleanDebugOptions = this.ThreadCrash_GameLoadingThread;
                }

                if (booleanDebugOptions[int0].getValue()) {
                    throw new Error("ThreadCrash Test! " + thread.getName());
                }
            }
        }
    }

    public static final class Checks extends OptionGroup {
        public final BooleanDebugOption BoundTextures = newDebugOnlyOption(this.Group, "BoundTextures", false);
        public final BooleanDebugOption SlowLuaEvents = newDebugOnlyOption(this.Group, "SlowLuaEvents", false);

        public Checks() {
            super("Checks");
        }
    }
}
