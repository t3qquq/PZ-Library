// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.UsedFromLua;
import zombie.core.random.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.Vector2;
import zombie.iso.zones.Zone;
import zombie.vehicles.BaseVehicle;

/**
 * Just a burnt car with 0, 1 or 2 burnt corpses near it
 */
@UsedFromLua
public final class RVSBurntCar extends RandomizedVehicleStoryBase {
    public RVSBurntCar() {
        this.name = "Burnt Car";
        this.minZoneWidth = 2;
        this.minZoneHeight = 5;
        this.setChance(130);
    }

    @Override
    public void randomizeVehicleStory(Zone zone, IsoChunk chunk) {
        this.callVehicleStorySpawner(zone, chunk, 0.0F);
    }

    @Override
    public boolean initVehicleStorySpawner(Zone zone, IsoChunk chunk, boolean debug) {
        VehicleStorySpawner spawner = VehicleStorySpawner.getInstance();
        spawner.clear();
        Vector2 v = IsoDirections.N.ToVector();
        float randAngle = (float) (Math.PI / 6);
        if (debug) {
            randAngle = 0.0F;
        }

        v.rotate(Rand.Next(-randAngle, randAngle));
        spawner.addElement("vehicle1", 0.0F, 0.0F, v.getDirection(), 2.0F, 5.0F);
        spawner.setParameter("zone", zone);
        return true;
    }

    @Override
    public void spawnElement(VehicleStorySpawner spawner, VehicleStorySpawner.Element element) {
        IsoGridSquare square = element.square;
        if (square != null) {
            float z = element.z;
            Zone zone = spawner.getParameter("zone", Zone.class);
            switch (element.id) {
                case "vehicle1":
                    BaseVehicle vehicle1 = this.addVehicle(
                        zone, element.position.x, element.position.y, z, element.direction, "normalburnt", null, null, null, true
                    );
                    if (vehicle1 != null) {
                        vehicle1.setAlarmed(false);
                        vehicle1.setSmashed("right");
                    }
            }
        }
    }
}
