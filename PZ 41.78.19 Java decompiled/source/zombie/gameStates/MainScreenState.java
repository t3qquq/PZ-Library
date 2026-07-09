// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.gameStates;

import fmod.fmod.Audio;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjglx.opengl.Display;
import org.lwjglx.opengl.OpenGLException;
import zombie.DebugFileWatcher;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.SoundManager;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.asset.AssetManagers;
import zombie.characters.IsoPlayer;
import zombie.core.BoxedStaticValues;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.ProxyPrintStream;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZipLogs;
import zombie.core.opengl.RenderThread;
import zombie.core.raknet.VoiceManager;
import zombie.core.skinnedmodel.advancedanimation.AnimNodeAsset;
import zombie.core.skinnedmodel.advancedanimation.AnimNodeAssetManager;
import zombie.core.skinnedmodel.model.AiSceneAsset;
import zombie.core.skinnedmodel.model.AiSceneAssetManager;
import zombie.core.skinnedmodel.model.AnimationAsset;
import zombie.core.skinnedmodel.model.AnimationAssetManager;
import zombie.core.skinnedmodel.model.MeshAssetManager;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.model.ModelAssetManager;
import zombie.core.skinnedmodel.model.ModelMesh;
import zombie.core.skinnedmodel.model.jassimp.JAssImpImporter;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.population.ClothingItemAssetManager;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureAssetManager;
import zombie.core.textures.TextureID;
import zombie.core.textures.TextureIDAssetManager;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.input.JoypadManager;
import zombie.modding.ActiveMods;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.ui.UIManager;
import zombie.worldMap.UIWorldMap;
import zombie.worldMap.WorldMapData;
import zombie.worldMap.WorldMapDataAssetManager;

public final class MainScreenState extends GameState {
    public static String Version = "RC 3";
    public static Audio ambient;
    public static float totalScale = 1.0F;
    public float alpha = 1.0F;
    public float alphaStep = 0.03F;
    private int RestartDebounceClickTimer = 10;
    public final ArrayList<MainScreenState.ScreenElement> Elements = new ArrayList<>(16);
    public float targetAlpha = 1.0F;
    int lastH;
    int lastW;
    MainScreenState.ScreenElement Logo;
    public static MainScreenState instance;
    public boolean showLogo = false;
    private float FadeAlpha = 0.0F;
    public boolean lightningTimelineMarker = false;
    float lightningTime = 0.0F;
    public UIWorldMap m_worldMap;
    public float lightningDelta = 0.0F;
    public float lightningTargetDelta = 0.0F;
    public float lightningFullTimer = 0.0F;
    public float lightningCount = 0.0F;
    public float lightOffCount = 0.0F;
    private ConnectToServerState connectToServerState;
    private static GLFWImage windowIcon1;
    private static GLFWImage windowIcon2;
    private static ByteBuffer windowIconBB1;
    private static ByteBuffer windowIconBB2;

    public static void main(String[] strings) {
        for (int int0 = 0; int0 < strings.length; int0++) {
            if (strings[int0] != null && strings[int0].startsWith("-cachedir=")) {
                ZomboidFileSystem.instance.setCacheDir(strings[int0].replace("-cachedir=", "").trim());
            }
        }

        ZipLogs.addZipFile(false);

        try {
            String string0 = ZomboidFileSystem.instance.getCacheDir() + File.separator + "console.txt";
            FileOutputStream fileOutputStream = new FileOutputStream(string0);
            PrintStream printStream = new PrintStream(fileOutputStream, true);
            System.setOut(new ProxyPrintStream(System.out, printStream));
            System.setErr(new ProxyPrintStream(System.err, printStream));
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

        Rand.init();
        DebugLog.init();
        LoggerManager.init();
        DebugLog.log("cachedir set to \"" + ZomboidFileSystem.instance.getCacheDir() + "\"");
        JAssImpImporter.Init();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        System.out.println(simpleDateFormat.format(Calendar.getInstance().getTime()));
        System.out.println("cachedir is \"" + ZomboidFileSystem.instance.getCacheDir() + "\"");
        System.out.println("LogFileDir is \"" + LoggerManager.getLogsDir() + "\"");
        printSpecs();
        System.getProperties().list(System.out);
        System.out.println("-----");
        System.out.println("version=" + Core.getInstance().getVersion() + " demo=false");
        Display.setIcon(loadIcons());

        for (int int1 = 0; int1 < strings.length; int1++) {
            if (strings[int1] != null) {
                if (strings[int1].contains("safemode")) {
                    Core.SafeMode = true;
                    Core.SafeModeForced = true;
                } else if (strings[int1].equals("-nosound")) {
                    Core.SoundDisabled = true;
                } else if (strings[int1].equals("-aitest")) {
                    IsoPlayer.isTestAIMode = true;
                } else if (strings[int1].equals("-novoip")) {
                    VoiceManager.VoipDisabled = true;
                } else if (strings[int1].equals("-debug")) {
                    Core.bDebug = true;
                } else if (!strings[int1].startsWith("-debuglog=")) {
                    if (!strings[int1].startsWith("-cachedir=")) {
                        if (strings[int1].equals("+connect")) {
                            if (int1 + 1 < strings.length) {
                                System.setProperty("args.server.connect", strings[int1 + 1]);
                            }

                            int1++;
                        } else if (strings[int1].equals("+password")) {
                            if (int1 + 1 < strings.length) {
                                System.setProperty("args.server.password", strings[int1 + 1]);
                            }

                            int1++;
                        } else if (strings[int1].contains("-debugtranslation")) {
                            Translator.debug = true;
                        } else if ("-modfolders".equals(strings[int1])) {
                            if (int1 + 1 < strings.length) {
                                ZomboidFileSystem.instance.setModFoldersOrder(strings[int1 + 1]);
                            }

                            int1++;
                        } else if (strings[int1].equals("-nosteam")) {
                            System.setProperty("zomboid.steam", "0");
                        } else {
                            DebugLog.log("unknown option \"" + strings[int1] + "\"");
                        }
                    }
                } else {
                    for (String string1 : strings[int1].replace("-debuglog=", "").split(",")) {
                        try {
                            char char0 = string1.charAt(0);
                            string1 = char0 != '+' && char0 != '-' ? string1 : string1.substring(1);
                            DebugLog.setLogEnabled(DebugType.valueOf(string1), char0 != '-');
                        } catch (IllegalArgumentException illegalArgumentException) {
                        }
                    }
                }
            }
        }

        try {
            RenderThread.init();
            AssetManagers assetManagers = GameWindow.assetManagers;
            AiSceneAssetManager.instance.create(AiSceneAsset.ASSET_TYPE, assetManagers);
            AnimationAssetManager.instance.create(AnimationAsset.ASSET_TYPE, assetManagers);
            AnimNodeAssetManager.instance.create(AnimNodeAsset.ASSET_TYPE, assetManagers);
            ClothingItemAssetManager.instance.create(ClothingItem.ASSET_TYPE, assetManagers);
            MeshAssetManager.instance.create(ModelMesh.ASSET_TYPE, assetManagers);
            ModelAssetManager.instance.create(Model.ASSET_TYPE, assetManagers);
            TextureIDAssetManager.instance.create(TextureID.ASSET_TYPE, assetManagers);
            TextureAssetManager.instance.create(Texture.ASSET_TYPE, assetManagers);
            WorldMapDataAssetManager.instance.create(WorldMapData.ASSET_TYPE, assetManagers);
            GameWindow.InitGameThread();
            RenderThread.renderLoop();
        } catch (OpenGLException openGLException) {
            File file = new File(ZomboidFileSystem.instance.getCacheDir() + File.separator + "options2.bin");
            file.delete();
            openGLException.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void DrawTexture(Texture tex, int x, int y, int width, int height, float _alpha) {
        SpriteRenderer.instance.renderi(tex, x, y, width, height, 1.0F, 1.0F, 1.0F, _alpha, null);
    }

    public static void DrawTexture(Texture tex, int x, int y, int width, int height, Color col) {
        SpriteRenderer.instance.renderi(tex, x, y, width, height, col.r, col.g, col.b, col.a, null);
    }

    @Override
    public void enter() {
        DebugLog.log("EXITDEBUG: MainScreenState.enter 1");
        this.Elements.clear();
        this.targetAlpha = 1.0F;
        TextureID.UseFiltering = true;
        this.RestartDebounceClickTimer = 100;
        totalScale = Core.getInstance().getOffscreenHeight(0) / 1080.0F;
        this.lastW = Core.getInstance().getOffscreenWidth(0);
        this.lastH = Core.getInstance().getOffscreenHeight(0);
        this.alpha = 1.0F;
        this.showLogo = false;
        SoundManager.instance.setMusicState("MainMenu");
        int int0 = (int)(Core.getInstance().getOffscreenHeight(0) * 0.7F);
        MainScreenState.ScreenElement screenElement = new MainScreenState.ScreenElement(
            Texture.getSharedTexture("media/ui/PZ_Logo.png"),
            Core.getInstance().getOffscreenWidth(0) / 2 - (int)(Texture.getSharedTexture("media/ui/PZ_Logo.png").getWidth() * totalScale) / 2,
            int0 - (int)(350.0F * totalScale),
            0.0F,
            0.0F,
            1
        );
        screenElement.targetAlpha = 1.0F;
        screenElement.alphaStep *= 0.9F;
        this.Logo = screenElement;
        this.Elements.add(screenElement);
        TextureID.UseFiltering = false;
        LuaEventManager.triggerEvent("OnMainMenuEnter");
        instance = this;
        float float0 = TextureID.totalMemUsed / 1024.0F;
        float float1 = float0 / 1024.0F;
        DebugLog.log("EXITDEBUG: MainScreenState.enter 2");
    }

    public static MainScreenState getInstance() {
        return instance;
    }

    public boolean ShouldShowLogo() {
        return this.showLogo;
    }

    @Override
    public void exit() {
        DebugLog.log("EXITDEBUG: MainScreenState.exit 1");
        DebugLog.log("LOADED UP A TOTAL OF " + Texture.totalTextureID + " TEXTURES");
        float float0 = Core.getInstance().getOptionMusicVolume() / 10.0F;
        long long0 = Calendar.getInstance().getTimeInMillis();

        while (true) {
            this.FadeAlpha = Math.min(1.0F, (float)(Calendar.getInstance().getTimeInMillis() - long0) / 250.0F);
            this.render();
            if (this.FadeAlpha >= 1.0F) {
                SoundManager.instance.stopMusic("");
                SoundManager.instance.setMusicVolume(float0);
                DebugLog.log("EXITDEBUG: MainScreenState.exit 2");
                return;
            }

            try {
                Thread.sleep(33L);
            } catch (Exception exception) {
            }

            SoundManager.instance.Update();
        }
    }

    @Override
    public void render() {
        this.lightningTime = this.lightningTime + 1.0F * GameTime.instance.getMultipliedSecondsSinceLastUpdate();
        Core.getInstance().StartFrame();
        Core.getInstance().EndFrame();
        boolean boolean0 = UIManager.useUIFBO;
        UIManager.useUIFBO = false;
        Core.getInstance().StartFrameUI();
        IndieGL.glBlendFunc(770, 771);
        SpriteRenderer.instance.renderi(null, 0, 0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0.0F, 0.0F, 0.0F, 1.0F, null);
        IndieGL.glBlendFunc(770, 770);
        this.renderBackground();
        UIManager.render();
        if (GameWindow.DrawReloadingLua) {
            int int0 = TextManager.instance.MeasureStringX(UIFont.Small, "Reloading Lua") + 32;
            int int1 = TextManager.instance.font.getLineHeight();
            int int2 = (int)Math.ceil(int1 * 1.5);
            SpriteRenderer.instance.renderi(null, Core.getInstance().getScreenWidth() - int0 - 12, 12, int0, int2, 0.0F, 0.5F, 0.75F, 1.0F, null);
            TextManager.instance
                .DrawStringCentre(Core.getInstance().getScreenWidth() - int0 / 2 - 12, 12 + (int2 - int1) / 2, "Reloading Lua", 1.0, 1.0, 1.0, 1.0);
        }

        if (this.FadeAlpha > 0.0F) {
            UIManager.DrawTexture(UIManager.getBlack(), 0.0, 0.0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), this.FadeAlpha);
        }

        ActiveMods.renderUI();
        JoypadManager.instance.renderUI();
        Core.getInstance().EndFrameUI();
        UIManager.useUIFBO = boolean0;
    }

    public void renderBackground() {
        if (this.lightningTargetDelta == 0.0F && this.lightningDelta != 0.0F && this.lightningDelta < 0.6F && this.lightningCount == 0.0F) {
            this.lightningTargetDelta = 1.0F;
            this.lightningCount = 1.0F;
        }

        if (this.lightningTimelineMarker) {
            this.lightningTimelineMarker = false;
            this.lightningTargetDelta = 1.0F;
        }

        if (this.lightningTargetDelta == 1.0F
            && this.lightningDelta == 1.0F
            && (this.lightningFullTimer > 1.0F && this.lightningCount == 0.0F || this.lightningFullTimer > 10.0F)) {
            this.lightningTargetDelta = 0.0F;
            this.lightningFullTimer = 0.0F;
        }

        if (this.lightningTargetDelta == 1.0F && this.lightningDelta == 1.0F) {
            this.lightningFullTimer = this.lightningFullTimer + GameTime.getInstance().getMultiplier();
        }

        if (this.lightningDelta != this.lightningTargetDelta) {
            if (this.lightningDelta < this.lightningTargetDelta) {
                this.lightningDelta = this.lightningDelta + 0.17F * GameTime.getInstance().getMultiplier();
                if (this.lightningDelta > this.lightningTargetDelta) {
                    this.lightningDelta = this.lightningTargetDelta;
                    if (this.lightningDelta == 1.0F) {
                        this.showLogo = true;
                    }
                }
            }

            if (this.lightningDelta > this.lightningTargetDelta) {
                this.lightningDelta = this.lightningDelta - 0.025F * GameTime.getInstance().getMultiplier();
                if (this.lightningCount == 0.0F) {
                    this.lightningDelta -= 0.1F;
                }

                if (this.lightningDelta < this.lightningTargetDelta) {
                    this.lightningDelta = this.lightningTargetDelta;
                    this.lightningCount = 0.0F;
                }
            }
        }

        Texture texture0 = Texture.getSharedTexture("media/ui/Title.png");
        Texture texture1 = Texture.getSharedTexture("media/ui/Title2.png");
        Texture texture2 = Texture.getSharedTexture("media/ui/Title3.png");
        Texture texture3 = Texture.getSharedTexture("media/ui/Title4.png");
        if (Rand.Next(150) == 0) {
            this.lightOffCount = 10.0F;
        }

        Texture texture4 = Texture.getSharedTexture("media/ui/Title_lightning.png");
        Texture texture5 = Texture.getSharedTexture("media/ui/Title_lightning2.png");
        Texture texture6 = Texture.getSharedTexture("media/ui/Title_lightning3.png");
        Texture texture7 = Texture.getSharedTexture("media/ui/Title_lightning4.png");
        float float0 = Core.getInstance().getScreenHeight() / 1080.0F;
        float float1 = texture0.getWidth() * float0;
        float float2 = texture1.getWidth() * float0;
        float float3 = Core.getInstance().getScreenWidth() - (float1 + float2);
        if (float3 >= 0.0F) {
            float3 = 0.0F;
        }

        float float4 = 1.0F - this.lightningDelta * 0.6F;
        float float5 = 1024.0F * float0;
        float float6 = 56.0F * float0;
        DrawTexture(texture0, (int)float3, 0, (int)float1, (int)float5, float4);
        DrawTexture(texture1, (int)float3 + (int)float1, 0, (int)float1, (int)float5, float4);
        DrawTexture(texture2, (int)float3, (int)float5, (int)float1, (int)(texture2.getHeight() * float0), float4);
        DrawTexture(texture3, (int)float3 + (int)float1, (int)float5, (int)float1, (int)(texture2.getHeight() * float0), float4);
        IndieGL.glBlendFunc(770, 1);
        DrawTexture(texture4, (int)float3, 0, (int)float1, (int)float5, this.lightningDelta);
        DrawTexture(texture5, (int)float3 + (int)float1, 0, (int)float1, (int)float5, this.lightningDelta);
        DrawTexture(texture6, (int)float3, (int)float5, (int)float1, (int)float5, this.lightningDelta);
        DrawTexture(texture7, (int)float3 + (int)float1, (int)float5, (int)float1, (int)float5, this.lightningDelta);
        IndieGL.glBlendFunc(770, 771);
    }

    @Override
    public GameStateMachine.StateAction update() {
        if (this.connectToServerState != null) {
            GameStateMachine.StateAction stateAction = this.connectToServerState.update();
            if (stateAction == GameStateMachine.StateAction.Continue) {
                this.connectToServerState.exit();
                this.connectToServerState = null;
                return GameStateMachine.StateAction.Remain;
            }
        }

        LuaEventManager.triggerEvent("OnFETick", BoxedStaticValues.toDouble(0.0));
        if (this.RestartDebounceClickTimer > 0) {
            this.RestartDebounceClickTimer--;
        }

        for (int int0 = 0; int0 < this.Elements.size(); int0++) {
            MainScreenState.ScreenElement screenElement = this.Elements.get(int0);
            screenElement.update();
        }

        this.lastW = Core.getInstance().getOffscreenWidth(0);
        this.lastH = Core.getInstance().getOffscreenHeight(0);
        DebugFileWatcher.instance.update();
        ZomboidFileSystem.instance.update();

        try {
            Core.getInstance().CheckDelayResetLua();
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
        }

        return GameStateMachine.StateAction.Remain;
    }

    public void setConnectToServerState(ConnectToServerState state) {
        this.connectToServerState = state;
    }

    @Override
    public GameState redirectState() {
        return null;
    }

    public static Buffer loadIcons() {
        Buffer buffer = null;
        String string = System.getProperty("os.name").toUpperCase(Locale.ENGLISH);
        if (string.contains("WIN")) {
            try {
                buffer = GLFWImage.create(2);
                BufferedImage bufferedImage0 = ImageIO.read(new File("media" + File.separator + "ui" + File.separator + "zomboidIcon16.png").getAbsoluteFile());
                ByteBuffer byteBuffer0;
                windowIconBB1 = byteBuffer0 = loadInstance(bufferedImage0, 16);
                GLFWImage gLFWImage0;
                windowIcon1 = gLFWImage0 = GLFWImage.create().set(16, 16, byteBuffer0);
                buffer.put(0, gLFWImage0);
                bufferedImage0 = ImageIO.read(new File("media" + File.separator + "ui" + File.separator + "zomboidIcon32.png").getAbsoluteFile());
                windowIconBB2 = byteBuffer0 = loadInstance(bufferedImage0, 32);
                windowIcon2 = gLFWImage0 = GLFWImage.create().set(32, 32, byteBuffer0);
                buffer.put(1, gLFWImage0);
            } catch (IOException iOException0) {
                iOException0.printStackTrace();
            }
        } else if (string.contains("MAC")) {
            try {
                buffer = GLFWImage.create(1);
                BufferedImage bufferedImage1 = ImageIO.read(new File("media" + File.separator + "ui" + File.separator + "zomboidIcon128.png").getAbsoluteFile());
                ByteBuffer byteBuffer1;
                windowIconBB1 = byteBuffer1 = loadInstance(bufferedImage1, 128);
                GLFWImage gLFWImage1;
                windowIcon1 = gLFWImage1 = GLFWImage.create().set(128, 128, byteBuffer1);
                buffer.put(0, gLFWImage1);
            } catch (IOException iOException1) {
                iOException1.printStackTrace();
            }
        } else {
            try {
                buffer = GLFWImage.create(1);
                BufferedImage bufferedImage2 = ImageIO.read(new File("media" + File.separator + "ui" + File.separator + "zomboidIcon32.png").getAbsoluteFile());
                ByteBuffer byteBuffer2;
                windowIconBB1 = byteBuffer2 = loadInstance(bufferedImage2, 32);
                GLFWImage gLFWImage2;
                windowIcon1 = gLFWImage2 = GLFWImage.create().set(32, 32, byteBuffer2);
                buffer.put(0, gLFWImage2);
            } catch (IOException iOException2) {
                iOException2.printStackTrace();
            }
        }

        return buffer;
    }

    private static ByteBuffer loadInstance(BufferedImage bufferedImage1, int int0) {
        BufferedImage bufferedImage0 = new BufferedImage(int0, int0, 3);
        Graphics2D graphics2D = bufferedImage0.createGraphics();
        double double0 = getIconRatio(bufferedImage1, bufferedImage0);
        double double1 = bufferedImage1.getWidth() * double0;
        double double2 = bufferedImage1.getHeight() * double0;
        graphics2D.drawImage(
            bufferedImage1,
            (int)((bufferedImage0.getWidth() - double1) / 2.0),
            (int)((bufferedImage0.getHeight() - double2) / 2.0),
            (int)double1,
            (int)double2,
            null
        );
        graphics2D.dispose();
        return convertToByteBuffer(bufferedImage0);
    }

    private static double getIconRatio(BufferedImage bufferedImage1, BufferedImage bufferedImage0) {
        double double0 = 1.0;
        if (bufferedImage1.getWidth() > bufferedImage0.getWidth()) {
            double0 = (double)bufferedImage0.getWidth() / bufferedImage1.getWidth();
        } else {
            double0 = bufferedImage0.getWidth() / bufferedImage1.getWidth();
        }

        if (bufferedImage1.getHeight() > bufferedImage0.getHeight()) {
            double double1 = (double)bufferedImage0.getHeight() / bufferedImage1.getHeight();
            if (double1 < double0) {
                double0 = double1;
            }
        } else {
            double double2 = bufferedImage0.getHeight() / bufferedImage1.getHeight();
            if (double2 < double0) {
                double0 = double2;
            }
        }

        return double0;
    }

    public static ByteBuffer convertToByteBuffer(BufferedImage image) {
        byte[] bytes = new byte[image.getWidth() * image.getHeight() * 4];
        byte byte0 = 0;

        for (int int0 = 0; int0 < image.getHeight(); int0++) {
            for (int int1 = 0; int1 < image.getWidth(); int1++) {
                int int2 = image.getRGB(int1, int0);
                bytes[byte0 + 0] = (byte)(int2 << 8 >> 24);
                bytes[byte0 + 1] = (byte)(int2 << 16 >> 24);
                bytes[byte0 + 2] = (byte)(int2 << 24 >> 24);
                bytes[byte0 + 3] = (byte)(int2 >> 24);
                byte0 += 4;
            }
        }

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.flip();
        return byteBuffer;
    }

    private static void printSpecs() {
        try {
            System.out.println("===== System specs =====");
            long long0 = 1024L;
            long long1 = long0 * 1024L;
            long long2 = long1 * 1024L;
            Map map = System.getenv();
            System.out
                .println("OS: " + System.getProperty("os.name") + ", version: " + System.getProperty("os.version") + ", arch: " + System.getProperty("os.arch"));
            if (map.containsKey("PROCESSOR_IDENTIFIER")) {
                System.out.println("Processor: " + (String)map.get("PROCESSOR_IDENTIFIER"));
            }

            if (map.containsKey("NUMBER_OF_PROCESSORS")) {
                System.out.println("Processor cores: " + (String)map.get("NUMBER_OF_PROCESSORS"));
            }

            System.out.println("Available processors (cores): " + Runtime.getRuntime().availableProcessors());
            System.out.println("Memory free: " + (float)Runtime.getRuntime().freeMemory() / (float)long1 + " MB");
            long long3 = Runtime.getRuntime().maxMemory();
            System.out.println("Memory max: " + (long3 == Long.MAX_VALUE ? "no limit" : (float)long3 / (float)long1) + " MB");
            System.out.println("Memory  total available to JVM: " + (float)Runtime.getRuntime().totalMemory() / (float)long1 + " MB");
            File[] files = File.listRoots();

            for (File file : files) {
                System.out
                    .println(
                        file.getAbsolutePath()
                            + ", Total: "
                            + (float)file.getTotalSpace() / (float)long2
                            + " GB, Free: "
                            + (float)file.getFreeSpace() / (float)long2
                            + " GB"
                    );
            }

            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                System.out.println("Mobo = " + wmic("baseboard", new String[]{"Product"}));
                System.out.println("CPU = " + wmic("cpu", new String[]{"Manufacturer", "MaxClockSpeed", "Name"}));
                System.out.println("Graphics = " + wmic("path Win32_videocontroller", new String[]{"AdapterRAM", "DriverVersion", "Name"}));
                System.out.println("VideoMode = " + wmic("path Win32_videocontroller", new String[]{"VideoModeDescription"}));
                System.out.println("Sound = " + wmic("path Win32_sounddevice", new String[]{"Manufacturer", "Name"}));
                System.out.println("Memory RAM = " + wmic("memorychip", new String[]{"Capacity", "Manufacturer"}));
            }

            System.out.println("========================");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static String wmic(String string2, String[] strings0) {
        String string0 = "";

        try {
            String string1 = "WMIC " + string2 + " GET";

            for (int int0 = 0; int0 < strings0.length; int0++) {
                string1 = string1 + " " + strings0[int0];
                if (int0 < strings0.length - 1) {
                    string1 = string1 + ",";
                }
            }

            Process process = Runtime.getRuntime().exec(new String[]{"CMD", "/C", string1});
            process.getOutputStream().close();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String string3 = "";

            String string4;
            while ((string4 = bufferedReader.readLine()) != null) {
                string3 = string3 + string4;
            }

            for (String string5 : strings0) {
                string3 = string3.replaceAll(string5, "");
            }

            string3 = string3.trim().replaceAll(" ( )+", "=");
            String[] strings1 = string3.split("=");
            if (strings1.length > strings0.length) {
                string0 = "{ ";
                int int1 = strings1.length / strings0.length;

                for (int int2 = 0; int2 < int1; int2++) {
                    string0 = string0 + "[";

                    for (int int3 = 0; int3 < strings0.length; int3++) {
                        int int4 = int2 * strings0.length + int3;
                        string0 = string0 + strings0[int3] + "=" + strings1[int4];
                        if (int3 < strings0.length - 1) {
                            string0 = string0 + ",";
                        }
                    }

                    string0 = string0 + "]";
                    if (int2 < int1 - 1) {
                        string0 = string0 + ", ";
                    }
                }

                string0 = string0 + " }";
            } else {
                string0 = "[";

                for (int int5 = 0; int5 < strings1.length; int5++) {
                    string0 = string0 + strings0[int5] + "=" + strings1[int5];
                    if (int5 < strings1.length - 1) {
                        string0 = string0 + ",";
                    }
                }

                string0 = string0 + "]";
            }

            return string0;
        } catch (Exception exception) {
            return "Couldnt get info...";
        }
    }

    public class Credit {
        public int disappearDelay = 200;
        public Texture name;
        public float nameAlpha;
        public float nameAppearDelay = 40.0F;
        public float nameTargetAlpha;
        public Texture title;
        public float titleAlpha;
        public float titleTargetAlpha = 1.0F;

        public Credit(Texture texture0, Texture texture1) {
            this.titleAlpha = 0.0F;
            this.nameTargetAlpha = 0.0F;
            this.nameAlpha = 0.0F;
            this.title = texture0;
            this.name = texture1;
        }
    }

    public static class ScreenElement {
        public float alpha = 0.0F;
        public float alphaStep = 0.2F;
        public boolean jumpBack = true;
        public float sx = 0.0F;
        public float sy = 0.0F;
        public float targetAlpha = 0.0F;
        public Texture tex;
        public int TicksTillTargetAlpha = 0;
        public float x = 0.0F;
        public int xCount = 1;
        public float xVel = 0.0F;
        public float xVelO = 0.0F;
        public float y = 0.0F;
        public float yVel = 0.0F;
        public float yVelO = 0.0F;

        public ScreenElement(Texture _tex, int _x, int _y, float _xVel, float _yVel, int _xCount) {
            this.x = this.sx = _x;
            this.y = this.sy = _y - _tex.getHeight() * MainScreenState.totalScale;
            this.xVel = _xVel;
            this.yVel = _yVel;
            this.tex = _tex;
            this.xCount = _xCount;
        }

        public void render() {
            int int0 = (int)this.x;
            int int1 = (int)this.y;

            for (int int2 = 0; int2 < this.xCount; int2++) {
                MainScreenState.DrawTexture(
                    this.tex,
                    int0,
                    int1,
                    (int)(this.tex.getWidth() * MainScreenState.totalScale),
                    (int)(this.tex.getHeight() * MainScreenState.totalScale),
                    this.alpha
                );
                int0 = (int)(int0 + this.tex.getWidth() * MainScreenState.totalScale);
            }

            TextManager.instance
                .DrawStringRight(
                    Core.getInstance().getOffscreenWidth(0) - 5,
                    Core.getInstance().getOffscreenHeight(0) - 15,
                    "Version: " + MainScreenState.Version,
                    1.0,
                    1.0,
                    1.0,
                    1.0
                );
        }

        public void setY(float _y) {
            this.y = this.sy = _y - this.tex.getHeight() * MainScreenState.totalScale;
        }

        public void update() {
            this.x = this.x + this.xVel * MainScreenState.totalScale;
            this.y = this.y + this.yVel * MainScreenState.totalScale;
            this.TicksTillTargetAlpha--;
            if (this.TicksTillTargetAlpha <= 0) {
                this.targetAlpha = 1.0F;
            }

            if (this.jumpBack && this.sx - this.x > this.tex.getWidth() * MainScreenState.totalScale) {
                this.x = this.x + this.tex.getWidth() * MainScreenState.totalScale;
            }

            if (this.alpha < this.targetAlpha) {
                this.alpha = this.alpha + this.alphaStep;
                if (this.alpha > this.targetAlpha) {
                    this.alpha = this.targetAlpha;
                }
            } else if (this.alpha > this.targetAlpha) {
                this.alpha = this.alpha - this.alphaStep;
                if (this.alpha < this.targetAlpha) {
                    this.alpha = this.targetAlpha;
                }
            }
        }
    }
}
