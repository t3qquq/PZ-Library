// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.population;

import java.util.ArrayList;
import zombie.util.StringUtils;

public class BeardStyle {
    public String name = "";
    public String model;
    public String texture = "F_Hair_White";
    public int level = 0;
    public ArrayList<String> trimChoices = new ArrayList<>();
    public boolean growReference = false;

    public boolean isValid() {
        return !StringUtils.isNullOrWhitespace(this.model) && !StringUtils.isNullOrWhitespace(this.texture);
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
}
