// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import java.util.HashMap;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;

public final class ZombieGetDownState extends State {
    private static final ZombieGetDownState _instance = new ZombieGetDownState();
    static final Integer PARAM_PREV_STATE = 1;
    static final Integer PARAM_WAIT_TIME = 2;
    static final Integer PARAM_START_X = 3;
    static final Integer PARAM_START_Y = 4;

    public static ZombieGetDownState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        HashMap hashMap = owner.getStateMachineParams(this);
        hashMap.put(PARAM_PREV_STATE, owner.getStateMachine().getPrevious());
        hashMap.put(PARAM_START_X, owner.getX());
        hashMap.put(PARAM_START_Y, owner.getY());
        owner.setStateEventDelayTimer((Float)hashMap.get(PARAM_WAIT_TIME));
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        HashMap hashMap = owner.getStateMachineParams(this);
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        HashMap hashMap = owner.getStateMachineParams(this);
        IsoZombie zombie0 = (IsoZombie)owner;
        zombie0.setStateEventDelayTimer(0.0F);
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
        IsoZombie zombie0 = (IsoZombie)owner;
        if (event.m_EventName.equalsIgnoreCase("StartCrawling") && !zombie0.isCrawling()) {
            zombie0.toggleCrawling();
        }
    }

    public boolean isNearStartXY(IsoGameCharacter owner) {
        HashMap hashMap = owner.getStateMachineParams(this);
        Float float0 = (Float)hashMap.get(PARAM_START_X);
        Float float1 = (Float)hashMap.get(PARAM_START_Y);
        return float0 != null && float1 != null ? owner.DistToSquared(float0, float1) <= 0.25F : false;
    }

    public void setParams(IsoGameCharacter owner) {
        HashMap hashMap = owner.getStateMachineParams(this);
        hashMap.put(PARAM_WAIT_TIME, Rand.Next(60.0F, 150.0F));
    }
}
