// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public final class TilePropertyAliasMap {
    public static final TilePropertyAliasMap instance = new TilePropertyAliasMap();
    public final HashMap<String, Integer> PropertyToID = new HashMap<>();
    public final ArrayList<TilePropertyAliasMap.TileProperty> Properties = new ArrayList<>();

    public void Generate(HashMap<String, ArrayList<String>> hashMap) {
        this.Properties.clear();
        this.PropertyToID.clear();

        for (Entry entry : hashMap.entrySet()) {
            String string0 = (String)entry.getKey();
            ArrayList arrayList0 = (ArrayList)entry.getValue();
            this.PropertyToID.put(string0, this.Properties.size());
            TilePropertyAliasMap.TileProperty tileProperty = new TilePropertyAliasMap.TileProperty();
            this.Properties.add(tileProperty);
            tileProperty.propertyName = string0;
            tileProperty.possibleValues.addAll(arrayList0);
            ArrayList arrayList1 = tileProperty.possibleValues;

            for (int int0 = 0; int0 < arrayList1.size(); int0++) {
                String string1 = (String)arrayList1.get(int0);
                tileProperty.idMap.put(string1, int0);
            }
        }
    }

    public int getIDFromPropertyName(String string) {
        return !this.PropertyToID.containsKey(string) ? -1 : this.PropertyToID.get(string);
    }

    public int getIDFromPropertyValue(int int0, String string) {
        TilePropertyAliasMap.TileProperty tileProperty = this.Properties.get(int0);
        if (tileProperty.possibleValues.isEmpty()) {
            return 0;
        } else {
            return !tileProperty.idMap.containsKey(string) ? 0 : tileProperty.idMap.get(string);
        }
    }

    public String getPropertyValueString(int int0, int int1) {
        TilePropertyAliasMap.TileProperty tileProperty = this.Properties.get(int0);
        return tileProperty.possibleValues.isEmpty() ? "" : tileProperty.possibleValues.get(int1);
    }

    public static final class TileProperty {
        public String propertyName;
        public final ArrayList<String> possibleValues = new ArrayList<>();
        public final HashMap<String, Integer> idMap = new HashMap<>();
    }
}
