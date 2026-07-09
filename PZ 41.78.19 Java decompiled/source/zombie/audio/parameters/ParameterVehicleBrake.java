// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.vehicles.BaseVehicle;

public class ParameterVehicleBrake extends FMODLocalParameter {
    private final BaseVehicle vehicle;

    public ParameterVehicleBrake(BaseVehicle _vehicle) {
        super("VehicleBrake");
        this.vehicle = _vehicle;
    }

    @Override
    public float calculateCurrentValue() {
        return this.vehicle.getController().isBrakePedalPressed() ? 1.0F : 0.0F;
    }
}
