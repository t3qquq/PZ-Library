// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.krka.kahlua.vm.KahluaThread;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.Translator;
import zombie.core.Styles.TransparentStyle;
import zombie.core.Styles.UIFBOStyle;
import zombie.core.opengl.RenderThread;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureFBO;
import zombie.debug.DebugOptions;
import zombie.gameStates.GameLoadingState;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.iso.IsoCamera;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.areas.SafeHouse;
import zombie.network.CoopMaster;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.util.list.PZArrayUtil;

public final class UIManager {
    public static int lastMouseX = 0;
    public static int lastMouseY = 0;
    public static IsoObjectPicker.ClickObject Picked = null;
    public static Clock clock;
    public static final ArrayList<UIElement> UI = new ArrayList<>();
    public static ObjectTooltip toolTip = null;
    public static Texture mouseArrow;
    public static Texture mouseExamine;
    public static Texture mouseAttack;
    public static Texture mouseGrab;
    public static SpeedControls speedControls;
    public static UIDebugConsole DebugConsole;
    public static UIServerToolbox ServerToolbox;
    public static final MoodlesUI[] MoodleUI = new MoodlesUI[4];
    public static boolean bFadeBeforeUI = false;
    public static final ActionProgressBar[] ProgressBar = new ActionProgressBar[4];
    public static float FadeAlpha = 1.0F;
    public static int FadeInTimeMax = 180;
    public static int FadeInTime = 180;
    public static boolean FadingOut = false;
    public static Texture lastMouseTexture;
    public static IsoObject LastPicked = null;
    public static final ArrayList<String> DoneTutorials = new ArrayList<>();
    public static float lastOffX = 0.0F;
    public static float lastOffY = 0.0F;
    public static ModalDialog Modal = null;
    public static boolean KeyDownZoomIn = false;
    public static boolean KeyDownZoomOut = false;
    public static boolean doTick;
    public static boolean VisibleAllUI = true;
    public static TextureFBO UIFBO;
    public static boolean useUIFBO = false;
    public static Texture black = null;
    public static boolean bSuspend = false;
    public static float lastAlpha = 10000.0F;
    public static final Vector2 PickedTileLocal = new Vector2();
    public static final Vector2 PickedTile = new Vector2();
    public static IsoObject RightDownObject = null;
    public static long uiUpdateTimeMS = 0L;
    public static long uiUpdateIntervalMS = 0L;
    public static long uiRenderTimeMS = 0L;
    public static long uiRenderIntervalMS = 0L;
    private static final ArrayList<UIElement> tutorialStack = new ArrayList<>();
    public static final ArrayList<UIElement> toTop = new ArrayList<>();
    public static KahluaThread defaultthread = null;
    public static KahluaThread previousThread = null;
    static final ArrayList<UIElement> toRemove = new ArrayList<>();
    static final ArrayList<UIElement> toAdd = new ArrayList<>();
    static int wheel = 0;
    static int lastwheel = 0;
    static final ArrayList<UIElement> debugUI = new ArrayList<>();
    static boolean bShowLuaDebuggerOnError = true;
    public static String luaDebuggerAction = null;
    static final UIManager.Sync sync = new UIManager.Sync();
    private static boolean showPausedMessage = true;
    private static UIElement playerInventoryUI;
    private static UIElement playerLootUI;
    private static UIElement playerInventoryTooltip;
    private static UIElement playerLootTooltip;
    private static final UIManager.FadeInfo[] playerFadeInfo = new UIManager.FadeInfo[4];

    public static void AddUI(UIElement el) {
        toRemove.remove(el);
        toRemove.add(el);
        toAdd.remove(el);
        toAdd.add(el);
    }

    public static void RemoveElement(UIElement el) {
        toAdd.remove(el);
        toRemove.remove(el);
        toRemove.add(el);
    }

    public static void clearArrays() {
        toAdd.clear();
        toRemove.clear();
        UI.clear();
    }

    public static void closeContainers() {
    }

    public static void CloseContainers() {
    }

    public static void DrawTexture(Texture tex, double x, double y) {
        double double0 = x + tex.offsetX;
        double double1 = y + tex.offsetY;
        SpriteRenderer.instance.renderi(tex, (int)double0, (int)double1, tex.getWidth(), tex.getHeight(), 1.0F, 1.0F, 1.0F, 1.0F, null);
    }

    public static void DrawTexture(Texture tex, double x, double y, double width, double height, double alpha) {
        double double0 = x + tex.offsetX;
        double double1 = y + tex.offsetY;
        SpriteRenderer.instance.renderi(tex, (int)double0, (int)double1, (int)width, (int)height, 1.0F, 1.0F, 1.0F, (float)alpha, null);
    }

    public static void FadeIn(double seconds) {
        setFadeInTimeMax((int)(seconds * 30.0 * (PerformanceSettings.getLockFPS() / 30.0F)));
        setFadeInTime(getFadeInTimeMax());
        setFadingOut(false);
    }

    public static void FadeOut(double seconds) {
        setFadeInTimeMax((int)(seconds * 30.0 * (PerformanceSettings.getLockFPS() / 30.0F)));
        setFadeInTime(getFadeInTimeMax());
        setFadingOut(true);
    }

    public static void CreateFBO(int width, int height) {
        if (Core.SafeMode) {
            useUIFBO = false;
        } else {
            if (useUIFBO && (UIFBO == null || UIFBO.getTexture().getWidth() != width || UIFBO.getTexture().getHeight() != height)) {
                if (UIFBO != null) {
                    RenderThread.invokeOnRenderContext(() -> UIFBO.destroy());
                }

                try {
                    UIFBO = createTexture(width, height, false);
                } catch (Exception exception) {
                    useUIFBO = false;
                    exception.printStackTrace();
                }
            }
        }
    }

    public static TextureFBO createTexture(float x, float y, boolean test) throws Exception {
        if (test) {
            Texture texture0 = new Texture((int)x, (int)y, 16);
            TextureFBO textureFBO = new TextureFBO(texture0);
            textureFBO.destroy();
            return null;
        } else {
            Texture texture1 = new Texture((int)x, (int)y, 16);
            return new TextureFBO(texture1);
        }
    }

    public static void init() {
        showPausedMessage = true;
        getUI().clear();
        debugUI.clear();
        clock = null;

        for (int int0 = 0; int0 < 4; int0++) {
            MoodleUI[int0] = null;
        }

        setSpeedControls(new SpeedControls());
        SpeedControls.instance = getSpeedControls();
        setbFadeBeforeUI(false);
        VisibleAllUI = true;

        for (int int1 = 0; int1 < 4; int1++) {
            playerFadeInfo[int1].setFadeBeforeUI(false);
            playerFadeInfo[int1].setFadeTime(0);
            playerFadeInfo[int1].setFadingOut(false);
        }

        setPicked(null);
        setLastPicked(null);
        RightDownObject = null;
        if (IsoPlayer.getInstance() != null) {
            if (!Core.GameMode.equals("LastStand") && !GameClient.bClient) {
                getUI().add(getSpeedControls());
            }

            if (!GameServer.bServer) {
                setToolTip(new ObjectTooltip());
                if (Core.getInstance().getOptionClockSize() == 2) {
                    setClock(new Clock(Core.getInstance().getOffscreenWidth(0) - 166, 10));
                } else {
                    setClock(new Clock(Core.getInstance().getOffscreenWidth(0) - 91, 10));
                }

                if (!Core.GameMode.equals("LastStand")) {
                    getUI().add(getClock());
                }

                getUI().add(getToolTip());
                setDebugConsole(new UIDebugConsole(20, Core.getInstance().getScreenHeight() - 265));
                setServerToolbox(new UIServerToolbox(100, 200));
                if (Core.bDebug && DebugOptions.instance.UIDebugConsoleStartVisible.getValue()) {
                    DebugConsole.setVisible(true);
                } else {
                    DebugConsole.setVisible(false);
                }

                if (CoopMaster.instance.isRunning()) {
                    ServerToolbox.setVisible(true);
                } else {
                    ServerToolbox.setVisible(false);
                }

                for (int int2 = 0; int2 < 4; int2++) {
                    MoodlesUI moodlesUI = new MoodlesUI();
                    setMoodleUI(int2, moodlesUI);
                    moodlesUI.setVisible(true);
                    getUI().add(moodlesUI);
                }

                getUI().add(getDebugConsole());
                getUI().add(getServerToolbox());
                setLastMouseTexture(getMouseArrow());
                resize();

                for (int int3 = 0; int3 < 4; int3++) {
                    ActionProgressBar actionProgressBar = new ActionProgressBar(0, 0);
                    actionProgressBar.setRenderThisPlayerOnly(int3);
                    setProgressBar(int3, actionProgressBar);
                    getUI().add(actionProgressBar);
                    actionProgressBar.setValue(1.0F);
                    actionProgressBar.setVisible(false);
                }

                playerInventoryUI = null;
                playerLootUI = null;
                LuaEventManager.triggerEvent("OnCreateUI");
            }
        }
    }

    public static void render() {
        if (!useUIFBO || Core.getInstance().UIRenderThisFrame) {
            if (!bSuspend) {
                long long0 = System.currentTimeMillis();
                uiRenderIntervalMS = Math.min(long0 - uiRenderTimeMS, 1000L);
                uiRenderTimeMS = long0;
                UIElement.StencilLevel = 0;
                if (useUIFBO) {
                    SpriteRenderer.instance.setDefaultStyle(UIFBOStyle.instance);
                }

                UITransition.UpdateAll();
                if (getBlack() == null) {
                    setBlack(Texture.getSharedTexture("black.png"));
                }

                if (LuaManager.thread == defaultthread) {
                    LuaEventManager.triggerEvent("OnPreUIDraw");
                }

                int int0 = Mouse.getXA();
                int int1 = Mouse.getYA();
                if (isbFadeBeforeUI()) {
                    setFadeAlpha(getFadeInTime().floatValue() / getFadeInTimeMax().floatValue());
                    if (getFadeAlpha() > 1.0) {
                        setFadeAlpha(1.0);
                    }

                    if (getFadeAlpha() < 0.0) {
                        setFadeAlpha(0.0);
                    }

                    if (isFadingOut()) {
                        setFadeAlpha(1.0 - getFadeAlpha());
                    }

                    if (IsoCamera.CamCharacter != null && getFadeAlpha() > 0.0) {
                        DrawTexture(getBlack(), 0.0, 0.0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), getFadeAlpha());
                    }
                }

                setLastAlpha(getFadeAlpha().floatValue());

                for (int int2 = 0; int2 < IsoPlayer.numPlayers; int2++) {
                    if (IsoPlayer.players[int2] != null && playerFadeInfo[int2].isFadeBeforeUI()) {
                        playerFadeInfo[int2].render();
                    }
                }

                for (int int3 = 0; int3 < getUI().size(); int3++) {
                    if ((UI.get(int3).isIgnoreLossControl() || !TutorialManager.instance.StealControl) && !UI.get(int3).isFollowGameWorld()) {
                        try {
                            if (getUI().get(int3).isDefaultDraw()) {
                                getUI().get(int3).render();
                            }
                        } catch (Exception exception) {
                            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, exception);
                        }
                    }
                }

                if (getToolTip() != null) {
                    getToolTip().render();
                }

                if (isShowPausedMessage() && GameTime.isGamePaused() && (getModal() == null || !Modal.isVisible()) && VisibleAllUI) {
                    String string = Translator.getText("IGUI_GamePaused");
                    int int4 = TextManager.instance.MeasureStringX(UIFont.Small, string) + 32;
                    int int5 = TextManager.instance.font.getLineHeight();
                    int int6 = (int)Math.ceil(int5 * 1.5);
                    SpriteRenderer.instance
                        .renderi(
                            null,
                            Core.getInstance().getScreenWidth() / 2 - int4 / 2,
                            Core.getInstance().getScreenHeight() / 2 - int6 / 2,
                            int4,
                            int6,
                            0.0F,
                            0.0F,
                            0.0F,
                            0.75F,
                            null
                        );
                    TextManager.instance
                        .DrawStringCentre(
                            Core.getInstance().getScreenWidth() / 2, Core.getInstance().getScreenHeight() / 2 - int5 / 2, string, 1.0, 1.0, 1.0, 1.0
                        );
                }

                if (!isbFadeBeforeUI()) {
                    setFadeAlpha(getFadeInTime() / getFadeInTimeMax());
                    if (getFadeAlpha() > 1.0) {
                        setFadeAlpha(1.0);
                    }

                    if (getFadeAlpha() < 0.0) {
                        setFadeAlpha(0.0);
                    }

                    if (isFadingOut()) {
                        setFadeAlpha(1.0 - getFadeAlpha());
                    }

                    if (IsoCamera.CamCharacter != null && getFadeAlpha() > 0.0) {
                        DrawTexture(getBlack(), 0.0, 0.0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), getFadeAlpha());
                    }
                }

                for (int int7 = 0; int7 < IsoPlayer.numPlayers; int7++) {
                    if (IsoPlayer.players[int7] != null && !playerFadeInfo[int7].isFadeBeforeUI()) {
                        playerFadeInfo[int7].render();
                    }
                }

                if (LuaManager.thread == defaultthread) {
                    LuaEventManager.triggerEvent("OnPostUIDraw");
                }

                if (useUIFBO) {
                    SpriteRenderer.instance.setDefaultStyle(TransparentStyle.instance);
                }
            }
        }
    }

    public static void resize() {
        if (useUIFBO && UIFBO != null) {
            CreateFBO(Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight());
        }

        if (getClock() != null) {
            setLastOffX(Core.getInstance().getScreenWidth());
            setLastOffY(Core.getInstance().getScreenHeight());

            for (int int0 = 0; int0 < 4; int0++) {
                int int1 = Core.getInstance().getScreenWidth();
                int int2 = Core.getInstance().getScreenHeight();
                byte byte0;
                if (!Clock.instance.isVisible()) {
                    byte0 = 24;
                } else {
                    byte0 = 64;
                }

                if (int0 == 0 && IsoPlayer.numPlayers > 1 || int0 == 2) {
                    int1 /= 2;
                }

                MoodleUI[int0].setX(int1 - 50);
                if ((int0 == 0 || int0 == 1) && IsoPlayer.numPlayers > 1) {
                    MoodleUI[int0].setY(byte0);
                }

                if (int0 == 2 || int0 == 3) {
                    MoodleUI[int0].setY(int2 / 2 + byte0);
                }

                MoodleUI[int0].setVisible(VisibleAllUI && IsoPlayer.players[int0] != null);
            }

            clock.resize();
            if (IsoPlayer.numPlayers == 1) {
                if (Core.getInstance().getOptionClockSize() == 2) {
                    clock.setX(Core.getInstance().getScreenWidth() - 166);
                } else {
                    clock.setX(Core.getInstance().getScreenWidth() - 91);
                }
            } else {
                if (Core.getInstance().getOptionClockSize() == 2) {
                    clock.setX(Core.getInstance().getScreenWidth() / 2.0F - 83.0F);
                } else {
                    clock.setX(Core.getInstance().getScreenWidth() / 2.0F - 45.5F);
                }

                clock.setY(Core.getInstance().getScreenHeight() - 70);
            }

            if (IsoPlayer.numPlayers == 1) {
                speedControls.setX(Core.getInstance().getScreenWidth() - 110);
            } else {
                speedControls.setX(Core.getInstance().getScreenWidth() / 2 - 50);
            }

            if (IsoPlayer.numPlayers == 1 && !clock.isVisible()) {
                speedControls.setY(clock.getY());
            } else {
                speedControls.setY(clock.getY() + clock.getHeight() + 6.0);
            }

            speedControls.setVisible(VisibleAllUI && !IsoPlayer.allPlayersDead());
        }
    }

    public static Vector2 getTileFromMouse(double mx, double my, double z) {
        PickedTile.x = IsoUtils.XToIso((float)(mx - 0.0), (float)(my - 0.0), (float)z);
        PickedTile.y = IsoUtils.YToIso((float)(mx - 0.0), (float)(my - 0.0), (float)z);
        PickedTileLocal.x = getPickedTile().x - (int)getPickedTile().x;
        PickedTileLocal.y = getPickedTile().y - (int)getPickedTile().y;
        PickedTile.x = (int)getPickedTile().x;
        PickedTile.y = (int)getPickedTile().y;
        return getPickedTile();
    }

    public static void update() {
        if (!bSuspend) {
            if (!toRemove.isEmpty()) {
                UI.removeAll(toRemove);
            }

            toRemove.clear();
            if (!toAdd.isEmpty()) {
                UI.addAll(toAdd);
            }

            toAdd.clear();
            setFadeInTime(getFadeInTime() - 1.0);

            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                playerFadeInfo[int0].update();
            }

            long long0 = System.currentTimeMillis();
            if (long0 - uiUpdateTimeMS >= 100L) {
                doTick = true;
                uiUpdateIntervalMS = Math.min(long0 - uiUpdateTimeMS, 1000L);
                uiUpdateTimeMS = long0;
            } else {
                doTick = false;
            }

            boolean boolean0 = false;
            boolean boolean1 = false;
            boolean boolean2 = false;
            int int1 = Mouse.getXA();
            int int2 = Mouse.getYA();
            int int3 = Mouse.getX();
            int int4 = Mouse.getY();
            tutorialStack.clear();

            for (int int5 = UI.size() - 1; int5 >= 0; int5--) {
                UIElement uIElement0 = UI.get(int5);
                if (uIElement0.getParent() != null) {
                    UI.remove(int5);
                    throw new IllegalStateException();
                }

                if (uIElement0.isFollowGameWorld()) {
                    tutorialStack.add(uIElement0);
                }

                if (uIElement0 instanceof ObjectTooltip) {
                    UIElement uIElement1 = UI.remove(int5);
                    UI.add(uIElement1);
                }
            }

            for (int int6 = 0; int6 < UI.size(); int6++) {
                UIElement uIElement2 = UI.get(int6);
                if (uIElement2.alwaysOnTop || toTop.contains(uIElement2)) {
                    UIElement uIElement3 = UI.remove(int6);
                    int6--;
                    toAdd.add(uIElement3);
                }
            }

            if (!toAdd.isEmpty()) {
                UI.addAll(toAdd);
                toAdd.clear();
            }

            toTop.clear();

            for (int int7 = 0; int7 < UI.size(); int7++) {
                UIElement uIElement4 = UI.get(int7);
                if (uIElement4.alwaysBack) {
                    UIElement uIElement5 = UI.remove(int7);
                    UI.add(0, uIElement5);
                }
            }

            for (int int8 = 0; int8 < tutorialStack.size(); int8++) {
                UI.remove(tutorialStack.get(int8));
                UI.add(0, tutorialStack.get(int8));
            }

            if (Mouse.isLeftPressed()) {
                Core.UnfocusActiveTextEntryBox();

                for (int int9 = UI.size() - 1; int9 >= 0; int9--) {
                    UIElement uIElement6 = UI.get(int9);
                    if ((getModal() == null || getModal() == uIElement6 || !getModal().isVisible())
                        && (uIElement6.isIgnoreLossControl() || !TutorialManager.instance.StealControl)
                        && uIElement6.isVisible()) {
                        if ((
                                !(int1 >= uIElement6.getX())
                                    || !(int2 >= uIElement6.getY())
                                    || !(int1 < uIElement6.getX() + uIElement6.getWidth())
                                    || !(int2 < uIElement6.getY() + uIElement6.getHeight())
                            )
                            && !uIElement6.isCapture()) {
                            uIElement6.onMouseDownOutside(int1 - uIElement6.getX().intValue(), int2 - uIElement6.getY().intValue());
                        } else if (uIElement6.onMouseDown(int1 - uIElement6.getX().intValue(), int2 - uIElement6.getY().intValue())) {
                            boolean0 = true;
                            break;
                        }
                    }
                }

                if (checkPicked() && !boolean0) {
                    LuaEventManager.triggerEvent("OnObjectLeftMouseButtonDown", Picked.tile, BoxedStaticValues.toDouble(int1), BoxedStaticValues.toDouble(int2));
                }

                if (!boolean0) {
                    LuaEventManager.triggerEvent("OnMouseDown", BoxedStaticValues.toDouble(int1), BoxedStaticValues.toDouble(int2));
                    CloseContainers();
                    if (IsoWorld.instance.CurrentCell != null
                        && !IsoWorld.instance.CurrentCell.DoBuilding(0, false)
                        && getPicked() != null
                        && !GameTime.isGamePaused()
                        && IsoPlayer.getInstance() != null
                        && !IsoPlayer.getInstance().isAiming()
                        && !IsoPlayer.getInstance().isAsleep()) {
                        getPicked().tile.onMouseLeftClick(getPicked().lx, getPicked().ly);
                    }
                } else {
                    Mouse.UIBlockButtonDown(0);
                }
            }

            if (Mouse.isLeftReleased()) {
                boolean boolean3 = false;

                for (int int10 = UI.size() - 1; int10 >= 0; int10--) {
                    UIElement uIElement7 = UI.get(int10);
                    if ((uIElement7.isIgnoreLossControl() || !TutorialManager.instance.StealControl)
                        && uIElement7.isVisible()
                        && (getModal() == null || getModal() == uIElement7 || !getModal().isVisible())) {
                        if ((
                                !(int1 >= uIElement7.getX())
                                    || !(int2 >= uIElement7.getY())
                                    || !(int1 < uIElement7.getX() + uIElement7.getWidth())
                                    || !(int2 < uIElement7.getY() + uIElement7.getHeight())
                            )
                            && !uIElement7.isCapture()) {
                            uIElement7.onMouseUpOutside(int1 - uIElement7.getX().intValue(), int2 - uIElement7.getY().intValue());
                        } else if (uIElement7.onMouseUp(int1 - uIElement7.getX().intValue(), int2 - uIElement7.getY().intValue())) {
                            boolean3 = true;
                            break;
                        }
                    }
                }

                if (!boolean3) {
                    LuaEventManager.triggerEvent("OnMouseUp", BoxedStaticValues.toDouble(int1), BoxedStaticValues.toDouble(int2));
                    if (checkPicked() && !boolean0) {
                        LuaEventManager.triggerEvent(
                            "OnObjectLeftMouseButtonUp", Picked.tile, BoxedStaticValues.toDouble(int1), BoxedStaticValues.toDouble(int2)
                        );
                    }
                }
            }

            if (Mouse.isRightPressed()) {
                for (int int11 = UI.size() - 1; int11 >= 0; int11--) {
                    UIElement uIElement8 = UI.get(int11);
                    if (uIElement8.isVisible() && (getModal() == null || getModal() == uIElement8 || !getModal().isVisible())) {
                        if ((
                                !(int1 >= uIElement8.getX())
                                    || !(int2 >= uIElement8.getY())
                                    || !(int1 < uIElement8.getX() + uIElement8.getWidth())
                                    || !(int2 < uIElement8.getY() + uIElement8.getHeight())
                            )
                            && !uIElement8.isCapture()) {
                            uIElement8.onRightMouseDownOutside(int1 - uIElement8.getX().intValue(), int2 - uIElement8.getY().intValue());
                        } else if (uIElement8.onRightMouseDown(int1 - uIElement8.getX().intValue(), int2 - uIElement8.getY().intValue())) {
                            boolean1 = true;
                            break;
                        }
                    }
                }

                if (!boolean1) {
                    LuaEventManager.triggerEvent("OnRightMouseDown", BoxedStaticValues.toDouble(int1), BoxedStaticValues.toDouble(int2));
                    if (checkPicked() && !boolean1) {
                        LuaEventManager.triggerEvent(
                            "OnObjectRightMouseButtonDown", Picked.tile, BoxedStaticValues.toDouble(int1), BoxedStaticValues.toDouble(int2)
                        );
                    }
                } else {
                    Mouse.UIBlockButtonDown(1);
                }

                if (IsoWorld.instance.CurrentCell != null
                    && getPicked() != null
                    && getSpeedControls() != null
                    && !IsoPlayer.getInstance().isAiming()
                    && !IsoPlayer.getInstance().isAsleep()
                    && !GameTime.isGamePaused()) {
                    getSpeedControls().SetCurrentGameSpeed(1);
                    getPicked().tile.onMouseRightClick(getPicked().lx, getPicked().ly);
                    setRightDownObject(getPicked().tile);
                }
            }

            if (Mouse.isRightReleased()) {
                boolean boolean4 = false;
                boolean boolean5 = false;

                for (int int12 = UI.size() - 1; int12 >= 0; int12--) {
                    UIElement uIElement9 = UI.get(int12);
                    if ((uIElement9.isIgnoreLossControl() || !TutorialManager.instance.StealControl)
                        && uIElement9.isVisible()
                        && (getModal() == null || getModal() == uIElement9 || !getModal().isVisible())) {
                        if ((
                                !(int1 >= uIElement9.getX())
                                    || !(int2 >= uIElement9.getY())
                                    || !(int1 < uIElement9.getX() + uIElement9.getWidth())
                                    || !(int2 < uIElement9.getY() + uIElement9.getHeight())
                            )
                            && !uIElement9.isCapture()) {
                            uIElement9.onRightMouseUpOutside(int1 - uIElement9.getX().intValue(), int2 - uIElement9.getY().intValue());
                        } else if (uIElement9.onRightMouseUp(int1 - uIElement9.getX().intValue(), int2 - uIElement9.getY().intValue())) {
                            boolean5 = true;
                            break;
                        }
                    }
                }

                if (!boolean5) {
                    LuaEventManager.triggerEvent("OnRightMouseUp", BoxedStaticValues.toDouble(int1), BoxedStaticValues.toDouble(int2));
                    if (checkPicked()) {
                        boolean boolean6 = true;
                        if (GameClient.bClient && Picked.tile.getSquare() != null) {
                            SafeHouse safeHouse = SafeHouse.isSafeHouse(Picked.tile.getSquare(), IsoPlayer.getInstance().getUsername(), true);
                            if (safeHouse != null) {
                                boolean6 = false;
                            }
                        }

                        if (boolean6) {
                            LuaEventManager.triggerEvent(
                                "OnObjectRightMouseButtonUp", Picked.tile, BoxedStaticValues.toDouble(int1), BoxedStaticValues.toDouble(int2)
                            );
                        }
                    }
                }

                if (IsoPlayer.getInstance() != null) {
                    IsoPlayer.getInstance().setDragObject(null);
                }

                if (IsoWorld.instance.CurrentCell != null
                    && getRightDownObject() != null
                    && IsoPlayer.getInstance() != null
                    && !IsoPlayer.getInstance().IsAiming()
                    && !IsoPlayer.getInstance().isAsleep()) {
                    getRightDownObject().onMouseRightReleased();
                    setRightDownObject(null);
                }
            }

            lastwheel = 0;
            wheel = Mouse.getWheelState();
            boolean boolean7 = false;
            if (wheel != lastwheel) {
                int int13 = wheel - lastwheel < 0 ? 1 : -1;

                for (int int14 = UI.size() - 1; int14 >= 0; int14--) {
                    UIElement uIElement10 = UI.get(int14);
                    if ((uIElement10.isIgnoreLossControl() || !TutorialManager.instance.StealControl)
                        && uIElement10.isVisible()
                        && (uIElement10.isPointOver(int1, int2) || uIElement10.isCapture())
                        && uIElement10.onMouseWheel(int13)) {
                        boolean7 = true;
                        break;
                    }
                }

                if (!boolean7) {
                    Core.getInstance().doZoomScroll(0, int13);
                }
            }

            if (getLastMouseX() != int1 || getLastMouseY() != int2) {
                for (int int15 = UI.size() - 1; int15 >= 0; int15--) {
                    UIElement uIElement11 = UI.get(int15);
                    if ((uIElement11.isIgnoreLossControl() || !TutorialManager.instance.StealControl) && uIElement11.isVisible()) {
                        if ((
                                !(int1 >= uIElement11.getX())
                                    || !(int2 >= uIElement11.getY())
                                    || !(int1 < uIElement11.getX() + uIElement11.getWidth())
                                    || !(int2 < uIElement11.getY() + uIElement11.getHeight())
                            )
                            && !uIElement11.isCapture()) {
                            uIElement11.onMouseMoveOutside(int1 - getLastMouseX(), int2 - getLastMouseY());
                        } else if (!boolean2 && uIElement11.onMouseMove(int1 - getLastMouseX(), int2 - getLastMouseY())) {
                            boolean2 = true;
                        }
                    }
                }
            }

            if (!boolean2 && IsoPlayer.players[0] != null) {
                setPicked(IsoObjectPicker.Instance.ContextPick(int1, int2));
                if (IsoCamera.CamCharacter != null) {
                    setPickedTile(getTileFromMouse(int3, int4, (int)IsoPlayer.players[0].getZ()));
                }

                LuaEventManager.triggerEvent(
                    "OnMouseMove",
                    BoxedStaticValues.toDouble(int1),
                    BoxedStaticValues.toDouble(int2),
                    BoxedStaticValues.toDouble(int3),
                    BoxedStaticValues.toDouble(int4)
                );
            } else {
                Mouse.UIBlockButtonDown(2);
            }

            setLastMouseX(int1);
            setLastMouseY(int2);

            for (int int16 = 0; int16 < UI.size(); int16++) {
                UI.get(int16).update();
            }

            updateTooltip(int1, int2);
            handleZoomKeys();
            IsoCamera.cameras[0].lastOffX = (int)IsoCamera.cameras[0].OffX;
            IsoCamera.cameras[0].lastOffY = (int)IsoCamera.cameras[0].OffY;
        }
    }

    private static boolean checkPicked() {
        return Picked != null && Picked.tile != null && Picked.tile.getObjectIndex() != -1;
    }

    private static void handleZoomKeys() {
        boolean boolean0 = true;
        if (Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.IsEditable && Core.CurrentTextEntryBox.DoingTextEntry) {
            boolean0 = false;
        }

        if (GameTime.isGamePaused()) {
            boolean0 = false;
        }

        if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Zoom in"))) {
            if (boolean0 && !KeyDownZoomIn) {
                Core.getInstance().doZoomScroll(0, -1);
            }

            KeyDownZoomIn = true;
        } else {
            KeyDownZoomIn = false;
        }

        if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Zoom out"))) {
            if (boolean0 && !KeyDownZoomOut) {
                Core.getInstance().doZoomScroll(0, 1);
            }

            KeyDownZoomOut = true;
        } else {
            KeyDownZoomOut = false;
        }
    }

    /**
     * @return the lastMouseX
     */
    public static Double getLastMouseX() {
        return BoxedStaticValues.toDouble(lastMouseX);
    }

    /**
     * 
     * @param aLastMouseX the lastMouseX to set
     */
    public static void setLastMouseX(double aLastMouseX) {
        lastMouseX = (int)aLastMouseX;
    }

    /**
     * @return the lastMouseY
     */
    public static Double getLastMouseY() {
        return BoxedStaticValues.toDouble(lastMouseY);
    }

    /**
     * 
     * @param aLastMouseY the lastMouseY to set
     */
    public static void setLastMouseY(double aLastMouseY) {
        lastMouseY = (int)aLastMouseY;
    }

    /**
     * @return the Picked
     */
    public static IsoObjectPicker.ClickObject getPicked() {
        return Picked;
    }

    /**
     * 
     * @param aPicked the Picked to set
     */
    public static void setPicked(IsoObjectPicker.ClickObject aPicked) {
        Picked = aPicked;
    }

    /**
     * @return the clock
     */
    public static Clock getClock() {
        return clock;
    }

    /**
     * 
     * @param aClock the clock to set
     */
    public static void setClock(Clock aClock) {
        clock = aClock;
    }

    /**
     * @return the UI
     */
    public static ArrayList<UIElement> getUI() {
        return UI;
    }

    /**
     * 
     * @param aUI the UI to set
     */
    public static void setUI(ArrayList<UIElement> aUI) {
        PZArrayUtil.copy(UI, aUI);
    }

    /**
     * @return the toolTip
     */
    public static ObjectTooltip getToolTip() {
        return toolTip;
    }

    /**
     * 
     * @param aToolTip the toolTip to set
     */
    public static void setToolTip(ObjectTooltip aToolTip) {
        toolTip = aToolTip;
    }

    /**
     * @return the mouseArrow
     */
    public static Texture getMouseArrow() {
        return mouseArrow;
    }

    /**
     * 
     * @param aMouseArrow the mouseArrow to set
     */
    public static void setMouseArrow(Texture aMouseArrow) {
        mouseArrow = aMouseArrow;
    }

    /**
     * @return the mouseExamine
     */
    public static Texture getMouseExamine() {
        return mouseExamine;
    }

    /**
     * 
     * @param aMouseExamine the mouseExamine to set
     */
    public static void setMouseExamine(Texture aMouseExamine) {
        mouseExamine = aMouseExamine;
    }

    /**
     * @return the mouseAttack
     */
    public static Texture getMouseAttack() {
        return mouseAttack;
    }

    /**
     * 
     * @param aMouseAttack the mouseAttack to set
     */
    public static void setMouseAttack(Texture aMouseAttack) {
        mouseAttack = aMouseAttack;
    }

    /**
     * @return the mouseGrab
     */
    public static Texture getMouseGrab() {
        return mouseGrab;
    }

    /**
     * 
     * @param aMouseGrab the mouseGrab to set
     */
    public static void setMouseGrab(Texture aMouseGrab) {
        mouseGrab = aMouseGrab;
    }

    /**
     * @return the speedControls
     */
    public static SpeedControls getSpeedControls() {
        return speedControls;
    }

    /**
     * 
     * @param aSpeedControls the speedControls to set
     */
    public static void setSpeedControls(SpeedControls aSpeedControls) {
        speedControls = aSpeedControls;
    }

    /**
     * @return the DebugConsole
     */
    public static UIDebugConsole getDebugConsole() {
        return DebugConsole;
    }

    /**
     * 
     * @param aDebugConsole the DebugConsole to set
     */
    public static void setDebugConsole(UIDebugConsole aDebugConsole) {
        DebugConsole = aDebugConsole;
    }

    /**
     * @return the ServerToolbox
     */
    public static UIServerToolbox getServerToolbox() {
        return ServerToolbox;
    }

    /**
     * 
     * @param aServerToolbox the ServerToolbox to set
     */
    public static void setServerToolbox(UIServerToolbox aServerToolbox) {
        ServerToolbox = aServerToolbox;
    }

    /**
     * @return the MoodleUI
     */
    public static MoodlesUI getMoodleUI(double index) {
        return MoodleUI[(int)index];
    }

    /**
     * 
     * @param index
     * @param aMoodleUI the MoodleUI to set
     */
    public static void setMoodleUI(double index, MoodlesUI aMoodleUI) {
        MoodleUI[(int)index] = aMoodleUI;
    }

    /**
     * @return the bFadeBeforeUI
     */
    public static boolean isbFadeBeforeUI() {
        return bFadeBeforeUI;
    }

    /**
     * 
     * @param abFadeBeforeUI the bFadeBeforeUI to set
     */
    public static void setbFadeBeforeUI(boolean abFadeBeforeUI) {
        bFadeBeforeUI = abFadeBeforeUI;
    }

    /**
     * @return the ProgressBar
     */
    public static ActionProgressBar getProgressBar(double index) {
        return ProgressBar[(int)index];
    }

    /**
     * 
     * @param index
     * @param aProgressBar the ProgressBar to set
     */
    public static void setProgressBar(double index, ActionProgressBar aProgressBar) {
        ProgressBar[(int)index] = aProgressBar;
    }

    /**
     * @return the FadeAlpha
     */
    public static Double getFadeAlpha() {
        return BoxedStaticValues.toDouble(FadeAlpha);
    }

    /**
     * 
     * @param aFadeAlpha the FadeAlpha to set
     */
    public static void setFadeAlpha(double aFadeAlpha) {
        FadeAlpha = (float)aFadeAlpha;
    }

    /**
     * @return the FadeInTimeMax
     */
    public static Double getFadeInTimeMax() {
        return BoxedStaticValues.toDouble(FadeInTimeMax);
    }

    /**
     * 
     * @param aFadeInTimeMax the FadeInTimeMax to set
     */
    public static void setFadeInTimeMax(double aFadeInTimeMax) {
        FadeInTimeMax = (int)aFadeInTimeMax;
    }

    /**
     * @return the FadeInTime
     */
    public static Double getFadeInTime() {
        return BoxedStaticValues.toDouble(FadeInTime);
    }

    /**
     * 
     * @param aFadeInTime the FadeInTime to set
     */
    public static void setFadeInTime(double aFadeInTime) {
        FadeInTime = Math.max((int)aFadeInTime, 0);
    }

    /**
     * @return the FadingOut
     */
    public static Boolean isFadingOut() {
        return FadingOut ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 
     * @param aFadingOut the FadingOut to set
     */
    public static void setFadingOut(boolean aFadingOut) {
        FadingOut = aFadingOut;
    }

    /**
     * @return the lastMouseTexture
     */
    public static Texture getLastMouseTexture() {
        return lastMouseTexture;
    }

    /**
     * 
     * @param aLastMouseTexture the lastMouseTexture to set
     */
    public static void setLastMouseTexture(Texture aLastMouseTexture) {
        lastMouseTexture = aLastMouseTexture;
    }

    /**
     * @return the LastPicked
     */
    public static IsoObject getLastPicked() {
        return LastPicked;
    }

    /**
     * 
     * @param aLastPicked the LastPicked to set
     */
    public static void setLastPicked(IsoObject aLastPicked) {
        LastPicked = aLastPicked;
    }

    /**
     * @return the DoneTutorials
     */
    public static ArrayList<String> getDoneTutorials() {
        return DoneTutorials;
    }

    /**
     * 
     * @param aDoneTutorials the DoneTutorials to set
     */
    public static void setDoneTutorials(ArrayList<String> aDoneTutorials) {
        PZArrayUtil.copy(DoneTutorials, aDoneTutorials);
    }

    /**
     * @return the lastOffX
     */
    public static float getLastOffX() {
        return lastOffX;
    }

    /**
     * 
     * @param aLastOffX the lastOffX to set
     */
    public static void setLastOffX(float aLastOffX) {
        lastOffX = aLastOffX;
    }

    /**
     * @return the lastOffY
     */
    public static float getLastOffY() {
        return lastOffY;
    }

    /**
     * 
     * @param aLastOffY the lastOffY to set
     */
    public static void setLastOffY(float aLastOffY) {
        lastOffY = aLastOffY;
    }

    /**
     * @return the Modal
     */
    public static ModalDialog getModal() {
        return Modal;
    }

    /**
     * 
     * @param aModal the Modal to set
     */
    public static void setModal(ModalDialog aModal) {
        Modal = aModal;
    }

    /**
     * @return the black
     */
    public static Texture getBlack() {
        return black;
    }

    /**
     * 
     * @param aBlack the black to set
     */
    public static void setBlack(Texture aBlack) {
        black = aBlack;
    }

    /**
     * @return the lastAlpha
     */
    public static float getLastAlpha() {
        return lastAlpha;
    }

    /**
     * 
     * @param aLastAlpha the lastAlpha to set
     */
    public static void setLastAlpha(float aLastAlpha) {
        lastAlpha = aLastAlpha;
    }

    /**
     * @return the PickedTileLocal
     */
    public static Vector2 getPickedTileLocal() {
        return PickedTileLocal;
    }

    /**
     * 
     * @param aPickedTileLocal the PickedTileLocal to set
     */
    public static void setPickedTileLocal(Vector2 aPickedTileLocal) {
        PickedTileLocal.set(aPickedTileLocal);
    }

    /**
     * @return the PickedTile
     */
    public static Vector2 getPickedTile() {
        return PickedTile;
    }

    /**
     * 
     * @param aPickedTile the PickedTile to set
     */
    public static void setPickedTile(Vector2 aPickedTile) {
        PickedTile.set(aPickedTile);
    }

    /**
     * @return the RightDownObject
     */
    public static IsoObject getRightDownObject() {
        return RightDownObject;
    }

    /**
     * 
     * @param aRightDownObject the RightDownObject to set
     */
    public static void setRightDownObject(IsoObject aRightDownObject) {
        RightDownObject = aRightDownObject;
    }

    static void pushToTop(UIElement uIElement) {
        toTop.add(uIElement);
    }

    public static boolean isShowPausedMessage() {
        return showPausedMessage;
    }

    public static void setShowPausedMessage(boolean _showPausedMessage) {
        showPausedMessage = _showPausedMessage;
    }

    public static void setShowLuaDebuggerOnError(boolean show) {
        bShowLuaDebuggerOnError = show;
    }

    public static boolean isShowLuaDebuggerOnError() {
        return bShowLuaDebuggerOnError;
    }

    public static void debugBreakpoint(String filename, long pc) {
        if (bShowLuaDebuggerOnError) {
            if (Core.CurrentTextEntryBox != null) {
                Core.CurrentTextEntryBox.DoingTextEntry = false;
                Core.CurrentTextEntryBox = null;
            }

            if (!GameServer.bServer) {
                if (!(GameWindow.states.current instanceof GameLoadingState)) {
                    previousThread = defaultthread;
                    defaultthread = LuaManager.debugthread;
                    int int0 = Core.getInstance().frameStage;
                    if (int0 != 0) {
                        if (int0 <= 1) {
                            Core.getInstance().EndFrame(0);
                        }

                        if (int0 <= 2) {
                            Core.getInstance().StartFrameUI();
                        }

                        if (int0 <= 3) {
                            Core.getInstance().EndFrameUI();
                        }
                    }

                    LuaManager.thread.bStep = false;
                    LuaManager.thread.bStepInto = false;
                    if (!toRemove.isEmpty()) {
                        UI.removeAll(toRemove);
                    }

                    toRemove.clear();
                    if (!toAdd.isEmpty()) {
                        UI.addAll(toAdd);
                    }

                    toAdd.clear();
                    ArrayList arrayList = new ArrayList();
                    boolean boolean0 = bSuspend;
                    arrayList.addAll(UI);
                    UI.clear();
                    bSuspend = false;
                    setShowPausedMessage(false);
                    boolean boolean1 = false;
                    boolean[] booleans = new boolean[11];

                    for (int int1 = 0; int1 < 11; int1++) {
                        booleans[int1] = true;
                    }

                    if (debugUI.size() == 0) {
                        LuaManager.debugcaller.pcall(LuaManager.debugthread, LuaManager.env.rawget("DoLuaDebugger"), filename, pc);
                    } else {
                        UI.addAll(debugUI);
                        LuaManager.debugcaller.pcall(LuaManager.debugthread, LuaManager.env.rawget("DoLuaDebuggerOnBreak"), filename, pc);
                    }

                    Mouse.setCursorVisible(true);
                    sync.begin();

                    while (!boolean1) {
                        if (RenderThread.isCloseRequested()) {
                            System.exit(0);
                        }

                        if (!GameWindow.bLuaDebuggerKeyDown && GameKeyboard.isKeyDown(Core.getInstance().getKey("Toggle Lua Debugger"))) {
                            GameWindow.bLuaDebuggerKeyDown = true;
                            executeGame(arrayList, boolean0, int0);
                            return;
                        }

                        String string = luaDebuggerAction;
                        luaDebuggerAction = null;
                        if ("StepInto".equalsIgnoreCase(string)) {
                            LuaManager.thread.bStep = true;
                            LuaManager.thread.bStepInto = true;
                            executeGame(arrayList, boolean0, int0);
                            return;
                        }

                        if ("StepOver".equalsIgnoreCase(string)) {
                            LuaManager.thread.bStep = true;
                            LuaManager.thread.bStepInto = false;
                            LuaManager.thread.lastCallFrame = LuaManager.thread.getCurrentCoroutine().getCallframeTop();
                            executeGame(arrayList, boolean0, int0);
                            return;
                        }

                        if ("Resume".equalsIgnoreCase(string)) {
                            executeGame(arrayList, boolean0, int0);
                            return;
                        }

                        sync.startFrame();

                        for (int int2 = 0; int2 < 11; int2++) {
                            boolean boolean2 = GameKeyboard.isKeyDown(59 + int2);
                            if (boolean2) {
                                if (!booleans[int2]) {
                                    if (int2 + 1 == 5) {
                                        LuaManager.thread.bStep = true;
                                        LuaManager.thread.bStepInto = true;
                                        executeGame(arrayList, boolean0, int0);
                                        return;
                                    }

                                    if (int2 + 1 == 6) {
                                        LuaManager.thread.bStep = true;
                                        LuaManager.thread.bStepInto = false;
                                        LuaManager.thread.lastCallFrame = LuaManager.thread.getCurrentCoroutine().getCallframeTop();
                                        executeGame(arrayList, boolean0, int0);
                                        return;
                                    }
                                }

                                booleans[int2] = true;
                            } else {
                                booleans[int2] = false;
                            }
                        }

                        Mouse.update();
                        GameKeyboard.update();
                        Core.getInstance().DoFrameReady();
                        update();
                        Core.getInstance().StartFrame(0, true);
                        Core.getInstance().EndFrame(0);
                        Core.getInstance().RenderOffScreenBuffer();
                        if (Core.getInstance().StartFrameUI()) {
                            render();
                        }

                        Core.getInstance().EndFrameUI();
                        resize();
                        if (!GameKeyboard.isKeyDown(Core.getInstance().getKey("Toggle Lua Debugger"))) {
                            GameWindow.bLuaDebuggerKeyDown = false;
                        }

                        sync.endFrame();
                        Core.getInstance().setScreenSize(RenderThread.getDisplayWidth(), RenderThread.getDisplayHeight());
                    }
                }
            }
        }
    }

    private static void executeGame(ArrayList<UIElement> arrayList, boolean boolean0, int int0) {
        debugUI.clear();
        debugUI.addAll(UI);
        UI.clear();
        UI.addAll(arrayList);
        bSuspend = boolean0;
        setShowPausedMessage(true);
        if (!LuaManager.thread.bStep && int0 != 0) {
            if (int0 == 1) {
                Core.getInstance().StartFrame(0, true);
            }

            if (int0 == 2) {
                Core.getInstance().StartFrame(0, true);
                Core.getInstance().EndFrame(0);
            }

            if (int0 == 3) {
                Core.getInstance().StartFrame(0, true);
                Core.getInstance().EndFrame(0);
                Core.getInstance().StartFrameUI();
            }
        }

        defaultthread = previousThread;
    }

    public static KahluaThread getDefaultThread() {
        if (defaultthread == null) {
            defaultthread = LuaManager.thread;
        }

        return defaultthread;
    }

    public static Double getDoubleClickInterval() {
        return BoxedStaticValues.toDouble(500.0);
    }

    public static Double getDoubleClickDist() {
        return BoxedStaticValues.toDouble(5.0);
    }

    public static Boolean isDoubleClick(double x1, double y1, double x2, double y2, double clickTime) {
        if (Math.abs(x2 - x1) > getDoubleClickDist()) {
            return false;
        } else if (Math.abs(y2 - y1) > getDoubleClickDist()) {
            return false;
        } else {
            return System.currentTimeMillis() - clickTime > getDoubleClickInterval() ? Boolean.FALSE : Boolean.TRUE;
        }
    }

    protected static void updateTooltip(double double1, double double0) {
        UIElement uIElement0 = null;

        for (int int0 = getUI().size() - 1; int0 >= 0; int0--) {
            UIElement uIElement1 = getUI().get(int0);
            if (uIElement1 != toolTip
                && uIElement1.isVisible()
                && double1 >= uIElement1.getX()
                && double0 >= uIElement1.getY()
                && double1 < uIElement1.getX() + uIElement1.getWidth()
                && double0 < uIElement1.getY() + uIElement1.getHeight()
                && (uIElement1.maxDrawHeight == -1 || double0 < uIElement1.getY() + uIElement1.maxDrawHeight)) {
                uIElement0 = uIElement1;
                break;
            }
        }

        IsoObject object = null;
        if (uIElement0 == null && getPicked() != null) {
            object = getPicked().tile;
            if (object != getLastPicked() && toolTip != null) {
                toolTip.targetAlpha = 0.0F;
                if (object.haveSpecialTooltip()) {
                    if (getToolTip().Object != object) {
                        getToolTip().show(object, (double)((int)double1 + 8), (double)((int)double0 + 16));
                        if (toolTip.isVisible()) {
                            toolTip.showDelay = 0;
                        }
                    } else {
                        toolTip.targetAlpha = 1.0F;
                    }
                }
            }
        }

        setLastPicked(object);
        if (toolTip != null && (object == null || toolTip.alpha <= 0.0F && toolTip.targetAlpha <= 0.0F)) {
            toolTip.hide();
        }
    }

    public static void setPlayerInventory(int playerIndex, UIElement inventory, UIElement loot) {
        if (playerIndex == 0) {
            playerInventoryUI = inventory;
            playerLootUI = loot;
        }
    }

    public static void setPlayerInventoryTooltip(int playerIndex, UIElement inventory, UIElement loot) {
        if (playerIndex == 0) {
            playerInventoryTooltip = inventory;
            playerLootTooltip = loot;
        }
    }

    public static boolean isMouseOverInventory() {
        if (playerInventoryTooltip != null && playerInventoryTooltip.isMouseOver()) {
            return true;
        } else if (playerLootTooltip != null && playerLootTooltip.isMouseOver()) {
            return true;
        } else if (playerInventoryUI != null && playerLootUI != null) {
            return playerInventoryUI.getMaxDrawHeight() == -1.0 && playerInventoryUI.isMouseOver()
                ? true
                : playerLootUI.getMaxDrawHeight() == -1.0 && playerLootUI.isMouseOver();
        } else {
            return false;
        }
    }

    public static void updateBeforeFadeOut() {
        if (!toRemove.isEmpty()) {
            UI.removeAll(toRemove);
            toRemove.clear();
        }

        if (!toAdd.isEmpty()) {
            UI.addAll(toAdd);
            toAdd.clear();
        }
    }

    public static void setVisibleAllUI(boolean visible) {
        VisibleAllUI = visible;
    }

    public static void setFadeBeforeUI(int playerIndex, boolean _bFadeBeforeUI) {
        playerFadeInfo[playerIndex].setFadeBeforeUI(_bFadeBeforeUI);
    }

    public static float getFadeAlpha(double playerIndex) {
        return playerFadeInfo[(int)playerIndex].getFadeAlpha();
    }

    public static void setFadeTime(double playerIndex, double FadeTime) {
        playerFadeInfo[(int)playerIndex].setFadeTime((int)FadeTime);
    }

    public static void FadeIn(double playerIndex, double seconds) {
        playerFadeInfo[(int)playerIndex].FadeIn((int)seconds);
    }

    public static void FadeOut(double playerIndex, double seconds) {
        playerFadeInfo[(int)playerIndex].FadeOut((int)seconds);
    }

    public static boolean isFBOActive() {
        return useUIFBO;
    }

    public static double getMillisSinceLastUpdate() {
        return uiUpdateIntervalMS;
    }

    public static double getSecondsSinceLastUpdate() {
        return uiUpdateIntervalMS / 1000.0;
    }

    public static double getMillisSinceLastRender() {
        return uiRenderIntervalMS;
    }

    public static double getSecondsSinceLastRender() {
        return uiRenderIntervalMS / 1000.0;
    }

    public static boolean onKeyPress(int key) {
        for (int int0 = UI.size() - 1; int0 >= 0; int0--) {
            UIElement uIElement = UI.get(int0);
            if (uIElement.isVisible() && uIElement.isWantKeyEvents()) {
                uIElement.onKeyPress(key);
                if (uIElement.isKeyConsumed(key)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean onKeyRepeat(int key) {
        for (int int0 = UI.size() - 1; int0 >= 0; int0--) {
            UIElement uIElement = UI.get(int0);
            if (uIElement.isVisible() && uIElement.isWantKeyEvents()) {
                uIElement.onKeyRepeat(key);
                if (uIElement.isKeyConsumed(key)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean onKeyRelease(int key) {
        for (int int0 = UI.size() - 1; int0 >= 0; int0--) {
            UIElement uIElement = UI.get(int0);
            if (uIElement.isVisible() && uIElement.isWantKeyEvents()) {
                uIElement.onKeyRelease(key);
                if (uIElement.isKeyConsumed(key)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isForceCursorVisible() {
        for (int int0 = UI.size() - 1; int0 >= 0; int0--) {
            UIElement uIElement = UI.get(int0);
            if (uIElement.isVisible() && (uIElement.isForceCursorVisible() || uIElement.isMouseOver())) {
                return true;
            }
        }

        return false;
    }

    static {
        for (int int0 = 0; int0 < 4; int0++) {
            playerFadeInfo[int0] = new UIManager.FadeInfo(int0);
        }
    }

    private static class FadeInfo {
        public int playerIndex;
        public boolean bFadeBeforeUI = false;
        public float FadeAlpha = 0.0F;
        public int FadeTime = 2;
        public int FadeTimeMax = 2;
        public boolean FadingOut = false;

        public FadeInfo(int arg0) {
            this.playerIndex = arg0;
        }

        public boolean isFadeBeforeUI() {
            return this.bFadeBeforeUI;
        }

        public void setFadeBeforeUI(boolean arg0) {
            this.bFadeBeforeUI = arg0;
        }

        public float getFadeAlpha() {
            return this.FadeAlpha;
        }

        public void setFadeAlpha(float arg0) {
            this.FadeAlpha = arg0;
        }

        public int getFadeTime() {
            return this.FadeTime;
        }

        public void setFadeTime(int arg0) {
            this.FadeTime = arg0;
        }

        public int getFadeTimeMax() {
            return this.FadeTimeMax;
        }

        public void setFadeTimeMax(int arg0) {
            this.FadeTimeMax = arg0;
        }

        public boolean isFadingOut() {
            return this.FadingOut;
        }

        public void setFadingOut(boolean arg0) {
            this.FadingOut = arg0;
        }

        public void FadeIn(int arg0) {
            this.setFadeTimeMax((int)(arg0 * 30 * (PerformanceSettings.getLockFPS() / 30.0F)));
            this.setFadeTime(this.getFadeTimeMax());
            this.setFadingOut(false);
        }

        public void FadeOut(int arg0) {
            this.setFadeTimeMax((int)(arg0 * 30 * (PerformanceSettings.getLockFPS() / 30.0F)));
            this.setFadeTime(this.getFadeTimeMax());
            this.setFadingOut(true);
        }

        public void update() {
            this.setFadeTime(this.getFadeTime() - 1);
        }

        public void render() {
            this.setFadeAlpha((float)this.getFadeTime() / this.getFadeTimeMax());
            if (this.getFadeAlpha() > 1.0F) {
                this.setFadeAlpha(1.0F);
            }

            if (this.getFadeAlpha() < 0.0F) {
                this.setFadeAlpha(0.0F);
            }

            if (this.isFadingOut()) {
                this.setFadeAlpha(1.0F - this.getFadeAlpha());
            }

            if (!(this.getFadeAlpha() <= 0.0F)) {
                int int0 = IsoCamera.getScreenLeft(this.playerIndex);
                int int1 = IsoCamera.getScreenTop(this.playerIndex);
                int int2 = IsoCamera.getScreenWidth(this.playerIndex);
                int int3 = IsoCamera.getScreenHeight(this.playerIndex);
                UIManager.DrawTexture(UIManager.getBlack(), int0, int1, int2, int3, this.getFadeAlpha());
            }
        }
    }

    static class Sync {
        private int fps = 30;
        private long period = 1000000000L / this.fps;
        private long excess;
        private long beforeTime = System.nanoTime();
        private long overSleepTime = 0L;

        void begin() {
            this.beforeTime = System.nanoTime();
            this.overSleepTime = 0L;
        }

        void startFrame() {
            this.excess = 0L;
        }

        void endFrame() {
            long long0 = System.nanoTime();
            long long1 = long0 - this.beforeTime;
            long long2 = this.period - long1 - this.overSleepTime;
            if (long2 > 0L) {
                try {
                    Thread.sleep(long2 / 1000000L);
                } catch (InterruptedException interruptedException) {
                }

                this.overSleepTime = System.nanoTime() - long0 - long2;
            } else {
                this.excess -= long2;
                this.overSleepTime = 0L;
            }

            this.beforeTime = System.nanoTime();
        }
    }
}
