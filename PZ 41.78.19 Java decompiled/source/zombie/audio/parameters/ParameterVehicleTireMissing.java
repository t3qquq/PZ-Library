// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.scripting.objects.VehicleScript;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclePart;

public class ParameterVehicleTireMissing extends FMODLocalParameter {
    private final BaseVehicle vehicle;

    public ParameterVehicleTireMissing(BaseVehicle _vehicle) {
        super("VehicleTireMissing");
        this.vehicle = _vehicle;
    }

    @Override
    public float calculateCurrentValue() {
        boolean boolean0 = false;
        VehicleScript vehicleScript = this.vehicle.getScript();
        if (vehicleScript != null) {
            for (int int0 = 0; int0 < vehicleScript.getWheelCount(); int0++) {
                VehicleScript.Wheel wheel = vehicleScript.getWheel(int0);
                VehiclePart part = this.vehicle.getPartById("Tire" + wheel.getId());
                if (part == null || part.getInventoryItem() == null) {
                    boolean0 = true;
                    break;
                }
            }
        }

        return boolean0 ? 1.0F : 0.0F;
    }
}
