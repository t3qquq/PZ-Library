// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.population;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAttribute;
import zombie.util.StringUtils;

public final class HairStyle {
    public String name = "";
    public String model;
    public String texture = "F_Hair_White";
    public final ArrayList<HairStyle.Alternate> alternate = new ArrayList<>();
    public int level = 0;
    public final ArrayList<String> trimChoices = new ArrayList<>();
    public boolean growReference = false;
    public boolean attachedHair = false;
    public boolean noChoose = false;

    public boolean isValid() {
        return !StringUtils.isNullOrWhitespace(this.model) && !StringUtils.isNullOrWhitespace(this.texture);
    }

    public String getAlternate(String category) {
        for (int int0 = 0; int0 < this.alternate.size(); int0++) {
            HairStyle.Alternate alternatex = this.alternate.get(int0);
            if (category.equalsIgnoreCase(alternatex.category)) {
                return alternatex.style;
            }
        }

        return this.name;
    }

    public int getLevel() {
        return this.level;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<String> getTrimChoices() {
        return this.trimChoices;
    }

    public boolean isAttachedHair() {
        return this.attachedHair;
    }

    public boolean isGrowReference() {
        return this.growReference;
    }

    public boolean isNoChoose() {
        return this.noChoose;
    }

    public static final class Alternate {
        @XmlAttribute
        public String category;
        @XmlAttribute
        public String style;
    }
}
