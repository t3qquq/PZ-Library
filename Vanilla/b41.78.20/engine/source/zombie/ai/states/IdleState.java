// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.util.StringUtils;

public final class IdleState extends State {
    private static final IdleState _instance = new IdleState();

    public static IdleState instance() {
        return _instance;
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
        if (event.m_EventName.equalsIgnoreCase("PlaySound") && !StringUtils.isNullOrEmpty(event.m_ParameterValue)) {
            owner.getSquare().playSound(event.m_ParameterValue);
        }
    }
}
