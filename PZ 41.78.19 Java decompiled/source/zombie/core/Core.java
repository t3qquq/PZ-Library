// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core;

import fmod.FMOD_DriverInfo;
import fmod.javafmod;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.glu.GLU;
import org.lwjglx.LWJGLException;
import org.lwjglx.input.Controller;
import org.lwjglx.input.Keyboard;
import org.lwjglx.opengl.Display;
import org.lwjglx.opengl.DisplayMode;
import org.lwjglx.opengl.OpenGLException;
import org.lwjglx.opengl.PixelFormat;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameSounds;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.MovingObjectUpdateScheduler;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.ZomboidFileSystem;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaHookManager;
import zombie.Lua.LuaManager;
import zombie.Lua.MapObjects;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorFactory;
import zombie.characters.AttachedItems.AttachedLocations;
import zombie.characters.WornItems.BodyLocations;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.skills.CustomPerks;
import zombie.characters.skills.PerkFactory;
import zombie.characters.traits.TraitFactory;
import zombie.core.VBO.GLVertexBufferObject;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.opengl.PZGLUtil;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.raknet.VoiceManager;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.advancedanimation.AnimationSet;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.core.skinnedmodel.population.ClothingDecals;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.sprite.SpriteRenderState;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.MultiTextureFBO2;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureFBO;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.gameStates.ChooseGameInfo;
import zombie.gameStates.IngameState;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.input.Mouse;
import zombie.iso.BentFences;
import zombie.iso.BrokenFences;
import zombie.iso.ContainerOverlays;
import zombie.iso.IsoCamera;
import zombie.iso.IsoPuddles;
import zombie.iso.IsoWater;
import zombie.iso.PlayerCamera;
import zombie.iso.TileOverlays;
import zombie.iso.weather.WeatherShader;
import zombie.modding.ActiveMods;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.sandbox.CustomSandboxOptions;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.ui.FPSGraph;
import zombie.ui.ObjectTooltip;
import zombie.ui.TextManager;
import zombie.ui.UIManager;
import zombie.ui.UITextBox2;
import zombie.util.StringUtils;
import zombie.vehicles.VehicleType;
import zombie.worldMap.WorldMap;

public final class Core {
    public static final boolean bDemo = false;
    public static boolean bTutorial;
    private static boolean fakefullscreen = false;
    private static final GameVersion gameVersion = new GameVersion(41, 78, "");
    private static final int buildVersion = 19;
    public String steamServerVersion = "1.0.0.0";
    public static boolean bAltMoveMethod = false;
    private boolean rosewoodSpawnDone = false;
    private final ColorInfo objectHighlitedColor = new ColorInfo(0.98F, 0.56F, 0.11F, 1.0F);
    private final ColorInfo goodHighlitedColor = new ColorInfo(0.0F, 1.0F, 0.0F, 1.0F);
    private final ColorInfo badHighlitedColor = new ColorInfo(1.0F, 0.0F, 0.0F, 1.0F);
    private boolean flashIsoCursor = false;
    private int isoCursorVisibility = 5;
    public static boolean OptionShowCursorWhileAiming = false;
    private boolean collideZombies = true;
    public final MultiTextureFBO2 OffscreenBuffer = new MultiTextureFBO2();
    private String saveFolder = null;
    public static boolean OptionZoom = true;
    public static boolean OptionModsEnabled = true;
    public static int OptionFontSize = 1;
    public static String OptionContextMenuFont = "Medium";
    public static String OptionInventoryFont = "Medium";
    private static int OptionInventoryContainerSize = 1;
    public static String OptionTooltipFont = "Small";
    public static String OptionMeasurementFormat = "Metric";
    public static int OptionClockFormat = 1;
    public static int OptionClockSize = 2;
    public static boolean OptionClock24Hour = true;
    public static boolean OptionVSync = false;
    public static int OptionSoundVolume = 8;
    public static int OptionMusicVolume = 6;
    public static int OptionAmbientVolume = 5;
    public static int OptionJumpScareVolume = 10;
    public static int OptionMusicActionStyle = 0;
    public static int OptionMusicLibrary = 1;
    public static boolean OptionVoiceEnable = true;
    public static int OptionVoiceMode = 3;
    public static int OptionVoiceVADMode = 3;
    public static int OptionVoiceAGCMode = 2;
    public static String OptionVoiceRecordDeviceName = "";
    public static int OptionVoiceVolumeMic = 10;
    public static int OptionVoiceVolumePlayers = 5;
    public static int OptionVehicleEngineVolume = 5;
    public static int OptionReloadDifficulty = 2;
    public static boolean OptionRackProgress = true;
    public static int OptionBloodDecals = 10;
    public static boolean OptionBorderlessWindow = false;
    public static boolean OptionLockCursorToWindow = false;
    public static boolean OptionTextureCompression = true;
    public static boolean OptionModelTextureMipmaps = false;
    public static boolean OptionTexture2x = true;
    private static int OptionMaxTextureSize = 1;
    private static int OptionMaxVehicleTextureSize = 2;
    private static String OptionZoomLevels1x = "";
    private static String OptionZoomLevels2x = "";
    public static boolean OptionEnableContentTranslations = true;
    public static boolean OptionUIFBO = true;
    public static int OptionUIRenderFPS = 20;
    public static boolean OptionRadialMenuKeyToggle = true;
    public static boolean OptionReloadRadialInstant = false;
    public static boolean OptionPanCameraWhileAiming = true;
    public static boolean OptionPanCameraWhileDriving = false;
    public static boolean OptionShowChatTimestamp = false;
    public static boolean OptionShowChatTitle = false;
    public static String OptionChatFontSize = "medium";
    public static float OptionMinChatOpaque = 1.0F;
    public static float OptionMaxChatOpaque = 1.0F;
    public static float OptionChatFadeTime = 0.0F;
    public static boolean OptionChatOpaqueOnFocus = true;
    public static boolean OptionTemperatureDisplayCelsius = false;
    public static boolean OptionDoWindSpriteEffects = true;
    public static boolean OptionDoDoorSpriteEffects = true;
    public static boolean OptionDoContainerOutline = true;
    public static boolean OptionRenderPrecipIndoors = true;
    public static boolean OptionAutoProneAtk = true;
    public static boolean Option3DGroundItem = true;
    public static int OptionRenderPrecipitation = 1;
    public static boolean OptionUpdateSneakButton = true;
    public static boolean OptiondblTapJogToSprint = false;
    private static int OptionAimOutline = 2;
    private static String OptionCycleContainerKey = "shift";
    private static boolean OptionDropItemsOnSquareCenter = false;
    private static boolean OptionTimedActionGameSpeedReset = false;
    private static int OptionShoulderButtonContainerSwitch = 1;
    private static boolean OptionProgressBar = false;
    private static String OptionLanguageName = null;
    private static final boolean[] OptionSingleContextMenu = new boolean[4];
    private static boolean OptionCorpseShadows = true;
    private static int OptionSimpleClothingTextures = 1;
    private static boolean OptionSimpleWeaponTextures = false;
    private static boolean OptionAutoDrink = true;
    private static boolean OptionLeaveKeyInIgnition = false;
    private static boolean OptionAutoWalkContainer = false;
    private static int OptionSearchModeOverlayEffect = 1;
    private static int OptionIgnoreProneZombieRange = 2;
    private static boolean OptionShowItemModInfo = true;
    private boolean OptionShowSurvivalGuide = true;
    private static boolean OptionEnableLeftJoystickRadialMenu = true;
    private boolean showPing = true;
    private boolean forceSnow = false;
    private boolean zombieGroupSound = true;
    private String blinkingMoodle = null;
    private boolean tutorialDone = false;
    private boolean vehiclesWarningShow = false;
    private String poisonousBerry = null;
    private String poisonousMushroom = null;
    private boolean doneNewSaveFolder = false;
    private static String difficulty = "Hardcore";
    public static int TileScale = 2;
    private boolean isSelectingAll = false;
    private boolean showYourUsername = true;
    private ColorInfo mpTextColor = null;
    private boolean isAzerty = false;
    private String seenUpdateText = "";
    private boolean toggleToAim = false;
    private boolean toggleToRun = false;
    private boolean toggleToSprint = true;
    private boolean celsius = false;
    private boolean riversideDone = false;
    private boolean noSave = false;
    private boolean showFirstTimeVehicleTutorial = false;
    private boolean showFirstTimeWeatherTutorial = false;
    private boolean showFirstTimeSneakTutorial = true;
    private boolean showFirstTimeSearchTutorial = true;
    private int termsOfServiceVersion = -1;
    private boolean newReloading = true;
    private boolean gotNewBelt = false;
    private boolean bAnimPopupDone = false;
    private boolean bModsPopupDone = false;
    public static float blinkAlpha = 1.0F;
    public static boolean blinkAlphaIncrease = false;
    private boolean bLoadedOptions = false;
    private static final HashMap<String, Object> optionsOnStartup = new HashMap<>();
    private boolean bChallenge;
    public static int width = 1280;
    public static int height = 720;
    public static int MaxJukeBoxesActive = 10;
    public static int NumJukeBoxesActive = 0;
    public static String GameMode = "Sandbox";
    private static String glVersion;
    private static int glMajorVersion = -1;
    private static final Core core = new Core();
    public static boolean bDebug = false;
    public static UITextBox2 CurrentTextEntryBox = null;
    public Shader RenderShader;
    private Map<String, Integer> keyMaps = null;
    public final boolean bUseShaders = true;
    private int iPerfSkybox = 1;
    private int iPerfSkybox_new = 1;
    public static final int iPerfSkybox_High = 0;
    public static final int iPerfSkybox_Medium = 1;
    public static final int iPerfSkybox_Static = 2;
    private int iPerfPuddles = 0;
    private int iPerfPuddles_new = 0;
    public static final int iPerfPuddles_None = 3;
    public static final int iPerfPuddles_GroundOnly = 2;
    public static final int iPerfPuddles_GroundWithRuts = 1;
    public static final int iPerfPuddles_All = 0;
    private boolean bPerfReflections = true;
    private boolean bPerfReflections_new = true;
    public int vidMem = 3;
    private boolean bSupportsFBO = true;
    public float UIRenderAccumulator = 0.0F;
    public boolean UIRenderThisFrame = true;
    public int version = 1;
    public int fileversion = 7;
    private static boolean fullScreen = false;
    private static final boolean[] bAutoZoom = new boolean[4];
    public static String GameMap = "DEFAULT";
    public static String GameSaveWorld = "";
    public static boolean SafeMode = false;
    public static boolean SafeModeForced = false;
    public static boolean SoundDisabled = false;
    public int frameStage = 0;
    private int stack = 0;
    public static int xx = 0;
    public static int yy = 0;
    public static int zz = 0;
    public final HashMap<Integer, Float> FloatParamMap = new HashMap<>();
    private final Matrix4f tempMatrix4f = new Matrix4f();
    private static final float isoAngle = 62.65607F;
    private static final float scale = 0.047085002F;
    public static boolean bLastStand = false;
    public static String ChallengeID = null;
    public static boolean bExiting = false;
    private String m_delayResetLua_activeMods = null;
    private String m_delayResetLua_reason = null;

    public boolean isMultiThread() {
        return true;
    }

    public void setChallenge(boolean _bChallenge) {
        this.bChallenge = _bChallenge;
    }

    public boolean isChallenge() {
        return this.bChallenge;
    }

    public String getChallengeID() {
        return ChallengeID;
    }

    public boolean getOptionTieredZombieUpdates() {
        return MovingObjectUpdateScheduler.instance.isEnabled();
    }

    public void setOptionTieredZombieUpdates(boolean val) {
        MovingObjectUpdateScheduler.instance.setEnabled(val);
    }

    public void setFramerate(int index) {
        PerformanceSettings.setUncappedFPS(index == 1);
        switch (index) {
            case 1:
                PerformanceSettings.setLockFPS(60);
                break;
            case 2:
                PerformanceSettings.setLockFPS(244);
                break;
            case 3:
                PerformanceSettings.setLockFPS(240);
                break;
            case 4:
                PerformanceSettings.setLockFPS(165);
                break;
            case 5:
                PerformanceSettings.setLockFPS(120);
                break;
            case 6:
                PerformanceSettings.setLockFPS(95);
                break;
            case 7:
                PerformanceSettings.setLockFPS(90);
                break;
            case 8:
                PerformanceSettings.setLockFPS(75);
                break;
            case 9:
                PerformanceSettings.setLockFPS(60);
                break;
            case 10:
                PerformanceSettings.setLockFPS(55);
                break;
            case 11:
                PerformanceSettings.setLockFPS(45);
                break;
            case 12:
                PerformanceSettings.setLockFPS(30);
                break;
            case 13:
                PerformanceSettings.setLockFPS(24);
        }
    }

    public void setMultiThread(boolean val) {
    }

    public boolean loadedShader() {
        return this.RenderShader != null;
    }

    public static int getGLMajorVersion() {
        if (glMajorVersion == -1) {
            getOpenGLVersions();
        }

        return glMajorVersion;
    }

    public boolean getUseShaders() {
        return true;
    }

    public int getPerfSkybox() {
        return this.iPerfSkybox_new;
    }

    public int getPerfSkyboxOnLoad() {
        return this.iPerfSkybox;
    }

    public void setPerfSkybox(int val) {
        this.iPerfSkybox_new = val;
    }

    public boolean getPerfReflections() {
        return this.bPerfReflections_new;
    }

    public boolean getPerfReflectionsOnLoad() {
        return this.bPerfReflections;
    }

    public void setPerfReflections(boolean val) {
        this.bPerfReflections_new = val;
    }

    public int getPerfPuddles() {
        return this.iPerfPuddles_new;
    }

    public int getPerfPuddlesOnLoad() {
        return this.iPerfPuddles;
    }

    public void setPerfPuddles(int val) {
        this.iPerfPuddles_new = val;
    }

    public int getVidMem() {
        return SafeMode ? 5 : this.vidMem;
    }

    public void setVidMem(int mem) {
        if (SafeMode) {
            this.vidMem = 5;
        }

        this.vidMem = mem;

        try {
            this.saveOptions();
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    public void setUseShaders(boolean bUse) {
    }

    public void shadersOptionChanged() {
        RenderThread.invokeOnRenderContext(() -> {
            if (!SafeModeForced) {
                try {
                    if (this.RenderShader == null) {
                        this.RenderShader = new WeatherShader("screen");
                    }

                    if (this.RenderShader != null && !this.RenderShader.isCompiled()) {
                        this.RenderShader = null;
                    }
                } catch (Exception exception0) {
                    this.RenderShader = null;
                }
            } else if (this.RenderShader != null) {
                try {
                    this.RenderShader.destroy();
                } catch (Exception exception1) {
                    exception1.printStackTrace();
                }

                this.RenderShader = null;
            }
        });
    }

    public void initShaders() {
        try {
            if (this.RenderShader == null && !SafeMode && !SafeModeForced) {
                RenderThread.invokeOnRenderContext(() -> this.RenderShader = new WeatherShader("screen"));
            }

            if (this.RenderShader == null || !this.RenderShader.isCompiled()) {
                this.RenderShader = null;
            }
        } catch (Exception exception) {
            this.RenderShader = null;
            exception.printStackTrace();
        }

        IsoPuddles.getInstance();
        IsoWater.getInstance();
    }

    public static String getGLVersion() {
        if (glVersion == null) {
            getOpenGLVersions();
        }

        return glVersion;
    }

    public String getGameMode() {
        return GameMode;
    }

    public static Core getInstance() {
        return core;
    }

    public static void getOpenGLVersions() {
        glVersion = GL11.glGetString(7938);
        glMajorVersion = glVersion.charAt(0) - '0';
    }

    public boolean getDebug() {
        return bDebug;
    }

    public static void setFullScreen(boolean bool) {
        fullScreen = bool;
    }

    public static int[] flipPixels(int[] ints1, int int0, int int1) {
        int[] ints0 = null;
        if (ints1 != null) {
            ints0 = new int[int0 * int1];

            for (int int2 = 0; int2 < int1; int2++) {
                for (int int3 = 0; int3 < int0; int3++) {
                    ints0[(int1 - int2 - 1) * int0 + int3] = ints1[int2 * int0 + int3];
                }
            }
        }

        return ints0;
    }

    public void TakeScreenshot() {
        this.TakeScreenshot(256, 256, 1028);
    }

    public void TakeScreenshot(int _width, int _height, int readBuffer) {
        byte byte0 = 0;
        int int0 = IsoCamera.getScreenWidth(byte0);
        int int1 = IsoCamera.getScreenHeight(byte0);
        _width = PZMath.min(_width, int0);
        _height = PZMath.min(_height, int1);
        int int2 = IsoCamera.getScreenLeft(byte0) + int0 / 2 - _width / 2;
        int int3 = IsoCamera.getScreenTop(byte0) + int1 / 2 - _height / 2;
        this.TakeScreenshot(int2, int3, _width, _height, readBuffer);
    }

    public void TakeScreenshot(int x, int y, int _width, int _height, int readBuffer) {
        GL11.glPixelStorei(3333, 1);
        GL11.glReadBuffer(readBuffer);
        byte byte0 = 3;
        ByteBuffer byteBuffer = MemoryUtil.memAlloc(_width * _height * byte0);
        GL11.glReadPixels(x, y, _width, _height, 6407, 5121, byteBuffer);
        int[] ints = new int[_width * _height];
        File file = ZomboidFileSystem.instance.getFileInCurrentSave("thumb.png");
        String string = "png";

        for (int int0 = 0; int0 < ints.length; int0++) {
            int int1 = int0 * 3;
            ints[int0] = 0xFF000000 | (byteBuffer.get(int1) & 255) << 16 | (byteBuffer.get(int1 + 1) & 255) << 8 | (byteBuffer.get(int1 + 2) & 255) << 0;
        }

        MemoryUtil.memFree(byteBuffer);
        ints = flipPixels(ints, _width, _height);
        BufferedImage bufferedImage = new BufferedImage(_width, _height, 2);
        bufferedImage.setRGB(0, 0, _width, _height, ints, 0, _width);

        try {
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }

        Texture.reload(ZomboidFileSystem.instance.getFileNameInCurrentSave("thumb.png"));
    }

    public void TakeFullScreenshot(String filename) {
        if (StringUtils.containsDoubleDot(filename)) {
            throw new IllegalArgumentException("Unable to save options to filename: %s".formatted(filename));
        } else {
            RenderThread.invokeOnRenderContext(
                filename,
                string -> {
                    GL11.glPixelStorei(3333, 1);
                    GL11.glReadBuffer(1028);
                    int int0 = Display.getDisplayMode().getWidth();
                    int int1 = Display.getDisplayMode().getHeight();
                    byte byte0 = 0;
                    byte byte1 = 0;
                    byte byte2 = 3;
                    ByteBuffer byteBuffer = MemoryUtil.memAlloc(int0 * int1 * byte2);
                    GL11.glReadPixels(byte0, byte1, int0, int1, 6407, 5121, byteBuffer);
                    int[] ints = new int[int0 * int1];
                    if (string == null) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
                        string = "screenshot_" + simpleDateFormat.format(Calendar.getInstance().getTime()) + ".png";
                    }

                    File file = new File(ZomboidFileSystem.instance.getScreenshotDir() + File.separator + string);

                    for (int int2 = 0; int2 < ints.length; int2++) {
                        int int3 = int2 * 3;
                        ints[int2] = 0xFF000000
                            | (byteBuffer.get(int3) & 255) << 16
                            | (byteBuffer.get(int3 + 1) & 255) << 8
                            | (byteBuffer.get(int3 + 2) & 255) << 0;
                    }

                    MemoryUtil.memFree(byteBuffer);
                    ints = flipPixels(ints, int0, int1);
                    BufferedImage bufferedImage = new BufferedImage(int0, int1, 2);
                    bufferedImage.setRGB(0, 0, int0, int1, ints, 0, int0);

                    try {
                        ImageIO.write(bufferedImage, "png", file);
                    } catch (IOException iOException) {
                        iOException.printStackTrace();
                    }
                }
            );
        }
    }

    public static boolean supportNPTTexture() {
        return false;
    }

    public boolean supportsFBO() {
        if (SafeMode) {
            this.OffscreenBuffer.bZoomEnabled = false;
            return false;
        } else if (!this.bSupportsFBO) {
            return false;
        } else if (this.OffscreenBuffer.Current != null) {
            return true;
        } else {
            try {
                if (TextureFBO.checkFBOSupport() && this.setupMultiFBO()) {
                    return true;
                } else {
                    this.bSupportsFBO = false;
                    SafeMode = true;
                    this.OffscreenBuffer.bZoomEnabled = false;
                    return false;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                this.bSupportsFBO = false;
                SafeMode = true;
                this.OffscreenBuffer.bZoomEnabled = false;
                return false;
            }
        }
    }

    private void sharedInit() {
        this.supportsFBO();
    }

    public void MoveMethodToggle() {
        bAltMoveMethod = !bAltMoveMethod;
    }

    public void EndFrameText(int nPlayer) {
        if (!LuaManager.thread.bStep) {
            if (this.OffscreenBuffer.Current != null) {
            }

            IndieGL.glDoEndFrame();
            this.frameStage = 2;
        }
    }

    public void EndFrame(int nPlayer) {
        if (!LuaManager.thread.bStep) {
            if (this.OffscreenBuffer.Current != null) {
                SpriteRenderer.instance.glBuffer(0, nPlayer);
            }

            IndieGL.glDoEndFrame();
            this.frameStage = 2;
        }
    }

    public void EndFrame() {
        IndieGL.glDoEndFrame();
        if (this.OffscreenBuffer.Current != null) {
            SpriteRenderer.instance.glBuffer(0, 0);
        }
    }

    public void EndFrameUI() {
        if (!blinkAlphaIncrease) {
            blinkAlpha = blinkAlpha - 0.07F * (GameTime.getInstance().getMultiplier() / 1.6F);
            if (blinkAlpha < 0.15F) {
                blinkAlpha = 0.15F;
                blinkAlphaIncrease = true;
            }
        } else {
            blinkAlpha = blinkAlpha + 0.07F * (GameTime.getInstance().getMultiplier() / 1.6F);
            if (blinkAlpha > 1.0F) {
                blinkAlpha = 1.0F;
                blinkAlphaIncrease = false;
            }
        }

        if (UIManager.useUIFBO && UIManager.UIFBO == null) {
            UIManager.CreateFBO(width, height);
        }

        if (LuaManager.thread != null && LuaManager.thread.bStep) {
            SpriteRenderer.instance.clearSprites();
        } else {
            ExceptionLogger.render();
            if (UIManager.useUIFBO && this.UIRenderThisFrame) {
                SpriteRenderer.instance.glBuffer(3, 0);
                IndieGL.glDoEndFrame();
                SpriteRenderer.instance.stopOffscreenUI();
                IndieGL.glDoStartFrame(width, height, 1.0F, -1);
                float float0 = (int)(1.0F / OptionUIRenderFPS * 100.0F) / 100.0F;
                int int0 = (int)(this.UIRenderAccumulator / float0);
                this.UIRenderAccumulator -= int0 * float0;
                if (FPSGraph.instance != null) {
                    FPSGraph.instance.addUI(System.currentTimeMillis());
                }
            }

            if (UIManager.useUIFBO) {
                SpriteRenderer.instance.setDoAdditive(true);
                SpriteRenderer.instance.renderi((Texture)UIManager.UIFBO.getTexture(), 0, height, width, -height, 1.0F, 1.0F, 1.0F, 1.0F, null);
                SpriteRenderer.instance.setDoAdditive(false);
            }

            if (getInstance().getOptionLockCursorToWindow()) {
                Mouse.renderCursorTexture();
            }

            IndieGL.glDoEndFrame();
            RenderThread.Ready();
            this.frameStage = 0;
        }
    }

    public static void UnfocusActiveTextEntryBox() {
        if (CurrentTextEntryBox != null && !CurrentTextEntryBox.getUIName().contains("chat text entry")) {
            CurrentTextEntryBox.DoingTextEntry = false;
            if (CurrentTextEntryBox.Frame != null) {
                CurrentTextEntryBox.Frame.Colour = CurrentTextEntryBox.StandardFrameColour;
            }

            CurrentTextEntryBox = null;
        }
    }

    public int getOffscreenWidth(int playerIndex) {
        if (this.OffscreenBuffer == null) {
            return IsoPlayer.numPlayers > 1 ? this.getScreenWidth() / 2 : this.getScreenWidth();
        } else {
            return this.OffscreenBuffer.getWidth(playerIndex);
        }
    }

    public int getOffscreenHeight(int playerIndex) {
        if (this.OffscreenBuffer == null) {
            return IsoPlayer.numPlayers > 2 ? this.getScreenHeight() / 2 : this.getScreenHeight();
        } else {
            return this.OffscreenBuffer.getHeight(playerIndex);
        }
    }

    public int getOffscreenTrueWidth() {
        return this.OffscreenBuffer != null && this.OffscreenBuffer.Current != null ? this.OffscreenBuffer.getTexture(0).getWidth() : this.getScreenWidth();
    }

    public int getOffscreenTrueHeight() {
        return this.OffscreenBuffer != null && this.OffscreenBuffer.Current != null ? this.OffscreenBuffer.getTexture(0).getHeight() : this.getScreenHeight();
    }

    public int getScreenHeight() {
        return height;
    }

    public int getScreenWidth() {
        return width;
    }

    public void setResolutionAndFullScreen(int w, int h, boolean _fullScreen) {
        setDisplayMode(w, h, _fullScreen);
        this.setScreenSize(Display.getWidth(), Display.getHeight());
    }

    public void setResolution(String res) {
        String[] strings = res.split("x");
        int int0 = Integer.parseInt(strings[0].trim());
        int int1 = Integer.parseInt(strings[1].trim());
        if (fullScreen) {
            setDisplayMode(int0, int1, true);
        } else {
            setDisplayMode(int0, int1, false);
        }

        this.setScreenSize(Display.getWidth(), Display.getHeight());

        try {
            this.saveOptions();
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    public boolean loadOptions() throws IOException {
        this.bLoadedOptions = false;
        File file0 = new File(ZomboidFileSystem.instance.getCacheDir() + File.separator + "options.ini");
        if (!file0.exists()) {
            this.saveFolder = getMyDocumentFolder();
            File file1 = new File(this.saveFolder);
            file1.mkdir();
            this.copyPasteFolders("mods");
            this.setOptionLanguageName(System.getProperty("user.language").toUpperCase());
            if (Translator.getAzertyMap().contains(Translator.getLanguage().name())) {
                this.setAzerty(true);
            }

            if (!GameServer.bServer) {
                try {
                    int int0 = 0;
                    int int1 = 0;
                    DisplayMode[] displayModes = Display.getAvailableDisplayModes();
                    int[] ints0 = new int[1];
                    int[] ints1 = new int[1];
                    int[] ints2 = new int[1];
                    int[] ints3 = new int[1];
                    GLFW.glfwGetMonitorWorkarea(GLFW.glfwGetPrimaryMonitor(), ints0, ints1, ints2, ints3);

                    for (int int2 = 0; int2 < displayModes.length; int2++) {
                        if (displayModes[int2].getWidth() > int0 && displayModes[int2].getWidth() < ints2[0] && displayModes[int2].getHeight() < ints3[0]) {
                            int0 = displayModes[int2].getWidth();
                            int1 = displayModes[int2].getHeight();
                        }
                    }

                    width = int0;
                    height = int1;
                } catch (LWJGLException lWJGLException) {
                    lWJGLException.printStackTrace();
                }
            }

            this.setOptionZoomLevels2x("50;75;125;150;175;200");
            this.setOptionZoomLevels1x("50;75;125;150;175;200");
            this.saveOptions();
            return false;
        } else {
            this.bLoadedOptions = true;

            for (int int3 = 0; int3 < 4; int3++) {
                this.setAutoZoom(int3, false);
            }

            OptionLanguageName = null;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file0));

            try {
                String string0;
                while ((string0 = bufferedReader.readLine()) != null) {
                    if (string0.startsWith("version=")) {
                        this.version = new Integer(string0.replaceFirst("version=", ""));
                    } else if (string0.startsWith("width=")) {
                        width = new Integer(string0.replaceFirst("width=", ""));
                    } else if (string0.startsWith("height=")) {
                        height = new Integer(string0.replaceFirst("height=", ""));
                    } else if (string0.startsWith("fullScreen=")) {
                        fullScreen = Boolean.parseBoolean(string0.replaceFirst("fullScreen=", ""));
                    } else if (string0.startsWith("frameRate=")) {
                        PerformanceSettings.setLockFPS(Integer.parseInt(string0.replaceFirst("frameRate=", "")));
                    } else if (string0.startsWith("uncappedFPS=")) {
                        PerformanceSettings.setUncappedFPS(Boolean.parseBoolean(string0.replaceFirst("uncappedFPS=", "")));
                    } else if (string0.startsWith("iso_cursor=")) {
                        getInstance().setIsoCursorVisibility(Integer.parseInt(string0.replaceFirst("iso_cursor=", "")));
                    } else if (string0.startsWith("showCursorWhileAiming=")) {
                        OptionShowCursorWhileAiming = Boolean.parseBoolean(string0.replaceFirst("showCursorWhileAiming=", ""));
                    } else if (string0.startsWith("water=")) {
                        PerformanceSettings.WaterQuality = Integer.parseInt(string0.replaceFirst("water=", ""));
                    } else if (string0.startsWith("puddles=")) {
                        PerformanceSettings.PuddlesQuality = Integer.parseInt(string0.replaceFirst("puddles=", ""));
                    } else if (string0.startsWith("lighting=")) {
                        PerformanceSettings.LightingFrameSkip = Integer.parseInt(string0.replaceFirst("lighting=", ""));
                    } else if (string0.startsWith("lightFPS=")) {
                        PerformanceSettings.instance.setLightingFPS(Integer.parseInt(string0.replaceFirst("lightFPS=", "")));
                    } else if (string0.startsWith("perfSkybox=")) {
                        this.iPerfSkybox = Integer.parseInt(string0.replaceFirst("perfSkybox=", ""));
                        this.iPerfSkybox_new = this.iPerfSkybox;
                    } else if (string0.startsWith("perfPuddles=")) {
                        this.iPerfPuddles = Integer.parseInt(string0.replaceFirst("perfPuddles=", ""));
                        this.iPerfPuddles_new = this.iPerfPuddles;
                    } else if (string0.startsWith("bPerfReflections=")) {
                        this.bPerfReflections = Boolean.parseBoolean(string0.replaceFirst("bPerfReflections=", ""));
                        this.bPerfReflections_new = this.bPerfReflections;
                    } else if (string0.startsWith("language=")) {
                        OptionLanguageName = string0.replaceFirst("language=", "").trim();
                    } else if (string0.startsWith("zoom=")) {
                        OptionZoom = Boolean.parseBoolean(string0.replaceFirst("zoom=", ""));
                    } else if (string0.startsWith("autozoom=")) {
                        String[] strings0 = string0.replaceFirst("autozoom=", "").split(",");

                        for (int int4 = 0; int4 < strings0.length; int4++) {
                            if (!strings0[int4].isEmpty()) {
                                int int5 = Integer.parseInt(strings0[int4]);
                                if (int5 >= 1 && int5 <= 4) {
                                    this.setAutoZoom(int5 - 1, true);
                                }
                            }
                        }
                    } else if (string0.startsWith("fontSize=")) {
                        this.setOptionFontSize(Integer.parseInt(string0.replaceFirst("fontSize=", "").trim()));
                    } else if (string0.startsWith("contextMenuFont=")) {
                        OptionContextMenuFont = string0.replaceFirst("contextMenuFont=", "").trim();
                    } else if (string0.startsWith("inventoryFont=")) {
                        OptionInventoryFont = string0.replaceFirst("inventoryFont=", "").trim();
                    } else if (string0.startsWith("inventoryContainerSize=")) {
                        OptionInventoryContainerSize = PZMath.tryParseInt(string0.replaceFirst("inventoryContainerSize=", ""), 1);
                        OptionInventoryContainerSize = PZMath.clamp(OptionInventoryContainerSize, 1, 3);
                    } else if (string0.startsWith("tooltipFont=")) {
                        OptionTooltipFont = string0.replaceFirst("tooltipFont=", "").trim();
                    } else if (string0.startsWith("measurementsFormat=")) {
                        OptionMeasurementFormat = string0.replaceFirst("measurementsFormat=", "").trim();
                    } else if (string0.startsWith("clockFormat=")) {
                        OptionClockFormat = Integer.parseInt(string0.replaceFirst("clockFormat=", ""));
                    } else if (string0.startsWith("clockSize=")) {
                        OptionClockSize = Integer.parseInt(string0.replaceFirst("clockSize=", ""));
                    } else if (string0.startsWith("clock24Hour=")) {
                        OptionClock24Hour = Boolean.parseBoolean(string0.replaceFirst("clock24Hour=", ""));
                    } else if (string0.startsWith("vsync=")) {
                        OptionVSync = Boolean.parseBoolean(string0.replaceFirst("vsync=", ""));
                    } else if (string0.startsWith("voiceEnable=")) {
                        OptionVoiceEnable = Boolean.parseBoolean(string0.replaceFirst("voiceEnable=", ""));
                    } else if (string0.startsWith("voiceMode=")) {
                        OptionVoiceMode = Integer.parseInt(string0.replaceFirst("voiceMode=", ""));
                    } else if (string0.startsWith("voiceVADMode=")) {
                        OptionVoiceVADMode = Integer.parseInt(string0.replaceFirst("voiceVADMode=", ""));
                    } else if (string0.startsWith("voiceAGCMode=")) {
                        OptionVoiceAGCMode = Integer.parseInt(string0.replaceFirst("voiceAGCMode=", ""));
                    } else if (string0.startsWith("voiceVolumeMic=")) {
                        OptionVoiceVolumeMic = Integer.parseInt(string0.replaceFirst("voiceVolumeMic=", ""));
                    } else if (string0.startsWith("voiceVolumePlayers=")) {
                        OptionVoiceVolumePlayers = Integer.parseInt(string0.replaceFirst("voiceVolumePlayers=", ""));
                    } else if (string0.startsWith("voiceRecordDeviceName=")) {
                        OptionVoiceRecordDeviceName = string0.replaceFirst("voiceRecordDeviceName=", "");
                    } else if (string0.startsWith("soundVolume=")) {
                        OptionSoundVolume = Integer.parseInt(string0.replaceFirst("soundVolume=", ""));
                    } else if (string0.startsWith("musicVolume=")) {
                        OptionMusicVolume = Integer.parseInt(string0.replaceFirst("musicVolume=", ""));
                    } else if (string0.startsWith("ambientVolume=")) {
                        OptionAmbientVolume = Integer.parseInt(string0.replaceFirst("ambientVolume=", ""));
                    } else if (string0.startsWith("jumpScareVolume=")) {
                        OptionJumpScareVolume = Integer.parseInt(string0.replaceFirst("jumpScareVolume=", ""));
                    } else if (string0.startsWith("musicActionStyle=")) {
                        OptionMusicActionStyle = Integer.parseInt(string0.replaceFirst("musicActionStyle=", ""));
                    } else if (string0.startsWith("musicLibrary=")) {
                        OptionMusicLibrary = Integer.parseInt(string0.replaceFirst("musicLibrary=", ""));
                    } else if (string0.startsWith("vehicleEngineVolume=")) {
                        OptionVehicleEngineVolume = Integer.parseInt(string0.replaceFirst("vehicleEngineVolume=", ""));
                    } else if (string0.startsWith("reloadDifficulty=")) {
                        OptionReloadDifficulty = Integer.parseInt(string0.replaceFirst("reloadDifficulty=", ""));
                    } else if (string0.startsWith("rackProgress=")) {
                        OptionRackProgress = Boolean.parseBoolean(string0.replaceFirst("rackProgress=", ""));
                    } else if (string0.startsWith("controller=")) {
                        String string1 = string0.replaceFirst("controller=", "");
                        if (!string1.isEmpty()) {
                            JoypadManager.instance.setControllerActive(string1, true);
                        }
                    } else if (string0.startsWith("tutorialDone=")) {
                        this.tutorialDone = Boolean.parseBoolean(string0.replaceFirst("tutorialDone=", ""));
                    } else if (string0.startsWith("vehiclesWarningShow=")) {
                        this.vehiclesWarningShow = Boolean.parseBoolean(string0.replaceFirst("vehiclesWarningShow=", ""));
                    } else if (string0.startsWith("bloodDecals=")) {
                        this.setOptionBloodDecals(Integer.parseInt(string0.replaceFirst("bloodDecals=", "")));
                    } else if (string0.startsWith("borderless=")) {
                        OptionBorderlessWindow = Boolean.parseBoolean(string0.replaceFirst("borderless=", ""));
                    } else if (string0.startsWith("lockCursorToWindow=")) {
                        OptionLockCursorToWindow = Boolean.parseBoolean(string0.replaceFirst("lockCursorToWindow=", ""));
                    } else if (string0.startsWith("textureCompression=")) {
                        OptionTextureCompression = Boolean.parseBoolean(string0.replaceFirst("textureCompression=", ""));
                    } else if (string0.startsWith("modelTextureMipmaps=")) {
                        OptionModelTextureMipmaps = Boolean.parseBoolean(string0.replaceFirst("modelTextureMipmaps=", ""));
                    } else if (string0.startsWith("texture2x=")) {
                        OptionTexture2x = Boolean.parseBoolean(string0.replaceFirst("texture2x=", ""));
                    } else if (string0.startsWith("maxTextureSize=")) {
                        OptionMaxTextureSize = Integer.parseInt(string0.replaceFirst("maxTextureSize=", ""));
                        OptionMaxTextureSize = PZMath.clamp(OptionMaxTextureSize, 1, 4);
                    } else if (string0.startsWith("maxVehicleTextureSize=")) {
                        OptionMaxVehicleTextureSize = Integer.parseInt(string0.replaceFirst("maxVehicleTextureSize=", ""));
                        OptionMaxVehicleTextureSize = PZMath.clamp(OptionMaxVehicleTextureSize, 1, 4);
                    } else if (string0.startsWith("zoomLevels1x=")) {
                        OptionZoomLevels1x = string0.replaceFirst("zoomLevels1x=", "");
                    } else if (string0.startsWith("zoomLevels2x=")) {
                        OptionZoomLevels2x = string0.replaceFirst("zoomLevels2x=", "");
                    } else if (string0.startsWith("showChatTimestamp=")) {
                        OptionShowChatTimestamp = Boolean.parseBoolean(string0.replaceFirst("showChatTimestamp=", ""));
                    } else if (string0.startsWith("showChatTitle=")) {
                        OptionShowChatTitle = Boolean.parseBoolean(string0.replaceFirst("showChatTitle=", ""));
                    } else if (string0.startsWith("chatFontSize=")) {
                        OptionChatFontSize = string0.replaceFirst("chatFontSize=", "");
                    } else if (string0.startsWith("minChatOpaque=")) {
                        OptionMinChatOpaque = Float.parseFloat(string0.replaceFirst("minChatOpaque=", ""));
                    } else if (string0.startsWith("maxChatOpaque=")) {
                        OptionMaxChatOpaque = Float.parseFloat(string0.replaceFirst("maxChatOpaque=", ""));
                    } else if (string0.startsWith("chatFadeTime=")) {
                        OptionChatFadeTime = Float.parseFloat(string0.replaceFirst("chatFadeTime=", ""));
                    } else if (string0.startsWith("chatOpaqueOnFocus=")) {
                        OptionChatOpaqueOnFocus = Boolean.parseBoolean(string0.replaceFirst("chatOpaqueOnFocus=", ""));
                    } else if (string0.startsWith("doneNewSaveFolder=")) {
                        this.doneNewSaveFolder = Boolean.parseBoolean(string0.replaceFirst("doneNewSaveFolder=", ""));
                    } else if (string0.startsWith("contentTranslationsEnabled=")) {
                        OptionEnableContentTranslations = Boolean.parseBoolean(string0.replaceFirst("contentTranslationsEnabled=", ""));
                    } else if (string0.startsWith("showYourUsername=")) {
                        this.showYourUsername = Boolean.parseBoolean(string0.replaceFirst("showYourUsername=", ""));
                    } else if (string0.startsWith("riversideDone=")) {
                        this.riversideDone = Boolean.parseBoolean(string0.replaceFirst("riversideDone=", ""));
                    } else if (string0.startsWith("rosewoodSpawnDone=")) {
                        this.rosewoodSpawnDone = Boolean.parseBoolean(string0.replaceFirst("rosewoodSpawnDone=", ""));
                    } else if (string0.startsWith("gotNewBelt=")) {
                        this.gotNewBelt = Boolean.parseBoolean(string0.replaceFirst("gotNewBelt=", ""));
                    } else if (string0.startsWith("mpTextColor=")) {
                        String[] strings1 = string0.replaceFirst("mpTextColor=", "").split(",");
                        float float0 = Float.parseFloat(strings1[0]);
                        float float1 = Float.parseFloat(strings1[1]);
                        float float2 = Float.parseFloat(strings1[2]);
                        if (float0 < 0.19F) {
                            float0 = 0.19F;
                        }

                        if (float1 < 0.19F) {
                            float1 = 0.19F;
                        }

                        if (float2 < 0.19F) {
                            float2 = 0.19F;
                        }

                        this.mpTextColor = new ColorInfo(float0, float1, float2, 1.0F);
                    } else if (string0.startsWith("objHighlightColor=")) {
                        String[] strings2 = string0.replaceFirst("objHighlightColor=", "").split(",");
                        float float3 = Float.parseFloat(strings2[0]);
                        float float4 = Float.parseFloat(strings2[1]);
                        float float5 = Float.parseFloat(strings2[2]);
                        if (float3 < 0.19F) {
                            float3 = 0.19F;
                        }

                        if (float4 < 0.19F) {
                            float4 = 0.19F;
                        }

                        if (float5 < 0.19F) {
                            float5 = 0.19F;
                        }

                        this.objectHighlitedColor.set(float3, float4, float5, 1.0F);
                    } else if (string0.startsWith("goodHighlightColor=")) {
                        String[] strings3 = string0.replaceFirst("goodHighlightColor=", "").split(",");
                        float float6 = Float.parseFloat(strings3[0]);
                        float float7 = Float.parseFloat(strings3[1]);
                        float float8 = Float.parseFloat(strings3[2]);
                        this.goodHighlitedColor.set(float6, float7, float8, 1.0F);
                    } else if (string0.startsWith("badHighlightColor=")) {
                        String[] strings4 = string0.replaceFirst("badHighlightColor=", "").split(",");
                        float float9 = Float.parseFloat(strings4[0]);
                        float float10 = Float.parseFloat(strings4[1]);
                        float float11 = Float.parseFloat(strings4[2]);
                        this.badHighlitedColor.set(float9, float10, float11, 1.0F);
                    } else if (string0.startsWith("seenNews=")) {
                        this.setSeenUpdateText(string0.replaceFirst("seenNews=", ""));
                    } else if (string0.startsWith("toggleToAim=")) {
                        this.setToggleToAim(Boolean.parseBoolean(string0.replaceFirst("toggleToAim=", "")));
                    } else if (string0.startsWith("toggleToRun=")) {
                        this.setToggleToRun(Boolean.parseBoolean(string0.replaceFirst("toggleToRun=", "")));
                    } else if (string0.startsWith("toggleToSprint=")) {
                        this.setToggleToSprint(Boolean.parseBoolean(string0.replaceFirst("toggleToSprint=", "")));
                    } else if (string0.startsWith("celsius=")) {
                        this.setCelsius(Boolean.parseBoolean(string0.replaceFirst("celsius=", "")));
                    } else if (!string0.startsWith("mapOrder=")) {
                        if (string0.startsWith("showFirstTimeSneakTutorial=")) {
                            this.setShowFirstTimeSneakTutorial(Boolean.parseBoolean(string0.replaceFirst("showFirstTimeSneakTutorial=", "")));
                        } else if (string0.startsWith("showFirstTimeSearchTutorial=")) {
                            this.setShowFirstTimeSearchTutorial(Boolean.parseBoolean(string0.replaceFirst("showFirstTimeSearchTutorial=", "")));
                        } else if (string0.startsWith("termsOfServiceVersion=")) {
                            this.termsOfServiceVersion = Integer.parseInt(string0.replaceFirst("termsOfServiceVersion=", ""));
                        } else if (string0.startsWith("uiRenderOffscreen=")) {
                            OptionUIFBO = Boolean.parseBoolean(string0.replaceFirst("uiRenderOffscreen=", ""));
                        } else if (string0.startsWith("uiRenderFPS=")) {
                            OptionUIRenderFPS = Integer.parseInt(string0.replaceFirst("uiRenderFPS=", ""));
                        } else if (string0.startsWith("radialMenuKeyToggle=")) {
                            OptionRadialMenuKeyToggle = Boolean.parseBoolean(string0.replaceFirst("radialMenuKeyToggle=", ""));
                        } else if (string0.startsWith("reloadRadialInstant=")) {
                            OptionReloadRadialInstant = Boolean.parseBoolean(string0.replaceFirst("reloadRadialInstant=", ""));
                        } else if (string0.startsWith("panCameraWhileAiming=")) {
                            OptionPanCameraWhileAiming = Boolean.parseBoolean(string0.replaceFirst("panCameraWhileAiming=", ""));
                        } else if (string0.startsWith("panCameraWhileDriving=")) {
                            OptionPanCameraWhileDriving = Boolean.parseBoolean(string0.replaceFirst("panCameraWhileDriving=", ""));
                        } else if (string0.startsWith("temperatureDisplayCelsius=")) {
                            OptionTemperatureDisplayCelsius = Boolean.parseBoolean(string0.replaceFirst("temperatureDisplayCelsius=", ""));
                        } else if (string0.startsWith("doWindSpriteEffects=")) {
                            OptionDoWindSpriteEffects = Boolean.parseBoolean(string0.replaceFirst("doWindSpriteEffects=", ""));
                        } else if (string0.startsWith("doDoorSpriteEffects=")) {
                            OptionDoDoorSpriteEffects = Boolean.parseBoolean(string0.replaceFirst("doDoorSpriteEffects=", ""));
                        } else if (string0.startsWith("doContainerOutline=")) {
                            OptionDoContainerOutline = Boolean.parseBoolean(string0.replaceFirst("doContainerOutline=", ""));
                        } else if (string0.startsWith("updateSneakButton2=")) {
                            OptionUpdateSneakButton = true;
                        } else if (string0.startsWith("updateSneakButton=")) {
                            OptionUpdateSneakButton = Boolean.parseBoolean(string0.replaceFirst("updateSneakButton=", ""));
                        } else if (string0.startsWith("dblTapJogToSprint=")) {
                            OptiondblTapJogToSprint = Boolean.parseBoolean(string0.replaceFirst("dblTapJogToSprint=", ""));
                        } else if (string0.startsWith("aimOutline=")) {
                            this.setOptionAimOutline(PZMath.tryParseInt(string0.replaceFirst("aimOutline=", ""), 2));
                        } else if (string0.startsWith("cycleContainerKey=")) {
                            OptionCycleContainerKey = string0.replaceFirst("cycleContainerKey=", "");
                        } else if (string0.startsWith("dropItemsOnSquareCenter=")) {
                            OptionDropItemsOnSquareCenter = Boolean.parseBoolean(string0.replaceFirst("dropItemsOnSquareCenter=", ""));
                        } else if (string0.startsWith("timedActionGameSpeedReset=")) {
                            OptionTimedActionGameSpeedReset = Boolean.parseBoolean(string0.replaceFirst("timedActionGameSpeedReset=", ""));
                        } else if (string0.startsWith("shoulderButtonContainerSwitch=")) {
                            OptionShoulderButtonContainerSwitch = Integer.parseInt(string0.replaceFirst("shoulderButtonContainerSwitch=", ""));
                        } else if (string0.startsWith("singleContextMenu=")) {
                            this.readPerPlayerBoolean(string0.replaceFirst("singleContextMenu=", ""), OptionSingleContextMenu);
                        } else if (string0.startsWith("renderPrecipIndoors=")) {
                            OptionRenderPrecipIndoors = Boolean.parseBoolean(string0.replaceFirst("renderPrecipIndoors=", ""));
                        } else if (string0.startsWith("autoProneAtk=")) {
                            OptionAutoProneAtk = Boolean.parseBoolean(string0.replaceFirst("autoProneAtk=", ""));
                        } else if (string0.startsWith("3DGroundItem=")) {
                            Option3DGroundItem = Boolean.parseBoolean(string0.replaceFirst("3DGroundItem=", ""));
                        } else if (string0.startsWith("tieredZombieUpdates=")) {
                            this.setOptionTieredZombieUpdates(Boolean.parseBoolean(string0.replaceFirst("tieredZombieUpdates=", "")));
                        } else if (string0.startsWith("progressBar=")) {
                            this.setOptionProgressBar(Boolean.parseBoolean(string0.replaceFirst("progressBar=", "")));
                        } else if (string0.startsWith("corpseShadows=")) {
                            OptionCorpseShadows = Boolean.parseBoolean(string0.replaceFirst("corpseShadows=", ""));
                        } else if (string0.startsWith("simpleClothingTextures=")) {
                            this.setOptionSimpleClothingTextures(PZMath.tryParseInt(string0.replaceFirst("simpleClothingTextures=", ""), 1));
                        } else if (string0.startsWith("simpleWeaponTextures=")) {
                            OptionSimpleWeaponTextures = Boolean.parseBoolean(string0.replaceFirst("simpleWeaponTextures=", ""));
                        } else if (string0.startsWith("autoDrink=")) {
                            OptionAutoDrink = Boolean.parseBoolean(string0.replaceFirst("autoDrink=", ""));
                        } else if (string0.startsWith("leaveKeyInIgnition=")) {
                            OptionLeaveKeyInIgnition = Boolean.parseBoolean(string0.replaceFirst("leaveKeyInIgnition=", ""));
                        } else if (string0.startsWith("autoWalkContainer=")) {
                            OptionAutoWalkContainer = Boolean.parseBoolean(string0.replaceFirst("autoWalkContainer=", ""));
                        } else if (string0.startsWith("searchModeOverlayEffect=")) {
                            OptionSearchModeOverlayEffect = Integer.parseInt(string0.replaceFirst("searchModeOverlayEffect=", ""));
                        } else if (string0.startsWith("ignoreProneZombieRange=")) {
                            this.setOptionIgnoreProneZombieRange(PZMath.tryParseInt(string0.replaceFirst("ignoreProneZombieRange=", ""), 1));
                        } else if (string0.startsWith("fogQuality=")) {
                            PerformanceSettings.FogQuality = Integer.parseInt(string0.replaceFirst("fogQuality=", ""));
                        } else if (string0.startsWith("renderPrecipitation=")) {
                            OptionRenderPrecipitation = Integer.parseInt(string0.replaceFirst("renderPrecipitation=", ""));
                        } else if (string0.startsWith("showItemModInfo=")) {
                            OptionShowItemModInfo = Boolean.parseBoolean(string0.replaceFirst("showItemModInfo=", ""));
                        } else if (string0.startsWith("showSurvivalGuide=")) {
                            this.OptionShowSurvivalGuide = Boolean.parseBoolean(string0.replaceFirst("showSurvivalGuide=", ""));
                        } else if (string0.startsWith("enableLeftJoystickRadialMenu=")) {
                            OptionEnableLeftJoystickRadialMenu = Boolean.parseBoolean(string0.replaceFirst("enableLeftJoystickRadialMenu=", ""));
                        }
                    } else {
                        if (this.version < 7) {
                            string0 = "mapOrder=";
                        }

                        String[] strings5 = string0.replaceFirst("mapOrder=", "").split(";");

                        for (String string2 : strings5) {
                            string2 = string2.trim();
                            if (!string2.isEmpty()) {
                                ActiveMods.getById("default").getMapOrder().add(string2);
                            }
                        }

                        ZomboidFileSystem.instance.saveModsFile();
                    }
                }

                if (OptionLanguageName == null) {
                    OptionLanguageName = System.getProperty("user.language").toUpperCase();
                }

                if (!this.doneNewSaveFolder) {
                    File file2 = new File(ZomboidFileSystem.instance.getSaveDir());
                    file2.mkdir();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add("Beginner");
                    arrayList.add("Survival");
                    arrayList.add("A Really CD DA");
                    arrayList.add("LastStand");
                    arrayList.add("Opening Hours");
                    arrayList.add("Sandbox");
                    arrayList.add("Tutorial");
                    arrayList.add("Winter is Coming");
                    arrayList.add("You Have One Day");
                    Object object = null;
                    File file3 = null;

                    try {
                        for (String string3 : arrayList) {
                            object = new File(ZomboidFileSystem.instance.getCacheDir() + File.separator + string3);
                            file3 = new File(ZomboidFileSystem.instance.getSaveDir() + File.separator + string3);
                            if (object.exists()) {
                                file3.mkdir();
                                Files.move(object.toPath(), file3.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            }
                        }
                    } catch (Exception exception0) {
                    }

                    this.doneNewSaveFolder = true;
                }
            } catch (Exception exception1) {
                exception1.printStackTrace();
            } finally {
                bufferedReader.close();
            }

            this.saveOptions();
            return true;
        }
    }

    public boolean isDefaultOptions() {
        return !this.bLoadedOptions;
    }

    public boolean isDedicated() {
        return GameServer.bServer;
    }

    private void copyPasteFolders(String string) {
        File file = new File(string).getAbsoluteFile();
        if (file.exists()) {
            this.searchFolders(file, string);
        }
    }

    private void searchFolders(File file0, String string) {
        if (file0.isDirectory()) {
            File file1 = new File(this.saveFolder + File.separator + string);
            file1.mkdir();
            String[] strings = file0.list();

            for (int int0 = 0; int0 < strings.length; int0++) {
                this.searchFolders(new File(file0.getAbsolutePath() + File.separator + strings[int0]), string + File.separator + strings[int0]);
            }
        } else {
            this.copyPasteFile(file0, string);
        }
    }

    private void copyPasteFile(File file1, String string) {
        FileOutputStream fileOutputStream = null;
        FileInputStream fileInputStream = null;

        try {
            File file0 = new File(this.saveFolder + File.separator + string);
            file0.createNewFile();
            fileOutputStream = new FileOutputStream(file0);
            fileInputStream = new FileInputStream(file1);
            fileOutputStream.getChannel().transferFrom(fileInputStream.getChannel(), 0L, file1.length());
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }

                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
    }

    public static String getMyDocumentFolder() {
        return ZomboidFileSystem.instance.getCacheDir();
    }

    public void saveOptions() throws IOException {
        File file = new File(ZomboidFileSystem.instance.getCacheDir() + File.separator + "options.ini");
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(file);

        try {
            fileWriter.write("version=" + this.fileversion + "\r\n");
            fileWriter.write("width=" + this.getScreenWidth() + "\r\n");
            fileWriter.write("height=" + this.getScreenHeight() + "\r\n");
            fileWriter.write("fullScreen=" + fullScreen + "\r\n");
            fileWriter.write("frameRate=" + PerformanceSettings.getLockFPS() + "\r\n");
            fileWriter.write("uncappedFPS=" + PerformanceSettings.isUncappedFPS() + "\r\n");
            fileWriter.write("iso_cursor=" + getInstance().getIsoCursorVisibility() + "\r\n");
            fileWriter.write("showCursorWhileAiming=" + OptionShowCursorWhileAiming + "\r\n");
            fileWriter.write("water=" + PerformanceSettings.WaterQuality + "\r\n");
            fileWriter.write("puddles=" + PerformanceSettings.PuddlesQuality + "\r\n");
            fileWriter.write("lighting=" + PerformanceSettings.LightingFrameSkip + "\r\n");
            fileWriter.write("lightFPS=" + PerformanceSettings.LightingFPS + "\r\n");
            fileWriter.write("perfSkybox=" + this.iPerfSkybox_new + "\r\n");
            fileWriter.write("perfPuddles=" + this.iPerfPuddles_new + "\r\n");
            fileWriter.write("bPerfReflections=" + this.bPerfReflections_new + "\r\n");
            fileWriter.write("vidMem=" + this.vidMem + "\r\n");
            fileWriter.write("language=" + this.getOptionLanguageName() + "\r\n");
            fileWriter.write("zoom=" + OptionZoom + "\r\n");
            fileWriter.write("fontSize=" + OptionFontSize + "\r\n");
            fileWriter.write("contextMenuFont=" + OptionContextMenuFont + "\r\n");
            fileWriter.write("inventoryFont=" + OptionInventoryFont + "\r\n");
            fileWriter.write("inventoryContainerSize=" + OptionInventoryContainerSize + "\r\n");
            fileWriter.write("tooltipFont=" + OptionTooltipFont + "\r\n");
            fileWriter.write("clockFormat=" + OptionClockFormat + "\r\n");
            fileWriter.write("clockSize=" + OptionClockSize + "\r\n");
            fileWriter.write("clock24Hour=" + OptionClock24Hour + "\r\n");
            fileWriter.write("measurementsFormat=" + OptionMeasurementFormat + "\r\n");
            String string0 = "";

            for (int int0 = 0; int0 < 4; int0++) {
                if (bAutoZoom[int0]) {
                    if (!string0.isEmpty()) {
                        string0 = string0 + ",";
                    }

                    string0 = string0 + (int0 + 1);
                }
            }

            fileWriter.write("autozoom=" + string0 + "\r\n");
            fileWriter.write("vsync=" + OptionVSync + "\r\n");
            fileWriter.write("soundVolume=" + OptionSoundVolume + "\r\n");
            fileWriter.write("ambientVolume=" + OptionAmbientVolume + "\r\n");
            fileWriter.write("musicVolume=" + OptionMusicVolume + "\r\n");
            fileWriter.write("jumpScareVolume=" + OptionJumpScareVolume + "\r\n");
            fileWriter.write("musicActionStyle=" + OptionMusicActionStyle + "\r\n");
            fileWriter.write("musicLibrary=" + OptionMusicLibrary + "\r\n");
            fileWriter.write("vehicleEngineVolume=" + OptionVehicleEngineVolume + "\r\n");
            fileWriter.write("voiceEnable=" + OptionVoiceEnable + "\r\n");
            fileWriter.write("voiceMode=" + OptionVoiceMode + "\r\n");
            fileWriter.write("voiceVADMode=" + OptionVoiceVADMode + "\r\n");
            fileWriter.write("voiceAGCMode=" + OptionVoiceAGCMode + "\r\n");
            fileWriter.write("voiceVolumeMic=" + OptionVoiceVolumeMic + "\r\n");
            fileWriter.write("voiceVolumePlayers=" + OptionVoiceVolumePlayers + "\r\n");
            fileWriter.write("voiceRecordDeviceName=" + OptionVoiceRecordDeviceName + "\r\n");
            fileWriter.write("reloadDifficulty=" + OptionReloadDifficulty + "\r\n");
            fileWriter.write("rackProgress=" + OptionRackProgress + "\r\n");

            for (String string1 : JoypadManager.instance.ActiveControllerGUIDs) {
                fileWriter.write("controller=" + string1 + "\r\n");
            }

            fileWriter.write("tutorialDone=" + this.isTutorialDone() + "\r\n");
            fileWriter.write("vehiclesWarningShow=" + this.isVehiclesWarningShow() + "\r\n");
            fileWriter.write("bloodDecals=" + OptionBloodDecals + "\r\n");
            fileWriter.write("borderless=" + OptionBorderlessWindow + "\r\n");
            fileWriter.write("lockCursorToWindow=" + OptionLockCursorToWindow + "\r\n");
            fileWriter.write("textureCompression=" + OptionTextureCompression + "\r\n");
            fileWriter.write("modelTextureMipmaps=" + OptionModelTextureMipmaps + "\r\n");
            fileWriter.write("texture2x=" + OptionTexture2x + "\r\n");
            fileWriter.write("maxTextureSize=" + OptionMaxTextureSize + "\r\n");
            fileWriter.write("maxVehicleTextureSize=" + OptionMaxVehicleTextureSize + "\r\n");
            fileWriter.write("zoomLevels1x=" + OptionZoomLevels1x + "\r\n");
            fileWriter.write("zoomLevels2x=" + OptionZoomLevels2x + "\r\n");
            fileWriter.write("showChatTimestamp=" + OptionShowChatTimestamp + "\r\n");
            fileWriter.write("showChatTitle=" + OptionShowChatTitle + "\r\n");
            fileWriter.write("chatFontSize=" + OptionChatFontSize + "\r\n");
            fileWriter.write("minChatOpaque=" + OptionMinChatOpaque + "\r\n");
            fileWriter.write("maxChatOpaque=" + OptionMaxChatOpaque + "\r\n");
            fileWriter.write("chatFadeTime=" + OptionChatFadeTime + "\r\n");
            fileWriter.write("chatOpaqueOnFocus=" + OptionChatOpaqueOnFocus + "\r\n");
            fileWriter.write("doneNewSaveFolder=" + this.doneNewSaveFolder + "\r\n");
            fileWriter.write("contentTranslationsEnabled=" + OptionEnableContentTranslations + "\r\n");
            fileWriter.write("showYourUsername=" + this.showYourUsername + "\r\n");
            fileWriter.write("rosewoodSpawnDone=" + this.rosewoodSpawnDone + "\r\n");
            if (this.mpTextColor != null) {
                fileWriter.write("mpTextColor=" + this.mpTextColor.r + "," + this.mpTextColor.g + "," + this.mpTextColor.b + "\r\n");
            }

            fileWriter.write(
                "objHighlightColor=" + this.objectHighlitedColor.r + "," + this.objectHighlitedColor.g + "," + this.objectHighlitedColor.b + "\r\n"
            );
            fileWriter.write("seenNews=" + this.getSeenUpdateText() + "\r\n");
            fileWriter.write("toggleToAim=" + this.isToggleToAim() + "\r\n");
            fileWriter.write("toggleToRun=" + this.isToggleToRun() + "\r\n");
            fileWriter.write("toggleToSprint=" + this.isToggleToSprint() + "\r\n");
            fileWriter.write("celsius=" + this.isCelsius() + "\r\n");
            fileWriter.write("riversideDone=" + this.isRiversideDone() + "\r\n");
            fileWriter.write("showFirstTimeSneakTutorial=" + this.isShowFirstTimeSneakTutorial() + "\r\n");
            fileWriter.write("showFirstTimeSearchTutorial=" + this.isShowFirstTimeSearchTutorial() + "\r\n");
            fileWriter.write("termsOfServiceVersion=" + this.termsOfServiceVersion + "\r\n");
            fileWriter.write("uiRenderOffscreen=" + OptionUIFBO + "\r\n");
            fileWriter.write("uiRenderFPS=" + OptionUIRenderFPS + "\r\n");
            fileWriter.write("radialMenuKeyToggle=" + OptionRadialMenuKeyToggle + "\r\n");
            fileWriter.write("reloadRadialInstant=" + OptionReloadRadialInstant + "\r\n");
            fileWriter.write("panCameraWhileAiming=" + OptionPanCameraWhileAiming + "\r\n");
            fileWriter.write("panCameraWhileDriving=" + OptionPanCameraWhileDriving + "\r\n");
            fileWriter.write("temperatureDisplayCelsius=" + OptionTemperatureDisplayCelsius + "\r\n");
            fileWriter.write("doWindSpriteEffects=" + OptionDoWindSpriteEffects + "\r\n");
            fileWriter.write("doDoorSpriteEffects=" + OptionDoDoorSpriteEffects + "\r\n");
            fileWriter.write("doDoContainerOutline=" + OptionDoContainerOutline + "\r\n");
            fileWriter.write("updateSneakButton=" + OptionUpdateSneakButton + "\r\n");
            fileWriter.write("dblTapJogToSprint=" + OptiondblTapJogToSprint + "\r\n");
            fileWriter.write("gotNewBelt=" + this.gotNewBelt + "\r\n");
            fileWriter.write("aimOutline=" + OptionAimOutline + "\r\n");
            fileWriter.write("cycleContainerKey=" + OptionCycleContainerKey + "\r\n");
            fileWriter.write("dropItemsOnSquareCenter=" + OptionDropItemsOnSquareCenter + "\r\n");
            fileWriter.write("timedActionGameSpeedReset=" + OptionTimedActionGameSpeedReset + "\r\n");
            fileWriter.write("shoulderButtonContainerSwitch=" + OptionShoulderButtonContainerSwitch + "\r\n");
            fileWriter.write("singleContextMenu=" + this.getPerPlayerBooleanString(OptionSingleContextMenu) + "\r\n");
            fileWriter.write("renderPrecipIndoors=" + OptionRenderPrecipIndoors + "\r\n");
            fileWriter.write("autoProneAtk=" + OptionAutoProneAtk + "\r\n");
            fileWriter.write("3DGroundItem=" + Option3DGroundItem + "\r\n");
            fileWriter.write("tieredZombieUpdates=" + this.getOptionTieredZombieUpdates() + "\r\n");
            fileWriter.write("progressBar=" + this.isOptionProgressBar() + "\r\n");
            fileWriter.write("corpseShadows=" + this.getOptionCorpseShadows() + "\r\n");
            fileWriter.write("simpleClothingTextures=" + this.getOptionSimpleClothingTextures() + "\r\n");
            fileWriter.write("simpleWeaponTextures=" + this.getOptionSimpleWeaponTextures() + "\r\n");
            fileWriter.write("autoDrink=" + this.getOptionAutoDrink() + "\r\n");
            fileWriter.write("leaveKeyInIgnition=" + this.getOptionLeaveKeyInIgnition() + "\r\n");
            fileWriter.write("autoWalkContainer=" + this.getOptionAutoWalkContainer() + "\r\n");
            fileWriter.write("searchModeOverlayEffect=" + this.getOptionSearchModeOverlayEffect() + "\r\n");
            fileWriter.write("ignoreProneZombieRange=" + this.getOptionIgnoreProneZombieRange() + "\r\n");
            fileWriter.write("fogQuality=" + PerformanceSettings.FogQuality + "\r\n");
            fileWriter.write("renderPrecipitation=" + OptionRenderPrecipitation + "\r\n");
            fileWriter.write("showItemModInfo=" + OptionShowItemModInfo + "\r\n");
            fileWriter.write("showSurvivalGuide=" + this.OptionShowSurvivalGuide + "\r\n");
            fileWriter.write("enableLeftJoystickRadialMenu=" + OptionEnableLeftJoystickRadialMenu + "\r\n");
            fileWriter.write("doContainerOutline=" + OptionDoContainerOutline + "\r\n");
            fileWriter.write("goodHighlightColor=" + this.goodHighlitedColor.r + "," + this.goodHighlitedColor.g + "," + this.goodHighlitedColor.b + "\r\n");
            fileWriter.write("badHighlightColor=" + this.badHighlitedColor.r + "," + this.badHighlitedColor.g + "," + this.badHighlitedColor.b + "\r\n");
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            fileWriter.close();
        }
    }

    public void setWindowed(boolean b) {
        RenderThread.invokeOnRenderContext(() -> {
            if (b != fullScreen) {
                setDisplayMode(this.getScreenWidth(), this.getScreenHeight(), b);
            }

            fullScreen = b;
            if (fakefullscreen) {
                Display.setResizable(false);
            } else {
                Display.setResizable(!b);
            }

            try {
                this.saveOptions();
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
        });
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public KahluaTable getScreenModes() {
        ArrayList arrayList = new ArrayList();
        KahluaTable table = LuaManager.platform.newTable();
        File file = new File(LuaManager.getLuaCacheDir() + File.separator + "screenresolution.ini");
        int int0 = 1;

        try {
            if (!file.exists()) {
                file.createNewFile();
                FileWriter fileWriter = new FileWriter(file);
                Integer integer0 = 0;
                Integer integer1 = 0;
                DisplayMode[] displayModes = Display.getAvailableDisplayModes();

                for (int int1 = 0; int1 < displayModes.length; int1++) {
                    integer0 = displayModes[int1].getWidth();
                    integer1 = displayModes[int1].getHeight();
                    if (!arrayList.contains(integer0 + " x " + integer1)) {
                        table.rawset(int0, integer0 + " x " + integer1);
                        fileWriter.write(integer0 + " x " + integer1 + " \r\n");
                        arrayList.add(integer0 + " x " + integer1);
                        int0++;
                    }
                }

                fileWriter.close();
            } else {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

                String string;
                for (string = null; (string = bufferedReader.readLine()) != null; int0++) {
                    table.rawset(int0, string.trim());
                }

                bufferedReader.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return table;
    }

    public static void setDisplayMode(int _width, int _height, boolean fullscreen) {
        RenderThread.invokeOnRenderContext(
            () -> {
                if (Display.getWidth() != _width
                    || Display.getHeight() != _height
                    || Display.isFullscreen() != fullscreen
                    || Display.isBorderlessWindow() != OptionBorderlessWindow) {
                    fullScreen = fullscreen;

                    try {
                        DisplayMode displayMode0 = null;
                        if (!fullscreen) {
                            if (OptionBorderlessWindow) {
                                if (Display.getWindow() != 0L && Display.isFullscreen()) {
                                    Display.setFullscreen(false);
                                }

                                long long0 = GLFW.glfwGetPrimaryMonitor();
                                GLFWVidMode gLFWVidMode = GLFW.glfwGetVideoMode(long0);
                                displayMode0 = new DisplayMode(gLFWVidMode.width(), gLFWVidMode.height());
                            } else {
                                displayMode0 = new DisplayMode(_width, _height);
                            }
                        } else {
                            DisplayMode[] displayModes = Display.getAvailableDisplayModes();
                            int int2 = 0;
                            DisplayMode displayMode1 = null;

                            for (DisplayMode displayMode2 : displayModes) {
                                if (displayMode2.getWidth() == _width && displayMode2.getHeight() == _height && displayMode2.isFullscreenCapable()) {
                                    if ((displayMode0 == null || displayMode2.getFrequency() >= int2)
                                        && (displayMode0 == null || displayMode2.getBitsPerPixel() > displayMode0.getBitsPerPixel())) {
                                        displayMode0 = displayMode2;
                                        int2 = displayMode2.getFrequency();
                                    }

                                    if (displayMode2.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()
                                        && displayMode2.getFrequency() == Display.getDesktopDisplayMode().getFrequency()) {
                                        displayMode0 = displayMode2;
                                        break;
                                    }
                                }

                                if (displayMode2.isFullscreenCapable()
                                    && (
                                        displayMode1 == null
                                            || Math.abs(displayMode2.getWidth() - _width) < Math.abs(displayMode1.getWidth() - _width)
                                            || displayMode2.getWidth() == displayMode1.getWidth() && displayMode2.getFrequency() > int2
                                    )) {
                                    displayMode1 = displayMode2;
                                    int2 = displayMode2.getFrequency();
                                    System.out.println("closest width=" + displayMode2.getWidth() + " freq=" + displayMode2.getFrequency());
                                }
                            }

                            if (displayMode0 == null && displayMode1 != null) {
                                displayMode0 = displayMode1;
                            }
                        }

                        if (displayMode0 == null) {
                            DebugLog.log("Failed to find value mode: " + _width + "x" + _height + " fs=" + fullscreen);
                            return;
                        }

                        Display.setBorderlessWindow(OptionBorderlessWindow);
                        if (fullscreen) {
                            Display.setDisplayModeAndFullscreen(displayMode0);
                        } else {
                            Display.setDisplayMode(displayMode0);
                            Display.setFullscreen(false);
                        }

                        if (!fullscreen && OptionBorderlessWindow) {
                            Display.setResizable(false);
                        } else if (!fullscreen && !fakefullscreen) {
                            Display.setResizable(false);
                            Display.setResizable(true);
                        }

                        if (Display.isCreated()) {
                            DebugLog.log(
                                "Display mode changed to "
                                    + Display.getWidth()
                                    + "x"
                                    + Display.getHeight()
                                    + " freq="
                                    + Display.getDisplayMode().getFrequency()
                                    + " fullScreen="
                                    + Display.isFullscreen()
                            );
                        }
                    } catch (LWJGLException lWJGLException) {
                        DebugLog.log("Unable to setup mode " + _width + "x" + _height + " fullscreen=" + fullscreen + lWJGLException);
                    }
                }
            }
        );
    }

    private boolean isFunctionKey(int int0) {
        return int0 >= 59 && int0 <= 68 || int0 >= 87 && int0 <= 105 || int0 == 113;
    }

    public boolean isDoingTextEntry() {
        if (CurrentTextEntryBox == null) {
            return false;
        } else {
            return !CurrentTextEntryBox.IsEditable ? false : CurrentTextEntryBox.DoingTextEntry;
        }
    }

    private void updateKeyboardAux(UITextBox2 uITextBox2, int int0) {
        boolean boolean0 = Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
        boolean boolean1 = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
        if (int0 == 28 || int0 == 156) {
            boolean boolean2 = false;
            if (UIManager.getDebugConsole() != null && uITextBox2 == UIManager.getDebugConsole().CommandLine) {
                boolean2 = true;
            }

            if (uITextBox2.multipleLine) {
                if (uITextBox2.Lines.size() < uITextBox2.getMaxLines()) {
                    if (uITextBox2.TextEntryCursorPos != uITextBox2.ToSelectionIndex) {
                        int int1 = Math.min(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
                        int int2 = Math.max(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
                        if (uITextBox2.internalText.length() > 0) {
                            uITextBox2.internalText = uITextBox2.internalText.substring(0, int1) + "\n" + uITextBox2.internalText.substring(int2);
                        } else {
                            uITextBox2.internalText = "\n";
                        }

                        uITextBox2.TextEntryCursorPos = int1 + 1;
                    } else {
                        int int3 = uITextBox2.TextEntryCursorPos;
                        String string0 = uITextBox2.internalText.substring(0, int3) + "\n" + uITextBox2.internalText.substring(int3);
                        uITextBox2.SetText(string0);
                        uITextBox2.TextEntryCursorPos = int3 + 1;
                    }

                    uITextBox2.ToSelectionIndex = uITextBox2.TextEntryCursorPos;
                    uITextBox2.CursorLine = uITextBox2.toDisplayLine(uITextBox2.TextEntryCursorPos);
                }
            } else {
                uITextBox2.onCommandEntered();
            }

            if (boolean2
                && (!GameClient.bClient || GameClient.connection.accessLevel != 1 || GameClient.connection != null && GameClient.connection.isCoopHost)) {
                UIManager.getDebugConsole().ProcessCommand();
            }
        } else if (int0 == 1) {
            uITextBox2.onOtherKey(1);
            GameKeyboard.eatKeyPress(1);
        } else if (int0 == 15) {
            uITextBox2.onOtherKey(15);
            LuaEventManager.triggerEvent("SwitchChatStream");
        } else if (int0 != 58) {
            if (int0 == 199) {
                uITextBox2.TextEntryCursorPos = 0;
                if (!uITextBox2.Lines.isEmpty()) {
                    uITextBox2.TextEntryCursorPos = uITextBox2.TextOffsetOfLineStart.get(uITextBox2.CursorLine);
                }

                if (!boolean1) {
                    uITextBox2.ToSelectionIndex = uITextBox2.TextEntryCursorPos;
                }

                uITextBox2.resetBlink();
            } else if (int0 == 207) {
                uITextBox2.TextEntryCursorPos = uITextBox2.internalText.length();
                if (!uITextBox2.Lines.isEmpty()) {
                    uITextBox2.TextEntryCursorPos = uITextBox2.TextOffsetOfLineStart.get(uITextBox2.CursorLine)
                        + uITextBox2.Lines.get(uITextBox2.CursorLine).length();
                }

                if (!boolean1) {
                    uITextBox2.ToSelectionIndex = uITextBox2.TextEntryCursorPos;
                }

                uITextBox2.resetBlink();
            } else if (int0 == 200) {
                if (uITextBox2.CursorLine > 0) {
                    int int4 = uITextBox2.TextEntryCursorPos - uITextBox2.TextOffsetOfLineStart.get(uITextBox2.CursorLine);
                    uITextBox2.CursorLine--;
                    if (int4 > uITextBox2.Lines.get(uITextBox2.CursorLine).length()) {
                        int4 = uITextBox2.Lines.get(uITextBox2.CursorLine).length();
                    }

                    uITextBox2.TextEntryCursorPos = uITextBox2.TextOffsetOfLineStart.get(uITextBox2.CursorLine) + int4;
                    if (!boolean1) {
                        uITextBox2.ToSelectionIndex = uITextBox2.TextEntryCursorPos;
                    }
                }

                uITextBox2.onPressUp();
            } else if (int0 == 208) {
                if (uITextBox2.Lines.size() - 1 > uITextBox2.CursorLine && uITextBox2.CursorLine + 1 < uITextBox2.getMaxLines()) {
                    int int5 = uITextBox2.TextEntryCursorPos - uITextBox2.TextOffsetOfLineStart.get(uITextBox2.CursorLine);
                    uITextBox2.CursorLine++;
                    if (int5 > uITextBox2.Lines.get(uITextBox2.CursorLine).length()) {
                        int5 = uITextBox2.Lines.get(uITextBox2.CursorLine).length();
                    }

                    uITextBox2.TextEntryCursorPos = uITextBox2.TextOffsetOfLineStart.get(uITextBox2.CursorLine) + int5;
                    if (!boolean1) {
                        uITextBox2.ToSelectionIndex = uITextBox2.TextEntryCursorPos;
                    }
                }

                uITextBox2.onPressDown();
            } else if (int0 != 29) {
                if (int0 != 157) {
                    if (int0 != 42) {
                        if (int0 != 54) {
                            if (int0 != 56) {
                                if (int0 != 184) {
                                    if (int0 == 203) {
                                        uITextBox2.TextEntryCursorPos--;
                                        if (uITextBox2.TextEntryCursorPos < 0) {
                                            uITextBox2.TextEntryCursorPos = 0;
                                        }

                                        if (!boolean1) {
                                            uITextBox2.ToSelectionIndex = uITextBox2.TextEntryCursorPos;
                                        }

                                        uITextBox2.resetBlink();
                                    } else if (int0 == 205) {
                                        uITextBox2.TextEntryCursorPos++;
                                        if (uITextBox2.TextEntryCursorPos > uITextBox2.internalText.length()) {
                                            uITextBox2.TextEntryCursorPos = uITextBox2.internalText.length();
                                        }

                                        if (!boolean1) {
                                            uITextBox2.ToSelectionIndex = uITextBox2.TextEntryCursorPos;
                                        }

                                        uITextBox2.resetBlink();
                                    } else if (!this.isFunctionKey(int0)) {
                                        if ((int0 == 211 || int0 == 14) && uITextBox2.TextEntryCursorPos != uITextBox2.ToSelectionIndex) {
                                            int int6 = Math.min(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
                                            int int7 = Math.max(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
                                            uITextBox2.internalText = uITextBox2.internalText.substring(0, int6) + uITextBox2.internalText.substring(int7);
                                            uITextBox2.CursorLine = uITextBox2.toDisplayLine(int6);
                                            uITextBox2.ToSelectionIndex = int6;
                                            uITextBox2.TextEntryCursorPos = int6;
                                            uITextBox2.onTextChange();
                                        } else if (int0 == 211) {
                                            if (uITextBox2.internalText.length() != 0 && uITextBox2.TextEntryCursorPos < uITextBox2.internalText.length()) {
                                                if (uITextBox2.TextEntryCursorPos > 0) {
                                                    uITextBox2.internalText = uITextBox2.internalText.substring(0, uITextBox2.TextEntryCursorPos)
                                                        + uITextBox2.internalText.substring(uITextBox2.TextEntryCursorPos + 1);
                                                } else {
                                                    uITextBox2.internalText = uITextBox2.internalText.substring(1);
                                                }

                                                uITextBox2.onTextChange();
                                            }
                                        } else if (int0 == 14) {
                                            if (uITextBox2.internalText.length() != 0 && uITextBox2.TextEntryCursorPos > 0) {
                                                if (uITextBox2.TextEntryCursorPos > uITextBox2.internalText.length()) {
                                                    uITextBox2.internalText = uITextBox2.internalText.substring(0, uITextBox2.internalText.length() - 1);
                                                } else {
                                                    int int8 = uITextBox2.TextEntryCursorPos;
                                                    uITextBox2.internalText = uITextBox2.internalText.substring(0, int8 - 1)
                                                        + uITextBox2.internalText.substring(int8);
                                                }

                                                uITextBox2.TextEntryCursorPos--;
                                                uITextBox2.ToSelectionIndex = uITextBox2.TextEntryCursorPos;
                                                uITextBox2.onTextChange();
                                            }
                                        } else if (boolean0 && int0 == 47) {
                                            String string1 = Clipboard.getClipboard();
                                            if (string1 != null) {
                                                if (uITextBox2.TextEntryCursorPos != uITextBox2.ToSelectionIndex) {
                                                    int int9 = Math.min(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
                                                    int int10 = Math.max(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
                                                    uITextBox2.internalText = uITextBox2.internalText.substring(0, int9)
                                                        + string1
                                                        + uITextBox2.internalText.substring(int10);
                                                    uITextBox2.ToSelectionIndex = int9 + string1.length();
                                                    uITextBox2.TextEntryCursorPos = int9 + string1.length();
                                                } else {
                                                    if (uITextBox2.TextEntryCursorPos < uITextBox2.internalText.length()) {
                                                        uITextBox2.internalText = uITextBox2.internalText.substring(0, uITextBox2.TextEntryCursorPos)
                                                            + string1
                                                            + uITextBox2.internalText.substring(uITextBox2.TextEntryCursorPos);
                                                    } else {
                                                        uITextBox2.internalText = uITextBox2.internalText + string1;
                                                    }

                                                    uITextBox2.TextEntryCursorPos = uITextBox2.TextEntryCursorPos + string1.length();
                                                    uITextBox2.ToSelectionIndex = uITextBox2.ToSelectionIndex + string1.length();
                                                }

                                                uITextBox2.onTextChange();
                                            }
                                        } else if (boolean0 && int0 == 46) {
                                            if (uITextBox2.TextEntryCursorPos != uITextBox2.ToSelectionIndex) {
                                                uITextBox2.updateText();
                                                int int11 = Math.min(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
                                                int int12 = Math.max(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
                                                String string2 = uITextBox2.Text.substring(int11, int12);
                                                if (string2 != null && string2.length() > 0) {
                                                    Clipboard.setClipboard(string2);
                                                }
                                            }
                                        } else if (boolean0 && int0 == 45) {
                                            if (uITextBox2.TextEntryCursorPos != uITextBox2.ToSelectionIndex) {
                                                uITextBox2.updateText();
                                                int int13 = Math.min(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
                                                int int14 = Math.max(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
                                                String string3 = uITextBox2.Text.substring(int13, int14);
                                                if (string3 != null && string3.length() > 0) {
                                                    Clipboard.setClipboard(string3);
                                                }

                                                uITextBox2.internalText = uITextBox2.internalText.substring(0, int13)
                                                    + uITextBox2.internalText.substring(int14);
                                                uITextBox2.ToSelectionIndex = int13;
                                                uITextBox2.TextEntryCursorPos = int13;
                                            }
                                        } else if (boolean0 && int0 == 30) {
                                            uITextBox2.selectAll();
                                        } else if (!uITextBox2.ignoreFirst) {
                                            if (uITextBox2.internalText.length() < uITextBox2.TextEntryMaxLength) {
                                                char char0 = Keyboard.getEventCharacter();
                                                if (char0 != 0) {
                                                    if (uITextBox2.isOnlyNumbers() && char0 != '.' && char0 != '-') {
                                                        try {
                                                            Double.parseDouble(String.valueOf(char0));
                                                        } catch (Exception exception) {
                                                            return;
                                                        }
                                                    }

                                                    if (uITextBox2.TextEntryCursorPos == uITextBox2.ToSelectionIndex) {
                                                        int int15 = uITextBox2.TextEntryCursorPos;
                                                        if (int15 < uITextBox2.internalText.length()) {
                                                            uITextBox2.internalText = uITextBox2.internalText.substring(0, int15)
                                                                + char0
                                                                + uITextBox2.internalText.substring(int15);
                                                        } else {
                                                            uITextBox2.internalText = uITextBox2.internalText + char0;
                                                        }

                                                        uITextBox2.TextEntryCursorPos++;
                                                        uITextBox2.ToSelectionIndex++;
                                                        uITextBox2.onTextChange();
                                                    } else {
                                                        int int16 = Math.min(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
                                                        int int17 = Math.max(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
                                                        if (uITextBox2.internalText.length() > 0) {
                                                            uITextBox2.internalText = uITextBox2.internalText.substring(0, int16)
                                                                + char0
                                                                + uITextBox2.internalText.substring(int17);
                                                        } else {
                                                            uITextBox2.internalText = char0 + "";
                                                        }

                                                        uITextBox2.ToSelectionIndex = int16 + 1;
                                                        uITextBox2.TextEntryCursorPos = int16 + 1;
                                                        uITextBox2.onTextChange();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void updateKeyboard() {
        if (this.isDoingTextEntry()) {
            while (Keyboard.next()) {
                if (this.isDoingTextEntry() && Keyboard.getEventKeyState()) {
                    int int0 = Keyboard.getEventKey();
                    this.updateKeyboardAux(CurrentTextEntryBox, int0);
                }
            }

            if (CurrentTextEntryBox != null && CurrentTextEntryBox.ignoreFirst) {
                CurrentTextEntryBox.ignoreFirst = false;
            }
        }
    }

    public void quit() {
        DebugLog.log("EXITDEBUG: Core.quit 1");
        if (IsoPlayer.getInstance() != null) {
            DebugLog.log("EXITDEBUG: Core.quit 2");
            bExiting = true;
        } else {
            DebugLog.log("EXITDEBUG: Core.quit 3");

            try {
                this.saveOptions();
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }

            GameClient.instance.Shutdown();
            SteamUtils.shutdown();
            DebugLog.log("EXITDEBUG: Core.quit 4");
            System.exit(0);
        }
    }

    public void exitToMenu() {
        DebugLog.log("EXITDEBUG: Core.exitToMenu");
        bExiting = true;
    }

    public void quitToDesktop() {
        DebugLog.log("EXITDEBUG: Core.quitToDesktop");
        GameWindow.closeRequested = true;
    }

    public boolean supportRes(int _width, int _height) throws LWJGLException {
        DisplayMode[] displayModes = Display.getAvailableDisplayModes();
        boolean boolean0 = false;

        for (int int0 = 0; int0 < displayModes.length; int0++) {
            if (displayModes[int0].getWidth() == _width && displayModes[int0].getHeight() == _height && displayModes[int0].isFullscreenCapable()) {
                return true;
            }
        }

        return false;
    }

    public void init(int _width, int _height) throws LWJGLException {
        System.setProperty("org.lwjgl.opengl.Window.undecorated", OptionBorderlessWindow ? "true" : "false");
        if (!System.getProperty("os.name").contains("OS X") && !System.getProperty("os.name").startsWith("Win")) {
            DebugLog.log("Creating display. If this fails, you may need to install xrandr.");
        }

        setDisplayMode(_width, _height, fullScreen);

        try {
            Display.create(new PixelFormat(32, 0, 24, 8, 0));
        } catch (LWJGLException lWJGLException) {
            Display.destroy();
            Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
            Display.create(new PixelFormat(32, 0, 24, 8, 0));
        }

        fullScreen = Display.isFullscreen();
        DebugLog.log("GraphicsCard: " + GL11.glGetString(7936) + " " + GL11.glGetString(7937));
        DebugLog.log("OpenGL version: " + GL11.glGetString(7938));
        DebugLog.log("Desktop resolution " + Display.getDesktopDisplayMode().getWidth() + "x" + Display.getDesktopDisplayMode().getHeight());
        DebugLog.log("Initial resolution " + width + "x" + height + " fullScreen=" + fullScreen);
        GLVertexBufferObject.init();
        DebugLog.General.println("VSync: %s", OptionVSync ? "ON" : "OFF");
        Display.setVSyncEnabled(OptionVSync);
        GL11.glEnable(3553);
        IndieGL.glBlendFunc(770, 771);
        GL32.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
    }

    private boolean setupMultiFBO() {
        try {
            if (!this.OffscreenBuffer.test()) {
                return false;
            } else {
                this.OffscreenBuffer.setZoomLevelsFromOption(TileScale == 2 ? OptionZoomLevels2x : OptionZoomLevels1x);
                this.OffscreenBuffer.create(Display.getWidth(), Display.getHeight());
                return true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public void setScreenSize(int _width, int _height) {
        if (width != _width || _height != height) {
            int int0 = width;
            int int1 = height;
            DebugLog.log("Screen resolution changed from " + int0 + "x" + int1 + " to " + _width + "x" + _height + " fullScreen=" + fullScreen);
            width = _width;
            height = _height;
            if (this.OffscreenBuffer != null && this.OffscreenBuffer.Current != null) {
                this.OffscreenBuffer.destroy();

                try {
                    this.OffscreenBuffer.setZoomLevelsFromOption(TileScale == 2 ? OptionZoomLevels2x : OptionZoomLevels1x);
                    this.OffscreenBuffer.create(_width, _height);
                } catch (Exception exception0) {
                    exception0.printStackTrace();
                }
            }

            try {
                LuaEventManager.triggerEvent("OnResolutionChange", int0, int1, _width, _height);
            } catch (Exception exception1) {
                exception1.printStackTrace();
            }

            for (int int2 = 0; int2 < IsoPlayer.numPlayers; int2++) {
                IsoPlayer player = IsoPlayer.players[int2];
                if (player != null) {
                    player.dirtyRecalcGridStackTime = 2.0F;
                }
            }
        }
    }

    public static boolean supportCompressedTextures() {
        return GL.getCapabilities().GL_EXT_texture_compression_latc;
    }

    public void StartFrame() {
        if (LuaManager.thread == null || !LuaManager.thread.bStep) {
            if (this.RenderShader != null && this.OffscreenBuffer.Current != null) {
                this.RenderShader.setTexture(this.OffscreenBuffer.getTexture(0));
            }

            SpriteRenderer.instance.prePopulating();
            UIManager.resize();
            boolean boolean0 = false;
            Texture.BindCount = 0;
            if (!boolean0) {
                IndieGL.glClear(18176);
                if (DebugOptions.instance.Terrain.RenderTiles.HighContrastBg.getValue()) {
                    SpriteRenderer.instance.glClearColor(255, 0, 255, 255);
                    SpriteRenderer.instance.glClear(16384);
                }
            }

            if (this.OffscreenBuffer.Current != null) {
                SpriteRenderer.instance.glBuffer(1, 0);
            }

            IndieGL.glDoStartFrame(this.getScreenWidth(), this.getScreenWidth(), this.getCurrentPlayerZoom(), 0);
            this.frameStage = 1;
        }
    }

    public void StartFrame(int nPlayer, boolean clear) {
        if (!LuaManager.thread.bStep) {
            this.OffscreenBuffer.update();
            if (this.RenderShader != null && this.OffscreenBuffer.Current != null) {
                this.RenderShader.setTexture(this.OffscreenBuffer.getTexture(nPlayer));
            }

            if (clear) {
                SpriteRenderer.instance.prePopulating();
            }

            if (!clear) {
                SpriteRenderer.instance.initFromIsoCamera(nPlayer);
            }

            Texture.BindCount = 0;
            IndieGL.glLoadIdentity();
            if (this.OffscreenBuffer.Current != null) {
                SpriteRenderer.instance.glBuffer(1, nPlayer);
            }

            IndieGL.glDoStartFrame(this.getScreenWidth(), this.getScreenHeight(), this.getZoom(nPlayer), nPlayer);
            IndieGL.glClear(17664);
            if (DebugOptions.instance.Terrain.RenderTiles.HighContrastBg.getValue()) {
                SpriteRenderer.instance.glClearColor(255, 0, 255, 255);
                SpriteRenderer.instance.glClear(16384);
            }

            this.frameStage = 1;
        }
    }

    public TextureFBO getOffscreenBuffer() {
        return this.OffscreenBuffer.getCurrent(0);
    }

    public TextureFBO getOffscreenBuffer(int nPlayer) {
        return this.OffscreenBuffer.getCurrent(nPlayer);
    }

    public void setLastRenderedFBO(TextureFBO fbo) {
        this.OffscreenBuffer.FBOrendered = fbo;
    }

    public void DoStartFrameStuff(int w, int h, float zoom, int player) {
        this.DoStartFrameStuff(w, h, zoom, player, false);
    }

    public void DoStartFrameStuff(int w, int h, float zoom, int player, boolean isTextFrame) {
        this.DoStartFrameStuffInternal(w, h, zoom, player, isTextFrame, false, false);
    }

    public void DoEndFrameStuffFx(int w, int h, int player) {
        GL11.glPopAttrib();
        this.stack--;
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        this.stack--;
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
    }

    public void DoStartFrameStuffSmartTextureFx(int w, int h, int player) {
        this.DoStartFrameStuffInternal(w, h, 1.0F, player, false, true, true);
    }

    private void DoStartFrameStuffInternal(int int2, int int3, float float0, int int4, boolean boolean2, boolean boolean0, boolean boolean1) {
        GL32.glEnable(3042);
        GL32.glDepthFunc(519);
        int int0 = this.getScreenWidth();
        int int1 = this.getScreenHeight();
        if (!boolean1 && !boolean0) {
            int2 = int0;
        }

        if (!boolean1 && !boolean0) {
            int3 = int1;
        }

        if (!boolean1 && int4 != -1) {
            int2 /= IsoPlayer.numPlayers > 1 ? 2 : 1;
            int3 /= IsoPlayer.numPlayers > 2 ? 2 : 1;
        }

        GL32.glMatrixMode(5889);
        if (!boolean0) {
            while (this.stack > 0) {
                try {
                    GL11.glPopMatrix();
                    GL11.glPopAttrib();
                    this.stack -= 2;
                } catch (OpenGLException openGLException) {
                    int int5 = GL11.glGetInteger(2992);

                    while (int5-- > 0) {
                        GL11.glPopAttrib();
                    }

                    int int6 = GL11.glGetInteger(2980);

                    while (int6-- > 1) {
                        GL11.glPopMatrix();
                    }

                    this.stack = 0;
                }
            }
        }

        GL11.glAlphaFunc(516, 0.0F);
        GL11.glPushAttrib(2048);
        this.stack++;
        GL11.glPushMatrix();
        this.stack++;
        GL11.glLoadIdentity();
        if (!boolean1 && !boolean2) {
            GLU.gluOrtho2D(0.0F, int2 * float0, int3 * float0, 0.0F);
        } else {
            GLU.gluOrtho2D(0.0F, int2, int3, 0.0F);
        }

        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        if (int4 != -1) {
            int int7 = int2;
            int int8 = int3;
            int int9;
            int int10;
            if (boolean2) {
                int9 = int2;
                int10 = int3;
            } else {
                int9 = int0;
                int10 = int1;
                if (IsoPlayer.numPlayers > 1) {
                    int9 = int0 / 2;
                }

                if (IsoPlayer.numPlayers > 2) {
                    int10 = int1 / 2;
                }
            }

            if (boolean0) {
                int7 = int9;
                int8 = int10;
            }

            float float1 = 0.0F;
            float float2 = int9 * (int4 % 2);
            if (int4 >= 2) {
                float1 += int10;
            }

            if (boolean2) {
                float1 = getInstance().getScreenHeight() - int8 - float1;
            }

            GL11.glViewport((int)float2, (int)float1, int7, int8);
            GL11.glEnable(3089);
            GL11.glScissor((int)float2, (int)float1, int7, int8);
            SpriteRenderer.instance.setRenderingPlayerIndex(int4);
        } else {
            GL11.glViewport(0, 0, int2, int3);
        }
    }

    public void DoPushIsoStuff(float ox, float oy, float oz, float useangle, boolean vehicle) {
        float float0 = getInstance().FloatParamMap.get(0);
        float float1 = getInstance().FloatParamMap.get(1);
        float float2 = getInstance().FloatParamMap.get(2);
        double double0 = float0;
        double double1 = float1;
        double double2 = float2;
        SpriteRenderState spriteRenderState = SpriteRenderer.instance.getRenderingState();
        int int0 = spriteRenderState.playerIndex;
        PlayerCamera playerCamera = spriteRenderState.playerCamera[int0];
        float float3 = playerCamera.RightClickX;
        float float4 = playerCamera.RightClickY;
        float float5 = playerCamera.getTOffX();
        float float6 = playerCamera.getTOffY();
        float float7 = playerCamera.DeferedX;
        float float8 = playerCamera.DeferedY;
        double0 -= playerCamera.XToIso(-float5 - float3, -float6 - float4, 0.0F);
        double1 -= playerCamera.YToIso(-float5 - float3, -float6 - float4, 0.0F);
        double0 += float7;
        double1 += float8;
        double double3 = playerCamera.OffscreenWidth / 1920.0F;
        double double4 = playerCamera.OffscreenHeight / 1920.0F;
        Matrix4f matrix4f0 = this.tempMatrix4f;
        matrix4f0.setOrtho(-((float)double3) / 2.0F, (float)double3 / 2.0F, -((float)double4) / 2.0F, (float)double4 / 2.0F, -10.0F, 10.0F);
        PZGLUtil.pushAndLoadMatrix(5889, matrix4f0);
        Matrix4f matrix4f1 = this.tempMatrix4f;
        float float9 = (float)(2.0 / Math.sqrt(2048.0));
        matrix4f1.scaling(0.047085002F);
        matrix4f1.scale(TileScale / 2.0F);
        matrix4f1.rotate((float) (Math.PI / 6), 1.0F, 0.0F, 0.0F);
        matrix4f1.rotate((float) (Math.PI * 3.0 / 4.0), 0.0F, 1.0F, 0.0F);
        double double5 = ox - double0;
        double double6 = oy - double1;
        matrix4f1.translate(-((float)double5), (float)(oz - double2) * 2.5F, -((float)double6));
        if (vehicle) {
            matrix4f1.scale(-1.0F, 1.0F, 1.0F);
        } else {
            matrix4f1.scale(-1.5F, 1.5F, 1.5F);
        }

        matrix4f1.rotate(useangle + (float) Math.PI, 0.0F, 1.0F, 0.0F);
        if (!vehicle) {
            matrix4f1.translate(0.0F, -0.48F, 0.0F);
        }

        PZGLUtil.pushAndLoadMatrix(5888, matrix4f1);
        GL11.glDepthRange(0.0, 1.0);
    }

    public void DoPushIsoParticleStuff(float ox, float oy, float oz) {
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        float float0 = getInstance().FloatParamMap.get(0);
        float float1 = getInstance().FloatParamMap.get(1);
        float float2 = getInstance().FloatParamMap.get(2);
        GL11.glLoadIdentity();
        double double0 = float0;
        double double1 = float1;
        double double2 = float2;
        double double3 = Math.abs(getInstance().getOffscreenWidth(0)) / 1920.0F;
        double double4 = Math.abs(getInstance().getOffscreenHeight(0)) / 1080.0F;
        GL11.glLoadIdentity();
        GL11.glOrtho(-double3 / 2.0, double3 / 2.0, -double4 / 2.0, double4 / 2.0, -10.0, 10.0);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glScaled(0.047085002F, 0.047085002F, 0.047085002F);
        GL11.glRotatef(62.65607F, 1.0F, 0.0F, 0.0F);
        GL11.glTranslated(0.0, -2.72F, 0.0);
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(1.7099999F, 14.193F, 1.7099999F);
        GL11.glScalef(0.59F, 0.59F, 0.59F);
        GL11.glTranslated(-(ox - double0), oz - double2, -(oy - double1));
        GL11.glDepthRange(0.0, 1.0);
    }

    public void DoPopIsoStuff() {
        GL11.glEnable(3008);
        GL11.glDepthFunc(519);
        GL11.glDepthMask(false);
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
    }

    public void DoEndFrameStuff(int w, int h) {
        try {
            GL11.glPopAttrib();
            this.stack--;
            GL11.glMatrixMode(5889);
            GL11.glPopMatrix();
            this.stack--;
        } catch (Exception exception) {
            int int0 = GL11.glGetInteger(2992);

            while (int0-- > 0) {
                GL11.glPopAttrib();
            }

            GL11.glMatrixMode(5889);
            int int1 = GL11.glGetInteger(2980);

            while (int1-- > 1) {
                GL11.glPopMatrix();
            }

            this.stack = 0;
        }

        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glDisable(3089);
    }

    public void RenderOffScreenBuffer() {
        if (LuaManager.thread == null || !LuaManager.thread.bStep) {
            if (this.OffscreenBuffer.Current != null) {
                IndieGL.disableStencilTest();
                IndieGL.glDoStartFrame(width, height, 1.0F, -1);
                IndieGL.glDisable(3042);
                this.OffscreenBuffer.render();
                IndieGL.glDoEndFrame();
            }
        }
    }

    public void StartFrameText(int nPlayer) {
        if (LuaManager.thread == null || !LuaManager.thread.bStep) {
            IndieGL.glDoStartFrame(IsoCamera.getScreenWidth(nPlayer), IsoCamera.getScreenHeight(nPlayer), 1.0F, nPlayer, true);
            this.frameStage = 2;
        }
    }

    public boolean StartFrameUI() {
        if (LuaManager.thread != null && LuaManager.thread.bStep) {
            return false;
        } else {
            boolean boolean0 = true;
            if (UIManager.useUIFBO) {
                if (UIManager.defaultthread == LuaManager.debugthread) {
                    this.UIRenderThisFrame = true;
                } else {
                    this.UIRenderAccumulator = this.UIRenderAccumulator + GameTime.getInstance().getMultiplier() / 1.6F;
                    this.UIRenderThisFrame = this.UIRenderAccumulator >= 30.0F / OptionUIRenderFPS;
                }

                if (this.UIRenderThisFrame) {
                    SpriteRenderer.instance.startOffscreenUI();
                    SpriteRenderer.instance.glBuffer(2, 0);
                } else {
                    boolean0 = false;
                }
            }

            IndieGL.glDoStartFrame(width, height, 1.0F, -1);
            IndieGL.glClear(1024);
            UIManager.resize();
            this.frameStage = 3;
            return boolean0;
        }
    }

    public Map<String, Integer> getKeyMaps() {
        return this.keyMaps;
    }

    public void setKeyMaps(Map<String, Integer> _keyMaps) {
        this.keyMaps = _keyMaps;
    }

    public void reinitKeyMaps() {
        this.keyMaps = new HashMap<>();
    }

    public int getKey(String keyName) {
        if (this.keyMaps == null) {
            return 0;
        } else {
            return this.keyMaps.get(keyName) != null ? this.keyMaps.get(keyName) : 0;
        }
    }

    public void addKeyBinding(String keyName, Integer key) {
        if (this.keyMaps == null) {
            this.keyMaps = new HashMap<>();
        }

        this.keyMaps.put(keyName, key);
    }

    public static boolean isLastStand() {
        return bLastStand;
    }

    public String getVersion() {
        return gameVersion + ".19";
    }

    public GameVersion getGameVersion() {
        return gameVersion;
    }

    public String getSteamServerVersion() {
        return this.steamServerVersion;
    }

    public void DoFrameReady() {
        this.updateKeyboard();
    }

    public float getCurrentPlayerZoom() {
        int int0 = IsoCamera.frameState.playerIndex;
        return this.getZoom(int0);
    }

    public float getZoom(int playerIndex) {
        return this.OffscreenBuffer != null ? this.OffscreenBuffer.zoom[playerIndex] * (TileScale / 2.0F) : 1.0F;
    }

    public float getNextZoom(int playerIndex, int del) {
        return this.OffscreenBuffer != null ? this.OffscreenBuffer.getNextZoom(playerIndex, del) : 1.0F;
    }

    public float getMinZoom() {
        return this.OffscreenBuffer != null ? this.OffscreenBuffer.getMinZoom() * (TileScale / 2.0F) : 1.0F;
    }

    public float getMaxZoom() {
        return this.OffscreenBuffer != null ? this.OffscreenBuffer.getMaxZoom() * (TileScale / 2.0F) : 1.0F;
    }

    public void doZoomScroll(int playerIndex, int del) {
        if (this.OffscreenBuffer != null) {
            this.OffscreenBuffer.doZoomScroll(playerIndex, del);
        }
    }

    public String getSaveFolder() {
        return this.saveFolder;
    }

    public boolean getOptionZoom() {
        return OptionZoom;
    }

    public void setOptionZoom(boolean zoom) {
        OptionZoom = zoom;
    }

    public void zoomOptionChanged(boolean inGame) {
        if (inGame) {
            RenderThread.invokeOnRenderContext(() -> {
                if (OptionZoom && !SafeModeForced) {
                    SafeMode = false;
                    this.bSupportsFBO = true;
                    this.OffscreenBuffer.bZoomEnabled = true;
                    this.supportsFBO();
                } else {
                    this.OffscreenBuffer.destroy();
                    SafeMode = true;
                    this.bSupportsFBO = false;
                    this.OffscreenBuffer.bZoomEnabled = false;
                }
            });
            DebugLog.log("SafeMode is " + (SafeMode ? "on" : "off"));
        } else {
            SafeMode = SafeModeForced;
            this.OffscreenBuffer.bZoomEnabled = OptionZoom && !SafeModeForced;
        }
    }

    public void zoomLevelsChanged() {
        if (this.OffscreenBuffer.Current != null) {
            RenderThread.invokeOnRenderContext(() -> {
                this.OffscreenBuffer.destroy();
                this.zoomOptionChanged(true);
            });
        }
    }

    public boolean isZoomEnabled() {
        return this.OffscreenBuffer.bZoomEnabled;
    }

    public void initFBOs() {
        if (OptionZoom && !SafeModeForced) {
            RenderThread.invokeOnRenderContext(this::supportsFBO);
        } else {
            SafeMode = true;
            this.OffscreenBuffer.bZoomEnabled = false;
        }

        DebugLog.log("SafeMode is " + (SafeMode ? "on" : "off"));
    }

    public boolean getAutoZoom(int playerIndex) {
        return bAutoZoom[playerIndex];
    }

    public void setAutoZoom(int playerIndex, boolean auto) {
        bAutoZoom[playerIndex] = auto;
        if (this.OffscreenBuffer != null) {
            this.OffscreenBuffer.bAutoZoom[playerIndex] = auto;
        }
    }

    public boolean getOptionVSync() {
        return OptionVSync;
    }

    public void setOptionVSync(boolean sync) {
        OptionVSync = sync;
        RenderThread.invokeOnRenderContext(() -> Display.setVSyncEnabled(sync));
    }

    public int getOptionSoundVolume() {
        return OptionSoundVolume;
    }

    public float getRealOptionSoundVolume() {
        return OptionSoundVolume / 10.0F;
    }

    public void setOptionSoundVolume(int volume) {
        OptionSoundVolume = Math.max(0, Math.min(10, volume));
        if (SoundManager.instance != null) {
            SoundManager.instance.setSoundVolume(volume / 10.0F);
        }
    }

    public int getOptionMusicVolume() {
        return OptionMusicVolume;
    }

    public void setOptionMusicVolume(int volume) {
        OptionMusicVolume = Math.max(0, Math.min(10, volume));
        if (SoundManager.instance != null) {
            SoundManager.instance.setMusicVolume(volume / 10.0F);
        }
    }

    public int getOptionAmbientVolume() {
        return OptionAmbientVolume;
    }

    public void setOptionAmbientVolume(int volume) {
        OptionAmbientVolume = Math.max(0, Math.min(10, volume));
        if (SoundManager.instance != null) {
            SoundManager.instance.setAmbientVolume(volume / 10.0F);
        }
    }

    public int getOptionJumpScareVolume() {
        return OptionJumpScareVolume;
    }

    public void setOptionJumpScareVolume(int volume) {
        OptionJumpScareVolume = PZMath.clamp(volume, 0, 10);
    }

    public int getOptionMusicActionStyle() {
        return OptionMusicActionStyle;
    }

    public void setOptionMusicActionStyle(int v) {
        OptionMusicActionStyle = PZMath.clamp(v, 1, 2);
    }

    public int getOptionMusicLibrary() {
        return OptionMusicLibrary;
    }

    public void setOptionMusicLibrary(int m) {
        if (m < 1) {
            m = 1;
        }

        if (m > 3) {
            m = 3;
        }

        OptionMusicLibrary = m;
    }

    public int getOptionVehicleEngineVolume() {
        return OptionVehicleEngineVolume;
    }

    public void setOptionVehicleEngineVolume(int volume) {
        OptionVehicleEngineVolume = Math.max(0, Math.min(10, volume));
        if (SoundManager.instance != null) {
            SoundManager.instance.setVehicleEngineVolume(OptionVehicleEngineVolume / 10.0F);
        }
    }

    public boolean getOptionVoiceEnable() {
        return OptionVoiceEnable;
    }

    public void setOptionVoiceEnable(boolean option) {
        if (OptionVoiceEnable != option) {
            OptionVoiceEnable = option;
            VoiceManager.instance.VoiceRestartClient(option);
        }
    }

    public int getOptionVoiceMode() {
        return OptionVoiceMode;
    }

    public void setOptionVoiceMode(int option) {
        OptionVoiceMode = option;
        VoiceManager.instance.setMode(option);
    }

    public int getOptionVoiceVADMode() {
        return OptionVoiceVADMode;
    }

    public void setOptionVoiceVADMode(int option) {
        OptionVoiceVADMode = option;
        VoiceManager.instance.setVADMode(option);
    }

    public int getOptionVoiceAGCMode() {
        return OptionVoiceAGCMode;
    }

    public void setOptionVoiceAGCMode(int option) {
        OptionVoiceAGCMode = option;
        VoiceManager.instance.setAGCMode(option);
    }

    public int getOptionVoiceVolumeMic() {
        return OptionVoiceVolumeMic;
    }

    public void setOptionVoiceVolumeMic(int option) {
        OptionVoiceVolumeMic = option;
        VoiceManager.instance.setVolumeMic(option);
    }

    public int getOptionVoiceVolumePlayers() {
        return OptionVoiceVolumePlayers;
    }

    public void setOptionVoiceVolumePlayers(int option) {
        OptionVoiceVolumePlayers = option;
        VoiceManager.instance.setVolumePlayers(option);
    }

    public String getOptionVoiceRecordDeviceName() {
        return OptionVoiceRecordDeviceName;
    }

    public void setOptionVoiceRecordDeviceName(String option) {
        OptionVoiceRecordDeviceName = option;
        VoiceManager.instance.UpdateRecordDevice();
    }

    public int getOptionVoiceRecordDevice() {
        if (!SoundDisabled && !VoiceManager.VoipDisabled) {
            int int0 = javafmod.FMOD_System_GetRecordNumDrivers();

            for (int int1 = 0; int1 < int0; int1++) {
                FMOD_DriverInfo fMOD_DriverInfo = new FMOD_DriverInfo();
                javafmod.FMOD_System_GetRecordDriverInfo(int1, fMOD_DriverInfo);
                if (fMOD_DriverInfo.name.equals(OptionVoiceRecordDeviceName)) {
                    return int1 + 1;
                }
            }

            return 0;
        } else {
            return 0;
        }
    }

    public void setOptionVoiceRecordDevice(int option) {
        if (!SoundDisabled && !VoiceManager.VoipDisabled) {
            if (option >= 1) {
                FMOD_DriverInfo fMOD_DriverInfo = new FMOD_DriverInfo();
                javafmod.FMOD_System_GetRecordDriverInfo(option - 1, fMOD_DriverInfo);
                OptionVoiceRecordDeviceName = fMOD_DriverInfo.name;
                VoiceManager.instance.UpdateRecordDevice();
            }
        }
    }

    public int getMicVolumeIndicator() {
        return VoiceManager.instance.getMicVolumeIndicator();
    }

    public boolean getMicVolumeError() {
        return VoiceManager.instance.getMicVolumeError();
    }

    public boolean getServerVOIPEnable() {
        return VoiceManager.instance.getServerVOIPEnable();
    }

    public void setTestingMicrophone(boolean testing) {
        VoiceManager.instance.setTestingMicrophone(testing);
    }

    public int getOptionReloadDifficulty() {
        return 2;
    }

    public void setOptionReloadDifficulty(int d) {
        OptionReloadDifficulty = Math.max(1, Math.min(3, d));
    }

    public boolean getOptionRackProgress() {
        return OptionRackProgress;
    }

    public void setOptionRackProgress(boolean b) {
        OptionRackProgress = b;
    }

    public int getOptionFontSize() {
        return OptionFontSize;
    }

    public void setOptionFontSize(int size) {
        OptionFontSize = PZMath.clamp(size, 1, 5);
    }

    public String getOptionContextMenuFont() {
        return OptionContextMenuFont;
    }

    public void setOptionContextMenuFont(String font) {
        OptionContextMenuFont = font;
    }

    public String getOptionInventoryFont() {
        return OptionInventoryFont;
    }

    public void setOptionInventoryFont(String font) {
        OptionInventoryFont = font;
    }

    public int getOptionInventoryContainerSize() {
        return OptionInventoryContainerSize;
    }

    public void setOptionInventoryContainerSize(int size) {
        OptionInventoryContainerSize = size;
    }

    public String getOptionTooltipFont() {
        return OptionTooltipFont;
    }

    public void setOptionTooltipFont(String font) {
        OptionTooltipFont = font;
        ObjectTooltip.checkFont();
    }

    public String getOptionMeasurementFormat() {
        return OptionMeasurementFormat;
    }

    public void setOptionMeasurementFormat(String format) {
        OptionMeasurementFormat = format;
    }

    public int getOptionClockFormat() {
        return OptionClockFormat;
    }

    public int getOptionClockSize() {
        return OptionClockSize;
    }

    public void setOptionClockFormat(int fmt) {
        if (fmt < 1) {
            fmt = 1;
        }

        if (fmt > 2) {
            fmt = 2;
        }

        OptionClockFormat = fmt;
    }

    public void setOptionClockSize(int size) {
        if (size < 1) {
            size = 1;
        }

        if (size > 2) {
            size = 2;
        }

        OptionClockSize = size;
    }

    public boolean getOptionClock24Hour() {
        return OptionClock24Hour;
    }

    public void setOptionClock24Hour(boolean b24Hour) {
        OptionClock24Hour = b24Hour;
    }

    public boolean getOptionModsEnabled() {
        return OptionModsEnabled;
    }

    public void setOptionModsEnabled(boolean enabled) {
        OptionModsEnabled = enabled;
    }

    public int getOptionBloodDecals() {
        return OptionBloodDecals;
    }

    public void setOptionBloodDecals(int n) {
        if (n < 0) {
            n = 0;
        }

        if (n > 10) {
            n = 10;
        }

        OptionBloodDecals = n;
    }

    public boolean getOptionBorderlessWindow() {
        return OptionBorderlessWindow;
    }

    public void setOptionBorderlessWindow(boolean b) {
        OptionBorderlessWindow = b;
    }

    public boolean getOptionLockCursorToWindow() {
        return OptionLockCursorToWindow;
    }

    public void setOptionLockCursorToWindow(boolean b) {
        OptionLockCursorToWindow = b;
    }

    public boolean getOptionTextureCompression() {
        return OptionTextureCompression;
    }

    public void setOptionTextureCompression(boolean b) {
        OptionTextureCompression = b;
    }

    public boolean getOptionTexture2x() {
        return OptionTexture2x;
    }

    public void setOptionTexture2x(boolean b) {
        OptionTexture2x = b;
    }

    public int getOptionMaxTextureSize() {
        return OptionMaxTextureSize;
    }

    public void setOptionMaxTextureSize(int int0) {
        OptionMaxTextureSize = PZMath.clamp(int0, 1, 4);
    }

    public int getOptionMaxVehicleTextureSize() {
        return OptionMaxVehicleTextureSize;
    }

    public void setOptionMaxVehicleTextureSize(int int0) {
        OptionMaxVehicleTextureSize = PZMath.clamp(int0, 1, 4);
    }

    public int getMaxTextureSizeFromFlags(int int0) {
        if ((int0 & 128) != 0) {
            return this.getMaxTextureSize();
        } else {
            return (int0 & 256) != 0 ? this.getMaxVehicleTextureSize() : 32768;
        }
    }

    public int getMaxTextureSizeFromOption(int int0) {
        return switch (int0) {
            case 1 -> 256;
            case 2 -> 512;
            case 3 -> 1024;
            case 4 -> 2048;
            default -> throw new IllegalStateException("Unexpected value: " + int0);
        };
    }

    public int getMaxTextureSize() {
        return this.getMaxTextureSizeFromOption(OptionMaxTextureSize);
    }

    public int getMaxVehicleTextureSize() {
        return this.getMaxTextureSizeFromOption(OptionMaxVehicleTextureSize);
    }

    public boolean getOptionModelTextureMipmaps() {
        return OptionModelTextureMipmaps;
    }

    public void setOptionModelTextureMipmaps(boolean b) {
        OptionModelTextureMipmaps = b;
    }

    public String getOptionZoomLevels1x() {
        return OptionZoomLevels1x;
    }

    public void setOptionZoomLevels1x(String levels) {
        OptionZoomLevels1x = levels == null ? "" : levels;
    }

    public String getOptionZoomLevels2x() {
        return OptionZoomLevels2x;
    }

    public void setOptionZoomLevels2x(String levels) {
        OptionZoomLevels2x = levels == null ? "" : levels;
    }

    public ArrayList<Integer> getDefaultZoomLevels() {
        return this.OffscreenBuffer.getDefaultZoomLevels();
    }

    public void setOptionActiveController(int controllerIndex, boolean active) {
        if (controllerIndex >= 0 && controllerIndex < GameWindow.GameInput.getControllerCount()) {
            Controller controller = GameWindow.GameInput.getController(controllerIndex);
            if (controller != null) {
                JoypadManager.instance.setControllerActive(controller.getGUID(), active);
            }
        }
    }

    public boolean getOptionActiveController(String guid) {
        return JoypadManager.instance.ActiveControllerGUIDs.contains(guid);
    }

    public boolean isOptionShowChatTimestamp() {
        return OptionShowChatTimestamp;
    }

    public void setOptionShowChatTimestamp(boolean optionShowChatTimestamp) {
        OptionShowChatTimestamp = optionShowChatTimestamp;
    }

    public boolean isOptionShowChatTitle() {
        return OptionShowChatTitle;
    }

    public String getOptionChatFontSize() {
        return OptionChatFontSize;
    }

    public void setOptionChatFontSize(String optionChatFontSize) {
        OptionChatFontSize = optionChatFontSize;
    }

    public void setOptionShowChatTitle(boolean optionShowChatTitle) {
        OptionShowChatTitle = optionShowChatTitle;
    }

    public float getOptionMinChatOpaque() {
        return OptionMinChatOpaque;
    }

    public void setOptionMinChatOpaque(float optionMinChatOpaque) {
        OptionMinChatOpaque = optionMinChatOpaque;
    }

    public float getOptionMaxChatOpaque() {
        return OptionMaxChatOpaque;
    }

    public void setOptionMaxChatOpaque(float optionMaxChatOpaque) {
        OptionMaxChatOpaque = optionMaxChatOpaque;
    }

    public float getOptionChatFadeTime() {
        return OptionChatFadeTime;
    }

    public void setOptionChatFadeTime(float optionChatFadeTime) {
        OptionChatFadeTime = optionChatFadeTime;
    }

    public boolean getOptionChatOpaqueOnFocus() {
        return OptionChatOpaqueOnFocus;
    }

    public void setOptionChatOpaqueOnFocus(boolean optionChatOpaqueOnFocus) {
        OptionChatOpaqueOnFocus = optionChatOpaqueOnFocus;
    }

    public boolean getOptionUIFBO() {
        return OptionUIFBO;
    }

    public void setOptionUIFBO(boolean use) {
        OptionUIFBO = use;
        if (GameWindow.states.current == IngameState.instance) {
            UIManager.useUIFBO = getInstance().supportsFBO() && OptionUIFBO;
        }
    }

    public int getOptionAimOutline() {
        return OptionAimOutline;
    }

    public void setOptionAimOutline(int choice) {
        OptionAimOutline = PZMath.clamp(choice, 1, 3);
    }

    public int getOptionUIRenderFPS() {
        return OptionUIRenderFPS;
    }

    public void setOptionUIRenderFPS(int fps) {
        OptionUIRenderFPS = fps;
    }

    public void setOptionRadialMenuKeyToggle(boolean toggle) {
        OptionRadialMenuKeyToggle = toggle;
    }

    public boolean getOptionRadialMenuKeyToggle() {
        return OptionRadialMenuKeyToggle;
    }

    public void setOptionReloadRadialInstant(boolean enable) {
        OptionReloadRadialInstant = enable;
    }

    public boolean getOptionReloadRadialInstant() {
        return OptionReloadRadialInstant;
    }

    public void setOptionPanCameraWhileAiming(boolean enable) {
        OptionPanCameraWhileAiming = enable;
    }

    public boolean getOptionPanCameraWhileAiming() {
        return OptionPanCameraWhileAiming;
    }

    public void setOptionPanCameraWhileDriving(boolean enable) {
        OptionPanCameraWhileDriving = enable;
    }

    public boolean getOptionPanCameraWhileDriving() {
        return OptionPanCameraWhileDriving;
    }

    public String getOptionCycleContainerKey() {
        return OptionCycleContainerKey;
    }

    public void setOptionCycleContainerKey(String s) {
        OptionCycleContainerKey = s;
    }

    public boolean getOptionDropItemsOnSquareCenter() {
        return OptionDropItemsOnSquareCenter;
    }

    public void setOptionDropItemsOnSquareCenter(boolean b) {
        OptionDropItemsOnSquareCenter = b;
    }

    public boolean getOptionTimedActionGameSpeedReset() {
        return OptionTimedActionGameSpeedReset;
    }

    public void setOptionTimedActionGameSpeedReset(boolean b) {
        OptionTimedActionGameSpeedReset = b;
    }

    public int getOptionShoulderButtonContainerSwitch() {
        return OptionShoulderButtonContainerSwitch;
    }

    public void setOptionShoulderButtonContainerSwitch(int v) {
        OptionShoulderButtonContainerSwitch = v;
    }

    public boolean getOptionSingleContextMenu(int playerIndex) {
        return OptionSingleContextMenu[playerIndex];
    }

    public void setOptionSingleContextMenu(int playerIndex, boolean b) {
        OptionSingleContextMenu[playerIndex] = b;
    }

    public boolean getOptionAutoDrink() {
        return OptionAutoDrink;
    }

    public void setOptionAutoDrink(boolean enable) {
        OptionAutoDrink = enable;
    }

    public boolean getOptionAutoWalkContainer() {
        return OptionAutoWalkContainer;
    }

    public void setOptionAutoWalkContainer(boolean boolean0) {
        OptionAutoWalkContainer = boolean0;
    }

    public boolean getOptionCorpseShadows() {
        return OptionCorpseShadows;
    }

    public void setOptionCorpseShadows(boolean enable) {
        OptionCorpseShadows = enable;
    }

    public boolean getOptionLeaveKeyInIgnition() {
        return OptionLeaveKeyInIgnition;
    }

    public void setOptionLeaveKeyInIgnition(boolean enable) {
        OptionLeaveKeyInIgnition = enable;
    }

    public int getOptionSearchModeOverlayEffect() {
        return OptionSearchModeOverlayEffect;
    }

    public void setOptionSearchModeOverlayEffect(int v) {
        OptionSearchModeOverlayEffect = v;
    }

    public int getOptionSimpleClothingTextures() {
        return OptionSimpleClothingTextures;
    }

    public void setOptionSimpleClothingTextures(int v) {
        OptionSimpleClothingTextures = PZMath.clamp(v, 1, 3);
    }

    public boolean isOptionSimpleClothingTextures(boolean bZombie) {
        switch (OptionSimpleClothingTextures) {
            case 1:
                return false;
            case 2:
                return bZombie;
            default:
                return true;
        }
    }

    public boolean getOptionSimpleWeaponTextures() {
        return OptionSimpleWeaponTextures;
    }

    public void setOptionSimpleWeaponTextures(boolean enable) {
        OptionSimpleWeaponTextures = enable;
    }

    public int getOptionIgnoreProneZombieRange() {
        return OptionIgnoreProneZombieRange;
    }

    public void setOptionIgnoreProneZombieRange(int i) {
        OptionIgnoreProneZombieRange = PZMath.clamp(i, 1, 5);
    }

    public float getIgnoreProneZombieRange() {
        switch (OptionIgnoreProneZombieRange) {
            case 1:
                return -1.0F;
            case 2:
                return 1.5F;
            case 3:
                return 2.0F;
            case 4:
                return 2.5F;
            case 5:
                return 3.0F;
            default:
                return -1.0F;
        }
    }

    private void readPerPlayerBoolean(String string, boolean[] booleans) {
        Arrays.fill(booleans, false);
        String[] strings = string.split(",");

        for (int int0 = 0; int0 < strings.length && int0 != 4; int0++) {
            booleans[int0] = StringUtils.tryParseBoolean(strings[int0]);
        }
    }

    private String getPerPlayerBooleanString(boolean[] booleans) {
        return String.format("%b,%b,%b,%b", booleans[0], booleans[1], booleans[2], booleans[3]);
    }

    @Deprecated
    public void ResetLua(boolean sp, String reason) throws IOException {
        this.ResetLua("default", reason);
    }

    public void ResetLua(String activeMods, String reason) throws IOException {
        if (SpriteRenderer.instance != null) {
            GameWindow.DrawReloadingLua = true;
            GameWindow.render();
            GameWindow.DrawReloadingLua = false;
        }

        RenderThread.setWaitForRenderState(false);
        SpriteRenderer.instance.notifyRenderStateQueue();
        ScriptManager.instance.Reset();
        ClothingDecals.Reset();
        BeardStyles.Reset();
        HairStyles.Reset();
        OutfitManager.Reset();
        AnimationSet.Reset();
        GameSounds.Reset();
        VehicleType.Reset();
        LuaEventManager.Reset();
        MapObjects.Reset();
        UIManager.init();
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
        WorldMap.Reset();
        LuaManager.init();
        JoypadManager.instance.Reset();
        GameKeyboard.doLuaKeyPressed = true;
        Texture.nullTextures.clear();
        ZomboidFileSystem.instance.Reset();
        ZomboidFileSystem.instance.init();
        ZomboidFileSystem.instance.loadMods(activeMods);
        ZomboidFileSystem.instance.loadModPackFiles();
        Languages.instance.init();
        Translator.loadFiles();
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

        try {
            TextManager.instance.Init();
            LuaManager.LoadDirBase();
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
            GameWindow.DoLoadingText("Reloading Lua - ERRORS!");

            try {
                Thread.sleep(2000L);
            } catch (InterruptedException interruptedException) {
            }
        }

        ZomboidGlobals.Load();
        RenderThread.setWaitForRenderState(true);
        LuaEventManager.triggerEvent("OnGameBoot");
        LuaEventManager.triggerEvent("OnMainMenuEnter");
        LuaEventManager.triggerEvent("OnResetLua", reason);
    }

    public void DelayResetLua(String activeMods, String reason) {
        this.m_delayResetLua_activeMods = activeMods;
        this.m_delayResetLua_reason = reason;
    }

    public void CheckDelayResetLua() throws IOException {
        if (this.m_delayResetLua_activeMods != null) {
            String string0 = this.m_delayResetLua_activeMods;
            String string1 = this.m_delayResetLua_reason;
            this.m_delayResetLua_activeMods = null;
            this.m_delayResetLua_reason = null;
            this.ResetLua(string0, string1);
        }
    }

    public boolean isShowPing() {
        return this.showPing;
    }

    public void setShowPing(boolean _showPing) {
        this.showPing = _showPing;
    }

    public boolean isForceSnow() {
        return this.forceSnow;
    }

    public void setForceSnow(boolean _forceSnow) {
        this.forceSnow = _forceSnow;
    }

    public boolean isZombieGroupSound() {
        return this.zombieGroupSound;
    }

    public void setZombieGroupSound(boolean _zombieGroupSound) {
        this.zombieGroupSound = _zombieGroupSound;
    }

    public String getBlinkingMoodle() {
        return this.blinkingMoodle;
    }

    public void setBlinkingMoodle(String _blinkingMoodle) {
        this.blinkingMoodle = _blinkingMoodle;
    }

    public boolean isTutorialDone() {
        return this.tutorialDone;
    }

    public void setTutorialDone(boolean done) {
        this.tutorialDone = done;
    }

    public boolean isVehiclesWarningShow() {
        return this.vehiclesWarningShow;
    }

    public void setVehiclesWarningShow(boolean done) {
        this.vehiclesWarningShow = done;
    }

    public void initPoisonousBerry() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("Base.BerryGeneric1");
        arrayList.add("Base.BerryGeneric2");
        arrayList.add("Base.BerryGeneric3");
        arrayList.add("Base.BerryGeneric4");
        arrayList.add("Base.BerryGeneric5");
        arrayList.add("Base.BerryPoisonIvy");
        this.setPoisonousBerry((String)arrayList.get(Rand.Next(0, arrayList.size() - 1)));
    }

    public void initPoisonousMushroom() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("Base.MushroomGeneric1");
        arrayList.add("Base.MushroomGeneric2");
        arrayList.add("Base.MushroomGeneric3");
        arrayList.add("Base.MushroomGeneric4");
        arrayList.add("Base.MushroomGeneric5");
        arrayList.add("Base.MushroomGeneric6");
        arrayList.add("Base.MushroomGeneric7");
        this.setPoisonousMushroom((String)arrayList.get(Rand.Next(0, arrayList.size() - 1)));
    }

    public String getPoisonousBerry() {
        return this.poisonousBerry;
    }

    public void setPoisonousBerry(String _poisonousBerry) {
        this.poisonousBerry = _poisonousBerry;
    }

    public String getPoisonousMushroom() {
        return this.poisonousMushroom;
    }

    public void setPoisonousMushroom(String _poisonousMushroom) {
        this.poisonousMushroom = _poisonousMushroom;
    }

    public static String getDifficulty() {
        return difficulty;
    }

    public static void setDifficulty(String vdifficulty) {
        difficulty = vdifficulty;
    }

    public boolean isDoneNewSaveFolder() {
        return this.doneNewSaveFolder;
    }

    public void setDoneNewSaveFolder(boolean _doneNewSaveFolder) {
        this.doneNewSaveFolder = _doneNewSaveFolder;
    }

    public static int getTileScale() {
        return TileScale;
    }

    public boolean isSelectingAll() {
        return this.isSelectingAll;
    }

    public void setIsSelectingAll(boolean _isSelectingAll) {
        this.isSelectingAll = _isSelectingAll;
    }

    public boolean getContentTranslationsEnabled() {
        return OptionEnableContentTranslations;
    }

    public void setContentTranslationsEnabled(boolean b) {
        OptionEnableContentTranslations = b;
    }

    public boolean isShowYourUsername() {
        return this.showYourUsername;
    }

    public void setShowYourUsername(boolean _showYourUsername) {
        this.showYourUsername = _showYourUsername;
    }

    public ColorInfo getMpTextColor() {
        if (this.mpTextColor == null) {
            this.mpTextColor = new ColorInfo((Rand.Next(135) + 120) / 255.0F, (Rand.Next(135) + 120) / 255.0F, (Rand.Next(135) + 120) / 255.0F, 1.0F);
        }

        return this.mpTextColor;
    }

    public void setMpTextColor(ColorInfo _mpTextColor) {
        if (_mpTextColor.r < 0.19F) {
            _mpTextColor.r = 0.19F;
        }

        if (_mpTextColor.g < 0.19F) {
            _mpTextColor.g = 0.19F;
        }

        if (_mpTextColor.b < 0.19F) {
            _mpTextColor.b = 0.19F;
        }

        this.mpTextColor = _mpTextColor;
    }

    public boolean isAzerty() {
        return this.isAzerty;
    }

    public void setAzerty(boolean _isAzerty) {
        this.isAzerty = _isAzerty;
    }

    public ColorInfo getObjectHighlitedColor() {
        return this.objectHighlitedColor;
    }

    public void setObjectHighlitedColor(ColorInfo _objectHighlitedColor) {
        this.objectHighlitedColor.set(_objectHighlitedColor);
    }

    public ColorInfo getGoodHighlitedColor() {
        return this.goodHighlitedColor;
    }

    public void setGoodHighlitedColor(ColorInfo colorInfo) {
        this.goodHighlitedColor.set(colorInfo);
    }

    public ColorInfo getBadHighlitedColor() {
        return this.badHighlitedColor;
    }

    public void setBadHighlitedColor(ColorInfo colorInfo) {
        this.badHighlitedColor.set(colorInfo);
    }

    public String getSeenUpdateText() {
        return this.seenUpdateText;
    }

    public void setSeenUpdateText(String _seenUpdateText) {
        this.seenUpdateText = _seenUpdateText;
    }

    public boolean isToggleToAim() {
        return this.toggleToAim;
    }

    public void setToggleToAim(boolean enable) {
        this.toggleToAim = enable;
    }

    public boolean isToggleToRun() {
        return this.toggleToRun;
    }

    public void setToggleToRun(boolean _toggleToRun) {
        this.toggleToRun = _toggleToRun;
    }

    public int getXAngle(int _width, float angle) {
        double double0 = Math.toRadians(225.0F + angle);
        return new Long(Math.round((Math.sqrt(2.0) * Math.cos(double0) + 1.0) * (_width / 2))).intValue();
    }

    public int getYAngle(int _width, float angle) {
        double double0 = Math.toRadians(225.0F + angle);
        return new Long(Math.round((Math.sqrt(2.0) * Math.sin(double0) + 1.0) * (_width / 2))).intValue();
    }

    public boolean isCelsius() {
        return this.celsius;
    }

    public void setCelsius(boolean _celsius) {
        this.celsius = _celsius;
    }

    public boolean isInDebug() {
        return bDebug;
    }

    public boolean isRiversideDone() {
        return this.riversideDone;
    }

    public void setRiversideDone(boolean _riversideDone) {
        this.riversideDone = _riversideDone;
    }

    public boolean isNoSave() {
        return this.noSave;
    }

    public void setNoSave(boolean _noSave) {
        this.noSave = _noSave;
    }

    public boolean isShowFirstTimeVehicleTutorial() {
        return this.showFirstTimeVehicleTutorial;
    }

    public void setShowFirstTimeVehicleTutorial(boolean _showFirstTimeVehicleTutorial) {
        this.showFirstTimeVehicleTutorial = _showFirstTimeVehicleTutorial;
    }

    public boolean getOptionDisplayAsCelsius() {
        return OptionTemperatureDisplayCelsius;
    }

    public void setOptionDisplayAsCelsius(boolean b) {
        OptionTemperatureDisplayCelsius = b;
    }

    public boolean isShowFirstTimeWeatherTutorial() {
        return this.showFirstTimeWeatherTutorial;
    }

    public void setShowFirstTimeWeatherTutorial(boolean _showFirstTimeWeatherTutorial) {
        this.showFirstTimeWeatherTutorial = _showFirstTimeWeatherTutorial;
    }

    public boolean getOptionDoWindSpriteEffects() {
        return OptionDoWindSpriteEffects;
    }

    public void setOptionDoWindSpriteEffects(boolean b) {
        OptionDoWindSpriteEffects = b;
    }

    public boolean getOptionDoDoorSpriteEffects() {
        return OptionDoDoorSpriteEffects;
    }

    public void setOptionDoDoorSpriteEffects(boolean b) {
        OptionDoDoorSpriteEffects = b;
    }

    public boolean getOptionDoContainerOutline() {
        return OptionDoContainerOutline;
    }

    public void setOptionDoContainerOutline(boolean boolean0) {
        OptionDoContainerOutline = boolean0;
    }

    public void setOptionUpdateSneakButton(boolean b) {
        OptionUpdateSneakButton = b;
    }

    public boolean getOptionUpdateSneakButton() {
        return OptionUpdateSneakButton;
    }

    public boolean isNewReloading() {
        return this.newReloading;
    }

    public void setNewReloading(boolean _newReloading) {
        this.newReloading = _newReloading;
    }

    public boolean isShowFirstTimeSneakTutorial() {
        return this.showFirstTimeSneakTutorial;
    }

    public void setShowFirstTimeSneakTutorial(boolean _showFirstTimeSneakTutorial) {
        this.showFirstTimeSneakTutorial = _showFirstTimeSneakTutorial;
    }

    public boolean isShowFirstTimeSearchTutorial() {
        return this.showFirstTimeSearchTutorial;
    }

    public void setShowFirstTimeSearchTutorial(boolean _showFirstTimeSearchTutorial) {
        this.showFirstTimeSearchTutorial = _showFirstTimeSearchTutorial;
    }

    public int getTermsOfServiceVersion() {
        return this.termsOfServiceVersion;
    }

    public void setTermsOfServiceVersion(int int0) {
        this.termsOfServiceVersion = int0;
    }

    public void setOptiondblTapJogToSprint(boolean dbltap) {
        OptiondblTapJogToSprint = dbltap;
    }

    public boolean isOptiondblTapJogToSprint() {
        return OptiondblTapJogToSprint;
    }

    public boolean isToggleToSprint() {
        return this.toggleToSprint;
    }

    public void setToggleToSprint(boolean _toggleToSprint) {
        this.toggleToSprint = _toggleToSprint;
    }

    public int getIsoCursorVisibility() {
        return this.isoCursorVisibility;
    }

    public void setIsoCursorVisibility(int _isoCursorVisibility) {
        this.isoCursorVisibility = _isoCursorVisibility;
    }

    public boolean getOptionShowCursorWhileAiming() {
        return OptionShowCursorWhileAiming;
    }

    public void setOptionShowCursorWhileAiming(boolean show) {
        OptionShowCursorWhileAiming = show;
    }

    public boolean gotNewBelt() {
        return this.gotNewBelt;
    }

    public void setGotNewBelt(boolean gotit) {
        this.gotNewBelt = gotit;
    }

    public void setAnimPopupDone(boolean done) {
        this.bAnimPopupDone = done;
    }

    public boolean isAnimPopupDone() {
        return this.bAnimPopupDone;
    }

    public void setModsPopupDone(boolean done) {
        this.bModsPopupDone = done;
    }

    public boolean isModsPopupDone() {
        return this.bModsPopupDone;
    }

    public boolean isRenderPrecipIndoors() {
        return OptionRenderPrecipIndoors;
    }

    public void setRenderPrecipIndoors(boolean optionRenderPrecipIndoors) {
        OptionRenderPrecipIndoors = optionRenderPrecipIndoors;
    }

    public boolean isCollideZombies() {
        return this.collideZombies;
    }

    public void setCollideZombies(boolean _collideZombies) {
        this.collideZombies = _collideZombies;
    }

    public boolean isFlashIsoCursor() {
        return this.flashIsoCursor;
    }

    public void setFlashIsoCursor(boolean _flashIsoCursor) {
        this.flashIsoCursor = _flashIsoCursor;
    }

    public boolean isOptionProgressBar() {
        return true;
    }

    public void setOptionProgressBar(boolean optionProgressBar) {
        OptionProgressBar = optionProgressBar;
    }

    public void setOptionLanguageName(String name) {
        OptionLanguageName = name;
    }

    public String getOptionLanguageName() {
        return OptionLanguageName;
    }

    public int getOptionRenderPrecipitation() {
        return OptionRenderPrecipitation;
    }

    public void setOptionRenderPrecipitation(int optionRenderPrecipitation) {
        OptionRenderPrecipitation = optionRenderPrecipitation;
    }

    public void setOptionAutoProneAtk(boolean optionAutoProneAtk) {
        OptionAutoProneAtk = optionAutoProneAtk;
    }

    public boolean isOptionAutoProneAtk() {
        return OptionAutoProneAtk;
    }

    public void setOption3DGroundItem(boolean option3Dgrounditem) {
        Option3DGroundItem = option3Dgrounditem;
    }

    public boolean isOption3DGroundItem() {
        return Option3DGroundItem;
    }

    public Object getOptionOnStartup(String name) {
        return optionsOnStartup.get(name);
    }

    public void setOptionOnStartup(String name, Object value) {
        optionsOnStartup.put(name, value);
    }

    public void countMissing3DItems() {
        ArrayList arrayList = ScriptManager.instance.getAllItems();
        int int0 = 0;

        for (Item item : arrayList) {
            if (item.type != Item.Type.Weapon
                && item.type != Item.Type.Moveable
                && !item.name.contains("ZedDmg")
                && !item.name.contains("Wound")
                && !item.name.contains("MakeUp")
                && !item.name.contains("Bandage")
                && !item.name.contains("Hat")
                && !item.getObsolete()
                && StringUtils.isNullOrEmpty(item.worldObjectSprite)
                && StringUtils.isNullOrEmpty(item.worldStaticModel)) {
                System.out.println("Missing: " + item.name);
                int0++;
            }
        }

        System.out.println("total missing: " + int0 + "/" + arrayList.size());
    }

    public boolean getOptionShowItemModInfo() {
        return OptionShowItemModInfo;
    }

    public void setOptionShowItemModInfo(boolean b) {
        OptionShowItemModInfo = b;
    }

    public boolean getOptionShowSurvivalGuide() {
        return this.OptionShowSurvivalGuide;
    }

    public void setOptionShowSurvivalGuide(boolean b) {
        this.OptionShowSurvivalGuide = b;
    }

    public boolean getOptionEnableLeftJoystickRadialMenu() {
        return OptionEnableLeftJoystickRadialMenu;
    }

    public void setOptionEnableLeftJoystickRadialMenu(boolean b) {
        OptionEnableLeftJoystickRadialMenu = b;
    }

    public String getVersionNumber() {
        return gameVersion.toString();
    }
}
