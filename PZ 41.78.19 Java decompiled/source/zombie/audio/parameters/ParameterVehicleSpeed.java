// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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
