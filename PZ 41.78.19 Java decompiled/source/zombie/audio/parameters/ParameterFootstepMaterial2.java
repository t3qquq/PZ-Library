// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.characters.IsoGameCharacter;
import zombie.iso.objects.IsoBrokenGlass;

public final class ParameterFootstepMaterial2 extends FMODLocalParameter {
    private final IsoGameCharacter character;

    public ParameterFootstepMaterial2(IsoGameCharacter _character) {
        super("FootstepMaterial2");
        this.character = _character;
    }

    @Override
    public float calculateCurrentValue() {
        return this.getMaterial().label;
    }

    private ParameterFootstepMaterial2.FootstepMaterial2 getMaterial() {
        if (this.character.getCurrentSquare() == null) {
            return ParameterFootstepMaterial2.FootstepMaterial2.None;
        } else {
            IsoBrokenGlass brokenGlass = this.character.getCurrentSquare().getBrokenGlass();
            if (brokenGlass != null) {
                return ParameterFootstepMaterial2.FootstepMaterial2.BrokenGlass;
            } else {
                float float0 = this.character.getCurrentSquare().getPuddlesInGround();
                if (float0 > 0.5F) {
                    return ParameterFootstepMaterial2.FootstepMaterial2.PuddleDeep;
                } else {
                    return float0 > 0.1F ? ParameterFootstepMaterial2.FootstepMaterial2.PuddleShallow : ParameterFootstepMaterial2.FootstepMaterial2.None;
                }
            }
        }
    }

    static enum FootstepMaterial2 {
        None(0),
        BrokenGlass(1),
        PuddleShallow(2),
        PuddleDeep(3);

        final int label;

        private FootstepMaterial2(int int1) {
            this.label = int1;
        }
    }
}
