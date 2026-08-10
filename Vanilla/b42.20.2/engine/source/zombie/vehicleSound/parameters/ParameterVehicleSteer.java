// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.vehicleSound.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.core.math.PZMath;
import zombie.vehicleSound.VehicleSoundOwner;

public class ParameterVehicleSteer extends FMODLocalParameter {
    private final VehicleSoundOwner vehicle;

    public ParameterVehicleSteer(VehicleSoundOwner vehicle) {
        super("VehicleSteer");
        this.vehicle = vehicle;
    }

    @Override
    public float calculateCurrentValue() {
        if (!this.vehicle.isEngineRunning()) {
            return 0.0F;
        }

        float value = this.vehicle.getMaxWheelSteering();
        return (int)(PZMath.clamp(value, 0.0F, 1.0F) * 100.0F) / 100.0F;
    }
}
