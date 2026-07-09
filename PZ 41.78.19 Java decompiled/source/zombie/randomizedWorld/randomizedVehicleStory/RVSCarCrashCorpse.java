// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;

public final class RVSCarCrashCorpse extends RandomizedVehicleStoryBase {
    /**
     * Vehicle alone, corpse not so far from the car's front with blood trail
     */
    public RVSCarCrashCorpse() {
        this.name = "Basic Car Crash Corpse";
        this.minZoneWidth = 6;
        this.minZoneHeight = 11;
        this.setChance(10);
    }

    @Override
    public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
        float float0 = (float) (Math.PI / 6);
        this.callVehicleStorySpawner(zone, chunk, Rand.Next(-float0, float0));
    }

    @Override
    public boolean initVehicleStorySpawner(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean debug) {
        VehicleStorySpawner vehicleStorySpawner = VehicleStorySpawner.getInstance();
        vehicleStorySpawner.clear();
        Vector2 vector = IsoDirections.N.ToVector();
        float float0 = 2.5F;
        vehicleStorySpawner.addElement("vehicle1", 0.0F, float0, vector.getDirection(), 2.0F, 5.0F);
        vehicleStorySpawner.addElement("corpse", 0.0F, float0 - (debug ? 7 : Rand.Next(4, 7)), vector.getDirection() + (float) Math.PI, 1.0F, 2.0F);
        vehicleStorySpawner.setParameter("zone", zone);
        return true;
    }

    @Override
    public void spawnElement(VehicleStorySpawner spawner, VehicleStorySpawner.Element element) {
        IsoGridSquare square = element.square;
        if (square != null) {
            float float0 = element.z;
            IsoMetaGrid.Zone zone = spawner.getParameter("zone", IsoMetaGrid.Zone.class);
            BaseVehicle vehicle = spawner.getParameter("vehicle1", BaseVehicle.class);
            String string = element.id;
            switch (string) {
                case "corpse":
                    if (vehicle != null) {
                        createRandomDeadBody(element.position.x, element.position.y, element.z, element.direction, false, 35, 30, null);
                        this.addTrailOfBlood(element.position.x, element.position.y, element.z, element.direction, 15);
                    }
                    break;
                case "vehicle1":
                    vehicle = this.addVehicle(zone, element.position.x, element.position.y, float0, element.direction, "bad", null, null, null);
                    if (vehicle != null) {
                        vehicle = vehicle.setSmashed("Front");
                        vehicle.setBloodIntensity("Front", 1.0F);
                        spawner.setParameter("vehicle1", vehicle);
                    }
            }
        }
    }
}
