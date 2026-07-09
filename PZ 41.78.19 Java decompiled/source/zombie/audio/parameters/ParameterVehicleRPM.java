// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.core.math.PZMath;
import zombie.vehicles.BaseVehicle;

public class ParameterVehicleRPM extends FMODLocalParameter {
    private final BaseVehicle vehicle;

    public ParameterVehicleRPM(BaseVehicle _vehicle) {
        super("VehicleRPM");
        this.vehicle = _vehicle;
    }

    @Override
    public float calculateCurrentValue() {
        float float0 = PZMath.clamp((float)this.vehicle.getEngineSpeed(), 0.0F, 7000.0F);
        float float1 = this.vehicle.getScript().getEngineIdleSpeed();
        float float2 = float1 * 1.1F;
        float float3 = 800.0F;
        float float4 = 7000.0F;
        float float5;
        if (float0 < float2) {
            float5 = float0 / float2 * float3;
        } else {
            float5 = float3 + (float0 - float2) / (7000.0F - float2) * (float4 - float3);
        }

        return (int)((float5 + 50.0F - 1.0F) / 50.0F) * 50.0F;
    }
}
