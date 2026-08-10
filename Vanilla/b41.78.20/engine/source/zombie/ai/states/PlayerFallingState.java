// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;

public final class PlayerFallingState extends State {
    private static final PlayerFallingState _instance = new PlayerFallingState();

    public static PlayerFallingState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        owner.setVariable("bHardFall", false);
        owner.clearVariable("bLandAnimFinished");
    }

    @Override
    public void execute(IsoGameCharacter owner) {
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        owner.clearVariable("bHardFall");
        owner.clearVariable("bLandAnimFinished");
    }
}
