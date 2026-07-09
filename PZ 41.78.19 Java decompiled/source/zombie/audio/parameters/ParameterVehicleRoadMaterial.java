// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.vehicles.BaseVehicle;

public final class ParameterVehicleRoadMaterial extends FMODLocalParameter {
    private final BaseVehicle vehicle;

    public ParameterVehicleRoadMaterial(BaseVehicle _vehicle) {
        super("VehicleRoadMaterial");
        this.vehicle = _vehicle;
    }

    @Override
    public float calculateCurrentValue() {
        if (!this.vehicle.isEngineRunning()) {
            return Float.isNaN(this.getCurrentValue()) ? 0.0F : this.getCurrentValue();
        } else {
            return this.getMaterial().label;
        }
    }

    private ParameterVehicleRoadMaterial.Material getMaterial() {
        IsoGridSquare square = this.vehicle.getCurrentSquare();
        if (square == null) {
            return ParameterVehicleRoadMaterial.Material.Concrete;
        } else {
            IsoObject object = this.vehicle.getCurrentSquare().getFloor();
            if (object != null && object.getSprite() != null && object.getSprite().getName() != null) {
                String string = object.getSprite().getName();
                if (string.endsWith("blends_natural_01_5")
                    || string.endsWith("blends_natural_01_6")
                    || string.endsWith("blends_natural_01_7")
                    || string.endsWith("blends_natural_01_0")) {
                    return ParameterVehicleRoadMaterial.Material.Sand;
                } else if (string.endsWith("blends_natural_01_64")
                    || string.endsWith("blends_natural_01_69")
                    || string.endsWith("blends_natural_01_70")
                    || string.endsWith("blends_natural_01_71")) {
                    return ParameterVehicleRoadMaterial.Material.Dirt;
                } else if (string.startsWith("blends_natural_01")) {
                    return ParameterVehicleRoadMaterial.Material.Grass;
                } else if (string.endsWith("blends_street_01_48")
                    || string.endsWith("blends_street_01_53")
                    || string.endsWith("blends_street_01_54")
                    || string.endsWith("blends_street_01_55")) {
                    return ParameterVehicleRoadMaterial.Material.Gravel;
                } else if (string.startsWith("floors_interior_tilesandwood_01_")) {
                    int int0 = Integer.parseInt(string.replaceFirst("floors_interior_tilesandwood_01_", ""));
                    return int0 > 40 && int0 < 48 ? ParameterVehicleRoadMaterial.Material.Wood : ParameterVehicleRoadMaterial.Material.Concrete;
                } else if (string.startsWith("carpentry_02_")) {
                    return ParameterVehicleRoadMaterial.Material.Wood;
                } else if (string.contains("interior_carpet_")) {
                    return ParameterVehicleRoadMaterial.Material.Carpet;
                } else {
                    float float0 = square.getPuddlesInGround();
                    return float0 > 0.1 ? ParameterVehicleRoadMaterial.Material.Puddle : ParameterVehicleRoadMaterial.Material.Concrete;
                }
            } else {
                return ParameterVehicleRoadMaterial.Material.Concrete;
            }
        }
    }

    static enum Material {
        Concrete(0),
        Grass(1),
        Gravel(2),
        Puddle(3),
        Snow(4),
        Wood(5),
        Carpet(6),
        Dirt(7),
        Sand(8);

        final int label;

        private Material(int int1) {
            this.label = int1;
        }
    }
}
