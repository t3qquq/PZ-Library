// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;

public class IsoRainSplash extends IsoObject {
    public int Age;

    @Override
    public boolean Serialize() {
        return false;
    }

    public IsoRainSplash(IsoCell cell, IsoGridSquare gridSquare) {
        if (gridSquare != null) {
            if (!gridSquare.getProperties().Is(IsoFlagType.HasRainSplashes)) {
                this.Age = 0;
                this.square = gridSquare;
                this.offsetX = 0.0F;
                this.offsetY = 0.0F;
                int int0 = 1 + Rand.Next(2);
                byte byte0 = 16;
                byte byte1 = 8;

                for (int int1 = 0; int1 < int0; int1++) {
                    float float0 = Rand.Next(0.1F, 0.9F);
                    float float1 = Rand.Next(0.1F, 0.9F);
                    short short0 = (short)(IsoUtils.XToScreen(float0, float1, 0.0F, 0) - byte0 / 2);
                    short short1 = (short)(IsoUtils.YToScreen(float0, float1, 0.0F, 0) - byte1 / 2);
                    this.AttachAnim(
                        "RainSplash", "00", 4, RainManager.RainSplashAnimDelay, -short0, -short1, true, 0, false, 0.7F, RainManager.RainSplashTintMod
                    );
                    this.AttachedAnimSprite.get(int1).Frame = (short)Rand.Next(4);
                    this.AttachedAnimSprite.get(int1).setScale(Core.TileScale, Core.TileScale);
                }

                gridSquare.getProperties().Set(IsoFlagType.HasRainSplashes);
                RainManager.AddRainSplash(this);
            }
        }
    }

    @Override
    public String getObjectName() {
        return "RainSplashes";
    }

    @Override
    public boolean HasTooltip() {
        return false;
    }

    public boolean TestCollide(IsoMovingObject obj, IsoGridSquare PassedObjectSquare) {
        return this.square == PassedObjectSquare;
    }

    @Override
    public IsoObject.VisionResult TestVision(IsoGridSquare from, IsoGridSquare to) {
        return IsoObject.VisionResult.NoEffect;
    }

    public void ChangeTintMod(ColorInfo NewTintMod) {
        if (this.AttachedAnimSprite != null) {
            int int0 = 0;

            while (int0 < this.AttachedAnimSprite.size()) {
                int0++;
            }
        }
    }

    @Override
    public void update() {
        this.sx = this.sy = 0.0F;
        this.Age++;

        for (int int0 = 0; int0 < this.AttachedAnimSprite.size(); int0++) {
            IsoSpriteInstance spriteInstance = this.AttachedAnimSprite.get(int0);
            IsoSprite sprite = spriteInstance.parentSprite;
            spriteInstance.update();
            spriteInstance.Frame = spriteInstance.Frame + spriteInstance.AnimFrameIncrease * (GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F);
            if ((int)spriteInstance.Frame >= sprite.CurrentAnim.Frames.size() && sprite.Loop && spriteInstance.Looped) {
                spriteInstance.Frame = 0.0F;
            }
        }

        for (int int1 = 0; int1 < IsoPlayer.numPlayers; int1++) {
            if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
                this.setAlphaAndTarget(int1, 0.25F);
            } else {
                this.setAlphaAndTarget(int1, 0.6F);
            }
        }
    }

    void Reset(IsoGridSquare square) {
        if (square != null) {
            if (!square.getProperties().Is(IsoFlagType.HasRainSplashes)) {
                this.Age = 0;
                this.square = square;
                int int0 = 1 + Rand.Next(2);
                if (this.AttachedAnimSprite != null) {
                    int int1 = 0;

                    while (int1 < this.AttachedAnimSprite.size()) {
                        int1++;
                    }
                }

                square.getProperties().Set(IsoFlagType.HasRainSplashes);
                RainManager.AddRainSplash(this);
            }
        }
    }
}
