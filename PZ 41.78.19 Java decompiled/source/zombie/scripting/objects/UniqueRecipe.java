// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.scripting.objects;

import java.util.ArrayList;

public final class UniqueRecipe extends BaseScriptObject {
    private String name = null;
    private String baseRecipe = null;
    private final ArrayList<String> items = new ArrayList<>();
    private int hungerBonus = 0;
    private int hapinessBonus = 0;
    private int boredomBonus = 0;

    public UniqueRecipe(String _name) {
        this.setName(_name);
    }

    @Override
    public void Load(String var1, String[] strings0) {
        for (int int0 = 0; int0 < strings0.length; int0++) {
            if (!strings0[int0].trim().isEmpty() && strings0[int0].contains(":")) {
                String[] strings1 = strings0[int0].split(":");
                String string0 = strings1[0].trim();
                String string1 = strings1[1].trim();
                if (string0.equals("BaseRecipeItem")) {
                    this.setBaseRecipe(string1);
                } else if (string0.equals("Item")) {
                    this.items.add(string1);
                } else if (string0.equals("Hunger")) {
                    this.setHungerBonus(Integer.parseInt(string1));
                } else if (string0.equals("Hapiness")) {
                    this.setHapinessBonus(Integer.parseInt(string1));
                } else if (string0.equals("Boredom")) {
                    this.setBoredomBonus(Integer.parseInt(string1));
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

    public String getBaseRecipe() {
        return this.baseRecipe;
    }

    public void setBaseRecipe(String _baseRecipe) {
        this.baseRecipe = _baseRecipe;
    }

    public int getHungerBonus() {
        return this.hungerBonus;
    }

    public void setHungerBonus(int _hungerBonus) {
        this.hungerBonus = _hungerBonus;
    }

    public int getHapinessBonus() {
        return this.hapinessBonus;
    }

    public void setHapinessBonus(int _hapinessBonus) {
        this.hapinessBonus = _hapinessBonus;
    }

    public ArrayList<String> getItems() {
        return this.items;
    }

    public int getBoredomBonus() {
        return this.boredomBonus;
    }

    public void setBoredomBonus(int _boredomBonus) {
        this.boredomBonus = _boredomBonus;
    }
}
