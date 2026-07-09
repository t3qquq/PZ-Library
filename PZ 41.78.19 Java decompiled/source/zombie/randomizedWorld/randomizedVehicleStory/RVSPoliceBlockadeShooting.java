// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclePart;

/**
 * Police barricading a road, 2 police cars, some zombies police with guns/rifle, dead corpses around
 */
public final class RVSPoliceBlockadeShooting extends RandomizedVehicleStoryBase {
    public RVSPoliceBlockadeShooting() {
        this.name = "Police Blockade Shooting";
        this.minZoneWidth = 8;
        this.minZoneHeight = 8;
        this.setChance(1);
        this.setMaximumDays(30);
    }

    @Override
    public boolean isValid(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean force) {
        boolean boolean0 = super.isValid(zone, chunk, force);
        return !boolean0 ? false : zone.isRectangle();
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

        boolean boolean0 = Rand.NextBool(2);
        if (debug) {
            boolean0 = true;
        }

        IsoDirections directions = Rand.NextBool(2) ? IsoDirections.W : IsoDirections.E;
        Vector2 vector = directions.ToVector();
        vector.rotate(Rand.Next(-float0, float0));
        vehicleStorySpawner.addElement("vehicle1", -float1, float2, vector.getDirection(), 2.0F, 5.0F);
        vector = directions.RotLeft(4).ToVector();
        vector.rotate(Rand.Next(-float0, float0));
        vehicleStorySpawner.addElement("vehicle2", float1, -float2, vector.getDirection(), 2.0F, 5.0F);
        vehicleStorySpawner.addElement(
            "barricade", 0.0F, boolean0 ? -float2 - 2.5F : float2 + 2.5F, IsoDirections.N.ToVector().getDirection(), this.zoneWidth, 1.0F
        );
        int int0 = Rand.Next(7, 15);

        for (int int1 = 0; int1 < int0; int1++) {
            vehicleStorySpawner.addElement(
                "corpse",
                Rand.Next(-this.zoneWidth / 2.0F + 1.0F, this.zoneWidth / 2.0F - 1.0F),
                boolean0 ? Rand.Next(-7, -4) - float2 : Rand.Next(5, 8) + float2,
                IsoDirections.getRandom().ToVector().getDirection(),
                1.0F,
                2.0F
            );
        }

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
        IsoGridSquare square0 = element.square;
        if (square0 != null) {
            float float0 = element.z;
            IsoMetaGrid.Zone zone = spawner.getParameter("zone", IsoMetaGrid.Zone.class);
            String string0 = spawner.getParameterString("script");
            String string1 = element.id;
            switch (string1) {
                case "barricade":
                    if (this.horizontalZone) {
                        int int0 = (int)(element.position.y - element.width / 2.0F);
                        int int1 = (int)(element.position.y + element.width / 2.0F) - 1;
                        int int2 = (int)element.position.x;

                        for (int int3 = int0; int3 <= int1; int3++) {
                            IsoGridSquare square1 = IsoCell.getInstance().getGridSquare(int2, int3, zone.z);
                            if (square1 != null) {
                                if (int3 != int0 && int3 != int1) {
                                    square1.AddTileObject(IsoObject.getNew(square1, "construction_01_9", null, false));
                                } else {
                                    square1.AddTileObject(IsoObject.getNew(square1, "street_decoration_01_26", null, false));
                                }
                            }
                        }
                    } else {
                        int int4 = (int)(element.position.x - element.width / 2.0F);
                        int int5 = (int)(element.position.x + element.width / 2.0F) - 1;
                        int int6 = (int)element.position.y;

                        for (int int7 = int4; int7 <= int5; int7++) {
                            IsoGridSquare square2 = IsoCell.getInstance().getGridSquare(int7, int6, zone.z);
                            if (square2 != null) {
                                if (int7 != int4 && int7 != int5) {
                                    square2.AddTileObject(IsoObject.getNew(square2, "construction_01_8", null, false));
                                } else {
                                    square2.AddTileObject(IsoObject.getNew(square2, "street_decoration_01_26", null, false));
                                }
                            }
                        }
                    }
                    break;
                case "corpse":
                    BaseVehicle vehicle1 = spawner.getParameter("vehicle1", BaseVehicle.class);
                    if (vehicle1 != null) {
                        createRandomDeadBody(element.position.x, element.position.y, zone.z, element.direction, false, 10, 10, null);
                        IsoDirections directions = this.horizontalZone
                            ? (element.position.x < vehicle1.x ? IsoDirections.W : IsoDirections.E)
                            : (element.position.y < vehicle1.y ? IsoDirections.N : IsoDirections.S);
                        float float1 = directions.ToVector().getDirection();
                        this.addTrailOfBlood(element.position.x, element.position.y, element.z, float1, 5);
                    }
                    break;
                case "vehicle1":
                case "vehicle2":
                    BaseVehicle vehicle0 = this.addVehicle(zone, element.position.x, element.position.y, float0, element.direction, null, string0, null, null);
                    if (vehicle0 != null) {
                        spawner.setParameter(element.id, vehicle0);
                        if (Rand.NextBool(3)) {
                            vehicle0.setHeadlightsOn(true);
                            vehicle0.setLightbarLightsMode(2);
                            VehiclePart part = vehicle0.getBattery();
                            if (part != null) {
                                part.setLastUpdated(0.0F);
                            }
                        }

                        String string2 = "PoliceRiot";
                        Integer integer = 0;
                        this.addZombiesOnVehicle(Rand.Next(2, 4), string2, integer, vehicle0);
                    }
            }
        }
    }
}
