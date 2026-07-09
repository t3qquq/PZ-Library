// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import se.krka.kahlua.vm.KahluaTable;
import zombie.characters.IsoGameCharacter;
import zombie.characters.SurvivorDesc;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.skinnedmodel.advancedanimation.AnimatedModel;
import zombie.core.skinnedmodel.population.IClothingItemListener;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.textures.TextureDraw;
import zombie.iso.IsoDirections;
import zombie.util.StringUtils;

public final class UI3DModel extends UIElement implements IClothingItemListener {
    private final AnimatedModel animatedModel = new AnimatedModel();
    private IsoDirections dir = IsoDirections.E;
    private boolean bDoExt = false;
    private long nextExt = -1L;
    private final UI3DModel.Drawer[] drawers = new UI3DModel.Drawer[3];
    private float zoom = 0.0F;
    private float yOffset = 0.0F;
    private float xOffset = 0.0F;

    public UI3DModel(KahluaTable table) {
        super(table);

        for (int int0 = 0; int0 < this.drawers.length; int0++) {
            this.drawers[int0] = new UI3DModel.Drawer();
        }

        if (OutfitManager.instance != null) {
            OutfitManager.instance.addClothingItemListener(this);
        }
    }

    @Override
    public void render() {
        if (this.isVisible()) {
            super.render();
            if (this.Parent == null || this.Parent.maxDrawHeight == -1 || !(this.Parent.maxDrawHeight <= this.y)) {
                if (this.bDoExt) {
                    long long0 = System.currentTimeMillis();
                    if (this.nextExt < 0L) {
                        this.nextExt = long0 + Rand.Next(5000, 10000);
                    }

                    if (this.nextExt < long0) {
                        this.animatedModel.getActionContext().reportEvent("EventDoExt");
                        this.animatedModel.setVariable("Ext", Rand.Next(0, 6) + 1);
                        this.nextExt = -1L;
                    }
                }

                this.animatedModel.update();
                UI3DModel.Drawer drawer = this.drawers[SpriteRenderer.instance.getMainStateIndex()];
                drawer.init(this.getAbsoluteX().intValue(), this.getAbsoluteY().intValue());
                SpriteRenderer.instance.drawGeneric(drawer);
            }
        }
    }

    public void setDirection(IsoDirections _dir) {
        this.dir = _dir;
        this.animatedModel.setAngle(_dir.ToVector());
    }

    public IsoDirections getDirection() {
        return this.dir;
    }

    public void setAnimate(boolean animate) {
        this.animatedModel.setAnimate(animate);
    }

    public void setAnimSetName(String name) {
        this.animatedModel.setAnimSetName(name);
    }

    public void setDoRandomExtAnimations(boolean doExt) {
        this.bDoExt = doExt;
    }

    public void setIsometric(boolean iso) {
        this.animatedModel.setIsometric(iso);
    }

    public void setOutfitName(String outfitName, boolean female, boolean zombie) {
        this.animatedModel.setOutfitName(outfitName, female, zombie);
    }

    public void setCharacter(IsoGameCharacter character) {
        this.animatedModel.setCharacter(character);
    }

    public void setSurvivorDesc(SurvivorDesc survivorDesc) {
        this.animatedModel.setSurvivorDesc(survivorDesc);
    }

    public void setState(String state) {
        this.animatedModel.setState(state);
    }

    public void reportEvent(String event) {
        if (!StringUtils.isNullOrWhitespace(event)) {
            this.animatedModel.getActionContext().reportEvent(event);
        }
    }

    @Override
    public void clothingItemChanged(String itemGuid) {
        this.animatedModel.clothingItemChanged(itemGuid);
    }

    public void setZoom(float newZoom) {
        this.zoom = newZoom;
    }

    public void setYOffset(float newYOffset) {
        this.yOffset = newYOffset;
    }

    public void setXOffset(float newXOffset) {
        this.xOffset = newXOffset;
    }

    private final class Drawer extends TextureDraw.GenericDrawer {
        int absX;
        int absY;
        float m_animPlayerAngle;
        boolean bRendered;

        public void init(int arg0, int arg1) {
            this.absX = arg0;
            this.absY = arg1;
            this.m_animPlayerAngle = UI3DModel.this.animatedModel.getAnimationPlayer().getRenderedAngle();
            this.bRendered = false;
            float float0 = UI3DModel.this.animatedModel.isIsometric() ? -0.45F : -0.5F;
            if (UI3DModel.this.yOffset != 0.0F) {
                float0 = UI3DModel.this.yOffset;
            }

            UI3DModel.this.animatedModel.setOffset(UI3DModel.this.xOffset, float0, 0.0F);
            UI3DModel.this.animatedModel.renderMain();
        }

        @Override
        public void render() {
            float float0 = UI3DModel.this.animatedModel.isIsometric() ? 22.0F : 25.0F;
            if (UI3DModel.this.zoom > 0.0F) {
                float0 -= UI3DModel.this.zoom;
            }

            UI3DModel.this.animatedModel
                .DoRender(
                    this.absX,
                    Core.height - this.absY - (int)UI3DModel.this.height,
                    (int)UI3DModel.this.width,
                    (int)UI3DModel.this.height,
                    float0,
                    this.m_animPlayerAngle
                );
            this.bRendered = true;
        }

        @Override
        public void postRender() {
            UI3DModel.this.animatedModel.postRender(this.bRendered);
        }
    }
}
