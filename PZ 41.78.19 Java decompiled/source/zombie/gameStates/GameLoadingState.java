// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.gameStates;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.lwjglx.input.Keyboard;
import zombie.AmbientStreamManager;
import zombie.ChunkMapFilenames;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SoundManager;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatManager;
import zombie.chat.ChatUtility;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.ThreadGroups;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.raknet.UdpConnection;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.runtime.RuntimeAnimationScript;
import zombie.core.textures.Texture;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.globalObjects.CGlobalObjects;
import zombie.globalObjects.SGlobalObjects;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.input.Mouse;
import zombie.inventory.RecipeManager;
import zombie.iso.IsoCamera;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoPuddles;
import zombie.iso.IsoWater;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.WorldStreamer;
import zombie.iso.areas.SafeHouse;
import zombie.iso.sprite.SkyBox;
import zombie.iso.weather.ClimateManager;
import zombie.modding.ActiveMods;
import zombie.modding.ActiveModsFile;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.NetworkAIParams;
import zombie.network.ServerOptions;
import zombie.scripting.ScriptManager;
import zombie.ui.TextManager;
import zombie.ui.TutorialManager;
import zombie.ui.UIFont;
import zombie.ui.UIManager;
import zombie.vehicles.BaseVehicle;
import zombie.world.WorldDictionary;

public final class GameLoadingState extends GameState {
    public static Thread loader = null;
    public static boolean newGame = true;
    private static long startTime;
    public static boolean build23Stop = false;
    public static boolean unexpectedError = false;
    public static String GameLoadingString = "";
    public static boolean playerWrongIP = false;
    private static boolean bShowedUI = false;
    private static boolean bShowedClickToSkip = false;
    public static boolean mapDownloadFailed = false;
    private volatile boolean bWaitForAssetLoadingToFinish1 = false;
    private volatile boolean bWaitForAssetLoadingToFinish2 = false;
    private final Object assetLock1 = "Asset Lock 1";
    private final Object assetLock2 = "Asset Lock 2";
    private String text;
    private float width;
    public static boolean playerCreated = false;
    public static boolean bDone = false;
    public static boolean convertingWorld = false;
    public static int convertingFileCount = -1;
    public static int convertingFileMax = -1;
    public int Stage = 0;
    float TotalTime = 33.0F;
    float loadingDotTick = 0.0F;
    String loadingDot = "";
    private float clickToSkipAlpha = 1.0F;
    private boolean clickToSkipFadeIn = false;
    public float Time = 0.0F;
    public boolean bForceDone = false;

    @Override
    public void enter() {
        if (GameClient.bClient) {
            this.text = Translator.getText("UI_DirectConnectionPortWarning", ServerOptions.getInstance().UDPPort.getValue());
            this.width = TextManager.instance.MeasureStringX(UIFont.NewMedium, this.text) + 8;
        }

        GameWindow.bLoadedAsClient = GameClient.bClient;
        GameWindow.OkToSaveOnExit = false;
        bShowedUI = false;
        ChunkMapFilenames.instance.clear();
        DebugLog.log("Savefile name is \"" + Core.GameSaveWorld + "\"");
        GameLoadingString = "";

        try {
            LuaManager.LoadDirBase("server");
            LuaManager.finishChecksum();
        } catch (Exception exception0) {
            ExceptionLogger.logException(exception0);
        }

        RecipeManager.LoadedAfterLua();
        Core.getInstance().initFBOs();
        Core.getInstance().initShaders();
        SkyBox.getInstance();
        IsoPuddles.getInstance();
        IsoWater.getInstance();
        GameWindow.bServerDisconnected = false;
        if (GameClient.bClient && !GameClient.instance.bConnected) {
            GameClient.instance.init();

            for (Core.GameMode = "Multiplayer"; GameClient.instance.ID == -1; GameClient.instance.update()) {
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }

            Core.GameSaveWorld = "clienttest" + GameClient.instance.ID;
            LuaManager.GlobalObject.deleteSave("clienttest" + GameClient.instance.ID);
            LuaManager.GlobalObject.createWorld("clienttest" + GameClient.instance.ID);
        }

        if (Core.GameSaveWorld.isEmpty()) {
            DebugLog.log("No savefile directory was specified.  It's a bug.");
            GameWindow.DoLoadingText("No savefile directory was specified.  The game will now close.  Sorry!");

            try {
                Thread.sleep(4000L);
            } catch (Exception exception1) {
            }

            System.exit(-1);
        }

        File file0 = new File(ZomboidFileSystem.instance.getCurrentSaveDir());
        if (!file0.exists() && !Core.getInstance().isNoSave()) {
            DebugLog.log("The savefile directory doesn't exist.  It's a bug.");
            GameWindow.DoLoadingText("The savefile directory doesn't exist.  The game will now close.  Sorry!");

            try {
                Thread.sleep(4000L);
            } catch (Exception exception2) {
            }

            System.exit(-1);
        }

        try {
            if (!GameClient.bClient && !GameServer.bServer && !Core.bTutorial && !Core.isLastStand() && !"Multiplayer".equals(Core.GameMode)) {
                FileWriter fileWriter = new FileWriter(new File(ZomboidFileSystem.instance.getCacheDir() + File.separator + "latestSave.ini"));
                fileWriter.write(IsoWorld.instance.getWorld() + "\r\n");
                fileWriter.write(Core.getInstance().getGameMode() + "\r\n");
                fileWriter.write(IsoWorld.instance.getDifficulty() + "\r\n");
                fileWriter.flush();
                fileWriter.close();
            }
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }

        bDone = false;
        this.bForceDone = false;
        IsoChunkMap.CalcChunkWidth();
        LosUtil.init(IsoChunkMap.ChunkGridWidth * 10, IsoChunkMap.ChunkGridWidth * 10);
        this.Time = 0.0F;
        this.Stage = 0;
        this.clickToSkipAlpha = 1.0F;
        this.clickToSkipFadeIn = false;
        startTime = System.currentTimeMillis();
        SoundManager.instance.Purge();
        SoundManager.instance.setMusicState("Loading");
        LuaEventManager.triggerEvent("OnPreMapLoad");
        newGame = true;
        build23Stop = false;
        unexpectedError = false;
        mapDownloadFailed = false;
        playerCreated = false;
        convertingWorld = false;
        convertingFileCount = 0;
        convertingFileMax = -1;
        File file1 = ZomboidFileSystem.instance.getFileInCurrentSave("map_ver.bin");
        if (file1.exists()) {
            newGame = false;
        }

        if (GameClient.bClient) {
            newGame = false;
        }

        WorldDictionary.setIsNewGame(newGame);
        GameKeyboard.bNoEventsWhileLoading = true;
        loader = new Thread(ThreadGroups.Workers, new Runnable() {
            @Override
            public void run() {
                try {
                    this.runInner();
                } catch (Throwable throwable) {
                    GameLoadingState.unexpectedError = true;
                    ExceptionLogger.logException(throwable);
                }
            }

            private void runInner() throws Exception {
                GameLoadingState.this.bWaitForAssetLoadingToFinish1 = true;
                synchronized (GameLoadingState.this.assetLock1) {
                    while (GameLoadingState.this.bWaitForAssetLoadingToFinish1) {
                        try {
                            GameLoadingState.this.assetLock1.wait();
                        } catch (InterruptedException interruptedException0) {
                        }
                    }
                }

                boolean boolean0 = new File(ZomboidFileSystem.instance.getGameModeCacheDir() + File.separator).mkdir();
                BaseVehicle.LoadAllVehicleTextures();
                if (GameClient.bClient) {
                    GameClient.instance.GameLoadingRequestData();
                }

                TutorialManager.instance = new TutorialManager();
                GameTime.setInstance(new GameTime());
                ClimateManager.setInstance(new ClimateManager());
                IsoWorld.instance = new IsoWorld();
                DebugOptions.testThreadCrash(0);
                IsoWorld.instance.init();
                if (GameWindow.bServerDisconnected) {
                    GameLoadingState.bDone = true;
                } else if (!GameLoadingState.playerWrongIP) {
                    if (!GameLoadingState.build23Stop) {
                        LuaEventManager.triggerEvent("OnGameTimeLoaded");
                        SGlobalObjects.initSystems();
                        CGlobalObjects.initSystems();
                        IsoObjectPicker.Instance.Init();
                        TutorialManager.instance.init();
                        TutorialManager.instance.CreateQuests();
                        File file = ZomboidFileSystem.instance.getFileInCurrentSave("map_t.bin");
                        if (file.exists()) {
                        }

                        if (!GameServer.bServer) {
                            file = ZomboidFileSystem.instance.getFileInCurrentSave("map_ver.bin");
                            boolean boolean1 = !file.exists();
                            if (boolean1 || IsoWorld.SavedWorldVersion != 195) {
                                if (!boolean1 && IsoWorld.SavedWorldVersion != 195) {
                                    GameLoadingState.GameLoadingString = "Saving converted world.";
                                }

                                try {
                                    GameWindow.save(true);
                                } catch (Throwable throwable) {
                                    ExceptionLogger.logException(throwable);
                                }
                            }
                        }

                        ChatUtility.InitAllowedChatIcons();
                        ChatManager.getInstance().init(true, IsoPlayer.getInstance());
                        GameLoadingState.this.bWaitForAssetLoadingToFinish2 = true;
                        synchronized (GameLoadingState.this.assetLock2) {
                            while (GameLoadingState.this.bWaitForAssetLoadingToFinish2) {
                                try {
                                    GameLoadingState.this.assetLock2.wait();
                                } catch (InterruptedException interruptedException1) {
                                }
                            }
                        }

                        UIManager.bSuspend = false;
                        GameLoadingState.playerCreated = true;
                        GameLoadingState.GameLoadingString = "";
                        GameLoadingState.SendDone();
                    }
                }
            }
        });
        UIManager.bSuspend = true;
        loader.setName("GameLoadingThread");
        loader.setUncaughtExceptionHandler(GameWindow::uncaughtException);
        loader.start();
    }

    public static void SendDone() {
        DebugLog.log("game loading took " + (System.currentTimeMillis() - startTime + 999L) / 1000L + " seconds");
        if (!GameClient.bClient) {
            bDone = true;
            GameKeyboard.bNoEventsWhileLoading = false;
        } else {
            GameClient.instance.sendLoginQueueDone2(System.currentTimeMillis() - startTime);
        }
    }

    public static void Done() {
        bDone = true;
        GameKeyboard.bNoEventsWhileLoading = false;
    }

    @Override
    public GameState redirectState() {
        return new IngameState();
    }

    @Override
    public void exit() {
        if (GameClient.bClient) {
            NetworkAIParams.Init();
        }

        UIManager.init();
        LuaEventManager.triggerEvent("OnCreatePlayer", 0, IsoPlayer.players[0]);
        loader = null;
        bDone = false;
        this.Stage = 0;
        IsoCamera.SetCharacterToFollow(IsoPlayer.getInstance());
        if (GameClient.bClient && !ServerOptions.instance.SafehouseAllowTrepass.getValue()) {
            SafeHouse safeHouse = SafeHouse.isSafeHouse(IsoPlayer.getInstance().getCurrentSquare(), GameClient.username, true);
            if (safeHouse != null) {
                IsoPlayer.getInstance().setX(safeHouse.getX() - 1);
                IsoPlayer.getInstance().setY(safeHouse.getY() - 1);
            }
        }

        SoundManager.instance.stopMusic("");
        AmbientStreamManager.instance.init();
        if (IsoPlayer.getInstance() != null && IsoPlayer.getInstance().isAsleep()) {
            UIManager.setFadeBeforeUI(IsoPlayer.getInstance().getPlayerNum(), true);
            UIManager.FadeOut(IsoPlayer.getInstance().getPlayerNum(), 2.0);
            UIManager.setFadeTime(IsoPlayer.getInstance().getPlayerNum(), 0.0);
            UIManager.getSpeedControls().SetCurrentGameSpeed(3);
        }

        if (!GameClient.bClient) {
            ActiveMods activeMods = ActiveMods.getById("currentGame");
            activeMods.checkMissingMods();
            activeMods.checkMissingMaps();
            ActiveMods.setLoadedMods(activeMods);
            String string = ZomboidFileSystem.instance.getFileNameInCurrentSave("mods.txt");
            ActiveModsFile activeModsFile = new ActiveModsFile();
            activeModsFile.write(string, activeMods);
        }

        GameWindow.OkToSaveOnExit = true;
    }

    @Override
    public void render() {
        this.loadingDotTick = this.loadingDotTick + GameTime.getInstance().getMultiplier();
        if (this.loadingDotTick > 20.0F) {
            this.loadingDot = ".";
        }

        if (this.loadingDotTick > 40.0F) {
            this.loadingDot = "..";
        }

        if (this.loadingDotTick > 60.0F) {
            this.loadingDot = "...";
        }

        if (this.loadingDotTick > 80.0F) {
            this.loadingDot = "";
            this.loadingDotTick = 0.0F;
        }

        this.Time = this.Time + GameTime.instance.getTimeDelta();
        float float0 = 0.0F;
        float float1 = 0.0F;
        float float2 = 0.0F;
        if (this.Stage == 0) {
            float float3 = this.Time;
            float float4 = 0.0F;
            float float5 = 1.0F;
            float float6 = 5.0F;
            float float7 = 7.0F;
            float float8 = 0.0F;
            if (float3 > float4 && float3 < float5) {
                float8 = (float3 - float4) / (float5 - float4);
            }

            if (float3 >= float5 && float3 <= float6) {
                float8 = 1.0F;
            }

            if (float3 > float6 && float3 < float7) {
                float8 = 1.0F - (float3 - float6) / (float7 - float6);
            }

            if (float3 >= float7) {
                this.Stage++;
            }

            float0 = float8;
        }

        if (this.Stage == 1) {
            float float9 = this.Time;
            float float10 = 7.0F;
            float float11 = 8.0F;
            float float12 = 13.0F;
            float float13 = 15.0F;
            float float14 = 0.0F;
            if (float9 > float10 && float9 < float11) {
                float14 = (float9 - float10) / (float11 - float10);
            }

            if (float9 >= float11 && float9 <= float12) {
                float14 = 1.0F;
            }

            if (float9 > float12 && float9 < float13) {
                float14 = 1.0F - (float9 - float12) / (float13 - float12);
            }

            if (float9 >= float13) {
                this.Stage++;
            }

            float1 = float14;
        }

        if (this.Stage == 2) {
            float float15 = this.Time;
            float float16 = 15.0F;
            float float17 = 16.0F;
            float float18 = 31.0F;
            float float19 = this.TotalTime;
            float float20 = 0.0F;
            if (float15 > float16 && float15 < float17) {
                float20 = (float15 - float16) / (float17 - float16);
            }

            if (float15 >= float17 && float15 <= float18) {
                float20 = 1.0F;
            }

            if (float15 > float18 && float15 < float19) {
                float20 = 1.0F - (float15 - float18) / (float19 - float18);
            }

            if (float15 >= float19) {
                this.Stage++;
            }

            float2 = float20;
        }

        Core.getInstance().StartFrame();
        Core.getInstance().EndFrame();
        boolean boolean0 = UIManager.useUIFBO;
        UIManager.useUIFBO = false;
        Core.getInstance().StartFrameUI();
        SpriteRenderer.instance.renderi(null, 0, 0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0.0F, 0.0F, 0.0F, 1.0F, null);
        if (mapDownloadFailed) {
            int int0 = Core.getInstance().getScreenWidth() / 2;
            int int1 = Core.getInstance().getScreenHeight() / 2;
            int int2 = TextManager.instance.getFontFromEnum(UIFont.Medium).getLineHeight();
            int int3 = int1 - int2 / 2;
            String string0 = Translator.getText("UI_GameLoad_MapDownloadFailed");
            TextManager.instance.DrawStringCentre(UIFont.Medium, int0, int3, string0, 0.8, 0.1, 0.1, 1.0);
            UIManager.render();
            Core.getInstance().EndFrameUI();
        } else if (unexpectedError) {
            int int4 = TextManager.instance.getFontFromEnum(UIFont.Medium).getLineHeight();
            int int5 = TextManager.instance.getFontFromEnum(UIFont.Small).getLineHeight();
            byte byte0 = 8;
            byte byte1 = 2;
            int int6 = int4 + byte0 + int5 + byte1 + int5;
            int int7 = Core.getInstance().getScreenWidth() / 2;
            int int8 = Core.getInstance().getScreenHeight() / 2;
            int int9 = int8 - int6 / 2;
            TextManager.instance.DrawStringCentre(UIFont.Medium, int7, int9, Translator.getText("UI_GameLoad_UnexpectedError1"), 0.8, 0.1, 0.1, 1.0);
            TextManager.instance
                .DrawStringCentre(UIFont.Small, int7, int9 + int4 + byte0, Translator.getText("UI_GameLoad_UnexpectedError2"), 1.0, 1.0, 1.0, 1.0);
            String string1 = ZomboidFileSystem.instance.getCacheDir() + File.separator + "console.txt";
            TextManager.instance.DrawStringCentre(UIFont.Small, int7, int9 + int4 + byte0 + int5 + byte1, string1, 1.0, 1.0, 1.0, 1.0);
            UIManager.render();
            Core.getInstance().EndFrameUI();
        } else if (GameWindow.bServerDisconnected) {
            int int10 = Core.getInstance().getScreenWidth() / 2;
            int int11 = Core.getInstance().getScreenHeight() / 2;
            int int12 = TextManager.instance.getFontFromEnum(UIFont.Medium).getLineHeight();
            byte byte2 = 2;
            int int13 = int11 - (int12 + byte2 + int12) / 2;
            String string2 = GameWindow.kickReason;
            if (string2 == null) {
                string2 = Translator.getText("UI_OnConnectFailed_ConnectionLost");
            }

            TextManager.instance.DrawStringCentre(UIFont.Medium, int10, int13, string2, 0.8, 0.1, 0.1, 1.0);
            UIManager.render();
            Core.getInstance().EndFrameUI();
        } else {
            if (build23Stop) {
                TextManager.instance
                    .DrawStringCentre(
                        UIFont.Small,
                        Core.getInstance().getScreenWidth() / 2,
                        Core.getInstance().getScreenHeight() - 100,
                        "This save is incompatible. Please switch to Steam branch \"build23\" to continue this save.",
                        0.8,
                        0.1,
                        0.1,
                        1.0
                    );
            } else if (convertingWorld) {
                TextManager.instance
                    .DrawStringCentre(
                        UIFont.Small,
                        Core.getInstance().getScreenWidth() / 2,
                        Core.getInstance().getScreenHeight() - 100,
                        Translator.getText("UI_ConvertWorld"),
                        0.5,
                        0.5,
                        0.5,
                        1.0
                    );
                if (convertingFileMax != -1) {
                    TextManager.instance
                        .DrawStringCentre(
                            UIFont.Small,
                            Core.getInstance().getScreenWidth() / 2,
                            Core.getInstance().getScreenHeight() - 100 + TextManager.instance.getFontFromEnum(UIFont.Small).getLineHeight() + 8,
                            convertingFileCount + " / " + convertingFileMax,
                            0.5,
                            0.5,
                            0.5,
                            1.0
                        );
                }
            }

            if (playerWrongIP) {
                int int14 = Core.getInstance().getScreenWidth() / 2;
                int int15 = Core.getInstance().getScreenHeight() / 2;
                int int16 = TextManager.instance.getFontFromEnum(UIFont.Medium).getLineHeight();
                byte byte3 = 2;
                int int17 = int15 - (int16 + byte3 + int16) / 2;
                String string3 = GameLoadingString;
                if (GameLoadingString == null) {
                    string3 = "";
                }

                TextManager.instance.DrawStringCentre(UIFont.Medium, int14, int17, string3, 0.8, 0.1, 0.1, 1.0);
                UIManager.render();
                Core.getInstance().EndFrameUI();
            } else {
                if (GameClient.bClient) {
                    String string4 = GameLoadingString;
                    if (GameLoadingString == null) {
                        string4 = "";
                    }

                    TextManager.instance
                        .DrawStringCentre(
                            UIFont.Small, Core.getInstance().getScreenWidth() / 2, Core.getInstance().getScreenHeight() - 60, string4, 0.5, 0.5, 0.5, 1.0
                        );
                    if (GameClient.connection.getConnectionType() == UdpConnection.ConnectionType.Steam) {
                        SpriteRenderer.instance
                            .render(
                                null,
                                (Core.getInstance().getScreenWidth() - this.width) / 2.0F,
                                Core.getInstance().getScreenHeight() - 32,
                                this.width,
                                18.0F,
                                1.0F,
                                0.4F,
                                0.35F,
                                0.8F,
                                null
                            );
                        TextManager.instance
                            .DrawStringCentre(
                                UIFont.Medium,
                                Core.getInstance().getScreenWidth() / 2,
                                Core.getInstance().getScreenHeight() - 32,
                                this.text,
                                0.1,
                                0.1,
                                0.1,
                                1.0
                            );
                    }
                } else if (!playerCreated && newGame && !Core.isLastStand()) {
                    TextManager.instance
                        .DrawStringCentre(
                            UIFont.Small,
                            Core.getInstance().getScreenWidth() / 2,
                            Core.getInstance().getScreenHeight() - 60,
                            Translator.getText("UI_Loading").replace(".", ""),
                            0.5,
                            0.5,
                            0.5,
                            1.0
                        );
                    TextManager.instance
                        .DrawString(
                            UIFont.Small,
                            Core.getInstance().getScreenWidth() / 2
                                + TextManager.instance.MeasureStringX(UIFont.Small, Translator.getText("UI_Loading").replace(".", "")) / 2
                                + 1,
                            Core.getInstance().getScreenHeight() - 60,
                            this.loadingDot,
                            0.5,
                            0.5,
                            0.5,
                            1.0
                        );
                }

                if (this.Stage == 0) {
                    int int18 = Core.getInstance().getScreenWidth() / 2;
                    int int19 = Core.getInstance().getScreenHeight() / 2 - TextManager.instance.getFontFromEnum(UIFont.Intro).getLineHeight() / 2;
                    TextManager.instance.DrawStringCentre(UIFont.Intro, int18, int19, Translator.getText("UI_Intro1"), 1.0, 1.0, 1.0, float0);
                }

                if (this.Stage == 1) {
                    int int20 = Core.getInstance().getScreenWidth() / 2;
                    int int21 = Core.getInstance().getScreenHeight() / 2 - TextManager.instance.getFontFromEnum(UIFont.Intro).getLineHeight() / 2;
                    TextManager.instance.DrawStringCentre(UIFont.Intro, int20, int21, Translator.getText("UI_Intro2"), 1.0, 1.0, 1.0, float1);
                }

                if (this.Stage == 2) {
                    int int22 = Core.getInstance().getScreenWidth() / 2;
                    int int23 = Core.getInstance().getScreenHeight() / 2 - TextManager.instance.getFontFromEnum(UIFont.Intro).getLineHeight() / 2;
                    TextManager.instance.DrawStringCentre(UIFont.Intro, int22, int23, Translator.getText("UI_Intro3"), 1.0, 1.0, 1.0, float2);
                }

                if (Core.getInstance().getDebug()) {
                    bShowedClickToSkip = true;
                }

                if (bDone && playerCreated && (!newGame || this.Time >= this.TotalTime || Core.isLastStand() || "Tutorial".equals(Core.GameMode))) {
                    if (this.clickToSkipFadeIn) {
                        this.clickToSkipAlpha = this.clickToSkipAlpha + GameTime.getInstance().getMultiplier() / 1.6F / 30.0F;
                        if (this.clickToSkipAlpha > 1.0F) {
                            this.clickToSkipAlpha = 1.0F;
                            this.clickToSkipFadeIn = false;
                        }
                    } else {
                        bShowedClickToSkip = true;
                        this.clickToSkipAlpha = this.clickToSkipAlpha - GameTime.getInstance().getMultiplier() / 1.6F / 30.0F;
                        if (this.clickToSkipAlpha < 0.25F) {
                            this.clickToSkipFadeIn = true;
                        }
                    }

                    if (GameWindow.ActivatedJoyPad != null && !JoypadManager.instance.JoypadList.isEmpty()) {
                        Texture texture = Texture.getSharedTexture("media/ui/xbox/XBOX_A.png");
                        if (texture != null) {
                            int int24 = TextManager.instance.getFontFromEnum(UIFont.Small).getLineHeight();
                            SpriteRenderer.instance
                                .renderi(
                                    texture,
                                    Core.getInstance().getScreenWidth() / 2
                                        - TextManager.instance.MeasureStringX(UIFont.Small, Translator.getText("UI_PressAToStart")) / 2
                                        - 8
                                        - texture.getWidth(),
                                    Core.getInstance().getScreenHeight() - 60 + int24 / 2 - texture.getHeight() / 2,
                                    texture.getWidth(),
                                    texture.getHeight(),
                                    1.0F,
                                    1.0F,
                                    1.0F,
                                    this.clickToSkipAlpha,
                                    null
                                );
                        }

                        TextManager.instance
                            .DrawStringCentre(
                                UIFont.Small,
                                Core.getInstance().getScreenWidth() / 2,
                                Core.getInstance().getScreenHeight() - 60,
                                Translator.getText("UI_PressAToStart"),
                                1.0,
                                1.0,
                                1.0,
                                this.clickToSkipAlpha
                            );
                    } else {
                        TextManager.instance
                            .DrawStringCentre(
                                UIFont.NewLarge,
                                Core.getInstance().getScreenWidth() / 2,
                                Core.getInstance().getScreenHeight() - 60,
                                Translator.getText("UI_ClickToSkip"),
                                1.0,
                                1.0,
                                1.0,
                                this.clickToSkipAlpha
                            );
                    }
                }

                ActiveMods.renderUI();
                Core.getInstance().EndFrameUI();
                UIManager.useUIFBO = boolean0;
            }
        }
    }

    @Override
    public GameStateMachine.StateAction update() {
        if (this.bWaitForAssetLoadingToFinish1 && !OutfitManager.instance.isLoadingClothingItems()) {
            if (Core.bDebug) {
                OutfitManager.instance.debugOutfits();
            }

            synchronized (this.assetLock1) {
                this.bWaitForAssetLoadingToFinish1 = false;
                this.assetLock1.notifyAll();
            }
        }

        if (this.bWaitForAssetLoadingToFinish2 && !ModelManager.instance.isLoadingAnimations() && !GameWindow.fileSystem.hasWork()) {
            synchronized (this.assetLock2) {
                this.bWaitForAssetLoadingToFinish2 = false;
                this.assetLock2.notifyAll();

                for (RuntimeAnimationScript runtimeAnimationScript : ScriptManager.instance.getAllRuntimeAnimationScripts()) {
                    runtimeAnimationScript.exec();
                }
            }
        }

        if (!unexpectedError && !GameWindow.bServerDisconnected && !playerWrongIP) {
            if (!bDone) {
                return GameStateMachine.StateAction.Remain;
            } else if (WorldStreamer.instance.isBusy()) {
                return GameStateMachine.StateAction.Remain;
            } else if (ModelManager.instance.isLoadingAnimations()) {
                return GameStateMachine.StateAction.Remain;
            } else if (!bShowedClickToSkip) {
                return GameStateMachine.StateAction.Remain;
            } else {
                if (Mouse.isButtonDown(0)) {
                    this.bForceDone = true;
                }

                if (GameWindow.ActivatedJoyPad != null && GameWindow.ActivatedJoyPad.isAPressed()) {
                    this.bForceDone = true;
                }

                if (this.bForceDone) {
                    SoundManager.instance.playUISound("UIClickToStart");
                    this.bForceDone = false;
                    return GameStateMachine.StateAction.Continue;
                } else {
                    return GameStateMachine.StateAction.Remain;
                }
            }
        } else {
            if (!bShowedUI) {
                bShowedUI = true;
                IsoPlayer.setInstance(null);
                IsoPlayer.players[0] = null;
                UIManager.UI.clear();
                LuaEventManager.Reset();
                LuaManager.call("ISGameLoadingUI_OnGameLoadingUI", "");
                UIManager.bSuspend = false;
            }

            if (Keyboard.isKeyDown(1)) {
                GameClient.instance.Shutdown();
                SteamUtils.shutdown();
                System.exit(1);
            }

            return GameStateMachine.StateAction.Remain;
        }
    }
}
