// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.Lua;

import fmod.fmod.EmitterType;
import fmod.fmod.FMODAudio;
import fmod.fmod.FMODManager;
import fmod.fmod.FMODSoundBank;
import fmod.fmod.FMODSoundEmitter;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.luaj.kahluafork.compiler.FuncState;
import org.lwjglx.input.Controller;
import org.lwjglx.input.Controllers;
import org.lwjglx.input.KeyCodes;
import org.lwjglx.input.Keyboard;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.LuaCaller;
import se.krka.kahlua.integration.LuaReturn;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.expose.LuaJavaClassExposer;
import se.krka.kahlua.j2se.J2SEPlatform;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.vm.Coroutine;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;
import zombie.AmbientStreamManager;
import zombie.BaseAmbientStreamManager;
import zombie.BaseSoundManager;
import zombie.DummySoundManager;
import zombie.GameSounds;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.MapGroups;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZombieSpawnRecorder;
import zombie.ZomboidFileSystem;
import zombie.ai.GameCharacterAIBrain;
import zombie.ai.MapKnowledge;
import zombie.ai.sadisticAIDirector.SleepingEvent;
import zombie.ai.states.AttackState;
import zombie.ai.states.BurntToDeath;
import zombie.ai.states.ClimbDownSheetRopeState;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbOverWallState;
import zombie.ai.states.ClimbSheetRopeState;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.CloseWindowState;
import zombie.ai.states.CrawlingZombieTurnState;
import zombie.ai.states.FakeDeadAttackState;
import zombie.ai.states.FakeDeadZombieState;
import zombie.ai.states.FishingState;
import zombie.ai.states.FitnessState;
import zombie.ai.states.IdleState;
import zombie.ai.states.LungeState;
import zombie.ai.states.OpenWindowState;
import zombie.ai.states.PathFindState;
import zombie.ai.states.PlayerActionsState;
import zombie.ai.states.PlayerAimState;
import zombie.ai.states.PlayerEmoteState;
import zombie.ai.states.PlayerExtState;
import zombie.ai.states.PlayerFallDownState;
import zombie.ai.states.PlayerFallingState;
import zombie.ai.states.PlayerGetUpState;
import zombie.ai.states.PlayerHitReactionPVPState;
import zombie.ai.states.PlayerHitReactionState;
import zombie.ai.states.PlayerKnockedDown;
import zombie.ai.states.PlayerOnGroundState;
import zombie.ai.states.PlayerSitOnGroundState;
import zombie.ai.states.PlayerStrafeState;
import zombie.ai.states.SmashWindowState;
import zombie.ai.states.StaggerBackState;
import zombie.ai.states.SwipeStatePlayer;
import zombie.ai.states.ThumpState;
import zombie.ai.states.WalkTowardState;
import zombie.ai.states.ZombieFallDownState;
import zombie.ai.states.ZombieGetDownState;
import zombie.ai.states.ZombieGetUpState;
import zombie.ai.states.ZombieIdleState;
import zombie.ai.states.ZombieOnGroundState;
import zombie.ai.states.ZombieReanimateState;
import zombie.ai.states.ZombieSittingState;
import zombie.asset.Asset;
import zombie.asset.AssetPath;
import zombie.audio.BaseSoundBank;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.DummySoundBank;
import zombie.audio.DummySoundEmitter;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.audio.parameters.ParameterRoomType;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characterTextures.BloodClothingType;
import zombie.characters.CharacterActionAnims;
import zombie.characters.CharacterSoundEmitter;
import zombie.characters.DummyCharacterSoundEmitter;
import zombie.characters.Faction;
import zombie.characters.HairOutfitDefinitions;
import zombie.characters.HaloTextHelper;
import zombie.characters.IsoDummyCameraCharacter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.Safety;
import zombie.characters.Stats;
import zombie.characters.SurvivorDesc;
import zombie.characters.SurvivorFactory;
import zombie.characters.ZombiesZoneDefinition;
import zombie.characters.AttachedItems.AttachedItem;
import zombie.characters.AttachedItems.AttachedItems;
import zombie.characters.AttachedItems.AttachedLocation;
import zombie.characters.AttachedItems.AttachedLocationGroup;
import zombie.characters.AttachedItems.AttachedLocations;
import zombie.characters.AttachedItems.AttachedWeaponDefinitions;
import zombie.characters.BodyDamage.BodyDamage;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.BodyDamage.Fitness;
import zombie.characters.BodyDamage.Metabolics;
import zombie.characters.BodyDamage.Nutrition;
import zombie.characters.BodyDamage.Thermoregulator;
import zombie.characters.CharacterTimedActions.LuaTimedAction;
import zombie.characters.CharacterTimedActions.LuaTimedActionNew;
import zombie.characters.Moodles.Moodle;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.Moodles.Moodles;
import zombie.characters.WornItems.BodyLocation;
import zombie.characters.WornItems.BodyLocationGroup;
import zombie.characters.WornItems.BodyLocations;
import zombie.characters.WornItems.WornItem;
import zombie.characters.WornItems.WornItems;
import zombie.characters.action.ActionGroup;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.skills.PerkFactory;
import zombie.characters.traits.ObservationFactory;
import zombie.characters.traits.TraitCollection;
import zombie.characters.traits.TraitFactory;
import zombie.chat.ChatBase;
import zombie.chat.ChatManager;
import zombie.chat.ChatMessage;
import zombie.chat.ServerChatMessage;
import zombie.commands.PlayerType;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigOption;
import zombie.config.DoubleConfigOption;
import zombie.config.EnumConfigOption;
import zombie.config.IntegerConfigOption;
import zombie.config.StringConfigOption;
import zombie.core.BoxedStaticValues;
import zombie.core.Clipboard;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.Core;
import zombie.core.GameVersion;
import zombie.core.ImmutableColor;
import zombie.core.IndieFileLoader;
import zombie.core.Language;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.Translator;
import zombie.core.fonts.AngelCodeFont;
import zombie.core.input.Input;
import zombie.core.logger.ExceptionLogger;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferWriter;
import zombie.core.opengl.RenderThread;
import zombie.core.physics.Bullet;
import zombie.core.physics.WorldSimulation;
import zombie.core.properties.PropertyContainer;
import zombie.core.raknet.UdpConnection;
import zombie.core.raknet.VoiceManager;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.advancedanimation.AnimNodeAssetManager;
import zombie.core.skinnedmodel.advancedanimation.AnimationSet;
import zombie.core.skinnedmodel.advancedanimation.debug.AnimatorDebugMonitor;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.model.ModelAssetManager;
import zombie.core.skinnedmodel.model.WorldItemModelDrawer;
import zombie.core.skinnedmodel.population.BeardStyle;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.core.skinnedmodel.population.ClothingDecalGroup;
import zombie.core.skinnedmodel.population.ClothingDecals;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.population.DefaultClothing;
import zombie.core.skinnedmodel.population.HairStyle;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.stash.Stash;
import zombie.core.stash.StashBuilding;
import zombie.core.stash.StashSystem;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureID;
import zombie.core.znet.GameServerDetails;
import zombie.core.znet.ISteamWorkshopCallback;
import zombie.core.znet.ServerBrowser;
import zombie.core.znet.SteamFriend;
import zombie.core.znet.SteamFriends;
import zombie.core.znet.SteamUGCDetails;
import zombie.core.znet.SteamUser;
import zombie.core.znet.SteamUtils;
import zombie.core.znet.SteamWorkshop;
import zombie.core.znet.SteamWorkshopItem;
import zombie.debug.BooleanDebugOption;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.debug.LineDrawer;
import zombie.erosion.ErosionConfig;
import zombie.erosion.ErosionData;
import zombie.erosion.ErosionMain;
import zombie.erosion.season.ErosionSeason;
import zombie.gameStates.AnimationViewerState;
import zombie.gameStates.AttachmentEditorState;
import zombie.gameStates.ChooseGameInfo;
import zombie.gameStates.ConnectToServerState;
import zombie.gameStates.DebugChunkState;
import zombie.gameStates.DebugGlobalObjectState;
import zombie.gameStates.GameLoadingState;
import zombie.gameStates.GameState;
import zombie.gameStates.IngameState;
import zombie.gameStates.LoadingQueueState;
import zombie.gameStates.MainScreenState;
import zombie.gameStates.TermsOfServiceState;
import zombie.globalObjects.CGlobalObject;
import zombie.globalObjects.CGlobalObjectSystem;
import zombie.globalObjects.CGlobalObjects;
import zombie.globalObjects.SGlobalObject;
import zombie.globalObjects.SGlobalObjectSystem;
import zombie.globalObjects.SGlobalObjects;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.input.Mouse;
import zombie.inventory.FixingManager;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemPickerJava;
import zombie.inventory.ItemType;
import zombie.inventory.RecipeManager;
import zombie.inventory.types.AlarmClock;
import zombie.inventory.types.AlarmClockClothing;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.ComboItem;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Key;
import zombie.inventory.types.KeyRing;
import zombie.inventory.types.Literature;
import zombie.inventory.types.MapItem;
import zombie.inventory.types.Moveable;
import zombie.inventory.types.Radio;
import zombie.inventory.types.WeaponPart;
import zombie.inventory.types.WeaponType;
import zombie.iso.BentFences;
import zombie.iso.BrokenFences;
import zombie.iso.BuildingDef;
import zombie.iso.CellLoader;
import zombie.iso.ContainerOverlays;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoDirectionSet;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoHeatSource;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoLot;
import zombie.iso.IsoLuaMover;
import zombie.iso.IsoMarkers;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaChunk;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoPuddles;
import zombie.iso.IsoPushableObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWaterGeometry;
import zombie.iso.IsoWorld;
import zombie.iso.LightingJNI;
import zombie.iso.LosUtil;
import zombie.iso.MetaObject;
import zombie.iso.MultiStageBuilding;
import zombie.iso.RoomDef;
import zombie.iso.SearchMode;
import zombie.iso.SliceY;
import zombie.iso.TileOverlays;
import zombie.iso.Vector2;
import zombie.iso.Vector3;
import zombie.iso.WorldMarkers;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.areas.SafeHouse;
import zombie.iso.areas.isoregion.IsoRegionLogType;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.areas.isoregion.IsoRegionsLogger;
import zombie.iso.areas.isoregion.IsoRegionsRenderer;
import zombie.iso.areas.isoregion.data.DataCell;
import zombie.iso.areas.isoregion.data.DataChunk;
import zombie.iso.areas.isoregion.regions.IsoChunkRegion;
import zombie.iso.areas.isoregion.regions.IsoWorldRegion;
import zombie.iso.objects.BSFurnace;
import zombie.iso.objects.IsoBarbecue;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoBrokenGlass;
import zombie.iso.objects.IsoCarBatteryCharger;
import zombie.iso.objects.IsoClothingDryer;
import zombie.iso.objects.IsoClothingWasher;
import zombie.iso.objects.IsoCombinationWasherDryer;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoFire;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoFireplace;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoJukebox;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoMannequin;
import zombie.iso.objects.IsoMolotovCocktail;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoStackedWasherDryer;
import zombie.iso.objects.IsoStove;
import zombie.iso.objects.IsoTelevision;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTrap;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWaveSignal;
import zombie.iso.objects.IsoWheelieBin;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.objects.ObjectRenderEffects;
import zombie.iso.objects.RainManager;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteGrid;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.weather.ClimateColorInfo;
import zombie.iso.weather.ClimateForecaster;
import zombie.iso.weather.ClimateHistory;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.ClimateMoon;
import zombie.iso.weather.ClimateValues;
import zombie.iso.weather.Temperature;
import zombie.iso.weather.ThunderStorm;
import zombie.iso.weather.WeatherPeriod;
import zombie.iso.weather.WorldFlares;
import zombie.iso.weather.fog.ImprovedFog;
import zombie.iso.weather.fx.IsoWeatherFX;
import zombie.modding.ActiveMods;
import zombie.modding.ActiveModsFile;
import zombie.modding.ModUtilsJava;
import zombie.network.ConnectionManager;
import zombie.network.CoopMaster;
import zombie.network.DBResult;
import zombie.network.DBTicket;
import zombie.network.DesktopBrowser;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ItemTransactionManager;
import zombie.network.MPStatistic;
import zombie.network.MPStatistics;
import zombie.network.NetChecksum;
import zombie.network.NetworkAIParams;
import zombie.network.PacketTypes;
import zombie.network.Server;
import zombie.network.ServerOptions;
import zombie.network.ServerSettings;
import zombie.network.ServerSettingsManager;
import zombie.network.ServerWorldDatabase;
import zombie.network.Userlog;
import zombie.network.chat.ChatServer;
import zombie.network.chat.ChatType;
import zombie.popman.ZombiePopulationManager;
import zombie.popman.ZombiePopulationRenderer;
import zombie.profanity.ProfanityFilter;
import zombie.radio.ChannelCategory;
import zombie.radio.RadioAPI;
import zombie.radio.RadioData;
import zombie.radio.ZomboidRadio;
import zombie.radio.StorySounds.DataPoint;
import zombie.radio.StorySounds.EventSound;
import zombie.radio.StorySounds.SLSoundManager;
import zombie.radio.StorySounds.StorySound;
import zombie.radio.StorySounds.StorySoundEvent;
import zombie.radio.devices.DeviceData;
import zombie.radio.devices.DevicePresets;
import zombie.radio.devices.PresetEntry;
import zombie.radio.media.MediaData;
import zombie.radio.media.RecordedMedia;
import zombie.radio.scripting.DynamicRadioChannel;
import zombie.radio.scripting.RadioBroadCast;
import zombie.radio.scripting.RadioChannel;
import zombie.radio.scripting.RadioLine;
import zombie.radio.scripting.RadioScript;
import zombie.radio.scripting.RadioScriptManager;
import zombie.randomizedWorld.RandomizedWorldBase;
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
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBandPractice;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBathroomZed;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBedroomZed;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBleach;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSCorpsePsycho;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSDeadDrunk;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSFootballNight;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSGunmanInBathroom;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSGunslinger;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSHenDo;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSHockeyPsycho;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSHouseParty;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPokerNight;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPoliceAtHouse;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPrisonEscape;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPrisonEscapeWithPolice;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSSkeletonPsycho;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSSpecificProfession;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSStagDo;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSStudentNight;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSSuicidePact;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSTinFoilHat;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSZombieLockedBathroom;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSZombiesEating;
import zombie.randomizedWorld.randomizedDeadSurvivor.RandomizedDeadSurvivorBase;
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
import zombie.savefile.PlayerDBHelper;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.EvolvedRecipe;
import zombie.scripting.objects.Fixing;
import zombie.scripting.objects.GameSoundScript;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.ItemRecipe;
import zombie.scripting.objects.MannequinScript;
import zombie.scripting.objects.ModelAttachment;
import zombie.scripting.objects.ModelScript;
import zombie.scripting.objects.MovableRecipe;
import zombie.scripting.objects.Recipe;
import zombie.scripting.objects.ScriptModule;
import zombie.scripting.objects.VehicleScript;
import zombie.spnetwork.SinglePlayerClient;
import zombie.text.templating.ReplaceProviderCharacter;
import zombie.text.templating.TemplateText;
import zombie.ui.ActionProgressBar;
import zombie.ui.Clock;
import zombie.ui.ModalDialog;
import zombie.ui.MoodlesUI;
import zombie.ui.NewHealthPanel;
import zombie.ui.ObjectTooltip;
import zombie.ui.RadarPanel;
import zombie.ui.RadialMenu;
import zombie.ui.RadialProgressBar;
import zombie.ui.SpeedControls;
import zombie.ui.TextDrawObject;
import zombie.ui.TextManager;
import zombie.ui.UI3DModel;
import zombie.ui.UIDebugConsole;
import zombie.ui.UIElement;
import zombie.ui.UIFont;
import zombie.ui.UIManager;
import zombie.ui.UIServerToolbox;
import zombie.ui.UITextBox2;
import zombie.ui.UITransition;
import zombie.ui.VehicleGauge;
import zombie.util.AddCoopPlayer;
import zombie.util.PZCalendar;
import zombie.util.PublicServerUtil;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.util.list.PZArrayList;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.EditVehicleState;
import zombie.vehicles.PathFindBehavior2;
import zombie.vehicles.PathFindState2;
import zombie.vehicles.UI3DScene;
import zombie.vehicles.VehicleDoor;
import zombie.vehicles.VehicleLight;
import zombie.vehicles.VehicleManager;
import zombie.vehicles.VehiclePart;
import zombie.vehicles.VehicleType;
import zombie.vehicles.VehicleWindow;
import zombie.vehicles.VehiclesDB2;
import zombie.world.moddata.ModData;
import zombie.worldMap.UIWorldMap;

public final class LuaManager {
    public static KahluaConverterManager converterManager = new KahluaConverterManager();
    public static J2SEPlatform platform = new J2SEPlatform();
    public static KahluaTable env;
    public static KahluaThread thread;
    public static KahluaThread debugthread;
    public static LuaCaller caller = new LuaCaller(converterManager);
    public static LuaCaller debugcaller = new LuaCaller(converterManager);
    public static LuaManager.Exposer exposer;
    public static ArrayList<String> loaded = new ArrayList<>();
    private static final HashSet<String> loading = new HashSet<>();
    public static HashMap<String, Object> loadedReturn = new HashMap<>();
    public static boolean checksumDone = false;
    public static ArrayList<String> loadList = new ArrayList<>();
    static ArrayList<String> paths = new ArrayList<>();
    private static final HashMap<String, Object> luaFunctionMap = new HashMap<>();
    private static final HashSet<KahluaTable> s_wiping = new HashSet<>();

    public static void outputTable(KahluaTable var0, int var1) {
    }

    private static void wipeRecurse(KahluaTable table0) {
        if (!table0.isEmpty()) {
            if (!s_wiping.contains(table0)) {
                s_wiping.add(table0);
                KahluaTableIterator kahluaTableIterator = table0.iterator();

                while (kahluaTableIterator.advance()) {
                    KahluaTable table1 = Type.tryCastTo(kahluaTableIterator.getValue(), KahluaTable.class);
                    if (table1 != null) {
                        wipeRecurse(table1);
                    }
                }

                s_wiping.remove(table0);
                table0.wipe();
            }
        }
    }

    public static void init() {
        loaded.clear();
        loading.clear();
        loadedReturn.clear();
        paths.clear();
        luaFunctionMap.clear();
        platform = new J2SEPlatform();
        if (env != null) {
            s_wiping.clear();
            wipeRecurse(env);
        }

        env = platform.newEnvironment();
        converterManager = new KahluaConverterManager();
        if (thread != null) {
            thread.bReset = true;
        }

        thread = new KahluaThread(platform, env);
        debugthread = new KahluaThread(platform, env);
        UIManager.defaultthread = thread;
        caller = new LuaCaller(converterManager);
        debugcaller = new LuaCaller(converterManager);
        if (exposer != null) {
            exposer.destroy();
        }

        exposer = new LuaManager.Exposer(converterManager, platform, env);
        loaded = new ArrayList<>();
        checksumDone = false;
        GameClient.checksum = "";
        GameClient.checksumValid = false;
        KahluaNumberConverter.install(converterManager);
        LuaEventManager.register(platform, env);
        LuaHookManager.register(platform, env);
        if (CoopMaster.instance != null) {
            CoopMaster.instance.register(platform, env);
        }

        if (VoiceManager.instance != null) {
            VoiceManager.instance.LuaRegister(platform, env);
        }

        KahluaTable table = env;
        exposer.exposeAll();
        exposer.TypeMap.put("function", LuaClosure.class);
        exposer.TypeMap.put("table", KahluaTable.class);
        outputTable(env, 0);
    }

    public static void LoadDir(String var0) throws URISyntaxException {
    }

    public static void LoadDirBase(String string) throws Exception {
        LoadDirBase(string, false);
    }

    public static void LoadDirBase(String string1, boolean boolean0) throws Exception {
        String string0 = "media/lua/" + string1 + "/";
        File file0 = ZomboidFileSystem.instance.getMediaFile("lua" + File.separator + string1);
        if (!paths.contains(string0)) {
            paths.add(string0);
        }

        try {
            searchFolders(ZomboidFileSystem.instance.baseURI, file0);
        } catch (IOException iOException0) {
            ExceptionLogger.logException(iOException0);
        }

        ArrayList arrayList0 = loadList;
        loadList = new ArrayList<>();
        ArrayList arrayList1 = ZomboidFileSystem.instance.getModIDs();

        for (int int0 = 0; int0 < arrayList1.size(); int0++) {
            String string2 = ZomboidFileSystem.instance.getModDir((String)arrayList1.get(int0));
            if (string2 != null) {
                File file1 = new File(string2);
                URI uri = file1.getCanonicalFile().toURI();
                File file2 = ZomboidFileSystem.instance.getCanonicalFile(file1, "media");
                File file3 = ZomboidFileSystem.instance.getCanonicalFile(file2, "lua");
                File file4 = ZomboidFileSystem.instance.getCanonicalFile(file3, string1);
                File file5 = file4;

                try {
                    searchFolders(uri, file5);
                } catch (IOException iOException1) {
                    ExceptionLogger.logException(iOException1);
                }
            }
        }

        Collections.sort(arrayList0);
        Collections.sort(loadList);
        arrayList0.addAll(loadList);
        loadList.clear();
        loadList = arrayList0;
        HashSet hashSet = new HashSet();

        for (String string3 : loadList) {
            if (!hashSet.contains(string3)) {
                hashSet.add(string3);
                String string4 = ZomboidFileSystem.instance.getAbsolutePath(string3);
                if (string4 == null) {
                    throw new IllegalStateException("couldn't find \"" + string3 + "\"");
                }

                if (!boolean0) {
                    RunLua(string4);
                }

                if (!checksumDone && !string3.contains("SandboxVars.lua") && (GameServer.bServer || GameClient.bClient)) {
                    NetChecksum.checksummer.addFile(string3, string4);
                }

                if (CoopMaster.instance != null) {
                    CoopMaster.instance.update();
                }
            }
        }

        loadList.clear();
    }

    public static void initChecksum() throws Exception {
        if (!checksumDone) {
            if (GameClient.bClient || GameServer.bServer) {
                NetChecksum.checksummer.reset(false);
            }
        }
    }

    public static void finishChecksum() {
        if (GameServer.bServer) {
            GameServer.checksum = NetChecksum.checksummer.checksumToString();
            DebugLog.General.println("luaChecksum: " + GameServer.checksum);
        } else {
            if (!GameClient.bClient) {
                return;
            }

            GameClient.checksum = NetChecksum.checksummer.checksumToString();
        }

        NetChecksum.GroupOfFiles.finishChecksum();
        checksumDone = true;
    }

    public static void LoadDirBase() throws Exception {
        initChecksum();
        LoadDirBase("shared");
        LoadDirBase("client");
    }

    public static void searchFolders(URI uri, File file) throws IOException {
        if (file.isDirectory()) {
            String[] strings = file.list();

            for (int int0 = 0; int0 < strings.length; int0++) {
                searchFolders(uri, new File(file.getCanonicalFile().getAbsolutePath() + File.separator + strings[int0]));
            }
        } else if (file.getAbsolutePath().toLowerCase().endsWith(".lua")) {
            String string = ZomboidFileSystem.instance.getRelativeFile(uri, file.getAbsolutePath());
            string = string.toLowerCase(Locale.ENGLISH);
            loadList.add(string);
        }
    }

    public static String getLuaCacheDir() {
        String string = ZomboidFileSystem.instance.getCacheDir() + File.separator + "Lua";
        File file = new File(string);
        if (!file.exists()) {
            file.mkdir();
        }

        return string;
    }

    public static String getSandboxCacheDir() {
        String string = ZomboidFileSystem.instance.getCacheDir() + File.separator + "Sandbox Presets";
        File file = new File(string);
        if (!file.exists()) {
            file.mkdir();
        }

        return string;
    }

    public static void fillContainer(ItemContainer container, IsoPlayer player) {
        ItemPickerJava.fillContainer(container, player);
    }

    public static void updateOverlaySprite(IsoObject object) {
        ItemPickerJava.updateOverlaySprite(object);
    }

    public static LuaClosure getDotDelimitedClosure(String string) {
        String[] strings = string.split("\\.");
        KahluaTable table = env;

        for (int int0 = 0; int0 < strings.length - 1; int0++) {
            table = (KahluaTable)env.rawget(strings[int0]);
        }

        return (LuaClosure)table.rawget(strings[strings.length - 1]);
    }

    public static void transferItem(IsoGameCharacter character, InventoryItem item, ItemContainer container0, ItemContainer container1) {
        LuaClosure luaClosure = (LuaClosure)env.rawget("javaTransferItems");
        caller.pcall(thread, luaClosure, character, item, container0, container1);
    }

    public static void dropItem(InventoryItem item) {
        LuaClosure luaClosure = getDotDelimitedClosure("ISInventoryPaneContextMenu.dropItem");
        caller.pcall(thread, luaClosure, item);
    }

    public static IsoGridSquare AdjacentFreeTileFinder(IsoGridSquare square, IsoPlayer player) {
        KahluaTable table = (KahluaTable)env.rawget("AdjacentFreeTileFinder");
        LuaClosure luaClosure = (LuaClosure)table.rawget("Find");
        return (IsoGridSquare)caller.pcall(thread, luaClosure, square, player)[1];
    }

    public static Object RunLua(String string) {
        return RunLua(string, false);
    }

    public static Object RunLua(String string1, boolean boolean0) {
        String string0 = string1.replace("\\", "/");
        if (loading.contains(string0)) {
            DebugLog.Lua.warn("recursive require(): %s", string0);
            return null;
        } else {
            loading.add(string0);

            Object object;
            try {
                object = RunLuaInternal(string1, boolean0);
            } finally {
                loading.remove(string0);
            }

            return object;
        }
    }

    private static Object RunLuaInternal(String string, boolean boolean0) {
        string = string.replace("\\", "/");
        if (loaded.contains(string)) {
            return loadedReturn.get(string);
        } else {
            FuncState.currentFile = string.substring(string.lastIndexOf(47) + 1);
            FuncState.currentfullFile = string;
            string = ZomboidFileSystem.instance.getString(string.replace("\\", "/"));
            if (DebugLog.isEnabled(DebugType.Lua)) {
                DebugLog.Lua.println("Loading: " + ZomboidFileSystem.instance.getRelativeFile(string));
            }

            InputStreamReader inputStreamReader;
            try {
                inputStreamReader = IndieFileLoader.getStreamReader(string);
            } catch (FileNotFoundException fileNotFoundException) {
                ExceptionLogger.logException(fileNotFoundException);
                return null;
            }

            LuaCompiler.rewriteEvents = boolean0;

            LuaClosure luaClosure;
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                luaClosure = LuaCompiler.loadis(bufferedReader, string.substring(string.lastIndexOf(47) + 1), env);
            } catch (Exception exception) {
                Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, "Error found in LUA file: " + string, null);
                ExceptionLogger.logException(exception);
                thread.debugException(exception);
                return null;
            }

            luaFunctionMap.clear();
            AttachedWeaponDefinitions.instance.m_dirty = true;
            DefaultClothing.instance.m_dirty = true;
            HairOutfitDefinitions.instance.m_dirty = true;
            ZombiesZoneDefinition.bDirty = true;
            LuaReturn luaReturn = caller.protectedCall(thread, luaClosure);
            if (!luaReturn.isSuccess()) {
                Logger.getLogger(IsoWorld.class.getName()).log(Level.SEVERE, luaReturn.getErrorString(), null);
                if (luaReturn.getJavaException() != null) {
                    Logger.getLogger(IsoWorld.class.getName()).log(Level.SEVERE, luaReturn.getJavaException().toString(), null);
                }

                Logger.getLogger(IsoWorld.class.getName()).log(Level.SEVERE, luaReturn.getLuaStackTrace(), null);
            }

            loaded.add(string);
            Object object = luaReturn.isSuccess() && luaReturn.size() > 0 ? luaReturn.getFirst() : null;
            if (object != null) {
                loadedReturn.put(string, object);
            } else {
                loadedReturn.remove(string);
            }

            LuaCompiler.rewriteEvents = false;
            return object;
        }
    }

    public static Object getFunctionObject(String string) {
        if (string != null && !string.isEmpty()) {
            Object object = luaFunctionMap.get(string);
            if (object != null) {
                return object;
            } else {
                KahluaTable table0 = env;
                if (string.contains(".")) {
                    String[] strings = string.split("\\.");

                    for (int int0 = 0; int0 < strings.length - 1; int0++) {
                        KahluaTable table1 = Type.tryCastTo(table0.rawget(strings[int0]), KahluaTable.class);
                        if (table1 == null) {
                            DebugLog.General.error("no such function \"%s\"", string);
                            return null;
                        }

                        table0 = table1;
                    }

                    object = table0.rawget(strings[strings.length - 1]);
                } else {
                    object = table0.rawget(string);
                }

                if (!(object instanceof JavaFunction) && !(object instanceof LuaClosure)) {
                    DebugLog.General.error("no such function \"%s\"", string);
                    return null;
                } else {
                    luaFunctionMap.put(string, object);
                    return object;
                }
            }
        } else {
            return null;
        }
    }

    public static void Test() throws IOException {
    }

    public static Object get(Object object) {
        return env.rawget(object);
    }

    public static void call(String string, Object object) {
        caller.pcall(thread, env.rawget(string), object);
    }

    private static void exposeKeyboardKeys(KahluaTable table1) {
        if (table1.rawget("Keyboard") instanceof KahluaTable table0) {
            Field[] fields = Keyboard.class.getFields();

            try {
                for (Field field : fields) {
                    if (Modifier.isStatic(field.getModifiers())
                        && Modifier.isPublic(field.getModifiers())
                        && Modifier.isFinal(field.getModifiers())
                        && field.getType().equals(int.class)
                        && field.getName().startsWith("KEY_")
                        && !field.getName().endsWith("WIN")) {
                        table0.rawset(field.getName(), (double)field.getInt(null));
                    }
                }
            } catch (Exception exception) {
            }
        }
    }

    private static void exposeLuaCalendar() {
        KahluaTable table = (KahluaTable)env.rawget("PZCalendar");
        if (table != null) {
            Field[] fields = Calendar.class.getFields();

            try {
                for (Field field : fields) {
                    if (Modifier.isStatic(field.getModifiers())
                        && Modifier.isPublic(field.getModifiers())
                        && Modifier.isFinal(field.getModifiers())
                        && field.getType().equals(int.class)) {
                        table.rawset(field.getName(), BoxedStaticValues.toDouble(field.getInt(null)));
                    }
                }
            } catch (Exception exception) {
            }

            env.rawset("Calendar", table);
        }
    }

    public static String getHourMinuteJava() {
        String string = Calendar.getInstance().get(12) + "";
        if (Calendar.getInstance().get(12) < 10) {
            string = "0" + string;
        }

        return Calendar.getInstance().get(11) + ":" + string;
    }

    public static KahluaTable copyTable(KahluaTable table) {
        return copyTable(null, table);
    }

    public static KahluaTable copyTable(KahluaTable table0, KahluaTable table1) {
        if (table0 == null) {
            table0 = platform.newTable();
        } else {
            table0.wipe();
        }

        if (table1 != null && !table1.isEmpty()) {
            KahluaTableIterator kahluaTableIterator = table1.iterator();

            while (kahluaTableIterator.advance()) {
                Object object0 = kahluaTableIterator.getKey();
                Object object1 = kahluaTableIterator.getValue();
                if (object1 instanceof KahluaTable) {
                    table0.rawset(object0, copyTable(null, (KahluaTable)object1));
                } else {
                    table0.rawset(object0, object1);
                }
            }

            return table0;
        } else {
            return table0;
        }
    }

    public static final class Exposer extends LuaJavaClassExposer {
        private final HashSet<Class<?>> exposed = new HashSet<>();

        public Exposer(KahluaConverterManager manager, Platform platform, KahluaTable environment) {
            super(manager, platform, environment);
        }

        public void exposeAll() {
            this.setExposed(BufferedReader.class);
            this.setExposed(BufferedWriter.class);
            this.setExposed(DataInputStream.class);
            this.setExposed(DataOutputStream.class);
            this.setExposed(Double.class);
            this.setExposed(Long.class);
            this.setExposed(Float.class);
            this.setExposed(Integer.class);
            this.setExposed(Math.class);
            this.setExposed(Void.class);
            this.setExposed(SimpleDateFormat.class);
            this.setExposed(ArrayList.class);
            this.setExposed(EnumMap.class);
            this.setExposed(HashMap.class);
            this.setExposed(LinkedList.class);
            this.setExposed(Stack.class);
            this.setExposed(Vector.class);
            this.setExposed(Iterator.class);
            this.setExposed(EmitterType.class);
            this.setExposed(FMODAudio.class);
            this.setExposed(FMODSoundBank.class);
            this.setExposed(FMODSoundEmitter.class);
            this.setExposed(Vector2f.class);
            this.setExposed(Vector3f.class);
            this.setExposed(KahluaUtil.class);
            this.setExposed(DummySoundBank.class);
            this.setExposed(DummySoundEmitter.class);
            this.setExposed(BaseSoundEmitter.class);
            this.setExposed(GameSound.class);
            this.setExposed(GameSoundClip.class);
            this.setExposed(AttackState.class);
            this.setExposed(BurntToDeath.class);
            this.setExposed(ClimbDownSheetRopeState.class);
            this.setExposed(ClimbOverFenceState.class);
            this.setExposed(ClimbOverWallState.class);
            this.setExposed(ClimbSheetRopeState.class);
            this.setExposed(ClimbThroughWindowState.class);
            this.setExposed(CloseWindowState.class);
            this.setExposed(CrawlingZombieTurnState.class);
            this.setExposed(FakeDeadAttackState.class);
            this.setExposed(FakeDeadZombieState.class);
            this.setExposed(FishingState.class);
            this.setExposed(FitnessState.class);
            this.setExposed(IdleState.class);
            this.setExposed(LungeState.class);
            this.setExposed(OpenWindowState.class);
            this.setExposed(PathFindState.class);
            this.setExposed(PlayerActionsState.class);
            this.setExposed(PlayerAimState.class);
            this.setExposed(PlayerEmoteState.class);
            this.setExposed(PlayerExtState.class);
            this.setExposed(PlayerFallDownState.class);
            this.setExposed(PlayerFallingState.class);
            this.setExposed(PlayerGetUpState.class);
            this.setExposed(PlayerHitReactionPVPState.class);
            this.setExposed(PlayerHitReactionState.class);
            this.setExposed(PlayerKnockedDown.class);
            this.setExposed(PlayerOnGroundState.class);
            this.setExposed(PlayerSitOnGroundState.class);
            this.setExposed(PlayerStrafeState.class);
            this.setExposed(SmashWindowState.class);
            this.setExposed(StaggerBackState.class);
            this.setExposed(SwipeStatePlayer.class);
            this.setExposed(ThumpState.class);
            this.setExposed(WalkTowardState.class);
            this.setExposed(ZombieFallDownState.class);
            this.setExposed(ZombieGetDownState.class);
            this.setExposed(ZombieGetUpState.class);
            this.setExposed(ZombieIdleState.class);
            this.setExposed(ZombieOnGroundState.class);
            this.setExposed(ZombieReanimateState.class);
            this.setExposed(ZombieSittingState.class);
            this.setExposed(GameCharacterAIBrain.class);
            this.setExposed(MapKnowledge.class);
            this.setExposed(BodyPartType.class);
            this.setExposed(BodyPart.class);
            this.setExposed(BodyDamage.class);
            this.setExposed(Thermoregulator.class);
            this.setExposed(Thermoregulator.ThermalNode.class);
            this.setExposed(Metabolics.class);
            this.setExposed(Fitness.class);
            this.setExposed(GameKeyboard.class);
            this.setExposed(LuaTimedAction.class);
            this.setExposed(LuaTimedActionNew.class);
            this.setExposed(Moodle.class);
            this.setExposed(Moodles.class);
            this.setExposed(MoodleType.class);
            this.setExposed(ProfessionFactory.class);
            this.setExposed(ProfessionFactory.Profession.class);
            this.setExposed(PerkFactory.class);
            this.setExposed(PerkFactory.Perk.class);
            this.setExposed(PerkFactory.Perks.class);
            this.setExposed(ObservationFactory.class);
            this.setExposed(ObservationFactory.Observation.class);
            this.setExposed(TraitFactory.class);
            this.setExposed(TraitFactory.Trait.class);
            this.setExposed(IsoDummyCameraCharacter.class);
            this.setExposed(Stats.class);
            this.setExposed(SurvivorDesc.class);
            this.setExposed(SurvivorFactory.class);
            this.setExposed(SurvivorFactory.SurvivorType.class);
            this.setExposed(IsoGameCharacter.class);
            this.setExposed(IsoGameCharacter.Location.class);
            this.setExposed(IsoGameCharacter.PerkInfo.class);
            this.setExposed(IsoGameCharacter.XP.class);
            this.setExposed(IsoGameCharacter.CharacterTraits.class);
            this.setExposed(TraitCollection.TraitSlot.class);
            this.setExposed(TraitCollection.class);
            this.setExposed(IsoPlayer.class);
            this.setExposed(IsoSurvivor.class);
            this.setExposed(IsoZombie.class);
            this.setExposed(CharacterActionAnims.class);
            this.setExposed(HaloTextHelper.class);
            this.setExposed(HaloTextHelper.ColorRGB.class);
            this.setExposed(NetworkAIParams.class);
            this.setExposed(BloodBodyPartType.class);
            this.setExposed(Clipboard.class);
            this.setExposed(AngelCodeFont.class);
            this.setExposed(ZLogger.class);
            this.setExposed(PropertyContainer.class);
            this.setExposed(ClothingItem.class);
            this.setExposed(AnimatorDebugMonitor.class);
            this.setExposed(ColorInfo.class);
            this.setExposed(Texture.class);
            this.setExposed(SteamFriend.class);
            this.setExposed(SteamUGCDetails.class);
            this.setExposed(SteamWorkshopItem.class);
            this.setExposed(Color.class);
            this.setExposed(Colors.class);
            this.setExposed(Core.class);
            this.setExposed(GameVersion.class);
            this.setExposed(ImmutableColor.class);
            this.setExposed(Language.class);
            this.setExposed(PerformanceSettings.class);
            this.setExposed(SpriteRenderer.class);
            this.setExposed(Translator.class);
            this.setExposed(PZMath.class);
            this.setExposed(DebugLog.class);
            this.setExposed(DebugOptions.class);
            this.setExposed(BooleanDebugOption.class);
            this.setExposed(DebugType.class);
            this.setExposed(ErosionConfig.class);
            this.setExposed(ErosionConfig.Debug.class);
            this.setExposed(ErosionConfig.Season.class);
            this.setExposed(ErosionConfig.Seeds.class);
            this.setExposed(ErosionConfig.Time.class);
            this.setExposed(ErosionMain.class);
            this.setExposed(ErosionSeason.class);
            this.setExposed(AnimationViewerState.class);
            this.setExposed(AnimationViewerState.BooleanDebugOption.class);
            this.setExposed(AttachmentEditorState.class);
            this.setExposed(ChooseGameInfo.Mod.class);
            this.setExposed(DebugChunkState.class);
            this.setExposed(DebugChunkState.BooleanDebugOption.class);
            this.setExposed(DebugGlobalObjectState.class);
            this.setExposed(GameLoadingState.class);
            this.setExposed(LoadingQueueState.class);
            this.setExposed(MainScreenState.class);
            this.setExposed(TermsOfServiceState.class);
            this.setExposed(CGlobalObject.class);
            this.setExposed(CGlobalObjects.class);
            this.setExposed(CGlobalObjectSystem.class);
            this.setExposed(SGlobalObject.class);
            this.setExposed(SGlobalObjects.class);
            this.setExposed(SGlobalObjectSystem.class);
            this.setExposed(Mouse.class);
            this.setExposed(AlarmClock.class);
            this.setExposed(AlarmClockClothing.class);
            this.setExposed(Clothing.class);
            this.setExposed(Clothing.ClothingPatch.class);
            this.setExposed(Clothing.ClothingPatchFabricType.class);
            this.setExposed(ComboItem.class);
            this.setExposed(Drainable.class);
            this.setExposed(DrainableComboItem.class);
            this.setExposed(Food.class);
            this.setExposed(HandWeapon.class);
            this.setExposed(InventoryContainer.class);
            this.setExposed(Key.class);
            this.setExposed(KeyRing.class);
            this.setExposed(Literature.class);
            this.setExposed(MapItem.class);
            this.setExposed(Moveable.class);
            this.setExposed(Radio.class);
            this.setExposed(WeaponPart.class);
            this.setExposed(ItemContainer.class);
            this.setExposed(ItemPickerJava.class);
            this.setExposed(InventoryItem.class);
            this.setExposed(InventoryItemFactory.class);
            this.setExposed(FixingManager.class);
            this.setExposed(RecipeManager.class);
            this.setExposed(IsoRegions.class);
            this.setExposed(IsoRegionsLogger.class);
            this.setExposed(IsoRegionsLogger.IsoRegionLog.class);
            this.setExposed(IsoRegionLogType.class);
            this.setExposed(DataCell.class);
            this.setExposed(DataChunk.class);
            this.setExposed(IsoChunkRegion.class);
            this.setExposed(IsoWorldRegion.class);
            this.setExposed(IsoRegionsRenderer.class);
            this.setExposed(IsoRegionsRenderer.BooleanDebugOption.class);
            this.setExposed(IsoBuilding.class);
            this.setExposed(IsoRoom.class);
            this.setExposed(SafeHouse.class);
            this.setExposed(BarricadeAble.class);
            this.setExposed(IsoBarbecue.class);
            this.setExposed(IsoBarricade.class);
            this.setExposed(IsoBrokenGlass.class);
            this.setExposed(IsoClothingDryer.class);
            this.setExposed(IsoClothingWasher.class);
            this.setExposed(IsoCombinationWasherDryer.class);
            this.setExposed(IsoStackedWasherDryer.class);
            this.setExposed(IsoCurtain.class);
            this.setExposed(IsoCarBatteryCharger.class);
            this.setExposed(IsoDeadBody.class);
            this.setExposed(IsoDoor.class);
            this.setExposed(IsoFire.class);
            this.setExposed(IsoFireManager.class);
            this.setExposed(IsoFireplace.class);
            this.setExposed(IsoGenerator.class);
            this.setExposed(IsoJukebox.class);
            this.setExposed(IsoLightSwitch.class);
            this.setExposed(IsoMannequin.class);
            this.setExposed(IsoMolotovCocktail.class);
            this.setExposed(IsoWaveSignal.class);
            this.setExposed(IsoRadio.class);
            this.setExposed(IsoTelevision.class);
            this.setExposed(IsoStackedWasherDryer.class);
            this.setExposed(IsoStove.class);
            this.setExposed(IsoThumpable.class);
            this.setExposed(IsoTrap.class);
            this.setExposed(IsoTree.class);
            this.setExposed(IsoWheelieBin.class);
            this.setExposed(IsoWindow.class);
            this.setExposed(IsoWindowFrame.class);
            this.setExposed(IsoWorldInventoryObject.class);
            this.setExposed(IsoZombieGiblets.class);
            this.setExposed(RainManager.class);
            this.setExposed(ObjectRenderEffects.class);
            this.setExposed(HumanVisual.class);
            this.setExposed(ItemVisual.class);
            this.setExposed(ItemVisuals.class);
            this.setExposed(IsoSprite.class);
            this.setExposed(IsoSpriteInstance.class);
            this.setExposed(IsoSpriteManager.class);
            this.setExposed(IsoSpriteGrid.class);
            this.setExposed(IsoFlagType.class);
            this.setExposed(IsoObjectType.class);
            this.setExposed(ClimateManager.class);
            this.setExposed(ClimateManager.DayInfo.class);
            this.setExposed(ClimateManager.ClimateFloat.class);
            this.setExposed(ClimateManager.ClimateColor.class);
            this.setExposed(ClimateManager.ClimateBool.class);
            this.setExposed(WeatherPeriod.class);
            this.setExposed(WeatherPeriod.WeatherStage.class);
            this.setExposed(WeatherPeriod.StrLerpVal.class);
            this.setExposed(ClimateManager.AirFront.class);
            this.setExposed(ThunderStorm.class);
            this.setExposed(ThunderStorm.ThunderCloud.class);
            this.setExposed(IsoWeatherFX.class);
            this.setExposed(Temperature.class);
            this.setExposed(ClimateColorInfo.class);
            this.setExposed(ClimateValues.class);
            this.setExposed(ClimateForecaster.class);
            this.setExposed(ClimateForecaster.DayForecast.class);
            this.setExposed(ClimateForecaster.ForecastValue.class);
            this.setExposed(ClimateHistory.class);
            this.setExposed(WorldFlares.class);
            this.setExposed(WorldFlares.Flare.class);
            this.setExposed(ImprovedFog.class);
            this.setExposed(ClimateMoon.class);
            this.setExposed(IsoPuddles.class);
            this.setExposed(IsoPuddles.PuddlesFloat.class);
            this.setExposed(BentFences.class);
            this.setExposed(BrokenFences.class);
            this.setExposed(ContainerOverlays.class);
            this.setExposed(IsoChunk.class);
            this.setExposed(BuildingDef.class);
            this.setExposed(IsoCamera.class);
            this.setExposed(IsoCell.class);
            this.setExposed(IsoChunkMap.class);
            this.setExposed(IsoDirections.class);
            this.setExposed(IsoDirectionSet.class);
            this.setExposed(IsoGridSquare.class);
            this.setExposed(IsoHeatSource.class);
            this.setExposed(IsoLightSource.class);
            this.setExposed(IsoLot.class);
            this.setExposed(IsoLuaMover.class);
            this.setExposed(IsoMetaChunk.class);
            this.setExposed(IsoMetaCell.class);
            this.setExposed(IsoMetaGrid.class);
            this.setExposed(IsoMetaGrid.Trigger.class);
            this.setExposed(IsoMetaGrid.VehicleZone.class);
            this.setExposed(IsoMetaGrid.Zone.class);
            this.setExposed(IsoMovingObject.class);
            this.setExposed(IsoObject.class);
            this.setExposed(IsoObjectPicker.class);
            this.setExposed(IsoPushableObject.class);
            this.setExposed(IsoUtils.class);
            this.setExposed(IsoWorld.class);
            this.setExposed(LosUtil.class);
            this.setExposed(MetaObject.class);
            this.setExposed(RoomDef.class);
            this.setExposed(SliceY.class);
            this.setExposed(TileOverlays.class);
            this.setExposed(Vector2.class);
            this.setExposed(Vector3.class);
            this.setExposed(WorldMarkers.class);
            this.setExposed(WorldMarkers.DirectionArrow.class);
            this.setExposed(WorldMarkers.GridSquareMarker.class);
            this.setExposed(WorldMarkers.PlayerHomingPoint.class);
            this.setExposed(SearchMode.class);
            this.setExposed(SearchMode.PlayerSearchMode.class);
            this.setExposed(SearchMode.SearchModeFloat.class);
            this.setExposed(IsoMarkers.class);
            this.setExposed(IsoMarkers.IsoMarker.class);
            this.setExposed(IsoMarkers.CircleIsoMarker.class);
            this.setExposed(LuaEventManager.class);
            this.setExposed(MapObjects.class);
            this.setExposed(ActiveMods.class);
            this.setExposed(Server.class);
            this.setExposed(ServerOptions.class);
            this.setExposed(ServerOptions.BooleanServerOption.class);
            this.setExposed(ServerOptions.DoubleServerOption.class);
            this.setExposed(ServerOptions.IntegerServerOption.class);
            this.setExposed(ServerOptions.StringServerOption.class);
            this.setExposed(ServerOptions.TextServerOption.class);
            this.setExposed(ServerSettings.class);
            this.setExposed(ServerSettingsManager.class);
            this.setExposed(ZombiePopulationRenderer.class);
            this.setExposed(ZombiePopulationRenderer.BooleanDebugOption.class);
            this.setExposed(RadioAPI.class);
            this.setExposed(DeviceData.class);
            this.setExposed(DevicePresets.class);
            this.setExposed(PresetEntry.class);
            this.setExposed(ZomboidRadio.class);
            this.setExposed(RadioData.class);
            this.setExposed(RadioScriptManager.class);
            this.setExposed(DynamicRadioChannel.class);
            this.setExposed(RadioChannel.class);
            this.setExposed(RadioBroadCast.class);
            this.setExposed(RadioLine.class);
            this.setExposed(RadioScript.class);
            this.setExposed(RadioScript.ExitOption.class);
            this.setExposed(ChannelCategory.class);
            this.setExposed(SLSoundManager.class);
            this.setExposed(StorySound.class);
            this.setExposed(StorySoundEvent.class);
            this.setExposed(EventSound.class);
            this.setExposed(DataPoint.class);
            this.setExposed(RecordedMedia.class);
            this.setExposed(MediaData.class);
            this.setExposed(MediaData.MediaLineData.class);
            this.setExposed(EvolvedRecipe.class);
            this.setExposed(Fixing.class);
            this.setExposed(Fixing.Fixer.class);
            this.setExposed(Fixing.FixerSkill.class);
            this.setExposed(GameSoundScript.class);
            this.setExposed(Item.class);
            this.setExposed(Item.Type.class);
            this.setExposed(ItemRecipe.class);
            this.setExposed(MannequinScript.class);
            this.setExposed(ModelAttachment.class);
            this.setExposed(ModelScript.class);
            this.setExposed(MovableRecipe.class);
            this.setExposed(Recipe.class);
            this.setExposed(Recipe.RequiredSkill.class);
            this.setExposed(Recipe.Result.class);
            this.setExposed(Recipe.Source.class);
            this.setExposed(ScriptModule.class);
            this.setExposed(VehicleScript.class);
            this.setExposed(VehicleScript.Area.class);
            this.setExposed(VehicleScript.Model.class);
            this.setExposed(VehicleScript.Part.class);
            this.setExposed(VehicleScript.Passenger.class);
            this.setExposed(VehicleScript.PhysicsShape.class);
            this.setExposed(VehicleScript.Position.class);
            this.setExposed(VehicleScript.Wheel.class);
            this.setExposed(ScriptManager.class);
            this.setExposed(TemplateText.class);
            this.setExposed(ReplaceProviderCharacter.class);
            this.setExposed(ActionProgressBar.class);
            this.setExposed(Clock.class);
            this.setExposed(UIDebugConsole.class);
            this.setExposed(ModalDialog.class);
            this.setExposed(MoodlesUI.class);
            this.setExposed(NewHealthPanel.class);
            this.setExposed(ObjectTooltip.class);
            this.setExposed(ObjectTooltip.Layout.class);
            this.setExposed(ObjectTooltip.LayoutItem.class);
            this.setExposed(RadarPanel.class);
            this.setExposed(RadialMenu.class);
            this.setExposed(RadialProgressBar.class);
            this.setExposed(SpeedControls.class);
            this.setExposed(TextManager.class);
            this.setExposed(UI3DModel.class);
            this.setExposed(UIElement.class);
            this.setExposed(UIFont.class);
            this.setExposed(UITransition.class);
            this.setExposed(UIManager.class);
            this.setExposed(UIServerToolbox.class);
            this.setExposed(UITextBox2.class);
            this.setExposed(VehicleGauge.class);
            this.setExposed(TextDrawObject.class);
            this.setExposed(PZArrayList.class);
            this.setExposed(PZCalendar.class);
            this.setExposed(BaseVehicle.class);
            this.setExposed(EditVehicleState.class);
            this.setExposed(PathFindBehavior2.BehaviorResult.class);
            this.setExposed(PathFindBehavior2.class);
            this.setExposed(PathFindState2.class);
            this.setExposed(UI3DScene.class);
            this.setExposed(VehicleDoor.class);
            this.setExposed(VehicleLight.class);
            this.setExposed(VehiclePart.class);
            this.setExposed(VehicleType.class);
            this.setExposed(VehicleWindow.class);
            this.setExposed(AttachedItem.class);
            this.setExposed(AttachedItems.class);
            this.setExposed(AttachedLocation.class);
            this.setExposed(AttachedLocationGroup.class);
            this.setExposed(AttachedLocations.class);
            this.setExposed(WornItems.class);
            this.setExposed(WornItem.class);
            this.setExposed(BodyLocation.class);
            this.setExposed(BodyLocationGroup.class);
            this.setExposed(BodyLocations.class);
            this.setExposed(DummySoundManager.class);
            this.setExposed(GameSounds.class);
            this.setExposed(GameTime.class);
            this.setExposed(GameWindow.class);
            this.setExposed(SandboxOptions.class);
            this.setExposed(SandboxOptions.BooleanSandboxOption.class);
            this.setExposed(SandboxOptions.DoubleSandboxOption.class);
            this.setExposed(SandboxOptions.StringSandboxOption.class);
            this.setExposed(SandboxOptions.EnumSandboxOption.class);
            this.setExposed(SandboxOptions.IntegerSandboxOption.class);
            this.setExposed(SoundManager.class);
            this.setExposed(SystemDisabler.class);
            this.setExposed(VirtualZombieManager.class);
            this.setExposed(WorldSoundManager.class);
            this.setExposed(WorldSoundManager.WorldSound.class);
            this.setExposed(DummyCharacterSoundEmitter.class);
            this.setExposed(CharacterSoundEmitter.class);
            this.setExposed(SoundManager.AmbientSoundEffect.class);
            this.setExposed(BaseAmbientStreamManager.class);
            this.setExposed(AmbientStreamManager.class);
            this.setExposed(Nutrition.class);
            this.setExposed(BSFurnace.class);
            this.setExposed(MultiStageBuilding.class);
            this.setExposed(MultiStageBuilding.Stage.class);
            this.setExposed(SleepingEvent.class);
            this.setExposed(IsoCompost.class);
            this.setExposed(Userlog.class);
            this.setExposed(Userlog.UserlogType.class);
            this.setExposed(ConfigOption.class);
            this.setExposed(BooleanConfigOption.class);
            this.setExposed(DoubleConfigOption.class);
            this.setExposed(EnumConfigOption.class);
            this.setExposed(IntegerConfigOption.class);
            this.setExposed(StringConfigOption.class);
            this.setExposed(Faction.class);
            this.setExposed(LuaManager.GlobalObject.LuaFileWriter.class);
            this.setExposed(Keyboard.class);
            this.setExposed(DBResult.class);
            this.setExposed(NonPvpZone.class);
            this.setExposed(DBTicket.class);
            this.setExposed(StashSystem.class);
            this.setExposed(StashBuilding.class);
            this.setExposed(Stash.class);
            this.setExposed(ItemType.class);
            this.setExposed(RandomizedWorldBase.class);
            this.setExposed(RandomizedBuildingBase.class);
            this.setExposed(RBBurntFireman.class);
            this.setExposed(RBBasic.class);
            this.setExposed(RBBurnt.class);
            this.setExposed(RBOther.class);
            this.setExposed(RBStripclub.class);
            this.setExposed(RBSchool.class);
            this.setExposed(RBSpiffo.class);
            this.setExposed(RBPizzaWhirled.class);
            this.setExposed(RBOffice.class);
            this.setExposed(RBHairSalon.class);
            this.setExposed(RBClinic.class);
            this.setExposed(RBPileOCrepe.class);
            this.setExposed(RBCafe.class);
            this.setExposed(RBBar.class);
            this.setExposed(RBLooted.class);
            this.setExposed(RBSafehouse.class);
            this.setExposed(RBBurntCorpse.class);
            this.setExposed(RBShopLooted.class);
            this.setExposed(RBKateAndBaldspot.class);
            this.setExposed(RandomizedDeadSurvivorBase.class);
            this.setExposed(RDSZombiesEating.class);
            this.setExposed(RDSBleach.class);
            this.setExposed(RDSDeadDrunk.class);
            this.setExposed(RDSGunmanInBathroom.class);
            this.setExposed(RDSGunslinger.class);
            this.setExposed(RDSZombieLockedBathroom.class);
            this.setExposed(RDSBandPractice.class);
            this.setExposed(RDSBathroomZed.class);
            this.setExposed(RDSBedroomZed.class);
            this.setExposed(RDSFootballNight.class);
            this.setExposed(RDSHenDo.class);
            this.setExposed(RDSStagDo.class);
            this.setExposed(RDSStudentNight.class);
            this.setExposed(RDSPokerNight.class);
            this.setExposed(RDSSuicidePact.class);
            this.setExposed(RDSPrisonEscape.class);
            this.setExposed(RDSPrisonEscapeWithPolice.class);
            this.setExposed(RDSSkeletonPsycho.class);
            this.setExposed(RDSCorpsePsycho.class);
            this.setExposed(RDSSpecificProfession.class);
            this.setExposed(RDSPoliceAtHouse.class);
            this.setExposed(RDSHouseParty.class);
            this.setExposed(RDSTinFoilHat.class);
            this.setExposed(RDSHockeyPsycho.class);
            this.setExposed(RandomizedVehicleStoryBase.class);
            this.setExposed(RVSCarCrash.class);
            this.setExposed(RVSBanditRoad.class);
            this.setExposed(RVSAmbulanceCrash.class);
            this.setExposed(RVSCrashHorde.class);
            this.setExposed(RVSCarCrashCorpse.class);
            this.setExposed(RVSPoliceBlockade.class);
            this.setExposed(RVSPoliceBlockadeShooting.class);
            this.setExposed(RVSBurntCar.class);
            this.setExposed(RVSConstructionSite.class);
            this.setExposed(RVSUtilityVehicle.class);
            this.setExposed(RVSChangingTire.class);
            this.setExposed(RVSFlippedCrash.class);
            this.setExposed(RVSTrailerCrash.class);
            this.setExposed(RandomizedZoneStoryBase.class);
            this.setExposed(RZSForestCamp.class);
            this.setExposed(RZSForestCampEaten.class);
            this.setExposed(RZSBuryingCamp.class);
            this.setExposed(RZSBeachParty.class);
            this.setExposed(RZSFishingTrip.class);
            this.setExposed(RZSBBQParty.class);
            this.setExposed(RZSHunterCamp.class);
            this.setExposed(RZSSexyTime.class);
            this.setExposed(RZSTrapperCamp.class);
            this.setExposed(RZSBaseball.class);
            this.setExposed(RZSMusicFestStage.class);
            this.setExposed(RZSMusicFest.class);
            this.setExposed(MapGroups.class);
            this.setExposed(BeardStyles.class);
            this.setExposed(BeardStyle.class);
            this.setExposed(HairStyles.class);
            this.setExposed(HairStyle.class);
            this.setExposed(BloodClothingType.class);
            this.setExposed(WeaponType.class);
            this.setExposed(IsoWaterGeometry.class);
            this.setExposed(ModData.class);
            this.setExposed(WorldMarkers.class);
            this.setExposed(ChatMessage.class);
            this.setExposed(ChatBase.class);
            this.setExposed(ServerChatMessage.class);
            this.setExposed(Safety.class);
            if (Core.bDebug) {
                this.setExposed(Field.class);
                this.setExposed(Method.class);
                this.setExposed(Coroutine.class);
            }

            UIWorldMap.setExposed(this);
            if (Core.bDebug) {
                try {
                    this.exposeMethod(Class.class, Class.class.getMethod("getName"), LuaManager.env);
                    this.exposeMethod(Class.class, Class.class.getMethod("getSimpleName"), LuaManager.env);
                } catch (NoSuchMethodException noSuchMethodException) {
                    noSuchMethodException.printStackTrace();
                }
            }

            for (Class clazz : this.exposed) {
                this.exposeLikeJavaRecursively(clazz, LuaManager.env);
            }

            this.exposeGlobalFunctions(new LuaManager.GlobalObject());
            LuaManager.exposeKeyboardKeys(LuaManager.env);
            LuaManager.exposeLuaCalendar();
        }

        public void setExposed(Class<?> clazz) {
            this.exposed.add(clazz);
        }

        @Override
        public boolean shouldExpose(Class<?> clazz) {
            return clazz == null ? false : this.exposed.contains(clazz);
        }
    }

    /**
     * Object containing global Lua functions. The methods in this class can be called from Lua using ``methodName()``, instead of qualifying them with the class name, even if they are not static: the instance is state of the Lua environment.
     */
    public static class GlobalObject {
        static FileOutputStream outStream;
        static FileInputStream inStream;
        static FileReader inFileReader = null;
        static BufferedReader inBufferedReader = null;
        static long timeLastRefresh = 0L;
        private static final LuaManager.GlobalObject.TimSortComparator timSortComparator = new LuaManager.GlobalObject.TimSortComparator();

        @LuaMethod(
            name = "loadVehicleModel",
            global = true
        )
        public static Model loadVehicleModel(String name, String loc, String tex) {
            return loadZomboidModel(name, loc, tex, "vehicle", true);
        }

        @LuaMethod(
            name = "loadStaticZomboidModel",
            global = true
        )
        public static Model loadStaticZomboidModel(String name, String loc, String tex) {
            return loadZomboidModel(name, loc, tex, null, true);
        }

        @LuaMethod(
            name = "loadSkinnedZomboidModel",
            global = true
        )
        public static Model loadSkinnedZomboidModel(String name, String loc, String tex) {
            return loadZomboidModel(name, loc, tex, null, false);
        }

        @LuaMethod(
            name = "loadZomboidModel",
            global = true
        )
        public static Model loadZomboidModel(String name, String mesh, String tex, String shader, boolean bStatic) {
            try {
                if (mesh.startsWith("/")) {
                    mesh = mesh.substring(1);
                }

                if (tex.startsWith("/")) {
                    tex = tex.substring(1);
                }

                if (StringUtils.isNullOrWhitespace(shader)) {
                    shader = "basicEffect";
                }

                if ("vehicle".equals(shader) && !Core.getInstance().getPerfReflectionsOnLoad()) {
                    shader = shader + "_noreflect";
                }

                Model model = ModelManager.instance.tryGetLoadedModel(mesh, tex, bStatic, shader, false);
                if (model != null) {
                    return model;
                } else {
                    ModelManager.instance.setModelMetaData(name, mesh, tex, shader, bStatic);
                    Model.ModelAssetParams modelAssetParams = new Model.ModelAssetParams();
                    modelAssetParams.bStatic = bStatic;
                    modelAssetParams.meshName = mesh;
                    modelAssetParams.shaderName = shader;
                    modelAssetParams.textureName = tex;
                    modelAssetParams.textureFlags = ModelManager.instance.getTextureFlags();
                    model = (Model)ModelAssetManager.instance.load(new AssetPath(name), modelAssetParams);
                    if (model != null) {
                        ModelManager.instance.putLoadedModel(mesh, tex, bStatic, shader, model);
                    }

                    return model;
                }
            } catch (Exception exception) {
                DebugLog.General
                    .error(
                        "LuaManager.loadZomboidModel> Exception thrown loading model: "
                            + name
                            + " mesh:"
                            + mesh
                            + " tex:"
                            + tex
                            + " shader:"
                            + shader
                            + " isStatic:"
                            + bStatic
                    );
                exception.printStackTrace();
                return null;
            }
        }

        @LuaMethod(
            name = "setModelMetaData",
            global = true
        )
        public static void setModelMetaData(String name, String mesh, String tex, String shader, boolean bStatic) {
            if (mesh.startsWith("/")) {
                mesh = mesh.substring(1);
            }

            if (tex.startsWith("/")) {
                tex = tex.substring(1);
            }

            ModelManager.instance.setModelMetaData(name, mesh, tex, shader, bStatic);
        }

        @LuaMethod(
            name = "reloadModelsMatching",
            global = true
        )
        public static void reloadModelsMatching(String meshName) {
            ModelManager.instance.reloadModelsMatching(meshName);
        }

        @LuaMethod(
            name = "getSLSoundManager",
            global = true
        )
        public static SLSoundManager getSLSoundManager() {
            return null;
        }

        @LuaMethod(
            name = "getRadioAPI",
            global = true
        )
        public static RadioAPI getRadioAPI() {
            return RadioAPI.hasInstance() ? RadioAPI.getInstance() : null;
        }

        @LuaMethod(
            name = "getRadioTranslators",
            global = true
        )
        public static ArrayList<String> getRadioTranslators(Language language) {
            return RadioData.getTranslatorNames(language);
        }

        @LuaMethod(
            name = "getTranslatorCredits",
            global = true
        )
        public static ArrayList<String> getTranslatorCredits(Language language) {
            File file = new File(ZomboidFileSystem.instance.getString("media/lua/shared/Translate/" + language.name() + "/credits.txt"));

            try {
                ArrayList arrayList0;
                try (
                    FileReader fileReader = new FileReader(file, Charset.forName(language.charset()));
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                ) {
                    ArrayList arrayList1 = new ArrayList();

                    String string;
                    while ((string = bufferedReader.readLine()) != null) {
                        if (!StringUtils.isNullOrWhitespace(string)) {
                            arrayList1.add(string.trim());
                        }
                    }

                    arrayList0 = arrayList1;
                }

                return arrayList0;
            } catch (FileNotFoundException fileNotFoundException) {
                return null;
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
                return null;
            }
        }

        @LuaMethod(
            name = "getBehaviourDebugPlayer",
            global = true
        )
        public static IsoGameCharacter getBehaviourDebugPlayer() {
            return null;
        }

        @LuaMethod(
            name = "setBehaviorStep",
            global = true
        )
        public static void setBehaviorStep(boolean b) {
        }

        @LuaMethod(
            name = "getPuddlesManager",
            global = true
        )
        public static IsoPuddles getPuddlesManager() {
            return IsoPuddles.getInstance();
        }

        @LuaMethod(
            name = "setPuddles",
            global = true
        )
        public static void setPuddles(float initialPuddles) {
            IsoPuddles.PuddlesFloat puddlesFloat = IsoPuddles.getInstance().getPuddlesFloat(3);
            puddlesFloat.setEnableAdmin(true);
            puddlesFloat.setAdminValue(initialPuddles);
            puddlesFloat = IsoPuddles.getInstance().getPuddlesFloat(1);
            puddlesFloat.setEnableAdmin(true);
            puddlesFloat.setAdminValue(PZMath.clamp_01(initialPuddles * 1.2F));
        }

        @LuaMethod(
            name = "getZomboidRadio",
            global = true
        )
        public static ZomboidRadio getZomboidRadio() {
            return ZomboidRadio.hasInstance() ? ZomboidRadio.getInstance() : null;
        }

        @LuaMethod(
            name = "getRandomUUID",
            global = true
        )
        public static String getRandomUUID() {
            return ModUtilsJava.getRandomUUID();
        }

        @LuaMethod(
            name = "sendItemListNet",
            global = true
        )
        public static boolean sendItemListNet(IsoPlayer sender, ArrayList<InventoryItem> items, IsoPlayer receiver, String transferID, String custom) {
            return ModUtilsJava.sendItemListNet(sender, items, receiver, transferID, custom);
        }

        @LuaMethod(
            name = "instanceof",
            global = true
        )
        public static boolean instof(Object obj, String name) {
            if ("PZKey".equals(name)) {
                boolean boolean0 = false;
            }

            if (obj == null) {
                return false;
            } else if (LuaManager.exposer.TypeMap.containsKey(name)) {
                Class clazz = LuaManager.exposer.TypeMap.get(name);
                return clazz.isInstance(obj);
            } else {
                return name.equals("LuaClosure") && obj instanceof LuaClosure ? true : name.equals("KahluaTableImpl") && obj instanceof KahluaTableImpl;
            }
        }

        @LuaMethod(
            name = "serverConnect",
            global = true
        )
        public static void serverConnect(
            String string0, String string1, String string2, String string3, String string4, String string5, String string6, boolean boolean0
        ) {
            Core.GameMode = "Multiplayer";
            Core.setDifficulty("Hardcore");
            if (GameClient.connection != null) {
                GameClient.connection.forceDisconnect("lua-connect");
            }

            GameClient.instance.resetDisconnectTimer();
            GameClient.bClient = true;
            GameClient.bCoopInvite = false;
            ZomboidFileSystem.instance.cleanMultiplayerSaves();
            GameClient.instance.doConnect(string0, string1, string2, string3, string4, string5, string6, boolean0);
        }

        @LuaMethod(
            name = "serverConnectCoop",
            global = true
        )
        public static void serverConnectCoop(String serverSteamID) {
            Core.GameMode = "Multiplayer";
            Core.setDifficulty("Hardcore");
            if (GameClient.connection != null) {
                GameClient.connection.forceDisconnect("lua-connect-coop");
            }

            GameClient.bClient = true;
            GameClient.bCoopInvite = true;
            GameClient.instance.doConnectCoop(serverSteamID);
        }

        @LuaMethod(
            name = "sendPing",
            global = true
        )
        public static void sendPing() {
            if (GameClient.bClient) {
                ByteBufferWriter byteBufferWriter = GameClient.connection.startPingPacket();
                PacketTypes.doPingPacket(byteBufferWriter);
                byteBufferWriter.putLong(System.currentTimeMillis());
                GameClient.connection.endPingPacket();
            }
        }

        @LuaMethod(
            name = "connectionManagerLog",
            global = true
        )
        public static void connectionManagerLog(String string0, String string1) {
            ConnectionManager.log(string0, string1, null);
        }

        @LuaMethod(
            name = "forceDisconnect",
            global = true
        )
        public static void forceDisconnect() {
            if (GameClient.connection != null) {
                GameClient.connection.forceDisconnect("lua-force-disconnect");
            }
        }

        @LuaMethod(
            name = "backToSinglePlayer",
            global = true
        )
        public static void backToSinglePlayer() {
            if (GameClient.bClient) {
                GameClient.instance.doDisconnect("going back to single-player");
                GameClient.bClient = false;
                timeLastRefresh = 0L;
            }
        }

        @LuaMethod(
            name = "isIngameState",
            global = true
        )
        public static boolean isIngameState() {
            return GameWindow.states.current == IngameState.instance;
        }

        @LuaMethod(
            name = "requestPacketCounts",
            global = true
        )
        public static void requestPacketCounts() {
            if (GameClient.bClient) {
                GameClient.instance.requestPacketCounts();
            }
        }

        @LuaMethod(
            name = "canConnect",
            global = true
        )
        public static boolean canConnect() {
            return GameClient.instance.canConnect();
        }

        @LuaMethod(
            name = "getReconnectCountdownTimer",
            global = true
        )
        public static String getReconnectCountdownTimer() {
            return GameClient.instance.getReconnectCountdownTimer();
        }

        @LuaMethod(
            name = "getPacketCounts",
            global = true
        )
        public static KahluaTable getPacketCounts(int category) {
            return GameClient.bClient ? PacketTypes.getPacketCounts(category) : null;
        }

        @LuaMethod(
            name = "getAllItems",
            global = true
        )
        public static ArrayList<Item> getAllItems() {
            return ScriptManager.instance.getAllItems();
        }

        @LuaMethod(
            name = "scoreboardUpdate",
            global = true
        )
        public static void scoreboardUpdate() {
            GameClient.instance.scoreboardUpdate();
        }

        @LuaMethod(
            name = "save",
            global = true
        )
        public static void save(boolean doCharacter) {
            try {
                GameWindow.save(doCharacter);
            } catch (Throwable throwable) {
                ExceptionLogger.logException(throwable);
            }
        }

        @LuaMethod(
            name = "saveGame",
            global = true
        )
        public static void saveGame() {
            save(true);
        }

        @LuaMethod(
            name = "getAllRecipes",
            global = true
        )
        public static ArrayList<Recipe> getAllRecipes() {
            return new ArrayList<>(ScriptManager.instance.getAllRecipes());
        }

        @LuaMethod(
            name = "requestUserlog",
            global = true
        )
        public static void requestUserlog(String user) {
            if (GameClient.bClient) {
                GameClient.instance.requestUserlog(user);
            }
        }

        @LuaMethod(
            name = "addUserlog",
            global = true
        )
        public static void addUserlog(String user, String type, String text) {
            if (GameClient.bClient) {
                GameClient.instance.addUserlog(user, type, text);
            }
        }

        @LuaMethod(
            name = "removeUserlog",
            global = true
        )
        public static void removeUserlog(String user, String type, String text) {
            if (GameClient.bClient) {
                GameClient.instance.removeUserlog(user, type, text);
            }
        }

        @LuaMethod(
            name = "tabToX",
            global = true
        )
        public static String tabToX(String a, int tabX) {
            while (a.length() < tabX) {
                a = a + " ";
            }

            return a;
        }

        @LuaMethod(
            name = "istype",
            global = true
        )
        public static boolean isType(Object obj, String name) {
            if (LuaManager.exposer.TypeMap.containsKey(name)) {
                Class clazz = LuaManager.exposer.TypeMap.get(name);
                return clazz.equals(obj.getClass());
            } else {
                return false;
            }
        }

        @LuaMethod(
            name = "isoToScreenX",
            global = true
        )
        public static float isoToScreenX(int player, float x, float y, float z) {
            float float0 = IsoUtils.XToScreen(x, y, z, 0) - IsoCamera.cameras[player].getOffX();
            float0 /= Core.getInstance().getZoom(player);
            return IsoCamera.getScreenLeft(player) + float0;
        }

        @LuaMethod(
            name = "isoToScreenY",
            global = true
        )
        public static float isoToScreenY(int player, float x, float y, float z) {
            float float0 = IsoUtils.YToScreen(x, y, z, 0) - IsoCamera.cameras[player].getOffY();
            float0 /= Core.getInstance().getZoom(player);
            return IsoCamera.getScreenTop(player) + float0;
        }

        @LuaMethod(
            name = "screenToIsoX",
            global = true
        )
        public static float screenToIsoX(int player, float x, float y, float z) {
            float float0 = Core.getInstance().getZoom(player);
            x -= IsoCamera.getScreenLeft(player);
            y -= IsoCamera.getScreenTop(player);
            return IsoCamera.cameras[player].XToIso(x * float0, y * float0, z);
        }

        @LuaMethod(
            name = "screenToIsoY",
            global = true
        )
        public static float screenToIsoY(int player, float x, float y, float z) {
            float float0 = Core.getInstance().getZoom(player);
            x -= IsoCamera.getScreenLeft(player);
            y -= IsoCamera.getScreenTop(player);
            return IsoCamera.cameras[player].YToIso(x * float0, y * float0, z);
        }

        @LuaMethod(
            name = "getAmbientStreamManager",
            global = true
        )
        public static BaseAmbientStreamManager getAmbientStreamManager() {
            return AmbientStreamManager.instance;
        }

        @LuaMethod(
            name = "getSleepingEvent",
            global = true
        )
        public static SleepingEvent getSleepingEvent() {
            return SleepingEvent.instance;
        }

        @LuaMethod(
            name = "setPlayerMovementActive",
            global = true
        )
        public static void setPlayerMovementActive(int id, boolean bActive) {
            IsoPlayer.players[id].bJoypadMovementActive = bActive;
        }

        @LuaMethod(
            name = "setActivePlayer",
            global = true
        )
        public static void setActivePlayer(int id) {
            if (!GameClient.bClient) {
                IsoPlayer.setInstance(IsoPlayer.players[id]);
                IsoCamera.CamCharacter = IsoPlayer.getInstance();
            }
        }

        /**
         * Gets the current player. To support splitscreen, getSpecificPlayer() should be preferred instead.
         * @return The current player.
         */
        @LuaMethod(
            name = "getPlayer",
            global = true
        )
        public static IsoPlayer getPlayer() {
            return IsoPlayer.getInstance();
        }

        @LuaMethod(
            name = "getNumActivePlayers",
            global = true
        )
        public static int getNumActivePlayers() {
            return IsoPlayer.numPlayers;
        }

        @LuaMethod(
            name = "playServerSound",
            global = true
        )
        public static void playServerSound(String sound, IsoGridSquare sq) {
            GameServer.PlayWorldSoundServer(sound, false, sq, 0.2F, 5.0F, 1.1F, true);
        }

        @LuaMethod(
            name = "getMaxActivePlayers",
            global = true
        )
        public static int getMaxActivePlayers() {
            return 4;
        }

        @LuaMethod(
            name = "getPlayerScreenLeft",
            global = true
        )
        public static int getPlayerScreenLeft(int player) {
            return IsoCamera.getScreenLeft(player);
        }

        @LuaMethod(
            name = "getPlayerScreenTop",
            global = true
        )
        public static int getPlayerScreenTop(int player) {
            return IsoCamera.getScreenTop(player);
        }

        @LuaMethod(
            name = "getPlayerScreenWidth",
            global = true
        )
        public static int getPlayerScreenWidth(int player) {
            return IsoCamera.getScreenWidth(player);
        }

        @LuaMethod(
            name = "getPlayerScreenHeight",
            global = true
        )
        public static int getPlayerScreenHeight(int player) {
            return IsoCamera.getScreenHeight(player);
        }

        @LuaMethod(
            name = "getPlayerByOnlineID",
            global = true
        )
        public static IsoPlayer getPlayerByOnlineID(int id) {
            if (GameServer.bServer) {
                return GameServer.IDToPlayerMap.get((short)id);
            } else {
                return GameClient.bClient ? GameClient.IDToPlayerMap.get((short)id) : null;
            }
        }

        @LuaMethod(
            name = "initUISystem",
            global = true
        )
        public static void initUISystem() {
            UIManager.init();
            LuaEventManager.triggerEvent("OnCreatePlayer", 0, IsoPlayer.players[0]);
        }

        @LuaMethod(
            name = "getPerformance",
            global = true
        )
        public static PerformanceSettings getPerformance() {
            return PerformanceSettings.instance;
        }

        @LuaMethod(
            name = "getDBSchema",
            global = true
        )
        public static void getDBSchema() {
            GameClient.instance.getDBSchema();
        }

        @LuaMethod(
            name = "getTableResult",
            global = true
        )
        public static void getTableResult(String tableName, int numberPerPages) {
            GameClient.instance.getTableResult(tableName, numberPerPages);
        }

        @LuaMethod(
            name = "getWorldSoundManager",
            global = true
        )
        public static WorldSoundManager getWorldSoundManager() {
            return WorldSoundManager.instance;
        }

        @LuaMethod(
            name = "AddWorldSound",
            global = true
        )
        public static void AddWorldSound(IsoPlayer player, int radius, int volume) {
            WorldSoundManager.instance.addSound(null, (int)player.getX(), (int)player.getY(), (int)player.getZ(), radius, volume, false);
        }

        @LuaMethod(
            name = "AddNoiseToken",
            global = true
        )
        public static void AddNoiseToken(IsoGridSquare sq, int radius) {
        }

        @LuaMethod(
            name = "pauseSoundAndMusic",
            global = true
        )
        public static void pauseSoundAndMusic() {
            DebugLog.log("EXITDEBUG: pauseSoundAndMusic 1");
            SoundManager.instance.pauseSoundAndMusic();
            DebugLog.log("EXITDEBUG: pauseSoundAndMusic 2");
        }

        @LuaMethod(
            name = "resumeSoundAndMusic",
            global = true
        )
        public static void resumeSoundAndMusic() {
            SoundManager.instance.resumeSoundAndMusic();
        }

        @LuaMethod(
            name = "isDemo",
            global = true
        )
        public static boolean isDemo() {
            Core.getInstance();
            return false;
        }

        @LuaMethod(
            name = "getTimeInMillis",
            global = true
        )
        public static long getTimeInMillis() {
            return System.currentTimeMillis();
        }

        @LuaMethod(
            name = "getCurrentCoroutine",
            global = true
        )
        public static Coroutine getCurrentCoroutine() {
            return LuaManager.thread.getCurrentCoroutine();
        }

        @LuaMethod(
            name = "reloadLuaFile",
            global = true
        )
        public static void reloadLuaFile(String filename) {
            if (StringUtils.containsDoubleDot(filename)) {
                DebugLog.Lua.warn("relative paths not allowed");
            } else {
                ZomboidFileSystem.instance.validatePrefix(filename);
                LuaManager.loaded.remove(filename);
                LuaManager.RunLua(filename, true);
            }
        }

        @LuaMethod(
            name = "reloadServerLuaFile",
            global = true
        )
        public static void reloadServerLuaFile(String filename) {
            if (GameServer.bServer) {
                if (StringUtils.containsDoubleDot(filename)) {
                    DebugLog.Lua.warn("relative paths not allowed");
                } else {
                    ZomboidFileSystem.instance.validatePrefix(filename);
                    filename = ZomboidFileSystem.instance.getCacheDir() + File.separator + "Server" + File.separator + filename;
                    LuaManager.loaded.remove(filename);
                    LuaManager.RunLua(filename, true);
                }
            }
        }

        @LuaMethod(
            name = "getServerSpawnRegions",
            global = true
        )
        public static KahluaTable getServerSpawnRegions() {
            return !GameClient.bClient ? null : GameClient.instance.getServerSpawnRegions();
        }

        @LuaMethod(
            name = "getServerOptions",
            global = true
        )
        public static ServerOptions getServerOptions() {
            return ServerOptions.instance;
        }

        @LuaMethod(
            name = "getServerName",
            global = true
        )
        public static String getServerName() {
            if (GameServer.bServer) {
                return GameServer.ServerName;
            } else {
                return GameClient.bClient ? GameClient.ServerName : "";
            }
        }

        @LuaMethod(
            name = "getServerIP",
            global = true
        )
        public static String getServerIP() {
            if (GameServer.bServer) {
                return GameServer.IPCommandline == null ? GameServer.ip : GameServer.IPCommandline;
            } else {
                return GameClient.bClient ? GameClient.ip : "";
            }
        }

        @LuaMethod(
            name = "getServerPort",
            global = true
        )
        public static String getServerPort() {
            if (GameServer.bServer) {
                return String.valueOf(GameServer.DEFAULT_PORT);
            } else {
                return GameClient.bClient ? String.valueOf(GameClient.port) : "";
            }
        }

        @LuaMethod(
            name = "isShowConnectionInfo",
            global = true
        )
        public static boolean isShowConnectionInfo() {
            return NetworkAIParams.isShowConnectionInfo();
        }

        @LuaMethod(
            name = "setShowConnectionInfo",
            global = true
        )
        public static void setShowConnectionInfo(boolean enabled) {
            NetworkAIParams.setShowConnectionInfo(enabled);
        }

        @LuaMethod(
            name = "isShowServerInfo",
            global = true
        )
        public static boolean isShowServerInfo() {
            return NetworkAIParams.isShowServerInfo();
        }

        @LuaMethod(
            name = "setShowServerInfo",
            global = true
        )
        public static void setShowServerInfo(boolean enabled) {
            NetworkAIParams.setShowServerInfo(enabled);
        }

        @LuaMethod(
            name = "isShowPingInfo",
            global = true
        )
        public static boolean isShowPingInfo() {
            return NetworkAIParams.isShowPingInfo();
        }

        @LuaMethod(
            name = "setShowPingInfo",
            global = true
        )
        public static void setShowPingInfo(boolean enabled) {
            NetworkAIParams.setShowPingInfo(enabled);
        }

        @LuaMethod(
            name = "getSpecificPlayer",
            global = true
        )
        public static IsoPlayer getSpecificPlayer(int player) {
            return IsoPlayer.players[player];
        }

        @LuaMethod(
            name = "getCameraOffX",
            global = true
        )
        public static float getCameraOffX() {
            return IsoCamera.getOffX();
        }

        @LuaMethod(
            name = "getLatestSave",
            global = true
        )
        public static KahluaTable getLatestSave() {
            KahluaTable table = LuaManager.platform.newTable();
            BufferedReader bufferedReader = null;

            try {
                bufferedReader = new BufferedReader(new FileReader(new File(ZomboidFileSystem.instance.getCacheDir() + File.separator + "latestSave.ini")));
            } catch (FileNotFoundException fileNotFoundException) {
                return table;
            }

            try {
                Object object = null;

                for (int int0 = 1; (object = bufferedReader.readLine()) != null; int0++) {
                    table.rawset(int0, object);
                }

                bufferedReader.close();
                return table;
            } catch (Exception exception) {
                return table;
            }
        }

        @LuaMethod(
            name = "isCurrentExecutionPoint",
            global = true
        )
        public static boolean isCurrentExecutionPoint(String file, int line) {
            int int0 = LuaManager.thread.currentCoroutine.getCallframeTop() - 1;
            if (int0 < 0) {
                int0 = 0;
            }

            LuaCallFrame luaCallFrame = LuaManager.thread.currentCoroutine.getCallFrame(int0);
            return luaCallFrame.closure == null
                ? false
                : luaCallFrame.closure.prototype.lines[luaCallFrame.pc] == line && file.equals(luaCallFrame.closure.prototype.filename);
        }

        @LuaMethod(
            name = "toggleBreakOnChange",
            global = true
        )
        public static void toggleBreakOnChange(KahluaTable table, Object key) {
            if (Core.bDebug) {
                LuaManager.thread.toggleBreakOnChange(table, key);
            }
        }

        @LuaMethod(
            name = "isDebugEnabled",
            global = true
        )
        public static boolean isDebugEnabled() {
            return Core.bDebug;
        }

        @LuaMethod(
            name = "toggleBreakOnRead",
            global = true
        )
        public static void toggleBreakOnRead(KahluaTable table, Object key) {
            if (Core.bDebug) {
                LuaManager.thread.toggleBreakOnRead(table, key);
            }
        }

        @LuaMethod(
            name = "toggleBreakpoint",
            global = true
        )
        public static void toggleBreakpoint(String file, int line) {
            file = file.replace("\\", "/");
            if (Core.bDebug) {
                LuaManager.thread.breakpointToggle(file, line);
            }
        }

        @LuaMethod(
            name = "sendVisual",
            global = true
        )
        public static void sendVisual(IsoPlayer player) {
            if (GameClient.bClient) {
                GameClient.instance.sendVisual(player);
            }
        }

        @LuaMethod(
            name = "sendClothing",
            global = true
        )
        public static void sendClothing(IsoPlayer player) {
            if (GameClient.bClient) {
                GameClient.instance.sendClothing(player, "", null);
            }
        }

        @LuaMethod(
            name = "hasDataReadBreakpoint",
            global = true
        )
        public static boolean hasDataReadBreakpoint(KahluaTable table, Object key) {
            return LuaManager.thread.hasReadDataBreakpoint(table, key);
        }

        @LuaMethod(
            name = "hasDataBreakpoint",
            global = true
        )
        public static boolean hasDataBreakpoint(KahluaTable table, Object key) {
            return LuaManager.thread.hasDataBreakpoint(table, key);
        }

        @LuaMethod(
            name = "hasBreakpoint",
            global = true
        )
        public static boolean hasBreakpoint(String file, int line) {
            return LuaManager.thread.hasBreakpoint(file, line);
        }

        @LuaMethod(
            name = "getLoadedLuaCount",
            global = true
        )
        public static int getLoadedLuaCount() {
            return LuaManager.loaded.size();
        }

        @LuaMethod(
            name = "getLoadedLua",
            global = true
        )
        public static String getLoadedLua(int n) {
            return LuaManager.loaded.get(n);
        }

        @LuaMethod(
            name = "isServer",
            global = true
        )
        public static boolean isServer() {
            return GameServer.bServer;
        }

        @LuaMethod(
            name = "isServerSoftReset",
            global = true
        )
        public static boolean isServerSoftReset() {
            return GameServer.bServer && GameServer.bSoftReset;
        }

        @LuaMethod(
            name = "isClient",
            global = true
        )
        public static boolean isClient() {
            return GameClient.bClient;
        }

        @LuaMethod(
            name = "canModifyPlayerStats",
            global = true
        )
        public static boolean canModifyPlayerStats() {
            return !GameClient.bClient ? true : GameClient.canModifyPlayerStats();
        }

        @LuaMethod(
            name = "executeQuery",
            global = true
        )
        public static void executeQuery(String query, KahluaTable params) {
            GameClient.instance.executeQuery(query, params);
        }

        @LuaMethod(
            name = "canSeePlayerStats",
            global = true
        )
        public static boolean canSeePlayerStats() {
            return GameClient.canSeePlayerStats();
        }

        @LuaMethod(
            name = "getAccessLevel",
            global = true
        )
        public static String getAccessLevel() {
            return PlayerType.toString(GameClient.connection.accessLevel);
        }

        @LuaMethod(
            name = "getOnlinePlayers",
            global = true
        )
        public static ArrayList<IsoPlayer> getOnlinePlayers() {
            if (GameServer.bServer) {
                return GameServer.getPlayers();
            } else {
                return GameClient.bClient ? GameClient.instance.getPlayers() : null;
            }
        }

        @LuaMethod(
            name = "getDebug",
            global = true
        )
        public static boolean getDebug() {
            return Core.bDebug || GameServer.bServer && GameServer.bDebug;
        }

        @LuaMethod(
            name = "getCameraOffY",
            global = true
        )
        public static float getCameraOffY() {
            return IsoCamera.getOffY();
        }

        /**
         * Create a dynamic table containing all spawnpoints.lua we find in vanilla  folder + in loaded mods
         */
        @LuaMethod(
            name = "createRegionFile",
            global = true
        )
        public static KahluaTable createRegionFile() {
            KahluaTable table0 = LuaManager.platform.newTable();
            String string0 = IsoWorld.instance.getMap();
            if (string0.equals("DEFAULT")) {
                MapGroups mapGroups = new MapGroups();
                mapGroups.createGroups();
                if (mapGroups.getNumberOfGroups() != 1) {
                    throw new RuntimeException("GameMap is DEFAULT but there are multiple worlds to choose from");
                }

                mapGroups.setWorld(0);
                string0 = IsoWorld.instance.getMap();
            }

            if (!GameClient.bClient && !GameServer.bServer) {
                string0 = MapGroups.addMissingVanillaDirectories(string0);
            }

            String[] strings = string0.split(";");
            int int0 = 1;

            for (String string1 : strings) {
                string1 = string1.trim();
                if (!string1.isEmpty()) {
                    File file = new File(ZomboidFileSystem.instance.getString("media/maps/" + string1 + "/spawnpoints.lua"));
                    if (file.exists()) {
                        KahluaTable table1 = LuaManager.platform.newTable();
                        table1.rawset("name", string1);
                        table1.rawset("file", "media/maps/" + string1 + "/spawnpoints.lua");
                        table0.rawset(int0, table1);
                        int0++;
                    }
                }
            }

            return table0;
        }

        @LuaMethod(
            name = "getMapDirectoryTable",
            global = true
        )
        public static KahluaTable getMapDirectoryTable() {
            KahluaTable table = LuaManager.platform.newTable();
            File file = ZomboidFileSystem.instance.getMediaFile("maps");
            String[] strings = file.list();
            if (strings == null) {
                return table;
            } else {
                int int0 = 1;

                for (int int1 = 0; int1 < strings.length; int1++) {
                    String string0 = strings[int1];
                    if (!string0.equals("challengemaps")) {
                        table.rawset(int0, string0);
                        int0++;
                    }
                }

                for (String string1 : ZomboidFileSystem.instance.getModIDs()) {
                    ChooseGameInfo.Mod mod = null;

                    try {
                        mod = ChooseGameInfo.getAvailableModDetails(string1);
                    } catch (Exception exception) {
                    }

                    if (mod != null) {
                        file = new File(mod.getDir() + "/media/maps/");
                        if (file.exists()) {
                            strings = file.list();
                            if (strings != null) {
                                for (int int2 = 0; int2 < strings.length; int2++) {
                                    String string2 = strings[int2];
                                    ChooseGameInfo.Map map = ChooseGameInfo.getMapDetails(string2);
                                    if (map.getLotDirectories() != null && !map.getLotDirectories().isEmpty() && !string2.equals("challengemaps")) {
                                        table.rawset(int0, string2);
                                        int0++;
                                    }
                                }
                            }
                        }
                    }
                }

                return table;
            }
        }

        @LuaMethod(
            name = "deleteSave",
            global = true
        )
        public static void deleteSave(String file) {
            if (StringUtils.containsDoubleDot(file)) {
                DebugLog.Lua.warn("relative paths not allowed");
            } else {
                File file0 = new File(ZomboidFileSystem.instance.getSaveDirSub(file));
                String[] strings = file0.list();
                if (strings != null) {
                    for (int int0 = 0; int0 < strings.length; int0++) {
                        File file1 = new File(ZomboidFileSystem.instance.getSaveDirSub(file + File.separator + strings[int0]));
                        if (file1.isDirectory()) {
                            deleteSave(file + File.separator + file1.getName());
                        }

                        file1.delete();
                    }

                    file0.delete();
                }
            }
        }

        @LuaMethod(
            name = "sendPlayerExtraInfo",
            global = true
        )
        public static void sendPlayerExtraInfo(IsoPlayer p) {
            GameClient.sendPlayerExtraInfo(p);
        }

        @LuaMethod(
            name = "getServerAddressFromArgs",
            global = true
        )
        public static String getServerAddressFromArgs() {
            if (System.getProperty("args.server.connect") != null) {
                String string = System.getProperty("args.server.connect");
                System.clearProperty("args.server.connect");
                return string;
            } else {
                return null;
            }
        }

        @LuaMethod(
            name = "getServerPasswordFromArgs",
            global = true
        )
        public static String getServerPasswordFromArgs() {
            if (System.getProperty("args.server.password") != null) {
                String string = System.getProperty("args.server.password");
                System.clearProperty("args.server.password");
                return string;
            } else {
                return null;
            }
        }

        @LuaMethod(
            name = "getServerListFile",
            global = true
        )
        public static String getServerListFile() {
            return SteamUtils.isSteamModeEnabled() ? "ServerListSteam.txt" : "ServerList.txt";
        }

        @LuaMethod(
            name = "getServerList",
            global = true
        )
        public static KahluaTable getServerList() {
            ArrayList arrayList = new ArrayList();
            KahluaTable table = LuaManager.platform.newTable();
            BufferedReader bufferedReader = null;

            try {
                File file = new File(LuaManager.getLuaCacheDir() + File.separator + getServerListFile());
                if (!file.exists()) {
                    file.createNewFile();
                }

                bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
                Object object = null;
                Server server0 = null;

                while ((object = bufferedReader.readLine()) != null) {
                    if (object.startsWith("name=")) {
                        server0 = new Server();
                        arrayList.add(server0);
                        server0.setName(object.replaceFirst("name=", ""));
                    } else if (object.startsWith("ip=")) {
                        server0.setIp(object.replaceFirst("ip=", ""));
                    } else if (object.startsWith("localip=")) {
                        server0.setLocalIP(object.replaceFirst("localip=", ""));
                    } else if (object.startsWith("description=")) {
                        server0.setDescription(object.replaceFirst("description=", ""));
                    } else if (object.startsWith("port=")) {
                        server0.setPort(object.replaceFirst("port=", ""));
                    } else if (object.startsWith("user=")) {
                        server0.setUserName(object.replaceFirst("user=", ""));
                    } else if (object.startsWith("password=")) {
                        server0.setPwd(object.replaceFirst("password=", ""));
                    } else if (object.startsWith("serverpassword=")) {
                        server0.setServerPassword(object.replaceFirst("serverpassword=", ""));
                    } else if (object.startsWith("usesteamrelay=")) {
                        server0.setUseSteamRelay(Boolean.parseBoolean(object.replaceFirst("usesteamrelay=", "")));
                    }
                }

                int int0 = 1;

                for (int int1 = 0; int1 < arrayList.size(); int1++) {
                    Server server1 = (Server)arrayList.get(int1);
                    Double double0 = (double)int0;
                    table.rawset(double0, server1);
                    int0++;
                }
            } catch (Exception exception0) {
                exception0.printStackTrace();
            } finally {
                try {
                    bufferedReader.close();
                } catch (Exception exception1) {
                }
            }

            return table;
        }

        @LuaMethod(
            name = "ping",
            global = true
        )
        public static void ping(String username, String pwd, String ip, String port) {
            GameClient.askPing = true;
            serverConnect(username, pwd, ip, "", port, "", "", false);
        }

        @LuaMethod(
            name = "stopPing",
            global = true
        )
        public static void stopPing() {
            GameClient.askPing = false;
        }

        @LuaMethod(
            name = "transformIntoKahluaTable",
            global = true
        )
        public static KahluaTable transformIntoKahluaTable(HashMap<Object, Object> map) {
            KahluaTable table = LuaManager.platform.newTable();

            for (Entry entry : map.entrySet()) {
                table.rawset(entry.getKey(), entry.getValue());
            }

            return table;
        }

        @LuaMethod(
            name = "getSaveDirectory",
            global = true
        )
        public static ArrayList<File> getSaveDirectory(String folder) {
            ZomboidFileSystem.instance.validatePrefix(folder);
            File file0 = new File(folder + File.separator);
            if (!file0.exists() && !Core.getInstance().isNoSave()) {
                file0.mkdir();
            }

            String[] strings = file0.list();
            if (strings == null) {
                return null;
            } else {
                ArrayList arrayList = new ArrayList();

                for (int int0 = 0; int0 < strings.length; int0++) {
                    File file1 = new File(folder + File.separator + strings[int0]);
                    if (file1.isDirectory()) {
                        arrayList.add(file1);
                    }
                }

                return arrayList;
            }
        }

        @LuaMethod(
            name = "getFullSaveDirectoryTable",
            global = true
        )
        public static KahluaTable getFullSaveDirectoryTable() {
            KahluaTable table = LuaManager.platform.newTable();
            File file0 = new File(ZomboidFileSystem.instance.getSaveDir() + File.separator);
            if (!file0.exists()) {
                file0.mkdir();
            }

            String[] strings = file0.list();
            if (strings == null) {
                return table;
            } else {
                ArrayList arrayList0 = new ArrayList();

                for (int int0 = 0; int0 < strings.length; int0++) {
                    File file1 = new File(ZomboidFileSystem.instance.getSaveDir() + File.separator + strings[int0]);
                    if (file1.isDirectory() && !"Multiplayer".equals(strings[int0])) {
                        ArrayList arrayList1 = getSaveDirectory(ZomboidFileSystem.instance.getSaveDir() + File.separator + strings[int0]);
                        arrayList0.addAll(arrayList1);
                    }
                }

                Collections.sort(arrayList0, new Comparator<File>() {
                    public int compare(File file0, File file1) {
                        return Long.valueOf(file1.lastModified()).compareTo(file0.lastModified());
                    }
                });
                int int1 = 1;

                for (int int2 = 0; int2 < arrayList0.size(); int2++) {
                    File file2 = (File)arrayList0.get(int2);
                    String string = getSaveName(file2);
                    Double double0 = (double)int1;
                    table.rawset(double0, string);
                    int1++;
                }

                return table;
            }
        }

        public static String getSaveName(File file) {
            String[] strings = file.getAbsolutePath().split("\\" + File.separator);
            return strings[strings.length - 2] + File.separator + file.getName();
        }

        @LuaMethod(
            name = "getSaveDirectoryTable",
            global = true
        )
        public static KahluaTable getSaveDirectoryTable() {
            return LuaManager.platform.newTable();
        }

        public static List<String> getMods() {
            ArrayList arrayList = new ArrayList();
            ZomboidFileSystem.instance.getAllModFolders(arrayList);
            return arrayList;
        }

        @LuaMethod(
            name = "doChallenge",
            global = true
        )
        public static void doChallenge(KahluaTable challenge) {
            Core.GameMode = challenge.rawget("gameMode").toString();
            Core.ChallengeID = challenge.rawget("id").toString();
            Core.bLastStand = Core.GameMode.equals("LastStand");
            Core.getInstance().setChallenge(true);
            getWorld().setMap(challenge.getString("world"));
            Integer integer = Rand.Next(100000000);
            IsoWorld.instance.setWorld(integer.toString());
            getWorld().bDoChunkMapUpdate = false;
        }

        @LuaMethod(
            name = "doTutorial",
            global = true
        )
        public static void doTutorial(KahluaTable tutorial) {
            Core.GameMode = "Tutorial";
            Core.bLastStand = false;
            Core.ChallengeID = null;
            Core.getInstance().setChallenge(false);
            Core.bTutorial = true;
            getWorld().setMap(tutorial.getString("world"));
            getWorld().bDoChunkMapUpdate = false;
        }

        @LuaMethod(
            name = "deleteAllGameModeSaves",
            global = true
        )
        public static void deleteAllGameModeSaves(String gameMode) {
            String string = Core.GameMode;
            Core.GameMode = gameMode;
            Path path = Paths.get(ZomboidFileSystem.instance.getGameModeCacheDir());
            if (!Files.exists(path)) {
                Core.GameMode = string;
            } else {
                try {
                    Files.walkFileTree(path, new FileVisitor<Path>() {
                        public FileVisitResult preVisitDirectory(Path var1, BasicFileAttributes var2) throws IOException {
                            return FileVisitResult.CONTINUE;
                        }

                        public FileVisitResult visitFile(Path path, BasicFileAttributes var2) throws IOException {
                            Files.delete(path);
                            return FileVisitResult.CONTINUE;
                        }

                        public FileVisitResult visitFileFailed(Path var1, IOException iOExceptionx) throws IOException {
                            iOExceptionx.printStackTrace();
                            return FileVisitResult.CONTINUE;
                        }

                        public FileVisitResult postVisitDirectory(Path path, IOException var2) throws IOException {
                            Files.delete(path);
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (IOException iOException) {
                    iOException.printStackTrace();
                }

                Core.GameMode = string;
            }
        }

        @LuaMethod(
            name = "sledgeDestroy",
            global = true
        )
        public static void sledgeDestroy(IsoObject object) {
            if (GameClient.bClient) {
                GameClient.destroy(object);
            }
        }

        @LuaMethod(
            name = "getTickets",
            global = true
        )
        public static void getTickets(String author) {
            if (GameClient.bClient) {
                GameClient.getTickets(author);
            }
        }

        @LuaMethod(
            name = "addTicket",
            global = true
        )
        public static void addTicket(String author, String message, int ticketID) {
            if (GameClient.bClient) {
                GameClient.addTicket(author, message, ticketID);
            }
        }

        @LuaMethod(
            name = "removeTicket",
            global = true
        )
        public static void removeTicket(int ticketID) {
            if (GameClient.bClient) {
                GameClient.removeTicket(ticketID);
            }
        }

        @LuaMethod(
            name = "sendFactionInvite",
            global = true
        )
        public static void sendFactionInvite(Faction faction, IsoPlayer host, String invited) {
            if (GameClient.bClient) {
                GameClient.sendFactionInvite(faction, host, invited);
            }
        }

        @LuaMethod(
            name = "acceptFactionInvite",
            global = true
        )
        public static void acceptFactionInvite(Faction faction, String host) {
            if (GameClient.bClient) {
                GameClient.acceptFactionInvite(faction, host);
            }
        }

        @LuaMethod(
            name = "sendSafehouseInvite",
            global = true
        )
        public static void sendSafehouseInvite(SafeHouse safehouse, IsoPlayer host, String invited) {
            if (GameClient.bClient) {
                GameClient.sendSafehouseInvite(safehouse, host, invited);
            }
        }

        @LuaMethod(
            name = "acceptSafehouseInvite",
            global = true
        )
        public static void acceptSafehouseInvite(SafeHouse safehouse, String host) {
            if (GameClient.bClient) {
                GameClient.acceptSafehouseInvite(safehouse, host);
            }
        }

        @LuaMethod(
            name = "createHordeFromTo",
            global = true
        )
        public static void createHordeFromTo(float spawnX, float spawnY, float targetX, float targetY, int count) {
            ZombiePopulationManager.instance.createHordeFromTo((int)spawnX, (int)spawnY, (int)targetX, (int)targetY, count);
        }

        @LuaMethod(
            name = "createHordeInAreaTo",
            global = true
        )
        public static void createHordeInAreaTo(int spawnX, int spawnY, int spawnW, int spawnH, int targetX, int targetY, int count) {
            ZombiePopulationManager.instance.createHordeInAreaTo(spawnX, spawnY, spawnW, spawnH, targetX, targetY, count);
        }

        @LuaMethod(
            name = "spawnHorde",
            global = true
        )
        public static void spawnHorde(float x, float y, float x2, float y2, float z, int count) {
            for (int int0 = 0; int0 < count; int0++) {
                VirtualZombieManager.instance.choices.clear();
                IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)Rand.Next(x, x2), (double)Rand.Next(y, y2), (double)z);
                if (square != null) {
                    VirtualZombieManager.instance.choices.add(square);
                    IsoZombie zombie0 = VirtualZombieManager.instance
                        .createRealZombieAlways(IsoDirections.fromIndex(Rand.Next(IsoDirections.Max.index())).index(), false);
                    zombie0.dressInRandomOutfit();
                    ZombieSpawnRecorder.instance.record(zombie0, "LuaManager.spawnHorde");
                }
            }
        }

        @LuaMethod(
            name = "createZombie",
            global = true
        )
        public static IsoZombie createZombie(float x, float y, float z, SurvivorDesc desc, int palette, IsoDirections dir) {
            VirtualZombieManager.instance.choices.clear();
            IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)x, (double)y, (double)z);
            VirtualZombieManager.instance.choices.add(square);
            IsoZombie zombie0 = VirtualZombieManager.instance.createRealZombieAlways(dir.index(), false);
            ZombieSpawnRecorder.instance.record(zombie0, "LuaManager.createZombie");
            return zombie0;
        }

        @LuaMethod(
            name = "triggerEvent",
            global = true
        )
        public static void triggerEvent(String event) {
            LuaEventManager.triggerEvent(event);
        }

        @LuaMethod(
            name = "triggerEvent",
            global = true
        )
        public static void triggerEvent(String event, Object param) {
            LuaEventManager.triggerEventGarbage(event, param);
        }

        @LuaMethod(
            name = "triggerEvent",
            global = true
        )
        public static void triggerEvent(String event, Object param, Object param2) {
            LuaEventManager.triggerEventGarbage(event, param, param2);
        }

        @LuaMethod(
            name = "triggerEvent",
            global = true
        )
        public static void triggerEvent(String event, Object param, Object param2, Object param3) {
            LuaEventManager.triggerEventGarbage(event, param, param2, param3);
        }

        @LuaMethod(
            name = "triggerEvent",
            global = true
        )
        public static void triggerEvent(String event, Object param, Object param2, Object param3, Object param4) {
            LuaEventManager.triggerEventGarbage(event, param, param2, param3, param4);
        }

        @LuaMethod(
            name = "debugLuaTable",
            global = true
        )
        public static void debugLuaTable(Object param, int depth) {
            if (depth <= 1) {
                if (param instanceof KahluaTable table) {
                    KahluaTableIterator kahluaTableIterator = table.iterator();
                    Object object0 = "";

                    for (int int0 = 0; int0 < depth; int0++) {
                        object0 = object0 + "\t";
                    }

                    do {
                        Object object1 = kahluaTableIterator.getKey();
                        Object object2 = kahluaTableIterator.getValue();
                        if (object1 != null) {
                            if (object2 != null) {
                                DebugLog.Lua.debugln(object0 + object1 + " : " + object2.toString());
                            }

                            if (object2 instanceof KahluaTable) {
                                debugLuaTable(object2, depth + 1);
                            }
                        }
                    } while (kahluaTableIterator.advance());

                    if (table.getMetatable() != null) {
                        debugLuaTable(table.getMetatable(), depth);
                    }
                }
            }
        }

        @LuaMethod(
            name = "debugLuaTable",
            global = true
        )
        public static void debugLuaTable(Object param) {
            debugLuaTable(param, 0);
        }

        @LuaMethod(
            name = "sendItemsInContainer",
            global = true
        )
        public static void sendItemsInContainer(IsoObject obj, ItemContainer container) {
            GameServer.sendItemsInContainer(obj, container == null ? obj.getContainer() : container);
        }

        @LuaMethod(
            name = "getModDirectoryTable",
            global = true
        )
        public static KahluaTable getModDirectoryTable() {
            KahluaTable table = LuaManager.platform.newTable();
            List list = getMods();
            int int0 = 1;

            for (int int1 = 0; int1 < list.size(); int1++) {
                String string = (String)list.get(int1);
                Double double0 = (double)int0;
                table.rawset(double0, string);
                int0++;
            }

            return table;
        }

        @LuaMethod(
            name = "getModInfoByID",
            global = true
        )
        public static ChooseGameInfo.Mod getModInfoByID(String modID) {
            try {
                return ChooseGameInfo.getModDetails(modID);
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }

        @LuaMethod(
            name = "getModInfo",
            global = true
        )
        public static ChooseGameInfo.Mod getModInfo(String modDir) {
            try {
                return ChooseGameInfo.readModInfo(modDir);
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
                return null;
            }
        }

        @LuaMethod(
            name = "getMapFoldersForMod",
            global = true
        )
        public static ArrayList<String> getMapFoldersForMod(String modID) {
            try {
                ChooseGameInfo.Mod mod = ChooseGameInfo.getModDetails(modID);
                if (mod == null) {
                    return null;
                } else {
                    String string = mod.getDir() + File.separator + "media" + File.separator + "maps";
                    File file = new File(string);
                    if (file.exists() && file.isDirectory()) {
                        ArrayList arrayList = null;

                        try (DirectoryStream directoryStream = Files.newDirectoryStream(file.toPath())) {
                            for (Path path : directoryStream) {
                                if (Files.isDirectory(path)) {
                                    file = new File(string + File.separator + path.getFileName().toString() + File.separator + "map.info");
                                    if (file.exists()) {
                                        if (arrayList == null) {
                                            arrayList = new ArrayList();
                                        }

                                        arrayList.add(path.getFileName().toString());
                                    }
                                }
                            }
                        }

                        return arrayList;
                    } else {
                        return null;
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }

        @LuaMethod(
            name = "spawnpointsExistsForMod",
            global = true
        )
        public static boolean spawnpointsExistsForMod(String modID, String mapFolder) {
            try {
                ChooseGameInfo.Mod mod = ChooseGameInfo.getModDetails(modID);
                if (mod == null) {
                    return false;
                } else {
                    String string = mod.getDir()
                        + File.separator
                        + "media"
                        + File.separator
                        + "maps"
                        + File.separator
                        + mapFolder
                        + File.separator
                        + "spawnpoints.lua";
                    return new File(string).exists();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                return false;
            }
        }

        /**
         * Returns the OS-defined file separator. It is not generally needed to use this, as most functions that expect a filepath string will parse them in an OS-independent way.
         * @return The file separator.
         */
        @LuaMethod(
            name = "getFileSeparator",
            global = true
        )
        public static String getFileSeparator() {
            return File.separator;
        }

        @LuaMethod(
            name = "getScriptManager",
            global = true
        )
        public static ScriptManager getScriptManager() {
            return ScriptManager.instance;
        }

        @LuaMethod(
            name = "checkSaveFolderExists",
            global = true
        )
        public static boolean checkSaveFolderExists(String f) {
            File file = new File(ZomboidFileSystem.instance.getSaveDir() + File.separator + f);
            return file.exists();
        }

        @LuaMethod(
            name = "getAbsoluteSaveFolderName",
            global = true
        )
        public static String getAbsoluteSaveFolderName(String f) {
            File file = new File(ZomboidFileSystem.instance.getSaveDir() + File.separator + f);
            return file.getAbsolutePath();
        }

        @LuaMethod(
            name = "checkSaveFileExists",
            global = true
        )
        public static boolean checkSaveFileExists(String f) {
            File file = new File(ZomboidFileSystem.instance.getFileNameInCurrentSave(f));
            return file.exists();
        }

        @LuaMethod(
            name = "checkSavePlayerExists",
            global = true
        )
        public static boolean checkSavePlayerExists() {
            if (!GameClient.bClient) {
                return PlayerDBHelper.isPlayerAlive(ZomboidFileSystem.instance.getCurrentSaveDir(), 1);
            } else {
                return ClientPlayerDB.getInstance() == null
                    ? false
                    : ClientPlayerDB.getInstance().clientLoadNetworkPlayer() && ClientPlayerDB.getInstance().isAliveMainNetworkPlayer();
            }
        }

        @LuaMethod(
            name = "fileExists",
            global = true
        )
        public static boolean fileExists(String filename) {
            String string = filename.replace("/", File.separator);
            string = string.replace("\\", File.separator);
            File file = new File(ZomboidFileSystem.instance.getString(string));
            return file.exists();
        }

        @LuaMethod(
            name = "serverFileExists",
            global = true
        )
        public static boolean serverFileExists(String filename) {
            String string = filename.replace("/", File.separator);
            string = string.replace("\\", File.separator);
            File file = new File(ZomboidFileSystem.instance.getCacheDir() + File.separator + "Server" + File.separator + string);
            return file.exists();
        }

        @LuaMethod(
            name = "takeScreenshot",
            global = true
        )
        public static void takeScreenshot() {
            Core.getInstance().TakeFullScreenshot(null);
        }

        @LuaMethod(
            name = "takeScreenshot",
            global = true
        )
        public static void takeScreenshot(String fileName) {
            Core.getInstance().TakeFullScreenshot(fileName);
        }

        @LuaMethod(
            name = "checkStringPattern",
            global = true
        )
        public static boolean checkStringPattern(String pattern) {
            return !pattern.contains("[");
        }

        @LuaMethod(
            name = "instanceItem",
            global = true
        )
        public static InventoryItem instanceItem(Item item) {
            return InventoryItemFactory.CreateItem(item.moduleDotType);
        }

        @LuaMethod(
            name = "instanceItem",
            global = true
        )
        public static InventoryItem instanceItem(String item) {
            return InventoryItemFactory.CreateItem(item);
        }

        @LuaMethod(
            name = "createNewScriptItem",
            global = true
        )
        public static Item createNewScriptItem(String base, String name, String display, String type, String icon) {
            Item item = new Item();
            item.module = ScriptManager.instance.getModule(base);
            item.module.ItemMap.put(name, item);
            item.Icon = "Item_" + icon;
            item.DisplayName = display;
            item.name = name;
            item.moduleDotType = item.module.name + "." + name;

            try {
                item.type = Item.Type.valueOf(type);
            } catch (Exception exception) {
            }

            return item;
        }

        @LuaMethod(
            name = "cloneItemType",
            global = true
        )
        public static Item cloneItemType(String newName, String oldName) {
            Item item0 = ScriptManager.instance.FindItem(oldName);
            Item item1 = new Item();
            item1.module = item0.getModule();
            item1.module.ItemMap.put(newName, item1);
            return item1;
        }

        @LuaMethod(
            name = "moduleDotType",
            global = true
        )
        public static String moduleDotType(String module, String type) {
            return StringUtils.moduleDotType(module, type);
        }

        @LuaMethod(
            name = "require",
            global = true
        )
        public static Object require(String f) {
            String string0 = f;
            if (!f.endsWith(".lua")) {
                string0 = f + ".lua";
            }

            for (int int0 = 0; int0 < LuaManager.paths.size(); int0++) {
                String string1 = LuaManager.paths.get(int0);
                String string2 = ZomboidFileSystem.instance.getAbsolutePath(string1 + string0);
                if (string2 != null) {
                    return LuaManager.RunLua(ZomboidFileSystem.instance.getString(string2));
                }
            }

            DebugLog.Lua.warn("require(\"" + f + "\") failed");
            return null;
        }

        @LuaMethod(
            name = "getRenderer",
            global = true
        )
        public static SpriteRenderer getRenderer() {
            return SpriteRenderer.instance;
        }

        @LuaMethod(
            name = "getGameTime",
            global = true
        )
        public static GameTime getGameTime() {
            return GameTime.instance;
        }

        @LuaMethod(
            name = "getMPStatistics",
            global = true
        )
        public static KahluaTable getStatistics() {
            return MPStatistics.getLuaStatistics();
        }

        @LuaMethod(
            name = "getMPStatus",
            global = true
        )
        public static KahluaTable getMPStatus() {
            return MPStatistics.getLuaStatus();
        }

        @LuaMethod(
            name = "getMaxPlayers",
            global = true
        )
        public static Double getMaxPlayers() {
            return (double)GameClient.connection.maxPlayers;
        }

        @LuaMethod(
            name = "getWorld",
            global = true
        )
        public static IsoWorld getWorld() {
            return IsoWorld.instance;
        }

        @LuaMethod(
            name = "getCell",
            global = true
        )
        public static IsoCell getCell() {
            return IsoWorld.instance.getCell();
        }

        @LuaMethod(
            name = "getSandboxOptions",
            global = true
        )
        public static SandboxOptions getSandboxOptions() {
            return SandboxOptions.instance;
        }

        /**
         * Gets an output stream for a file in the Lua cache.
         * 
         * @param filename Path, relative to the Lua cache root, to write to. '..' is not allowed.
         * @return The output stream, or null if the path was not valid.
         */
        @LuaMethod(
            name = "getFileOutput",
            global = true
        )
        public static DataOutputStream getFileOutput(String filename) {
            if (StringUtils.containsDoubleDot(filename)) {
                DebugLog.Lua.warn("relative paths not allowed");
                return null;
            } else {
                String string0 = LuaManager.getLuaCacheDir() + File.separator + filename;
                string0 = string0.replace("/", File.separator);
                string0 = string0.replace("\\", File.separator);
                String string1 = string0.substring(0, string0.lastIndexOf(File.separator));
                string1 = string1.replace("\\", "/");
                File file0 = new File(string1);
                if (!file0.exists()) {
                    file0.mkdirs();
                }

                File file1 = new File(string0);

                try {
                    outStream = new FileOutputStream(file1);
                } catch (FileNotFoundException fileNotFoundException) {
                    Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, fileNotFoundException);
                }

                return new DataOutputStream(outStream);
            }
        }

        @LuaMethod(
            name = "getLastStandPlayersDirectory",
            global = true
        )
        public static String getLastStandPlayersDirectory() {
            return "LastStand";
        }

        @LuaMethod(
            name = "getLastStandPlayerFileNames",
            global = true
        )
        public static List<String> getLastStandPlayerFileNames() throws IOException {
            ArrayList arrayList = new ArrayList();
            String string = LuaManager.getLuaCacheDir() + File.separator + getLastStandPlayersDirectory();
            string = string.replace("/", File.separator);
            string = string.replace("\\", File.separator);
            File file0 = new File(string);
            if (!file0.exists()) {
                file0.mkdir();
            }

            for (File file1 : file0.listFiles()) {
                if (!file1.isDirectory() && file1.getName().endsWith(".txt")) {
                    arrayList.add(getLastStandPlayersDirectory() + File.separator + file1.getName());
                }
            }

            return arrayList;
        }

        @Deprecated
        @LuaMethod(
            name = "getAllSavedPlayers",
            global = true
        )
        public static List<BufferedReader> getAllSavedPlayers() throws IOException {
            ArrayList arrayList = new ArrayList();
            String string = LuaManager.getLuaCacheDir() + File.separator + getLastStandPlayersDirectory();
            string = string.replace("/", File.separator);
            string = string.replace("\\", File.separator);
            File file0 = new File(string);
            if (!file0.exists()) {
                file0.mkdir();
            }

            for (File file1 : file0.listFiles()) {
                arrayList.add(new BufferedReader(new FileReader(file1)));
            }

            return arrayList;
        }

        @LuaMethod(
            name = "getSandboxPresets",
            global = true
        )
        public static List<String> getSandboxPresets() throws IOException {
            ArrayList arrayList = new ArrayList();
            String string = LuaManager.getSandboxCacheDir();
            File file0 = new File(string);
            if (!file0.exists()) {
                file0.mkdir();
            }

            for (File file1 : file0.listFiles()) {
                if (file1.getName().endsWith(".cfg")) {
                    arrayList.add(file1.getName().replace(".cfg", ""));
                }
            }

            Collections.sort(arrayList);
            return arrayList;
        }

        @LuaMethod(
            name = "deleteSandboxPreset",
            global = true
        )
        public static void deleteSandboxPreset(String name) {
            if (StringUtils.containsDoubleDot(name)) {
                DebugLog.Lua.warn("relative paths not allowed");
            } else {
                String string = LuaManager.getSandboxCacheDir() + File.separator + name + ".cfg";
                File file = new File(string);
                if (file.exists()) {
                    file.delete();
                }
            }
        }

        /**
         * Gets a file reader for a file in the Lua cache.
         * 
         * @param filename Path, relative to the Lua cache root, to read from. '..' is not allowed.
         * @param createIfNull Whether to create the file if it does not exist. The created file will be empty.
         * @return The file reader, or null if the path was not valid.
         */
        @LuaMethod(
            name = "getFileReader",
            global = true
        )
        public static BufferedReader getFileReader(String filename, boolean createIfNull) throws IOException {
            if (StringUtils.containsDoubleDot(filename)) {
                DebugLog.Lua.warn("relative paths not allowed");
                return null;
            } else {
                String string = LuaManager.getLuaCacheDir() + File.separator + filename;
                string = string.replace("/", File.separator);
                string = string.replace("\\", File.separator);
                File file = new File(string);
                if (!file.exists() && createIfNull) {
                    file.createNewFile();
                }

                if (file.exists()) {
                    BufferedReader bufferedReader = null;

                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                        bufferedReader = new BufferedReader(inputStreamReader);
                    } catch (IOException iOException) {
                        Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, iOException);
                    }

                    return bufferedReader;
                } else {
                    return null;
                }
            }
        }

        /**
         * Gets a file reader for a file in a mod's directory.
         * 
         * @param modId ID of the target mod. If null, the path will be relative to the local mods directory.
         * @param filename Path, relative to the mod's common folder, to read from. '..' is not allowed.
         * @param createIfNull Whether to create the file if it does not exist. The created file will be empty.
         * @return The file reader, or null if the path or mod was not valid.
         */
        @LuaMethod(
            name = "getModFileReader",
            global = true
        )
        public static BufferedReader getModFileReader(String modId, String filename, boolean createIfNull) throws IOException {
            if (!filename.isEmpty() && !StringUtils.containsDoubleDot(filename) && !new File(filename).isAbsolute()) {
                String string0 = ZomboidFileSystem.instance.getCacheDir() + File.separator + "mods" + File.separator + filename;
                if (modId != null) {
                    ChooseGameInfo.Mod mod = ChooseGameInfo.getModDetails(modId);
                    if (mod == null) {
                        return null;
                    }

                    string0 = mod.getDir() + File.separator + filename;
                }

                string0 = string0.replace("/", File.separator);
                string0 = string0.replace("\\", File.separator);
                File file0 = new File(string0);
                if (!file0.exists() && createIfNull) {
                    String string1 = string0.substring(0, string0.lastIndexOf(File.separator));
                    File file1 = new File(string1);
                    if (!file1.exists()) {
                        file1.mkdirs();
                    }

                    file0.createNewFile();
                }

                if (file0.exists()) {
                    BufferedReader bufferedReader = null;

                    try {
                        FileInputStream fileInputStream = new FileInputStream(file0);
                        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                        bufferedReader = new BufferedReader(inputStreamReader);
                    } catch (IOException iOException) {
                        Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, iOException);
                    }

                    return bufferedReader;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        public static void refreshAnimSets(boolean reload) {
            try {
                if (reload) {
                    AnimationSet.Reset();

                    for (Asset asset : AnimNodeAssetManager.instance.getAssetTable().values()) {
                        AnimNodeAssetManager.instance.reload(asset);
                    }
                }

                AnimationSet.GetAnimationSet("player", true);
                AnimationSet.GetAnimationSet("player-vehicle", true);
                AnimationSet.GetAnimationSet("zombie", true);
                AnimationSet.GetAnimationSet("zombie-crawler", true);

                for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                    IsoPlayer player = IsoPlayer.players[int0];
                    if (player != null) {
                        player.advancedAnimator.OnAnimDataChanged(reload);
                    }
                }

                for (IsoZombie zombie0 : IsoWorld.instance.CurrentCell.getZombieList()) {
                    zombie0.advancedAnimator.OnAnimDataChanged(reload);
                }
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
            }
        }

        public static void reloadActionGroups() {
            try {
                ActionGroup.reloadAll();
            } catch (Exception exception) {
            }
        }

        /**
         * Gets a file writer for a file in a mod's directory. Note: it is generally unwise to write to a mod's lua or scripts directories, as this will change the checksum.
         * 
         * @param modId ID of the target mod. If null, the path will be relative to the local mods directory.
         * @param filename Path, relative to the mod's common folder, to write to. '..' is not allowed.
         * @param createIfNull Whether to create the file if it does not exist. The created file will be empty.
         * @param append Whether to open the file in append mode. If true, the writer will write after the file's current contents. If false, the current contents of the file will be erased.
         * @return The file writer, or null if the path or mod was not valid.
         */
        @LuaMethod(
            name = "getModFileWriter",
            global = true
        )
        public static LuaManager.GlobalObject.LuaFileWriter getModFileWriter(String modId, String filename, boolean createIfNull, boolean append) {
            if (!filename.isEmpty() && !StringUtils.containsDoubleDot(filename) && !new File(filename).isAbsolute()) {
                ChooseGameInfo.Mod mod = ChooseGameInfo.getModDetails(modId);
                if (mod == null) {
                    return null;
                } else {
                    String string0 = mod.getDir() + File.separator + filename;
                    string0 = string0.replace("/", File.separator);
                    string0 = string0.replace("\\", File.separator);
                    String string1 = string0.substring(0, string0.lastIndexOf(File.separator));
                    File file0 = new File(string1);
                    if (!file0.exists()) {
                        file0.mkdirs();
                    }

                    File file1 = new File(string0);
                    if (!file1.exists() && createIfNull) {
                        try {
                            file1.createNewFile();
                        } catch (IOException iOException0) {
                            Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, iOException0);
                        }
                    }

                    PrintWriter printWriter = null;

                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(file1, append);
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
                        printWriter = new PrintWriter(outputStreamWriter);
                    } catch (IOException iOException1) {
                        Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, iOException1);
                    }

                    return new LuaManager.GlobalObject.LuaFileWriter(printWriter);
                }
            } else {
                return null;
            }
        }

        @LuaMethod(
            name = "updateFire",
            global = true
        )
        public static void updateFire() {
            IsoFireManager.Update();
        }

        @LuaMethod(
            name = "deletePlayerSave",
            global = true
        )
        public static void deletePlayerSave(String fileName) {
            if (StringUtils.containsDoubleDot(fileName)) {
                DebugLog.Lua.warn("relative paths not allowed");
            } else {
                String string = LuaManager.getLuaCacheDir() + File.separator + "Players" + File.separator + "player" + fileName + ".txt";
                string = string.replace("/", File.separator);
                string = string.replace("\\", File.separator);
                File file = new File(string);
                file.delete();
            }
        }

        @LuaMethod(
            name = "getControllerCount",
            global = true
        )
        public static int getControllerCount() {
            return GameWindow.GameInput.getControllerCount();
        }

        @LuaMethod(
            name = "isControllerConnected",
            global = true
        )
        public static boolean isControllerConnected(int index) {
            return index >= 0 && index <= GameWindow.GameInput.getControllerCount() ? GameWindow.GameInput.getController(index) != null : false;
        }

        @LuaMethod(
            name = "getControllerGUID",
            global = true
        )
        public static String getControllerGUID(int joypad) {
            if (joypad >= 0 && joypad < GameWindow.GameInput.getControllerCount()) {
                Controller controller = GameWindow.GameInput.getController(joypad);
                return controller != null ? controller.getGUID() : "???";
            } else {
                return "???";
            }
        }

        @LuaMethod(
            name = "getControllerName",
            global = true
        )
        public static String getControllerName(int joypad) {
            if (joypad >= 0 && joypad < GameWindow.GameInput.getControllerCount()) {
                Controller controller = GameWindow.GameInput.getController(joypad);
                return controller != null ? controller.getGamepadName() : "???";
            } else {
                return "???";
            }
        }

        @LuaMethod(
            name = "getControllerAxisCount",
            global = true
        )
        public static int getControllerAxisCount(int c) {
            if (c >= 0 && c < GameWindow.GameInput.getControllerCount()) {
                Controller controller = GameWindow.GameInput.getController(c);
                return controller == null ? 0 : controller.getAxisCount();
            } else {
                return 0;
            }
        }

        @LuaMethod(
            name = "getControllerAxisValue",
            global = true
        )
        public static float getControllerAxisValue(int c, int axis) {
            if (c >= 0 && c < GameWindow.GameInput.getControllerCount()) {
                Controller controller = GameWindow.GameInput.getController(c);
                if (controller == null) {
                    return 0.0F;
                } else {
                    return axis >= 0 && axis < controller.getAxisCount() ? controller.getAxisValue(axis) : 0.0F;
                }
            } else {
                return 0.0F;
            }
        }

        @LuaMethod(
            name = "getControllerDeadZone",
            global = true
        )
        public static float getControllerDeadZone(int c, int axis) {
            if (c < 0 || c >= GameWindow.GameInput.getControllerCount()) {
                return 0.0F;
            } else {
                return axis >= 0 && axis < GameWindow.GameInput.getAxisCount(c) ? JoypadManager.instance.getDeadZone(c, axis) : 0.0F;
            }
        }

        @LuaMethod(
            name = "setControllerDeadZone",
            global = true
        )
        public static void setControllerDeadZone(int c, int axis, float value) {
            if (c >= 0 && c < GameWindow.GameInput.getControllerCount()) {
                if (axis >= 0 && axis < GameWindow.GameInput.getAxisCount(c)) {
                    JoypadManager.instance.setDeadZone(c, axis, value);
                }
            }
        }

        @LuaMethod(
            name = "saveControllerSettings",
            global = true
        )
        public static void saveControllerSettings(int c) {
            if (c >= 0 && c < GameWindow.GameInput.getControllerCount()) {
                JoypadManager.instance.saveControllerSettings(c);
            }
        }

        @LuaMethod(
            name = "getControllerButtonCount",
            global = true
        )
        public static int getControllerButtonCount(int c) {
            if (c >= 0 && c < GameWindow.GameInput.getControllerCount()) {
                Controller controller = GameWindow.GameInput.getController(c);
                return controller == null ? 0 : controller.getButtonCount();
            } else {
                return 0;
            }
        }

        @LuaMethod(
            name = "getControllerPovX",
            global = true
        )
        public static float getControllerPovX(int c) {
            if (c >= 0 && c < GameWindow.GameInput.getControllerCount()) {
                Controller controller = GameWindow.GameInput.getController(c);
                return controller == null ? 0.0F : controller.getPovX();
            } else {
                return 0.0F;
            }
        }

        @LuaMethod(
            name = "getControllerPovY",
            global = true
        )
        public static float getControllerPovY(int c) {
            if (c >= 0 && c < GameWindow.GameInput.getControllerCount()) {
                Controller controller = GameWindow.GameInput.getController(c);
                return controller == null ? 0.0F : controller.getPovY();
            } else {
                return 0.0F;
            }
        }

        @LuaMethod(
            name = "reloadControllerConfigFiles",
            global = true
        )
        public static void reloadControllerConfigFiles() {
            JoypadManager.instance.reloadControllerFiles();
        }

        @LuaMethod(
            name = "isJoypadPressed",
            global = true
        )
        public static boolean isJoypadPressed(int joypad, int button) {
            return GameWindow.GameInput.isButtonPressedD(button, joypad);
        }

        @LuaMethod(
            name = "isJoypadDown",
            global = true
        )
        public static boolean isJoypadDown(int joypad) {
            return JoypadManager.instance.isDownPressed(joypad);
        }

        @LuaMethod(
            name = "isJoypadLTPressed",
            global = true
        )
        public static boolean isJoypadLTPressed(int joypad) {
            return JoypadManager.instance.isLTPressed(joypad);
        }

        @LuaMethod(
            name = "isJoypadRTPressed",
            global = true
        )
        public static boolean isJoypadRTPressed(int joypad) {
            return JoypadManager.instance.isRTPressed(joypad);
        }

        @LuaMethod(
            name = "isJoypadLeftStickButtonPressed",
            global = true
        )
        public static boolean isJoypadLeftStickButtonPressed(int joypad) {
            return JoypadManager.instance.isL3Pressed(joypad);
        }

        @LuaMethod(
            name = "isJoypadRightStickButtonPressed",
            global = true
        )
        public static boolean isJoypadRightStickButtonPressed(int joypad) {
            return JoypadManager.instance.isR3Pressed(joypad);
        }

        @LuaMethod(
            name = "getJoypadAimingAxisX",
            global = true
        )
        public static float getJoypadAimingAxisX(int joypad) {
            return JoypadManager.instance.getAimingAxisX(joypad);
        }

        @LuaMethod(
            name = "getJoypadAimingAxisY",
            global = true
        )
        public static float getJoypadAimingAxisY(int joypad) {
            return JoypadManager.instance.getAimingAxisY(joypad);
        }

        @LuaMethod(
            name = "getJoypadMovementAxisX",
            global = true
        )
        public static float getJoypadMovementAxisX(int joypad) {
            return JoypadManager.instance.getMovementAxisX(joypad);
        }

        @LuaMethod(
            name = "getJoypadMovementAxisY",
            global = true
        )
        public static float getJoypadMovementAxisY(int joypad) {
            return JoypadManager.instance.getMovementAxisY(joypad);
        }

        @LuaMethod(
            name = "getJoypadAButton",
            global = true
        )
        public static int getJoypadAButton(int joypad) {
            JoypadManager.Joypad _joypad = JoypadManager.instance.getFromControllerID(joypad);
            return _joypad != null ? _joypad.getAButton() : -1;
        }

        @LuaMethod(
            name = "getJoypadBButton",
            global = true
        )
        public static int getJoypadBButton(int joypad) {
            JoypadManager.Joypad _joypad = JoypadManager.instance.getFromControllerID(joypad);
            return _joypad != null ? _joypad.getBButton() : -1;
        }

        @LuaMethod(
            name = "getJoypadXButton",
            global = true
        )
        public static int getJoypadXButton(int joypad) {
            JoypadManager.Joypad _joypad = JoypadManager.instance.getFromControllerID(joypad);
            return _joypad != null ? _joypad.getXButton() : -1;
        }

        @LuaMethod(
            name = "getJoypadYButton",
            global = true
        )
        public static int getJoypadYButton(int joypad) {
            JoypadManager.Joypad _joypad = JoypadManager.instance.getFromControllerID(joypad);
            return _joypad != null ? _joypad.getYButton() : -1;
        }

        @LuaMethod(
            name = "getJoypadLBumper",
            global = true
        )
        public static int getJoypadLBumper(int joypad) {
            JoypadManager.Joypad _joypad = JoypadManager.instance.getFromControllerID(joypad);
            return _joypad != null ? _joypad.getLBumper() : -1;
        }

        @LuaMethod(
            name = "getJoypadRBumper",
            global = true
        )
        public static int getJoypadRBumper(int joypad) {
            JoypadManager.Joypad _joypad = JoypadManager.instance.getFromControllerID(joypad);
            return _joypad != null ? _joypad.getRBumper() : -1;
        }

        @LuaMethod(
            name = "getJoypadBackButton",
            global = true
        )
        public static int getJoypadBackButton(int joypad) {
            JoypadManager.Joypad _joypad = JoypadManager.instance.getFromControllerID(joypad);
            return _joypad != null ? _joypad.getBackButton() : -1;
        }

        @LuaMethod(
            name = "getJoypadStartButton",
            global = true
        )
        public static int getJoypadStartButton(int joypad) {
            JoypadManager.Joypad _joypad = JoypadManager.instance.getFromControllerID(joypad);
            return _joypad != null ? _joypad.getStartButton() : -1;
        }

        @LuaMethod(
            name = "getJoypadLeftStickButton",
            global = true
        )
        public static int getJoypadLeftStickButton(int joypad) {
            JoypadManager.Joypad _joypad = JoypadManager.instance.getFromControllerID(joypad);
            return _joypad != null ? _joypad.getL3() : -1;
        }

        @LuaMethod(
            name = "getJoypadRightStickButton",
            global = true
        )
        public static int getJoypadRightStickButton(int joypad) {
            JoypadManager.Joypad _joypad = JoypadManager.instance.getFromControllerID(joypad);
            return _joypad != null ? _joypad.getR3() : -1;
        }

        @LuaMethod(
            name = "wasMouseActiveMoreRecentlyThanJoypad",
            global = true
        )
        public static boolean wasMouseActiveMoreRecentlyThanJoypad() {
            if (IsoPlayer.players[0] == null) {
                JoypadManager.Joypad joypad = GameWindow.ActivatedJoyPad;
                return joypad != null && !joypad.isDisabled() ? JoypadManager.instance.getLastActivity(joypad.getID()) < Mouse.lastActivity : true;
            } else {
                int int0 = IsoPlayer.players[0].getJoypadBind();
                return int0 == -1 ? true : JoypadManager.instance.getLastActivity(int0) < Mouse.lastActivity;
            }
        }

        @LuaMethod(
            name = "activateJoypadOnSteamDeck",
            global = true
        )
        public static void activateJoypadOnSteamDeck() {
            if (GameWindow.ActivatedJoyPad == null) {
                JoypadManager.instance.isAPressed(0);
                if (JoypadManager.instance.JoypadList.isEmpty()) {
                    return;
                }

                JoypadManager.Joypad joypad = JoypadManager.instance.JoypadList.get(0);
                GameWindow.ActivatedJoyPad = joypad;
            }

            if (IsoPlayer.getInstance() != null) {
                LuaEventManager.triggerEvent("OnJoypadActivate", GameWindow.ActivatedJoyPad.getID());
            } else {
                LuaEventManager.triggerEvent("OnJoypadActivateUI", GameWindow.ActivatedJoyPad.getID());
            }
        }

        @LuaMethod(
            name = "reactivateJoypadAfterResetLua",
            global = true
        )
        public static boolean reactivateJoypadAfterResetLua() {
            if (GameWindow.ActivatedJoyPad != null) {
                LuaEventManager.triggerEvent("OnJoypadActivateUI", GameWindow.ActivatedJoyPad.getID());
                return true;
            } else {
                return false;
            }
        }

        @LuaMethod(
            name = "isJoypadConnected",
            global = true
        )
        public static boolean isJoypadConnected(int index) {
            return JoypadManager.instance.isJoypadConnected(index);
        }

        private static void addPlayerToWorld(int int0, IsoPlayer player, boolean boolean0) {
            if (IsoPlayer.players[int0] != null) {
                IsoPlayer.players[int0].getEmitter().stopAll();
                IsoPlayer.players[int0].getEmitter().unregister();
                IsoPlayer.players[int0].updateUsername();
                IsoPlayer.players[int0].setSceneCulled(true);
                IsoPlayer.players[int0] = null;
            }

            player.PlayerIndex = int0;
            if (GameClient.bClient && int0 != 0 && player.serverPlayerIndex != 1) {
                ClientPlayerDB.getInstance().forgetPlayer(player.serverPlayerIndex);
            }

            if (GameClient.bClient && int0 != 0 && player.serverPlayerIndex == 1) {
                player.serverPlayerIndex = ClientPlayerDB.getInstance().getNextServerPlayerIndex();
            }

            if (int0 == 0) {
                player.sqlID = 1;
            }

            if (boolean0) {
                player.applyTraits(IsoWorld.instance.getLuaTraits());
                player.createKeyRing();
                ProfessionFactory.Profession profession = ProfessionFactory.getProfession(player.getDescriptor().getProfession());
                if (profession != null && !profession.getFreeRecipes().isEmpty()) {
                    for (String string0 : profession.getFreeRecipes()) {
                        player.getKnownRecipes().add(string0);
                    }
                }

                for (String string1 : IsoWorld.instance.getLuaTraits()) {
                    TraitFactory.Trait trait = TraitFactory.getTrait(string1);
                    if (trait != null && !trait.getFreeRecipes().isEmpty()) {
                        for (String string2 : trait.getFreeRecipes()) {
                            player.getKnownRecipes().add(string2);
                        }
                    }
                }

                player.setDir(IsoDirections.SE);
                LuaEventManager.triggerEvent("OnNewGame", player, player.getCurrentSquare());
            }

            IsoPlayer.numPlayers = Math.max(IsoPlayer.numPlayers, int0 + 1);
            IsoWorld.instance.AddCoopPlayers.add(new AddCoopPlayer(player));
            if (int0 == 0) {
                IsoPlayer.setInstance(player);
            }
        }

        @LuaMethod(
            name = "toInt",
            global = true
        )
        public static int toInt(double val) {
            return (int)val;
        }

        @LuaMethod(
            name = "getClientUsername",
            global = true
        )
        public static String getClientUsername() {
            return GameClient.bClient ? GameClient.username : null;
        }

        @LuaMethod(
            name = "setPlayerJoypad",
            global = true
        )
        public static void setPlayerJoypad(int player, int joypad, IsoPlayer playerObj, String username) {
            if (IsoPlayer.players[player] == null || IsoPlayer.players[player].isDead()) {
                boolean boolean0 = playerObj == null;
                if (playerObj == null) {
                    IsoPlayer _player = IsoPlayer.getInstance();
                    IsoWorld world = IsoWorld.instance;
                    int int0 = world.getLuaPosX() + 300 * world.getLuaSpawnCellX();
                    int int1 = world.getLuaPosY() + 300 * world.getLuaSpawnCellY();
                    int int2 = world.getLuaPosZ();
                    DebugLog.Lua.debugln("coop player spawning at " + int0 + "," + int1 + "," + int2);
                    playerObj = new IsoPlayer(world.CurrentCell, world.getLuaPlayerDesc(), int0, int1, int2);
                    IsoPlayer.setInstance(_player);
                    world.CurrentCell.getAddList().remove(playerObj);
                    world.CurrentCell.getObjectList().remove(playerObj);
                    playerObj.SaveFileName = IsoPlayer.getUniqueFileName();
                }

                if (GameClient.bClient) {
                    if (username != null) {
                        assert player != 0;

                        playerObj.username = username;
                        playerObj.getModData().rawset("username", username);
                    } else {
                        assert player == 0;

                        playerObj.username = GameClient.username;
                    }
                }

                addPlayerToWorld(player, playerObj, boolean0);
            }

            playerObj.JoypadBind = joypad;
            JoypadManager.instance.assignJoypad(joypad, player);
        }

        @LuaMethod(
            name = "setPlayerMouse",
            global = true
        )
        public static void setPlayerMouse(IsoPlayer playerObj) {
            byte byte0 = 0;
            boolean boolean0 = playerObj == null;
            if (playerObj == null) {
                IsoPlayer player = IsoPlayer.getInstance();
                IsoWorld world = IsoWorld.instance;
                int int0 = world.getLuaPosX() + 300 * world.getLuaSpawnCellX();
                int int1 = world.getLuaPosY() + 300 * world.getLuaSpawnCellY();
                int int2 = world.getLuaPosZ();
                DebugLog.Lua.debugln("coop player spawning at " + int0 + "," + int1 + "," + int2);
                playerObj = new IsoPlayer(world.CurrentCell, world.getLuaPlayerDesc(), int0, int1, int2);
                IsoPlayer.setInstance(player);
                world.CurrentCell.getAddList().remove(playerObj);
                world.CurrentCell.getObjectList().remove(playerObj);
                playerObj.SaveFileName = null;
            }

            if (GameClient.bClient) {
                playerObj.username = GameClient.username;
            }

            addPlayerToWorld(byte0, playerObj, boolean0);
        }

        @LuaMethod(
            name = "revertToKeyboardAndMouse",
            global = true
        )
        public static void revertToKeyboardAndMouse() {
            JoypadManager.instance.revertToKeyboardAndMouse();
        }

        @LuaMethod(
            name = "isJoypadUp",
            global = true
        )
        public static boolean isJoypadUp(int joypad) {
            return JoypadManager.instance.isUpPressed(joypad);
        }

        @LuaMethod(
            name = "isJoypadLeft",
            global = true
        )
        public static boolean isJoypadLeft(int joypad) {
            return JoypadManager.instance.isLeftPressed(joypad);
        }

        @LuaMethod(
            name = "isJoypadRight",
            global = true
        )
        public static boolean isJoypadRight(int joypad) {
            return JoypadManager.instance.isRightPressed(joypad);
        }

        @LuaMethod(
            name = "isJoypadLBPressed",
            global = true
        )
        public static boolean isJoypadLBPressed(int joypad) {
            return JoypadManager.instance.isLBPressed(joypad);
        }

        @LuaMethod(
            name = "isJoypadRBPressed",
            global = true
        )
        public static boolean isJoypadRBPressed(int joypad) {
            return JoypadManager.instance.isRBPressed(joypad);
        }

        @LuaMethod(
            name = "getButtonCount",
            global = true
        )
        public static int getButtonCount(int joypad) {
            if (joypad >= 0 && joypad < GameWindow.GameInput.getControllerCount()) {
                Controller controller = GameWindow.GameInput.getController(joypad);
                return controller == null ? 0 : controller.getButtonCount();
            } else {
                return 0;
            }
        }

        @LuaMethod(
            name = "setDebugToggleControllerPluggedIn",
            global = true
        )
        public static void setDebugToggleControllerPluggedIn(int index) {
            Controllers.setDebugToggleControllerPluggedIn(index);
        }

        /**
         * Gets a file writer for a file in the Lua cache.
         * 
         * @param filename Path, relative to the Lua cache root, to write to. '..' is not allowed.
         * @param createIfNull Whether to create the file if it does not exist.
         * @param append Whether to open the file in append mode. If true, the writer will write after the file's current contents. If false, the current contents of the file will be erased.
         * @return The file writer, or null if the path was not valid.
         */
        @LuaMethod(
            name = "getFileWriter",
            global = true
        )
        public static LuaManager.GlobalObject.LuaFileWriter getFileWriter(String filename, boolean createIfNull, boolean append) {
            if (StringUtils.containsDoubleDot(filename)) {
                DebugLog.Lua.warn("relative paths not allowed");
                return null;
            } else {
                String string0 = LuaManager.getLuaCacheDir() + File.separator + filename;
                string0 = string0.replace("/", File.separator);
                string0 = string0.replace("\\", File.separator);
                String string1 = string0.substring(0, string0.lastIndexOf(File.separator));
                string1 = string1.replace("\\", "/");
                File file0 = new File(string1);
                if (!file0.exists()) {
                    file0.mkdirs();
                }

                File file1 = new File(string0);
                if (!file1.exists() && createIfNull) {
                    try {
                        file1.createNewFile();
                    } catch (IOException iOException0) {
                        Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, iOException0);
                    }
                }

                PrintWriter printWriter = null;

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file1, append);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
                    printWriter = new PrintWriter(outputStreamWriter);
                } catch (IOException iOException1) {
                    Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, iOException1);
                }

                return new LuaManager.GlobalObject.LuaFileWriter(printWriter);
            }
        }

        @LuaMethod(
            name = "getSandboxFileWriter",
            global = true
        )
        public static LuaManager.GlobalObject.LuaFileWriter getSandboxFileWriter(String filename, boolean createIfNull, boolean append) {
            if (StringUtils.containsDoubleDot(filename)) {
                DebugLog.Lua.warn("relative paths not allowed");
                return null;
            } else {
                String string0 = LuaManager.getSandboxCacheDir() + File.separator + filename;
                string0 = string0.replace("/", File.separator);
                string0 = string0.replace("\\", File.separator);
                String string1 = string0.substring(0, string0.lastIndexOf(File.separator));
                string1 = string1.replace("\\", "/");
                File file0 = new File(string1);
                if (!file0.exists()) {
                    file0.mkdirs();
                }

                File file1 = new File(string0);
                if (!file1.exists() && createIfNull) {
                    try {
                        file1.createNewFile();
                    } catch (IOException iOException0) {
                        Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, iOException0);
                    }
                }

                PrintWriter printWriter = null;

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file1, append);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
                    printWriter = new PrintWriter(outputStreamWriter);
                } catch (IOException iOException1) {
                    Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, iOException1);
                }

                return new LuaManager.GlobalObject.LuaFileWriter(printWriter);
            }
        }

        @LuaMethod(
            name = "createStory",
            global = true
        )
        public static void createStory(String storyName) {
            Core.GameMode = storyName;
            String string = ZomboidFileSystem.instance.getGameModeCacheDir();
            string = string.replace("/", File.separator);
            string = string.replace("\\", File.separator);
            int int0 = 1;
            Object object = null;
            boolean boolean0 = false;

            while (!boolean0) {
                object = new File(string + File.separator + "Game" + int0);
                if (!object.exists()) {
                    boolean0 = true;
                } else {
                    int0++;
                }
            }

            Core.GameSaveWorld = "newstory";
        }

        @LuaMethod(
            name = "createWorld",
            global = true
        )
        public static void createWorld(String worldName) {
            if (worldName == null || worldName.isEmpty()) {
                worldName = "blah";
            }

            worldName = sanitizeWorldName(worldName);
            String string0 = ZomboidFileSystem.instance.getGameModeCacheDir() + File.separator + worldName + File.separator;
            string0 = string0.replace("/", File.separator);
            string0 = string0.replace("\\", File.separator);
            String string1 = string0.substring(0, string0.lastIndexOf(File.separator));
            string1 = string1.replace("\\", "/");
            File file = new File(string1);
            if (!file.exists() && !Core.getInstance().isNoSave()) {
                file.mkdirs();
            }

            Core.GameSaveWorld = worldName;
        }

        @LuaMethod(
            name = "sanitizeWorldName",
            global = true
        )
        public static String sanitizeWorldName(String worldName) {
            return worldName.replace(" ", "_")
                .replace("/", "")
                .replace("\\", "")
                .replace("?", "")
                .replace("*", "")
                .replace("<", "")
                .replace(">", "")
                .replace(":", "")
                .replace("|", "")
                .trim();
        }

        @LuaMethod(
            name = "forceChangeState",
            global = true
        )
        public static void forceChangeState(GameState state) {
            GameWindow.states.forceNextState(state);
        }

        @LuaMethod(
            name = "endFileOutput",
            global = true
        )
        public static void endFileOutput() {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException iOException) {
                    Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, iOException);
                }
            }

            outStream = null;
        }

        /**
         * Gets an input stream for a file in the Lua cache.
         * 
         * @param filename Path, relative to the Lua cache root, to write to. '..' is not allowed.
         * @return The input stream, or null if the path was not valid.
         */
        @LuaMethod(
            name = "getFileInput",
            global = true
        )
        public static DataInputStream getFileInput(String filename) throws IOException {
            if (StringUtils.containsDoubleDot(filename)) {
                DebugLog.Lua.warn("relative paths not allowed");
                return null;
            } else {
                String string = LuaManager.getLuaCacheDir() + File.separator + filename;
                string = string.replace("/", File.separator);
                string = string.replace("\\", File.separator);
                File file = new File(string);
                if (file.exists()) {
                    try {
                        inStream = new FileInputStream(file);
                    } catch (FileNotFoundException fileNotFoundException) {
                        Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, fileNotFoundException);
                    }

                    return new DataInputStream(inStream);
                } else {
                    return null;
                }
            }
        }

        @LuaMethod(
            name = "getGameFilesInput",
            global = true
        )
        public static DataInputStream getGameFilesInput(String filename) {
            String string = filename.replace("/", File.separator);
            string = string.replace("\\", File.separator);
            if (!ZomboidFileSystem.instance.isKnownFile(string)) {
                return null;
            } else {
                File file = new File(ZomboidFileSystem.instance.getString(string));
                if (file.exists()) {
                    try {
                        inStream = new FileInputStream(file);
                    } catch (FileNotFoundException fileNotFoundException) {
                        Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, fileNotFoundException);
                    }

                    return new DataInputStream(inStream);
                } else {
                    return null;
                }
            }
        }

        @LuaMethod(
            name = "getGameFilesTextInput",
            global = true
        )
        public static BufferedReader getGameFilesTextInput(String filename) {
            if (!Core.getInstance().getDebug()) {
                return null;
            } else {
                String string = filename.replace("/", File.separator);
                string = string.replace("\\", File.separator);
                if (!ZomboidFileSystem.instance.isKnownFile(string)) {
                    return null;
                } else {
                    File file = new File(ZomboidFileSystem.instance.getString(string));
                    if (file.exists()) {
                        try {
                            inFileReader = new FileReader(filename);
                            inBufferedReader = new BufferedReader(inFileReader);
                            return inBufferedReader;
                        } catch (FileNotFoundException fileNotFoundException) {
                            Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, fileNotFoundException);
                        }
                    }

                    return null;
                }
            }
        }

        @LuaMethod(
            name = "endTextFileInput",
            global = true
        )
        public static void endTextFileInput() {
            if (inBufferedReader != null) {
                try {
                    inBufferedReader.close();
                    inFileReader.close();
                } catch (IOException iOException) {
                    Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, iOException);
                }
            }

            inBufferedReader = null;
            inFileReader = null;
        }

        @LuaMethod(
            name = "endFileInput",
            global = true
        )
        public static void endFileInput() {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException iOException) {
                    Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, iOException);
                }
            }

            inStream = null;
        }

        @LuaMethod(
            name = "getLineNumber",
            global = true
        )
        public static int getLineNumber(LuaCallFrame c) {
            if (c.closure == null) {
                return 0;
            } else {
                int int0 = c.pc;
                if (int0 < 0) {
                    int0 = 0;
                }

                if (int0 >= c.closure.prototype.lines.length) {
                    int0 = c.closure.prototype.lines.length - 1;
                }

                return c.closure.prototype.lines[int0];
            }
        }

        /**
         * Returns a pseudorandom integer between 0 and max - 1.
         * 
         * @param max Exclusive upper bound of the integer value.
         * @return The random integer.
         */
        @LuaMethod(
            name = "ZombRand",
            global = true
        )
        public static double ZombRand(double max) {
            if (max == 0.0) {
                return 0.0;
            } else {
                return max < 0.0 ? -Rand.Next(-((long)max), Rand.randlua) : Rand.Next((long)max, Rand.randlua);
            }
        }

        /**
         * Returns a pseudorandom integer between min and max - 1. No difference from ZombRand(min, max).
         * 
         * @param min The inclusive lower bound of the random integer.
         * @param max The exclusive upper bound of the random integer.
         * @return The random integer.
         */
        @LuaMethod(
            name = "ZombRandBetween",
            global = true
        )
        public static double ZombRandBetween(double min, double max) {
            return Rand.Next((long)min, (long)max, Rand.randlua);
        }

        /**
         * Returns a pseudorandom integer between min and max - 1.
         * 
         * @param min The inclusive lower bound of the random integer.
         * @param max The exclusive upper bound of the random integer.
         * @return The random integer.
         */
        @LuaMethod(
            name = "ZombRand",
            global = true
        )
        public static double ZombRand(double min, double max) {
            return Rand.Next((int)min, (int)max, Rand.randlua);
        }

        /**
         * Returns a pseudorandom float between min and max.
         * 
         * @param min The lower bound of the random float.
         * @param max The upper bound of the random float.
         * @return The random float.
         */
        @LuaMethod(
            name = "ZombRandFloat",
            global = true
        )
        public static float ZombRandFloat(float min, float max) {
            return Rand.Next(min, max, Rand.randlua);
        }

        @LuaMethod(
            name = "getShortenedFilename",
            global = true
        )
        public static String getShortenedFilename(String str) {
            return str.substring(str.indexOf("lua/") + 4);
        }

        @LuaMethod(
            name = "isKeyDown",
            global = true
        )
        public static boolean isKeyDown(int key) {
            return GameKeyboard.isKeyDown(key);
        }

        @LuaMethod(
            name = "wasKeyDown",
            global = true
        )
        public static boolean wasKeyDown(int key) {
            return GameKeyboard.wasKeyDown(key);
        }

        @LuaMethod(
            name = "isKeyPressed",
            global = true
        )
        public static boolean isKeyPressed(int key) {
            return GameKeyboard.isKeyPressed(key);
        }

        @LuaMethod(
            name = "getFMODSoundBank",
            global = true
        )
        public static BaseSoundBank getFMODSoundBank() {
            return BaseSoundBank.instance;
        }

        @LuaMethod(
            name = "isSoundPlaying",
            global = true
        )
        public static boolean isSoundPlaying(Object sound) {
            return sound instanceof Double ? FMODManager.instance.isPlaying(((Double)sound).longValue()) : false;
        }

        @LuaMethod(
            name = "stopSound",
            global = true
        )
        public static void stopSound(long sound) {
            FMODManager.instance.stopSound(sound);
        }

        @LuaMethod(
            name = "isShiftKeyDown",
            global = true
        )
        public static boolean isShiftKeyDown() {
            return GameKeyboard.isKeyDown(42) || GameKeyboard.isKeyDown(54);
        }

        @LuaMethod(
            name = "isCtrlKeyDown",
            global = true
        )
        public static boolean isCtrlKeyDown() {
            return GameKeyboard.isKeyDown(29) || GameKeyboard.isKeyDown(157);
        }

        @LuaMethod(
            name = "isAltKeyDown",
            global = true
        )
        public static boolean isAltKeyDown() {
            return GameKeyboard.isKeyDown(56) || GameKeyboard.isKeyDown(184);
        }

        @LuaMethod(
            name = "getCore",
            global = true
        )
        public static Core getCore() {
            return Core.getInstance();
        }

        @LuaMethod(
            name = "getGameVersion",
            global = true
        )
        public static String getGameVersion() {
            return Core.getInstance().getGameVersion().toString();
        }

        @LuaMethod(
            name = "getSquare",
            global = true
        )
        public static IsoGridSquare getSquare(double x, double y, double z) {
            return IsoCell.getInstance().getGridSquare(x, y, z);
        }

        @LuaMethod(
            name = "getDebugOptions",
            global = true
        )
        public static DebugOptions getDebugOptions() {
            return DebugOptions.instance;
        }

        @LuaMethod(
            name = "setShowPausedMessage",
            global = true
        )
        public static void setShowPausedMessage(boolean b) {
            DebugLog.log("EXITDEBUG: setShowPausedMessage 1");
            UIManager.setShowPausedMessage(b);
            DebugLog.log("EXITDEBUG: setShowPausedMessage 2");
        }

        @LuaMethod(
            name = "getFilenameOfCallframe",
            global = true
        )
        public static String getFilenameOfCallframe(LuaCallFrame c) {
            return c.closure == null ? null : c.closure.prototype.filename;
        }

        @LuaMethod(
            name = "getFilenameOfClosure",
            global = true
        )
        public static String getFilenameOfClosure(LuaClosure c) {
            return c == null ? null : c.prototype.filename;
        }

        @LuaMethod(
            name = "getFirstLineOfClosure",
            global = true
        )
        public static int getFirstLineOfClosure(LuaClosure c) {
            return c == null ? 0 : c.prototype.lines[0];
        }

        @LuaMethod(
            name = "getLocalVarCount",
            global = true
        )
        public static int getLocalVarCount(Coroutine c) {
            LuaCallFrame luaCallFrame = c.currentCallFrame();
            return luaCallFrame == null ? 0 : luaCallFrame.LocalVarNames.size();
        }

        @LuaMethod(
            name = "isSystemLinux",
            global = true
        )
        public static boolean isSystemLinux() {
            return !isSystemMacOS() && !isSystemWindows();
        }

        @LuaMethod(
            name = "isSystemMacOS",
            global = true
        )
        public static boolean isSystemMacOS() {
            return System.getProperty("os.name").contains("OS X");
        }

        @LuaMethod(
            name = "isSystemWindows",
            global = true
        )
        public static boolean isSystemWindows() {
            return System.getProperty("os.name").startsWith("Win");
        }

        @LuaMethod(
            name = "isModActive",
            global = true
        )
        public static boolean isModActive(ChooseGameInfo.Mod mod) {
            String string = mod.getDir();
            if (!StringUtils.isNullOrWhitespace(mod.getId())) {
                string = mod.getId();
            }

            return ZomboidFileSystem.instance.getModIDs().contains(string);
        }

        @LuaMethod(
            name = "openUrl",
            global = true
        )
        public static void openURl(String url) {
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Action.BROWSE)) {
                try {
                    URI uri = new URI(url);
                    desktop.browse(uri);
                } catch (Exception exception) {
                    ExceptionLogger.logException(exception);
                }
            } else {
                DesktopBrowser.openURL(url);
            }
        }

        @LuaMethod(
            name = "isDesktopOpenSupported",
            global = true
        )
        public static boolean isDesktopOpenSupported() {
            return !Desktop.isDesktopSupported() ? false : Desktop.getDesktop().isSupported(Action.OPEN);
        }

        @LuaMethod(
            name = "showFolderInDesktop",
            global = true
        )
        public static void showFolderInDesktop(String folder) {
            File file = new File(folder);
            if (file.exists() && file.isDirectory()) {
                Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                if (desktop != null && desktop.isSupported(Action.OPEN)) {
                    try {
                        desktop.open(file);
                    } catch (Exception exception) {
                        ExceptionLogger.logException(exception);
                    }
                }
            }
        }

        /**
         * Gets the list of currently activated mods. Remember that in B42+, mod ids are prefixed with a \ character.
         */
        @LuaMethod(
            name = "getActivatedMods",
            global = true
        )
        public static ArrayList<String> getActivatedMods() {
            return ZomboidFileSystem.instance.getModIDs();
        }

        @LuaMethod(
            name = "toggleModActive",
            global = true
        )
        public static void toggleModActive(ChooseGameInfo.Mod mod, boolean active) {
            String string = mod.getDir();
            if (!StringUtils.isNullOrWhitespace(mod.getId())) {
                string = mod.getId();
            }

            ActiveMods.getById("default").setModActive(string, active);
        }

        @LuaMethod(
            name = "saveModsFile",
            global = true
        )
        public static void saveModsFile() {
            ZomboidFileSystem.instance.saveModsFile();
        }

        private static void deleteSavefileFilesMatching(File file, String string) {
            Filter filter = pathx -> pathx.getFileName().toString().matches(string);

            try (DirectoryStream directoryStream = Files.newDirectoryStream(file.toPath(), filter)) {
                for (Path path : directoryStream) {
                    System.out.println("DELETE " + path);
                    Files.deleteIfExists(path);
                }
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
            }
        }

        @LuaMethod(
            name = "manipulateSavefile",
            global = true
        )
        public static void manipulateSavefile(String folder, String action) {
            if (!StringUtils.isNullOrWhitespace(folder)) {
                if (!StringUtils.containsDoubleDot(folder)) {
                    String string = ZomboidFileSystem.instance.getSaveDir() + File.separator + folder;
                    File file = new File(string);
                    if (file.exists() && file.isDirectory()) {
                        switch (action) {
                            case "DeleteChunkDataXYBin":
                                deleteSavefileFilesMatching(file, "chunkdata_[0-9]+_[0-9]+\\.bin");
                                break;
                            case "DeleteMapXYBin":
                                deleteSavefileFilesMatching(file, "map_[0-9]+_[0-9]+\\.bin");
                                break;
                            case "DeleteMapMetaBin":
                                deleteSavefileFilesMatching(file, "map_meta\\.bin");
                                break;
                            case "DeleteMapTBin":
                                deleteSavefileFilesMatching(file, "map_t\\.bin");
                                break;
                            case "DeleteMapZoneBin":
                                deleteSavefileFilesMatching(file, "map_zone\\.bin");
                                break;
                            case "DeletePlayersDB":
                                deleteSavefileFilesMatching(file, "players\\.db");
                                break;
                            case "DeleteReanimatedBin":
                                deleteSavefileFilesMatching(file, "reanimated\\.bin");
                                break;
                            case "DeleteVehiclesDB":
                                deleteSavefileFilesMatching(file, "vehicles\\.db");
                                break;
                            case "DeleteZOutfitsBin":
                                deleteSavefileFilesMatching(file, "z_outfits\\.bin");
                                break;
                            case "DeleteZPopVirtualBin":
                                deleteSavefileFilesMatching(file, "zpop_virtual\\.bin");
                                break;
                            case "DeleteZPopXYBin":
                                deleteSavefileFilesMatching(file, "zpop_[0-9]+_[0-9]+\\.bin");
                                break;
                            case "WriteModsDotTxt":
                                ActiveMods activeMods = ActiveMods.getById("currentGame");
                                ActiveModsFile activeModsFile = new ActiveModsFile();
                                activeModsFile.write(string + File.separator + "mods.txt", activeMods);
                                break;
                            default:
                                throw new IllegalArgumentException("unknown action \"" + action + "\"");
                        }
                    }
                }
            }
        }

        @LuaMethod(
            name = "getLocalVarName",
            global = true
        )
        public static String getLocalVarName(Coroutine c, int n) {
            LuaCallFrame luaCallFrame = c.currentCallFrame();
            return luaCallFrame.LocalVarNames.get(n);
        }

        @LuaMethod(
            name = "getLocalVarStack",
            global = true
        )
        public static int getLocalVarStack(Coroutine c, int n) {
            LuaCallFrame luaCallFrame = c.currentCallFrame();
            return (Integer)luaCallFrame.LocalVarToStackMap.get(luaCallFrame.LocalVarNames.get(n));
        }

        @LuaMethod(
            name = "getCallframeTop",
            global = true
        )
        public static int getCallframeTop(Coroutine c) {
            return c.getCallframeTop();
        }

        @LuaMethod(
            name = "getCoroutineTop",
            global = true
        )
        public static int getCoroutineTop(Coroutine c) {
            return c.getTop();
        }

        @LuaMethod(
            name = "getCoroutineObjStack",
            global = true
        )
        public static Object getCoroutineObjStack(Coroutine c, int n) {
            return c.getObjectFromStack(n);
        }

        @LuaMethod(
            name = "getCoroutineObjStackWithBase",
            global = true
        )
        public static Object getCoroutineObjStackWithBase(Coroutine c, int n) {
            return c.getObjectFromStack(n - c.currentCallFrame().localBase);
        }

        @LuaMethod(
            name = "localVarName",
            global = true
        )
        public static String localVarName(Coroutine c, int n) {
            int int0 = c.getCallframeTop() - 1;
            if (int0 < 0) {
                boolean boolean0 = false;
            }

            return "";
        }

        @LuaMethod(
            name = "getCoroutineCallframeStack",
            global = true
        )
        public static LuaCallFrame getCoroutineCallframeStack(Coroutine c, int n) {
            return c.getCallFrame(n);
        }

        @LuaMethod(
            name = "createTile",
            global = true
        )
        public static void createTile(String tile, IsoGridSquare square) {
            synchronized (IsoSpriteManager.instance.NamedMap) {
                IsoSprite sprite = IsoSpriteManager.instance.NamedMap.get(tile);
                if (sprite != null) {
                    int int0 = 0;
                    int int1 = 0;
                    int int2 = 0;
                    if (square != null) {
                        int0 = square.getX();
                        int1 = square.getY();
                        int2 = square.getZ();
                    }

                    CellLoader.DoTileObjectCreation(sprite, sprite.getType(), square, IsoWorld.instance.CurrentCell, int0, int1, int2, tile);
                }
            }
        }

        @LuaMethod(
            name = "getNumClassFunctions",
            global = true
        )
        public static int getNumClassFunctions(Object o) {
            return o.getClass().getDeclaredMethods().length;
        }

        @LuaMethod(
            name = "getClassFunction",
            global = true
        )
        public static Method getClassFunction(Object o, int i) {
            return o.getClass().getDeclaredMethods()[i];
        }

        @LuaMethod(
            name = "getNumClassFields",
            global = true
        )
        public static int getNumClassFields(Object o) {
            return o.getClass().getDeclaredFields().length;
        }

        @LuaMethod(
            name = "getClassField",
            global = true
        )
        public static Field getClassField(Object o, int i) {
            Field field = o.getClass().getDeclaredFields()[i];
            field.setAccessible(true);
            return field;
        }

        @LuaMethod(
            name = "getDirectionTo",
            global = true
        )
        public static IsoDirections getDirectionTo(IsoGameCharacter chara, IsoObject objTarget) {
            Vector2 vector = new Vector2(objTarget.getX(), objTarget.getY());
            vector.x = vector.x - chara.x;
            vector.y = vector.y - chara.y;
            return IsoDirections.fromAngle(vector);
        }

        @LuaMethod(
            name = "translatePointXInOverheadMapToWindow",
            global = true
        )
        public static float translatePointXInOverheadMapToWindow(float x, UIElement ui, float zoom, float xpos) {
            IngameState.draww = ui.getWidth().intValue();
            return IngameState.translatePointX(x, xpos, zoom, 0.0F);
        }

        @LuaMethod(
            name = "translatePointYInOverheadMapToWindow",
            global = true
        )
        public static float translatePointYInOverheadMapToWindow(float y, UIElement ui, float zoom, float ypos) {
            IngameState.drawh = ui.getHeight().intValue();
            return IngameState.translatePointY(y, ypos, zoom, 0.0F);
        }

        @LuaMethod(
            name = "translatePointXInOverheadMapToWorld",
            global = true
        )
        public static float translatePointXInOverheadMapToWorld(float x, UIElement ui, float zoom, float xpos) {
            IngameState.draww = ui.getWidth().intValue();
            return IngameState.invTranslatePointX(x, xpos, zoom, 0.0F);
        }

        @LuaMethod(
            name = "translatePointYInOverheadMapToWorld",
            global = true
        )
        public static float translatePointYInOverheadMapToWorld(float y, UIElement ui, float zoom, float ypos) {
            IngameState.drawh = ui.getHeight().intValue();
            return IngameState.invTranslatePointY(y, ypos, zoom, 0.0F);
        }

        @LuaMethod(
            name = "drawOverheadMap",
            global = true
        )
        public static void drawOverheadMap(UIElement ui, float zoom, float xpos, float ypos) {
            IngameState.renderDebugOverhead2(
                getCell(), 0, zoom, ui.getAbsoluteX().intValue(), ui.getAbsoluteY().intValue(), xpos, ypos, ui.getWidth().intValue(), ui.getHeight().intValue()
            );
        }

        @LuaMethod(
            name = "assaultPlayer",
            global = true
        )
        public static void assaultPlayer() {
            assert false;
        }

        @LuaMethod(
            name = "isoRegionsRenderer",
            global = true
        )
        public static IsoRegionsRenderer isoRegionsRenderer() {
            return new IsoRegionsRenderer();
        }

        @LuaMethod(
            name = "zpopNewRenderer",
            global = true
        )
        public static ZombiePopulationRenderer zpopNewRenderer() {
            return new ZombiePopulationRenderer();
        }

        @LuaMethod(
            name = "zpopSpawnTimeToZero",
            global = true
        )
        public static void zpopSpawnTimeToZero(int cellX, int cellY) {
            ZombiePopulationManager.instance.dbgSpawnTimeToZero(cellX, cellY);
        }

        @LuaMethod(
            name = "zpopClearZombies",
            global = true
        )
        public static void zpopClearZombies(int cellX, int cellY) {
            ZombiePopulationManager.instance.dbgClearZombies(cellX, cellY);
        }

        @LuaMethod(
            name = "zpopSpawnNow",
            global = true
        )
        public static void zpopSpawnNow(int cellX, int cellY) {
            ZombiePopulationManager.instance.dbgSpawnNow(cellX, cellY);
        }

        @LuaMethod(
            name = "addVirtualZombie",
            global = true
        )
        public static void addVirtualZombie(int x, int y) {
        }

        @LuaMethod(
            name = "luaDebug",
            global = true
        )
        public static void luaDebug() {
            try {
                throw new Exception("LuaDebug");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        @LuaMethod(
            name = "setAggroTarget",
            global = true
        )
        public static void setAggroTarget(int id, int x, int y) {
            ZombiePopulationManager.instance.setAggroTarget(id, x, y);
        }

        @LuaMethod(
            name = "debugFullyStreamedIn",
            global = true
        )
        public static void debugFullyStreamedIn(int x, int y) {
            IngameState.instance.debugFullyStreamedIn(x, y);
        }

        @LuaMethod(
            name = "getClassFieldVal",
            global = true
        )
        public static Object getClassFieldVal(Object o, Field field) {
            try {
                return field.get(o);
            } catch (Exception exception) {
                return "<private>";
            }
        }

        @LuaMethod(
            name = "getMethodParameter",
            global = true
        )
        public static String getMethodParameter(Method o, int i) {
            return o.getParameterTypes()[i].getSimpleName();
        }

        @LuaMethod(
            name = "getMethodParameterCount",
            global = true
        )
        public static int getMethodParameterCount(Method o) {
            return o.getParameterTypes().length;
        }

        @LuaMethod(
            name = "breakpoint",
            global = true
        )
        public static void breakpoint() {
            boolean boolean0 = false;
        }

        @LuaMethod(
            name = "getLuaDebuggerErrorCount",
            global = true
        )
        public static int getLuaDebuggerErrorCount() {
            return KahluaThread.m_error_count;
        }

        @LuaMethod(
            name = "getLuaDebuggerErrors",
            global = true
        )
        public static ArrayList<String> getLuaDebuggerErrors() {
            return new ArrayList<>(KahluaThread.m_errors_list);
        }

        @LuaMethod(
            name = "doLuaDebuggerAction",
            global = true
        )
        public static void doLuaDebuggerAction(String action) {
            UIManager.luaDebuggerAction = action;
        }

        @LuaMethod(
            name = "getGameSpeed",
            global = true
        )
        public static int getGameSpeed() {
            return UIManager.getSpeedControls() != null ? UIManager.getSpeedControls().getCurrentGameSpeed() : 0;
        }

        @LuaMethod(
            name = "setGameSpeed",
            global = true
        )
        public static void setGameSpeed(int NewSpeed) {
            DebugLog.log("EXITDEBUG: setGameSpeed 1");
            if (UIManager.getSpeedControls() == null) {
                DebugLog.log("EXITDEBUG: setGameSpeed 2");
            } else {
                UIManager.getSpeedControls().SetCurrentGameSpeed(NewSpeed);
                DebugLog.log("EXITDEBUG: setGameSpeed 3");
            }
        }

        @LuaMethod(
            name = "isGamePaused",
            global = true
        )
        public static boolean isGamePaused() {
            return GameTime.isGamePaused();
        }

        @LuaMethod(
            name = "getMouseXScaled",
            global = true
        )
        public static int getMouseXScaled() {
            return Mouse.getX();
        }

        @LuaMethod(
            name = "getMouseYScaled",
            global = true
        )
        public static int getMouseYScaled() {
            return Mouse.getY();
        }

        @LuaMethod(
            name = "getMouseX",
            global = true
        )
        public static int getMouseX() {
            return Mouse.getXA();
        }

        @LuaMethod(
            name = "setMouseXY",
            global = true
        )
        public static void setMouseXY(int x, int y) {
            Mouse.setXY(x, y);
        }

        @LuaMethod(
            name = "isMouseButtonDown",
            global = true
        )
        public static boolean isMouseButtonDown(int number) {
            return Mouse.isButtonDown(number);
        }

        @LuaMethod(
            name = "getMouseY",
            global = true
        )
        public static int getMouseY() {
            return Mouse.getYA();
        }

        @LuaMethod(
            name = "getSoundManager",
            global = true
        )
        public static BaseSoundManager getSoundManager() {
            return SoundManager.instance;
        }

        @LuaMethod(
            name = "getLastPlayedDate",
            global = true
        )
        public static String getLastPlayedDate(String filename) {
            File file = new File(ZomboidFileSystem.instance.getSaveDir() + File.separator + filename);
            if (!file.exists()) {
                return Translator.getText("UI_LastPlayed") + "???";
            } else {
                Date date = new Date(file.lastModified());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String string = simpleDateFormat.format(date);
                return Translator.getText("UI_LastPlayed") + string;
            }
        }

        @LuaMethod(
            name = "getTextureFromSaveDir",
            global = true
        )
        public static Texture getTextureFromSaveDir(String filename, String saveName) {
            if (StringUtils.containsDoubleDot(filename)) {
                DebugLog.Lua.warn("relative paths not allowed");
                return null;
            } else {
                TextureID.UseFiltering = true;
                String string = ZomboidFileSystem.instance.getSaveDir() + File.separator + saveName + File.separator + filename;
                Texture texture = Texture.getSharedTexture(string);
                TextureID.UseFiltering = false;
                return texture;
            }
        }

        @LuaMethod(
            name = "getSaveInfo",
            global = true
        )
        public static KahluaTable getSaveInfo(String saveDir) {
            if (!saveDir.contains(File.separator)) {
                saveDir = IsoWorld.instance.getGameMode() + File.separator + saveDir;
            }

            KahluaTable table0 = LuaManager.platform.newTable();
            File file = new File(ZomboidFileSystem.instance.getSaveDir() + File.separator + saveDir);
            if (file.exists()) {
                Date date = new Date(file.lastModified());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String string0 = simpleDateFormat.format(date);
                table0.rawset("lastPlayed", string0);
                String[] strings = saveDir.split("\\" + File.separator);
                table0.rawset("saveName", file.getName());
                table0.rawset("gameMode", strings[strings.length - 2]);
            }

            file = new File(ZomboidFileSystem.instance.getSaveDir() + File.separator + saveDir + File.separator + "map_ver.bin");
            if (file.exists()) {
                try (
                    FileInputStream fileInputStream = new FileInputStream(file);
                    DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                ) {
                    int int0 = dataInputStream.readInt();
                    table0.rawset("worldVersion", (double)int0);
                    if (int0 >= 18) {
                        try {
                            String string1 = GameWindow.ReadString(dataInputStream);
                            if (string1.equals("DEFAULT")) {
                                string1 = "Muldraugh, KY";
                            }

                            table0.rawset("mapName", string1);
                        } catch (Exception exception0) {
                        }
                    }

                    if (int0 >= 74) {
                        try {
                            String string2 = GameWindow.ReadString(dataInputStream);
                            table0.rawset("difficulty", string2);
                        } catch (Exception exception1) {
                        }
                    }
                } catch (Exception exception2) {
                    ExceptionLogger.logException(exception2);
                }
            }

            String string3 = ZomboidFileSystem.instance.getSaveDir() + File.separator + saveDir + File.separator + "mods.txt";
            ActiveMods activeMods = new ActiveMods(saveDir);
            ActiveModsFile activeModsFile = new ActiveModsFile();
            if (activeModsFile.read(string3, activeMods)) {
                table0.rawset("activeMods", activeMods);
            }

            String string4 = ZomboidFileSystem.instance.getSaveDir() + File.separator + saveDir;
            table0.rawset("playerAlive", PlayerDBHelper.isPlayerAlive(string4, 1));
            KahluaTable table1 = LuaManager.platform.newTable();

            try {
                ArrayList arrayList = PlayerDBHelper.getPlayers(string4);

                for (byte byte0 = 0; byte0 < arrayList.size(); byte0 += 3) {
                    Double double0 = (Double)arrayList.get(byte0);
                    String string5 = (String)arrayList.get(byte0 + 1);
                    Boolean boolean0 = (Boolean)arrayList.get(byte0 + 2);
                    KahluaTable table2 = LuaManager.platform.newTable();
                    table2.rawset("sqlID", double0);
                    table2.rawset("name", string5);
                    table2.rawset("isDead", boolean0);
                    table1.rawset(byte0 / 3 + 1, table2);
                }
            } catch (Exception exception3) {
                ExceptionLogger.logException(exception3);
            }

            table0.rawset("players", table1);
            return table0;
        }

        @LuaMethod(
            name = "renameSavefile",
            global = true
        )
        public static boolean renameSaveFile(String gameMode, String oldName, String newName) {
            if (gameMode == null
                || gameMode.contains("/")
                || gameMode.contains("\\")
                || gameMode.contains(File.separator)
                || StringUtils.containsDoubleDot(gameMode)) {
                return false;
            } else if (oldName == null
                || oldName.contains("/")
                || oldName.contains("\\")
                || oldName.contains(File.separator)
                || StringUtils.containsDoubleDot(oldName)) {
                return false;
            } else if (newName != null
                && !newName.contains("/")
                && !newName.contains("\\")
                && !newName.contains(File.separator)
                && !StringUtils.containsDoubleDot(newName)) {
                String string = sanitizeWorldName(newName);
                if (string.equals(newName) && !string.startsWith(".") && !string.endsWith(".")) {
                    if (!new File(ZomboidFileSystem.instance.getSaveDirSub(gameMode)).exists()) {
                        return false;
                    } else {
                        Path path0 = FileSystems.getDefault().getPath(ZomboidFileSystem.instance.getSaveDirSub(gameMode + File.separator + oldName));
                        Path path1 = FileSystems.getDefault().getPath(ZomboidFileSystem.instance.getSaveDirSub(gameMode + File.separator + string));

                        try {
                            Files.move(path0, path1);
                            return true;
                        } catch (IOException iOException) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        @LuaMethod(
            name = "setSavefilePlayer1",
            global = true
        )
        public static void setSavefilePlayer1(String gameMode, String saveDir, int sqlID) {
            String string = ZomboidFileSystem.instance.getSaveDirSub(gameMode + File.separator + saveDir);

            try {
                PlayerDBHelper.setPlayer1(string, sqlID);
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
            }
        }

        @LuaMethod(
            name = "getServerSavedWorldVersion",
            global = true
        )
        public static int getServerSavedWorldVersion(String saveFolder) {
            File file = new File(ZomboidFileSystem.instance.getSaveDir() + File.separator + saveFolder + File.separator + "map_t.bin");
            if (file.exists()) {
                try {
                    byte byte0;
                    try (
                        FileInputStream fileInputStream = new FileInputStream(file);
                        DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                    ) {
                        byte byte1 = dataInputStream.readByte();
                        byte byte2 = dataInputStream.readByte();
                        byte byte3 = dataInputStream.readByte();
                        byte byte4 = dataInputStream.readByte();
                        if (byte1 == 71 && byte2 == 77 && byte3 == 84 && byte4 == 77) {
                            return dataInputStream.readInt();
                        }

                        byte0 = 1;
                    }

                    return byte0;
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            return 0;
        }

        @LuaMethod(
            name = "getZombieInfo",
            global = true
        )
        public static KahluaTable getZombieInfo(IsoZombie zombie) {
            KahluaTable table = LuaManager.platform.newTable();
            if (zombie == null) {
                return table;
            } else {
                table.rawset("OnlineID", zombie.OnlineID);
                table.rawset("RealX", zombie.realx);
                table.rawset("RealY", zombie.realy);
                table.rawset("X", zombie.x);
                table.rawset("Y", zombie.y);
                table.rawset("TargetX", zombie.networkAI.targetX);
                table.rawset("TargetY", zombie.networkAI.targetY);
                table.rawset("PathLength", zombie.getPathFindBehavior2().getPathLength());
                table.rawset(
                    "TargetLength",
                    Math.sqrt(
                        (zombie.x - zombie.getPathFindBehavior2().getTargetX()) * (zombie.x - zombie.getPathFindBehavior2().getTargetX())
                            + (zombie.y - zombie.getPathFindBehavior2().getTargetY()) * (zombie.y - zombie.getPathFindBehavior2().getTargetY())
                    )
                );
                table.rawset("clientActionState", zombie.getActionStateName());
                table.rawset("clientAnimationState", zombie.getAnimationStateName());
                table.rawset("finderProgress", zombie.getFinder().progress.name());
                table.rawset("usePathFind", Boolean.toString(zombie.networkAI.usePathFind));
                table.rawset("owner", zombie.authOwner.username);
                zombie.networkAI.DebugInterfaceActive = true;
                return table;
            }
        }

        @LuaMethod(
            name = "getPlayerInfo",
            global = true
        )
        public static KahluaTable getPlayerInfo(IsoPlayer player) {
            KahluaTable table = LuaManager.platform.newTable();
            if (player == null) {
                return table;
            } else {
                long long0 = GameTime.getServerTime() / 1000000L;
                table.rawset("OnlineID", player.OnlineID);
                table.rawset("RealX", player.realx);
                table.rawset("RealY", player.realy);
                table.rawset("X", player.x);
                table.rawset("Y", player.y);
                table.rawset("TargetX", player.networkAI.targetX);
                table.rawset("TargetY", player.networkAI.targetY);
                table.rawset("TargetT", player.networkAI.targetZ);
                table.rawset("ServerT", long0);
                table.rawset("PathLength", player.getPathFindBehavior2().getPathLength());
                table.rawset(
                    "TargetLength",
                    Math.sqrt(
                        (player.x - player.getPathFindBehavior2().getTargetX()) * (player.x - player.getPathFindBehavior2().getTargetX())
                            + (player.y - player.getPathFindBehavior2().getTargetY()) * (player.y - player.getPathFindBehavior2().getTargetY())
                    )
                );
                table.rawset("clientActionState", player.getActionStateName());
                table.rawset("clientAnimationState", player.getAnimationStateName());
                table.rawset("finderProgress", player.getFinder().progress.name());
                table.rawset("usePathFind", Boolean.toString(player.networkAI.usePathFind));
                return table;
            }
        }

        @LuaMethod(
            name = "getMapInfo",
            global = true
        )
        public static KahluaTable getMapInfo(String mapDir) {
            if (mapDir.contains(";")) {
                mapDir = mapDir.split(";")[0];
            }

            ChooseGameInfo.Map map = ChooseGameInfo.getMapDetails(mapDir);
            if (map == null) {
                return null;
            } else {
                KahluaTable table0 = LuaManager.platform.newTable();
                table0.rawset("description", map.getDescription());
                table0.rawset("dir", map.getDirectory());
                KahluaTable table1 = LuaManager.platform.newTable();
                byte byte0 = 1;

                for (String string : map.getLotDirectories()) {
                    table1.rawset((double)byte0, string);
                }

                table0.rawset("lots", table1);
                table0.rawset("thumb", map.getThumbnail());
                table0.rawset("title", map.getTitle());
                return table0;
            }
        }

        @LuaMethod(
            name = "getVehicleInfo",
            global = true
        )
        public static KahluaTable getVehicleInfo(BaseVehicle vehicle) {
            if (vehicle == null) {
                return null;
            } else {
                KahluaTable table = LuaManager.platform.newTable();
                table.rawset("name", vehicle.getScript().getName());
                table.rawset("weight", vehicle.getMass());
                table.rawset("speed", vehicle.getMaxSpeed());
                table.rawset("frontEndDurability", Integer.toString(vehicle.frontEndDurability));
                table.rawset("rearEndDurability", Integer.toString(vehicle.rearEndDurability));
                table.rawset("currentFrontEndDurability", Integer.toString(vehicle.currentFrontEndDurability));
                table.rawset("currentRearEndDurability", Integer.toString(vehicle.currentRearEndDurability));
                table.rawset("engine_running", vehicle.isEngineRunning());
                table.rawset("engine_started", vehicle.isEngineStarted());
                table.rawset("engine_quality", vehicle.getEngineQuality());
                table.rawset("engine_loudness", vehicle.getEngineLoudness());
                table.rawset("engine_power", vehicle.getEnginePower());
                table.rawset("battery_isset", vehicle.getBattery() != null);
                table.rawset("battery_charge", vehicle.getBatteryCharge());
                table.rawset("gas_amount", vehicle.getPartById("GasTank").getContainerContentAmount());
                table.rawset("gas_capacity", vehicle.getPartById("GasTank").getContainerCapacity());
                VehiclePart part0 = vehicle.getPartById("DoorFrontLeft");
                table.rawset("doorleft_exist", part0 != null);
                if (part0 != null) {
                    table.rawset("doorleft_open", part0.getDoor().isOpen());
                    table.rawset("doorleft_locked", part0.getDoor().isLocked());
                    table.rawset("doorleft_lockbroken", part0.getDoor().isLockBroken());
                    VehicleWindow vehicleWindow0 = part0.findWindow();
                    table.rawset("windowleft_exist", vehicleWindow0 != null);
                    if (vehicleWindow0 != null) {
                        table.rawset("windowleft_open", vehicleWindow0.isOpen());
                        table.rawset("windowleft_health", vehicleWindow0.getHealth());
                    }
                }

                VehiclePart part1 = vehicle.getPartById("DoorFrontRight");
                table.rawset("doorright_exist", part1 != null);
                if (part0 != null) {
                    table.rawset("doorright_open", part1.getDoor().isOpen());
                    table.rawset("doorright_locked", part1.getDoor().isLocked());
                    table.rawset("doorright_lockbroken", part1.getDoor().isLockBroken());
                    VehicleWindow vehicleWindow1 = part1.findWindow();
                    table.rawset("windowright_exist", vehicleWindow1 != null);
                    if (vehicleWindow1 != null) {
                        table.rawset("windowright_open", vehicleWindow1.isOpen());
                        table.rawset("windowright_health", vehicleWindow1.getHealth());
                    }
                }

                table.rawset("headlights_set", vehicle.hasHeadlights());
                table.rawset("headlights_on", vehicle.getHeadlightsOn());
                if (vehicle.getPartById("Heater") != null) {
                    table.rawset("heater_isset", true);
                    Object object = vehicle.getPartById("Heater").getModData().rawget("active");
                    if (object == null) {
                        table.rawset("heater_on", false);
                    } else {
                        table.rawset("heater_on", object == Boolean.TRUE);
                    }
                } else {
                    table.rawset("heater_isset", false);
                }

                return table;
            }
        }

        @LuaMethod(
            name = "getLotDirectories",
            global = true
        )
        public static ArrayList<String> getLotDirectories() {
            return IsoWorld.instance.MetaGrid != null ? IsoWorld.instance.MetaGrid.getLotDirectories() : null;
        }

        @LuaMethod(
            name = "useTextureFiltering",
            global = true
        )
        public static void useTextureFiltering(boolean bUse) {
            TextureID.UseFiltering = bUse;
        }

        @LuaMethod(
            name = "getTexture",
            global = true
        )
        public static Texture getTexture(String filename) {
            return Texture.getSharedTexture(filename);
        }

        @LuaMethod(
            name = "getTextManager",
            global = true
        )
        public static TextManager getTextManager() {
            return TextManager.instance;
        }

        @LuaMethod(
            name = "setProgressBarValue",
            global = true
        )
        public static void setProgressBarValue(IsoPlayer player, int value) {
            if (player.isLocalPlayer()) {
                UIManager.getProgressBar(player.getPlayerNum()).setValue(value);
            }
        }

        @LuaMethod(
            name = "getText",
            global = true
        )
        public static String getText(String txt) {
            return Translator.getText(txt);
        }

        @LuaMethod(
            name = "getText",
            global = true
        )
        public static String getText(String txt, Object arg1) {
            return Translator.getText(txt, arg1);
        }

        @LuaMethod(
            name = "getText",
            global = true
        )
        public static String getText(String txt, Object arg1, Object arg2) {
            return Translator.getText(txt, arg1, arg2);
        }

        @LuaMethod(
            name = "getText",
            global = true
        )
        public static String getText(String txt, Object arg1, Object arg2, Object arg3) {
            return Translator.getText(txt, arg1, arg2, arg3);
        }

        @LuaMethod(
            name = "getText",
            global = true
        )
        public static String getText(String txt, Object arg1, Object arg2, Object arg3, Object arg4) {
            return Translator.getText(txt, arg1, arg2, arg3, arg4);
        }

        @LuaMethod(
            name = "getTextOrNull",
            global = true
        )
        public static String getTextOrNull(String txt) {
            return Translator.getTextOrNull(txt);
        }

        @LuaMethod(
            name = "getTextOrNull",
            global = true
        )
        public static String getTextOrNull(String txt, Object arg1) {
            return Translator.getTextOrNull(txt, arg1);
        }

        @LuaMethod(
            name = "getTextOrNull",
            global = true
        )
        public static String getTextOrNull(String txt, Object arg1, Object arg2) {
            return Translator.getTextOrNull(txt, arg1, arg2);
        }

        @LuaMethod(
            name = "getTextOrNull",
            global = true
        )
        public static String getTextOrNull(String txt, Object arg1, Object arg2, Object arg3) {
            return Translator.getTextOrNull(txt, arg1, arg2, arg3);
        }

        @LuaMethod(
            name = "getTextOrNull",
            global = true
        )
        public static String getTextOrNull(String txt, Object arg1, Object arg2, Object arg3, Object arg4) {
            return Translator.getTextOrNull(txt, arg1, arg2, arg3, arg4);
        }

        @LuaMethod(
            name = "getItemText",
            global = true
        )
        public static String getItemText(String txt) {
            return Translator.getDisplayItemName(txt);
        }

        @LuaMethod(
            name = "getRadioText",
            global = true
        )
        public static String getRadioText(String txt) {
            return Translator.getRadioText(txt);
        }

        @LuaMethod(
            name = "getTextMediaEN",
            global = true
        )
        public static String getTextMediaEN(String txt) {
            return Translator.getTextMediaEN(txt);
        }

        @LuaMethod(
            name = "getItemNameFromFullType",
            global = true
        )
        public static String getItemNameFromFullType(String fullType) {
            return Translator.getItemNameFromFullType(fullType);
        }

        @LuaMethod(
            name = "getRecipeDisplayName",
            global = true
        )
        public static String getRecipeDisplayName(String name) {
            return Translator.getRecipeName(name);
        }

        @LuaMethod(
            name = "getMyDocumentFolder",
            global = true
        )
        public static String getMyDocumentFolder() {
            return Core.getMyDocumentFolder();
        }

        @LuaMethod(
            name = "getSpriteManager",
            global = true
        )
        public static IsoSpriteManager getSpriteManager(String sprite) {
            return IsoSpriteManager.instance;
        }

        @LuaMethod(
            name = "getSprite",
            global = true
        )
        public static IsoSprite getSprite(String sprite) {
            return IsoSpriteManager.instance.getSprite(sprite);
        }

        @LuaMethod(
            name = "getServerModData",
            global = true
        )
        public static void getServerModData() {
            GameClient.getCustomModData();
        }

        @LuaMethod(
            name = "isXBOXController",
            global = true
        )
        public static boolean isXBOXController() {
            for (int int0 = 0; int0 < GameWindow.GameInput.getControllerCount(); int0++) {
                Controller controller = GameWindow.GameInput.getController(int0);
                if (controller != null && controller.getGamepadName().contains("XBOX 360")) {
                    return true;
                }
            }

            return false;
        }

        /**
         * Sends a command to the server, triggering the OnClientCommand event on the server. Does nothing if called on the server.
         * 
         * @param module Module of the command. It is conventional to use the name of your mod as the module for all of your commands.
         * @param command Name of the command.
         * @param args Arguments to pass to the server. Non-POD elements of the table will be lost.
         */
        @LuaMethod(
            name = "sendClientCommand",
            global = true
        )
        public static void sendClientCommand(String module, String command, KahluaTable args) {
            if (GameClient.bClient && GameClient.bIngame) {
                GameClient.instance.sendClientCommand(null, module, command, args);
            } else {
                if (GameServer.bServer) {
                    throw new IllegalStateException("can't call this function on the server");
                }

                SinglePlayerClient.sendClientCommand(null, module, command, args);
            }
        }

        /**
         * Sends a command to the server, triggering the OnClientCommand event on the server. Does nothing if called on the server.
         * 
         * @param player The local player to associate the command with. If the player is not local, no command will be sent.
         * @param module Module of the command. It is conventional to use the name of your mod as the module for all of your commands.
         * @param command Name of the command.
         * @param args Arguments to pass to the server. Non-POD elements of the table will be lost.
         */
        @LuaMethod(
            name = "sendClientCommand",
            global = true
        )
        public static void sendClientCommand(IsoPlayer player, String module, String command, KahluaTable args) {
            if (player != null && player.isLocalPlayer()) {
                if (GameClient.bClient && GameClient.bIngame) {
                    GameClient.instance.sendClientCommand(player, module, command, args);
                } else {
                    if (GameServer.bServer) {
                        throw new IllegalStateException("can't call this function on the server");
                    }

                    SinglePlayerClient.sendClientCommand(player, module, command, args);
                }
            }
        }

        /**
         * Sends a command to all clients, triggering the OnServerCommand event on every client. Does nothing if called on the client.
         * 
         * @param module Module of the command. It is conventional to use the name of your mod as the module for all of your commands.
         * @param command Name of the command.
         * @param args Arguments to pass to the clients. Non-POD elements of the table will be lost.
         */
        @LuaMethod(
            name = "sendServerCommand",
            global = true
        )
        public static void sendServerCommand(String module, String command, KahluaTable args) {
            if (GameServer.bServer) {
                GameServer.sendServerCommand(module, command, args);
            }
        }

        /**
         * Sends a command to a specific client, triggering the OnServerCommand event on the client. Does nothing if called on the client.
         * 
         * @param player The player to send the command to. Only that player's client will receive the command.
         * @param module Module of the command. It is conventional to use the name of your mod as the module for all of your commands.
         * @param command Name of the command.
         * @param args Arguments to pass to the client. Non-POD elements of the table will be lost.
         */
        @LuaMethod(
            name = "sendServerCommand",
            global = true
        )
        public static void sendServerCommand(IsoPlayer player, String module, String command, KahluaTable args) {
            if (GameServer.bServer) {
                GameServer.sendServerCommand(player, module, command, args);
            }
        }

        @LuaMethod(
            name = "getOnlineUsername",
            global = true
        )
        public static String getOnlineUsername() {
            return IsoPlayer.getInstance().getDisplayName();
        }

        @LuaMethod(
            name = "isValidUserName",
            global = true
        )
        public static boolean isValidUserName(String user) {
            return ServerWorldDatabase.isValidUserName(user);
        }

        @LuaMethod(
            name = "getHourMinute",
            global = true
        )
        public static String getHourMinute() {
            return LuaManager.getHourMinuteJava();
        }

        @LuaMethod(
            name = "SendCommandToServer",
            global = true
        )
        public static void SendCommandToServer(String command) {
            GameClient.SendCommandToServer(command);
        }

        @LuaMethod(
            name = "isAdmin",
            global = true
        )
        public static boolean isAdmin() {
            return GameClient.bClient && GameClient.connection.accessLevel == 32;
        }

        @LuaMethod(
            name = "canModifyPlayerScoreboard",
            global = true
        )
        public static boolean canModifyPlayerScoreboard() {
            return GameClient.bClient && GameClient.connection.accessLevel != 1;
        }

        @LuaMethod(
            name = "isAccessLevel",
            global = true
        )
        public static boolean isAccessLevel(String accessLevel) {
            if (GameClient.bClient) {
                return GameClient.connection.accessLevel == 1 ? false : GameClient.connection.accessLevel == PlayerType.fromString(accessLevel);
            } else {
                return false;
            }
        }

        @LuaMethod(
            name = "sendBandage",
            global = true
        )
        public static void sendBandage(int onlineID, int i, boolean bandaged, float bandageLife, boolean isAlcoholic, String bandageType) {
            GameClient.instance.sendBandage(onlineID, i, bandaged, bandageLife, isAlcoholic, bandageType);
        }

        @LuaMethod(
            name = "sendCataplasm",
            global = true
        )
        public static void sendCataplasm(int onlineID, int i, float plantainFactor, float comfreyFactor, float garlicFactor) {
            GameClient.instance.sendCataplasm(onlineID, i, plantainFactor, comfreyFactor, garlicFactor);
        }

        @LuaMethod(
            name = "sendStitch",
            global = true
        )
        public static void sendStitch(IsoGameCharacter wielder, IsoGameCharacter target, BodyPart bodyPart, InventoryItem item, boolean doIt) {
            GameClient.instance.sendStitch(wielder, target, bodyPart, item, doIt);
        }

        @LuaMethod(
            name = "sendDisinfect",
            global = true
        )
        public static void sendDisinfect(IsoGameCharacter wielder, IsoGameCharacter target, BodyPart bodyPart, InventoryItem alcohol) {
            GameClient.instance.sendDisinfect(wielder, target, bodyPart, alcohol);
        }

        @LuaMethod(
            name = "sendSplint",
            global = true
        )
        public static void sendSplint(int onlineID, int i, boolean doIt, float factor, String splintItem) {
            GameClient.instance.sendSplint(onlineID, i, doIt, factor, splintItem);
        }

        @LuaMethod(
            name = "sendRemoveGlass",
            global = true
        )
        public static void sendRemoveGlass(IsoGameCharacter wielder, IsoGameCharacter target, BodyPart bodyPart, boolean handPain) {
            GameClient.instance.sendRemoveGlass(wielder, target, bodyPart, handPain);
        }

        @LuaMethod(
            name = "sendRemoveBullet",
            global = true
        )
        public static void sendRemoveBullet(IsoGameCharacter wielder, IsoGameCharacter target, BodyPart bodyPart) {
            GameClient.instance.sendRemoveBullet(wielder, target, bodyPart);
        }

        @LuaMethod(
            name = "sendCleanBurn",
            global = true
        )
        public static void sendCleanBurn(IsoGameCharacter wielder, IsoGameCharacter target, BodyPart bodyPart, InventoryItem bandage) {
            GameClient.instance.sendCleanBurn(wielder, target, bodyPart, bandage);
        }

        @LuaMethod(
            name = "getGameClient",
            global = true
        )
        public static GameClient getGameClient() {
            return GameClient.instance;
        }

        @LuaMethod(
            name = "sendRequestInventory",
            global = true
        )
        public static void sendRequestInventory(IsoPlayer player) {
            GameClient.sendRequestInventory(player);
        }

        @LuaMethod(
            name = "InvMngGetItem",
            global = true
        )
        public static void InvMngGetItem(long itemId, String itemType, IsoPlayer player) {
            GameClient.invMngRequestItem((int)itemId, itemType, player);
        }

        @LuaMethod(
            name = "InvMngRemoveItem",
            global = true
        )
        public static void InvMngRemoveItem(long itemId, IsoPlayer player) {
            GameClient.invMngRequestRemoveItem((int)itemId, player);
        }

        @LuaMethod(
            name = "getConnectedPlayers",
            global = true
        )
        public static ArrayList<IsoPlayer> getConnectedPlayers() {
            return GameClient.instance.getConnectedPlayers();
        }

        @LuaMethod(
            name = "getPlayerFromUsername",
            global = true
        )
        public static IsoPlayer getPlayerFromUsername(String username) {
            return GameClient.instance.getPlayerFromUsername(username);
        }

        @LuaMethod(
            name = "isCoopHost",
            global = true
        )
        public static boolean isCoopHost() {
            return GameClient.connection != null && GameClient.connection.isCoopHost;
        }

        @LuaMethod(
            name = "setAdmin",
            global = true
        )
        public static void setAdmin() {
            if (CoopMaster.instance.isRunning()) {
                String string = "admin";
                if (GameClient.connection.accessLevel == 32) {
                    string = "";
                }

                GameClient.connection.accessLevel = PlayerType.fromString(string);
                IsoPlayer.getInstance().accessLevel = string;
                GameClient.SendCommandToServer("/setaccesslevel \"" + IsoPlayer.getInstance().username + "\" \"" + (string.equals("") ? "none" : string) + "\"");
                if (string.equals("") && IsoPlayer.getInstance().isInvisible() || string.equals("admin") && !IsoPlayer.getInstance().isInvisible()) {
                    GameClient.SendCommandToServer("/invisible");
                }
            }
        }

        @LuaMethod(
            name = "addWarningPoint",
            global = true
        )
        public static void addWarningPoint(String user, String reason, int amount) {
            if (GameClient.bClient) {
                GameClient.instance.addWarningPoint(user, reason, amount);
            }
        }

        @LuaMethod(
            name = "toggleSafetyServer",
            global = true
        )
        public static void toggleSafetyServer(IsoPlayer player) {
        }

        @LuaMethod(
            name = "disconnect",
            global = true
        )
        public static void disconnect() {
            GameClient.connection.forceDisconnect("lua-disconnect");
        }

        @LuaMethod(
            name = "writeLog",
            global = true
        )
        public static void writeLog(String loggerName, String logs) {
            LoggerManager.getLogger(loggerName).write(logs);
        }

        @LuaMethod(
            name = "doKeyPress",
            global = true
        )
        public static void doKeyPress(boolean doIt) {
            GameKeyboard.doLuaKeyPressed = doIt;
        }

        @LuaMethod(
            name = "getEvolvedRecipes",
            global = true
        )
        public static Stack<EvolvedRecipe> getEvolvedRecipes() {
            return ScriptManager.instance.getAllEvolvedRecipes();
        }

        @LuaMethod(
            name = "getZone",
            global = true
        )
        public static IsoMetaGrid.Zone getZone(int x, int y, int z) {
            return IsoWorld.instance.MetaGrid.getZoneAt(x, y, z);
        }

        @LuaMethod(
            name = "getZones",
            global = true
        )
        public static ArrayList<IsoMetaGrid.Zone> getZones(int x, int y, int z) {
            return IsoWorld.instance.MetaGrid.getZonesAt(x, y, z);
        }

        @LuaMethod(
            name = "getVehicleZoneAt",
            global = true
        )
        public static IsoMetaGrid.VehicleZone getVehicleZoneAt(int x, int y, int z) {
            return IsoWorld.instance.MetaGrid.getVehicleZoneAt(x, y, z);
        }

        @LuaMethod(
            name = "replaceWith",
            global = true
        )
        public static String replaceWith(String toReplace, String regex, String by) {
            return toReplace.replaceFirst(regex, by);
        }

        @LuaMethod(
            name = "getTimestamp",
            global = true
        )
        public static long getTimestamp() {
            return System.currentTimeMillis() / 1000L;
        }

        @LuaMethod(
            name = "getTimestampMs",
            global = true
        )
        public static long getTimestampMs() {
            return System.currentTimeMillis();
        }

        @LuaMethod(
            name = "forceSnowCheck",
            global = true
        )
        public static void forceSnowCheck() {
            ErosionMain.getInstance().snowCheck();
        }

        @LuaMethod(
            name = "getGametimeTimestamp",
            global = true
        )
        public static long getGametimeTimestamp() {
            return GameTime.instance.getCalender().getTimeInMillis() / 1000L;
        }

        @LuaMethod(
            name = "canInviteFriends",
            global = true
        )
        public static boolean canInviteFriends() {
            return GameClient.bClient && SteamUtils.isSteamModeEnabled() ? CoopMaster.instance.isRunning() || !GameClient.bCoopInvite : false;
        }

        @LuaMethod(
            name = "inviteFriend",
            global = true
        )
        public static void inviteFriend(String steamID) {
            if (CoopMaster.instance != null && CoopMaster.instance.isRunning()) {
                CoopMaster.instance.sendMessage("invite-add", steamID);
            }

            SteamFriends.InviteUserToGame(SteamUtils.convertStringToSteamID(steamID), "+connect " + GameClient.ip + ":" + GameClient.port);
        }

        @LuaMethod(
            name = "getFriendsList",
            global = true
        )
        public static KahluaTable getFriendsList() {
            KahluaTable table = LuaManager.platform.newTable();
            if (!getSteamModeActive()) {
                return table;
            } else {
                List list = SteamFriends.GetFriendList();
                int int0 = 1;

                for (int int1 = 0; int1 < list.size(); int1++) {
                    SteamFriend steamFriend = (SteamFriend)list.get(int1);
                    Double double0 = (double)int0;
                    table.rawset(double0, steamFriend);
                    int0++;
                }

                return table;
            }
        }

        @LuaMethod(
            name = "getSteamModeActive",
            global = true
        )
        public static Boolean getSteamModeActive() {
            return SteamUtils.isSteamModeEnabled();
        }

        @LuaMethod(
            name = "isValidSteamID",
            global = true
        )
        public static boolean isValidSteamID(String s) {
            return s != null && !s.isEmpty() ? SteamUtils.isValidSteamID(s) : false;
        }

        @LuaMethod(
            name = "getCurrentUserSteamID",
            global = true
        )
        public static String getCurrentUserSteamID() {
            return SteamUtils.isSteamModeEnabled() && !GameServer.bServer ? SteamUser.GetSteamIDString() : null;
        }

        @LuaMethod(
            name = "getCurrentUserProfileName",
            global = true
        )
        public static String getCurrentUserProfileName() {
            return SteamUtils.isSteamModeEnabled() && !GameServer.bServer ? SteamFriends.GetFriendPersonaName(SteamUser.GetSteamID()) : null;
        }

        @LuaMethod(
            name = "getSteamScoreboard",
            global = true
        )
        public static boolean getSteamScoreboard() {
            if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
                String string = ServerOptions.instance.SteamScoreboard.getValue();
                return "true".equals(string) || GameClient.connection.accessLevel == 32 && "admin".equals(string);
            } else {
                return false;
            }
        }

        @LuaMethod(
            name = "isSteamOverlayEnabled",
            global = true
        )
        public static boolean isSteamOverlayEnabled() {
            return SteamUtils.isOverlayEnabled();
        }

        @LuaMethod(
            name = "activateSteamOverlayToWorkshop",
            global = true
        )
        public static void activateSteamOverlayToWorkshop() {
            if (SteamUtils.isOverlayEnabled()) {
                SteamFriends.ActivateGameOverlayToWebPage("steam://url/SteamWorkshopPage/108600");
            }
        }

        @LuaMethod(
            name = "activateSteamOverlayToWorkshopUser",
            global = true
        )
        public static void activateSteamOverlayToWorkshopUser() {
            if (SteamUtils.isOverlayEnabled()) {
                SteamFriends.ActivateGameOverlayToWebPage("steam://url/SteamIDCommunityFilesPage/" + SteamUser.GetSteamIDString() + "/108600");
            }
        }

        @LuaMethod(
            name = "activateSteamOverlayToWorkshopItem",
            global = true
        )
        public static void activateSteamOverlayToWorkshopItem(String itemID) {
            if (SteamUtils.isOverlayEnabled() && SteamUtils.isValidSteamID(itemID)) {
                SteamFriends.ActivateGameOverlayToWebPage("steam://url/CommunityFilePage/" + itemID);
            }
        }

        @LuaMethod(
            name = "activateSteamOverlayToWebPage",
            global = true
        )
        public static void activateSteamOverlayToWebPage(String url) {
            if (SteamUtils.isOverlayEnabled()) {
                SteamFriends.ActivateGameOverlayToWebPage(url);
            }
        }

        @LuaMethod(
            name = "getSteamProfileNameFromSteamID",
            global = true
        )
        public static String getSteamProfileNameFromSteamID(String steamID) {
            if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
                long long0 = SteamUtils.convertStringToSteamID(steamID);
                if (long0 != -1L) {
                    return SteamFriends.GetFriendPersonaName(long0);
                }
            }

            return null;
        }

        @LuaMethod(
            name = "getSteamAvatarFromSteamID",
            global = true
        )
        public static Texture getSteamAvatarFromSteamID(String steamID) {
            if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
                long long0 = SteamUtils.convertStringToSteamID(steamID);
                if (long0 != -1L) {
                    return Texture.getSteamAvatar(long0);
                }
            }

            return null;
        }

        @LuaMethod(
            name = "getSteamIDFromUsername",
            global = true
        )
        public static String getSteamIDFromUsername(String username) {
            if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
                IsoPlayer player = GameClient.instance.getPlayerFromUsername(username);
                if (player != null) {
                    return SteamUtils.convertSteamIDToString(player.getSteamID());
                }
            }

            return null;
        }

        @LuaMethod(
            name = "resetRegionFile",
            global = true
        )
        public static void resetRegionFile() {
            ServerOptions.getInstance().resetRegionFile();
        }

        @LuaMethod(
            name = "getSteamProfileNameFromUsername",
            global = true
        )
        public static String getSteamProfileNameFromUsername(String username) {
            if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
                IsoPlayer player = GameClient.instance.getPlayerFromUsername(username);
                if (player != null) {
                    return SteamFriends.GetFriendPersonaName(player.getSteamID());
                }
            }

            return null;
        }

        @LuaMethod(
            name = "getSteamAvatarFromUsername",
            global = true
        )
        public static Texture getSteamAvatarFromUsername(String username) {
            if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
                IsoPlayer player = GameClient.instance.getPlayerFromUsername(username);
                if (player != null) {
                    return Texture.getSteamAvatar(player.getSteamID());
                }
            }

            return null;
        }

        @LuaMethod(
            name = "getSteamWorkshopStagedItems",
            global = true
        )
        public static ArrayList<SteamWorkshopItem> getSteamWorkshopStagedItems() {
            return SteamUtils.isSteamModeEnabled() ? SteamWorkshop.instance.loadStagedItems() : null;
        }

        @LuaMethod(
            name = "getSteamWorkshopItemIDs",
            global = true
        )
        public static ArrayList<String> getSteamWorkshopItemIDs() {
            if (SteamUtils.isSteamModeEnabled()) {
                ArrayList arrayList = new ArrayList();
                String[] strings = SteamWorkshop.instance.GetInstalledItemFolders();
                if (strings == null) {
                    return arrayList;
                } else {
                    for (int int0 = 0; int0 < strings.length; int0++) {
                        String string = SteamWorkshop.instance.getIDFromItemInstallFolder(strings[int0]);
                        if (string != null) {
                            arrayList.add(string);
                        }
                    }

                    return arrayList;
                }
            } else {
                return null;
            }
        }

        @LuaMethod(
            name = "getSteamWorkshopItemMods",
            global = true
        )
        public static ArrayList<ChooseGameInfo.Mod> getSteamWorkshopItemMods(String itemIDStr) {
            if (SteamUtils.isSteamModeEnabled()) {
                long long0 = SteamUtils.convertStringToSteamID(itemIDStr);
                if (long0 > 0L) {
                    return ZomboidFileSystem.instance.getWorkshopItemMods(long0);
                }
            }

            return null;
        }

        @LuaMethod(
            name = "isSteamRunningOnSteamDeck",
            global = true
        )
        public static boolean isSteamRunningOnSteamDeck() {
            return SteamUtils.isSteamModeEnabled() ? SteamUtils.isRunningOnSteamDeck() : false;
        }

        @LuaMethod(
            name = "showSteamGamepadTextInput",
            global = true
        )
        public static boolean showSteamGamepadTextInput(boolean password, boolean multiLine, String description, int maxChars, String existingText) {
            return SteamUtils.isSteamModeEnabled() ? SteamUtils.showGamepadTextInput(password, multiLine, description, maxChars, existingText) : false;
        }

        @LuaMethod(
            name = "showSteamFloatingGamepadTextInput",
            global = true
        )
        public static boolean showSteamFloatingGamepadTextInput(boolean multiLine, int x, int y, int width, int height) {
            return SteamUtils.isSteamModeEnabled() ? SteamUtils.showFloatingGamepadTextInput(multiLine, x, y, width, height) : false;
        }

        @LuaMethod(
            name = "isFloatingGamepadTextInputVisible",
            global = true
        )
        public static boolean isFloatingGamepadTextInputVisible() {
            return SteamUtils.isSteamModeEnabled() ? SteamUtils.isFloatingGamepadTextInputVisible() : false;
        }

        @LuaMethod(
            name = "sendPlayerStatsChange",
            global = true
        )
        public static void sendPlayerStatsChange(IsoPlayer player) {
            if (GameClient.bClient) {
                GameClient.instance.sendChangedPlayerStats(player);
            }
        }

        @LuaMethod(
            name = "sendPersonalColor",
            global = true
        )
        public static void sendPersonalColor(IsoPlayer player) {
            if (GameClient.bClient) {
                GameClient.instance.sendPersonalColor(player);
            }
        }

        @LuaMethod(
            name = "requestTrading",
            global = true
        )
        public static void requestTrading(IsoPlayer you, IsoPlayer other) {
            GameClient.instance.requestTrading(you, other);
        }

        @LuaMethod(
            name = "acceptTrading",
            global = true
        )
        public static void acceptTrading(IsoPlayer you, IsoPlayer other, boolean accept) {
            GameClient.instance.acceptTrading(you, other, accept);
        }

        @LuaMethod(
            name = "tradingUISendAddItem",
            global = true
        )
        public static void tradingUISendAddItem(IsoPlayer you, IsoPlayer other, InventoryItem i) {
            GameClient.instance.tradingUISendAddItem(you, other, i);
        }

        @LuaMethod(
            name = "tradingUISendRemoveItem",
            global = true
        )
        public static void tradingUISendRemoveItem(IsoPlayer you, IsoPlayer other, int index) {
            GameClient.instance.tradingUISendRemoveItem(you, other, index);
        }

        @LuaMethod(
            name = "tradingUISendUpdateState",
            global = true
        )
        public static void tradingUISendUpdateState(IsoPlayer you, IsoPlayer other, int state) {
            GameClient.instance.tradingUISendUpdateState(you, other, state);
        }

        @LuaMethod(
            name = "querySteamWorkshopItemDetails",
            global = true
        )
        public static void querySteamWorkshopItemDetails(ArrayList<String> itemIDs, LuaClosure functionObj, Object arg1) {
            if (itemIDs == null || functionObj == null) {
                throw new NullPointerException();
            } else if (itemIDs.isEmpty()) {
                if (arg1 == null) {
                    LuaManager.caller.pcall(LuaManager.thread, functionObj, "Completed", new ArrayList());
                } else {
                    LuaManager.caller.pcall(LuaManager.thread, functionObj, arg1, "Completed", new ArrayList());
                }
            } else {
                new LuaManager.GlobalObject.ItemQuery(itemIDs, functionObj, arg1);
            }
        }

        @LuaMethod(
            name = "connectToServerStateCallback",
            global = true
        )
        public static void connectToServerStateCallback(String button) {
            if (ConnectToServerState.instance != null) {
                ConnectToServerState.instance.FromLua(button);
            }
        }

        @LuaMethod(
            name = "getPublicServersList",
            global = true
        )
        public static KahluaTable getPublicServersList() {
            KahluaTable table = LuaManager.platform.newTable();
            if (!SteamUtils.isSteamModeEnabled() && !PublicServerUtil.isEnabled()) {
                return table;
            } else if (System.currentTimeMillis() - timeLastRefresh < 60000L) {
                return table;
            } else {
                ArrayList arrayList = new ArrayList();

                try {
                    if (getSteamModeActive()) {
                        ServerBrowser.RefreshInternetServers();
                        List list = ServerBrowser.GetServerList();

                        for (GameServerDetails gameServerDetails : list) {
                            Server server0 = new Server();
                            server0.setName(gameServerDetails.name);
                            server0.setDescription(gameServerDetails.gameDescription);
                            server0.setSteamId(Long.toString(gameServerDetails.steamId));
                            server0.setPing(Integer.toString(gameServerDetails.ping));
                            server0.setPlayers(Integer.toString(gameServerDetails.numPlayers));
                            server0.setMaxPlayers(Integer.toString(gameServerDetails.maxPlayers));
                            server0.setOpen(true);
                            server0.setIp(gameServerDetails.address);
                            server0.setPort(Integer.toString(gameServerDetails.port));
                            server0.setMods(gameServerDetails.tags);
                            server0.setVersion(Core.getInstance().getVersion());
                            server0.setLastUpdate(1);
                            arrayList.add(server0);
                        }

                        System.out.printf("%d servers\n", list.size());
                    } else {
                        URL url = new URL(PublicServerUtil.webSite + "servers.xml");
                        InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        Object object = null;
                        StringBuffer stringBuffer = new StringBuffer();

                        while ((object = bufferedReader.readLine()) != null) {
                            stringBuffer.append((String)object).append('\n');
                        }

                        bufferedReader.close();
                        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                        Document document = documentBuilder.parse(new InputSource(new StringReader(stringBuffer.toString())));
                        document.getDocumentElement().normalize();
                        NodeList nodeList0 = document.getElementsByTagName("server");

                        for (int int0 = 0; int0 < nodeList0.getLength(); int0++) {
                            Node node = nodeList0.item(int0);
                            if (node.getNodeType() == 1) {
                                Element element = (Element)node;
                                Server server1 = new Server();
                                server1.setName(element.getElementsByTagName("name").item(0).getTextContent());
                                if (element.getElementsByTagName("desc").item(0) != null
                                    && !"".equals(element.getElementsByTagName("desc").item(0).getTextContent())) {
                                    server1.setDescription(element.getElementsByTagName("desc").item(0).getTextContent());
                                }

                                server1.setIp(element.getElementsByTagName("ip").item(0).getTextContent());
                                server1.setPort(element.getElementsByTagName("port").item(0).getTextContent());
                                server1.setPlayers(element.getElementsByTagName("players").item(0).getTextContent());
                                server1.setMaxPlayers(element.getElementsByTagName("maxPlayers").item(0).getTextContent());
                                if (element.getElementsByTagName("version") != null && element.getElementsByTagName("version").item(0) != null) {
                                    server1.setVersion(element.getElementsByTagName("version").item(0).getTextContent());
                                }

                                server1.setOpen(element.getElementsByTagName("open").item(0).getTextContent().equals("1"));
                                Integer integer = Integer.parseInt(element.getElementsByTagName("lastUpdate").item(0).getTextContent());
                                if (element.getElementsByTagName("mods").item(0) != null
                                    && !"".equals(element.getElementsByTagName("mods").item(0).getTextContent())) {
                                    server1.setMods(element.getElementsByTagName("mods").item(0).getTextContent());
                                }

                                server1.setLastUpdate(new Double(Math.floor((getTimestamp() - integer.intValue()) / 60L)).intValue());
                                NodeList nodeList1 = element.getElementsByTagName("password");
                                server1.setPasswordProtected(nodeList1 != null && nodeList1.getLength() != 0 && nodeList1.item(0).getTextContent().equals("1"));
                                arrayList.add(server1);
                            }
                        }
                    }

                    int int1 = 1;

                    for (int int2 = 0; int2 < arrayList.size(); int2++) {
                        Server server2 = (Server)arrayList.get(int2);
                        Double double0 = (double)int1;
                        table.rawset(double0, server2);
                        int1++;
                    }

                    timeLastRefresh = Calendar.getInstance().getTimeInMillis();
                    return table;
                } catch (Exception exception) {
                    exception.printStackTrace();
                    return null;
                }
            }
        }

        @LuaMethod(
            name = "steamRequestInternetServersList",
            global = true
        )
        public static void steamRequestInternetServersList() {
            ServerBrowser.RefreshInternetServers();
        }

        @LuaMethod(
            name = "steamReleaseInternetServersRequest",
            global = true
        )
        public static void steamReleaseInternetServersRequest() {
            ServerBrowser.Release();
        }

        @LuaMethod(
            name = "steamGetInternetServersCount",
            global = true
        )
        public static int steamRequestInternetServersCount() {
            return ServerBrowser.GetServerCount();
        }

        @LuaMethod(
            name = "steamGetInternetServerDetails",
            global = true
        )
        public static Server steamGetInternetServerDetails(int index) {
            if (!ServerBrowser.IsRefreshing()) {
                return null;
            } else {
                GameServerDetails gameServerDetails = ServerBrowser.GetServerDetails(index);
                if (gameServerDetails == null) {
                    return null;
                } else if (gameServerDetails.tags.contains("hidden") || gameServerDetails.tags.contains("hosted")) {
                    return null;
                } else if (!gameServerDetails.tags.contains("hidden") && !gameServerDetails.tags.contains("hosted")) {
                    Server server = new Server();
                    server.setName(gameServerDetails.name);
                    server.setDescription("");
                    server.setSteamId(Long.toString(gameServerDetails.steamId));
                    server.setPing(Integer.toString(gameServerDetails.ping));
                    server.setPlayers(Integer.toString(gameServerDetails.numPlayers));
                    server.setMaxPlayers(Integer.toString(gameServerDetails.maxPlayers));
                    server.setOpen(true);
                    server.setPublic(true);
                    if (gameServerDetails.tags.contains("hidden")) {
                        server.setOpen(false);
                        server.setPublic(false);
                    }

                    server.setIp(gameServerDetails.address);
                    server.setPort(Integer.toString(gameServerDetails.port));
                    server.setMods("");
                    if (!gameServerDetails.tags.replace("hidden", "").replace("hosted", "").replace(";", "").isEmpty()) {
                        server.setMods(gameServerDetails.tags.replace(";hosted", "").replace("hidden", ""));
                    }

                    server.setHosted(gameServerDetails.tags.contains("hosted"));
                    server.setVersion("");
                    server.setLastUpdate(1);
                    server.setPasswordProtected(gameServerDetails.passwordProtected);
                    return server;
                } else {
                    return null;
                }
            }
        }

        @LuaMethod(
            name = "steamRequestServerRules",
            global = true
        )
        public static boolean steamRequestServerRules(String host, int port) {
            return ServerBrowser.RequestServerRules(host, port);
        }

        @LuaMethod(
            name = "steamRequestServerDetails",
            global = true
        )
        public static boolean steamRequestServerDetails(String host, int port) {
            return ServerBrowser.QueryServer(host, port);
        }

        @LuaMethod(
            name = "isPublicServerListAllowed",
            global = true
        )
        public static boolean isPublicServerListAllowed() {
            return SteamUtils.isSteamModeEnabled() ? true : PublicServerUtil.isEnabled();
        }

        @LuaMethod(
            name = "is64bit",
            global = true
        )
        public static boolean is64bit() {
            return "64".equals(System.getProperty("sun.arch.data.model"));
        }

        @LuaMethod(
            name = "testSound",
            global = true
        )
        public static void testSound() {
            float float0 = Mouse.getX();
            float float1 = Mouse.getY();
            int int0 = (int)IsoPlayer.getInstance().getZ();
            int int1 = (int)IsoUtils.XToIso(float0, float1, int0);
            int int2 = (int)IsoUtils.YToIso(float0, float1, int0);
            float float2 = 50.0F;
            float float3 = 1.0F;
            AmbientStreamManager.Ambient ambient = new AmbientStreamManager.Ambient("Meta/House Alarm", int1, int2, float2, float3);
            ambient.trackMouse = true;
            ((AmbientStreamManager)AmbientStreamManager.instance).ambient.add(ambient);
        }

        @LuaMethod(
            name = "debugSetRoomType",
            global = true
        )
        public static void debugSetRoomType(Double roomType) {
            ParameterRoomType.setRoomType(roomType.intValue());
        }

        @LuaMethod(
            name = "copyTable",
            global = true
        )
        public static KahluaTable copyTable(KahluaTable table) {
            return LuaManager.copyTable(table);
        }

        @LuaMethod(
            name = "copyTable",
            global = true
        )
        public static KahluaTable copyTable(KahluaTable to, KahluaTable from) {
            return LuaManager.copyTable(to, from);
        }

        @LuaMethod(
            name = "getUrlInputStream",
            global = true
        )
        public static DataInputStream getUrlInputStream(String url) {
            if (url != null && (url.startsWith("https://") || url.startsWith("http://"))) {
                try {
                    return new DataInputStream(new URL(url).openStream());
                } catch (IOException iOException) {
                    iOException.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }
        }

        @LuaMethod(
            name = "renderIsoCircle",
            global = true
        )
        public static void renderIsoCircle(float x, float y, float z, float radius, float r, float g, float b, float a, int thickness) {
            double double0 = Math.PI / 9;

            for (double double1 = 0.0; double1 < Math.PI * 2; double1 += double0) {
                float float0 = x + radius * (float)Math.cos(double1);
                float float1 = y + radius * (float)Math.sin(double1);
                float float2 = x + radius * (float)Math.cos(double1 + double0);
                float float3 = y + radius * (float)Math.sin(double1 + double0);
                float float4 = IsoUtils.XToScreenExact(float0, float1, z, 0);
                float float5 = IsoUtils.YToScreenExact(float0, float1, z, 0);
                float float6 = IsoUtils.XToScreenExact(float2, float3, z, 0);
                float float7 = IsoUtils.YToScreenExact(float2, float3, z, 0);
                LineDrawer.drawLine(float4, float5, float6, float7, r, g, b, a, thickness);
            }
        }

        @LuaMethod(
            name = "configureLighting",
            global = true
        )
        public static void configureLighting(float darkStep) {
            if (LightingJNI.init) {
                LightingJNI.configure(darkStep);
            }
        }

        @LuaMethod(
            name = "testHelicopter",
            global = true
        )
        public static void testHelicopter() {
            if (GameClient.bClient) {
                GameClient.SendCommandToServer("/chopper start");
            } else {
                IsoWorld.instance.helicopter.pickRandomTarget();
            }
        }

        @LuaMethod(
            name = "endHelicopter",
            global = true
        )
        public static void endHelicopter() {
            if (GameClient.bClient) {
                GameClient.SendCommandToServer("/chopper stop");
            } else {
                IsoWorld.instance.helicopter.deactivate();
            }
        }

        @LuaMethod(
            name = "getServerSettingsManager",
            global = true
        )
        public static ServerSettingsManager getServerSettingsManager() {
            return ServerSettingsManager.instance;
        }

        @LuaMethod(
            name = "rainConfig",
            global = true
        )
        public static void rainConfig(String cmd, int arg) {
            if ("alpha".equals(cmd)) {
                IsoWorld.instance.CurrentCell.setRainAlpha(arg);
            }

            if ("intensity".equals(cmd)) {
                IsoWorld.instance.CurrentCell.setRainIntensity(arg);
            }

            if ("speed".equals(cmd)) {
                IsoWorld.instance.CurrentCell.setRainSpeed(arg);
            }

            if ("reloadTextures".equals(cmd)) {
                IsoWorld.instance.CurrentCell.reloadRainTextures();
            }
        }

        @LuaMethod(
            name = "sendSwitchSeat",
            global = true
        )
        public static void sendSwitchSeat(BaseVehicle vehicle, IsoGameCharacter chr, int seatFrom, int seatTo) {
            if (GameClient.bClient) {
                VehicleManager.instance.sendSwitchSeat(GameClient.connection, vehicle, chr, seatFrom, seatTo);
            }
        }

        @LuaMethod(
            name = "getVehicleById",
            global = true
        )
        public static BaseVehicle getVehicleById(int id) {
            return VehicleManager.instance.getVehicleByID((short)id);
        }

        /**
         * Adds bloodstains to a specific square.
         * 
         * @param sq The square.
         * @param nbr Number of bloodstains to add.
         */
        @LuaMethod(
            name = "addBloodSplat",
            global = true
        )
        public void addBloodSplat(IsoGridSquare sq, int nbr) {
            for (int int0 = 0; int0 < nbr; int0++) {
                sq.getChunk().addBloodSplat(sq.x + Rand.Next(-0.5F, 0.5F), sq.y + Rand.Next(-0.5F, 0.5F), sq.z, Rand.Next(8));
            }
        }

        @LuaMethod(
            name = "addCarCrash",
            global = true
        )
        public static void addCarCrash() {
            IsoGridSquare square = IsoPlayer.getInstance().getCurrentSquare();
            if (square != null) {
                IsoChunk chunk = square.getChunk();
                if (chunk != null) {
                    IsoMetaGrid.Zone zone = square.getZone();
                    if (zone != null) {
                        if (chunk.canAddRandomCarCrash(zone, true)) {
                            square.chunk.addRandomCarCrash(zone, true);
                        }
                    }
                }
            }
        }

        @LuaMethod(
            name = "createRandomDeadBody",
            global = true
        )
        public static IsoDeadBody createRandomDeadBody(IsoGridSquare square, int blood) {
            if (square == null) {
                return null;
            } else {
                ItemPickerJava.ItemPickerRoom itemPickerRoom = ItemPickerJava.rooms.get("all");
                RandomizedBuildingBase.HumanCorpse humanCorpse = new RandomizedBuildingBase.HumanCorpse(
                    IsoWorld.instance.getCell(), square.x, square.y, square.z
                );
                humanCorpse.setDir(IsoDirections.getRandom());
                humanCorpse.setDescriptor(SurvivorFactory.CreateSurvivor());
                humanCorpse.setFemale(humanCorpse.getDescriptor().isFemale());
                humanCorpse.initWornItems("Human");
                humanCorpse.initAttachedItems("Human");
                Outfit outfit = humanCorpse.getRandomDefaultOutfit();
                humanCorpse.dressInNamedOutfit(outfit.m_Name);
                humanCorpse.initSpritePartsEmpty();
                humanCorpse.Dressup(humanCorpse.getDescriptor());

                for (int int0 = 0; int0 < blood; int0++) {
                    humanCorpse.addBlood(null, false, true, false);
                }

                IsoDeadBody deadBody = new IsoDeadBody(humanCorpse, true);
                ItemPickerJava.fillContainerType(itemPickerRoom, deadBody.getContainer(), humanCorpse.isFemale() ? "inventoryfemale" : "inventorymale", null);
                return deadBody;
            }
        }

        @LuaMethod(
            name = "addZombieSitting",
            global = true
        )
        public void addZombieSitting(int x, int y, int z) {
            IsoGridSquare square = IsoCell.getInstance().getGridSquare(x, y, z);
            if (square != null) {
                VirtualZombieManager.instance.choices.clear();
                VirtualZombieManager.instance.choices.add(square);
                IsoZombie zombie0 = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.getRandom().index(), false);
                zombie0.bDressInRandomOutfit = true;
                ZombiePopulationManager.instance.sitAgainstWall(zombie0, square);
            }
        }

        @LuaMethod(
            name = "addZombiesEating",
            global = true
        )
        public void addZombiesEating(int x, int y, int z, int totalZombies, boolean skeletonBody) {
            IsoGridSquare square = IsoCell.getInstance().getGridSquare(x, y, z);
            if (square != null) {
                VirtualZombieManager.instance.choices.clear();
                VirtualZombieManager.instance.choices.add(square);
                IsoZombie zombie0 = VirtualZombieManager.instance.createRealZombieAlways(Rand.Next(8), false);
                zombie0.setX(square.x);
                zombie0.setY(square.y);
                zombie0.setFakeDead(false);
                zombie0.setHealth(0.0F);
                zombie0.upKillCount = false;
                if (!skeletonBody) {
                    zombie0.dressInRandomOutfit();

                    for (int int0 = 0; int0 < 10; int0++) {
                        zombie0.addHole(null);
                        zombie0.addBlood(null, false, true, false);
                    }

                    zombie0.DoZombieInventory();
                }

                zombie0.setSkeleton(skeletonBody);
                if (skeletonBody) {
                    zombie0.getHumanVisual().setSkinTextureIndex(2);
                }

                IsoDeadBody deadBody = new IsoDeadBody(zombie0, true);
                VirtualZombieManager.instance.createEatingZombies(deadBody, totalZombies);
            }
        }

        @LuaMethod(
            name = "addZombiesInOutfitArea",
            global = true
        )
        public ArrayList<IsoZombie> addZombiesInOutfitArea(int x1, int y1, int x2, int y2, int z, int totalZombies, String outfit, Integer femaleChance) {
            ArrayList arrayList = new ArrayList();

            for (int int0 = 0; int0 < totalZombies; int0++) {
                arrayList.addAll(addZombiesInOutfit(Rand.Next(x1, x2), Rand.Next(y1, y2), z, 1, outfit, femaleChance));
            }

            return arrayList;
        }

        @LuaMethod(
            name = "addZombiesInOutfit",
            global = true
        )
        public static ArrayList<IsoZombie> addZombiesInOutfit(int x, int y, int z, int totalZombies, String outfit, Integer femaleChance) {
            return addZombiesInOutfit(x, y, z, totalZombies, outfit, femaleChance, false, false, false, false, 1.0F);
        }

        @LuaMethod(
            name = "addZombiesInOutfit",
            global = true
        )
        public static ArrayList<IsoZombie> addZombiesInOutfit(
            int x,
            int y,
            int z,
            int totalZombies,
            String outfit,
            Integer femaleChance,
            boolean isCrawler,
            boolean isFallOnFront,
            boolean isFakeDead,
            boolean isKnockedDown,
            float health
        ) {
            ArrayList arrayList = new ArrayList();
            if (IsoWorld.getZombiesDisabled()) {
                return arrayList;
            } else {
                IsoGridSquare square = IsoCell.getInstance().getGridSquare(x, y, z);
                if (square == null) {
                    return arrayList;
                } else {
                    for (int int0 = 0; int0 < totalZombies; int0++) {
                        if (health <= 0.0F) {
                            square.getChunk().AddCorpses(x / 10, y / 10);
                        } else {
                            VirtualZombieManager.instance.choices.clear();
                            VirtualZombieManager.instance.choices.add(square);
                            IsoZombie zombie0 = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.getRandom().index(), false);
                            if (zombie0 != null) {
                                if (femaleChance != null) {
                                    zombie0.setFemaleEtc(Rand.Next(100) < femaleChance);
                                }

                                if (outfit != null) {
                                    zombie0.dressInPersistentOutfit(outfit);
                                    zombie0.bDressInRandomOutfit = false;
                                } else {
                                    zombie0.bDressInRandomOutfit = true;
                                }

                                zombie0.bLunger = true;
                                zombie0.setKnockedDown(isKnockedDown);
                                if (isCrawler) {
                                    zombie0.setCrawler(true);
                                    zombie0.setCanWalk(false);
                                    zombie0.setOnFloor(true);
                                    zombie0.setKnockedDown(true);
                                    zombie0.setCrawlerType(1);
                                    zombie0.DoZombieStats();
                                }

                                zombie0.setFakeDead(isFakeDead);
                                zombie0.setFallOnFront(isFallOnFront);
                                zombie0.setHealth(health);
                                arrayList.add(zombie0);
                            }
                        }
                    }

                    ZombieSpawnRecorder.instance.record(arrayList, LuaManager.GlobalObject.class.getSimpleName());
                    return arrayList;
                }
            }
        }

        @LuaMethod(
            name = "addZombiesInBuilding",
            global = true
        )
        public ArrayList<IsoZombie> addZombiesInBuilding(BuildingDef def, int totalZombies, String outfit, RoomDef room, Integer femaleChance) {
            boolean boolean0 = room == null;
            ArrayList arrayList = new ArrayList();
            if (IsoWorld.getZombiesDisabled()) {
                return arrayList;
            } else {
                if (room == null) {
                    room = def.getRandomRoom(6);
                }

                int int0 = 2;
                int int1 = room.area / 2;
                if (totalZombies == 0) {
                    if (SandboxOptions.instance.Zombies.getValue() == 1) {
                        int1 += 4;
                    } else if (SandboxOptions.instance.Zombies.getValue() == 2) {
                        int1 += 3;
                    } else if (SandboxOptions.instance.Zombies.getValue() == 3) {
                        int1 += 2;
                    } else if (SandboxOptions.instance.Zombies.getValue() == 5) {
                        int1 -= 4;
                    }

                    if (int1 > 8) {
                        int1 = 8;
                    }

                    if (int1 < int0) {
                        int1 = int0 + 1;
                    }
                } else {
                    int0 = totalZombies;
                    int1 = totalZombies;
                }

                int int2 = Rand.Next(int0, int1);

                for (int int3 = 0; int3 < int2; int3++) {
                    IsoGridSquare square = RandomizedBuildingBase.getRandomSpawnSquare(room);
                    if (square == null) {
                        break;
                    }

                    VirtualZombieManager.instance.choices.clear();
                    VirtualZombieManager.instance.choices.add(square);
                    IsoZombie zombie0 = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.getRandom().index(), false);
                    if (zombie0 != null) {
                        if (femaleChance != null) {
                            zombie0.setFemaleEtc(Rand.Next(100) < femaleChance);
                        }

                        if (outfit != null) {
                            zombie0.dressInPersistentOutfit(outfit);
                            zombie0.bDressInRandomOutfit = false;
                        } else {
                            zombie0.bDressInRandomOutfit = true;
                        }

                        arrayList.add(zombie0);
                        if (boolean0) {
                            room = def.getRandomRoom(6);
                        }
                    }
                }

                ZombieSpawnRecorder.instance.record(arrayList, this.getClass().getSimpleName());
                return arrayList;
            }
        }

        @LuaMethod(
            name = "addVehicleDebug",
            global = true
        )
        public static BaseVehicle addVehicleDebug(String scriptName, IsoDirections dir, Integer skinIndex, IsoGridSquare sq) {
            if (dir == null) {
                dir = IsoDirections.getRandom();
            }

            BaseVehicle vehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
            if (!StringUtils.isNullOrEmpty(scriptName)) {
                vehicle.setScriptName(scriptName);
                vehicle.setScript();
                if (skinIndex != null) {
                    vehicle.setSkinIndex(skinIndex);
                }
            }

            vehicle.setDir(dir);
            float float0 = dir.toAngle() + (float) Math.PI + Rand.Next(-0.2F, 0.2F);

            while (float0 > Math.PI * 2) {
                float0 = (float)(float0 - (Math.PI * 2));
            }

            vehicle.savedRot.setAngleAxis(float0, 0.0F, 1.0F, 0.0F);
            vehicle.jniTransform.setRotation(vehicle.savedRot);
            vehicle.setX(sq.x);
            vehicle.setY(sq.y);
            vehicle.setZ(sq.z);
            if (IsoChunk.doSpawnedVehiclesInInvalidPosition(vehicle)) {
                vehicle.setSquare(sq);
                sq.chunk.vehicles.add(vehicle);
                vehicle.chunk = sq.chunk;
                vehicle.addToWorld();
                VehiclesDB2.instance.addVehicle(vehicle);
            }

            vehicle.setGeneralPartCondition(1.3F, 10.0F);
            vehicle.rust = 0.0F;
            return vehicle;
        }

        @LuaMethod(
            name = "addVehicle",
            global = true
        )
        public static BaseVehicle addVehicle(String script) {
            if (!StringUtils.isNullOrWhitespace(script) && ScriptManager.instance.getVehicle(script) == null) {
                DebugLog.Lua.warn("No such vehicle script \"" + script + "\"");
                return null;
            } else {
                ArrayList arrayList = ScriptManager.instance.getAllVehicleScripts();
                if (arrayList.isEmpty()) {
                    DebugLog.Lua.warn("No vehicle scripts defined");
                    return null;
                } else {
                    WorldSimulation.instance.create();
                    BaseVehicle vehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
                    if (StringUtils.isNullOrWhitespace(script)) {
                        VehicleScript vehicleScript = PZArrayUtil.pickRandom(arrayList);
                        script = vehicleScript.getFullName();
                    }

                    vehicle.setScriptName(script);
                    vehicle.setX(IsoPlayer.getInstance().getX());
                    vehicle.setY(IsoPlayer.getInstance().getY());
                    vehicle.setZ(0.0F);
                    if (IsoChunk.doSpawnedVehiclesInInvalidPosition(vehicle)) {
                        vehicle.setSquare(IsoPlayer.getInstance().getSquare());
                        vehicle.square.chunk.vehicles.add(vehicle);
                        vehicle.chunk = vehicle.square.chunk;
                        vehicle.addToWorld();
                        VehiclesDB2.instance.addVehicle(vehicle);
                    } else {
                        DebugLog.Lua.error("ERROR: I can not spawn the vehicle. Invalid position. Try to change position.");
                    }

                    return null;
                }
            }
        }

        @LuaMethod(
            name = "attachTrailerToPlayerVehicle",
            global = true
        )
        public static void attachTrailerToPlayerVehicle(int playerIndex) {
            IsoPlayer player = IsoPlayer.players[playerIndex];
            IsoGridSquare square = player.getCurrentSquare();
            BaseVehicle vehicle0 = player.getVehicle();
            if (vehicle0 == null) {
                vehicle0 = addVehicleDebug("Base.OffRoad", IsoDirections.N, 0, square);
                vehicle0.repair();
                player.getInventory().AddItem(vehicle0.createVehicleKey());
            }

            square = IsoWorld.instance.CurrentCell.getGridSquare(square.x, square.y + 5, square.z);
            BaseVehicle vehicle1 = addVehicleDebug("Base.Trailer", IsoDirections.N, 0, square);
            vehicle1.repair();
            vehicle0.addPointConstraint(player, vehicle1, "trailer", "trailer");
        }

        @LuaMethod(
            name = "getKeyName",
            global = true
        )
        public static String getKeyName(int key) {
            return Input.getKeyName(key);
        }

        @LuaMethod(
            name = "getKeyCode",
            global = true
        )
        public static int getKeyCode(String keyName) {
            return Input.getKeyCode(keyName);
        }

        @LuaMethod(
            name = "queueCharEvent",
            global = true
        )
        public static void queueCharEvent(String eventChar) {
            RenderThread.queueInvokeOnRenderContext(() -> GameKeyboard.getEventQueuePolling().addCharEvent(eventChar.charAt(0)));
        }

        @LuaMethod(
            name = "queueKeyEvent",
            global = true
        )
        public static void queueKeyEvent(int lwjglKeyCode) {
            RenderThread.queueInvokeOnRenderContext(() -> {
                int int0 = KeyCodes.toGlfwKey(lwjglKeyCode);
                GameKeyboard.getEventQueuePolling().addKeyEvent(int0, 1);
                GameKeyboard.getEventQueuePolling().addKeyEvent(int0, 0);
            });
        }

        @LuaMethod(
            name = "addAllVehicles",
            global = true
        )
        public static void addAllVehicles() {
            addAllVehicles(vehicleScript -> !vehicleScript.getName().contains("Smashed") && !vehicleScript.getName().contains("Burnt"));
        }

        @LuaMethod(
            name = "addAllBurntVehicles",
            global = true
        )
        public static void addAllBurntVehicles() {
            addAllVehicles(vehicleScript -> vehicleScript.getName().contains("Burnt"));
        }

        @LuaMethod(
            name = "addAllSmashedVehicles",
            global = true
        )
        public static void addAllSmashedVehicles() {
            addAllVehicles(vehicleScript -> vehicleScript.getName().contains("Smashed"));
        }

        public static void addAllVehicles(Predicate<VehicleScript> predicate) {
            ArrayList arrayList = ScriptManager.instance.getAllVehicleScripts();
            Collections.sort(arrayList, Comparator.comparing(VehicleScript::getName));
            float float0 = IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMinTiles() + 5;
            float float1 = IsoPlayer.getInstance().getY();
            float float2 = 0.0F;

            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                VehicleScript vehicleScript = (VehicleScript)arrayList.get(int0);
                if (vehicleScript.getModel() != null
                    && predicate.test(vehicleScript)
                    && IsoWorld.instance.CurrentCell.getGridSquare((double)float0, (double)float1, (double)float2) != null) {
                    WorldSimulation.instance.create();
                    BaseVehicle vehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
                    vehicle.setScriptName(vehicleScript.getFullName());
                    vehicle.setX(float0);
                    vehicle.setY(float1);
                    vehicle.setZ(float2);
                    if (IsoChunk.doSpawnedVehiclesInInvalidPosition(vehicle)) {
                        vehicle.setSquare(IsoPlayer.getInstance().getSquare());
                        vehicle.square.chunk.vehicles.add(vehicle);
                        vehicle.chunk = vehicle.square.chunk;
                        vehicle.addToWorld();
                        VehiclesDB2.instance.addVehicle(vehicle);
                        IsoChunk.addFromCheckedVehicles(vehicle);
                    } else {
                        DebugLog.Lua.warn(vehicleScript.getName() + " not spawned, position invalid");
                    }

                    float0 += 4.0F;
                    if (float0 > IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMaxTiles() - 5) {
                        float0 = IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMinTiles() + 5;
                        float1 += 8.0F;
                    }
                }
            }
        }

        @LuaMethod(
            name = "addPhysicsObject",
            global = true
        )
        public static BaseVehicle addPhysicsObject() {
            MPStatistic.getInstance().Bullet.Start();
            int int0 = Bullet.addPhysicsObject(getPlayer().getX(), getPlayer().getY());
            MPStatistic.getInstance().Bullet.End();
            IsoPushableObject pushableObject = new IsoPushableObject(
                IsoWorld.instance.getCell(), IsoPlayer.getInstance().getCurrentSquare(), IsoSpriteManager.instance.getSprite("trashcontainers_01_16")
            );
            WorldSimulation.instance.physicsObjectMap.put(int0, pushableObject);
            return null;
        }

        @LuaMethod(
            name = "toggleVehicleRenderToTexture",
            global = true
        )
        public static void toggleVehicleRenderToTexture() {
            BaseVehicle.RENDER_TO_TEXTURE = !BaseVehicle.RENDER_TO_TEXTURE;
        }

        @LuaMethod(
            name = "reloadSoundFiles",
            global = true
        )
        public static void reloadSoundFiles() {
            try {
                for (String string : ZomboidFileSystem.instance.ActiveFileMap.keySet()) {
                    if (string.matches(".*/sounds_.+\\.txt")) {
                        GameSounds.ReloadFile(string);
                    }
                }
            } catch (Throwable throwable) {
                ExceptionLogger.logException(throwable);
            }
        }

        @LuaMethod(
            name = "getAnimationViewerState",
            global = true
        )
        public static AnimationViewerState getAnimationViewerState() {
            return AnimationViewerState.instance;
        }

        @LuaMethod(
            name = "getAttachmentEditorState",
            global = true
        )
        public static AttachmentEditorState getAttachmentEditorState() {
            return AttachmentEditorState.instance;
        }

        @LuaMethod(
            name = "getEditVehicleState",
            global = true
        )
        public static EditVehicleState getEditVehicleState() {
            return EditVehicleState.instance;
        }

        @LuaMethod(
            name = "showAnimationViewer",
            global = true
        )
        public static void showAnimationViewer() {
            IngameState.instance.showAnimationViewer = true;
        }

        @LuaMethod(
            name = "showAttachmentEditor",
            global = true
        )
        public static void showAttachmentEditor() {
            IngameState.instance.showAttachmentEditor = true;
        }

        @LuaMethod(
            name = "showChunkDebugger",
            global = true
        )
        public static void showChunkDebugger() {
            IngameState.instance.showChunkDebugger = true;
        }

        @LuaMethod(
            name = "showGlobalObjectDebugger",
            global = true
        )
        public static void showGlobalObjectDebugger() {
            IngameState.instance.showGlobalObjectDebugger = true;
        }

        @LuaMethod(
            name = "showVehicleEditor",
            global = true
        )
        public static void showVehicleEditor(String scriptName) {
            IngameState.instance.showVehicleEditor = StringUtils.isNullOrWhitespace(scriptName) ? "" : scriptName;
        }

        @LuaMethod(
            name = "showWorldMapEditor",
            global = true
        )
        public static void showWorldMapEditor(String value) {
            IngameState.instance.showWorldMapEditor = StringUtils.isNullOrWhitespace(value) ? "" : value;
        }

        @LuaMethod(
            name = "reloadVehicles",
            global = true
        )
        public static void reloadVehicles() {
            try {
                for (String string0 : ScriptManager.instance.scriptsWithVehicleTemplates) {
                    ScriptManager.instance.LoadFile(string0, true);
                }

                for (String string1 : ScriptManager.instance.scriptsWithVehicles) {
                    ScriptManager.instance.LoadFile(string1, true);
                }

                BaseVehicle.LoadAllVehicleTextures();

                for (BaseVehicle vehicle : IsoWorld.instance.CurrentCell.vehicles) {
                    vehicle.scriptReloaded();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        @LuaMethod(
            name = "reloadEngineRPM",
            global = true
        )
        public static void reloadEngineRPM() {
            try {
                ScriptManager.instance.LoadFile(ZomboidFileSystem.instance.getString("media/scripts/vehicles/engine_rpm.txt"), true);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        @LuaMethod(
            name = "proceedPM",
            global = true
        )
        public static String proceedPM(String command) {
            command = command.trim();
            String string = null;
            Object object = null;
            Matcher matcher = Pattern.compile("(\"[^\"]*\\s+[^\"]*\"|[^\"]\\S*)\\s(.+)").matcher(command);
            if (matcher.matches()) {
                string = matcher.group(1);
                object = matcher.group(2);
                string = string.replaceAll("\"", "");
                ChatManager.getInstance().sendWhisperMessage(string, (String)object);
                return string;
            } else {
                ChatManager.getInstance().addMessage("Error", getText("IGUI_Commands_Whisper"));
                return "";
            }
        }

        @LuaMethod(
            name = "processSayMessage",
            global = true
        )
        public static void processSayMessage(String message) {
            if (message != null && !message.isEmpty()) {
                message = message.trim();
                ChatManager.getInstance().sendMessageToChat(ChatType.say, message);
            }
        }

        @LuaMethod(
            name = "processGeneralMessage",
            global = true
        )
        public static void processGeneralMessage(String message) {
            if (message != null && !message.isEmpty()) {
                message = message.trim();
                ChatManager.getInstance().sendMessageToChat(ChatType.general, message);
            }
        }

        @LuaMethod(
            name = "processShoutMessage",
            global = true
        )
        public static void processShoutMessage(String message) {
            if (message != null && !message.isEmpty()) {
                message = message.trim();
                ChatManager.getInstance().sendMessageToChat(ChatType.shout, message);
            }
        }

        @LuaMethod(
            name = "proceedFactionMessage",
            global = true
        )
        public static void ProceedFactionMessage(String message) {
            if (message != null && !message.isEmpty()) {
                message = message.trim();
                ChatManager.getInstance().sendMessageToChat(ChatType.faction, message);
            }
        }

        @LuaMethod(
            name = "processSafehouseMessage",
            global = true
        )
        public static void ProcessSafehouseMessage(String message) {
            if (message != null && !message.isEmpty()) {
                message = message.trim();
                ChatManager.getInstance().sendMessageToChat(ChatType.safehouse, message);
            }
        }

        @LuaMethod(
            name = "processAdminChatMessage",
            global = true
        )
        public static void ProcessAdminChatMessage(String message) {
            if (message != null && !message.isEmpty()) {
                message = message.trim();
                ChatManager.getInstance().sendMessageToChat(ChatType.admin, message);
            }
        }

        @LuaMethod(
            name = "showWrongChatTabMessage",
            global = true
        )
        public static void showWrongChatTabMessage(int actualTabID, int rightTabID, String chatCommand) {
            String string0 = ChatManager.getInstance().getTabName((short)actualTabID);
            String string1 = ChatManager.getInstance().getTabName((short)rightTabID);
            String string2 = Translator.getText("UI_chat_wrong_tab", string0, string1, chatCommand);
            ChatManager.getInstance().showServerChatMessage(string2);
        }

        @LuaMethod(
            name = "focusOnTab",
            global = true
        )
        public static void focusOnTab(Short id) {
            ChatManager.getInstance().focusOnTab(id);
        }

        @LuaMethod(
            name = "updateChatSettings",
            global = true
        )
        public static void updateChatSettings(String fontSize, boolean showTimestamp, boolean showTitle) {
            ChatManager.getInstance().updateChatSettings(fontSize, showTimestamp, showTitle);
        }

        @LuaMethod(
            name = "checkPlayerCanUseChat",
            global = true
        )
        public static Boolean checkPlayerCanUseChat(String chatCommand) {
            chatCommand = chatCommand.trim();
            ChatType chatType;
            switch (chatCommand) {
                case "/all":
                    chatType = ChatType.general;
                    break;
                case "/a":
                case "/admin":
                    chatType = ChatType.admin;
                    break;
                case "/s":
                case "/say":
                    chatType = ChatType.say;
                    break;
                case "/y":
                case "/yell":
                    chatType = ChatType.shout;
                    break;
                case "/f":
                case "/faction":
                    chatType = ChatType.faction;
                    break;
                case "/sh":
                case "/safehouse":
                    chatType = ChatType.safehouse;
                    break;
                case "/w":
                case "/whisper":
                    chatType = ChatType.whisper;
                    break;
                case "/radio":
                case "/r":
                    chatType = ChatType.radio;
                    break;
                default:
                    chatType = ChatType.notDefined;
                    DebugLog.Lua.warn("Chat command not found");
            }

            return ChatManager.getInstance().isPlayerCanUseChat(chatType);
        }

        @LuaMethod(
            name = "reloadVehicleTextures",
            global = true
        )
        public static void reloadVehicleTextures(String scriptName) {
            VehicleScript vehicleScript = ScriptManager.instance.getVehicle(scriptName);
            if (vehicleScript == null) {
                DebugLog.Lua.warn("no such vehicle script");
            } else {
                for (int int0 = 0; int0 < vehicleScript.getSkinCount(); int0++) {
                    VehicleScript.Skin skin = vehicleScript.getSkin(int0);
                    if (skin.texture != null) {
                        Texture.reload("media/textures/" + skin.texture + ".png");
                    }

                    if (skin.textureRust != null) {
                        Texture.reload("media/textures/" + skin.textureRust + ".png");
                    }

                    if (skin.textureMask != null) {
                        Texture.reload("media/textures/" + skin.textureMask + ".png");
                    }

                    if (skin.textureLights != null) {
                        Texture.reload("media/textures/" + skin.textureLights + ".png");
                    }

                    if (skin.textureDamage1Overlay != null) {
                        Texture.reload("media/textures/" + skin.textureDamage1Overlay + ".png");
                    }

                    if (skin.textureDamage1Shell != null) {
                        Texture.reload("media/textures/" + skin.textureDamage1Shell + ".png");
                    }

                    if (skin.textureDamage2Overlay != null) {
                        Texture.reload("media/textures/" + skin.textureDamage2Overlay + ".png");
                    }

                    if (skin.textureDamage2Shell != null) {
                        Texture.reload("media/textures/" + skin.textureDamage2Shell + ".png");
                    }

                    if (skin.textureShadow != null) {
                        Texture.reload("media/textures/" + skin.textureShadow + ".png");
                    }
                }
            }
        }

        @LuaMethod(
            name = "useStaticErosionRand",
            global = true
        )
        public static void useStaticErosionRand(boolean use) {
            ErosionData.staticRand = use;
        }

        @LuaMethod(
            name = "getClimateManager",
            global = true
        )
        public static ClimateManager getClimateManager() {
            return ClimateManager.getInstance();
        }

        @LuaMethod(
            name = "getClimateMoon",
            global = true
        )
        public static ClimateMoon getClimateMoon() {
            return ClimateMoon.getInstance();
        }

        @LuaMethod(
            name = "getWorldMarkers",
            global = true
        )
        public static WorldMarkers getWorldMarkers() {
            return WorldMarkers.instance;
        }

        @LuaMethod(
            name = "getIsoMarkers",
            global = true
        )
        public static IsoMarkers getIsoMarkers() {
            return IsoMarkers.instance;
        }

        @LuaMethod(
            name = "getErosion",
            global = true
        )
        public static ErosionMain getErosion() {
            return ErosionMain.getInstance();
        }

        @LuaMethod(
            name = "getAllOutfits",
            global = true
        )
        public static ArrayList<String> getAllOutfits(boolean female) {
            ArrayList arrayList = new ArrayList();
            ModelManager.instance.create();
            if (OutfitManager.instance == null) {
                return arrayList;
            } else {
                for (Outfit outfit : female ? OutfitManager.instance.m_FemaleOutfits : OutfitManager.instance.m_MaleOutfits) {
                    arrayList.add(outfit.m_Name);
                }

                Collections.sort(arrayList);
                return arrayList;
            }
        }

        @LuaMethod(
            name = "getAllVehicles",
            global = true
        )
        public static ArrayList<String> getAllVehicles() {
            return ScriptManager.instance
                .getAllVehicleScripts()
                .stream()
                .map(VehicleScript::getFullName)
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));
        }

        @LuaMethod(
            name = "getAllHairStyles",
            global = true
        )
        public static ArrayList<String> getAllHairStyles(boolean female) {
            ArrayList arrayList0 = new ArrayList();
            if (HairStyles.instance == null) {
                return arrayList0;
            } else {
                ArrayList arrayList1 = new ArrayList<>(female ? HairStyles.instance.m_FemaleStyles : HairStyles.instance.m_MaleStyles);
                arrayList1.sort((hairStyle0, hairStyle1) -> {
                    if (hairStyle0.name.isEmpty()) {
                        return -1;
                    } else if (hairStyle1.name.isEmpty()) {
                        return 1;
                    } else {
                        String string0 = getText("IGUI_Hair_" + hairStyle0.name);
                        String string1 = getText("IGUI_Hair_" + hairStyle1.name);
                        return string0.compareTo(string1);
                    }
                });

                for (HairStyle hairStyle : arrayList1) {
                    arrayList0.add(hairStyle.name);
                }

                return arrayList0;
            }
        }

        @LuaMethod(
            name = "getHairStylesInstance",
            global = true
        )
        public static HairStyles getHairStylesInstance() {
            return HairStyles.instance;
        }

        @LuaMethod(
            name = "getBeardStylesInstance",
            global = true
        )
        public static BeardStyles getBeardStylesInstance() {
            return BeardStyles.instance;
        }

        @LuaMethod(
            name = "getAllBeardStyles",
            global = true
        )
        public static ArrayList<String> getAllBeardStyles() {
            ArrayList arrayList0 = new ArrayList();
            if (BeardStyles.instance == null) {
                return arrayList0;
            } else {
                ArrayList arrayList1 = new ArrayList<>(BeardStyles.instance.m_Styles);
                arrayList1.sort((beardStyle0, beardStyle1) -> {
                    if (beardStyle0.name.isEmpty()) {
                        return -1;
                    } else if (beardStyle1.name.isEmpty()) {
                        return 1;
                    } else {
                        String string0 = getText("IGUI_Beard_" + beardStyle0.name);
                        String string1 = getText("IGUI_Beard_" + beardStyle1.name);
                        return string0.compareTo(string1);
                    }
                });

                for (BeardStyle beardStyle : arrayList1) {
                    arrayList0.add(beardStyle.name);
                }

                return arrayList0;
            }
        }

        @LuaMethod(
            name = "getAllItemsForBodyLocation",
            global = true
        )
        public static KahluaTable getAllItemsForBodyLocation(String bodyLocation) {
            KahluaTable table = LuaManager.platform.newTable();
            if (StringUtils.isNullOrWhitespace(bodyLocation)) {
                return table;
            } else {
                int int0 = 1;

                for (Item item : ScriptManager.instance.getAllItems()) {
                    if (!StringUtils.isNullOrWhitespace(item.getClothingItem())
                        && (bodyLocation.equals(item.getBodyLocation()) || bodyLocation.equals(item.CanBeEquipped))) {
                        table.rawset(int0++, item.getFullName());
                    }
                }

                return table;
            }
        }

        @LuaMethod(
            name = "getAllDecalNamesForItem",
            global = true
        )
        public static ArrayList<String> getAllDecalNamesForItem(InventoryItem item) {
            ArrayList arrayList = new ArrayList();
            if (item != null && ClothingDecals.instance != null) {
                ClothingItem clothingItem = item.getClothingItem();
                if (clothingItem == null) {
                    return arrayList;
                } else {
                    String string = clothingItem.getDecalGroup();
                    if (StringUtils.isNullOrWhitespace(string)) {
                        return arrayList;
                    } else {
                        ClothingDecalGroup clothingDecalGroup = ClothingDecals.instance.FindGroup(string);
                        if (clothingDecalGroup == null) {
                            return arrayList;
                        } else {
                            clothingDecalGroup.getDecals(arrayList);
                            return arrayList;
                        }
                    }
                }
            } else {
                return arrayList;
            }
        }

        @LuaMethod(
            name = "screenZoomIn",
            global = true
        )
        public void screenZoomIn() {
        }

        @LuaMethod(
            name = "screenZoomOut",
            global = true
        )
        public void screenZoomOut() {
        }

        @LuaMethod(
            name = "addSound",
            global = true
        )
        public void addSound(IsoObject source, int x, int y, int z, int radius, int volume) {
            WorldSoundManager.instance.addSound(source, x, y, z, radius, volume);
        }

        @LuaMethod(
            name = "sendAddXp",
            global = true
        )
        public void sendAddXp(IsoPlayer player, PerkFactory.Perk perk, int amount) {
            if (GameClient.bClient && player.isExistInTheWorld()) {
                GameClient.instance.sendAddXp(player, perk, amount);
            }
        }

        /**
         * Sends an XP sync packet. Does nothing when called on the server.
         * 
         * @param player The player whose XP to sync.
         */
        @LuaMethod(
            name = "SyncXp",
            global = true
        )
        public void SyncXp(IsoPlayer player) {
            if (GameClient.bClient) {
                GameClient.instance.sendSyncXp(player);
            }
        }

        @LuaMethod(
            name = "checkServerName",
            global = true
        )
        public String checkServerName(String name) {
            String string = ProfanityFilter.getInstance().validateString(name, true, true, true);
            return !StringUtils.isNullOrEmpty(string) ? Translator.getText("UI_BadWordCheck", string) : null;
        }

        /**
         * Draws an item's model in the world. Only works when certain render state is set.
         * 
         * @param item The item to render.
         * @param sq The square to draw the item on.
         * @param xoffset Offset on the x axis to draw the model.
         * @param yoffset Offset on the y axis to draw the model.
         * @param zoffset Offset on the z axis to draw the model.
         * @param rotation Yaw rotation of the model in degrees.
         */
        @LuaMethod(
            name = "Render3DItem",
            global = true
        )
        public void Render3DItem(InventoryItem item, IsoGridSquare sq, float xoffset, float yoffset, float zoffset, float rotation) {
            WorldItemModelDrawer.renderMain(item, sq, xoffset, yoffset, zoffset, 0.0F, rotation);
        }

        @LuaMethod(
            name = "getContainerOverlays",
            global = true
        )
        public ContainerOverlays getContainerOverlays() {
            return ContainerOverlays.instance;
        }

        @LuaMethod(
            name = "getTileOverlays",
            global = true
        )
        public TileOverlays getTileOverlays() {
            return TileOverlays.instance;
        }

        @LuaMethod(
            name = "getAverageFPS",
            global = true
        )
        public Double getAverageFSP() {
            float float0 = GameWindow.averageFPS;
            if (!PerformanceSettings.isUncappedFPS()) {
                float0 = Math.min(float0, (float)PerformanceSettings.getLockFPS());
            }

            return BoxedStaticValues.toDouble(Math.floor(float0));
        }

        @LuaMethod(
            name = "createItemTransaction",
            global = true
        )
        public static void createItemTransaction(InventoryItem item, ItemContainer src, ItemContainer dst) {
            if (GameClient.bClient && item != null) {
                int int0 = Optional.ofNullable(src).map(ItemContainer::getContainingItem).map(InventoryItem::getID).orElse(-1);
                int int1 = Optional.ofNullable(dst).map(ItemContainer::getContainingItem).map(InventoryItem::getID).orElse(-1);
                ItemTransactionManager.createItemTransaction(item.getID(), int0, int1);
            }
        }

        @LuaMethod(
            name = "removeItemTransaction",
            global = true
        )
        public static void removeItemTransaction(InventoryItem item, ItemContainer src, ItemContainer dst) {
            if (GameClient.bClient && item != null) {
                int int0 = Optional.ofNullable(src).map(ItemContainer::getContainingItem).map(InventoryItem::getID).orElse(-1);
                int int1 = Optional.ofNullable(dst).map(ItemContainer::getContainingItem).map(InventoryItem::getID).orElse(-1);
                ItemTransactionManager.removeItemTransaction(item.getID(), int0, int1);
            }
        }

        @LuaMethod(
            name = "isItemTransactionConsistent",
            global = true
        )
        public static boolean isItemTransactionConsistent(InventoryItem item, ItemContainer src, ItemContainer dst) {
            if (GameClient.bClient && item != null) {
                int int0 = Optional.ofNullable(src).map(ItemContainer::getContainingItem).map(InventoryItem::getID).orElse(-1);
                int int1 = Optional.ofNullable(dst).map(ItemContainer::getContainingItem).map(InventoryItem::getID).orElse(-1);
                return ItemTransactionManager.isConsistent(item.getID(), int0, int1);
            } else {
                return true;
            }
        }

        @LuaMethod(
            name = "getServerStatistic",
            global = true
        )
        public static KahluaTable getServerStatistic() {
            return MPStatistic.getInstance().getStatisticTableForLua();
        }

        @LuaMethod(
            name = "setServerStatisticEnable",
            global = true
        )
        public static void setServerStatisticEnable(boolean enable) {
            if (GameClient.bClient) {
                GameClient.setServerStatisticEnable(enable);
            }
        }

        @LuaMethod(
            name = "getServerStatisticEnable",
            global = true
        )
        public static boolean getServerStatisticEnable() {
            return GameClient.bClient ? GameClient.getServerStatisticEnable() : false;
        }

        @LuaMethod(
            name = "checkModsNeedUpdate",
            global = true
        )
        public static void checkModsNeedUpdate(UdpConnection udpConnection) {
            DebugLog.log("CheckModsNeedUpdate: Checking...");
            if (SteamUtils.isSteamModeEnabled() && isServer()) {
                ArrayList arrayList = getSteamWorkshopItemIDs();
                new LuaManager.GlobalObject.ItemQueryJava(arrayList, udpConnection);
            }
        }

        @LuaMethod(
            name = "getSearchMode",
            global = true
        )
        public static SearchMode getSearchMode() {
            return SearchMode.getInstance();
        }

        @LuaMethod(
            name = "timSort",
            global = true
        )
        public static void timSort(KahluaTable table, Object functionObject) {
            KahluaTableImpl kahluaTableImpl = Type.tryCastTo(table, KahluaTableImpl.class);
            if (kahluaTableImpl != null && kahluaTableImpl.len() >= 2 && functionObject != null) {
                timSortComparator.comp = functionObject;
                Object[] objects = kahluaTableImpl.delegate.values().toArray();
                Arrays.sort(objects, timSortComparator);

                for (int int0 = 0; int0 < objects.length; int0++) {
                    kahluaTableImpl.rawset(int0 + 1, objects[int0]);
                    objects[int0] = null;
                }
            }
        }

        private static final class ItemQuery implements ISteamWorkshopCallback {
            private LuaClosure functionObj;
            private Object arg1;
            private long handle;

            public ItemQuery(ArrayList<String> arrayList, LuaClosure luaClosure, Object object) {
                this.functionObj = luaClosure;
                this.arg1 = object;
                long[] longs = new long[arrayList.size()];
                int int0 = 0;

                for (int int1 = 0; int1 < arrayList.size(); int1++) {
                    long long0 = SteamUtils.convertStringToSteamID((String)arrayList.get(int1));
                    if (long0 != -1L) {
                        longs[int0++] = long0;
                    }
                }

                this.handle = SteamWorkshop.instance.CreateQueryUGCDetailsRequest(longs, this);
                if (this.handle == 0L) {
                    SteamWorkshop.instance.RemoveCallback(this);
                    if (object == null) {
                        LuaManager.caller.pcall(LuaManager.thread, luaClosure, "NotCompleted");
                    } else {
                        LuaManager.caller.pcall(LuaManager.thread, luaClosure, object, "NotCompleted");
                    }
                }
            }

            @Override
            public void onItemCreated(long var1, boolean var3) {
            }

            @Override
            public void onItemNotCreated(int var1) {
            }

            @Override
            public void onItemUpdated(boolean var1) {
            }

            @Override
            public void onItemNotUpdated(int var1) {
            }

            @Override
            public void onItemSubscribed(long var1) {
            }

            @Override
            public void onItemNotSubscribed(long var1, int var3) {
            }

            @Override
            public void onItemDownloaded(long var1) {
            }

            @Override
            public void onItemNotDownloaded(long var1, int var3) {
            }

            @Override
            public void onItemQueryCompleted(long long0, int int1) {
                if (long0 == this.handle) {
                    SteamWorkshop.instance.RemoveCallback(this);
                    ArrayList arrayList = new ArrayList();

                    for (int int0 = 0; int0 < int1; int0++) {
                        SteamUGCDetails steamUGCDetails = SteamWorkshop.instance.GetQueryUGCResult(long0, int0);
                        if (steamUGCDetails != null) {
                            arrayList.add(steamUGCDetails);
                        }
                    }

                    SteamWorkshop.instance.ReleaseQueryUGCRequest(long0);
                    if (this.arg1 == null) {
                        LuaManager.caller.pcall(LuaManager.thread, this.functionObj, "Completed", arrayList);
                    } else {
                        LuaManager.caller.pcall(LuaManager.thread, this.functionObj, this.arg1, "Completed", arrayList);
                    }
                }
            }

            @Override
            public void onItemQueryNotCompleted(long long0, int var3) {
                if (long0 == this.handle) {
                    SteamWorkshop.instance.RemoveCallback(this);
                    SteamWorkshop.instance.ReleaseQueryUGCRequest(long0);
                    if (this.arg1 == null) {
                        LuaManager.caller.pcall(LuaManager.thread, this.functionObj, "NotCompleted");
                    } else {
                        LuaManager.caller.pcall(LuaManager.thread, this.functionObj, this.arg1, "NotCompleted");
                    }
                }
            }
        }

        private static final class ItemQueryJava implements ISteamWorkshopCallback {
            private long handle;
            private UdpConnection connection;

            public ItemQueryJava(ArrayList<String> arrayList, UdpConnection udpConnection) {
                this.connection = udpConnection;
                long[] longs = new long[arrayList.size()];
                int int0 = 0;

                for (int int1 = 0; int1 < arrayList.size(); int1++) {
                    long long0 = SteamUtils.convertStringToSteamID((String)arrayList.get(int1));
                    if (long0 != -1L) {
                        longs[int0++] = long0;
                    }
                }

                this.handle = SteamWorkshop.instance.CreateQueryUGCDetailsRequest(longs, this);
                if (this.handle == 0L) {
                    SteamWorkshop.instance.RemoveCallback(this);
                    this.inform("CheckModsNeedUpdate: Check not completed");
                }
            }

            private void inform(String string) {
                if (this.connection != null) {
                    ChatServer.getInstance().sendMessageToServerChat(this.connection, string);
                }

                DebugLog.log(string);
            }

            @Override
            public void onItemCreated(long var1, boolean var3) {
            }

            @Override
            public void onItemNotCreated(int var1) {
            }

            @Override
            public void onItemUpdated(boolean var1) {
            }

            @Override
            public void onItemNotUpdated(int var1) {
            }

            @Override
            public void onItemSubscribed(long var1) {
            }

            @Override
            public void onItemNotSubscribed(long var1, int var3) {
            }

            @Override
            public void onItemDownloaded(long var1) {
            }

            @Override
            public void onItemNotDownloaded(long var1, int var3) {
            }

            @Override
            public void onItemQueryCompleted(long long0, int int1) {
                if (long0 == this.handle) {
                    SteamWorkshop.instance.RemoveCallback(this);

                    for (int int0 = 0; int0 < int1; int0++) {
                        SteamUGCDetails steamUGCDetails = SteamWorkshop.instance.GetQueryUGCResult(long0, int0);
                        if (steamUGCDetails != null) {
                            long long1 = steamUGCDetails.getID();
                            long long2 = SteamWorkshop.instance.GetItemState(long1);
                            if (SteamWorkshopItem.ItemState.Installed.and(long2)
                                && SteamWorkshopItem.ItemState.NeedsUpdate.not(long2)
                                && steamUGCDetails.getTimeCreated() != 0L
                                && steamUGCDetails.getTimeUpdated() != SteamWorkshop.instance.GetItemInstallTimeStamp(long1)) {
                                long2 |= SteamWorkshopItem.ItemState.NeedsUpdate.getValue();
                            }

                            if (SteamWorkshopItem.ItemState.NeedsUpdate.and(long2)) {
                                this.inform("CheckModsNeedUpdate: Mods need update");
                                SteamWorkshop.instance.ReleaseQueryUGCRequest(long0);
                                return;
                            }
                        }
                    }

                    this.inform("CheckModsNeedUpdate: Mods updated");
                    SteamWorkshop.instance.ReleaseQueryUGCRequest(long0);
                }
            }

            @Override
            public void onItemQueryNotCompleted(long long0, int var3) {
                if (long0 == this.handle) {
                    SteamWorkshop.instance.RemoveCallback(this);
                    SteamWorkshop.instance.ReleaseQueryUGCRequest(long0);
                    this.inform("CheckModsNeedUpdate: Check not completed");
                }
            }
        }

        public static final class LuaFileWriter {
            private final PrintWriter writer;

            public LuaFileWriter(PrintWriter _writer) {
                this.writer = _writer;
            }

            public void write(String str) throws IOException {
                this.writer.write(str);
            }

            public void writeln(String str) throws IOException {
                this.writer.write(str);
                this.writer.write(System.lineSeparator());
            }

            public void close() throws IOException {
                this.writer.close();
            }
        }

        private static final class TimSortComparator implements Comparator<Object> {
            Object comp;

            @Override
            public int compare(Object arg0, Object arg1) {
                if (Objects.equals(arg0, arg1)) {
                    return 0;
                } else {
                    Boolean boolean0 = LuaManager.thread.pcallBoolean(this.comp, arg0, arg1);
                    return boolean0 == Boolean.TRUE ? -1 : 1;
                }
            }
        }
    }
}
