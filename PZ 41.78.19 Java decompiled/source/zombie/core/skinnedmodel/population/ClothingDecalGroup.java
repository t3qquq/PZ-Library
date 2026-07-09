// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.population;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;

public class ClothingDecalGroup {
    @XmlElement(
        name = "name"
    )
    public String m_Name;
    @XmlElement(
        name = "decal"
    )
    public final ArrayList<String> m_Decals = new ArrayList<>();
    @XmlElement(
        name = "group"
    )
    public final ArrayList<String> m_Groups = new ArrayList<>();
    private final ArrayList<String> tempDecals = new ArrayList<>();

    public String getRandomDecal() {
        this.tempDecals.clear();
        this.getDecals(this.tempDecals);
        String string = OutfitRNG.pickRandom(this.tempDecals);
        return string == null ? null : string;
    }

    public void getDecals(ArrayList<String> arrayList) {
        arrayList.addAll(this.m_Decals);

        for (int int0 = 0; int0 < this.m_Groups.size(); int0++) {
            ClothingDecalGroup clothingDecalGroup1 = ClothingDecals.instance.FindGroup(this.m_Groups.get(int0));
            if (clothingDecalGroup1 != null) {
                clothingDecalGroup1.getDecals(arrayList);
            }
        }
    }
}
