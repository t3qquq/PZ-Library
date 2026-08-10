// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.characters.IsoGameCharacter;

public final class ParameterEquippedBaggageContainer extends FMODLocalParameter {
    private final IsoGameCharacter character;
    private ParameterEquippedBaggageContainer.ContainerType containerType = ParameterEquippedBaggageContainer.ContainerType.None;

    public ParameterEquippedBaggageContainer(IsoGameCharacter _character) {
        super("EquippedBaggageContainer");
        this.character = _character;
    }

    @Override
    public float calculateCurrentValue() {
        return this.containerType.label;
    }

    public void setContainerType(ParameterEquippedBaggageContainer.ContainerType _containerType) {
        this.containerType = _containerType;
    }

    public void setContainerType(String _containerType) {
        if (_containerType != null) {
            try {
                this.containerType = ParameterEquippedBaggageContainer.ContainerType.valueOf(_containerType);
            } catch (IllegalArgumentException illegalArgumentException) {
            }
        }
    }

    public static enum ContainerType {
        None(0),
        HikingBag(1),
        DuffleBag(2),
        PlasticBag(3),
        SchoolBag(4),
        ToteBag(5),
        GarbageBag(6);

        public final int label;

        private ContainerType(int int1) {
            this.label = int1;
        }
    }
}
