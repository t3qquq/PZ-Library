// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.gameStates;

import org.lwjglx.input.Keyboard;
import zombie.GameWindow;
import zombie.SoundManager;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.modding.ActiveMods;
import zombie.network.GameClient;
import zombie.ui.LoadingQueueUI;
import zombie.ui.UIManager;

public class LoadingQueueState extends GameState {
    private static boolean bCancel = false;
    private static boolean bDone = false;
    private static int placeInQueue = -1;
    private boolean bAButtonDown = false;
    private static final LoadingQueueUI ui = new LoadingQueueUI();

    @Override
    public void enter() {
        bCancel = false;
        bDone = false;
        placeInQueue = -1;
        this.bAButtonDown = GameWindow.ActivatedJoyPad != null && GameWindow.ActivatedJoyPad.isAPressed();
        SoundManager.instance.setMusicState("Loading");
        if (GameClient.bClient) {
            GameClient.instance.sendLoginQueueRequest2();
        }
    }

    @Override
    public GameState redirectState() {
        return (GameState)(bCancel ? new MainScreenState() : new GameLoadingState());
    }

    @Override
    public void render() {
        Core.getInstance().StartFrame();
        Core.getInstance().EndFrame();
        boolean boolean0 = UIManager.useUIFBO;
        UIManager.useUIFBO = false;
        Core.getInstance().StartFrameUI();
        SpriteRenderer.instance.renderi(null, 0, 0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0.0F, 0.0F, 0.0F, 1.0F, null);
        if (placeInQueue >= 0) {
            MainScreenState.instance.renderBackground();
            UIManager.render();
            ActiveMods.renderUI();
            ui.render();
        }

        Core.getInstance().EndFrameUI();
        UIManager.useUIFBO = boolean0;
    }

    @Override
    public GameStateMachine.StateAction update() {
        if (!GameClient.bClient) {
            return GameStateMachine.StateAction.Continue;
        } else {
            boolean boolean0 = GameWindow.ActivatedJoyPad != null && GameWindow.ActivatedJoyPad.isAPressed();
            if (boolean0) {
                if (this.bAButtonDown) {
                    boolean0 = false;
                }
            } else {
                this.bAButtonDown = false;
            }

            if (boolean0 || Keyboard.isKeyDown(1) || !GameClient.instance.bConnected) {
                bCancel = true;
                SoundManager.instance.setMusicState("MainMenu");
                if (GameClient.connection != null) {
                    GameClient.instance.bConnected = false;
                    GameClient.bClient = false;
                    GameClient.connection.forceDisconnect("loading-queue-canceled");
                    GameClient.connection = null;
                }

                return GameStateMachine.StateAction.Continue;
            } else {
                return bDone ? GameStateMachine.StateAction.Continue : GameStateMachine.StateAction.Remain;
            }
        }
    }

    public static void onConnectionImmediate() {
        bDone = true;
    }

    public static void onPlaceInQueue(int place) {
        placeInQueue = place;
        ui.setPlaceInQueue(place);
    }
}
