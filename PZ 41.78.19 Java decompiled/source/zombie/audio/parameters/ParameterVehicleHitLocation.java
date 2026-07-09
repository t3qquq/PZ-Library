// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import org.joml.Vector3f;
import zombie.audio.FMODLocalParameter;
import zombie.scripting.objects.VehicleScript;
import zombie.vehicles.BaseVehicle;

public class ParameterVehicleHitLocation extends FMODLocalParameter {
    private ParameterVehicleHitLocation.HitLocation location = ParameterVehicleHitLocation.HitLocation.Front;

    public ParameterVehicleHitLocation() {
        super("VehicleHitLocation");
    }

    @Override
    public float calculateCurrentValue() {
        return this.location.label;
    }

    public static ParameterVehicleHitLocation.HitLocation calculateLocation(BaseVehicle vehicle, float x, float y, float z) {
        VehicleScript vehicleScript = vehicle.getScript();
        if (vehicleScript == null) {
            return ParameterVehicleHitLocation.HitLocation.Front;
        } else {
            Vector3f vector3f0 = vehicle.getLocalPos(x, y, z, BaseVehicle.TL_vector3f_pool.get().alloc());
            Vector3f vector3f1 = vehicleScript.getExtents();
            Vector3f vector3f2 = vehicleScript.getCenterOfMassOffset();
            float float0 = vector3f2.z - vector3f1.z / 2.0F;
            float float1 = vector3f2.z + vector3f1.z / 2.0F;
            float0 *= 0.9F;
            float1 *= 0.9F;
            ParameterVehicleHitLocation.HitLocation hitLocation;
            if (vector3f0.z >= float0 && vector3f0.z <= float1) {
                hitLocation = ParameterVehicleHitLocation.HitLocation.Side;
            } else if (vector3f0.z > 0.0F) {
                hitLocation = ParameterVehicleHitLocation.HitLocation.Front;
            } else {
                hitLocation = ParameterVehicleHitLocation.HitLocation.Rear;
            }

            BaseVehicle.TL_vector3f_pool.get().release(vector3f0);
            return hitLocation;
        }
    }

    public void setLocation(ParameterVehicleHitLocation.HitLocation _location) {
        this.location = _location;
    }

    public static enum HitLocation {
        Front(0),
        Rear(1),
        Side(2);

        final int label;

        private HitLocation(int int1) {
            this.label = int1;
        }
    }
}
