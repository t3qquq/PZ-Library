// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.scripting.objects;

public enum AnimalFeedType {
    ANIMAL_FEED("AnimalFeed"),
    GRASS("Grass"),
    NUTS("Nuts"),
    SEEDS("Seeds");

    private final String id;

    AnimalFeedType(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
