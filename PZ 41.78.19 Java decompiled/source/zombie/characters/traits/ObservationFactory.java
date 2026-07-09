// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.traits;

import java.util.ArrayList;
import java.util.HashMap;
import zombie.interfaces.IListBoxItem;

public final class ObservationFactory {
    public static HashMap<String, ObservationFactory.Observation> ObservationMap = new HashMap<>();

    public static void init() {
    }

    public static void setMutualExclusive(String a, String b) {
        ObservationMap.get(a).MutuallyExclusive.add(b);
        ObservationMap.get(b).MutuallyExclusive.add(a);
    }

    public static void addObservation(String type, String name, String desc) {
        ObservationMap.put(type, new ObservationFactory.Observation(type, name, desc));
    }

    public static ObservationFactory.Observation getObservation(String name) {
        return ObservationMap.containsKey(name) ? ObservationMap.get(name) : null;
    }

    public static class Observation implements IListBoxItem {
        private String traitID;
        private String name;
        private String description;
        public ArrayList<String> MutuallyExclusive = new ArrayList<>(0);

        public Observation(String tr, String _name, String desc) {
            this.setTraitID(tr);
            this.setName(_name);
            this.setDescription(desc);
        }

        @Override
        public String getLabel() {
            return this.getName();
        }

        @Override
        public String getLeftLabel() {
            return this.getName();
        }

        @Override
        public String getRightLabel() {
            return null;
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String _description) {
            this.description = _description;
        }

        public String getTraitID() {
            return this.traitID;
        }

        public void setTraitID(String _traitID) {
            this.traitID = _traitID;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String _name) {
            this.name = _name;
        }
    }
}
