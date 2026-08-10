// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.permission;

import zombie.characters.IsoGameCharacter;

public class GenericStatePermissions implements IStatePermissions {
    private boolean m_deferredMovement = false;
    private boolean m_playerInput = false;

    public void setDeferredMovementAllowed(boolean boolean0) {
        this.m_deferredMovement = boolean0;
    }

    @Override
    public boolean isDeferredMovementAllowed(IsoGameCharacter var1) {
        return this.m_deferredMovement;
    }

    public void setPlayerInputAllowed(boolean boolean0) {
        this.m_playerInput = boolean0;
    }

    @Override
    public boolean isPlayerInputAllowed(IsoGameCharacter var1) {
        return this.m_playerInput;
    }
}
