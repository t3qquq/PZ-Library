// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

/**
 * Created by kroto on 1/17/2017.
 */
public class VehicleInterpolationData implements Comparable<VehicleInterpolationData> {
    protected long time;
    protected float x;
    protected float y;
    protected float z;
    protected float qx;
    protected float qy;
    protected float qz;
    protected float qw;
    protected float vx;
    protected float vy;
    protected float vz;
    protected float engineSpeed;
    protected float throttle;
    protected short wheelsCount = 4;
    protected float[] wheelSteering = new float[4];
    protected float[] wheelRotation = new float[4];
    protected float[] wheelSkidInfo = new float[4];
    protected float[] wheelSuspensionLength = new float[4];

    protected void setNumWheels(short short0) {
        if (short0 > this.wheelsCount) {
            this.wheelSteering = new float[short0];
            this.wheelRotation = new float[short0];
            this.wheelSkidInfo = new float[short0];
            this.wheelSuspensionLength = new float[short0];
        }

        this.wheelsCount = short0;
    }

    void copy(VehicleInterpolationData vehicleInterpolationData0) {
        this.time = vehicleInterpolationData0.time;
        this.x = vehicleInterpolationData0.x;
        this.y = vehicleInterpolationData0.y;
        this.z = vehicleInterpolationData0.z;
        this.qx = vehicleInterpolationData0.qx;
        this.qy = vehicleInterpolationData0.qy;
        this.qz = vehicleInterpolationData0.qz;
        this.qw = vehicleInterpolationData0.qw;
        this.vx = vehicleInterpolationData0.vx;
        this.vy = vehicleInterpolationData0.vy;
        this.vz = vehicleInterpolationData0.vz;
        this.engineSpeed = vehicleInterpolationData0.engineSpeed;
        this.throttle = vehicleInterpolationData0.throttle;
        this.setNumWheels(vehicleInterpolationData0.wheelsCount);

        for (int int0 = 0; int0 < vehicleInterpolationData0.wheelsCount; int0++) {
            this.wheelSteering[int0] = vehicleInterpolationData0.wheelSteering[int0];
            this.wheelRotation[int0] = vehicleInterpolationData0.wheelRotation[int0];
            this.wheelSkidInfo[int0] = vehicleInterpolationData0.wheelSkidInfo[int0];
            this.wheelSuspensionLength[int0] = vehicleInterpolationData0.wheelSuspensionLength[int0];
        }
    }

    public int compareTo(VehicleInterpolationData o) {
        return Long.compare(this.time, o.time);
    }
}
