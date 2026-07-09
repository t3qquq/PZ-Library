// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import zombie.characters.IsoPlayer;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoPushableObject;

public class IsoWheelieBin extends IsoPushableObject {
    float velx = 0.0F;
    float vely = 0.0F;

    @Override
    public String getObjectName() {
        return "WheelieBin";
    }

    public IsoWheelieBin(IsoCell cell) {
        super(cell);
        this.container = new ItemContainer("wheeliebin", this.square, this);
        this.Collidable = true;
        this.solid = true;
        this.shootable = false;
        this.width = 0.3F;
        this.dir = IsoDirections.E;
        this.setAlphaAndTarget(0.0F);
        this.offsetX = -26.0F;
        this.offsetY = -248.0F;
        this.OutlineOnMouseover = true;
        this.sprite.LoadFramesPageSimple("TileObjectsExt_7", "TileObjectsExt_5", "TileObjectsExt_6", "TileObjectsExt_8");
    }

    public IsoWheelieBin(IsoCell cell, int x, int y, int z) {
        super(cell, x, y, z);
        this.x = x + 0.5F;
        this.y = y + 0.5F;
        this.z = z;
        this.nx = this.x;
        this.ny = this.y;
        this.offsetX = -26.0F;
        this.offsetY = -248.0F;
        this.weight = 6.0F;
        this.sprite.LoadFramesPageSimple("TileObjectsExt_7", "TileObjectsExt_5", "TileObjectsExt_6", "TileObjectsExt_8");
        this.square = this.getCell().getGridSquare(x, y, z);
        this.current = this.getCell().getGridSquare(x, y, z);
        this.container = new ItemContainer("wheeliebin", this.square, this);
        this.Collidable = true;
        this.solid = true;
        this.shootable = false;
        this.width = 0.3F;
        this.dir = IsoDirections.E;
        this.setAlphaAndTarget(0.0F);
        this.OutlineOnMouseover = true;
    }

    @Override
    public void update() {
        this.velx = this.getX() - this.getLx();
        this.vely = this.getY() - this.getLy();
        float float0 = 1.0F - this.container.getContentsWeight() / 500.0F;
        if (float0 < 0.0F) {
            float0 = 0.0F;
        }

        if (float0 < 0.7F) {
            float0 *= float0;
        }

        if (IsoPlayer.getInstance() != null && IsoPlayer.getInstance().getDragObject() != this) {
            if (this.velx != 0.0F && this.vely == 0.0F && (this.dir == IsoDirections.E || this.dir == IsoDirections.W)) {
                this.setNx(this.getNx() + this.velx * 0.65F * float0);
            }

            if (this.vely != 0.0F && this.velx == 0.0F && (this.dir == IsoDirections.N || this.dir == IsoDirections.S)) {
                this.setNy(this.getNy() + this.vely * 0.65F * float0);
            }
        }

        super.update();
    }

    @Override
    public float getWeight(float x, float y) {
        float float0 = this.container.getContentsWeight() / 500.0F;
        if (float0 < 0.0F) {
            float0 = 0.0F;
        }

        if (float0 > 1.0F) {
            return this.getWeight() * 8.0F;
        } else {
            float float1 = this.getWeight() * float0 + 1.5F;
            if (this.dir != IsoDirections.W && (this.dir != IsoDirections.E || y != 0.0F)) {
                return this.dir != IsoDirections.N && (this.dir != IsoDirections.S || x != 0.0F) ? float1 * 3.0F : float1 / 2.0F;
            } else {
                return float1 / 2.0F;
            }
        }
    }
}
