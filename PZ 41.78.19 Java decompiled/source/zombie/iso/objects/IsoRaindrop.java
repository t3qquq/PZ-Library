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

public class IsoRaindrop extends IsoObject {
    public int AnimSpriteIndex;
    public float GravMod;
    public int Life;
    public float SplashY;
    public float OffsetY;
    public float Vel_Y;

    @Override
    public boolean Serialize() {
        return false;
    }

    public IsoRaindrop(IsoCell cell, IsoGridSquare gridSquare, boolean CanSee) {
        if (CanSee) {
            if (gridSquare != null) {
                if (!gridSquare.getProperties().Is(IsoFlagType.HasRaindrop)) {
                    this.Life = 0;
                    this.square = gridSquare;
                    int int0 = 1 * Core.TileScale;
                    int int1 = 64 * Core.TileScale;
                    float float0 = Rand.Next(0.1F, 0.9F);
                    float float1 = Rand.Next(0.1F, 0.9F);
                    short short0 = (short)(IsoUtils.XToScreen(float0, float1, 0.0F, 0) - int0 / 2);
                    short short1 = (short)(IsoUtils.YToScreen(float0, float1, 0.0F, 0) - int1);
                    this.offsetX = 0.0F;
                    this.offsetY = 0.0F;
                    this.OffsetY = RainManager.RaindropStartDistance;
                    this.SplashY = short1;
                    this.AttachAnim("Rain", "00", 1, 0.0F, -short0, -short1, true, 0, false, 0.7F, RainManager.RaindropTintMod);
                    if (this.AttachedAnimSprite != null) {
                        this.AnimSpriteIndex = this.AttachedAnimSprite.size() - 1;
                    } else {
                        this.AnimSpriteIndex = 0;
                    }

                    this.AttachedAnimSprite.get(this.AnimSpriteIndex).setScale(Core.TileScale, Core.TileScale);
                    gridSquare.getProperties().Set(IsoFlagType.HasRaindrop);
                    this.Vel_Y = 0.0F;
                    float float2 = 1000000.0F / Rand.Next(1000000) + 1.0E-5F;
                    this.GravMod = -(RainManager.GravModMin + (RainManager.GravModMax - RainManager.GravModMin) * float2);
                    RainManager.AddRaindrop(this);
                }
            }
        }
    }

    @Override
    public boolean HasTooltip() {
        return false;
    }

    @Override
    public String getObjectName() {
        return "RainDrops";
    }

    public boolean TestCollide(IsoMovingObject obj, IsoGridSquare PassedObjectSquare) {
        return this.square == PassedObjectSquare;
    }

    @Override
    public IsoObject.VisionResult TestVision(IsoGridSquare from, IsoGridSquare to) {
        return IsoObject.VisionResult.NoEffect;
    }

    public void ChangeTintMod(ColorInfo NewTintMod) {
    }

    @Override
    public void update() {
        this.sx = this.sy = 0.0F;
        this.Life++;

        for (int int0 = 0; int0 < this.AttachedAnimSprite.size(); int0++) {
            IsoSpriteInstance spriteInstance = this.AttachedAnimSprite.get(int0);
            spriteInstance.update();
            spriteInstance.Frame = spriteInstance.Frame + spriteInstance.AnimFrameIncrease * (GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F);
            IsoSprite sprite = spriteInstance.parentSprite;
            if ((int)spriteInstance.Frame >= sprite.CurrentAnim.Frames.size() && sprite.Loop && spriteInstance.Looped) {
                spriteInstance.Frame = 0.0F;
            }
        }

        this.Vel_Y = this.Vel_Y + this.GravMod * (GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F);
        this.OffsetY = this.OffsetY + this.Vel_Y;
        if (this.AttachedAnimSprite != null && this.AttachedAnimSprite.size() > this.AnimSpriteIndex && this.AnimSpriteIndex >= 0) {
            this.AttachedAnimSprite.get(this.AnimSpriteIndex).parentSprite.soffY = (short)(this.SplashY + (int)this.OffsetY);
        }

        if (this.OffsetY < 0.0F) {
            this.OffsetY = RainManager.RaindropStartDistance;
            this.Vel_Y = 0.0F;
            float float0 = 1000000.0F / Rand.Next(1000000) + 1.0E-5F;
            this.GravMod = -(RainManager.GravModMin + (RainManager.GravModMax - RainManager.GravModMin) * float0);
        }

        for (int int1 = 0; int1 < IsoPlayer.numPlayers; int1++) {
            if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
                this.setAlphaAndTarget(int1, 0.55F);
            } else {
                this.setAlphaAndTarget(int1, 1.0F);
            }
        }
    }

    void Reset(IsoGridSquare square, boolean boolean0) {
        if (boolean0) {
            if (square != null) {
                if (!square.getProperties().Is(IsoFlagType.HasRaindrop)) {
                    this.Life = 0;
                    this.square = square;
                    this.OffsetY = RainManager.RaindropStartDistance;
                    if (this.AttachedAnimSprite != null) {
                        this.AnimSpriteIndex = this.AttachedAnimSprite.size() - 1;
                    } else {
                        this.AnimSpriteIndex = 0;
                    }

                    square.getProperties().Set(IsoFlagType.HasRaindrop);
                    this.Vel_Y = 0.0F;
                    float float0 = 1000000.0F / Rand.Next(1000000) + 1.0E-5F;
                    this.GravMod = -(RainManager.GravModMin + (RainManager.GravModMax - RainManager.GravModMin) * float0);
                    RainManager.AddRaindrop(this);
                }
            }
        }
    }
}
