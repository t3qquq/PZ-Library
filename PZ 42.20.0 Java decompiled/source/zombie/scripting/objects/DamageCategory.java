// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.scripting.objects;

public enum DamageCategory {
    SLASH("Slash");

    private final String id;

    DamageCategory(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
