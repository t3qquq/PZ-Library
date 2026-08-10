// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.znet;

import zombie.Lua.LuaEventManager;

public class CallbackManager implements IJoinRequestCallback {
    public CallbackManager() {
        SteamUtils.addJoinRequestCallback(this);
    }

    @Override
    public void onJoinRequest(long var1, String string) {
        LuaEventManager.triggerEvent("OnAcceptInvite", string);
    }
}
