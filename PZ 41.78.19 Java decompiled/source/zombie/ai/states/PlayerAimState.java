// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.inventory.InventoryItem;

public final class PlayerAimState extends State {
    private static final PlayerAimState _instance = new PlayerAimState();

    public static PlayerAimState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        if (!"strafe".equals(owner.getPreviousActionContextStateName())) {
            InventoryItem item = owner.getPrimaryHandItem();
            if (item != null && item.getBringToBearSound() != null) {
                owner.getEmitter().playSoundImpl(item.getBringToBearSound(), null);
            }
        }
    }

    @Override
    public void execute(IsoGameCharacter owner) {
    }

    @Override
    public void exit(IsoGameCharacter owner) {
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
    }
}
