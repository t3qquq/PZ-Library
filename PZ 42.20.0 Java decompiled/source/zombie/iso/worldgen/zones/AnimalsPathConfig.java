// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.iso.worldgen.zones;

public record AnimalsPathConfig(String animalType, int count, float chance, int[] points, int[] radius, int[] extension, float extensionChance) {
    public int getNameHash() {
        return this.animalType.hashCode();
    }
}
