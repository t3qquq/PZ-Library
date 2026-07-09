// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import gnu.trove.map.hash.THashMap;
import java.util.ArrayList;
import java.util.Map.Entry;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.core.textures.Texture;
import zombie.inventory.ItemContainer;
import zombie.iso.objects.IsoStove;
import zombie.network.GameServer;
import zombie.util.LocationRNG;
import zombie.util.StringUtils;

public class ContainerOverlays {
    public static final ContainerOverlays instance = new ContainerOverlays();
    private static final ArrayList<ContainerOverlays.ContainerOverlayEntry> tempEntries = new ArrayList<>();
    private final THashMap<String, ContainerOverlays.ContainerOverlay> overlayMap = new THashMap<>();

    private void parseContainerOverlayMapV0(KahluaTableImpl kahluaTableImpl0) {
        for (Entry entry0 : kahluaTableImpl0.delegate.entrySet()) {
            String string0 = entry0.getKey().toString();
            ContainerOverlays.ContainerOverlay containerOverlay = new ContainerOverlays.ContainerOverlay();
            containerOverlay.name = string0;
            this.overlayMap.put(containerOverlay.name, containerOverlay);
            KahluaTableImpl kahluaTableImpl1 = (KahluaTableImpl)entry0.getValue();

            for (Entry entry1 : kahluaTableImpl1.delegate.entrySet()) {
                String string1 = entry1.getKey().toString();
                KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)entry1.getValue();
                String string2 = null;
                if (kahluaTableImpl2.delegate.containsKey(1.0)) {
                    string2 = kahluaTableImpl2.rawget(1.0).toString();
                }

                String string3 = null;
                if (kahluaTableImpl2.delegate.containsKey(2.0)) {
                    string3 = kahluaTableImpl2.rawget(2.0).toString();
                }

                ContainerOverlays.ContainerOverlayEntry containerOverlayEntry = new ContainerOverlays.ContainerOverlayEntry();
                containerOverlayEntry.manyItems = string2;
                containerOverlayEntry.fewItems = string3;
                containerOverlayEntry.room = string1;
                containerOverlay.entries.add(containerOverlayEntry);
            }
        }
    }

    private void parseContainerOverlayMapV1(KahluaTableImpl kahluaTableImpl0) {
        KahluaTableIterator kahluaTableIterator0 = kahluaTableImpl0.iterator();

        while (kahluaTableIterator0.advance()) {
            String string0 = kahluaTableIterator0.getKey().toString();
            if (!"VERSION".equalsIgnoreCase(string0)) {
                ContainerOverlays.ContainerOverlay containerOverlay = new ContainerOverlays.ContainerOverlay();
                containerOverlay.name = string0;
                KahluaTableImpl kahluaTableImpl1 = (KahluaTableImpl)kahluaTableIterator0.getValue();
                KahluaTableIterator kahluaTableIterator1 = kahluaTableImpl1.iterator();

                while (kahluaTableIterator1.advance()) {
                    KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)kahluaTableIterator1.getValue();
                    String string1 = kahluaTableImpl2.rawgetStr("name");
                    KahluaTableImpl kahluaTableImpl3 = (KahluaTableImpl)kahluaTableImpl2.rawget("tiles");
                    ContainerOverlays.ContainerOverlayEntry containerOverlayEntry = new ContainerOverlays.ContainerOverlayEntry();
                    containerOverlayEntry.manyItems = (String)kahluaTableImpl3.rawget(1);
                    containerOverlayEntry.fewItems = (String)kahluaTableImpl3.rawget(2);
                    if (StringUtils.isNullOrWhitespace(containerOverlayEntry.manyItems) || "none".equalsIgnoreCase(containerOverlayEntry.manyItems)) {
                        containerOverlayEntry.manyItems = null;
                    }

                    if (StringUtils.isNullOrWhitespace(containerOverlayEntry.fewItems) || "none".equalsIgnoreCase(containerOverlayEntry.fewItems)) {
                        containerOverlayEntry.fewItems = null;
                    }

                    containerOverlayEntry.room = string1;
                    containerOverlay.entries.add(containerOverlayEntry);
                }

                this.overlayMap.put(containerOverlay.name, containerOverlay);
            }
        }
    }

    public void addOverlays(KahluaTableImpl _overlayMap) {
        int int0 = _overlayMap.rawgetInt("VERSION");
        if (int0 == -1) {
            this.parseContainerOverlayMapV0(_overlayMap);
        } else {
            if (int0 != 1) {
                throw new RuntimeException("unknown overlayMap.VERSION " + int0);
            }

            this.parseContainerOverlayMapV1(_overlayMap);
        }
    }

    public boolean hasOverlays(IsoObject obj) {
        return obj != null && obj.sprite != null && obj.sprite.name != null && this.overlayMap.containsKey(obj.sprite.name);
    }

    public void updateContainerOverlaySprite(IsoObject obj) {
        if (obj != null) {
            if (!(obj instanceof IsoStove)) {
                IsoGridSquare square = obj.getSquare();
                if (square != null) {
                    String string0 = null;
                    ItemContainer container = obj.getContainer();
                    if (obj.sprite != null && obj.sprite.name != null && container != null && container.getItems() != null && !container.isEmpty()) {
                        ContainerOverlays.ContainerOverlay containerOverlay = this.overlayMap.get(obj.sprite.name);
                        if (containerOverlay != null) {
                            String string1 = "other";
                            if (square.getRoom() != null) {
                                string1 = square.getRoom().getName();
                            }

                            ContainerOverlays.ContainerOverlayEntry containerOverlayEntry = containerOverlay.pickRandom(string1, square.x, square.y, square.z);
                            if (containerOverlayEntry == null) {
                                containerOverlayEntry = containerOverlay.pickRandom("other", square.x, square.y, square.z);
                            }

                            if (containerOverlayEntry != null) {
                                string0 = containerOverlayEntry.manyItems;
                                if (containerOverlayEntry.fewItems != null && container.getItems().size() < 7) {
                                    string0 = containerOverlayEntry.fewItems;
                                }
                            }
                        }
                    }

                    if (!StringUtils.isNullOrWhitespace(string0) && !GameServer.bServer && Texture.getSharedTexture(string0) == null) {
                        string0 = null;
                    }

                    obj.setOverlaySprite(string0);
                }
            }
        }
    }

    public void Reset() {
        this.overlayMap.clear();
    }

    private static final class ContainerOverlay {
        public String name;
        public final ArrayList<ContainerOverlays.ContainerOverlayEntry> entries = new ArrayList<>();

        public void getEntries(String string, ArrayList<ContainerOverlays.ContainerOverlayEntry> arrayList) {
            arrayList.clear();

            for (int int0 = 0; int0 < this.entries.size(); int0++) {
                ContainerOverlays.ContainerOverlayEntry containerOverlayEntry = this.entries.get(int0);
                if (containerOverlayEntry.room.equalsIgnoreCase(string)) {
                    arrayList.add(containerOverlayEntry);
                }
            }
        }

        public ContainerOverlays.ContainerOverlayEntry pickRandom(String string, int int1, int int2, int int3) {
            this.getEntries(string, ContainerOverlays.tempEntries);
            if (ContainerOverlays.tempEntries.isEmpty()) {
                return null;
            } else {
                int int0 = LocationRNG.instance.nextInt(ContainerOverlays.tempEntries.size(), int1, int2, int3);
                return ContainerOverlays.tempEntries.get(int0);
            }
        }
    }

    private static final class ContainerOverlayEntry {
        public String room;
        public String manyItems;
        public String fewItems;
    }
}
