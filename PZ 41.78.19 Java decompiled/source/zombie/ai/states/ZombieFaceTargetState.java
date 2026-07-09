// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;

public final class ZombieFaceTargetState extends State {
    private static final ZombieFaceTargetState _instance = new ZombieFaceTargetState();

    public static ZombieFaceTargetState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter var1) {
    }

    @Override
    public void execute(IsoGameCharacter character) {
        IsoZombie zombie0 = (IsoZombie)character;
        if (zombie0.getTarget() != null) {
            zombie0.faceThisObject(zombie0.getTarget());
        }
    }

    @Override
    public void exit(IsoGameCharacter var1) {
    }
}
