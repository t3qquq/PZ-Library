// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import zombie.characters.IsoPlayer;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemPickerJava;
import zombie.iso.ContainerOverlays;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.TileOverlays;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.network.GameServer;

public final class LoadGridsquarePerformanceWorkaround {
    public static void init(int var0, int var1) {
        if (!GameClient.bClient) {
            LoadGridsquarePerformanceWorkaround.ItemPicker.instance.init();
        }
    }

    public static void LoadGridsquare(IsoGridSquare square) {
        if (LoadGridsquarePerformanceWorkaround.ItemPicker.instance.begin(square)) {
            IsoObject[] objects = square.getObjects().getElements();
            int int0 = square.getObjects().size();

            for (int int1 = 0; int1 < int0; int1++) {
                IsoObject object = objects[int1];
                if (!(object instanceof IsoWorldInventoryObject)) {
                    if (!GameClient.bClient) {
                        LoadGridsquarePerformanceWorkaround.ItemPicker.instance.checkObject(object);
                    }

                    if (object.sprite != null && object.sprite.name != null && !ContainerOverlays.instance.hasOverlays(object)) {
                        TileOverlays.instance.updateTileOverlaySprite(object);
                    }
                }
            }
        }

        LoadGridsquarePerformanceWorkaround.ItemPicker.instance.end(square);
    }

    private static class ItemPicker {
        public static final LoadGridsquarePerformanceWorkaround.ItemPicker instance = new LoadGridsquarePerformanceWorkaround.ItemPicker();
        private IsoGridSquare square;

        public void init() {
        }

        public boolean begin(IsoGridSquare squarex) {
            if (squarex.isOverlayDone()) {
                this.square = null;
                return false;
            } else {
                this.square = squarex;
                return true;
            }
        }

        public void checkObject(IsoObject object) {
            IsoSprite sprite = object.getSprite();
            if (sprite != null && sprite.getName() != null) {
                ItemContainer container = object.getContainer();
                if (container != null && !container.isExplored()) {
                    ItemPickerJava.fillContainer(container, IsoPlayer.getInstance());
                    container.setExplored(true);
                    if (GameServer.bServer) {
                        GameServer.sendItemsInContainer(object, container);
                    }
                }

                if (container == null || !container.isEmpty()) {
                    ItemPickerJava.updateOverlaySprite(object);
                }
            }
        }

        public void end(IsoGridSquare squarex) {
            squarex.setOverlayDone(true);
        }
    }
}
