// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;

public final class RVSBanditRoad extends RandomizedVehicleStoryBase {
    public RVSBanditRoad() {
        this.name = "Bandits on Road";
        this.minZoneWidth = 7;
        this.minZoneHeight = 9;
        this.setMinimumDays(30);
        this.setChance(3);
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
        vehicleStorySpawner.addElement("vehicle1", 0.0F, 2.0F, vector.getDirection(), 2.0F, 5.0F);
        boolean boolean0 = Rand.NextBool(2);
        vector = boolean0 ? IsoDirections.E.ToVector() : IsoDirections.W.ToVector();
        vector.rotate(Rand.Next(-float0, float0));
        float float1 = 0.0F;
        float float2 = -1.5F;
        vehicleStorySpawner.addElement("vehicle2", float1, float2, vector.getDirection(), 2.0F, 5.0F);
        int int0 = Rand.Next(3, 6);

        for (int int1 = 0; int1 < int0; int1++) {
            float float3 = Rand.Next(float1 - 3.0F, float1 + 3.0F);
            float float4 = Rand.Next(float2 - 3.0F, float2 + 3.0F);
            vehicleStorySpawner.addElement("corpse", float3, float4, Rand.Next(0.0F, (float) (Math.PI * 2)), 1.0F, 2.0F);
        }

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
            switch (string) {
                case "corpse":
                    BaseVehicle vehicle2 = spawner.getParameter("vehicle1", BaseVehicle.class);
                    if (vehicle2 != null) {
                        createRandomDeadBody(element.position.x, element.position.y, element.z, element.direction, false, 6, 0, null);
                        this.addTrailOfBlood(
                            element.position.x,
                            element.position.y,
                            element.z,
                            Vector2.getDirection(element.position.x - vehicle2.x, element.position.y - vehicle2.y),
                            15
                        );
                    }
                    break;
                case "vehicle1":
                    BaseVehicle vehicle1 = this.addVehicle(zone, element.position.x, element.position.y, float0, element.direction, "bad", null, null, null);
                    if (vehicle1 != null) {
                        vehicle1 = vehicle1.setSmashed("Front");
                        this.addZombiesOnVehicle(Rand.Next(3, 6), "Bandit", null, vehicle1);
                        spawner.setParameter("vehicle1", vehicle1);
                    }
                    break;
                case "vehicle2":
                    BaseVehicle vehicle0 = this.addVehicle(zone, element.position.x, element.position.y, float0, element.direction, "bad", null, null, null);
                    if (vehicle0 != null) {
                        this.addZombiesOnVehicle(Rand.Next(3, 5), null, null, vehicle0);
                        spawner.setParameter("vehicle2", vehicle0);
                    }
            }
        }
    }
}
