// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;

public final class ParameterCurrentZone extends FMODLocalParameter {
    private final IsoObject object;
    private zombie.iso.zones.Zone metaZone;
    private ParameterCurrentZone.Zone zone = ParameterCurrentZone.Zone.None;

    public ParameterCurrentZone(IsoObject object) {
        super("CurrentZone");
        this.object = object;
    }

    @Override
    public float calculateCurrentValue() {
        IsoGridSquare square = this.object.getSquare();
        if (square == null) {
            this.zone = ParameterCurrentZone.Zone.None;
            return this.zone.label;
        }

        if (square.zone == this.metaZone) {
            return this.zone.label;
        }

        this.metaZone = square.zone;
        if (this.metaZone != null && this.metaZone.type != null) {
            this.zone = switch (this.metaZone.type) {
                case "DeepForest" -> ParameterCurrentZone.Zone.DeepForest;
                case "Farm" -> ParameterCurrentZone.Zone.Farm;
                case "Forest" -> ParameterCurrentZone.Zone.Forest;
                case "Nav" -> ParameterCurrentZone.Zone.Nav;
                case "TownZone" -> ParameterCurrentZone.Zone.Town;
                case "TrailerPark" -> ParameterCurrentZone.Zone.TrailerPark;
                case "Vegitation" -> ParameterCurrentZone.Zone.Vegetation;
                default -> this.metaZone.type.endsWith("Forest") ? ParameterCurrentZone.Zone.Forest : ParameterCurrentZone.Zone.None;
            };
            return this.zone.label;
        } else {
            this.zone = ParameterCurrentZone.Zone.None;
            return this.zone.label;
        }
    }

    enum Zone {
        None(0),
        DeepForest(1),
        Farm(2),
        Forest(3),
        Nav(4),
        Town(5),
        TrailerPark(6),
        Vegetation(7);

        final int label;

        Zone(final int label) {
            this.label = label;
        }
    }
}
