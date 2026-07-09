// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.gameStates;

import zombie.Lua.LuaEventManager;
import zombie.core.Core;
import zombie.ui.UIManager;

public class TermsOfServiceState extends GameState {
    private boolean bExit = false;
    private boolean bCreated = false;

    @Override
    public void enter() {
        LuaEventManager.triggerEvent("OnGameStateEnter", this);
        if (!this.bCreated) {
            this.bExit = true;
        }
    }

    @Override
    public void exit() {
        UIManager.clearArrays();
    }

    @Override
    public GameStateMachine.StateAction update() {
        return this.bExit ? GameStateMachine.StateAction.Continue : GameStateMachine.StateAction.Remain;
    }

    @Override
    public void render() {
        Core.getInstance().StartFrame();
        Core.getInstance().EndFrame();
        if (Core.getInstance().StartFrameUI()) {
            UIManager.render();
        }

        Core.getInstance().EndFrameUI();
    }

    public Object fromLua0(String arg0) {
        switch (arg0) {
            case "created":
                this.bCreated = true;
                return null;
            case "exit":
                this.bExit = true;
                return null;
            default:
                throw new IllegalArgumentException("unhandled \"" + arg0 + "\"");
        }
    }
}
