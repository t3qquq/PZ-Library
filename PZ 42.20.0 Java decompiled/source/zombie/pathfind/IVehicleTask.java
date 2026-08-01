// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.pathfind;

import zombie.vehicles.BaseVehicle;

interface IVehicleTask {
    void init(PolygonalMap2 arg0, BaseVehicle arg1);

    void execute();

    void release();
}
