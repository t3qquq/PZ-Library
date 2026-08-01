// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.scripting.objects;

public enum WeaponSubCategory {
    FIREARM("Firearm"),
    SPEAR("Spear"),
    STAB("Stab"),
    SWINGING("Swinging");

    private final String id;

    WeaponSubCategory(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
