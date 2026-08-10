// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.permission;

import zombie.characters.IsoGameCharacter;

public class DefaultStatePermissions implements IStatePermissions {
    public static final DefaultStatePermissions Instance = new DefaultStatePermissions();

    @Override
    public boolean isDeferredMovementAllowed(IsoGameCharacter var1) {
        return true;
    }

    @Override
    public boolean isPlayerInputAllowed(IsoGameCharacter var1) {
        return true;
    }
}
