// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;

public final class StaggerBackState extends State {
    private static final StaggerBackState _instance = new StaggerBackState();

    public static StaggerBackState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        owner.setStateEventDelayTimer(this.getMaxStaggerTime(owner));
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        if (owner.hasAnimationPlayer()) {
            owner.getAnimationPlayer().setTargetToAngle();
        }

        owner.getVectorFromDirection(owner.getForwardDirection());
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        if (owner.isZombie()) {
            ((IsoZombie)owner).setStaggerBack(false);
        }

        owner.setShootable(true);
    }

    private float getMaxStaggerTime(IsoGameCharacter character) {
        float float0 = 35.0F * character.getHitForce() * character.getStaggerTimeMod();
        if (float0 < 20.0F) {
            return 20.0F;
        } else {
            return float0 > 30.0F ? 30.0F : float0;
        }
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
        if (event.m_EventName.equalsIgnoreCase("FallOnFront")) {
            owner.setFallOnFront(Boolean.parseBoolean(event.m_ParameterValue));
        }

        if (event.m_EventName.equalsIgnoreCase("SetState")) {
            IsoZombie zombie0 = (IsoZombie)owner;
            zombie0.parameterZombieState.setState(ParameterZombieState.State.Pushed);
        }
    }
}
