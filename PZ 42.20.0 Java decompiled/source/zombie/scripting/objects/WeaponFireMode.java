// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.scripting.objects;

public enum WeaponFireMode {
    AUTO("Auto"),
    SINGLE("Single");

    private final String id;

    WeaponFireMode(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
