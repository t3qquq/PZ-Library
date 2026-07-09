// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.core.math.PZMath;
import zombie.network.GameClient;
import zombie.scripting.objects.VehicleScript;
import zombie.vehicles.BaseVehicle;

public class ParameterVehicleSkid extends FMODLocalParameter {
    private final BaseVehicle vehicle;
    private final BaseVehicle.WheelInfo[] wheelInfo;

    public ParameterVehicleSkid(BaseVehicle _vehicle) {
        super("VehicleSkid");
        this.vehicle = _vehicle;
        this.wheelInfo = _vehicle.wheelInfo;
    }

    @Override
    public float calculateCurrentValue() {
        float float0 = 1.0F;
        if (GameClient.bClient && !this.vehicle.isLocalPhysicSim()) {
            return float0;
        } else {
            VehicleScript vehicleScript = this.vehicle.getScript();
            if (vehicleScript == null) {
                return float0;
            } else {
                int int0 = 0;

                for (int int1 = vehicleScript.getWheelCount(); int0 < int1; int0++) {
                    float0 = PZMath.min(float0, this.wheelInfo[int0].skidInfo);
                }

                return (int)(100.0F - PZMath.clamp(float0, 0.0F, 1.0F) * 100.0F) / 100.0F;
            }
        }
    }
}
