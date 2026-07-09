// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import java.util.HashMap;
import zombie.GameTime;
import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.gameStates.IngameState;
import zombie.iso.Vector2;
import zombie.network.GameServer;
import zombie.util.Type;

public final class LungeState extends State {
    private static final LungeState _instance = new LungeState();
    private final Vector2 temp = new Vector2();
    private static final Integer PARAM_TICK_COUNT = 0;

    public static LungeState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        IsoZombie zombie0 = (IsoZombie)owner;
        HashMap hashMap = owner.getStateMachineParams(this);
        if (System.currentTimeMillis() - zombie0.LungeSoundTime > 5000L) {
            String string = "MaleZombieAttack";
            if (zombie0.isFemale()) {
                string = "FemaleZombieAttack";
            }

            if (GameServer.bServer) {
                GameServer.sendZombieSound(IsoZombie.ZombieSound.Lunge, zombie0);
            }

            zombie0.LungeSoundTime = System.currentTimeMillis();
        }

        zombie0.LungeTimer = 180.0F;
        hashMap.put(PARAM_TICK_COUNT, IngameState.instance.numberTicks);
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        IsoZombie zombie0 = (IsoZombie)owner;
        HashMap hashMap = owner.getStateMachineParams(this);
        owner.setOnFloor(false);
        owner.setShootable(true);
        if (zombie0.bLunger) {
            zombie0.walkVariantUse = "ZombieWalk3";
        }

        zombie0.LungeTimer = zombie0.LungeTimer - GameTime.getInstance().getMultiplier() / 1.6F;
        IsoPlayer player = Type.tryCastTo(zombie0.getTarget(), IsoPlayer.class);
        if (player != null && player.isGhostMode()) {
            zombie0.LungeTimer = 0.0F;
        }

        if (zombie0.LungeTimer < 0.0F) {
            zombie0.LungeTimer = 0.0F;
        }

        if (zombie0.LungeTimer <= 0.0F) {
            zombie0.AllowRepathDelay = 0.0F;
        }

        this.temp.x = zombie0.vectorToTarget.x;
        this.temp.y = zombie0.vectorToTarget.y;
        zombie0.getZombieLungeSpeed();
        this.temp.normalize();
        zombie0.setForwardDirection(this.temp);
        zombie0.DirectionFromVector(this.temp);
        zombie0.getVectorFromDirection(zombie0.getForwardDirection());
        zombie0.setForwardDirection(this.temp);
        if (!zombie0.isTargetLocationKnown()
            && zombie0.LastTargetSeenX != -1
            && !owner.getPathFindBehavior2().isTargetLocation(zombie0.LastTargetSeenX + 0.5F, zombie0.LastTargetSeenY + 0.5F, zombie0.LastTargetSeenZ)) {
            zombie0.LungeTimer = 0.0F;
            owner.pathToLocation(zombie0.LastTargetSeenX, zombie0.LastTargetSeenY, zombie0.LastTargetSeenZ);
        }

        long long0 = (Long)hashMap.get(PARAM_TICK_COUNT);
        if (IngameState.instance.numberTicks - long0 == 2L) {
            ((IsoZombie)owner).parameterZombieState.setState(ParameterZombieState.State.LockTarget);
        }
    }

    @Override
    public void exit(IsoGameCharacter chr) {
    }

    /**
     * Return TRUE if the owner is currently moving.   Defaults to FALSE
     */
    @Override
    public boolean isMoving(IsoGameCharacter owner) {
        return true;
    }
}
