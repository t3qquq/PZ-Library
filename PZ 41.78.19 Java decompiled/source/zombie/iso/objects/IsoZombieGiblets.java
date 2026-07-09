// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import zombie.GameTime;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoCell;
import zombie.iso.IsoPhysicsObject;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSpriteInstance;

public class IsoZombieGiblets extends IsoPhysicsObject {
    public float tintb = 1.0F;
    public float tintg = 1.0F;
    public float tintr = 1.0F;
    public float time = 0.0F;
    boolean invis = false;

    public IsoZombieGiblets(IsoCell cell) {
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
    public void update() {
        if (Rand.Next(Rand.AdjustForFramerate(12)) == 0
            && this.getZ() > (int)this.getZ()
            && this.getCurrentSquare() != null
            && this.getCurrentSquare().getChunk() != null) {
            this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, (int)this.z, Rand.Next(8));
        }

        if (Core.bLastStand
            && Rand.Next(Rand.AdjustForFramerate(15)) == 0
            && this.getZ() > (int)this.getZ()
            && this.getCurrentSquare() != null
            && this.getCurrentSquare().getChunk() != null) {
            this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, (int)this.z, Rand.Next(8));
        }

        super.update();
        this.time = this.time + GameTime.instance.getMultipliedSecondsSinceLastUpdate();
        if (this.velX == 0.0F && this.velY == 0.0F && this.getZ() == (int)this.getZ()) {
            this.setCollidable(false);
            IsoWorld.instance.CurrentCell.getRemoveList().add(this);
        }
    }

    @Override
    public void render(float x, float y, float z, ColorInfo info, boolean bDoAttached, boolean bWallLightingPass, Shader shader) {
        if (!this.invis) {
            float float0 = info.r;
            float float1 = info.g;
            float float2 = info.b;
            info.r = 0.5F;
            info.g = 0.5F;
            info.b = 0.5F;
            this.setTargetAlpha(this.sprite.def.targetAlpha = this.def.targetAlpha = 1.0F - this.time / 1.0F);
            super.render(x, y, z, info, bDoAttached, bWallLightingPass, shader);
            if (Core.bDebug) {
            }

            info.r = float0;
            info.g = float1;
            info.b = float2;
        }
    }

    public IsoZombieGiblets(IsoZombieGiblets.GibletType type, IsoCell cell, float x, float y, float z, float xvel, float yvel) {
        super(cell);
        this.velX = xvel;
        this.velY = yvel;
        float float0 = Rand.Next(4000) / 10000.0F;
        float float1 = Rand.Next(4000) / 10000.0F;
        float0 -= 0.2F;
        float1 -= 0.2F;
        this.velX += float0;
        this.velY += float1;
        this.x = x;
        this.y = y;
        this.z = z;
        this.nx = x;
        this.ny = y;
        this.setAlpha(0.2F);
        this.def = IsoSpriteInstance.get(this.sprite);
        this.def.alpha = 0.2F;
        this.sprite.def.alpha = 0.4F;
        this.offsetX = 0.0F;
        this.offsetY = 0.0F;
        if (Rand.Next(3) != 0) {
            this.def.alpha = 0.0F;
            this.sprite.def.alpha = 0.0F;
            this.invis = true;
        }

        switch (type) {
            case A:
                this.sprite.setFromCache("Giblet", "00", 3);
                break;
            case B:
                this.sprite.setFromCache("Giblet", "01", 3);
                break;
            case Eye:
                this.sprite.setFromCache("Eyeball", "00", 1);
        }
    }

    public static enum GibletType {
        A,
        B,
        Eye;
    }
}
