// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.erosion.obj;

import java.util.ArrayList;
import zombie.erosion.ErosionMain;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoTree;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.util.list.PZArrayList;

public final class ErosionObj {
    private final ErosionObjSprites sprites;
    public String name;
    public int stages;
    public boolean hasSnow;
    public boolean hasFlower;
    public boolean hasChildSprite;
    public float bloomStart;
    public float bloomEnd;
    public boolean noSeasonBase;
    public int cycleTime = 1;

    public ErosionObj(ErosionObjSprites erosionObjSprites, int int0, float float0, float float1, boolean boolean0) {
        this.sprites = erosionObjSprites;
        this.name = erosionObjSprites.name;
        this.stages = erosionObjSprites.stages;
        this.hasSnow = erosionObjSprites.hasSnow;
        this.hasFlower = erosionObjSprites.hasFlower;
        this.hasChildSprite = erosionObjSprites.hasChildSprite;
        this.bloomStart = float0;
        this.bloomEnd = float1;
        this.noSeasonBase = boolean0;
        this.cycleTime = int0;
    }

    public IsoObject getObject(IsoGridSquare square, boolean boolean0) {
        PZArrayList pZArrayList = square.getObjects();

        for (int int0 = pZArrayList.size() - 1; int0 >= 0; int0--) {
            IsoObject object = (IsoObject)pZArrayList.get(int0);
            if (this.name.equals(object.getName())) {
                if (boolean0) {
                    pZArrayList.remove(int0);
                }

                object.doNotSync = true;
                return object;
            }
        }

        return null;
    }

    public IsoObject createObject(IsoGridSquare square, int int0, boolean boolean0, int int1) {
        String string = this.sprites.getBase(int0, this.noSeasonBase ? 0 : int1);
        if (string == null) {
            string = "";
        }

        Object object;
        if (boolean0) {
            object = IsoTree.getNew();
            ((IsoObject)object).sprite = IsoSpriteManager.instance.NamedMap.get(string);
            ((IsoObject)object).square = square;
            ((IsoObject)object).sx = 0.0F;
            ((IsoTree)object).initTree();
        } else {
            object = IsoObject.getNew(square, string, this.name, false);
        }

        ((IsoObject)object).setName(this.name);
        ((IsoObject)object).doNotSync = true;
        return (IsoObject)object;
    }

    public boolean placeObject(IsoGridSquare square, int int0, boolean boolean0, int int1, boolean boolean1) {
        IsoObject object = this.createObject(square, int0, boolean0, int1);
        if (object != null && this.setStageObject(int0, object, int1, boolean1)) {
            object.doNotSync = true;
            if (!boolean0) {
                square.getObjects().add(object);
                object.addToWorld();
            } else {
                square.AddTileObject(object);
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean setStageObject(int int0, IsoObject object, int int1, boolean boolean0) {
        object.doNotSync = true;
        if (int0 >= 0 && int0 < this.stages && object != null) {
            String string = this.sprites.getBase(int0, this.noSeasonBase ? 0 : int1);
            if (string == null) {
                object.setSprite(this.getSprite(""));
                if (object.AttachedAnimSprite != null) {
                    object.AttachedAnimSprite.clear();
                }

                return true;
            } else {
                IsoSprite sprite = this.getSprite(string);
                object.setSprite(sprite);
                if (this.hasChildSprite || this.hasFlower) {
                    if (object.AttachedAnimSprite == null) {
                        object.AttachedAnimSprite = new ArrayList<>();
                    }

                    object.AttachedAnimSprite.clear();
                    if (this.hasChildSprite && int1 != 0) {
                        string = this.sprites.getChildSprite(int0, int1);
                        if (string != null) {
                            sprite = this.getSprite(string);
                            object.AttachedAnimSprite.add(sprite.newInstance());
                        }
                    }

                    if (this.hasFlower && boolean0) {
                        string = this.sprites.getFlower(int0);
                        if (string != null) {
                            sprite = this.getSprite(string);
                            object.AttachedAnimSprite.add(sprite.newInstance());
                        }
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public boolean setStage(IsoGridSquare square, int int0, int int1, boolean boolean0) {
        IsoObject object = this.getObject(square, false);
        return object != null ? this.setStageObject(int0, object, int1, boolean0) : false;
    }

    public IsoObject removeObject(IsoGridSquare square) {
        return this.getObject(square, true);
    }

    private IsoSprite getSprite(String string) {
        return ErosionMain.getInstance().getSpriteManager().getSprite(string);
    }
}
