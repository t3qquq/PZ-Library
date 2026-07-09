// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.AttachedItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.Lua.LuaManager;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoWorld;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.StringUtils;

public final class AttachedWeaponDefinitions {
    public static final AttachedWeaponDefinitions instance = new AttachedWeaponDefinitions();
    public boolean m_dirty = true;
    public int m_chanceOfAttachedWeapon;
    public final ArrayList<AttachedWeaponDefinition> m_definitions = new ArrayList<>();
    public final ArrayList<AttachedWeaponCustomOutfit> m_outfitDefinitions = new ArrayList<>();

    public void checkDirty() {
        if (this.m_dirty) {
            this.m_dirty = false;
            this.init();
        }
    }

    public void addRandomAttachedWeapon(IsoZombie zombie0) {
        if (!"Tutorial".equals(Core.getInstance().getGameMode())) {
            this.checkDirty();
            if (!this.m_definitions.isEmpty()) {
                ArrayList arrayList = AttachedWeaponDefinitions.L_addRandomAttachedWeapon.definitions;
                arrayList.clear();
                int int0 = 1;
                AttachedWeaponCustomOutfit attachedWeaponCustomOutfit = null;
                Outfit outfit = zombie0.getHumanVisual().getOutfit();
                if (outfit != null) {
                    for (int int1 = 0; int1 < this.m_outfitDefinitions.size(); int1++) {
                        attachedWeaponCustomOutfit = this.m_outfitDefinitions.get(int1);
                        if (attachedWeaponCustomOutfit.outfit.equals(outfit.m_Name) && OutfitRNG.Next(100) < attachedWeaponCustomOutfit.chance) {
                            arrayList.addAll(attachedWeaponCustomOutfit.weapons);
                            int0 = attachedWeaponCustomOutfit.maxitem > -1 ? attachedWeaponCustomOutfit.maxitem : 1;
                            break;
                        }

                        attachedWeaponCustomOutfit = null;
                    }
                }

                if (arrayList.isEmpty()) {
                    if (OutfitRNG.Next(100) > this.m_chanceOfAttachedWeapon) {
                        return;
                    }

                    arrayList.addAll(this.m_definitions);
                }

                while (int0 > 0) {
                    AttachedWeaponDefinition attachedWeaponDefinition = this.pickRandomInList(arrayList, zombie0);
                    if (attachedWeaponDefinition == null) {
                        return;
                    }

                    arrayList.remove(attachedWeaponDefinition);
                    int0--;
                    this.addAttachedWeapon(attachedWeaponDefinition, zombie0);
                    if (attachedWeaponCustomOutfit != null && OutfitRNG.Next(100) >= attachedWeaponCustomOutfit.chance) {
                        return;
                    }
                }
            }
        }
    }

    private void addAttachedWeapon(AttachedWeaponDefinition attachedWeaponDefinition, IsoZombie zombie0) {
        String string = OutfitRNG.pickRandom(attachedWeaponDefinition.weapons);
        InventoryItem item0 = InventoryItemFactory.CreateItem(string);
        if (item0 != null) {
            if (item0 instanceof HandWeapon) {
                ((HandWeapon)item0).randomizeBullets();
            }

            item0.setCondition(OutfitRNG.Next(Math.max(2, item0.getConditionMax() - 5), item0.getConditionMax()));
            zombie0.setAttachedItem(OutfitRNG.pickRandom(attachedWeaponDefinition.weaponLocation), item0);
            if (attachedWeaponDefinition.ensureItem != null && !this.outfitHasItem(zombie0, attachedWeaponDefinition.ensureItem)) {
                Item item1 = ScriptManager.instance.FindItem(attachedWeaponDefinition.ensureItem);
                if (item1 != null && item1.getClothingItemAsset() != null) {
                    zombie0.getHumanVisual().addClothingItem(zombie0.getItemVisuals(), item1);
                } else {
                    zombie0.addItemToSpawnAtDeath(InventoryItemFactory.CreateItem(attachedWeaponDefinition.ensureItem));
                }
            }

            if (!attachedWeaponDefinition.bloodLocations.isEmpty()) {
                for (int int0 = 0; int0 < attachedWeaponDefinition.bloodLocations.size(); int0++) {
                    BloodBodyPartType bloodBodyPartType = attachedWeaponDefinition.bloodLocations.get(int0);
                    zombie0.addBlood(bloodBodyPartType, true, true, true);
                    zombie0.addBlood(bloodBodyPartType, true, true, true);
                    zombie0.addBlood(bloodBodyPartType, true, true, true);
                    if (attachedWeaponDefinition.addHoles) {
                        zombie0.addHole(bloodBodyPartType);
                        zombie0.addHole(bloodBodyPartType);
                        zombie0.addHole(bloodBodyPartType);
                        zombie0.addHole(bloodBodyPartType);
                    }
                }
            }
        }
    }

    private AttachedWeaponDefinition pickRandomInList(ArrayList<AttachedWeaponDefinition> arrayList1, IsoZombie zombie0) {
        AttachedWeaponDefinition attachedWeaponDefinition0 = null;
        int int0 = 0;
        ArrayList arrayList0 = AttachedWeaponDefinitions.L_addRandomAttachedWeapon.possibilities;
        arrayList0.clear();

        for (int int1 = 0; int1 < arrayList1.size(); int1++) {
            AttachedWeaponDefinition attachedWeaponDefinition1 = (AttachedWeaponDefinition)arrayList1.get(int1);
            if (attachedWeaponDefinition1.daySurvived > 0) {
                if (IsoWorld.instance.getWorldAgeDays() > attachedWeaponDefinition1.daySurvived) {
                    int0 += attachedWeaponDefinition1.chance;
                    arrayList0.add(attachedWeaponDefinition1);
                }
            } else if (!attachedWeaponDefinition1.outfit.isEmpty()) {
                if (zombie0.getHumanVisual().getOutfit() != null && attachedWeaponDefinition1.outfit.contains(zombie0.getHumanVisual().getOutfit().m_Name)) {
                    int0 += attachedWeaponDefinition1.chance;
                    arrayList0.add(attachedWeaponDefinition1);
                }
            } else {
                int0 += attachedWeaponDefinition1.chance;
                arrayList0.add(attachedWeaponDefinition1);
            }
        }

        int int2 = OutfitRNG.Next(int0);
        int int3 = 0;

        for (int int4 = 0; int4 < arrayList0.size(); int4++) {
            AttachedWeaponDefinition attachedWeaponDefinition2 = (AttachedWeaponDefinition)arrayList0.get(int4);
            int3 += attachedWeaponDefinition2.chance;
            if (int2 < int3) {
                attachedWeaponDefinition0 = attachedWeaponDefinition2;
                break;
            }
        }

        return attachedWeaponDefinition0;
    }

    public boolean outfitHasItem(IsoZombie zombie0, String string) {
        assert string.contains(".");

        ItemVisuals itemVisuals = zombie0.getItemVisuals();

        for (int int0 = 0; int0 < itemVisuals.size(); int0++) {
            ItemVisual itemVisual = itemVisuals.get(int0);
            if (StringUtils.equals(itemVisual.getItemType(), string)) {
                return true;
            }

            if ("Base.HolsterSimple".equals(string) && StringUtils.equals(itemVisual.getItemType(), "Base.HolsterDouble")) {
                return true;
            }

            if ("Base.HolsterDouble".equals(string) && StringUtils.equals(itemVisual.getItemType(), "Base.HolsterSimple")) {
                return true;
            }
        }

        return false;
    }

    private void init() {
        this.m_definitions.clear();
        this.m_outfitDefinitions.clear();
        KahluaTableImpl kahluaTableImpl0 = (KahluaTableImpl)LuaManager.env.rawget("AttachedWeaponDefinitions");
        if (kahluaTableImpl0 != null) {
            this.m_chanceOfAttachedWeapon = kahluaTableImpl0.rawgetInt("chanceOfAttachedWeapon");

            for (Entry entry0 : kahluaTableImpl0.delegate.entrySet()) {
                if (entry0.getValue() instanceof KahluaTableImpl) {
                    KahluaTableImpl kahluaTableImpl1 = (KahluaTableImpl)entry0.getValue();
                    if ("attachedWeaponCustomOutfit".equals(entry0.getKey())) {
                        KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)entry0.getValue();

                        for (Entry entry1 : kahluaTableImpl2.delegate.entrySet()) {
                            AttachedWeaponCustomOutfit attachedWeaponCustomOutfit = this.initOutfit((String)entry1.getKey(), (KahluaTableImpl)entry1.getValue());
                            if (attachedWeaponCustomOutfit != null) {
                                this.m_outfitDefinitions.add(attachedWeaponCustomOutfit);
                            }
                        }
                    } else {
                        AttachedWeaponDefinition attachedWeaponDefinition = this.init((String)entry0.getKey(), kahluaTableImpl1);
                        if (attachedWeaponDefinition != null) {
                            this.m_definitions.add(attachedWeaponDefinition);
                        }
                    }
                }
            }

            Collections.sort(
                this.m_definitions,
                (attachedWeaponDefinition1, attachedWeaponDefinition0) -> attachedWeaponDefinition1.id.compareTo(attachedWeaponDefinition0.id)
            );
        }
    }

    private AttachedWeaponCustomOutfit initOutfit(String string, KahluaTableImpl kahluaTableImpl0) {
        AttachedWeaponCustomOutfit attachedWeaponCustomOutfit = new AttachedWeaponCustomOutfit();
        attachedWeaponCustomOutfit.outfit = string;
        attachedWeaponCustomOutfit.chance = kahluaTableImpl0.rawgetInt("chance");
        attachedWeaponCustomOutfit.maxitem = kahluaTableImpl0.rawgetInt("maxitem");
        KahluaTableImpl kahluaTableImpl1 = (KahluaTableImpl)kahluaTableImpl0.rawget("weapons");

        for (Entry entry : kahluaTableImpl1.delegate.entrySet()) {
            KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)entry.getValue();
            AttachedWeaponDefinition attachedWeaponDefinition = this.init(kahluaTableImpl2.rawgetStr("id"), kahluaTableImpl2);
            if (attachedWeaponDefinition != null) {
                attachedWeaponCustomOutfit.weapons.add(attachedWeaponDefinition);
            }
        }

        return attachedWeaponCustomOutfit;
    }

    private AttachedWeaponDefinition init(String string, KahluaTableImpl kahluaTableImpl0) {
        AttachedWeaponDefinition attachedWeaponDefinition = new AttachedWeaponDefinition();
        attachedWeaponDefinition.id = string;
        attachedWeaponDefinition.chance = kahluaTableImpl0.rawgetInt("chance");
        this.tableToArrayList(kahluaTableImpl0, "outfit", attachedWeaponDefinition.outfit);
        this.tableToArrayList(kahluaTableImpl0, "weaponLocation", attachedWeaponDefinition.weaponLocation);
        KahluaTableImpl kahluaTableImpl1 = (KahluaTableImpl)kahluaTableImpl0.rawget("bloodLocations");
        if (kahluaTableImpl1 != null) {
            KahluaTableIterator kahluaTableIterator = kahluaTableImpl1.iterator();

            while (kahluaTableIterator.advance()) {
                BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromString(kahluaTableIterator.getValue().toString());
                if (bloodBodyPartType != BloodBodyPartType.MAX) {
                    attachedWeaponDefinition.bloodLocations.add(bloodBodyPartType);
                }
            }
        }

        attachedWeaponDefinition.addHoles = kahluaTableImpl0.rawgetBool("addHoles");
        attachedWeaponDefinition.daySurvived = kahluaTableImpl0.rawgetInt("daySurvived");
        attachedWeaponDefinition.ensureItem = kahluaTableImpl0.rawgetStr("ensureItem");
        this.tableToArrayList(kahluaTableImpl0, "weapons", attachedWeaponDefinition.weapons);
        Collections.sort(attachedWeaponDefinition.weaponLocation);
        Collections.sort(attachedWeaponDefinition.bloodLocations);
        Collections.sort(attachedWeaponDefinition.weapons);
        return attachedWeaponDefinition;
    }

    private void tableToArrayList(KahluaTable table, String string, ArrayList<String> arrayList) {
        KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)table.rawget(string);
        if (kahluaTableImpl != null) {
            int int0 = 1;

            for (int int1 = kahluaTableImpl.len(); int0 <= int1; int0++) {
                Object object = kahluaTableImpl.rawget(int0);
                if (object != null) {
                    arrayList.add(object.toString());
                }
            }
        }
    }

    private static final class L_addRandomAttachedWeapon {
        static final ArrayList<AttachedWeaponDefinition> possibilities = new ArrayList<>();
        static final ArrayList<AttachedWeaponDefinition> definitions = new ArrayList<>();
    }
}
