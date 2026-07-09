// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.iso.IsoPhysicsObject;
import zombie.network.GameClient;

public class IsoMolotovCocktail extends IsoPhysicsObject {
    private HandWeapon weapon = null;
    private IsoGameCharacter character = null;
    private int timer = 0;
    private int explodeTimer = 0;

    @Override
    public String getObjectName() {
        return "MolotovCocktail";
    }

    public IsoMolotovCocktail(IsoCell cell) {
        super(cell);
    }

    public IsoMolotovCocktail(IsoCell cell, float x, float y, float z, float xvel, float yvel, HandWeapon _weapon, IsoGameCharacter _character) {
        super(cell);
        this.weapon = _weapon;
        this.character = _character;
        this.explodeTimer = _weapon.getTriggerExplosionTimer();
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
        this.offsetX = 0.0F;
        this.offsetY = 0.0F;
        this.terminalVelocity = -0.02F;
        Texture texture = this.sprite.LoadFrameExplicit(_weapon.getTex().getName());
        if (texture != null) {
            this.sprite.Animate = false;
            int int0 = Core.TileScale;
            this.sprite.def.scaleAspect(texture.getWidthOrig(), texture.getHeightOrig(), 16 * int0, 16 * int0);
        }

        this.speedMod = 0.6F;
    }

    public void collideCharacter() {
        if (this.explodeTimer == 0) {
            this.Explode();
        }
    }

    @Override
    public void collideGround() {
        if (this.explodeTimer == 0) {
            this.Explode();
        }
    }

    @Override
    public void collideWall() {
        if (this.explodeTimer == 0) {
            this.Explode();
        }
    }

    @Override
    public void update() {
        super.update();
        if (this.isCollidedThisFrame() && this.explodeTimer == 0) {
            this.Explode();
        }

        if (this.explodeTimer > 0) {
            this.timer++;
            if (this.timer >= this.explodeTimer) {
                this.Explode();
            }
        }
    }

    @Override
    public void render(float x, float y, float z, ColorInfo info, boolean bDoAttached, boolean bWallLightingPass, Shader shader) {
        super.render(x, y, z, info, bDoAttached, bWallLightingPass, shader);
        if (Core.bDebug) {
        }
    }

    void Explode() {
        this.setDestroyed(true);
        this.getCurrentSquare().getMovingObjects().remove(this);
        this.getCell().Remove(this);
        if (GameClient.bClient) {
            if (!(this.character instanceof IsoPlayer) || !((IsoPlayer)this.character).isLocalPlayer()) {
                return;
            }

            this.square.syncIsoTrap(this.weapon);
        }

        if (this.weapon.isInstantExplosion()) {
            IsoTrap trap = new IsoTrap(this.weapon, this.getCurrentSquare().getCell(), this.getCurrentSquare());
            this.getCurrentSquare().AddTileObject(trap);
            trap.triggerExplosion(false);
        }
    }
}
