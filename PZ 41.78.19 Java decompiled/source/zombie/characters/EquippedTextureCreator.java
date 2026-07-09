// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import zombie.characterTextures.ItemSmartTexture;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.core.textures.SmartTexture;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.popman.ObjectPool;

public final class EquippedTextureCreator extends TextureDraw.GenericDrawer {
    private boolean bRendered;
    private ModelInstance modelInstance;
    private float bloodLevel;
    private final ArrayList<Texture> texturesNotReady = new ArrayList<>();
    private static final ObjectPool<EquippedTextureCreator> pool = new ObjectPool<>(EquippedTextureCreator::new);

    public void init(ModelInstance _modelInstance, InventoryItem item) {
        float float0 = 0.0F;
        if (item instanceof HandWeapon) {
            float0 = ((HandWeapon)item).getBloodLevel();
        }

        this.init(_modelInstance, float0);
    }

    public void init(ModelInstance _modelInstance, float _bloodLevel) {
        this.bRendered = false;
        this.texturesNotReady.clear();
        this.modelInstance = _modelInstance;
        this.bloodLevel = _bloodLevel;
        if (this.modelInstance != null) {
            this.modelInstance.renderRefCount++;
            Texture texture = this.modelInstance.tex;
            if (texture instanceof SmartTexture) {
                texture = null;
            }

            if (texture != null && !texture.isReady()) {
                this.texturesNotReady.add(texture);
            }

            texture = Texture.getSharedTexture("media/textures/BloodTextures/BloodOverlayWeapon.png");
            if (texture != null && !texture.isReady()) {
                this.texturesNotReady.add(texture);
            }

            texture = Texture.getSharedTexture("media/textures/BloodTextures/BloodOverlayWeaponMask.png");
            if (texture != null && !texture.isReady()) {
                this.texturesNotReady.add(texture);
            }
        }
    }

    @Override
    public void render() {
        for (int int0 = 0; int0 < this.texturesNotReady.size(); int0++) {
            Texture texture = this.texturesNotReady.get(int0);
            if (!texture.isReady()) {
                return;
            }
        }

        GL11.glPushAttrib(2048);

        try {
            this.updateTexture(this.modelInstance, this.bloodLevel);
        } finally {
            GL11.glPopAttrib();
        }

        this.bRendered = true;
    }

    private void updateTexture(ModelInstance modelInstancex, float float0) {
        if (modelInstancex != null) {
            ItemSmartTexture itemSmartTexture = null;
            if (float0 > 0.0F) {
                if (modelInstancex.tex instanceof ItemSmartTexture) {
                    itemSmartTexture = (ItemSmartTexture)modelInstancex.tex;
                } else if (modelInstancex.tex != null) {
                    itemSmartTexture = new ItemSmartTexture(modelInstancex.tex.getName());
                }
            } else if (modelInstancex.tex instanceof ItemSmartTexture) {
                itemSmartTexture = (ItemSmartTexture)modelInstancex.tex;
            }

            if (itemSmartTexture != null) {
                itemSmartTexture.setBlood(
                    "media/textures/BloodTextures/BloodOverlayWeapon.png", "media/textures/BloodTextures/BloodOverlayWeaponMask.png", float0, 300
                );
                itemSmartTexture.calculate();
                modelInstancex.tex = itemSmartTexture;
            }
        }
    }

    @Override
    public void postRender() {
        ModelManager.instance.derefModelInstance(this.modelInstance);
        this.texturesNotReady.clear();
        if (!this.bRendered) {
        }

        pool.release(this);
    }

    public boolean isRendered() {
        return this.bRendered;
    }

    public static EquippedTextureCreator alloc() {
        return pool.alloc();
    }
}
