// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.visual;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.core.skinnedmodel.population.ClothingItem;

public final class ItemVisuals extends ArrayList<ItemVisual> {
    public void save(ByteBuffer output) throws IOException {
        output.putShort((short)this.size());

        for (int int0 = 0; int0 < this.size(); int0++) {
            this.get(int0).save(output);
        }
    }

    public void load(ByteBuffer input, int WorldVersion) throws IOException {
        this.clear();
        short short0 = input.getShort();

        for (int int0 = 0; int0 < short0; int0++) {
            ItemVisual itemVisual = new ItemVisual();
            itemVisual.load(input, WorldVersion);
            this.add(itemVisual);
        }
    }

    public ItemVisual findHat() {
        for (int int0 = 0; int0 < this.size(); int0++) {
            ItemVisual itemVisual = this.get(int0);
            ClothingItem clothingItem = itemVisual.getClothingItem();
            if (clothingItem != null && clothingItem.isHat()) {
                return itemVisual;
            }
        }

        return null;
    }

    public ItemVisual findMask() {
        for (int int0 = 0; int0 < this.size(); int0++) {
            ItemVisual itemVisual = this.get(int0);
            ClothingItem clothingItem = itemVisual.getClothingItem();
            if (clothingItem != null && clothingItem.isMask()) {
                return itemVisual;
            }
        }

        return null;
    }
}
