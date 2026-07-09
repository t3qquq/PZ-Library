// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory;

import gnu.trove.map.hash.THashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.vm.KahluaUtil;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.stash.StashSystem;
import zombie.debug.DebugLog;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Key;
import zombie.inventory.types.MapItem;
import zombie.inventory.types.WeaponPart;
import zombie.iso.ContainerOverlays;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaChunk;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.radio.ZomboidRadio;
import zombie.radio.media.MediaData;
import zombie.radio.media.RecordedMedia;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.util.list.PZArrayList;
import zombie.util.list.PZArrayUtil;

public final class ItemPickerJava {
    private static IsoPlayer player;
    private static float OtherLootModifier;
    private static float FoodLootModifier;
    private static float CannedFoodLootModifier;
    private static float WeaponLootModifier;
    private static float RangedWeaponLootModifier;
    private static float AmmoLootModifier;
    private static float LiteratureLootModifier;
    private static float SurvivalGearsLootModifier;
    private static float MedicalLootModifier;
    private static float BagLootModifier;
    private static float MechanicsLootModifier;
    public static float zombieDensityCap = 8.0F;
    public static final ArrayList<String> NoContainerFillRooms = new ArrayList<>();
    public static final ArrayList<ItemPickerJava.ItemPickerUpgradeWeapons> WeaponUpgrades = new ArrayList<>();
    public static final HashMap<String, ItemPickerJava.ItemPickerUpgradeWeapons> WeaponUpgradeMap = new HashMap<>();
    public static final THashMap<String, ItemPickerJava.ItemPickerRoom> rooms = new THashMap<>();
    public static final THashMap<String, ItemPickerJava.ItemPickerContainer> containers = new THashMap<>();
    public static final THashMap<String, ItemPickerJava.ItemPickerContainer> ProceduralDistributions = new THashMap<>();
    public static final THashMap<String, ItemPickerJava.VehicleDistribution> VehicleDistributions = new THashMap<>();

    public static void Parse() {
        rooms.clear();
        NoContainerFillRooms.clear();
        WeaponUpgradeMap.clear();
        WeaponUpgrades.clear();
        containers.clear();
        KahluaTableImpl kahluaTableImpl0 = (KahluaTableImpl)LuaManager.env.rawget("NoContainerFillRooms");

        for (Entry entry0 : kahluaTableImpl0.delegate.entrySet()) {
            String string0 = entry0.getKey().toString();
            NoContainerFillRooms.add(string0);
        }

        KahluaTableImpl kahluaTableImpl1 = (KahluaTableImpl)LuaManager.env.rawget("WeaponUpgrades");

        for (Entry entry1 : kahluaTableImpl1.delegate.entrySet()) {
            String string1 = entry1.getKey().toString();
            ItemPickerJava.ItemPickerUpgradeWeapons itemPickerUpgradeWeapons = new ItemPickerJava.ItemPickerUpgradeWeapons();
            itemPickerUpgradeWeapons.name = string1;
            WeaponUpgrades.add(itemPickerUpgradeWeapons);
            WeaponUpgradeMap.put(string1, itemPickerUpgradeWeapons);
            KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)entry1.getValue();

            for (Entry entry2 : kahluaTableImpl2.delegate.entrySet()) {
                String string2 = entry2.getValue().toString();
                itemPickerUpgradeWeapons.Upgrades.add(string2);
            }
        }

        ParseSuburbsDistributions();
        ParseVehicleDistributions();
        ParseProceduralDistributions();
    }

    private static void ParseSuburbsDistributions() {
        KahluaTableImpl kahluaTableImpl0 = (KahluaTableImpl)LuaManager.env.rawget("SuburbsDistributions");

        for (Entry entry0 : kahluaTableImpl0.delegate.entrySet()) {
            String string0 = entry0.getKey().toString();
            KahluaTableImpl kahluaTableImpl1 = (KahluaTableImpl)entry0.getValue();
            if (kahluaTableImpl1.delegate.containsKey("rolls")) {
                ItemPickerJava.ItemPickerContainer itemPickerContainer0 = ExtractContainersFromLua(kahluaTableImpl1);
                containers.put(string0, itemPickerContainer0);
            } else {
                ItemPickerJava.ItemPickerRoom itemPickerRoom = new ItemPickerJava.ItemPickerRoom();
                rooms.put(string0, itemPickerRoom);

                for (Entry entry1 : kahluaTableImpl1.delegate.entrySet()) {
                    String string1 = entry1.getKey().toString();
                    if (entry1.getValue() instanceof Double) {
                        itemPickerRoom.fillRand = ((Double)entry1.getValue()).intValue();
                    } else if ("isShop".equals(string1)) {
                        itemPickerRoom.isShop = (Boolean)entry1.getValue();
                    } else {
                        KahluaTableImpl kahluaTableImpl2 = null;

                        try {
                            kahluaTableImpl2 = (KahluaTableImpl)entry1.getValue();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }

                        if (kahluaTableImpl2.delegate.containsKey("procedural")
                            || !string1.isEmpty() && kahluaTableImpl2.delegate.containsKey("rolls") && kahluaTableImpl2.delegate.containsKey("items")) {
                            ItemPickerJava.ItemPickerContainer itemPickerContainer1 = ExtractContainersFromLua(kahluaTableImpl2);
                            itemPickerRoom.Containers.put(string1, itemPickerContainer1);
                        } else {
                            DebugLog.log("ERROR: SuburbsDistributions[\"" + string0 + "\"] is broken");
                        }
                    }
                }
            }
        }
    }

    private static void ParseVehicleDistributions() {
        VehicleDistributions.clear();
        KahluaTableImpl kahluaTableImpl0 = (KahluaTableImpl)LuaManager.env.rawget("VehicleDistributions");
        if (kahluaTableImpl0 != null && kahluaTableImpl0.rawget(1) instanceof KahluaTableImpl) {
            kahluaTableImpl0 = (KahluaTableImpl)kahluaTableImpl0.rawget(1);

            for (Entry entry0 : kahluaTableImpl0.delegate.entrySet()) {
                if (entry0.getKey() instanceof String && entry0.getValue() instanceof KahluaTableImpl) {
                    KahluaTableImpl kahluaTableImpl1 = (KahluaTableImpl)entry0.getValue();
                    ItemPickerJava.VehicleDistribution vehicleDistribution = new ItemPickerJava.VehicleDistribution();
                    if (kahluaTableImpl1.rawget("Normal") instanceof KahluaTableImpl) {
                        KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)kahluaTableImpl1.rawget("Normal");
                        ItemPickerJava.ItemPickerRoom itemPickerRoom0 = new ItemPickerJava.ItemPickerRoom();

                        for (Entry entry1 : kahluaTableImpl2.delegate.entrySet()) {
                            String string0 = entry1.getKey().toString();
                            itemPickerRoom0.Containers.put(string0, ExtractContainersFromLua((KahluaTableImpl)entry1.getValue()));
                        }

                        vehicleDistribution.Normal = itemPickerRoom0;
                    }

                    if (kahluaTableImpl1.rawget("Specific") instanceof KahluaTableImpl) {
                        KahluaTableImpl kahluaTableImpl3 = (KahluaTableImpl)kahluaTableImpl1.rawget("Specific");

                        for (int int0 = 1; int0 <= kahluaTableImpl3.len(); int0++) {
                            KahluaTableImpl kahluaTableImpl4 = (KahluaTableImpl)kahluaTableImpl3.rawget(int0);
                            ItemPickerJava.ItemPickerRoom itemPickerRoom1 = new ItemPickerJava.ItemPickerRoom();

                            for (Entry entry2 : kahluaTableImpl4.delegate.entrySet()) {
                                String string1 = entry2.getKey().toString();
                                if (string1.equals("specificId")) {
                                    itemPickerRoom1.specificId = (String)entry2.getValue();
                                } else {
                                    itemPickerRoom1.Containers.put(string1, ExtractContainersFromLua((KahluaTableImpl)entry2.getValue()));
                                }
                            }

                            vehicleDistribution.Specific.add(itemPickerRoom1);
                        }
                    }

                    if (vehicleDistribution.Normal != null) {
                        VehicleDistributions.put((String)entry0.getKey(), vehicleDistribution);
                    }
                }
            }
        }
    }

    private static void ParseProceduralDistributions() {
        ProceduralDistributions.clear();
        KahluaTableImpl kahluaTableImpl0 = Type.tryCastTo(LuaManager.env.rawget("ProceduralDistributions"), KahluaTableImpl.class);
        if (kahluaTableImpl0 != null) {
            KahluaTableImpl kahluaTableImpl1 = Type.tryCastTo(kahluaTableImpl0.rawget("list"), KahluaTableImpl.class);
            if (kahluaTableImpl1 != null) {
                for (Entry entry : kahluaTableImpl1.delegate.entrySet()) {
                    String string = entry.getKey().toString();
                    KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)entry.getValue();
                    ItemPickerJava.ItemPickerContainer itemPickerContainer = ExtractContainersFromLua(kahluaTableImpl2);
                    ProceduralDistributions.put(string, itemPickerContainer);
                }
            }
        }
    }

    private static ItemPickerJava.ItemPickerContainer ExtractContainersFromLua(KahluaTableImpl kahluaTableImpl0) {
        ItemPickerJava.ItemPickerContainer itemPickerContainer = new ItemPickerJava.ItemPickerContainer();
        if (kahluaTableImpl0.delegate.containsKey("procedural")) {
            itemPickerContainer.procedural = kahluaTableImpl0.rawgetBool("procedural");
            itemPickerContainer.proceduralItems = ExtractProcList(kahluaTableImpl0);
            return itemPickerContainer;
        } else {
            if (kahluaTableImpl0.delegate.containsKey("noAutoAge")) {
                itemPickerContainer.noAutoAge = kahluaTableImpl0.rawgetBool("noAutoAge");
            }

            if (kahluaTableImpl0.delegate.containsKey("fillRand")) {
                itemPickerContainer.fillRand = kahluaTableImpl0.rawgetInt("fillRand");
            }

            if (kahluaTableImpl0.delegate.containsKey("maxMap")) {
                itemPickerContainer.maxMap = kahluaTableImpl0.rawgetInt("maxMap");
            }

            if (kahluaTableImpl0.delegate.containsKey("stashChance")) {
                itemPickerContainer.stashChance = kahluaTableImpl0.rawgetInt("stashChance");
            }

            if (kahluaTableImpl0.delegate.containsKey("dontSpawnAmmo")) {
                itemPickerContainer.dontSpawnAmmo = kahluaTableImpl0.rawgetBool("dontSpawnAmmo");
            }

            if (kahluaTableImpl0.delegate.containsKey("ignoreZombieDensity")) {
                itemPickerContainer.ignoreZombieDensity = kahluaTableImpl0.rawgetBool("ignoreZombieDensity");
            }

            double double0 = (Double)kahluaTableImpl0.delegate.get("rolls");
            if (kahluaTableImpl0.delegate.containsKey("junk")) {
                itemPickerContainer.junk = ExtractContainersFromLua((KahluaTableImpl)kahluaTableImpl0.rawget("junk"));
            }

            itemPickerContainer.rolls = (int)double0;
            KahluaTableImpl kahluaTableImpl1 = (KahluaTableImpl)kahluaTableImpl0.delegate.get("items");
            ArrayList arrayList = new ArrayList();
            int int0 = kahluaTableImpl1.len();

            for (byte byte0 = 0; byte0 < int0; byte0 += 2) {
                String string = Type.tryCastTo(kahluaTableImpl1.delegate.get(KahluaUtil.toDouble((long)(byte0 + 1))), String.class);
                Double double1 = Type.tryCastTo(kahluaTableImpl1.delegate.get(KahluaUtil.toDouble((long)(byte0 + 2))), Double.class);
                if (string != null && double1 != null) {
                    Item item = ScriptManager.instance.FindItem(string);
                    if (item != null && !item.OBSOLETE) {
                        ItemPickerJava.ItemPickerItem itemPickerItem = new ItemPickerJava.ItemPickerItem();
                        itemPickerItem.itemName = string;
                        itemPickerItem.chance = double1.floatValue();
                        arrayList.add(itemPickerItem);
                    } else if (Core.bDebug) {
                        DebugLog.General.warn("ignoring invalid ItemPicker item type \"%s\"", string);
                    }
                }
            }

            itemPickerContainer.Items = arrayList.toArray(itemPickerContainer.Items);
            return itemPickerContainer;
        }
    }

    private static ArrayList<ItemPickerJava.ProceduralItem> ExtractProcList(KahluaTableImpl kahluaTableImpl1) {
        ArrayList arrayList = new ArrayList();
        KahluaTableImpl kahluaTableImpl0 = (KahluaTableImpl)kahluaTableImpl1.rawget("procList");
        KahluaTableIterator kahluaTableIterator = kahluaTableImpl0.iterator();

        while (kahluaTableIterator.advance()) {
            KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)kahluaTableIterator.getValue();
            ItemPickerJava.ProceduralItem proceduralItem = new ItemPickerJava.ProceduralItem();
            proceduralItem.name = kahluaTableImpl2.rawgetStr("name");
            proceduralItem.min = kahluaTableImpl2.rawgetInt("min");
            proceduralItem.max = kahluaTableImpl2.rawgetInt("max");
            proceduralItem.weightChance = kahluaTableImpl2.rawgetInt("weightChance");
            String string0 = kahluaTableImpl2.rawgetStr("forceForItems");
            String string1 = kahluaTableImpl2.rawgetStr("forceForZones");
            String string2 = kahluaTableImpl2.rawgetStr("forceForTiles");
            String string3 = kahluaTableImpl2.rawgetStr("forceForRooms");
            if (!StringUtils.isNullOrWhitespace(string0)) {
                proceduralItem.forceForItems = Arrays.asList(string0.split(";"));
            }

            if (!StringUtils.isNullOrWhitespace(string1)) {
                proceduralItem.forceForZones = Arrays.asList(string1.split(";"));
            }

            if (!StringUtils.isNullOrWhitespace(string2)) {
                proceduralItem.forceForTiles = Arrays.asList(string2.split(";"));
            }

            if (!StringUtils.isNullOrWhitespace(string3)) {
                proceduralItem.forceForRooms = Arrays.asList(string3.split(";"));
            }

            arrayList.add(proceduralItem);
        }

        return arrayList;
    }

    public static void InitSandboxLootSettings() {
        OtherLootModifier = doSandboxSettings(SandboxOptions.getInstance().OtherLoot.getValue());
        FoodLootModifier = doSandboxSettings(SandboxOptions.getInstance().FoodLoot.getValue());
        WeaponLootModifier = doSandboxSettings(SandboxOptions.getInstance().WeaponLoot.getValue());
        RangedWeaponLootModifier = doSandboxSettings(SandboxOptions.getInstance().RangedWeaponLoot.getValue());
        AmmoLootModifier = doSandboxSettings(SandboxOptions.getInstance().AmmoLoot.getValue());
        CannedFoodLootModifier = doSandboxSettings(SandboxOptions.getInstance().CannedFoodLoot.getValue());
        LiteratureLootModifier = doSandboxSettings(SandboxOptions.getInstance().LiteratureLoot.getValue());
        SurvivalGearsLootModifier = doSandboxSettings(SandboxOptions.getInstance().SurvivalGearsLoot.getValue());
        MedicalLootModifier = doSandboxSettings(SandboxOptions.getInstance().MedicalLoot.getValue());
        MechanicsLootModifier = doSandboxSettings(SandboxOptions.getInstance().MechanicsLoot.getValue());
    }

    private static float doSandboxSettings(int int0) {
        switch (int0) {
            case 1:
                return 0.0F;
            case 2:
                return 0.05F;
            case 3:
                return 0.2F;
            case 4:
                return 0.6F;
            case 5:
                return 1.0F;
            case 6:
                return 2.0F;
            case 7:
                return 3.0F;
            default:
                return 0.6F;
        }
    }

    public static void fillContainer(ItemContainer container, IsoPlayer _player) {
        if (!GameClient.bClient && !"Tutorial".equals(Core.GameMode)) {
            if (container != null) {
                IsoGridSquare square = container.getSourceGrid();
                IsoRoom room = null;
                if (square != null) {
                    room = square.getRoom();
                    if (!container.getType().equals("inventorymale") && !container.getType().equals("inventoryfemale")) {
                        ItemPickerJava.ItemPickerRoom itemPickerRoom0 = null;
                        if (rooms.containsKey("all")) {
                            itemPickerRoom0 = rooms.get("all");
                        }

                        if (room != null && rooms.containsKey(room.getName())) {
                            String string0 = room.getName();
                            ItemPickerJava.ItemPickerRoom itemPickerRoom1 = rooms.get(string0);
                            ItemPickerJava.ItemPickerContainer itemPickerContainer0 = null;
                            if (itemPickerRoom1.Containers.containsKey(container.getType())) {
                                itemPickerContainer0 = itemPickerRoom1.Containers.get(container.getType());
                            }

                            if (itemPickerContainer0 == null && itemPickerRoom1.Containers.containsKey("other")) {
                                itemPickerContainer0 = itemPickerRoom1.Containers.get("other");
                            }

                            if (itemPickerContainer0 == null && itemPickerRoom1.Containers.containsKey("all")) {
                                itemPickerContainer0 = itemPickerRoom1.Containers.get("all");
                                string0 = "all";
                            }

                            if (itemPickerContainer0 == null) {
                                fillContainerType(itemPickerRoom0, container, string0, _player);
                                LuaEventManager.triggerEvent("OnFillContainer", string0, container.getType(), container);
                            } else {
                                if (rooms.containsKey(room.getName())) {
                                    itemPickerRoom0 = rooms.get(room.getName());
                                }

                                if (itemPickerRoom0 != null) {
                                    fillContainerType(itemPickerRoom0, container, room.getName(), _player);
                                    LuaEventManager.triggerEvent("OnFillContainer", room.getName(), container.getType(), container);
                                }
                            }
                        } else {
                            Object object = null;
                            if (room != null) {
                                object = room.getName();
                            } else {
                                object = "all";
                            }

                            fillContainerType(itemPickerRoom0, container, (String)object, _player);
                            LuaEventManager.triggerEvent("OnFillContainer", object, container.getType(), container);
                        }
                    } else {
                        String string1 = container.getType();
                        if (container.getParent() != null && container.getParent() instanceof IsoDeadBody) {
                            string1 = ((IsoDeadBody)container.getParent()).getOutfitName();
                        }

                        for (int int0 = 0; int0 < container.getItems().size(); int0++) {
                            if (container.getItems().get(int0) instanceof InventoryContainer) {
                                ItemPickerJava.ItemPickerContainer itemPickerContainer1 = containers.get(container.getItems().get(int0).getType());
                                if (itemPickerContainer1 != null && Rand.Next(itemPickerContainer1.fillRand) == 0) {
                                    rollContainerItem(
                                        (InventoryContainer)container.getItems().get(int0), null, containers.get(container.getItems().get(int0).getType())
                                    );
                                }
                            }
                        }

                        ItemPickerJava.ItemPickerContainer itemPickerContainer2 = rooms.get("all").Containers.get("Outfit_" + string1);
                        if (itemPickerContainer2 == null) {
                            itemPickerContainer2 = rooms.get("all").Containers.get(container.getType());
                        }

                        rollItem(itemPickerContainer2, container, true, _player, null);
                    }
                }
            }
        }
    }

    public static void fillContainerType(ItemPickerJava.ItemPickerRoom roomDist, ItemContainer container, String roomName, IsoGameCharacter character) {
        boolean boolean0 = true;
        if (NoContainerFillRooms.contains(roomName)) {
            boolean0 = false;
        }

        Object object = null;
        if (roomDist.Containers.containsKey("all")) {
            object = roomDist.Containers.get("all");
            rollItem((ItemPickerJava.ItemPickerContainer)object, container, boolean0, character, roomDist);
        }

        object = roomDist.Containers.get(container.getType());
        if (object == null) {
            object = roomDist.Containers.get("other");
        }

        if (object != null) {
            rollItem((ItemPickerJava.ItemPickerContainer)object, container, boolean0, character, roomDist);
        }
    }

    public static InventoryItem tryAddItemToContainer(ItemContainer container, String itemType, ItemPickerJava.ItemPickerContainer containerDist) {
        Item item = ScriptManager.instance.FindItem(itemType);
        if (item == null) {
            return null;
        } else if (item.OBSOLETE) {
            return null;
        } else {
            float float0 = item.getActualWeight() * item.getCount();
            if (!container.hasRoomFor(null, float0)) {
                return null;
            } else {
                if (container.getContainingItem() instanceof InventoryContainer) {
                    ItemContainer _container = container.getContainingItem().getContainer();
                    if (_container != null && !_container.hasRoomFor(null, float0)) {
                        return null;
                    }
                }

                return container.AddItem(itemType);
            }
        }
    }

    private static void rollProceduralItem(
        ArrayList<ItemPickerJava.ProceduralItem> arrayList0,
        ItemContainer container,
        float float0,
        IsoGameCharacter character,
        ItemPickerJava.ItemPickerRoom itemPickerRoom
    ) {
        if (container.getSourceGrid() != null && container.getSourceGrid().getRoom() != null) {
            HashMap hashMap0 = container.getSourceGrid().getRoom().getRoomDef().getProceduralSpawnedContainer();
            HashMap hashMap1 = new HashMap();
            HashMap hashMap2 = new HashMap();

            for (int int0 = 0; int0 < arrayList0.size(); int0++) {
                ItemPickerJava.ProceduralItem proceduralItem = (ItemPickerJava.ProceduralItem)arrayList0.get(int0);
                String string0 = proceduralItem.name;
                int int1 = proceduralItem.min;
                int int2 = proceduralItem.max;
                int int3 = proceduralItem.weightChance;
                List list0 = proceduralItem.forceForItems;
                List list1 = proceduralItem.forceForZones;
                List list2 = proceduralItem.forceForTiles;
                List list3 = proceduralItem.forceForRooms;
                if (hashMap0.get(string0) == null) {
                    hashMap0.put(string0, 0);
                }

                if (list0 != null) {
                    for (int int4 = container.getSourceGrid().getRoom().getRoomDef().x; int4 < container.getSourceGrid().getRoom().getRoomDef().x2; int4++) {
                        for (int int5 = container.getSourceGrid().getRoom().getRoomDef().y; int5 < container.getSourceGrid().getRoom().getRoomDef().y2; int5++) {
                            IsoGridSquare square0 = container.getSourceGrid().getCell().getGridSquare(int4, int5, container.getSourceGrid().z);
                            if (square0 != null) {
                                for (int int6 = 0; int6 < square0.getObjects().size(); int6++) {
                                    IsoObject object0 = square0.getObjects().get(int6);
                                    if (list0.contains(object0.getSprite().name)) {
                                        hashMap1.clear();
                                        hashMap1.put(string0, -1);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else if (list1 == null) {
                    if (list2 != null) {
                        IsoGridSquare square1 = container.getSourceGrid();
                        if (square1 != null) {
                            for (int int7 = 0; int7 < square1.getObjects().size(); int7++) {
                                IsoObject object1 = square1.getObjects().get(int7);
                                if (object1.getSprite() != null && list2.contains(object1.getSprite().getName())) {
                                    hashMap1.clear();
                                    hashMap1.put(string0, -1);
                                    break;
                                }
                            }
                        }
                    } else if (list3 != null) {
                        IsoGridSquare square2 = container.getSourceGrid();
                        if (square2 != null) {
                            for (int int8 = 0; int8 < list3.size(); int8++) {
                                if (square2.getBuilding().getRandomRoom((String)list3.get(int8)) != null) {
                                    hashMap1.clear();
                                    hashMap1.put(string0, -1);
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    ArrayList arrayList1 = IsoWorld.instance.MetaGrid.getZonesAt(container.getSourceGrid().x, container.getSourceGrid().y, 0);

                    for (int int9 = 0; int9 < arrayList1.size(); int9++) {
                        if ((Integer)hashMap0.get(string0) < int2
                            && (list1.contains(((IsoMetaGrid.Zone)arrayList1.get(int9)).type) || list1.contains(((IsoMetaGrid.Zone)arrayList1.get(int9)).name))
                            )
                         {
                            hashMap1.clear();
                            hashMap1.put(string0, -1);
                            break;
                        }
                    }
                }

                if (list0 == null && list1 == null && list2 == null && list3 == null) {
                    if (int1 == 1 && (Integer)hashMap0.get(string0) == 0) {
                        hashMap1.put(string0, int3);
                    } else if ((Integer)hashMap0.get(string0) < int2) {
                        hashMap2.put(string0, int3);
                    }
                }
            }

            String string1 = null;
            if (!hashMap1.isEmpty()) {
                string1 = getDistribInHashMap(hashMap1);
            } else if (!hashMap2.isEmpty()) {
                string1 = getDistribInHashMap(hashMap2);
            }

            if (string1 != null) {
                ItemPickerJava.ItemPickerContainer itemPickerContainer = ProceduralDistributions.get(string1);
                if (itemPickerContainer != null) {
                    if (itemPickerContainer.junk != null) {
                        doRollItem(itemPickerContainer.junk, container, float0, character, true, true, itemPickerRoom);
                    }

                    doRollItem(itemPickerContainer, container, float0, character, true, false, itemPickerRoom);
                    hashMap0.put(string1, (Integer)hashMap0.get(string1) + 1);
                }
            }
        }
    }

    private static String getDistribInHashMap(HashMap<String, Integer> hashMap) {
        int int0 = 0;
        int int1 = 0;

        for (String string0 : hashMap.keySet()) {
            int0 += hashMap.get(string0);
        }

        if (int0 == -1) {
            int int2 = Rand.Next(hashMap.size());
            Iterator iterator = hashMap.keySet().iterator();

            for (int int3 = 0; iterator.hasNext(); int3++) {
                if (int3 == int2) {
                    return (String)iterator.next();
                }
            }
        }

        int int4 = Rand.Next(int0);

        for (String string1 : hashMap.keySet()) {
            int int5 = (Integer)hashMap.get(string1);
            int1 += int5;
            if (int1 >= int4) {
                return string1;
            }
        }

        return null;
    }

    public static void rollItem(
        ItemPickerJava.ItemPickerContainer containerDist,
        ItemContainer container,
        boolean doItemContainer,
        IsoGameCharacter character,
        ItemPickerJava.ItemPickerRoom roomDist
    ) {
        if (!GameClient.bClient && !GameServer.bServer) {
            player = IsoPlayer.getInstance();
        }

        if (containerDist != null && container != null) {
            float float0 = 0.0F;
            IsoMetaChunk metaChunk = null;
            if (player != null && IsoWorld.instance != null) {
                metaChunk = IsoWorld.instance.getMetaChunk((int)player.getX() / 10, (int)player.getY() / 10);
            } else if (container.getSourceGrid() != null) {
                metaChunk = IsoWorld.instance.getMetaChunk(container.getSourceGrid().getX() / 10, container.getSourceGrid().getY() / 10);
            }

            if (metaChunk != null) {
                float0 = metaChunk.getLootZombieIntensity();
            }

            if (float0 > zombieDensityCap) {
                float0 = zombieDensityCap;
            }

            if (containerDist.ignoreZombieDensity) {
                float0 = 0.0F;
            }

            if (containerDist.procedural) {
                rollProceduralItem(containerDist.proceduralItems, container, float0, character, roomDist);
            } else {
                if (containerDist.junk != null) {
                    doRollItem(containerDist.junk, container, float0, character, doItemContainer, true, roomDist);
                }

                doRollItem(containerDist, container, float0, character, doItemContainer, false, roomDist);
            }
        }
    }

    public static void doRollItem(
        ItemPickerJava.ItemPickerContainer containerDist,
        ItemContainer container,
        float zombieDensity,
        IsoGameCharacter character,
        boolean doItemContainer,
        boolean isJunk,
        ItemPickerJava.ItemPickerRoom roomDist
    ) {
        boolean boolean0 = false;
        boolean boolean1 = false;
        String string0 = "";
        if (player != null && character != null) {
            boolean0 = character.Traits.Lucky.isSet();
            boolean1 = character.Traits.Unlucky.isSet();
        }

        for (int int0 = 0; int0 < containerDist.rolls; int0++) {
            ItemPickerJava.ItemPickerItem[] itemPickerItems = containerDist.Items;

            for (int int1 = 0; int1 < itemPickerItems.length; int1++) {
                ItemPickerJava.ItemPickerItem itemPickerItem = itemPickerItems[int1];
                float float0 = itemPickerItem.chance;
                string0 = itemPickerItem.itemName;
                if (boolean0) {
                    float0 *= 1.1F;
                }

                if (boolean1) {
                    float0 *= 0.9F;
                }

                float float1 = getLootModifier(string0);
                if (float1 == 0.0F) {
                    return;
                }

                if (isJunk) {
                    zombieDensity = 0.0F;
                    float1 = 1.0F;
                    float0 = (float)(float0 * 1.4);
                }

                if (Rand.Next(10000) <= float0 * 100.0F * float1 + zombieDensity * 10.0F) {
                    InventoryItem item0 = tryAddItemToContainer(container, string0, containerDist);
                    if (item0 == null) {
                        return;
                    }

                    checkStashItem(item0, containerDist);
                    if (container.getType().equals("freezer") && item0 instanceof Food && ((Food)item0).isFreezing()) {
                        ((Food)item0).freeze();
                    }

                    if (item0 instanceof Key key) {
                        key.takeKeyId();
                        if (container.getSourceGrid() != null
                            && container.getSourceGrid().getBuilding() != null
                            && container.getSourceGrid().getBuilding().getDef() != null) {
                            int int2 = container.getSourceGrid().getBuilding().getDef().getKeySpawned();
                            if (int2 < 2) {
                                container.getSourceGrid().getBuilding().getDef().setKeySpawned(int2 + 1);
                            } else {
                                container.Remove(item0);
                            }
                        }
                    }

                    String string1 = item0.getScriptItem().getRecordedMediaCat();
                    if (string1 != null) {
                        RecordedMedia recordedMedia = ZomboidRadio.getInstance().getRecordedMedia();
                        MediaData mediaData = recordedMedia.getRandomFromCategory(string1);
                        if (mediaData == null) {
                            container.Remove(item0);
                            if ("Home-VHS".equalsIgnoreCase(string1)) {
                                mediaData = recordedMedia.getRandomFromCategory("Retail-VHS");
                                if (mediaData == null) {
                                    return;
                                }

                                item0 = container.AddItem("Base.VHS_Retail");
                                if (item0 == null) {
                                    return;
                                }

                                item0.setRecordedMediaData(mediaData);
                            }

                            return;
                        }

                        item0.setRecordedMediaData(mediaData);
                    }

                    if (WeaponUpgradeMap.containsKey(item0.getType())) {
                        DoWeaponUpgrade(item0);
                    }

                    if (!containerDist.noAutoAge) {
                        item0.setAutoAge();
                    }

                    boolean boolean2 = false;
                    if (roomDist != null) {
                        boolean2 = roomDist.isShop;
                    }

                    if (!boolean2 && Rand.Next(100) < 40 && item0 instanceof DrainableComboItem) {
                        float float2 = 1.0F / ((DrainableComboItem)item0).getUseDelta();
                        ((DrainableComboItem)item0).setUsedDelta(Rand.Next(1.0F, float2 - 1.0F) * ((DrainableComboItem)item0).getUseDelta());
                    }

                    if (!boolean2 && item0 instanceof HandWeapon && Rand.Next(100) < 40) {
                        item0.setCondition(Rand.Next(1, item0.getConditionMax()));
                    }

                    if (item0 instanceof HandWeapon && !containerDist.dontSpawnAmmo && Rand.Next(100) < 90) {
                        int int3 = 30;
                        HandWeapon weapon = (HandWeapon)item0;
                        if (Core.getInstance().getOptionReloadDifficulty() > 1 && !StringUtils.isNullOrEmpty(weapon.getMagazineType()) && Rand.Next(100) < 90) {
                            if (Rand.NextBool(3)) {
                                InventoryItem item1 = container.AddItem(weapon.getMagazineType());
                                if (Rand.NextBool(5)) {
                                    item1.setCurrentAmmoCount(Rand.Next(1, item1.getMaxAmmo()));
                                }

                                if (!Rand.NextBool(5)) {
                                    item1.setCurrentAmmoCount(item1.getMaxAmmo());
                                }
                            } else {
                                if (!StringUtils.isNullOrWhitespace(weapon.getMagazineType())) {
                                    weapon.setContainsClip(true);
                                }

                                if (Rand.NextBool(6)) {
                                    weapon.setCurrentAmmoCount(Rand.Next(1, weapon.getMaxAmmo()));
                                } else {
                                    int3 = Rand.Next(60, 100);
                                }
                            }

                            if (weapon.haveChamber()) {
                                weapon.setRoundChambered(true);
                            }
                        }

                        if (Core.getInstance().getOptionReloadDifficulty() == 1 || StringUtils.isNullOrEmpty(weapon.getMagazineType()) && Rand.Next(100) < 30) {
                            weapon.setCurrentAmmoCount(Rand.Next(1, weapon.getMaxAmmo()));
                            if (weapon.haveChamber()) {
                                weapon.setRoundChambered(true);
                            }
                        }

                        if (!StringUtils.isNullOrEmpty(weapon.getAmmoBox()) && Rand.Next(100) < int3) {
                            container.AddItem(weapon.getAmmoBox());
                        } else if (!StringUtils.isNullOrEmpty(weapon.getAmmoType()) && Rand.Next(100) < 50) {
                            container.AddItems(weapon.getAmmoType(), Rand.Next(1, 5));
                        }
                    }

                    if (item0 instanceof InventoryContainer && containers.containsKey(item0.getType())) {
                        ItemPickerJava.ItemPickerContainer itemPickerContainer = containers.get(item0.getType());
                        if (doItemContainer && Rand.Next(itemPickerContainer.fillRand) == 0) {
                            rollContainerItem((InventoryContainer)item0, character, containers.get(item0.getType()));
                        }
                    }
                }
            }
        }
    }

    private static void checkStashItem(InventoryItem item, ItemPickerJava.ItemPickerContainer itemPickerContainer) {
        if (itemPickerContainer.stashChance > 0 && item instanceof MapItem && !StringUtils.isNullOrEmpty(((MapItem)item).getMapID())) {
            item.setStashChance(itemPickerContainer.stashChance);
        }

        StashSystem.checkStashItem(item);
    }

    public static void rollContainerItem(InventoryContainer bag, IsoGameCharacter character, ItemPickerJava.ItemPickerContainer containerDist) {
        if (containerDist != null) {
            ItemContainer container = bag.getInventory();
            float float0 = 0.0F;
            IsoMetaChunk metaChunk = null;
            if (player != null && IsoWorld.instance != null) {
                metaChunk = IsoWorld.instance.getMetaChunk((int)player.getX() / 10, (int)player.getY() / 10);
            }

            if (metaChunk != null) {
                float0 = metaChunk.getLootZombieIntensity();
            }

            if (float0 > zombieDensityCap) {
                float0 = zombieDensityCap;
            }

            if (containerDist.ignoreZombieDensity) {
                float0 = 0.0F;
            }

            boolean boolean0 = false;
            boolean boolean1 = false;
            String string = "";
            if (player != null && character != null) {
                boolean0 = character.Traits.Lucky.isSet();
                boolean1 = character.Traits.Unlucky.isSet();
            }

            for (int int0 = 0; int0 < containerDist.rolls; int0++) {
                ItemPickerJava.ItemPickerItem[] itemPickerItems = containerDist.Items;

                for (int int1 = 0; int1 < itemPickerItems.length; int1++) {
                    ItemPickerJava.ItemPickerItem itemPickerItem = itemPickerItems[int1];
                    float float1 = itemPickerItem.chance;
                    string = itemPickerItem.itemName;
                    if (boolean0) {
                        float1 *= 1.1F;
                    }

                    if (boolean1) {
                        float1 *= 0.9F;
                    }

                    float float2 = getLootModifier(string);
                    if (Rand.Next(10000) <= float1 * 100.0F * float2 + float0 * 10.0F) {
                        InventoryItem item = tryAddItemToContainer(container, string, containerDist);
                        if (item == null) {
                            return;
                        }

                        MapItem mapItem0 = Type.tryCastTo(item, MapItem.class);
                        if (mapItem0 != null && !StringUtils.isNullOrEmpty(mapItem0.getMapID()) && containerDist.maxMap > 0) {
                            int int2 = 0;

                            for (int int3 = 0; int3 < container.getItems().size(); int3++) {
                                MapItem mapItem1 = Type.tryCastTo(container.getItems().get(int3), MapItem.class);
                                if (mapItem1 != null && !StringUtils.isNullOrEmpty(mapItem1.getMapID())) {
                                    int2++;
                                }
                            }

                            if (int2 > containerDist.maxMap) {
                                container.Remove(item);
                            }
                        }

                        checkStashItem(item, containerDist);
                        if (container.getType().equals("freezer") && item instanceof Food && ((Food)item).isFreezing()) {
                            ((Food)item).freeze();
                        }

                        if (item instanceof Key key) {
                            key.takeKeyId();
                            if (container.getSourceGrid() != null
                                && container.getSourceGrid().getBuilding() != null
                                && container.getSourceGrid().getBuilding().getDef() != null) {
                                int int4 = container.getSourceGrid().getBuilding().getDef().getKeySpawned();
                                if (int4 < 2) {
                                    container.getSourceGrid().getBuilding().getDef().setKeySpawned(int4 + 1);
                                } else {
                                    container.Remove(item);
                                }
                            }
                        }

                        if (!container.getType().equals("freezer")) {
                            item.setAutoAge();
                        }
                    }
                }
            }
        }
    }

    private static void DoWeaponUpgrade(InventoryItem item0) {
        ItemPickerJava.ItemPickerUpgradeWeapons itemPickerUpgradeWeapons = WeaponUpgradeMap.get(item0.getType());
        if (itemPickerUpgradeWeapons != null) {
            if (itemPickerUpgradeWeapons.Upgrades.size() != 0) {
                int int0 = Rand.Next(itemPickerUpgradeWeapons.Upgrades.size());

                for (int int1 = 0; int1 < int0; int1++) {
                    String string = PZArrayUtil.pickRandom(itemPickerUpgradeWeapons.Upgrades);
                    InventoryItem item1 = InventoryItemFactory.CreateItem(string);
                    ((HandWeapon)item0).attachWeaponPart((WeaponPart)item1);
                }
            }
        }
    }

    public static float getLootModifier(String itemname) {
        Item item = ScriptManager.instance.FindItem(itemname);
        if (item == null) {
            return 0.6F;
        } else {
            float float0 = OtherLootModifier;
            if (item.getType() == Item.Type.Food) {
                if (item.CannedFood) {
                    float0 = CannedFoodLootModifier;
                } else {
                    float0 = FoodLootModifier;
                }
            }

            if ("Ammo".equals(item.getDisplayCategory())) {
                float0 = AmmoLootModifier;
            }

            if (item.getType() == Item.Type.Weapon && !item.isRanged()) {
                float0 = WeaponLootModifier;
            }

            if (item.getType() == Item.Type.WeaponPart
                || item.getType() == Item.Type.Weapon && item.isRanged()
                || item.getType() == Item.Type.Normal && !StringUtils.isNullOrEmpty(item.getAmmoType())) {
                float0 = RangedWeaponLootModifier;
            }

            if (item.getType() == Item.Type.Literature) {
                float0 = LiteratureLootModifier;
            }

            if (item.Medical) {
                float0 = MedicalLootModifier;
            }

            if (item.SurvivalGear) {
                float0 = SurvivalGearsLootModifier;
            }

            if (item.MechanicsItem) {
                float0 = MechanicsLootModifier;
            }

            return float0;
        }
    }

    public static void updateOverlaySprite(IsoObject obj) {
        ContainerOverlays.instance.updateContainerOverlaySprite(obj);
    }

    public static void doOverlaySprite(IsoGridSquare sq) {
        if (!GameClient.bClient) {
            if (sq != null && sq.getRoom() != null && !sq.isOverlayDone()) {
                PZArrayList pZArrayList = sq.getObjects();

                for (int int0 = 0; int0 < pZArrayList.size(); int0++) {
                    IsoObject object = (IsoObject)pZArrayList.get(int0);
                    if (object != null && object.getContainer() != null && !object.getContainer().isExplored()) {
                        fillContainer(object.getContainer(), IsoPlayer.getInstance());
                        object.getContainer().setExplored(true);
                        if (GameServer.bServer) {
                            LuaManager.GlobalObject.sendItemsInContainer(object, object.getContainer());
                        }
                    }

                    updateOverlaySprite(object);
                }

                sq.setOverlayDone(true);
            }
        }
    }

    public static ItemPickerJava.ItemPickerContainer getItemContainer(String room, String container, String proceduralName, boolean junk) {
        ItemPickerJava.ItemPickerRoom itemPickerRoom = rooms.get(room);
        if (itemPickerRoom == null) {
            return null;
        } else {
            ItemPickerJava.ItemPickerContainer itemPickerContainer0 = itemPickerRoom.Containers.get(container);
            if (itemPickerContainer0 != null && itemPickerContainer0.procedural) {
                ArrayList arrayList = itemPickerContainer0.proceduralItems;

                for (int int0 = 0; int0 < arrayList.size(); int0++) {
                    ItemPickerJava.ProceduralItem proceduralItem = (ItemPickerJava.ProceduralItem)arrayList.get(int0);
                    if (proceduralName.equals(proceduralItem.name)) {
                        ItemPickerJava.ItemPickerContainer itemPickerContainer1 = ProceduralDistributions.get(proceduralName);
                        if (itemPickerContainer1.junk != null && junk) {
                            return itemPickerContainer1.junk;
                        }

                        if (!junk) {
                            return itemPickerContainer1;
                        }
                    }
                }
            }

            return junk ? itemPickerContainer0.junk : itemPickerContainer0;
        }
    }

    public static final class ItemPickerContainer {
        public ItemPickerJava.ItemPickerItem[] Items = new ItemPickerJava.ItemPickerItem[0];
        public float rolls;
        public boolean noAutoAge;
        public int fillRand;
        public int maxMap;
        public int stashChance;
        public ItemPickerJava.ItemPickerContainer junk;
        public boolean procedural;
        public boolean dontSpawnAmmo = false;
        public boolean ignoreZombieDensity = false;
        public ArrayList<ItemPickerJava.ProceduralItem> proceduralItems;
    }

    public static final class ItemPickerItem {
        public String itemName;
        public float chance;
    }

    public static final class ItemPickerRoom {
        public THashMap<String, ItemPickerJava.ItemPickerContainer> Containers = new THashMap<>();
        public int fillRand;
        public boolean isShop;
        public String specificId = null;
    }

    public static final class ItemPickerUpgradeWeapons {
        public String name;
        public ArrayList<String> Upgrades = new ArrayList<>();
    }

    public static final class ProceduralItem {
        public String name;
        public int min;
        public int max;
        public List<String> forceForItems;
        public List<String> forceForZones;
        public List<String> forceForTiles;
        public List<String> forceForRooms;
        public int weightChance;
    }

    public static final class VehicleDistribution {
        public ItemPickerJava.ItemPickerRoom Normal;
        public final ArrayList<ItemPickerJava.ItemPickerRoom> Specific = new ArrayList<>();
    }
}
