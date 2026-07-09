// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.Vector2;
import zombie.iso.objects.IsoDeadBody;
import zombie.vehicles.BaseVehicle;

/**
 * Car crashed with quite some zombies around it
 */
public final class RVSCrashHorde extends RandomizedVehicleStoryBase {
    public RVSCrashHorde() {
        this.name = "Crash Horde";
        this.minZoneWidth = 8;
        this.minZoneHeight = 8;
        this.setChance(1);
        this.setMinimumDays(60);
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
        vehicleStorySpawner.addElement("vehicle1", 0.0F, 0.0F, vector.getDirection(), 2.0F, 5.0F);
        vehicleStorySpawner.setParameter("zone", zone);
        vehicleStorySpawner.setParameter("burnt", Rand.NextBool(5));
        return true;
    }

    @Override
    public void spawnElement(VehicleStorySpawner spawner, VehicleStorySpawner.Element element) {
        IsoGridSquare square = element.square;
        if (square != null) {
            float float0 = element.z;
            IsoMetaGrid.Zone zone = spawner.getParameter("zone", IsoMetaGrid.Zone.class);
            boolean boolean0 = spawner.getParameterBoolean("burnt");
            String string0 = element.id;
            switch (string0) {
                case "vehicle1":
                    BaseVehicle vehicle = this.addVehicleFlipped(
                        zone, element.position.x, element.position.y, float0 + 0.25F, element.direction, boolean0 ? "normalburnt" : "bad", null, null, null
                    );
                    if (vehicle != null) {
                        int int0 = Rand.Next(4);
                        String string1 = null;
                        switch (int0) {
                            case 0:
                                string1 = "Front";
                                break;
                            case 1:
                                string1 = "Rear";
                                break;
                            case 2:
                                string1 = "Left";
                                break;
                            case 3:
                                string1 = "Right";
                        }

                        vehicle = vehicle.setSmashed(string1);
                        vehicle.setBloodIntensity("Front", Rand.Next(0.7F, 1.0F));
                        vehicle.setBloodIntensity("Rear", Rand.Next(0.7F, 1.0F));
                        vehicle.setBloodIntensity("Left", Rand.Next(0.7F, 1.0F));
                        vehicle.setBloodIntensity("Right", Rand.Next(0.7F, 1.0F));
                        ArrayList arrayList = this.addZombiesOnVehicle(Rand.Next(2, 4), null, null, vehicle);
                        if (arrayList != null) {
                            for (int int1 = 0; int1 < arrayList.size(); int1++) {
                                IsoZombie zombie0 = (IsoZombie)arrayList.get(int1);
                                zombie0.upKillCount = false;
                                this.addBloodSplat(zombie0.getSquare(), Rand.Next(10, 20));
                                if (boolean0) {
                                    zombie0.setSkeleton(true);
                                    zombie0.getHumanVisual().setSkinTextureIndex(0);
                                } else {
                                    zombie0.DoCorpseInventory();
                                    if (Rand.NextBool(10)) {
                                        zombie0.setFakeDead(true);
                                        zombie0.bCrawling = true;
                                        zombie0.setCanWalk(false);
                                        zombie0.setCrawlerType(1);
                                    }
                                }

                                new IsoDeadBody(zombie0, false);
                            }

                            this.addZombiesOnVehicle(Rand.Next(12, 20), null, null, vehicle);
                        }
                    }
            }
        }
    }
}
