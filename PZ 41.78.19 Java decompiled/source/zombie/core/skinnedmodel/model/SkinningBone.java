// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import java.util.function.Consumer;

public final class SkinningBone {
    public SkinningBone Parent;
    public String Name;
    public int Index;
    public SkinningBone[] Children;

    public void forEachDescendant(Consumer<SkinningBone> consumer) {
        forEachDescendant(this, consumer);
    }

    private static void forEachDescendant(SkinningBone skinningBone0, Consumer<SkinningBone> consumer) {
        if (skinningBone0.Children != null && skinningBone0.Children.length != 0) {
            for (SkinningBone skinningBone1 : skinningBone0.Children) {
                consumer.accept(skinningBone1);
            }

            for (SkinningBone skinningBone2 : skinningBone0.Children) {
                forEachDescendant(skinningBone2, consumer);
            }
        }
    }

    @Override
    public String toString() {
        String string = System.lineSeparator();
        return this.getClass().getName() + string + "{" + string + "\tName:\"" + this.Name + "\"" + string + "\tIndex:" + this.Index + string + "}";
    }
}
