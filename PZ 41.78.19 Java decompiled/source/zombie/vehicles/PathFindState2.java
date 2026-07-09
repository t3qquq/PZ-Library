// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import java.util.HashMap;
import zombie.ai.State;
import zombie.ai.astar.AStarPathFinder;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.gameStates.IngameState;
import zombie.iso.IsoChunk;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import zombie.network.ServerMap;

public final class PathFindState2 extends State {
    private static final Integer PARAM_TICK_COUNT = 0;

    @Override
    public void enter(IsoGameCharacter character) {
        HashMap hashMap = character.getStateMachineParams(this);
        character.setVariable("bPathfind", true);
        character.setVariable("bMoving", false);
        ((IsoZombie)character).networkAI.extraUpdate();
        hashMap.put(PARAM_TICK_COUNT, IngameState.instance.numberTicks);
    }

    @Override
    public void execute(IsoGameCharacter character) {
        HashMap hashMap = character.getStateMachineParams(this);
        PathFindBehavior2.BehaviorResult behaviorResult = character.getPathFindBehavior2().update();
        if (behaviorResult == PathFindBehavior2.BehaviorResult.Failed) {
            character.setPathFindIndex(-1);
            character.setVariable("bPathfind", false);
            character.setVariable("bMoving", false);
        } else if (behaviorResult == PathFindBehavior2.BehaviorResult.Succeeded) {
            int int0 = (int)character.getPathFindBehavior2().getTargetX();
            int int1 = (int)character.getPathFindBehavior2().getTargetY();
            IsoChunk chunk = GameServer.bServer
                ? ServerMap.instance.getChunk(int0 / 10, int1 / 10)
                : IsoWorld.instance.CurrentCell.getChunkForGridSquare(int0, int1, 0);
            if (chunk == null) {
                character.setVariable("bPathfind", false);
                character.setVariable("bMoving", true);
            } else {
                character.setVariable("bPathfind", false);
                character.setVariable("bMoving", false);
                character.setPath2(null);
            }
        } else {
            if (character instanceof IsoZombie) {
                long long0 = (Long)hashMap.get(PARAM_TICK_COUNT);
                if (IngameState.instance.numberTicks - long0 == 2L) {
                    ((IsoZombie)character).parameterZombieState.setState(ParameterZombieState.State.Idle);
                }
            }
        }
    }

    @Override
    public void exit(IsoGameCharacter character) {
        if (character instanceof IsoZombie) {
            ((IsoZombie)character).networkAI.extraUpdate();
            ((IsoZombie)character).AllowRepathDelay = 0.0F;
        }

        character.setVariable("bPathfind", false);
        character.setVariable("bMoving", false);
        character.setVariable("ShouldBeCrawling", false);
        PolygonalMap2.instance.cancelRequest(character);
        character.getFinder().progress = AStarPathFinder.PathFindProgress.notrunning;
        character.setPath2(null);
    }

    @Override
    public void animEvent(IsoGameCharacter var1, AnimEvent var2) {
    }

    @Override
    public boolean isMoving(IsoGameCharacter character) {
        return character.isMoving();
    }
}
