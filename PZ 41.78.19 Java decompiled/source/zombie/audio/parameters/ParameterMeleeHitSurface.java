// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.characters.IsoGameCharacter;

public final class ParameterMeleeHitSurface extends FMODLocalParameter {
    private final IsoGameCharacter character;
    private ParameterMeleeHitSurface.Material material = ParameterMeleeHitSurface.Material.Default;

    public ParameterMeleeHitSurface(IsoGameCharacter _character) {
        super("MeleeHitSurface");
        this.character = _character;
    }

    @Override
    public float calculateCurrentValue() {
        return this.getMaterial().label;
    }

    private ParameterMeleeHitSurface.Material getMaterial() {
        return this.material;
    }

    public void setMaterial(ParameterMeleeHitSurface.Material _material) {
        this.material = _material;
    }

    public static enum Material {
        Default(0),
        Body(1),
        Fabric(2),
        Glass(3),
        Head(4),
        Metal(5),
        Plastic(6),
        Stone(7),
        Wood(8),
        GarageDoor(9),
        MetalDoor(10),
        MetalGate(11),
        PrisonMetalDoor(12),
        SlidingGlassDoor(13),
        WoodDoor(14),
        WoodGate(15),
        Tree(16);

        final int label;

        private Material(int int1) {
            this.label = int1;
        }
    }
}
