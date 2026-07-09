// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedBuilding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import zombie.SandboxOptions;
import zombie.VirtualZombieManager;
import zombie.ZombieSpawnRecorder;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorDesc;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemPickerJava;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.WeaponPart;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.SpawnPoints;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoWindow;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.randomizedWorld.RandomizedWorldBase;

public class RandomizedBuildingBase extends RandomizedWorldBase {
    private int chance = 0;
    private static int totalChance = 0;
    private static HashMap<RandomizedBuildingBase, Integer> rbMap = new HashMap<>();
    protected static final int KBBuildingX = 10744;
    protected static final int KBBuildingY = 9409;
    private boolean alwaysDo = false;
    private static HashMap<String, String> weaponsList = new HashMap<>();

    public void randomizeBuilding(BuildingDef def) {
        def.bAlarmed = false;
    }

    public void init() {
        if (weaponsList.isEmpty()) {
            weaponsList.put("Base.Shotgun", "Base.ShotgunShellsBox");
            weaponsList.put("Base.Pistol", "Base.Bullets9mmBox");
            weaponsList.put("Base.Pistol2", "Base.Bullets45Box");
            weaponsList.put("Base.Pistol3", "Base.Bullets44Box");
            weaponsList.put("Base.VarmintRifle", "Base.223Box");
            weaponsList.put("Base.HuntingRifle", "Base.308Box");
        }
    }

    public static void initAllRBMapChance() {
        for (int int0 = 0; int0 < IsoWorld.instance.getRandomizedBuildingList().size(); int0++) {
            totalChance = totalChance + IsoWorld.instance.getRandomizedBuildingList().get(int0).getChance();
            rbMap.put(IsoWorld.instance.getRandomizedBuildingList().get(int0), IsoWorld.instance.getRandomizedBuildingList().get(int0).getChance());
        }
    }

    /**
     * Don't do any building change in a player's building Also check if the  building have a bathroom, a kitchen and a bedroom  This is ignored for the alwaysDo building (so i can do stuff in spiffo, pizzawhirled, etc..)
     */
    public boolean isValid(BuildingDef def, boolean force) {
        this.debugLine = "";
        if (GameClient.bClient) {
            return false;
        } else if (def.isAllExplored() && !force) {
            return false;
        } else {
            if (!GameServer.bServer) {
                if (!force
                    && IsoPlayer.getInstance().getSquare() != null
                    && IsoPlayer.getInstance().getSquare().getBuilding() != null
                    && IsoPlayer.getInstance().getSquare().getBuilding().def == def) {
                    this.customizeStartingHouse(IsoPlayer.getInstance().getSquare().getBuilding().def);
                    return false;
                }
            } else if (!force) {
                for (int int0 = 0; int0 < GameServer.Players.size(); int0++) {
                    IsoPlayer player = GameServer.Players.get(int0);
                    if (player.getSquare() != null && player.getSquare().getBuilding() != null && player.getSquare().getBuilding().def == def) {
                        return false;
                    }
                }
            }

            boolean boolean0 = false;
            boolean boolean1 = false;
            boolean boolean2 = false;

            for (int int1 = 0; int1 < def.rooms.size(); int1++) {
                RoomDef roomDef = def.rooms.get(int1);
                if ("bedroom".equals(roomDef.name)) {
                    boolean0 = true;
                }

                if ("kitchen".equals(roomDef.name) || "livingroom".equals(roomDef.name)) {
                    boolean1 = true;
                }

                if ("bathroom".equals(roomDef.name)) {
                    boolean2 = true;
                }
            }

            if (!boolean0) {
                this.debugLine = this.debugLine + "no bedroom ";
            }

            if (!boolean2) {
                this.debugLine = this.debugLine + "no bathroom ";
            }

            if (!boolean1) {
                this.debugLine = this.debugLine + "no living room or kitchen ";
            }

            return boolean0 && boolean2 && boolean1;
        }
    }

    private void customizeStartingHouse(BuildingDef var1) {
    }

    public int getMinimumDays() {
        return this.minimumDays;
    }

    public void setMinimumDays(int minimumDays) {
        this.minimumDays = minimumDays;
    }

    public int getMinimumRooms() {
        return this.minimumRooms;
    }

    public void setMinimumRooms(int minimumRooms) {
        this.minimumRooms = minimumRooms;
    }

    public static void ChunkLoaded(IsoBuilding building) {
        if (!GameClient.bClient && building.def != null && !building.def.seen && building.def.isFullyStreamedIn()) {
            if (GameServer.bServer && GameServer.Players.isEmpty()) {
                return;
            }

            for (int int0 = 0; int0 < building.Rooms.size(); int0++) {
                if (building.Rooms.get(int0).def.bExplored) {
                    return;
                }
            }

            if (!building.def.isAnyChunkNewlyLoaded()) {
                building.def.seen = true;
                return;
            }

            ArrayList arrayList = new ArrayList();

            for (int int1 = 0; int1 < IsoWorld.instance.getRandomizedBuildingList().size(); int1++) {
                RandomizedBuildingBase randomizedBuildingBase0 = IsoWorld.instance.getRandomizedBuildingList().get(int1);
                if (randomizedBuildingBase0.isAlwaysDo() && randomizedBuildingBase0.isValid(building.def, false)) {
                    arrayList.add(randomizedBuildingBase0);
                }
            }

            building.def.seen = true;
            if (building.def.x == 10744 && building.def.y == 9409 && Rand.Next(100) < 31) {
                RBKateAndBaldspot rBKateAndBaldspot = new RBKateAndBaldspot();
                rBKateAndBaldspot.randomizeBuilding(building.def);
                return;
            }

            if (!arrayList.isEmpty()) {
                RandomizedBuildingBase randomizedBuildingBase1 = (RandomizedBuildingBase)arrayList.get(Rand.Next(0, arrayList.size()));
                if (randomizedBuildingBase1 != null) {
                    randomizedBuildingBase1.randomizeBuilding(building.def);
                    return;
                }
            }

            if (GameServer.bServer && SpawnPoints.instance.isSpawnBuilding(building.getDef())) {
                return;
            }

            RandomizedBuildingBase randomizedBuildingBase2 = IsoWorld.instance.getRBBasic();
            if ("Tutorial".equals(Core.GameMode)) {
                return;
            }

            try {
                byte byte0 = 10;
                switch (SandboxOptions.instance.SurvivorHouseChance.getValue()) {
                    case 1:
                        return;
                    case 2:
                        byte0 -= 5;
                    case 3:
                    default:
                        break;
                    case 4:
                        byte0 += 5;
                        break;
                    case 5:
                        byte0 += 10;
                        break;
                    case 6:
                        byte0 += 20;
                }

                if (Rand.Next(100) <= byte0) {
                    if (totalChance == 0) {
                        initAllRBMapChance();
                    }

                    randomizedBuildingBase2 = getRandomStory();
                    if (randomizedBuildingBase2 == null) {
                        return;
                    }
                }

                if (randomizedBuildingBase2.isValid(building.def, false) && randomizedBuildingBase2.isTimeValid(false)) {
                    randomizedBuildingBase2.randomizeBuilding(building.def);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public int getChance() {
        return this.chance;
    }

    public void setChance(int _chance) {
        this.chance = _chance;
    }

    public boolean isAlwaysDo() {
        return this.alwaysDo;
    }

    public void setAlwaysDo(boolean _alwaysDo) {
        this.alwaysDo = _alwaysDo;
    }

    private static RandomizedBuildingBase getRandomStory() {
        int int0 = Rand.Next(totalChance);
        Iterator iterator = rbMap.keySet().iterator();
        int int1 = 0;

        while (iterator.hasNext()) {
            RandomizedBuildingBase randomizedBuildingBase = (RandomizedBuildingBase)iterator.next();
            int1 += rbMap.get(randomizedBuildingBase);
            if (int0 < int1) {
                return randomizedBuildingBase;
            }
        }

        return null;
    }

    @Override
    public ArrayList<IsoZombie> addZombiesOnSquare(int totalZombies, String outfit, Integer femaleChance, IsoGridSquare square) {
        if (!IsoWorld.getZombiesDisabled() && !"Tutorial".equals(Core.GameMode)) {
            ArrayList arrayList = new ArrayList();

            for (int int0 = 0; int0 < totalZombies; int0++) {
                VirtualZombieManager.instance.choices.clear();
                VirtualZombieManager.instance.choices.add(square);
                IsoZombie zombie0 = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.getRandom().index(), false);
                if (zombie0 != null) {
                    if ("Kate".equals(outfit) || "Bob".equals(outfit) || "Raider".equals(outfit)) {
                        zombie0.doDirtBloodEtc = false;
                    }

                    if (femaleChance != null) {
                        zombie0.setFemaleEtc(Rand.Next(100) < femaleChance);
                    }

                    if (outfit != null) {
                        zombie0.dressInPersistentOutfit(outfit);
                        zombie0.bDressInRandomOutfit = false;
                    } else {
                        zombie0.bDressInRandomOutfit = true;
                    }

                    arrayList.add(zombie0);
                }
            }

            ZombieSpawnRecorder.instance.record(arrayList, this.getClass().getSimpleName());
            return arrayList;
        } else {
            return null;
        }
    }

    /**
     * If you specify a outfit, make sure it works for both gender! (or force  femaleChance to 0 or 1 if it's gender-specific)
     * 
     * @param def buildingDef
     * @param totalZombies zombies to spawn (if 0 we gonna randomize it)
     * @param outfit force zombies spanwed in a specific outfit (not mandatory)
     * @param femaleChance force female zombies (if not set it'll be 50% chance, you can set             it to 0 to exclude female from spawning, or 100 to force only             female)
     * @param room force spawn zombies inside a certain room (not mandatory)
     */
    public ArrayList<IsoZombie> addZombies(BuildingDef def, int totalZombies, String outfit, Integer femaleChance, RoomDef room) {
        boolean boolean0 = room == null;
        ArrayList arrayList = new ArrayList();
        if (!IsoWorld.getZombiesDisabled() && !"Tutorial".equals(Core.GameMode)) {
            if (room == null) {
                room = this.getRandomRoom(def, 6);
            }

            int int0 = 2;
            int int1 = room.area / 2;
            if (totalZombies == 0) {
                if (SandboxOptions.instance.Zombies.getValue() == 1) {
                    int1 += 4;
                } else if (SandboxOptions.instance.Zombies.getValue() == 2) {
                    int1 += 3;
                } else if (SandboxOptions.instance.Zombies.getValue() == 3) {
                    int1 += 2;
                } else if (SandboxOptions.instance.Zombies.getValue() == 5) {
                    int1 -= 4;
                }

                if (int1 > 8) {
                    int1 = 8;
                }

                if (int1 < int0) {
                    int1 = int0 + 1;
                }
            } else {
                int0 = totalZombies;
                int1 = totalZombies;
            }

            int int2 = Rand.Next(int0, int1);

            for (int int3 = 0; int3 < int2; int3++) {
                IsoGridSquare square = getRandomSpawnSquare(room);
                if (square == null) {
                    break;
                }

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
                        zombie0.bDressInRandomOutfit = true;
                    }

                    arrayList.add(zombie0);
                    if (boolean0) {
                        room = this.getRandomRoom(def, 6);
                    }
                }
            }

            ZombieSpawnRecorder.instance.record(arrayList, this.getClass().getSimpleName());
            return arrayList;
        } else {
            return arrayList;
        }
    }

    public HandWeapon addRandomRangedWeapon(ItemContainer container, boolean addBulletsInGun, boolean addBoxInContainer, boolean attachPart) {
        if (weaponsList == null || weaponsList.isEmpty()) {
            this.init();
        }

        ArrayList arrayList = new ArrayList<>(weaponsList.keySet());
        String string = (String)arrayList.get(Rand.Next(0, arrayList.size()));
        HandWeapon weapon = this.addWeapon(string, addBulletsInGun);
        if (weapon == null) {
            return null;
        } else {
            if (addBoxInContainer) {
                container.addItem(InventoryItemFactory.CreateItem(weaponsList.get(string)));
            }

            if (attachPart) {
                KahluaTable table0 = (KahluaTable)LuaManager.env.rawget("WeaponUpgrades");
                if (table0 == null) {
                    return null;
                }

                KahluaTable table1 = (KahluaTable)table0.rawget(weapon.getType());
                if (table1 == null) {
                    return null;
                }

                int int0 = Rand.Next(1, table1.len() + 1);

                for (int int1 = 1; int1 <= int0; int1++) {
                    int int2 = Rand.Next(table1.len()) + 1;
                    WeaponPart weaponPart = (WeaponPart)InventoryItemFactory.CreateItem((String)table1.rawget(int2));
                    weapon.attachWeaponPart(weaponPart);
                }
            }

            return weapon;
        }
    }

    public void spawnItemsInContainers(BuildingDef def, String distribName, int _chance) {
        ArrayList arrayList = new ArrayList();
        ItemPickerJava.ItemPickerRoom itemPickerRoom = ItemPickerJava.rooms.get(distribName);
        IsoCell cell = IsoWorld.instance.CurrentCell;

        for (int int0 = def.x - 1; int0 < def.x2 + 1; int0++) {
            for (int int1 = def.y - 1; int1 < def.y2 + 1; int1++) {
                for (int int2 = 0; int2 < 8; int2++) {
                    IsoGridSquare square = cell.getGridSquare(int0, int1, int2);
                    if (square != null) {
                        for (int int3 = 0; int3 < square.getObjects().size(); int3++) {
                            IsoObject object = square.getObjects().get(int3);
                            if (Rand.Next(100) <= _chance
                                && object.getContainer() != null
                                && square.getRoom() != null
                                && square.getRoom().getName() != null
                                && itemPickerRoom.Containers.containsKey(object.getContainer().getType())) {
                                object.getContainer().clear();
                                arrayList.add(object.getContainer());
                                object.getContainer().setExplored(true);
                            }
                        }
                    }
                }
            }
        }

        for (int int4 = 0; int4 < arrayList.size(); int4++) {
            ItemContainer container = (ItemContainer)arrayList.get(int4);
            ItemPickerJava.fillContainerType(itemPickerRoom, container, "", null);
            ItemPickerJava.updateOverlaySprite(container.getParent());
            if (GameServer.bServer) {
                GameServer.sendItemsInContainer(container.getParent(), container);
            }
        }
    }

    protected void removeAllZombies(BuildingDef buildingDef) {
        for (int int0 = buildingDef.x - 1; int0 < buildingDef.x + buildingDef.x2 + 1; int0++) {
            for (int int1 = buildingDef.y - 1; int1 < buildingDef.y + buildingDef.y2 + 1; int1++) {
                for (int int2 = 0; int2 < 8; int2++) {
                    IsoGridSquare square = this.getSq(int0, int1, int2);
                    if (square != null) {
                        for (int int3 = 0; int3 < square.getMovingObjects().size(); int3++) {
                            square.getMovingObjects().remove(int3);
                            int3--;
                        }
                    }
                }
            }
        }
    }

    public IsoWindow getWindow(IsoGridSquare sq) {
        for (int int0 = 0; int0 < sq.getObjects().size(); int0++) {
            IsoObject object = sq.getObjects().get(int0);
            if (object instanceof IsoWindow) {
                return (IsoWindow)object;
            }
        }

        return null;
    }

    public IsoDoor getDoor(IsoGridSquare sq) {
        for (int int0 = 0; int0 < sq.getObjects().size(); int0++) {
            IsoObject object = sq.getObjects().get(int0);
            if (object instanceof IsoDoor) {
                return (IsoDoor)object;
            }
        }

        return null;
    }

    public void addBarricade(IsoGridSquare sq, int numPlanks) {
        for (int int0 = 0; int0 < sq.getObjects().size(); int0++) {
            IsoObject object = sq.getObjects().get(int0);
            if (object instanceof IsoDoor) {
                if (!((IsoDoor)object).isBarricadeAllowed()) {
                    continue;
                }

                IsoGridSquare square0 = sq.getRoom() == null ? sq : ((IsoDoor)object).getOppositeSquare();
                if (square0 != null && square0.getRoom() == null) {
                    boolean boolean0 = square0 != sq;
                    IsoBarricade barricade0 = IsoBarricade.AddBarricadeToObject((IsoDoor)object, boolean0);
                    if (barricade0 != null) {
                        for (int int1 = 0; int1 < numPlanks; int1++) {
                            barricade0.addPlank(null, null);
                        }

                        if (GameServer.bServer) {
                            barricade0.transmitCompleteItemToClients();
                        }
                    }
                }
            }

            if (object instanceof IsoWindow && ((IsoWindow)object).isBarricadeAllowed()) {
                IsoGridSquare square1 = sq.getRoom() == null ? sq : ((IsoWindow)object).getOppositeSquare();
                boolean boolean1 = square1 != sq;
                IsoBarricade barricade1 = IsoBarricade.AddBarricadeToObject((IsoWindow)object, boolean1);
                if (barricade1 != null) {
                    for (int int2 = 0; int2 < numPlanks; int2++) {
                        barricade1.addPlank(null, null);
                    }

                    if (GameServer.bServer) {
                        barricade1.transmitCompleteItemToClients();
                    }
                }
            }
        }
    }

    public InventoryItem addWorldItem(String item, IsoGridSquare sq, float xoffset, float yoffset, float zoffset) {
        return this.addWorldItem(item, sq, xoffset, yoffset, zoffset, 0);
    }

    public InventoryItem addWorldItem(String item, IsoGridSquare sq, float xoffset, float yoffset, float zoffset, int worldZ) {
        if (item != null && sq != null) {
            InventoryItem _item = InventoryItemFactory.CreateItem(item);
            if (_item != null) {
                _item.setAutoAge();
                _item.setWorldZRotation(worldZ);
                if (_item instanceof HandWeapon) {
                    _item.setCondition(Rand.Next(2, _item.getConditionMax()));
                }

                return sq.AddWorldInventoryItem(_item, xoffset, yoffset, zoffset);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public InventoryItem addWorldItem(String item, IsoGridSquare sq, IsoObject obj) {
        if (item != null && sq != null) {
            float float0 = 0.0F;
            if (obj != null) {
                float0 = obj.getSurfaceOffsetNoTable() / 96.0F;
            }

            InventoryItem _item = InventoryItemFactory.CreateItem(item);
            if (_item != null) {
                _item.setAutoAge();
                return sq.AddWorldInventoryItem(_item, Rand.Next(0.3F, 0.9F), Rand.Next(0.3F, 0.9F), float0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public boolean isTableFor3DItems(IsoObject obj, IsoGridSquare sq) {
        return obj.getSurfaceOffsetNoTable() > 0.0F
            && obj.getContainer() == null
            && sq.getProperties().Val("waterAmount") == null
            && !obj.hasWater()
            && obj.getProperties().Val("BedType") == null;
    }

    public static final class HumanCorpse extends IsoGameCharacter implements IHumanVisual {
        final HumanVisual humanVisual = new HumanVisual(this);
        final ItemVisuals itemVisuals = new ItemVisuals();
        public boolean isSkeleton = false;

        public HumanCorpse(IsoCell cell, float float0, float float1, float float2) {
            super(cell, float0, float1, float2);
            cell.getObjectList().remove(this);
            cell.getAddList().remove(this);
        }

        @Override
        public void dressInNamedOutfit(String string) {
            this.getHumanVisual().dressInNamedOutfit(string, this.itemVisuals);
            this.getHumanVisual().synchWithOutfit(this.getHumanVisual().getOutfit());
        }

        @Override
        public HumanVisual getHumanVisual() {
            return this.humanVisual;
        }

        public HumanVisual getVisual() {
            return this.humanVisual;
        }

        @Override
        public void Dressup(SurvivorDesc var1) {
            this.wornItems.setFromItemVisuals(this.itemVisuals);
            this.wornItems.addItemsToItemContainer(this.inventory);
        }

        @Override
        public boolean isSkeleton() {
            return this.isSkeleton;
        }
    }
}
