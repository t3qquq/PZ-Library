// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.vehicles.BaseVehicle;

public class ParameterVehicleSpeed extends FMODLocalParameter {
    private final BaseVehicle vehicle;

    public ParameterVehicleSpeed(BaseVehicle _vehicle) {
        super("VehicleSpeed");
        this.vehicle = _vehicle;
    }

    @Override
    public float calculateCurrentValue() {
        return (float)Math.floor(Math.abs(this.vehicle.getCurrentSpeedKmHour()));
    }
}
