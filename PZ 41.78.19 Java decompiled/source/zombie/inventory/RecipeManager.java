// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.skills.PerkFactory;
import zombie.debug.DebugLog;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.Moveable;
import zombie.iso.IsoGridSquare;
import zombie.network.GameClient;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.EvolvedRecipe;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.MovableRecipe;
import zombie.scripting.objects.Recipe;
import zombie.scripting.objects.ScriptModule;
import zombie.util.StringUtils;

public final class RecipeManager {
    private static final ArrayList<Recipe> RecipeList = new ArrayList<>();

    public static void Loaded() {
        ArrayList arrayList = ScriptManager.instance.getAllRecipes();
        HashSet hashSet = new HashSet();

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            Recipe recipe = (Recipe)arrayList.get(int0);

            for (int int1 = 0; int1 < recipe.getSource().size(); int1++) {
                Recipe.Source source = recipe.getSource().get(int1);

                for (int int2 = 0; int2 < source.getItems().size(); int2++) {
                    String string = source.getItems().get(int2);
                    if (!"Water".equals(string) && !string.contains(".") && !string.startsWith("[")) {
                        Item item0 = resolveItemModuleDotType(recipe, string, hashSet, "recipe source");
                        if (item0 == null) {
                            source.getItems().set(int2, "???." + string);
                        } else {
                            source.getItems().set(int2, item0.getFullName());
                        }
                    }
                }
            }

            if (recipe.getResult() != null && recipe.getResult().getModule() == null) {
                Item item1 = resolveItemModuleDotType(recipe, recipe.getResult().getType(), hashSet, "recipe result");
                if (item1 == null) {
                    recipe.getResult().module = "???";
                } else {
                    recipe.getResult().module = item1.getModule().getName();
                }
            }
        }
    }

    private static Item resolveItemModuleDotType(Recipe recipe, String string0, Set<String> set, String string2) {
        ScriptModule scriptModule0 = recipe.getModule();
        Item item = scriptModule0.getItem(string0);
        if (item != null && !item.getObsolete()) {
            return item;
        } else {
            for (int int0 = 0; int0 < ScriptManager.instance.ModuleList.size(); int0++) {
                ScriptModule scriptModule1 = ScriptManager.instance.ModuleList.get(int0);
                item = scriptModule1.getItem(string0);
                if (item != null && !item.getObsolete()) {
                    String string1 = recipe.getModule().getName();
                    if (!set.contains(string1)) {
                        set.add(string1);
                        DebugLog.Recipe.warn("WARNING: module \"%s\" may have forgot to import module Base", string1);
                    }

                    return item;
                }
            }

            DebugLog.Recipe.warn("ERROR: can't find %s \"%s\" in recipe \"%s\"", string2, string0, recipe.getOriginalname());
            return null;
        }
    }

    public static void LoadedAfterLua() {
        ArrayList arrayList0 = new ArrayList();
        ArrayList arrayList1 = ScriptManager.instance.getAllRecipes();

        for (int int0 = 0; int0 < arrayList1.size(); int0++) {
            Recipe recipe = (Recipe)arrayList1.get(int0);
            LoadedAfterLua(recipe, arrayList0);
        }

        arrayList0.clear();
    }

    private static void LoadedAfterLua(Recipe recipe, ArrayList<Item> arrayList) {
        LoadedAfterLua(recipe, recipe.LuaCreate, "LuaCreate");
        LoadedAfterLua(recipe, recipe.LuaGiveXP, "LuaGiveXP");
        LoadedAfterLua(recipe, recipe.LuaTest, "LuaTest");

        for (int int0 = 0; int0 < recipe.getSource().size(); int0++) {
            Recipe.Source source = recipe.getSource().get(int0);
            LoadedAfterLua(source, arrayList);
        }
    }

    private static void LoadedAfterLua(Recipe recipe, String string0, String string1) {
        if (!StringUtils.isNullOrWhitespace(string0)) {
            Object object = LuaManager.getFunctionObject(string0);
            if (object == null) {
                DebugLog.General.error("no such function %s = \"%s\" in recipe \"%s\"", string1, string0, recipe.name);
            }
        }
    }

    private static void LoadedAfterLua(Recipe.Source source, ArrayList<Item> arrayList) {
        for (int int0 = source.getItems().size() - 1; int0 >= 0; int0--) {
            String string0 = source.getItems().get(int0);
            if (string0.startsWith("[")) {
                source.getItems().remove(int0);
                String string1 = string0.substring(1, string0.indexOf("]"));
                Object object = LuaManager.getFunctionObject(string1);
                if (object != null) {
                    arrayList.clear();
                    LuaManager.caller.protectedCallVoid(LuaManager.thread, object, arrayList);

                    for (int int1 = 0; int1 < arrayList.size(); int1++) {
                        Item item = (Item)arrayList.get(int1);
                        source.getItems().add(int0 + int1, item.getFullName());
                    }
                }
            }
        }
    }

    public static boolean DoesWipeUseDelta(String itemToUse, String itemToMake) {
        return true;
    }

    public static int getKnownRecipesNumber(IsoGameCharacter chr) {
        int int0 = 0;
        ArrayList arrayList = ScriptManager.instance.getAllRecipes();

        for (int int1 = 0; int1 < arrayList.size(); int1++) {
            Recipe recipe = (Recipe)arrayList.get(int1);
            if (chr.isRecipeKnown(recipe)) {
                int0++;
            }
        }

        return int0;
    }

    public static boolean DoesUseItemUp(String itemToUse, Recipe recipe) {
        assert "Water".equals(itemToUse) || itemToUse.contains(".");

        for (int int0 = 0; int0 < recipe.Source.size(); int0++) {
            if (recipe.Source.get(int0).keep) {
                ArrayList arrayList = recipe.Source.get(int0).getItems();

                for (int int1 = 0; int1 < arrayList.size(); int1++) {
                    if (itemToUse.equals(arrayList.get(int1))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static boolean IsItemDestroyed(String itemToUse, Recipe recipe) {
        assert "Water".equals(itemToUse) || itemToUse.contains(".");

        for (int int0 = 0; int0 < recipe.Source.size(); int0++) {
            Recipe.Source source = recipe.getSource().get(int0);
            if (source.destroy) {
                for (int int1 = 0; int1 < source.getItems().size(); int1++) {
                    if (itemToUse.equals(source.getItems().get(int1))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static float UseAmount(String sourceFullType, Recipe recipe, IsoGameCharacter chr) {
        Recipe.Source source = recipe.findSource(sourceFullType);
        return source.getCount();
    }

    public static ArrayList<Recipe> getUniqueRecipeItems(InventoryItem item, IsoGameCharacter chr, ArrayList<ItemContainer> containers) {
        RecipeList.clear();
        ArrayList arrayList = ScriptManager.instance.getAllRecipes();

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            Recipe recipe = (Recipe)arrayList.get(int0);
            if (IsRecipeValid(recipe, chr, item, containers)
                && (!(item instanceof Clothing) || item.getCondition() > 0 || !recipe.getOriginalname().equalsIgnoreCase("rip clothing"))) {
                RecipeList.add(recipe);
            }
        }

        if (item instanceof Moveable && RecipeList.size() == 0 && ((Moveable)item).getWorldSprite() != null) {
            if (item.type != null && item.type.equalsIgnoreCase(((Moveable)item).getWorldSprite())) {
                MovableRecipe movableRecipe = new MovableRecipe();
                LuaEventManager.triggerEvent("OnDynamicMovableRecipe", ((Moveable)item).getWorldSprite(), movableRecipe, item, chr);
                if (movableRecipe.isValid() && IsRecipeValid(movableRecipe, chr, item, containers)) {
                    RecipeList.add(movableRecipe);
                }
            } else {
                DebugLog.log("RecipeManager -> Cannot create recipe for this movable item: " + item.getFullType());
            }
        }

        return RecipeList;
    }

    public static boolean IsRecipeValid(Recipe recipe, IsoGameCharacter chr, InventoryItem item, ArrayList<ItemContainer> containers) {
        if (recipe.Result == null) {
            return false;
        } else if (!chr.isRecipeKnown(recipe)) {
            return false;
        } else if (item != null && !RecipeContainsItem(recipe, item)) {
            return false;
        } else if (!HasAllRequiredItems(recipe, chr, item, containers)) {
            return false;
        } else if (!isAllItemsUsableRotten(recipe, chr, item, containers)) {
            return false;
        } else if (!HasRequiredSkill(recipe, chr)) {
            return false;
        } else if (!isNearItem(recipe, chr)) {
            return false;
        } else {
            return !hasHeat(recipe, item, containers, chr) ? false : CanPerform(recipe, chr, item);
        }
    }

    private static boolean isNearItem(Recipe recipe, IsoGameCharacter character) {
        if (recipe.getNearItem() != null && !recipe.getNearItem().equals("")) {
            for (int int0 = character.getSquare().getX() - 2; int0 < character.getSquare().getX() + 2; int0++) {
                for (int int1 = character.getSquare().getY() - 2; int1 < character.getSquare().getY() + 2; int1++) {
                    IsoGridSquare square = character.getCell().getGridSquare(int0, int1, character.getSquare().getZ());
                    if (square != null) {
                        for (int int2 = 0; int2 < square.getObjects().size(); int2++) {
                            if (recipe.getNearItem().equals(square.getObjects().get(int2).getName())) {
                                return true;
                            }
                        }
                    }
                }
            }

            return false;
        } else {
            return true;
        }
    }

    private static boolean CanPerform(Recipe recipe, IsoGameCharacter character, InventoryItem item) {
        if (StringUtils.isNullOrWhitespace(recipe.getCanPerform())) {
            return true;
        } else {
            Object object = LuaManager.getFunctionObject(recipe.getCanPerform());
            if (object == null) {
                return false;
            } else {
                Boolean boolean0 = LuaManager.caller.protectedCallBoolean(LuaManager.thread, object, recipe, character, item);
                return boolean0 == Boolean.TRUE;
            }
        }
    }

    private static boolean HasRequiredSkill(Recipe recipe, IsoGameCharacter character) {
        if (recipe.getRequiredSkillCount() == 0) {
            return true;
        } else {
            for (int int0 = 0; int0 < recipe.getRequiredSkillCount(); int0++) {
                Recipe.RequiredSkill requiredSkill = recipe.getRequiredSkill(int0);
                if (character.getPerkLevel(requiredSkill.getPerk()) < requiredSkill.getLevel()) {
                    return false;
                }
            }

            return true;
        }
    }

    private static boolean RecipeContainsItem(Recipe recipe, InventoryItem item) {
        for (int int0 = 0; int0 < recipe.Source.size(); int0++) {
            Recipe.Source source = recipe.getSource().get(int0);

            for (int int1 = 0; int1 < source.getItems().size(); int1++) {
                String string = source.getItems().get(int1);
                if ("Water".equals(string) && item.isWaterSource()) {
                    return true;
                }

                if (string.equals(item.getFullType())) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean HasAllRequiredItems(Recipe recipe, IsoGameCharacter chr, InventoryItem selectedItem, ArrayList<ItemContainer> containers) {
        ArrayList arrayList = getAvailableItemsNeeded(recipe, chr, containers, selectedItem, null);
        return !arrayList.isEmpty();
    }

    public static boolean isAllItemsUsableRotten(Recipe recipe, IsoGameCharacter character, InventoryItem item1, ArrayList<ItemContainer> arrayList) {
        if (character.getPerkLevel(PerkFactory.Perks.Cooking) >= 7) {
            return true;
        } else if (recipe.isAllowRottenItem()) {
            return true;
        } else {
            for (InventoryItem item0 : getAvailableItemsNeeded(recipe, character, arrayList, item1, null)) {
                if (item0 instanceof Food && ((Food)item0).isRotten()) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean hasHeat(Recipe recipe, InventoryItem item, ArrayList<ItemContainer> containers, IsoGameCharacter chr) {
        if (recipe.getHeat() == 0.0F) {
            return true;
        } else {
            InventoryItem item0 = null;

            for (InventoryItem item1 : getAvailableItemsNeeded(recipe, chr, containers, item, null)) {
                if (item1 instanceof DrainableComboItem) {
                    item0 = item1;
                    break;
                }
            }

            if (item0 != null) {
                for (ItemContainer container : containers) {
                    for (InventoryItem item2 : container.getItems()) {
                        if (item2.getName().equals(item0.getName())) {
                            if (recipe.getHeat() < 0.0F) {
                                if (item2.getInvHeat() <= recipe.getHeat()) {
                                    return true;
                                }
                            } else if (recipe.getHeat() > 0.0F && item2.getInvHeat() + 1.0F >= recipe.getHeat()) {
                                return true;
                            }
                        }
                    }
                }
            }

            return false;
        }
    }

    public static ArrayList<InventoryItem> getAvailableItemsAll(
        Recipe recipe, IsoGameCharacter chr, ArrayList<ItemContainer> containers, InventoryItem selectedItem, ArrayList<InventoryItem> ignoreItems
    ) {
        return getAvailableItems(recipe, chr, containers, selectedItem, ignoreItems, true).allItems;
    }

    public static ArrayList<InventoryItem> getAvailableItemsNeeded(
        Recipe recipe, IsoGameCharacter chr, ArrayList<ItemContainer> containers, InventoryItem selectedItem, ArrayList<InventoryItem> ignoreItems
    ) {
        return getAvailableItems(recipe, chr, containers, selectedItem, ignoreItems, false).allItems;
    }

    private static RecipeManager.SourceItems getAvailableItems(
        Recipe recipe,
        IsoGameCharacter character,
        ArrayList<ItemContainer> arrayList1,
        InventoryItem item,
        ArrayList<InventoryItem> arrayList0,
        boolean boolean0
    ) {
        if (item != null && (item.getContainer() == null || !item.getContainer().contains(item))) {
            DebugLog.Recipe.warn("recipe: item appears to have been used already, ignoring " + item.getFullType());
            item = null;
        }

        RecipeManager.SourceItems sourceItems = new RecipeManager.SourceItems(recipe, character, item, arrayList0);
        if (arrayList1 == null) {
            arrayList1 = new ArrayList();
            arrayList1.add(character.getInventory());
        }

        if (item != null && !RecipeContainsItem(recipe, item)) {
            throw new RuntimeException("item " + item.getFullType() + " isn't used in recipe " + recipe.getOriginalname());
        } else {
            RecipeManager.RMRecipe rMRecipe = RecipeManager.RMRecipe.alloc(recipe);
            rMRecipe.getItemsFromContainers(character, arrayList1, item);
            if (boolean0 || rMRecipe.hasItems()) {
                rMRecipe.getAvailableItems(sourceItems, boolean0);
            }

            RecipeManager.RMRecipe.release(rMRecipe);
            return sourceItems;
        }
    }

    public static ArrayList<InventoryItem> getSourceItemsAll(
        Recipe recipe,
        int sourceIndex,
        IsoGameCharacter chr,
        ArrayList<ItemContainer> containers,
        InventoryItem selectedItem,
        ArrayList<InventoryItem> ignoreItems
    ) {
        if (sourceIndex >= 0 && sourceIndex < recipe.getSource().size()) {
            RecipeManager.SourceItems sourceItems = getAvailableItems(recipe, chr, containers, selectedItem, ignoreItems, true);
            return sourceItems.itemsPerSource[sourceIndex];
        } else {
            return null;
        }
    }

    public static ArrayList<InventoryItem> getSourceItemsNeeded(
        Recipe recipe,
        int sourceIndex,
        IsoGameCharacter chr,
        ArrayList<ItemContainer> containers,
        InventoryItem selectedItem,
        ArrayList<InventoryItem> ignoreItems
    ) {
        if (sourceIndex >= 0 && sourceIndex < recipe.getSource().size()) {
            RecipeManager.SourceItems sourceItems = getAvailableItems(recipe, chr, containers, selectedItem, ignoreItems, false);
            return sourceItems.itemsPerSource[sourceIndex];
        } else {
            return null;
        }
    }

    public static int getNumberOfTimesRecipeCanBeDone(Recipe recipe, IsoGameCharacter chr, ArrayList<ItemContainer> containers, InventoryItem selectedItem) {
        int int0 = 0;
        RecipeManager.RMRecipe rMRecipe = RecipeManager.RMRecipe.alloc(recipe);
        if (containers == null) {
            containers = new ArrayList();
            containers.add(chr.getInventory());
        }

        rMRecipe.getItemsFromContainers(chr, containers, selectedItem);
        ArrayList arrayList0 = new ArrayList();

        for (ArrayList arrayList1 = new ArrayList(); rMRecipe.hasItems(); int0++) {
            arrayList1.clear();
            rMRecipe.Use(arrayList1);
            if (arrayList0.containsAll(arrayList1)) {
                int0 = -1;
                break;
            }

            arrayList0.addAll(arrayList1);

            for (int int1 = 0; int1 < arrayList1.size(); int1++) {
                InventoryItem item = (InventoryItem)arrayList1.get(int1);
                if (item instanceof Food && ((Food)item).isFrozen() && !rMRecipe.recipe.isAllowFrozenItem()) {
                    int0--;
                    break;
                }
            }
        }

        RecipeManager.RMRecipe.release(rMRecipe);
        return int0;
    }

    public static InventoryItem GetMovableRecipeTool(
        boolean isPrimary, Recipe recipe, InventoryItem selectedItem, IsoGameCharacter chr, ArrayList<ItemContainer> containers
    ) {
        if (!(recipe instanceof MovableRecipe movableRecipe)) {
            return null;
        } else {
            Recipe.Source source = isPrimary ? movableRecipe.getPrimaryTools() : movableRecipe.getSecondaryTools();
            if (source != null && source.getItems() != null && source.getItems().size() != 0) {
                RecipeManager.SourceItems sourceItems = getAvailableItems(recipe, chr, containers, selectedItem, null, false);
                if (sourceItems.allItems != null && sourceItems.allItems.size() != 0) {
                    for (int int0 = 0; int0 < sourceItems.allItems.size(); int0++) {
                        InventoryItem item = sourceItems.allItems.get(int0);

                        for (int int1 = 0; int1 < source.getItems().size(); int1++) {
                            if (item.getFullType().equalsIgnoreCase(source.getItems().get(int1))) {
                                return item;
                            }
                        }
                    }

                    return null;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    public static InventoryItem PerformMakeItem(Recipe recipe, InventoryItem selectedItem, IsoGameCharacter chr, ArrayList<ItemContainer> containers) {
        boolean boolean0 = chr.getPrimaryHandItem() == selectedItem;
        boolean boolean1 = chr.getSecondaryHandItem() == selectedItem;
        RecipeManager.SourceItems sourceItems = getAvailableItems(recipe, chr, containers, selectedItem, null, false);
        ArrayList arrayList0 = sourceItems.allItems;
        if (arrayList0.isEmpty()) {
            throw new RuntimeException("getAvailableItems() didn't return the required number of items");
        } else {
            chr.removeFromHands(selectedItem);
            Recipe.Result result = recipe.getResult();
            InventoryItem item0 = InventoryItemFactory.CreateItem(result.getFullType());
            boolean boolean2 = false;
            boolean boolean3 = false;
            int int0 = -1;
            int int1 = 0;
            boolean boolean4 = false;
            boolean boolean5 = false;
            float float0 = 0.0F;
            float float1 = 0.0F;
            int int2 = 0;
            int int3 = 0;

            for (int int4 = 0; int4 < recipe.getSource().size(); int4++) {
                Recipe.Source source = recipe.getSource().get(int4);
                if (!source.isKeep()) {
                    ArrayList arrayList1 = sourceItems.itemsPerSource[int4];
                    switch (sourceItems.typePerSource[int4]) {
                        case DRAINABLE:
                            int int5 = (int)source.getCount();
                            int int6 = 0;

                            for (; int6 < arrayList1.size(); int6++) {
                                InventoryItem item1 = (InventoryItem)arrayList1.get(int6);
                                int int7 = AvailableUses(item1);
                                if (int7 >= int5) {
                                    ReduceUses(item1, int5, chr);
                                    int5 = 0;
                                } else {
                                    ReduceUses(item1, int7, chr);
                                    int5 -= int7;
                                }
                            }

                            if (int5 > 0) {
                                throw new RuntimeException("required amount of " + source.getItems() + " wasn't available");
                            }
                            break;
                        case FOOD:
                            int int10 = (int)source.use;

                            for (int int11 = 0; int11 < arrayList1.size(); int11++) {
                                InventoryItem item4 = (InventoryItem)arrayList1.get(int11);
                                int int12 = AvailableUses(item4);
                                if (int12 >= int10) {
                                    ReduceUses(item4, int10, chr);
                                    int10 = 0;
                                } else {
                                    ReduceUses(item4, int12, chr);
                                    int10 -= int12;
                                }
                            }
                            break;
                        case DESTROY:
                            for (int int9 = 0; int9 < arrayList1.size(); int9++) {
                                InventoryItem item3 = (InventoryItem)arrayList1.get(int9);
                                ItemUser.RemoveItem(item3);
                            }
                            break;
                        case OTHER:
                            for (int int8 = 0; int8 < arrayList1.size(); int8++) {
                                InventoryItem item2 = (InventoryItem)arrayList1.get(int8);
                                ItemUser.UseItem(item2, true, false);
                            }
                            break;
                        case WATER:
                            int int13 = recipe.getWaterAmountNeeded();

                            for (int int14 = 0; int14 < arrayList1.size(); int14++) {
                                InventoryItem item5 = (InventoryItem)arrayList1.get(int14);
                                int int15 = AvailableUses(item5);
                                if (int15 >= int13) {
                                    ReduceUses(item5, int13, chr);
                                    int13 = 0;
                                } else {
                                    ReduceUses(item5, int15, chr);
                                    int13 -= int15;
                                }
                            }

                            if (int13 > 0) {
                                throw new RuntimeException("required amount of water wasn't available");
                            }
                    }
                }
            }

            for (int int16 = 0; int16 < arrayList0.size(); int16++) {
                InventoryItem item6 = (InventoryItem)arrayList0.get(int16);
                if (item6 instanceof Food) {
                    if (((Food)item6).isCooked()) {
                        boolean2 = true;
                    }

                    if (((Food)item6).isBurnt()) {
                        boolean3 = true;
                    }

                    int0 = ((Food)item6).getPoisonDetectionLevel();
                    int1 = ((Food)item6).getPoisonPower();
                    int3++;
                    if (item6.getAge() > item6.getOffAgeMax()) {
                        boolean4 = true;
                    } else if (!boolean4 && item6.getOffAgeMax() < 1000000000) {
                        if (item6.getAge() < item6.getOffAge()) {
                            float1 += 0.5F * item6.getAge() / item6.getOffAge();
                        } else {
                            boolean5 = true;
                            float1 += 0.5F + 0.5F * (item6.getAge() - item6.getOffAge()) / (item6.getOffAgeMax() - item6.getOffAge());
                        }
                    }
                }

                if (item0 instanceof Food && item6.isTaintedWater()) {
                    item0.setTaintedWater(true);
                }

                if (item0.getScriptItem() == item6.getScriptItem() && item6.isFavorite()) {
                    item0.setFavorite(true);
                }

                float0 += (float)item6.getCondition() / item6.getConditionMax();
                int2++;
            }

            float1 /= int3;
            if (item0 instanceof Food && ((Food)item0).IsCookable) {
                ((Food)item0).setCooked(boolean2);
                ((Food)item0).setBurnt(boolean3);
                ((Food)item0).setPoisonDetectionLevel(int0);
                ((Food)item0).setPoisonPower(int1);
            }

            if (item0.getOffAgeMax() != 1.0E9) {
                if (boolean4) {
                    item0.setAge(item0.getOffAgeMax());
                } else {
                    if (boolean5 && float1 < 0.5F) {
                        float1 = 0.5F;
                    }

                    if (float1 < 0.5F) {
                        item0.setAge(2.0F * float1 * item0.getOffAge());
                    } else {
                        item0.setAge(item0.getOffAge() + 2.0F * (float1 - 0.5F) * (item0.getOffAgeMax() - item0.getOffAge()));
                    }
                }
            }

            item0.setCondition(Math.round(item0.getConditionMax() * (float0 / int2)));

            for (int int17 = 0; int17 < arrayList0.size(); int17++) {
                InventoryItem item7 = (InventoryItem)arrayList0.get(int17);
                item0.setConditionFromModData(item7);
            }

            GivePlayerExperience(recipe, arrayList0, item0, chr);
            if (recipe.LuaCreate != null) {
                Object object = LuaManager.getFunctionObject(recipe.LuaCreate);
                if (object != null) {
                    LuaManager.caller.protectedCall(LuaManager.thread, object, arrayList0, item0, chr, selectedItem, boolean0, boolean1);
                }
            }

            return !recipe.isRemoveResultItem() ? item0 : null;
        }
    }

    private static boolean ReduceUses(InventoryItem item, float float1, IsoGameCharacter var2) {
        if (item instanceof DrainableComboItem drainableComboItem) {
            float float0 = drainableComboItem.getUseDelta() * float1;
            drainableComboItem.setUsedDelta(drainableComboItem.getUsedDelta() - float0);
            if (AvailableUses(item) < 1) {
                drainableComboItem.setUsedDelta(0.0F);
                ItemUser.UseItem(drainableComboItem);
                return true;
            }

            if (GameClient.bClient && !item.isInPlayerInventory()) {
                GameClient.instance.sendItemStats(item);
            }
        }

        if (item instanceof Food food && food.getHungerChange() < 0.0F) {
            float float2 = Math.min(-food.getHungerChange() * 100.0F, float1);
            float float3 = float2 / (-food.getHungerChange() * 100.0F);
            if (float3 < 0.0F) {
                float3 = 0.0F;
            }

            if (float3 > 1.0F) {
                float3 = 1.0F;
            }

            food.setHungChange(food.getHungChange() - food.getHungChange() * float3);
            food.setCalories(food.getCalories() - food.getCalories() * float3);
            food.setCarbohydrates(food.getCarbohydrates() - food.getCarbohydrates() * float3);
            food.setLipids(food.getLipids() - food.getLipids() * float3);
            food.setProteins(food.getProteins() - food.getProteins() * float3);
            food.setThirstChange(food.getThirstChangeUnmodified() - food.getThirstChangeUnmodified() * float3);
            food.setFluReduction(food.getFluReduction() - (int)(food.getFluReduction() * float3));
            food.setPainReduction(food.getPainReduction() - food.getPainReduction() * float3);
            food.setEndChange(food.getEnduranceChangeUnmodified() - food.getEnduranceChangeUnmodified() * float3);
            food.setReduceFoodSickness(food.getReduceFoodSickness() - (int)(food.getReduceFoodSickness() * float3));
            food.setStressChange(food.getStressChangeUnmodified() - food.getStressChangeUnmodified() * float3);
            food.setFatigueChange(food.getFatigueChange() - food.getFatigueChange() * float3);
            if (food.getHungerChange() > -0.01) {
                ItemUser.UseItem(food);
                return true;
            }

            if (GameClient.bClient && !item.isInPlayerInventory()) {
                GameClient.instance.sendItemStats(item);
            }
        }

        return false;
    }

    private static int AvailableUses(InventoryItem item) {
        if (item instanceof DrainableComboItem drainableComboItem) {
            return drainableComboItem.getDrainableUsesInt();
        } else {
            return item instanceof Food food ? (int)(-food.getHungerChange() * 100.0F) : 0;
        }
    }

    private static void GivePlayerExperience(Recipe recipe, ArrayList<InventoryItem> arrayList, InventoryItem item, IsoGameCharacter character) {
        String string = recipe.LuaGiveXP;
        if (string == null) {
            string = "Recipe.OnGiveXP.Default";
        }

        Object object = LuaManager.getFunctionObject(string);
        if (object == null) {
            DebugLog.Recipe.warn("ERROR: Lua method \"" + string + "\" not found (in RecipeManager.GivePlayerExperience())");
        } else {
            LuaManager.caller.protectedCall(LuaManager.thread, object, recipe, arrayList, item, character);
        }
    }

    public static ArrayList<EvolvedRecipe> getAllEvolvedRecipes() {
        Stack stack = ScriptManager.instance.getAllEvolvedRecipes();
        ArrayList arrayList = new ArrayList();

        for (int int0 = 0; int0 < stack.size(); int0++) {
            arrayList.add((EvolvedRecipe)stack.get(int0));
        }

        return arrayList;
    }

    public static ArrayList<EvolvedRecipe> getEvolvedRecipe(
        InventoryItem baseItem, IsoGameCharacter chr, ArrayList<ItemContainer> containers, boolean need1ingredient
    ) {
        ArrayList arrayList0 = new ArrayList();
        if (baseItem instanceof Food && ((Food)baseItem).isRotten() && chr.getPerkLevel(PerkFactory.Perks.Cooking) < 7) {
            return arrayList0;
        } else {
            Stack stack = ScriptManager.instance.getAllEvolvedRecipes();

            for (int int0 = 0; int0 < stack.size(); int0++) {
                EvolvedRecipe evolvedRecipe = (EvolvedRecipe)stack.get(int0);
                if ((baseItem.isCooked() && evolvedRecipe.addIngredientIfCooked || !baseItem.isCooked())
                    && (baseItem.getType().equals(evolvedRecipe.baseItem) || baseItem.getType().equals(evolvedRecipe.getResultItem()))
                    && (!baseItem.getType().equals("WaterPot") || !(((Drainable)baseItem).getUsedDelta() < 0.75))) {
                    if (need1ingredient) {
                        ArrayList arrayList1 = evolvedRecipe.getItemsCanBeUse(chr, baseItem, containers);
                        if (!arrayList1.isEmpty()) {
                            if (!(baseItem instanceof Food) || !((Food)baseItem).isFrozen()) {
                                arrayList0.add(evolvedRecipe);
                            } else if (evolvedRecipe.isAllowFrozenItem()) {
                                arrayList0.add(evolvedRecipe);
                            }
                        }
                    } else {
                        arrayList0.add(evolvedRecipe);
                    }
                }
            }

            return arrayList0;
        }
    }

    private static void DebugPrintAllRecipes() {
        ArrayList arrayList = ScriptManager.instance.getAllRecipes();

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            Recipe recipe = (Recipe)arrayList.get(int0);
            if (recipe == null) {
                DebugLog.Recipe.println("Null recipe.");
            } else if (recipe.Result == null) {
                DebugLog.Recipe.println("Null result.");
            } else {
                DebugLog.Recipe.println(recipe.Result.type);
                DebugLog.Recipe.println("-----");

                for (int int1 = 0; int1 < recipe.Source.size(); int1++) {
                    if (recipe.Source.get(int1) == null) {
                        DebugLog.Recipe.println("Null ingredient.");
                    } else if (recipe.Source.get(int1).getItems().isEmpty()) {
                        DebugLog.Recipe.println(recipe.Source.get(int1).getItems().toString());
                    }
                }
            }
        }
    }

    public static Recipe getDismantleRecipeFor(String item) {
        RecipeList.clear();
        ArrayList arrayList0 = ScriptManager.instance.getAllRecipes();

        for (int int0 = 0; int0 < arrayList0.size(); int0++) {
            Recipe recipe = (Recipe)arrayList0.get(int0);
            ArrayList arrayList1 = recipe.getSource();
            if (arrayList1.size() > 0) {
                for (int int1 = 0; int1 < arrayList1.size(); int1++) {
                    Recipe.Source source = (Recipe.Source)arrayList1.get(int1);

                    for (int int2 = 0; int2 < source.getItems().size(); int2++) {
                        if (source.getItems().get(int2).equalsIgnoreCase(item) && recipe.name.toLowerCase().startsWith("dismantle ")) {
                            return recipe;
                        }
                    }
                }
            }
        }

        return null;
    }

    private static final class RMRecipe {
        Recipe recipe;
        final ArrayList<RecipeManager.RMRecipeSource> sources = new ArrayList<>();
        final ArrayList<RecipeManager.RMRecipeItem> allItems = new ArrayList<>();
        boolean usesWater;
        final HashSet<String> allSourceTypes = new HashSet<>();
        static ArrayDeque<RecipeManager.RMRecipe> pool = new ArrayDeque<>();

        RecipeManager.RMRecipe init(Recipe recipex) {
            assert this.allItems.isEmpty();

            assert this.sources.isEmpty();

            assert this.allSourceTypes.isEmpty();

            this.recipe = recipex;
            this.usesWater = false;

            for (int int0 = 0; int0 < recipex.getSource().size(); int0++) {
                RecipeManager.RMRecipeSource rMRecipeSource = RecipeManager.RMRecipeSource.alloc(this, int0);
                if (rMRecipeSource.usesWater) {
                    this.usesWater = true;
                }

                this.allSourceTypes.addAll(rMRecipeSource.source.getItems());
                this.sources.add(rMRecipeSource);
            }

            return this;
        }

        RecipeManager.RMRecipe reset() {
            this.recipe = null;

            for (int int0 = 0; int0 < this.allItems.size(); int0++) {
                RecipeManager.RMRecipeItem.release(this.allItems.get(int0));
            }

            this.allItems.clear();

            for (int int1 = 0; int1 < this.sources.size(); int1++) {
                RecipeManager.RMRecipeSource.release(this.sources.get(int1));
            }

            this.sources.clear();
            this.allSourceTypes.clear();
            return this;
        }

        void getItemsFromContainers(IsoGameCharacter character, ArrayList<ItemContainer> arrayList, InventoryItem item) {
            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                this.getItemsFromContainer(character, (ItemContainer)arrayList.get(int0), item);
            }

            if (this.Test(item)) {
                for (int int1 = 0; int1 < this.sources.size(); int1++) {
                    RecipeManager.RMRecipeSource rMRecipeSource = this.sources.get(int1);
                    rMRecipeSource.getItemsFrom(this.allItems, this);
                }
            }
        }

        void getItemsFromContainer(IsoGameCharacter character, ItemContainer container, InventoryItem item1) {
            for (int int0 = 0; int0 < container.getItems().size(); int0++) {
                InventoryItem item0 = container.getItems().get(int0);
                if ((item1 != null && item1 == item0 || !character.isEquippedClothing(item0) || this.isKeep(item0.getFullType()))
                    && (!this.recipe.InSameInventory || this.isKeep(item0.getFullType()) || item1 == null || container == item1.getContainer())) {
                    if (this.usesWater && item0 instanceof DrainableComboItem && item0.isWaterSource()) {
                        this.allItems.add(RecipeManager.RMRecipeItem.alloc(item0));
                    } else if (this.allSourceTypes.contains(item0.getFullType())) {
                        this.allItems.add(RecipeManager.RMRecipeItem.alloc(item0));
                    }
                }
            }
        }

        boolean Test(InventoryItem item) {
            if (item != null && this.recipe.LuaTest != null) {
                Object object = LuaManager.getFunctionObject(this.recipe.LuaTest);
                if (object == null) {
                    return false;
                } else {
                    Boolean boolean0 = LuaManager.caller.protectedCallBoolean(LuaManager.thread, object, item, this.recipe.getResult());
                    return boolean0 == Boolean.TRUE;
                }
            } else {
                return true;
            }
        }

        boolean hasItems() {
            for (int int0 = 0; int0 < this.sources.size(); int0++) {
                RecipeManager.RMRecipeSource rMRecipeSource = this.sources.get(int0);
                if (!rMRecipeSource.hasItems()) {
                    return false;
                }
            }

            return true;
        }

        boolean isKeep(String string) {
            for (int int0 = 0; int0 < this.sources.size(); int0++) {
                RecipeManager.RMRecipeSource rMRecipeSource = this.sources.get(int0);
                if (rMRecipeSource.isKeep(string)) {
                    return true;
                }
            }

            return false;
        }

        void getAvailableItems(RecipeManager.SourceItems sourceItems, boolean boolean0) {
            assert boolean0 || this.hasItems();

            for (int int0 = 0; int0 < this.sources.size(); int0++) {
                RecipeManager.RMRecipeSource rMRecipeSource = this.sources.get(int0);

                assert boolean0 || rMRecipeSource.hasItems();

                rMRecipeSource.getAvailableItems(sourceItems, boolean0);
            }
        }

        void Use(ArrayList<InventoryItem> arrayList) {
            assert this.hasItems();

            for (int int0 = 0; int0 < this.sources.size(); int0++) {
                RecipeManager.RMRecipeSource rMRecipeSource = this.sources.get(int0);

                assert rMRecipeSource.hasItems();

                rMRecipeSource.Use(arrayList);
            }
        }

        static RecipeManager.RMRecipe alloc(Recipe recipex) {
            return pool.isEmpty() ? new RecipeManager.RMRecipe().init(recipex) : pool.pop().init(recipex);
        }

        static void release(RecipeManager.RMRecipe rMRecipe) {
            assert !pool.contains(rMRecipe);

            pool.push(rMRecipe.reset());
        }
    }

    private static final class RMRecipeItem {
        InventoryItem item;
        int uses;
        int water;
        static ArrayDeque<RecipeManager.RMRecipeItem> pool = new ArrayDeque<>();

        RecipeManager.RMRecipeItem init(InventoryItem itemx) {
            this.item = itemx;
            return this;
        }

        RecipeManager.RMRecipeItem reset() {
            this.item = null;
            this.uses = 0;
            this.water = 0;
            return this;
        }

        int Use(int int1) {
            int int0 = Math.min(this.uses, int1);
            this.uses -= int0;
            return int0;
        }

        int UseWater(int int1) {
            int int0 = Math.min(this.water, int1);
            this.water -= int0;
            return int0;
        }

        static RecipeManager.RMRecipeItem alloc(InventoryItem itemx) {
            return pool.isEmpty() ? new RecipeManager.RMRecipeItem().init(itemx) : pool.pop().init(itemx);
        }

        static void release(RecipeManager.RMRecipeItem rMRecipeItem) {
            assert !pool.contains(rMRecipeItem);

            pool.push(rMRecipeItem.reset());
        }
    }

    private static final class RMRecipeItemList {
        RecipeManager.RMRecipeSource source;
        final ArrayList<RecipeManager.RMRecipeItem> items = new ArrayList<>();
        int index;
        int usesNeeded;
        RecipeManager.RMRecipeItemList.Type type = RecipeManager.RMRecipeItemList.Type.NONE;
        static ArrayDeque<RecipeManager.RMRecipeItemList> pool = new ArrayDeque<>();

        RecipeManager.RMRecipeItemList init(RecipeManager.RMRecipeSource rMRecipeSource, int int0) {
            assert this.items.isEmpty();

            this.source = rMRecipeSource;
            this.index = int0;
            String string = rMRecipeSource.source.getItems().get(int0);
            this.usesNeeded = (int)rMRecipeSource.source.getCount();
            if ("Water".equals(string)) {
                this.type = RecipeManager.RMRecipeItemList.Type.WATER;
            } else if (rMRecipeSource.source.isDestroy()) {
                this.type = RecipeManager.RMRecipeItemList.Type.DESTROY;
            } else if (ScriptManager.instance.isDrainableItemType(string)) {
                this.type = RecipeManager.RMRecipeItemList.Type.DRAINABLE;
            } else if (rMRecipeSource.source.use > 0.0F) {
                this.usesNeeded = (int)rMRecipeSource.source.use;
                this.type = RecipeManager.RMRecipeItemList.Type.FOOD;
            } else {
                this.type = RecipeManager.RMRecipeItemList.Type.OTHER;
            }

            return this;
        }

        RecipeManager.RMRecipeItemList reset() {
            this.source = null;
            this.items.clear();
            return this;
        }

        void getItemsFrom(ArrayList<RecipeManager.RMRecipeItem> arrayList, RecipeManager.RMRecipe rMRecipe) {
            String string = this.source.source.getItems().get(this.index);

            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                RecipeManager.RMRecipeItem rMRecipeItem = (RecipeManager.RMRecipeItem)arrayList.get(int0);
                DrainableComboItem drainableComboItem = zombie.util.Type.tryCastTo(rMRecipeItem.item, DrainableComboItem.class);
                Food food = zombie.util.Type.tryCastTo(rMRecipeItem.item, Food.class);
                if ("Water".equals(string)) {
                    if (rMRecipe.Test(rMRecipeItem.item) && rMRecipeItem.item instanceof DrainableComboItem && rMRecipeItem.item.isWaterSource()) {
                        rMRecipeItem.water = RecipeManager.AvailableUses(rMRecipeItem.item);
                        this.items.add(rMRecipeItem);
                    }
                } else if (string.equals(rMRecipeItem.item.getFullType())
                    && (
                        !(rMRecipe.recipe.getHeat() > 0.0F)
                            || drainableComboItem == null
                            || !rMRecipeItem.item.IsCookable
                            || !(rMRecipeItem.item.getInvHeat() + 1.0F < rMRecipe.recipe.getHeat())
                    )
                    && (
                        !(rMRecipe.recipe.getHeat() < 0.0F)
                            || drainableComboItem == null
                            || !rMRecipeItem.item.IsCookable
                            || !(rMRecipeItem.item.getInvHeat() > rMRecipe.recipe.getHeat())
                    )
                    && (food == null || !(food.getFreezingTime() > 0.0F) || rMRecipe.recipe.isAllowFrozenItem())
                    && (!rMRecipe.recipe.noBrokenItems() || !rMRecipeItem.item.isBroken())
                    && (!"Clothing".equals(rMRecipeItem.item.getCategory()) || !rMRecipeItem.item.isFavorite())
                    && rMRecipe.Test(rMRecipeItem.item)) {
                    if (this.source.source.isDestroy()) {
                        rMRecipeItem.uses = 1;
                        this.items.add(rMRecipeItem);
                    } else if (drainableComboItem != null) {
                        rMRecipeItem.uses = RecipeManager.AvailableUses(rMRecipeItem.item);
                        this.items.add(rMRecipeItem);
                    } else if (this.source.source.use > 0.0F) {
                        if (rMRecipeItem.item instanceof Food) {
                            rMRecipeItem.uses = RecipeManager.AvailableUses(rMRecipeItem.item);
                            this.items.add(rMRecipeItem);
                        }
                    } else {
                        rMRecipeItem.uses = rMRecipeItem.item.getUses();
                        this.items.add(rMRecipeItem);
                    }
                }
            }
        }

        boolean hasItems() {
            String string = this.source.source.getItems().get(this.index);
            int int0 = 0;

            for (int int1 = 0; int1 < this.items.size(); int1++) {
                if ("Water".equals(string)) {
                    int0 += this.items.get(int1).water;
                } else {
                    int0 += this.items.get(int1).uses;
                }
            }

            return int0 >= this.usesNeeded;
        }

        int indexOf(InventoryItem item) {
            for (int int0 = 0; int0 < this.items.size(); int0++) {
                RecipeManager.RMRecipeItem rMRecipeItem = this.items.get(int0);
                if (rMRecipeItem.item == item) {
                    return int0;
                }
            }

            return -1;
        }

        void getAvailableItems(RecipeManager.SourceItems sourceItems, boolean boolean0) {
            if (boolean0) {
                this.Use(sourceItems.itemsPerSource[this.source.index]);
                sourceItems.typePerSource[this.source.index] = this.type;
                sourceItems.allItems.addAll(sourceItems.itemsPerSource[this.source.index]);
            } else {
                assert this.hasItems();

                if (sourceItems.selectedItem != null) {
                    int int0 = this.indexOf(sourceItems.selectedItem);
                    if (int0 != -1) {
                        RecipeManager.RMRecipeItem rMRecipeItem = this.items.remove(int0);
                        this.items.add(0, rMRecipeItem);
                    }
                }

                this.Use(sourceItems.itemsPerSource[this.source.index]);
                sourceItems.typePerSource[this.source.index] = this.type;
                sourceItems.allItems.addAll(sourceItems.itemsPerSource[this.source.index]);
            }
        }

        void Use(ArrayList<InventoryItem> arrayList) {
            String string = this.source.source.getItems().get(this.index);
            int int0 = this.usesNeeded;

            for (int int1 = 0; int1 < this.items.size(); int1++) {
                RecipeManager.RMRecipeItem rMRecipeItem = this.items.get(int1);
                if ("Water".equals(string) && rMRecipeItem.water > 0) {
                    int0 -= rMRecipeItem.UseWater(int0);
                    arrayList.add(rMRecipeItem.item);
                } else if (this.source.source.isKeep() && rMRecipeItem.uses > 0) {
                    int0 -= Math.min(rMRecipeItem.uses, int0);
                    arrayList.add(rMRecipeItem.item);
                } else if (rMRecipeItem.uses > 0) {
                    int0 -= rMRecipeItem.Use(int0);
                    arrayList.add(rMRecipeItem.item);
                }

                if (int0 <= 0) {
                    break;
                }
            }
        }

        static RecipeManager.RMRecipeItemList alloc(RecipeManager.RMRecipeSource rMRecipeSource, int int0) {
            return pool.isEmpty() ? new RecipeManager.RMRecipeItemList().init(rMRecipeSource, int0) : pool.pop().init(rMRecipeSource, int0);
        }

        static void release(RecipeManager.RMRecipeItemList rMRecipeItemList) {
            assert !pool.contains(rMRecipeItemList);

            pool.push(rMRecipeItemList.reset());
        }

        static enum Type {
            NONE,
            WATER,
            DRAINABLE,
            FOOD,
            OTHER,
            DESTROY;
        }
    }

    private static final class RMRecipeSource {
        RecipeManager.RMRecipe recipe;
        Recipe.Source source;
        int index;
        final ArrayList<RecipeManager.RMRecipeItemList> itemLists = new ArrayList<>();
        boolean usesWater;
        static ArrayDeque<RecipeManager.RMRecipeSource> pool = new ArrayDeque<>();

        RecipeManager.RMRecipeSource init(RecipeManager.RMRecipe rMRecipe, int int0) {
            this.recipe = rMRecipe;
            this.source = rMRecipe.recipe.getSource().get(int0);
            this.index = int0;

            assert this.itemLists.isEmpty();

            for (int int1 = 0; int1 < this.source.getItems().size(); int1++) {
                this.itemLists.add(RecipeManager.RMRecipeItemList.alloc(this, int1));
            }

            this.usesWater = this.source.getItems().contains("Water");
            return this;
        }

        RecipeManager.RMRecipeSource reset() {
            for (int int0 = 0; int0 < this.itemLists.size(); int0++) {
                RecipeManager.RMRecipeItemList.release(this.itemLists.get(int0));
            }

            this.itemLists.clear();
            return this;
        }

        void getItemsFrom(ArrayList<RecipeManager.RMRecipeItem> arrayList, RecipeManager.RMRecipe rMRecipe) {
            for (int int0 = 0; int0 < this.itemLists.size(); int0++) {
                RecipeManager.RMRecipeItemList rMRecipeItemList = this.itemLists.get(int0);
                rMRecipeItemList.getItemsFrom(arrayList, rMRecipe);
            }
        }

        boolean hasItems() {
            for (int int0 = 0; int0 < this.itemLists.size(); int0++) {
                RecipeManager.RMRecipeItemList rMRecipeItemList = this.itemLists.get(int0);
                if (rMRecipeItemList.hasItems()) {
                    return true;
                }
            }

            return false;
        }

        boolean isKeep(String string) {
            return this.source.getItems().contains(string) ? this.source.keep : false;
        }

        void getAvailableItems(RecipeManager.SourceItems sourceItems, boolean boolean0) {
            if (boolean0) {
                for (int int0 = 0; int0 < this.itemLists.size(); int0++) {
                    RecipeManager.RMRecipeItemList rMRecipeItemList0 = this.itemLists.get(int0);
                    rMRecipeItemList0.getAvailableItems(sourceItems, boolean0);
                }
            } else {
                int int1 = -1;

                for (int int2 = 0; int2 < this.itemLists.size(); int2++) {
                    RecipeManager.RMRecipeItemList rMRecipeItemList1 = this.itemLists.get(int2);
                    if (rMRecipeItemList1.hasItems()) {
                        InventoryItem item = rMRecipeItemList1.items.get(0).item;
                        if (sourceItems.selectedItem != null && rMRecipeItemList1.indexOf(sourceItems.selectedItem) != -1) {
                            int1 = int2;
                            break;
                        }

                        if (item != null && item instanceof HandWeapon && (item.isEquipped() || item.isInPlayerInventory())) {
                            int1 = int2;
                            break;
                        }

                        if (int1 == -1) {
                            int1 = int2;
                        }
                    }
                }

                if (int1 != -1) {
                    this.itemLists.get(int1).getAvailableItems(sourceItems, boolean0);
                }
            }
        }

        void Use(ArrayList<InventoryItem> arrayList) {
            assert this.hasItems();

            for (int int0 = 0; int0 < this.itemLists.size(); int0++) {
                RecipeManager.RMRecipeItemList rMRecipeItemList = this.itemLists.get(int0);
                if (rMRecipeItemList.hasItems()) {
                    rMRecipeItemList.Use(arrayList);
                    return;
                }
            }

            assert false;
        }

        static RecipeManager.RMRecipeSource alloc(RecipeManager.RMRecipe rMRecipe, int int0) {
            return pool.isEmpty() ? new RecipeManager.RMRecipeSource().init(rMRecipe, int0) : pool.pop().init(rMRecipe, int0);
        }

        static void release(RecipeManager.RMRecipeSource rMRecipeSource) {
            assert !pool.contains(rMRecipeSource);

            pool.push(rMRecipeSource.reset());
        }
    }

    private static final class SourceItems {
        InventoryItem selectedItem;
        final ArrayList<InventoryItem> allItems = new ArrayList<>();
        final ArrayList<InventoryItem>[] itemsPerSource;
        final RecipeManager.RMRecipeItemList.Type[] typePerSource;

        SourceItems(Recipe recipe, IsoGameCharacter var2, InventoryItem item, ArrayList<InventoryItem> var4) {
            this.itemsPerSource = new ArrayList[recipe.getSource().size()];

            for (int int0 = 0; int0 < this.itemsPerSource.length; int0++) {
                this.itemsPerSource[int0] = new ArrayList<>();
            }

            this.typePerSource = new RecipeManager.RMRecipeItemList.Type[recipe.getSource().size()];
            this.selectedItem = item;
        }

        public ArrayList<InventoryItem> getItems() {
            return this.allItems;
        }
    }
}
