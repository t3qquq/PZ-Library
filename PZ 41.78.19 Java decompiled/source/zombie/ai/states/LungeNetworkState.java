// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import java.util.HashMap;
import org.joml.Vector3f;
import zombie.GameTime;
import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.gameStates.IngameState;
import zombie.iso.Vector2;
import zombie.util.Type;

public class LungeNetworkState extends State {
    static LungeNetworkState _instance = new LungeNetworkState();
    private Vector2 temp = new Vector2();
    private final Vector3f worldPos = new Vector3f();
    private static final Integer PARAM_TICK_COUNT = 0;

    public static LungeNetworkState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter character) {
        HashMap hashMap = character.getStateMachineParams(this);
        WalkTowardNetworkState.instance().enter(character);
        IsoZombie zombie0 = (IsoZombie)character;
        zombie0.LungeTimer = 180.0F;
        hashMap.put(PARAM_TICK_COUNT, IngameState.instance.numberTicks);
    }

    @Override
    public void execute(IsoGameCharacter character) {
        WalkTowardNetworkState.instance().execute(character);
        IsoZombie zombie0 = (IsoZombie)character;
        character.setOnFloor(false);
        character.setShootable(true);
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

        HashMap hashMap = character.getStateMachineParams(this);
        long long0 = (Long)hashMap.get(PARAM_TICK_COUNT);
        if (IngameState.instance.numberTicks - long0 == 2L) {
            ((IsoZombie)character).parameterZombieState.setState(ParameterZombieState.State.LockTarget);
        }
    }

    @Override
    public void exit(IsoGameCharacter character) {
        WalkTowardNetworkState.instance().exit(character);
    }

    @Override
    public boolean isMoving(IsoGameCharacter var1) {
        return true;
    }
}
