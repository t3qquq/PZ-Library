// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;

public final class ZombieFallDownState extends State {
    private static final ZombieFallDownState _instance = new ZombieFallDownState();

    public static ZombieFallDownState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        owner.blockTurning = true;
        owner.setHitReaction("");
    }

    @Override
    public void execute(IsoGameCharacter owner) {
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        owner.blockTurning = false;
        owner.setOnFloor(true);
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
        if (event.m_EventName.equalsIgnoreCase("FallOnFront")) {
            owner.setFallOnFront(Boolean.parseBoolean(event.m_ParameterValue));
        }

        if (event.m_EventName.equalsIgnoreCase("PlayDeathSound")) {
            owner.setDoDeathSound(false);
            owner.playDeadSound();
        }
    }
}
