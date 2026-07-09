// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import zombie.GameTime;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoCell;
import zombie.iso.IsoMovingObject;
import zombie.iso.sprite.IsoSpriteInstance;

public class IsoZombieHead extends IsoMovingObject {
    public float tintb = 1.0F;
    public float tintg = 1.0F;
    public float tintr = 1.0F;
    public float time = 0.0F;

    public IsoZombieHead(IsoCell cell) {
        super(cell);
    }

    @Override
    public boolean Serialize() {
        return false;
    }

    @Override
    public String getObjectName() {
        return "ZombieHead";
    }

    @Override
    public void update() {
        super.update();
        this.time = this.time + GameTime.instance.getMultipliedSecondsSinceLastUpdate();
        this.sx = this.sy = 0.0F;
    }

    @Override
    public void render(float float0, float float1, float float2, ColorInfo colorInfo, boolean boolean0, boolean boolean1, Shader shader) {
        this.setTargetAlpha(1.0F);
        super.render(float0, float1, float2, colorInfo, boolean0, boolean1, shader);
    }

    public IsoZombieHead(IsoZombieHead.GibletType gibletType, IsoCell cell, float float0, float float1, float float2) {
        super(cell);
        this.solid = false;
        this.shootable = false;
        this.x = float0;
        this.y = float1;
        this.z = float2;
        this.nx = float0;
        this.ny = float1;
        this.setAlpha(0.5F);
        this.def = IsoSpriteInstance.get(this.sprite);
        this.def.alpha = 1.0F;
        this.sprite.def.alpha = 1.0F;
        this.offsetX = -26.0F;
        this.offsetY = -242.0F;
        switch (gibletType) {
            case A:
                this.sprite.LoadFramesNoDirPageDirect("media/gibs/Giblet", "00", 3);
                break;
            case B:
                this.sprite.LoadFramesNoDirPageDirect("media/gibs/Giblet", "01", 3);
        }
    }

    public static enum GibletType {
        A,
        B,
        Eye;
    }
}
