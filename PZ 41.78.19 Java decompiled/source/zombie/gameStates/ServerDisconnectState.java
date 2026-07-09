// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.gameStates;

import zombie.GameWindow;
import zombie.IndieGL;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatElement;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.input.GameKeyboard;
import zombie.iso.IsoCamera;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSprite;
import zombie.ui.TextDrawObject;
import zombie.ui.TextManager;
import zombie.ui.TutorialManager;
import zombie.ui.UIFont;
import zombie.ui.UIManager;

public final class ServerDisconnectState extends GameState {
    private boolean keyDown = false;
    private int gridX = -1;
    private int gridY = -1;

    @Override
    public void enter() {
        TutorialManager.instance.StealControl = false;
        UIManager.UI.clear();
        LuaEventManager.ResetCallbacks();
        LuaManager.call("ISServerDisconnectUI_OnServerDisconnectUI", GameWindow.kickReason);
    }

    @Override
    public void exit() {
        GameWindow.kickReason = null;
    }

    @Override
    public void render() {
        boolean boolean0 = true;

        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            if (IsoPlayer.players[int0] == null) {
                if (int0 == 0) {
                    SpriteRenderer.instance.prePopulating();
                }
            } else {
                IsoPlayer.setInstance(IsoPlayer.players[int0]);
                IsoCamera.CamCharacter = IsoPlayer.players[int0];
                Core.getInstance().StartFrame(int0, boolean0);
                IsoCamera.frameState.set(int0);
                boolean0 = false;
                IsoSprite.globalOffsetX = -1.0F;
                IsoWorld.instance.render();
                Core.getInstance().EndFrame(int0);
            }
        }

        Core.getInstance().RenderOffScreenBuffer();

        for (int int1 = 0; int1 < IsoPlayer.numPlayers; int1++) {
            if (IsoPlayer.players[int1] != null) {
                Core.getInstance().StartFrameText(int1);
                IndieGL.disableAlphaTest();
                IndieGL.glDisable(2929);
                TextDrawObject.RenderBatch(int1);
                ChatElement.RenderBatch(int1);

                try {
                    Core.getInstance().EndFrameText(int1);
                } catch (Exception exception) {
                }
            }
        }

        if (Core.getInstance().StartFrameUI()) {
            UIManager.render();
            String string = GameWindow.kickReason;
            if (string == null || string.isEmpty()) {
                string = "Connection to server lost";
            }

            TextManager.instance
                .DrawStringCentre(UIFont.Medium, Core.getInstance().getScreenWidth() / 2, Core.getInstance().getScreenHeight() / 2, string, 1.0, 1.0, 1.0, 1.0);
        }

        Core.getInstance().EndFrameUI();
    }

    @Override
    public GameStateMachine.StateAction update() {
        if (!Core.bExiting && !GameKeyboard.isKeyDown(1)) {
            UIManager.update();
            return GameStateMachine.StateAction.Remain;
        } else {
            return GameStateMachine.StateAction.Continue;
        }
    }
}
