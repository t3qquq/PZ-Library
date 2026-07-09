// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import zombie.characters.EquippedTextureCreator;
import zombie.core.SpriteRenderer;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.popman.ObjectPool;
import zombie.util.Type;

public final class ModelInstanceTextureInitializer {
    private boolean m_bRendered;
    private ModelInstance m_modelInstance;
    private InventoryItem m_item;
    private float m_bloodLevel;
    private int m_changeNumberMain = 0;
    private int m_changeNumberThread = 0;
    private final ModelInstanceTextureInitializer.RenderData[] m_renderData = new ModelInstanceTextureInitializer.RenderData[3];
    private static final ObjectPool<ModelInstanceTextureInitializer> pool = new ObjectPool<>(ModelInstanceTextureInitializer::new);

    public void init(ModelInstance modelInstance, InventoryItem item) {
        this.m_item = item;
        this.m_modelInstance = modelInstance;
        HandWeapon weapon = Type.tryCastTo(item, HandWeapon.class);
        this.m_bloodLevel = weapon == null ? 0.0F : weapon.getBloodLevel();
        this.setDirty();
    }

    public void init(ModelInstance modelInstance, float bloodLevel) {
        this.m_item = null;
        this.m_modelInstance = modelInstance;
        this.m_bloodLevel = bloodLevel;
        this.setDirty();
    }

    public void setDirty() {
        this.m_changeNumberMain++;
        this.m_bRendered = false;
    }

    public boolean isDirty() {
        return !this.m_bRendered;
    }

    public void renderMain() {
        if (!this.m_bRendered) {
            int int0 = SpriteRenderer.instance.getMainStateIndex();
            if (this.m_renderData[int0] == null) {
                this.m_renderData[int0] = new ModelInstanceTextureInitializer.RenderData();
            }

            ModelInstanceTextureInitializer.RenderData renderData = this.m_renderData[int0];
            if (renderData.m_textureCreator == null) {
                renderData.m_changeNumber = this.m_changeNumberMain;
                renderData.m_textureCreator = EquippedTextureCreator.alloc();
                if (this.m_item == null) {
                    renderData.m_textureCreator.init(this.m_modelInstance, this.m_bloodLevel);
                } else {
                    renderData.m_textureCreator.init(this.m_modelInstance, this.m_item);
                }

                renderData.m_bRendered = false;
            }
        }
    }

    public void render() {
        int int0 = SpriteRenderer.instance.getRenderStateIndex();
        ModelInstanceTextureInitializer.RenderData renderData = this.m_renderData[int0];
        if (renderData != null) {
            if (renderData.m_textureCreator != null) {
                if (!renderData.m_bRendered) {
                    if (renderData.m_changeNumber == this.m_changeNumberThread) {
                        renderData.m_bRendered = true;
                    } else {
                        renderData.m_textureCreator.render();
                        if (renderData.m_textureCreator.isRendered()) {
                            this.m_changeNumberThread = renderData.m_changeNumber;
                            renderData.m_bRendered = true;
                        }
                    }
                }
            }
        }
    }

    public void postRender() {
        int int0 = SpriteRenderer.instance.getMainStateIndex();
        ModelInstanceTextureInitializer.RenderData renderData = this.m_renderData[int0];
        if (renderData != null) {
            if (renderData.m_textureCreator != null) {
                if (renderData.m_textureCreator.isRendered() && renderData.m_changeNumber == this.m_changeNumberMain) {
                    this.m_bRendered = true;
                }

                if (renderData.m_bRendered) {
                    renderData.m_textureCreator.postRender();
                    renderData.m_textureCreator = null;
                }
            }
        }
    }

    public boolean isRendered() {
        int int0 = SpriteRenderer.instance.getRenderStateIndex();
        ModelInstanceTextureInitializer.RenderData renderData = this.m_renderData[int0];
        if (renderData == null) {
            return true;
        } else {
            return renderData.m_textureCreator == null ? true : renderData.m_bRendered;
        }
    }

    public static ModelInstanceTextureInitializer alloc() {
        return pool.alloc();
    }

    public void release() {
        pool.release(this);
    }

    private static final class RenderData {
        int m_changeNumber = 0;
        boolean m_bRendered;
        EquippedTextureCreator m_textureCreator;
    }
}
