// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;

public final class ParameterCurrentZone extends FMODLocalParameter {
    private final IsoObject object;
    private IsoMetaGrid.Zone metaZone;
    private ParameterCurrentZone.Zone zone = ParameterCurrentZone.Zone.None;

    public ParameterCurrentZone(IsoObject objectx) {
        super("CurrentZone");
        this.object = objectx;
    }

    @Override
    public float calculateCurrentValue() {
        IsoGridSquare square = this.object.getSquare();
        if (square == null) {
            this.zone = ParameterCurrentZone.Zone.None;
            return this.zone.label;
        } else if (square.zone == this.metaZone) {
            return this.zone.label;
        } else {
            this.metaZone = square.zone;
            if (this.metaZone == null) {
                this.zone = ParameterCurrentZone.Zone.None;
                return this.zone.label;
            } else {
                String string = this.metaZone.type;

                this.zone = switch (string) {
                    case "DeepForest" -> ParameterCurrentZone.Zone.DeepForest;
                    case "Farm" -> ParameterCurrentZone.Zone.Farm;
                    case "Forest" -> ParameterCurrentZone.Zone.Forest;
                    case "Nav" -> ParameterCurrentZone.Zone.Nav;
                    case "TownZone" -> ParameterCurrentZone.Zone.Town;
                    case "TrailerPark" -> ParameterCurrentZone.Zone.TrailerPark;
                    case "Vegitation" -> ParameterCurrentZone.Zone.Vegetation;
                    default -> ParameterCurrentZone.Zone.None;
                };
                return this.zone.label;
            }
        }
    }

    static enum Zone {
        None(0),
        DeepForest(1),
        Farm(2),
        Forest(3),
        Nav(4),
        Town(5),
        TrailerPark(6),
        Vegetation(7);

        final int label;

        private Zone(int int1) {
            this.label = int1;
        }
    }
}
