// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.characters;

public enum InjurySeverity {
    LOW(0.5F),
    NORMAL(1.0F),
    HIGH(1.5F);

    public final float multiplier;

    InjurySeverity(final float multiplier) {
        this.multiplier = multiplier;
    }
}
