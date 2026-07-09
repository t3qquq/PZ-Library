// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.scripting.objects;

import java.util.Arrays;
import zombie.characters.skills.PerkFactory;
import zombie.debug.DebugLog;

/**
 * TurboTuTone.
 */
public class MovableRecipe extends Recipe {
    private boolean isValid = false;
    private String worldSprite = "";
    private PerkFactory.Perk xpPerk = PerkFactory.Perks.MAX;
    private Recipe.Source primaryTools;
    private Recipe.Source secondaryTools;

    public MovableRecipe() {
        this.AnimNode = "Disassemble";
        this.removeResultItem = true;
        this.AllowDestroyedItem = false;
        this.name = "Disassemble Movable";
        this.setCanBeDoneFromFloor(false);
    }

    public void setResult(String resultItem, int count) {
        Recipe.Result result = new Recipe.Result();
        result.count = count;
        if (resultItem.contains(".")) {
            result.type = resultItem.split("\\.")[1];
            result.module = resultItem.split("\\.")[0];
        } else {
            DebugLog.log("MovableRecipe invalid result item. item = " + resultItem);
        }

        this.Result = result;
    }

    public void setSource(String sourceItem) {
        Recipe.Source source = new Recipe.Source();
        source.getItems().add(sourceItem);
        this.Source.add(source);
    }

    public void setTool(String tools, boolean isPrimary) {
        Recipe.Source source = new Recipe.Source();
        source.keep = true;
        if (tools.contains("/")) {
            tools = tools.replaceFirst("keep ", "").trim();
            source.getItems().addAll(Arrays.asList(tools.split("/")));
        } else {
            source.getItems().add(tools);
        }

        if (isPrimary) {
            this.primaryTools = source;
        } else {
            this.secondaryTools = source;
        }

        this.Source.add(source);
    }

    public Recipe.Source getPrimaryTools() {
        return this.primaryTools;
    }

    public Recipe.Source getSecondaryTools() {
        return this.secondaryTools;
    }

    public void setRequiredSkill(PerkFactory.Perk perk, int level) {
        Recipe.RequiredSkill requiredSkill = new Recipe.RequiredSkill(perk, level);
        this.skillRequired.add(requiredSkill);
    }

    public void setXpPerk(PerkFactory.Perk perk) {
        this.xpPerk = perk;
    }

    public PerkFactory.Perk getXpPerk() {
        return this.xpPerk;
    }

    public boolean hasXpPerk() {
        return this.xpPerk != PerkFactory.Perks.MAX;
    }

    public void setOnCreate(String onCreate) {
        this.LuaCreate = onCreate;
    }

    public void setOnXP(String onXP) {
        this.LuaGiveXP = onXP;
    }

    public void setTime(float time) {
        this.TimeToMake = time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorldSprite() {
        return this.worldSprite;
    }

    public void setWorldSprite(String _worldSprite) {
        this.worldSprite = _worldSprite;
    }

    public boolean isValid() {
        return this.isValid;
    }

    public void setValid(boolean valid) {
        this.isValid = valid;
    }
}
