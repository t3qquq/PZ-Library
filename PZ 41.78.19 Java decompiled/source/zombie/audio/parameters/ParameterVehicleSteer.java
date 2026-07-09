// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.core.math.PZMath;
import zombie.scripting.objects.VehicleScript;
import zombie.vehicles.BaseVehicle;

public class ParameterVehicleSteer extends FMODLocalParameter {
    private final BaseVehicle vehicle;

    public ParameterVehicleSteer(BaseVehicle _vehicle) {
        super("VehicleSteer");
        this.vehicle = _vehicle;
    }

    @Override
    public float calculateCurrentValue() {
        float float0 = 0.0F;
        if (!this.vehicle.isEngineRunning()) {
            return float0;
        } else {
            VehicleScript vehicleScript = this.vehicle.getScript();
            if (vehicleScript == null) {
                return float0;
            } else {
                BaseVehicle.WheelInfo[] wheelInfos = this.vehicle.wheelInfo;
                int int0 = 0;

                for (int int1 = vehicleScript.getWheelCount(); int0 < int1; int0++) {
                    float0 = PZMath.max(float0, Math.abs(wheelInfos[int0].steering));
                }

                return (int)(PZMath.clamp(float0, 0.0F, 1.0F) * 100.0F) / 100.0F;
            }
        }
    }
}
