// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;

public final class RVSTrailerCrash extends RandomizedVehicleStoryBase {
    public RVSTrailerCrash() {
        this.name = "Trailer Crash";
        this.minZoneWidth = 5;
        this.minZoneHeight = 12;
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
        float float1 = 0.0F;
        float float2 = -1.5F;
        vehicleStorySpawner.addElement("vehicle1", float1, float2, vector.getDirection(), 2.0F, 5.0F);
        byte byte0 = 4;
        vehicleStorySpawner.addElement("trailer", float1, float2 + 2.5F + 1.0F + byte0 / 2.0F, vector.getDirection(), 2.0F, byte0);
        boolean boolean0 = Rand.NextBool(2);
        vector = boolean0 ? IsoDirections.E.ToVector() : IsoDirections.W.ToVector();
        vector.rotate(Rand.Next(-float0, float0));
        float float3 = 0.0F;
        float float4 = float2 - 2.5F - 1.0F;
        vehicleStorySpawner.addElement("vehicle2", float3, float4, vector.getDirection(), 2.0F, 5.0F);
        vehicleStorySpawner.setParameter("zone", zone);
        vehicleStorySpawner.setParameter("east", boolean0);
        return true;
    }

    @Override
    public void spawnElement(VehicleStorySpawner spawner, VehicleStorySpawner.Element element) {
        IsoGridSquare square = element.square;
        if (square != null) {
            float float0 = element.z;
            IsoMetaGrid.Zone zone = spawner.getParameter("zone", IsoMetaGrid.Zone.class);
            boolean boolean0 = spawner.getParameterBoolean("east");
            String string0 = element.id;
            switch (string0) {
                case "vehicle1":
                    BaseVehicle vehicle1 = this.addVehicle(
                        zone, element.position.x, element.position.y, float0, element.direction, null, "Base.PickUpVan", null, null
                    );
                    if (vehicle1 != null) {
                        vehicle1 = vehicle1.setSmashed("Front");
                        vehicle1.setBloodIntensity("Front", 1.0F);
                        String string2 = Rand.NextBool(2) ? "Base.Trailer" : "Base.TrailerCover";
                        if (Rand.NextBool(6)) {
                            string2 = "Base.TrailerAdvert";
                        }

                        BaseVehicle vehicle2 = this.addTrailer(vehicle1, zone, square.getChunk(), null, null, string2);
                        if (vehicle2 != null && Rand.NextBool(3)) {
                            vehicle2.setAngles(vehicle2.getAngleX(), Rand.Next(90.0F, 110.0F), vehicle2.getAngleZ());
                        }

                        if (Rand.Next(10) < 4) {
                            this.addZombiesOnVehicle(Rand.Next(2, 5), null, null, vehicle1);
                        }

                        spawner.setParameter("vehicle1", vehicle1);
                    }
                    break;
                case "vehicle2":
                    BaseVehicle vehicle0 = this.addVehicle(zone, element.position.x, element.position.y, float0, element.direction, "bad", null, null, null);
                    if (vehicle0 != null) {
                        String string1 = boolean0 ? "Right" : "Left";
                        vehicle0 = vehicle0.setSmashed(string1);
                        vehicle0.setBloodIntensity(string1, 1.0F);
                        spawner.setParameter("vehicle2", vehicle0);
                    }
            }
        }
    }
}
