// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.Vector2;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.BaseVehicle;

/**
 * An utility vehicle (mccoys, fire dept, police, ranger, postal..) with corresponding outfit zeds and sometimes tools
 */
public final class RVSUtilityVehicle extends RandomizedVehicleStoryBase {
    private ArrayList<String> tools = null;
    private ArrayList<String> carpenterTools = null;
    private RVSUtilityVehicle.Params params = new RVSUtilityVehicle.Params();

    public RVSUtilityVehicle() {
        this.name = "Utility Vehicle";
        this.minZoneWidth = 8;
        this.minZoneHeight = 9;
        this.setChance(7);
        this.tools = new ArrayList<>();
        this.tools.add("Base.PickAxe");
        this.tools.add("Base.Shovel");
        this.tools.add("Base.Shovel2");
        this.tools.add("Base.Hammer");
        this.tools.add("Base.LeadPipe");
        this.tools.add("Base.PipeWrench");
        this.tools.add("Base.Sledgehammer");
        this.tools.add("Base.Sledgehammer2");
        this.carpenterTools = new ArrayList<>();
        this.carpenterTools.add("Base.Hammer");
        this.carpenterTools.add("Base.NailsBox");
        this.carpenterTools.add("Base.Plank");
        this.carpenterTools.add("Base.Plank");
        this.carpenterTools.add("Base.Plank");
        this.carpenterTools.add("Base.Screwdriver");
        this.carpenterTools.add("Base.Saw");
        this.carpenterTools.add("Base.Saw");
        this.carpenterTools.add("Base.Woodglue");
    }

    @Override
    public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
        this.callVehicleStorySpawner(zone, chunk, 0.0F);
    }

    public void doUtilityVehicle(
        IsoMetaGrid.Zone zone,
        IsoChunk chunk,
        String zoneName,
        String scriptName,
        String outfits,
        Integer femaleChance,
        String vehicleDistrib,
        ArrayList<String> items,
        int nbrOfItem,
        boolean addTrailer
    ) {
        this.params.zoneName = zoneName;
        this.params.scriptName = scriptName;
        this.params.outfits = outfits;
        this.params.femaleChance = femaleChance;
        this.params.vehicleDistrib = vehicleDistrib;
        this.params.items = items;
        this.params.nbrOfItem = nbrOfItem;
        this.params.addTrailer = addTrailer;
    }

    @Override
    public boolean initVehicleStorySpawner(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean debug) {
        int int0 = Rand.Next(0, 7);
        switch (int0) {
            case 0:
                this.doUtilityVehicle(zone, chunk, null, "Base.PickUpTruck", "ConstructionWorker", 0, "ConstructionWorker", this.tools, Rand.Next(0, 3), true);
                break;
            case 1:
                this.doUtilityVehicle(zone, chunk, "police", null, "Police", null, null, null, 0, false);
                break;
            case 2:
                this.doUtilityVehicle(zone, chunk, "fire", null, "Fireman", null, null, null, 0, false);
                break;
            case 3:
                this.doUtilityVehicle(zone, chunk, "ranger", null, "Ranger", null, null, null, 0, true);
                break;
            case 4:
                this.doUtilityVehicle(zone, chunk, "mccoy", null, "McCoys", 0, "Carpenter", this.carpenterTools, Rand.Next(2, 6), true);
                break;
            case 5:
                this.doUtilityVehicle(zone, chunk, "postal", null, "Postal", null, null, null, 0, false);
                break;
            case 6:
                this.doUtilityVehicle(zone, chunk, "fossoil", null, "Fossoil", null, null, null, 0, false);
        }

        VehicleStorySpawner vehicleStorySpawner = VehicleStorySpawner.getInstance();
        vehicleStorySpawner.clear();
        Vector2 vector = IsoDirections.N.ToVector();
        float float0 = (float) (Math.PI / 6);
        if (debug) {
            float0 = 0.0F;
        }

        vector.rotate(Rand.Next(-float0, float0));
        float float1 = -2.0F;
        byte byte0 = 5;
        vehicleStorySpawner.addElement("vehicle1", 0.0F, float1, vector.getDirection(), 2.0F, byte0);
        if (this.params.addTrailer && Rand.NextBool(7)) {
            byte byte1 = 3;
            vehicleStorySpawner.addElement("trailer", 0.0F, float1 + byte0 / 2.0F + 1.0F + byte1 / 2.0F, vector.getDirection(), 2.0F, byte1);
        }

        if (this.params.items != null) {
            for (int int1 = 0; int1 < this.params.nbrOfItem; int1++) {
                vehicleStorySpawner.addElement("tool", Rand.Next(-3.5F, 3.5F), Rand.Next(-3.5F, 3.5F), 0.0F, 1.0F, 1.0F);
            }
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
            BaseVehicle vehicle = spawner.getParameter("vehicle1", BaseVehicle.class);
            String string = element.id;
            switch (string) {
                case "tool":
                    if (vehicle != null) {
                        float float1 = PZMath.max(element.position.x - square.x, 0.001F);
                        float float2 = PZMath.max(element.position.y - square.y, 0.001F);
                        float float3 = 0.0F;
                        square.AddWorldInventoryItem(PZArrayUtil.pickRandom(this.params.items), float1, float2, float3);
                    }
                    break;
                case "trailer":
                    if (vehicle != null) {
                        this.addTrailer(
                            vehicle,
                            zone,
                            square.getChunk(),
                            this.params.zoneName,
                            this.params.vehicleDistrib,
                            Rand.NextBool(1) ? "Base.Trailer" : "Base.TrailerCover"
                        );
                    }
                    break;
                case "vehicle1":
                    vehicle = this.addVehicle(
                        zone,
                        element.position.x,
                        element.position.y,
                        float0,
                        element.direction,
                        this.params.zoneName,
                        this.params.scriptName,
                        null,
                        this.params.vehicleDistrib
                    );
                    if (vehicle != null) {
                        this.addZombiesOnVehicle(Rand.Next(2, 5), this.params.outfits, this.params.femaleChance, vehicle);
                    }
            }
        }
    }

    private static final class Params {
        String zoneName;
        String scriptName;
        String outfits;
        Integer femaleChance;
        String vehicleDistrib;
        ArrayList<String> items;
        int nbrOfItem;
        boolean addTrailer;
    }
}
