// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import java.util.HashMap;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;

public final class ZombieGetUpFromCrawlState extends State {
    private static final ZombieGetUpFromCrawlState _instance = new ZombieGetUpFromCrawlState();

    public static ZombieGetUpFromCrawlState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter character) {
        HashMap hashMap = character.getStateMachineParams(this);
        IsoZombie zombie0 = (IsoZombie)character;
        hashMap.put(1, character.getStateMachine().getPrevious());
        if (zombie0.isCrawling()) {
            zombie0.toggleCrawling();
            zombie0.setOnFloor(true);
        }
    }

    @Override
    public void execute(IsoGameCharacter var1) {
    }

    @Override
    public void exit(IsoGameCharacter character) {
        HashMap hashMap = character.getStateMachineParams(this);
        IsoZombie zombie0 = (IsoZombie)character;
        zombie0.AllowRepathDelay = 0.0F;
        if (hashMap.get(1) == PathFindState.instance()) {
            if (character.getPathFindBehavior2().getTargetChar() == null) {
                character.setVariable("bPathfind", true);
                character.setVariable("bMoving", false);
            } else if (zombie0.isTargetLocationKnown()) {
                character.pathToCharacter(character.getPathFindBehavior2().getTargetChar());
            } else if (zombie0.LastTargetSeenX != -1) {
                character.pathToLocation(zombie0.LastTargetSeenX, zombie0.LastTargetSeenY, zombie0.LastTargetSeenZ);
            }
        }
    }

    @Override
    public void animEvent(IsoGameCharacter var1, AnimEvent var2) {
    }
}
