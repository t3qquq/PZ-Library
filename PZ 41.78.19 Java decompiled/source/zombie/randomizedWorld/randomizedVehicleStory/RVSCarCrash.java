// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;

public final class RVSCarCrash extends RandomizedVehicleStoryBase {
    public RVSCarCrash() {
        this.name = "Basic Car Crash";
        this.minZoneWidth = 5;
        this.minZoneHeight = 7;
        this.setChance(25);
    }

    @Override
    public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
        this.callVehicleStorySpawner(zone, chunk, 0.0F);
    }

    @Override
    public boolean initVehicleStorySpawner(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean debug) {
        VehicleStorySpawner vehicleStorySpawner = VehicleStorySpawner.getInstance();
        vehicleStorySpawner.clear();
        float float0 = (float) (Math.PI / 6);
        if (debug) {
            float0 = 0.0F;
        }

        Vector2 vector = IsoDirections.N.ToVector();
        vector.rotate(Rand.Next(-float0, float0));
        vehicleStorySpawner.addElement("vehicle1", 0.0F, 1.0F, vector.getDirection(), 2.0F, 5.0F);
        boolean boolean0 = Rand.NextBool(2);
        vector = boolean0 ? IsoDirections.E.ToVector() : IsoDirections.W.ToVector();
        vector.rotate(Rand.Next(-float0, float0));
        vehicleStorySpawner.addElement("vehicle2", 0.0F, -2.5F, vector.getDirection(), 2.0F, 5.0F);
        vehicleStorySpawner.setParameter("zone", zone);
        vehicleStorySpawner.setParameter("smashed", Rand.NextBool(3));
        vehicleStorySpawner.setParameter("east", boolean0);
        return true;
    }

    @Override
    public void spawnElement(VehicleStorySpawner spawner, VehicleStorySpawner.Element element) {
        IsoGridSquare square = element.square;
        if (square != null) {
            float float0 = element.z;
            IsoMetaGrid.Zone zone = spawner.getParameter("zone", IsoMetaGrid.Zone.class);
            boolean boolean0 = spawner.getParameterBoolean("smashed");
            boolean boolean1 = spawner.getParameterBoolean("east");
            String string0 = element.id;
            switch (string0) {
                case "vehicle1":
                case "vehicle2":
                    BaseVehicle vehicle = this.addVehicle(zone, element.position.x, element.position.y, float0, element.direction, "bad", null, null, null);
                    if (vehicle != null) {
                        if (boolean0) {
                            String string1 = "Front";
                            if ("vehicle2".equals(element.id)) {
                                string1 = boolean1 ? "Right" : "Left";
                            }

                            vehicle = vehicle.setSmashed(string1);
                            vehicle.setBloodIntensity(string1, 1.0F);
                        }

                        if ("vehicle1".equals(element.id) && Rand.Next(10) < 4) {
                            this.addZombiesOnVehicle(Rand.Next(2, 5), null, null, vehicle);
                        }
                    }
            }
        }
    }
}
