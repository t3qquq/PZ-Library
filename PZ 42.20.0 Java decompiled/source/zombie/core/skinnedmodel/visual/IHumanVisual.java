// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.core.skinnedmodel.visual;

public interface IHumanVisual {
    HumanVisual getHumanVisual();

    void getItemVisuals(ItemVisuals itemVisuals);

    boolean isFemale();

    boolean isZombie();

    boolean isSkeleton();
}
