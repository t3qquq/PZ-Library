// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.Predicate;
import se.krka.kahlua.integration.LuaReturn;
import se.krka.kahlua.vm.LuaClosure;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.SystemDisabler;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorDesc;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.inventory.types.AlarmClock;
import zombie.inventory.types.AlarmClockClothing;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Key;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoBarbecue;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoFireplace;
import zombie.iso.objects.IsoMannequin;
import zombie.iso.objects.IsoStove;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.network.GameClient;
import zombie.network.PacketTypes;
import zombie.popman.ObjectPool;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclePart;

public final class ItemContainer {
    private static final ArrayList<InventoryItem> tempList = new ArrayList<>();
    private static final ArrayList<IsoObject> s_tempObjects = new ArrayList<>();
    public boolean active = false;
    private boolean dirty = true;
    public boolean IsDevice = false;
    public float ageFactor = 1.0F;
    public float CookingFactor = 1.0F;
    public int Capacity = 50;
    public InventoryItem containingItem = null;
    public ArrayList<InventoryItem> Items = new ArrayList<>();
    public ArrayList<InventoryItem> IncludingObsoleteItems = new ArrayList<>();
    public IsoObject parent = null;
    public IsoGridSquare SourceGrid = null;
    public VehiclePart vehiclePart = null;
    public InventoryContainer inventoryContainer = null;
    public boolean bExplored = false;
    public String type = "none";
    public int ID = 0;
    private boolean drawDirty = true;
    private float customTemperature = 0.0F;
    private boolean hasBeenLooted = false;
    private String openSound = null;
    private String closeSound = null;
    private String putSound = null;
    private String OnlyAcceptCategory = null;
    private String AcceptItemFunction = null;
    private int weightReduction = 0;
    private String containerPosition = null;
    private String freezerPosition = null;
    private static final ThreadLocal<ItemContainer.Comparators> TL_comparators = ThreadLocal.withInitial(ItemContainer.Comparators::new);
    private static final ThreadLocal<ItemContainer.InventoryItemListPool> TL_itemListPool = ThreadLocal.withInitial(ItemContainer.InventoryItemListPool::new);
    private static final ThreadLocal<ItemContainer.Predicates> TL_predicates = ThreadLocal.withInitial(ItemContainer.Predicates::new);

    public ItemContainer(int _ID, String containerName, IsoGridSquare square, IsoObject _parent) {
        this.ID = _ID;
        this.parent = _parent;
        this.type = containerName;
        this.SourceGrid = square;
        if (containerName.equals("fridge")) {
            this.ageFactor = 0.02F;
            this.CookingFactor = 0.0F;
        }
    }

    public ItemContainer(String containerName, IsoGridSquare square, IsoObject _parent) {
        this.ID = -1;
        this.parent = _parent;
        this.type = containerName;
        this.SourceGrid = square;
        if (containerName.equals("fridge")) {
            this.ageFactor = 0.02F;
            this.CookingFactor = 0.0F;
        }
    }

    public ItemContainer(int _ID) {
        this.ID = _ID;
    }

    public ItemContainer() {
        this.ID = -1;
    }

    public static float floatingPointCorrection(float val) {
        byte byte0 = 100;
        float float0 = val * byte0;
        return (float)((int)(float0 - (int)float0 >= 0.5F ? float0 + 1.0F : float0)) / byte0;
    }

    public int getCapacity() {
        return this.Capacity;
    }

    public void setCapacity(int int0) {
        this.Capacity = int0;
    }

    public InventoryItem FindAndReturnWaterItem(int uses) {
        for (int int0 = 0; int0 < this.getItems().size(); int0++) {
            InventoryItem item = this.getItems().get(int0);
            if (item instanceof DrainableComboItem && item.isWaterSource()) {
                DrainableComboItem drainableComboItem = (DrainableComboItem)item;
                if (drainableComboItem.getDrainableUsesInt() >= uses) {
                    return item;
                }
            }
        }

        return null;
    }

    public InventoryItem getItemFromTypeRecurse(String _type) {
        return this.getFirstTypeRecurse(_type);
    }

    public int getEffectiveCapacity(IsoGameCharacter chr) {
        if (chr != null && !(this.parent instanceof IsoGameCharacter) && !(this.parent instanceof IsoDeadBody) && !"floor".equals(this.getType())) {
            if (chr.Traits.Organized.isSet()) {
                return (int)Math.max(this.Capacity * 1.3F, (float)(this.Capacity + 1));
            }

            if (chr.Traits.Disorganized.isSet()) {
                return (int)Math.max(this.Capacity * 0.7F, 1.0F);
            }
        }

        return this.Capacity;
    }

    public boolean hasRoomFor(IsoGameCharacter chr, InventoryItem item) {
        if (this.vehiclePart != null && this.vehiclePart.getId().contains("Seat") && this.Items.isEmpty() && item.getUnequippedWeight() <= 50.0F) {
            return true;
        } else if (floatingPointCorrection(this.getCapacityWeight()) + item.getUnequippedWeight() <= this.getEffectiveCapacity(chr)) {
            return this.getContainingItem() != null
                    && this.getContainingItem().getEquipParent() != null
                    && this.getContainingItem().getEquipParent().getInventory() != null
                    && !this.getContainingItem().getEquipParent().getInventory().contains(item)
                ? floatingPointCorrection(this.getContainingItem().getEquipParent().getInventory().getCapacityWeight()) + item.getUnequippedWeight()
                    <= this.getContainingItem().getEquipParent().getInventory().getEffectiveCapacity(chr)
                : true;
        } else {
            return false;
        }
    }

    public boolean hasRoomFor(IsoGameCharacter chr, float weightVal) {
        return floatingPointCorrection(this.getCapacityWeight()) + weightVal <= this.getEffectiveCapacity(chr);
    }

    public boolean isItemAllowed(InventoryItem item) {
        if (item == null) {
            return false;
        } else {
            String string0 = this.getOnlyAcceptCategory();
            if (string0 != null && !string0.equalsIgnoreCase(item.getCategory())) {
                return false;
            } else {
                String string1 = this.getAcceptItemFunction();
                if (string1 != null) {
                    Object object = LuaManager.getFunctionObject(string1);
                    if (object != null) {
                        Boolean boolean0 = LuaManager.caller.protectedCallBoolean(LuaManager.thread, object, this, item);
                        if (boolean0 != Boolean.TRUE) {
                            return false;
                        }
                    }
                }

                if (this.parent != null && !this.parent.isItemAllowedInContainer(this, item)) {
                    return false;
                } else if (this.getType().equals("clothingrack") && !(item instanceof Clothing)) {
                    return false;
                } else if (this.getParent() != null
                    && this.getParent().getProperties() != null
                    && this.getParent().getProperties().Val("CustomName") != null
                    && this.getParent().getProperties().Val("CustomName").equals("Toaster")
                    && !item.hasTag("FitsToaster")) {
                    return false;
                } else {
                    if (this.getParent() != null && this.getParent().getProperties() != null && this.getParent().getProperties().Val("GroupName") != null) {
                        boolean boolean1 = this.getParent().getProperties().Val("GroupName").equals("Coffee")
                            || this.getParent().getProperties().Val("GroupName").equals("Espresso");
                        if (boolean1 && !item.hasTag("CoffeeMaker")) {
                            return false;
                        }
                    }

                    return true;
                }
            }
        }
    }

    public boolean isRemoveItemAllowed(InventoryItem item) {
        return item == null ? false : this.parent == null || this.parent.isRemoveItemAllowedFromContainer(this, item);
    }

    public boolean isExplored() {
        return this.bExplored;
    }

    public void setExplored(boolean b) {
        this.bExplored = b;
    }

    public boolean isInCharacterInventory(IsoGameCharacter chr) {
        if (chr.getInventory() == this) {
            return true;
        } else {
            if (this.containingItem != null) {
                if (chr.getInventory().contains(this.containingItem, true)) {
                    return true;
                }

                if (this.containingItem.getContainer() != null) {
                    return this.containingItem.getContainer().isInCharacterInventory(chr);
                }
            }

            return false;
        }
    }

    public boolean isInside(InventoryItem item) {
        if (this.containingItem == null) {
            return false;
        } else {
            return this.containingItem == item ? true : this.containingItem.getContainer() != null && this.containingItem.getContainer().isInside(item);
        }
    }

    public InventoryItem getContainingItem() {
        return this.containingItem;
    }

    public InventoryItem DoAddItem(InventoryItem item) {
        return this.AddItem(item);
    }

    public InventoryItem DoAddItemBlind(InventoryItem item) {
        return this.AddItem(item);
    }

    public ArrayList<InventoryItem> AddItems(String item, int use) {
        ArrayList arrayList = new ArrayList();

        for (int int0 = 0; int0 < use; int0++) {
            InventoryItem _item = this.AddItem(item);
            if (_item != null) {
                arrayList.add(_item);
            }
        }

        return arrayList;
    }

    public void AddItems(InventoryItem item, int use) {
        for (int int0 = 0; int0 < use; int0++) {
            this.AddItem(item.getFullType());
        }
    }

    public int getNumberOfItem(String findItem, boolean includeReplaceOnDeplete) {
        return this.getNumberOfItem(findItem, includeReplaceOnDeplete, false);
    }

    public int getNumberOfItem(String findItem) {
        return this.getNumberOfItem(findItem, false);
    }

    public int getNumberOfItem(String findItem, boolean includeReplaceOnDeplete, ArrayList<ItemContainer> containers) {
        int int0 = this.getNumberOfItem(findItem, includeReplaceOnDeplete);
        if (containers != null) {
            for (ItemContainer container : containers) {
                if (container != this) {
                    int0 += container.getNumberOfItem(findItem, includeReplaceOnDeplete);
                }
            }
        }

        return int0;
    }

    public int getNumberOfItem(String findItem, boolean includeReplaceOnDeplete, boolean insideInv) {
        int int0 = 0;

        for (int int1 = 0; int1 < this.Items.size(); int1++) {
            InventoryItem item = this.Items.get(int1);
            if (item.getFullType().equals(findItem) || item.getType().equals(findItem)) {
                int0++;
            } else if (insideInv && item instanceof InventoryContainer) {
                int0 += ((InventoryContainer)item).getItemContainer().getNumberOfItem(findItem);
            } else if (includeReplaceOnDeplete && item instanceof DrainableComboItem && ((DrainableComboItem)item).getReplaceOnDeplete() != null) {
                DrainableComboItem drainableComboItem = (DrainableComboItem)item;
                if (drainableComboItem.getReplaceOnDepleteFullType().equals(findItem) || drainableComboItem.getReplaceOnDeplete().equals(findItem)) {
                    int0++;
                }
            }
        }

        return int0;
    }

    public InventoryItem addItem(InventoryItem item) {
        return this.AddItem(item);
    }

    public InventoryItem AddItem(InventoryItem item) {
        if (item == null) {
            return null;
        } else if (this.containsID(item.id)) {
            System.out.println("Error, container already has id");
            return this.getItemWithID(item.id);
        } else {
            this.drawDirty = true;
            if (this.parent != null) {
                this.dirty = true;
            }

            if (this.parent != null && !(this.parent instanceof IsoGameCharacter)) {
                this.parent.DirtySlice();
            }

            if (item.container != null) {
                item.container.Remove(item);
            }

            item.container = this;
            this.Items.add(item);
            if (IsoWorld.instance.CurrentCell != null) {
                IsoWorld.instance.CurrentCell.addToProcessItems(item);
            }

            return item;
        }
    }

    public InventoryItem AddItemBlind(InventoryItem item) {
        if (item == null) {
            return null;
        } else if (item.getWeight() + this.getCapacityWeight() > this.getCapacity()) {
            return null;
        } else {
            if (this.parent != null && !(this.parent instanceof IsoGameCharacter)) {
                this.parent.DirtySlice();
            }

            this.Items.add(item);
            return item;
        }
    }

    public InventoryItem AddItem(String _type) {
        this.drawDirty = true;
        if (this.parent != null && !(this.parent instanceof IsoGameCharacter)) {
            this.dirty = true;
        }

        Item item0 = ScriptManager.instance.FindItem(_type);
        if (item0 == null) {
            DebugLog.log("ERROR: ItemContainer.AddItem: can't find " + _type);
            return null;
        } else if (item0.OBSOLETE) {
            return null;
        } else {
            InventoryItem item1 = null;
            int int0 = item0.getCount();

            for (int int1 = 0; int1 < int0; int1++) {
                item1 = InventoryItemFactory.CreateItem(_type);
                if (item1 == null) {
                    return null;
                }

                item1.container = this;
                this.Items.add(item1);
                if (item1 instanceof Food) {
                    ((Food)item1).setHeat(this.getTemprature());
                }

                if (IsoWorld.instance.CurrentCell != null) {
                    IsoWorld.instance.CurrentCell.addToProcessItems(item1);
                }
            }

            return item1;
        }
    }

    public boolean AddItem(String _type, float useDelta) {
        this.drawDirty = true;
        if (this.parent != null && !(this.parent instanceof IsoGameCharacter)) {
            this.dirty = true;
        }

        InventoryItem item = InventoryItemFactory.CreateItem(_type);
        if (item == null) {
            return false;
        } else {
            if (item instanceof Drainable) {
                ((Drainable)item).setUsedDelta(useDelta);
            }

            item.container = this;
            this.Items.add(item);
            return true;
        }
    }

    public boolean contains(InventoryItem item) {
        return this.Items.contains(item);
    }

    public boolean containsWithModule(String moduleType) {
        return this.containsWithModule(moduleType, false);
    }

    public boolean containsWithModule(String moduleType, boolean withDeltaLeft) {
        String string0 = moduleType;
        String string1 = "Base";
        if (moduleType.contains(".")) {
            string1 = moduleType.split("\\.")[0];
            string0 = moduleType.split("\\.")[1];
        }

        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if (item == null) {
                this.Items.remove(int0);
                int0--;
            } else if (item.type.equals(string0.trim())
                && string1.equals(item.getModule())
                && (!withDeltaLeft || !(item instanceof DrainableComboItem) || !(((DrainableComboItem)item).getUsedDelta() <= 0.0F))) {
                return true;
            }
        }

        return false;
    }

    public void removeItemOnServer(InventoryItem item) {
        if (GameClient.bClient) {
            if (this.containingItem != null && this.containingItem.getWorldItem() != null) {
                GameClient.instance.addToItemRemoveSendBuffer(this.containingItem.getWorldItem(), this, item);
            } else {
                GameClient.instance.addToItemRemoveSendBuffer(this.parent, this, item);
            }
        }
    }

    public void addItemOnServer(InventoryItem item) {
        if (GameClient.bClient) {
            if (this.containingItem != null && this.containingItem.getWorldItem() != null) {
                GameClient.instance.addToItemSendBuffer(this.containingItem.getWorldItem(), this, item);
            } else {
                GameClient.instance.addToItemSendBuffer(this.parent, this, item);
            }
        }
    }

    public boolean contains(InventoryItem itemToFind, boolean doInv) {
        ItemContainer.InventoryItemList inventoryItemList = TL_itemListPool.get().alloc();

        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if (item == null) {
                this.Items.remove(int0);
                int0--;
            } else {
                if (item == itemToFind) {
                    TL_itemListPool.get().release(inventoryItemList);
                    return true;
                }

                if (doInv && item instanceof InventoryContainer && ((InventoryContainer)item).getInventory() != null && !inventoryItemList.contains(item)) {
                    inventoryItemList.add(item);
                }
            }
        }

        for (int int1 = 0; int1 < inventoryItemList.size(); int1++) {
            ItemContainer container = ((InventoryContainer)inventoryItemList.get(int1)).getInventory();
            if (container.contains(itemToFind, doInv)) {
                TL_itemListPool.get().release(inventoryItemList);
                return true;
            }
        }

        TL_itemListPool.get().release(inventoryItemList);
        return false;
    }

    public boolean contains(String _type, boolean doInv) {
        return this.contains(_type, doInv, false);
    }

    public boolean containsType(String _type) {
        return this.contains(_type, false, false);
    }

    public boolean containsTypeRecurse(String _type) {
        return this.contains(_type, true, false);
    }

    private boolean testBroken(boolean boolean0, InventoryItem item) {
        return !boolean0 ? true : !item.isBroken();
    }

    public boolean contains(String _type, boolean doInv, boolean ignoreBroken) {
        ItemContainer.InventoryItemList inventoryItemList = TL_itemListPool.get().alloc();
        if (_type.contains("Type:")) {
            for (int int0 = 0; int0 < this.Items.size(); int0++) {
                InventoryItem item0 = this.Items.get(int0);
                if (_type.contains("Food") && item0 instanceof Food) {
                    TL_itemListPool.get().release(inventoryItemList);
                    return true;
                }

                if (_type.contains("Weapon") && item0 instanceof HandWeapon && this.testBroken(ignoreBroken, item0)) {
                    TL_itemListPool.get().release(inventoryItemList);
                    return true;
                }

                if (_type.contains("AlarmClock") && item0 instanceof AlarmClock) {
                    TL_itemListPool.get().release(inventoryItemList);
                    return true;
                }

                if (_type.contains("AlarmClockClothing") && item0 instanceof AlarmClockClothing) {
                    TL_itemListPool.get().release(inventoryItemList);
                    return true;
                }

                if (doInv && item0 instanceof InventoryContainer && ((InventoryContainer)item0).getInventory() != null && !inventoryItemList.contains(item0)) {
                    inventoryItemList.add(item0);
                }
            }
        } else if (_type.contains("/")) {
            String[] strings = _type.split("/");

            for (String string : strings) {
                for (int int1 = 0; int1 < this.Items.size(); int1++) {
                    InventoryItem item1 = this.Items.get(int1);
                    if (compareType(string.trim(), item1) && this.testBroken(ignoreBroken, item1)) {
                        TL_itemListPool.get().release(inventoryItemList);
                        return true;
                    }

                    if (doInv
                        && item1 instanceof InventoryContainer
                        && ((InventoryContainer)item1).getInventory() != null
                        && !inventoryItemList.contains(item1)) {
                        inventoryItemList.add(item1);
                    }
                }
            }
        } else {
            for (int int2 = 0; int2 < this.Items.size(); int2++) {
                InventoryItem item2 = this.Items.get(int2);
                if (item2 == null) {
                    this.Items.remove(int2);
                    int2--;
                } else {
                    if (compareType(_type.trim(), item2) && this.testBroken(ignoreBroken, item2)) {
                        TL_itemListPool.get().release(inventoryItemList);
                        return true;
                    }

                    if (doInv
                        && item2 instanceof InventoryContainer
                        && ((InventoryContainer)item2).getInventory() != null
                        && !inventoryItemList.contains(item2)) {
                        inventoryItemList.add(item2);
                    }
                }
            }
        }

        for (int int3 = 0; int3 < inventoryItemList.size(); int3++) {
            ItemContainer container = ((InventoryContainer)inventoryItemList.get(int3)).getInventory();
            if (container.contains(_type, doInv, ignoreBroken)) {
                TL_itemListPool.get().release(inventoryItemList);
                return true;
            }
        }

        TL_itemListPool.get().release(inventoryItemList);
        return false;
    }

    public boolean contains(String _type) {
        return this.contains(_type, false);
    }

    private static InventoryItem getBestOf(ItemContainer.InventoryItemList inventoryItemList, Comparator<InventoryItem> comparator) {
        if (inventoryItemList != null && !inventoryItemList.isEmpty()) {
            InventoryItem item0 = inventoryItemList.get(0);

            for (int int0 = 1; int0 < inventoryItemList.size(); int0++) {
                InventoryItem item1 = inventoryItemList.get(int0);
                if (comparator.compare(item1, item0) > 0) {
                    item0 = item1;
                }
            }

            return item0;
        } else {
            return null;
        }
    }

    public InventoryItem getBest(Predicate<InventoryItem> predicate, Comparator<InventoryItem> comparator) {
        ItemContainer.InventoryItemList inventoryItemList = TL_itemListPool.get().alloc();
        this.getAll(predicate, inventoryItemList);
        InventoryItem item = getBestOf(inventoryItemList, comparator);
        TL_itemListPool.get().release(inventoryItemList);
        return item;
    }

    public InventoryItem getBestRecurse(Predicate<InventoryItem> predicate, Comparator<InventoryItem> comparator) {
        ItemContainer.InventoryItemList inventoryItemList = TL_itemListPool.get().alloc();
        this.getAllRecurse(predicate, inventoryItemList);
        InventoryItem item = getBestOf(inventoryItemList, comparator);
        TL_itemListPool.get().release(inventoryItemList);
        return item;
    }

    public InventoryItem getBestType(String _type, Comparator<InventoryItem> comparator) {
        ItemContainer.TypePredicate typePredicate = TL_predicates.get().type.alloc().init(_type);

        InventoryItem item;
        try {
            item = this.getBest(typePredicate, comparator);
        } finally {
            TL_predicates.get().type.release(typePredicate);
        }

        return item;
    }

    public InventoryItem getBestTypeRecurse(String _type, Comparator<InventoryItem> comparator) {
        ItemContainer.TypePredicate typePredicate = TL_predicates.get().type.alloc().init(_type);

        InventoryItem item;
        try {
            item = this.getBestRecurse(typePredicate, comparator);
        } finally {
            TL_predicates.get().type.release(typePredicate);
        }

        return item;
    }

    public InventoryItem getBestEval(LuaClosure predicateObj, LuaClosure comparatorObj) {
        ItemContainer.EvalPredicate evalPredicate = TL_predicates.get().eval.alloc().init(predicateObj);
        ItemContainer.EvalComparator evalComparator = TL_comparators.get().eval.alloc().init(comparatorObj);

        InventoryItem item;
        try {
            item = this.getBest(evalPredicate, evalComparator);
        } finally {
            TL_predicates.get().eval.release(evalPredicate);
            TL_comparators.get().eval.release(evalComparator);
        }

        return item;
    }

    public InventoryItem getBestEvalRecurse(LuaClosure predicateObj, LuaClosure comparatorObj) {
        ItemContainer.EvalPredicate evalPredicate = TL_predicates.get().eval.alloc().init(predicateObj);
        ItemContainer.EvalComparator evalComparator = TL_comparators.get().eval.alloc().init(comparatorObj);

        InventoryItem item;
        try {
            item = this.getBestRecurse(evalPredicate, evalComparator);
        } finally {
            TL_predicates.get().eval.release(evalPredicate);
            TL_comparators.get().eval.release(evalComparator);
        }

        return item;
    }

    public InventoryItem getBestEvalArg(LuaClosure predicateObj, LuaClosure comparatorObj, Object arg) {
        ItemContainer.EvalArgPredicate evalArgPredicate = TL_predicates.get().evalArg.alloc().init(predicateObj, arg);
        ItemContainer.EvalArgComparator evalArgComparator = TL_comparators.get().evalArg.alloc().init(comparatorObj, arg);

        InventoryItem item;
        try {
            item = this.getBest(evalArgPredicate, evalArgComparator);
        } finally {
            TL_predicates.get().evalArg.release(evalArgPredicate);
            TL_comparators.get().evalArg.release(evalArgComparator);
        }

        return item;
    }

    public InventoryItem getBestEvalArgRecurse(LuaClosure predicateObj, LuaClosure comparatorObj, Object arg) {
        ItemContainer.EvalArgPredicate evalArgPredicate = TL_predicates.get().evalArg.alloc().init(predicateObj, arg);
        ItemContainer.EvalArgComparator evalArgComparator = TL_comparators.get().evalArg.alloc().init(comparatorObj, arg);

        InventoryItem item;
        try {
            item = this.getBestRecurse(evalArgPredicate, evalArgComparator);
        } finally {
            TL_predicates.get().evalArg.release(evalArgPredicate);
            TL_comparators.get().evalArg.release(evalArgComparator);
        }

        return item;
    }

    public InventoryItem getBestTypeEval(String _type, LuaClosure comparatorObj) {
        ItemContainer.TypePredicate typePredicate = TL_predicates.get().type.alloc().init(_type);
        ItemContainer.EvalComparator evalComparator = TL_comparators.get().eval.alloc().init(comparatorObj);

        InventoryItem item;
        try {
            item = this.getBest(typePredicate, evalComparator);
        } finally {
            TL_predicates.get().type.release(typePredicate);
            TL_comparators.get().eval.release(evalComparator);
        }

        return item;
    }

    public InventoryItem getBestTypeEvalRecurse(String _type, LuaClosure comparatorObj) {
        ItemContainer.TypePredicate typePredicate = TL_predicates.get().type.alloc().init(_type);
        ItemContainer.EvalComparator evalComparator = TL_comparators.get().eval.alloc().init(comparatorObj);

        InventoryItem item;
        try {
            item = this.getBestRecurse(typePredicate, evalComparator);
        } finally {
            TL_predicates.get().type.release(typePredicate);
            TL_comparators.get().eval.release(evalComparator);
        }

        return item;
    }

    public InventoryItem getBestTypeEvalArg(String _type, LuaClosure comparatorObj, Object arg) {
        ItemContainer.TypePredicate typePredicate = TL_predicates.get().type.alloc().init(_type);
        ItemContainer.EvalArgComparator evalArgComparator = TL_comparators.get().evalArg.alloc().init(comparatorObj, arg);

        InventoryItem item;
        try {
            item = this.getBest(typePredicate, evalArgComparator);
        } finally {
            TL_predicates.get().type.release(typePredicate);
            TL_comparators.get().evalArg.release(evalArgComparator);
        }

        return item;
    }

    public InventoryItem getBestTypeEvalArgRecurse(String _type, LuaClosure comparatorObj, Object arg) {
        ItemContainer.TypePredicate typePredicate = TL_predicates.get().type.alloc().init(_type);
        ItemContainer.EvalArgComparator evalArgComparator = TL_comparators.get().evalArg.alloc().init(comparatorObj, arg);

        InventoryItem item;
        try {
            item = this.getBestRecurse(typePredicate, evalArgComparator);
        } finally {
            TL_predicates.get().type.release(typePredicate);
            TL_comparators.get().evalArg.release(evalArgComparator);
        }

        return item;
    }

    public InventoryItem getBestCondition(Predicate<InventoryItem> predicate) {
        ItemContainer.ConditionComparator conditionComparator = TL_comparators.get().condition.alloc();
        InventoryItem item = this.getBest(predicate, conditionComparator);
        TL_comparators.get().condition.release(conditionComparator);
        if (item != null && item.getCondition() <= 0) {
            item = null;
        }

        return item;
    }

    public InventoryItem getBestConditionRecurse(Predicate<InventoryItem> predicate) {
        ItemContainer.ConditionComparator conditionComparator = TL_comparators.get().condition.alloc();
        InventoryItem item = this.getBestRecurse(predicate, conditionComparator);
        TL_comparators.get().condition.release(conditionComparator);
        if (item != null && item.getCondition() <= 0) {
            item = null;
        }

        return item;
    }

    public InventoryItem getBestCondition(String _type) {
        ItemContainer.TypePredicate typePredicate = TL_predicates.get().type.alloc().init(_type);
        InventoryItem item = this.getBestCondition(typePredicate);
        TL_predicates.get().type.release(typePredicate);
        return item;
    }

    public InventoryItem getBestConditionRecurse(String _type) {
        ItemContainer.TypePredicate typePredicate = TL_predicates.get().type.alloc().init(_type);
        InventoryItem item = this.getBestConditionRecurse(typePredicate);
        TL_predicates.get().type.release(typePredicate);
        return item;
    }

    public InventoryItem getBestConditionEval(LuaClosure functionObj) {
        ItemContainer.EvalPredicate evalPredicate = TL_predicates.get().eval.alloc().init(functionObj);
        InventoryItem item = this.getBestCondition(evalPredicate);
        TL_predicates.get().eval.release(evalPredicate);
        return item;
    }

    public InventoryItem getBestConditionEvalRecurse(LuaClosure functionObj) {
        ItemContainer.EvalPredicate evalPredicate = TL_predicates.get().eval.alloc().init(functionObj);
        InventoryItem item = this.getBestConditionRecurse(evalPredicate);
        TL_predicates.get().eval.release(evalPredicate);
        return item;
    }

    public InventoryItem getBestConditionEvalArg(LuaClosure functionObj, Object arg) {
        ItemContainer.EvalArgPredicate evalArgPredicate = TL_predicates.get().evalArg.alloc().init(functionObj, arg);
        InventoryItem item = this.getBestCondition(evalArgPredicate);
        TL_predicates.get().evalArg.release(evalArgPredicate);
        return item;
    }

    public InventoryItem getBestConditionEvalArgRecurse(LuaClosure functionObj, Object arg) {
        ItemContainer.EvalArgPredicate evalArgPredicate = TL_predicates.get().evalArg.alloc().init(functionObj, arg);
        InventoryItem item = this.getBestConditionRecurse(evalArgPredicate);
        TL_predicates.get().evalArg.release(evalArgPredicate);
        return item;
    }

    public InventoryItem getFirstEval(LuaClosure functionObj) {
        ItemContainer.EvalPredicate evalPredicate = TL_predicates.get().eval.alloc().init(functionObj);
        InventoryItem item = this.getFirst(evalPredicate);
        TL_predicates.get().eval.release(evalPredicate);
        return item;
    }

    public InventoryItem getFirstEvalArg(LuaClosure functionObj, Object arg) {
        ItemContainer.EvalArgPredicate evalArgPredicate = TL_predicates.get().evalArg.alloc().init(functionObj, arg);
        InventoryItem item = this.getFirst(evalArgPredicate);
        TL_predicates.get().evalArg.release(evalArgPredicate);
        return item;
    }

    public boolean containsEval(LuaClosure functionObj) {
        return this.getFirstEval(functionObj) != null;
    }

    public boolean containsEvalArg(LuaClosure functionObj, Object arg) {
        return this.getFirstEvalArg(functionObj, arg) != null;
    }

    public boolean containsEvalRecurse(LuaClosure functionObj) {
        return this.getFirstEvalRecurse(functionObj) != null;
    }

    public boolean containsEvalArgRecurse(LuaClosure functionObj, Object arg) {
        return this.getFirstEvalArgRecurse(functionObj, arg) != null;
    }

    public boolean containsTag(String tag) {
        return this.getFirstTag(tag) != null;
    }

    public boolean containsTagEval(String tag, LuaClosure functionObj) {
        return this.getFirstTagEval(tag, functionObj) != null;
    }

    public boolean containsTagRecurse(String tag) {
        return this.getFirstTagRecurse(tag) != null;
    }

    public boolean containsTagEvalRecurse(String tag, LuaClosure functionObj) {
        return this.getFirstTagEvalRecurse(tag, functionObj) != null;
    }

    public boolean containsTagEvalArgRecurse(String tag, LuaClosure functionObj, Object arg) {
        return this.getFirstTagEvalArgRecurse(tag, functionObj, arg) != null;
    }

    public boolean containsTypeEvalRecurse(String _type, LuaClosure functionObj) {
        return this.getFirstTypeEvalRecurse(_type, functionObj) != null;
    }

    public boolean containsTypeEvalArgRecurse(String _type, LuaClosure functionObj, Object arg) {
        return this.getFirstTypeEvalArgRecurse(_type, functionObj, arg) != null;
    }

    private static boolean compareType(String string0, String string1) {
        if (string0 != null && string0.contains("/")) {
            int int0 = string0.indexOf(string1);
            if (int0 == -1) {
                return false;
            } else {
                char char0 = int0 > 0 ? string0.charAt(int0 - 1) : 0;
                char char1 = int0 + string1.length() < string0.length() ? string0.charAt(int0 + string1.length()) : 0;
                return char0 == 0 && char1 == '/' || char0 == '/' && char1 == 0 || char0 == '/' && char1 == '/';
            }
        } else {
            return string0.equals(string1);
        }
    }

    private static boolean compareType(String string, InventoryItem item) {
        return string != null && string.indexOf(46) == -1
            ? compareType(string, item.getType())
            : compareType(string, item.getFullType()) || compareType(string, item.getType());
    }

    public InventoryItem getFirst(Predicate<InventoryItem> predicate) {
        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if (item == null) {
                this.Items.remove(int0);
                int0--;
            } else if (predicate.test(item)) {
                return item;
            }
        }

        return null;
    }

    public InventoryItem getFirstRecurse(Predicate<InventoryItem> predicate) {
        ItemContainer.InventoryItemList inventoryItemList = TL_itemListPool.get().alloc();

        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item0 = this.Items.get(int0);
            if (item0 == null) {
                this.Items.remove(int0);
                int0--;
            } else {
                if (predicate.test(item0)) {
                    TL_itemListPool.get().release(inventoryItemList);
                    return item0;
                }

                if (item0 instanceof InventoryContainer) {
                    inventoryItemList.add(item0);
                }
            }
        }

        for (int int1 = 0; int1 < inventoryItemList.size(); int1++) {
            ItemContainer container = ((InventoryContainer)inventoryItemList.get(int1)).getInventory();
            InventoryItem item1 = container.getFirstRecurse(predicate);
            if (item1 != null) {
                TL_itemListPool.get().release(inventoryItemList);
                return item1;
            }
        }

        TL_itemListPool.get().release(inventoryItemList);
        return null;
    }

    public ArrayList<InventoryItem> getSome(Predicate<InventoryItem> predicate, int count, ArrayList<InventoryItem> result) {
        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if (item == null) {
                this.Items.remove(int0);
                int0--;
            } else if (predicate.test(item)) {
                result.add(item);
                if (result.size() >= count) {
                    break;
                }
            }
        }

        return result;
    }

    public ArrayList<InventoryItem> getSomeRecurse(Predicate<InventoryItem> predicate, int count, ArrayList<InventoryItem> result) {
        ItemContainer.InventoryItemList inventoryItemList = TL_itemListPool.get().alloc();

        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if (item == null) {
                this.Items.remove(int0);
                int0--;
            } else {
                if (predicate.test(item)) {
                    result.add(item);
                    if (result.size() >= count) {
                        TL_itemListPool.get().release(inventoryItemList);
                        return result;
                    }
                }

                if (item instanceof InventoryContainer) {
                    inventoryItemList.add(item);
                }
            }
        }

        for (int int1 = 0; int1 < inventoryItemList.size(); int1++) {
            ItemContainer container = ((InventoryContainer)inventoryItemList.get(int1)).getInventory();
            container.getSomeRecurse(predicate, count, result);
            if (result.size() >= count) {
                break;
            }
        }

        TL_itemListPool.get().release(inventoryItemList);
        return result;
    }

    public ArrayList<InventoryItem> getAll(Predicate<InventoryItem> predicate, ArrayList<InventoryItem> result) {
        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if (item == null) {
                this.Items.remove(int0);
                int0--;
            } else if (predicate.test(item)) {
                result.add(item);
            }
        }

        return result;
    }

    public ArrayList<InventoryItem> getAllRecurse(Predicate<InventoryItem> predicate, ArrayList<InventoryItem> result) {
        ItemContainer.InventoryItemList inventoryItemList = TL_itemListPool.get().alloc();

        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if (item == null) {
                this.Items.remove(int0);
                int0--;
            } else {
                if (predicate.test(item)) {
                    result.add(item);
                }

                if (item instanceof InventoryContainer) {
                    inventoryItemList.add(item);
                }
            }
        }

        for (int int1 = 0; int1 < inventoryItemList.size(); int1++) {
            ItemContainer container = ((InventoryContainer)inventoryItemList.get(int1)).getInventory();
            container.getAllRecurse(predicate, result);
        }

        TL_itemListPool.get().release(inventoryItemList);
        return result;
    }

    public int getCount(Predicate<InventoryItem> predicate) {
        ItemContainer.InventoryItemList inventoryItemList = TL_itemListPool.get().alloc();
        this.getAll(predicate, inventoryItemList);
        int int0 = inventoryItemList.size();
        TL_itemListPool.get().release(inventoryItemList);
        return int0;
    }

    public int getCountRecurse(Predicate<InventoryItem> predicate) {
        ItemContainer.InventoryItemList inventoryItemList = TL_itemListPool.get().alloc();
        this.getAllRecurse(predicate, inventoryItemList);
        int int0 = inventoryItemList.size();
        TL_itemListPool.get().release(inventoryItemList);
        return int0;
    }

    public int getCountTag(String tag) {
        ItemContainer.TagPredicate tagPredicate = TL_predicates.get().tag.alloc().init(tag);
        int int0 = this.getCount(tagPredicate);
        TL_predicates.get().tag.release(tagPredicate);
        return int0;
    }

    public int getCountTagEval(String tag, LuaClosure functionObj) {
        ItemContainer.TagEvalPredicate tagEvalPredicate = TL_predicates.get().tagEval.alloc().init(tag, functionObj);
        int int0 = this.getCount(tagEvalPredicate);
        TL_predicates.get().tagEval.release(tagEvalPredicate);
        return int0;
    }

    public int getCountTagEvalArg(String tag, LuaClosure functionObj, Object arg) {
        ItemContainer.TagEvalArgPredicate tagEvalArgPredicate = TL_predicates.get().tagEvalArg.alloc().init(tag, functionObj, arg);
        int int0 = this.getCount(tagEvalArgPredicate);
        TL_predicates.get().tagEvalArg.release(tagEvalArgPredicate);
        return int0;
    }

    public int getCountTagRecurse(String tag) {
        ItemContainer.TagPredicate tagPredicate = TL_predicates.get().tag.alloc().init(tag);
        int int0 = this.getCountRecurse(tagPredicate);
        TL_predicates.get().tag.release(tagPredicate);
        return int0;
    }

    public int getCountTagEvalRecurse(String tag, LuaClosure functionObj) {
        ItemContainer.TagEvalPredicate tagEvalPredicate = TL_predicates.get().tagEval.alloc().init(tag, functionObj);
        int int0 = this.getCountRecurse(tagEvalPredicate);
        TL_predicates.get().tagEval.release(tagEvalPredicate);
        return int0;
    }

    public int getCountTagEvalArgRecurse(String tag, LuaClosure functionObj, Object arg) {
        ItemContainer.TagEvalArgPredicate tagEvalArgPredicate = TL_predicates.get().tagEvalArg.alloc().init(tag, functionObj, arg);
        int int0 = this.getCountRecurse(tagEvalArgPredicate);
        TL_predicates.get().tagEvalArg.release(tagEvalArgPredicate);
        return int0;
    }

    public int getCountType(String _type) {
        ItemContainer.TypePredicate typePredicate = TL_predicates.get().type.alloc().init(_type);
        int int0 = this.getCount(typePredicate);
        TL_predicates.get().type.release(typePredicate);
        return int0;
    }

    public int getCountTypeEval(String _type, LuaClosure functionObj) {
        ItemContainer.TypeEvalPredicate typeEvalPredicate = TL_predicates.get().typeEval.alloc().init(_type, functionObj);
        int int0 = this.getCount(typeEvalPredicate);
        TL_predicates.get().typeEval.release(typeEvalPredicate);
        return int0;
    }

    public int getCountTypeEvalArg(String _type, LuaClosure functionObj, Object arg) {
        ItemContainer.TypeEvalArgPredicate typeEvalArgPredicate = TL_predicates.get().typeEvalArg.alloc().init(_type, functionObj, arg);
        int int0 = this.getCount(typeEvalArgPredicate);
        TL_predicates.get().typeEvalArg.release(typeEvalArgPredicate);
        return int0;
    }

    public int getCountTypeRecurse(String _type) {
        ItemContainer.TypePredicate typePredicate = TL_predicates.get().type.alloc().init(_type);
        int int0 = this.getCountRecurse(typePredicate);
        TL_predicates.get().type.release(typePredicate);
        return int0;
    }

    public int getCountTypeEvalRecurse(String _type, LuaClosure functionObj) {
        ItemContainer.TypeEvalPredicate typeEvalPredicate = TL_predicates.get().typeEval.alloc().init(_type, functionObj);
        int int0 = this.getCountRecurse(typeEvalPredicate);
        TL_predicates.get().typeEval.release(typeEvalPredicate);
        return int0;
    }

    public int getCountTypeEvalArgRecurse(String _type, LuaClosure functionObj, Object arg) {
        ItemContainer.TypeEvalArgPredicate typeEvalArgPredicate = TL_predicates.get().typeEvalArg.alloc().init(_type, functionObj, arg);
        int int0 = this.getCountRecurse(typeEvalArgPredicate);
        TL_predicates.get().typeEvalArg.release(typeEvalArgPredicate);
        return int0;
    }

    public int getCountEval(LuaClosure functionObj) {
        ItemContainer.EvalPredicate evalPredicate = TL_predicates.get().eval.alloc().init(functionObj);
        int int0 = this.getCount(evalPredicate);
        TL_predicates.get().eval.release(evalPredicate);
        return int0;
    }

    public int getCountEvalArg(LuaClosure functionObj, Object arg) {
        ItemContainer.EvalArgPredicate evalArgPredicate = TL_predicates.get().evalArg.alloc().init(functionObj, arg);
        int int0 = this.getCount(evalArgPredicate);
        TL_predicates.get().evalArg.release(evalArgPredicate);
        return int0;
    }

    public int getCountEvalRecurse(LuaClosure functionObj) {
        ItemContainer.EvalPredicate evalPredicate = TL_predicates.get().eval.alloc().init(functionObj);
        int int0 = this.getCountRecurse(evalPredicate);
        TL_predicates.get().eval.release(evalPredicate);
        return int0;
    }

    public int getCountEvalArgRecurse(LuaClosure functionObj, Object arg) {
        ItemContainer.EvalArgPredicate evalArgPredicate = TL_predicates.get().evalArg.alloc().init(functionObj, arg);
        int int0 = this.getCountRecurse(evalArgPredicate);
        TL_predicates.get().evalArg.release(evalArgPredicate);
        return int0;
    }

    public InventoryItem getFirstCategory(String category) {
        ItemContainer.CategoryPredicate categoryPredicate = TL_predicates.get().category.alloc().init(category);
        InventoryItem item = this.getFirst(categoryPredicate);
        TL_predicates.get().category.release(categoryPredicate);
        return item;
    }

    public InventoryItem getFirstCategoryRecurse(String category) {
        ItemContainer.CategoryPredicate categoryPredicate = TL_predicates.get().category.alloc().init(category);
        InventoryItem item = this.getFirstRecurse(categoryPredicate);
        TL_predicates.get().category.release(categoryPredicate);
        return item;
    }

    public InventoryItem getFirstEvalRecurse(LuaClosure functionObj) {
        ItemContainer.EvalPredicate evalPredicate = TL_predicates.get().eval.alloc().init(functionObj);
        InventoryItem item = this.getFirstRecurse(evalPredicate);
        TL_predicates.get().eval.release(evalPredicate);
        return item;
    }

    public InventoryItem getFirstEvalArgRecurse(LuaClosure functionObj, Object arg) {
        ItemContainer.EvalArgPredicate evalArgPredicate = TL_predicates.get().evalArg.alloc().init(functionObj, arg);
        InventoryItem item = this.getFirstRecurse(evalArgPredicate);
        TL_predicates.get().evalArg.release(evalArgPredicate);
        return item;
    }

    public InventoryItem getFirstTag(String tag) {
        ItemContainer.TagPredicate tagPredicate = TL_predicates.get().tag.alloc().init(tag);
        InventoryItem item = this.getFirst(tagPredicate);
        TL_predicates.get().tag.release(tagPredicate);
        return item;
    }

    public InventoryItem getFirstTagRecurse(String tag) {
        ItemContainer.TagPredicate tagPredicate = TL_predicates.get().tag.alloc().init(tag);
        InventoryItem item = this.getFirstRecurse(tagPredicate);
        TL_predicates.get().tag.release(tagPredicate);
        return item;
    }

    public InventoryItem getFirstTagEval(String tag, LuaClosure functionObj) {
        ItemContainer.TagEvalPredicate tagEvalPredicate = TL_predicates.get().tagEval.alloc().init(tag, functionObj);
        InventoryItem item = this.getFirstRecurse(tagEvalPredicate);
        TL_predicates.get().tagEval.release(tagEvalPredicate);
        return item;
    }

    public InventoryItem getFirstTagEvalRecurse(String tag, LuaClosure functionObj) {
        ItemContainer.TagEvalPredicate tagEvalPredicate = TL_predicates.get().tagEval.alloc().init(tag, functionObj);
        InventoryItem item = this.getFirstRecurse(tagEvalPredicate);
        TL_predicates.get().tagEval.release(tagEvalPredicate);
        return item;
    }

    public InventoryItem getFirstTagEvalArgRecurse(String tag, LuaClosure functionObj, Object arg) {
        ItemContainer.TagEvalArgPredicate tagEvalArgPredicate = TL_predicates.get().tagEvalArg.alloc().init(tag, functionObj, arg);
        InventoryItem item = this.getFirstRecurse(tagEvalArgPredicate);
        TL_predicates.get().tagEvalArg.release(tagEvalArgPredicate);
        return item;
    }

    public InventoryItem getFirstType(String _type) {
        ItemContainer.TypePredicate typePredicate = TL_predicates.get().type.alloc().init(_type);
        InventoryItem item = this.getFirst(typePredicate);
        TL_predicates.get().type.release(typePredicate);
        return item;
    }

    public InventoryItem getFirstTypeRecurse(String _type) {
        ItemContainer.TypePredicate typePredicate = TL_predicates.get().type.alloc().init(_type);
        InventoryItem item = this.getFirstRecurse(typePredicate);
        TL_predicates.get().type.release(typePredicate);
        return item;
    }

    public InventoryItem getFirstTypeEval(String _type, LuaClosure functionObj) {
        ItemContainer.TypeEvalPredicate typeEvalPredicate = TL_predicates.get().typeEval.alloc().init(_type, functionObj);
        InventoryItem item = this.getFirstRecurse(typeEvalPredicate);
        TL_predicates.get().typeEval.release(typeEvalPredicate);
        return item;
    }

    public InventoryItem getFirstTypeEvalRecurse(String _type, LuaClosure functionObj) {
        ItemContainer.TypeEvalPredicate typeEvalPredicate = TL_predicates.get().typeEval.alloc().init(_type, functionObj);
        InventoryItem item = this.getFirstRecurse(typeEvalPredicate);
        TL_predicates.get().typeEval.release(typeEvalPredicate);
        return item;
    }

    public InventoryItem getFirstTypeEvalArgRecurse(String _type, LuaClosure functionObj, Object arg) {
        ItemContainer.TypeEvalArgPredicate typeEvalArgPredicate = TL_predicates.get().typeEvalArg.alloc().init(_type, functionObj, arg);
        InventoryItem item = this.getFirstRecurse(typeEvalArgPredicate);
        TL_predicates.get().typeEvalArg.release(typeEvalArgPredicate);
        return item;
    }

    public ArrayList<InventoryItem> getSomeCategory(String category, int count, ArrayList<InventoryItem> result) {
        ItemContainer.CategoryPredicate categoryPredicate = TL_predicates.get().category.alloc().init(category);
        ArrayList arrayList = this.getSome(categoryPredicate, count, result);
        TL_predicates.get().category.release(categoryPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getSomeCategoryRecurse(String category, int count, ArrayList<InventoryItem> result) {
        ItemContainer.CategoryPredicate categoryPredicate = TL_predicates.get().category.alloc().init(category);
        ArrayList arrayList = this.getSomeRecurse(categoryPredicate, count, result);
        TL_predicates.get().category.release(categoryPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getSomeTag(String tag, int count, ArrayList<InventoryItem> result) {
        ItemContainer.TagPredicate tagPredicate = TL_predicates.get().tag.alloc().init(tag);
        ArrayList arrayList = this.getSome(tagPredicate, count, result);
        TL_predicates.get().tag.release(tagPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getSomeTagEval(String tag, LuaClosure functionObj, int count, ArrayList<InventoryItem> result) {
        ItemContainer.TagEvalPredicate tagEvalPredicate = TL_predicates.get().tagEval.alloc().init(tag, functionObj);
        ArrayList arrayList = this.getSome(tagEvalPredicate, count, result);
        TL_predicates.get().tagEval.release(tagEvalPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getSomeTagEvalArg(String tag, LuaClosure functionObj, Object arg, int count, ArrayList<InventoryItem> result) {
        ItemContainer.TagEvalArgPredicate tagEvalArgPredicate = TL_predicates.get().tagEvalArg.alloc().init(tag, functionObj, arg);
        ArrayList arrayList = this.getSome(tagEvalArgPredicate, count, result);
        TL_predicates.get().tagEvalArg.release(tagEvalArgPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getSomeTagRecurse(String tag, int count, ArrayList<InventoryItem> result) {
        ItemContainer.TagPredicate tagPredicate = TL_predicates.get().tag.alloc().init(tag);
        ArrayList arrayList = this.getSomeRecurse(tagPredicate, count, result);
        TL_predicates.get().tag.release(tagPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getSomeTagEvalRecurse(String tag, LuaClosure functionObj, int count, ArrayList<InventoryItem> result) {
        ItemContainer.TagEvalPredicate tagEvalPredicate = TL_predicates.get().tagEval.alloc().init(tag, functionObj);
        ArrayList arrayList = this.getSomeRecurse(tagEvalPredicate, count, result);
        TL_predicates.get().tagEval.release(tagEvalPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getSomeTagEvalArgRecurse(String tag, LuaClosure functionObj, Object arg, int count, ArrayList<InventoryItem> result) {
        ItemContainer.TagEvalArgPredicate tagEvalArgPredicate = TL_predicates.get().tagEvalArg.alloc().init(tag, functionObj, arg);
        ArrayList arrayList = this.getSomeRecurse(tagEvalArgPredicate, count, result);
        TL_predicates.get().tagEvalArg.release(tagEvalArgPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getSomeType(String _type, int count, ArrayList<InventoryItem> result) {
        ItemContainer.TypePredicate typePredicate = TL_predicates.get().type.alloc().init(_type);
        ArrayList arrayList = this.getSome(typePredicate, count, result);
        TL_predicates.get().type.release(typePredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getSomeTypeEval(String _type, LuaClosure functionObj, int count, ArrayList<InventoryItem> result) {
        ItemContainer.TypeEvalPredicate typeEvalPredicate = TL_predicates.get().typeEval.alloc().init(_type, functionObj);
        ArrayList arrayList = this.getSome(typeEvalPredicate, count, result);
        TL_predicates.get().typeEval.release(typeEvalPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getSomeTypeEvalArg(String _type, LuaClosure functionObj, Object arg, int count, ArrayList<InventoryItem> result) {
        ItemContainer.TypeEvalArgPredicate typeEvalArgPredicate = TL_predicates.get().typeEvalArg.alloc().init(_type, functionObj, arg);
        ArrayList arrayList = this.getSome(typeEvalArgPredicate, count, result);
        TL_predicates.get().typeEvalArg.release(typeEvalArgPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getSomeTypeRecurse(String _type, int count, ArrayList<InventoryItem> result) {
        ItemContainer.TypePredicate typePredicate = TL_predicates.get().type.alloc().init(_type);
        ArrayList arrayList = this.getSomeRecurse(typePredicate, count, result);
        TL_predicates.get().type.release(typePredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getSomeTypeEvalRecurse(String _type, LuaClosure functionObj, int count, ArrayList<InventoryItem> result) {
        ItemContainer.TypeEvalPredicate typeEvalPredicate = TL_predicates.get().typeEval.alloc().init(_type, functionObj);
        ArrayList arrayList = this.getSomeRecurse(typeEvalPredicate, count, result);
        TL_predicates.get().typeEval.release(typeEvalPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getSomeTypeEvalArgRecurse(String _type, LuaClosure functionObj, Object arg, int count, ArrayList<InventoryItem> result) {
        ItemContainer.TypeEvalArgPredicate typeEvalArgPredicate = TL_predicates.get().typeEvalArg.alloc().init(_type, functionObj, arg);
        ArrayList arrayList = this.getSomeRecurse(typeEvalArgPredicate, count, result);
        TL_predicates.get().typeEvalArg.release(typeEvalArgPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getSomeEval(LuaClosure functionObj, int count, ArrayList<InventoryItem> result) {
        ItemContainer.EvalPredicate evalPredicate = TL_predicates.get().eval.alloc().init(functionObj);
        ArrayList arrayList = this.getSome(evalPredicate, count, result);
        TL_predicates.get().eval.release(evalPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getSomeEvalArg(LuaClosure functionObj, Object arg, int count, ArrayList<InventoryItem> result) {
        ItemContainer.EvalArgPredicate evalArgPredicate = TL_predicates.get().evalArg.alloc().init(functionObj, arg);
        ArrayList arrayList = this.getSome(evalArgPredicate, count, result);
        TL_predicates.get().evalArg.release(evalArgPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getSomeEvalRecurse(LuaClosure functionObj, int count, ArrayList<InventoryItem> result) {
        ItemContainer.EvalPredicate evalPredicate = TL_predicates.get().eval.alloc().init(functionObj);
        ArrayList arrayList = this.getSomeRecurse(evalPredicate, count, result);
        TL_predicates.get().eval.release(evalPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getSomeEvalArgRecurse(LuaClosure functionObj, Object arg, int count, ArrayList<InventoryItem> result) {
        ItemContainer.EvalArgPredicate evalArgPredicate = TL_predicates.get().evalArg.alloc().init(functionObj, arg);
        ArrayList arrayList = this.getSomeRecurse(evalArgPredicate, count, result);
        TL_predicates.get().evalArg.release(evalArgPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getAllCategory(String category, ArrayList<InventoryItem> result) {
        ItemContainer.CategoryPredicate categoryPredicate = TL_predicates.get().category.alloc().init(category);
        ArrayList arrayList = this.getAll(categoryPredicate, result);
        TL_predicates.get().category.release(categoryPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getAllCategoryRecurse(String category, ArrayList<InventoryItem> result) {
        ItemContainer.CategoryPredicate categoryPredicate = TL_predicates.get().category.alloc().init(category);
        ArrayList arrayList = this.getAllRecurse(categoryPredicate, result);
        TL_predicates.get().category.release(categoryPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getAllTag(String tag, ArrayList<InventoryItem> result) {
        ItemContainer.TagPredicate tagPredicate = TL_predicates.get().tag.alloc().init(tag);
        ArrayList arrayList = this.getAll(tagPredicate, result);
        TL_predicates.get().tag.release(tagPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getAllTagEval(String tag, LuaClosure functionObj, ArrayList<InventoryItem> result) {
        ItemContainer.TagEvalPredicate tagEvalPredicate = TL_predicates.get().tagEval.alloc().init(tag, functionObj);
        ArrayList arrayList = this.getAll(tagEvalPredicate, result);
        TL_predicates.get().tagEval.release(tagEvalPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getAllTagEvalArg(String tag, LuaClosure functionObj, Object arg, ArrayList<InventoryItem> result) {
        ItemContainer.TagEvalArgPredicate tagEvalArgPredicate = TL_predicates.get().tagEvalArg.alloc().init(tag, functionObj, arg);
        ArrayList arrayList = this.getAll(tagEvalArgPredicate, result);
        TL_predicates.get().tagEvalArg.release(tagEvalArgPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getAllTagRecurse(String tag, ArrayList<InventoryItem> result) {
        ItemContainer.TagPredicate tagPredicate = TL_predicates.get().tag.alloc().init(tag);
        ArrayList arrayList = this.getAllRecurse(tagPredicate, result);
        TL_predicates.get().tag.release(tagPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getAllTagEvalRecurse(String tag, LuaClosure functionObj, ArrayList<InventoryItem> result) {
        ItemContainer.TagEvalPredicate tagEvalPredicate = TL_predicates.get().tagEval.alloc().init(tag, functionObj);
        ArrayList arrayList = this.getAllRecurse(tagEvalPredicate, result);
        TL_predicates.get().tagEval.release(tagEvalPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getAllTagEvalArgRecurse(String tag, LuaClosure functionObj, Object arg, ArrayList<InventoryItem> result) {
        ItemContainer.TagEvalArgPredicate tagEvalArgPredicate = TL_predicates.get().tagEvalArg.alloc().init(tag, functionObj, arg);
        ArrayList arrayList = this.getAllRecurse(tagEvalArgPredicate, result);
        TL_predicates.get().tagEvalArg.release(tagEvalArgPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getAllType(String _type, ArrayList<InventoryItem> result) {
        ItemContainer.TypePredicate typePredicate = TL_predicates.get().type.alloc().init(_type);
        ArrayList arrayList = this.getAll(typePredicate, result);
        TL_predicates.get().type.release(typePredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getAllTypeEval(String _type, LuaClosure functionObj, ArrayList<InventoryItem> result) {
        ItemContainer.TypeEvalPredicate typeEvalPredicate = TL_predicates.get().typeEval.alloc().init(_type, functionObj);
        ArrayList arrayList = this.getAll(typeEvalPredicate, result);
        TL_predicates.get().typeEval.release(typeEvalPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getAllTypeEvalArg(String _type, LuaClosure functionObj, Object arg, ArrayList<InventoryItem> result) {
        ItemContainer.TypeEvalArgPredicate typeEvalArgPredicate = TL_predicates.get().typeEvalArg.alloc().init(_type, functionObj, arg);
        ArrayList arrayList = this.getAll(typeEvalArgPredicate, result);
        TL_predicates.get().typeEvalArg.release(typeEvalArgPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getAllTypeRecurse(String _type, ArrayList<InventoryItem> result) {
        ItemContainer.TypePredicate typePredicate = TL_predicates.get().type.alloc().init(_type);
        ArrayList arrayList = this.getAllRecurse(typePredicate, result);
        TL_predicates.get().type.release(typePredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getAllTypeEvalRecurse(String _type, LuaClosure functionObj, ArrayList<InventoryItem> result) {
        ItemContainer.TypeEvalPredicate typeEvalPredicate = TL_predicates.get().typeEval.alloc().init(_type, functionObj);
        ArrayList arrayList = this.getAllRecurse(typeEvalPredicate, result);
        TL_predicates.get().typeEval.release(typeEvalPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getAllTypeEvalArgRecurse(String _type, LuaClosure functionObj, Object arg, ArrayList<InventoryItem> result) {
        ItemContainer.TypeEvalArgPredicate typeEvalArgPredicate = TL_predicates.get().typeEvalArg.alloc().init(_type, functionObj, arg);
        ArrayList arrayList = this.getAllRecurse(typeEvalArgPredicate, result);
        TL_predicates.get().typeEvalArg.release(typeEvalArgPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getAllEval(LuaClosure functionObj, ArrayList<InventoryItem> result) {
        ItemContainer.EvalPredicate evalPredicate = TL_predicates.get().eval.alloc().init(functionObj);
        ArrayList arrayList = this.getAll(evalPredicate, result);
        TL_predicates.get().eval.release(evalPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getAllEvalArg(LuaClosure functionObj, Object arg, ArrayList<InventoryItem> result) {
        ItemContainer.EvalArgPredicate evalArgPredicate = TL_predicates.get().evalArg.alloc().init(functionObj, arg);
        ArrayList arrayList = this.getAll(evalArgPredicate, result);
        TL_predicates.get().evalArg.release(evalArgPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getAllEvalRecurse(LuaClosure functionObj, ArrayList<InventoryItem> result) {
        ItemContainer.EvalPredicate evalPredicate = TL_predicates.get().eval.alloc().init(functionObj);
        ArrayList arrayList = this.getAllRecurse(evalPredicate, result);
        TL_predicates.get().eval.release(evalPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getAllEvalArgRecurse(LuaClosure functionObj, Object arg, ArrayList<InventoryItem> result) {
        ItemContainer.EvalArgPredicate evalArgPredicate = TL_predicates.get().evalArg.alloc().init(functionObj, arg);
        ArrayList arrayList = this.getAllRecurse(evalArgPredicate, result);
        TL_predicates.get().evalArg.release(evalArgPredicate);
        return arrayList;
    }

    public ArrayList<InventoryItem> getSomeCategory(String category, int count) {
        return this.getSomeCategory(category, count, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getSomeEval(LuaClosure functionObj, int count) {
        return this.getSomeEval(functionObj, count, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getSomeEvalArg(LuaClosure functionObj, Object arg, int count) {
        return this.getSomeEvalArg(functionObj, arg, count, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getSomeTypeEval(String _type, LuaClosure functionObj, int count) {
        return this.getSomeTypeEval(_type, functionObj, count, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getSomeTypeEvalArg(String _type, LuaClosure functionObj, Object arg, int count) {
        return this.getSomeTypeEvalArg(_type, functionObj, arg, count, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getSomeEvalRecurse(LuaClosure functionObj, int count) {
        return this.getSomeEvalRecurse(functionObj, count, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getSomeEvalArgRecurse(LuaClosure functionObj, Object arg, int count) {
        return this.getSomeEvalArgRecurse(functionObj, arg, count, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getSomeTag(String tag, int count) {
        return this.getSomeTag(tag, count, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getSomeTagRecurse(String tag, int count) {
        return this.getSomeTagRecurse(tag, count, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getSomeTagEvalRecurse(String tag, LuaClosure functionObj, int count) {
        return this.getSomeTagEvalRecurse(tag, functionObj, count, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getSomeTagEvalArgRecurse(String tag, LuaClosure functionObj, Object arg, int count) {
        return this.getSomeTagEvalArgRecurse(tag, functionObj, arg, count, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getSomeType(String _type, int count) {
        return this.getSomeType(_type, count, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getSomeTypeRecurse(String _type, int count) {
        return this.getSomeTypeRecurse(_type, count, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getSomeTypeEvalRecurse(String _type, LuaClosure functionObj, int count) {
        return this.getSomeTypeEvalRecurse(_type, functionObj, count, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getSomeTypeEvalArgRecurse(String _type, LuaClosure functionObj, Object arg, int count) {
        return this.getSomeTypeEvalArgRecurse(_type, functionObj, arg, count, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getAll(Predicate<InventoryItem> predicate) {
        return this.getAll(predicate, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getAllCategory(String category) {
        return this.getAllCategory(category, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getAllEval(LuaClosure functionObj) {
        return this.getAllEval(functionObj, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getAllEvalArg(LuaClosure functionObj, Object arg) {
        return this.getAllEvalArg(functionObj, arg, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getAllTagEval(String _type, LuaClosure functionObj) {
        return this.getAllTagEval(_type, functionObj, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getAllTagEvalArg(String _type, LuaClosure functionObj, Object arg) {
        return this.getAllTagEvalArg(_type, functionObj, arg, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getAllTypeEval(String _type, LuaClosure functionObj) {
        return this.getAllTypeEval(_type, functionObj, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getAllTypeEvalArg(String _type, LuaClosure functionObj, Object arg) {
        return this.getAllTypeEvalArg(_type, functionObj, arg, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getAllEvalRecurse(LuaClosure functionObj) {
        return this.getAllEvalRecurse(functionObj, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getAllEvalArgRecurse(LuaClosure functionObj, Object arg) {
        return this.getAllEvalArgRecurse(functionObj, arg, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getAllType(String _type) {
        return this.getAllType(_type, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getAllTypeRecurse(String _type) {
        return this.getAllTypeRecurse(_type, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getAllTypeEvalRecurse(String _type, LuaClosure functionObj) {
        return this.getAllTypeEvalRecurse(_type, functionObj, new ArrayList<>());
    }

    public ArrayList<InventoryItem> getAllTypeEvalArgRecurse(String _type, LuaClosure functionObj, Object arg) {
        return this.getAllTypeEvalArgRecurse(_type, functionObj, arg, new ArrayList<>());
    }

    public InventoryItem FindAndReturnCategory(String category) {
        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if (item.getCategory().equals(category)) {
                return item;
            }
        }

        return null;
    }

    public ArrayList<InventoryItem> FindAndReturn(String _type, int count) {
        return this.getSomeType(_type, count);
    }

    public InventoryItem FindAndReturn(String _type, ArrayList<InventoryItem> itemToCheck) {
        if (_type == null) {
            return null;
        } else {
            for (int int0 = 0; int0 < this.Items.size(); int0++) {
                InventoryItem item = this.Items.get(int0);
                if (item.type != null && compareType(_type, item) && !itemToCheck.contains(item)) {
                    return item;
                }
            }

            return null;
        }
    }

    public InventoryItem FindAndReturn(String _type) {
        return this.getFirstType(_type);
    }

    public ArrayList<InventoryItem> FindAll(String _type) {
        return this.getAllType(_type);
    }

    public InventoryItem FindAndReturnStack(String _type) {
        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item0 = this.Items.get(int0);
            if (compareType(_type, item0)) {
                InventoryItem item1 = InventoryItemFactory.CreateItem(item0.module + "." + _type);
                if (item0.CanStack(item1)) {
                    return item0;
                }
            }
        }

        return null;
    }

    public InventoryItem FindAndReturnStack(InventoryItem itemlike) {
        String string = itemlike.type;

        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if ((item.type == null ? string == null : item.type.equals(string)) && item.CanStack(itemlike)) {
                return item;
            }
        }

        return null;
    }

    public boolean HasType(ItemType itemType) {
        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if (item.cat == itemType) {
                return true;
            }
        }

        return false;
    }

    public void Remove(InventoryItem item) {
        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem _item = this.Items.get(int0);
            if (_item == item) {
                if (item.uses > 1) {
                    item.uses--;
                } else {
                    this.Items.remove(item);
                }

                item.container = null;
                this.drawDirty = true;
                this.dirty = true;
                if (this.parent != null) {
                    this.dirty = true;
                }

                if (this.parent instanceof IsoDeadBody) {
                    ((IsoDeadBody)this.parent).checkClothing(item);
                }

                if (this.parent instanceof IsoMannequin) {
                    ((IsoMannequin)this.parent).checkClothing(item);
                }

                return;
            }
        }
    }

    public void DoRemoveItem(InventoryItem item) {
        this.drawDirty = true;
        if (this.parent != null) {
            this.dirty = true;
        }

        this.Items.remove(item);
        item.container = null;
        if (this.parent instanceof IsoDeadBody) {
            ((IsoDeadBody)this.parent).checkClothing(item);
        }

        if (this.parent instanceof IsoMannequin) {
            ((IsoMannequin)this.parent).checkClothing(item);
        }
    }

    public void Remove(String itemTypes) {
        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if (item.type.equals(itemTypes)) {
                if (item.uses > 1) {
                    item.uses--;
                } else {
                    this.Items.remove(item);
                }

                item.container = null;
                this.drawDirty = true;
                this.dirty = true;
                if (this.parent != null) {
                    this.dirty = true;
                }

                return;
            }
        }
    }

    public InventoryItem Remove(ItemType itemType) {
        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if (item.cat == itemType) {
                this.Items.remove(item);
                item.container = null;
                this.drawDirty = true;
                this.dirty = true;
                if (this.parent != null) {
                    this.dirty = true;
                }

                return item;
            }
        }

        return null;
    }

    public InventoryItem Find(ItemType itemType) {
        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if (item.cat == itemType) {
                return item;
            }
        }

        return null;
    }

    /**
     * Remove all the item of the type in parameter inside the container Ex of  itemType : Broccoli (no need the module like Base.Broccoli)
     */
    public void RemoveAll(String itemType) {
        this.drawDirty = true;
        if (this.parent != null) {
            this.dirty = true;
        }

        ArrayList arrayList = new ArrayList();

        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item0 = this.Items.get(int0);
            if (item0.type.equals(itemType)) {
                item0.container = null;
                arrayList.add(item0);
                this.dirty = true;
            }
        }

        for (InventoryItem item1 : arrayList) {
            this.Items.remove(item1);
        }
    }

    public boolean RemoveOneOf(String String, boolean insideInv) {
        this.drawDirty = true;
        if (this.parent != null && !(this.parent instanceof IsoGameCharacter)) {
            this.dirty = true;
        }

        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item0 = this.Items.get(int0);
            if (item0.getFullType().equals(String) || item0.type.equals(String)) {
                if (item0.uses > 1) {
                    item0.uses--;
                } else {
                    item0.container = null;
                    this.Items.remove(item0);
                }

                this.dirty = true;
                return true;
            }
        }

        if (insideInv) {
            for (int int1 = 0; int1 < this.Items.size(); int1++) {
                InventoryItem item1 = this.Items.get(int1);
                if (item1 instanceof InventoryContainer
                    && ((InventoryContainer)item1).getItemContainer() != null
                    && ((InventoryContainer)item1).getItemContainer().RemoveOneOf(String, insideInv)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void RemoveOneOf(String String) {
        this.RemoveOneOf(String, true);
    }

    /** @deprecated */
    public int getWeight() {
        if (this.parent instanceof IsoPlayer && ((IsoPlayer)this.parent).isGhostMode()) {
            return 0;
        } else {
            float float0 = 0.0F;

            for (int int0 = 0; int0 < this.Items.size(); int0++) {
                InventoryItem item = this.Items.get(int0);
                float0 += item.ActualWeight * item.uses;
            }

            return (int)(float0 * (this.weightReduction / 0.01F));
        }
    }

    public float getContentsWeight() {
        float float0 = 0.0F;

        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            float0 += item.getUnequippedWeight();
        }

        return float0;
    }

    public float getMaxWeight() {
        return this.parent instanceof IsoGameCharacter ? ((IsoGameCharacter)this.parent).getMaxWeight() : this.Capacity;
    }

    public float getCapacityWeight() {
        if (this.parent instanceof IsoPlayer) {
            if (Core.bDebug && ((IsoPlayer)this.parent).isGhostMode()
                || !((IsoPlayer)this.parent).isAccessLevel("None") && ((IsoPlayer)this.parent).isUnlimitedCarry()) {
                return 0.0F;
            }

            if (((IsoPlayer)this.parent).isUnlimitedCarry()) {
                return 0.0F;
            }
        }

        return this.parent instanceof IsoGameCharacter ? ((IsoGameCharacter)this.parent).getInventoryWeight() : this.getContentsWeight();
    }

    public boolean isEmpty() {
        return this.Items == null || this.Items.isEmpty();
    }

    public boolean isMicrowave() {
        return "microwave".equals(this.getType());
    }

    private boolean isSquareInRoom(IsoGridSquare square) {
        return square == null ? false : square.getRoom() != null;
    }

    private boolean isSquarePowered(IsoGridSquare square0) {
        if (square0 == null) {
            return false;
        } else {
            boolean boolean0 = IsoWorld.instance.isHydroPowerOn();
            if (boolean0 && square0.getRoom() != null) {
                return true;
            } else if (square0.haveElectricity()) {
                return true;
            } else {
                if (boolean0 && square0.getRoom() == null) {
                    IsoGridSquare square1 = square0.nav[IsoDirections.N.index()];
                    IsoGridSquare square2 = square0.nav[IsoDirections.S.index()];
                    IsoGridSquare square3 = square0.nav[IsoDirections.W.index()];
                    IsoGridSquare square4 = square0.nav[IsoDirections.E.index()];
                    if (this.isSquareInRoom(square1) || this.isSquareInRoom(square2) || this.isSquareInRoom(square3) || this.isSquareInRoom(square4)) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    public boolean isPowered() {
        if (this.parent != null && this.parent.getObjectIndex() != -1) {
            IsoGridSquare square0 = this.parent.getSquare();
            if (this.isSquarePowered(square0)) {
                return true;
            } else {
                this.parent.getSpriteGridObjects(s_tempObjects);

                for (int int0 = 0; int0 < s_tempObjects.size(); int0++) {
                    IsoObject object = s_tempObjects.get(int0);
                    if (object != this.parent) {
                        IsoGridSquare square1 = object.getSquare();
                        if (this.isSquarePowered(square1)) {
                            return true;
                        }
                    }
                }

                return false;
            }
        } else {
            return false;
        }
    }

    public float getTemprature() {
        if (this.customTemperature != 0.0F) {
            return this.customTemperature;
        } else {
            boolean boolean0 = false;
            if (this.getParent() != null && this.getParent().getSprite() != null) {
                boolean0 = this.getParent().getSprite().getProperties().Is("IsFridge");
            }

            if (this.isPowered()) {
                if (this.type.equals("fridge") || this.type.equals("freezer") || boolean0) {
                    return 0.2F;
                }

                if (("stove".equals(this.type) || "microwave".equals(this.type)) && this.parent instanceof IsoStove) {
                    return ((IsoStove)this.parent).getCurrentTemperature() / 100.0F;
                }
            }

            if ("barbecue".equals(this.type) && this.parent instanceof IsoBarbecue) {
                return ((IsoBarbecue)this.parent).getTemperature();
            } else if ("fireplace".equals(this.type) && this.parent instanceof IsoFireplace) {
                return ((IsoFireplace)this.parent).getTemperature();
            } else if ("woodstove".equals(this.type) && this.parent instanceof IsoFireplace) {
                return ((IsoFireplace)this.parent).getTemperature();
            } else if ((this.type.equals("fridge") || this.type.equals("freezer") || boolean0)
                && GameTime.instance.NightsSurvived == SandboxOptions.instance.getElecShutModifier()
                && GameTime.instance.getTimeOfDay() < 13.0F) {
                float float0 = (GameTime.instance.getTimeOfDay() - 7.0F) / 6.0F;
                return GameTime.instance.Lerp(0.2F, 1.0F, float0);
            } else {
                return 1.0F;
            }
        }
    }

    public boolean isTemperatureChanging() {
        return this.parent instanceof IsoStove ? ((IsoStove)this.parent).isTemperatureChanging() : false;
    }

    public ArrayList<InventoryItem> save(ByteBuffer output, IsoGameCharacter noCompress) throws IOException {
        GameWindow.WriteString(output, this.type);
        output.put((byte)(this.bExplored ? 1 : 0));
        ArrayList arrayList = CompressIdenticalItems.save(output, this.Items, null);
        output.put((byte)(this.isHasBeenLooted() ? 1 : 0));
        output.putInt(this.Capacity);
        return arrayList;
    }

    public ArrayList<InventoryItem> save(ByteBuffer output) throws IOException {
        return this.save(output, null);
    }

    public ArrayList<InventoryItem> load(ByteBuffer input, int WorldVersion) throws IOException {
        this.type = GameWindow.ReadString(input);
        this.bExplored = input.get() == 1;
        ArrayList arrayList = CompressIdenticalItems.load(input, WorldVersion, this.Items, this.IncludingObsoleteItems);

        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            item.container = this;
        }

        this.setHasBeenLooted(input.get() == 1);
        this.Capacity = input.getInt();
        this.dirty = false;
        return arrayList;
    }

    public boolean isDrawDirty() {
        return this.drawDirty;
    }

    public void setDrawDirty(boolean b) {
        this.drawDirty = b;
    }

    public InventoryItem getBestWeapon(SurvivorDesc desc) {
        InventoryItem item0 = null;
        float float0 = -1.0E7F;

        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item1 = this.Items.get(int0);
            if (item1 instanceof HandWeapon) {
                float float1 = item1.getScore(desc);
                if (float1 >= float0) {
                    float0 = float1;
                    item0 = item1;
                }
            }
        }

        return item0;
    }

    public InventoryItem getBestWeapon() {
        InventoryItem item0 = null;
        float float0 = 0.0F;

        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item1 = this.Items.get(int0);
            if (item1 instanceof HandWeapon) {
                float float1 = item1.getScore(null);
                if (float1 >= float0) {
                    float0 = float1;
                    item0 = item1;
                }
            }
        }

        return item0;
    }

    public float getTotalFoodScore(SurvivorDesc desc) {
        float float0 = 0.0F;

        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if (item instanceof Food) {
                float0 += item.getScore(desc);
            }
        }

        return float0;
    }

    public float getTotalWeaponScore(SurvivorDesc desc) {
        float float0 = 0.0F;

        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if (item instanceof HandWeapon) {
                float0 += item.getScore(desc);
            }
        }

        return float0;
    }

    public InventoryItem getBestFood(SurvivorDesc descriptor) {
        InventoryItem item0 = null;
        float float0 = 0.0F;

        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item1 = this.Items.get(int0);
            if (item1 instanceof Food) {
                float float1 = item1.getScore(descriptor);
                if (((Food)item1).isbDangerousUncooked() && !item1.isCooked()) {
                    float1 *= 0.2F;
                }

                if (((Food)item1).Age > item1.OffAge) {
                    float1 *= 0.2F;
                }

                if (float1 >= float0) {
                    float0 = float1;
                    item0 = item1;
                }
            }
        }

        return item0;
    }

    public InventoryItem getBestBandage(SurvivorDesc descriptor) {
        InventoryItem item0 = null;

        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item1 = this.Items.get(int0);
            if (item1.isCanBandage()) {
                item0 = item1;
                break;
            }
        }

        return item0;
    }

    public int getNumItems(String item) {
        int int0 = 0;
        if (item.contains("Type:")) {
            for (int int1 = 0; int1 < this.Items.size(); int1++) {
                InventoryItem item0 = this.Items.get(int1);
                if (item0 instanceof Food && item.contains("Food")) {
                    int0 += item0.uses;
                }

                if (item0 instanceof HandWeapon && item.contains("Weapon")) {
                    int0 += item0.uses;
                }
            }
        } else {
            for (int int2 = 0; int2 < this.Items.size(); int2++) {
                InventoryItem item1 = this.Items.get(int2);
                if (item1.type.equals(item)) {
                    int0 += item1.uses;
                }
            }
        }

        return int0;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * 
     * @param _active the active to set
     */
    public void setActive(boolean _active) {
        this.active = _active;
    }

    /**
     * @return the dirty
     */
    public boolean isDirty() {
        return this.dirty;
    }

    /**
     * 
     * @param _dirty the dirty to set
     */
    public void setDirty(boolean _dirty) {
        this.dirty = _dirty;
    }

    /**
     * @return the IsDevice
     */
    public boolean isIsDevice() {
        return this.IsDevice;
    }

    /**
     * 
     * @param _IsDevice the IsDevice to set
     */
    public void setIsDevice(boolean _IsDevice) {
        this.IsDevice = _IsDevice;
    }

    /**
     * @return the ageFactor
     */
    public float getAgeFactor() {
        return this.ageFactor;
    }

    /**
     * 
     * @param _ageFactor the ageFactor to set
     */
    public void setAgeFactor(float _ageFactor) {
        this.ageFactor = _ageFactor;
    }

    /**
     * @return the CookingFactor
     */
    public float getCookingFactor() {
        return this.CookingFactor;
    }

    /**
     * 
     * @param _CookingFactor the CookingFactor to set
     */
    public void setCookingFactor(float _CookingFactor) {
        this.CookingFactor = _CookingFactor;
    }

    /**
     * @return the Items
     */
    public ArrayList<InventoryItem> getItems() {
        return this.Items;
    }

    /**
     * 
     * @param _Items the Items to set
     */
    public void setItems(ArrayList<InventoryItem> _Items) {
        this.Items = _Items;
    }

    /**
     * @return the parent
     */
    public IsoObject getParent() {
        return this.parent;
    }

    /**
     * 
     * @param _parent the parent to set
     */
    public void setParent(IsoObject _parent) {
        this.parent = _parent;
    }

    /**
     * @return the SourceGrid
     */
    public IsoGridSquare getSourceGrid() {
        return this.SourceGrid;
    }

    /**
     * 
     * @param _SourceGrid the SourceGrid to set
     */
    public void setSourceGrid(IsoGridSquare _SourceGrid) {
        this.SourceGrid = _SourceGrid;
    }

    /**
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * 
     * @param _type the type to set
     */
    public void setType(String _type) {
        this.type = _type;
    }

    public void clear() {
        this.Items.clear();
        this.dirty = true;
        this.drawDirty = true;
    }

    public int getWaterContainerCount() {
        int int0 = 0;

        for (int int1 = 0; int1 < this.Items.size(); int1++) {
            InventoryItem item = this.Items.get(int1);
            if (item.CanStoreWater) {
                int0++;
            }
        }

        return int0;
    }

    public InventoryItem FindWaterSource() {
        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if (item.isWaterSource()) {
                if (!(item instanceof Drainable)) {
                    return item;
                }

                if (((Drainable)item).getUsedDelta() > 0.0F) {
                    return item;
                }
            }
        }

        return null;
    }

    public ArrayList<InventoryItem> getAllWaterFillables() {
        tempList.clear();

        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if (item.CanStoreWater) {
                tempList.add(item);
            }
        }

        return tempList;
    }

    public int getItemCount(String _type) {
        return this.getCountType(_type);
    }

    public int getItemCountRecurse(String _type) {
        return this.getCountTypeRecurse(_type);
    }

    public int getItemCount(String _type, boolean doBags) {
        return doBags ? this.getCountTypeRecurse(_type) : this.getCountType(_type);
    }

    private static int getUses(ItemContainer.InventoryItemList inventoryItemList) {
        int int0 = 0;

        for (int int1 = 0; int1 < inventoryItemList.size(); int1++) {
            DrainableComboItem drainableComboItem = Type.tryCastTo(inventoryItemList.get(int1), DrainableComboItem.class);
            if (drainableComboItem != null) {
                int0 += drainableComboItem.getDrainableUsesInt();
            } else {
                int0++;
            }
        }

        return int0;
    }

    public int getUsesRecurse(Predicate<InventoryItem> predicate) {
        ItemContainer.InventoryItemList inventoryItemList = TL_itemListPool.get().alloc();
        this.getAllRecurse(predicate, inventoryItemList);
        int int0 = getUses(inventoryItemList);
        TL_itemListPool.get().release(inventoryItemList);
        return int0;
    }

    public int getUsesType(String _type) {
        ItemContainer.InventoryItemList inventoryItemList = TL_itemListPool.get().alloc();
        this.getAllType(_type, inventoryItemList);
        int int0 = getUses(inventoryItemList);
        TL_itemListPool.get().release(inventoryItemList);
        return int0;
    }

    public int getUsesTypeRecurse(String _type) {
        ItemContainer.InventoryItemList inventoryItemList = TL_itemListPool.get().alloc();
        this.getAllTypeRecurse(_type, inventoryItemList);
        int int0 = getUses(inventoryItemList);
        TL_itemListPool.get().release(inventoryItemList);
        return int0;
    }

    public int getWeightReduction() {
        return this.weightReduction;
    }

    public void setWeightReduction(int _weightReduction) {
        _weightReduction = Math.min(_weightReduction, 100);
        _weightReduction = Math.max(_weightReduction, 0);
        this.weightReduction = _weightReduction;
    }

    public void removeAllItems() {
        this.drawDirty = true;
        if (this.parent != null) {
            this.dirty = true;
        }

        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            item.container = null;
        }

        this.Items.clear();
        if (this.parent instanceof IsoDeadBody) {
            ((IsoDeadBody)this.parent).checkClothing(null);
        }

        if (this.parent instanceof IsoMannequin) {
            ((IsoMannequin)this.parent).checkClothing(null);
        }
    }

    public boolean containsRecursive(InventoryItem item) {
        for (int int0 = 0; int0 < this.getItems().size(); int0++) {
            InventoryItem _item = this.getItems().get(int0);
            if (_item == item) {
                return true;
            }

            if (_item instanceof InventoryContainer && ((InventoryContainer)_item).getInventory().containsRecursive(item)) {
                return true;
            }
        }

        return false;
    }

    public int getItemCountFromTypeRecurse(String _type) {
        int int0 = 0;

        for (int int1 = 0; int1 < this.getItems().size(); int1++) {
            InventoryItem item = this.getItems().get(int1);
            if (item.getFullType().equals(_type)) {
                int0++;
            }

            if (item instanceof InventoryContainer) {
                int int2 = ((InventoryContainer)item).getInventory().getItemCountFromTypeRecurse(_type);
                int0 += int2;
            }
        }

        return int0;
    }

    public float getCustomTemperature() {
        return this.customTemperature;
    }

    public void setCustomTemperature(float newTemp) {
        this.customTemperature = newTemp;
    }

    public InventoryItem getItemFromType(String _type, IsoGameCharacter chr, boolean notEquipped, boolean ignoreBroken, boolean includeInv) {
        ItemContainer.InventoryItemList inventoryItemList = TL_itemListPool.get().alloc();
        if (_type.contains(".")) {
            _type = _type.split("\\.")[1];
        }

        for (int int0 = 0; int0 < this.getItems().size(); int0++) {
            InventoryItem item0 = this.getItems().get(int0);
            if (!item0.getFullType().equals(_type) && !item0.getType().equals(_type)) {
                if (includeInv
                    && item0 instanceof InventoryContainer
                    && ((InventoryContainer)item0).getInventory() != null
                    && !inventoryItemList.contains(item0)) {
                    inventoryItemList.add(item0);
                }
            } else if ((!notEquipped || chr == null || !chr.isEquippedClothing(item0)) && this.testBroken(ignoreBroken, item0)) {
                TL_itemListPool.get().release(inventoryItemList);
                return item0;
            }
        }

        for (int int1 = 0; int1 < inventoryItemList.size(); int1++) {
            ItemContainer container = ((InventoryContainer)inventoryItemList.get(int1)).getInventory();
            InventoryItem item1 = container.getItemFromType(_type, chr, notEquipped, ignoreBroken, includeInv);
            if (item1 != null) {
                TL_itemListPool.get().release(inventoryItemList);
                return item1;
            }
        }

        TL_itemListPool.get().release(inventoryItemList);
        return null;
    }

    public InventoryItem getItemFromType(String _type, boolean ignoreBroken, boolean includeInv) {
        return this.getItemFromType(_type, null, false, ignoreBroken, includeInv);
    }

    public InventoryItem getItemFromType(String _type) {
        return this.getFirstType(_type);
    }

    public ArrayList<InventoryItem> getItemsFromType(String _type) {
        return this.getAllType(_type);
    }

    public ArrayList<InventoryItem> getItemsFromFullType(String _type) {
        return _type != null && _type.contains(".") ? this.getAllType(_type) : new ArrayList<>();
    }

    public ArrayList<InventoryItem> getItemsFromFullType(String _type, boolean includeInv) {
        if (_type != null && _type.contains(".")) {
            return includeInv ? this.getAllTypeRecurse(_type) : this.getAllType(_type);
        } else {
            return new ArrayList<>();
        }
    }

    public ArrayList<InventoryItem> getItemsFromType(String _type, boolean includeInv) {
        return includeInv ? this.getAllTypeRecurse(_type) : this.getAllType(_type);
    }

    public ArrayList<InventoryItem> getItemsFromCategory(String category) {
        return this.getAllCategory(category);
    }

    public void sendContentsToRemoteContainer() {
        if (GameClient.bClient) {
            this.sendContentsToRemoteContainer(GameClient.connection);
        }
    }

    public void requestSync() {
        if (GameClient.bClient) {
            if (this.parent == null || this.parent.square == null || this.parent.square.chunk == null) {
                return;
            }

            GameClient.instance.worldObjectsSyncReq.putRequestSyncIsoChunk(this.parent.square.chunk);
        }
    }

    public void requestServerItemsForContainer() {
        if (this.parent != null && this.parent.square != null) {
            UdpConnection udpConnection = GameClient.connection;
            ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
            PacketTypes.PacketType.RequestItemsForContainer.doPacket(byteBufferWriter);
            byteBufferWriter.putShort(IsoPlayer.getInstance().OnlineID);
            byteBufferWriter.putUTF(this.type);
            if (this.parent.square.getRoom() != null) {
                byteBufferWriter.putUTF(this.parent.square.getRoom().getName());
            } else {
                byteBufferWriter.putUTF("all");
            }

            byteBufferWriter.putInt(this.parent.square.getX());
            byteBufferWriter.putInt(this.parent.square.getY());
            byteBufferWriter.putInt(this.parent.square.getZ());
            int int0 = this.parent.square.getObjects().indexOf(this.parent);
            if (int0 == -1 && this.parent.square.getStaticMovingObjects().indexOf(this.parent) != -1) {
                byteBufferWriter.putShort((short)0);
                int0 = this.parent.square.getStaticMovingObjects().indexOf(this.parent);
                byteBufferWriter.putByte((byte)int0);
            } else if (this.parent instanceof IsoWorldInventoryObject) {
                byteBufferWriter.putShort((short)1);
                byteBufferWriter.putInt(((IsoWorldInventoryObject)this.parent).getItem().id);
            } else if (this.parent instanceof BaseVehicle) {
                byteBufferWriter.putShort((short)3);
                byteBufferWriter.putShort(((BaseVehicle)this.parent).VehicleID);
                byteBufferWriter.putByte((byte)this.vehiclePart.getIndex());
            } else {
                byteBufferWriter.putShort((short)2);
                byteBufferWriter.putByte((byte)int0);
                byteBufferWriter.putByte((byte)this.parent.getContainerIndex(this));
            }

            PacketTypes.PacketType.RequestItemsForContainer.send(udpConnection);
        }
    }

    @Deprecated
    public void sendContentsToRemoteContainer(UdpConnection connection) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.AddInventoryItemToContainer.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(0);
        boolean boolean0 = false;
        byteBufferWriter.putInt(this.parent.square.getX());
        byteBufferWriter.putInt(this.parent.square.getY());
        byteBufferWriter.putInt(this.parent.square.getZ());
        byteBufferWriter.putByte((byte)this.parent.square.getObjects().indexOf(this.parent));

        try {
            CompressIdenticalItems.save(byteBufferWriter.bb, this.Items, null);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        PacketTypes.PacketType.AddInventoryItemToContainer.send(connection);
    }

    public InventoryItem getItemWithIDRecursiv(int id) {
        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if (item.id == id) {
                return item;
            }

            if (item instanceof InventoryContainer
                && ((InventoryContainer)item).getItemContainer() != null
                && !((InventoryContainer)item).getItemContainer().getItems().isEmpty()) {
                item = ((InventoryContainer)item).getItemContainer().getItemWithIDRecursiv(id);
                if (item != null) {
                    return item;
                }
            }
        }

        return null;
    }

    public InventoryItem getItemWithID(int id) {
        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if (item.id == id) {
                return item;
            }
        }

        return null;
    }

    public boolean removeItemWithID(int id) {
        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if (item.id == id) {
                this.Remove(item);
                return true;
            }
        }

        return false;
    }

    public boolean containsID(int id) {
        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if (item.id == id) {
                return true;
            }
        }

        return false;
    }

    public boolean removeItemWithIDRecurse(int id) {
        for (int int0 = 0; int0 < this.Items.size(); int0++) {
            InventoryItem item = this.Items.get(int0);
            if (item.id == id) {
                this.Remove(item);
                return true;
            }

            if (item instanceof InventoryContainer && ((InventoryContainer)item).getInventory().removeItemWithIDRecurse(id)) {
                return true;
            }
        }

        return false;
    }

    public boolean isHasBeenLooted() {
        return this.hasBeenLooted;
    }

    public void setHasBeenLooted(boolean _hasBeenLooted) {
        this.hasBeenLooted = _hasBeenLooted;
    }

    public String getOpenSound() {
        return this.openSound;
    }

    public void setOpenSound(String _openSound) {
        this.openSound = _openSound;
    }

    public String getCloseSound() {
        return this.closeSound;
    }

    public void setCloseSound(String _closeSound) {
        this.closeSound = _closeSound;
    }

    public String getPutSound() {
        return this.putSound;
    }

    public void setPutSound(String _putSound) {
        this.putSound = _putSound;
    }

    public InventoryItem haveThisKeyId(int keyId) {
        for (int int0 = 0; int0 < this.getItems().size(); int0++) {
            InventoryItem item = this.getItems().get(int0);
            if (item instanceof Key key) {
                if (key.getKeyId() == keyId) {
                    return key;
                }
            } else if (item.getType().equals("KeyRing") && ((InventoryContainer)item).getInventory().haveThisKeyId(keyId) != null) {
                return ((InventoryContainer)item).getInventory().haveThisKeyId(keyId);
            }
        }

        return null;
    }

    public String getOnlyAcceptCategory() {
        return this.OnlyAcceptCategory;
    }

    public void setOnlyAcceptCategory(String onlyAcceptCategory) {
        this.OnlyAcceptCategory = StringUtils.discardNullOrWhitespace(onlyAcceptCategory);
    }

    public String getAcceptItemFunction() {
        return this.AcceptItemFunction;
    }

    public void setAcceptItemFunction(String functionName) {
        this.AcceptItemFunction = StringUtils.discardNullOrWhitespace(functionName);
    }

    public IsoGameCharacter getCharacter() {
        if (this.getParent() instanceof IsoGameCharacter) {
            return (IsoGameCharacter)this.getParent();
        } else {
            return this.containingItem != null && this.containingItem.getContainer() != null ? this.containingItem.getContainer().getCharacter() : null;
        }
    }

    public void emptyIt() {
        this.Items = new ArrayList<>();
    }

    public LinkedHashMap<String, InventoryItem> getItems4Admin() {
        LinkedHashMap linkedHashMap = new LinkedHashMap();

        for (int int0 = 0; int0 < this.getItems().size(); int0++) {
            InventoryItem item = this.getItems().get(int0);
            item.setCount(1);
            if (item.getCat() != ItemType.Drainable
                && item.getCat() != ItemType.Weapon
                && linkedHashMap.get(item.getFullType()) != null
                && !(item instanceof InventoryContainer)) {
                ((InventoryItem)linkedHashMap.get(item.getFullType())).setCount(((InventoryItem)linkedHashMap.get(item.getFullType())).getCount() + 1);
            } else if (linkedHashMap.get(item.getFullType()) != null) {
                linkedHashMap.put(item.getFullType() + Rand.Next(100000), item);
            } else {
                linkedHashMap.put(item.getFullType(), item);
            }
        }

        return linkedHashMap;
    }

    public LinkedHashMap<String, InventoryItem> getAllItems(LinkedHashMap<String, InventoryItem> items, boolean inInv) {
        if (items == null) {
            items = new LinkedHashMap();
        }

        for (int int0 = 0; int0 < this.getItems().size(); int0++) {
            InventoryItem item = this.getItems().get(int0);
            if (inInv) {
                item.setWorker("inInv");
            }

            item.setCount(1);
            if (item.getCat() != ItemType.Drainable && item.getCat() != ItemType.Weapon && items.get(item.getFullType()) != null) {
                ((InventoryItem)items.get(item.getFullType())).setCount(((InventoryItem)items.get(item.getFullType())).getCount() + 1);
            } else if (items.get(item.getFullType()) != null) {
                items.put(item.getFullType() + Rand.Next(100000), item);
            } else {
                items.put(item.getFullType(), item);
            }

            if (item instanceof InventoryContainer
                && ((InventoryContainer)item).getItemContainer() != null
                && !((InventoryContainer)item).getItemContainer().getItems().isEmpty()) {
                items = ((InventoryContainer)item).getItemContainer().getAllItems(items, true);
            }
        }

        return items;
    }

    public InventoryItem getItemById(long id) {
        for (int int0 = 0; int0 < this.getItems().size(); int0++) {
            InventoryItem item = this.getItems().get(int0);
            if (item.getID() == id) {
                return item;
            }

            if (item instanceof InventoryContainer
                && ((InventoryContainer)item).getItemContainer() != null
                && !((InventoryContainer)item).getItemContainer().getItems().isEmpty()) {
                item = ((InventoryContainer)item).getItemContainer().getItemById(id);
                if (item != null) {
                    return item;
                }
            }
        }

        return null;
    }

    public void addItemsToProcessItems() {
        IsoWorld.instance.CurrentCell.addToProcessItems(this.Items);
    }

    public void removeItemsFromProcessItems() {
        IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this.Items);
        if (!"floor".equals(this.type)) {
            ItemSoundManager.removeItems(this.Items);
        }
    }

    public boolean isExistYet() {
        if (!SystemDisabler.doWorldSyncEnable) {
            return true;
        } else if (this.getCharacter() != null) {
            return true;
        } else if (this.getParent() instanceof BaseVehicle) {
            return true;
        } else if (this.parent instanceof IsoDeadBody) {
            return this.parent.getStaticMovingObjectIndex() != -1;
        } else if (this.parent instanceof IsoCompost) {
            return this.parent.getObjectIndex() != -1;
        } else if (this.containingItem != null && this.containingItem.worldItem != null) {
            return this.containingItem.worldItem.getWorldObjectIndex() != -1;
        } else if (this.getType().equals("floor")) {
            return true;
        } else if (this.SourceGrid == null) {
            return false;
        } else {
            IsoGridSquare square = this.SourceGrid;
            return !square.getObjects().contains(this.parent) ? false : this.parent.getContainerIndex(this) != -1;
        }
    }

    public String getContainerPosition() {
        return this.containerPosition;
    }

    public void setContainerPosition(String _containerPosition) {
        this.containerPosition = _containerPosition;
    }

    public String getFreezerPosition() {
        return this.freezerPosition;
    }

    public void setFreezerPosition(String _freezerPosition) {
        this.freezerPosition = _freezerPosition;
    }

    public VehiclePart getVehiclePart() {
        return this.vehiclePart;
    }

    private static final class CategoryPredicate implements Predicate<InventoryItem> {
        String category;

        ItemContainer.CategoryPredicate init(String string) {
            this.category = Objects.requireNonNull(string);
            return this;
        }

        public boolean test(InventoryItem item) {
            return item.getCategory().equals(this.category);
        }
    }

    private static final class Comparators {
        ObjectPool<ItemContainer.ConditionComparator> condition = new ObjectPool<>(ItemContainer.ConditionComparator::new);
        ObjectPool<ItemContainer.EvalComparator> eval = new ObjectPool<>(ItemContainer.EvalComparator::new);
        ObjectPool<ItemContainer.EvalArgComparator> evalArg = new ObjectPool<>(ItemContainer.EvalArgComparator::new);
    }

    private static final class ConditionComparator implements Comparator<InventoryItem> {
        public int compare(InventoryItem item1, InventoryItem item0) {
            return item1.getCondition() - item0.getCondition();
        }
    }

    private static final class EvalArgComparator implements Comparator<InventoryItem> {
        LuaClosure functionObj;
        Object arg;

        ItemContainer.EvalArgComparator init(LuaClosure luaClosure, Object object) {
            this.functionObj = Objects.requireNonNull(luaClosure);
            this.arg = object;
            return this;
        }

        public int compare(InventoryItem item0, InventoryItem item1) {
            LuaReturn luaReturn = LuaManager.caller.protectedCall(LuaManager.thread, this.functionObj, item0, item1, this.arg);
            if (luaReturn.isSuccess() && !luaReturn.isEmpty() && luaReturn.getFirst() instanceof Double) {
                double double0 = (Double)luaReturn.getFirst();
                return Double.compare(double0, 0.0);
            } else {
                return 0;
            }
        }
    }

    private static final class EvalArgPredicate implements Predicate<InventoryItem> {
        LuaClosure functionObj;
        Object arg;

        ItemContainer.EvalArgPredicate init(LuaClosure luaClosure, Object object) {
            this.functionObj = Objects.requireNonNull(luaClosure);
            this.arg = object;
            return this;
        }

        public boolean test(InventoryItem item) {
            return LuaManager.caller.protectedCallBoolean(LuaManager.thread, this.functionObj, item, this.arg) == Boolean.TRUE;
        }
    }

    private static final class EvalComparator implements Comparator<InventoryItem> {
        LuaClosure functionObj;

        ItemContainer.EvalComparator init(LuaClosure luaClosure) {
            this.functionObj = Objects.requireNonNull(luaClosure);
            return this;
        }

        public int compare(InventoryItem item0, InventoryItem item1) {
            LuaReturn luaReturn = LuaManager.caller.protectedCall(LuaManager.thread, this.functionObj, item0, item1);
            if (luaReturn.isSuccess() && !luaReturn.isEmpty() && luaReturn.getFirst() instanceof Double) {
                double double0 = (Double)luaReturn.getFirst();
                return Double.compare(double0, 0.0);
            } else {
                return 0;
            }
        }
    }

    private static final class EvalPredicate implements Predicate<InventoryItem> {
        LuaClosure functionObj;

        ItemContainer.EvalPredicate init(LuaClosure luaClosure) {
            this.functionObj = Objects.requireNonNull(luaClosure);
            return this;
        }

        public boolean test(InventoryItem item) {
            return LuaManager.caller.protectedCallBoolean(LuaManager.thread, this.functionObj, item) == Boolean.TRUE;
        }
    }

    private static final class InventoryItemList extends ArrayList<InventoryItem> {
        @Override
        public boolean equals(Object object) {
            return this == object;
        }
    }

    private static final class InventoryItemListPool extends ObjectPool<ItemContainer.InventoryItemList> {
        public InventoryItemListPool() {
            super(ItemContainer.InventoryItemList::new);
        }

        public void release(ItemContainer.InventoryItemList inventoryItemList) {
            inventoryItemList.clear();
            super.release(inventoryItemList);
        }
    }

    private static final class Predicates {
        final ObjectPool<ItemContainer.CategoryPredicate> category = new ObjectPool<>(ItemContainer.CategoryPredicate::new);
        final ObjectPool<ItemContainer.EvalPredicate> eval = new ObjectPool<>(ItemContainer.EvalPredicate::new);
        final ObjectPool<ItemContainer.EvalArgPredicate> evalArg = new ObjectPool<>(ItemContainer.EvalArgPredicate::new);
        final ObjectPool<ItemContainer.TagPredicate> tag = new ObjectPool<>(ItemContainer.TagPredicate::new);
        final ObjectPool<ItemContainer.TagEvalPredicate> tagEval = new ObjectPool<>(ItemContainer.TagEvalPredicate::new);
        final ObjectPool<ItemContainer.TagEvalArgPredicate> tagEvalArg = new ObjectPool<>(ItemContainer.TagEvalArgPredicate::new);
        final ObjectPool<ItemContainer.TypePredicate> type = new ObjectPool<>(ItemContainer.TypePredicate::new);
        final ObjectPool<ItemContainer.TypeEvalPredicate> typeEval = new ObjectPool<>(ItemContainer.TypeEvalPredicate::new);
        final ObjectPool<ItemContainer.TypeEvalArgPredicate> typeEvalArg = new ObjectPool<>(ItemContainer.TypeEvalArgPredicate::new);
    }

    private static final class TagEvalArgPredicate implements Predicate<InventoryItem> {
        String tag;
        LuaClosure functionObj;
        Object arg;

        ItemContainer.TagEvalArgPredicate init(String string, LuaClosure luaClosure, Object object) {
            this.tag = string;
            this.functionObj = Objects.requireNonNull(luaClosure);
            this.arg = object;
            return this;
        }

        public boolean test(InventoryItem item) {
            return item.hasTag(this.tag) && LuaManager.caller.protectedCallBoolean(LuaManager.thread, this.functionObj, item, this.arg) == Boolean.TRUE;
        }
    }

    private static final class TagEvalPredicate implements Predicate<InventoryItem> {
        String tag;
        LuaClosure functionObj;

        ItemContainer.TagEvalPredicate init(String string, LuaClosure luaClosure) {
            this.tag = string;
            this.functionObj = Objects.requireNonNull(luaClosure);
            return this;
        }

        public boolean test(InventoryItem item) {
            return item.hasTag(this.tag) && LuaManager.caller.protectedCallBoolean(LuaManager.thread, this.functionObj, item) == Boolean.TRUE;
        }
    }

    private static final class TagPredicate implements Predicate<InventoryItem> {
        String tag;

        ItemContainer.TagPredicate init(String string) {
            this.tag = Objects.requireNonNull(string);
            return this;
        }

        public boolean test(InventoryItem item) {
            return item.hasTag(this.tag);
        }
    }

    private static final class TypeEvalArgPredicate implements Predicate<InventoryItem> {
        String type;
        LuaClosure functionObj;
        Object arg;

        ItemContainer.TypeEvalArgPredicate init(String string, LuaClosure luaClosure, Object object) {
            this.type = string;
            this.functionObj = Objects.requireNonNull(luaClosure);
            this.arg = object;
            return this;
        }

        public boolean test(InventoryItem item) {
            return ItemContainer.compareType(this.type, item)
                && LuaManager.caller.protectedCallBoolean(LuaManager.thread, this.functionObj, item, this.arg) == Boolean.TRUE;
        }
    }

    private static final class TypeEvalPredicate implements Predicate<InventoryItem> {
        String type;
        LuaClosure functionObj;

        ItemContainer.TypeEvalPredicate init(String string, LuaClosure luaClosure) {
            this.type = string;
            this.functionObj = Objects.requireNonNull(luaClosure);
            return this;
        }

        public boolean test(InventoryItem item) {
            return ItemContainer.compareType(this.type, item)
                && LuaManager.caller.protectedCallBoolean(LuaManager.thread, this.functionObj, item) == Boolean.TRUE;
        }
    }

    private static final class TypePredicate implements Predicate<InventoryItem> {
        String type;

        ItemContainer.TypePredicate init(String string) {
            this.type = Objects.requireNonNull(string);
            return this;
        }

        public boolean test(InventoryItem item) {
            return ItemContainer.compareType(this.type, item);
        }
    }
}
