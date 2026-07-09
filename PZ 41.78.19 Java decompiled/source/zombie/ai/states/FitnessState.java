// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.network.GameClient;

public final class FitnessState extends State {
    private static final FitnessState _instance = new FitnessState();

    public static FitnessState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        owner.setIgnoreMovement(true);
        owner.setVariable("FitnessFinished", false);
        owner.clearVariable("ExerciseStarted");
        owner.clearVariable("ExerciseEnded");
    }

    @Override
    public void execute(IsoGameCharacter owner) {
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        if (GameClient.bClient && owner instanceof IsoPlayer && ((IsoPlayer)owner).isLocalPlayer()) {
            GameClient.sendEvent((IsoPlayer)owner, "EventUpdateFitness");
        }

        owner.setIgnoreMovement(false);
        owner.clearVariable("FitnessFinished");
        owner.clearVariable("ExerciseStarted");
        owner.clearVariable("ExerciseHand");
        owner.clearVariable("FitnessStruggle");
        owner.setVariable("ExerciseEnded", true);
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
    }
}
