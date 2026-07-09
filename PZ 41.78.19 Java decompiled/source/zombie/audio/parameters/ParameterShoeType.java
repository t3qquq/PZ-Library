// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.scripting.objects.Item;

public final class ParameterShoeType extends FMODLocalParameter {
    private static final ItemVisuals tempItemVisuals = new ItemVisuals();
    private final IsoGameCharacter character;
    private ParameterShoeType.ShoeType shoeType = null;

    public ParameterShoeType(IsoGameCharacter _character) {
        super("ShoeType");
        this.character = _character;
    }

    @Override
    public float calculateCurrentValue() {
        if (this.shoeType == null) {
            this.shoeType = this.getShoeType();
        }

        return this.shoeType.label;
    }

    private ParameterShoeType.ShoeType getShoeType() {
        this.character.getItemVisuals(tempItemVisuals);
        Item item0 = null;

        for (int int0 = 0; int0 < tempItemVisuals.size(); int0++) {
            ItemVisual itemVisual = tempItemVisuals.get(int0);
            Item item1 = itemVisual.getScriptItem();
            if (item1 != null && "Shoes".equals(item1.getBodyLocation())) {
                item0 = item1;
                break;
            }
        }

        if (item0 == null) {
            return ParameterShoeType.ShoeType.Barefoot;
        } else {
            String string = item0.getName();
            if (string.contains("Boots") || string.contains("Wellies")) {
                return ParameterShoeType.ShoeType.Boots;
            } else if (string.contains("FlipFlop")) {
                return ParameterShoeType.ShoeType.FlipFlops;
            } else if (string.contains("Slippers")) {
                return ParameterShoeType.ShoeType.Slippers;
            } else {
                return string.contains("Trainer") ? ParameterShoeType.ShoeType.Sneakers : ParameterShoeType.ShoeType.Shoes;
            }
        }
    }

    public void setShoeType(ParameterShoeType.ShoeType _shoeType) {
        this.shoeType = _shoeType;
    }

    private static enum ShoeType {
        Barefoot(0),
        Boots(1),
        FlipFlops(2),
        Shoes(3),
        Slippers(4),
        Sneakers(5);

        final int label;

        private ShoeType(int int1) {
            this.label = int1;
        }
    }
}
