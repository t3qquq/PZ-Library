// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclePart;

/**
 * Good car with a couple changing its tire
 */
public final class RVSChangingTire extends RandomizedVehicleStoryBase {
    public RVSChangingTire() {
        this.name = "Changing Tire";
        this.minZoneWidth = 5;
        this.minZoneHeight = 5;
        this.setChance(3);
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
        boolean boolean0 = Rand.NextBool(2);
        if (debug) {
            boolean0 = true;
        }

        int int0 = boolean0 ? 1 : -1;
        Vector2 vector = IsoDirections.N.ToVector();
        vehicleStorySpawner.addElement("vehicle1", int0 * -1.5F, 0.0F, vector.getDirection(), 2.0F, 5.0F);
        vehicleStorySpawner.addElement("tire1", int0 * 0.0F, 0.0F, 0.0F, 1.0F, 1.0F);
        vehicleStorySpawner.addElement("tool1", int0 * 0.8F, -0.2F, 0.0F, 1.0F, 1.0F);
        vehicleStorySpawner.addElement("tool2", int0 * 1.2F, 0.2F, 0.0F, 1.0F, 1.0F);
        vehicleStorySpawner.addElement("tire2", int0 * 2.0F, 0.0F, 0.0F, 1.0F, 1.0F);
        vehicleStorySpawner.setParameter("zone", zone);
        vehicleStorySpawner.setParameter("removeRightWheel", boolean0);
        return true;
    }

    @Override
    public void spawnElement(VehicleStorySpawner spawner, VehicleStorySpawner.Element element) {
        IsoGridSquare square = element.square;
        if (square != null) {
            float float0 = PZMath.max(element.position.x - square.x, 0.001F);
            float float1 = PZMath.max(element.position.y - square.y, 0.001F);
            float float2 = 0.0F;
            float float3 = element.z;
            IsoMetaGrid.Zone zone = spawner.getParameter("zone", IsoMetaGrid.Zone.class);
            boolean boolean0 = spawner.getParameterBoolean("removeRightWheel");
            BaseVehicle vehicle = spawner.getParameter("vehicle1", BaseVehicle.class);
            String string = element.id;
            switch (string) {
                case "tire1":
                    if (vehicle != null) {
                        InventoryItem item1 = square.AddWorldInventoryItem("Base.ModernTire" + vehicle.getScript().getMechanicType(), float0, float1, float2);
                        if (item1 != null) {
                            item1.setItemCapacity(item1.getMaxCapacity());
                        }

                        this.addBloodSplat(square, Rand.Next(10, 20));
                    }
                    break;
                case "tire2":
                    if (vehicle != null) {
                        InventoryItem item0 = square.AddWorldInventoryItem("Base.OldTire" + vehicle.getScript().getMechanicType(), float0, float1, float2);
                        if (item0 != null) {
                            item0.setCondition(0);
                        }
                    }
                    break;
                case "tool1":
                    square.AddWorldInventoryItem("Base.LugWrench", float0, float1, float2);
                    break;
                case "tool2":
                    square.AddWorldInventoryItem("Base.Jack", float0, float1, float2);
                    break;
                case "vehicle1":
                    vehicle = this.addVehicle(zone, element.position.x, element.position.y, float3, element.direction, "good", null, null, null);
                    if (vehicle != null) {
                        vehicle.setGeneralPartCondition(0.7F, 40.0F);
                        vehicle.setRust(0.0F);
                        VehiclePart part = vehicle.getPartById(boolean0 ? "TireRearRight" : "TireRearLeft");
                        vehicle.setTireRemoved(part.getWheelIndex(), true);
                        part.setModelVisible("InflatedTirePlusWheel", false);
                        part.setInventoryItem(null);
                        this.addZombiesOnVehicle(2, null, null, vehicle);
                        spawner.setParameter("vehicle1", vehicle);
                    }
            }
        }
    }
}
