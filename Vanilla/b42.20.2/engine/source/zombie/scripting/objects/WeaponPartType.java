// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.scripting.objects;

public enum WeaponPartType {
    CANON("Canon"),
    CLIP("Clip"),
    RECOIL_PAD("RecoilPad"),
    SCOPE("Scope"),
    SLING("Sling"),
    STOCK("Stock");

    private final String id;

    WeaponPartType(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
