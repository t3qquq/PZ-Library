// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import zombie.GameTime;
import zombie.core.Rand;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoPhysicsObject;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.sprite.IsoSpriteInstance;

public class IsoBloodDrop extends IsoPhysicsObject {
    public float tintb = 1.0F;
    public float tintg = 1.0F;
    public float tintr = 1.0F;
    public float time = 0.0F;
    float sx = 0.0F;
    float sy = 0.0F;
    float lsx = 0.0F;
    float lsy = 0.0F;
    static Vector2 temp = new Vector2();

    public IsoBloodDrop(IsoCell cell) {
        super(cell);
    }

    @Override
    public boolean Serialize() {
        return false;
    }

    @Override
    public String getObjectName() {
        return "ZombieGiblets";
    }

    @Override
    public void collideGround() {
        float float0 = this.x - (int)this.x;
        float float1 = this.y - (int)this.y;
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((int)this.x, (int)this.y, (int)this.z);
        if (square != null) {
            IsoObject object = square.getFloor();
            object.addChild(this);
            this.setCollidable(false);
            IsoWorld.instance.CurrentCell.getRemoveList().add(this);
        }
    }

    @Override
    public void collideWall() {
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((int)this.x, (int)this.y, (int)this.z);
        if (square != null) {
            IsoObject object = null;
            if (this.isCollidedN()) {
                object = square.getWall(true);
            } else if (this.isCollidedS()) {
                square = IsoWorld.instance.CurrentCell.getGridSquare((int)this.x, (int)this.y + 1, (int)this.z);
                if (square != null) {
                    object = square.getWall(true);
                }
            } else if (this.isCollidedW()) {
                object = square.getWall(false);
            } else if (this.isCollidedE()) {
                square = IsoWorld.instance.CurrentCell.getGridSquare((int)this.x + 1, (int)this.y, (int)this.z);
                if (square != null) {
                    object = square.getWall(false);
                }
            }

            if (object != null) {
                object.addChild(this);
                this.setCollidable(false);
                IsoWorld.instance.CurrentCell.getRemoveList().add(this);
            }
        }
    }

    @Override
    public void update() {
        super.update();
        this.time = this.time + GameTime.instance.getMultipliedSecondsSinceLastUpdate();
        if (this.velX == 0.0F && this.velY == 0.0F && this.getZ() == (int)this.getZ()) {
            this.setCollidable(false);
            IsoWorld.instance.CurrentCell.getRemoveList().add(this);
        }
    }

    @Override
    public void render(float float0, float float1, float float2, ColorInfo colorInfo, boolean var5, boolean var6, Shader var7) {
        this.setTargetAlpha(0.3F);
        this.sprite.render(this, float0, float1, float2, this.dir, this.offsetX, this.offsetY, colorInfo, true);
    }

    public IsoBloodDrop(IsoCell cell, float float3, float float4, float float5, float float0, float float1) {
        super(cell);
        this.velX = float0 * 2.0F;
        this.velY = float1 * 2.0F;
        this.terminalVelocity = -0.1F;
        this.velZ = this.velZ + (Rand.Next(10000) / 10000.0F - 0.5F) * 0.05F;
        float float2 = Rand.Next(9000) / 10000.0F;
        float2 += 0.1F;
        this.velX *= float2;
        this.velY *= float2;
        this.velZ += float2 * 0.05F;
        if (Rand.Next(7) == 0) {
            this.velX *= 2.0F;
            this.velY *= 2.0F;
        }

        this.velX *= 0.8F;
        this.velY *= 0.8F;
        temp.x = this.velX;
        temp.y = this.velY;
        temp.rotate((Rand.Next(1000) / 1000.0F - 0.5F) * 0.07F);
        if (Rand.Next(3) == 0) {
            temp.rotate((Rand.Next(1000) / 1000.0F - 0.5F) * 0.1F);
        }

        if (Rand.Next(5) == 0) {
            temp.rotate((Rand.Next(1000) / 1000.0F - 0.5F) * 0.2F);
        }

        if (Rand.Next(8) == 0) {
            temp.rotate((Rand.Next(1000) / 1000.0F - 0.5F) * 0.3F);
        }

        if (Rand.Next(10) == 0) {
            temp.rotate((Rand.Next(1000) / 1000.0F - 0.5F) * 0.4F);
        }

        this.velX = temp.x;
        this.velY = temp.y;
        this.x = float3;
        this.y = float4;
        this.z = float5;
        this.nx = float3;
        this.ny = float4;
        this.setAlpha(0.5F);
        this.def = IsoSpriteInstance.get(this.sprite);
        this.def.alpha = 0.4F;
        this.sprite.def.alpha = 0.4F;
        this.offsetX = -26.0F;
        this.offsetY = -242.0F;
        this.offsetX += 8.0F;
        this.offsetY += 9.0F;
        this.sprite.LoadFramesNoDirPageSimple("BloodSplat");
    }
}
