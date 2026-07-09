// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.characters.IsoGameCharacter;

public final class ParameterCharacterMovementSpeed extends FMODLocalParameter {
    private final IsoGameCharacter character;
    private ParameterCharacterMovementSpeed.MovementType movementType = ParameterCharacterMovementSpeed.MovementType.Walk;

    public ParameterCharacterMovementSpeed(IsoGameCharacter _character) {
        super("CharacterMovementSpeed");
        this.character = _character;
    }

    @Override
    public float calculateCurrentValue() {
        return this.movementType.label;
    }

    public void setMovementType(ParameterCharacterMovementSpeed.MovementType _movementType) {
        this.movementType = _movementType;
    }

    public static enum MovementType {
        SneakWalk(0),
        SneakRun(1),
        Strafe(2),
        Walk(3),
        Run(4),
        Sprint(5);

        public final int label;

        private MovementType(int int1) {
            this.label = int1;
        }
    }
}
