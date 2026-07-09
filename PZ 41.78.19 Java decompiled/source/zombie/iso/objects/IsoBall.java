// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import zombie.WorldSoundManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.iso.IsoPhysicsObject;
import zombie.network.GameClient;

public class IsoBall extends IsoPhysicsObject {
    private HandWeapon weapon = null;
    private IsoGameCharacter character = null;
    private int lastCheckX = 0;
    private int lastCheckY = 0;

    @Override
    public String getObjectName() {
        return "MolotovCocktail";
    }

    public IsoBall(IsoCell cell) {
        super(cell);
    }

    public IsoBall(IsoCell cell, float float4, float float5, float float6, float float0, float float1, HandWeapon weaponx, IsoGameCharacter characterx) {
        super(cell);
        this.weapon = weaponx;
        this.character = characterx;
        this.velX = float0;
        this.velY = float1;
        float float2 = Rand.Next(4000) / 10000.0F;
        float float3 = Rand.Next(4000) / 10000.0F;
        float2 -= 0.2F;
        float3 -= 0.2F;
        this.velX += float2;
        this.velY += float3;
        this.x = float4;
        this.y = float5;
        this.z = float6;
        this.nx = float4;
        this.ny = float5;
        this.offsetX = 0.0F;
        this.offsetY = 0.0F;
        this.terminalVelocity = -0.02F;
        Texture texture = this.sprite.LoadFrameExplicit(weaponx.getTex().getName());
        if (texture != null) {
            this.sprite.Animate = false;
            int int0 = Core.TileScale;
            this.sprite.def.scaleAspect(texture.getWidthOrig(), texture.getHeightOrig(), 16 * int0, 16 * int0);
        }

        this.speedMod = 0.6F;
    }

    @Override
    public void collideGround() {
        this.Fall();
    }

    @Override
    public void collideWall() {
        this.Fall();
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void render(float float0, float float1, float float2, ColorInfo colorInfo, boolean boolean0, boolean boolean1, Shader shader) {
        super.render(float0, float1, float2, colorInfo, boolean0, boolean1, shader);
        if (Core.bDebug) {
        }
    }

    void Fall() {
        this.getCurrentSquare().getMovingObjects().remove(this);
        this.getCell().Remove(this);
        if (!GameClient.bClient) {
            WorldSoundManager.instance.addSound(this, (int)this.x, (int)this.y, 0, 600, 600);
        }

        if (this.character instanceof IsoPlayer) {
            if (((IsoPlayer)this.character).isLocalPlayer()) {
                this.square.AddWorldInventoryItem(this.weapon, Rand.Next(0.2F, 0.8F), Rand.Next(0.2F, 0.8F), 0.0F, true);
            }
        } else {
            DebugLog.General.error("IsoBall: character isn't instance of IsoPlayer");
        }
    }
}
