// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.vehicleSound.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.vehicleSound.VehicleSoundOwner;

public class ParameterVehicleGear extends FMODLocalParameter {
    private final VehicleSoundOwner vehicle;

    public ParameterVehicleGear(VehicleSoundOwner vehicle) {
        super("VehicleGear");
        this.vehicle = vehicle;
    }

    @Override
    public float calculateCurrentValue() {
        return this.vehicle.getTransmissionNumber() + 1.0F;
    }
}
