// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.erosion.obj;

import java.util.ArrayList;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.iso.IsoDirections;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameServer;

public final class ErosionObjSprites {
    public static final int SECTION_BASE = 0;
    public static final int SECTION_SNOW = 1;
    public static final int SECTION_FLOWER = 2;
    public static final int SECTION_CHILD = 3;
    public static final int NUM_SECTIONS = 4;
    public String name;
    public int stages;
    public boolean hasSnow;
    public boolean hasFlower;
    public boolean hasChildSprite;
    public boolean noSeasonBase;
    public int cycleTime = 1;
    private ErosionObjSprites.Stage[] sprites;

    public ErosionObjSprites(int int0, String string, boolean boolean0, boolean boolean1, boolean boolean2) {
        this.name = string;
        this.stages = int0;
        this.hasSnow = boolean0;
        this.hasFlower = boolean1;
        this.hasChildSprite = boolean2;
        this.sprites = new ErosionObjSprites.Stage[int0];

        for (int int1 = 0; int1 < int0; int1++) {
            this.sprites[int1] = new ErosionObjSprites.Stage();
            this.sprites[int1].sections[0] = new ErosionObjSprites.Section();
            if (this.hasSnow) {
                this.sprites[int1].sections[1] = new ErosionObjSprites.Section();
            }

            if (this.hasFlower) {
                this.sprites[int1].sections[2] = new ErosionObjSprites.Section();
            }

            if (this.hasChildSprite) {
                this.sprites[int1].sections[3] = new ErosionObjSprites.Section();
            }
        }
    }

    private String getSprite(int int2, int int1, int int0) {
        return this.sprites[int2] != null && this.sprites[int2].sections[int1] != null && this.sprites[int2].sections[int1].seasons[int0] != null
            ? this.sprites[int2].sections[int1].seasons[int0].getNext()
            : null;
    }

    public String getBase(int int0, int int1) {
        return this.getSprite(int0, 0, int1);
    }

    public String getFlower(int int0) {
        return this.hasFlower ? this.getSprite(int0, 2, 0) : null;
    }

    public String getChildSprite(int int0, int int1) {
        return this.hasChildSprite ? this.getSprite(int0, 3, int1) : null;
    }

    private void setSprite(int int1, int int0, String string, int int2) {
        if (this.sprites[int1] != null && this.sprites[int1].sections[int0] != null) {
            this.sprites[int1].sections[int0].seasons[int2] = new ErosionObjSprites.Sprites(string);
        }
    }

    private void setSprite(int int1, int int0, ArrayList<String> arrayList, int int2) {
        assert !arrayList.isEmpty();

        if (this.sprites[int1] != null && this.sprites[int1].sections[int0] != null) {
            this.sprites[int1].sections[int0].seasons[int2] = new ErosionObjSprites.Sprites(arrayList);
        }
    }

    public void setBase(int int0, String string, int int1) {
        this.setSprite(int0, 0, string, int1);
    }

    public void setBase(int int0, ArrayList<String> arrayList, int int1) {
        this.setSprite(int0, 0, arrayList, int1);
    }

    public void setFlower(int int0, String string) {
        this.setSprite(int0, 2, string, 0);
    }

    public void setFlower(int int0, ArrayList<String> arrayList) {
        this.setSprite(int0, 2, arrayList, 0);
    }

    public void setChildSprite(int int0, String string, int int1) {
        this.setSprite(int0, 3, string, int1);
    }

    public void setChildSprite(int int0, ArrayList<String> arrayList, int int1) {
        this.setSprite(int0, 3, arrayList, int1);
    }

    private static class Section {
        public ErosionObjSprites.Sprites[] seasons = new ErosionObjSprites.Sprites[6];
    }

    private static final class Sprites {
        public final ArrayList<String> sprites = new ArrayList<>();
        private int index = -1;

        public Sprites(String string) {
            if (Core.bDebug || GameServer.bServer && GameServer.bDebug) {
                IsoSprite sprite = IsoSpriteManager.instance.getSprite(string);
                if (sprite.CurrentAnim.Frames.size() == 0
                    || !GameServer.bServer && sprite.CurrentAnim.Frames.get(0).getTexture(IsoDirections.N) == null
                    || sprite.ID < 10000) {
                    DebugLog.log("EMPTY SPRITE " + string);
                }
            }

            this.sprites.add(string);
        }

        public Sprites(ArrayList<String> arrayList) {
            if (Core.bDebug || GameServer.bServer && GameServer.bDebug) {
                for (int int0 = 0; int0 < arrayList.size(); int0++) {
                    IsoSprite sprite = IsoSpriteManager.instance.getSprite((String)arrayList.get(int0));
                    if (sprite.CurrentAnim.Frames.size() == 0
                        || !GameServer.bServer && sprite.CurrentAnim.Frames.get(0).getTexture(IsoDirections.N) == null
                        || sprite.ID < 10000) {
                        DebugLog.log("EMPTY SPRITE " + (String)arrayList.get(int0));
                    }
                }
            }

            this.sprites.addAll(arrayList);
        }

        public String getNext() {
            if (++this.index >= this.sprites.size()) {
                this.index = 0;
            }

            return this.sprites.get(this.index);
        }
    }

    private static class Stage {
        public ErosionObjSprites.Section[] sections = new ErosionObjSprites.Section[4];
    }
}
