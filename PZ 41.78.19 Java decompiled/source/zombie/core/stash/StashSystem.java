// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.stash;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.VirtualZombieManager;
import zombie.ZombieSpawnRecorder;
import zombie.Lua.LuaManager;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemPickerJava;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.MapItem;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTrap;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.ui.UIFont;
import zombie.util.Type;
import zombie.worldMap.symbols.WorldMapBaseSymbol;

public final class StashSystem {
    public static ArrayList<Stash> allStashes;
    public static ArrayList<StashBuilding> possibleStashes;
    public static ArrayList<StashBuilding> buildingsToDo;
    private static final ArrayList<String> possibleTrap = new ArrayList<>();
    private static ArrayList<String> alreadyReadMap = new ArrayList<>();

    public static void init() {
        if (possibleStashes == null) {
            initAllStashes();
            buildingsToDo = new ArrayList<>();
            possibleTrap.add("Base.FlameTrapSensorV1");
            possibleTrap.add("Base.SmokeBombSensorV1");
            possibleTrap.add("Base.NoiseTrapSensorV1");
            possibleTrap.add("Base.NoiseTrapSensorV2");
            possibleTrap.add("Base.AerosolbombSensorV1");
        }
    }

    /**
     * Load our different stashes description from lua files in "media/lua/shared/StashDescriptions"
     */
    public static void initAllStashes() {
        allStashes = new ArrayList<>();
        possibleStashes = new ArrayList<>();
        KahluaTable table = (KahluaTable)LuaManager.env.rawget("StashDescriptions");
        KahluaTableIterator kahluaTableIterator = table.iterator();

        while (kahluaTableIterator.advance()) {
            KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)kahluaTableIterator.getValue();
            Stash stash = new Stash(kahluaTableImpl.rawgetStr("name"));
            stash.load(kahluaTableImpl);
            allStashes.add(stash);
        }
    }

    /**
     * check if the spawned item could be a stash item (map or note...)
     */
    public static void checkStashItem(InventoryItem item) {
        if (!GameClient.bClient && !possibleStashes.isEmpty()) {
            int int0 = 60;
            if (item.getStashChance() > 0) {
                int0 = item.getStashChance();
            }

            switch (SandboxOptions.instance.AnnotatedMapChance.getValue()) {
                case 1:
                    return;
                case 2:
                    int0 += 15;
                    break;
                case 3:
                    int0 += 10;
                case 4:
                default:
                    break;
                case 5:
                    int0 -= 10;
                    break;
                case 6:
                    int0 -= 20;
            }

            if (Rand.Next(100) <= 100 - int0) {
                ArrayList arrayList = new ArrayList();

                for (int int1 = 0; int1 < allStashes.size(); int1++) {
                    Stash stash0 = allStashes.get(int1);
                    if (stash0.item.equals(item.getFullType()) && checkSpecificSpawnProperties(stash0, item)) {
                        boolean boolean0 = false;

                        for (int int2 = 0; int2 < possibleStashes.size(); int2++) {
                            StashBuilding stashBuilding = possibleStashes.get(int2);
                            if (stashBuilding.stashName.equals(stash0.name)) {
                                boolean0 = true;
                                break;
                            }
                        }

                        if (boolean0) {
                            arrayList.add(stash0);
                        }
                    }
                }

                if (!arrayList.isEmpty()) {
                    Stash stash1 = (Stash)arrayList.get(Rand.Next(0, arrayList.size()));
                    doStashItem(stash1, item);
                }
            }
        }
    }

    /**
     * Public for lua debug stash map
     */
    public static void doStashItem(Stash stash, InventoryItem item) {
        if (stash.customName != null) {
            item.setName(stash.customName);
        }

        if ("Map".equals(stash.type)) {
            MapItem mapItem = Type.tryCastTo(item, MapItem.class);
            if (mapItem == null) {
                throw new IllegalArgumentException(item + " is not a MapItem");
            }

            if (stash.annotations != null) {
                for (int int0 = 0; int0 < stash.annotations.size(); int0++) {
                    StashAnnotation stashAnnotation = stash.annotations.get(int0);
                    if (stashAnnotation.symbol != null) {
                        mapItem.getSymbols()
                            .addTexture(
                                stashAnnotation.symbol,
                                stashAnnotation.x,
                                stashAnnotation.y,
                                0.5F,
                                0.5F,
                                WorldMapBaseSymbol.DEFAULT_SCALE,
                                stashAnnotation.r,
                                stashAnnotation.g,
                                stashAnnotation.b,
                                1.0F
                            );
                    } else if (stashAnnotation.text != null) {
                        mapItem.getSymbols()
                            .addUntranslatedText(
                                stashAnnotation.text,
                                UIFont.Handwritten,
                                stashAnnotation.x,
                                stashAnnotation.y,
                                stashAnnotation.r,
                                stashAnnotation.g,
                                stashAnnotation.b,
                                1.0F
                            );
                    }
                }
            }

            removeFromPossibleStash(stash);
            item.setStashMap(stash.name);
        }
    }

    /**
     * Used when you read an annoted map
     */
    public static void prepareBuildingStash(String stashName) {
        if (stashName != null) {
            Stash stash = getStash(stashName);
            if (stash != null && !alreadyReadMap.contains(stashName)) {
                alreadyReadMap.add(stashName);
                buildingsToDo.add(new StashBuilding(stash.name, stash.buildingX, stash.buildingY));
                RoomDef roomDef = IsoWorld.instance.getMetaGrid().getRoomAt(stash.buildingX, stash.buildingY, 0);
                if (roomDef != null && roomDef.getBuilding() != null && roomDef.getBuilding().isFullyStreamedIn()) {
                    doBuildingStash(roomDef.getBuilding());
                }
            }
        }
    }

    private static boolean checkSpecificSpawnProperties(Stash stash, InventoryItem item) {
        return !stash.spawnOnlyOnZed || item.getContainer() != null && item.getContainer().getParent() instanceof IsoDeadBody
            ? (stash.minDayToSpawn <= -1 || GameTime.instance.getDaysSurvived() >= stash.minDayToSpawn)
                && (stash.maxDayToSpawn <= -1 || GameTime.instance.getDaysSurvived() <= stash.maxDayToSpawn)
            : false;
    }

    private static void removeFromPossibleStash(Stash stash) {
        for (int int0 = 0; int0 < possibleStashes.size(); int0++) {
            StashBuilding stashBuilding = possibleStashes.get(int0);
            if (stashBuilding.buildingX == stash.buildingX && stashBuilding.buildingY == stash.buildingY) {
                possibleStashes.remove(int0);
                int0--;
            }
        }
    }

    /**
     * Fetch our list of building in which we'll spawn stash, if this building correspond, we do the necessary stuff
     */
    public static void doBuildingStash(BuildingDef def) {
        if (buildingsToDo == null) {
            init();
        }

        for (int int0 = 0; int0 < buildingsToDo.size(); int0++) {
            StashBuilding stashBuilding = buildingsToDo.get(int0);
            if (stashBuilding.buildingX > def.x && stashBuilding.buildingX < def.x2 && stashBuilding.buildingY > def.y && stashBuilding.buildingY < def.y2) {
                if (def.hasBeenVisited) {
                    buildingsToDo.remove(int0);
                    int0--;
                } else {
                    Stash stash = getStash(stashBuilding.stashName);
                    if (stash != null) {
                        ItemPickerJava.ItemPickerRoom itemPickerRoom0 = ItemPickerJava.rooms.get(stash.spawnTable);
                        if (itemPickerRoom0 != null) {
                            def.setAllExplored(true);
                            doSpecificBuildingProperties(stash, def);

                            for (int int1 = def.x - 1; int1 < def.x2 + 1; int1++) {
                                for (int int2 = def.y - 1; int2 < def.y2 + 1; int2++) {
                                    for (int int3 = 0; int3 < 8; int3++) {
                                        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
                                        if (square != null) {
                                            for (int int4 = 0; int4 < square.getObjects().size(); int4++) {
                                                IsoObject object = square.getObjects().get(int4);
                                                if (object.getContainer() != null
                                                    && square.getRoom() != null
                                                    && square.getRoom().getBuilding().getDef() == def
                                                    && square.getRoom().getName() != null
                                                    && itemPickerRoom0.Containers.containsKey(object.getContainer().getType())) {
                                                    ItemPickerJava.ItemPickerRoom itemPickerRoom1 = ItemPickerJava.rooms.get(square.getRoom().getName());
                                                    boolean boolean0 = false;
                                                    if (itemPickerRoom1 == null || !itemPickerRoom1.Containers.containsKey(object.getContainer().getType())) {
                                                        object.getContainer().clear();
                                                        boolean0 = true;
                                                    }

                                                    ItemPickerJava.fillContainerType(itemPickerRoom0, object.getContainer(), "", null);
                                                    ItemPickerJava.updateOverlaySprite(object);
                                                    if (boolean0) {
                                                        object.getContainer().setExplored(true);
                                                    }
                                                }

                                                BarricadeAble barricadeAble = Type.tryCastTo(object, BarricadeAble.class);
                                                if (stash.barricades > -1
                                                    && barricadeAble != null
                                                    && barricadeAble.isBarricadeAllowed()
                                                    && Rand.Next(100) < stash.barricades) {
                                                    if (object instanceof IsoDoor) {
                                                        ((IsoDoor)object).addRandomBarricades();
                                                    } else if (object instanceof IsoWindow) {
                                                        ((IsoWindow)object).addRandomBarricades();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            buildingsToDo.remove(int0);
                            int0--;
                        }
                    }
                }
            }
        }
    }

    private static void doSpecificBuildingProperties(Stash stash, BuildingDef buildingDef) {
        if (stash.containers != null) {
            ArrayList arrayList0 = new ArrayList();

            for (int int0 = 0; int0 < stash.containers.size(); int0++) {
                StashContainer stashContainer = stash.containers.get(int0);
                IsoGridSquare square0 = null;
                if (!"all".equals(stashContainer.room)) {
                    for (int int1 = 0; int1 < buildingDef.rooms.size(); int1++) {
                        RoomDef roomDef0 = buildingDef.rooms.get(int1);
                        if (stashContainer.room.equals(roomDef0.name)) {
                            arrayList0.add(roomDef0);
                        }
                    }
                } else if (stashContainer.contX > -1 && stashContainer.contY > -1 && stashContainer.contZ > -1) {
                    square0 = IsoWorld.instance.getCell().getGridSquare(stashContainer.contX, stashContainer.contY, stashContainer.contZ);
                } else {
                    square0 = buildingDef.getFreeSquareInRoom();
                }

                if (!arrayList0.isEmpty()) {
                    RoomDef roomDef1 = (RoomDef)arrayList0.get(Rand.Next(0, arrayList0.size()));
                    square0 = roomDef1.getFreeSquare();
                }

                if (square0 != null) {
                    if (stashContainer.containerItem != null && !stashContainer.containerItem.isEmpty()) {
                        ItemPickerJava.ItemPickerRoom itemPickerRoom = ItemPickerJava.rooms.get(stash.spawnTable);
                        if (itemPickerRoom == null) {
                            DebugLog.log("Container distribution " + stash.spawnTable + " not found");
                            return;
                        }

                        InventoryItem item = InventoryItemFactory.CreateItem(stashContainer.containerItem);
                        if (item == null) {
                            DebugLog.General.error("Item " + stashContainer.containerItem + " Doesn't exist.");
                            return;
                        }

                        ItemPickerJava.ItemPickerContainer itemPickerContainer = itemPickerRoom.Containers.get(item.getType());
                        ItemPickerJava.rollContainerItem((InventoryContainer)item, null, itemPickerContainer);
                        square0.AddWorldInventoryItem(item, 0.0F, 0.0F, 0.0F);
                    } else {
                        IsoThumpable thumpable = new IsoThumpable(square0.getCell(), square0, stashContainer.containerSprite, false, null);
                        thumpable.setIsThumpable(false);
                        thumpable.container = new ItemContainer(stashContainer.containerType, square0, thumpable);
                        square0.AddSpecialObject(thumpable);
                        square0.RecalcAllWithNeighbours(true);
                    }
                } else {
                    DebugLog.log("No free room was found to spawn special container for stash " + stash.name);
                }
            }
        }

        if (stash.minTrapToSpawn > -1) {
            for (int int2 = stash.minTrapToSpawn; int2 < stash.maxTrapToSpawn; int2++) {
                IsoGridSquare square1 = buildingDef.getFreeSquareInRoom();
                if (square1 != null) {
                    HandWeapon weapon = (HandWeapon)InventoryItemFactory.CreateItem(possibleTrap.get(Rand.Next(0, possibleTrap.size())));
                    if (GameServer.bServer) {
                        GameServer.AddExplosiveTrap(weapon, square1, weapon.getSensorRange() > 0);
                    } else {
                        IsoTrap trap = new IsoTrap(weapon, square1.getCell(), square1);
                        square1.AddTileObject(trap);
                    }
                }
            }
        }

        if (stash.zombies > -1) {
            for (int int3 = 0; int3 < buildingDef.rooms.size(); int3++) {
                RoomDef roomDef2 = buildingDef.rooms.get(int3);
                if (IsoWorld.getZombiesEnabled()) {
                    byte byte0 = 1;
                    int int4 = 0;

                    for (int int5 = 0; int5 < roomDef2.area; int5++) {
                        if (Rand.Next(100) < stash.zombies) {
                            int4++;
                        }
                    }

                    if (SandboxOptions.instance.Zombies.getValue() == 1) {
                        int4 += 4;
                    } else if (SandboxOptions.instance.Zombies.getValue() == 2) {
                        int4 += 3;
                    } else if (SandboxOptions.instance.Zombies.getValue() == 3) {
                        int4 += 2;
                    } else if (SandboxOptions.instance.Zombies.getValue() == 5) {
                        int4 -= 4;
                    }

                    if (int4 > roomDef2.area / 2) {
                        int4 = roomDef2.area / 2;
                    }

                    if (int4 < byte0) {
                        int4 = byte0;
                    }

                    ArrayList arrayList1 = VirtualZombieManager.instance.addZombiesToMap(int4, roomDef2, false);
                    ZombieSpawnRecorder.instance.record(arrayList1, "StashSystem");
                }
            }
        }
    }

    public static Stash getStash(String stashName) {
        for (int int0 = 0; int0 < allStashes.size(); int0++) {
            Stash stash = allStashes.get(int0);
            if (stash.name.equals(stashName)) {
                return stash;
            }
        }

        return null;
    }

    /**
     * Check if the visited building is in one of our random stash, in that case we won't spawn any stash for this building
     */
    public static void visitedBuilding(BuildingDef def) {
        if (!GameClient.bClient) {
            for (int int0 = 0; int0 < possibleStashes.size(); int0++) {
                StashBuilding stashBuilding = possibleStashes.get(int0);
                if (stashBuilding.buildingX > def.x && stashBuilding.buildingX < def.x2 && stashBuilding.buildingY > def.y && stashBuilding.buildingY < def.y2) {
                    possibleStashes.remove(int0);
                    int0--;
                }
            }
        }
    }

    public static void load(ByteBuffer input, int WorldVersion) {
        init();
        alreadyReadMap = new ArrayList<>();
        possibleStashes = new ArrayList<>();
        buildingsToDo = new ArrayList<>();
        int int0 = input.getInt();

        for (int int1 = 0; int1 < int0; int1++) {
            possibleStashes.add(new StashBuilding(GameWindow.ReadString(input), input.getInt(), input.getInt()));
        }

        int int2 = input.getInt();

        for (int int3 = 0; int3 < int2; int3++) {
            buildingsToDo.add(new StashBuilding(GameWindow.ReadString(input), input.getInt(), input.getInt()));
        }

        if (WorldVersion >= 109) {
            int int4 = input.getInt();

            for (int int5 = 0; int5 < int4; int5++) {
                alreadyReadMap.add(GameWindow.ReadString(input));
            }
        }
    }

    public static void save(ByteBuffer output) {
        if (allStashes != null) {
            output.putInt(possibleStashes.size());

            for (int int0 = 0; int0 < possibleStashes.size(); int0++) {
                StashBuilding stashBuilding0 = possibleStashes.get(int0);
                GameWindow.WriteString(output, stashBuilding0.stashName);
                output.putInt(stashBuilding0.buildingX);
                output.putInt(stashBuilding0.buildingY);
            }

            output.putInt(buildingsToDo.size());

            for (int int1 = 0; int1 < buildingsToDo.size(); int1++) {
                StashBuilding stashBuilding1 = buildingsToDo.get(int1);
                GameWindow.WriteString(output, stashBuilding1.stashName);
                output.putInt(stashBuilding1.buildingX);
                output.putInt(stashBuilding1.buildingY);
            }

            output.putInt(alreadyReadMap.size());

            for (int int2 = 0; int2 < alreadyReadMap.size(); int2++) {
                GameWindow.WriteString(output, alreadyReadMap.get(int2));
            }
        }
    }

    public static ArrayList<StashBuilding> getPossibleStashes() {
        return possibleStashes;
    }

    public static void reinit() {
        possibleStashes = null;
        alreadyReadMap = new ArrayList<>();
        init();
    }

    public static void Reset() {
        allStashes = null;
        possibleStashes = null;
        buildingsToDo = null;
        possibleTrap.clear();
        alreadyReadMap.clear();
    }
}
