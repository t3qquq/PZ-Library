// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import java.util.HashMap;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;

public final class ZombieTurnAlerted extends State {
    private static final ZombieTurnAlerted _instance = new ZombieTurnAlerted();
    public static final Integer PARAM_TARGET_ANGLE = 0;

    public static ZombieTurnAlerted instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter character) {
        HashMap hashMap = character.getStateMachineParams(this);
        float float0 = (Float)hashMap.get(PARAM_TARGET_ANGLE);
        character.getAnimationPlayer().setTargetAngle(float0);
    }

    @Override
    public void execute(IsoGameCharacter var1) {
    }

    @Override
    public void exit(IsoGameCharacter character) {
        character.pathToSound(character.getPathTargetX(), character.getPathTargetY(), character.getPathTargetZ());
        ((IsoZombie)character).alerted = false;
    }

    public void setParams(IsoGameCharacter character, float float0) {
        HashMap hashMap = character.getStateMachineParams(this);
        hashMap.clear();
        hashMap.put(PARAM_TARGET_ANGLE, float0);
    }
}
