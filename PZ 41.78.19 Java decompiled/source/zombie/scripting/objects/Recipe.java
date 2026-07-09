// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.scripting.objects;

import java.util.ArrayList;
import java.util.Arrays;
import zombie.characters.skills.PerkFactory;
import zombie.core.Translator;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.util.StringUtils;

public class Recipe extends BaseScriptObject {
    private boolean canBeDoneFromFloor = false;
    public float TimeToMake;
    public String Sound;
    protected String AnimNode;
    protected String Prop1;
    protected String Prop2;
    public final ArrayList<Recipe.Source> Source = new ArrayList<>();
    public Recipe.Result Result;
    public boolean AllowDestroyedItem;
    public boolean AllowFrozenItem;
    public boolean AllowRottenItem;
    public boolean InSameInventory;
    public String LuaTest;
    public String LuaCreate;
    public String LuaGrab;
    public String name;
    private String originalname;
    private String nearItem;
    private String LuaCanPerform;
    private String tooltip = null;
    public ArrayList<Recipe.RequiredSkill> skillRequired = null;
    public String LuaGiveXP;
    private boolean needToBeLearn = false;
    protected String category = null;
    protected boolean removeResultItem = false;
    private float heat = 0.0F;
    protected boolean stopOnWalk = true;
    protected boolean stopOnRun = true;
    public boolean hidden = false;

    public boolean isCanBeDoneFromFloor() {
        return this.canBeDoneFromFloor;
    }

    public void setCanBeDoneFromFloor(boolean _canBeDoneFromFloor) {
        this.canBeDoneFromFloor = _canBeDoneFromFloor;
    }

    public Recipe() {
        this.TimeToMake = 0.0F;
        this.Result = null;
        this.AllowDestroyedItem = false;
        this.AllowFrozenItem = false;
        this.AllowRottenItem = false;
        this.InSameInventory = false;
        this.LuaTest = null;
        this.LuaCreate = null;
        this.LuaGrab = null;
        this.name = "recipe";
        this.setOriginalname("recipe");
    }

    public int FindIndexOf(InventoryItem a) {
        return -1;
    }

    public ArrayList<Recipe.Source> getSource() {
        return this.Source;
    }

    public int getNumberOfNeededItem() {
        int int0 = 0;

        for (int int1 = 0; int1 < this.getSource().size(); int1++) {
            Recipe.Source source = this.getSource().get(int1);
            if (!source.getItems().isEmpty()) {
                int0 = (int)(int0 + source.getCount());
            }
        }

        return int0;
    }

    public float getTimeToMake() {
        return this.TimeToMake;
    }

    public String getName() {
        return this.name;
    }

    public String getFullType() {
        return this.module + "." + this.originalname;
    }

    @Override
    public void Load(String string0, String[] strings0) {
        this.name = Translator.getRecipeName(string0);
        this.originalname = string0;
        boolean boolean0 = false;

        for (int int0 = 0; int0 < strings0.length; int0++) {
            if (!strings0[int0].trim().isEmpty()) {
                if (strings0[int0].contains(":")) {
                    String[] strings1 = strings0[int0].split(":");
                    String string1 = strings1[0].trim();
                    String string2 = strings1[1].trim();
                    if (string1.equals("Override")) {
                        boolean0 = string2.trim().equalsIgnoreCase("true");
                    }

                    if (string1.equals("AnimNode")) {
                        this.AnimNode = string2.trim();
                    }

                    if (string1.equals("Prop1")) {
                        this.Prop1 = string2.trim();
                    }

                    if (string1.equals("Prop2")) {
                        this.Prop2 = string2.trim();
                    }

                    if (string1.equals("Time")) {
                        this.TimeToMake = Float.parseFloat(string2);
                    }

                    if (string1.equals("Sound")) {
                        this.Sound = string2.trim();
                    }

                    if (string1.equals("InSameInventory")) {
                        this.InSameInventory = Boolean.parseBoolean(string2);
                    }

                    if (string1.equals("Result")) {
                        this.DoResult(string2);
                    }

                    if (string1.equals("OnCanPerform")) {
                        this.LuaCanPerform = StringUtils.discardNullOrWhitespace(string2);
                    }

                    if (string1.equals("OnTest")) {
                        this.LuaTest = string2;
                    }

                    if (string1.equals("OnCreate")) {
                        this.LuaCreate = string2;
                    }

                    if (string1.equals("AllowDestroyedItem")) {
                        this.AllowDestroyedItem = Boolean.parseBoolean(string2);
                    }

                    if (string1.equals("AllowFrozenItem")) {
                        this.AllowFrozenItem = Boolean.parseBoolean(string2);
                    }

                    if (string1.equals("AllowRottenItem")) {
                        this.AllowRottenItem = Boolean.parseBoolean(string2);
                    }

                    if (string1.equals("OnGrab")) {
                        this.LuaGrab = string2;
                    }

                    if (string1.toLowerCase().equals("needtobelearn")) {
                        this.setNeedToBeLearn(string2.trim().equalsIgnoreCase("true"));
                    }

                    if (string1.toLowerCase().equals("category")) {
                        this.setCategory(string2.trim());
                    }

                    if (string1.equals("RemoveResultItem")) {
                        this.removeResultItem = string2.trim().equalsIgnoreCase("true");
                    }

                    if (string1.equals("CanBeDoneFromFloor")) {
                        this.setCanBeDoneFromFloor(string2.trim().equalsIgnoreCase("true"));
                    }

                    if (string1.equals("NearItem")) {
                        this.setNearItem(string2.trim());
                    }

                    if (string1.equals("SkillRequired")) {
                        this.skillRequired = new ArrayList<>();
                        String[] strings2 = string2.split(";");

                        for (int int1 = 0; int1 < strings2.length; int1++) {
                            String[] strings3 = strings2[int1].split("=");
                            PerkFactory.Perk perk = PerkFactory.Perks.FromString(strings3[0]);
                            if (perk == PerkFactory.Perks.MAX) {
                                DebugLog.Recipe.warn("Unknown skill \"%s\" in recipe \"%s\"", strings3, this.name);
                            } else {
                                int int2 = PZMath.tryParseInt(strings3[1], 1);
                                Recipe.RequiredSkill requiredSkill = new Recipe.RequiredSkill(perk, int2);
                                this.skillRequired.add(requiredSkill);
                            }
                        }
                    }

                    if (string1.equals("OnGiveXP")) {
                        this.LuaGiveXP = string2;
                    }

                    if (string1.equalsIgnoreCase("Tooltip")) {
                        this.tooltip = StringUtils.discardNullOrWhitespace(string2);
                    }

                    if (string1.equals("Obsolete") && string2.trim().toLowerCase().equals("true")) {
                        this.module.RecipeMap.remove(this);
                        this.module.RecipeByName.remove(this.getOriginalname());
                        this.module.RecipesWithDotInName.remove(this);
                        return;
                    }

                    if (string1.equals("Heat")) {
                        this.heat = Float.parseFloat(string2);
                    }

                    if (string1.equals("NoBrokenItems")) {
                        this.AllowDestroyedItem = !StringUtils.tryParseBoolean(string2);
                    }

                    if (string1.equals("StopOnWalk")) {
                        this.stopOnWalk = string2.trim().equalsIgnoreCase("true");
                    }

                    if (string1.equals("StopOnRun")) {
                        this.stopOnRun = string2.trim().equalsIgnoreCase("true");
                    }

                    if (string1.equals("IsHidden")) {
                        this.hidden = string2.trim().equalsIgnoreCase("true");
                    }
                } else {
                    this.DoSource(strings0[int0].trim());
                }
            }
        }

        if (boolean0) {
            Recipe recipe1 = this.module.getRecipe(string0);
            if (recipe1 != null && recipe1 != this) {
                this.module.RecipeMap.remove(recipe1);
                this.module.RecipeByName.put(string0, this);
            }
        }
    }

    public void DoSource(String type) {
        Recipe.Source source = new Recipe.Source();
        if (type.contains("=")) {
            source.count = new Float(type.split("=")[1].trim());
            type = type.split("=")[0].trim();
        }

        if (type.indexOf("keep") == 0) {
            type = type.replace("keep ", "");
            source.keep = true;
        }

        if (type.contains(";")) {
            String[] strings = type.split(";");
            type = strings[0];
            source.use = Float.parseFloat(strings[1]);
        }

        if (type.indexOf("destroy") == 0) {
            type = type.replace("destroy ", "");
            source.destroy = true;
        }

        if (type.equals("null")) {
            source.getItems().clear();
        } else if (type.contains("/")) {
            type = type.replaceFirst("keep ", "").trim();
            source.getItems().addAll(Arrays.asList(type.split("/")));
        } else {
            source.getItems().add(type);
        }

        if (!type.isEmpty()) {
            this.Source.add(source);
        }
    }

    public void DoResult(String type) {
        Recipe.Result result = new Recipe.Result();
        if (type.contains("=")) {
            String[] strings0 = type.split("=");
            type = strings0[0].trim();
            result.count = Integer.parseInt(strings0[1].trim());
        }

        if (type.contains(";")) {
            String[] strings1 = type.split(";");
            type = strings1[0].trim();
            result.drainableCount = Integer.parseInt(strings1[1].trim());
        }

        if (type.contains(".")) {
            result.type = type.split("\\.")[1];
            result.module = type.split("\\.")[0];
        } else {
            result.type = type;
        }

        this.Result = result;
    }

    public Recipe.Result getResult() {
        return this.Result;
    }

    public String getSound() {
        return this.Sound;
    }

    public void setSound(String sound) {
        this.Sound = sound;
    }

    public String getOriginalname() {
        return this.originalname;
    }

    public void setOriginalname(String _originalname) {
        this.originalname = _originalname;
    }

    public boolean needToBeLearn() {
        return this.needToBeLearn;
    }

    public void setNeedToBeLearn(boolean _needToBeLearn) {
        this.needToBeLearn = _needToBeLearn;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String _category) {
        this.category = _category;
    }

    public ArrayList<String> getRequiredSkills() {
        ArrayList arrayList = null;
        if (this.skillRequired != null) {
            arrayList = new ArrayList();

            for (int int0 = 0; int0 < this.skillRequired.size(); int0++) {
                Recipe.RequiredSkill requiredSkill = this.skillRequired.get(int0);
                PerkFactory.Perk perk = PerkFactory.getPerk(requiredSkill.perk);
                if (perk == null) {
                    arrayList.add(requiredSkill.perk.name + " " + requiredSkill.level);
                } else {
                    String string = perk.name + " " + requiredSkill.level;
                    arrayList.add(string);
                }
            }
        }

        return arrayList;
    }

    public int getRequiredSkillCount() {
        return this.skillRequired == null ? 0 : this.skillRequired.size();
    }

    public Recipe.RequiredSkill getRequiredSkill(int index) {
        return this.skillRequired != null && index >= 0 && index < this.skillRequired.size() ? this.skillRequired.get(index) : null;
    }

    public void clearRequiredSkills() {
        if (this.skillRequired != null) {
            this.skillRequired.clear();
        }
    }

    public void addRequiredSkill(PerkFactory.Perk perk, int level) {
        if (this.skillRequired == null) {
            this.skillRequired = new ArrayList<>();
        }

        this.skillRequired.add(new Recipe.RequiredSkill(perk, level));
    }

    public Recipe.Source findSource(String sourceFullType) {
        for (int int0 = 0; int0 < this.Source.size(); int0++) {
            Recipe.Source source = this.Source.get(int0);

            for (int int1 = 0; int1 < source.getItems().size(); int1++) {
                if (source.getItems().get(int1).equals(sourceFullType)) {
                    return source;
                }
            }
        }

        return null;
    }

    public boolean isDestroy(String sourceFullType) {
        Recipe.Source source = this.findSource(sourceFullType);
        if (source != null) {
            return source.isDestroy();
        } else {
            throw new RuntimeException("recipe " + this.getOriginalname() + " doesn't use item " + sourceFullType);
        }
    }

    public boolean isKeep(String sourceFullType) {
        Recipe.Source source = this.findSource(sourceFullType);
        if (source != null) {
            return source.isKeep();
        } else {
            throw new RuntimeException("recipe " + this.getOriginalname() + " doesn't use item " + sourceFullType);
        }
    }

    public float getHeat() {
        return this.heat;
    }

    public boolean noBrokenItems() {
        return !this.AllowDestroyedItem;
    }

    public boolean isAllowDestroyedItem() {
        return this.AllowDestroyedItem;
    }

    public void setAllowDestroyedItem(boolean allow) {
        this.AllowDestroyedItem = allow;
    }

    public boolean isAllowFrozenItem() {
        return this.AllowFrozenItem;
    }

    public void setAllowFrozenItem(boolean allow) {
        this.AllowFrozenItem = allow;
    }

    public boolean isAllowRottenItem() {
        return this.AllowRottenItem;
    }

    public void setAllowRottenItem(boolean boolean0) {
        this.AllowRottenItem = boolean0;
    }

    public int getWaterAmountNeeded() {
        Recipe.Source source = this.findSource("Water");
        return source != null ? (int)source.getCount() : 0;
    }

    public String getNearItem() {
        return this.nearItem;
    }

    public void setNearItem(String _nearItem) {
        this.nearItem = _nearItem;
    }

    public String getCanPerform() {
        return this.LuaCanPerform;
    }

    public void setCanPerform(String functionName) {
        this.LuaCanPerform = functionName;
    }

    public String getLuaTest() {
        return this.LuaTest;
    }

    public void setLuaTest(String functionName) {
        this.LuaTest = functionName;
    }

    public String getLuaCreate() {
        return this.LuaCreate;
    }

    public void setLuaCreate(String functionName) {
        this.LuaCreate = functionName;
    }

    public String getLuaGrab() {
        return this.LuaGrab;
    }

    public void setLuaGrab(String functionName) {
        this.LuaGrab = functionName;
    }

    public String getLuaGiveXP() {
        return this.LuaGiveXP;
    }

    public void setLuaGiveXP(String functionName) {
        this.LuaGiveXP = functionName;
    }

    public boolean isRemoveResultItem() {
        return this.removeResultItem;
    }

    public void setRemoveResultItem(boolean _removeResultItem) {
        this.removeResultItem = _removeResultItem;
    }

    public String getAnimNode() {
        return this.AnimNode;
    }

    public void setAnimNode(String animNode) {
        this.AnimNode = animNode;
    }

    public String getProp1() {
        return this.Prop1;
    }

    public void setProp1(String prop1) {
        this.Prop1 = prop1;
    }

    public String getProp2() {
        return this.Prop2;
    }

    public void setProp2(String prop2) {
        this.Prop2 = prop2;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public void setStopOnWalk(boolean stop) {
        this.stopOnWalk = stop;
    }

    public boolean isStopOnWalk() {
        return this.stopOnWalk;
    }

    public void setStopOnRun(boolean stop) {
        this.stopOnRun = stop;
    }

    public boolean isStopOnRun() {
        return this.stopOnRun;
    }

    public void setIsHidden(boolean hide) {
        this.hidden = hide;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public boolean isInSameInventory() {
        return this.InSameInventory;
    }

    public static final class RequiredSkill {
        private final PerkFactory.Perk perk;
        private final int level;

        public RequiredSkill(PerkFactory.Perk _perk, int _level) {
            this.perk = _perk;
            this.level = _level;
        }

        public PerkFactory.Perk getPerk() {
            return this.perk;
        }

        public int getLevel() {
            return this.level;
        }
    }

    public static final class Result {
        public String module = null;
        public String type;
        public int count = 1;
        public int drainableCount = 0;

        public String getType() {
            return this.type;
        }

        public void setType(String _type) {
            this.type = _type;
        }

        public int getCount() {
            return this.count;
        }

        public void setCount(int _count) {
            this.count = _count;
        }

        public String getModule() {
            return this.module;
        }

        public void setModule(String _module) {
            this.module = _module;
        }

        public String getFullType() {
            return this.module + "." + this.type;
        }

        public int getDrainableCount() {
            return this.drainableCount;
        }

        public void setDrainableCount(int _count) {
            this.drainableCount = _count;
        }
    }

    public static final class Source {
        public boolean keep = false;
        private final ArrayList<String> items = new ArrayList<>();
        public boolean destroy = false;
        public float count = 1.0F;
        public float use = 0.0F;

        public boolean isDestroy() {
            return this.destroy;
        }

        public void setDestroy(boolean _destroy) {
            this.destroy = _destroy;
        }

        public boolean isKeep() {
            return this.keep;
        }

        public void setKeep(boolean _keep) {
            this.keep = _keep;
        }

        public float getCount() {
            return this.count;
        }

        public void setCount(float _count) {
            this.count = _count;
        }

        public float getUse() {
            return this.use;
        }

        public void setUse(float _use) {
            this.use = _use;
        }

        public ArrayList<String> getItems() {
            return this.items;
        }

        public String getOnlyItem() {
            if (this.items.size() != 1) {
                throw new RuntimeException("items.size() == " + this.items.size());
            } else {
                return this.items.get(0);
            }
        }
    }
}
