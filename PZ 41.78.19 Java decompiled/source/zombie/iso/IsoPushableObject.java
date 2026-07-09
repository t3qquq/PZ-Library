// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.core.Core;
import zombie.inventory.ItemContainer;
import zombie.iso.objects.IsoWheelieBin;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;

public class IsoPushableObject extends IsoMovingObject {
    public int carryCapacity = 100;
    public float emptyWeight = 4.5F;
    public ArrayList<IsoPushableObject> connectList = null;
    public float ox = 0.0F;
    public float oy = 0.0F;

    public IsoPushableObject(IsoCell cell) {
        super(cell);
        this.getCell().getPushableObjectList().add(this);
    }

    public IsoPushableObject(IsoCell cell, int x, int y, int z) {
        super(cell, true);
        this.getCell().getPushableObjectList().add(this);
    }

    public IsoPushableObject(IsoCell cell, IsoGridSquare square, IsoSprite spr) {
        super(cell, square, spr, true);
        this.setX(square.getX() + 0.5F);
        this.setY(square.getY() + 0.5F);
        this.setZ(square.getZ());
        this.ox = this.getX();
        this.oy = this.getY();
        this.setNx(this.getX());
        this.setNy(this.getNy());
        this.offsetX = 6 * Core.TileScale;
        this.offsetY = -30 * Core.TileScale;
        this.setWeight(6.0F);
        this.square = square;
        this.setCurrent(square);
        this.getCurrentSquare().getMovingObjects().add(this);
        this.Collidable = true;
        this.solid = true;
        this.shootable = false;
        this.width = 0.5F;
        this.setAlphaAndTarget(0.0F);
        this.getCell().getPushableObjectList().add(this);
    }

    @Override
    public String getObjectName() {
        return "Pushable";
    }

    @Override
    public void update() {
        if (this.connectList != null) {
            Iterator iterator = this.connectList.iterator();
            float float0 = 0.0F;

            while (iterator.hasNext()) {
                IsoPushableObject pushableObject1 = (IsoPushableObject)iterator.next();
                float float1 = pushableObject1.getAlpha();
                if (float1 > float0) {
                    float0 = float1;
                }
            }

            this.setAlphaAndTarget(float0);
        }

        super.update();
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        super.load(input, WorldVersion, IS_DEBUG_SAVE);
        if (!(this instanceof IsoWheelieBin)) {
            this.sprite = IsoSpriteManager.instance.getSprite(input.getInt());
        }

        if (input.get() == 1) {
            this.container = new ItemContainer();
            this.container.load(input, WorldVersion);
        }
    }

    @Override
    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        super.save(output, IS_DEBUG_SAVE);
        if (!(this instanceof IsoWheelieBin)) {
            output.putInt(this.sprite.ID);
        }

        if (this.container != null) {
            output.put((byte)1);
            this.container.save(output);
        } else {
            output.put((byte)0);
        }
    }

    @Override
    public float getWeight(float x, float y) {
        if (this.container == null) {
            return this.emptyWeight;
        } else {
            float float0 = this.container.getContentsWeight() / this.carryCapacity;
            if (float0 < 0.0F) {
                float0 = 0.0F;
            }

            return float0 > 1.0F ? this.getWeight() * 8.0F : this.getWeight() * float0 + this.emptyWeight;
        }
    }

    @Override
    public boolean Serialize() {
        return true;
    }

    @Override
    public void DoCollideNorS() {
        if (this.connectList == null) {
            super.DoCollideNorS();
        } else {
            for (IsoPushableObject pushableObject1 : this.connectList) {
                if (pushableObject1 != this) {
                    if (pushableObject1.ox < this.ox) {
                        pushableObject1.setNx(this.getNx() - 1.0F);
                        pushableObject1.setX(this.getX() - 1.0F);
                    } else if (pushableObject1.ox > this.ox) {
                        pushableObject1.setNx(this.getNx() + 1.0F);
                        pushableObject1.setX(this.getX() + 1.0F);
                    } else {
                        pushableObject1.setNx(this.getNx());
                        pushableObject1.setX(this.getX());
                    }

                    if (pushableObject1.oy < this.oy) {
                        pushableObject1.setNy(this.getNy() - 1.0F);
                        pushableObject1.setY(this.getY() - 1.0F);
                    } else if (pushableObject1.oy > this.oy) {
                        pushableObject1.setNy(this.getNy() + 1.0F);
                        pushableObject1.setY(this.getY() + 1.0F);
                    } else {
                        pushableObject1.setNy(this.getNy());
                        pushableObject1.setY(this.getY());
                    }

                    pushableObject1.setImpulsex(this.getImpulsex());
                    pushableObject1.setImpulsey(this.getImpulsey());
                }
            }
        }
    }

    @Override
    public void DoCollideWorE() {
        if (this.connectList == null) {
            super.DoCollideWorE();
        } else {
            for (IsoPushableObject pushableObject1 : this.connectList) {
                if (pushableObject1 != this) {
                    if (pushableObject1.ox < this.ox) {
                        pushableObject1.setNx(this.getNx() - 1.0F);
                        pushableObject1.setX(this.getX() - 1.0F);
                    } else if (pushableObject1.ox > this.ox) {
                        pushableObject1.setNx(this.getNx() + 1.0F);
                        pushableObject1.setX(this.getX() + 1.0F);
                    } else {
                        pushableObject1.setNx(this.getNx());
                        pushableObject1.setX(this.getX());
                    }

                    if (pushableObject1.oy < this.oy) {
                        pushableObject1.setNy(this.getNy() - 1.0F);
                        pushableObject1.setY(this.getY() - 1.0F);
                    } else if (pushableObject1.oy > this.oy) {
                        pushableObject1.setNy(this.getNy() + 1.0F);
                        pushableObject1.setY(this.getY() + 1.0F);
                    } else {
                        pushableObject1.setNy(this.getNy());
                        pushableObject1.setY(this.getY());
                    }

                    pushableObject1.setImpulsex(this.getImpulsex());
                    pushableObject1.setImpulsey(this.getImpulsey());
                }
            }
        }
    }
}
