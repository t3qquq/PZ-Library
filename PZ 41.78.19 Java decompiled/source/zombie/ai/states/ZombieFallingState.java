// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;

public final class ZombieFallingState extends State {
    private static final ZombieFallingState _instance = new ZombieFallingState();

    public static ZombieFallingState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter character) {
        character.setVariable("bHardFall", false);
        character.clearVariable("bLandAnimFinished");
    }

    @Override
    public void execute(IsoGameCharacter var1) {
    }

    @Override
    public void exit(IsoGameCharacter character) {
        character.clearVariable("bHardFall");
        character.clearVariable("bLandAnimFinished");
    }

    @Override
    public void animEvent(IsoGameCharacter character, AnimEvent animEvent) {
        if (animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
            character.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
        }
    }
}
