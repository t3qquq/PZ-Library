// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import java.util.HashMap;
import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.network.GameClient;
import zombie.util.StringUtils;

public final class ZombieGetUpState extends State {
    private static final ZombieGetUpState _instance = new ZombieGetUpState();
    static final Integer PARAM_STANDING = 1;
    static final Integer PARAM_PREV_STATE = 2;

    public static ZombieGetUpState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        IsoZombie zombie0 = (IsoZombie)owner;
        HashMap hashMap = owner.getStateMachineParams(this);
        hashMap.put(PARAM_STANDING, Boolean.FALSE);
        State state = owner.getStateMachine().getPrevious();
        if (state == ZombieGetUpFromCrawlState.instance()) {
            state = (State)owner.getStateMachineParams(ZombieGetUpFromCrawlState.instance()).get(1);
        }

        hashMap.put(PARAM_PREV_STATE, state);
        zombie0.parameterZombieState.setState(ParameterZombieState.State.GettingUp);
        if (GameClient.bClient) {
            owner.setKnockedDown(false);
        }
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        HashMap hashMap = owner.getStateMachineParams(this);
        boolean boolean0 = hashMap.get(PARAM_STANDING) == Boolean.TRUE;
        owner.setOnFloor(!boolean0);
        ((IsoZombie)owner).setKnockedDown(!boolean0);
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        HashMap hashMap = owner.getStateMachineParams(this);
        IsoZombie zombie0 = (IsoZombie)owner;
        owner.setCollidable(true);
        owner.clearVariable("SprinterTripped");
        owner.clearVariable("ShouldStandUp");
        if (StringUtils.isNullOrEmpty(owner.getHitReaction())) {
            zombie0.setSitAgainstWall(false);
        }

        zombie0.setKnockedDown(false);
        zombie0.AllowRepathDelay = 0.0F;
        if (hashMap.get(PARAM_PREV_STATE) == PathFindState.instance()) {
            if (owner.getPathFindBehavior2().getTargetChar() == null) {
                owner.setVariable("bPathfind", true);
                owner.setVariable("bMoving", false);
            } else if (zombie0.isTargetLocationKnown()) {
                owner.pathToCharacter(owner.getPathFindBehavior2().getTargetChar());
            } else if (zombie0.LastTargetSeenX != -1) {
                owner.pathToLocation(zombie0.LastTargetSeenX, zombie0.LastTargetSeenY, zombie0.LastTargetSeenZ);
            }
        } else if (hashMap.get(PARAM_PREV_STATE) == WalkTowardState.instance()) {
            owner.setVariable("bPathFind", false);
            owner.setVariable("bMoving", true);
        }
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
        HashMap hashMap = owner.getStateMachineParams(this);
        IsoZombie zombie0 = (IsoZombie)owner;
        if (event.m_EventName.equalsIgnoreCase("IsAlmostUp")) {
            hashMap.put(PARAM_STANDING, Boolean.TRUE);
        }
    }
}
