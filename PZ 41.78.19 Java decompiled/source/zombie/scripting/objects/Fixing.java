// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.scripting.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.DrainableComboItem;
import zombie.util.Type;

public final class Fixing extends BaseScriptObject {
    private String name = null;
    private ArrayList<String> require = null;
    private final LinkedList<Fixing.Fixer> fixers = new LinkedList<>();
    private Fixing.Fixer globalItem = null;
    private float conditionModifier = 1.0F;
    private static final Fixing.PredicateRequired s_PredicateRequired = new Fixing.PredicateRequired();
    private static final ArrayList<InventoryItem> s_InventoryItems = new ArrayList<>();

    @Override
    public void Load(String string0, String[] strings0) {
        this.setName(string0);

        for (int int0 = 0; int0 < strings0.length; int0++) {
            if (!strings0[int0].trim().isEmpty() && strings0[int0].contains(":")) {
                String[] strings1 = strings0[int0].split(":");
                String string1 = strings1[0].trim();
                String string2 = strings1[1].trim();
                if (string1.equals("Require")) {
                    List list0 = Arrays.asList(string2.split(";"));

                    for (int int1 = 0; int1 < list0.size(); int1++) {
                        this.addRequiredItem(((String)list0.get(int1)).trim());
                    }
                } else if (!string1.equals("Fixer")) {
                    if (string1.equals("GlobalItem")) {
                        if (string2.contains("=")) {
                            this.setGlobalItem(new Fixing.Fixer(string2.split("=")[0], null, Integer.parseInt(string2.split("=")[1])));
                        } else {
                            this.setGlobalItem(new Fixing.Fixer(string2, null, 1));
                        }
                    } else if (string1.equals("ConditionModifier")) {
                        this.setConditionModifier(Float.parseFloat(string2.trim()));
                    }
                } else if (!string2.contains(";")) {
                    if (string2.contains("=")) {
                        this.fixers.add(new Fixing.Fixer(string2.split("=")[0], null, Integer.parseInt(string2.split("=")[1])));
                    } else {
                        this.fixers.add(new Fixing.Fixer(string2, null, 1));
                    }
                } else {
                    LinkedList linkedList = new LinkedList();
                    List list1 = Arrays.asList(string2.split(";"));

                    for (int int2 = 1; int2 < list1.size(); int2++) {
                        String[] strings2 = ((String)list1.get(int2)).trim().split("=");
                        linkedList.add(new Fixing.FixerSkill(strings2[0].trim(), Integer.parseInt(strings2[1].trim())));
                    }

                    if (string2.split(";")[0].trim().contains("=")) {
                        String[] strings3 = string2.split(";")[0].trim().split("=");
                        this.fixers.add(new Fixing.Fixer(strings3[0], linkedList, Integer.parseInt(strings3[1])));
                    } else {
                        this.fixers.add(new Fixing.Fixer(string2.split(";")[0].trim(), linkedList, 1));
                    }
                }
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    public ArrayList<String> getRequiredItem() {
        return this.require;
    }

    public void addRequiredItem(String _require) {
        if (this.require == null) {
            this.require = new ArrayList<>();
        }

        this.require.add(_require);
    }

    public LinkedList<Fixing.Fixer> getFixers() {
        return this.fixers;
    }

    public Fixing.Fixer usedInFixer(InventoryItem itemType, IsoGameCharacter chr) {
        for (int int0 = 0; int0 < this.getFixers().size(); int0++) {
            Fixing.Fixer fixer = this.getFixers().get(int0);
            if (fixer.getFixerName().equals(itemType.getType())) {
                if (itemType instanceof DrainableComboItem drainableComboItem) {
                    if (!(drainableComboItem.getUsedDelta() < 1.0F)) {
                        return fixer;
                    }

                    if (drainableComboItem.getDrainableUsesInt() >= fixer.getNumberOfUse()) {
                        return fixer;
                    }
                } else if (chr.getInventory().getCountTypeRecurse(this.getModule().getName() + "." + fixer.getFixerName()) >= fixer.getNumberOfUse()) {
                    return fixer;
                }
            }
        }

        return null;
    }

    public InventoryItem haveGlobalItem(IsoGameCharacter chr) {
        s_InventoryItems.clear();
        ArrayList arrayList = this.getRequiredFixerItems(chr, this.getGlobalItem(), null, s_InventoryItems);
        return arrayList == null ? null : (InventoryItem)arrayList.get(0);
    }

    public InventoryItem haveThisFixer(IsoGameCharacter chr, Fixing.Fixer fixer, InventoryItem brokenObject) {
        s_InventoryItems.clear();
        ArrayList arrayList = this.getRequiredFixerItems(chr, fixer, brokenObject, s_InventoryItems);
        return arrayList == null ? null : (InventoryItem)arrayList.get(0);
    }

    public int countUses(IsoGameCharacter chr, Fixing.Fixer fixer, InventoryItem brokenObject) {
        s_InventoryItems.clear();
        s_PredicateRequired.uses = 0;
        this.getRequiredFixerItems(chr, fixer, brokenObject, s_InventoryItems);
        return s_PredicateRequired.uses;
    }

    private static int countUses(InventoryItem item) {
        DrainableComboItem drainableComboItem = Type.tryCastTo(item, DrainableComboItem.class);
        return drainableComboItem != null ? drainableComboItem.getDrainableUsesInt() : 1;
    }

    public ArrayList<InventoryItem> getRequiredFixerItems(IsoGameCharacter chr, Fixing.Fixer fixer, InventoryItem brokenItem, ArrayList<InventoryItem> items) {
        if (fixer == null) {
            return null;
        } else {
            assert Thread.currentThread() == GameWindow.GameThread;

            Fixing.PredicateRequired predicateRequired = s_PredicateRequired;
            predicateRequired.fixer = fixer;
            predicateRequired.brokenItem = brokenItem;
            predicateRequired.uses = 0;
            chr.getInventory().getAllRecurse(predicateRequired, items);
            return predicateRequired.uses >= fixer.getNumberOfUse() ? items : null;
        }
    }

    public ArrayList<InventoryItem> getRequiredItems(IsoGameCharacter chr, Fixing.Fixer fixer, InventoryItem brokenItem) {
        ArrayList arrayList = new ArrayList();
        if (this.getRequiredFixerItems(chr, fixer, brokenItem, arrayList) == null) {
            arrayList.clear();
            return null;
        } else if (this.getGlobalItem() != null && this.getRequiredFixerItems(chr, this.getGlobalItem(), brokenItem, arrayList) == null) {
            arrayList.clear();
            return null;
        } else {
            return arrayList;
        }
    }

    public Fixing.Fixer getGlobalItem() {
        return this.globalItem;
    }

    public void setGlobalItem(Fixing.Fixer _globalItem) {
        this.globalItem = _globalItem;
    }

    public float getConditionModifier() {
        return this.conditionModifier;
    }

    public void setConditionModifier(float _conditionModifier) {
        this.conditionModifier = _conditionModifier;
    }

    public static final class Fixer {
        private String fixerName = null;
        private LinkedList<Fixing.FixerSkill> skills = null;
        private int numberOfUse = 1;

        public Fixer(String name, LinkedList<Fixing.FixerSkill> _skills, int _numberOfUse) {
            this.fixerName = name;
            this.skills = _skills;
            this.numberOfUse = _numberOfUse;
        }

        public String getFixerName() {
            return this.fixerName;
        }

        public LinkedList<Fixing.FixerSkill> getFixerSkills() {
            return this.skills;
        }

        public int getNumberOfUse() {
            return this.numberOfUse;
        }
    }

    public static final class FixerSkill {
        private String skillName = null;
        private int skillLvl = 0;

        public FixerSkill(String _skillName, int _skillLvl) {
            this.skillName = _skillName;
            this.skillLvl = _skillLvl;
        }

        public String getSkillName() {
            return this.skillName;
        }

        public int getSkillLevel() {
            return this.skillLvl;
        }
    }

    private static final class PredicateRequired implements Predicate<InventoryItem> {
        Fixing.Fixer fixer;
        InventoryItem brokenItem;
        int uses;

        public boolean test(InventoryItem arg0) {
            if (this.uses >= this.fixer.getNumberOfUse()) {
                return false;
            } else if (arg0 == this.brokenItem) {
                return false;
            } else if (!this.fixer.getFixerName().equals(arg0.getType())) {
                return false;
            } else {
                int int0 = Fixing.countUses(arg0);
                if (int0 > 0) {
                    this.uses += int0;
                    return true;
                } else {
                    return false;
                }
            }
        }
    }
}
