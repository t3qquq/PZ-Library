// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory;

import java.util.ArrayList;
import zombie.audio.BaseSoundEmitter;
import zombie.iso.IsoWorld;

public final class ItemSoundManager {
    private static final ArrayList<InventoryItem> items = new ArrayList<>();
    private static final ArrayList<BaseSoundEmitter> emitters = new ArrayList<>();
    private static final ArrayList<InventoryItem> toAdd = new ArrayList<>();
    private static final ArrayList<InventoryItem> toRemove = new ArrayList<>();
    private static final ArrayList<InventoryItem> toStopItems = new ArrayList<>();
    private static final ArrayList<BaseSoundEmitter> toStopEmitters = new ArrayList<>();

    public static void addItem(InventoryItem item) {
        if (item != null && !items.contains(item)) {
            toRemove.remove(item);
            int int0 = toStopItems.indexOf(item);
            if (int0 != -1) {
                toStopItems.remove(int0);
                BaseSoundEmitter baseSoundEmitter = toStopEmitters.remove(int0);
                items.add(item);
                emitters.add(baseSoundEmitter);
            } else if (!toAdd.contains(item)) {
                toAdd.add(item);
            }
        }
    }

    public static void removeItem(InventoryItem item) {
        toAdd.remove(item);
        int int0 = items.indexOf(item);
        if (item != null && int0 != -1) {
            if (!toRemove.contains(item)) {
                toRemove.add(item);
            }
        }
    }

    public static void removeItems(ArrayList<InventoryItem> arrayList) {
        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            removeItem((InventoryItem)arrayList.get(int0));
        }
    }

    public static void update() {
        if (!toStopItems.isEmpty()) {
            for (int int0 = 0; int0 < toStopItems.size(); int0++) {
                BaseSoundEmitter baseSoundEmitter0 = toStopEmitters.get(int0);
                baseSoundEmitter0.stopAll();
                IsoWorld.instance.returnOwnershipOfEmitter(baseSoundEmitter0);
            }

            toStopItems.clear();
            toStopEmitters.clear();
        }

        if (!toAdd.isEmpty()) {
            for (int int1 = 0; int1 < toAdd.size(); int1++) {
                InventoryItem item0 = toAdd.get(int1);

                assert !items.contains(item0);

                items.add(item0);
                BaseSoundEmitter baseSoundEmitter1 = IsoWorld.instance.getFreeEmitter();
                IsoWorld.instance.takeOwnershipOfEmitter(baseSoundEmitter1);
                emitters.add(baseSoundEmitter1);
            }

            toAdd.clear();
        }

        if (!toRemove.isEmpty()) {
            for (int int2 = 0; int2 < toRemove.size(); int2++) {
                InventoryItem item1 = toRemove.get(int2);

                assert items.contains(item1);

                int int3 = items.indexOf(item1);
                items.remove(int3);
                BaseSoundEmitter baseSoundEmitter2 = emitters.get(int3);
                emitters.remove(int3);
                toStopItems.add(item1);
                toStopEmitters.add(baseSoundEmitter2);
            }

            toRemove.clear();
        }

        for (int int4 = 0; int4 < items.size(); int4++) {
            InventoryItem item2 = items.get(int4);
            BaseSoundEmitter baseSoundEmitter3 = emitters.get(int4);
            ItemContainer container = item2.getOutermostContainer();
            if (container != null) {
                if (container.containingItem != null && container.containingItem.getWorldItem() != null) {
                    if (container.containingItem.getWorldItem().getWorldObjectIndex() == -1) {
                        container = null;
                    }
                } else if (container.parent != null) {
                    if (container.parent.getObjectIndex() == -1
                        && container.parent.getMovingObjectIndex() == -1
                        && container.parent.getStaticMovingObjectIndex() == -1) {
                        container = null;
                    }
                } else {
                    container = null;
                }
            }

            if (container != null || item2.getWorldItem() != null && item2.getWorldItem().getWorldObjectIndex() != -1) {
                item2.updateSound(baseSoundEmitter3);
                baseSoundEmitter3.tick();
            } else {
                removeItem(item2);
            }
        }
    }

    public static void Reset() {
        items.clear();
        emitters.clear();
        toAdd.clear();
        toRemove.clear();
        toStopItems.clear();
        toStopEmitters.clear();
    }
}
