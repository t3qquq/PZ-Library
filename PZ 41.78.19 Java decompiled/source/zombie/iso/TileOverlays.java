// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import gnu.trove.map.hash.THashMap;
import java.util.ArrayList;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.core.Core;
import zombie.core.math.PZMath;
import zombie.core.textures.Texture;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameServer;
import zombie.util.LocationRNG;
import zombie.util.StringUtils;

public class TileOverlays {
    public static final TileOverlays instance = new TileOverlays();
    private static final THashMap<String, TileOverlays.TileOverlay> overlayMap = new THashMap<>();
    private static final ArrayList<TileOverlays.TileOverlayEntry> tempEntries = new ArrayList<>();

    public void addOverlays(KahluaTableImpl _overlayMap) {
        KahluaTableIterator kahluaTableIterator0 = _overlayMap.iterator();

        while (kahluaTableIterator0.advance()) {
            String string0 = kahluaTableIterator0.getKey().toString();
            if (!"VERSION".equalsIgnoreCase(string0)) {
                TileOverlays.TileOverlay tileOverlay = new TileOverlays.TileOverlay();
                tileOverlay.tile = string0;
                KahluaTableImpl kahluaTableImpl0 = (KahluaTableImpl)kahluaTableIterator0.getValue();
                KahluaTableIterator kahluaTableIterator1 = kahluaTableImpl0.iterator();

                while (kahluaTableIterator1.advance()) {
                    KahluaTableImpl kahluaTableImpl1 = (KahluaTableImpl)kahluaTableIterator1.getValue();
                    TileOverlays.TileOverlayEntry tileOverlayEntry = new TileOverlays.TileOverlayEntry();
                    tileOverlayEntry.room = kahluaTableImpl1.rawgetStr("name");
                    tileOverlayEntry.chance = kahluaTableImpl1.rawgetInt("chance");
                    tileOverlayEntry.usage.parse(kahluaTableImpl1.rawgetStr("usage"));
                    KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)kahluaTableImpl1.rawget("tiles");
                    KahluaTableIterator kahluaTableIterator2 = kahluaTableImpl2.iterator();

                    while (kahluaTableIterator2.advance()) {
                        String string1 = kahluaTableIterator2.getValue().toString();
                        if (StringUtils.isNullOrWhitespace(string1) || "none".equalsIgnoreCase(string1)) {
                            string1 = "";
                        } else if (Core.bDebug && !GameServer.bServer && Texture.getSharedTexture(string1) == null) {
                            System.out.println("BLANK OVERLAY TEXTURE. Set it to \"none\".: " + string1);
                        }

                        tileOverlayEntry.tiles.add(string1);
                    }

                    tileOverlay.entries.add(tileOverlayEntry);
                }

                overlayMap.put(tileOverlay.tile, tileOverlay);
            }
        }
    }

    public boolean hasOverlays(IsoObject obj) {
        return obj != null && obj.sprite != null && obj.sprite.name != null && overlayMap.containsKey(obj.sprite.name);
    }

    public void updateTileOverlaySprite(IsoObject obj) {
        if (obj != null) {
            IsoGridSquare square = obj.getSquare();
            if (square != null) {
                String string0 = null;
                float float0 = -1.0F;
                float float1 = -1.0F;
                float float2 = -1.0F;
                float float3 = -1.0F;
                if (obj.sprite != null && obj.sprite.name != null) {
                    TileOverlays.TileOverlay tileOverlay = overlayMap.get(obj.sprite.name);
                    if (tileOverlay != null) {
                        String string1 = "other";
                        if (square.getRoom() != null) {
                            string1 = square.getRoom().getName();
                        }

                        TileOverlays.TileOverlayEntry tileOverlayEntry = tileOverlay.pickRandom(string1, square);
                        if (tileOverlayEntry == null) {
                            tileOverlayEntry = tileOverlay.pickRandom("other", square);
                        }

                        if (tileOverlayEntry != null) {
                            if (tileOverlayEntry.usage.bTableTop && this.hasObjectOnTop(obj)) {
                                return;
                            }

                            string0 = tileOverlayEntry.pickRandom(square.x, square.y, square.z);
                            if (tileOverlayEntry.usage.alpha >= 0.0F) {
                                float2 = 1.0F;
                                float1 = 1.0F;
                                float0 = 1.0F;
                                float3 = tileOverlayEntry.usage.alpha;
                            }
                        }
                    }
                }

                if (!StringUtils.isNullOrWhitespace(string0) && !GameServer.bServer && Texture.getSharedTexture(string0) == null) {
                    string0 = null;
                }

                if (!StringUtils.isNullOrWhitespace(string0)) {
                    if (obj.AttachedAnimSprite == null) {
                        obj.AttachedAnimSprite = new ArrayList<>(4);
                    }

                    IsoSprite sprite = IsoSpriteManager.instance.getSprite(string0);
                    sprite.name = string0;
                    IsoSpriteInstance spriteInstance = IsoSpriteInstance.get(sprite);
                    if (float3 > 0.0F) {
                        spriteInstance.tintr = float0;
                        spriteInstance.tintg = float1;
                        spriteInstance.tintb = float2;
                        spriteInstance.alpha = float3;
                    }

                    spriteInstance.bCopyTargetAlpha = false;
                    spriteInstance.bMultiplyObjectAlpha = true;
                    obj.AttachedAnimSprite.add(spriteInstance);
                }
            }
        }
    }

    private boolean hasObjectOnTop(IsoObject object0) {
        if (!object0.isTableSurface()) {
            return false;
        } else {
            IsoGridSquare square = object0.getSquare();

            for (int int0 = object0.getObjectIndex() + 1; int0 < square.getObjects().size(); int0++) {
                IsoObject object1 = square.getObjects().get(int0);
                if (object1.isTableTopObject() || object1.isTableSurface()) {
                    return true;
                }
            }

            return false;
        }
    }

    public void fixTableTopOverlays(IsoGridSquare square) {
        if (square != null && !square.getObjects().isEmpty()) {
            boolean boolean0 = false;

            for (int int0 = square.getObjects().size() - 1; int0 >= 0; int0--) {
                IsoObject object = square.getObjects().get(int0);
                if (boolean0 && object.isTableSurface()) {
                    this.removeTableTopOverlays(object);
                }

                if (object.isTableSurface() || object.isTableTopObject()) {
                    boolean0 = true;
                }
            }
        }
    }

    private void removeTableTopOverlays(IsoObject object) {
        if (object != null && object.isTableSurface()) {
            if (object.sprite != null && object.sprite.name != null) {
                if (object.AttachedAnimSprite != null && !object.AttachedAnimSprite.isEmpty()) {
                    TileOverlays.TileOverlay tileOverlay = overlayMap.get(object.sprite.name);
                    if (tileOverlay != null) {
                        int int0 = object.AttachedAnimSprite.size();

                        for (int int1 = 0; int1 < tileOverlay.entries.size(); int1++) {
                            TileOverlays.TileOverlayEntry tileOverlayEntry = tileOverlay.entries.get(int1);
                            if (tileOverlayEntry.usage.bTableTop) {
                                for (int int2 = 0; int2 < tileOverlayEntry.tiles.size(); int2++) {
                                    this.tryRemoveAttachedSprite(object.AttachedAnimSprite, tileOverlayEntry.tiles.get(int2));
                                }
                            }
                        }

                        if (int0 != object.AttachedAnimSprite.size()) {
                        }
                    }
                }
            }
        }
    }

    private void tryRemoveAttachedSprite(ArrayList<IsoSpriteInstance> arrayList, String string) {
        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            IsoSpriteInstance spriteInstance = (IsoSpriteInstance)arrayList.get(int0);
            if (string.equals(spriteInstance.getName())) {
                arrayList.remove(int0--);
                IsoSpriteInstance.add(spriteInstance);
            }
        }
    }

    public void Reset() {
        overlayMap.clear();
    }

    private static final class TileOverlay {
        public String tile;
        public final ArrayList<TileOverlays.TileOverlayEntry> entries = new ArrayList<>();

        public void getEntries(String string, IsoGridSquare square, ArrayList<TileOverlays.TileOverlayEntry> arrayList) {
            arrayList.clear();

            for (int int0 = 0; int0 < this.entries.size(); int0++) {
                TileOverlays.TileOverlayEntry tileOverlayEntry = this.entries.get(int0);
                if (tileOverlayEntry.room.equalsIgnoreCase(string) && tileOverlayEntry.matchUsage(square)) {
                    arrayList.add(tileOverlayEntry);
                }
            }
        }

        public TileOverlays.TileOverlayEntry pickRandom(String string, IsoGridSquare square) {
            this.getEntries(string, square, TileOverlays.tempEntries);
            if (TileOverlays.tempEntries.isEmpty()) {
                return null;
            } else {
                int int0 = LocationRNG.instance.nextInt(TileOverlays.tempEntries.size(), square.x, square.y, square.z);
                return TileOverlays.tempEntries.get(int0);
            }
        }
    }

    private static final class TileOverlayEntry {
        public String room;
        public int chance;
        public final ArrayList<String> tiles = new ArrayList<>();
        public final TileOverlays.TileOverlayUsage usage = new TileOverlays.TileOverlayUsage();

        public boolean matchUsage(IsoGridSquare square) {
            return this.usage.match(square);
        }

        public String pickRandom(int int1, int int2, int int3) {
            int int0 = LocationRNG.instance.nextInt(this.chance, int1, int2, int3);
            if (int0 == 0 && !this.tiles.isEmpty()) {
                int0 = LocationRNG.instance.nextInt(this.tiles.size());
                return this.tiles.get(int0);
            } else {
                return null;
            }
        }
    }

    private static final class TileOverlayUsage {
        String usage;
        int zOnly = -1;
        int zGreaterThan = -1;
        float alpha = -1.0F;
        boolean bTableTop = false;

        boolean parse(String string0) {
            this.usage = string0.trim();
            if (StringUtils.isNullOrWhitespace(this.usage)) {
                return true;
            } else {
                String[] strings = string0.split(";");

                for (int int0 = 0; int0 < strings.length; int0++) {
                    String string1 = strings[int0];
                    if (string1.startsWith("z=")) {
                        this.zOnly = Integer.parseInt(string1.substring(2));
                    } else if (string1.startsWith("z>")) {
                        this.zGreaterThan = Integer.parseInt(string1.substring(2));
                    } else if (string1.startsWith("alpha=")) {
                        this.alpha = Float.parseFloat(string1.substring(6));
                        this.alpha = PZMath.clamp(this.alpha, 0.0F, 1.0F);
                    } else {
                        if (!string1.startsWith("tabletop")) {
                            return false;
                        }

                        this.bTableTop = true;
                    }
                }

                return true;
            }
        }

        boolean match(IsoGridSquare square) {
            return this.zOnly != -1 && square.z != this.zOnly ? false : this.zGreaterThan == -1 || square.z > this.zGreaterThan;
        }
    }
}
