// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.erosion.obj;

import java.util.ArrayList;
import zombie.iso.IsoObject;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;

public final class ErosionObjOverlay {
    private final ErosionObjOverlaySprites sprites;
    public String name;
    public int stages;
    public boolean applyAlpha;
    public int cycleTime;

    public ErosionObjOverlay(ErosionObjOverlaySprites erosionObjOverlaySprites, int int0, boolean boolean0) {
        this.sprites = erosionObjOverlaySprites;
        this.name = erosionObjOverlaySprites.name;
        this.stages = erosionObjOverlaySprites.stages;
        this.applyAlpha = boolean0;
        this.cycleTime = int0;
    }

    public int setOverlay(IsoObject object, int int1, int int0, int int2, float var5) {
        if (int0 >= 0 && int0 < this.stages && object != null) {
            if (int1 >= 0) {
                this.removeOverlay(object, int1);
            }

            IsoSprite sprite = this.sprites.getSprite(int0, int2);
            IsoSpriteInstance spriteInstance = sprite.newInstance();
            if (object.AttachedAnimSprite == null) {
                object.AttachedAnimSprite = new ArrayList<>();
            }

            object.AttachedAnimSprite.add(spriteInstance);
            return spriteInstance.getID();
        } else {
            return -1;
        }
    }

    public boolean removeOverlay(IsoObject object, int int1) {
        if (object == null) {
            return false;
        } else {
            ArrayList arrayList = object.AttachedAnimSprite;
            if (arrayList != null && !arrayList.isEmpty()) {
                for (int int0 = 0; int0 < object.AttachedAnimSprite.size(); int0++) {
                    if (object.AttachedAnimSprite.get(int0).parentSprite.ID == int1) {
                        object.AttachedAnimSprite.remove(int0--);
                    }
                }

                for (int int2 = arrayList.size() - 1; int2 >= 0; int2--) {
                    if (((IsoSpriteInstance)arrayList.get(int2)).getID() == int1) {
                        arrayList.remove(int2);
                        return true;
                    }
                }

                return false;
            } else {
                return false;
            }
        }
    }
}
