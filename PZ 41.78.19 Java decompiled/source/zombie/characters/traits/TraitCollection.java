// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.traits;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import zombie.util.Lambda;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;

public class TraitCollection {
    private final List<String> m_activeTraitNames = new ArrayList<>();
    private final List<TraitCollection.TraitSlot> m_traits = new ArrayList<>();

    public boolean remove(Object o) {
        return this.remove(String.valueOf(o));
    }

    public boolean remove(String name) {
        int int0 = this.indexOfTrait(name);
        if (int0 > -1) {
            this.deactivateTraitSlot(int0);
        }

        return int0 > -1;
    }

    public void addAll(Collection<? extends String> c) {
        PZArrayUtil.forEach(c, this::add);
    }

    public void removeAll(Collection<?> c) {
        PZArrayUtil.forEach(c, this::remove);
    }

    public void clear() {
        PZArrayUtil.forEach(this.m_traits, traitSlot -> traitSlot.m_isSet = false);
        this.m_activeTraitNames.clear();
    }

    public int size() {
        return this.m_activeTraitNames.size();
    }

    public boolean isEmpty() {
        return this.m_activeTraitNames.isEmpty();
    }

    public boolean contains(Object o) {
        return this.contains(String.valueOf(o));
    }

    public boolean contains(String trait) {
        int int0 = this.indexOfTrait(trait);
        return int0 > -1 && this.getSlotInternal(int0).m_isSet;
    }

    public void add(String trait) {
        if (trait != null) {
            this.getOrCreateSlotInternal(trait).m_isSet = true;
            this.m_activeTraitNames.add(trait);
        }
    }

    public String get(int n) {
        return this.m_activeTraitNames.get(n);
    }

    public void set(String name, boolean val) {
        if (val) {
            this.add(name);
        } else {
            this.remove(name);
        }
    }

    public TraitCollection.TraitSlot getTraitSlot(String name) {
        return StringUtils.isNullOrWhitespace(name) ? null : this.getOrCreateSlotInternal(name);
    }

    private int indexOfTrait(String string) {
        return PZArrayUtil.indexOf(this.m_traits, Lambda.predicate(string, TraitCollection.TraitSlot::isName));
    }

    private TraitCollection.TraitSlot getSlotInternal(int int0) {
        return this.m_traits.get(int0);
    }

    private TraitCollection.TraitSlot getOrCreateSlotInternal(String string) {
        int int0 = this.indexOfTrait(string);
        if (int0 == -1) {
            int0 = this.m_traits.size();
            this.m_traits.add(new TraitCollection.TraitSlot(string));
        }

        return this.getSlotInternal(int0);
    }

    private void deactivateTraitSlot(int int0) {
        TraitCollection.TraitSlot traitSlot = this.getSlotInternal(int0);
        traitSlot.m_isSet = false;
        int int1 = PZArrayUtil.indexOf(this.m_activeTraitNames, Lambda.predicate(traitSlot.Name, String::equalsIgnoreCase));
        if (int1 != -1) {
            this.m_activeTraitNames.remove(int1);
        }
    }

    @Override
    public String toString() {
        return "TraitCollection(" + PZArrayUtil.arrayToString(this.m_activeTraitNames, "", "", ", ") + ")";
    }

    public class TraitSlot {
        public final String Name;
        private boolean m_isSet;

        private TraitSlot(String string) {
            this.Name = string;
            this.m_isSet = false;
        }

        public boolean isName(String name) {
            return StringUtils.equalsIgnoreCase(this.Name, name);
        }

        public boolean isSet() {
            return this.m_isSet;
        }

        public void set(boolean val) {
            if (this.m_isSet != val) {
                TraitCollection.this.set(this.Name, val);
            }
        }

        @Override
        public String toString() {
            return "TraitSlot(" + this.Name + ":" + this.m_isSet + ")";
        }
    }
}
