// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import fmod.fmod.FMODManager;
import zombie.ai.State;
import zombie.audio.parameters.ParameterCharacterMovementSpeed;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.IsoDirections;

public final class CollideWithWallState extends State {
    private static final CollideWithWallState _instance = new CollideWithWallState();

    public static CollideWithWallState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter character) {
        character.setIgnoreMovement(true);
        if (character instanceof IsoPlayer) {
            ((IsoPlayer)character).setIsAiming(false);
        }

        if (character.isCollidedN()) {
            character.setDir(IsoDirections.N);
        }

        if (character.isCollidedS()) {
            character.setDir(IsoDirections.S);
        }

        if (character.isCollidedE()) {
            character.setDir(IsoDirections.E);
        }

        if (character.isCollidedW()) {
            character.setDir(IsoDirections.W);
        }

        character.setCollideType("wall");
    }

    @Override
    public void execute(IsoGameCharacter character) {
        character.setLastCollideTime(70.0F);
    }

    @Override
    public void exit(IsoGameCharacter character) {
        character.setCollideType(null);
        character.setIgnoreMovement(false);
    }

    @Override
    public void animEvent(IsoGameCharacter character, AnimEvent animEvent) {
        if ("PlayCollideSound".equalsIgnoreCase(animEvent.m_EventName)) {
            long long0 = character.playSound(animEvent.m_ParameterValue);
            ParameterCharacterMovementSpeed parameterCharacterMovementSpeed = ((IsoPlayer)character).getParameterCharacterMovementSpeed();
            character.getEmitter()
                .setParameterValue(long0, parameterCharacterMovementSpeed.getParameterDescription(), ParameterCharacterMovementSpeed.MovementType.Sprint.label);
            character.getEmitter().setParameterValue(long0, FMODManager.instance.getParameterDescription("TripObstacleType"), 7.0F);
        }
    }
}
