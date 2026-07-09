// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import java.util.HashMap;
import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.gameStates.IngameState;
import zombie.iso.objects.RainManager;

public final class ZombieIdleState extends State {
    private static final ZombieIdleState _instance = new ZombieIdleState();
    private static final Integer PARAM_TICK_COUNT = 0;

    public static ZombieIdleState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        HashMap hashMap = owner.getStateMachineParams(this);
        ((IsoZombie)owner).soundSourceTarget = null;
        ((IsoZombie)owner).soundAttract = 0.0F;
        ((IsoZombie)owner).movex = 0.0F;
        ((IsoZombie)owner).movey = 0.0F;
        owner.setStateEventDelayTimer(this.pickRandomWanderInterval());
        if (IngameState.instance == null) {
            hashMap.put(PARAM_TICK_COUNT, 0L);
        } else {
            hashMap.put(PARAM_TICK_COUNT, IngameState.instance.numberTicks);
        }
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        IsoZombie zombie0 = (IsoZombie)owner;
        HashMap hashMap = owner.getStateMachineParams(this);
        zombie0.movex = 0.0F;
        zombie0.movey = 0.0F;
        if (Core.bLastStand) {
            IsoPlayer player = null;
            float float0 = 1000000.0F;

            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                if (IsoPlayer.players[int0] != null && IsoPlayer.players[int0].DistTo(owner) < float0 && !IsoPlayer.players[int0].isDead()) {
                    float0 = IsoPlayer.players[int0].DistTo(owner);
                    player = IsoPlayer.players[int0];
                }
            }

            if (player != null) {
                zombie0.pathToCharacter(player);
            }
        } else {
            if (((IsoZombie)owner).bCrawling) {
                owner.setOnFloor(true);
            } else {
                owner.setOnFloor(false);
            }

            long long0 = (Long)hashMap.get(PARAM_TICK_COUNT);
            if (IngameState.instance.numberTicks - long0 == 2L) {
                ((IsoZombie)owner).parameterZombieState.setState(ParameterZombieState.State.Idle);
            }

            if (!zombie0.bIndoorZombie) {
                if (!zombie0.isUseless()) {
                    if (zombie0.getStateEventDelayTimer() <= 0.0F) {
                        owner.setStateEventDelayTimer(this.pickRandomWanderInterval());
                        int int1 = (int)owner.getX() + (Rand.Next(8) - 4);
                        int int2 = (int)owner.getY() + (Rand.Next(8) - 4);
                        if (owner.getCell().getGridSquare((double)int1, (double)int2, (double)owner.getZ()) != null
                            && owner.getCell().getGridSquare((double)int1, (double)int2, (double)owner.getZ()).isFree(true)) {
                            owner.pathToLocation(int1, int2, (int)owner.getZ());
                            zombie0.AllowRepathDelay = 200.0F;
                        }
                    }

                    zombie0.networkAI.mindSync.zombieIdleUpdate();
                }
            }
        }
    }

    @Override
    public void exit(IsoGameCharacter owner) {
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
    }

    private float pickRandomWanderInterval() {
        float float0 = Rand.Next(400, 1000);
        if (!RainManager.isRaining()) {
            float0 *= 1.5F;
        }

        return float0;
    }
}
