// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;

/**
 * Just a burnt car with 0, 1 or 2 burnt corpses near it
 */
public final class RVSBurntCar extends RandomizedVehicleStoryBase {
    public RVSBurntCar() {
        this.name = "Burnt Car";
        this.minZoneWidth = 2;
        this.minZoneHeight = 5;
        this.setChance(13);
    }

    @Override
    public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
        this.callVehicleStorySpawner(zone, chunk, 0.0F);
    }

    @Override
    public boolean initVehicleStorySpawner(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean debug) {
        VehicleStorySpawner vehicleStorySpawner = VehicleStorySpawner.getInstance();
        vehicleStorySpawner.clear();
        Vector2 vector = IsoDirections.N.ToVector();
        float float0 = (float) (Math.PI / 6);
        if (debug) {
            float0 = 0.0F;
        }

        vector.rotate(Rand.Next(-float0, float0));
        vehicleStorySpawner.addElement("vehicle1", 0.0F, 0.0F, vector.getDirection(), 2.0F, 5.0F);
        vehicleStorySpawner.setParameter("zone", zone);
        return true;
    }

    @Override
    public void spawnElement(VehicleStorySpawner spawner, VehicleStorySpawner.Element element) {
        IsoGridSquare square = element.square;
        if (square != null) {
            float float0 = element.z;
            IsoMetaGrid.Zone zone = spawner.getParameter("zone", IsoMetaGrid.Zone.class);
            String string = element.id;
            byte byte0 = -1;
            switch (string.hashCode()) {
                case 2014205573:
                    if (string.equals("vehicle1")) {
                        byte0 = 0;
                    }
                default:
                    switch (byte0) {
                        case 0:
                            BaseVehicle vehicle = this.addVehicle(
                                zone, element.position.x, element.position.y, float0, element.direction, null, "Base.CarNormal", null, null
                            );
                            if (vehicle != null) {
                                vehicle = vehicle.setSmashed("right");
                            }
                    }
            }
        }
    }
}
