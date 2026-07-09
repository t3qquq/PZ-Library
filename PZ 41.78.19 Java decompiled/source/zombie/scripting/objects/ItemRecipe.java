// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.scripting.objects;

public final class ItemRecipe {
    public String name;
    public Integer use = -1;
    public Boolean cooked = false;
    private String module = null;

    public Integer getUse() {
        return this.use;
    }

    public ItemRecipe(String _name, String _module, Integer _use) {
        this.name = _name;
        this.use = _use;
        this.setModule(_module);
    }

    public String getName() {
        return this.name;
    }

    public String getModule() {
        return this.module;
    }

    public void setModule(String _module) {
        this.module = _module;
    }

    public String getFullType() {
        return this.module + "." + this.name;
    }
}
