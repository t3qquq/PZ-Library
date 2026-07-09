// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.sprite;

import java.util.concurrent.atomic.AtomicBoolean;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoCamera;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.popman.ObjectPool;

public final class IsoSpriteInstance {
    public static final ObjectPool<IsoSpriteInstance> pool = new ObjectPool<>(IsoSpriteInstance::new);
    private static final AtomicBoolean lock = new AtomicBoolean(false);
    public IsoSprite parentSprite;
    public float tintb = 1.0F;
    public float tintg = 1.0F;
    public float tintr = 1.0F;
    public float Frame = 0.0F;
    public float alpha = 1.0F;
    public float targetAlpha = 1.0F;
    public boolean bCopyTargetAlpha = true;
    public boolean bMultiplyObjectAlpha = false;
    public boolean Flip;
    public float offZ = 0.0F;
    public float offX = 0.0F;
    public float offY = 0.0F;
    public float AnimFrameIncrease = 1.0F;
    static float multiplier = 1.0F;
    public boolean Looped = true;
    public boolean Finished = false;
    public boolean NextFrame;
    public float scaleX = 1.0F;
    public float scaleY = 1.0F;

    public static IsoSpriteInstance get(IsoSprite spr) {
        while (!lock.compareAndSet(false, true)) {
            Thread.onSpinWait();
        }

        IsoSpriteInstance spriteInstance = pool.alloc();
        lock.set(false);
        spriteInstance.parentSprite = spr;
        spriteInstance.reset();
        return spriteInstance;
    }

    private void reset() {
        this.tintb = 1.0F;
        this.tintg = 1.0F;
        this.tintr = 1.0F;
        this.Frame = 0.0F;
        this.alpha = 1.0F;
        this.targetAlpha = 1.0F;
        this.bCopyTargetAlpha = true;
        this.bMultiplyObjectAlpha = false;
        this.Flip = false;
        this.offZ = 0.0F;
        this.offX = 0.0F;
        this.offY = 0.0F;
        this.AnimFrameIncrease = 1.0F;
        multiplier = 1.0F;
        this.Looped = true;
        this.Finished = false;
        this.NextFrame = false;
        this.scaleX = 1.0F;
        this.scaleY = 1.0F;
    }

    public IsoSpriteInstance() {
    }

    public void setFrameSpeedPerFrame(float perSecond) {
        this.AnimFrameIncrease = perSecond * multiplier;
    }

    public int getID() {
        return this.parentSprite.ID;
    }

    public String getName() {
        return this.parentSprite.getName();
    }

    public IsoSprite getParentSprite() {
        return this.parentSprite;
    }

    public IsoSpriteInstance(IsoSprite spr) {
        this.parentSprite = spr;
    }

    public float getTintR() {
        return this.tintr;
    }

    public float getTintG() {
        return this.tintg;
    }

    public float getTintB() {
        return this.tintb;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public float getTargetAlpha() {
        return this.targetAlpha;
    }

    public boolean isCopyTargetAlpha() {
        return this.bCopyTargetAlpha;
    }

    public boolean isMultiplyObjectAlpha() {
        return this.bMultiplyObjectAlpha;
    }

    public void render(IsoObject obj, float x, float y, float z, IsoDirections dir, float offsetX, float offsetY, ColorInfo info2) {
        this.parentSprite.render(this, obj, x, y, z, dir, offsetX, offsetY, info2, true);
    }

    public void SetAlpha(float f) {
        this.alpha = f;
        this.bCopyTargetAlpha = false;
    }

    public void SetTargetAlpha(float _targetAlpha) {
        this.targetAlpha = _targetAlpha;
        this.bCopyTargetAlpha = false;
    }

    public void update() {
    }

    protected void renderprep(IsoObject object) {
        if (object != null && this.bCopyTargetAlpha) {
            this.targetAlpha = object.getTargetAlpha(IsoCamera.frameState.playerIndex);
            this.alpha = object.getAlpha(IsoCamera.frameState.playerIndex);
        } else if (!this.bMultiplyObjectAlpha) {
            if (this.alpha < this.targetAlpha) {
                this.alpha = this.alpha + IsoSprite.alphaStep;
                if (this.alpha > this.targetAlpha) {
                    this.alpha = this.targetAlpha;
                }
            } else if (this.alpha > this.targetAlpha) {
                this.alpha = this.alpha - IsoSprite.alphaStep;
                if (this.alpha < this.targetAlpha) {
                    this.alpha = this.targetAlpha;
                }
            }

            if (this.alpha < 0.0F) {
                this.alpha = 0.0F;
            }

            if (this.alpha > 1.0F) {
                this.alpha = 1.0F;
            }
        }
    }

    public float getFrame() {
        return this.Frame;
    }

    public boolean isFinished() {
        return this.Finished;
    }

    public void Dispose() {
    }

    public void RenderGhostTileColor(int x, int y, int z, float r, float g, float b, float a) {
        if (this.parentSprite != null) {
            IsoSpriteInstance spriteInstance = get(this.parentSprite);
            spriteInstance.Frame = this.Frame;
            spriteInstance.tintr = r;
            spriteInstance.tintg = g;
            spriteInstance.tintb = b;
            spriteInstance.alpha = spriteInstance.targetAlpha = a;
            IsoGridSquare.getDefColorInfo().r = IsoGridSquare.getDefColorInfo().g = IsoGridSquare.getDefColorInfo().b = IsoGridSquare.getDefColorInfo().a = 1.0F;
            this.parentSprite.render(spriteInstance, null, x, y, z, IsoDirections.N, 0.0F, -144.0F, IsoGridSquare.getDefColorInfo(), true);
        }
    }

    public void setScale(float _scaleX, float _scaleY) {
        this.scaleX = _scaleX;
        this.scaleY = _scaleY;
    }

    public float getScaleX() {
        return this.scaleX;
    }

    public float getScaleY() {
        return this.scaleY;
    }

    public void scaleAspect(float texW, float texH, float width, float height) {
        if (texW > 0.0F && texH > 0.0F && width > 0.0F && height > 0.0F) {
            float float0 = height * texW / texH;
            float float1 = width * texH / texW;
            boolean boolean0 = float0 <= width;
            if (boolean0) {
                width = float0;
            } else {
                height = float1;
            }

            this.scaleX = width / texW;
            this.scaleY = height / texH;
        }
    }

    public static void add(IsoSpriteInstance isoSpriteInstance) {
        isoSpriteInstance.reset();

        while (!lock.compareAndSet(false, true)) {
            Thread.onSpinWait();
        }

        pool.release(isoSpriteInstance);
        lock.set(false);
    }
}
