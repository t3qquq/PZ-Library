// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;

/**
 * Van with a sewer hole & road cones around it, some construction worker and a foreman + some tools in ground
 */
public final class RVSConstructionSite extends RandomizedVehicleStoryBase {
    private ArrayList<String> tools = null;

    public RVSConstructionSite() {
        this.name = "Construction Site";
        this.minZoneWidth = 6;
        this.minZoneHeight = 6;
        this.setChance(3);
        this.tools = new ArrayList<>();
        this.tools.add("Base.PickAxe");
        this.tools.add("Base.Shovel");
        this.tools.add("Base.Shovel2");
        this.tools.add("Base.Hammer");
        this.tools.add("Base.LeadPipe");
        this.tools.add("Base.PipeWrench");
        this.tools.add("Base.Sledgehammer");
        this.tools.add("Base.Sledgehammer2");
    }

    @Override
    public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
        this.callVehicleStorySpawner(zone, chunk, 0.0F);
    }

    @Override
    public boolean initVehicleStorySpawner(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean debug) {
        VehicleStorySpawner vehicleStorySpawner = VehicleStorySpawner.getInstance();
        vehicleStorySpawner.clear();
        boolean boolean0 = Rand.NextBool(2);
        if (debug) {
            boolean0 = true;
        }

        int int0 = boolean0 ? 1 : -1;
        Vector2 vector = IsoDirections.N.ToVector();
        float float0 = (float) (Math.PI / 6);
        if (debug) {
            float0 = 0.0F;
        }

        vector.rotate(Rand.Next(-float0, float0));
        vehicleStorySpawner.addElement("vehicle1", -int0 * 2.0F, 0.0F, vector.getDirection(), 2.0F, 5.0F);
        float float1 = 0.0F;
        vehicleStorySpawner.addElement("manhole", int0 * 1.5F, 1.5F, float1, 3.0F, 3.0F);
        int int1 = Rand.Next(0, 3);

        for (int int2 = 0; int2 < int1; int2++) {
            float1 = 0.0F;
            vehicleStorySpawner.addElement("tool", int0 * Rand.Next(0.0F, 3.0F), -Rand.Next(0.7F, 2.3F), float1, 1.0F, 1.0F);
        }

        vehicleStorySpawner.setParameter("zone", zone);
        return true;
    }

    @Override
    public void spawnElement(VehicleStorySpawner spawner, VehicleStorySpawner.Element element) {
        IsoGridSquare square0 = element.square;
        if (square0 != null) {
            float float0 = PZMath.max(element.position.x - square0.x, 0.001F);
            float float1 = PZMath.max(element.position.y - square0.y, 0.001F);
            float float2 = 0.0F;
            float float3 = element.z;
            IsoMetaGrid.Zone zone = spawner.getParameter("zone", IsoMetaGrid.Zone.class);
            BaseVehicle vehicle = spawner.getParameter("vehicle1", BaseVehicle.class);
            String string0 = element.id;
            switch (string0) {
                case "manhole":
                    square0.AddTileObject(IsoObject.getNew(square0, "street_decoration_01_15", null, false));
                    IsoGridSquare square1 = square0.getAdjacentSquare(IsoDirections.E);
                    if (square1 != null) {
                        square1.AddTileObject(IsoObject.getNew(square1, "street_decoration_01_26", null, false));
                    }

                    square1 = square0.getAdjacentSquare(IsoDirections.W);
                    if (square1 != null) {
                        square1.AddTileObject(IsoObject.getNew(square1, "street_decoration_01_26", null, false));
                    }

                    square1 = square0.getAdjacentSquare(IsoDirections.S);
                    if (square1 != null) {
                        square1.AddTileObject(IsoObject.getNew(square1, "street_decoration_01_26", null, false));
                    }

                    square1 = square0.getAdjacentSquare(IsoDirections.N);
                    if (square1 != null) {
                        square1.AddTileObject(IsoObject.getNew(square1, "street_decoration_01_26", null, false));
                    }
                    break;
                case "tool":
                    String string1 = this.tools.get(Rand.Next(this.tools.size()));
                    square0.AddWorldInventoryItem(string1, float0, float1, float2);
                    break;
                case "vehicle1":
                    vehicle = this.addVehicle(
                        zone, element.position.x, element.position.y, float3, element.direction, null, "Base.PickUpTruck", null, "ConstructionWorker"
                    );
                    if (vehicle != null) {
                        this.addZombiesOnVehicle(Rand.Next(2, 5), "ConstructionWorker", 0, vehicle);
                        this.addZombiesOnVehicle(1, "Foreman", 0, vehicle);
                        spawner.setParameter("vehicle1", vehicle);
                    }
            }
        }
    }
}
