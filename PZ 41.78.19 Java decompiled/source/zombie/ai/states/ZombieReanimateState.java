// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;

public final class ZombieReanimateState extends State {
    private static final ZombieReanimateState _instance = new ZombieReanimateState();

    public static ZombieReanimateState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        IsoZombie zombie0 = (IsoZombie)owner;
        zombie0.clearVariable("ReanimateAnim");
        zombie0.parameterZombieState.setState(ParameterZombieState.State.Idle);
    }

    @Override
    public void execute(IsoGameCharacter owner) {
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        IsoZombie zombie0 = (IsoZombie)owner;
        zombie0.clearVariable("ReanimateAnim");
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
        IsoZombie zombie0 = (IsoZombie)owner;
        if (event.m_EventName.equalsIgnoreCase("FallOnFront")) {
            owner.setFallOnFront(Boolean.parseBoolean(event.m_ParameterValue));
        }

        if (event.m_EventName.equalsIgnoreCase("ReanimateAnimFinishing")) {
            zombie0.setReanimate(false);
            zombie0.setFallOnFront(true);
        }

        if (event.m_EventName.equalsIgnoreCase("FallOnFront")) {
            owner.setFallOnFront(Boolean.parseBoolean(event.m_ParameterValue));
        }
    }
}
