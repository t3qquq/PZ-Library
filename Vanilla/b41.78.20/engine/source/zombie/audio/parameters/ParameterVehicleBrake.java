// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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
