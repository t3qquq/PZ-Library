// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclePart;

/**
 * Police barricading a road, 2 police cars, some zombies police
 */
public final class RVSPoliceBlockade extends RandomizedVehicleStoryBase {
    public RVSPoliceBlockade() {
        this.name = "Police Blockade";
        this.minZoneWidth = 8;
        this.minZoneHeight = 8;
        this.setChance(3);
        this.setMaximumDays(30);
    }

    @Override
    public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
        this.callVehicleStorySpawner(zone, chunk, 0.0F);
    }

    @Override
    public boolean initVehicleStorySpawner(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean debug) {
        VehicleStorySpawner vehicleStorySpawner = VehicleStorySpawner.getInstance();
        vehicleStorySpawner.clear();
        float float0 = (float) (Math.PI / 18);
        if (debug) {
            float0 = 0.0F;
        }

        float float1 = 1.5F;
        float float2 = 1.0F;
        if (this.zoneWidth >= 10) {
            float1 = 2.5F;
            float2 = 0.0F;
        }

        IsoDirections directions = Rand.NextBool(2) ? IsoDirections.W : IsoDirections.E;
        Vector2 vector = directions.ToVector();
        vector.rotate(Rand.Next(-float0, float0));
        vehicleStorySpawner.addElement("vehicle1", -float1, float2, vector.getDirection(), 2.0F, 5.0F);
        vector = directions.RotLeft(4).ToVector();
        vector.rotate(Rand.Next(-float0, float0));
        vehicleStorySpawner.addElement("vehicle2", float1, -float2, vector.getDirection(), 2.0F, 5.0F);
        String string = "Base.CarLightsPolice";
        if (Rand.NextBool(3)) {
            string = "Base.PickUpVanLightsPolice";
        }

        vehicleStorySpawner.setParameter("zone", zone);
        vehicleStorySpawner.setParameter("script", string);
        return true;
    }

    @Override
    public void spawnElement(VehicleStorySpawner spawner, VehicleStorySpawner.Element element) {
        IsoGridSquare square = element.square;
        if (square != null) {
            float float0 = element.z;
            IsoMetaGrid.Zone zone = spawner.getParameter("zone", IsoMetaGrid.Zone.class);
            String string0 = spawner.getParameterString("script");
            String string1 = element.id;
            switch (string1) {
                case "vehicle1":
                case "vehicle2":
                    BaseVehicle vehicle = this.addVehicle(zone, element.position.x, element.position.y, float0, element.direction, null, string0, null, null);
                    if (vehicle != null) {
                        if (Rand.NextBool(3)) {
                            vehicle.setHeadlightsOn(true);
                            vehicle.setLightbarLightsMode(2);
                            VehiclePart part = vehicle.getBattery();
                            if (part != null) {
                                part.setLastUpdated(0.0F);
                            }
                        }

                        this.addZombiesOnVehicle(Rand.Next(2, 4), "police", null, vehicle);
                    }
            }
        }
    }
}
