// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld;

import java.util.ArrayList;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.VirtualZombieManager;
import zombie.ZombieSpawnRecorder;
import zombie.Lua.MapObjects;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorFactory;
import zombie.core.Rand;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.randomizedWorld.randomizedBuilding.RandomizedBuildingBase;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleType;
import zombie.vehicles.VehiclesDB2;

public class RandomizedWorldBase {
    private static final Vector2 s_tempVector2 = new Vector2();
    protected int minimumDays = 0;
    protected int maximumDays = 0;
    protected int minimumRooms = 0;
    protected boolean unique = false;
    private boolean rvsVehicleKeyAddedToZombie = false;
    protected String name = null;
    protected String debugLine = "";

    public BaseVehicle addVehicle(IsoMetaGrid.Zone zone, IsoGridSquare sq, IsoChunk chunk, String zoneName, String scriptName, IsoDirections dir) {
        return this.addVehicle(zone, sq, chunk, zoneName, scriptName, null, dir, null);
    }

    public BaseVehicle addVehicleFlipped(
        IsoMetaGrid.Zone zone,
        IsoGridSquare sq,
        IsoChunk chunk,
        String zoneName,
        String scriptName,
        Integer skinIndex,
        IsoDirections dir,
        String specificContainer
    ) {
        if (sq == null) {
            return null;
        } else {
            if (dir == null) {
                dir = IsoDirections.getRandom();
            }

            Vector2 vector = dir.ToVector();
            return this.addVehicleFlipped(zone, sq.x, sq.y, sq.z, vector.getDirection(), zoneName, scriptName, skinIndex, specificContainer);
        }
    }

    public BaseVehicle addVehicleFlipped(
        IsoMetaGrid.Zone zone,
        float vehicleX,
        float vehicleY,
        float vehicleZ,
        float direction,
        String zoneName,
        String scriptName,
        Integer skinIndex,
        String specificContainer
    ) {
        if (StringUtils.isNullOrEmpty(zoneName)) {
            zoneName = "junkyard";
        }

        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)vehicleX, (double)vehicleY, (double)vehicleZ);
        if (square == null) {
            return null;
        } else {
            IsoChunk chunk = square.getChunk();
            IsoDirections directions = IsoDirections.fromAngle(direction);
            BaseVehicle vehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
            vehicle.specificDistributionId = specificContainer;
            VehicleType vehicleType = VehicleType.getRandomVehicleType(zoneName, false);
            if (!StringUtils.isNullOrEmpty(scriptName)) {
                vehicle.setScriptName(scriptName);
                vehicle.setScript();
                if (skinIndex != null) {
                    vehicle.setSkinIndex(skinIndex);
                }
            } else {
                if (vehicleType == null) {
                    return null;
                }

                vehicle.setVehicleType(vehicleType.name);
                if (!chunk.RandomizeModel(vehicle, zone, zoneName, vehicleType)) {
                    return null;
                }
            }

            if (vehicleType.isSpecialCar) {
                vehicle.setDoColor(false);
            }

            vehicle.setDir(directions);
            float float0 = direction - (float) (Math.PI / 2);

            while (float0 > Math.PI * 2) {
                float0 = (float)(float0 - (Math.PI * 2));
            }

            vehicle.savedRot.rotationXYZ(0.0F, -float0, (float) Math.PI);
            vehicle.jniTransform.setRotation(vehicle.savedRot);
            vehicle.setX(vehicleX);
            vehicle.setY(vehicleY);
            vehicle.setZ(vehicleZ);
            if (IsoChunk.doSpawnedVehiclesInInvalidPosition(vehicle)) {
                vehicle.setSquare(square);
                square.chunk.vehicles.add(vehicle);
                vehicle.chunk = square.chunk;
                vehicle.addToWorld();
                VehiclesDB2.instance.addVehicle(vehicle);
            }

            vehicle.setGeneralPartCondition(0.2F, 70.0F);
            vehicle.rust = Rand.Next(100) < 70 ? 1.0F : 0.0F;
            return vehicle;
        }
    }

    public BaseVehicle addVehicle(
        IsoMetaGrid.Zone zone,
        IsoGridSquare sq,
        IsoChunk chunk,
        String zoneName,
        String scriptName,
        Integer skinIndex,
        IsoDirections dir,
        String specificContainer
    ) {
        if (sq == null) {
            return null;
        } else {
            if (dir == null) {
                dir = IsoDirections.getRandom();
            }

            Vector2 vector = dir.ToVector();
            vector.rotate(Rand.Next(-0.5F, 0.5F));
            return this.addVehicle(zone, sq.x, sq.y, sq.z, vector.getDirection(), zoneName, scriptName, skinIndex, specificContainer);
        }
    }

    public BaseVehicle addVehicle(
        IsoMetaGrid.Zone zone,
        float vehicleX,
        float vehicleY,
        float vehicleZ,
        float direction,
        String zoneName,
        String scriptName,
        Integer skinIndex,
        String specificContainer
    ) {
        if (StringUtils.isNullOrEmpty(zoneName)) {
            zoneName = "junkyard";
        }

        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)vehicleX, (double)vehicleY, (double)vehicleZ);
        if (square == null) {
            return null;
        } else {
            IsoChunk chunk = square.getChunk();
            IsoDirections directions = IsoDirections.fromAngle(direction);
            BaseVehicle vehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
            vehicle.specificDistributionId = specificContainer;
            VehicleType vehicleType = VehicleType.getRandomVehicleType(zoneName, false);
            if (!StringUtils.isNullOrEmpty(scriptName)) {
                vehicle.setScriptName(scriptName);
                vehicle.setScript();
                if (skinIndex != null) {
                    vehicle.setSkinIndex(skinIndex);
                }
            } else {
                if (vehicleType == null) {
                    return null;
                }

                vehicle.setVehicleType(vehicleType.name);
                if (!chunk.RandomizeModel(vehicle, zone, zoneName, vehicleType)) {
                    return null;
                }
            }

            if (vehicleType.isSpecialCar) {
                vehicle.setDoColor(false);
            }

            vehicle.setDir(directions);
            float float0 = direction - (float) (Math.PI / 2);

            while (float0 > Math.PI * 2) {
                float0 = (float)(float0 - (Math.PI * 2));
            }

            vehicle.savedRot.setAngleAxis(-float0, 0.0F, 1.0F, 0.0F);
            vehicle.jniTransform.setRotation(vehicle.savedRot);
            vehicle.setX(vehicleX);
            vehicle.setY(vehicleY);
            vehicle.setZ(vehicleZ);
            if (IsoChunk.doSpawnedVehiclesInInvalidPosition(vehicle)) {
                vehicle.setSquare(square);
                square.chunk.vehicles.add(vehicle);
                vehicle.chunk = square.chunk;
                vehicle.addToWorld();
                VehiclesDB2.instance.addVehicle(vehicle);
            }

            vehicle.setGeneralPartCondition(0.2F, 70.0F);
            vehicle.rust = Rand.Next(100) < 70 ? 1.0F : 0.0F;
            return vehicle;
        }
    }

    public static void removeAllVehiclesOnZone(IsoMetaGrid.Zone zone) {
        for (int int0 = zone.x; int0 < zone.x + zone.w; int0++) {
            for (int int1 = zone.y; int1 < zone.y + zone.h; int1++) {
                IsoGridSquare square = IsoCell.getInstance().getGridSquare(int0, int1, 0);
                if (square != null) {
                    BaseVehicle vehicle = square.getVehicleContainer();
                    if (vehicle != null) {
                        vehicle.permanentlyRemove();
                    }
                }
            }
        }
    }

    /**
     * Add zombies near the vehicles, around a 4x4 square around it, avoiding being  ON the vehicle & randomizing square for each zombies
     */
    public ArrayList<IsoZombie> addZombiesOnVehicle(int totalZombies, String outfit, Integer femaleChance, BaseVehicle vehicle) {
        ArrayList arrayList = new ArrayList();
        if (vehicle == null) {
            return arrayList;
        } else {
            int int0 = 100;
            IsoGridSquare square0 = vehicle.getSquare();
            if (square0 != null && square0.getCell() != null) {
                for (; totalZombies > 0; int0 = 100) {
                    while (int0 > 0) {
                        IsoGridSquare square1 = square0.getCell()
                            .getGridSquare(Rand.Next(square0.x - 4, square0.x + 4), Rand.Next(square0.y - 4, square0.y + 4), square0.z);
                        if (square1 != null && square1.getVehicleContainer() == null) {
                            totalZombies--;
                            arrayList.addAll(this.addZombiesOnSquare(1, outfit, femaleChance, square1));
                            break;
                        }

                        int0--;
                    }
                }

                if (!this.rvsVehicleKeyAddedToZombie && !arrayList.isEmpty()) {
                    IsoZombie zombie0 = (IsoZombie)arrayList.get(Rand.Next(0, arrayList.size()));
                    zombie0.addItemToSpawnAtDeath(vehicle.createVehicleKey());
                    this.rvsVehicleKeyAddedToZombie = true;
                }

                return arrayList;
            } else {
                return arrayList;
            }
        }
    }

    public static IsoDeadBody createRandomDeadBody(RoomDef room, int blood) {
        if (IsoWorld.getZombiesDisabled()) {
            return null;
        } else if (room == null) {
            return null;
        } else {
            IsoGridSquare square = getRandomSquareForCorpse(room);
            return square == null ? null : createRandomDeadBody(square, null, blood, 0, null);
        }
    }

    public ArrayList<IsoZombie> addZombiesOnSquare(int totalZombies, String outfit, Integer femaleChance, IsoGridSquare square) {
        ArrayList arrayList = new ArrayList();
        if (IsoWorld.getZombiesDisabled()) {
            return arrayList;
        } else if (square == null) {
            return arrayList;
        } else {
            for (int int0 = 0; int0 < totalZombies; int0++) {
                VirtualZombieManager.instance.choices.clear();
                VirtualZombieManager.instance.choices.add(square);
                IsoZombie zombie0 = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.getRandom().index(), false);
                if (zombie0 != null) {
                    if (femaleChance != null) {
                        zombie0.setFemaleEtc(Rand.Next(100) < femaleChance);
                    }

                    if (outfit != null) {
                        zombie0.dressInPersistentOutfit(outfit);
                        zombie0.bDressInRandomOutfit = false;
                    } else {
                        zombie0.dressInRandomOutfit();
                        zombie0.bDressInRandomOutfit = false;
                    }

                    arrayList.add(zombie0);
                }
            }

            ZombieSpawnRecorder.instance.record(arrayList, this.getClass().getSimpleName());
            return arrayList;
        }
    }

    public static IsoDeadBody createRandomDeadBody(int x, int y, int z, IsoDirections dir, int blood) {
        return createRandomDeadBody(x, y, z, dir, blood, 0);
    }

    public static IsoDeadBody createRandomDeadBody(int x, int y, int z, IsoDirections dir, int blood, int crawlerChance) {
        IsoGridSquare square = IsoCell.getInstance().getGridSquare(x, y, z);
        return createRandomDeadBody(square, dir, blood, crawlerChance, null);
    }

    public static IsoDeadBody createRandomDeadBody(IsoGridSquare sq, IsoDirections dir, int blood, int crawlerChance, String outfit) {
        if (sq == null) {
            return null;
        } else {
            boolean boolean0 = dir == null;
            if (boolean0) {
                dir = IsoDirections.getRandom();
            }

            return createRandomDeadBody(
                sq.x + Rand.Next(0.05F, 0.95F), sq.y + Rand.Next(0.05F, 0.95F), sq.z, dir.ToVector().getDirection(), boolean0, blood, crawlerChance, outfit
            );
        }
    }

    public static IsoDeadBody createRandomDeadBody(
        float x, float y, float z, float direction, boolean alignToSquare, int blood, int crawlerChance, String outfit
    ) {
        if (IsoWorld.getZombiesDisabled()) {
            return null;
        } else {
            IsoGridSquare square = IsoCell.getInstance().getGridSquare((double)x, (double)y, (double)z);
            if (square == null) {
                return null;
            } else {
                IsoDirections directions = IsoDirections.fromAngle(direction);
                VirtualZombieManager.instance.choices.clear();
                VirtualZombieManager.instance.choices.add(square);
                IsoZombie zombie0 = VirtualZombieManager.instance.createRealZombieAlways(directions.index(), false);
                if (zombie0 == null) {
                    return null;
                } else {
                    if (outfit != null) {
                        zombie0.dressInPersistentOutfit(outfit);
                        zombie0.bDressInRandomOutfit = false;
                    } else {
                        zombie0.dressInRandomOutfit();
                    }

                    if (Rand.Next(100) < crawlerChance) {
                        zombie0.setFakeDead(true);
                        zombie0.setCrawler(true);
                        zombie0.setCanWalk(false);
                        zombie0.setCrawlerType(1);
                    } else {
                        zombie0.setFakeDead(false);
                        zombie0.setHealth(0.0F);
                    }

                    zombie0.upKillCount = false;
                    zombie0.getHumanVisual().zombieRotStage = ((HumanVisual)zombie0.getVisual()).pickRandomZombieRotStage();

                    for (int int0 = 0; int0 < blood; int0++) {
                        zombie0.addBlood(null, false, true, true);
                    }

                    zombie0.DoCorpseInventory();
                    zombie0.setX(x);
                    zombie0.setY(y);
                    zombie0.getForwardDirection().setLengthAndDirection(direction, 1.0F);
                    if (alignToSquare || zombie0.isSkeleton()) {
                        alignCorpseToSquare(zombie0, square);
                    }

                    IsoDeadBody deadBody = new IsoDeadBody(zombie0, true);
                    if (!deadBody.isFakeDead() && !deadBody.isSkeleton() && Rand.Next(20) == 0) {
                        deadBody.setFakeDead(true);
                        if (Rand.Next(5) == 0) {
                            deadBody.setCrawling(true);
                        }
                    }

                    return deadBody;
                }
            }
        }
    }

    public void addTraitOfBlood(IsoDirections dir, int time, int x, int y, int z) {
        for (int int0 = 0; int0 < time; int0++) {
            float float0 = 0.0F;
            float float1 = 0.0F;
            if (dir == IsoDirections.S) {
                float1 = Rand.Next(-2.0F, 0.5F);
            }

            if (dir == IsoDirections.N) {
                float1 = Rand.Next(-0.5F, 2.0F);
            }

            if (dir == IsoDirections.E) {
                float0 = Rand.Next(-2.0F, 0.5F);
            }

            if (dir == IsoDirections.W) {
                float0 = Rand.Next(-0.5F, 2.0F);
            }

            new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, IsoCell.getInstance(), x, y, z + 0.2F, float0, float1);
        }
    }

    public void addTrailOfBlood(float x, float y, float z, float direction, int count) {
        Vector2 vector = s_tempVector2;

        for (int int0 = 0; int0 < count; int0++) {
            float float0 = Rand.Next(-0.5F, 2.0F);
            if (float0 < 0.0F) {
                vector.setLengthAndDirection(direction + (float) Math.PI, -float0);
            } else {
                vector.setLengthAndDirection(direction, float0);
            }

            new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, IsoCell.getInstance(), x, y, z + 0.2F, vector.x, vector.y);
        }
    }

    public void addBloodSplat(IsoGridSquare sq, int nbr) {
        for (int int0 = 0; int0 < nbr; int0++) {
            sq.getChunk().addBloodSplat(sq.x + Rand.Next(-0.5F, 0.5F), sq.y + Rand.Next(-0.5F, 0.5F), sq.z, Rand.Next(8));
        }
    }

    public void setAttachedItem(IsoZombie zombie, String location, String item, String ensureItem) {
        InventoryItem _item = InventoryItemFactory.CreateItem(item);
        if (_item != null) {
            _item.setCondition(Rand.Next(Math.max(2, _item.getConditionMax() - 5), _item.getConditionMax()));
            if (_item instanceof HandWeapon) {
                ((HandWeapon)_item).randomizeBullets();
            }

            zombie.setAttachedItem(location, _item);
            if (!StringUtils.isNullOrEmpty(ensureItem)) {
                zombie.addItemToSpawnAtDeath(InventoryItemFactory.CreateItem(ensureItem));
            }
        }
    }

    public static IsoGameCharacter createRandomZombie(RoomDef room) {
        IsoGridSquare square = getRandomSpawnSquare(room);
        return createRandomZombie(square.getX(), square.getY(), square.getZ());
    }

    public static IsoGameCharacter createRandomZombieForCorpse(RoomDef room) {
        IsoGridSquare square = getRandomSquareForCorpse(room);
        if (square == null) {
            return null;
        } else {
            IsoGameCharacter character = createRandomZombie(square.getX(), square.getY(), square.getZ());
            if (character != null) {
                alignCorpseToSquare(character, square);
            }

            return character;
        }
    }

    public static IsoDeadBody createBodyFromZombie(IsoGameCharacter chr) {
        if (IsoWorld.getZombiesDisabled()) {
            return null;
        } else {
            for (int int0 = 0; int0 < 6; int0++) {
                chr.splatBlood(Rand.Next(1, 4), 0.3F);
            }

            return new IsoDeadBody(chr, true);
        }
    }

    public static IsoGameCharacter createRandomZombie(int x, int y, int z) {
        RandomizedBuildingBase.HumanCorpse humanCorpse = new RandomizedBuildingBase.HumanCorpse(IsoWorld.instance.getCell(), x, y, z);
        humanCorpse.setDescriptor(SurvivorFactory.CreateSurvivor());
        humanCorpse.setFemale(humanCorpse.getDescriptor().isFemale());
        humanCorpse.setDir(IsoDirections.fromIndex(Rand.Next(8)));
        humanCorpse.initWornItems("Human");
        humanCorpse.initAttachedItems("Human");
        Outfit outfit = humanCorpse.getRandomDefaultOutfit();
        humanCorpse.dressInNamedOutfit(outfit.m_Name);
        humanCorpse.initSpritePartsEmpty();
        humanCorpse.Dressup(humanCorpse.getDescriptor());
        return humanCorpse;
    }

    private static boolean isSquareClear(IsoGridSquare square) {
        return square != null
            && canSpawnAt(square)
            && !square.HasStairs()
            && !square.HasTree()
            && !square.getProperties().Is(IsoFlagType.bed)
            && !square.getProperties().Is(IsoFlagType.waterPiped);
    }

    private static boolean isSquareClear(IsoGridSquare square1, IsoDirections directions) {
        IsoGridSquare square0 = square1.getAdjacentSquare(directions);
        return isSquareClear(square0) && !square1.isSomethingTo(square0) && square1.getRoomID() == square0.getRoomID();
    }

    public static boolean is1x2AreaClear(IsoGridSquare square) {
        return isSquareClear(square) && isSquareClear(square, IsoDirections.N);
    }

    public static boolean is2x1AreaClear(IsoGridSquare square) {
        return isSquareClear(square) && isSquareClear(square, IsoDirections.W);
    }

    public static boolean is2x1or1x2AreaClear(IsoGridSquare square) {
        return isSquareClear(square) && (isSquareClear(square, IsoDirections.W) || isSquareClear(square, IsoDirections.N));
    }

    public static boolean is2x2AreaClear(IsoGridSquare square) {
        return isSquareClear(square)
            && isSquareClear(square, IsoDirections.N)
            && isSquareClear(square, IsoDirections.W)
            && isSquareClear(square, IsoDirections.NW);
    }

    public static void alignCorpseToSquare(IsoGameCharacter chr, IsoGridSquare square) {
        int int0 = square.x;
        int int1 = square.y;
        IsoDirections directions = IsoDirections.fromIndex(Rand.Next(8));
        boolean boolean0 = is1x2AreaClear(square);
        boolean boolean1 = is2x1AreaClear(square);
        if (boolean0 && boolean1) {
            boolean0 = Rand.Next(2) == 0;
            boolean1 = !boolean0;
        }

        if (is2x2AreaClear(square)) {
            chr.setX(int0);
            chr.setY(int1);
        } else if (boolean0) {
            chr.setX(int0 + 0.5F);
            chr.setY(int1);
            directions = Rand.Next(2) == 0 ? IsoDirections.N : IsoDirections.S;
        } else if (boolean1) {
            chr.setX(int0);
            chr.setY(int1 + 0.5F);
            directions = Rand.Next(2) == 0 ? IsoDirections.W : IsoDirections.E;
        } else if (is1x2AreaClear(square.getAdjacentSquare(IsoDirections.S))) {
            chr.setX(int0 + 0.5F);
            chr.setY(int1 + 0.99F);
            directions = Rand.Next(2) == 0 ? IsoDirections.N : IsoDirections.S;
        } else if (is2x1AreaClear(square.getAdjacentSquare(IsoDirections.E))) {
            chr.setX(int0 + 0.99F);
            chr.setY(int1 + 0.5F);
            directions = Rand.Next(2) == 0 ? IsoDirections.W : IsoDirections.E;
        }

        chr.setDir(directions);
        chr.lx = chr.nx = chr.x;
        chr.ly = chr.ny = chr.y;
        chr.setScriptnx(chr.x);
        chr.setScriptny(chr.y);
    }

    /**
     * Get a random room in the building
     */
    public RoomDef getRandomRoom(BuildingDef bDef, int minArea) {
        RoomDef roomDef = bDef.getRooms().get(Rand.Next(0, bDef.getRooms().size()));
        if (minArea > 0 && roomDef.area >= minArea) {
            return roomDef;
        } else {
            int int0 = 0;

            while (int0 <= 20) {
                int0++;
                roomDef = bDef.getRooms().get(Rand.Next(0, bDef.getRooms().size()));
                if (roomDef.area >= minArea) {
                    return roomDef;
                }
            }

            return roomDef;
        }
    }

    /**
     * Return the wanted room
     */
    public RoomDef getRoom(BuildingDef bDef, String roomName) {
        for (int int0 = 0; int0 < bDef.rooms.size(); int0++) {
            RoomDef roomDef = bDef.rooms.get(int0);
            if (roomDef.getName().equalsIgnoreCase(roomName)) {
                return roomDef;
            }
        }

        return null;
    }

    /**
     * Get either the living room or kitchen (in this order)
     */
    public RoomDef getLivingRoomOrKitchen(BuildingDef bDef) {
        RoomDef roomDef = this.getRoom(bDef, "livingroom");
        if (roomDef == null) {
            roomDef = this.getRoom(bDef, "kitchen");
        }

        return roomDef;
    }

    private static boolean canSpawnAt(IsoGridSquare square) {
        if (square == null) {
            return false;
        } else {
            return square.HasStairs() ? false : VirtualZombieManager.instance.canSpawnAt(square.x, square.y, square.z);
        }
    }

    public static IsoGridSquare getRandomSpawnSquare(RoomDef roomDef) {
        return roomDef == null ? null : roomDef.getRandomSquare(RandomizedWorldBase::canSpawnAt);
    }

    public static IsoGridSquare getRandomSquareForCorpse(RoomDef roomDef) {
        IsoGridSquare square0 = roomDef.getRandomSquare(RandomizedWorldBase::is2x2AreaClear);
        IsoGridSquare square1 = roomDef.getRandomSquare(RandomizedWorldBase::is2x1or1x2AreaClear);
        if (square0 == null || square1 != null && Rand.Next(4) == 0) {
            square0 = square1;
        }

        return square0;
    }

    public BaseVehicle spawnCarOnNearestNav(String carName, BuildingDef def) {
        IsoGridSquare square0 = null;
        int int0 = (def.x + def.x2) / 2;
        int int1 = (def.y + def.y2) / 2;

        for (int int2 = int0; int2 < int0 + 20; int2++) {
            IsoGridSquare square1 = IsoCell.getInstance().getGridSquare(int2, int1, 0);
            if (square1 != null && "Nav".equals(square1.getZoneType())) {
                square0 = square1;
                break;
            }
        }

        if (square0 != null) {
            return this.spawnCar(carName, square0);
        } else {
            for (int int3 = int0; int3 > int0 - 20; int3--) {
                IsoGridSquare square2 = IsoCell.getInstance().getGridSquare(int3, int1, 0);
                if (square2 != null && "Nav".equals(square2.getZoneType())) {
                    square0 = square2;
                    break;
                }
            }

            if (square0 != null) {
                return this.spawnCar(carName, square0);
            } else {
                for (int int4 = int1; int4 < int1 + 20; int4++) {
                    IsoGridSquare square3 = IsoCell.getInstance().getGridSquare(int0, int4, 0);
                    if (square3 != null && "Nav".equals(square3.getZoneType())) {
                        square0 = square3;
                        break;
                    }
                }

                if (square0 != null) {
                    return this.spawnCar(carName, square0);
                } else {
                    for (int int5 = int1; int5 > int1 - 20; int5--) {
                        IsoGridSquare square4 = IsoCell.getInstance().getGridSquare(int0, int5, 0);
                        if (square4 != null && "Nav".equals(square4.getZoneType())) {
                            square0 = square4;
                            break;
                        }
                    }

                    return square0 != null ? this.spawnCar(carName, square0) : null;
                }
            }
        }
    }

    private BaseVehicle spawnCar(String string, IsoGridSquare square) {
        BaseVehicle vehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
        vehicle.setScriptName(string);
        vehicle.setX(square.x + 0.5F);
        vehicle.setY(square.y + 0.5F);
        vehicle.setZ(0.0F);
        vehicle.savedRot.setAngleAxis(Rand.Next(0.0F, (float) (Math.PI * 2)), 0.0F, 1.0F, 0.0F);
        vehicle.jniTransform.setRotation(vehicle.savedRot);
        if (IsoChunk.doSpawnedVehiclesInInvalidPosition(vehicle)) {
            vehicle.keySpawned = 1;
            vehicle.setSquare(square);
            vehicle.square.chunk.vehicles.add(vehicle);
            vehicle.chunk = vehicle.square.chunk;
            vehicle.addToWorld();
            VehiclesDB2.instance.addVehicle(vehicle);
        }

        vehicle.setGeneralPartCondition(0.3F, 70.0F);
        return vehicle;
    }

    public InventoryItem addItemOnGround(IsoGridSquare square, String type) {
        return square != null && !StringUtils.isNullOrWhitespace(type)
            ? square.AddWorldInventoryItem(type, Rand.Next(0.2F, 0.8F), Rand.Next(0.2F, 0.8F), 0.0F)
            : null;
    }

    public InventoryItem addItemOnGround(IsoGridSquare square, InventoryItem item) {
        return square != null && item != null ? square.AddWorldInventoryItem(item, Rand.Next(0.2F, 0.8F), Rand.Next(0.2F, 0.8F), 0.0F) : null;
    }

    public void addRandomItemsOnGround(RoomDef room, String type, int count) {
        for (int int0 = 0; int0 < count; int0++) {
            IsoGridSquare square = getRandomSpawnSquare(room);
            this.addItemOnGround(square, type);
        }
    }

    public void addRandomItemsOnGround(RoomDef room, ArrayList<String> types, int count) {
        for (int int0 = 0; int0 < count; int0++) {
            IsoGridSquare square = getRandomSpawnSquare(room);
            this.addRandomItemOnGround(square, types);
        }
    }

    public InventoryItem addRandomItemOnGround(IsoGridSquare square, ArrayList<String> types) {
        if (square != null && !types.isEmpty()) {
            String string = PZArrayUtil.pickRandom(types);
            return this.addItemOnGround(square, string);
        } else {
            return null;
        }
    }

    /**
     * Create and return a weapon, if it's ranged you can ask for some bullets in it
     */
    public HandWeapon addWeapon(String type, boolean addRandomBullets) {
        HandWeapon weapon = (HandWeapon)InventoryItemFactory.CreateItem(type);
        if (weapon == null) {
            return null;
        } else {
            if (weapon.isRanged() && addRandomBullets) {
                if (!StringUtils.isNullOrWhitespace(weapon.getMagazineType())) {
                    weapon.setContainsClip(true);
                }

                weapon.setCurrentAmmoCount(Rand.Next(Math.max(weapon.getMaxAmmo() - 8, 0), weapon.getMaxAmmo() - 2));
            }

            return weapon;
        }
    }

    public IsoDeadBody createSkeletonCorpse(RoomDef room) {
        if (room == null) {
            return null;
        } else {
            IsoGridSquare square = room.getRandomSquare(RandomizedWorldBase::is2x1or1x2AreaClear);
            if (square == null) {
                return null;
            } else {
                VirtualZombieManager.instance.choices.clear();
                VirtualZombieManager.instance.choices.add(square);
                IsoZombie zombie0 = VirtualZombieManager.instance.createRealZombieAlways(Rand.Next(8), false);
                if (zombie0 == null) {
                    return null;
                } else {
                    ZombieSpawnRecorder.instance.record(zombie0, this.getClass().getSimpleName());
                    alignCorpseToSquare(zombie0, square);
                    zombie0.setFakeDead(false);
                    zombie0.setHealth(0.0F);
                    zombie0.upKillCount = false;
                    zombie0.setSkeleton(true);
                    zombie0.getHumanVisual().setSkinTextureIndex(Rand.Next(1, 3));
                    return new IsoDeadBody(zombie0, true);
                }
            }
        }
    }

    /**
     * Check if the world age is correct for our definition
     */
    public boolean isTimeValid(boolean force) {
        if (this.minimumDays != 0 && this.maximumDays != 0) {
            float float0 = (float)GameTime.getInstance().getWorldAgeHours() / 24.0F;
            float0 += (SandboxOptions.instance.TimeSinceApo.getValue() - 1) * 30;
            return this.minimumDays > 0 && float0 < this.minimumDays ? false : this.maximumDays <= 0 || !(float0 > this.maximumDays);
        } else {
            return true;
        }
    }

    public String getName() {
        return this.name;
    }

    public String getDebugLine() {
        return this.debugLine;
    }

    public void setDebugLine(String _debugLine) {
        this.debugLine = _debugLine;
    }

    public int getMaximumDays() {
        return this.maximumDays;
    }

    public void setMaximumDays(int _maximumDays) {
        this.maximumDays = _maximumDays;
    }

    public boolean isUnique() {
        return this.unique;
    }

    public void setUnique(boolean _unique) {
        this.unique = _unique;
    }

    public IsoGridSquare getSq(int x, int y, int z) {
        return IsoWorld.instance.getCell().getGridSquare(x, y, z);
    }

    public IsoObject addTileObject(int x, int y, int z, String spriteName) {
        return this.addTileObject(this.getSq(x, y, z), spriteName);
    }

    public IsoObject addTileObject(IsoGridSquare sq, String spriteName) {
        if (sq == null) {
            return null;
        } else {
            IsoObject object = IsoObject.getNew(sq, spriteName, null, false);
            sq.AddTileObject(object);
            MapObjects.newGridSquare(sq);
            MapObjects.loadGridSquare(sq);
            return object;
        }
    }

    public IsoObject addTentNorthSouth(int x, int y, int z) {
        this.addTileObject(x, y - 1, z, "camping_01_1");
        return this.addTileObject(x, y, z, "camping_01_0");
    }

    public IsoObject addTentWestEast(int x, int y, int z) {
        this.addTileObject(x - 1, y, z, "camping_01_2");
        return this.addTileObject(x, y, z, "camping_01_3");
    }

    public BaseVehicle addTrailer(BaseVehicle v, IsoMetaGrid.Zone zone, IsoChunk chunk, String zoneName, String vehicleDistrib, String trailerName) {
        IsoGridSquare square = v.getSquare();
        IsoDirections directions = v.getDir();
        byte byte0 = 0;
        byte byte1 = 0;
        if (directions == IsoDirections.S) {
            byte1 = -3;
        }

        if (directions == IsoDirections.N) {
            byte1 = 3;
        }

        if (directions == IsoDirections.W) {
            byte0 = 3;
        }

        if (directions == IsoDirections.E) {
            byte0 = -3;
        }

        BaseVehicle vehicle = this.addVehicle(
            zone, this.getSq(square.x + byte0, square.y + byte1, square.z), chunk, zoneName, trailerName, null, directions, vehicleDistrib
        );
        if (vehicle != null) {
            v.positionTrailer(vehicle);
        }

        return vehicle;
    }
}
