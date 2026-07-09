// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;

public final class RVSAmbulanceCrash extends RandomizedVehicleStoryBase {
    public RVSAmbulanceCrash() {
        this.name = "Ambulance Crash";
        this.minZoneWidth = 5;
        this.minZoneHeight = 7;
        this.setChance(5);
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
                case "vehicle1":
                    BaseVehicle vehicle1 = this.addVehicle(
                        zone, element.position.x, element.position.y, float0, element.direction, null, "Base.VanAmbulance", null, null
                    );
                    if (vehicle1 != null) {
                        this.addZombiesOnVehicle(Rand.Next(1, 3), "AmbulanceDriver", null, vehicle1);
                        ArrayList arrayList = this.addZombiesOnVehicle(Rand.Next(1, 3), "HospitalPatient", null, vehicle1);

                        for (int int0 = 0; int0 < arrayList.size(); int0++) {
                            for (int int1 = 0; int1 < 7; int1++) {
                                if (Rand.NextBool(2)) {
                                    ((IsoZombie)arrayList.get(int0)).addVisualBandage(BodyPartType.getRandom(), true);
                                }
                            }
                        }
                    }
                    break;
                case "vehicle2":
                    BaseVehicle vehicle0 = this.addVehicle(zone, element.position.x, element.position.y, float0, element.direction, "bad", null, null, null);
                    if (vehicle0 == null) {
                    }
            }
        }
    }
}
