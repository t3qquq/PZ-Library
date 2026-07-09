// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import fmod.fmod.FMODManager;
import zombie.audio.FMODLocalParameter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.properties.PropertyContainer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.util.list.PZArrayList;

public final class ParameterFootstepMaterial extends FMODLocalParameter {
    private final IsoGameCharacter character;

    public ParameterFootstepMaterial(IsoGameCharacter _character) {
        super("FootstepMaterial");
        this.character = _character;
    }

    @Override
    public float calculateCurrentValue() {
        return this.getMaterial().label;
    }

    private ParameterFootstepMaterial.FootstepMaterial getMaterial() {
        if (FMODManager.instance.getNumListeners() == 1) {
            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                IsoPlayer player = IsoPlayer.players[int0];
                if (player != null && player != this.character && !player.Traits.Deaf.isSet()) {
                    if ((int)player.getZ() < (int)this.character.getZ()) {
                        return ParameterFootstepMaterial.FootstepMaterial.Upstairs;
                    }
                    break;
                }
            }
        }

        Object object0 = null;
        IsoObject object1 = null;
        IsoGridSquare square = this.character.getCurrentSquare();
        if (square != null) {
            PZArrayList pZArrayList = square.getObjects();

            for (int int1 = 0; int1 < pZArrayList.size(); int1++) {
                IsoObject object2 = (IsoObject)pZArrayList.get(int1);
                if (!(object2 instanceof IsoWorldInventoryObject)) {
                    PropertyContainer propertyContainer = object2.getProperties();
                    if (propertyContainer != null) {
                        if (propertyContainer.Is(IsoFlagType.solidfloor)) {
                            ;
                        }

                        if (propertyContainer.Is("FootstepMaterial")) {
                            object1 = object2;
                        }
                    }
                }
            }
        }

        if (object1 != null) {
            try {
                String string = object1.getProperties().Val("FootstepMaterial");
                return ParameterFootstepMaterial.FootstepMaterial.valueOf(string);
            } catch (IllegalArgumentException illegalArgumentException) {
                boolean boolean0 = true;
            }
        }

        return ParameterFootstepMaterial.FootstepMaterial.Concrete;
    }

    static enum FootstepMaterial {
        Upstairs(0),
        BrokenGlass(1),
        Concrete(2),
        Grass(3),
        Gravel(4),
        Puddle(5),
        Snow(6),
        Wood(7),
        Carpet(8),
        Dirt(9),
        Sand(10),
        Ceramic(11),
        Metal(12);

        final int label;

        private FootstepMaterial(int int1) {
            this.label = int1;
        }
    }
}
